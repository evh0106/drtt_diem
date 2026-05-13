/*
 * Created on 2005. 8. 1.
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inisteel.cim.ym.etc.util;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import jspeed.base.log.LogLevel;
import jspeed.base.log.LogService;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.util.StringHelper;

/**
 * 
 * 
 * HttpExcelHelper.java
 * 
 * @version    :
 * @author     : 
 * @date       : 2005. 8. 1.
 *
 * @description :
 *
 */
public class HttpExcelHelper {
    
	// request로 받은 데이타를 저장할 JDTORecord 
	private List listData = new ArrayList();
	private Logger logger = null;

	//OS check
	private Properties p = System.getProperties();
	final String osName = (String)p.get("os.name");

	public HttpExcelHelper(String sFileName) {
		String sUploadDir = "";
		if(osName.startsWith("Windows")){
			sUploadDir = "C:/app/webapps/inisteelApp/inisteelWebApp/ym/common/upload";
		}else{
			sUploadDir = "/app/webapps/inisteelApp/inisteelWebApp/ym/common/upload";
		}
		
		logger    = LogService.getInstance().getLogger("ym");

		this.generateRecord(sUploadDir + "/" + sFileName);
	}

	/**
	 * @see JDTORecord
	 * 파라미터를 모두 받아서 JDTORecord로 만든다.
	 * @param request
	 */
	private void generateRecord(String sFileName) {
		
		if(sFileName == null) { 
		    logger.println(LogLevel.ERROR, "Excel File Name is null");
		    return; 
		}
		
		try { 
			FileInputStream fis = new FileInputStream(sFileName);
			
			POIFSFileSystem fs = new POIFSFileSystem(fis); 

	       //워크북을 생성!                

	       HSSFWorkbook workbook = new HSSFWorkbook(fs);

	       int sheetNum = workbook.getNumberOfSheets();

	       
	       
	       for (int ii = 0; ii < sheetNum; ii++) {
	            HSSFSheet sheet = workbook.getSheetAt(ii);
	            int rows = sheet.getLastRowNum();
	            
	            for (int iRow = 0; iRow <= rows; iRow++) {
	                HSSFRow row   = sheet.getRow(iRow);
	                if (row == null) {
	                	continue;
	                }
	                JDTORecord dtoRecord = JDTORecordFactory.getInstance().create();

                    int cells = row.getLastCellNum();
 
                    for (short iCol = 0; iCol < cells; iCol++) {
                    	
                        // 행에대한 셀을 하나씩 추출하여 셀 타입에 따라 처리
                        HSSFCell cell  = row.getCell(iCol);
                        String value = null;

                        if (cell == null) {
                        	value = "" ;
                        } else {

	                        switch (cell.getCellType()) {
	
	                        	case HSSFCell.CELL_TYPE_FORMULA :
	                        		value = "" + cell.getCellFormula();
	                        		break;
	                        	case HSSFCell.CELL_TYPE_NUMERIC :
	                        		value = "" + StringHelper.format(cell.getNumericCellValue(), "##############.#####"); //double
	                        		break;
	                        	case HSSFCell.CELL_TYPE_STRING :
	                        		value = "" + cell.getStringCellValue(); //String
	                        		break;
	                        	case HSSFCell.CELL_TYPE_BLANK :
	                        		value = "";
	                        		break;
	                        	case HSSFCell.CELL_TYPE_BOOLEAN :
	                        		value = "" +cell.getBooleanCellValue(); //boolean
	                        		break;
	                        	case HSSFCell.CELL_TYPE_ERROR :
	                        		value = "" +cell.getErrorCellValue(); // byte
	                        		break;
	                        	default :
	                        }
                        }                    
			    
                        dtoRecord.setField("COL_" + iCol , value);
                         
                        //logger.println(LogLevel.INFO,">>>>>>> YJK2 >>>>>>>rows="+rows+"/ iCol="+iCol+"/value="+value);	                        
                    }
                    
                    listData.add(dtoRecord);
	            }
	       }
	       fis.close();
	       
		} catch (Exception e) {
		    logger.println(LogLevel.INFO, e);
		}
		
	}
	
	/**
	 * JDTORecord 객체를 리턴 받는다.
	 * @return
	 */
	public List getData() {
		return listData;
	}   
	
	
}
