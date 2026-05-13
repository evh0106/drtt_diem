package com.inisteel.cim.yd.common.util;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

/**
 * DB Assist Util
 * @author Administrator
 *
 */
public class YdDBAssist {
	
	private String szSessionName =getClass().getName();
	
	private	Connection conn = null;
	private	Statement stmt = null;
	private YdUtils ydUtils =new YdUtils();
	
	private int nSelectSvr=0;	// 0: 개발계, 2: 테스트계
	private String szIPDevSys1="10.216.133.62";
	//private String szIPDevSys1="10.216.133.58";
	//private String szIPDevSys1="10.216.130.45";
	private String szIPTstSys1="10.216.133.163";
	private String szIPTstSys2="10.216.133.164";
	
	
	public YdDBAssist(){
	
		
	} // end of DBAssist()
	
	protected void finalize() throws Throwable{
		if( !conn.isClosed()|| conn!=null){
			conn.close();
			stmt.close();
		}
		
	} // end of finalize()
	
	public void setSvr(int nSvrID){
		if( nSvrID==0)	this.nSelectSvr=0;
		else			this.nSelectSvr=1;
 
	} // end of setSvr()
	
	
	
	
	
	/**
	 * 현재 시스템의 IP를 체크
	 * return 1 : 테스트계
	 *        0 : 기타
	 */
	private int chkSystem(){
		
		try{
			InetAddress ipAddr=InetAddress.getLocalHost();

			if( (ipAddr.getHostAddress().equals(szIPTstSys1)) ||
					(ipAddr.getHostAddress().equals(szIPTstSys2))  )
				return 1;
			
		}catch(Exception e){
			return 0;
		}
		return 0;
		
	} // end of chkSystem()
		
	
	/**
	 * ResultSet에 포함 된 레코드의 갯수
	 * @param rs : (ResultSet) 대상 레코드 셋
	 * @return (int) 레코드셋에 포함 된 레코드의 수
	 */
	public int getRowCnt(ResultSet rs){
		
		String szMsg="";
		String szMethodName="getRowCnt";
		
		int nRowCnt=0;
		try{
			
		
		if( rs==null )
			return -1;
		
		if( !rs.isBeforeFirst())
			rs.beforeFirst();
		
		while(rs.next()){
			nRowCnt++;
		} 
		
		
		}catch (Exception e){
			szMsg=szMethodName+" Exception Error : "+e.getLocalizedMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return -2;
		}
		
		
		return nRowCnt;
		
	} // end of getRowCnt()
		
	
	
	/**
	 * 
	 * ResultSet에 포함 된 컬럼의 갯수
	 * @param rs : (ResultSet) 대상 레코드 셋
	 * @return (int) 레코드셋에 포함 된 컬럼의 수
	 */
	public int getColCnt(ResultSet rs){
		
		String szMsg="";
		String szMethodName="getColCnt";
		
		int nColCnt =0;
		try {
			ResultSetMetaData metaData =rs.getMetaData();
			nColCnt =metaData.getColumnCount();
			rs.beforeFirst();

		} catch (SQLException e) {
			szMsg=szMethodName+" Exception Error : "+e.getLocalizedMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			e.printStackTrace();
			return -1;
		}
	
		return nColCnt;
		
	} // end of getColCnt()
	
	
	
	/**
	 * RecordSet형식의 데이터를 JDTORecordSet 형식으로 변환
	 * @param rs
	 * @return
	 */
	private int makeRs2JdtoRecSet(ResultSet rs, JDTORecordSet rltRecSet){
		
		String szMsg="";
		String szMethodName="makeRs2JdtoRecSet";
		JDTORecord rowRec =null;
		
		int nRecCnt =0;
		int nColCnt =0;
		try{
			
			nRecCnt =getRowCnt(rs);			

			ResultSetMetaData metaData =rs.getMetaData();
			nColCnt =metaData.getColumnCount();
			
			rs.beforeFirst();
			
			for( int i=0; i<nRecCnt; i++){

				rs.next();

				rowRec =JDTORecordFactory.getInstance().create();
				
				for( int j=1; j<=nColCnt; j++){
					// Set Column
					rowRec.setField(metaData.getColumnName(j), rs.getString(j));
				
				} // end of for(j)

				rltRecSet.addRecord(rowRec);
				
			} // end of for(i)
			
		} catch(Exception e){
			szMsg=szMethodName+" Exception Error : "+e.getLocalizedMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return -1 ;
		}

		
		return nRecCnt;
		
	} // end of makeRs2JdtoRecSet();
	
	private int makeRs2JdtoRec(ResultSet rs, JDTORecord rltRec){
		
		String szMsg="";
		String szMethodName="makeRs2JdtoRec";
		JDTORecord rowRec =null;
		
		int nRecCnt =0;
		int nColCnt =0;
		try{
			
			nRecCnt =getRowCnt(rs);			

			ResultSetMetaData metaData =rs.getMetaData();
			nColCnt =metaData.getColumnCount();
			
			rs.beforeFirst();
			
			for( int i=0; i<nRecCnt; i++){

				rs.next();

				rowRec =JDTORecordFactory.getInstance().create();
				
				for( int j=1; j<=nColCnt; j++){
					// Set Column
					rowRec.setField(metaData.getColumnName(j), rs.getString(j));
				
				} // end of for(j)

				rltRec.setField(""+i, rowRec);
				
			} // end of for(i)
			
		} catch(Exception e){
			szMsg=szMethodName+" Exception Error : "+e.getLocalizedMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return -1 ;
		}

		
		return nRecCnt;
		
	} // end of makeRs2JdtoRec()
	
	
	
	
	
	
	
  //---------------------------------------------------------------------------	
} // end of class YdDBAssist
