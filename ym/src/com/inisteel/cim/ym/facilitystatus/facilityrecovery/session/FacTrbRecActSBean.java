package com.inisteel.cim.ym.facilitystatus.facilityrecovery.session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.log.LogService;
import jspeed.base.log.LogServiceConfig;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTORecord;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.util.CommonUtil;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.common.parser.Level2Parser;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdEquipDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdEquipDownDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO;
/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="FacTrbRecActEJB" jndi-name="JNDIFacTrbRecAct" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class FacTrbRecActSBean extends BaseSessionBean {
    private Logger logger 			= null;
    private ymCommonDAO ymCommonDAO = null;
    private CraneSchDAO dao 		= null;
	private YmComm ymComm 			= new YmComm();
	
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
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public List selectFicilityTrobleList(String yardgubun, String ficilitygubun, String fromdate, String todate){
		YdEquipDAO yardFicilityDAO = null;	    
	    List facilityList = null;
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}

	    	yardFicilityDAO = new YdEquipDAO();
	    	facilityList = yardFicilityDAO.getListData("ym.facilitystatus.facilityinquiry.dao.YdEquipDownDAO.readFacilityInfo",new Object[]{yardgubun, ficilitygubun, todate, fromdate});
	    	return facilityList;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}
		
      /**
	 * 오퍼레이션명 : 
	 *
	 * 야드 Level-2로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * 1.TC_CD	: THCH540.
        * 2.I/F ID	: YM-AIF-005.
        * 
        * 전문코드		CHAR	07		
        * CRANE 번호	CHAR	04		
        * SPARE		CHAR	04		
        * 발생일			CHAR	06		YYMMDD
        * 발생시			CHAR	06		HHMMSS
        * 고장/복구 구분	CHAR	01		0:복구, 1:고장
        * 고장코드		CHAR	04		
        * SPARE		CHAR	118		
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
	public boolean acyCRTroRecResult(String sMessage) {
        logger.println(LogLevel.DEBUG, this, "ACY_CRANE 고장/복구 실적");        
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
            Map tc = ymCommonDAO.readColumnLenOfTc(getField(parseData, "전문코드"));            
            /**
             * valid check
             */
            validRecDataAOfCRTroRec(parseData, tc);
			/**
			 * AS-IS 크레인 번호를 TO-BE 크레인 번호로 변환하여 설비정보를 가져온다.
			 * 1. AS-IS 크레인번호: ACR1
			 * 2. TO-BE 크레인번호: 1ACR72
			 */
			JDTORecord equip = 
			    ymCommonDAO.readEquipInfo(YmCommonConst.YD_GP_1, getField(parseData, "CRANE번호"));
			if(equip == null || equip.size() == 0) {
			    throw new Exception("설비테이블에 해당 설비가 존재하지 않습니다.");
			}			
			/**
			 * 수신항목의 '고장/복구구분'의 값이 '1'일경우 '고장', '0'일경우 '복구' 처리한다.
			 */
			doATroRec(parseData, equip);			
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return true;
	}

	/**
	 * 오퍼레이션명 : 
	 */
    private void doATroRec(JDTORecord parseData, JDTORecord equip) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        List base 		= null;
        String yd		= getField(equip, "YD_GP");
        String bay		= getField(equip, "BAY_GP");
        String equipGp	= getField(equip, "EQUIP_GP");
        String equipNo	= getField(equip, "EQUIP_NO");
        String alEquipNo= getField(equip, "BACKUP_EQUIP_NO");
        String alEquipGp= yd + bay +"CR"+ alEquipNo;
        if(isTroble(parseData, equip)) {
            logger.println(LogLevel.DEBUG, this, "크레인 설비 고장 처리.");
		    doTroOfEquip(getField(equip, "WPROG_STAT"), getField(parseData, "고장코드"), equipGp);
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
		}else if(isRecovery(parseData, equip)) {
            logger.println(LogLevel.DEBUG, this, "크레인 설비 복구 처리.");
		    doRecOfEquip(YmCommonConst.WPROG_STAT_W, YmCommonConst.DOWN_CD_0000, equipGp);
		    JDTORecord dto	= ymCommonDAO.readEquipInfo(alEquipGp);
		    String alStat 	= null;
		    if(dto != null) {
		        alStat = getField(dto, "EQUIP_STAT");    
		    }		    
	        if("".equals(alEquipNo)) {
			    base = ymCommonDAO.readTroRecSch(
			            yd, bay, equipNo, YmCommonConst.SCH_RULE_STAT_X);
		        editSchRule(yd, bay, equipNo, equipNo,
		                YmCommonConst.SCH_RULE_STAT_X,
		                YmCommonConst.SCH_RULE_ACTIVE_STAT_A);	            
	        }else {	            
	            if(YmCommonConst.EQUIP_STAT_C.equals(alStat)) {
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
	         * 복구 크레인의 초기 크레인 정보를 송신한다. 
	         */
	        considerSendCRIniInfo(yd, equipGp);
	        /**
	         * 대체 크레인의 설비 작업상태를 IDLE로 하고 크레인 작업지시를 CALL
	         * --대체가 없는 경우 SKIP
	         */
	        if((! "".equals(alEquipNo) && (! YmCommonConst.EQUIP_STAT_C.equals(alStat)))) {
	            callCRWorkOrder(yd, bay, alEquipNo);
	        }
		    /**
		     * 복구일 경우 복구 크레인의 설비 작업상태를 IDLE로 하고 크레인 작업지시를 CALL
		     */
		    callCRWorkOrder(yd, bay, equipNo);
		}else {
		    logger.println(LogLevel.DEBUG, this, "수신항목의 '고장/복구구분' Error");
		    /**
		     * 고장일경우 초기정보 송신, 정상이면 SKIP
		     */		    
		    if(YmCommonConst.EQUIP_STAT_C.equals(getField(equip, "EQUIP_STAT"))) {
		        considerSendCRIniInfo(yd, equipGp);
		    }
		}    
    }

    /**
	 * 수신항목 'CRANE번호', '고장/복구구분'을 체크한다.
     * @param parseData	수신정보
     * @return
     */
    private void validRecDataAOfCRTroRec(JDTORecord parseData, Map tc) throws Exception {
        logger.println(LogLevel.DEBUG, this, "수신항목 'CRANE번호', '고장/복구구분' 체크");
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        String craneNo 	= getFieldNvl(parseData, "CRANE번호");
        String troRecGp = getFieldNvl(parseData, "고장복구구분");
        if(craneNo.length() != getFieldLen(tc, "CRANE번호")) {
            throw new Exception("수신항목 'CRANE번호' Error: "+ craneNo);
        }else if(troRecGp.length() != getFieldLen(tc, "고장복구구분")) {
            throw new Exception("수신항목 '고장/복구구분' Error: "+ troRecGp);
        }        
    }

	/**
	 * 오퍼레이션명 : 
	 *
	 * 야드 Level-2로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * 1.TC_CD	: CM1PB04.//HC3PB53(A열연 SLAB야드 추가 (MCH))
        * 2.I/F ID	: YM-BIF-021.
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
        * 고장/복구 구분	Trouble_Recovery	CHAR	01		0:복구, 1:고장
        * 고장코드	Trouble_Code			CHAR	04			
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     	
	public boolean bsyCRTroRecResult(String sMessage) {
	    try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
	        JDTORecord parseData = new Level2Parser().parse(sMessage);
	        String msg = "";
	        if(YmCommonConst.TC_HC3PB53.equals(parseData.getFieldString("전문코드"))){
	        	msg = "ASY_ROT";
	        }else if(YmCommonConst.YD_GP_0.equals(parseData.getFieldString("야드구분"))){
	        	msg = "ASY_CRANE";
	        }else{
	        	msg = "BSY_CRANE";
	        }
	        logger.println(LogLevel.DEBUG, this, "TODO: "+ msg +" 고장/복구 실적");
	        logger.println(LogLevel.DEBUG, this, parseData);
	        crTroRecResult(parseData);
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return true;
	}

    /**
     * 설비가 크레인인지 리턴한다.
     * @param parseData
     * @return
     */
    private boolean notCREquip(JDTORecord parseData) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
        if(YmCommonConst.EQUIP_KIND_CR.equals(getField(parseData, "설비종류"))) {
            return false;
        }
        return true;
    }

    /**
     * 설비구분을 리턴한다.
     * @param dto
     * @return
     */
    private String getEtcEquipGp(JDTORecord dto) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        if(YmCommonConst.EQUIP_KIND_TC.equals(getField(dto, "설비종류"))) {
            return getField(dto, "야드구분") +"XTC"+ getField(dto, "설비번호");
        }
        return "";
    }

	/**
	 * 오퍼레이션명 : 
	 *
	 * 야드 Level-2로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * 1.TC_CD	: CN1PB04.
        * 2.I/F ID	: YM-BIF-004.
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     	     
	public boolean bcyCRTroRecResult(String sMessage) {
        logger.println(LogLevel.DEBUG, this, " TODO: BCY_CRANE 고장/복구 실적");        
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
	        crTroRecResult(parseData);
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return true;
	}
	
    /**
     * @param message
     */
    private void crTroRecResult(JDTORecord parseData) throws Exception {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return ;
		}
		
        Map tc = ymCommonDAO.readColumnLenOfTc(getField(parseData, "전문코드"));
        /**
         * valid check
         */
        validRecDataBOfCRTroRec(parseData, tc);
        /**
         * 설비가 대차이면 대차의 상태만 UPDATE 하고 리턴 한다.
         */
        String equipKind= getField(parseData, "설비종류");
        String troRecGp = getField(parseData, "고장복구구분");
        String equipStat = "O";
        if("1".equals(troRecGp)) {
            equipStat = "C";
        }
        if(notCREquip(equipKind)) {
            logger.println(LogLevel.DEBUG, this, "설비가 크레인이 아님으로 상태만 UPDATE 처리.");
            //A열연 ROT 고장,복구는 X인 경우는 X로 함.(MCH)
            if(YmCommonConst.TC_HC3PB53.equals(parseData.getFieldString("전문코드"))){
            	ymCommonDAO.modifyEquipStatOfRotEquip(equipStat, getEquipKey(parseData));
            }else{
            	ymCommonDAO.modifyEquipStatOfEquip(equipStat, getEquipKey(parseData));
            	
            	/*
            	 *	B열연 Slab 야드 STE 설비 고장시 크레인 비상스케쥴 작업지시 호출
            	 */
            	if("2AST01".equals(getEquipKey(parseData))&&"C".equals(equipStat)){
        		
        		EJBConnector ejbConn = new EJBConnector("default","JNDISRTSpplyWrkOrdReg",this);
			Boolean isTrue = (Boolean)ejbConn.trx("callCraneCtc1Info",new  Class[]{String.class},
															new Object[]{"STE1 고장에 따른 크레인 작업지시 호출"});
		}else if("2CST03".equals(getEquipKey(parseData))&&"C".equals(equipStat)){
			EJBConnector ejbConn = new EJBConnector("default","JNDISRTSpplyWrkOrdReg",this);
			Boolean isTrue = (Boolean)ejbConn.trx("callCraneCtc4Info",new  Class[]{String.class},
															new Object[]{"STE3 고장에 따른 크레인 작업지시 호출"});
		}
            	
            }
            return;
        }
		/**
		 * '설비구분'을 조건으로 설비 테이블을 조회한다.
		 */
		JDTORecord equip = ymCommonDAO.readEquipInfo(getEquipKey(parseData));	
		/**
		 * 수신항목의 '고장/복구구분'의 값이 '1'일경우 '고장', '0'일경우 '복구' 처리한다.
		 */
		doTroRec(parseData, equip);			
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
        }else if("BK".equals(equipKind)) {
            return true;        
        }else if("ST".equals(equipKind)) {
            return true;            
         }else if("CT".equals(equipKind)) {
            return true;                
        }else if("WT".equals(equipKind)) {
            return true;
        }else if("RT".equals(equipKind)) {
            return true;    
        }
        return false;
    }

    /**
     * 수신항목 '야드구분', '동구분', '설비종류', '설비번호'를 조합하여 설비구분을 리턴한다.
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
		
        String ydGp 		= getField(parseData, "야드구분");
        String bayGp 		= getField(parseData, "동구분");
        String equipKind 	= getField(parseData, "설비종류");
        String equipNo 		= getField(parseData, "설비번호");
        StringBuffer buffer = new StringBuffer();
        buffer.append(ydGp);
        if("TC".equals(equipKind) 
        	|| "SC".equals(equipKind)) {
            buffer.append("X");    
        }else {
            buffer.append(bayGp);
        }
        buffer.append(equipKind);
        buffer.append(equipNo);
        return buffer.toString();
	}	

    /**
     * 수신항목 '야드구분', '동구분', '설비종류', '설비번호', '고장복구구분', '고장코드'를 체크한다.
     * @param parseData	수신정보
     * @return
     */
    private void validRecDataBOfCRTroRec(JDTORecord parseData, Map tc) throws Exception {
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
        String troRecGp = getFieldNvl(parseData, "고장복구구분");
        String troCode 	= getFieldNvl(parseData, "고장코드");
        if(ydGp.length() != getFieldLen(tc, "야드구분")) {
            throw new Exception("수신항목 '야드구분' Error: "+ ydGp);
        }else if(bayGp.length() != getFieldLen(tc, "동구분")) {
            throw new Exception("수신항목 '동구분' Error: "+ bayGp);
        }else if(equipKind.length() != getFieldLen(tc, "설비종류")) {
            throw new Exception("수신항목 '설비종류' Error: "+ equipKind);
        }else if(equipNo.length() != getFieldLen(tc, "설비번호")) {
            throw new Exception("수신항목 '설비번호' Error: "+ equipNo);
        }else if(troRecGp.length() != getFieldLen(tc, "고장복구구분")) {
            throw new Exception("수신항목 '고장/복구구분' Error: "+ troRecGp);
        }else if(troCode.length() != getFieldLen(tc, "고장코드")) {
            throw new Exception("수신항목 '고장코드' Error: "+ troCode);
        }        
    }

    /**
     * 크레인 고장 또는 복구 처리
     * -고장일경우 기준을 대체로 할당하고 크레인의 작업상태가 IDLE일 경우에만 크레인 작업지시를 CALL 한다.
     * -복구일경우 대체를 기준으로 할당하고 크레인의 작업상태를 IDLE로 셋팅하고 크레인 작업지시를 CALL 한다.
     * @param parseData	수신정보
     * @param equip		크레인정보
     * @param reCrane	AS-IS 
     */
    private void doTroRec(JDTORecord parseData, JDTORecord equip) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        List base 		= null;
        List alter 		= null;
        List Arot 		= new ArrayList();
        String Rotequip = "";
        String yd		= getField(equip, "YD_GP");
        String bay		= getField(equip, "BAY_GP");
        String equipGp	= getField(equip, "EQUIP_GP");
        String equipNo	= getField(equip, "EQUIP_NO");
        String alEquipNo= getField(equip, "BACKUP_EQUIP_NO");
        String alEquipGp= yd + bay +"CR"+ alEquipNo;
	    String workMode = getField(equip, "WORK_MODE");
	    String hmiMode 	= getField(equip, "HMI_STAT");
	    JDTORecord dto	= ymCommonDAO.readEquipInfo(alEquipGp);
	    String alStat 		= null;
	    String alWorkMode	= null;
	    String alHMIMode	= null;
	    if(dto != null) {
	        alStat 		= getField(dto, "EQUIP_STAT");    
	        alWorkMode	= getField(dto, "WORK_MODE");
	        alHMIMode	= getField(dto, "HMI_STAT");
	    }
        if(isTroble(parseData, equip)) {		    
            logger.println(LogLevel.DEBUG, this, "크레인 설비 고장 처리.야드구분:"+yd+"    동:"+bay);
		    doTroOfEquip(getField(equip, "WPROG_STAT"), getField(parseData, "고장코드"), equipGp);
		    //A열연 SLAB야드 크레인 고장이면 해당동 ROT설비 상태를 사용금지로 셋팅한다.(MCH)
		    if(YmCommonConst.YD_GP_0.equals(yd)){
		    	if(YmCommonConst.BAY_GP_A.equals(bay)){
		    		Rotequip = yd + bay + YmCommonConst.EQUIP_KIND_0_A_RT;
		    	}else{
		    		Rotequip = yd + bay + YmCommonConst.EQUIP_KIND_0_B_RT;
		    	}
		    	
		    	Arot.add(YmCommonConst.EQUIP_STAT_X);
		    	Arot.add(Rotequip);		    	
		    	logger.println(LogLevel.DEBUG, this, "A열연 SLAB야드 크레인 고장신 같은동 ROT사용 금지"+Rotequip);
				/*
				UPDATE  TB_YM_EQUIP A
				   SET  A.EQUIP_STAT = DECODE(A.EQUIP_STAT, 'C', A.EQUIP_STAT, ?),  --설비 상태  
						A.MODIFIER   = 'SYSTEM',
						A.MOD_DDTT   = SYSDATE
				WHERE   A.EQUIP_GP        = ?
				*/		    	
		    	new YdEquipDAO().updateData("ym.common.dao.updateEquipStatAndDownCdOfRotEquip",Arot);
		    }
		    /**
		     * 크레인의 고장/복구,운전모드,system모드가 정상일 경우만 처리
		     */
		    if("O".equals(workMode)) {
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
			        editTroRecOfSch(alter, alEquipNo, 1);
		        }		        
		    }
	    	
	    	/**
	         * 고장 크레인의 초기 크레인 정보를 송신한다. 
	         */
	        considerSendCRIniInfo(yd, equipGp);
	    	
		}else if(isRecovery(parseData, equip)) {
            logger.println(LogLevel.DEBUG, this, "크레인 설비 복구 처리.");
		    //A열연 Slab 크레인 복구시에 해당 동 ROT설비의 설비상태가 사용금지(X)이면 정상으로 셋팅한다.(MCH)
            if(YmCommonConst.YD_GP_0.equals(yd)){
		    	if(YmCommonConst.BAY_GP_A.equals(bay)){
		    		Rotequip = yd + bay + YmCommonConst.EQUIP_KIND_0_A_RT;
		    	}else{
		    		Rotequip = yd + bay + YmCommonConst.EQUIP_KIND_0_B_RT;
		    	}
		    	
		    	Arot.add(YmCommonConst.EQUIP_STAT_O);
		    	Arot.add(Rotequip);		    	
				/*
				UPDATE  TB_YM_EQUIP A
				   SET  A.EQUIP_STAT = DECODE(A.EQUIP_STAT, 'C', A.EQUIP_STAT, ?),  --설비 상태  
						A.MODIFIER   = 'SYSTEM',
						A.MOD_DDTT   = SYSDATE
				WHERE   A.EQUIP_GP        = ?
				*/		    	
		    	new YdEquipDAO().updateData("ym.common.dao.updateEquipStatAndDownCdOfRotEquip",Arot);
		    }
		    
		    doRecOfEquip(YmCommonConst.WPROG_STAT_W, YmCommonConst.DOWN_CD_0000, equipGp);		    
		    if("O".equals(workMode)) {
		        if("".equals(alEquipNo)) {
				    base = ymCommonDAO.readTroRecSch(
				            yd, bay, equipNo, YmCommonConst.SCH_RULE_STAT_X);
			        editSchRule(yd, bay, equipNo, equipNo,
			                YmCommonConst.SCH_RULE_STAT_X,
			                YmCommonConst.SCH_RULE_ACTIVE_STAT_A);	            
		        }else {	            
		            if(YmCommonConst.EQUIP_STAT_C.equals(alStat)) {
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
			     *	YJK.20066020
			     *	B열연 COIL 추가 수정.
			     */
		        if(YmCommonConst.YD_GP_3.equals(yd)) {
		        	
		        	/**
			         * 대체 크레인의 설비 작업상태를 IDLE로 하고 크레인 작업지시를 CALL
			         * --대체가 없는 경우 SKIP
			         */
			        if((! "".equals(alEquipNo) && (! YmCommonConst.EQUIP_STAT_C.equals(alStat)))) {
			            callCRWorkOrder(yd, bay, alEquipNo);
			        }
				    /**
				     * 복구일 경우 복구 크레인의 설비 작업상태를 IDLE로 하고 크레인 작업지시를 CALL
				     */
				    callCRWorkOrder(yd, bay, equipNo);
				    
		        }else{
		    		
		    		/**
				     * 복구일 경우 복구 크레인의 설비 작업상태를 IDLE로 하고 크레인 작업지시를 CALL
				     */
				    callCRWorkOrder(yd, bay, equipNo);
		    	}
			    
		    }else {
		        /**
		         * 고장 크레인의 초기 크레인 정보를 송신한다. 
		         */
		        considerSendCRIniInfo(yd, equipGp);
		    }
		}else {
		    logger.println(LogLevel.DEBUG, this, "수신항목의 '고장/복구구분' Error");
		}
        //A열연 SLAB야드 일경우만 전송(MCH)
        if(YmCommonConst.YD_GP_0.equals(yd)){
	        try{
	        	logger.println(LogLevel.DEBUG, this, "A열연 SLAB야드 크레인 고장/복구시 연주로 전문 송신");
		    	//2007-04-25 A열연 SLAB야드는 크레인 고장,복구시 연주로 전문 송신(MCH)
		    	StringBuffer sendMsg = new StringBuffer();
		    	String tcName = YmCommonConst.TC_HC3BP52;
		    	Map tc = ymCommonDAO.readColumnLenOfTc(tcName);
		    	sendMsg.append(tcName);
		    	sendMsg.append(YmCommonUtil.getStringYMD("-"));
		        sendMsg.append(YmCommonUtil.getStringHMS("-"));
		        sendMsg.append(YmCommonConst.FORM_I);
		        appendMsgNum(sendMsg, ""+ (YmCommonUtil.getTotalLenOfTc(tc) - 30), getFieldLen(tc, "전문길이"));		        
		        appendMsgNum(sendMsg, getField(parseData, "동구분"), getFieldLen(tc, "CRANE번호"));
		        appendMsgNum(sendMsg, getField(parseData, "고장복구구분"), getFieldLen(tc, "고장구분"));
		        
		        EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
		        ejbConn.trx("send"+ tcName, new Class[]{ String.class }, new Object[]{ sendMsg.toString() });		            
		        sendMsg.delete(31, sendMsg.length());
		        //2007-04-25 A열연 SLAB야드는 크레인 고장,복구시 연주로 전문 송신(MCH)
	        }catch(Exception e) {
	            throw new EJBServiceException(e);
	        }
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
        //A열연 SLAB야드 추가(MCH)
        }else if(YmCommonConst.YD_GP_0.equals(yd) && YmCommonConst.BAY_GP_A.equals(equipGp.substring(1, 2))) {
        	callCRIniInfo(
                    ymCommonDAO.readColumnLenOfTc(YmCommonConst.TC_HM1BP01),
                    YmCommonConst.TC_HM1BP01,
                    equipGp);
        }else if(YmCommonConst.YD_GP_0.equals(yd)&& YmCommonConst.BAY_GP_B.equals(equipGp.substring(1, 2))) {
        	callCRIniInfo(
                    ymCommonDAO.readColumnLenOfTc(YmCommonConst.TC_HM1BP51),
                    YmCommonConst.TC_HM1BP51,
                    equipGp);
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
			return;
		}
		
	    equipGp 			= equipGp.substring(0, 4) + alEquipNo;
	    JDTORecord equip 	= ymCommonDAO.readEquipInfo(equipGp);		    
	    if(YmCommonConst.WPROG_STAT_W.equals(getField(equip, "WPROG_STAT"))) {
		    callCRWorkOrder(yd, bay, alEquipNo);
	    }
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
	        callCRWorkCancel(base, baseCnt);	            
        }
    }

    /**
     * 고장 크레인 작업을 취소한다.
     * @param schs		스케쥴정보
     * @param schsCnt	스케쥴정보 개수
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
            e.printStackTrace();
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
			ejbConn = new EJBConnector("default","JNDICraneStatusReg",this);
            ejbConn.trx("acyCRIniReq",new Class[]{String.class},new Object[]{sendMsg.toString()});			    
            
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
			return ;
		}
		
        JDTORecord dto 	= null;
        List lst 		= ymCommonDAO.readSchInfo(yd, bay, equipNo);
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
                considerSendCRIniInfo(yd, (yd+bay+"CR"+equipNo));
            }
        }
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
        }
        return "";
    }

    /**
     * 고장인 설비에 대해서 설비 테이블 UPDATE, 설비휴지 테이블 INSERT 한다.
     * @param equipStat	설비상태
     * @param troCd		고장코드
     * @param equipGp	설비구분
     */
    private void doTroOfEquip(String wproStat, String troCd, String equipGp) {
        logger.println(LogLevel.DEBUG, this, "### 크레인 설비 고장 처리.");
        
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        if(YmCommonConst.YD_GP_1.equals(equipGp.substring(0, 1))) {
            ymCommonDAO.modifyEquipStatAndDownCdOfEquip(
                    YmCommonConst.EQUIP_STAT_C, YmCommonConst.MODE_C, wproStat, troCd, equipGp);            
        }else {
            ymCommonDAO.modifyEquipStatAndDownCdOfEquip(
                    YmCommonConst.EQUIP_STAT_C, wproStat, troCd, equipGp);
        }
        
        List data = new ArrayList();
        //EQUIP_GP	VARCHAR2(6)	Not Null	설비 구분
        data.add(equipGp);
        
        //DOWN_OCCUR_SEQ	VARCHAR2(18)	Not Null	휴지 발생 순서
        
        //DOWN_PASS_HR_CARRYOVER	VARCHAR2(1)		휴지 경과 시간 이월
        data.add("");
        
        //DOWN_CD	VARCHAR2(4)		휴지 CODE
        data.add(troCd);

        //DOWN_OCCUR_DDTT	VARCHAR2(12)		휴지 발생 일시
        
        //DOWN_OCCUR_WORK_DUTY
        data.add(YmCommonUtil.getWorkDuty());
        
        //DOWN_OCCUR_WORK_PARTY	VARCHAR2(2)		휴지 발생 작업 조
        data.add(YmCommonUtil.getWorkParty());

        //DOWN_END_DDTT	VARCHAR2(12)			휴지 종료 일시
        data.add("");
        
        //DOWN_END_WORK_PARTY	VARCHAR2(2)		휴지 종료 작업 조
        data.add("");
        
        //DOWN_PASS_HR	VARCHAR2(6)		휴지 경과 시간
        data.add("");
        
        //DOWN_RECOVER_CONTENTS	VARCHAR2(1000)		휴지 복구 내용
        data.add("");
        
        //REGISTER	VARCHAR2(10)		등록자

        //REG_DDTT	DATE				등록 일시
        
        //MODIFIER	VARCHAR2(10)		수정자

        //MOD_DDTT	DATE				수정 일시

        //DEL_YN	VARCHAR2(1)		삭제 유무        
        ymCommonDAO.createEquipDown(data);
    }

    /**
     * 스케쥴기준 정보를 백업 처리 한다.
     * @param schs	고장 크레인 스케쥴기준 정보
     * @param equipNo	설비번호
     */
    private void editSchRule(
            String yd, String bay, String crNo, String alCrNo, String stat, String alStat) {
    	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        List base 		= ymCommonDAO.readTroRecSchRule(yd, bay, crNo, stat);
        int baseCnt 	= base != null ? base.size() : 0;
        for(int i = 0; i < baseCnt; i++) {
            ymCommonDAO.modifyAlterNoAndActiveStatOfSchRule(
                    alCrNo, 
                    alStat, 
                    getField((JDTORecord)base.get(i), "SCH_RULE_ID"));
        }
    }

    /**
     * '고장/복구구분'이 '1'이고 설비 테이블의 '설비상태'가 'A'이면 true를 리턴한다.
     * @param parseData	수신정보
     * @param equip		설비정보
     * @return true, false
     */
    private boolean isTroble(JDTORecord parseData, JDTORecord equip) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		String troRecGp	= getField(parseData, "고장복구구분");
		String equipStat= getField(equip, "EQUIP_STAT");
        return YmCommonConst.TRO_REC_1.equals(troRecGp) && 
        		YmCommonConst.EQUIP_STAT_O.equals(equipStat);
    }
    
    /**
     * '고장/복구구분'이 '0'이고 설비 테이블의 '설비상태'가 'D'이면 true를 리턴한다.
     * @param parseData	수신정보
     * @param equip		설비정보
     * @return true, false
     */
    private boolean isRecovery(JDTORecord parseData, JDTORecord equip) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		String troRecGp	= getField(parseData, "고장복구구분");
		String equipStat= getField(equip, "EQUIP_STAT");
        return YmCommonConst.TRO_REC_0.equals(troRecGp) && 
        		YmCommonConst.EQUIP_STAT_C.equals(equipStat);
    }
	    
    /**
     * 고장/복구인 크레인 스케쥴 정보를 백업한다.
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
			return;
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
			return;
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
     * 크레인 복구 처리 한다.
     * @param equipStat	설비상태	
     * @param troCode	고장코드
     * @param equipGp	설비구분
     */
    private void doRecOfEquip(String wproStat, String troCode, String equipGp) {
    	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return ;
		}
		
        if(YmCommonConst.YD_GP_1.equals(equipGp.substring(0, 1))) {
            ymCommonDAO.modifyEquipStatAndDownCdOfEquip(
                    YmCommonConst.EQUIP_STAT_O, YmCommonConst.MODE_O, wproStat, troCode, equipGp);            
        }else {
            ymCommonDAO.modifyEquipStatAndDownCdOfEquip(
                    YmCommonConst.EQUIP_STAT_O, wproStat, troCode, equipGp);
        }
        ymCommonDAO.modifyDownCdOfEquipDown(
                YmCommonConst.DOWN_CD_0000, 
                YmCommonUtil.getWorkDuty(), 
                YmCommonUtil.getWorkParty(),
                equipGp);
    }
    
    /**
     * 고장 크레인의 스케쥴기준 정보 복구
     * @param base	복구할 크레인의 스케쥴기준 정보
     * @param equip	복구할 크레인 정보
     */
    private void doRecOfSchRule(List base, JDTORecord equip) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return ;
		}
		
        logger.println(LogLevel.DEBUG, this, "크레인 스케쥴기준 테이블 복구 처리.");
        int schRulesCnt = base != null ? base.size() : 0;
        for(int i = 0; i < schRulesCnt; i++) {
            ymCommonDAO.modifyActiveStatOfSchRule(
                    YmCommonConst.SCH_RULE_ACTIVE_STAT_A, 
                    getField((JDTORecord)base.get(i), "SCH_RULE_ID"));	
        }
    }

    /**
     * 복구할 크레인의 스케쥴 기준 정로를 가져온다.
     * @param equip		크레인 정보
     * @param alEquipNo	대체 크레인 번호
     * @param ativeStat	스케쥴 기준 활성 상태
     * @return
     */
    private List getRecSchRules(JDTORecord equip, String alEquipNo, String ativeStat) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        return ymCommonDAO.readRecSchRule(
                getField(equip, "YD_GP"), 
                getField(equip, "BAY_GP"), 
                alEquipNo, 
                ativeStat);
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
     * JDTORecord 가 가지는 name parameter에 대한 값을 리턴한다.
     * @param data
     * @param name
     * @return
     */
    private String getField(JDTORecord data, String name) {
        return StringHelper.evl(data.getFieldString(name), "").trim();
    }

    /**
     * JDTORecord 가 가지는 name parameter에 대한 값을 리턴한다.
     * @param data
     * @param name
     * @return
     */
    private String getFieldNvl(JDTORecord data, String name) {
        return StringHelper.nvl(data.getFieldString(name), "");
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
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
	public int insertFicilityTrobleList(String insertQueryId, String updateQueryId, List listData1, List listData2){
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return 0;
		}
		int res = 0;
		res = insertFicilityTroble(insertQueryId, listData1);
		res = updateFicilityTroble(updateQueryId, listData2);
		return res;
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
	public int insertFicilityTroble(String queryID, List listData) throws EJBServiceException ,DAOException{   		
		try {   	
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			YdEquipDownDAO ydequipdownDAO = new YdEquipDownDAO(); 
			return ydequipdownDAO.insertData(queryID, listData);
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
	public int updateFicilityTroble(String queryid, List listData) throws EJBServiceException ,DAOException {   	 
		try {		
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			YdEquipDAO ydequipDAO = new YdEquipDAO();
			return ydequipDAO.updateData(queryid, listData);
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
	public int updateFicilityTrobleList(String updateQueryId1, String updateQueryId2, List listData1, List listData2){
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return 0;
		}
		
		int res = 0;
		res = updateFicilityTroble(updateQueryId1, listData1);
		res = updateFicilityTroble(updateQueryId2, listData2);
		return res;
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
	public JDTORecord getFacilityInfo(String queryID, List listdata) throws EJBServiceException ,DAOException{	
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			YdEquipDAO ydequipDAO = new YdEquipDAO();
			return ydequipDAO.getData(queryID, listdata);
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
	public int deleteFicilityTrobleList(String queryid, List whereData) throws EJBServiceException ,DAOException {	
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			YdEquipDAO ydequipDAO = new YdEquipDAO();		
			return ydequipDAO.deleteData(queryid, whereData);		
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}


	
}

