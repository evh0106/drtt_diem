package com.inisteel.cim.yd.common.util;

import java.io.File;
import java.io.IOException;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class YdXlsTool {
	
	private String szSessionName="YdXlsTool";
	
	private String szFileName="";
	
	private Workbook workbook = null;
	private Sheet sheet = null;
    private Cell cell = null;
    
    
    
	
	protected void finalize(){
		if(workbook!=null)
			workbook.close();
			
	}
	
	
	
	//
	// XlsTool Init
	// 사용 할 XLS 파일을 설정한다.
	//
	public void setFile(String szFileName){
		String szMethodName="setFile";
		this.szFileName =szFileName.trim();
		System.out.println("[DEBUG] "+szSessionName+":"+szMethodName+
				"지정 EXCEL File=["+szFileName+"]");
	
	}// end of setFile();

	
	
	//
	// Xls File Open
	//
	public int openXls(){
		
		String szMethodName="openXls";
		
		// 엑셀파일 인식
		try {
			workbook = Workbook.getWorkbook( new File(this.szFileName) );

		} catch (BiffException e) {
			//System.out.println("getWorkbook()-BiffException Error : "+e.getMessage());
			e.printStackTrace();
			return -1;
		} catch (IOException e) {
			//System.out.println("getWorkbook()-IOException Error : "+e.getMessage());
			e.printStackTrace();
			return -1;
		} // end of	
		
		
		//엑셀파일에 포함된 sheet의 배열을 리턴한다.
		workbook.getSheets();

		if( workbook == null){
			System.out.println("[DEBUG] "+szSessionName+":"+szMethodName+
					"[DEBUG]WorkBook is Empty!" );
			return -1;
		}
			
		return 0;
		
	} // end of openXls()
	
	
	
	
	//
	// Xls 파일에 사용되는 Sheet의 이름을 배열로 리턴한다.
	//
	public String [] getSheetNames(){
		String [] szaSheetName =workbook.getSheetNames();
		return szaSheetName;
		
	} // end of getSheetName()
	
	
	
	
	//
	// Xls 파일에 사용되는 Sheet의 갯수를 리턴한다.
	//
	public int getSheetCnt(){
		int nSheetCnt =workbook.getNumberOfSheets();
		return nSheetCnt;
	} // end of getSheetCnt()
	
	
	
	
	//
	// Workbook중 사용 할 Sheet를 선한다.
	//
	public void setSheet(int nSheetId){
		sheet =workbook.getSheet(nSheetId);
	} // end of setSheet()
	
	
	
	
	//
	// Workbook의 Sheet중 이름과 일치하는 SheetID를 선택한다.
	//
	public int getSheetId(String szRcvSheetName){
		
		if( szRcvSheetName==null)
			return -1;
		
		String szTargetName=szRcvSheetName.toUpperCase();
		szTargetName.trim();
		String szTemp="";
		
		String []szaSheetNames=getSheetNames();
		int nSheets=szaSheetNames.length;
		if( nSheets<1)
			return -2;
		
		for(int i=0;i<nSheets;i++){
			szTemp=szaSheetNames[i].toUpperCase();
			szTemp.trim();
			if(szTargetName.equals(szTemp) ){
				return i;
			}
			
		} // end of for()
		
		//
		// Error
		//같은 이름의 Sheet를 찾지 못함
		//
		return -3;
		
	} // end of setSheet()
	
	
	
	
	//
	// Sheet에 존재하는 컬럼의 갯수를 리턴한다.
	//
	public int getColCnt(){
		int nColCnt =sheet.getColumns();
		return nColCnt;
		
	} // end of getColCnt()




	//
	//	Sheet에 존재하는 컬럼의 갯수를 리턴한다.
	//
	public int getRowCnt(){
		int nColCnt =sheet.getRows();
		return nColCnt;

	} // end of getRowCnt()
	
	
	
	
	//
	// Get Field Names
	//
	public String [] getFieldName(){
		int nColCnt=getColCnt();
		String [] szaFieldName =new String[nColCnt];
		for(int i=0; i<nColCnt; i++){
			szaFieldName[i]=getData(i,0);
		}
		
		return szaFieldName;
		
	} // end of getFieldName()
	
	//
	// Get Field Names
	//
	public String [] getFieldNameTrim(){
		int nColCnt=getColCnt();
		String [] szaFieldName =new String[nColCnt];
		for(int i=0; i<nColCnt; i++){
			szaFieldName[i]=getData(i,0).trim();
		}
		
		return szaFieldName;
		
	} // end of getFieldName()
	
	
	
	//
	// Col,Row의 데이터 발췌
	//
	public String getData(int nCol, int nRow){
		cell =sheet.getCell(nCol, nRow);
		String szValue =cell.getContents();
		
		return szValue;
		
	} // end of getData()
	
	
	
	//
	// getJDTORecord
	//
	public JDTORecord getRecord(int nRec){
		
		int nColCnt=getColCnt();
		int nRowCnt=getRowCnt();
		int i=0;
		
		if( nRec <=0 || nRec>nRowCnt-1){
			//System.out.println("[DEBUG] Record Cnt Error");
			return null;
		}

		
		// Get Fiend Name 
		String []szFieldName=getFieldName();
		
		
		JDTORecord jdtoRec =JDTORecordFactory.getInstance().create();
		
		try{
		
			for(i=0; i<nColCnt; i++){
				jdtoRec.addField(szFieldName[i], getData(i,nRec) );
			}
		}catch(JDTOException je){
			System.out.println(je.getMessage());
			return null;
		} // end of try-catch
		
		return jdtoRec;
		
	} // end of getRecord()
	
	
	
	//
	// get JDTORecordSet
	//
	public JDTORecordSet getRecordSet(){
		
		int nColCnt=getColCnt();
		int nRowCnt=getRowCnt();
		int nCol=0;
		int nRow=0;
		
		
		
		// Get Fiend Name 
		String []szFieldName=getFieldName();
		JDTORecordSet jdtoRecSet =JDTORecordFactory.getInstance().createRecordSet("XlsToolRecSet");
	
		String szKey="";
		String szValue="";
		
		try{
		
			for(nRow=1; nRow<nRowCnt;nRow++){
				JDTORecord jdtoRec =JDTORecordFactory.getInstance().create();
				for(nCol=0; nCol<nColCnt; nCol++){
					szKey=szFieldName[nCol];
					szValue=getData(nCol, nRow);
					if( szKey==null)	continue;
					jdtoRec.setField(szKey, szValue);
				} // end of for(nCol)
				jdtoRecSet.addRecord(jdtoRec);
			} // end of for(nRow)
		}catch(JDTOException je){
			System.out.println(je.getMessage());
			return null;
		} // end of try-catch
		
		return jdtoRecSet;
		
	} // end of getRecord()
	

	
	


	//┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
	//                                                
	//                     일관제철소정보관리시스템-야드관리
	//                 MS-EXCEL 파일을 읽어 관련 데이터를 핸들링한다.
	//                          2008.11.20 YHWHman
	//                                                      
	//┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
	
	
  //---------------------------------------------------------------------------
} // end of class
