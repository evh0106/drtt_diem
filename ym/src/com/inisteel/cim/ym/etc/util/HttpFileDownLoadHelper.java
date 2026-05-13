/*
 * Created on 2005. 8. 1.
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inisteel.cim.ym.etc.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.inisteel.cim.ym.common.dao.*;
import jspeed.base.record.JDTORecord;
import jspeed.base.http.*;
import jspeed.base.util.StringHelper;
import jspeed.base.log.LogLevel;
import jspeed.base.log.LogService;
import jspeed.base.log.Logger;

public class HttpFileDownLoadHelper
{
	private File excelFile; 
	private Logger logger = null;
	
	private HSSFWorkbook wb = new HSSFWorkbook();
	HSSFSheet sheet = null;
	
	public static void main(String[] args) throws SQLException
	{
		File excel = new File("c:/DBtoExcel.xls");
		HttpFileDownLoadHelper dbToExcel = new HttpFileDownLoadHelper(excel);     
		dbToExcel.exec();
	}

	public HttpFileDownLoadHelper(File excelFile)
	{
		this.excelFile = excelFile;
		logger = LogService.getInstance().getLogger("ym");
	}
       
	public void exec() throws SQLException
	{
		ymCommonDAO dao = ymCommonDAO.getInstance();
		String sQueryId	= "ym.facilitystatus.facilityinquiry.CraneSchDAO.getTankWrsltInfo_test";
	    	List DataList		= dao.getCommonList(sQueryId,new Object[]{"SM"});

		buildExcel(DataList);

		FileOutputStream fileOut = null;
		try
		{
			fileOut = new FileOutputStream(excelFile);
		}
		catch (FileNotFoundException e)
		{
			System.out.println(e);
		}
	      					
		try
		{
			wb.write(fileOut);
			fileOut.close();
		}
		catch (IOException e)
		{
			System.out.println(e);
		}
	}
               
	private void buildExcel(List DataList) throws SQLException
	{
		int numberOfColumns = 10;
		
		sheet 			= wb.createSheet("LAND");
		HSSFRow row 	= null;
		HSSFCell cell 		= null;
		
		String[] columnsName = {"A","B","C","D","E","F","G","H","J","K"};
		
		row = sheet.createRow((short) 0);
		for (int i = 0; i < numberOfColumns; i++) // Column의 Title 출력 부분
		{
			cell 	= row.createCell((short) (i + 1));
			cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			cell.setCellValue(columnsName[i]);
			
			//logger.println(LogLevel.INFO,">>>>>>>>>>>>"+columnsName[i]);	      			
		}
		
		HSSFCellStyle cellStyle = wb.createCellStyle();
		cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));
		
		int rsCnt = 1;
		for(int x = 0; x < DataList.size(); x++)
		{ 
			row = sheet.createRow((short) rsCnt);
			
			JDTORecord dataDTO = (JDTORecord)DataList.get(x);
			
			for (int i = 0; i < numberOfColumns; i++)
			{
				cell = row.createCell((short) (i+1));
				cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell.setCellValue(StringHelper.evl(dataDTO.getFieldString(columnsName[i]),""));
				cell.setCellStyle(cellStyle);
				
				//logger.println(LogLevel.INFO,">>>>>>>>>>>> x="+x+"/i="+i+"/data="+StringHelper.evl(dataDTO.getFieldString(columnsName[i]),""));	 
			}
			rsCnt++;
		}
	}
}

