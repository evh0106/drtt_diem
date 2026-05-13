package com.inisteel.cim.ym.facilitywork.putwrecord.session;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import com.inisteel.cim.common.util.CommonUtil;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.yd.common.dao.ydCarFtmvMtlDao.YdCarFtmvMtlDao;
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO;
import com.inisteel.cim.ym.steelinfo.steelinforecv.dao.YdStockDAO;
import com.inisteel.cim.ym.workrequest.wrequestaccept.dao.YdWBookDAO;
import com.inisteel.cim.common.jms.model.CommonModel;
import com.inisteel.cim.common.jms.model.dm.*;
import com.inisteel.cim.common.jms.model.po.*;
import com.inisteel.cim.common.jms.model.ps.*;
import com.inisteel.cim.common.jms.model.pc.ZZPC001;
import com.inisteel.cim.common.level2.util.*;
import com.inisteel.cim.common.parser.Level2Parser;
import com.inisteel.cim.ym.bcommon.dao.YmCommDAO;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.common.YmCommonDB;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
import com.inisteel.cim.common.eai.EAIHttpSender;

import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.ydStock.StockSpecReg.CoilSpecRegSeEJBBean;

import javax.naming.*;

import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.*;
import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;
/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="CPutResRegEJB" jndi-name="JNDICPutResReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class CPutResRegSBean extends BaseSessionBean {

	private Logger logger = null;
	private CraneSchDAO dao = null;
	private YmComm ymComm = new YmComm();
	private YmCommDAO commDao = new YmCommDAO();
	
	//EAI HTTP 전송객체  취득
	YdDelegate      ydDelegate      = new YdDelegate();
	public void ejbCreate() {
		LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		logger 	= new Logger(config);
		dao 	= new CraneSchDAO();
	}
	 
       /**
	 * 오퍼레이션명 : 
	 *
	 * Crane 작업자가 외부인터페이스(JMS)를 통해 Crane Put 실적을 발생시킨다.
	 *
	 * YM-AIF-008	CRANE 권하 실적	Level2	Level3	THCH570
	 * 전문코드							CHAR	07	전문코드 
	 * CRANE 번호						CHAR	04	CRANE번호	     
	 * SPARE1							CHAR	04	SPARE1   	     
	 * 발생일							CHAR	06	발생일   	YYMMDD
	 * 발생시							CHAR	06	발생시   	HHMMSS
	 * 권하위치							CHAR	08	권하위치 	     
	 * 권하 X 위치						CHAR	06	권하X위치	     
        * 		Y 위치						CHAR	06	Y위치    	     
	 * SPARE2							CHAR	103	SPARE2   	     
	 *                                              
	 * YM-AIF-008	CRANE 권하 이상 실적	Level2	Level3	THCH580
	 * 위와 상동
	 * 구 권하 위치						CHAR	08 구권하위치      
	 * 신규 권하이상위치				CHAR	08 신규권하이상위치
	 * 권하 X 위치						CHAR	06 권하X위치       
        *  	Y 위치						CHAR	06 Y위치           
	 * SPARE2							CHAR	95 SPARE2          
	 *
	 * YM-BIF-006	CRANE 권하 실적	Level2	Level3	CN1PB06
	 * 전문코드			TC				CHAR	07	전문코드  	
	 * 발생일자			Date			CHAR	10	발생일자  	YYYY-MM-DD
	 * 발생시간			Time			CHAR	08	발생시간  	HH-MM-SS
	 * 전문구분			Form			CHAR	01	전문구분  	I  : Initialize, U : Update,D : Delete,   R : Re-request
	 * 전문길이			Message_Length	CHAR	04	전문길이  	
	 * 야드구분			Yard_Id			CHAR	01	야드구분  	
	 * 동구분			Bay_GP			CHAR	01	동구분    	
	 * 설비종류			Equip_Kind		CHAR	02	설비종류  	
	 * 설비번호			Equip_No		CHAR	02	설비번호  	
	 * Coil No			Coil_No			CHAR	10	COILNO    	
	 * Schedule Code	Sch_Code		CHAR	04	SCHCODE   	
	 * 주작업구분		Work_Id			CHAR	02	주작업구분	01: 주작업. 02: Dummy 작업
	 * 권하위치			Put_Position	CHAR	10	권하위치  	
	 * 권하 X 위치		Put_X_Position	CHAR	06	권하X위치 	
	 * 권하 Y 위치		Put_Y_Position	CHAR	06	권하Y위치 	
        *                                                  
        * YM-BIF-006	CRANE 권하 이상 실적	Level2	Level3	CN1PB07
	 * 위와 상동
	 * 작업지시 위치	WorkOrder_Position	CHAR	10 작업지시위치
        *
        * YM-BIF-026	CRANE 권하 실적	Level2	Level3	CM1PB06
	 * 전문코드			TC				CHAR	07	전문코드    	
	 * 발생일자			Date			CHAR	10	발생일자    	YYYY-MM-DD
	 * 발생시간			Time			CHAR	08	발생시간    	HH-MM-SS
	 * 전문구분			Form			CHAR	01	전문구분    	I  : Initialize, U : Update,D : Delete,   R : Re-request
	 * 전문길이			Message_Length	CHAR	04	전문길이    	
	 * 야드구분			Yard_Id			CHAR	01	야드구분    	
	 * 동구분			Bay_GP			CHAR	01	동구분      	
	 * 설비종류			Equip_Kind		CHAR	02	설비종류    	
	 * 설비번호			Equip_No		CHAR	02	설비번호    	
	 * Slab No			Slab_No			CHAR	10	SlabNo      	
	 * Schedule Code	Sch_Code		CHAR	04	ScheduleCode	
	 * 주작업구분		Work_Id			CHAR	02	주작업구분  	01: 주작업. 02: Dummy 작업
	 * 권하위치			Put_Position	CHAR	10	권하위치    	
	 * 권하 X 위치		Put_X_Position	CHAR	06	권하X위치   	
	 * 권하 Y 위치		Put_Y_Position	CHAR	06	권하Y위치   	
	 *
	 * YM-BIF-026	CRANE 권하 이상 실적	Level2	Level3	CM1PB07
	 * 위와 상동
	 * 작업지시 위치	WorkOrder_Position	CHAR	10 작업지시위치
	 *
	 * 2007-02-26 A열연 SLAB야드 추가
	 * YM-BIF-027	CRANE 권하 실적	Level2	Level3	HM1PB06
	 * 전문코드			TC				CHAR	07	전문코드    	
	 * 발생일자			Date			CHAR	10	발생일자    	YYYY-MM-DD
	 * 발생시간			Time			CHAR	08	발생시간    	HH-MM-SS
	 * 전문구분			Form			CHAR	01	전문구분    	I  : Initialize, U : Update,D : Delete,   R : Re-request
	 * 전문길이			Message_Length	CHAR	04	전문길이    	
	 * 야드구분			Yard_Id			CHAR	01	야드구분    	
	 * 동구분				Bay_GP			CHAR	01	동구분      	
	 * 설비종류			Equip_Kind		CHAR	02	설비종류    	
	 * 설비번호			Equip_No		CHAR	02	설비번호    	
	 * Slab No			Slab_No			CHAR	10	SlabNo      	
	 * Schedule Code	Sch_Code		CHAR	04	ScheduleCode	
	 * 주작업구분			Work_Id			CHAR	02	주작업구분  	01: 주작업. 02: Dummy 작업
	 * 권하위치			Put_Position	CHAR	10	권하위치    	
	 * 권하 X 위치		Put_X_Position	CHAR	06	권하X위치   	
	 * 권하 Y 위치		Put_Y_Position	CHAR	06	권하Y위치   	
	 *
	 * YM-BIF-028	CRANE 권하 이상 실적	Level2	Level3	HM1PB07
	 * 위와 상동
	 * 작업지시 위치	WorkOrder_Position	CHAR	10 작업지시위치
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
	public boolean callCranePutRtInfo(String sMessage){
		
		boolean isSuccess = false;
				
	    try{
	    	
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
	    	Level2Parser level2Parser 	= new Level2Parser();
			JDTORecord jtR = level2Parser.parse(sMessage);
			
			JDTORecord rRd = JDTORecordFactory.getInstance().create();
			
			String sTC 	= StringHelper.evl(jtR.getFieldString("전문코드"), "");
			String sForm 	= StringHelper.evl(jtR.getFieldString("전문구분"), "");
			
			if("R".equals(sForm)){
				sForm = YmCommonConst.CRANE_FUNC_L;
			}else{
				sForm = YmCommonConst.CRANE_FUNC_N;
			} 
			
			if(YmCommonConst.TC_THCH570.equals(sTC)){//A열연 Coil 권하실적
				
				String sLegacyCraneNo 	= StringHelper.evl(jtR.getFieldString("CRANE번호"), "");
				String sPut_Position 	= StringHelper.evl(jtR.getFieldString("권하위치"), "");
				
				JDTORecord crnRc  = null;
				JDTORecord schRc  = null;
		    	
				/*
				 * A열연 Legacy Crane No를 가지고 현재 Crane No를 가져온다.
				 */
				crnRc = dao.getCurEquipNoWithLegacyEquipNo(sLegacyCraneNo);
				if(crnRc == null){
	    			throw new EJBServiceException("=권하실적=>A열연 레거시 크레인정보 존재안함.");
	    		}
	    		/*
				 * A열연 권하실적 전문에는 Crane No 정보만 존재.
				 * Crane No를 권하실적에 필요한 정보를 SCH, EQUIP 
				 * TABLE에서 가져온다.
				 */		
		    	schRc = dao.getSchInfoWithEquipNo(YmCommonConst.YD_GP_1,
		    									  YmCommonConst.WORK_PROG_STAT_3,//PUT지시		
		    			                          StringHelper.evl(crnRc.getFieldString("CRANE_NO"), ""));
		    	if(schRc == null){
		    		isSuccess = sendWorkFinishInfo_Coil(sLegacyCraneNo,
		    											YmCommonConst.RESULT_MODE_1);
	    			throw new EJBServiceException("=권하실적=>A열연 스케쥴정보 존재안함(THHC200응답전문 상태를 확인 하세요).");
	    		}
	    		
	    		String sYdGp  = StringHelper.evl(schRc.getFieldString("YD_GP"), "");
	    		String sBayGp = StringHelper.evl(schRc.getFieldString("BAY_GP"), "");
	 			rRd.setField("TC"				, StringHelper.evl(jtR.getFieldString("전문코드"), ""));
				rRd.setField("Yard_Id"			, sYdGp);
				rRd.setField("Bay_GP"			, sBayGp);
				rRd.setField("Equip_Kind"		, StringHelper.evl(schRc.getFieldString("EQUIP_KIND"), ""));
				rRd.setField("Equip_No"			, StringHelper.evl(schRc.getFieldString("EQUIP_NO"), ""));
				rRd.setField("Coil_No"			, StringHelper.evl(schRc.getFieldString("STOCK_ID"), ""));
				rRd.setField("Sch_Code"			, StringHelper.evl(schRc.getFieldString("SCH_WORK_KIND"), ""));
				rRd.setField("Work_Id"			, StringHelper.evl(schRc.getFieldString("SCH_WORK_AID_YN"), ""));
				rRd.setField("Put_Position"		, YmCommonUtil.setCurPositionWithLegacy(sPut_Position,sYdGp,sBayGp));
				rRd.setField("Put_X_Position"	, StringHelper.evl(jtR.getFieldString("권하X위치"), ""));
				rRd.setField("Put_Y_Position"	, StringHelper.evl(jtR.getFieldString("Y위치"), ""));
				/**
				 * 이후 처리를 위해 필요한 값 셋팅
				 */
				rRd.setField("LEGACY_CRANE_NO", sLegacyCraneNo);
				
			}else if(YmCommonConst.TC_THCH580.equals(sTC)){//A열연 Coil 권하 이상 실적	
				
				String sLegacyCraneNo 	= StringHelper.evl(jtR.getFieldString("CRANE번호"), "");
				/**
				 * 권하이상실적 발생시 신규권하위치(적치정보)는 줄 수 없슴
				 * X,Y 좌표값으로 적치정보 찾는 모듈을 만들어야 함
				 */
				String sPut_Position 	= StringHelper.evl(jtR.getFieldString("신규권하이상위치"), "");
				
				JDTORecord crnRc  = null;
				JDTORecord schRc  = null;
		    	
				/*
				 * A열연 Legacy Crane No를 가지고 현재 Crane No를 가져온다.
				 */
				crnRc = dao.getCurEquipNoWithLegacyEquipNo(sLegacyCraneNo);
				if(crnRc == null){
	    			throw new EJBServiceException("=권하실적=>A열연 레거시 크레인정보 존재안함.");
	    		}
	    		/*
				 * A열연 권하실적 전문에는 Crane No 정보만 존재.
				 * Crane No를 권하실적에 필요한 정보를 SCH, EQUIP 
				 * TABLE에서 가져온다.
				 */		
		    	schRc = dao.getSchInfoWithEquipNo(YmCommonConst.YD_GP_1,
		    									  YmCommonConst.WORK_PROG_STAT_3,//PUT지시	
		    			                          StringHelper.evl(crnRc.getFieldString("CRANE_NO"), ""));
		    	if(schRc == null){
		    		isSuccess = sendWorkFinishInfo_Coil(sLegacyCraneNo,
		    											YmCommonConst.RESULT_MODE_1);
	    			throw new EJBServiceException("=권하실적=>A열연 스케쥴정보 존재안함(THHC200응답전문 상태를 확인 하세요).");
	    		}
	    		
	    		String sYdGp  = StringHelper.evl(schRc.getFieldString("YD_GP"), "");
	    		String sBayGp = StringHelper.evl(schRc.getFieldString("BAY_GP"), "");
	 			rRd.setField("TC"				, StringHelper.evl(jtR.getFieldString("전문코드"), ""));
				rRd.setField("Yard_Id"			, sYdGp);
				rRd.setField("Bay_GP"			, sBayGp);
				rRd.setField("Equip_Kind"		, StringHelper.evl(schRc.getFieldString("EQUIP_KIND"), ""));
				rRd.setField("Equip_No"			, StringHelper.evl(schRc.getFieldString("EQUIP_NO"), ""));
				rRd.setField("Coil_No"			, StringHelper.evl(schRc.getFieldString("STOCK_ID"), ""));
				rRd.setField("Sch_Code"			, StringHelper.evl(schRc.getFieldString("SCH_WORK_KIND"), ""));
				rRd.setField("Work_Id"			, StringHelper.evl(schRc.getFieldString("SCH_WORK_AID_YN"), ""));
				rRd.setField("Put_Position"		, YmCommonUtil.setCurPositionWithLegacy(sPut_Position,sYdGp,sBayGp));
				rRd.setField("Put_X_Position"	, StringHelper.evl(jtR.getFieldString("권하X위치"), ""));
				rRd.setField("Put_Y_Position"	, StringHelper.evl(jtR.getFieldString("Y위치"), ""));
				
				rRd.setField("WorkOrder_Position", YmCommonUtil.setCurPositionWithLegacy(
												   StringHelper.evl(jtR.getFieldString("구권하위치"), ""),sYdGp,sBayGp));
				/**
				 * 이후 처리를 위해 필요한 값 셋팅
				 */
				rRd.setField("LEGACY_CRANE_NO", sLegacyCraneNo);
				
			}else if(YmCommonConst.TC_CN1PB06.equals(sTC)){  //B열연 Coil 권하실적
				
				rRd.setField("TC"				, StringHelper.evl(jtR.getFieldString("전문코드"), ""));
				rRd.setField("Yard_Id"			, StringHelper.evl(jtR.getFieldString("야드구분"), ""));
				rRd.setField("Bay_GP"			, StringHelper.evl(jtR.getFieldString("동구분"), ""));
				rRd.setField("Equip_Kind"		, StringHelper.evl(jtR.getFieldString("설비종류"), ""));
				rRd.setField("Equip_No"			, StringHelper.evl(jtR.getFieldString("설비번호"), ""));
				rRd.setField("Coil_No"			, StringHelper.evl(jtR.getFieldString("COILNO"), ""));
				rRd.setField("Sch_Code"			, StringHelper.evl(jtR.getFieldString("SCHCODE"), ""));
				rRd.setField("Work_Id"			, StringHelper.evl(jtR.getFieldString("주작업구분"), ""));
				rRd.setField("Put_Position"		, StringHelper.evl(jtR.getFieldString("권하위치"), ""));
				rRd.setField("Put_X_Position"	, StringHelper.evl(jtR.getFieldString("권하X위치"), ""));
				rRd.setField("Put_Y_Position"	, StringHelper.evl(jtR.getFieldString("권하Y위치"), ""));
			
			}else if(YmCommonConst.TC_CN1PB07.equals(sTC)){//B열연 Coil 권하 이상 실적	
				
				/**
				 * 권하이상실적 발생시 신규권하위치(적치정보)는 줄 수 없슴
				 * X,Y 좌표값으로 적치정보 찾는 모듈을 만들어야 함
				 */
				String sPut_Position 	= StringHelper.evl(jtR.getFieldString("권하위치"), "");
				
				rRd.setField("TC"				, StringHelper.evl(jtR.getFieldString("전문코드"), ""));
				rRd.setField("Yard_Id"			, StringHelper.evl(jtR.getFieldString("야드구분"), ""));
				rRd.setField("Bay_GP"			, StringHelper.evl(jtR.getFieldString("동구분"), ""));
				rRd.setField("Equip_Kind"		, StringHelper.evl(jtR.getFieldString("설비종류"), ""));
				rRd.setField("Equip_No"			, StringHelper.evl(jtR.getFieldString("설비번호"), ""));
				rRd.setField("Coil_No"			, StringHelper.evl(jtR.getFieldString("COILNO"), ""));
				rRd.setField("Sch_Code"			, StringHelper.evl(jtR.getFieldString("SCHCODE"), ""));
				rRd.setField("Work_Id"			, StringHelper.evl(jtR.getFieldString("주작업구분"), ""));
				rRd.setField("Put_Position"		, sPut_Position);
				rRd.setField("Put_X_Position"	, StringHelper.evl(jtR.getFieldString("권하X위치"), ""));
				rRd.setField("Put_Y_Position"	, StringHelper.evl(jtR.getFieldString("권하Y위치"), ""));
				
				rRd.setField("WorkOrder_Position", StringHelper.evl(jtR.getFieldString("작업지시위치"), ""));
				
			}else if(YmCommonConst.TC_CM1PB06.equals(sTC)|| 	//B열연 Slab 권하실적 
					 YmCommonConst.TC_HM1PB06.equals(sTC)||		//A_A열연 SLAB 권하실적 (2007-02-26(MCH)추가)
					 YmCommonConst.TC_HM1PB56.equals(sTC)){	 	//A_B열연 SLAB 권하실적 (2007-02-26(MCH)추가)
				
				rRd.setField("TC"				, StringHelper.evl(jtR.getFieldString("전문코드"), ""));
				rRd.setField("Yard_Id"			, StringHelper.evl(jtR.getFieldString("야드구분"), ""));
				rRd.setField("Bay_GP"			, StringHelper.evl(jtR.getFieldString("동구분"), ""));
				rRd.setField("Equip_Kind"		, StringHelper.evl(jtR.getFieldString("설비종류"), ""));
				rRd.setField("Equip_No"			, StringHelper.evl(jtR.getFieldString("설비번호"), ""));
				rRd.setField("Slab_No"			, StringHelper.evl(jtR.getFieldString("SlabNo"), ""));
				rRd.setField("Sch_Code"		, StringHelper.evl(jtR.getFieldString("ScheduleCode"), ""));
				rRd.setField("Work_Id"			, StringHelper.evl(jtR.getFieldString("주작업구분"), ""));
				rRd.setField("Put_Position"		, StringHelper.evl(jtR.getFieldString("권하위치"), ""));
				rRd.setField("Put_X_Position"		, StringHelper.evl(jtR.getFieldString("권하X위치"), ""));
				rRd.setField("Put_Y_Position"		, StringHelper.evl(jtR.getFieldString("권하Y위치"), ""));
				rRd.setField("TC_GBN"			, StringHelper.evl(jtR.getFieldString("전문구분"), ""));

			}else if(YmCommonConst.TC_CM1PB07.equals(sTC)|| //B열연 Slab 권하 이상 실적	
					YmCommonConst.TC_HM1PB07.equals(sTC)||	//A열연 A_A SLAB 권하 이상 실적 (2007-02-26(MCH)추가)
					YmCommonConst.TC_HM1PB57.equals(sTC)){	//A열연 A_B SLAB 권하 이상 실적 (2007-02-26(MCH)추가))
				
				/**
				 * 권하이상실적 발생시 신규권하위치(적치정보)는 줄 수 없슴
				 * X,Y 좌표값으로 적치정보 찾는 모듈을 만들어야 함
				 */
				String sPut_Position 	= StringHelper.evl(jtR.getFieldString("권하위치"), "");
				
				rRd.setField("TC"				, StringHelper.evl(jtR.getFieldString("전문코드"), ""));
				rRd.setField("Yard_Id"			, StringHelper.evl(jtR.getFieldString("야드구분"), ""));
				rRd.setField("Bay_GP"			, StringHelper.evl(jtR.getFieldString("동구분"), ""));
				rRd.setField("Equip_Kind"		, StringHelper.evl(jtR.getFieldString("설비종류"), ""));
				rRd.setField("Equip_No"			, StringHelper.evl(jtR.getFieldString("설비번호"), ""));
				rRd.setField("Slab_No"			, StringHelper.evl(jtR.getFieldString("SlabNo"), ""));
				rRd.setField("Sch_Code"		, StringHelper.evl(jtR.getFieldString("ScheduleCode"), ""));
				rRd.setField("Work_Id"			, StringHelper.evl(jtR.getFieldString("주작업구분"), ""));
				rRd.setField("Put_Position"		, sPut_Position);
				rRd.setField("Put_X_Position"		, StringHelper.evl(jtR.getFieldString("권하X위치"), ""));
				rRd.setField("Put_Y_Position"		, StringHelper.evl(jtR.getFieldString("권하Y위치"), ""));
				
				rRd.setField("WorkOrder_Position", StringHelper.evl(jtR.getFieldString("작업지시위치"), ""));
			}
			
	    	isSuccess = callCranePutRtInfo(rRd,
	    							   sForm);
	    	
	    	return isSuccess;
	    	
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}
	
      /**
	 * 오퍼레이션명 : 야드운영자가 Schedule관리기능을 통해 Crane PUT 실적을 발생시킨다.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
       public boolean callCranePutRtInfo(String sTC
								, String sSchId
								, String sPutPosition){
    	   
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
		return callCranePutRtInfo(sTC,sSchId,sPutPosition,"");										
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     									
	public boolean callCranePutRtInfo(String sTC
								, String sSchId
								, String sPutPosition
								, String sUserId){
		
		boolean isSuccess = false;
		
		logger.println(LogLevel.DEBUG,this, "화면처리 권하TC    ="+sTC+"=");
		logger.println(LogLevel.DEBUG,this, "화면처리 권하SCHID ="+sSchId+"=");		
		logger.println(LogLevel.DEBUG,this, "화면처리 권하TOLOC ="+sPutPosition+"=");		
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
	    	JDTORecord rRd = JDTORecordFactory.getInstance().create();
	    	
	    	// USER_ID 셋팅
	    	rRd.setField("USER_ID", sUserId);
	    	
	    	JDTORecord schInfo = null;
	    	 
	    	schInfo = dao.getSchInfoWithSchId(sSchId);
	    	
	    	if(schInfo == null){
	    		logger.println(LogLevel.DEBUG,this, "화면처리 권하에러..");
	    	   	logger.println(LogLevel.DEBUG,this, "스케쥴정보를 가져올 수 없슴.");
	    	   	throw new EJBServiceException("=권하실적=>스케쥴정보 존재안함.");
    		}
    		
    		JDTORecord layerRc = null;
	    	 
	    	layerRc = dao.getPutStackLayerListWithSchId(sSchId);
	    	
	    	if(layerRc == null){
	    		logger.println(LogLevel.DEBUG,this, "화면처리 권하에러..");
	    	   	logger.println(LogLevel.DEBUG,this, "적치단정보를 가져올 수 없슴.");
    			layerRc = createToLoc_01(sSchId,sTC);
    		}
			
			JDTORecord IsangRc = null;
			
			if(sPutPosition.length() == 10){
	    		
				IsangRc  = dao.getStackLayerInfoWithPk(sPutPosition.substring(0, 6),
	    										  	   sPutPosition.substring(6, 8),	
	    										  	   sPutPosition.substring(8,10));
	    		if(IsangRc == null){
		    		logger.println(LogLevel.DEBUG,this, "화면처리 권하에러..");
		    	   	logger.println(LogLevel.DEBUG,this, "권하이상 적치단 정보를 가져올 수 없슴.");
	    			throw new EJBServiceException("=권하실적=>권하이상 적치단정보 존재안함.");
	    		}								  	   
    		}								  	   
    										  
			if(YmCommonConst.TC_THCH570.equals(sTC)||//A열연 Coil 권하실적
			   YmCommonConst.TC_CN1PB06.equals(sTC)){//B열연 Coil 권하실적	
			   
				rRd.setField("TC", sTC);
				rRd.setField("Yard_Id"		, StringHelper.evl(schInfo.getFieldString("YD_GP"), ""));
				rRd.setField("Bay_GP"		, StringHelper.evl(schInfo.getFieldString("BAY_GP"), ""));
				rRd.setField("Equip_Kind"	, YmCommonConst.EQUIP_KIND_CR);
				rRd.setField("Equip_No"		, StringHelper.evl(schInfo.getFieldString("SCH_WORK_EQUIP_NO"), ""));
				rRd.setField("Coil_No"		, StringHelper.evl(schInfo.getFieldString("STOCK_ID"), ""));
				rRd.setField("Sch_Code"		, StringHelper.evl(schInfo.getFieldString("SCH_WORK_KIND"), ""));
				rRd.setField("Work_Id"		, StringHelper.evl(schInfo.getFieldString("SCH_WORK_AID_YN"), ""));
				rRd.setField("Put_Position"	, StringHelper.evl(layerRc.getFieldString("STACK_COL_GP"), "")+
											  StringHelper.evl(layerRc.getFieldString("STACK_BED_GP"), "")+
											  StringHelper.evl(layerRc.getFieldString("STACK_LAYER_GP"), ""));
				rRd.setField("Put_X_Position", StringHelper.evl(layerRc.getFieldString("STACK_LAYER_X_AXIS"), ""));
				rRd.setField("Put_Y_Position", StringHelper.evl(layerRc.getFieldString("STACK_LAYER_Y_AXIS"), ""));
				
				if(YmCommonConst.TC_THCH570.equals(sTC)){
					/*
					 * A열연 Crane No를 가지고 현재 Legacy Crane No를 가져온다.
					 */
					JDTORecord crnRc = dao.getLegacyEquipNoWithCurEquipNo(StringHelper.evl(schInfo.getFieldString("YD_GP"), "")+
																		  StringHelper.evl(schInfo.getFieldString("BAY_GP"), "")+
																		  YmCommonConst.EQUIP_KIND_CR+
																		  StringHelper.evl(schInfo.getFieldString("SCH_WORK_EQUIP_NO"), ""));
					if(crnRc == null){
						throw new EJBServiceException("=권상실적=>A열연 크레인정보 존재안함.");
		    		}
					/**
					 * 이후 처리를 위해 필요한 값 셋팅
					 */
					rRd.setField("LEGACY_CRANE_NO", StringHelper.evl(crnRc.getFieldString("EQUIP_GP"), ""));
				}	
				
			}else if(YmCommonConst.TC_THCH580.equals(sTC)||//A열연 Coil 권하 이상 실적								
				     YmCommonConst.TC_CN1PB07.equals(sTC)){//B열연 Coil 권하 이상 실적	
				     	   	
				rRd.setField("TC", sTC);
				rRd.setField("Yard_Id"		 , StringHelper.evl(schInfo.getFieldString("YD_GP"), ""));
				rRd.setField("Bay_GP"		 , StringHelper.evl(schInfo.getFieldString("BAY_GP"), ""));
				rRd.setField("Equip_Kind"	 , YmCommonConst.EQUIP_KIND_CR);
				rRd.setField("Equip_No"		 , StringHelper.evl(schInfo.getFieldString("SCH_WORK_EQUIP_NO"), ""));
				rRd.setField("Coil_No"		 , StringHelper.evl(schInfo.getFieldString("STOCK_ID"), ""));
				rRd.setField("Sch_Code"		 , StringHelper.evl(schInfo.getFieldString("SCH_WORK_KIND"), ""));
				rRd.setField("Work_Id"		 , StringHelper.evl(schInfo.getFieldString("SCH_WORK_AID_YN"), ""));
				rRd.setField("Put_Position"	 , StringHelper.evl(IsangRc.getFieldString("STACK_COL_GP"), "")+
											   StringHelper.evl(IsangRc.getFieldString("STACK_BED_GP"), "")+
											   StringHelper.evl(IsangRc.getFieldString("STACK_LAYER_GP"), ""));
				rRd.setField("Put_X_Position", StringHelper.evl(IsangRc.getFieldString("STACK_LAYER_X_AXIS"), ""));
				rRd.setField("Put_Y_Position", StringHelper.evl(IsangRc.getFieldString("STACK_LAYER_Y_AXIS"), ""));
				rRd.setField("WorkOrder_Position" , StringHelper.evl(layerRc.getFieldString("STACK_COL_GP"), "")+
											  		StringHelper.evl(layerRc.getFieldString("STACK_BED_GP"), "")+
											  		StringHelper.evl(layerRc.getFieldString("STACK_LAYER_GP"), ""));
				
				if(YmCommonConst.TC_THCH580.equals(sTC)){
					/*
					 * A열연 Crane No를 가지고 현재 Legacy Crane No를 가져온다.
					 */
					JDTORecord crnRc = dao.getLegacyEquipNoWithCurEquipNo(StringHelper.evl(schInfo.getFieldString("YD_GP"), "")+
																		  StringHelper.evl(schInfo.getFieldString("BAY_GP"), "")+
																		  YmCommonConst.EQUIP_KIND_CR+
																		  StringHelper.evl(schInfo.getFieldString("SCH_WORK_EQUIP_NO"), ""));
					if(crnRc == null){
						throw new EJBServiceException("=권상실적=>A열연 크레인정보 존재안함.");
		    		}
					/**
					 * 이후 처리를 위해 필요한 값 셋팅
					 */
					rRd.setField("LEGACY_CRANE_NO", StringHelper.evl(crnRc.getFieldString("EQUIP_GP"), ""));
				}	
				
			}else if(YmCommonConst.TC_CM1PB06.equals(sTC)		//B열연 Slab 권하실적 
					 || YmCommonConst.TC_HM1PB06.equals(sTC)	//A_A열연 Slab 권하실적(MCH)
					 || YmCommonConst.TC_HM1PB56.equals(sTC) ){	//A_B열연 Slab 권하실적(MCH)
				
				rRd.setField("TC", sTC);
				rRd.setField("Yard_Id"		 , StringHelper.evl(schInfo.getFieldString("YD_GP"), ""));
				rRd.setField("Bay_GP"		 , StringHelper.evl(schInfo.getFieldString("BAY_GP"), ""));
				rRd.setField("Equip_Kind"	 , YmCommonConst.EQUIP_KIND_CR);
				rRd.setField("Equip_No"		 , StringHelper.evl(schInfo.getFieldString("SCH_WORK_EQUIP_NO"), ""));
				rRd.setField("Slab_No"		 , StringHelper.evl(schInfo.getFieldString("STOCK_ID"), ""));
				rRd.setField("Sch_Code"		 , StringHelper.evl(schInfo.getFieldString("SCH_WORK_KIND"), ""));
				rRd.setField("Work_Id"		 , StringHelper.evl(schInfo.getFieldString("SCH_WORK_AID_YN"), ""));
				rRd.setField("Put_Position"	 , StringHelper.evl(layerRc.getFieldString("STACK_COL_GP"), "")+
											   StringHelper.evl(layerRc.getFieldString("STACK_BED_GP"), "")+
											   StringHelper.evl(layerRc.getFieldString("STACK_LAYER_GP"), ""));
				rRd.setField("Put_X_Position", StringHelper.evl(layerRc.getFieldString("STACK_LAYER_X_AXIS"), ""));
				rRd.setField("Put_Y_Position", StringHelper.evl(layerRc.getFieldString("STACK_LAYER_Y_AXIS"), ""));
								
			}else if(YmCommonConst.TC_CM1PB07.equals(sTC)			//B열연 Slab 권하 이상 실적
					 || YmCommonConst.TC_HM1PB07.equals(sTC) 		//A_A열연 Slab 권하 이상 실적(MCH)
					 || YmCommonConst.TC_HM1PB57.equals(sTC) ){		//A_B열연 Slab 권하 이상 실적(MCH)
				
				rRd.setField("TC", sTC);
				rRd.setField("Yard_Id"		 , StringHelper.evl(schInfo.getFieldString("YD_GP"), ""));
				rRd.setField("Bay_GP"		 , StringHelper.evl(schInfo.getFieldString("BAY_GP"), ""));
				rRd.setField("Equip_Kind"	 , YmCommonConst.EQUIP_KIND_CR);
				rRd.setField("Equip_No"		 , StringHelper.evl(schInfo.getFieldString("SCH_WORK_EQUIP_NO"), ""));
				rRd.setField("Slab_No"		 , StringHelper.evl(schInfo.getFieldString("STOCK_ID"), ""));
				rRd.setField("Sch_Code"		 , StringHelper.evl(schInfo.getFieldString("SCH_WORK_KIND"), ""));
				rRd.setField("Work_Id"		 , StringHelper.evl(schInfo.getFieldString("SCH_WORK_AID_YN"), ""));
				rRd.setField("Put_Position"	 , StringHelper.evl(IsangRc.getFieldString("STACK_COL_GP"), "")+
											   StringHelper.evl(IsangRc.getFieldString("STACK_BED_GP"), "")+
											   StringHelper.evl(IsangRc.getFieldString("STACK_LAYER_GP"), ""));
				rRd.setField("Put_X_Position", StringHelper.evl(IsangRc.getFieldString("STACK_LAYER_X_AXIS"), ""));
				rRd.setField("Put_Y_Position", StringHelper.evl(IsangRc.getFieldString("STACK_LAYER_Y_AXIS"), ""));
				rRd.setField("WorkOrder_Position" , StringHelper.evl(layerRc.getFieldString("STACK_COL_GP"), "")+
											  		StringHelper.evl(layerRc.getFieldString("STACK_BED_GP"), "")+
											  		StringHelper.evl(layerRc.getFieldString("STACK_LAYER_GP"), ""));
			}
			
	    	isSuccess = callCranePutRtInfo(rRd,
	    								   YmCommonConst.CRANE_FUNC_V);
	    	
	    	return isSuccess;
	    	
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}
	
       /**
	 * 오퍼레이션명 : 야드운영자가 Schedule관리기능을 통해 Crane Put 실적을 발생시킨다.
	 *
	 * param jDTORecord : 전문항목
        * param String	 : 실적처리방법
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     			
	public boolean callCranePutRtInfo(JDTORecord jtRcd,
									  String sFuncGbn){
		
		boolean isSuccess = false;
		 
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
	    	String sTc = StringHelper.evl(jtRcd.getFieldString("TC"), "");
	    	
	    	if(
			   YmCommonConst.TC_THCH570.equals(sTc) || //A열연 Coil 권하실적
			   YmCommonConst.TC_CN1PB06.equals(sTc)	|| //B열연 Coil 권하실적
			   YmCommonConst.TC_THCH580.equals(sTc) || //A열연 Coil 권하이상실적
			   YmCommonConst.TC_CN1PB07.equals(sTc)	   //B열연 Coil 권하이상실적
				){
				
				isSuccess = callCranePutRtInfo_Coil(jtRcd,
													sFuncGbn);
			}else if(false){//YmCommonConst.TC_THCH580.equals(sTc)    //A열연 Coil 권하이상실적
				
				isSuccess = callCranePutIsangInfo_Coil(jtRcd,
												       sFuncGbn);
				
			}else if(YmCommonConst.TC_CM1PB06.equals(sTc) || //B열연 Slab 권하실적		
					YmCommonConst.TC_CM1PB07.equals(sTc)){	 //B열연 Slab 권하이상실적
				
				String sTcGbn = StringHelper.evl(jtRcd.getFieldString("TC_GBN"), "");
				
				if("U".equals(sTcGbn)||"B".equals(sTcGbn)){
					
					String sBCraneNo 		= StringHelper.evl(jtRcd.getFieldString("Yard_Id"), "").trim() +
					                                      StringHelper.evl(jtRcd.getFieldString("Bay_GP"), "").trim() +
					                                      StringHelper.evl(jtRcd.getFieldString("Equip_Kind"), "").trim() +
					                                      StringHelper.evl(jtRcd.getFieldString("Equip_No"), "").trim();
					String sBSchCd		= StringHelper.evl(jtRcd.getFieldString("Sch_Code"), "").trim();
					String sBSlabNo 		= StringHelper.evl(jtRcd.getFieldString("Slab_No"), "").trim();
					String sBPutPosition 	= StringHelper.evl(jtRcd.getFieldString("Put_Position"), "").trim();
					
					logger.println(LogLevel.DEBUG,this,"=Level 2 비상조업 백업실적 구분		="+sTcGbn); 
					logger.println(LogLevel.DEBUG,this,"=Level 2 비상조업 백업실적 크레인번호	="+sBCraneNo); 
					logger.println(LogLevel.DEBUG,this,"=Level 2 비상조업 백업실적 스케쥴코드	="+sBSchCd); 
					logger.println(LogLevel.DEBUG,this,"=Level 2 비상조업 백업실적 슬라브정보	="+sBSlabNo); 
					logger.println(LogLevel.DEBUG,this,"=Level 2 비상조업 백업실적 백업위치정보	="+sBPutPosition); 
										
					EJBConnector ejbConn1 = new EJBConnector("default","JNDICraneSchReg",this);
		    			Boolean isBool = (Boolean)ejbConn1.trx("changeSlabLocationInfo",new Class[]{	String.class, 
				    																		String.class,
				    																		String.class, 
				    																		String.class, 
				    																		String.class,
				    																		String.class, 
				    																		String.class,String.class},
					    														   new Object[]{	sTcGbn,
					    														   				sBCraneNo,
					    														   				sBSchCd,
					    														   				sBSlabNo, 
					    														   				sBPutPosition, 
					    														    				sBPutPosition,
					 																		"R","SYSTEM"});
				    	//W/B, CTC 설비 초기화   
				    	/*
				    	dao.updateCraneStackLayerStat("2CWB01","01","01","","E");
				    	dao.updateCraneStackLayerStat("2CWB01","01","02","","E");
				    	dao.updateCraneStackLayerStat("2CWB01","01","03","","E");
				    	dao.updateCraneStackLayerStat("2CWB01","01","04","","E");
				    	dao.updateCraneStackLayerStat("2ACT01","01","01","","E");
				    	dao.updateCraneStackLayerStat("2ACT01","01","02","","E");
				    	dao.updateCraneStackLayerStat("2ACT02","01","01","","E");
				    	dao.updateCraneStackLayerStat("2BCT03","01","01","","E");
				    	dao.updateCraneStackLayerStat("2CCT04","01","01","","E");
					*/																							    			
				}else{
				
					isSuccess = callCranePutRtInfo_Slab(jtRcd,
													    sFuncGbn);
				}
			}else if(YmCommonConst.TC_HM1PB06.equals(sTc) || 	//A열연 Slab 지상국A 권하실적(MCH)
					 YmCommonConst.TC_HM1PB56.equals(sTc) ||  	//A열연 Slab 지상국B 권하실적(MCH)
					 YmCommonConst.TC_HM1PB07.equals(sTc) ||	//A열연 Slab 지상국A 권하이상실적(MCH)
					 YmCommonConst.TC_HM1PB57.equals(sTc)){		//A열연 Slab 지상국B 권하이상실적(MCH)
				isSuccess = callCranePutRtInfo_ASlab(jtRcd,
					    							sFuncGbn);				
			}
			
//			try{
//	    		com.inisteel.cim.common.realtime.RealTimeUtil.pushTopic("ymCraneTopic");
//	    	}catch(Exception e){}
			
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess;
	}
	
	/**
	 * 오퍼레이션명 : 0.	COIL INFO
	 *
	 *야드운영자가 Schedule관리기능을 통해 Crane Put 실적을 발생시킨다.
	 *
	 * param jDTORecord : 전문항목
        * param String	 : 실적처리방법
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     	
	public boolean callCranePutRtInfo_Coil(JDTORecord jtRcd,
									 	   String sFuncGbn){
		
		boolean isSuccess = false;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			// 현재 stock의 정보를 가져와 차량이적 여부를 판별한다. 최규성
			boolean bCarMove = checkStockInfo_CarMove(jtRcd);
	    	/**
	    	 * 1.	권하이상 PUT 위치 오차검정
	    	 */
	    	 	logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶COIL PUT STEP 1 START◀◀◀◀◀◀◀◀◀◀"); 
	    	 	boolean isCheck = setCranePutCheckInfo_Coil(jtRcd,
	    	 										     	sFuncGbn);
	    	 	/*
	    	 	 * TRUE : 논리적위치 값과 실 좌표값이 맞지않음.
	    	 	 *		  재작업지시 송신.
	    	 	 */
	    	 	if(isCheck) return isSuccess;
	    	/**
	    	 * 2.	권하실적 UPDATE/ 이상실적발생시 지시위치 CLEAR
	    	 */
	    	 	logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶COIL PUT STEP 2 START◀◀◀◀◀◀◀◀◀◀"); 
	    	 	//출하쪽에서 코일공통을 update시 lock현상을 방지 하기 위하여 commit처리 함
	    	 	EJBConnector ejbConn = new EJBConnector("default","JNDICPutResReg",this);
	    	 	Boolean isYd = (Boolean)ejbConn.trx("setCranePutRtInfo_Coil",new  Class[]{JDTORecord.class,String.class},
															new Object[]{jtRcd,sFuncGbn});
	    	 	if(!isYd.booleanValue())  return isSuccess;
	    	 	
	    	/**
	    	 * 3.	야드작업실적 송신 Call
	    	 */
	    	 	logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶COIL PUT STEP 3 START◀◀◀◀◀◀◀◀◀◀"); 
	    	 	boolean isWrk =	callInnerWorkInfo_Coil(jtRcd);
	    	 			isWrk =	callInnerWorkInfo_Coil_Dm(jtRcd); //이적작업실적
	    	 			isWrk =	callInnerWorkInfo_CoilTR(jtRcd); //차량이적 작업 
//	    	 	if(isWrk) return isSuccess;
	    	 		    
	    	/**
	    	 * 4.	MILL L2 작업결과 송신
	    	 */ 
	    	 	logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶COIL PUT STEP 4 START◀◀◀◀◀◀◀◀◀◀"); 
	    	 	boolean isMill=	callMillFinishInfo_Coil(jtRcd); 	
	    	/**
	    	 * 5.	야드맵 정보 송신 Call
	    	 */  
	    	 	logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶COIL PUT STEP 5 START◀◀◀◀◀◀◀◀◀◀"); 
    	 		boolean isMap =	callMapInfo_Coil(jtRcd);
    	 		
//	 		/**
//	    	 * 5.	HFL 결속대 보급/출하 실적 정보 송신 Call
//	    	 */  
//	    	 	logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶COIL PUT STEP HFL결속대 START◀◀◀◀◀◀◀◀◀◀"); 
//    	 		boolean isHFL =	callHFLInfo_Coil(jtRcd);	  
        	 		
	    	/**
	    	 * 6.	대차출발지시 Call
	    	 */  
	    	 	logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶COIL PUT STEP 6 START◀◀◀◀◀◀◀◀◀◀"); 
	    		boolean isTC  =	callTcWorkInfo_Coil(jtRcd);
	    	/**
	    	 * 7.	CTS 작업지시 Call
	    	 */  
	    	 	logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶COIL PUT STEP 7(목적동지정) START◀◀◀◀◀◀◀◀◀◀"); 
	    		boolean isCTS =	callCtsWorkInfo_Coil(jtRcd);
	    	/**
	    	 * 8.	Crane 작업결과 송신
	    	 */ 
	    	 	logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶COIL PUT STEP 8 START◀◀◀◀◀◀◀◀◀◀"); 
	    	 	boolean isFsh =	callWorkFinishInfo_Coil(jtRcd,
	    	 											YmCommonConst.RESULT_MODE_0);
	    	/**
	    	 * 9.	Next 작업지시 Call
	    	 */  
	    	 	logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶COIL PUT STEP 9 START◀◀◀◀◀◀◀◀◀◀"); 
    	 		boolean isSch =	callCraneSchInfo_Coil(jtRcd);
    	 		
    	 		// 최규성 
    	 		// 차량이적 상차 PUT작업시 구내 운송 로직은 실행하지 않는다.
    	 		// 차량이적 여부는 작업 코일 정보에 카드번호를 가지고 있는가를 보고 판단한다.
    	 		// 카드번호를 9999~9995까지의 번호를 가지고 있다면 차량이적이라고 판단한다.
    	 		// checkStockInfo(jtRcd)
    	 		//boolean bCarMove = checkStockInfo_CarMove(jtRcd);
    	 		// 체크 여부를 함수 시작 전에 한다. 2009-11-01
    	 		
    	 		if ( bCarMove ) 
    	 		{
    	 			// 차량이적인 경우엔 임가공사 이송 여부 체크하지 않는다.
    	 			logger.println(LogLevel.DEBUG,this,"======= 차량이적인 경우엔 임가공사 이송 여부 체크하지 않는다.========");
    	 			isSuccess = true;
    	 		}else{
    	 		
	    	 		 
	    	    	/** 	
	    	       	 * 10. 이송상차-상차개시/완료전문 송신
	    			 * (hyuksang)
	    			 */
					/*
					 * 20090903 정종균 
					 * 임가공사 이송 여부 체크 : 구내운송은 임가공사인 경우 페스한다. 
					 */
					//*****************************************************************************************
	    	 		String sStockId	= StringHelper.evl(jtRcd.getFieldString("Coil_No"), "").trim();
					List pmList  = dao.getYmPoFrtoInfo(sStockId);
					if (pmList.size() <= 0) {					
		
		    	    	logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶COIL PUT STEP 10 START◀◀◀◀◀◀◀◀◀◀");	
		    	    	boolean issep =	callStartLastWorkInfo_Coil(jtRcd);
					}
					//*****************************************************************************************
    	 		
    	 		} 
    	 		
    	 		
	    	isSuccess = true;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	    	try{
		    	boolean isFsh = callWorkFinishInfo_Coil(jtRcd,
		    											YmCommonConst.RESULT_MODE_1);
		    }catch(Exception ex){}
	        throw new EJBServiceException(e);
	    }
	    return isSuccess;
	}
	
       /**
	 * 오퍼레이션명 : 0.	SLAB INFO(A열연 SLAB야드(2007-02-26 추가))(MCH)
	 *
	 *야드운영자가 Schedule관리기능을 통해 Crane Put 실적을 발생시킨다.
	 *
	 * param jDTORecord : 전문항목
        * param String	 : 실적처리방법
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     	
	public boolean callCranePutRtInfo_ASlab(JDTORecord jtRcd,
									 	   String sFuncGbn){
		
		boolean isSuccess = false;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
	    	/**
	    	 * 1.	PUT 위치 검정
	    	 */
	    	 	logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶SLAB(A) PUT STEP 1 START◀◀◀◀◀◀◀◀◀◀"); 
	    	 	boolean isCheck = setCranePutCheckInfo_Slab(jtRcd,
	    	 										    sFuncGbn);
	    	 	/*
	    	 	 * TRUE : 논리적위치 값과 실 좌표값이 맞지않음.
	    	 	 *		  재작업지시 송신.
	    	 	 */
	    	 	if(isCheck) return isSuccess;
	    	
	    	/**
	    	 * 2.	권하실적 UPDATE/ 이상실적발생시 지시위치 CLEAR
	    	 */
	    	 	logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶SLAB(A) PUT STEP 2 START◀◀◀◀◀◀◀◀◀◀"); 
//	    	 	boolean isYd = setCranePutRtInfo_Slab(jtRcd,
//	    	 										  sFuncGbn);
//	    	 	if(!isYd) return isSuccess;
	    	 	
	    	 	
	    	 	//출하쪽에서 코일공통을 update시 lock현상을 방지 하기 위하여 commit처리 함
	    	 	EJBConnector ejbConn = new EJBConnector("default","JNDICPutResReg",this);
	    	 	Boolean isYd2 = (Boolean)ejbConn.trx("setCranePutRtInfo_Slab",new  Class[]{JDTORecord.class,String.class},
															new Object[]{jtRcd,sFuncGbn});
	    	 	if(!isYd2.booleanValue())  return isSuccess;
	    	 	
	    	/**
	    	 * 3.	야드작업실적 송신 Call
	    	 * A열연 SLAB야드 추가(MCH)
	    	 */
	    	 	// 일관제철부분 필요없슴 
	    	 	//logger.println(LogLevel.DEBUG,this,"=SLAB(A) PUT STEP 3 START="); 
	    	 	//boolean isWrk =	callInnerWorkInfo_ASlab(jtRcd);
	    	 			    	 	
	    	/**
	    	 * 4.	Crane 작업결과 송신
	    	 */ 
	    	 	logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶SLAB(A) PUT STEP 3 START◀◀◀◀◀◀◀◀◀◀"); 
	    	 	boolean isFsh =	callWorkFinishInfo_Slab(jtRcd);
	    	 	
	    	/**
	    	 * 5.	야드맵 정보 송신 Call(A열연 차상국)
	    	 */  
	    	 	logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶SLAB(A) PUT STEP 4 START◀◀◀◀◀◀◀◀◀◀"); 
    	 		boolean isMap =	callMapInfo_ASlab(jtRcd);
    	 		
	    	/**
	    	 * 6.	야드맵 정보 송신 Call(연주7호기)
	    	 */  
	    	 	logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶SLAB(A) PUT STEP 5 START◀◀◀◀◀◀◀◀◀◀"); 
    	 		boolean isMap7 = callMapInfo_ASlab7(jtRcd);
    	 		
	    	/**
	    	 * 7.	Next 작업지시 Call
	    	 */  
	    	 	logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶SLAB(A) PUT STEP 6 START◀◀◀◀◀◀◀◀◀◀"); 
    	 		boolean isSch =	callCraneSchInfo_Slab(jtRcd);
    	 		
    	 	/**
	    	 * 8.	SCARFING 작업지시 Call
	    	 */  
	    	 	logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶SLAB(A) PUT STEP 7 START◀◀◀◀◀◀◀◀◀◀"); 
    	 		boolean isScf =	callScarfingInfo_Slab(jtRcd);	

			/**
			 * 9.	야드작업실적 송신 Call 마지막 권상완료시 공정으로 JMS 송신
			 * A열연 SLAB야드 추가(MCH)
			 */
    	 		// 일관제철부분 필요없슴 
	    	 	//logger.println(LogLevel.DEBUG,this,"=SLAB(A) PUT STEP 9 START=");	//hyuksang
	    	 	//boolean ispm =	callLastWorkInfo_ASlab(jtRcd);
	    	/** 	
	       	 * 12. 이송상차-상차개시/완료전문 송신
			 * A열연 SLAB야드 추가(hyuksang)
			 */
		    	logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶SLAB(A) PUT STEP 8 START◀◀◀◀◀◀◀◀◀◀");	
		    	boolean issep =	callStartLastWorkInfo_ASlab(jtRcd);
	
		    	isSuccess = true;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess;
	}

	
	
    /**
	 * 오퍼레이션명 : 0.	SSLAB INFO
	 *
	 * 야드운영자가 Schedule관리기능을 통해 Crane Put 실적을 발생시킨다.
	 *
	 * param jDTORecord : 전문항목
        * param String	 : 실적처리방법
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     	 
	public boolean callCranePutRtInfo_Slab(JDTORecord jtRcd,
									 	   String sFuncGbn){
		
		boolean isSuccess = false;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
	    	/**
	    	 * 1.	PUT 위치 검정
	    	 */
	    	 	logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶SLAB(B) PUT STEP 1 START◀◀◀◀◀◀◀◀◀◀"); 
	    	 	boolean isCheck = setCranePutCheckInfo_Slab(jtRcd,
	    	 										    sFuncGbn);
	    	 	/*
	    	 	 * TRUE : 논리적위치 값과 실 좌표값이 맞지않음.
	    	 	 *		  재작업지시 송신.
	    	 	 */
	    	 	if(isCheck) return isSuccess;
	    	
	    	/**
	    	 * 2.	권하실적 UPDATE/ 이상실적발생시 지시위치 CLEAR
	    	 */
	    	 	logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶SLAB(B) PUT STEP 2 START◀◀◀◀◀◀◀◀◀◀"); 
//	    	 	boolean isYd = setCranePutRtInfo_Slab(jtRcd,
//	    	 										  sFuncGbn);
//	    	 	if(!isYd) return isSuccess;
	    	 	
	    	 	
	    	 	//출하쪽에서 코일공통을 update시 lock현상을 방지 하기 위하여 commit처리 함
	    	 	EJBConnector ejbConn = new EJBConnector("default","JNDICPutResReg",this);
	    	 	Boolean isYd = (Boolean)ejbConn.trx("setCranePutRtInfo_Slab",new  Class[]{JDTORecord.class,String.class},
															new Object[]{jtRcd,sFuncGbn});
	    	 	if(!isYd.booleanValue())  return isSuccess;
	    	 	
	    	/**
	    	 * 3.	야드작업실적 송신 Call
	    	 */
	    	 	logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶SLAB(B) PUT STEP 3 START◀◀◀◀◀◀◀◀◀◀"); 
	    	 	boolean isWrk =	callInnerWorkInfo_Slab(jtRcd);
	    	/**
	    	 * 4.	Crane 작업결과 송신
	    	 */ 
	    	 	logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶SLAB(B) PUT STEP 4 START◀◀◀◀◀◀◀◀◀◀"); 
	    	 	boolean isFsh =	callWorkFinishInfo_Slab(jtRcd);
	    	 	
	    	/**
	    	 * 5.	MILL L2 작업결과 송신
	    	 */ 
	    	 	logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶SLAB(B) PUT STEP 5 START◀◀◀◀◀◀◀◀◀◀"); 
	    	 	boolean isMill=	callMillFinishInfo_Slab(jtRcd); 	
	    	/**
	    	 * 10.	야드맵 정보 송신 Call
	    	 */  
	    	 	logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶SLAB(B) PUT STEP 6 START◀◀◀◀◀◀◀◀◀◀"); 
    	 		boolean isMap =	callMapInfo_Slab(jtRcd);		
    	 		 	
	    	/**
	    	 * 6.	대차출발지시 Call
	    	 */  
	    	 	logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶SLAB(B) PUT STEP 7 START◀◀◀◀◀◀◀◀◀◀"); 
	    		boolean isTC  =	callTcWorkInfo_Slab(jtRcd);
	    	/**
	    	 * 7.	CTS 작업지시 Call
	    	 */  
	    	 	logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶SLAB(B) PUT STEP 8 START◀◀◀◀◀◀◀◀◀◀"); 
	    		boolean isCTS =	callCtsWorkInfo_Slab(jtRcd);
	    	/**
	    	 * 8.	Next 작업지시 Call
	    	 */  
	    	 	logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶SLAB(B) PUT STEP 9 START◀◀◀◀◀◀◀◀◀◀"); 
    	 		boolean isSch =	callCraneSchInfo_Slab(jtRcd);
    	 	/**
	    	 * 9.	SCARFING 작업지시 Call
	    	 */  
	    	 	logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶SLAB(B) PUT STEP 10 START◀◀◀◀◀◀◀◀◀◀"); 
    	 		boolean isScf =	callScarfingInfo_Slab(jtRcd);	
    	    	/**
	    	 * 10. W/B 고장시 CTC4 비상스케쥴 Call
	    	 */  
	    	 	logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶SLAB(B) PUT STEP 11 START◀◀◀◀◀◀◀◀◀◀"); 
    	 		callWBSch_Slab();	
	    	/**
			 * 11. 야드작업실적 송신 Call 마지막 권상완료시 공정으로 JMS 송신
			 * 	   B열연 SLAB야드 추가(YJK)
			 */
	 			//일관제철부분 필요없슴 
    	 		//logger.println(LogLevel.DEBUG,this,"=SLAB(A) PUT STEP 11 START=");  //hyuksang	
    	 		//boolean ispm =	callLastWorkInfo_BSlab(jtRcd);
	    	 /**		
	    	  * 12. 이송상차-상차개시/완료전문 송신
			  *     B열연 SLAB야드 추가(hyuksang)
			  */
	 			logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶SLAB(B) PUT STEP 12 START◀◀◀◀◀◀◀◀◀◀");	
		    	boolean issep =	callStartLastWorkInfo_BSlab(jtRcd);
		 					     
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
	 *	3.	COIL INFO
        * 	A열연 권하이상실적 처리시
	 *
	 * param jDTORecord : 전문항목
        * param String	 : 실적처리방법
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     	 
	public boolean callCranePutIsangInfo_Coil(JDTORecord jtR,
									 		  String sFuncGbn){
		
		boolean isSuccess = false;
		logger.println(LogLevel.DEBUG,this, "============================");
		logger.println(LogLevel.DEBUG,this, "callCranePutIsangInfo_Coil("+ jtR + ", "+sFuncGbn+") 시작");
		logger.println(LogLevel.DEBUG,this, "============================");

		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			/**
	    	 * 1.	작업지시 PUT위치를 권하이상실적처리를 한다(CLEAR)
	    	 */
	    	 	logger.println(LogLevel.DEBUG,this,"=권하이상실적=> 권하이상실적처리."); 
	    	 	boolean isWrk =	setCraneISangPutRtInfo_Coil(jtR,"");

	    	/**
	    	 * 2.	권하실적처리가 가능하게 스케쥴정보의 PUT위치를 수정한다.
	    	 */
	    	 	String sYard_Id 		= StringHelper.evl(jtR.getFieldString("Yard_Id"), "");
				String sBay_Gp 			= StringHelper.evl(jtR.getFieldString("Bay_GP"), "");
				String sEquip_Kind 		= StringHelper.evl(jtR.getFieldString("Equip_Kind"), "");
				String sEquip_No 		= StringHelper.evl(jtR.getFieldString("Equip_No"), "");
				String sStockId         = StringHelper.evl(jtR.getFieldString("Coil_No"), "").trim();
				String sSch_Code 		= StringHelper.evl(jtR.getFieldString("Sch_Code"), "");
				String sPut_Position 	= StringHelper.evl(jtR.getFieldString("Put_Position"), "");
	    	 	
				logger.println(LogLevel.DEBUG,this, "sYard_Id="			+ sYard_Id);
				logger.println(LogLevel.DEBUG,this, "sBay_Gp="			+ sBay_Gp);
				logger.println(LogLevel.DEBUG,this, "sEquip_Kind="		+ sEquip_Kind);
				logger.println(LogLevel.DEBUG,this, "sEquip_No="		+ sEquip_No);
				logger.println(LogLevel.DEBUG,this, "sStockId="			+ sStockId+"==");
				logger.println(LogLevel.DEBUG,this, "sSch_Code="		+ sSch_Code);
				logger.println(LogLevel.DEBUG,this, "sPut_Position="	+ sPut_Position);
	    	 	
				/*
				 * Crane Put실적 저장품의 적치단정보를 가져온다.
				 */			
		    	JDTORecord schRc = dao.getSchIdInfo(sYard_Id,
							    					sBay_Gp,
							    					sEquip_Kind,
							    					sEquip_No,
							    					sStockId,
							    					sSch_Code
				   									);
		    	if(schRc == null){
		    		logger.println(LogLevel.DEBUG,this, "권하이상에러..");
		    	   	logger.println(LogLevel.DEBUG,this, "스케쥴정보 가져올 수 없슴");
		    	   	throw new EJBServiceException("=권하이상실적=> 스케쥴정보 존재안함.");
	    		}
	    		
		    	String sSchId   = StringHelper.evl(schRc.getFieldString("SCH_ID"), "");
		    	
	    	 	int iSeq = dao.updatePutLocInfoWithSchId(sSchId,
		    						   				     sPut_Position);
	    	 	logger.println(LogLevel.DEBUG,this,"=권하이상실적=> 스케쥴 정보 수정처리."); 
	    	/* 
			 * 3.	적치단 PUT위치 셋팅
			 * 		tb_ym_stacklayer Table : stock_id = stock_id(저장품ID)
			 * 		tb_ym_stacklayer Table : stack_layer_stat	   = 'P'(PUT 스케쥴수행)
			 */	
		    	iSeq = dao.updateCraneStackLayerStat(sPut_Position.substring(0, 6),
		    										 sPut_Position.substring(6, 8),
		    										 sPut_Position.substring(8,10),
		    										 sStockId,
		    										 YmCommonConst.STACK_LAYER_STAT_P);
				logger.println(LogLevel.DEBUG,this,"=권하이상실적=> TO위치 정보 수정처리.");   									 
	    	/**
	    	 * 4.	수정작업지시 호출
	    	 */
	    	 	
		    	
	    	 	EJBConnector ejbConn = new EJBConnector("default","JNDICWrkOrdReg",this);
				Boolean isTemp  = (Boolean)ejbConn.trx("callACoilCraneMsgInfo",new  Class[]{String.class,
																					  		String.class},
																		       new Object[]{YmCommonConst.TC_THHC130,
																					  		sSchId});
	    	 	logger.println(LogLevel.DEBUG,this,"=권하이상실적=> 수정작업지시 호출."); 
	    	 	
	    	isSuccess = true;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	    	try{
		    	boolean isFsh = callWorkFinishInfo_Coil(jtR,
		    											YmCommonConst.RESULT_MODE_1);
		    }catch(Exception ex){}
	        throw new EJBServiceException(e);
	    }
	    return isSuccess;
	}

       /**
	 * 오퍼레이션명 : 
	 *  	2007.07.13 이정훈
	 *	0.	COIL INFO
     	 *		Crane Put 권하이상 실적처리시 오차점검
	 *
	 * param jDTORecord : 전문항목
        * param String	   : 실적처리방법
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 * @ejb.transaction type="RequiresNew"
	 */     	 
	public boolean setCranePutCheckInfo_Coil(JDTORecord jtR,
	 	  	 String sFuncGbn){

		boolean isSuccess = false;
		logger.println(LogLevel.DEBUG,this, "============================");
		logger.println(LogLevel.DEBUG,this, "setCranePutCheckInfo_Coil("+ jtR + ", "+sFuncGbn+") 시작");
		logger.println(LogLevel.DEBUG,this, "============================");

		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String sTc			= StringHelper.evl(jtR.getFieldString("TC"), "");
			String sYard_Id 		= StringHelper.evl(jtR.getFieldString("Yard_Id"), "");
			String sBay_Gp 		= StringHelper.evl(jtR.getFieldString("Bay_GP"), "");
			String sEquip_Kind 		= StringHelper.evl(jtR.getFieldString("Equip_Kind"), "");
			String sEquip_No 		= StringHelper.evl(jtR.getFieldString("Equip_No"), "");
			String sStockId         	= StringHelper.evl(jtR.getFieldString("Coil_No"), "").trim();
			String sSch_Code 		= StringHelper.evl(jtR.getFieldString("Sch_Code"), "");
			String sPutRLoc		= StringHelper.evl(jtR.getFieldString("Put_Position"), "").trim();
			String sPutRXLoc		= StringHelper.evl(jtR.getFieldString("Put_X_Position"), "0").trim();
			String sPutRYLoc		= StringHelper.evl(jtR.getFieldString("Put_Y_Position"), "0").trim();

			logger.println(LogLevel.DEBUG,this, "sTc="				+ sTc);
			logger.println(LogLevel.DEBUG,this, "sYard_Id="			+ sYard_Id);
			logger.println(LogLevel.DEBUG,this, "sBay_Gp="			+ sBay_Gp);
			logger.println(LogLevel.DEBUG,this, "sEquip_Kind="		+ sEquip_Kind);
			logger.println(LogLevel.DEBUG,this, "sEquip_No="		+ sEquip_No);
			logger.println(LogLevel.DEBUG,this, "sStockId="			+ sStockId+"=");
			logger.println(LogLevel.DEBUG,this, "sSch_Code="		+ sSch_Code);
			logger.println(LogLevel.DEBUG,this, "sPut_Position="	+ sPutRLoc);
			logger.println(LogLevel.DEBUG,this, "sPut_X_Position="	+ sPutRXLoc);
			logger.println(LogLevel.DEBUG,this, "sPut_Y_Position="	+ sPutRYLoc);

			if( YmCommonConst.CRANE_FUNC_N.equals(sFuncGbn)) {//정상전문처리
				if (YmCommonConst.TC_THCH580.equals(sTc) || !"".equals(sPutRLoc)) {
					logger.println(LogLevel.DEBUG,this, "== A열연 권하이상  ==");
					
					isSuccess = setCranePutCheckInfo_ACoil(	jtR,
					     									sFuncGbn);
				}
				else if(YmCommonConst.TC_CN1PB07.equals(sTc)) {
					logger.println(LogLevel.DEBUG,this, "== B열연 권하이상  ==");
					
					isSuccess = setCranePutCheckInfo_BCoil(	jtR,
								    						sFuncGbn);
				}
			}
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
		return isSuccess;
	}
		
       /**
	 * 오퍼레이션명 : 
	 *	0.	COIL INFO
     	 *		Crane Put 권하이상 실적처리시 오차점검
	 *
	 * param jDTORecord : 전문항목
        * param String	   : 실적처리방법
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     	 
	public boolean setCranePutCheckInfo_ACoil(JDTORecord jtR,
									 	  	 String sFuncGbn){
		
		boolean isSuccess = false;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String sTc			= StringHelper.evl(jtR.getFieldString("TC"), "");
			String sYard_Id 		= StringHelper.evl(jtR.getFieldString("Yard_Id"), "");
			String sBay_Gp 		= StringHelper.evl(jtR.getFieldString("Bay_GP"), "");
			String sEquip_Kind 		= StringHelper.evl(jtR.getFieldString("Equip_Kind"), "");
			String sEquip_No 		= StringHelper.evl(jtR.getFieldString("Equip_No"), "");
			String sStockId         	= StringHelper.evl(jtR.getFieldString("Coil_No"), "").trim();
			String sSch_Code 		= StringHelper.evl(jtR.getFieldString("Sch_Code"), "");
			String sPutRLoc		= StringHelper.evl(jtR.getFieldString("Put_Position"), "0").trim();
			String sPutRXLoc		= StringHelper.evl(jtR.getFieldString("Put_X_Position"), "0").trim();
			String sPutRYLoc		= StringHelper.evl(jtR.getFieldString("Put_Y_Position"), "0").trim();
			
			logger.println(LogLevel.DEBUG,this, "sTc="				+ sTc);
			logger.println(LogLevel.DEBUG,this, "sYard_Id="			+ sYard_Id);
			logger.println(LogLevel.DEBUG,this, "sBay_Gp="			+ sBay_Gp);
			logger.println(LogLevel.DEBUG,this, "sEquip_Kind="		+ sEquip_Kind);
			logger.println(LogLevel.DEBUG,this, "sEquip_No="		+ sEquip_No);
			logger.println(LogLevel.DEBUG,this, "sStockId="			+ sStockId+"=");
			logger.println(LogLevel.DEBUG,this, "sSch_Code="		+ sSch_Code);
			logger.println(LogLevel.DEBUG,this, "sPut_Position="	+ sPutRLoc);
			logger.println(LogLevel.DEBUG,this, "sPut_X_Position="	+ sPutRXLoc);
			logger.println(LogLevel.DEBUG,this, "sPut_Y_Position="	+ sPutRYLoc);
					
    	 	if( 	(
    	 		YmCommonConst.CRANE_FUNC_N.equals(sFuncGbn)|| 	//차상국 전문처리
    	 		YmCommonConst.CRANE_FUNC_L.equals(sFuncGbn)		//지상국 전문처리 
    	 		)&&
    	 		(
    	 		YmCommonConst.TC_THCH580.equals(sTc) || //A열연 Coil 권하이상실적
			YmCommonConst.TC_CN1PB07.equals(sTc)	//B열연 Coil 권하이상실적
			)
    	 	){
    	 	
    	 		//if(YmCommonConst.TC_THCH580.equals(sTc)){
    	 		if(false){	
    	 			String tStockId		= "";
					String tLayerStat	= "";
					
    	 			if(sPutRLoc.length() == 10){
			    		JDTORecord putR = dao.getStackLayerInfoWithPk(sPutRLoc.substring(0, 6),
			    											 	sPutRLoc.substring(6, 8),
															sPutRLoc.substring(8,10));
				    	if(putR != null){
				    		tStockId 	= StringHelper.evl(putR.getFieldString("STOCK_ID"), "");					
							tLayerStat 	= StringHelper.evl(putR.getFieldString("STACK_LAYER_STAT"), "");
							
							///////////////////////////////////////////////////////////////
			    			////////////////코일점검 체크로직//////////////////////////////
			    			if(YmCommonConst.STACK_LAYER_STAT_L.equals(tLayerStat) &&
			    			   !tStockId.equals(sStockId)
			    			){
					    		isSuccess = true;		
					    		logger.println(LogLevel.DEBUG,this, "=권하이상=> 오차점검 NG => 작업 재지시 CALL");
					    	}else{
					    		isSuccess = false;		
					    		logger.println(LogLevel.DEBUG,this, "=권하이상=> 오차점검 NG => 오차점검 OK");
					    	}	
						}
					}
					
    	 		}else if(YmCommonConst.TC_CN1PB07.equals(sTc) || YmCommonConst.TC_THCH580.equals(sTc)){
	    	 			
	    	 		String sPutSXLoc	= "";
					String sPutSYLoc	= "";
					String sPutXLeft 	= "";
					String sPutXRight 	= "";
					String sPutYUp 		= "";
					String sPutYDown 	= ""; 
					
					JDTORecord rulePutX = dao.getStackRuleInfo_002(sPutRLoc.substring(0, 6),
																   YmCommonConst.STACK_RULE_CD_XCD);
					if(rulePutX != null){
						sPutXLeft 	= StringHelper.evl(rulePutX.getFieldString("STACK_RULE_MIN"), "0");
						sPutXRight 	= StringHelper.evl(rulePutX.getFieldString("STACK_RULE_MAX"), "0");
					}
					
					JDTORecord rulePutY = dao.getStackRuleInfo_002(sPutRLoc.substring(0, 6),
																   YmCommonConst.STACK_RULE_CD_YCD);
					if(rulePutY != null){
						sPutYUp 	= StringHelper.evl(rulePutY.getFieldString("STACK_RULE_MAX"), "0");
						sPutYDown 	= StringHelper.evl(rulePutY.getFieldString("STACK_RULE_MIN"), "0");
					}
					
					if(sPutRLoc.length() == 10){
			    		JDTORecord putR = dao.getStackLayerInfoWithPk(sPutRLoc.substring(0, 6),
			    													  sPutRLoc.substring(6, 8),
																	  sPutRLoc.substring(8,10));
				    	if(putR != null){
				    		sPutSXLoc = StringHelper.evl(putR.getFieldString("STACK_LAYER_X_AXIS"), "0");					
							sPutSYLoc = StringHelper.evl(putR.getFieldString("STACK_LAYER_Y_AXIS"), "0");
						}else{
							/*
			    			 * 설비정보는 권상시 적치단정보를 삭제하기 때문에 좌표정보를 가져올 수 없다.
			    			 * 따라서, 적치열정보를 가져온다.
			    			 */
							JDTORecord putSubR = dao.getStackColInfoWithPk(sPutRLoc.substring(0, 6));
							if(putSubR != null){
								sPutSXLoc = StringHelper.evl(putSubR.getFieldString("STACK_COL_RULE_X_AXIS"), "0");					
								sPutSYLoc = StringHelper.evl(putSubR.getFieldString("STACK_COL_RULE_Y_AXIS"), "0");
							}
			    		}
			    	}
			    	
			    	
			    	///////////////////////////////////////////////////////////////
			    	////////////////오차점검 체크로직//////////////////////////////
					
			    	long lPutRXLoc	= Long.parseLong(sPutRXLoc);
					long lPutSXLoc	= Long.parseLong(sPutSXLoc);
					long lPutXLeft 	= Long.parseLong(sPutXLeft);
					long lPutXRight = Long.parseLong(sPutXRight);
					
					long lPutRYLoc	= Long.parseLong(sPutRYLoc);
			    	long lPutSYLoc	= Long.parseLong(sPutSYLoc);
					long lPutYUp 	= Long.parseLong(sPutYUp);
					long lPutYDown 	= Long.parseLong(sPutYDown);
			    	
			    	logger.println(LogLevel.DEBUG,this, "=lPutRXLoc	=>"+lPutRXLoc);
			    	logger.println(LogLevel.DEBUG,this, "=lPutSXLoc	=>"+lPutSXLoc);
			    	logger.println(LogLevel.DEBUG,this, "=lPutXLeft	=>"+lPutXLeft);
			    	logger.println(LogLevel.DEBUG,this, "=lPutXRight=>"+lPutXRight);
			    	logger.println(LogLevel.DEBUG,this, "=lPutRYLoc	=>"+lPutRYLoc);
			    	logger.println(LogLevel.DEBUG,this, "=lPutSYLoc	=>"+lPutSYLoc);
			    	logger.println(LogLevel.DEBUG,this, "=lPutYUp	=>"+lPutYUp);
			    	logger.println(LogLevel.DEBUG,this, "=lPutYDown	=>"+lPutYDown);
			    	
			    	if(
			    	   (
				    	   (lPutSXLoc - lPutXLeft)  <= lPutRXLoc 
				    		&&
				    	   (lPutSXLoc + lPutXRight) >= lPutRXLoc 
			    		)
			    		&&
			    		(
				    	   (lPutSYLoc + lPutYUp)   >= lPutRYLoc 
				    		&&
				    	   (lPutSYLoc - lPutYDown) <= lPutRYLoc 
			    		)
			    	){
			    		isSuccess = false;		
			    		logger.println(LogLevel.DEBUG,this, "=권하이상=> 오차점검 OK");
			    	}else{
			    		isSuccess = true;		
			    		logger.println(LogLevel.DEBUG,this, "=권하이상=> 오차점검 NG => 작업 재지시 CALL");
			    	}	
		    	}
		    	
		    	//작업재지시
		    	if(isSuccess){
		    		
		    		JDTORecord schRc = null;
			    	
			    	schRc = dao.getSchIdInfo(sYard_Id,
					    					 sBay_Gp,
					    					 sEquip_Kind,
					    					 sEquip_No,
					    					 sStockId,
					    					 sSch_Code
		   									 );
			    	if(schRc == null){
			    		logger.println(LogLevel.DEBUG,this, "권하이상..");
			    		logger.println(LogLevel.DEBUG,this, "=권하이상=> 스케쥴정보 존재안함.");
//			    	   	throw new EJBServiceException("=권하이상=> 스케쥴정보 존재안함.");
			    	    return isSuccess;
		    		}
		    		
			    	String sSchId   = StringHelper.evl(schRc.getFieldString("SCH_ID"), "");
			    	String sMsg   	= "입력위치와 좌표값이 일치하지 않습니다.";
			    	
		    		Boolean isTemp  = new Boolean(false);
		    		
		    		EJBConnector ejbConn = new EJBConnector("default","JNDICWrkOrdReg",this);
		    		
		    		if(YmCommonConst.YD_GP_1.equals(sYard_Id)){
		    		
    					/*isTemp  = (Boolean)ejbConn.trx("callACoilCraneMsgInfo",new  Class[]{String.class,
																					  		String.class},
																			   new Object[]{YmCommonConst.TC_THHC130,
																					  		sSchId});
						logger.println(LogLevel.DEBUG,this, "=권하이상=> A열연 COIL 작업 재지시");
						*/
					
					}else if(YmCommonConst.YD_GP_3.equals(sYard_Id)){
						
						isTemp  = (Boolean)ejbConn.trx("callBCoilCraneMsgInfo",new  Class[]{String.class,
																					  		String.class},
																			   new Object[]{sSchId,
																					  		sMsg});
						logger.println(LogLevel.DEBUG,this, "=권하이상=> B열연 COIL 작업 재지시");
					} 																	  		
				}																			  		
		    	///////////////////////////////////////////////////////////////
		    	///////////////////////////////////////////////////////////////
    	 	}
			
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess;
	}
		
       /**
	 * 오퍼레이션명 : 
	 *	2007.07.13 이정훈
	 *     B 열연 Coil 권하 이상
	 *
	 * param jDTORecord : 전문항목
        * param String	   : 실적처리방법
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     	 
	public boolean setCranePutCheckInfo_BCoil(JDTORecord jtR,
			      							String sFuncGbn){

		boolean isSuccess = false;
		
		logger.println(LogLevel.DEBUG,this, "============================");
		logger.println(LogLevel.DEBUG,this, "setCranePutCheckInfo_BCoil("+ jtR + ", "+sFuncGbn+") 시작");
		logger.println(LogLevel.DEBUG,this, "============================");
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String sEquipGp		= "";
			String sMessage 	= "";

			String sStackColGp  = "";
			String sStackBedGp	= "";
			String sStackLayerGp= "";
			String sStackLayerStat = "";
			String sStackLayerActiveStat = "";
			
			
			String sStockId_01  		= "";
			String sStackColGp_01  		= "";
			String sStackBedGp_01		= "";
			String sStackLayerGp_01		= "";
			String sStackLayerStat_01 	= "";
			String sJJANGGU_CHK  		= "";
			
			JDTORecord infoV = null;
			JDTORecord infoL = null;
			
			String sTc			= StringHelper.evl(jtR.getFieldString("TC"), "");
			String sYard_Id 	= StringHelper.evl(jtR.getFieldString("Yard_Id"), "");
			String sBay_Gp 		= StringHelper.evl(jtR.getFieldString("Bay_GP"), "");
			String sEquip_Kind 	= StringHelper.evl(jtR.getFieldString("Equip_Kind"), "");
			String sEquip_No 	= StringHelper.evl(jtR.getFieldString("Equip_No"), "");
			String sStockId     = StringHelper.evl(jtR.getFieldString("Coil_No"), "").trim();
			String sSch_Code 	= StringHelper.evl(jtR.getFieldString("Sch_Code"), "");
			String sPutRLoc		= StringHelper.evl(jtR.getFieldString("Put_Position"), "0");
			String sPutRXLoc	= StringHelper.evl(jtR.getFieldString("Put_X_Position"), "0");
			String sPutRYLoc	= StringHelper.evl(jtR.getFieldString("Put_Y_Position"), "0");
			sEquipGp			= sYard_Id+sBay_Gp+sEquip_Kind+sEquip_No;

			logger.println(LogLevel.DEBUG,this, "sTc="				+ sTc);
			logger.println(LogLevel.DEBUG,this, "sYard_Id="			+ sYard_Id);
			logger.println(LogLevel.DEBUG,this, "sBay_Gp="			+ sBay_Gp);
			logger.println(LogLevel.DEBUG,this, "sEquip_Kind="		+ sEquip_Kind);
			logger.println(LogLevel.DEBUG,this, "sEquip_No="		+ sEquip_No);
			logger.println(LogLevel.DEBUG,this, "sStockId="			+ sStockId+"=");
			logger.println(LogLevel.DEBUG,this, "sSch_Code="		+ sSch_Code);
			logger.println(LogLevel.DEBUG,this, "sPut_Position="	+ sPutRLoc);
			logger.println(LogLevel.DEBUG,this, "sPut_X_Position="	+ sPutRXLoc);
			logger.println(LogLevel.DEBUG,this, "sPut_Y_Position="	+ sPutRYLoc);

			if( YmCommonConst.CRANE_FUNC_N.equals(sFuncGbn) 	//정상전문처리
					&& YmCommonConst.TC_CN1PB07.equals(sTc)) {		  	//B열연 Coil 권하이상실적

				String sSchId1 	= "";	
				String sSchId2 	= "";
				String sPut1 		= "";	
				String sPut2 		= "";
				String sStockId1 	= "";	
				String sStockId2 	= "";

				JDTORecord schRc = dao.getSchIdInfo( 	sYard_Id,
	    					 							sBay_Gp,
	    					 							sEquip_Kind,
	    					 							sEquip_No,
	    					 							sStockId,
	    					 							sSch_Code
								 					);
				if(schRc == null){
					sMessage	= "스케쥴 정보가 존재하지 않습니다.";
					sendMessageToBCoilCrane(sEquipGp,sMessage);
					logger.println(LogLevel.DEBUG,this, "=Coil 권하이상 => 스케쥴 정보가 존재하지 않습니다");
					return true;
				}
				if(YmCommonConst.NEW_SCH_WORK_KIND_CTFL.equals(sEquip_Kind)){
					sMessage	= " 대차 출하 상차는 위치를 입력해 주세요";
					sendMessageToBCoilCrane(sEquipGp,sMessage);
					logger.println(LogLevel.DEBUG,this, "=Coil 권하이상 => 대차 출하 상차는 위치를 입력해 주세요");
					return true;
				}
				sSchId1   	= StringHelper.evl(schRc.getFieldString("SCH_ID"), "");
				sPut1       = StringHelper.evl(schRc.getFieldString("CRANE_WORD_PUT_LOC"), "");
				sStockId1  	= StringHelper.evl(schRc.getFieldString("STOCK_ID"), "");

				/*
				 *	1. 위치정보 가져오기
				 */
				List MapInfo	= dao.getBCoilXYLogicalInfo(	sYard_Id+sBay_Gp,
					      										sPutRXLoc,
					      										sPutRYLoc);

				logger.println(LogLevel.DEBUG,this, "=Coil 권하이상 =>MapInfo Size="+MapInfo.size());

				if(MapInfo.size() == 0 ){

					sMessage	= "논리적위치정보가 존재하지 않습니다.";
					sendMessageToBCoilCrane(sSchId1,sMessage);

					logger.println(LogLevel.DEBUG,this, "=Coil 권하이상 => 위치정보 존재 않함");
					return true;
				}   	
				if(MapInfo.size() > 1){

					sMessage	= "논리적위치정보가 2개 이상 존재합니다.";
					sendMessageToBCoilCrane(sSchId1,sMessage);

					logger.println(LogLevel.DEBUG,this, "=Coil 권하이상 => 위치정보 2개 이상 존재");
					return true;
				}   	

				
				infoV = (JDTORecord)MapInfo.get(0);
					
				sStackColGp 	= StringHelper.evl(infoV.getFieldString("STACK_COL_GP"), "");	 														 
				sStackBedGp 	= StringHelper.evl(infoV.getFieldString("STACK_BED_GP"), "");	 
				sStackLayerGp   = StringHelper.evl(infoV.getFieldString("STACK_LAYER_GP"), "");	
				sStackLayerStat = StringHelper.evl(infoV.getFieldString("STACK_LAYER_STAT"), "");
				sStackLayerActiveStat = StringHelper.evl(infoV.getFieldString("STACK_LAYER_ACTIVE_STAT"), "");
				
				
				
				logger.println(LogLevel.DEBUG,this, "=Coil 권하이상 =>sSchId1			="+sSchId1);
				logger.println(LogLevel.DEBUG,this, "=Coil 권하이상 =>sPut1			="+sPut1);
				logger.println(LogLevel.DEBUG,this, "=Coil 권하이상 =>sStackColGp		="+sStackColGp);
				logger.println(LogLevel.DEBUG,this, "=Coil 권하이상 =>sStackBedGp		="+sStackBedGp);
				logger.println(LogLevel.DEBUG,this, "=Coil 권하이상 =>sStackLayerGp	="+sStackLayerGp);
				logger.println(LogLevel.DEBUG,this, "=Coil 권하이상 =>sStackStatGp	="+sStackLayerStat);
				logger.println(LogLevel.DEBUG,this, "=Coil 권하이상 =>sStackStatGp	="+sStackLayerActiveStat);
				
				/*
				 *	2. To위치 정보 체크
				 */
				{ 	 														 
					if(YmCommonConst.STACK_LAYER_STAT_U.equals(sStackLayerStat)){
					
						sMessage	= "Coil 정보가 권상대기(U) 상태입니다..";
						sendMessageToBCoilCrane(sSchId1,sMessage);
				
						logger.println(LogLevel.DEBUG,this, "=Coil 권하이상 => 상단 Coil정보 U");
						return true;
					}else if(YmCommonConst.STACK_LAYER_STAT_P.equals(sStackLayerStat)){
					
						sMessage	= "Coil 정보가 권하대기(P) 상태입니다..";
						sendMessageToBCoilCrane(sSchId1,sMessage);
						logger.println(LogLevel.DEBUG,this, "=Coil 권하이상 => 상단 Coil정보 P");
						return true;
					}else if(YmCommonConst.STACK_LAYER_STAT_S.equals(sStackLayerStat)){
					
						sMessage	= "Coil 정보가 작업예약등록(S) 상태입니다..";
						sendMessageToBCoilCrane(sSchId1,sMessage);
						logger.println(LogLevel.DEBUG,this, "=Coil 권하이상 => 상단 Coil정보 S");
						return true;
					}else if(YmCommonConst.STACK_LAYER_STAT_L.equals(sStackLayerStat)){
					
						sMessage	= "Coil 정보가 존재합니다..";
						sendMessageToBCoilCrane(sSchId1,sMessage);
						logger.println(LogLevel.DEBUG,this, "=Coil 권하이상 => Coil 정보가 존재 L");
						return true;
					}else if(!YmCommonConst.STACK_LAYER_ACTIVE_STAT_O.equals(sStackLayerActiveStat)){
					
						sMessage	= "야드 Map 확인(적치 불가 or 비활성화)";
						sendMessageToBCoilCrane(sSchId1,sMessage);
						logger.println(LogLevel.DEBUG,this, "=Coil 권하이상 => 야드 Map 확인(적치 불가 or 비활성화)");
						return true;
					}
				}
				/*
				 * 2단 위치 일때 1단 정보 확인
				 */
				if (("02".equals(sStackLayerGp)))
				{
					List LayerInfo = dao.getStackLayerInfo(sStackColGp, sStackBedGp, sStackLayerGp);
					for(int inx = 0; inx < LayerInfo.size() ; inx++)
					{
						infoL = (JDTORecord)LayerInfo.get(inx);
						
						sStockId_01			= StringHelper.evl(infoL.getFieldString("STOCK_ID"), "");
						sStackColGp_01 		= StringHelper.evl(infoL.getFieldString("STACK_COL_GP"), "");	 														 
						sStackBedGp_01 		= StringHelper.evl(infoL.getFieldString("STACK_BED_GP"), "");	 
						sStackLayerGp_01  	= StringHelper.evl(infoL.getFieldString("STACK_LAYER_GP"), "");	
						sStackLayerStat_01 	= StringHelper.evl(infoL.getFieldString("STACK_LAYER_STAT"), "");
						sJJANGGU_CHK		= StringHelper.evl(infoL.getFieldString("JJANGGU_CHK"), "N");
						
						logger.println(LogLevel.DEBUG,this, "=Coil 권하이상 (1단 확인) =>STOCK_ID   		="+sStockId_01);
						logger.println(LogLevel.DEBUG,this, "=Coil 권하이상 (1단 확인) =>sStackColGp_01   	="+sStackColGp_01);
						logger.println(LogLevel.DEBUG,this, "=Coil 권하이상 (1단 확인) =>sStackBedGp_01		="+sStackBedGp_01);
						logger.println(LogLevel.DEBUG,this, "=Coil 권하이상 (1단 확인) =>sStackLayerGp_01	="+sStackLayerGp_01);
						logger.println(LogLevel.DEBUG,this, "=Coil 권하이상 (1단 확인) =>sStackStatGp_01	="+sStackLayerStat_01);	
						logger.println(LogLevel.DEBUG,this, "=Coil 권하이상 (1단 확인) =>짱구유무 체크	    	="+sJJANGGU_CHK);	
						
						if(YmCommonConst.STACK_LAYER_STAT_U.equals(sStackLayerStat_01)){
							
							sMessage	= "1단 Coil 정보가 권상대기(U) 상태입니다..";
							sendMessageToBCoilCrane(sSchId1,sMessage);
					
							logger.println(LogLevel.DEBUG,this, "=Coil 권하이상(1단) => 1단 Coil정보 U");
							return true;
						}else if(YmCommonConst.STACK_LAYER_STAT_P.equals(sStackLayerStat_01)){
						
							sMessage	= "1단 Coil 정보가 권하대기(P) 상태입니다..";
							sendMessageToBCoilCrane(sSchId1,sMessage);
							logger.println(LogLevel.DEBUG,this, "=Coil 권하이상(1단) => 1단 Coil정보 P");
							return true;
						}else if(YmCommonConst.STACK_LAYER_STAT_S.equals(sStackLayerStat_01)){
						
							sMessage	= "1단 Coil 정보가 작업예약등록(S) 상태입니다..";
							sendMessageToBCoilCrane(sSchId1,sMessage);
							logger.println(LogLevel.DEBUG,this, "=Coil 권하이상(1단) => 1단 Coil정보 S");
							return true;
						}else if(YmCommonConst.STACK_LAYER_STAT_E.equals(sStackLayerStat_01)){
						
							sMessage	= "1단에  Coil이 없습니다..";
							sendMessageToBCoilCrane(sSchId1,sMessage);
							logger.println(LogLevel.DEBUG,this, "=Coil 권하이상(1단) => 1단 Coil정보 E");
							return true;
						}else if("Y".equals(sJJANGGU_CHK)){
						
							sMessage	= "1단에  Coil이 짱구코일 상태 입니다.";
							sendMessageToBCoilCrane(sSchId1,sMessage);
							logger.println(LogLevel.DEBUG,this, "=Coil 권하이상(1단) => 1단 Coil정보 짱구");
							return true;
						}
					}
				}
			
			/*
			*	3. 구 To위치정보 초기화 및 신 To 위치 정보 셋팅
			*/
			
		
			/*
			* Crane 설비테이블에 셋팅된 SCH_ID 값을 가져온다.
			*/		
			
			JDTORecord craneV = dao.getEquipInfoWithEquipNo(sYard_Id,
													    sEquip_No);
			if(craneV == null){
				sMessage	= "크레인정보가 존재하지 않습니다.";
				sendMessageToBCoilCrane(sSchId1,sMessage);
				logger.println(LogLevel.DEBUG,this, "=Coil 권하이상 => 크레인정보가 존재하지 않습니다");
				return isSuccess;
			}
			
			String sTWprogStat = StringHelper.evl(craneV.getFieldString("WPROG_STAT"), "");
			String sTWbookId   = StringHelper.evl(craneV.getFieldString("WBOOK_ID"), "");
			
			logger.println(LogLevel.DEBUG,this, "sTWprogStat="	+ sTWprogStat);
			logger.println(LogLevel.DEBUG,this, "sTWbookId="	+ sTWbookId);
			
			/*
			*  전문을 통해 받은 스케쥴ID와 해당 크레인에 셋팅된 스케쥴ID가 같은지를 체크한다.
			*  같은경우에만 권하처리를 한다.
			* - 현재 작업진행상태가 '3'(PUT지시)
			* - 스케쥴ID가 동일한 경우
			*/	
			if(!YmCommonConst.WORK_PROG_STAT_3.equals(sTWprogStat)||
			 !sSchId1.equals(sTWbookId)){
			 	
			 	sMessage	= "스케쥴ID나 작업진행상태에러.";
			 	sendMessageToBCoilCrane(sSchId1,sMessage);
			 	logger.println(LogLevel.DEBUG,this, "=Coil 권하이상 => 스케쥴ID나 작업진행상태에러");
			 	return isSuccess;
			
			}
			
			/*
			*	기존 TO위치 초기화
			*/	
			if(!"".equals(sPut1)){
				logger.println(LogLevel.DEBUG,this, "=기존 TO위치 초기화");
				logger.println(LogLevel.DEBUG,this, "=sSchId1");
				
				
				
				//setCranePutCheckInfo_Coil_01(sPut1,sStockId1);
				
				Boolean isTemp1  = new Boolean(false);
				EJBConnector ejbConn1 = new EJBConnector("default","JNDICPutResReg",this);
				isTemp1  = (Boolean)ejbConn1.trx("setCranePutCheckInfo_Coil_01",
												new  Class[]{String.class,
															String.class},
												new Object[]{sPut1,
										 	      	        sStockId1});
				
			}
			
			/*
			*	신 TO위치 셋팅
			*/
			logger.println(LogLevel.DEBUG,this, "=Coil 권하이상 =>sSchId1			="+sSchId1);
			logger.println(LogLevel.DEBUG,this, "=Coil 권하이상 =>sPut1			="+sPut1);
			logger.println(LogLevel.DEBUG,this, "=Coil 권하이상 =>sStackColGp		="+sStackColGp);
			logger.println(LogLevel.DEBUG,this, "=Coil 권하이상 =>sStackBedGp		="+sStackBedGp);
			logger.println(LogLevel.DEBUG,this, "=Coil 권하이상 =>sStackLayerGp	="+sStackLayerGp);
			logger.println(LogLevel.DEBUG,this, "=Coil 권하이상 =>sStackStatGp	="+sStackLayerStat);
			
//			setCranePutCheckInfo_Coil_02(   sSchId1,
// 	      	        sStockId1,
// 	      	        YmCommonConst.STACK_LAYER_STAT_P, 
// 	      	        sStackColGp,
// 	      	        sStackBedGp, 
// 	      	        sStackLayerGp);
			
			logger.println(LogLevel.DEBUG,this, "=Coil 스케줄 권하위치 등록  =>sSchId1			="+sSchId1);
			
			Boolean isTemp2  = new Boolean(false);
			
			EJBConnector ejbConn1 = new EJBConnector("default","JNDICPutResReg",this);
			isTemp2  = (Boolean)ejbConn1.trx("setCranePutCheckInfo_Coil_02",
											new  Class[]{String.class,
														String.class,
														String.class,
														String.class,
														String.class,
														String.class},
											new Object[]{sSchId1,
									 	      	        sStockId1,
									 	      	        YmCommonConst.STACK_LAYER_STAT_P, 
									 	      	        sStackColGp,
									 	      	        sStackBedGp, 
									 	      	        sStackLayerGp});
			/*
			*	4. 재 작업지시 송신
			*/
			{
				Boolean isTemp  = new Boolean(false);
			
				EJBConnector ejbConn = new EJBConnector("default","JNDICWrkOrdReg",this);
			    
				logger.println(LogLevel.DEBUG,this, "=Coil 권하이상 =>sSchId1			="+sSchId1);
				/*isTemp  = (Boolean)ejbConn.trx("callBCoilCraneMsgInfo",new  Class[]{String.class},
						   new Object[]{sSchId1});
				 */
				isTemp  = (Boolean)ejbConn.trx("callCraneSchInfo",
			new  Class[]{String.class,
						String.class,
						String.class,
						String.class,
						String.class,
						String.class,
						String.class},
			new Object[]{YmCommonConst.TC_CN1PB02,
						sYard_Id,
							sBay_Gp,
							sEquip_Kind,
							sEquip_No,
							sSch_Code,
							""});

				logger.println(LogLevel.DEBUG,this, "=Coil 권하이상 => 재작업지시");
			}
			
			return true;																			  		
			}
			}catch(DAOException daoe){
			throw daoe;
			}catch(Exception e){
			throw new EJBServiceException(e);
			}
			return isSuccess;
			}
	
       /**
	 * 오퍼레이션명 : 
	 *	1.	COIL INFO
        *		Crane Put 정상실적에 관련된 상태정보를 등록 및 수정한다.
	 *
	 * param jDTORecord : 전문항목
        * param String	   : 실적처리방법
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 * @ejb.transaction type="RequiresNew"
	 */     	 
	public boolean setCranePutRtInfo_Coil(JDTORecord jtR,
									 	  String sFuncGbn){
		
		boolean isSuccess = false;
		int iReq = -1;
		logger.println(LogLevel.DEBUG,this, "============================");
		logger.println(LogLevel.DEBUG,this, "setCranePutRtInfo_Coil("+ jtR + ", "+sFuncGbn+") 시작");
		logger.println(LogLevel.DEBUG,this, "============================");

		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			String sTc				= StringHelper.evl(jtR.getFieldString("TC"), "");
			String sYard_Id 		= StringHelper.evl(jtR.getFieldString("Yard_Id"), "");
			String sBay_Gp 			= StringHelper.evl(jtR.getFieldString("Bay_GP"), "");
			String sEquip_Kind 		= StringHelper.evl(jtR.getFieldString("Equip_Kind"), "");
			String sEquip_No 		= StringHelper.evl(jtR.getFieldString("Equip_No"), "");
			String sStockId         = StringHelper.evl(jtR.getFieldString("Coil_No"), "").trim();
			String sSch_Code 		= StringHelper.evl(jtR.getFieldString("Sch_Code"), "");
			String sWork_Id 		= YmCommonUtil.getCurDataWithLegacy(
									  StringHelper.evl(jtR.getFieldString("Work_Id"), ""));
			String sPut_Position 	= StringHelper.evl(jtR.getFieldString("Put_Position"), "");
			String sPut_X_Position	= StringHelper.evl(jtR.getFieldString("Put_X_Position"), "");
			String sPut_Y_Position	= StringHelper.evl(jtR.getFieldString("Put_Y_Position"), "");
			
			logger.println(LogLevel.DEBUG,this, "sTc="				+ sTc);
			logger.println(LogLevel.DEBUG,this, "sYard_Id="			+ sYard_Id);
			logger.println(LogLevel.DEBUG,this, "sBay_Gp="			+ sBay_Gp);
			logger.println(LogLevel.DEBUG,this, "sEquip_Kind="		+ sEquip_Kind);
			logger.println(LogLevel.DEBUG,this, "sEquip_No="		+ sEquip_No);
			logger.println(LogLevel.DEBUG,this, "sStockId="			+ sStockId+"==");
			logger.println(LogLevel.DEBUG,this, "sSch_Code="		+ sSch_Code);
			logger.println(LogLevel.DEBUG,this, "sWork_Id="			+ sWork_Id);
			logger.println(LogLevel.DEBUG,this, "sPut_Position="	+ sPut_Position);
			logger.println(LogLevel.DEBUG,this, "sPut_X_Position="	+ sPut_X_Position);
			logger.println(LogLevel.DEBUG,this, "sPut_Y_Position="	+ sPut_Y_Position);
			
			if("".equals(sPut_Position)){
				logger.println(LogLevel.DEBUG,this, "권하에러..");
	    	   	logger.println(LogLevel.DEBUG,this, "권하위치정보 존재안함");
//	    	   	throw new EJBServiceException("=권하실적=> 권하위치정보 존재안함.");
	    	   	return isSuccess;
			}
			
			/*
			 *  sPut_Position=1H07020601
			 */			
			JDTORecord schRc2 = null;
	    	
	    	schRc2 = dao.getStackLayerChkInfo(sPut_Position.substring(0 , 6),
							    			  sPut_Position.substring(6 , 8),
							    			  sPut_Position.substring(8 , 10) 
	   									 	  );
	    	if(schRc2 == null){
	    		logger.println(LogLevel.DEBUG,this, "권하에러..");
	    	   	logger.println(LogLevel.DEBUG,this, "적치가 불가능한 저장위치");
	    	   	return isSuccess;
    		}
			
			
			
			/*
			 * Crane Put실적 저장품의 적치단정보를 가져온다.
			 */			
			JDTORecord schRc = null;
	    	
	    	schRc = dao.getSchIdInfo(sYard_Id,
			    					 sBay_Gp,
			    					 sEquip_Kind,
			    					 sEquip_No,
			    					 sStockId,
			    					 sSch_Code
   									 );
	    	if(schRc == null){
	    		logger.println(LogLevel.DEBUG,this, "권하에러..");
	    	   	logger.println(LogLevel.DEBUG,this, "스케쥴정보 가져올 수 없슴");
//	    	   	throw new EJBServiceException("=권하실적=> 스케쥴정보 존재안함.");
	    	   	return isSuccess;
    		}
    		
	    	String sGrobalSchId   = StringHelper.evl(schRc.getFieldString("SCH_ID"), "");
	    	String sGrobalWbookId = StringHelper.evl(schRc.getFieldString("WBOOK_ID"), "");
	    	/**
	    	 *	메소드 전역변수 셋팅
	    	 */
	    	jtR.setField("GROBAL_WBOOK_ID",sGrobalWbookId);	
	    	
	    	logger.println(LogLevel.DEBUG,this, "sGrobalSchId	="	+ sGrobalSchId);
	    	logger.println(LogLevel.DEBUG,this, "sGrobalWbookId	="	+ sGrobalWbookId);
	    	
	    	/*
			 * Crane 설비테이블에 셋팅된 SCH_ID 값을 가져온다.
			 */		
	    	JDTORecord craneV = null;
	    	
	    	craneV = dao.getEquipInfoWithEquipNo(sYard_Id,
	    										 sEquip_No);
	    	if(craneV == null){
	    		logger.println(LogLevel.DEBUG,this, "권하에러..");
	    	   	logger.println(LogLevel.DEBUG,this, "크레인정보 가져올 수 없슴");
//    			throw new EJBServiceException("=권하실적=> 크레인정보 존재안함.");
	    	   	return isSuccess;
    		}
    		
	    	String sTWprogStat = StringHelper.evl(craneV.getFieldString("WPROG_STAT"), "");
	    	String sTWbookId   = StringHelper.evl(craneV.getFieldString("WBOOK_ID"), "");
	    	
	    	logger.println(LogLevel.DEBUG,this, "sTWprogStat="	+ sTWprogStat);
	    	logger.println(LogLevel.DEBUG,this, "sTWbookId="	+ sTWbookId);
	    	
	    	/*
			 * 전문을 통해 받은 스케쥴ID와 
			 * 해당 크레인에 셋팅된 스케쥴ID가 같은지를 
			 * 체크한다.
			 * 같은경우에만 권하처리를 한다.
			 * - 현재 작업진행상태가 '3'(PUT지시)
			 * - 스케쥴ID가 동일한 경우
			 */	
	    	if(!YmCommonConst.WORK_PROG_STAT_3.equals(sTWprogStat)||
	    	   !sGrobalSchId.equals(sTWbookId)){
	    	   	
	    	   	logger.println(LogLevel.DEBUG,this, "권하에러..");
	    	   	logger.println(LogLevel.DEBUG,this, "스케쥴ID나 작업진행상태에러..");
//	    	   	throw new EJBServiceException("=권하실적=> 스케쥴ID나 작업진행상태에러.");
	    	   	return isSuccess;
	    	}
	    	
			/*
			 * Crane Put실적 저장품의 적치단정보를 가져온다.
			 * SADDLE인 경우 입측은 stack_layer_active_stat = 'C' 이다.
			 * 따라서 위 조건은 제외시킨다.
			 * tb_ym_stacklayer Table stack_layer_active_stat : 'O'(Open)
			 * tb_ym_stacklayer Table stack_layer_stat 	   	  : 'P'(Put Schedule 수행예정)
			 */			
			JDTORecord layerRc = null;
	    	
	    	layerRc = dao.getPutStackLayerListWithSchId(sGrobalSchId);
	    	
	    	if(layerRc == null){
    			logger.println(LogLevel.DEBUG,this, "권하에러..");
	    	   	logger.println(LogLevel.DEBUG,this, "적치단정보 가져올 수 없슴");
    			layerRc = createToLoc_01(sGrobalSchId,sTc);
    		}
    		
    		if(YmCommonConst.TC_THCH570.equals(sTc) || //A열연 Coil 권하실적
			   YmCommonConst.TC_CN1PB06.equals(sTc) ){ //B열연 Coil 권하실적
				
				JDTORecord tempRc = dao.getPutStackLayerListWithSchId(sGrobalSchId);
		    	
		    	if(tempRc == null){
		    		logger.println(LogLevel.DEBUG,this, "=권하실적=> TO위치정보가 존재하지 않습니다.\n 권하이상으로 처리하십시요.");
//	    			throw new EJBServiceException("=권하실적=> TO위치정보가 존재하지 않습니다.\n 권하이상으로 처리하십시요.");
		    	   	return isSuccess;
	    		}
	    	}
			
    		String sStackColGp	 = StringHelper.evl(layerRc.getFieldString("STACK_COL_GP"), "");
	    	String sStackBedGp	 = StringHelper.evl(layerRc.getFieldString("STACK_BED_GP"), "");
	    	String sStackLayerGp = StringHelper.evl(layerRc.getFieldString("STACK_LAYER_GP"), "");
	    	
	    	logger.println(LogLevel.DEBUG,this, "============Put DB Position============");
			logger.println(LogLevel.DEBUG,this, "sStackColGp="	 + sStackColGp);
			logger.println(LogLevel.DEBUG,this, "sStackBedGp="	 + sStackBedGp);
			logger.println(LogLevel.DEBUG,this, "sStackLayerGp=" + sStackLayerGp);
			
			/**
			 * LINE IN 권하 실적작업일 경우에는 MAP상에 존재하는
			 * 위치를 가지고 실적처리한다.
			 * 전문상에 존재하는 위치정보는 무시한다.
			 * 권하이상실적은 존재하지 않는다.
			 */
			if(YmCommonConst.MAIN_WORK_M.equals(sWork_Id)&&
			   YmCommonUtil.isLineInWork(sSch_Code)){
				
				sPut_Position = sStackColGp + 	
								sStackBedGp +
								sStackLayerGp;
								
				jtR.setField("Put_Position",sPut_Position);	
				logger.println(LogLevel.DEBUG,this, "=======LINE IN 작업 Position=======");
				logger.println(LogLevel.DEBUG,this, "Put_Position="	 + sPut_Position);									
			}
			
			/**
			 * A열연일 경우.
			 * 대차 권하 실적작업일 경우에는 MAP상에 존재하는
			 * 위치를 가지고 실적처리한다.
			 * 전문상에 존재하는 위치정보는 무시한다.
			 * LCAR 전문내용으로는 번지정보를 가져오지 못한다.
			 * 실제 MAP을 찾아서 번지정보를 알아낸다.
			 */
			if(YmCommonConst.TC_THCH570.equals(sTc)) {
			if(YmCommonConst.TC_1ATC03.equals(sStackColGp)||
			   YmCommonConst.TC_1BTC03.equals(sStackColGp)){
		    
		    	sPut_Position = sStackColGp + 	
								sStackBedGp +
								sStackLayerGp;
				
		    	jtR.setField("Put_Position",sPut_Position);	
		    	logger.println(LogLevel.DEBUG,this, "============A열연 대차 Position============");
				logger.println(LogLevel.DEBUG,this, "Put_Position="	 + sPut_Position);							
			}}
			
			/**
			 * A열연일 경우.
			 * 차량 권상 실적 작업일 경우에는 MAP상에 존재하는
			 * 위치를 가지고 실적처리한다.
			 * 전문상에 존재하는 위치정보는 무시한다.
			 * TA38888 전문내용으로는 번지정보를 가져오지 못한다.
			 * 실제 MAP을 찾아서 번지정보를 알아낸다.
			 */
			if(YmCommonConst.YD_GP_1.equals(sYard_Id)&&
			   sStackColGp.indexOf("TR") != -1){
		    
		    	sPut_Position  = sStackColGp + 	
								 sStackBedGp +
								 sStackLayerGp;
				
				jtR.setField("Put_Position",sPut_Position);	
				logger.println(LogLevel.DEBUG,this, "============A열연 차량 Position============");
				logger.println(LogLevel.DEBUG,this, "Put_Position="	 + sPut_Position);			
			}
			
			
	    	/*
			 * Crane이 실제 PUT한 전문 적치단 정보
			 */
			if(sPut_Position.length() == 10){
	    		
				String sL2StackColGp   = sPut_Position.substring(0, 6);
				String sL2StackBedGp   = sPut_Position.substring(6, 8);
				String sL2StackLayerGp = sPut_Position.substring(8,10);
				
				/*
				 * 최초 스케쥴 등록시점의 PUT위치와 
				 * 권하실적항목의 PUT위치를 비교한다.
				 */
	    		if(!sStackColGp.equals(sL2StackColGp)||
		    	   !sStackBedGp.equals(sL2StackBedGp)||	
		    	   !sStackLayerGp.equals(sL2StackLayerGp)){
		    		
		    		logger.println(LogLevel.DEBUG,this, "권하에러..");
	    	   		logger.println(LogLevel.DEBUG,this, "DB UP위치와 LEVEL2 전문 UP위치 같지 않음.");   	
	    	   		logger.println(LogLevel.DEBUG,this, "LEVEL2 전문 UP위치를 우선으로 적용."); 
	    	   		
	    	   		sStackColGp   = sL2StackColGp;
	    	   		sStackBedGp   = sL2StackBedGp;
	    	   		sStackLayerGp = sL2StackLayerGp;
	    	   		
	    	   		sPut_Position = sL2StackColGp + 	
									sL2StackBedGp +
									sL2StackLayerGp;
					
			    	jtR.setField("Put_Position",sPut_Position);		
			    	
			    	/**
			    	 * 권하이상처리를 설비에 할 경우.
			    	 * 설비 콘베이어 정보를 생성한다.
			    	 */
			    	{
				    	String sPutUsageCd = YmCommonUtil.getStackColInfoWithPk(sL2StackColGp);
						
				   		if(YmCommonConst.STACK_COL_USAGE_CD_XX.equals(sPutUsageCd)	||// COIL 비상적치위치	
						   YmCommonConst.STACK_COL_USAGE_CD_FE.equals(sPutUsageCd)	||// COIL HFL보급위치
						   YmCommonConst.STACK_COL_USAGE_CD_FI.equals(sPutUsageCd)	||// COIL HFLTAKEIN위치
						   YmCommonConst.STACK_COL_USAGE_CD_FD.equals(sPutUsageCd)	||// COIL HFL추출위치
						   YmCommonConst.STACK_COL_USAGE_CD_KE.equals(sPutUsageCd)	||// COIL SPM보급위치
						   YmCommonConst.STACK_COL_USAGE_CD_QE.equals(sPutUsageCd)	||// COIL EQL보급위치
						   YmCommonConst.STACK_COL_USAGE_CD_KI.equals(sPutUsageCd)	||// COIL SPMTAKEIN위치
						   YmCommonConst.STACK_COL_USAGE_CD_QD.equals(sPutUsageCd)	||// COIL EQL추출위치
						   YmCommonConst.STACK_COL_USAGE_CD_KD.equals(sPutUsageCd)	){// COIL SPM추출위치
						   	
							int iSeq = YmCommonDB.shiftConveyorInfo(sL2StackColGp,
																	sL2StackBedGp);
						}
					}	
	    	   	}
			}
			
	    	/*
			 * 작업실적 Table에 권하실적을 Update
			 */		
			iReq = dao.updateCranePutWrslt(getPutRtData(jtR,sGrobalSchId,sFuncGbn));
			
			/* 
			 * 적치단 Put위치에 다른 코일이 있을 경우.
			 * 해당동의 XX번지로 저장품 MAP을 수정한다.
			 */	
			iReq = YmCommonDB.updateLegacyStockId_Coil(dao,
												  	   sStackColGp,
												  	   sStackBedGp,
												  	   sStackLayerGp,
												  	   sStockId);
			
			if(iReq < 1){
				logger.println(LogLevel.DEBUG,this, "****권하처리 위치에 실물or예정코일이 존재 합니다. 확인 요망");
				return isSuccess;
			}
			//Crane 작업 실적 등록
//			String sTempLayer = sStackColGp.substring(0,2)+
//								YmCommonConst.STACK_COL_USAGE_CD_XX+
//								YmCommonConst.STACK_BED_GP_01;
			
//			JDTORecord putJr = dao.getStackLayerInfoWithPk(sStackColGp,
//															sStackBedGp,
//															sStackLayerGp);
//
//			String sToStockId 	= "00";
//			String sToStat 		= "";
//			
//			if(putJr != null){
//			
//			sToStockId 	= StringHelper.evl(putJr.getFieldString("STOCK_ID"), "");
//			sToStat 	= StringHelper.evl(putJr.getFieldString("STACK_LAYER_STAT"), "");
//			}
//			
//			logger.println(LogLevel.DEBUG,this, "sToStockId:"+sToStockId+" ,sToStat"+ sToStat+" ,sStackColGp"+sStackColGp);
			
			//CTS 권하 위치에 다른 코일이 존재 하는 경우 20111121
//			if(!"".equals(sToStockId)&&
//					   !"0".equals(sToStockId.substring(0,1))&&
//					   !sStockId.equals(sToStockId)&&
//					   (sStackColGp.substring(2, 4).equals("SR") ||sStackColGp.substring(2, 4).equals("SL") )		   
//					   ){
//				logger.println(LogLevel.DEBUG,this, "=권하실적=> 세들상에 권하 위치에 작업중인 코일이 존재 합니다.");
//				 return isSuccess;
//			}
			
//			if(!"".equals(sToStockId)&&
//					   !sStockId.equals(sToStockId)&&
//					   !sStackColGp.substring(2, 4).equals("TR")		   
//					   ){
//			EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
//    	 	ejbConn.trx("insertUpPutWrslRtData",new  Class[]{String.class,String.class,String.class,String.class,String.class,String.class},
//														new Object[]{sToStockId,sStackColGp,sTempLayer,"CYMM", sStackColGp.substring(0,1),"userym"});
//             
//			}
            
	    	/* 
			 * 적치단 Put위치를 적치상태로 변경
			 * tb_ym_stacklayer Table : stock_id = Coil No
			 * tb_ym_stacklayer Table : stack_layer_stat	   = 'L'(적치중)
			 */	
	    	
	    	String rLayerStat = YmCommonConst.STACK_LAYER_STAT_L;
	    	/**
	    	 *	보조작업으로 작업한 대상이 작업예약ID를 가지고 있으면 'S'로 셋팅
	    	 */
	    	if(YmCommonConst.SUB_WORK_S.equals(sWork_Id)){
		    	
		    	JDTORecord stockJr = dao.getStockInfo(sStockId);
				if(stockJr != null){
					String sWbookId = StringHelper.evl(stockJr.getFieldString("WBOOK_ID"), "");
					if(!"".equals(sWbookId)){
						rLayerStat = YmCommonConst.STACK_LAYER_STAT_S;
					}
				}
		    }
	    	
	   		//****************************************************************************
	    	//2009.12.14 정종균 :권하처리 시 명확한 위치로 update 처리 하기 위하여 변경
	    	
	    	String sPutUsageCd = YmCommonUtil.getStackColInfoWithPk(sStackColGp);
			
	   		if(YmCommonConst.STACK_COL_USAGE_CD_XX.equals(sPutUsageCd)	||// COIL 비상적치위치	
			   YmCommonConst.STACK_COL_USAGE_CD_FE.equals(sPutUsageCd)	||// COIL HFL보급위치
			   YmCommonConst.STACK_COL_USAGE_CD_FI.equals(sPutUsageCd)	||// COIL HFLTAKEIN위치
			   YmCommonConst.STACK_COL_USAGE_CD_FD.equals(sPutUsageCd)	||// COIL HFL추출위치
			   YmCommonConst.STACK_COL_USAGE_CD_KE.equals(sPutUsageCd)	||// COIL SPM보급위치
			   YmCommonConst.STACK_COL_USAGE_CD_QE.equals(sPutUsageCd)	||// COIL EQL보급위치
			   YmCommonConst.STACK_COL_USAGE_CD_QD.equals(sPutUsageCd)	||// COIL EQL추출위치
			   YmCommonConst.STACK_COL_USAGE_CD_KI.equals(sPutUsageCd)	||// COIL SPMTAKEIN위치
			   YmCommonConst.STACK_COL_USAGE_CD_KD.equals(sPutUsageCd)	){// COIL SPM추출위치
	   			
	   			
	   			JDTORecord tempRc2 = dao.getStackLayerInfoWithStockIdChk(sStockId);
	   			
	   			if(tempRc2 != null){
	   			//설비인 경우 설비에서 찾아서 UPDATE 함.
	   			logger.println(LogLevel.DEBUG,this, "설비에서 권하상태 완료 처리 >>>>>>>");
	   			iReq = dao.updateCraneStackLayerStat2(sStackColGp,
						 sStackLayerGp,
						 sStockId,
						 rLayerStat);
	   			} else {
		   			logger.println(LogLevel.DEBUG,this, "직보급시 설비에서  권하상태 완료 처리 >>>>>>>");
			    	iReq = dao.updateCraneStackLayerStat(sStackColGp,
			    										 sStackBedGp,
			    										 sStackLayerGp,
			    										 sStockId,
			    										 rLayerStat);
	   			}
	   		}else {	    	
	   			logger.println(LogLevel.DEBUG,this, "야드상에서 권하상태 완료 처리 >>>>>>>");
		    	iReq = dao.updateCraneStackLayerStat(sStackColGp,
		    										 sStackBedGp,
		    										 sStackLayerGp,
		    										 sStockId,
		    										 rLayerStat);
	   		}
	   		
	   		//****************************************************************************
	   		
    		/*
	    	 * 적치단이 '01'단일 경우
			 * 적치단상태가 'L', 'P' 이면 상단 적치단 정보를 적치가능상태로 변경
			 */	
	    	if(YmCommonConst.STACK_LAYER_GP_01.equals(sStackLayerGp)){
	    		
	    		/*
		    	 * A.B열연 Coil 권하실적	
		    	 * 상단 왼쪽 상태정보를 UPDATE
		    	 * 상단 오른쪽 상태정보를 UPDATE
		    	 */	
		    	iReq = YmCommonDB.setCoilUpperState_E(sStackColGp,
		    							 	   		  sStackBedGp,
		    							 	   		  sStackLayerGp);
    		}
	    	
	    	/*
			 * 크레인의 권하실적이 발생하면 크레인에 할당된 
			 * 저장품 적치단 정보를 삭제한다.
			 * tb_ym_stacklayer Table : stock_id = ''(Empty)
			 * tb_ym_stacklayer Table : stack_layer_stat	   = 'E'(적치가능)
			 */	
	    	iReq = dao.updateCraneStackLayerStat(sYard_Id+sBay_Gp+
	    										 YmCommonConst.EQUIP_KIND_CR+sEquip_No,
												 YmCommonConst.STACK_BED_GP_01,
												 YmCommonConst.STACK_LAYER_GP_01,
	    										 "",
	    										 YmCommonConst.STACK_LAYER_STAT_E);
	    	
	    	// CGS 추가
	    	// 권하시 스크랩 박스에 대한 상태를 변경한다.
	    	// 크레인작업요구현황조회 화면에서 SCH기동 이벤트를 처리하기 위해서 추가함.
	    	// 작업예약 단계에서 적치 단과 저장품 정보가 변경됨.
	    	// 수정.
	    	if (sStockId.substring(0,1).equals("S")) {
	    		logger.println(LogLevel.DEBUG,this, "Scrap 권하시에는 적치 단의 상태를 'E'로 변경한다.");
	    		iReq = dao.updateCraneStackLayerStat(sStackColGp,
	    										 sStackBedGp,
	    								 		 sStackLayerGp,
	    										 /*sStockId,*/
	    								 		 "",
	    										 YmCommonConst.STACK_LAYER_STAT_E);
	    	}
	    	
	    	/*
			 * 저장품 이동조건 셋팅
			 */
			{
				iReq = setStockMoveTerm_Coil(sStockId,
											 sSch_Code,
											 sPut_Position); 
			}
			
	    	/*
			 * 작업예약 TABLE,저장품 TABLE 셋팅
			 */	
	    	{
	    		/**
	    		 * 주작업 저장품일 경우에만 적용
	    		 */
	    		if(YmCommonConst.MAIN_WORK_M.equals(sWork_Id)){
			    	/*
					 * 저장품 Table의 Wbook_id 항목을 Update
					 * tb_ym_stock Table wbook_id : ''(empty)
					 */	 
					iReq = dao.updateStockWbookId(sStockId,""); 
				}
				/*
				 * 저장품 Table에 Wbook_id가 존재하는지 Check
				 */
				JDTORecord countRc = dao.getStockWbookId(sGrobalWbookId); 
		    	
		    	/*
				 * 저장품 Table에 Wbook_id가 존재하지 않으면 작업예약 Table Delete
				 */	  
		    	if(countRc == null){
		    		iReq = dao.deleteWbookInfo(sGrobalWbookId);  	 
				}  
			}
			
			/*
			 * Schedule Table Delete
			 */	 
	    	iReq = dao.deleteSchInfo(sGrobalSchId);  	
						   
			/*
			 * Coil 공통 Table 저장위치 Update 
			 */	 
			iReq = dao.updateCoilCommonLocInfo(sStockId,sPut_Position);  
			
			/*
			 *	저장품 예상PUT 위치 CLEAR
			 *  저장품지정조회화면에서 예약상태로 남아있는것을 방지하기 위해
			 */
			{
				JDTORecord stockRc = dao.getStockInfo(sStockId);
				if(stockRc != null){
	    			String sCarunloadBay = StringHelper.evl(stockRc.getFieldString("CARUNLOAD_PUT_LOC"), "");//하차PUT위치
	    			if(sPut_Position.equals(sCarunloadBay)){
						iReq = dao.updateStockPutLocWithStockId(sStockId,"");  
					}
					logger.println(LogLevel.DEBUG,this, "저장품 예상PUT 위치 CLEAR A="+ sCarunloadBay);
	    			logger.println(LogLevel.DEBUG,this, "저장품 예상PUT 위치 CLEAR B="+ sPut_Position);
	    		}
	    	}
			/*
			 * Coil 이상실적 발생시 처리
			 */
			if(
			   YmCommonConst.TC_THCH580.equals(sTc)||  //A열연 Coil 권하이상실적
			   YmCommonConst.TC_CN1PB07.equals(sTc)	   //B열연 Coil 권하이상실적
			  ){
				
				isSuccess = setCraneISangPutRtInfo_Coil(jtR,sStockId);
			}  
			 
			/*
			 * 2007.04.11 
			 * 대차 출하를 위한 이적 물량 재 작업 예약
			 */
			logger.println(LogLevel.DEBUG,this, "대차출하 = "+ sWork_Id);
			logger.println(LogLevel.DEBUG,this, "대차출하 = "+ sSch_Code);
			logger.println(LogLevel.DEBUG,this, "대차출하 = "+ sStackColGp.substring(0,4));
			
			if( YmCommonConst.MAIN_WORK_M.equals(sWork_Id) &&
			    YmCommonConst.NEW_SCH_WORK_KIND_CTFL.equals(sSch_Code) &&
			     "3D".equals(sStackColGp.substring(0,2))&&
			    !YmCommonConst.EQUIP_KIND_TC.equals(sStackColGp.substring(2,4)))
			{
				EJBConnector ejbConnCTFL 	= new EJBConnector("default","JNDICTnkSpplWrkOrdReg",this);
				ejbConnCTFL.trx("callTankLoadWbookInfo",new Class[]{String.class,
																String.class,
																String.class,
																String.class},
											 	   new Object[]{sStockId,
																sYard_Id,
																sBay_Gp,
											 	   				"U"});
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
	 *	1.	COIL INFO
        *		Crane Put 이상실적에 관련된 상태정보를 등록 및 수정한다.
        *		X,Y 좌표값에 의한 위치검색 모듈 추가해야한다.
	 *
	 * param jDTORecord : 전문항목
        * param String	   : 저장품
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
	public boolean setCraneISangPutRtInfo_Coil(JDTORecord jtR,
											   String sStockId){
		
		boolean isSuccess = false;
		int iReq = -1;
		logger.println(LogLevel.DEBUG,this, "============================");
		logger.println(LogLevel.DEBUG,this, "setCraneISangPutRtInfo_Coil("+ jtR + ", "+sStockId+") 시작");
		logger.println(LogLevel.DEBUG,this, "============================");

		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
	    	String sWorkOrder_Ps 	= StringHelper.evl(jtR.getFieldString("WorkOrder_Position"), ""); // 작업지시위치

			logger.println(LogLevel.DEBUG,this, "sWorkOrder_Ps="	+ sWorkOrder_Ps);
			logger.println(LogLevel.DEBUG,this, "==이상실적 발생시 작업지시위치 CLEAR==");
			
			/*
			 * 이상실적 발생시 작업지시위치 CLEAR
			 */
			{  
				String sOrginStackColGp = "";
				String sOrginStackBedGp = "";
				String sOrginStackLayerGp = "";
				
				if(sWorkOrder_Ps.length() == 10){
		    		
					sOrginStackColGp 	= sWorkOrder_Ps.substring(0,6);
					sOrginStackBedGp 	= sWorkOrder_Ps.substring(6,8);
					sOrginStackLayerGp 	= sWorkOrder_Ps.substring(8,10);
				}	
				
				logger.println(LogLevel.DEBUG,this, "============Put Origin Position============");
				logger.println(LogLevel.DEBUG,this, "sOrginStackColGp="	 + sOrginStackColGp);
				logger.println(LogLevel.DEBUG,this, "sOrginStackBedGp="	 + sOrginStackBedGp);
				logger.println(LogLevel.DEBUG,this, "sOrginStackLayerGp="+ sOrginStackLayerGp);
				
				/**
		    	 * ORIGIN 위치가 설비일 경우.
		    	 * 설비 콘베이어 정보를 삭제한다.
		    	 */
		    	String sPutUsageCd = YmCommonUtil.getStackColInfoWithPk(sOrginStackColGp);
		   		
		   		if(YmCommonConst.STACK_COL_USAGE_CD_XX.equals(sPutUsageCd)	||// COIL 비상적치위치	
				   YmCommonConst.STACK_COL_USAGE_CD_FE.equals(sPutUsageCd)	||// COIL HFL보급위치
				   YmCommonConst.STACK_COL_USAGE_CD_FI.equals(sPutUsageCd)	||// COIL HFLTAKEIN위치
				   YmCommonConst.STACK_COL_USAGE_CD_FD.equals(sPutUsageCd)	||// COIL HFL추출위치
				   YmCommonConst.STACK_COL_USAGE_CD_KE.equals(sPutUsageCd)	||// COIL SPM보급위치
				   YmCommonConst.STACK_COL_USAGE_CD_QE.equals(sPutUsageCd)	||// COIL EQL보급위치
				   YmCommonConst.STACK_COL_USAGE_CD_QD.equals(sPutUsageCd)	||// COIL EQL추출위치
				   YmCommonConst.STACK_COL_USAGE_CD_KI.equals(sPutUsageCd)	||// COIL SPMTAKEIN위치
				   YmCommonConst.STACK_COL_USAGE_CD_KD.equals(sPutUsageCd)	){// COIL SPM추출위치
				   	
					int iSeq = YmCommonDB.deleteConveyorInfo(sOrginStackColGp,
															 sStockId);
				}else{
				
					JDTORecord layerInfo = dao.getStackLayerInfoWithPk(sOrginStackColGp,
																	   sOrginStackBedGp,
																	   sOrginStackLayerGp);
					String sLayerStat = "";
					
					if(layerInfo != null){
					
						sLayerStat 	= StringHelper.evl(layerInfo.getFieldString("STACK_LAYER_STAT"), "");
					}
					/**
					 * 정상적인 이상실적 처리
					 *	- 구 권하위치 'P'상태임. 따라서 스케쥴 TO위치 삭제
					 * TO위치 FAIL시 이상실적 처리
					 *	- 신,구 권하위치가 동일함. 따라서 구 권하위치 처리 필요없슴
					 */
					if(YmCommonConst.STACK_LAYER_STAT_P.equals(sLayerStat)){
					 	
					 	/* 
						 * 적치단 ORIGINAL 위치를 적치상태로 변경
						 * tb_ym_stacklayer Table : stock_id = ''(empty)
						 * tb_ym_stacklayer Table : stack_layer_stat	   = 'E'(적치가능)
						 */	
				    	iReq = dao.updateCraneStackLayerStat(sOrginStackColGp,
				    										 sOrginStackBedGp,
				    										 sOrginStackLayerGp,
				    										 "",
				    										 YmCommonConst.STACK_LAYER_STAT_E);
			    	}
			    	/**
			    	 * 이상실적 발생시 Original To 위치의 상단정보를 변경한다.
			    	 * 업무적 충돌발생여부는 존재하나, 지금은 무조건 
			    	 * 'V'(하단에 적치되지 않은 위치)값으로 셋팅한다.
			    	 */
			    	if(YmCommonConst.STACK_LAYER_GP_01.equals(sOrginStackLayerGp)){
			    		
			    		/*
				    	 * A.B열연 Coil ORIGINAL 위치	
				    	 * 상단 왼쪽 상태정보를 UPDATE
				    	 * 상단 오른쪽 상태정보를 UPDATE
				    	 */	
				    	iReq = YmCommonDB.setCoilUpperState_V(sOrginStackColGp,
				    							 	   		  sOrginStackBedGp,
				    							 	   		  sOrginStackLayerGp);
		    		}
		    		
		    		/*
					 *	저장품 예상PUT 위치 CLEAR
					 *  저장품지정조회화면에서 예약상태로 남아있는것을 방지하기 위해
					 */
					 {
						JDTORecord stockRc = dao.getStockInfo(sStockId);
						if(stockRc != null){
			    			String sCarunloadBay = StringHelper.evl(stockRc.getFieldString("CARUNLOAD_PUT_LOC"), "");//하차PUT위치
			    			
			    			String sPutLoc = sOrginStackColGp+
			    			                 sOrginStackColGp+
			    			                 sOrginStackLayerGp;
			    			
			    			if(sPutLoc.equals(sCarunloadBay)){
								iReq = dao.updateStockPutLocWithStockId(sStockId,"");  
							}
							logger.println(LogLevel.DEBUG,this, "저장품 예상PUT 위치 CLEAR A="+ sCarunloadBay);
	    					logger.println(LogLevel.DEBUG,this, "저장품 예상PUT 위치 CLEAR B="+ sPutLoc);
			    		}
			    	}   			
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
	 *	2.	COIL INFO
        *		야드작업실적송신
        *		업무처리에 따른 실적을 조업,출하 등
        * 	       타업무에 내부인터페이스를 통해 전달한다.
        *
        * param dao 			: DAO
        * param jDTORecord 	: 전문항목
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
	public boolean callInnerWorkInfo_Coil(JDTORecord jtR){
								     	  	
		Boolean isSuccess = new Boolean(false);
		String selCardNo = "";
		String sCMBN_CARLD_YN ="";
		String szTRANS_EQUIPMENT_TYPE ="";
		JDTORecord recInTemp			= null;
		YdCarSchDao	ydCarSchDao			= new YdCarSchDao();
		YdCarFtmvMtlDao ydCarftmvmtlDao = new YdCarFtmvMtlDao();
		YdStockDAO ydStockDao = new YdStockDAO();
		
		logger.println(LogLevel.DEBUG,this, "============================");
		logger.println(LogLevel.DEBUG,this, "callInnerWorkInfo_Coil("+ jtR + ") 시작");
		logger.println(LogLevel.DEBUG,this, "============================");

		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String tWork_Id 		= YmCommonUtil.getCurDataWithLegacy(
									  StringHelper.evl(jtR.getFieldString("Work_Id"), ""));
			logger.println(LogLevel.DEBUG,this, "callInnerWorkInfo_Coil(): " + tWork_Id);
			if(YmCommonConst.SUB_WORK_S.equals(tWork_Id)){
				logger.println(LogLevel.DEBUG,this, "내부IF호출===보조작업대상으로 작업실적전송 SKIP.===");
				return false;
			}
									  
			/*
			 *	1.	출하
			 *		Coil을 제품 야드로 입고시
			 */
			{
				String sStockId	= StringHelper.evl(jtR.getFieldString("Coil_No"), "").trim();
				String sYardGp	= StringHelper.evl(jtR.getFieldString("Yard_Id"), "");
				String sSchCode = StringHelper.evl(jtR.getFieldString("Sch_Code"), "");
				
			    //AB열연 ##############################################################################################
				/*수냉 대상재 확대
				 * 
				 * if(YmCommonConst.NEW_SCH_WORK_KIND_CTFU.equals(sSchCode))
				{
					logger.println(LogLevel.DEBUG,this, "내부IF호출=== 대차 출하 작업실적전송 SKIP.===");
					return false;
				}
				*/		
				JDTORecord dmRc = null;
				
				if(!"".equals(sStockId)){
					dmRc = dao.getYMDM001Info(sStockId);
			    }
		    	if(dmRc != null){
		    		
					String sPut_Position= StringHelper.evl(dmRc.getFieldString("PUT_POSITION"), "");
					String sCURR_PROG_CD= StringHelper.evl(dmRc.getFieldString("CURR_PROG_CD"), "");
				
					//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                    //코일입고작업실적
					JDTORecord tcRecordDM = null;
					tcRecordDM = JDTORecordFactory.getInstance().create(); 
					tcRecordDM.setField("GOODS_NO",sStockId);
					tcRecordDM.setField("YD_GP",sYardGp);
					tcRecordDM.setField("STORE_LOC",sPut_Position);
					tcRecordDM.setField("CURR_PROG_CD",sCURR_PROG_CD);
					
					//인터페이스 전문 호출
					EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
					isSuccess = (Boolean)ejbConn.trx("getYDDMR001",new Class[]{JDTORecord.class},
					  	  	 new Object[]{tcRecordDM}); 
                    logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 코일입고작업실적.===");
                   //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                    						
				}
			}
			
			/*
			 *	2.	출하
			 *		Coil 제품창고 출고 상차 작업 시 첫 Coil 상차 권하 시
			 *		Coil 제품창고 출고 상차 작업 시 마지막 Coil 상차 권하 시
			 */
			{
				String sStockId	= StringHelper.evl(jtR.getFieldString("Coil_No"), "").trim();
				String sSchCode = StringHelper.evl(jtR.getFieldString("Sch_Code"), "");
				String sYardGp	= StringHelper.evl(jtR.getFieldString("Yard_Id"), "");
				String szYD_CAR_SCH_ID ="";
				String sYD_STK_BED_NO= "";
				
				//육송출하고도화
				ymCommonDAO dao2 = ymCommonDAO.getInstance();
				 List chkList = null;
				String QueryId 	= "com.inisteel.cim.yd.dao.chklist";
				chkList = dao2.getCommonList(QueryId, new Object[]{});

			    JDTORecord unloadPointrec = (JDTORecord)chkList.get(0);
		    	String CHK   = StringHelper.evl(unloadPointrec.getFieldString("CHK"), "");	    	
		    	logger.println(LogLevel.DEBUG,this, "◑◑◑◑◑  CHK:"+CHK);
		    	
		    	//임가공 유무 
		    	List pmList2  = dao.getYmPoFrtoInfo(sStockId);
		    	
		    	//PDA차량 구분 
		    	JDTORecord dmRcPDA = dao.getCarSchInfo(sStockId);					    
		    	if(dmRcPDA != null){				    		
					szTRANS_EQUIPMENT_TYPE	= StringHelper.evl(dmRcPDA.getFieldString("TRANS_EQUIPMENT_TYPE"), ""); 
		    	}
				
				
				//*****************************************************************************************************************
				///////////////////////////////제품출하 상차개시 완료  및 차량스케줄 관리 ///////////////////////////////////////////////
				//*****************************************************************************************************************
				if(YmCommonConst.NEW_SCH_WORK_KIND_GVFL.equals(sSchCode)|| //COIL 제품출하상차
				   YmCommonConst.NEW_SCH_WORK_KIND_GVF1.equals(sSchCode)|| //Coil 제품출하상차
		    	   YmCommonConst.NEW_SCH_WORK_KIND_GVF2.equals(sSchCode)||
		    	   YmCommonConst.NEW_SCH_WORK_KIND_GTFL.equals(sSchCode)|| // COIL 제품출하상차
		    	   YmCommonConst.NEW_SCH_WORK_KIND_GTF1.equals(sSchCode)|| // Coil 제품출하상차
		    	   YmCommonConst.NEW_SCH_WORK_KIND_GTF2.equals(sSchCode)||    		    
		    	   YmCommonConst.NEW_SCH_WORK_KIND_GPFL.equals(sSchCode)|| // COIL 제품출하상차
		    	   YmCommonConst.NEW_SCH_WORK_KIND_GPF1.equals(sSchCode)|| // Coil 제품출하상차
		    	   YmCommonConst.NEW_SCH_WORK_KIND_GPF2.equals(sSchCode)||
	
				   YmCommonConst.NEW_SCH_WORK_KIND_GVML.equals(sSchCode)|| // COIL 제품이송상차	
				   YmCommonConst.NEW_SCH_WORK_KIND_GVM2.equals(sSchCode)|| // COIL 제품이송상차
	   	  		   YmCommonConst.NEW_SCH_WORK_KIND_GVM3.equals(sSchCode)|| // COIL 제품이송상차
				   YmCommonConst.NEW_SCH_WORK_KIND_GVMU.equals(sSchCode)|| // COIL 제품이송하차	
				   YmCommonConst.NEW_SCH_WORK_KIND_GVM4.equals(sSchCode)|| // COIL 제품이송하차	 
		  		   YmCommonConst.NEW_SCH_WORK_KIND_GVM5.equals(sSchCode)
					){ //Coil 제품출하상차	
					 
					logger.println(LogLevel.DEBUG,this, "Coil 제품창고 출고 상차 작업:"+sSchCode);
					
					//임가공 이송은 제외 AND 출하PDA 도 제외
					if (pmList2.size() <= 0 && !"P".equals(szTRANS_EQUIPMENT_TYPE)) {
					 
						List 	   dmList  = null; // Card No,운송지시일자,순번이 같은 저장품정보
						JDTORecord dmRc    = null; // 저장품정보
						String 	   sSmt    = "";   // 저장품이동조건
						int		   iDmSize = 0;    // Card No,운송지시일자,순번이 같은 저장품 갯수
						int		   iDmFns  = 0;    // 상차완료인 저장품 갯수	
						
						if(!"".equals(sStockId)){
							
							dmRc = dao.getCarSchInfo(sStockId);					    
					    	if(dmRc != null){				    		
								szYD_CAR_SCH_ID	= StringHelper.evl(dmRc.getFieldString("YD_CAR_SCH_ID"), "");
								sYD_STK_BED_NO	= StringHelper.evl(dmRc.getFieldString("STACK_BED_GP"), "");
								sCMBN_CARLD_YN	= StringHelper.evl(dmRc.getFieldString("CMBN_CARLD_YN"), "");
					    	}
					    	 
	    			    	if(!CHK.equals("Y") ){ //기존방식
	    			    		/*--ym.facilitystatus.facilityinquiry.CraneSchDAO.getYmDmCommonInfo_PIDEV*/
								dmList  = dao.getYmDmCommonInfo(sStockId);
	    			    	}else{
	    			    		/*--ym.facilitystatus.facilityinquiry.CraneSchDAO.getYmDmCommonInfoNEW_PIDEV*/
								dmList  = dao.getYmDmCommonInfoNEW(sStockId);
	    			    	}
							iDmSize = dmList.size();
							
							for(int inx = 0; inx < dmList.size() ; inx++){
						 	 	dmRc = (JDTORecord)dmList.get(inx);
						 	 	sSmt = StringHelper.evl(dmRc.getFieldString("STOCK_MOVE_TERM"), "");
						 	 	
						 	 	if(YmCommonConst.NEW_STOCK_MOVE_TERM_VL.equals(sSmt)){ // 상차완료
						 	 		iDmFns++;
						 	 	}
						 	}
							
							logger.println(LogLevel.DEBUG,this, "▶▶▶상차대상 카운트 :"+iDmSize+",상차완료 카운트:"+iDmFns);
					
							//차량이적 차량출발 처리 /////////////////////////////////////////////////////////////
							String CarCardNo     = StringHelper.evl(dmRc.getFieldString("CAR_CARD_NO"), "");
	
							if ((CarCardNo.equals(YmCommonConst.CAR_BAY_TRANS_CARD_NO_1)
					 	    			|| CarCardNo.equals(YmCommonConst.CAR_BAY_TRANS_CARD_NO_2)
					 	    			|| CarCardNo.equals(YmCommonConst.CAR_BAY_TRANS_CARD_NO_3)
					 	    			|| CarCardNo.equals(YmCommonConst.CAR_BAY_TRANS_CARD_NO_4)
					 	    			|| CarCardNo.equals(YmCommonConst.CAR_BAY_TRANS_CARD_NO_5))) {
	//				 	    			
	//							/*
	//							 *차량 출발 처리
	//							 */
								logger.println(LogLevel.DEBUG,this, "차량이적용 차량 출발 처리("+CarCardNo+")");
								String sCarPosition	= StringHelper.evl(dmRc.getFieldString("position"), "");
			
								EJBConnector ejbConn = new EJBConnector("default","JNDICTSStatusReg",this);
								Boolean isTemp = (Boolean)ejbConn.trx("carStartOrder",
																	new  Class[]{String.class,
																				 String.class,
																				 String.class},
																	new Object[]{" ",				//한자리공백
																				 CarCardNo,			//카드번호
																				 sCarPosition});	//차량정지위치
																		 
								return true;
							}
							/////////////////////////////////////////////////////////////////////////////////////
							
	 
					    	
					    	//제원정보 전송------------------------------------------------------------------------------------------
					 
				    		String sCarCardNo     = StringHelper.evl(dmRc.getFieldString("CAR_CARD_NO"), "");
				    		String sTransWordNo   = StringHelper.evl(dmRc.getFieldString("TRANS_WORD_NO"), "");
				    		String sCarNo   	  = StringHelper.evl(dmRc.getFieldString("CAR_NO"), "");
					    	
	 
		                    //코일일품출하상차실적 #################################################################################
		         			JDTORecord tcRecordDM = null;
		         			tcRecordDM = JDTORecordFactory.getInstance().create(); 
		                    tcRecordDM.setField("CARD_NO", StringHelper.evl(dmRc.getFieldString("CAR_CARD_NO"), ""));
		                    tcRecordDM.setField("CAR_NO", StringHelper.evl(sCarNo, ""));
		                    tcRecordDM.setField("YD_GP", jtR.getFieldString("Yard_Id"));		
		                    tcRecordDM.setField("GOODS_NO"      , jtR.getFieldString("Coil_No").trim());
		                    tcRecordDM.setField("TRANS_WORD_DATE", sTransWordNo.length() > 8 ? sTransWordNo.substring(0,8) : sTransWordNo);
		                    tcRecordDM.setField("TRANS_WORD_SEQNO", sTransWordNo.length() > 8 ? sTransWordNo.substring(8) : "");
		         			
		                    if(iDmSize != 0 && iDmSize == iDmFns){ // 상차완료인 저장품이 ALL일때 처리
		                    	tcRecordDM.setField("GOODS_EA","*");
		                    }else{
		                    	tcRecordDM.setField("GOODS_EA","1");
		                    }
	//	         			//인터페이스 전문 호출
	//	         			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
	//	         			isSuccess = (Boolean)ejbConn.trx("getYDDMR011",new Class[]{JDTORecord.class},
	//	         			  	  	 new Object[]{tcRecordDM}); 
	//	                    logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 코일일품출하상차실적.===");
		                   //##########################################################################################################
		                    
		                    
		                  //검수 테이블 생성 //////////////////////////////////////////////////////////////
		                    tcRecordDM.setField("STOCK_ID",jtR.getFieldString("Coil_No").trim());
		                    
		        			// ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.CreatCarExaminationjlNEW_PIDEV
		        			int intRtnVal2 = ydStockDao.updYdStock10(tcRecordDM, 0);			
	
		        			if(intRtnVal2 >0){
		        				String szMsg = "수신한 재료번호 ["+jtR.getFieldString("Coil_No").trim()+"]에 대한 검수 DATA등록이 되었습니다.";
		        		 
		        				logger.println(LogLevel.DEBUG,this, szMsg);
	
		        			}else if(intRtnVal2 == 0){
		        				String szMsg = "수신한  재료번호 ["+jtR.getFieldString("Coil_No").trim()+"]에 대한 검수 DATA등록 되었거나  실패 하였습니다.";
		        			 
		        				logger.println(LogLevel.DEBUG,this, szMsg);
		         			}
		        			
		                    
			               //차량스케줄 제료정보 등록 ####################################################################################
		                    
		                    recInTemp = JDTORecordFactory.getInstance().create();
		                    recInTemp.setField("YD_CAR_SCH_ID",     szYD_CAR_SCH_ID);
		                    recInTemp.setField("STL_NO",            sStockId); 
		                    recInTemp.setField("DEL_YN",       		"N");
		                    recInTemp.setField("YD_STK_BED_NO",      sYD_STK_BED_NO) ;
		                    recInTemp.setField("YD_STK_LYR_NO",     "001") ;
		                    recInTemp.setField("REGISTER",			"callInner");
		                    //int intRtnVal = ydCarftmvmtlDao.insYdCarftmvmtl(recInTemp);
		                     
		                    EJBConnector ejbConn2 = new EJBConnector("default","JNDICPutResReg",this);
		         			isSuccess = (Boolean)ejbConn2.trx("getinsYdCarftmvmtl",new Class[]{JDTORecord.class},
		         			  	  	 new Object[]{recInTemp}); 
		         			
		                   //###########################################################################################################
		                    //----------------------------------------------------------------------------------------------------------
		                    
		                    
		                    
							
		                    //상차완료 처리----------------------------------------------------------------------------------------------
							if(iDmSize != 0 && iDmSize == iDmFns){ // 상차완료인 저장품이 ALL일때 처리
								
			
								//육송출하고도화 	
		    			    	logger.println(LogLevel.DEBUG,this, "◑◑◑◑◑  CHK:"+CHK);
		    			    	
		    			    	if(!CHK.equals("Y") ){
				                    //코일출하상차완료###############################################################################
				         			tcRecordDM = null;
				         			tcRecordDM = JDTORecordFactory.getInstance().create(); 
				                    tcRecordDM.setField("CARD_NO",sCarCardNo);
				                    tcRecordDM.setField("CAR_NO", sCarNo);
				                    tcRecordDM.setField("YD_GP", sYardGp);		
				                    tcRecordDM.setField("TRANS_WORD_DATE", sTransWordNo.length() > 8 ? sTransWordNo.substring(0,8) : sTransWordNo);
				                    tcRecordDM.setField("TRANS_WORD_SEQNO", sTransWordNo.length() > 8 ? sTransWordNo.substring(8) : "");
				         			
				         			//인터페이스 전문 호출
				         			EJBConnector ejbConn1 = new EJBConnector("default","JNDIYardWrkResReg",this);
				         			isSuccess = (Boolean)ejbConn1.trx("getYDDMR015",new Class[]{JDTORecord.class},
				         			  	  	 new Object[]{tcRecordDM}); 
				                    logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 코일출하상차완료.===");
				                   //################################################################################################	
		    			    	}else{
			    		    		//복수상차 처리 로직
			    		    		recInTemp = JDTORecordFactory.getInstance().create();
									recInTemp.setField("STL_NO", 				sStockId); 
									recInTemp.setField("YD_GP", 				sYardGp);
									
									EJBConnector ejbConn1 = new EJBConnector("default","RtModRegSeEJB",this);
									String isTemp = (String)ejbConn1.trx("procCmbnCarldYn",
																new  Class[]{JDTORecord.class },
																new Object[]{recInTemp});
									
									if(isTemp.equals(YdConstant.RETN_CD_EXIST)){
			    		    		
					                    //코일출하상차완료###############################################################################
					         			tcRecordDM = null;
					         			tcRecordDM = JDTORecordFactory.getInstance().create(); 
					                    tcRecordDM.setField("CARD_NO",sCarCardNo);
					                    tcRecordDM.setField("CAR_NO", sCarNo);
					                    tcRecordDM.setField("YD_GP", sYardGp);		
					                    tcRecordDM.setField("TRANS_WORD_DATE", sTransWordNo.length() > 8 ? sTransWordNo.substring(0,8) : sTransWordNo);
					                    tcRecordDM.setField("TRANS_WORD_SEQNO", sTransWordNo.length() > 8 ? sTransWordNo.substring(8) : "");
					         			
					         			//인터페이스 전문 호출
					         			EJBConnector ejbConn3 = new EJBConnector("default","JNDIYardWrkResReg",this);
					         			isSuccess = (Boolean)ejbConn3.trx("getYDDMR015",new Class[]{JDTORecord.class},
					         			  	  	 new Object[]{tcRecordDM}); 
					                    logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 코일출하상차완료.===");
					                   //################################################################################################
					                    
					                    
					                    
					                  //진행관리 냉연코일이송진행 상태실적   *********************************************
										JDTORecord recPara         = null;
										JDTORecord recGetVal       = null;
										JDTORecordSet rsCoilResult = null;
										 
										recPara = JDTORecordFactory.getInstance().create();			
										rsCoilResult 	= JDTORecordFactory.getInstance().createRecordSet("");
										
										recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
										/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarYdStockYdCarFtmvMtlMES*/
										int intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsCoilResult, 434);		
										if(intRtnVal > 0) {						 	
										
											String szMsg = "상차완료 처리 대상 건수  [Ret : " + rsCoilResult.size() + "]";
											logger.println(LogLevel.DEBUG,this, szMsg);
											
											for(int nIdx=0; nIdx< intRtnVal; nIdx++) {
												recGetVal = rsCoilResult.getRecord(nIdx);
											 
										        //진행관리 냉연코일이송진행 상태실적 
								    			recInTemp = JDTORecordFactory.getInstance().create();
												recInTemp.setField("MSG_ID",        "YDPTJ006"); 
												recInTemp.setField("STL_NO", StringHelper.evl(recGetVal.getFieldString("STL_NO"), "") );
												ydDelegate.sendMsg(recInTemp);
										        
											}
										}
										//진행관리 냉연코일이송진행 상태실적   *********************************************
									}
		    			    	}
		    			    	
	
			                    
			                    
			                  //차량스케줄 업데이트 - 상차완료######################################################################
	            				recInTemp = JDTORecordFactory.getInstance().create();
	            				recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);								//차량스케줄ID
	            				recInTemp.setField("YD_CAR_PROG_STAT", "5");										//차량진행상태
	        	    			recInTemp.setField("YD_EQP_WRK_STAT", "L");											//작업상태
	        	    			recInTemp.setField("YD_CARLD_CMPL_DT", YdUtils.getCurDate("yyyyMMddHHmmss"));		//상차완료일시
	        	    			recInTemp.setField("MODIFIER",	"callInner");										//수정자
	  
	        	    			int intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 0);
	        	    			
	        	    			if(intRtnVal <= 0) {
	        	    				String szMsg="[callInnerWorkInfo_Coil] 차량스케줄 상차완료 시 오류발생[반환값 : " + intRtnVal + "]";
	        	    				logger.println(LogLevel.DEBUG,this, szMsg);
	        	    			}      
	        	    			logger.println(LogLevel.DEBUG,this, "[callInnerWorkInfo_Coil] 차량스케줄 상차완료.===");
	        	    			//#################################################################################################
		
							}
							//--------------------------------------------------------------------------------------------------------
						}
					}
				}
				//*****************************************************************************************************************
			}	
			
			/*
			 *	3.	출하
			 *		Coil 제품창고 이송 상/하차 작업 시 첫 Coil 상/하차 권하 시
			 *		Coil 제품창고 이송 상/하차 작업 시 마지막 Coil 상/하차 권하 시
			 */		
			{
				String sStockId	= StringHelper.evl(jtR.getFieldString("Coil_No"), "").trim();
				String sSchCode = StringHelper.evl(jtR.getFieldString("Sch_Code"), "");
				

				if(YmCommonConst.NEW_SCH_WORK_KIND_CVML.equals(sSchCode)|| // COIL 소재이송상차
				   YmCommonConst.NEW_SCH_WORK_KIND_CVM2.equals(sSchCode)|| // COIL 소재이송상차
	      		   YmCommonConst.NEW_SCH_WORK_KIND_CVM3.equals(sSchCode)|| // COIL 소재이송상차
				   YmCommonConst.NEW_SCH_WORK_KIND_CVMU.equals(sSchCode)|| // COIL 소재이송하차
				   YmCommonConst.NEW_SCH_WORK_KIND_CVM4.equals(sSchCode)|| // COIL 소재이송하차	 
		  		   YmCommonConst.NEW_SCH_WORK_KIND_CVM5.equals(sSchCode)|| // COIL 소재이송하차	
				   YmCommonConst.NEW_SCH_WORK_KIND_GVML.equals(sSchCode)|| // COIL 제품이송상차	
				   YmCommonConst.NEW_SCH_WORK_KIND_GVM2.equals(sSchCode)|| // COIL 제품이송상차
	   	  		   YmCommonConst.NEW_SCH_WORK_KIND_GVM3.equals(sSchCode)|| // COIL 제품이송상차
				   YmCommonConst.NEW_SCH_WORK_KIND_GVMU.equals(sSchCode)|| // COIL 제품이송하차	
				   YmCommonConst.NEW_SCH_WORK_KIND_GVM4.equals(sSchCode)|| // COIL 제품이송하차	 
		  		   YmCommonConst.NEW_SCH_WORK_KIND_GVM5.equals(sSchCode)){ // COIL 제품이송하차	
					
					logger.println(LogLevel.DEBUG,this, "Coil 제품창고 이송 상/하차 작업: CVML, GVML");
					
					List 	   dmList  = null; // Card No,운송지시일자,순번이 같은 저장품정보
					List 	   pmList  = null; // Card No,운송지시일자,순번이 같은 저장품정보
					JDTORecord dmRc    = null; // 저장품정보
					String 	   sSmt    = "";   // 저장품이동조건
					String 	   sUpDown = "";   // 이송상차/하차구분
					int		   iDmSize = 0;    // Card No,운송지시일자,순번이 같은 저장품 갯수
					int		   iDmFns  = 0;    // 상차완료인 저장품 갯수	
					String 	   sDmItem = "";					 
					
					//육송출하고도화
					ymCommonDAO dao2 = ymCommonDAO.getInstance();
					 List chkList = null;
					String QueryId 	= "com.inisteel.cim.yd.dao.chklist";
					chkList = dao2.getCommonList(QueryId, new Object[]{});

				    JDTORecord unloadPointrec = (JDTORecord)chkList.get(0);
			    	String CHK   = StringHelper.evl(unloadPointrec.getFieldString("CHK"), "");	    	
			    	logger.println(LogLevel.DEBUG,this, "◑◑◑◑◑  CHK:"+CHK);
					
					if(!"".equals(sStockId)){
						logger.println(LogLevel.DEBUG,this, "Coil 소재이송상차,소재이송하차,제품이송상차,제품이송하차 처리: "+sStockId);
						
						 
						if(!CHK.equals("Y") ){ //기존방식
    			    		/*--ym.facilitystatus.facilityinquiry.CraneSchDAO.getYmDmCommonInfo_PIDEV*/
							dmList  = dao.getYmDmCommonInfo(sStockId);
    			    	}else{
    			    		/*--ym.facilitystatus.facilityinquiry.CraneSchDAO.getYmDmCommonInfoNEW_PIDEV*/
							dmList  = dao.getYmDmCommonInfoNEW(sStockId);
    			    	}
						
						iDmSize = dmList.size();
						
						if(YmCommonConst.NEW_SCH_WORK_KIND_CVML.equals(sSchCode)|| // COIL 소재이송상차
						   YmCommonConst.NEW_SCH_WORK_KIND_CVM2.equals(sSchCode)|| // COIL 소재이송상차
			      		   YmCommonConst.NEW_SCH_WORK_KIND_CVM3.equals(sSchCode)){ // COIL 소재이송상차
						   	sUpDown = "U";
							sDmItem	= "1";
						}else if(YmCommonConst.NEW_SCH_WORK_KIND_CVMU.equals(sSchCode)||// COIL 소재이송하차	 
						         YmCommonConst.NEW_SCH_WORK_KIND_CVM4.equals(sSchCode)||// COIL 소재이송하차	 
		  		   				 YmCommonConst.NEW_SCH_WORK_KIND_CVM5.equals(sSchCode)){// COIL 소재이송하차
							sUpDown = "D";
							sDmItem	= "1";
						}else if(YmCommonConst.NEW_SCH_WORK_KIND_GVML.equals(sSchCode)||// COIL 제품이송상차
								 YmCommonConst.NEW_SCH_WORK_KIND_GVM2.equals(sSchCode)||// COIL 제품이송상차
				   	  		     YmCommonConst.NEW_SCH_WORK_KIND_GVM3.equals(sSchCode)){// COIL 제품이송상차	
							sUpDown = "U";
							sDmItem	= "2";
						}else if(YmCommonConst.NEW_SCH_WORK_KIND_GVMU.equals(sSchCode)||// COIL 제품이송하차
								 YmCommonConst.NEW_SCH_WORK_KIND_GVM4.equals(sSchCode)||// COIL 제품이송하차	
				  		   		 YmCommonConst.NEW_SCH_WORK_KIND_GVM5.equals(sSchCode)){// COIL 제품이송하차			
							sUpDown = "D";
							sDmItem	= "2";
						}
						
						/*
						 * 2008.01.14 이정훈
						 * 임가공사 이송 완료 구분 추가
						 */
						//*****************************************************************************************
						pmList  = dao.getYmPoFrtoInfo(sStockId);
						if (pmList.size() > 0) {
						
						for(int inx = 0; inx < dmList.size() ; inx++){
							
					 	 	dmRc   = (JDTORecord)dmList.get(inx);
					 	 	sSmt   = StringHelper.evl(dmRc.getFieldString("STOCK_MOVE_TERM"), "");
					 	 	
					 	 	if(YmCommonConst.NEW_SCH_WORK_KIND_CVML.equals(sSchCode)||// COIL 소재이송상차
					 	 	   YmCommonConst.NEW_SCH_WORK_KIND_CVM2.equals(sSchCode)||// COIL 소재이송상차
			      		   	   YmCommonConst.NEW_SCH_WORK_KIND_CVM3.equals(sSchCode)||// COIL 소재이송상차
					 	 	   YmCommonConst.NEW_SCH_WORK_KIND_GVML.equals(sSchCode)||// COIL 제품이송상차		
					 	 	   YmCommonConst.NEW_SCH_WORK_KIND_GVM2.equals(sSchCode)||// COIL 제품이송상차
				   	  		   YmCommonConst.NEW_SCH_WORK_KIND_GVM3.equals(sSchCode)){// COIL 제품이송상차		
								
								if(YmCommonConst.NEW_STOCK_MOVE_TERM_VL.equals(sSmt)){ 		// 상차완료
									logger.println(LogLevel.DEBUG,this, "소재/제품 이송 상차 완료");
						 	 		iDmFns++;
						 	 	}	
							}else if(YmCommonConst.NEW_SCH_WORK_KIND_CVMU.equals(sSchCode)||// COIL 소재이송하차
							         YmCommonConst.NEW_SCH_WORK_KIND_CVM4.equals(sSchCode)||// COIL 소재이송하차	 
		  		   					 YmCommonConst.NEW_SCH_WORK_KIND_CVM5.equals(sSchCode)||// COIL 소재이송하차	 
								     YmCommonConst.NEW_SCH_WORK_KIND_GVMU.equals(sSchCode)||// COIL 제품이송하차
								     YmCommonConst.NEW_SCH_WORK_KIND_GVM4.equals(sSchCode)||// COIL 제품이송하차	 	
				  		   		     YmCommonConst.NEW_SCH_WORK_KIND_GVM5.equals(sSchCode)){// COIL 제품이송하차				
								
								if(YmCommonConst.NEW_STOCK_MOVE_TERM_E1.equals(sSmt)|| 		// 이송완료
								   YmCommonConst.NEW_STOCK_MOVE_TERM_BC.equals(sSmt)|| 		// 정정작업지시대기	 
								   YmCommonConst.NEW_STOCK_MOVE_TERM_CC.equals(sSmt)){ 		// 정정작업대기	
						 	 		iDmFns++;
						 	 	}
							}
						}
					    
//				    	if(iDmFns == 1){ //상,하차완료인 저장품이 1개일 때 처리
//				    		logger.println(LogLevel.DEBUG,this, "상,하차완료인 저장품이 1개일 때 처리");
//							String sCarCardNo     = StringHelper.evl(dmRc.getFieldString("CAR_CARD_NO"), "");
//				    		String sTransWordNo   = StringHelper.evl(dmRc.getFieldString("TRANS_WORD_NO"), "");
//				    	
//						    //AB열연 ##############################################################################################	
//			
//							JDTORecord carR = dao.getDmCarInfo(sStockId);
//							String sCarNo = "";
//					    	if(carR != null){
//								sCarNo = StringHelper.evl(carR.getFieldString("CAR_NO"), "");
//					    	}
//				
//						    //일관제철 ##############################################################################################	
//		                    
//							//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//		                    //임가공이송상하차개시
//		         			JDTORecord tcRecordDM = null;
//		         			tcRecordDM = JDTORecordFactory.getInstance().create(); 
//		         			tcRecordDM.setField("UPCARUNLOAD_GP", sUpDown); 
//		                    tcRecordDM.setField("CARD_NO",sCarCardNo);
//		                    tcRecordDM.setField("CAR_NO", sCarNo);
//		                    tcRecordDM.setField("YD_GP", jtR.getFieldString("Yard_Id"));		
//		                    tcRecordDM.setField("TRANS_WORD_DATE",sTransWordNo.length() > 8 ? sTransWordNo.substring(0,8) : sTransWordNo);
//		                    tcRecordDM.setField("TRANS_WORD_SEQNO", sTransWordNo.length() > 8 ? sTransWordNo.substring(8) : "");
//		         			
//		         			//인터페이스 전문 호출
//		         			EJBConnector ejbConn1 = new EJBConnector("default","JNDIYardWrkResReg",this);
//		         			isSuccess = (Boolean)ejbConn1.trx("getYDDMR020",new Class[]{JDTORecord.class},
//		         			  	  	 new Object[]{tcRecordDM}); 
//		                    logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 임가공이송상하차개시.===");
//		                    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//		                    
//						}
						
						if(iDmSize != 0 && iDmSize == iDmFns){ // 상,하차완료인 저장품이 ALL일때 처리
							logger.println(LogLevel.DEBUG,this, "상,하차완료인 저장품이 ALL일때 처리");
				    		String sCarCardNo     = StringHelper.evl(dmRc.getFieldString("CAR_CARD_NO"), "");
				    		String sTransWordNo   = StringHelper.evl(dmRc.getFieldString("TRANS_WORD_NO"), "");
				    		String sCarNo 		  =  StringHelper.evl(dmRc.getFieldString("CAR_NO"), "");
						    //AB열연 ##############################################################################################	
			
//							JDTORecord carR = dao.getDmCarInfo(sStockId);
//							String sCarNo = "";
//					    	if(carR != null){
//								sCarNo = StringHelper.evl(carR.getFieldString("CAR_NO"), "");
//					    	}
				
							
						    //일관제철 ##############################################################################################	
					    	//육송출하고도화				
	    			    	
	    			    	if(!CHK.equals("Y") ){
								//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			                    //임가공이송상하차완료
			         			JDTORecord tcRecordDM = null;
			         			tcRecordDM = JDTORecordFactory.getInstance().create(); 
			         			tcRecordDM.setField("UPCARUNLOAD_GP", sUpDown); 
			                    tcRecordDM.setField("CARD_NO",sCarCardNo);
			                    tcRecordDM.setField("CAR_NO", sCarNo);
			                    tcRecordDM.setField("YD_GP", jtR.getFieldString("Yard_Id"));		
			                    tcRecordDM.setField("TRANS_WORD_DATE",sTransWordNo.length() > 8 ? sTransWordNo.substring(0,8) : sTransWordNo);
			                    tcRecordDM.setField("TRANS_WORD_SEQNO",  sTransWordNo.length() > 8 ? sTransWordNo.substring(8) : "");
			         			
			         			//인터페이스 전문 호출
			         			EJBConnector ejbConn1 = new EJBConnector("default","JNDIYardWrkResReg",this);
			         			isSuccess = (Boolean)ejbConn1.trx("getYDDMR022",new Class[]{JDTORecord.class},
			         			  	  	 new Object[]{tcRecordDM}); 
			                    logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 임가공이송상하차완료.===");
			                    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	                            
	                            
			                    //임가공 도착 시 이송테이블 상태 처리 20091006 JKJEUNG
								//TB_PT_STLFRTOMOVE update 		
			                    YdStockDAO ydStockDAO = new YdStockDAO();
								String stkQueryId = "ym.facilitywork.putwrecord.session.updateunLoadTimeToPT2";
								int iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ sTransWordNo } );
							
							 
								/*
							 	 *****************************************************************************
							 	 * 제품/소재 이송 상/하차 완료시 자동출발 모듈 CALL
							 	 */
							 	{ 
							 		
					 	 			String sCarPosition	= "";
					 	 			
					 	 			if("U".equals(sUpDown)){
					 	 				sCarPosition	= StringHelper.evl(jtR.getFieldString("Put_Position"), "");
					 	 			}else if("D".equals(sUpDown)){
					 	 				JDTORecord tmRc = dao.getYMDM008Info_03(sStockId);
									    if(tmRc != null){
								    		sCarPosition  = StringHelper.evl(tmRc.getFieldString("CRANE_WRSLT_UP_LOC"), "");
								    	}	
								    }
					 	 			try{
						 	 			ejbConn1 = new EJBConnector("default","JNDICTSStatusReg",this);
					    				Boolean isTemp = (Boolean)ejbConn1.trx("carStartOrder",
																	new  Class[]{String.class,
																				 String.class,
																				 String.class},
																	new Object[]{" ",							//한자리공백
																				 sCarCardNo,					//카드번호
																				 sCarPosition.substring(0, 6)});//차량정지위치
									}catch(Exception e){
										logger.println(LogLevel.DEBUG,this," L2 출하실적 전문 송신 모듈 EXCEPTION");
									}					
									logger.println(LogLevel.DEBUG,this, "내부IF호출===Coil 제품창고 이송 상차 작업 시 마지막 Coil 상/하차 권하 시 자동출발 CALL.===");
								}
	    			    	}else{
	    			    		
	    			    		//복수상차 처리 로직
		    		    		recInTemp = JDTORecordFactory.getInstance().create();
								recInTemp.setField("STL_NO", 				sStockId); 
								recInTemp.setField("YD_GP", 				jtR.getFieldString("Yard_Id"));
								
								EJBConnector ejbConn1 = new EJBConnector("default","RtModRegSeEJB",this);
								String isTemp = (String)ejbConn1.trx("procCmbnCarldYn",
															new  Class[]{JDTORecord.class },
															new Object[]{recInTemp});
								
								if(isTemp.equals(YdConstant.RETN_CD_EXIST)){
									//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				                    //임가공이송상하차완료
				         			JDTORecord tcRecordDM = null;
				         			tcRecordDM = JDTORecordFactory.getInstance().create(); 
				         			tcRecordDM.setField("UPCARUNLOAD_GP", sUpDown); 
				                    tcRecordDM.setField("CARD_NO",sCarCardNo);
				                    tcRecordDM.setField("CAR_NO", sCarNo);
				                    tcRecordDM.setField("YD_GP", jtR.getFieldString("Yard_Id"));		
				                    tcRecordDM.setField("TRANS_WORD_DATE",sTransWordNo.length() > 8 ? sTransWordNo.substring(0,8) : sTransWordNo);
				                    tcRecordDM.setField("TRANS_WORD_SEQNO",  sTransWordNo.length() > 8 ? sTransWordNo.substring(8) : "");
				         			
				         			//인터페이스 전문 호출
				         			EJBConnector ejbConn2 = new EJBConnector("default","JNDIYardWrkResReg",this);
				         			isSuccess = (Boolean)ejbConn2.trx("getYDDMR022",new Class[]{JDTORecord.class},
				         			  	  	 new Object[]{tcRecordDM}); 
				                    logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 임가공이송상하차완료.===");
				                    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		                            
		                            
				                    //임가공 도착 시 이송테이블 상태 처리 20091006 JKJEUNG
									//TB_PT_STLFRTOMOVE update 		
				                    YdStockDAO ydStockDAO = new YdStockDAO();
									String stkQueryId = "ym.facilitywork.putwrecord.session.updateunLoadTimeToPT2";
									int iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ sTransWordNo } );
		 
				        			
									/*
								 	 *****************************************************************************
								 	 * 제품/소재 이송 상/하차 완료시 자동출발 모듈 CALL
								 	 */
								 	{ 
								 		
						 	 			String sCarPosition	= "";
						 	 			
						 	 			if("U".equals(sUpDown)){
						 	 				sCarPosition	= StringHelper.evl(jtR.getFieldString("Put_Position"), "");
						 	 			}else if("D".equals(sUpDown)){
						 	 				JDTORecord tmRc = dao.getYMDM008Info_03(sStockId);
										    if(tmRc != null){
									    		sCarPosition  = StringHelper.evl(tmRc.getFieldString("CRANE_WRSLT_UP_LOC"), "");
									    	}	
									    }
						 	 			try{
						 	 				//차량정보를 가져온다.
						 	 				String sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getcarStartOrderInfo";		
										 	ymCommonDAO dao = ymCommonDAO.getInstance();
											List 	list = dao.getCommonList(sQueryId,new Object[]{jtR.getFieldString("Coil_No").trim()});		
											
											
							 	 			ejbConn1 = new EJBConnector("default","JNDICTSStatusReg",this);
						    				Boolean isTemp2 = (Boolean)ejbConn1.trx("carStartOrder",
																		new  Class[]{String.class,
																					 String.class,
																					 String.class},
																		new Object[]{" ",							//한자리공백
																					 sCarCardNo,					//카드번호
																					 sCarPosition.substring(0, 6)});//차량정지위치
						    				
						    				
						    					
											if(list.size()> 0){ 
												
												logger.println(LogLevel.DEBUG,this," 에 대한 포인트 정보가 존재함");
										 
													JDTORecord jtR2 = (JDTORecord)list.get(0); 
										 			String sYD_CAR_SCH_ID 	= YmCommonUtil.paraRecChkNull(jtR2,"YD_CAR_SCH_ID");
										 			String sYD_CARPNT_CD 	= YmCommonUtil.paraRecChkNull(jtR2,"YD_CARPNT_CD");
										 			
										 	
											 	//입동지시요구모듈 호출(trailer인 경우)///////////////////////////////////////////////////////
												if(!sYD_CARPNT_CD.equals("") ){
													/*
													 * 6. 차량정지위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
													 */
												 
													logger.println(LogLevel.DEBUG,this, "내부IF호출=== AB차량입동지시요구 모듈을 호출 시작===");
													
													recInTemp = JDTORecordFactory.getInstance().create(); 
													recInTemp.setField("JMS_TC_CD",  "YDYDJ662");
													recInTemp.setField("YD_CARPNT_CD",    sYD_CARPNT_CD);	//입동포인트
													recInTemp.setField("YD_CAR_SCH_ID",    sYD_CAR_SCH_ID);	//차량스케줄ID
											 
													ydDelegate.sendMsg(recInTemp);
													
			//										EJBConnector ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);
			//										ejbConn.trx("procCarBayInOrdReqNEW", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
													logger.println(LogLevel.DEBUG,this, "내부IF호출=== - AB차량입동지시요구 모듈을 호출 성공===");
			 
												}
												//////////////////////////////////////////////////////////////////////////////////////////
												}
											
										}catch(Exception e){
											logger.println(LogLevel.DEBUG,this," L2 출하실적 전문 송신 모듈 EXCEPTION");
										}					
										logger.println(LogLevel.DEBUG,this, "내부IF호출===Coil 제품창고 이송 상차 작업 시 마지막 Coil 상/하차 권하 시 자동출발 CALL.===");
									}
								 	 
								}
	    			    		
	    			    	}
						 	/*
						 	 *****************************************************************************
						 	 */
						 	
						 	
						
						 	
						 	/**
							 * 제품이송하차시
							 * 권하시 저장품이동조건 = 이송완료로 셋팅
							 * 모든코일 작업완료후 다시 출하작업지시대기로 바꿈
							 */
							if(YmCommonConst.NEW_SCH_WORK_KIND_GVMU.equals(sSchCode)||// COIL 제품이송하차	
							   YmCommonConst.NEW_SCH_WORK_KIND_GVM4.equals(sSchCode)||// COIL 제품이송하차	 	
				  		   	   YmCommonConst.NEW_SCH_WORK_KIND_GVM5.equals(sSchCode)){// COIL 제품이송하차			 
		   						JDTORecord tRc    = null; // 저장품정보
								String 	   tId    =   ""; // 저장품ID
								int 	   tSeq   =    0; 
								for(int iny = 0; iny < dmList.size() ; iny++){
									tRc   = (JDTORecord)dmList.get(iny);
							 	 	tId   = StringHelper.evl(tRc.getFieldString("STOCK_ID"), "");
							 	 	tSeq  = dao.updateStockTransInfo(tId,
							 	 			YmCommonConst.NEW_STOCK_MOVE_TERM_KG); //Coil 출하작업지시대기 
						 	 	}	
						 	}
						}
						
							 //검수 테이블 생성 //////////////////////////////////////////////////////////////
							JDTORecord tcRecordDM = null;
		         			tcRecordDM = JDTORecordFactory.getInstance().create(); 
		                    tcRecordDM.setField("STOCK_ID",jtR.getFieldString("Coil_No").trim());
		                    
		        			// ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.CreatCarExaminationjlNEW_PIDEV
		        			int intRtnVal2 = ydStockDao.updYdStock10(tcRecordDM, 0);
						
						
						}//if (pmList.size() > 0) END
						//*****************************************************************************************
						// 임가공사 소재에 대한 처리외 차량이적처리를 위해 분기 추가.
						else{
							
							logger.println(LogLevel.DEBUG,this," 임가공관련 외 ");
							logger.println(LogLevel.DEBUG,this," 차량이적과 관련된 코드.");
							String CarCardNo     = "";
							
								logger.println(LogLevel.DEBUG,this," 처리List:"+dmList);
								for(int inx = 0; inx < dmList.size() ; inx++){
									
									dmRc   = (JDTORecord)dmList.get(inx);
									CarCardNo     = StringHelper.evl(dmRc.getFieldString("CAR_CARD_NO"), "");
									sSmt   = StringHelper.evl(dmRc.getFieldString("STOCK_MOVE_TERM"), "");
									
									if(YmCommonConst.NEW_SCH_WORK_KIND_CVML.equals(sSchCode)||// COIL 소재이송상차
									   YmCommonConst.NEW_SCH_WORK_KIND_CVM2.equals(sSchCode)||// COIL 소재이송상차
									   YmCommonConst.NEW_SCH_WORK_KIND_CVM3.equals(sSchCode)||// COIL 소재이송상차
									   YmCommonConst.NEW_SCH_WORK_KIND_GVML.equals(sSchCode)||// COIL 제품이송상차		
									   YmCommonConst.NEW_SCH_WORK_KIND_GVM2.equals(sSchCode)||// COIL 제품이송상차
									   YmCommonConst.NEW_SCH_WORK_KIND_GVM3.equals(sSchCode)){// COIL 제품이송상차		
										
										if(YmCommonConst.NEW_STOCK_MOVE_TERM_VL.equals(sSmt)){ 		// 상차완료
											logger.println(LogLevel.DEBUG,this, "소재/제품 이송 상차 완료");
											iDmFns++;
										}	
									}else if(YmCommonConst.NEW_SCH_WORK_KIND_CVMU.equals(sSchCode)||// COIL 소재이송하차
											 YmCommonConst.NEW_SCH_WORK_KIND_CVM4.equals(sSchCode)||// COIL 소재이송하차	 
											 YmCommonConst.NEW_SCH_WORK_KIND_CVM5.equals(sSchCode)||// COIL 소재이송하차	 
											 YmCommonConst.NEW_SCH_WORK_KIND_GVMU.equals(sSchCode)||// COIL 제품이송하차
											 YmCommonConst.NEW_SCH_WORK_KIND_GVM4.equals(sSchCode)||// COIL 제품이송하차	 	
											 YmCommonConst.NEW_SCH_WORK_KIND_GVM5.equals(sSchCode)){// COIL 제품이송하차				
										
										if(YmCommonConst.NEW_STOCK_MOVE_TERM_E1.equals(sSmt)|| 		// 이송완료
										   YmCommonConst.NEW_STOCK_MOVE_TERM_BC.equals(sSmt)|| 		// 정정작업지시대기	 
										   YmCommonConst.NEW_STOCK_MOVE_TERM_CC.equals(sSmt)){ 		// 정정작업대기	
											iDmFns++;
										}
									}
								}	// END for

							if ((CarCardNo.equals(YmCommonConst.CAR_BAY_TRANS_CARD_NO_1)
										|| CarCardNo.equals(YmCommonConst.CAR_BAY_TRANS_CARD_NO_2)
										|| CarCardNo.equals(YmCommonConst.CAR_BAY_TRANS_CARD_NO_3)
										|| CarCardNo.equals(YmCommonConst.CAR_BAY_TRANS_CARD_NO_4)
										|| CarCardNo.equals(YmCommonConst.CAR_BAY_TRANS_CARD_NO_5))) {
								if(iDmFns == 1){ //상,하차완료인 저장품이 1개일 때 처리
									logger.println(LogLevel.DEBUG,this, "임가공사 외 차량이적-상,하차완료인 저장품이 1개일 때 처리");
									String sCarCardNo     = StringHelper.evl(dmRc.getFieldString("CAR_CARD_NO"), "");
									String sTransWordNo   = StringHelper.evl(dmRc.getFieldString("TRANS_WORD_NO"), "");

									JDTORecord carR = dao.getDmCarInfo(sStockId);
									String sCarNo = "";
									if(carR != null){
										sCarNo = StringHelper.evl(carR.getFieldString("CAR_NO"), "");
										// 최규성 추가. 2009-10-19
										selCardNo = StringHelper.evl(carR.getFieldString("card_no"), "");
									}
								}
								if(iDmSize != 0 && iDmSize == iDmFns){ // 상,하차완료인 저장품이 ALL일때 처리
									logger.println(LogLevel.DEBUG,this, "임가공사 외 차량이적-상,하차완료인 저장품이 ALL일때 처리");
									String sCarCardNo     = StringHelper.evl(dmRc.getFieldString("CAR_CARD_NO"), "");
									String sTransWordNo   = StringHelper.evl(dmRc.getFieldString("TRANS_WORD_NO"), "");

									JDTORecord carR = dao.getDmCarInfo(sStockId);
									String sCarNo = "";
									if(carR != null){
										sCarNo = StringHelper.evl(carR.getFieldString("CAR_NO"), "");
										// 최규성 추가. 2009-10-19
										selCardNo = StringHelper.evl(carR.getFieldString("card_no"), "");
									}

									/******************************************************************************
									 * 제품/소재 이송 상/하차 완료시 자동출발 모듈 CALL
									 */
									{ 
										
										String sCarPosition	= "";
										
										if("U".equals(sUpDown)){
											sCarPosition	= StringHelper.evl(jtR.getFieldString("Put_Position"), "");
										}else if("D".equals(sUpDown)){
											JDTORecord tmRc = dao.getYMDM008Info_03(sStockId);
											if(tmRc != null){
												sCarPosition  = StringHelper.evl(tmRc.getFieldString("CRANE_WRSLT_UP_LOC"), "");
											}	
										}
										try{
											EJBConnector ejbConn1 = new EJBConnector("default","JNDICTSStatusReg",this);
											Boolean isTemp = (Boolean)ejbConn1.trx("carStartOrder",
																		new  Class[]{String.class,
																					 String.class,
																					 String.class},
																		new Object[]{" ",							//한자리공백
																					 sCarCardNo,					//카드번호
																					 sCarPosition.substring(0, 6)});//차량정지위치
										}catch(Exception e){
											logger.println(LogLevel.DEBUG,this," L2 출하실적 전문 송신 모듈 EXCEPTION");
										}					
										logger.println(LogLevel.DEBUG,this, "내부IF호출===Coil 제품창고 이송 상차 작업 시 마지막 Coil 상/하차 권하 시 자동출발 CALL.===");
									}
									/*******************************************************************************/
								}
							}	// if (카드번호 조건) END
						}// if (pmList.size() > 0) END
					}
					
				}



			}
			
			/*
			 *	6.	조업
			 *		Coil 제품/소재 이송 상/하차 완료 시
			 */		
			{
				String sSchCode = StringHelper.evl(jtR.getFieldString("Sch_Code"), "");
				
				if(YmCommonConst.NEW_SCH_WORK_KIND_CVML.equals(sSchCode)|| // COIL 소재이송상차
				   YmCommonConst.NEW_SCH_WORK_KIND_CVM2.equals(sSchCode)|| // COIL 소재이송상차
			       YmCommonConst.NEW_SCH_WORK_KIND_CVM3.equals(sSchCode)|| // COIL 소재이송상차
				   YmCommonConst.NEW_SCH_WORK_KIND_CVMU.equals(sSchCode)|| // COIL 소재이송하차
				   YmCommonConst.NEW_SCH_WORK_KIND_CVM4.equals(sSchCode)|| // COIL 소재이송하차	 
		  		   YmCommonConst.NEW_SCH_WORK_KIND_CVM5.equals(sSchCode)|| // COIL 소재이송하차	 
				   YmCommonConst.NEW_SCH_WORK_KIND_GVML.equals(sSchCode)|| // COIL 제품이송상차	
				   YmCommonConst.NEW_SCH_WORK_KIND_GVM2.equals(sSchCode)|| // COIL 제품이송상차
				   YmCommonConst.NEW_SCH_WORK_KIND_GVM3.equals(sSchCode)|| // COIL 제품이송상차	
				   YmCommonConst.NEW_SCH_WORK_KIND_GVMU.equals(sSchCode)|| // COIL 제품이송하차	
				   YmCommonConst.NEW_SCH_WORK_KIND_GVM4.equals(sSchCode)|| // COIL 제품이송하차	 	
				   YmCommonConst.NEW_SCH_WORK_KIND_GVM5.equals(sSchCode)){ // COIL 제품이송하차		
				   	
					String sStockId		= StringHelper.evl(jtR.getFieldString("Coil_No"), "").trim();
					String sPut_Position= StringHelper.evl(jtR.getFieldString("Put_Position"), "");
					String sUpDown 		= "";
					if(YmCommonConst.NEW_SCH_WORK_KIND_CVML.equals(sSchCode)||// COIL 소재이송상차
					   YmCommonConst.NEW_SCH_WORK_KIND_CVM2.equals(sSchCode)||// COIL 소재이송상차
			      	   YmCommonConst.NEW_SCH_WORK_KIND_CVM3.equals(sSchCode)||// COIL 소재이송상차
					   YmCommonConst.NEW_SCH_WORK_KIND_GVML.equals(sSchCode)||// COIL 제품이송상차
					   YmCommonConst.NEW_SCH_WORK_KIND_GVM2.equals(sSchCode)||// COIL 제품이송상차
				   	   YmCommonConst.NEW_SCH_WORK_KIND_GVM3.equals(sSchCode)){// COIL 제품이송상차				
						sUpDown = "U";
					}else if(YmCommonConst.NEW_SCH_WORK_KIND_CVMU.equals(sSchCode)||// COIL 소재이송하차
					         YmCommonConst.NEW_SCH_WORK_KIND_CVM4.equals(sSchCode)||// COIL 소재이송하차	 
		  		   			 YmCommonConst.NEW_SCH_WORK_KIND_CVM5.equals(sSchCode)||// COIL 소재이송하차	 
							 YmCommonConst.NEW_SCH_WORK_KIND_GVMU.equals(sSchCode)||// COIL 제품이송하차
							 YmCommonConst.NEW_SCH_WORK_KIND_GVM4.equals(sSchCode)||// COIL 제품이송하차	 	
				   			 YmCommonConst.NEW_SCH_WORK_KIND_GVM5.equals(sSchCode)){// COIL 제품이송하차				
						sUpDown = "D";
					}
					
					YMPO159 model = new YMPO159();
					model.setTcCode(YmCommonConst.MODEL_YMPO159);
					model.setTcDate(YmCommonUtil.getCurDate("yyyy-MM-dd"));
					model.setTcTime(YmCommonUtil.getCurDate("HH-mm-ss"));
					
					/* 상하차처리일자  CHAR(8)	 yyyymmdd */
					model.setupDownDate(YmCommonUtil.getCurDate("yyyyMMdd"));
					
					 /* COIL번호	 CHAR(11)	*/
					model.setcoilNo(sStockId);
					
					/* 상하차구분	 CHAR(1)		U:상차, D:하차		*/
					model.setupDownGbn(sUpDown);
					
					/* 상하차위치 	 CHAR(10) 상차,하차 위치 */
					model.setupDownLoc(sPut_Position);
					
					//EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
					//isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{CommonModel.class},
					//											  	  	 new Object[]{model});
					//logger.println(LogLevel.DEBUG,this, "내부IF호출===Coil 이송 상/하차 완료 시.===");
				}
			}
			/*
			 *	7.	조업
			 *		보류장에 반납 Coil 권하 시
			 */		
			{
			   /*
			 	* String sStockId	= StringHelper.evl(jtR.getFieldString("Coil_No"), "").trim();
				* 
				* JDTORecord dmRc = null;
				* 
				* if(!"".equals(sStockId)){
				* 	dmRc = dao.getYMPO163Info(sStockId);
			    * }
		    	* if(dmRc != null){
				* 	
				* 	YMPO163 model = new YMPO163();
				* 	model.setTcCode(YmCommonConst.MODEL_YMPO163);
				* 	model.setTcDate(YmCommonUtil.getCurDate("yyyy-MM-dd"));
				* 	model.setTcTime(YmCommonUtil.getCurDate("HH-mm-ss"));
				* 	
				* 	// 권하 일자   CHAR(8)		yyyymmdd	
				* 	model.setdownDate(YmCommonUtil.getCurDate("yyyyMMdd"));
				* 	
				* 	// 권하 시각   CHAR(6)	 HHMMSS 
				* 	model.setdownTime(YmCommonUtil.getCurDate("HHmmss"));
				* 	
				* 	// COIL번호  CHAR(10)	
				* 	model.setcoilNo(sStockId);
				* 	
				* 	EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
				* 	isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{CommonModel.class},
				* 												  	  	 new Object[]{model});
				* 	logger.println(LogLevel.DEBUG,this, "내부IF호출===보류장에 반납 Coil 권하 시.===");
				* 	
				* }
				*/
			}
			
			/*
			 *	8.	조업
			 *		SPM, HFL 입측에 권하시 발생.
			 * 
			 */
			{
/*
				String sVal 			= "";
				String sPut_Position 	= StringHelper.evl(jtR.getFieldString("Put_Position"), "");
				String sSch_Code 		= StringHelper.evl(jtR.getFieldString("Sch_Code"), "");			// 최규성 추가. 2009-12-14 SPM2 보급 실적 처리
								
		    	String sPutUsageCd = YmCommonUtil.getStackColInfoWithPk(sPut_Position.substring(0, 6));
				
		   		if(      YmCommonConst.STACK_COL_USAGE_CD_KE.equals(sPutUsageCd)){ // SPM 보급/SPM보급위치
//		   			if (sSch_Code.equals(YmCommonConst.NEW_SCH_WORK_KIND_CNLI)){
//		   				sVal = "N1";
//		   			}else{
		   				sVal = "S1";
//		   			}
				}else if(YmCommonConst.STACK_COL_USAGE_CD_KI.equals(sPutUsageCd)){ // SPM Take In/SPMTAKEIN위치
//					if (sSch_Code.equals(YmCommonConst.NEW_SCH_WORK_KIND_CNTI)){
//		   				sVal = "N5";
//		   			}else{
		   				sVal = "S5";
//		   			}
			   		
			   	}else if(YmCommonConst.STACK_COL_USAGE_CD_FE.equals(sPutUsageCd)){ // HFL 보급/COIL HFL보급위치
			   		sVal = "H1";
			   	}else if(YmCommonConst.STACK_COL_USAGE_CD_FI.equals(sPutUsageCd)){ // HFL Take In/HFLTAKEIN위치
					sVal = "H5";
				}  
*/
		   		
				String sVal 			= "";
				String sPut_Position 	= StringHelper.evl(jtR.getFieldString("Put_Position"), "");
				String sSch_Code 		= StringHelper.evl(jtR.getFieldString("Sch_Code"), "");			// 최규성 추가. 2009-12-14 SPM2 보급 실적 처리
				String sStockId			= StringHelper.evl(jtR.getFieldString("Coil_No"), "").trim();
				String sYardGp			= StringHelper.evl(jtR.getFieldString("Yard_Id"), "");
				
		    	String sPutUsageCd = YmCommonUtil.getStackColInfoWithPk(sPut_Position.substring(0, 6));
				
		    	if(YmCommonConst.STACK_COL_USAGE_CD_KE.equals(sPutUsageCd)){ // SPM 보급/SPM보급위치
		   	    	//2010.01.28 정종균 : SPM2 분리
		   	    	if (sSch_Code.equals(YmCommonConst.NEW_SCH_WORK_KIND_CNLI)
		   	    		||	"DKE".equals(sPut_Position.substring(1,4))   //권하위치가 SPM2 보급 D,E인 경우 처리
		   	    		||	"EKE".equals(sPut_Position.substring(1,4))
		   	    	    ){
		   	    		//SPM2 보급/SPM2보급위치
		   	    		sVal = "N1";
		   		   }else if (sSch_Code.equals(YmCommonConst.NEW_SCH_WORK_KIND_CKLR)){
		   	    		//SPM 재작업 추출 
		   	    		sVal = "S5";
		   		   }else{ 
		   			   //SPM 보급/SPM보급위치
		   			   sVal = "S1";
		   		   }
				}else if(YmCommonConst.STACK_COL_USAGE_CD_KI.equals(sPutUsageCd)){ // SPM Take In/SPMTAKEIN위치
		   	    	//2010.01.28 정종균 : SPM2 분리
					if (sSch_Code.equals(YmCommonConst.NEW_SCH_WORK_KIND_CNTI)){
						//SPM2 Take In위치
			   			sVal = "N5";
					}else { 
						//SPM TakeIn위치
						sVal = "S5";
					}
			   	}else if(YmCommonConst.STACK_COL_USAGE_CD_FE.equals(sPutUsageCd)){ // HFL 보급/COIL HFL보급위치
			   		if (sSch_Code.equals(YmCommonConst.NEW_SCH_WORK_KIND_CHLI)){
			   			//SPM2 내 HFL 보급
			   			sVal = "F1";
			   		}else { 
			   			//SPM 보급/SPM보급위치
			   			sVal = "H1";
			   		}
			   		//sVal = "H1";
			   	}else if(YmCommonConst.STACK_COL_USAGE_CD_FI.equals(sPutUsageCd)){ // HFL Take In/HFLTAKEIN위치
					sVal = "H5";
				}  
	
				if(!"".equals(sVal))
				{
					//A열연 이고 N으로 만들어 진 경우 S로 변경
					if("N".equals(sVal.substring(0,1)) && YmCommonConst.YD_GP_1.equals(sYardGp)){
						sVal="S"+sVal.substring(1,2);
					}
					
				
					YMPO161 model = new YMPO161();
					model.setTcCode(YmCommonConst.MODEL_YMPO161);
					model.setTcDate(YmCommonUtil.getCurDate("yyyy-MM-dd"));
					model.setTcTime(YmCommonUtil.getCurDate("HH-mm-ss"));
					
					/* 권하일자	CHAR(8)  yyyymmdd	*/
					model.setdownDate(YmCommonUtil.getCurDate("yyyyMMdd"));
					
					/* 권하시각     CHAR(6)  HHMMSS */
					model.setdownTime(YmCommonUtil.getCurDate("HHmmss"));
					
					/* 공장구분	CHAR(1)  A:A열연, B:B열연*/
					model.setplantGbn(YmCommonConst.YD_GP_1.equals(sYardGp)?"A":"B");
					
					/* 공정구분	CHAR(1)  H : Hot Filnal, S :  SkinPass, Q  : EQL	*/
					model.setprocGbn(sVal.substring(0,1));
					
					/* COIL번호	CHAR(11) */
					model.setcoilNo(sStockId);
					
					/* 처리구분     CHAR(1)  1:보급,2:보급취소,3:추출,4:Take-Out,5:Take-In */
					model.setProcessId(sVal.substring(1,2));
					  
					/* 위치포지션  CHAR(2)  */
					model.setpositionNo("5".equals(sVal.substring(1,2))?
										YmCommonConst.PO_POSITION_D5:
										YmCommonConst.PO_POSITION_D1);
					
					EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
					isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{CommonModel.class},
																  	  	 new Object[]{model});
					logger.println(LogLevel.DEBUG,this, "내부IF호출===SPM, HFL 입측에 권하시 발생.===");
					
					
					//품질 열연정정입측보급실적----------------------------------------------
					YdDelegate      ydDelegate      = new YdDelegate();					 
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("MSG_ID",      	"YDQMJ002"); 							 
					recInTemp.setField("STL_NO",     	sStockId);	  			//재료번호				
					ydDelegate.sendMsg(recInTemp);
			 
					logger.println(LogLevel.DEBUG,this, "품질 L3 열연정정입측보급실적 전송 송신 완료9"); 
					//-------------------------------------------------------------------
				
				}else if(YmCommonConst.NEW_SCH_WORK_KIND_EQLI.equals(sSch_Code)||
						 YmCommonConst.NEW_SCH_WORK_KIND_EQLR.equals(sSch_Code)||
						 YmCommonConst.NEW_SCH_WORK_KIND_EQTI.equals(sSch_Code)||
						( (YmCommonConst.NEW_SCH_WORK_KIND_CCMU.equals(sSch_Code) || YmCommonConst.NEW_SCH_WORK_KIND_CCMR.equals(sSch_Code)) 
						   && "QE".equals(sPut_Position.substring(2, 4)) ) //CTS하차 -> EQ보급(직보급) 
						){
					
					recInTemp = JDTORecordFactory.getInstance().create();
	    			recInTemp.setField("MSG_ID",        "YDHRJ005"); 							//열연조업 L3 정정보급완료 실적  전문코드
	    			recInTemp.setField("STL_NO",     	sStockId);								//재료번호
	    			recInTemp.setField("YD_EQP_ID",     sPut_Position.substring(0, 6));			//야드설비id :"1EQE01"
	    			//recInTemp.setField("YD_STK_BED_NO", sPut_Position.substring(6,8));			//야드적치베드번호
	    			recInTemp.setField("YD_STK_BED_NO", "01");			//야드적치베드번호
	    			recInTemp.setField("YD_DN_CMPL_DT", YmCommonUtil.getTcDate("yyyyMMddHHmmss"));	//야드권하완료일시
	    			recInTemp.setField("TREAT_GP", "1");	
	    			
	    			ydDelegate.sendMsg(recInTemp);

					logger.println(LogLevel.DEBUG,this, "열연조업 L3 이퀄라이저 보급완료 실적 전송 송신 완료");
					
					
					//품질 열연정정입측보급실적----------------------------------------------
					YdDelegate      ydDelegate      = new YdDelegate();					 
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("MSG_ID",      	"YDQMJ002"); 							 
					recInTemp.setField("STL_NO",     	sStockId);	  			//재료번호				
					ydDelegate.sendMsg(recInTemp);
			 
					logger.println(LogLevel.DEBUG,this, "품질 L3 열연정정입측보급실적 전송 송신 완료10"); 
					//-------------------------------------------------------------------
				}
				
			}
			
			/*
			 *	9.	출하
			 *		B열연 수냉재 코일 수조탱크 보급완료시.
			 */		
			{
			   
			 	 String sStockId		= StringHelper.evl(jtR.getFieldString("Coil_No"), "").trim();
				 String sPut_Position 	= StringHelper.evl(jtR.getFieldString("Put_Position"), "");
				 
				 JDTORecord dmRc = null;
				 
				 /**
				 *	수조탱크 보급완료정보 처리 부분
				 *	이미 실적처리가 되어 있으면 실적처리 하지 않음.
				 *	1.	진도코드가 A,B,C가 아니면 SKIP
				 *	2.	다음공정 및 계획공정이 2T가 아니면 SKIP
				 */
				boolean isPros1		= false; 
				boolean isPros2		= false; 
				boolean isPros3		= false; 
				String sCoilProc    = "";
				String sNextProc 	= "";
				String sPlanProc 	= "";
				String sCurrProgCd 	= "";
				
				String sPutUsageCd = YmCommonUtil.getStackColInfoWithPk(sPut_Position.substring(0, 6));
				
		   		if(YmCommonConst.STACK_COL_USAGE_CD_CW.equals(sPutUsageCd)){ // 수냉탱크
					isPros1 = true; 
				}
				
				if(isPros1){
					
					JDTORecord stockJr	= dao.getCoilCommonInfo(sStockId); 
					if(stockJr != null){
						sNextProc	= StringHelper.evl(stockJr.getFieldString("차공정"), "");
						sPlanProc 	= StringHelper.evl(stockJr.getFieldString("계획공정"),"");
						sCurrProgCd = StringHelper.evl(stockJr.getFieldString("CURR_PROG_CD"), "");
					}
					if("".equals(sNextProc)){
						sCoilProc = sPlanProc;
					}else{
						sCoilProc = sNextProc;
					}
					 
					/*
					 *	다음공정이 '2T'
					 */
					if(YmCommonConst.SHEAR_SUPPLY_GP_5T.equals(sCoilProc)){
						isPros2		= true; 
					}
					/*
					 *	진도코드가 A,B,C
					 */
					if(YmCommonConst.CURR_PROG_CD_COIL_A.equals(sCurrProgCd) || //재질판정대기 AC  
					   YmCommonConst.CURR_PROG_CD_COIL_B.equals(sCurrProgCd) || //정정작업지시 BC 
					   YmCommonConst.CURR_PROG_CD_COIL_C.equals(sCurrProgCd) ){ //정정작업대기 CC 
						isPros3		= true; 
					}
					
					if(isPros2&&isPros3){ 	
						 //AB열연  ##############################################################################################
					 /*	YMDM013 model = new YMDM013();
					 	model.setTcCode(YmCommonConst.MODEL_YMDM013);
					 	model.setTcDate(YmCommonUtil.getCurDate("yyyy-MM-dd"));
					 	model.setTcTime(YmCommonUtil.getCurDate("HH-mm-ss"));
					 	
					 	// COIL번호  CHAR(10)	
					 	model.setGOODS_NO(sStockId);
					 	
					 	EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
					 	isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{CommonModel.class},
					 												  	  	 new Object[]{model});
					 	logger.println(LogLevel.DEBUG,this, "내부IF호출===수조탱크 보급 완료시.===");
					 */
					    //일관제철 ##############################################################################################	
	                    
						//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	                    //HYSCO수냉실적
	         			JDTORecord tcRecordDM = null;
	         			tcRecordDM = JDTORecordFactory.getInstance().create(); 
	         			tcRecordDM.setField("GOODS_NO", sStockId); 
	         			
	         			//인터페이스 전문 호출
	         			EJBConnector ejbConn1 = new EJBConnector("default","JNDIYardWrkResReg",this);
	         			isSuccess = (Boolean)ejbConn1.trx("getYDDMR025",new Class[]{JDTORecord.class},
	         			  	  	 new Object[]{tcRecordDM}); 
	                    logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 HYSCO수냉실적.===");
	                    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
					}
				}
			}
			
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue();
	}
	
	
	/**
	 * 오퍼레이션명 : 차량을 이용한 이적작업
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
	public boolean callInnerWorkInfo_CoilTR(JDTORecord jtR){
								     	  	
		Boolean isSuccess = new Boolean(false);
		ymCommonDAO ymCommonDAO =new ymCommonDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO = new YdWBookDAO();
		YdStockDAO ydStockDAO = new YdStockDAO();
		String wBookid 		= "";
		String	sCarCardNo 	= "";
		String  sCarArr		= "";
		String szstackcolGp = "";
		logger.println(LogLevel.DEBUG,this, "============================");
		logger.println(LogLevel.DEBUG,this, "callInnerWorkInfo_CoilTR("+ jtR + ") 시작");
		logger.println(LogLevel.DEBUG,this, "============================");

		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			
			String tWork_Id 		= YmCommonUtil.getCurDataWithLegacy(
									  StringHelper.evl(jtR.getFieldString("Work_Id"), ""));
			logger.println(LogLevel.DEBUG,this, "callInnerWorkInfo_CoilTR(): " + tWork_Id);
			if(YmCommonConst.SUB_WORK_S.equals(tWork_Id)){
				logger.println(LogLevel.DEBUG,this, "내부IF호출===보조작업대상으로 작업실적전송 SKIP.===");
				return false;
			}
			
		
			
			/*
			 *	3.	출하
			 *		Coil 제품창고 이송 상/하차 작업 시 첫 Coil 상/하차 권하 시
			 *		Coil 제품창고 이송 상/하차 작업 시 마지막 Coil 상/하차 권하 시
			 */		
			{
				String sStockId	= StringHelper.evl(jtR.getFieldString("Coil_No"), "").trim();
				String sSchCode = StringHelper.evl(jtR.getFieldString("Sch_Code"), "");
				

				if(YmCommonConst.NEW_SCH_WORK_KIND_CVM8.equals(sSchCode)|| // COIL 소재차량이송상차(R)
				   YmCommonConst.NEW_SCH_WORK_KIND_CVM9.equals(sSchCode)|| // COIL 소재차량이송하차(R)	
				   YmCommonConst.NEW_SCH_WORK_KIND_GVM8.equals(sSchCode)|| // COIL 제품차량이송상차(R)
				   YmCommonConst.NEW_SCH_WORK_KIND_GVM9.equals(sSchCode)|| // COIL 제품차량이송하차(R)	
				   YmCommonConst.NEW_SCH_WORK_KIND_CVM6.equals(sSchCode)|| // COIL 소재차량이송상차(L)
				   YmCommonConst.NEW_SCH_WORK_KIND_CVM7.equals(sSchCode)|| // COIL 소재차량이송하차(L)	
				   YmCommonConst.NEW_SCH_WORK_KIND_GVM6.equals(sSchCode)|| // COIL 제품차량이송상차(L)
				   YmCommonConst.NEW_SCH_WORK_KIND_GVM7.equals(sSchCode)){ // COIL 제품차량이송하차(L)
					
					logger.println(LogLevel.DEBUG,this, "Coil 소재,제품 차량 이송 상/하차 작업: CVM8, CVM9 , GVM8, GVM9");
					
					List 	   dmList  = null; // Card No,운송지시일자,순번이 같은 저장품정보
					JDTORecord dmRc    = null; // 저장품정보
					String 	   sUpDown = "";   // 이송상차/하차구분
					String	   iDmSize = "0";    // Card No,운송지시일자,순번이 같은 저장품 갯수
					String 		iWbookSize ="";
					if(!"".equals(sStockId)){
						logger.println(LogLevel.DEBUG,this, "Coil 소재이송상차,소재이송하차,제품이송상차,제품이송하차 처리: "+sStockId);
						
						
						if(YmCommonConst.NEW_SCH_WORK_KIND_CVM8.equals(sSchCode)||YmCommonConst.NEW_SCH_WORK_KIND_CVM6.equals(sSchCode)){ // COIL 소재차량이송상차
						   	sUpDown = "U";
						}else if(YmCommonConst.NEW_SCH_WORK_KIND_CVM9.equals(sSchCode)||YmCommonConst.NEW_SCH_WORK_KIND_CVM7.equals(sSchCode)){// COIL 소재차량이송하차
							sUpDown = "D";
						}else if(YmCommonConst.NEW_SCH_WORK_KIND_GVM8.equals(sSchCode)||YmCommonConst.NEW_SCH_WORK_KIND_GVM6.equals(sSchCode)){// COIL 제품차량이송상차	
							sUpDown = "U";
						}else if(YmCommonConst.NEW_SCH_WORK_KIND_GVM9.equals(sSchCode)||YmCommonConst.NEW_SCH_WORK_KIND_GVM7.equals(sSchCode)){// COIL 제품차량이송하차			
							sUpDown = "D";
						}
						
						
						String sQueryId_EmptyBay = "ym.facilitystatus.facilityinquiry.dao.YdStockDAO.getCardNo";
						List listCoilPos = dao.getListData(sQueryId_EmptyBay, new Object[] {sStockId});
					 
						logger.println(LogLevel.DEBUG, this, "listCoilPos.size()"+ listCoilPos.size());
						JDTORecord jtrCoilPos = null;
						if (listCoilPos.size() > 0) {
							for(int j=0;j<listCoilPos.size();j++)
							{
								jtrCoilPos = (JDTORecord)listCoilPos.get(j);
								sCarCardNo = StringHelper.evl(jtrCoilPos.getFieldString("CAR_CARD_NO"),"");
								sCarArr    = StringHelper.evl(jtrCoilPos.getFieldString("CTS_RELAY_SADDLE"),""); //상하차방향
		
							}
						}
						
						//상하차 처리유무 등록
						String stkQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStocksupplyyn";
						int stockSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[] {sUpDown, sStockId});
						
			    		dmList  = dao.getYmDmCommonInfo5(sCarCardNo,sStockId,sUpDown);

						dmRc    = (JDTORecord)dmList.get(0);
						iDmSize = StringHelper.evl(dmRc.getFieldString("CNT"), "");
						iWbookSize = StringHelper.evl(dmRc.getFieldString("WBOOK_CNT"), "");
						logger.println(LogLevel.DEBUG,this, sCarCardNo+"::상,하차완료 매수 :"+iDmSize+"남은 상차작업예약 건수:"+iWbookSize);	
						
			    		String sTransWordNo   = StringHelper.evl(dmRc.getFieldString("TRANS_WORD_NO"), "");
			    		String sBayGp		  = StringHelper.evl(dmRc.getFieldString("CARUNLOAD_BAY"), "");
			    		String sCAR_MAXCNT    = StringHelper.evl(dmRc.getFieldString("CARUNLOAD_YD"),""); // 상차가능매수
			    		

						
						if((!iDmSize.equals("0") && iDmSize.equals(sCAR_MAXCNT)) || iWbookSize.equals("0")){ // 상,하차완료인 저장품이 ALL일때 처리
							logger.println(LogLevel.DEBUG,this, "상,하차완료인 저장품이 ALL일때 처리");	
				    		
				    		if(sUpDown.equals("U")){
				    			logger.println(LogLevel.DEBUG,this, "상차완료 처리 작업 -> 하차지 스케줄 생성");		
				    			
				    			//하차지 작업예약 생성///////////////////////////////////////////////////////////////////				    			
//				    			 작업예약(TB_YM_WBOOK) Table Select WBOOK_ID Key 생성 Seq + 1
								String wBookQueryId = "ym.steelinfo.steelinforecv.dao.YdStockDAO.getWBookID";
								JDTORecord wBookSel = ydStackLayerDAO.requestFind(wBookQueryId);
								wBookid = wBookSel.getFieldString("WBOOK_ID");
								logger.println(LogLevel.DEBUG, this, "차량동간이적하차지: 작업예약ID["+wBookid+"]");	
								
								
//								<option value="1"> 좌(L)->우(R) </option>
//							    <option value="2"> 좌(L)->좌(L) </option>
//							    <option value="3"> 우(R)->좌(L) </option>
//							    <option value="4"> 우(R)->우(R) </option>
								if(sSchCode.substring(3,4).equals("6")){
									//상차(L) -> 하차
									if(sCarArr.equals("1")||sCarArr.equals("4")){
										//하차(우R)
										sSchCode = sSchCode.substring(0,3)+"9";
									}else if(sCarArr.equals("2")||sCarArr.equals("3")){
										//하차(좌L)
										sSchCode = sSchCode.substring(0,3)+"7";
									}
								}else if(sSchCode.substring(3,4).equals("8")){
									//상차(R) -> 하차
									if(sCarArr.equals("1")||sCarArr.equals("4")){
										//하차(우R)
										sSchCode = sSchCode.substring(0,3)+"9";
									}else if(sCarArr.equals("2")||sCarArr.equals("3")){
										//하차(좌L)
										sSchCode = sSchCode.substring(0,3)+"7";
									}
								}
			
								
								//하차지 작업예약 생성
								String sWBookQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.InsertYdWBook";
								int wbookSeq = ydWBookDAO.requestinsertData(sWBookQueryId,
																			new Object[] { wBookid, "3", sBayGp, sSchCode,
																			YmCommonUtil.getWorkDuty(),
																			YmCommonUtil.getWorkParty() });

								//하차방향
								if(sCarArr.equals("1")||sCarArr.equals("4")){
									//우(R)
									szstackcolGp = "3"+ sBayGp+"TR0"+sCarCardNo.substring(3, 4);
								}else{
									//좌(L)
									szstackcolGp = "3"+ sBayGp+"TR1"+sCarCardNo.substring(3, 4);
								}
								
								
								//하차지 차량위치 생성
								stkQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateStacklayercreate2";
								  stockSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[] {
										  										sTransWordNo, sCarCardNo,szstackcolGp});
								  
								//상차지 차량위치 초기화
								stkQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateStacklayerclear";
								  stockSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[] {
										  										sTransWordNo, sCarCardNo});
								  
								  
								  //하차완료 처리(이전 하찾가업 잔재 초기화)
								 stkQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockTranwordno";
								 stockSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[] {
																				"", sTransWordNo, sCarCardNo, "D" });
									 
								//하차대상 작업예약 등록
								 stkQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockTranwordno";
								 stockSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[] {
																				wBookid, sTransWordNo, sCarCardNo, sUpDown });
								  
								///////////////////////////////////////////////////////////////////////////////////////
							
				    		}else{
				    			logger.println(LogLevel.DEBUG,this, "하차완료 처리 작업 -> 상차지 스케줄 생성:"+iWbookSize);		
				    			
				    			if(!iWbookSize.equals("0")){
					    			String wBookQueryId = "ym.steelinfo.steelinforecv.dao.YdStockDAO.getWBookID2";
					    			JDTORecord wBookSel = ydStackLayerDAO.requestgetData(wBookQueryId, new Object[] {
																							 sTransWordNo, sCarCardNo});
					    			
					    			if(wBookSel.size() > 0){
										wBookid = wBookSel.getFieldString("WBOOK_ID");
										logger.println(LogLevel.DEBUG, this, "차량동간이적상차지: 작업예약ID["+wBookid+"]");	
					    			}
					    			
				    			}
				    			
								//하차완료 처리 
								 stkQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockTranwordno";
								 stockSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[] {
																				"", sTransWordNo, sCarCardNo, sUpDown });
								
				    		}
				    		
				    		//작업예약에 해당동 상차 스케줄이 존재 하는 경우 크레인 스케줄 호출 SKIP
							String sQueryId = "ym.steelinfo.steelinforecv.dao.YdStockDAO.getWBookSchskip";
							listCoilPos = dao.getListData(sQueryId, new Object[] {wBookid});
						 
							logger.println(LogLevel.DEBUG, this, "listCoilPos.size()"+ listCoilPos.size());
							if (listCoilPos.size() <= 0) {
				    		
								if(!wBookid.equals("")){
								//크레인 스케줄 호출 ---------------------------------------------------------------
								EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
								isSuccess = (Boolean)ejbConn.trx("callCraneSchInfo",new  Class[]{String.class},
																					new Object[]{wBookid});
								}
							}
						}
					
					}
					
				}



			}
			
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue();
	}
	
	/**
	 * 오퍼레이션명 : 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
	public boolean callInnerWorkInfo_Coil_Dm(JDTORecord jtR){
								     	  	
		Boolean isSuccess = new Boolean(false);
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			
			/*
			 *	0.	출하
			 *		Coil을 제품 야드에서 동내, 동간 이적 권하 시
			 */		
			
			String sStockId	= StringHelper.evl(jtR.getFieldString("Coil_No"), "").trim();
					
	  		String[] sStockInfo = YmCommonUtil.getCoilCurrProgCd(sStockId,"");
			String sProgCd   	= sStockInfo[0];
			String sStocMv   	= sStockInfo[1];
			
			if (sProgCd.equals(YmCommonConst.CURR_PROG_CD_COIL_K) || 
				sProgCd.equals(YmCommonConst.CURR_PROG_CD_COIL_Z) || 
				sProgCd.equals(YmCommonConst.CURR_PROG_CD_COIL_J) || 
				sProgCd.equals(YmCommonConst.CURR_PROG_CD_COIL_L) || 
				sProgCd.equals(YmCommonConst.CURR_PROG_CD_COIL_N) || //20091105 추가 
				sProgCd.equals(YmCommonConst.CURR_PROG_CD_COIL_X) || 
				sProgCd.equals(YmCommonConst.CURR_PROG_CD_COIL_P) || 
				sProgCd.equals(YmCommonConst.CURR_PROG_CD_COIL_M)){
					
				JDTORecord dmRc = dao.getYMDM008Info_03(sStockId);
				    
				if(dmRc != null){
				    		
				    String sUpLoc  = StringHelper.evl(dmRc.getFieldString("CRANE_WRSLT_UP_LOC"), "");
				    String sPutLoc = StringHelper.evl(dmRc.getFieldString("CRANE_WRSLT_PUT_LOC"), "");
				    
				    /**
				     * 2007.05.09 이정훈
				     * 이적 실적 송신
				     * 입고시점,상차시점
				     */
					if (sProgCd.equals(YmCommonConst.CURR_PROG_CD_COIL_H)||
						sProgCd.equals(YmCommonConst.CURR_PROG_CD_COIL_L)	){	
						
						if(sPutLoc.equals("")){
							logger.println(LogLevel.DEBUG,this, "내부IF호출===제품 이적 Skip.===");
							return true;
						}
						String sPutUsageCd = YmCommonUtil.getStackColInfoWithPk(sPutLoc.substring(0, 6));
						
						if (YmCommonConst.STACK_COL_USAGE_CD_CX.equals(sPutUsageCd)	|| // 대차정지위치
							YmCommonConst.STACK_COL_USAGE_CD_TX.equals(sPutUsageCd) || // 차량정지위치
							YmCommonConst.STACK_COL_USAGE_CD_FS.equals(sPutUsageCd) || // CTS FROM SADDLE
							YmCommonConst.STACK_COL_USAGE_CD_TS.equals(sPutUsageCd) // CTS TO SADDLE
							) 
						{
							logger.println(LogLevel.DEBUG,this, "내부IF호출===제품 이적 Skip.===");
							return true;
						}
						
					}
					
				/*	YMDM008 model = new YMDM008();
					model.setTcCode(YmCommonConst.MODEL_YMDM008);
					model.setTcDate(YmCommonUtil.getCurDate("yyyy-MM-dd"));
					model.setTcTime(YmCommonUtil.getCurDate("HH-mm-ss"));
							
					// 이적 일자 
					model.setMOVENSTACK_DATE(YmCommonUtil.getCurDate("yyyyMMdd"));
							
					// 이적 시각 
					model.setMOVENSTACK_TIME(YmCommonUtil.getCurDate("HHmmss"));
							
					// 코일 번호 
					model.setGOODS_NO(sStockId);
							
					// FROM 저장 위치  1F02030901 
					model.setBEFO_STORE_LOC(sUpLoc);
							
					// TO 저장 위치  1F02030901 
					model.setSTORE_LOC(sPutLoc);
							
					EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
					isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{CommonModel.class},
																		  	  	 new Object[]{model});
					logger.println(LogLevel.DEBUG,this, "내부IF호출===Coil을 제품 야드에서 동내, 동간 이적 권하 시.===");*/
					
				
           		    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                    //코일제품이적작업실적
         			JDTORecord tcRecordDM = null;
         			tcRecordDM = JDTORecordFactory.getInstance().create(); 
         			tcRecordDM.setField("GOODS_NO",sStockId);
         			tcRecordDM.setField("BEFO_STORE_LOC",sUpLoc);
         			tcRecordDM.setField("TO_STORE_LOC",sPutLoc);
         			
         			//인터페이스 전문 호출
         			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
         			isSuccess = (Boolean)ejbConn.trx("getYDDMR004",new Class[]{JDTORecord.class},
         			  	  	 new Object[]{tcRecordDM}); 
                    logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 코일제품이적작업실적.===");
                    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                    
					}
				} 
				
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue();
	}
	
       /**
	 * 오퍼레이션명 : 
	 *
	 *	3.	COIL INFO
        *		권하작업실적 발생시 작업결과송신 Call
        *		처리결과 메세지를 야드 Level-2에 송신한다.
        *		A열연 COIL 은 전문이 존재함 
        *		B열연은 일단 작업결과송신을 하지 않기로 함
        *
        * param jDTORecord 	: 전문항목
        * param String		: 0 - 정상, 1 - 이상
	 *
        * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
	public boolean callWorkFinishInfo_Coil(JDTORecord tcRc,String sGbn){
		
		boolean isSuccess = false;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String sTc 				= StringHelper.evl(tcRc.getFieldString("TC"), "");
			/* 
			 * A열연 COIL 권하/권하이상 실적이 발생시
			 */
			if(YmCommonConst.TC_THCH570.equals(sTc)||
			   YmCommonConst.TC_THCH580.equals(sTc)){
				
				String sLegacyCraneNo = StringHelper.evl(tcRc.getFieldString("LEGACY_CRANE_NO"), "");
	    	
	    		isSuccess = sendWorkFinishInfo_Coil(sLegacyCraneNo,sGbn);
	    		logger.println(LogLevel.DEBUG,this, "A열연 COIL 권하실적결과 송신 CALL======");				
			}
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess;
	}
	
       /**
	 * 오퍼레이션명 : 
	 *
	 *	3.1	COIL INFO
        *		권하작업실적 발생시 작업결과송신 Call
        *		처리결과 메세지를 야드 Level-2에 송신한다.
        *		A열연 COIL 은 전문이 존재함 
        *		B열연은 일단 작업결과송신을 하지 않기로 함
        *
        * param String 	: 크레인번호
        * param String	: 0 - 정상, 1 - 이상
	 *
        * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
	public boolean sendWorkFinishInfo_Coil(String sLegacyCraneNo,String sGbn){
		
		Boolean isSuccess = new Boolean(false);
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String sMessage	= setAWorkFinishMsgInfo(YmCommonConst.TC_THHC200,
													sLegacyCraneNo,
													sGbn);
			
			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
			isSuccess = (Boolean)ejbConn.trx("THHC200send",new Class[]{String.class},new Object[]{ sMessage });	
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue();
	}
       
        /**
	 * 오퍼레이션명 : 
	 *
	 *	4.	COIL INFO
        *		권하작업실적 발생시 작업결과송신 Call
        *		처리결과 메세지를 B열연 MILL Level-2에 송신한다.
        *
        * param dao 			: DAO
        * param jDTORecord 	: 전문항목
	 *
        * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
	public boolean callMillFinishInfo_Coil(JDTORecord tcRc){
		
		Boolean isSuccess = new Boolean(false);
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			String sTc = StringHelper.evl(tcRc.getFieldString("TC"), "");
						
			if(
			   YmCommonConst.TC_CN1PB06.equals(sTc)	|| //B열연 Coil 권하실적
			   YmCommonConst.TC_CN1PB07.equals(sTc)	   //B열연 Coil 권하이상실적
			  ){
				
				logger.println(LogLevel.DEBUG,this, "==B열연 COIL MILL L2 외부IF 호출==");
				
				String sStockId         = StringHelper.evl(tcRc.getFieldString("Coil_No"), "").trim();
				String sSch_Code 		= StringHelper.evl(tcRc.getFieldString("Sch_Code"), "");
				String sWork_Id 		= YmCommonUtil.getCurDataWithLegacy(
										  StringHelper.evl(tcRc.getFieldString("Work_Id"), ""));
				String sPut_Position 	= StringHelper.evl(tcRc.getFieldString("Put_Position"), "");
				
				/*
				 * B열연 L2로부터 분기콘베이어 Line In 요구를 받아 Coil Line In 완료시 호출
				 * Param : String Coil_No, String Destination, String Position
				 */
				{
					//분기컨베이어보급(CDLI) 스케쥴코드 체크
					if(YmCommonConst.NEW_SCH_WORK_KIND_CDLI.equals(sSch_Code)){
						
						EJBConnector ejbConn = new EJBConnector("default","JNDICCExtWrkOrdReg",this);
						isSuccess = (Boolean)ejbConn.trx("receiveConvLineInResult",new  Class[]{String.class,
																								String.class},
																				   new Object[]{sStockId,
																				   				sPut_Position});
						logger.println(LogLevel.DEBUG,this, "외부IF호출===B열연 L2로부터 분기콘베이어 Line In 요구를 받아 Coil Line In 완료시 호출.===");
					}
				}
				
				/*
				 * B열연 L2로부터 Coil Take In 요구를 받아 Coil Take In 완료시 호출
				 * Param : String Coil_No
				 */
				{
					//TAKE IN(CDTI) 스케쥴코드 체크
					if(YmCommonConst.NEW_SCH_WORK_KIND_CDTI.equals(sSch_Code)){
						
						EJBConnector ejbConn = new EJBConnector("default","JNDICCExtWrkOrdReg",this);
						isSuccess = (Boolean)ejbConn.trx("receiveConvTakeInResult",new  Class[]{String.class},
																					new Object[]{sStockId});
						logger.println(LogLevel.DEBUG,this, "외부IF호출===B열연 L2로부터 Coil Take In 요구를 받아 Coil Take In 완료시 호출.===");
					}
				}  
				
			}
	
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue();
	}
	
       /**
	 * 오퍼레이션명 : 
	 *
	 *	5.	COIL INFO
        *		대차 작업지시 Call
        *		권하시 대차에게 작업지시 전문을 Call
        *
        * param jDTORecord 	: 전문항목
        * param String		: Wbook_id
	 *
        * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
	public boolean callTcWorkInfo_Coil(JDTORecord jtR){
		
		Boolean isSuccess = new Boolean(false);
		logger.println(LogLevel.DEBUG,this, "callTcWorkInfo_Coil() Start");
		logger.println(LogLevel.DEBUG,this, "대차출발 Start");
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			String sYard_Id 	 = StringHelper.evl(jtR.getFieldString("Yard_Id"), "");
			String sTc 			 = StringHelper.evl(jtR.getFieldString("TC"), "");
			String sSch_Code 	 = StringHelper.evl(jtR.getFieldString("Sch_Code"), "");
			String sPut_Position = StringHelper.evl(jtR.getFieldString("Put_Position"), "");
			String sStockId      = StringHelper.evl(jtR.getFieldString("Coil_No"), "").trim();
			String sStackColGp   = (sPut_Position.length() >=  6)? sPut_Position.substring(0, 6):"";
		   	String sStackBedGp   = (sPut_Position.length() >=  8)? sPut_Position.substring(6, 8):"";
		   	String sStackLayerGp = (sPut_Position.length() >= 10)? sPut_Position.substring(8,10):"";
			
			logger.println(LogLevel.DEBUG,this, "sYard_Id= "+sYard_Id);
			logger.println(LogLevel.DEBUG,this, "TC= "+sTc);
			logger.println(LogLevel.DEBUG,this, "SCH_CODE= "+sSch_Code);
			logger.println(LogLevel.DEBUG,this, "PUT LOC= "+sPut_Position);
			logger.println(LogLevel.DEBUG,this, "STOCK_ID= "+sStockId);
			/*
			 * 권하위치항목을 가지고 적치단,설비 TABLE을 JOIN해서
			 * 조건으로 설비종류가 대차인 경우를 체크한다.	
			 */
			String sPutUsageCd = YmCommonUtil.getStackColInfoWithPk(sStackColGp);
			/*
			 * 권하위치가 대차일 경우에 
			 * 대차출발 모듈을 호출한다.
			 */
			if(YmCommonConst.STACK_COL_USAGE_CD_CX.equals(sPutUsageCd)){ // 대차정지위치
				 
    			logger.println(LogLevel.DEBUG,this, "====TC 작업지시========");
    			logger.println(LogLevel.DEBUG,this, "sStackColGp="	+ sStackColGp);
    			logger.println(LogLevel.DEBUG,this, "sStackBedGp="	+ sStackBedGp);
    			logger.println(LogLevel.DEBUG,this, "sStackLayerGp="+ sStackLayerGp);
    			logger.println(LogLevel.DEBUG,this, "sStockId="		+ sStockId);
				
				/**
		    	 *	1.  해당 권상위치(적치번지)의 적치BED
				 * 		현재수량(-1), 가능수량(+1) 을 셋팅한다.
				 * 		tb_ym_stacker Table stack_bed_qnty_curr : 적치BED수량현재
				 * 		tb_ym_stacker Table stack_bed_able_qnty : 적치BED가능수량
				 */ 
				{ 
					String sCurrQty	= "0";
					
					sCurrQty	= "1";
					 
					int iReq = dao.updateStackerQtyInfo(sStackColGp,
				    									sStackBedGp,
				    									sCurrQty);
				}
				
				/**
				 * 저장품TABLE의 이동설비항목에 
				 * 권하위치값을 셋팅한다.
				 * tb_ym_stock Table frtomove_equip_gp : 		sPut_Position.substring(0, 6)
				 * tb_ym_stock Table frtomove_equip_bed_gp : 	sPut_Position.substring(6, 8)
				 * tb_ym_stock Table frtomove_equip_layer_gp : 	sPut_Position.substring(8,10)
				 */ 
				if(sPut_Position.length() >= 10)
				{
					int iReq = dao.updateStockMoveEquipInfo(sStockId,
				    								   		sStackColGp.substring(0, 1)+ "X"+
				   						    				sStackColGp.substring(2),
				    								   		sStackBedGp,
				    								   		sStackLayerGp,
				    								   		"",
				    								   		"");
				}
				
				/**
				 * 대차출발 EJB CALL
				 */
				if(!YmCommonConst.YD_GP_1.equals(sYard_Id)){ 
					// B열연일 경우만 적용.YmCommonConst.YD_GP_3 
					/**
	    			 *	동간추출시 설비에 있는 목적동 정보를
	    			 *	저장품정보에 셋팅한다.
	    			 *
	    			 *  추가: 최규성 CNLO에 대한 조건 추가
		    		 *
		    		 *	3CFD01	C동 HFL출측컨베이어		YmCommonConst.STACK_COL_GP_3CFD01
					 *	3AKD01	A동 SPM출측컨베이어		YmCommonConst.STACK_COL_GP_3AKD01
					 *  3EKD01  E동 SPM2출측컨베이어		YmCommonConst.STACK_COL_GP_3EKD01			// 최규성
					 */
					if(YmCommonConst.NEW_SCH_WORK_KIND_CFLO.equals(sSch_Code)||  //Coil HFL 추출
					   YmCommonConst.NEW_SCH_WORK_KIND_CKLO.equals(sSch_Code)||  //Coil SPM 추출
					   YmCommonConst.NEW_SCH_WORK_KIND_CNLO.equals(sSch_Code) ) {//Coil SPM2 추출
	    				
	    				String sBay 	= "";
	    				String sEquip 	= "";
	    				
	    				if(YmCommonConst.NEW_SCH_WORK_KIND_CFLO.equals(sSch_Code)){//Coil HFL 추출
	    				
	    					sEquip = YmCommonConst.STACK_COL_GP_3CFD01;
					   	
					   	}else if(YmCommonConst.NEW_SCH_WORK_KIND_CKLO.equals(sSch_Code)){//Coil SPM 추출
					   	
					   		sEquip = YmCommonConst.STACK_COL_GP_3AKD01;
	    				}
	    				/*
	    				 * SPM2에 대한 정보를 추가한다.
	    				 * 최규성
	    				 * */
					   	else if(YmCommonConst.NEW_SCH_WORK_KIND_CNLO.equals(sSch_Code)){	// Coil SPM2 추출
					   		sEquip = YmCommonConst.STACK_COL_GP_3EKD01;
					   	}
	    				
	    				JDTORecord equipJr 	= dao.getEquipInfoWithEquipGp(sEquip);
	    				
	    				if(equipJr != null){
	    					sBay = StringHelper.evl(equipJr.getFieldString("CARUNLOAD_BAY"), "").trim();	
	    				} 
	    				logger.println(LogLevel.DEBUG,this, "목적동=" + sBay);
	    				/* 
	    				Ver4.0--
	    				UPDATE TB_YM_STOCK
	    				SET
	    				    CARUNLOAD_PUT_LOC    = :Bay,	
	    				    modifier    = 'SYSTEM',
	    				 	mod_ddtt    = sysdate     
	    				WHERE STOCK_ID 	= :stock_id            
	    				*/
	    				int iSeq = dao.updateStockPutLocWithStockId(sStockId,sBay);
	    				// Stock테이블에 CARUNLOAD_PUT_LOC외에 CARUNLOAD_PUT_YD, CARUNLOAD_PUT_BAY 정보도 업데이트한다. 최규성. 잠시 보류
	    			}
					
					String sMessage = StringHelper.evl(jtR.getFieldString("GROBAL_WBOOK_ID"), "") + 
									  sStackColGp + sStackBedGp;					
									
					logger.println(LogLevel.DEBUG,this, "====CR PUT시 TC 작업지시 CALL====");
					logger.println(LogLevel.DEBUG,this, "MESSAGE="	+ sMessage);
	    			
					EJBConnector ejbConn = new EJBConnector("default","JNDICTSStatusReg",this);
					isSuccess = (Boolean)ejbConn.trx("bsyVicCarMoveOrder",new Class[]{String.class},
																	  	  new Object[]{sMessage});
				}												  	  
			}			
			
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue(); 
	}
	
       /**
	 * 오퍼레이션명 : 
	 *
	 *	6.	COIL INFO
        *		CTS 작업지시 Call
        *		A열연 Saddle 권하시 CTS에게 작업지시 전문을 Call
        *
        * param jDTORecord 	: 전문항목
	 *
        * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
	public boolean callCtsWorkInfo_Coil(JDTORecord jtR){
		
		Boolean isSuccess = new Boolean(false);
    
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String sTc			 = StringHelper.evl(jtR.getFieldString("TC"), "");
			String sPut_Position = StringHelper.evl(jtR.getFieldString("Put_Position"), "");
			String sSch_Code 	 = StringHelper.evl(jtR.getFieldString("Sch_Code"), "");
			String sStackColGp   = (sPut_Position.length() >=  6)? sPut_Position.substring(0, 6):"";
		   	String sStackBedGp   = (sPut_Position.length() >=  8)? sPut_Position.substring(6, 8):"";
		   	String sStackLayerGp = (sPut_Position.length() >= 10)? sPut_Position.substring(8,10):"";
			/*
			 * A열연 Coil 권하/권하이상 실적이 발생하고
			 * 권하위치가 CTS SADDLE일 경우에만
			 * CTS 작업지시를 한다.
			 */
			if(YmCommonConst.TC_THCH570.equals(sTc)||
			   YmCommonConst.TC_THCH580.equals(sTc)){
				
				/*
				 * 권하위치항목을 가지고 적치단,설비 TABLE을 JOIN해서
				 * 조건으로 설비종류가 SADDLE인 경우를 체크한다.	
				 */
				String sPutUsageCd = YmCommonUtil.getStackColInfoWithPk(sStackColGp);
				/*
				 * 권하위치가 CTS SADDLE일 경우에 CTS에 작업지시를 한다.
				 */
				if(YmCommonConst.STACK_COL_USAGE_CD_FS.equals(sPutUsageCd)){ // CTS FROM SADDLE
	    			 
	    			String sStockId = StringHelper.evl(jtR.getFieldString("Coil_No"), "").trim();	
					
					logger.println(LogLevel.DEBUG,this, "====CTS 작업지시====");
	    			logger.println(LogLevel.DEBUG,this, "sStackColGp="	+ sStackColGp);
	    			logger.println(LogLevel.DEBUG,this, "sStockId="		+ sStockId);
	    			
	    			/**
	    			 *	동간추출시 설비에 있는 목적동 정보를
	    			 *	저장품정보에 셋팅한다.
		    		 *
		    		 *	1BDC01	B동 분기컨베이어		YmCommonConst.STACK_COL_GP_1BDC01
					 *	1CFD01	C동 HFL출측컨베이어		YmCommonConst.STACK_COL_GP_1CFD01
					 *	1FKD01	F동 SPM출측컨베이어		YmCommonConst.STACK_COL_GP_1FKD01	
					 */
					if(YmCommonConst.NEW_SCH_WORK_KIND_CDLO.equals(sSch_Code)||//Coil DC Line Off
	    			   YmCommonConst.NEW_SCH_WORK_KIND_CFLO.equals(sSch_Code)||//Coil HFL 추출
					   YmCommonConst.NEW_SCH_WORK_KIND_CKLO.equals(sSch_Code)||
					   YmCommonConst.NEW_SCH_WORK_KIND_EQLO.equals(sSch_Code)
						){//Coil SPM 추출
	    				
	    				String sBay 	= "";
	    				String sEquip 	= "";
	    				
						if (YmCommonConst.NEW_SCH_WORK_KIND_CDLO.equals(sSch_Code)) {// Coil DC Line Off

							sEquip = YmCommonConst.STACK_COL_GP_1BDC01;

						} else if (YmCommonConst.NEW_SCH_WORK_KIND_CFLO.equals(sSch_Code)) {// Coil HFL 추출

							sEquip = YmCommonConst.STACK_COL_GP_1CFD01;

						} else if (YmCommonConst.NEW_SCH_WORK_KIND_CKLO.equals(sSch_Code)) {// Coil SPM 추출

							sEquip = YmCommonConst.STACK_COL_GP_1FKD01;
							
						} else if (YmCommonConst.NEW_SCH_WORK_KIND_EQLO.equals(sSch_Code)) {// Coil EQ 추출
							
							if ("F".equals(sStackColGp.substring(1 , 2))) {
								
								sEquip = YmCommonConst.STACK_COL_GP_1FQD01;
								
							} else {
								
								sEquip = YmCommonConst.STACK_COL_GP_1GQD01;
							}
						}
	    				
	    				String[] sStockInfo = YmCommonUtil.getCoilCurrProgCd(sStockId,"");
					    String sProgCd   	= sStockInfo[0];
						String sStocMv   	= sStockInfo[1];
						
	    				JDTORecord equipJr 	= dao.getEquipInfoWithEquipGp(sEquip);
	    				
	    				if(equipJr != null){
	    					
	    					/*
	    					 *  차공정재 목적동 처리
	    					 *  정정작업지시대기,정정작업지시 추가 2012.05.10 
	    					 *  HFL/SPM 추출 인경우에만 중계동 사용 2010.02.10
	    					 */
	    					if((YmCommonConst.CURR_PROG_CD_COIL_B.equals(sProgCd)||YmCommonConst.CURR_PROG_CD_COIL_C.equals(sProgCd) )
	    							&& !YmCommonConst.STACK_COL_GP_1BDC01.equals(sEquip)){ 
	    						sBay = StringHelper.evl(equipJr.getFieldString("CTS_RELAY_BAY"), "").trim();	
	    					}else{
	    						sBay = StringHelper.evl(equipJr.getFieldString("CARUNLOAD_BAY"), "").trim();	
	    					}
	    				} 
	    				logger.println(LogLevel.DEBUG,this, "목적동=" + sBay);
	    				
	    				int iSeq = dao.updateStockPutLocWithStockId(sStockId,sBay);
	    			}
	    				    			
	    			EJBConnector ejbConn = new EJBConnector("default","JNDICTSSchReg",this);
					isSuccess = (Boolean)ejbConn.trx("callCtsWorkInfo",new Class[]{String.class,String.class,String.class},
																	   new Object[]{sStackColGp,sStockId,"1"});
					
					
					
					/**
					 * 설비Table에서 CTS 권하 skid정보 CTS L2에 전송
					 */ 
					ymCommonDAO dao = ymCommonDAO.getInstance();
					List FrtostlList1 = null;
					String sSaddleName ="";
					String sSaddleUseYn ="";
					String sSaddleUsage ="";
					String sStockID	 	="";
					
					
		 			String trnQueryId 		= "ym.common.dao.selectSkidIniStlNoInfo";
					FrtostlList1 	= dao.getCommonList(trnQueryId, new Object[]{sStackColGp,sStackBedGp,sStackLayerGp});
			
			
					int iSeqCount 	= FrtostlList1.size();

					for(int i=0; i < iSeqCount ; i++){
				
						JDTORecord FrtoSltrec = (JDTORecord)FrtostlList1.get(i); 
						
						sSaddleName		= StringHelper.evl(FrtoSltrec.getFieldString("SADDLE_NAME"), ""); 
		    			sSaddleUseYn	= StringHelper.evl(FrtoSltrec.getFieldString("EQUIP_STAT"), ""); 
		    			sSaddleUsage	= StringHelper.evl(FrtoSltrec.getFieldString("STACK_COL_USAGE_CD"), ""); 
		    			sStockID		= StringHelper.evl(FrtoSltrec.getFieldString("STOCK_ID"), "");
		    			
		    			logger.println(LogLevel.DEBUG,this, i+"sSaddleName	="+ sSaddleName);
		    			logger.println(LogLevel.DEBUG,this, i+"sSaddleUseYn	="+ sSaddleUseYn);
		    			logger.println(LogLevel.DEBUG,this, i+"sSaddleUsage	="+ sSaddleUsage);
		    			logger.println(LogLevel.DEBUG,this, i+"sStockID	="+ sStockID);
		    			
		    			
		    			String sMessage = setCtsACoilMsgInfoPUT(sSaddleName,sSaddleUseYn,sSaddleUsage,"1"+sStockID);
		    			
		    			EJBConnector ejbConnPut = new EJBConnector("default","JNDIYardWrkResReg",this);
		    			isSuccess = (Boolean)ejbConnPut.trx("THHT410send",new Class[]{String.class},new Object[]{ sMessage });	    			    			
					}
				}
			}
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue();
	}
	
       /**
	 * 오퍼레이션명 : 
	 *
	 *	7.	COIL INFO
        *		Next 작업지시 Call
        * 
        * param jDTORecord 	: 전문항목
        * param String		: Wbook_Id
	 *
        * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
	public boolean callCraneSchInfo_Coil(JDTORecord jtR){
		
		Boolean isSuccess = new Boolean(false);
		logger.println(LogLevel.DEBUG,this, "callCraneSchInfo_Coil() 시작=");
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String sTc				= StringHelper.evl(jtR.getFieldString("TC"), "");
			String sYard_Id 		= StringHelper.evl(jtR.getFieldString("Yard_Id"), "");
			String sBay_Gp 			= StringHelper.evl(jtR.getFieldString("Bay_GP"), "");
			String sEquip_Kind 		= StringHelper.evl(jtR.getFieldString("Equip_Kind"), "");
			String sEquip_No 		= StringHelper.evl(jtR.getFieldString("Equip_No"), "");
			String sSch_Code 		= StringHelper.evl(jtR.getFieldString("Sch_Code"), "");
			String sWbookId         = StringHelper.evl(jtR.getFieldString("GROBAL_WBOOK_ID"), "") ;
			
			if(YmCommonConst.TC_THCH570.equals(sTc)||  //A열연 Coil 권하실적
			   YmCommonConst.TC_THCH580.equals(sTc)){  //A열연 Coil 권하이상실적
			   	sTc	= YmCommonConst.TC_THCH520;
			}else if(YmCommonConst.TC_CN1PB06.equals(sTc)||  //B열연 Coil 권하실적
					 YmCommonConst.TC_CN1PB07.equals(sTc)){  //B열연 Coil 권하이상실적
				sTc	= YmCommonConst.TC_CN1PB02;
			}
			
			logger.println(LogLevel.DEBUG,this, "sTc="				+ sTc);
			logger.println(LogLevel.DEBUG,this, "sYard_Id="			+ sYard_Id);
			logger.println(LogLevel.DEBUG,this, "sBay_Gp="			+ sBay_Gp);
			logger.println(LogLevel.DEBUG,this, "sEquip_Kind="		+ sEquip_Kind);
			logger.println(LogLevel.DEBUG,this, "sEquip_No="		+ sEquip_No);
			logger.println(LogLevel.DEBUG,this, "sSch_Code="		+ sSch_Code);
			logger.println(LogLevel.DEBUG,this, "sWbookId="			+ sWbookId);
			
			JDTORecord schRc = null;
    		/*
    		 * 2007.07.04 이정훈
    		 * 권하 시 확장 Conv Sch Call
    		 */
			if(YmCommonConst.NEW_SCH_WORK_KIND_CELO.equals(sSch_Code) &&
			   "C".equals(sBay_Gp))
			{
				JDTORecord wbookRc = dao.getCraneWbookInfo_04(sYard_Id,
						  									  sBay_Gp,
						  									  sSch_Code);
			
				if(wbookRc != null){
				/**
	    		 * 작업예정이 존재하면
	    		 * 해당 작업예정에 대해 스케쥴을 호출한다.
	    		 */
	    		String sWbookID = StringHelper.evl(wbookRc.getFieldString("WBOOK_ID"), "");
  				
				EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
				isSuccess = (Boolean)ejbConn.trx("callCraneSchInfo",new  Class[]{String.class},
																	new Object[]{sWbookID});
				}
			}
			
			/**
    		 * 1. B열연 :lineoff가 존재 하는 경우 우선 순위로 작업
    		 */
			if("3".equals(sYard_Id)&&!"E".equals(sBay_Gp)){
				schRc = dao.getCraneSchInfo(sYard_Id,
											sBay_Gp,
											sEquip_Kind,
											sEquip_No,
											"LINEOFF",
											"");
			}
						
				if(schRc == null){
				/**
	    		 * 1. 같은 작업예약ID[WBOOK_ID]로 묶인 스케쥴이
	    		 *    있는지를 체크한다.
	    		 */
	    		schRc = dao.getCraneSchInfo(sYard_Id,
	    									sBay_Gp,
	    									sEquip_Kind,
	    									sEquip_No,
	    									sSch_Code,
	    									sWbookId);
	    		if(schRc == null){
	    			//sWbookId  = "";
	    			/**
		    		 * 2. 같은 스케쥴코드로 묶인 스케쥴이
		    		 *    있는지를 체크한다.
		    		 *	  연속작업가능 스케쥴코드에만 해당
		    		 */
		    		boolean isConWork = YmCommonUtil.isContinueWork(sSch_Code); 
		    		if(isConWork){
		    			logger.println(LogLevel.DEBUG,this, "같은 스케쥴코드로 묶인 스케쥴 검사 == TRUE"	);
			    		schRc = dao.getCraneSchInfo(sYard_Id,
			    									sBay_Gp,
			    									sEquip_Kind,
			    									sEquip_No,
			    									sSch_Code,
			    									"");
		    		}
		    		
		    		if(schRc == null){
		    			logger.println(LogLevel.DEBUG,this, "해당크레인에 할당된 스케쥴 검사"	+ YmCommonConst.WORK_PROG_STAT_W);
		    			//sSch_Code  = "";
		    			/**
			    		 * 3. 해당크레인에 할당된 스케쥴이
			    		 *    있는지를 체크한다.
			    		 */
			    		schRc = dao.getCraneSchInfo(sYard_Id,
			    									sBay_Gp,
			    									sEquip_Kind,
			    									sEquip_No,
			    									"",
			    									"");
			    	}							
		    	}
			    
		    	if(schRc == null){
		    		logger.println(LogLevel.DEBUG,this, "크레인 상태 변경"	+ YmCommonConst.WORK_PROG_STAT_W);
		    		/**
		    		 * 스케쥴이 존재하지 않으면
		    		 * 크레인 상태를 'W'로 
		    		 * 상차스케쥴코드를 '' 로 셋팅한다.
		    		 */
		    		int iReq  = dao.updateSubCraneEquipStat(sYard_Id,
		    												sBay_Gp,
		    												sEquip_Kind,
		    												sEquip_No,
		    												YmCommonConst.WORK_PROG_STAT_W,
		    												"");
		    	
		    	}
			}
	    	
	    	
	    	{
	    		/**
	    		 * 작업지시를 요구한다.
	    		 */
	    		logger.println(LogLevel.DEBUG,this, "callCraneSchInfo_Coil() 크레인 작업지시를 요구");
		    	EJBConnector ejbConn = new EJBConnector("default","JNDICWrkOrdReg",this);
				isSuccess = (Boolean)ejbConn.trx("callCraneSchInfo",new  Class[]{String.class,
																				 String.class,
																				 String.class,
																				 String.class,
																				 String.class,
																				 String.class,
																				 String.class},
																	  new Object[]{sTc,
																	  			   sYard_Id,
																	  			   sBay_Gp,
																	  			   sEquip_Kind,
																	  			   sEquip_No,
																	  			   sSch_Code,
		    																	   sWbookId});
			}
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue();
	}
       
	/**
	 * 오퍼레이션명 : 
	 *
	 *	9.	COIL INFO
        *		야드맵 정보송신 Call
        *
        * param jDTORecord 	: 전문항목
        *
        * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
	public boolean callMapInfo_Coil(JDTORecord jtR){
		
		Boolean isSuccess = new Boolean(false);
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String sYard_Id 	 = StringHelper.evl(jtR.getFieldString("Yard_Id"), "");
			
			if(YmCommonConst.YD_GP_3.equals(sYard_Id)){ 
				
				String sPut_Position = StringHelper.evl(jtR.getFieldString("Put_Position"), "");
				String sMsg 		 = YmCommonUtil.setBCoilMapMsgInfo(sPut_Position);	
					
				logger.println(LogLevel.DEBUG,this, "====야드맵 작업송신 CALL====");
				
				EJBConnector ejbConn = new EJBConnector("default","JNDICraneStatusReg",this);
				isSuccess = (Boolean)ejbConn.trx("bcyYdMapInfo",new  Class[]{String.class},
																new Object[]{sMsg});
			}
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue();
	}
	
	
	
	/**
	 * 오퍼레이션명 : 
	 *
	 *	9.	COIL INFO
        *		HFL결속대 정보송신 Call(YDHRJ003, YDHRJ004)
        *
        * param jDTORecord 	: 전문항목
        *
        * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
	public boolean callHFLInfo_Coil(JDTORecord jtR){
		YdDelegate      ydDelegate      = new YdDelegate();
		Boolean isSuccess = new Boolean(false);
		JDTORecord recInTemp	=null;
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String sYard_Id 	 = StringHelper.evl(jtR.getFieldString("Yard_Id"), "");
			String sSchCode 	 = StringHelper.evl(jtR.getFieldString("Sch_Code"), "");
			String sStockId		 = StringHelper.evl(jtR.getFieldString("Coil_No"), "").trim();
			String sPut_Position = StringHelper.evl(jtR.getFieldString("Put_Position"), "");
			
			if(YmCommonConst.YD_GP_3.equals(sYard_Id)){

				if(YmCommonConst.NEW_SCH_WORK_KIND_CFSI.equals(sSchCode) && 
						sPut_Position.substring(2,4).equals("HS") //HFL결속대 설비 위치인 경우 
				   ){
				//HFL 결속대 보급실적	
					
					recInTemp = JDTORecordFactory.getInstance().create();
	    			recInTemp.setField("MSG_ID",        "YDHRJ001"); 							//열연조업 L3 정정보급완료 실적  전문코드
	    			recInTemp.setField("STL_NO",     	sStockId);								//재료번호
	    			recInTemp.setField("YD_EQP_ID",     sPut_Position.substring(0,6));			//야드설비id 
	    			recInTemp.setField("YD_STK_BED_NO", sPut_Position.substring(6,8));			//야드적치베드번호
	    			recInTemp.setField("YD_DN_CMPL_DT", YmCommonUtil.getTcDate("yyyyMMddHHmmss"));	//야드권하완료일시
	    			recInTemp.setField("TREAT_GP", "1");	
	    			
	    			ydDelegate.sendMsg(recInTemp);

					logger.println(LogLevel.DEBUG,this, "열연조업 L3 HFL 결속대 보급완료 실적 전송 송신 완료");					
					
					//품질 열연정정입측보급실적----------------------------------------------
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("MSG_ID",      	"YDQMJ002"); 							 
					recInTemp.setField("STL_NO",     	sStockId);	  			//재료번호				
					ydDelegate.sendMsg(recInTemp);
			 
					logger.println(LogLevel.DEBUG,this, "품질 L3 열연정정입측보급실적 전송 송신 완료6"); 
					//-------------------------------------------------------------------
				}				
			}


		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue();
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 자동이적 권하이상 처리 
	 * => 메인 메소드
        *
        * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */      
	public boolean setCranePutCheckInfo_Slab(JDTORecord jtR,
									      String sFuncGbn){
		
		boolean isSuccess = false;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String sEquipGp	= "";
			String sMessage 	= "";
			
			String sStackColGp        = "";
			String sStackBedGp	= "";
		
			String sTc			= StringHelper.evl(jtR.getFieldString("TC"), "");
			String sYard_Id 		= StringHelper.evl(jtR.getFieldString("Yard_Id"), "");
			String sBay_Gp 		= StringHelper.evl(jtR.getFieldString("Bay_GP"), "");
			String sEquip_Kind 		= StringHelper.evl(jtR.getFieldString("Equip_Kind"), "");
			String sEquip_No 		= StringHelper.evl(jtR.getFieldString("Equip_No"), "");
			String sStockId         	= StringHelper.evl(jtR.getFieldString("Slab_No"), "").trim();
			String sSch_Code 		= StringHelper.evl(jtR.getFieldString("Sch_Code"), "");
			String sPutRLoc		= StringHelper.evl(jtR.getFieldString("Put_Position"), "0");
			String sPutRXLoc		= StringHelper.evl(jtR.getFieldString("Put_X_Position"), "0");
			String sPutRYLoc		= StringHelper.evl(jtR.getFieldString("Put_Y_Position"), "0");
			sEquipGp				= sYard_Id+sBay_Gp+sEquip_Kind+sEquip_No;
			
			logger.println(LogLevel.DEBUG,this, "sTc="				+ sTc);
			logger.println(LogLevel.DEBUG,this, "sYard_Id="			+ sYard_Id);
			logger.println(LogLevel.DEBUG,this, "sBay_Gp="			+ sBay_Gp);
			logger.println(LogLevel.DEBUG,this, "sEquip_Kind="		+ sEquip_Kind);
			logger.println(LogLevel.DEBUG,this, "sEquip_No="		+ sEquip_No);
			logger.println(LogLevel.DEBUG,this, "sStockId="			+ sStockId+"=");
			logger.println(LogLevel.DEBUG,this, "sSch_Code="		+ sSch_Code);
			logger.println(LogLevel.DEBUG,this, "sPut_Position="		+ sPutRLoc);
			logger.println(LogLevel.DEBUG,this, "sPut_X_Position="	+ sPutRXLoc);
			logger.println(LogLevel.DEBUG,this, "sPut_Y_Position="	+ sPutRYLoc);
				
    	 		if((
    	 		  YmCommonConst.CRANE_FUNC_N.equals(sFuncGbn)|| 	//차상국 전문처리
    	 		  YmCommonConst.CRANE_FUNC_L.equals(sFuncGbn)	//지상국 전문처리 
    	 		  )&&( YmCommonConst.TC_CM1PB07.equals(sTc)		//B열연 Slab 권하이상실적
    	 		   || YmCommonConst.TC_HM1PB07.equals(sTc)	  	  	//A_A열연 Slab 권하이상실적(MCH)
			   || YmCommonConst.TC_HM1PB57.equals(sTc))){	  	//A_B열연 Slab 권하이상실적(MCH)
    	 			
    	 			String sSchId1 	= "";	
				String sSchId2 	= "";
				String sPut1 		= "";	
				String sPut2 		= "";
				String sStockId1 	= "";	
				String sStockId2 	= "";
				
				JDTORecord schRc = dao.getSchIdInfo( sYard_Id,
							    					 sBay_Gp,
							    					 sEquip_Kind,
							    					 sEquip_No,
							    					 sStockId,
							    					 sSch_Code
				   									 );
			    	if(schRc == null){
			    		sMessage	= "스케쥴 정보가 존재하지 않습니다.";
					sendMessageToSlabCrane(sEquipGp,sMessage);
			 		logger.println(LogLevel.DEBUG,this, "=SLAB 권하이상 => 스케쥴 정보가 존재하지 않습니다");
			 		return true;
		    		}
	    		
		    		sSchId1   	= StringHelper.evl(schRc.getFieldString("SCH_ID"), "");
		    		sPut1       	= StringHelper.evl(schRc.getFieldString("CRANE_WORD_PUT_LOC"), "");
		    		sStockId1  	= StringHelper.evl(schRc.getFieldString("STOCK_ID"), "");
		    		
    	 		/*
				 *	1. 위치정보 가져오기
				 */
				List MapInfo	= dao.getXYLogicalInfo(sYard_Id+sBay_Gp,
											      sPutRXLoc,
											      sPutRYLoc);
				
				logger.println(LogLevel.DEBUG,this, "=SLAB 권하이상 =>MapInfo Size="+MapInfo.size());
				 
				if(MapInfo.size() == 0 ){
					
					sMessage	= "논리적위치정보가 존재하지 않습니다.";
					sendMessageToSlabCrane(sSchId1,sMessage);
					
					logger.println(LogLevel.DEBUG,this, "=SLAB 권하이상 => 위치정보 체크 오류");
					return true;
				}   	
				if(MapInfo.size() > 1){
					
					sMessage	= "논리적위치정보가 2개 이상 존재합니다.";
					sendMessageToSlabCrane(sSchId1,sMessage);
					
					logger.println(LogLevel.DEBUG,this, "=SLAB 권하이상 => 위치정보 체크 오류");
					return true;
				}   	
				
				for(int inx = 0; inx < MapInfo.size() ; inx++)
				{
				 	JDTORecord infoV = (JDTORecord)MapInfo.get(inx);
				 	
				 	sStackColGp 	= StringHelper.evl(infoV.getFieldString("STACK_COL_GP"), "");	 														 
				 	sStackBedGp 	= StringHelper.evl(infoV.getFieldString("STACK_BED_GP"), "");	 
				 	
				 	logger.println(LogLevel.DEBUG,this, "=SLAB 권하이상 =>sStackColGp	="+sStackColGp);
				 	logger.println(LogLevel.DEBUG,this, "=SLAB 권하이상 =>sStackBedGp	="+sStackBedGp);	
				 }
				 
				 /*
				  *	2. To위치 정보 체크
				  */
				{ 
					List infoL = dao.getStackLayerInfoWithBed(sStackColGp,
													    sStackBedGp,
													    "L");
					
					if(infoL.size() > 0)
					{	
						JDTORecord infoV 	   = null;
						
					 	String sStackLayerStat	= "";
					 	
						//for(int inx = 0; inx < infoL.size() ; inx++)
						for(int inx = 0; inx < 1 ; inx++)
						{
						 	infoV = (JDTORecord)infoL.get(inx);
						 	
						 	sStackLayerStat 	= StringHelper.evl(infoV.getFieldString("STACK_LAYER_STAT"), "");	 														 
						 	
						 	if(YmCommonConst.STACK_LAYER_STAT_U.equals(sStackLayerStat)){
						 		
						 		sMessage	= "Slab정보가 권상대기(U) 상태입니다..";
								sendMessageToSlabCrane(sSchId1,sMessage);
							
						 		logger.println(LogLevel.DEBUG,this, "=SLAB 권하이상 => 상단 Slab정보 U");
						 		return true;
						 	}else if(YmCommonConst.STACK_LAYER_STAT_P.equals(sStackLayerStat)){
						 		
						 		sMessage	= "Slab정보가 권하대기(P) 상태입니다..";
								sendMessageToSlabCrane(sSchId1,sMessage);
						 		logger.println(LogLevel.DEBUG,this, "=SLAB 권하이상 => 상단 Slab정보 P");
								return true;
						 	}else if(YmCommonConst.STACK_LAYER_STAT_S.equals(sStackLayerStat)){
						 		
						 		sMessage	= "Slab정보가 작업예약등록(S) 상태입니다..";
								sendMessageToSlabCrane(sSchId1,sMessage);
						 		logger.println(LogLevel.DEBUG,this, "=SLAB 권하이상 => 상단 Slab정보 S");
						 		return true;
						 	}
						 }
					 }		
				}
				
				/*
				  *	3. 구 To위치정보 초기화 및 신 To 위치 정보 셋팅
				  */
				
				{ 
					/*
					 * Crane 설비테이블에 셋팅된 SCH_ID 값을 가져온다.
					 */		
			    	
				    	JDTORecord craneV = dao.getEquipInfoWithEquipNo(sYard_Id,
				    											    sEquip_No);
				    	if(craneV == null){
				    		sMessage	= "크레인정보가 존재하지 않습니다.";
						sendMessageToSlabCrane(sEquipGp,sMessage);
				 		logger.println(LogLevel.DEBUG,this, "=SLAB 권하이상 => 크레인정보가 존재하지 않습니다");
				 		return true;
			    		}
		    		
				    	String sTWprogStat = StringHelper.evl(craneV.getFieldString("WPROG_STAT"), "");
				    	String sTWbookId   = StringHelper.evl(craneV.getFieldString("WBOOK_ID"), "");
				    	
				    	logger.println(LogLevel.DEBUG,this, "sTWprogStat="	+ sTWprogStat);
				    	logger.println(LogLevel.DEBUG,this, "sTWbookId="	+ sTWbookId);
				    	
			    		/*
					 *  전문을 통해 받은 스케쥴ID와 해당 크레인에 셋팅된 스케쥴ID가 같은지를 체크한다.
					 *  같은경우에만 권하처리를 한다.
					 * - 현재 작업진행상태가 '3'(PUT지시)
					 * - 스케쥴ID가 동일한 경우
					 */	
				    	if(!YmCommonConst.WORK_PROG_STAT_3.equals(sTWprogStat)||
				    	   !sSchId1.equals(sTWbookId)){
				    	   	
				    	   	sMessage	= "스케쥴ID나 작업진행상태에러.";
						sendMessageToSlabCrane(sEquipGp,sMessage);
				 		logger.println(LogLevel.DEBUG,this, "=SLAB 권하이상 => 스케쥴ID나 작업진행상태에러");
				 		return true;
				 		
				    	}
			    	
			    		/*
					 * 2매 작업 대상 정보 셋팅
					 */		
			    		int    iGripCount 		=  1; //1매 작업
			    		
			    		JDTORecord stockV = dao.getSlabGripInfo_02(sYard_Id,
									    					    sBay_Gp,
									    					    sEquip_Kind,
									    					    sEquip_No,
									    					    sStockId,
									    					    sSch_Code );
				    	if(stockV != null){
				    		iGripCount 	 = 2; //2매 작업
				    		sSchId2 		 = StringHelper.evl(stockV.getFieldString("SCH_ID"), "");
				    		sPut2       	 = StringHelper.evl(stockV.getFieldString("CRANE_WORD_PUT_LOC"), "");
				    		sStockId2  	 = StringHelper.evl(stockV.getFieldString("STOCK_ID"), "");
					}
					
					logger.println(LogLevel.DEBUG,this, "=SLAB 권하이상 =>sSchId1	="+sSchId1);
					logger.println(LogLevel.DEBUG,this, "=SLAB 권하이상 =>sPut1		="+sPut1);
					logger.println(LogLevel.DEBUG,this, "=SLAB 권하이상 =>sStockId1	="+sStockId1);
					logger.println(LogLevel.DEBUG,this, "=SLAB 권하이상 =>sSchId2	="+sSchId2);
					logger.println(LogLevel.DEBUG,this, "=SLAB 권하이상 =>sPut2		="+sPut2);
					logger.println(LogLevel.DEBUG,this, "=SLAB 권하이상 =>sStockId2	="+sStockId2);
					/*
					 *	기존 TO위치 초기화
					 */	
					if(!"".equals(sPut1)){
						setCranePutCheckInfo_Slab_01(sPut1,sStockId1);
					}
					if(!"".equals(sPut2)){
						setCranePutCheckInfo_Slab_01(sPut2,sStockId2);
					}
					
					/*
					 *	신 TO위치 셋팅
					 */
					List infoL = dao.getStackLayerInfoWithBed(sStackColGp,
													    sStackBedGp,
													    "E");
					
					if(infoL.size() > 0)
					{	
						int iSeq = 0;
						JDTORecord infoV 	   = null;
						
					 	String sTStackColGp	= "";
					 	String sTStackBedGp	= "";
					 	String sTStackLayerGp	= "";
					 	
						for(int inx = 0; inx < iGripCount ; inx++)
						{
						 	infoV = (JDTORecord)infoL.get(inx);
						 	
						 	sTStackColGp 	= StringHelper.evl(infoV.getFieldString("STACK_COL_GP"), "");	 														 
						 	sTStackBedGp 	= StringHelper.evl(infoV.getFieldString("STACK_BED_GP"), "");	 														 
						 	sTStackLayerGp 	= StringHelper.evl(infoV.getFieldString("STACK_LAYER_GP"), "");	 														 
						 	
						 	if(iGripCount == 1){
						 		
						 		setCranePutCheckInfo_Slab_02(   sSchId1,
												     	      	        sStockId1,
												     	      	        YmCommonConst.STACK_LAYER_STAT_P, 
												     	      	        sTStackColGp,
												     	      	        sTStackBedGp, 
												     	      	        sTStackLayerGp);
    								     				  			     
						 	}else{
								
								if(inx == 0){
									
									setCranePutCheckInfo_Slab_02(   sSchId2,
													     	      	        sStockId2,
													     	      	        YmCommonConst.STACK_LAYER_STAT_P, 
													     	      	        sTStackColGp,
													     	      	        sTStackBedGp, 
													     	      	        sTStackLayerGp);
								}else{
									
									setCranePutCheckInfo_Slab_02(   sSchId1,
													     	      	        sStockId1,
													     	      	        YmCommonConst.STACK_LAYER_STAT_P, 
													     	      	        sTStackColGp,
													     	      	        sTStackBedGp, 
													     	      	        sTStackLayerGp);
								}
							}
						 	
						}
					 }		
				}
				
				/*
				  *	4. 재 작업지시 송신
				  */
				{
			    		Boolean isTemp  = new Boolean(false);
			    		
			    		EJBConnector ejbConn = new EJBConnector("default","JNDICWrkOrdReg",this);
		    		
					isTemp  = (Boolean)ejbConn.trx("callBSlabCraneMsgInfo",new  Class[]{String.class},
																		   new Object[]{sSchId1});
					logger.println(LogLevel.DEBUG,this, "=SLAB 권하이상 => 재작업지시");
				}
				
				return true;																			  		
	    		}
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess;
	}
	/**
	 * 자동이적 권하이상 처리 
	 * => 메세지를 송신한다.
	 */
	private void sendMessageToSlabCrane(String sEquipGp,
									 String sMessage){
		try{	
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return ;
			}
			    	
	    		Boolean isTemp  = new Boolean(false);
	    		
	    		EJBConnector ejbConn = new EJBConnector("default","JNDICWrkOrdReg",this);
	    		
			isTemp  = (Boolean)ejbConn.trx("callBSlabCraneMsgInfo",new  Class[]{String.class,
																	     String.class},
											                           new Object[]{sEquipGp,
																  	      sMessage});
		}catch(Exception e){}
	}		
	/**
	 * 자동이적 권하이상 처리 
	 * => 구 권하위치를 Clear한다.
	 */	
	private void setCranePutCheckInfo_Slab_01(String sPutLoc,
							     	      	        String sStockId)
	{
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return ;
		}
		
		int iSeq = 0;
						
		String sStackColGp   	= sPutLoc.substring(0, 6);
		String sStackBedGp   	= sPutLoc.substring(6, 8);
		String sStackLayerGp 	= sPutLoc.substring(8,10);
	
		String sUpUsageCd 	= YmCommonUtil.getStackColInfoWithPk(sStackColGp);
	
		if(YmCommonConst.STACK_COL_USAGE_CD_XX.equals(sUpUsageCd)	||// SLAB 비상적치위치
       	   YmCommonConst.STACK_COL_USAGE_CD_SE.equals(sUpUsageCd)	||// SLAB Scafing 입측
	   	   YmCommonConst.STACK_COL_USAGE_CD_SD.equals(sUpUsageCd)	){// SLAB Scafing 출측
	   		
			iSeq = YmCommonDB.deleteConveyorInfo(sStackColGp,
			 			  			 		 sStockId);
			if(iSeq < 0){
				logger.println(LogLevel.DEBUG,this, "콘베이어 권상처리 : 적치단 삭제 FAIL");
			}	
		
		}else{ 
		
    			iSeq = dao.updateCraneStackLayerStat(sStackColGp,
	    										 sStackBedGp,
	    										 sStackLayerGp,
	    										 "",
	    										 YmCommonConst.STACK_LAYER_STAT_E);
   
		    	iSeq = YmCommonDB.setSlabUpperState_V(sStackColGp,
				    							      sStackBedGp,
				    							      sStackLayerGp);
		}    
	}
	
	/**
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * 자동이적 권하이상 처리 
	 * => 구 권하위치를 Clear한다.
	 *  @ejb.transaction type="RequiresNew"
	 */	
	public boolean setCranePutCheckInfo_Coil_01(String sPutLoc,
							     	      	  String sStockId){
	boolean isSuccess = false;
	try{
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		int iSeq = 0;
						
		String sStackColGp   	= sPutLoc.substring(0, 6);
		String sStackBedGp   	= sPutLoc.substring(6, 8);
		String sStackLayerGp 	= sPutLoc.substring(8,10);
	
		String sUpUsageCd 	= YmCommonUtil.getStackColInfoWithPk(sStackColGp);
	
		if(YmCommonConst.STACK_COL_USAGE_CD_XX.equals(sUpUsageCd)	||// SLAB 비상적치위치
       	   YmCommonConst.STACK_COL_USAGE_CD_SE.equals(sUpUsageCd)	||// SLAB Scafing 입측
	   	   YmCommonConst.STACK_COL_USAGE_CD_SD.equals(sUpUsageCd)	){// SLAB Scafing 출측
	   		
			iSeq = YmCommonDB.deleteConveyorInfo(sStackColGp,
			 			  			 		 sStockId);
			if(iSeq < 0){
				logger.println(LogLevel.DEBUG,this, "콘베이어 권상처리 : 적치단 삭제 FAIL");
			}	
		
		}else{ 
			logger.println(LogLevel.DEBUG,this, "setCranePutCheckInfo_Coil_01");
    			iSeq = dao.updateCraneStackLayerStat_isang(sStackColGp,
	    										 sStackBedGp,
	    										 sStackLayerGp,
	    										 sStockId,
	    										 YmCommonConst.STACK_LAYER_STAT_E);
    			logger.println(LogLevel.DEBUG,this, "setCranePutCheckInfo_Coil_02");
		    	iSeq = YmCommonDB.setCoilUpperState_V(sStackColGp,
				    							      sStackBedGp,
				    							      sStackLayerGp);
		}
		
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess;
	}
	
	/**
	 * 자동이적 권하이상 처리 
	 * => 신 권하위치를 셋팅한다.
	 */	
	private void setCranePutCheckInfo_Slab_02(String sSchId,
							     	      	        String sStockId,
							     	      	        String sStackLayerStat,
							     	      	        String sStackColGp,
							     	      	        String sStackBedGp,
							     	      	        String sStackLayerGp)
	{
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
		int iSeq = 0;
		
		ymCommonDAO dao1 = ymCommonDAO.getInstance();
						
		dao1.modifyStockStatOfLayer(sStockId, 
								sStackLayerStat, 
								sStackColGp, 
								sStackBedGp, 
								sStackLayerGp);
								
		iSeq = YmCommonDB.setSlabUpperState_E(sStackColGp, 
											sStackBedGp, 
											sStackLayerGp);
		
		iSeq = dao.updatePutLocInfoWithSchId(sSchId,
		     				  			     	       sStackColGp+sStackBedGp+sStackLayerGp);
	}
	/**
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * 자동이적 권하이상 처리 
	 * => 신 권하위치를 셋팅한다.
	 *  @ejb.transaction type="RequiresNew"
	 */	
	public boolean setCranePutCheckInfo_Coil_02(String sSchId,
							     	      	        String sStockId,
							     	      	        String sStackLayerStat,
							     	      	        String sStackColGp,
							     	      	        String sStackBedGp,
							     	      	        String sStackLayerGp)
	{
		boolean isSuccess = false;
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			int iSeq = 0;
			
			ymCommonDAO dao1 = ymCommonDAO.getInstance();
							
			dao1.modifyStockStatOfLayer(sStockId, 
									sStackLayerStat, 
									sStackColGp, 
									sStackBedGp, 
									sStackLayerGp);
									
			if(YmCommonConst.STACK_LAYER_GP_01.equals(sStackLayerGp)){
				
				iSeq = YmCommonDB.setCoilUpperState_E(sStackColGp, 
												sStackBedGp, 
												sStackLayerGp);
			}
			logger.println(LogLevel.DEBUG,this, "setCranePutCheckInfo_Coil_01  " + sStackColGp+sStackBedGp+sStackLayerGp);
			iSeq = dao.updatePutLocInfoWithSchId_01(sSchId, sStackColGp+sStackBedGp+sStackLayerGp);
			iSeq = dao.updatePutLocInfoWbookWithSchId(sSchId,
		     	       sStackColGp+sStackBedGp+sStackLayerGp);
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
        * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */      
	public boolean setCranePutCheckInfo_Slab_BackUp(JDTORecord jtR,
									      		     String sFuncGbn){
		
		boolean isSuccess = false;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String sTc			= StringHelper.evl(jtR.getFieldString("TC"), "");
			String sYard_Id 		= StringHelper.evl(jtR.getFieldString("Yard_Id"), "");
			String sBay_Gp 		= StringHelper.evl(jtR.getFieldString("Bay_GP"), "");
			String sEquip_Kind 		= StringHelper.evl(jtR.getFieldString("Equip_Kind"), "");
			String sEquip_No 		= StringHelper.evl(jtR.getFieldString("Equip_No"), "");
			String sStockId         	= StringHelper.evl(jtR.getFieldString("Slab_No"), "").trim();
			String sSch_Code 		= StringHelper.evl(jtR.getFieldString("Sch_Code"), "");
			String sPutRLoc		= StringHelper.evl(jtR.getFieldString("Put_Position"), "0");
			String sPutRXLoc		= StringHelper.evl(jtR.getFieldString("Put_X_Position"), "0");
			String sPutRYLoc		= StringHelper.evl(jtR.getFieldString("Put_Y_Position"), "0");
			
			logger.println(LogLevel.DEBUG,this, "sTc="				+ sTc);
			logger.println(LogLevel.DEBUG,this, "sYard_Id="			+ sYard_Id);
			logger.println(LogLevel.DEBUG,this, "sBay_Gp="			+ sBay_Gp);
			logger.println(LogLevel.DEBUG,this, "sEquip_Kind="		+ sEquip_Kind);
			logger.println(LogLevel.DEBUG,this, "sEquip_No="		+ sEquip_No);
			logger.println(LogLevel.DEBUG,this, "sStockId="			+ sStockId+"=");
			logger.println(LogLevel.DEBUG,this, "sSch_Code="		+ sSch_Code);
			logger.println(LogLevel.DEBUG,this, "sPut_Position="		+ sPutRLoc);
			logger.println(LogLevel.DEBUG,this, "sPut_X_Position="	+ sPutRXLoc);
			logger.println(LogLevel.DEBUG,this, "sPut_Y_Position="	+ sPutRYLoc);
				
    	 		if((
    	 		  YmCommonConst.CRANE_FUNC_N.equals(sFuncGbn)|| 	//차상국 전문처리
    	 		  YmCommonConst.CRANE_FUNC_L.equals(sFuncGbn)	//지상국 전문처리 
    	 		  )&&( YmCommonConst.TC_CM1PB07.equals(sTc)		//B열연 Slab 권하이상실적
    	 		   || YmCommonConst.TC_HM1PB07.equals(sTc)	  	  //A_A열연 Slab 권하이상실적(MCH)
			   || YmCommonConst.TC_HM1PB57.equals(sTc))){	  //A_B열연 Slab 권하이상실적(MCH)
    	 			
    	 			String sPutSXLoc	= "";
				String sPutSYLoc	= "";
				String sPutXLeft 	= "";
				String sPutXRight 	= "";
				String sPutYUp 		= "";
				String sPutYDown 	= ""; 
				
				JDTORecord rulePutX = dao.getStackRuleInfo_002(sPutRLoc.substring(0, 6),
															   YmCommonConst.STACK_RULE_CD_XCD);
				if(rulePutX != null){
					sPutXLeft 	= StringHelper.evl(rulePutX.getFieldString("STACK_RULE_MIN"), "0");
					sPutXRight 	= StringHelper.evl(rulePutX.getFieldString("STACK_RULE_MAX"), "0");
				}
				
				JDTORecord rulePutY = dao.getStackRuleInfo_002(sPutRLoc.substring(0, 6),
															   YmCommonConst.STACK_RULE_CD_YCD);
				if(rulePutY != null){
					sPutYUp 	= StringHelper.evl(rulePutY.getFieldString("STACK_RULE_MAX"), "0");
					sPutYDown 	= StringHelper.evl(rulePutY.getFieldString("STACK_RULE_MIN"), "0");
				}
				
				if(sPutRLoc.length() == 10){
		    		JDTORecord putR = dao.getStackLayerInfoWithPk(sPutRLoc.substring(0, 6),
		    													  sPutRLoc.substring(6, 8),
																  sPutRLoc.substring(8,10));
			    	if(putR != null){
			    		sPutSXLoc = StringHelper.evl(putR.getFieldString("STACK_LAYER_X_AXIS"), "0");					
						sPutSYLoc = StringHelper.evl(putR.getFieldString("STACK_LAYER_Y_AXIS"), "0");
					}else{
						/*
		    			 * 설비정보는 권상시 적치단정보를 삭제하기 때문에 좌표정보를 가져올 수 없다.
		    			 * 따라서, 적치열정보를 가져온다.
		    			 */
						JDTORecord putSubR = dao.getStackColInfoWithPk(sPutRLoc.substring(0, 6));
						if(putSubR != null){
							sPutSXLoc = StringHelper.evl(putSubR.getFieldString("STACK_COL_RULE_X_AXIS"), "0");					
							sPutSYLoc = StringHelper.evl(putSubR.getFieldString("STACK_COL_RULE_Y_AXIS"), "0");
						}
		    		}
		    	}
		    	
		    	
		    	///////////////////////////////////////////////////////////////
		    	////////////////오차점검 체크로직//////////////////////////////
				
		    	long lPutRXLoc	= Long.parseLong(sPutRXLoc);
			long lPutSXLoc	= Long.parseLong(sPutSXLoc);
			long lPutXLeft 	= Long.parseLong(sPutXLeft);
			long lPutXRight = Long.parseLong(sPutXRight);
				
			long lPutRYLoc	= Long.parseLong(sPutRYLoc);
		    	long lPutSYLoc	= Long.parseLong(sPutSYLoc);
			long lPutYUp 	= Long.parseLong(sPutYUp);
			long lPutYDown 	= Long.parseLong(sPutYDown);
		    	
		    	logger.println(LogLevel.DEBUG,this, "=lPutRXLoc	=>"+lPutRXLoc);
		    	logger.println(LogLevel.DEBUG,this, "=lPutSXLoc	=>"+lPutSXLoc);
		    	logger.println(LogLevel.DEBUG,this, "=lPutXLeft	=>"+lPutXLeft);
		    	logger.println(LogLevel.DEBUG,this, "=lPutXRight=>"+lPutXRight);
		    	logger.println(LogLevel.DEBUG,this, "=lPutRYLoc	=>"+lPutRYLoc);
		    	logger.println(LogLevel.DEBUG,this, "=lPutSYLoc	=>"+lPutSYLoc);
		    	logger.println(LogLevel.DEBUG,this, "=lPutYUp	=>"+lPutYUp);
		    	logger.println(LogLevel.DEBUG,this, "=lPutYDown	=>"+lPutYDown);
		    	
		    	if(
		    	   (
			    	   (lPutSXLoc - lPutXLeft)  <= lPutRXLoc 
			    		&&
			    	   (lPutSXLoc + lPutXRight) >= lPutRXLoc 
		    		)
		    		&&
		    		(
			    	   (lPutSYLoc + lPutYUp)   >= lPutRYLoc 
			    		&&
			    	   (lPutSYLoc - lPutYDown) <= lPutRYLoc 
		    		)
		    	){
		    		isSuccess = false;		
		    		logger.println(LogLevel.DEBUG,this, "=권하이상=> 오차점검 OK");
		    	}else{
		    		isSuccess = true;		
		    		logger.println(LogLevel.DEBUG,this, "=권하이상=> 오차점검 NG => 작업 재지시 CALL");
		    	}	
	    	}
	    	
	    	//작업재지시
	    	if(isSuccess){
	    		
	    		JDTORecord schRc = null;
		    	
		    	schRc = dao.getSchIdInfo(sYard_Id,
				    					 sBay_Gp,
				    					 sEquip_Kind,
				    					 sEquip_No,
				    					 sStockId,
				    					 sSch_Code
	   									 );
		    	if(schRc == null){
		    		logger.println(LogLevel.DEBUG,this, "권하이상..");
		    	   	throw new EJBServiceException("=권하이상=> 스케쥴정보 존재안함.");
	    		}
	    		
		    	String sSchId   = StringHelper.evl(schRc.getFieldString("SCH_ID"), "");
		    	String sMsg   	= "입력위치와 좌표값이 일치하지 않습니다.";
		    	
	    		Boolean isTemp  = new Boolean(false);
	    		
	    		EJBConnector ejbConn = new EJBConnector("default","JNDICWrkOrdReg",this);
	    		
				isTemp  = (Boolean)ejbConn.trx("callBSlabCraneMsgInfo",new  Class[]{String.class,
																			  		String.class},
																	   new Object[]{sSchId,
																			  		sMsg});
				logger.println(LogLevel.DEBUG,this, "=권하이상=> B열연 SLAB 작업 재지시");
			}																			  		
	    	///////////////////////////////////////////////////////////////
	    	///////////////////////////////////////////////////////////////
    	 				
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
	 *	1.	SLAB INFO
        *		Crane Put 정상실적에 관련된 상태정보를 등록 및 수정한다.
        * 
        * param jDTORecord 	: 전문항목
        * param String	 	: 실적처리방법
	 *
        * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 * @ejb.transaction type="RequiresNew"
	 */      
	public boolean setCranePutRtInfo_Slab(JDTORecord jtR,
									 	  String sFuncGbn){
		
		boolean isSuccess = false;
		int iReq = -1;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String sTc				= StringHelper.evl(jtR.getFieldString("TC"), "");
			String sYard_Id 		= StringHelper.evl(jtR.getFieldString("Yard_Id"), "");
			String sBay_Gp 			= StringHelper.evl(jtR.getFieldString("Bay_GP"), "");
			String sEquip_Kind 		= StringHelper.evl(jtR.getFieldString("Equip_Kind"), "");
			String sEquip_No 		= StringHelper.evl(jtR.getFieldString("Equip_No"), "");
			String sStockId         = StringHelper.evl(jtR.getFieldString("Slab_No"), "").trim();
			String sSch_Code 		= StringHelper.evl(jtR.getFieldString("Sch_Code"), "");
			String sWork_Id 		= YmCommonUtil.getCurDataWithLegacy(
									  StringHelper.evl(jtR.getFieldString("Work_Id"), ""));
			String sPut_Position 	= StringHelper.evl(jtR.getFieldString("Put_Position"), "");
			String sPut_X_Position	= StringHelper.evl(jtR.getFieldString("Put_X_Position"), "");
			String sPut_Y_Position	= StringHelper.evl(jtR.getFieldString("Put_Y_Position"), "");
			
			logger.println(LogLevel.DEBUG,this, "sTc="				+ sTc);
			logger.println(LogLevel.DEBUG,this, "sYard_Id="			+ sYard_Id);
			logger.println(LogLevel.DEBUG,this, "sBay_Gp="			+ sBay_Gp);
			logger.println(LogLevel.DEBUG,this, "sEquip_Kind="		+ sEquip_Kind);
			logger.println(LogLevel.DEBUG,this, "sEquip_No="		+ sEquip_No);
			logger.println(LogLevel.DEBUG,this, "sStockId="			+ sStockId+"==");
			logger.println(LogLevel.DEBUG,this, "sSch_Code="		+ sSch_Code);
			logger.println(LogLevel.DEBUG,this, "sWork_Id="			+ sWork_Id);
			logger.println(LogLevel.DEBUG,this, "sPut_Position="	+ sPut_Position);
			logger.println(LogLevel.DEBUG,this, "sPut_X_Position="	+ sPut_X_Position);
			logger.println(LogLevel.DEBUG,this, "sPut_Y_Position="	+ sPut_Y_Position);
			
			if("".equals(sPut_Position)){
				logger.println(LogLevel.DEBUG,this, "권하에러..");
	    	   	logger.println(LogLevel.DEBUG,this, "권하위치정보 존재안함");
	    	   	throw new EJBServiceException("=권하실적=> 권하위치정보 존재안함.");
			}
			
			/*
			 * Crane Put실적 저장품의 적치단정보를 가져온다.
			 */			
			JDTORecord schRc = null;
	    	
	    	schRc = dao.getSchIdInfo(sYard_Id,
			    					 sBay_Gp,
			    					 sEquip_Kind,
			    					 sEquip_No,
			    					 sStockId,
			    					 sSch_Code
   									 );
	    	if(schRc == null){
	    		logger.println(LogLevel.DEBUG,this, "권하에러..");
	    	   	logger.println(LogLevel.DEBUG,this, "스케쥴정보 가져올 수 없슴");
    			throw new EJBServiceException("=권하실적=> 스케쥴정보 존재 안함.");
    		}
    		
	    	String sGrobalSchId   = StringHelper.evl(schRc.getFieldString("SCH_ID"), "");
	    	String sGrobalWbookId = StringHelper.evl(schRc.getFieldString("WBOOK_ID"), "");
	    	/**
	    	 *	메소드 전역변수 셋팅
	    	 */
	    	jtR.setField("GROBAL_WBOOK_ID",sGrobalWbookId);	
	    	
	    	logger.println(LogLevel.DEBUG,this, "sGrobalSchId	="	+ sGrobalSchId);
	    	logger.println(LogLevel.DEBUG,this, "sGrobalWbookId	="	+ sGrobalWbookId);
	    	
	    	/*
			 * Crane 설비테이블에 셋팅된 SCH_ID 값을 가져온다.
			 */		
	    	JDTORecord craneV = null;
	    	
	    	craneV = dao.getEquipInfoWithEquipNo(sYard_Id,
	    										 sEquip_No);
	    	if(craneV == null){
	    		logger.println(LogLevel.DEBUG,this, "권하에러..");
	    	   	logger.println(LogLevel.DEBUG,this, "크레인정보 가져올 수 없슴");
    			throw new EJBServiceException("=권하실적=> 크레인정보 존재 안함.");
    		}
    		
	    	String sTWprogStat = StringHelper.evl(craneV.getFieldString("WPROG_STAT"), "");
	    	String sTWbookId   = StringHelper.evl(craneV.getFieldString("WBOOK_ID"), "");
	    	
	    	logger.println(LogLevel.DEBUG,this, "sTWprogStat="	+ sTWprogStat);
	    	logger.println(LogLevel.DEBUG,this, "sTWbookId="	+ sTWbookId);
	    	
	    	/*
			 * 전문을 통해 받은 스케쥴ID와 해당 크레인에 셋팅된 스케쥴ID가 같은지를 체크한다.
			 * 같은경우에만 권하처리를 한다.
			 * - 현재 작업진행상태가 '3'(PUT지시)
			 * - 스케쥴ID가 동일한 경우
			 */	
	    	if(!YmCommonConst.WORK_PROG_STAT_3.equals(sTWprogStat)||
	    	   !sGrobalSchId.equals(sTWbookId)){
	    	   	
	    	   	logger.println(LogLevel.DEBUG,this, "권하에러..");
	    	   	logger.println(LogLevel.DEBUG,this, "스케쥴ID나 작업진행상태에러..");
	    	   	throw new EJBServiceException("=권하실적=>스케쥴ID나 작업진행상태에러.");
	    	}
	    	
	    	/*
			 * 2매 작업 대상 정보 셋팅
			 */		
			String sInnerStockId 	= "";
	    	String sInnerSchId 		= "";
	    	String sInnerWbookId	= "";
	    	String sInnerPutPositoin= "";
	    	String sInnerCraneLayerGp = "";
	    	
	    	String sDSchId 			= "";
	    	String sDWbookId   		= "";
	    	String sDStockId 		= "";
	    	String sDCraneWordUpLoc = "";
	    	String sDCraneWordPutLoc= "";
		    String sGripYn 			= "";
	    	int    iGripCount 		=  1; //1매 작업
	    	
	    	JDTORecord stockV = null;
	    	
	    	stockV = dao.getSlabGripInfo_02(sYard_Id,
				    					    sBay_Gp,
				    					    sEquip_Kind,
				    					    sEquip_No,
				    					    sStockId,
				    					    sSch_Code
	   									    );
	    	if(stockV != null){
	    		iGripCount 		 = 2; //2매 작업
	    		sDSchId 		 = StringHelper.evl(stockV.getFieldString("SCH_ID"), "");
		    	sDWbookId   	 = StringHelper.evl(stockV.getFieldString("WBOOK_ID"), "");
		    	sDStockId 		 = StringHelper.evl(stockV.getFieldString("STOCK_ID"), "");
		    	sDCraneWordUpLoc = StringHelper.evl(stockV.getFieldString("CRANE_WORD_UP_LOC"), "");
		    	sDCraneWordPutLoc= StringHelper.evl(stockV.getFieldString("CRANE_WORD_PUT_LOC"), "");
		    	
		    	/**
		    	 *	메소드 전역변수 셋팅
		    	 */
		    	jtR.setField("GROBAL_STOCK_ID",sDStockId);	
		    	jtR.setField("GROBAL_LOCATION",sDCraneWordPutLoc);	
				
	    	   	logger.println(LogLevel.DEBUG,this, "===2매 작업 대상 정보===.");
	    	   	logger.println(LogLevel.DEBUG,this, "===iGripCount			="+iGripCount);
	    	   	logger.println(LogLevel.DEBUG,this, "===sDSchId				="+sDSchId);
	    	   	logger.println(LogLevel.DEBUG,this, "===sDWbookId			="+sDWbookId);
	    	   	logger.println(LogLevel.DEBUG,this, "===sDStockId			="+sDStockId);
	    	   	logger.println(LogLevel.DEBUG,this, "===sDCraneWordUpLoc	="+sDCraneWordUpLoc);
	    	   	logger.println(LogLevel.DEBUG,this, "===sDCraneWordPutLoc	="+sDCraneWordPutLoc);
    		}
		    
		    for(int inx = 1; inx <= iGripCount; inx++)
	    	{
	    		if(iGripCount == 2){
	    			if(inx == 1){
		    			
		    			sInnerStockId		= sDStockId;
			    		sInnerSchId 		= sDSchId;
			    		sInnerWbookId		= sDWbookId;
			    		sInnerPutPositoin 	= sDCraneWordPutLoc;
			    		sInnerCraneLayerGp	= YmCommonConst.STACK_BED_GP_02;
			    		jtR.setField("Put_Position", sDCraneWordPutLoc);
			    	}else if(inx == 2){
				    	
			    		sInnerStockId 		= sStockId;
			    		sInnerSchId 		= sGrobalSchId;
			    		sInnerWbookId		= sGrobalWbookId;
			    		sInnerPutPositoin 	= sPut_Position;
			    		sInnerCraneLayerGp	= YmCommonConst.STACK_BED_GP_01;
			    		jtR.setField("Put_Position", sPut_Position);
				    }
	    		}else {
	    			if(inx == 1){
		    		
			    		sInnerStockId 		= sStockId;
			    		sInnerSchId 		= sGrobalSchId;
			    		sInnerWbookId		= sGrobalWbookId;
			    		sInnerPutPositoin 	= sPut_Position;
			    		sInnerCraneLayerGp	= YmCommonConst.STACK_BED_GP_01;
			    		jtR.setField("Put_Position", sPut_Position);
			    	}
	    		}
	    		
				/*
				 * Crane Put실적 저장품의 적치단정보를 가져온다.
				 * SADDLE인 경우 입측은 stack_layer_active_stat = 'C' 이다.
				 * 따라서 위 조건은 제외시킨다.
				 * tb_ym_stacklayer Table stack_layer_active_stat : 'O'(Open)
				 * tb_ym_stacklayer Table stack_layer_stat 	   	  : 'P'(Put Schedule 수행예정)
				 */			
				JDTORecord layerRc = null;
		    	
		    	layerRc = dao.getPutStackLayerListWithSchId(sInnerSchId);
		    	
		    	if(layerRc == null){
	    			logger.println(LogLevel.DEBUG,this, "권하에러..");
		    	   	logger.println(LogLevel.DEBUG,this, "적치단정보 가져올 수 없슴");
	    			layerRc = createToLoc_01(sInnerSchId,sTc);
	    		}
	    		
	    		if(YmCommonConst.TC_CM1PB06.equals(sTc) || //B열연 SLAB 권하실적 
	    			YmCommonConst.TC_HM1PB06.equals(sTc)|| //A_A열연 SLAB 권하실적(MCH)
	    			YmCommonConst.TC_HM1PB56.equals(sTc)){ //A_B열연 SLAB 권하실적(MCH)
					
					JDTORecord tempRc = dao.getPutStackLayerListWithSchId(sInnerSchId);
			    	
			    	if(tempRc == null){
		    			throw new EJBServiceException("=권하실적=> TO위치정보가 존재하지 않습니다.\n 권하이상으로 처리하십시요.");
		    		}
		    	}
		    	
	    		String sStackColGp	 = StringHelper.evl(layerRc.getFieldString("STACK_COL_GP"), "");
		    	String sStackBedGp	 = StringHelper.evl(layerRc.getFieldString("STACK_BED_GP"), "");
		    	String sStackLayerGp = StringHelper.evl(layerRc.getFieldString("STACK_LAYER_GP"), "");
	    	
		    	logger.println(LogLevel.DEBUG,this, "============Put DB Position============");
				logger.println(LogLevel.DEBUG,this, "sStackColGp="	 + sStackColGp);
				logger.println(LogLevel.DEBUG,this, "sStackBedGp="	 + sStackBedGp);
				logger.println(LogLevel.DEBUG,this, "sStackLayerGp=" + sStackLayerGp);
			
				/**
				 * LINE IN 권하 실적작업일 경우에는 MAP상에 존재하는 위치를 가지고 실적처리한다.
				 * 전문상에 존재하는 위치정보는 무시한다.
				 * 권하이상실적은 존재하지 않는다.
				 */
				if(YmCommonConst.MAIN_WORK_M.equals(sWork_Id)&&
				   YmCommonUtil.isLineInWork(sSch_Code)){
					
					sInnerPutPositoin= sStackColGp + 	
									   sStackBedGp +
									   sStackLayerGp;
									
					jtR.setField("Put_Position",sInnerPutPositoin);		
					logger.println(LogLevel.DEBUG,this, "=======LINE IN 작업 Position=======");
					logger.println(LogLevel.DEBUG,this, "Put_Position="	 + sPut_Position);								
				}
				
		    	/*
				 * Crane이 실제 PUT한 전문 적치단 정보
				 */
				if(sInnerPutPositoin.length() == 10){
		    		
					String sL2StackColGp   = sInnerPutPositoin.substring(0, 6);
					String sL2StackBedGp   = sInnerPutPositoin.substring(6, 8);
					String sL2StackLayerGp = sInnerPutPositoin.substring(8,10);
					
					/*
					 * 최초 스케쥴 등록시점의 PUT위치와 권하실적항목의 PUT위치를 비교한다.
					 */
		    		if(!sStackColGp.equals(sL2StackColGp)||
			    	   !sStackBedGp.equals(sL2StackBedGp)||	
			    	   !sStackLayerGp.equals(sL2StackLayerGp)){
			    		
			    		logger.println(LogLevel.DEBUG,this, "권하에러..");
		    	   		logger.println(LogLevel.DEBUG,this, "DB PUT위치와 LEVEL2 전문 PUT위치 같지 않음.");   	
		    	   		logger.println(LogLevel.DEBUG,this, "LEVEL2 전문 PUT위치를 우선으로 적용."); 
		    	   		
		    	   		sStackColGp   = sL2StackColGp;
		    	   		sStackBedGp   = sL2StackBedGp;
		    	   		sStackLayerGp = sL2StackLayerGp;
		    	   		
		    	   		sInnerPutPositoin = sL2StackColGp + 	
											sL2StackBedGp +
											sL2StackLayerGp;
						
				    	jtR.setField("Put_Position",sInnerPutPositoin);		
				    	
				    	/**
				    	 * 권하이상처리를 설비에 할 경우.
				    	 * 설비 콘베이어 정보를 생성한다.
				    	 */
				    	{
					    	String sPutUsageCd = YmCommonUtil.getStackColInfoWithPk(sL2StackColGp);
					   		
					   		if(YmCommonConst.STACK_COL_USAGE_CD_XX.equals(sPutUsageCd)	||// SLAB 비상적치위치
						       YmCommonConst.STACK_COL_USAGE_CD_SE.equals(sPutUsageCd)	||// SLAB Scafing 입측
							   YmCommonConst.STACK_COL_USAGE_CD_SD.equals(sPutUsageCd)	){// SLAB Scafing 출측
							   	
								int iSeq = YmCommonDB.shiftConveyorInfo(sL2StackColGp,
																		sL2StackBedGp);
							}
						}	
		    	   	}
				}
				
				/*
				 * MAX 단으로 권하위치 변경 작업
				 */	
				String sStackLayerGpOld = sStackLayerGp;
				
				JDTORecord stackLayer = dao.getStackLayerMaxInfo(sStackColGp,sStackBedGp);
				
				if(stackLayer != null){
					sStackLayerGp = StringHelper.evl(stackLayer.getFieldString("STACK_LAYER_GP"), "");
			 		
					
					sInnerPutPositoin = sStackColGp + 	
										sStackBedGp +
										sStackLayerGp;

					jtR.setField("Put_Position",sInnerPutPositoin);	
					
					logger.println(LogLevel.DEBUG,this, "MAX단으로 변경 작업 >>"+sStackLayerGpOld+"-->"+sStackLayerGp);
				}
				
				
				
		    	/*
				 * 작업실적 Table에 권하실적을 Update
				 */		
				iReq = dao.updateCranePutWrslt(getPutRtData(jtR,sInnerSchId,sFuncGbn));
				
				/* 
				 * 적치단 Put위치에 다른 SLAB가 있을 경우.
				 * 해당동의 XX번지로 저장품 MAP을 수정한다.
				 */	
				iReq = YmCommonDB.updateLegacyStockId_Slab(dao,
													  	   sStackColGp,
													  	   sStackBedGp,
													  	   sStackLayerGp,
													  	   sInnerStockId);
													  
		    	/* 
				 * 적치단 Put위치를 적치상태로 변경
				 * tb_ym_stacklayer Table : stock_id = Slab No
				 * tb_ym_stacklayer Table : stack_layer_stat	   = 'L'(적치중)
				 */	
				
				String rLayerStat = YmCommonConst.STACK_LAYER_STAT_L;
		    	/**
		    	 *	보조작업으로 작업한 대상이 작업예약ID를 가지고 있으면 'S'로 셋팅
		    	 */
		    	if(YmCommonConst.SUB_WORK_S.equals(sWork_Id)){
			    	
			    	JDTORecord stockJr = dao.getStockInfo(sInnerStockId);
					if(stockJr != null){
						String sWbookId = StringHelper.evl(stockJr.getFieldString("WBOOK_ID"), "");
						if(!"".equals(sWbookId)){
							rLayerStat = YmCommonConst.STACK_LAYER_STAT_S;
						}
					}
			    } 
		    	logger.println(LogLevel.DEBUG,this, "Crane Put Loc의 상태를 변경한다."); 
		    	iReq = dao.updateCraneStackLayerStat(sStackColGp,
		    										 sStackBedGp,
		    										 sStackLayerGp,
		    										 sInnerStockId,
		    										 rLayerStat);
		    	
		    	
		    	//P상태의 저장위치를 삭제 한다. 
		    	logger.println(LogLevel.DEBUG,this, "Crane Put Loc위치 초기화 작업,"); 
				
		    	if(sStackColGp.equals("2ESE01")){
			    	iReq = dao.deleteStackerStockInfo(sInnerStockId);	
					
					iReq = dao.deleteStackLayerStockInfo(sInnerStockId);
		    	}
		    	
	    	
	    		/*
		    	 * B열연 Slab 권하실적	
		    	 * 바로 위 상단 상태정보를 UPDATE
		    	 */
	    		iReq = YmCommonDB.setSlabUpperState_E(sStackColGp,
					    						 	  sStackBedGp,
					    						 	  sStackLayerGp);
	    	
		    	/*
				 * 크레인의 권하실적이 발생하면 크레인에 할당된 저장품 적치단 정보를 삭제한다.
				 * tb_ym_stacklayer Table : stock_id 			= ''(Empty)
				 * tb_ym_stacklayer Table : stack_layer_stat	= 'E'(적치가능)
				 */
	    		logger.println(LogLevel.DEBUG,this, "크레인의 권하실적이 발생하면 크레인에 할당된 저장품 적치단 정보를 삭제"); 
		    	iReq = dao.updateCraneStackLayerStat(sYard_Id+sBay_Gp+
		    										 YmCommonConst.EQUIP_KIND_CR+sEquip_No,
													 YmCommonConst.STACK_BED_GP_01,
		    										 sInnerCraneLayerGp,
													 "",
		    										 YmCommonConst.STACK_LAYER_STAT_E);
		    	
	    	
		    	/**
		    	 *	1.  해당 권상위치(적치번지)의 적치BED
				 * 		현재수량(-1), 가능수량(+1) 을 셋팅한다.
				 * 		tb_ym_stacker Table stack_bed_qnty_curr : 적치BED수량현재
				 * 		tb_ym_stacker Table stack_bed_able_qnty : 적치BED가능수량
				 * 
				 *	2.	해당 권상위치(적치번지)의 적치BED
				 * 		현재높이, 가능높이, 현재중량, 가능중량 을 셋팅한다.(SLAB)
				 * 		tb_ym_stacker Table stack_bed_high_curr : 적치BED높이현재
				 * 		tb_ym_stacker Table stack_bed_able_high : 적치BED가능높이
	        	 * 		tb_ym_stacker Table stack_bed_wt_curr   : 적치BED중량현재
	        	 * 		tb_ym_stacker Table stack_bed_able_wt   : 적치BED가능중량
	        	 *
	        	 *		COIL, SLAB GRIP 사용유무에 따라 아래의 값을 셋팅
				 */ 
				{ 
				/*	
					String sCurrQty	= "0";
					String sSlabT	= "0";
					String sSlabWt 	= "0";
					
					sCurrQty	= "1";
					 
					iReq = dao.updateStackerQtyInfo(sStackColGp,
			    									sStackBedGp,
			    									sCurrQty);
					
					JDTORecord slabRc = dao.getSlabCommonInfo(sInnerStockId);
					if(slabRc != null){
						
						sSlabT  = StringHelper.evl(slabRc.getFieldString("SLAB_T"), "0"); //SLAB 두께 = 높이
						sSlabWt = StringHelper.evl(slabRc.getFieldString("SLAB_WT"), "0");//SLAB 중량 = 중량
						
						iReq = dao.updateStackerWtInfo(sStackColGp,
				    								   sStackBedGp,
				    								   sSlabT,
				    								   sSlabWt);
					}
				*/	
				}
			
				/*
				 * 저장품 이동조건 셋팅
				 */
				
				{
					iReq = setStockMoveTerm_Slab(sInnerStockId,
											 	 sSch_Code,
											 	 sInnerPutPositoin);
				 
				}
				
		    	/*
				 * 작업예약 TABLE,저장품 TABLE 셋팅
				 */	
		    	{
		    		/**
		    		 * 주작업 저장품일 경우에만 적용
		    		 */
		    		if(YmCommonConst.MAIN_WORK_M.equals(sWork_Id)){
				    	/*
						 * 저장품 Table의 Wbook_id 항목을 Update
						 * tb_ym_stock Table wbook_id : ''(empty)
						 */	 
						iReq = dao.updateStockWbookId(sInnerStockId,""); 
					}
					/*
					 * 저장품 Table에 Wbook_id가 존재하는지 Check
					 */
					JDTORecord countRc = dao.getStockWbookId(sInnerWbookId); 
			    	
					/*
					 * 저장품 Table에 Wbook_id가 존재하지 않으면 작업예약 Table Delete
					 */	  
			    	if(countRc == null){
			    		iReq = dao.deleteWbookInfo(sInnerWbookId);  	 
					}  
				}
				
				/*
				 * Schedule Table Delete
				 */	 
		    	iReq = dao.deleteSchInfo(sInnerSchId);  	
							   
				/*
				 * SLAB 공통 Table 저장위치 Update 
				 */	 
				iReq = dao.updateSlabCommonLocInfo(sInnerStockId,sInnerPutPositoin);  	  
				
				/*
				 *	저장품 예상PUT 위치 CLEAR
				 *  저장품지정조회화면에서 예약상태로 남아있는것을 방지하기 위해
				 */
				{
					JDTORecord stockRc = dao.getStockInfo(sInnerStockId);
					if(stockRc != null){
		    			String sCarunloadBay = StringHelper.evl(stockRc.getFieldString("CARUNLOAD_PUT_LOC"), "");//하차PUT위치
		    			if(sInnerPutPositoin.equals(sCarunloadBay)){
							iReq = dao.updateStockPutLocWithStockId(sInnerStockId,"");  
						}
						logger.println(LogLevel.DEBUG,this, "저장품 예상PUT 위치 CLEAR A="+ sCarunloadBay);
	    				logger.println(LogLevel.DEBUG,this, "저장품 예상PUT 위치 CLEAR B="+ sInnerPutPositoin);
		    		}
		    	}
				/*
				 * B열연 SLAB 권하이상실적 발생시 처리
				 * A_A열연 SLAB 권하이상실적 발생시 처리(MCH)
				 * A_B열연 SLAB 권하이상실적 발생시 처리(MCH)
				 */	  
				if(YmCommonConst.TC_CM1PB07.equals(sTc) 
				|| YmCommonConst.TC_HM1PB07.equals(sTc)
				|| YmCommonConst.TC_HM1PB57.equals(sTc)){				
					
					isSuccess = setCraneIsangPutRtInfo_Slab(jtR,sInnerStockId);
				}  
				
				
				/*
				 * 장입보급 순서 복구
				 */
				{
					iReq = setStockLotNo_Slab(sInnerSchId,
											  sInnerStockId,
											  sSch_Code,
											  sWork_Id,
											  sInnerPutPositoin.substring(0,6));
				 
				}
				
				/*
				 * 생산통제 장입진행실적 송신.
				 */
				{
					iReq = sendPcStatus_Slab(sInnerStockId,
											 sSch_Code,
											 sWork_Id);
				 
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
	 *	1.	SLAB INFO
        *		Crane Put 이상실적에 관련된 상태정보를 등록 및 수정한다.
        * 
        * param JDTORecord 	: 전문항목
        * param String 		: 저장품
	 *
        * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */      
	public boolean setCraneIsangPutRtInfo_Slab(JDTORecord jtR,
											   String sStockId){
		
		boolean isSuccess = false;
		int iReq = -1;
		
		try{
			
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
	    	String sWorkOrder_Ps 	= StringHelper.evl(jtR.getFieldString("WorkOrder_Position"), ""); // 작업지시위치
			
			logger.println(LogLevel.DEBUG,this, "sWorkOrder_Ps="	+ sWorkOrder_Ps);
			logger.println(LogLevel.DEBUG,this, "==이상실적 발생시 작업지시위치 CLEAR==");
			
	    	/*
			 * 이상실적 발생시 작업지시위치 CLEAR
			 */
			{  
				String sOrginStackColGp = "";
				String sOrginStackBedGp = "";
				String sOrginStackLayerGp = "";
				
				if(sWorkOrder_Ps.length() == 10){
		    		
					sOrginStackColGp 	= sWorkOrder_Ps.substring(0,6);
					sOrginStackBedGp 	= sWorkOrder_Ps.substring(6,8);
					sOrginStackLayerGp 	= sWorkOrder_Ps.substring(8,10);
				}	
				
				logger.println(LogLevel.DEBUG,this, "============Put Origin Position============");
				logger.println(LogLevel.DEBUG,this, "sOrginStackColGp="	 + sOrginStackColGp);
				logger.println(LogLevel.DEBUG,this, "sOrginStackBedGp="	 + sOrginStackBedGp);
				logger.println(LogLevel.DEBUG,this, "sOrginStackLayerGp="+ sOrginStackLayerGp);
				
				/**
		    	 * ORIGIN 위치가 설비일 경우.
		    	 * 설비 콘베이어 정보를 삭제한다.
		    	 */
		    	String sPutUsageCd = YmCommonUtil.getStackColInfoWithPk(sOrginStackColGp);
		   		
		   		if(YmCommonConst.STACK_COL_USAGE_CD_XX.equals(sPutUsageCd)	||// SLAB 비상적치위치
			       YmCommonConst.STACK_COL_USAGE_CD_SE.equals(sPutUsageCd)	||// SLAB Scafing 입측
				   YmCommonConst.STACK_COL_USAGE_CD_SD.equals(sPutUsageCd)	){// SLAB Scafing 출측
				   	
					int iSeq = YmCommonDB.deleteConveyorInfo(sOrginStackColGp,
															 sStockId);
				}else{
				
					JDTORecord layerInfo = dao.getStackLayerInfoWithPk(sOrginStackColGp,
																	   sOrginStackBedGp,
																	   sOrginStackLayerGp);
					String sLayerStat = "";
					
					if(layerInfo != null){
					
						sLayerStat 	= StringHelper.evl(layerInfo.getFieldString("STACK_LAYER_STAT"), "");
					}
					/**
					 * 정상적인 이상실적 처리
					 *	- 구 권하위치 'P'상태임. 따라서 스케쥴 TO위치 삭제
					 * TO위치 FAIL시 이상실적 처리
					 *	- 신,구 권하위치가 동일함. 따라서 구 권하위치 처리 필요없슴
					 */
					if(YmCommonConst.STACK_LAYER_STAT_P.equals(sLayerStat)){
						
						/* 
						 * 적치단 ORIGINAL 위치를 적치상태로 변경
						 * tb_ym_stacklayer Table : stock_id = ''(empty)
						 * tb_ym_stacklayer Table : stack_layer_stat	   = 'E'(적치가능)
						 */	
				    	iReq = dao.updateCraneStackLayerStat(sOrginStackColGp,
				    										 sOrginStackBedGp,
				    										 sOrginStackLayerGp,
				    										 "",
				    										 YmCommonConst.STACK_LAYER_STAT_E);
			    	}
			    	/**
			    	 * 이상실적 발생시 Original To 위치의 상단정보를 변경한다.
			    	 * 업무적 충돌발생여부는 존재하나, 지금은 무조건 
			    	 * 'V'(하단에 적치되지 않은 위치)값으로 셋팅한다.
		    		 * B열연 Slab ORIGINAL 위치
			    	 * 바로 위 상단 상태정보를 UPDATE
			    	 */
			    	
			    	iReq = YmCommonDB.setSlabUpperState_V(sOrginStackColGp,
					    							 	  sOrginStackBedGp,
					    							 	  sOrginStackLayerGp);
					/*
					 *	저장품 예상PUT 위치 CLEAR
					 *  저장품지정조회화면에서 예약상태로 남아있는것을 방지하기 위해
					 */
					 {
						JDTORecord stockRc = dao.getStockInfo(sStockId);
						if(stockRc != null){
			    			String sCarunloadBay = StringHelper.evl(stockRc.getFieldString("CARUNLOAD_PUT_LOC"), "");//하차PUT위치
			    			
			    			String sPutLoc = sOrginStackColGp+
			    			                 sOrginStackColGp+
			    			                 sOrginStackLayerGp;
			    			
			    			if(sPutLoc.equals(sCarunloadBay)){
								iReq = dao.updateStockPutLocWithStockId(sStockId,"");  
							}
							logger.println(LogLevel.DEBUG,this, "저장품 예상PUT 위치 CLEAR A="+ sCarunloadBay);
	    					logger.println(LogLevel.DEBUG,this, "저장품 예상PUT 위치 CLEAR B="+ sPutLoc);
			    		}
			    	}   							 	  
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
	 *	2.	SLAB INFO
        *		야드작업실적송신
        *		업무처리에 따른 실적을 조업,출하 등 타업무에 내부인터페이스를 통해 전달한다.
        * 
        * param JDTORecord 	: 전문항목
        *
        * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */      
	public boolean callInnerWorkInfo_Slab(JDTORecord jtR){
								     	  	
		Boolean isSuccess = new Boolean(false);
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String tWork_Id 		= YmCommonUtil.getCurDataWithLegacy(StringHelper.evl(jtR.getFieldString("Work_Id"), ""));
			if(YmCommonConst.SUB_WORK_S.equals(tWork_Id)){
				logger.println(LogLevel.DEBUG,this, "내부IF호출===보조작업대상으로 작업실적전송 SKIP.===");
				return false;
			}
			/*
			 *	1.	출하
			 *		SLAB외판 출하상차 첫 개시및 마지막 완료시
			 */	
			{
				String sStockId	= StringHelper.evl(jtR.getFieldString("Slab_No"), "").trim();
				String sSchCode 	= StringHelper.evl(jtR.getFieldString("Sch_Code"), "");
				String sGstockId	= StringHelper.evl(jtR.getFieldString("GROBAL_STOCK_ID"), "");
				String sGlocation	= StringHelper.evl(jtR.getFieldString("GROBAL_LOCATION"), "");
				String sYardGp	= StringHelper.evl(jtR.getFieldString("Yard_Id"), "");
				
				if(YmCommonConst.NEW_SCH_WORK_KIND_SVFL.equals(sSchCode)){ // SLAB외판 출하상차
					
					List 	   dmList  		= null; // Card No,운송지시일자,순번이 같은 저장품정보
					JDTORecord dmRc    	= null; // 저장품정보
					String 	   sSItem  	= "";   // 저장품품목
					String     sGOODS_NO =null;  // 제품번호
					String 	   sSmt    		= "";   // 저장품이동조건
					String 	   sUpDown 	= "";   // 출하상차/하차구분
					int		   iDmSize 	= 0;    // Card No,운송지시일자,순번이 같은 저장품 갯수
					int		   iDmFns  	= 0;    // 상차완료인 저장품 갯수	
					int        	   iPsCnt  	= 0;    // 출하 하차 스케쥴  갯수  
					
					if(!"".equals(sStockId)){
						
						dmList  = dao.getYmDmCommonInfo(sStockId);
						iDmSize = dmList.size();
						
						if(YmCommonConst.NEW_SCH_WORK_KIND_SVFL.equals(sSchCode)){
				 	 	 	sUpDown = "D";
					 	}else{ 	
					 	 	sUpDown = "D";
					 	}
					 	
						for(int inx = 0; inx < dmList.size() ; inx++){
					 	 	
					 	 	dmRc   	= (JDTORecord)dmList.get(inx);
					 	 	sSmt   	= StringHelper.evl(dmRc.getFieldString("STOCK_MOVE_TERM"), "");
					 	 	sSItem 	= StringHelper.evl(dmRc.getFieldString("STOCK_ITEM"), "");
					 	 	
					 	 	
					 	 	if(YmCommonConst.NEW_SCH_WORK_KIND_SVFL.equals(sSchCode)){	// SLAB외판 출하상차
					 	 		if(YmCommonConst.NEW_STOCK_MOVE_TERM_VL.equals(sSmt)){ 	// 상차완료
							 		iDmFns++;						 		
							 		
							 	}	
				   			}
						}
						
			    		String sCarCardNo     = StringHelper.evl(dmRc.getFieldString("CAR_CARD_NO"), "");
			    		String sTransWordNo   = StringHelper.evl(dmRc.getFieldString("TRANS_WORD_NO"), "");

						JDTORecord carR = dao.getDmCarInfo(sStockId);
						String sCarNo = "";
					    	if(carR != null){
								sCarNo = StringHelper.evl(carR.getFieldString("CAR_NO"), "");
					    	}
					    
					    /**
					     * Slab 1매 작업이면 최초 권하시 1매
					     * Slab 2매 작업이면 최초 권하시 2매
					     */
					    int iCount = 1;
					    if(!"".equals(sGstockId)) iCount = 2;
					    
				    		if(iDmFns == iCount){ //상차,하차완료인 저장품이 1개일 때 처리
				    		

				    			
				    			
				    	//		YMDM014 model = new YMDM014();
						//	model.setTcCode(YmCommonConst.MODEL_YMDM014);
						//	model.setTcDate(YmCommonUtil.getCurDate("yyyy-MM-dd"));
						//	model.setTcTime(YmCommonUtil.getCurDate("HH-mm-ss"));
														
							/** 상차 개시 일자	 */
						//	model.setCARLOAD_START_DATE(YmCommonUtil.getCurDate("yyyyMMdd"));
							
							/** 상차 개시 시각 */
						//	model.setCARLOAD_START_TIME(YmCommonUtil.getCurDate("HHmmss"));
							
							/** 상하차 구분 */
						//	model.setUPCARUNLOAD_GP(sUpDown);
						
							/** 카드 번호	 */
						//	model.setCARD_NO(sCarCardNo);
							
		
							/** 차량 번호 */
						//	model.setCAR_NO(sCarNo);
							
						//	/** 운송지시일자 */
						//	model.setTRANS_WORD_DATE(sTransWordNo.length() > 8 ? sTransWordNo.substring(0,8) : sTransWordNo);
							
							/** 운송지시순번 */
						//	model.setTRANS_WORD_SEQNO(sTransWordNo.length() > 8 ? sTransWordNo.substring(8) : "");
							
						//	EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
						//	isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{CommonModel.class},
																		 // 	  	 new Object[]{model});
							
		                    
		           		    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		                    //외판슬라브출하상차개시
		         			JDTORecord tcRecordDM = null;
		         			tcRecordDM = JDTORecordFactory.getInstance().create(); 
		         			tcRecordDM.setField("CARD_NO",sCarCardNo);
		         			tcRecordDM.setField("CAR_NO",sCarNo);
		         			tcRecordDM.setField("YD_GP",sYardGp);
		         			tcRecordDM.setField("TRANS_WORD_DATE",sTransWordNo.length() > 8 ? sTransWordNo.substring(0,8) : sTransWordNo);
		         			tcRecordDM.setField("TRANS_WORD_SEQNO",sTransWordNo.length() > 8 ? sTransWordNo.substring(8) : "");
		         			
		         			//인터페이스 전문 호출
		         			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
		         			isSuccess = (Boolean)ejbConn.trx("getYDDMR009",new Class[]{JDTORecord.class},
		         			  	  	 new Object[]{tcRecordDM}); 
		                    logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 외판슬라브출하상차개시.===");
		                    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
													  	  	 																	  	  	 
				    	}
				    		
				    		
							//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		                    //외판슬라브일품출하상차실적
		         			JDTORecord tcRecordDM = null;
		         			tcRecordDM = JDTORecordFactory.getInstance().create(); 
		                    tcRecordDM.setField("CARD_NO",StringHelper.evl(dmRc.getFieldString("CAR_CARD_NO"), ""));
		                    tcRecordDM.setField("CAR_NO",StringHelper.evl(dmRc.getFieldString("CAR_NO"), ""));
		                    tcRecordDM.setField("YD_GP", sYardGp);
		                    tcRecordDM.setField("GOODS_EA", Integer.toString(dmList.size()) );
		                    tcRecordDM.setField("GOODS_NO",sStockId);
				            tcRecordDM.setField("TRANS_WORD_DATE", sTransWordNo.length() > 8 ? sTransWordNo.substring(0,8) : sTransWordNo);
		                    tcRecordDM.setField("TRANS_WORD_SEQNO", sTransWordNo.length() > 8 ? sTransWordNo.substring(8) : "");
		         			
		         			//인터페이스 전문 호출
		         			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
		         			isSuccess = (Boolean)ejbConn.trx("getYDDMR013",new Class[]{JDTORecord.class},
		         			  	  	 new Object[]{tcRecordDM}); 
		                    logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 외판슬라브출하상차완료.===");
		                    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
										    	
				    	if(iDmSize != 0 && iDmSize == iDmFns){ // 상차,하차완료인 저장품이 ALL일때 처리
				    		
				    			
				    	//		YMDM015 model = new YMDM015();
						//	model.setTcCode(YmCommonConst.MODEL_YMDM015);
						//	model.setTcDate(YmCommonUtil.getCurDate("yyyy-MM-dd"));
						//	model.setTcTime(YmCommonUtil.getCurDate("HH-mm-ss"));
							
							
							/** 상차 완료 일자	 */
						//	model.setCARLOAD_END_DATE(YmCommonUtil.getCurDate("yyyyMMdd"));
							
							/** 상차 완료 시각 */
						//	model.setCARLOAD_END_TIME(YmCommonUtil.getCurDate("HHmmss"));
							
							/** 상하차 구분 */
						//	model.setUPCARUNLOAD_GP(sUpDown);
						
							/** 카드 번호	 */
						//	model.setCARD_NO(sCarCardNo);
							
							/** 차량 번호 */
						//	model.setCAR_NO(sCarNo);
							
							/** 운송지시일자 */
						//	model.setTRANS_WORD_DATE(sTransWordNo.length() > 8 ? sTransWordNo.substring(0,8) : sTransWordNo);
							
							/** 운송지시순번 */
						//	model.setTRANS_WORD_SEQNO(sTransWordNo.length() > 8 ? sTransWordNo.substring(8) : "");
							
							
						//	EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
						//	isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{CommonModel.class},
						//												  	  	 new Object[]{model});
							
							//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		                    //외판슬라브출하상차완료
//		         			JDTORecord tcRecordDM = null;
		         			tcRecordDM = JDTORecordFactory.getInstance().create(); 
		                    tcRecordDM.setField("CARD_NO",sCarCardNo);
		                    tcRecordDM.setField("CAR_NO", sCarNo);
		                    tcRecordDM.setField("YD_GP", sYardGp);		
		                    tcRecordDM.setField("TRANS_WORD_DATE", sTransWordNo.length() > 8 ? sTransWordNo.substring(0,8) : sTransWordNo);
		                    tcRecordDM.setField("TRANS_WORD_SEQNO", sTransWordNo.length() > 8 ? sTransWordNo.substring(8) : "");
		         			
		         			//인터페이스 전문 호출
		         			EJBConnector ejbConn2 = new EJBConnector("default","JNDIYardWrkResReg",this);
		         			isSuccess = (Boolean)ejbConn2.trx("getYDDMR017",new Class[]{JDTORecord.class},
		         			  	  	 new Object[]{tcRecordDM}); 
		                    logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 외판슬라브출하상차완료.===");
		                    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

						}

				 	}
	
				 }	
			}   	
			/*
			 *	1.	출하
			 *		SLAB 이송상차 하차 첫 개시및 마지막 완료시
			 */	
			{
				String sStockId	= StringHelper.evl(jtR.getFieldString("Slab_No"), "").trim();
				String sSchCode 	= StringHelper.evl(jtR.getFieldString("Sch_Code"), "");
				String sGstockId	= StringHelper.evl(jtR.getFieldString("GROBAL_STOCK_ID"), "");
				String sGlocation	= StringHelper.evl(jtR.getFieldString("GROBAL_LOCATION"), "");
				
				if(YmCommonConst.NEW_SCH_WORK_KIND_SVML.equals(sSchCode)|| // SLAB 이송상차
				   YmCommonConst.NEW_SCH_WORK_KIND_SVMU.equals(sSchCode)){ // SLAB 이송하차	
					
					List 	   dmList  = null; // Card No,운송지시일자,순번이 같은 저장품정보
					JDTORecord dmRc    = null; // 저장품정보
					String 	   sSItem  = "";   // 저장품품목
					String 	   sSmt    = "";   // 저장품이동조건
					String 	   sUpDown = "";   // 이송상차/하차구분
					int		   iDmSize = 0;    // Card No,운송지시일자,순번이 같은 저장품 갯수
					int		   iDmFns  = 0;    // 상차완료인 저장품 갯수	
					int        iPsCnt  = 0;    // 이송 하차 스케쥴  갯수  
					
					if(!"".equals(sStockId)){
						
						dmList  = dao.getYmDmCommonInfo(sStockId);
						iDmSize = dmList.size();
						
						if(YmCommonConst.NEW_SCH_WORK_KIND_SVML.equals(sSchCode)){
				 	 	 	sUpDown = "U";
					 	}else{ 	
					 	 	sUpDown = "D";
					 	}
					 	
						for(int inx = 0; inx < dmList.size() ; inx++){
					 	 	dmRc   = (JDTORecord)dmList.get(inx);
					 	 	sSmt   = StringHelper.evl(dmRc.getFieldString("STOCK_MOVE_TERM"), "");
					 	 	sSItem = StringHelper.evl(dmRc.getFieldString("STOCK_ITEM"), "");
					 	 	
					 	 	if(YmCommonConst.NEW_SCH_WORK_KIND_SVML.equals(sSchCode)){	// SLAB 이송상차
					 	 		if(YmCommonConst.NEW_STOCK_MOVE_TERM_VL.equals(sSmt)){ 	// 상차완료
							 		iDmFns++;
							 	}	
				   			}else if(YmCommonConst.NEW_SCH_WORK_KIND_SVMU.equals(sSchCode)){ 	// SLAB 이송하차	
				   				//if(YmCommonConst.NEW_STOCK_MOVE_TERM_C1.equals(sSmt)|| 	// 이송완료
				   				//   YmCommonConst.NEW_STOCK_MOVE_TERM_DS.equals(sSmt)|| 	// 정정작업대기	
				   				//   YmCommonConst.NEW_STOCK_MOVE_TERM_ES.equals(sSmt)){ 	// 압연지시대기		
							 		iDmFns++;
							 	//}
				   			}
						}
					    
					    /**
					     * Slab 1매 작업이면 최초 권하시 1매
					     * Slab 2매 작업이면 최초 권하시 2매
					     */
					    int iCount = 1;
					    if(!"".equals(sGstockId)) iCount = 2;
					    
				    		if(iDmFns == iCount){ //상차,하차완료인 저장품이 1개일 때 처리
				    		
				    		String sCarCardNo     = StringHelper.evl(dmRc.getFieldString("CAR_CARD_NO"), "");
				    		String sTransWordNo   = StringHelper.evl(dmRc.getFieldString("TRANS_WORD_NO"), "");
				    		
			 	
							JDTORecord carR = dao.getDmCarInfo(sStockId);
							String sCarNo = "";
						    	if(carR != null){
									sCarNo = StringHelper.evl(carR.getFieldString("CAR_NO"), "");
						    	}
			 
                            
							
				    	}
										    	
				    	if(iDmSize != 0 && iDmSize == iDmFns){ // 상차,하차완료인 저장품이 ALL일때 처리
				    		
				    		String sCarCardNo     = StringHelper.evl(dmRc.getFieldString("CAR_CARD_NO"), "");
				    		String sTransWordNo   = StringHelper.evl(dmRc.getFieldString("TRANS_WORD_NO"), "");
				    		
						/*	YMDM011 model = new YMDM011();
							model.setTcCode(YmCommonConst.MODEL_YMDM011);
							model.setTcDate(YmCommonUtil.getCurDate("yyyy-MM-dd"));
							model.setTcTime(YmCommonUtil.getCurDate("HH-mm-ss"));
							
							
							// 상차 완료 일자	 
							model.setCARLOAD_END_DATE(YmCommonUtil.getCurDate("yyyyMMdd"));
							
							// 상차 완료 시각 
							model.setCARLOAD_END_TIME(YmCommonUtil.getCurDate("HHmmss"));
							
							// 상하차 구분 
							model.setUPCARUNLOAD_GP(sUpDown);
						
							// 카드 번호	 
							model.setCARD_NO(sCarCardNo);
						*/	
							JDTORecord carR = dao.getDmCarInfo(sStockId);
							String sCarNo = "";
						    	if(carR != null){
									sCarNo = StringHelper.evl(carR.getFieldString("CAR_NO"), "");
						    	}
	 
							
							/*
							 * 항만 권하 완료 추가
							 * 2006.12.6 YMPS001
							 */
							if(YmCommonConst.NEW_SCH_WORK_KIND_SVMU.equals(sSchCode)){
								
								JDTORecord psCnt = dao.getYmPsCommonInfo(sSchCode, sCarCardNo);
								iPsCnt =  psCnt.getFieldInt("CNT");
								logger.println(LogLevel.DEBUG,this, "내부IF호출=== 항만 권하 완료 카운트==="+iPsCnt);		
								if (iPsCnt == 0) {
									
									if (sCarCardNo.startsWith("95")){
										//A열연 SLAB 이송
									}else{ 
			 	 				
										logger.println(LogLevel.DEBUG,this, "내부IF호출=== 항만 권하 완료 시작 ===");							
									
									//	YMPS001 model1 = new YMPS001();
									//	model1.setTcCode(YmCommonConst.MODEL_YMPS001);
									//	model1.setTcDate(YmCommonUtil.getCurDate("yyyy-MM-dd"));
									//	model1.setTcTime(YmCommonUtil.getCurDate("HH-mm-ss"));
									
										/** Yard구분    CHAR 1 */
									//	model1.setYdGp(YmCommonConst.YD_GP_2);
									
										/** 사업장코드  CHAR 2(01: 인천, 02:포항, 03:당진) */
									//	model1.setBizOfficeCd("03");
										
										/** Card번호    CHAR 4        */
									//	model1.setCardNo(sCarCardNo);
									
										/** Palette번호  CHAR 3        */
									//	model1.setPaletteNo(StringHelper.evl(dmRc.getFieldString("SHEAR_SUPPLY_SEQ"), ""));  
									
										/** 구분        CHAR 1(1: 정상, 2:취소) */
									//	model1.setDataGp("1");
									    
										/** 운송지시일자 */
									//	model1.setFrToMoveOrdDate(sTransWordNo.length() > 8 ? sTransWordNo.substring(0,8) : sTransWordNo);
										
										/** 운송지시순번 */
									//	model1.setFrToMoveOrdSeqNo(sTransWordNo.length() > 8 ? sTransWordNo.substring(8) : "");
										
									//	EJBConnector ejbConn1 = new EJBConnector("default","JNDIYardWrkResReg",this);
									//	isSuccess = (Boolean)ejbConn1.trx("sendInternalModel",new Class[]{CommonModel.class},
												                                          //    new Object[]{model1});
									}
									
									/*
									 *	2008.01.08 YJK 
									 *	1. 저장품 카드번호 Clear
									 *	2. Pallet 위치정보 Clear(Tb_Ym_StackLayer)
									 *	3. Pallet 위치정보 Clear(Tb_Ym_StackCol)
									 *
									 *	기능 : 팔레트 하차완료 후 자동출발처리기능 추가(이송상차 배제)
									 */
								 	/*
									{ 
								 		String sCarPosition	= "";
				 	 			
					 	 				JDTORecord tmRc = dao.getYMDM008Info_03(sStockId);
									   	 if(tmRc != null){
								    			sCarPosition  = StringHelper.evl(tmRc.getFieldString("CRANE_WRSLT_UP_LOC"), "");
								    		}	
								    		logger.println(LogLevel.DEBUG,this, "내부IF호출===Slab 이송 하차 작업위치==="+sCarPosition);		
								    		if(!"".equals(sCarPosition)){
									    			
							 	 			try{
							 	 				EJBConnector ejbConn = new EJBConnector("default","JNDICTSStatusReg",this);
							    					Boolean isTemp = (Boolean)ejbConn.trx("carStartOrder",
																			new  Class[]{String.class,
																						String.class,
																						String.class},
																			new Object[]{" ",							//한자리공백
																						 sCarCardNo,					//카드번호
																						 sCarPosition.substring(0, 6)});    //차량정지위치
											}catch(Exception e){
												logger.println(LogLevel.DEBUG,this," L2 출하실적 전문 송신 모듈 EXCEPTION");
											}					
											logger.println(LogLevel.DEBUG,this, "내부IF호출===Slab 이송 하차 작업 시 마지막 Slabl 하차 권하 시 자동출발 CALL.===");
											
											int iSeq1 = dao.updateStockCardNo(sCarCardNo);
										 	int iSeq2 = dao.updateStackLayerCardNo(sCarCardNo);
										 	int iSeq3 = dao.updateStackColCardNo_02(sCarCardNo);
										}
									}
									*/
								}
							}
							
							logger.println(LogLevel.DEBUG,this, "내부IF호출===Slab 이송 상차/하차 작업 시 마지막 Slab 상/하차 권하 시.===");											  	  	 

						}
				 	}
					
				 }	
			}   	
			
			/*
			 *	2.	조업
			 *		B열연 SLAB이송의 하차 완료 시
			 */		
			{
				String sYard_Id = StringHelper.evl(jtR.getFieldString("Yard_Id"), "");
				String sStockId	= StringHelper.evl(jtR.getFieldString("Slab_No"), "").trim();
				String sSchCode = StringHelper.evl(jtR.getFieldString("Sch_Code"), "");
				
				if((YmCommonConst.YD_GP_2.equals(sYard_Id) 		   	   // B열연 
//					|| YmCommonConst.YD_GP_0.equals(sYard_Id)		   // A열연 (MCH)
					)&&	YmCommonConst.NEW_SCH_WORK_KIND_SVMU.equals(sSchCode)){ // 이송하차	
					
					//2매 작업시 처리
					String sGstockId	= StringHelper.evl(jtR.getFieldString("GROBAL_STOCK_ID"), "");
					String sGlocation	= StringHelper.evl(jtR.getFieldString("GROBAL_LOCATION"), "");
				
					

					String sPut_Position= StringHelper.evl(jtR.getFieldString("Put_Position"), "");
					
					
				
				}	
			}
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue();
	}
	
       /**
	 * 오퍼레이션명 : 
	 *
	 *	2.	SLAB INFO
        *		야드작업실적송신
        *		업무처리에 따른 실적을 조업,출하 등 타업무에 내부인터페이스를 통해 전달한다.
        *		2007-03-08 A열연 SLAB야드 상차 추가(MCH)
        * 
        * param JDTORecord 	: 전문항목
        *
        * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */      
	public boolean callInnerWorkInfo_ASlab(JDTORecord jtR){
								     	  	
		Boolean isSuccess = new Boolean(false);
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String tWork_Id 		= YmCommonUtil.getCurDataWithLegacy(StringHelper.evl(jtR.getFieldString("Work_Id"), ""));
			if(YmCommonConst.SUB_WORK_S.equals(tWork_Id)){
				logger.println(LogLevel.DEBUG,this, "내부IF호출===보조작업대상으로 작업실적전송 SKIP.===");
				return false;
			}
			/*
			 *	1.	출하
			 *		SLAB 이송상차 하차 첫 개시및 마지막 완료시
			 */
			{
				String sStockId		= StringHelper.evl(jtR.getFieldString("Slab_No"), "").trim();
				String sSchCode 	= StringHelper.evl(jtR.getFieldString("Sch_Code"), "");
				String sGstockId	= StringHelper.evl(jtR.getFieldString("GROBAL_STOCK_ID"), "");
				String sGlocation	= StringHelper.evl(jtR.getFieldString("GROBAL_LOCATION"), "");
				
				if(YmCommonConst.NEW_SCH_WORK_KIND_SVML.equals(sSchCode)){ // SLAB 이송상차
				   	
					List 	   dmList  = null; // Card No,운송지시일자,순번이 같은 저장품정보
					JDTORecord dmRc    = null; // 저장품정보
					String 	   sSItem  = "";   // 저장품품목
					String 	   sSmt    = "";   // 저장품이동조건
					String 	   sUpDown = "";   // 이송상차/하차구분
					int		   iDmSize = 0;    // Card No,운송지시일자,순번이 같은 저장품 갯수
					int		   iDmFns  = 0;    // 상차완료인 저장품 갯수	
					int        iPsCnt  = 0;    // 이송 하차 스케쥴  갯수  
					
					if(!"".equals(sStockId)){
						
						dmList  = dao.getYmDmCommonInfo(sStockId);
						iDmSize = dmList.size();
						
						if(YmCommonConst.NEW_SCH_WORK_KIND_SVML.equals(sSchCode)){
				 	 	 	sUpDown = "U";
					 	}
						for(int inx = 0; inx < dmList.size() ; inx++){
					 	 	dmRc   = (JDTORecord)dmList.get(inx);
					 	 	sSmt   = StringHelper.evl(dmRc.getFieldString("STOCK_MOVE_TERM"), "");
					 	 	sSItem = StringHelper.evl(dmRc.getFieldString("STOCK_ITEM"), "");
					 	 	
					 	 	if(YmCommonConst.NEW_SCH_WORK_KIND_SVML.equals(sSchCode)){		// SLAB 이송상차
					 	 		if(YmCommonConst.NEW_STOCK_MOVE_TERM_VL.equals(sSmt)){ 		// 상차완료
							 		iDmFns++;
							 	}	
				   			}
						}
					    
					    /**
					     * Slab 1매 작업이면 최초 권하시 1매
					     * Slab 2매 작업이면 최초 권하시 2매
					     */
					    int iCount = 1;
					    if(!"".equals(sGstockId)) iCount = 2;
					    
			 
										    	
		 
				 	}
				 }	
			}   	
			
			/*
			 *	2.	조업
			 *		A열연 SLAB이송의 상차 완료시
			 */		
			{
				String sYard_Id = StringHelper.evl(jtR.getFieldString("Yard_Id"), "");
				String sStockId	= StringHelper.evl(jtR.getFieldString("Slab_No"), "").trim();
				String sSchCode = StringHelper.evl(jtR.getFieldString("Sch_Code"), "");
				String Position = StringHelper.evl(jtR.getFieldString("Put_Position"), "");
				
				if(YmCommonConst.YD_GP_0.equals(sYard_Id)		   			   // A열연 
					&& Position.lastIndexOf("PT") != -1){ 						// Pallet에 실었을때 	
					
					//2매 작업시 처리
					String sGstockId	= StringHelper.evl(jtR.getFieldString("GROBAL_STOCK_ID"), "");
					String sGlocation	= StringHelper.evl(jtR.getFieldString("GROBAL_LOCATION"), "");
				
					if(!"".equals(sGstockId)){
						
						YMPO155 poGModel = new YMPO155();
						poGModel.setTcCode(YmCommonConst.MODEL_YMPO155);
						poGModel.setTcDate(YmCommonUtil.getCurDate("yyyy-MM-dd"));
						poGModel.setTcTime(YmCommonUtil.getCurDate("HH-mm-ss"));
						
						/* 상하차처리일자  CHAR(8)	 yyyymmdd */
						poGModel.setupDownDate(YmCommonUtil.getCurDate("yyyyMMdd"));
						
						 /* SLAB번호		  CHAR(11) */
						poGModel.setslabNo(sGstockId);
						
						/* 상하차구분	 CHAR(1)		U:상차, D:하차		*/
						poGModel.setupDownGbn("U");
						
						/* 상하차위치 	 CHAR(10) 상차,하차 위치 */
						poGModel.setupDownLoc(sGlocation);
						
					//	EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
					//	isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{CommonModel.class},
																	  //	  	 new Object[]{poGModel});
					}
					
					String sPut_Position= StringHelper.evl(jtR.getFieldString("Put_Position"), "");
					
					YMPO155 poModel = new YMPO155();
					poModel.setTcCode(YmCommonConst.MODEL_YMPO155);
					poModel.setTcDate(YmCommonUtil.getCurDate("yyyy-MM-dd"));
					poModel.setTcTime(YmCommonUtil.getCurDate("HH-mm-ss"));
					
					/* 상하차처리일자  CHAR(8)	 yyyymmdd */
					poModel.setupDownDate(YmCommonUtil.getCurDate("yyyyMMdd"));
					
					 /* SLAB번호		  CHAR(11) */
					poModel.setslabNo(sStockId);
					
					/* 상하차구분	 CHAR(1)		U:상차, D:하차		*/
					poModel.setupDownGbn("U");
					
					/* 상하차위치 	 CHAR(10) 상차,하차 위치 */
					poModel.setupDownLoc(sPut_Position);
					
					//EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
					//isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{CommonModel.class},
					//											  	  	 new Object[]{poModel});
					logger.println(LogLevel.DEBUG,this, "내부IF호출===A열연 SLAB 이송 상차 완료 시.===");
				}	
			}
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue();
	}
	
       /**
	 * 오퍼레이션명 : 
	 *
	 *	2.	SLAB INFO
        *		야드작업실적송신
        *		A열연 SLAB야드 차량 상차시 마지막 slab권화 완료시 공정으로 JMS 송신
        *		2007-03-08 A열연 SLAB야드 상차 추가(MCH)
        * 
        * param JDTORecord 	: 전문항목
        *
        * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */      
	public boolean callLastWorkInfo_ASlab(JDTORecord jtR){
			Boolean isSuccess = new Boolean(false);
			logger.println(LogLevel.DEBUG,this, "put 처리시 JDTORecord 내용:"+jtR);			
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			//공정에서 상차 지시편성된 이송대상인지 아닌지 체크한다.
//			JDTORecord jRecord =  dao.getFrtomoveWordNo(StringHelper.evl(StringHelper.trim(jtR.getFieldString("Slab_No")),""));
//
//			if("".equals(StringHelper.evl(jRecord.getFieldString("FRTOMOVE_WORD_NO"),""))){
				if(YmCommonConst.EQUIP_KIND_PT.equals(jtR.getFieldString("Put_Position").substring(2, 4))){
				//마지막 상차  Slab인지 대상 조회
					JDTORecord jRecord =  dao.getlayerstatinfo(jtR.getFieldString("Put_Position").substring(0, 6));
					logger.println(LogLevel.DEBUG,this, "PALLET 적치 가능한 갯수"+ StringHelper.evl(jRecord.getFieldString("ECOUNT"),""));
					if(jRecord.getFieldInt("ECOUNT") >= 0 ){
						logger.println(LogLevel.DEBUG,this, "마지막 SLAB 권하 완료 - > 공정으로 정보 송신");
						EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
						isSuccess = (Boolean)ejbConn.trx("SendYMPM002", new Class[]{String.class}, new Object[]{jtR.getFieldString("Put_Position").substring(0, 6)});
					}
					logger.println(LogLevel.DEBUG,this, "차량 상차 대기중"+ jRecord);
					return isSuccess.booleanValue();
				}
//			}
			return true;
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}
	
	
	/**
	 * 오퍼레이션명 : 
	 *
	 *	2.	SLAB INFO
        *		야드작업실적송신
        *		A열연 SLAB야드 차량 상차시 마지막 slab권화 완료시 공정으로 JMS 송신
        * 
        * param JDTORecord 	: 전문항목
        *
        * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */      
	public boolean callLastWorkInfo_BSlab(JDTORecord jtR){

		Boolean isSuccess = new Boolean(false);
		logger.println(LogLevel.DEBUG,this, "put 처리시 JDTORecord 내용:"+jtR);			

		try{
			
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			if(YmCommonConst.EQUIP_KIND_PT.equals(jtR.getFieldString("Put_Position").substring(2, 4)))
			{
				//마지막 상차  Slab인지 대상 조회
				JDTORecord jRecord =  dao.getlayerstatinfo(jtR.getFieldString("Put_Position").substring(0, 6));
				logger.println(LogLevel.DEBUG,this, "PALLET 적치 가능한 갯수"+ StringHelper.evl(jRecord.getFieldString("ECOUNT"),""));
				if(jRecord.getFieldInt("ECOUNT") >= 0 ){
					logger.println(LogLevel.DEBUG,this, "마지막 SLAB 권하 완료 - > 공정으로 정보 송신");
					EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
					isSuccess = (Boolean)ejbConn.trx("SendYMPM002", new Class[]{String.class}, new Object[]{jtR.getFieldString("Put_Position").substring(0, 6)});
				}
				logger.println(LogLevel.DEBUG,this, "차량 상차 대기중"+ jRecord);
				return isSuccess.booleanValue();
			}
			return true;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}
	
	

	
	/**
	 * 오퍼레이션명 : 
	 *
	 *	2.	SLAB INFO
        *		야드작업실적송신
        *		B열연 SLAB야드 차량 상/하차시 마지막 상하차개시/완료실적 구내운송으로 송신
        * 
        * param JDTORecord 	: 전문항목
        *
        * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */      
	public boolean callStartLastWorkInfo_BSlab(JDTORecord jtR){

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		Boolean isSuccess = new Boolean(false);
		
		String Sch_Code = StringHelper.evl(jtR.getFieldString("Sch_Code"), "");
 		if(YmCommonConst.NEW_SCH_WORK_KIND_SVML.equals(Sch_Code) || 
 		   YmCommonConst.NEW_SCH_WORK_KIND_SVMU.equals(Sch_Code)){
 		}else{
 			logger.println(LogLevel.DEBUG,this, "내부IF호출===이송상/하차 작업아님. 작업실적전송 SKIP.===");
			return false;
 		}
		String tWork_Id 		= YmCommonUtil.getCurDataWithLegacy(StringHelper.evl(jtR.getFieldString("Work_Id"), ""));
		if(YmCommonConst.SUB_WORK_S.equals(tWork_Id)){
			logger.println(LogLevel.DEBUG,this, "내부IF호출===보조작업대상으로 작업실적전송 SKIP.===");
			return false;
		}
		
		logger.println(LogLevel.DEBUG,this, "put 처리시 JDTORecord 내용:"+jtR);			

		ymCommonDAO dao = ymCommonDAO.getInstance();
		YdStockDAO ydStockDAO = new YdStockDAO();
		List FrtStockList = null;
		List FrtStockList2 = null;
		List FrtoProductList = null;
		List FrtoProductList2 = null;
		List FrtoProductList3 = null;
		List FrtoProductList4 = null;
		List car_sch_List = null;
		
		String stkBed = "";
		String stkLayer = "";
		String grobal_stkBed = "";
		String grobal_stkLayer = "";
		String grobal_stk_col = "";
		String stk_col = "";
		String car_sch_ID = "";
		 String  szPT_TB_COMM = "";
		String slab_prod_GP = ""; 
	    List FrtostlList = null;
	    List FrtostlList2 = null;
	    String CurrProg_CD = "";
	    
	    CoilSpecRegSeEJBBean CoilSpecRegSeEJBBean=new CoilSpecRegSeEJBBean();
			
		int tot_Qty = 0;
		int work_Qty = 0;
		int count = 0;
		
		// B열연 상차개시/완료
		try{
			if(YmCommonConst.EQUIP_KIND_PT.equals(jtR.getFieldString("Put_Position").substring(2, 4))
					||YmCommonConst.EQUIP_KIND_TR.equals(jtR.getFieldString("Put_Position").substring(2, 4)))
			{
				String slab_no = StringHelper.evl(jtR.getFieldString("Slab_No"), "").trim();
				String sGstockId	= StringHelper.evl(jtR.getFieldString("GROBAL_STOCK_ID"), "");
				String sGlocation	= StringHelper.evl(jtR.getFieldString("GROBAL_LOCATION"), "");
				stk_col = StringHelper.evl(jtR.getFieldString("Put_Position").substring(0,6), "");
				
				String queryID = "ym.facilitywork.putwrecord.session.getListworkQty";
	    		
	    		FrtoProductList = dao.getCommonList(queryID, new Object[]{slab_no,slab_no});
	    		
	    		if(FrtoProductList.size() > 0){
			    	JDTORecord StkColRec = (JDTORecord)FrtoProductList.get(0);
			    	tot_Qty  	= StkColRec.getFieldInt("TOT_QTY");
			    	work_Qty  	= StkColRec.getFieldInt("WORK_QTY");
	    		}
	    		
	    		/*
	    		 * 두 매 작업체크(현재 재료정보와 WBOOK_SCH_ACT_DDTT가 동일한 숫자를 가져와서 
	    		 * 
	    		 * 
	    		 * 
	    		 */
	    		
	    		int iCount = 1;
	    		if(!"".equals(sGstockId)) iCount = 2;  //두매작업
	    		
	    		if(iCount == 2)
	    		{	    			
	    			 grobal_stkBed  = StringHelper.evl(jtR.getFieldString("GROBAL_LOCATION").substring(6,8), "");
	    			    grobal_stkLayer  = StringHelper.evl(jtR.getFieldString("GROBAL_LOCATION").substring(8,10), "");
	    			
	    			    grobal_stk_col = StringHelper.evl(jtR.getFieldString("GROBAL_LOCATION").substring(0,6), "");
	    			    queryID = "ym.facilitywork.putwrecord.session.getWlocPntCD";
		    		    car_sch_List = dao.getCommonList(queryID, new Object[]{grobal_stk_col});
		    		    if(car_sch_List.size() > 0){
				    	    JDTORecord carschrec = (JDTORecord)car_sch_List.get(0);
				    	    car_sch_ID  = StringHelper.evl(carschrec.getFieldString("YD_CAR_SCH_ID"), "");
		    		    }
	
		    		    queryID 		= "ym.tsinfo.insertCarftmtl_Dtl";
			    	    int iSeq = dao.insertData(queryID, new Object[]{ car_sch_ID,
			    			                                            sGstockId,
			    			                                            grobal_stkBed,
			    			                                            "0"+grobal_stkLayer}); 
	    			
	    		}

	    		stkBed  = StringHelper.evl(jtR.getFieldString("Put_Position").substring(6,8), "");
    		    stkLayer  = StringHelper.evl(jtR.getFieldString("Put_Position").substring(8,10), "");
    		    stk_col = StringHelper.evl(jtR.getFieldString("Put_Position").substring(0,6), "");
    		    queryID = "ym.facilitywork.putwrecord.session.getWlocPntCD";
    		    car_sch_List = dao.getCommonList(queryID, new Object[]{stk_col});
    		    if(car_sch_List.size() > 0){
		    	    JDTORecord carschrec = (JDTORecord)car_sch_List.get(0);
		    	    car_sch_ID  = StringHelper.evl(carschrec.getFieldString("YD_CAR_SCH_ID"), "");
    		    }
    		
    		    queryID 		= "ym.tsinfo.insertCarftmtl_Dtl";
	    	    int iSeq = dao.insertData(queryID, new Object[]{ 	car_sch_ID,
	    			                                        slab_no,
	    			                                        stkBed,
	    			                                        "0"+stkLayer}); 
	    	
	    	    queryID = "ym.facilitywork.putwrecord.session.getListworkQty_2";
    		
    		    FrtoProductList = dao.getCommonList(queryID, new Object[]{stk_col,slab_no});
    		
    		    if(FrtoProductList.size() > 0){
		     	    JDTORecord StkColRec = (JDTORecord)FrtoProductList.get(0);
		    	    tot_Qty  = StkColRec.getFieldInt("TOT_QTY");
		    	    work_Qty  = StkColRec.getFieldInt("WORK_QTY");
    		    }
    		
	    	    logger.println(LogLevel.DEBUG,this, "tot_Qty	==="+tot_Qty);
	    	    logger.println(LogLevel.DEBUG,this, "work_Qty	==="+work_Qty);
	    	    logger.println(LogLevel.DEBUG,this, "iCount		==="+iCount);
	    			    			  
	    		
	    		if(work_Qty == 1 || (work_Qty == 2 && iCount == 2))
	    		{
	    			//상차개시실적 송신
	    			
	    			//차량스케쥴테이블 - 상차개시시간 업데이트
	    			queryID = "ym.facilitywork.putwrecord.session.updateLoadstartTime";
			    	count = dao.updateData(queryID, new Object[]{slab_no}); 
			    	
			    	queryID	= "ym.facilitywork.putwrecord.session.getListTcconst";
			    	FrtoProductList = dao.getCommonList(queryID, new Object[]{slab_no});
			    	JDTORecord FrtoProduct = (JDTORecord)FrtoProductList.get(0);
			    
		    		
                    JDTORecord tcRecord = null;
					tcRecord = JDTORecordFactory.getInstance().create(); 
					tcRecord.setField("JMS_TC_CD", "YDTSJ007");
					tcRecord.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));					
					tcRecord.setField("TRN_EQP_CD", StringHelper.evl(FrtoProduct.getFieldString("TRN_EQP_CD"), ""));
					tcRecord.setField("SPOS_WLOC_CD", StringHelper.evl(FrtoProduct.getFieldString("SPOS_WLOC_CD"), ""));
					tcRecord.setField("SPOS_YD_PNT_CD", StringHelper.evl(FrtoProduct.getFieldString("YD_PNT_CD"), ""));
					tcRecord.setField("ARR_WLOC_CD", StringHelper.evl(FrtoProduct.getFieldString("ARR_WLOC_CD"), ""));
					tcRecord.setField("TRN_WRK_ST_DT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
					
					EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
					isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{JDTORecord.class},
					  	  	 new Object[]{tcRecord});
					logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 상차개시실적.===");

	    		}
	    		
	    		
	    		
// 2011.02.14 chito 상차완료 수동처리로 변경 	    		
	    		if(tot_Qty == work_Qty)
    		    {
 		    
			        	//상위 단정보 업데이트				    		
		    	    	stkBed  = StringHelper.evl(jtR.getFieldString("Put_Position").substring(6,8), "");
		    	    	stkLayer  = StringHelper.evl(jtR.getFieldString("Put_Position").substring(8,10), "");
		    	    	stk_col = StringHelper.evl(jtR.getFieldString("Put_Position").substring(0,6), "");
				    	String stkQueryId = "ym.facilitywork.putwrecord.session.updateUpperlayerStat";
				    	iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ stk_col,stkBed,stkLayer});
				    	/*
				    	 * update TB_YM_STACKLAYER
                           set STACK_LAYER_STAT = 'V'
                           where STACK_COL_GP = :STACK_COL_GP
                           and STACK_BED_GP = :STACK_BED_GP
                           and STACK_LAYER_GP > :STACK_LAYER_GP
				    	 */
			        	logger.println(LogLevel.DEBUG,this, "===상차완료단정보=== COL : "+stk_col+" BED :"+stkBed+" LAYER : "+stkLayer);
 
    	    	} 	    		
			}
			//B열연 하차개시/완료(상차완료는 백업처리토록 변경)
			if((!YmCommonConst.EQUIP_KIND_PT.equals(jtR.getFieldString("Put_Position").substring(2, 4)) && !YmCommonConst.EQUIP_KIND_TR.equals(jtR.getFieldString("Put_Position").substring(2, 4)))
					   &&
					   (YmCommonConst.NEW_SCH_WORK_KIND_SVMU.equals(Sch_Code)   //이송하차
					    	
					    )
					   )
				{
                String slab_no = StringHelper.evl(jtR.getFieldString("Slab_No"), "").trim();
        		String sGstockId	= StringHelper.evl(jtR.getFieldString("GROBAL_STOCK_ID"), "");
        		String sGlocation	= StringHelper.evl(jtR.getFieldString("GROBAL_LOCATION"), "");
				
				String queryID = "ym.facilitywork.putwrecord.session.getListworkQty_1";
	    	
	    		FrtoProductList = dao.getCommonList(queryID, new Object[]{slab_no,slab_no});
				
	    		if(FrtoProductList.size() > 0){
			    	JDTORecord StkColRec = (JDTORecord)FrtoProductList.get(0);
			    	tot_Qty  	= StkColRec.getFieldInt("TOT_QTY");
			    	work_Qty  	= StkColRec.getFieldInt("WORK_QTY");
	    		}
	    		
	    		
	    		
	    		
	    		int iCount = 1;
	    		if(!"".equals(sGstockId)) iCount = 2;  //두매작업
	    		
	    		logger.println(LogLevel.DEBUG,this, "하차완료처리 ====재료단위 재료공통 테이블 업데이트.===");
	    		if(iCount == 2)	//두매작업일경우 첫번째재료 처리    			
	    		{
	    			
//	    			TB_PT_STLFRTOMOVE update 				
					String stkQueryId = "ym.facilitywork.putwrecord.session.updateunLoadTimeToPT";
					int iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ sGstockId, 
							                                                          sGstockId});
	    			
//	    			주편공통에서 현재 재료상태 확인						
					queryID	= "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getMSLABCOMM";
					FrtStockList = dao.getCommonList(queryID, new Object[]{sGstockId});
					
					if(FrtStockList.size()>0)
					{						
				    	JDTORecord FrtStockreq = (JDTORecord)FrtStockList.get(0);
				    	
				    	CurrProg_CD = StringHelper.evl(FrtStockreq.getFieldString("RECORD_PROG_STAT"),"");
				    	
				    	if(CurrProg_CD.equals("3"))
				    	{
				    	 	logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶슬라브공통 업데이트처리 메소드 분리1◀◀◀◀◀◀◀◀◀◀"); 
				    	 	EJBConnector ejbConn = new EJBConnector("default","JNDICPutResReg",this);
				    	 	Boolean isYd = (Boolean)ejbConn.trx("updateMatlFtmvWlrstSlab",new  Class[]{String.class},
																		new Object[]{sGstockId});
							
							
				    	}
				    	else
				    	{
				    		logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶주편공통 업데이트처리 메소드 분리2◀◀◀◀◀◀◀◀◀◀"); 
				    	 	EJBConnector ejbConn = new EJBConnector("default","JNDICPutResReg",this);
				    	 	Boolean isYd = (Boolean)ejbConn.trx("updateMatlFtmvWlrstMSlab",new  Class[]{String.class},
																		new Object[]{sGstockId});
						    
						    	 				    
				    	 	String sScarfingYn 		= StringHelper.evl(FrtStockreq.getFieldString("SCARFING_YN"),"");
				    	 	String sOrdYeojaeGp 	= StringHelper.evl(FrtStockreq.getFieldString("ORD_YEOJAE_GP"),"");
				    	 	String sSlabCreateGp 	= StringHelper.evl(FrtStockreq.getFieldString("SLAB_CREATE_GP"),"");
				    	 	
				    	 	logger.println(LogLevel.DEBUG,this,"▶▶정정작업 sScarfingYn    ◀◀"+sScarfingYn);
				    	 	logger.println(LogLevel.DEBUG,this,"▶▶정정작업 sOrdYeojaeGp◀◀"+sOrdYeojaeGp);
				    	 	logger.println(LogLevel.DEBUG,this,"▶▶정정작업 sSlabCreateGp◀◀"+sSlabCreateGp);
				    
				    	    slab_prod_GP = sGstockId.substring(0, 1); //슬라브 생산공장구분
				    	 	
				    	    if(slab_prod_GP.equals("M"))
				    	    {
				    	 	  // 일단 처리 없이 SKIP
				    	    }
				    	    else
				    	    {
				    	    	if("N".equals(sScarfingYn)){	
					    	 		if("G".equals(sSlabCreateGp)&&"2".equals(sOrdYeojaeGp)){	
					    	 		}else{
				    	 			/*
						    	     * B열연 정정마감실적 송신
						    	     * 조건 : Non Scarfing 대상재(단,구입재이면서 여재인것은 제외)
						    	     */
					    			JDTORecord tEndRecord = null;
					    			tEndRecord = JDTORecordFactory.getInstance().create(); 
					    			tEndRecord.setField("JMS_TC_CD", "YMCSJ001");
					    			tEndRecord.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));						
					    			tEndRecord.setField("MSLAB_NO",sGstockId);
							
									EJBConnector ejbConn2 = new EJBConnector("default","JNDIYardWrkResReg",this);
									isSuccess = (Boolean)ejbConn2.trx("sendInternalModel",new Class[]{JDTORecord.class},
									  	  	 new Object[]{tEndRecord});
									logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 B열연 정정마감실적.===");
					    	 		}
					    	 	}
				    	    	
				    	    }			    	    				    	 						    				    
				    	}
					}else
					{
						logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶슬라브공통 업데이트처리 메소드 분리3◀◀◀◀◀◀◀◀◀◀"); 
			    	 	EJBConnector ejbConn = new EJBConnector("default","JNDICPutResReg",this);
			    	 	Boolean isYd = (Boolean)ejbConn.trx("updateMatlFtmvWlrstSlab",new  Class[]{String.class},
																	new Object[]{sGstockId});																		
					}
	 
					//공통에 전문 전송(YDCTJ032, YDPTJ001)
		    	 	this.SlabYDPTJ001Send(sGstockId);
	    		}
	    		
	    		
	    		
	    		//한매작업 - 주편공통에서 현재 진도코드 확인													
				queryID	= "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getMSLABCOMM";
				FrtStockList2 = dao.getCommonList(queryID, new Object[]{slab_no});
				
				
				if(FrtStockList2.size()>0)
				{
				
			    	JDTORecord FrtStockreq2 = (JDTORecord)FrtStockList2.get(0);
			    	
			    	//TB_PT_STLFRTOMOVE update 				
					String stkQueryId = "ym.facilitywork.putwrecord.session.updateunLoadTimeToPT";
					int iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ slab_no, 
							                                                          slab_no});
			    	
			    	CurrProg_CD = StringHelper.evl(FrtStockreq2.getFieldString("RECORD_PROG_STAT"),"");
			    	
			    	if(CurrProg_CD.equals("3"))
			    	{
			    		/*하차완료 후 재료공통 테이블 업데이트(슬라브공통)
			    		String stkQueryId = "ym.facilitywork.putwrecord.session.updateMatlFtmvWlrstSlab";
						int iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ slab_no,slab_no});	*/
						
						logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶슬라브공통 업데이트처리 메소드 분리4◀◀◀◀◀◀◀◀◀◀"); 
			    	 	EJBConnector ejbConn = new EJBConnector("default","JNDICPutResReg",this);
			    	 	Boolean isYd = (Boolean)ejbConn.trx("updateMatlFtmvWlrstSlab",new  Class[]{String.class},
																	new Object[]{slab_no});
			    	}
			    	else
			    	{
			    		/*하차완료 후 재료공통 테이블 업데이트(주편공통
			    		String stkQueryId = "ym.facilitywork.putwrecord.session.updateMatlFtmvWlrstMSlab"; 
					    int iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ slab_no});*/
					    
			    	 	logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶주편공통 업데이트처리 메소드 분리5◀◀◀◀◀◀◀◀◀◀"); 
			    	 	EJBConnector ejbConn = new EJBConnector("default","JNDICPutResReg",this);
			    	 	Boolean isYd = (Boolean)ejbConn.trx("updateMatlFtmvWlrstMSlab",new  Class[]{String.class},
																	new Object[]{slab_no});
					    
			    	 	String sScarfingYn 		= StringHelper.evl(FrtStockreq2.getFieldString("SCARFING_YN"),"");
			    	 	String sOrdYeojaeGp 	= StringHelper.evl(FrtStockreq2.getFieldString("ORD_YEOJAE_GP"),"");
			    	 	String sSlabCreateGp 	= StringHelper.evl(FrtStockreq2.getFieldString("SLAB_CREATE_GP"),"");
			    	 	
			    	 	logger.println(LogLevel.DEBUG,this,"▶▶정정작업 sScarfingYn    ◀◀"+sScarfingYn);
			    	 	logger.println(LogLevel.DEBUG,this,"▶▶정정작업 sOrdYeojaeGp◀◀"+sOrdYeojaeGp);
			    	 	logger.println(LogLevel.DEBUG,this,"▶▶정정작업 sSlabCreateGp◀◀"+sSlabCreateGp);
			    	 	
			    	 	
			    	 	slab_prod_GP = slab_no.substring(0, 1); //슬라브 생산공장구분
			    	 	
			    	    if(slab_prod_GP.equals("M"))
			    	    {
			    	 	  // 일단 처리 없이 SKIP
			    	    }
			    	    else
			    	    {
			    	    	if("N".equals(sScarfingYn)){	
				    	 		if("G".equals(sSlabCreateGp)&&"2".equals(sOrdYeojaeGp)){	
				    	 		}else{
			    	 			/*
					    	     * B열연 정정마감실적 송신
					    	     * 조건 : Non Scarfing 대상재(단,구입재이면서 여재인것은 제외)
					    	     */
				    			JDTORecord tEndRecord = null;
				    			tEndRecord = JDTORecordFactory.getInstance().create(); 
				    			tEndRecord.setField("JMS_TC_CD", "YMCSJ001");
				    			tEndRecord.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));						
				    			tEndRecord.setField("MSLAB_NO",slab_no);
						
								EJBConnector ejbConn2 = new EJBConnector("default","JNDIYardWrkResReg",this);
								isSuccess = (Boolean)ejbConn2.trx("sendInternalModel",new Class[]{JDTORecord.class},
								  	  	 new Object[]{tEndRecord});
								logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 B열연 정정마감실적.===");
				    	 		}
				    	 	}
			    	    	
			    	    }		    	 			    	 	
			    	 
			    	}
				}
				else{
//					TB_PT_STLFRTOMOVE update 				
					String stkQueryId = "ym.facilitywork.putwrecord.session.updateunLoadTimeToPT";
					int iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ slab_no, 
							                                                          slab_no});
					
					logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶슬라브공통 업데이트처리 메소드 분리6◀◀◀◀◀◀◀◀◀◀"); 
		    	 	EJBConnector ejbConn = new EJBConnector("default","JNDICPutResReg",this);
		    	 	Boolean isYd = (Boolean)ejbConn.trx("updateMatlFtmvWlrstSlab",new  Class[]{String.class},
																new Object[]{slab_no});
						
				}
				
				//공통에 전문 전송(YDCTJ032, YDPTJ001)
	    	 	this.SlabYDPTJ001Send(slab_no);	
	    		
    			    		
	    		if(work_Qty == 1 || (work_Qty == 2 && iCount == 2))
	    		{
	    			//차량스케쥴테이블 - 하차개시시간 업데이트
	    			queryID = "ym.facilitywork.putwrecord.session.updateUnloadstartTime";
			    	count = dao.updateData(queryID, new Object[]{slab_no}); 
			    	
			    	queryID	= "ym.facilitywork.putwrecord.session.getListTcconst_1";
			    	FrtoProductList = dao.getCommonList(queryID, new Object[]{slab_no});
			    	JDTORecord FrtoProduct = (JDTORecord)FrtoProductList.get(0);
			    	
	    			
	    			//하차개시실적 송신
	    			//소재차량하차개시
	    			JDTORecord tcRecord = null;
					tcRecord = JDTORecordFactory.getInstance().create(); 
					tcRecord.setField("JMS_TC_CD", "YDTSJ009");
					tcRecord.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
					
					tcRecord.setField("TRN_EQP_CD", StringHelper.evl(FrtoProduct.getFieldString("TRN_EQP_CD"), ""));
					tcRecord.setField("ARR_WLOC_CD", StringHelper.evl(FrtoProduct.getFieldString("ARR_WLOC_CD"), ""));
					tcRecord.setField("ARR_YD_PNT_CD", StringHelper.evl(FrtoProduct.getFieldString("YD_PNT_CD"), ""));
					tcRecord.setField("TRN_WRK_ST_DT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
			
					EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
					isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{JDTORecord.class},
					  	  	 new Object[]{tcRecord});
					logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 하차개시실적.===");

	    		}
	    		int CurrStk_Layer = 0;
	    		if(iCount == 2)	//두매작업일경우 첫번째재료 처리    			
	    		{ 
	    			logger.println(LogLevel.DEBUG,this, "===========두매작업임. 아래 슬라브의 단정보를 이용===");
	    		      queryID	= "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getLastworkYn";
				      List FrtoProduct5= dao.getCommonList(queryID, new Object[]{sGstockId});
		    	      JDTORecord FrtStockreq3 = (JDTORecord)FrtoProduct5.get(0);
 	
		    	      CurrStk_Layer = FrtStockreq3.getFieldInt("YD_STK_LYR_NO");
		    	      logger.println(LogLevel.DEBUG,this, "============== 권하처리 슬라브번호 .===" +sGstockId);
		    	      logger.println(LogLevel.DEBUG,this, "============== 권하처리 슬라브의 단정보 .===" +CurrStk_Layer);
	    		}
	    		else
	    		{	    		
	    		    queryID	= "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getLastworkYn";
				    List FrtoProduct5= dao.getCommonList(queryID, new Object[]{slab_no});
		    	    JDTORecord FrtStockreq3 = (JDTORecord)FrtoProduct5.get(0);
 	
		    	    CurrStk_Layer = FrtStockreq3.getFieldInt("YD_STK_LYR_NO");
		    	    logger.println(LogLevel.DEBUG,this, "============== 권하처리 슬라브번호 .===" +slab_no);
		    	    logger.println(LogLevel.DEBUG,this, "============== 권하처리 슬라브의 단정보 .===" +CurrStk_Layer);
	    		}
	    		
	    		if(CurrStk_Layer == 1)
	    	//	if(tot_Qty == work_Qty)
	    		{	
	    			logger.println(LogLevel.DEBUG,this, "============== 권하처리 슬라브의 단정보가 001이므로 하차완료처리.===");
			    	queryID	= "ym.facilitywork.putwrecord.session.getListTcconst_1";
			    	FrtoProductList = dao.getCommonList(queryID, new Object[]{slab_no});
			    	JDTORecord FrtoProduct = (JDTORecord)FrtoProductList.get(0);
			    	
			    	//차량스케쥴ID로 이송재료 조회(
		    		String trnQueryId = "ym.tsinfo.getListFrtostlList";
		    		FrtostlList = dao.getCommonList(trnQueryId, new Object[]{StringHelper.evl(FrtoProduct.getFieldString("TRN_EQP_CD"), "")});
		    		/*
		    		 * SELECT STL_NO,SUBSTR(YD_STK_LYR_NO,2,2)STK_LYR
	                   FROM TB_YD_CARFTMVMTL A, TB_YD_CARSCH B
	                   WHERE A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
	                   AND A.DEL_YN = 'N'
	                   AND B.TRN_EQP_CD = ?
	                   ORDER BY A.YD_STK_LYR_NO 
		    		 */
		    				    				    		
	    			
		    		//차량스케쥴테이블 - 하차완료시간 업데이트
	    			queryID = "ym.facilitywork.putwrecord.session.updateUnloadendTime";
			    	count = dao.updateData(queryID, new Object[]{slab_no});
			    	
			    	//차량재료테이블 - DEL_YN 업데이트
	    			queryID = "ym.facilitywork.putwrecord.session.deletefrmtList";
			    	count = dao.updateData(queryID, new Object[]{slab_no});
			    	
		    		int iSeqCount 	= FrtostlList.size();
//		    		
			    	   			
	    			//하차완료실적 송신
     			    //소재차량하차완료
	    			JDTORecord tcRecord = null;
					tcRecord = JDTORecordFactory.getInstance().create(); 
					tcRecord.setField("JMS_TC_CD", "YDTSJ010");
					tcRecord.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
					tcRecord.setField("TRN_EQP_CD", StringHelper.evl(FrtoProduct.getFieldString("TRN_EQP_CD"), ""));
					tcRecord.setField("ARR_WLOC_CD", StringHelper.evl(FrtoProduct.getFieldString("ARR_WLOC_CD"), ""));
					tcRecord.setField("ARR_YD_PNT_CD", StringHelper.evl(FrtoProduct.getFieldString("YD_PNT_CD"), ""));
					tcRecord.setField("CARUD_CMPL_DT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
 				
					EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
					isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{JDTORecord.class},
					  	  	 new Object[]{tcRecord});
					logger.println(LogLevel.DEBUG,this, "내부IF호출===슬라브 일관제철하차완료실적.===");

	    		}
				
			}

			return isSuccess.booleanValue();
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}
	
	
	/**
	 * 오퍼레이션명 : 
	 *
	 *	2.	SLAB INFO
        *		야드작업실적송신
        *		B열연 SLAB야드 차량 상/하차시 마지막 상하차개시/완료실적 구내운송으로 송신
        * 
        * param JDTORecord 	: 전문항목
        *
        * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */      
	public boolean callStartLastWorkInfo_ASlab(JDTORecord jtR){

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		Boolean isSuccess = new Boolean(false);
		logger.println(LogLevel.DEBUG,this, "put 처리시 JDTORecord 내용:"+jtR);			

		ymCommonDAO dao = ymCommonDAO.getInstance();
		YdStockDAO ydStockDAO = new YdStockDAO();
		CoilSpecRegSeEJBBean CoilSpecRegSeEJBBean=new CoilSpecRegSeEJBBean();
		List FrtostlList = null;
		List FrtStockList = null;
		List FrtoProductList = null;

		String stkBed = "";
		String stkLayer = "";
		String stk_col = "";
		String grobal_stkBed = "";
		String grobal_stkLayer = "";
		String grobal_stk_col = "";
		String car_sch_ID = "";
		List car_sch_List = null;
		List FrtoProductList2 = null;
		List mtl_List = null;
		List FrtStockList2 = null;
		List FrtoProductList3 = null;
		List FrtoProductList4 = null;
		String Sch_Code = StringHelper.evl(jtR.getFieldString("Sch_Code"), "");
		
		String CurrProg_CD ="";
		String slab_prod_GP = "";
			
		int tot_Qty = 0;
		int work_Qty = 0;
		int count = 0;
		int iSeq = 0;
		
		//A열연 상차개시/완료
		try{
			if(YmCommonConst.EQUIP_KIND_PT.equals(jtR.getFieldString("Put_Position").substring(2, 4))
						||YmCommonConst.EQUIP_KIND_TR.equals(jtR.getFieldString("Put_Position").substring(2, 4)))
			{
				String slab_no = StringHelper.evl(jtR.getFieldString("Slab_No"), "").trim();
				String sGstockId	= StringHelper.evl(jtR.getFieldString("GROBAL_STOCK_ID"), "");
				String sGlocation	= StringHelper.evl(jtR.getFieldString("GROBAL_LOCATION"), "");
				stk_col = StringHelper.evl(jtR.getFieldString("Put_Position").substring(0,6), "");
				

				//재료가 이송재료테이블(TB_YD_CARFTMVMTL)에 있는지 확인을 한다.(직상차와 자동편성의 경우 처리가 달라짐)
				
				logger.println(LogLevel.DEBUG,this, "====================이송재료테이블(TB_YD_CARFTMVMTL)에 있는지 확인");
				String queryID = "";
				queryID = "ym.facilitywork.putwrecord.session.loadingLotConYN";
				mtl_List = dao.getCommonList(queryID, new Object[]{slab_no});
				/*
				 * SELECT STL_NO
                   from TB_YD_CARFTMVMTL
                   where STL_NO = :STL_NO
                   and DEL_YN = 'N'
				 */
				int iSeqCount 	= mtl_List.size();
				logger.println(LogLevel.DEBUG,this, "===================BCast이송상차 재료번호 :" + slab_no+ "이송재료테이블 조회 건수 :" + iSeqCount);
				if(iSeqCount>0)//이송재료테이블(TB_YD_CARFTMVMTL)에 해당재료 존재(BCast자동편성처리 된 경우
				{
					logger.println(LogLevel.DEBUG,this, "=====================이송재료테이블(TB_YD_CARFTMVMTL)에 해당재료 존재======BCast 자동편성된 경우 처리시작 ");
										
					queryID = "ym.facilitywork.putwrecord.session.getListworkQty";
		    		
		    		FrtoProductList = dao.getCommonList(queryID, new Object[]{slab_no,slab_no});
		    		
		    		if(FrtoProductList.size() > 0){
				    	JDTORecord StkColRec = (JDTORecord)FrtoProductList.get(0);
				    	tot_Qty  	= StkColRec.getFieldInt("TOT_QTY");
				    	work_Qty  	= StkColRec.getFieldInt("WORK_QTY");
		    		}

	    		    int iCount = 1;
	    		    if(!"".equals(sGstockId)) iCount = 2;  //두매작업
	    			    		   	    		 		    		
		    		
		    		if(iCount == 2)
		    		{	    			
		    			grobal_stkBed  = StringHelper.evl(jtR.getFieldString("GROBAL_LOCATION").substring(6,8), "");
		    			grobal_stkLayer  = StringHelper.evl(jtR.getFieldString("GROBAL_LOCATION").substring(8,10), "");
			    		queryID = "ym.facilitywork.putwrecord.session.updateCarftmtl";
				    	count = dao.updateData(queryID, new Object[]{grobal_stkBed,"0"+grobal_stkLayer,sGstockId}); 
		    			
		    		}

		    		//차량재료정보에 Bed/Layer 업데이트
		    		stkBed  = StringHelper.evl(jtR.getFieldString("Put_Position").substring(6,8), "");
		    		stkLayer  = StringHelper.evl(jtR.getFieldString("Put_Position").substring(8,10), "");
		    		queryID = "ym.facilitywork.putwrecord.session.updateCarftmtl";
			    	count = dao.updateData(queryID, new Object[]{stkBed,"0"+stkLayer,slab_no}); 
   	    
		    	    logger.println(LogLevel.DEBUG,this, "tot_Qty	==="+tot_Qty);
		    	    logger.println(LogLevel.DEBUG,this, "work_Qty	==="+work_Qty);
		    	    logger.println(LogLevel.DEBUG,this, "iCount		==="+iCount);
		    	    
		    	    		    	    	    		     
		    	    if(work_Qty == 1 || (work_Qty == 2 && iCount == 2))
	    		    {
                        //상차개시실적 송신
	    			
	    			    //차량스케쥴테이블 - 상차개시시간 업데이트
	    			    queryID = "ym.facilitywork.putwrecord.session.updateLoadstartTime";
			    	    count = dao.updateData(queryID, new Object[]{slab_no}); 
			    	
			    	    queryID	= "ym.facilitywork.putwrecord.session.getListTcconst";
			    	    FrtoProductList = dao.getCommonList(queryID, new Object[]{slab_no});
			    	    JDTORecord FrtoProduct = (JDTORecord)FrtoProductList.get(0);
			    
		    		
                        JDTORecord tcRecord = null;
					    tcRecord = JDTORecordFactory.getInstance().create(); 
					    tcRecord.setField("JMS_TC_CD", "YDTSJ007");
					    tcRecord.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));					
					    tcRecord.setField("TRN_EQP_CD", StringHelper.evl(FrtoProduct.getFieldString("TRN_EQP_CD"), ""));
					    tcRecord.setField("SPOS_WLOC_CD", StringHelper.evl(FrtoProduct.getFieldString("SPOS_WLOC_CD"), ""));
					    tcRecord.setField("SPOS_YD_PNT_CD", StringHelper.evl(FrtoProduct.getFieldString("YD_PNT_CD"), ""));
					    tcRecord.setField("ARR_WLOC_CD", StringHelper.evl(FrtoProduct.getFieldString("ARR_WLOC_CD"), ""));
					    tcRecord.setField("TRN_WRK_ST_DT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
					
					    EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
					    isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{JDTORecord.class},
					      	  	 new Object[]{tcRecord});
					    logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 상차개시실적.===");
		
	    	    	}
	    		
	    		    if(tot_Qty == work_Qty)
	    		    {
	    		    	int nIdx = 0;
	    			
//	    		     	차량스케쥴테이블 - 상차완료시간 업데이트
	    		    	queryID = "ym.facilitywork.putwrecord.session.updateLoadendTime";
			        	count = dao.updateData(queryID, new Object[]{slab_no}); 
			        	queryID	= "ym.facilitywork.putwrecord.session.getListTcconst";
			        	FrtoProductList = dao.getCommonList(queryID, new Object[]{slab_no});
			        	JDTORecord FrtoProduct = (JDTORecord)FrtoProductList.get(0);
			        	//차량스케쥴ID로 이송재료 조회(
		        		String trnQueryId = "ym.tsinfo.getListFrtostlList_loadEnd_PIDEV";
		        		FrtostlList = dao.getCommonList(trnQueryId, new Object[]{StringHelper.evl(FrtoProduct.getFieldString("TRN_EQP_CD"), "")});
		    		
		        		JDTORecord tcRecord = null;
		        		iSeqCount 	= FrtostlList.size();
		        		String szPT_TB_COMM = "";
		        		for(int i=0; i < iSeqCount ; i++){
						
			        		JDTORecord FrtoSltrec = (JDTORecord)FrtostlList.get(i);
			    			String s_STOCK_ID = StringHelper.evl(FrtoSltrec.getFieldString("STL_NO"),"").trim();
						
			    			//TB_PT_STLFRTOMOVE update				
			    			String stkQueryId = "ym.facilitywork.putwrecord.session.updateLoadTimeToPT";
				    		iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ s_STOCK_ID, 
								                                                          s_STOCK_ID});
						
				    		//주편공통에서 현재 진도코드 확인						
				    		queryID	= "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getMSLABCOMM";
				        	FrtoProductList2 = dao.getCommonList(queryID, new Object[]{s_STOCK_ID});
	    			    	JDTORecord FrtoProduct2 = (JDTORecord)FrtoProductList2.get(0);
				    	
				        	CurrProg_CD = StringHelper.evl(FrtoProduct2.getFieldString("RECORD_PROG_STAT"),"");
				    	
				        	if(CurrProg_CD.equals("3"))
				        	{
				        		stkQueryId = "ym.facilitywork.putwrecord.session.updateMatlFtmvTimeSlab";
						    	iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ s_STOCK_ID});
						    	szPT_TB_COMM = "S";
				        	}
				        	
				        	else
				        	{
				        		stkQueryId = "ym.facilitywork.putwrecord.session.updateMatlFtmvTimeMSlab"; 
					    	    iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ s_STOCK_ID});
					    	    szPT_TB_COMM = "B";
				        	}
					    
				        	//상위 단정보 업데이트				    		
			    	    	stkBed  = StringHelper.evl(jtR.getFieldString("Put_Position").substring(6,8), "");
			    	    	stkLayer  = StringHelper.evl(jtR.getFieldString("Put_Position").substring(8,10), "");
			    	    	stk_col = StringHelper.evl(jtR.getFieldString("Put_Position").substring(0,6), "");
					    	stkQueryId = "ym.facilitywork.putwrecord.session.updateUpperlayerStat";
					    	iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ stk_col,stkBed,stkLayer});
					    	/*
					    	 * update TB_YM_STACKLAYER
                               set STACK_LAYER_STAT = 'V'
                               where STACK_COL_GP = :STACK_COL_GP
                               and STACK_BED_GP = :STACK_BED_GP
                               and STACK_LAYER_GP > :STACK_LAYER_GP
					    	 */
				        	logger.println(LogLevel.DEBUG,this, "===상차완료단정보=== COL : "+stk_col+" BED :"+stkBed+" LAYER : "+stkLayer);
				    	
							    					    	
				    		if(i == 0){
		    		    	   //상차완료실적 송신
	                           //소재차량상차완료
		    		    	   tcRecord = JDTORecordFactory.getInstance().create(); 
					    	   tcRecord.setField("JMS_TC_CD", "YDTSJ008");  //A슬라브 야드
					    	   tcRecord.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));					
					    	   tcRecord.setField("TRN_EQP_CD", StringHelper.evl(FrtoSltrec.getFieldString("TRN_EQP_CD"), ""));
					    	   tcRecord.setField("SPOS_WLOC_CD", StringHelper.evl(FrtoSltrec.getFieldString("SPOS_WLOC_CD"), ""));
					    	   tcRecord.setField("SPOS_YD_PNT_CD", StringHelper.evl(FrtoSltrec.getFieldString("YD_PNT_CD"), ""));
					    	   tcRecord.setField("ARR_WLOC_CD", StringHelper.evl(FrtoSltrec.getFieldString("ARR_WLOC_CD"), ""));
					    	   tcRecord.setField("TRN_WRK_MTL_GP", StringHelper.evl(FrtoSltrec.getFieldString("TRN_WRK_MTL_GP"), ""));
					    	   tcRecord.setField("MTL_UGNT_GP", StringHelper.evl(FrtoSltrec.getFieldString("MTL_UGNT_GP"), ""));
					    	   tcRecord.setField("HCR_GP", StringHelper.evl(FrtoSltrec.getFieldString("HCR_GP"), ""));
					    	   tcRecord.setField("CARLD_SH", Integer.toString(iSeqCount));
					    	}
						    tcRecord.setField("STL_NO" + (1+nIdx), StringHelper.evl(FrtoSltrec.getFieldString("STL_NO"), ""));
						    tcRecord.setField("STL_WT" + (1+nIdx), StringHelper.evl(FrtoSltrec.getFieldString("STL_WT"), ""));
						    nIdx ++;
						    
						    logger.println(LogLevel.DEBUG,this, "상차완료 후 저장품 정보 YD송신.===");
						    
						    
						    
							String s_YD_GP = getYdFromWlocCd(StringHelper.evl(FrtoSltrec.getFieldString("ARR_WLOC_CD"), ""));	
							
							//c열연과 후판,slab간에 분리처리
							if(s_YD_GP.equals("H")){

								CoilSpecRegSeEJBBean.stockProcCom(StringHelper.evl(FrtoSltrec.getFieldString("STL_NO"), ""),1);

							}else {
						        JDTORecord ydStlRecord = null;
						        ydStlRecord = JDTORecordFactory.getInstance().create();
						        ydStlRecord.setField("PT_TB_COMM",szPT_TB_COMM);
						        ydStlRecord.setField("STL_NO", StringHelper.evl(FrtoSltrec.getFieldString("STL_NO"), ""));							        
						        ydStlRecord.setField("SLAB_WO_RT_CD", StringHelper.evl(FrtoSltrec.getFieldString("SLAB_WO_RT_CD"), ""));
						        ydStlRecord.setField("ORD_YEOJAE_GP", StringHelper.evl(FrtoSltrec.getFieldString("ORD_YEOJAE_GP"), ""));
						        ydStlRecord.setField("SCARFING_YN", StringHelper.evl(FrtoSltrec.getFieldString("SCARFING_YN"), ""));
						        ydStlRecord.setField("SCARFING_DONE_YN", StringHelper.evl(FrtoSltrec.getFieldString("SCARFING_DONE_YN"), ""));
						        ydStlRecord.setField("MILL_WO_EXN", StringHelper.evl(FrtoSltrec.getFieldString("MILL_WO_EXN"), ""));					        
						        ydStlRecord.setField("YD_GP", s_YD_GP);					        
						        ydStlRecord.setField("STL_APPEAR_GP", StringHelper.evl(FrtoSltrec.getFieldString("STL_APPEAR_GP"), ""));
						        ydStlRecord.setField("HCR_GP", StringHelper.evl(FrtoSltrec.getFieldString("HCR_GP"), ""));
						        
						        String szRetunMsg = YdCommonUtils.uptStockCodeMapping(ydStlRecord);
							}
				    
		    		    }
		    		
		    	    	tcRecord.setField("CARLD_CMPL_DT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
		    		    EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
				        isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{JDTORecord.class},
				  	  	             new Object[]{tcRecord});
				        logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 상차완료실적.===");
				                

	    	    	}	
				}	
			else
				{
					logger.println(LogLevel.DEBUG,this, "=====================BCast직상차인경우  경우 처리시작 ");
				
	    		    int iCount = 1;
	    		    if(!"".equals(sGstockId)) iCount = 2;  //두매작업
	    		
	    		    if(iCount == 2)
	    		    {	    			
	    			    grobal_stkBed  = StringHelper.evl(jtR.getFieldString("GROBAL_LOCATION").substring(6,8), "");
	    			    grobal_stkLayer  = StringHelper.evl(jtR.getFieldString("GROBAL_LOCATION").substring(8,10), "");
	    			
	    			    grobal_stk_col = StringHelper.evl(jtR.getFieldString("GROBAL_LOCATION").substring(0,6), "");
	    			    queryID = "ym.facilitywork.putwrecord.session.getWlocPntCD";
		    		    car_sch_List = dao.getCommonList(queryID, new Object[]{grobal_stk_col});
		    		    if(car_sch_List.size() > 0){
				    	    JDTORecord carschrec = (JDTORecord)car_sch_List.get(0);
				    	    car_sch_ID  = StringHelper.evl(carschrec.getFieldString("YD_CAR_SCH_ID"), "");
		    		    }
	
		    		    queryID 		= "ym.tsinfo.insertCarftmtl_Dtl";
			    	    iSeq = dao.insertData(queryID, new Object[]{ 	car_sch_ID,
			    			                                            sGstockId,
			    			                                            stkBed,
			    			                                            "0"+stkLayer}); 
	    			
	    	    	}
	    		
	    		    //차량재료정보에 Bed/Layer insert
	    		    stkBed  = StringHelper.evl(jtR.getFieldString("Put_Position").substring(6,8), "");
	    		    stkLayer  = StringHelper.evl(jtR.getFieldString("Put_Position").substring(8,10), "");
	    		    stk_col = StringHelper.evl(jtR.getFieldString("Put_Position").substring(0,6), "");
	    		    queryID = "ym.facilitywork.putwrecord.session.getWlocPntCD";
	    		    car_sch_List = dao.getCommonList(queryID, new Object[]{stk_col});
	    		    if(car_sch_List.size() > 0){
			    	    JDTORecord carschrec = (JDTORecord)car_sch_List.get(0);
			    	    car_sch_ID  = StringHelper.evl(carschrec.getFieldString("YD_CAR_SCH_ID"), "");
	    		    }
	    		
	    		    queryID 		= "ym.tsinfo.insertCarftmtl_Dtl";
		    	    iSeq = dao.insertData(queryID, new Object[]{ 	car_sch_ID,
		    			                                        slab_no,
		    			                                        stkBed,
		    			                                        "0"+stkLayer}); 
		    	
		    	    queryID = "ym.facilitywork.putwrecord.session.getListworkQty_2";
	    		
	    		    FrtoProductList = dao.getCommonList(queryID, new Object[]{stk_col,slab_no});
	    		
	    		    if(FrtoProductList.size() > 0){
			     	    JDTORecord StkColRec = (JDTORecord)FrtoProductList.get(0);
			    	    tot_Qty  = StkColRec.getFieldInt("TOT_QTY");
			    	    work_Qty  = StkColRec.getFieldInt("WORK_QTY");
	    		    }
	    		
		    	    logger.println(LogLevel.DEBUG,this, "tot_Qty	==="+tot_Qty);
		    	    logger.println(LogLevel.DEBUG,this, "work_Qty	==="+work_Qty);
		    	    logger.println(LogLevel.DEBUG,this, "iCount		==="+iCount);
	    		     
		    	    if(work_Qty == 1 || (work_Qty == 2 && iCount == 2))
	    		    {
                        //상차개시실적 송신 
	    			
	    			    //차량스케쥴테이블 - 상차개시시간 업데이트
	    			    queryID = "ym.facilitywork.putwrecord.session.updateLoadstartTime";
			    	    count = dao.updateData(queryID, new Object[]{slab_no}); 
			    	
			    	    queryID	= "ym.facilitywork.putwrecord.session.getListTcconst";
			    	    FrtoProductList = dao.getCommonList(queryID, new Object[]{slab_no});
			    	    JDTORecord FrtoProduct = (JDTORecord)FrtoProductList.get(0);
			    
		    		
                        JDTORecord tcRecord = null;
					    tcRecord = JDTORecordFactory.getInstance().create(); 
					    tcRecord.setField("JMS_TC_CD", "YDTSJ007");
					    tcRecord.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));					
					    tcRecord.setField("TRN_EQP_CD", StringHelper.evl(FrtoProduct.getFieldString("TRN_EQP_CD"), ""));
					    tcRecord.setField("SPOS_WLOC_CD", StringHelper.evl(FrtoProduct.getFieldString("SPOS_WLOC_CD"), ""));
					    tcRecord.setField("SPOS_YD_PNT_CD", StringHelper.evl(FrtoProduct.getFieldString("YD_PNT_CD"), ""));
					    tcRecord.setField("ARR_WLOC_CD", StringHelper.evl(FrtoProduct.getFieldString("ARR_WLOC_CD"), ""));
					    tcRecord.setField("TRN_WRK_ST_DT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
					
					    EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
					    isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{JDTORecord.class},
					      	  	 new Object[]{tcRecord});
					    logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 상차개시실적.===");
		
	    	    	}
	    		
	    		    if(tot_Qty == work_Qty)
	    		    {
	    		    	int nIdx = 0;
	    			
//	    		     	차량스케쥴테이블 - 상차완료시간 업데이트
	    		    	queryID = "ym.facilitywork.putwrecord.session.updateLoadendTime";
			        	count = dao.updateData(queryID, new Object[]{slab_no}); 
			        	queryID	= "ym.facilitywork.putwrecord.session.getListTcconst";
			        	FrtoProductList = dao.getCommonList(queryID, new Object[]{slab_no});
			        	JDTORecord FrtoProduct = (JDTORecord)FrtoProductList.get(0);
			        	//차량스케쥴ID로 이송재료 조회(
		        		String trnQueryId = "ym.tsinfo.getListFrtostlList_loadEnd_PIDEV";
		        		FrtostlList = dao.getCommonList(trnQueryId, new Object[]{StringHelper.evl(FrtoProduct.getFieldString("TRN_EQP_CD"), "")});
		    		
		        		JDTORecord tcRecord = null;
		        		iSeqCount 	= FrtostlList.size();
		        		String szPT_TB_COMM = "";
		        		for(int i=0; i < iSeqCount ; i++){
						
			        		JDTORecord FrtoSltrec = (JDTORecord)FrtostlList.get(i);
			    			String s_STOCK_ID = StringHelper.evl(FrtoSltrec.getFieldString("STL_NO"),"").trim();
						
			    			//TB_PT_STLFRTOMOVE update				
			    			String stkQueryId = "ym.facilitywork.putwrecord.session.updateLoadTimeToPT";
				    		iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ s_STOCK_ID, 
								                                                          s_STOCK_ID});
						
				    		//주편공통에서 현재 진도코드 확인						
				    		queryID	= "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getMSLABCOMM";
				        	FrtoProductList2 = dao.getCommonList(queryID, new Object[]{s_STOCK_ID});
	    			    	JDTORecord FrtoProduct2 = (JDTORecord)FrtoProductList2.get(0);
				    	
				        	CurrProg_CD = StringHelper.evl(FrtoProduct2.getFieldString("RECORD_PROG_STAT"),"");
				    	
				        	if(CurrProg_CD.equals("3"))
				        	{
				        		stkQueryId = "ym.facilitywork.putwrecord.session.updateMatlFtmvTimeSlab";
						    	iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ s_STOCK_ID});
						    	szPT_TB_COMM = "S";
				        	}
				        	
				        	else
				        	{
				        		stkQueryId = "ym.facilitywork.putwrecord.session.updateMatlFtmvTimeMSlab"; 
					    	    iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ s_STOCK_ID});
					    	    szPT_TB_COMM = "B";
				        	}
					    
				        	//상위 단정보 업데이트				    		
			    	    	stkBed  = StringHelper.evl(jtR.getFieldString("Put_Position").substring(6,8), "");
			    	    	stkLayer  = StringHelper.evl(jtR.getFieldString("Put_Position").substring(8,10), "");
			    	    	stk_col = StringHelper.evl(jtR.getFieldString("Put_Position").substring(0,6), "");
					    	stkQueryId = "ym.facilitywork.putwrecord.session.updateUpperlayerStat";
					    	iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ stk_col,stkBed,stkLayer});
					    	/*
					    	 * update TB_YM_STACKLAYER
                               set STACK_LAYER_STAT = 'V'
                               where STACK_COL_GP = :STACK_COL_GP
                               and STACK_BED_GP = :STACK_BED_GP
                               and STACK_LAYER_GP > :STACK_LAYER_GP
					    	 */
				        	logger.println(LogLevel.DEBUG,this, "===상차완료단정보=== COL : "+stk_col+" BED :"+stkBed+" LAYER : "+stkLayer);
				    	
							    					    	
				    		if(i == 0){
		    		    	   //상차완료실적 송신
	                           //소재차량상차완료
		    		    	   tcRecord = JDTORecordFactory.getInstance().create(); 
					    	   tcRecord.setField("JMS_TC_CD", "YDTSJ008");  //A슬라브 야드
					    	   tcRecord.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));					
					    	   tcRecord.setField("TRN_EQP_CD", StringHelper.evl(FrtoSltrec.getFieldString("TRN_EQP_CD"), ""));
					    	   tcRecord.setField("SPOS_WLOC_CD", StringHelper.evl(FrtoSltrec.getFieldString("SPOS_WLOC_CD"), ""));
					    	   tcRecord.setField("SPOS_YD_PNT_CD", StringHelper.evl(FrtoSltrec.getFieldString("YD_PNT_CD"), ""));
					    	   tcRecord.setField("ARR_WLOC_CD", StringHelper.evl(FrtoSltrec.getFieldString("ARR_WLOC_CD"), ""));
					    	   tcRecord.setField("TRN_WRK_MTL_GP", StringHelper.evl(FrtoSltrec.getFieldString("TRN_WRK_MTL_GP"), ""));
					    	   tcRecord.setField("MTL_UGNT_GP", StringHelper.evl(FrtoSltrec.getFieldString("MTL_UGNT_GP"), ""));
					    	   tcRecord.setField("HCR_GP", StringHelper.evl(FrtoSltrec.getFieldString("HCR_GP"), ""));
					    	   tcRecord.setField("CARLD_SH", Integer.toString(iSeqCount));
					    	}
						    tcRecord.setField("STL_NO" + (1+nIdx), StringHelper.evl(FrtoSltrec.getFieldString("STL_NO"), ""));
						    tcRecord.setField("STL_WT" + (1+nIdx), StringHelper.evl(FrtoSltrec.getFieldString("STL_WT"), ""));
						    nIdx ++;
						    
						    logger.println(LogLevel.DEBUG,this, "상차완료 후 저장품 정보 YD송신.===");
						    
							String s_YD_GP = getYdFromWlocCd(StringHelper.evl(FrtoSltrec.getFieldString("ARR_WLOC_CD"), ""));	
							
							//c열연과 후판,slab간에 분리처리
							if(s_YD_GP.equals("H")){

								CoilSpecRegSeEJBBean.stockProcCom(StringHelper.evl(FrtoSltrec.getFieldString("STL_NO"), ""),1);

							}else {
						        JDTORecord ydStlRecord = null;
						        ydStlRecord = JDTORecordFactory.getInstance().create();
						        ydStlRecord.setField("PT_TB_COMM",szPT_TB_COMM);
						        ydStlRecord.setField("STL_NO", StringHelper.evl(FrtoSltrec.getFieldString("STL_NO"), ""));							        
						        ydStlRecord.setField("SLAB_WO_RT_CD", StringHelper.evl(FrtoSltrec.getFieldString("SLAB_WO_RT_CD"), ""));
						        ydStlRecord.setField("ORD_YEOJAE_GP", StringHelper.evl(FrtoSltrec.getFieldString("ORD_YEOJAE_GP"), ""));
						        ydStlRecord.setField("SCARFING_YN", StringHelper.evl(FrtoSltrec.getFieldString("SCARFING_YN"), ""));
						        ydStlRecord.setField("SCARFING_DONE_YN", StringHelper.evl(FrtoSltrec.getFieldString("SCARFING_DONE_YN"), ""));
						        ydStlRecord.setField("MILL_WO_EXN", StringHelper.evl(FrtoSltrec.getFieldString("MILL_WO_EXN"), ""));					        
						        ydStlRecord.setField("YD_GP", s_YD_GP);					        
						        ydStlRecord.setField("STL_APPEAR_GP", StringHelper.evl(FrtoSltrec.getFieldString("STL_APPEAR_GP"), ""));
						        ydStlRecord.setField("HCR_GP", StringHelper.evl(FrtoSltrec.getFieldString("HCR_GP"), ""));
						        
						        String szRetunMsg = YdCommonUtils.uptStockCodeMapping(ydStlRecord);
							}
		    		    }
		    		
		    	    	tcRecord.setField("CARLD_CMPL_DT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
		    		    EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
				        isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{JDTORecord.class},
				  	  	             new Object[]{tcRecord});
				        logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 상차완료실적.===");

	    	    	}

			}
    		 
	    			    		
	  }
			
        //A열연 하차개시/완료
		if((!YmCommonConst.EQUIP_KIND_PT.equals(jtR.getFieldString("Put_Position").substring(2, 4)) && !YmCommonConst.EQUIP_KIND_TR.equals(jtR.getFieldString("Put_Position").substring(2, 4)))
				   &&
				   (YmCommonConst.NEW_SCH_WORK_KIND_SVMU.equals(Sch_Code) 	
				    )
				   )
			{
                String slab_no = StringHelper.evl(jtR.getFieldString("Slab_No"), "").trim();
        		String sGstockId	= StringHelper.evl(jtR.getFieldString("GROBAL_STOCK_ID"), "");
        		String sGlocation	= StringHelper.evl(jtR.getFieldString("GROBAL_LOCATION"), "");
				
				String queryID = "ym.facilitywork.putwrecord.session.getListworkQty_1";
	    	
	    		FrtoProductList = dao.getCommonList(queryID, new Object[]{slab_no,slab_no});
				
	    		if(FrtoProductList.size() > 0){
			    	JDTORecord StkColRec = (JDTORecord)FrtoProductList.get(0);
			    	tot_Qty  	= StkColRec.getFieldInt("TOT_QTY");
			    	work_Qty  	= StkColRec.getFieldInt("WORK_QTY");
	    		}
	    		
	    		
	    		
	    		
	    		int iCount = 1;
	    		if(!"".equals(sGstockId)) iCount = 2;  //두매작업
	    		
	    		logger.println(LogLevel.DEBUG,this, "하차완료처리 ====재료단위 재료공통 테이블 업데이트.===");
	    		if(iCount == 2)	//두매작업일경우 첫번째재료 처리    			
	    		{
	    			
//	    			TB_PT_STLFRTOMOVE update 				
					String stkQueryId = "ym.facilitywork.putwrecord.session.updateunLoadTimeToPT";
					iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ sGstockId, 
							                                                          sGstockId});
	    			
//	    			주편공통에서 현재 재료상태 확인						
					queryID	= "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getMSLABCOMM";
					FrtStockList = dao.getCommonList(queryID, new Object[]{sGstockId});
					
					if(FrtStockList.size()>0)
					{						
				    	JDTORecord FrtStockreq = (JDTORecord)FrtStockList.get(0);
				    	
				    	CurrProg_CD = StringHelper.evl(FrtStockreq.getFieldString("RECORD_PROG_STAT"),"");
				    	
				    	if(CurrProg_CD.equals("3"))
				    	{
 					
				    	 	logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶슬라브공통 업데이트처리 메소드 분리7◀◀◀◀◀◀◀◀◀◀"); 
				    	 	EJBConnector ejbConn = new EJBConnector("default","JNDICPutResReg",this);
				    	 	Boolean isYd = (Boolean)ejbConn.trx("updateMatlFtmvWlrstBcastSlab",new  Class[]{String.class},
																		new Object[]{sGstockId});
							
							
				    	}
				    	else
				    	{
 				    
				    	 	logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶주편공통 업데이트처리 메소드 분리8◀◀◀◀◀◀◀◀◀◀"); 
				    	 	EJBConnector ejbConn = new EJBConnector("default","JNDICPutResReg",this);
				    	 	Boolean isYd = (Boolean)ejbConn.trx("updateMatlFtmvWlrstBcastMSlab",new  Class[]{String.class},
																		new Object[]{sGstockId});
						    
						    	 				    
				    	 	String sScarfingYn 		= StringHelper.evl(FrtStockreq.getFieldString("SCARFING_YN"),"");
				    	 	String sOrdYeojaeGp 	= StringHelper.evl(FrtStockreq.getFieldString("ORD_YEOJAE_GP"),"");
				    	 	String sSlabCreateGp 	= StringHelper.evl(FrtStockreq.getFieldString("SLAB_CREATE_GP"),"");
				    	 	
				    	 	logger.println(LogLevel.DEBUG,this,"▶▶정정작업 sScarfingYn    ◀◀"+sScarfingYn);
				    	 	logger.println(LogLevel.DEBUG,this,"▶▶정정작업 sOrdYeojaeGp◀◀"+sOrdYeojaeGp);
				    	 	logger.println(LogLevel.DEBUG,this,"▶▶정정작업 sSlabCreateGp◀◀"+sSlabCreateGp);
				    
				    	    slab_prod_GP = sGstockId.substring(0, 1); //슬라브 생산공장구분
				    	 	
				    	    if(slab_prod_GP.equals("M"))
				    	    {
				    	 	  // 일단 처리 없이 SKIP
				    	    }
				    	    else
				    	    {
				    	    	if("N".equals(sScarfingYn)){	
					    	 		if("G".equals(sSlabCreateGp)&&"2".equals(sOrdYeojaeGp)){	
					    	 		}else{
				    	 			/*
						    	     * B열연 정정마감실적 송신
						    	     * 조건 : Non Scarfing 대상재(단,구입재이면서 여재인것은 제외)
						    	     */
					    			JDTORecord tEndRecord = null;
					    			tEndRecord = JDTORecordFactory.getInstance().create(); 
					    			tEndRecord.setField("JMS_TC_CD", "YMCSJ001");
					    			tEndRecord.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));						
					    			tEndRecord.setField("MSLAB_NO",sGstockId);
							
									EJBConnector ejbConn2 = new EJBConnector("default","JNDIYardWrkResReg",this);
									isSuccess = (Boolean)ejbConn2.trx("sendInternalModel",new Class[]{JDTORecord.class},
									  	  	 new Object[]{tEndRecord});
									logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 B열연 정정마감실적.===");
					    	 		}
					    	 	}
				    	    	
				    	    }			    	    				    	 						    				    
				    	}
					}else
					{
						logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶슬라브공통 업데이트처리 메소드 분리9◀◀◀◀◀◀◀◀◀◀"); 
			    	 	EJBConnector ejbConn = new EJBConnector("default","JNDICPutResReg",this);
			    	 	Boolean isYd = (Boolean)ejbConn.trx("updateMatlFtmvWlrstBcastSlab",new  Class[]{String.class},
																	new Object[]{sGstockId});																		
					}
					
					//공통에 전문 전송(YDCTJ032, YDPTJ001)
		    	 	this.SlabYDPTJ001Send(sGstockId);
	    		}
	    		
	    			
	    	 	
	    		
	    		//한매작업 - 주편공통에서 현재 진도코드 확인													
				queryID	= "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getMSLABCOMM";
				FrtStockList2 = dao.getCommonList(queryID, new Object[]{slab_no});
				
				
				if(FrtStockList2.size()>0)
				{
				
			    	JDTORecord FrtStockreq2 = (JDTORecord)FrtStockList2.get(0);
			    	
			    	//TB_PT_STLFRTOMOVE update 				
					String stkQueryId = "ym.facilitywork.putwrecord.session.updateunLoadTimeToPT";
					iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ slab_no, 
							                                                          slab_no});
			    	
			    	CurrProg_CD = StringHelper.evl(FrtStockreq2.getFieldString("RECORD_PROG_STAT"),"");
			    	
			    	if(CurrProg_CD.equals("3"))
			    	{
 
						logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶슬라브공통 업데이트처리 메소드 분리10◀◀◀◀◀◀◀◀◀◀"); 
			    	 	EJBConnector ejbConn = new EJBConnector("default","JNDICPutResReg",this);
			    	 	Boolean isYd = (Boolean)ejbConn.trx("updateMatlFtmvWlrstBcastSlab",new  Class[]{String.class},
																	new Object[]{slab_no});
			    	}
			    	else
			    	{
 
			    	 	logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶주편공통 업데이트처리 메소드 분리11◀◀◀◀◀◀◀◀◀◀"); 
			    	 	EJBConnector ejbConn = new EJBConnector("default","JNDICPutResReg",this);
			    	 	Boolean isYd = (Boolean)ejbConn.trx("updateMatlFtmvWlrstBcastMSlab",new  Class[]{String.class},
																	new Object[]{slab_no});
					    
			    	 	String sScarfingYn 		= StringHelper.evl(FrtStockreq2.getFieldString("SCARFING_YN"),"");
			    	 	String sOrdYeojaeGp 	= StringHelper.evl(FrtStockreq2.getFieldString("ORD_YEOJAE_GP"),"");
			    	 	String sSlabCreateGp 	= StringHelper.evl(FrtStockreq2.getFieldString("SLAB_CREATE_GP"),"");
			    	 	
			    	 	logger.println(LogLevel.DEBUG,this,"▶▶정정작업 sScarfingYn    ◀◀"+sScarfingYn);
			    	 	logger.println(LogLevel.DEBUG,this,"▶▶정정작업 sOrdYeojaeGp◀◀"+sOrdYeojaeGp);
			    	 	logger.println(LogLevel.DEBUG,this,"▶▶정정작업 sSlabCreateGp◀◀"+sSlabCreateGp);
			    	 	
			    	 	
			    	 	slab_prod_GP = slab_no.substring(0, 1); //슬라브 생산공장구분
			    	 	
			    	    if(slab_prod_GP.equals("M"))
			    	    {
			    	 	  // 일단 처리 없이 SKIP
			    	    }
			    	    else
			    	    {
			    	    	if("N".equals(sScarfingYn)){	
				    	 		if("G".equals(sSlabCreateGp)&&"2".equals(sOrdYeojaeGp)){	
				    	 		}else{
			    	 			/*
					    	     * B열연 정정마감실적 송신
					    	     * 조건 : Non Scarfing 대상재(단,구입재이면서 여재인것은 제외)
					    	     */
				    			JDTORecord tEndRecord = null;
				    			tEndRecord = JDTORecordFactory.getInstance().create(); 
				    			tEndRecord.setField("JMS_TC_CD", "YMCSJ001");
				    			tEndRecord.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));						
				    			tEndRecord.setField("MSLAB_NO",slab_no);
						
								EJBConnector ejbConn2 = new EJBConnector("default","JNDIYardWrkResReg",this);
								isSuccess = (Boolean)ejbConn2.trx("sendInternalModel",new Class[]{JDTORecord.class},
								  	  	 new Object[]{tEndRecord});
								logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 B열연 정정마감실적.===");
				    	 		}
				    	 	}
			    	    	
			    	    }		    	 			    	 	
			    	 
			    	}
				}
				else{
//					TB_PT_STLFRTOMOVE update 				
					String stkQueryId = "ym.facilitywork.putwrecord.session.updateunLoadTimeToPT";
					iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ slab_no, 
							                                                          slab_no});
					
					logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶슬라브공통 업데이트처리 메소드 분리12◀◀◀◀◀◀◀◀◀◀"); 
		    	 	EJBConnector ejbConn = new EJBConnector("default","JNDICPutResReg",this);
		    	 	Boolean isYd = (Boolean)ejbConn.trx("updateMatlFtmvWlrstSlab",new  Class[]{String.class},
																new Object[]{slab_no});
 
		    	 	//공통에 전문 전송(YDCTJ032, YDPTJ001)
		    	 	this.SlabYDPTJ001Send(slab_no);	
				}
	    		
    			    		
	    		if(work_Qty == 1 || (work_Qty == 2 && iCount == 2))
	    		{
	    			//차량스케쥴테이블 - 하차개시시간 업데이트
	    			queryID = "ym.facilitywork.putwrecord.session.updateUnloadstartTime";
			    	count = dao.updateData(queryID, new Object[]{slab_no}); 
			    	
			    	queryID	= "ym.facilitywork.putwrecord.session.getListTcconst_1";
			    	FrtoProductList = dao.getCommonList(queryID, new Object[]{slab_no});
			    	JDTORecord FrtoProduct = (JDTORecord)FrtoProductList.get(0);
			    	
	    			
	    			//하차개시실적 송신
	    			//소재차량하차개시
	    			JDTORecord tcRecord = null;
					tcRecord = JDTORecordFactory.getInstance().create(); 
					tcRecord.setField("JMS_TC_CD", "YDTSJ009");
					tcRecord.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
					
					tcRecord.setField("TRN_EQP_CD", StringHelper.evl(FrtoProduct.getFieldString("TRN_EQP_CD"), ""));
					tcRecord.setField("ARR_WLOC_CD", StringHelper.evl(FrtoProduct.getFieldString("ARR_WLOC_CD"), ""));
					tcRecord.setField("ARR_YD_PNT_CD", StringHelper.evl(FrtoProduct.getFieldString("YD_PNT_CD"), ""));
					tcRecord.setField("TRN_WRK_ST_DT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
			
					EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
					isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{JDTORecord.class},
					  	  	 new Object[]{tcRecord});
					logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 하차개시실적.===");

	    		}
	    		int CurrStk_Layer = 0;
	    		if(iCount == 2)	//두매작업일경우 첫번째재료 처리    			
	    		{ 
	    			logger.println(LogLevel.DEBUG,this, "===========두매작업임. 아래 슬라브의 단정보를 이용===");
	    		      queryID	= "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getLastworkYn";
				      List FrtoProduct5= dao.getCommonList(queryID, new Object[]{sGstockId});
		    	      JDTORecord FrtStockreq3 = (JDTORecord)FrtoProduct5.get(0);
 	
		    	      CurrStk_Layer = FrtStockreq3.getFieldInt("YD_STK_LYR_NO");
		    	      logger.println(LogLevel.DEBUG,this, "============== 권하처리 슬라브번호 .===" +sGstockId);
		    	      logger.println(LogLevel.DEBUG,this, "============== 권하처리 슬라브의 단정보 .===" +CurrStk_Layer);
	    		}
	    		else
	    		{	    		
	    		    queryID	= "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getLastworkYn";
				    List FrtoProduct5= dao.getCommonList(queryID, new Object[]{slab_no});
		    	    JDTORecord FrtStockreq3 = (JDTORecord)FrtoProduct5.get(0);
 	
		    	    CurrStk_Layer = FrtStockreq3.getFieldInt("YD_STK_LYR_NO");
		    	    logger.println(LogLevel.DEBUG,this, "============== 권하처리 슬라브번호 .===" +slab_no);
		    	    logger.println(LogLevel.DEBUG,this, "============== 권하처리 슬라브의 단정보 .===" +CurrStk_Layer);
	    		}
	    		
	    		if(CurrStk_Layer == 1)
	    	//	if(tot_Qty == work_Qty)
	    		{	
	    			logger.println(LogLevel.DEBUG,this, "============== 권하처리 슬라브의 단정보가 001이므로 하차완료처리.===");
			    	queryID	= "ym.facilitywork.putwrecord.session.getListTcconst_1";
			    	FrtoProductList = dao.getCommonList(queryID, new Object[]{slab_no});
			    	JDTORecord FrtoProduct = (JDTORecord)FrtoProductList.get(0);	    	
	 				    				    		
	    			
		    		//차량스케쥴테이블 - 하차완료시간 업데이트
	    			queryID = "ym.facilitywork.putwrecord.session.updateUnloadendTime";
			    	count = dao.updateData(queryID, new Object[]{slab_no});
			    	
			    	//차량재료테이블 - DEL_YN 업데이트
	    			queryID = "ym.facilitywork.putwrecord.session.deletefrmtList";
			    	count = dao.updateData(queryID, new Object[]{slab_no});
			    	
			    	   			
	    			//하차완료실적 송신
     			    //소재차량하차완료
	    			JDTORecord tcRecord = null;
					tcRecord = JDTORecordFactory.getInstance().create(); 
					tcRecord.setField("JMS_TC_CD", "YDTSJ010");
					tcRecord.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
					tcRecord.setField("TRN_EQP_CD", StringHelper.evl(FrtoProduct.getFieldString("TRN_EQP_CD"), ""));
					tcRecord.setField("ARR_WLOC_CD", StringHelper.evl(FrtoProduct.getFieldString("ARR_WLOC_CD"), ""));
					tcRecord.setField("ARR_YD_PNT_CD", StringHelper.evl(FrtoProduct.getFieldString("YD_PNT_CD"), ""));
					tcRecord.setField("CARUD_CMPL_DT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
 				
					EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
					isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{JDTORecord.class},
					  	  	 new Object[]{tcRecord});
					logger.println(LogLevel.DEBUG,this, "내부IF호출===슬라브 일관제철하차완료실적.===");

	    		}
				
			}
		return isSuccess.booleanValue();

		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}
	
	
	/**
	 * 오퍼레이션명 : 
	 *
	 *	2.	COIL INFO
        *		야드작업실적송신BACKUP처리 
        *		코일야드 차량 상/하차시 마지막 상하차개시/완료실적 구내운송으로 송신
        * 
        * param JDTORecord 	: 전문항목
        *
        * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */      
	public boolean callStartLastWorkBackupInfo_Coil(String sEQP_NO){
		Boolean isSuccess = new Boolean(false);
		ymCommonDAO dao = ymCommonDAO.getInstance();
		YdStockDAO ydStockDAO = new YdStockDAO();
		List FrtoProductList = null;	
		JDTORecord tcRecordDM = null;
		
		String Sch_Code 	= ""; 	
		String Work_Id 	 	= "";	
		String Put_Position =  "";
		String Yard_Id 	 	=  "";	
		String Coil_No		= "";	
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String queryID = "ym.facilitywork.putwrecord.session.getSangChaEndList_Coil";		
			FrtoProductList = dao.getCommonList(queryID, new Object[]{sEQP_NO});
			
			if(FrtoProductList.size() > 0){
				
		    	JDTORecord StkColRec = (JDTORecord)FrtoProductList.get(0);
		    	
	 			
		    	 Sch_Code 	= 	StkColRec.getFieldString("SCH_CODE");
		    	 Work_Id 	 	=	StringHelper.evl(StkColRec.getFieldString("WORK_ID"), "");
		    	 Put_Position =  StringHelper.evl(StkColRec.getFieldString("PUT_POSITION"), "");
		    	 Yard_Id 	 	= 	StringHelper.evl(StkColRec.getFieldString("YARD_ID"), "");
		    	 Coil_No		=	StringHelper.evl(StkColRec.getFieldString("STL_NO"), "");            
			
			
				tcRecordDM = JDTORecordFactory.getInstance().create(); 
	 			tcRecordDM.setField("Sch_Code", 	Sch_Code);
	            tcRecordDM.setField("Work_Id",		Work_Id);
	            tcRecordDM.setField("Put_Position", Put_Position);
	            tcRecordDM.setField("Yard_Id", 		Yard_Id);
	            tcRecordDM.setField("Coil_No", 		Coil_No);  
	            
	            this.callStartLastWorkInfo_Coil(tcRecordDM);
	            logger.println(LogLevel.DEBUG,this, "내부IF호출===AB열연간  구내운송 상하차 완료 전문 BACKUP전송 성공 ===");
	            return true;
			}
			logger.println(LogLevel.DEBUG,this, "내부IF호출===AB열연간  구내운송 상하차 완료 전문 BACKUP전송 실패 ===");
			return false;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}

	}
	/**
	 * 오퍼레이션명 : 
	 *
	 *	2.	COIL INFO
        *		야드작업실적송신
        *		코일야드 차량 상/하차시 마지막 상하차개시/완료실적 구내운송으로 송신
        * 
        * param JDTORecord 	: 전문항목
        *
        * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */      
	public boolean callStartLastWorkInfo_Coil(JDTORecord jtR){

		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		Boolean isSuccess = new Boolean(false);
		
		String Sch_Code = StringHelper.evl(jtR.getFieldString("Sch_Code"), "");
 		if(YmCommonConst.NEW_SCH_WORK_KIND_CVML.equals(Sch_Code)|| //Coil 소재이송상차
 		   YmCommonConst.NEW_SCH_WORK_KIND_CVM2.equals(Sch_Code)|| // COIL 소재이송상차
	       YmCommonConst.NEW_SCH_WORK_KIND_CVM3.equals(Sch_Code)|| // COIL 소재이송상차
		   YmCommonConst.NEW_SCH_WORK_KIND_CVMU.equals(Sch_Code)|| // COIL 소재이송하차
		   YmCommonConst.NEW_SCH_WORK_KIND_CVM4.equals(Sch_Code)|| // COIL 소재이송하차	 
  		   YmCommonConst.NEW_SCH_WORK_KIND_CVM5.equals(Sch_Code)|| // COIL 소재이송하차	 
		   YmCommonConst.NEW_SCH_WORK_KIND_GVML.equals(Sch_Code)|| // COIL 제품이송상차	
		   YmCommonConst.NEW_SCH_WORK_KIND_GVM2.equals(Sch_Code)|| // COIL 제품이송상차
		   YmCommonConst.NEW_SCH_WORK_KIND_GVM3.equals(Sch_Code)|| // COIL 제품이송상차	
		   YmCommonConst.NEW_SCH_WORK_KIND_GVMU.equals(Sch_Code)|| // COIL 제품이송하차	
		   YmCommonConst.NEW_SCH_WORK_KIND_GVM4.equals(Sch_Code)|| // COIL 제품이송하차	 	
		   YmCommonConst.NEW_SCH_WORK_KIND_GVM5.equals(Sch_Code)){ // COIL 제품이송하차	
 		}else{
 			logger.println(LogLevel.DEBUG,this, "내부IF호출===이송상/하차 작업아님. 작업실적전송 SKIP.===");
			return false;
 		}
		
			
		String tWork_Id 		= YmCommonUtil.getCurDataWithLegacy(StringHelper.evl(jtR.getFieldString("Work_Id"), ""));
		if(YmCommonConst.SUB_WORK_S.equals(tWork_Id)){
			logger.println(LogLevel.DEBUG,this, "내부IF호출===보조작업대상으로 작업실적전송 SKIP.===");
			return false;
		}
		
		logger.println(LogLevel.DEBUG,this, "put 처리시 JDTORecord 내용:"+jtR);			

		ymCommonDAO dao = ymCommonDAO.getInstance();
		YdStockDAO ydStockDAO = new YdStockDAO();
		List FrtoProductList = null;
		String[] rVal = new String[2];
		CoilSpecRegSeEJBBean CoilSpecRegSeEJBBean=new CoilSpecRegSeEJBBean();
		JDTORecord    recInTemp         	= null;
		String stkBed = "";
		String stkLayer = "";
		String grobal_stkBed = "";
		String grobal_stkLayer = "";
		String stl_appear_gp ="";
	    List FrtostlList = null;	    
			
		int tot_Qty = 0;
		int work_Qty = 0;
		int count = 0;
		int intRtnVal = 0;
		
		
		
		try{
			// PIDEV
//			String sApplyYnPI = commDao.ApplyYnPI("", "CPutResRegSBean.callStartLastWorkInfo_Coil ", "APPPI0", "*", "*");
			
			//##############################################################################################################
			// 코일 상차개시/완료#############################################################################################
			//##############################################################################################################
			if((YmCommonConst.EQUIP_KIND_PT.equals(jtR.getFieldString("Put_Position").substring(2, 4)) ||YmCommonConst.EQUIP_KIND_TR.equals(jtR.getFieldString("Put_Position").substring(2, 4)))
			   &&
			   (YmCommonConst.NEW_SCH_WORK_KIND_CVML.equals(Sch_Code)|| //Coil 소재이송상차
		 		   YmCommonConst.NEW_SCH_WORK_KIND_CVM2.equals(Sch_Code)|| // COIL 소재이송상차
			       YmCommonConst.NEW_SCH_WORK_KIND_CVM3.equals(Sch_Code)|| // COIL 소재이송상차
			       YmCommonConst.NEW_SCH_WORK_KIND_GVML.equals(Sch_Code)|| // COIL 제품이송상차	
				   YmCommonConst.NEW_SCH_WORK_KIND_GVM2.equals(Sch_Code)|| // COIL 제품이송상차
				   YmCommonConst.NEW_SCH_WORK_KIND_GVM3.equals(Sch_Code)   // COIL 제품이송상차	
			    )
			   ){ 

				String Coil_No = StringHelper.evl(jtR.getFieldString("Coil_No"), "").trim();
				
				String queryID = "ym.facilitywork.putwrecord.session.getListworkQty_Coil";
	    		
	    		FrtoProductList = dao.getCommonList(queryID, new Object[]{Coil_No,Coil_No,Coil_No});
	    		
	    		if(FrtoProductList.size() > 0){
			    	JDTORecord StkColRec = (JDTORecord)FrtoProductList.get(0);
			    	tot_Qty  	= StkColRec.getFieldInt("TOT_QTY");
			    	work_Qty  	= StkColRec.getFieldInt("WORK_QTY");
			    	stl_appear_gp=StkColRec.getFieldString("STL_APPEAR_GP");   //재료외형구분 Y: 제품 , 기타: 소재
	    		}
	    		

	    		//차량재료정보에 Bed/Layer 업데이트
	    		stkBed  = StringHelper.evl(jtR.getFieldString("Put_Position").substring(6,8), "");
	    		stkLayer  = StringHelper.evl(jtR.getFieldString("Put_Position").substring(8,10), "");
	    		queryID = "ym.facilitywork.putwrecord.session.updateCarftmtl";
		    	count = dao.updateData(queryID, new Object[]{stkBed,"0"+stkLayer,Coil_No}); 
 				
	    		
		    	queryID	= "ym.facilitywork.putwrecord.session.getListTcconst";
		    	FrtoProductList = dao.getCommonList(queryID, new Object[]{Coil_No});
		    	JDTORecord FrtoProduct = (JDTORecord)FrtoProductList.get(0);
		    	
		    	String szYD_CAR_USE_GP = StringHelper.evl(FrtoProduct.getFieldString("YD_CAR_USE_GP"), "");
		    	String szTRANS_EQUIPMENT_TYPE = StringHelper.evl(FrtoProduct.getFieldString("TRANS_EQUIPMENT_TYPE"), "");
		    	String szYD_CAR_PROG_STAT= StringHelper.evl(FrtoProduct.getFieldString("YD_CAR_PROG_STAT"), "");
		    	String szYD_CAR_SCH_ID= StringHelper.evl(FrtoProduct.getFieldString("YD_CAR_SCH_ID"), "");
		    	
		    	if(szYD_CAR_PROG_STAT.equals("2") || szYD_CAR_PROG_STAT.equals("3"))  //개시
	    		{
	    			//차량스케쥴테이블 - 상차개시시간 업데이트
	    			queryID = "ym.facilitywork.putwrecord.session.updateLoadstartTime";
			    	count = dao.updateData(queryID, new Object[]{Coil_No}); 
			    	
			    	
			    
			    	if( szYD_CAR_USE_GP.equals("L") ) {
				    	//구내운송 소재차량상차개시#############################################################################
	                    JDTORecord tcRecord = null;
						tcRecord = JDTORecordFactory.getInstance().create(); 
						tcRecord.setField("JMS_TC_CD", "YDTSJ007");
						tcRecord.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));					
						tcRecord.setField("TRN_EQP_CD", StringHelper.evl(FrtoProduct.getFieldString("TRN_EQP_CD"), ""));
						tcRecord.setField("SPOS_WLOC_CD", StringHelper.evl(FrtoProduct.getFieldString("SPOS_WLOC_CD"), ""));
						tcRecord.setField("SPOS_YD_PNT_CD", StringHelper.evl(FrtoProduct.getFieldString("YD_PNT_CD"), ""));
						tcRecord.setField("ARR_WLOC_CD", StringHelper.evl(FrtoProduct.getFieldString("ARR_WLOC_CD"), ""));
						tcRecord.setField("TRN_WRK_ST_DT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
						
						EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
						isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{JDTORecord.class},
						  	  	 new Object[]{tcRecord});
						logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 구내운송 이송상차개시실적.===");					
						//####################################################################################################
						
						
						//20091111 개시 시점에 상차대상을 한번에 전송 한다.
						//출하고간이송상차개시실적 송신(제품인 경우에만 출하에 전송 한다.)################################################
						if(stl_appear_gp.equals("Y")){						
							//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		                    //코일제품고간이송상하차개시
							
					    	queryID	= "ym.facilitywork.putwrecord.session.getListTcconst11";
					    	FrtoProductList = dao.getCommonList(queryID, new Object[]{StringHelper.evl(FrtoProduct.getFieldString("TRN_EQP_CD"), "")});
			    	
					    	
		         			JDTORecord tcRecordDM = null;
		         			
		         			tcRecordDM = JDTORecordFactory.getInstance().create(); 
		         			
		         			// PIDEV
//							if ("Y".equals(sApplyYnPI)) {
								int iSeqCount 	= FrtoProductList.size();
								
			         			tcRecordDM.setField("MQ_TC_CD"			, "M10YDLMJ1111");
								tcRecordDM.setField("MQ_TC_CREATE_DDTT"	, new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
			         			
			                    tcRecordDM.setField("CARD_NO"			,"");
			                    tcRecordDM.setField("CAR_NO"			, StringHelper.evl(FrtoProduct.getFieldString("TRN_EQP_CD"), ""));
			                    tcRecordDM.setField("YD_GP"				, jtR.getFieldString("Yard_Id"));	
			                    tcRecordDM.setField("DIST_GOODS_GP"		, "H");
			                    tcRecordDM.setField("YARD_GP"			, "");
			                    tcRecordDM.setField("UPCARUNLOAD_GP"	, "U"); //U:상차,D:하차
			                    tcRecordDM.setField("SCH_YN"			, "N");
			                    
			                    tcRecordDM.setField("CARUD_ST_DATE"		, YmCommonUtil.getCurDate("yyyyMMdd"));
			                    tcRecordDM.setField("CARUD_ST_TIME"		, YmCommonUtil.getCurDate("HHmmss"));	
			                    
			                    tcRecordDM.setField("GOODS_CNT"			, iSeqCount);
			                    
					    		for (int i=0; i < iSeqCount ; i++) {
						    		JDTORecord FrtoProduct2 = (JDTORecord)FrtoProductList.get(i);
						    		
						    		tcRecordDM.setField("GOODS_NO" 		+ (1+i),StringHelper.evl(FrtoProduct2.getFieldString("STL_NO"), ""));
				                    tcRecordDM.setField("TRN_REQ_DATE" 	+ (1+i),StringHelper.evl(FrtoProduct2.getFieldString("TRANS_WORD_DATE"), ""));
				                    tcRecordDM.setField("TRN_REQ_SEQ" 	+ (1+i),StringHelper.evl(FrtoProduct2.getFieldString("TRANS_WORD_SEQNO"), ""));
					    		}

//							} else {
//			         			tcRecordDM.setField("JMS_TC_CD", "YDDMR019");
//								tcRecordDM.setField("TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
//			         			tcRecordDM.setField("UPCARUNLOAD_GP", "U"); //U:상차,D:하차
//			                    tcRecordDM.setField("CARD_NO","");
//			                    tcRecordDM.setField("CAR_NO", StringHelper.evl(FrtoProduct.getFieldString("TRN_EQP_CD"), ""));
//			                    tcRecordDM.setField("YD_GP", jtR.getFieldString("Yard_Id"));	
//			                    tcRecordDM.setField("CARLOAD_START_DATE", YmCommonUtil.getCurDate("yyyyMMdd"));
//			                    tcRecordDM.setField("CARLOAD_START_TIME", YmCommonUtil.getCurDate("HHmmss"));	
//			                    
//			    	    		int iSeqCount 	= FrtoProductList.size();
//					    		for(int i=0; i < iSeqCount ; i++) {
//						    		JDTORecord FrtoProduct2 = (JDTORecord)FrtoProductList.get(i);
//						    		tcRecordDM.setField("GOODS_NO" 			+ (1+i),StringHelper.evl(FrtoProduct2.getFieldString("STL_NO"), ""));
//				                    tcRecordDM.setField("TRANS_WORD_DATE" 	+ (1+i),StringHelper.evl(FrtoProduct2.getFieldString("TRANS_WORD_DATE"), ""));
//				                    tcRecordDM.setField("TRANS_WORD_SEQNO" 	+ (1+i),StringHelper.evl(FrtoProduct2.getFieldString("TRANS_WORD_SEQNO"), ""));
//					    		}
//							}

		         			//인터페이스 전문 호출
						   EJBConnector ejbConn1 = new EJBConnector("default","JNDIYardWrkResReg",this);
						   isSuccess = (Boolean)ejbConn1.trx("sendInternalModel",new Class[]{JDTORecord.class},
						  	  	             new Object[]{tcRecordDM}); 
		                    logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 코일제품고간이송상하차개시.===");
		                    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		                    
						}
						//####################################################################################################
	    			}else if (szTRANS_EQUIPMENT_TYPE.equals("P")) { // 출하PDA

							// 코일이송상차개시 전송PDA#############################################################################
							JDTORecord tcRecordDM = null;

							tcRecordDM = JDTORecordFactory.getInstance().create();
						
						// PIDEV
//						if("Y".equals(sApplyYnPI)) {
							tcRecordDM.setField("MQ_TC_CD" , "M10YDLMJ1071");
							tcRecordDM.setField("MQ_TC_CREATE_DDTT" , new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
							tcRecordDM.setField("TRN_REQ_DATE" , StringHelper.evl(FrtoProduct.getFieldString("TRANS_WORD_DATE") , ""));
							tcRecordDM.setField("TRN_REQ_SEQ" , StringHelper.evl(FrtoProduct.getFieldString("TRANS_WORD_SEQNO") , ""));
							tcRecordDM.setField("CAR_NO" , StringHelper.evl(FrtoProduct.getFieldString("CAR_NO") , ""));
							tcRecordDM.setField("CARD_NO" , StringHelper.evl(FrtoProduct.getFieldString("CARD_NO") , ""));
							tcRecordDM.setField("YD_GP" , jtR.getFieldString("Yard_Id"));
							tcRecordDM.setField("DIST_GOODS_GP" , "H");
							tcRecordDM.setField("SCH_YN" , "Y");
							tcRecordDM.setField("CARLOAD_START_DATE" , YmCommonUtil.getCurDate("yyyyMMdd"));
							tcRecordDM.setField("CARLOAD_START_TIME" , YmCommonUtil.getCurDate("HHmmss"));
							tcRecordDM.setField("CR_FRTOMOVE_GP" , StringHelper.evl(FrtoProduct.getFieldString("CR_FRTOMOVE_GP") , ""));								
						
//						} else {
//							tcRecordDM.setField("JMS_TC_CD" , "YDDMR071");
//							tcRecordDM.setField("TC_CREATE_DDTT" , new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
//							tcRecordDM.setField("CARD_NO" , StringHelper.evl(FrtoProduct.getFieldString("CARD_NO") , ""));
//							tcRecordDM.setField("CAR_NO" , StringHelper.evl(FrtoProduct.getFieldString("CAR_NO") , ""));
//							tcRecordDM.setField("YD_GP" , jtR.getFieldString("Yard_Id"));
//							tcRecordDM.setField("CARLOAD_START_DATE" , YmCommonUtil.getCurDate("yyyyMMdd"));
//							tcRecordDM.setField("CARLOAD_START_TIME" , YmCommonUtil.getCurDate("HHmmss"));
//							tcRecordDM.setField("TRANS_WORD_DATE" , StringHelper.evl(FrtoProduct.getFieldString("TRANS_WORD_DATE") , ""));
//							tcRecordDM.setField("TRANS_WORD_SEQNO" , StringHelper.evl(FrtoProduct.getFieldString("TRANS_WORD_SEQNO") , ""));
//							tcRecordDM.setField("CR_FRTOMOVE_GP" , StringHelper.evl(FrtoProduct.getFieldString("CR_FRTOMOVE_GP") , ""));								
//						}

						// 인터페이스 전문 호출
						EJBConnector ejbConn1 = new EJBConnector("default" , "JNDIYardWrkResReg" , this);
						isSuccess = (Boolean) ejbConn1.trx("sendInternalModel" , new Class[]{JDTORecord.class} , new Object[]{tcRecordDM});
						//####################################################################################################
					}
	    		}
		    	
		    	
		    	if( szTRANS_EQUIPMENT_TYPE.equals("P") ) {					//출하PDA
 
		            logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일품 상차실적 송신 YDDMR072 (코일일품출하상차실적 송신).==="); 
		         // 일품 상차실적 송신 전송PDA#############################################################################
					JDTORecord tcRecordDM = null;

					tcRecordDM = JDTORecordFactory.getInstance().create();
					
					// PIDEV
//					if ("Y".equals(sApplyYnPI)) {
						tcRecordDM.setField("MQ_TC_CD" 			, "M10YDLMJ1081"														);
						tcRecordDM.setField("MQ_TC_CREATE_DDTT" , new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss"))					);
						tcRecordDM.setField("TRN_REQ_DATE"		, StringHelper.evl(FrtoProduct.getFieldString("TRANS_WORD_DATE") , "")	);
						tcRecordDM.setField("TRN_REQ_SEQ" 		, StringHelper.evl(FrtoProduct.getFieldString("TRANS_WORD_SEQNO") , "")	);						
						tcRecordDM.setField("CARD_NO" 			, StringHelper.evl(FrtoProduct.getFieldString("CARD_NO") , "")			);
						tcRecordDM.setField("CAR_NO" 			, StringHelper.evl(FrtoProduct.getFieldString("CAR_NO") , "")			);
						tcRecordDM.setField("YD_GP" 			, jtR.getFieldString("Yard_Id")											);
						tcRecordDM.setField("DIST_GOODS_GP" 	, "H"																	);
						tcRecordDM.setField("SCH_YN" 			, "Y"																	);
						tcRecordDM.setField("GOODS_NO" 			, Coil_No																); 
						
//					} else {
//						tcRecordDM.setField("JMS_TC_CD" , "YDDMR072");
//						tcRecordDM.setField("TC_CREATE_DDTT" , new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
//						tcRecordDM.setField("CARD_NO" , StringHelper.evl(FrtoProduct.getFieldString("CARD_NO") , ""));
//						tcRecordDM.setField("CAR_NO" , StringHelper.evl(FrtoProduct.getFieldString("CAR_NO") , ""));
//						tcRecordDM.setField("YD_GP" , jtR.getFieldString("Yard_Id"));
//						tcRecordDM.setField("GOODS_NO" , Coil_No); 
//						tcRecordDM.setField("TRANS_WORD_DATE" , StringHelper.evl(FrtoProduct.getFieldString("TRANS_WORD_DATE") , ""));
//						tcRecordDM.setField("TRANS_WORD_SEQNO" , StringHelper.evl(FrtoProduct.getFieldString("TRANS_WORD_SEQNO") , ""));
//					}
					
					// 인터페이스 전문 호출
					EJBConnector ejbConn1 = new EJBConnector("default" , "JNDIYardWrkResReg" , this);
					isSuccess = (Boolean) ejbConn1.trx("sendInternalModel" , new Class[]{JDTORecord.class} , new Object[]{tcRecordDM});
					//####################################################################################################
	            }
		    	
		    	
		    	
		    	
		    	
		    	
		    	
	    		logger.println(LogLevel.DEBUG,this, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+tot_Qty+" rrrrr "+work_Qty);
	    		if(tot_Qty == work_Qty) //완료
	    		{    //상차개시완료 실적 송신
	    			
	    			int nIdx = 0;
	    			
	    			// 차량스케쥴테이블 - 상차완료시간 업데이트
	    			queryID = "ym.facilitywork.putwrecord.session.updateLoadendTime";
			    	count = dao.updateData(queryID, new Object[]{Coil_No}); 
			    	
			    	
			    	if( szYD_CAR_USE_GP.equals("L") ) {		
			    	
			    	
				    	queryID	= "ym.facilitywork.putwrecord.session.getListTcconst";
				    	FrtoProductList = dao.getCommonList(queryID, new Object[]{Coil_No});
				    	JDTORecord FrtoProduct1 = (JDTORecord)FrtoProductList.get(0);
				    	 	
				    	//차량스케쥴ID로 이송재료 조회(
			    		String trnQueryId = "ym.tsinfo.getListFrtostlList_loadEnd_PIDEV";
			    		FrtostlList = dao.getCommonList(trnQueryId, new Object[]{StringHelper.evl(FrtoProduct1.getFieldString("TRN_EQP_CD"), "")});
			    	
			    		
			    		JDTORecord tcRecord1 = null;
						tcRecord1 = JDTORecordFactory.getInstance().create(); 
						
						JDTORecord tcRecord = null;
			    		int iSeqCount 	= FrtostlList.size();
			    		for(int i=0; i < iSeqCount ; i++){
							
				    		JDTORecord FrtoSltrec = (JDTORecord)FrtostlList.get(i);
							String s_STOCK_ID = StringHelper.evl(FrtoSltrec.getFieldString("STL_NO"),"").trim();
							
							//TB_PT_STLFRTOMOVE update				
							String stkQueryId = "ym.facilitywork.putwrecord.session.updateLoadTimeToPT";
							int iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ s_STOCK_ID, 
									                                                          s_STOCK_ID});							
							

							if(i == 0){
								//상차완료실적 송신
								//구내운송 소재차량상차완료#############################################################################
								tcRecord = JDTORecordFactory.getInstance().create(); 
								tcRecord.setField("JMS_TC_CD", "YDTSJ008");  //코일  야드
								tcRecord.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));					
								tcRecord.setField("TRN_EQP_CD", StringHelper.evl(FrtoSltrec.getFieldString("TRN_EQP_CD"), ""));
								tcRecord.setField("SPOS_WLOC_CD", StringHelper.evl(FrtoSltrec.getFieldString("SPOS_WLOC_CD"), ""));
								tcRecord.setField("SPOS_YD_PNT_CD", StringHelper.evl(FrtoSltrec.getFieldString("YD_PNT_CD"), ""));
								tcRecord.setField("ARR_WLOC_CD", StringHelper.evl(FrtoSltrec.getFieldString("ARR_WLOC_CD"), ""));
								tcRecord.setField("TRN_WRK_MTL_GP", StringHelper.evl(FrtoSltrec.getFieldString("TRN_WRK_MTL_GP"), ""));
								tcRecord.setField("MTL_UGNT_GP", StringHelper.evl(FrtoSltrec.getFieldString("MTL_UGNT_GP"), ""));
								tcRecord.setField("HCR_GP", StringHelper.evl(FrtoSltrec.getFieldString("HCR_GP"), ""));
								tcRecord.setField("CARLD_SH", Integer.toString(iSeqCount));

								//(제품인 경우에만 출하에 전송 한다.)
								if(stl_appear_gp.equals("Y")){
									// PIDEV
//									if("Y".equals(sApplyYnPI)) {
										tcRecord1.setField("TRN_REQ_DATE", StringHelper.evl(FrtoSltrec.getFieldString("TRANS_WORD_DATE"), ""));
										tcRecord1.setField("TRN_REQ_SEQ", StringHelper.evl(FrtoSltrec.getFieldString("TRANS_WORD_SEQNO"), ""));
										tcRecord1.setField("CAR_NO", StringHelper.evl(FrtoSltrec.getFieldString("TRN_EQP_CD"), ""));
										tcRecord1.setField("YD_GP",  StringHelper.evl(FrtoSltrec.getFieldString("YD_GP"), ""));
										tcRecord1.setField("DIST_GOODS_GP", "H");
										tcRecord1.setField("YARD_GP", "");
										tcRecord1.setField("ARR_YD_PNT_CD", StringHelper.evl(FrtoSltrec.getFieldString("ARR_YD_PNT_CD"), ""));
										tcRecord1.setField("CARUD_CMPL_DATE", YmCommonUtil.getCurDate("yyyyMMdd"));
										tcRecord1.setField("CARUD_CMPL_TIME", YmCommonUtil.getCurDate("HHmmss"));
//									} else {
//										tcRecord1.setField("CARD_NO","");
//										tcRecord1.setField("CAR_NO", StringHelper.evl(FrtoSltrec.getFieldString("TRN_EQP_CD"), ""));					    	
//										tcRecord1.setField("ARR_YD_PNT_CD", StringHelper.evl(FrtoSltrec.getFieldString("ARR_YD_PNT_CD"), ""));
//										tcRecord1.setField("ISSUE_DDTT", StringHelper.evl(FrtoSltrec.getFieldString("ISSUE_DDTT"), ""));
//									}
								}
							}
							
							tcRecord.setField("STL_NO" + (1+nIdx), StringHelper.evl(FrtoSltrec.getFieldString("STL_NO"), ""));
							tcRecord.setField("STL_WT" + (1+nIdx), StringHelper.evl(FrtoSltrec.getFieldString("STL_WT"), ""));
	
						    //C열연 이송인 경우에만 적용 
						    String sARRWLOCCD=  StringHelper.evl(FrtoSltrec.getFieldString("ARR_WLOC_CD"), "");
						    
						    if(sARRWLOCCD.equals("DJY21")||sARRWLOCCD.equals("DJY22")||sARRWLOCCD.equals("DJY1E")){
							    //C열연 코일 저장품 등록
							    CoilSpecRegSeEJBBean.stockProcCom(StringHelper.evl(FrtoSltrec.getFieldString("STL_NO"), ""),1);
							     
								//TB_PT_COILCOMM update 시험생산제품구분='C'로 UPDATE 적용(2010.12.31까지)				
								String stkQueryId2 = "ym.facilitywork.putwrecord.session.updateCoilcomm";
								iSeq = ydStockDAO.requestupdateData(stkQueryId2, new Object[]{ StringHelper.evl(FrtoSltrec.getFieldString("STL_NO"), "")});
						    }
							
							//(제품인 경우에만 출하에 전송 한다.)
							if(stl_appear_gp.equals("Y")){
								
//								if("Y".equals(sApplyYnPI)) {
									tcRecord1.setField("GOODS_NO"+ (1+nIdx) , StringHelper.evl(FrtoSltrec.getFieldString("STL_NO"), "").trim());
							    	tcRecord1.setField("STORE_LOC_CD" + (1+nIdx), StringHelper.evl(FrtoSltrec.getFieldString("STORE_LOC_CD"), ""));
//								} else {
//									tcRecord1.setField("GOODS_NO"+ (1+nIdx) , StringHelper.evl(FrtoSltrec.getFieldString("STL_NO"), "").trim());
//							    	tcRecord1.setField("TRANS_WORD_DATE" + (1+nIdx), StringHelper.evl(FrtoSltrec.getFieldString("TRANS_WORD_DATE"), ""));
//							    	tcRecord1.setField("TRANS_WORD_SEQNO" + (1+nIdx), StringHelper.evl(FrtoSltrec.getFieldString("TRANS_WORD_SEQNO"), ""));
//							    	tcRecord1.setField("STORE_LOC_CD" + (1+nIdx), StringHelper.evl(FrtoSltrec.getFieldString("STORE_LOC_CD"), ""));
//							    	tcRecord1.setField("YD_GP"+ (1+nIdx) , StringHelper.evl(FrtoSltrec.getFieldString("YD_GP"), ""));
//							    	tcRecord1.setField("BAY_GP"+ (1+nIdx) , StringHelper.evl(FrtoSltrec.getFieldString("BAY_GP"), ""));
//							    	tcRecord1.setField("SPAN" + (1+nIdx), StringHelper.evl(FrtoSltrec.getFieldString("SPAN_GP"), ""));
//							    	tcRecord1.setField("STK_LYR" + (1+nIdx), StringHelper.evl(FrtoSltrec.getFieldString("STACK_LAYER_GP"), ""));
//								}
							}
							
							nIdx ++;
			    		}
			    		
			    		
						tcRecord.setField("CARLD_CMPL_DT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
									
						   EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
						   isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{JDTORecord.class},
						  	  	             new Object[]{tcRecord});
						   logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 구내운송 이송상차완료실적.===>>"+nIdx);				
						
			    		// 출하고간이송상차완료실적 송신(제품인 경우에만 출하에 전송 한다.)################################################
						if (stl_appear_gp.equals("Y")) {
//							if("Y".equals(sApplyYnPI)) {
								tcRecord1.setField("MQ_TC_CD", "M10YDLMJ1121");
								tcRecord1.setField("MQ_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
								tcRecord1.setField("UPCARUNLOAD_GP", "U"); //U:상차,D:하차
						    	tcRecord1.setField("GOODS_CNT",Integer.toString(iSeqCount)); //처리개수
//							} else {
//								tcRecord1.setField("JMS_TC_CD", "YDDMR021");
//								tcRecord1.setField("TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
//								tcRecord1.setField("UPCARUNLOAD_GP", "U"); //U:상차,D:하차
//						    	tcRecord1.setField("TREAT_EA",Integer.toString(iSeqCount)); //처리개수
//							}

							EJBConnector ejbConn2 = new EJBConnector("default","JNDIYardWrkResReg",this);
							isSuccess = (Boolean)ejbConn2.trx("sendInternalModel",new Class[]{JDTORecord.class},
							  	  	 new Object[]{tcRecord1});
							logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 출하제품 이송상차완료실적.===>>"+nIdx);
						}
						//####################################################################################################
						
						
	    			}else if( szTRANS_EQUIPMENT_TYPE.equals("P") ) {					//출하PDA
						
	    				// 코일이송상차개시 전송PDA#############################################################################
						JDTORecord tcRecordDM = null;

						tcRecordDM = JDTORecordFactory.getInstance().create();
						
						// PIDEV
//						if ("Y".equals(sApplyYnPI)) {
							tcRecordDM.setField("MQ_TC_CD" , "M10YDLMJ1091");
							tcRecordDM.setField("MQ_TC_CREATE_DDTT" , new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
							tcRecordDM.setField("TRN_REQ_DATE" , StringHelper.evl(FrtoProduct.getFieldString("TRANS_WORD_DATE") , ""));
							tcRecordDM.setField("TRN_REQ_SEQ" , StringHelper.evl(FrtoProduct.getFieldString("TRANS_WORD_SEQNO") , ""));
							tcRecordDM.setField("CAR_NO" , StringHelper.evl(FrtoProduct.getFieldString("CAR_NO") , ""));
							tcRecordDM.setField("CARD_NO" , StringHelper.evl(FrtoProduct.getFieldString("CARD_NO") , ""));
							tcRecordDM.setField("YD_GP" , jtR.getFieldString("Yard_Id"));
							tcRecordDM.setField("DIST_GOODS_GP" , "H");
							tcRecordDM.setField("SCH_YN" , "Y");
							tcRecordDM.setField("CARLD_CMPL_DATE" , YmCommonUtil.getCurDate("yyyyMMdd"));
							tcRecordDM.setField("CARLD_CMPL_TIME" , YmCommonUtil.getCurDate("HHmmss"));
							tcRecordDM.setField("CR_FRTOMOVE_GP" , StringHelper.evl(FrtoProduct.getFieldString("CR_FRTOMOVE_GP") , ""));								
							
//						} else {
//							tcRecordDM.setField("JMS_TC_CD" , "YDDMR073");
//							tcRecordDM.setField("TC_CREATE_DDTT" , new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
//							tcRecordDM.setField("CARD_NO" , StringHelper.evl(FrtoProduct.getFieldString("CARD_NO") , ""));
//							tcRecordDM.setField("CAR_NO" , StringHelper.evl(FrtoProduct.getFieldString("CAR_NO") , ""));
//							tcRecordDM.setField("YD_GP" , jtR.getFieldString("Yard_Id"));
//							tcRecordDM.setField("CARLOAD_END_DATE" , YmCommonUtil.getCurDate("yyyyMMdd"));
//							tcRecordDM.setField("CARLOAD_END_TIME" , YmCommonUtil.getCurDate("HHmmss"));
//							tcRecordDM.setField("TRANS_WORD_DATE" , StringHelper.evl(FrtoProduct.getFieldString("TRANS_WORD_DATE") , ""));
//							tcRecordDM.setField("TRANS_WORD_SEQNO" , StringHelper.evl(FrtoProduct.getFieldString("TRANS_WORD_SEQNO") , ""));
//							tcRecordDM.setField("CR_FRTOMOVE_GP" , StringHelper.evl(FrtoProduct.getFieldString("CR_FRTOMOVE_GP") , ""));
//						}

						// 인터페이스 전문 호출
						EJBConnector ejbConn1 = new EJBConnector("default" , "JNDIYardWrkResReg" , this);
						isSuccess = (Boolean) ejbConn1.trx("sendInternalModel" , new Class[]{JDTORecord.class} , new Object[]{tcRecordDM});
						//####################################################################################################
						
						
						//진행관리 냉연코일이송진행 상태실적   *********************************************
						JDTORecord recPara         = null;
						JDTORecord recGetVal       = null;
						JDTORecordSet rsCoilResult = null; 
						YdCarSchDao	ydCarSchDao			= new YdCarSchDao();
						recPara = JDTORecordFactory.getInstance().create();			
						rsCoilResult 	= JDTORecordFactory.getInstance().createRecordSet("");
						
						recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
						/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarYdStockYdCarFtmvMtlMES*/
						intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsCoilResult, 434);		
						if(intRtnVal > 0) {						 	
						
							String szMsg = "상차완료 처리 대상 건수  [Ret : " + rsCoilResult.size() + "]";
							logger.println(LogLevel.DEBUG,this, szMsg);
							
							for(int Idx=0; Idx<intRtnVal; Idx++) {
								recGetVal = rsCoilResult.getRecord(Idx);
							 
						        //진행관리 냉연코일이송진행 상태실적 
				    			recInTemp = JDTORecordFactory.getInstance().create();
								recInTemp.setField("MSG_ID",        "YDPTJ006"); 
								recInTemp.setField("STL_NO", StringHelper.evl(recGetVal.getFieldString("STL_NO"), "") );
								ydDelegate.sendMsg(recInTemp);
						        
							}
						}
						//진행관리 냉연코일이송진행 상태실적   *********************************************
					}
				
	    		}
	    		
	    		
	    		//검수 테이블 생성 //////////////////////////////////////////////////////////////
	    		JDTORecord tcRecordDM = null;
	    		tcRecordDM = JDTORecordFactory.getInstance().create();
                tcRecordDM.setField("STOCK_ID",Coil_No);
                
    			// ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.CreatCarExaminationjlNEW_PIDEV
    			int intRtnVal2 = ydStockDAO.updYdStock10(tcRecordDM, 0);			

    			if(intRtnVal2 >0){
    				String szMsg = "수신한 재료번호 ["+jtR.getFieldString("Coil_No").trim()+"]에 대한 검수 DATA등록이 되었습니다.1";
    		 
    				logger.println(LogLevel.DEBUG,this, szMsg);

    			}else if(intRtnVal2 == 0){
    				String szMsg = "수신한  재료번호 ["+jtR.getFieldString("Coil_No").trim()+"]에 대한 검수 DATA등록 되었거나  실패 하였습니다.1";
    			 
    				logger.println(LogLevel.DEBUG,this, szMsg);
     			}
 	    			    		
			}
			
			
			//##############################################################################################################
			// 코일 하차개시/완료#############################################################################################
			//##############################################################################################################
			if((!YmCommonConst.EQUIP_KIND_PT.equals(jtR.getFieldString("Put_Position").substring(2, 4)) && !YmCommonConst.EQUIP_KIND_TR.equals(jtR.getFieldString("Put_Position").substring(2, 4)))
					   &&
					   (YmCommonConst.NEW_SCH_WORK_KIND_CVMU.equals(Sch_Code)|| //Coil 소재이송하차
				 		   YmCommonConst.NEW_SCH_WORK_KIND_CVM4.equals(Sch_Code)|| // COIL 소재이송하차
					       YmCommonConst.NEW_SCH_WORK_KIND_CVM5.equals(Sch_Code)|| // COIL 소재이송하차
					       YmCommonConst.NEW_SCH_WORK_KIND_GVMU.equals(Sch_Code)|| // COIL 제품이송하차	
						   YmCommonConst.NEW_SCH_WORK_KIND_GVM4.equals(Sch_Code)|| // COIL 제품이송하차
						   YmCommonConst.NEW_SCH_WORK_KIND_GVM5.equals(Sch_Code)   // COIL 제품이송하차	
					    )
					   ){
                String Coil_No = StringHelper.evl(jtR.getFieldString("Coil_No"), "").trim();
				
				String queryID = "ym.facilitywork.putwrecord.session.getListworkQty_Coil1";
	    	
	    		FrtoProductList = dao.getCommonList(queryID, new Object[]{Coil_No,Coil_No,Coil_No});
				
	    		if(FrtoProductList.size() > 0){
			    	JDTORecord StkColRec = (JDTORecord)FrtoProductList.get(0);
			    	tot_Qty  	= StkColRec.getFieldInt("TOT_QTY");
			    	work_Qty  	= StkColRec.getFieldInt("WORK_QTY");
			    	stl_appear_gp=StkColRec.getFieldString("STL_APPEAR_GP");   //재료외형구분 Y: 제품 , 기타: 소재
	    		}	    
	    		 
	    		
	    		JDTORecord FrtoProduct =null;
	    			
	    		queryID	= "ym.facilitywork.putwrecord.session.getListTcconst_1";
		    	FrtoProductList = dao.getCommonList(queryID, new Object[]{Coil_No});
		    	if(FrtoProductList.size() > 0){
		    		FrtoProduct = (JDTORecord)FrtoProductList.get(0);
		    	}
		    	
		    	String szTRANS_EQUIPMENT_TYPE		= StringHelper.evl(FrtoProduct.getFieldString("TRANS_EQUIPMENT_TYPE"), ""); //운송장비TYPE P:PDA
	    		 
		    	
	    		//하차 권하 시 건단위 실적처리로 변경
				//####################################################################################################	    		
	    		{
	    	 	logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶코일 이송하차 실적처리 START ◀◀◀◀◀◀◀◀◀◀"); 
	    	 	EJBConnector ejbConn3 = new EJBConnector("default","JNDICPutResReg",this);
	    	 	Boolean isYd = (Boolean)ejbConn3.trx("updateMatlFtmvWlrstCoil",new  Class[]{String.class},
															new Object[]{Coil_No});
	    	 	
	    	 	//저장품 이동조건 업데이트 
				rVal = YmCommonUtil.getCoilCurrProgCd(Coil_No,"");
				String sStocMv = rVal[1];
				queryID = "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.updYdStock";						
				ydStockDAO.requestupdateData(queryID,new Object[]{ sStocMv,"YDTSJ010",Coil_No });
	    	 	
				ymCommonDAO dao2 = ymCommonDAO.getInstance();
			    queryID 			= "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getCOILCOMM";
				List productList 	= dao2.getCommonList(queryID, new Object[]{Coil_No});
				JDTORecord stlRecord = (JDTORecord)productList.get(0);
				
	            
				if(!stl_appear_gp.equals("Y"))
				{
					
					//TB_PT_STLFRTOMOVE update				
					String stkQueryId = "ym.facilitywork.putwrecord.session.updateunLoadTimeToPT";
					int iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ Coil_No,Coil_No}); 
					
					JDTORecord tcRecord2 = null;
					
				    //코일소재 이송완료실적(YDPTJ002)
				    tcRecord2 =JDTORecordFactory.getInstance().create();
				    tcRecord2.setField("JMS_TC_CD", "YDPTJ002");
				    tcRecord2.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
				    tcRecord2.setField("STL_NO",  StringHelper.evl(stlRecord.getFieldString("COIL_NO"), ""));
				    // 주문번호
				    tcRecord2.setField("ORD_NO",  StringHelper.evl(stlRecord.getFieldString("ORD_NO"), ""));
				    // 주문행번
				    tcRecord2.setField("ORD_DTL", StringHelper.evl(stlRecord.getFieldString("ORD_DTL"), ""));
				    // 공장공정코드
				    tcRecord2.setField("PLNT_PROC_CD",  StringHelper.evl(stlRecord.getFieldString("PLNT_PROC_CD"), ""));
				    // 재료외형구분
				    tcRecord2.setField("STL_APPEAR_GP",  StringHelper.evl(stlRecord.getFieldString("STL_APPEAR_GP"), ""));
				    // 현재진도코드
				    tcRecord2.setField("CURR_PROG_CD",  StringHelper.evl(stlRecord.getFieldString("CURR_PROG_CD"), ""));
				    // 주문여재구분
				    tcRecord2.setField("ORD_YEOJAE_GP", StringHelper.evl(stlRecord.getFieldString("ORD_YEOJAE_GP"), ""));
				    // 재료중량 (SLAB중량) 
				    tcRecord2.setField("STL_WT",  StringHelper.evl(stlRecord.getFieldString("COIL_WT"), ""));
			    	// 설계재료중량(항목명?)
			    	tcRecord2.setField("DS_MTL_WT", "");
				    // 재료상태구분(항목명?)
				    tcRecord2.setField("MTL_STAT_GP",  StringHelper.evl(stlRecord.getFieldString("RECORD_PROG_STAT"), ""));
				    // Record 종료구분
				    tcRecord2.setField("RECORD_END_GP",  StringHelper.evl(stlRecord.getFieldString("RECORD_END_GP"), ""));
				    // Record 종료구분 1(항목명?)
				    tcRecord2.setField("RECORD_END_GP1", "");
				    // 전진도 코드
				    tcRecord2.setField("BEFO_PROG_CD",  StringHelper.evl(stlRecord.getFieldString("BEFO_PROG_CD"), ""));
				    // 전주문 번호
				    tcRecord2.setField("BEF_ORD_NO",  StringHelper.evl(stlRecord.getFieldString("BEF_ORD_NO"), ""));
				    // 전주문 행번
				    tcRecord2.setField("BEF_ORD_DTL",  StringHelper.evl(stlRecord.getFieldString("BEF_ORD_DTL"), ""));
				    // 모재료번호   
				    tcRecord2.setField("MMATL_FEE_NO",  StringHelper.evl(stlRecord.getFieldString("MMATL_FEE_NO"), ""));
				    // 목전충당구분
				    tcRecord2.setField("ORDERTRANS_MATCH_GP", StringHelper.evl(stlRecord.getFieldString("MATCH_ORDERTRANS_GP"), ""));	
				
				    EJBConnector ejbConn2 = new EJBConnector("default","JNDIYardWrkResReg",this);
				    isSuccess = (Boolean)ejbConn2.trx("sendInternalModel",new Class[]{JDTORecord.class},
				  	  	 new Object[]{tcRecord2});
				    logger.println(LogLevel.DEBUG,this, "내부IF호출===코일소재 이송완료실적.===");
				}
				logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶코일 이송하차 실적처리 END  ◀◀◀◀◀◀◀◀◀◀"); 
	    		}
			 
				//####################################################################################################
	    			    	
			
//	    		if(work_Qty == 1) //개시
//	    		{
//	    			//차량스케쥴테이블 - 하차개시시간 업데이트
//	    			queryID = "ym.facilitywork.putwrecord.session.updateUnloadstartTime";
//			    	count = dao.updateData(queryID, new Object[]{Coil_No}); 
//			    	
//			    	queryID	= "ym.facilitywork.putwrecord.session.getListTcconst_1";
//			    	FrtoProductList = dao.getCommonList(queryID, new Object[]{Coil_No});
//			    	JDTORecord FrtoProduct = (JDTORecord)FrtoProductList.get(0);
//			    	
//	    			
//	    			//하차개시실적 송신
//	    			//소재차량하차개시
//	    			JDTORecord tcRecord = null;
//					tcRecord = JDTORecordFactory.getInstance().create(); 
//					tcRecord.setField("JMS_TC_CD", "YDTSJ009");
//					tcRecord.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
//					
//					tcRecord.setField("TRN_EQP_CD", StringHelper.evl(FrtoProduct.getFieldString("TRN_EQP_CD"), ""));
//					tcRecord.setField("ARR_WLOC_CD", StringHelper.evl(FrtoProduct.getFieldString("ARR_WLOC_CD"), ""));
//					tcRecord.setField("ARR_YD_PNT_CD", StringHelper.evl(FrtoProduct.getFieldString("YD_PNT_CD"), ""));
//					tcRecord.setField("TRN_WRK_ST_DT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
//			
//					EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
//					isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{JDTORecord.class},
//					  	  	 new Object[]{tcRecord});
//					logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 구내운송 이송하차개시실적.===");
//					
//				
//					//20091111 개시 시점에 상차대상을 한번에 전송 한다.
//					//출하고간이송상차개시실적 송신(제품인 경우에만 출하에 전송 한다.)################################################
//					if(stl_appear_gp.equals("Y")){						
//						//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//	                    //코일제품고간이송상하차개시
//						
//				    	queryID	= "ym.facilitywork.putwrecord.session.getListTcconst22_PIDEV";
//				    	FrtoProductList = dao.getCommonList(queryID, new Object[]{StringHelper.evl(FrtoProduct.getFieldString("TRN_EQP_CD"), "")});
//		    	
//				    	
//	         			JDTORecord tcRecordDM = null;
//	         			
//	         			tcRecordDM = JDTORecordFactory.getInstance().create(); 
//	         			tcRecordDM.setField("JMS_TC_CD", "YDDMR019");
//						tcRecordDM.setField("TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
//	         			tcRecordDM.setField("UPCARUNLOAD_GP", "D"); //U:상차,D:하차
//	                    tcRecordDM.setField("CARD_NO","");
//	                    tcRecordDM.setField("CAR_NO", StringHelper.evl(FrtoProduct.getFieldString("TRN_EQP_CD"), ""));
//	                    tcRecordDM.setField("YD_GP", jtR.getFieldString("Yard_Id"));	
//	                    tcRecordDM.setField("CARLOAD_START_DATE", YmCommonUtil.getCurDate("yyyyMMdd"));
//	                    tcRecordDM.setField("CARLOAD_START_TIME", YmCommonUtil.getCurDate("HHmmss"));
//	                    
//	    	    		int iSeqCount 	= FrtoProductList.size();
//			    		for(int i=0; i < iSeqCount ; i++){
//							
//				    		JDTORecord FrtoProduct2 = (JDTORecord)FrtoProductList.get(i);
//				    		
//				    		tcRecordDM.setField("GOODS_NO" 			+ (1+i),StringHelper.evl(FrtoProduct2.getFieldString("STL_NO"), ""));
//		                    tcRecordDM.setField("TRANS_WORD_DATE" 	+ (1+i),StringHelper.evl(FrtoProduct2.getFieldString("TRANS_WORD_DATE"), ""));
//		                    tcRecordDM.setField("TRANS_WORD_SEQNO" 	+ (1+i),StringHelper.evl(FrtoProduct2.getFieldString("TRANS_WORD_SEQNO"), ""));
//	         				         		
//			    		}
//	         			//인터페이스 전문 호출
//					   EJBConnector ejbConn1 = new EJBConnector("default","JNDIYardWrkResReg",this);
//					   isSuccess = (Boolean)ejbConn1.trx("sendInternalModel",new Class[]{JDTORecord.class},
//					  	  	             new Object[]{tcRecordDM}); 
//	                    logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 코일제품고간이송상하차개시.===");
//	                    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//	                    
//					}
//					//####################################################################################################
//	    		}
//			}
			
//	    		
//	    		if(tot_Qty == work_Qty) //완료 
//	    		{			    	
//			    	queryID	= "ym.facilitywork.putwrecord.session.getListTcconst_1";
//			    	FrtoProductList = dao.getCommonList(queryID, new Object[]{Coil_No});
//			    	JDTORecord FrtoProduct = (JDTORecord)FrtoProductList.get(0);
//			    	
//			    	//차량스케쥴ID로 이송재료 조회(
//		    		String trnQueryId = "ym.tsinfo.getListFrtostlList_loadEnd_PIDEV";
//		    		FrtostlList = dao.getCommonList(trnQueryId, new Object[]{StringHelper.evl(FrtoProduct.getFieldString("TRN_EQP_CD"), "")});
//		    		/*
//		    		 * SELECT STL_NO,SUBSTR(YD_STK_LYR_NO,2,2)STK_LYR
//	                   FROM TB_YD_CARFTMVMTL A, TB_YD_CARSCH B
//	                   WHERE A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
//	                   AND A.DEL_YN = 'N'
//	                   AND B.TRN_EQP_CD = ?
//	                   ORDER BY A.YD_STK_LYR_NO 
//		    		 */
//	    			
//		    		//차량스케쥴테이블 - 하차완료시간 업데이트
//	    			queryID = "ym.facilitywork.putwrecord.session.updateUnloadendTime";
//			    	count = dao.updateData(queryID, new Object[]{Coil_No});
//			    	
//			    	//차량재료테이블 - DEL_YN 업데이트
//	    			queryID = "ym.facilitywork.putwrecord.session.deletefrmtList";
//			    	count = dao.updateData(queryID, new Object[]{Coil_No});
//			    	
//			    	JDTORecord tcRecord1 = null;
//			    	
//					tcRecord1 = JDTORecordFactory.getInstance().create(); 
//					
//			    	int nIdx = 0;
//			    	
//		    		int iSeqCount 	= FrtostlList.size();
//		    		for(int i=0; i < iSeqCount ; i++){
//						
//			    		JDTORecord FrtoSltrec = (JDTORecord)FrtostlList.get(i);
//								
//						//출하고간이송상차완료실적 송신(제품인 경우에만 출하에 전송 한다.)################################################
//						if(stl_appear_gp.equals("Y")){
//							//코일제품고간이송상하차완료							
//		         									    
//							if(i==0){		         
//							tcRecord1.setField("JMS_TC_CD", "YDDMR021");
//							tcRecord1.setField("TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
//							tcRecord1.setField("UPCARUNLOAD_GP", "D"); //U:상차,D:하차
//					    	tcRecord1.setField("TREAT_EA",Integer.toString(iSeqCount)); //처리개수
//							tcRecord1.setField("CARD_NO","");
//							tcRecord1.setField("CAR_NO", StringHelper.evl(FrtoSltrec.getFieldString("TRN_EQP_CD"), ""));					    	
//					    	tcRecord1.setField("ARR_YD_PNT_CD", StringHelper.evl(FrtoSltrec.getFieldString("ARR_YD_PNT_CD"), ""));
//					    	tcRecord1.setField("ISSUE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
//							}
//							
//					    	tcRecord1.setField("GOODS_NO" +(nIdx+1) , StringHelper.evl(FrtoSltrec.getFieldString("STL_NO"), ""));
//					    	tcRecord1.setField("TRANS_WORD_DATE" +(nIdx+1) , StringHelper.evl(FrtoSltrec.getFieldString("TRANS_WORD_DATE"), ""));
//					    	tcRecord1.setField("TRANS_WORD_SEQNO" +(nIdx+1) , StringHelper.evl(FrtoSltrec.getFieldString("TRANS_WORD_SEQNO"), ""));
//					    	tcRecord1.setField("STORE_LOC_CD" +(nIdx+1) , StringHelper.evl(FrtoSltrec.getFieldString("STORE_LOC_CD"), ""));
//					    	tcRecord1.setField("YD_GP"  +(nIdx+1), StringHelper.evl(FrtoSltrec.getFieldString("YD_GP"), ""));
//					    	tcRecord1.setField("BAY_GP" +(nIdx+1) , StringHelper.evl(FrtoSltrec.getFieldString("BAY_GP"), ""));
//					    	tcRecord1.setField("SPAN" +(nIdx+1) , StringHelper.evl(FrtoSltrec.getFieldString("SPAN_GP"), ""));
//					    	tcRecord1.setField("STK_LYR" +(nIdx+1) , StringHelper.evl(FrtoSltrec.getFieldString("STACK_LAYER_GP"), ""));
//						}
//						//####################################################################################################
//						
//						   nIdx ++;
//		    		}
//					
//		    		// 출하고간이송상차완료실적 송신(제품인 경우에만 출하에 전송 한다.)################################################
//		    		if(stl_appear_gp.equals("Y")){
// 
//				
//					EJBConnector ejbConn2 = new EJBConnector("default","JNDIYardWrkResReg",this);
//					isSuccess = (Boolean)ejbConn2.trx("sendInternalModel",new Class[]{JDTORecord.class},
//					  	  	 new Object[]{tcRecord1});
//					logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 출하제품 이송상차완료실적.===>>"+nIdx);
//		    		}
//					//####################################################################################################
//	    		  			
//	    			//하차완료실적 송신
//     			    //소재차량하차완료
//	    			JDTORecord tcRecord = null;
//					tcRecord = JDTORecordFactory.getInstance().create(); 
//					tcRecord.setField("JMS_TC_CD", "YDTSJ010");
//					tcRecord.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
//					tcRecord.setField("TRN_EQP_CD", StringHelper.evl(FrtoProduct.getFieldString("TRN_EQP_CD"), ""));
//					tcRecord.setField("ARR_WLOC_CD", StringHelper.evl(FrtoProduct.getFieldString("ARR_WLOC_CD"), ""));
//					tcRecord.setField("ARR_YD_PNT_CD", StringHelper.evl(FrtoProduct.getFieldString("YD_PNT_CD"), ""));
//					tcRecord.setField("CARUD_CMPL_DT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
// 				
//					EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
//					isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{JDTORecord.class},
//					  	  	 new Object[]{tcRecord});
//					logger.println(LogLevel.DEBUG,this, "내부IF호출===코일 일관제철 구내운송 이송하차완료실적.===");
//
//	    		}
// 
//			}
			
    		if(tot_Qty == work_Qty) //완료 
    		{
    			 if (szTRANS_EQUIPMENT_TYPE.equals("P")) { // 출하PDA
    				 
    				 
 		    		//차량스케쥴테이블 - 하차완료시간 업데이트
 	    			queryID = "ym.facilitywork.putwrecord.session.updateUnloadendTime";
 			    	count = dao.updateData(queryID, new Object[]{Coil_No});
 			    	
 			    	//차량재료테이블 - DEL_YN 업데이트
 	    			queryID = "ym.facilitywork.putwrecord.session.deletefrmtList";
 			    	count = dao.updateData(queryID, new Object[]{Coil_No});

					// 코일이송하차완료PDA
					JDTORecord tcRecordDM = null;
					tcRecordDM = JDTORecordFactory.getInstance().create();
					
					// PIDEV
//					if ("Y".equals(sApplyYnPI)) {
						tcRecordDM.setField("MQ_TC_CD" , "M10YDLMJ1121");
						tcRecordDM.setField("MQ_TC_CREATE_DDTT" , new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
						tcRecordDM.setField("TRN_REQ_DATE" , StringHelper.evl(FrtoProduct.getFieldString("TRANS_WORD_DATE") , ""));
						tcRecordDM.setField("TRN_REQ_SEQ" , StringHelper.evl(FrtoProduct.getFieldString("TRANS_WORD_SEQNO") , ""));						
						tcRecordDM.setField("CAR_NO" , StringHelper.evl(FrtoProduct.getFieldString("CAR_NO") , ""));
						tcRecordDM.setField("YD_GP" , jtR.getFieldString("Yard_Id"));
						tcRecordDM.setField("DIST_GOODS_GP" , "H");
						tcRecordDM.setField("YARD_GP" , "");
						tcRecordDM.setField("CARUD_CMPL_DATE" , YmCommonUtil.getCurDate("yyyyMMdd"));
						tcRecordDM.setField("CARUD_CMPL_TIME" , YmCommonUtil.getCurDate("HHmmss"));					
//					} else {
//						tcRecordDM.setField("JMS_TC_CD" , "YDDMR076");
//						tcRecordDM.setField("TC_CREATE_DDTT" , new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
//						tcRecordDM.setField("CARD_NO" , StringHelper.evl(FrtoProduct.getFieldString("CARD_NO") , ""));
//						tcRecordDM.setField("CAR_NO" , StringHelper.evl(FrtoProduct.getFieldString("CAR_NO") , ""));
//						tcRecordDM.setField("YD_GP" , jtR.getFieldString("Yard_Id"));
//						tcRecordDM.setField("CARUD_END_DATE" , YmCommonUtil.getCurDate("yyyyMMdd"));
//						tcRecordDM.setField("CARUD_END_TIME" , YmCommonUtil.getCurDate("HHmmss"));
//						tcRecordDM.setField("TRANS_WORD_DATE" , StringHelper.evl(FrtoProduct.getFieldString("TRANS_WORD_DATE") , ""));
//						tcRecordDM.setField("TRANS_WORD_SEQNO" , StringHelper.evl(FrtoProduct.getFieldString("TRANS_WORD_SEQNO") , ""));
//						tcRecordDM.setField("CR_FRTOMOVE_GP" , StringHelper.evl(FrtoProduct.getFieldString("CR_FRTOMOVE_GP") , ""));
//					}

					// 인터페이스 전문 호출
					EJBConnector ejbConn1 = new EJBConnector("default" , "JNDIYardWrkResReg" , this);
					isSuccess = (Boolean) ejbConn1.trx("sendInternalModel" , new Class[]{JDTORecord.class} , new Object[]{tcRecordDM});
				}
    		}
			}
			
			return isSuccess.booleanValue();

		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}
	
	
	
       /**
	 * 오퍼레이션명 : 
	 *
	 *	3.	SLAB INFO
        *		권하작업실적 발생시 작업결과송신 Call
        *		처리결과 메세지를 야드 Level-2에 송신한다.
        * 
        * param JDTORecord 	: 전문항목
        *
        * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */      
	public boolean callWorkFinishInfo_Slab(JDTORecord tcRc){
		
		Boolean isSuccess = new Boolean(false);
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			isSuccess = Boolean.TRUE;
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue();
	}
	       
       /**
	 * 오퍼레이션명 : 
	 *
	 *	4.	SLAB INFO
        *		권하작업실적 발생시 작업결과송신 Call
        *		처리결과 메세지를 B열연 MILL Level-2에 송신한다.
        * 
        * param JDTORecord 	: 전문항목
        *
        * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */      
	public boolean callMillFinishInfo_Slab(JDTORecord tcRc){
		
		Boolean isSuccess = new Boolean(false);
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String tWork_Id 		= YmCommonUtil.getCurDataWithLegacy(
									  StringHelper.evl(tcRc.getFieldString("Work_Id"), ""));
			if(YmCommonConst.SUB_WORK_S.equals(tWork_Id)){
				logger.println(LogLevel.DEBUG,this, "외부IF호출===보조작업대상으로 작업실적전송 SKIP.===");
				return false;
			}
			
			String sTc = StringHelper.evl(tcRc.getFieldString("TC"), "");
						
			if(YmCommonConst.TC_CM1PB06.equals(sTc)|| //B열연 Slab 권하실적		
			   YmCommonConst.TC_CM1PB07.equals(sTc)){ //B열연 Slab 권하이상실적
			  
				logger.println(LogLevel.DEBUG,this, "==B열연 SLAB MILL L2 외부IF 호출==");
				
				String sStockId         = StringHelper.evl(tcRc.getFieldString("Slab_No"), "").trim();
				String sSch_Code 		= StringHelper.evl(tcRc.getFieldString("Sch_Code"), "");
				String sWork_Id 		= YmCommonUtil.getCurDataWithLegacy(
										  StringHelper.evl(tcRc.getFieldString("Work_Id"), ""));
				String sPut_Position 	= StringHelper.evl(tcRc.getFieldString("Put_Position"), "");
				
				String sGstockId		= StringHelper.evl(tcRc.getFieldString("GROBAL_STOCK_ID"), "");
				String sGlocation		= StringHelper.evl(tcRc.getFieldString("GROBAL_LOCATION"), "");
					
				/*
				 * B열연 L2로부터 Slab Line Off 요구를 받아 Slab Line Off 완료시 호출
				 * SLAB Line Off Result	Level3 Mill L2	CF1BP03
				 * Param : 
				 */
				//Holding Bed CCR 추출(SHLO) 스케쥴코드 체크
				if(YmCommonConst.NEW_SCH_WORK_KIND_SHLO.equals(sSch_Code))
				{
					//2매 작업시 처리
					if(!"".equals(sGstockId)){
						EJBConnector ejbConn = new EJBConnector("default","JNDISRTExtWrkOrdReg",this);
						isSuccess = (Boolean)ejbConn.trx("receiveSlabLineOffResult",new  Class[]{String.class},
																					new Object[]{sGstockId});
					}
					
					EJBConnector ejbConn = new EJBConnector("default","JNDISRTExtWrkOrdReg",this);
					isSuccess = (Boolean)ejbConn.trx("receiveSlabLineOffResult",new  Class[]{String.class},
																				new Object[]{sStockId});
					logger.println(LogLevel.DEBUG,this, "외부IF호출===B열연 L2로부터 Slab Line Off 요구를 받아 Slab Line Off 완료시 호출.===");
				}
				
				/*
				 * B열연 L2로부터 #3 CTC, #4 CTC Slab Loading  완료시 호출
				 * SLAB Loading Result Level3 Mill L2	CF1BP12
				 * Param : 
				 */
				//CTC #3 CCR 보급(SCLI) 스케쥴코드 체크
				if(YmCommonConst.NEW_SCH_WORK_KIND_SCLI.equals(sSch_Code))
				{
					//2매 작업시 처리
					if(!"".equals(sGstockId)){
						EJBConnector ejbConn = new EJBConnector("default","JNDISRTExtWrkOrdReg",this);
						isSuccess = (Boolean)ejbConn.trx("receiveSlabLoadingResult",new  Class[]{String.class,
																								 String.class},
																				    new Object[]{sGstockId,
																				    			 sPut_Position});
					}
					EJBConnector ejbConn = new EJBConnector("default","JNDISRTExtWrkOrdReg",this);
					isSuccess = (Boolean)ejbConn.trx("receiveSlabLoadingResult",new  Class[]{String.class,
																							 String.class},
																			    new Object[]{sStockId,
																			    			 sPut_Position});
					logger.println(LogLevel.DEBUG,this, "외부IF호출===B열연 L2로부터 #3 CTC, #4 CTC Slab Loading  완료시 호출.===");
				}  
			}   
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue();
	}
	              
       /**
	 * 오퍼레이션명 : 
	 *
	 *	5.	SLAB INFO
        *		대차 작업지시 Call
        *		권하시 대차에게 작업지시 전문을 Call
        * 
        * param JDTORecord 	: 전문항목
        *
        * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */       
	public boolean callTcWorkInfo_Slab(JDTORecord jtR){
		
		Boolean isSuccess = new Boolean(false);
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String sTc 			 = StringHelper.evl(jtR.getFieldString("TC"), "");
			String sTcNo		 = "";
			String sPut_Position = StringHelper.evl(jtR.getFieldString("Put_Position"), "");
			String sStockId 	 = StringHelper.evl(jtR.getFieldString("Slab_No"), "").trim();	
			String sStackColGp   = (sPut_Position.length() >=  6)? sPut_Position.substring(0, 6):"";
		   	String sStackBedGp   = (sPut_Position.length() >=  8)? sPut_Position.substring(6, 8):"";
		   	String sStackLayerGp = (sPut_Position.length() >= 10)? sPut_Position.substring(8,10):"";
			
			String sGwbookId	= StringHelper.evl(jtR.getFieldString("GROBAL_WBOOK_ID"), "");
			String sGstockId	= StringHelper.evl(jtR.getFieldString("GROBAL_STOCK_ID"), "");
			String sGlocation	= StringHelper.evl(jtR.getFieldString("GROBAL_LOCATION"), "");
									   
			/*
			 * 권하위치항목을 가지고 적치단,설비 TABLE을 JOIN해서
			 * 조건으로 설비종류가 대차인 경우를 체크한다.	
			 */
			String sPutUsageCd = YmCommonUtil.getStackColInfoWithPk(sStackColGp);
			
			/*
			 * 권하위치가 대차일 경우에 
			 * 대차출발 모듈을 호출한다.
			 */
			if(YmCommonConst.STACK_COL_USAGE_CD_CX.equals(sPutUsageCd)){ // 대차정지위치
				 
    			logger.println(LogLevel.DEBUG,this, "====TC 작업지시========");
    			logger.println(LogLevel.DEBUG,this, "sStackColGp="	+ sStackColGp);
    			logger.println(LogLevel.DEBUG,this, "sStackBedGp="	+ sStackBedGp);
    			logger.println(LogLevel.DEBUG,this, "sStackLayerGp="+ sStackLayerGp);
    			logger.println(LogLevel.DEBUG,this, "sStockId="		+ sStockId);
				
				sTcNo = sStackColGp.substring(0,1) + "X"+ 
					  	sStackColGp.substring(2,4) + "0"+ 
					  	sStackColGp.substring(4,5);
				 
				/**
				 * SLAB GRIP 대상재 처리
				 * 2매 권하작업시 상단에 SLAB 정보를 처리한다.
				 */ 
				if(!"".equals(sGstockId)){
					
					if(sGlocation.length() >= 10)
					{
						int iReq = -1;
						
						String sTcStackColGp   = sGlocation.substring(0, 6);
				   		String sTcStackBedGp   = sGlocation.substring(6, 8);
				   		String sTcStackLayerGp = sGlocation.substring(8,10);
																	  				   							
						iReq = dao.updateSlabMoveEquipInfo_01(sGstockId,
					    								   	  sTcNo,
					    								   	  sTcStackBedGp,
					    								   	  sTcStackLayerGp);
						
						{ 
							String sCurrQty	= "0";
							
							sCurrQty	= "1";
							 
							iReq = dao.updateStackerQtyInfo(sTcStackColGp,
					    									sTcStackBedGp,
					    									sCurrQty);
							
						}
					}
				}
				
				/**
				 * 저장품TABLE의 이동설비항목에 
				 * 권하위치값을 셋팅한다.
				 * tb_ym_stock Table frtomove_equip_gp : 		sPut_Position.substring(0, 6)
				 * tb_ym_stock Table frtomove_equip_bed_gp : 	sPut_Position.substring(6, 8)
				 * tb_ym_stock Table frtomove_equip_layer_gp : 	sPut_Position.substring(8,10)
				 */ 
				if(sPut_Position.length() >= 10)
				{
					int iReq = -1;
					
					iReq = dao.updateSlabMoveEquipInfo_01(sStockId,
				    								   	  sTcNo,
				    								   	  sStackBedGp,
				    								   	  sStackLayerGp);
						
					{ 
						String sCurrQty	= "0";
						
						sCurrQty	= "1";
						 
						iReq = dao.updateStackerQtyInfo(sStackColGp,
				    									sStackBedGp,
				    									sCurrQty);
						
					}
				}
				
				/*
				 * 대차에 권하시에 연속작업횟수를 +1한다.
				 */
				{
					JDTORecord tJr = dao.getEquipInfoWithEquipGp(sTcNo); 
					
					String sCount = "0";
					
					if(tJr != null){
						sCount = StringHelper.evl(tJr.getFieldString("CTS_RELAY_BAY"), "0");
					}
					
					int iCnt = dao.updateEquipSaddleInfo(sTcNo,
														 (Integer.parseInt(sCount)+1)+"");
				}
				/**
				 * 대차출발 EJB CALL
				 */
				String sMessage = sGwbookId + sStackColGp + sStackBedGp;					
				
				logger.println(LogLevel.DEBUG,this, "====TC 작업지시 CALL====");
				logger.println(LogLevel.DEBUG,this, "MESSAGE="	+ sMessage);
    			
				EJBConnector ejbConn = new EJBConnector("default","JNDICTSStatusReg",this);
				isSuccess = (Boolean)ejbConn.trx("bcyVicCarMoveOrder",new Class[]{String.class},
																  	  new Object[]{sMessage});
			}
			
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue();
	}
       
       /**
	 * 오퍼레이션명 : 
	 *
	 *	6.	SLAB INFO
        *		CTS 작업지시 Call
        * 
        * param JDTORecord 	: 전문항목
        *
        * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */       
	public boolean callCtsWorkInfo_Slab(JDTORecord jtR){
		
		Boolean isSuccess = new Boolean(false);
    
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			isSuccess = Boolean.TRUE;
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue();
	}
	
       /**
	 * 오퍼레이션명 : 
	 *
	 *	7.	SLAB INFO
        *		Next 작업지시 Call
        * 
        * param JDTORecord 	: 전문항목
        *
        * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */       
	public boolean callCraneSchInfo_Slab(JDTORecord jtR){
		
		Boolean isSuccess = new Boolean(false);
    
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String sTc				= YmCommonConst.TC_CM1PB02; //B열연 Slab 권하실적
																//B열연 Slab 권하이상실적
			String sYard_Id 		= StringHelper.evl(jtR.getFieldString("Yard_Id"), "");
			String sBay_Gp 			= StringHelper.evl(jtR.getFieldString("Bay_GP"), "");
			String sEquip_Kind 		= StringHelper.evl(jtR.getFieldString("Equip_Kind"), "");
			String sEquip_No 		= StringHelper.evl(jtR.getFieldString("Equip_No"), "");
			String sSch_Code 		= StringHelper.evl(jtR.getFieldString("Sch_Code"), "");
			String sWbookId         = StringHelper.evl(jtR.getFieldString("GROBAL_WBOOK_ID"), "");
			
			if("0".equals(sYard_Id) && "A".equals(sBay_Gp)){
				sTc = YmCommonConst.TC_HM1PB02;					//A_A열연 SLAB(2007-02-26(MCH)) 작업지시 추가 
			}else if("0".equals(sYard_Id) && "B".equals(sBay_Gp)){
				sTc = YmCommonConst.TC_HM1PB52;					//A_B열연 SLAB(2007-02-26(MCH)) 작업지시 추가 
			}
			logger.println(LogLevel.DEBUG,this, "sYard_Id="			+ sYard_Id);
			logger.println(LogLevel.DEBUG,this, "sBay_Gp="			+ sBay_Gp);
			logger.println(LogLevel.DEBUG,this, "sEquip_Kind="		+ sEquip_Kind);
			logger.println(LogLevel.DEBUG,this, "sEquip_No="		+ sEquip_No);
			logger.println(LogLevel.DEBUG,this, "sSch_Code="		+ sSch_Code);
			logger.println(LogLevel.DEBUG,this, "sTc="				+ sTc);
			logger.println(LogLevel.DEBUG,this, "sWbookId="			+ sWbookId);
			
			JDTORecord schRc = null;
    		
			/**
    		 * 1. 같은 작업예약ID로 묶인 스케쥴이 있는지를 체크한다.
    		 */
    		schRc = dao.getCraneSchInfo(sYard_Id,
    									sBay_Gp,
    									sEquip_Kind,
    									sEquip_No,
    									sSch_Code,
    									sWbookId);
    		if(schRc == null){    			
    			//sWbookId  = "";
    			/**
	    		 * 2. 같은 스케쥴코드로 묶인 스케쥴이 있는지를 체크한다.
	    		 *	  연속작업가능 스케쥴코드에만 해당
	    		 */
	    		boolean isConWork = YmCommonUtil.isContinueWork(sSch_Code); 
	    		if(isConWork){
	    			 
		    		schRc = dao.getCraneSchInfo(sYard_Id,
		    									sBay_Gp,
		    									sEquip_Kind,
		    									sEquip_No,
		    									sSch_Code,
		    									"");
	    		}
	    		
	    		if(schRc == null){
	    			//sSch_Code  = "";
	    			/**
		    		 * 3. 해당크레인에 할당된 스케쥴이 있는지를 체크한다.
		    		 */
		    		schRc = dao.getCraneSchInfo(sYard_Id,
		    									sBay_Gp,
		    									sEquip_Kind,
		    									sEquip_No,
		    									"",
		    									"");
		    	}							
	    	}
		    
	    	if(schRc == null){
	    		/**
	    		 * 스케쥴이 존재하지 않으면
	    		 * 크레인 상태를 'W'로 
	    		 * 상차스케쥴코드를 '' 로 셋팅한다.
	    		 */
	    		int iReq  = dao.updateSubCraneEquipStat(sYard_Id,
	    												sBay_Gp,
	    												sEquip_Kind,
	    												sEquip_No,
	    												YmCommonConst.WORK_PROG_STAT_W,
	    												"");
	    	}
	    	
	    	{
	    		/**
	    		 * 작업지시를 요구한다.
	    		 */
		    	EJBConnector ejbConn = new EJBConnector("default","JNDICWrkOrdReg",this);
				isSuccess = (Boolean)ejbConn.trx("callCraneSchInfo",new  Class[]{String.class,
																				 String.class,
																				 String.class,
																				 String.class,
																				 String.class,
																				 String.class,
																				 String.class},
																	  new Object[]{sTc,
																	  			   sYard_Id,
																	  			   sBay_Gp,
																	  			   sEquip_Kind,
																	  			   sEquip_No,
																	  			   sSch_Code,
		    																	   sWbookId});
			}
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue();
	}
	
      /**
	 * 오퍼레이션명 : 
	 *
	 *	8.	SLAB INFO
        *		SCARFING 작업지시 Call
        *		권하시 SCARFING 작업지시 전문을 Call
        * 
        * param JDTORecord 	: 전문항목
        *
        * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */       
	public boolean callScarfingInfo_Slab(JDTORecord jtR){
		
		Boolean isSuccess = new Boolean(false);
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String sSch_Code 	 = StringHelper.evl(jtR.getFieldString("Sch_Code"), "");
			String sStockId 	 = StringHelper.evl(jtR.getFieldString("Slab_No"), "").trim();	
			String sWork_Id 	 = YmCommonUtil.getCurDataWithLegacy(StringHelper.evl(jtR.getFieldString("Work_Id"), ""));
			/**
			 * SLAB 인 경우 SCARFING 보급 스케쥴만 TRUE
			 * 2매 작업 없습
			 */
			if(YmCommonConst.MAIN_WORK_M.equals(sWork_Id)&&
			   YmCommonUtil.isLineInWork(sSch_Code)){
				
				logger.println(LogLevel.DEBUG,this, "====SCARFING 작업지시 CALL====");
				
				EJBConnector ejbConn = new EJBConnector("default","JNDISSSpplyWrkOrdReg",this);
				isSuccess = (Boolean)ejbConn.trx("callScarfingMsgInfo",new Class[]{String.class},
																  	   new Object[]{sStockId});
							
			}
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue();
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */       
	public void callWBSch_Slab(){
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return ;
			}
			EJBConnector ejbConn = new EJBConnector("default","JNDISRTSpplyWrkOrdReg",this);
			Boolean isTrue = (Boolean)ejbConn.trx("callCraneCtc4Info",new  Class[]{String.class},
															new Object[]{"STE3 고장에 따른 크레인 작업지시 호출"});
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}
	
	/**
	 *  SLAB 권하시에 장입보급 순서를 복구한다.
	 *
	 * @param sSchId 		: SCH_ID
	 * @param sStockId 		: 저장품ID
	 * @param sSch_Code 	: 스케쥴 코드
	 * @param sWork_Id 		: 주작업/보조작업 구분
	 *
	 * @return int
	 * @throws 
	 */	 
	private int setStockLotNo_Slab(	String sSchId,
								String sStockId,
								String sSch_Code,
								String sWork_Id,
								String sL2StackColGp){
		int iReq = 0; 
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			if(YmCommonConst.MAIN_WORK_M.equals(sWork_Id)){
				
				if(YmCommonConst.NEW_SCH_WORK_KIND_SWLI.equals(sSch_Code)||	// Slab W/B 보급
				   YmCommonConst.NEW_SCH_WORK_KIND_SCLI.equals(sSch_Code)){	// Slab CTC 보급	 
					
					String sPutUsageCd  = YmCommonUtil.getStackColInfoWithPk(sL2StackColGp);
			   		
			   		//SLAB W/B, CTC 일 경우만.
			   		if(!YmCommonConst.STACK_COL_USAGE_CD_WB.equals(sPutUsageCd)	&&// W/B	
			   		   !YmCommonConst.STACK_COL_USAGE_CD_CT.equals(sPutUsageCd)	){ // CTC
			   			
			   			logger.println(LogLevel.DEBUG,this, "=====권상 장입보급 순서 복구 시작=====");
						int Seq = dao.updateStockSaddleInfo_02(sStockId);
						logger.println(LogLevel.DEBUG,this, "=====권상 장입보급 순서 초기화 종료=====");
					}									   
				}
			}
		    	   	
		}catch(Exception e){}
		return iReq;
	}
	/**
	 *  SLAB 권하시에 장입동(C동)에 장입재 권하시 생상통제에 장입진행실적 송신.
	 *
	 * @param sStockId 		: 저장품ID
	 * @param sSch_Code 	: 스케쥴 코드
	 * @param sWork_Id 		: 주작업/보조작업 구분
	 *
	 * @return int
	 * @throws 
	 */	 
	private int sendPcStatus_Slab(	String sStockId,
									String sSch_Code,
									String sWork_Id){
		int iReq = 0; 
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			if(YmCommonConst.MAIN_WORK_M.equals(sWork_Id)){
				
				if(YmCommonConst.NEW_SCH_WORK_KIND_STMU.equals(sSch_Code)||	// Slab 대차하차(1)
				   YmCommonConst.NEW_SCH_WORK_KIND_STM4.equals(sSch_Code)){	// Slab 대차하차(2)
					
					JDTORecord stockJR = dao.getStockInfo(sStockId);
					
					if(stockJR != null){
						
						String sCurLotNo = StringHelper.evl(stockJR.getFieldString("CHARGE_LOT_NO"),"");
						
						if(!"".equals(sCurLotNo)){
							//관제 전송 CALL
							boolean isTrue = callGwanJeInfo_Slab(YmCommonConst.TC_YMPC010,
																 sStockId);
						}
					}									   
				}
			}
		    	   	
		}catch(Exception e){}
		return iReq;
	}
       /**
	 * 오퍼레이션명 :
	 *
	 *	8.	A열연 SLAB야드 추가(2007-02-26(MCH))
	 *		SLAB INFO 권하시
     	 *		야드맵 정보정신 Call 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */       
	public boolean callMapInfo_ASlab(JDTORecord jtR){
		Boolean isSuccess = new Boolean(true);
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
				String sGlocation	= StringHelper.evl(jtR.getFieldString("GROBAL_LOCATION"), "");
				if(!"".equals(sGlocation)){
					String sMsg 		 = YmCommonUtil.setASlabMapMsgInfo(sGlocation);	
					
					logger.println(LogLevel.DEBUG,this, "====A열연 야드맵 작업송신 CALL====");
					EJBConnector ejbConn = new EJBConnector("default","JNDICraneStatusReg",this);
					isSuccess = (Boolean)ejbConn.trx("bsyYdMapInfo",new  Class[]{String.class}, new Object[]{sMsg});
				}											
															
				String sPut_Position = StringHelper.evl(jtR.getFieldString("Put_Position"), "");
				String sMsg 		 = YmCommonUtil.setASlabMapMsgInfo(sPut_Position);	
				
				logger.println(LogLevel.DEBUG,this, "====A열연 야드맵 작업송신 CALL====");
				EJBConnector ejbConn = new EJBConnector("default","JNDICraneStatusReg",this);
				isSuccess = (Boolean)ejbConn.trx("bsyYdMapInfo",new  Class[]{String.class}, new Object[]{sMsg});																
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue();
	}

       /**
	 * 오퍼레이션명 :
	 *
	 *	8.	A열연 SLAB야드 추가(2007-02-26(MCH))
	 *		SLAB INFO 권하시
     	 *		야드맵 정보정신 Call 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */       
	public boolean callMapInfo_ASlab7(JDTORecord jtR){
		Boolean isSuccess = new Boolean(true);
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
				String sGlocation	= StringHelper.evl(jtR.getFieldString("GROBAL_LOCATION"), "");
				if(!"".equals(sGlocation)){
					String sMsg 		 = YmCommonUtil.setASlabMapMsgInfo7(sGlocation);	
					
					logger.println(LogLevel.DEBUG,this, "====A열연 연주 7호기 야드맵 작업송신 CALL====");
					EJBConnector ejbConn = new EJBConnector("default","JNDICraneStatusReg",this);
					isSuccess = (Boolean)ejbConn.trx("bsyYdMapInfo",new  Class[]{String.class}, new Object[]{sMsg});
				}											
															
				String sPut_Position = StringHelper.evl(jtR.getFieldString("Put_Position"), "");
				String sMsg 		 = YmCommonUtil.setASlabMapMsgInfo7(sPut_Position);	
				
				logger.println(LogLevel.DEBUG,this, "====A열연 연주 7호기 작업송신 CALL====");
				EJBConnector ejbConn = new EJBConnector("default","JNDICraneStatusReg",this);
				isSuccess = (Boolean)ejbConn.trx("bsyYdMapInfo",new  Class[]{String.class}, new Object[]{sMsg});																
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue();
	}
	
	
	/**
	 * 오퍼레이션명 :
	 *
	 *	8.	SLAB INFO
        *		야드맵 정보정신 Call
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */       
	public boolean callMapInfo_Slab(JDTORecord jtR){
		
		Boolean isSuccess = new Boolean(false);
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			{
				String sGlocation	= StringHelper.evl(jtR.getFieldString("GROBAL_LOCATION"), "");
				if(!"".equals(sGlocation)){
					String sMsg 		 = YmCommonUtil.setBSlabMapMsgInfo(sGlocation);	
					
					logger.println(LogLevel.DEBUG,this, "====야드맵 작업송신 CALL====GROBAL_LOCATION:"+sMsg);
					
					EJBConnector ejbConn = new EJBConnector("default","JNDICraneStatusReg",this);
					isSuccess = (Boolean)ejbConn.trx("bsyYdMapInfo",new  Class[]{String.class},
																new Object[]{sMsg});
				}											
			}
			{												
				String sPut_Position = StringHelper.evl(jtR.getFieldString("Put_Position"), "");
				
				String sMsg 		 = YmCommonUtil.setBSlabMapMsgInfo(sPut_Position);	
				
				logger.println(LogLevel.DEBUG,this, "====야드맵 작업송신 CALL====Put_Position:"+sMsg);
				
				EJBConnector ejbConn = new EJBConnector("default","JNDICraneStatusReg",this);
				isSuccess = (Boolean)ejbConn.trx("bsyYdMapInfo",new  Class[]{String.class},
																new Object[]{sMsg});
			}
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue();
	}
	
       /**
	 * 오퍼레이션명 :
	 *
	 *	8.	SLAB INFO
        *		야드맵 정보정신 Call
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */       
	public boolean callMapInfo_SlabNEW(JDTORecord jtR){
		
		Boolean isSuccess = new Boolean(false);
		StringBuffer sendMsg = new StringBuffer();
		List maps = null;
		JDTORecord dto = null;
		ymCommonDAO ymCommonDAO = new ymCommonDAO();
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
//			{
//				String sGlocation	= StringHelper.evl(jtR.getFieldString("GROBAL_LOCATION"), "");
//				if(!"".equals(sGlocation)){
//					String sMsg 		 = YmCommonUtil.setBSlabMapMsgInfo(sGlocation);	
//					
//					logger.println(LogLevel.DEBUG,this, "====야드맵 작업송신 CALL====");
//					
//					EJBConnector ejbConn = new EJBConnector("default","JNDICraneStatusReg",this);
//					isSuccess = (Boolean)ejbConn.trx("bsyYdMapInfo",new  Class[]{String.class},
//																new Object[]{sMsg});
//				}											
//			}
			{												
				String sPut_Position = StringHelper.evl(jtR.getFieldString("Put_Position"), "");
				String sStockId		 = StringHelper.evl(jtR.getFieldString("Slab_No"), "").trim();
				
				String sMsg 		 = YmCommonUtil.setBSlabMapMsgInfo(sPut_Position);	
				
				logger.println(LogLevel.DEBUG,this, "====야드맵 작업송신 CALL====");
			  
				maps = ymCommonDAO.readBYDMapInfoNEW(sPut_Position ,sStockId);
				int mapsCnt = maps != null ? maps.size() : 0;
				Map tc = ymCommonDAO.readColumnLenOfTc(YmCommonConst.TC_CM1BP05);	     
				
				sendMsg.append(YmCommonConst.TC_CM1BP05);
		        sendMsg.append(YmCommonUtil.getStringYMD("-"));
		        sendMsg.append(YmCommonUtil.getStringHMS("-"));
		        sendMsg.append(YmCommonConst.FORM_I);
		        appendMsgNum(sendMsg, ""+ (YmCommonUtil.getTotalLenOfTc(tc) - 30), getFieldLen(tc, "전문길이"));
		        sendMsg.append(YmCommonConst.SEND_REQ_A);
		        
				if(mapsCnt > 0) {
					

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
		        	//B열연 SLAB야드 연주 7호기에서 
			      
		            for(int i = 0 ; i < mapsCnt; i++) {
		                dto = (JDTORecord)maps.get(i); 
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
				        sendQueueBSlab(YmCommonConst.TC_CM1BP05, sendMsg.toString());
				        sendMsg.delete(31, sendMsg.length());
		            }
				}
				
			}
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue();
	}
	
	/**
     * 송신 EJB를 이용하여 송신데이터를 송신한다.
     * @param methodName	TC명
     * @param sendMsg		송신데이터
     * @throws Exception
     */
    private void sendQueueBSlab(String methodName, String sendMsg) {
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
	 *	작업실적에 등록할 DATA SETTING
     *	JDTORecord Type 의 값을 List Type 으로 변환한다.
     *
     * @param  JDTORecord	: 전문항목
     * @param  String		: 스케쥴 ID
     * @param  String		: 실적처리방법(N:Crane정상작업, V 화면, S:산적위치수정)
     *
     * @return List
     * @throws 
     */			
	private List getPutRtData(JDTORecord rtRc,
							  String sSchId,
							  String sFuncGbn)
	{	 
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		List param  = new ArrayList(); 
		
		String scrane_wrslt_put_x_axis	= ""; //크레인작업결과put X축
		String scrane_wrslt_put_y_axis	= ""; //크레인작업결과put Y축
		String scrane_wrslt_put_z_axis	= ""; //크레인작업결과put Z축
		String scrane_wrslt_put_loc		= ""; //크레인작업결과put 위치     
		String scrane_wrslt_put_func	= ""; //크레인작업결과put 기능
		String scrane_wrslt_put_ddtt	= ""; //크레인작업결과put 일시
		String smodifier				= ""; //register,
				 
		scrane_wrslt_put_x_axis	= StringHelper.evl(rtRc.getFieldString("Put_X_Position"), "");
		scrane_wrslt_put_y_axis	= StringHelper.evl(rtRc.getFieldString("Put_Y_Position"), "");
		scrane_wrslt_put_z_axis	= "0"; 
		scrane_wrslt_put_loc	= StringHelper.evl(rtRc.getFieldString("Put_Position"), "");
		scrane_wrslt_put_func	= getWorkWrsltFunc(rtRc,sFuncGbn); 
		scrane_wrslt_put_ddtt   	= YmCommonUtil.getCurDate("yyyyMMddHHmmss");
		smodifier				= StringHelper.evl(rtRc.getFieldString("USER_ID"), "SYSTEM"); 
		 
		param.add(scrane_wrslt_put_x_axis);
		param.add(scrane_wrslt_put_y_axis);
		param.add(scrane_wrslt_put_z_axis);   
		param.add(scrane_wrslt_put_loc);      
		param.add(scrane_wrslt_put_func);	
		param.add(scrane_wrslt_put_ddtt);  
		param.add(smodifier);    			
		param.add(sSchId);    				
		param.add(sSchId);    				
			
		return param;
	}
	
	private String getWorkWrsltFunc(JDTORecord jtR, String sFuncGbn)
	{
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		//차상국작업이고 주작업구분이 'M1,M2' 등 Manual 작업일경우에만 'M' 변경
		if(YmCommonConst.CRANE_FUNC_N.equals(sFuncGbn)){
			
			if(StringHelper.evl(jtR.getFieldString("Work_Id"), "").startsWith(YmCommonConst.CRANE_FUNC_M)){
				sFuncGbn = YmCommonConst.CRANE_FUNC_M;
			}
		}
		return sFuncGbn;
	}
	/**
     *  CRANE에 작업결과를 송신할 전문MESSAGE를 구성한다.
     *
     *  야드L3	Target	야드L2	I/F방법	JMS	I/F주기	REQ	I/F유형	OnLine
 	 *	T/C	THHC200		
	 *	1	전문코드			TC		CHAR	07  전문코드        
	 *  2	CRANE번호			CNO		CHAR	04  CRANE번호       
	 *	3	SPARE				SPARE	CHAR	04  SPARE           
	 *	4	발생일				DATE	CHAR	06	발생일          	YYYYMMDD
	 *	5	발생시				TIME	CHAR	06	발생시          	HHMMSS
	 *	6	권상권하실적구분	GBN		CHAR	07	권상권하실적구분	권상:THCH550, 권하:THCH570
	 *	7	이상유무			YN		CHAR	01	이상유무        	0:정상, 1:이상
	 *	8	SPARE				SPARE	CHAR	165 SPARE           
	 *
	 * @param schInfo : CTS 작업지시 INFO
     *
     * @return
     * @throws 
     */	 
	private String setAWorkFinishMsgInfo(String sTc,
										 String sLegacyCraneNo,	
										 String sGbn){
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		StringBuffer sMsg = new StringBuffer();

		String TC			= ""; 
		String CNO			= ""; 
		String SPARE1		= ""; 
		String sDATE		= ""; 
		String sTIME		= ""; 
		String GBN			= ""; 
		String YN			= ""; 
		String SPARE2		= ""; 
				
		int iTC				=  7;
		int iCNO			=  4;
		int iSPARE1			=  4;
		int iDATE			=  6;
		int iTIME			=  6;
		int iGBN			=  7;
		int iYN				=  1;
		int iSPARE2			=165;
						   //200	
								   
		try{
			
			//VALUE SETTING
			TC			= sTc; 
			CNO			= sLegacyCraneNo; 
			SPARE1		= ""; 
			sDATE		= YmCommonUtil.getCurDate("yyMMdd");
			sTIME		= YmCommonUtil.getCurDate("HHmmss");
			GBN			= YmCommonConst.TC_THCH570; 
			YN			= sGbn; 
			SPARE2		= ""; 
			
			sMsg.append(YmCommonUtil.FillToString(TC		,iTC));
			sMsg.append(YmCommonUtil.FillToString(CNO		,iCNO));
			sMsg.append(YmCommonUtil.FillToString(SPARE1	,iSPARE1));
			sMsg.append(YmCommonUtil.FillToString(sDATE		,iDATE));
			sMsg.append(YmCommonUtil.FillToString(sTIME		,iTIME));
			sMsg.append(YmCommonUtil.FillToString(GBN		,iGBN));
			sMsg.append(YmCommonUtil.FillToString(YN		,iYN));
			sMsg.append(YmCommonUtil.FillToString(SPARE2	,iSPARE2));
			
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return sMsg.toString();
	}
	
	/**
     *  코일 권하시에 저장품 이동조건을 셋팅한다.
     *
     * @param sStockId 		: 저장품ID
     * @param sSch_Code 	: 스케쥴 코드
     * @param sPut_Position : 권하위치
     *
     * 권상/권하위치가 차량일 경우에  
	 * 저장품 이동조건을 셋팅한다.
	 *
	 * @return int
     * @throws 
     */	 
	private int setStockMoveTerm_Coil(String sStockId,
									  String sSch_Code,	
									  String sPut_Position){
		
		logger.println(LogLevel.DEBUG,this, "=====권하 저장품 이동조건 셋팅 시작=====");
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return 0;
		}
		
		String sStackColGp   		= (sPut_Position.length() >=  6)? sPut_Position.substring(0, 6):"";
		
		String[] sStockInfo = YmCommonUtil.getCoilCurrProgCd(sStockId,"");
	    String sProgCd   	= sStockInfo[0];
		String sStocMv   	= sStockInfo[1];
		logger.println(LogLevel.DEBUG,this, "===sProgCd = " + sProgCd);	
		logger.println(LogLevel.DEBUG,this, "===sStocMv = " + sStocMv);	
		 
		String sUsageCd = YmCommonUtil.getStackColInfoWithPk(sStackColGp);
		
		if(YmCommonConst.STACK_COL_USAGE_CD_FS.equals(sUsageCd)){ // CTS FROM SADDLE
			
			sStocMv = YmCommonConst.NEW_STOCK_MOVE_TERM_CL;// CTS상차완료
			logger.println(LogLevel.DEBUG,this, "CTS에서는 이동조건 변경 작업을 안함.");
			return 0;
			
		}else if(YmCommonConst.STACK_COL_USAGE_CD_CX.equals(sUsageCd)){ // 대차정지위치
			
			sStocMv = YmCommonConst.NEW_STOCK_MOVE_TERM_TL;// 대차상차완료
			
			//HYSCO 대차상차시 입고실적 처리를 위해...
			/*
			{
				JDTORecord stockV	= dao.getCoilCommonInfo(sStockId);
	    		
	    		if(stockV != null){				
					// 코일공통 Hysco 이송수단이 'C'
					if(YmCommonConst.HYSCO_TRANS_GP_C.equals(
							StringHelper.evl(stockV.getFieldString("HYSCO이송수단"), ""))){
						sStocMv = YmCommonConst.NEW_STOCK_MOVE_TERM_L1;// 대차출하완료
					}
				}
			}
			*/
		}else if(YmCommonConst.STACK_COL_USAGE_CD_TX.equals(sUsageCd)){ // 차량정지위치
			
			sStocMv = YmCommonConst.NEW_STOCK_MOVE_TERM_VL;// 차량상차완료
		
		}else if(YmCommonConst.STACK_COL_USAGE_CD_FE.equals(sUsageCd)|| // COIL HFL보급위치
			     YmCommonConst.STACK_COL_USAGE_CD_FI.equals(sUsageCd)|| // COIL HFLTAKEIN위치
			     YmCommonConst.STACK_COL_USAGE_CD_KE.equals(sUsageCd)|| // COIL SPM보급위치
			     YmCommonConst.STACK_COL_USAGE_CD_QE.equals(sUsageCd)|| // COIL EQL보급위치
			     YmCommonConst.STACK_COL_USAGE_CD_KE.equals(sUsageCd)){ // COIL SPMTAKEIN위치
			
			sStocMv = YmCommonConst.NEW_STOCK_MOVE_TERM_C1;// 보급완료
		
		}else if(YmCommonConst.STACK_COL_USAGE_CD_C1.equals(sUsageCd)|| 
				 YmCommonConst.STACK_COL_USAGE_CD_C2.equals(sUsageCd)|| 
				 YmCommonConst.STACK_COL_USAGE_CD_C3.equals(sUsageCd)|| 
				 YmCommonConst.STACK_COL_USAGE_CD_C4.equals(sUsageCd)|| 
				 YmCommonConst.STACK_COL_USAGE_CD_C5.equals(sUsageCd)|| 
				 YmCommonConst.STACK_COL_USAGE_CD_C6.equals(sUsageCd)|| 
				 YmCommonConst.STACK_COL_USAGE_CD_C7.equals(sUsageCd)|| 
				 YmCommonConst.STACK_COL_USAGE_CD_C8.equals(sUsageCd)|| 
				 YmCommonConst.STACK_COL_USAGE_CD_G1.equals(sUsageCd)|| // COIL 제품출하대기장
				 YmCommonConst.STACK_COL_USAGE_CD_G2.equals(sUsageCd)|| // COIL 제품이송상차장
				 YmCommonConst.STACK_COL_USAGE_CD_G3.equals(sUsageCd)|| // COIL 제품이송하차장
				 YmCommonConst.STACK_COL_USAGE_CD_G4.equals(sUsageCd)|| // COIL 제품이적중계장
				 YmCommonConst.STACK_COL_USAGE_CD_G5.equals(sUsageCd)){ // COIL 제품보관적치장
			
			if(YmCommonConst.CURR_PROG_CD_COIL_H.equals(sProgCd)){ //입고대기
				
				/*
				//HYSCO 대차상차시 입고실적 처리를 위해...
				JDTORecord stockV	= dao.getCoilCommonInfo(sStockId);
	    		
	    		if(stockV != null){				
					// 코일공통 Hysco 이송수단이 'C'
					if(YmCommonConst.HYSCO_TRANS_GP_C.equals(
							StringHelper.evl(stockV.getFieldString("HYSCO이송수단"), ""))){
						sStocMv = YmCommonConst.NEW_STOCK_MOVE_TERM_L2;// 대차출하대기
					}else{
						sStocMv = YmCommonConst.NEW_STOCK_MOVE_TERM_H1;// 입고완료
					}
				}
				*/
				sStocMv = YmCommonConst.NEW_STOCK_MOVE_TERM_H1;// 입고완료
			}
			
		}else if(YmCommonConst.STACK_COL_USAGE_CD_C1.equals(sUsageCd)||	// 냉각장
				 YmCommonConst.STACK_COL_USAGE_CD_C6.equals(sUsageCd)||	// 정정보급대기장
				 YmCommonConst.STACK_COL_USAGE_CD_C8.equals(sUsageCd)){	// 보류장
			if(YmCommonConst.CURR_PROG_CD_COIL_J.equals(sProgCd)){ //반납대기
				if(YmCommonConst.NEW_STOCK_MOVE_TERM_JR.equals(sStocMv)){ //실물반납 	
					sStocMv = YmCommonConst.NEW_STOCK_MOVE_TERM_J1;// 반납완료
				}	
			}
		}else if(YmCommonConst.STACK_COL_USAGE_CD_CW.equals(sUsageCd)){	// 수냉탱크
				sStocMv = YmCommonConst.NEW_STOCK_MOVE_TERM_A5;// 수냉재보급완료
		}
	
		
		if(YmCommonConst.NEW_SCH_WORK_KIND_CVMU.equals(sSch_Code)|| // COIL 소재이송하차
		   YmCommonConst.NEW_SCH_WORK_KIND_CVM4.equals(sSch_Code)|| // COIL 소재이송하차	 
		   YmCommonConst.NEW_SCH_WORK_KIND_CVM5.equals(sSch_Code)|| // COIL 소재이송하차	 
		   YmCommonConst.NEW_SCH_WORK_KIND_GVMU.equals(sSch_Code)|| // COIL 제품이송하차
		   YmCommonConst.NEW_SCH_WORK_KIND_GVM4.equals(sSch_Code)|| // COIL 제품이송하차	 	
		   YmCommonConst.NEW_SCH_WORK_KIND_GVM5.equals(sSch_Code)){ // COIL 제품이송하차				
		   	
		   	sStocMv = YmCommonConst.NEW_STOCK_MOVE_TERM_E1;// 이송완료
		}
		
		int iReq = 0; 
		if(!"".equals(sStocMv)){ 
			iReq = dao.updateStockTransInfo(sStockId,
											sStocMv); 
			logger.println(LogLevel.DEBUG,this, "===STOCK_MOVE_TERM = " + sStocMv);								
		}
		logger.println(LogLevel.DEBUG,this, "=====권하 저장품 이동조건 셋팅 종료=====");
		return iReq;
	}
	
	/**
     *  SLAB 권하시에 저장품 이동조건을 셋팅한다.
     *
     * @param sStockId 		: 저장품ID
     * @param sSch_Code 	: 스케쥴 코드
     * @param sPut_Position : 권하위치
     *
     * 권상/권하위치가 차량일 경우에  
	 * 저장품 이동조건을 셋팅한다.
	 *
	 * @return int
     * @throws 
     */	 
	private int setStockMoveTerm_Slab(String sStockId,
									  String sSch_Code,
									  String sPut_Position){
		int iReq = 0; 
		
		logger.println(LogLevel.DEBUG,this, "=====권하 저장품 이동조건 셋팅 시작=====");
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return 0;
		}
		
		String sStackColGp   		= (sPut_Position.length() >=  6)? sPut_Position.substring(0, 6):"";
		
		String[] sStockInfo = YmCommonUtil.getSlabCurrProgCd(sStockId,"");
	    String sProgCd   	= sStockInfo[0];
		String sStocMv   	= sStockInfo[1];
		logger.println(LogLevel.DEBUG,this, "===sProgCd = " + sProgCd);	
		logger.println(LogLevel.DEBUG,this, "===sStocMv = " + sStocMv);	
		
		String sUsageCd = YmCommonUtil.getStackColInfoWithPk(sStackColGp);
		
		if(YmCommonConst.STACK_COL_USAGE_CD_CX.equals(sUsageCd)){ // 대차정지위치
			
			sStocMv = YmCommonConst.NEW_STOCK_MOVE_TERM_TL;// 대차상차완료
		
		}else if(YmCommonConst.STACK_COL_USAGE_CD_TX.equals(sUsageCd)){ // 차량정지위치
			if(sStackColGp.startsWith("0")){
				//B-Cast 제외한다[To위치결정에서 저장품이동조건으로 체크하는 부분때문.]
				sStocMv = "";
			}else{
				sStocMv = YmCommonConst.NEW_STOCK_MOVE_TERM_VL;// 차량상차완료
			}
		}else if(YmCommonConst.STACK_COL_USAGE_CD_PX.equals(sUsageCd)){ // 팔레크정지위치
			if(sStackColGp.startsWith("0")){
				//B-Cast 제외한다[To위치결정에서 저장품이동조건으로 체크하는 부분때문.]
				sStocMv = "";
			}else{
				sStocMv = YmCommonConst.NEW_STOCK_MOVE_TERM_VL;// 차량상차완료
			}
		}else if(YmCommonConst.STACK_COL_USAGE_CD_SE.equals(sUsageCd)){	// SCARFING 입측위치
			
			sStocMv = YmCommonConst.NEW_STOCK_MOVE_TERM_D1;// SCARFING 보급완료
			
		}else if(YmCommonConst.STACK_COL_USAGE_CD_WB.equals(sUsageCd)){	// W/B 보급위치
			
			sStocMv = YmCommonConst.NEW_STOCK_MOVE_TERM_F1;// W/B 보급완료
			
			//관제 전송 CALL
			boolean isTrue = callGwanJeInfo_Slab(YmCommonConst.TC_YMPC030,
												 sStockId);
			
			//야드 L2 전송 장입순번 Clear CALL
			isTrue = callL2LotEndInfo_Slab(sStockId);	
											   
		}else if(YmCommonConst.STACK_COL_USAGE_CD_CT.equals(sUsageCd)){	// CTC 보급위치
			
			sStocMv = YmCommonConst.NEW_STOCK_MOVE_TERM_F2;// CTC 보급완료
			
			//관제 전송 CALL
			boolean isTrue = callGwanJeInfo_Slab(YmCommonConst.TC_YMPC031,
												 sStockId);
			//야드 L2 전송 장입순번 Clear CALL
			isTrue = callL2LotEndInfo_Slab(sStockId);	
		}
			
		if(YmCommonConst.NEW_SCH_WORK_KIND_SVMU.equals(sSch_Code)){ // SLAB 이송하차	
			sStocMv = YmCommonConst.NEW_STOCK_MOVE_TERM_C1;// 이송완료
		}		   		
		
		if(!"".equals(sStocMv)){ 
			iReq = dao.updateStockTransInfo2(sStockId,
											sStocMv,
											sUsageCd); 
			logger.println(LogLevel.DEBUG,this, "===STOCK_MOVE_TERM = " + sStocMv);								
		}
		logger.println(LogLevel.DEBUG,this, "=====권하 저장품 이동조건 셋팅 종료=====");
		return iReq;
	}
	
	/**
	 *	10.	SLAB INFO
     *		관제 정보 송신 Call
	 *
	 *		YMPC140	CTC Loading대기 (W/B 적치완료)	 : 신규코드[30]
	 *		YMPC150	CTC Loading대기 (CTC 적치완료)	 : 신규코드[31]
     *
     * @param dao 			: DAO
     * @param jDTORecord 	: 전문항목
     *
     * @return
     * @throws 
     */	
	private boolean callGwanJeInfo_Slab(String sTc,
									    String sStockId){
		
		Boolean isSuccess = new Boolean(false);
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			/*
	    	 * 	2009.07 YJK 생산통제 장입진행정보 기존모듈 삭제.
	    	 * 
				ZZPC001 model = new ZZPC001();
				model.setTcCode(YmCommonConst.MODEL_YMPC100);
				model.setTcDate(YmCommonUtil.getCurDate("yyyy-MM-dd"));
				model.setTcTime(YmCommonUtil.getCurDate("HH-mm-ss"));
				model.setrealStlNo(sStockId);
				model.seteventStat(sTc);
				
				EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
				isSuccess = (Boolean)ejbConn.trx("sendInternalModel", new Class[]{CommonModel.class},
															  	  	 new Object[]{model});
			 *
			 */	
			
			/*
			 * 2009.07 YJK 일관제철 생산통제 장입진행실적 추가모듈.
			 */
			JDTORecord model = null;
			model = JDTORecordFactory.getInstance().create(); 
			model.setField("JMS_TC_CD", 		"YDCTJ032");
			model.setField("JMS_TC_CREATE_DDTT",new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));	
			model.setField("PTOP_PLNT_GP", 		"HB");
			model.setField("STL_APPEAR_GP", 	"C");
			model.setField("CHG_SUP_PROG_STAT", sTc);
			model.setField("WR_OCCR_DT", 		new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
			model.setField("YD_EQP_WR_CNT", 	"1");
			model.setField("STL_NO1", 			sStockId);

			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
			isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new  Class[]{JDTORecord.class},
			  	  	 											 new Object[]{model});
				
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue();
	}
	
	/**
	 *	10_1.	SLAB INFO
	 *		야드 L2로 장입완료시 삭제 Slab 정보 송신
	 *
	 *		YMPC140	CTC Loading대기 (W/B 적치완료)	
	 *		YMPC150	CTC Loading대기 (CTC 적치완료)	
	 *
	 * @param dao 			: DAO
	 * @param jDTORecord 	: 전문항목
	 *
	 * @return
	 * @throws 
	 */	
	private boolean callL2LotEndInfo_Slab(String sStockId){
		
		Boolean isSuccess = new Boolean(false);
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			ymCommonDAO ymDao 	= ymCommonDAO.getInstance();
			JDTORecord slabInfo = ymDao.readZoneInStocks_Lot(sStockId);
			String sSendMsg 	= YmCommonUtil.getSlabMsgInfo(slabInfo,YmCommonConst.FORM_R);
			   
		    EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
			isSuccess = (Boolean)ejbConn.trx("sendCM1BP02", new Class[]{String.class},
													     	new Object[]{sSendMsg});
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue();
	}
	
	private String getField(JDTORecord data, String name) {
	    return StringHelper.evl(data.getFieldString(name), "").trim();
	}
	private int getFieldLen(Map data, String name) {
	    return StringHelper.parseInt((String)data.get(name), 0);
	}
	private void fillZeroSpace(StringBuffer buffer, int cnt) {
		for(int i = 0; i < cnt; i++) {
		    buffer.append("0");
		}
	}
	private void fillSpace(StringBuffer buffer, int cnt) {
		for(int i = 0; i < cnt; i++) {
		    buffer.append(" ");
		}
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
	 * 오퍼레이션명 : 권하시 TO위치정보가 없을 경우 생성한다.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */       
	public JDTORecord createToLoc_01(String sSchId,
							   	     String sTc){
		
		boolean isSuccess = false;
		
		JDTORecord ToLocV 	 = null;
		String sSchCode 	 = "";
		String sToStockId	 = "";
		String sCurStockId	 = "";
		String sLayerStat	 = "";
		String sPut_Position = "";
		String sStackColGp   = "";
	   	String sStackBedGp   = "";
	   	String sStackLayerGp = "";
	   	
	   	int iSeq = -1;
	   	
		try{
			logger.println(LogLevel.DEBUG,this, "=====권하 처리시 TO위치 FAIL LOGIC 시작=====");
			
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			JDTORecord schInfo = dao.getSchInfoWithSchId(sSchId);
			
			if(schInfo != null){
				
				sSchCode 		= StringHelper.evl(schInfo.getFieldString("SCH_WORK_KIND"), ""); 		// SCH CODE
				sToStockId 		= StringHelper.evl(schInfo.getFieldString("STOCK_ID"), ""); 			// SCH CODE
				sPut_Position 	= StringHelper.evl(schInfo.getFieldString("CRANE_WORD_PUT_LOC"), ""); 	// SCH TO LOC
			}
			
			sStackColGp   = (sPut_Position.length() >=  6)? sPut_Position.substring(0, 6):"";
		   	sStackBedGp   = (sPut_Position.length() >=  8)? sPut_Position.substring(6, 8):"";
		   	sStackLayerGp = (sPut_Position.length() >= 10)? sPut_Position.substring(8,10):"";
			
			String tWork_Id = YmCommonUtil.getCurDataWithLegacy(
							  StringHelper.evl(schInfo.getFieldString("SCH_WORK_AID_YN"), ""));
				
			if(YmCommonUtil.isLineInWork(sSchCode)&&
			   YmCommonConst.MAIN_WORK_M.equals(tWork_Id)){
	    			
    			if(!"".equals(sPut_Position)){
					iSeq = YmCommonDB.shiftConveyorInfo(sStackColGp,
				   			 		  					YmCommonConst.GBN_MIN);
				   	
				   	if(iSeq < 1){
						throw new EJBServiceException("=TO_FAIL=>CONVEYOR CREATE FAIL="+iSeq);
					}
				}
			}else{
			
				JDTORecord layerInfo = dao.getStackLayerInfoWithPk(sStackColGp,
																   sStackBedGp,
																   sStackLayerGp);
				if(layerInfo != null){
				
					sCurStockId = StringHelper.evl(layerInfo.getFieldString("STOCK_ID"), "").trim(); 		
					sLayerStat 	= StringHelper.evl(layerInfo.getFieldString("STACK_LAYER_STAT"), "");
				}
				
				{
					/**
					 *	2008.07.02 YJK
					 *	CTC4 설비에 적치된 SLAB 정보때문에 권하처리 안되는 문제 해결
					 *	> STE고장시점/권하완료시점/WB보급요구시점에 CTC4 적치정보는 Clear하고 있음
					 *	> A 권상완료 후 W/B보급요구로 추가로 등록된 B 스케쥴의 권하실적처리시 A가 적치되어
					 *	  권하실적처리가 안되는 문제 발생
					 *	=> 따라서 임시로 적치된 정보를 P상태로 셋팅을 함.
					 */
					if(YmCommonConst.TC_CM1PB06.equals(sTc)||  //B열연 Slab 권하실적		
					   YmCommonConst.TC_CM1PB07.equals(sTc)){  //B열연 Slab 권하이상실적
						if("2CCT04".equals(sStackColGp)){
							sLayerStat 	=  "P";
						}
					}
				}
				
				if(!"".equals(sCurStockId) &&			//저장품이 존재하고
				   !sToStockId.equals(sCurStockId)&&    //저장품이 다르고
				   (									//적치중이면
				   YmCommonConst.STACK_LAYER_STAT_L.equals(sLayerStat)||
				   YmCommonConst.STACK_LAYER_STAT_S.equals(sLayerStat)||
				   YmCommonConst.STACK_LAYER_STAT_U.equals(sLayerStat)
				   )
				   ){
				   	
				}else{
				
					if(YmCommonConst.TC_THCH570.equals(sTc) || //A열연 Coil 권하실적
					   YmCommonConst.TC_THCH580.equals(sTc) || //A열연 Coil 권하이상실적
					   YmCommonConst.TC_CN1PB06.equals(sTc) || //B열연 Coil 권하실적
					   YmCommonConst.TC_CN1PB07.equals(sTc)	){ //B열연 Coil 권하이상실적
						
						/* 
						 * 적치단 PUT위치 셋팅
						 * tb_ym_stacklayer Table : stock_id = stock_id(저장품ID)
						 * tb_ym_stacklayer Table : stack_layer_stat	   = 'P'(PUT 스케쥴수행)
						 */	
				    	iSeq = dao.updateCraneStackLayerStat(sStackColGp,
				    										 sStackBedGp,
				    										 sStackLayerGp,
				    										 sToStockId,
				    										 YmCommonConst.STACK_LAYER_STAT_P);
				    	/*
				    	 * A.B열연 Coil 스케쥴 TO위치 예약 결정시
				    	 * 상단 좌,우 상태정보를 UPDATE
				    	 */	
				    	{
				    		if(YmCommonConst.STACK_LAYER_GP_01.equals(sStackLayerGp)){
					    		
					    		/*
						    	 * A.B열연 Coil 스케쥴 TO위치 예약 결정시
						    	 * 상단 왼쪽 상태정보를 UPDATE
						    	 * 상단 오른쪽 상태정보를 UPDATE
						    	 */	
				    			iSeq = YmCommonDB.setCoilUpperState_E(sStackColGp,
							    							 	   	  sStackBedGp,
							    							 	   	  sStackLayerGp);
				    		}
				    	}
					
					}else if(YmCommonConst.TC_CM1PB06.equals(sTc)||  //B열연 Slab 권하실적		
							 YmCommonConst.TC_CM1PB07.equals(sTc)||  //B열연 Slab 권하이상실적
							 YmCommonConst.TC_HM1PB06.equals(sTc)||	 //A_A열연 Slab 권하실적(MCH)
							 YmCommonConst.TC_HM1PB56.equals(sTc)||	 //A_B열연 Slab 권하실적(MCH)
							 YmCommonConst.TC_HM1PB07.equals(sTc)||	 //A_A열연 Slab 권하이상실적(MCH)							 
							 YmCommonConst.TC_HM1PB57.equals(sTc)	 //A_B열연 Slab 권하이상실적(MCH)							 
					){ 		
						
						/* 
						 * 적치단 Put위치를 적치상태로 변경
						 * tb_ym_stacklayer Table : stock_id = Coil No
						 * tb_ym_stacklayer Table : stack_layer_stat	   = 'L'(적치중)
						 */	
						iSeq = dao.updateCraneStackLayerStat(sStackColGp,
				    										 sStackBedGp,
				    										 sStackLayerGp,
				    										 sToStockId,
				    										 YmCommonConst.STACK_LAYER_STAT_P);
			    	
			    		/*
				    	 * B열연 Slab 
				    	 * 바로 위 상단 상태정보를 UPDATE
				    	 */
						iSeq = YmCommonDB.setSlabUpperState_E(sStackColGp,
							    						 	  sStackBedGp,
							    						 	  sStackLayerGp);
						
					}				
				}					  	   						   
			
			}		
		 	
		 	ToLocV = JDTORecordFactory.getInstance().create();
			
			ToLocV.setField("STACK_COL_GP",  sStackColGp);
			ToLocV.setField("STACK_BED_GP",  sStackBedGp);
			ToLocV.setField("STACK_LAYER_GP",sStackLayerGp);

			JDTORecord layerJr = dao.getStackLayerInfoWithPk(sStackColGp,
							    						 	 sStackBedGp,
							    						 	 sStackLayerGp);
		 	if(layerJr != null){
		 		ToLocV.setField("Put_X_Position", StringHelper.evl(layerJr .getFieldString("STACK_LAYER_X_AXIS"), ""));
				ToLocV.setField("Put_Y_Position", StringHelper.evl(layerJr .getFieldString("STACK_LAYER_Y_AXIS"), ""));
		 	}
		 	
		 	logger.println(LogLevel.DEBUG,this, "=====권하 처리시 TO위치 FAIL LOGIC 종료=====");
		 	
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return ToLocV;
	}
	
	/*
	 * 2007.07.09 이정훈
	 * B열연 Coil Message 전송
	 */
	
	private void sendMessageToBCoilCrane(String sEquipGp,
									 	String sMessage){
		try{							 	
			    	
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
			
	    		Boolean isTemp  = new Boolean(false);
	    		
	    		EJBConnector ejbConn = new EJBConnector("default","JNDICWrkOrdReg",this);
	    		
			isTemp  = (Boolean)ejbConn.trx("callBCoilCraneMsgInfo",new  Class[]{String.class,
																	     String.class},
											                           new Object[]{sEquipGp,
																  	      sMessage});
		}catch(Exception e){}
	}
	
	
    
	/**
	 * 관리되는 개소코드를 야드구분으로 변환하는 메소드
	 * @param szWLOC_CD
	 * @return
	 */
	public static String getYdFromWlocCd(String szWLOC_CD) {		
		
		String szYD_GP = "";
		if(szWLOC_CD.equals("DHY21") || szWLOC_CD.equals("DHY22")) {				//C연주슬라브
			szYD_GP = YdConstant.YD_GP_C_SLAB_YARD;
		}else if(szWLOC_CD.equals("DJY21") || szWLOC_CD.equals("DJY22")) {			//C열연소재
			szYD_GP = YdConstant.YD_GP_C_HR_COIL_MATL_YARD;
		}else if(szWLOC_CD.equals("DJY15") 
				|| szWLOC_CD.equals("DJY16") 
				|| szWLOC_CD.equals("DJY17") 
    	    	|| szWLOC_CD.equals("DJY18") 
    	    	|| szWLOC_CD.equals("DJY19") 
    	    	|| szWLOC_CD.equals("DJY30")) {										//C열연 코일제품창고
			szYD_GP = YdConstant.YD_GP_C_HR_COIL_GDS_YARD;
    	}else if(szWLOC_CD.equals("DKY21")||szWLOC_CD.equals("DWY22")) {	 									//A후판 소재
    		szYD_GP = YdConstant.YD_GP_A_PLATE_SLAB_YARD;
    	}else if(szWLOC_CD.equals("DKY30")) {										//A후판 제품창고
    		szYD_GP = YdConstant.YD_GP_PLATE_GDS_YARD;
    	}else  if("DJY25".equals(szWLOC_CD)||"DYY15".equals(szWLOC_CD)||"BSY01".equals(szWLOC_CD)||"BSY02".equals(szWLOC_CD)||"BSY03".equals(szWLOC_CD)) { //(비상야드추가)
    		szYD_GP = YdConstant.YD_GP_INTGR_YARD;
    	}else if( YdConstant.WLOC_CD_A_PLATE_PLANT.equals(szWLOC_CD)||
    			  YdConstant.WLOC_CD_B_PLATE_PLANT.equals(szWLOC_CD)){
    		szYD_GP = YdConstant.YD_GP_A_PLATE_PLANT;								//A후판조업
    	}else if( YdConstant.WLOC_CD_C_HR_PLANT.equals(szWLOC_CD) ) {
    		szYD_GP = YdConstant.YD_GP_C_HR_PLANT;									//C열연조업
		}else if( YdConstant.WLOC_CD_B_HR_PLANT.equals(szWLOC_CD) ) {
			szYD_GP = YdConstant.YD_GP_B_HR_SLAB_YARD;								//B열연
		}
		return szYD_GP;
	}
	
	
	
    /**
	 * 오퍼레이션명 : 
	 *	1.	주편공통 업데이트 처리
        *		.
	 *
	 * param jDTORecord : 전문항목
     * param String	   : 실적처리방법
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 * @ejb.transaction type="RequiresNew"
	 */     	 
	public boolean updateMatlFtmvWlrstMSlab(String stl_no){
		
		  logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶주편공통 업데이트처리 분리메소드 호출◀◀◀◀◀◀◀◀◀◀"); 
		
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
		    YdStockDAO ydStockDAO = new YdStockDAO();
		    ymCommonDAO dao = ymCommonDAO.getInstance();
		    List FrtoProductList = null;
		    
		    //공정 함수를 이용한 진도코드 가져오기
		    String queryID	= "ym.facilitywork.putwrecord.session.getListcurrprogcd";
		    FrtoProductList = dao.getCommonList(queryID, new Object[]{stl_no});
	    	JDTORecord FrtoProduct = (JDTORecord)FrtoProductList.get(0);

    		String szCurrProgCd =StringHelper.evl(FrtoProduct.getFieldString("CURR_PROG_CD"), "");
    		
			logger.println(LogLevel.DEBUG,this, "IHSF_PM_주편SLAB진도찾기==>>:"+szCurrProgCd);

		    String stkQueryId = "ym.facilitywork.putwrecord.session.updateMatlFtmvWlrstMSlabNEW"; 
	        int iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{szCurrProgCd , stl_no});
		    
		    return true;	
	}
	
    /**
	 * 오퍼레이션명 : 
	 *	1.	B CAST 주편공통 업데이트 처리
        *		.
	 *
	 * param jDTORecord : 전문항목
     * param String	   : 실적처리방법
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 * @ejb.transaction type="RequiresNew"
	 */     	 
	public boolean updateMatlFtmvWlrstBcastMSlab(String stl_no){
		
		  logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶주편공통 업데이트처리 분리메소드 호출◀◀◀◀◀◀◀◀◀◀"); 
		
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			
		    YdStockDAO ydStockDAO = new YdStockDAO();
		
		    String stkQueryId = "ym.facilitywork.putwrecord.session.updateMatlFtmvWlrstBcastMSlab"; 
	        int iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{stl_no});
		    
		    return true;	
	}
	
    /**
	 * 오퍼레이션명 : 
	 *	1.	슬라브공통 업데이트 처리
        *		.
	 *
	 * param jDTORecord : 전문항목
     * param String	   : 실적처리방법
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 * @ejb.transaction type="RequiresNew"
	 */     	 
	public boolean updateMatlFtmvWlrstSlab(String stl_no){
		
		 logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶슬라브공통 업데이트처리 분리메소드 호출◀◀◀◀◀◀◀◀◀◀"); 
		
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			
		    YdStockDAO ydStockDAO = new YdStockDAO();
		    ymCommonDAO dao = ymCommonDAO.getInstance();
		    List FrtoProductList = null;
		    
		    //공정 함수를 이용한 진도코드 가져오기
		    String queryID	= "ym.facilitywork.putwrecord.session.getListcurrprogcdSlab";
		    FrtoProductList = dao.getCommonList(queryID, new Object[]{stl_no});
	    	JDTORecord FrtoProduct = (JDTORecord)FrtoProductList.get(0);

    		String szCurrProgCd =StringHelper.evl(FrtoProduct.getFieldString("CURR_PROG_CD"), "");
    		
			logger.println(LogLevel.DEBUG,this, "IHSF_PM_주편SLAB진도찾기==>>:"+szCurrProgCd);
		
			String stkQueryId = "ym.facilitywork.putwrecord.session.updateMatlFtmvWlrstSlabNEW";
			int iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{szCurrProgCd, stl_no,stl_no});
		    
		    return true;	
	}
	
	
    /**
	 * 오퍼레이션명 : 
	 *	1.	B CAST 슬라브공통 업데이트 처리
        *		.
	 *
	 * param jDTORecord : 전문항목
     * param String	   : 실적처리방법
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 * @ejb.transaction type="RequiresNew"
	 */     	 
	public boolean updateMatlFtmvWlrstBcastSlab(String stl_no){
		
		 logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶슬라브공통 업데이트처리 분리메소드 호출◀◀◀◀◀◀◀◀◀◀"); 
		
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			
		    YdStockDAO ydStockDAO = new YdStockDAO();
		    //주편: 주문재,여재 구분없이 진도코드 :A
		    //슬라브:주문재 진도코드:A , 여재 스카핑유무;N 진도코드 ;Y
		
			String stkQueryId = "ym.facilitywork.putwrecord.session.updateMatlFtmvWlrstBcastSlab";
			int iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ stl_no,stl_no});
		    
		    return true;	
	}
	
	
    /**
	 * 오퍼레이션명 : 
	 *	1.	코일 이송실적 업데이트 처리
        *		.
	 *
	 * param jDTORecord : 전문항목
     * param String	   : 실적처리방법
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 * @ejb.transaction type="RequiresNew"
	 */     	 
	public boolean updateMatlFtmvWlrstCoil(String stl_no){
		
		 logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶코일공통 업데이트처리 분리메소드 호출◀◀◀◀◀◀◀◀◀◀"); 
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
		    YdStockDAO ydStockDAO = new YdStockDAO();
		  
		    
			//코일공통 업데이트				
			String stkQueryId = "ym.facilitywork.putwrecord.session.updateMatlFtmvWlrstCoil";
			int iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ stl_no,stl_no});
			 
		    return true;	
	}
	

	
	// 현재 작업 중인 코일번호의 정보를 가져와서 
	// 카드번호를 검사한다. 최규성
	 /**
	 * 오퍼레이션명 : 권하시 TO위치정보가 없을 경우 생성한다.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean checkStockInfo_CarMove(JDTORecord jtR){
		boolean isSuccess = false;

		logger.println(LogLevel.DEBUG,this, "============================");
		logger.println(LogLevel.DEBUG,this, "checkStockInfo_CarMove("+ jtR + " ) 시작");
		logger.println(LogLevel.DEBUG,this, "============================");

		try{ 
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			
			// 전달받은 레코드에서 필요항목의 데이터를 구한다.

			String sTc				= StringHelper.evl(jtR.getFieldString("TC"), "");
			String sYard_Id 		= StringHelper.evl(jtR.getFieldString("Yard_Id"), "");
			String sBay_Gp 			= StringHelper.evl(jtR.getFieldString("Bay_GP"), "");
			String sEquip_Kind 		= StringHelper.evl(jtR.getFieldString("Equip_Kind"), "");
			String sEquip_No 		= StringHelper.evl(jtR.getFieldString("Equip_No"), "");
			String sWbookId			= StringHelper.evl(jtR.getFieldString("GROBAL_WBOOK_ID"), "") ;
			String sStockId			= StringHelper.evl(jtR.getFieldString("Coil_No"), "").trim();
			String sPut_Position	= StringHelper.evl(jtR.getFieldString("Put_Position"), "");
			String sSch_Code 		= StringHelper.evl(jtR.getFieldString("Sch_Code"), "");
			String sPutRXLoc		= StringHelper.evl(jtR.getFieldString("Put_X_Position"), "0").trim();
			String sPutRYLoc		= StringHelper.evl(jtR.getFieldString("Put_Y_Position"), "0").trim();


			logger.println(LogLevel.DEBUG,this, "sTc=            "	+ sTc);
			logger.println(LogLevel.DEBUG,this, "sYard_Id=       "	+ sYard_Id);
			logger.println(LogLevel.DEBUG,this, "sBay_Gp=        "	+ sBay_Gp);
			logger.println(LogLevel.DEBUG,this, "sEquip_Kind=    "	+ sEquip_Kind);
			logger.println(LogLevel.DEBUG,this, "sEquip_No=      "	+ sEquip_No);
			logger.println(LogLevel.DEBUG,this, "sWbookId=       "	+ sWbookId);
			logger.println(LogLevel.DEBUG,this, "sStockId=       "	+ sStockId+"=");
			logger.println(LogLevel.DEBUG,this, "sSch_Code=      "	+ sSch_Code);
			logger.println(LogLevel.DEBUG,this, "sPut_Position=  "	+ sPut_Position);
			logger.println(LogLevel.DEBUG,this, "sPut_X_Position="	+ sPutRXLoc);
			logger.println(LogLevel.DEBUG,this, "sPut_Y_Position="	+ sPutRYLoc);
			
			String sMessage = "";
			// 코일번호로 카드번호 유무를 확인한다.
			/* SELECT STOCK_ID
                    , WBOOK_ID
                    , CAR_CARD_NO 
                    , STOCK_MOVE_TERM
                FROM TB_YM_STOCK 
               WHERE STOCK_ID = :coilno
            */
			// 차량카드번호를 기준으로 차량이적 여부를 판별한다. 
			String sQueryId_stockInfo = "ym.steelinfo.steelinforecv.YdStockDAO.getStockInfo";
			List listArgu = new ArrayList();
			listArgu.clear();
			listArgu.add(sStockId);
			
			List listStockInfo = dao.getListData(sQueryId_stockInfo, listArgu);
			
			logger.println(LogLevel.DEBUG, this, "list data: "+listStockInfo);
			JDTORecord jtrStockInfo = null;
			String sCarCardNo = "";
//			jtrStockInfo = dao.getData(sQueryId_stockInfo, new Object[]{sStockId});
			if(listStockInfo.size() > 0){
				jtrStockInfo = (JDTORecord)listStockInfo.get(0);
				sCarCardNo = StringHelper.evl(jtrStockInfo.getFieldString("CAR_CARD_NO"), "");
			}else{
				logger.println(LogLevel.DEBUG, this, "정보가 없습니다. return");
				isSuccess = false;
				return isSuccess;
			}
			
					
			if (sCarCardNo.equals("") || sCarCardNo == null )
			{
				sMessage = "저장품 정보가 존재하지 않습니다.";
				logger.println(LogLevel.DEBUG, this, sMessage);
					 
				isSuccess = false;
				//return isSuccess;
			}else if(!"T".equals(sCarCardNo.substring(0, 1))
					&& !"P".equals(sCarCardNo.substring(0, 1))
					&& !"A".equals(sCarCardNo.substring(0, 1))
					&& !"B".equals(sCarCardNo.substring(0, 1))
					&& !"C".equals(sCarCardNo.substring(0, 1))
					&& !"K".equals(sCarCardNo.substring(0, 1))
					&& !"S".equals(sCarCardNo.substring(0, 1))
					){
				if (Integer.parseInt(sCarCardNo)>= 9995 && Integer.parseInt(sCarCardNo)<=9999 )			
				{
					sMessage = "카드번호가 존재합니다. "+sCarCardNo;
					logger.println(LogLevel.DEBUG, this, sMessage);
					isSuccess = true;
					//return isSuccess;
				}else  {
					isSuccess = false;
					//return isSuccess;
				}
			} else  {
				isSuccess = false;
				//return isSuccess;
			}
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}

		return isSuccess;

	}
	
	
	/**
	 * 오퍼레이션명 : 권하시 TO위치정보가 없을 경우 생성한다.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 * @ejb.transaction type="RequiresNew"
	 */
	public boolean getinsYdCarftmvmtl(JDTORecord jtR){
		boolean isSuccess = false;
		YdCarFtmvMtlDao ydCarftmvmtlDao = new YdCarFtmvMtlDao();
		logger.println(LogLevel.DEBUG,this, "============================");
		logger.println(LogLevel.DEBUG,this, "getinsYdCarftmvmtl("+ jtR + " ) 시작");
		logger.println(LogLevel.DEBUG,this, "============================");

		try{ 
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			
			int intRtnVal = ydCarftmvmtlDao.insYdCarftmvmtl(jtR);
			if(intRtnVal>0){
				isSuccess= true;
			}
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}

		return isSuccess;

	}
	
	
    /**
	 * 오퍼레이션명 : 
	 *	1.	YDPTJ001 전송
	 * param jDTORecord : 전문항목
     * param String	   : 실적처리방법
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     	 
	public boolean SlabYDPTJ001Send(String slab_no){
		List FrtStockList2 = null;
		List FrtoProductList2 = null;
		List FrtoProductList3 = null;
		List FrtoProductList4 = null;
		String stkQueryId    ="";		 
		String CurrProg_CD 	 ="";		 
		String queryID		 ="";		 
		Boolean isSuccess = new Boolean(false);
		ymCommonDAO dao = ymCommonDAO.getInstance();
		
		 try{ 
			 logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶▶▶▶▶YDPTJ001 전송 ◀◀◀◀◀◀◀◀◀◀"); 
			 
				/*
				 * 구자원 단계별 삭제 로직  
				 */
				String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
				if(sAPP060_OLDSRC_YN.equals("Y")){
					return false;
				}
				
				
			// 주편공통에서 현재 진도코드 확인
				queryID = "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getMSLABCOMM";
				FrtoProductList2 = dao.getCommonList(queryID , new Object[]{slab_no});

				if (FrtoProductList2.size() > 0) {

					JDTORecord FrtoProduct2 = (JDTORecord) FrtoProductList2.get(0);

					CurrProg_CD = StringHelper.evl(FrtoProduct2.getFieldString("RECORD_PROG_STAT") , "");

					if (CurrProg_CD.equals("3")) {

						// YDPTJ001 송신 추가(슬라브소재이송완료실적)- 슬라브인경우 슬라브 공통에서 항목 조회에화서 전문편집
						// 슬라브 공통에서 조회

						JDTORecord FrtoProduct3 = JDTORecordFactory.getInstance().create();

						queryID = "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getSLABCOMM";
						FrtoProductList3 = dao.getCommonList(queryID , new Object[]{slab_no});
						FrtoProduct3 = (JDTORecord) FrtoProductList3.get(0);

						JDTORecord FrtoendRecord = null;
						FrtoendRecord = JDTORecordFactory.getInstance().create();
						FrtoendRecord.setField("JMS_TC_CD" , "YDPTJ001");
						FrtoendRecord.setField("JMS_TC_CREATE_DDTT" , new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
						FrtoendRecord.setField("STL_NO" , StringHelper.evl(FrtoProduct3.getFieldString("SLAB_NO") , "").trim());// 재료번호
						FrtoendRecord.setField("ORD_NO" , StringHelper.evl(FrtoProduct3.getFieldString("ORD_NO") , "")); // 주문번호
						FrtoendRecord.setField("ORD_DTL" , StringHelper.evl(FrtoProduct3.getFieldString("ORD_DTL") , "")); // 주문행번
						FrtoendRecord.setField("PLNT_PROC_CD" , StringHelper.evl(FrtoProduct3.getFieldString("PLNT_PROC_CD") , ""));// 공장공정코드
						FrtoendRecord.setField("STL_APPEAR_GP" , StringHelper.evl(FrtoProduct3.getFieldString("STL_APPEAR_GP") , ""));// 재료외형구분
						FrtoendRecord.setField("CURR_PROG_CD" , StringHelper.evl(FrtoProduct3.getFieldString("CURR_PROG_CD") , ""));// 현재진도코드
						FrtoendRecord.setField("ORD_YEOJAE_GP" , StringHelper.evl(FrtoProduct3.getFieldString("ORD_YEOJAE_GP") , "")); // 주문여재구분
						FrtoendRecord.setField("STL_WT" , StringHelper.evl(FrtoProduct3.getFieldString("SLAB_WT") , ""));// 재료중량 (SLAB중량)
						FrtoendRecord.setField("DS_MTL_WT" , ""); // 설계재료중량
						FrtoendRecord.setField("MTL_STAT_GP" , StringHelper.evl(FrtoProduct3.getFieldString("RECORD_PROG_STAT") , "")); // 재료상태구분
						FrtoendRecord.setField("RECORD_END_GP" , StringHelper.evl(FrtoProduct3.getFieldString("RECORD_END_GP") , ""));// Record 종료구분
						FrtoendRecord.setField("RECORD_END_GP1" , "");
						FrtoendRecord.setField("BEFO_PROG_CD" , StringHelper.evl(FrtoProduct3.getFieldString("BEFO_PROG_CD") , ""));// 전진도 코드
						FrtoendRecord.setField("BEF_ORD_NO" , StringHelper.evl(FrtoProduct3.getFieldString("BEF_ORD_NO") , ""));// 전주문 번호
						FrtoendRecord.setField("BEF_ORD_DTL" , StringHelper.evl(FrtoProduct3.getFieldString("BEF_ORD_DTL") , ""));// 전주문 행번
						FrtoendRecord.setField("MMATL_FEE_NO" , "");// 모재료번호
						FrtoendRecord.setField("ORDERTRANS_MATCH_GP" , StringHelper.evl(FrtoProduct3.getFieldString("MATCH_ORDERTRANS_GP") , ""));// 목전충당구분

						EJBConnector ejbConn2 = new EJBConnector("default" , "JNDIYardWrkResReg" , this);
						isSuccess = (Boolean) ejbConn2.trx("sendInternalModel" , new Class[]{JDTORecord.class} , new Object[]{FrtoendRecord});
						logger.println(LogLevel.DEBUG , this , "내부IF호출=== 슬라브소재이송완료실적.===");

						// 이송완료 후 YDCTJ032전문 송신
						JDTORecord FrtoendRecord2 = null;
						FrtoendRecord2 = JDTORecordFactory.getInstance().create();
						FrtoendRecord2.setField("JMS_TC_CD" , "YDCTJ032");
						FrtoendRecord2.setField("JMS_TC_CREATE_DDTT" , new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
						FrtoendRecord2.setField("PTOP_PLNT_GP" , "HB");
						FrtoendRecord2.setField("STL_APPEAR_GP" , "C");
						FrtoendRecord2.setField("CHG_SUP_PROG_STAT" , "09");
						FrtoendRecord2.setField("WR_OCCR_DT" , new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
						FrtoendRecord2.setField("YD_EQP_WR_CNT" , "1");
						FrtoendRecord2.setField("STL_NO1" , StringHelper.evl(FrtoProduct3.getFieldString("SLAB_NO") , "").trim());

						EJBConnector ejbConn = new EJBConnector("default" , "JNDIYardWrkResReg" , this);
						isSuccess = (Boolean) ejbConn.trx("sendInternalModel" , new Class[]{JDTORecord.class} , new Object[]{FrtoendRecord2});
						logger.println(LogLevel.DEBUG , this , "내부IF호출=== YDCTJ032.===");
					}

					else {

						stkQueryId = "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getMSLABCOMM";
						FrtoProductList4 = dao.getCommonList(stkQueryId , new Object[]{slab_no});
						JDTORecord FrtoProduct4 = (JDTORecord) FrtoProductList4.get(0);

						// YDPTJ001 송신 추가(슬라브소재이송완료실적)- 주편인경우 주편공통에서 항목 조회에화서 전문편집
						JDTORecord FrtoendRecord = null;
						FrtoendRecord = JDTORecordFactory.getInstance().create();
						FrtoendRecord.setField("JMS_TC_CD" , "YDPTJ001");
						FrtoendRecord.setField("JMS_TC_CREATE_DDTT" , new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
						FrtoendRecord.setField("STL_NO" , StringHelper.evl(FrtoProduct4.getFieldString("MSLAB_NO") , "").trim());// 재료번호
						FrtoendRecord.setField("ORD_NO" , StringHelper.evl(FrtoProduct4.getFieldString("ORD_NO") , "")); // 주문번호
						FrtoendRecord.setField("ORD_DTL" , StringHelper.evl(FrtoProduct4.getFieldString("ORD_DTL") , "")); // 주문행번
						FrtoendRecord.setField("PLNT_PROC_CD" , StringHelper.evl(FrtoProduct4.getFieldString("PLNT_PROC_CD") , ""));// 공장공정코드
						FrtoendRecord.setField("STL_APPEAR_GP" , StringHelper.evl(FrtoProduct4.getFieldString("STL_APPEAR_GP") , ""));// 재료외형구분
						FrtoendRecord.setField("CURR_PROG_CD" , StringHelper.evl(FrtoProduct4.getFieldString("CURR_PROG_CD") , ""));// 현재진도코드
						FrtoendRecord.setField("ORD_YEOJAE_GP" , StringHelper.evl(FrtoProduct4.getFieldString("ORD_YEOJAE_GP") , "")); // 주문여재구분
						FrtoendRecord.setField("STL_WT" , StringHelper.evl(FrtoProduct4.getFieldString("MSLAB_WT") , ""));// 재료중량 (SLAB중량)
						FrtoendRecord.setField("DS_MTL_WT" , ""); // 설계재료중량
						FrtoendRecord.setField("MTL_STAT_GP" , StringHelper.evl(FrtoProduct4.getFieldString("RECORD_PROG_STAT") , "")); // 재료상태구분
						FrtoendRecord.setField("RECORD_END_GP" , StringHelper.evl(FrtoProduct4.getFieldString("RECORD_END_GP") , ""));// Record 종료구분
						FrtoendRecord.setField("RECORD_END_GP1" , "");
						FrtoendRecord.setField("BEFO_PROG_CD" , StringHelper.evl(FrtoProduct4.getFieldString("BEFO_PROG_CD") , ""));// 전진도 코드
						FrtoendRecord.setField("BEF_ORD_NO" , "");// 전주문 번호
						FrtoendRecord.setField("BEF_ORD_DTL" , "");// 전주문 행번
						FrtoendRecord.setField("MMATL_FEE_NO" , "");// 모재료번호
						FrtoendRecord.setField("ORDERTRANS_MATCH_GP" , "");// 목전충당구분

						EJBConnector ejbConn3 = new EJBConnector("default" , "JNDIYardWrkResReg" , this);
						isSuccess = (Boolean) ejbConn3.trx("sendInternalModel" , new Class[]{JDTORecord.class} , new Object[]{FrtoendRecord});
						logger.println(LogLevel.DEBUG , this , "내부IF호출=== 슬라브소재이송완료실적.===");

						// 이송완료 후 YDCTJ032전문 송신
						JDTORecord FrtoendRecord2 = null;
						FrtoendRecord2 = JDTORecordFactory.getInstance().create();
						FrtoendRecord2.setField("JMS_TC_CD" , "YDCTJ032");
						FrtoendRecord2.setField("JMS_TC_CREATE_DDTT" , new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
						FrtoendRecord2.setField("PTOP_PLNT_GP" , "HB");
						FrtoendRecord2.setField("STL_APPEAR_GP" , "C");
						FrtoendRecord2.setField("CHG_SUP_PROG_STAT" , "09");
						FrtoendRecord2.setField("WR_OCCR_DT" , new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
						FrtoendRecord2.setField("YD_EQP_WR_CNT" , "1");
						FrtoendRecord2.setField("STL_NO1" , StringHelper.evl(FrtoProduct4.getFieldString("MSLAB_NO") , "").trim());

						EJBConnector ejbConn = new EJBConnector("default" , "JNDIYardWrkResReg" , this);
						isSuccess = (Boolean) ejbConn.trx("sendInternalModel" , new Class[]{JDTORecord.class} , new Object[]{FrtoendRecord2});
						logger.println(LogLevel.DEBUG , this , "내부IF호출=== YDCTJ032.===");

					}
				} else {
					// YDPTJ001 송신 추가(슬라브소재이송완료실적)- 슬라브인경우 슬라브 공통에서 항목 조회에화서 전문편집
					// 슬라브 공통에서 조회

					JDTORecord FrtoProduct3 = JDTORecordFactory.getInstance().create();

					queryID = "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getSLABCOMM";
					FrtoProductList3 = dao.getCommonList(queryID , new Object[]{slab_no});
					FrtoProduct3 = (JDTORecord) FrtoProductList3.get(0);

					JDTORecord FrtoendRecord = null;
					FrtoendRecord = JDTORecordFactory.getInstance().create();
					FrtoendRecord.setField("JMS_TC_CD" , "YDPTJ001");
					FrtoendRecord.setField("JMS_TC_CREATE_DDTT" , new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
					FrtoendRecord.setField("STL_NO" , StringHelper.evl(FrtoProduct3.getFieldString("SLAB_NO") , "").trim());// 재료번호
					FrtoendRecord.setField("ORD_NO" , StringHelper.evl(FrtoProduct3.getFieldString("ORD_NO") , "")); // 주문번호
					FrtoendRecord.setField("ORD_DTL" , StringHelper.evl(FrtoProduct3.getFieldString("ORD_DTL") , "")); // 주문행번
					FrtoendRecord.setField("PLNT_PROC_CD" , StringHelper.evl(FrtoProduct3.getFieldString("PLNT_PROC_CD") , ""));// 공장공정코드
					FrtoendRecord.setField("STL_APPEAR_GP" , StringHelper.evl(FrtoProduct3.getFieldString("STL_APPEAR_GP") , ""));// 재료외형구분
					FrtoendRecord.setField("CURR_PROG_CD" , StringHelper.evl(FrtoProduct3.getFieldString("CURR_PROG_CD") , ""));// 현재진도코드
					FrtoendRecord.setField("ORD_YEOJAE_GP" , StringHelper.evl(FrtoProduct3.getFieldString("ORD_YEOJAE_GP") , "")); // 주문여재구분
					FrtoendRecord.setField("STL_WT" , StringHelper.evl(FrtoProduct3.getFieldString("SLAB_WT") , ""));// 재료중량 (SLAB중량)
					FrtoendRecord.setField("DS_MTL_WT" , ""); // 설계재료중량
					FrtoendRecord.setField("MTL_STAT_GP" , StringHelper.evl(FrtoProduct3.getFieldString("RECORD_PROG_STAT") , "")); // 재료상태구분
					FrtoendRecord.setField("RECORD_END_GP" , StringHelper.evl(FrtoProduct3.getFieldString("RECORD_END_GP") , ""));// Record 종료구분
					FrtoendRecord.setField("RECORD_END_GP1" , "");
					FrtoendRecord.setField("BEFO_PROG_CD" , StringHelper.evl(FrtoProduct3.getFieldString("BEFO_PROG_CD") , ""));// 전진도 코드
					FrtoendRecord.setField("BEF_ORD_NO" , StringHelper.evl(FrtoProduct3.getFieldString("BEF_ORD_NO") , ""));// 전주문 번호
					FrtoendRecord.setField("BEF_ORD_DTL" , StringHelper.evl(FrtoProduct3.getFieldString("BEF_ORD_DTL") , ""));// 전주문 행번
					FrtoendRecord.setField("MMATL_FEE_NO" , "");// 모재료번호
					FrtoendRecord.setField("ORDERTRANS_MATCH_GP" , StringHelper.evl(FrtoProduct3.getFieldString("MATCH_ORDERTRANS_GP") , ""));// 목전충당구분

					EJBConnector ejbConn4 = new EJBConnector("default" , "JNDIYardWrkResReg" , this);
					isSuccess = (Boolean) ejbConn4.trx("sendInternalModel" , new Class[]{JDTORecord.class} , new Object[]{FrtoendRecord});
					logger.println(LogLevel.DEBUG , this , "내부IF호출=== 슬라브소재이송완료실적.===");

					// 이송완료 후 YDCTJ032전문 송신
					JDTORecord FrtoendRecord2 = null;
					FrtoendRecord2 = JDTORecordFactory.getInstance().create();
					FrtoendRecord2.setField("JMS_TC_CD" , "YDCTJ032");
					FrtoendRecord2.setField("JMS_TC_CREATE_DDTT" , new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
					FrtoendRecord2.setField("PTOP_PLNT_GP" , "HB");
					FrtoendRecord2.setField("STL_APPEAR_GP" , "C");
					FrtoendRecord2.setField("CHG_SUP_PROG_STAT" , "09");
					FrtoendRecord2.setField("WR_OCCR_DT" , new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
					FrtoendRecord2.setField("YD_EQP_WR_CNT" , "1");
					FrtoendRecord2.setField("STL_NO1" , StringHelper.evl(FrtoProduct3.getFieldString("SLAB_NO") , "").trim());

					EJBConnector ejbConn = new EJBConnector("default" , "JNDIYardWrkResReg" , this);
					isSuccess = (Boolean) ejbConn.trx("sendInternalModel" , new Class[]{JDTORecord.class} , new Object[]{FrtoendRecord2});
					logger.println(LogLevel.DEBUG , this , "내부IF호출=== YDCTJ032.===");

				}
			 
			}catch(DAOException daoe){
				throw daoe;
			}catch(Exception e){
				throw new EJBServiceException(e);
			}

			return true;
	}
	
	
	/**
     *  CTS 에 지시할 전문MESSAGE를 구성한다.
     *  야드L3	Target	CTS L2	I/F방법	JMS	I/F주기	REQ	I/F유형	OnLine
 	 *	T/C	THHT410		 
	 * @param schInfo : CTS 작업지시 INFO
     *
     * @return
     * @throws 
     */	 
	private String setCtsACoilMsgInfoPUT(String SaddleName,
									  String SaddleUseYn,
									  String SaddleUsage,
									  String Filler
									  ){
		
		StringBuffer sMsg = new StringBuffer();

		String TC			= ""; 	 
				
		int iTC				=  7;
		int iSaddleName		=  5;
		int iSaddleUseYn	=  1;
		int iSaddleUsage	=  1;
		int iFiller			= 36;
						   
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			
			//VALUE SETTING
			TC				= YmCommonConst.TC_THHT410;
			
			sMsg.append(YmCommonUtil.FillToString(TC			,iTC));
			sMsg.append(YmCommonUtil.FillToString(SaddleName	,iSaddleName));
			sMsg.append(YmCommonUtil.FillToString(SaddleUsage	,iSaddleUsage));
			sMsg.append(YmCommonUtil.FillToString(SaddleUseYn	,iSaddleUseYn));			
			sMsg.append(YmCommonUtil.FillToString(Filler		,iFiller));
			
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return sMsg.toString();
	}
} 

