/**
 * @(#)YfComScript
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2019/08/05
 *
 * @description      야드관리 공통 Utils
 * ------------------------------------------------------------------------------
 * Ver.   수정일자       요청자   수정자   내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2019/08/05   이정우   이정우   최초 등록
 * 
 */
package com.inisteel.cim.yf.acoilBak;

import java.util.List;

import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.CmCode;

/**
 * 공통 스크립트.
 * 
 * @version 1.0
 * @date 2017-02-02
 * 
 * @description
 */

public class YfComScript {

	private YfComScript() {
	}

	/**
	 * get Reference.
	 */
	public static YfComScript getInstance() {
		return new YfComScript();
	}

	public String setWiseCombo(String GrdObjNm, String[] sWiseGridCombo) throws Exception {
		return setWiseCombo(GrdObjNm, sWiseGridCombo, "", false);
	}

	public String setWiseCombo(String GrdObjNm, String[] sWiseGridCombo, boolean flagAll) throws Exception {
		return setWiseCombo(GrdObjNm, sWiseGridCombo, "", flagAll);
	}

	public String setWiseCombo(String GrdObjNm, String[] sWiseGridCombo, String sViewString) throws Exception {
		return setWiseCombo(GrdObjNm, sWiseGridCombo, sViewString, false);
	}

	public String setWiseCombo(String GrdObjNm, String[] sWiseGridCombo, String sViewString, boolean flagAll) throws Exception {
		StringBuffer sbResult = new StringBuffer();
		try {
			for (int ii = 0; ii < sWiseGridCombo.length; ii++) {
				String sCombo = sWiseGridCombo[ii];
				String sCodeItem[] = sCombo.split(";");
				String sColID = sCodeItem[0];
				String sItem = sCodeItem[1];
				String sCategory = "HS0000";
				if (sCodeItem.length == 3) {
					sCategory =  sCodeItem[2];
				}

				CmCode ccCmCode = new CmCode(sItem, sCategory);
				String sCode = ccCmCode.getCodes();
				String sName = ccCmCode.getNames();

				String sArrayCode[] = sCode.split(";");
				String sArrayName[] = sName.split(";");

				if (flagAll == true) {
					sbResult.append(GrdObjNm + ".AddComboListValue('" + sColID + "', '', '');");
				}
				for (int jj = 0; jj < sArrayCode.length; jj++) {
					String sViewTmp = "";
					if ("CD".equals(sViewString)) {
						sViewTmp = sArrayCode[jj];
					} else if ("CD_NAME".equals(sViewString)) {
						sViewTmp = sArrayCode[jj] + ":" + sArrayName[jj];
					} else {
						sViewTmp = sArrayName[jj];
					}

					sbResult.append(GrdObjNm + ".AddComboListValue('" + sColID + "', '" + sViewTmp + "', '" + sArrayCode[jj] + "');");
				}

			}

		} catch (Exception e) {
		}
		return sbResult.toString();
	}
	
	public String setWiseComboCate(String GrdObjNm, String[] sWiseGridCombo, String sViewString, boolean flagAll) throws Exception {
		StringBuffer sbResult = new StringBuffer();
		try {
			for (int ii = 0; ii < sWiseGridCombo.length; ii++) {
				String sCombo = sWiseGridCombo[ii];
				String sCodeItem[] = sCombo.split(";");
				String sColID = sCodeItem[0];
				String sItem = sCodeItem[1];
				String sCategory = sCodeItem[2] ;
				if (sCodeItem.length == 3) {
					sCategory =  sCodeItem[2];
				}

				CmCode ccCmCode = new CmCode(sItem, sCategory);
				String sCode = ccCmCode.getCodes();
				String sName = ccCmCode.getNames();

				String sArrayCode[] = sCode.split(";");
				String sArrayName[] = sName.split(";");

				if (flagAll == true) {
					sbResult.append(GrdObjNm + ".AddComboListValue('" + sColID + "', '', '');");
				}
				for (int jj = 0; jj < sArrayCode.length; jj++) {
					String sViewTmp = "";
					if ("CD".equals(sViewString)) {
						sViewTmp = sArrayCode[jj];
					} else if ("CD_NAME".equals(sViewString)) {
						sViewTmp = sArrayCode[jj] + ":" + sArrayName[jj];
					} else {
						sViewTmp = sArrayName[jj];
					}

					sbResult.append(GrdObjNm + ".AddComboListValue('" + sColID + "', '" + sViewTmp + "', '" + sArrayCode[jj] + "');");
				}

			}

		} catch (Exception e) {
		}
		
		return sbResult.toString();

	}
	
	
	/**
	 * 야드 , 동 , 크레인, 스케줄 정보를 카테고리 형식으로 넣어줌
	 * 
	 * @param List	 *            
	 * @return List
	 * @throws Exception
	 */
	
	

	public String setWiseComboYdDongCrnSch(String GrdObjNm,String szYdGp) throws Exception {
		
		StringBuffer sbResult = new StringBuffer();
		
		try {
			
			
			
		

			} catch (Exception e) {
		}
		
		return sbResult.toString();

	}
	
	
	

	/**
	 * select box 공통 스크립트.
	 * 
	 * @param List
	 *            resultList, String sSelectName, String sOptionDefault, String
	 *            sViewString, int iSize, String sSelectStr
	 * @return List
	 * @throws Exception
	 */

	public String getSelectBox(String sObjNm, String sStdItm) throws Exception {
		return getSelectBox(sObjNm, sStdItm, "HS0000", "", "", "", 0, "");
	}

	public String getSelectBox(String sObjNm, String sStdItm, String sCatId) throws Exception {
		return getSelectBox(sObjNm, sStdItm, sCatId, "", "", "", 0, "");
	}

	public String getSelectBox(String sObjNm, String sStdItm, String sCatId, String sOptionDefault) throws Exception {
		return getSelectBox(sObjNm, sStdItm, sCatId, sOptionDefault, "", "", 0, "");
	}

	public String getSelectBox(String sObjNm, String sStdItm, String sCatId, String sOptionDefault, String sChangeEvent) throws Exception {
		return getSelectBox(sObjNm, sStdItm, sCatId, sOptionDefault, "", "", 0, sChangeEvent);
	}
	
	public String getSelectBox(String sObjNm, String sStdItm, String sCatId, String sOptionDefault, String sSelectStr, String sChangeEvent) throws Exception {
		return getSelectBox(sObjNm, sStdItm, sCatId, sOptionDefault, sSelectStr, "", 0, sChangeEvent);
	}
	
	public String getSelectBox(String sObjNm, String sStdItm, String sCatId, String sOptionDefault, String sSelectStr, String sViewString, int iSize, String sChangeEvent) throws Exception {

		String sViewTmp = "";
		String sInivalue = "";

		String sStyleWidth = Integer.toString((6 * iSize) + 16);
		if ("0".equals(sStyleWidth) || iSize == 0) {
			sStyleWidth = "";
		}

		String sCodeSelect = "";
		if (!("".equals(sObjNm))) {
			if("".equals(sChangeEvent) || sChangeEvent == null)
				sCodeSelect = "<select name='" + sObjNm + "' class='input' style='width:" + sStyleWidth + "px'>\n";
			else
				sCodeSelect = "<select name='" + sObjNm + "' class='input' style='width:" + sStyleWidth + "px' onchange=\"" + sChangeEvent + "\"> \n";
		}

		if ("".equals(sOptionDefault)) {
			sInivalue = "--&nbsp선택&nbsp--";
		} else {
			sInivalue = sOptionDefault;
		}

		if (!"NEED".equals(sOptionDefault)) {
			sCodeSelect += "<option value = \"\">" + sInivalue + "</option>\n";
		}

		sCatId = "".equals(sCatId) ? "HS0000" : sCatId;

		CmCode ccCmCode = new CmCode(sStdItm, sCatId);
		String sCode = ccCmCode.getCodes();
		String sName = ccCmCode.getNames();
		String sArrayCode[] = sCode.split(";");
		String sArrayName[] = sName.split(";");

		int iMatchCount = 0;

		for (int ii = 0; ii < sArrayCode.length; ii++) {

			String sSelect = "";

			if (!("".equals(sSelectStr))) {
				if (sSelectStr.equals(sArrayCode[ii])) {
					sSelect = " selected ";
					iMatchCount++;

				} else {
					sSelect = "";
				}
			}

			if ("CD".equals(sViewString)) {
				sViewTmp = sArrayCode[ii];
			} else if ("CD_NAME".equals(sViewString)) {
				sViewTmp = sArrayCode[ii] + ":" + sArrayName[ii];
			} else {
				sViewTmp = sArrayName[ii];
			}

		//	sCodeSelect += "<option value=\"" + sArrayCode[ii] + sSelect + "\">&nbsp;" + sViewTmp + "&nbsp;</option>\n";
			sCodeSelect += "<option value=\"" + sArrayCode[ii] + "\"" + sSelect + ">&nbsp;" + sViewTmp + "&nbsp;</option>\n";
		}

		if (iMatchCount < 1 && !("".equals(sSelectStr))) {
			sCodeSelect += "<option value=\"" + sSelectStr + "\" selected>&nbsp;" + sSelectStr + "&nbsp;</option>\n";
		}

		sCodeSelect += "</select>";
		return sCodeSelect;
	}

	
	public JDTORecordSet getCommCodeList(String sCommItm, String sCategory) throws DAOException {
		try {
			JDTORecordSet jSetReturn = JDTORecordFactory.getInstance().createRecordSet("") ;
			
			CmCode ccCmCode = new CmCode(sCommItm, sCategory);
			
			String sCode = ccCmCode.getCodes().trim();
			String sName = ccCmCode.getNames().trim();
	
			if ("".equals(sCode)) {
				return jSetReturn;
			}
			
			
			String sArrayCode[] = sCode.split(";");
			String sArrayName[] = sName.split(";");
			
			for (int jj = 0; jj < sArrayCode.length; jj++) {
				JDTORecord jRecInDto = JDTORecordFactory.getInstance().create(); 
				jRecInDto.setField("CODE", sArrayCode[jj]) ; 
				jRecInDto.setField("NAME", sArrayName[jj]) ;
				
				jSetReturn.addRecord(jRecInDto) ; 
			}		
			return jSetReturn;
		} catch (Exception e) {
			throw new DAOException(e) ; 
		}
	}
	
	
	
	public String getPopBoxTag(String sType_cd) throws Exception {
		return this.getPopBoxTag(sType_cd, 1, "CD_NAME", "HS0000");
	}

	public String getPopBoxTag(String sType_cd,  String sCategory) throws Exception {
		return this.getPopBoxTag(sType_cd, 1, "CD_NAME", sCategory);
	}
	
	public String getPopBoxTag(String sType_cd, int nLayer) throws Exception {
		return this.getPopBoxTag(sType_cd, nLayer, "CD_NAME", "HS0000");
	}

	public String getPopBoxTag(String sType_cd,  String sCategory, int nLayer) throws Exception {
		return this.getPopBoxTag(sType_cd, nLayer, "CD_NAME", sCategory);
	}

	
	// sClassValue - css타입.
	// sOptionDefaultValue 추가.
	public String getPopBoxTag(String sType_cd, int nLayer, String sViewString, String sCategory) throws Exception {

		String sStartCode = "onMouseOver=" + '"' + "popBox('";
		String sEndCode = "')" + '"' + "onMouseOut=" + '"' + "kill()" + '"';

		String sCodeSelect = this.getPopBoxData(sType_cd, nLayer, sViewString,  sCategory);
		sCodeSelect = sStartCode + sCodeSelect + sEndCode;

		return sCodeSelect;
	}

	public String getPopBoxData(String sType_cd) throws Exception {
		return this.getPopBoxData(sType_cd, 1, "CD_NAME", "HS0000");
	}

	public String getPopBoxData(String sType_cd,  String sCategory) throws Exception {
		return this.getPopBoxData(sType_cd, 1, "CD_NAME", sCategory);
	}

	public String getPopBoxData(String sType_cd, int nLayer) throws Exception {
		return this.getPopBoxData(sType_cd, nLayer, "CD_NAME", "HS0000");
	}

	public String getPopBoxData(String sType_cd,  String sCategory, int nLayer) throws Exception {
		return this.getPopBoxData(sType_cd, nLayer, "CD_NAME", sCategory);
	}

	// sClassValue - css타입.
	// sOptionDefaultValue 추가.
	public String getPopBoxData(String sType_cd, int nLayer, String sViewString, String sCategory) throws Exception {
		
		String sCodeSelect = "";

		JDTORecordSet jSetCommCodeList = this.getCommCodeList(sType_cd, sCategory);

		if (jSetCommCodeList.size() < 1) {
			return "";
		}

		int nMaxByte = 0 ; 
		for (int ii = 0; ii < jSetCommCodeList.size(); ii++) {
			JDTORecord resultRecord = jSetCommCodeList.getRecord(ii);
			String sViewTmp = resultRecord.getFieldString("NAME");
			int nByte = sViewTmp.getBytes().length;
			if (nMaxByte < nByte) nMaxByte = nByte ; 
		}
		
		
		for (int ii = 0; ii < jSetCommCodeList.size(); ii++) {
			JDTORecord resultRecord = jSetCommCodeList.getRecord(ii);

			String sCode = resultRecord.getFieldString("CODE");
			String sName = resultRecord.getFieldString("NAME");
			
			if (nLayer != 1) {
				sName = this.fillerHtml(sName, nMaxByte) ; 
			}
			
			String sViewTmp = "";
			if ("CODE".equals(sViewString)) {
				sViewTmp = sCode;
			} else if ("NAME".equals(sViewString) ) {
				sViewTmp = sName;
				
			} else {
				sViewTmp = sCode + " : " + sName;
			}
			if (ii != 0) {
				int nLayerGp = ii % nLayer ; 
				
				if (nLayerGp == 0) {
					sCodeSelect += "<br>";
				}else {
					sCodeSelect += "&nbsp;";
				}
			}

			sCodeSelect += sViewTmp;
		}

		return sCodeSelect;
	}

	// sClassValue - css타입.
	// sOptionDefaultValue 추가.
	public String getPopBoxTag(List listParam) throws Exception {

		String sStartCode = "onMouseOver=" + '"' + "popBox('";
		String sEndCode = "')" + '"' + "onMouseOut=" + '"' + "kill()" + '"';

		String sCodeSelect = this.getPopBoxData(listParam);
		sCodeSelect = sStartCode + sCodeSelect + sEndCode;

		return sCodeSelect;
	}


	// sClassValue - css타입.
	// sOptionDefaultValue 추가.
	public String getPopBoxData(List listParam) throws Exception {


		String sCodeSelect = "";

		if (listParam.size() < 1) {
			return "";
		}

		for (int ii = 0; ii < listParam.size(); ii++) {
			String sItem = (String) listParam.get(ii);

			if (ii != 0) {
				sCodeSelect += "<br>";
			}

			sCodeSelect += sItem;
		}

		return sCodeSelect;
	}
	
	/*
	 * sType : C:Char, N:Number
	 */
	public String fillerHtml(String sSource, int iSize) {

		String sRtn = sSource.trim();

		for (int ii = sSource.getBytes().length; ii < iSize; ii++) {
				sRtn += "&nbsp;";
		}

		return sRtn;

	}
		
}// end class
