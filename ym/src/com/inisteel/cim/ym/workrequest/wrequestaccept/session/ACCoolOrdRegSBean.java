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
import com.inisteel.cim.common.jms.model.ym.POYM008;
import com.inisteel.cim.common.jms.model.ym.QMYM002;
import com.inisteel.cim.common.jms.model.ym.QMYM003;
import com.inisteel.cim.common.jms.model.ym.POYM007;
import com.inisteel.cim.common.jms.model.ym.DMYM010;
import com.inisteel.cim.common.parser.Level2Parser;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ym.common.YmCommonDB;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdEquipDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO;
import com.inisteel.cim.ym.steelinfo.steelinforecv.dao.YdStockDAO;
import com.inisteel.cim.ym.workrequest.wrequestaccept.dao.YdWBookDAO;
/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="ACCoolOrdRegEJB" jndi-name="JNDIACCoolOrdReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class ACCoolOrdRegSBean extends BaseSessionBean {
	Logger logger=null;
	
	private YmComm ymComm = new YmComm();
	
	/**
	 *	1 : 동간이적
	 *	2 : 동내이적
	 *	3 : CTS하차
	 */
	public static int iBFlag = 1;
	public static int iCFlag = 1;
	public static int iDFlag = 1;
	public static int iEFlag = 1;
	public static int iFFlag = 1;
	public static int iHFlag = 1;
	
	public void ejbCreate() {
        LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
        logger = new Logger(config);				
	}

	/**
	 *  야드 LEVEL2로 부터 넘어온 전문을 파싱한 후 전문내용을 가지고 해당 업무 로직을 처리한다.
     	 *  전문내용을 JDTORecord로 파싱한다.
        *  업무 로직
        *  1.TC_CD - THCH660 (I/F ID : YM-AIF-014 )
        *  2.야드 LEVEL2로부터 ACY_냉각장지시요구 정보를 수신
        *  3.수신한 Crane No가 어느동에 해당하는 점검
        *  4.A,B(냉각장),C,F(추출),D(이적),E,G,H(출하)
        *  5.해당 작업예약이 되어 있다면 Schedule Call, 아니면 Error    
        *  THCH660BCR1    050927092711     1
        * 
        *  logger.println(LogLevel.DEBUG, "전문코드	 ==" +jDTORecord.getFieldString("전문코드"));
        *  logger.println(LogLevel.DEBUG, "CRANENO  ==" +jDTORecord.getFieldString("CRANENO"));
        *  logger.println(LogLevel.DEBUG, "SPARE1   ==" +jDTORecord.getFieldString("SPARE1"));
        *  logger.println(LogLevel.DEBUG, "일자	     ==" +jDTORecord.getFieldString("일자"));
        *  logger.println(LogLevel.DEBUG, "시간	     ==" +jDTORecord.getFieldString("시간"));
        *  logger.println(LogLevel.DEBUG, "SPARE2   ==" +jDTORecord.getFieldString("SPARE2"));
        *  logger.println(LogLevel.DEBUG, "구분	     ==" +jDTORecord.getFieldString("구분")); 1:요구, 2:해제
        *  logger.println(LogLevel.DEBUG, "SPARE3   ==" +jDTORecord.getFieldString("SPARE3"));                        
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param sMessage
	 * @return
	 * @throws
	 */
	public boolean receiveACCoolOrd(String sMessage) throws java.rmi.RemoteException{ 
        
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
        LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		Logger logger           = new Logger(config);
		logger.println(LogLevel.DEBUG,this,"Start-receiveACCoolOrd()");
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		YdEquipDAO ydEquipDAO           = new YdEquipDAO();
		String sSchwBook                = "";
		CraneSchDAO craneSchDAO 		= new CraneSchDAO();
		try{
			Boolean isSuccess = new Boolean(false); 
			int iResult = 0;
			
			Level2Parser level2Parser 	= new Level2Parser();
			JDTORecord jDTORecord 		= level2Parser.parse(sMessage);
			
			// 야드 Level-2에서  수신한  Crane No			
			String OldCraneNo = StringHelper.evl(jDTORecord.getFieldString("CRANENO"), "");
			String sGP_CHK = StringHelper.evl(jDTORecord.getFieldString("구분"), "1");
			
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
			
			String ydGp  = StringHelper.evl(schRule.getFieldString("YD_GP"), "");			
			String bayGp = StringHelper.evl(schRule.getFieldString("BAY_GP"), "");
			String delYn = StringHelper.evl(schRule.getFieldString("DEL_YN"), "");
			
			logger.println(LogLevel.DEBUG,this,"=작업요구 => C동 수입 백업가능 여부( VW_YD_YDB999 )="+delYn);
			
			//해당크레인 긴급재 작업 초기화 작업
			//ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.updateresetSch
			int iSeq = craneSchDAO.updateCraneSchStat2(ydGp, bayGp ,NewCraneNo );
			logger.println(LogLevel.DEBUG,this,"=작업요구 => 스케쥴 정보 초기화="+iSeq);
			
//			String schWorkKind = StringHelper.evl(schRule.getFieldString("SCH_WORK_KIND"), "");
			
			/*	A,B(냉각장)=CDLO
			 *	C,F(추출)=C동:CFLO , F동:CKLO
             *	D(동간이적)=CTML
             *  E,G,H(출하)=GVFL
             *	분기 Conveyor 작업요구=CDLO
             *	A,B,E,G,H(이적)=CSLM
             *	C,F(출하)=GVFL , I동:GVFL
             *	C,F(동간이적)=CTML
             *	B,D,E(보급)=B동:CFLI , D동:CKLO  ,E동:CKTI
			 */
			
			/*(A,B(냉각장),C,F(추출),D(이적),E,G,H(출하)요구/해제)
			 * A동 대차 하차로 변경(06.08.29) 
			String schWorkKind = "";
			if (bayGp.equals(YmCommonConst.BAY_GP_A) || bayGp.equals(YmCommonConst.BAY_GP_B)){     //A,B(냉각장)
				receiveACCoolSch(sMessage);
			*/	

			String schWorkKind = "";
			
			if("1".equals(sGP_CHK)){	
				if(bayGp.equals(YmCommonConst.BAY_GP_B)){
				 
					if(iBFlag == 1){
						iBFlag	= 2;
						
						if(!"Y".equals(delYn)){
							receiveACCoolSch(sMessage);								 
							schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CDLO;      //B(냉각장)	
							logger.println(LogLevel.DEBUG,this, "End-receiveACCoolOrd()");
						}
						
						return true;
					}else if(iBFlag == 2){
						iBFlag	= 3;
						schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CTMU;     //A(대차 하차)
					}else if(iBFlag == 3){
						iBFlag	= 4;
						schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CFLI;     //B(HFL보급)
					}else if(iBFlag == 4){
						iBFlag	= 1;
						schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CVML;     //B(소재이송상차)
					}
					
				}else if(bayGp.equals(YmCommonConst.BAY_GP_A)){
			    	schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CTMU;     //A(대차 하차)
			    }else if (bayGp.equals(YmCommonConst.BAY_GP_C)){                                   //C(추출) Coil HFL 추출
			    	if("Y".equals(delYn)){
			    		//C동 백업으로 처리 하는 경우 수입까지 포함 한다. 2017.04.16
						if(iCFlag == 1){
							iCFlag	= 2;
							
							receiveACCoolSch(sMessage);								 
							schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CDLO;      //B(냉각장)	
							logger.println(LogLevel.DEBUG,this, "End-receiveACCoolOrd()");
							
							return true;
						}else if(iCFlag == 2){
							iCFlag	= 1;
							schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CFLO; 	//C(추출) Coil HFL 추출
						}
			    	}else {
			    		schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CFLO; 	//C(추출) Coil HFL 추출
			    	}
				}else if (bayGp.equals(YmCommonConst.BAY_GP_F)){                                   //F(추출) Coil SPM 추출
					
					schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CKLO;				//크레인 SPM 추출 요구
					
				}else if (bayGp.equals(YmCommonConst.BAY_GP_D)){     
					if(iDFlag == 1){
						iDFlag	= 2;
						schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CTML; //D(이적) Coil 동간이적상차
					}else if(iDFlag == 2){
						iDFlag	= 3;
						schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CYMM; //D(이적) Coil 동내이적
					}else if(iDFlag == 3){
						iDFlag	= 1;
						schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CCMU; //D(이적) Coil CTS 하차
					}	
				}else if (bayGp.equals(YmCommonConst.BAY_GP_G) || 
						  bayGp.equals(YmCommonConst.BAY_GP_H)){                                   //G,H(출하) Coil 제품출하상차
	//					schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_GVFL;
					
					if(iHFlag == 1){
						iHFlag	= 2;
						schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_GVFL; //H(출하) 제품  출하 상차 
					}else if(iHFlag == 2){
						iHFlag	= 3;
						schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_GVF1; //H(출하) 제품  출하 상차  
					}else if(iHFlag == 3){
						iHFlag	= 4;
						schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_GVF2; //H(출하) 제품  출하 상차 
					}else if(iHFlag == 4){
						iHFlag	= 1;
						schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_GVMU; //H(출하) 제품  이송 하차
					}	
					 
					logger.println(LogLevel.DEBUG,this,"H동 Schedule 구분 : "+ schWorkKind);
						
				} // E 출하, 소재이송 동시에 사용 (2007.03.07 이정훈)
				else if (bayGp.equals(YmCommonConst.BAY_GP_E)){                                   
					if(iEFlag == 1){
						iEFlag	= 2;
						schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_GVF1; //E(출하) 제품  출하 상차 
					}else if(iEFlag == 2){
						iEFlag	= 3;
						schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_GVFL; //E(출하) 제품  출하 상차 
					}else if(iEFlag == 3){
						iEFlag	= 1;
						schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_CKLR;	//크레인 SPM 재작업 추출 요구
					}	
					
					logger.println(LogLevel.DEBUG,this,"E동 Schedule 구분 : "+ schWorkKind);
				}
			}
			
			if("2".equals(sGP_CHK)){
				schWorkKind = YmCommonConst.NEW_SCH_WORK_KIND_EQLO;				//크레인 QE 추출 요구	
			}

			    logger.println(LogLevel.DEBUG,this,bayGp+"동 Schedule 구분 : "+ schWorkKind);
			    
				EJBConnector ejbConn = new EJBConnector("default","JNDICWrkOrdReg",this);
				isSuccess = (Boolean)ejbConn.trx("callCraneSchInfo",new  Class[]{String.class, String.class, String.class, String.class},
									            new Object[]{ydGp.trim(), bayGp.trim(), NewCraneNo.trim(), schWorkKind.trim()});
				
				if (isSuccess.booleanValue() == false){
					return false; 
				}
				
				/**
				 *  2007.04.02 이정훈
				 *  이송 상차, 출하 는 작업 예약에서 찾지 않음
				 */
				if (schWorkKind.equals(YmCommonConst.NEW_SCH_WORK_KIND_GVFL) ||
					schWorkKind.equals(YmCommonConst.NEW_SCH_WORK_KIND_GVF1) ||
					schWorkKind.equals(YmCommonConst.NEW_SCH_WORK_KIND_GVF2) ||
					
					schWorkKind.equals(YmCommonConst.NEW_SCH_WORK_KIND_GTFL) ||
					schWorkKind.equals(YmCommonConst.NEW_SCH_WORK_KIND_GTF1) ||
					schWorkKind.equals(YmCommonConst.NEW_SCH_WORK_KIND_GTF2) ||
					
					schWorkKind.equals(YmCommonConst.NEW_SCH_WORK_KIND_GPFL) ||
					schWorkKind.equals(YmCommonConst.NEW_SCH_WORK_KIND_GPF1) ||
					schWorkKind.equals(YmCommonConst.NEW_SCH_WORK_KIND_GPF2) ||
					
					schWorkKind.equals(YmCommonConst.NEW_SCH_WORK_KIND_CVM2) ||
					schWorkKind.equals(YmCommonConst.NEW_SCH_WORK_KIND_CVM3) ||
					schWorkKind.equals(YmCommonConst.NEW_SCH_WORK_KIND_CVML))
				{
					sSchwBook = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectSchWBook_01";
				}else
				{
					sSchwBook = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectSchWBook";
				}
				/* 작업 예약  Table(TB_YM_WBOOK) : 작업예약 등록 우선 순위가 제일 빠른것 추출
				 * Select MIN(WBOOK_ID) From TB_YM_WBOOK a
				 *  Where YD_GP         = ? --야드구분
				 *    And BAY_GP        = ? --동구분
				 *    And SCH_WORK_KIND = ? --스케줄코드
				 *    AND not exists (select wbook_id from tb_ym_sch where wbook_id = a.wbook_id) 
				 */
				
				JDTORecord WBook = ydEquipDAO.getData(sSchwBook, new Object[]{ ydGp.trim(), bayGp.trim(), schWorkKind.trim() });			
				
				String wbookId = StringHelper.evl(WBook.getFieldString("WBOOK_ID"), "");
				
				logger.println(LogLevel.DEBUG,this, "receiveACCoolOrd()WBOOK_ID: "+wbookId);
				if (wbookId == null || wbookId.equals("")){
					// Error Message EJB Call				
				} 
				else{
					logger.println(LogLevel.DEBUG,this, "wbookId "+wbookId.trim());
					//Coil Schedule EJB Call
					 ejbConn         = new EJBConnector("default","JNDICraneSchReg",this);
					 Boolean isTrue  = (Boolean)ejbConn.trx("callCraneSchInfo",new Class[]{String.class, String.class},new Object[]{ 
					 		           wbookId.trim(), NewCraneNo.trim() });
				}				
			

			
			logger.println(LogLevel.DEBUG,this, "End-receiveACCoolOrd()");
							
			return true;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	    	e.printStackTrace();  
	        throw new EJBServiceException(e);
	    }    
	} 

	/**
	 * 오퍼레이션명 : A,B(냉각장적치)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param sMessage
	 * @return
	 * @throws 
	 */
	public boolean receiveACCoolSch(String sMessage)  throws java.rmi.RemoteException{ 
        
        LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		Logger logger           = new Logger(config);
		logger.println(LogLevel.DEBUG,this,"Start-receiveACCoolSch()");
		
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
			
			int iResult = 0;
			
			Level2Parser level2Parser 	= new Level2Parser();
			JDTORecord jDTORecord 		= level2Parser.parse(sMessage);
			
			receiveACCoolSchBackUp(jDTORecord);
			
			logger.println(LogLevel.DEBUG,this,"End-receiveACCoolSch()");
			
			return true;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	    	e.printStackTrace();  
	        throw new EJBServiceException(e);
	    }    
	}  
	
	
	/**
	 * 오퍼레이션명 : 화면에서 Call (JNDIACCoolOrdReg)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param jDTORecord
	 * @return
	 * @throws 
	 */
	public boolean receiveACCoolSchBackUp(JDTORecord jDTORecord) throws java.rmi.RemoteException{
		LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		Logger logger           = new Logger(config);
		logger.println(LogLevel.DEBUG,this,"Start-receiveACCoolSchBackUp()");
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		YdEquipDAO ydEquipDAO           = new YdEquipDAO();
		CraneSchDAO craneSchDAO 	    = new CraneSchDAO();
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			/*
			 * 보완사항 : 1. 압연실적 발생시 C동 분기 Conveyor 적치열에 저장품을 순서대로 등록하고 작업예약한다.
			 *           2. B동 Crane에서 냉각장 적치요구를 했을시 C동 분기 Conveyor Max에 있는 작업예약 되어 있는 Coil을
			 *              B동으로 이동 후 Coil Schedule을 Call 한다.
			 *           3. 문제점 : 현재 C동 Crane에서 C동 분기 Conveyor에서 C동으로 냉각장 적치요구 전문이 없음에도 불구하고
			 *                      진행반요원과 무전기로 C동 분기 Conveyor 있는 Coil을 C동으로 뺀뒤 실적처리(BackUp)하고 있다.
			 *                      그래서 C동 분기 Conveyor 있는 Coil을 C동으로 빼는 작업중에 B동 Crane이 냉각장적치요구를 발생한다면
			 *                      C동 분기 Conveyor Max에 있는 Coil을 B동으로 이동하는 행위를 해서는 않된다.
			 *                      C동 분기 Conveyor Max에 있는 Coil이 작업중인지를 어떻게 알수 있나?  
			 *  
			 */			
			Boolean isSuccess     = new Boolean(false);
			int iResult           = 0;
			String wbookId        = "";
			String MaxBedStock    = "";
			
			// 야드 Level-2에서  수신한  Crane No			
			String OldCraneNo = StringHelper.evl(jDTORecord.getFieldString("CRANENO"), "");

            /* 수신한 Crane No를  변환
             * SELECT class3_cd   AS EQUIPNO,
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
				throw new EJBServiceException("NewCraneNo Error");
			}
			
			/* Crane No로 설비 Table(TB_YM_EQUIP)을 Read
			 * Crane No로 야드구분, 동구분, 설비 Select
			 * SELECT yd_gp, bay_gp, equip_kind, equip_no  FROM tb_ym_equip WHERE equip_no   = :crane_no AND equip_kind = 'CR' 
			 */

			String sCraneNo   = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.selectCraneNo";
			JDTORecord schRule = ydEquipDAO.getData(sCraneNo, new Object[]{ NewCraneNo.trim() });

			if (schRule == null){
				throw new EJBServiceException("Crane No Error");
			}
			
			String ydGp        = StringHelper.evl(schRule.getFieldString("YD_GP"), "");			
			String bayGp       = StringHelper.evl(schRule.getFieldString("BAY_GP"), "");
			String schWorkKind = "";
			
			/**
			 * 2011.09.21 DCLINOFF 시점에 스케줄을 하나만 올라 가도록 보완
	   		 * 스케쥴 등록 갯수를 체크한다.
	   		 * 몇개 이상이면 스케쥴 등록을 못하게 한다.
	   		 */
	   		int iSchRuleCount = 0; 
	   		String sSchCount  = "0"; 
	   		JDTORecord schRc  = craneSchDAO.getCraneSchCount(ydGp,
	   														bayGp,
	   														YmCommonConst.NEW_SCH_WORK_KIND_CDLO);
	   		if(schRc != null){
	   			sSchCount = StringHelper.evl(schRc.getFieldString("COUNT"), "0");
		    	}
	    	
	    	if(Integer.parseInt(sSchCount) == iSchRuleCount){	    		
	    		logger.println(LogLevel.DEBUG,this, "= A 작업요구=스케쥴 등록을 SKIP처리 한다.");

				/*
				 *	4	크레인 설비상태 idle 셋팅
				 */ 
			   	
	    		String sSchId	  = "";
				{
					JDTORecord craneV = craneSchDAO.getEquipInfoWithEquipNo(ydGp, NewCraneNo);
					 	
				 	if(craneV != null){
				 		sSchId	   = StringHelper.evl(craneV.getFieldString("WBOOK_ID"),"");
				 	}
			 	}
				
				int iReq = craneSchDAO.updateSubCraneEquipStat(ydGp,
														bayGp,
	    											   YmCommonConst.EQUIP_KIND_CR,
	    											   NewCraneNo,
	    											   YmCommonConst.WORK_PROG_STAT_W,
	    											   "");
				logger.println(LogLevel.DEBUG,this,"= A 작업요구=크레인 설비상태 idle 셋팅="+iReq); 	    											   

				/*
				 *	5	스케쥴 정보 초기화
				 */ 

				iReq = craneSchDAO.updateCraneSchStat(sSchId, YmCommonConst.SCH_WORK_STAT_S);
				logger.println(LogLevel.DEBUG,this,"= A 작업요구=스케쥴 정보 초기화="+iReq);		   									   	  

	    	}else {			

				/*
				 * 윤재광 추가 2006.2.4 
				 */
				EJBConnector ejbConn = new EJBConnector("default","JNDICWrkOrdReg",this);
				isSuccess = (Boolean)ejbConn.trx("callCraneSchInfo",new  Class[]{String.class, String.class, String.class, String.class},
									            new Object[]{ydGp.trim(), bayGp.trim(), NewCraneNo.trim(), YmCommonConst.NEW_SCH_WORK_KIND_CDLO});
				
				if (isSuccess.booleanValue() == false){
					return false; 
				}
	    	}
			
			
			if (!bayGp.equals(YmCommonConst.BAY_GP_A)){
				
		    	/*
			     *  3.수신한 Crane No가 어느동에 해당하는 점검
			     *    3-1. B동에 있는 Crane에서 작업요구가 일어났다면 C동 분기 Conveyor-01(1CDC01)의 Max번지 있는 재료를
			     *         B동 분기 Conveyor-01(1BDC01)의 01번지에 Insert하고, C동 분기 Conveyor-01(1CDC01)의 Max번지의 저장품ID를  Delete한다.
			     *    3-2. C동에 있는 Crane에서 작업요구가 일어났다면 C동 분기 Conveyor 01(1CDC01)의 Max번지 있는 재료를
			     *         C동 분기 Conveyor-02(1CDC02)의 01번지에 Insert하고, C동 분기 Conveyor-01(1CDC01)의 Max번지의 저장품ID를  Delete한다.
			     *  4.작업예약(TB_YM_WBOOK) Table Select WBOOK_ID Key 생성 Seq + 1
			     *  5.작업 예약조 계산해서 작업예약(TB_YM_WBOOK) Table Insert(Yard 구분, 동구분, 작업예약일시, 작업예약조, 등록자, 등록일시) 한다.
			     *  6.저장품 Table(TB_YM_STOCK)에 WBOOK_ID를 Update 한다.  				
				 */
				/*-- 적치열 STACK_COL_GP = '1CDC01'과  적치단 STACK_LAYER_GP = '01'에 해당하는  적치 Bed Max 번지와 Coil No Select
				 *  select A.STACK_BED_GP, A.STOCK_ID, A.STACK_LAYER_ACTIVE_STAT, A.STACK_LAYER_STAT
				 *    From USRYMA.TB_YM_STACKLAYER A
				 *   Where A.STACK_BED_GP = (select max(STACK_BED_GP)
				 *                             From USRYMA.TB_YM_STACKLAYER
				 *                            Where STACK_COL_GP = ?
				 *                              And STACK_LAYER_GP = '01'
				 *                              And STACK_LAYER_STAT = 'S'
				 *                              And not exists (select STOCK_ID from USRYMA.TB_YM_SCH where STOCK_ID = A.STOCK_ID )
				 *                           )
				 *     And A.STACK_COL_GP = ?
				 *     And A.STACK_LAYER_GP = '01'
				 *     And  STOCK_ID IS NOT NULL
				 */
				String stackLayQueryMaxId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.selectStackColGpMax";
				JDTORecord StackColGpMax  = ydStackLayerDAO.requestgetData(stackLayQueryMaxId, new Object[]{ 
						                    YmCommonConst.STACK_COL_GP_1CDC01, YmCommonConst.STACK_COL_GP_1CDC01 });

				if (StackColGpMax != null ){
					MaxBedStock    = StringHelper.evl(StackColGpMax.getFieldString("STOCK_ID"), "");  		
					
					schWorkKind           = YmCommonConst.NEW_SCH_WORK_KIND_CDLO; //Coil DC Line Off
					
					// 수신한 Crane No가 어느동에 해당하는지 점검
					if (bayGp.equals(YmCommonConst.BAY_GP_C)){   
						// C동 분기 Conveyor 01(1CDC01)의 Max 번지 있는 재료를 Select
						// C동 분기 Conveyor-02(1CDC02)의 01 번지에 Insert
						   int iSeq1 = YmCommonDB.insertConveyorInfo(
						   		       YmCommonConst.STACK_COL_GP_1CDC02, MaxBedStock.trim(), YmCommonConst.GBN_MIN);
						// C동 분기 Conveyor-01(1CDC01)의 Max 번지의 저장품ID를 Delete
						   int iSeq2 = YmCommonDB.deleteConveyorInfo(
						   		       YmCommonConst.STACK_COL_GP_1CDC01, MaxBedStock.trim());
						   
					}else if(bayGp.equals(YmCommonConst.BAY_GP_B)){
						// C동 분기 Conveyor 01(1CDC01)의 Max 번지 있는 재료를 Select
						// B동 분기 Conveyor-01(1BDC01)의 01 번지에 Insert
						int iSeq1 = YmCommonDB.insertConveyorInfo(
						   		    YmCommonConst.STACK_COL_GP_1BDC01, MaxBedStock.trim(), YmCommonConst.GBN_MIN);
						// C동 분기 Conveyor-01(1CDC01)의 Max 번지의 저장품ID를 Delete
						int iSeq2 = YmCommonDB.deleteConveyorInfo(
						   		    YmCommonConst.STACK_COL_GP_1CDC01, MaxBedStock.trim());
						// C동에서 B동으로 이동시 작업예약에 등록 되어 있는 동을 C동에서 B동으로 Update   
						// Update TB_YM_WBOOK SET BAY_GP = 'B' WHERE STOCK_ID = 
						// (Select WBOOK_ID FROM TB_YM_STOCK WHERE STOCK_ID = MaxBedStock.trim())   
						   String UpdateBayGpWBookQuery = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.UpdateYdWBookBayGp";
						   int updateBayGp = ydWBookDAO.requestupdateData(UpdateBayGpWBookQuery, new Object[]{ MaxBedStock.trim() });
					}	
					
	                // 적치단  Table Update(작업요구상태='S'로 변경)
					// UPDATE TB_YM_STACKLAYER SET STACK_LAYER_STAT = 'S' WHERE STOCK_ID = ?
					String sStkLayerQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark";
					int stkColGp = ydStackLayerDAO.requestupdateData(sStkLayerQueryId, new Object[]{ 
							       YmCommonConst.STACK_LAYER_STAT_S, MaxBedStock.trim() });				
				}
			
				/*-- 적치열 STACK_COL_GP = '1BDC01'과  적치단 STACK_LAYER_GP = '01'에 해당하는  적치 Bed Max 번지와 Coil No Select
				 *  select A.STACK_BED_GP, A.STOCK_ID, A.STACK_LAYER_ACTIVE_STAT, A.STACK_LAYER_STAT
				 *    From USRYMA.TB_YM_STACKLAYER A
				 *   Where A.STACK_BED_GP = (select max(STACK_BED_GP)
				 *                             From USRYMA.TB_YM_STACKLAYER
				 *                            Where STACK_COL_GP = ?
				 *                              And STACK_LAYER_GP = '01'
				 *                              And STACK_LAYER_STAT = 'S'
				 *                              And not exists (select STOCK_ID from USRYMA.TB_YM_SCH where STOCK_ID = A.STOCK_ID )
				 *                           )
				 *     And A.STACK_COL_GP = ?
				 *     And A.STACK_LAYER_GP = '01'
				 *     And  STOCK_ID IS NOT NULL
				 */				
				String stackBLayQueryMaxId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.selectStackColGpMax";
				JDTORecord StackColGpBMax  = ydStackLayerDAO.requestgetData(stackBLayQueryMaxId, new Object[]{ 
						                     YmCommonConst.STACK_COL_GP_1BDC01, YmCommonConst.STACK_COL_GP_1BDC01 });				
				
				if (StackColGpBMax != null ){
					MaxBedStock    = StringHelper.evl(StackColGpBMax.getFieldString("STOCK_ID"), "");  		
				}	
					
				/* 해당 Coil의 작업예약 가져온다.  
                 * Select STOCK_ID, WBOOK_ID from TB_YM_STOCK Where STOCK_ID = ?
                 */ 
				String sStockQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.selectStockID";
				JDTORecord WBook     = ydStockDAO.getData(sStockQueryId, new Object[]{ MaxBedStock.trim() });
				
				wbookId   = StringHelper.evl(WBook.getFieldString("WBOOK_ID"), "");
				
				if (wbookId == null || wbookId.equals("")){
					// Error Message EJB Call				
				}
				else{
 
					EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
					 Boolean isTrue = (Boolean)ejbConn.trx("callCraneSchInfo",new Class[]{String.class, String.class},new Object[]{ 
					 		          wbookId.trim(), NewCraneNo.trim() });
				}
			}
			
			logger.println(LogLevel.DEBUG,this, "End-receiveACCoolSchBackUp()");
							
			return true;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	    	e.printStackTrace();  
	        throw new EJBServiceException(e);
	    }    
	}

	/**
	 * 오퍼레이션명 : 품질에서 Coil 저장이동조건 수신후 저장조건을 정정지시대기 로 변경
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param qMYM002
	 * @return
	 * @throws 
	 */ 
	public boolean receiveCoilStatusChange(QMYM002 qMYM002) { 
		
		boolean isSuccess = false;

		YdStockDAO stockDao 	        = new YdStockDAO();
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			logger.println(LogLevel.DEBUG,this, "Start-receiveCoilStatusChange()");
			
			int iSeq = 0;
			
			String YardID       = qMYM002.getyardID();
			String Stockid      = qMYM002.getcoilNo();
			
			String[] sStockInfo = null;
			
			if(YmCommonConst.YD_GP_1.equals(YardID)||
			   YmCommonConst.YD_GP_3.equals(YardID)){
			   	
			   	sStockInfo = YmCommonUtil.getCoilCurrProgCd(Stockid,"");
			   		
			}else if(YmCommonConst.YD_GP_2.equals(YardID)||
			   		 YmCommonConst.YD_GP_4.equals(YardID)){
			 	
			 	sStockInfo = YmCommonUtil.getSlabCurrProgCd(Stockid,"");  		 	
			}
	     	
	     	String sProgCd   	= sStockInfo[0];
			String sStocMv   	= sStockInfo[1];
			
    		iSeq = stockDao.updateStockTransInfo(Stockid,		// 저장품ID
												 sStocMv);		// 저장품이동조건
	    	
	    	logger.println(LogLevel.DEBUG,this, "품질이동조건 셋팅 => 진도="+sProgCd);
	    	logger.println(LogLevel.DEBUG,this, "품질이동조건 셋팅 => 이동="+sStocMv);
	    	
			logger.println(LogLevel.DEBUG,this, "End-receiveCoilStatusChange()");
			
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return true;
	}	
	
	/**
	 * 오퍼레이션명 : 품질에서 Coil 저장이동조건 수신후 저장조건을 정정지시대기 로 변경
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param qMYM003
	 * @return
	 * @throws 
	 */ 
	public boolean receiveSlabStatusChange(QMYM003 qMYM003) { 
		
		boolean isSuccess = false;

		YdStockDAO stockDao 	        = new YdStockDAO();
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			logger.println(LogLevel.DEBUG,this, "Start-receiveCoilStatusChange()");
			
			int iSeq = 0;
			
			String YardID       = qMYM003.getyardID();
			String Stockid      = qMYM003.getStockId().trim();
			
			String[] sStockInfo = null;
			
			if(YmCommonConst.YD_GP_1.equals(YardID)||
			   YmCommonConst.YD_GP_3.equals(YardID)){
			   	
			   	sStockInfo = YmCommonUtil.getCoilCurrProgCd(Stockid,"");
			   		
			}else if(YmCommonConst.YD_GP_2.equals(YardID)||
			   		 YmCommonConst.YD_GP_4.equals(YardID)){
			 	
			 	sStockInfo = YmCommonUtil.getSlabCurrProgCd(Stockid,"");  		 	
			}
	     	
	     	String sProgCd   	= sStockInfo[0];
			String sStocMv   	= sStockInfo[1];
			
    		iSeq = stockDao.updateStockTransInfo(Stockid,		// 저장품ID
												 sStocMv);		// 저장품이동조건
	    	
	    	logger.println(LogLevel.DEBUG,this, "품질이동조건 셋팅 => 진도="+sProgCd);
	    	logger.println(LogLevel.DEBUG,this, "품질이동조건 셋팅 => 이동="+sStocMv);
	    	
			logger.println(LogLevel.DEBUG,this, "End-receiveCoilStatusChange()");
			
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return true;
	}	
	
	/**
	 * 오퍼레이션명 : 출하에서 대상재가 입고실적등록이 되면 저장이동조건 수신후 저장조건을 수입검사대기 로 변경
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param dMYM010
	 * @return
	 * @throws 
	 */  
	public boolean receiveDMStockMoveConditionStatusChange(DMYM010 dMYM010) { 
		
		boolean isSuccess = false;

		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		CraneSchDAO craneSchDAO         = new CraneSchDAO();
		YdEquipDAO  ydEquipDAO          = new YdEquipDAO();
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			logger.println(LogLevel.DEBUG,this, "Start-receiveDMStockMoveConditionStatusChange()");
			
			int iResult = 0;
			
			String YardID        = dMYM010.getyardID();
			String Stockid       = dMYM010.getstockid();

			// 저장품 Table(TB_YM_STOCK)에 저장품이동조건을 Update 한다.
			String[] sStockInfo         = YmCommonUtil.getSlabCurrProgCd(Stockid.trim(),"");
			
			// 저장품 Table(TB_YM_STOCK)에 WBOOK_ID를 Update 한다.
			// UPDATE TB_YM_STOCK SET STOCK_MOVE_TERM = ? WHERE STOCK_ID = ?
//			String updateYdStockCondition = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockDMCondition";
//			int updateydstockcondition    = ydStockDAO.requestupdateData(updateYdStockCondition, new Object[]{ 
//					                        sStockInfo[1], Stockid.trim() });
			
	    	
			logger.println(LogLevel.DEBUG,this, "End-receiveDMStockMoveConditionStatusChange()");
			
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return true;
	}
	
	/**
	 * 오퍼레이션명 : 조업에서 대상재가 매 하차작업실적 처리후 야드로 송신. 저장이동조건 수신후 저장조건을 (정정작업지시대기/압연지시대기) 로 변경
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param pOYM007
	 * @return
	 * @throws 
	 */   
	public boolean receivePOStockMoveConditionStatusChange(POYM007 pOYM007) { 
		
		boolean isSuccess = false;

		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		CraneSchDAO craneSchDAO         = new CraneSchDAO();
		YdEquipDAO  ydEquipDAO          = new YdEquipDAO();
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			logger.println(LogLevel.DEBUG,this, "Start-receivePOStockMoveConditionStatusChange()");
			
			int iResult = 0;
			
			String YardID       = pOYM007.getyardID();
			String Stockid      = pOYM007.getstockid();
			String Schedulecode = ""; 

			// 저장품 Table(TB_YM_STOCK)에 저장품이동조건을 Update 한다.
			// UPDATE STOCK_MOVE_TERM(저장품이동조건) = ?	 WHERE STOCK_ID = ?
			String updateYdStockCondition  = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockCondition";
			
			String TmpCoilCurrProgCd       = "";
			String TmpSlabCurrProgCd       = "";
			if (YardID.equals(YmCommonConst.YD_GP_1) || YardID.equals(YmCommonConst.YD_GP_3)){
				// ym.common.dao.selectCommonCoilInfo   (Coil 진도코드  CURR_PROG_CD)
				String selectCommonCoilInfo       = "ym.common.dao.selectCommonCoilInfo";
				JDTORecord seletcommoncoilinfo    = ydStockDAO.getData(selectCommonCoilInfo, new Object[]{ Stockid.trim() });
				TmpCoilCurrProgCd                 = StringHelper.evl(seletcommoncoilinfo.getFieldString("CURR_PROG_CD"), "");				
			}else if (YardID.equals(YmCommonConst.YD_GP_2) || YardID.equals(YmCommonConst.YD_GP_4)){
				// ym.common.dao.selectSlabMatirialInfo (Slab 진도코드  CURR_PROG_CD)
				String selectSlabMatirialInfo     = "ym.common.dao.selectSlabMatirialInfo";
				JDTORecord selectslabmatirialinfo = ydStockDAO.getData(selectSlabMatirialInfo, new Object[]{ Stockid.trim() });
				TmpSlabCurrProgCd                 = StringHelper.evl(selectslabmatirialinfo.getFieldString("CURR_PROG_CD"), "");							
			}

			if (YardID.equals(YmCommonConst.YD_GP_1)){                              // A열연 Coil 정정작업지시대기
				String[] sStockInfo = YmCommonUtil.getCoilCurrProgCd(Stockid.trim(),"");
				Schedulecode = sStockInfo[1];
			}else if (YardID.equals(YmCommonConst.YD_GP_2)){                        // B열연 Slab
				String[] sStockInfo = YmCommonUtil.getSlabCurrProgCd(Stockid.trim(),"");
				Schedulecode = sStockInfo[1];
//				if (TmpSlabCurrProgCd.equals("D")){                                 // D:정정작업대기DS  
//					Schedulecode = YmCommonConst.NEW_STOCK_MOVE_TERM_DS;	
//				}else if (TmpSlabCurrProgCd.equals("E")){                           // E:압연지시대기ES
//					Schedulecode = YmCommonConst.NEW_STOCK_MOVE_TERM_ES;
//				}
			}else if (YardID.equals(YmCommonConst.YD_GP_3)){                        // B열연 Coil 정정작업지시대기
				String[] sStockInfo = YmCommonUtil.getCoilCurrProgCd(Stockid.trim(),"");
				Schedulecode = sStockInfo[1];
//				Schedulecode = YmCommonConst.NEW_STOCK_MOVE_TERM_BC;   
			}else if (YardID.equals(YmCommonConst.YD_GP_4)){                        // 부두야드       압연지시대기
				String[] sStockInfo = YmCommonUtil.getSlabCurrProgCd(Stockid.trim(),"");
				Schedulecode = sStockInfo[1];
//				if (TmpSlabCurrProgCd.equals("D")){                                 // D:정정작업대기DS  
//					Schedulecode = YmCommonConst.NEW_STOCK_MOVE_TERM_DS;	
//				}else if (TmpSlabCurrProgCd.equals("E")){                           // E:압연지시대기ES
//					Schedulecode = YmCommonConst.NEW_STOCK_MOVE_TERM_ES;
//				}   				
			}
			
			int updateydstockcondition    = ydStockDAO.requestupdateData(updateYdStockCondition, new Object[]{ 
					                        Schedulecode, Stockid.trim() });
			
			logger.println(LogLevel.DEBUG,this, "End-receivePOStockMoveConditionStatusChange()");
			
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return true;
	}		

	/**
	 * 오퍼레이션명 : 조업에서 공냉재 실적 발생후 종합판정한뒤 야드에게 실적 처리완료 정보를 송신 
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param pOYM008
	 * @return
	 * @throws 
	 */    
	public boolean receivePOCoolStatusChange(POYM008 pOYM008) { 
		
		boolean isSuccess = false;

		YdStockDAO stockDao 	        = new YdStockDAO();
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			logger.println(LogLevel.DEBUG,this, "Start-receivePOCoolStatusChange()");
			
			int iSeq = 0;
			
			String YardID       = pOYM008.getyardID();
			String Stockid      = pOYM008.getstockid();
						
//			코일공통 진도코드 Table 참조.
	     	String[] sStockInfo = YmCommonUtil.getCoilCurrProgCd(Stockid,"");
		    String sProgCd   	= sStockInfo[0];
			String sStocMv   	= sStockInfo[1];
							
	    	if(YmCommonConst.NEW_STOCK_MOVE_TERM_HG.equals(sStocMv)){
	    		
	    		iSeq = stockDao.updateStockTransInfo_06(Stockid,				// 저장품ID
												   		YmCommonConst.ITEM_CG,	// 저장품품목
												   		sStocMv);				// 저장품이동조건
	    	}else{
	    	
	    		iSeq = stockDao.updateStockTransInfo(Stockid,		// 저장품ID
													 sStocMv);		// 저장품이동조건
	    	}
	    	
			logger.println(LogLevel.DEBUG,this, "End-receivePOCoolStatusChange()");
			
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return true;
	}			
	//////////////////////////////////////////////////////////////////////////////
	/////////////////////B열연수정시작///////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////	
	/**
	 * 오퍼레이션명 : 조업에서 대상재가 매 하차작업실적 처리후 야드로 송신. 저장이동조건 수신후 저장조건을 (정정작업지시대기/압연지시대기) 로 변경
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param pOYM007
	 * @return
	 * @throws 
	 */   
	public boolean receivePOYM007(JDTORecord rcvMsg){
//s	public boolean receivePOStockMoveConditionStatusChange(POYM007 pOYM007) { 
		
		boolean isSuccess = false;

		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		CraneSchDAO craneSchDAO         = new CraneSchDAO();
		YdEquipDAO  ydEquipDAO          = new YdEquipDAO();
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			logger.println(LogLevel.DEBUG,this, "ACCoolOrdRegEJB:receivePOYM007()");
			
			int iResult = 0;
			
//S			String YardID       = pOYM007.getyardID();
//S			String Stockid      = pOYM007.getstockid();
			
//			String YardID   = StringHelper.evl(rcvMsg.getFieldString("yardID"),"").trim();    			
//			String Stockid	= StringHelper.evl(rcvMsg.getFieldString("stockid"),"").trim();
			
			String YardID   = StringHelper.evl(rcvMsg.getFieldString("yardID"),"").trim();    			
			String Stockid	= StringHelper.evl(rcvMsg.getFieldString("stockid"),"").trim();

			logger.println(LogLevel.DEBUG,this," yardID 		=" + YardID);
			logger.println(LogLevel.DEBUG,this," stockid 		=" + Stockid);
			
			
			String Schedulecode = ""; 

			// 저장품 Table(TB_YM_STOCK)에 저장품이동조건을 Update 한다.
			// UPDATE STOCK_MOVE_TERM(저장품이동조건) = ?	 WHERE STOCK_ID = ?
			String updateYdStockCondition  = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockCondition";
			
			String TmpCoilCurrProgCd       = "";
			String TmpSlabCurrProgCd       = "";
			if (YardID.equals(YmCommonConst.YD_GP_1) || YardID.equals(YmCommonConst.YD_GP_3)){
				// ym.common.dao.selectCommonCoilInfo   (Coil 진도코드  CURR_PROG_CD)
				String selectCommonCoilInfo       = "ym.common.dao.selectCommonCoilInfo";
				JDTORecord seletcommoncoilinfo    = ydStockDAO.getData(selectCommonCoilInfo, new Object[]{ Stockid.trim() });
				TmpCoilCurrProgCd                 = StringHelper.evl(seletcommoncoilinfo.getFieldString("CURR_PROG_CD"), "");				
			}else if (YardID.equals(YmCommonConst.YD_GP_2) || YardID.equals(YmCommonConst.YD_GP_4)){
				// ym.common.dao.selectSlabMatirialInfo (Slab 진도코드  CURR_PROG_CD)
				String selectSlabMatirialInfo     = "ym.common.dao.selectSlabMatirialInfo";
				JDTORecord selectslabmatirialinfo = ydStockDAO.getData(selectSlabMatirialInfo, new Object[]{ Stockid.trim() });
				TmpSlabCurrProgCd                 = StringHelper.evl(selectslabmatirialinfo.getFieldString("CURR_PROG_CD"), "");							
			}

			if (YardID.equals(YmCommonConst.YD_GP_1)){                              // A열연 Coil 정정작업지시대기
				String[] sStockInfo = YmCommonUtil.getCoilCurrProgCd(Stockid.trim(),"");
				Schedulecode = sStockInfo[1];
			}else if (YardID.equals(YmCommonConst.YD_GP_2)){                        // B열연 Slab
				String[] sStockInfo = YmCommonUtil.getSlabCurrProgCd(Stockid.trim(),"");
				Schedulecode = sStockInfo[1];
//				if (TmpSlabCurrProgCd.equals("D")){                                 // D:정정작업대기DS  
//					Schedulecode = YmCommonConst.NEW_STOCK_MOVE_TERM_DS;	
//				}else if (TmpSlabCurrProgCd.equals("E")){                           // E:압연지시대기ES
//					Schedulecode = YmCommonConst.NEW_STOCK_MOVE_TERM_ES;
//				}
			}else if (YardID.equals(YmCommonConst.YD_GP_3)){                        // B열연 Coil 정정작업지시대기
				String[] sStockInfo = YmCommonUtil.getCoilCurrProgCd(Stockid.trim(),"");
				Schedulecode = sStockInfo[1];
//				Schedulecode = YmCommonConst.NEW_STOCK_MOVE_TERM_BC;   
			}else if (YardID.equals(YmCommonConst.YD_GP_4)){                        // 부두야드       압연지시대기
				String[] sStockInfo = YmCommonUtil.getSlabCurrProgCd(Stockid.trim(),"");
				Schedulecode = sStockInfo[1];
//				if (TmpSlabCurrProgCd.equals("D")){                                 // D:정정작업대기DS  
//					Schedulecode = YmCommonConst.NEW_STOCK_MOVE_TERM_DS;	
//				}else if (TmpSlabCurrProgCd.equals("E")){                           // E:압연지시대기ES
//					Schedulecode = YmCommonConst.NEW_STOCK_MOVE_TERM_ES;
//				}   				
			}
			
			int updateydstockcondition    = ydStockDAO.requestupdateData(updateYdStockCondition, new Object[]{ 
					                        Schedulecode, Stockid.trim() });
			
			logger.println(LogLevel.DEBUG,this, "End-receivePOStockMoveConditionStatusChange()");
			
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return true;
	}			
	
	/**
	 * 오퍼레이션명 : 조업에서 공냉재 실적 발생후 종합판정한뒤 야드에게 실적 처리완료 정보를 송신 
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param pOYM008
	 * @return
	 * @throws 
	 */    
	public boolean receivePOYM008(JDTORecord rcvMsg){
//	public boolean receivePOCoolStatusChange(POYM008 pOYM008) { 
		
		boolean isSuccess = false;

		YdStockDAO stockDao 	        = new YdStockDAO();
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			logger.println(LogLevel.DEBUG,this, "ACCoolOrdRegEJB:receivePOYM008()");
			
			int iSeq = 0;
			
//S			String YardID       = pOYM008.getyardID();
//S			String Stockid      = pOYM008.getstockid();
//			String YardID   = StringHelper.evl(rcvMsg.getFieldString("yardID"),"").trim();    			
//			String Stockid	= StringHelper.evl(rcvMsg.getFieldString("stockid"),"").trim();

			String YardID   = StringHelper.evl(rcvMsg.getFieldString("yardID"),"").trim();    			
			String Stockid	= StringHelper.evl(rcvMsg.getFieldString("stockid"),"").trim();
			
			logger.println(LogLevel.DEBUG,this," yardID 	=" + YardID);
			logger.println(LogLevel.DEBUG,this," stockid 	=" + Stockid);
			
//			코일공통 진도코드 Table 참조.
	     	String[] sStockInfo = YmCommonUtil.getCoilCurrProgCd(Stockid,"");
		    String sProgCd   	= sStockInfo[0];
			String sStocMv   	= sStockInfo[1];
							
	    	if(YmCommonConst.NEW_STOCK_MOVE_TERM_HG.equals(sStocMv)){
	    		
	    		iSeq = stockDao.updateStockTransInfo_06(Stockid,				// 저장품ID
												   		YmCommonConst.ITEM_CG,	// 저장품품목
												   		sStocMv);				// 저장품이동조건
	    	}else{
	    	
	    		iSeq = stockDao.updateStockTransInfo(Stockid,		// 저장품ID
													 sStocMv);		// 저장품이동조건
	    	}
	    	
			logger.println(LogLevel.DEBUG,this, "End-receivePOCoolStatusChange()");
			
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return true;
	}		
}
