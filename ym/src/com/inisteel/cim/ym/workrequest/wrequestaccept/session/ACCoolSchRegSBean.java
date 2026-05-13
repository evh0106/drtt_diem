package com.inisteel.cim.ym.workrequest.wrequestaccept.session;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.log.LogService;
import jspeed.base.log.LogServiceConfig;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTORecord;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.common.parser.Level2Parser;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ym.common.YmCommonDB;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdEquipDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO;
import com.inisteel.cim.ym.steelinfo.steelinforecv.dao.YdStockDAO;
import com.inisteel.cim.ym.workrequest.wrequestaccept.dao.YdWBookDAO;
/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="ACCoolSchRegEJB" jndi-name="JNDIACCoolSchReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class ACCoolSchRegSBean extends BaseSessionBean {
	public static int iGVFlag = 1;
	public void ejbCreate() {
	}
	private YmComm ymComm = new YmComm();

	/**
	 * 오퍼레이션명 : 
	 *  야드 LEVEL2로 부터 넘어온 전문을 파싱한 후 전문내용을 가지고 해당 업무 로직을 처리한다.
        *  전문내용을 JDTORecord로 파싱한다.
        *  업무 로직
        *  1.TC_CD - THCH590 (I/F ID : YM-AIF-010) 출하작업(JNDIACCoolSchReg/receiveACCoolSch)
        *  2.야드 LEVEL2로부터 ACY_냉각장 SCHEDULE 요구 정보를 수신
        *  3.수신한 Crane No가 어느동에 해당하는 점검
        *  4.A/B동 출하작업예약(TB_YM_WBOOK)을 점검해서  
        *  5.작업 예약이 제일 빠른 Coil을 찾아서 Schedule Call
        *      
        *  THCH590ACR2    050927094624     1
        * 
        *  logger.println(LogLevel.DEBUG, "전문코드	 ==" +jDTORecord.getFieldString("전문코드"));
        *  logger.println(LogLevel.DEBUG, "CRANENO  ==" +jDTORecord.getFieldString("CRANENO"));
        *  logger.println(LogLevel.DEBUG, "SPARE1   ==" +jDTORecord.getFieldString("SPARE1"));
        *  logger.println(LogLevel.DEBUG, "발생일	 ==" +jDTORecord.getFieldString("발생일"));
        *  logger.println(LogLevel.DEBUG, "발생시	 ==" +jDTORecord.getFieldString("발생시"));
        *  logger.println(LogLevel.DEBUG, "SPARE2   ==" +jDTORecord.getFieldString("SPARE2"));
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */    
	public boolean receiveDistributionSch(String sMessage)  throws java.rmi.RemoteException{ 
        
        LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		Logger logger           = new Logger(config);
		logger.println(LogLevel.DEBUG,this,"Start-receiveDistributionSch()");
		
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
			
			String ydGp        = StringHelper.evl(schRule.getFieldString("YD_GP"), "");			
			String bayGp       = StringHelper.evl(schRule.getFieldString("BAY_GP"), "");
			String schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_GVF1; //Coil 제품출하상차
			if(iGVFlag == 1){
				iGVFlag	= 2;
				schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_GVF1; //H(출하) 제품  출하 상차 
			}else if(iGVFlag == 2){
				iGVFlag	= 1;
				schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_GVFL; //H(출하) 제품  출하 상차  
			} 
			
			EJBConnector ejbConn = new EJBConnector("default","JNDICWrkOrdReg",this);
			isSuccess = (Boolean)ejbConn.trx("callCraneSchInfo",new  Class[]{String.class, String.class, String.class, String.class},
								            new Object[]{ydGp.trim(), bayGp.trim(), NewCraneNo.trim(), schWorkKind.trim()});
			
			if (isSuccess.booleanValue() == false){
				return false; 
			}
			
		    /* 작업 예약  Table(TB_YM_WBOOK) : 출하 작업예약 등록 우선 순위가 제일 빠른것 추출
			 * Select MIN(WBOOK_ID) From TB_YM_WBOOK a
			 *  Where YD_GP         = ? --야드구분
			 *    And BAY_GP        = ? --동구분
			 *    And SCH_WORK_KIND = ? --스케줄코드
			 *    AND not exists (select wbook_id from tb_ym_sch where wbook_id = a.wbook_id) 
			 */
			String sSchwBook = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectSchWBook";
			JDTORecord WBook = ydEquipDAO.getData(sSchwBook, new Object[]{ ydGp.trim(), bayGp.trim(), schWorkKind.trim() });			
			
			String wbookId = StringHelper.evl(WBook.getFieldString("WBOOK_ID"), "");
			
			logger.println(LogLevel.DEBUG,this, "receiveDistributionSch()WBOOK_ID: "+wbookId);
			if (wbookId == null || wbookId.equals("")){
				// Error Message EJB Call				
			}
			else{
				//Coil Schedule EJB Call
				ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
			    Boolean isTrue  = (Boolean)ejbConn.trx("callCraneSchInfo",new Class[]{String.class, String.class},new Object[]{ 
		 		                   wbookId.trim(), NewCraneNo.trim() }); 
			}							
			
			logger.println(LogLevel.DEBUG,this,"End-receiveDistributionSch()");
			
			return true;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	    	e.printStackTrace();  
	        throw new EJBServiceException(e);
	    }    
	} 

}