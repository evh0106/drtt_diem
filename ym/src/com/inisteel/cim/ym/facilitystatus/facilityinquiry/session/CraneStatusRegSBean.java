package com.inisteel.cim.ym.facilitystatus.facilityinquiry.session;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.log.LogService;
import jspeed.base.log.LogServiceConfig;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.util.CommonUtil;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.common.jms.model.CommonModel;
import com.inisteel.cim.common.jms.model.po.YMPO161;
import com.inisteel.cim.common.parser.Level2Parser;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdEquipDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO;
import com.inisteel.cim.ym.steelinfo.steelinforecv.dao.YdStockDAO;
import com.inisteel.cim.ym.workrequest.wrequestaccept.dao.YdWBookDAO;
/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="CraneStatusRegEJB" jndi-name="JNDICraneStatusReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class CraneStatusRegSBean extends BaseSessionBean {
    private Logger logger 			= null;
    private ymCommonDAO ymCommonDAO = null;
    private CraneSchDAO dao 		= null;
	private YmComm ymComm = new YmComm();
	
	public void ejbCreate() {
        LogServiceConfig config = 
            LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
        logger = new Logger(config);
        ymCommonDAO = new ymCommonDAO();
        dao 		= new CraneSchDAO();
	}

	/**
	 * 오퍼레이션명 : 
	 *
	 * 야드 Level-2로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * 1.TC_CD	: CN1PB11.
        * 2.I/F ID	: YM-BIF-020.
        * 전문코드	TC				CHAR	7		
        * 발생일자	Date			CHAR	10		YYYY-MM-DD
        * 발생시간	Time			CHAR	8		HH-MM-SS
        * 전문구분	Form			CHAR	1		I  : Initialize, U : Update D : Delete  R : Re-request
        * 전문길이	Message_Length	CHAR	4		
        * 송수신구분	SendReq			CHAR	1		R:요구, A:응답
        * SEQ NO	SeqNo			CHAR	3		001 ~ 999, END
        * SKID ADDRESS				CHAR	10		YARD(1) + 동(1) + SPAN(2) + 열(2) + 번지(2) + 단(2)
        * 사용유무	Use_Check		CHAR	1		SKID 사용유무-->0, 1
        * COIL NO	Coil_No			CHAR	10		SPACE : 코일무
        * 군정보	Group_Info			CHAR	1		
        * 제작번호/행번	ProductNo	CHAR	13		
        * 두께	Thick				CHAR	7	㎜	소수점3자리 (###.###)
        * 폭	Width				CHAR	6	㎜	소수점1자리 (####.#)
        * 길이	Length				CHAR	6	㎜	
        * 외경	Outdia				CHAR	5	㎜	
        * 중량	Weight				CHAR	5	Kg	X 물리위치	X_Physical_Address	CHAR	6		
        * Y 물리위치	Y_Physical_Address	CHAR	6		
        * X 허용오차(+)	X_Plus_Range	CHAR	4		
        * X 허용오차(-)	X_Minus_Range	CHAR	4		
        * Y 허용오차(+)	Y_Plus_Range	CHAR	4		
        * Y 허용오차(-)	Y_Minus_Range	CHAR	4		
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */      
	public boolean bcyYdMapInfo(String sMessage) {
        logger.println(LogLevel.DEBUG, this, "B열연 COIL Yard Map 정보 처리");
	    try {
	    	
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
	        /**
	         * Message Parsing
	         */
	        JDTORecord parseData = new Level2Parser().parse(sMessage);
	        logger.println(LogLevel.DEBUG, this, parseData);	        
	        /**
	         * vlaid check
	         */
	        String reqGp 	= getField(parseData, "송수신구분");
	        String skidAdd 	= getField(parseData, "SKIDADDRESS");
	        String tc_cd 	= getField(parseData, "전문코드");
	        if(reqGp.length() != 1) {
	            throw new Exception("##### 송수신구분 정보를 확인 하십시요: "+ reqGp);
	        }	        
	        if(YmCommonConst.SEND_REQ_A.equals(reqGp)) {
	            editResponse(parseData);
	        }else if(YmCommonConst.SEND_REQ_R.equals(reqGp)) {
	            sendResponse(skidAdd, tc_cd);
	        }
	    }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
	    return true;
	}


	/**
	 * 오퍼레이션명 : 임가공 PIDEV
	 *
	 * 야드 Level-2로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * 1.TC_CD	: CN1PB11.
        * 2.I/F ID	: YM-BIF-020.
        * 전문코드	TC				CHAR	7		
        * 발생일자	Date			CHAR	10		YYYY-MM-DD
        * 발생시간	Time			CHAR	8		HH-MM-SS
        * 전문구분	Form			CHAR	1		I  : Initialize, U : Update D : Delete  R : Re-request
        * 전문길이	Message_Length	CHAR	4		
        * 송수신구분	SendReq			CHAR	1		R:요구, A:응답
        * SEQ NO	SeqNo			CHAR	3		001 ~ 999, END
        * SKID ADDRESS				CHAR	10		YARD(1) + 동(1) + SPAN(2) + 열(2) + 번지(2) + 단(2)
        * 사용유무	Use_Check		CHAR	1		SKID 사용유무-->0, 1
        * COIL NO	Coil_No			CHAR	10		SPACE : 코일무
        * 군정보	Group_Info			CHAR	1		
        * 제작번호/행번	ProductNo	CHAR	13		
        * 두께	Thick				CHAR	7	㎜	소수점3자리 (###.###)
        * 폭	Width				CHAR	6	㎜	소수점1자리 (####.#)
        * 길이	Length				CHAR	6	㎜	
        * 외경	Outdia				CHAR	5	㎜	
        * 중량	Weight				CHAR	5	Kg	X 물리위치	X_Physical_Address	CHAR	6		
        * Y 물리위치	Y_Physical_Address	CHAR	6		
        * X 허용오차(+)	X_Plus_Range	CHAR	4		
        * X 허용오차(-)	X_Minus_Range	CHAR	4		
        * Y 허용오차(+)	Y_Plus_Range	CHAR	4		
        * Y 허용오차(-)	Y_Minus_Range	CHAR	4		
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */      
	public boolean bcyYdMapInfoPI(String sMessage) {
        logger.println(LogLevel.DEBUG, this, "B열연 COIL Yard Map 정보 처리");
	    try {
	    	
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
	        /**
	         * Message Parsing
	         */
	        JDTORecord parseData = new Level2Parser().parse(sMessage);
	        logger.println(LogLevel.DEBUG, this, parseData);	        
	        /**
	         * vlaid check
	         */
	        String reqGp 	= getField(parseData, "송수신구분");
	        String skidAdd 	= getField(parseData, "SKIDADDRESS");
	        String tc_cd 	= getField(parseData, "전문코드");
	        if(reqGp.length() != 1) {
	            throw new Exception("##### 송수신구분 정보를 확인 하십시요: "+ reqGp);
	        }	        
	        if(YmCommonConst.SEND_REQ_A.equals(reqGp)) {
	            editResponsePI(parseData);
	        }else if(YmCommonConst.SEND_REQ_R.equals(reqGp)) {
	            sendResponsePI(skidAdd, tc_cd);
	        }
	    }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
	    return true;
	}
	
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 야드 Level-2로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * 1.TC_CD	: CM1PB10.//HM1PB09//HM1PB59//HC3PB52
        * 2.I/F ID	: YM-BIF-020.
        * 전문코드	TC				CHAR	7		
        * 발생일자	Date			CHAR	10		YYYY-MM-DD
        * 발생시간	Time			CHAR	8		HH-MM-SS
        * 전문구분	Form			CHAR	1		I  : Initialize, U : Update D : Delete  R : Re-request
        * 전문길이	Message_Length	CHAR	4		
        * 송수신구분	SendReq			CHAR	1		R:요구, A:응답
        * BED ADDRESS	BedAddress	CHAR	8		야드(1)+동(1)+Span(2)+ Row(2)+BED(2)
        * 사용유무	UseCheck			CHAR	1		BED 사용유무
        * 적치가능매수	StackUseCount	CHAR	2		BED 적치 가능 매수
        * 적치매수	StackCount			CHAR	2		현재 적치 매수
        * 적치 SEQ	StackSeq			CHAR	2		SLAB 적치 단
        * SLAB NO	SlabNo				CHAR	11		SPACE : 적치 무
        * 제작번호/행번	ProductNo		CHAR	13		
        * 두께	Thck					CHAR	7	㎜	소수점3자리 (###.###)
        * 폭	Width					CHAR	6	㎜	소수점1자리 (####.#)
        * 중량							CHAR	5	kg	
        * 길이	Length					CHAR	6		
        * X 물리위치	X_Physical_Address	CHAR	6		
        * Y 물리위치	Y_Physical_Address	CHAR	6		
        * X 허용오차(+)	X_Plus_Range	CHAR	4		
        * X 허용오차(-)	X_Minus_Range	CHAR	4		
        * Y 허용오차(+)	Y_Plus_Range	CHAR	4		
        * Y 허용오차(-)	Y_Minus_Range	CHAR	4				
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */      
	public boolean bsyYdMapInfo(String sMessage) {
	    try { 
	    	
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
	        /**
	         * Message Parsing
	         */
	        JDTORecord parseData = new Level2Parser().parse(sMessage);
	        logger.println(LogLevel.DEBUG, this, parseData);	        
	        /**
	         * vlaid check
	         */
	        String reqGp  = getField(parseData, "송수신구분");
	        String bedAdd = getField(parseData, "BEDADDRESS");
	        String tc_cd  = getField(parseData, "전문코드");
	        
	        //A열연 SLAB야드 (MCH)
	        if(YmCommonConst.TC_CM1PB10.equals(tc_cd)){
	        	logger.println(LogLevel.DEBUG, this, "B열연 SLAB Yard Map 정보 처리");
	        }else if(YmCommonConst.TC_HM1PB08.equals(tc_cd)){
	        	logger.println(LogLevel.DEBUG, this, "A_A열연 SLAB Yard Map 정보 처리");
	        }else if(YmCommonConst.TC_HM1PB58.equals(tc_cd)){
	        	logger.println(LogLevel.DEBUG, this, "A_B열연 SLAB Yard Map 정보 처리");
	        }else if(YmCommonConst.TC_HC3PB52.equals(tc_cd)){
	        	logger.println(LogLevel.DEBUG, this, "A열연 연주 7호기 SLAB Yard Map 정보 처리");
	        }
	        
	        if(reqGp.length() != 1) {
	            throw new Exception("##### 송수신구분 정보를 확인 하십시요: "+ reqGp);
	        }else if(bedAdd.length() != 8 && bedAdd.length() != 6 ) {
	            throw new Exception("##### BEDADDRESS 정보를 확인 하십시요: "+ bedAdd);
	        }
	        /**
	         * 응답 또는 맵일치
	         */
	        if(YmCommonConst.SEND_REQ_A.equals(reqGp)&&
	        	(YmCommonConst.TC_HM1PB59.equals(tc_cd)||
	        	 YmCommonConst.TC_HM1PB09.equals(tc_cd))){
	        	AslabeditResponse(parseData);
	        }else{
		        if(YmCommonConst.SEND_REQ_A.equals(reqGp)) {
		            editResponse(parseData);
		        }else if(YmCommonConst.SEND_REQ_R.equals(reqGp)) {
		            sendResponse(bedAdd, tc_cd);
		        }
	        }
	    }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
	    return true;
	}

	
	/**
	 * 오퍼레이션명 : 
	 * 임가공 PIDEV
	 * 야드 Level-2로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * 1.TC_CD	: CM1PB10.//HM1PB09//HM1PB59//HC3PB52
        * 2.I/F ID	: YM-BIF-020.
        * 전문코드	TC				CHAR	7		
        * 발생일자	Date			CHAR	10		YYYY-MM-DD
        * 발생시간	Time			CHAR	8		HH-MM-SS
        * 전문구분	Form			CHAR	1		I  : Initialize, U : Update D : Delete  R : Re-request
        * 전문길이	Message_Length	CHAR	4		
        * 송수신구분	SendReq			CHAR	1		R:요구, A:응답
        * BED ADDRESS	BedAddress	CHAR	8		야드(1)+동(1)+Span(2)+ Row(2)+BED(2)
        * 사용유무	UseCheck			CHAR	1		BED 사용유무
        * 적치가능매수	StackUseCount	CHAR	2		BED 적치 가능 매수
        * 적치매수	StackCount			CHAR	2		현재 적치 매수
        * 적치 SEQ	StackSeq			CHAR	2		SLAB 적치 단
        * SLAB NO	SlabNo				CHAR	11		SPACE : 적치 무
        * 제작번호/행번	ProductNo		CHAR	13		
        * 두께	Thck					CHAR	7	㎜	소수점3자리 (###.###)
        * 폭	Width					CHAR	6	㎜	소수점1자리 (####.#)
        * 중량							CHAR	5	kg	
        * 길이	Length					CHAR	6		
        * X 물리위치	X_Physical_Address	CHAR	6		
        * Y 물리위치	Y_Physical_Address	CHAR	6		
        * X 허용오차(+)	X_Plus_Range	CHAR	4		
        * X 허용오차(-)	X_Minus_Range	CHAR	4		
        * Y 허용오차(+)	Y_Plus_Range	CHAR	4		
        * Y 허용오차(-)	Y_Minus_Range	CHAR	4				
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */      
	public boolean bsyYdMapInfoPI(String sMessage) {
	    try { 
	    	
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
	        /**
	         * Message Parsing
	         */
	        JDTORecord parseData = new Level2Parser().parse(sMessage);
	        logger.println(LogLevel.DEBUG, this, parseData);	        
	        /**
	         * vlaid check
	         */
	        String reqGp  = getField(parseData, "송수신구분");
	        String bedAdd = getField(parseData, "BEDADDRESS");
	        String tc_cd  = getField(parseData, "전문코드");
	        
	        //A열연 SLAB야드 (MCH)
	        if(YmCommonConst.TC_CM1PB10.equals(tc_cd)){
	        	logger.println(LogLevel.DEBUG, this, "B열연 SLAB Yard Map 정보 처리");
	        }else if(YmCommonConst.TC_HM1PB08.equals(tc_cd)){
	        	logger.println(LogLevel.DEBUG, this, "A_A열연 SLAB Yard Map 정보 처리");
	        }else if(YmCommonConst.TC_HM1PB58.equals(tc_cd)){
	        	logger.println(LogLevel.DEBUG, this, "A_B열연 SLAB Yard Map 정보 처리");
	        }else if(YmCommonConst.TC_HC3PB52.equals(tc_cd)){
	        	logger.println(LogLevel.DEBUG, this, "A열연 연주 7호기 SLAB Yard Map 정보 처리");
	        }
	        
	        if(reqGp.length() != 1) {
	            throw new Exception("##### 송수신구분 정보를 확인 하십시요: "+ reqGp);
	        }else if(bedAdd.length() != 8 && bedAdd.length() != 6 ) {
	            throw new Exception("##### BEDADDRESS 정보를 확인 하십시요: "+ bedAdd);
	        }
	        /**
	         * 응답 또는 맵일치
	         */
	        if(YmCommonConst.SEND_REQ_A.equals(reqGp)&&
	        	(YmCommonConst.TC_HM1PB59.equals(tc_cd)||
	        	 YmCommonConst.TC_HM1PB09.equals(tc_cd))){
	        	AslabeditResponsePI(parseData);
	        }else{
		        if(YmCommonConst.SEND_REQ_A.equals(reqGp)) {
		            editResponsePI(parseData);
		        }else if(YmCommonConst.SEND_REQ_R.equals(reqGp)) {
		            sendResponsePI(bedAdd, tc_cd);
		        }
	        }
	    }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
	    return true;
	}
	
	/**
     * B열연 야드맵 응답, A열연 야드맵 응답(추가)
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param add	야드맵정보
	 * @param gp	요청/응답 구분
     */
	public void sendResponse(String add, String tc_cd) {
	    try {
	    	
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
	        /**
	         * 요청한 야드맵 정보를 가져온다.
	         * add
	         * --COIL:야드(1)+ 동(1)+ Span(2)+ 열(2)+ 번지(2) + 단(2)
	         * --SLAB:야드(1)+ 동(1)+ Span(2)+ 열(2)+ 번지(2)
	         */
	        String yd 	= add.substring(0, 1);
	        String bay 	= add.substring(1, 2);
	        String col  = add.substring(0, 6);
	        String bed	= null;
	        String layer= null;
	        String sDEMANDER_NM = "";
			String sHRMILL_CMPL_DT = "";
			String sNEXT_PROC = "";
			
	        if(YmCommonConst.YD_GP_3.equals(yd)) {
	            if(8 == add.length()) {
	                bed	= add.substring(6, 8);
	            }else if(10 == add.length()) {
	                bed		= add.substring(6, 8);
	                layer 	= add.substring(8, 10);
	            }
	        //2007-02-27 A열연 SLAB야드 추가(MCH)
	        }else if(YmCommonConst.YD_GP_2.equals(yd)
	        		|| YmCommonConst.YD_GP_0.equals(yd)) {
	        	if(8 <= add.length()){
	        		bed	= add.substring(6, 8);
	        	}
	        }
	        
	        List maps = null;
	        //2007-02-27 A열연 SLAB야드 추가(MCH)
	        if(YmCommonConst.YD_GP_2.equals(yd)
	        ||YmCommonConst.YD_GP_0.equals(yd)) {
	        	if(bed == null) {
	        		maps = ymCommonDAO.readAYDMapInfo(col);
	        	}else{
	        		maps = ymCommonDAO.readBYDMapInfo(col, bed);
	        	}
	        }else {
	            if(bed == null) {
		            maps = ymCommonDAO.readBYDMapInfo(col);	                
	            }else if(layer == null) {
	                maps = ymCommonDAO.readCoilBYDMapInfo(col, bed);
	            }else {
	                maps = ymCommonDAO.readBYDMapInfo(col, bed, layer);
	            }
	        }
	        
	        if(maps == null || maps.size() == 0) {
	        	//A열연 SLAB야드 맵(MCH)
	        	//송신할 경우 데이터가 없어도 BEDADDRESS는 전송함.
	        	if(YmCommonConst.YD_GP_0.equals(yd)){
	        		JDTORecord jRecord = JDTORecordFactory.getInstance().create();
	        		jRecord.setField("BEDADDRESS", add);
	        		maps.add(jRecord);
	        	}else{
		            if(bed == null) {
			            maps = ymCommonDAO.readBYDMapInfo(col, "01", "01");	                
		            }else {
			            maps = ymCommonDAO.readBYDMapInfo(col, bed, "01");
		            }
	        	}
	        }
	        
	        int mapsCnt = maps != null ? maps.size() : 0;

	        String tcName		 = null;
	        StringBuffer sendMsg = new StringBuffer();
	        if(YmCommonConst.YD_GP_3.equals(yd)) {
	            tcName = YmCommonConst.TC_CN1BP05;
	        }else if(YmCommonConst.YD_GP_2.equals(yd)) {
	            tcName = YmCommonConst.TC_CM1BP05;
	        //2007-02-27 A열연 SLAB야드 추가(MCH)//////////////////////////////////////////////////	            
	        }else if(YmCommonConst.TC_HC3PB52.equals(tc_cd)) {
	            tcName = YmCommonConst.TC_HC3BP51;	            
	        }else if(YmCommonConst.YD_GP_0.equals(yd) && YmCommonConst.BAY_GP_A.equals(bay)) {
	            tcName = YmCommonConst.TC_HM1BP04;
	        }else if(YmCommonConst.YD_GP_0.equals(yd) && YmCommonConst.BAY_GP_B.equals(bay)) {
	        	tcName = YmCommonConst.TC_HM1BP54;
	        }
	        //2007-02-27 A열연 SLAB야드 추가(MCH)//////////////////////////////////////////////////
	        Map tc = ymCommonDAO.readColumnLenOfTc(tcName);	        
	        sendMsg.append(tcName);
	        sendMsg.append(YmCommonUtil.getStringYMD("-"));
	        sendMsg.append(YmCommonUtil.getStringHMS("-"));
	        sendMsg.append(YmCommonConst.FORM_I);
	        
	        if(YmCommonConst.YD_GP_3.equals(yd)) {
	        	appendMsgNum(sendMsg, ""+ 155, getFieldLen(tc, "전문길이"));
	        }else{
	        	appendMsgNum(sendMsg, ""+ (YmCommonUtil.getTotalLenOfTc(tc) - 30), getFieldLen(tc, "전문길이"));
	        }
	        sendMsg.append(YmCommonConst.SEND_REQ_A);

	        JDTORecord dto = null;
	        if(mapsCnt > 0) {
		        if(YmCommonConst.YD_GP_3.equals(yd)) {
		            /**
		             * CN1BP05
		             * 전문코드		전문코드	CHAR	7		
		             * 발생일자		발생일자	CHAR	10		YYYY-MM-DD
		             * 발생시간		발생시간	CHAR	8		HH-MM-SS
		             * 전문구분		전문구분	CHAR	1		
		             * 전문길이		전문길이	CHAR	4		
		             * 송수신구분		송수신구분CHAR	1		R:요구, A:응답
		             * SEQ NO		SEQNO	CHAR	3		001 ~ 999, END
		             * SKID ADDRESS	SKIDADDRESS	CHAR	10		YARD(1) + 동(1) + SPAN(2) + 열(2) + 번지(2) + 단(2)
		             * 사용유무		사용유무		CHAR	1		SKID 사용유무
		             * COIL NO		CoilNo		CHAR	10		SPACE : 코일무
		             * 군정보			군정보		CHAR	1		
		             * 제작번호/행번	제작번호행번	CHAR	13		
		             * 두께			두께			CHAR	7	㎜	소수점3자리 (###.###)
		             * 폭				폭			CHAR	6	㎜	소수점1자리 (####.#)
		             * 길이			길이			CHAR	6	㎜	
		             * 외경			외경			CHAR	5	㎜	
		             * 중량			중량			CHAR	5	Kg	
		             * 분기위치CODE
		             * 확장분기위치CODE
		             * 냉각방법
		             * X 물리위치		X물리위치		CHAR	6		
		             * Y 물리위치		Y물리위치		CHAR	6		
		             * X 허용오차(+)	X허용오차P	CHAR	4		
		             * X 허용오차(-)	X허용오차M	CHAR	4		
		             * Y 허용오차(+)	Y허용오차P	CHAR	4		
		             * Y 허용오차(-)	Y허용오차M	CHAR	4		
		             */
		            for(int i = 0 ; i < mapsCnt; i++) {
		                dto = (JDTORecord)maps.get(i);
		                
		                JDTORecord putJr = dao.getCoilInfo(getField(dto,"STOCK_ID"));
						
						if(putJr != null){
							sDEMANDER_NM = StringHelper.evl(putJr.getFieldString("DEMANDER_NM"), "");
							sHRMILL_CMPL_DT = StringHelper.evl(putJr.getFieldString("HRMILL_CMPL_DT"), "");
							sNEXT_PROC = StringHelper.evl(putJr.getFieldString("NEXT_PROC"), "");
						} 
						
		                if(i == (mapsCnt - 1)) {
					        appendMsg(sendMsg, "END", 					getFieldLen(tc, "SEQNO"));	                    
		                }else {
					        appendMsgNum(sendMsg, ""+ i, 				getFieldLen(tc, "SEQNO"));
		                }
				        appendMsg(sendMsg, getField(dto,"SKIDADDRESS"),	getFieldLen(tc, "SKIDADDRESS"));
				        appendMsg(sendMsg, getField(dto,"USE_YN"), 		getFieldLen(tc, "사용유무"));
				        appendMsg(sendMsg, getField(dto,"STOCK_ID"), 	getFieldLen(tc, "CoilNo"));
				        appendMsg(sendMsg, "", 							getFieldLen(tc, "군정보"));
				        appendMsg(sendMsg, getField(dto,"PRODUCT_NO"), 	getFieldLen(tc, "제작번호행번"));
				        String t = YmCommonUtil.format(getField(dto, "COIL_T"), 3, 3).replace('.', ' ');
				        String w = YmCommonUtil.format(getField(dto, "COIL_W"), 4, 1).replace('.', ' ');
				        appendMsgNum(sendMsg, t.replaceAll(" ", ""), 		getFieldLen(tc, "두께"));
				        appendMsgNum(sendMsg, w.replaceAll(" ", ""), 		getFieldLen(tc, "폭"));
				        appendMsgNum(sendMsg, getField(dto, "COIL_LEN"), 	getFieldLen(tc, "길이"));
				        appendMsgNum(sendMsg, getField(dto, "COIL_OUTDIA"), getFieldLen(tc, "외경"));
				        appendMsgNum(sendMsg, getField(dto, "COIL_WT"), 	getFieldLen(tc, "중량"));
				        appendMsgNum(sendMsg, getField(dto, "BRANCH_CD"), 	getFieldLen(tc, "분기위치CODE"));
				        appendMsgNum(sendMsg, getField(dto, "EXTEND_CONVEYOR_BRANCH_CD"), 	getFieldLen(tc, "확장분기위치CODE"));
				        appendMsgNum(sendMsg, getField(dto, "COOL_METHOD"), 	getFieldLen(tc, "냉각방법"));
				        appendMsgNum(sendMsg, getField(dto, "STACK_LAYER_X_AXIS"), getFieldLen(tc, "X물리위치"));
				        appendMsgNum(sendMsg, getField(dto, "STACK_LAYER_Y_AXIS"), getFieldLen(tc, "Y물리위치"));
				         
				        appendMsgNum(sendMsg, getField(dto, "XPCD"), getFieldLen(tc, "X허용오차P"));
				        appendMsgNum(sendMsg, getField(dto, "XMCD"), getFieldLen(tc, "X허용오차M"));
				        appendMsgNum(sendMsg, getField(dto, "YPCD"), getFieldLen(tc, "Y허용오차P"));
				        appendMsgNum(sendMsg, getField(dto, "YMCD"), getFieldLen(tc, "Y허용오차M"));
				        appendMsgNum(sendMsg, sDEMANDER_NM			, getFieldLen(tc, "수요가"));
				        appendMsgNum(sendMsg, sHRMILL_CMPL_DT		, getFieldLen(tc, "압연완료시각"));
				        appendMsgNum(sendMsg, sNEXT_PROC			, getFieldLen(tc, "차공정"));
				        sendQueue(tcName, sendMsg.toString());
				        sendMsg.delete(31, sendMsg.length());
		            }
		        //2007-02-27 A열연 SLAB 야드 추가(MCH)
		        }else if(YmCommonConst.YD_GP_2.equals(yd)
		        		||YmCommonConst.YD_GP_0.equals(yd)) {
		            /**
		             * CM1BP05//HM1BP04//HM1BP54
		             * 전문코드		전문코드		CHAR	7		
		             * 발생일자		발생일자		CHAR	10		YYYY-MM-DD
		             * 발생시간		발생시간		CHAR	8		HH-MM-SS
		             * 전문구분		전문구분		CHAR	1		
		             * 전문길이		전문길이		CHAR	4		
		             * 송수신구분		송수신구분	CHAR	1		R:요구, A:응답
		             * BED ADDRESS	BEDADDRESS	CHAR	8		야드(1)+동(1)+Span(2)+Row(2)+BED(2)
		             * 사용유무		사용유무		CHAR	1		BED 사용유무
		             * 적치가능매수	적치가능매수	CHAR	2		BED 적치 가능 매수
		             * 적치매수		적치매수		CHAR	2		현재 적치 매수
		             * 적치 SEQ		적치SEQ		CHAR	2		SLAB 적치 단
		             * SLAB NO		SLABNo		CHAR	11		SPACE : 적치 무
		             * 제작번호/행번	제작번호행번	CHAR	13		
		             * 두께			두께			CHAR	7	㎜	소수점3자리 (###.###)
		             * 폭			폭			CHAR	6	㎜	소수점1자리 (####.#)
		             * 중량			중량			CHAR	5	kg	
		             * 길이			길이			CHAR	6		
		             * X 물리위치		X물리위치		CHAR	6		
		             * Y 물리위치		Y물리위치		CHAR	6		
		             * X 허용오차(+)	X허용오차P	CHAR	4		
		             * X 허용오차(-)	X허용오차M	CHAR	4		
		             * Y 허용오차(+)	Y허용오차P	CHAR	4		
		             * Y 허용오차(-)	Y허용오차M	CHAR	4		
		             */
		        	//A열연 SLAB야드 연주 7호기에서 
			        //appendMsg(sendMsg, add, getFieldLen(tc, "BEDADDRESS"));
		            for(int i = 0 ; i < mapsCnt; i++) {
		                dto = (JDTORecord)maps.get(i);
		                //appendMsg(sendMsg, getField(dto,"BEDADDRESS"), 				getFieldLen(tc, "BEDADDRESS"));
		                appendMsg(sendMsg, StringHelper.evl(dto.getFieldString("BEDADDRESS"), StringHelper.evl(dto.getFieldString("SKIDADDRESS"), "")),getFieldLen(tc, "BEDADDRESS"));
		                      appendMsg(sendMsg, getField(dto,"USE_YN"), 					getFieldLen(tc, "사용유무"));
				        appendMsgNum(sendMsg, getField(dto,"STACK_BED_QNTY_MAX"), 	getFieldLen(tc, "적치가능매수"));
				        appendMsgNum(sendMsg, getField(dto,"STACK_BED_QNTY_CURR"), 	getFieldLen(tc, "현재적치매수"));
				        appendMsgNum(sendMsg, getField(dto,"STACK_LAYER_GP"), 		getFieldLen(tc, "적치단"));
				        appendMsg(sendMsg, getField(dto,"STOCK_ID"), 				getFieldLen(tc, "SLAB번호"));
				        appendMsg(sendMsg, getField(dto,"PRODUCT_NO"), 				getFieldLen(tc, "제작번호행번"));
				        String t = YmCommonUtil.format(getField(dto, "SLAB_T"), 3, 3).replace('.', ' ');
				        String w = YmCommonUtil.format(getField(dto, "SLAB_W"), 4, 1).replace('.', ' ');
				        appendMsgNum(sendMsg, t.replaceAll(" ", ""), 				getFieldLen(tc, "두께"));
				        appendMsgNum(sendMsg, w.replaceAll(" ", ""), 				getFieldLen(tc, "폭"));
				        appendMsgNum(sendMsg, getField(dto, "SLAB_WT"), 			getFieldLen(tc, "중량"));
				        appendMsgNum(sendMsg, getField(dto, "SLAB_LEN"), 			getFieldLen(tc, "길이"));
				        appendMsgNum(sendMsg, getField(dto, "STACK_LAYER_X_AXIS"), 	getFieldLen(tc, "X물리위치"));
				        appendMsgNum(sendMsg, getField(dto, "STACK_LAYER_Y_AXIS"), 	getFieldLen(tc, "Y물리위치"));
				        
				        appendMsgNum(sendMsg, getField(dto, "XPCD"), getFieldLen(tc, "X허용오차P"));
				        appendMsgNum(sendMsg, getField(dto, "XMCD"), getFieldLen(tc, "X허용오차M"));
				        appendMsgNum(sendMsg, getField(dto, "YPCD"), getFieldLen(tc, "Y허용오차P"));
				        appendMsgNum(sendMsg, getField(dto, "YMCD"), getFieldLen(tc, "Y허용오차M"));
				        sendQueue(tcName, sendMsg.toString());
				        sendMsg.delete(31, sendMsg.length());
		            }
		        }	        	            
	        }
	    }catch(DAOException daoe) {
	        throw daoe;
	    }catch(Exception e) {
	        throw new EJBServiceException(e);
	    }
    }

	
	/**
	 * 임가공 PIDEV
     * B열연 야드맵 응답, A열연 야드맵 응답(추가)
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param add	야드맵정보
	 * @param gp	요청/응답 구분
     */
	public void sendResponsePI(String add, String tc_cd) {
	    try {
	    	
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
	        /**
	         * 요청한 야드맵 정보를 가져온다.
	         * add
	         * --COIL:야드(1)+ 동(1)+ Span(2)+ 열(2)+ 번지(2) + 단(2)
	         * --SLAB:야드(1)+ 동(1)+ Span(2)+ 열(2)+ 번지(2)
	         */
	        String yd 	= add.substring(0, 1);
	        String bay 	= add.substring(1, 2);
	        String col  = add.substring(0, 6);
	        String bed	= null;
	        String layer= null;
	        String sDEMANDER_NM = "";
			String sHRMILL_CMPL_DT = "";
			String sNEXT_PROC = "";
			
	        if(YmCommonConst.YD_GP_3.equals(yd)) {
	            if(8 == add.length()) {
	                bed	= add.substring(6, 8);
	            }else if(10 == add.length()) {
	                bed		= add.substring(6, 8);
	                layer 	= add.substring(8, 10);
	            }
	        //2007-02-27 A열연 SLAB야드 추가(MCH)
	        }else if(YmCommonConst.YD_GP_2.equals(yd)
	        		|| YmCommonConst.YD_GP_0.equals(yd)) {
	        	if(8 <= add.length()){
	        		bed	= add.substring(6, 8);
	        	}
	        }
	        
	        List maps = null;
	        //2007-02-27 A열연 SLAB야드 추가(MCH)
	        if(YmCommonConst.YD_GP_2.equals(yd)
	        ||YmCommonConst.YD_GP_0.equals(yd)) {
	        	if(bed == null) {
	        		maps = ymCommonDAO.readAYDMapInfo(col);
	        	}else{
	        		maps = ymCommonDAO.readBYDMapInfo(col, bed);
	        	}
	        }else {
	            if(bed == null) {
		            maps = ymCommonDAO.readBYDMapInfo(col);	                
	            }else if(layer == null) {
	                maps = ymCommonDAO.readCoilBYDMapInfo(col, bed);
	            }else {
	                maps = ymCommonDAO.readBYDMapInfo(col, bed, layer);
	            }
	        }
	        
	        if(maps == null || maps.size() == 0) {
	        	//A열연 SLAB야드 맵(MCH)
	        	//송신할 경우 데이터가 없어도 BEDADDRESS는 전송함.
	        	if(YmCommonConst.YD_GP_0.equals(yd)){
	        		JDTORecord jRecord = JDTORecordFactory.getInstance().create();
	        		jRecord.setField("BEDADDRESS", add);
	        		maps.add(jRecord);
	        	}else{
		            if(bed == null) {
			            maps = ymCommonDAO.readBYDMapInfo(col, "01", "01");	                
		            }else {
			            maps = ymCommonDAO.readBYDMapInfo(col, bed, "01");
		            }
	        	}
	        }
	        
	        int mapsCnt = maps != null ? maps.size() : 0;

	        String tcName		 = null;
	        StringBuffer sendMsg = new StringBuffer();
	        if(YmCommonConst.YD_GP_3.equals(yd)) {
	            tcName = YmCommonConst.TC_CN1BP05;
	        }else if(YmCommonConst.YD_GP_2.equals(yd)) {
	            tcName = YmCommonConst.TC_CM1BP05;
	        //2007-02-27 A열연 SLAB야드 추가(MCH)//////////////////////////////////////////////////	            
	        }else if(YmCommonConst.TC_HC3PB52.equals(tc_cd)) {
	            tcName = YmCommonConst.TC_HC3BP51;	            
	        }else if(YmCommonConst.YD_GP_0.equals(yd) && YmCommonConst.BAY_GP_A.equals(bay)) {
	            tcName = YmCommonConst.TC_HM1BP04;
	        }else if(YmCommonConst.YD_GP_0.equals(yd) && YmCommonConst.BAY_GP_B.equals(bay)) {
	        	tcName = YmCommonConst.TC_HM1BP54;
	        }
	        //2007-02-27 A열연 SLAB야드 추가(MCH)//////////////////////////////////////////////////
	        Map tc = ymCommonDAO.readColumnLenOfTc(tcName);	        
	        sendMsg.append(tcName);
	        sendMsg.append(YmCommonUtil.getStringYMD("-"));
	        sendMsg.append(YmCommonUtil.getStringHMS("-"));
	        sendMsg.append(YmCommonConst.FORM_I);
	        
	        if(YmCommonConst.YD_GP_3.equals(yd)) {
	        	appendMsgNum(sendMsg, ""+ 155, getFieldLen(tc, "전문길이"));
	        }else{
	        	appendMsgNum(sendMsg, ""+ (YmCommonUtil.getTotalLenOfTc(tc) - 30), getFieldLen(tc, "전문길이"));
	        }
	        sendMsg.append(YmCommonConst.SEND_REQ_A);

	        JDTORecord dto = null;
	        if(mapsCnt > 0) {
		        if(YmCommonConst.YD_GP_3.equals(yd)) {
		            /**
		             * CN1BP05
		             * 전문코드		전문코드	CHAR	7		
		             * 발생일자		발생일자	CHAR	10		YYYY-MM-DD
		             * 발생시간		발생시간	CHAR	8		HH-MM-SS
		             * 전문구분		전문구분	CHAR	1		
		             * 전문길이		전문길이	CHAR	4		
		             * 송수신구분		송수신구분CHAR	1		R:요구, A:응답
		             * SEQ NO		SEQNO	CHAR	3		001 ~ 999, END
		             * SKID ADDRESS	SKIDADDRESS	CHAR	10		YARD(1) + 동(1) + SPAN(2) + 열(2) + 번지(2) + 단(2)
		             * 사용유무		사용유무		CHAR	1		SKID 사용유무
		             * COIL NO		CoilNo		CHAR	10		SPACE : 코일무
		             * 군정보			군정보		CHAR	1		
		             * 제작번호/행번	제작번호행번	CHAR	13		
		             * 두께			두께			CHAR	7	㎜	소수점3자리 (###.###)
		             * 폭				폭			CHAR	6	㎜	소수점1자리 (####.#)
		             * 길이			길이			CHAR	6	㎜	
		             * 외경			외경			CHAR	5	㎜	
		             * 중량			중량			CHAR	5	Kg	
		             * 분기위치CODE
		             * 확장분기위치CODE
		             * 냉각방법
		             * X 물리위치		X물리위치		CHAR	6		
		             * Y 물리위치		Y물리위치		CHAR	6		
		             * X 허용오차(+)	X허용오차P	CHAR	4		
		             * X 허용오차(-)	X허용오차M	CHAR	4		
		             * Y 허용오차(+)	Y허용오차P	CHAR	4		
		             * Y 허용오차(-)	Y허용오차M	CHAR	4		
		             */
		            for(int i = 0 ; i < mapsCnt; i++) {
		                dto = (JDTORecord)maps.get(i);
		                
		                JDTORecord putJr = dao.getCoilInfo(getField(dto,"STOCK_ID"));
						
						if(putJr != null){
							sDEMANDER_NM = StringHelper.evl(putJr.getFieldString("DEMANDER_NM"), "");
							sHRMILL_CMPL_DT = StringHelper.evl(putJr.getFieldString("HRMILL_CMPL_DT"), "");
							sNEXT_PROC = StringHelper.evl(putJr.getFieldString("NEXT_PROC"), "");
						} 
						
		                if(i == (mapsCnt - 1)) {
					        appendMsg(sendMsg, "END", 					getFieldLen(tc, "SEQNO"));	                    
		                }else {
					        appendMsgNum(sendMsg, ""+ i, 				getFieldLen(tc, "SEQNO"));
		                }
				        appendMsg(sendMsg, getField(dto,"SKIDADDRESS"),	getFieldLen(tc, "SKIDADDRESS"));
				        appendMsg(sendMsg, getField(dto,"USE_YN"), 		getFieldLen(tc, "사용유무"));
				        appendMsg(sendMsg, getField(dto,"STOCK_ID"), 	getFieldLen(tc, "CoilNo"));
				        appendMsg(sendMsg, "", 							getFieldLen(tc, "군정보"));
				        appendMsg(sendMsg, getField(dto,"PRODUCT_NO"), 	getFieldLen(tc, "제작번호행번"));
				        String t = YmCommonUtil.format(getField(dto, "COIL_T"), 3, 3).replace('.', ' ');
				        String w = YmCommonUtil.format(getField(dto, "COIL_W"), 4, 1).replace('.', ' ');
				        appendMsgNum(sendMsg, t.replaceAll(" ", ""), 		getFieldLen(tc, "두께"));
				        appendMsgNum(sendMsg, w.replaceAll(" ", ""), 		getFieldLen(tc, "폭"));
				        appendMsgNum(sendMsg, getField(dto, "COIL_LEN"), 	getFieldLen(tc, "길이"));
				        appendMsgNum(sendMsg, getField(dto, "COIL_OUTDIA"), getFieldLen(tc, "외경"));
				        appendMsgNum(sendMsg, getField(dto, "COIL_WT"), 	getFieldLen(tc, "중량"));
				        appendMsgNum(sendMsg, getField(dto, "BRANCH_CD"), 	getFieldLen(tc, "분기위치CODE"));
				        appendMsgNum(sendMsg, getField(dto, "EXTEND_CONVEYOR_BRANCH_CD"), 	getFieldLen(tc, "확장분기위치CODE"));
				        appendMsgNum(sendMsg, getField(dto, "COOL_METHOD"), 	getFieldLen(tc, "냉각방법"));
				        appendMsgNum(sendMsg, getField(dto, "STACK_LAYER_X_AXIS"), getFieldLen(tc, "X물리위치"));
				        appendMsgNum(sendMsg, getField(dto, "STACK_LAYER_Y_AXIS"), getFieldLen(tc, "Y물리위치"));
				         
				        appendMsgNum(sendMsg, getField(dto, "XPCD"), getFieldLen(tc, "X허용오차P"));
				        appendMsgNum(sendMsg, getField(dto, "XMCD"), getFieldLen(tc, "X허용오차M"));
				        appendMsgNum(sendMsg, getField(dto, "YPCD"), getFieldLen(tc, "Y허용오차P"));
				        appendMsgNum(sendMsg, getField(dto, "YMCD"), getFieldLen(tc, "Y허용오차M"));
				        appendMsgNum(sendMsg, sDEMANDER_NM			, getFieldLen(tc, "수요가"));
				        appendMsgNum(sendMsg, sHRMILL_CMPL_DT		, getFieldLen(tc, "압연완료시각"));
				        appendMsgNum(sendMsg, sNEXT_PROC			, getFieldLen(tc, "차공정"));
				        sendQueue(tcName, sendMsg.toString());
				        sendMsg.delete(31, sendMsg.length());
		            }
		        //2007-02-27 A열연 SLAB 야드 추가(MCH)
		        }else if(YmCommonConst.YD_GP_2.equals(yd)
		        		||YmCommonConst.YD_GP_0.equals(yd)) {
		            /**
		             * CM1BP05//HM1BP04//HM1BP54
		             * 전문코드		전문코드		CHAR	7		
		             * 발생일자		발생일자		CHAR	10		YYYY-MM-DD
		             * 발생시간		발생시간		CHAR	8		HH-MM-SS
		             * 전문구분		전문구분		CHAR	1		
		             * 전문길이		전문길이		CHAR	4		
		             * 송수신구분		송수신구분	CHAR	1		R:요구, A:응답
		             * BED ADDRESS	BEDADDRESS	CHAR	8		야드(1)+동(1)+Span(2)+Row(2)+BED(2)
		             * 사용유무		사용유무		CHAR	1		BED 사용유무
		             * 적치가능매수	적치가능매수	CHAR	2		BED 적치 가능 매수
		             * 적치매수		적치매수		CHAR	2		현재 적치 매수
		             * 적치 SEQ		적치SEQ		CHAR	2		SLAB 적치 단
		             * SLAB NO		SLABNo		CHAR	11		SPACE : 적치 무
		             * 제작번호/행번	제작번호행번	CHAR	13		
		             * 두께			두께			CHAR	7	㎜	소수점3자리 (###.###)
		             * 폭			폭			CHAR	6	㎜	소수점1자리 (####.#)
		             * 중량			중량			CHAR	5	kg	
		             * 길이			길이			CHAR	6		
		             * X 물리위치		X물리위치		CHAR	6		
		             * Y 물리위치		Y물리위치		CHAR	6		
		             * X 허용오차(+)	X허용오차P	CHAR	4		
		             * X 허용오차(-)	X허용오차M	CHAR	4		
		             * Y 허용오차(+)	Y허용오차P	CHAR	4		
		             * Y 허용오차(-)	Y허용오차M	CHAR	4		
		             */
		        	//A열연 SLAB야드 연주 7호기에서 
			        //appendMsg(sendMsg, add, getFieldLen(tc, "BEDADDRESS"));
		            for(int i = 0 ; i < mapsCnt; i++) {
		                dto = (JDTORecord)maps.get(i);
		                //appendMsg(sendMsg, getField(dto,"BEDADDRESS"), 				getFieldLen(tc, "BEDADDRESS"));
		                appendMsg(sendMsg, StringHelper.evl(dto.getFieldString("BEDADDRESS"), StringHelper.evl(dto.getFieldString("SKIDADDRESS"), "")),getFieldLen(tc, "BEDADDRESS"));
		                      appendMsg(sendMsg, getField(dto,"USE_YN"), 					getFieldLen(tc, "사용유무"));
				        appendMsgNum(sendMsg, getField(dto,"STACK_BED_QNTY_MAX"), 	getFieldLen(tc, "적치가능매수"));
				        appendMsgNum(sendMsg, getField(dto,"STACK_BED_QNTY_CURR"), 	getFieldLen(tc, "현재적치매수"));
				        appendMsgNum(sendMsg, getField(dto,"STACK_LAYER_GP"), 		getFieldLen(tc, "적치단"));
				        appendMsg(sendMsg, getField(dto,"STOCK_ID"), 				getFieldLen(tc, "SLAB번호"));
				        appendMsg(sendMsg, getField(dto,"PRODUCT_NO"), 				getFieldLen(tc, "제작번호행번"));
				        String t = YmCommonUtil.format(getField(dto, "SLAB_T"), 3, 3).replace('.', ' ');
				        String w = YmCommonUtil.format(getField(dto, "SLAB_W"), 4, 1).replace('.', ' ');
				        appendMsgNum(sendMsg, t.replaceAll(" ", ""), 				getFieldLen(tc, "두께"));
				        appendMsgNum(sendMsg, w.replaceAll(" ", ""), 				getFieldLen(tc, "폭"));
				        appendMsgNum(sendMsg, getField(dto, "SLAB_WT"), 			getFieldLen(tc, "중량"));
				        appendMsgNum(sendMsg, getField(dto, "SLAB_LEN"), 			getFieldLen(tc, "길이"));
				        appendMsgNum(sendMsg, getField(dto, "STACK_LAYER_X_AXIS"), 	getFieldLen(tc, "X물리위치"));
				        appendMsgNum(sendMsg, getField(dto, "STACK_LAYER_Y_AXIS"), 	getFieldLen(tc, "Y물리위치"));
				        
				        appendMsgNum(sendMsg, getField(dto, "XPCD"), getFieldLen(tc, "X허용오차P"));
				        appendMsgNum(sendMsg, getField(dto, "XMCD"), getFieldLen(tc, "X허용오차M"));
				        appendMsgNum(sendMsg, getField(dto, "YPCD"), getFieldLen(tc, "Y허용오차P"));
				        appendMsgNum(sendMsg, getField(dto, "YMCD"), getFieldLen(tc, "Y허용오차M"));
				        sendQueue(tcName, sendMsg.toString());
				        sendMsg.delete(31, sendMsg.length());
		            }
		        }	        	            
	        }
	    }catch(DAOException daoe) {
	        throw daoe;
	    }catch(Exception e) {
	        throw new EJBServiceException(e);
	    }
    }
	
    /**
     *	A열연 SLAB야드 (MCH) 야드맵 응답 처리
     * @param parseData
     */
    private void AslabeditResponse(JDTORecord parseData) {
        /**
         * --SLAB:야드(1)+ 동(1)+ Span(2)+ 열(2)+ 번지(2)+ 단(2)
         */
    	Boolean isTrue  = Boolean.FALSE;
        String f_addr	= "";
        String t_addr	= getField(parseData, "BEDADDRESS") + getField(parseData, "적치단");
        String SlabGbn  = "R";
        String stockid 	= getField(parseData, "SLAB번호");
        try{
        	
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
			
	        /*
			SELECT A.YD_GP AS 야드,
			       A.STOCK_ID,
			       ADDR AS 현재위치,
			       DECODE(ADDR, NULL, '없음', '적치') AS 적치상태,
			       NVL(B.SCH_WORK_KIND, ' ') AS SCH, 
			       DECODE(A.STOCK_ITEM, 'SM', D.CURR_PROG_CD,
			                            'CM', C.CURR_PROG_CD,
			                            'CG', C.CURR_PROG_CD,
			                            '-') AS 진도코드,
			       A.STOCK_MOVE_TERM AS 이동조건
			FROM (        
			       SELECT A.STOCK_ID, A.STOCK_ITEM, A.STOCK_MOVE_TERM,
			              NVL(A.WBOOK_ID, '20060101000000') AS WBOOK_ID,
			              NVL(SUBSTR(B.STACK_COL_GP, 1, 1), '-') AS YD_GP,
			              NVL(B.STACK_COL_GP, '-') AS COL,
			              NVL(B.STACK_BED_GP, '-') AS BED,
			              NVL(B.STACK_LAYER_GP, '-') AS LAYER,
			              B.STACK_COL_GP||B.STACK_BED_GP||B.STACK_LAYER_GP AS ADDR
			       FROM TB_YM_STOCK A, TB_YM_STACKLAYER B
			       WHERE A.STOCK_ID = B.STOCK_ID(+)
			         AND A.STOCK_ID = ? --저장품번호(HB00001)
			     ) A, TB_YM_WBOOK B, USRPMA.TB_PM_COILCOMM C, USRPMA.TB_PM_SLABCOMM D
			WHERE A.WBOOK_ID = B.WBOOK_ID(+)
			  AND A.STOCK_ID = C.COIL_NO(+)
			  AND A.STOCK_ID = D.SLAB_NO(+)
			*/
	        String queryid = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.getStockLoc";
	        JDTORecord jrecord = ymCommonDAO.getCommonInfo(queryid, new Object[]{stockid});
	        
	        if(jrecord == null){
	        	logger.println(LogLevel.DEBUG, this, "### A열연 SLAB야드 일관 처리 SLAB+"+stockid+"]의 정보가 없습니다.");
	        	throw new Exception("### A열연 SLAB야드 일관 처리 SLAB+"+stockid+"]의 정보가 없습니다.");
	        }
	        
	        f_addr = StringHelper.evl(jrecord.getFieldString("현재위치"), "");
	        logger.println(LogLevel.DEBUG, this, "### A열연 SLAB야드 일관 처리 위치가 변경된 SLAB만 처리한다.");
	        logger.println(LogLevel.DEBUG, this, "### A열연 SLAB야드 SLAB_NO ["+ stockid +"]");
	        logger.println(LogLevel.DEBUG, this, "### A열연 SLAB야드 현 위치["+ f_addr +"] -> 변경위치["+ t_addr +"]");
	
	        if(!f_addr.equals(t_addr)){
				logger.println(LogLevel.DEBUG, this, "### A열연 SLAB야드 현 위치["+ f_addr +"] -> 변경위치["+ t_addr +"] 산적위치 수정 모듈 CALL");
		        EJBConnector ejbConn1 = new EJBConnector("default","JNDICraneSchReg",this);
		    	isTrue = (Boolean)ejbConn1.trx("changeSlabLocationInfo",new Class[]{String.class, 
		    																		String.class,
		    																		String.class, 
		    																		String.class},
		    														   new Object[]{stockid, 
		    														    			f_addr, 
		    														    			t_addr,
		    														    			SlabGbn});
	        }
	        
	        if(isTrue.booleanValue() == false){
	        	logger.println(LogLevel.DEBUG, this, "### A열연 SLAB야드 일관 처리 재료번호 ["+ stockid +"]를 ["+ t_addr +"]위치로 산적위치를 수정하는 도중에 에러가 발생하였습니다.");
	        	throw new Exception("### A열연 SLAB야드 일관 처리 재료번호 ["+ stockid +"]를 ["+ t_addr +"]위치로 산적위치를 수정하는 도중에 에러가 발생하였습니다.");
	        }
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
    }
    

    /**
     * 임가공 PIDEV
     *	A열연 SLAB야드 (MCH) 야드맵 응답 처리
     * @param parseData
     */
    private void AslabeditResponsePI(JDTORecord parseData) {
        /**
         * --SLAB:야드(1)+ 동(1)+ Span(2)+ 열(2)+ 번지(2)+ 단(2)
         */
    	Boolean isTrue  = Boolean.FALSE;
        String f_addr	= "";
        String t_addr	= getField(parseData, "BEDADDRESS") + getField(parseData, "적치단");
        String SlabGbn  = "R";
        String stockid 	= getField(parseData, "SLAB번호");
        try{
        	
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
			
	        /*
			SELECT A.YD_GP AS 야드,
			       A.STOCK_ID,
			       ADDR AS 현재위치,
			       DECODE(ADDR, NULL, '없음', '적치') AS 적치상태,
			       NVL(B.SCH_WORK_KIND, ' ') AS SCH, 
			       DECODE(A.STOCK_ITEM, 'SM', D.CURR_PROG_CD,
			                            'CM', C.CURR_PROG_CD,
			                            'CG', C.CURR_PROG_CD,
			                            '-') AS 진도코드,
			       A.STOCK_MOVE_TERM AS 이동조건
			FROM (        
			       SELECT A.STOCK_ID, A.STOCK_ITEM, A.STOCK_MOVE_TERM,
			              NVL(A.WBOOK_ID, '20060101000000') AS WBOOK_ID,
			              NVL(SUBSTR(B.STACK_COL_GP, 1, 1), '-') AS YD_GP,
			              NVL(B.STACK_COL_GP, '-') AS COL,
			              NVL(B.STACK_BED_GP, '-') AS BED,
			              NVL(B.STACK_LAYER_GP, '-') AS LAYER,
			              B.STACK_COL_GP||B.STACK_BED_GP||B.STACK_LAYER_GP AS ADDR
			       FROM TB_YM_STOCK A, TB_YM_STACKLAYER B
			       WHERE A.STOCK_ID = B.STOCK_ID(+)
			         AND A.STOCK_ID = ? --저장품번호(HB00001)
			     ) A, TB_YM_WBOOK B, USRPMA.TB_PM_COILCOMM C, USRPMA.TB_PM_SLABCOMM D
			WHERE A.WBOOK_ID = B.WBOOK_ID(+)
			  AND A.STOCK_ID = C.COIL_NO(+)
			  AND A.STOCK_ID = D.SLAB_NO(+)
			*/
	        String queryid = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.getStockLoc";
	        JDTORecord jrecord = ymCommonDAO.getCommonInfo(queryid, new Object[]{stockid});
	        
	        if(jrecord == null){
	        	logger.println(LogLevel.DEBUG, this, "### A열연 SLAB야드 일관 처리 SLAB+"+stockid+"]의 정보가 없습니다.");
	        	throw new Exception("### A열연 SLAB야드 일관 처리 SLAB+"+stockid+"]의 정보가 없습니다.");
	        }
	        
	        f_addr = StringHelper.evl(jrecord.getFieldString("현재위치"), "");
	        logger.println(LogLevel.DEBUG, this, "### A열연 SLAB야드 일관 처리 위치가 변경된 SLAB만 처리한다.");
	        logger.println(LogLevel.DEBUG, this, "### A열연 SLAB야드 SLAB_NO ["+ stockid +"]");
	        logger.println(LogLevel.DEBUG, this, "### A열연 SLAB야드 현 위치["+ f_addr +"] -> 변경위치["+ t_addr +"]");
	
	        if(!f_addr.equals(t_addr)){
				logger.println(LogLevel.DEBUG, this, "### A열연 SLAB야드 현 위치["+ f_addr +"] -> 변경위치["+ t_addr +"] 산적위치 수정 모듈 CALL");
		        EJBConnector ejbConn1 = new EJBConnector("default","JNDICraneSchReg",this);
		    	isTrue = (Boolean)ejbConn1.trx("changeSlabLocationInfoPI",new Class[]{String.class, 
		    																		String.class,
		    																		String.class, 
		    																		String.class},
		    														   new Object[]{stockid, 
		    														    			f_addr, 
		    														    			t_addr,
		    														    			SlabGbn});
	        }
	        
	        if(isTrue.booleanValue() == false){
	        	logger.println(LogLevel.DEBUG, this, "### A열연 SLAB야드 일관 처리 재료번호 ["+ stockid +"]를 ["+ t_addr +"]위치로 산적위치를 수정하는 도중에 에러가 발생하였습니다.");
	        	throw new Exception("### A열연 SLAB야드 일관 처리 재료번호 ["+ stockid +"]를 ["+ t_addr +"]위치로 산적위치를 수정하는 도중에 에러가 발생하였습니다.");
	        }
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
    }
        
    
    /**
     *	A열연, B열연 야드맵 응답 처리
     * @param parseData
     */
    private void editResponse(JDTORecord parseData) {
    	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        /**
         * 요청한 야드맵 정보를 가져온다.
         * add
         * --COIL:야드(1)+ 동(1)+ Span(2)+ 열(2)+ 번지(2) + 단(2)
         * --SLAB:야드(1)+ 동(1)+ Span(2)+ 열(2)+ 번지(2)
         */
        String add 	= null;
        String col  = null;
        String bed	= null;
        String layer= null;
        String tcCd = getField(parseData, "전문코드");
        if(YmCommonConst.TC_CM1PB10.equals(tcCd)) {
            add = getField(parseData, "BEDADDRESS");
            col = add.substring(0, 6);
            bed = add.substring(6, 8);
            layer = getField(parseData, "적치단");
            considerChangeLoc(parseData, "changeSlabLocationInfo", 2);
            ymCommonDAO.modifyPossibleAndCurrCntOfStacker(  getField(parseData, "적치가능매수"), 
										                    getField(parseData, "현재적치매수"), col, bed);
        
         //2007-02-26 A열연 SLAB야드 추가(MCH)
        }else if(YmCommonConst.TC_HM1PB09.equals(tcCd) ||
        		YmCommonConst.TC_HM1PB59.equals(tcCd)  ||
        		YmCommonConst.TC_HC3BP51.equals(tcCd)) {
            add = getField(parseData, "BEDADDRESS");
            col = add.substring(0, 6);
            bed = add.substring(6, 8);
            layer = getField(parseData, "적치단");
            considerChangeLoc(parseData, "changeSlabLocationInfo", 2);
            ymCommonDAO.modifyPossibleAndCurrCntOfStacker(  getField(parseData, "적치가능매수"), 
										                    getField(parseData, "현재적치매수"), col, bed);
        }else {
            add = getField(parseData, "SKIDADDRESS");
            col = add.substring(0, 6);
            bed = add.substring(6, 8);
            layer = add.substring(8, 10);
            considerChangeLoc(parseData, "changeCoilLocationInfo", 3);
        }        
        /**
         * X물리위치, Y물리위치 UPDATE
         */
        /*2007-06-05 mch 물리적 위치는 수정되면 않된다구 해서 임시 주석 처리하려 했으나 요청시 산적위치를 수정하여 문제가 발생요인이있어서 그냥 두었음.
         * 쿼리틀렸음.. layer아니고 TB_YM_STACKER로 되어있음...
		 */
        ymCommonDAO.modifyXYLocOfLayer(
                getField(parseData, "X물리위치"),
                getField(parseData, "Y물리위치"), col, bed, layer);
    }

    
    /**
     * 임가공 PIDEV
     *	A열연, B열연 야드맵 응답 처리
     * @param parseData
     */
    private void editResponsePI(JDTORecord parseData) {
    	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        /**
         * 요청한 야드맵 정보를 가져온다.
         * add
         * --COIL:야드(1)+ 동(1)+ Span(2)+ 열(2)+ 번지(2) + 단(2)
         * --SLAB:야드(1)+ 동(1)+ Span(2)+ 열(2)+ 번지(2)
         */
        String add 	= null;
        String col  = null;
        String bed	= null;
        String layer= null;
        String tcCd = getField(parseData, "전문코드");
        if(YmCommonConst.TC_CM1PB10.equals(tcCd)) {
            add = getField(parseData, "BEDADDRESS");
            col = add.substring(0, 6);
            bed = add.substring(6, 8);
            layer = getField(parseData, "적치단");
            considerChangeLoc(parseData, "changeSlabLocationInfoPI", 2);
            ymCommonDAO.modifyPossibleAndCurrCntOfStacker(  getField(parseData, "적치가능매수"), 
										                    getField(parseData, "현재적치매수"), col, bed);
        
         //2007-02-26 A열연 SLAB야드 추가(MCH)
        }else if(YmCommonConst.TC_HM1PB09.equals(tcCd) ||
        		YmCommonConst.TC_HM1PB59.equals(tcCd)  ||
        		YmCommonConst.TC_HC3BP51.equals(tcCd)) {
            add = getField(parseData, "BEDADDRESS");
            col = add.substring(0, 6);
            bed = add.substring(6, 8);
            layer = getField(parseData, "적치단");
            considerChangeLoc(parseData, "changeSlabLocationInfoPI", 2);
            ymCommonDAO.modifyPossibleAndCurrCntOfStacker(  getField(parseData, "적치가능매수"), 
										                    getField(parseData, "현재적치매수"), col, bed);
        }else {
            add = getField(parseData, "SKIDADDRESS");
            col = add.substring(0, 6);
            bed = add.substring(6, 8);
            layer = add.substring(8, 10);
            considerChangeLoc(parseData, "changeCoilLocationInfoPI", 3);
        }        
        /**
         * X물리위치, Y물리위치 UPDATE
         */
        /*2007-06-05 mch 물리적 위치는 수정되면 않된다구 해서 임시 주석 처리하려 했으나 요청시 산적위치를 수정하여 문제가 발생요인이있어서 그냥 두었음.
         * 쿼리틀렸음.. layer아니고 TB_YM_STACKER로 되어있음...
		 */
        ymCommonDAO.modifyXYLocOfLayer(
                getField(parseData, "X물리위치"),
                getField(parseData, "Y물리위치"), col, bed, layer);
    }
    
    
    /**
     * @param string
     * @param stockId
     * @param col
     * @param bed
     * @param layer
     */
    private void considerChangeLoc(JDTORecord pData, String callMethod, int gp) {
    	
    	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        String add 	= null;
        String col	= null;  
        String bed	= null;
        String layer 	= null;
        String stockId 	= null;
        if(gp == 3) {
            add = getField(pData, "SKIDADDRESS");
            col	= add.substring(0, 6);  
            bed	= add.substring(6, 8);
            layer = add.substring(8, 10);
            stockId = getField(pData, "CoilNo");
        }else {
            add = getField(pData, "BEDADDRESS");
            col	= add.substring(0, 6);  
            bed	= add.substring(6, 8);
            layer = getField(pData, "적치단");
            stockId = getField(pData, "SLAB번호");
        }
        
        String useYN = getField(pData, "사용유무");
        if(YmCommonConst.USE_YN_Y.equals(useYN)) {
            if("".equals(stockId)) {
                ymCommonDAO.modifyStockStatOfLayer("", "E", col, bed, layer);
            }else {
                callLocationModify(callMethod, stockId, (col + bed + layer), gp);
            }
        }else if(YmCommonConst.USE_YN_N.equals(useYN)) {
            if("".equals(stockId)) {
                ymCommonDAO.modifyStockStatOfLayer("", "C", "E", col, bed, layer);
            }else {
                callLocationModify(callMethod, stockId, (col + bed + layer), gp);
            }
        }
    }

    /**
     * @param string
     * @param field
     * @param string2
     */
    private void callLocationModify(String methodName, String stockId, String toAdd, int gp) {
        EJBConnector ejbConn = null;
        try {
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return;
    		}
    		
            ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
            if(gp == 3) {
                ejbConn.trx(methodName, 
                        new Class[]{ String.class, String.class, String.class },
                        new Object[]{ stockId, "", toAdd });
            }else {
                ejbConn.trx(methodName, 
                        new Class[]{ String.class, String.class, String.class, String.class },
                        new Object[]{ stockId, "", toAdd, "R" });                
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    private void editYdMap(String stock, String useGp, String col, String bed, String layer) {
    	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        String actStat = null;
        if(YmCommonConst.USE_YN_0.equals(useGp)) {
            actStat = YmCommonConst.STACK_LAYER_ACTIVE_STAT_C;
        }else {
            actStat = YmCommonConst.STACK_LAYER_ACTIVE_STAT_O;
        }
        ymCommonDAO.modifyYdMap(stock, actStat, col, bed, layer);
	}

	/**
	 * 오퍼레이션명 : 
	 *
	 * 야드 Level-2로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * 1.TC_CD	: CM1PB03.
        * 2.I/F ID	: YM-BIF-020.
        * 
        * 전문코드	TC		CHAR	07		
        * 발생일자	Date	CHAR	10		YYYY-MM-DD
        * 발생시간	Time	CHAR	08		HH-MM-SS
        * 전문구분	Form	CHAR	01		I  : Initialize, U : Update, D : Delete,   R : Re-request
        * 전문길이	Message_Length	CHAR	04		
        * 야드구분	Yard_Id		CHAR	01		
        * 동구분		Bay_GP		CHAR	01		
        * 설비종류	Equip_Kind	CHAR	02		
        * 설비번호	Equip_No	CHAR	02		
        * 시스템 MODE	System_Mode	CHAR	01		1:Off Line, 0:On Line
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */      
	public boolean bsyCRDriOnOffResult(String sMessage) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
        logger.println(LogLevel.DEBUG, this, "### BSY_CRANE 운전 ModeOn/Off 실적 처리");
	    try {
	        JDTORecord parseData = new Level2Parser().parse(sMessage);
	        logger.println(LogLevel.DEBUG, this, parseData);	        
	        crDriOnOffResult(parseData);
	    }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
	    return true;
	}

	/**
	 * 오퍼레이션명 : 
	 *
	 * 야드 Level-2로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * 1.TC_CD	: HM1PB03//HM1PB53
        * 2.I/F ID	: YM-BIF-020.
        * 
        * 전문코드	TC		CHAR	07		
        * 발생일자	Date	CHAR	10		YYYY-MM-DD
        * 발생시간	Time	CHAR	08		HH-MM-SS
        * 전문구분	Form	CHAR	01		I  : Initialize, U : Update, D : Delete,   R : Re-request
        * 전문길이	Message_Length	CHAR	04		
        * 야드구분	Yard_Id		CHAR	01		
        * 동구분		Bay_GP		CHAR	01		
        * 설비종류	Equip_Kind	CHAR	02		
        * 설비번호	Equip_No	CHAR	02		
        * 시스템 MODE	System_Mode	CHAR	01		1:Off Line, 0:On Line
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */      
	public boolean asyCRDriOnOffResult(String sMessage) {
        logger.println(LogLevel.DEBUG, this, "### ASY_CRANE 운전 ModeOn/Off 실적 처리");
	    try {
	    	
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
	        JDTORecord parseData = new Level2Parser().parse(sMessage);
	        logger.println(LogLevel.DEBUG, this, parseData);	        
	        crDriOnOffResult(parseData);
	    }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
	    return true;
	}
	
       /**
	 * 오퍼레이션명 : 
	 *
	 * 야드 Level-2로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * 1.TC_CD	: CN1PB03.
        * 2.I/F ID	: YM-BIF-003.
        * 
        * 전문코드	TC		CHAR	07		
        * 발생일자	Date	CHAR	10		YYYY-MM-DD
        * 발생시간	Time	CHAR	08		HH-MM-SS
        * 전문구분	Form	CHAR	01		I  : Initialize, U : Update,D : Delete,   R : Re-request
        * 전문길이	Message_Length	CHAR	04		
        * 야드구분	Yard_Id		CHAR	01		
        * 동구분		Bay_GP		CHAR	01		
        * 설비종류	Equip_Kind	CHAR	02		
        * 설비번호	Equip_No	CHAR	02		
        * 시스템 MODE	System_Mode	CHAR	01		0:Off Line, 1:On Line
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */      
	public boolean bcyCRDriOnOffResult(String sMessage) {
        logger.println(LogLevel.DEBUG, this, "### BCY_CRANE 운전 ModeOn/Off 실적 처리");
	    try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
            JDTORecord parseData = new Level2Parser().parse(sMessage);
            logger.println(LogLevel.DEBUG, this, parseData);
	        crDriOnOffResult(parseData);
	    }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
	    return true;
	}

	/**
	 * 크레인 운전모드 on/off 실적을 처리한다.
	 * @param parseData		수신정보
	 * @throws Exception
	 */
    private void crDriOnOffResult(JDTORecord parseData) throws Exception {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        /**
         * Message Parsing 
         */
        Map tc = ymCommonDAO.readColumnLenOfTc(getField(parseData, "전문코드"));
        /**
         * valid check
         */
        validRecDataOfCROnOff(parseData, tc);
        /**
         * 수신 설비가 대차이면 고장 처리하고 리턴한다.
         */
        String equipKind= getField(parseData, "설비종류");
        String systemMode = getField(parseData, "시스템MODE");
        String equipStat = "O";
        if("1".equals(systemMode)) {
            equipStat = "C";
        }           
        //자리비움 추가 :2013.05.02
        if("2".equals(systemMode)) {
            equipStat = "E";
        }
        if(notCREquip(equipKind)) {
            logger.println(LogLevel.DEBUG, this, "설비가 크레인이 아님으로 상태만 UPDATE 처리.");
            ymCommonDAO.modifyModeOfEquip(equipStat, getEquipKey(parseData));                
            
            /*
             *	운전모드를 ON 상태로 만드는 경우.
             *  B열연 대차인 경우 설비상태도 '정상'으로 만든다.
             */
            if("TC".equals(equipKind)&&
                "O".equals(equipStat)){
	            ymCommonDAO.modifyEquipStatOfEquip(YmCommonConst.EQUIP_STAT_O, getEquipKey(parseData));                
	        }
	        
            return;
        }
        /**
         * 설비정보를 가져온다.
         */
        JDTORecord equip = ymCommonDAO.readEquipInfo(getEquipKey(parseData)); 
        /**
         * 수신한 '시스템 MODE'로 설비 테이블에 설비상태를 Update 한다.
         */            
        editCRDriveInfo(equip, systemMode, "2");
    }
    
    /**
     * @param equipKind
     */
    private boolean notCREquip(String equipKind) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
        if("TC".equals(equipKind)) {
            return true;
        }else if("WB".equals(equipKind)) {
            return true;
        }else if("SC".equals(equipKind)) {
            return true;    
        }
        return false;
    }

    /**
     * 수신항목 '야드구분', '동구분', '설비종류', '설비번호', '시스템MODE'를 체크한다.
     * @param parseData	수신정보
     * @param tc		송/수신항목의 LENGTH 정보
     * @return
     */
    private void validRecDataOfCROnOff(JDTORecord parseData, Map tc) throws Exception {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        String ydGp 	= getFieldNvl(parseData, "야드구분");
        String bayGp 	= getFieldNvl(parseData, "동구분");
        String equipKind= getFieldNvl(parseData, "설비종류");
        String equipNo 	= getFieldNvl(parseData, "설비번호");
        String systemMode = getFieldNvl(parseData, "시스템MODE");        
        if(ydGp.length() != getFieldLen(tc, "야드구분")) {
            throw new Exception("수신항목 '야드구분' Error: "+ ydGp);
        }else if(bayGp.length() != getFieldLen(tc, "동구분")) {
            throw new Exception("수신항목 '동구분' Error: "+ bayGp);
        }else if(equipKind.length() != getFieldLen(tc, "설비종류")) {
            throw new Exception("수신항목 '설비종류' Error: "+ equipKind);
        }else if(equipNo.length() != getFieldLen(tc, "설비번호")) {
            throw new Exception("수신항목 '설비번호' Error: "+ equipNo);
        }else if(systemMode.length() != getFieldLen(tc, "시스템MODE")) {
            throw new Exception("수신항목 '시스템 MODE' Error: "+ systemMode);
        }
    }

    /**
     * 설비구분을 리턴한다.
     * @param parseData	수신정보
     * @return
     */
    private String getEquipKey(JDTORecord parseData) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        String ydGp 		= getFieldNvl(parseData, "야드구분");
        String bayGp 		= getFieldNvl(parseData, "동구분");
        String equipKind 	= getFieldNvl(parseData, "설비종류");
        String equipNo 		= getFieldNvl(parseData, "설비번호");
        StringBuffer buffer = new StringBuffer();
        buffer.append(ydGp);
        if("TC".equals(equipKind) ||
                "SC".equals(equipKind)) {
            buffer.append("X");    
        }else {
            buffer.append(bayGp);
        }
        buffer.append(equipKind);
        buffer.append(equipNo);
        return buffer.toString();
    }

    /**
     * 크레인 운전모드에 따른 On/OffLine 처리한다. CN1PB03
     * @param equip		설비정보
     * @param equipGp	설비구분
     * @param mode		시스템모드
     * @param gp		운전/시스템모드 구분
     */
    private void editCRDriveInfo(JDTORecord equip, String mode, String gp) {
    	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return ;
		}
		
        List base 	= null;
        List alter 	= null;
        String yd		= getField(equip, "YD_GP");
        String bay		= getField(equip, "BAY_GP");
        String equipGp	= getField(equip, "EQUIP_GP");
        String equipNo	= getField(equip, "EQUIP_NO");
        String alEquipNo= getField(equip, "BACKUP_EQUIP_NO");
        String alEquipGp= yd + bay +"CR"+ alEquipNo;
	    String hmiMode 	= getField(equip, "HMI_STAT");
	    String equipStat= getField(equip, "EQUIP_STAT");
	    JDTORecord dto	= ymCommonDAO.readEquipInfo(alEquipGp);
	    String alStat 		= null;
	    String alWorkMode	= null;
	    String alHMIMode	= null;
	    if(dto != null) {
	        alStat 		= getField(dto, "EQUIP_STAT");    
	        alWorkMode	= getField(dto, "WORK_MODE");
	        alHMIMode	= getField(dto, "HMI_STAT");
	    }
        if(isModeOff(equip, mode, gp) || YmCommonConst.MODE_2.equals(mode)) {
		    logger.println(LogLevel.DEBUG, this, "CN1PB03 크레인 설비 OffLine 처리.");
	 		    
		    if(YmCommonConst.MODE_2.equals(mode)){
		    	ymCommonDAO.modifyModeOfEquip(YmCommonConst.TRO_REC_E, equipGp);
		    }else{
		    	ymCommonDAO.modifyModeOfEquip(YmCommonConst.TRO_REC_C, equipGp);
		    	
		    	/**
			     * 크레인의 고장/복구,운전모드,system모드가 정상일 경우만 처리
			     */
			    if("O".equals(equipStat)) {
				    /**
				     * 스케쥴정보를 읽은 후 스케쥴기준 정보를 처리한다.
				     */
			        if("".equals(alEquipNo)) {
				        base = ymCommonDAO.readTroRecSch(yd, bay, equipNo, 
				                YmCommonConst.SCH_RULE_ACTIVE_STAT_A);
				        editSchRule(yd, bay, equipNo, alEquipNo,
				                YmCommonConst.SCH_RULE_ACTIVE_STAT_A,
				                YmCommonConst.SCH_RULE_STAT_X);
			        }else {
				        base = ymCommonDAO.readTroRecSch(yd, bay, equipNo, 
				                YmCommonConst.SCH_RULE_ACTIVE_STAT_A);
				        editSchRule(yd, bay, equipNo, alEquipNo,
				                YmCommonConst.SCH_RULE_ACTIVE_STAT_A,
				                YmCommonConst.SCH_RULE_ACTIVE_STAT_B);
				        editTroRecOfSch(base, alEquipNo, 1);
				        /**
				         * 대체 스케쥴기준을 처리한다.
				         */
				        alter = ymCommonDAO.readTroRecSch(yd, bay, alEquipNo, 
				                YmCommonConst.SCH_RULE_ACTIVE_STAT_A);
				        editSchRule(yd, bay, alEquipNo, alEquipNo,
				                YmCommonConst.SCH_RULE_ACTIVE_STAT_A,
				                YmCommonConst.SCH_RULE_ACTIVE_STAT_B);
				        editTroRecOfSch(base, alEquipNo, 1);
			        }
			    }
		    }
		    
		    
	        /**
	         * 고장 크레인의 초기 크레인 정보를 송신한다. 
	         */
	        considerSendCRIniInfo(yd, equipGp);
	        
		}else if(isModeOn(equip, mode, gp)) {
		    logger.println(LogLevel.DEBUG, this, "CN1PB03 크레인 설비 OnLine 처리.");
		    ymCommonDAO.modifyModeOfEquip(YmCommonConst.TRO_REC_O, equipGp);
		    /**
		     * 크레인의 고장/복구,운전모드,system모드가 정상일 경우만 처리
		     */
		    if("O".equals(equipStat)) {
		        if("".equals(alEquipNo)) {
				    base = ymCommonDAO.readTroRecSch(
				            yd, bay, equipNo, YmCommonConst.SCH_RULE_STAT_X);
			        editSchRule(yd, bay, equipNo, equipNo,
			                YmCommonConst.SCH_RULE_STAT_X,
			                YmCommonConst.SCH_RULE_ACTIVE_STAT_A);	            
		        }else {	            
		            if(YmCommonConst.WORK_MODE_C.equals(alStat)) {
		                //스케쥴을 복구크레인에 할당한다.
					    base = ymCommonDAO.readTroRecSch(
					            yd, bay, equipNo, YmCommonConst.SCH_RULE_ACTIVE_STAT_B);
				        editTroRecOfSch(base, equipNo, 3);	            
					    base = ymCommonDAO.readTroRecSch(
					            yd, bay, alEquipNo, YmCommonConst.SCH_RULE_ACTIVE_STAT_B);
				        editTroRecOfSch(base, equipNo, 3);	            
		                //복구크레인의 대체가 고장이므로 크레인기준을 대체크레인의 고장상태로 셋팅한다.
				        editSchRule(yd, bay, alEquipNo, equipNo,
				                YmCommonConst.SCH_RULE_ACTIVE_STAT_B,
				                YmCommonConst.SCH_RULE_ACTIVE_STAT_B);            
				        editSchRule(yd, bay, equipNo, equipNo,
				                YmCommonConst.SCH_RULE_ACTIVE_STAT_B,
				                YmCommonConst.SCH_RULE_ACTIVE_STAT_B);
		            }else {
					    base = ymCommonDAO.readTroRecSch(
					            yd, bay, equipNo, YmCommonConst.SCH_RULE_ACTIVE_STAT_B);
				        editSchRule(yd, bay, equipNo, alEquipNo,
				                YmCommonConst.SCH_RULE_ACTIVE_STAT_B,
				                YmCommonConst.SCH_RULE_ACTIVE_STAT_A);
				        editTroRecOfSch(base, equipNo, 2);	            
				        /**
				         * 대체 스케쥴기준을 처리한다. 
				         */
				        editSchRule(yd, bay, alEquipNo, equipNo,	                
				                YmCommonConst.SCH_RULE_ACTIVE_STAT_B,
				                YmCommonConst.SCH_RULE_ACTIVE_STAT_A);	                
		            }
		        }
			    /**
			     * 복구일 경우 복구 크레인의 설비 작업상태를 IDLE로 하고 크레인 작업지시를 CALL
			     */
			    callCRWorkOrder(yd, bay, equipNo);
		    }else {
		        /**
		         * 고장 크레인의 초기 크레인 정보를 송신한다. 
		         */
		        considerSendCRIniInfo(yd, equipGp);
		    }
		}else {
		    logger.println(LogLevel.ERROR, this, "### CN1PB03 수신항목의 '운전 모드' ERROR.");
		}    
    }
    
    /**
     * 크레인 운전모드에 따른 On/OffLine 처리한다. 
     * @param equip		설비정보
     * @param equipGp	설비구분
     * @param mode		시스템모드
     * @param gp		운전/시스템모드 구분
     */
    private void editACRDriveInfo(JDTORecord equip, String mode, String gp) {
    	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return ;
		}
		
        List base 	= null;
        List alter 	= null;
        String yd		= getField(equip, "YD_GP");
        String bay		= getField(equip, "BAY_GP");
        String equipGp	= getField(equip, "EQUIP_GP");
        String equipNo	= getField(equip, "EQUIP_NO");
        String alEquipNo= getField(equip, "BACKUP_EQUIP_NO");
        String alEquipGp= yd + bay +"CR"+ alEquipNo;
        if(isModeOff(equip, mode, gp) || YmCommonConst.MODE_2.equals(mode)) {
		    logger.println(LogLevel.DEBUG, this, "크레인 설비 OffLine 처리.");
		    if(YmCommonConst.MODE_2.equals(mode)){
		    	ymCommonDAO.modifyModeOfEquip(YmCommonConst.TRO_REC_E, equipGp);
		    }else{
		    	ymCommonDAO.modifyModeOfEquip(YmCommonConst.TRO_REC_C, equipGp);
		    }
		    /**
		     * 스케쥴정보를 읽은 후 스케쥴기준 정보를 처리한다.
		     */
	        if("".equals(alEquipNo)) {
		        base = ymCommonDAO.readTroRecSch(yd, bay, equipNo, 
		                YmCommonConst.SCH_RULE_ACTIVE_STAT_A);
		        editSchRule(yd, bay, equipNo, alEquipNo,
		                YmCommonConst.SCH_RULE_ACTIVE_STAT_A,
		                YmCommonConst.SCH_RULE_STAT_X);
	        }else {
		        base = ymCommonDAO.readTroRecSch(yd, bay, equipNo, 
		                YmCommonConst.SCH_RULE_ACTIVE_STAT_A);
		        editSchRule(yd, bay, equipNo, alEquipNo,
		                YmCommonConst.SCH_RULE_ACTIVE_STAT_A,
		                YmCommonConst.SCH_RULE_ACTIVE_STAT_B);
		        editTroRecOfSch(base, alEquipNo, 1);
		        /**
		         * 고장 크레인의 작업을 취소한다.
		         */
		        considerCRWorkCancel(base, yd);
		        /**
		         * 대체 스케쥴기준을 처리한다.
		         */
		        base = ymCommonDAO.readTroRecSch(yd, bay, alEquipNo, 
		                YmCommonConst.SCH_RULE_ACTIVE_STAT_A);
		        editSchRule(yd, bay, alEquipNo, alEquipNo,
		                YmCommonConst.SCH_RULE_ACTIVE_STAT_A,
		                YmCommonConst.SCH_RULE_ACTIVE_STAT_B);
		        editTroRecOfSch(base, alEquipNo, 1);
	        }
	        /**
	         * 고장 크레인의 초기 크레인 정보를 송신한다. 
	         */
	        considerSendCRIniInfo(yd, equipGp);
	        /**
		     * 대체 크레인의 설비 '작업상태'가 IDLE일 경우에만 대체 크레인 작업지시를 CALL
	         * --대체가 없는 경우 SKIP
	         */
	        if(! "".equals(alEquipNo)) {
	            considerCRWorkOrder(yd, bay, equipGp, alEquipNo);
	        }
		}else if(isModeOn(equip, mode, gp)) {
		    logger.println(LogLevel.DEBUG, this, "크레인 설비 OnLine 처리.");
		    /**
		     * 크레인의 설비상태가
		     * -고장이면 작업모드 OFF LINE[0]
		     * -정상이면 작업모드 ON LINE[1]
		     */
		    String equipStat = getField(equip, "EQUIP_STAT");
		    if(YmCommonConst.EQUIP_STAT_O.equals(equipStat)) {
			    ymCommonDAO.modifyModeOfEquip(YmCommonConst.TRO_REC_O, equipGp);
			    JDTORecord dto	= ymCommonDAO.readEquipInfo(alEquipGp);
			    String alStat	= getField(dto, "EQUIP_STAT");
		        if("".equals(alEquipNo)) {
				    base = ymCommonDAO.readTroRecSch(yd, bay, equipNo, YmCommonConst.SCH_RULE_STAT_X);
			        editSchRule(yd, 
				        		bay, 
				        		equipNo,
				        		equipNo,
				                YmCommonConst.SCH_RULE_STAT_X,
				                YmCommonConst.SCH_RULE_ACTIVE_STAT_A);	            
		        }else {	            
		            if(YmCommonConst.EQUIP_STAT_C.equals(alStat)) {
		                //스케쥴을 복구크레인에 할당한다.
					    base = ymCommonDAO.readTroRecSch(yd, bay, equipNo, YmCommonConst.SCH_RULE_ACTIVE_STAT_B);
				        editTroRecOfSch(base, equipNo, 3);
				        
					    base = ymCommonDAO.readTroRecSch(yd, bay, alEquipNo, YmCommonConst.SCH_RULE_ACTIVE_STAT_B);
				        editTroRecOfSch(base, equipNo, 3);	            
		                //복구크레인의 대체가 고장이므로 크레인기준을 대체크레인의 고장상태로 셋팅한다.
				        editSchRule(yd, bay, alEquipNo, equipNo,YmCommonConst.SCH_RULE_ACTIVE_STAT_B,YmCommonConst.SCH_RULE_ACTIVE_STAT_B);            
				        editSchRule(yd, bay, equipNo, equipNo,
				                YmCommonConst.SCH_RULE_ACTIVE_STAT_B,
				                YmCommonConst.SCH_RULE_ACTIVE_STAT_B);
		            }else {
					    base = ymCommonDAO.readTroRecSch(yd, bay, equipNo, YmCommonConst.SCH_RULE_ACTIVE_STAT_B);
				        editSchRule(yd, 
					        		bay, 
					        		equipNo, 
					        		alEquipNo,
					                YmCommonConst.SCH_RULE_ACTIVE_STAT_B,
					                YmCommonConst.SCH_RULE_ACTIVE_STAT_A);
				        editTroRecOfSch(base, equipNo, 2);	            
				        /**
				         * 대체 스케쥴기준을 처리한다. 
				         */
				        editSchRule(yd, 
					        		bay, 
					        		alEquipNo, 
					        		equipNo,	                
					                YmCommonConst.SCH_RULE_ACTIVE_STAT_B,
					                YmCommonConst.SCH_RULE_ACTIVE_STAT_A);	                
		            }
		        }
		        /**
		         * 복구 크레인의 초기 크레인 정보를 송신한다. 
		         */
		        considerSendCRIniInfo(yd, equipGp);
		        /**
		         * 대체 크레인의 설비 작업상태를 IDLE로 하고 크레인 작업지시를 CALL
		         * --대체가 없는 경우 SKIP
		         */
		        if((! "".equals(alEquipNo) 
		        	&& (! YmCommonConst.EQUIP_STAT_C.equals(alStat)))) {
		            callCRWorkOrder(yd, bay, alEquipNo);
		        }
			    /**
			     * 복구일 경우 복구 크레인의 설비 작업상태를 IDLE로 하고 크레인 작업지시를 CALL
			     */
			    callCRWorkOrder(yd, bay, equipNo);
		    }else if(YmCommonConst.EQUIP_STAT_C.equals(equipStat)) {
			    ymCommonDAO.modifyModeOfEquip(YmCommonConst.TRO_REC_C, equipGp);		        
		    }
		}else {
		    logger.println(LogLevel.ERROR, this, "### 수신항목의 '운전 모드' ERROR.");
		}    
    }

    /**
     * '운전모드'가 '1'이고 설비 테이블의 '작업모드가'가 'O'이면 true를 리턴한다.
     * @param equip		설비정보
     * @param mode		운전모드
     * @param gp		운전/시스템모드 구분
     * @return true, false
     */
    private boolean isModeOff(JDTORecord equip, String mode, String gp) {
    	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false ;
		}
		
        String yd = getField(equip, "EQUIP_GP").substring(0, 1);
        if(YmCommonConst.YD_GP_1.equals(yd)) {
        	if(YmCommonConst.MODE_0.equals(mode) && 
            		YmCommonConst.MODE_O.equals(getModeValue(equip, gp))){
	            return YmCommonConst.MODE_0.equals(mode) && 
	            		YmCommonConst.MODE_O.equals(getModeValue(equip, gp));
        	}else{
        		return YmCommonConst.MODE_0.equals(mode) && 
        				YmCommonConst.MODE_E.equals(getModeValue(equip, gp));
        	}
        }else {
        	if(YmCommonConst.MODE_1.equals(mode) && 
        			YmCommonConst.MODE_O.equals(getModeValue(equip, gp))){
	            return YmCommonConst.MODE_1.equals(mode) && 
	        			YmCommonConst.MODE_O.equals(getModeValue(equip, gp));  
        	}else{
        		return YmCommonConst.MODE_1.equals(mode) && 
    					YmCommonConst.MODE_E.equals(getModeValue(equip, gp));
        	}
        }
    }
    
    /**
     * '운전모드'가 '0'이고 설비 테이블의 '설비상태'가 'C'이면 true를 리턴한다.
     * @param equip		설비정보
     * @param mode		운전모드
     * @return true, false
     */
    private boolean isModeOn(JDTORecord equip, String mode, String gp) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false ;
		}
		
		
        String yd = getField(equip, "EQUIP_GP").substring(0, 1);
        if(YmCommonConst.YD_GP_1.equals(yd)) {
        	if(YmCommonConst.MODE_1.equals(mode) && 
    				YmCommonConst.MODE_C.equals(getModeValue(equip, gp))){
	            return YmCommonConst.MODE_1.equals(mode) && 
	    				YmCommonConst.MODE_C.equals(getModeValue(equip, gp));
        	}else{
        		return YmCommonConst.MODE_1.equals(mode) && 
						YmCommonConst.MODE_E.equals(getModeValue(equip, gp));
        	}
        }else {
        	if(YmCommonConst.MODE_0.equals(mode) && 
    				YmCommonConst.MODE_C.equals(getModeValue(equip, gp))){
	            return YmCommonConst.MODE_0.equals(mode) && 
	    				YmCommonConst.MODE_C.equals(getModeValue(equip, gp));
        	}else{
        		 return YmCommonConst.MODE_0.equals(mode) && 
 						YmCommonConst.MODE_E.equals(getModeValue(equip, gp));
        	}
        }
    }

    /**
     * 운전/시스템모드에 따른 값을 리턴한다.
     * @param equip	설비정보
     * @param gp	운전/시스템모드 구분
     * @return
     */
    private String getModeValue(JDTORecord equip, String gp) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null ;
		}
    	
        if("1".equals(gp)) {
            return getField(equip, "HMI_STAT");
        }else if("2".equals(gp)) {
            return getField(equip, "WORK_MODE");
        }else if("3".equals(gp)) {
            return getField(equip, "HMI_STAT");
        }
        return null;
    }

    /**
     * A열연일 경우 고장 크레인의 작업을 취소한다.
     * @param base	고장 크레인 스케쥴 정보
     * @param yd	야드구분
     */
    private void considerCRWorkCancel(List base, String yd) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        int baseCnt = base != null ? base.size() : 0;
        if(baseCnt == 0) {
            return;
        }
        if(YmCommonConst.YD_GP_1.equals(yd)) {
	        callCRWorkCancel(base, base.size());	            
        }
    }

    /**
     * @param map
     */
    private void callCRIniInfo(Map tc, String tcCd, String crNo) {
        EJBConnector ejbConn = null;
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
			
	        String methodName = "";
	        if(YmCommonConst.TC_CN1BP01.equals(tcCd)) {
	            methodName = "callBCoilCraneMsgInfo";
	        }else {
	            methodName = "callBSlabCraneMsgInfo";
	        }
			ejbConn = new EJBConnector("default","JNDICWrkOrdReg",this);
            ejbConn.trx(methodName, new Class[]{ String.class }, new Object[]{ crNo });
		}catch (Exception e) {
            e.printStackTrace();
        }	        
    }

    /**
     * 대체 크레인의 설비 '작업상태'가 IDLE일 경우에만 대체 크레인 작업지시를 CALL
     * @param yd		야드구분
     * @param bay		동구분
     * @param equipGp	기준 크레인 설비구분
     * @param alEquipNo	대체 크레인 설비번호
     */
    private void considerCRWorkOrder(String yd, String bay, String equipGp, String alEquipNo) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return  ;
		}
		
	    equipGp 			= equipGp.substring(0, 4) + alEquipNo;
	    JDTORecord equip 	= ymCommonDAO.readEquipInfo(equipGp);		    
	    if(YmCommonConst.WPROG_STAT_W.equals(getField(equip, "WPROG_STAT"))) {
		    callCRWorkOrder(yd, bay, alEquipNo);
	    }
    }

    /**
     * 스케쥴기준 정보를 백업 처리 한다.
     * @param schs	고장 크레인 스케쥴기준 정보
     * @param equipNo	설비번호
     */
    private void editSchRule(String yd, String bay, String crNo, String alCrNo, String stat, String alStat) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return  ;
		}
		
        List base 		= ymCommonDAO.readTroRecSchRule(yd, bay, crNo, stat);
        int baseCnt 	= base != null ? base.size() : 0;
        for(int i = 0; i < baseCnt; i++) {
            ymCommonDAO.modifyAlterNoAndActiveStatOfSchRule(alCrNo, 
										                    alStat, 
										                    getField((JDTORecord)base.get(i), "SCH_RULE_ID"));
        }
    }

    /**
     * ON/OFF인 크레인 스케쥴 정보를 백업한다.
     * -작업상태가 UP지시일 경우 스케쥴 작업상태를 IDEL로 한다.
     * -작업상태가 PUT지시일 경우 스케쥴을 대체로 할당하는 것을  SKIP 한다.
     * @param lst			스케쥴기준/대체정보
     * @param equipNo		설비번호
     * @param gp			조건설비번호
     */
    private void editTroRecOfSch(List lst, String equipNo, int gp) {
    	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return  ;
		}
		
        JDTORecord data 	= null;
        int lstCnt 			= lst != null ? lst.size() : 0;
        String ydGp 		= null;
        String priority 	= null;
        for(int i = 0; i < lstCnt; i++) {
            data = (JDTORecord)lst.get(i); 
            ydGp = getField(data, "YD_GP");
            if(notPutOrder(getField(data, "SCH_WORK_STAT"))) {
                editSch(data, equipNo, gp);
//            if(YmCommonConst.YD_GP_1.equals(ydGp) &&
//                    notPutOrder(getField(data, "SCH_WORK_STAT"))) {
//                editSch(data, equipNo, gp);
            }
//            else {
//                if(YmCommonConst.SCH_WORK_STAT_S.equals(
//                        getField(data, "SCH_WORK_STAT"))) {
//                    editSch(data, equipNo, gp);    
//                }
//            }
        }
    }
    
    /**
     * @param data
     * @param equipNo
     * @param gp
     */
    private void editSch(JDTORecord data, String equipNo, int gp) {
    	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return  ;
		}
		
        String priority = null;
        if(gp == 1) {
            priority = getField(data, "SCH_RULE_ALTER_WPREFER");
        }else if(gp == 2) {
            priority = getField(data, "SCH_RULE_WPREFER");
        }else if(gp == 3) {
            priority = getField(data, "SCH_WPREFER");
        }
        ymCommonDAO.modifyTroRecOfSchedule(
                equipNo, 
                YmCommonConst.SCH_WORK_STAT_S, 
                priority, 
                getField(data, "SCH_ID"));
    }
    
    /**
     * 크레인이 PUT 상태인지 리턴한다.
     * @param workStat	크레인 작업상태
     * @return
     */
    private boolean notPutOrder(String workStat) {
    	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
        if(YmCommonConst.SCH_WORK_STAT_2.equals(workStat)) {
            return false;
        }else if(YmCommonConst.SCH_WORK_STAT_3.equals(workStat)) {            
            return false;
        }else {
            return true;
        }
    }

    /**
     * 고장 크레인 작업을 취소한다.
     * @param schs	스케쥴정보
     */
    private void callCRWorkCancel(List schs, int schsCnt) {
        JDTORecord dto 			= null;
        EJBConnector ejbConn 	= null;
		try {
			
			
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
			
			ejbConn = new EJBConnector("default","JNDICWrkOrdReg",this);
			for(int i = 0; i < schsCnt; i++) {
			    dto = (JDTORecord)schs.get(i);
			    if(YmCommonConst.SCH_WORK_STAT_1.equals(getField(dto, "SCH_WORK_STAT"))) {
		            ejbConn.trx(
		                    "callACoilCraneMsgInfo",
		                    new Class[]{ String.class, String.class },
		            		new Object[]{ YmCommonConst.TC_THHC120, getField(dto, "SCH_ID") });
		            break;
			    }
			}
        }catch (Exception e) {
            throw new EJBServiceException(e);
        }
    }

    /**
     * CRANE 작업지시를 CALL
     * @param equipGp
     * @throws Exception
     */
    private void callCRWorkOrder(String yd, String bay, String equipNo) {
    	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        List lst 		= ymCommonDAO.readSchInfo(yd, bay, equipNo);
        JDTORecord dto 	= null;
        int lstCnt 		= lst != null ? lst.size() : 0;
        
        /*
         *	YJK.	2006.07.14	
         *	복구처리시 반대크레인의 작업상태를 작업대기('W')로 수정
         */
        {
	        int iSeq = dao.updateEquipTcInfo(YmCommonConst.STACK_STAT_I,
	        								 YmCommonConst.WORK_PROG_STAT_W,
	 							   			 yd+bay+"CR"+equipNo); 
	    	
	    	logger.println(LogLevel.DEBUG, this, "크레인 작업상태 대기모드로 수정="+yd+bay+"CR"+equipNo);
		}
 							   			 
        if(lstCnt > 0) {
            dto = (JDTORecord)lst.get(0);
	        crWorkOrder(
	                getCRWorkOrderTc(yd),
	                yd,
	                bay,
	                YmCommonConst.EQUIP_KIND_CR,
	                equipNo,
	                getField(dto, "SCH_WORK_KIND"),
	                getField(dto, "WBOOK_ID"));            
        }else {
            if(! YmCommonConst.YD_GP_1.equals(yd)) {
                considerSendCRIniInfo(yd, yd+bay+"CR"+equipNo);
            }
        }
    }
    
    /**
     * @param ydGp
     * @return
     */
    private String getCRWorkOrderTc(String ydGp) {
    	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        if(YmCommonConst.YD_GP_1.equals(ydGp)) {
            return YmCommonConst.TC_THCH520;
        }else if(YmCommonConst.YD_GP_2.equals(ydGp)) {
            return YmCommonConst.TC_CM1PB02;
        }else if(YmCommonConst.YD_GP_3.equals(ydGp)) {
            return YmCommonConst.TC_CN1PB02;
        //A열연 SLAB야드 추가(MCH)
        }else if(YmCommonConst.YD_GP_0.equals(ydGp)) {
            return YmCommonConst.TC_HM1PB02;
        }
        return "";
    }

	/**
	 * 오퍼레이션명 : 
	 *
	 * CRANE 작업지시를 CALL
        * param tc	전문코드
        * param yd	야드구분
        * param bay	동구분
        * param cr	설비종류
        * param crNo	크레인번호
        * param kind	스케쥴작업종류
        * param id	작업예약ID
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */       
    public void crWorkOrder(
            String tc, String yd, String bay, String cr, String crNo, String kind, String id) {
        EJBConnector ejbConn = null;
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
			
			ejbConn = new EJBConnector("default","JNDICWrkOrdReg",this);
            ejbConn.trx(
                    "callCraneSchInfo",
                    new  Class[]{
                            String.class,
            				String.class,
            				String.class,
            				String.class,
            				String.class,
            				String.class,
            				String.class },
            		new Object[]{tc, yd, bay, cr, crNo, kind, id });
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

	/**
	 * 오퍼레이션명 : 
	 *
	 * 야드 Level-2로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * 1.TC_CD	: CM1PB01.
        * 2.I/F ID	: YM-BIF-018.
        * 
        * 전문코드	TC		CHAR	07		
        * 발생일자	Date	CHAR	10		YYYY-MM-DD
        * 발생시간	Time	CHAR	08		HH-MM-SS
        * 전문구분	Form	CHAR	01		I  : Initialize, U : Update, D : Delete,   R : Re-request
        * 전문길이	Message_Length	CHAR	04		
        * 야드구분	Yard_Id			CHAR	01		
        * 동구분		Bay_GP			CHAR	01		
        * 설비종류	Equip_Kind		CHAR	02		
        * 설비번호	Equip_No		CHAR	02		
        * 시스템 MODE	System_Mode	CHAR	01		0:Off Line, 1:On Line
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */      
	public boolean bsyCRSysOnOffResult(String sMessage) {
        logger.println(LogLevel.DEBUG, this, "### BSY_CRANE 시스템 On/Off 실적 처리");
	    try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
	        JDTORecord parseData = new Level2Parser().parse(sMessage);
	        logger.println(LogLevel.DEBUG, this, parseData);
	        craneSystemOnOffResult(parseData);
	    }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
	    return true;
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 야드 Level-2로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * 1.TC_CD	: HM1PB01//HM1PB51
        * 2.I/F ID	: YM-BIF-022
        * A열연 SLAB 시스템 ON/OFF 정보(MCH)
        * 전문코드	TC				CHAR	07		
        * 발생일자	Date			CHAR	10		YYYY-MM-DD
        * 발생시간	Time			CHAR	08		HH-MM-SS
        * 전문구분	Form			CHAR	01		I  : Initialize, U : Update, D : Delete,   R : Re-request
        * 전문길이	Message_Length	CHAR	04		
        * 야드구분	Yard_Id			CHAR	01		
        * 동구분	Bay_GP			CHAR	01		
        * 설비종류	Equip_Kind		CHAR	02		
        * 설비번호	Equip_No		CHAR	02		
        * 시스템 MODE	System_Mode	CHAR	01		0:Off Line, 1:On Line
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */           
	public boolean asyCRSysOnOffResult(String sMessage) {
        logger.println(LogLevel.DEBUG, this, "### ASY_CRANE 시스템 On/Off 실적 처리");
	    try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
	        JDTORecord parseData = new Level2Parser().parse(sMessage);
	        logger.println(LogLevel.DEBUG, this, parseData);
	        craneSystemOnOffResult(parseData);
	    }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
	    return true;
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 야드 Level-2로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * 1.TC_CD	: CN1PB01.
        * 2.I/F ID	: YM-BIF-001.
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */           
	public boolean bcyCRSysOnOffResult(String sMessage) {
        logger.println(LogLevel.DEBUG, this, "### BCY_CRANE 시스템 On/Off 실적 처리");
	    try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
            JDTORecord parseData = new Level2Parser().parse(sMessage);
            logger.println(LogLevel.DEBUG, this, parseData);
	        craneSystemOnOffResult(parseData);
	    }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
	    return true;
	}
	
    /**
     * 크레인 시스템 on/off 실적처리를 한다.
     * @param parseData		수신정보
     * @throws Exception
     */
    private void craneSystemOnOffResult(JDTORecord parseData) throws Exception {
    	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        /**
         * Message Parsing
         */
        Map tc = ymCommonDAO.readColumnLenOfTc(getField(parseData, "전문코드"));
        /**
         * valid check
         */
        validRecDataOfCROnOff(parseData, tc);
        /**
         * 설비정보를 가져온다.
         */
        JDTORecord equip = ymCommonDAO.readEquipInfo(getEquipKey(parseData));        
        /**
         * 수신한 '시스템 MODE'로 설비 테이블에 설비상태를 Update 한다.
         */
        editCRSystemInfo(equip, getField(parseData, "시스템MODE"), "3");
    }
   
      /**
	 * 오퍼레이션명 : 
	 *
	 * 야드 Level-2로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * TC ID	: CM1PB03
        * I/F ID	: YM-BIF-031
        * 전문코드	TC				CHAR	07		
        * 발생일자	Date			CHAR	10		YYYY-MM-DD
        * 발생시간	Time			CHAR	08		HH-MM-SS
        * 전문구분	Form			CHAR	01		I  : Initialize, U : Update, D : Delete,   R : Re-request
        * 전문길이	Message_Length	CHAR	04		
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */           
	public boolean bsySysTimeReq(String sMessage) {
        logger.println(LogLevel.DEBUG, this, "### BSY_시스템 시각 동기화 요구 처리");
        try {
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return false;
    		}
    		
            /**
             * TC ID	: CM1PB03
             * I/F ID	: YM-BIF-031
             * 전문코드	TC				CHAR	07		
             * 발생일자	Date			CHAR	10		YYYY-MM-DD
             * 발생시간	Time			CHAR	08		HH-MM-SS
             * 전문구분	Form			CHAR	01		I  : Initialize, U : Update, D : Delete,   R : Re-request
             * 전문길이	Message_Length	CHAR	04		
             */
            StringBuffer sendMsg = new StringBuffer();
            sysTimeReq(YmCommonConst.TC_CM1BP03, sendMsg);
            sendQueue(YmCommonConst.TC_CM1BP03, sendMsg.toString());
        }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
	    return true;
	}

	/**
	 * 오퍼레이션명 : 
	 *
	 * 야드 Level-2로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * 1.TC_CD	: HM1PB08.//HM1PB58
        * 2.I/F ID	: (MCH)
        * 전문코드	TC				CHAR	07		
        * 발생일자	Date			CHAR	10		YYYY-MM-DD
        * 발생시간	Time			CHAR	08		HH-MM-SS
        * 전문구분	Form			CHAR	01		I  : Initialize, U : Update, D : Delete,   R : Re-request
        * 전문길이	Message_Length	CHAR	04		
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */           
	public boolean asySysTimeReq(String sMessage) {
        logger.println(LogLevel.DEBUG, this, "### ASY_시스템 시각 동기화 요구 처리");
        try {
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return false;
    		}
    		
        	JDTORecord parseData = new Level2Parser().parse(sMessage);
        	String TC_CD = "";

        	if(YmCommonConst.TC_HM1PB08.equals(parseData.getFieldString("전문코드"))){ //A열연 A_A SLAB 시각정보요구
        		TC_CD = YmCommonConst.TC_HM1BP03;
        	}else{        															  //A열연 A_B SLAB 시각정보요구
        		TC_CD = YmCommonConst.TC_HM1BP53;
        	}
            StringBuffer sendMsg = new StringBuffer();
            sysTimeReq(TC_CD, sendMsg);
            sendQueue(TC_CD, sendMsg.toString());
        }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
	    return true;
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                
	public boolean bcySysTimeReq(String sMessage) {
        logger.println(LogLevel.DEBUG, this, "### BCY_시스템 시각 동기화 요구 처리");
        try {
        	
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return false;
    		}
            /**
             * TC ID	: CN1BP03
             * I/F ID	: YM-BIF-015
             * 전문코드	TC				CHAR	07
             * 발생일자	Date			CHAR	10
             * 발생시간	Time			CHAR	08
             * 전문구분	Form			CHAR	01
             * 전문길이	Message_Length	CHAR	04
             */
            StringBuffer sendMsg = new StringBuffer();
            sysTimeReq(YmCommonConst.TC_CN1BP03, sendMsg);
            sendQueue(YmCommonConst.TC_CN1BP03, sendMsg.toString());
        }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
	    return true;
	}
	
	/**
	 * 공통적인 전문을 셋팅한다.
     * @param tcCode
     * @param sendMsg
     */
    private void sysTimeReq(String tcCode, StringBuffer sendMsg) {
    	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        sendMsg.append(tcCode); 						//전문코드
        sendMsg.append(YmCommonUtil.getStringYMD("-")); //발생일자
        sendMsg.append(YmCommonUtil.getStringHMS("-")); //발생시간
        sendMsg.append(YmCommonConst.FORM_I); 			//전문구분
        sendMsg.append("0000"); 						//전문길이
    }

	/**
	 * 오퍼레이션명 : 
	 *
	 * 야드 Level-2로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * 1.TC_CD	: THCH600.
        * 2.I/F ID	: YM-AIF-011.
        *
        * 전문코드		CHAR	07		
        * CRANE 번호	CHAR	04		
        * SPARE		CHAR	04	
        * 발생일			CHAR	06		YYMMDD
        * 발생시			CHAR	06		HHMMSS
        * YARD MAP KEY	CHAR	05		
        * SPARE		CHAR	118		
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
	public boolean acyYdMapIniReq(String msg) {
        logger.println(LogLevel.DEBUG, this, "### ACY_YARD MAP 초기 정보요구 처리");
	    try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
            /**
             * Message Parsing.
             */
            JDTORecord parseData = new Level2Parser().parse(msg);
            logger.println(LogLevel.DEBUG, this, parseData);            
            /**
             * 수신항목의 'YARD MAP KEY'를 가져온다.
             */
            String rYdMapKey = getField(parseData, "YARDMAPKEY");        
            if(rYdMapKey.length() == 0) {               
                throw new Exception("수신항목 'YARD MAP KEY' Error: "+ rYdMapKey);                
            }            
            /**
             * 야드 맵을 가져온다.
             */
            String ydMapKey = getYdMapKey(rYdMapKey);
            List ydMaps 	= ymCommonDAO.readYdMapInfo(ydMapKey);
            int ydMapsCnt 	= ydMaps != null ? ydMaps.size() : 0;
            /**
             * I/F ID	: YM-AIF-029
             * T/C		: THHC160		
		     * 01 전문 코드		CHAR	07		
		     * 02 KEY			CHAR	05		
		     * 03 INDEX KEY		CHAR	02		
		     * 04 BED ADDRESS	CHAR	08		10회 반복 시작
		     * 05 사용유무		CHAR	01		.
		     * 06 군정보			CHAR	01		.
		     * 07 코일번호		CHAR	10		.
		     * 08 제작번호/행번	CHAR	13		.
		     * 09 두께			CHAR	05		.
		     * 10 폭				CHAR	05		.
		     * 11 외경			CHAR	05		.
		     * 12 중량			CHAR	05		.
		     * 13 길이			CHAR	05		.
		     * 14 X 물리위치		CHAR	06		.
		     * 15 Y 물리위치		CHAR	06		10회 반복 
		     * 16 SPARE		CHAR	86		
             */
            int indexKey		= 0;
            JDTORecord ydMap 	= null;
            Map tc = ymCommonDAO.readColumnLenOfTc(YmCommonConst.TC_THHC160);
            StringBuffer sendMsg 	= new StringBuffer();
            StringBuffer keyBuffer 	= new StringBuffer();
            String totmsg = "";
            sendMsg.append(YmCommonConst.TC_THHC160);	//전문 코드
            appendMsg(sendMsg, keyBuffer.toString(), getFieldLen(tc, "KEY"));
           
            
            
            logger.println(LogLevel.DEBUG, "전문갯수   ==" +""+ (ydMapsCnt));
            
            if(ydMapsCnt > 0) {
                //열단위로 00-04까지 전송
                //00-10개 채운다.
                String col = null;
                
	            for(int i = 0; i < ydMapsCnt; i++) {
	                ydMap = (JDTORecord)ydMaps.get(i);	                
	                if(! getField(ydMap, "COL").equals(col)) {
	                    indexKey = 0;
	                    keyBuffer.setLength(0);
		                if(((i + 1) % 10) == 0) {
		                	logger.println(LogLevel.DEBUG, "메세지000000   ==" +""+ sendMsg.toString());
		                	totmsg = sendMsg.toString();
		                	appendMsg(sendMsg, "", 	714-totmsg.length());
		                	sendQueue(YmCommonConst.TC_THHC160, sendMsg.toString());
			                sendMsg.delete(12, sendMsg.length());
		                }

		                col = getField(ydMap, "COL");
		                if(ydMapKey.length() < 2) {
		                    keyBuffer.append("H").append(getField(ydMap, "BAY"))
		                    .append(getField(ydMap, "SPAN")).append(col);
		                }else if(ydMapKey.length() < 3) {
		                    keyBuffer.append("H").append(ydMapKey.substring(1,2))
		                    .append(getField(ydMap, "SPAN")).append(col);
		                }else if(ydMapKey.length() < 5) {
		                    keyBuffer.append("H").append(ydMapKey.substring(1,2))
		                    .append(ydMapKey.substring(2,4)).append(col);
		                }else {
		                    keyBuffer.append(ydMapKey);
		                }
		                
	                }
	                
	                logger.println(LogLevel.DEBUG, "메세지0   ==" +""+ sendMsg.toString());
	                logger.println(LogLevel.DEBUG, "i + 1   ==" +""+ (i ));
	                if((i  % 10) == 0) {
	                	appendMsgNum(sendMsg, ""+ (indexKey), getFieldLen(tc, "INDEXKEY"));
	                }
	                
	                appendMsg(sendMsg, getField(ydMap, "BED_ADDRESS"), 	getFieldLen(tc, "BEDADDRESS1"));
	                appendMsg(sendMsg, getField(ydMap, "USE_YN"), 		getFieldLen(tc, "사용유무1"));
	                appendMsg(sendMsg, "", 								getFieldLen(tc, "군정보1"));	                
	                appendMsg(sendMsg, getField(ydMap, "STOCK_ID"), 	getFieldLen(tc, "코일번호1"));
	                appendMsg(sendMsg, getField(ydMap, "PRODUCT_NO"), 	getFieldLen(tc, "제작번호행번1"));
	                String t = YmCommonUtil.deletePoint(""+ (getFieldFloat(ydMap, "COIL_T") * 1000));
	                String w = YmCommonUtil.deletePoint(getField(ydMap, "COIL_W"));
	                appendMsgNum(sendMsg, t,								getFieldLen(tc, "두께1"));
	                appendMsgNum(sendMsg, w, 								getFieldLen(tc, "폭1"));
	                appendMsgNum(sendMsg, getField(ydMap, "COIL_OUTDIA"), 	getFieldLen(tc, "외경1"));
	                appendMsgNum(sendMsg, getField(ydMap, "NET_WEIGH_WT"),	getFieldLen(tc, "중량1")); 
	                appendMsgNum(sendMsg, ""+ (getFieldInt(ydMap, "COIL_LEN") * 10), getFieldLen(tc, "길이1"));
	                appendMsgNum(sendMsg, getField(ydMap, "X_LOC"), 	getFieldLen(tc, "X물리위치1"));
	                appendMsgNum(sendMsg, getField(ydMap, "Y_LOC"), 	getFieldLen(tc, "Y물리위치1"));
	                
	                logger.println(LogLevel.DEBUG, "indexKey   ==" +""+ (indexKey));
	                logger.println(LogLevel.DEBUG, "BED_ADDRESS   ==" +""+ getField(ydMap, "BED_ADDRESS"));
	                logger.println(LogLevel.DEBUG, "BED_ADDRESS   ==" +""+ getField(ydMap, "BED_ADDRESS"));
	                logger.println(LogLevel.DEBUG, "USE_YN   ==" +""+ getField(ydMap, "USE_YN"));
	                logger.println(LogLevel.DEBUG, "STOCK_ID   ==" +""+ getField(ydMap, "STOCK_ID"));
	                logger.println(LogLevel.DEBUG, "PRODUCT_NO   ==" +""+ getField(ydMap, "PRODUCT_NO"));
	                logger.println(LogLevel.DEBUG, "COIL_OUTDIA   ==" +""+ getField(ydMap, "COIL_OUTDIA"));
	                logger.println(LogLevel.DEBUG, "NET_WEIGH_WT   ==" +""+ getField(ydMap, "NET_WEIGH_WT"));
	                logger.println(LogLevel.DEBUG, "COIL_LEN   ==" +""+ getField(ydMap, "COIL_LEN"));
	                logger.println(LogLevel.DEBUG, "X_LOC   ==" +""+ getField(ydMap, "X_LOC"));
	                logger.println(LogLevel.DEBUG, "Y_LOC   ==" +""+ getField(ydMap, "Y_LOC"));
	                      
	                
	                if((i == ydMapsCnt - 1)) {
	                	logger.println(LogLevel.DEBUG, "메세지1   ==" +""+ sendMsg.toString());
	                	totmsg = sendMsg.toString();
	                	appendMsg(sendMsg, "", 	714-totmsg.length());
	                	sendQueue(YmCommonConst.TC_THHC160, sendMsg.toString());
	                }else if(((i + 1) % 10) == 0) {
	                    ++indexKey;
	                    logger.println(LogLevel.DEBUG, "메세지2   ==" +""+ sendMsg.toString());
	                    totmsg = sendMsg.toString();
	                    appendMsg(sendMsg, "", 	714-totmsg.length());
	                    sendQueue(YmCommonConst.TC_THHC160, sendMsg.toString());
		                sendMsg.delete(12, sendMsg.length());
		                
	                }
	            }
            }else {
                appendMsg(sendMsg, rYdMapKey, getFieldLen(tc, "KEY"));
                appendMsg(sendMsg, "", getFieldLen(tc, "INDEXKEY"));
                appendMsg(sendMsg, "", getFieldLen(tc, "BEDADDRESS1"));
                appendMsg(sendMsg, "", getFieldLen(tc, "사용유무1"));
                appendMsg(sendMsg, "", getFieldLen(tc, "군정보1"));
                appendMsg(sendMsg, "", getFieldLen(tc, "코일번호1"));
                appendMsg(sendMsg, "", getFieldLen(tc, "제작번호행번1"));
                appendMsgNum(sendMsg, "", getFieldLen(tc, "두께1"));
                appendMsgNum(sendMsg, "", getFieldLen(tc, "폭1"));
                appendMsgNum(sendMsg, "", getFieldLen(tc, "외경1"));
                appendMsgNum(sendMsg, "", getFieldLen(tc, "중량1"));
                appendMsgNum(sendMsg, "", getFieldLen(tc, "길이1"));
                appendMsgNum(sendMsg, "", getFieldLen(tc, "X물리위치1"));
                appendMsgNum(sendMsg, "", getFieldLen(tc, "Y물리위치1"));
                
                totmsg = sendMsg.toString();
            	appendMsg(sendMsg, "", 	714-totmsg.length());
            	sendQueue(YmCommonConst.TC_THHC160, sendMsg.toString());
            }
	    }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
	    return true;
	}

    /**
     * 야드에서 사용가능한 데이터로 변환하여 리턴한다.
     * @param ydMapKey
     * @return
     */
    private String getYdMapKey(String rYdMapKey) {
    	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        if(rYdMapKey.indexOf("*") == 0) {		//야드
            return YmCommonConst.YD_GP_1;
        }else if(rYdMapKey.indexOf("*") == 2) {	//동
            return YmCommonConst.YD_GP_1 + rYdMapKey.substring(1, 2);            
        }else if(rYdMapKey.indexOf("*") == 4) {	//스판
            return YmCommonConst.YD_GP_1 + rYdMapKey.substring(1, 4);
        }else {									//열
            return YmCommonConst.YD_GP_1 		+
            		rYdMapKey.substring(1, 4) 	+ 
            		YmCommonUtil.addZero(rYdMapKey.substring(4, 5));
        }
    }
    
	/**
	 * 오퍼레이션명 : 
	 *
	 * 야드 Level-2로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * 1.TC_CD	: THCH610.
        * 2.I/F ID	: YM-AIF-012.
        * 
        * 전문코드				CHAR	07		
        * CRANE 번호			CHAR	04		
        * SPARE					CHAR	04		
        * 발생일					CHAR	06		YYMMDD
        * 발생시					CHAR	06		HHMMSS
        * CONVEYOR LINE 구분	CHAR	01		0:LHCVO, 1:LHFPI, 2:LSPMI, 3:LSH1I, 4:LSH2I,  *:ALL
        * SPARE				CHAR	122
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
	public boolean acyConveyorIniReq(String msg) {
        logger.println(LogLevel.DEBUG, this, "### ACY_CONVEYOR 초기정보요구 처리");
	    try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
            /**
             * Message Parsing.
             */
            JDTORecord parseData = new Level2Parser().parse(msg);
            logger.println(LogLevel.DEBUG, this, parseData);
	        Map tc = ymCommonDAO.readColumnLenOfTc(getField(parseData, "전문코드"));

            /**
             * valid check.
             */
            String conLine = getFieldNvl(parseData, "CONVEYORLINE구분");        
            if(conLine.length() != getFieldLen(tc, "CONVEYORLINE구분")) {               
                throw new Exception("수신항목 'CONVEYOR LINE 구분' Error: "+ conLine);
            }            
            
            /**
             * I/F ID	: YM-AIF-030
             * T/C		: THHC170		
		     * 01 전문 코드			CHAR	07		
		     * 02 CONVEYOR LINE		CHAR	05		
		     * 03 적치				CHAR	03		
		     * 04 군정보				CHAR	01		15회 반복 시작
		     * 05 코일적치 유무			CHAR	01		.
		     * 06 SPARE				CHAR	01		.
		     * 07 코일번호			CHAR	10		.
		     * 08 제작번호/행번		CHAR	13		.
		     * 09 두께				CHAR	05		.
		     * 10 외경				CHAR	05		.
		     * 11 폭					CHAR	05		.
		     * 12 중량				CHAR	05		.
		     * 13 길이				CHAR	05		15회 반복 끝
		     * 14 SPARE				CHAR	95		
             */
            tc = ymCommonDAO.readColumnLenOfTc(YmCommonConst.TC_THHC170);
            StringBuffer sendMsg = new StringBuffer();
            sendMsg.append(YmCommonConst.TC_THHC170);
            
            if(YmCommonConst.CONVEYOR_LINE_0.equals(conLine)) {
                doConveyorOfMill(tc, sendMsg);
            }else if(YmCommonConst.CONVEYOR_LINE_1.equals(conLine)) {
                doConveyorOfHFL(tc, sendMsg);
            }else if(YmCommonConst.CONVEYOR_LINE_2.equals(conLine)) {
                doConveyorOfSPM(tc, sendMsg);
            }else if(YmCommonConst.CONVEYOR_LINE_ASTA.equals(conLine)) {
                doConveyorOfMill(tc, sendMsg);
                doConveyorOfHFL(tc, sendMsg);
                doConveyorOfSPM(tc, sendMsg);
            }else if(YmCommonConst.CONVEYOR_LINE_3.equals(conLine)) {
                sendMsg.append(YmCommonConst.CONVEYOR_LINE_3_LSH1I);
                doSpace(tc, sendMsg);
            }else if(YmCommonConst.CONVEYOR_LINE_4.equals(conLine)) {
                sendMsg.append(YmCommonConst.CONVEYOR_LINE_4_LSH2I);
                doSpace(tc, sendMsg);
            }            
	    }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
	    return true;
	}

	/**
	 * 오퍼레이션명 : 
	 *
	 *  야드 LEVEL2로 부터 넘어온 전문을 파싱한 후 전문내용을 가지고 해당 업무 로직을 처리한다.
        *  전문내용을 JDTORecord로 파싱한다.
        *  업무 로직
        * 1. TC_CD - THCH510 초기정보 요구
        * 2. A: 대차 이송 상차
        * 3. B: 대차 이송 하차   
        *  THCH510BCR1    050927092711     1
        * 
        *  logger.println(LogLevel.DEBUG, "전문코드   ==" +jDTORecord.getFieldString("전문코드"));
        *  logger.println(LogLevel.DEBUG, "CRANENO ==" +jDTORecord.getFieldString("CRANENO"));
        *  logger.println(LogLevel.DEBUG, "SPARE1    ==" +jDTORecord.getFieldString("SPARE1"));
        *  logger.println(LogLevel.DEBUG, "일자	    ==" +jDTORecord.getFieldString("일자"));
        *  logger.println(LogLevel.DEBUG, "시간	    ==" +jDTORecord.getFieldString("시간"));
        *  logger.println(LogLevel.DEBUG, "SPARE2    ==" +jDTORecord.getFieldString("SPARE2"));
        *  logger.println(LogLevel.DEBUG, "구분	    ==" +jDTORecord.getFieldString("구분")); 1:요구, 2:해제
        *  logger.println(LogLevel.DEBUG, "SPARE3    ==" +jDTORecord.getFieldString("SPARE3"));                                                                     
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
	public boolean receiveAOrd(String sMessage) throws java.rmi.RemoteException{ 
        
        LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		Logger logger           = new Logger(config);
		logger.println(LogLevel.DEBUG,this,"Start-receiveAOrd()");
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		YdEquipDAO ydEquipDAO           = new YdEquipDAO();
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			Boolean isSuccess = new Boolean(false); 
			int iResult = 0;
			
			Level2Parser level2Parser 	= new Level2Parser();
			JDTORecord jDTORecord 		= level2Parser.parse(sMessage);
			
			// 야드 Level-2에서  수신한  Crane No			
			String OldCraneNo = StringHelper.evl(jDTORecord.getFieldString("CRANENO"), "");
			
            /* 수신한 Crane No를  변환
			 * SELECT class3_cd   AS EQUIPNO,
			 *        b.equip_no  AS CRANE_NO
			 *   FROM tb_cm_cdclass3 a, tb_ym_equip b 
			 *  Where a.type_cd 		= 'YM002'
			 *    AND a.class1_cd 		= 'EQPNO'
			 *    AND a.class2_cd 		= ?
			 *    AND a.class3_cd 		= b.equip_gp
			 *    AND a.CLASS3_NAME2    = ?
			 */  
			String sCraneConvert  = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.convertEquipNo";
			JDTORecord newCraneNo = ydEquipDAO.getData(sCraneConvert, new Object[]{ "1", OldCraneNo.trim() });
			String NewCraneNo     = StringHelper.evl(newCraneNo.getFieldString("CRANE_NO"), "");

			if (NewCraneNo == null){
				throw new EJBServiceException("수신한 C/R No를 시스템 C/R로 변환하는 Query Error : 등록된 C/R No가 없습니다.");
			}
			
			/* Crane No로 설비 Table(TB_YM_EQUIP)을 Read
			 * Crane No로 야드구분, 동구분, 설비 Select
			 * SELECT yd_gp, bay_gp, equip_kind, equip_no  FROM tb_ym_equip WHERE equip_no   = :crane_no AND equip_kind = 'CR'
			 */
			String sCraneNo   = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.selectCraneNo";
			JDTORecord schRule = ydEquipDAO.getData(sCraneNo, new Object[]{ NewCraneNo.trim() });
			
			if (schRule == null){
				throw new EJBServiceException("Crane No로 설비 Table(TB_YM_EQUIP)을 Read Error");
			}
			
			String ydGp  = StringHelper.evl(schRule.getFieldString("YD_GP"), "");			
			String bayGp = StringHelper.evl(schRule.getFieldString("BAY_GP"), "");
			
			
//			String schWorkKind = StringHelper.evl(schRule.getFieldString("SCH_WORK_KIND"), "");
			
			/*	A  CTM2 : 동간이적(대차)
			 *	B  CTMU : 대차하차 
           	 */

			String schWorkKind = "";
			
			if(bayGp.equals(YmCommonConst.BAY_GP_A)){
				schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CTM2;     //동간이적(대차)
			}else if (bayGp.equals(YmCommonConst.BAY_GP_B)){                        
				schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CTMU;     //대차하차
			}else {
				return true; 
			}
			

			EJBConnector ejbConn = new EJBConnector("default","JNDICWrkOrdReg",this);
			isSuccess = (Boolean)ejbConn.trx("callCraneSchInfo",new  Class[]{String.class, String.class, String.class, String.class},
									            new Object[]{ydGp.trim(), bayGp.trim(), NewCraneNo.trim(), schWorkKind.trim()});
				
			if (isSuccess.booleanValue() == false){
				return false; 
			}
				
			/* 작업 예약  Table(TB_YM_WBOOK) : 작업예약 등록 우선 순위가 제일 빠른것 추출
			 * Select MIN(WBOOK_ID) From TB_YM_WBOOK a
			 *  Where YD_GP         = ? --야드구분
			 *    And BAY_GP        = ? --동구분
			 *    And SCH_WORK_KIND = ? --스케줄코드
			 *    AND not exists (select wbook_id from tb_ym_sch where wbook_id = a.wbook_id) 
			 */
			String sSchwBook = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectSchWBook";
			JDTORecord WBook = ydEquipDAO.getData(sSchwBook, new Object[]{ ydGp.trim(), bayGp.trim(), schWorkKind.trim() });			
			
			String wbookId = StringHelper.evl(WBook.getFieldString("WBOOK_ID"), "");
			
			logger.println(LogLevel.DEBUG,this, "receiveAOrd()WBOOK_ID: "+wbookId);
			if (wbookId == null || wbookId.equals("")){
				// Error Message EJB Call				
			}
			else{
				//Coil Schedule EJB Call
				 ejbConn         = new EJBConnector("default","JNDICraneSchReg",this);
				 Boolean isTrue  = (Boolean)ejbConn.trx("callCraneSchInfo",new Class[]{String.class, String.class},new Object[]{ 
				 		           wbookId.trim(), NewCraneNo.trim() });
			}				
		
			
			
			logger.println(LogLevel.DEBUG,this, "End-receiveACCoolOrd()");
							
			return true;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	    	e.printStackTrace();  
	        throw new EJBServiceException(e);
	    }    
	} 
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 야드 Level-2로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * 1.TC_CD	: THCH510, THCH511
        * 2.I/F ID	: YM-AIF-001, YM-AIF-002
        * 
        * 전문코드		CHAR	07		
        * CRANE 번호	CHAR	04		
        * SPARE1		CHAR	04		
        * 발생일			CHAR	06		YYMMDD
        * 발생시			CHAR	06		HHMMSS
        * 운전MODE		CHAR	01		0:Off Line, 1:On Line
        * SPARE2		CHAR	122		    
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */          
    public boolean acyCRIniReq(String msg) {
        logger.println(LogLevel.DEBUG, this, "TO DO: ACY_CRANE 초기정보요구 처리");
	    try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
            /**
             * Message Parsing.
             */	        
            JDTORecord parseData = new Level2Parser().parse(msg);
            logger.println(LogLevel.DEBUG, this, parseData);            
            String tcCode = getField(parseData, "전문코드");
            
            if(YmCommonConst.TC_THCH510.equals(tcCode)){
                return receiveAOrd(msg);
            }
            
            Map tc = ymCommonDAO.readColumnLenOfTc(tcCode);
            /**
             * valid check
             */
            String reCraneNo = getField(parseData, "CRANE번호");
            if(reCraneNo.length() != getFieldLen(tc, "CRANE번호")) {
                throw new Exception("수신항목 중 'CRANE번호' ERROR: "+ reCraneNo);
            }
		
			return acyCRIniReq(tcCode,
    						   reCraneNo);
    						   
		}catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
	} 
	/**
	 * 오퍼레이션명 : 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */          	
    public boolean acyCRIniReq(String tcCode,
    						   String reCraneNo) {
    						   	
        logger.println(LogLevel.DEBUG, this, "TO DO: ACY_CRANE 초기정보요구 처리");
	    try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}

            Map tc = null;
            /**
             * 크레인 정보를 가져온다.
             */
            JDTORecord crInfo = ymCommonDAO.readCurrCRInfo(YmCommonConst.YD_GP_1, reCraneNo);
            if(crInfo == null) {
                throw new Exception("CRANE 설비가 존재하지 않습니다: "+ reCraneNo);
            }
            /**
             * I/F ID	: YM-AIF-028
             * T/C		: THHC150, THHC151		
             *01	전문코드	TC				CHAR	7
             *02	CRANE NO	CraneNo		CHAR	4
             *03	SPARE		SPARE		CHAR	1
             *04	운전 MODE	DrivMode		CHAR	1		운전 MODE 1:ON, 0:OFF
             *05	고장 유무	TroRecYN		CHAR	1		고장유무 0:정상, 1:경미한 작업 불가, 9:치명적 작업 불가
             *06	상태구분	StatusId		CHAR	1		상태구분 (0:작업무 1:선택중 2:pick-up 중 3:이동중 4:고장 5:pick-up 이상 6:put 이상 9:put 중)
             *07	발생일시	OccurDateTime	CHAR	12
             *08	SCHEDULE CODE	SchCode	CHAR	3
             *09	권상 ADDRESS	LoadAdd		CHAR	8
             *10	권하 ADDRESS	UnLoadAdd	CHAR	8
             *11	군			Group		CHAR	1
             *12	제품 구분		GoodsId		CHAR	2
             *13	COIL NO		CoilNo		CHAR	10
             *14	제작번호/행번	ProduceNo	CHAR	13
             *15	두께			T			CHAR	5
             *16	폭 			Width		CHAR	5
             *17	길이			Len			CHAR	5
             *18	외경			OutsideDia	CHAR	5
             *19	중량			Wt			CHAR	5
             *20	운송회사		TransCom	CHAR	5
             *21	차량번호		CarNo		CHAR	5
             *22	통로구분		PassId		CHAR	1
             *23	입출구분		InOutId		CHAR	1
             *24	적재매수		HeapEA		CHAR	2
             *25	잔여매수		RemEA		CHAR	2
             *26	권상 X 축 물리 ADDRESS		LoadXPhysAdd	CHAR	6
             *27	권상 X 축 (좌) 허용 오차	LoadXLeftTol	CHAR	4
             *28	권상 X 축 (우) 허용 오차	LoadXRightTol	CHAR	4
             *29	권상 Y 축 물리 ADDRESS		LoadYPhysAdd	CHAR	6
             *30	권상 Y 축 (좌) 허용 오차	LoadYLeftTol	CHAR	4
             *31	권상 Y 축 (우) 허용 오차	LoadYRightTol	CHAR	4
             *32	권하 X 축 물리 ADDRESS		UnLoadXPhysAdd	CHAR	6
             *33	권하 X 축 (좌) 허용 오차	UnLoadXLeftTol	CHAR	4
             *34	권하 X 축 (우) 허용 오차	UnLoadXRightTol	CHAR	4
             *35	권하 Y 축 물리 ADDRESS		UnLoadYPhysAdd	CHAR	6
             *36	권하 Y 축 (좌) 허용 오차	UnLoadYLeftTol	CHAR	4
             *37	권하 Y 축 (우) 허용 오차	UnLoadYRightTol	CHAR	4
             *38	SPARE			SPARE	CHAR	30
             */
            String statusId = getField(crInfo, "STATUS_ID");
			StringBuffer sendMsg = new StringBuffer();
			if(YmCommonConst.TC_THCH510.equals(tcCode)) {
				tc 		= ymCommonDAO.readColumnLenOfTc(YmCommonConst.TC_THHC150);
			    tcCode 	= YmCommonConst.TC_THHC150;
			}else if(YmCommonConst.TC_THCH511.equals(tcCode)) {
				tc 		= ymCommonDAO.readColumnLenOfTc(YmCommonConst.TC_THHC151);
			    tcCode 	= YmCommonConst.TC_THHC151;
			}
			sendMsg.append(tcCode);								//전문코드
			appendMsg(sendMsg, reCraneNo, 						getFieldLen(tc, "CRANENO"));
			appendMsg(sendMsg, "", 								getFieldLen(tc, "SPARE"));
			appendMsg(sendMsg, getField(crInfo, "WORK_MODE"),	getFieldLen(tc, "운전MODE"));
		    appendMsg(sendMsg, getField(crInfo, "TRO_REC_YN"), 	getFieldLen(tc, "고장유무"));
		    appendMsg(sendMsg, statusId, 						getFieldLen(tc, "상태구분"));  
			sendMsg.append(YmCommonUtil.getStringYMDHM());		//발생일시
			
            String upLoc 		= "";
            String putLoc		= "";            
            String schWorkKind 	= "";
            String schId		= getField(crInfo, "SCH_ID");
			if("0".equals(statusId)	|| 
			   "4".equals(statusId)	||
			   "".equals(schId)) {
				appendMsgNum(sendMsg, "", 						getFieldLen(tc, "SCHEDULECODE"));
				appendMsg(sendMsg, "", 							getFieldLen(tc, "권상ADDRESS"));
				appendMsg(sendMsg, "", 							getFieldLen(tc, "권하ADDRESS"));
				appendMsg(sendMsg, "2",							getFieldLen(tc, "군"));
				appendMsg(sendMsg, "00",						getFieldLen(tc, "제품구분"));
				appendMsg(sendMsg, "", 							getFieldLen(tc, "COILNO"));
				appendMsg(sendMsg, "          000",				getFieldLen(tc, "제작번호행번"));
				appendMsgNum(sendMsg, "", 						getFieldLen(tc, "두께"));
				appendMsgNum(sendMsg, "", 						getFieldLen(tc, "폭"));
				appendMsgNum(sendMsg, "", 						getFieldLen(tc, "길이"));
				appendMsgNum(sendMsg, "", 						getFieldLen(tc, "외경"));
				appendMsgNum(sendMsg, "", 						getFieldLen(tc, "중량"));
				appendMsg(sendMsg, "#####",						getFieldLen(tc, "운송회사"));
				appendMsg(sendMsg, "#####",						getFieldLen(tc, "차량번호"));
				appendMsg(sendMsg, "R",							getFieldLen(tc, "통로구분"));
				appendMsg(sendMsg, "?",							getFieldLen(tc, "입출구분"));
				appendMsgNum(sendMsg, "", 						getFieldLen(tc, "적재매수"));
				appendMsgNum(sendMsg, "", 						getFieldLen(tc, "잔여매수"));
				appendMsg(sendMsg, "", 							getFieldLen(tc, "권상X축물리ADDRESS"));
				appendMsgNum(sendMsg, "", 						getFieldLen(tc, "권상X축좌허용오차"));
				appendMsgNum(sendMsg, "", 						getFieldLen(tc, "권상X축우허용오차"));
				appendMsg(sendMsg, "", 							getFieldLen(tc, "권상Y축물리ADDRESS"));
				appendMsgNum(sendMsg, "", 						getFieldLen(tc, "권상Y축좌허용오차"));
				appendMsgNum(sendMsg, "", 						getFieldLen(tc, "권상Y축우허용오차"));
				appendMsg(sendMsg, "", 							getFieldLen(tc, "권하X축물리ADDRESS"));
				appendMsgNum(sendMsg, "", 						getFieldLen(tc, "권하X축좌허용오차"));
				appendMsgNum(sendMsg, "", 						getFieldLen(tc, "권하X축우허용오차"));
				appendMsg(sendMsg, "", 							getFieldLen(tc, "권하Y축물리ADDRESS"));
				appendMsgNum(sendMsg, "", 						getFieldLen(tc, "권하Y축좌허용오차"));
				appendMsgNum(sendMsg, "", 						getFieldLen(tc, "권하Y축우허용오차"));
				appendMsgNum(sendMsg, "", 						getFieldLen(tc, "SPARE1"));			    			    
			}else {
	            upLoc 		= getField(crInfo, "CRANE_WORD_UP_LOC");
	            putLoc 		= getField(crInfo, "CRANE_WORD_PUT_LOC");            
	            schWorkKind	= ymCommonDAO.readLegacySchCode(getField(crInfo, "SCH_WORK_KIND"));
                String t = YmCommonUtil.deletePoint(""+ (getFieldFloat(crInfo, "COIL_T") * 1000));
                String w = YmCommonUtil.deletePoint(getField(crInfo, "COIL_W"));
                String len = ""+ (getFieldInt(crInfo, "COIL_LEN") * 10);
				appendMsgNum(sendMsg, schWorkKind, 				getFieldLen(tc, "SCHEDULECODE"));
				appendMsg(sendMsg, YmCommonUtil.setLegacyPositionWithCur(upLoc),
				        										getFieldLen(tc, "권상ADDRESS"));
				appendMsg(sendMsg, YmCommonUtil.setLegacyPositionWithCur(putLoc),
				        										getFieldLen(tc, "권하ADDRESS"));
				appendMsg(sendMsg, "2",							getFieldLen(tc, "군"));
				appendMsg(sendMsg, "00",						getFieldLen(tc, "제품구분"));
				appendMsg(sendMsg, getField(crInfo, "COIL_NO"),	getFieldLen(tc, "COILNO"));
				appendMsg(sendMsg, getField(crInfo, "PRODUCT_NO"),	getFieldLen(tc, "제작번호행번"));
				appendMsgNum(sendMsg, t, 						getFieldLen(tc, "두께"));
				appendMsgNum(sendMsg, w, 						getFieldLen(tc, "폭"));
				appendMsgNum(sendMsg, len, 						getFieldLen(tc, "길이"));
				appendMsgNum(sendMsg, getField(crInfo, "COIL_OUTDIA"),	getFieldLen(tc, "외경"));
				appendMsgNum(sendMsg, getField(crInfo, "NET_WEIGH_WT"), getFieldLen(tc, "중량"));
				appendMsg(sendMsg, "#####",						getFieldLen(tc, "운송회사"));
				appendMsg(sendMsg, "#####",						getFieldLen(tc, "차량번호"));
				appendMsg(sendMsg, "R",							getFieldLen(tc, "통로구분"));
				appendMsg(sendMsg, "?",							getFieldLen(tc, "입출구분"));
				appendMsgNum(sendMsg, "", 						getFieldLen(tc, "적재매수"));
				appendMsgNum(sendMsg, "", 						getFieldLen(tc, "잔여매수"));
				appendMsg(sendMsg, "", 							getFieldLen(tc, "권상X축물리ADDRESS"));
				appendMsgNum(sendMsg, "", 						getFieldLen(tc, "권상X축좌허용오차"));
				appendMsgNum(sendMsg, "", 						getFieldLen(tc, "권상X축우허용오차"));
				appendMsg(sendMsg, "", 							getFieldLen(tc, "권상Y축물리ADDRESS"));
				appendMsgNum(sendMsg, "", 						getFieldLen(tc, "권상Y축좌허용오차"));
				appendMsgNum(sendMsg, "", 						getFieldLen(tc, "권상Y축우허용오차"));
				appendMsg(sendMsg, "", 							getFieldLen(tc, "권하X축물리ADDRESS"));
				appendMsgNum(sendMsg, "", 						getFieldLen(tc, "권하X축좌허용오차"));
				appendMsgNum(sendMsg, "", 						getFieldLen(tc, "권하X축우허용오차"));
				appendMsg(sendMsg, "", 							getFieldLen(tc, "권하Y축물리ADDRESS"));
				appendMsgNum(sendMsg, "", 						getFieldLen(tc, "권하Y축좌허용오차"));
				appendMsgNum(sendMsg, "", 						getFieldLen(tc, "권하Y축우허용오차"));
				appendMsgNum(sendMsg, "", 						getFieldLen(tc, "SPARE1"));			    			    
			}
			sendQueue(tcCode, sendMsg.toString());
	    }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
	    return true;
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 야드 Level-2로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * 1.TC_CD	: THCH911
        * 
        * 전문코드		CHAR	07		
        * CRANE 번호	CHAR	04		
        * SPARE1		CHAR	04		
        * 발생일			CHAR	06		YYMMDD
        * 발생시			CHAR	06		HHMMSS
        * 코일번호		CHAR	10
        * SPARE2		CHAR	113		
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */               
	public boolean acyCoilInfoReq(String msg) {
        logger.println(LogLevel.DEBUG, this, "TO DO: COIL 정보 요구 처리");
	    try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
            /**
             * Message Parsing.
             */	        
            JDTORecord parseData = new Level2Parser().parse(msg);
            logger.println(LogLevel.DEBUG, this, parseData);            
            String tcCode = getField(parseData, "전문코드");
            Map tc = ymCommonDAO.readColumnLenOfTc(tcCode);
            /**
             * 수신항목의 'CRANE 번호', '운전 MODE'를 가져온다.
             */
            String coilNo 		= getField(parseData, "코일번호");
            String reCraneNo 	= getField(parseData, "CRANE번호");
            /**
             * valid check
             */
            if(coilNo.length() > getFieldLen(tc, "코일번호")) {
                throw new Exception("수신항목 중 '코일번호' ERROR: "+ coilNo);
            }
            /**
             * 크레인 정보를 가져온다.
             */
            JDTORecord crInfo = ymCommonDAO.readCurrCRInfo(YmCommonConst.YD_GP_1, reCraneNo);
            if(crInfo == null) {
                throw new Exception("CRANE 설비가 존재하지 않습니다: "+ reCraneNo);
            }
            /**
             * T/C: THHC131		
             *01	전문코드	TC				CHAR	7
             *02	CRANE NO	CraneNo		CHAR	4
             *03	SPARE		SPARE		CHAR	1
             *04	운전 MODE	DrivMode		CHAR	1		운전 MODE 1:ON, 0:OFF
             *05	고장 유무	TroRecYN		CHAR	1		고장유무 0:정상, 1:경미한 작업 불가, 9:치명적 작업 불가
             *06	상태구분	StatusId		CHAR	1		상태구분 (0:작업무 1:선택중 2:pick-up 중 3:이동중 4:고장 5:pick-up 이상 6:put 이상 9:put 중)
             *07	발생일시	OccurDateTime	CHAR	12
             *08	SCHEDULE CODE	SchCode	CHAR	3
             *09	권상 ADDRESS	LoadAdd		CHAR	8
             *10	권하 ADDRESS	UnLoadAdd	CHAR	8
             *11	군			Group		CHAR	1
             *12	제품 구분		GoodsId		CHAR	2
             *13	COIL NO		CoilNo		CHAR	10
             *14	제작번호/행번	ProduceNo	CHAR	13
             *15	두께			T			CHAR	5
             *16	폭 			Width		CHAR	5
             *17	길이			Len			CHAR	5
             *18	외경			OutsideDia	CHAR	5
             *19	중량			Wt			CHAR	5
             *20	운송회사		TransCom	CHAR	5
             *21	차량번호		CarNo		CHAR	5
             *22	통로구분		PassId		CHAR	1
             *23	입출구분		InOutId		CHAR	1
             *24	적재매수		HeapEA		CHAR	2
             *25	잔여매수		RemEA		CHAR	2
             *26	권상 X 축 물리 ADDRESS		LoadXPhysAdd	CHAR	6
             *27	권상 X 축 (좌) 허용 오차		LoadXLeftTol	CHAR	4
             *28	권상 X 축 (우) 허용 오차		LoadXRightTol	CHAR	4
             *29	권상 Y 축 물리 ADDRESS		LoadYPhysAdd	CHAR	6
             *30	권상 Y 축 (좌) 허용 오차		LoadYLeftTol	CHAR	4
             *31	권상 Y 축 (우) 허용 오차		LoadYRightTol	CHAR	4
             *32	권하 X 축 물리 ADDRESS		UnLoadXPhysAdd	CHAR	6
             *33	권하 X 축 (좌) 허용 오차		UnLoadXLeftTol	CHAR	4
             *34	권하 X 축 (우) 허용 오차		UnLoadXRightTol	CHAR	4
             *35	권하 Y 축 물리 ADDRESS		UnLoadYPhysAdd	CHAR	6
             *36	권하 Y 축 (좌) 허용 오차		UnLoadYLeftTol	CHAR	4
             *37	권하 Y 축 (우) 허용 오차		UnLoadYRightTol	CHAR	4
             *38	SPARE			SPARE	CHAR	30
             */
			StringBuffer sendMsg = new StringBuffer();
			tc = ymCommonDAO.readColumnLenOfTc(YmCommonConst.TC_THHC131);
			sendMsg.append(YmCommonConst.TC_THHC131);	//전문코드
			appendMsg(sendMsg, reCraneNo, getFieldLen(tc, "CRANENO"));
			/**
			 * 크레인정보를 셋팅한다.
			 */
			setCRSendInfo(crInfo, tc, sendMsg);
			/**
			 * 코일정보를 셋팅한다.
			 */
            JDTORecord coilInfo = ymCommonDAO.readCommonCoilInfo(coilNo);
            setCoilSendInfo(coilInfo, tc, sendMsg);
			sendQueue(YmCommonConst.TC_THHC131, sendMsg.toString());
	    }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
	    return true;
	}

    /**
     * 코일정보를 셋팅한다.
     * @param coilInfo	코일정보
     * @param tc		전문정보
     * @param sendMsg	송신메시지
     */
    private void setCoilSendInfo(JDTORecord coilInfo, Map tc, StringBuffer sendMsg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        String t = YmCommonUtil.deletePoint(""+ (getFieldFloat(coilInfo, "COIL_T") * 1000));
        String w = YmCommonUtil.deletePoint(getField(coilInfo, "COIL_W"));
        String len = ""+ (getFieldInt(coilInfo, "COIL_LEN") * 10);
		appendMsg(sendMsg, getField(coilInfo, "COIL_NO"),		getFieldLen(tc, "COILNO"));
		appendMsg(sendMsg, getField(coilInfo, "PRODUCT_NO"),	getFieldLen(tc, "제작번호행번"));
		appendMsgNum(sendMsg, t, 						getFieldLen(tc, "두께"));
		appendMsgNum(sendMsg, w, 						getFieldLen(tc, "폭"));
		appendMsgNum(sendMsg, len, 						getFieldLen(tc, "길이"));
		appendMsgNum(sendMsg, getField(coilInfo, "COIL_OUTDIA"),	getFieldLen(tc, "외경"));
		appendMsgNum(sendMsg, getField(coilInfo, "NET_WEIGH_WT"), 	getFieldLen(tc, "중량"));
		appendMsg(sendMsg, "#####",						getFieldLen(tc, "운송회사"));
		appendMsg(sendMsg, "#####",						getFieldLen(tc, "차량번호"));
		appendMsg(sendMsg, "R",							getFieldLen(tc, "통로구분"));
		appendMsg(sendMsg, "?",							getFieldLen(tc, "입출구분"));
		appendMsgNum(sendMsg, "", 						getFieldLen(tc, "적재매수"));
		appendMsgNum(sendMsg, "", 						getFieldLen(tc, "잔여매수"));
		appendMsg(sendMsg, "", 							getFieldLen(tc, "권상X축물리ADDRESS"));
		appendMsgNum(sendMsg, "", 						getFieldLen(tc, "권상X축좌허용오차"));
		appendMsgNum(sendMsg, "", 						getFieldLen(tc, "권상X축우허용오차"));
		appendMsg(sendMsg, "", 							getFieldLen(tc, "권상Y축물리ADDRESS"));
		appendMsgNum(sendMsg, "", 						getFieldLen(tc, "권상Y축좌허용오차"));
		appendMsgNum(sendMsg, "", 						getFieldLen(tc, "권상Y축우허용오차"));
		appendMsg(sendMsg, "", 							getFieldLen(tc, "권하X축물리ADDRESS"));
		appendMsgNum(sendMsg, "", 						getFieldLen(tc, "권하X축좌허용오차"));
		appendMsgNum(sendMsg, "", 						getFieldLen(tc, "권하X축우허용오차"));
		appendMsg(sendMsg, "", 							getFieldLen(tc, "권하Y축물리ADDRESS"));
		appendMsgNum(sendMsg, "", 						getFieldLen(tc, "권하Y축좌허용오차"));
		appendMsgNum(sendMsg, "", 						getFieldLen(tc, "권하Y축우허용오차"));
		appendMsgNum(sendMsg, "", 						getFieldLen(tc, "SPARE1"));
    }

    /**
     * 크레인정보를 셋팅한다.
     * @param crInfo	크레인정보
     * @param tc		전문정보
     * @param sendMsg	송신메시지
     */
    private void setCRSendInfo(JDTORecord crInfo, Map tc, StringBuffer sendMsg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
		appendMsg(sendMsg, "", 								getFieldLen(tc, "SPARE"));
		appendMsg(sendMsg, getField(crInfo, "WORK_MODE"),	getFieldLen(tc, "운전MODE"));
	    appendMsg(sendMsg, getField(crInfo, "TRO_REC_YN"), 	getFieldLen(tc, "고장유무"));
	    appendMsg(sendMsg, getField(crInfo, "STATUS_ID"),	getFieldLen(tc, "상태구분"));  
		sendMsg.append(YmCommonUtil.getStringYMDHM());		//발생일시			
		appendMsgNum(sendMsg, "", 						getFieldLen(tc, "SCHEDULECODE"));
		appendMsg(sendMsg, "",							getFieldLen(tc, "권상ADDRESS"));
		appendMsg(sendMsg, "",							getFieldLen(tc, "권하ADDRESS"));
		appendMsg(sendMsg, "2",							getFieldLen(tc, "군"));
		appendMsg(sendMsg, "00",						getFieldLen(tc, "제품구분"));
    }

	/**
	 * 오퍼레이션명 : 
	 *
	 * 야드 Level-2로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * 1.TC_CD	: THCH912
        * 
        * 전문코드		CHAR	07		
        * CRANE 번호	CHAR	04		
        * SPARE1		CHAR	04		
        * 발생일			CHAR	06		YYMMDD
        * 발생시			CHAR	06		HHMMSS
        * ADDRESS		CHAR	5
        * SPARE2		CHAR	118		
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                    
	public boolean acyAddressInfoReq(String msg) {
        logger.println(LogLevel.DEBUG, this, "TO DO: ADDRESS 정보 요구 처리");
	    try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
	        /**
	         * 수신전문 ADDRESS 자리수 5에 대해서 TO BE 변환이 되지 않음.
	         */
//            /**
//             * Message Parsing.
//             */	        
//            JDTORecord parseData = new Level2Parser().parse(msg);
//            logger.println(LogLevel.DEBUG, this, parseData);            
//            String tcCode = getField(parseData, "전문코드");
//            Map tc = ymCommonDAO.readColumnLenOfTc(tcCode);
//            /**
//             * 수신항목의 'CRANE 번호', '운전 MODE'를 가져온다.
//             */
//            String address 		= getField(parseData, "ADDRESS");
//            String reCraneNo 	= getField(parseData, "CRANE번호");
//            /**
//             * valid check
//             */
//            if(address.length() > getFieldLen(tc, "ADDRESS")) {
//                throw new Exception("수신항목 중 'ADDRESS' ERROR: "+ address);
//            }
//            /**
//             * 크레인 정보를 가져온다.
//             */
//            JDTORecord crInfo = ymCommonDAO.readCurrCRInfo(YmCommonConst.YD_GP_1, reCraneNo);
//            if(crInfo == null) {
//                throw new Exception("CRANE 설비가 존재하지 않습니다: "+ reCraneNo);
//            }
//            /**
//             * T/C: THHC132		
//             *01	전문코드	TC				CHAR	7
//             *02	CRANE NO	CraneNo		CHAR	4
//             *03	SPARE		SPARE		CHAR	1
//             *04	운전 MODE	DrivMode		CHAR	1		운전 MODE 1:ON, 0:OFF
//             *05	고장 유무	TroRecYN		CHAR	1		고장유무 0:정상, 1:경미한 작업 불가, 9:치명적 작업 불가
//             *06	상태구분	StatusId		CHAR	1		상태구분 (0:작업무 1:선택중 2:pick-up 중 3:이동중 4:고장 5:pick-up 이상 6:put 이상 9:put 중)
//             *07	발생일시	OccurDateTime	CHAR	12
//             *08	SCHEDULE CODE	SchCode	CHAR	3
//             *09	권상 ADDRESS	LoadAdd		CHAR	8
//             *10	권하 ADDRESS	UnLoadAdd	CHAR	8
//             *11	군			Group		CHAR	1
//             *12	제품 구분		GoodsId		CHAR	2
//             *13	COIL NO		CoilNo		CHAR	10
//             *14	제작번호/행번	ProduceNo	CHAR	13
//             *15	두께			T			CHAR	5
//             *16	폭 			Width		CHAR	5
//             *17	길이			Len			CHAR	5
//             *18	외경			OutsideDia	CHAR	5
//             *19	중량			Wt			CHAR	5
//             *20	운송회사		TransCom	CHAR	5
//             *21	차량번호		CarNo		CHAR	5
//             *22	통로구분		PassId		CHAR	1
//             *23	입출구분		InOutId		CHAR	1
//             *24	적재매수		HeapEA		CHAR	2
//             *25	잔여매수		RemEA		CHAR	2
//             *26	권상 X 축 물리 ADDRESS		LoadXPhysAdd	CHAR	6
//             *27	권상 X 축 (좌) 허용 오차		LoadXLeftTol	CHAR	4
//             *28	권상 X 축 (우) 허용 오차		LoadXRightTol	CHAR	4
//             *29	권상 Y 축 물리 ADDRESS		LoadYPhysAdd	CHAR	6
//             *30	권상 Y 축 (좌) 허용 오차		LoadYLeftTol	CHAR	4
//             *31	권상 Y 축 (우) 허용 오차		LoadYRightTol	CHAR	4
//             *32	권하 X 축 물리 ADDRESS		UnLoadXPhysAdd	CHAR	6
//             *33	권하 X 축 (좌) 허용 오차		UnLoadXLeftTol	CHAR	4
//             *34	권하 X 축 (우) 허용 오차		UnLoadXRightTol	CHAR	4
//             *35	권하 Y 축 물리 ADDRESS		UnLoadYPhysAdd	CHAR	6
//             *36	권하 Y 축 (좌) 허용 오차		UnLoadYLeftTol	CHAR	4
//             *37	권하 Y 축 (우) 허용 오차		UnLoadYRightTol	CHAR	4
//             *38	SPARE			SPARE	CHAR	30
//             */
//			StringBuffer sendMsg = new StringBuffer();
//			tc = ymCommonDAO.readColumnLenOfTc(YmCommonConst.TC_THHC132);
//			sendMsg.append(YmCommonConst.TC_THHC132);	//전문코드
//			appendMsg(sendMsg, reCraneNo, getFieldLen(tc, "CRANENO"));
//			/**
//			 * 크레인정보를 셋팅한다.
//			 */
//			setCRSendInfo(crInfo, tc, sendMsg);
//			/**
//			 * 코일정보를 셋팅한다.
//			 */
//			JDTORecord coilInfo = ymCommonDAO.readAddressInfo(address, "", "");
//			setCoilSendInfo(coilInfo, tc, sendMsg);
//			sendQueue(YmCommonConst.TC_THHC132, sendMsg.toString());
	    }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
	    return true;
	}

	/**
	 * 오퍼레이션명 : 
	 *
	 * 야드 Level-2로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * 1.TC_CD	: THCH530.
        * 2.I/F ID	: YM-AIF-004.
        * 
        * 전문코드		CHAR	07		
        * CRANE 번호	CHAR	04		
        * SPARE			CHAR	04		
        * 발생일			CHAR	06		YYMMDD
        * 발생시			CHAR	06		HHMMSS
        * 운전 MODE		CHAR	01		0:Off Line, 1:On Line
        * SPARE			CHAR	122		
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                         
	public boolean acyCRDriOnOffResult(String sMessage) {
        logger.println(LogLevel.DEBUG, this, "ACY_CRANE 운전 ModeOn/Off 실적 처리");
	    try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
            /**
             * Message Parsing.
             */	        
            JDTORecord parseData = new Level2Parser().parse(sMessage);
            logger.println(LogLevel.DEBUG, this, parseData);
            Map tc = ymCommonDAO.readColumnLenOfTc(getField(parseData, "전문코드"));            
            /**
             * valid check
             */
            validCRNoAndDriMode(parseData, tc);
            /**
             * 수신항목의 'CRANE 번호', '운전 MODE'를 가져온다.
             */
            String reCraneNo = getField(parseData, "CRANE번호");
            String driveMode = getField(parseData, "운전MODE");            
			/**
			 * '설비구분'을 조건으로 설비 테이블을 조회한다.
			 */
			JDTORecord equipInfo = ymCommonDAO.readEquipInfo(YmCommonConst.YD_GP_1, reCraneNo);
            /**
             * 수신항목의 '운전MODE'의 값이 '1'일경우 OnLine, '0'일경우 OffLine 처리한다.
             */
			editACRDriveInfo(equipInfo, driveMode, "2");
	    }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
	    return true;
	}
	
    /**
     * 수신항목 'CRANE번호', '운전MODE'를 체크한다.
     * @param parseData	수신정보
     * @param tc		송/수신항목의 LENGTH 정보
     * @return
     */
	private void validCRNoAndDriMode(JDTORecord parseData, Map tc) throws Exception {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        String reCraneNo = getField(parseData, "CRANE번호");
        String driveMode = getField(parseData, "운전MODE");
        if(reCraneNo.length() != getFieldLen(tc, "CRANE번호")) {
            throw new Exception("수신항목 중 'CRANE번호' ERROR: "+ reCraneNo);
        }else if(driveMode.length() != getFieldLen(tc, "운전MODE")) {
            throw new Exception("수신항목 중 '운전MODE' ERROR: "+ driveMode);
        }
	}
    
    /**
     * 스케쥴 정보를 가져온다.
     * @param baData
     * @param alterEquipNo
     * @return
     */
    private Object[] getOffDataOfSchedule(
            JDTORecord baData, String equipNo, String alterEquipNo) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        return new Object[]{
                alterEquipNo,
                getField(baData, "SCH_RULE_ALTER_WPREFER"),
                getField(baData, "SCH_RULE_ID"),
                getField(baData, "YD_GP"),
                getField(baData, "BAY_GP"),
                getField(baData, "SCH_WORK_KIND"),
                equipNo };
    }

    /**
     * 크레인 고장 스케쥴 정보를 가져온다.
     * @param baData
     * @param alterEquipNo
     * @return
     */
    private Object[] getOffDataOfSchedule(JDTORecord baData, String alterEquipNo) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        return getOffDataOfSchedule(baData, alterEquipNo, alterEquipNo);
    }

	/**
	 * 오퍼레이션명 : 
	 *
	 * 야드 Level-2로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * 1.TC_CD	: 없음.
        * 2.I/F ID	: 없음.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
	public boolean acySysTimeReq(String sMessage) {
        logger.println(LogLevel.DEBUG, this, " TODO: ACY_시스템 시각 동기화 요구 처리");
        try {
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return false;
    		}
    		
            /**
             * TC ID	: THHT510
             * I/F ID	: YM-AIF-041
             * 전문코드	CHAR	7
             * 현재 날짜	CHAR	6
             * 현재 시각	CHAR	6
             * filler 	CHAR	31
             */
            StringBuffer sendMsg = new StringBuffer();
            sendMsg.append(YmCommonConst.TC_THHT510);
            sendMsg.append(YmCommonUtil.getStringSubYMD());
            sendMsg.append(YmCommonUtil.getStringHMS());            
            appendMsg(sendMsg, "", 31);

//            sendQueue(YmCommonConst.TC_THHT510,
//                    Level2SendQueue.DOMAIN_YM_ACYLR_S, sendMsg.toString());
        }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
	    return true;
	}
	
    /**
     * 공백을 셋팅한다.
     * @param sendMsg
     * @throws Exception
     * @throws RemoteException
     */
    private void doSpace(Map tc, StringBuffer sendMsg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        appendMsgNum(sendMsg, "", getFieldLen(tc, "적치"));
        appendMsg(	 sendMsg, "", getFieldLen(tc, "군정보1"));
        appendMsg(	 sendMsg, "", getFieldLen(tc, "코일적치유무1"));
        appendMsg(	 sendMsg, "", getFieldLen(tc, "SPARE1"));
        appendMsg(	 sendMsg, "", getFieldLen(tc, "코일번호1"));
        appendMsg(	 sendMsg, "", getFieldLen(tc, "제작번호행번1"));
        appendMsgNum(sendMsg, "", getFieldLen(tc, "두께1"));
        appendMsgNum(sendMsg, "", getFieldLen(tc, "폭1"));
        appendMsgNum(sendMsg, "", getFieldLen(tc, "외경1"));
        appendMsgNum(sendMsg, "", getFieldLen(tc, "중량1"));
        appendMsgNum(sendMsg, "", getFieldLen(tc, "길이1"));
        appendMsg(	 sendMsg, "", getFieldLen(tc, "SPARE"));

        sendQueue(YmCommonConst.TC_THHC170, sendMsg.toString());
    }

    /**
     * SPM 컨베이어 정보를 셋팅한다.
     * @param ydEquipDAO
     * @param sendMsg
     */
    private void doConveyorOfSPM(Map tc, StringBuffer sendMsg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        sendMsg.append(YmCommonConst.CONVEYOR_LINE_2_LSPMI); //SPM
        doConveyorIniInfo(tc, sendMsg, YmCommonConst.CONVEYOR_LINE_2_LSPMI);
        sendQueue(YmCommonConst.TC_THHC170, sendMsg.toString());
        sendMsg.delete(1, sendMsg.length());
    }

    /**
     * HFL 컨베이어 정보를 셋팅한다.
     * @param ydEquipDAO
     * @param sendMsg
     */
    private void doConveyorOfHFL(Map tc, StringBuffer sendMsg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        sendMsg.append(YmCommonConst.CONVEYOR_LINE_1_LHFPI); //HFL                
        doConveyorIniInfo(tc, sendMsg, YmCommonConst.CONVEYOR_LINE_1_LHFPI); 
        sendQueue(YmCommonConst.TC_THHC170, sendMsg.toString());
        sendMsg.delete(1, sendMsg.length());
    }

    /**
     * Mill 컨베이어 정보를 셋팅한다.
     * @param tc		송/수신항목의 LENGTH 정보
     * @param sendMsg	송신정보
     */
    private void doConveyorOfMill(Map tc, StringBuffer sendMsg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        sendMsg.append(YmCommonConst.CONVEYOR_LINE_0_LHCVO); //압연                
        doConveyorIniInfo(tc, sendMsg, YmCommonConst.CONVEYOR_LINE_0_LHCVO); 
        sendQueue(YmCommonConst.TC_THHC170, sendMsg.toString());
        sendMsg.delete(1, sendMsg.length());
    }

    /**
     * 컨베이어 초기정보를 셋팅한다.
     * @param tc		송/수신항목의 LENGTH 정보
     * @param sendMsg	송신정보
     * @param lineGp	컨베이어 라인구분
     */
    private void doConveyorIniInfo(Map tc, StringBuffer sendMsg, String lineGp) {        
    	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        String coilYN 		= null;
        String stackCnt 	= null;
		JDTORecord conInfo 	= null;
        List conInfos 		= getConveyorIniInfo(lineGp);
        int conInfosCnt		= conInfos != null && conInfos.size() > 0 ? conInfos.size() : 0;
        if(conInfosCnt > 0) {
    		for(int i = 0; i < conInfosCnt; i++) {
    		    conInfo = (JDTORecord)conInfos.get(i);    		    
    		    if(i == 0) { //적치				CHAR	03
    		        appendMsgNum(sendMsg, getField(conInfo, "COIL_STACK_CNT"), getFieldLen(tc, "적치")); 		        
    		    }
    		    
    		    coilYN = getField(conInfo, "COIL_YN");
    		    appendMsg(sendMsg, "", 		getFieldLen(tc, "군정보1"));
    		    appendMsg(sendMsg, coilYN, 	getFieldLen(tc, "코일적치유무1"));
			    appendMsg(sendMsg, "", 		getFieldLen(tc, "SPARE1"));

    		    if(YmCommonConst.COIL_YN_N.equals(coilYN)) {
    			    appendMsg(sendMsg, "", getFieldLen(tc, "코일번호1"));
    			    appendMsg(sendMsg, "", getFieldLen(tc, "제작번호행번1"));    			    
    			    appendMsgNum(sendMsg, "", getFieldLen(tc, "두께1"));
    			    appendMsgNum(sendMsg, "", getFieldLen(tc, "폭1"));
    			    appendMsgNum(sendMsg, "", getFieldLen(tc, "외경1"));
    			    appendMsgNum(sendMsg, "", getFieldLen(tc, "중량1"));
    			    appendMsgNum(sendMsg, "", getFieldLen(tc, "길이1"));
    		    }else if(YmCommonConst.COIL_YN_Y.equals(coilYN)) {
    		        appendMsg(sendMsg, getField(conInfo, "COIL_NO"), 	getFieldLen(tc, "코일번호1"));
    		        appendMsg(sendMsg, getField(conInfo, "PRODUCT_NO"), getFieldLen(tc, "제작번호행번1"));
	                String t = YmCommonUtil.deletePoint(""+ (getFieldFloat(conInfo, "COIL_T") * 1000));
	                String w = YmCommonUtil.deletePoint(getField(conInfo, "COIL_W"));
    		        appendMsgNum(sendMsg, t, 							getFieldLen(tc, "두께1"));
    		        appendMsgNum(sendMsg, w, 							getFieldLen(tc, "폭1"));
    		        appendMsgNum(sendMsg, getField(conInfo, "COIL_OUTDIA"), 	getFieldLen(tc, "외경1"));
    		        appendMsgNum(sendMsg, getField(conInfo, "NET_WEIGH_WT"), 	getFieldLen(tc, "중량1")); 
    		        appendMsgNum(sendMsg, ""+ (getFieldInt(conInfo, "COIL_LEN") * 10), 		getFieldLen(tc, "길이1"));
    		    }
    		}
    		appendMsg(sendMsg, "", getFieldLen(tc, "SPARE"));
        }else {
            doSpace(tc, sendMsg);
        }
    }

    /**
     * 컨베이어 초기정보를 구분한다.
     * @param lineGp	컨베이어 라인구분
     * @return
     */
    private List getConveyorIniInfo(String lineGp) {
    	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        if(YmCommonConst.CONVEYOR_LINE_0_LHCVO.equals(lineGp)) {
            return ymCommonDAO.readHFLConIniInfo(
                    YmCommonConst.Roll_COL_1BDC, YmCommonConst.Roll_COL_1CDC);            
        }else if(YmCommonConst.CONVEYOR_LINE_1_LHFPI.equals(lineGp)) {
            return ymCommonDAO.readHFLConIniInfo(
                    YmCommonConst.HFL_COL_1BFE, YmCommonConst.HFL_COL_1CFD);            
        }else if(YmCommonConst.CONVEYOR_LINE_2_LSPMI.equals(lineGp)) {
            return ymCommonDAO.readSPMConIniInfo(
                    YmCommonConst.SPM_COL_1DKE, YmCommonConst.SPM_COL_1EKE,
                    YmCommonConst.SPM_COL_1EKD, YmCommonConst.SPM_COL_1FKD);
            
        }
        return null;
    }

    /**
     * 크레인 시스템모드에 따른 On/OffLine 처리한다. 
     * @param equip		설비정보
     * @param equipGp	설비구분
     * @param mode		시스템모드
     */
    private void editCRSystemInfo(JDTORecord equip, String mode, String gp) throws Exception {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        String yd		= getField(equip, "YD_GP");
        String equipGp	= getField(equip, "EQUIP_GP");
        if(isModeOff(equip, mode, gp)) {
            logger.println(LogLevel.DEBUG, this, "크레인 설비 OffLine 처리.");
		    ymCommonDAO.modifyHMIOfEquip(YmCommonConst.TRO_REC_C, equipGp);
	        /**
	         * 고장 크레인의 초기 크레인 정보를 송신한다. 
	         */
	        considerSendCRIniInfo(yd, equipGp);
		}else if(isModeOn(equip, mode, gp)) {
		    logger.println(LogLevel.DEBUG, this, "크레인 설비 OnLine 처리.");
		    ymCommonDAO.modifyHMIOfEquip(YmCommonConst.TRO_REC_O, equipGp);	
	        /**
	         * 고장 크레인의 초기 크레인 정보를 송신한다. 
	         */
	        considerSendCRIniInfo(yd, equipGp);
		}else {
		    logger.println(LogLevel.ERROR, this, "### 수신항목의 '시스템모드' ERROR.");
		}    
    }
    
    /**
     * 고장 크레인의 초기 크레인 정보를 송신한다. 
     * @param equipGp
     * @param yd
     * 
     */
    private void considerSendCRIniInfo(String yd, String equipGp) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        if(YmCommonConst.YD_GP_1.equals(yd)) {
	        callCRIniInfo(yd, ymCommonDAO.readEquipGpOfToBe(yd, equipGp));	            
        }else if(YmCommonConst.YD_GP_2.equals(yd)) {
            callCRIniInfo(
                    ymCommonDAO.readColumnLenOfTc(YmCommonConst.TC_CM1BP01),
                    YmCommonConst.TC_CM1BP01,
                    equipGp);
        }else if(YmCommonConst.YD_GP_3.equals(yd)) {
            callCRIniInfo(
                    ymCommonDAO.readColumnLenOfTc(YmCommonConst.TC_CN1BP01),
                    YmCommonConst.TC_CN1BP01,
                    equipGp);
         //2007-02-22 A열연 SLAB야드 추가 [CRANE 작업지시 전문]
        }else if(YmCommonConst.YD_GP_0.equals(yd) && YmCommonConst.BAY_GP_A.equals(equipGp.substring(1, 2))) {
            callCRIniInfo(
                    ymCommonDAO.readColumnLenOfTc(YmCommonConst.TC_HM1BP01),
                    YmCommonConst.TC_HM1BP01,
                    equipGp);
         //2007-02-22 A열연 SLAB야드 추가 [CRANE 작업지시 전문]            
        }else if(YmCommonConst.YD_GP_0.equals(yd) && YmCommonConst.BAY_GP_B.equals(equipGp.substring(1, 2))) {
            callCRIniInfo(
                    ymCommonDAO.readColumnLenOfTc(YmCommonConst.TC_HM1BP51),
                    YmCommonConst.TC_HM1BP51,
                    equipGp);
        }
    }

    /**
     * 고장 크레인의 초기 크레인 정보를 송신한다.
     * @param schs	스케쥴정보
     */
    private void callCRIniInfo(String yd, String reEquipGp) {
        EJBConnector ejbConn = null;
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
			
			/*
		    Map tc = ymCommonDAO.readColumnLenOfTc(YmCommonConst.TC_THCH510);
		    StringBuffer sendMsg = new StringBuffer();
		    sendMsg.append(YmCommonConst.TC_THCH510);		//전문코드
		    sendMsg.append(reEquipGp);						//CRANE번호
		    appendMsg(sendMsg, "", 							getFieldLen(tc, "SPARE1"));
		    sendMsg.append(YmCommonUtil.getStringSubYMD());	//발생일
		    sendMsg.append(YmCommonUtil.getStringHMS());	//발생시
		    appendMsg(sendMsg, "", 							getFieldLen(tc, "SPARE2"));
		    appendMsg(sendMsg, "", 							getFieldLen(tc, "운전MODE"));
		    
		    -	YJK.20060619
            -	C/R 고장/복구 실적시 C/R 초기정보 송신
		    */
			ejbConn = new EJBConnector("default","JNDICraneStatusReg",this);
            ejbConn.trx("acyCRIniReq",new Class[]{String.class,
            									  String.class},
            						 new Object[]{YmCommonConst.TC_THCH510,
            						 			  reEquipGp});			    
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

	/**
     * 송신 EJB를 이용하여 송신데이터를 송신한다.
     * @param methodName	TC명
     * @param sendMsg		송신데이터
     * @throws Exception
     */
    private void sendQueue(String methodName, String sendMsg) {
        EJBConnector ejbConn = null;
    	try {
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return;
    		}
    		
    	    ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
            ejbConn.trx("send"+ methodName, new Class[]{ String.class }, new Object[]{ sendMsg });
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
    }

    /**
     * JDTORecord 가 가지는 name parameter에 대한 값을 리턴한다.
     * @param data
     * @param name
     * @return
     */
    private String getField(JDTORecord data, String name) {
        return StringHelper.evl(data.getFieldString(name), "").trim();
    }

    private int getFieldInt(JDTORecord data, String name) {
        return StringHelper.parseInt(data.getFieldString(name), 0);
    }

    private float getFieldFloat(JDTORecord data, String name) {
        return StringHelper.parseFloat(data.getFieldString(name), 0);
    }

    /**
     * JDTORecord 가 가지는 name parameter에 대한 값을 리턴한다.
     * @param data
     * @param name
     * @return
     */
    private String getFieldNvl(JDTORecord data, String name) {
        return StringHelper.nvl(data.getFieldString(name), "").trim();
    }

    /**
     * name parameter에 대한 값을 반환한다.
     * @param data	
     * @param name  
     * @return
     */
    private int getFieldLen(Map data, String name) {
        return StringHelper.parseInt((String)data.get(name), 0);
    }

    /**
     * 공백을 cnt 만큼 리턴한다.
     * @param cnt	공백 수
     * @return
     */
    private void appendMsg(StringBuffer buffer, String field, int cnt) {
	    try{	
	    	if("".equals(field)) {
	            fillSpace(buffer, cnt);
	        }else if(CommonUtil.getLength(field) > cnt) {
	        	buffer.append(CommonUtil.substr(field, 0, cnt));
	        }else if(CommonUtil.getLength(field) < cnt) {
	            buffer.append(field);
	            fillSpace(buffer, cnt - CommonUtil.getLength(field));
	        }else {
	            buffer.append(field);
	        }
	    }catch(Exception e){}
    }
    
    /**
     * 0을 cnt 만큼 리턴한다.
     * @param cnt	공백 수
     * @return
     */
    private void appendMsgNum(StringBuffer buffer, String field, int cnt) {
	    try{    
	        if("".equals(field)) {
	            fillZeroSpace(buffer, cnt);
	        }else if(CommonUtil.getLength(field) > cnt) {
	            buffer.append(CommonUtil.substr(field, 0, cnt));
	        }else if(CommonUtil.getLength(field) < cnt) {
	            fillZeroSpace(buffer, cnt - CommonUtil.getLength(field));
	            buffer.append(field);
	        }else {
	            buffer.append(field);
	        }
	    }catch(Exception e){}
    }

    /**
     * 공백을 cnt 만큼 리턴한다.
     * @param cnt	공백 수
     * @return
     */
    private void fillSpace(StringBuffer buffer, int cnt) {
        for(int i = 0; i < cnt; i++) {
            buffer.append(" ");
        }
    }

    /**
     * 0을 cnt 만큼 리턴한다.
     * @param cnt	공백 수
     * @return
     */
    private void fillZeroSpace(StringBuffer buffer, int cnt) {
        for(int i = 0; i < cnt; i++) {
            buffer.append("0");
        }
    }

	/**
	 * 오퍼레이션명 : 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
    public JDTORecord getEquipStat(String YD_GP, String BAY_GP, String EQUIP_KIND){
    	YdEquipDAO ydequipDAO = null;	    
	    JDTORecord returnRecord = null;
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
	    	ydequipDAO = new YdEquipDAO();
	    	returnRecord = ydequipDAO.getData("ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.getEquipStat",new Object[]{YD_GP, BAY_GP, EQUIP_KIND});
	    	return returnRecord;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}
        
	/**
	 * 오퍼레이션명 : 
	 *
	 * 작업예약취소 버튼 기능
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */          
    public boolean setWorkIdCancle(String sStockId) {
		boolean isSuccess = false;
		String sSCH_WORK_KIND      ="";
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String sWbookId = "";
			
			JDTORecord stockJr = dao.getStockInfo(sStockId);
			
			if(stockJr != null){
				sWbookId = StringHelper.evl(stockJr.getFieldString("WBOOK_ID"), "");
			}
			
			/*
			 *	1.   작업예약ID에 해당하는  저장품을 추출
	    	 */
	    	YdStockDAO ydStockDAO = new YdStockDAO(); 
			String sQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.selectListStockID";
			List stockL     = ydStockDAO.getListData(sQueryId, new Object[]{ sWbookId.trim() });
			
			int iMaxRec     = stockL.size();
			int iSeq		= -1;
			
			if (iMaxRec > 0 ){
				//소재이송취소인 다른 작업예약이 존재 시 해당 저장품만 취소 처리함. 
				JDTORecord selStockJr2 	= (JDTORecord) stockL.get(0);
				sSCH_WORK_KIND      	= StringHelper.evl(selStockJr2.getFieldString("SCH_WORK_KIND"), "");
				
				if(!"".equals(sSCH_WORK_KIND)){
					sSCH_WORK_KIND =sSCH_WORK_KIND.substring(0 , 3);
				}
				
				logger.println(LogLevel.DEBUG, this, "소재이송 취소 작업:"+iMaxRec+"SCH_CD:"+sSCH_WORK_KIND);
				if (iMaxRec > 1 && sSCH_WORK_KIND.equals("CVM")){
					String sSelStock      = sStockId;
					
					
					/**
			 		 *	7.	차량정보 재료에 있으면 삭제
			 		 */
			 		 iSeq = dao.deleteCarmtlInfo(sSelStock);
			 		 logger.println(LogLevel.DEBUG, this, "차량정보재료취소=> 차량정보재료취소="+iSeq);
			 		 
					
					JDTORecord selSchJr   = dao.getSchInfoWithWbookId(sWbookId,
		    							  					  		  sSelStock); 
					
					if(selSchJr != null){
						/**
		 	 			 *	2	스케쥴 취소 모듈 CALL
		 	 			 */
		 	 			String sSchId = StringHelper.evl(selSchJr.getFieldString("SCH_ID"),""); 
		 	 			
		 	 			EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
						Boolean isTemp  = (Boolean)ejbConn.trx("cancelCoilSchInfo",
													new  Class[]{String.class},
													new Object[]{sSchId});
						logger.println(LogLevel.DEBUG, this, "작업예약취소=> 스케쥴 취소 모듈 CALL="+isTemp);													
		 	 		}    
		 	 		
		 	 		JDTORecord selLayerJr = dao.getStackLayerInfoWithStockId_02(sSelStock);
		 	 		
		 	 		String sStackColGp   = "";
		    		String sStackBedGp   = "";
		    		String sStackLayerGp = "";
		    		String sUpUsageCd    = "";
		    		
		 	 		if(selLayerJr != null){			
		 	 			
		 	 			sStackColGp   = StringHelper.evl(selLayerJr.getFieldString("STACK_COL_GP"), "");
			    		sStackBedGp   = StringHelper.evl(selLayerJr.getFieldString("STACK_BED_GP"), "");
			    		sStackLayerGp = StringHelper.evl(selLayerJr.getFieldString("STACK_LAYER_GP"), "");
			    		
			    		logger.println(LogLevel.DEBUG, this, "작업예약취소=> StackLayer정보 CALL=" + sStackColGp);	
			    		logger.println(LogLevel.DEBUG, this, "작업예약취소=> StackLayer정보 CALL=" + sStackBedGp);
			    		logger.println(LogLevel.DEBUG, this, "작업예약취소=> StackLayer정보 CALL=" + sStackLayerGp);
			    		
			    		sUpUsageCd    = YmCommonUtil.getStackColInfoWithPk(sStackColGp);
						
				   		if(YmCommonConst.STACK_COL_USAGE_CD_CX.equals(sUpUsageCd)){// COIL 대차정지위치
						   	
						   	/**
							 *	대차위치 정보 셋팅
							 */ 
							if (sStackColGp.startsWith(YmCommonConst.YD_GP_1)||
			 	 				sStackColGp.startsWith(YmCommonConst.YD_GP_3)){
			 	 				
			 	 				iSeq = dao.updateStockMoveEquipInfo(sSelStock,
						    								   		sStackColGp.substring(0, 1)+ "X"+
						   						    				sStackColGp.substring(2),
						    								   		sStackBedGp,
						    								   		sStackLayerGp,
						    								   		"",
						    								   		"");
			 	 						
			 	 			}else{
			 	 				
			 	 				iSeq = dao.updateStockMoveEquipInfo(sSelStock,
						    								   		sStackColGp.substring(0,1) + "X"+ 
																  	sStackColGp.substring(2,4) + "0"+ 
																  	sStackColGp.substring(4,5),
						    								   		sStackBedGp,
						    								   		sStackLayerGp,
						    								   		"",
						    								   		"");						
			 	 			}	
						}
						
			    		/* 
						 *	3.	적치단 FROM위치 초기화
						 *		tb_ym_stacklayer Table : stock_id = sStockId
						 *		tb_ym_stacklayer Table : stack_layer_stat= 'L'(예약상태)
						 */	
				    	iSeq = dao.updateCraneStackLayerStat(sStackColGp,
				    										 sStackBedGp,
				    										 sStackLayerGp,
				    										 sSelStock,
				    										 YmCommonConst.STACK_LAYER_STAT_L);
				    	logger.println(LogLevel.DEBUG, this, "작업예약취소=> 적치단 FROM위치 초기화");											 
				    }
				    
				    /**
	 	 			 *	4.	저장품 TABLE 작업예약 항목 삭제
	 	 			 */
				    iSeq = dao.updateStockWbookId_01(sSelStock); 
					logger.println(LogLevel.DEBUG, this, "작업예약취소=> 저장품 작업예약 항목 삭제");
					
					/**
	 	 			 *	5.	저장품 TABLE 저장품이동조건 셋팅
	 	 			 */
	 	 			String[] sStockInfo = null;
	 	 			 
	 	 			if (sStackColGp.startsWith(YmCommonConst.YD_GP_1)||
	 	 				sStackColGp.startsWith(YmCommonConst.YD_GP_3)){
	 	 				
	 	 				sStockInfo = YmCommonUtil.getCoilCurrProgCd(sSelStock,"");
	 	 						
	 	 			}else{
	 	 				
	 	 				sStockInfo = YmCommonUtil.getSlabCurrProgCd(sSelStock,"");
	 	 			}	
	 	 			String sProgCd   	= sStockInfo[0];
					String sStocMv   	= sStockInfo[1];
					    							  
					iSeq = dao.updateStockTransInfo(sSelStock,
											        sStocMv);	
					logger.println(LogLevel.DEBUG, this, "작업예약취소=> 저장품 저장품이동조건 셋팅="+sStocMv);
					
					iSeq = dao.updateStockPutLocWithStockId(sSelStock,"");  
					logger.println(LogLevel.DEBUG, this, "작업예약취소=> 저장품지정 예약위치 CLEAR =");
					
		 
					//********************************************************************
				}else{				
					//********************************************************************
					for (int inx = 0; inx < iMaxRec; inx++){
						JDTORecord selStockJr = (JDTORecord) stockL.get(inx);
						String sSelStock      = StringHelper.evl(selStockJr.getFieldString("STOCK_ID"), "");
						
						
						/**
				 		 *	7.	차량정보 재료에 있으면 삭제
				 		 */
				 		 iSeq = dao.deleteCarmtlInfo(sSelStock);
				 		 logger.println(LogLevel.DEBUG, this, "차량정보재료취소=> 차량정보재료취소="+iSeq);
				 		 
						
						JDTORecord selSchJr   = dao.getSchInfoWithWbookId(sWbookId,
			    							  					  		  sSelStock); 
						
						if(selSchJr != null){
							/**
			 	 			 *	2	스케쥴 취소 모듈 CALL
			 	 			 */
			 	 			String sSchId = StringHelper.evl(selSchJr.getFieldString("SCH_ID"),""); 
			 	 			
			 	 			EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
							Boolean isTemp  = (Boolean)ejbConn.trx("cancelCoilSchInfo",
														new  Class[]{String.class},
														new Object[]{sSchId});
							logger.println(LogLevel.DEBUG, this, "작업예약취소=> 스케쥴 취소 모듈 CALL="+isTemp);													
			 	 		}    
			 	 		
			 	 		JDTORecord selLayerJr = dao.getStackLayerInfoWithStockId_02(sSelStock);
			 	 		
			 	 		String sStackColGp   = "";
			    		String sStackBedGp   = "";
			    		String sStackLayerGp = "";
			    		String sUpUsageCd    = "";
			    		
			 	 		if(selLayerJr != null){			
			 	 			
			 	 			sStackColGp   = StringHelper.evl(selLayerJr.getFieldString("STACK_COL_GP"), "");
				    		sStackBedGp   = StringHelper.evl(selLayerJr.getFieldString("STACK_BED_GP"), "");
				    		sStackLayerGp = StringHelper.evl(selLayerJr.getFieldString("STACK_LAYER_GP"), "");
				    		
				    		logger.println(LogLevel.DEBUG, this, "작업예약취소=> StackLayer정보 CALL=" + sStackColGp);	
				    		logger.println(LogLevel.DEBUG, this, "작업예약취소=> StackLayer정보 CALL=" + sStackBedGp);
				    		logger.println(LogLevel.DEBUG, this, "작업예약취소=> StackLayer정보 CALL=" + sStackLayerGp);
				    		
				    		sUpUsageCd    = YmCommonUtil.getStackColInfoWithPk(sStackColGp);
							
					   		if(YmCommonConst.STACK_COL_USAGE_CD_CX.equals(sUpUsageCd)){// COIL 대차정지위치
							   	
							   	/**
								 *	대차위치 정보 셋팅
								 */ 
								if (sStackColGp.startsWith(YmCommonConst.YD_GP_1)||
				 	 				sStackColGp.startsWith(YmCommonConst.YD_GP_3)){
				 	 				
				 	 				iSeq = dao.updateStockMoveEquipInfo(sSelStock,
							    								   		sStackColGp.substring(0, 1)+ "X"+
							   						    				sStackColGp.substring(2),
							    								   		sStackBedGp,
							    								   		sStackLayerGp,
							    								   		"",
							    								   		"");
				 	 						
				 	 			}else{
				 	 				
				 	 				iSeq = dao.updateStockMoveEquipInfo(sSelStock,
							    								   		sStackColGp.substring(0,1) + "X"+ 
																	  	sStackColGp.substring(2,4) + "0"+ 
																	  	sStackColGp.substring(4,5),
							    								   		sStackBedGp,
							    								   		sStackLayerGp,
							    								   		"",
							    								   		"");						
				 	 			}	
							}
							
				    		/* 
							 *	3.	적치단 FROM위치 초기화
							 *		tb_ym_stacklayer Table : stock_id = sStockId
							 *		tb_ym_stacklayer Table : stack_layer_stat= 'L'(예약상태)
							 */	
					    	iSeq = dao.updateCraneStackLayerStat(sStackColGp,
					    										 sStackBedGp,
					    										 sStackLayerGp,
					    										 sSelStock,
					    										 YmCommonConst.STACK_LAYER_STAT_L);
					    	logger.println(LogLevel.DEBUG, this, "작업예약취소=> 적치단 FROM위치 초기화");											 
					    }
					    
					    /**
		 	 			 *	4.	저장품 TABLE 작업예약 항목 삭제
		 	 			 */
					    iSeq = dao.updateStockWbookId_01(sSelStock); 
						logger.println(LogLevel.DEBUG, this, "작업예약취소=> 저장품 작업예약 항목 삭제");
						
						/**
		 	 			 *	5.	저장품 TABLE 저장품이동조건 셋팅
		 	 			 */
		 	 			String[] sStockInfo = null;
		 	 			 
		 	 			if (sStackColGp.startsWith(YmCommonConst.YD_GP_1)||
		 	 				sStackColGp.startsWith(YmCommonConst.YD_GP_3)){
		 	 				
		 	 				sStockInfo = YmCommonUtil.getCoilCurrProgCd(sSelStock,"");
		 	 						
		 	 			}else{
		 	 				
		 	 				sStockInfo = YmCommonUtil.getSlabCurrProgCd(sSelStock,"");
		 	 			}	
		 	 			String sProgCd   	= sStockInfo[0];
						String sStocMv   	= sStockInfo[1];
						    							  
						iSeq = dao.updateStockTransInfo(sSelStock,
												        sStocMv);	
						logger.println(LogLevel.DEBUG, this, "작업예약취소=> 저장품 저장품이동조건 셋팅="+sStocMv);
						
						iSeq = dao.updateStockPutLocWithStockId(sSelStock,"");  
						logger.println(LogLevel.DEBUG, this, "작업예약취소=> 저장품지정 예약위치 CLEAR =");
						
						/*
						 *	6.	SPM/FHL 보급취소일때 조업시스템으로 보급취소 전문  YMPO161 Send 처리 
						 */
						isSuccess = SPMHFLLineInCancel(sWbookId.trim(), sSelStock.trim());
						logger.println(LogLevel.DEBUG, this, "작업예약취소=>  보급취소 전문 처리");
					}
				}
			}
			
			/**
	 		 *	7.	작업예약정보 있으면 삭제
	 		 */
			if (iMaxRec > 1 && sSCH_WORK_KIND.equals("CVM")){
				
			}else{
	 		 iSeq = dao.deleteWbookInfo(sWbookId);
	 		 logger.println(LogLevel.DEBUG, this, "작업예약취소=> 작업예약 삭제="+iSeq);
			}
	 		
	 		 
	 		 isSuccess = true;
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
		return isSuccess;
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 야드, 동, 스케쥴 코드에 해당하는 작업예약 삭제하기
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */          	 
	public boolean setWorkIdAllCancle(String sYdGp,
    								  String sDong,
    								  String sSchCode)   {
		boolean isSuccess = false;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String sWbookId 	= "";
			String sSelStock	= "";
			
			/*
			 *	1.   작업예약 리스트 추출
	    	 */
	    	List stockL     = dao.getWbookListData(sYdGp,
	    									  	   sDong,
	    									  	   sSchCode);
			
			int iMaxRec     = stockL.size();
			int iSeq		= -1;
			
			if (iMaxRec > 0 ){
				for (int inx = 0; inx < iMaxRec; inx++){
					JDTORecord selStockJr = (JDTORecord) stockL.get(inx);
					
					sWbookId    = StringHelper.evl(selStockJr.getFieldString("WBOOK_ID"), "");
					sSelStock	= StringHelper.evl(selStockJr.getFieldString("STOCK_ID"), "");
					
		 	 		JDTORecord selLayerJr = dao.getStackLayerInfoWithStockId_02(sSelStock);
		 	 		
		 	 		String sStackColGp   = "";
		    		String sStackBedGp   = "";
		    		String sStackLayerGp = "";
		    		String sUpUsageCd    = "";
		    		
		 	 		if(selLayerJr != null){			
		 	 			
		 	 			sStackColGp   = StringHelper.evl(selLayerJr.getFieldString("STACK_COL_GP"), "");
			    		sStackBedGp   = StringHelper.evl(selLayerJr.getFieldString("STACK_BED_GP"), "");
			    		sStackLayerGp = StringHelper.evl(selLayerJr.getFieldString("STACK_LAYER_GP"), "");
			    		
			    		sUpUsageCd    = YmCommonUtil.getStackColInfoWithPk(sStackColGp);
						
				   		if(YmCommonConst.STACK_COL_USAGE_CD_CX.equals(sUpUsageCd)){// COIL 대차정지위치
						   	
						   	/**
							 *	대차위치 정보 셋팅
							 */ 
							if (sStackColGp.startsWith(YmCommonConst.YD_GP_1)||
			 	 				sStackColGp.startsWith(YmCommonConst.YD_GP_3)){
			 	 				
			 	 				iSeq = dao.updateStockMoveEquipInfo(sSelStock,
						    								   		sStackColGp.substring(0, 1)+ "X"+
						   						    				sStackColGp.substring(2),
						    								   		sStackBedGp,
						    								   		sStackLayerGp,
						    								   		"",
						    								   		"");
			 	 						
			 	 			}else{
			 	 				
			 	 				iSeq = dao.updateStockMoveEquipInfo(sSelStock,
						    								   		sStackColGp.substring(0,1) + "X"+ 
																  	sStackColGp.substring(2,4) + "0"+ 
																  	sStackColGp.substring(4,5),
						    								   		sStackBedGp,
						    								   		sStackLayerGp,
						    								   		"",
						    								   		"");						
			 	 			}	
			 	 				   		
						}
						
			    		/* 
						 *	3.	적치단 FROM위치 초기화
						 *		tb_ym_stacklayer Table : stock_id = sStockId
						 *		tb_ym_stacklayer Table : stack_layer_stat= 'L'(예약상태)
						 */	
				    	iSeq = dao.updateCraneStackLayerStat(sStackColGp,
				    										 sStackBedGp,
				    										 sStackLayerGp,
				    										 sSelStock,
				    										 YmCommonConst.STACK_LAYER_STAT_L);
				    	logger.println(LogLevel.DEBUG, this, "작업예약취소=> 적치단 FROM위치 초기화");											 
				    }
				    
				    /**
	 	 			 *	4.	저장품 TABLE 작업예약 항목 삭제
	 	 			 */
				    iSeq = dao.updateStockWbookId(sSelStock,
    							  				  ""); 
					logger.println(LogLevel.DEBUG, this, "작업예약취소=> 저장품 작업예약 항목 삭제");
					
					/**
	 	 			 *	5.	저장품 TABLE 저장품이동조건 셋팅
	 	 			 */
	 	 			String[] sStockInfo = null;
	 	 			 
	 	 			if (sStackColGp.startsWith(YmCommonConst.YD_GP_1)||
	 	 				sStackColGp.startsWith(YmCommonConst.YD_GP_3)){
	 	 				
	 	 				sStockInfo = YmCommonUtil.getCoilCurrProgCd(sSelStock,"");
	 	 						
	 	 			}else{
	 	 				
	 	 				sStockInfo = YmCommonUtil.getSlabCurrProgCd(sSelStock,"");
	 	 			}	
	 	 			
	 	 			String sProgCd   	= sStockInfo[0];
					String sStocMv   	= sStockInfo[1];
					    							  
					iSeq = dao.updateStockTransInfo(sSelStock,
											        sStocMv);	
					logger.println(LogLevel.DEBUG, this, "작업예약취소=> 저장품 저장품이동조건 셋팅="+sStocMv);
					
					iSeq = dao.updateStockPutLocWithStockId(sSelStock,"");  
					logger.println(LogLevel.DEBUG, this, "작업예약취소=> 저장품지정 예약위치 CLEAR =");
					
					/*
					 *	6.	SPM/FHL 보급취소일때 조업시스템으로 보급취소 전문  YMPO161 Send 처리 
					 */
					isSuccess = SPMHFLLineInCancel(sWbookId.trim(), sSelStock.trim());
					logger.println(LogLevel.DEBUG, this, "작업예약취소=>  보급취소 전문 처리");
					
					/**
			 		 *	7.	작업예약정보 있으면 삭제
			 		 */
			 		 iSeq = dao.deleteWbookInfo(sWbookId);
			 		 logger.println(LogLevel.DEBUG, this, "작업예약취소=> 작업예약 삭제="+iSeq);
			 		
				}
			}
			 
	 		isSuccess = true;
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
		return isSuccess;
	}			

	/**
	 * 오퍼레이션명 : 
	 *
	 * 작업예약취소 버튼 기능
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */          	      
    public boolean setWorkIdCancle_backup(String stock_id) {
		boolean isSuccess = false;
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		
		String WBook_Str = "";
		
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
    		/* 처리구분 : 작업예약취소
    		 * 1. 선택한 재료 No로 저장품 Table Select 
    		 *       Select WBOOK_ID From TB_YM_STOCK Where STOCK_ID = 재료No
    		 */   
		
		    logger.println(LogLevel.DEBUG,this,"Start-작업예약취소()");
		
			/* 수신한  Coil No가 저장품(TB_YM_STOCK) Table 에 존재 하는지 점검 
             * 저장품 Table에 작업예약_ID가 존재한다면 Error
             * Select STOCK_ID, WBOOK_ID from TB_YM_STOCK Where STOCK_ID = ?
             */ 
			String sStockQueryId   = "ym.steelinfo.steelinforecv.YdStockDAO.selectStockID";
			JDTORecord StockCoilNo = ydStockDAO.getData(sStockQueryId, new Object[]{ stock_id.trim() });

			if (StockCoilNo == null){
				throw new EJBServiceException("해당 Coil이 존재하지 않습니다. Error");
			}	

			String stockId = StringHelper.evl(StockCoilNo.getFieldString("STOCK_ID"), "");
			String wbookId = StringHelper.evl(StockCoilNo.getFieldString("WBOOK_ID"), "");
            
			logger.print(LogLevel.DEBUG,this, "stockId="+ stockId);
			
			List list = new ArrayList();
			list.add(stock_id.trim());
			boolean exsitCoilNo = ydStockDAO.isExistPrimaryKey(list);

			/*
			 * Schedule(TB_YM_SCH)에  해당하는  재료가 SCHEDULE 작업상태(SCH_WORK_STAT)가 
			 * 1:UP지시, 2:UP실적, 3:PUT지시 일경우  보급취소 불가
			 * 
			 * ym.facilitystatus.facilityinquiry.CraneSchDAO.selectWorkStockId 
			 * SELECT SCH_WORK_STAT
			 *   FROM TB_YM_SCH
			 *  WHERE STOCK_ID = ?
			 *    
			 */
			String scheduleQuery   = "ym.facilitystatus.facilityinquiry.CraneSchDAO.selectWorkStockId";
			JDTORecord schWorkStat = ydStackLayerDAO.requestgetData(scheduleQuery, new Object[]{ stock_id.trim() });

			if (schWorkStat == null){
				//throw new EJBServiceException("schWorkStat Error");
			}else{
				String SchStat = StringHelper.evl(schWorkStat.getFieldString("SCH_WORK_STAT"), "");
				
				if (SchStat.trim().equals(YmCommonConst.SCH_WORK_STAT_1) || 
					SchStat.trim().equals(YmCommonConst.SCH_WORK_STAT_2) || 
				    SchStat.trim().equals(YmCommonConst.SCH_WORK_STAT_3)){
					exsitCoilNo = false;
				}				
			}
			
			if(exsitCoilNo) {

    		    if (wbookId != null  && !wbookId.equals("")){
    		    	// 2.   예약ID에 해당하는  Coil No들 추출
    		    	//      Select STOCK_ID From TB_YM_STOCK Where WBOOK_ID = WBOOK_ID.trim()
    				String sListStockQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.selectListStockID";
    				List ListStockCoilNo     = ydStockDAO.getListData(sListStockQueryId, new Object[]{ wbookId.trim() });	    		    	
    				
    				JDTORecord TmpSelStock = null;
    				String SelStock        = null;
    				int MaxRec             = ListStockCoilNo.size();
					
    				if (MaxRec > 0 ){
						for (int ii=0; ii<MaxRec; ii++){
							TmpSelStock    = (JDTORecord) ListStockCoilNo.get(ii);
							SelStock       = StringHelper.evl(TmpSelStock.getFieldString("STOCK_ID"), "");
							
		    	    		// 2-1. 적치단  Table Update (적치단상태='L'로 변경:적치중)
		    	    		//      UPDATE TB_YM_STACKLAYER SET STACK_LAYER_STAT = 'L' WHERE STOCK_ID = ?
							String sStkLayerQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark";
							int stkColGp            = ydStackLayerDAO.requestupdateData(sStkLayerQueryId, new Object[]{ 
									                  YmCommonConst.STACK_LAYER_STAT_L, SelStock.trim() });
							
		    	    		// 2-2. SCHEDULE(TB_YM_SCH) Table Delete
		    	    		//      DELETE TB_YM_SCH WHERE STOCK_ID = ? 
							String sSchQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.deleteSchWBook";
							int delsch         = ydStackLayerDAO.requestdeleteData(sSchQueryId, new Object[]{ SelStock.trim() });

		    	    		// 2-3. 저장품(TB_YM_STOCK) Table Update
		    	    		//      UPDATE TB_YM_STOCK SET WBOOK_ID = null, STOCK_MOVE_TERM = null  WHERE STOCK_ID = ?								
							String stkQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateDeleteStockId";
							int stkId         = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ "", "", SelStock.trim() });

							WBook_Str = wbookId.trim();
							
							/*
							 * SPM/FHL 보급취소일때 조업시스템으로 보급취소 전문  YMPO161 Send 처리 
							 */
							SPMHFLLineInCancel(wbookId.trim(), SelStock.trim());
						}
    				}	
    		    	
    	    		/* 3. 작업예약(TB_YM_WBOOK) Table Delete
    	    		 *    DELETE TB_YM_WBOOK WHERE WBOOK_ID  = 추출 WBOOK_ID
					 *    String sdeleteQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.DeleteYdWBook";
					 *    int delsch = ydStackLayerDAO.requestdeleteData(sdeleteQueryId, new Object[]{ WBook_Str.trim() });
					 *    DELETE tb_ym_wbook	WHERE wbook_id = :wbook_id
					 */  
					int delsch = ydWBookDAO.deleteWbookInfo( WBook_Str.trim() );
					
					logger.println(LogLevel.DEBUG,this,"End-작업예약취소()");
	    		}	
	    	}
	    	
			isSuccess = true;
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
		    return isSuccess;
    }
    
    
	private boolean SPMHFLLineInCancel(String WBookID, String COILNO){
		boolean isSuccess = false;
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
	    	logger.println(LogLevel.DEBUG,this, "Start-SPMHFLLineInCancel()");

	    	/*
		     * A/B열연 SPM 보급(Line In)  : Schedule Code(CSLI)
		     * A/B열연 HFL 보급(Line In)  : Schedule Code(CHLI)
		     * 
	    	 * Select YD_GP, SCH_WORK_KIND From TB_YM_WBOOK 
	    	 *  Where WBOOK_ID = WBookID
	    	 */ 
			String selectWBookSchCode = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectWBookSchCode";
			JDTORecord selectwbookschcode  = ydWBookDAO.requestgetData(selectWBookSchCode, new Object[]{ WBookID });
			
			if (selectwbookschcode == null){
				return false;
			}
			
			String schworkkind  = StringHelper.evl(selectwbookschcode.getFieldString("SCH_WORK_KIND"), "");
			String YardID       = StringHelper.evl(selectwbookschcode.getFieldString("YD_GP"), "");

			/*
			 * 2007.03.19 이정훈
			 * SPM 보급 취소 또는  HFL 보급취소  일때만 해당 Method 처리한다.
			 * SPM Take-In 취소 
			 */
			if (schworkkind.trim().equals(YmCommonConst.NEW_SCH_WORK_KIND_CFLI)|| //HFL 보급
				schworkkind.trim().equals(YmCommonConst.NEW_SCH_WORK_KIND_CNLI)|| //SPM2 보급
				schworkkind.trim().equals(YmCommonConst.NEW_SCH_WORK_KIND_CNTI)||
				schworkkind.trim().equals(YmCommonConst.NEW_SCH_WORK_KIND_EQLI)|| //EQL 보급
				schworkkind.trim().equals(YmCommonConst.NEW_SCH_WORK_KIND_EQTI)||
				schworkkind.trim().equals(YmCommonConst.NEW_SCH_WORK_KIND_CKTI)|| //SPM 보급				
				schworkkind.trim().equals(YmCommonConst.NEW_SCH_WORK_KIND_CKLI)){
				
				/*
				 * 보급취소일 경우 예약항목값 "" 로 수정
				 */
				int iSeq = dao.updateStockSupplyGpWithStockId(COILNO,
															  "",
															  "");
					
				/*
				 * 보급취소 조업 YMPO161 Send
				 */
				YMPO161 model = new YMPO161();
				model.setTcCode(YmCommonConst.MODEL_YMPO161);
				model.setTcDate(YmCommonUtil.getCurDate("yyyy-MM-dd"));
				model.setTcTime(YmCommonUtil.getCurDate("HH-mm-ss"));
				/* 권하일자	CHAR(8)  yyyymmdd	*/
				model.setdownDate(YmCommonUtil.getCurDate("yyyyMMdd"));
				/* 권하시각     CHAR(6)  HHMMSS */
				model.setdownTime(YmCommonUtil.getCurDate("HHmmss"));
				/* 공장구분	CHAR(1)  A:A열연, B:B열연 */
				model.setplantGbn(YmCommonConst.YD_GP_1.equals(YardID)?YmCommonConst.YD_GP_A:YmCommonConst.YD_GP_B);
				/* 공정구분	CHAR(1)  H : Hot Filnal, S :  SkinPass	*/
				if (schworkkind.trim().equals(YmCommonConst.NEW_SCH_WORK_KIND_CKLI) || 
					schworkkind.trim().equals(YmCommonConst.NEW_SCH_WORK_KIND_CKTI)  		// Coil SPM 보급
					    ){       
					model.setprocGbn(YmCommonConst.WORK_SPM_S);				
				}else if (schworkkind.trim().equals(YmCommonConst.NEW_SCH_WORK_KIND_EQLI) || 
						schworkkind.trim().equals(YmCommonConst.NEW_SCH_WORK_KIND_EQTI)  		// Coil EQL 보급
			    ){       
					model.setprocGbn(YmCommonConst.WORK_SPM_E);				
				}else if (schworkkind.trim().equals(YmCommonConst.NEW_SCH_WORK_KIND_CNLI) ||		// Coil SPM2 보급 - 최규성
						  schworkkind.trim().equals(YmCommonConst.NEW_SCH_WORK_KIND_CNTI) ){
					model.setprocGbn(YmCommonConst.NEW_WORK_SPM_N);
				}else if (schworkkind.trim().equals(YmCommonConst.NEW_SCH_WORK_KIND_CFLI)){ //Coil HFL 보급
					model.setprocGbn(YmCommonConst.WORK_HFL_H);
				}else if (schworkkind.trim().equals(YmCommonConst.NEW_SCH_WORK_KIND_CHLI)){ //Coil SPM2HFL 보급
					model.setprocGbn(YmCommonConst.NEW_WORK_HFL_F);
				}
				/* COIL번호	CHAR(11) */
				model.setcoilNo(COILNO);
				/* 처리구분     CHAR(1)  1:보급,2:보급취소,3:추출,4:Take-Out,5:Take-In */
				model.setProcessId(YmCommonConst.PROCESS_ID_2);
				/* 위치포지션  CHAR(2) D1 */
				model.setpositionNo(YmCommonConst.PO_POSITION_D1);
				
				EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
				Boolean isTrue = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{CommonModel.class},new Object[]{ model });	
			}
			
			logger.println(LogLevel.DEBUG,this,"End-SPMHFLLineInCancel()");
			
	    	return isSuccess;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}
	/**
	 * 오퍼레이션명 : flex에 표시할 크레인 정보를 가져온다. Flex에서 호출함. 최규성
	 *
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */          
	public List getCoilYmBCraneStatFlex(HashMap param) throws EJBServiceException ,DAOException
	{
		/**/
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			String sQueryId="ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.getListCraneStatFlex";
			List listArgu = new ArrayList();
			List listArgu1 = new ArrayList();
    		YdStockDAO ydstockDAO = new YdStockDAO();
    		
    		listArgu.clear();
    		listArgu.add("3");
    		
    		listArgu1 = ydstockDAO.getListData(sQueryId, listArgu);
    		logger.println(LogLevel.DEBUG,this,"List내용:"+listArgu1);
    		
    		return listJdtoRecordTohashMap(listArgu1);
    		
    	}catch(DAOException daoe){
    		throw daoe;
    	}catch(Exception e){
    		throw new EJBServiceException(e);
    	}
	
	}
	/**
	 * 오퍼레이션명 : 야드모니터링 화면에서 호출하여 사용함.
	 *
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */          
	public List getCoilYmBCraneStatFlex(String sQueryId, List listArgu) throws EJBServiceException ,DAOException
	{
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		YdStockDAO ydstockDAO = new YdStockDAO();
		return ydstockDAO.getListData(sQueryId, listArgu);
	}
	
    //List의 JDTORecord를 HashMap으로 변환한다.
	// 최규성 2009-10-05
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
    
    //해쉬맵의 내용을 JDTORecord로 담는다.
	// 최규성 2009-10-05
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
    // NULL 값 검사.
	// 최규성 2009-10-05
    public static String nvl(Object o, String defaultValue) {
        return (o == null) ? defaultValue.trim() : o.toString().trim();
    }

	/**
	 * 플렉스 화면 - Push Test
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param HashMap
	 *            param
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public List pushYdTest2(HashMap param) {
		
		EJBConnector ejbConn1 = null;		
		EJBConnector ejbConn2 = null;
		ArrayList pushData  = new ArrayList();
		
		pushData.add(param.get("MSG"));

		try {


    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return null;
    		}
    		
			String dest = "yd_monitor3";//(String)param.get("DEST");
			
			//15 was SE EJB call
			ejbConn1 = new EJBConnector("default", "JNDIYMLog", this);//Class를 지정 해 주고
			
			ejbConn1.trx("pushToFlexClient2"
								  , new Class[] { String.class, Object.class }
								  //, new Object[] { dest,  pushData}
								  , new Object[] { dest,  param}
						);
	

			return new ArrayList();

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {
		}
	}
	
	/**
	 * 플렉스 화면 - Push Test
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param HashMap
	 *            param
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public boolean ModifyCrStatInfo(String sEquipStat, String sEquipGp) {
		
		boolean isSuccess = false;
		logger.println(LogLevel.DEBUG,this,"Start-ModifyCrStatInfo()");
		
		try{

    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return false;
    		}
    		
			int nRetVal = dao.UpdateEquipStatInfo(sEquipStat, sEquipGp);
			logger.println(LogLevel.DEBUG,this,"End-ModifyCrStatInfo()");
			
			isSuccess = true;
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
		
		return isSuccess;
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 야드 Level-2로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */      
	public boolean receiveYmCrnCurrLoc(String sMessage) {
		logger.println(LogLevel.DEBUG, this, "### BCY_CRANE 운전 ModeOn/Off 실적 처리");
	    try {

    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return false;
    		}
    		
            JDTORecord parseData = new Level2Parser().parse(sMessage);
            logger.println(LogLevel.DEBUG, this, parseData);
	        procYmCrnCurrLoc(parseData);
	    }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
	    return true;
	}
	
	
	/**
	 * 오퍼레이션명 : 크레인현재위치. 최규성				
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */		
	public void procYmCrnCurrLoc(JDTORecord msgRecord) {
		// 레코드 선언
        JDTORecord getParamRecord  = null;
        JDTORecord setCrnschRecord = null;

		
        // 변수 선언
    	String szMethodName        = "procYmCrnCurrLoc";
		String szMsg               = "";
		String szOperationName     = "B열연Coil L2 크레인현재위치";
		//String szRcvTcCode         = ydUtils.getTcCode(msgRecord);
        String szYD_EQP_ID         = "";
        String szYD_CRN_XAXIS      = "";
        String szYD_CRN_YAXIS      = "";
		int nRtnVal                = 0;

		try{
			

    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return;
    		}
    		
        	//=============================================================
        	// Log 테이블 등록 
        	//=============================================================
        	szMsg = "[B열연Coil] 크레인현재위치 수신";
       
        	getParamRecord  = JDTORecordFactory.getInstance().create();
    		setCrnschRecord = JDTORecordFactory.getInstance().create();
        	
			// 파라미터 Check
			
//			nRtnVal = this.paramY1YDL005Check(msgRecord, getParamRecord, 0);
//	        if(nRtnVal == -1) {
//                szMsg = "파라미터 Check중 Error	: " + nRtnVal;
//                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//                return ;
//	        }       

    		//=================================================================
	        // 수신받은 크레인 위치정보 화면으로 호출
	        //=================================================================
    		logger.println(LogLevel.DEBUG, this, "B열연Coil 크레인현재위치 화면처리 위한 호출");
			for(int i=0; i<16; i++){
//				szYD_EQP_ID    = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID" + (i+1));
//				szYD_CRN_XAXIS = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_XAXIS" + (i+1));
//				szYD_CRN_YAXIS = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_YAXIS" + (i+1));

				szYD_EQP_ID    = msgRecord.getFieldString("YD_EQP_ID" + String.valueOf(i+1));
				szYD_CRN_XAXIS = msgRecord.getFieldString("YD_CRN_XAXIS" + String.valueOf(i+1));
				szYD_CRN_YAXIS = msgRecord.getFieldString("YD_CRN_YAXIS" + String.valueOf(i+1));

				// 존재하는 설비ID에 대한 위치정보 Flex화면에서 보여지기 위한 호출
				if(!szYD_EQP_ID.trim().equals("")){
					this.putYmFlexCrnPos( 
											 YmCommonConst.YD_MONITORING_CHANNEL_3,
											 "3", 
											 szYD_EQP_ID, 
											 Integer.parseInt(szYD_CRN_XAXIS), 
											 Integer.parseInt(szYD_CRN_YAXIS)
										   );
				}
			}
        }catch(Exception e){
            szMsg = "Error : " + e.getLocalizedMessage();
            logger.println(LogLevel.DEBUG, this, szMsg);
			return ;
        } 
	} 
	/**
	 * 오퍼레이션명 : 크레인 위치 정보 전송(Flex)
	 * 
	 * @param String destid	        // Monitoring Channel[yd_monitor3]
	 *        String szYdGp			// 야드구분
	 *        String szCrnName 		// 크레인 설비명
	 *        int intPosX			// 크레인 현 X 좌표
	 *        int intPosY			// 크레인 현 Y 좌표
	 * @return
	 * @throws DAOException, JDTOException
	 */
	public void putYmFlexCrnPos(String destid, String szYdGp , String szCrnName , int intPosX, int intPosY)  {
		
		/*
		 * 1. 기능 : 크레인 위치 정보를 받아 선택된 채널 정보에 위치정보를 전송하는 기능
		 * 2. 작성자  : 이현성
		 * 3. 작성일시 : 2009.12.31
		 * 
		 */
		
		String szMsg="";	
		HashMap hmap = new HashMap();
		String szOperationName = "크레인 위치 정보 전송(Flex)_YM";
		String szMethodName = "putYmFlexCrnPos";
		
		try{
			

    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return ;
    		}
    		
//			if(destid.equals("")){
//				destid = YmCommonConst.YD_MONITORING_CHANNEL_3;
//			}
			
			hmap.put("MSG_GP", YmCommonConst.YD_EVT_CRANE);  // Flex 크레인 좌표받는 메세지 구분
			hmap.put("YD_GP", szYdGp); 
			hmap.put("YD_EQP_ID", szCrnName);
			hmap.put("YD_POS_X", new Integer(intPosX));
			hmap.put("YD_POS_Y", new Integer(intPosY));
			
			szMsg	= "[YmFlex : "+szOperationName+"] 채널 전송";
			logger.println(LogLevel.DEBUG, this, szMsg);
			
			//FLAX_PUSH사용여부 체크 /////////////////////////////////////////////////
		 	JDTORecord recPara =  JDTORecordFactory.getInstance().create();
	    	JDTORecord recInTemp =  JDTORecordFactory.getInstance().create();
	    	String CHK ="N";
			JDTORecordSet outRecSet = null;
			YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
			
	    	outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			recPara =  JDTORecordFactory.getInstance().create();
			recPara.setField("YD_GP",szYdGp);
			/*com.inisteel.cim.yd.common.util.YdUtils.chklist*/
			int intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 999);
			
			if( intRtnVal > 0 ) {
				outRecSet.first();
				recInTemp = outRecSet.getRecord();
				CHK = recInTemp.getFieldString("CHK").trim();
			}
			
			logger.println(LogLevel.INFO, this, szYdGp+":야드 FLAX PUSH 사용유무:"+CHK);
			/////////////////////////////////////////////////////////////////////////
	    	if(CHK.equals("Y")){
			YmCommonUtil.pushToFlexClient(YmCommonConst.YD_MONITORING_CHANNEL_3, hmap);
	    	}
			
		}catch (Exception e){
			
			szMsg	= "[YdUtils : "+szOperationName+"] Exception Error : "+ e.getLocalizedMessage();
			logger.println(LogLevel.DEBUG, this, szMsg+":"+szMethodName);
				
		} // end of try-catch()
		
	} // end of putYdCrnPos
	
	/**
     * B열연 야드맵 정보 l2 전송
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param add	야드맵정보
	 * @param gp	요청/응답 구분
     */
	public void sendLayerMap(String isFirstView ,String State,  String ColGp, String BedGp , String LayerGp , String sellayer , String State2) {
		// 변수 선언
    	String szMethodName        = "sendLayerMap";
		String szMsg               = "";
		String szOperationName     = "B열연 야드맵 정보 l2 전송";
		 
	    try {

    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return;
    		}
    		
	    	//단별 건단위변경
	    	if (isFirstView.equals("1")){

	    		String sQueryId1 = "";
	    		sQueryId1    = "ym.common.YmCommonDB.setStackLayerStateInfo";
	    		int bedList = ymCommonDAO.updateData(sQueryId1,new Object[]{State,State2,ColGp,BedGp,LayerGp});
	    		
	    		EJBConnector ejbConn4 = new EJBConnector("default","JNDICraneStatusReg",this);
	    		ejbConn4.trx("sendResponse",new Class[]{String.class, String.class },
	    								 	new Object[]{ColGp+BedGp+LayerGp , "CN1PB11"});
	    	}
	    	//전체 일괄변경
	    	if (isFirstView.equals("2")){

	    		String sQueryId1 = "";
	    		sQueryId1    = "ym.common.YmCommonDB.setStackLayerStateInfo2";
	    		int bedList = ymCommonDAO.updateData(sQueryId1,new Object[]{State,State2,ColGp});
	    		
	    		EJBConnector ejbConn2 = new EJBConnector("default","JNDICraneStatusReg",this);
	    		ejbConn2.trx("sendResponse",new Class[]{String.class, String.class },
	    								 	new Object[]{ColGp , "CN1PB11"});
	    	}
	    	//단별 일괄변경
	    	if (isFirstView.equals("3")){

	    		String sQueryId1 = "";
	    		sQueryId1    = "ym.common.YmCommonDB.setStackLayerStateInfo3";
	    		int bedList = ymCommonDAO.updateData(sQueryId1,new Object[]{State,State2,ColGp,sellayer});
	    		
	    		EJBConnector ejbConn3 = new EJBConnector("default","JNDICraneStatusReg",this);
	    		ejbConn3.trx("sendResponse",new Class[]{String.class, String.class },
	    								 	new Object[]{ColGp , "CN1PB11"});
	    	}
	    	return ;
	    	
	    }catch(Exception e){
            szMsg = "Error : " + e.getLocalizedMessage();
            logger.println(LogLevel.DEBUG, this, szMsg);
			return ;
        }
	    
	}

}

