package com.inisteel.cim.ym.facilitywork.upwrecord.session;

import java.util.List;
import java.util.ArrayList;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO;
import com.inisteel.cim.common.jms.model.CommonModel;
import com.inisteel.cim.common.jms.model.po.YMPO161;
import com.inisteel.cim.common.jms.model.dm.YMDM012;
import com.inisteel.cim.common.level2.util.*;
import com.inisteel.cim.common.parser.Level2Parser;
import com.inisteel.cim.ym.bcommon.dao.YmCommDAO;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.common.YmCommonDB;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
import com.inisteel.cim.ym.steelinfo.steelinforecv.dao.YdStockDAO;

import javax.naming.*;
import jspeed.base.record.*;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.*;
import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.util.StringHelper;
/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="CUpResRegEJB" jndi-name="JNDICUpResReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class CUpResRegSBean extends BaseSessionBean { 

	private Logger logger = null;
	private CraneSchDAO dao = null;
	JDTORecord tcRecord = JDTORecordFactory.getInstance().create();
	 // SJH    
	private YmComm ymComm = new YmComm();	
	private YmCommDAO commDao = new YmCommDAO();
	
	public void ejbCreate() {
		LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		logger 	= new Logger(config);
		dao 	= new CraneSchDAO();
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * Crane 작업자가 외부인터페이스(JMS)를 통해 Crane Up 실적을 발생시킨다.
	 *
	 * YM-AIF-006	CRANE 권상 실적	Level2	Level3	THCH550
	 * 전문코드							CHAR	07	전문코드 	
	 * CRANE 번호						CHAR	04	CRANE번호	
	 * SPARE1							CHAR	04	SPARE1   	
	 * 발생일								CHAR	06	발생일   	YYMMDD
	 * 발생시								CHAR	06	발생시   	HHMMSS
	 * 권상위치							CHAR	08	권상위치 	Conveyor, CTS, Yard Map
	 * 권상 X 위치						CHAR	06	권상X위치	
        * 		Y 위치						CHAR	06	Y위치    	
	 * SPARE2							CHAR	103	SPARE2   	
	 *
	 * YM-AIF-006	CRANE 권상 이상실적	Level2	Level3	THCH560
	 *	전문코드							CHAR	7	전문코드	
	 *	CRANE번호						CHAR	4	CRANE번호	
	 *	SPARE1							CHAR	4	SPARE1	
	 *	발생일							CHAR	6	발생일	
	 *	발생시							CHAR	6	발생시	
	 *	구권상위치						CHAR	8	구권상위치	
	 *	신규권상이상위치					CHAR	8	신규권상이상위치	
	 *	권상X위치							CHAR	6	권상X위치	
	 *	Y위치							CHAR	6	Y위치	
	 *	SPARE2							CHAR	95	SPARE2	
	 * YM-BIF-005	CRANE 권상 실적	Level2	Level3	CN1PB05
	 * 전문코드			TC				CHAR	07	전문코드    	
	 * 발생일자			Date			CHAR	10	발생일자    	YYYY-MM-DD
	 * 발생시간			Time			CHAR	08	발생시간    	HH-MM-SS
	 * 전문구분			Form			CHAR	01	전문구분    	I  : Initialize, U : Update,D : Delete,   R : Re-request
	 * 전문길이			Message_Length	CHAR	04	전문길이    	
	 * 야드구분			Yard_Id			CHAR	01	야드구분    	
	 * 동구분				Bay_GP			CHAR	01	동구분      	
	 * 설비종류			Equip_Kind		CHAR	02	설비종류    	
	 * 설비번호			Equip_No		CHAR	02	설비번호    	
	 * Coil No			Coil_No			CHAR	10	CoilNo      	
	 * Schedule Code	Sch_Code		CHAR	04	ScheduleCode	
	 * 주작업구분			Work_Id			CHAR	02	주작업구분  	01: 주작업. 02: Dummy 작업
	 * 권상위치			Up_Position		CHAR	10	권상위치    	
	 * 권상 X 위치		Up_X_Position	CHAR	06	권상X위치   	
	 * 권상 Y 위치		Up_Y_Position	CHAR	06	권상Y위치 	
	 * 
	 * YM-BIF-022	CRANE 권상 실적	Level2	Level3	CM1PB05
	 * 전문코드			TC				CHAR	07	전문코드    	
	 * 발생일자			Date			CHAR	10	발생일자    	YYYY-MM-DD
	 * 발생시간			Time			CHAR	08	발생시간    	HH-MM-SS
	 * 전문구분			Form			CHAR	01	전문구분    	I  : Initialize, U : Update,D : Delete,   R : Re-request
	 * 전문길이			Message_Length	CHAR	04	전문길이    	
	 * 야드구분			Yard_Id			CHAR	01	야드구분    	
	 * 동구분				Bay_GP			CHAR	01	동구분      	
	 * 설비종류			Equip_Kind		CHAR	02	설비종류    	
	 * 설비번호			Equip_No		CHAR	02	설비번호    	
	 * Slab No			Slab_No			CHAR	11	SlabNo      	
	 * Schedule Code	Sch_Code		CHAR	04	ScheduleCode	
	 * 주작업구분			Work_Id			CHAR	02	주작업구분  	01: 주작업. 02: Dummy 작업
	 * 권상위치			Up_Position		CHAR	10	권상위치    	
	 * 권상 X 위치		Up_X_Position	CHAR	06	권상X위치   	
	 * 권상 Y 위치		Up_Y_Position	CHAR	06	권상Y위치   	
	 *
	 * YM-BIF-026	CRANE 권상 실적	Level2	Level3	HM1PB05
	 * 전문코드			TC				CHAR	07	전문코드    	
	 * 발생일자			Date			CHAR	10	발생일자    	YYYY-MM-DD
	 * 발생시간			Time			CHAR	08	발생시간    	HH-MM-SS
	 * 전문구분			Form			CHAR	01	전문구분    	I  : Initialize, U : Update,D : Delete,   R : Re-request
	 * 전문길이			Message_Length	CHAR	04	전문길이    	
	 * 야드구분			Yard_Id			CHAR	01	야드구분    	
	 * 동구분				Bay_GP			CHAR	01	동구분      	
	 * 설비종류			Equip_Kind		CHAR	02	설비종류    	
	 * 설비번호			Equip_No		CHAR	02	설비번호    	
	 * Slab No			Slab_No			CHAR	11	SlabNo      	
	 * Schedule Code	Sch_Code		CHAR	04	ScheduleCode	
	 * 주작업구분			Work_Id			CHAR	02	주작업구분  	01: 주작업. 02: Dummy 작업
	 * 권상위치			Up_Position		CHAR	10	권상위치    	
	 * 권상 X 위치		Up_X_Position	CHAR	06	권상X위치   	
	 * 권상 Y 위치		Up_Y_Position	CHAR	06	권상Y위치
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                    
	public boolean callCraneUpRtInfo(String sMessage){
		
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
			
			String sTC 		= StringHelper.evl(jtR.getFieldString("전문코드"), "");
			String sForm 		= StringHelper.evl(jtR.getFieldString("전문구분"), "");
			
			if("R".equals(sForm)){
				sForm = YmCommonConst.CRANE_FUNC_L;
			}else{
				sForm = YmCommonConst.CRANE_FUNC_N;
			} 
			
			if(YmCommonConst.TC_THCH550.equals(sTC)){//A열연 Coil 권상실적
				
				String sLegacyCraneNo 	= StringHelper.evl(jtR.getFieldString("CRANE번호"), "");
				String sUp_Position 	= StringHelper.evl(jtR.getFieldString("권상위치"), "");
				
				JDTORecord crnRc  = null;
				JDTORecord schRc  = null;
		    	
				/*
				 * A열연 Legacy Crane No를 가지고 현재 Crane No를 가져온다.
				 */
				crnRc = dao.getCurEquipNoWithLegacyEquipNo(sLegacyCraneNo);
				if(crnRc == null){
					throw new EJBServiceException("=권상실적=>A열연 레거시 크레인정보 존재안함.");
	    			}
	    			/*
				 * A열연 권상실적 전문에는 Crane No 정보만 존재.
				 * Crane No를 권상실적에 필요한 정보를 SCH, EQUIP 
				 * TABLE에서 가져온다.
				 */		
			    	schRc = dao.getSchInfoWithEquipNo(YmCommonConst.YD_GP_1,
			    									  YmCommonConst.WORK_PROG_STAT_1,//UP지시	
			    			                          StringHelper.evl(crnRc.getFieldString("CRANE_NO"), ""));
			    	if(schRc == null){
			    		isSuccess = sendWorkFinishInfo_Coil(sLegacyCraneNo,
			    											YmCommonConst.RESULT_MODE_1);
		    			throw new EJBServiceException("=권상실적=>A열연 스케쥴정보 존재안함.");
		    		}
		    		
		    		String sYdGp  = StringHelper.evl(schRc.getFieldString("YD_GP"), "");
		    		String sBayGp = StringHelper.evl(schRc.getFieldString("BAY_GP"), "");
		    		
	 			rRd.setField("TC"				, StringHelper.evl(jtR.getFieldString("전문코드"), ""));
				rRd.setField("Yard_Id"			, sYdGp);
				rRd.setField("Bay_GP"			, sBayGp);
				rRd.setField("Equip_Kind"		, StringHelper.evl(schRc.getFieldString("EQUIP_KIND"), ""));
				rRd.setField("Equip_No"			, StringHelper.evl(schRc.getFieldString("EQUIP_NO"), ""));
				rRd.setField("Coil_No"			, StringHelper.evl(schRc.getFieldString("STOCK_ID"), ""));
				rRd.setField("Sch_Code"		, StringHelper.evl(schRc.getFieldString("SCH_WORK_KIND"), ""));
				rRd.setField("Work_Id"			, StringHelper.evl(schRc.getFieldString("SCH_WORK_AID_YN"), ""));
				rRd.setField("Up_Position"		, YmCommonUtil.setCurPositionWithLegacy(sUp_Position,sYdGp,sBayGp));
				rRd.setField("Up_X_Position"		, StringHelper.evl(jtR.getFieldString("권상X위치"), ""));
				rRd.setField("Up_Y_Position"		, StringHelper.evl(jtR.getFieldString("Y위치"), ""));
				/**
				 * 이후 처리를 위해 필요한 값 셋팅
				 */
				rRd.setField("LEGACY_CRANE_NO", sLegacyCraneNo);
				
			}else if(YmCommonConst.TC_THCH560.equals(sTC)){//A열연 Coil 권상이상실적
				
				String sLegacyCraneNo 	= StringHelper.evl(jtR.getFieldString("CRANE번호"), "");
				/**
				 * 권상이상실적 발생시 신규권상위치(적치정보)는 줄 수 없슴
				 * X,Y 좌표값으로 적치정보 찾는 모듈을 만들어야 함
				 */
				String sUp_Position 	= StringHelper.evl(jtR.getFieldString("신규권상이상위치"), "");
				 
				JDTORecord crnRc  = null;
				JDTORecord schRc  = null;
		    	
				/*
				 * A열연 Legacy Crane No를 가지고 현재 Crane No를 가져온다.
				 */
				crnRc = dao.getCurEquipNoWithLegacyEquipNo(sLegacyCraneNo);
				if(crnRc == null){
					throw new EJBServiceException("=권상실적=>A열연 레거시 크레인정보 존재안함.");
		    		}
		    		/*
				 * A열연 권상실적 전문에는 Crane No 정보만 존재.
				 * Crane No를 권상실적에 필요한 정보를 SCH, EQUIP 
				 * TABLE에서 가져온다.
				 */		
			    	schRc = dao.getSchInfoWithEquipNo(YmCommonConst.YD_GP_1,
			    									  YmCommonConst.WORK_PROG_STAT_1,//UP지시	
			    			                          StringHelper.evl(crnRc.getFieldString("CRANE_NO"), ""));
			    	if(schRc == null){
			    		isSuccess = sendWorkFinishInfo_Coil(sLegacyCraneNo,
			    											YmCommonConst.RESULT_MODE_1);
		    			throw new EJBServiceException("=권상실적=>A열연 스케쥴정보 존재안함.");
		    		}
		    		
		    		String sYdGp  = StringHelper.evl(schRc.getFieldString("YD_GP"), "");
		    		String sBayGp = StringHelper.evl(schRc.getFieldString("BAY_GP"), "");
		    		
	 			rRd.setField("TC"					, StringHelper.evl(jtR.getFieldString("전문코드"), ""));
				rRd.setField("Yard_Id"				, sYdGp);
				rRd.setField("Bay_GP"				, sBayGp);
				rRd.setField("Equip_Kind"			, StringHelper.evl(schRc.getFieldString("EQUIP_KIND"), ""));
				rRd.setField("Equip_No"				, StringHelper.evl(schRc.getFieldString("EQUIP_NO"), ""));
				rRd.setField("Coil_No"				, StringHelper.evl(schRc.getFieldString("STOCK_ID"), ""));
				rRd.setField("Sch_Code"			, StringHelper.evl(schRc.getFieldString("SCH_WORK_KIND"), ""));
				rRd.setField("Work_Id"				, StringHelper.evl(schRc.getFieldString("SCH_WORK_AID_YN"), ""));
				rRd.setField("Up_Position"			, YmCommonUtil.setCurPositionWithLegacy(sUp_Position,sYdGp,sBayGp));
				rRd.setField("Up_X_Position"			, StringHelper.evl(jtR.getFieldString("권상X위치"), ""));
				rRd.setField("Up_Y_Position"			, StringHelper.evl(jtR.getFieldString("Y위치"), ""));
				rRd.setField("WorkOrder_Position"		, YmCommonUtil.setCurPositionWithLegacy(
												  		StringHelper.evl(jtR.getFieldString("구권상위치"), ""),sYdGp,sBayGp));
				/**
				 * 이후 처리를 위해 필요한 값 셋팅
				 */
				rRd.setField("LEGACY_CRANE_NO", sLegacyCraneNo);
				
			}else if(YmCommonConst.TC_CN1PB05.equals(sTC)){  //B열연 Coil 권상실적
				
				rRd.setField("TC"				, StringHelper.evl(jtR.getFieldString("전문코드"), ""));
				rRd.setField("Yard_Id"			, StringHelper.evl(jtR.getFieldString("야드구분"), ""));
				rRd.setField("Bay_GP"			, StringHelper.evl(jtR.getFieldString("동구분"), ""));
				rRd.setField("Equip_Kind"		, StringHelper.evl(jtR.getFieldString("설비종류"), ""));
				rRd.setField("Equip_No"			, StringHelper.evl(jtR.getFieldString("설비번호"), ""));
				rRd.setField("Coil_No"			, StringHelper.evl(jtR.getFieldString("CoilNo"), ""));
				rRd.setField("Sch_Code"		, StringHelper.evl(jtR.getFieldString("ScheduleCode"), ""));
				rRd.setField("Work_Id"			, StringHelper.evl(jtR.getFieldString("주작업구분"), ""));
				rRd.setField("Up_Position"		, StringHelper.evl(jtR.getFieldString("권상위치"), ""));
				rRd.setField("Up_X_Position"		, StringHelper.evl(jtR.getFieldString("권상X위치"), ""));
				rRd.setField("Up_Y_Position"		, StringHelper.evl(jtR.getFieldString("권상Y위치"), ""));
			
			}else if(YmCommonConst.TC_CM1PB05.equals(sTC)			//B열연 Slab 권상실적
					|| YmCommonConst.TC_HM1PB05.equals(sTC)			//A_A열연 Slab 권상실적(MCH)	
					|| YmCommonConst.TC_HM1PB55.equals(sTC)){  		//A_B열연 Slab 권상실적(MCH)					
				
				rRd.setField("TC"				, StringHelper.evl(jtR.getFieldString("전문코드"), ""));
				rRd.setField("Yard_Id"			, StringHelper.evl(jtR.getFieldString("야드구분"), ""));
				rRd.setField("Bay_GP"			, StringHelper.evl(jtR.getFieldString("동구분"), ""));
				rRd.setField("Equip_Kind"		, StringHelper.evl(jtR.getFieldString("설비종류"), ""));
				rRd.setField("Equip_No"			, StringHelper.evl(jtR.getFieldString("설비번호"), ""));
				rRd.setField("Slab_No"			, StringHelper.evl(jtR.getFieldString("SlabNo"), ""));
				rRd.setField("Sch_Code"		, StringHelper.evl(jtR.getFieldString("ScheduleCode"), ""));
				rRd.setField("Work_Id"			, StringHelper.evl(jtR.getFieldString("주작업구분"), ""));
				rRd.setField("Up_Position"		, StringHelper.evl(jtR.getFieldString("권상위치"), ""));
				rRd.setField("Up_X_Position"		, StringHelper.evl(jtR.getFieldString("권상X위치"), ""));
				rRd.setField("Up_Y_Position"		, StringHelper.evl(jtR.getFieldString("권상Y위치"), ""));
			}
						
	    	isSuccess = callCraneUpRtInfo(rRd,
	    							   sForm);
	    	
	    	return isSuccess;
	    	
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}
	
 	/**
	 * 오퍼레이션명 : 
	 *
	 * 야드운영자가 Schedule관리기능을 통해 Crane Up 실적을 발생시킨다.
        * 
        * param String	 : 전문코드
        * param String	 : SCH_ID
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
       public boolean callCraneUpRtInfo(String sTC, String sSchId){
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
       	return callCraneUpRtInfo(sTC, sSchId,"");
    	}
	/**
	 * 오퍼레이션명 : 
	 *
	 * 야드운영자가 Schedule관리기능을 통해 Crane Up 실적을 발생시킨다.
        * 
        * param String	 : 전문코드
        * param String	 : SCH_ID
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean callCraneUpRtInfo(String sTC, String sSchId, String sUserId){
		
		boolean isSuccess = false;
		logger.println(LogLevel.DEBUG,this, "callCraneUpRtInfo(String sTC, String sSchId, String sUserId) 시작");
		logger.println(LogLevel.DEBUG,this, "화면처리 권상TC    ="+sTC+"=");
		logger.println(LogLevel.DEBUG,this, "화면처리 권상SCHID ="+sSchId+"=");
		logger.println(LogLevel.DEBUG,this, "화면처리 권상USERID ="+sUserId+"=");
		
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
	    		logger.println(LogLevel.DEBUG,this, "화면처리 권상에러..");
	    	   	logger.println(LogLevel.DEBUG,this, "스케쥴정보를 가져올 수 없슴.");
    			throw new EJBServiceException("=권상실적=>스케쥴정보 존재안함.");
    		}
    		
    		JDTORecord layerRc = null;
	    	 
	    	layerRc = dao.getUpStackLayerListWithSchId(sSchId);
	    	
	    	if(layerRc == null){
	    		logger.println(LogLevel.DEBUG,this, "화면처리 권상에러..");
	    	   	logger.println(LogLevel.DEBUG,this, "적치단정보를 가져올 수 없슴.");
    			//throw new EJBServiceException("=권상실적=>적치단정보 존재안함.");
    			layerRc = createFromLoc(sSchId);
    		}
    		
	    	if(YmCommonConst.TC_THCH550.equals(sTC)||//A열연 Coil 권상실적
	    	   YmCommonConst.TC_CN1PB05.equals(sTC)){//B열연 Coil 권상실적	

				rRd.setField("TC", sTC);
				rRd.setField("Yard_Id"		, StringHelper.evl(schInfo.getFieldString("YD_GP"), ""));
				rRd.setField("Bay_GP"		, StringHelper.evl(schInfo.getFieldString("BAY_GP"), ""));
				rRd.setField("Equip_Kind"	, YmCommonConst.EQUIP_KIND_CR);
				rRd.setField("Equip_No"		, StringHelper.evl(schInfo.getFieldString("SCH_WORK_EQUIP_NO"), ""));
				rRd.setField("Coil_No"		, StringHelper.evl(schInfo.getFieldString("STOCK_ID"), ""));
				rRd.setField("Sch_Code"		, StringHelper.evl(schInfo.getFieldString("SCH_WORK_KIND"), ""));
				rRd.setField("Work_Id"		, StringHelper.evl(schInfo.getFieldString("SCH_WORK_AID_YN"), ""));
				rRd.setField("Up_Position"	, StringHelper.evl(layerRc.getFieldString("STACK_COL_GP"), "")+
											  StringHelper.evl(layerRc.getFieldString("STACK_BED_GP"), "")+
											  StringHelper.evl(layerRc.getFieldString("STACK_LAYER_GP"), ""));
				rRd.setField("Up_X_Position", StringHelper.evl(layerRc.getFieldString("STACK_LAYER_X_AXIS"), ""));
				rRd.setField("Up_Y_Position", StringHelper.evl(layerRc.getFieldString("STACK_LAYER_Y_AXIS"), ""));
				
				
				if(YmCommonConst.TC_THCH550.equals(sTC)){
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
				
			}else if(YmCommonConst.TC_CM1PB05.equals(sTC)			//B열연 Slab 권상실적
					|| YmCommonConst.TC_HM1PB05.equals(sTC)			//A_A열연 Slab 권상실적(MCH)	
					|| YmCommonConst.TC_HM1PB55.equals(sTC)){  		//A_B열연 Slab 권상실적(MCH)	
				
				rRd.setField("TC", sTC);
				rRd.setField("Yard_Id"		, StringHelper.evl(schInfo.getFieldString("YD_GP"), ""));
				rRd.setField("Bay_GP"		, StringHelper.evl(schInfo.getFieldString("BAY_GP"), ""));
				rRd.setField("Equip_Kind"	, YmCommonConst.EQUIP_KIND_CR);
				rRd.setField("Equip_No"		, StringHelper.evl(schInfo.getFieldString("SCH_WORK_EQUIP_NO"), ""));
				rRd.setField("Slab_No"		, StringHelper.evl(schInfo.getFieldString("STOCK_ID"), ""));
				rRd.setField("Sch_Code"		, StringHelper.evl(schInfo.getFieldString("SCH_WORK_KIND"), ""));
				rRd.setField("Work_Id"		, StringHelper.evl(schInfo.getFieldString("SCH_WORK_AID_YN"), ""));
				rRd.setField("Up_Position"	, StringHelper.evl(layerRc.getFieldString("STACK_COL_GP"), "")+
											  StringHelper.evl(layerRc.getFieldString("STACK_BED_GP"), "")+
											  StringHelper.evl(layerRc.getFieldString("STACK_LAYER_GP"), ""));
				rRd.setField("Up_X_Position", StringHelper.evl(layerRc.getFieldString("STACK_LAYER_X_AXIS"), ""));
				rRd.setField("Up_Y_Position", StringHelper.evl(layerRc.getFieldString("STACK_LAYER_Y_AXIS"), ""));
			}
	    	logger.println(LogLevel.DEBUG,this, "callCraneUpRtInfo(JDTORecord,String) 호출" );
	    	logger.println(LogLevel.DEBUG,this, "인자전달(Record):" + rRd );
	    	
	    	isSuccess = callCraneUpRtInfo(rRd,
	    								  YmCommonConst.CRANE_FUNC_V);
	    	
	    	return isSuccess;
	    	
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 야드운영자가 Schedule관리기능을 통해 Crane Up 실적을 발생시킨다.
        * 
        * param jDTORecord : 전문항목
        * param String	 : 실적처리방법
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
	public boolean callCraneUpRtInfo(JDTORecord jtRcd,
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
			   YmCommonConst.TC_THCH550.equals(sTc) || //A열연 Coil 권상실적
			   YmCommonConst.TC_THCH560.equals(sTc) || //A열연 Coil 권상이상실적
			   YmCommonConst.TC_CN1PB05.equals(sTc)	   //B열연 Coil 권상실적
			  ){
				
				isSuccess = callCraneUpRtInfo_Coil(jtRcd,
												   sFuncGbn);

			}else if(YmCommonConst.TC_CM1PB05.equals(sTc)){ 		//B열연 Slab 권상실적
				
				
				isSuccess = callCraneUpRtInfo_Slab(jtRcd,
												   sFuncGbn);
			}else if(YmCommonConst.TC_HM1PB05.equals(sTc)			//A_A열연 Slab 권상실적(MCH)
					||YmCommonConst.TC_HM1PB55.equals(sTc)){  		//A_B열연 Slab 권상실적(MCH)
				
				isSuccess = callCraneUpRtInfo_ASlab(jtRcd,
						   							sFuncGbn);
			}  
			
//			try{
//	    		com.inisteel.cim.common.realtime.RealTimeUtil.pushTopic("ymCraneTopic");
//			}catch(Exception e){}
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
	 *	0.	COIL INFO
        *		야드운영자가 Schedule관리기능을 통해 Crane Up 실적을 발생시킨다.
        * 
        * param jDTORecord : 전문항목
        * param String	 : 실적처리방법
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */          
	public boolean callCraneUpRtInfo_Coil(JDTORecord jtRcd,
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
	    	 * 1.	작업오차검정
	    	 */
	    	 	logger.println(LogLevel.DEBUG,this,"=COIL UP STEP 1 START="); 
	    	
	    	/**
	    	 * 2.	권상실적처리
	    	 */ 
	    	 	logger.println(LogLevel.DEBUG,this,"=COIL UP STEP 2 START=");   
	    		boolean isYd = setCraneUpRtInfo_Coil(jtRcd,
	    											 sFuncGbn);
	    		if(!isYd) return isSuccess;
	    	/**
	    	 * 3.	야드작업실적송신
	    	 */
	    	 	logger.println(LogLevel.DEBUG,this,"=COIL UP STEP 3 START=");   
	    		boolean isWrk = callInnerWorkInfo_Coil(jtRcd);
	    	
	    	/**
	    	 * 4.	MILL L2 작업결과 송신
	    	 */ 
	    	 	logger.println(LogLevel.DEBUG,this,"=COIL UP STEP 4 START="); 
	    	 	boolean isMill=	callMillFinishInfo_Coil(jtRcd); 	
	    	/**
	    	 * 5.	Crane 작업결과 송신
	    	 */  
	    	 	logger.println(LogLevel.DEBUG,this,"=COIL UP STEP 5 START=");   
	    		boolean isFsh = callWorkFinishInfo_Coil(jtRcd,
	    												YmCommonConst.RESULT_MODE_0); 
	    	/** 
	    	 * 6.	대차출발지시 Call
	    	 */
	    	 	logger.println(LogLevel.DEBUG,this,"=COIL UP STEP 6 START=");   
	    		boolean isTC  =	callTcWorkInfo_Coil(jtRcd); 
	    		
//	 		/**
//	    	 * 5.	HFL 결속대 보급/출하 실적 정보 송신 Call
//	    	 */  
//	    	 	logger.println(LogLevel.DEBUG,this,"=COIL PUT STEP HFL결속대 START="); 
//    	 		boolean isHFL =	callHFLInfo_Coil(jtRcd);
	    	
	    	// 6번 7번은 순서가 의미가 있슴 - 주의요망.
	        /* 이정훈 2007.04.23
	         *  대차 출하 순서 변경
	         */
	    	String sSch_Code  = StringHelper.evl(jtRcd.getFieldString("Sch_Code"), "");
	    	if(YmCommonConst.NEW_SCH_WORK_KIND_CTFL.equals(sSch_Code))	{
	    		
	    		logger.println(LogLevel.DEBUG,this,"=대차 출하 PUT 작업 지시");   
	    		boolean isSch =	callCraneSchInfo_Coil(jtRcd);
	    		
	    		logger.println(LogLevel.DEBUG,this,"=대차 출하 스케쥴 Call");   
	    		boolean isCool=	callWbookInfo_Coil(jtRcd); 
	    	} else {
	    		/**
	    		 * 7.	미등록 작업예정 스케쥴을 생성 Call
	    		 */   
	    	 	logger.println(LogLevel.DEBUG,this,"=COIL UP STEP 7 START=");   
	    		boolean isCool=	callWbookInfo_Coil(jtRcd);     		
	    		/**
	    		 * 8.	PUT 작업지시 Call
	    		 */   
	    	 	logger.println(LogLevel.DEBUG,this,"=COIL UP STEP 8 START=");   
	    		boolean isSch =	callCraneSchInfo_Coil(jtRcd);
	    	}
	    	
	    	/** 
	    	 * 9.	구내운송 하차개시/완료 Call
	    	 */
	    	 	logger.println(LogLevel.DEBUG,this,"=COIL UP STEP 9 START=");   
	    		boolean isTSTC  =	callTSTcWorkInfo_Coil(jtRcd); 
	    		
    		/** 
	    	 * 10.	육송출하/임가공 상차개시 Call
	    	 */
	    	 	logger.println(LogLevel.DEBUG,this,"=COIL UP STEP 10 START=");   
	    		callDMTcWorkInfo_Coil(jtRcd); 	
    		
	    	
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
			
			if(YmCommonConst.YD_GP_3.equals(sYard_Id)){

			if(YmCommonConst.NEW_SCH_WORK_KIND_CFSO.equals(sSchCode)){
				//HFL 결속대 추출실적
					
					recInTemp = JDTORecordFactory.getInstance().create();
	    			recInTemp.setField("MSG_ID"       , "YDHRJ002");						//전문코드
	    			recInTemp.setField("STL_NO"       , sStockId);							//재료번호
	    			recInTemp.setField("TREAT_GP"     , "3");							//재료번호
	    			recInTemp.setField("YD_UP_CMPL_DT", YmCommonUtil.getTcDate("yyyyMMddHHmmss"));  //크레인스케줄ID
	    			ydDelegate.sendMsg(recInTemp);

					logger.println(LogLevel.DEBUG,this, "열연조업 L3 HFL 결속대 추출완료 실적 전송 송신 완료");					
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
	 *	0.	SLAB INFO
        *		야드운영자가 Schedule관리기능을 통해 Crane Up 실적을 발생시킨다.
        * 
        * param jDTORecord : 전문항목
        * param String	 : 실적처리방법
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */               
	public boolean callCraneUpRtInfo_Slab(JDTORecord jtRcd,
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
	    	 * 1.	작업오차검정
	    	 */
	    	 	logger.println(LogLevel.DEBUG,this,"=SLAB UP STEP 1 START="); 
	    	
	    	/**
	    	 * 2.	권상실적처리
	    	 */ 
	    	 	logger.println(LogLevel.DEBUG,this,"=SLAB UP STEP 2 START="); 
	    	 	boolean isYd = setCraneUpRtInfo_Slab(jtRcd,
	    	 										 sFuncGbn);
	    		if(!isYd) return isSuccess;
	    	/**
	    	 * 3.	야드작업실적송신
	    	 */
	    	 	logger.println(LogLevel.DEBUG,this,"=SLAB UP STEP 3 START="); 
	    		boolean isWrk = callInnerWorkInfo_Slab(jtRcd);
	    	/**
	    	 * 4.	Crane 작업결과 송신
	    	 */  
	    	 	logger.println(LogLevel.DEBUG,this,"=SLAB UP STEP 4 START="); 
	    		boolean isFsh = callWorkFinishInfo_Slab(jtRcd); 
	    	/** 
	    	 * 5.	대차출발지시 Call
	    	 */
	    	 	logger.println(LogLevel.DEBUG,this,"=SLAB UP STEP 5 START="); 
	    		boolean isTC  =	callTcWorkInfo_Slab(jtRcd); 
	    	/**
	    	 * 6.	미등록 작업예정 스케쥴을 생성 Call
	    	 */   
	    	 	logger.println(LogLevel.DEBUG,this,"=COIL UP STEP 6 START=");   
	    		boolean isCool=	callWbookInfo_Slab(jtRcd);     	
	    	/**
	    	 * 7.	PUT 작업지시 Call
	    	 */   
	    	 	logger.println(LogLevel.DEBUG,this,"=SLAB UP STEP 7 START="); 
	    		boolean isSch =	callCraneSchInfo_Slab(jtRcd);
	    	/**
	    	 * 8.	크레인 지원작업 Call
	    	 */   
	    	 	logger.println(LogLevel.DEBUG,this,"=SLAB UP STEP 8 START="); 
	    		//boolean isSppt = callCraneSupportInfo_Slab(jtRcd);
	    	/**
	    	 * 9.	C동 대차하차 작업예약 스케쥴 Call
	    	 */   
	    	 	logger.println(LogLevel.DEBUG,this,"=SLAB UP STEP 9 START="); 
	    		boolean isSppt = callCraneSvmuInfo_Slab(jtRcd);  
	    		  
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
	 *	0.	A열연 SLAB INFO(MCH)
        *		야드운영자가 Schedule관리기능을 통해 Crane Up 실적을 발생시킨다.
        * 
        * param jDTORecord : 전문항목
        * param String	 : 실적처리방법
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */         
	public boolean callCraneUpRtInfo_ASlab(JDTORecord jtRcd,
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
	    	 * 1.	작업오차검정
	    	 */
				logger.println(LogLevel.DEBUG,this,"=A열연 SLAB야드 Crane Up 실적 시작");
	    	 	logger.println(LogLevel.DEBUG,this,"=SLAB UP STEP 1 START="); 
	    	
	    	/**
	    	 * 2.	권상실적처리
	    	 */ 
	    	 	logger.println(LogLevel.DEBUG,this,"=SLAB UP STEP 2 START="); 
	    	 	boolean isYd = setCraneUpRtInfo_Slab(jtRcd,
	    	 										 sFuncGbn);
	    		if(!isYd) return isSuccess;
	    	/**
	    	 * 3.	야드작업실적송신
	    	 */
	    	 	logger.println(LogLevel.DEBUG,this,"=SLAB UP STEP 3 START="); 
	    		boolean isWrk = callInnerWorkInfo_Slab(jtRcd);
	    	/**
	    	 * 4.	Crane 작업결과 송신
	    	 */  
	    	 	logger.println(LogLevel.DEBUG,this,"=SLAB UP STEP 4 START="); 
	    		boolean isFsh = callWorkFinishInfo_Slab(jtRcd); 
	    	/** 
	    	 * 5.	대차출발지시 Call

	    	 	logger.println(LogLevel.DEBUG,this,"=SLAB UP STEP 5 START="); 
	    		boolean isTC  =	callTcWorkInfo_Slab(jtRcd);
	    	 */	    		 
	    	/**
	    	 * 6.	미등록 작업예정 스케쥴을 생성 Call
	    	 */   
	    	 	logger.println(LogLevel.DEBUG,this,"=COIL UP STEP 6 START=");   
	    		boolean isCool=	callWbookInfo_Slab(jtRcd);     	
	    	/**
	    	 * 7.	PUT 작업지시 Call
	    	 */   
	    	 	logger.println(LogLevel.DEBUG,this,"=SLAB UP STEP 7 START="); 
	    		boolean isSch =	callCraneSchInfo_ASlab(jtRcd);
	    		
	    	/**
	    	 * 5.	야드맵 정보 송신 Call(A열연 차상국)
	    	 */  
	    	 	logger.println(LogLevel.DEBUG,this,"=SLAB(A) PUT STEP 5 START="); 
    	 		boolean isMap =	callMapInfo_ASlab(jtRcd);
    	 		
	    	/**
	    	 * 6.	야드맵 정보 송신 Call(연주7호기)
	    	 */  
	    	 	logger.println(LogLevel.DEBUG,this,"=SLAB(A) PUT STEP 7 START="); 
    	 		boolean isMap7 = callMapInfo_ASlab7(jtRcd);
	    	 		
	    	/**
	    	 * 8.	크레인 지원작업 Call
	    	 */   
	    	 	logger.println(LogLevel.DEBUG,this,"=SLAB UP STEP 8 START="); 
	    		//boolean isSppt = callCraneSupportInfo_Slab(jtRcd);
	    	  
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
	 *	1.	COIL INFO
        *		Crane Up 실적에 관련된 상태정보를 등록 및 수정한다.
        * 
        * param jDTORecord 	: 전문항목
        * param String 		: 실적처리방법
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */              
	public boolean setCraneUpRtInfo_Coil(JDTORecord jtR,
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
			String sStockId         = StringHelper.evl(jtR.getFieldString("Coil_No"), "").trim();
			String sSch_Code 		= StringHelper.evl(jtR.getFieldString("Sch_Code"), "");
			String sWork_Id 		= YmCommonUtil.getCurDataWithLegacy(
									  StringHelper.evl(jtR.getFieldString("Work_Id"), ""));
			String sUp_Position 	= StringHelper.evl(jtR.getFieldString("Up_Position"), "");
			String sUp_X_Position 	= StringHelper.evl(jtR.getFieldString("Up_X_Position"), "");
			String sUp_Y_Position 	= StringHelper.evl(jtR.getFieldString("Up_Y_Position"), "");

			logger.println(LogLevel.DEBUG,this, "sTc="				+ sTc);
			logger.println(LogLevel.DEBUG,this, "sYard_Id="			+ sYard_Id);
			logger.println(LogLevel.DEBUG,this, "sBay_Gp="			+ sBay_Gp);
			logger.println(LogLevel.DEBUG,this, "sEquip_Kind="		+ sEquip_Kind);
			logger.println(LogLevel.DEBUG,this, "sEquip_No="		+ sEquip_No);
			logger.println(LogLevel.DEBUG,this, "sStockId="			+ sStockId+"==");
			logger.println(LogLevel.DEBUG,this, "sSch_Code="		+ sSch_Code);
			logger.println(LogLevel.DEBUG,this, "sWork_Id="			+ sWork_Id);
			logger.println(LogLevel.DEBUG,this, "sUp_Position="		+ sUp_Position);
			logger.println(LogLevel.DEBUG,this, "sUp_X_Position="	+ sUp_X_Position);
			logger.println(LogLevel.DEBUG,this, "sUp_Y_Position="	+ sUp_Y_Position);
			
			if("".equals(sUp_Position)){
				logger.println(LogLevel.DEBUG,this, "권상에러..");
	    	   	logger.println(LogLevel.DEBUG,this, "권상위치정보 존재안함");
	    	   	throw new EJBServiceException("=권상실적=> 권상위치정보 존재안함.");
			}
			
			/*
			 * Crane UP실적 저장품의 적치단정보를 가져온다.
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
	    		logger.println(LogLevel.DEBUG,this, "권상에러..");
	    	   	logger.println(LogLevel.DEBUG,this, "스케쥴ID를 가져올 수 없슴.");
    			throw new EJBServiceException("=권상실적=>스케쥴정보 존재안함.");
    		}
    		
	    	String sGrobalSchId   = StringHelper.evl(schRc.getFieldString("SCH_ID"), "");
	    	String sGrobalWbookId = StringHelper.evl(schRc.getFieldString("WBOOK_ID"), "");
	    	/**
	    	 *	메소드 전역변수 셋팅
	    	 */
	    	jtR.setField("GROBAL_WBOOK_ID",sGrobalWbookId);	
	    	
	    	logger.println(LogLevel.DEBUG,this, "sGrobalSchId	="	+ sGrobalSchId);
	    	logger.println(LogLevel.DEBUG,this, "sGrobalWbookId ="	+ sGrobalWbookId);
	    	
	    	/*
			 * Crane 설비테이블에 셋팅된 SCH_ID 값을 가져온다.
			 */		
	    	JDTORecord craneV = null;
	    	
	    	craneV = dao.getEquipInfoWithEquipNo(sYard_Id,
	    										 sEquip_No);
	    	if(craneV == null){
	    		logger.println(LogLevel.DEBUG,this, "권상에러..");
	    	   	logger.println(LogLevel.DEBUG,this, "크레인정보를 가져올 수 없슴.");
    			throw new EJBServiceException("=권상실적=>크레인정보 존재안함.");
    		}
    		
	    	String sTWprogStat = StringHelper.evl(craneV.getFieldString("WPROG_STAT"), "");
	    	String sTWbookId   = StringHelper.evl(craneV.getFieldString("WBOOK_ID"), "");
	    	
	    	logger.println(LogLevel.DEBUG,this, "sTWprogStat="	+ sTWprogStat);
	    	logger.println(LogLevel.DEBUG,this, "sTWbookId="	+ sTWbookId);
	    	
	    	/*
			 * 전문을 통해 받은 스케쥴ID와 
			 * 해당 크레인에 셋팅된 스케쥴ID가 같은지를 
			 * 체크한다.
			 * 같은경우에만 권상처리를 한다.
			 * - 현재 작업진행상태가 '1'(UP지시)
			 * - 스케쥴ID가 동일한 경우
			 */	
	    	if(!YmCommonConst.WORK_PROG_STAT_1.equals(sTWprogStat)||
	    	   !sGrobalSchId.equals(sTWbookId)){
	    	   	
	    	   	logger.println(LogLevel.DEBUG,this, "권상에러..");
	    	   	logger.println(LogLevel.DEBUG,this, "스케쥴ID나 작업진행상태에러..");
	    	   	throw new EJBServiceException("=권상실적=>스케쥴ID나 작업진행상태에러.");
	    	}
	    	
	    	/*
			 * Crane UP실적 저장품의 적치단정보를 가져온다.
			 * SADDLE인 경우 입측은 stack_layer_active_stat = 'C' 이다.
			 * 따라서 위 조건은 제외시킨다.
			 * tb_ym_stacklayer Table stack_layer_active_stat : 'O'(Open)
			 * tb_ym_stacklayer Table stack_layer_stat 	   	  : 'U'(Up Schedule 수행예정)
			 */			
			
			JDTORecord layerRc = null;
			String sStackColGp = "";
			String sStackBedGp = "";
			String sStackLayerGp = "";
			
			
	    	layerRc = dao.getUpStackLayerListWithSchId(sGrobalSchId);
	    	
	    	if(layerRc == null){
	    		logger.println(LogLevel.DEBUG,this, "권상에러..");
	    	   	logger.println(LogLevel.DEBUG,this, "적치단정보를 가져올 수 없슴.");
    			//throw new EJBServiceException("=권상실적=>적치단정보 존재 안함.");
    			//layerRc = createFromLoc(sGrobalSchId);
    			 
    			JDTORecord schInfo = dao.getSchInfoWithSchId(sGrobalSchId);
    			  
    			if(schInfo != null){
    				
    				sUp_Position 	= StringHelper.evl(schInfo.getFieldString("CRANE_WORD_UP_LOC"), "");// SCH TO LOC
    			}
    			
    			sStackColGp   = (sUp_Position.length() >=  6)? sUp_Position.substring(0, 6):"";
    			sStackBedGp   = (sUp_Position.length() >=  8)? sUp_Position.substring(6, 8):"";
    			sStackLayerGp = (sUp_Position.length() >= 10)? sUp_Position.substring(8,10):"";
    			
    		}else{
    		
		    	sStackColGp		= StringHelper.evl(layerRc.getFieldString("STACK_COL_GP"), "");
		    	sStackBedGp		= StringHelper.evl(layerRc.getFieldString("STACK_BED_GP"), "");
		    	sStackLayerGp	= StringHelper.evl(layerRc.getFieldString("STACK_LAYER_GP"), "");
	    	
    		}
	    	
	    	logger.println(LogLevel.DEBUG,this, "============UP DB Position============");
			logger.println(LogLevel.DEBUG,this, "sStackColGp="	 + sStackColGp);
			logger.println(LogLevel.DEBUG,this, "sStackBedGp="	 + sStackBedGp);
			logger.println(LogLevel.DEBUG,this, "sStackLayerGp=" + sStackLayerGp);
			
			/**
			 * A열연일 경우.
			 * 대차 권상 실적작업일 경우에는 MAP상에 존재하는
			 * 위치를 가지고 실적처리한다.
			 * 전문상에 존재하는 위치정보는 무시한다.
			 * LCAR 전문내용으로는 번지정보를 가져오지 못한다.
			 * 실제 MAP을 찾아서 번지정보를 알아낸다.
			 */
			if(YmCommonConst.TC_1ATC03.equals(sStackColGp)||
			   YmCommonConst.TC_1BTC03.equals(sStackColGp)){
		    
		    	sUp_Position  = sStackColGp + 	
								sStackBedGp +
								sStackLayerGp;
				
				jtR.setField("Up_Position",sUp_Position);	
				logger.println(LogLevel.DEBUG,this, "============A열연 대차 Position============");
				logger.println(LogLevel.DEBUG,this, "sUp_Position="	 + sUp_Position);
			}
			
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
		    
		    	sUp_Position  = sStackColGp + 	
								sStackBedGp +
								sStackLayerGp;
				
				jtR.setField("Up_Position",sUp_Position);	
				logger.println(LogLevel.DEBUG,this, "============A열연 차량 Position============");
				logger.println(LogLevel.DEBUG,this, "sUp_Position="	 + sUp_Position);
			}
			
	    		/*
			 * Crane이 실제 UP한 전문 적치단 정보
			 */
			logger.println(LogLevel.DEBUG,this, "=최초 스케쥴 등록시점의 UP위치="+sStackColGp);
			logger.println(LogLevel.DEBUG,this, "=최초 스케쥴 등록시점의 UP위치="+sStackBedGp);
			logger.println(LogLevel.DEBUG,this, "=최초 스케쥴 등록시점의 UP위치="+sStackLayerGp);
			
			logger.println(LogLevel.DEBUG,this, "=Crane이 실제 UP한 전문 적치단 정보="+sUp_Position);
			if(sUp_Position.length() == 10){
	    		
				String sL2StackColGp   = sUp_Position.substring(0, 6);
				String sL2StackBedGp   = sUp_Position.substring(6, 8);
				String sL2StackLayerGp = sUp_Position.substring(8,10);
				
				/*
				 * 최초 스케쥴 등록시점의 UP위치와 
				 * 권상실적항목의 UP위치를 비교한다.
				 */
		    		if(!sStackColGp.equals(sL2StackColGp)||
			    	   !sStackBedGp.equals(sL2StackBedGp)||	
			    	   !sStackLayerGp.equals(sL2StackLayerGp)){
			    		
			    		logger.println(LogLevel.DEBUG,this, "권상에러..");
		    	   		logger.println(LogLevel.DEBUG,this, "DB UP위치와 LEVEL2 전문 UP위치 같지 않음.");   	
		    	   		logger.println(LogLevel.DEBUG,this, "DB UP위치를 우선으로 적용.");  
		    	   		
		    	   		sUp_Position  = sStackColGp + 	
										sStackBedGp +
										sStackLayerGp;
						
						jtR.setField("Up_Position",sUp_Position);		
			    	}
			}
			
	    	/*
			 * SCH_ID 에 해당하는 실적테이블 정보를 가져온다.
			 */	
	    	JDTORecord jrWrsltInfo = null;
	    	
	    	jrWrsltInfo = dao.getWrsltInfoWithSchId(sGrobalSchId); 
	    	
	    	if(jrWrsltInfo == null){
		    	/*
				 * Crane 작업실적을 등록한다.
				 */		
				iReq = dao.insertCraneWrslt(getUpRtData(jtR,sGrobalSchId,sFuncGbn));
			}else{
				/*
				 * Crane 작업실적을 수정한다.
				 */		
				iReq = dao.updateCraneUpWrslt(getUpRtData(jtR,sGrobalSchId,sFuncGbn));
			}
			
			if(YmCommonConst.TC_THCH560.equals(sTc)){//A열연 Coil 권상이상실적
				/*
				 *	권상이상실적 발생시에 
				 *	실적처리만 한다.
				 */
				logger.println(LogLevel.DEBUG,this, "권상이상실적..");
	    	   	logger.println(LogLevel.DEBUG,this, "실적처리 OK..."); 
				return isSuccess;
			}
			
			/*
			 * Crane 설비상태를 변경한다.
			 * tb_ym_equip Table : work_prog_stat = '2'(UP 실적)
			 */			
	    	iReq = dao.updateCraneEquipStatFromUp(sGrobalSchId,YmCommonConst.WORK_PROG_STAT_2);
	    	 
	    	/*
			 * Crane 작업상태를 변경한다.
			 * tb_ym_sch Table : sch_work_stat = '2'(UP 실적)
			 */			
	    	iReq = dao.updateCraneSchStat(sGrobalSchId,YmCommonConst.SCH_WORK_STAT_2);
	    	
	 	   /*
			* 콘베이어 OFF 권상처리 - 콘베이어 정보 삭제
			* A열연은 냉각장 적치 스케쥴 코드(CDLO)로 
		    * 콘베이어정보 삭제한다.
		    */
	    	if(YmCommonUtil.isLineOffWork(sSch_Code) ||
	    	   (
	    	    YmCommonConst.YD_GP_1.equals(sYard_Id)&&
	    	   	YmCommonConst.NEW_SCH_WORK_KIND_CDLO.equals(sSch_Code)&&
	    	    YmCommonConst.STACK_COL_GP_1BDC01.equals(sStackColGp)
			   )	
	    	  ){
				
				logger.println(LogLevel.DEBUG,this, "콘베이어 권상처리 : 적치단 삭제="+sSch_Code);
				logger.println(LogLevel.DEBUG,this, "콘베이어 권상처리 : 적치단 삭제="+sStackColGp);
				logger.println(LogLevel.DEBUG,this, "콘베이어 권상처리 : 적치단 삭제="+sStockId);

				int iSeq = YmCommonDB.deleteConveyorInfo(sStackColGp,
										  			 	 sStockId);
			    if(iSeq < 0){
					//throw new EJBServiceException("=권상실적=>CONVEYOR DELETE FAIL.");
					logger.println(LogLevel.DEBUG,this, "콘베이어 권상처리 : 적치단 삭제 FAIL");
				}	
			
			}else{ 
				
		    	/* 
				 * 적치단 UP위치 Clear
				 * tb_ym_stacklayer Table : stock_id = ''(Empty)
				 * tb_ym_stacklayer Table : stack_layer_stat	   = 'E'(적치가능)
				 */	
		    	iReq = dao.updateCraneStackLayerStat(sStackColGp,
		    										 sStackBedGp,
		    										 sStackLayerGp,
		    										 "",
		    										 YmCommonConst.STACK_LAYER_STAT_E);
		        
		    	if(YmCommonConst.STACK_LAYER_GP_01.equals(sStackLayerGp)){
			    	
			    	/*
			    	 * A.B열연 Coil 권상실적	
			    	 * 상단 왼쪽 상태정보를 UPDATE
			    	 * 상단 오른쪽 상태정보를 UPDATE
			    	 */	
			    	iReq = YmCommonDB.setCoilUpperState_V(sStackColGp,
				    							 	   	  sStackBedGp,
				    							 	   	  sStackLayerGp);
	    		}		
		    }    
			
			/*
			 * 크레인의 권상실적이 발생하면 저장품 적치단 정보를
			 * 크레인으로 할당한다.(예:3CCR06)
			 * tb_ym_stacklayer Table : stock_id = stock_id(저장품ID)
			 * tb_ym_stacklayer Table : stack_layer_stat	   = 'L'(적치중)
			 */	
	    	iReq = dao.updateCraneStackLayerStat(sYard_Id+sBay_Gp+
	    										 YmCommonConst.EQUIP_KIND_CR+sEquip_No,
	    										 YmCommonConst.STACK_BED_GP_01,
												 YmCommonConst.STACK_LAYER_GP_01,
	    										 sStockId,
	    										 YmCommonConst.STACK_LAYER_STAT_L);
	    	
	    	/*
			 * 설비 추출위치 코일 남아 있는 경우 초기화 작업 CHITO 2018.04.04
			 */
	    	iReq = dao.updateTrakingLayerReset(sStockId,sUp_Position);
	    	
			
			/*
			 * 저장품 이동조건 셋팅
			 */
			{
				String sSmt  = "";
					
				if(!"".equals(sSmt)){
					logger.println(LogLevel.DEBUG,this, "============ 권상 저장품 이동조건 셋팅============");
					logger.println(LogLevel.DEBUG,this, "현재는 정보 없슴");
				}
			}
			
			/*
			 * CTS 목적존 초기화
			 */
			if(YmCommonConst.NEW_SCH_WORK_KIND_CCMU.equals(sSch_Code) 
				|| YmCommonConst.NEW_SCH_WORK_KIND_CCMR.equals(sSch_Code)){
				logger.println(LogLevel.DEBUG,this, "============ CTS 목적존 초기화 ============");
				
				 ymCommonDAO dao2 = ymCommonDAO.getInstance();
				String sQueryId = "ym.facilitywork.upwrecord.updateStockZoneReset";
				iReq	 = dao2.updateData(sQueryId,new Object[]{sStockId});
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
	 *	2.	COIL INFO
        *		야드작업실적송신
        *		업무처리에 따른 실적을 조업,출하 등
        *		타업무에 내부인터페이스를 통해 전달한다.
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
		JDTORecord recInTemp                = null;
		YdDelegate      ydDelegate      = new YdDelegate();
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			/*
			 *	1.SPM, HFL 추출 및 Take-Out 시에 발생.
			 * 
			 */
			{
				String sSchCode = StringHelper.evl(jtR.getFieldString("Sch_Code"), "");
				String sStockId	= StringHelper.evl(jtR.getFieldString("Coil_No"), "").trim();
				String sYardGp	= StringHelper.evl(jtR.getFieldString("Yard_Id"), "");	
		 		String sVal 	= YmCommonUtil.getSPM_HFL_LineOffWork(sSchCode);
		 		
				if(!"".equals(sVal))
				{
					String sSubVal  = sVal.substring(1,2);
					
					if("3".equals(sSubVal)||
					   "4".equals(sSubVal)){
					   	
						
						
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
						
						/* 공정구분	CHAR(1)  H : Hot Filnal, S :  SkinPass	*/
						model.setprocGbn(sVal.substring(0,1));
						
						/* COIL번호	CHAR(11) */
						model.setcoilNo(sStockId);
						
						/* 처리구분     CHAR(1)  1:보급,2:보급취소,3:추출,4:Take-Out,5:Take-In */
						model.setProcessId(sSubVal);
						
						/* 위치포지션  CHAR(2)  */
						model.setpositionNo(YmCommonConst.PO_POSITION_D1);
						
						EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
						isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{CommonModel.class},
																	  	  	 new Object[]{model});
						
						logger.println(LogLevel.DEBUG,this, "내부IF호출===SPM, HFL 추출 및 Take-Out 시에 발생.===");
					}
				}else if(YmCommonConst.NEW_SCH_WORK_KIND_EQLO.equals(sSchCode)||
						YmCommonConst.NEW_SCH_WORK_KIND_EQTO.equals(sSchCode)){
					
						recInTemp = JDTORecordFactory.getInstance().create();
		    			recInTemp.setField("MSG_ID"       , "YDHRJ006");						//전문코드
		    			recInTemp.setField("STL_NO"       , sStockId);							//재료번호
		    			recInTemp.setField("TREAT_GP"     , "3");							//재료번호
		    			recInTemp.setField("YD_UP_CMPL_DT", YmCommonUtil.getTcDate("yyyyMMddHHmmss"));  //크레인스케줄ID
		    			ydDelegate.sendMsg(recInTemp);


						logger.println(LogLevel.DEBUG,this, "열연조업 L3 이퀄라이저 추출완료 실적 전송 송신 완료");
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
						
			if(YmCommonConst.TC_CN1PB05.equals(sTc)	){ //B열연 Coil 권상실적
			   
				logger.println(LogLevel.DEBUG,this, "==B열연 COIL MILL L2 외부IF 호출==");
				
				String sStockId         = StringHelper.evl(tcRc.getFieldString("Coil_No"), "").trim();
				String sSch_Code 		= StringHelper.evl(tcRc.getFieldString("Sch_Code"), "");
				
				/*
				 * B열연 L2로부터 분기콘베이어 Line Off 요구를 받아 Coil Line Off 완료시 호출
				 * Param : String Coil_No
				 */
				{
					//냉각장적치(CDLO) 스케쥴코드 체크
					if(YmCommonConst.NEW_SCH_WORK_KIND_CDLO.equals(sSch_Code)){
						
						EJBConnector ejbConn = new EJBConnector("default","JNDICCExtWrkOrdReg",this);
						isSuccess = (Boolean)ejbConn.trx("receiveConvLineOffResult",new  Class[]{String.class},
																					new Object[]{sStockId});
						logger.println(LogLevel.DEBUG,this, "외부IF호출===B열연 L2로부터 분기콘베이어 Line Off 요구를 받아 Coil Line Off 완료시 호출.===");
					}
				}
				
				/*
				 * B열연 L2로부터 Coil Take Out 요구를 받아 Coil Take Off 완료시 호출
				 * Param : String Coil_No
				 */
				{
					//TAKE OUT(CDTO) 스케쥴코드 체크
					if(YmCommonConst.NEW_SCH_WORK_KIND_CDTO.equals(sSch_Code)){
						
						EJBConnector ejbConn = new EJBConnector("default","JNDICCExtWrkOrdReg",this);
						isSuccess = (Boolean)ejbConn.trx("receiveConvTakeOutResult",new  Class[]{String.class},
																					new Object[]{sStockId});
						logger.println(LogLevel.DEBUG,this, "외부IF호출===B열연 L2로부터 Coil Take Out 요구를 받아 Coil Take Off 완료시 호출.===");
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
	 *	3.	COIL INFO
        *		권상작업실적 발생시 작업결과송신 Call
        * 		처리결과 메세지를 야드 Level-2에 송신한다.
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
			
			String sTc = StringHelper.evl(tcRc.getFieldString("TC"), "");
			/* 
			 * A열연 COIL 권상실적이 발생시
			 */
			if(YmCommonConst.TC_THCH550.equals(sTc)){
				
				String sLegacyCraneNo = StringHelper.evl(tcRc.getFieldString("LEGACY_CRANE_NO"), "");
	    	
	    		isSuccess = sendWorkFinishInfo_Coil(sLegacyCraneNo,sGbn);
	    		logger.println(LogLevel.DEBUG,this, "A열연 COIL 권상실적결과 송신 CALL======");
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
        *		대차 작업지시 Call
        *		권상시 대차에게 작업지시 전문을 Call
        *
        * param jDTORecord 	: 전문항목
        * param String		: Wbook_Id
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */              
	public boolean callTcWorkInfo_Coil(JDTORecord jtR){
		
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
			String sTc 			 = StringHelper.evl(jtR.getFieldString("TC"), "");
			String sUp_Position  = StringHelper.evl(jtR.getFieldString("Up_Position"), "");
			String sStockId 	 = StringHelper.evl(jtR.getFieldString("Coil_No"), "").trim();
			String sStackColGp   = (sUp_Position.length() >=  6)? sUp_Position.substring(0, 6):"";
		   	String sStackBedGp   = (sUp_Position.length() >=  8)? sUp_Position.substring(6, 8):"";
		   	String sStackLayerGp = (sUp_Position.length() >= 10)? sUp_Position.substring(8,10):"";
			/*
			 * 권상위치항목을 가지고 적치단,설비 TABLE을 JOIN해서
			 * TC NO, FROM LOC, TO LOC 항목을 가져온다.
			 * 조건으로 설비종류가 대차인 경우를 체크한다.	
			 */
			String sUsageCd = YmCommonUtil.getStackColInfoWithPk(sStackColGp);
			/*
			 * 권상위치가 대차일 경우에 TC에 전송할 
			 * 작업지시 전문을 만든다.
			 */
			if(YmCommonConst.STACK_COL_USAGE_CD_CX.equals(sUsageCd)){ // 대차정지위치
			   	
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
					
					sCurrQty	= "-1";
					 
					int iReq = dao.updateStackerQtyInfo(sStackColGp,
				    									sStackBedGp,
				    									sCurrQty);
				}
				
				/**
				 * 저장품TABLE의 이동설비항목에 
				 * 권하위치값을 삭제한다.
				 * tb_ym_stock Table frtomove_equip_gp : 		empty('')
				 * tb_ym_stock Table frtomove_equip_bed_gp : 	empty('')
				 * tb_ym_stock Table frtomove_equip_layer_gp : 	empty('')
				 */ 
				if(sUp_Position.length() >= 10)
				{
					int iReq = dao.updateStockMoveEquipInfo(sStockId,
				    								   		"",
				    								   		"",
				    								   		"",
				    								   		"",
				    								   		"");
				}
				
				/**
				 * 대차출발 EJB CALL
				 */
				if(!YmCommonConst.YD_GP_1.equals(sYard_Id)){
					
					String sMessage = StringHelper.evl(jtR.getFieldString("GROBAL_WBOOK_ID"), "                  ") + 
									  sStackColGp + sStackBedGp;					
					
					logger.println(LogLevel.DEBUG,this, "====TC 작업지시 CALL====");
					logger.println(LogLevel.DEBUG,this, "MESSAGE="	+ sMessage);
	    			
					EJBConnector ejbConn = new EJBConnector("default","JNDICTSStatusReg",this);
					isSuccess = (Boolean)ejbConn.trx("bcyVicCarMoveOrder",new Class[]{String.class},
																	  	  new Object[]{sMessage});
				}
			}else if(YmCommonConst.STACK_COL_USAGE_CD_TS.equals(sUsageCd)){ // CTS TO SADDLE
				
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
	    		 
	    			logger.println(LogLevel.DEBUG,this, i+"sSaddleName	="+ sSaddleName);
	    			logger.println(LogLevel.DEBUG,this, i+"sSaddleUseYn	="+ sSaddleUseYn);
	    			logger.println(LogLevel.DEBUG,this, i+"sSaddleUsage	="+ sSaddleUsage);
	    			logger.println(LogLevel.DEBUG,this, i+"sStockId	="+ sStockId);
	    			
	    			
	    			String sMessage = setCtsACoilMsgInfoUP(sSaddleName,sSaddleUseYn,sSaddleUsage,"0"+sStockId);
	    			
	    			EJBConnector ejbConnPut = new EJBConnector("default","JNDIYardWrkResReg",this);
	    			isSuccess = (Boolean)ejbConnPut.trx("THHT410send",new Class[]{String.class},new Object[]{ sMessage });	    			    			
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
        *		PUT 작업지시 Call
        * 
        * param jDTORecord : 전문항목
        * param String	 : Wbook_Id
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */           
	public boolean callCraneSchInfo_Coil(JDTORecord jtR){
		
		Boolean isSuccess = new Boolean(false);
		
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
			String sWbookId         = StringHelper.evl(jtR.getFieldString("GROBAL_WBOOK_ID"), "");
			
			if(YmCommonConst.TC_THCH550.equals(sTc)){//A열연 Coil 권상실적
				sTc	= YmCommonConst.TC_THCH520;
			}else if(YmCommonConst.TC_CN1PB05.equals(sTc)){  //B열연 Coil 권상실적
				sTc	= YmCommonConst.TC_CN1PB02;
			}
			
			logger.println(LogLevel.DEBUG,this, "sYard_Id="			+ sYard_Id);
			logger.println(LogLevel.DEBUG,this, "sBay_Gp="			+ sBay_Gp);
			logger.println(LogLevel.DEBUG,this, "sEquip_Kind="		+ sEquip_Kind);
			logger.println(LogLevel.DEBUG,this, "sEquip_No="		+ sEquip_No);
			logger.println(LogLevel.DEBUG,this, "sSch_Code="		+ sSch_Code);
			logger.println(LogLevel.DEBUG,this, "sTc="				+ sTc);
			logger.println(LogLevel.DEBUG,this, "sWbookId="			+ sWbookId);
			
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

	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue();
	    
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 *	6.	COIL INFO
	 *		아직 스케쥴에 등록되지 않은 작업예정 정보에 대해
	 *		스케쥴을 생성한다.
        *		A열연 냉각장 적치 작업지시 요구는 예외처리한다.
        *
        * param dao 			: DAO
        * param jDTORecord 	: 전문항목
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                
	public boolean callWbookInfo_Coil(JDTORecord jtR){
		
		Boolean isSuccess = new Boolean(false);
		YdStockDAO ydStockDAO = new YdStockDAO();
		String iDmSize ="";
		String sCAR_CARD_NO = "";
		String sCAR_MAXCNT = "";
		
		logger.println(LogLevel.DEBUG,this, "callWbookInfo_Coil("+jtR+") 시작==========");
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			 
			String sTc 		  = StringHelper.evl(jtR.getFieldString("TC"), "");
			String sSch_Code  = StringHelper.evl(jtR.getFieldString("Sch_Code"), "");
			String sYard_Id   = StringHelper.evl(jtR.getFieldString("Yard_Id"), "");
			String sBay_Gp	  = StringHelper.evl(jtR.getFieldString("Bay_GP"), "");
			
			/**
    		 * 스케쥴 등록 갯수를 체크한다.
    		 * 몇개 이상이면 스케쥴 등록을 못하게 한다.
    		 */
    		int iSchRuleCount = 1; 
    		String sSchCount  = "0"; 
    		JDTORecord wbookRc = null;
    		JDTORecord schRc  = dao.getCraneSchCountM(sYard_Id,
		    									     sBay_Gp,
		    									     sSch_Code);
    		if(schRc != null){
    			sSchCount = StringHelper.evl(schRc.getFieldString("COUNT"), "0");
	    	}
	    	
	    	if(Integer.parseInt(sSchCount) > iSchRuleCount){
	    		logger.println(LogLevel.DEBUG,this, "====권상실적시 스케쥴 등록 제한으로 FALSE====");
	    		return false;
	    	}
	    	/*
	    	 * 2007.07.04 확장 Conv Line Off Skip
	    	 * 권하 시 역순으로 Sch Call
	    	 */
	    	if(YmCommonConst.NEW_SCH_WORK_KIND_GVFL.equals(sSch_Code)|| //COIL 제품출하상차
			   YmCommonConst.NEW_SCH_WORK_KIND_GVF1.equals(sSch_Code)|| //Coil 제품출하상차
			   YmCommonConst.NEW_SCH_WORK_KIND_GVF2.equals(sSch_Code)|| //Coil 제품출하상차	
			   YmCommonConst.NEW_SCH_WORK_KIND_GTFL.equals(sSch_Code)|| // COIL 제품출하상차
	    	   YmCommonConst.NEW_SCH_WORK_KIND_GTF1.equals(sSch_Code)|| // Coil 제품출하상차
	    	   YmCommonConst.NEW_SCH_WORK_KIND_GTF2.equals(sSch_Code)||    		    
	    	   YmCommonConst.NEW_SCH_WORK_KIND_GPFL.equals(sSch_Code)|| // COIL 제품출하상차
	    	   YmCommonConst.NEW_SCH_WORK_KIND_GPF1.equals(sSch_Code)|| // Coil 제품출하상차
	    	   YmCommonConst.NEW_SCH_WORK_KIND_GPF2.equals(sSch_Code)||
			   YmCommonConst.NEW_SCH_WORK_KIND_CVML.equals(sSch_Code)|| //COIL 소재이송상차
			   YmCommonConst.NEW_SCH_WORK_KIND_CVM2.equals(sSch_Code)|| //COIL 소재이송상차
			   YmCommonConst.NEW_SCH_WORK_KIND_CVM3.equals(sSch_Code)|| //COIL 소재이송상차
			   YmCommonConst.NEW_SCH_WORK_KIND_GVML.equals(sSch_Code)|| //COIL 제품이송상차	
			   YmCommonConst.NEW_SCH_WORK_KIND_GVM2.equals(sSch_Code)|| //COIL 제품이송상차
			   YmCommonConst.NEW_SCH_WORK_KIND_GVM3.equals(sSch_Code)|| //COIL 제품이송상차
			   YmCommonConst.NEW_SCH_WORK_KIND_CELO.equals(sSch_Code)){ 		
			   	
			   	logger.println(LogLevel.DEBUG,this, "====권상실적시 차량 상차작업 스케쥴 제한FALSE====");
	    		return false;
			}   
			
			if(YmCommonConst.TC_THCH550.equals(sTc) 				&& //A열연 Coil 권상실적
			   YmCommonConst.BAY_GP_B.equals(sBay_Gp) 				&& //동구분(B)
			   YmCommonConst.NEW_SCH_WORK_KIND_CDLO.equals(sSch_Code)  //냉각장적치(CDLO)
			  ){
				
				logger.println(LogLevel.DEBUG,this, "====권상실적시 A열연 냉각장 적치 작업지시 CALL====");
				logger.println(LogLevel.DEBUG,this, "====sTc		="+sTc);
				logger.println(LogLevel.DEBUG,this, "====sBay_Gp	="+sBay_Gp);
				logger.println(LogLevel.DEBUG,this, "====sSch_Code	="+sSch_Code);
				
				JDTORecord crRc = dao.getLegacyEquipNoWithCurEquipNo(StringHelper.evl(jtR.getFieldString("Yard_Id"), "")	+
																	 StringHelper.evl(jtR.getFieldString("Bay_GP"), "")	+
																	 StringHelper.evl(jtR.getFieldString("Equip_Kind"), "")+
																	 StringHelper.evl(jtR.getFieldString("Equip_No"), ""));
	    		String sLegacyCraneNo = "";
	    		if(crRc != null){
	    			sLegacyCraneNo = StringHelper.evl(crRc.getFieldString("EQUIP_GP"), "");
	    		}
	    		
				JDTORecord dtoR = JDTORecordFactory.getInstance().create();
				dtoR.setField("전문코드"	, YmCommonConst.TC_THCH660);
				dtoR.setField("CRANENO"		, sLegacyCraneNo);
				dtoR.setField("발생일"		, YmCommonUtil.getCurDate("yyMMdd"));
				dtoR.setField("발생시"		, YmCommonUtil.getCurDate("HHmmss"));
				
				EJBConnector ejbConn = new EJBConnector("default","JNDIACCoolOrdReg",this);
				isSuccess = (Boolean)ejbConn.trx("receiveACCoolSchBackUp",new  Class[]{JDTORecord.class},
																  	  	  new Object[]{dtoR});
		 
				logger.println(LogLevel.DEBUG,this, "====권상실적시 A열연 냉각장 적치 작업지시 종료====");
				
			}else{
				
				logger.println(LogLevel.DEBUG,this, "====권상실적시 미등록 작업예정 스케쥴 생성 시작====");
				/**
	    		 * 스케쥴코드가 없으면 해당 크레인에
	    		 * 할당된 작업예정을 검색한다.
	    		 * 
	    		 * 2007.04.10 이정훈
	    		 * B열연 Coil 야드 대차출하상차 작업 시 
	    		 *  1. 대차 도착 후 대차 출하하기 위해 이적한 물량 편성
	    		 *  2. 대차 도착 전 이적 편성
	    		 */
				if (YmCommonConst.NEW_SCH_WORK_KIND_CTFL.equals(sSch_Code))// 대차출하상차
				{
					logger.println(LogLevel.DEBUG,this, "====대차 도착 후 대차 출하하기 위해 이적한 물량 편성====");
					JDTORecord TcRc = dao.getCranTcInfoCTFL(sYard_Id,
				    									  sBay_Gp,
				    									  sSch_Code);
					if(TcRc != null) // 대차 도착
					{
						logger.println(LogLevel.DEBUG,this, "====대차 도착 후 대차 출하하기 위해 이적한 물량 편성====");
						wbookRc = dao.getCraneWbookInfoCTFL_01(sYard_Id,
								  sBay_Gp,
								  sSch_Code);
						
						// 대차 도착  상차 편성 _지정 야드 이외 
						if(wbookRc == null)
						{   
							logger.println(LogLevel.DEBUG,this, "====대차 도착  상차 편성 _지정 야드 이외====");
							wbookRc = dao.getCraneWbookInfoCTFL_02(sYard_Id,
									  sBay_Gp,
									  sSch_Code);
						}
					}
					else // 대차 미도착
					{
						//B열연 대차상차 완료 후 보조작업 편성 막는 작업 (임경빈 주임 요청: 2013.05.10)
						if("3".equals(sYard_Id)){
							logger.println(LogLevel.DEBUG,this, "====대차 도착 전 스케줄 생성 skip====");
							wbookRc = null;
						}else{
							// 대차 도착 전 16span 부처 search 
							logger.println(LogLevel.DEBUG,this, "====대차 도착 전 16span 부처 search====");
							wbookRc = dao.getCraneWbookInfoCTFL_03(sYard_Id,
									  sBay_Gp,
									  sSch_Code);
						}
					}
					
					
				}else
				{
					logger.println(LogLevel.DEBUG,this, " 작업예약 검색("+sYard_Id+", "+sBay_Gp+", "+sSch_Code+") 호출");
					String sUpDown ="";
					if(YmCommonConst.NEW_SCH_WORK_KIND_CVM8.equals(sSch_Code)|| // COIL 소재차량이송상차(R)
					   YmCommonConst.NEW_SCH_WORK_KIND_CVM9.equals(sSch_Code)|| // COIL 소재차량이송하차(R)	
					   YmCommonConst.NEW_SCH_WORK_KIND_GVM8.equals(sSch_Code)|| // COIL 제품차량이송상차(R)
					   YmCommonConst.NEW_SCH_WORK_KIND_GVM9.equals(sSch_Code)|| // COIL 제품차량이송하차(R)	
					   YmCommonConst.NEW_SCH_WORK_KIND_CVM6.equals(sSch_Code)|| // COIL 소재차량이송상차(L)
					   YmCommonConst.NEW_SCH_WORK_KIND_CVM7.equals(sSch_Code)|| // COIL 소재차량이송하차(L)	
					   YmCommonConst.NEW_SCH_WORK_KIND_GVM6.equals(sSch_Code)|| // COIL 제품차량이송상차(L)
					   YmCommonConst.NEW_SCH_WORK_KIND_GVM7.equals(sSch_Code)){ // COIL 제품차량이송하차(L)
					
						if(YmCommonConst.NEW_SCH_WORK_KIND_CVM8.equals(sSch_Code)||YmCommonConst.NEW_SCH_WORK_KIND_CVM6.equals(sSch_Code)){ // COIL 소재차량이송상차
						   	sUpDown = "U";
						}else if(YmCommonConst.NEW_SCH_WORK_KIND_CVM9.equals(sSch_Code)||YmCommonConst.NEW_SCH_WORK_KIND_CVM7.equals(sSch_Code)){// COIL 소재차량이송하차
							sUpDown = "D";
						}else if(YmCommonConst.NEW_SCH_WORK_KIND_GVM8.equals(sSch_Code)||YmCommonConst.NEW_SCH_WORK_KIND_GVM6.equals(sSch_Code)){// COIL 제품차량이송상차	
							sUpDown = "U";
						}else if(YmCommonConst.NEW_SCH_WORK_KIND_GVM9.equals(sSch_Code)||YmCommonConst.NEW_SCH_WORK_KIND_GVM7.equals(sSch_Code)){// COIL 제품차량이송하차			
							sUpDown = "D";
						}
						
						String sStockId	= StringHelper.evl(jtR.getFieldString("Coil_No"), "").trim();

					
						String sQueryId_EmptyBay = "ym.facilitystatus.facilityinquiry.dao.YdStockDAO.getCardNo";
						List listCoilPos = dao.getListData(sQueryId_EmptyBay, new Object[] {sStockId});
					 
						logger.println(LogLevel.DEBUG, this, "listCoilPos.size()"+ listCoilPos.size());
						JDTORecord jtrCoilPos = null;
						if (listCoilPos.size() > 0) {
							for(int j=0;j<listCoilPos.size();j++)
							{
								jtrCoilPos = (JDTORecord)listCoilPos.get(j);
								sCAR_CARD_NO = StringHelper.evl(jtrCoilPos.getFieldString("CAR_CARD_NO"),"");
								sCAR_MAXCNT  = StringHelper.evl(jtrCoilPos.getFieldString("CARUNLOAD_YD"),"");					
							}
						}
						
						
						//	이전 잘못 처리된 상차 처리 내역 CLEAR작업 진행
						String stkQueryId = "ym.steelinfo.steelinforecv.dao.YdStockDAO.getWBookSchState";
						int stockSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[] {sCAR_CARD_NO });
						 
						
						List 	   dmList  = dao.getYmDmCommonInfo5(sCAR_CARD_NO ,sStockId,sUpDown);

						JDTORecord dmRc    = (JDTORecord)dmList.get(0);
						iDmSize = StringHelper.evl(dmRc.getFieldString("CNT"), "");					
					

						logger.println(LogLevel.DEBUG, this, sCAR_CARD_NO+ "번호 차량이송상차 완료 매수:"+ iDmSize );
						if(!iDmSize.equals(sCAR_MAXCNT)){ //상차가능매수
						    //해당 차량의 작업 예약을 가져 온다
							wbookRc = dao.getCraneWbookInfoCarNo(sYard_Id,
																  sBay_Gp,
																  sSch_Code,
																  sCAR_CARD_NO);
						}else{
							//해당 차량이 아닌 다른 상차 작업을 가져 온다. 하차작업 스케줄이 없는 작업 예약을 가져 온다.
							wbookRc = dao.getCraneWbookInfoCarNo2(sYard_Id,
																  sBay_Gp,
																  sSch_Code,
																  sCAR_CARD_NO);
						} 
					}else{
						//2013.10.08 다음 작업예약 기동 시 b열연 같은 경우 해당 동에서 우선순위가 빠른 순으로 스케줄 기동
						if("3".equals(sYard_Id)){ 
							wbookRc = dao.getCraneWbookInfo_14(sYard_Id, sBay_Gp, sSch_Code);
						}else{
							wbookRc = dao.getCraneWbookInfo_02(sYard_Id, sBay_Gp, sSch_Code);
						}
					}
				}
				
				
				
	    		if(wbookRc != null){
	    			/**
		    		 * 작업예정이 존재하면
		    		 * 해당 작업예정에 대해 스케쥴을 호출한다.
		    		 */
		    		String sWbookID = StringHelper.evl(wbookRc.getFieldString("WBOOK_ID"), "");
	    				
		    		logger.println(LogLevel.DEBUG,this, "callWbookInfo_Coil() WBOOK_ID: "+sWbookID);
		    		
					EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
					isSuccess = (Boolean)ejbConn.trx("callCraneSchInfo",new  Class[]{String.class},
																		new Object[]{sWbookID});
				}
				logger.println(LogLevel.DEBUG,this, "====권상실적시 미등록 작업예정 스케쥴 생성 종료====");
			}
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue();
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 *	1.	SLAB INFO
        *		1,2매 작업 Crane Up 실적에 관련된 상태정보를 등록 및 수정한다.
        * 
        * param jDTORecord 	: 전문항목
        * param String 		: 실적처리방법
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                     
	public boolean setCraneUpRtInfo_Slab(JDTORecord jtR,
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
			String sUp_Position 	= StringHelper.evl(jtR.getFieldString("Up_Position"), "");
			String sUp_X_Position 	= StringHelper.evl(jtR.getFieldString("Up_X_Position"), "");
			String sUp_Y_Position 	= StringHelper.evl(jtR.getFieldString("Up_Y_Position"), "");

			logger.println(LogLevel.DEBUG,this, "sTc="				+ sTc);
			logger.println(LogLevel.DEBUG,this, "sYard_Id="			+ sYard_Id);
			logger.println(LogLevel.DEBUG,this, "sBay_Gp="			+ sBay_Gp);
			logger.println(LogLevel.DEBUG,this, "sEquip_Kind="		+ sEquip_Kind);
			logger.println(LogLevel.DEBUG,this, "sEquip_No="		+ sEquip_No);
			logger.println(LogLevel.DEBUG,this, "sStockId="			+ sStockId+"==");
			logger.println(LogLevel.DEBUG,this, "sSch_Code="		+ sSch_Code);
			logger.println(LogLevel.DEBUG,this, "sWork_Id="			+ sWork_Id);
			logger.println(LogLevel.DEBUG,this, "sUp_Position="		+ sUp_Position);
			logger.println(LogLevel.DEBUG,this, "sUp_X_Position="	+ sUp_X_Position);
			logger.println(LogLevel.DEBUG,this, "sUp_Y_Position="	+ sUp_Y_Position);
			
			if("".equals(sUp_Position)){
				logger.println(LogLevel.DEBUG,this, "권상에러..");
	    	   	logger.println(LogLevel.DEBUG,this, "권상위치정보 존재안함");
	    	   	throw new EJBServiceException("=권상실적=> 권상위치정보 존재안함.");
			}
			
			/*
			 * Crane UP실적 저장품의 적치단정보를 가져온다.
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
	    		logger.println(LogLevel.DEBUG,this, "권상에러..");
	    	   	logger.println(LogLevel.DEBUG,this, "스케쥴ID를 가져올 수 없슴.");
    			throw new EJBServiceException("=권상실적=>스케쥴ID 존재 안함");
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
	    		logger.println(LogLevel.DEBUG,this, "권상에러..");
	    	   	logger.println(LogLevel.DEBUG,this, "크레인정보를 가져올 수 없슴.");
    			throw new EJBServiceException("=권상실적=>크레인정보 존재 안함");
    		}
    		
	    	String sTWprogStat = StringHelper.evl(craneV.getFieldString("WPROG_STAT"), "");
	    	String sTWbookId   = StringHelper.evl(craneV.getFieldString("WBOOK_ID"), "");
	    	
	    	logger.println(LogLevel.DEBUG,this, "sTWprogStat="	+ sTWprogStat);
	    	logger.println(LogLevel.DEBUG,this, "sTWbookId="	+ sTWbookId);
	    	
	    	/*
			 * 전문을 통해 받은 스케쥴ID와 해당 크레인에 셋팅된 스케쥴ID가 같은지를 체크한다.
			 * 같은경우에만 권상처리를 한다.
			 * - 현재 작업진행상태가 '1'(UP지시)
			 * - 스케쥴ID가 동일한 경우
			 */	
	    	if(!YmCommonConst.WORK_PROG_STAT_1.equals(sTWprogStat)||
	    	   !sGrobalSchId.equals(sTWbookId)){
	    	   	
	    	   	logger.println(LogLevel.DEBUG,this, "권상에러..");
	    	   	logger.println(LogLevel.DEBUG,this, "스케쥴ID나 작업진행상태에러..");
	    	   	throw new EJBServiceException("=권상실적=>스케쥴ID나 작업진행상태에러");
	    	}
	    	
	    	/*
			 * Crane 설비상태를 변경한다.
			 * tb_ym_equip Table : work_prog_stat = '2'(UP 실적)
			 */			
	    	iReq = dao.updateCraneEquipStatFromUp(sGrobalSchId,YmCommonConst.WORK_PROG_STAT_2);
	    	
	    	/*
			 * 2매 작업 대상 정보 셋팅
			 */		
			String sInnerStockId 	= "";
	    	String sInnerSchId 		= "";
	    	String sInnerUpPositoin = "";
	    	String sInnerCraneLayerGp = "";
	    	 
	    	String sDSchId 			= "";
	    	String sDWbookId   		= "";
	    	String sDStockId 		= "";
	    	String sDCraneWordUpLoc = "";
	    	String sDCraneWordPutLoc= "";
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
		    	jtR.setField("GROBAL_STOCK_ID",sStockId);	
		    	jtR.setField("GROBAL_LOCATION",sUp_Position);	
		    	
	    	   	logger.println(LogLevel.DEBUG,this, "===2매 작업 대상 정보===.");
	    	   	logger.println(LogLevel.DEBUG,this, "===iGripCount			="+iGripCount);
	    	   	logger.println(LogLevel.DEBUG,this, "===sDSchId				="+sDSchId);
	    	   	logger.println(LogLevel.DEBUG,this, "===sDWbookId			="+sDWbookId);
	    	   	logger.println(LogLevel.DEBUG,this, "===sDStockId			="+sDStockId);
	    	   	logger.println(LogLevel.DEBUG,this, "===sDCraneWordUpLoc	="+sDCraneWordUpLoc);
	    	   	logger.println(LogLevel.DEBUG,this, "===sDCraneWordPutLoc	="+sDCraneWordPutLoc);
	   	
	    	   	
    		}
	    	
	    	
	    	
	    	//보온뱅크적치유무
	    	stockV = dao.getSlabGripInfo_03(sYard_Id,
										    sBay_Gp,
										    sEquip_Kind,
										    sEquip_No,
										    sStockId,
										    sSch_Code
											    );
			if(stockV != null){ 
			sDCraneWordUpLoc = StringHelper.evl(stockV.getFieldString("CRANE_WORD_UP_LOC"), "");
			sDCraneWordPutLoc= StringHelper.evl(stockV.getFieldString("CRANE_WORD_PUT_LOC"), "");
			
			/**
			*	메소드 전역변수 셋팅
			*/	 
			logger.println(LogLevel.DEBUG,this, "===sDCraneWordUpLoc	="+sDCraneWordUpLoc);
			logger.println(LogLevel.DEBUG,this, "===sDCraneWordPutLoc	="+sDCraneWordPutLoc);
			
			
			
			//보온뱅크적치유무
			String sUpLocEqp = "";
			String sPutLocEqp= "";
			
			sUpLocEqp =sDCraneWordUpLoc.substring(2 , 4);
			sPutLocEqp =sDCraneWordPutLoc.substring(2 , 4);
			
			if("BK".equals(sUpLocEqp) && !"BK".equals(sPutLocEqp) ){
			//주편(보온뱅크추출시간)
			String queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateMslabCommonSubEndInfo";	
			Object[] params = {sStockId};	
			iReq = dao.updateData(queryCode,params); 
			}	
			
			}
    		
	    	for(int inx = 1; inx <= iGripCount; inx++){

	    		if(inx == 1){
	    			
		    		sInnerStockId 		= sStockId;
		    		sInnerSchId 		= sGrobalSchId;
		    		sInnerUpPositoin 	= sUp_Position;
		    		sInnerCraneLayerGp	= YmCommonConst.STACK_BED_GP_01;
		    	}else if(inx == 2){
			    	
			    	sInnerStockId		= sDStockId;
		    		sInnerSchId 		= sDSchId;
		    		sInnerUpPositoin 	= sDCraneWordUpLoc;
		    		sInnerCraneLayerGp	= YmCommonConst.STACK_BED_GP_02;
		    		jtR.setField("Up_Position", sDCraneWordUpLoc);
		    	}
		    	
		    	/*
				 * Crane 작업상태를 변경한다.
				 * tb_ym_sch Table : sch_work_stat = '2'(UP 실적)
				 */			
		    	iReq = dao.updateCraneSchStat(sInnerSchId,YmCommonConst.SCH_WORK_STAT_2);
	    	
		    	/*
				 * Crane 작업실적을 등록한다.
				 */		
				iReq = dao.insertCraneWrslt(getUpRtData(jtR,sInnerSchId,sFuncGbn));
			
				/*
				 * Crane UP실적 저장품의 적치단정보를 가져온다.
				 * SADDLE인 경우 입측은 stack_layer_active_stat = 'C' 이다.
				 * 따라서 위 조건은 제외시킨다.
				 * tb_ym_stacklayer Table stack_layer_active_stat : 'O'(Open)
				 * tb_ym_stacklayer Table stack_layer_stat 	   	  : 'U'(Up Schedule 수행예정)
				 */			
				
				JDTORecord layerRc = null;
		    	 
		    	layerRc = dao.getUpStackLayerListWithSchId(sInnerSchId);
		    	
		    	if(layerRc == null){
		    		logger.println(LogLevel.DEBUG,this, "권상에러..");
		    	   	logger.println(LogLevel.DEBUG,this, "적치단정보를 가져올 수 없슴.");
	    			//throw new EJBServiceException("=권상실적=>적치단정보 존재 안함.");
	    			layerRc = createFromLoc(sInnerSchId);
	    		}
	    		
		    	String sStackColGp		= StringHelper.evl(layerRc.getFieldString("STACK_COL_GP"), "");
		    	String sStackBedGp		= StringHelper.evl(layerRc.getFieldString("STACK_BED_GP"), "");
		    	String sStackLayerGp	= StringHelper.evl(layerRc.getFieldString("STACK_LAYER_GP"), "");
		    	
		    	logger.println(LogLevel.DEBUG,this, "============UP DB Position============");
				logger.println(LogLevel.DEBUG,this, "sStackColGp="	 + sStackColGp);
				logger.println(LogLevel.DEBUG,this, "sStackBedGp="	 + sStackBedGp);
				logger.println(LogLevel.DEBUG,this, "sStackLayerGp=" + sStackLayerGp);
				
		    	/*
				 * Crane이 실제 UP한 전문 적치단 정보
				 */
				if(sUp_Position.length() == 10){
		    		
					String sL2StackColGp   = sInnerUpPositoin.substring(0, 6);
					String sL2StackBedGp   = sInnerUpPositoin.substring(6, 8);
					String sL2StackLayerGp = sInnerUpPositoin.substring(8,10);
					
					/*
					 * 최초 스케쥴 등록시점의 UP위치와 권상실적항목의 UP위치를 비교한다.
					 */
		    		if(!sStackColGp.equals(sL2StackColGp)||
			    	   !sStackBedGp.equals(sL2StackBedGp)||	
			    	   !sStackLayerGp.equals(sL2StackLayerGp)){
			    		
			    		logger.println(LogLevel.DEBUG,this, "권상에러..");
		    	   		logger.println(LogLevel.DEBUG,this, "DB UP위치와 LEVEL2 전문 UP위치 같지 않음.");   	
		    	   		logger.println(LogLevel.DEBUG,this, "DB UP위치를 우선으로 적용.");  
		    	   		
		    	   		sUp_Position  = sStackColGp + 	
										sStackBedGp +
										sStackLayerGp;
						
						jtR.setField("Up_Position", sUp_Position);		
			    	}
				}
				
		 	   /*
				* 콘베이어 OFF 권상처리 - 콘베이어 정보 삭제
				* if(YmCommonUtil.isLineOffWork(sSch_Code)){
				*/
		    	String sUpUsageCd 		= YmCommonUtil.getStackColInfoWithPk(sStackColGp);
		   		
		   		if(YmCommonConst.STACK_COL_USAGE_CD_XX.equals(sUpUsageCd)	||// SLAB 비상적치위치
			       YmCommonConst.STACK_COL_USAGE_CD_SE.equals(sUpUsageCd)	||// SLAB Scafing 입측
				   YmCommonConst.STACK_COL_USAGE_CD_SD.equals(sUpUsageCd)	){// SLAB Scafing 출측
				   		
					logger.println(LogLevel.DEBUG,this, "콘베이어 권상처리 : 적치단 삭제="+sSch_Code);
					logger.println(LogLevel.DEBUG,this, "콘베이어 권상처리 : 적치단 삭제="+sStackColGp);
					logger.println(LogLevel.DEBUG,this, "콘베이어 권상처리 : 적치단 삭제="+sInnerStockId);
	
					int iSeq = YmCommonDB.deleteConveyorInfo(sStackColGp,
										  			 		 sInnerStockId);
					if(iSeq < 0){
						//throw new EJBServiceException("CONVEYOR DELETE ERROR");
						logger.println(LogLevel.DEBUG,this, "콘베이어 권상처리 : 적치단 삭제 FAIL");
					}	
					
					logger.println(LogLevel.DEBUG,this, "콘베이어 권상처리 : 적치단 삭제="+sSch_Code);
				}else{
					
					//2010.03.10 hyuksang ( 같은동 차량하차/상차 진행 시 하차중인 차량으로 상차스케쥴 생성안되도록)
					if(	YmCommonConst.STACK_COL_USAGE_CD_TX.equals(sUpUsageCd)||		// 차량정지위치
					   YmCommonConst.STACK_COL_USAGE_CD_PX.equals(sUpUsageCd))// 팔레트정지위치	
					{
						logger.println(LogLevel.DEBUG,this, "권상위치가 차량이면  : 적치단 close=");
						/* 
						 * 적치단 UP위치 Clear
						 * tb_ym_stacklayer Table : stock_id = ''(Empty)
						 * tb_ym_stacklayer Table : STACK_LAYER_ACTIVE_STAT = 'C'
						 * tb_ym_stacklayer Table : stack_layer_stat	   = 'E'(적치가능)
						 */							
						iReq = dao.updateCraneStackLayerStat1(sStackColGp,
								 sStackBedGp,
								 sStackLayerGp,
								 "",
								 "C",
								 YmCommonConst.STACK_LAYER_STAT_E);

                                 /*
                                * B열연 Slab 권상실적	// A열연 Slab 권상실적 같이 사용(MCH)
                                * 바로 위 상단 상태정보를 UPDATE
                                */
                                  iReq = YmCommonDB.setSlabUpperState_V(sStackColGp,
							 	  sStackBedGp,
							 	  sStackLayerGp);
						
						
					}
					else
					{
												
				    	/* 
						 * 적치단 UP위치 Clear
						 * tb_ym_stacklayer Table : stock_id = ''(Empty)
						 * tb_ym_stacklayer Table : stack_layer_stat	   = 'E'(적치가능)
						 */	
				    	iReq = dao.updateCraneStackLayerStat(sStackColGp,
				    										 sStackBedGp,
				    										 sStackLayerGp,
				    										 "",
				    										 YmCommonConst.STACK_LAYER_STAT_E);
				    	
			    		/*
				    	 * B열연 Slab 권상실적	// A열연 Slab 권상실적 같이 사용(MCH)
				    	 * 바로 위 상단 상태정보를 UPDATE
				    	 */
				    	iReq = YmCommonDB.setSlabUpperState_V(sStackColGp,
						    							 	  sStackBedGp,
						    							 	  sStackLayerGp);
						
						
					}
									
				}    
			
				/*
				 * 크레인의 권상실적이 발생하면 저장품 적치단 정보를
				 * 크레인으로 할당한다.(예:3CCR06)
				 * tb_ym_stacklayer Table : stock_id = stock_id(저장품ID)
				 * tb_ym_stacklayer Table : stack_layer_stat	   = 'L'(적치중)
				 */	
		    	iReq = dao.updateCraneStackLayerStat(sYard_Id+sBay_Gp+
		    										 YmCommonConst.EQUIP_KIND_CR+sEquip_No,
		    										 YmCommonConst.STACK_BED_GP_01,
		    										 sInnerCraneLayerGp,
													 sInnerStockId,
		    										 YmCommonConst.STACK_LAYER_STAT_L);
			
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
					
					sCurrQty	= "-1";
					 
					iReq = dao.updateStackerQtyInfo(sStackColGp,
			    									sStackBedGp,
			    									sCurrQty);
						
					JDTORecord slabRc = dao.getSlabCommonInfo(sStockId);
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
				 * 장입보급 순서 초기화
				 */
				
				{
					iReq = setStockLotNo_Slab(sInnerSchId,
											  sInnerStockId,
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
	 *	2.	SLAB INFO
        *		야드작업실적송신
        *		업무처리에 따른 실적을 조업,출하 등
        *		타업무에 내부인터페이스를 통해 전달한다.
        * 
        * param jDTORecord 	: 전문항목
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
			isSuccess = Boolean.TRUE;
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue();
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 *	3.	SLAB INFO
        *		권상작업실적 발생시 작업결과송신 Call
        *		처리결과 메세지를 야드 Level-2에 송신한다.
        * 
        * param jDTORecord 	: 전문항목
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
        *		대차 작업지시 Call
        *		권상시 대차에게 작업지시 전문을 Call
        * 
        * param jDTORecord 	: 전문항목
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
			String sUp_Position  = StringHelper.evl(jtR.getFieldString("Up_Position"), "");
			String sStockId      = StringHelper.evl(jtR.getFieldString("Slab_No"), "").trim();
			String sStackColGp   = (sUp_Position.length() >=  6)? sUp_Position.substring(0, 6):"";
		   	String sStackBedGp   = (sUp_Position.length() >=  8)? sUp_Position.substring(6, 8):"";
		   	String sStackLayerGp = (sUp_Position.length() >= 10)? sUp_Position.substring(8,10):"";
			
			String sGwbookId     = StringHelper.evl(jtR.getFieldString("GROBAL_WBOOK_ID"), "");
			String sGstockId     = StringHelper.evl(jtR.getFieldString("GROBAL_STOCK_ID"), "");
			/*
			 * 권상위치항목을 가지고 적치단,설비 TABLE을 JOIN해서
			 * TC NO, FROM LOC, TO LOC 항목을 가져온다.
			 * 조건으로 설비종류가 대차인 경우를 체크한다.	
			 */
			String sUsageCd = YmCommonUtil.getStackColInfoWithPk(sStackColGp);
			/*
			 * 권상위치가 대차일 경우에 TC에 전송할 
			 * 작업지시 전문을 만든다.
			 */
			if(YmCommonConst.STACK_COL_USAGE_CD_CX.equals(sUsageCd)){ // 대차정지위치
				
    			logger.println(LogLevel.DEBUG,this, "====TC 작업지시========");
    			logger.println(LogLevel.DEBUG,this, "sStackColGp="	+ sStackColGp);
    			logger.println(LogLevel.DEBUG,this, "sStackBedGp="	+ sStackBedGp);
    			logger.println(LogLevel.DEBUG,this, "sStackLayerGp="+ sStackLayerGp);
    			logger.println(LogLevel.DEBUG,this, "sStockId="		+ sStockId);
				
				/**
				 * 저장품TABLE의 이동설비항목에 
				 * 권하위치값을 삭제한다.
				 * tb_ym_stock Table frtomove_equip_gp : 		empty('')
				 * tb_ym_stock Table frtomove_equip_bed_gp : 	empty('')
				 * tb_ym_stock Table frtomove_equip_layer_gp : 	empty('')
				 */ 
				if(sUp_Position.length() >= 10)
				{
					int iReq = -1;
					
			    	iReq = dao.updateSlabMoveEquipInfo_01(sStockId,
				    								   	  "",
				    								   	  "",
				    								   	  "");
			    	
			    	{ 
						String sCurrQty	= "0";
						
						sCurrQty	= "-1";
						 
						iReq = dao.updateStackerQtyInfo(sStackColGp,
				    									sStackBedGp,
				    									sCurrQty);
							
					}							   		
								    								   		
				}
				
				/**
				 * SLAB GRIP 대상재 처리
				 * 2매 권상작업시 상단에 SLAB 정보를 처리한다.
				 */ 
				if(!"".equals(sGstockId)){
					
					int iReq = -1;
					
			    	iReq = dao.updateSlabMoveEquipInfo_01(sGstockId,
				    								   	  "",
				    								   	  "",
				    								   	  "");
					{ 
						String sCurrQty	= "0";
						
						sCurrQty	= "-1";
						 
						iReq = dao.updateStackerQtyInfo(sStackColGp,
				    									sStackBedGp,
				    									sCurrQty);
							
					}			
				}
				
				
				/**
				 * 대차출발 EJB CALL
				 */
				String sMessage = sGwbookId + sStackColGp + sStackBedGp;					
				
				logger.println(LogLevel.DEBUG,this, "====TC 작업지시 CALL====");
				logger.println(LogLevel.DEBUG,this, "MESSAGE="	+ sMessage);
    			
				EJBConnector ejbConn = new EJBConnector("default","JNDICTSStatusReg",this);
				isSuccess = (Boolean)ejbConn.trx("bsyVicCarMoveOrder",new Class[]{String.class},
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
	 *		아직 스케쥴에 등록되지 않은 작업예정 정보에 대해
	 *		스케쥴을 생성한다.
        * 
        * param jDTORecord 	: 전문항목
        *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                
	public boolean callWbookInfo_Slab(JDTORecord jtR){
		
		Boolean isSuccess = new Boolean(false);
    
		try{
			 
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String sTc 		  = StringHelper.evl(jtR.getFieldString("TC"), "");
			String sSch_Code  = StringHelper.evl(jtR.getFieldString("Sch_Code"), "");
			String sYard_Id   = StringHelper.evl(jtR.getFieldString("Yard_Id"), "");
			String sBay_Gp	  = StringHelper.evl(jtR.getFieldString("Bay_GP"), "");
			
			logger.println(LogLevel.DEBUG,this, "====권상실적시 미등록 작업예정 스케쥴 생성 시작====");
			
			if(YmCommonConst.NEW_SCH_WORK_KIND_SSLI.equals(sSch_Code)){ //Slab Scarfing 보급
				logger.println(LogLevel.DEBUG,this, "====스카핑보급 권상실적시 미등록 작업예정 스케쥴 생성 하지 않음====");
				return false;
			}
			
			if(YmCommonConst.NEW_SCH_WORK_KIND_STML.equals(sSch_Code)||
			   YmCommonConst.NEW_SCH_WORK_KIND_STM2.equals(sSch_Code)){ //Slab 동간이적상차 	
				logger.println(LogLevel.DEBUG,this, "====동간이적상차 권상실적시 미등록 작업예정 스케쥴 생성 하지 않음====");
				return false;
			}
			
			if(YmCommonConst.NEW_SCH_WORK_KIND_STSL.equals(sSch_Code)){ //Slab 동간보급상차
				logger.println(LogLevel.DEBUG,this, "====동간보급상차 권상실적시 미등록 작업예정 스케쥴 생성 하지 않음====");
				return false;
			}
			
			if(YmCommonConst.NEW_SCH_WORK_KIND_STMU.equals(sSch_Code)|| //Slab 대차하차(1)
			   YmCommonConst.NEW_SCH_WORK_KIND_STM4.equals(sSch_Code)){ //Slab 대차하차(2)
				logger.println(LogLevel.DEBUG,this, "====대차하차 권상실적시 미등록 작업예정 스케쥴 생성 하지 않음====");
				return false;
			}
			
			if(YmCommonConst.NEW_SCH_WORK_KIND_SYMM.equals(sSch_Code)|| //Slab 동내이적
			   YmCommonConst.NEW_SCH_WORK_KIND_SYM2.equals(sSch_Code)){ //Slab 동내이적	 
				
				/**
	    		 * 스케쥴 등록 갯수를 체크한다.
	    		 * 몇개 이상이면 스케쥴 등록을 못하게 한다.
	    		 */
	    		int iSchRuleCount = 4; 
	    		String sSchCount  = "0"; 
	    		JDTORecord schRc  = dao.getCraneSchCount(sYard_Id,
			    									     sBay_Gp,
			    									     sSch_Code);
	    		if(schRc != null){
	    			sSchCount = StringHelper.evl(schRc.getFieldString("COUNT"), "0");
		    	}
		    	
		    	if(Integer.parseInt(sSchCount) > iSchRuleCount){
		    		logger.println(LogLevel.DEBUG,this, "====권상실적시 스케쥴 등록 제한으로 FALSE====");
		    		return false;
		    	}
		    	
				List stockL     = dao.getWbookListData(sYard_Id,
		    									  	   sBay_Gp,
		    									  	   sSch_Code);
	    		
	    		int iMaxCount		= 3;
	    		String sSchIdList 	= "";
	    		int MaxRec = iMaxCount > stockL.size() ?  stockL.size() : iMaxCount;	
	    		for (int ii = 0; ii < MaxRec; ii++){
					JDTORecord infoV = (JDTORecord)stockL.get(ii);
					sSchIdList += StringHelper.evl(infoV.getFieldString("WBOOK_ID"), "") +"-";						
				}
				
				if(!"".equals(sSchIdList)){
					
					EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
					isSuccess = (Boolean)ejbConn.trx("syCraneScheduleInfoInsert",new  Class[]{String.class},
																				 new Object[]{sSchIdList});	
				}
			}else{
				/**
	    		 * 스케쥴코드가 없으면 해당 크레인에
	    		 * 할당된 작업예정을 검색한다.
	    		 */
	    		JDTORecord wbookRc = dao.getCraneWbookInfo_02(sYard_Id,sBay_Gp,sSch_Code);
	    		
	    		
	    		
	    		if(wbookRc != null){
	    			
	    			
	    			//WB 2매작업 가능 여부 체크
	    			if(sSch_Code.equals("SWLI")){
		    			String sTStockId = StringHelper.evl(wbookRc.getFieldString("TSTOCKID"), "");
		    			String sGStockId = StringHelper.evl(wbookRc.getFieldString("GSTOCKID"), "");  //WB 1번지 슬라브 존재여부
		    			String sCHARGE_LOT_YN = StringHelper.evl(wbookRc.getFieldString("CHARGE_LOT_YN"), "");
		    			
		    			//WB 장입LOT 순번이 역전된 경우 
		    			if(sCHARGE_LOT_YN.equals("N")){
		    				logger.println(LogLevel.DEBUG,this, "====권상실적시 장입lot순번이 변경된 경우 작업예정 스케쥴 생성 종료====");
		    				return true; 
		    			}
		    			
		    			//WB 1번지 1단에 슬라브가 존재 하는 경우
		    			if(!sGStockId.equals("")){
			    			EJBConnector ejbConn = new EJBConnector("default","JNDICUpResReg",this);
							isSuccess = (Boolean)ejbConn.trx("setCraneWBBSlabGrapChk",new  Class[]{String.class,String.class},
																						 new Object[]{sTStockId,sGStockId});
							
							//WB 2단 작업 금지
			    			if (isSuccess.booleanValue() == false){
			    				logger.println(LogLevel.DEBUG,this, "====권상실적시 WB 미등록 작업예정 스케쥴 생성 종료====");
			    				return true; 
			    			}
		    			}
	    			
	    			}
	    			
	    			/**
		    		 * 작업예정이 존재하면
		    		 * 해당 작업예정에 대해 스케쥴을 호출한다.
		    		 */
		    		String sWbookID = StringHelper.evl(wbookRc.getFieldString("WBOOK_ID"), "");
	    				
		    		logger.println(LogLevel.DEBUG,this, "callWbookInfo_Slab() WBOOK_ID: "+sWbookID);
					EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
					isSuccess = (Boolean)ejbConn.trx("syCraneScheduleInfoInsert",new  Class[]{String.class},
																				 new Object[]{sWbookID});
				}
			}
			
			logger.println(LogLevel.DEBUG,this, "====권상실적시 미등록 작업예정 스케쥴 생성 종료====");
		
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue();
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 *	5.	SLAB INFO
        *		PUT 작업지시 Call
        * 
        * param jDTORecord 	: 전문항목
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
			String sTc				= YmCommonConst.TC_CM1PB02;
			String sYard_Id 		= StringHelper.evl(jtR.getFieldString("Yard_Id"), "");
			String sBay_Gp 			= StringHelper.evl(jtR.getFieldString("Bay_GP"), "");
			String sEquip_Kind 		= StringHelper.evl(jtR.getFieldString("Equip_Kind"), "");
			String sEquip_No 		= StringHelper.evl(jtR.getFieldString("Equip_No"), "");
			String sSch_Code 		= StringHelper.evl(jtR.getFieldString("Sch_Code"), "");
			String sWbookId         = StringHelper.evl(jtR.getFieldString("GROBAL_WBOOK_ID"), "");
			
			logger.println(LogLevel.DEBUG,this, "sYard_Id="			+ sYard_Id);
			logger.println(LogLevel.DEBUG,this, "sBay_Gp="			+ sBay_Gp);
			logger.println(LogLevel.DEBUG,this, "sEquip_Kind="		+ sEquip_Kind);
			logger.println(LogLevel.DEBUG,this, "sEquip_No="		+ sEquip_No);
			logger.println(LogLevel.DEBUG,this, "sSch_Code="		+ sSch_Code);
			logger.println(LogLevel.DEBUG,this, "sTc="				+ sTc);
			logger.println(LogLevel.DEBUG,this, "sWbookId="			+ sWbookId);
			  
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

	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue();
	    
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 *	5.	SLAB INFO(A열연 SLAB 야드(MCH))
        *		PUT 작업지시 Call
        * 
        * param jDTORecord 	: 전문항목
        *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                          
	public boolean callCraneSchInfo_ASlab(JDTORecord jtR){
		
		Boolean isSuccess = new Boolean(false);
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			String sTc				= YmCommonConst.TC_HM1PB02;
			String sYard_Id 		= StringHelper.evl(jtR.getFieldString("Yard_Id"), "");
			String sBay_Gp 			= StringHelper.evl(jtR.getFieldString("Bay_GP"), "");
			String sEquip_Kind 		= StringHelper.evl(jtR.getFieldString("Equip_Kind"), "");
			String sEquip_No 		= StringHelper.evl(jtR.getFieldString("Equip_No"), "");
			String sSch_Code 		= StringHelper.evl(jtR.getFieldString("Sch_Code"), "");
			String sWbookId         = StringHelper.evl(jtR.getFieldString("GROBAL_WBOOK_ID"), "");
			
			logger.println(LogLevel.DEBUG,this, "sYard_Id="			+ sYard_Id);
			logger.println(LogLevel.DEBUG,this, "sBay_Gp="			+ sBay_Gp);
			logger.println(LogLevel.DEBUG,this, "sEquip_Kind="		+ sEquip_Kind);
			logger.println(LogLevel.DEBUG,this, "sEquip_No="		+ sEquip_No);
			logger.println(LogLevel.DEBUG,this, "sSch_Code="		+ sSch_Code);
			logger.println(LogLevel.DEBUG,this, "sTc="				+ sTc);
			logger.println(LogLevel.DEBUG,this, "sWbookId="			+ sWbookId);
			  
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

	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue();
	    
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 *	5.	SLAB INFO
        *		크레인 지원작업 Call
        *		-	C동 W/B 보급 권상시 추가 W/B 보급 SLAB에 대해
        *			지원크레인이 동시에 작업할 수 있도록 처리한다.
        * 
        * param jDTORecord 	: 전문항목
        *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                             
	public boolean callCraneSupportInfo_Slab(JDTORecord jtR){
		
		Boolean isSuccess = new Boolean(false);
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			String sTc				= YmCommonConst.TC_CM1PB02;
			String sYard_Id 		= StringHelper.evl(jtR.getFieldString("Yard_Id"), "");
			String sBay_Gp 			= StringHelper.evl(jtR.getFieldString("Bay_GP"), "");
			String sEquip_Kind 		= StringHelper.evl(jtR.getFieldString("Equip_Kind"), "");
			String sEquip_No 		= StringHelper.evl(jtR.getFieldString("Equip_No"), "");
			String sSch_Code 		= StringHelper.evl(jtR.getFieldString("Sch_Code"), "");
			String sStockId         = StringHelper.evl(jtR.getFieldString("Slab_No"), "").trim();
			String sWbookId         = "";
			String sSchId         	= "";
			String sMCrNo			= "";
			
			logger.println(LogLevel.DEBUG,this, "sYard_Id="			+ sYard_Id);
			logger.println(LogLevel.DEBUG,this, "sBay_Gp="			+ sBay_Gp);
			logger.println(LogLevel.DEBUG,this, "sEquip_Kind="		+ sEquip_Kind);
			logger.println(LogLevel.DEBUG,this, "sEquip_No="		+ sEquip_No);
			logger.println(LogLevel.DEBUG,this, "sSch_Code="		+ sSch_Code);
			logger.println(LogLevel.DEBUG,this, "sTc="				+ sTc);
			logger.println(LogLevel.DEBUG,this, "sWbookId="			+ sWbookId);
			
			/**
			 *	1.	지원작업 스케쥴코드 인지를 체크
			 */
			{
				if(!YmCommonConst.NEW_SCH_WORK_KIND_SWLI.equals(sSch_Code)){ 
					
					logger.println(LogLevel.DEBUG,this, "=SLAB 지원작업 => 지원작업 스케쥴 코드가 아님.");
			    	return false;
				}
			}
			/**
			 *	2.	지원작업 스케쥴이 있는지를 체크
			 *		-	바로 아래의 SLAB 정보에 대한 체크는 하지 않고,
			 *			야드MAP 전체에 대해서 검색한다.
			 */
			JDTORecord schRc  = null; 
			{	
				schRc  = dao.getCraneWbookInfo_03(sYard_Id,
							   				      sBay_Gp,
							   				      sEquip_No,
							   				      sSch_Code);
	    		if(schRc == null){
		    		logger.println(LogLevel.DEBUG,this, "=SLAB 지원작업 => 지원작업 스케쥴 정보가 존재 안함.");
		    		return false;
		    	}
	    	}
	    	/**
			 *	3.	지원작업 가능 크레인이 있는지를 체크
			 *		-	지원작업 가능 크레인이 운전모드 상태를 체크
			 *		-	지원작업 가능 크레인이 설비		상태를 체크
			 *		-	지원작업 가능 크레인이 IDLE 상태인지를 체크
			 */
			{ 
				//주 크레인 정보
				JDTORecord mainRc  = dao.getEquipInfoWithEquipGp(sYard_Id	+
											   				     sBay_Gp	+
											   				     sEquip_Kind+
											   				     sEquip_No);
											   				   
			    if(mainRc != null){
			    	sMCrNo = StringHelper.evl(mainRc.getFieldString("BACKUP_EQUIP_NO"), "");
			    }		
			    
			    //지원 크레인 정보
				String sWorkMode  = "";
				String sEquipStat = "";
				String sWprogStat = "";
				
				JDTORecord subRc  = dao.getEquipInfoWithEquipGp(sYard_Id	+
											   				    sBay_Gp		+
											   				    sEquip_Kind +
											   				    sMCrNo);
											   				   
			    if(subRc != null){
			    	sWorkMode  = StringHelper.evl(subRc.getFieldString("WORK_MODE"),"");
					sEquipStat = StringHelper.evl(subRc.getFieldString("EQUIP_STAT"),"");
					sWprogStat = StringHelper.evl(subRc.getFieldString("WPROG_STAT"),"");
					
					if(YmCommonConst.WORK_MODE_C.equals(sWorkMode)){
						logger.println(LogLevel.DEBUG,this, "=SLAB 지원작업 => 지원작업 크레인 작업코드 고장.");
		    			return false;
					}
					
					if(YmCommonConst.WORK_MODE_C.equals(sEquipStat)){
						logger.println(LogLevel.DEBUG,this, "=SLAB 지원작업 => 지원작업 크레인 설비상태 고장.");
		    			return false;
					}
					
					if(!YmCommonConst.WPROG_STAT_W.equals(sWprogStat)){
						logger.println(LogLevel.DEBUG,this, "=SLAB 지원작업 => 지원작업 크레인 IDLE 상태 아님.");
		    			return false;
					}
			    }									     
			}	
			
			/**
			 *	4.	지원작업 크레인 GRIP 대상정보 수정
			 *		-	지원작업대상 스케쥴의 저장품정보를 가지고 
			 *			GRIP 대상이 있는지를 체크한다.
			 */ 
			{
				
				String sTStockId = StringHelper.evl(schRc.getFieldString("STOCK_ID"),"");
				
				JDTORecord gripJr = dao.getSlabGripInfo_01(sYard_Id,
								    					   sBay_Gp,
								    					   sEquip_Kind,
								    					   sEquip_No,
								    					   sTStockId,
								    					   sSch_Code);
							    					 
		    	if(gripJr != null){
		    		
		    		String sGSchId 	= StringHelper.evl(gripJr.getFieldString("SCH_ID"),"");
		    		int iReq 		= dao.updateCraneNoWithSchId(sGSchId,
			    									   		 	 sMCrNo); 
		    		logger.println(LogLevel.DEBUG,this, "=SLAB 지원작업 => 지원작업 크레인 GRIP 대상정보 셋팅.");
		    	}
			}
			    	
			/**
			 *	5.	지원작업 크레인으로 스케쥴 정보 수정
			 */ 
			{
				sSchId  	= StringHelper.evl(schRc.getFieldString("SCH_ID"),"");
				sWbookId  	= StringHelper.evl(schRc.getFieldString("WBOOK_ID"),"");
				
				int iReq 	= dao.updateCraneNoWithSchId(sSchId,
			    									   	 sMCrNo); 
				
				logger.println(LogLevel.DEBUG,this, "=SLAB 지원작업 => 지원작업 크레인 작업요구 정보 셋팅 성공.");
			}
			
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
															   				 sMCrNo,
															   				 sSch_Code,
															   				 sWbookId});

	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue();
	    
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 *	5.	SLAB INFO
	 *		C동 크레인 01단 권상시 대차하차 작업예약 스케쥴 Call
        * 
        * param jDTORecord 	: 전문항목
        *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     	
	public boolean callCraneSvmuInfo_Slab(JDTORecord jtR){
		
		Boolean isSuccess = new Boolean(false);
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			String sYard_Id 		= StringHelper.evl(jtR.getFieldString("Yard_Id"), "");
			String sBay_Gp 		= StringHelper.evl(jtR.getFieldString("Bay_GP"), "");
			String sEquip_Kind 		= StringHelper.evl(jtR.getFieldString("Equip_Kind"), "");
			String sEquip_No 		= StringHelper.evl(jtR.getFieldString("Equip_No"), "");
			String sSch_Code 		= StringHelper.evl(jtR.getFieldString("Sch_Code"), "");
			String sStockId         	= StringHelper.evl(jtR.getFieldString("Slab_No"), "").trim();
			String sUp_Position 	= StringHelper.evl(jtR.getFieldString("Up_Position"), "");
			
			String sSvmuStackColGp   	= "";
			String sSvmuStackBedGp   	= "";
			String sSvmuStackLayerGp 	= "";
			
			logger.println(LogLevel.DEBUG,this, "sYard_Id="			+ sYard_Id);
			logger.println(LogLevel.DEBUG,this, "sBay_Gp="			+ sBay_Gp);
			logger.println(LogLevel.DEBUG,this, "sEquip_Kind="		+ sEquip_Kind);
			logger.println(LogLevel.DEBUG,this, "sEquip_No="		+ sEquip_No);
			logger.println(LogLevel.DEBUG,this, "sSch_Code="		+ sSch_Code);
			logger.println(LogLevel.DEBUG,this, "sStockId="			+ sStockId);
			
			if(sUp_Position.length() == 10){
				
				sSvmuStackColGp    	= sUp_Position.substring(0, 6);
				sSvmuStackBedGp   	= sUp_Position.substring(6, 8);
				sSvmuStackLayerGp 	= sUp_Position.substring(8,10);
			}
			
			if(sSvmuStackColGp.indexOf("2C0") == -1){
				
				logger.println(LogLevel.DEBUG,this, "=SLAB 대차하차 이벤트 처리 => C동 야드 권상작업 아님.");
				return false;
			}
			
			/*
			 * GRIP 으로 실적처리를 하는지 체크한다.
			 */
			JDTORecord stockV = null;
			
			stockV = dao.getSlabGripInfo_02(sYard_Id,
									    sBay_Gp,
									    sEquip_Kind,
									    sEquip_No,
									    sStockId,
									    sSch_Code
									    );
			
			
			if(YmCommonConst.STACK_LAYER_GP_01.equals(sSvmuStackLayerGp) ||
			   (
			   YmCommonConst.STACK_LAYER_GP_02.equals(sSvmuStackLayerGp) &&
			   stockV != null
			   )
			  ){
			}else{
				logger.println(LogLevel.DEBUG,this, "=SLAB 대차하차 이벤트 처리 => C동 야드 01단 1매작업/ 02단 GRIP 작업 아님.");
				return false;
			}
			
			/*
			*	1.	C동 대차하차 스케쥴 등록안된 작업예약 정보를 검색한다.
			*		작업예약 Table에는 스케쥴코드가 SWLI
			*/
			String sQuery0 	= "ym.steelinfo.steelinforecv.YdStockDAO.selectWBSlabSearch_05";
			List wbL0  		= dao.getListData(sQuery0, new Object[]{YmCommonConst.YD_GP_2,
															   YmCommonConst.BAY_GP_C, 
															   YmCommonConst.NEW_SCH_WORK_KIND_SWLI});
			/*
			*	2.	해당 작업예약 정보를 스케쥴 Call한다.
			*/
			JDTORecord wbR0 		= null;
			String Glo_Sch_Call		= "";
			
			for (int index = 0; index < wbL0.size(); index++){
					wbR0		= (JDTORecord) wbL0.get(index);
					Glo_Sch_Call  += StringHelper.evl(wbR0.getFieldString("WBOOK_ID"), "") +"-" ;							
			}
			
			if (!Glo_Sch_Call.equals("")){
				
				EJBConnector ejbConn 	= new EJBConnector("default","JNDICraneSchReg",this);
				Boolean isTrue 		= (Boolean)ejbConn.trx("syCraneScheduleInfoInsert",
															new  Class[]{String.class},
															new Object[]{Glo_Sch_Call });	
			}											
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue();
	    
	}
	
	/**
     *  SLAB 권상시에 장입보급 순서를 초기화한다.
     *
     * @param sSchId 		: SCH_ID
     * @param sStockId 		: 저장품ID
     * @param sSch_Code 	: 스케쥴 코드
     * @param sWork_Id 		: 주작업/보조작업 구분
     *
     * 권상/권하위치가 차량일 경우에  
	 * 저장품 이동조건을 셋팅한다.
	 *
	 * @return int
     * @throws 
     */	 
	private int setStockLotNo_Slab(String sSchId,
								   String sStockId,
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
			logger.println(LogLevel.DEBUG,this, "=====권상 장입보급 순서 초기화 시작=====");
			
			if(YmCommonConst.MAIN_WORK_M.equals(sWork_Id)){
				
				if(YmCommonConst.NEW_SCH_WORK_KIND_SWLI.equals(sSch_Code)||	// Slab W/B 보급
				   YmCommonConst.NEW_SCH_WORK_KIND_SCLI.equals(sSch_Code)){	// Slab CTC 보급	 
					
					JDTORecord schInfo  = dao.getCraneSchInfo(sSchId);
					
					String sPutLoc 		= StringHelper.evl(schInfo.getFieldString("CRANE_WORD_PUT_LOC"),"");
					
					String sPutUsageCd  = YmCommonUtil.getStackColInfoWithPk(sPutLoc.substring(0, 6));
			   		
			   		//SLAB W/B, CTC 일 경우만.
			   		if(YmCommonConst.STACK_COL_USAGE_CD_WB.equals(sPutUsageCd)	||// W/B	
			   		   YmCommonConst.STACK_COL_USAGE_CD_CT.equals(sPutUsageCd)	){// CTC
			   		
						JDTORecord stockJR = dao.getStockInfo(sStockId);
								
						if(stockJR != null){
							//현재 W/B보급되는 SLAB의 장입LOT 번호
							String sCurLotNo = StringHelper.evl(stockJR.getFieldString("CHARGE_LOT_NO"),"0");
							int iCurLotNo 	 = Integer.parseInt(sCurLotNo);
							
							int Seq = dao.updateStockSaddleInfo(sStockId,iCurLotNo+"");
						}
						
						//저장품 TABLE CHARGE_LOT_NO 항목 CLEAR
						iReq = dao.updateStockLotNoWithStockId(sStockId,
															   "");			
						
						/*
						 * 2009.07 YJK 일관제철 생산통제 장입진행실적 추가모듈.
						 */
						Boolean isSuccess = new Boolean(false);
						
						JDTORecord model = null;
						model = JDTORecordFactory.getInstance().create(); 
						model.setField("JMS_TC_CD", 		"YDCTJ032");
						model.setField("JMS_TC_CREATE_DDTT",new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));	
						model.setField("PTOP_PLNT_GP", 		"HB");
						model.setField("STL_APPEAR_GP", 	"C");
						model.setField("CHG_SUP_PROG_STAT", YmCommonConst.TC_YMPC020);
						model.setField("WR_OCCR_DT", 		new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
						model.setField("YD_EQP_WR_CNT", 	"1");
						model.setField("STL_NO1", 			sStockId);

						EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
						isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new  Class[]{JDTORecord.class},
						  	  	 											 new Object[]{model});
					}									   
				}
			}
			logger.println(LogLevel.DEBUG,this, "=====권상 장입보급 순서 초기화 종료=====");
		}catch(Exception e){}
		return iReq;
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
	private List getUpRtData(JDTORecord rtRc,
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
		
		String sYard_Id 				= ""; //야드구분
		String scrane_work_duty			= ""; //크레인작업근
		String scrane_work_party		= ""; //크레인작업조
		String scrane_wrslt_up_x_axis	= ""; //크레인작업결과up X축
		String scrane_wrslt_up_y_axis	= ""; //크레인작업결과up Y축
		String scrane_wrslt_up_z_axis	= ""; //크레인작업결과up Z축
		String scrane_wrslt_up_loc		= ""; //크레인작업결과up 위치     
		String scrane_wrslt_up_func		= ""; //크레인작업결과up 기능
		String scrane_wrslt_up_ddtt		= ""; //크레인작업결과up 일시
		String sregister				= ""; //register,
		
		scrane_work_duty		= YmCommonUtil.getWorkDuty();
		scrane_work_party		= YmCommonUtil.getWorkParty();				
		scrane_wrslt_up_x_axis	= StringHelper.evl(rtRc.getFieldString("Up_X_Position"), "");
		scrane_wrslt_up_y_axis	= StringHelper.evl(rtRc.getFieldString("Up_Y_Position"), "");
		scrane_wrslt_up_z_axis	= "0"; 
		scrane_wrslt_up_loc	= StringHelper.evl(rtRc.getFieldString("Up_Position"), "");
		scrane_wrslt_up_func	= getWorkWrsltFunc(rtRc,sFuncGbn); 
		scrane_wrslt_up_ddtt    	= YmCommonUtil.getCurDate("yyyyMMddHHmmss");
		sYard_Id 				= StringHelper.evl(rtRc.getFieldString("Yard_Id"), "");
		sregister				= StringHelper.evl(rtRc.getFieldString("USER_ID"), "SYSTEM"); 
		
		param.add(scrane_work_duty);
		param.add(scrane_work_party); 
		param.add(scrane_wrslt_up_x_axis);
		param.add(scrane_wrslt_up_y_axis);
		param.add(scrane_wrslt_up_z_axis);   
		param.add(scrane_wrslt_up_loc);      
		param.add(scrane_wrslt_up_func);	
		param.add(scrane_wrslt_up_ddtt);  
		param.add(sregister);    			
		param.add(sYard_Id);    	
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
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			//VALUE SETTING
			TC			= sTc; 
			CNO			= sLegacyCraneNo;
			SPARE1		= ""; 
			sDATE		= YmCommonUtil.getCurDate("yyMMdd");
			sTIME		= YmCommonUtil.getCurDate("HHmmss");
			GBN			= YmCommonConst.TC_THCH550; 
			YN			= "0";  //sGbn; 2011.09.22 에러가 나는 경우라도 0으로 정상 응답전문 송신 
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
	 * 오퍼레이션명 : 
	 *
	 *	권상시 FROM위치정보가 없을 경우 임시 FROM 위치 생성한다.
        * 
        * param jDTORecord 	: 전문항목
        *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     	     
	public JDTORecord createFromLoc(String sSchId){
		
		JDTORecord jRecord = null;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			logger.println(LogLevel.DEBUG,this, "=====권상 처리시 FROM위치 FAIL LOGIC 시작=====");
			
			JDTORecord schInfo = dao.getSchInfoWithSchId(sSchId);
			
			String sUp_Position = "";
			String sStackColGp = "";
			String sStackBedGp = "";
			String sStackLayerGp = "";
			
			if(schInfo != null){
				
				sUp_Position 	= StringHelper.evl(schInfo.getFieldString("CRANE_WORD_UP_LOC"), "");// SCH TO LOC
			}
			
			sStackColGp   = (sUp_Position.length() >=  6)? sUp_Position.substring(0, 6):"";
		   	sStackBedGp   = (sUp_Position.length() >=  8)? sUp_Position.substring(6, 8):"";
		   	sStackLayerGp = (sUp_Position.length() >= 10)? sUp_Position.substring(8,10):"";
		   	
		    jRecord = dao.getStackLayerInfoWithPk(sStackColGp,
												  sStackBedGp,
												  sStackLayerGp);
			
		 	logger.println(LogLevel.DEBUG,this, "=====권상 처리시 FROM위치 FAIL LOGIC 종료=====");
		 	
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return jRecord;
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * HYSCO 권상,권하 실적을 발생시킨다.
        *
        *	J/C 작업실적	MHMI710	(HYSCO → 현대제철)		
	 *	
	 *	1	전문코드		전문코드		CHAR	7	7	Inter-face T/C Code
	 *	2	발생일자		발생일자		CHAR	14	21	전문발생시간(yyyymmddhhmmss)
	 *	3	작업근조		작업근조		CHAR	2	23	작업근조
	 *	4	작업공장		작업공장		CHAR	1	24	작업공장( 현대제철 : 'I', HYSCO : 'B')
	 *	5	작업공정		작업공정		CHAR	1	25	작업공정( 현대제철 : 'I', HYSCO : 'H')
	 *	6	전문count		전문count		CHAR	1	26	전문 발생 count(전문이 3개 생성시 첫번째 1, 두번째 2, 마지막 전문일경우 *)
	 *	7	전문요구구분	전문요구구분	CHAR	1	27	작업공정( 현대제철 : 'I', HYSCO : 'H')
	 *	8	여분항목		여분항목		CHAR	13	40	여분항목(space)
	 *	9	설비코드		설비코드		CHAR	4	44	작업설비 Code
	 *	10	작업구분		작업구분		CHAR	1	45	작업구분( '1':권상, '2':권하 )
	 *	11	COILNO			COILNO			CHAR	15	60	공급사 coil 번호
	 *	12	작업위치		작업위치		CHAR	10	70	작업 위치( 권상시 권상위치, 권하시 권하위치)
        *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     	          
	public boolean callHyscoUpPutRtInfo(String sMessage){
		boolean isSuccess = false;
		
		try{
	    		Level2Parser level2Parser = new Level2Parser();
			JDTORecord jtR = level2Parser.parse(sMessage);
			
			
			JDTORecord rRd = JDTORecordFactory.getInstance().create();
			
			rRd.setField("COUNT"		, StringHelper.evl(jtR.getFieldString("전문count"), ""));
			rRd.setField("WORK_GBN"	, StringHelper.evl(jtR.getFieldString("작업구분"), ""));
			rRd.setField("COILNO"		, StringHelper.evl(jtR.getFieldString("COILNO"), ""));
			rRd.setField("POSITION"		, StringHelper.evl(jtR.getFieldString("작업위치"), ""));

			String sBCOIL_EFF_YN = "N";
			JDTORecord jrChk = ymComm.getNewModuleEffYn_YM();
			sBCOIL_EFF_YN = jrChk.getFieldString("BCOIL_EFF_YN");
			if(sBCOIL_EFF_YN.equals("Y")) {
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setField("MSG_ID"	, "MHMI710");  
				jrYdMsg.setField("WORK_GP"	, StringHelper.evl(jtR.getFieldString("작업구분"), "")); 
				jrYdMsg.setField("COILNO"	, StringHelper.evl(jtR.getFieldString("COILNO"), ""));
				jrYdMsg.setField("EQUIP_CD"	, "JCX1");
				
				//B열연 신규모듈 호출
				EJBConnector ydEjbCon = new EJBConnector("default", this);
			    ydEjbCon.trx("YmCommEJB", "rcvInterface", jrYdMsg);
				return true;
				
			} 
		
			isSuccess = callHyscoUpPutRtInfo(rRd);
			
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
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     	          
	public boolean callHyscoUpPutRtInfo(JDTORecord jtR){
		boolean isSuccess = false;
		Boolean isSuccess2 = new Boolean(false);
		
		int iSeq = 0;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String sCount		= StringHelper.evl(jtR.getFieldString("COUNT"), "");
			String sWorkGbn 	= StringHelper.evl(jtR.getFieldString("WORK_GBN"), "");
			String sStockId	= StringHelper.evl(jtR.getFieldString("COILNO"), "").trim();
			String sLocation	= StringHelper.evl(jtR.getFieldString("POSITION"), "");
			
			logger.println(LogLevel.DEBUG,this, "sCount="		+ sCount);
			logger.println(LogLevel.DEBUG,this, "sWorkGbn="		+ sWorkGbn);
			logger.println(LogLevel.DEBUG,this, "sStockId="		+ sStockId+"==");
			logger.println(LogLevel.DEBUG,this, "sLocation="		+ sLocation);
	   		
			/**
			 * 2007.1.2 Hysco 권상 실적으로 정보 처리 
			 * 
			 */
			
	   		/**
	   		 *	2.	권하 실적처리
	   		 */
	   		if("2".equals(sWorkGbn)){
	   			
	   			logger.println(LogLevel.DEBUG,this, "HYSCO실적=>권하실적 처리");	
					 
	   		/**
	   		 *	1.	권상 실적처리
	   		 */	
	   		}else if("1".equals(sWorkGbn)){
	   			
	   			String sStackColGp	= "";
		    		String sStackBedGp	= "";
		    		String sStackLayerGp= "";
		    	
	   			JDTORecord layerJr = dao.getStackLayerInfoWithStockId_02(sStockId);
	   			
	   			logger.println(LogLevel.DEBUG,this, "layerJr="	+ layerJr);
	   			/**
		   		 *	1.1	권상위치 CLEAR
		   		 */
		   		if(layerJr != null){
		   			
			   		sStackColGp  = StringHelper.evl(layerJr.getFieldString("STACK_COL_GP"), "");
				    	sStackBedGp	 = StringHelper.evl(layerJr.getFieldString("STACK_BED_GP"), "");
				    	sStackLayerGp= StringHelper.evl(layerJr.getFieldString("STACK_LAYER_GP"), "");
			    	
			   		iSeq = dao.updateCraneStackLayerStat(sStackColGp,
			    										 sStackBedGp,
			    										 sStackLayerGp,
			    										 "",
			    										 YmCommonConst.STACK_LAYER_STAT_E);
			    }	
			    
			    /*
				 *	1.3	출하
				 *		HYSCO 대차 이송 실적 발생시 
				 */		
				{
				/*	YMDM012 model = new YMDM012();
					model.setTcCode(YmCommonConst.MODEL_YMDM012);
					model.setTcDate(YmCommonUtil.getCurDate("yyyy-MM-dd"));
					model.setTcTime(YmCommonUtil.getCurDate("HH-mm-ss"));
					
					//이송일자
					model.setTRANS_DATE(YmCommonUtil.getCurDate("yyyyMMdd"));
					
					// 이송시각
					model.setTRANS_TIME(YmCommonUtil.getCurDate("HHmmss"));
					
					// 제품번호 
					model.setGOODS_NO(sStockId);
					
					// FROM저장위치CODE 
					model.setFROM_STORE_LOC_CD(sStackColGp+
											   sStackBedGp+
											   sStackLayerGp);
					
					EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
					Boolean isHysco = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{CommonModel.class},
																  	  	      new Object[]{model});
					logger.println(LogLevel.DEBUG,this, "내부IF호출===HYSCO 대차 이송 실적 발생시.===");	
				*/	
					
					//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                    //HYSCO대차이송실적
         			JDTORecord tcRecordDM = null;
         			tcRecordDM = JDTORecordFactory.getInstance().create(); 
         			tcRecordDM.setField("GOODS_NO", jtR.getFieldString("COILNO")); 
                    tcRecordDM.setField("FROM_STORE_LOC_CD",sStackColGp+sStackBedGp+sStackLayerGp);
         			
         			//인터페이스 전문 호출
         			EJBConnector ejbConn1 = new EJBConnector("default","JNDIYardWrkResReg",this);
         			isSuccess2 = (Boolean)ejbConn1.trx("getYDDMR024",new Class[]{JDTORecord.class},
         			  	  	 new Object[]{tcRecordDM}); 
                    logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 HYSCO대차이송실적.===");
                    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				}
				
	   			/**
		   		 *	1.4	출하 완료 처리
		   		 */
		   		{
					iSeq = (new YdStockDAO()).updateStockDelYnInfo(sStockId,"Y"); 
			   		
			   		/**
					 * 		저장품TABLE의 이동설비항목에 
					 * 		권하위치값을 삭제한다.
					 *		tb_ym_stock Table frtomove_equip_gp : 		empty('')
					 * 		tb_ym_stock Table frtomove_equip_bed_gp : 	empty('')
					 * 		tb_ym_stock Table frtomove_equip_layer_gp : empty('')
					 */    
					iSeq = dao.updateStockMoveEquipInfo(sStockId,
				    								   		  "",
				    								   		  "",
				    								   		  "",
				    								   		  "",
				    								   		  "");
				    								   		  
			   		isSuccess = callMapInfo_Coil();
			   	}
			   										 
		   		/**
		   		 *	1.5	대차 출발 CALL
		   		 */ 
		   		{
			   		JDTORecord jRecord = JDTORecordFactory.getInstance().create();
			
				        jRecord.setField("Yard_Id"		,YmCommonConst.YD_GP_3);
				        jRecord.setField("Up_Position"	,sStackColGp+
				        							 sStackBedGp+
				        							 sStackLayerGp);
				        jRecord.setField("Coil_No"		,sStockId);
			        
			   		boolean isTrue = callTcWorkInfo_Coil(jRecord);
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
	 *  COIL INFO
     	 *  야드맵 정보정신 Call
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     	               
	public boolean callMapInfo_Coil(){
		
		Boolean isSuccess = new Boolean(false);
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String sPut_Position = YmCommonConst.HYSCO_3HTC02;
			String sMsg 		 = YmCommonUtil.setBCoilMapMsgInfo(sPut_Position);	
				
			logger.println(LogLevel.DEBUG,this, "====야드맵 작업송신 CALL====");
			
			EJBConnector ejbConn = new EJBConnector("default","JNDICraneStatusReg",this);
			isSuccess = (Boolean)ejbConn.trx("bcyYdMapInfo",new  Class[]{String.class},
															new Object[]{sMsg});
		
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue();
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 *  HYSCO 대차 상차 실적을 발생시킨다.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     	                    	
	public boolean callHyscoTcRtInfo(String sTcCode){
		Boolean isSuccess = new Boolean(false);
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
	    		/**
	   		 *	1.	HYSCO 출하일 경우 실적정보를 송신한다.
	   		 */	
    			String[] sMsg = setBHyscoMsgInfo(sTcCode);
			
			//HYSCO COIL 에 대해서만 송신한다.
			if("HYSCO".equals(sMsg[1])){
				EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
				isSuccess = (Boolean)ejbConn.trx("sendMIMH110",new Class[]{String.class},new Object[]{ sMsg[0] });	
			}
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue();
	}    
	
	/**
	 *	B열연 대차상차실적	MIMH110	(현대제철 → HYSCO)		
	 *	
	 *	1	전문코드		전문코드		CHAR	7	7	Inter-face T/C Code
	 *	2	발생일자		발생일자		CHAR	14	21	전문발생시간(yyyymmddhhmmss)
	 *	3	작업근조		작업근조		CHAR	2	23	작업근조
	 *	4	작업공장		작업공장		CHAR	1	24	작업공장( 현대제철 : 'I', HYSCO : 'B')
	 *	5	작업공정		작업공정		CHAR	1	25	작업공정( 현대제철 : 'I', HYSCO : 'H')
	 *	6	전문count		전문count		CHAR	1	26	전문 발생 count(전문이 3개 생성시 첫번째 1, 두번째 2, 마지막 전문일경우 *)
	 *	7	전문요구구분	전문요구구분	CHAR	1	27	작업공정( 현대제철 : 'I', HYSCO : 'H')
	 *	8	여분항목		여분항목		CHAR	13	40	여분항목(space)
	 *	9	설비코드		설비코드		CHAR	4	44	현대제철 대차설비코드 'SSX1'
	 *	10	작업COIL수		작업COIL수		CHAR	1	45	대차 상차 coil 수
	 *	11	COILNO1			COILNO1			CHAR	15	60	''01'번지 상차 coil 번호(현대제철코일번호)
	 *	12	계약번호1		계약번호1		CHAR	10	70	계약 번호
	 *	13	계약행번1		계약행번1		CHAR	5	75	계약 행번
	 *	14	COILNO2			COILNO2			CHAR	15	90	''02'번지 상차 coil 번호
	 *	15	계약번호2		계약번호2		CHAR	10	100	계약 번호
	 *	16	계약행번2		계약행번2		CHAR	5	105	계약 행번
	 *	17	COILNO3			COILNO3			CHAR	15	120	''03'번지 상차 coil 번호
	 *	18	계약번호3		계약번호3		CHAR	10	130	계약 번호
	 *	19	계약행번3		계약행번3		CHAR	5	135	계약 행번 
	 *	
	 * @param schInfo : 압연실적 실적송신 INFO
     *
     * @return
     * @throws 
     */	 
	private String[] setBHyscoMsgInfo(String sTcNo){
		
		String[] sResult = new String[2];
		String 	 sVal	 = "";
		
		StringBuffer sMsg = new StringBuffer();

		String TC				= ""; 
		String sDate			= ""; 
		String sWorkDuty		= ""; 
		String sPlant			= ""; 
		String sProcess		= ""; 
		String sCount			= ""; 
		String sDemandGbn		= ""; 
		String sSpace			= ""; 
		String sEquipGp		= ""; 
		String sCoilCount		= ""; 
		String sCoilNo1		= ""; 
		String sNo1			= ""; 
		String sSeq1			= ""; 
		String sCoilNo2		= ""; 
		String sNo2			= ""; 
		String sSeq2			= ""; 
		String sCoilNo3		= ""; 
		String sNo3			= ""; 
		String sSeq3			= ""; 
	    
	    int iTC					=  7; 
		int iDate				= 14; 
		int iWorkDuty			=  2; 
		int iPlant				=  1; 
		int iProcess			=  1; 
		int iCount				=  1; 
		int iDemandGbn		=  1; 
		int iSpace			= 13; 
		int iEquipGp			=  4; 
		int iCoilCount			=  1; 
		int iCoilNo1			= 15; 
		int iNo1				= 10; 
		int iSeq1				=  5; 
		int iCoilNo2			= 15; 
		int iNo2				= 10; 
		int iSeq2				=  5; 
		int iCoilNo3			= 15; 
		int iNo3				= 10; 
		int iSeq3				=  5; 
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			//VALUE SETTING
			TC				= YmCommonConst.TC_MIMH110;
			sDate			= YmCommonUtil.getCurDate("yyyyMMddHHmmss");
			sWorkDuty		= YmCommonUtil.getWorkGroup(); 
			sPlant			= "I"; 
			sProcess			= "I"; 
			sCount			= "*"; 
			sDemandGbn		= "I"; 
			sSpace			= ""; 
			sEquipGp			= "SSX1"; 
			
			JDTORecord equipJr = dao.getEquipInfoWithEquipGp(sTcNo);
			
			String sUSchCd = "";
			
			if(equipJr != null){
				sUSchCd = StringHelper.evl(equipJr.getFieldString("CARLOAD_SCH_WORK_KIND"), "");//상차 Schedule 작업종류
			}
			
			List stock_list  = new ArrayList(); 
			stock_list = dao.getStockIdWithFrtomoveEquipNo(sTcNo);
			{ 
			 	 JDTORecord stockV			= null;
			 	 JDTORecord cInfoV			= null;
				 
				 String sStockId  = "";
				 String sBedGp	  = "";
				 String sNo  	  = "";
				 String sSeq  	  = "";
				 String sHyscoGp  = "";
				 int 	iCnt 	  =  0;
				 
				 for(int inx = 0; inx < stock_list.size() ; inx++){
				 	
				 	stockV 	  	 	= (JDTORecord)stock_list.get(inx);
				 	 	
				 	if(stockV != null){
					 	sStockId  	= StringHelper.evl(stockV.getFieldString("STOCK_ID"),"");	
					 	sBedGp  		= StringHelper.evl(stockV.getFieldString("FRTOMOVE_EQUIP_BED_GP"),"");	
					 	cInfoV 		= dao.getCoilCommonInfo2(sStockId);
			    	
		    			if(cInfoV != null){
		    				sNo  		= StringHelper.evl(cInfoV.getFieldString("주문번호"), "");
		    				sSeq 		= StringHelper.evl(cInfoV.getFieldString("주문행번"), "");
		    				sHyscoGp	= StringHelper.evl(cInfoV.getFieldString("HYSCO이송수단"), "");
		    			}
		    			
		    			// 코일공통 Hysco 이송수단이 'C'
						if(YmCommonConst.NEW_SCH_WORK_KIND_CTFL.equals(sUSchCd)&&
						   YmCommonConst.HYSCO_TRANS_GP_C.equals(sHyscoGp)){
						
			    			if(YmCommonConst.STACK_BED_GP_01.equals(sBedGp)){
			    				sCoilNo1		= sStockId; 
								sNo1	= sNo; 
								sSeq1	= sSeq; 
			    			}else if(YmCommonConst.STACK_BED_GP_02.equals(sBedGp)){ 
			    				sCoilNo2		= sStockId; 
								sNo2	= sNo; 
								sSeq2	= sSeq; 	
							}else if(YmCommonConst.STACK_BED_GP_03.equals(sBedGp)){ 	
								sCoilNo3	= sStockId; 
								sNo3	= sNo; 
								sSeq3	= sSeq; 	    				
			    			}	
			    			sVal	= "HYSCO";
			    			iCnt++;
			    		}	
					}	
				 } 
				 sCoilCount		= iCnt + ""; 
			}
				
			sMsg.append(YmCommonUtil.FillToString(TC		    	,iTC));
			sMsg.append(YmCommonUtil.FillToString(sDate			,iDate));
			sMsg.append(YmCommonUtil.FillToString(sWorkDuty	    	,iWorkDuty));
			sMsg.append(YmCommonUtil.FillToString(sPlant			,iPlant));
			sMsg.append(YmCommonUtil.FillToString(sProcess		,iProcess));
			sMsg.append(YmCommonUtil.FillToString(sCount			,iCount));
			sMsg.append(YmCommonUtil.FillToString(sDemandGbn	,iDemandGbn));
			sMsg.append(YmCommonUtil.FillToString(sSpace			,iSpace));
			sMsg.append(YmCommonUtil.FillToString(sEquipGp    		,iEquipGp));
			sMsg.append(YmCommonUtil.FillToString(sCoilCount		,iCoilCount));
			sMsg.append(YmCommonUtil.FillToString(sCoilNo1	    	,iCoilNo1));
			sMsg.append(YmCommonUtil.FillToString(sNo1	    		,iNo1));
			sMsg.append(YmCommonUtil.FillToNumber(sSeq1	    	,iSeq1));
			sMsg.append(YmCommonUtil.FillToString(sCoilNo2	    	,iCoilNo2));
			sMsg.append(YmCommonUtil.FillToString(sNo2			,iNo2));
			sMsg.append(YmCommonUtil.FillToNumber(sSeq2		,iSeq2));
			sMsg.append(YmCommonUtil.FillToString(sCoilNo3	    	,iCoilNo3));
			sMsg.append(YmCommonUtil.FillToString(sNo3			,iNo3));
			sMsg.append(YmCommonUtil.FillToNumber(sSeq3		,iSeq3));
			
			sResult[0] = sMsg.toString();
			sResult[1] = sVal;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return sResult;
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 *	8.	A열연 SLAB야드 추가(2007-02-26(MCH))
	 *		SLAB INFO 권하시
        *		야드맵 정보정신 Call
        * param jDTORecord 	: 전문항목
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
															
				String sUp_Position = StringHelper.evl(jtR.getFieldString("Up_Position"), "");
				String sMsg 		 = YmCommonUtil.setASlabMapMsgInfo(sUp_Position);	
				
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
	 *		SLAB INFO 권하시 연주 7호기
        *		야드맵 정보정신 Call
        *
        * param jDTORecord 	: 전문항목
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
															
				String sUp_Position = StringHelper.evl(jtR.getFieldString("Up_Position"), "");
				String sMsg 		 = YmCommonUtil.setASlabMapMsgInfo7(sUp_Position);	
				
				logger.println(LogLevel.DEBUG,this, "====A열연 연주 7호기 작업송신 CALL====");
				EJBConnector ejbConn = new EJBConnector("default","JNDICraneStatusReg",this);
				isSuccess = (Boolean)ejbConn.trx("bsyYdMapInfo",new  Class[]{String.class}, new Object[]{sMsg});																
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue();
	}
	
	
	/**
	 *	SLAB GRIP 가능한지를 체크해서 가능하면
	 *	SLAB GRIP 관련 항목을 셋팅한다.
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean setCraneWBBSlabGrapChk(String sTStockId,String sGStockId){
		
		boolean isSuccess = false;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
				    	
		    	/*
		    	 *	0.3	2개의 SLAB 기본정보를 가져온다.
		    	 */
		    	 
			    	JDTORecord stockJr1 = dao.getSlabCommonInfo(sTStockId);
			    	JDTORecord stockJr2 = dao.getSlabCommonInfo(sGStockId);
			    	
			    	if(stockJr1 == null){
			    		logger.println(LogLevel.DEBUG,this, "=SLAB GRIP["+sTStockId+":"+sGStockId+"] => SLAB 공통정보가 존재안함.");
			    		return false;
			    	}
			    	if(stockJr2 == null){
			    		logger.println(LogLevel.DEBUG,this, "=SLAB GRIP["+sTStockId+":"+sGStockId+"] => SLAB 공통정보가 존재안함.");
			    		return false;
			    	}
			    	
					String sTSlabWt = StringHelper.evl(stockJr1.getFieldString("SLAB_WT"),"0");
					String sGSlabWt = StringHelper.evl(stockJr2.getFieldString("SLAB_WT"),"0");
					
					String sTSlabW = StringHelper.evl(stockJr1.getFieldString("SLAB_W"),"0");
					String sGSlabW = StringHelper.evl(stockJr2.getFieldString("SLAB_W"),"0");
					
					String sTScarfingYn = StringHelper.evl(stockJr1.getFieldString("SCARFING_DONE_YN"),"N");
					String sGScarfingYn = StringHelper.evl(stockJr2.getFieldString("SCARFING_DONE_YN"),"N");
					//이송장비 구분 : PT , TR
					String sTEqpCD = StringHelper.evl(stockJr1.getFieldString("TRN_EQP_CD"),"PT");

					double dTSlabWt = Double.parseDouble(sTSlabWt);
					double dGSlabWt = Double.parseDouble(sGSlabWt);
					double dTSlabW = Double.parseDouble(sTSlabW);
					double dGSlabW = Double.parseDouble(sGSlabW);
					 
					logger.println(LogLevel.DEBUG,this, "=SLAB GRIP => dTSlabWt	="+dTSlabWt);
					logger.println(LogLevel.DEBUG,this, "=SLAB GRIP => dGSlabWt	="+dGSlabWt);
					logger.println(LogLevel.DEBUG,this, "=SLAB GRIP => dTSlabW	="+dTSlabW);
					logger.println(LogLevel.DEBUG,this, "=SLAB GRIP => dGSlabW	="+dGSlabW);
					
					if((dTSlabWt + dGSlabWt) > 53000){
						
						logger.println(LogLevel.DEBUG,this, "=SLAB GRIP["+sTStockId+":"+sGStockId+"] => 크레인설정 중량을 초과함1.");
			    		return false;
					}
					/*
					 * W/B인 경우 에만 적용 Slab 폭 20mm 이상 제외
					 */
					if( Math.abs(dTSlabW - dGSlabW) > 20.0){
						
						logger.println(LogLevel.DEBUG,this, "=SLAB GRIP["+sTStockId+":"+sGStockId+"] =>WB 크레인설정 폭을 초과함1.");
			    		return false;
					}
					
					/*
					 *  Slab 폭 2000mm 이상 제외 2015.07.09 
					 */
					if(dTSlabW>=2000 || dGSlabW>=2000){
						logger.println(LogLevel.DEBUG,this, "=SLAB GRIP["+sTStockId+":"+sGStockId+"] => 크레인설정 폭 2000mm를 초과함1.");
			    		return false;
					}
					
					/*
					 * TR인 경우 한매라도 스카핑 인 경우 그립 작업 스킵 2011.04.11 지영묵 계장 요청
					 */
//이슈ID:13241		if( sTEqpCD.equals("TR") && (sTScarfingYn.equals("Y") || sGScarfingYn.equals("Y"))){
//						
//						logger.println(LogLevel.DEBUG,this, "=SLAB GRIP["+sTStockId+":"+sGStockId+"] => TR인 경우 한매라도 스카핑 인 경우 그립 작업 스킵.");
//			    		return false;
//					}
		    	/*
		    	 *	3.	재료속성을 체크한다.
				 *		1. CAMBER_YN 이 Y이면 1매 작업
				 *		2. TAPER_SLAB_GP 이 Y이면 1매 작업
				 *		3. LONG_BOW_YN 이 Y이고
				 *		   LONG_BOW_VAL 이 50mm 이상이면 1매 작업
				 */ 
		    		
		    		String sTCamberYn 		= StringHelper.evl(stockJr1.getFieldString("CAMBER_YN"),"");
					String sTTaperSlabGp 	= StringHelper.evl(stockJr1.getFieldString("TAPER_SLAB_GP"),"");
					String sTLongBowYn 		= StringHelper.evl(stockJr1.getFieldString("LONG_BOW_YN"),"");
					String sTLongBowVal 	= StringHelper.evl(stockJr1.getFieldString("LONG_BOW_VAL"),"0");
					String sTLspecAbbsym 	= StringHelper.evl(stockJr1.getFieldString("SPEC_ABBSYM"),"");
					String sTLCwrsltYn	 	= StringHelper.evl(stockJr1.getFieldString("C_WRSLT_CHK"),"");
					
					String sGCamberYn 		= StringHelper.evl(stockJr2.getFieldString("CAMBER_YN"),"");
					String sGTaperSlabGp 	= StringHelper.evl(stockJr2.getFieldString("TAPER_SLAB_GP"),"");
					String sGLongBowYn 		= StringHelper.evl(stockJr2.getFieldString("LONG_BOW_YN"),"");
					String sGLongBowVal 	= StringHelper.evl(stockJr2.getFieldString("LONG_BOW_VAL"),"0");
					String sGLspecAbbsym 	= StringHelper.evl(stockJr2.getFieldString("SPEC_ABBSYM"),"");
					String sGLCwrsltYn	 	= StringHelper.evl(stockJr2.getFieldString("C_WRSLT_CHK"),"");
					
			    		logger.println(LogLevel.DEBUG,this, "=SLAB GRIP => sTCamberYn	="+sTCamberYn);
			    		logger.println(LogLevel.DEBUG,this, "=SLAB GRIP => sTTaperSlabGp="+sTTaperSlabGp);
			    		logger.println(LogLevel.DEBUG,this, "=SLAB GRIP => sTLongBowYn	="+sTLongBowYn);
			    		logger.println(LogLevel.DEBUG,this, "=SLAB GRIP => sTLongBowVal	="+sTLongBowVal);
			    		logger.println(LogLevel.DEBUG,this, "=SLAB GRIP => sTLspecAbbsym	="+sTLspecAbbsym);
			    		logger.println(LogLevel.DEBUG,this, "=SLAB GRIP => sTLCwrsltYn	="+sTLCwrsltYn);
			    		
			    		logger.println(LogLevel.DEBUG,this, "=SLAB GRIP => sGCamberYn	="+sGCamberYn);
			    		logger.println(LogLevel.DEBUG,this, "=SLAB GRIP => sGTaperSlabGp="+sGTaperSlabGp);
			    		logger.println(LogLevel.DEBUG,this, "=SLAB GRIP => sGLongBowYn	="+sGLongBowYn);
			    		logger.println(LogLevel.DEBUG,this, "=SLAB GRIP => sGLongBowVal	="+sGLongBowVal);
			    		logger.println(LogLevel.DEBUG,this, "=SLAB GRIP => sGLspecAbbsym	="+sGLspecAbbsym);
			    		logger.println(LogLevel.DEBUG,this, "=SLAB GRIP => sGLCwrsltYn	="+sGLCwrsltYn);
			    	
//이슈ID:13241  		if(YmCommonConst.USE_YN_Y.equals(sTLCwrsltYn)
//		    				&&(sTScarfingYn.equals("Y"))){
//						logger.println(LogLevel.DEBUG,this, "=SLAB GRIP["+sTStockId+":"+sGStockId+"] => 카본함량0.20이상 스카핑완료재(sTLCwrsltYn)1매작업대상");
//			    		return false;
//					}
//
//					if((sTLspecAbbsym.equals("API-J55")||sTLspecAbbsym.equals("JS-S45C")||sTLspecAbbsym.equals("JS-SS490")||sTLspecAbbsym.equals("HSC1470HPF"))
//						&&(sTScarfingYn.equals("Y"))){
//						logger.println(LogLevel.DEBUG,this, "=SLAB GRIP["+sTStockId+":"+sGStockId+"] => 재료속성(sTLspecAbbsym)1매작업대상");
//			    		return false;
//					}
//					
//					
//					if(YmCommonConst.USE_YN_Y.equals(sGLCwrsltYn)
//							&&(sGScarfingYn.equals("Y"))){
//						logger.println(LogLevel.DEBUG,this, "=SLAB GRIP["+sTStockId+":"+sGStockId+"] => 카본함량0.20이상 스카핑완료재(sTLCwrsltYn)1매작업대상");
//			    		return false;
//					}
//					
//					if((sGLspecAbbsym.equals("API-J55")||sGLspecAbbsym.equals("JS-S45C")||sGLspecAbbsym.equals("JS-SS490")||sGLspecAbbsym.equals("HSC1470HPF"))
//					   &&(sGScarfingYn.equals("Y"))){
//						logger.println(LogLevel.DEBUG,this, "=SLAB GRIP["+sTStockId+":"+sGStockId+"] => 재료속성(sTLspecAbbsym)1매작업대상");
//			    		return false;
//					}
    
	
		    	    				
    		logger.println(LogLevel.DEBUG,this, "=SLAB GRIP["+sTStockId+":"+sGStockId+"] => 2매 작업 셋팅 완료.");
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
	 *	4.	COIL INFO
        *	구내운송하차개시/완료 Call
        *	권상시 대차에게 작업지시 전문을 Call
        *
        * param jDTORecord 	: 전문항목
        * param String		: Wbook_Id
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */              
	public boolean callTSTcWorkInfo_Coil(JDTORecord jtR){
		
		Boolean isSuccess     = new Boolean(false);
		
		YdStockDAO ydStockDAO = new YdStockDAO(); 
		ymCommonDAO dao 	  = ymCommonDAO.getInstance();
		List FrtoProductList  = null;
		
		int count			= 0;
		int tot_Qty  		= 0;
		int work_Qty  		= 0;
		String stl_appear_gp= "";   //재료외형구분 Y: 제품 , 기타: 소재
		String queryID		= "";
		String szYD_CAR_SCH_ID = "";
		String szYD_CAR_USE_GP = "";
		String szTRANS_EQUIPMENT_TYPE = "";
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}

			// PIDEV
//			String sApplyYnPI = commDao.ApplyYnPI("", "CUpResRegSBean => 열연코일하차개시수신", "APPPI0", "*", "*");
			
			String Sch_Code 	 = StringHelper.evl(jtR.getFieldString("Sch_Code"), "").trim();
			String Up_Position 	 = StringHelper.evl(jtR.getFieldString("Up_Position"), "").trim();
			String Coil_No 	 	 = StringHelper.evl(jtR.getFieldString("Coil_No"), "").trim();
 
			
			//##############################################################################################################
			// 코일 하차개시/완료#############################################################################################
			//##############################################################################################################
			if((YmCommonConst.EQUIP_KIND_PT.equals(Up_Position.substring(2, 4)) || YmCommonConst.EQUIP_KIND_TR.equals(Up_Position.substring(2, 4)))
			   &&
			   (YmCommonConst.NEW_SCH_WORK_KIND_CVMU.equals(Sch_Code)|| //Coil 소재이송하차
	 		   YmCommonConst.NEW_SCH_WORK_KIND_CVM4.equals(Sch_Code)|| // COIL 소재이송하차
		       YmCommonConst.NEW_SCH_WORK_KIND_CVM5.equals(Sch_Code)|| // COIL 소재이송하차
		       YmCommonConst.NEW_SCH_WORK_KIND_GVMU.equals(Sch_Code)|| // COIL 제품이송하차	
			   YmCommonConst.NEW_SCH_WORK_KIND_GVM4.equals(Sch_Code)|| // COIL 제품이송하차
			   YmCommonConst.NEW_SCH_WORK_KIND_GVM5.equals(Sch_Code)   // COIL 제품이송하차	
			    )
			  ){
           					
				queryID = "ym.facilitywork.putwrecord.session.getListworkQty_Coil1";		    	
	    		FrtoProductList = dao.getCommonList(queryID, new Object[]{Coil_No,Coil_No,Coil_No});
				
	    		if(FrtoProductList.size() > 0){
			    	JDTORecord StkColRecT = (JDTORecord)FrtoProductList.get(0);
			    	tot_Qty  		= 	StkColRecT.getFieldInt("TOT_QTY");
			    	work_Qty  		= 	StkColRecT.getFieldInt("WORK_QTY");
			    	stl_appear_gp	=	StringHelper.evl(StkColRecT.getFieldString("STL_APPEAR_GP"), "");   //재료외형구분 Y: 제품 , 기타: 소재
	    		}	    
	    		
	    		
	    		
	    		queryID	= "ym.facilitywork.putwrecord.session.getListTcconst_1";
		    	FrtoProductList = dao.getCommonList(queryID, new Object[]{Coil_No});
		    	JDTORecord FrtoProduct = (JDTORecord)FrtoProductList.get(0);
		    	szYD_CAR_SCH_ID = StringHelper.evl(FrtoProduct.getFieldString("YD_CAR_SCH_ID"), "");
		    	szYD_CAR_USE_GP = StringHelper.evl(FrtoProduct.getFieldString("YD_CAR_USE_GP"), "");
		    	szTRANS_EQUIPMENT_TYPE		= StringHelper.evl(FrtoProduct.getFieldString("TRANS_EQUIPMENT_TYPE"), ""); //운송장비TYPE P:PDA
	    		/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		         * 하차개시 전문 송신 처리 - 구내운송
		         * 업무기준 Desc : 권상실적위치로 적치열에서 운송장비코드를 조회하고 그 코드로 차량스케줄을 조회하여 차량진행상태가
		         * 				하차검수 or 하차도착이면 하차개시로 차량스케줄을 업데이트하고 전문을 전송한다.
		         * 기능 추가 : 정종균
		         * 일자 : 2014.03.28
		         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	    		if(work_Qty == 1) //개시
	    		{
	    			//차량스케쥴테이블 - 하차개시시간 업데이트
	    			queryID = "ym.facilitywork.putwrecord.session.updateUnloadstartTime";
			    	count = dao.updateData(queryID, new Object[]{Coil_No}); 
			    	
			    	
			    	
			    	if( szYD_CAR_USE_GP.equals("L") ) {
						// 하차개시실적 송신#####################################################################################
						// 소재차량하차개시
						JDTORecord tcRecord = null;
						tcRecord = JDTORecordFactory.getInstance().create();
						tcRecord.setField("JMS_TC_CD" , "YDTSJ009");
						tcRecord.setField("JMS_TC_CREATE_DDTT" , new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
						tcRecord.setField("TRN_EQP_CD" , StringHelper.evl(FrtoProduct.getFieldString("TRN_EQP_CD") , ""));
						tcRecord.setField("ARR_WLOC_CD" , StringHelper.evl(FrtoProduct.getFieldString("ARR_WLOC_CD") , ""));
						tcRecord.setField("ARR_YD_PNT_CD" , StringHelper.evl(FrtoProduct.getFieldString("YD_PNT_CD") , ""));
						tcRecord.setField("TRN_WRK_ST_DT" , new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));

						EJBConnector ejbConn = new EJBConnector("default" , "JNDIYardWrkResReg" , this);
						isSuccess = (Boolean) ejbConn.trx("sendInternalModel" , new Class[]{JDTORecord.class} , new Object[]{tcRecord});
						logger.println(LogLevel.DEBUG , this , "내부IF호출=== 일관제철 구내운송 이송하차개시실적.===");
						// ####################################################################################################

						// 20091111 개시 시점에 상차대상을 한번에 전송 한다.
						// 출하고간이송상차개시실적 송신(제품인 경우에만 출하에 전송 한다.)################################################
						if (stl_appear_gp.equals("Y")) {
							// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
							// 코일제품고간이송상하차개시

							queryID = "ym.facilitywork.putwrecord.session.getListTcconst22_PIDEV";
							FrtoProductList = dao.getCommonList(queryID , new Object[]{StringHelper.evl(FrtoProduct.getFieldString("TRN_EQP_CD") , "")});

							JDTORecord tcRecordDM2 = null;

							tcRecordDM2 = JDTORecordFactory.getInstance().create();
							tcRecordDM2.setField("JMS_TC_CD" , "YDDMR019");
							tcRecordDM2.setField("TC_CREATE_DDTT" , new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
							tcRecordDM2.setField("UPCARUNLOAD_GP" , "D"); // U:상차,D:하차
							tcRecordDM2.setField("CARD_NO" , "");
							tcRecordDM2.setField("CAR_NO" , StringHelper.evl(FrtoProduct.getFieldString("TRN_EQP_CD") , ""));
							tcRecordDM2.setField("YD_GP" , jtR.getFieldString("Yard_Id"));
							tcRecordDM2.setField("CARLOAD_START_DATE" , YmCommonUtil.getCurDate("yyyyMMdd"));
							tcRecordDM2.setField("CARLOAD_START_TIME" , YmCommonUtil.getCurDate("HHmmss"));

							int iSeqCount = FrtoProductList.size();
							for (int i = 0; i < iSeqCount; i++) {

								JDTORecord FrtoProduct2 = (JDTORecord) FrtoProductList.get(i);

								tcRecordDM2.setField("GOODS_NO" + (1 + i) , StringHelper.evl(FrtoProduct2.getFieldString("STL_NO") , ""));
								tcRecordDM2.setField("TRANS_WORD_DATE" + (1 + i) , StringHelper.evl(FrtoProduct2.getFieldString("TRANS_WORD_DATE") , ""));
								tcRecordDM2.setField("TRANS_WORD_SEQNO" + (1 + i) , StringHelper.evl(FrtoProduct2.getFieldString("TRANS_WORD_SEQNO") , ""));

							}
							// 인터페이스 전문 호출
							EJBConnector ejbConn1 = new EJBConnector("default" , "JNDIYardWrkResReg" , this);
							isSuccess = (Boolean) ejbConn1.trx("sendInternalModel" , new Class[]{JDTORecord.class} , new Object[]{tcRecordDM2});
							logger.println(LogLevel.DEBUG , this , "내부IF호출=== 일관제철 코일제품고간이송상하차개시.===");
							// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

						}
					} else if (szTRANS_EQUIPMENT_TYPE.equals("P")) { // 출하PDA

						// 코일이송하차개시 전송PDA
						JDTORecord tcRecordDM = null;

						tcRecordDM = JDTORecordFactory.getInstance().create();
						
						// PIDEV
//						if ("Y".equals(sApplyYnPI)) {
							tcRecordDM.setField("MQ_TC_CD" 			, "M10YDLMJ1111");
							tcRecordDM.setField("MQ_TC_CREATE_DDTT" , new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
							tcRecordDM.setField("TRN_REQ_DATE1" 	, StringHelper.evl(FrtoProduct.getFieldString("TRANS_WORD_DATE") , ""));
							tcRecordDM.setField("TRN_REQ_SEQ1" 		, StringHelper.evl(FrtoProduct.getFieldString("TRANS_WORD_SEQNO") , ""));
							tcRecordDM.setField("CARD_NO" 			, StringHelper.evl(FrtoProduct.getFieldString("CARD_NO") , ""));
							tcRecordDM.setField("CAR_NO" 			, StringHelper.evl(FrtoProduct.getFieldString("CAR_NO") , ""));
							tcRecordDM.setField("YD_GP" 			, jtR.getFieldString("Yard_Id"));
							tcRecordDM.setField("DIST_GOODS_GP" 	, "H");
							tcRecordDM.setField("YARD_GP" 			, "");							
							tcRecordDM.setField("CARUD_ST_DATE" 	, YmCommonUtil.getCurDate("yyyyMMdd"));
							tcRecordDM.setField("CARUD_ST_TIME" 	, YmCommonUtil.getCurDate("HHmmss"));
							tcRecordDM.setField("GOODS_CNT" 		, "0");
							tcRecordDM.setField("CR_FRTOMOVE_GP" 	, StringHelper.evl(FrtoProduct.getFieldString("CR_FRTOMOVE_GP") , ""));
							
//						} else {
//							tcRecordDM.setField("JMS_TC_CD" , "YDDMR075");
//							tcRecordDM.setField("TC_CREATE_DDTT" , new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
//							tcRecordDM.setField("CARD_NO" , StringHelper.evl(FrtoProduct.getFieldString("CARD_NO") , ""));
//							tcRecordDM.setField("CAR_NO" , StringHelper.evl(FrtoProduct.getFieldString("CAR_NO") , ""));
//							tcRecordDM.setField("YD_GP" , jtR.getFieldString("Yard_Id"));
//							tcRecordDM.setField("CARUD_START_DATE" , YmCommonUtil.getCurDate("yyyyMMdd"));
//							tcRecordDM.setField("CARUD_START_TIME" , YmCommonUtil.getCurDate("HHmmss"));
//							tcRecordDM.setField("TRANS_WORD_DATE" , StringHelper.evl(FrtoProduct.getFieldString("TRANS_WORD_DATE") , ""));
//							tcRecordDM.setField("TRANS_WORD_SEQNO" , StringHelper.evl(FrtoProduct.getFieldString("TRANS_WORD_SEQNO") , ""));
//							tcRecordDM.setField("CR_FRTOMOVE_GP" , StringHelper.evl(FrtoProduct.getFieldString("CR_FRTOMOVE_GP") , ""));
//						}

						// 인터페이스 전문 호출
						EJBConnector ejbConn1 = new EJBConnector("default" , "JNDIYardWrkResReg" , this);
						isSuccess = (Boolean) ejbConn1.trx("sendInternalModel" , new Class[]{JDTORecord.class} , new Object[]{tcRecordDM});
					}
					//####################################################################################################
	    		}
	    		
	    		
	    		if( szTRANS_EQUIPMENT_TYPE.equals("P") ) {					//출하PDA
	    			 
		            logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일품 상차실적 송신 YDDMR072 (코일일품출하상차실적 송신)1.==="); 
		         // 일품 상차실적 송신 전송PDA#############################################################################
					JDTORecord tcRecordDM = null;

					tcRecordDM = JDTORecordFactory.getInstance().create();
					tcRecordDM.setField("JMS_TC_CD" , "YDDMR072");
					tcRecordDM.setField("TC_CREATE_DDTT" , new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
					tcRecordDM.setField("CARD_NO" , StringHelper.evl(FrtoProduct.getFieldString("CARD_NO") , ""));
					tcRecordDM.setField("CAR_NO" , StringHelper.evl(FrtoProduct.getFieldString("CAR_NO") , ""));
					tcRecordDM.setField("YD_GP" , jtR.getFieldString("Yard_Id"));
					tcRecordDM.setField("GOODS_NO" , Coil_No); 
					tcRecordDM.setField("TRANS_WORD_DATE" , StringHelper.evl(FrtoProduct.getFieldString("TRANS_WORD_DATE") , ""));
					tcRecordDM.setField("TRANS_WORD_SEQNO" , StringHelper.evl(FrtoProduct.getFieldString("TRANS_WORD_SEQNO") , "")); 

					// 인터페이스 전문 호출
					EJBConnector ejbConn1 = new EJBConnector("default" , "JNDIYardWrkResReg" , this);
					isSuccess = (Boolean) ejbConn1.trx("sendInternalModel" , new Class[]{JDTORecord.class} , new Object[]{tcRecordDM});
					//####################################################################################################
	            }
	    		
	    		
	    		 
	    		/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		         * 하차완료 전문 송신 처리 - 구내운송
		         * 업무기준 Desc : 마지막 코일을 권상하는 시점에 하차완료 전문을 전송함.
		         * 기능 추가 : 정종균
		         * 일자 : 2014.03.28
		         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	    		if(tot_Qty == work_Qty) //완료 
	    		{			    	
 	 
					if (szYD_CAR_USE_GP.equals("L")) {
						
						// 차량스케쥴테이블 - 하차완료시간 업데이트
						queryID = "ym.facilitywork.putwrecord.session.updateUnloadendTime";
						count = dao.updateData(queryID , new Object[]{Coil_No});

						// 차량재료테이블 - DEL_YN 업데이트
						queryID = "ym.facilitywork.putwrecord.session.deletefrmtList";
						count = dao.updateData(queryID , new Object[]{Coil_No});

						// 하차완료실적 송신#####################################################################################
						// 소재차량하차완료
						JDTORecord tcRecord = null;
						tcRecord = JDTORecordFactory.getInstance().create();
						tcRecord.setField("JMS_TC_CD" , "YDTSJ010");
						tcRecord.setField("JMS_TC_CREATE_DDTT" , new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
						tcRecord.setField("TRN_EQP_CD" , StringHelper.evl(FrtoProduct.getFieldString("TRN_EQP_CD") , ""));
						tcRecord.setField("ARR_WLOC_CD" , StringHelper.evl(FrtoProduct.getFieldString("ARR_WLOC_CD") , ""));
						tcRecord.setField("ARR_YD_PNT_CD" , StringHelper.evl(FrtoProduct.getFieldString("YD_PNT_CD") , ""));
						tcRecord.setField("CARUD_CMPL_DT" , new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));

						EJBConnector ejbConn = new EJBConnector("default" , "JNDIYardWrkResReg" , this);
						isSuccess = (Boolean) ejbConn.trx("sendInternalModel" , new Class[]{JDTORecord.class} , new Object[]{tcRecord});
						logger.println(LogLevel.DEBUG , this , "내부IF호출===코일 일관제철 구내운송 이송하차완료실적.===");
						// ####################################################################################################

						// 출하고간이송상차완료실적 송신(제품인 경우에만 출하에 전송 한다.)################################################
						if (stl_appear_gp.equals("Y")) {
							// 코일제품고간이송상하차완료
							// 차량스케쥴ID로 이송재료 조회(
							String trnQueryId = "ym.tsinfo.getListFrtostlList_loadEnd_PIDEV";
							List FrtostlList = dao.getCommonList(trnQueryId , new Object[]{StringHelper.evl(FrtoProduct.getFieldString("TRN_EQP_CD") , "")});

							JDTORecord tcRecord1 = null;
							tcRecord1 = JDTORecordFactory.getInstance().create();
							int nIdx = 0;

							int iSeqCount = FrtostlList.size();

							if (iSeqCount > 0) {
								for (int i = 0; i < iSeqCount; i++) {

									JDTORecord FrtoSltrec = (JDTORecord) FrtostlList.get(i);
									
									// PIDEV
//									if ("Y".equals(sApplyYnPI)) {
										if (i == 0) {
											tcRecord1.setField("MQ_TC_CD" , "M10YDLMJ1121");
											tcRecord1.setField("MQ_TC_CREATE_DDTT" , new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
											tcRecord1.setField("TRN_REQ_DATE", StringHelper.evl(FrtoSltrec.getFieldString("TRANS_WORD_DATE") , ""));
											tcRecord1.setField("TRN_REQ_SEQ", StringHelper.evl(FrtoSltrec.getFieldString("TRANS_WORD_SEQNO") , ""));
											tcRecord1.setField("CAR_NO" , StringHelper.evl(FrtoSltrec.getFieldString("TRN_EQP_CD") , ""));
											tcRecord1.setField("YD_GP" , StringHelper.evl(FrtoSltrec.getFieldString("YD_GP") , ""));
											tcRecord1.setField("DIST_GOODS_GP" , "H");
											tcRecord1.setField("YARD_GP" , "");
											tcRecord1.setField("UPCARUNLOAD_GP" , "D"); // U:상차,D:하차
											tcRecord1.setField("ARR_YD_PNT_CD" , StringHelper.evl(FrtoSltrec.getFieldString("ARR_YD_PNT_CD") , ""));
											tcRecord1.setField("CARUD_CMPL_DATE" , new String(YmCommonUtil.getTcDate("yyyyMMdd")));
											tcRecord1.setField("CARUD_CMPL_TIME" , new String(YmCommonUtil.getTcDate("HHmmss")));
											tcRecord1.setField("GOODS_CNT" , Integer.toString(iSeqCount)); // 처리개수
										}
										tcRecord1.setField("GOODS_NO" + (nIdx + 1) , StringHelper.evl(FrtoSltrec.getFieldString("STL_NO") , ""));
										tcRecord1.setField("STORE_LOC_CD" + (nIdx + 1) , StringHelper.evl(FrtoSltrec.getFieldString("STORE_LOC_CD") , ""));
										
										nIdx++;
										
//									} else {
//										if (i == 0) {
//											tcRecord1.setField("JMS_TC_CD" , "YDDMR021");
//											tcRecord1.setField("TC_CREATE_DDTT" , new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
//											tcRecord1.setField("UPCARUNLOAD_GP" , "D"); // U:상차,D:하차
//											tcRecord1.setField("TREAT_EA" , Integer.toString(iSeqCount)); // 처리개수
//											tcRecord1.setField("CARD_NO" , "");
//											tcRecord1.setField("CAR_NO" , StringHelper.evl(FrtoSltrec.getFieldString("TRN_EQP_CD") , ""));
//											tcRecord1.setField("ARR_YD_PNT_CD" , StringHelper.evl(FrtoSltrec.getFieldString("ARR_YD_PNT_CD") , ""));
//											tcRecord1.setField("ISSUE_DDTT" , new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
//										}
//
//										tcRecord1.setField("GOODS_NO" + (nIdx + 1) , StringHelper.evl(FrtoSltrec.getFieldString("STL_NO") , ""));
//										tcRecord1.setField("TRANS_WORD_DATE" + (nIdx + 1) , StringHelper.evl(FrtoSltrec.getFieldString("TRANS_WORD_DATE") , ""));
//										tcRecord1.setField("TRANS_WORD_SEQNO" + (nIdx + 1) , StringHelper.evl(FrtoSltrec.getFieldString("TRANS_WORD_SEQNO") , ""));
//										tcRecord1.setField("STORE_LOC_CD" + (nIdx + 1) , StringHelper.evl(FrtoSltrec.getFieldString("STORE_LOC_CD") , ""));
//										tcRecord1.setField("YD_GP" + (nIdx + 1) , StringHelper.evl(FrtoSltrec.getFieldString("YD_GP") , ""));
//										tcRecord1.setField("BAY_GP" + (nIdx + 1) , StringHelper.evl(FrtoSltrec.getFieldString("BAY_GP") , ""));
//										tcRecord1.setField("SPAN" + (nIdx + 1) , StringHelper.evl(FrtoSltrec.getFieldString("SPAN_GP") , ""));
//										tcRecord1.setField("STK_LYR" + (nIdx + 1) , StringHelper.evl(FrtoSltrec.getFieldString("STACK_LAYER_GP") , ""));
//
//										nIdx++;
//									}
								}

								EJBConnector ejbConn2 = new EJBConnector("default" , "JNDIYardWrkResReg" , this);
								isSuccess = (Boolean) ejbConn2.trx("sendInternalModel" , new Class[]{JDTORecord.class} , new Object[]{tcRecord1});
								logger.println(LogLevel.DEBUG , this , "내부IF호출=== 일관제철 출하제품 이송상차완료실적.===>>" + nIdx);
							}
						}
					} 
					//####################################################################################################
	    		  
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
	 *	4.	COIL INFO
        *	출하상차개시/완료 Call
        *	권상시  작업지시 전문을 Call
        *
        * param jDTORecord 	: 전문항목
        * param String		: Wbook_Id
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */              
	public boolean callDMTcWorkInfo_Coil(JDTORecord jtR){
		
		Boolean isSuccess     = new Boolean(false); 
		YdCarSchDao	ydCarSchDao	 = new YdCarSchDao();

		String szYD_CAR_SCH_ID		= "";
		String sCMBN_CARLD_YN		= "";
		String sCarNo 				= "";
		String szTRANS_EQUIPMENT_TYPE ="";
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String sSchCode 	 = StringHelper.evl(jtR.getFieldString("Sch_Code"), "").trim();
			String sStockId	 	 = StringHelper.evl(jtR.getFieldString("Coil_No"), "").trim();
			String sYardGp	 	 = StringHelper.evl(jtR.getFieldString("Yard_Id"), "").trim(); 
			String Work_Id 		 = YmCommonUtil.getCurDataWithLegacy(StringHelper.evl(jtR.getFieldString("Work_Id"), ""));// M: 주작업 , S: 보조작업
 
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
			if(Work_Id.equals("M")){
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
						
						/*
						 * 저장품 이동조건 셋팅
						 */
						{
							int iReq = dao.updateStockTransInfo(sStockId, "VL"); 
							logger.println(LogLevel.DEBUG,this, "===STOCK_MOVE_TERM = " + "VL");
						}
				 
						dmRc = dao.getCarSchInfo(sStockId);					    
				    	if(dmRc != null){				    		
							szYD_CAR_SCH_ID	= StringHelper.evl(dmRc.getFieldString("YD_CAR_SCH_ID"), "");
	 						sCMBN_CARLD_YN	= StringHelper.evl(dmRc.getFieldString("CMBN_CARLD_YN"), "");
				    	}
				    	 
				    	/*--ym.facilitystatus.facilityinquiry.CraneSchDAO.getYmDmCommonInfoNEW2_PIDEV*/
						dmList  = dao.getYmDmCommonInfoNEW2(sStockId);
						iDmSize = dmList.size();
						
						for(int inx = 0; inx < dmList.size() ; inx++){
					 	 	dmRc = (JDTORecord)dmList.get(inx);
					 	 	sSmt = StringHelper.evl(dmRc.getFieldString("STOCK_MOVE_TERM"), "");
					 	 	sCarNo = StringHelper.evl(dmRc.getFieldString("CAR_NO"), "");
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
						
						
						
						
						//상차개시 처리----------------------------------------------------------------------------------------------
				    	if(iDmFns == 1 && !"E".equals(sCMBN_CARLD_YN)){ //상차개시인 저장품이 1개일 때 처리
				    		
				    		String sCarCardNo     = StringHelper.evl(dmRc.getFieldString("CAR_CARD_NO"), "");
				    		String sTransWordNo   = StringHelper.evl(dmRc.getFieldString("TRANS_WORD_NO"), "");
		
				 
	
		                    //코일출하상차개시###############################################################################
		         			JDTORecord tcRecordDM = null;
		         			tcRecordDM = JDTORecordFactory.getInstance().create(); 
		         			tcRecordDM.setField("CARD_NO",			sCarCardNo);
		         			tcRecordDM.setField("CAR_NO",			sCarNo);
		         			tcRecordDM.setField("YD_GP",			sYardGp);
		         			tcRecordDM.setField("TRANS_WORD_DATE",	sTransWordNo.length() > 8 ? sTransWordNo.substring(0,8) : sTransWordNo);
		         			tcRecordDM.setField("TRANS_WORD_SEQNO",	sTransWordNo.length() > 8 ? sTransWordNo.substring(8) : "");
		         
							
		         			//인터페이스 전문 호출
		         			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
		         			isSuccess = (Boolean)ejbConn.trx("getYDDMR007" ,new Class[]{JDTORecord.class},
		         			  	  	 new Object[]{tcRecordDM}); 
		                    logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 코일출하상차개시.===");
		                    //###############################################################################################
		                    
	
		                    
		                  //차량스케줄 업데이트 - 상차개시######################################################################
		                    JDTORecord recInTemp	=null;
	        				recInTemp = JDTORecordFactory.getInstance().create();
	        				recInTemp.setField("YD_CAR_SCH_ID", 	szYD_CAR_SCH_ID);								//차량스케줄ID
	        				recInTemp.setField("YD_CAR_PROG_STAT", 	"4");										//차량진행상태
	    	    			recInTemp.setField("YD_EQP_WRK_STAT", 	"U");											//작업상태
	    	    			recInTemp.setField("YD_CARLD_ST_DT", 	YdUtils.getCurDate("yyyyMMddHHmmss"));			//상차개시일시
	    	    			recInTemp.setField("MODIFIER",			"callInner");										//수정자
	
	    	    			int intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 0);        	    			
	    	    			if(intRtnVal <= 0) {
	    	    				String szMsg="[callInnerWorkInfo_Coil] 차량스케줄 상차개시 시 오류발생[반환값 : " + intRtnVal + "]";
	    	    				logger.println(LogLevel.DEBUG,this, szMsg);
	    	    			}
	    	    			logger.println(LogLevel.DEBUG,this, "[callInnerWorkInfo_Coil] 차량스케줄 상차개시완료.===");
	    	    			//#################################################################################################
		                    
				    	}
				        //-----------------------------------------------------------------------------------------------------
				    	
				    	
				    	
				    	//제원정보 전송------------------------------------------------------------------------------------------
			 
			    		String sCarCardNo     = StringHelper.evl(dmRc.getFieldString("CAR_CARD_NO"), "");
			    		String sTransWordNo   = StringHelper.evl(dmRc.getFieldString("TRANS_WORD_NO"), "");
				    	
	
	                    //코일일품출하상차실적 #################################################################################
	         			JDTORecord tcRecordDM = null;
	         			tcRecordDM = JDTORecordFactory.getInstance().create(); 
	                    tcRecordDM.setField("CARD_NO",			sCarCardNo);
	                    tcRecordDM.setField("CAR_NO", 			sCarNo);
	                    tcRecordDM.setField("YD_GP",  			sYardGp);		
	                    tcRecordDM.setField("GOODS_NO", 		sStockId);
	                    tcRecordDM.setField("TRANS_WORD_DATE", 	sTransWordNo.length() > 8 ? sTransWordNo.substring(0,8) : sTransWordNo);
	                    tcRecordDM.setField("TRANS_WORD_SEQNO", sTransWordNo.length() > 8 ? sTransWordNo.substring(8) : "");
	         			
	                    if(iDmSize != 0 && iDmSize == iDmFns){ // 상차완료인 저장품이 ALL일때 처리
	                    	tcRecordDM.setField("GOODS_EA","*");
	                    }else{
	                    	tcRecordDM.setField("GOODS_EA","1");
	                    }
	         
						
	         			//인터페이스 전문 호출
	         			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
	         			isSuccess = (Boolean)ejbConn.trx("getYDDMR011" ,new Class[]{JDTORecord.class},
	         			  	  	 new Object[]{tcRecordDM}); 
	                    logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 코일일품출하상차실적.===");
	                   //##########################################################################################################
	     
						//--------------------------------------------------------------------------------------------------------
					}
				}
			}
			//*****************************************************************************************************************
			
			
			
			
			//*****************************************************************************************************************
			///////////////////////////////임가공 상차개시  및 차량스케줄 관리 ///////////////////////////////////////////////
			//*****************************************************************************************************************			
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
					int		   iDmFns  = 0;    // 상차완료인 저장품 갯수	
				 

					if(!"".equals(sStockId)){
						logger.println(LogLevel.DEBUG,this, "Coil 소재이송상차,소재이송하차,제품이송상차,제품이송하차 처리: "+sStockId);
			  
						
						/*
						 * 저장품 이동조건 셋팅
						 */
						{
							int iReq = dao.updateStockTransInfo(sStockId, "VL"); 
							logger.println(LogLevel.DEBUG,this, "===STOCK_MOVE_TERM = " + "VL");
						}
						
						/*
						 * 2008.01.14 이정훈
						 * 임가공사 이송 완료 구분 추가
						 */
						//*****************************************************************************************
						pmList  = dao.getYmPoFrtoInfo(sStockId);
						if (pmList.size() > 0) {
							
							/*--ym.facilitystatus.facilityinquiry.CraneSchDAO.getYmDmCommonInfoNEW2_PIDEV*/
							dmList  = dao.getYmDmCommonInfoNEW2(sStockId);
							
							for(int inx = 0; inx < dmList.size() ; inx++){
								
						 	 	dmRc   = (JDTORecord)dmList.get(inx);
						 	 	sSmt   = StringHelper.evl(dmRc.getFieldString("STOCK_MOVE_TERM"), "");
						 	 	sCarNo = StringHelper.evl(dmRc.getFieldString("CAR_NO"), "");
						 	 	
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
						    
					    	if(iDmFns == 1){ //상,하차완료인 저장품이 1개일 때 처리
					    		logger.println(LogLevel.DEBUG,this, "상,하차완료인 저장품이 1개일 때 처리");
								String sCarCardNo     = StringHelper.evl(dmRc.getFieldString("CAR_CARD_NO"), "");
					    		String sTransWordNo   = StringHelper.evl(dmRc.getFieldString("TRANS_WORD_NO"), "");
					    	
							    //AB열연 ##############################################################################################	
				
					 
					
						    	if(YmCommonConst.NEW_SCH_WORK_KIND_CVML.equals(sSchCode)|| // COIL 소재이송상차
								   YmCommonConst.NEW_SCH_WORK_KIND_CVM2.equals(sSchCode)|| // COIL 소재이송상차
					      		   YmCommonConst.NEW_SCH_WORK_KIND_CVM3.equals(sSchCode)){ // COIL 소재이송상차
								   	sUpDown = "U";
			
								}else if(YmCommonConst.NEW_SCH_WORK_KIND_CVMU.equals(sSchCode)||// COIL 소재이송하차	 
								         YmCommonConst.NEW_SCH_WORK_KIND_CVM4.equals(sSchCode)||// COIL 소재이송하차	 
				  		   				 YmCommonConst.NEW_SCH_WORK_KIND_CVM5.equals(sSchCode)){// COIL 소재이송하차
									sUpDown = "D";

								}else if(YmCommonConst.NEW_SCH_WORK_KIND_GVML.equals(sSchCode)||// COIL 제품이송상차
										 YmCommonConst.NEW_SCH_WORK_KIND_GVM2.equals(sSchCode)||// COIL 제품이송상차
						   	  		     YmCommonConst.NEW_SCH_WORK_KIND_GVM3.equals(sSchCode)){// COIL 제품이송상차	
									sUpDown = "U";

								}else if(YmCommonConst.NEW_SCH_WORK_KIND_GVMU.equals(sSchCode)||// COIL 제품이송하차
										 YmCommonConst.NEW_SCH_WORK_KIND_GVM4.equals(sSchCode)||// COIL 제품이송하차	
						  		   		 YmCommonConst.NEW_SCH_WORK_KIND_GVM5.equals(sSchCode)){// COIL 제품이송하차			
									sUpDown = "D";

								}
						    	
							    //일관제철 ##############################################################################################	
			                    
								//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			                    //임가공이송상하차개시
			         			JDTORecord tcRecordDM = null;
			         			tcRecordDM = JDTORecordFactory.getInstance().create(); 
			         			tcRecordDM.setField("UPCARUNLOAD_GP", 	sUpDown); 
			                    tcRecordDM.setField("CARD_NO",			sCarCardNo);
			                    tcRecordDM.setField("CAR_NO", 			sCarNo);
			                    tcRecordDM.setField("YD_GP", 			sYardGp);		
			                    tcRecordDM.setField("TRANS_WORD_DATE",	sTransWordNo.length() > 8 ? sTransWordNo.substring(0,8) : sTransWordNo);
			                    tcRecordDM.setField("TRANS_WORD_SEQNO", sTransWordNo.length() > 8 ? sTransWordNo.substring(8) : "");
			         			
			         			//인터페이스 전문 호출
			         			EJBConnector ejbConn1 = new EJBConnector("default","JNDIYardWrkResReg",this);
			         			isSuccess = (Boolean)ejbConn1.trx("getYDDMR020",new Class[]{JDTORecord.class},
			         			  	  	 new Object[]{tcRecordDM}); 
			                    logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 임가공이송상하차개시.===");
			                    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++		                    
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
     *  CTS 에 지시할 전문MESSAGE를 구성한다.
     *  야드L3	Target	CTS L2	I/F방법	JMS	I/F주기	REQ	I/F유형	OnLine
 	 *	T/C	THHT410		 
	 * @param schInfo : CTS 작업지시 INFO
     *
     * @return
     * @throws 
     */	 
	private String setCtsACoilMsgInfoUP(String SaddleName,
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



