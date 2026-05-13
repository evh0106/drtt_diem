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
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdEquipDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO;
import com.inisteel.cim.ym.steelinfo.steelinforecv.dao.YdStockDAO;
import com.inisteel.cim.ym.workrequest.wrequestaccept.dao.YdWBookDAO;
/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="ACMoveOrdRegEJB" jndi-name="JNDIACMoveOrdReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class ACMoveOrdRegSBean extends BaseSessionBean {
	
	/**
	 *	1 : 동간이적
	 *	2 : 동내이적
	 *	3 : CTS하차
	 */
	public static int iCFlag = 1;
	public static int iFFlag = 1;
	
	public void ejbCreate() {
	}
	private YmComm ymComm = new YmComm();
	
	/**
	 * 오퍼레이션명 : 
	 *  야드 LEVEL2로 부터 넘어온 전문을 파싱한 후 전문내용을 가지고 해당 업무 로직을 처리한다.
        *  전문내용을 JDTORecord로 파싱한다.
        *  업무 로직
        *  1.TC_CD - THCH680 (I/F ID : YM-AIF-016 )
        *  2.야드 LEVEL2로부터 ACY_이적지시요구 정보를 수신
        *  3.수신한 Crane No가 어느동에 해당하는 점검
        *  4.해당동이 C, F 동이면 이적
        *  5.해당동이 B, D, E 동이면 보급
        *  6.이적 : 해당 작업예약이 되어 있다면 Schedule Call, 아니면 Error
        *  7.보급 : 해당 작업예약이 되어 있다면 Schedule Call, 아니면 Error    
        *  THCH680BCR1    050927094820     1
        * 
        *  logger.println(LogLevel.DEBUG, "전문코드	 ==" +jDTORecord.getFieldString("전문코드"));
        *  logger.println(LogLevel.DEBUG, "CRANENO  ==" +jDTORecord.getFieldString("CRANENO"));
        *  logger.println(LogLevel.DEBUG, "SPARE1   ==" +jDTORecord.getFieldString("SPARE1"));
        *  logger.println(LogLevel.DEBUG, "일자	     ==" +jDTORecord.getFieldString("일자"));
        *  logger.println(LogLevel.DEBUG, "시간	     ==" +jDTORecord.getFieldString("시간"));
        *  logger.println(LogLevel.DEBUG, "SPARE    ==" +jDTORecord.getFieldString("SPARE"));
        *  logger.println(LogLevel.DEBUG, "구분	     ==" +jDTORecord.getFieldString("구분"));  1:요구, 2:해제
        *  logger.println(LogLevel.DEBUG, "SPARE2	 ==" +jDTORecord.getFieldString("SPARE2"));                             
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */    
	public boolean receiveACMoveOrd(String sMessage) throws java.rmi.RemoteException{ 
        
        LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		Logger logger           = new Logger(config);
		logger.println(LogLevel.DEBUG,this,"Start-receiveACMoveOrd()");
		
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
			String sGP_CHK = StringHelper.evl(jDTORecord.getFieldString("구분"), "1");

            /* 수신한 Crane No를  변환
			 * SELECT class3_cd  AS EQUIPNO,
			 *        b.equip_no  as crane_no
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
			
			String ydGp  = StringHelper.evl(schRule.getFieldString("YD_GP"), "");			
			String bayGp = StringHelper.evl(schRule.getFieldString("BAY_GP"), "");
//			String schWorkKind = StringHelper.evl(schRule.getFieldString("SCH_WORK_KIND"), "");

			/*	A,B(냉각장)=CDLO
			 *	C,F(추출)=C동:CFLO , F동:CKLO
             *	D(동간이적)=CTML
             *	분기 Conveyor 작업요구=CDLO
             *	A,B,E,G,H(이적)=CSLM
             *	C,F(동간이적)=CTML
             *	B,D,E(보급)=B동:CFLI , D동:CKLO  ,E동:CKTI
			 */			
			//C,F(이적), B,D,E(보급)
			String schWorkKind = "";
			
			if("1".equals(sGP_CHK)){
			if (bayGp.equals(YmCommonConst.BAY_GP_B)){            //B(보급) Coil HFL 보급
				schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CFLI;
			}else if (bayGp.equals(YmCommonConst.BAY_GP_D)){      //D(보급) Coil SPM 보급
				schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CKLI;
			}else if (bayGp.equals(YmCommonConst.BAY_GP_E)){      //E(보급) Coil SPM Take In
				schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CKTI;
			}else if (bayGp.equals(YmCommonConst.BAY_GP_C)){
				if(iCFlag == 1){
					iCFlag	= 2;
					schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CTML; //C(이적) Coil 동간이적상차
				}else if(iCFlag == 2){
					iCFlag	= 3;
					schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CYMM; //C(이적) Coil 동내이적
				}else if(iCFlag == 3){
					iCFlag	= 1;
					schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CCMU; //C(이적) Coil CTS 하차
				}		    
			}else if (bayGp.equals(YmCommonConst.BAY_GP_F)){
				if(iFFlag == 1){
					iFFlag	= 2;
					schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CTML; //F(이적) Coil 동간이적상차
				}else if(iFFlag == 2){
					iFFlag	= 3;
					schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CYMM; //F(이적) Coil 동내이적
				}else if(iFFlag == 3){
					iFFlag	= 1;
					schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CCMU; //F(이적) Coil CTS 하차
				}
//				else if(iFFlag == 4){
//					iFFlag	= 1;
//					schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CCMR; //F(이적) Coil CTS 하차(2)
//				}		    
			}	
			}
			
			//보급기능 추가 2인경우 이퀄라이저 보급
			if("2".equals(sGP_CHK)){
				if (bayGp.equals(YmCommonConst.BAY_GP_E)|| bayGp.equals(YmCommonConst.BAY_GP_F)){
					schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_EQLI; //F Coil EQL 보급	    
				}
				
				if (bayGp.equals(YmCommonConst.BAY_GP_G)){
					schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_EQLO; //G Coil EQL 추출	    
				}
			}
			
			EJBConnector ejbConn = new EJBConnector("default","JNDICWrkOrdReg",this);
			isSuccess = (Boolean)ejbConn.trx("callCraneSchInfo",new  Class[]{String.class, String.class, String.class, String.class},
								            new Object[]{ydGp.trim(), bayGp.trim(), NewCraneNo.trim(), schWorkKind.trim()});
			
			if (isSuccess.booleanValue() == false){
				return false; 
			}
			
            /* 작업 예약  Table(TB_YM_WBOOK) : 작업예약 등록 우선 순위가 제일 빠른것 추출
			 * Select MIN(WBOOK_ID) From TB_YM_WBOOK a
			 *  Where YD_GP         = ? --야드구분
			 *    And BAY_GP        = ? --동구분
			 *    And SCH_WORK_KIND = ? --스케줄코드
			 *    AND not exists (select wbook_id from tb_ym_sch where wbook_id = a.wbook_id) 
			 */ 
			String sSchwBook = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectSchWBook";
			JDTORecord WBook = ydEquipDAO.getData(sSchwBook, new Object[]{ ydGp.trim(), bayGp.trim(), schWorkKind.trim() });			
			
			String wbookId = StringHelper.evl(WBook.getFieldString("WBOOK_ID"), "");
			
			logger.println(LogLevel.DEBUG,this, "receiveACMoveOrd()WBOOK_ID: "+wbookId);
			
			if (wbookId == null || wbookId.equals("")){
				// Error Message EJB Call				
			}
			else{
				//Coil Schedule EJB Call
				ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
				 Boolean isTrue  = (Boolean)ejbConn.trx("callCraneSchInfo",new Class[]{String.class, String.class},new Object[]{ 
		 		           wbookId.trim(), NewCraneNo.trim() });
			}
		
			logger.println(LogLevel.DEBUG,this, "End-receiveACMoveOrd()");
							
			return true;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	    	e.printStackTrace();  
	        throw new EJBServiceException(e);
	    }    
	} 

}