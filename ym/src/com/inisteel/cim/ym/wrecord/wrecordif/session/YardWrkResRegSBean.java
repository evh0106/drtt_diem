package com.inisteel.cim.ym.wrecord.wrecordif.session;

import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.io.OutputStream;
import java.net.Socket;

import javax.naming.InitialContext;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.log.LogService;
import jspeed.base.log.LogServiceConfig;
import jspeed.base.log.Logger;
import jspeed.base.util.StringHelper;
import jspeed.base.util.DateHelper;
import jspeed.base.property.PropertyService;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.jms.JmsQueueSender;
import com.inisteel.cim.common.eai.EAIHttpSender;

import com.inisteel.cim.common.util.CommonUtil;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.common.jms.model.CommonModel;
import com.inisteel.cim.common.jms.model.ps.YMPS001;
import com.inisteel.cim.common.jms.model.ps.YMPS002;
import com.inisteel.cim.common.jms.model.dm.YMDM001;
import com.inisteel.cim.common.jms.model.dm.YMDM002;
import com.inisteel.cim.common.jms.model.dm.YMDM003;
import com.inisteel.cim.common.jms.model.dm.YMDM004;
import com.inisteel.cim.common.jms.model.dm.YMDM005;
import com.inisteel.cim.common.jms.model.dm.YMDM006;
import com.inisteel.cim.common.jms.model.dm.YMDM007;
import com.inisteel.cim.common.jms.model.dm.YMDM008;
import com.inisteel.cim.common.jms.model.dm.YMDM009;
import com.inisteel.cim.common.jms.model.dm.YMDM010;
import com.inisteel.cim.common.jms.model.dm.YMDM011;
import com.inisteel.cim.common.jms.model.dm.YMDM012;
import com.inisteel.cim.common.jms.model.dm.YMDM013;
import com.inisteel.cim.common.jms.model.dm.YMDM014;
import com.inisteel.cim.common.jms.model.dm.YMDM015;
import com.inisteel.cim.common.jms.model.pc.ZZPC001;
import com.inisteel.cim.common.jms.model.pm.ZZPM001;
import com.inisteel.cim.common.jms.model.po.YMPO155;
import com.inisteel.cim.common.jms.model.po.YMPO159;
import com.inisteel.cim.common.jms.model.po.YMPO161;
import com.inisteel.cim.common.jms.model.po.YMPO163;
import com.inisteel.cim.common.jms.model.po.YMPO164;
import com.inisteel.cim.common.jms.model.ym.DMYM001;
import com.inisteel.cim.common.jms.model.ym.DMYM002;
import com.inisteel.cim.common.jms.model.ym.DMYM003;
import com.inisteel.cim.common.jms.model.ym.DMYM004;
import com.inisteel.cim.common.jms.model.ym.DMYM005;
import com.inisteel.cim.common.jms.model.ym.DMYM006;
import com.inisteel.cim.common.jms.model.ym.DMYM007;
import com.inisteel.cim.common.jms.model.ym.DMYM008;
import com.inisteel.cim.common.jms.model.ym.DMYM009;
import com.inisteel.cim.common.jms.model.ym.DMYM010;
import com.inisteel.cim.common.jms.model.ym.PCYM001;
import com.inisteel.cim.common.jms.model.ym.PCYM002;
import com.inisteel.cim.common.jms.model.ym.PCYM003;
import com.inisteel.cim.common.jms.model.ym.PMYM001;
import com.inisteel.cim.common.jms.model.ym.PMYM002;
import com.inisteel.cim.common.jms.model.ym.PMYM003;
import com.inisteel.cim.common.jms.model.ym.PMYM004;
import com.inisteel.cim.common.jms.model.ym.PMYM005;
import com.inisteel.cim.common.jms.model.ym.PMYM006;
import com.inisteel.cim.common.jms.model.ym.PMYM007;
import com.inisteel.cim.common.jms.model.ym.PMYM008;
import com.inisteel.cim.common.jms.model.ym.POYM001;
import com.inisteel.cim.common.jms.model.ym.POYM002;
import com.inisteel.cim.common.jms.model.ym.POYM003;
import com.inisteel.cim.common.jms.model.ym.POYM004;
import com.inisteel.cim.common.jms.model.ym.POYM005;
import com.inisteel.cim.common.jms.model.ym.POYM006;
import com.inisteel.cim.common.jms.model.ym.POYM008;
import com.inisteel.cim.common.jms.model.ym.POYM009;
import com.inisteel.cim.common.jms.model.ym.POYM010;	//spm2 최규성
import com.inisteel.cim.common.jms.model.ym.QMYM001;
import com.inisteel.cim.common.jms.model.ym.QMYM002;
import com.inisteel.cim.common.jms.model.ym.QMYM003;
import com.inisteel.cim.common.jms.model.ym.PSYM001;
import com.inisteel.cim.common.jms.model.ym.PSYM002;
import com.inisteel.cim.common.jms.util.SendQueue;
import com.inisteel.cim.common.level2.util.Level2SendQueue;
import com.inisteel.cim.common.level2.util.SendQueueUtil;
import com.inisteel.cim.common.util.CommonUtil;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdEquipDAO;
import com.inisteel.cim.ym.wrecord.wrecordif.dao.YdWRsltDAO;
import com.inisteel.cim.ym.bcommon.dao.YmCommDAO;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.bcommon.util.YmCommUtils;

/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="YardWrkResRegEJB" jndi-name="JNDIYardWrkResReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class YardWrkResRegSBean extends BaseSessionBean {
    private Logger logger 			= null;
    private ymCommonDAO ymCommonDAO = null;
    private YmCommDAO commDAO = null;
    private int len					= 0;

	Boolean isSuccess = new Boolean(false);
    
    private Hashtable hMap = new Hashtable();
    
    private YmCommUtils ymCommUtils = new YmCommUtils();
    
 // SJH    
	private YmComm ymComm = new YmComm();
//	
	public void ejbCreate() {
        LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
        logger 		= new Logger(config);    
        ymCommonDAO = new ymCommonDAO();
        commDAO = new YmCommDAO();
        
        createHashMapInfo();
	}
	 
	///////////////////////////////////////////////////////
	/////일관제철 내부인터페이스 시작 ///////////////////
	///////////////////////////////////////////////////////   
	/**
	* 모델에 따른 도메인 NAME을 리턴한다.
	* @param tcCode	전문코드
	* @return
	*/
	private String getNewDomain(String sTcCode) {
		
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		String sJmsTcCd = StringHelper.evl(sTcCode.substring(2, 4), "YD");
		logger.println(LogLevel.INFO, this,"==일관제철 내부송신 전문["+sJmsTcCd+"]" );
		
		if(YmCommonConst.DOMAIN_BF.equals(sJmsTcCd)) {
			return "jms.queue.BF_MDB_QUEUE";    
		}else if(YmCommonConst.DOMAIN_CM.equals(sJmsTcCd)) {
			return "jms.queue.CM_MDB_QUEUE";    
		}else if(YmCommonConst.DOMAIN_CP.equals(sJmsTcCd)) {
			return "jms.queue.CP_MDB_QUEUE";    
		}else if(YmCommonConst.DOMAIN_CS.equals(sJmsTcCd)) {
			return "jms.queue.CS_MDB_QUEUE";    
		}else if(YmCommonConst.DOMAIN_CT.equals(sJmsTcCd)) {
			return "jms.queue.CT_MDB_QUEUE";    
		}else if(YmCommonConst.DOMAIN_DM.equals(sJmsTcCd)) {
			if("YDDMR001".equals(sTcCode) || "YDDMR004".equals(sTcCode)){
				return "jms.queue.DMA_MDB_QUEUE";  //remote 영업출하 큐   	
			}else {
				return "jms.queue.DMR_MDB_QUEUE";    
			}
		}else if(YmCommonConst.DOMAIN_HM.equals(sJmsTcCd)) {
			return "jms.queue.HM_MDB_QUEUE";    
		}else if(YmCommonConst.DOMAIN_HR.equals(sJmsTcCd)) {
			return "jms.queue.HR_MDB_QUEUE";    
		}else if(YmCommonConst.DOMAIN_MA.equals(sJmsTcCd)) {
			return "jms.queue.MA_MDB_QUEUE";    
		}else if(YmCommonConst.DOMAIN_OR.equals(sJmsTcCd)) {
			return "jms.queue.OR_MDB_QUEUE";    
		}else if(YmCommonConst.DOMAIN_PC.equals(sJmsTcCd)) {
			return "jms.queue.PC_MDB_QUEUE";    
		}else if(YmCommonConst.DOMAIN_PM.equals(sJmsTcCd)) {
			return "jms.queue.PM_MDB_QUEUE";    
		}else if(YmCommonConst.DOMAIN_PO.equals(sJmsTcCd)) {
			return "jms.queue.PO_MDB_QUEUE";    
		}else if(YmCommonConst.DOMAIN_PR.equals(sJmsTcCd)) {
			return "jms.queue.PR_MDB_QUEUE";    
		}else if(YmCommonConst.DOMAIN_PS.equals(sJmsTcCd)) {
			return "jms.queue.PS_MDB_QUEUE";    
		}else if(YmCommonConst.DOMAIN_PT.equals(sJmsTcCd)) {
			return "jms.queue.PT_MDB_QUEUE";    
		}else if(YmCommonConst.DOMAIN_QM.equals(sJmsTcCd)) {
			return "jms.queue.QM_MDB_QUEUE";    
		}else if(YmCommonConst.DOMAIN_SC.equals(sJmsTcCd)) {
			return "jms.queue.SC_MDB_QUEUE";    
		}else if(YmCommonConst.DOMAIN_SM.equals(sJmsTcCd)) {
			return "jms.queue.SM_MDB_QUEUE";    
		}else if(YmCommonConst.DOMAIN_SS.equals(sJmsTcCd)) {
			return "jms.queue.SS_MDB_QUEUE";    
		}else if(YmCommonConst.DOMAIN_TS.equals(sJmsTcCd)) {
			return "jms.queue.TS_MDB_QUEUE";    
		}
		return "jms.queue.YD_MDB_QUEUE";    
	}
	
	///////////////////////////////////////////////////////
	/////B열연수정시작 ///////////////////
	///////////////////////////////////////////////////////  	
	 /**
	 * 오퍼레이션명 : 
	 *
	 * 메시지에서 사용하는 내부인터페이스 송신모듈
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */          
	public boolean sendInternalModel(CommonModel model) {
		logger.println(LogLevel.INFO, this,"==AB 내부송신 전문TEST[]" );
//S	    sendModel(model);	    
//S	    return true;			
	    
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			JDTORecord jrChk = ymComm.getNewModuleEffYn_YM();
			// 신규 적용
			String poChk = jrChk.getFieldString("PO_EFF_YN");
			if(poChk.equals("Y")) {
				String tcCode = model.getTcCode();
				 
//				if(tcCode.equals("YMPO155")) {
//					logger.println(LogLevel.INFO, this,"==AB PO 송신 전문TEST[JDTORECORD 형태전송]" + tcCode);
//					JDTORecord jdto = YmCommUtils.genJDTO1((YMPO155) model);
//					// 인터페이스 전문ID는 JMS_TC_CD 로 공통으로 사용되므로 JMS_TC_CD 의 값을 설정합니다.
//					sendInternalModel(jdto);
//				    return true;				
//				} else if(tcCode.equals("YMPO159")) {
//					logger.println(LogLevel.INFO, this,"==AB PO 송신 전문TEST[JDTORECORD 형태전송]" + tcCode);
//					JDTORecord jdto = YmCommUtils.genJDTO2((YMPO159) model);
//					// 인터페이스 전문ID는 JMS_TC_CD 로 공통으로 사용되므로 JMS_TC_CD 의 값을 설정합니다.
//					sendInternalModel(jdto);
//				    return true;				
//				} else 
				if(tcCode.equals("YMPO161")) {
					logger.println(LogLevel.INFO, this,"==AB PO 송신 전문TEST[JDTORECORD 형태전송]" + tcCode);
					JDTORecord jdto = YmCommUtils.genJDTO3((YMPO161)model);
					// 인터페이스 전문ID는 JMS_TC_CD 로 공통으로 사용되므로 JMS_TC_CD 의 값을 설정합니다.
					sendInternalModel(jdto);
				    return true;				
				} else {
				    sendModel(model);	    
				    return true;				
				}
			} else {
			    sendModel(model);	    
			    return true;				
			}
			
		}catch(Exception e){
		    sendModel(model);	    
		    return true;				
		} 
	}
	
	/**
	* AB열연 기존 인터페이스 
	* @param model 전문모델
	*/
	private void sendModel(CommonModel model) {
	
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
			
			JmsQueueSender sender 		= null;
			PropertyService propertyService	= null;
			String queueName 				= null;
			propertyService 				= PropertyService.getInstance();
			queueName 		= propertyService.getProperty("common.properties",getNewDomain(model.getTcCode()));
			sender 			= new JmsQueueSender();
			sender.initQueueService(queueName);
		
			if(model instanceof YMPO159) {
				sender.send((YMPO159)model);
			}else if(model instanceof YMPO163) {
				sender.send((YMPO163)model);
			}else if(model instanceof YMPO161) {
				sender.send((YMPO161)model);
			}else if(model instanceof YMPO155) {
				sender.send((YMPO155)model);               
			}else if(model instanceof YMPO164) {
				sender.send((YMPO164)model);
			}
		}catch(Exception e){
			throw new EJBServiceException(e);
		} 
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 메시지에서 사용하는 내부인터페이스 송신모듈
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */          
	public boolean sendInternalModel(JDTORecord model) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
	    sendModel(model);	    
	    return true;
	}    
	
	/**
	 * 일관제철 인터페이스 
	 * 오퍼레이션명 : 일관제철 내부인터페이스 
	 *	
		진행관리(송신 4종)	
		YDPTJ001		슬라브소재이송완료실적		[YMDM006]참조    -- 완료(항목차이 있음)
		YDPTJ003		코일소재임가공이송완료실적	[YMDM006]참조   // JNDICCLdWrkOrdReg, callCoilFromToResult YMPO159 밑에 추가 --완료(항목차이 있음)
		YDPTJ004		구입슬라브입고실적			[YMDM006]참조  //보류
		
		생산통제(송신 1종)	
		YDCTJ032	B열연장입진행실적		//보류	
		
		연주조업(송신 1종)	
		YDCSJ001	슬라브수입실적		 삭제
		
		구내운송(송신 7종)	
		YDTSJ007		소재차량상차개시				[YMDM010]참조  //0  전문 layout 상이
		YDTSJ008		소재차량상차완료				[YMDM011]참조  //0  전문 layout 상이
		YDTSJ009		소재차량하차개시				[YMDM010]참조  //0  전문 layout 상이
		YDTSJ010		소재차량하차완료				[YMDM011]참조  //0  전문 layout 상이
		YDTSJ011		소재차량Point지시					//보류	
		YDTSJ012		소재차량Point개폐					//보류
		YDTSJ013		소재차량상하차지연사유             //보류
		
		출하(송신 17종)

			
		YDDMR001	코일입고작업실적			[YMDM001]참조 //0   완료 
		 
		YDDMR004	코일제품이적작업실적		[YMDM008]참조 //0   완료
		
		YDDMR011	코일일품출하상차실적			   신규       완료
		YDDMR007	코일출하상차개시			[YMDM003]참조 //0  완료
		YDDMR015	코일출하상차완료			[YMDM004]참조 //0  완료
		
		YDDMR019	코일제품고간이송상하차개시	[YMDM005]참조 //0  완료
		YDDMR021	코일제품고간이송상하차완료	[YMDM006]참조 //0  항목차이(20회 반복)
			
		YDDMR003	임가공입고작업실적			[YMDM001]참조 //0  완료
		YDDMR020	임가공이송상하차개시		[YMDM005]참조 //0  완료		
		YDDMR022	임가공이송상하차완료		[YMDM006]참조 //0  완료	
			 
		YDDMR013	외판슬라브일품출하상차실적		   신규  완료	
		YDDMR009	외판슬라브출하상차개시		[YMDM010]참조 //0  완료 				 
		YDDMR017	외판슬라브출하상차완료		[YMDM011]참조 //0  완료
		
		YDDMR010	SLAB 운송LOT 편성정보 수신		   신규 //보류
		
		YDDMR024	HYSCO대차이송실적			[YMDM012]참조 //완료
		YDDMR025	HYSCO수냉실적				[YMDM013] //완료
	* @param model 전문모델 
	*/
	private void sendModel(JDTORecord model) {
		
		String sJmsTcCd = StringHelper.evl(model.getFieldString("JMS_TC_CD"), StringHelper.evl(model.getFieldString("TC_CODE"),""));
		logger.println(LogLevel.INFO, this,"==>>>일관제철 내부송신 전문["+sJmsTcCd+"]" );
	
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
			

			{
			JmsQueueSender sender 		= null;
			PropertyService propertyService	= null;
			String queueName 				= null;
			propertyService 				= PropertyService.getInstance();
			
			logger.println(LogLevel.INFO, this,"==>>>getNewDomain: ["+getNewDomain(sJmsTcCd)+"]" );			
			queueName 		= propertyService.getProperty("common.properties",getNewDomain(sJmsTcCd));
			logger.println(LogLevel.INFO, this,"==>>>queueName: ["+queueName+"]" );
			
			sender 			= new JmsQueueSender();
			sender.initQueueService(queueName);
			
			if("DM".equals(StringHelper.evl(sJmsTcCd.substring(2, 4), ""))){
			model.setField("TC_CODE", sJmsTcCd);
			}
			sender.send(model);
			}

		}catch(Exception e){
			throw new EJBServiceException(e);
		} 
	}
	
	///////////////////////////////////////////////////////
	/////일관제철 내부인터페이스 종료 ///////////////////
	//////////////////////////////////////////////////////

	 /**
	 * 오퍼레이션명 : 
	 *
	 * 화면에서 사용하는 내부인터페이스 송신모듈
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public String sendInternalModel(String reSendCnt, String msg, String tcLogId) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
	    logger.println(LogLevel.DEBUG,this,"### 수신: "+ reSendCnt + msg + tcLogId);
	    len			= 25;
        String tc 	= msg.substring(0, 7);
        Map item = ymCommonDAO.readColumnLenOfTc(tc, "ym.common.dao.selectInternalTc");
        
        JDTORecord tcRecord = null;
        tcRecord = JDTORecordFactory.getInstance().create();
        //tcRecord.setField("JMS_TC_CREATE_DDTT", szDate);
        
        if(YmCommonConst.MODEL_YMPC100.equals(tc)) {
            sendModel(getYMPC100(new ZZPC001(), item, msg));
        }else if(YmCommonConst.MODEL_YMPC110.equals(tc)) {
            sendModel(getYMPC110(new ZZPC001(), item, msg));
        }else if(YmCommonConst.MODEL_YMPC120.equals(tc)) {
            sendModel(getYMPC120(new ZZPC001(), item, msg));
        }else if(YmCommonConst.MODEL_YMPC130.equals(tc)) {
            sendModel(getYMPC130(new ZZPC001(), item, msg));
        }else if(YmCommonConst.MODEL_YMPC140.equals(tc)) {
            sendModel(getYMPC140(new ZZPC001(), item, msg));
        }else if(YmCommonConst.MODEL_YMPC150.equals(tc)) {
            sendModel(getYMPC150(new ZZPC001(), item, msg));
        }else if("POPM020".equals(tc)) {
            sendModel(getPOPM020(new ZZPM001(), item, msg));
        }else if(YmCommonConst.MODEL_YMPM001.equals(tc)) {
            sendModel(getYMPM001(new ZZPM001(), item, msg));
        }else if(YmCommonConst.MODEL_YMDM001.equals(tc)) {
            sendModel(getYMDM001(new YMDM001(), item, msg));
        }else if(YmCommonConst.MODEL_YMDM002.equals(tc)) { 
            sendModel(getYMDM002(new YMDM002(), item, msg));
        }else if(YmCommonConst.MODEL_YMDM003.equals(tc)) {
            sendModel(getYMDM003(new YMDM003(), item, msg));
        }else if(YmCommonConst.MODEL_YMDM004.equals(tc)) {
            sendModel(getYMDM004(new YMDM004(), item, msg));
        }else if(YmCommonConst.MODEL_YMDM005.equals(tc)) {
            sendModel(getYMDM005(new YMDM005(), item, msg));
        }else if(YmCommonConst.MODEL_YMDM006.equals(tc)) {
            sendModel(getYMDM006(new YMDM006(), item, msg));
        }else if(YmCommonConst.MODEL_YMDM007.equals(tc)) {
            sendModel(getYMDM007(new YMDM007(), item, msg));
        }else if(YmCommonConst.MODEL_YMDM008.equals(tc)) {
            sendModel(getYMDM008(new YMDM008(), item, msg));
        }else if(YmCommonConst.MODEL_YMDM009.equals(tc)) {
            sendModel(getYMDM009(new YMDM009(), item, msg));
        }else if(YmCommonConst.MODEL_YMDM010.equals(tc)) {
            sendModel(getYMDM010(new YMDM010(), item, msg));
        }else if(YmCommonConst.MODEL_YMDM011.equals(tc)) {
            sendModel(getYMDM011(new YMDM011(), item, msg));
        }else if(YmCommonConst.MODEL_YMDM012.equals(tc)) {
            sendModel(getYMDM012(new YMDM012(), item, msg));
        }else if(YmCommonConst.MODEL_YMDM013.equals(tc)) {
            sendModel(getYMDM013(new YMDM013(), item, msg));
        }else if(YmCommonConst.MODEL_YMDM014.equals(tc)) {
            sendModel(getYMDM014(new YMDM014(), item, msg));
        }else if(YmCommonConst.MODEL_YMDM015.equals(tc)) {
            sendModel(getYMDM015(new YMDM015(), item, msg));
        }else if(YmCommonConst.MODEL_YMPO159.equals(tc)) {
            sendModel(getYMPO159(new YMPO159(), item, msg));
        }else if(YmCommonConst.MODEL_YMPO163.equals(tc)) {
            sendModel(getYMPO163(new YMPO163(), item, msg));
        }else if(YmCommonConst.MODEL_YMPO161.equals(tc)) {
            sendModel(getYMPO161(new YMPO161(), item, msg));
        }else if(YmCommonConst.MODEL_YMPO155.equals(tc)) {
            sendModel(getYMPO155(new YMPO155(), item, msg));
        }else if(YmCommonConst.MODEL_YMPO164.equals(tc)) {
            sendModel(getYMPO164(new YMPO164(), item, msg));
        }else if(YmCommonConst.MODEL_POYM005.equals(tc)) {
//S            sendModel(getPOYM005(new POYM005(), item, msg));
            sendInternalModel(getPOYM005(new POYM005(), item, msg));            
        }else if(YmCommonConst.MODEL_PMYM001.equals(tc)) {
            sendModel(getPMYM001(new PMYM001(), item, msg));
        }else if(YmCommonConst.MODEL_PMYM002.equals(tc)) {
            sendModel(getPMYM002(new PMYM002(), item, msg));
        }else if(YmCommonConst.MODEL_PCYM003.equals(tc)) {
            sendModel(getPCYM003(new PCYM003(), item, msg));
        }else if(YmCommonConst.MODEL_PCYM002.equals(tc)) {
            sendModel(getPCYM002(new PCYM002(), item, msg));
        }else if(YmCommonConst.MODEL_PCYM001.equals(tc)) {
            sendModel(getPCYM001(new PCYM001(), item, msg));
        }else if(YmCommonConst.MODEL_POYM001.equals(tc)) {
//S            sendModel(getPOYM001(new POYM001(), item, msg));
        	sendInternalModel(getPOYM001(new POYM001(), item, msg));
        }else if(YmCommonConst.MODEL_DMYM001.equals(tc)) {
            sendModel(getDMYM001(new DMYM001(), item, msg));
        }else if(YmCommonConst.MODEL_DMYM002.equals(tc)) {
            sendModel(getDMYM002(new DMYM002(), item, msg));
        }else if(YmCommonConst.MODEL_DMYM003.equals(tc)) {
            sendModel(getDMYM003(new DMYM003(), item, msg));
        }else if(YmCommonConst.MODEL_DMYM004.equals(tc)) {
            sendModel(getDMYM004(new DMYM004(), item, msg));
        }else if(YmCommonConst.MODEL_DMYM005.equals(tc)) {
            sendModel(getDMYM005(new DMYM005(), item, msg));
        }else if(YmCommonConst.MODEL_DMYM006.equals(tc)) {
            sendModel(getDMYM006(new DMYM006(), item, msg));
        }else if(YmCommonConst.MODEL_DMYM007.equals(tc)) {
            sendModel(getDMYM007(new DMYM007(), item, msg));
        }else if(YmCommonConst.MODEL_DMYM008.equals(tc)) {
            sendModel(getDMYM008(new DMYM008(), item, msg));
        }else if(YmCommonConst.MODEL_DMYM009.equals(tc)) {
            sendModel(getDMYM009(new DMYM009(), item, msg));
        }else if(YmCommonConst.MODEL_DMYM010.equals(tc)) {
            sendModel(getDMYM010(new DMYM010(), item, msg));
        }else if(YmCommonConst.MODEL_POYM003.equals(tc)) {
//S            sendModel(getPOYM003(new POYM003(), item, msg));
        	sendInternalModel(getPOYM003(new POYM003(), item, msg));
        }else if(YmCommonConst.MODEL_PMYM004.equals(tc)) {
            sendModel(getPMYM004(new PMYM004(), item, msg));
        }else if(YmCommonConst.MODEL_PMYM006.equals(tc)) {
            sendModel(getPMYM006(new PMYM006(), item, msg));
        }else if(YmCommonConst.MODEL_PMYM007.equals(tc)) {
            sendModel(getPMYM007(new PMYM007(), item, msg));
        }else if(YmCommonConst.MODEL_PMYM008.equals(tc)) {
            sendModel(getPMYM008(new PMYM008(), item, msg));
        }else if(YmCommonConst.MODEL_POYM004.equals(tc)) {
//S            sendModel(getPOYM004(new POYM004(), item, msg));
        	sendInternalModel(getPOYM004(new POYM004(), item, msg));
        }else if(YmCommonConst.MODEL_POYM010.equals(tc)) {		// SPM2 최규성
//S        	sendModel(getPOYM010(new POYM010(), item, msg));
        	sendInternalModel(getPOYM010(new POYM010(), item, msg));
        }else if(YmCommonConst.MODEL_QMYM001.equals(tc)) {
            sendModel(getQMYM001(new QMYM001(), item, msg));
        }else if(YmCommonConst.MODEL_QMYM002.equals(tc)) {
            sendModel(getQMYM002(new QMYM002(), item, msg));
        }else if(YmCommonConst.MODEL_QMYM003.equals(tc)) {
            sendModel(getQMYM003(new QMYM003(), item, msg));
        }else if(YmCommonConst.MODEL_POYM006.equals(tc)) {
//S            sendModel(getPOYM006(new POYM006(), item, msg));
        	sendInternalModel(getPOYM006(new POYM006(), item, msg));
        }else if(YmCommonConst.MODEL_PMYM003.equals(tc)) {
            sendModel(getPMYM003(new PMYM003(), item, msg));
        }else if(YmCommonConst.MODEL_PMYM003.equals(tc)) {
            sendModel(getPMYM003(new PMYM003(), item, msg));
        }else if(YmCommonConst.MODEL_PMYM003.equals(tc)) {
            sendModel(getPMYM003(new PMYM003(), item, msg));
        }else if(YmCommonConst.MODEL_POYM002.equals(tc)) {
//S            sendModel(getPOYM002(new POYM002(), item, msg));
        	sendInternalModel(getPOYM002(new POYM002(), item, msg));
        }else if(YmCommonConst.MODEL_PMYM005.equals(tc)) {
            sendModel(getPMYM005(new PMYM005(), item, msg));
        }else if(YmCommonConst.MODEL_POYM008.equals(tc)) {
//S            sendModel(getPOYM008(new POYM008(), item, msg));
        	sendInternalModel(getPOYM008(new POYM008(), item, msg));
        }else if(YmCommonConst.MODEL_YMPO164.equals(tc)) {
            sendModel(getYMPO164(new YMPO164(), item, msg));
        }else if(YmCommonConst.MODEL_POYM009.equals(tc)) {
//S            sendModel(getPOYM009(new POYM009(), item, msg));
        	sendInternalModel(getPOYM009(new POYM009(), item, msg));
        }else if(YmCommonConst.MODEL_YMPS001.equals(tc)) {
            sendModel(getYMPS001(new YMPS001(), item, msg));
	    }else if(YmCommonConst.MODEL_YMPS002.equals(tc)) {
            sendModel(getYMPS002(new YMPS002(), item, msg));            
	 	}else if(YmCommonConst.MODEL_PSYM001.equals(tc)) {
	 		sendModel(getPSYM001(new PSYM001(), item, msg));
	 	}else if(YmCommonConst.MODEL_PSYM002.equals(tc)) {
	 		sendModel(getPSYM002(new PSYM002(), item, msg));
	 	}else if(YmCommonConst.MODEL_YMPM002.equals(tc)) {
	 		sendModel(getYMPM002(new ZZPM001(), item, msg));	 		
	 	}             
        return "모델: 정상적으로 전송하였습니다.";
	}
    /**
     * A열연 SLAB야드 상차지시 편성 요청 추가 (MCH)
     * @param YMPM002
     * @param item
     * @param msg
     * @return
     */
   private CommonModel getYMPM002(ZZPM001 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
            model.setTcCode(YmCommonConst.MODEL_YMPM002);
            model.setTcDate(YmCommonUtil.getStringYMD("-"));
            model.setTcTime(YmCommonUtil.getStringHMS("-"));
            model.setlocation_no(msg.substring(len, getField(item, "Slab_no")).trim());
            return model;
    }
   
     /**
     * @param zzpc001
     * @param item
     * @param msg
     * @return
     */
   private CommonModel getYMPS001(YMPS001 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
            model.setTcCode(YmCommonConst.MODEL_YMPS001);
            model.setTcDate(YmCommonUtil.getStringYMD("-"));
            model.setTcTime(YmCommonUtil.getStringHMS("-"));
            model.setBizOfficeCd(msg.substring(len, getField(item, "bizOfficeCd")).trim());
            model.setYdGp(msg.substring(len, getField(item, "ydGp")).trim());
            model.setCardNo(msg.substring(len, getField(item, "cardNo")).trim());
            model.setPaletteNo(msg.substring(len, getField(item, "paletteNo")).trim());
            model.setDataGp(msg.substring(len, getField(item, "dataGp")).trim());
            model.setFrToMoveOrdDate(msg.substring(len, getField(item, "frToMoveOrdDate")).trim());
            model.setFrToMoveOrdSeqNo(msg.substring(len, getField(item, "frToMoveOrdSeqNo")).trim());
            return model;
    }
   
    private CommonModel getYMPS002(YMPS002 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
            model.setTcCode(YmCommonConst.MODEL_YMPS002);
            model.setTcDate(YmCommonUtil.getStringYMD("-"));
            model.setTcTime(YmCommonUtil.getStringHMS("-"));
            model.setToGate(msg.substring(len, getField(item, "toGate")).trim());
            model.setCardNo(msg.substring(len, getField(item, "cardNo")).trim());
            model.setPaletteNo(msg.substring(len, getField(item, "paletteNo")).trim());
            model.setFrToMoveOrdDate(msg.substring(len, getField(item, "frToMoveOrdDate")).trim());
            model.setFrToMoveOrdSeqNo(msg.substring(len, getField(item, "frToMoveOrdSeqNo")).trim());
            return model;
    } 
   private CommonModel getPSYM001(PSYM001 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
       model.setTcCode(YmCommonConst.MODEL_PSYM001);
       model.setTcDate(YmCommonUtil.getStringYMD("-"));
       model.setTcTime(YmCommonUtil.getStringHMS("-"));
       model.setyardID(msg.substring(len, getField(item, "yardID")).trim());
       model.setBizOfficeCD(msg.substring(len, getField(item, "bizOfficeCD")).trim());
       model.setTransLoadOrderDate(msg.substring(len, getField(item, "transLoadOrderDate")).trim());
       model.setTransLoadOrderSeq(msg.substring(len, getField(item, "transLoadOrderSeq")).trim());
       model.setCardNo(msg.substring(len, getField(item, "cardNo")).trim());
       model.setPalletNo(msg.substring(len, getField(item, "palletNo")).trim());
       model.setDataGp(msg.substring(len, getField(item, "dataGp")).trim());
       return model;
   }
   
   private CommonModel getPSYM002(PSYM002 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
       model.setTcCode(YmCommonConst.MODEL_PSYM002);
       model.setTcDate(YmCommonUtil.getStringYMD("-"));
       model.setTcTime(YmCommonUtil.getStringHMS("-"));
       model.setyardID(msg.substring(len, getField(item, "yardID")).trim());
       model.setTransLoadOrderDate(msg.substring(len, getField(item, "transLoadOrderDate")).trim());
       model.setTransLoadOrderSeq(msg.substring(len, getField(item, "transLoadOrderSeq")).trim());
       model.setCardNo(msg.substring(len, getField(item, "cardNo")).trim());
       model.setPalletNo(msg.substring(len, getField(item, "palletNo")).trim());
       return model;
   }
     /**
     * @param zzpc001
     * @param item
     * @param msg
     * @return
     */
    private CommonModel getYMPC100(ZZPC001 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_YMPC100);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setrealStlNo(msg.substring(len, getField(item, "realStlNo")).trim());
        model.setplanStlNo(msg.substring(len, getField(item, "planStlNo")).trim());
        model.seteventStat(msg.substring(len, getField(item, "eventStat")).trim());
        model.seteventOccurDDTT(msg.substring(len, getField(item, "eventOccurDDTT")).trim());
        return model;
    }

	/**
     * @param zzpc001
     * @param item
     * @param msg
     * @return
     */
    private CommonModel getYMPC110(ZZPC001 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_YMPC100);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setrealStlNo(msg.substring(len, getField(item, "realStlNo")).trim());
        model.setplanStlNo(msg.substring(len, getField(item, "planStlNo")).trim());
        model.seteventStat(msg.substring(len, getField(item, "eventStat")).trim());
        model.seteventOccurDDTT(msg.substring(len, getField(item, "eventOccurDDTT")).trim());
        return model;
    }

	/**
     * @param zzpc001
     * @param item
     * @param msg
     * @return
     */
    private CommonModel getYMPC120(ZZPC001 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_YMPC100);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setrealStlNo(msg.substring(len, getField(item, "realStlNo")).trim());
        model.setplanStlNo(msg.substring(len, getField(item, "planStlNo")).trim());
        model.seteventStat(msg.substring(len, getField(item, "eventStat")).trim());
        model.seteventOccurDDTT(msg.substring(len, getField(item, "eventOccurDDTT")).trim());
        return model;
    }
    
	/**
     * @param zzpc001
     * @param item
     * @param msg
     * @return
     */
    private CommonModel getYMPC130(ZZPC001 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_YMPC100);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setrealStlNo(msg.substring(len, getField(item, "realStlNo")).trim());
        model.setplanStlNo(msg.substring(len, getField(item, "planStlNo")).trim());
        model.seteventStat(msg.substring(len, getField(item, "eventStat")).trim());
        model.seteventOccurDDTT(msg.substring(len, getField(item, "eventOccurDDTT")).trim());
        return model;
    }

	/**
     * @param zzpc001
     * @param item
     * @param msg
     * @return
     */
    private CommonModel getYMPC140(ZZPC001 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_YMPC100);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setrealStlNo(msg.substring(len, getField(item, "realStlNo")).trim());
        model.setplanStlNo(msg.substring(len, getField(item, "planStlNo")).trim());
        model.seteventStat(msg.substring(len, getField(item, "eventStat")).trim());
        model.seteventOccurDDTT(msg.substring(len, getField(item, "eventOccurDDTT")).trim());
        return model;
    }

	/**
     * @param zzpc001
     * @param item
     * @param msg
     * @return
     */
    private CommonModel getYMPC150(ZZPC001 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_YMPC100);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setrealStlNo(msg.substring(len, getField(item, "realStlNo")).trim());
        model.setplanStlNo(msg.substring(len, getField(item, "planStlNo")).trim());
        model.seteventStat(msg.substring(len, getField(item, "eventStat")).trim());
        model.seteventOccurDDTT(msg.substring(len, getField(item, "eventOccurDDTT")).trim());
        return model;
    }

	/**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private PMYM005 getPMYM005(PMYM005 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_PMYM005);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setyardID(msg.substring(len, getField(item, "yardID")).trim());
        model.setCoilNo(msg.substring(len, getField(item, "coilNo")).trim());
        return model;
    }

	/**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private POYM008 getPOYM008(POYM008 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_POYM008);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setyardID(msg.substring(len, getField(item, "yardID")).trim());
        model.setstockid(msg.substring(len, getField(item, "stockid")).trim());
        return model;
    }
	
	/**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private YMPO164 getYMPO164(YMPO164 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_YMPO164);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setcoilNo(msg.substring(len, getField(item, "coilNo")).trim());
        model.setairWaterGp(msg.substring(len, getField(item, "airWaterGp")).trim());
        return model;
    }

	/**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private POYM009 getPOYM009(POYM009 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_POYM009);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setyardID(msg.substring(len, getField(item, "yardID")).trim());
        model.setstockid(msg.substring(len, getField(item, "stockid")).trim());
        return model;
    }

    /**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private POYM002 getPOYM002(POYM002 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_POYM002);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setyardID(msg.substring(len, getField(item, "yardID")).trim());
        model.setCoilNo(msg.substring(len, getField(item, "coilNo")).trim());
        return model;
    }

    /**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private PMYM003 getPMYM003(PMYM003 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_PMYM003);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setyardID(msg.substring(len, getField(item, "yardID")).trim());
        model.setSlabNo(msg.substring(len, getField(item, "slabNo")).trim());
        return model;
    }

	/**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private POYM006 getPOYM006(POYM006 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_POYM006);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setyardID(msg.substring(len, getField(item, "yardID")).trim());
        model.setSlabNo(msg.substring(len, getField(item, "slabNo")).trim());
        return model;
    }

	/**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private DMYM008 getDMYM008(DMYM008 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_DMYM008);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setyardID(msg.substring(len, getField(item, "yardID")));
        model.setTransLoadOrder(msg.substring(len, getField(item, "transLoadOrder")).trim());
        model.setTransLoadOrderDate(msg.substring(len, getField(item, "transLoadOrderDate")).trim());
        model.setTransLoadOrderSeq(msg.substring(len, getField(item, "transLoadOrderSeq")).trim());
        return model;
    }

	/**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private DMYM009 getDMYM009(DMYM009 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_DMYM009);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setyardID(msg.substring(len, getField(item, "yardID")));
        model.setTransOrderId(msg.substring(len, getField(item, "transOrderId")));
        model.setTranOrderDate(msg.substring(len, getField(item, "tranOrderDate")));
        model.setTranOrderSeq(msg.substring(len, getField(item, "tranOrderSeq")));
        return model;
    }

	/**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private DMYM010 getDMYM010(DMYM010 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_DMYM010);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setyardID(msg.substring(len, getField(item, "yardID")));
        model.setstockid(msg.substring(len, getField(item, "stockid")));
        return model;
    }

    /**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private QMYM001 getQMYM001(QMYM001 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_QMYM001);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setyardID(msg.substring(len, getField(item, "yardID")).trim());
        model.setProcessId(msg.substring(len, getField(item, "ProcessId")).trim());
        model.setSlabNo(msg.substring(len, getField(item, "slabNo")).trim());
        return model;
    }
	
	/**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private QMYM002 getQMYM002(QMYM002 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_QMYM002);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setyardID(msg.substring(len, getField(item, "yardID")).trim());
        model.setcoilNo(msg.substring(len, getField(item, "coilNo")).trim());
        return model;
    }
    
    /**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private QMYM003 getQMYM003(QMYM003 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_QMYM003);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setyardID(msg.substring(len, getField(item, "yardID")).trim());
        model.setStockId(msg.substring(len, getField(item, "stockId")).trim());
        return model;
    }
    
	/**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private POYM004 getPOYM004(POYM004 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_POYM004);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setYardId(msg.substring(len, getField(item, "YardId")).trim());
        model.setWorkId(msg.substring(len, getField(item, "WorkId")).trim());
        model.setProcessId(msg.substring(len, getField(item, "ProcessId")).trim());
        model.setCoilNo(msg.substring(len, getField(item, "coilNo")).trim());
        model.setPosition(msg.substring(len, getField(item, "position")).trim());
        return model;
    }
	// SPM2 최규성
    /**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private POYM010 getPOYM010(POYM010 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_POYM010);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setYardId(msg.substring(len, getField(item, "YardId")).trim());
        model.setWorkId(msg.substring(len, getField(item, "WorkId")).trim());
        model.setProcessId(msg.substring(len, getField(item, "ProcessId")).trim());
        model.setCoilNo(msg.substring(len, getField(item, "coilNo")).trim());
        model.setPosition(msg.substring(len, getField(item, "position")).trim());
        return model;
    }
	/**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private PMYM007 getPMYM007(PMYM007 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_PMYM007);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setyardID(msg.substring(len, getField(item, "yardID")).trim());
        model.setCoilNo(msg.substring(len, getField(item, "coilNo")).trim());
        return model;
    }

	/**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private PMYM006 getPMYM006(PMYM006 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_PMYM006);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setyardID(msg.substring(len, getField(item, "yardID")).trim());
        model.setFrtomoveWordDate(msg.substring(len, getField(item, "frtomoveWordDate")).trim());
        model.setFrtomoveWordSeqno(msg.substring(len, getField(item, "frtomoveWordSeqno")).trim());
        return model;
    }

	/**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private PMYM008 getPMYM008(PMYM008 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_PMYM008);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setPlantGp(msg.substring(len, getField(item, "plantGp")).trim());
        model.setProcGp(msg.substring(len, getField(item, "procGp")).trim());
        model.setWordUnitName(msg.substring(len, getField(item, "wordUnitName")).trim());
        return model;
    }

	/**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private POYM003 getPOYM003(POYM003 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_POYM003);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setPlantGp(msg.substring(len, getField(item, "plantGp")).trim());
        model.setProcGp(msg.substring(len, getField(item, "procGp")).trim());
        model.setWordUnitName(msg.substring(len, getField(item, "wordUnitName")).trim());
        return model;
    }

	/**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private PMYM004 getPMYM004(PMYM004 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_PMYM004);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setyardID(msg.substring(len, getField(item, "yardID")).trim());
        model.setRollingOrderDate(msg.substring(len, getField(item, "rollingOrderDate")).trim());
        model.setProgressCode(msg.substring(len, getField(item, "progressCode")).trim());
        return model;
    }

	/**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private DMYM004 getDMYM004(DMYM004 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_DMYM004);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setyardID(msg.substring(len, getField(item, "yardID")).trim());
        model.setMaterialgoods(msg.substring(len, getField(item, "materialgoods")).trim());
        model.setTransLoadOrder(msg.substring(len, getField(item, "transLoadOrder")).trim());
        model.setTransLoadOrderDate(msg.substring(len, getField(item, "transLoadOrderDate")).trim());
        model.setTransLoadOrderSeq(msg.substring(len, getField(item, "transLoadOrderSeq")).trim());
        return model;
    }

	/**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private DMYM003 getDMYM003(DMYM003 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_DMYM003);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setyardID(msg.substring(len, getField(item, "yardID")).trim());
        model.setTransLoadOrder(msg.substring(len, getField(item, "transLoadOrder")).trim());
        model.setTransLoadOrderDate(msg.substring(len, getField(item, "transLoadOrderDate")).trim());
        model.setTransLoadOrderSeq(msg.substring(len, getField(item, "transLoadOrderSeq")).trim());
        return model;
    }

	/**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private DMYM002 getDMYM002(DMYM002 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_DMYM002);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setyardID(msg.substring(len, getField(item, "yardID")).trim());
        model.setDistLoadOrder(msg.substring(len, getField(item, "distLoadOrder")).trim());
        model.setDistLoadOrderDate(msg.substring(len, getField(item, "distLoadOrderDate")).trim());
        model.setDistLoadOrderSeq(msg.substring(len, getField(item, "distLoadOrderSeq")).trim());
        return model;
    }

	/**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private DMYM001 getDMYM001(DMYM001 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_DMYM001);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setyardID(msg.substring(len, getField(item, "yardID")).trim());
        model.setMaterialgoods(msg.substring(len, getField(item, "materialgoods")).trim());
        model.setTransOrderId(msg.substring(len, getField(item, "transOrderId")).trim());
        model.setTranOrderDate(msg.substring(len, getField(item, "tranOrderDate")).trim());
        model.setTranOrderSeq(msg.substring(len, getField(item, "tranOrderSeq")).trim());
        return model;
    }

	/**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private DMYM005 getDMYM005(DMYM005 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_DMYM005);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setyardID(msg.substring(len, getField(item, "yardID")).trim());
        model.setGoodsEa(msg.substring(len, getField(item, "goodsEa")).trim());
        model.setGoodsNo(msg.substring(len, getField(item, "goodsNo")).trim());
        model.setkeepstockflag(msg.substring(len, getField(item, "keepstockflag")).trim());
        return model;
    }

	/**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private DMYM006 getDMYM006(DMYM006 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_DMYM006);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setyardID(msg.substring(len, getField(item, "yardID")).trim());
        model.setProcessId(msg.substring(len, getField(item, "processId")).trim());
        model.setGoodsNo(msg.substring(len, getField(item, "goodsNo")).trim());
        return model;
    }

    /**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private DMYM007 getDMYM007(DMYM007 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_DMYM007);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setyardID(msg.substring(len, getField(item, "yardID")).trim());
        model.setGoodsEa(msg.substring(len, getField(item, "goodsEa")).trim());
        model.setGoodsNo(msg.substring(len, getField(item, "goodsNo")).trim());
        return model;
    }

    /**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private POYM001 getPOYM001(POYM001 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_POYM001);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setProcessID(msg.substring(len, getField(item, "ProcessID")).trim());
        model.setCoilNo(msg.substring(len, getField(item, "coilNo")).trim());
        model.setYardID(msg.substring(len, getField(item, "yardID")).trim());
        model.setProcessCode(msg.substring(len, getField(item, "processCode")).trim());
        return model;
    }

	/**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private PCYM001 getPCYM001(PCYM001 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_PCYM001);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setyardID(msg.substring(len, getField(item, "yardID")).trim());
        model.setSlabNo(msg.substring(len, getField(item, "slabNo")).trim());
        return model;
    }

	/**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private PCYM003 getPCYM003(PCYM003 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_PCYM003);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setyardID(msg.substring(len, getField(item, "yardID")).trim());
        model.setSlabNo(msg.substring(len, getField(item, "slabNo")).trim());
        return model;
    }

	/**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private PCYM002 getPCYM002(PCYM002 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_PCYM002);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setyardID(msg.substring(len, getField(item, "yardID")).trim());
        model.setSlabNo(msg.substring(len, getField(item, "slabNo")).trim());
        return model;
    }

    /**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private PMYM002 getPMYM002(PMYM002 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_PMYM002);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setyardID(msg.substring(len, getField(item, "yardID")).trim());
        model.setSlabNo(msg.substring(len, getField(item, "slabNo")).trim());
        return model;
    }

	/**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private PMYM001 getPMYM001(PMYM001 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_PMYM001);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setyardID(msg.substring(len, getField(item, "yardID")).trim());
        model.setTransOrderDate(msg.substring(len, getField(item, "transOrderDate")).trim());
        model.settransLoadOrderSeq(msg.substring(len, getField(item, "transLoadOrderSeq")).trim());
        return model;
    }

	/**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private POYM005 getPOYM005(POYM005 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_POYM005);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setyardID(msg.substring(len, getField(item, "yardID")).trim());
        model.setProcessID(msg.substring(len, getField(item, "ProcessId")).trim());
        model.setSlabNo(msg.substring(len, getField(item, "slabNo")).trim());
        return model;
    }

    /**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private ZZPM001 getPOPM020(ZZPM001 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode("POPM020");
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setStl_no(msg.substring(len, getField(item, "Stl_no")).trim());
        model.setPlant_gp(msg.substring(len, getField(item, "Plant_gp")).trim());
        model.setOrd_no(msg.substring(len, getField(item, "Ord_no")).trim());
        model.setOrd_dtl(msg.substring(len, getField(item, "Ord_dtl")).trim());
        model.setOrd_yeojae_gp(msg.substring(len, getField(item, "Ord_yeojae_gp")).trim());
        model.setStl_prog_cd(msg.substring(len, getField(item, "Stl_prog_cd")).trim());
        model.setStl_wt(Double.parseDouble(msg.substring(len, getField(item, "Stl_wt")).trim()));
        model.setYeojae_cause_cd(msg.substring(len, getField(item, "Yeojae_cause_cd")).trim());
        model.setHold_stl_stamp_gp(msg.substring(len, getField(item, "Hold_stl_stamp_gp")).trim());
        model.setSlab_no(msg.substring(len, getField(item, "Slab_no")).trim());      
        return model;
    }

    /**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private ZZPM001 getYMPM001(ZZPM001 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_YMPM001);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setTc_occur_pgm(msg.substring(len, getField(item, "Tc_occur_pgm")).trim());
        model.setTc_occur_ddtt(msg.substring(len, getField(item, "Tc_occur_ddtt")).trim());
        model.setStl_no(msg.substring(len, getField(item, "Stl_no")).trim());
        model.setPlant_gp(msg.substring(len, getField(item, "Plant_gp")).trim());
        model.setOrd_no(msg.substring(len, getField(item, "Ord_no")).trim());
        model.setOrd_dtl(msg.substring(len, getField(item, "Ord_dtl")).trim());
        model.setOrd_yeojae_gp(msg.substring(len, getField(item, "Ord_yeojae_gp")).trim());
        model.setStl_prog_cd(msg.substring(len, getField(item, "Stl_prog_cd")).trim());
        model.setStl_wt(Double.parseDouble(msg.substring(len, getField(item, "Stl_wt")).trim()));
        model.setScarfing_yn(msg.substring(len, getField(item, "Scarfing_yn")).trim());
        return model;
    }

    /**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private ZZPC001 getZZPC001(ZZPC001 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(model.getTcCode());
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setrealStlNo(msg.substring(len, getField(item, "realStlNo")).trim());
        model.setplanStlNo(msg.substring(len, getField(item, "planStlNo")).trim());
        model.seteventStat(msg.substring(len, getField(item, "eventStat")).trim());
        model.seteventOccurDDTT(msg.substring(len, getField(item, "eventOccurDDTT")).trim());
        return model;
    }

    /**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private YMPO155 getYMPO155(YMPO155 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_YMPO155);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setslabNo(msg.substring(len, getField(item, "slabNo")).trim());
        model.setupDownGbn(msg.substring(len, getField(item, "upDownGbn")).trim());
        model.setupDownDate(msg.substring(len, getField(item, "upDownDate")).trim());
        model.setupDownLoc(msg.substring(len, getField(item, "upDownLoc")).trim());
        return model;
    }

    /**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private YMPO161 getYMPO161(YMPO161 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_YMPO161);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setplantGbn(msg.substring(len, getField(item, "plantGbn")).trim());
        model.setprocGbn(msg.substring(len, getField(item, "procGbn")).trim());
        model.setcoilNo(msg.substring(len, getField(item, "coilNo")).trim());
        model.setProcessId(msg.substring(len, getField(item, "processId")).trim());
        model.setdownDate(msg.substring(len, getField(item, "downDate")).trim());
        model.setdownTime(msg.substring(len, getField(item, "downTime")).trim());
        model.setpositionNo(msg.substring(len, getField(item, "positionNo")).trim());
        return model;
    }

    /**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private YMPO163 getYMPO163(YMPO163 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_YMPO163);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setcoilNo(msg.substring(len, getField(item, "coilNo")).trim());
        model.setdownDate(msg.substring(len, getField(item, "downDate")).trim());
        model.setdownTime(msg.substring(len, getField(item, "downTime")).trim());
        return model;
    }

    /**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private YMPO159 getYMPO159(YMPO159 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_YMPO159);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setcoilNo(msg.substring(len, getField(item, "coilNo")).trim());
        model.setupDownGbn(msg.substring(len, getField(item, "upDownGbn")).trim());
        model.setupDownDate(msg.substring(len, getField(item, "upDownDate")).trim());
        model.setupDownLoc(msg.substring(len, getField(item, "upDownLoc")).trim());
        return model;
    }

      /**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private YMDM015 getYMDM015(YMDM015 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_YMDM015);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setUPCARUNLOAD_GP(msg.substring(len, getField(item, "UPCARUNLOAD_GP")).trim());
        model.setCARD_NO(msg.substring(len, getField(item, "CARD_NO")).trim());
        model.setCAR_NO(msg.substring(len, getField(item, "CAR_NO")).trim());
        model.setCARLOAD_END_DATE(msg.substring(len, getField(item, "CARLOAD_END_DATE")).trim());
        model.setCARLOAD_END_TIME(msg.substring(len, getField(item, "CARLOAD_END_TIME")).trim());
        model.setTRANS_WORD_DATE(msg.substring(len, getField(item, "TRANS_WORD_DATE")).trim());
        model.setTRANS_WORD_SEQNO(msg.substring(len, getField(item, "TRANS_WORD_SEQNO")).trim());
        return model;
    }

    /**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private YMDM014 getYMDM014(YMDM014 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_YMDM014);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setUPCARUNLOAD_GP(msg.substring(len, getField(item, "UPCARUNLOAD_GP")).trim());
        model.setCARD_NO(msg.substring(len, getField(item, "CARD_NO")).trim());
        model.setCAR_NO(msg.substring(len, getField(item, "CAR_NO")).trim());
        model.setCARLOAD_START_DATE(msg.substring(len, 
                getField(item, "CARLOAD_START_DATE")).trim());
        model.setCARLOAD_START_TIME(msg.substring(len, 
                getField(item, "CARLOAD_START_TIME")).trim());
        model.setTRANS_WORD_DATE(msg.substring(len, getField(item, "TRANS_WORD_DATE")).trim());
        model.setTRANS_WORD_SEQNO(msg.substring(len, getField(item, "TRANS_WORD_SEQNO")).trim());
        return model;
    }
    	
    /**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private YMDM012 getYMDM012(YMDM012 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_YMDM012);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setGOODS_NO(msg.substring(len, getField(item, "GOODS_NO")).trim());
        model.setFROM_STORE_LOC_CD(msg.substring(len, getField(item, "FROM_STORE_LOC_CD")).trim());
        model.setTRANS_DATE(msg.substring(len, getField(item, "TRANS_DATE")).trim());
        model.setTRANS_TIME(msg.substring(len, getField(item, "TRANS_TIME")).trim());
        return model;
    }
    
    /**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private YMDM013 getYMDM013(YMDM013 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_YMDM013);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setGOODS_NO(msg.substring(len, getField(item, "GOODS_NO")).trim());
        return model;
    }

    /**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private YMDM011 getYMDM011(YMDM011 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_YMDM011);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setUPCARUNLOAD_GP(msg.substring(len, getField(item, "UPCARUNLOAD_GP")).trim());
        model.setCARD_NO(msg.substring(len, getField(item, "CARD_NO")).trim());
        model.setCAR_NO(msg.substring(len, getField(item, "CAR_NO")).trim());
        model.setCARLOAD_END_DATE(msg.substring(len, getField(item, "CARLOAD_END_DATE")).trim());
        model.setCARLOAD_END_TIME(msg.substring(len, getField(item, "CARLOAD_END_TIME")).trim());
        model.setTRANS_WORD_DATE(msg.substring(len, getField(item, "TRANS_WORD_DATE")).trim());
        model.setTRANS_WORD_SEQNO(msg.substring(len, getField(item, "TRANS_WORD_SEQNO")).trim());
        return model;
    }

    /**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private YMDM010 getYMDM010(YMDM010 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_YMDM010);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setUPCARUNLOAD_GP(msg.substring(len, getField(item, "UPCARUNLOAD_GP")).trim());
        model.setCARD_NO(msg.substring(len, getField(item, "CARD_NO")).trim());
        model.setCAR_NO(msg.substring(len, getField(item, "CAR_NO")).trim());
        model.setCARLOAD_START_DATE(msg.substring(len, 
                getField(item, "CARLOAD_START_DATE")).trim());
        model.setCARLOAD_START_TIME(msg.substring(len, 
                getField(item, "CARLOAD_START_TIME")).trim());
        model.setTRANS_WORD_DATE(msg.substring(len, getField(item, "TRANS_WORD_DATE")).trim());
        model.setTRANS_WORD_SEQNO(msg.substring(len, getField(item, "TRANS_WORD_SEQNO")).trim());
        return model;
    }

    /**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private YMDM009 getYMDM009(YMDM009 model, Map item, String msg) {
    	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
        model.setTcCode(YmCommonConst.MODEL_YMDM009);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setSLAB_NO(msg.substring(len, getField(item, "SLAB_NO")).trim());
        model.setRECEIPT_DATE(msg.substring(len, getField(item, "RECEIPT_DATE")).trim());
        model.setRECEIPT_TIME(msg.substring(len, getField(item, "RECEIPT_TIME")).trim());
        model.setCURR_PROG_CD(msg.substring(len, getField(item, "CURR_PROG_CD")).trim());
        model.setYD_GP(msg.substring(len, getField(item, "YD_GP")).trim());
        model.setBAY(msg.substring(len, getField(item, "BAY")).trim());
        model.setSPAN(msg.substring(len, getField(item, "SPAN")).trim());
        model.setCOL(msg.substring(len, getField(item, "COL")).trim());
        model.setCELLNO(msg.substring(len, getField(item, "CELLNO")).trim());
        model.setSTACK_LAYER(msg.substring(len, getField(item, "STACK_LAYER")).trim());
        model.setSTORE_LOC_CD(msg.substring(len, getField(item, "STORE_LOC_CD")).trim());
        return model;
    }

    /**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private YMDM008 getYMDM008(YMDM008 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_YMDM008);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setGOODS_NO(msg.substring(len, getField(item, "GOODS_NO")).trim());
        model.setBEFO_STORE_LOC(msg.substring(len, getField(item, "BEFO_STORE_LOC")).trim());
        model.setSTORE_LOC(msg.substring(len, getField(item, "STORE_LOC")).trim());
        model.setMOVENSTACK_DATE(msg.substring(len, getField(item, "MOVENSTACK_DATE")).trim());
        model.setMOVENSTACK_TIME(msg.substring(len, getField(item, "MOVENSTACK_TIME")).trim());
        return model;
    }

    /**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private YMDM007 getYMDM007(YMDM007 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_YMDM007);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setGOODS_NO(msg.substring(len, getField(item, "GOODS_NO")).trim());
        model.setYD_GP(msg.substring(len, getField(item, "YD_GP")).trim());
        model.setSTORE_LOC(msg.substring(len, getField(item, "STORE_LOC")).trim());
        model.setTAKEIN_DATE(msg.substring(len, getField(item, "TAKEIN_DATE")).trim());
        model.setTAKEIN_TIME(msg.substring(len, getField(item, "TAKEIN_TIME")).trim());
        model.setTRANS_WRSLT_DATE(msg.substring(len, getField(item, "TRANS_WRSLT_DATE")).trim());
        model.setTRANS_WRSLT_SEQNO(msg.substring(len, getField(item, "TRANS_WRSLT_SEQNO")).trim());
        return model;
    }

    /**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private YMDM006 getYMDM006(YMDM006 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_YMDM006);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setMATERIAL_GOODS(msg.substring(len, getField(item, "MATERIAL_GOODS")).trim());
        model.setUPCARUNLOAD_GP(msg.substring(len, getField(item, "UPCARUNLOAD_GP")).trim());
        model.setTRANS_WORD_DATE(msg.substring(len, getField(item, "TRANS_WORD_DATE")).trim());
        model.setTRANS_WORD_SEQNO(msg.substring(len, getField(item, "TRANS_WORD_SEQNO")).trim());
        model.setCARD_NO(msg.substring(len, getField(item, "CARD_NO")).trim());
        model.setCAR_NO(msg.substring(len, getField(item, "CAR_NO")).trim());
        model.setCARLOAD_DONE_DATE(msg.substring(len, getField(item, "CARLOAD_DONE_DATE")).trim());
        model.setCARLOAD_DONE_TIME(msg.substring(len, getField(item, "CARLOAD_DONE_TIME")).trim());
        return model;
    }

    /**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private YMDM005 getYMDM005(YMDM005 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_YMDM005);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setUPCARUNLOAD_GP(msg.substring(len, getField(item, "UPCARUNLOAD_GP")).trim());
        model.setCARD_NO(msg.substring(len, getField(item, "CARD_NO")).trim());
        model.setCAR_NO(msg.substring(len, getField(item, "CAR_NO")).trim());
        model.setCARLOAD_START_DATE(msg.substring(len, 
                getField(item, "CARLOAD_START_DATE")).trim());
        model.setCARLOAD_START_TIME(msg.substring(len, 
                getField(item, "CARLOAD_START_TIME")).trim());
        model.setTRANS_WORD_DATE(msg.substring(len, getField(item, "TRANS_WORD_DATE")).trim());
        model.setTRANS_WORD_SEQNO(msg.substring(len, getField(item, "TRANS_WORD_SEQNO")).trim());
        return model;
    }

    /**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private YMDM004 getYMDM004(YMDM004 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_YMDM004);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setCARD_NO(msg.substring(len, getField(item, "CARD_NO")).trim());
        model.setCAR_NO(msg.substring(len, getField(item, "CAR_NO")).trim());
        model.setCARLOAD_DONE_DATE(msg.substring(len, getField(item, "CARLOAD_DONE_DATE")).trim());
        model.setCARLOAD_DONE_TIME(msg.substring(len, getField(item, "CARLOAD_DONE_TIME")).trim());
        model.setTRANS_WORD_DATE(msg.substring(len, getField(item, "TRANS_WORD_DATE")).trim());
        model.setTRANS_WORD_SEQNO(msg.substring(len, getField(item, "TRANS_WORD_SEQNO")).trim());
        return model;
    }

    /**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private YMDM003 getYMDM003(YMDM003 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_YMDM003);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setCARD_NO(msg.substring(len, getField(item, "CARD_NO")).trim());
        model.setCAR_NO(msg.substring(len, getField(item, "CAR_NO")).trim());
        model.setCARLOAD_START_DATE(msg.substring(len, 
                getField(item, "CARLOAD_START_DATE")).trim());
        model.setCARLOAD_START_TIME(msg.substring(len, 
                getField(item, "CARLOAD_START_TIME")).trim());
        model.setTRANS_WORD_DATE(msg.substring(len, getField(item, "TRANS_WORD_DATE")).trim());
        model.setTRANS_WORD_SEQNO(msg.substring(len, getField(item, "TRANS_WORD_SEQNO")).trim());
        return model;
    }

    /**
     * @param model
     * @param item
     * @param msg
     * @return
     */
    private YMDM002 getYMDM002(YMDM002 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_YMDM002);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setCARD_NO(msg.substring(len, getField(item, "CARD_NO")).trim());
        model.setCAR_NO(msg.substring(len, getField(item, "CAR_NO")).trim());
        model.setBAYIN_DATE(msg.substring(len, getField(item, "BAYIN_DATE")).trim());
        model.setBAYIN_TIME(msg.substring(len, getField(item, "BAYIN_TIME")).trim());
        model.setTRANS_WORD_DATE(msg.substring(len, getField(item, "TRANS_WORD_DATE")).trim());
        model.setTRANS_WORD_SEQNO(msg.substring(len, getField(item, "TRANS_WORD_SEQNO")).trim());
        return model;
    }

    /**
     * @param ymdm002
     * @param item
     * @param msg
     * @return
     */
    private YMDM001 getYMDM001(YMDM001 model, Map item, String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        model.setTcCode(YmCommonConst.MODEL_YMDM001);
        model.setTcDate(YmCommonUtil.getStringYMD("-"));
        model.setTcTime(YmCommonUtil.getStringHMS("-"));
        model.setYD_GP(msg.substring(len, getField(item, "YD_GP")));
        model.setRECEIPT_DATE(msg.substring(len, getField(item, "RECEIPT_DATE")).trim());
        model.setRECEIPT_TIME(msg.substring(len, getField(item, "RECEIPT_TIME")).trim());
        model.setGOODS_NO(msg.substring(len, getField(item, "GOODS_NO")).trim());
        model.setSTORE_LOC(msg.substring(len, getField(item, "STORE_LOC")).trim());
        return model;
    }
    
    private int getField(Map item, String name) {
        len  += Integer.parseInt((String)item.get(name));
        return len;
    }
    
    /**
     * name parameter에 대한 값을 반환한다.
     * @param data	
     * @param name  
     * @return
     */
    private int getFieldLen(Map data, String name) {
        if(data != null) {
            return StringHelper.parseInt((String)data.get(name), 0);
        }
        return 0;
    }
    
	///////////////////////////////////////////////////////////////////////////////
    /**
	 * 출하 또는 조업으로 전문을 송신한다.
     * @param model 전문모델
     */
    private void sendModel_AB(CommonModel model) {
        
        SendQueue sendQueue = null;
        try {
    		/*
    		 * 구자원 단계별 삭제 로직  
    		 */
    		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
    		if(sAPP060_OLDSRC_YN.equals("Y")){
    			return;
    		}
    		
        	//제철 jms송신부 수정(YMPO)
        	JmsQueueSender sender 		= null;
        	PropertyService propertyService	= null;
        	String queueName 				= null;
        	propertyService 				= PropertyService.getInstance();
        	queueName 		= propertyService.getProperty("common.properties",getNewDomain(model.getTcCode()));
        	sender 			= new JmsQueueSender();
        	sender.initQueueService(queueName);
        	
        	// 기존 AB열연 
       //     sendQueue 		= new SendQueue(getDomain(model.getTcCode()));
      //      InitialContext ic		= sendQueue.getInitailContext();
       //     sendQueue.init(ic);
            
            if(model instanceof YMDM001) {
                sendQueue.send((YMDM001)model);    
            }else if(model instanceof YMDM002) {
                sendQueue.send((YMDM002)model);
            }else if(model instanceof YMDM003) {
                sendQueue.send((YMDM003)model);
            }else if(model instanceof YMDM004) {
                sendQueue.send((YMDM004)model);
            }else if(model instanceof YMDM005) {
                sendQueue.send((YMDM005)model);
            }else if(model instanceof YMDM006) {
                sendQueue.send((YMDM006)model);
            }else if(model instanceof YMDM007) {
                sendQueue.send((YMDM007)model);
            }else if(model instanceof YMDM008) {
                sendQueue.send((YMDM008)model);
            }else if(model instanceof YMDM009) {
                sendQueue.send((YMDM009)model);
            }else if(model instanceof YMDM010) {
                sendQueue.send((YMDM010)model);
            }else if(model instanceof YMDM011) {
                sendQueue.send((YMDM011)model);
            }else if(model instanceof YMDM012) {
                sendQueue.send((YMDM012)model);
            }else if(model instanceof YMDM013) {
                sendQueue.send((YMDM013)model);
            }else if(model instanceof YMDM014) {
                sendQueue.send((YMDM014)model);
            }else if(model instanceof YMDM015) {
                sendQueue.send((YMDM015)model);
            }else if(model instanceof YMPO159) {
            	sender.send((YMPO159)model);
            }else if(model instanceof YMPO163) {
            	sender.send((YMPO163)model);
            }else if(model instanceof YMPO161) {
            	sender.send((YMPO161)model);
            }else if(model instanceof YMPO155) {
            	sender.send((YMPO155)model);               
            }else if(model instanceof POYM005) {
                sendQueue.send((POYM005)model);
            }else if(model instanceof PMYM001) {
                sendQueue.send((PMYM001)model);
            }else if(model instanceof PMYM002) {
                sendQueue.send((PMYM002)model);
            }else if(model instanceof PCYM003) {
                sendQueue.send((PCYM003)model);
            }else if(model instanceof PCYM002) {
                sendQueue.send((PCYM002)model);
            }else if(model instanceof PCYM001) {
                sendQueue.send((PCYM001)model);
            }else if(model instanceof POYM001) {
                sendQueue.send((POYM001)model);
            }else if(model instanceof DMYM001) {
                sendQueue.send((DMYM001)model);
            }else if(model instanceof DMYM002) {
                sendQueue.send((DMYM002)model);
            }else if(model instanceof DMYM003) {
                sendQueue.send((DMYM003)model);
            }else if(model instanceof DMYM004) {
                sendQueue.send((DMYM004)model);
            }else if(model instanceof DMYM005) {
                sendQueue.send((DMYM005)model);
            }else if(model instanceof DMYM006) {
                sendQueue.send((DMYM006)model);
            }else if(model instanceof DMYM007) {
                sendQueue.send((DMYM007)model);
            }else if(model instanceof DMYM008) {
                sendQueue.send((DMYM008)model);
            }else if(model instanceof DMYM009) {
                sendQueue.send((DMYM009)model);
            }else if(model instanceof DMYM010) {
                sendQueue.send((DMYM010)model);
            }else if(model instanceof PMYM004) {
                sendQueue.send((PMYM004)model);
            }else if(model instanceof POYM003) {
                sendQueue.send((POYM003)model);
            }else if(model instanceof PMYM008) {
                sendQueue.send((PMYM008)model);
            }else if(model instanceof PMYM006) {
                sendQueue.send((PMYM006)model);
            }else if(model instanceof PMYM007) {
                sendQueue.send((PMYM007)model);
            }else if(model instanceof POYM004) {
                sendQueue.send((POYM004)model);
            }else if(model instanceof POYM010) {		// SPM2 최규성
            	sendQueue.send((POYM010)model);
            }else if(model instanceof QMYM001) {
                sendQueue.send((QMYM001)model);
            }else if(model instanceof QMYM002) {
                sendQueue.send((QMYM002)model);
            }else if(model instanceof QMYM003) {
                sendQueue.send((QMYM003)model);        
            }else if(model instanceof POYM006) {
                sendQueue.send((POYM006)model);
            }else if(model instanceof PMYM003) {
                sendQueue.send((PMYM003)model);
            }else if(model instanceof ZZPM001) {
                sendQueue.send((ZZPM001)model);
            }else if(model instanceof ZZPC001) {
                sendQueue.send((ZZPC001)model);
            }else if(model instanceof YMPO164) {
            	sender.send((YMPO164)model);
            }else if(model instanceof POYM008) {
                sendQueue.send((POYM008)model);
            }else if(model instanceof POYM009) {
                sendQueue.send((POYM009)model);
            }else if(model instanceof PSYM001) {
                sendQueue.send((PSYM001)model);
           }else if(model instanceof PSYM002) {
                sendQueue.send((PSYM002)model);
            }else if(model instanceof YMPS001) {
                sendQueue.send((YMPS001)model);
            }else if(model instanceof YMPS002) {
                sendQueue.send((YMPS002)model);
            }
        }catch(Exception e){
            throw new EJBServiceException(e);
	    }finally {
	        try { 
	            if(sendQueue != null) { 
	                sendQueue.close(); 
	            } 
	        }catch(Exception e) { 
	            throw new EJBServiceException(e);	        
	        }
	    } 
    }
    
	/**
	 * 모델에 따른 도메인 NAME을 리턴한다.
     * @param tcCode	전문코드
     * @return
     */
    private String getDomain(String tcCode) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
    	logger.println(LogLevel.DEBUG,this,"tcCode : "+tcCode);
    	if(YmCommonConst.MODEL_YMPM001.equals(tcCode) 
    			|| YmCommonConst.MODEL_YMPM002.equals(tcCode)
    			|| "POPM020".equals(tcCode)){
    		return SendQueue.DOMAIN_PM;
    	}else if(YmCommonConst.DOMAIN_PC.equals(tcCode.substring(2, 4))) {
    	    return SendQueue.DOMAIN_PC;
    	}else if(YmCommonConst.DOMAIN_PS.equals(tcCode.substring(2, 4))) {
    	    return SendQueue.DOMAIN_PS;
        }else if(YmCommonConst.DOMAIN_YM.equals(tcCode.substring(0, 2))) {
            if(Integer.parseInt(tcCode.substring(4, 7)) <=15) {
                return SendQueue.DOMAIN_DM;
            }else {                  
                return SendQueue.DOMAIN_PO;
            }
        }else {
            return SendQueue.DOMAIN_YM;
        }
    }
    
	/***********************************************************************
	/************************** 외부인터페이스 ********************************
	********************************************************************** */
	 /**
	 * 오퍼레이션명 : 
	 *
	 * HYSCO 대차상태정보 
        * 송신(MIMH210) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean sendMIMH210(String sendMsg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		logger.println(LogLevel.DEBUG,this,"### HYSCO 대차상태정보 : SEND START OF MIMH210");
		sendQueue(Level2SendQueue.DOMAIN_ETC_HYSCO_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"### HYSCO 대차상태정보 : SEND END OF MIMH210");
	    return true;
	}
	 /**
	 * 오퍼레이션명 : 
	 *
	 * B열연 COIL 차량 도착/출발 지시
        * 송신(CN1BP06/YM-BIF-021) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean sendCN1BP06(String sendMsg) {
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		logger.println(LogLevel.DEBUG,this,"### B열연코일 차량 도착/출발 지시 정보: SEND START OF CN1BP06");
		sendQueue(Level2SendQueue.DOMAIN_YM_BCYLR_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"### B열연코일 차량 도착/출발 지시 정보: SEND END OF CN1BP06");
	    return true;
	}

	 /**
	 * 오퍼레이션명 : 
	 *
	 * B열연 SLAB 차량 도착/출발 지시
        * 송신(CM1BP06/YM-BIF-042) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean sendCM1BP06(String sendMsg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		
		logger.println(LogLevel.DEBUG,this,"### B열연 차량 도착/출발 지시 정보: SEND START OF CM1BP06");
		sendQueue(Level2SendQueue.DOMAIN_YM_BSYLR_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"### B열연 차량 도착/출발 지시 정보: SEND END OF CM1BP06");
	    return true;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           
	}

	 /**
	 * 오퍼레이션명 : 
	 *
	 * A열연 차량 도착/출발 지시
        * 송신(THHC190/YM-AIF-034) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
	public boolean sendTHHC190(String sendMsg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		logger.println(LogLevel.DEBUG,this,"### A열연 차량 도착/출발 지시 정보: SEND START OF THHC190");
		sendQueue(Level2SendQueue.DOMAIN_YM_ACYLR_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"### A열연 차량 도착/출발 지시 정보: SEND END OF THHC190");
	    return true;
	}


	 /**
	 * 오퍼레이션명 : 
	 *
	 * B열연 SLAB 시각 설정 정보
        * 송신(CM1BP03/YM-BIF-030) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */          
	public boolean sendCM1BP03(String sendMsg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		logger.println(LogLevel.DEBUG,this,"### B열연 SLAB 시각 설정 정보: SEND START OF CM1BP03");
		sendQueue(Level2SendQueue.DOMAIN_YM_BSYLR_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"### B열연 SLAB 시각 설정 정보: SEND END OF CM1BP03");
	    return true;
	}

	 /**
	 * 오퍼레이션명 : 
	 *
	 * SLAB 정보
        * 송신(CM1BP02/YM-BIF-030) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */              
	public boolean sendCM1BP02(String sendMsg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		logger.println(LogLevel.DEBUG,this,"### SLAB 정보: SEND START OF CM1BP02");
		sendQueue(Level2SendQueue.DOMAIN_YM_BSYLR_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"### SLAB 정보: SEND END OF CM1BP02");
	    return true;
	}

	 /**
	 * 오퍼레이션명 : 
	 *
	 * BCY_시스템 시각 동기화
        * 송신(CN1BP03/YM-BIF-015) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                     
	public boolean sendCN1BP03(String sendMsg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		logger.println(LogLevel.DEBUG,this,"### BCY_시스템 시각 동기화: SEND START OF CN1BP03");
		sendQueue(Level2SendQueue.DOMAIN_YM_BCYLR_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"### BCY_시스템 시각 동기화: SEND END OF CN1BP03");
	    return true;
	}

	 /**
	 * 오퍼레이션명 : 
	 *
	 * C.T.S 상태정보
        * 송신(THHC250/YM-AIF-036) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                     
	public boolean sendTHHC250(String sendMsg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		logger.println(LogLevel.DEBUG,this,"### C.T.S 상태정보: SEND START OF THHC250");
		sendQueue(Level2SendQueue.DOMAIN_YM_ACYCB_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"### C.T.S 상태정보: SEND END OF THHC250");
	    return true;
	}

	 /**
	 * 오퍼레이션명 : 
	 *
	 * B열연 COIL YARD MAP 정보
        * 송신(CN1BP05/YM-BIF-016)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                          
	public boolean sendCN1BP05(String sendMsg) {	    
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		logger.println(LogLevel.DEBUG,this,"### B열연 COIL YARD MAP 정보: SEND START OF CN1BP05");
		sendQueue(Level2SendQueue.DOMAIN_YM_BCYLR_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"### B열연 COIL YARD MAP 정보: SEND END OF CN1BP05");
	    return true;
	}


	 /**
	 * 오퍼레이션명 : 
	 *
	 * A열연 COIL 대차출발지시
        * 송신(THHC300/YM-BIF-016) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                            
	public boolean sendTHHC300(String sendMsg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		logger.println(LogLevel.DEBUG,this,"### COIL 대차출발지시: SEND START OF THHC300");
		sendQueue(Level2SendQueue.DOMAIN_YM_ACYLR_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"### COIL 대차출발지시: SEND END OF THHC300");
	    return true;
	}

	 /**
	 * 오퍼레이션명 : 
	 *
	 * B열연 COIL 대차출발지시
        * 송신(CN1BP04/YM-BIF-016) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                        
	public boolean sendCN1BP04(String sendMsg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		logger.println(LogLevel.DEBUG,this,"### COIL 대차출발지시: SEND START OF CN1BP04");
		sendQueue(Level2SendQueue.DOMAIN_YM_BCYLR_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"### COIL 대차출발지시: SEND END OF CN1BP04");
	    return true;
	}

	 /**
	 * 오퍼레이션명 : 
	 *
	 * B열연 SLAB YARD MAP 정보
        * 송신(CM1BP05/YM-BIF-016) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                             
	public boolean sendCM1BP05(String sendMsg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		logger.println(LogLevel.DEBUG,this,"### B열연 SLAB YARD MAP 정보: SEND START OF CM1BP05");
		sendQueue(Level2SendQueue.DOMAIN_YM_BSYLR_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"### B열연 SLAB : SEND END OF CM1BP05");
	    return true;
	}

	 /**
	 * 오퍼레이션명 : 
	 *
	 * B열연 SLAB 대차출발지시
        * 송신(CM1BP04/YM-BIF-016) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                 
	public boolean sendCM1BP04(String sendMsg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		logger.println(LogLevel.DEBUG,this,"### SLAB 대차출발지시: SEND START OF CM1BP04");
		sendQueue(Level2SendQueue.DOMAIN_YM_BSYLR_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"### SLAB 대차출발지시: SEND END OF CM1BP04");
	    return true;
	}

	 /**
	 * 오퍼레이션명 : 
	 *
	 * C.T.S 초기정보
        * 송신(THHC172/YM-AIF-032) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
	public boolean sendTHHC172(String sendMsg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		logger.println(LogLevel.DEBUG,this,"### C.T.S 초기정보: SEND START OF THHC172");
		sendQueue(Level2SendQueue.DOMAIN_YM_ACYCB_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"### C.T.S 초기정보: SEND END OF THHC172");
	    return true;
	}

	 /**
	 * 오퍼레이션명 : 
	 *
	 * Crane 초기정보
        * 송신(THHC151/YM-AIF-028) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */          
	public boolean sendTHHC151(String sendMsg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		logger.println(LogLevel.DEBUG,this,"### Crane 초기정보: SEND START OF THHC151");
		sendQueue(Level2SendQueue.DOMAIN_YM_ACYLR_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"### Crane 초기정보: SEND END OF THHC151");
	    return true;
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * Lock 실적
        * 송신(THHC270/YM-AIF-028) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */          
	public boolean sendTHHC270(String sendMsg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		logger.println(LogLevel.DEBUG,this,"### Lock 실적: SEND START OF THHC270");
		sendQueue(Level2SendQueue.DOMAIN_YM_ACYLR_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"### Lock 실적: SEND END OF THHC270");
	    return true;
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * Unlock 실적
        * 송신(THHC280/YM-AIF-028) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */          
	public boolean sendTHHC280(String sendMsg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		logger.println(LogLevel.DEBUG,this,"### Unlock 실적: SEND START OF THHC280");
		sendQueue(Level2SendQueue.DOMAIN_YM_ACYLR_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"### Unlock 실적: SEND END OF THHC280");
	    return true;
	}

	 /**
	 * 오퍼레이션명 : 
	 *
	 * Crane 초기정보
     	 * 송신(THHC150/YM-AIF-028) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */               	
	public boolean sendTHHC150(String sendMsg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		logger.println(LogLevel.DEBUG,this,"### Crane 초기정보: SEND START OF THHC150");
		sendQueue(Level2SendQueue.DOMAIN_YM_ACYLR_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"### Crane 초기정보: SEND END OF THHC150");
	    return true;
	}

	 /**
	 * 오퍼레이션명 : 
	 *
	 * CONVEYOR 초기정보
        * 송신(THHC170/YM-AIF-030) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */       
	public boolean sendTHHC170(String sendMsg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		logger.println(LogLevel.DEBUG,this,"### CONVEYOR 초기정보: SEND START OF THHC170");
		sendQueue(Level2SendQueue.DOMAIN_YM_ACYLB_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"### CONVEYOR 초기정보: SEND END OF THHC170");
	    return true;
	}
	
	 /**
	 * 오퍼레이션명 : 
	 *
	 * YARD MAP 초기정보
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */   
	public boolean sendTHHC160(String sendMsg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		logger.println(LogLevel.DEBUG,this,"### YARD MAP 초기정보: SEND START OF THHC160");
		sendQueue(Level2SendQueue.DOMAIN_YM_ACYLB_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"### YARD MAP 초기정보: SEND END OF THHC160");
	    return true;
	}
	
	 /**
	 * 오퍼레이션명 : 
	 *
	 * SKID 상태정보
        * 송신(THHC260/YM-AIF-037) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */   
	public boolean sendTHHC260(String sendMsg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		logger.println(LogLevel.DEBUG,this,"### SKID 상태정보: SEND START OF THHC260");
		sendQueue(Level2SendQueue.DOMAIN_YM_ACYCB_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"### SKID 상태정보: SEND END OF THHC260");
	    return true;
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * COIL 조회 응답
        * 송신(THHC120/YM-AIF-025) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */        
	public boolean THHC120send(String sendMsg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		logger.println(LogLevel.DEBUG,this,"### C/R 작업지시취소: SEND START OF THHC120");
		sendQueue(Level2SendQueue.DOMAIN_YM_ACYLR_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"### C/R 작업지시취소: SEND END OF THHC120");
	    return true;
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * COIL 조회 응답
        * 송신(THHC130/YM-AIF-025) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */        
	public boolean THHC130send(String sendMsg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		logger.println(LogLevel.DEBUG,this,"### C/R 수정작업지시: SEND START OF THHC130");
		sendQueue(Level2SendQueue.DOMAIN_YM_ACYLR_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"### C/R 수정작업지시: SEND END OF THHC130");
	    return true;
	}

	 /**
	 * 오퍼레이션명 : 
	 *
	 * COIL 조회 응답
        * 송신(THHC131/YM-AIF-025) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */        
	public boolean sendTHHC131(String sendMsg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		logger.println(LogLevel.DEBUG,this,"### COIL 조회 응답: SEND START OF THHC131");
		sendQueue(Level2SendQueue.DOMAIN_YM_ACYLR_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"### COIL 조회 응답: SEND END OF THHC131");
	    return true;
	}

	 /**
	 * 오퍼레이션명 : 
	 *
	 * ADDRESS 요구 응답
        * 송신(THHC132/YM-AIF-025) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */             
	public boolean sendTHHC132(String sendMsg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		logger.println(LogLevel.DEBUG,this,"### ADDRESS 요구 응답: SEND START OF THHC132");
		sendQueue(Level2SendQueue.DOMAIN_YM_ACYLR_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"### ADDRESS 요구 응답: SEND END OF THHC132");
	    return true;
	}
	
	 /**
	 * 오퍼레이션명 : 
	 *
	 * ADDRESS 요구 응답
        * 송신(THHC140/YM-AIF-025) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */             
	public boolean THHC140send(String sendMsg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		logger.println(LogLevel.DEBUG,this,"### CRANE BACKUP 실적: SEND START OF THHC140");
		sendQueue(Level2SendQueue.DOMAIN_YM_ACYLR_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"### CRANE BACKUP 실적: SEND END OF THHC140");
	    return true;
	}

	 /**
	 * 오퍼레이션명 : 
	 *
	 * BSY_W/B Information Result 
        * 송신(CF1BP14/YM-BIF-044) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                  
	public boolean CF1BP14send(String MsgCF1BP14) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		logger.println(LogLevel.DEBUG,this,"Start-CF1BP14send()");
		sendQueue(Level2SendQueue.DOMAIN_PO_BHRMILL_S, MsgCF1BP14);
		logger.println(LogLevel.DEBUG,this,"End-CF1BP14send()");
		return true;
	}

	 /**
	 * 오퍼레이션명 : 
	 *
	 * 분기 Conveyor COIL Line Off 실적 
        * 송신(CF1BP04) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                
	public boolean CF1BP04send(String MsgCF1BP04) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		logger.println(LogLevel.DEBUG,this,"Start-CF1BP04send()");
		sendQueue(Level2SendQueue.DOMAIN_PO_BHRMILL_S, MsgCF1BP04);
		logger.println(LogLevel.DEBUG,this,"End-CF1BP04send()");						
		return true;			
	}

	 /**
	 * 오퍼레이션명 : 
	 *
	 * SLAB Take Out Result (Auto Only)
        * 송신(CF1BP05) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                     
	public boolean CF1BP05send(String MsgCF1BP05) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		logger.println(LogLevel.DEBUG,this,"Start-CF1BP05send()");
		sendQueue(Level2SendQueue.DOMAIN_PO_BHRMILL_S, MsgCF1BP05);
		logger.println(LogLevel.DEBUG,this,"End-CF1BP05send()");						
		return true;			
	}

	 /**
	 * 오퍼레이션명 : 
	 *
	 * SLAB Take In Result (Auto Only)
        * 송신(CF1BP06) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                          
	public boolean CF1BP06send(String MsgCF1BP06) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		logger.println(LogLevel.DEBUG,this,"Start-CF1BP06send()");
		sendQueue(Level2SendQueue.DOMAIN_PO_BHRMILL_S, MsgCF1BP06);
		logger.println(LogLevel.DEBUG,this,"End-CF1BP06send()");						
		return true;			
	}
	
	 /**
	 * 오퍼레이션명 : 
	 *
	 * 분기 Conveyor COIL Line In 실적 
        * 송신(CF1BP15) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */       	
	public boolean CF1BP15send(String MsgCF1BP15) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		boolean isSuccess = false;
		logger.println(LogLevel.DEBUG,this,"Start-CF1BP15send()");
		sendQueue(Level2SendQueue.DOMAIN_PO_BHRMILL_S, MsgCF1BP15);
		logger.println(LogLevel.DEBUG,this,"End-CF1BP15send()");						
		return true;			
	}
	
	 /**
	 * 오퍼레이션명 : 
	 *
	 * 검색조건에 해당하는 야드설비별 작업내역 리스트를 가져온다.
        * 송신(CF1BP15) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */       	     
	public List selectFicilityWorkHisList(String queryID, List listData) throws EJBServiceException ,DAOException{
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			YdEquipDAO ydequipDAO = new YdEquipDAO();
			return ydequipDAO.getListData(queryID, listData);
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}

	 /**
	 * 오퍼레이션명 : 
	 *
	  * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */   	
	public List getListWrkRatio(String queryID, String s_yardgubun, String s_donggubun, String s_fromDate, String s_toDate) {
		YdWRsltDAO ydwrsltDAO = null;	    
	    List WrkRatioList = null;
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
	    	ydwrsltDAO = new YdWRsltDAO();
	    	WrkRatioList = ydwrsltDAO.getListData(queryID,new Object[]{s_yardgubun+s_donggubun, s_toDate, s_fromDate, s_yardgubun+s_donggubun, s_toDate, s_fromDate, s_toDate, s_fromDate});
	    	return WrkRatioList;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}
	
	 /**
	 * 오퍼레이션명 : 
	 *
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */   
	public List getListErrorLog(String queryID, List listData) throws EJBServiceException ,DAOException{
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			YdWRsltDAO ydwrsltDAO = new YdWRsltDAO();
			return ydwrsltDAO.getListData(queryID, listData);
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}
	 /**
	 * 오퍼레이션명 : 
	 *
	 * 검색조건에 해당하는 야드설비별 작업내역 리스트를 가져온다.
        * 송신(CF1BP15) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */   
	public List getListTCErrorLog(String queryID, List listData) throws EJBServiceException ,DAOException{
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			YdWRsltDAO ydwrsltDAO = new YdWRsltDAO();
			return ydwrsltDAO.getListData(queryID, listData);
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}
	 /**
	 * 오퍼레이션명 : 
	 *
	 * 검색조건에 해당하는 야드설비별 작업내역 리스트를 가져온다.
        * 송신(CF1BP15) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */   	
	public int deleteTCErrorLog(String queryid, List whereData) throws EJBServiceException ,DAOException {	
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			YdWRsltDAO ydwrsltDAO = new YdWRsltDAO();		
			return ydwrsltDAO.deleteData(queryid, whereData);		
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}

	 /**
	 * 오퍼레이션명 : 
	 *
	 * A열연 COIL 권상/권하실적결과 전문송신
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */        
	public boolean THHC200send(String sendMsg) {
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		sendQueue(Level2SendQueue.DOMAIN_YM_ACYLR_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"THHC200=A열연 COIL 권상/권하실적결과 전문송신 성공=");
	    return true;
	}

	 /**
	 * 오퍼레이션명 : 
	 *
	 * A열연 Coil 작업지시 전문송신
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */             
	public boolean THHC110send(String sendMsg){
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		sendQueue(Level2SendQueue.DOMAIN_YM_ACYLR_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"THHC110=A열연 Coil 작업지시 전문송신 성공=");
	    return true;
	}
	

	 /**
	 * 오퍼레이션명 : 
	 *
	 * B열연 Coil 작업지시 전문송신
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                  
	public boolean CN1BP01send(String sendMsg){
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		sendQueue(Level2SendQueue.DOMAIN_YM_BCYLR_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"CN1BP01=B열연 Coil 작업지시 전문송신 성공=");
		
	    return true;
	}

	 /**
	 * 오퍼레이션명 : 
	 *
	 * B열연 Slab 작업지시 전문송신
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */             
	public boolean CM1BP01send(String sendMsg){
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		sendQueue(Level2SendQueue.DOMAIN_YM_BSYLR_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"CM1BP01=B열연 Slab 작업지시 전문송신 성공=");
	    return true;
	}

	 /**
	 * 오퍼레이션명 : 
	 *
	 * A열연 Saddle 권하시 CTS에게 작업지시 전문송신
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                  
	public boolean THHT400send(String sendMsg){
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		sendQueue(Level2SendQueue.DOMAIN_YM_ACYCR_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"THHT400=A열연 Saddle 권하시 CTS에게 작업지시 전문송신 성공=");
	    return true;
	}
	
	
	 /**
	 * 오퍼레이션명 : 
	 *
	 * A열연 Saddle 상태정보  전문송신
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                  
	public boolean THHT410send(String sendMsg){
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		sendQueue(Level2SendQueue.DOMAIN_YM_ACYCR_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"THHT410=A열연 Saddle 상태정보 전문송신 성공=");
	    return true;
	}

	 /**
	 * 오퍼레이션명 : 
	 *
	 * A열연 압연실적처리  전문송신
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                      
	public boolean THHC171send(String sendMsg){	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		sendQueue(Level2SendQueue.DOMAIN_YM_ACYLR_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"THHC171=A열연 압연실적처리  전문송신 성공=");
	    return true;
	}
	
	 /**
	 * 오퍼레이션명 : 
	 *
	 *A열연 대기차량정보처리  전문송신
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                           
	public boolean THHC180send(String sendMsg){	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		sendQueue(Level2SendQueue.DOMAIN_YM_ACYLR_S, sendMsg);
		logger.println(LogLevel.DEBUG,this," THHC180=A열연 대기차량정보처리 전문송신 성공=");
	    return true;
	}

	 /**
	 * 오퍼레이션명 : 
	 *
	 * B열연 압연실적처리 전문송신
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                
	public boolean CN1BP02send(String sendMsg){
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		sendQueue(Level2SendQueue.DOMAIN_YM_BCYLR_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"CN1BP02=B열연 압연실적처리 전문송신 성공=");
	    return true;
	}

	/**
	 * 오퍼레이션명 : 
	 *
	 * B열연 대차상차실적 전문송신
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                     
	public boolean sendMIMH110(String sendMsg){	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		sendQueue(Level2SendQueue.DOMAIN_ETC_HYSCO_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"MIMH110=B열연 대차상차실적 전문송신 성공=");
	    return true;
	}

	/**
	 * 오퍼레이션명 : 
	 *
	 * B열연 분기컨베이어에서 확장컨베이어로 전환시 전문송신
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                          
	public boolean CN1BP07send(String sendMsg){
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		sendQueue(Level2SendQueue.DOMAIN_YM_BCYLR_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"CN1BP07=B열연 분기컨베이어에서 확장컨베이어로 전환시 전문송신 성공=");
	    return true;
	}	

	/**
	 * 오퍼레이션명 : 
	 *
	 * B열연 HYSCO 대차 이동요구를 수신받아 야드 Level-2로 ByPass
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                 	
	public boolean CN1BP08send(String sendMsg){
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		sendQueue(Level2SendQueue.DOMAIN_YM_BCYLR_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"CN1BP08=B열연 HYSCO 대차 이동요구를 수신받아 야드 Level-2로 송신 성공=");
	    return true;
	}	
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * B열연 B열연 확장 Conv' Line Off요구를 수신 응답정보를 야드 Level-2로 송신
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                 	     
	public boolean CN1BP09send(String sendMsg){
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		sendQueue(Level2SendQueue.DOMAIN_YM_BCYLR_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"CN1BP09=B열연 확장 Conv' Line Off요구를 수신 응답정보를 야드 Level-2로 송신 성공=");
	    return true;
	}	
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * HYSCO 대차 이동 정보(야드 L2 CN1PB13 에서 수신받아 HYSCO로 MIMH220 넘겨줄 TC)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */            
	public boolean MIMH220send(String sendMsg){		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		sendQueue(Level2SendQueue.DOMAIN_ETC_HYSCO_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"MIMH220=B열연 대차 이동정보를 수신받아 HYSCO로 송신 성공=");
	    return true;
	}	
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * HYSCO Coil 상세정보 요구 수신에 상세 정보 송신 MIMH510
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                 
	public boolean MIMH510send(String sendMsg){
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		sendQueue(Level2SendQueue.DOMAIN_ETC_HYSCO_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"MIMH510=B열연 Coil 상세정보 송신 성공=");
	    return true;
	}	
	
     	/**
	 * 오퍼레이션명 : 
	 *
	 * B열연 SCARFING 작업지시 전문송신
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */              
	public boolean CS1BP01send(String sendMsg){
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		sendQueue(Level2SendQueue.DOMAIN_YM_BSYLR_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"CS1BP01=B열연 B열연 SCARFING 작업지시 전문송신 성공=");
	    return true;
	}
	
     	/**
	 * 오퍼레이션명 : 
	 *
	 * B열연 L2로부터 Slab Line Off 요구를 받아 Slab Line Off 완료시 호출
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */              
	public boolean CF1BP03send(String sendMsg){
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		sendQueue(Level2SendQueue.DOMAIN_PO_BHRMILL_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"CF1BP03=B열연 L2로부터 Slab Line Off 요구를 받아 Slab Line Off 전문송신 성공=");
	    return true;
	}

       /**
	 * 오퍼레이션명 : 
	 *
	 * B열연 L2로부터 #3 CTC, #4 CTC Slab Loading  완료시 호출
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */      
	public boolean CF1BP12send(String sendMsg){
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		sendQueue(Level2SendQueue.DOMAIN_PO_BHRMILL_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"CF1BP12=B열연 L2로부터 #3 CTC, #4 CTC Slab Loading 전문송신 성공=");
	    return true;
	}
	
	/**********************************************************************************/
	/****************************** [A열연 SLAB 야드 추가] ******************************/
	/**********************************************************************************/

	/**
	 * 오퍼레이션명 : 
	 *
	 * A열연 Slab 작업지시 전문송신
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean HM1BP01send(String sendMsg){
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		logger.println(LogLevel.DEBUG,this,"### A_A 열연 SLAB 작업지시 전문송신 정보: SEND START OF HM1BP01");
		//sendQueue(Level2SendQueue.DOMAIN_YM_ASYCRA_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"HM1BP01=A_A열연 Slab 작업지시 전문송신 성공=");
	    return true;
	}
	/**
	 * 오퍼레이션명 : 
	 *
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean HM1BP51send(String sendMsg){
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		logger.println(LogLevel.DEBUG,this,"### A_B열연 SLAB 작업지시 전문송신 정보: SEND START OF HM1BP51");
		//sendQueue(Level2SendQueue.DOMAIN_YM_ASYCRB_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"HM1BP51=A_B열연 Slab 작업지시 전문송신 성공=");
	    return true;
	}
	
      /**
	 * 오퍼레이션명 : 
	 *
	 * A열연 A동 SLAB YARD MAP 정보
        * 송신(HM1BP04/YM-BIF-041) (MCH)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean sendHM1BP04(String sendMsg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		logger.println(LogLevel.DEBUG,this,"### A_A열연 SLAB YARD MAP 정보: SEND START OF HM1BP04");
		//sendQueue(Level2SendQueue.DOMAIN_YM_ASYCRA_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"### A_A열연 SLAB : SEND END OF HM1BP04");
	    return true;
	}	
	
      /**
	 * 오퍼레이션명 : 
	 *
	 * A열연 B동 SLAB YARD MAP 정보
        * 송신(HM1BP54/YM-BIF-041) (MCH)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean sendHM1BP54(String sendMsg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		logger.println(LogLevel.DEBUG,this,"### A_B열연 SLAB YARD MAP 정보: SEND START OF HM1BP54");
		//sendQueue(Level2SendQueue.DOMAIN_YM_ASYCRB_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"### A_B열연 SLAB : SEND END OF HM1BP54");
	    return true;
	}

      /**
	 * 오퍼레이션명 : 
	 *
	 * A열연 7연주 SLAB YARD MAP 정보
        * 송신(HC3BP51) (MCH)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
	public boolean sendHC3BP51(String sendMsg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		logger.println(LogLevel.DEBUG,this,"### 7연주 SLAB YARD MAP 정보: SEND START OF HC3BP51");
		//sendQueue(Level2SendQueue.DOMAIN_PO_AHRCCASTINGDEV7_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"### 7연주 SLAB : SEND END OF HC3BP51");
	    return true;
	}	

	/**
	 * 오퍼레이션명 : 
	 *
	 * A열연 SLAB 시각 설정 정보
        * 송신(HM1BP03/YM-BIF-030) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */          
	public boolean sendHM1BP03(String sendMsg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		logger.println(LogLevel.DEBUG,this,"### A_A 열연 SLAB 시각 설정 정보: SEND START OF HM1BP03");
		//sendQueue(Level2SendQueue.DOMAIN_YM_ASYCRA_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"### A_A열연 SLAB 시각 설정 정보: SEND END OF HM1BP03");
	    return true;
	}

	/**
	 * 오퍼레이션명 : 
	 *
	 * A열연 SLAB 시각 설정 정보
        * 송신(HM1BP53/YM-BIF-030) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return                   
	 * @throws 
	 */               
	public boolean sendHM1BP53(String sendMsg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		logger.println(LogLevel.DEBUG,this,"### A_B열연 SLAB 시각 설정 정보: SEND START OF HM1BP53");
		//sendQueue(Level2SendQueue.DOMAIN_YM_ASYCRB_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"### A_B열연 SLAB 시각 설정 정보: SEND END OF HM1BP53");
	    return true;
	}
	/**
	 * 오퍼레이션명 : 
	 *
	 * SLAB 정보
        * 송신(HM1BP02/YM-BIF-038) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */    
	public boolean sendHM1BP02(String sendMsg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		logger.println(LogLevel.DEBUG,this,"### A_A열연 SLAB 정보: SEND START OF HM1BP02");
		sendQueue(Level2SendQueue.DOMAIN_YM_ASYCRA_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"### A_A열연 SLAB 정보: SEND END OF HM1BP02");
	    return true;
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * SLAB 정보
        * 송신(HM1BP52/YM-BIF-038) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */         
	public boolean sendHM1BP52(String sendMsg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		logger.println(LogLevel.DEBUG,this,"### A_B열연 SLAB 정보: SEND START OF HM1BP52");
		sendQueue(Level2SendQueue.DOMAIN_YM_ASYCRB_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"### A_B열연 SLAB 정보: SEND END OF HM1BP52");
	    return true;
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 연주 CRANE 고장 정보 전송
        * 송신(HC3BP52/YM-BIF-038) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */              	
	public boolean sendHC3BP52(String sendMsg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		logger.println(LogLevel.DEBUG,this,"### A열연 연주 CRANE 고장 정보 전송: SEND START OF HC3BP52");
		sendQueue(Level2SendQueue.DOMAIN_PO_AHRCCASTINGDEV7_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"### A열연 연주 CRANE 고장 정보 전송 : SEND END OF HC3BP52");
	    return true;
	}	
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 연주 CRANE 고장 정보 전송
        * 송신(HC3BP53/YM-BIF-038) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */              	
	public boolean sendHC3BP53(String sendMsg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		logger.println(LogLevel.DEBUG,this,"### A열연 연주 CRANE 고장 정보 전송: SEND START OF HC3BP53");
		sendQueue(Level2SendQueue.DOMAIN_PO_AHRCCASTINGDEV7_S, sendMsg);
		logger.println(LogLevel.DEBUG,this,"### A열연 연주 CRANE 고장 정보 전송 : SEND END OF HC3BP53");
	    return true;
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
	 * L-2 송신
	 * @param qName		MESSAGE QUEUE NAME
	 * @param sendMsg	전송메시지
	 * @return
	 */
	private boolean sendQueue(String qName, String sendMsg) {
		
		JmsQueueSender sender 		= null;
		String queueName 			= null;
		JDTORecord inRecord 		= null;
		PropertyService ptService	= null;
		
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
		    // 프로퍼티 서비스 인스턴스를 취득합니다.
			ptService = PropertyService.getInstance();

			// 큐 명칭을 프로퍼티로부터 취득합니다.
			queueName = ptService.getProperty("common.properties",this.getNewQueueName(qName));
		
			sender = new JmsQueueSender();
			
			// 큐에 연결할 리소스를 생성합니다.
			sender.initQueueService(queueName);
			
			// 큐에 넣을 데이터를 생성합니다.
			inRecord = JDTORecordFactory.getInstance().create();
			inRecord.setField("JMS_TC_CD", 		this.getNewTcId(sendMsg));
			inRecord.setField("JMS_TC_MESSAGE", sendMsg);
			inRecord.setField("JMS_TC_CREATE_DDTT", DateHelper.format(
					new java.util.Date(System.currentTimeMillis()),
					"yyyy/MM/dd HH:mm:ss"));
			//inRecord.setField("JMS_QUEUE_NAME", queueName);
			
			// 큐에 데이터를 전송합니다.
			sender.send(inRecord);

			
			// common jspeed 로그를 출력합니다.
			logger.println(LogLevel.INFO,  "[ " + queueName + " 에  전송을 성공하였습니다. ]");
		} catch (Exception e) {
			logger.println(LogLevel.ERROR,  e.toString(), e);
		    e.printStackTrace();
		}
	    return true;
	}
	
	/**
	 * AB OLD QUEUE > 일관제철 NEW QUEUE 
	 * @param qName		MESSAGE QUEUE NAME
	 * @return
	 */
	private String getNewQueueName(String sQname){
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		String sVal = "";
		if(sQname.equals(Level2SendQueue.DOMAIN_PO_AHRCCASTINGDEV7_S)){
			 
			sVal = "jms.queue.POA_EAI_QUEUE";
		}else if(sQname.equals(Level2SendQueue.DOMAIN_YM_ACYLR_S)||
				 sQname.equals(Level2SendQueue.DOMAIN_YM_ACYCB_S)||
				 sQname.equals(Level2SendQueue.DOMAIN_YM_ACYLB_S)||
				 sQname.equals(Level2SendQueue.DOMAIN_YM_ACYCR_S)||
				 sQname.equals(Level2SendQueue.DOMAIN_YM_ASYCRA_S)||
				 sQname.equals(Level2SendQueue.DOMAIN_YM_ASYCRB_S)){
			
			sVal = "jms.queue.YMA_EAI_QUEUE";
		}else if(sQname.equals(Level2SendQueue.DOMAIN_PO_BHRMILL_S)){
			
			sVal = "jms.queue.POB_EAI_QUEUE";
		}else if(sQname.equals(Level2SendQueue.DOMAIN_YM_BCYLR_S)||
				 sQname.equals(Level2SendQueue.DOMAIN_YM_BSYLR_S)){
		
			sVal = "jms.queue.YMB_EAI_QUEUE";
		}else if(sQname.equals(Level2SendQueue.DOMAIN_ETC_HYSCO_S)){
		
			sVal = "jms.queue.ETC_EAI_QUEUE";
		}
		
		logger.println(LogLevel.INFO,  "QUEUE CONVERT : [ " + sQname + " ]["+sVal+"]");
		
		return sVal;
	}
	
	/**
	 * AB OLD TC ID > 일관제철 NEW TC_ID 
	 * @param sendMsg	Message
	 * @return
	 */
	private String getNewTcId(String sendMsg){
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		String sOldTcId = "";
		String sNewTcId = "";
		
		if(sendMsg.length() > 7){
			sOldTcId = sendMsg.substring(0, 7);
		}
		
		sNewTcId = (String)hMap.get(sOldTcId);
		
		logger.println(LogLevel.INFO,  "TC CONVERT : [ " + sOldTcId + " ]["+sNewTcId+"]");
		
		return sNewTcId;
	}
	
	/**
	 * AB TC ID 와 일관제철 NEW TC_ID Mapping Table Info 
	 */
	private void createHashMapInfo(){
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		
		// PIDEV
//		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
//		if(sAPP060_OLDSRC_YN.equals("Y")){
//			return;
//		}

		String sAPP060_OLDSRC_YN = "Y";
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
		hMap.put("CF1BP03",	"POCFL103");
		hMap.put("CF1BP04",	"POCFL104");
		hMap.put("CF1BP05",	"POCFL105");
		hMap.put("CF1BP06",	"POCFL106");
		hMap.put("CF1BP12",	"POCFL112");
		hMap.put("CF1BP14",	"POCFL114");
		hMap.put("CF1BP15",	"POCFL115");
		hMap.put("CM1BP01",	"YMCML101");
		hMap.put("CM1BP02",	"YMCML102");
		hMap.put("CM1BP03",	"YMCML103");
		hMap.put("CM1BP04",	"YMCML104");
		hMap.put("CM1BP05",	"YMCML105");
		hMap.put("CM1BP06",	"YMCML106");
		hMap.put("CN1BP01",	"YMCNL101");
		hMap.put("CN1BP02",	"YMCNL102");
		hMap.put("CN1BP03",	"YMCNL103");
		hMap.put("CN1BP04",	"YMCNL104");
		hMap.put("CN1BP05",	"YMCNL105");
		hMap.put("CN1BP06",	"YMCNL106");
		hMap.put("CN1BP07",	"YMCNL107");
		hMap.put("CN1BP08",	"YMCNL108");
		hMap.put("CN1BP09",	"YMCNL109");
		hMap.put("CS1BP01",	"YMCML107");
		hMap.put("HC3BP51",	"POHCL704");
		hMap.put("HC3BP52",	"POHCL705");
		hMap.put("HC3BP53",	"POHCL706");
		hMap.put("HM1BP01",	"YMHML101");
		hMap.put("HM1BP03",	"YMHML102");
		hMap.put("HM1BP04",	"YMHML103");
		hMap.put("HM1BP51",	"YMHML201");
		hMap.put("HM1BP53",	"YMHML202");
		hMap.put("HM1BP54",	"YMHML203");
		hMap.put("MIMH110",	"ETMML101");
		hMap.put("MIMH210",	"ETMML102");
		hMap.put("MIMH220",	"ETMML103");
		hMap.put("MIMH510",	"ETMML104");
		hMap.put("THHC110",	"YMTHL201");
		hMap.put("THHC120",	"YMTHL202");
		hMap.put("THHC130",	"YMTHL203");
		hMap.put("THHC131",	"YMTHL204");
		hMap.put("THHC132",	"YMTHL205");
		hMap.put("THHC140",	"YMTHL206");
		hMap.put("THHC150",	"YMTHL207");
		hMap.put("THHC151",	"YMTHL208");
		hMap.put("THHC160",	"YMTHL101");
		hMap.put("THHC170",	"YMTHL102");
		hMap.put("THHC171",	"YMTHL209");
		hMap.put("THHC172",	"YMTHL301");
		hMap.put("THHC180",	"YMTHL210");
		hMap.put("THHC190",	"YMTHL211");
		hMap.put("THHC200",	"YMTHL212");
		hMap.put("THHC250",	"YMTHL302");
		hMap.put("THHC260",	"YMTHL303");
		hMap.put("THHC270",	"YMTHL213");
		hMap.put("THHC280",	"YMTHL214");
		hMap.put("THHT400",	"YMTHL401");
		hMap.put("THHT410",	"YMTHL402");
	}
	
	
    /**
	 * 오퍼레이션명 : 코일입고작업실적.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */  
	public boolean getYDDMR001(JDTORecord model) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		JDTORecord tcRecord1 = JDTORecordFactory.getInstance().create();
		String sCurrDateDM ="";
		String sCurrTimeDM ="";
		try{
//			GOODS_NO		제품 번호
//			RECEIPT_DATE	입고 일자
//			RECEIPT_TIME	입고 시각
//			YD_GP			YARD 구분
//			STORE_LOC		저장 위치
//			PROD_ITEM_CODE	ITEMCODE
			Date date = new Date();
			sCurrDateDM = DateHelper.format(DateHelper.addMinute(date,2), "yyyyMMdd");
			sCurrTimeDM = DateHelper.format(DateHelper.addMinute(date,2), "HHmmss");

			// PIDEV
//			String sApplyYnPI = commDAO.ApplyYnPI("", "YardWrkResReg => getYDDMR001", "APPPI0", "*", "*");
			
//			if("Y".equals(sApplyYnPI)) {
				tcRecord1.setField("MQ_TC_CD"			, "M10YDLMJ1011"												);
				tcRecord1.setField("MQ_TC_CREATE_DDTT"	, new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss"))			);
				tcRecord1.setField("YD_GP"				, StringHelper.evl(model.getFieldString("YD_GP"), "")			);
				tcRecord1.setField("DIST_GOODS_GP"		, "H"															);
				tcRecord1.setField("YARD_GP"			, ""															);
				tcRecord1.setField("GOODS_NO"			, StringHelper.evl(model.getFieldString("GOODS_NO"), "").trim() );
				tcRecord1.setField("STORE_LOC_CD"		, StringHelper.evl(model.getFieldString("STORE_LOC"), "")		);
				tcRecord1.setField("RECEIPT_DATE"		, sCurrDateDM													);
				tcRecord1.setField("RECEIPT_TIME"		, sCurrTimeDM													);
				tcRecord1.setField("PROD_ITEM_CODE"		, StringHelper.evl(model.getFieldString("PROD_ITEM_CODE"), "")	);
				tcRecord1.setField("CURR_PROG_CD"		, StringHelper.evl(model.getFieldString("CURR_PROG_CD"), "")	);
				ymCommUtils.addSndData(tcRecord1, tcRecord1);
				
//			} else {
//				tcRecord1.setField("JMS_TC_CD", "YDDMR001");		
//				tcRecord1.setField("TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
//				tcRecord1.setField("GOODS_NO", StringHelper.evl(model.getFieldString("GOODS_NO"), "").trim());
//				//tcRecord1.setField("RECEIPT_DATE", YmCommonUtil.getCurDate("yyyyMMdd"));
//				//tcRecord1.setField("RECEIPT_TIME", YmCommonUtil.getCurDate("HHmmss"));
//				tcRecord1.setField("RECEIPT_DATE", sCurrDateDM);
//				tcRecord1.setField("RECEIPT_TIME", sCurrTimeDM);		
//				tcRecord1.setField("YD_GP", StringHelper.evl(model.getFieldString("YD_GP"), ""));
//				tcRecord1.setField("STORE_LOC", StringHelper.evl(model.getFieldString("STORE_LOC"), ""));
//				tcRecord1.setField("PROD_ITEM_CODE", StringHelper.evl(model.getFieldString("PROD_ITEM_CODE"), ""));
//				tcRecord1.setField("CURR_PROG_CD", StringHelper.evl(model.getFieldString("CURR_PROG_CD"), ""));
//				
//				//내부인터페이스 송신모듈 호출 
//				this.sendInternalModel(tcRecord1);
//			}
			

			
			}
			catch(Exception e){
		    }
	        return true;
	}
	 
	
	
    /**
	 * 오퍼레이션명 : 코일입고작업실적.
	 * 임가공 PIDEV
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */  
	public boolean getM10YDLMJ1011(JDTORecord model) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		JDTORecord tcRecord1 = JDTORecordFactory.getInstance().create();
		String sCurrDateDM ="";
		String sCurrTimeDM ="";
		
		try{
//			GOODS_NO		제품 번호
//			RECEIPT_DATE	입고 일자
//			RECEIPT_TIME	입고 시각
//			YD_GP			YARD 구분
//			STORE_LOC		저장 위치
//			PROD_ITEM_CODE	ITEMCODE
			Date date = new Date();
			sCurrDateDM = DateHelper.format(DateHelper.addMinute(date,2), "yyyyMMdd");
			sCurrTimeDM = DateHelper.format(DateHelper.addMinute(date,2), "HHmmss");

			tcRecord1.setField("MQ_TC_CD"			, "M10YDLMJ1011"												);
			tcRecord1.setField("MQ_TC_CREATE_DDTT"	, new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss"))			);
			tcRecord1.setField("YD_GP"				, StringHelper.evl(model.getFieldString("YD_GP"), "")			);
			tcRecord1.setField("DIST_GOODS_GP"		, "H"															);
			tcRecord1.setField("YARD_GP"			, ""															);
			tcRecord1.setField("GOODS_NO"			, StringHelper.evl(model.getFieldString("GOODS_NO"), "").trim() );
			tcRecord1.setField("STORE_LOC_CD"		, StringHelper.evl(model.getFieldString("STORE_LOC"), "")		);
			tcRecord1.setField("RECEIPT_DATE"		, sCurrDateDM													);
			tcRecord1.setField("RECEIPT_TIME"		, sCurrTimeDM													);
			tcRecord1.setField("PROD_ITEM_CODE"		, StringHelper.evl(model.getFieldString("PROD_ITEM_CODE"), "")	);
			tcRecord1.setField("CURR_PROG_CD"		, StringHelper.evl(model.getFieldString("CURR_PROG_CD"), "")	);
			ymCommUtils.addSndData(tcRecord1, tcRecord1);
			
		}
		catch(Exception e){
	    }
		
	    return true;
	}	
	
	/**
	 * 오퍼레이션명 : 임가공입고작업실적.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean getYDDMR003(JDTORecord model) {
		JDTORecord tcRecord1 = JDTORecordFactory.getInstance().create();
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
//			GOODS_NO		제품 번호
//			RECEIPT_DATE	입고 일자
//			RECEIPT_TIME	입고 시각
//			YD_GP			YARD 구분
//			STORE_LOC		저장 위치
//			PROD_ITEM_CODE	ITEMCODE

		tcRecord1.setField("JMS_TC_CD", "YDDMR003");
		tcRecord1.setField("TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
		tcRecord1.setField("GOODS_NO", StringHelper.evl(model.getFieldString("GOODS_NO"), "").trim());
		tcRecord1.setField("RECEIPT_DATE", YmCommonUtil.getCurDate("yyyyMMdd"));
		tcRecord1.setField("RECEIPT_TIME", YmCommonUtil.getCurDate("HHmmss"));
		tcRecord1.setField("YD_GP", StringHelper.evl(model.getFieldString("YD_GP"), ""));
		tcRecord1.setField("STORE_LOC", StringHelper.evl(model.getFieldString("STORE_LOC"), ""));
		tcRecord1.setField("PROD_ITEM_CODE", StringHelper.evl(model.getFieldString("PROD_ITEM_CODE"), ""));
		
		//내부인터페이스 송신모듈 호출 
		this.sendInternalModel(tcRecord1);
		}
		catch(Exception e){
	    }
        return true;
	 }
	
	
	/**
	 * 오퍼레이션명 : 코일제품이적작업실적.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean getYDDMR004(JDTORecord model) {
		JDTORecord tcRecord1 = JDTORecordFactory.getInstance().create();
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
//			GOODS_NO		제품 번호
//			BEFO_STORE_LOC	FROM 저장위치
//			TO_STORE_LOC	TO 저장위치
//			MOVENSTACK_DATE	이적 일자
//			MOVENSTACK_TIME	이적 시각

			// PIDEV
//			String sApplyYnPI = commDAO.ApplyYnPI("", "YardWrkResReg => getYDDMR04.getYDDMR004", "APPPI0", "*", "*");
			
//			if("Y".equals(sApplyYnPI)) {				
				tcRecord1.setField("MQ_TC_CD", "M10YDLMJ1031");
				tcRecord1.setField("MQ_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
				tcRecord1.setField("YD_GP", StringHelper.evl(model.getFieldString("YD_GP"), ""));
				tcRecord1.setField("DIST_GOODS_GP", StringHelper.evl(model.getFieldString("DIST_GOODS_GP"), ""));
				tcRecord1.setField("YARD_GP", StringHelper.evl(model.getFieldString("YARD_GP"), ""));
				tcRecord1.setField("GOODS_NO", StringHelper.evl(model.getFieldString("GOODS_NO"), "").trim());
				tcRecord1.setField("STORE_LOC_CD_FROM", StringHelper.evl(model.getFieldString("BEFO_STORE_LOC"), ""));
				tcRecord1.setField("STORE_LOC_CD_TO", StringHelper.evl(model.getFieldString("TO_STORE_LOC"), ""));
				tcRecord1.setField("MOVENSTACK_DATE", YmCommonUtil.getCurDate("yyyyMMdd"));
				tcRecord1.setField("MOVENSTACK_TIME", YmCommonUtil.getCurDate("HHmmss"));
				
				ymCommUtils.addSndData(tcRecord1, tcRecord1);
//			} else {
//				tcRecord1.setField("JMS_TC_CD", "YDDMR004");
//				tcRecord1.setField("TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
//				tcRecord1.setField("GOODS_NO", StringHelper.evl(model.getFieldString("GOODS_NO"), "").trim());
//				tcRecord1.setField("BEFO_STORE_LOC", StringHelper.evl(model.getFieldString("BEFO_STORE_LOC"), ""));
//				tcRecord1.setField("TO_STORE_LOC", StringHelper.evl(model.getFieldString("TO_STORE_LOC"), ""));
//				tcRecord1.setField("MOVENSTACK_DATE", YmCommonUtil.getCurDate("yyyyMMdd"));
//				tcRecord1.setField("MOVENSTACK_TIME", YmCommonUtil.getCurDate("HHmmss"));	
//				
//				//내부인터페이스 송신모듈 호출 
//				this.sendInternalModel(tcRecord1);
//			}
				

			
		} catch(Exception e) {
			
	    }
	    return true;
	 }
	
	/**
	 * 
	 * 임가공 PIDEV
	 * 
	 * 오퍼레이션명 : 코일제품이적작업실적.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean getYDDMR004PI(JDTORecord model) {
		JDTORecord tcRecord1 = JDTORecordFactory.getInstance().create();
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
//			GOODS_NO		제품 번호
//			BEFO_STORE_LOC	FROM 저장위치
//			TO_STORE_LOC	TO 저장위치
//			MOVENSTACK_DATE	이적 일자
//			MOVENSTACK_TIME	이적 시각

			// PIDEV
//			String sApplyYnPI = commDAO.ApplyYnPI("", "YardWrkResReg => getYDDMR004PI", "APPPI0", "5", "*");
			
//			if("Y".equals(sApplyYnPI)) {				
				tcRecord1.setField("MQ_TC_CD", "M10YDLMJ1031");
				tcRecord1.setField("MQ_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
				tcRecord1.setField("YD_GP", StringHelper.evl(model.getFieldString("YD_GP"), ""));
				tcRecord1.setField("DIST_GOODS_GP", StringHelper.evl(model.getFieldString("DIST_GOODS_GP"), ""));
				tcRecord1.setField("YARD_GP", StringHelper.evl(model.getFieldString("YARD_GP"), ""));
				tcRecord1.setField("GOODS_NO", StringHelper.evl(model.getFieldString("GOODS_NO"), "").trim());
				tcRecord1.setField("STORE_LOC_CD_FROM", StringHelper.evl(model.getFieldString("BEFO_STORE_LOC"), ""));
				tcRecord1.setField("STORE_LOC_CD_TO", StringHelper.evl(model.getFieldString("TO_STORE_LOC"), ""));
				tcRecord1.setField("MOVENSTACK_DATE", YmCommonUtil.getCurDate("yyyyMMdd"));
				tcRecord1.setField("MOVENSTACK_TIME", YmCommonUtil.getCurDate("HHmmss"));
				
				ymCommUtils.addSndData(tcRecord1, tcRecord1);
//			} else {
//				tcRecord1.setField("JMS_TC_CD", "YDDMR004");
//				tcRecord1.setField("TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
//				tcRecord1.setField("GOODS_NO", StringHelper.evl(model.getFieldString("GOODS_NO"), "").trim());
//				tcRecord1.setField("BEFO_STORE_LOC", StringHelper.evl(model.getFieldString("BEFO_STORE_LOC"), ""));
//				tcRecord1.setField("TO_STORE_LOC", StringHelper.evl(model.getFieldString("TO_STORE_LOC"), ""));
//				tcRecord1.setField("MOVENSTACK_DATE", YmCommonUtil.getCurDate("yyyyMMdd"));
//				tcRecord1.setField("MOVENSTACK_TIME", YmCommonUtil.getCurDate("HHmmss"));	
//				
//				//내부인터페이스 송신모듈 호출 
//				this.sendInternalModel(tcRecord1);
//			}
				

			
		} catch(Exception e) {
			
	    }
	    return true;
	 }	
	
	/**
	 * 오퍼레이션명 : 코일출하상차개시.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */		 
	public boolean getYDDMR007(JDTORecord model) {
		JDTORecord tcRecord1 = JDTORecordFactory.getInstance().create();
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
//			CARD_NO				카드 번호
//			CAR_NO				차량 번호
//			YD_GP				야드구분
//			CARLOAD_START_DATE	상차 개시 일자
//			CARLOAD_START_TIME	상차 개시 시각
//			TRANS_WORD_DATE		운송지시일자
//			TRANS_WORD_SEQNO	운송지시순번

			// PIDEV
//			String sApplyYnPI = commDAO.ApplyYnPI("", "YardWrkResReg => getYDDMR007 => 열연코일상차개시수신", "APPPI0", "*", "*");
			
//			if("Y".equals(sApplyYnPI)) {
				tcRecord1.setField("MQ_TC_CD"			, "M10YDLMJ1071"												);
				tcRecord1.setField("MQ_TC_CREATE_DDTT"	, new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss"))			);
				// tcRecord1.setField("CARD_NO",StringHelper.evl(model.getFieldString("CARD_NO"), ""));
				tcRecord1.setField("TRN_REQ_DATE"		, StringHelper.evl(model.getFieldString("TRANS_WORD_DATE"), "")	);
				tcRecord1.setField("TRN_REQ_SEQ"		, StringHelper.evl(model.getFieldString("TRANS_WORD_SEQNO"), ""));
				tcRecord1.setField("CAR_NO"				, StringHelper.evl(model.getFieldString("CAR_NO"), "")			);
				tcRecord1.setField("YD_GP"				, StringHelper.evl(model.getFieldString("YD_GP"), "")			);
				tcRecord1.setField("DIST_GOODS_GP"		, "H"															);
				tcRecord1.setField("SCH_YN"				, "N"															);
				tcRecord1.setField("CARLOAD_START_DATE", YmCommonUtil.getCurDate("yyyyMMdd")							);
				tcRecord1.setField("CARLOAD_START_TIME", YmCommonUtil.getCurDate("HHmmss")								);

				ymCommUtils.addSndData(tcRecord1, tcRecord1);
//			} else {
//				tcRecord1.setField("JMS_TC_CD", "YDDMR007");
//				tcRecord1.setField("TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
//				tcRecord1.setField("CARD_NO",StringHelper.evl(model.getFieldString("CARD_NO"), ""));
//				tcRecord1.setField("CAR_NO", StringHelper.evl(model.getFieldString("CAR_NO"), ""));
//				tcRecord1.setField("YD_GP", StringHelper.evl(model.getFieldString("YD_GP"), ""));
//				tcRecord1.setField("CARLOAD_START_DATE", YmCommonUtil.getCurDate("yyyyMMdd"));
//				tcRecord1.setField("CARLOAD_START_TIME", YmCommonUtil.getCurDate("HHmmss"));
//				tcRecord1.setField("TRANS_WORD_DATE", StringHelper.evl(model.getFieldString("TRANS_WORD_DATE"), ""));
//				tcRecord1.setField("TRANS_WORD_SEQNO", StringHelper.evl(model.getFieldString("TRANS_WORD_SEQNO"), ""));
//				
//				//내부인터페이스 송신모듈 호출 
//				this.sendInternalModel(tcRecord1);
//			}

			
		} catch(Exception e) {
			
	    }
        return true;
	 }
	
	
	
	 /**
	 * 오퍼레이션명 : 외판슬라브출하상차개시.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */  
	public boolean getYDDMR009(JDTORecord model) {
		    JDTORecord tcRecord1 = JDTORecordFactory.getInstance().create();
		    try{
				/*
				 * 구자원 단계별 삭제 로직  
				 */
				String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
				if(sAPP060_OLDSRC_YN.equals("Y")){
					return false;
				}
//		    	CARD_NO				카드 번호
//		    	CAR_NO				차량 번호
//		    	YD_GP				야드구분
//		    	CARLOAD_START_DATE	상차 개시 일자
//		    	CARLOAD_START_TIME	상차 개시 시각
//		    	TRANS_WORD_DATE		운송지시일자
//		    	TRANS_WORD_SEQNO	운송지시순번

		    tcRecord1.setField("JMS_TC_CD", "YDDMR009");
		    tcRecord1.setField("TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
		    tcRecord1.setField("CARD_NO",StringHelper.evl(model.getFieldString("CARD_NO"), ""));
		    tcRecord1.setField("CAR_NO", StringHelper.evl(model.getFieldString("CAR_NO"), ""));	
		    tcRecord1.setField("YD_GP", StringHelper.evl(model.getFieldString("YD_GP"), ""));
		    tcRecord1.setField("CARLOAD_START_DATE", YmCommonUtil.getCurDate("yyyyMMdd"));
		    tcRecord1.setField("CARLOAD_START_TIME", YmCommonUtil.getCurDate("HHmmss"));
		    tcRecord1.setField("TRANS_WORD_DATE", StringHelper.evl(model.getFieldString("TRANS_WORD_DATE"), ""));
		    tcRecord1.setField("TRANS_WORD_SEQNO", StringHelper.evl(model.getFieldString("TRANS_WORD_SEQNO"), ""));
		    
			//내부인터페이스 송신모듈 호출 
		    this.sendInternalModel(tcRecord1);
		    }
			catch(Exception e){
		    }
	        return true;
	    }
	
	/**
	 * 오퍼레이션명 : SLAB 운송LOT 편성정보 수신
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */  
	public boolean getYDDMR010(JDTORecord model) {
		    JDTORecord tcRecord1 = JDTORecordFactory.getInstance().create();
		    try{
				/*
				 * 구자원 단계별 삭제 로직  
				 */
				String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
				if(sAPP060_OLDSRC_YN.equals("Y")){
					return false;
				}
//		    	TRANS_LOT_DATE		운송LOT편성일자
//		    	TRANS_LOT_SEQ		운송LOT번호
//		    	CUST_CD				고객코드
//		    	DEST_CD				목적지코드
//		    	DEST_TEL_NO			목적지전화번호
//		    	DIST_SHPASSIGIN_GP	출하배선지시구분
//		    	CARD_NO				카드 번호
//		    	DIST_GOODS_GP		출하제품구분
//		    	YD_GP				YARD 구분
//		    	GOODS_EA			제품 개수
//		    	GOODS_NO			재품번호	OCCURS 20

		    	
		    tcRecord1.setField("JMS_TC_CD", "YDDMR010");
		    tcRecord1.setField("TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
		    tcRecord1.setField("TRANS_LOT_DATE",StringHelper.evl(model.getFieldString("TRANS_LOT_DATE"), ""));
		    tcRecord1.setField("TRANS_LOT_SEQ", StringHelper.evl(model.getFieldString("TRANS_LOT_SEQ"), ""));	
		    tcRecord1.setField("CUST_CD", StringHelper.evl(model.getFieldString("CUST_CD"), ""));
		    tcRecord1.setField("DEST_CD", StringHelper.evl(model.getFieldString("DEST_CD"), ""));
		    tcRecord1.setField("DEST_TEL_NO", StringHelper.evl(model.getFieldString("DEST_TEL_NO"), ""));
		    tcRecord1.setField("DIST_SHPASSIGIN_GP", StringHelper.evl(model.getFieldString("DIST_SHPASSIGIN_GP"), ""));
		    tcRecord1.setField("CARD_NO", StringHelper.evl(model.getFieldString("CARD_NO"), ""));
		    tcRecord1.setField("DIST_GOODS_GP", StringHelper.evl(model.getFieldString("DIST_GOODS_GP"), ""));
		    tcRecord1.setField("YD_GP", StringHelper.evl(model.getFieldString("YD_GP"), ""));
		    tcRecord1.setField("GOODS_EA", StringHelper.evl(model.getFieldString("GOODS_EA"), ""));
		    tcRecord1.setField("GOODS_NO", StringHelper.evl(model.getFieldString("GOODS_NO"), ""));
		    
			//내부인터페이스 송신모듈 호출 
		    this.sendInternalModel(tcRecord1);
		    }
			catch(Exception e){
		    }
	        return true;
	    }
	
	/**
	 * 오퍼레이션명 : 코일일품출하상차실적.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean getYDDMR011(JDTORecord model) {

		JDTORecord tcRecord1 = JDTORecordFactory.getInstance().create();
		
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
			if (sAPP060_OLDSRC_YN.equals("Y")) {
				return false;
			}
			
//		   	CARD_NO				카드 번호
//		    CAR_NO				차량 번호
//		    YD_GP				야드구분
//		    GOODS_EA			제품 개수
//		    GOODS_NO			제품 번호			OCCURS 20
//		    TRANS_WORD_DATE		운송지시일자
//		    TRANS_WORD_SEQNO	운송지시순번
	    	
			// PIDEV
//			String sApplyYnPI = commDAO.ApplyYnPI("", "YardWrkResReg => getYDDMR011", "APPPI0", "*", "*");
			
//			if ("Y".equals(sApplyYnPI)) {
			    tcRecord1.setField("MQ_TC_CD"			, "M10YDLMJ1081"												);
			    tcRecord1.setField("MQ_TC_CREATE_DDTT"	, new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss"))			);
			    tcRecord1.setField("TRN_REQ_DATE"		, StringHelper.evl(model.getFieldString("TRANS_WORD_DATE"), "")	);
			    tcRecord1.setField("TRN_REQ_SEQ"		, StringHelper.evl(model.getFieldString("TRANS_WORD_SEQNO"), ""));
			    tcRecord1.setField("CARD_NO"			, StringHelper.evl(model.getFieldString("CARD_NO"), "")			);
			    tcRecord1.setField("CAR_NO"				, StringHelper.evl(model.getFieldString("CAR_NO"), "")			);
			    tcRecord1.setField("YD_GP"				, StringHelper.evl(model.getFieldString("YD_GP"), "")			);
			    tcRecord1.setField("DIST_GOODS_GP"		, "H"															);
			    tcRecord1.setField("SCH_YN"				, "N"															);
			    tcRecord1.setField("GOODS_NO"     		, StringHelper.evl(model.getFieldString("GOODS_NO"), "").trim()	);
			    tcRecord1.setField("GOODS_EA"      		, StringHelper.evl(model.getFieldString("GOODS_EA"), "1") 		);	

			    ymCommUtils.addSndData(tcRecord1, tcRecord1);
//			} else {
//			    tcRecord1.setField("JMS_TC_CD", "YDDMR011");
//			    tcRecord1.setField("TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
//			    tcRecord1.setField("CARD_NO",StringHelper.evl(model.getFieldString("CARD_NO"), ""));
//			    tcRecord1.setField("CAR_NO", StringHelper.evl(model.getFieldString("CAR_NO"), ""));
//			    tcRecord1.setField("YD_GP", StringHelper.evl(model.getFieldString("YD_GP"), ""));		
//			    tcRecord1.setField("GOODS_EA"      ,StringHelper.evl(model.getFieldString("GOODS_EA"), "1") );	
//			    tcRecord1.setField("GOODS_NO"      , StringHelper.evl(model.getFieldString("GOODS_NO"), "").trim());
//			    tcRecord1.setField("TRANS_WORD_DATE", StringHelper.evl(model.getFieldString("TRANS_WORD_DATE"), ""));
//			    tcRecord1.setField("TRANS_WORD_SEQNO", StringHelper.evl(model.getFieldString("TRANS_WORD_SEQNO"), ""));
//			    
//				//내부인터페이스 송신모듈 호출 
//			    this.sendInternalModel(tcRecord1);			    
//			}
				    

		    
	    } catch(Exception e) {
		
	    }
		
	    return true;
	}

	 /**
	 * 오퍼레이션명 : 외판슬라브일품출하상차실적.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean getYDDMR013(JDTORecord model) {
		   JDTORecord tcRecord1 = JDTORecordFactory.getInstance().create();
		   try{
				/*
				 * 구자원 단계별 삭제 로직  
				 */
				String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
				if(sAPP060_OLDSRC_YN.equals("Y")){
					return false;
				}
//			   CARD_NO			카드 번호
//			   CAR_NO			차량 번호
//			   YD_GP			야드구분
//			   GOODS_EA			제품 개수
//			   GOODS_NO			제품 번호		OCCURS 20
//			   TRANS_WORD_DATE	운송지시일자
//			   TRANS_WORD_SEQNO	운송지시순번

			   
		   tcRecord1 = JDTORecordFactory.getInstance().create();
		   tcRecord1.setField("JMS_TC_CD", "YDDMR013");
		   tcRecord1.setField("TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
		   tcRecord1.setField("CARD_NO",StringHelper.evl(model.getFieldString("CARD_NO"), ""));
		   tcRecord1.setField("CAR_NO", StringHelper.evl(model.getFieldString("CAR_NO"), ""));	
		   tcRecord1.setField("YD_GP", StringHelper.evl(model.getFieldString("YD_GP"), ""));
		   tcRecord1.setField("GOODS_EA"        ,StringHelper.evl(model.getFieldString("GOODS_EA"), ""));
		   tcRecord1.setField("GOODS_NO"        , StringHelper.evl(model.getFieldString("GOODS_NO"), "").trim());
		   tcRecord1.setField("TRANS_WORD_DATE", StringHelper.evl(model.getFieldString("TRANS_WORD_DATE"), ""));
		   tcRecord1.setField("TRANS_WORD_SEQNO", StringHelper.evl(model.getFieldString("TRANS_WORD_SEQNO"), ""));
		   
			//내부인터페이스 송신모듈 호출 
		   this.sendInternalModel(tcRecord1);
		   }
			catch(Exception e){
		    }
	        return true;
	    }

	

	 
	/**
	 * 오퍼레이션명 : 코일출하상차완료.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */		 
	public boolean getYDDMR015(JDTORecord model) {
		JDTORecord tcRecord1 = JDTORecordFactory.getInstance().create();
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
//			CARD_NO				카드 번호
//			CAR_NO				차량 번호
//			YD_GP				야드구분
//			CARLOAD_END_DATE	상차 완료 일자
//			CARLOAD_END_TIME	상차 완료 시각
//			TRANS_WORD_DATE		운송지시일자
//			TRANS_WORD_SEQNO	운송지시순번

			// PIDEV
//			String sApplyYnPI = commDAO.ApplyYnPI("", "YardWrkResReg => getYDDMR015", "APPPI0", "*", "*");
//			
//			if ("Y".equals(sApplyYnPI)) {
				tcRecord1.setField("MQ_TC_CD"			, "M10YDLMJ1091");
				tcRecord1.setField("MQ_TC_CREATE_DDTT" 	, new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
				tcRecord1.setField("TRN_REQ_DATE"		, StringHelper.evl(model.getFieldString("TRANS_WORD_DATE"), ""));
				tcRecord1.setField("TRN_REQ_SEQ"		, StringHelper.evl(model.getFieldString("TRANS_WORD_SEQNO"), ""));	
				tcRecord1.setField("CARD_NO"			, StringHelper.evl(model.getFieldString("CARD_NO"), ""));
				tcRecord1.setField("CAR_NO"				, StringHelper.evl(model.getFieldString("CAR_NO"), ""));
				tcRecord1.setField("YD_GP"				, StringHelper.evl(model.getFieldString("YD_GP"), ""));
				tcRecord1.setField("DIST_GOODS_GP"		, "H");
				tcRecord1.setField("SCH_YN"				, "N");
				tcRecord1.setField("CARLD_CMPL_DATE"	, YmCommonUtil.getCurDate("yyyyMMdd"));
				tcRecord1.setField("CARLD_CMPL_TIME"	, YmCommonUtil.getCurDate("HHmmss"));
				
			    ymCommUtils.addSndData(tcRecord1, tcRecord1);				
//			} else {
//				tcRecord1.setField("JMS_TC_CD", "YDDMR015");
//				tcRecord1.setField("TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
//				tcRecord1.setField("CARD_NO",StringHelper.evl(model.getFieldString("CARD_NO"), ""));
//				tcRecord1.setField("CAR_NO", StringHelper.evl(model.getFieldString("CAR_NO"), ""));
//				tcRecord1.setField("YD_GP", StringHelper.evl(model.getFieldString("YD_GP"), ""));		
//				tcRecord1.setField("CARLOAD_END_DATE", YmCommonUtil.getCurDate("yyyyMMdd"));
//				tcRecord1.setField("CARLOAD_END_TIME", YmCommonUtil.getCurDate("HHmmss"));
//				tcRecord1.setField("TRANS_WORD_DATE", StringHelper.evl(model.getFieldString("TRANS_WORD_DATE"), ""));
//				tcRecord1.setField("TRANS_WORD_SEQNO", StringHelper.evl(model.getFieldString("TRANS_WORD_SEQNO"), ""));
//				
//				//내부인터페이스 송신모듈 호출 
//				this.sendInternalModel(tcRecord1);
//			}
			

		} catch(Exception e) {
			
	    }
	    
        return true;
    }
	
	 /**
	 * 오퍼레이션명 : 외판슬라브출하상차완료.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean getYDDMR017(JDTORecord model) {
		JDTORecord tcRecord1 = JDTORecordFactory.getInstance().create();
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
			if (sAPP060_OLDSRC_YN.equals("Y")) {
				return false;
			}
//		    CARD_NO				카드 번호
//		    CAR_NO				차량 번호
//		    YD_GP				야드구분
//		    CARLOAD_END_DATE	상차 완료 일자
//		    CARLOAD_END_TIME	상차 완료 시각
//		    TRANS_WORD_DATE		운송지시일자
//		    TRANS_WORD_SEQNO	운송지시순번

		    tcRecord1.setField("JMS_TC_CD", "YDDMR017");
		    tcRecord1.setField("TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
		    tcRecord1.setField("CARD_NO",StringHelper.evl(model.getFieldString("CARD_NO"), ""));
		    tcRecord1.setField("CAR_NO", StringHelper.evl(model.getFieldString("CAR_NO"), ""));	
		    tcRecord1.setField("YD_GP", StringHelper.evl(model.getFieldString("YD_GP"), ""));
		    tcRecord1.setField("CARLOAD_END_DATE", YmCommonUtil.getCurDate("yyyyMMdd"));
			tcRecord1.setField("CARLOAD_END_TIME", YmCommonUtil.getCurDate("HHmmss"));
			tcRecord1.setField("TRANS_WORD_DATE", StringHelper.evl(model.getFieldString("TRANS_WORD_DATE"), ""));
			tcRecord1.setField("TRANS_WORD_SEQNO", StringHelper.evl(model.getFieldString("TRANS_WORD_SEQNO"), ""));
			
			//내부인터페이스 송신모듈 호출 
			this.sendInternalModel(tcRecord1);
			
		} catch(Exception e) {
			
	    }
		
        return true;
    }

	/**
	 * 오퍼레이션명 : 코일제품고간이송상하차개시 .
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean getYDDMR019(JDTORecord model) {
		JDTORecord tcRecord1 = JDTORecordFactory.getInstance().create();
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
			if (sAPP060_OLDSRC_YN.equals("Y")) {
				return false;
			}
//			UPCARUNLOAD_GP		상하차 구분
//			CARD_NO				카드 번호
//			CAR_NO				차량 번호
//			YD_GP				야드구분
//			CARLOAD_START_DATE	상차 개시 일자
//			CARLOAD_START_TIME	상차 개시 시각
//			TRANS_WORD_DATE		이송지시일자
//			TRANS_WORD_SEQNO	이송지시순번

			tcRecord1.setField("JMS_TC_CD", "YDDMR019");
			tcRecord1.setField("TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
			tcRecord1.setField("UPCARUNLOAD_GP", StringHelper.evl(model.getFieldString("UPCARUNLOAD_GP"), ""));
			tcRecord1.setField("CARD_NO",StringHelper.evl(model.getFieldString("CARD_NO"), ""));
			tcRecord1.setField("CAR_NO", StringHelper.evl(model.getFieldString("CAR_NO"), ""));
			tcRecord1.setField("YD_GP", StringHelper.evl(model.getFieldString("YD_GP"), ""));		
			tcRecord1.setField("CARLOAD_START_DATE", YmCommonUtil.getCurDate("yyyyMMdd"));
			tcRecord1.setField("CARLOAD_START_TIME", YmCommonUtil.getCurDate("HHmmss"));
			tcRecord1.setField("TRANS_WORD_DATE", StringHelper.evl(model.getFieldString("TRANS_WORD_DATE"), ""));
			tcRecord1.setField("TRANS_WORD_SEQNO", StringHelper.evl(model.getFieldString("TRANS_WORD_SEQNO"), ""));	  
			
			//내부인터페이스 송신모듈 호출 
			this.sendInternalModel(tcRecord1);
			
		} catch(Exception e) {
			
	    }
		
        return true;
	}
	
	/**
	 * 오퍼레이션명 : 임가공이송상하차개시.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean getYDDMR020(JDTORecord model) {
		JDTORecord tcRecord1 = JDTORecordFactory.getInstance().create();
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
//			UPCARUNLOAD_GP		상하차 구분
//			CARD_NO				카드 번호
//			CAR_NO				차량 번호
//			YD_GP				야드구분
//			CARLOAD_START_DATE	상차 개시 일자
//			CARLOAD_START_TIME	상차 개시 시각
//			TRANS_WORD_DATE		이송지시일자
//			TRANS_WORD_SEQNO	이송지시순번

			
		tcRecord1.setField("JMS_TC_CD", "YDDMR020");
		tcRecord1.setField("TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
		tcRecord1.setField("UPCARUNLOAD_GP", StringHelper.evl(model.getFieldString("UPCARUNLOAD_GP"), ""));
		tcRecord1.setField("CARD_NO",StringHelper.evl(model.getFieldString("CARD_NO"), ""));
		tcRecord1.setField("CAR_NO", StringHelper.evl(model.getFieldString("CAR_NO"), ""));
		tcRecord1.setField("YD_GP", StringHelper.evl(model.getFieldString("YD_GP"), ""));		
		tcRecord1.setField("CARLOAD_START_DATE", YmCommonUtil.getCurDate("yyyyMMdd"));
		tcRecord1.setField("CARLOAD_START_TIME", YmCommonUtil.getCurDate("HHmmss"));
		tcRecord1.setField("TRANS_WORD_DATE", StringHelper.evl(model.getFieldString("TRANS_WORD_DATE"), ""));
		tcRecord1.setField("TRANS_WORD_SEQNO", StringHelper.evl(model.getFieldString("TRANS_WORD_SEQNO"), ""));	  
		
		//내부인터페이스 송신모듈 호출 
		this.sendInternalModel(tcRecord1);
		}
		catch(Exception e){
	    }
        return true;
	}
	

	/**
	 * 오퍼레이션명 : 코일제품고간이송상하차완료.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean getYDDMR021(JDTORecord model) {
		JDTORecord tcRecord1 = JDTORecordFactory.getInstance().create();
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
			if (sAPP060_OLDSRC_YN.equals("Y")) {
				return false;
			}

			// PIDEV
//			String sApplyYnPI = commDAO.ApplyYnPI("", "YardWrkResReg => getYDDMR021", "APPPI0", "*", "*");
//			
//			if("Y".equals(sApplyYnPI)) {
				tcRecord1.setField("MQ_TC_CD", "M10YDLMJ1121");
				tcRecord1.setField("MQ_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
				tcRecord1.setField("TRANS_WORD_DATE" , StringHelper.evl(model.getFieldString("TRANS_WORD_DATE"), ""));
				tcRecord1.setField("TRANS_WORD_SEQNO" , StringHelper.evl(model.getFieldString("TRANS_WORD_SEQNO"), ""));
				tcRecord1.setField("CAR_NO", StringHelper.evl(model.getFieldString("CAR_NO"), ""));
				tcRecord1.setField("YD_GP" , StringHelper.evl(model.getFieldString("YD_GP"), ""));
				tcRecord1.setField("DIST_GOODS_GP" , "H");
				tcRecord1.setField("YARD_GP" , "");
				tcRecord1.setField("UPCARUNLOAD_GP", StringHelper.evl(model.getFieldString("UPCARUNLOAD_GP"), ""));
				tcRecord1.setField("ARR_YD_PNT_CD", StringHelper.evl(model.getFieldString("ARR_YD_PNT_CD"), ""));
				tcRecord1.setField("CARUD_CMPL_DATE", new String(YmCommonUtil.getTcDate("yyyyMMdd")));
				tcRecord1.setField("CARUD_CMPL_TIME", new String(YmCommonUtil.getTcDate("HHmmss")));
				tcRecord1.setField("GOODS_CNT", StringHelper.evl(model.getFieldString("TREAT_EA"), ""));
				tcRecord1.setField("GOODS_NO1" , StringHelper.evl(model.getFieldString("GOODS_NO"), ""));
				tcRecord1.setField("STORE_LOC_CD1" , StringHelper.evl(model.getFieldString("STORE_LOC_CD"), ""));

			    ymCommUtils.addSndData(tcRecord1, tcRecord1);				
//			} else {
//				UPCARUNLOAD_GP		상하차 구분
//				CARD_NO				카드 번호
//				CAR_NO				차량 번호
//				ARR_YD_PNT_CD      	착지야드포인트코드
//				ISSUE_DDTT			발생일시
//				TREAT_EA			처리개수
//				GOODS_NO			재료번호			반복 20 회
//				TRANS_WORD_DATE		이송지시일자		반복 20 회
//				TRANS_WORD_SEQNO	이송지시순번		반복 20 회
//				STORE_LOC_CD		저장위치코드TO		반복 20 회
//				YD_GP				야드구분			반복 20 회
//				BAY_GP				동구분			반복 20 회
//				SPAN				SPAN			반복 20 회
//				STK_LYR				적치단			반복 20 회

//				tcRecord1.setField("JMS_TC_CD", "YDDMR021");
//				tcRecord1.setField("TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
//				tcRecord1.setField("UPCARUNLOAD_GP", StringHelper.evl(model.getFieldString("UPCARUNLOAD_GP"), ""));
//				tcRecord1.setField("CARD_NO",StringHelper.evl(model.getFieldString("CARD_NO"), ""));
//				tcRecord1.setField("CAR_NO", StringHelper.evl(model.getFieldString("CAR_NO"), ""));					    	
//				tcRecord1.setField("ARR_YD_PNT_CD", StringHelper.evl(model.getFieldString("ARR_YD_PNT_CD"), ""));
//				tcRecord1.setField("ISSUE_DDTT", StringHelper.evl(model.getFieldString("ISSUE_DDTT"), ""));
//				tcRecord1.setField("TREAT_EA", StringHelper.evl(model.getFieldString("TREAT_EA"), ""));
//				tcRecord1.setField("GOODS_NO" , StringHelper.evl(model.getFieldString("GOODS_NO"), ""));
//				tcRecord1.setField("TRANS_WORD_DATE" , StringHelper.evl(model.getFieldString("TRANS_WORD_DATE"), ""));
//				tcRecord1.setField("TRANS_WORD_SEQNO" , StringHelper.evl(model.getFieldString("TRANS_WORD_SEQNO"), ""));
//				tcRecord1.setField("STORE_LOC_CD" , StringHelper.evl(model.getFieldString("STORE_LOC_CD"), ""));
//				tcRecord1.setField("YD_GP" , StringHelper.evl(model.getFieldString("YD_GP"), ""));
//				tcRecord1.setField("BAY_GP" , StringHelper.evl(model.getFieldString("BAY_GP"), ""));
//		    	tcRecord1.setField("SPAN" , StringHelper.evl(model.getFieldString("SPAN"), ""));
//		    	tcRecord1.setField("STK_LYR" , StringHelper.evl(model.getFieldString("STK_LYR"), ""));
//		    	
//				//내부인터페이스 송신모듈 호출 
//		    	this.sendInternalModel(tcRecord1);		    	
//			}

		} catch(Exception e) {
			
	    }
		
        return true;
	 }
	
	/**
	 * 오퍼레이션명 : 임가공이송상하차완료.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */	 
	public boolean getYDDMR022(JDTORecord model) {
		    JDTORecord tcRecord1 = JDTORecordFactory.getInstance().create();
		    try{
				/*
				 * 구자원 단계별 삭제 로직  
				 */
				String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
				if(sAPP060_OLDSRC_YN.equals("Y")){
					return false;
				}
//		    	UPCARUNLOAD_GP		상하차 구분
//		    	CARD_NO				카드 번호
//		    	CAR_NO				차량 번호
//		    	YD_GP				야드구분
//		    	CARLOAD_DONE_DATE	상차 완료 일자
//		    	CARLOAD_DONE_TIME	상차 완료 시각
//		    	TRANS_WORD_DATE		이송지시일자
//		    	TRANS_WORD_SEQNO	이송지시순번

		    	
		    tcRecord1.setField("JMS_TC_CD", "YDDMR022");
		    tcRecord1.setField("TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
		    tcRecord1.setField("UPCARUNLOAD_GP", StringHelper.evl(model.getFieldString("UPCARUNLOAD_GP"), ""));
		    tcRecord1.setField("CARD_NO",StringHelper.evl(model.getFieldString("CARD_NO"), ""));
		    tcRecord1.setField("CAR_NO", StringHelper.evl(model.getFieldString("CAR_NO"), ""));				    
		    tcRecord1.setField("YD_GP", StringHelper.evl(model.getFieldString("YD_GP"), ""));
		    tcRecord1.setField("CARLOAD_DONE_DATE", YmCommonUtil.getCurDate("yyyyMMdd"));
		    tcRecord1.setField("CARLOAD_DONE_TIME", YmCommonUtil.getCurDate("HHmmss"));	
		    tcRecord1.setField("TRANS_WORD_DATE", StringHelper.evl(model.getFieldString("TRANS_WORD_DATE"), ""));
		    tcRecord1.setField("TRANS_WORD_SEQNO", StringHelper.evl(model.getFieldString("TRANS_WORD_SEQNO"), ""));
		    
			//내부인터페이스 송신모듈 호출 
		    this.sendInternalModel(tcRecord1);
		    }
			catch(Exception e){
		    }
	        return true;
	    }
	 
	
	/**
	 * 오퍼레이션명 : HYSCO대차이송실적
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */	 
	public boolean getYDDMR024(JDTORecord model) {
	    JDTORecord tcRecord1 = JDTORecordFactory.getInstance().create();
	    try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
	    	String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
			if (sAPP060_OLDSRC_YN.equals("Y")) {
				return false;
			}
			
//		    GOODS_NO			제품 번호
//		    FROM_STORE_LOC_CD	FROM저장 위치 CODE
//		    TRANS_DATE			이송일자
//		    TRANS_TIME			이송시각

			// PIDEV
//			String sApplyYnPI = commDAO.ApplyYnPI("", "YardWrkResReg => getYDDMR024", "APPPI0", "*", "*");
//			
//			if ("Y".equals(sApplyYnPI)) {
				tcRecord1 = JDTORecordFactory.getInstance().create();
				tcRecord1.setField("MQ_TC_CD"			, "M10YDLMJ1041");				
				tcRecord1.setField("MQ_TC_CREATE_DDTT"	, new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")) );
				tcRecord1.setField("YD_GP"				, "3");
				tcRecord1.setField("DIST_GOODS_GP"		, "H");
				tcRecord1.setField("GOODS_NO"          	, StringHelper.evl(model.getFieldString("GOODS_NO"), "").trim() );
				tcRecord1.setField("STORE_LOC_CD_FROM" 	, StringHelper.evl(model.getFieldString("FROM_STORE_LOC_CD"), "") );
				tcRecord1.setField("TRANS_DATE"        	, YmCommUtils.getCurDate("yyyyMMdd"));
				tcRecord1.setField("TRANS_TIME"        	, YmCommUtils.getCurDate("HHmmss"));	

			    ymCommUtils.addSndData(tcRecord1, tcRecord1);				
//			} else {
//			    tcRecord1.setField("JMS_TC_CD", "YDDMR024");
//			    tcRecord1.setField("TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
//			    tcRecord1.setField("GOODS_NO" ,  StringHelper.evl(model.getFieldString("GOODS_NO"), "").trim());
//			    tcRecord1.setField("FROM_STORE_LOC_CD" ,  StringHelper.evl(model.getFieldString("FROM_STORE_LOC_CD"), ""));
//			    tcRecord1.setField("TRANS_DATE" ,  YmCommonUtil.getCurDate("yyyyMMdd"));
//			    tcRecord1.setField("TRANS_TIME" ,  YmCommonUtil.getCurDate("HHmmss"));		
//			    tcRecord1.setField("CR_FRTOMOVE_GP" ,  "82"); //냉연KEY	
//			    
//				//내부인터페이스 송신모듈 호출 
//			    this.sendInternalModel(tcRecord1);			    
//			}
	    

		    
	    } catch(Exception e) {
	    	
	    }
	    
        return true;
    }
	
	 /**
	 * 오퍼레이션명 : HYSCO수냉실적.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean getYDDMR025(JDTORecord model) {
		    JDTORecord tcRecord1 = JDTORecordFactory.getInstance().create(); 
		    try{
				/*
				 * 구자원 단계별 삭제 로직  
				 */
				String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
				if(sAPP060_OLDSRC_YN.equals("Y")){
					return false;
				}
//		    	GOODS_NO			제품 번호

		    tcRecord1.setField("JMS_TC_CD", "YDDMR025");
		    tcRecord1.setField("TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
		    tcRecord1.setField("GOODS_NO"        , StringHelper.evl(model.getFieldString("GOODS_NO"), "").trim());  
		    
			//내부인터페이스 송신모듈 호출 
		    this.sendInternalModel(tcRecord1);
		    }
			catch(Exception e){
		    }
	        return true;
	}
	
	 /**
	 * 오퍼레이션명 : 포인트사용실적.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean getYDDMR026(JDTORecord model) {
		    JDTORecord tcRecord1 = JDTORecordFactory.getInstance().create(); 
		    try{	
				/*
				 * 구자원 단계별 삭제 로직  
				 */
				String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
				if(sAPP060_OLDSRC_YN.equals("Y")){
					return false;
				}
//		    	WLOC_CD					개소코드
//		    	YD_PNT_CD				야드포인트코드
//		    	YD_PNT_OCPY_GP			야드포인트점유구분
//		    	YD_PNT_UNIT_CL_GP		야드포인트개폐구분
//		    	LOAN_PULLOUT_ABLE_YN	차입인출가능여부
//		    	OCPY_TRN_EQP_CD			점유운송장비코드


		    tcRecord1.setField("JMS_TC_CD", "YDDMR026");
		    tcRecord1.setField("TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
		    tcRecord1.setField("WLOC_CD" ,  StringHelper.evl(model.getFieldString("WLOC_CD"), ""));
		    tcRecord1.setField("YD_PNT_CD" ,  StringHelper.evl(model.getFieldString("YD_PNT_CD"), ""));
		    tcRecord1.setField("YD_PNT_OCPY_GP" ,  StringHelper.evl(model.getFieldString("YD_PNT_OCPY_GP"), ""));
		    tcRecord1.setField("YD_PNT_UNIT_CL_GP" ,  StringHelper.evl(model.getFieldString("YD_PNT_UNIT_CL_GP"), ""));
		    tcRecord1.setField("LOAN_PULLOUT_ABLE_YN" ,  StringHelper.evl(model.getFieldString("LOAN_PULLOUT_ABLE_YN"), ""));
		    tcRecord1.setField("OCPY_TRN_EQP_CD" ,  StringHelper.evl(model.getFieldString("OCPY_TRN_EQP_CD"), ""));
		    
			//내부인터페이스 송신모듈 호출 
		    this.sendInternalModel(tcRecord1);
		    }
			catch(Exception e){
		    }
	        return true;
	}
	
	
	 /**
	 * 오퍼레이션명 : 검수완료실적.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean getYDDMR027(JDTORecord model) {
		    JDTORecord tcRecord1 = JDTORecordFactory.getInstance().create(); 
		    try{	
				/*
				 * 구자원 단계별 삭제 로직  
				 */
				String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
				if(sAPP060_OLDSRC_YN.equals("Y")){
					return false;
				}
//		    	CARD_NO				카드 번호
//		    	CAR_NO				차량 번호
//		    	YD_ISSUE_CHK_DT		야드출고검수완료일시
//		    	ISSUE_CHK_WORKER	출고검수작업자
//		    	TRANS_WORD_DATE		운송지시일자
//		    	TRANS_WORD_SEQNO	운송지시순번


		    tcRecord1.setField("JMS_TC_CD", "YDDMR027");
		    tcRecord1.setField("TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
		    tcRecord1.setField("CARD_NO" ,  StringHelper.evl(model.getFieldString("CARD_NO"), ""));
		    tcRecord1.setField("CAR_NO" ,  StringHelper.evl(model.getFieldString("CAR_NO"), ""));
		    tcRecord1.setField("YD_ISSUE_CHK_DT" ,  StringHelper.evl(model.getFieldString("YD_ISSUE_CHK_DT"), ""));
		    tcRecord1.setField("ISSUE_CHK_WORKER" ,  StringHelper.evl(model.getFieldString("ISSUE_CHK_WORKER"), ""));
		    tcRecord1.setField("TRANS_WORD_DATE" ,  StringHelper.evl(model.getFieldString("TRANS_ORD_DATE"), ""));
		    tcRecord1.setField("TRANS_WORD_SEQNO" ,  StringHelper.evl(model.getFieldString("TRANS_ORD_SEQNO"), ""));
		    
			//내부인터페이스 송신모듈 호출 
		    this.sendInternalModel(tcRecord1);
		    }
			catch(Exception e){
		    }
	        return true;
	}
	
	
	 /**
	 * 오퍼레이션명 : 차량입동지시.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean getYDDMR028(JDTORecord model) {
		JDTORecord tcRecord1 = JDTORecordFactory.getInstance().create(); 
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
			if (sAPP060_OLDSRC_YN.equals("Y")) {
				return false;
			}
			
//		    CARD_NO					카드 번호
//		    CAR_NO					차량 번호
//		    BAYIN_DDTT				입동일시
//		    WLOC_CD					개소코드
//		    YD_PNT_CD				야드포인트코드
//		    LOAN_PULLOUT_ABLE_YN	차입인출가능여부

			// PIDEV
//			String sApplyYnPI = commDAO.ApplyYnPI("", "YardWrkResReg => getYDDMR028", "APPPI0", "*", "*");
//			
//			if("Y".equals(sApplyYnPI)) {
			    tcRecord1.setField("MQ_TC_CD", "M10YDLMJ1061");
			    tcRecord1.setField("MQ_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
			    tcRecord1.setField("CARD_NO" ,  StringHelper.evl(model.getFieldString("CARD_NO"), ""));
			    tcRecord1.setField("CAR_NO" ,  StringHelper.evl(model.getFieldString("CAR_NO"), ""));
			    tcRecord1.setField("BAYIN_DDTT" ,  StringHelper.evl(model.getFieldString("BAYIN_DDTT"), ""));
			    tcRecord1.setField("WLOC_CD" ,  StringHelper.evl(model.getFieldString("WLOC_CD"), ""));
			    tcRecord1.setField("YD_PNT_CD" ,  StringHelper.evl(model.getFieldString("YD_PNT_CD"), ""));
			    tcRecord1.setField("LOAN_PULLOUT_ABLE_YN" ,  StringHelper.evl(model.getFieldString("LOAN_PULLOUT_ABLE_YN"), ""));
			    
			    ymCommUtils.addSndData(tcRecord1, tcRecord1);
//			} else {
//			    tcRecord1.setField("JMS_TC_CD", "YDDMR028");
//			    tcRecord1.setField("TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
//			    tcRecord1.setField("CARD_NO" ,  StringHelper.evl(model.getFieldString("CARD_NO"), ""));
//			    tcRecord1.setField("CAR_NO" ,  StringHelper.evl(model.getFieldString("CAR_NO"), ""));
//			    tcRecord1.setField("BAYIN_DDTT" ,  StringHelper.evl(model.getFieldString("BAYIN_DDTT"), ""));
//			    tcRecord1.setField("WLOC_CD" ,  StringHelper.evl(model.getFieldString("WLOC_CD"), ""));
//			    tcRecord1.setField("YD_PNT_CD" ,  StringHelper.evl(model.getFieldString("YD_PNT_CD"), ""));
//			    tcRecord1.setField("LOAN_PULLOUT_ABLE_YN" ,  StringHelper.evl(model.getFieldString("LOAN_PULLOUT_ABLE_YN"), ""));
//			    
//				//내부인터페이스 송신모듈 호출 
//			    this.sendInternalModel(tcRecord1);			    
//			}
		    
	    } catch(Exception e) {
	    	
	    }
		
        return true;
	}
	 /**
	 * 오퍼레이션명 : AB열연코일제품출하차량도착실적.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean getYDDMR029(JDTORecord model) {
		    JDTORecord tcRecord1 = JDTORecordFactory.getInstance().create(); 
		    try{	
				/*
				 * 구자원 단계별 삭제 로직  
				 */
				String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC3");
				if(sAPP060_OLDSRC_YN.equals("Y")){
					return false;
				}
//		    	TC_CODE			인터페이스ID
//		    	TC_CREATE_DDTT	전송일시
//		    	YD_GP			야드구분
//		    	TRANS_ORD_DT	운송지시일자
//		    	TRANS_ORD_SEQNO	운송지시순번
//		    	CAR_NO			차량번호
//		    	CARD_NO			카드번호
//		    	ARR_WLOC_CD     착지개소코드
//		    	ARR_YD_PNT_CD   착지야드포인트코드
//		    	CAR_ARR_DT	차량도착일시


		    tcRecord1.setField("JMS_TC_CD", "YDDMR029");
		    tcRecord1.setField("TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
		    tcRecord1.setField("YD_GP" ,  StringHelper.evl(model.getFieldString("YD_GP"), ""));
		    tcRecord1.setField("TRANS_WORD_DATE" ,  StringHelper.evl(model.getFieldString("TRANS_WORD_DATE"), ""));
		    tcRecord1.setField("TRANS_WORD_SEQNO" ,  StringHelper.evl(model.getFieldString("TRANS_WORD_SEQNO"), ""));
		    tcRecord1.setField("CAR_NO" ,  StringHelper.evl(model.getFieldString("CAR_NO"), ""));
		    tcRecord1.setField("CARD_NO" ,  StringHelper.evl(model.getFieldString("CARD_NO"), ""));
		    tcRecord1.setField("ARR_WLOC_CD" ,  StringHelper.evl(model.getFieldString("ARR_WLOC_CD"), ""));
		    tcRecord1.setField("ARR_YD_PNT_CD" ,  StringHelper.evl(model.getFieldString("ARR_YD_PNT_CD"), ""));
		    tcRecord1.setField("CAR_ARR_DT" ,   new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
		    
			//내부인터페이스 송신모듈 호출 
		    this.sendInternalModel(tcRecord1);
		    }
			catch(Exception e){
		    }
	        return true;
	}
}




