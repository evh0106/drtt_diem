package com.inisteel.cim.ym.common.session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.log.LogLevel;
import jspeed.base.log.LogService;
import jspeed.base.log.LogServiceConfig;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.common.jms.model.CommonModel;
import com.inisteel.cim.common.jms.model.pc.ZZPC001;
import com.inisteel.cim.common.jms.model.pm.ZZPM001;
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
import com.inisteel.cim.common.jms.model.ym.POYM007;
import com.inisteel.cim.common.jms.model.ym.POYM008;
import com.inisteel.cim.common.jms.model.ym.POYM009;
import com.inisteel.cim.common.jms.model.ym.POYM010;	// spm2
import com.inisteel.cim.common.jms.model.ym.QMYM001;
import com.inisteel.cim.common.jms.model.ym.QMYM002;
import com.inisteel.cim.common.jms.model.ym.QMYM003;
import com.inisteel.cim.common.jms.model.ym.PSYM001;
import com.inisteel.cim.common.jms.model.ym.PSYM002;
import com.inisteel.cim.common.jms.model.ps.YMPS001;
import com.inisteel.cim.common.jms.model.ps.YMPS002;
import com.inisteel.cim.yd.common.dao.ydMsgInfoMgtDao.YdMsgInfoMgtDao;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;

// flex 화면(야드모니터링)의 표시를 위한 추가
import flex.messaging.MessageBroker;
import flex.messaging.messages.AsyncMessage;
import flex.messaging.util.UUIDUtils;


/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="YMLogEJB" jndi-name="JNDIYMLog" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class YMLogSBean extends BaseSessionBean {
    private Logger logger 			= null;
    private ymCommonDAO ymCommonDAO = null;
    
	public void ejbCreate() {
        LogServiceConfig config = 
            LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
        logger 		= new Logger(config);
        ymCommonDAO = new ymCommonDAO();
	}	

	/**
	 * 오퍼레이션명 : 
	 *
	 * 내부 인터페이스 전문에 대한 송수신 LOG 기록을 UPDATE
        * param type		TC 발생 유형[L:Log, W:Warning, E:Error]
        * param msg		TC 발생 유형 내용
        * param whrTcCd	TC CODE
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public void modifyLog(String type, String msg, String whrTcCd) {
	    try {	        
	        ymCommonDAO.modifyLog(type, 
	                msg.length() > 100 ? msg.substring(0, 100) : msg, 
	                whrTcCd);
	    }catch (Exception e) {
	        logger.println(LogLevel.DEBUG,this,"updateLog log error: ");
	        logger.println(LogLevel.DEBUG,this,"TC 발생 유형		: "+ type);
	        logger.println(LogLevel.DEBUG,this,"TC 발생 유형 내용	: "+ msg);
	        logger.println(LogLevel.DEBUG,this,"TC CODE			: "+ whrTcCd);
	        throw new EJBServiceException(e);
        }
	}

	/**
	 * 오퍼레이션명 : 
	 *
	 * 내부 인터페이스 전문에 대한 송수신 LOG 기록을 UPDATE
        * param reSendCnt		TC 재기동 횟수
        * param msg			TC 발생 유형 내용
        * param whrTcLogId	TC LOG ID
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public void modifyLogOfReSend(String reSendCnt, String msg, String whrTcLogId) {
	    try {	        
	        ymCommonDAO.modifyLogOfReSend(reSendCnt, msg, whrTcLogId);
	    }catch (Exception e) {
	        logger.println(LogLevel.DEBUG,this,"updateLog log of recend error: ");
	        logger.println(LogLevel.DEBUG,this,"TC 재기동 횟수		: "+ reSendCnt);
	        logger.println(LogLevel.DEBUG,this,"TC 발생 유형 내용	: "+ msg);
	        logger.println(LogLevel.DEBUG,this,"TC LOG ID		: "+ whrTcLogId);
	        throw new EJBServiceException(e);
        }
	}

	/**
	 * 오퍼레이션명 : 
	 *
	  * 야드 내부인터페이스 로그 생성
         * param model	내부 모델
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */      
	public void createLog(CommonModel model) {    
	    List log = null;
	    JDTORecord jtrLog = JDTORecordFactory.getInstance().create();
	    try {	        
	    	logger.println(LogLevel.DEBUG,this,"LOG YARD GP="+getYdGp(model));
	    	
	        Map itemLen = getItemLen(model.getTcCode());
	        log = new ArrayList();
	        log.add(getYdGp(model));
	        log.add(model.getTcCode());            
//	        log.add(model.getTcDate() +" "+ model.getTcTime());
	        String msg = null;
	        if(YmCommonConst.MODEL_YMPM001.equals(model.getTcCode())){
	            msg = getYMPM001Msg((ZZPM001)model, itemLen);
	        }else if("POPM020".equals(model.getTcCode())) {
	            msg = getPOPM020Msg((ZZPM001)model, itemLen);
	        }else if(YmCommonConst.MODEL_YMPM002.equals(model.getTcCode())) {
	        	msg = getYMPM002Msg((ZZPM001)model, itemLen);
	        }else {
	            msg = YmCommonUtil.getLogData(getNameValueOfModel(model.toString()), itemLen);
	        }
            log.add(msg.length() < 100 ? msg : msg.substring(0,100));
	        log.add("");
	        log.add(model.getTcCode());
	        ymCommonDAO.createLog(log);			// DB Insert
	        
	        /* *****************************************************************
	         * 동일한 메시지를 플렉스 화면에도 보여주기 위해서 
	         pushToFlexClient() method call.
	        */
	        jtrLog.setField("DESTI", "yd_monitor3");
	        jtrLog.setField("LOGMSG",msg.length() < 100 ? msg : msg.substring(0,100));
	        jtrLog.setField("TCCODE",model.getTcCode());
	        jtrLog.setField("YD_GP",getYdGp(model));
	        
	        //YmCommonUtil.putLogMsg(jtrLog);
	        YmCommonUtil.putLogMsg( msg.length() < 100 ? msg : msg.substring(0,100) );
	        
	        //this.putLogMsg(jtrLog);
//	        this.putLogMsg(String szYdGp, 
//	        		  String desti, 
//	        		  String szLogMsg, 
//	        		  String szYdBayGp, 
//	        		  String  szYdEqpId, 
//	        		  String szYdSchCd, 
//	        		  String szYdEvtGp, 
//	        		  String szYdMsgOutpwrGrd, 
//	        		  String szYdPgmTp, 
//	        		  String szYdIfCd, 
//	        		  String szEJBId, 
//	        		  String szMsgName);
	        /* *****************************************************************/
	    }catch (Exception e) {
	        logger.println(LogLevel.DEBUG,this,"insert log error list: "+ log);
	        logger.println(LogLevel.DEBUG,this,"insert log error model: "+ model);
	        throw new EJBServiceException(e);
        }	    
	}
	/**
	 * 발생한 로그를 야드모니터링 화면의 로그 메시지 창에 보이기 위해서 플렉스 채널로 데이터를 전송합니다.
	 * @param desti
	 * @param pushData
	 */
/*	
	public void pushToFlexClient(String desti, Object pushData)
	{
		try {
			logger.println(LogLevel.DEBUG,this, "pushToFlexClient start");
			MessageBroker msgBroker = MessageBroker.getMessageBroker(null);
			String clientID = UUIDUtils.createUUID(false);
			AsyncMessage msg = new AsyncMessage();
			
			msg.setDestination(desti);
			msg.setClientId(clientID);
			msg.setMessageId( UUIDUtils.createUUID(false));
			msg.setTimestamp(System.currentTimeMillis());
			
			msg.setBody(pushData);
			
			//msgBroker.routeMessageToService(msg, null);
		}catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			logger.println(LogLevel.DEBUG,this, "pushToFlexClient exception");
			//throw new DAOException( getClass().getName() + e.getMessage());
		}finally {
			
		}
	}
*/
	/**
     * @param tcCode
     * @return
     */
    private Map getItemLen(String tcCode) {
        if(isPCModel(tcCode)) {
            return ymCommonDAO.readColumnLenOfTc("ZZPC001", "ym.common.dao.selectInternalTc");
        }else {
            return ymCommonDAO.readColumnLenOfTc(tcCode, "ym.common.dao.selectInternalTc");
        }
    }

    
    /**
     * @param tcCode
     * @return  
     */
    private boolean isPCModel(String tcCode) {
        if(YmCommonConst.MODEL_YMPC110.equals(tcCode)) {
            return true;
        }else if(YmCommonConst.MODEL_YMPC120.equals(tcCode)) {
            return true;
        }else if(YmCommonConst.MODEL_YMPC130.equals(tcCode)) {
            return true;
        }else if(YmCommonConst.MODEL_YMPC140.equals(tcCode)) {
            return true;
        }else if(YmCommonConst.MODEL_YMPC150.equals(tcCode)) {
            return true;
        }
        return false;
    }

	/**
	 * 오퍼레이션명 : 
	 *
	 * 야드 내/외부 이상 로그 생성
        * param tcId	내부/외부 전문ID
        * param msg	메시지
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */           
	public void createLog(String tcId, String msg) {
        List log = null;
	    try {	       
	        log = new ArrayList();
	        log.add("U");
	        log.add(tcId);            
//	        log.add(YmCommonUtil.getCurDate("yyyyMMddhhmmss"));
	        log.add(msg.length() < 100 ? msg : msg.substring(0,100));
	        log.add("");
	        log.add(tcId);
	        ymCommonDAO.createLog(log);
	        // Flex내 로그 메시지 보이기
	        /* *****************************************************************
	         * 동일한 메시지를 플렉스 화면에도 보여주기 위해서 
	         pushToFlexClient() method call.
	        */
	        JDTORecord jtrLog = JDTORecordFactory.getInstance().create();
	        jtrLog.setField("DESTI", "yd_monitor3");
	        jtrLog.setField("LOGMSG",msg.length() < 100 ? msg : msg.substring(0,100));
	        jtrLog.setField("TCCODE",tcId);
	        jtrLog.setField("YD_GP","3");
	        YmCommonUtil.putLogMsg(msg.length() < 100 ? msg : msg.substring(0,100));
	        //this.putLogMsg(jtrLog);
//	        this.putLogMsg(String szYdGp, 
//	        		  String desti, 
//	        		  String szLogMsg, 
//	        		  String szYdBayGp, 
//	        		  String  szYdEqpId, 
//	        		  String szYdSchCd, 
//	        		  String szYdEvtGp, 
//	        		  String szYdMsgOutpwrGrd, 
//	        		  String szYdPgmTp, 
//	        		  String szYdIfCd, 
//	        		  String szEJBId, 
//	        		  String szMsgName);
	        /* *****************************************************************/

	        
	        
	    }catch (Exception e) {
	        logger.println(LogLevel.DEBUG,this,"insert log error: "+ tcId);
	        throw new EJBServiceException(e);
        }	    
	}

	/**
	 * 공정 모델
     * @param model
     */
    private String getPOPM020Msg(ZZPM001 model, Map itemLen) {
        StringBuffer buffer = new StringBuffer();
        appendMsg(buffer, model.getTcCode(), 		itemLen, 	"TcCode");
        appendMsg(buffer, model.getTcDate(), 		itemLen, 	"TcDate");
        appendMsg(buffer, model.getTcTime(), 		itemLen, 	"TcTime");
        appendMsg(buffer, model.getStl_no(), 		itemLen, 	"Stl_no");
        appendMsg(buffer, model.getPlant_gp(), 		itemLen, 	"Plant_gp");
        appendMsg(buffer, model.getOrd_no(), 		itemLen, 	"Ord_no");
        appendMsg(buffer, model.getOrd_dtl(), 		itemLen, 	"Ord_dtl");
        appendMsg(buffer, model.getOrd_yeojae_gp(), itemLen, 	"Ord_yeojae_gp");
        appendMsg(buffer, model.getStl_prog_cd(), 	itemLen, 	"Stl_prog_cd");
        appendMsg(buffer, ""+ model.getStl_wt(), 	itemLen,	"Stl_wt");
        appendMsg(buffer, model.getYeojae_cause_cd(), 	itemLen,"Yeojae_cause_cd");
        appendMsg(buffer, model.getHold_stl_stamp_gp(), itemLen,"Hold_stl_stamp_gp");
        appendMsg(buffer, model.getSlab_no(), 			itemLen,"Slab_no");
        return buffer.toString();
    }

	/**
	 * 공정 모델
     * @param model
     */
    private String getYMPM001Msg(ZZPM001 model, Map itemLen) {
        StringBuffer buffer = new StringBuffer();
        appendMsg(buffer, model.getTcCode(), itemLen, 			"TcCode");
        appendMsg(buffer, model.getTcDate(), itemLen, 			"TcDate");
        appendMsg(buffer, model.getTcTime(), itemLen, 			"TcTime");
        appendMsg(buffer, model.getTc_occur_pgm(), itemLen, 	"Tc_occur_pgm");
        appendMsg(buffer, model.getTc_occur_ddtt(), itemLen, 	"Tc_occur_ddtt");
        appendMsg(buffer, model.getStl_no(), itemLen, 			"Stl_no");
        appendMsg(buffer, model.getPlant_gp(), itemLen, 		"Plant_gp");
        appendMsg(buffer, model.getOrd_no(), itemLen, 			"Ord_no");
        appendMsg(buffer, model.getOrd_dtl(), itemLen, 			"Ord_dtl");
        appendMsg(buffer, model.getOrd_yeojae_gp(), itemLen, 	"Ord_yeojae_gp");
        appendMsg(buffer, model.getStl_prog_cd(), itemLen, 		"Stl_prog_cd");
        appendMsg(buffer, ""+ model.getStl_wt(), itemLen, 		"Stl_wt");
        appendMsg(buffer, model.getScarfing_yn(), itemLen, 		"Scarfing_yn");
        return buffer.toString();
    }

	/**
	 * 공정 모델
     * @param model
     */
    private String getYMPM002Msg(ZZPM001 model, Map itemLen) {
        StringBuffer buffer = new StringBuffer();
        appendMsg(buffer, model.getTcCode(), itemLen, 			"TcCode");
        appendMsg(buffer, model.getTcDate(), itemLen, 			"TcDate");
        appendMsg(buffer, model.getTcTime(), itemLen, 			"TcTime");
        appendMsg(buffer, model.getTc_occur_pgm(), itemLen, 	"Tc_occur_pgm");
        appendMsg(buffer, model.getTc_occur_ddtt(), itemLen, 	"Tc_occur_ddtt");
        appendMsg(buffer, model.getlocation_no(), itemLen, 		"location_no");
        return buffer.toString();
    }
    /**
	 * 공정 모델
     * @param model
     */
    private String getZZPC001Msg(ZZPC001 model, Map itemLen) {
        StringBuffer buffer = new StringBuffer();
        appendMsg(buffer, model.getTcCode(), itemLen, 			"TcCode");
        appendMsg(buffer, model.getTcDate(), itemLen, 			"TcDate");
        appendMsg(buffer, model.getTcTime(), itemLen, 			"TcTime");
        appendMsg(buffer, model.getrealStlNo(), itemLen, 		"Tc_occur_pgm");
        appendMsg(buffer, model.getplanStlNo(), itemLen, 		"Tc_occur_ddtt");
        appendMsg(buffer, model.geteventStat(), itemLen, 		"Stl_no");
        appendMsg(buffer, model.geteventOccurDDTT(), itemLen, 	"Plant_gp");
        return buffer.toString();
    }

    /**
     * @param buffer
     * @param tcCode
     * @param itemLen
     * @param string
     * @return
     */
    private void appendMsg(StringBuffer buffer, String val, Map itemLen, String field) {
        int len = Integer.parseInt((String)itemLen.get(field));
        if(val != null) {
            buffer.append(val);
            if(len > val.length()) {
                len -= val.length();
                for(int i = 0; i < len; i++) {
                    buffer.append(" ");
                }                
            }
        }else {
            for(int i = 0; i < len; i++) {
                buffer.append(" ");
            }
        }
    }

    /**
	 * 모델 데이터를 NAME/VLAUE 쌍으로 리턴한다.
	 * @return
	 */
	private String getNameValueOfModel(String md) {
        return md.substring(md.indexOf("["), md.length()).replace('[',' ').replace(']',' ');
	}
	
	/**
	 * 수신 모델의 야드구분을 리턴한다.
	 * @param model
	 * @return
	 */	
	private String getYdGp(CommonModel model) {
	    if(model instanceof DMYM001) {
	        return ((DMYM001)model).getyardID();
	    }else if(model instanceof DMYM002) {
	        return ((DMYM002)model).getyardID();
	    }else if(model instanceof DMYM003) {
	        return ((DMYM003)model).getyardID();
	    }else if(model instanceof DMYM004) {
	        return ((DMYM004)model).getyardID();
	    }else if(model instanceof DMYM005) {
	        return ((DMYM005)model).getyardID();
	    }else if(model instanceof DMYM006) {
	        return ((DMYM006)model).getyardID();
	    }else if(model instanceof DMYM007) {
	        return ((DMYM007)model).getyardID();
	    }else if(model instanceof DMYM008) {
	        return ((DMYM008)model).getyardID();
	    }else if(model instanceof DMYM009) {
	        return ((DMYM009)model).getyardID();
	    }else if(model instanceof DMYM010) {
	        return ((DMYM010)model).getyardID();
	    }else if(model instanceof PCYM001) {
	        return ((PCYM001)model).getyardID();
	    }else if(model instanceof PCYM002) {
	        return ((PCYM002)model).getyardID();
	    }else if(model instanceof PCYM003) {
	        return ((PCYM003)model).getyardID();	        
	    }else if(model instanceof PMYM001) {
	        return ((PMYM001)model).getyardID();
	    }else if(model instanceof PMYM002) {
	        return ((PMYM002)model).getyardID();
	    }else if(model instanceof PMYM003) {
	        return ((PMYM003)model).getyardID();
	    }else if(model instanceof PMYM004) {
	        return ((PMYM004)model).getyardID();
	    }else if(model instanceof PMYM005) {
	        return ((PMYM005)model).getyardID();
	    }else if(model instanceof PMYM006) {
	        return ((PMYM006)model).getyardID();
	    }else if(model instanceof PMYM007) {
	        return ((PMYM007)model).getyardID();
	    }else if(model instanceof PMYM008) {
	        return ((PMYM008)model).getPlantGp();
	    }else if(model instanceof POYM001) {
	        return ((POYM001)model).getYardID();
	    }else if(model instanceof POYM002) {
	        return ((POYM002)model).getyardID();
	    }else if(model instanceof POYM003) {
	        return ((POYM003)model).getPlantGp();
	    }else if(model instanceof POYM004) {
	        return ((POYM004)model).getYardId();
	    }else if(model instanceof POYM005) {
	        return ((POYM005)model).getyardID();
	    }else if(model instanceof POYM006) {
	        return ((POYM006)model).getyardID();
	    }else if(model instanceof POYM007) {
	        return ((POYM007)model).getyardID();
	    }else if(model instanceof QMYM001) {
	        return ((QMYM001)model).getyardID();
	    }else if(model instanceof QMYM002) {
	        return ((QMYM002)model).getyardID();
	    }else if(model instanceof QMYM003) {
	        return ((QMYM003)model).getyardID();    
	    }else if(model instanceof POYM008) {
	        return ((POYM008)model).getyardID();
	    }else if(model instanceof POYM009) {
	        return ((POYM009)model).getyardID();
	    }else if(model instanceof PSYM001) {
	        return ((PSYM001)model).getyardID();
	    }else if(model instanceof PSYM002) {
	        return ((PSYM002)model).getyardID();
	    }else if(model instanceof POYM010) {		// SPM2 최규성
	        return ((POYM010)model).getYardId();
	    }
        return " ";
	}
	
	/**
	 * 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param desti
	 * @param pushData
	 */
	public void pushToFlexClient2(String desti,Object pushData) {
		//logger.println(LogLevel.DEBUG,this,"pushToFlexClient2() Start");
		try {
			String szLogMsg = "";
			String szMethodName ="pushToFlexClient2";
			String szSessionName  =getClass().getName();
		
			MessageBroker msgBroker = MessageBroker.getMessageBroker(null);
			String cliendID = UUIDUtils.createUUID(false);
			AsyncMessage msg = new AsyncMessage();
			
			msg.setDestination(desti);
			msg.setClientId(cliendID);
			msg.setMessageId( UUIDUtils.createUUID(false));				
			msg.setTimestamp(System.currentTimeMillis());
			
		
			msg.setBody(pushData);
		
			msgBroker.routeMessageToService(msg, null);
			
			
			szLogMsg = "pushToFlexClient SEND COUNT>>ym>>pushToFlexClient2!!";
			YmCommonUtil.putLog(szSessionName, szMethodName, szLogMsg, 1);
			//logger.println(LogLevel.DEBUG,this,szLogMsg);
		
		} catch(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
				System.out.println("pushToFlexClient exception!!");
				throw new DAOException(getClass().getName() + e.getMessage());
		} finally {
				
		}	
		//logger.println(LogLevel.DEBUG,this,"pushToFlexClient2() END");

	}

}