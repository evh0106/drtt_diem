package com.inisteel.cim.ym.scheduling.cts.session;

import java.util.List;

import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
import com.inisteel.cim.common.level2.util.*;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.log.*;
import jspeed.base.util.StringHelper;
/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="CTSSchRegEJB" jndi-name="JNDICTSSchReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class CTSSchRegSBean extends BaseSessionBean {		
	private Logger logger 			= null;
	private ymCommonDAO ymCommonDAO = null;
	private CraneSchDAO dao 		= null;
	private YmComm ymComm = new YmComm();
	
	public void ejbCreate() {
		LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		logger 		= new Logger(config);
		ymCommonDAO = new ymCommonDAO();
		dao 		= new CraneSchDAO();
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * CTS 작업지시 Call
     	 * A열연 Saddle 권하시 CTS에게 작업지시 전문을 Call
        *
        * param String 	: Saddle 설비구분
        * param String 	: 저장품ID
        * param String 	: 1-권하시점에 호출, 2-중계구역에서 호출
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean callCtsWorkInfo(String sStackColGp,String sCoilNo,String sGbn){
		
		Boolean isSuccess = new Boolean(false);
    
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String sFrtomoveEquipGp 	= "";   // CTS이동설비
			String sCtsRelaySaddle		= "";   // 목적Saddle
			String sStockId 			= "";	// 저장품ID
			String sCtsRelayYn 			= "";	// CTS중계구분
			String sCtsRelayBay 		= "";	// CTS중계동
			String sCarunloadBay 		= "";	// 하차PUT위치
			String sFromBay				= "";
			String sToBay				= "";
			
			logger.println(LogLevel.DEBUG,this, "=====CTS 작업지시 EJB======");
			logger.println(LogLevel.DEBUG,this, "sStackColGp="	+ sStackColGp);
			logger.println(LogLevel.DEBUG,this, "sCoilNo="		+ sCoilNo);
			logger.println(LogLevel.DEBUG,this, "sGbn="			+ sGbn);
			
			/**
			 * 저장품Table에서 CTS관련 항목들을 가져온다.
			 */
			JDTORecord stockRc = dao.getStockInfo(sCoilNo);
			
    		if(stockRc != null){
    			
    			sStockId 		= StringHelper.evl(stockRc.getFieldString("STOCK_ID"), "");			//저장품ID
    			sCarunloadBay	= StringHelper.evl(stockRc.getFieldString("CARUNLOAD_PUT_LOC"), "");//하차PUT위치
    		}
    		
    		if("".equals(sCarunloadBay)){
				 throw new EJBServiceException("=CTS 작업지시=>목적동 정보를 찾을 수 없습니다.");
			}
			
			/**
			 * 중계구분항목을 검색해서, 작업지시할
			 * 이동설비, 목적동 정보를 가져온다.
			 */
			String[] ctsInfo = getFrtomoveEquipGp(sStackColGp,sCarunloadBay);
    		
    		logger.println(LogLevel.DEBUG,this, "=====CTS 작업지시 검색결과======");
			logger.println(LogLevel.DEBUG,this, "이동설비	="	+ ctsInfo[0]);
			logger.println(LogLevel.DEBUG,this, "출발동		="	+ ctsInfo[1]);
			logger.println(LogLevel.DEBUG,this, "목적동		="	+ ctsInfo[2]);
			logger.println(LogLevel.DEBUG,this, "중계사용	="	+ ctsInfo[3]);
    		
    		if(ctsInfo[2] == null  || "".equals(ctsInfo[2])){
				 throw new EJBServiceException("=CTS 작업지시=>목적동 정보를 찾을 수 없습니다.");
			}
			  
    		List toSaddleList = dao.getCtsSaddleToLocInfo(YmCommonConst.YD_GP_1,
												   		  ctsInfo[2]);
    		
    		if(toSaddleList == null||
    		   toSaddleList.size() == 0	){
				 throw new EJBServiceException("=CTS 작업지시=>TO SADDLE LIST 정보를 찾을 수 없습니다.");
			}
    		
    		String[] aToSkid = getToSaddleInfo(toSaddleList);
				
			if(aToSkid[1] == null || "".equals(aToSkid[1])){
				 throw new EJBServiceException("=CTS 작업지시=>TO SADDLE LOC을 찾을 수 없습니다.");
			}
			
			logger.println(LogLevel.DEBUG,this, "NOW SKID ="+ aToSkid[0]);
			logger.println(LogLevel.DEBUG,this, "NEXT SKID="+ aToSkid[1]);
			
			String sFromSkid 	= sStackColGp;
			String sToSkid		= aToSkid[1];
			/**
			 * 해당 Saddle 설비정보에 '*'을 마크한다.
			 * tb_ym_equip Table cts_relay_bay : '*'
			 */ 
			{
				int iReq = -1;
				
		    	iReq = dao.updateEquipSaddleInfo(aToSkid[0],"");
		    	
		    	iReq = dao.updateEquipSaddleInfo(aToSkid[1],"*");
			}
			/**
			 * CTS SADDLE 목적동 정보를 셋팅한다.
			 * tb_ym_equip Table carunload_stop_loc : 목적동
			 */ 
			{
				int iReq = -1;
				
		    	iReq = dao.updateEquipPutLocWithEquipGp(sFromSkid,sToSkid);
			}
			
			logger.println(LogLevel.DEBUG,this, "FROM_LOC	="+ sFromSkid);
			logger.println(LogLevel.DEBUG,this, "TO_LOC		="+ sToSkid);
			
			JDTORecord skidRc1 = dao.getLegacyEquipNoWithCurEquipNo(sFromSkid);
    		if(skidRc1 != null){
    			sFromSkid = StringHelper.evl(skidRc1.getFieldString("EQUIP_GP"), "");
    		}
    		
    		JDTORecord skidRc2 = dao.getLegacyEquipNoWithCurEquipNo(sToSkid);
    		if(skidRc2 != null){
    			sToSkid   = StringHelper.evl(skidRc2.getFieldString("EQUIP_GP"), "");
    		}
    		
    		logger.println(LogLevel.DEBUG,this, "LEGACY_FROM_LOC	="+ sFromSkid);
			logger.println(LogLevel.DEBUG,this, "LEGACY_TO_LOC		="+ sToSkid);
			
			/**
			 * CTS 작업지시 이동설비,목적동,중계구분 관련 정보를
			 * 저장품TABLE에 셋팅한다.
			 * tb_ym_stock Table frtomove_equip_gp : CTS이동설비
			 * tb_ym_stock Table cts_relay_saddle  : 목적Saddle
			 * tb_ym_stock Table cts_relay_yn  	   : 중계구분항목
			 */ 
			{
				int iReq = -1;
				
		    	iReq = dao.updateStockMoveEquipInfo(sStockId,
		    								   		ctsInfo[0],
		    								   		YmCommonConst.STACK_BED_GP_01,
		    								   		YmCommonConst.STACK_LAYER_GP_01,
		    								   		aToSkid[1],
		    								   		ctsInfo[3]);
			}
			
			String sMessage = setCtsACoilMsgInfo(sStockId,sFromSkid,sToSkid);
			
			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
			isSuccess = (Boolean)ejbConn.trx("THHT400send",new Class[]{String.class},new Object[]{ sMessage });	
    		
    					 
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue(); 
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * CTS 작업지시 재송신
        * A열연 Saddle 권하시 CTS에게 작업지시 전문을 Call
        *
        * param String 	: Saddle 설비구분
        * param String 	: 저장품ID
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
	public boolean reCallCtsWorkInfo(String sStackColGp,String sCoilNo){
		
		Boolean isSuccess = new Boolean(false);
    
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String sCtsRelaySaddle		= "";   // 목적Saddle
			
			logger.println(LogLevel.DEBUG,this, "=====CTS 작업지시 EJB======");
			logger.println(LogLevel.DEBUG,this, "sStackColGp="	+ sStackColGp);
			logger.println(LogLevel.DEBUG,this, "sCoilNo="		+ sCoilNo);
			
			/**
			 * 저장품Table에서 CTS관련 항목들을 가져온다.
			 */
			JDTORecord stockRc = dao.getStockInfo(sCoilNo);
			
    		if(stockRc != null){
    			
    			sCtsRelaySaddle	= StringHelper.evl(stockRc.getFieldString("CTS_RELAY_SADDLE"), "");//목적동
    		}
    		
    		if("".equals(sCtsRelaySaddle)){
				 throw new EJBServiceException("=CTS 작업지시=>작업지시를 재송신 할 수 없습니다.");
			}
			
			String sFromSkid = sStackColGp;
			String sToSkid   = sCtsRelaySaddle;
			
			logger.println(LogLevel.DEBUG,this, "FROM_LOC	="+ sFromSkid);
			logger.println(LogLevel.DEBUG,this, "TO_LOC		="+ sToSkid);
			
			JDTORecord skidRc1 = dao.getLegacyEquipNoWithCurEquipNo(sFromSkid);
    		if(skidRc1 != null){
    			sFromSkid = StringHelper.evl(skidRc1.getFieldString("EQUIP_GP"), "");
    		}
    		
    		JDTORecord skidRc2 = dao.getLegacyEquipNoWithCurEquipNo(sToSkid);
    		if(skidRc2 != null){
    			sToSkid   = StringHelper.evl(skidRc2.getFieldString("EQUIP_GP"), "");
    		}
    		
    		logger.println(LogLevel.DEBUG,this, "LEGACY_FROM_LOC	="+ sFromSkid);
			logger.println(LogLevel.DEBUG,this, "LEGACY_TO_LOC		="+ sToSkid);
			
			String sMessage = setCtsACoilMsgInfo(sCoilNo,sFromSkid,sToSkid);
			
			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
			isSuccess = (Boolean)ejbConn.trx("THHT400send",new Class[]{String.class},new Object[]{ sMessage });	
    		    					 
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue(); 
	}
	
	
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * CTS 상태정보 송신
        * A열연 Saddle 권하시 CTS에게 작업지시 전문을 Call
        *
        * param String 	: Saddle 설비구분
 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
	public boolean reCallCtsWorkInfo2(String sStackColGp){
		
		Boolean isSuccess = new Boolean(false);
		String sSaddleName ="";
		String sSaddleUseYn ="";
		String sSaddleUsage ="";
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			logger.println(LogLevel.DEBUG,this, "=====CTS 작업지시 EJB======");
			logger.println(LogLevel.DEBUG,this, "sStackColGp="	+ sStackColGp);

			
			/**
			 * 설비Table에서 CTS관련 항목들을 가져온다.
			 */
			JDTORecord stockRc = ymCommonDAO.readSkidStatInfoRE(YmCommonConst.YD_GP_1, sStackColGp);
 
			
    		if(stockRc != null){
    			
    			sSaddleName	= StringHelper.evl(stockRc.getFieldString("SADDLE_NAME"), ""); 
    			sSaddleUseYn	= StringHelper.evl(stockRc.getFieldString("EQUIP_STAT"), ""); 
    			sSaddleUsage	= StringHelper.evl(stockRc.getFieldString("STACK_COL_USAGE_CD"), ""); 
    		}
 

    		logger.println(LogLevel.DEBUG,this, "sSaddleName	="+ sSaddleName);
			logger.println(LogLevel.DEBUG,this, "sSaddleUseYn	="+ sSaddleUseYn);
			logger.println(LogLevel.DEBUG,this, "sSaddleUsage	="+ sSaddleUsage);
			
			String sMessage = setCtsACoilMsgInfo2(sSaddleName,sSaddleUseYn,sSaddleUsage);
			
			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
			isSuccess = (Boolean)ejbConn.trx("THHT410send",new Class[]{String.class},new Object[]{ sMessage });	
    		    					 
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue(); 
	}
	
	
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * CTS 상태정보 송신
        * A열연 Saddle 권하시 CTS에게 작업지시 전문을 Call
        *
        * param String 	: Saddle 설비구분
 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
	public boolean reCallCtsWorkInfoAll(String sStackColGp){
		
		Boolean isSuccess = new Boolean(false);
		ymCommonDAO dao = ymCommonDAO.getInstance();
		String sSaddleName ="";
		String sSaddleUseYn ="";
		String sSaddleUsage ="";
		String trnQueryId	="";
		
		List FrtostlList1 = null;
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
 
			
			logger.println(LogLevel.DEBUG,this, "=====CTS 작업지시 EJB======");
			logger.println(LogLevel.DEBUG,this, "sStackColGp="	+ sStackColGp);

			
			/**
			 * 설비Table에서 CTS관련 항목들을 가져온다.
			 */ 
 			trnQueryId 		= "ym.common.dao.selectSkidIniInfoREAll";
			FrtostlList1 	= dao.getCommonList(trnQueryId, new Object[]{YmCommonConst.YD_GP_1});
	
	
			int iSeqCount 	= FrtostlList1.size();

			for(int i=0; i < iSeqCount ; i++){
		
				JDTORecord FrtoSltrec = (JDTORecord)FrtostlList1.get(i); 
				
				sSaddleName		= StringHelper.evl(FrtoSltrec.getFieldString("SADDLE_NAME"), ""); 
    			sSaddleUseYn	= StringHelper.evl(FrtoSltrec.getFieldString("EQUIP_STAT"), ""); 
    			sSaddleUsage	= StringHelper.evl(FrtoSltrec.getFieldString("STACK_COL_USAGE_CD"), "");     			
    			
    			logger.println(LogLevel.DEBUG,this, i+"sSaddleName	="+ sSaddleName);
    			logger.println(LogLevel.DEBUG,this, i+"sSaddleUseYn	="+ sSaddleUseYn);
    			logger.println(LogLevel.DEBUG,this, i+"sSaddleUsage	="+ sSaddleUsage);
    			
    			String sMessage = setCtsACoilMsgInfo2(sSaddleName,sSaddleUseYn,sSaddleUsage);
    			
    			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
    			isSuccess = (Boolean)ejbConn.trx("THHT410send",new Class[]{String.class},new Object[]{ sMessage });	    			    			
			}
		 

    		
    		    					 
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue(); 
	}
	
	/**
     *  Saddle From위치와 To위치를 가지고 해당 코일의
     *  CTS 이동설비를 셋팅한다.
     *
     * @param dao 	 : DAO
     * @param String : From Saddle
     * @param String : To Saddle
     *
     * @return
     * @throws 
     */	 
	private String[] getFrtomoveEquipGp(String FromSkid,
									    String ToSkid){
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		String[] ctsInfo = new String[4];
		
		String FromBay = (FromSkid.length() > 2)? FromSkid.substring(1,2):"";
		String ToBay   = (ToSkid.length()   > 2)? ToSkid.substring(1,2)  :ToSkid;						  	 
		
		try{
			if(!"".equals(FromBay) && !"".equals(ToBay)){
				JDTORecord tcRc1 = ymCommonDAO.readEquipInfo(YmCommonConst.CTS_1XTC01);
				JDTORecord tcRc2 = ymCommonDAO.readEquipInfo(YmCommonConst.CTS_1XTC02);
				
				String sEquipStat1 	= StringHelper.evl(tcRc1.getFieldString("EQUIP_STAT"), "");		//설비상태('O','C')
				String sUseYn1 		= StringHelper.evl(tcRc1.getFieldString("CTS_RELAY_YN"), "");	//CTS 중계 구역 사용 유무
				String sBay1 		= StringHelper.evl(tcRc1.getFieldString("CTS_RELAY_BAY"), "");	//CTS 중계 구역 동
				
				String sEquipStat2 	= StringHelper.evl(tcRc2.getFieldString("EQUIP_STAT"), "");		//설비상태('O','C')
				String sUseYn2 		= StringHelper.evl(tcRc2.getFieldString("CTS_RELAY_YN"), "");	//CTS 중계 구역 사용 유무
				String sBay2 		= StringHelper.evl(tcRc2.getFieldString("CTS_RELAY_BAY"), "");	//CTS 중계 구역 동
				
				logger.println(LogLevel.DEBUG,this, "FromBay	="+FromBay);
				logger.println(LogLevel.DEBUG,this, "ToBay		="+ToBay);
				logger.println(LogLevel.DEBUG,this, "sEquipStat1="+sEquipStat1);
				logger.println(LogLevel.DEBUG,this, "sUseYn1	="+sUseYn1);
				logger.println(LogLevel.DEBUG,this, "sBay1		="+sBay1);
				logger.println(LogLevel.DEBUG,this, "sEquipStat2="+sEquipStat2);
				logger.println(LogLevel.DEBUG,this, "sUseYn2	="+sUseYn2);
				logger.println(LogLevel.DEBUG,this, "sBay2		="+sBay2);
				
				//중계구역이 설정되어 있는 경우
				if(YmCommonConst.CTS_RELAY_YN_Y.equals(sUseYn1) && 
				   YmCommonConst.CTS_RELAY_YN_Y.equals(sUseYn2)){
				   
				   if(FromBay.equals(sBay2)){
						if(ToBay.compareTo(sBay2) > 0){
							//예 E -> F,G,H	
							if(YmCommonConst.EQUIP_STAT_O.equals(sEquipStat2)){
								ctsInfo[0] = YmCommonConst.CTS_1XTC02;
								ctsInfo[1] = FromBay;
								ctsInfo[2] = ToBay;
								ctsInfo[3] = YmCommonConst.CTS_RELAY_GP_N;
								logger.println(LogLevel.DEBUG,this, "===========");
								logger.println(LogLevel.DEBUG,this, "E -> F,G,H	");
								logger.println(LogLevel.DEBUG,this, "===========");
							}
						}else{		
							//예 E -> A,B,C,D
							if(YmCommonConst.EQUIP_STAT_O.equals(sEquipStat1)){
								ctsInfo[0] = YmCommonConst.CTS_1XTC01;
								ctsInfo[1] = FromBay;
								ctsInfo[2] = ToBay;
								ctsInfo[3] = YmCommonConst.CTS_RELAY_GP_N;
								logger.println(LogLevel.DEBUG,this, "===========");
								logger.println(LogLevel.DEBUG,this, "E -> A,B,C,D");
								logger.println(LogLevel.DEBUG,this, "===========");
							}
						}	
				   }else if(ToBay.equals(sBay2)){		
						if(FromBay.compareTo(sBay2) > 0){
						   	//예 F,G,H	-> E
						   	if(YmCommonConst.EQUIP_STAT_O.equals(sEquipStat2)){
								ctsInfo[0] = YmCommonConst.CTS_1XTC02;
								ctsInfo[1] = FromBay;
								ctsInfo[2] = ToBay;
								ctsInfo[3] = YmCommonConst.CTS_RELAY_GP_N;
								logger.println(LogLevel.DEBUG,this, "===========");
								logger.println(LogLevel.DEBUG,this, "F,G,H	-> E");
								logger.println(LogLevel.DEBUG,this, "===========");
							}
						}else{		
						   	//예 A,B,C,D-> E
						   	if(YmCommonConst.EQUIP_STAT_O.equals(sEquipStat1)){
								ctsInfo[0] = YmCommonConst.CTS_1XTC01;
								ctsInfo[1] = FromBay;
								ctsInfo[2] = ToBay;
								ctsInfo[3] = YmCommonConst.CTS_RELAY_GP_N;
								logger.println(LogLevel.DEBUG,this, "===========");
								logger.println(LogLevel.DEBUG,this, "A,B,C,D-> E");
								logger.println(LogLevel.DEBUG,this, "===========");
							}
						}		
				   }else{
				   		if(FromBay.compareTo(sBay2) < 0 &&
						     ToBay.compareTo(sBay2) > 0 ){
							//예 A,B,C,D-> F,G,H
							if(YmCommonConst.EQUIP_STAT_O.equals(sEquipStat1)){
					   			ctsInfo[0] = YmCommonConst.CTS_1XTC01;
					   			ctsInfo[1] = FromBay;
								ctsInfo[2] = sBay2;
								ctsInfo[3] = YmCommonConst.CTS_RELAY_GP_Y;
								logger.println(LogLevel.DEBUG,this, "===========");
					   			logger.println(LogLevel.DEBUG,this, "A,B,C,D-> F,G,H");
					   			logger.println(LogLevel.DEBUG,this, "===========");
					   		}		
						}else if(FromBay.compareTo(sBay2) > 0 &&
						     	   ToBay.compareTo(sBay2) < 0 ){
							//예 F,G,H -> A,B,C,D 
							if(YmCommonConst.EQUIP_STAT_O.equals(sEquipStat2)){
					   			ctsInfo[0] = YmCommonConst.CTS_1XTC02;
					   			ctsInfo[1] = FromBay;
								ctsInfo[2] = sBay2;
								ctsInfo[3] = YmCommonConst.CTS_RELAY_GP_Y;
								logger.println(LogLevel.DEBUG,this, "===========");
					   			logger.println(LogLevel.DEBUG,this, "F,G,H -> A,B,C,D");
					   			logger.println(LogLevel.DEBUG,this, "===========");
					   		}	
						}else if(FromBay.compareTo(sBay2) > 0 &&
						     	   ToBay.compareTo(sBay2) > 0 ){
				   	   		//예 F,G,H-> F,G,H
					   	   	if(YmCommonConst.EQUIP_STAT_O.equals(sEquipStat2)){
					   			ctsInfo[0] = YmCommonConst.CTS_1XTC02;
					   			ctsInfo[1] = FromBay;
								ctsInfo[2] = ToBay;
								ctsInfo[3] = YmCommonConst.CTS_RELAY_GP_N;
								logger.println(LogLevel.DEBUG,this, "===========");
					   			logger.println(LogLevel.DEBUG,this, "F,G,H-> F,G,H");
					   			logger.println(LogLevel.DEBUG,this, "===========");
					   		} 	
				   	    }else if(FromBay.compareTo(sBay2) < 0 &&
						     	   ToBay.compareTo(sBay2) < 0 ){
				   	   		//예 A,B,C,D-> A,B,C,D
					   	   	if(YmCommonConst.EQUIP_STAT_O.equals(sEquipStat1)){
					   			ctsInfo[0] = YmCommonConst.CTS_1XTC01;
					   			ctsInfo[1] = FromBay;
								ctsInfo[2] = ToBay;
								ctsInfo[3] = YmCommonConst.CTS_RELAY_GP_N;
								logger.println(LogLevel.DEBUG,this, "===========");
					   			logger.println(LogLevel.DEBUG,this, "A,B,C,D-> A,B,C,D");
					   			logger.println(LogLevel.DEBUG,this, "===========");
					   		} 
				   	   }
				   }
				   
				//중계구역이 설정되어 있지 않은 경우
				}else if(YmCommonConst.CTS_RELAY_YN_N.equals(sUseYn1) && 
				   		 YmCommonConst.CTS_RELAY_YN_N.equals(sUseYn2)){

					if(YmCommonConst.EQUIP_STAT_O.equals(sEquipStat1)){
						ctsInfo[0] = YmCommonConst.CTS_1XTC01;
						ctsInfo[1] = FromBay;
						ctsInfo[2] = ToBay;
						ctsInfo[3] = YmCommonConst.CTS_RELAY_GP_N;
						logger.println(LogLevel.DEBUG,this, "===========");
						logger.println(LogLevel.DEBUG,this, "미설정 1XTC01");
						logger.println(LogLevel.DEBUG,this, "===========");
					}else if(YmCommonConst.EQUIP_STAT_O.equals(sEquipStat2)){
						ctsInfo[0] = YmCommonConst.CTS_1XTC02;
						ctsInfo[1] = FromBay;
						ctsInfo[2] = ToBay;
						ctsInfo[3] = YmCommonConst.CTS_RELAY_GP_N;
						logger.println(LogLevel.DEBUG,this, "===========");
						logger.println(LogLevel.DEBUG,this, "미설정 1XTC02");
						logger.println(LogLevel.DEBUG,this, "===========");
					}
				}   	
			}
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    
	    return ctsInfo;
	}
	
	/**
     *  CTS 에 지시할 전문MESSAGE를 구성한다.
     *  야드L3	Target	CTS L2	I/F방법	JMS	I/F주기	REQ	I/F유형	OnLine
 	 *	T/C	THHT400		
	 *	1	전문코드	TC			CHAR	전문코드	07
	 *	2	coil no		CoilNo		CHAR	CoilNo  	10
	 *	3	From Skid	FromSkid	CHAR	FromSkid	05
	 *	4	To Skid		ToSkid		CHAR	ToSkid  	05
	 *	5	filler		Filler		CHAR	Filler  	13
	 * @param schInfo : CTS 작업지시 INFO
     *
     * @return
     * @throws 
     */	 
	private String setCtsACoilMsgInfo(String CoilNo,
									  String FromSkid,
									  String ToSkid
									  ){
		
		StringBuffer sMsg = new StringBuffer();

		String TC			= ""; 
		String Filler		= ""; 
				
		int iTC				=  7;
		int iCoilNo			= 10;
		int iFromSkid		=  5;
		int iToSkid			=  5;
		int iFiller			= 23;
						   
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			//VALUE SETTING
			TC				= YmCommonConst.TC_THHT400;
			
			sMsg.append(YmCommonUtil.FillToString(TC		,iTC));
			sMsg.append(YmCommonUtil.FillToString(CoilNo	,iCoilNo));
			sMsg.append(YmCommonUtil.FillToString(FromSkid	,iFromSkid));
			sMsg.append(YmCommonUtil.FillToString(ToSkid	,iToSkid));
			sMsg.append(YmCommonUtil.FillToString(Filler	,iFiller));
			
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return sMsg.toString();
	}
	
	
	/**
     *  CTS 에 지시할 전문MESSAGE를 구성한다.
     *  야드L3	Target	CTS L2	I/F방법	JMS	I/F주기	REQ	I/F유형	OnLine
 	 *	T/C	THHT410		 
	 * @param schInfo : CTS 작업지시 INFO
     *
     * @return
     * @throws 
     */	 
	private String setCtsACoilMsgInfo2(String SaddleName,
									  String SaddleUseYn,
									  String SaddleUsage
									  ){
		
		StringBuffer sMsg = new StringBuffer();

		String TC			= ""; 
		String Filler		= ""; 
				
		int iTC				=  7;
		int iSaddleName		=  5;
		int iSaddleUseYn	=  1;
		int iSaddleUsage	=  1;
		int iFiller			= 36;
						   
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			//VALUE SETTING
			TC				= YmCommonConst.TC_THHT410;
			
			sMsg.append(YmCommonUtil.FillToString(TC			,iTC));
			sMsg.append(YmCommonUtil.FillToString(SaddleName	,iSaddleName));
			sMsg.append(YmCommonUtil.FillToString(SaddleUsage	,iSaddleUsage));
			sMsg.append(YmCommonUtil.FillToString(SaddleUseYn	,iSaddleUseYn));			
			sMsg.append(YmCommonUtil.FillToString(Filler		,iFiller));
			
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return sMsg.toString();
	}
	
	/**
     * Saddle 정보중에 순환적치로 가능한 Saddle 정보를 가져온다.
     *
     * @return
     * @throws 
     */	 
	private String[] getToSaddleInfo(List listInfo){
		
		String[] aVal = new String[2];
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			JDTORecord saddleV  = null;
			String sFlag    	= "";
			String sEquipGp 	= "";
			
			for(int inx = 0; inx < listInfo.size() ; inx++){
			 
				saddleV    = (JDTORecord)listInfo.get(inx);
				sFlag      = StringHelper.evl(saddleV.getFieldString("FLAG"), "");
				sEquipGp   = StringHelper.evl(saddleV.getFieldString("EQUIP_GP"), "");
				
				if("*".equals(sFlag)){
					aVal[0] = sEquipGp;
					if(inx == listInfo.size()-1){
						saddleV = (JDTORecord)listInfo.get(0);
						aVal[1] = StringHelper.evl(saddleV.getFieldString("EQUIP_GP"), "");
					}else{
					    saddleV = (JDTORecord)listInfo.get(inx+1);
						aVal[1] = StringHelper.evl(saddleV.getFieldString("EQUIP_GP"), "");	
					}
					break;
				}
			}	
			
			if("".equals(sFlag)){
				saddleV = (JDTORecord)listInfo.get(0);
				aVal[0] = StringHelper.evl(saddleV.getFieldString("EQUIP_GP"), "");
				aVal[1] = StringHelper.evl(saddleV.getFieldString("EQUIP_GP"), "");	
			}
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return aVal;
	}
}

