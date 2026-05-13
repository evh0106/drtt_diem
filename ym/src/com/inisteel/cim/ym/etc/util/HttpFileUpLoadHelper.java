/*
 * Created on 2005. 8. 1.
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inisteel.cim.ym.etc.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import jspeed.base.http.AttachFileDataSource;
import jspeed.base.http.MultipartRequest;
import jspeed.base.log.LogLevel;
import jspeed.base.log.LogService;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;

/**
 * 
 * 
 * HttpFileUpLoadHelper.java
 * 
 * @version    :
 * @author     : 
 * @date       : 2005. 8. 1.
 *
 * @description :
 *
 */
public class HttpFileUpLoadHelper {
    
	// request로 받은 데이타를 저장할 JDTORecord 
	private JDTORecord dtoRecord;
	private Logger logger = null;
	
	//OS check
	private Properties p = System.getProperties();
	final String osName = (String)p.get("os.name");

	public HttpFileUpLoadHelper(HttpServletRequest request) {
		dtoRecord = JDTORecordFactory.getInstance().create();
		logger    = LogService.getInstance().getLogger("ym");
		

		MultipartRequest mpReq;
		try {
			mpReq = new MultipartRequest(request);
			this.generateParameter(mpReq, "Y", "N");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	public HttpFileUpLoadHelper(HttpServletRequest request, String sUpLoadYN) {
		dtoRecord = JDTORecordFactory.getInstance().create();
		logger    = LogService.getInstance().getLogger("ym");
		

		MultipartRequest mpReq;
		try {
			mpReq = new MultipartRequest(request);
			this.generateParameter(mpReq, sUpLoadYN, "N");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public HttpFileUpLoadHelper(HttpServletRequest request, String sUpLoadYN, String sDeleteYn) {
		dtoRecord = JDTORecordFactory.getInstance().create();
		logger    = LogService.getInstance().getLogger("ym");
		

		MultipartRequest mpReq;
		try {
			mpReq = new MultipartRequest(request);
			this.generateParameter(mpReq, sUpLoadYN, sDeleteYn);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @see JDTORecord
	 * 파라미터를 모두 받아서 JDTORecord로 만든다.
	 * @param request
	 */
	private void generateParameter(MultipartRequest request, String sUpLoadYN, String sDeleteYn) {
		
		String sUploadDir = "";
		
		if("5".equals(sDeleteYn)){
			if(osName.startsWith("Windows")){
				sUploadDir = "C:/app/webdocs/images/ym/land";
			}else{
				sUploadDir = "/app/webdocs/images/ym/land";
			}
		}else{
			
			if(osName.startsWith("Windows")){
				sUploadDir = "C:/app/webapps/inisteelApp/inisteelWebApp/ym/common/upload";
			}else{
				sUploadDir = "/app/webapps/inisteelApp/inisteelWebApp/ym/common/upload";
			}
		}
		
		sDeleteYn = "Y";
			
		if(request == null) { 
		    logger.println(LogLevel.ERROR, "Request is null");
		    return; 
		}
		
		try { 
			

			//첨부파일을 제외한 parameter name을 얻는다 (input type=text).
			Enumeration paramEnu = request.getParameterNames();
			
			while (paramEnu.hasMoreElements()) {
				String paramName 	= (String)paramEnu.nextElement();

				//해당 parameter name 의 value를 얻는다.
				String[] paramValue = request.getParameterValues(paramName);								
				
				if(paramValue != null) {
					if(1 == paramValue.length) {
					    
					    //list.add(values[0]);
						dtoRecord.setField(paramName, paramValue[0]);
					} else {
					    
					    List list = new ArrayList();
					    ////////////////////////////////////////////////////////////////
					    StringBuffer sb = new StringBuffer();
					    
						for(int i=0; i < paramValue.length; i++) {
						    list.add(paramValue[i]);
						    
						    if(i==0)
						        sb.append( paramName ).append( "=[" ).append( paramValue[i] );
						    else if( i < paramValue.length - 1)
						        sb.append(",").append(paramValue[i]);
						    else
						        sb.append(",").append(paramValue[i]).append("]");						    
						}
						
						///////////////////////////////////////////////////////////////
						
						// 파라미터가 다중일때..
						dtoRecord.setField(paramName, list);
					}
				}			
			}
			
			if ("Y".equals(sUpLoadYN)) {
			
				//모든 첨부파일 parameter name을 얻는다 (input type=file).
				Enumeration fileEnu = request.getAttachNames();
				
				while (fileEnu.hasMoreElements()) {
					
					String paramName 	= (String)fileEnu.nextElement();

					//해당 parameter name 을 갖는 모든 첨부파일의 DataSource를 얻는다.
					AttachFileDataSource[] afds = request.getAttachFiles(paramName);
					
					if(afds != null) {
						if(1 == afds.length) {

							String uploadFileName = "";
							if ("Y".equals(sDeleteYn)) {
//								File tmp= new File(sUploadDir + "/" + afds[0].getName());
//								if(tmp.exists()) {
//								   tmp.delete();
//								}
								//해당 AttachFileDataSource를 특정 디렉토리에 해당 파일이름으로 업로드한다. 
								uploadFileName = request.upload(afds[0], sUploadDir, afds[0].getName());
							} else {
								//해당 AttachFileDataSource를 특정 디렉토리에 해당 파일이름으로 업로드한다. 
								uploadFileName = request.upload(afds[0], sUploadDir);
							}
	
							// 파라미터가 하나이다.

						    //list.add(values[0]);
							dtoRecord.setField(paramName, uploadFileName);
						} else {
						    
						    List list = new ArrayList();
						    ////////////////////////////////////////////////////////////////
						    StringBuffer sb = new StringBuffer();
						    
							for(int i=0; i < afds.length; i++) {

								String uploadFileName = ""; 
								
								if ("Y".equals(sDeleteYn)) {
//									File tmp= new File(sUploadDir + "/" + afds[i].getName());
//									if(tmp.exists()) {
//									   tmp.delete();
//									}
									
									uploadFileName = request.upload(afds[i], sUploadDir, afds[i].getName());
									
								} else {
									uploadFileName = request.upload(afds[i], sUploadDir);
								}
								
							    list.add(uploadFileName);

							    if(i==0)
							        sb.append( paramName ).append( "=[" ).append( uploadFileName );
							    else if( i < afds.length - 1)
							        sb.append(",").append(uploadFileName);
							    else
							        sb.append(",").append(uploadFileName).append("]");						    
							}


							///////////////////////////////////////////////////////////////
							
							// 파라미터가 다중일때..
							dtoRecord.setField(paramName, list);
						}
					}	
				}
				
			}
		
		} catch (Exception e) {
		    logger.println(LogLevel.INFO, e);
			dtoRecord.setResultMsg("Exception");
		}
		
	}
	
	/**
	 * JDTORecord 객체를 리턴 받는다.
	 * @return
	 */
	public JDTORecord getJDTORecord() {
		return dtoRecord;
	}   
	
	
}
