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
 * @ejb.bean name="ACDistOrdRegEJB" jndi-name="JNDIACDistOrdReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class ACDistOrdRegSBean extends BaseSessionBean {
	
	/**
	 *	1 : 동간이적
	 *	2 : 동내이적
	 *	3 : CTS하차
	 */
	public static int iAFlag = 1;
	public static int iBFlag = 1;
	public static int iEFlag = 1;
	public static int iGFlag = 1;
	public static int iHFlag = 1;
	public static int iCDFlag = 1;
	
	public void ejbCreate() {
	}
	private YmComm ymComm = new YmComm();
	
	/**
	 * 오퍼레이션명 : 
	 *  야드 LEVEL2로 부터 넘어온 전문을 파싱한 후 전문내용을 가지고 해당 업무 로직을 처리한다.
        *  전문내용을 JDTORecord로 파싱한다.
        *  업무 로직
        *  1.TC_CD - THCH670 (I/F ID : YM-AIF-015)
        *  2.야드 LEVEL2로부터 ACY_이적지시요구 정보를 수신
        *  3.수신한 Crane No가 어느동에 해당하는 점검
        *  4.해당동이 A,B,E,G,H 동이면 이적
        *  5.해당동이 C,D,F 동이면 출하
        *  6.이적 : 해당 작업예약이 되어 있다면 Schedule Call, 아니면 Error
        *  7.출하 : 해당 작업예약이 되어 있다면 Schedule Call, 아니면 Error    
        *  THCH670ACR2    050927094624     1
        * 
        * logger.println(LogLevel.DEBUG, "전문코드	==" +jDTORecord.getFieldString("전문코드"));
        * logger.println(LogLevel.DEBUG, "CRANENO  ==" +jDTORecord.getFieldString("CRANENO"));
        * logger.println(LogLevel.DEBUG, "SPARE1   ==" +jDTORecord.getFieldString("SPARE1"));
        * logger.println(LogLevel.DEBUG, "일자	    ==" +jDTORecord.getFieldString("일자"));
        * logger.println(LogLevel.DEBUG, "시간	    ==" +jDTORecord.getFieldString("시간"));
        * logger.println(LogLevel.DEBUG, "SPARE    ==" +jDTORecord.getFieldString("SPARE"));
        * logger.println(LogLevel.DEBUG, "구분	    ==" +jDTORecord.getFieldString("구분"));  1:요구, 2:해제
        * logger.println(LogLevel.DEBUG, "SPARE2	==" +jDTORecord.getFieldString("SPARE2"));
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */    
	public boolean receiveACDistOrd(String sMessage) throws java.rmi.RemoteException{ 
        
        LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		Logger logger = new Logger(config);
		logger.println(LogLevel.DEBUG,this,"Start-receiveACDistOrd()");
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		YdEquipDAO ydEquipDAO           = new YdEquipDAO();
		CraneSchDAO dao                 = new CraneSchDAO(); 
		
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
			
			// Crane No로 설비 Table(TB_YM_EQUIP)을 Read
			// Crane No로 야드구분, 동구분, 설비 Select
			//SELECT yd_gp, bay_gp, equip_kind, equip_no  FROM tb_ym_equip WHERE equip_no   = :crane_no AND equip_kind = 'CR'
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
             *  E,D,G,H(출하)=GVFL
             *	분기 Conveyor 작업요구=CDLO
             *	A,B,E,G,H(이적)=CYMM
             *	C,F(출하)=GVFL , I동:GVFL
             *	C,F(동간이적)=CTML
             *	B,D,E(보급)=B동:CFLI , D동:CKLO  ,E동:CKTI
			 */
			
			//(A,B,E,G,H(이적),C,F(출하)
			String schWorkKind = "";
			if (bayGp.equals(YmCommonConst.BAY_GP_A)){             
			    if(iAFlag == 1){
					iAFlag	= 2;
					schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CTML; //A(이적) Coil 동간이적상차
				}else if(iAFlag == 2){
					iAFlag	= 3;
					schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CYMM; //A(이적) Coil 동내이적
				}else if(iAFlag == 3){
					iAFlag	= 1;
					schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CCMU; //A(이적) Coil CTS 하차
				}
//				else if(iAFlag == 4){
//					iAFlag	= 1;
//					schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CCMR; //A(이적) Coil CTS 하차(2)
//				}	
			}else if (bayGp.equals(YmCommonConst.BAY_GP_B)){
				if(iBFlag == 1){
					iBFlag	= 2;
					schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CTML; //B(이적) Coil 동간이적상차
				}else if(iBFlag == 2){
					iBFlag	= 3;
					schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CYMM; //B(이적) Coil 동내이적
				}else if(iBFlag == 3){
					iBFlag	= 1;
					schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CCMU; //B(이적) Coil CTS 하차
				}
//				else if(iBFlag == 4){
//					iBFlag	= 1;
//					schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CCMR; //B(이적) Coil CTS 하차(2)
//				}	
			}else if (bayGp.equals(YmCommonConst.BAY_GP_E)){
				if(iEFlag == 1){
					iEFlag	= 2;
					schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CTML; //E(이적) Coil 동간이적상차
				}else if(iEFlag == 2){
					iEFlag	= 3;
					schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CYMM; //E(이적) Coil 동내이적
				}else if(iEFlag == 3){
					iEFlag	= 1;
					schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CCMU; //E(이적) Coil CTS 하차
				}
//				else if(iEFlag == 4){
//					iEFlag	= 1;
//					schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CCMR; //E(이적) Coil CTS 하차(2)
//				}
			}else if (bayGp.equals(YmCommonConst.BAY_GP_G)){
				if(iGFlag == 1){
					iGFlag	= 2;
					schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CTML; //G(이적) Coil 동간이적상차
				}else if(iGFlag == 2){
					iGFlag	= 3;
					schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CYMM; //G(이적) Coil 동내이적
				}else if(iGFlag == 3){
					iGFlag	= 1;
					schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CCMU; //G(이적) Coil CTS 하차
				}
//				else if(iGFlag == 4){
//					iGFlag	= 1;
//					schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CCMR; //G(이적) Coil CTS 하차(2)
//				}	
			}else if (bayGp.equals(YmCommonConst.BAY_GP_H)){			
				if(iHFlag == 1){
					iHFlag	= 2;
					schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CTML; //H(이적) Coil 동간이적상차
				}else if(iHFlag == 2){
					iHFlag	= 3;
					schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CYMM; //H(이적) Coil 동내이적
				}else if(iHFlag == 3){
					iHFlag	= 1;
					schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CCMU; //H(이적) Coil CTS 하차
				}
//				else if(iHFlag == 4){
//					iHFlag	= 1;
//					schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CCMR; //H(이적) Coil CTS 하차(2)
//				}		    
			}else if (bayGp.equals(YmCommonConst.BAY_GP_C)|| bayGp.equals(YmCommonConst.BAY_GP_D)){             //C,D(출하)  Coil 제품출하상차
				
				if(iCDFlag == 1){
					iCDFlag	= 2;
					schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_GVF1; // (출하) 제품  출하 상차 
				}else if(iCDFlag == 2){
					iCDFlag	= 1;
					schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_GVFL; // (출하) 제품  출하 상차  
				}
				 
				     /**
				      * 2007.04.30 이정훈
				      */
				    JDTORecord schRc  = dao.getCraneSchCount(ydGp,
				    										bayGp,
				    										schWorkKind);
				    if(schRc != null){
				    	String sSchCount = StringHelper.evl(schRc.getFieldString("COUNT"), "0");
				    	
				    	if(Integer.parseInt(sSchCount) == 0){
				    		logger.println(LogLevel.DEBUG,this, "C동 출하 작업 없슴 1");
					    	return false;
				    	}
				    	
				    }
				    
			}else if (bayGp.equals(YmCommonConst.BAY_GP_F)){             //F(출하)  Coil 제품출하상차
				if (OldCraneNo.equals(YmCommonConst.A_CraneNo_FCR1)){
					schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_GVFL;
					
				}else if (OldCraneNo.equals(YmCommonConst.A_CraneNo_FCR2)){
					schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_GVF1;
					
				}
			}
			
			EJBConnector ejbConn = new EJBConnector("default","JNDICWrkOrdReg",this);
			isSuccess = (Boolean)ejbConn.trx("callCraneSchInfo",new  Class[]{String.class, String.class, String.class, String.class},
								            new Object[]{ydGp.trim(), bayGp.trim(), NewCraneNo.trim(), schWorkKind.trim()});
			
			if (isSuccess.booleanValue() == false){
				return false; 
			}
           
			/**
			 *  2007.05.02 이정훈
			 *  이송 상차, 출하 는 작업 예약에서 찾지 않음
			 */
			if (schWorkKind.equals(YmCommonConst.NEW_SCH_WORK_KIND_GVFL) ||
				schWorkKind.equals(YmCommonConst.NEW_SCH_WORK_KIND_GVF1) ||
				schWorkKind.equals(YmCommonConst.NEW_SCH_WORK_KIND_GVF2) ||
				schWorkKind.equals(YmCommonConst.NEW_SCH_WORK_KIND_CVM2) ||
				schWorkKind.equals(YmCommonConst.NEW_SCH_WORK_KIND_CVM3) ||
				schWorkKind.equals(YmCommonConst.NEW_SCH_WORK_KIND_CVML))
			{
				isSuccess = (Boolean)ejbConn.trx("callCraneSchInfo",new  Class[]{String.class, String.class, String.class, String.class,String.class, String.class, String.class},
			            new Object[]{YmCommonConst.TC_THCH520, ydGp.trim(), bayGp.trim(),YmCommonConst.EQUIP_KIND_CR, NewCraneNo.trim(), schWorkKind.trim(),""});
				
				/*boolean isYahoo =  callCraneSchInfo(YmCommonConst.TC_THCH520,
									ydGp.trim(), 
					  	sBayGp,
					    YmCommonConst.EQUIP_KIND_CR,
					    sCraneNo,
					    sSchCode,
					    "");*/
				logger.println(LogLevel.DEBUG,this,"= A 작업요구=크레인 작업요구 출하 호출="+isSuccess);	
				return true;
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
			
			logger.println(LogLevel.DEBUG,this, "receiveACDistOrd()WBOOK_ID: "+wbookId);
			if (wbookId == null || wbookId.equals("")){
				// Error Message EJB Call				
			}
			else{
				//Coil Schedule EJB Call
				 ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
				 Boolean isTrue  = (Boolean)ejbConn.trx("callCraneSchInfo",new Class[]{String.class, String.class},new Object[]{ 
		 		                    wbookId.trim(), NewCraneNo.trim() });
			}
		
			logger.println(LogLevel.DEBUG,this, "End-receiveACDistOrd()");
							
			return true;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	    	e.printStackTrace();  
	        throw new EJBServiceException(e);
	    }    
	} 

}
