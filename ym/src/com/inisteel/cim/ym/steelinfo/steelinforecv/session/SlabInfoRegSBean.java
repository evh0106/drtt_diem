package com.inisteel.cim.ym.steelinfo.steelinforecv.session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.log.LogService;
import jspeed.base.log.LogServiceConfig;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordImplMap;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;
import com.inisteel.cim.common.util.MessageHelper;

import com.inisteel.cim.common.util.CommonUtil;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.common.jms.model.CommonModel;
import com.inisteel.cim.common.jms.model.ym.PCYM003;
import com.inisteel.cim.common.jms.model.ym.PMYM003;
import com.inisteel.cim.common.jms.model.ym.POYM005;
import com.inisteel.cim.common.jms.model.ym.POYM006;
import com.inisteel.cim.common.jms.model.ym.QMYM001;
import com.inisteel.cim.ym.ilkwan.dao.YdCarSpecDao;
import com.inisteel.cim.ym.ilkwan.dao.YdStkColDao;
import com.inisteel.cim.ym.ilkwan.dao.YdMtlStatModHistDao;
import com.inisteel.cim.ym.ilkwan.dao.YdStockDao;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.common.ModelWarning;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO;
import com.inisteel.cim.ym.steelinfo.steelinforecv.dao.YdSlabMoveBayRankingDAO;
import com.inisteel.cim.ym.steelinfo.steelinforecv.dao.StoredMaterialDAO;
import com.inisteel.cim.ym.steelinfo.steelinforecv.dao.YdStockDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO;
/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="SlabInfoRegEJB" jndi-name="JNDISlabInfoReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class SlabInfoRegSBean extends BaseSessionBean {
    private Logger logger 			= null;
    private ymCommonDAO ymCommonDAO = null;
    private String szSessionName = "JNDISlabInfoReg";
	// [DEBUG] message flag
	private boolean bDebugFlag=true; 
	private YmComm ymComm = new YmComm();
	
	public void ejbCreate() {
        LogServiceConfig config = 
            LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
        logger 		= new Logger(config);
        ymCommonDAO = new ymCommonDAO();
	}	
		
	/**
	 * 오퍼레이션명 : 
	 *
	 * 공정L-3의 요청에 대한 SLAB 저장조건 변경 처리
        * -공정시스템으로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * -이송지시, 압연작업, 정정작업 시점에 SLAB 충당으로 인한 저장조건 변경 요청을 처리한다.
        * -TC_CD	: PMYM003
        * -I/F ID	: YM-LIF-035
        * 1	전문코드	TC			CHAR	07		
        * 2	발생일자	Date		CHAR	10		YYYY-MM-DD
        * 3	발생시간	Time		CHAR	08		HH-MM-SS
        * 4	YARD 구분	YD_GP	CHAR	1		1:A열연Coil,2:B열연Slab,3:B열연Coil
        * 5	Slab No	SlabNo		CHAR	11		
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */       
    public boolean slabConditionModify(PMYM003 model) {
        logger.println(LogLevel.DEBUG, this, "TODO: SLAB 저장조건변경 처리");
        try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
            /**
             * vlaid check
             */
            if(model.getSlabNo().length() > 11) {
                ModelWarning.getInstance().setWarning(model, "슬라브번호의 길이가 11보다 작아야 합니다.");
                return false;
            }            
            /**
             * SLAB 정보를 조회한다.
             */
            JDTORecord slabInfo = ymCommonDAO.readSlabInfo(model.getSlabNo());
            if(slabInfo == null) {
                ModelWarning.getInstance().setWarning(model, 
                        "슬라브 번호가 슬라브 공통 테이블에 존재하지 않습니다.");
                return false;                
            }            
            /**
             * SLAB 번호로 TB_PM_SLABCOMM 테이블을 조회한다.
             * -진도코드에 따라 야드 저장품의 저장품상태, 저장품이동조건을 UPDATE 한다.
             * -Z-->ZS	-B-->B,BS
             */
            ymCommonDAO.modifyStackLocOfStock(
                    getField(slabInfo, "STACK_LOT"), 
                    getField(slabInfo, "TERM"),
                    model.getSlabNo());
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
	 * 조업L-3의 요청에 의해 SLAB 결번실적을 처리한다.
	 * -조업 -> 야드 (미장입된 Slab 결번 시점)
        * -TC_CD	: POYM006
        * -I/F ID	: YM-LIF-022
        * 1	전문코드	TC			CHAR	07		
        * 2	발생일자	Date		CHAR	10		YYYY-MM-DD
        * 3	발생시간	Time		CHAR	08		HH-MM-SS
        * 4	YARD 구분	YD_GP	CHAR	1		1:A열연Coil,2:B열연Slab,3:B열연Coil
        * 5	Slab No	SlabNo		CHAR	11		
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */  
    public boolean slabMissingResult(POYM006 model) {
        logger.println(LogLevel.DEBUG, this, "TODO: SLAB 결번실적 처리");
        try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
            if(model.getSlabNo().length() > 11) {
                ModelWarning.getInstance().setWarning(model, "슬라브번호의 길이가 11보다 작아야 합니다.");
                return false;
            }
            ymCommonDAO.modifyMoveTermOfStock(
                    YmCommonUtil.getSlabCurrProgCd(model.getSlabNo(),"")[1], 
                    model.getSlabNo());
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
	 * 관제L-3의 요청에 의해 SLAB 결번실적을 처리한다.
	 * -관제 -> 야드 (미처리,반송 Slab 결번 시점)
        * -TC_CD	: POYM006
        * -I/F ID	: YM-LIF-022
        * 1	전문코드	TC			CHAR	07		
        * 2	발생일자	Date		CHAR	10		YYYY-MM-DD
        * 3	발생시간	Time		CHAR	08		HH-MM-SS
        * 4	YARD 구분	YD_GP	CHAR	1		1:A열연Coil,2:B열연Slab,3:B열연Coil
        * 5	Slab No	SlabNo		CHAR	11		
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */      
    public boolean slabMissingResult(PCYM003 model) {
        logger.println(LogLevel.DEBUG, this, "TODO: SLAB 결번실적 처리");
        try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			
            if(model.getSlabNo().length() > 11) { 
                ModelWarning.getInstance().setWarning(model, "슬라브번호의 길이가 11보다 작아야 합니다.");
                return false;
            }
            ymCommonDAO.modifyMoveTermOfStock(
                    YmCommonUtil.getSlabCurrProgCd(model.getSlabNo(),"")[1], 
                    model.getSlabNo());
            
            //야드 L2 전송 장입순번 Clear CALL
	     boolean isTrue = callL2LotEndInfo_Slab(model.getSlabNo());	
					            
        }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
        return true;
    }
     
    private boolean callL2LotEndInfo_Slab(String sStockId){
		
		Boolean isSuccess = new Boolean(false);
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
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


 	/**
	 * 오퍼레이션명 : 
	 *
	 * 조업시스템으로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * 1.TC_CD	: POYM005(전단실적)
        * 2.I/F ID	: YM-LIF-021
        * A열연 SLAB 추가 getProcessID = 6(전단실적)
        * 				 getProcessID = 7(전단취소)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */      
    public boolean sySlabInfoInsert(POYM005 model) {
        logger.println(LogLevel.DEBUG, this, "TODO: Slab 재료정보 등록 처리");
        try {          
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
            /**
             * 수신항목의 'Slab No'를 체크.
             */
            if(! model.validSlabNo()) {
                ModelWarning.getInstance().setWarning(model, "슬라브번호의 길이가 11보다 작아야 합니다.");
                return false;
            }
            /**
             * 수신항목의 'Slab No'로 공정시스템의 PM_SLAB공통 Table을 READ 한다.
             */
            JDTORecord slabInfo	= ymCommonDAO.readSlabInfo(model.getSlabNo());
            String slabNo		= getField(slabInfo, "SLAB_NO");
            if("".equals(slabNo)) {
                if("6".equals(model.getProcessID())) {
                    throw new Exception("##### TB_PM_SLABCOMM 테이블에 슬라브 정보가 없습니다.");
                }else {
                    logger.println(LogLevel.DEBUG, this, "PM_SLAB공통 Table에 전문 내용의 'Slab No'가 존재하지 않습니다.");                    
                }
            }
            /**
             * 저장품 테이블에 READ 한 Slab No 가 존재하면 UPDATE 하고 존재하지 않으면 INSERT 한다.
             * 1. UPDATE/INSERT 항목: 산적LOT번호, 저장품이동조건
             */
            String[] sStockInfo = YmCommonUtil.getSlabCurrProgCd(slabNo,"");
        	String sProgCd   	= sStockInfo[0];
			String sStocMv   	= sStockInfo[1];
			if("".equals(sStocMv)) sStocMv = YmCommonConst.NEW_STOCK_MOVE_TERM_ZS;
			
			// 6 - 자 SLAB 생성
            if("6".equals(model.getProcessID())) {
            	JDTORecord stackjrecrod = new CraneSchDAO().getStackLayerInfoWithStockId_02(slabNo);

                if(null != stackjrecrod){
                	String sPutLoc = stackjrecrod.getFieldString("STACK_COL_GP")+
									stackjrecrod.getFieldString("STACK_BED_GP")+
									stackjrecrod.getFieldString("STACK_LAYER_GP");
                	
                	//TB_PM_SLABCOMM에 적재위치 세팅
                	new CraneSchDAO().updateSlabCommonLocInfo(slabNo, sPutLoc);
                	
                }else{
                	//createSlabInfo(getField(slabInfo, "STACK_LOT_CD"), slabNo, sStocMv);
                }
                /*
                 * 추가 : C연주 전단실적 모듈로 통일.
                 */
                EJBConnector ejbCon = new EJBConnector("default", "JNDISlabReg", this);
        		((Boolean)ejbCon.trx("procCcFsWr", 
        			  new Class[]{ JDTORecord.class }, new Object[]{ slabInfo })).booleanValue();
        		/*
                 * 추가 : 일관제철 전단실적 모듈 호출 필요.
                 */
                
        		
            	//2007-04-02 A열연 SLAB 야드 전단 실적 추가  (MCH)
            	if(!"0".equals(model.getyardID())){
            		sendSlabInfo(slabInfo, model.getSlabNo(), "1");	
            	}
                
            // 7 - 모 SLAB 종료    
            }else if("7".equals(model.getProcessID())) {
                ymCommonDAO.modifyStackStateOfLayer(slabNo);
                
                YdStockDAO dao	= new YdStockDAO();
                int iSeq = dao.updateStockDelYnInfo(slabNo,"Y");
                
                //2007-04-02 A열연 SLAB 야드 전단 실적 취소 추가  (MCH)
                if(!"0".equals(model.getyardID())){
                	sendSlabInfo(slabInfo, model.getSlabNo(), "0");
                }
            }else {
                ymCommonDAO.modifyStockTermOfStock(sStocMv, slabNo);
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
	 * 조업시스템으로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
	 * 2010,03.17 YD 전단실적처리모듈 호출로 변경
        * 1.TC_CD	: POYM005(전단실적)
        * 2.I/F ID	: YM-LIF-021
        * A열연 SLAB 추가 getProcessID = 6(전단실적)
        * 				 getProcessID = 7(전단취소)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */      
    public boolean sySlabInfoInsert2(POYM005 model) {
        logger.println(LogLevel.DEBUG, this, "TODO: Slab 재료정보 등록 처리");
        logger.println(LogLevel.DEBUG, this, "BCast전단실적처리 일관제철 모듈 호출");

        try {         
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
            /**
             * 수신항목의 'Slab No'를 체크.
             */
            if(! model.validSlabNo()) {
                ModelWarning.getInstance().setWarning(model, "슬라브번호의 길이가 11보다 작아야 합니다.");
                return false;
            }
            /**
             * 수신항목의 'Slab No'로 공정시스템의 PM_SLAB공통 Table을 READ 한다.
             */
            
            JDTORecord slabInfo	= ymCommonDAO.readSlabInfo(model.getSlabNo());
            String slabNo		= getField(slabInfo, "SLAB_NO");
            String s_STL_APPEAR_GP		= getField(slabInfo, "STL_APPEAR_GP");
            String s_SLAB_T		= getField(slabInfo, "SLAB_T");
            String s_SLAB_W		= getField(slabInfo, "SLAB_W");
            String s_SLAB_LEN		= getField(slabInfo, "SLAB_LEN");
            String s_SLAB_WT		= getField(slabInfo, "SLAB_WT");
            String s_CURR_PROG_CD		= getField(slabInfo, "CURR_PROG_CD");
            String s_ORD_YEOJAE_GP		= getField(slabInfo, "ORD_YEOJAE_GP");
            String s_ORD_NO		= getField(slabInfo, "ORD_NO");
            String s_ORD_DTL		= getField(slabInfo, "ORD_DTL");
            String s_SLAB_WO_RT_CD		= getField(slabInfo, "SLAB_WO_RT_CD");
            String s_ORD_HCR_GP		= getField(slabInfo, "ORD_HCR_GP");
            String s_HCR_GP		= getField(slabInfo, "HCR_GP");
            String s_SCARFING_YN		= getField(slabInfo, "SCARFING_YN");
            
            if("7".equals(model.getProcessID())) {
                ymCommonDAO.modifyStackStateOfLayer(slabNo);
                
                YdStockDAO dao	= new YdStockDAO();
                int iSeq = dao.updateStockDelYnInfo(slabNo,"Y");
                
                //2007-04-02 A열연 SLAB 야드 전단 실적 취소 추가  (MCH)
                if(!"0".equals(model.getyardID())){
                	sendSlabInfo(slabInfo, model.getSlabNo(), "0");
                }
            	return true;
            }
           else {
	           JDTORecord tcRecord1 = JDTORecordFactory.getInstance().create();
	         
			   tcRecord1.setField("JMS_TC_CD", "CSYDJ001");
			   tcRecord1.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
			   tcRecord1.setField("MOD_GP","I");
			   tcRecord1.setField("STL_APPEAR_GP", StringHelper.evl(s_STL_APPEAR_GP, ""));	
			   tcRecord1.setField("STL_NO", StringHelper.evl(slabNo, ""));
			   tcRecord1.setField("YD_STL_W"        ,StringHelper.evl(s_SLAB_W, ""));
			   tcRecord1.setField("YD_STL_L"        , StringHelper.evl(s_SLAB_LEN, ""));
			   tcRecord1.setField("YD_STL_T", StringHelper.evl(s_SLAB_T, ""));
			   tcRecord1.setField("YD_STL_WT", StringHelper.evl(s_SLAB_WT, ""));
			   tcRecord1.setField("CURR_PROG_CD",StringHelper.evl(s_CURR_PROG_CD, ""));
			   tcRecord1.setField("ORD_YEOJAE_GP",StringHelper.evl(s_ORD_YEOJAE_GP, ""));
			   tcRecord1.setField("ORD_NO",StringHelper.evl(s_ORD_NO, ""));
			   tcRecord1.setField("ORD_DTL",StringHelper.evl(s_ORD_DTL, ""));
			   tcRecord1.setField("SLAB_WO_RT_CD",StringHelper.evl(s_SLAB_WO_RT_CD, ""));
			   tcRecord1.setField("ORD_HCR_GP",StringHelper.evl(s_ORD_HCR_GP, ""));
			   tcRecord1.setField("HCR_GP",StringHelper.evl(s_HCR_GP, ""));
			   tcRecord1.setField("CC_MC_CD","1");
			   tcRecord1.setField("SCARFING_YN",StringHelper.evl(s_SCARFING_YN, ""));
			   tcRecord1.setField("ARR_WLOC_CD","");
			   tcRecord1.setField("MultiSend","Y");
			   
			   logger.println(LogLevel.DEBUG, this, "BCast전단실적처리 일관제철 모듈 호출");
				EJBConnector ejbCon = new EJBConnector("default", "StockSpecRegFaEJB", this);
				ejbCon.trx("rcvCcFsWr", new Class[]{ JDTORecord.class }, new Object[]{ tcRecord1 });
				return true;	
           }
      
        }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
    }    
    
    
    /**
     * 저장품 테이블에 INSERT OR UPDATE 한다.
     * @param 	ydStockDAO		: DAO
     * @param 	slabNo			: 슬라브번호
     * @param 	stockStat		: 저장품상태
     * @return 	1(성공),0(실패)
     */
    private int createShearSlabInfo(String stackLoc, String slabNo, String  sStocMv) {
    	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return 0;
		}
		
		
        logger.println(LogLevel.DEBUG, this, "저장품 등록 ");
        List insertData = new ArrayList();
        insertData.add(slabNo);						//저장품 ID
        insertData.add(YmCommonConst.ITEM_SM); 		//저장품 품목
        insertData.add(stackLoc);					//산적 LOT 번호
        insertData.add(sStocMv);					//저장품 이동 조건
        insertData.add(YmCommonConst.SCARFING_N); 	//SCARFING 보급 유무
        insertData.add("SYSTEM");	//등록자        
        return new YdStockDAO().AslabcreateData(insertData);		        
    }
    
    /**
     * 연주 전단 완료 시, 항만 야드에서 옥외 야드로 이송 차량 도착 시 L-2로 송신
     * TC		: CM1BP02
     * INTERFACE: YM-BIF-030
     *1	전문코드			CHAR	7		
     *2	발생일자			CHAR	10		YYYY/MM/DD
     *3	발생시간			CAR		08		HH:MM:SS
     *4	전문구분			CHAR	01		발생시스템 구분
     *5	전문길이			CHAR	04		
     *6	SLAB 번호		CHAR	11		
     *7	주문/여재 구분		CHAR	01		
     *8	제작번호/행번		CHAR	13		
     *9	두께				CHAR	07	㎜	소수점3자리 (###.###)
     *10	폭			CHAR	06	㎜	소수점1자리 (####.#)
     *11	길이			CHAR	06	㎜	
     *12	중량			CHAR	05	Kg	
     *13	예정 COIL NO	CHAR	10		
     *14	압연 예정 일시	CHAR	14	YYYYMMDDHHMMSS
     *15	장입 LOT 번호	CHAR	10
     *		구입SLABNO	CHAR	25
     *16	LOT내 작업 순위CHAR	4
     */    	            
    private void sendSlabInfo(JDTORecord slabInfo, String stockId, String gp) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
		
	    Map tc = ymCommonDAO.readColumnLenOfTc(YmCommonConst.TC_CM1BP02);
        StringBuffer sendMsg = new StringBuffer();
        sendMsg.append(YmCommonConst.TC_CM1BP02);
        sendMsg.append(YmCommonUtil.getStringYMD("/"));
        sendMsg.append(YmCommonUtil.getStringHMS(":"));
        sendMsg.append(YmCommonConst.FORM_I);
        appendMsgNum(sendMsg, ""+ (YmCommonUtil.getTotalLenOfTc(tc) - 30), getFieldLen(tc, "전문길이"));
        appendMsg(sendMsg, stockId,								getFieldLen(tc, "SLAB번호"));
        appendMsg(sendMsg, gp,									getFieldLen(tc, "처리구분"));
        appendMsg(sendMsg, getField(slabInfo, "ORD_YEOJAE_GP"), getFieldLen(tc, "주문여재구분"));
        appendMsg(sendMsg, getField(slabInfo, "PRODUC_NO"),		getFieldLen(tc, "제작번호행번"));
        String t 	= YmCommonUtil.format(getField(slabInfo, "SLAB_T"), 3, 3).replace('.', ' ');
        String w 	= YmCommonUtil.format(getField(slabInfo, "SLAB_W"), 4, 1).replace('.', ' ');
        appendMsgNum(sendMsg, t.replaceAll(" ", ""),			getFieldLen(tc, "두께"));
        appendMsgNum(sendMsg, w.replaceAll(" ", ""),			getFieldLen(tc, "폭"));
        appendMsgNum(sendMsg, getField(slabInfo, "SLAB_LEN"), 	getFieldLen(tc, "길이"));
        appendMsgNum(sendMsg, getField(slabInfo, "SLAB_WT"),	getFieldLen(tc, "중량"));
        appendMsg(sendMsg, getField(slabInfo, "COIL_NO"), 		getFieldLen(tc, "예정COILNO"));
        appendMsg(sendMsg, getField(slabInfo, "MILL_PLAN_DDTT"),getFieldLen(tc, "압연예정일시"));
        appendMsg(sendMsg, getField(slabInfo, "LOT_NO"),		getFieldLen(tc, "장입LOT번호"));
        appendMsgNum(sendMsg, getField(slabInfo, "LOT_IN_SLAB_PRIOR"),getFieldLen(tc, "LOT내작업순위"));
        appendMsg(sendMsg, getField(slabInfo, "BUY_SLAB_NO"), 	getFieldLen(tc, "구입SLABNO"));
        appendMsg(sendMsg, " ", getFieldLen(tc, "장입순번"));
        
        sendQueue(YmCommonConst.TC_CM1BP02, sendMsg.toString());
    }

 	/**
	 * 오퍼레이션명 : 
	 *
	 * 품질시스템으로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * 1.TC_CD	: QMYM001
        * 2.I/F ID	: YM-LIF-021
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */           
    public boolean syLieSlabInfoInsert(QMYM001 model) {
        logger.println(LogLevel.DEBUG, this, "TODO: 부두 Slab 재료정보 등록 처리");
        try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			
            /**
             * 수신항목의 'Slab No'를 체크.
             */
            if(model.getSlabNo().length() > 11) {
                ModelWarning.getInstance().setWarning(model, "슬라브번호의 길이가 11보다 작아야 합니다.");
                return false;
            }            
            /**
             * 수신항목의 'Slab No'로 공정시스템의 PM_SLAB공통 Table을 READ 한다.
             */
            JDTORecord slabInfo	= ymCommonDAO.readSlabInfo(model.getSlabNo());
            String slabNo		= getField(slabInfo, "SLAB_NO");
            if("".equals(slabNo)) {
                logger.println(LogLevel.DEBUG, this, 
                        "PM_SLAB공통 Table에 전문 내용의 'Slab No'가 존재하지 않습니다.");
            }            
            /**
             * 저장품 테이블에 READ 한 Slab No 가 존재하면 UPDATE 하고 존재하지 않으면 INSERT 한다.
             * 1. UPDATE/INSERT 항목: 산적LOT번호, 저장품이동조건
             */
            if("1".equals(model.getProcessId())) {
            	String sSlabNo 		= model.getSlabNo().trim();
            	String[] sStockInfo = YmCommonUtil.getSlabCurrProgCd(sSlabNo,"");
            	String sProgCd   	= sStockInfo[0];
				String sStocMv   	= sStockInfo[1];
				if("".equals(sStocMv)) sStocMv = YmCommonConst.NEW_STOCK_MOVE_TERM_1S;
				
                createSlabInfo(getField(slabInfo, "STACK_LOT_CD"), sSlabNo, sStocMv);
                /**
                 * 야드L-2로 슬라브 정보 송신
                 */
                sendSlabInfo(slabInfo, model.getSlabNo(), model.getProcessId());
            }else if("2".equals(model.getProcessId())) {
                if("".equals(getField(slabInfo, "LAYER_STOCK_ID"))) {
                    ymCommonDAO.removeStock(model.getSlabNo());
                    /**
                     * 야드L-2로 슬라브 정보 송신
                     */
                    sendSlabInfo(slabInfo, model.getSlabNo(), model.getProcessId());
                }else {
                    ModelWarning.getInstance().setWarning(model, "슬라브번호"+ model.getSlabNo() +"가 이미 야드맵에 적치되어 있습니다.");
                    return false;
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
     * 저장품 테이블에 INSERT 한다.
     * @param 	ydStockDAO		: DAO
     * @param 	slabNo			: 슬라브번호
     * @param 	stockStat		: 저장품상태
     * @return 	1(성공),0(실패)
     */
    private int createSlabInfo(String stackLoc, String slabNo, String  sStocMv) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return 0;
		}
		
		
        logger.println(LogLevel.DEBUG, this, "저장품 등록 ");
        List insertData = new ArrayList();
        insertData.add(slabNo);					//저장품 ID
        insertData.add("");						//작업예약 ID
        insertData.add(YmCommonConst.ITEM_SM); 	//저장품 품목
        insertData.add("");						//저장품 상태        
        insertData.add("");		//저장품 냉각 상태
        insertData.add("");		//저장품 냉각 시작 일시
        insertData.add("");		//저장품 냉각 시작 온도
        insertData.add(stackLoc);	//산적 LOT 번호
        insertData.add(sStocMv);	//저장품 이동 조건
        insertData.add("");		//이송 설비 구분
        insertData.add("");		//이송 설비 BED 구분
        insertData.add("");		//이송 설비 단 구분
        insertData.add("");		//장입 LOT 번호
        insertData.add("");		//이송 지시 번호
        insertData.add("");		//운송 작업지시 번호
        insertData.add(YmCommonConst.SCARFING_N); 	//SCARFING 보급 유무
        insertData.add("");		//차량 CARD 번호
        insertData.add("");		//정정 보급 순서
        insertData.add("");		//CTS 중계 구분
        insertData.add("");		//CTS 중계 동
        insertData.add("");		//CTS 중계 SADDLE
        insertData.add("");		//하차 YARD
        insertData.add("");		//하차 동
        insertData.add("SYSTEM");	//등록자
        //등록 일시
        insertData.add("");		//수정자
        insertData.add("");		//수정 일시
        //삭제 유무
//            냉각상태:HA-냉각중/0-8사이(현재시간-냉각시작일시)
        return new YdStockDAO().createData(insertData);		        
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
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
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
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		
        return StringHelper.evl(data.getFieldString(name), "").trim();
    }

    /**
     * name parameter에 대한 값을 반환한다.
     * @param data	
     * @param name  
     * @return
     */
    private int getFieldLen(Map data, String name) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return 0;
		}
		
        return StringHelper.parseInt((String)data.get(name), 0);
    }

    /**
     * 공백을 cnt 만큼 리턴한다.
     * @param cnt	공백 수
     * @return
     */
    private void appendMsg(StringBuffer buffer, String field, int cnt) {
	    try{	
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
			
			
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
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
			
			
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
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
		
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
	 * 해당 슬라브 재료번호에 해당하는 상세정보를 가져와 JDTORecord로 데이터를 리턴한다.
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                
	public JDTORecord selectSalbDetInfo(String queryId, String slabNo) {
		YdStockDAO ydstockDAO = null;	    
	    JDTORecord returnRecord = null;
	    try{
	    	
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			
	    	ydstockDAO = new YdStockDAO();
	    	returnRecord = ydstockDAO.getData(queryId, new Object[]{slabNo});
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
	 *  스카핑 지연 사유 등록.
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
    public JDTORecord updateScarfingDelayPUP(JDTORecord paramRecord) {
        logger.println(LogLevel.INFO,this,"Called updateScarfingDelayPUP() Start");

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        int count=0;
        JDTORecord dtoRecordR = null;
        List paramList = new ArrayList();	
        StoredMaterialDAO storedmaterialDAO = new StoredMaterialDAO();
        try{
        	logger.println(LogLevel.INFO,this,"tryhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");	
            dtoRecordR = JDTORecordFactory.getInstance().create();
            logger.println(LogLevel.INFO,this,"tryhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh"+paramRecord);
            if(paramRecord != null) {
                    paramList.add(StringHelper.evl(paramRecord.getFieldString("DELAY_GP"), ""));      
                    paramList.add(StringHelper.evl(paramRecord.getFieldString("DELAY_DTL"), ""));
                    paramList.add(StringHelper.evl(paramRecord.getFieldString("REMARK"), ""));
                    paramList.add(StringHelper.evl(paramRecord.getFieldString("SLAB_NO"), ""));
                    paramList.add(StringHelper.evl(paramRecord.getFieldString("STEP_NO"), ""));
                    logger.println(LogLevel.INFO,this,"1111111111111111111111111111111"+paramList);	
                    count = storedmaterialDAO.updateScarfingDelayPUP(paramList);  
                    logger.println(LogLevel.INFO,this,"ㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎㅎ"+count);
                    if(count == 0) {
                        dtoRecordR.setResultCode("MSG0034");
                        dtoRecordR.setResultMsg(MessageHelper.getUserMessage("MSG0034",
                                new String[] { "해당 레코드 " }, "{1} 이/가 존재하지 않습니다.") );
                        m_ctx.setRollbackOnly();
                    }
                    if(count == 1) {
                        dtoRecordR.setResultCode("MSG0042");
                        dtoRecordR.setResultMsg(MessageHelper.getUserMessage("MSG0042",
                                new String[] {"수정"}, "수정 작업이 정상적으로 완료되었습니다."));
                    }
                }
            
            return dtoRecordR;
        } catch (DAOException daoe) {
            throw daoe;
        } catch (Exception e) {
            throw new EJBServiceException(e);
        }
    }
	
	
	
	
 	/**
	 * 오퍼레이션명 : 
	 *
	 *  스카핑 실적패턴 수정.
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
    public JDTORecord updateScarfingPatternWrt(JDTORecord paramRecord) {
        logger.println(LogLevel.INFO,this,"Called updateScarfingPatternWrt() Start");

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        int count =0;
        JDTORecord dtoRecordR = null;
        List rtnList = null;

        StoredMaterialDAO storedmaterialDAO = new StoredMaterialDAO();

        try{
            dtoRecordR = JDTORecordFactory.getInstance().create();

            rtnList = (List)paramRecord.getField("LIST");

            if(rtnList != null) {

                JDTORecord jRecord = null;
                List paramList = new ArrayList();

                for(int ii=0 ; ii<rtnList.size() ; ii++) {
                    jRecord = (JDTORecord)rtnList.get(ii);

                    paramList.clear();

                    paramList.add(StringHelper.evl(jRecord.getFieldString("SCARFING_PATTERN" ), "")); //사업장 CODE
                    paramList.add(StringHelper.evl(jRecord.getFieldString("SCARFING_PATTERN_WRSLT" ), "")); //사업장 CODE
                    paramList.add(StringHelper.evl(jRecord.getFieldString("SLAB_NO" ), "")); //마감 년월
                    paramList.add(StringHelper.evl(jRecord.getFieldString("STEP_NO"    ), "")); //하역 여부

                    count = storedmaterialDAO.updateScarfingPatternWrt(paramList);

                    if(count == 0) {
                        dtoRecordR.setResultCode("MSG0034");
                        dtoRecordR.setResultMsg(MessageHelper.getUserMessage("MSG0034", "자료가 존재하지 않습니다.") );
                        m_ctx.setRollbackOnly();
                        break;
                    } 
                }
            }

            if(count ==1) {
                dtoRecordR.setResultCode("MSG0042");
                dtoRecordR.setResultMsg(MessageHelper.getUserMessage("MSG0042",
                        new String[] {"수정"}, "수정 작업이 정상적으로 완료되었습니다.")
                );
            }
            return dtoRecordR;
        } catch (DAOException daoe) {
            throw daoe;
        } catch (Exception e) {
            throw new EJBServiceException(e);
        }
    }
    
    /**
	 * 오퍼레이션명 : 
	 *
	 *  핸드 스카핑 실적 등록/수정.
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
    public JDTORecord updateHdScarfingWrt(JDTORecord paramRecord) {
        logger.println(LogLevel.INFO,this,"Called updateHdScarfingWrt() Start");

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        JDTORecord dtoRecordR = null;

        StoredMaterialDAO storedmaterialDAO = new StoredMaterialDAO();

        try{
            dtoRecordR = JDTORecordFactory.getInstance().create();
            
            List paramList = new ArrayList();
            
            String sDate 		= StringHelper.evl(paramRecord.getFieldString("DATE" ), ""); 	  //일자
            String sWorkTeam 	= StringHelper.evl(paramRecord.getFieldString("WORK_TEAM" ), ""); //근
            String sSlabSu 		= StringHelper.evl(paramRecord.getFieldString("SLAB_SU" ), "0");   //본수
            String sSlabWt 		= StringHelper.evl(paramRecord.getFieldString("SLAB_WT"    ), "0");//중량

            paramList.add(sDate);
            paramList.add(sWorkTeam);
            paramList.add(sSlabSu);
            paramList.add(sSlabWt);
            paramList.add(sDate);
            paramList.add(sWorkTeam);
            paramList.add(sSlabSu);
            paramList.add(sSlabWt);
            
            int count = storedmaterialDAO.updateHdScarfingWrt(paramList);
            
            if(count ==1) {
	            dtoRecordR.setResultCode("MSG0042");
	            dtoRecordR.setResultMsg(MessageHelper.getUserMessage("MSG0042",
	                        new String[] {"수정"}, "수정 작업이 정상적으로 완료되었습니다.")
	                );
            }
            return dtoRecordR;
        } catch (DAOException daoe) {
            throw daoe;
        } catch (Exception e) {
            throw new EJBServiceException(e);
        }
    }
    
	
	
	

 	/**
	 * 오퍼레이션명 : 
	 *
	 * 해당 슬라브 재료번호에 해당하는 슬라브 이력정보를 가져와 JDTORecord로 데이터를 리턴한다.
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                     
	public List selectSalbLocHis(String queryId, String slabNo){
		YdStockDAO ydstockDAO = null;	    
	    List slabHis = null;
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
	    	ydstockDAO = new YdStockDAO();
	    	slabHis = ydstockDAO.getListData(queryId, new Object[]{slabNo});
	    	return slabHis;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}


 	/**
	 * 오퍼레이션명 : 
	 *
	 * 해당 슬라브 재료번호에 해당하는 슬라브 이력정보를 가져와 JDTORecord로 데이터를 리턴한다.
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */        
	public List scarFingList(String queryId, String lotNum, String stack_gp){
		YdStockDAO ydstockDAO = null;	    
	    List slabHis = null;
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
	    	ydstockDAO = new YdStockDAO();
	    	slabHis = ydstockDAO.getListData(queryId, new Object[]{lotNum, stack_gp});
	    	return slabHis;
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
	public List scarFingList(String queryId, String stack_gp){
		YdStockDAO ydstockDAO = null;	    
	    List slabHis = null;
	    try{
	    	
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
	    	ydstockDAO = new YdStockDAO();
	    	slabHis = ydstockDAO.getListData(queryId, new Object[]{stack_gp});
	    	return slabHis;
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
	public List getListSlabSCResult(String queryId, String yd_gp, String fromdate, String todate){
		YdStockDAO ydstockDAO = null;	    
	    List slabHis = null;
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
	    	ydstockDAO = new YdStockDAO();
	    	slabHis = ydstockDAO.getListData(queryId, new Object[]{yd_gp, fromdate, todate});
	    	return slabHis;
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
	public int updateStockScarfing(String qeury, List listData) throws EJBServiceException{   
    	try { 	
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
    		YdStockDAO ydstockDAO = new YdStockDAO();  			
   			return ydstockDAO.updateMoveProduct(qeury, listData);
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
	public List getMatList(String queryID, List listData) throws EJBServiceException{   
		try{
			
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			YdStackLayerDAO ydstacklayerDAO = new YdStackLayerDAO();
			return ydstacklayerDAO.getListData(queryID, listData);
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
	public List getListBuySlabNo(String queryID, List listData) throws EJBServiceException{   
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			YdStackLayerDAO ydstacklayerDAO = new YdStackLayerDAO();
			return ydstacklayerDAO.getListData(queryID, listData);
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
	public int updateBuySlabNo(String queryid, List listData) throws EJBServiceException ,DAOException {   	 
		try {		
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			YdStackLayerDAO ydstacklayerDAO = new YdStackLayerDAO(); 
			return ydstacklayerDAO.updateData(queryid, listData);
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
	public int updateSupplySeq(String queryid, List listData) throws EJBServiceException ,DAOException {   	 
		try {	
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			YdStockDAO ydstockDAO = new YdStockDAO(); 
			return ydstockDAO.updateData(queryid, listData);
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
	public int updateScarfingSupply(String queryid, List listData) throws EJBServiceException ,DAOException {   	 
		try {		
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			YdStockDAO ydstockDAO = new YdStockDAO(); 
			return ydstockDAO.updateData(queryid, listData);
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
	public JDTORecord getPutPositionState(String queryID, List listData) throws EJBServiceException ,DAOException{	
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			YdStockDAO ydstockDAO = new YdStockDAO(); 
			return ydstockDAO.getData(queryID, listData);
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
	public List getListBedgp(String queryID, String Stack_col_gp) throws EJBServiceException ,DAOException{	
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			return new YdStockDAO().getListData(queryID, new Object[]{Stack_col_gp});
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
	public List getListSlabMoveBayRanking(String queryID) throws EJBServiceException ,DAOException{	
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			return new YdSlabMoveBayRankingDAO().getListData(queryID);
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
	public List getListSlabMoveBayRanking_BCast(String queryID) throws EJBServiceException ,DAOException{	
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			return new YdSlabMoveBayRankingDAO().getListData(queryID);
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
	public List getListSlabMoveBayRanking_Port(String queryID) throws EJBServiceException ,DAOException{	
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			return new YdSlabMoveBayRankingDAO().getListData(queryID);
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
	public List getListSlabMoveBayRanking_Ccast(String queryID) throws EJBServiceException ,DAOException{	
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			return new YdSlabMoveBayRankingDAO().getListData(queryID);
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
	public List getListSlabMoveBayRanking_C3Cast(String queryID) throws EJBServiceException ,DAOException{	
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			return new YdSlabMoveBayRankingDAO().getListData(queryID);
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
	public List getListloadLotRanking_Wloc_CD(String queryID) throws EJBServiceException ,DAOException{	
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			return new YdSlabMoveBayRankingDAO().getListData(queryID);
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
	public List getListloadLotRanking_Bay(String queryID) throws EJBServiceException ,DAOException{	
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			return new YdSlabMoveBayRankingDAO().getListData(queryID);
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
	public List getListloadLotRanking_ScarfingYN(String queryID) throws EJBServiceException ,DAOException{	
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			return new YdSlabMoveBayRankingDAO().getListData(queryID);
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
	public int updateSlabMoveBayRankingNEW(String queryID,String yd_gp, JDTORecord jrecord) throws EJBServiceException ,DAOException{
		try{
			
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			int count = 0;
			
			String chk ="";
			
			if(yd_gp.equals("A")){
				chk ="3";
			}else if(yd_gp.equals("S")){
				chk ="2";
			}else if(yd_gp.equals("0")){
				chk ="1";
			}else if(yd_gp.equals("M")){
				chk ="4";
			} 
			
			List ORD_YEOJAE_GP 	= (List)jrecord.getField("ORD_YEOJAE_GP"+chk);
			List SLAB_GP 		= (List)jrecord.getField("SLAB_GP"+chk);
			List RANKING 		= (List)jrecord.getField("RANKING"+chk);
			List DEST_BAY 		= (List)jrecord.getField("DEST_BAY"+chk);
			List PT_DEST_BAY 	= (List)jrecord.getField("PT_DEST_BAY"+chk);
			String register		= StringHelper.evl(jrecord.getFieldString("register"),"");
			
			for(int ii =0; ii<ORD_YEOJAE_GP.size();ii++){
				count+= new YdSlabMoveBayRankingDAO().updateSlabMoveBayRankingNEW(queryID,
																				 ""+RANKING.get(ii),
																				 ""+DEST_BAY.get(ii),
																				 ""+PT_DEST_BAY.get(ii),
																				 register,
																				 ""+ORD_YEOJAE_GP.get(ii),
																				 ""+SLAB_GP.get(ii)
																				 );
			}
			return count;
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
	public int updateSlabMoveBayRankingAll(String queryid, String yd_gp,String trans_gp, String Bay) throws EJBServiceException ,DAOException {   	 
		try {	
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			ymCommonDAO dao = ymCommonDAO.getInstance();
			String chk1 ="";
			String chk2 ="";
			int count = 0;
			
			if(yd_gp.equals("0")){
				chk1="H";
				chk2="C";
			}else if(yd_gp.equals("S")){
				chk1="I";
				chk2="D";
			}else if(yd_gp.equals("A")){
				chk1="J";
				chk2="E";
			}else if(yd_gp.equals("M")){
				chk1="K";
				chk2="";
			}  
			
			if("TR".equals(trans_gp)){
				count = dao.updateData(queryid, new Object[]{Bay,"",chk1,chk2});
			}else{
				count = dao.updateData(queryid, new Object[]{"",Bay,chk1,chk2});
			}
			
			  
			return count;
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
	public int updateSlabMoveBayRankingBCast(String queryID, JDTORecord jrecord) throws EJBServiceException ,DAOException{
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			int count = 0;
			List ORD_YEOJAE_GP1 	= (List)jrecord.getField("ORD_YEOJAE_GP1");
			List SLAB_GP1 		= (List)jrecord.getField("SLAB_GP1");
			List RANKING1 		= (List)jrecord.getField("RANKING1");
			List DEST_BAY1 		= (List)jrecord.getField("DEST_BAY1"); 
			String register		= StringHelper.evl(jrecord.getFieldString("register"),"");
			
			for(int ii =0; ii<ORD_YEOJAE_GP1.size();ii++){
				count+= new YdSlabMoveBayRankingDAO().updateSlabMoveBayRanking(queryID,
																				 ""+RANKING1.get(ii),
																				 ""+DEST_BAY1.get(ii),
																				 register,
																				 ""+ORD_YEOJAE_GP1.get(ii),
																				 ""+SLAB_GP1.get(ii)
																				 );
			}
			return count;
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
	public int updateSlabMoveBayRankingBCastAll(String queryid, String Bay) throws EJBServiceException ,DAOException {   	 
		try {	
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			ymCommonDAO dao = ymCommonDAO.getInstance();
			int count = dao.updateData(queryid, new Object[]{Bay});
			return count;
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
	public int updateSlabMoveBayRankingCCastAll(String queryid, String Bay) throws EJBServiceException ,DAOException {   	 
		try {	
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			ymCommonDAO dao = ymCommonDAO.getInstance();
			int count = dao.updateData(queryid, new Object[]{Bay});
			return count;
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
	public int updateSlabMoveBayRankingPortAll(String queryid, String Bay) throws EJBServiceException ,DAOException {   	 
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			ymCommonDAO dao = ymCommonDAO.getInstance();
			int count = dao.updateData(queryid, new Object[]{Bay});
			return count;
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
	public int updateSlabMoveBayRankingPort(String queryID, JDTORecord jrecord) throws EJBServiceException ,DAOException{ 
		try{  
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			int count = 0;
			List ORD_YEOJAE_GP2 	= (List)jrecord.getField("ORD_YEOJAE_GP2");
			List SLAB_GP2 		= (List)jrecord.getField("SLAB_GP2");
			List RANKING2 		= (List)jrecord.getField("RANKING2");
			List DEST_BAY2 		= (List)jrecord.getField("DEST_BAY2");
			String register		= StringHelper.evl(jrecord.getFieldString("register"),"");
			
			for(int ii =0; ii<ORD_YEOJAE_GP2.size();ii++){
				count+= new YdSlabMoveBayRankingDAO().updateSlabMoveBayRanking(queryID,
																				 ""+RANKING2.get(ii),
																				 ""+DEST_BAY2.get(ii),
																				 register,
																				 ""+ORD_YEOJAE_GP2.get(ii),
																				 ""+SLAB_GP2.get(ii)
																				 );
			}
			return count;
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
	public int updateSlabMoveBayRankingCCast(String queryID, JDTORecord jrecord) throws EJBServiceException ,DAOException{
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			int count = 0;
			List ORD_YEOJAE_GP3 	= (List)jrecord.getField("ORD_YEOJAE_GP3");
			List SLAB_GP3 		= (List)jrecord.getField("SLAB_GP3");
			List RANKING3 		= (List)jrecord.getField("RANKING3");
			List DEST_BAY3 		= (List)jrecord.getField("DEST_BAY3");
			String register		= StringHelper.evl(jrecord.getFieldString("register"),"");
			
			for(int ii =0; ii<ORD_YEOJAE_GP3.size();ii++){
				count+= new YdSlabMoveBayRankingDAO().updateSlabMoveBayRanking(queryID,
																				 ""+RANKING3.get(ii),
																				 ""+DEST_BAY3.get(ii),
																				 register,
																				 ""+ORD_YEOJAE_GP3.get(ii),
																				 ""+SLAB_GP3.get(ii)
																				 );
			}
			return count;
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}
	
	
	
	/***************수정*******************/
	/**
	 * 오퍼레이션명 : 
	 *
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public int updateSlabMoveBayRankingC3Cast(String queryID, JDTORecord jrecord) throws EJBServiceException ,DAOException{ 
		try{  
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			int count = 0;
			List ORD_YEOJAE_GP4 	= (List)jrecord.getField("ORD_YEOJAE_GP4");
			List SLAB_GP4 		= (List)jrecord.getField("SLAB_GP4");
			List RANKING4 		= (List)jrecord.getField("RANKING4");
			List DEST_BAY4 		= (List)jrecord.getField("DEST_BAY4");
			String register		= StringHelper.evl(jrecord.getFieldString("register"),"");
			
			for(int ii =0; ii<ORD_YEOJAE_GP4.size();ii++){
				count+= new YdSlabMoveBayRankingDAO().updateSlabMoveBayRanking(queryID,
																				 ""+RANKING4.get(ii),
																				 ""+DEST_BAY4.get(ii),
																				 register,
																				 ""+ORD_YEOJAE_GP4.get(ii),
																				 ""+SLAB_GP4.get(ii)
																				 );
			}
			return count;
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}
	
	
	/***************수정*******************/
	/**
	 * 오퍼레이션명 : 
	 *
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public int updateSlabMoveBayRankingC3All(String queryid, String Bay) throws EJBServiceException ,DAOException {   	 
		try {	
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			ymCommonDAO dao = ymCommonDAO.getInstance();
			int count = dao.updateData(queryid, new Object[]{Bay});
			return count;
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
	public int updateSlabloadLotRanking(String queryID, JDTORecord jrecord) throws EJBServiceException ,DAOException{
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			int count = 0;
			List RULE_ID 	= (List)jrecord.getField("RULE_ID");
			List SCH_CD 		= (List)jrecord.getField("SCH_CD");
			List RANKING1 		= (List)jrecord.getField("RANKING1");
			
			for(int ii =0; ii<RULE_ID.size();ii++){
				

				count+= new YdSlabMoveBayRankingDAO().updateSlabloadLotRanking(queryID,
																				 ""+RANKING1.get(ii),
																				 ""+RULE_ID.get(ii),
																				 ""+SCH_CD.get(ii)
																				 );

			}
			return count;
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
	public int updateSlabloadLotRankingSF(String queryID, JDTORecord jrecord) throws EJBServiceException ,DAOException{
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			int count = 0;
			List RULE_ID1 	= (List)jrecord.getField("RULE_ID1");
			List SCH_CD1 		= (List)jrecord.getField("SCH_CD1");
			List RANKING2 		= (List)jrecord.getField("RANKING2");
			
			for(int ii =0; ii<RULE_ID1.size();ii++){
				

				count+= new YdSlabMoveBayRankingDAO().updateSlabloadLotRankingSF(queryID,
																				 ""+RANKING2.get(ii),
																				 ""+RULE_ID1.get(ii),
																				 ""+SCH_CD1.get(ii)
																				 );

			}
			return count;
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
	public int updateSlabloadLotRankingWLOC(String queryID, JDTORecord jrecord) throws EJBServiceException ,DAOException{
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			int count = 0;
			List RULE_ID2 	= (List)jrecord.getField("RULE_ID2");
			List SCH_CD2 		= (List)jrecord.getField("SCH_CD2");
			List RANKING3 		= (List)jrecord.getField("RANKING3");
			
			for(int ii =0; ii<RULE_ID2.size();ii++){
				

				count+= new YdSlabMoveBayRankingDAO().updateSlabloadLotRankingWLOC(queryID,
																				 ""+RANKING3.get(ii),
																				 ""+RULE_ID2.get(ii),
																				 ""+SCH_CD2.get(ii)
																				 );

			}
			return count;
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////
	/////////////////////B열연수정시작///////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////
 	/**
	 * 오퍼레이션명 : 
	 *
	 * 조업시스템으로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * 1.TC_CD	: POYM005(전단실적)
        * 2.I/F ID	: YM-LIF-021
        * A열연 SLAB 추가 getProcessID = 6(전단실적)
        * 				 getProcessID = 7(전단취소)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */      	
    public boolean receivePOYM005(JDTORecord rcvMsg){
//    	public boolean sySlabInfoInsert2(POYM005 model) {
        logger.println(LogLevel.DEBUG, this, "TODO: Slab 재료정보 등록 처리");
        logger.println(LogLevel.DEBUG, this, "BCast전단실적처리 일관제철 모듈 호출");
        logger.println(LogLevel.DEBUG, this, "SlabInfoRegSBean:receivePOYM005(POYM005)");
        try {    
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			

//			String pslabNo			= StringHelper.evl(rcvMsg.getFieldString("slabNo"),"").trim();
//			String pProcessID		= StringHelper.evl(rcvMsg.getFieldString("ProcessId"),"").trim();
//			String pyardID   		= StringHelper.evl(rcvMsg.getFieldString("yardID"),"").trim();          
            
			String pslabNo			= StringHelper.evl(rcvMsg.getFieldString("SlabNo"),"").trim();
			String pProcessID		= StringHelper.evl(rcvMsg.getFieldString("ProcessID"),"").trim();
			String pyardID   		= StringHelper.evl(rcvMsg.getFieldString("yardID"),"").trim();          
            
			logger.println(LogLevel.DEBUG,this," SlabNo 	=" + pslabNo);
			logger.println(LogLevel.DEBUG,this," ProcessID 	=" + pProcessID);
			logger.println(LogLevel.DEBUG,this," yardID 	=" + pyardID);

        	/**
             * 수신항목의 'Slab No'를 체크.
             */
//S            if(! model.validSlabNo()) {
//S                ModelWarning.getInstance().setWarning(model, "슬라브번호의 길이가 11보다 작아야 합니다.");
//S                return false;
//S            }
			if(pslabNo.length() > 11) {
              logger.println(LogLevel.DEBUG, this, "슬라브번호의 길이가 11보다 작아야 합니다.");
              return false;
			}
            /**
             * 수신항목의 'Slab No'로 공정시스템의 PM_SLAB공통 Table을 READ 한다.
             */
            
            
//S            JDTORecord slabInfo	= ymCommonDAO.readSlabInfo(model.getSlabNo());
            JDTORecord slabInfo	= ymCommonDAO.readSlabInfo(pslabNo);
                      
            String slabNo			= getField(slabInfo, "SLAB_NO");
            String s_STL_APPEAR_GP	= getField(slabInfo, "STL_APPEAR_GP");
            String s_SLAB_T			= getField(slabInfo, "SLAB_T");
            String s_SLAB_W			= getField(slabInfo, "SLAB_W");
            String s_SLAB_LEN		= getField(slabInfo, "SLAB_LEN");
            String s_SLAB_WT		= getField(slabInfo, "SLAB_WT");
            String s_CURR_PROG_CD	= getField(slabInfo, "CURR_PROG_CD");
            String s_ORD_YEOJAE_GP	= getField(slabInfo, "ORD_YEOJAE_GP");
            String s_ORD_NO			= getField(slabInfo, "ORD_NO");
            String s_ORD_DTL		= getField(slabInfo, "ORD_DTL");
            String s_SLAB_WO_RT_CD	= getField(slabInfo, "SLAB_WO_RT_CD");
            String s_ORD_HCR_GP		= getField(slabInfo, "ORD_HCR_GP");
            String s_HCR_GP			= getField(slabInfo, "HCR_GP");
            String s_SCARFING_YN	= getField(slabInfo, "SCARFING_YN");
            
//S            if("7".equals(model.getProcessID())) {
            if("7".equals(pProcessID)) {
                ymCommonDAO.modifyStackStateOfLayer(slabNo);
                
                YdStockDAO dao	= new YdStockDAO();
                int iSeq = dao.updateStockDelYnInfo(slabNo,"Y");
                
                //2007-04-02 A열연 SLAB 야드 전단 실적 취소 추가  (MCH)
//S                if(!"0".equals(model.getyardID())){
//S                	sendSlabInfo(slabInfo, model.getSlabNo(), "0");
//S                }
                if(!"0".equals(pyardID)){
                	sendSlabInfo(slabInfo, pslabNo, "0");
                }

                return true;
            }
           else {
	           JDTORecord tcRecord1 = JDTORecordFactory.getInstance().create();
	         
			   tcRecord1.setField("JMS_TC_CD", "CSYDJ001");
			   tcRecord1.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
			   tcRecord1.setField("MOD_GP","I");
			   tcRecord1.setField("STL_APPEAR_GP", StringHelper.evl(s_STL_APPEAR_GP, ""));	
			   tcRecord1.setField("STL_NO", StringHelper.evl(slabNo, ""));
			   tcRecord1.setField("YD_STL_W"        ,StringHelper.evl(s_SLAB_W, ""));
			   tcRecord1.setField("YD_STL_L"        , StringHelper.evl(s_SLAB_LEN, ""));
			   tcRecord1.setField("YD_STL_T", StringHelper.evl(s_SLAB_T, ""));
			   tcRecord1.setField("YD_STL_WT", StringHelper.evl(s_SLAB_WT, ""));
			   tcRecord1.setField("CURR_PROG_CD",StringHelper.evl(s_CURR_PROG_CD, ""));
			   tcRecord1.setField("ORD_YEOJAE_GP",StringHelper.evl(s_ORD_YEOJAE_GP, ""));
			   tcRecord1.setField("ORD_NO",StringHelper.evl(s_ORD_NO, ""));
			   tcRecord1.setField("ORD_DTL",StringHelper.evl(s_ORD_DTL, ""));
			   tcRecord1.setField("SLAB_WO_RT_CD",StringHelper.evl(s_SLAB_WO_RT_CD, ""));
			   tcRecord1.setField("ORD_HCR_GP",StringHelper.evl(s_ORD_HCR_GP, ""));
			   tcRecord1.setField("HCR_GP",StringHelper.evl(s_HCR_GP, ""));
			   tcRecord1.setField("CC_MC_CD","1");
			   tcRecord1.setField("SCARFING_YN",StringHelper.evl(s_SCARFING_YN, ""));
			   tcRecord1.setField("ARR_WLOC_CD","");
			   tcRecord1.setField("MultiSend","Y");
			   
			   logger.println(LogLevel.DEBUG, this, "BCast전단실적처리 일관제철 모듈 호출");
				EJBConnector ejbCon = new EJBConnector("default", "StockSpecRegFaEJB", this);
				ejbCon.trx("rcvCcFsWr", new Class[]{ JDTORecord.class }, new Object[]{ tcRecord1 });
				return true;	
           }
      
        }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
    }   
    
 	/**
	 * 오퍼레이션명 : 
	 *
	 * 관제L-3의 요청에 의해 SLAB 결번실적을 처리한다.
	 * -관제 -> 야드 (미처리,반송 Slab 결번 시점)
        * -TC_CD	: POYM006
        * -I/F ID	: YM-LIF-022
        * 1	전문코드	TC			CHAR	07		
        * 2	발생일자	Date		CHAR	10		YYYY-MM-DD
        * 3	발생시간	Time		CHAR	08		HH-MM-SS
        * 4	YARD 구분	YD_GP	CHAR	1		1:A열연Coil,2:B열연Slab,3:B열연Coil
        * 5	Slab No	SlabNo		CHAR	11		
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */      
    public boolean receivePOYM006(JDTORecord rcvMsg){
//        public boolean slabMissingResult(PCYM003 model) {
    	
        logger.println(LogLevel.DEBUG, this, "SlabInfoRegSBean:receivePOYM006(POYM006)");
        logger.println(LogLevel.DEBUG, this, "TODO: SLAB 결번실적 처리");

        String pslabNo			= StringHelper.evl(rcvMsg.getFieldString("SlabNo"),"").trim();
        
		logger.println(LogLevel.DEBUG,this," SlabNo 	=" + pslabNo);
        
        try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
//S            if(model.getSlabNo().length() > 11) { 
//S                ModelWarning.getInstance().setWarning(model, "슬라브번호의 길이가 11보다 작아야 합니다.");
//S                return false;
//S            }
			if(pslabNo.length() > 11) {
	              logger.println(LogLevel.DEBUG, this, "슬라브번호의 길이가 11보다 작아야 합니다.");
	              return false;
			}
        	
            ymCommonDAO.modifyMoveTermOfStock(
//S                    YmCommonUtil.getSlabCurrProgCd(model.getSlabNo(),"")[1], 
//S                    model.getSlabNo());
            YmCommonUtil.getSlabCurrProgCd(pslabNo,"")[1], 
            pslabNo);
            
            //야드 L2 전송 장입순번 Clear CALL
//S   	     boolean isTrue = callL2LotEndInfo_Slab(model.getSlabNo());	
	     boolean isTrue = callL2LotEndInfo_Slab(pslabNo);	
					            
        }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
        return true;
    }   	 
}
