package com.inisteel.cim.yd.jsp.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import jspeed.base.log.LogLevel;
import jspeed.base.log.Logger;
import jspeed.base.query.QueryService;
import jspeed.base.query.QueryServiceException;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordImplMap;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;
import xlib.cmc.GridData;
import xlib.cmc.GridHeader;
import xlib.cmc.OperateGridData;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.ExceptionMessageUtil;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.jsp.common.YDDataUtil;


public class YDComUtil { 

	/**
	 * 로그서비스 선언
	 */
	private Logger logger = new Logger("yd");
	private YdUtils ydUtils = new YdUtils();

	/*
	 * WiseGrid 관련 Helper Method 디비 결과값 List를 GridData로 변환한다. JSP에서 요청된 파리미터를
	 * 그대로 리턴 그리드에 담는다.
	 */
	public GridData jdtoRecordToGridData(GridData returnGrid, JDTORecord[] dataList, GridData gdReq) throws Exception {
		return jdtoRecordToGridData(returnGrid, dataList, gdReq, null);
	}

	public GridData jdtoRecordToGridData(GridData returnGrid, JDTORecord[] dataList, GridData gdReq, String numberType) throws Exception {
		/*
		 * DataType C - t_checkbox L - t_combo N - t_number T - t_text D -
		 * t_date I - t_imagetext R - t_radio
		 */
		JDTORecord dataJrecord = null;

		try {
			GridHeader[] gridHeader = returnGrid.getHeaders();
			String headerName = "";
			String dataType = "";

			if (dataList == null || dataList.length == 0) {
				returnGrid.addParam("TOTALCOUNT", "0");
			} else {
				String totCount = "0";
				for (int ii = 0; ii < gridHeader.length; ii++) {
					headerName = gridHeader[ii].getID();
					dataType = gridHeader[ii].getDataType();

					/*
					 * 컬럼에 맞게 데이타를 세팅한다. SEQ_NO, SELECTED은 따로 생성한다. 이 두개의 컬럼은
					 * 디비에서 가져오지 않는다.
					 */
					if ("SEQ_NO".equals(headerName)) {
						for (int kk = 0; kk < dataList.length; kk++) {
							returnGrid.getHeader("SEQ_NO").addValue("" + (kk + 1), "");
						}
					} else if ("SELECTED".equals(headerName)) {
						for (int kk = 0; kk < dataList.length; kk++) {
							returnGrid.getHeader("SELECTED").addValue("0", "");
						}
					} else {
						for (int jj = 0; jj < dataList.length; jj++) {
							dataJrecord = dataList[jj];

							/*
							 * 페이징 처리를 했었을 경우 total row값이 들어가는 파라미터를 생성해 줘야 한다.
							 */
							if ("0".equals(totCount)) {
								totCount = StringHelper.evl(dataJrecord.getFieldString("TOTALCOUNT"), "0");
							}

							if ("L".equals(dataType)) {
								// t_combo 일때...
								returnGrid.getHeader(headerName).addSelectedHiddenValue(StringHelper.evl(dataJrecord.getFieldString(headerName), ""));
							} else if ("C".equals(dataType)) {
								// t_checkbox 일때...
								if (StringHelper.evl(dataJrecord.getFieldString(headerName), "").length() > 0) {
									if (dataJrecord.getFieldString(headerName).charAt(0) == '0' || dataJrecord.getFieldString(headerName).charAt(0) == '1') {
										returnGrid.getHeader(headerName).addValue("" + dataJrecord.getFieldString(headerName).charAt(0), "");
									} else {
										// 언체크로 세팅(0)
										returnGrid.getHeader(headerName).addValue("0", "");
									}
								} else {
									// 언체크로 세팅(0)
									returnGrid.getHeader(headerName).addValue("0", "");
								}
							} else if ("R".equals(dataType)) {
								// t_radio 일때... 어떻게 쓰이는지 모름...
								// 0, 1 이 아니면 에러가 남....에러가 나지 않도록...세팅함
								if (StringHelper.evl(dataJrecord.getFieldString(headerName), "").length() > 0) {
									if (dataJrecord.getFieldString(headerName).charAt(0) == '0' || dataJrecord.getFieldString(headerName).charAt(0) == '1') {
										returnGrid.getHeader(headerName).addValue("" + dataJrecord.getFieldString(headerName).charAt(0), "");
									} else {
										// 0으로 세팅
										returnGrid.getHeader(headerName).addValue("0", "");
									}
								} else {
									// 0으로 세팅
									returnGrid.getHeader(headerName).addValue("0", "");
								}
							} else if ("D".equals(dataType)) {
								// t_date 일때...YYYYMMDD 형식이 아니면 에러가 떨어짐.
								if (StringHelper.evl(dataJrecord.getFieldString(headerName), "").length() > 10) {
									returnGrid.getHeader(headerName).addValue(StringHelper.replaceStr(dataJrecord.getFieldString(headerName).substring(0, 10), "-", ""), "");
								} else if (StringHelper.evl(dataJrecord.getFieldString(headerName), "").length() == 10
										|| StringHelper.evl(dataJrecord.getFieldString(headerName), "").length() == 8) {
									returnGrid.getHeader(headerName).addValue(StringHelper.replaceStr(dataJrecord.getFieldString(headerName), "-", ""), "");
								} else {
									returnGrid.getHeader(headerName).addValue("", "");
								}
							} else if ("I".equals(dataType)) {
								// t_imagetext 일때...
								returnGrid.getHeader(headerName).addValue(StringHelper.evl(dataJrecord.getFieldString(headerName), ""),
										StringHelper.evl(dataJrecord.getFieldString(headerName), ""), 0);
							} else if ("N".equals(dataType)) {
								// t_number 일때 값이 0이면 space를 전송한다.
								if (!"number".equals(numberType)) {
									if (!"0".equals(StringHelper.evl(dataJrecord.getFieldString(headerName), ""))) {
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
						}// for
					}// if
				}// for

				/*
				 * 페이징 처리를 했었을 경우 total row값이 들어가는 파라미터를 생성해 줘야 한다.
				 */
				// total row 세팅..
				returnGrid.addParam("TOTALCOUNT", totCount);
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
		} catch (Exception e) {
			throw e;
		}
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
			for (int i = 0; i < params.length; i++) {
				String key = (String) params[i];
				String value = StringHelper.nvl(inDto.getParam(params[i]), "");
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
		for (int i = 0; i < rCount; i++) {
			GridHeader chHeader = inDto.getHeader("CHECK");
			boolean Checked = (Integer.parseInt(chHeader.getValue(i)) == 1) ? true : false;
			if (Checked) {
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
				jdtoAl[i] = jDto;
			}
		}

		logger.println(LogLevel.DEBUG, "========== JDTORecord START ===========");
		for (int ss = 0; ss < jdtoAl.length; ss++) {
			logger.println(LogLevel.DEBUG, jdtoAl[ss].toString());
		}
		logger.println(LogLevel.DEBUG, "========== JDTORecord END ===========");

		return jdtoAl;
	}
	
	
	
	
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
	 *	JDTORecordSet을 GridData 변환하기 한다.( 조회결과 목록을 GridData로 전송할 때 사용)
	 * @param gdRes
	 * @param rSet
	 * @return
	 * @throws Exception
	 */
	public GridData genGridMCData(GridData gdRes, JDTORecordSet rSet, String[][] mcKey) throws Exception{
		rSet.beforeFirst();
		JDTORecord outRecord =null;
		GridHeader [] ghs = gdRes.getHeaders();

		while (rSet.next() == true){
			//행단위의 데이터를 취득합니다.
			String logS = "";
			outRecord = (JDTORecord) rSet.getRecord();
			//체크박스 공통
			for(int ix=0;ix<ghs.length;ix++){
				String id = ghs[ix].getID();
				String sLevel = "";

				if(gdRes.getHeader(id).getDataType().equals(OperateGridData.t_checkbox)){
					if(id.equals("CHECK")){
						gdRes.getHeader(id).addValue("0", "");
					}else{
						gdRes.getHeader(id).addValue(StringHelper.nvl(outRecord.getFieldString(id), "0"), "0");
					}
				}else if(gdRes.getHeader(id).getDataType().equals(OperateGridData.t_combo)){
					logger.println(LogLevel.DEBUG,   "Combo Id : "+id+"     hasComboList : "+gdRes.getHeader(id).hasComboList());
					if(gdRes.getHeader(id).hasComboList()){
						for(int ii=0; ii<mcKey.length; ii++){
							if(id.equals(mcKey[ii][0])){
								if("1".equals(mcKey[ii][1])){
									gdRes.getHeader(id).addSelectedHiddenValue("MULTICOMBO" ,outRecord.getFieldString(id));
								}else{
									gdRes.getHeader(id).addSelectedHiddenValue(outRecord.getFieldString(mcKey[ii][2]) ,outRecord.getFieldString(id));
								}
							}
							
						}						
					}else 
						gdRes.getHeader(id).addSelectedHiddenValue(outRecord.getFieldString(id));
				}else if(id.equals("CRUD")){
					gdRes.getHeader("CRUD").addValue("R","R");
				}else if(gdRes.getHeader(id).getDataType().equals(OperateGridData.t_imagetext)){
					gdRes.getHeader(id).addValue(StringHelper.nvl(outRecord.getFieldString(id), ""), "", 0);
				}else{
					gdRes.getHeader(id).addValue(StringHelper.nvl(outRecord.getFieldString(id), ""), "");
				}
			}
		}
		if(outRecord == null){
			gdRes.addParam("TOTAL", "0");
		}else if(outRecord != null){
			gdRes.addParam("TOTAL", StringHelper.nvl((String)outRecord.getField("TOTAL"),"0"));
		}
		return gdRes;
	}
	
	
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
	 * Grid의 Check된 Row를 JDTORecordSet 변환한다.
	 *  차이점 :: genJDTORecordSet
	 *   - jsp에서 WISEGRIDDATA_ALL 를 보낼때 실제 Check된 건만 선별한다.
	 *  
	 * @param inDto
	 * @return
	 */
	public JDTORecord [] genJDTORecordSetByDataAllTypeCheck(GridData inDto) throws Exception{
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
		
		int nCheckRowCnt = 0;
		if(rCount > 0){
			for(int i=0;i<rCount;i++){
				GridHeader chHeader = inDto.getHeader("CHECK");
				boolean Checked = (Integer.parseInt( CmnUtil.nvl(chHeader.getValue(i),"0")) == 1)? true:false;
				if(Checked){
					nCheckRowCnt++;
				}
			}
		}

		JDTORecord [] jdtoAl = new JDTORecord[nCheckRowCnt];
		logger.println(LogLevel.DEBUG,   "========  GridData -> JDTORecord []  ================");
		logger.println(LogLevel.DEBUG,   "헤더갯수:"+hCount);
		logger.println(LogLevel.DEBUG,   "Row갯수:"+rCount);
		logger.println(LogLevel.DEBUG,   "Check갯수:"+nCheckRowCnt);
		logger.println(LogLevel.DEBUG,   "========== GridData inDto ROW DATA ===========");
		int nCheckIdx = 0;
		for(int i=0;i<rCount;i++){
			GridHeader chHeader = inDto.getHeader("CHECK");
			boolean Checked = (Integer.parseInt( CmnUtil.nvl(chHeader.getValue(i),"0")) == 1)? true:false;
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
				  
				jdtoAl[nCheckIdx++] = jDto;
			}
		}

		logger.println(LogLevel.DEBUG,   "========== JDTORecord START ===========");
		for(int ss=0;ss<jdtoAl.length;ss++){
			logger.println(LogLevel.DEBUG,   jdtoAl[ss].toString());
		}
		logger.println(LogLevel.DEBUG,   "========== JDTORecord END ===========");

		return jdtoAl;
	}		
	
	public String getAgoMonth(String currDate, int agoMon, int agoDay) {
		Calendar c1 = Calendar.getInstance();

		int iCurrYear = Integer.parseInt(currDate.substring(0, 4));
		int iCurrMonth = Integer.parseInt(currDate.substring(4, 6));
		int iCurrDay = Integer.parseInt(currDate.substring(6, 8));
		// System.out.println(iCurrYear+"<>"+iCurrMonth+"<>"+iCurrDay);

		c1.set(iCurrYear, iCurrMonth - 1, iCurrDay);

		c1.add(Calendar.MONTH, agoMon);
		c1.add(Calendar.DAY_OF_MONTH, agoDay);

		String sAgoYear = "" + c1.get(Calendar.YEAR);
		String sAgoMonth = (c1.get(Calendar.MONTH) + 1) < 10 ? "0" + (c1.get(Calendar.MONTH) + 1) : "" + (c1.get(Calendar.MONTH) + 1);
		String sAgoDay = c1.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + c1.get(Calendar.DAY_OF_MONTH) : "" + c1.get(Calendar.DAY_OF_MONTH);

		String sAgoDate = sAgoYear + sAgoMonth + sAgoDay;
		// System.out.println(sAgoDate);

		return sAgoDate;
	}

	/*----------------------------------------------
	 * 
	 */

	/**
	 * 문자열을 인코딩한다.
	 * <p>
	 * 
	 * @param String
	 *            str
	 * @return 날짜 문자열 MM, yyyyMMdd, yyyyMMddHHmmss, yyyyMMddHHmmss.ss,
	 *         yyyy/MM/dd HH:mm:ss, yyMMdd, HHmmss...등 주의 : 대소문자 구분.
	 */
	public String getDate(String type) {
		SimpleDateFormat getDate = new SimpleDateFormat(type, Locale.KOREA);
		String tmpDate = getDate.format(new Date());
		getDate = null; // 수동 null 처리.
		return tmpDate;
	}

	/**
	 * 문자열을 인코딩한다.
	 * <p>
	 * 
	 * @param String
	 *            str
	 * @return 날짜 문자열 MM, yyyyMMdd, yyyyMMddHHmmss, yyyyMMddHHmmss.ss,
	 *         yyyy/MM/dd HH:mm:ss, yyMMdd, HHmmss...등 주의 : 대소문자 구분.
	 */
	public String getINIDate(String type) {
		SimpleDateFormat getDate = new SimpleDateFormat(type, Locale.KOREA);
		Date dToday = new Date();
		Date dINIDate = new Date();
		int iOneHour = 1000 * 60 * 60;

		dINIDate.setTime(dToday.getTime() - iOneHour * 6);

		String tmpDate = getDate.format(dINIDate);
		getDate = null; // 수동 null 처리.
		return tmpDate;
	}

	/**
	 * 문자열을 인코딩한다.
	 * <p>
	 * 
	 * @param String
	 *            str
	 * @return 날짜 문자열 MM, yyyyMMdd, yyyyMMddHHmmss, yyyyMMddHHmmss.ss,
	 *         yyyy/MM/dd HH:mm:ss, yyMMdd, HHmmss...등 주의 : 대소문자 구분.
	 */
	public String getInputINIDate(String type, String input) {
		SimpleDateFormat getDate = new SimpleDateFormat(type);
		try {
			Date dToday = getDate.parse(input);
			Date dINIDate = new Date();
			int iOneHour = 1000 * 60 * 60;

			dINIDate.setTime(dToday.getTime() - iOneHour * 6);

			String tmpDate = getDate.format(dINIDate);

			return tmpDate;
		} catch (ParseException e) {
			// TODO plz log
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 일자 계산
	 * <p>
	 * 
	 * @param String
	 *            str
	 * @return 날짜 문자열 MM, yyyyMMdd, yyyy/MM/dd, yyMMdd 등 주의 : 대소문자 구분.
	 */
	public String addDays(String sDate, String type, int iDays) {

		SimpleDateFormat sf = new SimpleDateFormat(type);

		try {
			Date cDate = sf.parse(sDate);

			Calendar cal = Calendar.getInstance();

			cal.setTime(cDate);

			cal.add(Calendar.DATE, iDays);

			return "" + sf.format(cal.getTime());

		} catch (ParseException e) {
			// TODO plz log
			e.printStackTrace();
			return "";
		}
	}

	/*
	 * sType : C:Char, N:Number
	 */
	public String filler(String sSource, int iSize, String sType) {

		String sRtn = sSource.trim();

		for (int ii = sSource.getBytes().length; ii < iSize; ii++) {
			if ("C".equals(sType)) {
				sRtn += " ";
			} else {
				sRtn = "0" + sRtn;
			}
		}

		return sRtn;

	}

	public void logWriteQueryLabel(String sQueryID, Object oParam[], DAOException daoe) {
		String sQuery = "";
		try {
			sQuery = QueryService.getInstance().getSQL(sQueryID);
			sQuery = sQuery + " ";
			for (int ii = 0; ii < oParam.length; ii++) {

				String sReplaceStr = "";
				if (oParam[ii] instanceof Double) {
					sReplaceStr = ((Double) oParam[ii]) + "";
				} else if (oParam[ii] instanceof Integer) {
					sReplaceStr = ((Integer) oParam[ii]) + "";
				} else {
					sReplaceStr = (String) oParam[ii];
				}

				int iStartPos = sQuery.indexOf(":");
				if (iStartPos < 0)
					continue;

				String sFindData = sQuery.substring(iStartPos);
				boolean bFound = false;
				for (int jj = 0; jj < 4; jj++) {

					int iEndPos = sFindData.indexOf(")");
					if (iEndPos < 0) {
						iEndPos = sFindData.indexOf("\n");
						if (iEndPos < 0) {
							iEndPos = sFindData.indexOf("\t");
							if (iEndPos < 0) {
								iEndPos = sFindData.indexOf(" ");
								if (iEndPos < 0) {
									continue;
								}
							}
						}
					}
					bFound = true;
					sFindData = sFindData.substring(0, iEndPos);

				}

				if (bFound == false)
					continue;

				if (oParam[ii] instanceof Double) {
					sQuery = sQuery.replaceFirst(sFindData, sReplaceStr);
				} else if (oParam[ii] instanceof Integer) {
					sQuery = sQuery.replaceFirst(sFindData, sReplaceStr);
				} else {
					sQuery = sQuery.replaceFirst(sFindData, "'" + sReplaceStr + "'");
				}

			}
		} catch (QueryServiceException e) {
			// TODO 자동 생성된 catch 블록
			e.printStackTrace();
		}

		logger.println(LogLevel.ERROR, "▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒ DAO  ERROR ▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒");
		logger.println(LogLevel.ERROR, "QUERY_ID  : " + sQueryID);
		logger.println(LogLevel.ERROR, "QUERY     : ");
		logger.println(LogLevel.ERROR, "\n" + sQuery);
		logger.println(LogLevel.ERROR, "----------------------------------------------------------------------------------------------");
		logger.println(LogLevel.ERROR, " EXCEPTION : ");
		daoe.printStackTrace();
		logger.println(LogLevel.ERROR, "▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒");

	}

	public void logWriteQueryLabel(String sQueryID, Object oParam[]) {
		String sQuery = "";
		try {
			sQuery = QueryService.getInstance().getSQL(sQueryID);
			sQuery = sQuery + " ";
			for (int ii = 0; ii < oParam.length; ii++) {

				String sReplaceStr = "";
				if (oParam[ii] instanceof Double) {
					sReplaceStr = ((Double) oParam[ii]) + "";
				} else if (oParam[ii] instanceof Integer) {
					sReplaceStr = ((Integer) oParam[ii]) + "";
				} else {
					sReplaceStr = (String) oParam[ii];
				}

				int iStartPos = sQuery.indexOf(":");
				if (iStartPos < 0)
					continue;

				String sFindData = sQuery.substring(iStartPos);
				boolean bFound = false;
				for (int jj = 0; jj < 4; jj++) {

					int iEndPos = sFindData.indexOf(")");
					if (iEndPos < 0) {
						iEndPos = sFindData.indexOf("\n");
						if (iEndPos < 0) {
							iEndPos = sFindData.indexOf("\t");
							if (iEndPos < 0) {
								iEndPos = sFindData.indexOf(" ");
								if (iEndPos < 0) {
									continue;
								}
							}
						}
					}
					bFound = true;
					sFindData = sFindData.substring(0, iEndPos);

				}

				if (bFound == false)
					continue;

				if (oParam[ii] instanceof Double) {
					sQuery = sQuery.replaceFirst(sFindData, sReplaceStr);
				} else if (oParam[ii] instanceof Integer) {
					sQuery = sQuery.replaceFirst(sFindData, sReplaceStr);
				} else {
					sQuery = sQuery.replaceFirst(sFindData, "'" + sReplaceStr + "'");
				}

			}

		} catch (QueryServiceException e) {
			// TODO 자동 생성된 catch 블록
			e.printStackTrace();
		}

		logger.println(LogLevel.ERROR, "▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒ Query Info ▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒");
		logger.println(LogLevel.ERROR, "");
		logger.println(LogLevel.ERROR, "QUERY_ID  : " + sQueryID);
		logger.println(LogLevel.ERROR, "QUERY     : ");
		logger.println(LogLevel.ERROR, "\n" + sQuery);
		logger.println(LogLevel.ERROR, "▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒");
	}

	/**
	 *	JDTORecordSet을 GridData 변환하기 한다.( 조회결과 목록을 GridData로 전송할 때 사용)
	 * @param gdRes
	 * @param rSet
	 * @return
	 * @throws Exception
	 */
	public GridData genGridData(GridData gdRes, JDTORecordSet rSet) throws Exception{
		rSet.beforeFirst();
		JDTORecord outRecord =null;
		GridHeader [] ghs = gdRes.getHeaders();

		while (rSet.next() == true){
			//행단위의 데이터를 취득합니다.
			String logS = "";
			outRecord = (JDTORecord) rSet.getRecord();
			//체크박스 공통
			for(int ix=0;ix<ghs.length;ix++){
				String id = ghs[ix].getID();

				if(gdRes.getHeader(id).getDataType().equals(OperateGridData.t_checkbox)){
					if(id.equals("CHECK")){
						gdRes.getHeader(id).addValue("0", "");
					}else{
						gdRes.getHeader(id).addValue(StringHelper.nvl(outRecord.getFieldString(id), "0"), "0");
					}
				}else if(gdRes.getHeader(id).getDataType().equals(OperateGridData.t_combo)){
					gdRes.getHeader(id).addSelectedHiddenValue(outRecord.getFieldString(id));
				}else if(id.equals("CRUD")){
					gdRes.getHeader("CRUD").addValue("R","R");
				}else if(gdRes.getHeader(id).getDataType().equals(OperateGridData.t_imagetext)){
					gdRes.getHeader(id).addValue(StringHelper.nvl(outRecord.getFieldString(id), ""), "", -1);
				}else{
					gdRes.getHeader(id).addValue(StringHelper.nvl(outRecord.getFieldString(id), ""), "");
				}
			}
		}
		if(outRecord == null){
			gdRes.addParam("TOTAL", "0");
		}else if(outRecord != null){
			gdRes.addParam("TOTAL", StringHelper.nvl((String)outRecord.getField("TOTAL"),"0"));
		}
		return gdRes;
	}
	
	public double KRound(double dNumber, int iPoint) {
		double dValue = 0d ;

		try {
			int dVal1 = (int) Math.floor(dNumber * Math.pow(10, iPoint)) ;
			int dVal2 = (int) Math.floor(dNumber * Math.pow(10, iPoint + 2)) ; // 추가 2자리 값 계산
			
			
			int nModValue =  dVal2 % 100 ;
			
			if (nModValue < 50 ) {
				
			} else if (nModValue > 50 ) {
				dVal1 = dVal1 + 1 ;  
				
			} else {
				if (dVal1 % 2 != 0 ) {
					dVal1 = dVal1 + 1 ;
				}
			}

			double  kk = 1; 
			for (int ii = 0 ; ii < iPoint ; ii++) {
				kk = kk * 10.0 ; 
			}
				
			 dValue = dVal1 / kk ;
	
		} catch (Exception e) {
			 
		}
		
		return dValue;
	}	
	
	public JDTORecordSet getRecParamToSetParam(JDTORecord JRecParam) throws DAOException {
		JDTORecordSet jSetOutRecord = JDTORecordFactory.getInstance().createRecordSet("") ;
		
		try {
		
			JDTORecord jRecTest = JDTORecordFactory.getInstance().create();
			
			int iMaxRow = 0 ;
			
			Iterator it = JRecParam.iterateName();
			String strNum = "";
			while(it.hasNext()) {
			    strNum = (String)it.next();

			    if(JRecParam.getField(strNum) instanceof String[]) {
			    	int iRow = ((String[])JRecParam.getField(strNum)).length;

			    	jRecTest.setField(strNum, JRecParam.getField(strNum));  
			    	
			    	if (iMaxRow < iRow) iMaxRow = iRow; 
			    	
			    } else if(JRecParam.getField(strNum) instanceof String) {
			    	jRecTest.setField(strNum, JRecParam.getFieldString(strNum).split(";"));  
			    	if (iMaxRow == 0 ) iMaxRow = 1; 
			    }
			    
			}
			
			for (int ii = 0 ; ii < iMaxRow ; ii ++  ) {
				
				JDTORecord jRecInDto = JDTORecordFactory.getInstance().create();
				it = jRecTest.iterateName();
				strNum = "";
				while(it.hasNext()) {

				    strNum = (String)it.next();
					
					String[] sObject = (String[])jRecTest.getField(strNum) ;
					String sObjectValue = "" ;
					
			    	int iRow = sObject.length;
			    	
			    	if (ii < iRow) {
			    		sObjectValue = sObject[ii] ; 
			    	} else {
			    		sObjectValue = sObject[sObject.length - 1 ] ; 
			    	}

			    	jRecInDto.setField(strNum,sObjectValue) ; 
				}
				
				jSetOutRecord.addRecord(jRecInDto) ; 
				
			}
		
			return jSetOutRecord; 

		} catch (Exception e){
			 throw new DAOException(e);
		}
		
		
	}
	

	
	public String format(String sSource,  String sMaskText){
		String	sReturnVal = "";
		String	sCharSource = "";
		String  sCharMask = "";
		String  sMask = sMaskText;
		
		if(sSource == null || "".equals(sSource) ){
			return "";
		}
		
 		for (int ii = 0; ii < sSource.length(); ii ++){
			
			sCharSource = sSource.substring(ii, ii + 1);

			if (sMask.length()  < 1) {
				break;
//				if (ii < sSource.length()) {
//					sReturnVal += sSource.substring(ii, sSource.length() );
//					break;
//				}
			}
			
			while(true){
				sCharMask = sMask.substring(0, 1);
				
				if ("@".equals(sCharMask)) {
					sReturnVal +=  sCharSource;
					sMask = sMask.substring(1, sMask.length());
					break;
				} else {
					sReturnVal += sCharMask;
				}
				
				if (sMask.length() < 2) {
					sReturnVal +=  sCharSource;
					sMask = "";
					break;
				} else {
					sMask = sMask.substring(1, sMask.length());
				}
			}
			
			
			
		}
 		return sReturnVal;
	}	
	
	
	
	public JDTORecordSet extFindRecordSet(JDTORecord jSetFindItem, JDTORecordSet jSetSrcItem, String[] sFindKey) throws DAOException {
		
		try{
			
			JDTORecordSet jSetRtnRec = JDTORecordFactory.getInstance().createRecordSet("") ;
			
			String sFindText = "" ; 
			for (int ii = 0 ;  ii < sFindKey.length ; ii ++ ) { 
				sFindText += StringHelper.nvl(jSetFindItem.getFieldString(sFindKey[ii]), "") ; 
			}
			
			int iStartPos = 0 ; 
			int iEndPos = 0;
			int iCenterPos = 0; 
			int iFindPos = -1;
			
			iEndPos = jSetSrcItem.size() - 1 ;
			
			for(int ii = 0 ; ii < jSetSrcItem.size(); ii ++ ) {
				
				int iTempPos = iStartPos + iEndPos ; 
				
				if (iTempPos  % 2 == 1) {
					iTempPos = (iTempPos  - 1) ;  
				} 
				
				iCenterPos = iTempPos / 2 ;
				
				String sText = "" ;
				for (int jj = 0 ;  jj < sFindKey.length ; jj ++ ) { 
					sText += StringHelper.nvl(jSetSrcItem.getRecord(iCenterPos).getFieldString(sFindKey[jj]), "") ; 
				}
				
				if (sFindText.compareTo(sText) > 0 ) { // sFindText >  sText
					iStartPos = iCenterPos + 1 ;
					if (iStartPos > jSetSrcItem.size() ) {
						break;
					}
					
				} else if (sFindText.compareTo(sText) < 0 ) { // sFindText <  sText
					iEndPos = iCenterPos - 1 ;
					if (iEndPos < 0  ) {
						break;
					}
				} else {  // sFindText ==  sText
					iFindPos = iCenterPos ; 
					break;
				}
				
			}
			
			if (iFindPos == -1  ) {
				return jSetRtnRec; 
			}

			for(int ii = iFindPos - 1  ; ii >= 0; ii-- ) {
				
				String sText = "" ;
				for (int jj = 0 ;  jj < sFindKey.length ; jj ++ ) { 
					sText += StringHelper.nvl(jSetSrcItem.getRecord(ii).getFieldString(sFindKey[jj]), "") ; 
				}
				
				if (sFindText.equals(sText)) {
					jSetRtnRec.addRecord(jSetSrcItem.getRecord(ii)) ; 
				} else {
					break;
				}
			}

			jSetRtnRec.reverseOrder();
			
			for(int ii = iFindPos ; ii < jSetSrcItem.size(); ii ++ ) {
				
				String sText = "" ;
				for (int jj = 0 ;  jj < sFindKey.length ; jj ++ ) { 
					sText += StringHelper.nvl(jSetSrcItem.getRecord(ii).getFieldString(sFindKey[jj]), "") ; 
				}
				
				if (sFindText.equals(sText)) {
					jSetRtnRec.addRecord(jSetSrcItem.getRecord(ii)) ; 
				} else {
					break;
				}
			}
			
			return jSetRtnRec;
			
		} catch(DAOException daoe) {
			throw daoe;
		} catch(Exception e) {
			logger.println(LogLevel.ERROR, this, ExceptionMessageUtil.getStackTrace(e)) ;
			throw new DAOException(e); 
		}
		
	}	
	/**
	 * GridData의 입력/수정/삭제 정보를 JDTORecord [] 으로 변환하여 리턴한다. (GridData의 입력/수정/삭제
	 * 항목을 가져오기위해 사용)
	 * 
	 * @param inDto
	 * @return
	 */
	public  JDTORecord[] genJDTORecordSetTemp(GridData inDto)
			throws Exception {
		boolean isUpperKey = false;
		if (inDto.getParam("set_upper") != null
				&& inDto.getParam("set_upper").equals("true")) {
			isUpperKey = true;
		}

		GridHeader[] ghs = inDto.getHeaders();
		//
		String []  ghseq = inDto.getHeaderSequence();
		int hCount = ghs.length;
		int rCount = 0;
		if (hCount > 0) {
			rCount = ghs[0].getRowCount();
		}
		JDTORecord[] jdtoAl = new JDTORecord[rCount];
	
		for (int i = 0; i < rCount; i++) {
			GridHeader chHeader = inDto.getHeader("CHECK");
			boolean Checked = (Integer.parseInt(chHeader.getValue(i)) == 1) ? true
					: false;
			if (Checked) {
				JDTORecord jDto = JDTORecordFactory.getInstance().create();
				for (int j = 0; j < hCount; j++) {
					String key = ghseq[j];
					
					System.out.println("Key "+ j+ "= "+key);
					String rValue = "";
					
					if ("CHECK".equals(key) || "CRUD".equals(key) ){
						continue;
					}
				
					for(int k =0; k< hCount;k++){
						
						
						if (ghs[k].getID() == key)
						{
							rValue = StringHelper.evl(ghs[k].getValue(i), "");
							break;
						}
						
					}
					
					jDto.setField((isUpperKey) ? key.toUpperCase() : key,
							rValue);
				}
				jdtoAl[i] = jDto;
			}
		}


		return jdtoAl;
	}
	
	
	/**
	 * JDTORecord의 내용 앞에 전문 ID를 삽입한다. 
	 * 
	 * @param fillerSize
	 * @param inRecord
	 * @return
	 */
	public int editRec(JDTORecord inRecord,String szTc,JDTORecord outRecord)	{
		int nRecCnt= -1;
		
		Object [] objTemp=((JDTORecordImplMap)inRecord).getFieldNames();
		nRecCnt =objTemp.length;
		String szRecKey="";
		String szValue="";
	
		try {
			
				
			outRecord.setField("JMS_TC_CD", szTc);
			
			for(int i=0; i<nRecCnt; i++){
				szRecKey =objTemp[i].toString();
				szValue =inRecord.getFieldString(szRecKey);				
				outRecord.setField(szRecKey, szValue);
			
			} // end of for()
			nRecCnt = 1;
			
		}catch (JDTOException e) {
				// TODO Auto-generated catch block
				logger.print(e.getMessage());
			};
		System.out.println("화면EJB 자체 전문 편집완료");
		return nRecCnt;
		
	} // end addFiller()
	
	
	
	
	/**
	 * JDTORecord의 내용 앞에 전문 ID를 삽입한다. 
	 * 
	 * @param fillerSize
	 * @param inRecord
	 * @return
	 */
	public int editRecEai(JDTORecord inRecord,String szTc,JDTORecord outRecord)	{
		int nRecCnt= -1;
		
		Object [] objTemp=((JDTORecordImplMap)inRecord).getFieldNames();
		nRecCnt =objTemp.length;
		String szRecKey="";
		String szValue="";
	
		try {
			
				
			outRecord.setField("TC_CODE", szTc);
			outRecord.setField("TC_CREATE_DDTT",  YdUtils.getCurDate("yyyyMMddHHmmss"));
			
			for(int i=0; i<nRecCnt; i++){
				szRecKey =objTemp[i].toString();
				szValue =inRecord.getFieldString(szRecKey);				
				outRecord.setField(szRecKey, szValue);
			
			} // end of for()
			nRecCnt = 1;
			
		}catch (JDTOException e) {
				// TODO Auto-generated catch block
				logger.print(e.getMessage());
			};
		System.out.println("화면EJB 자체 전문 편집완료");
		return nRecCnt;
		
	} // end addFiller()
	
	
	
	
}