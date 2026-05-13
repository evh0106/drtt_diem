package com.inisteel.cim.ym.facade.internal.session;

import java.util.List;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.log.LogService;
import jspeed.base.log.LogServiceConfig;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.common.jms.model.CommonModel;

import com.inisteel.cim.common.jms.model.ym.DMYM007;
import com.inisteel.cim.common.jms.model.ym.POYM001;
import com.inisteel.cim.common.jms.model.ym.POYM005;
import com.inisteel.cim.common.jms.model.ym.PMYM001;
import com.inisteel.cim.common.jms.model.ym.PMYM002;
import com.inisteel.cim.common.jms.model.ym.PCYM001;
import com.inisteel.cim.common.jms.model.ym.PCYM002;

import com.inisteel.cim.common.jms.model.ym.*;
import com.inisteel.cim.ym.common.ModelWarning;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
import com.inisteel.cim.ym.ilkwan.dao.YdStockDao;

/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="InternalEJB" jndi-name="JNDIInternal" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class InternalSBean extends BaseSessionBean {
    private Logger logger 			= null;
    private ymCommonDAO ymCommonDAO = null;
    
	public void ejbCreate() {
        LogServiceConfig config = 
            LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
        logger 		= new Logger(config);
        ymCommonDAO = new ymCommonDAO();
	}	
	//////////////////////////////////////////////////////////////////////////////
	/////////////////////일관제철 시작///////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/**
	 * 오퍼레이션명 : 일관제철 내부인터페이스 
	 *
		공정계획(수신 2종)	
			PMYDJ001	슬라브충당실적                 저장품테이블 업데이트 <저장품 이동조건>  0
			PMYDJ002	슬라브이송지시                 저장품테이블 업데이트 <저장품 이동조건>  0
		
		진행관리(수신 3종) 
			PTYDJ001		코일충당실적              저장품테이블 업데이트 <저장품 이동조건>  0
			PTYDJ002		코일소재이송지시           저장품테이블 업데이트 <저장품 이동조건>  0
			PTYDJ003		코일소재임가공이송지시       저장품테이블 업데이트 <저장품 이동조건> 0 (임가공사코드 추가해야함)
		
		생산통제(수신 4종)
			CTYDJ013	외판행선변경확정               보류
			CTYDJ032	B열연압연지시확정 저장품테이블       <장입LOT번호 업데이트> ---------------------- ???
		
		연주조업(수신 1종)
			CSYDJ001	연주전단실적 -- 수신된 재료번호로 기존 예정번호 업데이트  0
		
		구내운송(수신 3종)
			TSYDJ002		소재차량도착Point 요구   
			TSYDJ003		소재차량도착
			TSYDJ004		소재차량출발
		
		출하관리(수신 22종)
			DMYDR002	코일제품보류확정                  이동조건 업데이트  0---------
			DMYDR004	외판슬라브출하지시대기             이동조건 업데이트  0--------
			DMYDR005	코일제품출하지시대기               이동조건 업데이트 0-------
			DMYDR008	코일제품반납대기                   이동조건 업데이트 0-------
			DMYDR011	코일제품고간이송지시               이동조건업데이트  0 , 운송순번 업데이트 0
			DMYDR013	외판슬라브목전(주문자변경)         이동조건업데이트 0------
			DMYDR014	코일제품목전                     이동조건업데이트  0------
			DMYDR016	외판슬라브운송지시대기                이동조건, 운송순번 업데이트 0
			DMYDR020	코일제품운송지시                 이동조건, 운송순번 업데이트 0
			DMYDR022	외판슬라브운송상차지시             이동조건,운송번호 ,운송순번 ,카드번호 업데이트 0
			DMYDR023	코일제품상차지시                 이동조건,카드번호 업데이트 0
			DMYDR025	임가공이송상차지시                  이동조건,운송번호 ,운송순번 ,카드번호 업데이트 0
			DMYDR026	외판슬라브보관지시               보관매출구분 Y 업데이트 0
			DMYDR027	코일제품보관지시                보관매출구분 Y 업데이트 0
			DMYDR029	외판슬라브출하완료                이동조건업데이트, 카드번호 ''로 업데이트 0
			DMYDR030	코일제품출하완료                 이동조건업데이트, 카드번호 ''로 업데이트 0
			DMYDR032	외판슬라브반품                  이동조건업데이트 0-------
			DMYDR033	코일제품반품                   이동조건업데이트 0-------
			
			DMYDR035	외판슬라브출하차량도착실적--O ( 운송lot 편성 -> 크레인 스케줄 편성)
			DMYDR036	코일제품출하차량도착실적--O ( 크레인 스케줄 편성)
			DMYDR037	코일임가공차량도착실적--X 보류
			DMYDR039	외판슬라브출하차량출발실적--X 보류
			DMYDR040	코일제품출하차량출발실적--X 보류
			DMYDR041	코일임가공차량출발실적--X 보류
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean receiveInternal(JDTORecord cModel) {
		
		String sJmsTcCd = StringHelper.evl(cModel.getFieldString("JMS_TC_CD"), StringHelper.evl(cModel.getFieldString("TC_CODE"),""));
		
		logger.println(LogLevel.INFO, this,"==>>일관제철 내부수신 전문["+sJmsTcCd+"]" );
		
		try {	        
			if(YmCommonConst.MODEL_PMYDJ001.equals(sJmsTcCd)) {		
				return getPMYDJ001(cModel);
			}else if(YmCommonConst.MODEL_PMYDJ002.equals(sJmsTcCd)) {	
				return getPMYDJ002(cModel);
			}else if(YmCommonConst.MODEL_PTYDJ001.equals(sJmsTcCd)) {	
				return getPTYDJ001(cModel);
			}else if(YmCommonConst.MODEL_PTYDJ002.equals(sJmsTcCd)) {	
				return getPTYDJ002(cModel);
			}else if(YmCommonConst.MODEL_PTYDJ003.equals(sJmsTcCd)) {	
				return getPTYDJ003(cModel);
			}else if(YmCommonConst.MODEL_CTYDJ013.equals(sJmsTcCd)) {	
				return getCTYDJ013(cModel);
			}else if(YmCommonConst.MODEL_CTYDJ032.equals(sJmsTcCd)) {	
				return getCTYDJ032(cModel);
			}else if(YmCommonConst.MODEL_CSYDJ001.equals(sJmsTcCd)) {	
				return getCSYDJ001(cModel);
			}else if(YmCommonConst.MODEL_TSYDJ002.equals(sJmsTcCd)) {	
				return getTSYDJ002(cModel);
			}else if(YmCommonConst.MODEL_TSYDJ003.equals(sJmsTcCd)) {	
				return getTSYDJ003(cModel);
			}else if(YmCommonConst.MODEL_TSYDJ004.equals(sJmsTcCd)) {	
				return getTSYDJ004(cModel);
			}else if(YmCommonConst.MODEL_TSYDJ014.equals(sJmsTcCd)) {	
				return getTSYDJ014(cModel);
			}else if("YDYDJ651".equals(sJmsTcCd)) {	// 임시.
				return getTSYDJ004(cModel);	
			}else if(YmCommonConst.MODEL_DMYDR002.equals(sJmsTcCd)) {	
				return getDMYDR002(cModel);
			}else if(YmCommonConst.MODEL_DMYDR004.equals(sJmsTcCd)) {	
				return getDMYDR004(cModel);
			}else if(YmCommonConst.MODEL_DMYDR005.equals(sJmsTcCd)) {	
				return getDMYDR005(cModel);
			}else if(YmCommonConst.MODEL_DMYDR008.equals(sJmsTcCd)) {	
				return getDMYDR008(cModel);
			}else if(YmCommonConst.MODEL_DMYDR011.equals(sJmsTcCd)) {	
				return getDMYDR011(cModel);
			}else if(YmCommonConst.MODEL_DMYDR013.equals(sJmsTcCd)) {	
				return getDMYDR013(cModel);
			}else if(YmCommonConst.MODEL_DMYDR014.equals(sJmsTcCd)) {	
				return getDMYDR014(cModel);
			}else if(YmCommonConst.MODEL_DMYDR016.equals(sJmsTcCd)) {	
				return getDMYDR016(cModel);
			}else if(YmCommonConst.MODEL_DMYDR020.equals(sJmsTcCd)) {	
				return getDMYDR020(cModel);
			}else if(YmCommonConst.MODEL_DMYDR022.equals(sJmsTcCd)) {	
				boolean	Return =  getDMYDR022(cModel);
				return getDMYM002(cModel);  //작업예약
			}else if(YmCommonConst.MODEL_DMYDR023.equals(sJmsTcCd)) {	
				boolean	Return = getDMYDR023(cModel);
				 if(Return){
				 return getDMYM002(cModel);  //작업예약
				 }
			}else if(YmCommonConst.MODEL_DMYDR025.equals(sJmsTcCd)) {	
				boolean	Return = getDMYDR025(cModel);
				 return getDMYM004(cModel);  //작업예약(임가공사)
			}else if(YmCommonConst.MODEL_DMYDR026.equals(sJmsTcCd)) {	
				return getDMYDR026(cModel);
			}else if(YmCommonConst.MODEL_DMYDR027.equals(sJmsTcCd)) {	
				return getDMYDR027(cModel);
			}else if(YmCommonConst.MODEL_DMYDR029.equals(sJmsTcCd)) {	
				return getDMYDR029(cModel);
			}else if(YmCommonConst.MODEL_DMYDR030.equals(sJmsTcCd)) {	
				return getDMYDR030(cModel);
			}else if(YmCommonConst.MODEL_DMYDR032.equals(sJmsTcCd)) {	
				return getDMYDR032(cModel);
			}else if(YmCommonConst.MODEL_DMYDR033.equals(sJmsTcCd)) {	
				return getDMYDR033(cModel);
			}else if(YmCommonConst.MODEL_DMYDR035.equals(sJmsTcCd)) {	
				return getDMYDR035(cModel);
			}else if(YmCommonConst.MODEL_DMYDR036.equals(sJmsTcCd)) {	
				return getDMYDR036(cModel);
			}else if(YmCommonConst.MODEL_DMYDR037.equals(sJmsTcCd)) {	
				return getDMYDR037(cModel);
			}else if(YmCommonConst.MODEL_DMYDR039.equals(sJmsTcCd)) {	
				return getDMYDR039(cModel);
			}else if(YmCommonConst.MODEL_DMYDR040.equals(sJmsTcCd)) {	
				return getDMYDR040(cModel);  //크레인 스케줄 기동
			}else if(YmCommonConst.MODEL_DMYDR041.equals(sJmsTcCd)) {	
				return getDMYDR041(cModel);
			}else if(YmCommonConst.MODEL_DMYDR070.equals(sJmsTcCd)) {	
				return getDMYDR070(cModel);
			}else if(YmCommonConst.MODEL_DMYDR071.equals(sJmsTcCd)) {	
				return getDMYDR071(cModel);
			}else if(YmCommonConst.MODEL_DMYDR072.equals(sJmsTcCd)) {	
				return getDMYDR072(cModel);
			}else if(YmCommonConst.MODEL_DMYDR073.equals(sJmsTcCd)) {	
				return getDMYDR073(cModel);
			}else if(YmCommonConst.MODEL_DMYDR074.equals(sJmsTcCd)) {	
				return getDMYDR074(cModel);
			}else if(YmCommonConst.MODEL_DMYDR075.equals(sJmsTcCd)) {	
				return getDMYDR075(cModel);
			}
			
		}catch(Exception e) {
			//throw new EJBServiceException(e);
			logger.println(LogLevel.INFO, this,"==일관제철 내부수신 전문["+sJmsTcCd+"] EXCEPTION 발생 " );
			
		}
		return true;
	}
	/*
	 	JNDISlabReg/JNDICoilReg/JNDITsInfoReg/JNDIDmInfoReg
	 */
	private boolean getPMYDJ001(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDISlabReg", this);
		return ((Boolean)ejbCon.trx("procSlabMatchWr", //0
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getPMYDJ002(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDISlabReg", this);
		return ((Boolean)ejbCon.trx("procSlavFtmvOrd",  // 필요??
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getPTYDJ001(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDICoilReg", this);
		return ((Boolean)ejbCon.trx("procCoilMatchWr",  //0
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getPTYDJ002(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDICoilReg", this);
		return ((Boolean)ejbCon.trx("procCoilMatlFtmvOrd", //필요?
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getPTYDJ003(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDICoilReg", this);
		return ((Boolean)ejbCon.trx("procCoilMatlRentprocFtmvOrd", //필요?
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getCTYDJ013(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDISlabReg", this);
		return ((Boolean)ejbCon.trx("procOutplRtChng",  //0
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getCTYDJ032(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDISlabReg", this);
		return ((Boolean)ejbCon.trx("procBHrMillOrdCmmt", //0
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getCSYDJ001(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDISlabReg", this);
		return ((Boolean)ejbCon.trx("procCcFsWr", //0
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getTSYDJ002(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDITsInfoReg", this);
		return ((Boolean)ejbCon.trx("procMatlCarArrPntRequest", 
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getTSYDJ003(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDITsInfoReg", this);
		return ((Boolean)ejbCon.trx("procMatlCarArr", 
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getTSYDJ004(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDITsInfoReg", this);
		return ((Boolean)ejbCon.trx("procMatlCarLev", 
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getTSYDJ014(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDITsInfoReg", this);
		return ((Boolean)ejbCon.trx("procStartdeleteRev", 
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getDMYDR002(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDICoilReg", this);
		return ((Boolean)ejbCon.trx("procCoilGdsHoldCommt", //필요?
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getDMYDR004(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDISlabReg", this);
		return ((Boolean)ejbCon.trx("procOutplSlabDistOrdWait", //0
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getDMYDR005(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDICoilReg", this);
		return ((Boolean)ejbCon.trx("procCoilGdsDistOrdWait", //0
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getDMYDR008(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDICoilReg", this);
		return ((Boolean)ejbCon.trx("procCoilGdsRetnWait",//0 
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getDMYDR011(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDICoilReg", this);
		return ((Boolean)ejbCon.trx("procCoilGdsWhFtmvOrd", 
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getDMYDR013(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDISlabReg", this);
		return ((Boolean)ejbCon.trx("procOutplSlabOrdtrn", //0
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getDMYDR014(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDICoilReg", this);
		return ((Boolean)ejbCon.trx("procCoilGdsOrdtrn", //0
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getDMYDR016(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDISlabReg", this);
		return ((Boolean)ejbCon.trx("procOutplSlabTrnOrdWait", //0
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getDMYDR020(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDICoilReg", this);
		return ((Boolean)ejbCon.trx("procCoilGdsTrnOrd", 
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getDMYDR022(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDISlabReg", this);
		return ((Boolean)ejbCon.trx("procSlabGdsCarLdOrd", //0 
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getDMYDR023(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDICoilReg", this);
		return ((Boolean)ejbCon.trx("procCoilGdsCarLdOrd", //0 
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	//일관제철용 : 작업예약편성
	private boolean getDMYM002(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDIWorkOrderInfoReg", this);
		return ((Boolean)ejbCon.trx("hsCyGoodsChulHaSangChaGisiRegistInfo", 
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	//일관제철용 : 작업예약편성(임가공사)
	private boolean getDMYM004(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDIWorkOrderInfoReg", this);
		return ((Boolean)ejbCon.trx("cyGoodsISongSangChaGisiRegistInfo", 
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getDMYDR025(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDICoilReg", this);
		return ((Boolean)ejbCon.trx("procCoilMatlRentGdsCarLdOrd", //0 
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getDMYDR026(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDISlabReg", this);
		return ((Boolean)ejbCon.trx("procOutplSlabKeepOrd", //0
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getDMYDR027(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDICoilReg", this);
		return ((Boolean)ejbCon.trx("procCoilGdsKeepOrd", //0
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getDMYDR029(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDISlabReg", this);
		return ((Boolean)ejbCon.trx("procOutplSlabDistCmpl", //0
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getDMYDR030(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDICoilReg", this);
		return ((Boolean)ejbCon.trx("procCoilGdsDistCmpl", //0
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getDMYDR032(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDISlabReg", this);
		return ((Boolean)ejbCon.trx("procOutplSlabRetngds", //0
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getDMYDR033(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDICoilReg", this);
		return ((Boolean)ejbCon.trx("procCoilGdsRetngds", //0
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getDMYDR035(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDICTSStatusReg", this);
		return ((Boolean)ejbCon.trx("hscarArrival", 
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getDMYDR036(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDICTSStatusReg", this);
		return ((Boolean)ejbCon.trx("hscarArrival", 
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getDMYDR037(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "xxxxxxxxxxxx", this);
		return ((Boolean)ejbCon.trx("xxxxxxxxxxxxxx", 
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getDMYDR039(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "xxxxxxxxxxxx", this);
		return ((Boolean)ejbCon.trx("xxxxxxxxxxxxxx", 
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getDMYDR040(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDICTSStatusReg", this);
		return ((Boolean)ejbCon.trx("hscarStartOrder", 
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getDMYDR041(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDICTSStatusReg", this);
		return ((Boolean)ejbCon.trx("carStartOrder", 
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getDMYDR070(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDICoilReg", this);
		return ((Boolean)ejbCon.trx("procCoilGdsTrnOrdLdPDAAB", 
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getDMYDR071(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDICoilReg", this);
		return ((Boolean)ejbCon.trx("procStandByYdArriveLdPDAAB", //0 
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getDMYDR072(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDICoilReg", this);
		return ((Boolean)ejbCon.trx("procCoilGdsDistCmplLdPDAAB", //0
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getDMYDR073(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDICoilReg", this);
		return ((Boolean)ejbCon.trx("procCoilGdsTrnOrdUdPDAAB", 
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getDMYDR074(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDICoilReg", this);
		return ((Boolean)ejbCon.trx("procStandByYdArriveUdPDAAB", //0 
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	private boolean getDMYDR075(JDTORecord model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDICoilReg", this);
		return ((Boolean)ejbCon.trx("procCoilGdsDistCmplLdPDAAB", //0
			  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 적치대  데이타를 insert한다.
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean receiveInternal(CommonModel cModel) {
        logger.println(LogLevel.INFO, this, cModel.toString());
	    try {	        

	    	if(YmCommonConst.MODEL_POYM005.equals(cModel.getTcCode())) {		//POYM005(전단실적) A열연 SLAB야드 전단실적 추가(2007-03-02)MCH  
	            return getPOYM005((POYM005)cModel);
	        }else if(YmCommonConst.MODEL_POYM001.equals(cModel.getTcCode())) {	//POYM001
	            return getPOYM001((POYM001)cModel);
	        }else if(YmCommonConst.MODEL_POYM003.equals(cModel.getTcCode())) {	//POYM003 
	            return getPOYM003((POYM003)cModel);
	        }else if(YmCommonConst.MODEL_POYM004.equals(cModel.getTcCode())) {	//POYM004  
	            return getPOYM004((POYM004)cModel);
	        }else if(YmCommonConst.MODEL_POYM006.equals(cModel.getTcCode())) {	//POYM006  
	            return getPOYM006((POYM006)cModel);
	        }else if(YmCommonConst.MODEL_POYM002.equals(cModel.getTcCode())) {	//POYM002  
	            return getPOYM002((POYM002)cModel);
	        }else if(YmCommonConst.MODEL_POYM007.equals(cModel.getTcCode())) {	//POYM007  
	            return getPOYM007((POYM007)cModel);	            	            
	        }else if(YmCommonConst.MODEL_POYM008.equals(cModel.getTcCode())) {	//POYM008  
	            return getPOYM008((POYM008)cModel);	            	            	            
	        }else if(YmCommonConst.MODEL_POYM009.equals(cModel.getTcCode())) {	//POYM009  
	            return getPOYM009((POYM009)cModel);      	            
	        }else if(YmCommonConst.MODEL_POYM010.equals(cModel.getTcCode())) {	//POYM010  
	            return getPOYM010((POYM010)cModel);      	            
	        }
	    }catch(DAOException daoe) {
               throw daoe;
           }catch(Exception e) {
	        throw new EJBServiceException("="+cModel.getTcCode()+"전문처리 중 입력값 누락 에러 발생");
        }
		return true;
	}
	
	
	
	/**
	 * 오퍼레이션명 : AB열연 출하전문 취소처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean receiveCancel(JDTORecord inRecord) {
		String[] rVal = new String[2];
		JDTORecord recStockColumn =JDTORecordFactory.getInstance().create();
		String 		sCURR_PROG_CD ="";
		String 		sSTOCK_MOVE_TERM="";
		String 		sSchId	="";
		YdStockDao ydStockDao = new YdStockDao();
		List 	   dmList  = null; 
		int			intRtnVal = 0;
        logger.println(LogLevel.INFO, this, "#############AB열연 출하취소 전문 START###############");
        
		String sJmsTcCd = StringHelper.evl(inRecord.getFieldString("JMS_TC_CD"), StringHelper.evl(inRecord.getFieldString("TC_CODE"),""));		
		logger.println(LogLevel.INFO, this,"==>>일관제철 내부수신 전문["+sJmsTcCd+"]" );
		
	    try {	        
	    	/*
				DMYDR008	코일제품반납대기		1.저장품 이동 조건 변경
				DMYDR013	외판슬라브목전			1.저장품 이동 조건 변경
				DMYDR014	코일제품목전			1.저장품 이동 조건 변경
				DMYDR016	외판슬라브운송지시대기	1.저장품 이동 조건 변경
				DMYDR029	외판슬라브출하완료 		1.저장품 이동 조건 변경
				DMYDR030	코일제품출하완료              1.저장품 이동 조건 변경
				DMYDR011	코일제품고간이송지시	1.운송지시번호 삭제 (운송지시번호로 찾아서 삭제 함),저장품 이동 조건 변경
				DMYDR020	코일제품운송지시              1.운송지시번호 삭제 (운송지시번호로 찾아서 삭제 함) ,저장품 이동 조건 변경
				DMYDR022	외판슬라브운송상차지시 	1.크레인 스케줄취소 ,2 작업예약취소 , 3. 카드번호삭제,저장품 이동 조건 변경
				DMYDR023	코일제품상차지시		1.크레인 스케줄취소 ,2 작업예약취소 , 3. 카드번호삭제,저장품 이동 조건 변경
				DMYDR026	외판슬라브보관지시		KEEPSTOCK_STL_YN= ''
				DMYDR027	코일제품보관지시		KEEPSTOCK_STL_YN= ''

	    	 */
	    	String sSTL_NO ="";
	    	String scardNo ="";
	    	//이송지시 인경우 진도코드가 변경이 안됨 
	    	if(!YmCommonConst.DMYDR011.equals(sJmsTcCd)){
		    	//진도코드: 취소후 전단계의 진도코드
		    	sCURR_PROG_CD 			=StringHelper.evl(YmCommonUtil.paraRecChkNull(inRecord,"CURR_PROG_CD"), "");	    		
		    	String sTRANS_ORD_DT 	=StringHelper.evl(YmCommonUtil.paraRecChkNull(inRecord,"TRANS_ORD_DT"), "");
		    	String sTRANS_ORD_SEQNO =StringHelper.evl(YmCommonUtil.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO"), "");
		    		scardNo 			=StringHelper.evl(YmCommonUtil.paraRecChkNull(inRecord,"CARD_NO"), "");
		    	
		    	sSTL_NO = StringHelper.evl(YmCommonUtil.paraRecChkNull(inRecord,"STL_NO"), "");
		    	if(sSTL_NO ==""){
			    	ymCommonDAO dao = ymCommonDAO.getInstance();			
					String sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getTransStockInfo";				
					dmList = dao.getCommonInfo2(sQueryId,new Object[]{sTRANS_ORD_DT+sTRANS_ORD_SEQNO});
					intRtnVal= dmList.size();
					
					for(int inx = 0; inx < dmList.size() ; inx++){
					JDTORecord jtR = (JDTORecord)dmList.get(inx);
					sSTL_NO = jtR.getFieldString("STL_NO");
					 //scardNo =StringHelper.evl(jtR.getFieldString("CAR_CARD_NO"), "");
					}
		    	} else {
		    		sSTL_NO = inRecord.getFieldString("STL_NO");
		    	}
				
		    	//저장품 이동조건
		    	rVal = YmCommonUtil.getCoilCurrProgCd2(sSTL_NO,sCURR_PROG_CD);
		    	sSTOCK_MOVE_TERM = rVal[1];
		    	
		    	recStockColumn.setField("STOCK_MOVE_TERM", sSTOCK_MOVE_TERM);
	      	}else {
	      		recStockColumn.setField("TC_CODE", sJmsTcCd);	      		
	      	}
	    	
	    	recStockColumn.setField("STOCK_ID", sSTL_NO);
	    	recStockColumn.setField("TRANS_ORD_DT", StringHelper.evl(YmCommonUtil.paraRecChkNull(inRecord,"TRANS_ORD_DT"), ""));
			recStockColumn.setField("TRANS_ORD_SEQNO", StringHelper.evl(YmCommonUtil.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO"), ""));			
			recStockColumn.setField("MODIFIER", sJmsTcCd);
			
			String sYD_GP 		=  YmCommonUtil.paraRecChkNull(inRecord,"YD_GP");
			
			
	    	if(YmCommonConst.DMYDR008.equals(sJmsTcCd)||  //코일제품반납대기
 	    	   YmCommonConst.DMYDR013.equals(sJmsTcCd)||  //외판슬라브목전
	    	   YmCommonConst.DMYDR014.equals(sJmsTcCd)||  //코일제품목전
	    	   YmCommonConst.DMYDR016.equals(sJmsTcCd)||  //외판슬라브운송지시대기
	    	   YmCommonConst.DMYDR029.equals(sJmsTcCd)||  //외판슬라브출하완료
	    	   YmCommonConst.DMYDR030.equals(sJmsTcCd)) { //코일제품출하완료
				//1.저장품 이동 조건 변경************************************************************************
	    		intRtnVal = ydStockDao.updYdStock(recStockColumn, 0);	
				if(intRtnVal >0){
					logger.println(LogLevel.INFO, this,"==>>전문["+sJmsTcCd+"] 취소작업 완료" );

				}else if(intRtnVal == 0){
					logger.println(LogLevel.INFO, this,"==>>전문["+sJmsTcCd+"] 취소작업 실패" );
				 
				}	 
				//*******************************************************************************************	
	        } else 
 
	        if(YmCommonConst.DMYDR011.equals(sJmsTcCd)||  //코일제품고간이송지시
	           YmCommonConst.DMYDR020.equals(sJmsTcCd)||  //코일제품운송지시
	           YmCommonConst.DMYDR070.equals(sJmsTcCd)||  //코일이송상차대기장도착PDA
	           YmCommonConst.DMYDR073.equals(sJmsTcCd)||  //코일이송하차대기장도착PDA
	           YmCommonConst.DMYDR060.equals(sJmsTcCd)||  //코일제품운송상차지시
	           YmCommonConst.DMYDR023.equals(sJmsTcCd)||  //코일제품상차지시
	           YmCommonConst.DMYDR022.equals(sJmsTcCd)) { //외판슬라브운송상차지시 
	        	//1.운송지시번호 삭제 (운송지시번호로 찾아서 삭제 함),저장품 이동 조건 변경	*********************************
	        	//1. 크레인 스케줄취소 ,2. 작업예약취소 , 3. 카드번호삭제,저장품 이동 조건 변경******************************
	        	
	        	//1. 크레인 스케줄취소	        	
//	        	String sYD_GP 		=  YmCommonUtil.paraRecChkNull(inRecord,"YD_GP");
	        	//String sSTL_NO 		=  YmCommonUtil.paraRecChkNull(inRecord,"STL_NO");
//	        	String sSchId 		=  "";
	        	
	        	String message = "";        
	        	
	        	
				//차량포인트 점유 정보 비우기***********************************************************************
	        	String sTRANS_ORD_DT = StringHelper.evl(inRecord.getFieldString("TRANS_ORD_DT"), "");
	        	String sTRANS_ORD_SEQNO = StringHelper.evl(inRecord.getFieldString("TRANS_ORD_SEQNO"), "");	
				
			    ymCommonDAO dao = ymCommonDAO.getInstance();
			    String sQueryId = "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.updYdStackcolCarPint";
			    int count = dao.updateData(sQueryId,new Object[]{sJmsTcCd ,sTRANS_ORD_DT+sTRANS_ORD_SEQNO});		
				//********************************************************************************************
	        	
	        	
		        if(YmCommonConst.DMYDR011.equals(sJmsTcCd)||  //코일제품고간이송지시
	 	    	   YmCommonConst.DMYDR020.equals(sJmsTcCd)||
	 	    	   YmCommonConst.DMYDR070.equals(sJmsTcCd)||  //코일이송상차대기장도착PDA
		           YmCommonConst.DMYDR073.equals(sJmsTcCd)||  //코일이송하차대기장도착PDA
	 	    	   YmCommonConst.DMYDR060.equals(sJmsTcCd)
		           ) { //코일제품운송지시
		 		        //1.운송지시번호 삭제 (운송지시번호로 찾아서 삭제 함),저장품 이동 조건 변경	*********************************
		 	    		intRtnVal = ydStockDao.updYdStock6(recStockColumn);	
		 				if(intRtnVal >0){
		 					logger.println(LogLevel.INFO, this,"==>>전문["+sJmsTcCd+"] 취소작업 완료" );

		 				}else if(intRtnVal == 0){
		 					logger.println(LogLevel.INFO, this,"==>>전문["+sJmsTcCd+"] 취소작업 실패" );
		 				 
		 				}
		 	        	//*******************************************************************************************	
		 	        } else {	    		
			        	//3. 카드번호삭제,저장품 이동 조건 변경
			    		intRtnVal = ydStockDao.updYdStock7(recStockColumn);	
						if(intRtnVal >0){
							logger.println(LogLevel.INFO, this,"==>>전문["+sJmsTcCd+"] 취소작업 완료" );
		
						}else if(intRtnVal == 0){
							logger.println(LogLevel.INFO, this,"==>>전문["+sJmsTcCd+"] 취소작업 실패" );
					 
						}
		 	        	//*******************************************************************************************
		 	        }
				
	        	//*******************************************************************************************
	 	    } else 
	        if(YmCommonConst.DMYDR027.equals(sJmsTcCd)) { //코일제품보관지시
	        	//1. 보관지시구분 KEEPSTOCK_STL_YN= '' *********************************************************
	    		intRtnVal = ydStockDao.updYdStock2(recStockColumn,1);	
				if(intRtnVal >0){
					logger.println(LogLevel.INFO, this,"==>>전문["+sJmsTcCd+"] 취소작업 완료" );

				}else if(intRtnVal == 0){
					logger.println(LogLevel.INFO, this,"==>>전문["+sJmsTcCd+"] 취소작업 실패" );
				 
				}
	        	//*******************************************************************************************
 	        }
	    	
			///////////////////////////////////////////////////////////////////////////////////////////////////
	    	if(!YmCommonConst.DMYDR011.equals(sJmsTcCd)){
        	for(int inx = 0; inx < dmList.size() ; inx++){
				JDTORecord jtR = (JDTORecord)dmList.get(inx);
				sSTL_NO = jtR.getFieldString("STL_NO");
			
				logger.println(LogLevel.DEBUG, this,"스케줄/작업예약 취소작업 START==>>"+ dmList.size());
				
				
	        	//스케줄 번호 가져오기-----------------------------------------------------------------------
	    		JDTORecord jtR2 = ymCommonDAO.getSchSearch(sSTL_NO);
	    		if (jtR2 == null || jtR2.size() == 0) {
	    			logger.println(LogLevel.DEBUG, this, "SCH =>스케줄 정보가 없습니다.");
	    		} else {	    		
		    		sSchId =YmCommonUtil.paraRecChkNull(jtR2,"SCH_ID");
		    		
		    		//스케줄 취소처리
		        	EJBConnector ejbCon = new EJBConnector("default", "JNDICraneSchReg", this);
		        	Boolean isFalse = new Boolean(false);        	
		        	
	        		if(sYD_GP != null) {
	        			if( (sYD_GP.compareTo("1") == 0) || (sYD_GP.compareTo("3") == 0) )
	        		      
	        				isFalse =  (Boolean)ejbCon.trx("cancelCoilSchInfo",new Class[]{ String.class}, new Object[]{ sSchId });
	        		      
	        			if( (sYD_GP.compareTo("2") == 0) || (sYD_GP.compareTo("4") == 0 || sYD_GP.compareTo("0") == 0) )
	        		      
	        				isFalse =  (Boolean)ejbCon.trx("cancelSlabSchInfo",new Class[]{ String.class}, new Object[]{ sSchId });
	        		}
	        		
		        	if(isFalse.booleanValue() == true){
		        		logger.println(LogLevel.DEBUG, this,  "스케줄취소 처리가 완료되었습니다.");
		        	}else{	
		        		logger.println(LogLevel.DEBUG, this, "스케줄취소 처리도중에 에러가 발생하였습니다.");
		        		return false;
		        	}
	    		}
	        	//--------------------------------------------------------------------------------------
	        	
	        	
	        	//2. 작업예약취소--------------------------------------------------------------------------
	    		JDTORecord jtR1 = ymCommonDAO.getWbookSearch(sSTL_NO);
	    		if (jtR1 == null || jtR1.size() == 0) {
	    			logger.println(LogLevel.DEBUG, this, "SCH =>작업예약 정보가 없습니다.");
	    		} else {	        	
	        	
		        	//작업예약취소처리
		        	EJBConnector ejbConn1 = new EJBConnector("default","JNDICraneStatusReg",this);
		        	Boolean resultRes =  (Boolean) ejbConn1.trx("setWorkIdCancle", new Class[] {String.class}, new Object[] {sSTL_NO});	
		        	if(resultRes.booleanValue() == true){
		        		logger.println(LogLevel.DEBUG, this,  "작업예약취소  처리가 완료되었습니다.");
		        	}else{	
		        		logger.println(LogLevel.DEBUG, this, "작업예약취소 처리도중에 에러가 발생하였습니다.");
		        		return false;
		        	}
	    		}
    		   //------------------------------------------------------------------------------------------
        	} //for end
        	
        	
        		//차량스케줄 종료처리------------------------------------------------------------------------
	        	if(!"".equals(scardNo)){
		        	 
			        logger.println(LogLevel.DEBUG, this, "receiveCancel():차량스케줄 종료처리");
			        
			        //차량이송재로 종료처리
			        ymCommonDAO.modifyCardNoOfDetailEND(scardNo);
			        //차량스케줄 종료 처리
			        ymCommonDAO.modifyCardNoOfEND(scardNo);
			        
			      //차량예약 포인트 지우기
			        ymCommonDAO.modifyCardNoOfStackCol(scardNo);
			        
			        
			        //차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
			        EJBConnector ejbConn2 = new EJBConnector("default","JNDITsInfoReg",this);
					ejbConn2.trx("CarPointinforeg", new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class},
				  	             new Object[]{"A","",scardNo,"","","","C"});
	        	}
	        	//------------------------------------------------------------------------------------------
	        	
    		///////////////////////////////////////////////////////////////////////////////////////////////////////
	    	}
        	
	        logger.println(LogLevel.INFO, this, "############# AB열연 출하취소 전문 END ###############");
	    }catch(DAOException daoe) {
               throw daoe;
           }catch(Exception e) {
	        throw new EJBServiceException(e);
        }
		return true;
	}
	
	
	//////////////////////////////////////////////////////////////////////////////
	/////////////////////일관제철 종료///////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 적치대  데이타를 insert한다.
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean receiveInternal_AB(CommonModel cModel) {
        logger.println(LogLevel.INFO, this, cModel.toString());
	    try {	        
		    
	    	if(YmCommonConst.MODEL_POYM005.equals(cModel.getTcCode())) {		//POYM005(전단실적) A열연 SLAB야드 전단실적 추가(2007-03-02)MCH  
	            return getPOYM005((POYM005)cModel);
	        }else if(YmCommonConst.MODEL_PMYM001.equals(cModel.getTcCode())) {	//PMYM001
	            return getPMYM001((PMYM001)cModel);
	        }else if(YmCommonConst.MODEL_PMYM002.equals(cModel.getTcCode())) {	//PMYM002
	            return getPMYM002((PMYM002)cModel);
	        }else if(YmCommonConst.MODEL_PCYM002.equals(cModel.getTcCode())) {	//PCYM002
	            return getPCYM002((PCYM002)cModel);
	        }else if(YmCommonConst.MODEL_PCYM001.equals(cModel.getTcCode())) {	//PCYM001
	            return getPCYM001((PCYM001)cModel);
	        }else if(YmCommonConst.MODEL_PCYM003.equals(cModel.getTcCode())) {	//PCYM003
	            return getPCYM003((PCYM003)cModel);	            
	        }else if(YmCommonConst.MODEL_POYM001.equals(cModel.getTcCode())) {	//POYM001
	            return getPOYM001((POYM001)cModel);
	        }else if(YmCommonConst.MODEL_DMYM007.equals(cModel.getTcCode())) {	//DMYM007
	            return getDMYM007((DMYM007)cModel);
	        }else if(YmCommonConst.MODEL_DMYM001.equals(cModel.getTcCode())) {	//DMYM001
	            return getDMYM001((DMYM001)cModel);
	        }else if(YmCommonConst.MODEL_DMYM002.equals(cModel.getTcCode())) {	//DMYM002
	            return getDMYM002((DMYM002)cModel);
	        }else if(YmCommonConst.MODEL_DMYM003.equals(cModel.getTcCode())) {	//DMYM003
	            return getDMYM003((DMYM003)cModel);
	        }else if(YmCommonConst.MODEL_DMYM004.equals(cModel.getTcCode())) {	//DMYM004
	            return getDMYM004((DMYM004)cModel);
	        }else if(YmCommonConst.MODEL_PMYM004.equals(cModel.getTcCode())) {	//PMYM004
	            return getPMYM004((PMYM004)cModel);
	        }else if(YmCommonConst.MODEL_POYM003.equals(cModel.getTcCode())) {	//POYM003 
	            return getPOYM003((POYM003)cModel);
	        }else if(YmCommonConst.MODEL_PMYM008.equals(cModel.getTcCode())) {	//PMYM008
	            return getPMYM008((PMYM008)cModel);
	        }else if(YmCommonConst.MODEL_PMYM006.equals(cModel.getTcCode())) {	//PMYM006
	            return getPMYM006((PMYM006)cModel);
	        }else if(YmCommonConst.MODEL_PMYM007.equals(cModel.getTcCode())) {	//PMYM007  
	            return getPMYM007((PMYM007)cModel);
	        }else if(YmCommonConst.MODEL_POYM004.equals(cModel.getTcCode())) {	//POYM004  
	            return getPOYM004((POYM004)cModel);
	        }else if(YmCommonConst.MODEL_QMYM001.equals(cModel.getTcCode())) {	//QMYM001  
	            return getQMYM001((QMYM001)cModel);
	        }else if(YmCommonConst.MODEL_DMYM008.equals(cModel.getTcCode())) {	//DMYM008  
	            return getDMYM008((DMYM008)cModel);
	        }else if(YmCommonConst.MODEL_POYM006.equals(cModel.getTcCode())) {	//POYM006  
	            return getPOYM006((POYM006)cModel);
	        }else if(YmCommonConst.MODEL_PMYM003.equals(cModel.getTcCode())) {	//PMYM003  
	            return getPMYM003((PMYM003)cModel);
	        }else if(YmCommonConst.MODEL_POYM002.equals(cModel.getTcCode())) {	//POYM002  
	            return getPOYM002((POYM002)cModel);
	        }else if(YmCommonConst.MODEL_PMYM005.equals(cModel.getTcCode())) {	//PMYM005  
	            return getPMYM005((PMYM005)cModel);
	        }else if(YmCommonConst.MODEL_DMYM005.equals(cModel.getTcCode())) {	//DMYM005  
	            return getDMYM005((DMYM005)cModel);
	        }else if(YmCommonConst.MODEL_DMYM006.equals(cModel.getTcCode())) {	//DMYM006  
	            return getDMYM006((DMYM006)cModel);
	        }else if(YmCommonConst.MODEL_QMYM002.equals(cModel.getTcCode())) {	//QMYM002  
	            return getQMYM002((QMYM002)cModel);	            
	        }else if(YmCommonConst.MODEL_QMYM003.equals(cModel.getTcCode())) {	//QMYM003  
	            return getQMYM003((QMYM003)cModel);	            
	        }else if(YmCommonConst.MODEL_DMYM009.equals(cModel.getTcCode())) {	//DMYM009  
	            return getDMYM009((DMYM009)cModel);
	        }else if(YmCommonConst.MODEL_DMYM010.equals(cModel.getTcCode())) {	//DMYM010  
	            return getDMYM010((DMYM010)cModel);
	        }else if(YmCommonConst.MODEL_POYM007.equals(cModel.getTcCode())) {	//POYM007  
	            return getPOYM007((POYM007)cModel);	            	            
	        }else if(YmCommonConst.MODEL_POYM008.equals(cModel.getTcCode())) {	//POYM008  
	            return getPOYM008((POYM008)cModel);	            	            	            
	        }else if(YmCommonConst.MODEL_POYM009.equals(cModel.getTcCode())) {	//POYM009  
	            return getPOYM009((POYM009)cModel);      	            
	        }else if(YmCommonConst.MODEL_PSYM001.equals(cModel.getTcCode())) {	//PSYM001  
	            return getPSYM001((PSYM001)cModel);	            	            	            
	        }else if(YmCommonConst.MODEL_PSYM002.equals(cModel.getTcCode())) {	//PSYM002  
	            return getPSYM002((PSYM002)cModel);	            	            	            
	        }else if(YmCommonConst.MODEL_POYM010.equals(cModel.getTcCode())) {	//POYM010  최규성  
	            return getPOYM010((POYM010)cModel);      	            
	        }
	    }catch(DAOException daoe) {
	        throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
		return true;
	}

	
	/**
	 * 내부인터페이스 에러 로그를 처리한다.
	 * @param err
	 * @return
	 */
	private String getErrMsg(Exception err) {
        String[] errMsg = err.getStackTrace()[0].toString().replace('.', ',').split(",");
	    String bean 	= errMsg[errMsg.length - 2];
	    String method 	= errMsg[errMsg.length - 1].replaceAll("Unknown Source", "");
	    return bean +"."+ method +"<br>"+ "EJBServiceException: "+ err.getMessage();	        
	}
	
    private boolean getPMYM003(PMYM003 model) throws Exception {
        EJBConnector ejbCon = new EJBConnector("default", "JNDISlabInfoReg", this);
        return considerWarn(((Boolean)ejbCon.trx("slabConditionModify", 
                new Class[]{ PMYM003.class }, new Object[]{ model })).booleanValue(), model);
    }

    private boolean getPOYM006(POYM006 model) throws Exception {
        EJBConnector ejbCon = new EJBConnector("default", "JNDISlabInfoReg", this);
        return considerWarn(((Boolean)ejbCon.trx("slabMissingResult", 
                new Class[]{ POYM006.class }, new Object[]{ model })).booleanValue(), model);
    }

    private boolean getDMYM008(DMYM008 model) throws Exception {
        EJBConnector ejbCon = new EJBConnector("default", "JNDIWorkOrderInfoReg", this);
        return considerWarn(((Boolean)ejbCon.trx("moveLoadOrderOrCancel", 
                new Class[]{ DMYM008.class }, new Object[]{ model })).booleanValue(), model);
    }

    private boolean getDMYM001(DMYM001 model) throws Exception {
		EJBConnector ejbCon = new EJBConnector("default", "JNDIWorkOrderInfoReg", this);
        return ((Boolean)ejbCon.trx("cyGoodsUnSongGisiRegistInfo", 
                new Class[]{ DMYM001.class }, new Object[]{ model })).booleanValue();
    }  
	
	private boolean getDMYM002(DMYM002 model) throws Exception {
        EJBConnector ejbCon = new EJBConnector("default", "JNDIWorkOrderInfoReg", this);
        return ((Boolean)ejbCon.trx("cyGoodsChulHaSangChaGisiRegistInfo", 
                new Class[]{ DMYM002.class }, new Object[]{ model })).booleanValue();
    }  	

	private boolean getDMYM003(DMYM003 model) throws Exception {
        EJBConnector ejbCon = new EJBConnector("default", "JNDIWorkOrderInfoReg", this);
        return ((Boolean)ejbCon.trx("cyGoodsISongGisiRegistInfo", 
                new Class[]{ DMYM003.class }, new Object[]{ model })).booleanValue();
    }  
	
	private boolean getDMYM004(DMYM004 model) throws Exception {
        EJBConnector ejbCon = new EJBConnector("default", "JNDIWorkOrderInfoReg", this);
        return ((Boolean)ejbCon.trx("cyGoodsISongSangChaGisiRegistInfo", 
                new Class[]{ DMYM004.class }, new Object[]{ model })).booleanValue();
    }  
	
    private boolean getPMYM004(PMYM004 model) throws Exception {
        EJBConnector ejbCon = new EJBConnector("default", "JNDIWorkOrderInfoReg", this);
        return considerWarn(((Boolean)ejbCon.trx("syRollingWorkOrderInsert", 
                new Class[]{ PMYM004.class }, new Object[]{ model })).booleanValue(), model);
    }

    private boolean getPOYM001(POYM001 model) throws Exception {
        EJBConnector ejbCon = new EJBConnector("default", "JNDICoilInfoReg", this);
        return ((Boolean)ejbCon.trx("receiveInCoilInfo", 
                new Class[]{ POYM001.class }, new Object[]{ model })).booleanValue();
    }  

    private boolean getPOYM004(POYM004 model) throws Exception {
        EJBConnector ejbCon = new EJBConnector("default", "JNDISPMConStatReg", this);
        return ((Boolean)ejbCon.trx("receiveSPMConStat", 
                new Class[]{ POYM004.class }, new Object[]{ model })).booleanValue();
    }  
    /* 최규성
     * SPM2 관련 내부인터페이스 추가
     * 
     * */
    private boolean getPOYM010(POYM010 model) throws Exception {
        EJBConnector ejbCon = new EJBConnector("default", "JNDISPMConStatMReg", this);
        return ((Boolean)ejbCon.trx("receiveSPMConStatM", 
                new Class[]{ POYM010.class }, new Object[]{ model })).booleanValue();
    }
    
    private boolean getDMYM007(DMYM007 model) throws Exception {
        EJBConnector ejbCon = new EJBConnector("default", "JNDICoilInfoReg", this);
        return ((Boolean)ejbCon.trx("receiveInCoilInfo", 
                new Class[]{ DMYM007.class }, new Object[]{ model })).booleanValue();
    }
    
    private boolean getPCYM001(PCYM001 model) throws Exception {
        EJBConnector ejbCon = new EJBConnector("default", "JNDIWorkOrderInfoReg", this);
        return considerWarn(((Boolean)ejbCon.trx("syZoneInReservationCancel", 
                new Class[]{ PCYM001.class }, new Object[]{ model })).booleanValue(), model);
    }  
    
    private boolean getPCYM002(PCYM002 model) throws Exception {
        EJBConnector ejbCon = new EJBConnector("default", "JNDIWorkOrderInfoReg", this);
        return considerWarn(((Boolean)ejbCon.trx("syZoneInReservationInsert", 
                new Class[]{ PCYM002.class }, new Object[]{ model })).booleanValue(), model);
    }  

    private boolean getPCYM003(PCYM003 model) throws Exception {
        EJBConnector ejbCon = new EJBConnector("default", "JNDISlabInfoReg", this);
        return considerWarn(((Boolean)ejbCon.trx("slabMissingResult", 
                new Class[]{ PCYM003.class }, new Object[]{ model })).booleanValue(), model);
    }      
    
    private boolean getPMYM002(PMYM002 model) throws Exception {
        EJBConnector ejbCon = new EJBConnector("default", "JNDIWorkOrderInfoReg", this);
        return considerWarn(((Boolean)ejbCon.trx("syMoveInstructionCancel", 
                new Class[]{ PMYM002.class }, new Object[]{ model })).booleanValue(), model);
    }  
    
    private boolean getPMYM001(PMYM001 model) throws Exception {
        EJBConnector ejbCon = new EJBConnector("default", "JNDIWorkOrderInfoReg", this);
        return considerWarn(((Boolean)ejbCon.trx("syMoveInstructionInsert", 
                new Class[]{ PMYM001.class }, new Object[]{ model })).booleanValue(), model);
    }
    
    private boolean getPOYM005(POYM005 model) throws Exception {        
        EJBConnector ejbCon = new EJBConnector("default", "JNDISlabInfoReg", this);
        return considerWarn(((Boolean)ejbCon.trx("sySlabInfoInsert2", 
                new Class[]{ POYM005.class }, new Object[]{ model })).booleanValue(), model);
    }
    
    private boolean getQMYM001(QMYM001 model) throws Exception {
        EJBConnector ejbCon = new EJBConnector("default", "JNDISlabInfoReg", this);
        return considerWarn(((Boolean)ejbCon.trx("syLieSlabInfoInsert", 
                new Class[]{ QMYM001.class }, new Object[]{ model })).booleanValue(), model);
    }
    
    private boolean getPOYM003(POYM003 model) throws Exception {
        EJBConnector ejbCon = new EJBConnector("default", "JNDIWorkOrderInfoReg", this);
        return ((Boolean)ejbCon.trx("cyJungJungGisiRegistInfo", 
                new Class[]{ POYM003.class }, new Object[]{ model })).booleanValue();
    }  
    
    private boolean getPMYM008(PMYM008 model) throws Exception {
        EJBConnector ejbCon = new EJBConnector("default", "JNDIWorkOrderInfoReg", this);
        return ((Boolean)ejbCon.trx("cyJungJungGisiRegistInfo", 
                new Class[]{ PMYM008.class }, new Object[]{ model })).booleanValue();
    }  
    
    private boolean getPMYM006(PMYM006 model) throws Exception {
        EJBConnector ejbCon = new EJBConnector("default", "JNDIWorkOrderInfoReg", this);
        return ((Boolean)ejbCon.trx("cyISongGisiRegistInfo", 
                new Class[]{ PMYM006.class }, new Object[]{ model })).booleanValue();
    }  
    
    private boolean getPMYM007(PMYM007 model) throws Exception {
        EJBConnector ejbCon = new EJBConnector("default", "JNDIWorkOrderInfoReg", this);
        return ((Boolean)ejbCon.trx("cyISongGisiCancelInfo", 
                new Class[]{ PMYM007.class }, new Object[]{ model })).booleanValue();
    }
    
    private boolean getPOYM002(POYM002 model) throws Exception {
        EJBConnector ejbCon = new EJBConnector("default", "JNDICoilInfoReg", this);
        return ((Boolean)ejbCon.trx("receiveInCoilInfo", 
                new Class[]{ POYM002.class }, new Object[]{ model })).booleanValue();
    }  
    
    private boolean getPMYM005(PMYM005 model) throws Exception {
        EJBConnector ejbCon = new EJBConnector("default", "JNDICoilInfoReg", this);
        return ((Boolean)ejbCon.trx("receiveInCoilInfo", 
                new Class[]{ PMYM005.class }, new Object[]{ model })).booleanValue();
    }  
    
    private boolean getDMYM005(DMYM005 model) throws Exception {
        EJBConnector ejbCon = new EJBConnector("default", "JNDICoilInfoReg", this);
        return ((Boolean)ejbCon.trx("receiveInCoilInfo", 
                new Class[]{ DMYM005.class }, new Object[]{ model })).booleanValue();
    }  
    
    private boolean getDMYM006(DMYM006 model) throws Exception {
        EJBConnector ejbCon = new EJBConnector("default", "JNDICoilInfoReg", this);
        return ((Boolean)ejbCon.trx("receiveInCoilInfo", 
                new Class[]{ DMYM006.class }, new Object[]{ model })).booleanValue();
    }  
 
    private boolean getQMYM002(QMYM002 model) throws Exception {
        EJBConnector ejbCon = new EJBConnector("default", "JNDIACCoolOrdReg", this);
        return considerWarn(((Boolean)ejbCon.trx("receiveCoilStatusChange", 
                new Class[]{ QMYM002.class }, new Object[]{ model })).booleanValue(), model);
    }
	
	private boolean getQMYM003(QMYM003 model) throws Exception {
        EJBConnector ejbCon = new EJBConnector("default", "JNDIACCoolOrdReg", this);
        return considerWarn(((Boolean)ejbCon.trx("receiveSlabStatusChange", 
                new Class[]{ QMYM003.class }, new Object[]{ model })).booleanValue(), model);
    }
    
    private boolean getPOYM007(POYM007 model) throws Exception {
        EJBConnector ejbCon = new EJBConnector("default", "JNDIACCoolOrdReg", this);
        return considerWarn(((Boolean)ejbCon.trx("receivePOStockMoveConditionStatusChange", 
                new Class[]{ POYM007.class }, new Object[]{ model })).booleanValue(), model);
    }

    private boolean getPOYM008(POYM008 model) throws Exception {
        EJBConnector ejbCon = new EJBConnector("default", "JNDIACCoolOrdReg", this);
        return considerWarn(((Boolean)ejbCon.trx("receivePOCoolStatusChange", 
                new Class[]{ POYM008.class }, new Object[]{ model })).booleanValue(), model);
    }
    
    private boolean getPOYM009(POYM009 model) throws Exception {
        EJBConnector ejbCon = new EJBConnector("default", "JNDICTnkSpplWrkOrdReg", this);
        return considerWarn(((Boolean)ejbCon.trx("receiveWTWork", 
                new Class[]{ POYM009.class }, new Object[]{ model })).booleanValue(), model);
    }
    
    private boolean getDMYM009(DMYM009 model) throws Exception {
//        EJBConnector ejbCon = new EJBConnector("default", "JNDIACCoolOrdReg", this);
//        return considerWarn(((Boolean)ejbCon.trx("receiveDMStockMoveConditionStatusChange", 
//                new Class[]{ DMYM009.class }, new Object[]{ model })).booleanValue(), model);
        return true;
    }

    private boolean getDMYM010(DMYM010 model) throws Exception {
        EJBConnector ejbCon = new EJBConnector("default", "JNDIACCoolOrdReg", this);
        return considerWarn(((Boolean)ejbCon.trx("receiveDMStockMoveConditionStatusChange", 
                new Class[]{ DMYM010.class }, new Object[]{ model })).booleanValue(), model);
    }

    private boolean getPSYM001(PSYM001 model) throws Exception {
    	EJBConnector ejbCon = new EJBConnector("default", "JNDIWorkOrderInfoReg", this);
        return considerWarn(((Boolean)ejbCon.trx("startLoadOrderOrCancel", 
                new Class[]{ PSYM001.class }, new Object[]{ model })).booleanValue(), model);
     
  }
  
  private boolean getPSYM002(PSYM002 model) throws Exception {
    	EJBConnector ejbCon = new EJBConnector("default", "JNDIWorkOrderInfoReg", this);
        return considerWarn(((Boolean)ejbCon.trx("startLoadOrderOrCancel_02", 
                new Class[]{ PSYM002.class }, new Object[]{ model })).booleanValue(), model);
     
  }
    
    private boolean considerWarn(boolean isTrue, CommonModel model) {
    	logger.println(LogLevel.DEBUG, this, 
    	        "MODEL WARNING SIZE: "+ ModelWarning.getInstance().size());
        return isTrue;
    }
	//////////////////////////////////////////////////////////////////////////////
	/////////////////////B열연수정시작///////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
	/**
	 * 오퍼레이션명 : 일관제철 내부인터페이스 
	 *
		공정계획(수신 2종)	
			PMYDJ001	슬라브충당실적                 저장품테이블 업데이트 <저장품 이동조건>  0
			PMYDJ002	슬라브이송지시                 저장품테이블 업데이트 <저장품 이동조건>  0
		
		진행관리(수신 3종) 
			PTYDJ001		코일충당실적              저장품테이블 업데이트 <저장품 이동조건>  0
			PTYDJ002		코일소재이송지시           저장품테이블 업데이트 <저장품 이동조건>  0
			PTYDJ003		코일소재임가공이송지시       저장품테이블 업데이트 <저장품 이동조건> 0 (임가공사코드 추가해야함)
		
		생산통제(수신 4종)
			CTYDJ013	외판행선변경확정               보류
			CTYDJ032	B열연압연지시확정 저장품테이블       <장입LOT번호 업데이트> ---------------------- ???
		
		연주조업(수신 1종)
			CSYDJ001	연주전단실적 -- 수신된 재료번호로 기존 예정번호 업데이트  0
		
		구내운송(수신 3종)
			TSYDJ002		소재차량도착Point 요구   
			TSYDJ003		소재차량도착
			TSYDJ004		소재차량출발
		
		출하관리(수신 22종)
			DMYDR002	코일제품보류확정                  이동조건 업데이트  0---------
			DMYDR004	외판슬라브출하지시대기             이동조건 업데이트  0--------
			DMYDR005	코일제품출하지시대기               이동조건 업데이트 0-------
			DMYDR008	코일제품반납대기                   이동조건 업데이트 0-------
			DMYDR011	코일제품고간이송지시               이동조건업데이트  0 , 운송순번 업데이트 0
			DMYDR013	외판슬라브목전(주문자변경)         이동조건업데이트 0------
			DMYDR014	코일제품목전                     이동조건업데이트  0------
			DMYDR016	외판슬라브운송지시대기                이동조건, 운송순번 업데이트 0
			DMYDR020	코일제품운송지시                 이동조건, 운송순번 업데이트 0
			DMYDR022	외판슬라브운송상차지시             이동조건,운송번호 ,운송순번 ,카드번호 업데이트 0
			DMYDR023	코일제품상차지시                 이동조건,카드번호 업데이트 0
			DMYDR025	임가공이송상차지시                  이동조건,운송번호 ,운송순번 ,카드번호 업데이트 0
			DMYDR026	외판슬라브보관지시               보관매출구분 Y 업데이트 0
			DMYDR027	코일제품보관지시                보관매출구분 Y 업데이트 0
			DMYDR029	외판슬라브출하완료                이동조건업데이트, 카드번호 ''로 업데이트 0
			DMYDR030	코일제품출하완료                 이동조건업데이트, 카드번호 ''로 업데이트 0
			DMYDR032	외판슬라브반품                  이동조건업데이트 0-------
			DMYDR033	코일제품반품                   이동조건업데이트 0-------
			
			DMYDR035	외판슬라브출하차량도착실적--O ( 운송lot 편성 -> 크레인 스케줄 편성)
			DMYDR036	코일제품출하차량도착실적--O ( 크레인 스케줄 편성)
			DMYDR037	코일임가공차량도착실적--X 보류
			DMYDR039	외판슬라브출하차량출발실적--X 보류
			DMYDR040	코일제품출하차량출발실적--X 보류
			DMYDR041	코일임가공차량출발실적--X 보류
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean receiveInternal_PO(JDTORecord cModel) {
//SJH		
		String sJmsTcCd = StringHelper.evl(cModel.getFieldString("JMS_TC_CD"), StringHelper.evl(cModel.getFieldString("TC_CODE"),""));
		
		logger.println(LogLevel.INFO, this,"==>>일관제철 내부수신 전문["+sJmsTcCd+"]" );
		
		try {	        
			if("POYM001".equals(sJmsTcCd)) {		
				return procPOYM001(cModel);
			}else if("POYM002".equals(sJmsTcCd)) {	
				return procPOYM002(cModel);
			}else if("POYM003".equals(sJmsTcCd)) {	
				return procPOYM003(cModel);
			}else if("POYM004".equals(sJmsTcCd)) {	
				return procPOYM004(cModel);
			}else if("POYM005".equals(sJmsTcCd)) {	
				return procPOYM005(cModel);
			}else if("POYM006".equals(sJmsTcCd)) {	
				return procPOYM006(cModel);
			}else if("POYM007".equals(sJmsTcCd)) {	
				return procPOYM007(cModel);
			}else if("POYM008".equals(sJmsTcCd)) {	
				return procPOYM008(cModel);
//			}else if("POYM009".equals(sJmsTcCd)) {  //소스 없음	
//				return getPOYM009(cModel);
			}else if("POYM010".equals(sJmsTcCd)) {	
				return procPOYM010(cModel);
			}
			
		}catch(Exception e) {
			//throw new EJBServiceException(e);
			logger.println(LogLevel.INFO, this,"==PO 내부수신 전문["+sJmsTcCd+"] EXCEPTION 발생 " );
			
		}
		return true;
	}   

	
	private boolean procPOYM001(JDTORecord model) throws Exception {
        EJBConnector ejbCon = new EJBConnector("default", "JNDICoilInfoReg", this);
//        return ((Boolean)ejbCon.trx("receiveInCoilInfo", 
        return ((Boolean)ejbCon.trx("receivePOYM001", 
        		  new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
    }  
	
    private boolean procPOYM002(JDTORecord model) throws Exception {
        EJBConnector ejbCon = new EJBConnector("default", "JNDICoilInfoReg", this);
//        return ((Boolean)ejbCon.trx("receiveInCoilInfo", 
                return ((Boolean)ejbCon.trx("receivePOYM002", 
                new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
    }  
    private boolean procPOYM003(JDTORecord model) throws Exception {
        EJBConnector ejbCon = new EJBConnector("default", "JNDIWorkOrderInfoReg", this);
//        return ((Boolean)ejbCon.trx("cyJungJungGisiRegistInfo", 
                return ((Boolean)ejbCon.trx("receivePOYM003", 
                new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
    }  
    private boolean procPOYM004(JDTORecord model) throws Exception {
        EJBConnector ejbCon = new EJBConnector("default", "JNDISPMConStatReg", this);
//        return ((Boolean)ejbCon.trx("receiveSPMConStat", 
                return ((Boolean)ejbCon.trx("receivePOYM004", 
                new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
    }  
    private boolean procPOYM005(JDTORecord model) throws Exception {        
        EJBConnector ejbCon = new EJBConnector("default", "JNDISlabInfoReg", this);
//        return ((Boolean)ejbCon.trx("sySlabInfoInsert2", 
                return ((Boolean)ejbCon.trx("receivePOYM005", 
                new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
    }
    private boolean procPOYM006(JDTORecord model) throws Exception {
        EJBConnector ejbCon = new EJBConnector("default", "JNDISlabInfoReg", this);
//        return ((Boolean)ejbCon.trx("slabMissingResult", 
                return ((Boolean)ejbCon.trx("receivePOYM006", 
                new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
    }
    private boolean procPOYM007(JDTORecord model) throws Exception {
        EJBConnector ejbCon = new EJBConnector("default", "JNDIACCoolOrdReg", this);
//        return ((Boolean)ejbCon.trx("receivePOStockMoveConditionStatusChange", 
                return ((Boolean)ejbCon.trx("receivePOYM007", 
                new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
    }
    private boolean procPOYM008(JDTORecord model) throws Exception {
        EJBConnector ejbCon = new EJBConnector("default", "JNDIACCoolOrdReg", this);
//        return ((Boolean)ejbCon.trx("receivePOCoolStatusChange", 
                return ((Boolean)ejbCon.trx("receivePOYM008", 
                new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
    }
//    private boolean getPOYM009(JDTORecord model) throws Exception {
//       EJBConnector ejbCon = new EJBConnector("default", "JNDICTnkSpplWrkOrdReg", this);
//        return ((Boolean)ejbCon.trx("receiveWTWork", 
//                return ((Boolean)ejbCon.trx("receivePOYM009", 
//                new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
//    }
    private boolean procPOYM010(JDTORecord model) throws Exception {
        EJBConnector ejbCon = new EJBConnector("default", "JNDISPMConStatMReg", this);
//        return ((Boolean)ejbCon.trx("receiveSPMConStatM", 
        return ((Boolean)ejbCon.trx("receivePOYM010", 
                new Class[]{ JDTORecord.class }, new Object[]{ model })).booleanValue();
    }	
} 
