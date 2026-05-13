package com.inisteel.cim.ym.workrequest.wrequestaccept.session;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.log.LogService;
import jspeed.base.log.LogServiceConfig;
import jspeed.base.log.Logger;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdEquipDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO;
import com.inisteel.cim.ym.steelinfo.steelinforecv.dao.YdStockDAO;
import com.inisteel.cim.ym.workrequest.wrequestaccept.dao.YdWBookDAO;
/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="ACExtOrdRegEJB" jndi-name="JNDIACExtOrdReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class ACExtOrdRegSBean extends BaseSessionBean {
	public void ejbCreate() {
	}
	private YmComm ymComm = new YmComm();
	/**
	 * 오퍼레이션명 : 
	 *  야드 LEVEL2로 부터 넘어온 전문을 파싱한 후 전문내용을 가지고 해당 업무 로직을 처리한다.
        *  전문내용을 JDTORecord로 파싱한다.
        *  업무 로직
        *	1.TC_CD - THCH670 (I/F ID : YM-AIF-015)
        *	2.야드 LEVEL2로부터 ACY_이적지시요구 정보를 수신
        *  3.수신한 Crane No가 어느동에 해당하는 점검
        *  4.해당동이 A,B,E,G,H 동이면 이적
        *  5.해당동이 C,F 동이면 출하
        *  6.이적 : 해당 작업예약이 되어 있다면 Schedule Call, 아니면 Error
        *  7.출하 : 해당 작업예약이 되어 있다면 Schedule Call, 아니면 Error    
        *  THCH670ACR2    050927094624     1                         
	 * logger.println(LogLevel.DEBUG, "전문코드	  ==" +jDTORecord.getFieldString("전문코드"));
	 * logger.println(LogLevel.DEBUG, "CRANENO  ==" +jDTORecord.getFieldString("CRANENO"));
	 * logger.println(LogLevel.DEBUG, "SPARE    ==" +jDTORecord.getFieldString("SPARE"));
	 * logger.println(LogLevel.DEBUG, "일자	  ==" +jDTORecord.getFieldString("일자"));
	 * logger.println(LogLevel.DEBUG, "시간	  ==" +jDTORecord.getFieldString("시간"));
	 * logger.println(LogLevel.DEBUG, "SPARE    ==" +jDTORecord.getFieldString("SPARE"));
	 * logger.println(LogLevel.DEBUG, "구분	  ==" +jDTORecord.getFieldString("구분"));
	 * logger.println(LogLevel.DEBUG, "SPARE	  ==" +jDTORecord.getFieldString("SPARE"));                                  
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */    
	public boolean receiveACExtOrd(String sMessage)  throws java.rmi.RemoteException{ 
        
        LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		Logger logger = new Logger(config);
		logger.println(LogLevel.DEBUG,this,"Start-receiveACExtOrd()");
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		YdEquipDAO ydEquipDAO           = new YdEquipDAO();
		
		try{
			
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			EJBConnector ejbConn = new EJBConnector("default","JNDIACCoolOrdReg",this);
			Boolean isTrue = (Boolean)ejbConn.trx("JNDIACDistOrdReg",new Class[]{String.class},new Object[]{ sMessage });
			
			logger.println(LogLevel.DEBUG,this, "End-receiveACExtOrd()");
							
			return true;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	    	e.printStackTrace();  
	        throw new EJBServiceException(e);
	    }    
	} 

}