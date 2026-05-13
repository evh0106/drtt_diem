package com.inisteel.cim.ym.workrequest.wrequestaccept.session;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.log.LogService;
import jspeed.base.log.LogServiceConfig;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.util.StringHelper;
import jspeed.base.util.DateHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.common.parser.Level2Parser;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdEquipDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO;
import com.inisteel.cim.ym.steelinfo.steelinforecv.dao.YdStockDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO;
import com.inisteel.cim.ym.workrequest.wrequestaccept.dao.YdWBookDAO;
import com.inisteel.cim.common.jms.model.CommonModel;
import com.inisteel.cim.common.jms.model.ym.POYM009;
import com.inisteel.cim.common.jms.model.po.YMPO164;
import com.inisteel.cim.common.jms.model.pm.ZZPM001;
import com.inisteel.cim.common.jms.model.dm.YMDM001;

import com.inisteel.cim.common.util.CommonUtil;

import com.inisteel.cim.ym.workrequest.wrequestaccept.dao.CoolWrsltProcDAO;
/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="CTnkSpplWrkOrdRegEJB" jndi-name="JNDICTnkSpplWrkOrdReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class CTnkSpplWrkOrdRegSBean extends BaseSessionBean {

 /*
 
 		<container-transaction>
			<method>
				<ejb-name>CTnkSpplWrkOrdRegEJB</ejb-name>
				<method-intf>Remote</method-intf>
				<method-name>receiveTransactionWTWork</method-name>
			</method>
			<trans-attribute>RequiresNew</trans-attribute>
		</container-transaction>

		<container-transaction>
			<method>
				<ejb-name>CTnkSpplWrkOrdRegEJB</ejb-name>
				<method-intf>Remote</method-intf>
				<method-name>receiveWTWork_01</method-name>
			</method>
			<trans-attribute>RequiresNew</trans-attribute>
		</container-transaction>
		
*/		
	
	private Logger logger 			= null;
	Boolean isSuccess = new Boolean(false);
	private YmComm ymComm = new YmComm();
	
	public void ejbCreate() {
		LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		logger 		= new Logger(config);
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 저장품에 대해 
        * 작업예약을 호출한다.
        *
        * param String : 저장품 List
        * param String : 야드구분
        * param String : 동구분
        * param String : 수조탱크보급(P)/대차출하상차(U) 구분
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */          	           
	public void callTankLoadWbookInfo(String sStockList,
								  	  String sYdGp,
							      	  String sBayGp,
							      	  String sGbn){
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
			
			int iSeq = 0;
			
			YdStockDAO ydStockDAO 	        = new YdStockDAO();
			YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
			YdWBookDAO ydWBookDAO           = new YdWBookDAO();
			
			String sSchCode 		= "";
			String sStockMoveTerm 	= "";
			
			String sStkLayerQueryId = "";
			String wBookQueryId 	= "";
			String sWBookQueryId 	= "";	
			String stkQueryId 		= "";
			String wBookid      	= "";
			
			JDTORecord wBookSel		= null;
			
			logger.println(LogLevel.DEBUG, this, "=======작업예약 생성 시작========");
			
			String[] bStockId = sStockList.split("-");
			
			for(int index = 0; index < bStockId.length; index++)
			{
				String sStockId = bStockId[index];
				
	            sStkLayerQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark";
				iSeq = ydStackLayerDAO.requestupdateData(sStkLayerQueryId, new Object[]{ YmCommonConst.STACK_LAYER_STAT_S, 
																						 sStockId});
				
				// 작업예약(TB_YM_WBOOK) Table Select WBOOK_ID Key 생성 Seq + 1
				wBookQueryId 	= "ym.steelinfo.steelinforecv.dao.YdStockDAO.getWBookID";
				wBookSel 		= ydStackLayerDAO.requestFind(wBookQueryId);
				wBookid      	= wBookSel.getFieldString("WBOOK_ID");
				
				if("U".equals(sGbn)){
					sSchCode 		= YmCommonConst.NEW_SCH_WORK_KIND_CTFL; //Coil 대차출하상차
					sStockMoveTerm 	= YmCommonConst.NEW_STOCK_MOVE_TERM_KG; //Coil 출하작업지시대기
				}else if("P".equals(sGbn)){
					sSchCode 		= YmCommonConst.NEW_SCH_WORK_KIND_CWLI; //Coil 수냉재보급
					sStockMoveTerm 	= YmCommonConst.NEW_STOCK_MOVE_TERM_A3; //Coil 수냉재추출
				}
				
				sWBookQueryId 	= "ym.workrequest.wrequestaccept.dao.YdWBookDAO.InsertYdWBook";
				iSeq = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ wBookid, 
																				 sYdGp, 
																				 sBayGp,
																				 sSchCode, 
																				 YmCommonUtil.getWorkDuty(),
																				 YmCommonUtil.getWorkParty()});	
																				 
				stkQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
				iSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ wBookid, 
																			  sStockMoveTerm, 
																			  sStockId});	
				
				/* 2007.04.10 이정훈
				 * 대차 출하 상차 보조 작업 편성 관계로 스케쥴 호출 막음
				 */
				if("P".equals(sGbn)){
					//Coil Schedule EJB Call
					EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
					Boolean isTrue = (Boolean)ejbConn.trx("callCraneSchInfo",new Class[]{String.class},new Object[]{ wBookid });
				}
				
			} 
			logger.println(LogLevel.DEBUG, this, "=======작업예약 생성 끝========");
						
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 수조 TANK 보급요구 정보
        *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */          	  	 
	public void receiveCTnkSpplWrkOrd(String sMessage)  throws java.rmi.RemoteException{ 
		/*
	     *  야드 LEVEL2로 부터 넘어온 전문을 파싱한 후 전문내용을 가지고 해당 업무 로직을 처리한다.
	     *  전문내용을 JDTORecord로 파싱한다.
	     *  업무 로직
	     *	1.TC_CD - CJ1PB01 (I/F ID : YM-BIF-009 )
	     *	2.야드 LEVEL2로부터 수조 TANK 보급요구 정보를 수신
	     *  3.수신한 수조 TANK 상태 구분이 작업준비완료이면 적치단을 Open
	     *  4.수신한 수조 TANK 상태 구분이 작업준비취소이면 적치단을 Close
	     *  CJ1PB012005-09-2210:10:10I0036CWT011
	     * 
			동구분	    CHAR	1	C
			SPAN	    CHAR	2	WT
			ROW	        CHAR	2	TANK-NO
			구분	        CHAR	1	1:작업준비완료,2:작업준비취소
	     */
		
        logger.println(LogLevel.DEBUG,this,"Start-receiveCTnkSpplWrkOrd()");
		
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
			int iResult = 0;
			
			Level2Parser level2Parser 	= new Level2Parser();
			JDTORecord jDTORecord 		= level2Parser.parse(sMessage);

			String TmpBayGp   = StringHelper.evl(jDTORecord.getFieldString("동구분"), "");
			String TmpSPAN    = StringHelper.evl(jDTORecord.getFieldString("SPAN"), "");
			String TmpROW     = StringHelper.evl(jDTORecord.getFieldString("ROW"), "");
			String TmpProcess = StringHelper.evl(jDTORecord.getFieldString("구분"), "");
			
			String stackColGp = "" + YmCommonConst.YD_GP_3 + "" + TmpBayGp.trim() + ""+ TmpSPAN.trim() + "" + TmpROW.trim() + "" ; //3CWT01
			String stackBedGp = "";
			
			if (TmpProcess.trim().equals("1" )){
				/*
				 * 작업준비완료이면 수조 TANK에 대한 적치단을 Open
				 * ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateLayerActiveStat 
				 * update TB_YM_STACKLAYER  set STACK_LAYER_ACTIVE_STAT = 'O'  
				 *  where STACK_COL_GP = stackColGp
				 *    And STACK_BED_GP = 01~03
				 *    And STACK_LAYER_GP = '01'
				 */
				for (int ii=1; ii<4; ii++){
					stackBedGp = "0" + ii + ""; 
					String updateLayerActiveStat = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateLayerActiveStat";
					int updatelayeractivestat = ydStackLayerDAO.requestupdateData(updateLayerActiveStat, new Object[]{ 
							                    YmCommonConst.STACK_LAYER_ACTIVE_STAT_O, stackColGp.trim(), 
												stackBedGp.trim(), YmCommonConst.STACK_LAYER_GP_01 });					
				}
				
			}else{
				/*
				 * 작업준비취소이면 수조 TANK에 대한 적치단을 Close
				 * update TB_YM_STACKLAYER  set STACK_LAYER_ACTIVE_STAT = 'C'
				 *  where STACK_COL_GP = stackColGp
				 *    And STACK_BED_GP = 01~03
				 *    And STACK_LAYER_GP = '01' 
				 */
				for (int ii=1; ii<4; ii++){
					stackBedGp = "0" + ii + ""; 
					String updateLayerActiveStat = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateLayerActiveStat";
					int updatelayeractivestat    = ydStackLayerDAO.requestupdateData(updateLayerActiveStat, new Object[]{ 
							                       YmCommonConst.STACK_LAYER_ACTIVE_STAT_C, stackColGp.trim(), 
												   stackBedGp.trim(), YmCommonConst.STACK_LAYER_GP_01});					
				}				
			}
			
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	    	e.printStackTrace();  
	        throw new EJBServiceException(e);
	    }    
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 수조 TANK Event 정보
        *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */          	  	 
	public void receiveWTEvent(String sMessage)  throws java.rmi.RemoteException{
		/*
	     *  야드 LEVEL2로 부터 넘어온 전문을 파싱한 후 전문내용을 가지고 해당 업무 로직을 처리한다.
	     *  전문내용을 JDTORecord로 파싱한다.
	     *  업무 로직
	     *	1.TC_CD - CJ1PB02
	     *	2.야드 LEVEL2로부터 수조 TANK Event 정보를 수신
	     *  3.
	     *  CJ1PB022005-09-2210:10:10I0036CWT011S    yyyymmddhhbbss
	     * 
			동구분	        CHAR	1		‘C’
			SPAN	        CHAR	2		‘WT’
			ROW		        CHAR	2		TANK-NO
			입수/배수 구분		CHAR	1		1:입수, 2:배수
			개시/종료 구분		CHAR	1		S:개시, E:종료
			냉각 온도		     INT	4		현재 미사용
			발생일시		    CHAR	14		yyyymmddhhbbss
			
			TB_YM_EQUIP			
			EQUIP_GP	        VARCHAR2(6)		설비 구분
			YD_GP	            VARCHAR2(1)		YARD 구분
			BAY_GP	            VARCHAR2(1)		동 구분			
			WATERIN_WATEROUT_GP	VARCHAR2(1)	
            START_END_GP	    VARCHAR2(1)	
	     */		
        logger.println(LogLevel.DEBUG,this,"Start-receiveWTEvent()");
		
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdEquipDAO 				dao  	= new YdEquipDAO();
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
			int iResult = 0;
			
			Level2Parser level2Parser 	= new Level2Parser();
			JDTORecord jDTORecord 		= level2Parser.parse(sMessage);

			String TmpBayGp   = StringHelper.evl(jDTORecord.getFieldString("동구분"), "");
			String TmpSPAN    = StringHelper.evl(jDTORecord.getFieldString("SPAN"), "");
			String TmpROW     = StringHelper.evl(jDTORecord.getFieldString("ROW"), "");
			String TmpWTstart = StringHelper.evl(jDTORecord.getFieldString("입수배수구분"), "");
			String TmpWTend   = StringHelper.evl(jDTORecord.getFieldString("개시종료구분"), "");
			String TmpWTtemp  = StringHelper.evl(jDTORecord.getFieldString("냉각온도"), "");
			String TmpWTdate  = StringHelper.evl(jDTORecord.getFieldString("발생일시"), "");
			
			boolean isSuccess = WTEventBackUp(TmpBayGp, 
											  TmpSPAN, 
											  TmpROW, 
											  TmpWTstart, 
											  TmpWTend, 
											  TmpWTtemp, 
											  TmpWTdate);
										 
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	    	e.printStackTrace();  
	        throw new EJBServiceException(e);
	    }  		
	   
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 수조 TANK 화면에서 Event 정보 백업 버튼 (CJ1PB02)
	 * param BAYGP   동구분	        	CHAR	1		‘C’
	 * param SPAN    SPAN	        	CHAR	2		‘WT’
	 * param ROW     ROW		        CHAR	2		TANK-NO
	 * param WTstart 입수/배수 구분		CHAR	1		1:입수, 2:배수
	 * param WTend   개시/종료 구분		CHAR	1		S:개시, E:종료
	 * param WTtemp  냉각 온도		     INT	4		현재 미사용
	 * param WTdate  발생일시		    CHAR	14		yyyymmddhhbbss
        *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */          	  	 	 
	public boolean WTEventBackUp(String BAYGP, 
								 String SPAN, 
								 String ROW, 
								 String WTstart, 
								 String WTend, 
								 String WTtemp, 
								 String WTdate){
		boolean isSuccess = false;
		
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdEquipDAO 				dao  	= new YdEquipDAO();
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String stackColGp = YmCommonConst.YD_GP_3+ 
								BAYGP.trim()+ 
								SPAN.trim()+ 
								ROW.trim(); //3CWT01
			/*
			수조 Tank 상태정보 변경
			ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.updateTankGp 
			UPDATE TB_YM_EQUIP 
			   SET WATERIN_WATEROUT_GP = ?, START_END_GP = ?
			 WHERE EQUIP_GP = ?   
			*/
			String updateTankGp = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.updateTankGp";
			int updatetankgp    = ydStackLayerDAO.requestupdateData(updateTankGp, 
																	new Object[]{WTstart.trim(), 
																				 WTend.trim(), 
																				 stackColGp.trim()});	
			
			/*
			 *	설비 Table에 수신한 항목을 Update
			 */
			updatetankgp = dao.updateWtEquipStatInfo(stackColGp,
													 WTstart.trim()+WTend.trim(),
													 WTdate);	
										     
			isSuccess = true;	
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
		    return isSuccess;
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 수조 TANK 화면에서 작업준비 상태 정보 백업 버튼 (CJ1PB01)
	 * param BAYGP     동구분	    CHAR	1	C
	 * param SPAN      SPAN	    CHAR	2	WT
	 * param ROW       ROW	        CHAR	2	TANK-NO
	 * param Process   구분	        CHAR	1	1:작업준비완료,2:작업준비취소
        *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */          	  	 	 
	public boolean WTSpplyBackUp(String BAYGP, String SPAN, String ROW, String Process){
		boolean isSuccess = false;
		
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String stackColGp = "" + YmCommonConst.YD_GP_3 + "" + BAYGP.trim() + ""+ SPAN.trim() + "" + ROW.trim() + "" ; //3CWT01
			String stackBedGp = "";
			
			if (Process.trim().equals("1" )){
				/*
				 * 작업준비완료이면 수조 TANK에 대한 적치단을 Open
				 * ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateLayerActiveStat 
				 * update TB_YM_STACKLAYER  set STACK_LAYER_ACTIVE_STAT = 'O'  
				 *  where STACK_COL_GP = stackColGp
				 *    And STACK_BED_GP = 01~03
				 *    And STACK_LAYER_GP = '01'
				 */
				for (int ii=1; ii<4; ii++){
					stackBedGp = "0" + ii + ""; 
					String updateLayerActiveStat = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateLayerActiveStat";
					int updatelayeractivestat = ydStackLayerDAO.requestupdateData(updateLayerActiveStat, new Object[]{ 
							                    YmCommonConst.STACK_LAYER_ACTIVE_STAT_O, stackColGp.trim(), 
												stackBedGp.trim(), YmCommonConst.STACK_LAYER_GP_01 });					
				}
				
			}else{
				/*
				 * 작업준비취소이면 수조 TANK에 대한 적치단을 Close
				 * update TB_YM_STACKLAYER  set STACK_LAYER_ACTIVE_STAT = 'C'
				 *  where STACK_COL_GP = stackColGp
				 *    And STACK_BED_GP = 01~03
				 *    And STACK_LAYER_GP = '01' 
				 */
				for (int ii=1; ii<4; ii++){
					stackBedGp = "0" + ii + ""; 
					String updateLayerActiveStat = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateLayerActiveStat";
					int updatelayeractivestat    = ydStackLayerDAO.requestupdateData(updateLayerActiveStat, new Object[]{ 
							                       YmCommonConst.STACK_LAYER_ACTIVE_STAT_C, stackColGp.trim(), 
												   stackBedGp.trim(), YmCommonConst.STACK_LAYER_GP_01});					
				}				
			}			
			
			isSuccess = true;	
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
		    return isSuccess;
	}

	/**
	 * 오퍼레이션명 : 
	 *
	 *  야드 LEVEL2로 부터 넘어온 전문을 파싱한 후 전문내용을 가지고 해당 업무 로직을 처리한다.
        *  전문내용을 JDTORecord로 파싱한다.
        *  업무 로직
        *	1.TC_CD - CJ1PB03
        *	2.야드 LEVEL2로부터 수조 TANK 실적 정보를 수신
        *  3.설비 Table에 수신한 항목을 Update
        *  3.COIL 공통 Table에 수조 TANK 냉각 실적을 Update
        *  4.조업에서 작성한  종합판정 Method Call
        *  5.수조 TANK 추출요구 Method Call
        *   
        *  CJ1PB032005-09-2210:10:10I0036CWT011S    yyyymmddhhbbss
        * 
	 *	동구분		    CHAR	1		‘C’
	 *	SPAN		    CHAR	2		‘WT’
	 *	ROW		        CHAR	2		TANK-NO
	 *	번지		        CHAR	2		01, 02, 03
	 *	단		        CHAR	2		01
	 *	COILNO		    CHAR	10		
	 *	입수개시일시		CHAR	14		yyyymmddhhbbss
	 *	입수완료일시		CHAR	14		yyyymmddhhbbss
	 *	배수개시일시		CHAR	14		yyyymmddhhbbss
	 *	배수완료일시		CHAR	14		yyyymmddhhbbss
	 *	근		        CHAR	1		1,2,3
	 *	조		        CHAR	1		A,B,C,D
	 *	냉각시간		     INT	4	        분	
	 *	개시냉각온도		 INT	4		현재 미사용
	 *	완료냉각온도		 INT	4		현재 미사용
	 *	냉각방법		    CHAR	1		현재 미사용
	 *	종료구분		    CHAR	1		SPACE,  ‘E’ (TANK 별 마지막 COIL 여부)
        *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */          	  	 	      
	public boolean receiveWTWork(String sMessage){
		boolean isSuccess = false;
		
		try{
			
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
	    	Level2Parser level2Parser = new Level2Parser();
			JDTORecord jDTORecord = level2Parser.parse(sMessage);
			
			int iSeq = 0;
			
			String TmpBayGp       = StringHelper.evl(jDTORecord.getFieldString("동구분"), "");
			String TmpSPAN        = StringHelper.evl(jDTORecord.getFieldString("SPAN"), "");
			String TmpROW         = StringHelper.evl(jDTORecord.getFieldString("ROW"), "");
			String TmpWTaddr      = StringHelper.evl(jDTORecord.getFieldString("번지"), "");
			String TmpWTStack     = StringHelper.evl(jDTORecord.getFieldString("단"), "");
			String TmpCOILNO      = StringHelper.evl(jDTORecord.getFieldString("COILNO"), "").trim();
			String TmpWTInSdate   = StringHelper.evl(jDTORecord.getFieldString("입수개시일시"), "");
			String TmpWTInEdate   = StringHelper.evl(jDTORecord.getFieldString("입수완료일시"), "");
			String TmpWTOutSdate  = StringHelper.evl(jDTORecord.getFieldString("배수개시일시"), "");
			String TmpWTOutEdate  = StringHelper.evl(jDTORecord.getFieldString("배수완료일시"), "");
			String TmpWTShift     = StringHelper.evl(jDTORecord.getFieldString("근"), "");
			String TmpWTGroup     = StringHelper.evl(jDTORecord.getFieldString("조"), "");
			String TmpWTTime      = StringHelper.evl(jDTORecord.getFieldString("냉각시간"), "");
			String TmpWTStemp     = StringHelper.evl(jDTORecord.getFieldString("개시냉각온도"), "");
			String TmpWTEtemp     = StringHelper.evl(jDTORecord.getFieldString("완료냉각온도"), "");
			String TmpWTType      = StringHelper.evl(jDTORecord.getFieldString("냉각방법"), "");
			String TmpWTEprocess  = StringHelper.evl(jDTORecord.getFieldString("종료구분"), "");
		
			
			receiveWTWork(TmpBayGp,
						  TmpSPAN,
						  TmpROW,
						  TmpWTaddr,
						  TmpWTStack,
						  TmpCOILNO,
						  TmpWTInSdate,
						  TmpWTInEdate,
						  TmpWTOutSdate,
						  TmpWTOutEdate,
						  TmpWTEprocess);
	    	
	    	isSuccess = true;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess;
	}    
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */          	  	 	      	
	public void receiveWTWork(  String TmpBayGp,       
			String TmpSPAN,        
			String TmpROW,         
			String TmpWTaddr,      
			String TmpWTStack,     
			String TmpCOILNO,     
			String TmpWTInSdate,  
			String TmpWTInEdate,  
			String TmpWTOutSdate, 
			String TmpWTOutEdate, 
			String TmpWTEprocess  )  throws java.rmi.RemoteException{
		
		boolean isSuccess = false;
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
			
			logger.println(LogLevel.DEBUG,this,"Start-receiveWTWork()");
			int iSeq = 0;
			
			/*
			 *	TRANSACTION COMMIT 처리를 위해 메소드 분리
			 *  ejb-jar.xml 에 receiveTransactionWTWork 메소드단위 
			 *  TRANSACTION 셋팅
			 */
			EJBConnector ejbConn = new EJBConnector("default", "JNDICTnkSpplWrkOrdReg", this);
			iSeq = Integer.parseInt("" + ejbConn.trx("receiveTransactionWTWork", 
									new Class[]{String.class,
												String.class,
												String.class,
												String.class,
												String.class,
												String.class,
												String.class,
												String.class,
												String.class,
												String.class,
												String.class}, 
									new Object[]{TmpBayGp,
												  TmpSPAN,
												  TmpROW,
												  TmpWTaddr,
												  TmpWTStack,
												  TmpCOILNO,
												  TmpWTInSdate,
												  TmpWTInEdate,
												  TmpWTOutSdate,
												  TmpWTOutEdate,
												  TmpWTEprocess}));
			/*
			iSeq = receiveTransactionWTWork(  TmpBayGp,
					  TmpSPAN,
					  TmpROW,
					  TmpWTaddr,
					  TmpWTStack,
					  TmpCOILNO,
					  TmpWTInSdate,
					  TmpWTInEdate,
					  TmpWTOutSdate,
					  TmpWTOutEdate,
					  TmpWTEprocess);
			*/
		/*	receiveWTWork_01(TmpBayGp,
						  TmpSPAN,
						  TmpROW,
						  TmpWTaddr,
						  TmpWTStack,
						  TmpCOILNO,
						  TmpWTInSdate,
						  TmpWTInEdate,
						  TmpWTOutSdate,
						  TmpWTOutEdate,
						  TmpWTEprocess);*/
			ejbConn.trx("receiveWTWork_01", 
						new Class[]{String.class,
									String.class,
									String.class,
									String.class,
									String.class,
									String.class,
									String.class,
									String.class,
									String.class,	
									String.class,
									String.class}, 
						new Object[]{TmpBayGp,
									  TmpSPAN,
									  TmpROW,
									  TmpWTaddr,
									  TmpWTStack,
									  TmpCOILNO,
									  TmpWTInSdate,
									  TmpWTInEdate,
									  TmpWTOutSdate,
									  TmpWTOutEdate,
									  TmpWTEprocess});
			
	    	isSuccess = true;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}  
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 수조 TANK 실적 정보
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 * @ejb.transaction type="RequiresNew"
	 */
	public void receiveWTWork_01(  String TmpBayGp,       
								String TmpSPAN,        
								String TmpROW,         
								String TmpWTaddr,      
								String TmpWTStack,     
								String TmpCOILNO,     
								String TmpWTInSdate,  
								String TmpWTInEdate,  
								String TmpWTOutSdate, 
								String TmpWTOutEdate, 
								String TmpWTEprocess  )  throws java.rmi.RemoteException{
		
        logger.println(LogLevel.DEBUG,this,"Start-receiveWTWork_01()");
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
			
			YdEquipDAO dao  	= new YdEquipDAO();
			YdStockDAO stockDao	= new YdStockDAO();
		
			int iSeq = 0;
			
			String stackColGp 	= YmCommonConst.YD_GP_3 + "" + 
								  TmpBayGp.trim() + ""+ 
								  TmpSPAN.trim() + "" + 
								  TmpROW.trim() + "" ; //3CWT01
			String stackBedGp 	= TmpWTaddr;
			String stackLayerGp = TmpWTStack;
			
			
			//코일공통 진도코드 Table 참조.
			/*
			 * 2007.04.17 이정훈
			 * Transaction 문제로 조업  DataSource  사용
			 * 
			 */
	     	//String[] sStockInfo = YmCommonUtil.getCoilCurrProgCd(TmpCOILNO,"");
			String[] sStockInfo = YmCommonUtil.getCoilCurrProgCd_PO(TmpCOILNO);
		    String sProgCd   	= sStockInfo[0];
		    String sStocMv   	= sStockInfo[1];
		    String sDemander    = sStockInfo[2];
			
			logger.println(LogLevel.DEBUG,this,"receiveTransactionWTWork =>"+sProgCd);
			logger.println(LogLevel.DEBUG,this,"receiveTransactionWTWork =>"+sStocMv);
			logger.println(LogLevel.DEBUG,this,"receiveTransactionsDemander =>"+sDemander);
			
			/* 
			 * 2008.10.01
			 * Hysco 순천향 수냉 로직 반영 
			 * 
			 */
	    	if(YmCommonConst.NEW_STOCK_MOVE_TERM_HG.equals(sStocMv) && "A42692".equals(sDemander)){
	    		
	    		iSeq = stockDao.updateStockTransInfo_06(TmpCOILNO,				// 저장품ID
												   		YmCommonConst.ITEM_CG,	// 저장품품목
												   		YmCommonConst.NEW_STOCK_MOVE_TERM_KG);// 출하작업지시대기
	    	} else if (!"A42692".equals(sDemander)) {
	    	
	    		iSeq = stockDao.updateStockTransInfo(TmpCOILNO,		// 저장품ID
													 sStocMv);		// 저장품이동조건
	    		iSeq = stockDao.updateCoilCommTransInfo(TmpCOILNO);		// 저장품ID
	
	    	}
	    	else{
	    	
	    		iSeq = stockDao.updateStockTransInfo(TmpCOILNO,		// 저장품ID
													 sStocMv);		// 저장품이동조건
	    	}
	    	
	    	if(YmCommonConst.WT_E_PROCESS.equals(TmpWTEprocess)){

				/*
				 *	3.설비 Table에 수신한 항목을 Update
				 */
				iSeq = dao.updateWtEquipInfo(stackColGp,
											 "", //TmpWTInSdate,
											 "", //TmpWTInEdate,
											 "", //TmpWTOutSdate,
											 "");//TmpWTOutEdate);										
				
				logger.println(LogLevel.DEBUG,this,"설비 TABLE 에 수조탱크 입,배수 관련 정보 수정");
				
				/*
				 *	4.수조 TANK 추출요구 Method Call
				 */
				EJBConnector ejbConn = new EJBConnector("default","JNDICTankExtWrkOrdReg",this);
				Boolean bool = (Boolean)ejbConn.trx("receiveCTankExtWrkOrd",new  Class[]{String.class},
																		    new Object[]{stackColGp});
				logger.println(LogLevel.DEBUG,this,"수조 TANK 추출요구 Method Call");
			}
				
	    	if(YmCommonConst.NEW_STOCK_MOVE_TERM_HG.equals(sStocMv) && "A42692".equals(sDemander)){	
		    	/*
				 *	제품 입고실적 처리
				 */
		    	{
		    		
		    		String sPut_Position= stackColGp+stackBedGp+stackLayerGp;
		    		/*
					YMDM001 model = new YMDM001();
					model.setTcCode(YmCommonConst.MODEL_YMDM001);
					model.setTcDate(YmCommonUtil.getCurDate("yyyy-MM-dd"));
					model.setTcTime(YmCommonUtil.getCurDate("HH-mm-ss"));
					
					// 입고 일자 
					model.setRECEIPT_DATE(YmCommonUtil.getCurDate("yyyyMMdd"));
					// 입고 시각 
					model.setRECEIPT_TIME(YmCommonUtil.getCurDate("HHmmss"));
					// YARD 구분 1:ACoil, 2:BSlab, 3:BCoil 
					model.setYD_GP(YmCommonConst.YD_GP_3);
					// 제품 번호  
					model.setGOODS_NO(TmpCOILNO);
					// 저장 위치 1F02030901 
					model.setSTORE_LOC(sPut_Position);
					
					EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
					Boolean isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{CommonModel.class},
																  	  	 		new Object[]{model});
					logger.println(LogLevel.DEBUG,this, "내부IF호출===Coil을 제품 야드로 입고시.===");
					*/
					
					//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                    //코일입고작업실적
					JDTORecord tcRecordDM = null;
					tcRecordDM = JDTORecordFactory.getInstance().create(); 
					tcRecordDM.setField("GOODS_NO",TmpCOILNO);
					tcRecordDM.setField("YD_GP",YmCommonConst.YD_GP_3);
					tcRecordDM.setField("STORE_LOC",sPut_Position);
					
					//인터페이스 전문 호출
					EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
					isSuccess = (Boolean)ejbConn.trx("getYDDMR001",new Class[]{JDTORecord.class},
					  	  	 new Object[]{tcRecordDM}); 
                    logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 코일입고작업실적.===");
                   //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			           
				}
	    	}
			
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	    	e.printStackTrace();  
	        throw new EJBServiceException(e);
	    }  				
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 수조 TANK 실적 정보
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 * @ejb.transaction type="RequiresNew"
	 */
	public int receiveTransactionWTWork(    String TmpBayGp,       
											String TmpSPAN,        
											String TmpROW,         
											String TmpWTaddr,      
											String TmpWTStack,     
											String TmpCOILNO,     
											String TmpWTInSdate,  
											String TmpWTInEdate,  
											String TmpWTOutSdate, 
											String TmpWTOutEdate, 
											String TmpWTEprocess  )  throws java.rmi.RemoteException{
		
        logger.println(LogLevel.DEBUG,this,"Start-receiveTransactionWTWork()");
		
		YdEquipDAO dao  	= new YdEquipDAO();
		YdStockDAO stockDao	= new YdStockDAO();
		CraneSchDAO schDao	= new CraneSchDAO();
		
		int iSeq = 0;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			String stackColGp 	= YmCommonConst.YD_GP_3 + "" + 
								  TmpBayGp.trim() + ""+ 
								  TmpSPAN.trim() + "" + 
								  TmpROW.trim() + "" ; //3CWT01
			String stackBedGp 	= TmpWTaddr;
			String stackLayerGp = TmpWTStack;
			/*
			 *	1.코일 공통 Table에 냉각완료구분 항목을 Update
			 *	tb_pm_coilcomm table cool_done_gp = 'Y'
			 *	-	조업 실적처리에서 처리하고 있슴
			 */
			//iSeq = schDao.updateCommonCoilCoolDoneInfo(TmpCOILNO);	
			//logger.println(LogLevel.DEBUG,this,"코일 공통 Table에 냉각완료구분 항목 수정="+TmpCOILNO+"="+iSeq);
			
			if(TmpWTInSdate.compareTo(TmpWTInEdate) > 0){
	    		throw new EJBServiceException("=수조탱크 실적=>입수일시에러.");
    		}
    		
    		if(TmpWTOutSdate.compareTo(TmpWTOutEdate) > 0){
	    		throw new EJBServiceException("=수조탱크 실적=>배수일시에러.");
    		}
    		
    		if(TmpWTInSdate.compareTo(TmpWTOutSdate) > 0){
	    		throw new EJBServiceException("=수조탱크 실적=>입배수일시에러.");
    		}
    		
    		if(TmpWTInEdate.compareTo(TmpWTOutEdate) > 0){
	    		throw new EJBServiceException("=수조탱크 실적=>입배수일시에러.");
    		}
			
			if("".equals(TmpCOILNO)){
	    		throw new EJBServiceException("=수조탱크 실적=>코일번호 존재안함.");
    		}
    		
    		
    		JDTORecord layerJr	= schDao.getStackLayerInfoWithPk(stackColGp,
							    							  	 stackBedGp,
																 stackLayerGp);
			
			if(layerJr == null){
				throw new EJBServiceException("=수조탱크 실적=>설비정보 존재안함.");
			}	
			
			if(!TmpCOILNO.equals(StringHelper.evl(layerJr.getFieldString("STOCK_ID"), ""))){
				throw new EJBServiceException("=수조탱크 실적=>코일정보가 일치하지 않습니다.");
			}
		    		   				    										  
			/*
			 *	2.코일 종합판정 정보 가져오기
			 */
			{
				/**
				 *	수조탱크 실적정보 처리 부분
				 *	이미 실적처리가 되어 있으면 실적처리 하지 않음.
				 *	1.	진도코드가 A,B,C가 아니면 SKIP
				 *	2.	다음공정 및 계획공정이 5T가 아니면 SKIP
				 *	3.	정정작업실적정보가 있으면 SKIP
				 */
				boolean isPros1		= false; 
				boolean isPros2		= false; 
				boolean isPros3		= false; 
				String sCoilProc    = "";
				String sNextProc 	= "";
				String sPlanProc 	= "";
				String sCurrProgCd 	= "";
				String sJJWrslt		= ""; 
				
				JDTORecord stockJr	= schDao.getCoilCommonInfo(TmpCOILNO); 
				if(stockJr != null){
					sNextProc	= StringHelper.evl(stockJr.getFieldString("차공정"), "");
					sPlanProc 	= StringHelper.evl(stockJr.getFieldString("계획공정"),"");
					sCurrProgCd = StringHelper.evl(stockJr.getFieldString("CURR_PROG_CD"), "");
				}
				if("".equals(sNextProc)){
					sCoilProc = sPlanProc;
				}else{
					sCoilProc = sNextProc;
				}
				
				JDTORecord jjWrsltJr = schDao.getCoilShearOrdInfo(TmpCOILNO); 
				if(jjWrsltJr != null){
					sJJWrslt = StringHelper.evl(jjWrsltJr.getFieldString("COIL_NO"), "");
				}
				
				/*
				 *	다음공정이 '5T'
				 */
				if(YmCommonConst.SHEAR_SUPPLY_GP_5T.equals(sCoilProc)){
					isPros1		= true; 
				}else{
					isPros1		= false; 
					logger.println(LogLevel.DEBUG,this,"=수조탱크 실적=>다음공정 에러="+sCoilProc);
				}
				/*
				 *	진도코드가 A,B,C
				 */
				if(YmCommonConst.CURR_PROG_CD_COIL_A.equals(sCurrProgCd) || //재질판정대기 AC  
				   YmCommonConst.CURR_PROG_CD_COIL_B.equals(sCurrProgCd) || //정정작업지시 BC 
				   YmCommonConst.CURR_PROG_CD_COIL_C.equals(sCurrProgCd) ){ //정정작업대기 CC 
					isPros2		= true; 
				}else{
					isPros2		= false; 
					logger.println(LogLevel.DEBUG,this,"=수조탱크 실적=>진도코드 에러="+sCurrProgCd);
				}
				/*
				 *	정정작업실적정보가 존재안함.
				 */
				if("".equals(sJJWrslt)){
					isPros3		= true; 
				}else{
					isPros3		= false; 
					logger.println(LogLevel.DEBUG,this,"=수조탱크 실적=>정정작업실적정보 에러="+sJJWrslt);
				}
				
				if(isPros1&&isPros2&&isPros3){
					
					logger.println(LogLevel.DEBUG,this,"=조업모듈 CALL 시작=");
					
					/* 
					 * 1. 조업 DataSource와 품질 DataSource 분리 시 DB Lock 발생
					 * 2. 품질에서 조업 DataSource 사용
					 * 3. 조업에서 사용하는 종합 판정 Logic을 수정하여 사용함.
					 * 4. 조업 로직 변경시 수정해야함. 
					 */  
					
					
					// 2010.01.14 수냉실적 처리 후 YDPTJ002전문을 진행에 전송(OS UPDATE) 정종균 
					//List msgL = coolWrsltProcMain(TmpCOILNO, "T");
					
			 		//실적처리 
		    	 	EJBConnector ejbConn = new EJBConnector("default","JNDICTnkSpplWrkOrdReg",this);
		    	 	List msgL  = (List)ejbConn.trx("coolWrsltProcMain",new  Class[]{String.class,String.class},
																new Object[]{TmpCOILNO, "T"});	
		    	 	
		    	 	
		    	 	//YDPTJ002 전송
		    	 	coolOSYDPTJ002(TmpCOILNO);
		    	 	
				    logger.println(LogLevel.DEBUG,this,"=조업모듈 CALL 종료=");
				}
				
				
		    }
	    	
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	    	e.printStackTrace();  
	        throw new EJBServiceException(e);
	    }  			
	    return iSeq;	
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : YDPTJ002전문 
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException	 
	 */
 
	public boolean coolOSYDPTJ002(String s_STOCK_ID ) throws JDTOException  {
        logger.println(LogLevel.INFO,this,"coolOSYDPTJ002진행 전문  Start");
		
        JDTORecord tcRecord2 = null;

        try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
    			ymCommonDAO dao2 = ymCommonDAO.getInstance();
    			String queryID 		= "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getCOILCOMM";
				List productList 	= dao2.getCommonList(queryID, new Object[]{s_STOCK_ID});
				JDTORecord stlRecord = (JDTORecord)productList.get(0);
				

			    //코일소재 이송완료실적(YDPTJ002)
			    tcRecord2 =JDTORecordFactory.getInstance().create();
			    tcRecord2.setField("JMS_TC_CD", "YDPTJ002");
			    tcRecord2.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
			    tcRecord2.setField("STL_NO",  StringHelper.evl(stlRecord.getFieldString("COIL_NO"), ""));
			    // 주문번호
			    tcRecord2.setField("ORD_NO",  StringHelper.evl(stlRecord.getFieldString("ORD_NO"), ""));
			    // 주문행번
			    tcRecord2.setField("ORD_DTL", StringHelper.evl(stlRecord.getFieldString("ORD_DTL"), ""));
			    // 공장공정코드
			    tcRecord2.setField("PLNT_PROC_CD",  StringHelper.evl(stlRecord.getFieldString("PLNT_PROC_CD"), ""));
			    // 재료외형구분
			    tcRecord2.setField("STL_APPEAR_GP",  StringHelper.evl(stlRecord.getFieldString("STL_APPEAR_GP"), ""));
			    // 현재진도코드
			    tcRecord2.setField("CURR_PROG_CD",  StringHelper.evl(stlRecord.getFieldString("CURR_PROG_CD"), ""));
			    // 주문여재구분
			    tcRecord2.setField("ORD_YEOJAE_GP", StringHelper.evl(stlRecord.getFieldString("ORD_YEOJAE_GP"), ""));
			    // 재료중량 (SLAB중량) 
			    tcRecord2.setField("STL_WT",  StringHelper.evl(stlRecord.getFieldString("COIL_WT"), ""));
		    	// 설계재료중량(항목명?)
		    	tcRecord2.setField("DS_MTL_WT", "");
			    // 재료상태구분(항목명?)
			    tcRecord2.setField("MTL_STAT_GP",  StringHelper.evl(stlRecord.getFieldString("RECORD_PROG_STAT"), ""));
			    // Record 종료구분
			    tcRecord2.setField("RECORD_END_GP",  StringHelper.evl(stlRecord.getFieldString("RECORD_END_GP"), ""));
			    // Record 종료구분 1(항목명?)
			    tcRecord2.setField("RECORD_END_GP1", "");
			    // 전진도 코드
			    tcRecord2.setField("BEFO_PROG_CD",  StringHelper.evl(stlRecord.getFieldString("BEFO_PROG_CD"), ""));
			    // 전주문 번호
			    tcRecord2.setField("BEF_ORD_NO",  StringHelper.evl(stlRecord.getFieldString("BEF_ORD_NO"), ""));
			    // 전주문 행번
			    tcRecord2.setField("BEF_ORD_DTL",  StringHelper.evl(stlRecord.getFieldString("BEF_ORD_DTL"), ""));
			    // 모재료번호   
			    tcRecord2.setField("MMATL_FEE_NO",  StringHelper.evl(stlRecord.getFieldString("MMATL_FEE_NO"), ""));
			    // 목전충당구분
			    tcRecord2.setField("ORDERTRANS_MATCH_GP", StringHelper.evl(stlRecord.getFieldString("MATCH_ORDERTRANS_GP"), ""));	
			
			    EJBConnector ejbConn2 = new EJBConnector("default","JNDIYardWrkResReg",this);
			    isSuccess = (Boolean)ejbConn2.trx("sendInternalModel",new Class[]{JDTORecord.class},
			  	  	 new Object[]{tcRecord2});
		
				
				logger.println(LogLevel.INFO,this,"coolOSYDPTJ002진행 전문  END");

            return true;
        } catch (DAOException daoe) {
        	return false;
        } catch (Exception e) {
            throw new EJBServiceException(e);
        }
    }
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 냉각 실적 처리 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 * @ejb.transaction type="RequiresNew"
	 */
	public List coolWrsltProcMain(String sCoilNo, String sAirWaterGp) { 
		
		String sCurrDate = DateHelper.currentTime("yyyy-MM-dd");
		String sCurrTime = DateHelper.currentTime("HH:mm:ss"); 
	    String  sErrorGbn                  = "";    /* 오류구분     */
	    String  sErrorGbnCdMsg             = "";    /* 오류메세지 */
	
		CoolWrsltProcDAO subCoolWrsltProcDAO = new CoolWrsltProcDAO();
	
		List sRetList = new ArrayList();  // RETURN 용 LIST
		
		/****************************************************************************** 
		 *코일공통 전체 조회
		 ******************************************************************************/ 
		
		List getResultList = new ArrayList(); 
		sRetList = new ArrayList(); 
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			List sParamList = new ArrayList();
//			sParamList.add("B");     // 0 공장구분('B') 2009.07.20 이후 c열연 통합
			sParamList.add(sCoilNo); // 1 코일번호
			    
			logger.println(LogLevel.DEBUG,"MSG >>냉각처리 대상 코일공통 정보 조회 PARAM:"+sParamList ); 
			getResultList  = (ArrayList)subCoolWrsltProcDAO.getListCoolWrsltProcCoilCommRead(sParamList);
	        logger.println(LogLevel.DEBUG,"MSG >>냉각처리 대상 코일공통 정보 조회  DONE:"+getResultList.size() ); 
	        
	            
	        if ( getResultList == null  ||  getResultList.size() != 1 ) { 
			     sRetList.add("E");
		       	 sRetList.add("냉각 실적 처리 코일공통 대상 없음.");
		         logger.println(LogLevel.ERROR,"MSG >>"+sRetList ); 
		         return sRetList; 
	        }
	            
		  }catch(Exception e){
	          	sRetList.add("E");
	        	sRetList.add("냉각 실적 처리 코일공통 대상 없음."+e.getMessage());
	            logger.println(LogLevel.ERROR,"MSG >>"+sRetList ); 
	        	return sRetList; 
		  }
		  
	
		  JDTORecord jRecordSet 		      = (JDTORecord)getResultList.get(0);
		   String  sStepNo                    =jRecordSet.getFieldString("STEP_NO"               );  //차수                  
		   String  sPlantGp                   =jRecordSet.getFieldString("HR_PLNT_GP"            );  //공장 구분(B열연)               
		   String  sProcGp                    =jRecordSet.getFieldString("PROC_GP"               );  //공정 구분(A:공냉,T:수냉)               
		   String  sWorkStat                  =jRecordSet.getFieldString("WORK_STAT"             );  //작업 상태(*)              
		   String  sCoilOrdOutdia             =jRecordSet.getFieldString("COIL_OUTDIA"           );  //COIL 외경           
		   String  sCoilT                     =StringHelper.evl(jRecordSet.getFieldString("COIL_T" ),"0"); //코일공통.실적COIL 두께               
		   String  sCoilW                     =StringHelper.evl(jRecordSet.getFieldString("COIL_W" ),"0"); //코일공통.실적COIL 폭                 
		   String  sWrsltCoilT                =jRecordSet.getFieldString("PO_WRSLT_COIL_T"             );  //압연실적.실적COIL 두께               
		   String  sWrsltCoilW                =jRecordSet.getFieldString("PO_WRSLT_COIL_W"             );  //압연실적.실적COIL 폭                 
		   String  sWtDecisionCd              =StringHelper.evl(jRecordSet.getFieldString("PO_WT_DECISION_CD"),"");  //중량 결정 CODE          
		   String  sCalWt                     =StringHelper.evl(jRecordSet.getFieldString("PO_COIL_CAL_WT"   ),"0"); //계산 중량               
		   String  sRealWt                    =StringHelper.evl(jRecordSet.getFieldString("PO_COIL_REAL_WT"  ),"0"); //실 중량                 
		   String  sReagentPickYn             =jRecordSet.getFieldString("REAGENT_PICK_YN"             );  //시편 채취 유무          
		   String  sReagentNo                 =jRecordSet.getFieldString("REAGENT_NO"                  );  //시편 번호  
		   String  sTTolMax                   =jRecordSet.getFieldString("SM_T_TOL_MAX"                );  //두께 허용오차 최대      
		   String  sTTolMin                   =jRecordSet.getFieldString("SM_T_TOL_MIN"                );  //두께 허용오차 최소      
		   String  sWTolMax                   =jRecordSet.getFieldString("SM_W_TOL_MAX"                );  //폭 허용오차 최대        
		   String  sWTolMin                   =jRecordSet.getFieldString("SM_W_TOL_MIN"                );  //폭 허용오차 최소        
		   String  sOrdT                      =jRecordSet.getFieldString("SM_ORD_T"                    );  //주문 두께               
		   String  sOrdW                      =jRecordSet.getFieldString("SM_ORD_W"                    );  //주문 폭                 
		   String  sOrdLen                    =jRecordSet.getFieldString("SM_ORD_LEN"                  );  //주문 길이               
		   String  sMidInspectDefectCd1       =jRecordSet.getFieldString("MID_INSPECT_DEFECT_CD1"      );  //중간 검사 흠 CODE1        
		   String  sMidInspectDefectCd2       =jRecordSet.getFieldString("MID_INSPECT_DEFECT_CD2"      );  //중간 검사 흠 CODE2        
		   String  sMidInspectDefectCd3       =jRecordSet.getFieldString("MID_INSPECT_DEFECT_CD3"      );  //중간 검사 흠 CODE3        
		   String  sMidInspectDefectCd4       =jRecordSet.getFieldString("MID_INSPECT_DEFECT_CD4"      );  //중간 검사 흠 CODE4        
		   String  sMidInspectDefectCd5       =jRecordSet.getFieldString("MID_INSPECT_DEFECT_CD5"      );  //중간 검사 흠 CODE5   
		   String  sCustCd                    =jRecordSet.getFieldString("SM_CUST_CD"                  );  //수요가 CODE             
		   String  sCustName                  =jRecordSet.getFieldString("SM_CUST_NAME"                );  //수요가 명               
		   String  sOrdNo                     =jRecordSet.getFieldString("ORD_NO"                      );  //주문 번호               
		   String  sOrdDtl                    =jRecordSet.getFieldString("ORD_DTL"                     );  //주문 행번               
		   String  sSpecAbbsym                =jRecordSet.getFieldString("SPEC_ABBSYM"                 );  //규격 약호               
		   String  sUsageCd                   =jRecordSet.getFieldString("USAGE_CD"                    );  //용도 CODE   
//		   String  sMillWorkDate              =jRecordSet.getFieldString("PO_MILL_WORK_DATE"           );  //압연 작업 일자          
//		   String  sMillWorkTime              =jRecordSet.getFieldString("PO_MILL_WORK_TIME"           );  //압연 작업 시각 
//		   String  sShearOrdDate              =jRecordSet.getFieldString("SHEAR_ORD_DATE"              );  //정정 지시 일자     
		   
		   logger.println(LogLevel.DEBUG,"MSG >>주문 번호 정보 조회 PARAM:  "+sOrdNo ); 
		   logger.println(LogLevel.DEBUG,"MSG >>주문 행번 PARAM:  "+sOrdDtl ); 
		   String  sMillWorkDate              =StringHelper.evl(jRecordSet.getFieldString("PO_MILL_WORK_DATE" ),"");  //압연 작업 일자 
		   logger.println(LogLevel.DEBUG,"MSG >>sMillWorkDate PARAM:  "+sMillWorkDate );
		   if(!sMillWorkDate.equals("")){
		           sMillWorkDate = sMillWorkDate.substring(0, 8);
		   }
		   logger.println(LogLevel.DEBUG,"MSG >>sMillWorkDate1 PARAM:  "+sMillWorkDate );       
		   String  sMillWorkTime              =StringHelper.evl(jRecordSet.getFieldString("PO_MILL_WORK_DATE" ),"");  //압연 작업 시각
		   logger.println(LogLevel.DEBUG,"MSG >>sMillWorkTime PARAM:  "+sMillWorkTime );
		   if(!sMillWorkTime.equals("")){
		           sMillWorkTime = sMillWorkTime.substring(8, 14);
		   }
		   logger.println(LogLevel.DEBUG,"MSG >>sMillWorkTime1 PARAM:  "+sMillWorkTime );        
		   String  sShearOrdDate              =StringHelper.evl(jRecordSet.getFieldString("PO_MILL_WORK_DATE" ),"");  //정정 지시 일자
		   logger.println(LogLevel.DEBUG,"MSG >>sShearOrdDate PARAM:  "+sShearOrdDate );
		   if(!sShearOrdDate.equals("")){
		           sShearOrdDate = sShearOrdDate.substring(0, 8);
		   }
		   logger.println(LogLevel.DEBUG,"MSG >>sShearOrdDate1 PARAM:  "+sShearOrdDate );        

		   String  sCoilOutdia                =jRecordSet.getFieldString("COIL_OUTDIA"                 );  //코일 외경               
		   String  sCoilIndia                 =jRecordSet.getFieldString("COIL_INDIA"                  );  //코일 내경   
		   String  sReceiptHoldScrapCauseGP   =jRecordSet.getFieldString("RECEIPT_HOLD_SCRAP_CAUSE_GP" );  //입고 보류 SCRAP 원인 구분 (입고(I),보류(B))
		   String  sHoldCauseCd	              =StringHelper.evl(jRecordSet.getFieldString("HOLD_CAUSE_CD"	           ),"");  //보류 원인 CODE
		   String  sHoldProgStat	          =StringHelper.evl(jRecordSet.getFieldString("HOLD_PROG_STAT"	           ),"");  //보류 진행 상태
		   String  sHoldStampDate	          =StringHelper.evl(jRecordSet.getFieldString("HOLD_STAMP_DATE"	           ),"");  //보류 판정 일자
		   String  sHoldStampTime	          =StringHelper.evl(jRecordSet.getFieldString("HOLD_STAMP_TIME"	           ),"");  //보류 판정 시각
		   String  sRealCustCd                =jRecordSet.getFieldString("SM_REAL_CUST_CD"             );  //실 고객  CODE             
		   String  sRealCustName              =jRecordSet.getFieldString("SM_REAL_CUST_NAME"           );  //실 고객  명               
		   String  sWrapUnitWtUnit            =jRecordSet.getFieldString("SM_WRAP_UNIT_WT_UNIT"        );  //포장 단위 중량 단위                             
		   String  sWrapUnitWtMax             =jRecordSet.getFieldString("SM_WRAP_UNIT_WT_MAX"         );  //포장 단위 중량 최대  
		   String  sWrapUnitWtMin             =jRecordSet.getFieldString("SM_WRAP_UNIT_WT_MIN"         );  //포장 단위 중량 최소
		   String  sFnlTcBackupGp	          =jRecordSet.getFieldString("FNL_TC_BACKUP_GP"	           );  //최종 TC BACKUP 구분(B)
		   String  sFnlRegTcName	          =jRecordSet.getFieldString("FNL_REG_TC_NAME"	           );  //최종 등록 TC 명(coolWrsltBatch) 
		   String  sItemname_c                =jRecordSet.getFieldString("ITEMNAME_CD"                 );  //품명 CODE
		   String  sRegister	              =jRecordSet.getFieldString("REGISTER"	                   );  //등록자
//		   String  sReg_dt	                  =jRecordSet.getFieldString("REG_DDTT"	                   );  //등록 일시
		   String  sModifier	              =jRecordSet.getFieldString("MODIFIER"	                   );  //수정자
//		   String  sModDdtt                   =jRecordSet.getFieldString("MOD_DDTT"	                   );  //수정 일시  
	
		   String sFnlShearDefectCd1    	  =StringHelper.evl(jRecordSet.getFieldString("FNL_SHEAR_DEFECT_CD1"),""); // 최종  정정 흠코드 1  
		   String sFnlShearDefectCd2          =StringHelper.evl(jRecordSet.getFieldString("FNL_SHEAR_DEFECT_CD2"),""); // 최종  정정 흠코드 2  
		   String sFnlShearDefectCd3          =StringHelper.evl(jRecordSet.getFieldString("FNL_SHEAR_DEFECT_CD3"),""); // 최종  정정 흠코드 3  
		   String sFnlShearDefectCd4          =StringHelper.evl(jRecordSet.getFieldString("FNL_SHEAR_DEFECT_CD4"),""); // 최종  정정 흠코드 4  
		   String sFnlShearDefectCd5          =StringHelper.evl(jRecordSet.getFieldString("FNL_SHEAR_DEFECT_CD5"),""); // 최종  정정 흠코드 5  
	
		   String  sOrdYeojaeGp               =jRecordSet.getFieldString("ORD_YEOJAE_GP"               );  //주문 여재 구분 
		   String  sYeojaeCauseCD             =jRecordSet.getFieldString("YEOJAE_CAUSE_CD"             );  //여재 원인 CODE
		   String  sYeojaeOccurDate           =jRecordSet.getFieldString("YEOJAE_OCCUR_DATE"           );  //여재 발생일자
		   String  sYeojaeOccurTime           =jRecordSet.getFieldString("YEOJAE_OCCUR_TIME"           );  //여재 발생시각
		   String  sYeojaeCauseCD1            =jRecordSet.getFieldString("YEOJAE_CAUSE_CD1"            );  //여재 원인 CODE1
		   String  sYeojaeOccurDate1          =jRecordSet.getFieldString("YEOJAE_OCCUR_DATE1"          );  //여재 발생일자1
		   String  sYeojaeCauseCD2            =jRecordSet.getFieldString("YEOJAE_CAUSE_CD2"            );  //여재 원인 CODE2
		   String  sYeojaeOccurDate2          =jRecordSet.getFieldString("YEOJAE_OCCUR_DATE2"          );  //여재 발생일자2
		   String  sYeojaeCauseCD3            =jRecordSet.getFieldString("YEOJAE_CAUSE_CD3"            );  //여재 원인 CODE3
		   String  sYeojaeOccurDate3          =jRecordSet.getFieldString("YEOJAE_OCCUR_DATE3"          );  //여재 발생일자3
		   String  sYeojaeCauseCD4            =jRecordSet.getFieldString("YEOJAE_CAUSE_CD4"            );  //여재 원인 CODE4
		   String  sYeojaeOccurDate4          =jRecordSet.getFieldString("YEOJAE_OCCUR_DATE4"          );  //여재 발생일자4
		   String  sCurrProgCd                =jRecordSet.getFieldString("CURR_PROG_CD"                );  //현재진도코드
		   String  sSlabNo                    =jRecordSet.getFieldString("SLAB_NO"                     );  //현재진도코드
		   //----------------------------------------일관제철 변경에 따른 추가 ----------------------------------------------------
		   String  sWoItm                     =jRecordSet.getFieldString("WO_ITM"              		   );  //작업지시ITEM
		   String  sHrPlntGp                  =jRecordSet.getFieldString("HR_PLNT_GP"                  );  //열연공장구분
		   String  sPrdItmCd                  =jRecordSet.getFieldString("PRD_ITM_CD"                  );  //생산ITEM코드
		   String  sPtopPlntGp                =jRecordSet.getFieldString("PTOP_PLNT_GP"                );  //조업공장구분
		   String  sWrsltRealWt               =jRecordSet.getFieldString("NET_WEIGH_WT"                );  //WRSLT_REAL_WT 실적실중량
		   String  sYdGp                      =jRecordSet.getFieldString("YD_GP"                       );  //YARD 구분
		   String  sStoreLocCd                =jRecordSet.getFieldString("YD_STR_LOC"                  );  //저장 위치
		   String  sShearItmCd                =jRecordSet.getFieldString("SHEAR_ITM_CD"                  );  //정정 ITEM코드
		   
		   String sPlntprocCD = null;
		   String sRecordprogStat = null;
		   String sRecordendGP = null;
		   String sBefoprogCD   = null;
		   String sBefordNO  = null;
		   String sBefordDtl  = null;
		   String sMmatlfeeNo  = null;
	
			/*********************************************************************************************
			 * 보류처리 루틴 
			 ********************************************************************************************/
		    ArrayList sSize = new ArrayList();
			sSize.add(sCoilNo);                  // 코일번호 
			sSize.add(new Double(sCoilT));        // 두께 
			sSize.add(new Double(sCoilW));        // 폭 
			if ("NC".equals(sWtDecisionCd)) 
			     sSize.add(new Double(sCalWt));   // 계산중량 
			else{
				sSize.add(new Double(sRealWt));   // 실 중량 
			}
			
			List sResultList = null;
			
			try{	
				logger.println(LogLevel.DEBUG,"MSG >>냉각처리 대상 보류처리 정보 조회 PARAM: B, A, "+sSize ); 
				sResultList = abBoRyuChuriPanJungMain("B","T",sSize);	 
				logger.println(LogLevel.DEBUG,"MSG >>냉각처리 대상 보류처리 정보 조회 DONE:"+sResultList ); 
			}catch(Exception e){
	             sRetList.add("E");
	             sRetList.add("냉각처리 대상 보류처리 정보 조회중 오류 발생 하였습니다.:"+e.getMessage());
	             logger.println(LogLevel.ERROR,"MSG >>"+sRetList.get(1) ); 
	             return sRetList; 
	        }
	        
			String sBoErrGp         = "";
			String sBoErrMsg        = "";
			String sBoReceiptHoldScrapCauseGP  = "";
			String sBoHoldCauseCd   = "";
			String sBoHoldCauseCd1  = "";
			String sBoHoldCauseCd2  = "";
			String sBoHoldCauseCd3  = "";
			String sBoHoldCauseCd4  = "";
			String sBoOrdYeojaeGp   = "";
			String sBoYeojaeCauseCD = "";
			
			if (sResultList == null || sResultList.size() < 9 || sResultList.get(0).equals("E") ) {
                sRetList.add("E");
                sRetList.add("냉각처리 대상 보류처리 정보 조회 중 오류 발생 :"+sResultList);
                logger.println(LogLevel.ERROR,"MSG >>"+sRetList.get(1) ); 
                return sRetList;
			}

			if (sResultList.size() > 8 ) {
				
				  sBoErrGp                      =StringHelper.evl((String)sResultList.get(0),"").trim(); // 오류코드 : E:오류, S:성공 
				  sBoErrMsg                     =StringHelper.evl((String)sResultList.get(1),"").trim(); // 오류메세지 
				  sBoReceiptHoldScrapCauseGP    =StringHelper.evl((String)sResultList.get(2),"").trim(); // 판정구분 (입고(I) , 보류(B))
				  sBoHoldCauseCd	            =StringHelper.evl((String)sResultList.get(3),"").trim(); // 보류 원인 CODE
				  sBoOrdYeojaeGp                =StringHelper.evl((String)sResultList.get(4),"").trim(); // 주문 여재 구분( 1:주문 , 2:여재 )
				  sBoYeojaeCauseCD              =StringHelper.evl((String)sResultList.get(5),"").trim(); // 여재 원인 CODE
	
				  String sBoRyuCd_02	        =StringHelper.evl((String)sResultList.get(6),"").trim(); // 보류 원인 CODE PM1
				  String sBoRyuCd_03	        =StringHelper.evl((String)sResultList.get(7),"").trim(); // 보류 원인 CODE PM2
				  String sBoRyuCd_04	        =StringHelper.evl((String)sResultList.get(8),"").trim(); // 보류 원인 CODE PM3
	
				  //String sBoRyuCd_02	        =""; // 보류 원인 CODE PM1
				  //String sBoRyuCd_03	        =""; // 보류 원인 CODE PM2
				  //String sBoRyuCd_04	        =""; // 보류 원인 CODE PM3
				  
	    	      if (!sBoRyuCd_02.equals("")) {
	    	      	sBoHoldCauseCd1 = sBoRyuCd_02;
		    	    	if (!sBoRyuCd_03.equals("")) {
		    	    		sBoHoldCauseCd2 = sBoRyuCd_03;
			    	    	if (!sBoRyuCd_04.equals("")) {
			    	    		sBoHoldCauseCd3 = sBoRyuCd_04;
			    	    	}
		    	    	}else{
			    	    	if (!sBoRyuCd_04.equals("")) {
			    	    		sBoHoldCauseCd2 = sBoRyuCd_04;
			    	    	}
		    	    	}
	    	    	}else{
		    	    	if (!sBoRyuCd_03.equals("")) {
		    	    		sBoHoldCauseCd1 = sBoRyuCd_03;
			    	    	if (!sBoRyuCd_04.equals("")) {
			    	    		sBoHoldCauseCd2 = sBoRyuCd_04;
			    	    	}
		    	    	}else{
			    	    	if (!sBoRyuCd_04.equals("")) {
			    	    		sBoHoldCauseCd1 = sBoRyuCd_04;
			    	    	}
		    	    	}
	    	    	}
			}else{
	            sRetList.add("E");
	            sRetList.add("냉각처리 대상 보류처리 정보 조회 중 오류 발생 :"+sResultList);
	            logger.println(LogLevel.ERROR,"MSG >>"+sRetList.get(1) ); 
	            return sRetList;
				 
			}
			
			String nowDate  = (String)(DateHelper.currentTime("yyyyMMdd"));
			String nowDate2 = (String)(DateHelper.currentTime("yyyy.MM.dd"));
	
			String nowTime  = (String)(DateHelper.currentTime("HHmmss"));
			String nowTime2 = (String)(DateHelper.currentTime("HH:mm:ss"));
			
			// 근,조,계상 일자 
			CommonUtil comUtil = new CommonUtil();
			int iYYYY = StringHelper.parseInt(nowDate.substring(0,4));
			int iMM   = StringHelper.parseInt(nowDate.substring(4,6));
			int iDD   = StringHelper.parseInt(nowDate.substring(6,8));
			int iHH   = StringHelper.parseInt(nowTime.substring(0,2));
			
			String sDuty  = Integer.toString( comUtil.getGeun(iHH) );  //근
			String sParty = comUtil.getTeam(iYYYY,iMM,iDD,iHH );       //근
	
	        String sINIDate = nowDate;		                           //계상일자	
	        if ( iHH >=0 && iHH < 6 ) {
	        	Date dNowDate = (Date)DateHelper.toUtilDate(iYYYY,iMM,iDD);
	        	sINIDate = DateHelper.format(DateHelper.addDate(dNowDate,-1),"yyyyMMdd");
	        }
			logger.println(LogLevel.DEBUG,"MSG >>냉각처리 대상 근,조,계상일자 PARAM:"+sCoilNo+","+sDuty+","+ sParty +","+sINIDate); 
				
	        sRetList = new ArrayList();
	        try{
	
	        	List sParamList = new ArrayList(); 
			    // 여재로 다운 되었을 경우 처리
	        	
		        if ( sBoOrdYeojaeGp.equals("2") ) {
	        	/****************************************************************************** 
	        	/ 냉각 완료 대상 코일 공통 처리  -- 여재인 경우
	        	/******************************************************************************/
	        	
		        	sParamList.add("COOL_YM");       	// 0 현재진도CODE등록 PROGRAM
//		        sParamList.add("G");                // 1 현재진도CODE(보류재:F,종합판정대기:G) 
	        	if (sBoReceiptHoldScrapCauseGP.equals("B")) {
 		            sParamList.add("F");                // 1 현재진도CODE(판정 보류재:F,종합판정대기:G)
	        	}else{
	        		sParamList.add("G");                // 1 현재진도CODE(판정 보류재:F,종합판정대기:G)
	        	}
		        	
		        sParamList.add(sBoOrdYeojaeGp);     // 2 주문 여재 구분 
		        sParamList.add(sBoYeojaeCauseCD);   // 3 여재 원인 CODE
		        sParamList.add(nowDate);            // 4 여재 발생일자
		        sParamList.add(nowTime);            // 5 여재 발생시각
		        sParamList.add(sYeojaeCauseCD);     // 6 여재 원인 CODE1
		        sParamList.add(sYeojaeOccurDate);   // 7 여재 발생일자1
		        sParamList.add(sYeojaeCauseCD1);    // 8 여재 원인 CODE2
		        sParamList.add(sYeojaeOccurDate1);  // 9 여재 발생일자2
		        sParamList.add(sYeojaeCauseCD2);    // 10 여재 원인 CODE3
		        sParamList.add(sYeojaeOccurDate2);  // 11 여재 발생일자3
		        sParamList.add(sYeojaeCauseCD3);    // 12 여재 원인 CODE4
		        sParamList.add(sYeojaeOccurDate4);  // 13 여재 발생일자14
	
		        sParamList.add("Y");                // 14 냉각완료('Y')
		        sParamList.add("5T");               // 15 통과공정1('2T') 	20090903 정종균 2T ->5T
		        sParamList.add("");                 // 16 잔여공정1('')
		        sParamList.add("");                 // 17 다음공정('')   
		        
	        	if (sBoReceiptHoldScrapCauseGP.equals("B")) {
	 		        	sParamList.add(sBoHoldCauseCd);     // 18 보류원인 CODE
						sParamList.add(sBoHoldCauseCd1);    // 19 보류원인 CODE PM1
//						sParamList.add(sBoHoldCauseCd2);    // 20 보류원인 CODE PM2
//						sParamList.add(sBoHoldCauseCd3);    // 21 보류원인 CODE PM3
//						sParamList.add(sBoHoldCauseCd4);// 22 보류원인 CODE PM4
	 	 		        sParamList.add("1");   	        // 23 보류 진행 상태('1')
	 	 		        sParamList.add(nowDate);        // 24 보류 판정 일자
	 	 		        sParamList.add(nowTime);        // 25 보류 판정 시각
		        	}else{
	 	 		        sParamList.add("");             // 18 보류원인 CODE
	 	 		        sParamList.add("");             // 19 보류원인 CODE PM1
//	 	 		        sParamList.add("");             // 20 보류원인 CODE PM2
//	 	 		        sParamList.add("");             // 21 보류원인 CODE PM3
//	 	 		        sParamList.add("");             // 22 보류원인 CODE PM4
	 	 		        sParamList.add("");   	        // 23 보류 진행 상태('1')
	 	 		        sParamList.add("");         	// 24 보류 판정 일자
	 	 		        sParamList.add("");         	// 25 보류 판정 시각
		        	}
	
	 		        sParamList.add(sWrsltCoilT);     	// 26 실적 코일 두께
	 		        sParamList.add(new Integer((int)StringHelper.parseDouble(sWrsltCoilW)));          // 27 실적 코일 폭(소수점 이하 삭제 )
	
	 		        sParamList.add(nowDate);     		// 28 정정 작업 지시 일자
	 		        sParamList.add(nowDate);     		// 29 정정 실적 일자
	
	 		        sParamList.add("COOL_YM");    		// 30 수정자 
	 		        
 	 		        sParamList.add(sShearItmCd          );  //74 정정지시ITEM
 	 		        sParamList.add(sShearItmCd          );  //74 정정실적ITEM
 	 		        
 	 		        if (sPlantGp.equals("C")) {
	 		        	sParamList.add("HCF");             	// 32 공장공정코드  
		            }else{
		            	sParamList.add("HBF");             	// 32 공장공정코드  
		            }
 	 		        
	 		        sParamList.add(sCoilNo);         	// 31 코일번호
//	 		        sParamList.add("B");             	// 32 공장구분
 	 		        if (sPlantGp.equals("C")) {
 	 		        	sParamList.add("C");             	// 32 공장구분  
		            }else{
		            	sParamList.add("B");             	// 32 공장구분  
		            }
 	 		        
	 		        logger.println(LogLevel.DEBUG,"MSG >>냉각 실적 처리 코일공통(여재인경우) UPDATE PARAM:"+sParamList ); 
	                 int iRetResult  = (int)subCoolWrsltProcDAO.upDateCoolWrsltProcYeojae(sParamList);
	                 logger.println(LogLevel.DEBUG,"MSG >>냉각 실적 처리 코일공통(여재인경우) UPDATE DONE:"+iRetResult ); 
	                 if ( iRetResult <= 0 ) {
	                     sRetList.add("E");
	                     sRetList.add("냉각 실적 처리 코일공통(여재인경우) 수정 대상이 존재 하지 않습니다.");
	                     logger.println(LogLevel.ERROR,"MSG >>"+sRetList.get(1) ); 
	                     return sRetList;
	                 }
	
			   }else{
				 
				 /***************************************************************************************************
				 * 냉각 완료 대상 코일 공통 처리 -- 주문재 경우
				 ***************************************************************************************************/
						
				 sParamList.add("COOL_YM");    // 0 현재진도CODE등록 PROGRAM
		         if (sBoReceiptHoldScrapCauseGP.equals("B")) {
 		             sParamList.add("F");                // 1 현재진도CODE(보류재:F,입고대기:G)
			         }else{
		 		             sParamList.add("G");                // 1 현재진도CODE(보류재:F,입고대기:G)
			         }
				 sParamList.add("Y");             		// 2 냉각완료('Y')
				 sParamList.add("5T");            		// 3 통과공정1('2T') 	20090903 정종균 2T ->5T
				 sParamList.add("");              		// 4 잔여공정1('')
				 sParamList.add("");              		// 5 다음공정('')   
	
	        	 if (sBoReceiptHoldScrapCauseGP.equals("B")) {
	 		        sParamList.add(sBoHoldCauseCd);     // 6 보류원인 CODE
	 		        sParamList.add(sBoHoldCauseCd1);    // 7 보류원인 CODE PM1
//	 		        sParamList.add(sBoHoldCauseCd2);    // 8 보류원인 CODE PM2
//	 		        sParamList.add(sBoHoldCauseCd3);    // 9 보류원인 CODE PM3
//	 		        sParamList.add(sBoHoldCauseCd4);    // 10 보류원인 CODE PM4
	 		        sParamList.add("1");   	            // 11 보류 진행 상태('1')
	 		        sParamList.add(nowDate);         	// 12 보류 판정 일자
	 		        sParamList.add(nowTime);         	// 13 보류 판정 시각
	        	 }else{
	 		        sParamList.add("");                 // 6 보류원인 CODE
	 		        sParamList.add("");                 // 7 보류원인 CODE PM1
//	 		        sParamList.add("");                 // 8 보류원인 CODE PM2
//	 		        sParamList.add("");                 // 9 보류원인 CODE PM3
//	 		        sParamList.add("");                 // 10 보류원인 CODE PM4
	 		        sParamList.add("");   	            // 11 보류 진행 상태('1')
	 		        sParamList.add("");         	    // 12 보류 판정 일자
	 		        sParamList.add("");         	    // 13 보류 판정 시각
	        	 }
	 		     sParamList.add(nowDate);     	        // 14 정정 작업 지시 일자
 	 		     sParamList.add(nowDate);     	        // 15 정정 실적 일자

 				 sParamList.add("COOL_BATCH");          // 16 수정자
 				 
 				 sParamList.add(sShearItmCd          );  //74 정정지시ITEM
	 		     sParamList.add(sShearItmCd          );  //74 정정실적ITEM
	 		        
	 		    if (sPlantGp.equals("C")) {
 		        	sParamList.add("HCF");             	// 32 공장공정코드  
	            }else{
	            	sParamList.add("HBF");             	// 32 공장공정코드  
	            }  
	 		    sParamList.add(sCoilNo);             	// 31 코일번호
	 		        
	 		    if (sPlantGp.equals("C")) {
	 		        sParamList.add("C");             	// 32 공장구분  
	            }else{
	            	sParamList.add("B");             	// 32 공장구분  
	            }
	
		        logger.println(LogLevel.DEBUG,"MSG >>냉각 실적 처리 코일공통(주문재경우) UPDATE PARAM:"+sParamList ); 
	            int iRetResult  = (int)subCoolWrsltProcDAO.upDateCoolWrsltProc(sParamList);
	            logger.println(LogLevel.DEBUG,"MSG >>냉각 실적 처리 코일공통(주문재경우) UPDATE DONE:"+iRetResult ); 
	            if ( iRetResult <= 0 ) {
	                sRetList.add("E");
	                sRetList.add("냉각 실적 처리 코일공통(주문재경우) 수정 대상이 존재 하지 않습니다.");
	                logger.println(LogLevel.ERROR,"MSG >>"+sRetList.get(1) ); 
	                return sRetList;
	            }
			   }
//	         }catch(Exception e){
//	             sRetList.add("E");
//	             sRetList.add("냉각 실적 처리 코일공통 수정중 오류 발생 하였습니다.:"+e.getMessage());
//	             logger.println(LogLevel.ERROR,"MSG >>"+sRetList.get(1) ); 
//	             return sRetList; 
//	         }
	        			  
			/****************************************************************
			 * 정정 지시실적 등록 
			 ****************************************************************/
			
	         sRetList = new ArrayList();
//			  try {
				  List sjjParamList = new ArrayList();
				  sjjParamList.add(sCoilNo                  );  //0 코일번호
				  sjjParamList.add(new Integer(StringHelper.parseInt(sStepNo,0) + 1) );  //1   차수 + 1
				  sjjParamList.add(sPlantGp                 );  //2  공장 구분(B열연)
				  sjjParamList.add(sAirWaterGp              );  //3  공정 구분(A:공냉,T:수냉)
				  sjjParamList.add("*"                      );  //4  작업 상태(*)
				  sjjParamList.add(sCoilOrdOutdia           );  //5  작업지시 외경
				  sjjParamList.add(sCoilT                   );  //6  COIL 두께
				  sjjParamList.add(sCoilW                   );  //7  COIL 폭
				  sjjParamList.add(sWtDecisionCd            );  //8  중량 결정 CODE
				  sjjParamList.add(sRealWt                  );  //9  실 중량
				  sjjParamList.add(sCalWt                   );  //10 계산 중량
				  sjjParamList.add(sOrdT                    );  //11 목표 두께
				  sjjParamList.add(sTTolMax                 );  //12 두께 허용오차 최대
				  sjjParamList.add(sTTolMin                 );  //13 두께 허용오차 최소
				  sjjParamList.add(sOrdW                    );  //14 목표 폭
				  sjjParamList.add(sWTolMax                 );  //15 폭 허용오차 최대
				  sjjParamList.add(sWTolMin                 );  //16 폭 허용오차 최소
				  sjjParamList.add(sOrdT                    );  //17 주문 두께
				  sjjParamList.add(sOrdW                    );  //18 주문 폭
				  sjjParamList.add(sOrdLen                  );  //19 주문 길이
				  sjjParamList.add(sMidInspectDefectCd1     );  //20 전 공정 흠 CODE1(중간 검사 흠 CODE1)
				  sjjParamList.add(sMidInspectDefectCd2     );  //21 전 공정 흠 CODE2(중간 검사 흠 CODE2)
				  sjjParamList.add(sMidInspectDefectCd3     );  //22 전 공정 흠 CODE3(중간 검사 흠 CODE3)
				  sjjParamList.add(sMidInspectDefectCd4     );  //23 전 공정 흠 CODE4(중간 검사 흠 CODE4)
				  sjjParamList.add(sMidInspectDefectCd5     );  //24 전 공정 흠 CODE5(중간 검사 흠 CODE5)
				  sjjParamList.add(sCustCd                  );  //25 수요가 CODE
				  sjjParamList.add(sCustName                );  //26 수요가 명
				  sjjParamList.add(sOrdNo                   );  //27 주문 번호
				  sjjParamList.add(sOrdDtl                  );  //28 주문 행번
				  sjjParamList.add(sSpecAbbsym              );  //29 규격 약호
				  sjjParamList.add(sUsageCd                 );  //30 용도 CODE
				  sjjParamList.add(sMillWorkDate + sMillWorkTime );  //31 압연 작업 일자
				  sjjParamList.add(sShearOrdDate + sMillWorkTime );  //33 정정 지시 일자
				  sjjParamList.add(sOrdLen                  );  //34 목표 길이
				  sjjParamList.add(sCoilOutdia              );  //35 실적 외경
				  sjjParamList.add(sCoilIndia               );  //36 실적 내경 
				  
				  /* 2007.09.18 이정훈
				   * 이상진 대리 요청으로 실적 두께,폭 -> 지시 두께,폭 으로 수정
				   
				  sjjParamList.add(sWrsltCoilT              );  //37 실적 두께
				  sjjParamList.add(new Integer((int)StringHelper.parseFloat(sWrsltCoilW)) );  //38 실적 코일 폭(소수점 이하 삭제 )
				  */
				  sjjParamList.add(sCoilT              );  //37 지시  두께
				  sjjParamList.add(new Integer((int)StringHelper.parseDouble(sCoilW)) );  //38 지시 코일 폭(소수점 이하 삭제 )
				  
				  sjjParamList.add(sOrdLen                  );  //39 실적 길이
				  sjjParamList.add(sWtDecisionCd            );  //40 실적 중량 결정 CODE
				  sjjParamList.add(sRealWt                  );  //41 실적 실 중량
				  sjjParamList.add(sCalWt                   );  //42 실적 계산 중량
				  sjjParamList.add(sBoReceiptHoldScrapCauseGP );  //43 입고 보류 SCRAP 원인 구분 (입고(I),보류(B))
				  sjjParamList.add(sDuty                    );  //44 근
				  sjjParamList.add(sParty                   );  //45 조
				  sjjParamList.add(sINIDate                 );  //46 계상 일자
				  sjjParamList.add(nowDate + nowTime        );  //47 정정 작업 검사 일자
				  sjjParamList.add(nowDate + nowTime        );  //49 정정 작업 FROM 일자
				  sjjParamList.add(nowDate + nowTime        );  //51 정정 작업 TO 일자
				  sjjParamList.add(nowDate + nowTime        );  //53 정정 작업 종료 일자
	        	  if (sBoReceiptHoldScrapCauseGP.equals("B")) {
					  sjjParamList.add(sBoHoldCauseCd             );  //55 보류 원인 CODE
					  sjjParamList.add(sBoHoldCauseCd1            );  //56 보류 원인 CODE1
					  sjjParamList.add(sBoHoldCauseCd2            );  //57 보류 원인 CODE2
					  sjjParamList.add(sBoHoldCauseCd3            );  //58 보류 원인 CODE3
					  sjjParamList.add(sBoHoldCauseCd4            );  //59 보류 원인 CODE4
					  sjjParamList.add("1"                        );  //60 보류 진행 상태
					  sjjParamList.add(sHoldStampDate + sHoldStampTime           );  //61 보류 판정 일자
					 // sjjParamList.add(sHoldStampTime             );  //62 보류 판정 시각
	        	  }else{
					  sjjParamList.add(""             );  //55 보류 원인  CODE
					  sjjParamList.add(""             );  //56 보류 원인1 CODE
					  sjjParamList.add(""             );  //57 보류 원인2 CODE
					  sjjParamList.add(""             );  //58 보류 원인3 CODE
					  sjjParamList.add(""             );  //59 보류 원인4 CODE
					  sjjParamList.add(""             );  //60 보류 진행 상태
					  sjjParamList.add(""             );  //61 보류 판정 일자
					  //sjjParamList.add(""             );  //62 보류 판정 시각
	        	  }				  
				  sjjParamList.add(sRealCustCd              );  //63 주문자 CODE
				  sjjParamList.add(sRealCustName            );  //64 주문자 명
				  sjjParamList.add(sWrapUnitWtUnit          );  //65 포장 단위 중량 단위
				  sjjParamList.add(sWrapUnitWtMax           );  //66 포장 단위 중량 최대
				  sjjParamList.add(sWrapUnitWtMin           );  //67 포장 단위 중량 최소
				 // sjjParamList.add("B"                      );  //68 최종 TC BACKUP 구분(B)
				  //sjjParamList.add("COOL_BATCH"             );  //69 최종 등록 TC 명(coolWrsltBatch)
				  sjjParamList.add(sItemname_c              );  //70 품명 CODE
				  sjjParamList.add("COOL_YD"             );  //71 등록자
		          sjjParamList.add(nowDate+nowTime          );  //72 등록 일시
		          sjjParamList.add(sModifier                );  //73 수정자
		          sjjParamList.add(nowDate+nowTime          );  //74 수정 일시
		          sjjParamList.add(sShearItmCd          );  //74 정정지시ITEM
		          sjjParamList.add(sShearItmCd          );  //74 정정실적ITEM
		          if (sPlantGp.equals("C")) {
		              sjjParamList.add("HC"          );  //74 공장공정구분
		          }else{
		        	  sjjParamList.add("HB"          );  //74 공장공정구분
		          }
		          sjjParamList.add("5T");            // 3 작업지시공정('WORD_PROC') 
		          sjjParamList.add("5T");            // 3 통과공정('PASS_PROC')
		          sjjParamList.add(sSlabNo                  );  //75 SLAB번
	                
	                
				  logger.println(LogLevel.DEBUG,"MSG >>냉각 실적 처리  정정지시실적 등록  PARAM:"+sjjParamList ); 
				  int iRetResult  = (int)subCoolWrsltProcDAO.insertCoolWrsltProc(sjjParamList);
				  logger.println(LogLevel.DEBUG,"MSG >>냉각 실적 처리  정정지시실적 등록 DONE:"+iRetResult ); 
	            
	            if ( iRetResult <= 0 ) {
	            	sRetList.add("E");
	            	sRetList.add("냉각 실적 처리  정정지시실적 등록시 오류가 발생 하였습니다.");
		            logger.println(LogLevel.ERROR,"MSG >>"+sRetList ); 
	            	return sRetList;
	            }
	        	
			  }catch(Exception e){
		          	sRetList.add("E");
		        	sRetList.add("냉각 실적 처리  정정지시실적 등록시 오류가 발생 하였습니다.."+e.getMessage());
		            logger.println(LogLevel.ERROR,"MSG >>"+sRetList ); 
		        	return sRetList; 
			  }			  
			  if (sBoReceiptHoldScrapCauseGP.equals("I")) { 		  		  
			  	/*********************************************************************************
			  	 * 품질 종합판정 Call 
			  	 *********************************************************************************/
			  	String HOLD_YN                       = "";      // 보류 여부           ) 값 Y,N              
				String HOLD_CAUSE_CD                 = "";      // 보류 원인 CODE      )                                  
				String ORD_YEOJAE_GP                 = "";      // 주문 여재 구분      ) 값 1,2                  
				String YEOJAE_CAUSE_CD               = "";      // 여재 원인 CODE      ) 2A, 4A, 3B, 3D ??   
				String YEOJAE_OCCUR_DATE             = "";      // 여재 발생 일자      ) YYYYMMDD              
				String YEOJAE_OCCUR_TIME             = "";      // 여재 발생 시각      ) HHMISS              
				String SURFACE_GRADE_CR_USAGE        = "";      // 표면 등급 냉연 용도 )                                 
				String SURFACE_GRADE_STLPIPE_USAGE   = "";      // 표면 등급 강관 용도 )                                 
				String SURFACE_GRADE_STRUCTURE_USAGE = "";      // 표면 등급 구조 용도 )                                 
				String SURFACE_OVERALL_GRADE         = "";      // 표면 종합 등급      )                                 
				String FORM_GRADE                    = "";      // 형상 등급           )                                 
				String WDH_GRADE                     = "";      // 칫수 등급           )                                 
				String APPEAR_OVERALL_GRADE          = "";      // 외관 종합 등급      )                                 
				String APPEAR_GRADE_STAMP_DATE       = "";      // 외관 등급 판정 일자 ) YYYYMMDD                 
				String OVERALL_STAMP_GRADE           = "";      // 종합 판정 등급      )                                  
				String OVERALL_STAMP_DATE            = "";      // 종합 판정 일자      ) HHMISS     

				/***********************************************************************************************
		    	**	품 질 : 종합판정로직 EJB Call  
		    	**	JNDI Name   : JNDITotalDecisionMgt
		    	**	Method Name : processTotalDecision
		    	**	INPUT Parameter :  JDTORecord 
		    	************************************************************************************************
		    	**	========================================================================
		    	**	1. COIL_NO                         COIL 번호
		    	**	2. STEP_NO                         차수
		    	**	2. FNL_SHEAR_DEFECT_CD1            최종 정정 흠 CODE1
		    	**	3. FNL_SHEAR_DEFECT_CD2            최종 정정 흠 CODE2
		    	**	4. FNL_SHEAR_DEFECT_CD3            최종 정정 흠 CODE3
		    	**	5. FNL_SHEAR_DEFECT_CD4            최종 정정 흠 CODE4
		    	**	6. FNL_SHEAR_DEFECT_CD5            최종 정정 흠 CODE5
		    	**	7. REAGENT_PICK_YN                 시편 채취 유무
		    	**	========================================================================
		    	**	OUTPUT Parameter : JDTORecord 
		    	**	========================================================================
		    	**	 1. HOLD_YN                      (보류 여부           ) 값 Y,N
		    	**	 2. HOLD_CAUSE_CD                (보류 원인 CODE      )
		    	**	 3. ORD_YEOJAE_GP                (주문 여재 구분      ) 값 1,2
		    	**	 4. YEOJAE_CAUSE_CD              (여재 원인 CODE      ) 2A, 4A, 3B, 3D ??
		    	**	 5. YEOJAE_OCCUR_DATE            (여재 발생 일자        ) YYYYMMDD      
		    	**	 6. YEOJAE_OCCUR_TIME            (여재 발생 시각        ) HHMISS      
		    	**	 7. SURFACE_GRADE_CR_USAGE       (표면 등급 냉연 용도 ) 
		    	**	 8. SURFACE_GRADE_STLPIPE_USAGE  (표면 등급 강관 용도 ) 
		    	**	 9. SURFACE_GRADE_STRUCTURE_USAGE(표면 등급 구조 용도 ) 
		    	**	10. SURFACE_OVERALL_GRADE        (표면 종합 등급      ) 
		    	**	11. FORM_GRADE                   (형상 등급           )       
		    	**	12. WDH_GRADE                    (칫수 등급           )       
		    	**	13. APPEAR_OVERALL_GRADE         (외관 종합 등급      )       
		    	**	14. APPEAR_GRADE_STAMP_DATE      (외관 등급 판정 일자 ) YYYYMMDD      
		    	**	15. OVERALL_STAMP_GRADE          (종합 판정 등급      )       
		    	**	16. OVERALL_STAMP_DATE           (종합 판정 일자      ) HHMISS      
		    	**	17. PGM_ID                       
		    	**	17. ERR_CODE                                                          
		    	**	18. ERR_MSG                                                          
		    	**	========================================================================
		    	***************************************************************************************************/

				logger.println(LogLevel.DEBUG, "MSG >> 품질: 종합판정로직EJB Call Start" );

				try {
					JDTORecordFactory jDTORecFac  = JDTORecordFactory.getInstance();  //JDTORecord 선언
					JDTORecord jDTORec  = jDTORecFac.create();  
					jDTORec.setField("COIL_NO",sCoilNo);                             // 코일번호
					jDTORec.setField("STEP_NO",new Integer(StringHelper.parseInt(sStepNo,0) + 1));              // 차수
					jDTORec.setField("FNL_SHEAR_DEFECT_CD1",sMidInspectDefectCd1); // 흠코드1              
					jDTORec.setField("FNL_SHEAR_DEFECT_CD2",sMidInspectDefectCd1); // 흠코드2            
					jDTORec.setField("FNL_SHEAR_DEFECT_CD3",sMidInspectDefectCd1); // 흠코드3               
					jDTORec.setField("FNL_SHEAR_DEFECT_CD4",sMidInspectDefectCd1); // 흠코드4              
					jDTORec.setField("FNL_SHEAR_DEFECT_CD5",sMidInspectDefectCd1); // 흠코드5   
					jDTORec.setField("REAGENT_PICK_YN",sReagentPickYn);            // 시편채취유무   
					jDTORec.setField("COIL_T",new Double(sCoilT));                  // 실적두께   
					jDTORec.setField("COIL_W",new Double(sCoilW));                  // 실적폭  
					jDTORec.setField("OPERATION_GP","4");                          // 1:정정실적, 2:보류확정,3:공냉,4:수냉 
					jDTORec.setField("MTL_GP","COIL"); 

					jDTORec.setField("MID_INSPECT_DEFECT_CD1",sMidInspectDefectCd1); // 흠코1              
					jDTORec.setField("MID_INSPECT_DEFECT_CD2",sMidInspectDefectCd2); // 흠코드2            
					jDTORec.setField("MID_INSPECT_DEFECT_CD3",sMidInspectDefectCd3); // 흠코드3               
					jDTORec.setField("MID_INSPECT_DEFECT_CD4",sMidInspectDefectCd4); // 흠코드4              
					jDTORec.setField("MID_INSPECT_DEFECT_CD5",sMidInspectDefectCd5); // 흠코드5 
					
					//*************************************************************************************
					//* 품질: 종합판정로직EJB Call Start
					//************************************************************************************** 
					logger.println(LogLevel.DEBUG, "MSG >> 품질: 종합판정로직EJB Call Start [ JNDITotalDecisionMgt => processTotalDecision ]jDTORec ="+jDTORec );
					/*******************************************************************************************/
					/*******************************************************************************************/
					/********************************품질: 종합판정로직  EJB Call*******************************/
					/*******************************************************************************************/
					/*******************************************************************************************/
				    EJBConnector ejbConn2    = new EJBConnector("hsteelApp","OadecFaEJB",this);
					JDTORecord jDtoPumjilRec = (JDTORecord)ejbConn2.trx("trtHrOadecReq", new Class[]{JDTORecord.class}, 
																							   new Object[]{jDTORec});
					
					 if(!StringHelper.evl(jDtoPumjilRec.getFieldString("ERR_CODE"),"").trim().equals("0")) {
					 	// ERR_CODE 0:정상, 나머지: 비정상            
	    		  	    sErrorGbn   = "E"; 
	    		  	    sErrorGbnCdMsg = "품질: 종합판정로직 EJB CALL 오류발생("+StringHelper.evl(jDtoPumjilRec.getFieldString("ERR_MSG"),"").trim()+")";
			            logger.println(LogLevel.ERROR, "MSG >> "+sErrorGbnCdMsg);
			            sRetList.add(sErrorGbn);
			            sRetList.add(sErrorGbnCdMsg);
	    		  		return sRetList;
					 }	
					 //HOLD_YN                       = StringHelper.evl(jDtoPumjilRec.getFieldString("HOLD_YN"),"").trim();                       // 보류 여부           ) 값 Y,N            
					 //HOLD_CAUSE_CD                 = StringHelper.evl(jDtoPumjilRec.getFieldString("HOLD_CAUSE_CD"),"").trim();                 // 보류 원인 CODE      )                   
					 //ORD_YEOJAE_GP                 = StringHelper.evl(jDtoPumjilRec.getFieldString("ORD_YEOJAE_GP"),"").trim();                 // 주문 여재 구분      ) 값 1,2            
					 //YEOJAE_CAUSE_CD               = StringHelper.evl(jDtoPumjilRec.getFieldString("YEOJAE_CAUSE_CD"),"").trim();               // 여재 원인 CODE      ) 2A, 4A, 3B, 3D ?? 
					 //YEOJAE_OCCUR_DATE             = StringHelper.evl(jDtoPumjilRec.getFieldString("YEOJAE_OCCUR_DATE"),"").trim();             // 여재 발생 일자      ) YYYYMMDD          
					 //YEOJAE_OCCUR_TIME             = StringHelper.evl(jDtoPumjilRec.getFieldString("YEOJAE_OCCUR_TIME"),"").trim();             // 여재 발생 시각      ) HHMISS            
					// SURFACE_GRADE_CR_USAGE        = StringHelper.evl(jDtoPumjilRec.getFieldString("SURFACE_GRADE_CR_USAGE"),"").trim();        // 표면 등급 냉연 용도 )                   
					// SURFACE_GRADE_STLPIPE_USAGE   = StringHelper.evl(jDtoPumjilRec.getFieldString("SURFACE_GRADE_STLPIPE_USAGE"),"").trim();   // 표면 등급 강관 용도 )                   
					// SURFACE_GRADE_STRUCTURE_USAGE = StringHelper.evl(jDtoPumjilRec.getFieldString("SURFACE_GRADE_STRUCTURE_USAGE"),"").trim(); // 표면 등급 구조 용도 )                   
					// SURFACE_OVERALL_GRADE         = StringHelper.evl(jDtoPumjilRec.getFieldString("SURFACE_OVERALL_GRADE"),"").trim();         // 표면 종합 등급      )                   
					 //FORM_GRADE                    = StringHelper.evl(jDtoPumjilRec.getFieldString("FORM_GRADE"),"").trim();                    // 형상 등급           )                   
					 //WDH_GRADE                     = StringHelper.evl(jDtoPumjilRec.getFieldString("WDH_GRADE"),"").trim();                     // 칫수 등급           )                   
					// APPEAR_OVERALL_GRADE          = StringHelper.evl(jDtoPumjilRec.getFieldString("APPEAR_OVERALL_GRADE"),"").trim();          // 외관 종합 등급      )                   
					 //APPEAR_GRADE_STAMP_DATE       = StringHelper.evl(jDtoPumjilRec.getFieldString("APPEAR_GRADE_STAMP_DATE"),"").trim();       // 외관 등급 판정 일자 ) YYYYMMDD          
					 OVERALL_STAMP_GRADE           = StringHelper.evl(jDtoPumjilRec.getFieldString("OVERALL_STAMP_GRADE"),"").trim();           // 종합 판정 등급      )                   
					 //OVERALL_STAMP_DATE            = StringHelper.evl(jDtoPumjilRec.getFieldString("OVERALL_STAMP_DATE"),"").trim();            // 종합 판정 일자      ) HHMISS

					 logger.println(LogLevel.DEBUG, "MSG >> ************품질: 종합판정로직EJB Call 결과**********************");
					 logger.println(LogLevel.DEBUG, "ERR_CODE            ERR_CODE                      ="+ jDtoPumjilRec.getFieldString("ERR_CODE") );
					 logger.println(LogLevel.DEBUG, "ERR_MSG             ERR_MSG                       ="+ jDtoPumjilRec.getFieldString("ERR_MSG") );
					// logger.println(LogLevel.DEBUG, "보류 여부           HOLD_YN                       ="+ HOLD_YN                        );
					// logger.println(LogLevel.DEBUG, "보류 원인 CODE      HOLD_CAUSE_CD                 ="+ HOLD_CAUSE_CD                  );
					 //logger.println(LogLevel.DEBUG, "주문 여재 구분      ORD_YEOJAE_GP                 ="+ ORD_YEOJAE_GP                  );
					 //logger.println(LogLevel.DEBUG, "여재 원인 CODE      YEOJAE_CAUSE_CD               ="+ YEOJAE_CAUSE_CD                );
					 //logger.println(LogLevel.DEBUG, "여재 발생 일자      YEOJAE_OCCUR_DATE             ="+ YEOJAE_OCCUR_DATE              );
					 //logger.println(LogLevel.DEBUG, "여재 발생 시각      YEOJAE_OCCUR_TIME             ="+ YEOJAE_OCCUR_TIME              );
					 //logger.println(LogLevel.DEBUG, "표면 등급 냉연 용도 SURFACE_GRADE_CR_USAGE        ="+ SURFACE_GRADE_CR_USAGE         );
					 //logger.println(LogLevel.DEBUG, "표면 등급 강관 용도 SURFACE_GRADE_STLPIPE_USAGE   ="+ SURFACE_GRADE_STLPIPE_USAGE    );
					 //logger.println(LogLevel.DEBUG, "표면 등급 구조 용도 SURFACE_GRADE_STRUCTURE_USAGE ="+ SURFACE_GRADE_STRUCTURE_USAGE  );
					 //logger.println(LogLevel.DEBUG, "표면 종합 등급      SURFACE_OVERALL_GRADE         ="+ SURFACE_OVERALL_GRADE          );
					 //logger.println(LogLevel.DEBUG, "형상 등급           FORM_GRADE                    ="+ FORM_GRADE                     );
					 //logger.println(LogLevel.DEBUG, "칫수 등급           WDH_GRADE                     ="+ WDH_GRADE                      );
					 //logger.println(LogLevel.DEBUG, "외관 종합 등급      APPEAR_OVERALL_GRADE          ="+ APPEAR_OVERALL_GRADE           );
					 //logger.println(LogLevel.DEBUG, "외관 등급 판정 일자 APPEAR_GRADE_STAMP_DATE       ="+ APPEAR_GRADE_STAMP_DATE        );
					 //logger.println(LogLevel.DEBUG, "종합 판정 등급      OVERALL_STAMP_GRADE           ="+ OVERALL_STAMP_GRADE            );
					 //logger.println(LogLevel.DEBUG, "종합 판정 일자      OVERALL_STAMP_DATE            ="+ OVERALL_STAMP_DATE             );

    			} catch(Exception e) { 
    				sErrorGbn   = "E"; 
    				sErrorGbnCdMsg = "품질: 종합판정로직 EJB CALL 오류발생("+e+")";
		            logger.println(LogLevel.ERROR, "MSG >> "+sErrorGbnCdMsg);
		            sRetList.add(sErrorGbn);
		            sRetList.add(sErrorGbnCdMsg);
    		  		return sRetList;

				}
			  }
    	    	/* ---------------------------------------------------------
    	    	 * COIL공통 조회 
    	    	 * 조  건  : COIL번호 <= 모코일번호 또는 cOIL번호 조회
    	    	 * 테이블 : COIL공통 조회(TB_PM_COILCOMM)  
    	    	 * ---------------------------------------------------------*/     		
    			/*- DAO선언  : ABMillReceiveRollingInfoDAO  -*/     		
    	      	CoolWrsltProcDAO abReceiveDAO = null;
				abReceiveDAO = new CoolWrsltProcDAO();
				
    	      	logger.println(LogLevel.DEBUG, "Msg >> 종합판정로직EJB Call이후  COIL공통  자료존재 체크 조회  >> ");
    	        try { 		        
    		          	List coilCommData = abReceiveDAO.getCoilCommDtl(sCoilNo);
    					if (coilCommData.size() <= 0) {
    		    	            sErrorGbn   = "E"; 
    		    	            sErrorGbnCdMsg = "종합판정로직EJB Call이후 COIL공통 자료가 존재하지않습니다 Coil번호="+sCoilNo;
    		    		 		logger.println(LogLevel.ERROR,"Error Msg >> " +sErrorGbnCdMsg);
    					} else {
    				 		    logger.println(LogLevel.DEBUG, "Msg >> COIL공통 자료(coilCommData) : "+coilCommData.size() );
    							JDTORecord jDtocoilComm = (JDTORecord)coilCommData.get(0);
    							sCurrProgCd           =  StringHelper.evl(jDtocoilComm.getFieldString("CURR_PROG_CD"),"");                    // 현재진도CODE F:판정보류, G:종합판정대기,  H:입고 
    							sOrdNo                =  StringHelper.evl(jDtocoilComm.getFieldString("ORD_NO"),"").trim();                   // 주문번호 
    							sOrdDtl               =  StringHelper.evl(jDtocoilComm.getFieldString("ORD_DTL"),"").trim();                  // 주문행번
    							sOrdYeojaeGp          =  StringHelper.evl(jDtocoilComm.getFieldString("ORD_YEOJAE_GP"),"").trim();            // 주문여재구분
    							sYeojaeCauseCD        =  StringHelper.evl(jDtocoilComm.getFieldString("YEOJAE_CAUSE_CD"),"").trim();          // 여재 원인 CODE
    							sPlntprocCD           =  StringHelper.evl(jDtocoilComm.getFieldString("PLNT_PROC_CD"),"").trim();          // 공장공정코드
    							sRecordprogStat       =  StringHelper.evl(jDtocoilComm.getFieldString("RECORD_PROG_STAT"),"").trim();          // 재료상태구분
    							sRecordendGP       =  StringHelper.evl(jDtocoilComm.getFieldString("RECORD_END_GP"),"").trim();          // 레코드종료구분
    							sBefoprogCD        =  StringHelper.evl(jDtocoilComm.getFieldString("BEFO_PROG_CD"),"").trim();          // 전진도코드
    							sBefordNO      =  StringHelper.evl(jDtocoilComm.getFieldString("BEF_ORD_NO"),"").trim();          // 전주문번호
    							sBefordDtl        =  StringHelper.evl(jDtocoilComm.getFieldString("BEF_ORD_DTL"),"").trim();          // 전주문행번
    							sMmatlfeeNo      =  StringHelper.evl(jDtocoilComm.getFieldString("MMATL_FEE_NO"),"").trim();          // 모재료번호
    					        if (sCurrProgCd.equals("F")){
    					        	sReceiptHoldScrapCauseGP = "B"; //보류
    					        }		
    					 		logger.println(LogLevel.DEBUG, "Msg >>종합판정로직EJB Call이후 현재진도CODE   : "+sCurrProgCd );
    					 		logger.println(LogLevel.DEBUG, "Msg >>종합판정로직EJB Call이후  RECORD진행상태 : "+sYeojaeCauseCD);
    					}
    		 		    logger.println(LogLevel.DEBUG, "Msg >> ----------------------- << ");
    	 		}catch(DAOException daoe){
    	 			daoe.printStackTrace();
    	            sErrorGbn   = "E"; 
    	            sErrorGbnCdMsg = " 종합판정로직EJB Call이후 COIL공통 COIL번호 상태조회시 오류발생 DAOException";
    	    	}catch(Exception e){
    	         	// 오류발생으로 return
    	 			e.printStackTrace();
    	            sErrorGbn   = "E"; 
    	            sErrorGbnCdMsg = " 종합판정로직EJB Call이후 COIL공통 COIL번호 상태조회시 오류발생 Exception"+e;
    	    	}
    	 		//-오류발생시 처리
    	    	if(sErrorGbn.equals("E")) {
    			 		logger.println(LogLevel.ERROR,"Error Msg >> " +sErrorGbnCdMsg);
    			 		sRetList.add(sErrorGbn);
    			 		sRetList.add(sErrorGbnCdMsg);
    			 		return sRetList;
    		   	}		
		    	        
				/**********************************************************************************
				 *  공정 내부 JMS[ZZPM001] 시작
				 *  POPT021	정정실적 : 모든코일
				 *********************************************************************************/
				 try {
					 
					 JDTORecord tcRecord = null;
					 tcRecord = JDTORecordFactory.getInstance().create(); 
					 tcRecord.setField("JMS_TC_CD", "POPT021");
					 tcRecord.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
					 tcRecord.setField("STL_NO", sCoilNo);  //재료번호
					 tcRecord.setField("ORD_NO", sOrdNo);  //주문번호
					 tcRecord.setField("ORD_DTL", sOrdDtl); //주문행번 
					 tcRecord.setField("PLNT_PROC_CD", sPlntprocCD); //공장공정코드
					 //tcRecord.setField("STL_APPEAR_GP", "B"); //재료외형구분
					 tcRecord.setField("STL_APPEAR_GP", "E"); //재료외형구분       //20091019
					 tcRecord.setField("CURR_PROG_CD", sCurrProgCd); //현재진도코드
					 tcRecord.setField("ORD_YEOJAE_GP", sOrdYeojaeGp);// 주문여재구분
					 double dCoilWt                  = StringHelper.parseDouble(sRealWt);  /* COIL 실 중량 */
					 if (sWtDecisionCd.equals("NC"))
						 dCoilWt                  = StringHelper.parseDouble(sCalWt);   /* COIL 이론 중량 */
					 tcRecord.setField("STL_WT", dCoilWt+""); //재료중량
					 tcRecord.setField("DS_MTL_WT", ""); //설계재료중량
					 tcRecord.setField("MTL_STAT_GP", sRecordprogStat); //재료상태구분  
					 tcRecord.setField("RECORD_END_GP", sRecordendGP); //Record종료구분 
					 tcRecord.setField("RECORD_END_GP1", ""); //레코드종료구분1
					 tcRecord.setField("BEFO_PROG_CD", sBefoprogCD);  //전진도코드
					 tcRecord.setField("BEF_ORD_NO", sBefordNO); //전주문번호
					 tcRecord.setField("BEF_ORD_DTL", sBefordDtl); //전주문행번
					 tcRecord.setField("MMATL_FEE_NO", sMmatlfeeNo); //모재료번호  
					 tcRecord.setField("ORDERTRANS_MATCH_GP", ""); //목전충당구분
					 					 
					 EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
					 Boolean isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{JDTORecord.class},
						  	  	 new Object[]{tcRecord});
					 logger.println(LogLevel.DEBUG,this, "내부IF호출===정정실적입고 수신 후에 발생.===");
					 
		//기존 AB열연 전문송신			 
	 	/*
				  ZZPM001 sndZZPM001 = new ZZPM001();
				  sndZZPM001.setTcCode("POPM020");                        //정정실적 입고
	    
				  sndZZPM001.setTcDate(sCurrDate);                        //현재일자
				  sndZZPM001.setTcTime(sCurrTime);                        //현재시간
				  sndZZPM001.setStl_no(sCoilNo);                          //재료                              
				  sndZZPM001.setPlant_gp(sPlantGp);                       //공장구분                          
				  sndZZPM001.setOrd_no(sOrdNo);                           //주문번호                          
				  sndZZPM001.setOrd_dtl(sOrdDtl);                         //행번 
				  
				  sndZZPM001.setOrd_yeojae_gp(sOrdYeojaeGp);              //주여구분                          
				  sndZZPM001.setStl_prog_cd(sCurrProgCd);                 //진도코드
	
				  double dCoilWt                  = StringHelper.parseDouble(sRealWt); 
			      if (sWtDecisionCd.equals("NC"))                              
			        	  dCoilWt                  = StringHelper.parseDouble(sCalWt);  
				  sndZZPM001.setStl_wt((double) dCoilWt);                 //중량
				  
				  sndZZPM001.setYeojae_cause_cd(sYeojaeCauseCD);          //여재원인코드
				  
				  //sndZZPM001.setBefo_ord_no(sCmOrdNo);                  //제작번호(전)                      
				  //sndZZPM001.setBefo_ord_dtl(String);                   //행번(전)                          
				  //sndZZPM001.setBefo_ord_yeojae_gp(String);             //주여구분(전)                      
				  //sndZZPM001.setBefo_prog_cd(String);                   //진도코드(전)                      
				  //sndZZPM001.setOrdertrans_match_gp(String);            //목전충당구분                      
				  //sndZZPM001.setWork_cancel_cd(String);                 //취소코드(취소 C)                  
				  //sndZZPM001.setOrdertrans_cause_cd(String);            //목전원인코드                      
				  //setReturn_gp_cause_cd()                               //반납사유코드(COIL반납처리)        
				  //sndZZPM001.setDist_end_cause(String);                 //출하종결사유                      
				  //sndZZPM001.setReturn_gp(String);                      //반송구분(생산관제) (C:출강,S:SLAB)
				  //sndZZPM001.setOver_yeojae_prod_gp("");                //SLAB잉여재생산구분(*;잉여재)      
				  //sndZZPM001.setPlan_slab_no(sCmPlanSlabNo);            //예정 SLAB NO (전단실적,압연실적) 
				  //sndZZPM001.setStl_ord_hcr_gp(String);                 //HCR구분                           
				  //sndZZPM001.setSlab_missno_reject_cd("S");             //결번/REJECT/SCRAP
				  //sJaZZPM001.setSlab_reheat_slab_gp(sAbOccurGpCd);      //SLAB재열재구분
				  
				  sndZZPM001.setHold_stl_stamp_gp(sBoReceiptHoldScrapCauseGP);  //보류재판정구분\
				  
				  //sndZZPM001.setNext_demand_proc(String);                //다음요구공정                      
				  //sndZZPM001.setScarfing_yn(String);                     //스카핑유무    
				  //sndZZPM001.setCoil_divide_qnty(0);                     //COIL분할수 일반코일
				  //sndZZPM001.setSlab_unit_sendback_gp_pc(String);        //RETURN                            
				  //sndZZPM001.setReg_ddtt(String);                        //등록일시                        
				  //sndZZPM001.setSeqno(int);                              //순번                            
				  //sndZZPM001.setTc_occur_pgm(String);                    //TC발생프로그램                  
				  //sndZZPM001.setTc_err_cd(String);                       //TC_ERRORCODE                    
				  //sndZZPM001.setTc_err_contents(String);                 //TC_ERROR내용                    
				  sndZZPM001.setSlab_no(sSlabNo);                          //SLAB번호                        
			  	  
			  	  EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
				  Boolean isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{CommonModel.class},
															  	  	          new Object[]{sndZZPM001});
				
				  logger.println(LogLevel.DEBUG,this, "내부IF호출===정정실적입고 수신 후에 발생.===");  
			*/
			} catch(Exception e) { 
				sErrorGbn   = "E"; 
				sErrorGbnCdMsg = "공정:  정정실적입고 내부JMS EJB CALL 오류발생("+e+")";
	            logger.println(LogLevel.ERROR, "MSG >> "+sErrorGbnCdMsg);
	            sRetList.add(sErrorGbn);
	            sRetList.add(sErrorGbnCdMsg);
		  		return sRetList;
			} 	
		  	sRetList.add("S");
			sRetList.add("");
			//sRetList.add(sndZZPM001);
					
			return sRetList;  
	} 
	 
	/**
	 * 오퍼레이션명 : 
	 *
	 * B열연 공정 보류판정 메쇼드
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public List abBoRyuChuriPanJungMain(String sPlantGbn, String sProcGbn, ArrayList abBoRyuChuri){
        /*------------------------------------------------------------------------------
		 * 파라메터 : 1. 공장구분  A:A열연,   B:B열연 
		 *          2. 공정구분  HFL:HotFinal, SPM:SkinPassMill, A:공냉 , T:수냉
		 *          3. List (CoilNo,두께,폭,중량)        
		 * ----------------------------------------------------------------------------- */
		 /* 처리순서
		  * -
		  */	
        logger.println(LogLevel.DEBUG, "-- AB열연 공정 보류판정 메쇼드 => abBoRyuChuriPanJungMain() -- ");
		logger.println(LogLevel.DEBUG, "-- AB열연 공정 보류판정 파라메터  => 공장구분 = " + sPlantGbn);
		logger.println(LogLevel.DEBUG, "-- AB열연 공정 보류판정 파라메터  => 공정구분 = " + sProcGbn);
		logger.println(LogLevel.DEBUG, "-- AB열연 공정 보류판정 파라메터  => List     = " + abBoRyuChuri);
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        List lErrList = new ArrayList();

		/* 공통 변수 적용부문 */
        String  sErrorGbn                  = "";    /* 오류구분     */   
        String  sErrorGbnCdMsg             = "";    /* 오류메세지 */     
        String  sDateTime                  = "";    /* 일시     */       
        String  sDate                      = "";    /* 일자     */       
        String  sTime                      = "";    /* 시각     */       
        String  sTmp                       = "";    /* temp */           
        String  sCurrDate8                 = "";    //현재일자           
        String  sCurrTime6                 = "";    //현재시각           
        String  sCurrDateTime14            = "";    //현재일시           

        /* Coil정정작업지시실적(TB_PO_COILSHEARORD_WRSLT) */
        String   sAStat                       =  "";   // A열연상태                   
        String   sCoilNo                      =  "";   // COIL 번호          
        int      iJjStepNo                    =   0;   // 차수                        
        String   sJjPlantGp                   =  "";   // 공장 구분                   
        String   sJjProcGp                    =  "";   // 공정 구분                   
        String   sJjWorkStat                  =  "";   // 작업 상태                      
        double    fJjCoilT                     =   0;   // COIL 두께                   
        double    fJjCoilW                     =   0;   // COIL 폭                     
        String   sJjWtGp                      =  "";   // 중량 구분                   
        int      iJjRealWt                    =   0;   // 실 중량                     
        int      iJjCalWt                     =   0;   // 계산 중량                   

        double    fJjAimT                      =   0;   // 목표 두께                   
        double    fJjTTolMax                   =   0;   // 두께 허용공차 상한          
        double    fJjTTolMin                   =   0;   // 두께 허용공차 하한 
        
        double    fJjAimW                      =   0;   // 목표 폭                     
        double    fJjWTolMax                   =   0;   // 폭 허용공차 상한            
        double    fJjWTolMin                   =   0;   // 폭 허용공차 하한            
        String   sJjDomeExportGp              =  "";   // 내수 수출 구분              
        String   sJjSpecAbbsym                =  "";   // 규격 약호                   
        String   sJjSpecYy                    =  "";   // 규격 년도                   
        double    fJjOrdT                      =   0;   // 주문 두께                   
        double    fJjOrdW                      =   0;   // 주문 폭   
        
        double    fWrsltT                      =   0;   // 실적 두께 :10-3% mm                 
        double    fWrsltW                      =   0;   // 실적 폭    :mm  
        double    fWrsltRealWt                 =   0;   // 실적 실 중량                
        
        int      iJjWrsltLen                  =   0;   // 실적 길이 :m                
        String   sJjWrsltWtGp                 =  "";   // 실적 중량 구분 (NA, NC)              
        int      iJjWrsltCalWt                =   0;   // 실적 계산 중량              
        String   sJjReceiptHoldScrapCauseGp   =  "";   // 입고 보류 SCRAP 원인 구분   
        String   sJjHoldCauseCd               =  "";   // 보류 원인 CODE              
        String   sJjHoldProgStat              =  "";   // 보류 진행 상태              
        String   sJjHoldStampDate             =  "";   // 보류 판정 일자              
        String   sJjHoldStampTime             =  "";   // 보류 판정 시각              
 
        double    fJjWrapUnitWtMax             =   0;   // 포장 단위 중량 상한         
        double    fJjWrapUnitWtMin             =   0;   // 포장 단위 중량 하한         
        
        /*-- Coil공통TB 변수 --*/
        String  sCmCoilNo                     = "";    /* COIL번호 */
        String  sCmPlantGp                    = "";    /* 공장구분 */
        String  sCmRecordProgStat             = "";    /* RECORD진행상태 */
        String  sCmCurrProgCd                 = "";    /* 현재진도CODE */
        String  sCmOrdYeojaeGp                = "";    /* 주문여재구분 */
        double   fCmCoilT                      = 0;     /* COIL두께 */
        double   fCmCoilW                      = 0;     /* COIL폭 */
        String  sCmWtGp                       = "";    /* 중량구분 */
        int     iCmNetWeighWt                 = 0;     /* NET계량중량 */
        int     iCmNetCalWt                   = 0;     /* NET계산중량 */
        double     fCmCoilWt                     = 0;     /* COIL중량 */
        String  sCmOrdNo                      = "";    /* 주문번호 */
        String  sCmOrdDtl                     = "";    /* 주문행번 */
        String  sCmItemnameCd                 = "";    /* 품명CODE */
        String  sCmYeojaeCauseCd              = "";    /* 여재원인CODE */
        String  sCmYeojaeOccurDate            = "";    /* 여재발생일자 */
        String  sCmYeojaeOccurTime            = "";    /* 여재발생시각 */
        String  sCmHoldCauseCd                =  "";   // 보류 원인 CODE              
        String  sCmHoldProgStat               =  "";   // 보류 진행 상태              
        String  sCmHoldStampDate              =  "";   // 보류 판정 일자              
        String  sCmHoldStampTime              =  "";   // 보류 판정 시각              
        
        String  sItemNameCd                   = "";    /* 품명코드 */
        String  sCheckHRPLATE				  = "" ;   
        
        /*- 주문진행TB 자료조회(TB_PM_ORDPROG) -*/
        String sRecordProgStat             = "";    // RecordProgStat                 RECORD진행상태
        String sProdEndGp                  = "";    // PROD_END_GP                    생산 종료 구분              
        String sDeliverTolGp               = "";    // DELIVER_TOL_GP                 인도 허용오차 구분          
        int    iDeliverTolMin              =  0;    // DELIVER_TOL_MIN                인도 허용오차 MIN           
        int    iDeliverTolMax              =  0;    // DELIVER_TOL_MAX                인도 허용오차 MAX           
        double  fIndia                      =  0;    // INDIA                          내경                        
        double  fOutdia                     =  0;    // OUTDIA                         외경                        
        int    iOrdQnty                    =  0;    // ORD_QNTY                       주문 량                     
        int    iPutQnty                    =  0;    // PUT_QNTY                       투입 량                     
        int    iIncPutQnty                 =  0;    // INC_PUT_QNTY                   증가 투입 량                
        double  fStlDesignWaitQnty          =  0;    // STL_DESIGN_WAIT_QNTY           재료 설계 대기 량           
        double  fHeatoutOrdWaitQntyA        =  0;    // HEATOUT_ORD_WAIT_QNTY_A        출강 지시 대기 량 A         
        double  fHeatoutWaitQntyB           =  0;    // HEATOUT_WAIT_QNTY_B            출강 대기 량 B              
        double  fSlabFrtomoveWordWaitQntyB  =  0;    // SLAB_FRTOMOVE_WORD_WAIT_QNTY_B SLAB 이송 작업지시 대기 량 B
        double  fSlabFrtomoveWwaitQntyC     =  0;    // SLAB_FRTOMOVE_WWAIT_QNTY_C     SLAB 이송 작업대기 량 C     
        double  fSlabShearWwaitQntyD        =  0;    // SLAB_SHEAR_WWAIT_QNTY_D        SLAB 정정 작업대기 량 D     
        double  fSlabMillWordWaitQntyE      =  0;    // SLAB_MILL_WORD_WAIT_QNTY_E     SLAB 압연 작업지시 대기 량 E
        double  fSlabMillWwaitQntyF         =  0;    // SLAB_MILL_WWAIT_QNTY_F         SLAB 압연 작업대기 량 F     
        double  fSlabTrimWwaitQntyG         =  0;    // SLAB_TRIM_WWAIT_QNTY_G         SLAB 절단 작업대기 량 G     
        double  fCoilStlqltyStampWaitQntyA  =  0;    // COIL_STLQLTY_STAMP_WAIT_QNTY_A COIL 재질 판정 대기 량 A    
        double  fCoilShearWordWaitQntyB     =  0;    // COIL_SHEAR_WORD_WAIT_QNTY_B    COIL 정정 작업지시 대기 량 B
        double  fCoilShearWwaitQntyC        =  0;    // COIL_SHEAR_WWAIT_QNTY_C        COIL 정정 작업대기 량 C     
        double  fCoilFrtomoveWordWaitQntyD  =  0;    // COIL_FRTOMOVE_WORD_WAIT_QNTY_D COIL 이송 작업지시 대기 량 D
        double  fCoilFrtomoveWwaitQntyE     =  0;    // COIL_FRTOMOVE_WWAIT_QNTY_E     COIL 이송 작업대기 량 E     
        double  fCoilStampHoldQntyF         =  0;    // COIL_STAMP_HOLD_QNTY_F         COIL 판정 보류 량 F         
        double  fCoilOverallStampWaitQntyG  =  0;    // COIL_OVERALL_STAMP_WAIT_QNTY_G COIL 종합 판정 대기 량 G    
        double  fCoilReceiptWaitQntyH       =  0;    // COIL_RECEIPT_WAIT_QNTY_H       COIL 입고 대기 량 H         
        double  fCoilReturnWaitQntyJ        =  0;    // COIL_RETURN_WAIT_QNTY_J        COIL 반납 대기 량 J         
        double  fCoilDistWordWaitQntyK      =  0;    // COIL_DIST_WORD_WAIT_QNTY_K     COIL 출하 작업지시 대기 량 K
        double  fCoilDistWwaitQntyL         =  0;    // COIL_DIST_WWAIT_QNTY_L         COIL 출하 작업대기 량 L     
        double  fCoilDistQntyM              =  0;    // COIL_DIST_QNTY_M               COIL 출하 량 M              
        double  fCoilShiploadingWaitQntyN   =  0;    // COIL_SHIPLOADING_WAIT_QNTY_N   COIL 선적 대기 량 N         
        double  fCoilDeliverDoneQntyP       =  0;    // COIL_DELIVER_DONE_QNTY_P       COIL 인도 완료 량 P         

        /*- 주문공통TB 자료조회(TB_SM_ORDCOMM) -*/
        String sSmTeamCd ="";  // 영업 TEAM CODE 

		//현재 날짜 저장
		sCurrDate8 = DateHelper.currentTime("yyyyMMdd");
		sCurrTime6 = DateHelper.currentTime("HHmmss");
		sCurrDateTime14 = sCurrDate8 + sCurrTime6;
		logger.println(LogLevel.DEBUG,"현재날짜 저장 완료" + sCurrDate8 + " " + sCurrTime6);
		
      	logger.println(LogLevel.DEBUG, "Msg >- AB열연 공정 보류판정  ->> ");
      	sCoilNo       =  StringHelper.evl((String)abBoRyuChuri.get(0),"").trim();   // Coil번호
      	fWrsltT       = ((Double)abBoRyuChuri.get(1)).doubleValue(); //두께
      	fWrsltW       = ((Double)abBoRyuChuri.get(2)).doubleValue(); //폭
      	fWrsltRealWt  = ((Double)abBoRyuChuri.get(3)).doubleValue(); //중량
		
		/*- DAO선언  : CoolWrsltProcDAO  -*/     		
		CoolWrsltProcDAO abReceiveDAO = null;
		abReceiveDAO = new CoolWrsltProcDAO();
      
    	/* ---------------------------------------------------------
    	 * COIL공통 조회 
    	 * 조  건  : COIL번호 <= 모코일번호 또는 cOIL번호 조회
    	 * 테이블 : COIL공통 조회(TB_PM_COILCOMM)  
    	 * ---------------------------------------------------------*/     		
      	logger.println(LogLevel.DEBUG, "Msg >> COIL공통  자료존재 체크 조회  >> 공정구분="+sProcGbn);
        try { 		        
	          	List coilCommData = abReceiveDAO.getCoilCommDtl(sCoilNo);
				if (coilCommData.size() <= 0) 
				{
	    	            sErrorGbn   = "E"; 
	    	            sErrorGbnCdMsg = " COIL공통 자료가 존재하지않습니다 Coil번호="+sCoilNo;
	    		 		logger.println(LogLevel.ERROR,"Error Msg >> " +sErrorGbnCdMsg);
				} else {
			 		    logger.println(LogLevel.DEBUG, "Msg >> COIL공통 자료(coilCommData) : "+coilCommData );
						JDTORecord jDtocoilComm = (JDTORecord)coilCommData.get(0);
						sCmCurrProgCd           =  StringHelper.evl(jDtocoilComm.getFieldString("CURR_PROG_CD"),"");                    // 현재진도CODE C:정정작업대기 
						sCmRecordProgStat       =  StringHelper.evl(jDtocoilComm.getFieldString("RECORD_PROG_STAT"),"");                // RECORD 진행 상태
				        sCmOrdNo                =  StringHelper.evl(jDtocoilComm.getFieldString("ORD_NO"),"").trim();                   // 주문번호 
				        sCmOrdDtl               =  StringHelper.evl(jDtocoilComm.getFieldString("ORD_DTL"),"").trim();                  // 주문행번
				        sCmOrdYeojaeGp          =  StringHelper.evl(jDtocoilComm.getFieldString("ORD_YEOJAE_GP"),"").trim();            // 주문여재구분
				        sCmYeojaeCauseCd        =  StringHelper.evl(jDtocoilComm.getFieldString("YEOJAE_CAUSE_CD"),"").trim();          // 여재 원인 CODE
				        fCmCoilWt               =  StringHelper.parseDouble(jDtocoilComm.getFieldString("COIL_WT"));                        // Coil중량
				        sItemNameCd                =  StringHelper.evl(jDtocoilComm.getFieldString("ITEMNAME_CD"),"").trim();                   // 품명코드
				        
				        sCheckHRPLATE 			= sItemNameCd.substring(2,3);
				        
				        logger.println(LogLevel.DEBUG, "Msg >> 현재진도CODE   : "+sCmCurrProgCd );
				 		logger.println(LogLevel.DEBUG, "Msg >> RECORD진행상태 : "+sCmRecordProgStat);
						if (!sCmOrdYeojaeGp.equals("1")) {
					        logger.println(LogLevel.DEBUG, "Msg >> 주문여재구분이 여재 이므로 보류판정하지않습니다  : "+sCmOrdYeojaeGp );
					 		lErrList.add("S");              //0: 오류코드  E:오류, S:성공
					 		lErrList.add("");               //1: 오류메세지
					 		lErrList.add("I");              //2: 판정구분  I:입고, B:보류
					 		lErrList.add("");               //3: 보류 코드 
					 		lErrList.add(sCmOrdYeojaeGp);   //4: 여재구분      1:주문,2:여재 
					 		lErrList.add(sCmYeojaeCauseCd); //5: 여재원인코드 
					 		lErrList.add("");               //6: OVER ROLL 
					 		lErrList.add("");               //7: 단중제한 
					 		lErrList.add("");               //8: 칫수이상 
					 		return lErrList;
						}
						if (sCmRecordProgStat.equals("3")) {
				            sErrorGbn   = "E"; 
				            sErrorGbnCdMsg = " Coil공통에 COIL번호의 레코드 상태(3)가 이미종료 되었습니다. 진도코드 ="+sCmRecordProgStat;
						}
				}
	 		    logger.println(LogLevel.DEBUG, "Msg >> ----------------------- << ");
 		}catch(DAOException daoe){
 			daoe.printStackTrace();
            sErrorGbn   = "E"; 
            sErrorGbnCdMsg = " COIL공통 COIL번호 상태조회시 오류발생 DAOException";
    	}catch(Exception e){
         	// 오류발생으로 return
 			e.printStackTrace();
            sErrorGbn   = "E"; 
            sErrorGbnCdMsg = " COIL공통 COIL번호 상태조회시 오류발생 Exception"+e;
    	}
 		//-오류발생시 처리
    	if(sErrorGbn.equals("E")) {
		 		logger.println(LogLevel.ERROR,"Error Msg >> " +sErrorGbnCdMsg);
		 		lErrList.add(sErrorGbn);      //0: 오류코드  E:오류, S:성공
		 		lErrList.add(sErrorGbnCdMsg); //1: 오류메세지
		 		lErrList.add("");             //2: 판정구분  I:입고, B:보류
		 		lErrList.add("");             //3: 보류 코드 
		 		lErrList.add("");             //4: 여재구분      1:주문,2:여재 
		 		lErrList.add("");             //5: 여재원인코드 
		 		lErrList.add("");             //6: OVER ROLL 
		 		lErrList.add("");             //7: 단중제한 
		 		lErrList.add("");             //8: 칫수이상 
		 		return lErrList;
	   	}
    	//
    	/* ---------------------------------------------------------
         * 주문진행TB 자료조회(TB_PM_ORDPROG)     		
     	 * 조  건  : 주문번호, 행번
    	 * 테이블 : 주문진행TB(TB_PM_ORDPROG)   
    	 * ---------------------------------------------------------*/     		
      	logger.println(LogLevel.DEBUG, "Msg >> 주문진행TB 자료조회 ");
        try { 
        	    List ordParList = new ArrayList();
        	    ordParList.add(sCmOrdNo); 
        	    ordParList.add(sCmOrdDtl); 
				List OrdProgData = abReceiveDAO.getOrdProgDtl(ordParList);
				if (OrdProgData.size() <= 0) 
				{
	    	            sErrorGbn   = "E"; 
	    	            sErrorGbnCdMsg = " 주문진행TB 자료가 존재하지 않습니다  주문번호행번="+ordParList;
				} else {
						logger.println(LogLevel.DEBUG, "Msg >> 주문진행           : "+OrdProgData );
						JDTORecord jDtoOrdProg = (JDTORecord)OrdProgData.get(0);
						sRecordProgStat             =  StringHelper.evl(jDtoOrdProg.getFieldString("RECORD_PROG_STAT"),"").trim();             // 레코드 진행 상태              
						sProdEndGp                  =  StringHelper.evl(jDtoOrdProg.getFieldString("PROD_END_GP"),"").trim();                  // 생산 종료 구분              
						sDeliverTolGp               =  StringHelper.evl(jDtoOrdProg.getFieldString("DELIVER_TOL_GP"),"").trim();               // 인도 허용오차 구분
						
					    fJjOrdT                     =  StringHelper.parseDouble(StringHelper.evl(jDtoOrdProg.getFieldString("ORD_CONV_T"),"0").trim());	 //주문 두께  
					    fJjOrdW                     =  StringHelper.parseDouble(StringHelper.evl(jDtoOrdProg.getFieldString("ORD_CONV_W"),"0").trim());	 //주문 폭  
						fJjTTolMax                  =  StringHelper.parseDouble(StringHelper.evl(jDtoOrdProg.getFieldString("T_TOL_MAX"),"0").trim());    //두께 허용오차 최대
						fJjTTolMin                  =  StringHelper.parseDouble(StringHelper.evl(jDtoOrdProg.getFieldString("T_TOL_MIN"),"0").trim());    //두께 허용오차 최소
						fJjWTolMax                  =  StringHelper.parseDouble(StringHelper.evl(jDtoOrdProg.getFieldString("W_TOL_MAX"),"0").trim());    //폭 허용오차 최대  
						fJjWTolMin                  =  StringHelper.parseDouble(StringHelper.evl(jDtoOrdProg.getFieldString("W_TOL_MIN"),"0").trim());    //폭 허용오차 최소  
				        fJjWrapUnitWtMin            =  StringHelper.parseInt(StringHelper.evl(jDtoOrdProg.getFieldString("WRAPUNIT_WT_EA_MIN"),"0").trim());	//포장 단위 중량 최소
						fJjWrapUnitWtMax            =  StringHelper.parseInt(StringHelper.evl(jDtoOrdProg.getFieldString("WRAPUNIT_WT_EA_MAX"),"0").trim());	//포장 단위 중량 최대

						iDeliverTolMin              =  StringHelper.parseInt(jDtoOrdProg.getFieldString("DELIVER_TOL_MINUS"));                   // 인도 허용오차 MIN            
						iDeliverTolMax              =  StringHelper.parseInt(jDtoOrdProg.getFieldString("DELIVER_TOL_PLUS"));                   // 인도 허용오차 MAX            

						fIndia                      =  StringHelper.parseDouble(jDtoOrdProg.getFieldString("INDIA"));                          // 내경                        
						fOutdia                     =  StringHelper.parseDouble(jDtoOrdProg.getFieldString("OUTDIA"));                         // 외경                        

						iOrdQnty                    =  StringHelper.parseInt(jDtoOrdProg.getFieldString("ORD_DTL_WT"));                         // 주문 량                     
						iPutQnty                    =  StringHelper.parseInt(jDtoOrdProg.getFieldString("PUT_QNTY"));                         // 투입 량                     
						iIncPutQnty                 =  StringHelper.parseInt(jDtoOrdProg.getFieldString("INCDEC_PUT_QTY"));                     // 증가 투입 량                

						fStlDesignWaitQnty          =  StringHelper.parseDouble(jDtoOrdProg.getFieldString("STL_DESIGN_WAIT_QNTY"));           // 재료 설계 대기 량           
						fHeatoutOrdWaitQntyA        =  StringHelper.parseDouble(jDtoOrdProg.getFieldString("HEATOUT_ORD_WAIT_QNTY_A"));        // 출강 지시 대기 량 A         
						fHeatoutWaitQntyB           =  StringHelper.parseDouble(jDtoOrdProg.getFieldString("HEATOUT_WAIT_QNTY_B"));            // 출강 대기 량 B              
						fSlabFrtomoveWordWaitQntyB  =  StringHelper.parseDouble(jDtoOrdProg.getFieldString("SLAB_FRTOMOVE_WORD_WAIT_QNTY_B")); // SLAB 이송 작업지시 대기 량 B
						fSlabFrtomoveWwaitQntyC     =  StringHelper.parseDouble(jDtoOrdProg.getFieldString("SLAB_FRTOMOVE_WWAIT_QNTY_C"));     // SLAB 이송 작업대기 량 C     
						fSlabShearWwaitQntyD        =  StringHelper.parseDouble(jDtoOrdProg.getFieldString("SLAB_SHEAR_WWAIT_QNTY_D"));        // SLAB 정정 작업대기 량 D     
						fSlabMillWordWaitQntyE      =  StringHelper.parseDouble(jDtoOrdProg.getFieldString("SLAB_MILL_WORD_WAIT_QNTY_E"));     // SLAB 압연 작업지시 대기 량 E
						fSlabMillWwaitQntyF         =  StringHelper.parseDouble(jDtoOrdProg.getFieldString("SLAB_MILL_WWAIT_QNTY_F"));         // SLAB 압연 작업대기 량 F     
						fSlabTrimWwaitQntyG         =  StringHelper.parseDouble(jDtoOrdProg.getFieldString("SLAB_TRIM_WWAIT_QNTY_G"));         // SLAB 절단 작업대기 량 G     
						fCoilStlqltyStampWaitQntyA  =  StringHelper.parseDouble(jDtoOrdProg.getFieldString("COIL_STLQLTY_STAMP_WAIT_QNTY_A")); // COIL 재질 판정 대기 량 A    
						fCoilShearWordWaitQntyB     =  StringHelper.parseDouble(jDtoOrdProg.getFieldString("COIL_SHEAR_WORD_WAIT_QNTY_B"));    // COIL 정정 작업지시 대기 량 B
						fCoilShearWwaitQntyC        =  StringHelper.parseDouble(jDtoOrdProg.getFieldString("COIL_SHEAR_WWAIT_QNTY_C"));        // COIL 정정 작업대기 량 C     
						fCoilFrtomoveWordWaitQntyD  =  StringHelper.parseDouble(jDtoOrdProg.getFieldString("COIL_FRTOMOVE_WORD_WAIT_QNTY_D")); // COIL 이송 작업지시 대기 량 D
						fCoilFrtomoveWwaitQntyE     =  StringHelper.parseDouble(jDtoOrdProg.getFieldString("COIL_FRTOMOVE_WWAIT_QNTY_E"));     // COIL 이송 작업대기 량 E     
						fCoilStampHoldQntyF         =  StringHelper.parseDouble(jDtoOrdProg.getFieldString("COIL_STAMP_HOLD_QNTY_F"));         // COIL 판정 보류 량 F         
						fCoilOverallStampWaitQntyG  =  StringHelper.parseDouble(jDtoOrdProg.getFieldString("COIL_OVERALL_STAMP_WAIT_QNTY_G")); // COIL 종합 판정 대기 량 G    
						fCoilReceiptWaitQntyH       =  StringHelper.parseDouble(jDtoOrdProg.getFieldString("COIL_RECEIPT_WAIT_QNTY_H"));       // COIL 입고 대기 량 H         
						fCoilReturnWaitQntyJ        =  StringHelper.parseDouble(jDtoOrdProg.getFieldString("COIL_RETURN_WAIT_QNTY_J"));        // COIL 반납 대기 량 J         
						fCoilDistWordWaitQntyK      =  StringHelper.parseDouble(jDtoOrdProg.getFieldString("COIL_DIST_WORD_WAIT_QNTY_K"));     // COIL 출하 작업지시 대기 량 K
						fCoilDistWwaitQntyL         =  StringHelper.parseDouble(jDtoOrdProg.getFieldString("COIL_DIST_WWAIT_QNTY_L"));         // COIL 출하 작업대기 량 L     
						fCoilDistQntyM              =  StringHelper.parseDouble(jDtoOrdProg.getFieldString("COIL_DIST_QNTY_M"));               // COIL 출하 량 M              
						fCoilShiploadingWaitQntyN   =  StringHelper.parseDouble(jDtoOrdProg.getFieldString("COIL_SHIPLOADING_WAIT_QNTY_N"));   // COIL 선적 대기 량 N         
						fCoilDeliverDoneQntyP       =  StringHelper.parseDouble(jDtoOrdProg.getFieldString("COIL_DELIVER_DONE_QNTY_P"));       // COIL 인도 완료 량 P         
						
						sSmTeamCd	= StringHelper.evl(jDtoOrdProg.getFieldString("TEAM_PART_CD"),"").trim();
						
				 		logger.println(LogLevel.DEBUG, "Msg >> 생산 종료 구분   : "+sProdEndGp );
				 		logger.println(LogLevel.DEBUG, "Msg >> 인도 허용오차    : "+iDeliverTolMin+":"+iDeliverTolMax);
				}
	 		    logger.println(LogLevel.DEBUG, "Msg >> ----------------------- << ");
	 		    
 		}catch(DAOException daoe){
 			daoe.printStackTrace();
            sErrorGbn   = "E"; 
            sErrorGbnCdMsg = " 주문진행TB 조회시 오류발생 DAOException";
    	}catch(Exception e){
         	// 오류발생으로 return
 			e.printStackTrace();
            sErrorGbn   = "E"; 
            sErrorGbnCdMsg = " 주문진행TB 조회시 오류발생 Exception";
    	}
 		//-오류발생시 처리
    	if(sErrorGbn.equals("E")) {
	 		logger.println(LogLevel.ERROR,"Error Msg >> " +sErrorGbnCdMsg);
	 		lErrList.add(sErrorGbn);      //0: 오류코드  E:오류, S:성공
	 		lErrList.add(sErrorGbnCdMsg); //1: 오류메세지
	 		lErrList.add("");             //2: 판정구분  I:입고, B:보류
	 		lErrList.add("");             //3: 보류 코드 
	 		lErrList.add("");             //4: 여재구분      1:주문,2:여재 
	 		lErrList.add("");             //5: 여재원인코드 
	 		lErrList.add("");             //6: OVER ROLL 
	 		lErrList.add("");             //7: 단중제한 
	 		lErrList.add("");             //8: 칫수이상 
	 		return lErrList;
	   	}

    	/* ---------------------------------------------------------
         * 주문공통TB 자료조회(TB_SM_ORDCOMM)     		
     	 * 조  건  : 주문번호
    	 * 테이블 : 주문공통TB(TB_SM_ORDCOMM)   
    	 * ---------------------------------------------------------*/     		
      	logger.println(LogLevel.DEBUG, "Msg >> 주문공통TB 자료조회 ");
    	List OrdCommParList = new ArrayList();
	    OrdCommParList.add(sCmOrdNo);	    
	    sSmTeamCd ="";  // 영업 TEAM CODE 
	    try {
			List OrdCommData = abReceiveDAO.getOrdComm(OrdCommParList);
			if (OrdCommData.size() <= 0){
    	            sErrorGbn   = "E"; 
    	            sErrorGbnCdMsg = " 주문공통TB 자료가 존재하지 않습니다  주문번호="+OrdCommParList;
			} else {
					JDTORecord jDtoOrdComm = (JDTORecord)OrdCommData.get(0);
					sSmTeamCd              = StringHelper.evl(jDtoOrdComm.getFieldString("SM_TEAM_CD"),"").trim();             // 영업 TEAM CODE               
			 		logger.println(LogLevel.DEBUG,"주문공통TB 자료조회  영업 TEAM CODE Msg >> " +sSmTeamCd);
			}
 		}catch(DAOException daoe){
 			daoe.printStackTrace();
            sErrorGbn   = "E"; 
            sErrorGbnCdMsg = " 주문공통TB 조회시 오류발생 DAOException";
    	}catch(Exception e){
         	// 오류발생으로 return
 			e.printStackTrace();
            sErrorGbn   = "E"; 
            sErrorGbnCdMsg = " 주문공통TB 조회시 오류발생 Exception";
    	}
 		//-오류발생시 처리
    	if(sErrorGbn.equals("E")) {
	 		logger.println(LogLevel.ERROR,"Error Msg >> " +sErrorGbnCdMsg);
	 		lErrList.add(sErrorGbn);      //0: 오류코드  E:오류, S:성공
	 		lErrList.add(sErrorGbnCdMsg); //1: 오류메세지
	 		lErrList.add("");             //2: 판정구분  I:입고, B:보류
	 		lErrList.add("");             //3: 보류 코드 
	 		lErrList.add("");             //4: 여재구분      1:주문,2:여재 
	 		lErrList.add("");             //5: 여재원인코드 
	 		lErrList.add("");             //6: OVER ROLL 
	 		lErrList.add("");             //7: 단중제한 
	 		lErrList.add("");             //8: 칫수이상 
	 		return lErrList;
	   	}
   	
      //--< 조업보류판정처리 : 입고품 ,주문재>---------------------------------------------------//
      // 변경일자 : 20060407 <= 공정 : 한수호과장, IT실 이상진
      // 보류원인코드 : 01 - 생산종결
      //             02 - OVER ROLL
      //             03 - 단중제한
      //             04 - 칫수이상
      //--< 조업보류판정처리 : 입고품 ,주문재>---------------------------------------------------
    	String sBoRyuGbn           = "";    /* 보류구분                  */
    	String sBoRyuTotCd         = "";    /* 보류코드 종합           */
    	String sBoRyuCd_01         = "";    /* 보류코드 생산종결     */
    	String sBoRyuCd_02         = "";    /* 보류코드 OVER ROLL*/
    	String sBoRyuCd_03         = "";    /* 보류코드 단중제한     */
    	String sBoRyuCd_04         = "";    /* 보류코드 칫수이상     */
    	
        sCmYeojaeCauseCd           = "";    /* 여재원인CODE */
        sCmYeojaeOccurDate         = "";    /* 여재발생일자 */
        sCmYeojaeOccurTime         = "";    /* 여재발생시각 */
        
		//	실적(두께/폭) : (주문 두께/폭) ? 두께/폭공차Min) ~ (주문두께/폭 + 두께/폭공차Max)  벗어남) 이면
        //	=> 이유코드 = ‘08’ => 보류코드 칫수이상 '04'
    	if (((fWrsltT < (fJjOrdT - fJjTTolMin)) || (fWrsltT > (fJjOrdT + fJjTTolMax ))) ||
            ((fWrsltW < (fJjOrdW - fJjWTolMin)) || (fWrsltW > (fJjOrdW + fJjWTolMax )))) {
	    	sBoRyuCd_04     = "04";    /* 보류코드 칫수이상     */
	 		logger.println(LogLevel.DEBUG, "Msg >> 칫수이상체크  실적두께:"+fWrsltT+" 주문 두께:"+fJjOrdT+"두께공차Min:"+fJjTTolMin+"두께공차Max:"+fJjTTolMax);
	 		logger.println(LogLevel.DEBUG, "Msg >> 칫수이상체크  실적폭   :"+fWrsltW+"   주문 폭:"+fJjOrdW+"  폭공차Min:"+fJjWTolMin+"폭공차Max:"+fJjWTolMax);
        }

       // 단중제한보류 - 실적중량, 포장단중min/max
       // 포장단중min 계산 (fJjWrapUnitWtMin - (fJjWrapUnitWtMin * 0.1))
       //if (iJjWrsltRealWt < (fJjWrapUnitWtMin - (fJjWrapUnitWtMin * 0.1)) || iJjWrsltRealWt > fJjWrapUnitWtMax) {
       //	sJjReceiptHoldScrapCauseGp = "B";
       // 	sJjShearCauseCd = "03";
	   //		logger.println(LogLevel.DEBUG, "Msg >> 단중제한보류 실적중량="+iJjWrsltRealWt+"  포장단중min:max "+fJjWrapUnitWtMin+" : "+fJjWrapUnitWtMax);
       //}
		
    	// SM_TEAM_CD 영업 TEAM CODE
    	// 단중 미달시
    	//   if ( 중량min  - 중량min * 0.1 < 실적중량   < 중량min )                   => if (영업업체분류  = 'A2' 이면) => 자동 입고   아니면 보류처리
    	//   if ( 중량min  - 중량min * 0.5 < 실적중량   <= 중량min  - 중량min * 0.1 )  => if (영업업체분류  = 'A2' 이면) => 보류처리    아니면 보류처리
    	//   if (                       0 < 실적중량  <= 중량min  - 중량min * 0.5 )  => if (영업업체분류  = 'A2' 이면) => 여재 입고   아니면 여재 입고 
    	// 단중 over시 보류처리
        // 단중제한보류 - 실적중량, 포장단중min/max
        // 포장단중min 계산 (fJjWrapUnitWtMin - (fJjWrapUnitWtMin * 0.1))
    	
    	if(sCheckHRPLATE.equals("P") || sCheckHRPLATE.equals("Q")){
    		logger.println(LogLevel.DEBUG, "Msg >> 품종코드 3번째 자리 ="+sCheckHRPLATE+"  단중제한 보류 처리하지 않음 ");
    	}
    	else {
    		
    	if ((fWrsltRealWt > fJjWrapUnitWtMax)) {
	    	sBoRyuCd_03       = "03";    /* 보류코드 단중제한     */
	 		logger.println(LogLevel.DEBUG, "Msg >> 단중제한보류 실적중량="+fWrsltRealWt+"  포장단중max "+fJjWrapUnitWtMax);
    	}
        // 단중제한보류 - 실적중량, 포장단중min/max
        // 포장단중min 계산 (fJjWrapUnitWtMin - (fJjWrapUnitWtMin * 0.1))
    	if (sBoRyuCd_03.equals("") && fWrsltRealWt < fJjWrapUnitWtMin ) {
	    	if (sSmTeamCd.equals("A2")) {      
	    		// 기존 : 영업2팀 단중이상재(3C) 보류원인코드 찍고, 자동 입고 처리됨
	    		// 변경 : 타 영업팀과 같이 단중이상시 보류처리함. 0825 생산공정 이종현 DR 요청
		    	if ((fJjWrapUnitWtMin - (fJjWrapUnitWtMin * 0.1)) < fWrsltRealWt) {
		    		sBoRyuCd_03     = "03";    /* 보류코드 단중제한         */
			 		logger.println(LogLevel.DEBUG, "Msg >> 영업분류코드[A2] 단중제한의 Min 10%에 걸린경우 보류처리(08월25일적용)  실적중량="+fWrsltRealWt+"  포장단중min "+fJjWrapUnitWtMin+" : "+(fJjWrapUnitWtMin - (fJjWrapUnitWtMin * 0.1)));
		    	}else {
			    	if (((fJjWrapUnitWtMin - (fJjWrapUnitWtMin * 0.5)) < fWrsltRealWt) && (fWrsltRealWt <= (fJjWrapUnitWtMin - (fJjWrapUnitWtMin * 0.1)))) {
				    	sBoRyuCd_03     = "03";    /* 보류코드 단중제한     */
				 		logger.println(LogLevel.DEBUG, "Msg >> 영업분류코드[A2] 단중제한의 Min 50%< 실적중량  <= Min 10%에 걸린경우 보류처리  실적중량="+fWrsltRealWt+"  포장단중min "+fJjWrapUnitWtMin);
			    	}else {
				   // 	sBoRyuGbn       = "I";     /* 보류구분 'I':여재입고  */  // 20080904 생산공정팀 이종현 대리 요청 보류 여재입고 처리 없앰.
				    	sBoRyuCd_03     = "03";    /* 보류코드 단중제한         */
				 		logger.println(LogLevel.DEBUG, "Msg >> 영업분류코드[A2] 단중제한의 Min 50% 여재처리  실적중량="+fWrsltRealWt+"  포장단중min "+fJjWrapUnitWtMin);
			    	}
		    	}
	    	}else {
		    	if ((fJjWrapUnitWtMin - (fJjWrapUnitWtMin * 0.1)) < fWrsltRealWt) {
			    	sBoRyuCd_03     = "03";    /* 보류코드 단중제한         */
			 		logger.println(LogLevel.DEBUG, "Msg >> 기타 단중제한의 Min 10%에 걸린경우 보류처리  실적중량="+fWrsltRealWt+"  포장단중min "+fJjWrapUnitWtMin+" : "+(fJjWrapUnitWtMin - (fJjWrapUnitWtMin * 0.1)));
		    	}else {
			    	if (((fJjWrapUnitWtMin - (fJjWrapUnitWtMin * 0.5)) < fWrsltRealWt) && (fWrsltRealWt <= (fJjWrapUnitWtMin - (fJjWrapUnitWtMin * 0.1)))) {
				    	sBoRyuCd_03     = "03";    /* 보류코드 단중제한         */
				 		logger.println(LogLevel.DEBUG, "Msg >> 영업분류코드[A2] 단중제한의 Min 50%< 실적중량  <= Min 10%에 걸린경우 보류처리  실적중량="+fWrsltRealWt+"  포장단중min "+fJjWrapUnitWtMin);
			    	}else {
				    //	sBoRyuGbn       = "I";     /* 보류구분 'I':여재입고  */   // 20080904 생산공정팀 이종현 대리 요청 보류 여재입고 처리 없앰.
				    	sBoRyuCd_03     = "03";    /* 보류코드 단중제한         */
				 		logger.println(LogLevel.DEBUG, "Msg >> 영업분류코드[A2] 단중제한의 Min 50% 여재처리  실적중량="+fWrsltRealWt+"  포장단중min "+fJjWrapUnitWtMin);
			    	}
		    	}
	    	}
    	}
    	}
    	
    	//진도코드 "G":종합판정대기, "H":입고대기, "F":판정보류, "Y":충당지시대기                            
	    if ((sCmCurrProgCd.equals("G") || sCmCurrProgCd.equals("H")) && sCmOrdYeojaeGp.equals("1")) {
	 		logger.println(LogLevel.DEBUG, "Msg >> 진도코드 : "+sCmCurrProgCd+" 중량 변경:"+fWrsltRealWt+" 기존:"+fCmCoilWt);
	    	fWrsltRealWt = fWrsltRealWt - fCmCoilWt;
	 		logger.println(LogLevel.DEBUG, "Msg >> 중량차이 : "+fWrsltRealWt);
	    }
//        //	  Over Roll Check 2009.07.20 이후 부터 품질과 진행관리로 넘어감.
//		// 주문진행(TB_PM_ORDPROG) Over Roll Check(02) 투입량
//    	double fiOrdQnty  = ((double)iPutQnty) * ((double)(1.0 + (double)(((double)iDeliverTolMax) / 100.0)));  //인도허용주문중량
//    	
//    	double fiiOrdQnty =    fCoilReceiptWaitQntyH          // COIL 입고 대기 량 H         
//							+ fCoilReturnWaitQntyJ           // COIL 반납 대기 량 J         
//							+ fCoilDistWordWaitQntyK         // COIL 출하 작업지시 대기 량 K
//							+ fCoilDistWwaitQntyL            // COIL 출하 작업대기 량 L     
//							+ fCoilDistQntyM                 // COIL 출하 량 M
//    	                    + fWrsltRealWt;                  // 정정실적중량 
// 		logger.println(LogLevel.DEBUG, "Msg >> OverRoll 체크  기준  : "+fiOrdQnty+" : "+fiiOrdQnty);
//    	if ((fiOrdQnty < fiiOrdQnty)) {
//	    	sBoRyuCd_02     = "02";    /* 보류코드 OVER ROLL*/
//	 		logger.println(LogLevel.DEBUG, "Msg >> OverRoll 체크  : "+fiOrdQnty+":"+fiiOrdQnty+" 코드="+sBoRyuCd_02);
//    	}
    	
    	/* -----------------------------------
                     주문진행(TB_PM_ORDPROG) 
    	   ->  G:생산종결가능재,
    	   ->  J:자동종결재  => 생산종결(06)
    	     수정 : if (sProdEndGp.equals("G")|| sProdEndGp.equals("J")) {
		   ----------------------------------- */
    	if (sProdEndGp.equals("J") && sRecordProgStat.equals("3")) {
	    	sBoRyuCd_01     = "01";    /* 보류코드 생산종결     */
	 		logger.println(LogLevel.DEBUG, "Msg >> 주문진행(TB_PM_ORDPROG) 생산종결(06) J:자동종결재  값=>"+sProdEndGp);
    	}
 		logger.println(LogLevel.DEBUG, "Msg ============================================ ");
 		logger.println(LogLevel.DEBUG, "Msg >> 생산    종결   보류 원인 CODE [ "+sBoRyuCd_01+" ]");
 		logger.println(LogLevel.DEBUG, "Msg >> OVER ROLL 보류 원인 CODE [ "+sBoRyuCd_02+" ]");
 		logger.println(LogLevel.DEBUG, "Msg >> 단중제한       보류 원인 CODE [ "+sBoRyuCd_03+" ]");
 		logger.println(LogLevel.DEBUG, "Msg >> 칫수이상       보류 원인 CODE [ "+sBoRyuCd_04+" ]");	 		
 		logger.println(LogLevel.DEBUG, "Msg ============================================ ");
	    
        // ----------------------------------------------------------------------------------------------------- //
		// 1.여재다운시 현재료 사이즈 운영은 두께,폭 불량은 정정실적 정보로 Setting하고 그외 불량은 주문칫수로  운영한다
		// 2.실질적 공정보류재 처리는 SIZE이상(04),단중(03),OVER ROLL(02) 처리만이 존재 한다. 나머지보류는 모두 품질보류가 된다.
		//   -> 실적 처리중 2가지이상보류대상은 보류통합코드로 가져간다(각 코드의 합으로만듬).
		//   -> 보류통합코드구성 : (SIZE이상(04)  + 단중(03)                = 07)  : 보류코드 07 
		//                     (SIZE이상(04)  + OVER ROLL(02)           = 06)  : 보류코드 06 
		//                     (단중(03)      + OVER ROLL(02)           = 05)  : 보류코드 05  
		//                     (SIZE이상(04)  + 단중(03) + OVER ROLL(02) = 09)  : 보류코드 09
		// 3. 처리기준 보류재가 여재처리보다 우선한다.
		// 4. 보류재 해제 처리시 강제여재처리 기능 필요(품질,공정 공통화면).
		// 5. 보류코드구성(품질보류코드구성이 11번이상임)을 공정보류코드구성은 01부터 10 까지 구성하도록  새롭게 만들었습니다.
        // ----------------------------------------------------------------------------------------------------- //
    	if (sBoRyuCd_02.equals("02") || sBoRyuCd_03.equals("03") || sBoRyuCd_04.equals("04")) {
    		if (sBoRyuCd_02.equals("02")) {
        		if (sBoRyuCd_03.equals("03")) {
            		if (sBoRyuCd_04.equals("04")) {
            			sBoRyuTotCd  = "09";    /* 보류코드 종합  :SIZE이상(04) + 단중(03) + OVER ROLL(02)*/
            		}else{
            			sBoRyuTotCd  = "05";    /* 보류코드 종합  : 단중(03) + OVER ROLL(02)*/
            		}
        		}else{
            		if (sBoRyuCd_04.equals("04")) {
            			sBoRyuTotCd  = "06";    /* 보류코드 종합  :SIZE이상(04) + OVER ROLL(02)*/
            		}else{
            			sBoRyuTotCd  = "02";    /* 보류코드 종합  : OVER ROLL(02)*/
            		}        			
        		}
    		}else{
        		if (sBoRyuCd_03.equals("03")) {
            		if (sBoRyuCd_04.equals("04")) {
            			sBoRyuTotCd  = "07";    /* 보류코드 종합  :SIZE이상(04) + 단중(03)*/
            		}else{
            			sBoRyuTotCd  = "03";    /* 보류코드 종합  : 단중(03)*/
            		}
        		}else{
            		if (sBoRyuCd_04.equals("04")) {
            			sBoRyuTotCd  = "04";    /* 보류코드 종합  :SIZE이상(04)*/
            		}        			
        		}
    		}
    	    //- [ 보류결과처리  ] -//
        	if (!sBoRyuTotCd.equals("")) {
            	sJjReceiptHoldScrapCauseGp = "B";               /* 보류         */            	
            	//여재Down : 보류코드 단중제한 이면서 여재입고
            	if (sBoRyuTotCd.equals("03") && sBoRyuGbn.equals("I") ) {
                	sJjReceiptHoldScrapCauseGp = "B";           /* 여재입고    */
                	sCmYeojaeCauseCd           = "3C";          /* 여재원인CODE 3C:단중이상*/
                	//sCmOrdYeojaeGp             = "2"; //여재
                	logger.println(LogLevel.DEBUG, "Msg >> 여재Down부문 -----------------------------------------------------");
                	logger.println(LogLevel.DEBUG, "Msg >> 여재 원인 CODE     : "+sCmYeojaeCauseCd);
                	logger.println(LogLevel.DEBUG, "Msg >> ---------  -----------------------------------------------------");            
            	}
            	//여재Down : 보류코드 SIZE이상 이면서 여재입고
            	if (!sJjReceiptHoldScrapCauseGp.equals("I") && sBoRyuCd_04.equals("04")) {
                	sJjReceiptHoldScrapCauseGp = "B";           /* 여재입고    */
                	sCmYeojaeCauseCd           = "3B";          /* 여재원인CODE 3B:칫수이상 */
                	//sCmOrdYeojaeGp             = "2"; //여재
                	logger.println(LogLevel.DEBUG, "Msg >> 여재Down부문 -----------------------------------------------------");
                	logger.println(LogLevel.DEBUG, "Msg >> 여재 원인 CODE     : "+sCmYeojaeCauseCd);
                	logger.println(LogLevel.DEBUG, "Msg >> ---------  -----------------------------------------------------");            
            	}
           }
    		
    	} else {
    		if (sBoRyuCd_01.equals("01")) {
    			// '01' 생산종결       정주문 (sCmOrdNo.substring(0,1)="P")은  정상입고처리 하며 이유코드는 유지함 
            	sJjReceiptHoldScrapCauseGp = "B";               /* 여재 */
    			sBoRyuTotCd  = "01";                            /* 보류코드 종합  :생산종결(01)*/
        		if (!(sCmOrdNo.substring(0,1).equals("P") || sCmOrdNo.substring(0,1).equals("G"))) {
        			// 정주문 (!sCmOrdNo.substring(0,1)="P" OR "G")이 아닌경우 여재입고한다 
                	sCmYeojaeCauseCd           = "1H";          /* 여재원인CODE 1H:주문보류*/
                	//sCmOrdYeojaeGp             = "2"; //여재
                	logger.println(LogLevel.DEBUG, "Msg >> 여재Down부문 -----------------------------------------------------");
                	logger.println(LogLevel.DEBUG, "Msg >> 여재 원인 CODE    : "+sCmYeojaeCauseCd);
                	logger.println(LogLevel.DEBUG, "Msg >> ---------  -----------------------------------------------------");            
        		}
    		}else{
            	sJjReceiptHoldScrapCauseGp = "I";               /* 여재 */    			
    		}
    	}

 		logger.println(LogLevel.DEBUG, "Msg ============================================ ");
 		logger.println(LogLevel.DEBUG," Msg >> 보류판정 결과 ******************************");
 		logger.println(LogLevel.DEBUG, "Msg >> 보류판정구분[ "+sJjReceiptHoldScrapCauseGp+" ]");
 		logger.println(LogLevel.DEBUG, "Msg >> 보류원인코드[ "+sBoRyuTotCd+" ]");
 		logger.println(LogLevel.DEBUG, "Msg >> 주문여재구분[ "+sCmOrdYeojaeGp+" ]");
 		logger.println(LogLevel.DEBUG, "Msg >> 여재원인코드[ "+sCmYeojaeCauseCd+" ]");	 		
 		logger.println(LogLevel.DEBUG, "Msg ============================================ ");
 		logger.println(LogLevel.DEBUG," Msg >> 보류판정 처리가 완료되었습니다. ");
    	//-오류발생시 처리
    	if(!sErrorGbn.equals("E")) {
	 		lErrList.add("S");                        //0: 오류코드      E:오류, S:성공
	 		lErrList.add("");                         //1: 오류메세지
	 		lErrList.add(sJjReceiptHoldScrapCauseGp); //2: 판정구분      I:입고, B:보류
	 		lErrList.add(sBoRyuTotCd);                //3: 보류 코드 
	 		lErrList.add(sCmOrdYeojaeGp);             //4: 여재구분      1:주문, 2:여재 
	 		lErrList.add(sCmYeojaeCauseCd);           //5: 여재원인코드
	 		lErrList.add(sBoRyuCd_02);                //6: OVER ROLL 
	 		lErrList.add(sBoRyuCd_03);                //7: 단중제한 
	 		lErrList.add(sBoRyuCd_04);                //8: 칫수이상 
	   	}
 		return lErrList;
	    	
	}			

}

