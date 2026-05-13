package com.inisteel.cim.ym.brentproc.session;

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
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdEquipDAO;
import com.inisteel.cim.ym.wrecord.wrecordif.dao.YdWRsltDAO;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.bcommon.util.YmCommUtils;

/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="YMYardWrkResRegEJB" jndi-name="JNDIYMYardWrkResReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class YMYardWrkResRegSBean extends BaseSessionBean {
    private Logger logger 			= null;
    private ymCommonDAO ymCommonDAO = null;
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
        
        createHashMapInfo();
	}
	/**
	 * AB TC ID 와 일관제철 NEW TC_ID Mapping Table Info 
	 */
	private void createHashMapInfo(){
		
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
	///////////////////////////////////////////////////////
	/////일관제철 내부인터페이스 시작 ///////////////////
	///////////////////////////////////////////////////////   
	/**
	* 모델에 따른 도메인 NAME을 리턴한다.
	* @param tcCode	전문코드
	* @return
	*/
	private String getNewDomain(String sTcCode) {
		
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
			
			JDTORecord jrChk = ymComm.getNewModuleEffYn_YM();
			// 신규 적용
			String poChk = jrChk.getFieldString("PO_EFF_YN");
			if(poChk.equals("Y")) {
				String tcCode = model.getTcCode();

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
	 * 오퍼레이션명 : 임가공이송상하차개시 PIDEV
	 * YDDMR020
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean getM10YDLMJ1115(JDTORecord model) {
		JDTORecord tcRecord1 = JDTORecordFactory.getInstance().create();
		
		 logger.println(LogLevel.DEBUG,this, "내부IF호출 시작 === 임가공이송상하차개시(M10YDLMJ1115) ===");
		
		 JDTORecord jRtn = JDTORecordFactory.getInstance().create();
		 
		try{
//			UPCARUNLOAD_GP		상하차 구분
//			CARD_NO				카드 번호
//			CAR_NO				차량 번호
//			YD_GP				야드구분
//			CARLOAD_START_DATE	상차 개시 일자
//			CARLOAD_START_TIME	상차 개시 시각
//			TRANS_WORD_DATE		이송지시일자
//			TRANS_WORD_SEQNO	이송지시순번

			String sYD_GP = StringHelper.evl(model.getFieldString("YD_GP"), "");
			
			String sYARD_GP = "";
			if("5".equals(sYD_GP)) {
				sYARD_GP = "DX3B";
			} else if("7".equals(sYD_GP)) {
				sYARD_GP = "DH1G";
			} else if("N".equals(sYD_GP)) {
				sYARD_GP = "DH1J";
			} else if("Q".equals(sYD_GP)) {
				sYARD_GP = "DX3A";
			}
			
				
			tcRecord1.setField("MQ_TC_CD", "M10YDLMJ1115");
			tcRecord1.setField("MQ_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
			tcRecord1.setField("TRN_REQ_DATE", StringHelper.evl(model.getFieldString("TRANS_WORD_DATE"), ""));
			tcRecord1.setField("TRN_REQ_SEQ", StringHelper.evl(model.getFieldString("TRANS_WORD_SEQNO"), ""));		
			tcRecord1.setField("CAR_NO", StringHelper.evl(model.getFieldString("CAR_NO"), ""));
			tcRecord1.setField("YD_GP", sYD_GP);		
			tcRecord1.setField("DIST_GOODS_GP", "H");		
			tcRecord1.setField("YARD_GP", sYARD_GP);		
			tcRecord1.setField("CARUD_ST_DATE", YmCommonUtil.getCurDate("yyyyMMdd"));
			tcRecord1.setField("CARUD_ST_TIME", YmCommonUtil.getCurDate("HHmmss"));
		  
			jRtn = ymCommUtils.addSndData(tcRecord1, tcRecord1);
			
			EJBConnector ydEjbCon = new EJBConnector("default", this);
			ydEjbCon.trx("YmCommEJB", "sndInterface", jRtn);			
			
		}
		catch(Exception e){
			logger.println(LogLevel.DEBUG,this, "내부IF호출 ERROR === 임가공이송상하차개시(M10YDLMJ1115) ===");
			logger.println(LogLevel.DEBUG,this, e.getMessage());
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
	 * 임가공 PIDEV
	 * 오퍼레이션명 : 임가공이송상하차완료.
	 * YDDMR022
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */	 
	public boolean getM10YDLMJ1125(JDTORecord model) {
	    JDTORecord tcRecord1 = JDTORecordFactory.getInstance().create();
	    
	    logger.println(LogLevel.DEBUG,this, "내부IF호출 시작 === 임가공이송상하차완료(M10YDLMJ1125) ===");
	    
	    JDTORecord jRtn = JDTORecordFactory.getInstance().create();
	    
	    try{
//		    	UPCARUNLOAD_GP		상하차 구분
//		    	CARD_NO				카드 번호
//		    	CAR_NO				차량 번호
//		    	YD_GP				야드구분
//		    	CARLOAD_DONE_DATE	상차 완료 일자
//		    	CARLOAD_DONE_TIME	상차 완료 시각
//		    	TRANS_WORD_DATE		이송지시일자
//		    	TRANS_WORD_SEQNO	이송지시순번

			String sYD_GP = StringHelper.evl(model.getFieldString("YD_GP"), "");
			
			String sYARD_GP = "";
			if("5".equals(sYD_GP)) {
				sYARD_GP = "DX3B";
			} else if("7".equals(sYD_GP)) {
				sYARD_GP = "DH1G";
			} else if("N".equals(sYD_GP)) {
				sYARD_GP = "DH1J";
			} else if("Q".equals(sYD_GP)) {
				sYARD_GP = "DX3A";
			}		    	
		    	
		    tcRecord1.setField("MQ_TC_CD", "M10YDLMJ1125");
		    tcRecord1.setField("MQ_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
		    tcRecord1.setField("TRN_REQ_DATE", StringHelper.evl(model.getFieldString("TRANS_WORD_DATE"), ""));
		    tcRecord1.setField("TRN_REQ_SEQ", StringHelper.evl(model.getFieldString("TRANS_WORD_SEQNO"), ""));
		    tcRecord1.setField("CAR_NO", StringHelper.evl(model.getFieldString("CAR_NO"), ""));
		    tcRecord1.setField("YD_GP", sYD_GP);
			tcRecord1.setField("DIST_GOODS_GP", "H");		
			tcRecord1.setField("YARD_GP", sYARD_GP);				    
		    tcRecord1.setField("CARUD_CMPL_DATE", YmCommonUtil.getCurDate("yyyyMMdd"));
		    tcRecord1.setField("CARUD_CMPL_TIME", YmCommonUtil.getCurDate("HHmmss"));	

		    jRtn = ymCommUtils.addSndData(tcRecord1, tcRecord1);
		    
		    EJBConnector ydEjbCon = new EJBConnector("default", this);
		    ydEjbCon.trx("YmCommEJB", "sndInterface", jRtn);
		    
			logger.println(LogLevel.DEBUG,this, "내부IF호출 종료 === 임가공이송상하차완료(M10YDLMJ1125) ===");
		}
		catch(Exception e){
			logger.println(LogLevel.DEBUG,this, "내부IF호출 ERROR === 임가공이송상하차완료(M10YDLMJ1125) ===");
			logger.println(LogLevel.DEBUG,this, e.getMessage());
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
//			GOODS_NO		제품 번호
//			BEFO_STORE_LOC	FROM 저장위치
//			TO_STORE_LOC	TO 저장위치
//			MOVENSTACK_DATE	이적 일자
//			MOVENSTACK_TIME	이적 시각

		tcRecord1.setField("JMS_TC_CD", "YDDMR004");
		tcRecord1.setField("TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
		tcRecord1.setField("GOODS_NO", StringHelper.evl(model.getFieldString("GOODS_NO"), "").trim());
		tcRecord1.setField("BEFO_STORE_LOC", StringHelper.evl(model.getFieldString("BEFO_STORE_LOC"), ""));
		tcRecord1.setField("TO_STORE_LOC", StringHelper.evl(model.getFieldString("TO_STORE_LOC"), ""));
		tcRecord1.setField("MOVENSTACK_DATE", YmCommonUtil.getCurDate("yyyyMMdd"));
		tcRecord1.setField("MOVENSTACK_TIME", YmCommonUtil.getCurDate("HHmmss"));
		
		//내부인터페이스 송신모듈 호출 
		this.sendInternalModel(tcRecord1);
		}
		catch(Exception e){
	    }
        return true;
	 }
	
	/**
	 * 임가공 PIDEV
	 * 오퍼레이션명 : 코일제품이적작업실적
	 * YDDMR004
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean getM10YDLMJ1031(JDTORecord model) {
		JDTORecord tcRecord1 = JDTORecordFactory.getInstance().create();
		
		logger.println(LogLevel.DEBUG,this, "내부IF호출 시작 === 임가공코일제품이적작업실적(M10YDLMJ1031) ===");		
		
		JDTORecord jRtn = JDTORecordFactory.getInstance().create();
		
		try{
//			GOODS_NO		제품 번호
//			STORE_LOC_CD_FROM	FROM 저장위치
//			STORE_LOC_CD_TO	TO 저장위치
//			MOVENSTACK_DATE	이적 일자
//			MOVENSTACK_TIME	이적 시각

			String sYD_GP = StringHelper.evl(model.getFieldString("YD_GP"), "");
			
			String sYARD_GP = "";
			if("5".equals(sYD_GP)) {
				sYARD_GP = "DX3B";
			} else if("7".equals(sYD_GP)) {
				sYARD_GP = "DH1G";
			} else if("N".equals(sYD_GP)) {
				sYARD_GP = "DH1J";
			} else if("Q".equals(sYD_GP)) {
				sYARD_GP = "DX3A";
			}			
			
			tcRecord1.setField("MQ_TC_CD", "M10YDLMJ1031");
			tcRecord1.setField("MQ_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
			tcRecord1.setField("GOODS_NO", StringHelper.evl(model.getFieldString("GOODS_NO"), "YD_GP").trim() );
			tcRecord1.setField("YD_GP", sYD_GP);
			tcRecord1.setField("DIST_GOODS_GP", "H");
			tcRecord1.setField("YARD_GP", sYARD_GP);		
			tcRecord1.setField("GOODS_NO", StringHelper.evl(model.getFieldString("GOODS_NO"), "").trim());
			tcRecord1.setField("STORE_LOC_CD_FROM", StringHelper.evl(model.getFieldString("BEFO_STORE_LOC"), ""));
			tcRecord1.setField("STORE_LOC_CD_TO", StringHelper.evl(model.getFieldString("TO_STORE_LOC"), ""));
			tcRecord1.setField("MOVENSTACK_DATE", YmCommonUtil.getCurDate("yyyyMMdd"));
			tcRecord1.setField("MOVENSTACK_TIME", YmCommonUtil.getCurDate("HHmmss"));
			
			jRtn = ymCommUtils.addSndData(tcRecord1, tcRecord1);
			
			EJBConnector ydEjbCon = new EJBConnector("default", this);
			ydEjbCon.trx("YmCommEJB", "sndInterface", jRtn);			
			
			logger.println(LogLevel.DEBUG,this, "내부IF호출 종료 === 임가공코일제품이적작업실적(M10YDLMJ1031) ===");
		}
		catch(Exception e){
			logger.println(LogLevel.DEBUG,this, "내부IF호출 ERROR === 임가공코일제품이적작업실적(M10YDLMJ1031) ===");
			logger.println(LogLevel.DEBUG,this, e.getMessage());
	    }
		
        return true;
	 }	
	
}




