package com.inisteel.cim.ym.facilitywork.worder.session;

import java.util.List;
import java.util.Map;
import java.lang.Math;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.common.parser.Level2Parser;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
import com.inisteel.cim.ym.common.YmCommonDB;

import jspeed.base.log.*;
import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.util.StringHelper;
/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="CWrkOrdRegEJB" jndi-name="JNDICWrkOrdReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class CWrkOrdRegSBean extends BaseSessionBean {
	private Logger logger 			= null;
	private ymCommonDAO ymCommonDAO = null;
	private YmComm ymComm = new YmComm();
	
	private CraneSchDAO dao = null;
	
	public void ejbCreate() {
		LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		logger 		= new Logger(config);
		ymCommonDAO = new ymCommonDAO();
		dao 		= new CraneSchDAO();
	}
	
 	/**
	 * 오퍼레이션명 : 
	 *
	 * 야드 Level-2로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * 1.TC_CD	: THCH790.
        * 2.I/F ID	: YM-AIF-018.
        *
        * 전문코드		CHAR	07		
        * CRANE 번호	CHAR	04		
        * SPARE			CHAR	04		
        * 발생일			CHAR	06		YYMMDD
        * 발생시			CHAR	06		HHMMSS
        * 코일번호		CHAR	10		
        * FROM ADDRESS	CHAR	08		
        * TO ADDRESS	CHAR	08		
        * SPARE		CHAR	97				
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
	public boolean acyBackUpData(String msg) {
        logger.println(LogLevel.DEBUG, this, "TODO: ACY_BACKUP DATA 처리");        
	    try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
            /**
             * Message Parsing.
             */
            JDTORecord parseData = new Level2Parser().parse(msg);
            logger.println(LogLevel.DEBUG, this, parseData);

            /**
             * valid check
             */
            validRecDataOfBackUpReq(parseData, ymCommonDAO.readColumnLenOfTc(getField(parseData, "전문코드")));

            /**
             * 야드에서 사용 가능한 코드로 변환한다.[코드구성: 공동SP열단번지]
             */                        
            String reToAdd 		= getField(parseData, "TOADDRESS");
            String reFromAdd 	= getField(parseData, "FROMADDRESS");

            String fromColGp 	= getStackColGp(reFromAdd);            
            String fromBedGp 	= reFromAdd.substring(reFromAdd.length() - 2, reFromAdd.length());           
            String fromLayerGp  = "0"+ reFromAdd.substring(5, 6);

            String toColGp 		= getStackColGp(reToAdd);
            String toBedGp 		= reToAdd.substring(reToAdd.length() - 2, reToAdd.length());
            String toLayerGp 	= "0"+ reToAdd.substring(5, 6);
                        
            String to 	= toColGp+toBedGp+toLayerGp;
            String from = fromColGp+fromBedGp+fromLayerGp;

            /**
             * 1. 스케쥴이 존재하면
             *    1.1 작업예약/스케쥴 삭제
             *    1.2 권상위치의 코일이
             *        1.2.1 백업 요청 코일과 동일하면 CLEAR
             *        1.2.2 백업 요청 코일과 동일하지 않으면 SKIP
             *    1.3 권하위치의 코일이
             *        1.3.1 백업 요청 코일과 동일하면 UPDATE
             *        1.3.2 백업 요청 코일과 동일하지 않으면 SKIP
             *    1.4 작업실적 생성
             * 2. 스케쥴이 존재하지 않으면 ERROR
             */
            String 	   coilNo	  = getField(parseData, "코일번호");
            String 	   craneNo 	  = ymCommonDAO.readEquipGp("1", getField(parseData, "CRANE번호"));
            JDTORecord backupData = ymCommonDAO.readBackUpData(coilNo, craneNo);
            if(backupData == null) {
                throw new Exception("수신 코일에 대한 스케쥴 정보가 없습니다.");
            }
            removeWBookAndSch(backupData);
            editUpLoc(backupData);
            editPutLoc(backupData);
            ymCommonDAO.createBackUpWrslt(
					                      getField(backupData, "SCH_ID"),
					                      coilNo, 
					                      craneNo, 
					                      getField(backupData, "SCH_WORK_KIND"),
					                      getField(backupData, "SCH_WPREFER"),
					                      from, 
					                      to);
	    }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
	    return true;
	}

    /**
     * 백업 요청에 대한 작업예약/스케쥴 정보를 삭제한다.
     * @param backupData	백업 저장품 정보
     */
    private void removeWBookAndSch(JDTORecord backupData) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        ymCommonDAO.removeWBook(getField(backupData, "WBOOK_ID"));
        ymCommonDAO.removeSchdule(getField(backupData, "SCH_ID"));
    }

    /**
     * 적치단 FROM 위치를 UPDATE
     * @param backupData
     */
    private void editUpLoc(JDTORecord backupData) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
        JDTORecord dto = ymCommonDAO.readBackUpData(
                getField(backupData, "UP_STACK_COL_GP"),
                getField(backupData, "UP_STACK_BED_GP"),
                getField(backupData, "UP_STACK_LAYER_GP"),
                getField(backupData, "STOCK_ID"));
        if(dto != null && dto.size() > 0) {
            ymCommonDAO.modifyStockStatOfLayer(
                    "",
                    YmCommonConst.STACK_LAYER_STAT_E,
                    getField(backupData, "UP_STACK_COL_GP"),
                    getField(backupData, "UP_STACK_BED_GP"),
                    getField(backupData, "UP_STACK_LAYER_GP"));
        }
    }

    /**
     * 적치단 TO 위치를 UPDATE
     * @param backupData
     */
    private void editPutLoc(JDTORecord backupData) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
        JDTORecord dto = ymCommonDAO.readBackUpData(
                getField(backupData, "PUT_STACK_COL_GP"),
                getField(backupData, "PUT_STACK_BED_GP"),
                getField(backupData, "PUT_STACK_LAYER_GP"),
                getField(backupData, "STOCK_ID"));
        if(dto != null && dto.size() > 0) {
            ymCommonDAO.modifyStockStatOfLayer(
                    getField(backupData, "STOCK_ID"),
                    YmCommonConst.STACK_LAYER_STAT_L,
                    getField(backupData, "PUT_STACK_COL_GP"),
                    getField(backupData, "PUT_STACK_BED_GP"),
                    getField(backupData, "PUT_STACK_LAYER_GP"));
        }
    }

    /**
     * 사용가능한 적치열을 리턴한다.
     * @param address
     * @return
     */
    private String getStackColGp(String address) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        return new StringBuffer(YmCommonConst.YD_GP_1)
	        			.append(address.substring(1, 2))
	        			.append(address.substring(2, 4))
	        			.append("0"+ address.substring(4, 5))
	        			.toString();
    }

	/**
	 * 오퍼레이션명 : 
	 *
	 * 조회 조건을 가져와 해당 조건에 해당하는 쿼리 아이디와 함께 DAO에 넘겨준다.
        * 
        * param queryID : 쿼리 아이디
        * param yardgubun : 야드구분
        * param dongGubun : 동구분
        * param schCode : 스케줄 코드
        * param searchCon : 조회 조건
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */          
	public List getListCraneWBook(String queryID, String yardgubun, String dongGubun, String schCode, String searchCon){
		CraneSchDAO craneschDAO = null;	    
	    List crWkLIst = null;
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
	    	craneschDAO = new CraneSchDAO();
	    	crWkLIst = craneschDAO.getListCraneWBook(queryID,new Object[]{yardgubun, dongGubun, schCode});
	    	return crWkLIst;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}
		
	/**
	 * 오퍼레이션명 : 
	 *
	 * 예약 내역을 크레인 작업 테이블에 인서트 한다.
        * 
        * param queryID : 쿼리 아이디
        * param CranNo : 크레인 번호
        * param SchNo : 스케줄 번호
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
	public String insertCRWrkBook(String queryID, String CranNo, String SchNo){
		return "";	
	}

	/**
	 * 오퍼레이션명 : 
	 *
	 * 크레인 작업 요구 현황 조회 조건을 가져와 해당 조건에 해당하는 쿼리 아이디와 함께 DAO에 넘겨준다.
        * 
        * craneWorkReqjl.jsp에서 호출된다.
        * 
        * param queryID : 쿼리 아이디
        * param yardgubun : 야드구분
        * param dongGubun : 동구분
        * param schCode : 크레인 호기
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */      
	public List getCurrentCrWrk(String queryID, List listData) throws EJBServiceException ,DAOException{
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			CraneSchDAO craneschDAO = new CraneSchDAO();
			return craneschDAO.getListData(queryID, listData);
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}

	/**
	 * 오퍼레이션명 : 
	 *
	 * 현재 검색된 현 크레인 작업현황중 선택한 작업에 대한 실적 처리르루위해 해당 작업의 내역을 가져온다.
        * 
        * craneWorkReqjl.jsp에서 호출된다.
        * 
        * param queryID : 쿼리 아이디
        * param yardgubun : 작업요구일시
        * param dongGubun : 작업종류
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */          
	public List getListCrWrkLot(String queryID, String p_cranename, String kindWrk, String wrkOrderDate , String wrkranking){
		CraneSchDAO craneschDAO = null;	    
	    List crWkLIst = null;
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
	    	craneschDAO = new CraneSchDAO();
	    	crWkLIst = craneschDAO.getListCraneWBook(queryID,new Object[]{p_cranename, kindWrk, wrkOrderDate, wrkranking});
	    	return crWkLIst;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}
    
	///////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	///////////////////////////YJK START///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////

	/**
	 * 오퍼레이션명 : 
	 *
	 * A열연 크레인 작업요구시 아래의 모듈을 호출
	 *
	 * param sYardId 		: 야드구분
        * param sBayGp 		: 동구분
        * param sEquipNo		: 설비번호
        * param sSchCode 	: 스케쥴코드
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 	 
	public boolean callCraneSchInfo(String sYdGp, 
								String sBayGp,
								String sCraneNo,
						    		String sSchCode){
		boolean isSuccess 	= false;
		String sProgressId 	= "";
		
		logger.println(LogLevel.DEBUG,this,"= A 작업요구=크레인 적극적 요구 시작"); 
		try{ 
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			/*
			 *	1.	현재 크레인 상태를 체크한다.
			 * 		WORK_PROG_STAT_W	: 대기
			 *		WORK_PROG_STAT_1 	: UP지시
			 *		WORK_PROG_STAT_2 	: UP실적
			 *		WORK_PROG_STAT_3 	: PUT지시
			 */
			String sWprogStat = "";
			String sSchId	  = "";
			{
				JDTORecord craneV = dao.getEquipInfoWithEquipNo(sYdGp, sCraneNo);
				 	
			 	if(craneV != null){
			 		sWprogStat = StringHelper.evl(craneV.getFieldString("WPROG_STAT"),"");
			 		sSchId	   = StringHelper.evl(craneV.getFieldString("WBOOK_ID"),"");
			 	}
		 	}
		 	
		 	/**
		 	 * A열연 B동 크레인의 냉각장 적치 작업요구에 대한 작업예약이 없을 경우 
		 	 *       C동에 있는 냉각장 적치 작업예약을 검색한다.
		 	 */
		 	 
			/*
			 *	2.	현재 스케쥴에 등록되지 않은 작업예약 정보를 체크한다.
			 */
			boolean isWbook = false; 
			{
				JDTORecord wbookV = null;
 
				if(YmCommonConst.YD_GP_1.equals(sYdGp) 				&& //야드구분(1)
				   YmCommonConst.BAY_GP_B.equals(sBayGp) 			&& //동구분(B)
				   YmCommonConst.NEW_SCH_WORK_KIND_CDLO.equals(sSchCode)   //냉각장적치(CDLO)
			 	){
					//2.1	냉각장 적치 스케쥴 코드인 경우.
					wbookV = dao.getCraneWbookInfo_02(sYdGp,
										 		 	  YmCommonConst.BAY_GP_C,
										          	  YmCommonConst.NEW_SCH_WORK_KIND_CDLO);
					logger.println(LogLevel.DEBUG,this,"= A 작업요구=C 냉각장 적치 스케쥴 코드 검색"); 
				}
				
				if(wbookV == null){
					//2.2	냉각장 적치 스케쥴 코드가 아닌경우.
					wbookV = dao.getCraneWbookInfo_02(sYdGp,
										 		 	  sBayGp,
										          	  sSchCode);
					logger.println(LogLevel.DEBUG,this,"= A 작업요구=일반 스케쥴 코드 검색"); 
				}
				
			 	if(wbookV != null){
			 		isWbook = true;
			 	}
			 	
			 	/**
	    		 * 스케쥴 등록 갯수를 체크한다.
	    		 * 몇개 이상이면 스케쥴 등록을 못하게 한다.
	    		 */
	    		int iSchRuleCount = 2; 
	    		String sSchCount  = "0"; 
	    		JDTORecord schRc  = dao.getCraneSchCount(sYdGp,
			    									     sBayGp,
			    									     sSchCode);
	    		if(schRc != null){
	    			sSchCount = StringHelper.evl(schRc.getFieldString("COUNT"), "0");
		    	}
		    	
	    		//A열연 DCLINEOFF 인경우 
	    		if(sSchCode.equals(YmCommonConst.NEW_SCH_WORK_KIND_CDLO) && sYdGp.equals("1")){
	    			iSchRuleCount = 1; 
	    		}
	    		
		    	if(Integer.parseInt(sSchCount) > iSchRuleCount){
		    		isWbook = false;
		    		logger.println(LogLevel.DEBUG,this, "= A 작업요구=스케쥴 등록 제한으로 FALSE====");
		    	}		
		 	}
		 	logger.println(LogLevel.DEBUG,this,"= A 작업요구=크레인 상태 	: "+sWprogStat); 
		 	logger.println(LogLevel.DEBUG,this,"= A 작업요구=SCH_ID			: "+sSchId); 
		 	logger.println(LogLevel.DEBUG,this,"= A 작업요구=작업예약유무 	: "+isWbook); 
			/*
			 *	3.	크레인 상태와 할당된 작업예약 정보를 가지고
			 *		처리방법을 분리한다.
			 */
			{
				/*
				 *	3.1	크레인 : IDLE - 작업예약 O
				 *		- return true
				 */  
				if(YmCommonConst.WORK_PROG_STAT_W.equals(sWprogStat) && 
				   isWbook){
				   	
				   	sProgressId	= "1";
					isSuccess 	= true;   	
					logger.println(LogLevel.DEBUG,this,"= A 작업요구=크레인 : IDLE - 작업예약 O"); 
				}
				/*
				 *	3.2	크레인 : IDLE - 작업예약 X
				 *		- 크레인 작업요구 호출
				 *			- B동 냉각장 적치처럼 다른 크레인의 작업 INTERCEPT
				 *		- return false
				 */
				if(YmCommonConst.WORK_PROG_STAT_W.equals(sWprogStat) && 
				   !isWbook){
				   	
				   	sProgressId	= "2";
				   	isSuccess 	= false;   	
				   	logger.println(LogLevel.DEBUG,this,"= A 작업요구=크레인 : IDLE - 작업예약 X"); 
				}
				/*
				 *	3.3	크레인 : UP지시 - 작업예약 O
				 *		- 설비상태 idle 셋팅
				 *		- 스케쥴 정보 초기화
				 *		- return true
				 */
				if(YmCommonConst.WORK_PROG_STAT_1.equals(sWprogStat) && 
				   isWbook){
				   	
				   	sProgressId	= "3";
					isSuccess 	= true;  
					logger.println(LogLevel.DEBUG,this,"= A 작업요구=크레인 : UP지시 - 작업예약 O"); 	
				}
				/*
				 *	3.4	크레인 : UP지시 - 작업예약 X
				 *		- 설비상태 idle 셋팅
				 *		- 스케쥴 정보 초기화
				 *		- 크레인 작업요구 호출
				 *			- 해당 스케쥴 코드로 호출
				 *		- return false
				 */   
				if(YmCommonConst.WORK_PROG_STAT_1.equals(sWprogStat) && 
				   !isWbook){
				   	
				   	sProgressId	= "4";								   
				   	isSuccess 	= false;
				   	logger.println(LogLevel.DEBUG,this,"= A 작업요구=크레인 : UP지시 - 작업예약 X"); 	   	
				}
				/*
				 *	3.5	크레인 : UP실적 - 작업예약 O
				 *		- return true
				 */
				if(YmCommonConst.WORK_PROG_STAT_2.equals(sWprogStat) && 
				   isWbook){
				   	
				   	sProgressId	= "5";
					isSuccess 	= true;  
					logger.println(LogLevel.DEBUG,this,"= A 작업요구=크레인 : UP실적 - 작업예약 O"); 	
				}
				/*
				 *	3.6	크레인 : UP실적 - 작업예약 X
				 *		- return false
				 */   
				if(YmCommonConst.WORK_PROG_STAT_2.equals(sWprogStat) && 
				   !isWbook){
				   	
				   	sProgressId	= "6";								   
				   	isSuccess 	= false;
				   	logger.println(LogLevel.DEBUG,this,"= A 작업요구=크레인 : UP실적 - 작업예약 X"); 	   	
				}
				/*
				 *	3.7	크레인 : PUT지시 - 작업예약 O,X
				 *		- 크레인 작업요구 호출
				 *			- 기존의 PUT 작업지시 다시 호출
				 *			- 추가 메세지 처리
				 *		- return false
				 */ 
				if(YmCommonConst.WORK_PROG_STAT_3.equals(sWprogStat)){
					
					sProgressId	= "7";
					isSuccess 	= false;   	
					logger.println(LogLevel.DEBUG,this,"= A 작업요구=크레인 : PUT지시 - 작업예약 O,X"); 		
				}
			}
				
			/*
			 *	4	크레인 설비상태 idle 셋팅
			 */ 
			if("3".equals(sProgressId)||
			   "4".equals(sProgressId)){
			   	
				int iReq = dao.updateSubCraneEquipStat(sYdGp,
	    											   sBayGp,
	    											   YmCommonConst.EQUIP_KIND_CR,
	    											   sCraneNo,
	    											   YmCommonConst.WORK_PROG_STAT_W,
	    											   "");
				logger.println(LogLevel.DEBUG,this,"= A 작업요구=크레인 설비상태 idle 셋팅="+iReq); 	    											   
			}
			/*
			 *	5	스케쥴 정보 초기화
			 */ 
			if("3".equals(sProgressId)||
			   "4".equals(sProgressId)){
			   	
				int iReq = dao.updateCraneSchStat(sSchId, YmCommonConst.SCH_WORK_STAT_S);
				logger.println(LogLevel.DEBUG,this,"= A 작업요구=스케쥴 정보 초기화="+iReq);		   									   	  
			}
			
			/*
			 *	6	크레인 작업요구 호출
			 */ 
			if("2".equals(sProgressId)||
			   "4".equals(sProgressId)||	
			   "7".equals(sProgressId)||
			   ("3".equals(sProgressId) && sSchCode.equals(YmCommonConst.NEW_SCH_WORK_KIND_CDLO) && sYdGp.equals("1"))
			   ){
			   	
				boolean isYahoo =  callCraneSchInfo(YmCommonConst.TC_THCH520,
							   					 	sYdGp, 
												  	sBayGp,
												    YmCommonConst.EQUIP_KIND_CR,
												    sCraneNo,
												    sSchCode,
												    "");
				logger.println(LogLevel.DEBUG,this,"= A 작업요구=크레인 작업요구 호출="+isYahoo);													    
			}

		logger.println(LogLevel.DEBUG,this,"= A 작업요구=크레인 적극적 요구 종료"); 					    									   
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
	 * 전문 크레인 긴급 작업요구 편성
	 *
	 * param sYardId 		: 야드구분
        * param sBayGp 		: 동구분
        * param sEquipNo		: 설비번호
        * param sSchCode 		: 스케쥴코드
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 	 	 
	public boolean callEmergencySchInfo_01(String sYdGp, 
									   String sBayGp,
									   String sCraneNo,
						    			   String sSchCode){
		boolean isSuccess 	= false;
		int iReq = -1;
		
		
		
		String sWprogStat = "";
		String sSchId	  = "";
		
		//JDTORecord schV = dao.getEquipInfoWithEquipNo(sYdGp, sCraneNo);

		try{ 
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			logger.println(LogLevel.DEBUG,this,"=작업요구=크레인 긴급작업 요구 시작"); 
			logger.println(LogLevel.DEBUG,this,"=1 	: "+sYdGp); 
			logger.println(LogLevel.DEBUG,this,"=1 	: "+sBayGp); 
			logger.println(LogLevel.DEBUG,this,"=1 	: "+sCraneNo); 
			logger.println(LogLevel.DEBUG,this,"=1 	: "+sSchCode); 
			
			/*
			 * 2007.07.03
			 * Sch 존재 선 확인으로 막음
			 */
			/*JDTORecord schV = dao.getWorkCraneSchInfo( sYdGp, 
													   sBayGp, 
													   sCraneNo, 
													   sSchCode);
			
			if(schV == null){
		 		
				logger.println(LogLevel.DEBUG,this,"=작업요구=크레인 작업이 없습니다."); 
		 		
				JDTORecord craneV1 = dao.getEquipInfoWithEquipNo(sYdGp,
																 sCraneNo);

				if(craneV1 != null){
					sWprogStat = StringHelper.evl(craneV1.getFieldString("WPROG_STAT"),"");
					sSchId	   = StringHelper.evl(craneV1.getFieldString("WBOOK_ID"),"");
				}
				
				logger.println(LogLevel.DEBUG,this,"=작업요구=크레인 상태 	: "+sWprogStat); 
			 	logger.println(LogLevel.DEBUG,this,"=작업요구=SCH_ID		: "+sSchId); 
			 	
			 	
			 	if(YmCommonConst.WORK_PROG_STAT_1.equals(sWprogStat)){
					
					iReq = dao.updateSubCraneEquipStat(sYdGp,
		    										   sBayGp,
		    										   YmCommonConst.EQUIP_KIND_CR,
		    										   sCraneNo,
		    										   YmCommonConst.WORK_PROG_STAT_W,
		    										   "");
					logger.println(LogLevel.DEBUG,this,"=작업요구=크레인 설비상태 idle 셋팅="+iReq); 		
					
					iReq = dao.updateCraneSchStat(sSchId,
			   									  YmCommonConst.SCH_WORK_STAT_S);
					logger.println(LogLevel.DEBUG,this,"=작업요구=스케쥴 정보 초기화="+iReq);	
				}
			 	
				String sQueryId  = "ym.common.dao.ymCommonDAO.getCodeToName";	   	
			   	JDTORecord comJr = ymCommonDAO.getCommonInfo(sQueryId,new Object[]{"YM104","3",
																		   sSchCode});
				String sSchName = StringHelper.evl(comJr.getFieldString("CLASS2_NAME1"), "");
				isSuccess =  callBCoilCraneMsgInfo( sYdGp+sBayGp+"CR"+sCraneNo,
													sSchName+" 작업이 없습니다.");
				
		 		return isSuccess;
		 		
			}
			*/
			
			/*
			 *	1.	현재 크레인 상태를 체크한다.
			 * 		WORK_PROG_STAT_W	: 대기
			 *		WORK_PROG_STAT_1 	: UP지시
			 *		WORK_PROG_STAT_2 	: UP실적
			 *		WORK_PROG_STAT_3 	: PUT지시
			 */
			
			{
				JDTORecord craneV = dao.getEquipInfoWithEquipNo(sYdGp,
																sCraneNo);
				 	
			 	if(craneV != null){
			 		sWprogStat = StringHelper.evl(craneV.getFieldString("WPROG_STAT"),"");
			 		sSchId	   = StringHelper.evl(craneV.getFieldString("WBOOK_ID"),"");
			 	}
		 	}
		 	
		 	logger.println(LogLevel.DEBUG,this,"=작업요구=크레인 상태 	: "+sWprogStat); 
		 	logger.println(LogLevel.DEBUG,this,"=작업요구=SCH_ID		: "+sSchId); 
		 	
		 	if(YmCommonConst.WORK_PROG_STAT_3.equals(sWprogStat)){
		 		logger.println(LogLevel.DEBUG,this,"긴급작업 : PUT 지시 상태에서는 긴급작업을 편성할 수 없습니다.");
				throw new EJBServiceException("긴급작업 : PUT 지시 상태에서는 긴급작업을 편성할 수 없습니다.");
		 	}
		 	
		 	/*
			 *	2.	크레인 : UP지시 상태이면 정보를 초기화한다.
			 */
			if(YmCommonConst.WORK_PROG_STAT_1.equals(sWprogStat)){
				
				iReq = dao.updateSubCraneEquipStat(sYdGp,
	    										   sBayGp,
	    										   YmCommonConst.EQUIP_KIND_CR,
	    										   sCraneNo,
	    										   YmCommonConst.WORK_PROG_STAT_W,
	    										   "");
				logger.println(LogLevel.DEBUG,this,"=작업요구=크레인 설비상태 idle 셋팅="+iReq); 		
				
				iReq = dao.updateCraneSchStat(sSchId,
		   									  YmCommonConst.SCH_WORK_STAT_S);
				logger.println(LogLevel.DEBUG,this,"=작업요구=스케쥴 정보 초기화="+iReq);	
			}
			
			/*
			 *	3.	크레인 : 스케쥴 코드에 대해 긴급작업을 편성한다.
			 *		- 기존에 긴급작업 편성된 정보를 원복한다.
			 *		- 새로 긴급작업을 편성한다.
			 */
			 
			iReq = dao.updateCraneSchClaer(sYdGp,
    									   sBayGp,
    									   sCraneNo);
			logger.println(LogLevel.DEBUG,this,"=작업요구=크레인 긴급작업 정보 초기화="+iReq); 
			
			iReq = dao.updateEmergencySchInfo(sYdGp,
    									      sBayGp,
    									      sCraneNo,
    									      sSchCode);
			logger.println(LogLevel.DEBUG,this,"=작업요구=크레인 긴급작업 정보 편성="+iReq); 
			
			/*
			 *	4.	크레인 작업요구 호출
			 */ 
			if(YmCommonConst.WORK_PROG_STAT_1.equals(sWprogStat)){
				
				String sTc = "";
				
				//A열연 SLAB 야드 추가 (MCH)
				if(YmCommonConst.YD_GP_0.equals(sYdGp) && YmCommonConst.BAY_GP_A.equals(sBayGp)){
					
					sTc = YmCommonConst.TC_HM1PB02;
				}else if(YmCommonConst.YD_GP_0.equals(sYdGp) && YmCommonConst.BAY_GP_B.equals(sBayGp)){
					
					sTc = YmCommonConst.TC_HM1PB52;
				}else if(YmCommonConst.YD_GP_1.equals(sYdGp)){
					
					sTc = YmCommonConst.TC_THCH520;
				}else if(YmCommonConst.YD_GP_2.equals(sYdGp)){   									
					
					sTc = YmCommonConst.TC_CM1PB02;
				}else if(YmCommonConst.YD_GP_3.equals(sYdGp)){
					
					sTc = YmCommonConst.TC_CN1PB02;
				}
				
				boolean isYahoo =  callCraneSchInfo(sTc,
													sYdGp, 
												  	sBayGp,
												    YmCommonConst.EQUIP_KIND_CR,
												    sCraneNo,
												    sSchCode,
												    "");
				logger.println(LogLevel.DEBUG,this,"=작업요구=크레인 작업요구 호출="+isYahoo);													    
			}

		logger.println(LogLevel.DEBUG,this,"=작업요구=크레인 긴급작업 요구 종료"); 		
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
	 * 화면 크레인 긴급 작업요구 편성
	 *
	 * param sYardId 		: 야드구분
        * param sBayGp 		: 동구분
        * param sEquipNo		: 설비번호
        * param sSchCode 	: 스케쥴코드
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 	 	 
	public boolean callEmergencySchInfo(String sYdGp, 
									String sBayGp,
									String sCraneNo,
						    			String sSchCode){
		boolean isSuccess 	= false;
		int iReq = -1;
		
		logger.println(LogLevel.DEBUG,this,"=작업요구=크레인 긴급작업 요구 시작"); 
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			/*
			 *	1.	현재 크레인 상태를 체크한다.
			 * 		WORK_PROG_STAT_W	: 대기
			 *		WORK_PROG_STAT_1 	: UP지시
			 *		WORK_PROG_STAT_2 	: UP실적
			 *		WORK_PROG_STAT_3 	: PUT지시
			 */
			String sWprogStat = "";
			String sSchId	  = "";
			{
				JDTORecord craneV = dao.getEquipInfoWithEquipNo(sYdGp,
																sCraneNo);
				 	
			 	if(craneV != null){
			 		sWprogStat = StringHelper.evl(craneV.getFieldString("WPROG_STAT"),"");
			 		sSchId	   = StringHelper.evl(craneV.getFieldString("WBOOK_ID"),"");
			 	}
		 	}
		 	
		 	logger.println(LogLevel.DEBUG,this,"=작업요구=크레인 상태 	: "+sWprogStat); 
		 	logger.println(LogLevel.DEBUG,this,"=작업요구=SCH_ID		: "+sSchId); 
		 	
		 	if(YmCommonConst.WORK_PROG_STAT_3.equals(sWprogStat)){
		 		logger.println(LogLevel.DEBUG,this,"긴급작업 : PUT 지시 상태에서는 긴급작업을 편성할 수 없습니다.");
				throw new EJBServiceException("긴급작업 : PUT 지시 상태에서는 긴급작업을 편성할 수 없습니다.");
		 	}
		 	
		 	/*
			 *	2.	크레인 : UP지시 상태이면 정보를 초기화한다.
			 */
			if(YmCommonConst.WORK_PROG_STAT_1.equals(sWprogStat)){
				
				iReq = dao.updateSubCraneEquipStat(sYdGp,
	    										   sBayGp,
	    										   YmCommonConst.EQUIP_KIND_CR,
	    										   sCraneNo,
	    										   YmCommonConst.WORK_PROG_STAT_W,
	    										   "");
				logger.println(LogLevel.DEBUG,this,"=작업요구=크레인 설비상태 idle 셋팅="+iReq); 		
				
				iReq = dao.updateCraneSchStat(sSchId,
		   									  YmCommonConst.SCH_WORK_STAT_S);
				logger.println(LogLevel.DEBUG,this,"=작업요구=스케쥴 정보 초기화="+iReq);	
			}
			
			/*
			 *	3.	크레인 : 스케쥴 코드에 대해 긴급작업을 편성한다.
			 *		- 기존에 긴급작업 편성된 정보를 원복한다.
			 *		- 새로 긴급작업을 편성한다.
			 */
			 
			iReq = dao.updateCraneSchClaer(sYdGp,
    									   sBayGp,
    									   sCraneNo);
			logger.println(LogLevel.DEBUG,this,"=작업요구=크레인 긴급작업 정보 초기화="+iReq); 
			
			iReq = dao.updateEmergencySchInfo(sYdGp,
    									      sBayGp,
    									      sCraneNo,
    									      sSchCode);
			
			logger.println(LogLevel.DEBUG,this,"=작업요구=크레인 긴급작업 정보 편성="+iReq); 
			
			/*
			 *	4.	크레인 작업요구 호출
			 */ 
			if(YmCommonConst.WORK_PROG_STAT_1.equals(sWprogStat)){
				
				String sTc = "";
				
				//A열연 SLAB 야드 추가 (MCH)
				if(YmCommonConst.YD_GP_0.equals(sYdGp) && YmCommonConst.BAY_GP_A.equals(sBayGp)){
					
					sTc = YmCommonConst.TC_HM1PB02;
				}else if(YmCommonConst.YD_GP_0.equals(sYdGp) && YmCommonConst.BAY_GP_B.equals(sBayGp)){
					
					sTc = YmCommonConst.TC_HM1PB52;
					
				}else if(YmCommonConst.YD_GP_1.equals(sYdGp)){
					
					sTc = YmCommonConst.TC_THCH520;
				}else if(YmCommonConst.YD_GP_2.equals(sYdGp)){   									
					
					sTc = YmCommonConst.TC_CM1PB02;
				}else if(YmCommonConst.YD_GP_3.equals(sYdGp)){
					
					sTc = YmCommonConst.TC_CN1PB02;
				}
				
				boolean isYahoo =  callCraneSchInfo(sTc,
													sYdGp, 
												  	sBayGp,
												    YmCommonConst.EQUIP_KIND_CR,
												    sCraneNo,
												    sSchCode,
												    "");
				logger.println(LogLevel.DEBUG,this,"=작업요구=크레인 작업요구 호출="+isYahoo);													    
			}

		logger.println(LogLevel.DEBUG,this,"=작업요구=크레인 긴급작업 요구 종료"); 		
		isSuccess = true;			    									   
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }		
	    
	    return isSuccess;							   	
	}
	/**
	 * 오퍼레이션명 : 최규성 2010-01-27 
	 *
	 * 화면 크레인 긴급 작업요구 편성.
	 *
	 * param sYardId 		: 야드구분
        * param sBayGp 		: 동구분
        * param sEquipNo		: 설비번호
        * param sSchCode 	: 스케쥴코드
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 	 	 
	public boolean callEmergencySchInfo_bCoil(String sYdGp, 
									String sBayGp,
									String sCraneNo,
						    			String sSchCode,
						    			String sSch_Id){
		boolean isSuccess 	= false;
		int iReq = -1;
		
		logger.println(LogLevel.DEBUG,this,"=작업요구=크레인 긴급작업 요구 시작"); 
		try{ 
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			/*
			 *	1.	현재 크레인 상태를 체크한다.
			 * 		WORK_PROG_STAT_W	: 대기
			 *		WORK_PROG_STAT_1 	: UP지시
			 *		WORK_PROG_STAT_2 	: UP실적
			 *		WORK_PROG_STAT_3 	: PUT지시
			 */
			String sWprogStat = "";
			String sSchId	  = "";
			{
				JDTORecord craneV = dao.getEquipInfoWithEquipNo(sYdGp,
																sCraneNo);
				 	
			 	if(craneV != null){
			 		sWprogStat = StringHelper.evl(craneV.getFieldString("WPROG_STAT"),"");
			 		sSchId	   = StringHelper.evl(craneV.getFieldString("WBOOK_ID"),"");
			 	}
		 	}
		 	
		 	logger.println(LogLevel.DEBUG,this,"=작업요구=크레인 상태 	: "+sWprogStat); 
		 	logger.println(LogLevel.DEBUG,this,"=작업요구=SCH_ID		: "+sSchId); 
		 	
		 	if(YmCommonConst.WORK_PROG_STAT_3.equals(sWprogStat)){
		 		logger.println(LogLevel.DEBUG,this,"긴급작업 : PUT 지시 상태에서는 긴급작업을 편성할 수 없습니다.");
				throw new EJBServiceException("긴급작업 : PUT 지시 상태에서는 긴급작업을 편성할 수 없습니다.");
		 	}
		 	
		 	/*
			 *	2.	크레인 : UP지시 상태이면 정보를 초기화한다.
			 */
			if(YmCommonConst.WORK_PROG_STAT_1.equals(sWprogStat)){
				
				iReq = dao.updateSubCraneEquipStat(sYdGp,
	    										   sBayGp,
	    										   YmCommonConst.EQUIP_KIND_CR,
	    										   sCraneNo,
	    										   YmCommonConst.WORK_PROG_STAT_W,
	    										   "");
				logger.println(LogLevel.DEBUG,this,"=작업요구=크레인 설비상태 idle 셋팅="+iReq); 		
				
				iReq = dao.updateCraneSchStat(sSchId,
		   									  YmCommonConst.SCH_WORK_STAT_S);
				logger.println(LogLevel.DEBUG,this,"=작업요구=스케쥴 정보 초기화="+iReq);	
			}
			
			/*
			 *	3.	크레인 : 스케쥴 코드에 대해 긴급작업을 편성한다.
			 *		- 기존에 긴급작업 편성된 정보를 원복한다.
			 *		- 새로 긴급작업을 편성한다.
			 */
			 
			iReq = dao.updateCraneSchClaer(sYdGp,
    									   sBayGp,
    									   sCraneNo);
			logger.println(LogLevel.DEBUG,this,"=작업요구=크레인 긴급작업 정보 초기화="+iReq); 
			//if(YmCommonConst.NEW_SCH_WORK_KIND_CNLO.equals(sSchCode)){
				logger.println(LogLevel.DEBUG,this,"=작업요구=크레인 긴급작업 정보 편성=SPM2추출스케줄"+iReq);
				iReq = dao.updateEmergencySchInfo_spm2(sYdGp,
												      sBayGp,
												      sCraneNo,
												      sSchCode,
												      sSch_Id);
			//}else{
			//	logger.println(LogLevel.DEBUG,this,"=작업요구=크레인 긴급작업 정보 편성=SPM2추출 이외"+iReq);
			//iReq = dao.updateEmergencySchInfo(sYdGp,
    		//							      sBayGp,
    		//							      sCraneNo,
    		//							      sSchCode);
			
			logger.println(LogLevel.DEBUG,this,"=작업요구=크레인 긴급작업 정보 편성="+iReq); 
			//}
			/*
			 *	4.	크레인 작업요구 호출
			 */ 
			if(YmCommonConst.WORK_PROG_STAT_1.equals(sWprogStat)){
				
				String sTc = "";
				
				//A열연 SLAB 야드 추가 (MCH)
				if(YmCommonConst.YD_GP_0.equals(sYdGp) && YmCommonConst.BAY_GP_A.equals(sBayGp)){
					
					sTc = YmCommonConst.TC_HM1PB02;
				}else if(YmCommonConst.YD_GP_0.equals(sYdGp) && YmCommonConst.BAY_GP_B.equals(sBayGp)){
					
					sTc = YmCommonConst.TC_HM1PB52;
					
				}else if(YmCommonConst.YD_GP_1.equals(sYdGp)){
					
					sTc = YmCommonConst.TC_THCH520;
				}else if(YmCommonConst.YD_GP_2.equals(sYdGp)){   									
					
					sTc = YmCommonConst.TC_CM1PB02;
				}else if(YmCommonConst.YD_GP_3.equals(sYdGp)){
					
					sTc = YmCommonConst.TC_CN1PB02+"E";
				}
				
				boolean isYahoo =  callCraneSchInfo(sTc,
													sYdGp, 
												  	sBayGp,
												    YmCommonConst.EQUIP_KIND_CR,
												    sCraneNo,
												    sSchCode,
												    "");
				logger.println(LogLevel.DEBUG,this,"=작업요구=크레인 작업요구 호출="+isYahoo);													    
			}

		logger.println(LogLevel.DEBUG,this,"=작업요구=크레인 긴급작업 요구 종료"); 		
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
	 * I/F :
	 * 	YM-AIF-003	CRANE 작업지시 요구	Level2	Level3	THCH520
	 *				1	전문코드	CHAR	07	    전문코드 	
	 *				2	CRANE 번호	CHAR	04		CRANE번호
	 *				3	SPARE1		CHAR	04		SPARE1   
	 *				4	발생일		CHAR	06		발생일   YYMMDD
	 *				5	발생시		CHAR	06		발생시   HHMMSS
	 *				6	SPARE2		CHAR	123		SPARE2   
	 * 	YM-AIF-022	CRANE 작업지시		Level3	Level2	THHC110
	 *
	 * 	YM-BIF-002	CRANE 작업지시 요구 Level2	Level3	CN1PB02/CM1PB02 
	 *  		  - CRANE 초기정보
	 *              1	전문코드	TC				CHAR	07	전문코드	
	 *              2	발생일자	Date			CHAR	10	발생일자	YYYY-MM-DD
	 *              3	발생시간	Time			CHAR	08	발생시간	HH-MM-SS
	 *              4	전문구분	Form			CHAR	01	전문구분	I  : Initialize, U : Update,D : Delete,   R : Re-request
	 *              5	전문길이	Message_Length	CHAR	04	전문길이	
	 *              6	야드구분	Yard_Id			CHAR	01	야드구분	
	 *              7	동구분	Bay_GP			CHAR	01	동구분  	
	 *              8	설비종류	Equip_Kind		CHAR	02	설비종류	
	 *              9	설비번호	Equip_No		CHAR	02  설비번호
	 * 	YM-BIF-013	CRANE 작업지시		Level3	Level2	CN1BP01/CM1BP01	
	 *
        * Crane 작업자가 외부인터페이스(JMS)를 통해 Crane 작업지시를 요청한다.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 	 
	public boolean callCraneSchInfo(String sMessage){
		
		boolean isSuccess = false;
		
		String sYardId 		= "";	
		String sBayGp 		= "";
		String sEquipKind 	= "";
		String sEquipNo 	= "";
		String sSchCode		= "";
		String sWbookId		= "";
		String sWork        = "";
		String sSch			= "";
			
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
	    		Level2Parser level2Parser 	= new Level2Parser();
			JDTORecord jDTORecord 		= level2Parser.parse(sMessage);
			
			String sTC 		= StringHelper.evl(jDTORecord.getFieldString("전문코드"), "");
			
			if(YmCommonConst.TC_THCH520.equals(sTC)){//A열연 Coil 작업지시요구
				
				String sLegacyCraneNo 	= StringHelper.evl(jDTORecord.getFieldString("CRANE번호"), "");
				
				JDTORecord crnRc  = null;
				JDTORecord eqpRc  = null;
		    	
				/*
				 * A열연 Legacy Crane No를 가지고 현재 Crane No를 가져온다.
				 */
				crnRc = dao.getCurEquipNoWithLegacyEquipNo(sLegacyCraneNo);
					if(crnRc == null){
		    			throw new EJBServiceException("");
		    		}
	    			/*
				 * A열연 작업지시요구 전문에는 Crane No 정보만 존재.
				 * Crane No를 가지고 필요한 아래의 정보를 
				 * EQUIP TABLE에서 가져온다.
				 */		
			    	eqpRc = dao.getEquipInfoWithEquipNo(YmCommonConst.YD_GP_1,
			    										StringHelper.evl(crnRc.getFieldString("CRANE_NO"), ""));
			    	if(eqpRc == null){
		    			return false;
		    		}
									  
				sYardId    = StringHelper.evl(eqpRc.getFieldString("YD_GP"), "");
				sBayGp     = StringHelper.evl(eqpRc.getFieldString("BAY_GP"), "");
				sEquipKind = StringHelper.evl(eqpRc.getFieldString("EQUIP_KIND"), "");
				sEquipNo   = StringHelper.evl(eqpRc.getFieldString("EQUIP_NO"), "");
				
			}else if(YmCommonConst.TC_CN1PB02.equals(sTC)){ //B열연 Coil 작업지시요구
											
				sYardId    = StringHelper.evl(jDTORecord.getFieldString("야드구분"), "");
				sBayGp     = StringHelper.evl(jDTORecord.getFieldString("동구분"), "");
				sEquipKind = StringHelper.evl(jDTORecord.getFieldString("설비종류"), "");
				sEquipNo   = StringHelper.evl(jDTORecord.getFieldString("설비번호"), "");
				sWork      = StringHelper.evl(jDTORecord.getFieldString("작업구분"), "");
				sSchCode   = StringHelper.evl(jDTORecord.getFieldString("스케쥴종류"), "");
				
				if(YmCommonConst.WORK_GP_H.equals(sWork)){
					/*
					 * 2007.07.02 이정훈 
					 * 차상국에서 요구한 Sch 찾기
					 */
					sSchCode = setBCoilCraneWorkInfo(sYardId,
														sBayGp,
														sSchCode,
														sEquipNo);
					/*
					 * 2007.07.02 이정훈 
					 * UP 지시 -> Sch 등록 원복
					 * 긴급 지시 편성
					 */
					isSuccess = setBCoilCraneWorkInfo_01(sYardId,
							sBayGp,
							sSchCode,
							sEquipNo);
				} else 
				{
					sSchCode = "";
				}
				
				
				
			}else if(YmCommonConst.TC_CM1PB02.equals(sTC)){  //B열연 Slab 작업지시요구
											
				sYardId    = StringHelper.evl(jDTORecord.getFieldString("야드구분"), "");
				sBayGp     = StringHelper.evl(jDTORecord.getFieldString("동구분"), "");
				sEquipKind = StringHelper.evl(jDTORecord.getFieldString("설비종류"), "");
				sEquipNo   = StringHelper.evl(jDTORecord.getFieldString("설비번호"), "");
				sWork	   = StringHelper.evl(jDTORecord.getFieldString("작업요구구분"), "");
				sSchCode   = StringHelper.evl(jDTORecord.getFieldString("스케쥴구분"), "");
				
				if(YmCommonConst.WORK_GP_H.equals(sWork)){
					sSchCode = setBSLABCraneWorkInfo(sYardId,
													sBayGp,
													sSchCode,
													sEquipNo);
					if("NOSCH".equals(sSchCode)){
						return false;
					}													
				}
				
			}else if(YmCommonConst.TC_HM1PB02.equals(sTC)
					||YmCommonConst.TC_HM1PB52.equals(sTC)){  ////A열연 A_SLAB 야드 추가(MCH)
											
				sYardId    = StringHelper.evl(jDTORecord.getFieldString("야드구분"), "");
				sBayGp     = StringHelper.evl(jDTORecord.getFieldString("동구분"), "");
				sEquipKind = StringHelper.evl(jDTORecord.getFieldString("설비종류"), "");
				sEquipNo   = StringHelper.evl(jDTORecord.getFieldString("설비번호"), "");
				sWork	   = StringHelper.evl(jDTORecord.getFieldString("작업요구구분"), "");
				sSchCode   = StringHelper.evl(jDTORecord.getFieldString("스케쥴구분"), "");
				
				if(YmCommonConst.WORK_GP_H.equals(sWork)){
					isSuccess = setASLABCraneWorkInfo(sYardId,
													sBayGp,
													sSchCode,
													sEquipNo);
				}
				
			}else if(YmCommonConst.TC_HM1PB10.equals(sTC)
				 ||YmCommonConst.TC_HM1PB60.equals(sTC)	  //A열연 SLAB 자동이적요구
				 ||YmCommonConst.TC_CM1PB12.equals(sTC)){  //B열연 SLAB 자동이적요구	
				 							
				return setSlabAutoWorkInfo(jDTORecord);
											
			}else if(YmCommonConst.TC_CN1PB14.equals(sTC)){  ////B열연 COIL 자동이적요구
											
				return setBCoilAutoWorkInfo(jDTORecord);
				
			}
			
			logger.println(LogLevel.DEBUG,this, "sYardId="		+ sYardId);
			logger.println(LogLevel.DEBUG,this, "sBayGp="		+ sBayGp);
			logger.println(LogLevel.DEBUG,this, "sEquipKind="	+ sEquipKind);
			logger.println(LogLevel.DEBUG,this, "sEquipNo="		+ sEquipNo);
			logger.println(LogLevel.DEBUG,this, "sWork="	    	+ sWork);
			logger.println(LogLevel.DEBUG,this, "sSchCode="	+ sSchCode);
		
			isSuccess = callCraneSchInfo(sTC,
									 	sYardId,
									 	sBayGp,
									 	sEquipKind,
									 	sEquipNo,
									 	sSchCode,
									 	sWbookId,
									 	YmCommonConst.TC_WORK_R); //크레인 작업지시 요구에 의한 처리
	    	return isSuccess;
	    	
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 야드운영자가 Schedule관리기능을 통해 Crane 작업지시를 요청한다.
        * 
        * param sTcCode 		: 전문번호
        * param sYardId 		: 야드구분
        * param sBayGp 		: 동구분
        * param sEquipKind 	: 설비종류
        * param sEquipNo 		: 설비번호
        * param sSchCode 	: 스케쥴코드
        * param sWbookId 		: 작업예약ID
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 	      
       public boolean callCraneSchInfo(String sTcCode,
							     String sYardId, 
							     String sBayGp,
							     String sEquipKind,
							     String sEquipNo,
							     String sSchCode,
							     String sWbookId){
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
		return callCraneSchInfo(sTcCode,
						      	sYardId, 
							sBayGp, 
							sEquipKind,
							sEquipNo,
							sSchCode,
							sWbookId,
							YmCommonConst.TC_WORK_I); 	//크레인 작업지시 요구에 의한 처리						    	
	}
	/**
	 * 오퍼레이션명 : 
	 *
	 * 야드운영자가 Schedule관리기능을 통해 Crane 작업지시를 요청한다.
        * 
        * param sTcCode 		: 전문번호
        * param sYardId 		: 야드구분
        * param sBayGp 		: 동구분
        * param sEquipKind 	: 설비종류
        * param sEquipNo 		: 설비번호
        * param sSchCode 	: 스케쥴코드
        * param sWbookId 		: 작업예약ID
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 	      										    	
	public boolean callCraneSchInfo(String sTcCode,
							    String sYardId, 
							    String sBayGp,
							    String sEquipKind,
							    String sEquipNo,
							    String sSchCode,
							    String sWbookId,
							    String sTcGbn	//I:시스템, R:크레인 요구
							    ){
		
		boolean isSuccess 	= false;
		String sEmergency = "";
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			// 긴급작업 처리를 위한 꼼수.. 최규성 2010-01-27
			if(sTcCode.equals("CN1PB02E")){
				sEmergency = "Y";
				sTcCode = "CN1PB02";
			}

	    	logger.println(LogLevel.DEBUG,this, "====작업지시 요구 시작====");
			logger.println(LogLevel.DEBUG,this, "====sTcCode	=" + sTcCode +"=");
			logger.println(LogLevel.DEBUG,this, "====YardId		=" + sYardId +"=");
			logger.println(LogLevel.DEBUG,this, "====Bay_Gp		=" + sBayGp  +"=");
			logger.println(LogLevel.DEBUG,this, "====sSchCode	=" + sSchCode+"=");
			logger.println(LogLevel.DEBUG,this, "====CraneNo	=" + sEquipNo+"=");
			logger.println(LogLevel.DEBUG,this, "====sEmergency	=" + sEmergency+"=");
	    	/**
	    	 * 0.	A열연 B동 냉각장적치 크레인 동시작업 적용
	    	 */
	    	 	/*YJK_ALLWORK
	    	 	logger.println(LogLevel.DEBUG,this,"=작업요구 STEP 1 START="); 
		    	if(YmCommonConst.TC_THCH520.equals(sTcCode) 			&& //A열연 Coil 작업지시요구
				   YmCommonConst.YD_GP_1.equals(sYardId) 				&& //야드구분(1)
				   YmCommonConst.BAY_GP_B.equals(sBayGp) 				&& //동구분(B)
				   YmCommonConst.NEW_SCH_WORK_KIND_CDLO.equals(sSchCode) 	   //냉각장적치(CDLO)
				  ){
				  	isSuccess = setACraneDoubleWorkInfo(sYardId,
				  										sBayGp,
				  										sSchCode,
				  										sEquipNo);
				}  	
				*/
				logger.println(LogLevel.DEBUG,this,"=작업요구 STEP 1 START="); 
		    	if(YmCommonConst.TC_THCH520.equals(sTcCode) 			&& //A열연 Coil 작업지시요구
				   YmCommonConst.YD_GP_1.equals(sYardId) 				   //야드구분(1)
				  ){
				  	isSuccess = setACraneDoubleWorkInfo(sYardId,
				  										sBayGp,
				  										sSchCode,
				  										sEquipNo);
				}  	
				
				logger.println(LogLevel.DEBUG,this,"=작업요구 STEP 1 END="); 
		    	/**
		    	 * 1.	스케쥴정보가져오기
		    	 */
	    	 	logger.println(LogLevel.DEBUG,this,"=작업요구 STEP 2 START="); 
	    		JDTORecord schInfo	= null;
	    		
	    		/**
	    		 * 1.0	긴급작업지시가 아닌 스케쥴이 UP지시,UP실적,PUT지시 상태인 정보가 있는지를 체크한다.
	    		 *		해당정보가 있으면 긴급작업은 무시한다.
	    		 */
	    		JDTORecord chkInfo = dao.getNotEmergencyCraneSchInfo(sYardId,
							    									 sBayGp,
							    									 sEquipKind,
							    									 sEquipNo);
				logger.println(LogLevel.DEBUG,this,"=작업요구 긴급작업지시체크 단계=");			    									   
				logger.println(LogLevel.DEBUG,this,"=작업요구 STEP 2.0 END="); 
				
				if(chkInfo == null){
		    		/**
		    		 * 1.0	긴급작업지시 스케쥴이 있는지를 체크한다.
		    		 */
		    		schInfo = dao.getEmergencyCraneSchInfo(sYardId,
				    									   sBayGp,
				    									   sEquipKind,
				    									   sEquipNo);
					logger.println(LogLevel.DEBUG,this,"=작업요구 긴급작업지시편성 단계=");			    									   
					logger.println(LogLevel.DEBUG,this,"=작업요구 STEP 2.1 END="); 			    									   
				}
				
	    		if(schInfo == null){
	    			
	    			/**
	        		 * 1.1 B열연 :lineoff가 존재 하는 경우 우선 순위로 작업
	        		 */
	    			if("3".equals(sYardId)&&!"E".equals(sBayGp)){
	    				logger.println(LogLevel.DEBUG,this,"=작업요구 STEP 2.2 START="); 
	    				schInfo = dao.getCraneSchInfo(sYardId,
						    						sBayGp,
						    						sEquipKind,
						    						sEquipNo,
	    											"",
	    											"",
	    											"LINEOFF");
	    				logger.println(LogLevel.DEBUG,this,"=작업요구 LINEOFF우선편성 단계=");			    									   
						logger.println(LogLevel.DEBUG,this,"=작업요구 STEP 2.2 END="); 	
	    			}
	    			if(schInfo == null){
	    			
	    			/**
		    		 * 1.2	같은 작업예약ID로 묶인 스케쥴이 있는지를 체크한다.
		    		 */
		    		if(!"".equals(sWbookId)){ 
			    		logger.println(LogLevel.DEBUG,this,"=작업요구 STEP 3 START=");  
			    		schInfo = dao.getCraneSchInfo(sYardId,
			    									  sBayGp,
			    									  sEquipKind,
			    									  sEquipNo,
			    									  sSchCode,
			    									  sWbookId);
			    		logger.println(LogLevel.DEBUG,this,"=작업요구 동일작업예약ID 단계=");							  
						logger.println(LogLevel.DEBUG,this,"=작업요구 STEP 3 END="); 
					}
							    									  
		    		if(schInfo == null){
		    			sWbookId  = "";
		    			
		    			logger.println(LogLevel.DEBUG,this,"=작업요구 STEP 3.1 START=");  
		    			boolean isPutWork = false;
			    		
			    		/**
			    		 * 1.3	크레인에게 할당된 PUT 작업지시가 있는지를 체크한다.
			    		 */
			    		JDTORecord putInfo = dao.getSchInfoWithEquipNo(sYardId,
				    									    		   YmCommonConst.WORK_PROG_STAT_3,	//PUT지시	
				    			                            		   sEquipNo);
				    	if(putInfo == null){		
				    		isPutWork = true;	                          
				    	}
				    	logger.println(LogLevel.DEBUG,this,"=작업요구 PUT스케쥴 작업 체크단계="+isPutWork);
				    	logger.println(LogLevel.DEBUG,this,"=작업요구 STEP 3.1 END=");  
		    			/**
			    		 *	  	연속작업가능 스케쥴코드에만 해당
			    		 *	  	A열연과 B열연 처리방식 다르다.
			    		 *	  	A열연 : 해당스케쥴코드로 작업을 요구한다.
			    		 *				스케쥴코드에 해당하는 작업검색기능 필요.
			    		 *	  	B열연 : 특정스케쥴코드로 작업을 요구하지 않는다.	
			    		 *			    연속작업가능 스케쥴코드만 작업검색기능 필요.
			    		 */
			    		if(isPutWork){
			    			logger.println(LogLevel.DEBUG,this,"=작업요구 STEP 4 START=");  
				    		boolean isConWork = false;
				    		
				    		if(YmCommonConst.YD_GP_1.equals(sYardId)){
				    			isConWork = true;
				    			logger.println(LogLevel.DEBUG,this,"=작업요구 A열연방식 스케쥴코드에 해당하는 작업검색="+sSchCode);  
					    	}else{
					    		isConWork = YmCommonUtil.isContinueWork(sSchCode); 
					    		logger.println(LogLevel.DEBUG,this,"=작업요구 B열연방식(A열연 SLAB 포함) 연속작업가능 스케쥴코드만 작업검색="+sSchCode);  
						}
					    	
				    		if(isConWork){
				    			/**
					    		 * 1.4	같은 스케쥴코드로 묶인 스케쥴이 있는지를 체크한다.
					    		 * 
					    		 */
				    			logger.println(LogLevel.DEBUG,this,"=같은 스케쥴코드로 묶인 스케쥴이 있는지를 검사=");
					    		schInfo = dao.getCraneSchInfo(sYardId,
					    									  sBayGp,
					    									  sEquipKind,
					    									  sEquipNo,
					    									  sSchCode,
					    									  sWbookId);
				    		}
					    	logger.println(LogLevel.DEBUG,this,"=작업요구 동일스케쥴 연속작업 단계=");
					    	logger.println(LogLevel.DEBUG,this,"=작업요구 STEP 4 END=");  
				    	}
			    		
			    		if(schInfo == null){
			    			//sSchCode  = "";
			    			/**
				    		 * 1.5	해당크레인에 할당된 스케쥴이 있는지를 체크한다.
				    		 */
				    		logger.println(LogLevel.DEBUG,this,"=작업요구 STEP 5 START=");
				    		if(YmCommonConst.NEW_SCH_WORK_KIND_CNLO.equals(sSchCode)){
				    			logger.println(LogLevel.DEBUG,this,"=작업요구 해당크레인 스케쥴 단계= SPM2추출");
				    			schInfo = dao.getCraneSchInfo_CNLO(sYardId,
											    					sBayGp,
											    					sEquipKind,
											    					sEquipNo,
											    					sSchCode,
											    					sWbookId);
				    		}else{
				    			logger.println(LogLevel.DEBUG,this,"=작업요구 해당크레인 스케쥴 단계= SPM2추출 이");
				    			sSchCode  = "";
				    		schInfo = dao.getCraneSchInfo(sYardId,
				    									  sBayGp,
				    									  sEquipKind,
				    									  sEquipNo,
				    									  sSchCode,
				    									  sWbookId);
				    		}
				    		logger.println(LogLevel.DEBUG,this,"=작업요구 해당크레인 스케쥴 단계=");							  
				    		logger.println(LogLevel.DEBUG,this,"=작업요구 STEP 5 END=");  							  
				    	}		
				    	
		    		}
			    	
	    			 
		    		
		    		/* 주의 :
		    		 * 작업요구 모듈에서 작업예정 검색 안함 - 권상모듈로 이동
		    		 */
//		    		if(false){
//		    			/**
//		    			 * 권하시점에 스케쥴정보가 존재하면 해당 스케쥴로 작업지시요구를 한다.
//		    			 * 스케쥴정보가 없으면 크레인상태를 준비상태로 셋팅하고, 다시 작업지시요구를 한다.
//		    			 * 따라서, 작업지시요구EJB에서 스케쥴정보가 존재하지 않으면 이 경우에는 작업예정내역을 조회한다.
//		    			 * 작업예정이 존재하면 해당 작업예정에 대해스케쥴을 기동시킨다.
//		    			 * sSchCode = "" ,sWbookId = "" 인 경우
//		    			 */
//		    			
//		    			isSuccess = setSchInfoFromWbook_01(sYardId,
//				    									   sBayGp,
//					    								   sEquipKind,
//					    								   sEquipNo);
//					    logger.println(LogLevel.DEBUG,this,"=작업요구 스케쥴 데이타 존재안함=");	 
//		    			logger.println(LogLevel.DEBUG,this,"=작업요구 작업예정 검색 =");								   
//					    logger.println(LogLevel.DEBUG,this,"=작업요구 모듈 종료(이후 처리안함)=");	
//		    			
//		    			/**
//			    		 *	이 모듈에서 작업요구에 대한 스케쥴을 호출한다는 것은 크레인이 IDLE 상태라는 것이다.
//			    		 *	따라서 스케쥴 모듈에서 다시 작업지시를 하기 때문에 이후의 Process를 실행할 필요는 없다.
//			    		 */
//			    		return isSuccess;
//		    		}
		    		
	    	 	}
	    		}
	    	 	
	    	 	if(schInfo == null){
		    			
	    			logger.println(LogLevel.DEBUG,this,"=작업요구 => 작업지시 정보가 존재안함=");
	    			logger.println(LogLevel.DEBUG,this,"야드 정보 : "+sYardId);
	    			/**
	    			 *	작업지시 정보가 없을 경우
	    			 *	'대기' 작업지시 전문을 송신한다.
	    			 */
					if(YmCommonConst.YD_GP_1.equals(sYardId)){
		    			
		    			isSuccess	=	callACoilCraneMsgInfo(YmCommonConst.TC_THHC110,		
		    												  sYardId	+
															  sBayGp	+
						    								  sEquipKind+
						    								  sEquipNo);
	    			}else if(YmCommonConst.YD_GP_2.equals(sYardId)
							||YmCommonConst.YD_GP_0.equals(sYardId)){		//A열연 SLAB야드 추가(MCH)
		    			
		    			isSuccess	=	callBSlabCraneMsgInfo(sYardId	+
															  sBayGp	+
						    								  sEquipKind+
						    								  sEquipNo);
	    			}else if(YmCommonConst.YD_GP_3.equals(sYardId)){
	    				
	    				isSuccess	=	callBCoilCraneMsgInfo(sYardId	+
													      sBayGp	+
						    							      sEquipKind+
						    							      sEquipNo);
	    			}
	    			
	    			return isSuccess;
	    		
	    		}else{
	    			
		    		logger.println(LogLevel.DEBUG,this,"=작업요구 => 작업지시 정보가 존재함=");				
		    		
		    		/*
			    	 *	1.6	해당크레인에 할당된 스케쥴의 상위 우선순위를 찾는다(예외 사항)
			    	 *		즉, 기존의 야드 작업지시규칙에 위배되는 작업을 송신해야하는 경우
			    	 */
			    	 				    		
		    		//	작업지시내릴 스케쥴 코드 
		    		String sSchWorkKind = StringHelper.evl(schInfo.getFieldString("SCH_WORK_KIND"),"");
		    		
				if(YmCommonConst.TC_CN1PB02.equals(sTcCode) 				&& //B열연 COIL작업지시요구
				   YmCommonConst.YD_GP_3.equals(sYardId) 					&& //야드구분(3)
				   YmCommonConst.NEW_SCH_WORK_KIND_CDLO.equals(sSchWorkKind)){ //분기LINE-OFF 요구(CDLO)
				  	
				  	/*
				    	 *	1.6.1	B열연 COIL 분기콘베이어 LINE-OFF 요구시 처리
				    	 *			A,B,C동 마지막위치포지션부터 LINE-OFF 요구 처리
					 *			A 1F 3AST01 01,02,03,04,05
					 *			B 2F 3BST01 01,02,03,04
					 *			C 3S 3CST01 01,02,03
					 */	
					String sSchStat = StringHelper.evl(schInfo.getFieldString("SCH_WORK_STAT"),"");
			    		String sSchId 	= StringHelper.evl(schInfo.getFieldString("SCH_ID"), "");
    			    	
    			    		if(YmCommonConst.SCH_WORK_STAT_S.equals(sSchStat)){
    			    	   	
						JDTORecord ordJr = dao.getFirstDConveyorInfo(sYardId,
							    									 sBayGp,
							    									 sSchWorkKind,
							    									 sEquipNo);
						if(ordJr != null){
							schInfo = ordJr;
								
							/*
				    			 *	1.6.1.1	UP지시 상태이면 크레인,스케쥴정보를 초기화한다.
				    			 *			크레인정보 초기화는 하지 않는다(작업지시때 수정).
				    			 */
				    			if(YmCommonConst.SCH_WORK_STAT_1.equals(sSchStat)){
				    				
								int iSeq = dao.updateCraneSchStat(sSchId, YmCommonConst.SCH_WORK_STAT_S);
								logger.println(LogLevel.DEBUG,this,"=작업요구 => 스케쥴 정보 초기화="+iSeq);	
							}	
							String tSchId 	= StringHelper.evl(schInfo.getFieldString("SCH_ID"), "");
							logger.println(LogLevel.DEBUG,this,"=작업요구 => A,B,C동 마지막위치포지션부터 LINE-OFF 요구 처리=");							  
				    			logger.println(LogLevel.DEBUG,this,"=작업요구 => 기존 SCH_ID="+sSchId);							  
				    			logger.println(LogLevel.DEBUG,this,"=작업요구 => 수정 SCH_ID="+tSchId);							  
			    				logger.println(LogLevel.DEBUG,this,"=작업요구 STEP 6 END=");  				
						}	    									 
    			    		} 
				}else if(YmCommonConst.TC_CN1PB02.equals(sTcCode) 				&& //B열연 COIL작업지시요구
						   YmCommonConst.YD_GP_3.equals(sYardId) 					&& //야드구분(3)
						   YmCommonConst.NEW_SCH_WORK_KIND_CNLO.equals(sSchWorkKind)){ //SPM2 LINE-OFF 요구(CNLO)
					
					logger.println(LogLevel.DEBUG,this,"=작업요구 =>SPM2 스케쥴 정보 초기화 작업시작=");
					
					String sSchStat = StringHelper.evl(schInfo.getFieldString("SCH_WORK_STAT"),"");
		    		String sSchId 	= StringHelper.evl(schInfo.getFieldString("SCH_ID"), "");
		    		logger.println(LogLevel.DEBUG,this,"=작업요구 =>SPM2 스케쥴 정보 STAT = "+sSchStat);
		    		logger.println(LogLevel.DEBUG,this,"=작업요구 =>SPM2 스케쥴 정보 SCHCD= "+sSchId);
		    		if(YmCommonConst.SCH_WORK_STAT_S.equals(sSchStat) ){
//		    			JDTORecord jtrEquipInfo = dao.getEquipInfo_Spm2(sEquipNo);
//						String sWprogStat = StringHelper.evl(jtrEquipInfo.getFieldString("WPROG_STAT"), "");
						
//						logger.println(LogLevel.DEBUG,this,"=작업요구 => 장비상태검사"+sWprogStat);
			    		
//						if(sWprogStat.equals("I") || sWprogStat.equals("W")) {
						if(!sEmergency.equals("Y")){
							
		    			
							JDTORecord ordJr = dao.getFirstSchInfo_Spm2( sYardId,
								    									 sBayGp,
								    									 sSchWorkKind,
								    									 sEquipNo);
							if(ordJr != null){
								schInfo = ordJr;
							
								/*
					    			 *	1.6.1.1	UP지시 상태이면 크레인,스케쥴정보를 초기화한다.
					    			 *			크레인정보 초기화는 하지 않는다(작업지시때 수정).
					    			 */
									int iSeq = dao.updateCraneSchStat_spm2(YmCommonConst.SCH_WORK_STAT_S);
									logger.println(LogLevel.DEBUG,this,"=작업요구 =>SPM2 스케쥴 정보 초기화="+iSeq);	
							
								String tSchId 	= StringHelper.evl(schInfo.getFieldString("SCH_ID"), "");
								logger.println(LogLevel.DEBUG,this,"=작업요구 => E동 마지막위치포지션부터 LINE-OFF 요구 처리=");							  
					    		logger.println(LogLevel.DEBUG,this,"=작업요구 => 기존 SCH_ID="+sSchId);							  
					    		logger.println(LogLevel.DEBUG,this,"=작업요구 => 수정 SCH_ID="+tSchId);	
					    		logger.println(LogLevel.DEBUG,this,"=작업요구 =>SPM2 스케쥴 정보 초기화 작업종=");
				    			logger.println(LogLevel.DEBUG,this,"=작업요구 STEP 6 END=");  				
							}	
			    		}
		    		}
				}else if(YmCommonConst.TC_CN1PB02.equals(sTcCode) 				&& //B열연 COIL작업지시요구
					   YmCommonConst.YD_GP_3.equals(sYardId) 					&& //야드구분(3)
					   YmCommonConst.NEW_SCH_WORK_KIND_CELO.equals(sSchWorkKind)){ //확장 LINE-OFF 요구(CELO)
					
					/*
					 * 06.11.20 이정훈 
					 * 확장 Conv Line-Off 요구시 4E 분기 위치 순서 변경 (1->2->3->4->5)
					 */
							  	
					/*
				    	 *	1.6.1	B열연 COIL 확장콘베이어 LINE-OFF 요구시 처리
					 *			C 4E 3CWB10 01,02,03,04,05
					 */	
					String sSchStat = StringHelper.evl(schInfo.getFieldString("SCH_WORK_STAT"),"");
    			    		String sSchId 	= StringHelper.evl(schInfo.getFieldString("SCH_ID"), "");
    			    	
    			    		if(YmCommonConst.SCH_WORK_STAT_S.equals(sSchStat)){
    			    	   	//||YmCommonConst.SCH_WORK_STAT_1.equals(sSchStat)){
    			    	   	
						JDTORecord ordJr = dao.getFirstEConveyorInfo(sYardId,
							    									 sBayGp,
							    									 sSchWorkKind,
							    									 sEquipNo);
						if(ordJr != null){
							schInfo = ordJr;
								
							/*
				    			 *	1.6.1.1	UP지시 상태이면 크레인,스케쥴정보를 초기화한다.
				    			 *			크레인정보 초기화는 하지 않는다(작업지시때 수정).
				    			 */
				    			if(YmCommonConst.SCH_WORK_STAT_1.equals(sSchStat)){
				    				
								int iSeq = dao.updateCraneSchStat(sSchId,
						  				  				  	      YmCommonConst.SCH_WORK_STAT_S);
								logger.println(LogLevel.DEBUG,this,"=작업요구 => 스케쥴 정보 초기화="+iSeq);	
							}	
							String tSchId 	= StringHelper.evl(schInfo.getFieldString("SCH_ID"), "");
							
							logger.println(LogLevel.DEBUG,this,"=작업요구 => 확장  마지막위치포지션부터 LINE-OFF 요구 처리=");							  
				    			logger.println(LogLevel.DEBUG,this,"=작업요구 => 기존 SCH_ID="+sSchId);							  
				    			logger.println(LogLevel.DEBUG,this,"=작업요구 => 수정 SCH_ID="+tSchId);							  
			    				logger.println(LogLevel.DEBUG,this,"=작업요구 STEP 6 END=");  				
						}	    									 
    			    		} 				
				}else{
					/*
				    	 *	1.6.2	A1 크레인이 01단 저장품이 우선순위 작업지시대상이나,
				    	 *			02단에 A1 크레인에 할당된 또다른 작업이 존재할 때
				    	 *			물리적으로 02단을 우선적으로 작업지시를 줘야한다.
				    	 */
					 
					   logger.println(LogLevel.DEBUG,this,"=B동 CTC보급 시 권하위치 P셋팅"+sYardId+sBayGp+sSchWorkKind);
					   if( YmCommonConst.YD_GP_2.equals(sYardId) && sBayGp.equals("B") && YmCommonConst.NEW_SCH_WORK_KIND_SCLI.equals(sSchWorkKind))
					   {
						   
						   JDTORecord WbookSltreq = dao.getListYwbookStlNo(sWbookId);
						   
						   if(WbookSltreq != null){
							     String slabNo 	= StringHelper.evl(WbookSltreq.getFieldString("STOCK_ID"), "");
					    
							     int iSeq = dao.updateCraneStackLayerStat(YmCommonConst.STACK_COL_GP_2BCT03,
									  YmCommonConst.STACK_BED_GP_01,
									  YmCommonConst.STACK_LAYER_GP_01,
									  slabNo,
									  "P");						   
					       }
					   }
					  
		
			    		String sSchStat = StringHelper.evl(schInfo.getFieldString("SCH_WORK_STAT"),"");
    			    		String sSchId 	= StringHelper.evl(schInfo.getFieldString("SCH_ID"), "");
    			    	
    			    		if(YmCommonConst.SCH_WORK_STAT_S.equals(sSchStat)||
    			    	   		YmCommonConst.SCH_WORK_STAT_1.equals(sSchStat)){
				    		
				    		JDTORecord ordJr = null;
				    		if(YmCommonConst.YD_GP_2.equals(sYardId) 
				    		|| YmCommonConst.YD_GP_0.equals(sYardId)){ //A열연 SLAB야드 추가(MCH)
		    					ordJr = dao.getCraneSchSlabInfo(sSchId);
					    	}else{
				    			ordJr = dao.getCraneSchCoilInfo(sSchId);
				    		}
				    		
				    		if(ordJr != null){
				    			schInfo = ordJr;
				    			
				    			/*
				    			 *	1.6.2.1	UP지시 상태이면 크레인,스케쥴정보를 초기화한다.
				    			 *			크레인정보 초기화는 하지 않는다(작업지시때 수정).
				    			 */
				    			if(YmCommonConst.SCH_WORK_STAT_1.equals(sSchStat)){
				    				
								int iSeq = dao.updateCraneSchStat(sSchId
															, YmCommonConst.SCH_WORK_STAT_S);
								logger.println(LogLevel.DEBUG,this,"=작업요구 => 스케쥴 정보 초기화="+iSeq);	
							}	
								
				    			String tSchId 	= StringHelper.evl(schInfo.getFieldString("SCH_ID"), "");
				    			logger.println(LogLevel.DEBUG,this,"=작업요구 => 예외 상위 운선순위 존재함=");							  
				    			logger.println(LogLevel.DEBUG,this,"=작업요구 => 기존 SCH_ID="+sSchId);							  
				    			logger.println(LogLevel.DEBUG,this,"=작업요구 => 수정 SCH_ID="+tSchId);							  
			    				logger.println(LogLevel.DEBUG,this,"=작업요구 STEP 6 END=");  				
				    		}
			    		}   
				}//END ELSE
			 }//END ELSE
	    	/**
	    	 * 2.	설비 및 크레인 상태변경
	    	 */ 
	    	 	int iReq = -1;
	    	 	 
	    		iReq = setCraneStatInfo(schInfo);
	    		
	    	/**
	    	 * 3.	작업지시Message Call
	    	 */
	    	 	/*
	    	 	 * A열연은 권상,권하 작업지시를 한번만 준다.
	    	 	 */
	    		if(YmCommonConst.YD_GP_1.equals(sYardId)){
	    		    
	    		    String sSchWorkStat = StringHelper.evl(schInfo.getFieldString("SCH_WORK_STAT"),"");
			    	
			    	if(YmCommonConst.SCH_WORK_STAT_S.equals(sSchWorkStat)||
			    	   YmCommonConst.SCH_WORK_STAT_1.equals(sSchWorkStat)){
			    		
		    		boolean isMsg =	callCraneMsgInfo(sTcCode, schInfo);
			    	}  
	    		/*
	    	 	 * B열연은 권상,권하 작업지시를 각각 준다.
	    	 	 */
	    		}else{
	    		    boolean isMsg =	callCraneMsgInfo(sTcCode, schInfo, sTcGbn);
	    		}
		    		
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess;
	}
	
	/**
	 * 권하시점에 스케쥴정보가 존재하면 해당 스케쥴로
	 * 작업지시요구를 한다.
	 * 스케쥴정보가 없으면 크레인상태를 준비상태로 셋팅하고,
	 * 다시 작업지시요구를 한다.
	 * 따라서, 작업지시요구EJB에서 스케쥴정보가 존재하지 않으면
	 * 이 경우에는 작업예정내역을 조회한다.
	 * 작업예정이 존재하면 해당 작업예정에 대해
	 * 스케쥴을 기동시킨다.
	 * 스케쥴을 기동시키고 해당 스케쥴정보를 Return한다.
	 *
	 * @param sYardId 		: 야드구분
     * @param sBayGp 		: 동구분
     * @param sEquipKind	: 설비종류
     * @param sEquipNo 		: 설비번호
     *
     * @return JDTORecord : SCHEDULE
     * @throws 
	 */
	private boolean setSchInfoFromWbook_01(String sYard_Id, 
										   String sBay_Gp,
										   String sEquipKind,
						    			   String sEquipNo){
		Boolean isSuccess = new Boolean(false);
		
		try{ 
		
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			/**
    		 * 스케쥴코드가 없으면 해당 크레인에
    		 * 할당된 작업예정을 검색한다.
    		 */
    		JDTORecord wbookRc = dao.getCraneWbookInfo_01(sYard_Id,
				    									  sBay_Gp,
				    									  sEquipKind,
				    									  sEquipNo);
    		if(wbookRc != null){
    			/**
	    		 * 작업예정이 존재하면 해당 작업예정에 대해 스케쥴을 호출한다.
	    		 */
	    		String sWbookID = StringHelper.evl(wbookRc.getFieldString("WBOOK_ID"), "");
	    		
	    		if(YmCommonConst.YD_GP_2.equals(sYard_Id)){
	    			
	    			EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
					isSuccess = (Boolean)ejbConn.trx("syCraneScheduleInfoInsert",new  Class[]{String.class},
																				 new Object[]{sWbookID});
    			}else{
    				
    				EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
					isSuccess = (Boolean)ejbConn.trx("callCraneSchInfo",new  Class[]{String.class},
																		new Object[]{sWbookID});
    			} 
		    	
			}
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }		
	    
	    return isSuccess.booleanValue();							   	
	}
	/**
	 * 해당동에 특정 스케쥴코드에 해당하는 작업예약을 
	 * 생성한다.
	 * 작업예약은 크레인번호에 관계없이 생성.
	 * 스케쥴은 Param으로 받은 크레인으로 할당.
	 *
	 * @param sYardId 		: 야드구분
     * @param sBayGp 		: 동구분
     * @param sSchCode		: 스케쥴코드
     * @param sCraneNo		: 크레인번호
     *
     * @return JDTORecord : SCHEDULE
     * @throws 
	 */
	private boolean setSchInfoFromWbook_02(String sYard_Id, 
										   String sBay_Gp,
										   String sSchCode,
										   String sCraneNo){
		Boolean isSuccess = new Boolean(false);
		
		try{ 
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			/**
    		 * 스케쥴코드가 없으면 해당 크레인에
    		 * 할당된 작업예정을 검색한다.
    		 */
    		JDTORecord wbookRc = dao.getCraneWbookInfo_02(sYard_Id,
				    									  sBay_Gp,
				    									  sSchCode);
    		if(wbookRc != null){
    			/**
	    		 * 작업예정이 존재하면
	    		 * 해당 작업예정에 대해 스케쥴을 호출한다.
	    		 */
	    		String sWbookID = StringHelper.evl(wbookRc.getFieldString("WBOOK_ID"), "");
	    		
	    		logger.println(LogLevel.DEBUG,this, "setSchInfoFromWbook_02() WBOOK_ID: "+sWbookID);	
				EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
				isSuccess = (Boolean)ejbConn.trx("callCraneSchInfo",new  Class[]{String.class,
																				 String.class},
																	new Object[]{sWbookID,
																				 sCraneNo});
			}
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }		
	    
	    return isSuccess.booleanValue();							   	
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 해당동에 특정 스케쥴코드에 해당하는 작업예약을 
	 * 생성한다.
	 * 작업예약은 크레인번호에 관계없이 생성.
	 * 스케쥴은 크레인기준에 따라 할당.
	 *
	 * param sYardId 		: 야드구분
        * param sBayGp 		: 동구분
        * param sSchCode		: 스케쥴코드
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 	      	 
	public boolean setSchInfoFromWbook_03(String sYard_Id, 
										  String sBay_Gp,
										  String sSchCode){
		Boolean isSuccess = new Boolean(false);
		
		try{ 
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			/**
    		 * 스케쥴코드가 없으면 해당 크레인에
    		 * 할당된 작업예정을 검색한다.
    		 */
    		JDTORecord wbookRc = dao.getCraneWbookInfo_02(sYard_Id,
				    									  sBay_Gp,
				    									  sSchCode);
    		if(wbookRc != null){
    			/**
	    		 * 작업예정이 존재하면
	    		 * 해당 작업예정에 대해 스케쥴을 호출한다.
	    		 */
	    		String sWbookID = StringHelper.evl(wbookRc.getFieldString("WBOOK_ID"), "");
    				
	    		logger.println(LogLevel.DEBUG,this, "setSchInfoFromWbook_03() WBOOK_ID: "+sWbookID);	
				EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
				isSuccess = (Boolean)ejbConn.trx("callCraneSchInfo",new  Class[]{String.class},
																	new Object[]{sWbookID});
			}
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }		
	    
	    return isSuccess.booleanValue();							   	
	}
	
	/**
     * Crane 작업지시에 관련된 상태정보를 등록 및 수정한다.
     * 
     * @param dao 			: DAO
     * @param jDTORecord 	: 스케쥴정보
     *
     * @return
     * @throws 
     */	
	private int setCraneStatInfo(JDTORecord schRc){
		int iReq = -1;
		
		try{ 
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			String sScheduleId 	= StringHelper.evl(schRc.getFieldString("SCH_ID"), "");
	    	String sSchWorkStat = StringHelper.evl(schRc.getFieldString("SCH_WORK_STAT"),"");
	    	
	    	logger.println(LogLevel.DEBUG,this, "sScheduleId	="	+ sScheduleId);
	    	logger.println(LogLevel.DEBUG,this, "sSchWorkStat   ="	+ sSchWorkStat);
	    	
	    	String sWorkProgStat= "";
	    	
	    	if(YmCommonConst.SCH_WORK_STAT_S.equals(sSchWorkStat)){
	    		sWorkProgStat	= YmCommonConst.WORK_PROG_STAT_1;
	    	}else if(YmCommonConst.SCH_WORK_STAT_1.equals(sSchWorkStat)){
	    		sWorkProgStat	= YmCommonConst.WORK_PROG_STAT_1;
	    	}else if(YmCommonConst.SCH_WORK_STAT_2.equals(sSchWorkStat)){
	    		sWorkProgStat	= YmCommonConst.WORK_PROG_STAT_3;
	    	}else if(YmCommonConst.SCH_WORK_STAT_3.equals(sSchWorkStat)){
	    		sWorkProgStat	= YmCommonConst.WORK_PROG_STAT_3;
	    	}
			/*
			 * Crane 설비상태를 변경한다.
			 * tb_ym_equip Table : work_prog_stat = '1'(UP 지시)
			 * tb_ym_equip Table : work_prog_stat = '3'(PUT 지시)
			 * tb_ym_equip Table : wbook_id       = SCH_ID
			 */			
	    	iReq = dao.updateCraneEquipStatFromOrd(sScheduleId, sWorkProgStat);
	    	/*
			 * Crane 작업상태를 변경한다.
			 * tb_ym_sch Table : sch_work_stat = '1'(UP 지시)
			 * tb_ym_sch Table : sch_work_stat = '3'(PUT 지시)
			 */			
	    	iReq = dao.updateCraneSchStat(sScheduleId, sWorkProgStat);
	    	
	    	logger.println(LogLevel.DEBUG,this, "스케쥴 및 설비 상태 셋팅 완료 ="+ sWorkProgStat);
	    	
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return iReq;
	    
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * A열연 적치요구 크레인 동시작업 처리
        *
        *	- 직열작업 방법 
        * 		-	해당동 스케쥴 정보 'S','1' 인것만 가져온다.
        *		-	'1'일경우 설비,스케쥴 초기화하고, 작업취소 전문 송신한다.	
        *		-	상대에게 내려간 작업지시 부터 가져온다.
        *	- 병열작업 방법
        *		-	해당동 스케쥴 정보 'S' 인것만 가져온다.
        *		-	상대에게 내려간 작업지시 다음부터 가져온다.
        * 
        * param sYardId 	: 야드구분
        * param sBayGp 	: 동구분
        * param sEquipNo 	: 설비번호
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 	        
	public boolean setACraneDoubleWorkInfo(String sYardId, 
										   String sBayGp,
										   String sSchCode,
										   String sEquipNo){
		
		boolean isSuccess 	= false;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
	    	logger.println(LogLevel.DEBUG,this, "= A열연 동시작업 = CRANE 작업지시 요구 시작====");
			/**
			 *	1.	해당 동에 할당된 스케쥴 정보를 가져온다.
			 *		'S','1' 상태인 스케쥴 정보만 가져온다.
			 */
				JDTORecord schInfo	= null;
	    		schInfo = dao.getCraneSchInfo(sYardId,
		    					  		      sBayGp,
		    								  sSchCode);
    		/**
			 *	2.	해당 동에 할당된 작업예약 정보를 가져온다.
			 *		스케쥴에 등록되지 않은 작업예약ID만 가져온다.
			 */
				if(schInfo == null){
		    		
		    		/* 주의 :
		    		 * 작업예정에만 등록된 정보를 스케쥴을 생성해서 할당하지 않는다.
		    		 * 스케쥴테이블만 검색해서 정보가 존재하면 크레인 할당한다.	
		    		 */
	    			/*
	    			isSuccess = setSchInfoFromWbook_02(sYardId,
			    									   sBayGp,
			    									   sSchCode,
			    									   sEquipNo);
	    			 
	    			if(isSuccess){
	    				schInfo = dao.getCraneSchInfo(sYardId,
				    					  		      sBayGp,
				    								  sSchCode);
	    			}
	    			*/
	    		}
			/**
			 *	3.	기준 크레인에 할당된 스케쥴정보를 요구 크레인에 할당한다.
			 */
			 	if(schInfo != null){
			 		
			 		String sSelCraneNo 	= StringHelper.evl(schInfo.getFieldString("SCH_WORK_EQUIP_NO"), "");
			 		String sScheduleId 	= StringHelper.evl(schInfo.getFieldString("SCH_ID"), "");
			 		String sSchWorkStat	= StringHelper.evl(schInfo.getFieldString("SCH_WORK_STAT"), "");
			 		
			 		logger.println(LogLevel.DEBUG,this, "sSelCraneNo	="	+ sSelCraneNo);
			 		logger.println(LogLevel.DEBUG,this, "sEquipNo		="	+ sEquipNo);
			 		logger.println(LogLevel.DEBUG,this, "sScheduleId	="	+ sScheduleId);
			 		
			 		if(sSelCraneNo.equals(sEquipNo)){
			 			/*
			 			 * 기 등록된 스케쥴의 크레인번호 일치
			 			 */
			 			//SKIP
			 			logger.println(LogLevel.DEBUG,this, "= A열연 동시작업 = 기 등록된 스케쥴의 크레인번호 일치====");    	 
			 		}else{
			 		
			 			if(YmCommonConst.SCH_WORK_STAT_1.equals(sSchWorkStat)){
			 				 /*
			 				  *	1.	UP지시 취소 전문을 송신한다.
			 				  */
			 					isSuccess = callACoilCraneMsgInfo(YmCommonConst.TC_THHC120,		
									 			  				  sScheduleId);
								logger.println(LogLevel.DEBUG,this,"= A열연 동시작업 = 스케쥴 취소 전문 송신="+isSuccess);		
			 				 /*
			 				  *	2.	크레인 설비상태 idle셋팅
			 				  */
								int iReq1 = dao.updateSubCraneEquipStat(sYardId,
					    										        sBayGp,
																		YmCommonConst.EQUIP_KIND_CR,
																		sSelCraneNo,
																		YmCommonConst.WORK_PROG_STAT_W,
					    										   		"");
								logger.println(LogLevel.DEBUG,this,"= A열연 동시작업 = 크레인 설비상태 idle 셋팅="+iReq1); 		 
			 				 /*
			 				  *	3.	스케쥴 정보 초기화
			 				  */  
								int iReq2 = dao.updateCraneSchStat(sScheduleId,
					   									  	  	   YmCommonConst.SCH_WORK_STAT_S); 
					   			logger.println(LogLevel.DEBUG,this,"= A열연 동시작업 = 스케쥴 정보 초기화="+iReq2);						  	  
			 			}
			 			
			 			/*
			 			 * 기 등록된 스케쥴의 크레인번호와 불일치
			 			 * 스케쥴 TABLE CRANE 번호를 수정한다.
					 	 * tb_ym_sch Table : sch_work_equip_no = 작업요구 크레인
			 			 */
			 			int iReq3 = dao.updateCraneNoWithSchId(sScheduleId,
			    									  	  	   sEquipNo); 
			    		logger.println(LogLevel.DEBUG,this, "= A열연 동시작업 = 기 등록된 스케쥴의 크레인번호 불일치="+iReq3); 
			 		}
			    }	
			logger.println(LogLevel.DEBUG,this, "= A열연 동시작업 = CRANE 작업지시 요구 종료====");    								  	  
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
	 * A열연 SLAB야드 크레인 메뉴얼 적치요구시 수신 처리
        *
        *	- 직열작업 방법 
        * 		-	해당동 스케쥴 정보 'S','1' 인것만 가져온다.
        *		-	'1'일경우 설비,스케쥴 초기화하고, 작업취소 전문 송신한다.	
        *		-	상대에게 내려간 작업지시 부터 가져온다. 
        * param sYardId 		: 야드구분
        * param sBayGp 		: 동구분
        * param sEquipNo 		: 설비번호
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 	             
	public boolean setASLABCraneWorkInfo(String sYardId, 
										   String sBayGp,
										   String sSchCode,
										   String sEquipNo){
		
		boolean isSuccess 	= false;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
	    	logger.println(LogLevel.DEBUG,this, "= A열연  SLAB야드 = CRANE 작업지시 요구 시작====");
			/**
			 *	1.	현재 UP지시가 내려간 작업스케쥴 정보를 가져온다.
			 */
				JDTORecord schInfo	= null;
				JDTORecord jRecord = null;
	    		schInfo = dao.getCraneSchASlabInfo(sYardId + sBayGp+YmCommonConst.EQUIP_KIND_CR+sEquipNo);

			/**
			 *	3.	기준 크레인에 할당된 스케쥴정보를 요구 크레인에 할당한다.
			 */
			 	if(schInfo != null){
			 		
			 		String sSelCraneNo 	= StringHelper.evl(schInfo.getFieldString("SCH_WORK_EQUIP_NO"), "");
			 		String sScheduleId 	= StringHelper.evl(schInfo.getFieldString("SCH_ID"), "");
			 		String sSchWorkStat	= StringHelper.evl(schInfo.getFieldString("SCH_WORK_STAT"), "");
			 		String sSchworkKind	= StringHelper.evl(schInfo.getFieldString("SCH_WORK_KIND"), "");
			 		
			 		logger.println(LogLevel.DEBUG,this, "sSelCraneNo	="	+ sSelCraneNo);
			 		logger.println(LogLevel.DEBUG,this, "sEquipNo		="	+ sEquipNo);
			 		logger.println(LogLevel.DEBUG,this, "sSchCode		="	+ sSchCode +"<같을 경우는 생략함>"+sSchworkKind);
			 		logger.println(LogLevel.DEBUG,this, "sScheduleId	="	+ sScheduleId);
			 		
			 		//UP지시일 경우만 해당됨... 스케쥴 종류일때는 생략함
		 			if(YmCommonConst.SCH_WORK_STAT_1.equals(sSchWorkStat) && !sSchCode.equals(sSchworkKind)){
		 				 /*
		 				  *	1.	UP지시 취소 전문을 송신한다.
		 				  */
		 				
		 					isSuccess = callBSlabCraneMsgInfo(sYardId + sBayGp+YmCommonConst.EQUIP_KIND_CR+sEquipNo);
							logger.println(LogLevel.DEBUG,this,"= A열연 SLAB야드  = 스케쥴 대기 전문 송신="+isSuccess);		
		 				 /*
		 				  *	2.	크레인 설비상태 idle셋팅
		 				  */
							int iReq1 = dao.updateSubCraneEquipStat(sYardId,
				    										        sBayGp,
																	YmCommonConst.EQUIP_KIND_CR,
																	sSelCraneNo,
																	YmCommonConst.WORK_PROG_STAT_W,
				    										   		"");
							logger.println(LogLevel.DEBUG,this,"= A열연 동시작업 = 크레인 설비상태 idle 셋팅="+iReq1); 		 
		 				 /*
		 				  *	3.	스케쥴 정보 초기화
		 				  */  
							int iReq2 = dao.updateCraneSchStat(sScheduleId,
				   									  	  	   YmCommonConst.SCH_WORK_STAT_S); 
				   			logger.println(LogLevel.DEBUG,this,"= A열연 동시작업 = 스케쥴 정보 초기화="+iReq2);
			 		}
			    }
			logger.println(LogLevel.DEBUG,this, "= A열연 동시작업 = CRANE 작업지시 요구 종료====");    								  	  
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
	 * B열연 SLAB야드 크레인 메뉴얼 적치요구시 수신 처리
        *
        *	- 직열작업 방법 
        * 		-	해당동 스케쥴 정보 'S','1' 인것만 가져온다.
        *		-	'1'일경우 설비,스케쥴 초기화하고, 작업취소 전문 송신한다.	
        *		-	상대에게 내려간 작업지시 부터 가져온다. 
        * 이송하차			이송하차							1001 SVMU
        *                   트레인러 하차 						1014 SVMU
        *                   ET 하차							1015 SVMU
	 * 이송상차			이송상차							1002 SVML
	 * 동내이적			동내이적							1003 SYMM,SYM2,SYM3
	 * 대차작업(#1)		동간이적상차,동간보급상차,대차하차	1004 STML,STM2,STSL,STMU,STM4
	 * 대차작업(#2)		동간이적상차,동간보급상차,대차하차	1005 STML,STM2,STSL,STMU,STM4
	 * 대차작업(#3)		동간이적상차,동간보급상차,대차하차	1006 STML,STM2,STSL,STMU,STM4
	 * W/B 보급			W/B 보급							1007 SWLI
	 * CTC보급			CTC #2~4						1008 SCLI
	 * 재열재인출		H/B LINE OFF						1009 SHLO
	 * 스카핑보급		SCARFING 보급					1010 SSLI
	 * 스카핑추출		SCARFING 추출					1011 SSLO
	 * 절단장보급		HAND SCARFING 보급				1012 SHSI
	 * 절단장추출		HAND SCARFING 추출				1013 SHSO
	    * param sYardId 		: 야드구분
        * param sBayGp 		: 동구분
        * param sEquipNo 		: 설비번호
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 	                  
	public String setBSLABCraneWorkInfo(String sYardId, 
								     String sBayGp,
								     String sSchCode,
								     String sEquipNo){
		
		String sOriginSchCd	= "";
		String sOriginSchId		= "";
			
		try{
			
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			int iSeq = 0;
		
	    		logger.println(LogLevel.DEBUG,this, "= B열연  SLAB야드 = CRANE 작업지시 요구 시작====");
	    		
	    		List schL = dao.getWorkCraneSchInfo_02(sYardId, 
					   						   sBayGp, 
					   						   sSchCode, 
					   						   sEquipNo);
			int iMaxRec     = schL.size(); 
			
			if(iMaxRec == 0){
				
				String sMessage 	= "";

				if("1001".equals(sSchCode)){
					sMessage = "이송하차";
				} else if("1014".equals(sSchCode)){
					sMessage = "이송하차(TR)";	
				} else if("1015".equals(sSchCode)){
					sMessage = "이송하차(ET)";		
				} else if("1002".equals(sSchCode)){
					sMessage = "이송상차";
				} else if("1003".equals(sSchCode)){
					sMessage = "동내이적";		
				} else if("1004".equals(sSchCode)){
					sMessage = "대차작업(#1)";
				} else if("1005".equals(sSchCode)){
					sMessage = "대차작업(#2)";	
				} else if("1006".equals(sSchCode)){
					sMessage = "대차작업(#3)";		
				} else if("1007".equals(sSchCode)){
					sMessage = "W/B 보급";		
				} else if("1008".equals(sSchCode)){
					sMessage = "CTC보급";		
				} else if("1009".equals(sSchCode)){
					sMessage = "재열재인출";	
				} else if("1010".equals(sSchCode)){
					sMessage = "스카핑보급";
				} else if("1011".equals(sSchCode)){
					sMessage = "스카핑추출";		
				} else if("1012".equals(sSchCode)){
					sMessage = "절단장보급";		
				} else if("1013".equals(sSchCode)){
					sMessage = "절단장추출";		
				} 
				sMessage += "작업이 없습니다.";
				
				sendMessageToSlabCrane(sYardId + sBayGp+YmCommonConst.EQUIP_KIND_CR+sEquipNo,sMessage);
				
				return "NOSCH";

		         } else if (iMaxRec > 0 ){
					JDTORecord selSchJr = (JDTORecord) schL.get(0);
					sOriginSchCd     	= StringHelper.evl(selSchJr.getFieldString("SCH_CODE"), "");
					sOriginSchId     	= StringHelper.evl(selSchJr.getFieldString("SCH_ID"), "");
		         }
			
			/**
			 *	1.	현재 UP지시가 내려간 작업스케쥴 정보를 가져온다.
			 */
				JDTORecord schInfo	= null;
				JDTORecord jRecord 	= null;
	    			schInfo = dao.getCraneSchASlabInfo(sYardId + sBayGp + YmCommonConst.EQUIP_KIND_CR + sEquipNo);

			/**
			 *	3.	기준 크레인에 할당된 스케쥴정보를 요구 크레인에 할당한다.
			 */
			 	if(schInfo != null){
			 		
			 		String sSelCraneNo 	= StringHelper.evl(schInfo.getFieldString("SCH_WORK_EQUIP_NO"), "");
			 		String sScheduleId 		= StringHelper.evl(schInfo.getFieldString("SCH_ID"), "");
			 		String sSchWorkStat	= StringHelper.evl(schInfo.getFieldString("SCH_WORK_STAT"), "");
			 		String sSchworkKind	= StringHelper.evl(schInfo.getFieldString("SCH_WORK_KIND"), "");
			 		
			 		logger.println(LogLevel.DEBUG,this, "sSelCraneNo	="	+ sSelCraneNo);
			 		logger.println(LogLevel.DEBUG,this, "sEquipNo		="	+ sEquipNo);
			 		logger.println(LogLevel.DEBUG,this, "sSchCode		="	+ sSchworkKind +"<같을 경우는 생략함>"+sOriginSchCd);
			 		logger.println(LogLevel.DEBUG,this, "sSchCode		="	+ sScheduleId +"<같을 경우는 생략함>"+sOriginSchId);
			 		
			 		
			 		if("1004".equals(sSchCode)||"1005".equals(sSchCode)||"1006".equals(sSchCode)||"1014".equals(sSchCode)||"1015".equals(sSchCode)){
			 			
			 			logger.println(LogLevel.DEBUG,this, "크레인 특정작업요구 => 대차작업요구="+sOriginSchCd);			 		
				 		//UP지시일 경우만 해당됨... 스케쥴 종류일때는 생략함
			 			if(YmCommonConst.SCH_WORK_STAT_1.equals(sSchWorkStat) && !sOriginSchId.equals(sScheduleId)){
			 				 /*
			 				  *	1.	UP지시 취소 전문을 송신한다.
			 				  */
			 				
			 					callBSlabCraneMsgInfo(sYardId + sBayGp+YmCommonConst.EQUIP_KIND_CR+sEquipNo);
								logger.println(LogLevel.DEBUG,this,"= B열연 SLAB야드  = 스케쥴 대기 전문 송신=");		
			 				 /*
			 				  *	2.	크레인 설비상태 idle셋팅
			 				  */
								iSeq = dao.updateSubCraneEquipStat(	sYardId,
					    										        sBayGp,
																 YmCommonConst.EQUIP_KIND_CR,
																 sSelCraneNo,
																 YmCommonConst.WORK_PROG_STAT_W,
					    										   	 "");
								logger.println(LogLevel.DEBUG,this,"=B열연  SLAB야드 = 크레인 설비상태 idle 셋팅="+iSeq); 		 
			 				 /*
			 				  *	3.	스케쥴 정보 초기화 
			 				  */  
								iSeq = dao.updateCraneSchStat(sScheduleId, 
					   									     YmCommonConst.SCH_WORK_STAT_S); 
					   			logger.println(LogLevel.DEBUG,this,"= B열연  SLAB야드 = 스케쥴 정보 초기화="+iSeq);
				 				
				 			 /*
							  *	4.	크레인 : 스케쥴 코드에 대해 긴급작업을 편성한다.
							  *		- 기존에 긴급작업 편성된 정보를 원복한다.
							  * 		- 새로 긴급작업을 편성한다. 
							  */
							 
								iSeq = dao.updateCraneSchClaer(sYardId,
						    									sBayGp,
						    									sSelCraneNo);
								logger.println(LogLevel.DEBUG,this,"=B열연  SLAB야드=크레인 긴급작업 정보 초기화="+iSeq); 
								
								String tOriginSchCd 	= "";
								String tOriginSchId   	= "";
								for(int inx = 0 ; inx < iMaxRec ; inx++ )
								{
									JDTORecord selSchJr = (JDTORecord) schL.get(inx);
									tOriginSchCd     	= StringHelper.evl(selSchJr.getFieldString("SCH_CODE"), "");
									tOriginSchId     	= StringHelper.evl(selSchJr.getFieldString("SCH_ID"), "");
									
									if(sOriginSchCd.equals(tOriginSchCd)){
										iSeq = dao.updateEmergencySchInfo_02(tOriginSchId);
										logger.println(LogLevel.DEBUG,this,"=B열연  SLAB야드=해당 스케쥴 ID 긴급작업 처리="+sOriginSchId); 				    									      
									}
								}
				 		}
			    	       }else{
			    	       
			    	       	logger.println(LogLevel.DEBUG,this, "크레인 특정작업요구 => 일반작업요구="+sSchCode);			 		
			    	       	//UP지시일 경우만 해당됨... 스케쥴 종류일때는 생략함
			 			if(YmCommonConst.SCH_WORK_STAT_1.equals(sSchWorkStat) && !sSchworkKind.equals(sOriginSchCd)){
			 				 /*
			 				  *	1.	UP지시 취소 전문을 송신한다.
			 				  */
			 				
			 					callBSlabCraneMsgInfo(sYardId + sBayGp+YmCommonConst.EQUIP_KIND_CR+sEquipNo);
								logger.println(LogLevel.DEBUG,this,"= B열연 SLAB야드  = 스케쥴 대기 전문 송신=");		
			 				 /*
			 				  *	2.	크레인 설비상태 idle셋팅
			 				  */
								iSeq = dao.updateSubCraneEquipStat(	sYardId,
					    										        sBayGp,
																 YmCommonConst.EQUIP_KIND_CR,
																 sSelCraneNo,
																 YmCommonConst.WORK_PROG_STAT_W,
					    										   	 "");
								logger.println(LogLevel.DEBUG,this,"=B열연  SLAB야드 = 크레인 설비상태 idle 셋팅="+iSeq); 		 
			 				 /*
			 				  *	3.	스케쥴 정보 초기화 
			 				  */  
								iSeq = dao.updateCraneSchStat(sScheduleId, 
					   									     YmCommonConst.SCH_WORK_STAT_S); 
					   			logger.println(LogLevel.DEBUG,this,"= B열연  SLAB야드 = 스케쥴 정보 초기화="+iSeq);
				 		
					 		 /*
							  *	4.	크레인 : 스케쥴 코드에 대해 긴급작업을 편성한다.
							  *		- 기존에 긴급작업 편성된 정보를 원복한다.
							  * 		- 새로 긴급작업을 편성한다. 
							  */
							 
								iSeq = dao.updateCraneSchClaer(sYardId,
						    									sBayGp,
						    									sSelCraneNo);
								logger.println(LogLevel.DEBUG,this,"=B열연  SLAB야드=크레인 긴급작업 정보 초기화="+iSeq); 
								
								iSeq = dao.updateEmergencySchInfo(sYardId,
					    									           sBayGp,
					    									      	    sSelCraneNo,
					    									           sOriginSchCd);
								logger.println(LogLevel.DEBUG,this,"=B열연  SLAB야드=해당 스케쥴 ID 긴급작업 처리="+iSeq); 		
			    	    		}
			    	    	}
			    }
			logger.println(LogLevel.DEBUG,this, "= B열연 동시작업 = CRANE 작업지시 요구 종료====");    								  	  
    
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return sOriginSchCd;
	}

	/**
	 * 오퍼레이션명 : 
	 *
	 * B열연 Coil야드 크레인 메뉴얼 적치요구시 수신 처리
        *
        *	- 직열작업 방법 
        * 		-	해당동 스케쥴 정보 'S','1' 인것만 가져온다.
        *		-	'1'일경우 설비,스케쥴 초기화하고, 작업취소 전문 송신한다.	
        *		-	상대에게 내려간 작업지시 부터 가져온다. 
        * param sYardId 		: 야드구분
        * param sBayGp 		: 동구분
        * param sEquipNo 		: 설비번호
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 	                       
	public boolean setBCoilCraneWorkInfo_01(String sYardId, 
										   String sBayGp,
										   String sSchCode,
										   String sEquipNo){
		
		boolean isSuccess 	= false;
		int iReq = 0;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
	    	logger.println(LogLevel.DEBUG,this, "= B열연 Coil야드 = CRANE 작업지시 요구 시작====");
			/**
			 *	1.	현재 UP지시가 내려간 작업스케쥴 정보를 가져온다.
			 */
				JDTORecord schInfo	= null;
				JDTORecord jRecord = null;
	    		schInfo = dao.getCraneSchASlabInfo(sYardId + sBayGp+YmCommonConst.EQUIP_KIND_CR+sEquipNo);

			/**
			 *	3.	기준 크레인에 할당된 스케쥴정보를 요구 크레인에 할당한다.
			 */
			 	if(schInfo != null){
			 		
			 		String sSelCraneNo 	= StringHelper.evl(schInfo.getFieldString("SCH_WORK_EQUIP_NO"), "");
			 		String sScheduleId 	= StringHelper.evl(schInfo.getFieldString("SCH_ID"), "");
			 		String sSchWorkStat	= StringHelper.evl(schInfo.getFieldString("SCH_WORK_STAT"), "");
			 		String sSchworkKind	= StringHelper.evl(schInfo.getFieldString("SCH_WORK_KIND"), "");
			 		
			 		logger.println(LogLevel.DEBUG,this, "sSelCraneNo	="	+ sSelCraneNo);
			 		logger.println(LogLevel.DEBUG,this, "sEquipNo		="	+ sEquipNo);
			 		logger.println(LogLevel.DEBUG,this, "sSchCode		="	+ sSchCode +"<같을 경우는 생략함>"+sSchworkKind);
			 		logger.println(LogLevel.DEBUG,this, "sScheduleId	="	+ sScheduleId);
			 		
			 		//UP지시일 경우만 해당됨... 스케쥴 종류일때는 생략함
		 			if(YmCommonConst.SCH_WORK_STAT_1.equals(sSchWorkStat) && !sSchCode.equals(sSchworkKind)){
		 				 /*
		 				  *	1.	UP지시 취소 전문을 송신한다.
		 				  
		 				
		 					isSuccess = callBSlabCraneMsgInfo(sYardId + sBayGp+YmCommonConst.EQUIP_KIND_CR+sEquipNo);
							logger.println(LogLevel.DEBUG,this,"= B열연 Coil야드  = 스케쥴 대기 전문 송신="+isSuccess);		
		 				*/
							
						 /*
		 				  *	2.	크레인 설비상태 idle셋팅
		 				  */
							int iReq1 = dao.updateSubCraneEquipStat(sYardId,
				    										        sBayGp,
																	YmCommonConst.EQUIP_KIND_CR,
																	sSelCraneNo,
																	YmCommonConst.WORK_PROG_STAT_W,
				    										   		"");
							logger.println(LogLevel.DEBUG,this,"= B열연 Coil야드 = 크레인 설비상태 idle 셋팅="+iReq1); 		 
		 				 /*
		 				  *	3.	스케쥴 정보 초기화
		 				  */  
							int iReq2 = dao.updateCraneSchStat(sScheduleId,
				   									  	  	   YmCommonConst.SCH_WORK_STAT_S); 
				   			logger.println(LogLevel.DEBUG,this,"= B열연 Coil야드 = 스케쥴 정보 초기화="+iReq2);
			 		}
		 			
		 			/*
					 *	3.	크레인 : 스케쥴 코드에 대해 긴급작업을 편성한다.
					 *		- 기존에 긴급작업 편성된 정보를 원복한다.
					 *		- 새로 긴급작업을 편성한다.
					 */
					 
					iReq = dao.updateCraneSchClaer(sYardId,
		    									   sBayGp,
		    									   sSelCraneNo);
					logger.println(LogLevel.DEBUG,this,"=작업요구=크레인 긴급작업 정보 초기화="+iReq); 
					
					iReq = dao.updateEmergencySchInfo(sYardId,
		    									      sBayGp,
		    									      sSelCraneNo,
		    									      sSchCode);
					logger.println(LogLevel.DEBUG,this,"=작업요구=크레인 긴급작업 정보 편성="+iReq); 
			    }
			logger.println(LogLevel.DEBUG,this, "= B열연 Coil야드 = CRANE 작업지시 요구 종료====");    								  	  
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
	 * B열연 Coil야드 크레인 메뉴얼 적치요구시 해당 Sch 찾기
        *
        *	- 직열작업 방법 
        * 		-	해당동 스케쥴 정보 'S','1' 인것만 가져온다.
        *		-	'1'일경우 설비,스케쥴 초기화하고, 작업취소 전문 송신한다.	
        *		-	상대에게 내려간 작업지시 부터 가져온다. 
        * param sYardId 		: 야드구분
        * param sBayGp 		: 동구분
        * param sEquipNo 		: 설비번호
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 	                            
	public String setBCoilCraneWorkInfo( String sYdGp, 
									  String sBayGp,
									  String sSchCode,
									  String sEquipNo){
		
		String sWprogStat 	= "";
		String sSchId 	= "";
		String sSelSch  = "";
		boolean isSuccess = false;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
	    	logger.println(LogLevel.DEBUG,this, "= B열연  Coil야드 = Sch 찾기 시작====");
			
			List schL = dao.getWorkCraneSchInfo_01( sYdGp, 
					   							    sBayGp, 
					   							    sSchCode, 
					   								sEquipNo);
			int iMaxRec     = schL.size(); 
			
			if(iMaxRec == 0){
				logger.println(LogLevel.DEBUG,this,"=작업요구=크레인 작업이 없습니다."); 

				String sSchName = "";
					
				if("0001".equals(sSchCode)){
					sSchName = "권취컨베어 인출";
				} else if("0002".equals(sSchCode)){
					sSchName = "보급";
				} else if("0003".equals(sSchCode)){
					sSchName = "대차작업(HFL)";		
				} else if("0004".equals(sSchCode)){
					sSchName = "차량작업(HFL)";
				} else if("0005".equals(sSchCode)){
					sSchName = "확장컨베어  인출";	
				} else if("0006".equals(sSchCode)){
					sSchName = "추출";		
				} else if("0007".equals(sSchCode)){
					sSchName = "대차작업(HYSCO)";		
				} else if("0008".equals(sSchCode)){
					sSchName = "차량작업(HYSCO)";		
				} else if("0009".equals(sSchCode)){
					sSchName = "수냉보급";	
				} else if("0010".equals(sSchCode)){
					sSchName = "동내이적";
				} else if("0013".equals(sSchCode)){
					sSchName = "대차출하(수냉)";		
				} else if("0014".equals(sSchCode)){
					sSchName = "대차출하(공냉)";		
				} else if("0015".equals(sSchCode)){
					sSchName = "지포장 보급";		
				} else if("0016".equals(sSchCode)){
					sSchName = "지포장 추출";		
				} else if("0017".equals(sSchCode)){
					sSchName = "대차작업(A-B)";		
				} else if("0018".equals(sSchCode)){
					sSchName = "대차작업(C-D)";		
				} else if("0019".equals(sSchCode)){
					sSchName = "대차작업(D-E)";		
				} 
				
			
				isSuccess =  callBCoilCraneMsgInfo( sYdGp+sBayGp+"CR"+sEquipNo,
								sSchName+" 작업이 없습니다.");
			
				return "";

         } else if (iMaxRec > 0 ){
			JDTORecord selSchJr = (JDTORecord) schL.get(0);
			sSelSch     = StringHelper.evl(selSchJr.getFieldString("SCH_CODE"), "");
        	 
         }
        	
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return sSelSch;
	}	
	
	/**
     * CRANE 에 지시할 전문MESSAGE를 구성한다.
     * 
     * @param sTcCode 		: 스케쥴코드
     * @param schRc 		: 스케쥴정보
     * 						  
     * @return
     * @throws  
     */	
       private boolean callCraneMsgInfo(String sTcCode,
								JDTORecord schRc){
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
		return callCraneMsgInfo(sTcCode,
							schRc,
							""); 								
	}									
	private boolean callCraneMsgInfo(String sTcCode,
								JDTORecord schRc,
								String sTcGbn){
		
		boolean isSuccess = false;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String sScheduleId = StringHelper.evl(schRc.getFieldString("SCH_ID"), "");
			
			if(YmCommonConst.TC_THCH520.equals(sTcCode)){//A열연 Coil 작업지시
				
				isSuccess = callACoilCraneMsgInfo(YmCommonConst.TC_THHC110, sScheduleId);
				
			}else if(YmCommonConst.TC_CN1PB02.equals(sTcCode)){//B열연 Coil 작업지시
				
				isSuccess = callBCoilCraneInMsgInfo(sScheduleId,sTcGbn);
				
			}else if(YmCommonConst.TC_CM1PB02.equals(sTcCode)		//B열연 Slab 작업지시
				    || YmCommonConst.TC_HM1PB02.equals(sTcCode)	//A열연 A동 Slab 작업지시(MCH)
				    || YmCommonConst.TC_HM1PB52.equals(sTcCode) ){	//A열연 B동 Slab 작업지시(MCH)
			
				isSuccess = callBSlabCraneInMsgInfo(sScheduleId,sTcGbn);
			}
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
	 * A열연 COIL CRANE에 지시할 전문MESSAGE를 구성한다.
        * 
        * param sTcCode 		: TC코드
        * param schRc 		: 스케쥴정보
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
	public boolean callACoilCraneMsgInfo(String sTcCode,
									 	 String sSchId){
		
		Boolean isSuccess = new Boolean(false);
		
		try{	
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
				if(YmCommonConst.TC_THHC110.equals(sTcCode)){
					if(sSchId.length() != 6){ //대기모드시 제외
						boolean isWork  = setCoilCraneWorkOrderInfo(sSchId);
					}
				}
				String sMessage = setCraneACoilMsgInfo(sTcCode,		
													   sSchId);
				
				EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
				isSuccess = (Boolean)ejbConn.trx("THHC110send",new Class[]{String.class},new Object[]{ sMessage });	
				
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue(); 
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	* A열연 COIL CRANE에 지시할 전문MESSAGE를 구성한다.
        * 
        * param sTcCode 		: TC코드
        * param schRc 		: 스케쥴정보
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */          
	public boolean callASlabCraneMsgInfo(String sTcCode,
									 	 String sSchId){
		
		Boolean isSuccess = new Boolean(false);
		
		try{	
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
				String sMessage = setCraneACoilMsgInfo(sTcCode,		
													   sSchId);
				
				EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
				isSuccess = (Boolean)ejbConn.trx(sTcCode+"send",new Class[]{String.class},new Object[]{ sMessage });	
				
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue(); 
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * A열연 COIL CRANE에 지시할 전문MESSAGE를 구성한다.
        * 
        * param sTcCode 		: TC코드
        * param schRc 		: 스케쥴정보
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */               
	public boolean callACoilCraneBackUpMsgInfo(String sStockId,
											   String sUpLoc,
											   String sPutLoc){
		
		Boolean isSuccess = new Boolean(false);
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
				String sMessage = setCraneACoilBackUpMsgInfo(sStockId,
															 sUpLoc,
															 sPutLoc);
				
				EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
				isSuccess = (Boolean)ejbConn.trx("THHC110send",new Class[]{String.class},new Object[]{ sMessage });	
				
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue(); 
	}

	
	/**
	 * 오퍼레이션명 : 
	 * 임가공 PIDEV
	 * A열연 COIL CRANE에 지시할 전문MESSAGE를 구성한다.
        * 
        * param sTcCode 		: TC코드
        * param schRc 		: 스케쥴정보
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */               
	public boolean callACoilCraneBackUpMsgInfoPI(String sStockId,
											   String sUpLoc,
											   String sPutLoc){
		
		Boolean isSuccess = new Boolean(false);
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
				String sMessage = setCraneACoilBackUpMsgInfoPI(sStockId,
															 sUpLoc,
															 sPutLoc);
				
				EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
				isSuccess = (Boolean)ejbConn.trx("THHC110send",new Class[]{String.class},new Object[]{ sMessage });	
				
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue(); 
	}

	
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * B열연 COIL CRANE에 지시할 전문MESSAGE를 구성한다.
        * 
        * param String 		: 스케쥴ID(작업이 있을 경우) / 크레인설비번호(대기모드시)
        * param String 		: 메세지
        * param String 		: 사용자 지정 TO위치
        * param String 		: 작업구분(I:시스템, R:크레인요구)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */               
	public boolean callBCoilCraneInMsgInfo(String sSchId,
									  String sTcGbn){
		
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		return callBCoilCraneMsgInfo(sSchId,"","",sTcGbn);
	}
	/**
	 * 오퍼레이션명 : 
	 *
	 * B열연 COIL CRANE에 지시할 전문MESSAGE를 구성한다.
        * 
        * param String 		: 스케쥴ID(작업이 있을 경우) / 크레인설비번호(대기모드시)
        * param String 		: 메세지
        * param String 		: 사용자 지정 TO위치
        * param String 		: 작업구분(I:시스템, R:크레인요구)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */               
	public boolean callBCoilCraneMsgInfo(String sSchId){
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		return callBCoilCraneMsgInfo(sSchId,"","","");
	}
	/**
	 * 오퍼레이션명 : 
	 *
	 * B열연 COIL CRANE에 지시할 전문MESSAGE를 구성한다.
        * 
        * param String 		: 스케쥴ID(작업이 있을 경우) / 크레인설비번호(대기모드시)
        * param String 		: 메세지
        * param String 		: 사용자 지정 TO위치
        * param String 		: 작업구분(I:시스템, R:크레인요구)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */               
	public boolean callBCoilCraneMsgInfo(String sSchId,
									String sMsg){
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		return callBCoilCraneMsgInfo(sSchId,sMsg,"","");
	}
	/**
	 * 오퍼레이션명 : 
	 *
	 * B열연 COIL CRANE에 지시할 전문MESSAGE를 구성한다.
        * 
        * param String 		: 스케쥴ID(작업이 있을 경우) / 크레인설비번호(대기모드시)
        * param String 		: 메세지
        * param String 		: 사용자 지정 TO위치
        * param String 		: 작업구분(I:시스템, R:크레인요구)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */               
	public boolean callBCoilCraneMsgInfo(String sSchId,
									String sMsg,
									String sPutLoc){
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		return callBCoilCraneMsgInfo(sSchId,
								sMsg,
								sPutLoc,
								"");								
	}					
	/**
	 * 오퍼레이션명 : 
	 *
	 * B열연 COIL CRANE에 지시할 전문MESSAGE를 구성한다.
        * 
        * param String 		: 스케쥴ID(작업이 있을 경우) / 크레인설비번호(대기모드시)
        * param String 		: 메세지
        * param String 		: 사용자 지정 TO위치
        * param String 		: 작업구분(I:시스템, R:크레인요구)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */               					 	
	public boolean callBCoilCraneMsgInfo(String sSchId,
									String sMsg,
									String sPutLoc,
									String sTcGbn){
		
		Boolean isSuccess = new Boolean(false);
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
				/**
				 * 재작업지시일 경우 현재 위치 삭제 및 수정 위치 셋팅
				 */
				if(!"".equals(sPutLoc)){
					//isSuccess = (Boolean)setCraneBCoilLocInfo(sSchId,sPutLoc);
					//- 이슈번호(11725)
					 EJBConnector ejbConn1 = new EJBConnector("default","JNDICWrkOrdReg",this);
					 isSuccess = (Boolean)ejbConn1.trx("setCraneBCoilLocInfo",new Class[]{String.class, 
				    																		String.class},
				    														   new Object[]{sSchId,
							 																sPutLoc});
				    	
					 if(isSuccess.booleanValue() == false){
						 return isSuccess.booleanValue(); 
					 }
				} 
				
				/**
				 * 작업지시 호출
				 */
				if("".equals(sMsg)){	// 수정작업지시 제외
					if(sSchId.length() != 6){ //대기모드시 제외
						boolean isWork  = setCoilCraneWorkOrderInfo(sSchId);
					}
				}
				String sMessage = setCraneBCoilMsgInfo(sSchId,
												    sMsg,
												    sPutLoc,
												    sTcGbn);
				
				EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
				isSuccess = (Boolean)ejbConn.trx("CN1BP01send",new Class[]{String.class},new Object[]{ sMessage });	
				
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue(); 
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * B열연 SLAB CRANE에 지시할 전문MESSAGE를 구성한다.
        * 
        * param String 		: 스케쥴ID(작업이 있을 경우) / 크레인설비번호(대기모드시)
        * param String 		: 메세지
        * param String 		: 사용자 지정 TO위치
        * param String 		: 작업구분(I:시스템, R:크레인요구)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                    
	public boolean callBSlabCraneInMsgInfo(String sSchId,
									   String sTcGbn){
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		return callBSlabCraneMsgInfo(sSchId,"","",sTcGbn);
	}
	/**
	 * 오퍼레이션명 : 
	 *
	 * B열연 SLAB CRANE에 지시할 전문MESSAGE를 구성한다.
        * 
        * param String 		: 스케쥴ID(작업이 있을 경우) / 크레인설비번호(대기모드시)
        * param String 		: 메세지
        * param String 		: 사용자 지정 TO위치
        * param String 		: 작업구분(I:시스템, R:크레인요구)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */        		
	public boolean callBSlabCraneMsgInfo(String sSchId){
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		return callBSlabCraneMsgInfo(sSchId,"","","");
	}
	/**
	 * 오퍼레이션명 : 
	 *
	 * B열연 SLAB CRANE에 지시할 전문MESSAGE를 구성한다.
        * 
        * param String 		: 스케쥴ID(작업이 있을 경우) / 크레인설비번호(대기모드시)
        * param String 		: 메세지
        * param String 		: 사용자 지정 TO위치
        * param String 		: 작업구분(I:시스템, R:크레인요구)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */        	
	public boolean callBSlabCraneMsgInfo(String sSchId,
									String sMsg){
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		return callBSlabCraneMsgInfo(sSchId,sMsg,"","");
	}
	/**
	 * 오퍼레이션명 : 
	 *
	 * B열연 SLAB CRANE에 지시할 전문MESSAGE를 구성한다.
        * 
        * param String 		: 스케쥴ID(작업이 있을 경우) / 크레인설비번호(대기모드시)
        * param String 		: 메세지
        * param String 		: 사용자 지정 TO위치
        * param String 		: 작업구분(I:시스템, R:크레인요구)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */        	
	public boolean callBSlabCraneMsgInfo(String sSchId,
									String sMsg,
									String sPutLoc){
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		return callBSlabCraneMsgInfo(sSchId,
								 sMsg,
								 sPutLoc,
								 ""); 									
	}			
	/**
	 * 오퍼레이션명 : 
	 *
	 * B열연 SLAB CRANE에 지시할 전문MESSAGE를 구성한다.
        * 
        * param String 		: 스케쥴ID(작업이 있을 경우) / 크레인설비번호(대기모드시)
        * param String 		: 메세지
        * param String 		: 사용자 지정 TO위치
        * param String 		: 작업구분(I:시스템, R:크레인요구)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */        							
	public boolean callBSlabCraneMsgInfo(String sSchId,
									String sMsg,
									String sPutLoc,
									String sTcGbn){
		
		Boolean isSuccess = new Boolean(false);
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
				JDTORecord jRecord = null;
				/**
				 * 재작업지시일 경우 현재 위치 삭제 및 수정 위치 셋팅
				 */
				if(!"".equals(sPutLoc)){
					boolean isLoc = setCraneBSlabLocInfo(sSchId,sPutLoc);
				} 
				
				/**
				 * 작업지시 호출
				 */
				if("".equals(sMsg)){	// 수정작업지시 제외
					if(sSchId.length() != 6){ //대기모드시 제외
						boolean isWork  = setSlabCraneWorkOrderInfo(sSchId);
						if(!isWork) return false;
						boolean isGrap  = setCraneBSlabGrapInfo(sSchId);
					}
				}
				
				
				/*
				 *	-	선 W/B 보급 작업지시 처리	
				 *	-	W/B 보급 대상 크레인 정보 가져오기
				 */
				String sMainCrane = "";
				JDTORecord craneV = dao.getCoilCraneInfo(YmCommonConst.YD_GP_2,
					 									YmCommonConst.BAY_GP_C,
					 									YmCommonConst.NEW_SCH_WORK_KIND_SWLI);
				if(craneV != null){
					sMainCrane	= StringHelper.evl(craneV.getFieldString("SELECT_CRANE_NO"),"");
				}
				
				sMainCrane = 	YmCommonConst.YD_GP_2		+
						   	YmCommonConst.BAY_GP_C		+
						   	YmCommonConst.EQUIP_KIND_CR	+
						   	sMainCrane;
				
				
			
				
				/*
				 *	-	선 A동 CTC 보급 작업지시 처리	
				 *	-	CTC 보급 대상 크레인 정보 가져오기
				 */
				String sCtcMainCrane = "";
				JDTORecord ctcCraneV = dao.getCoilCraneInfo(YmCommonConst.YD_GP_2,
					 								     YmCommonConst.BAY_GP_A,
					 								     YmCommonConst.NEW_SCH_WORK_KIND_SCLI);
				if(ctcCraneV != null){
					sCtcMainCrane	= StringHelper.evl(ctcCraneV.getFieldString("SELECT_CRANE_NO"),"");
				}
				
				sCtcMainCrane = 	YmCommonConst.YD_GP_2		+
						   		YmCommonConst.BAY_GP_A		+
						   		YmCommonConst.EQUIP_KIND_CR	+
						   		sCtcMainCrane;
				
				/*
				 *	-	선 B동 CTC 보급 작업지시 처리	
				 *	-	CTC 보급 대상 크레인 정보 가져오기
				 */
				String sBCtcMainCrane = "";
				JDTORecord BctcCraneV = dao.getCoilCraneInfo(YmCommonConst.YD_GP_2,
					 								     YmCommonConst.BAY_GP_B,
					 								     YmCommonConst.NEW_SCH_WORK_KIND_SCLI);
				if(BctcCraneV != null){
					sBCtcMainCrane	= StringHelper.evl(BctcCraneV.getFieldString("SELECT_CRANE_NO"),"");
				}
				
				sBCtcMainCrane = 	YmCommonConst.YD_GP_2		+
						   		YmCommonConst.BAY_GP_B		+
						   		YmCommonConst.EQUIP_KIND_CR	+
						   		sBCtcMainCrane;
				
				
				logger.println(LogLevel.DEBUG,this, "=대기 작업지시= "+ sSchId);
						   	
				if(sMainCrane.equals(sSchId) ) {
					
					String sQuery	= "ym.steelinfo.steelinforecv.YdStockDAO.selectWBSlabSearch_03";
					List wbL 		= dao.getListData(sQuery, new Object[]{	YmCommonConst.SEARCH_C_BAY_GP,
																		   	YmCommonConst.SEARCH_C_BAY_GP,
																		   	YmCommonConst.SEARCH_TC_C_BAY_GP,
																		   	YmCommonConst.SEARCH_TC_C_BAY_GP });
		    			/* 
					 *	W/B보급 주크레인의 W/B보급 선작업지시 실행여부
					 *	TB_YM_EQUIP(2CWB01)  WORK_MODE 체크 
					 */
					String sQuery1	= "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.SelectWBHMISTAT";
					JDTORecord wbJr = dao.getData(sQuery1, new Object[]{ YmCommonConst.STACK_COL_GP_2CWB01 });
					
					boolean isExecute 	= false;
					String sHmiStat  	= "";
					
					if (wbJr != null){ 
						sHmiStat  = StringHelper.evl(wbJr.getFieldString("WORK_MODE"), "");
					}
					
					if ("C".equals(sHmiStat)){
						isExecute = true;
					}
						
					if (wbL == null	|| wbL.size()	== 0){	
						isExecute = true;
					}
					
			    		if (isExecute){
						
						logger.println(LogLevel.DEBUG,this, "=W/B보급 대기 작업지시= ");
						
						jRecord = setCraneBSlabMsgInfo(sSchId,sMsg,sPutLoc,sTcGbn);
						
						String methodName = StringHelper.evl(jRecord.getFieldString("TC_CD"), YmCommonConst.TC_CM1BP01);
						String sMessage   = jRecord.getFieldString("sMessage");
						
						logger.println(LogLevel.DEBUG,this, "methodName"+methodName);
						EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
						isSuccess = (Boolean)ejbConn.trx(methodName+"send",new Class[]{String.class},new Object[]{ sMessage });	
						
					}else{
						
						logger.println(LogLevel.DEBUG,this, "=W/B보급  선 작업지시= ");
						
						EJBConnector ejbConn = new EJBConnector("default","JNDIRolletTableStatusReg",this);
						isSuccess = (Boolean)ejbConn.trx("callPreviousWBWork",new Class[]{String.class},new Object[]{ "YJK" });	
						
					}	
				}else if(sCtcMainCrane.equals(sSchId) ) {
					
					String sQuery	= "ym.steelinfo.steelinforecv.YdStockDAO.selectWBSlabSearch_09";
					List wbL 		= dao.getListData(sQuery, new Object[]{ "2A%",
	 															   "2A%",
	 															   "2A%",
	 															   "2A%",
																   YmCommonConst.SEARCH_TC_A_BAY_GP,
																   YmCommonConst.SEARCH_TC_A_BAY_GP,
																   YmCommonConst.SEARCH_TC_A_BAY_GP,
																   YmCommonConst.SEARCH_TC_A_BAY_GP});
		    			
		    			/* 
					 *	CTC보급 주크레인의 CTC보급 선작업지시 실행여부
					 *	TB_YM_EQUIP(2ACT02)  WORK_MODE 체크 
					 */
					String sQuery1	= "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.SelectWBHMISTAT";
					JDTORecord wbJr = dao.getData(sQuery1, new Object[]{ YmCommonConst.STACK_COL_GP_2ACT02 });
					
					boolean isExecute 	= false;
					String sHmiStat  	= "";
					
					if (wbJr != null){ 
						sHmiStat  = StringHelper.evl(wbJr.getFieldString("WORK_MODE"), "");
					}
					
					if ("C".equals(sHmiStat)){
						isExecute = true;
					}
						
					if (wbL == null	|| wbL.size()	== 0){	
						isExecute = true;
					}
					
			    		if (isExecute){
						
						logger.println(LogLevel.DEBUG,this, "=CTC보급 대기 작업지시= ");
						
						jRecord = setCraneBSlabMsgInfo(sSchId,sMsg,sPutLoc,sTcGbn);
						
						String methodName = StringHelper.evl(jRecord.getFieldString("TC_CD"), YmCommonConst.TC_CM1BP01);
						String sMessage   = jRecord.getFieldString("sMessage");
						
						logger.println(LogLevel.DEBUG,this, "methodName"+methodName);
						EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
						isSuccess = (Boolean)ejbConn.trx(methodName+"send",new Class[]{String.class},new Object[]{ sMessage });	
						
					}else{
						
						logger.println(LogLevel.DEBUG,this, "=CTC보급  선 작업지시= ");
						
						EJBConnector ejbConn = new EJBConnector("default","JNDISRTSpplyWrkOrdReg",this);
						isSuccess = (Boolean)ejbConn.trx("callPreviousCTCWork",new Class[]{String.class},new Object[]{ "YJK" });	
						
					}	
				}else if(sBCtcMainCrane.equals(sSchId) ) {
					
					String sQuery	= "ym.steelinfo.steelinforecv.YdStockDAO.selectWBSlabSearch_09";
					List wbL 		= dao.getListData(sQuery, new Object[]{ "2B%",
	 															   "2B%",
	 															   "2B%",
	 															   "2B%",
																   YmCommonConst.SEARCH_TC_B_BAY_GP,
																   YmCommonConst.SEARCH_TC_B_BAY_GP,
																   YmCommonConst.SEARCH_TC_B_BAY_GP,
																   YmCommonConst.SEARCH_TC_B_BAY_GP});
		    			
		    			/* 
					 *	CTC보급 주크레인의 CTC보급 선작업지시 실행여부
					 *	TB_YM_EQUIP(2BCT02)  WORK_MODE 체크 
					 */
					String sQuery1	= "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.SelectWBHMISTAT";
					JDTORecord wbJr = dao.getData(sQuery1, new Object[]{ YmCommonConst.STACK_COL_GP_2BCT03 });
					
					boolean isExecute 	= false;
					String sHmiStat  	= "";
					
					if (wbJr != null){ 
						sHmiStat  = StringHelper.evl(wbJr.getFieldString("WORK_MODE"), "");
					}
					
					if ("C".equals(sHmiStat)){
						isExecute = true;
					}
						
					if (wbL == null	|| wbL.size()	== 0){	
						isExecute = true;
					}
					
			    		if (isExecute){
						
						logger.println(LogLevel.DEBUG,this, "=CTC보급 대기 작업지시= ");
						
						jRecord = setCraneBSlabMsgInfo(sSchId,sMsg,sPutLoc,sTcGbn);
						
						String methodName = StringHelper.evl(jRecord.getFieldString("TC_CD"), YmCommonConst.TC_CM1BP01);
						String sMessage   = jRecord.getFieldString("sMessage");
						
						logger.println(LogLevel.DEBUG,this, "methodName"+methodName);
						EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
						isSuccess = (Boolean)ejbConn.trx(methodName+"send",new Class[]{String.class},new Object[]{ sMessage });	
						
					}else{
						
						logger.println(LogLevel.DEBUG,this, "=CTC보급  선 작업지시= ");
						
						EJBConnector ejbConn = new EJBConnector("default","JNDISRTSpplyWrkOrdReg",this);
						isSuccess = (Boolean)ejbConn.trx("callPreviousCTCWork1",new Class[]{String.class},new Object[]{ "YJK" });							
					}	
				}else{
					
					logger.println(LogLevel.DEBUG,this, "=일반적 슬라브 작업지시= ");
					
					jRecord = setCraneBSlabMsgInfo(sSchId,sMsg,sPutLoc,sTcGbn);
					
					String methodName = StringHelper.evl(jRecord.getFieldString("TC_CD"), YmCommonConst.TC_CM1BP01);
					String sMessage   = jRecord.getFieldString("sMessage");
					
					logger.println(LogLevel.DEBUG,this, "methodName"+methodName);
					EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
					isSuccess = (Boolean)ejbConn.trx(methodName+"send",new Class[]{String.class},new Object[]{ sMessage });	
				}
				
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue(); 
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 재작업지시일 경우 현재 위치 삭제 및 수정 위치 셋팅
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */        	 
	public boolean setCraneBCoilLocInfo(String sSchId, String sNewPutLoc){
		
		boolean isSuccess = false;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			/*
			 *	0.	스케쥴 정보 검색	
			 */
				JDTORecord schInfo  = dao.getCraneSchInfo(sSchId);
			    logger.println(LogLevel.DEBUG,this, "sSchId=" + sSchId);
		    	if(schInfo == null){
		    		logger.println(LogLevel.DEBUG,this, "작업지시 전문발생 스케쥴 정보가 존재안함.");
		    		return isSuccess;
		    	}
		    	String sOldPutLoc = StringHelper.evl(schInfo.getFieldString("CRANE_WORD_PUT_LOC"),"");
		    	String sStockId   = StringHelper.evl(schInfo.getFieldString("STOCK_ID"),"");
		    	
	
				
				/*
				 * 지시PUT위치 코일존재 유무 체크 - 이슈번호(11725)/////////////////////////////////////
				 */
		    	String sPutStackColGp    = "";
				String sPutStackBedGp    = "";
				String sPutStackLayerGp  = "";
				String sPutUsageCd 		= "";
				String sStock_id		= "";
				sPutStackColGp   = sNewPutLoc.substring(0, 6);
				sPutStackBedGp   = sNewPutLoc.substring(6, 8);
				sPutStackLayerGp = sNewPutLoc.substring(8,10);
				
				
				sStock_id = YmCommonUtil.getStackColInfoCoilPk(sPutStackColGp,sPutStackBedGp,sPutStackLayerGp );
		    	
				if(!"".equals(sStock_id)){
					logger.println(LogLevel.DEBUG,this, "권하위치에 코일이 존재 함:stock_id:"+sStock_id);
		    		return isSuccess;
				}
				////////////////////////////////////////////////////////////////////////////////
				
				
			/*
			 *	1.	스케쥴 PUT위치 정보 수정	
			 */
				int iSeq1 = dao.updatePutLocInfoWithSchId(sSchId,
    								     				  sNewPutLoc);
			/*
			 *	2.	현재위치 CLEAR
			 */
			 	int iSeq	= 0;
			 	
			 	String sUpStackColGp    = "";
				String sUpStackBedGp    = "";
				String sUpStackLayerGp  = "";
				String sUpUsageCd 		= "";
				
				sUpStackColGp   = sOldPutLoc.substring(0, 6);
				sUpStackBedGp   = sOldPutLoc.substring(6, 8);
				sUpStackLayerGp = sOldPutLoc.substring(8,10);
			
				
				sUpUsageCd 		= YmCommonUtil.getStackColInfoWithPk(sUpStackColGp);
		   		
		   		if(YmCommonConst.STACK_COL_USAGE_CD_XX.equals(sUpUsageCd)	||// COIL 비상적치위치
			       YmCommonConst.STACK_COL_USAGE_CD_FE.equals(sUpUsageCd)	||// COIL HFL보급위치
				   YmCommonConst.STACK_COL_USAGE_CD_FI.equals(sUpUsageCd)	||// COIL HFLTAKEIN위치
				   YmCommonConst.STACK_COL_USAGE_CD_FD.equals(sUpUsageCd)	||// COIL HFL추출위치
				   YmCommonConst.STACK_COL_USAGE_CD_KE.equals(sUpUsageCd)	||// COIL SPM보급위치
				   YmCommonConst.STACK_COL_USAGE_CD_KI.equals(sUpUsageCd)	||// COIL SPMTAKEIN위치
				   YmCommonConst.STACK_COL_USAGE_CD_KD.equals(sUpUsageCd)	){// COIL SPM추출위치
				   	
					iSeq = YmCommonDB.deleteConveyorInfo(sUpStackColGp,
									  			 	 	 sStockId);
				    if(iSeq < 0){
						logger.println(LogLevel.DEBUG,this, "재작업지시=> 적치단 삭제 FAIL");
					}	
					
				}else{	
				
			   		/* 
					 * 적치단 UP위치 Clear
					 * tb_ym_stacklayer Table : stock_id 		 = ''(Empty)
					 * tb_ym_stacklayer Table : stack_layer_stat = 'E'(적치가능)
					 */	
			    	iSeq = dao.updateCraneStackLayerStat(sUpStackColGp,
			    										 sUpStackBedGp,
			    										 sUpStackLayerGp,
			    										 "",
			    										 YmCommonConst.STACK_LAYER_STAT_E);
			        
					/**
					 *	FROM 위치 상단 적치상태 수정
					 */
				 	if(YmCommonConst.STACK_LAYER_GP_01.equals(sUpStackLayerGp)){
				    	
				    	/*
				    	 * A.B열연 Coil 권상실적	
				    	 * 상단 왼쪽 상태정보를 UPDATE
				    	 * 상단 오른쪽 상태정보를 UPDATE
				    	 */	
				    	iSeq = YmCommonDB.setCoilUpperState_V(sUpStackColGp,
					    							 	   	  sUpStackBedGp,
					    							 	   	  sUpStackLayerGp);
		    		}	
		    	}
			/*
			 *	3.	수정위치 셋팅
			 */ 
			 	
			 	
				
				sPutUsageCd = YmCommonUtil.getStackColInfoWithPk(sPutStackColGp);
		   		
		   		if(YmCommonConst.STACK_COL_USAGE_CD_XX.equals(sPutUsageCd)	||// COIL 비상적치위치	
				   YmCommonConst.STACK_COL_USAGE_CD_FE.equals(sPutUsageCd)	||// COIL HFL보급위치
				   YmCommonConst.STACK_COL_USAGE_CD_FI.equals(sPutUsageCd)	||// COIL HFLTAKEIN위치
				   YmCommonConst.STACK_COL_USAGE_CD_FD.equals(sPutUsageCd)	||// COIL HFL추출위치
				   YmCommonConst.STACK_COL_USAGE_CD_KE.equals(sPutUsageCd)	||// COIL SPM보급위치
				   YmCommonConst.STACK_COL_USAGE_CD_KI.equals(sPutUsageCd)	||// COIL SPMTAKEIN위치
				   YmCommonConst.STACK_COL_USAGE_CD_KD.equals(sPutUsageCd)	){// COIL SPM추출위치
				   	
					iSeq = YmCommonDB.insertConveyorInfo(sPutStackColGp,
													     sStockId,
														 sPutStackBedGp);
				    if(iSeq < 0){
						logger.println(LogLevel.DEBUG,this, "재작업지시=> 적치단 생성 FAIL");
					}	
					
				}else{	
					
					/* 
					 * 적치단 Put위치에 다른 코일이 있을 경우.
					 * 해당동의 XX번지로 저장품 MAP을 수정한다.
					 */	
					iSeq = YmCommonDB.updateLegacyStockId_Coil(dao,
														  	   sPutStackColGp,
														  	   sPutStackBedGp,
														  	   sPutStackLayerGp,
														  	   sStockId);
					
					if(iSeq < 1){
						logger.println(LogLevel.DEBUG,this, "****권하처리 위치에 실물or예정코일이 존재 합니다. 확인 요망");
						return isSuccess;
					}
					
														  	   
//					Crane 작업 실적 등록
//					String sTempLayer = sPutStackColGp.substring(0,2)+
//										YmCommonConst.STACK_COL_USAGE_CD_XX+
//										YmCommonConst.STACK_BED_GP_01;
//					
//					JDTORecord putJr = dao.getStackLayerInfoWithPk(sPutStackColGp,
//																	sPutStackBedGp,
//																	sPutStackLayerGp);
//
//					String sToStockId 	= "00";
//					String sToStat 		= "";
//					
//					if(putJr != null){
//					
//					sToStockId 	= StringHelper.evl(putJr.getFieldString("STOCK_ID"), "");
//					sToStat 	= StringHelper.evl(putJr.getFieldString("STACK_LAYER_STAT"), "");
//					}
//					
//					if(!"".equals(sToStockId)&&
//					   !sStockId.equals(sToStockId)&&
//					   !sPutStackColGp.substring(2, 4).equals("TR")		   
//					   ){ 
//					EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
//		    	 	ejbConn.trx("insertUpPutWrslRtData",new  Class[]{String.class,String.class,String.class,String.class,String.class,String.class},
//																new Object[]{sToStockId,sPutStackColGp,sTempLayer,"CYMM", sPutStackColGp.substring(0,1),"userym"});
//					}
//					
//					
//					//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//		            //코일제품이적작업실적
//		 			JDTORecord tcRecordDM = null;
//		 			tcRecordDM = JDTORecordFactory.getInstance().create(); 
//		 			tcRecordDM.setField("GOODS_NO",sToStockId);
//		 			tcRecordDM.setField("BEFO_STORE_LOC",sPutStackColGp+sPutStackBedGp+sPutStackLayerGp);
//		 			tcRecordDM.setField("TO_STORE_LOC",sTempLayer+"0101");
//		 			
//		 			//인터페이스 전문 호출
//		 			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
//		 			ejbConn.trx("getYDDMR004",new Class[]{JDTORecord.class},
//		 			  	  	 new Object[]{tcRecordDM}); 
//		            logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 코일제품이적작업실적XX2.===");
//		            //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		            
		    	 	
					/* 
					 * 적치단 Put위치를 적치상태로 변경
					 * tb_ym_stacklayer Table : stock_id = Coil No
					 * tb_ym_stacklayer Table : stack_layer_stat	   = 'P'(적치중)
					 */	
			    	iSeq = dao.updateCraneStackLayerStat(sPutStackColGp,
			    										 sPutStackBedGp,
			    										 sPutStackLayerGp,
			    										 sStockId,
			    										 YmCommonConst.STACK_LAYER_STAT_P);
		    		/*
			    	 * 적치단이 '01'단일 경우
					 * 적치단상태가 'L', 'P' 이면 상단 적치단 정보를 적치가능상태로 변경
					 */	
			    	if(YmCommonConst.STACK_LAYER_GP_01.equals(sPutStackLayerGp)){
			    		
			    		/*
				    	 * A.B열연 Coil 권하실적	
				    	 * 상단 왼쪽 상태정보를 UPDATE
				    	 * 상단 오른쪽 상태정보를 UPDATE
				    	 */	
				    	iSeq = YmCommonDB.setCoilUpperState_E(sPutStackColGp,
				    							 	   		  sPutStackBedGp,
				    							 	   		  sPutStackLayerGp);
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
	 * 재작업지시일 경우 현재 위치 삭제 및 수정 위치 셋팅
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */        	 	 
	public boolean setCraneBSlabLocInfo(String sSchId,
										String sNewPutLoc){
		
		boolean isSuccess 	= false;
		String sSysPutLoc 		= "";
		String sMaxLayerGp 	= "";
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
				logger.println(LogLevel.DEBUG,this, "작업재지시=>START.");		
			/*
			 *	★	PUT 위치 정보가 8자리이면 해당 BED의 MAX번지로 단정보를 셋팅한다.
			 */
			 
				if(sNewPutLoc.length() == 8)
				{
					JDTORecord maxInfo  = dao.getAbledMaxLayerInfo(sNewPutLoc.substring(0, 6),
								  							  sNewPutLoc.substring(6, 8));
					
					if(maxInfo != null){
				    		sMaxLayerGp	= StringHelper.evl(maxInfo.getFieldString("MAX_LAYER_GP"), "");
				    	}
				    	
				    	logger.println(LogLevel.DEBUG,this, "작업재지시=>MAX 번지정보="+sMaxLayerGp);		
				    	
					if("".equals(sMaxLayerGp) ||		//  적치단 정보가 없을 경우
					"13".equals(sMaxLayerGp)) {		//  적치단 정보가 13단 이상일 경우
						throw new Exception("적치단 정보가 올바르지 않습니다.");
					}
			            
				    	sSysPutLoc = sNewPutLoc + sMaxLayerGp;
				}else{
					sSysPutLoc = sNewPutLoc;
				}
				
				logger.println(LogLevel.DEBUG,this, "작업재지시=>10자리 번지정보="+sSysPutLoc);		
				
			/*
			 *	0.	스케쥴 정보 검색	
			 */
				JDTORecord schInfo  = dao.getCraneSchInfo(sSchId);
			    	logger.println(LogLevel.DEBUG,this, "sSchId=" + sSchId);
			    	if(schInfo == null){
			    		logger.println(LogLevel.DEBUG,this, "작업지시 전문발생 스케쥴 정보가 존재안함.");
			    		return isSuccess;
			    	}
			    	String sOldPutLoc 	= StringHelper.evl(schInfo.getFieldString("CRANE_WORD_PUT_LOC"),"");
			    	String sStockId   	= StringHelper.evl(schInfo.getFieldString("STOCK_ID"),"");
				String sYdGp 		= StringHelper.evl(schInfo.getFieldString("YD_GP"),"");
				String sBayGp 	= StringHelper.evl(schInfo.getFieldString("BAY_GP"),"");
				String sEquipKind	= YmCommonConst.EQUIP_KIND_CR;
				String sEquipNo 	= StringHelper.evl(schInfo.getFieldString("SCH_WORK_EQUIP_NO"),"");
				String sSchCode 	= StringHelper.evl(schInfo.getFieldString("SCH_WORK_KIND"),"");
				
				String sSchId1 	= sSchId;
				String sSlabNo1 	= sStockId;
				String sOPutLoc1	= sOldPutLoc;
				String sNPutLoc1	= sSysPutLoc;
				String sSchId2 	= "";
			    	String sSlabNo2 	= "";
			    	String sOPutLoc2	= "";
				String sNPutLoc2	= "";
		
			    	JDTORecord stockV = dao.getSlabGripInfo_02(sYdGp,
								    					   sBayGp,
								    					   sEquipKind,
								    					   sEquipNo,
								    					   sSlabNo1,
													   sSchCode);
			    	if(stockV != null){
			    		
			    		if(sNewPutLoc.length() == 8)
					{
						if("".equals(sMaxLayerGp) ||		//  적치단 정보가 없을 경우
						"12".equals(sMaxLayerGp)) {		//  적치단 정보가 12단 이상일 경우
							throw new Exception("적치단 정보가 올바르지 않습니다.");
						}
						
						sNPutLoc1 = 	sNPutLoc1.substring(0, 6)+
								  	sNPutLoc1.substring(6, 8)+
								  	YmCommonUtil.changeLayerFormat(sNPutLoc1.substring(8,10), "P");
					}
					
			    		sSchId2     	= StringHelper.evl(stockV.getFieldString("SCH_ID"), "");
			    		sSlabNo2    	= StringHelper.evl(stockV.getFieldString("STOCK_ID"), "");
			    		sOPutLoc2	= sOPutLoc1.substring(0, 6)+
								  sOPutLoc1.substring(6, 8)+
								  YmCommonUtil.changeLayerFormat(sOPutLoc1.substring(8,10), "M");
					sNPutLoc2	= sNPutLoc1.substring(0, 6)+
								  sNPutLoc1.substring(6, 8)+
								  YmCommonUtil.changeLayerFormat(sNPutLoc1.substring(8,10), "M");
			    	}
			    	
			    	logger.println(LogLevel.DEBUG,this, "작업재지시=>sSchId1		="+sSchId1);	
				logger.println(LogLevel.DEBUG,this, "작업재지시=>sSlabNo1	="+sSlabNo1);	
				logger.println(LogLevel.DEBUG,this, "작업재지시=>sOPutLoc1	="+sOPutLoc1);	
				logger.println(LogLevel.DEBUG,this, "작업재지시=>sNPutLoc1	="+sNPutLoc1);	
				logger.println(LogLevel.DEBUG,this, "작업재지시=>sSchId2		="+sSchId2);	
				logger.println(LogLevel.DEBUG,this, "작업재지시=>sSlabNo2	="+sSlabNo2);	
				logger.println(LogLevel.DEBUG,this, "작업재지시=>sOPutLoc2	="+sOPutLoc2);	
				logger.println(LogLevel.DEBUG,this, "작업재지시=>sNPutLoc2	="+sNPutLoc2);	
				
			/*
			 *	1.	스케쥴 PUT위치 정보 수정	
			 */
				int iSeq1 = dao.updatePutLocInfoWithSchId(sSchId1,
    								     				  sNPutLoc1);
				if(!"".equals(sSchId2)){
					int iSeq2 = dao.updatePutLocInfoWithSchId(sSchId2,
	    								     				  sNPutLoc2);    								     				  
				}
			/*
			 *	2.	현재위치 CLEAR
			 */
				int iSeq	= 0;
			 	
			 	String sUpStackColGp    = "";
				String sUpStackBedGp    = "";
				String sUpStackLayerGp  = "";
				String sUpUsageCd 		= "";
				
				sUpStackColGp   = sOPutLoc1.substring(0, 6);
				sUpStackBedGp   = sOPutLoc1.substring(6, 8);
				sUpStackLayerGp = sOPutLoc1.substring(8,10);
				
				sUpUsageCd 		= YmCommonUtil.getStackColInfoWithPk(sUpStackColGp);
		   		
		   		if(YmCommonConst.STACK_COL_USAGE_CD_XX.equals(sUpUsageCd)	||// SLAB 비상적치위치
			       YmCommonConst.STACK_COL_USAGE_CD_SE.equals(sUpUsageCd)	||// SLAB Scafing 입측
				   YmCommonConst.STACK_COL_USAGE_CD_SD.equals(sUpUsageCd)	){// SLAB Scafing 출측
				   	
					iSeq = YmCommonDB.deleteConveyorInfo(sUpStackColGp,
					   		  			 	 		     sSlabNo1);
				    if(iSeq < 0){
						logger.println(LogLevel.DEBUG,this, "재작업지시=> 적치단 삭제 FAIL");
					}	
					
				}else{	
					/* 
					 * 적치단 UP위치 Clear
					 * tb_ym_stacklayer Table : stock_id 		 = ''(Empty)
					 * tb_ym_stacklayer Table : stack_layer_stat = 'E'(적치가능)
					 */	
			    	iSeq = dao.updateCraneStackLayerStat(sUpStackColGp,
			    										 sUpStackBedGp,
			    										 sUpStackLayerGp,
			    										 "",
			    										 YmCommonConst.STACK_LAYER_STAT_E);
			        
					/*
			    	 * B열연 Slab 	
			    	 * 바로 위 상단 상태정보를 UPDATE
			    	 */
			    	iSeq = YmCommonDB.setSlabUpperState_V(sUpStackColGp,
					    							 	  sUpStackBedGp,
					    							 	  sUpStackLayerGp);
		    	}
		    	
		    	if(!"".equals(sSchId2)){
		    		
			    	sUpStackColGp   = sOPutLoc2.substring(0, 6);
					sUpStackBedGp   = sOPutLoc2.substring(6, 8);
					sUpStackLayerGp = sOPutLoc2.substring(8,10);
					
					sUpUsageCd 		= YmCommonUtil.getStackColInfoWithPk(sUpStackColGp);
			   		
			   		if(YmCommonConst.STACK_COL_USAGE_CD_XX.equals(sUpUsageCd)	||// SLAB 비상적치위치
				       YmCommonConst.STACK_COL_USAGE_CD_SE.equals(sUpUsageCd)	||// SLAB Scafing 입측
					   YmCommonConst.STACK_COL_USAGE_CD_SD.equals(sUpUsageCd)	){// SLAB Scafing 출측
					   	
						iSeq = YmCommonDB.deleteConveyorInfo(sUpStackColGp,
						   		  			 	 		     sSlabNo2);
					    if(iSeq < 0){
							logger.println(LogLevel.DEBUG,this, "재작업지시=> 적치단 삭제 FAIL");
						}	
						
					}else{	
						/* 
						 * 적치단 UP위치 Clear
						 * tb_ym_stacklayer Table : stock_id 		 = ''(Empty)
						 * tb_ym_stacklayer Table : stack_layer_stat = 'E'(적치가능)
						 */	
				    	iSeq = dao.updateCraneStackLayerStat(sUpStackColGp,
				    										 sUpStackBedGp,
				    										 sUpStackLayerGp,
				    										 "",
				    										 YmCommonConst.STACK_LAYER_STAT_E);
				        
						/*
				    	 * B열연 Slab 	
				    	 * 바로 위 상단 상태정보를 UPDATE
				    	 */
				    	iSeq = YmCommonDB.setSlabUpperState_V(sUpStackColGp,
						    							 	  sUpStackBedGp,
						    							 	  sUpStackLayerGp);
			    	}
		    	}
			/*
			 *	3.	수정위치 셋팅
			 */ 
			 	
			 	String sPutStackColGp    = "";
				String sPutStackBedGp    = "";
				String sPutStackLayerGp  = "";
				String sPutUsageCd 		= "";
				
				if(!"".equals(sSchId2)){
					
					sPutStackColGp   = sNPutLoc2.substring(0, 6);
					sPutStackBedGp   = sNPutLoc2.substring(6, 8);
					sPutStackLayerGp = sNPutLoc2.substring(8,10);
															  	   
					sPutUsageCd = YmCommonUtil.getStackColInfoWithPk(sPutStackColGp);
			   		
			   		if(YmCommonConst.STACK_COL_USAGE_CD_XX.equals(sPutUsageCd)	||// SLAB 비상적치위치
				       YmCommonConst.STACK_COL_USAGE_CD_SE.equals(sPutUsageCd)	||// SLAB Scafing 입측
					   YmCommonConst.STACK_COL_USAGE_CD_SD.equals(sPutUsageCd)	){// SLAB Scafing 출측
					   	
						iSeq = YmCommonDB.insertConveyorInfo(sPutStackColGp,
														     sSlabNo2,
															 sPutStackBedGp);
					    if(iSeq < 0){
							logger.println(LogLevel.DEBUG,this, "산적위치 수정=> 적치단 생성 FAIL");
						}	
						
					}else{	
						
						/* 
						 * 적치단 Put위치에 다른 SLAB가 있을 경우.
						 * 해당동의 XX번지로 저장품 MAP을 수정한다.
						 */	
						iSeq = YmCommonDB.updateLegacyStockId_Slab(dao,
															  	   sPutStackColGp,
															  	   sPutStackBedGp,
															  	   sPutStackLayerGp,
															  	   sSlabNo2);
						/* 
						 * 적치단 Put위치를 적치상태로 변경
						 * tb_ym_stacklayer Table : stock_id = Coil No
						 * tb_ym_stacklayer Table : stack_layer_stat	   = 'P'(적치중)
						 */	
				    	iSeq = dao.updateCraneStackLayerStat(sPutStackColGp,
				    										 sPutStackBedGp,
				    										 sPutStackLayerGp,
				    										 sSlabNo2,
				    										 YmCommonConst.STACK_LAYER_STAT_P);
			    		/*
				    	 * B열연 Slab 	
				    	 * 바로 위 상단 상태정보를 UPDATE
				    	 */
			    		iSeq = YmCommonDB.setSlabUpperState_E(sPutStackColGp,
							    						 	  sPutStackBedGp,
							    						 	  sPutStackLayerGp);
			    		
			    	}	
			    }
				sPutStackColGp   = sNPutLoc1.substring(0, 6);
				sPutStackBedGp   = sNPutLoc1.substring(6, 8);
				sPutStackLayerGp = sNPutLoc1.substring(8,10);
														  	   
				sPutUsageCd = YmCommonUtil.getStackColInfoWithPk(sPutStackColGp);
		   		
		   		if(YmCommonConst.STACK_COL_USAGE_CD_XX.equals(sPutUsageCd)	||// SLAB 비상적치위치
			       YmCommonConst.STACK_COL_USAGE_CD_SE.equals(sPutUsageCd)	||// SLAB Scafing 입측
				   YmCommonConst.STACK_COL_USAGE_CD_SD.equals(sPutUsageCd)	){// SLAB Scafing 출측
				   	
					iSeq = YmCommonDB.insertConveyorInfo(sPutStackColGp,
													     sSlabNo1,
														 sPutStackBedGp);
				    if(iSeq < 0){
						logger.println(LogLevel.DEBUG,this, "산적위치 수정=> 적치단 생성 FAIL");
					}	
					
				}else{	
					
					/* 
					 * 적치단 Put위치에 다른 SLAB가 있을 경우.
					 * 해당동의 XX번지로 저장품 MAP을 수정한다.
					 */	
					iSeq = YmCommonDB.updateLegacyStockId_Slab(dao,
														  	   sPutStackColGp,
														  	   sPutStackBedGp,
														  	   sPutStackLayerGp,
														  	   sSlabNo1);
														  	   
					/* 
					 * 적치단 Put위치를 적치상태로 변경
					 * tb_ym_stacklayer Table : stock_id = Coil No
					 * tb_ym_stacklayer Table : stack_layer_stat	   = 'P'(적치중)
					 */	
			    	iSeq = dao.updateCraneStackLayerStat(sPutStackColGp,
			    										 sPutStackBedGp,
			    										 sPutStackLayerGp,
			    										 sSlabNo1,
			    										 YmCommonConst.STACK_LAYER_STAT_P);
		    		/*
			    	 * B열연 Slab 	
			    	 * 바로 위 상단 상태정보를 UPDATE
			    	 */
		    		iSeq = YmCommonDB.setSlabUpperState_E(sPutStackColGp,
						    						 	  sPutStackBedGp,
						    						 	  sPutStackLayerGp);
		    		
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
     * CRANE 에 지시할 전문MESSAGE를 구성한다.
	 *     전문코드		TC				CHAR	07 전문코드       THHC110/120/130(작업지시/취소/수정작업지시)
	 *     CRANE 번호	CraneCode		CHAR	04 CRANE번호      
	 *     응답구분		ResponsId		CHAR	01 응답구분       
	 *     운전모드		DrivMode		CHAR	01 운전모드       1:ON LINE, 0:OFF LINE
	 *     고장/복구	TroubleRecover	CHAR	01 고장복구       0:정상,1:경미한 작업불가,9:치명적 작업불가
	 *     작업상태		WorkStatus		CHAR	01 작업상태       0:작업무, 1:선택중, 2:권상중, 3:이동중, 4:고장, 5:권상이상, 6:권하이상, 9:권하중
	 *     발생일		OccurDate		CHAR	06 발생일         
	 *     발생시		OccurTime		CHAR	06 발생시         
	 *     SCHEDULECODE	SchCode			CHAR	03 SCHEDULECODE   
	 *     권상번지		LoadAdd			CHAR	08 권상번지       
	 *     권하번지		UnLoadAdd		CHAR	08 권하번지       
	 *     군정보		GroupInfor		CHAR	01 군정보         
	 *     제품구분		GoodsId			CHAR	02 제품구분       1자리(1:입고재, 2:보류재, 3:장기재, 4:SCRAP, 5:차공정재) 2자리(1:냉각재, 2:H.F.P, 3:S.P.M)
	 *     코일번호		CoilNo			CHAR	10 코일번호       
	 *     제작번호/행번ProduceNo		CHAR	13 제작번호행번   
	 *     두께			T				CHAR	05 두께           ㎜	
	 *     폭			Width			CHAR	05 폭             ㎜
	 *     길이			Len				CHAR	05 길이           ㎜
	 *     외경			OutsideDia		CHAR	05 외경           ㎜
	 *     중량			Wt				CHAR	05 중량           g
 	 *     운송회사		TransCom		CHAR	05 운송회사       
	 *     차량번호		CarNo			CHAR	05 차량번호       
	 *     통로구분		PassId			CHAR	01 통로구분       
	 *     입출구분		InOutId			CHAR	01 입출구분       
	 *     적재매수		HeapEA			CHAR	02 적재매수       
	 *     잔여매수		RemEA			CHAR	02 잔여매수       
	 *     권상 X 위치	LoadXPosition	CHAR	06 권상X위치      
	 *     좌 허용오차	LoadXLeftTol	CHAR	04 권상X좌허용오차
	 *     우 허용오차	LoadXRightTol	CHAR	04 권상X우허용오차
	 *     권상 Y 위치	LoadYPosition	CHAR	06 권상Y위치      
	 *     상 허용오차	LoadYUpTol		CHAR	04 권상Y상허용오차
	 *     하 허용오차	LoadYDownTol	CHAR	04 권상Y하허용오차
	 *     권하 X 위치	UnLoadXPosition	CHAR	06 권하X위치      
	 *     좌 허용오차	UnLoadXLeftTol	CHAR	04 권하X좌허용오차
	 *     우 허용오차	UnLoadXRightTol	CHAR	04 권하X우허용오차
	 *     권하 Y 위치	UnLoadYPosition	CHAR	06 권하Y위치      
	 *     상 허용오차	UnLoadYUpTol	CHAR	04 권하Y상허용오차
	 *     하 허용오차	UnLoadYDownTol	CHAR	04 권하Y하허용오차
	 *     CTS NO 1		CTSNo1			CHAR	05 CTSNO1         
	 *     		상태	CTSNo1Status	CHAR	01 CTSNO1상태     
	 *     CTS NO 2		CTSNo2			CHAR	05 CTSNO2         
	 *         	상태	CTSNo2Status	CHAR	01 CTSNO2상태     
	 *	  수신코드		ReceiveCode		CHAR	07 수신코드       
	 *     LINE 구분	LineId			CHAR	01 LINE구분       
	 *     		SPARE	Spare			CHAR	11 SPARE          
     * @param schInfo : SCHEDULE INFO                             
     *
     * @return
     * @throws 
     */	 
	private String setCraneACoilMsgInfo(String sTcCd,
										String sSchId){
		
		StringBuffer sMsg = new StringBuffer();

		String TC				= ""; //07
		String CraneCode		= ""; //04
		String ResponsId		= ""; //01
		String DrivMode			= ""; //01
		String TroubleRecover	= ""; //01
		String WorkStatus		= ""; //01
		String OccurDate		= ""; //06
		String OccurTime		= ""; //06
		String SchCode			= ""; //03
		String LoadAdd			= ""; //08
		String UnLoadAdd		= ""; //08
		String GroupInfor		= ""; //01
		String GoodsId			= ""; //02
		String CoilNo			= ""; //10
		String ProduceNo		= ""; //13
		String T				= ""; //05
		String Width			= ""; //05
		String Len				= ""; //05
		String OutsideDia		= ""; //05
		String Wt				= ""; //05
		String TransCom			= ""; //05
		String CarNo			= ""; //05
		String PassId			= ""; //01
		String InOutId			= ""; //01
		String HeapEA			= ""; //02
		String RemEA			= ""; //02
		String LoadXPosition	= ""; //06
		String LoadXLeftTol		= ""; //04
		String LoadXRightTol	= ""; //04
		String LoadYPosition	= ""; //06
		String LoadYUpTol		= ""; //04
		String LoadYDownTol		= ""; //04
		String UnLoadXPosition	= ""; //06
		String UnLoadXLeftTol	= ""; //04
		String UnLoadXRightTol	= ""; //04
		String UnLoadYPosition	= ""; //06
		String UnLoadYUpTol		= ""; //04
		String UnLoadYDownTol	= ""; //04
		String CTSNo1			= ""; //5
		String CTSNo1Status		= ""; //1
		String CTSNo2			= ""; //5
		String CTSNo2Status		= ""; //1
		String ReceiveCode		= ""; //7
		String LineId			= ""; //1
		String Spare			= ""; //11
		String ZoneGp			= "";
		
		int iTC				=  7;
		int iCraneCode		=  4;
		int iResponsId		=  1;
		int iDrivMode		=  1;
		int iTroubleRecover	=  1;
		int iWorkStatus		=  1;
		int iOccurDate		=  6;
		int iOccurTime		=  6;
		int iSchCode		=  3;
		int iLoadAdd		=  8;
		int iUnLoadAdd		=  8;
		int iGroupInfor		=  1;
		int iGoodsId		=  2;
		int iCoilNo			= 10;
		int iProduceNo		= 13;
		int iT				=  5;
		int iWidth			=  5;
		int iLen			=  5;
		int iOutsideDia		=  5;
		int iWt				=  5;
		int iTransCom		=  5;
		int iCarNo			=  5;
		int iPassId			=  1;
		int iInOutId		=  1;
		int iHeapEA			=  2;
		int iRemEA			=  2;
		int iLoadXPosition	=  6;
		int iLoadXLeftTol	=  4;
		int iLoadXRightTol	=  4;
		int iLoadYPosition	=  6;
		int iLoadYUpTol		=  4;
		int iLoadYDownTol	=  4;
		int iUnLoadXPosition=  6;
		int iUnLoadXLeftTol	=  4;
		int iUnLoadXRightTol=  4;
		int iUnLoadYPosition=  6;
		int iUnLoadYUpTol	=  4;
		int iUnLoadYDownTol	=  4;
		int iCTSNo1			=  5;
		int iCTSNo1Status	=  1;
		int iCTSNo2			=  5;
		int iCTSNo2Status	=  1;
		int iReceiveCode	=  7;
		int iLineId			=  1;
		int iSpare			= 11;
						   
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			if(sSchId.length() == 6){
				
				logger.println(LogLevel.DEBUG,this, "작업지시 전문발생 대기모드.");
				
				JDTORecord epR  = dao.getLegacyEquipNoWithCurEquipNo(sSchId);
				
				TC				= YmCommonConst.TC_THHC110;
				OccurDate		= YmCommonUtil.getCurDate("yyMMdd");
				OccurTime		= YmCommonUtil.getCurDate("HHmmss");
				CraneCode		= epR != null ? 
								  StringHelper.evl(epR.getFieldString("EQUIP_GP"), "") : "";
				
				/*
				 *	1.	운전모드
				 *		1:ON LINE	0:OFFLINE
				 *	2.	고장복구
				 *		0:정상	1:경미한 작업불가	2:치명적작업불가
				  *	3.	작업상태
				 *		0:작업무	1:선택중	2:권상중	3:이동중
				 *		4:고장		5:권상이상	6:권하이상	9:권하중
				 */
				{ 
					JDTORecord jtR = dao.getEquipInfoWithEquipNo(sSchId.substring(0,1),//sYdGp
															 	 sSchId.substring(4)); //sEquipNo
															 	 
					String sWorkMode  = StringHelper.evl(jtR.getFieldString("WORK_MODE"),"");
					String sEquipStat = StringHelper.evl(jtR.getFieldString("EQUIP_STAT"),"");
					String sWprogStat = StringHelper.evl(jtR.getFieldString("WPROG_STAT"),"");
					
//					DrivMode		= (YmCommonConst.WORK_MODE_O.equals(sWorkMode) ? "1" : 
//									  (YmCommonConst.WORK_MODE_C.equals(sWorkMode) ? "0" : ""));
					
					if(YmCommonConst.WORK_MODE_O.equals(sWorkMode)){
						DrivMode ="0";
					}else if(YmCommonConst.WORK_MODE_C.equals(sWorkMode)){
						DrivMode ="1";
					}else if(YmCommonConst.WORK_MODE_E.equals(sWorkMode)){
						DrivMode ="2";
					}else{
						DrivMode ="";
					}
					
					TroubleRecover	= (YmCommonConst.WORK_MODE_O.equals(sEquipStat)? "0" : 
									  (YmCommonConst.WORK_MODE_C.equals(sEquipStat)? "1" : ""));
					
					if(YmCommonConst.WORK_PROG_STAT_W.equals(sWprogStat)){		sWprogStat = "0";
					}else if(YmCommonConst.WORK_PROG_STAT_1.equals(sWprogStat)){
						if(YmCommonConst.TC_THHC120.equals(sTcCd)){
							sWprogStat = "1";
						}else{
							sWprogStat = "2";
						}
					}else if(YmCommonConst.WORK_PROG_STAT_2.equals(sWprogStat)){sWprogStat = "9";
					}else if(YmCommonConst.WORK_PROG_STAT_3.equals(sWprogStat)){sWprogStat = "9";
					}
									  
					WorkStatus		= (YmCommonConst.WORK_MODE_C.equals(sEquipStat)? "4" :  sWprogStat);
				}
				
			}else{
				logger.println(LogLevel.DEBUG,this, "작업지시 전문발생 운행모드.");
				
				JDTORecord schInfo  = dao.getCraneSchInfo(sSchId);
		    	
		    	logger.println(LogLevel.DEBUG,this, "sSchId=" + sSchId);
		    	if(schInfo == null){
		    		logger.println(LogLevel.DEBUG,this, "작업지시 전문발생 스케쥴 정보가 존재안함.");
		    		return sMsg.toString();
		    	}
		    		
				String sYdGp 		= StringHelper.evl(schInfo.getFieldString("YD_GP"),"");
				String sBayGp 		= StringHelper.evl(schInfo.getFieldString("BAY_GP"),"");
				String sEquipKind	= YmCommonConst.EQUIP_KIND_CR;
				String sEquipNo 	= StringHelper.evl(schInfo.getFieldString("SCH_WORK_EQUIP_NO"),"");
				String sStockId		= StringHelper.evl(schInfo.getFieldString("STOCK_ID"),"");
				String sSchCode 	= StringHelper.evl(schInfo.getFieldString("SCH_WORK_KIND"),"");
				String sMainWorkYn 	= StringHelper.evl(schInfo.getFieldString("SCH_WORK_AID_YN"),"");
				String sUpLoc 		= StringHelper.evl(schInfo.getFieldString("CRANE_WORD_UP_LOC"),"");
				String sPutLoc 		= StringHelper.evl(schInfo.getFieldString("CRANE_WORD_PUT_LOC"),"");
				String sSchWorkStat = StringHelper.evl(schInfo.getFieldString("SCH_WORK_STAT"),"");
				String sSchJisiDate = StringHelper.evl(schInfo.getFieldString("WBOOK_SCH_ACT_DDTT"),"");
				
				/*
				 *	A.	최초 작업지시 시점에 작업지시일자를 셋팅한다.
				 */
				logger.println(LogLevel.DEBUG,this, "A.	최초 작업지시 시점에 작업지시일자를 셋팅한다.");
				{
					if("".equals(sSchJisiDate)){
						int iWrslt = dao.updateWbookSchActDdttSchInfo(sSchId,
																  	  YmCommonUtil.getStringYMDHMS());
					}											  	  
				}										  
				/*
				 *	0.	작업지시 전문발생 전 현 저장품 위치 체크
				 */
				logger.println(LogLevel.DEBUG,this, "0.	작업지시 전문발생 전 현 저장품 위치 체크");
				{
					String sDbUpLoc		= "";
			    	
					JDTORecord layerRc = dao.getUpStackLayerListWithSchId(sSchId);
			    	
			    	if(layerRc != null){
			    		sDbUpLoc	= StringHelper.evl(layerRc.getFieldString("STACK_COL_GP"), "")
							    	+ StringHelper.evl(layerRc.getFieldString("STACK_BED_GP"), "")
							    	+ StringHelper.evl(layerRc.getFieldString("STACK_LAYER_GP"), "");
		    		}
		    		
		    		if(!"".equals(sDbUpLoc) &&
		    		   !sUpLoc.equals(sDbUpLoc)){
		    		   	logger.println(LogLevel.DEBUG,this, "작업지시 전 저장품 위치 바뀜.==");
		    		   	logger.println(LogLevel.DEBUG,this, "현 위치로 권상위치 작업지시 내림.==");
		    		   	
		    		   	sUpLoc = sDbUpLoc;
		    		   	
		    		   	// 스케쥴 정보 수정
		    		   	int iSeq = dao.updateUpLocInfoWithSchId(sSchId,
		    								   				    sDbUpLoc);
		    		}
		    	}						  	    	
				/*
				 *	1.	TC CODE 
				 */
					TC	= sTcCd;
				logger.println(LogLevel.DEBUG,this, "1.	TC CODE ");
				/*
				 *	2.	CRANE NO 
				 *		6자리 설비번호(예:1ACR01)을 4자리 Legacy Crane No로 변환
				 */
				logger.println(LogLevel.DEBUG,this, "2.	CRANE NO ");	
				{ 
					JDTORecord jtR = dao.getLegacyEquipNoWithCurEquipNo(sYdGp+
																		sBayGp+
																		sEquipKind+
																		sEquipNo);
		    		String sCraneNo = "";
		    		if(jtR != null){
		    			sCraneNo = StringHelper.evl(jtR.getFieldString("EQUIP_GP"), "");
		    		}
	    		
					CraneCode		= sCraneNo;
				}
				/*
				 *	3.	응답구분
				 */
				logger.println(LogLevel.DEBUG,this, "3.	응답구분 ");
				{ 
					
					ResponsId		= "";
				}
				
				/*
				 *	3.	운전모드
				 *		1:ON LINE	0:OFFLINE
				 *	4.	고장복구
				 *		0:정상	1:경미한 작업불가	2:치명적작업불가
				  *	5.	작업상태
				 *		0:작업무	1:선택중	2:권상중	3:이동중
				 *		4:고장		5:권상이상	6:권하이상	9:권하중
				 */
				logger.println(LogLevel.DEBUG,this, "운전모드/고장복구/작업상태 ");
				{ 
					JDTORecord jtR = dao.getEquipInfoWithEquipNo(sYdGp,
															 	 sEquipNo);
					
					String sWorkMode  = StringHelper.evl(jtR.getFieldString("WORK_MODE"),"");
					String sEquipStat = StringHelper.evl(jtR.getFieldString("EQUIP_STAT"),"");
					String sWprogStat = StringHelper.evl(jtR.getFieldString("WPROG_STAT"),"");
					
//					DrivMode		= (YmCommonConst.WORK_MODE_O.equals(sWorkMode) ? "1" : 
//									  (YmCommonConst.WORK_MODE_C.equals(sWorkMode) ? "0" : ""));
					
					if(YmCommonConst.WORK_MODE_O.equals(sWorkMode)){
						DrivMode ="0";
					}else if(YmCommonConst.WORK_MODE_C.equals(sWorkMode)){
						DrivMode ="1";
					}else if(YmCommonConst.WORK_MODE_E.equals(sWorkMode)){
						DrivMode ="2";
					}else{
						DrivMode ="";
					}
					
					TroubleRecover	= (YmCommonConst.WORK_MODE_O.equals(sEquipStat)? "0" : 
									  (YmCommonConst.WORK_MODE_C.equals(sEquipStat)? "1" : ""));
					
					if(YmCommonConst.WORK_PROG_STAT_W.equals(sWprogStat)){		sWprogStat = "0";
					}else if(YmCommonConst.WORK_PROG_STAT_1.equals(sWprogStat)){
						if(YmCommonConst.TC_THHC120.equals(sTcCd)){
							sWprogStat = "1";
						}else{
							sWprogStat = "2";
						}
					}else if(YmCommonConst.WORK_PROG_STAT_2.equals(sWprogStat)){sWprogStat = "9";
					}else if(YmCommonConst.WORK_PROG_STAT_3.equals(sWprogStat)){sWprogStat = "9";
					}
									  
					WorkStatus		= (YmCommonConst.WORK_MODE_C.equals(sEquipStat)? "4" :  sWprogStat);
					
					OccurDate		= YmCommonUtil.getCurDate("yyMMdd");
					OccurTime		= YmCommonUtil.getCurDate("HHmmss");
				}
				
				/*
				 *	6.	SCHEDULE CODE
				 *		4자리 SCH_CODE => 3자리 SCH_CODE 로 변환
				 */
				logger.println(LogLevel.DEBUG,this, "6.	SCHEDULE CODE");
				{ 
					JDTORecord jtR = dao.getLegacySchCode(sSchCode);
		    		
		    		if(jtR != null){
		    			SchCode	= StringHelper.evl(jtR.getFieldString("SCH_WORK_KIND"), "");
		    		}
		    	}
				/*
				 *	7.	권상번지
				 */
				logger.println(LogLevel.DEBUG,this, "7.	권상번지");
				{ 
					if(sUpLoc.indexOf("TR") != -1){
						LoadAdd		= YmCommonUtil.setLegacyPositionWithCurTr(sUpLoc,sStockId);
					}else{
						LoadAdd		= YmCommonUtil.setLegacyPositionWithCur(sUpLoc);
					}
				} 
				/*
				 *	8.	권하번지
				 */
				logger.println(LogLevel.DEBUG,this, "6.	SCHEDULE CODE");
				{ 
					if(sPutLoc.indexOf("TR") != -1){
						UnLoadAdd		= YmCommonUtil.setLegacyPositionWithCurTr(sPutLoc,sStockId);
					}else{
						UnLoadAdd		= YmCommonUtil.setLegacyPositionWithCur(sPutLoc);
					}
				}			
				/*
				 *	9.	군정보
				 */
				logger.println(LogLevel.DEBUG,this, "9.	군정보");
				{ 
					GroupInfor	= "2";
				}	
				/*
				 *	10.	제품구분
				 *		1자리-	1:입고재	2:보류재	3:장기재	4:SCRAP	5:차공정재
				 *		2자리-	1:냉각재	2:HFL		3:SPM
				 */
				logger.println(LogLevel.DEBUG,this, "10.	제품구분");
				{ 
					GoodsId	= "";
				}				
				/*
				 *	11.	코일번호
				 */
				logger.println(LogLevel.DEBUG,this, "11.	코일번호");
				{ 
					CoilNo	= sStockId;
				}	
				
				/*
				 *	12.	코일 기본정보 
				 *		-	제작번호/행번
				 *		-	두께	CHAR	5	㎜	Coil 공통 두께 * 1000
				 *		-	폭		CHAR	5	㎜	Coil 공통 폭에 정수만 가져와서 5자리로 만듦
				 *		-	길이	CHAR	5	Cm	Coil 공통 길이 * 10
				 *		-	외경	CHAR	5	㎜	Coil 공통 그대로
				 *		-	중량	CHAR	5	Kg	Coil 공통 그대로
				 */
				logger.println(LogLevel.DEBUG,this, "12.	코일 기본정보 ");
				{ 
					JDTORecord cInfo = dao.getCoilCommonInfo(CoilNo);
			    		
		    		if(cInfo != null){
		    			
		    			ProduceNo	= YmCommonUtil.FillToString(StringHelper.evl(cInfo.getFieldString("제작번호"), ""),10)+
		    						  StringHelper.evl(cInfo.getFieldString("제작행번"), "");
		    			T 			= Double.valueOf((Double.parseDouble(StringHelper.evl(cInfo.getFieldString("코일두께"), "0")) * 1000) + "").longValue()+"";
						Width 		= Double.valueOf(StringHelper.evl(cInfo.getFieldString("코일폭"), "0")).longValue()+ "";
						Len 		= (Long.parseLong(StringHelper.evl(cInfo.getFieldString("코일길이"), "0"))* 10) + "";
		    			OutsideDia	= StringHelper.evl(cInfo.getFieldString("코일외경"), ""); 
						Wt			= StringHelper.evl(cInfo.getFieldString("코일중량"), ""); 
						ZoneGp		= StringHelper.evl(cInfo.getFieldString("YD_ZONE_GP"), ""); 
		    		}
				}
				
				//목적존이 존재 하는 경우 제품구분 항목에 넣어 L2차상국 화면에 표기를 해준다.
				if(!"".equals(ZoneGp)){
					GoodsId =ZoneGp ;
				}
				
				/*
				 *	13.	운송회사
				 *		운송회사코드	-	출하(TB_DM_CARCARDINFO)TRANS_COM_CD
				 *	14.	차량번호
				 *		차넘버 끝 4자리	-	출하(TB_DM_CARCARDINFO)CAR_NO
				 *	15.	통로구분
			     *		동구분, 출하가 아닐때는 동구분을 'R' 로 넘김;
			     *		정지위치가 1FTR03 or 1FTR04 일대는 I 동	출하시
				 *	16.	입출구분
				 *		1 : 반입  ,  2 : 출하, 3:이송상차 , 4:이송하차
				 *	17.	적재매수
				 *	18.	잔여매수
				 	
				 	19. 출하가 아닐 경우. 
						 
						 운송회사	[5]	SS			#####
						 차량번호	[5]	2162		#####
						 통로구분	[1]	6			R
						 입출구분	[1]	2			?
						 적재매수	[2]	00			00
						 잔여매수	[2]	01			00
				 */
				logger.println(LogLevel.DEBUG,this, "운송회사/차량번호/통로구분/입출구분");
				{ 
					if(YmCommonConst.NEW_SCH_WORK_KIND_GVFL.equals(sSchCode)|| // COIL 제품출하상차
					   YmCommonConst.NEW_SCH_WORK_KIND_GVF1.equals(sSchCode)|| // Coil 제품출하상차
			    	   YmCommonConst.NEW_SCH_WORK_KIND_GVF2.equals(sSchCode)|| // Coil 제품출하상차	
			    	   YmCommonConst.NEW_SCH_WORK_KIND_GTFL.equals(sSchCode)|| // COIL 제품출하상차
			    	   YmCommonConst.NEW_SCH_WORK_KIND_GTF1.equals(sSchCode)|| // Coil 제품출하상차
			    	   YmCommonConst.NEW_SCH_WORK_KIND_GTF2.equals(sSchCode)||    		    
			    	   YmCommonConst.NEW_SCH_WORK_KIND_GPFL.equals(sSchCode)|| // COIL 제품출하상차
			    	   YmCommonConst.NEW_SCH_WORK_KIND_GPF1.equals(sSchCode)|| // Coil 제품출하상차
			    	   YmCommonConst.NEW_SCH_WORK_KIND_GPF2.equals(sSchCode)||
					   YmCommonConst.NEW_SCH_WORK_KIND_GVML.equals(sSchCode)|| // COIL 제품이송상차
					   YmCommonConst.NEW_SCH_WORK_KIND_GVM2.equals(sSchCode)|| // COIL 제품이송상차
			   		   YmCommonConst.NEW_SCH_WORK_KIND_GVM3.equals(sSchCode)|| // COIL 제품이송상차		
					   YmCommonConst.NEW_SCH_WORK_KIND_GVMU.equals(sSchCode)|| // COIL 제품이송하차
					   YmCommonConst.NEW_SCH_WORK_KIND_GVM4.equals(sSchCode)|| // COIL 제품이송하차	 	
		   			   YmCommonConst.NEW_SCH_WORK_KIND_GVM5.equals(sSchCode)|| // COIL 제품이송하차			
					   YmCommonConst.NEW_SCH_WORK_KIND_CVML.equals(sSchCode)|| // COIL 소재이송상차
					   YmCommonConst.NEW_SCH_WORK_KIND_CVM2.equals(sSchCode)|| // COIL 소재이송상차
			   		   YmCommonConst.NEW_SCH_WORK_KIND_CVM3.equals(sSchCode)|| // COIL 소재이송상차
					   YmCommonConst.NEW_SCH_WORK_KIND_CVMU.equals(sSchCode)|| // COIL 소재이송하차
					   YmCommonConst.NEW_SCH_WORK_KIND_CVM4.equals(sSchCode)|| // COIL 소재이송하차	 
		  		   	   YmCommonConst.NEW_SCH_WORK_KIND_CVM5.equals(sSchCode)){ // COIL 소재이송하차	 
				   	
					   	
					   	JDTORecord carR = dao.getDmCarInfo(sStockId);
				    	if(carR != null){
				    		TransCom	= StringHelper.evl(carR.getFieldString("TRANS_COM_CD"), "");
							CarNo		= StringHelper.evl(carR.getFieldString("CAR_NO_ADDR"), "");
				    	}
							
						if(sPutLoc.indexOf("1FTR03") != -1 ||
						   sPutLoc.indexOf("1FTR04") != -1 ){
							
							PassId	= 	"I";
						}else{
							PassId	= 	sPutLoc.substring(1,2);	
						}
					
						if(YmCommonConst.NEW_SCH_WORK_KIND_GVFL.equals(sSchCode)|| //COIL 제품출하상차
						   YmCommonConst.NEW_SCH_WORK_KIND_GVF1.equals(sSchCode)|| //Coil 제품출하상차
				    	   YmCommonConst.NEW_SCH_WORK_KIND_GVF2.equals(sSchCode)||
				    	   YmCommonConst.NEW_SCH_WORK_KIND_GTFL.equals(sSchCode)|| // COIL 제품출하상차
				    	   YmCommonConst.NEW_SCH_WORK_KIND_GTF1.equals(sSchCode)|| // Coil 제품출하상차
				    	   YmCommonConst.NEW_SCH_WORK_KIND_GTF2.equals(sSchCode)||    		    
				    	   YmCommonConst.NEW_SCH_WORK_KIND_GPFL.equals(sSchCode)|| // COIL 제품출하상차
				    	   YmCommonConst.NEW_SCH_WORK_KIND_GPF1.equals(sSchCode)|| // Coil 제품출하상차
				    	   YmCommonConst.NEW_SCH_WORK_KIND_GPF2.equals(sSchCode) 
				    	   ){ 		InOutId		= "2";
						}else if(YmCommonConst.NEW_SCH_WORK_KIND_GVML.equals(sSchCode)|| // COIL 제품이송상차		
						         YmCommonConst.NEW_SCH_WORK_KIND_GVM2.equals(sSchCode)|| // COIL 제품이송상차
			   		   			 YmCommonConst.NEW_SCH_WORK_KIND_GVM3.equals(sSchCode)){InOutId		= "3";
						}else if(YmCommonConst.NEW_SCH_WORK_KIND_GVMU.equals(sSchCode)|| // COIL 제품이송하차	 
								 YmCommonConst.NEW_SCH_WORK_KIND_GVM4.equals(sSchCode)|| // COIL 제품이송하차	 	
				   			     YmCommonConst.NEW_SCH_WORK_KIND_GVM5.equals(sSchCode)){InOutId		= "4";		
						}else if(YmCommonConst.NEW_SCH_WORK_KIND_CVML.equals(sSchCode)|| // COIL 소재이송상차
								 YmCommonConst.NEW_SCH_WORK_KIND_CVM2.equals(sSchCode)|| // COIL 소재이송상차
					   		     YmCommonConst.NEW_SCH_WORK_KIND_CVM3.equals(sSchCode)){InOutId		= "3";
						}else if(YmCommonConst.NEW_SCH_WORK_KIND_CVMU.equals(sSchCode)|| // COIL 소재이송하차	  
						         YmCommonConst.NEW_SCH_WORK_KIND_CVM4.equals(sSchCode)|| // COIL 소재이송하차	 
		  		   	   			 YmCommonConst.NEW_SCH_WORK_KIND_CVM5.equals(sSchCode)){InOutId		= "4";
						}
						
						{
					    	List 	   dmList  = null; // Card No,운송지시일자,순번이 같은 저장품정보
							JDTORecord dmRc    = null; // 저장품정보
							String 	   sSmt    = "";   // 저장품이동조건
							int		   iDmSize = 0;    // Card No,운송지시일자,순번이 같은 저장품 갯수
							int		   iDmFns  = 0;    // 상,하차완료인 저장품 갯수	
								
							dmList  = dao.getYmDmCommonInfo(sStockId);
							iDmSize = dmList.size();
							
							for(int inx = 0; inx < dmList.size() ; inx++){
						 	 	dmRc = (JDTORecord)dmList.get(inx);
						 	 	sSmt = StringHelper.evl(dmRc.getFieldString("STOCK_MOVE_TERM"), "");
						 	 	
						 	 	if(YmCommonConst.NEW_STOCK_MOVE_TERM_VL.equals(sSmt)|| // 상차완료
						 	 	   YmCommonConst.NEW_STOCK_MOVE_TERM_E1.equals(sSmt)|| // 이송완료	
						 	 	   YmCommonConst.NEW_STOCK_MOVE_TERM_CC.equals(sSmt)){ // 정정작업대기	 	
									
									iDmFns++;
						 	 	}
						 	}
						 	HeapEA		=  iDmSize + "";
						 	RemEA		= (iDmSize - iDmFns)+"";
						}	
	
					}else{
						TransCom	= "#####";
						CarNo		= "#####";
						PassId		= "R";
						InOutId		= "?";
						HeapEA		= "00";
						RemEA		= "00";
					}
				}		
				
				/*
				 *	19.	권상,권하 X Y 좌표 및 오차범위
				 */
				logger.println(LogLevel.DEBUG,this, "19.	권상,권하 X Y 좌표 및 오차범위");
				{
					String sUpXLeft 	= "";
					String sUpXRight 	= "";
					String sUpYUp 		= "";
					String sUpYDown 	= "";
					 
					String sPutXLeft 	= "";
					String sPutXRight 	= "";
					String sPutYUp 		= "";
					String sPutYDown 	= ""; 
	
					JDTORecord ruleUpX = dao.getStackRuleInfo_002(sUpLoc.substring(0, 6),
																  YmCommonConst.STACK_RULE_CD_XCD);
					if(ruleUpX != null){
						sUpXLeft 	= StringHelper.evl(ruleUpX.getFieldString("STACK_RULE_MIN"), "");
						sUpXRight 	= StringHelper.evl(ruleUpX.getFieldString("STACK_RULE_MAX"), "");
					}
					JDTORecord ruleUpY = dao.getStackRuleInfo_002(sUpLoc.substring(0, 6),
																  YmCommonConst.STACK_RULE_CD_YCD);
					if(ruleUpY != null){
						sUpYUp 		= StringHelper.evl(ruleUpY.getFieldString("STACK_RULE_MAX"), "");
						sUpYDown 	= StringHelper.evl(ruleUpY.getFieldString("STACK_RULE_MIN"), "");
					}
					
					JDTORecord rulePutX = dao.getStackRuleInfo_002(sPutLoc.substring(0, 6),
																   YmCommonConst.STACK_RULE_CD_XCD);
					if(rulePutX != null){
						sPutXLeft 	= StringHelper.evl(rulePutX.getFieldString("STACK_RULE_MIN"), "");
						sPutXRight 	= StringHelper.evl(rulePutX.getFieldString("STACK_RULE_MAX"), "");
					}
					
					JDTORecord rulePutY = dao.getStackRuleInfo_002(sPutLoc.substring(0, 6),
																   YmCommonConst.STACK_RULE_CD_YCD);
					if(rulePutY != null){
						sPutYUp 	= StringHelper.evl(rulePutY.getFieldString("STACK_RULE_MAX"), "");
						sPutYDown 	= StringHelper.evl(rulePutY.getFieldString("STACK_RULE_MIN"), "");
					}
					
					if(sUpLoc.length() == 10){
						JDTORecord upR = dao.getStackLayerInfoWithPk(sUpLoc.substring(0, 6),
																	 sUpLoc.substring(6, 8),
																	 sUpLoc.substring(8,10));
				    	String sUpXPosition = "";	
				    	String sUpYPosition = "";
				    		    	
				    	if(upR != null){
				    	
				    		sUpXPosition = StringHelper.evl(upR.getFieldString("STACK_LAYER_X_AXIS"), "");					
				    		sUpYPosition = StringHelper.evl(upR.getFieldString("STACK_LAYER_Y_AXIS"), "");
				    		
			    		}else{
			    			
			    			/*
			    			 * 설비정보는 권상시 적치단정보를 삭제하기 때문에 좌표정보를 가져올 수 없다.
			    			 * 따라서, 적치열정보를 가져온다.
			    			 */
			    			JDTORecord upSubR = dao.getStackColInfoWithPk(sUpLoc.substring(0, 6));
			    			if(upSubR != null){
			    				sUpXPosition = StringHelper.evl(upSubR.getFieldString("STACK_COL_RULE_X_AXIS"), "");					
			    				sUpYPosition = StringHelper.evl(upSubR.getFieldString("STACK_COL_RULE_Y_AXIS"), "");
			    			}
			    		
			    		}
			    		
			    		LoadXPosition	= sUpXPosition;
						LoadXLeftTol	= sUpXLeft;
						LoadXRightTol	= sUpXRight;
						LoadYPosition	= sUpYPosition;
						LoadYUpTol		= sUpYUp;
						LoadYDownTol	= sUpYDown;
			    	}
			    	
			    	if(sPutLoc.length() == 10){
			    		JDTORecord putR = dao.getStackLayerInfoWithPk(sPutLoc.substring(0, 6),
																	  sPutLoc.substring(6, 8),
																	  sPutLoc.substring(8,10));
				    	String sPutXPosition = "";
				    	String sPutYPosition = "";
				    	
				    	if(putR != null){
				    		sPutXPosition = StringHelper.evl(putR.getFieldString("STACK_LAYER_X_AXIS"), "");					
				    		sPutYPosition = StringHelper.evl(putR.getFieldString("STACK_LAYER_Y_AXIS"), "");
				    		
			    		}else{
			    			
			    			/*
			    			 * 설비정보는 권상시 적치단정보를 삭제하기 때문에 좌표정보를 가져올 수 없다.
			    			 * 따라서, 적치열정보를 가져온다.
			    			 */
			    			JDTORecord putSubR = dao.getStackColInfoWithPk(sPutLoc.substring(0, 6));
			    			if(putSubR != null){
			    				sPutXPosition = StringHelper.evl(putSubR.getFieldString("STACK_COL_RULE_X_AXIS"), "");					
			    				sPutYPosition = StringHelper.evl(putSubR.getFieldString("STACK_COL_RULE_Y_AXIS"), "");
			    			}
			    		}
			    		UnLoadXPosition	= sPutXPosition;
						UnLoadXLeftTol	= sPutXLeft;
						UnLoadXRightTol	= sPutXRight;
						UnLoadYPosition	= sPutYPosition;
						UnLoadYUpTol	= sPutYUp;
						UnLoadYDownTol	= sPutYDown;
			    	}
		    	}
				/*
				 *	19.	CTSNO1
				 */
				{ 
					CTSNo1	= "";
				}	
				/*
				 *	20.	CTSNO1 상태
				 */
				{ 
					CTSNo1Status	= "";
				}	
				/*
				 *	21.	CTSNO2
				 */
				{ 
					CTSNo2	= "";
				}	
				/*
				 *	22.	CTSNO2 상태
				 */
				{ 
					CTSNo2Status	= "";
				}	
				/*
				 *	23.	수신코드
				 */
				{ 
					ReceiveCode	= "";
				}	
				/*
				 *	24.	LINE 구분
				 */
				{ 
					LineId	= "";
				}	
				/*
				 *	25.	SPARE
				 */
				{ 
					Spare	= "";
				}			
			}
			logger.println(LogLevel.DEBUG,this, "Message:" + sMsg);
			sMsg.append(YmCommonUtil.FillToString(TC				,iTC));
			sMsg.append(YmCommonUtil.FillToString(CraneCode			,iCraneCode));
			sMsg.append(YmCommonUtil.FillToString(ResponsId			,iResponsId));
			sMsg.append(YmCommonUtil.FillToString(DrivMode			,iDrivMode));
			sMsg.append(YmCommonUtil.FillToString(TroubleRecover	,iTroubleRecover));
			sMsg.append(YmCommonUtil.FillToString(WorkStatus		,iWorkStatus));
			sMsg.append(YmCommonUtil.FillToString(OccurDate			,iOccurDate));
			sMsg.append(YmCommonUtil.FillToString(OccurTime			,iOccurTime));
			sMsg.append(YmCommonUtil.FillToString(SchCode			,iSchCode));
			sMsg.append(YmCommonUtil.FillToString(LoadAdd			,iLoadAdd));
			sMsg.append(YmCommonUtil.FillToString(UnLoadAdd			,iUnLoadAdd));
			sMsg.append(YmCommonUtil.FillToString(GroupInfor		,iGroupInfor));
			sMsg.append(YmCommonUtil.FillToString(GoodsId			,iGoodsId));
			sMsg.append(YmCommonUtil.FillToString(CoilNo			,iCoilNo));
			sMsg.append(YmCommonUtil.FillToString(ProduceNo			,iProduceNo));
			sMsg.append(YmCommonUtil.FillToNumber(T					,iT));
			sMsg.append(YmCommonUtil.FillToNumber(Width				,iWidth));
			sMsg.append(YmCommonUtil.FillToNumber(Len				,iLen));
			sMsg.append(YmCommonUtil.FillToNumber(OutsideDia		,iOutsideDia));
			sMsg.append(YmCommonUtil.FillToNumber(Wt				,iWt));
			sMsg.append(YmCommonUtil.FillToString(TransCom			,iTransCom));
			sMsg.append(YmCommonUtil.FillToString(CarNo				,iCarNo));
			sMsg.append(YmCommonUtil.FillToString(PassId			,iPassId));
			sMsg.append(YmCommonUtil.FillToString(InOutId			,iInOutId));
			sMsg.append(YmCommonUtil.FillToNumber(HeapEA			,iHeapEA));
			sMsg.append(YmCommonUtil.FillToNumber(RemEA				,iRemEA));
			sMsg.append(YmCommonUtil.FillToNumber(LoadXPosition		,iLoadXPosition));
			sMsg.append(YmCommonUtil.FillToNumber(LoadXLeftTol		,iLoadXLeftTol));
			sMsg.append(YmCommonUtil.FillToNumber(LoadXRightTol		,iLoadXRightTol));
			sMsg.append(YmCommonUtil.FillToNumber(LoadYPosition		,iLoadYPosition));
			sMsg.append(YmCommonUtil.FillToNumber(LoadYUpTol		,iLoadYUpTol));
			sMsg.append(YmCommonUtil.FillToNumber(LoadYDownTol		,iLoadYDownTol));
			sMsg.append(YmCommonUtil.FillToNumber(UnLoadXPosition	,iUnLoadXPosition));
			sMsg.append(YmCommonUtil.FillToNumber(UnLoadXLeftTol	,iUnLoadXLeftTol));
			sMsg.append(YmCommonUtil.FillToNumber(UnLoadXRightTol	,iUnLoadXRightTol));
			sMsg.append(YmCommonUtil.FillToNumber(UnLoadYPosition	,iUnLoadYPosition));
			sMsg.append(YmCommonUtil.FillToNumber(UnLoadYUpTol		,iUnLoadYUpTol));
			sMsg.append(YmCommonUtil.FillToNumber(UnLoadYDownTol	,iUnLoadYDownTol));
			sMsg.append(YmCommonUtil.FillToString(CTSNo1			,iCTSNo1));
			sMsg.append(YmCommonUtil.FillToString(CTSNo1Status		,iCTSNo1Status));
			sMsg.append(YmCommonUtil.FillToString(CTSNo2			,iCTSNo2));
			sMsg.append(YmCommonUtil.FillToString(CTSNo2Status		,iCTSNo2Status));
			sMsg.append(YmCommonUtil.FillToString(ReceiveCode		,iReceiveCode));
			sMsg.append(YmCommonUtil.FillToString(LineId			,iLineId));
			sMsg.append(YmCommonUtil.FillToString(Spare				,iSpare));
			
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return sMsg.toString();
	}
	
	/**
     * CRANE 에 전송할 BACK UP실적 전문MESSAGE를 구성한다.
     *
	 *	1	전문코드			C	7	전문코드	THHC140
	 *	2	SPARE				C	4	SPARE
	 *	3	SPARE				C	4	SPARE
	 *	4	발생일시			C	12	발생일시
	 *	5	SPARE				C	3	SPARE
	 *	6	권상ADDRESS			C	8	권상ADDRESS
	 *	7	권하ADDRESS			C	8	권하ADDRESS
	 *	8	군					C	1	군
	 *	9	제품구분			C	2	제품구분
	 *	10	COILNO				C	10	COILNO
	 *	11	제작번호행번		C	13	제작번호행번
	 *	12	두께				C	5	두께
	 *	13	폭 					C	5	폭 
	 *	14	길이				C	5	길이
	 *	15	외경				C	5	외경
	 *	16	중량				C	5	중량
	 *	17	SPARE				C	12	SPARE
	 *	18	SPARE				C	4	SPARE
	 *	19	권상X축물리ADDRESS	C	6	권상X축물리ADDRESS
	 *	20	권상Y축물리ADDRESS	C	6	권상Y축물리ADDRESS
	 *	21	권하X축물리ADDRESS	C	6	권하X축물리ADDRESS
	 *	22	권하Y축물리ADDRESS	C	6	권하Y축물리ADDRESS      
	 * 
     * @param schInfo : SCHEDULE INFO                             
     *
     * @return
     * @throws 
     */	 
	private String setCraneACoilBackUpMsgInfo(String sStockId,
											  String sUpLoc,
											  String sPutLoc){
		
		StringBuffer sMsg = new StringBuffer();
		
		String TC				= ""; //07
		String Spare1			= ""; //04
		String Spare2			= ""; //04
		String OccurDate		= ""; //12
		String Spare3			= ""; //03
		String LoadAdd			= ""; //08
		String UnLoadAdd		= ""; //08
		String GroupInfor		= ""; //01
		String GoodsId			= ""; //02
		String CoilNo			= ""; //10
		String ProduceNo		= ""; //13
		String T				= ""; //05
		String Width			= ""; //05
		String Len				= ""; //05
		String OutsideDia		= ""; //05
		String Wt				= ""; //05
		String Spare4			= ""; //12
		String Spare5			= ""; //04
		String LoadXPosition	= ""; //06
		String LoadYPosition	= ""; //06
		String UnLoadXPosition	= ""; //06
		String UnLoadYPosition	= ""; //06
		String ZoneGp			= "";
		
		int	iTC					=  7;
		int iSpare1				=  4;
		int iSpare2				=  4;
		int iOccurDate			= 12;
		int iSpare3				=  3;
		int iLoadAdd			=  8;
		int iUnLoadAdd			=  8;
		int iGroupInfor			=  1;
		int iGoodsId			=  2;
		int iCoilNo				= 10;
		int iProduceNo			= 13;
		int iT					=  5;
		int iWidth				=  5;
		int iLen				=  5;
		int iOutsideDia			=  5;
		int iWt					=  5;
		int iSpare4				= 12;
		int iSpare5				=  4;
		int iLoadXPosition		=  6;
		int iLoadYPosition		=  6;
		int iUnLoadXPosition	=  6;
		int iUnLoadYPosition	=  6;
						   
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			/*
			 *	1.	TC CODE 
			 */
				TC	= YmCommonConst.TC_THHC140;
			/*
			 *	2.	발생일자
			 */
			{ 
				OccurDate		= YmCommonUtil.getCurDate("yyMMddHHmmss");
			} 
			/*
			 *	3.	권상번지
			 */ 
			{ 
				if(sUpLoc.indexOf("TR") != -1){
					LoadAdd		= YmCommonUtil.setLegacyPositionWithCurTr(sUpLoc,sStockId);
				}else{
					LoadAdd		= YmCommonUtil.setLegacyPositionWithCur(sUpLoc);
				}
			} 
			/*
			 *	4.	권하번지
			 */
			{ 
				if(sPutLoc.indexOf("TR") != -1){
					UnLoadAdd	= YmCommonUtil.setLegacyPositionWithCurTr(sPutLoc,sStockId);
				}else{
					UnLoadAdd	= YmCommonUtil.setLegacyPositionWithCur(sPutLoc);
				}
			}			
			/*
			 *	5.	군정보
			 */
			{ 
				GroupInfor	= "";
			}	
			/*
			 *	6.	제품구분
			 *		1자리-	1:입고재	2:보류재	3:장기재	4:SCRAP	5:차공정재
			 *		2자리-	1:냉각재	2:HFL		3:SPM
			 */
			{ 
				GoodsId	= "";
			}				
			/*
			 *	7.	코일번호
			 */
			{ 
				CoilNo	= sStockId;
			}	
			
			/*
			 *	8.	코일 기본정보 
			 *		-	제작번호/행번
			 *		-	두께	CHAR	5	㎜	Coil 공통 두께 * 1000
			 *		-	폭		CHAR	5	㎜	Coil 공통 폭에 정수만 가져와서 5자리로 만듦
			 *		-	길이	CHAR	5	Cm	Coil 공통 길이 * 10
			 *		-	외경	CHAR	5	㎜	Coil 공통 그대로
			 *		-	중량	CHAR	5	Kg	Coil 공통 그대로
			 */
			{ 
				JDTORecord cInfo = dao.getCoilCommonInfo(CoilNo);
		    		
	    		if(cInfo != null){
	    			
	    			ProduceNo	= YmCommonUtil.FillToString(StringHelper.evl(cInfo.getFieldString("제작번호"), ""),10)+
	    						  StringHelper.evl(cInfo.getFieldString("제작행번"), "");
					T 			= Double.valueOf((Double.parseDouble(StringHelper.evl(cInfo.getFieldString("코일두께"), "0")) * 1000) + "").longValue()+"";
					Width 		= Double.valueOf(StringHelper.evl(cInfo.getFieldString("코일폭"), "0")).longValue()+ "";
					Len 		= (Long.parseLong(StringHelper.evl(cInfo.getFieldString("코일길이"), "0"))* 10) + "";
					OutsideDia	= StringHelper.evl(cInfo.getFieldString("코일외경"), ""); 
					Wt			= StringHelper.evl(cInfo.getFieldString("코일중량"), ""); 
					ZoneGp		= StringHelper.evl(cInfo.getFieldString("YD_ZONE_GP"), ""); 
	    		}
			}
			
			
			//목적존이 존재 하는 경우 제품구분 항목에 넣어 L2차상국 화면에 표기를 해준다.
			if(!"".equals(ZoneGp)){
				GoodsId =ZoneGp ;
			}
			
			/*
			 *	9.	권상,권하 X Y 좌표 
			 */
			{
				if(sUpLoc.length() == 10){
					JDTORecord upR = dao.getStackLayerInfoWithPk(sUpLoc.substring(0, 6),
																 sUpLoc.substring(6, 8),
																 sUpLoc.substring(8,10));
			    	String sUpXPosition = "";
			    	String sUpYPosition = "";
			    	
			    	if(upR != null){
			    		sUpXPosition = StringHelper.evl(upR.getFieldString("STACK_LAYER_X_AXIS"), "");					
						sUpYPosition = StringHelper.evl(upR.getFieldString("STACK_LAYER_Y_AXIS"), "");
					}else{
						/*
		    			 * 설비정보는 권상시 적치단정보를 삭제하기 때문에 좌표정보를 가져올 수 없다.
		    			 * 따라서, 적치열정보를 가져온다.
		    			 */
						JDTORecord upSubR = dao.getStackColInfoWithPk(sUpLoc.substring(0, 6));
						if(upSubR != null){
							sUpXPosition = StringHelper.evl(upSubR.getFieldString("STACK_COL_RULE_X_AXIS"), "");					
							sUpYPosition = StringHelper.evl(upSubR.getFieldString("STACK_COL_RULE_Y_AXIS"), "");
						}
					}
					LoadXPosition	= sUpXPosition;
					LoadYPosition	= sUpYPosition;
		    	}
		    	

		    	if(sPutLoc.length() == 10){
		    		JDTORecord putR = dao.getStackLayerInfoWithPk(sPutLoc.substring(0, 6),
																  sPutLoc.substring(6, 8),
																  sPutLoc.substring(8,10));
			    	String sPutXPosition = "";
			    	String sPutYPosition = "";
			    	
			    	if(putR != null){
			    		sPutXPosition = StringHelper.evl(putR.getFieldString("STACK_LAYER_X_AXIS"), "");					
						sPutYPosition = StringHelper.evl(putR.getFieldString("STACK_LAYER_Y_AXIS"), "");
					}else{
						/*
		    			 * 설비정보는 권상시 적치단정보를 삭제하기 때문에 좌표정보를 가져올 수 없다.
		    			 * 따라서, 적치열정보를 가져온다.
		    			 */
						JDTORecord putSubR = dao.getStackColInfoWithPk(sPutLoc.substring(0, 6));
						if(putSubR != null){
							sPutXPosition = StringHelper.evl(putSubR.getFieldString("STACK_COL_RULE_X_AXIS"), "");					
							sPutYPosition = StringHelper.evl(putSubR.getFieldString("STACK_COL_RULE_Y_AXIS"), "");
						}	
					}
					UnLoadXPosition	= sPutXPosition;
					UnLoadYPosition	= sPutYPosition;
		    	}
	    	}
			
			sMsg.append(YmCommonUtil.FillToString(TC				,iTC));
			sMsg.append(YmCommonUtil.FillToString(Spare1			,iSpare1));
			sMsg.append(YmCommonUtil.FillToString(Spare2			,iSpare2));
			sMsg.append(YmCommonUtil.FillToString(OccurDate			,iOccurDate));
			sMsg.append(YmCommonUtil.FillToString(Spare3			,iSpare3));
			sMsg.append(YmCommonUtil.FillToString(LoadAdd			,iLoadAdd));
			sMsg.append(YmCommonUtil.FillToString(UnLoadAdd			,iUnLoadAdd));
			sMsg.append(YmCommonUtil.FillToString(GroupInfor		,iGroupInfor));
			sMsg.append(YmCommonUtil.FillToString(GoodsId			,iGoodsId));
			sMsg.append(YmCommonUtil.FillToString(CoilNo			,iCoilNo));
			sMsg.append(YmCommonUtil.FillToString(ProduceNo			,iProduceNo));
			sMsg.append(YmCommonUtil.FillToNumber(T					,iT));
			sMsg.append(YmCommonUtil.FillToNumber(Width				,iWidth));
			sMsg.append(YmCommonUtil.FillToNumber(Len				,iLen));
			sMsg.append(YmCommonUtil.FillToNumber(OutsideDia		,iOutsideDia));
			sMsg.append(YmCommonUtil.FillToNumber(Wt				,iWt));
			sMsg.append(YmCommonUtil.FillToString(Spare4			,iSpare4));
			sMsg.append(YmCommonUtil.FillToString(Spare5			,iSpare5));
			sMsg.append(YmCommonUtil.FillToNumber(LoadXPosition		,iLoadXPosition));
			sMsg.append(YmCommonUtil.FillToNumber(LoadYPosition		,iLoadYPosition));
			sMsg.append(YmCommonUtil.FillToNumber(UnLoadXPosition	,iUnLoadXPosition));
			sMsg.append(YmCommonUtil.FillToNumber(UnLoadYPosition	,iUnLoadYPosition));
			
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return sMsg.toString();
	}
	
	
	/**
	 * 임가공 PIDEV
     * CRANE 에 전송할 BACK UP실적 전문MESSAGE를 구성한다.
     *
	 *	1	전문코드			C	7	전문코드	THHC140
	 *	2	SPARE				C	4	SPARE
	 *	3	SPARE				C	4	SPARE
	 *	4	발생일시			C	12	발생일시
	 *	5	SPARE				C	3	SPARE
	 *	6	권상ADDRESS			C	8	권상ADDRESS
	 *	7	권하ADDRESS			C	8	권하ADDRESS
	 *	8	군					C	1	군
	 *	9	제품구분			C	2	제품구분
	 *	10	COILNO				C	10	COILNO
	 *	11	제작번호행번		C	13	제작번호행번
	 *	12	두께				C	5	두께
	 *	13	폭 					C	5	폭 
	 *	14	길이				C	5	길이
	 *	15	외경				C	5	외경
	 *	16	중량				C	5	중량
	 *	17	SPARE				C	12	SPARE
	 *	18	SPARE				C	4	SPARE
	 *	19	권상X축물리ADDRESS	C	6	권상X축물리ADDRESS
	 *	20	권상Y축물리ADDRESS	C	6	권상Y축물리ADDRESS
	 *	21	권하X축물리ADDRESS	C	6	권하X축물리ADDRESS
	 *	22	권하Y축물리ADDRESS	C	6	권하Y축물리ADDRESS      
	 * 
     * @param schInfo : SCHEDULE INFO                             
     *
     * @return
     * @throws 
     */	 
	private String setCraneACoilBackUpMsgInfoPI(String sStockId,
											  String sUpLoc,
											  String sPutLoc){
		
		StringBuffer sMsg = new StringBuffer();
		
		String TC				= ""; //07
		String Spare1			= ""; //04
		String Spare2			= ""; //04
		String OccurDate		= ""; //12
		String Spare3			= ""; //03
		String LoadAdd			= ""; //08
		String UnLoadAdd		= ""; //08
		String GroupInfor		= ""; //01
		String GoodsId			= ""; //02
		String CoilNo			= ""; //10
		String ProduceNo		= ""; //13
		String T				= ""; //05
		String Width			= ""; //05
		String Len				= ""; //05
		String OutsideDia		= ""; //05
		String Wt				= ""; //05
		String Spare4			= ""; //12
		String Spare5			= ""; //04
		String LoadXPosition	= ""; //06
		String LoadYPosition	= ""; //06
		String UnLoadXPosition	= ""; //06
		String UnLoadYPosition	= ""; //06
		String ZoneGp			= "";
		
		int	iTC					=  7;
		int iSpare1				=  4;
		int iSpare2				=  4;
		int iOccurDate			= 12;
		int iSpare3				=  3;
		int iLoadAdd			=  8;
		int iUnLoadAdd			=  8;
		int iGroupInfor			=  1;
		int iGoodsId			=  2;
		int iCoilNo				= 10;
		int iProduceNo			= 13;
		int iT					=  5;
		int iWidth				=  5;
		int iLen				=  5;
		int iOutsideDia			=  5;
		int iWt					=  5;
		int iSpare4				= 12;
		int iSpare5				=  4;
		int iLoadXPosition		=  6;
		int iLoadYPosition		=  6;
		int iUnLoadXPosition	=  6;
		int iUnLoadYPosition	=  6;
						   
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			/*
			 *	1.	TC CODE 
			 */
				TC	= YmCommonConst.TC_THHC140;
			/*
			 *	2.	발생일자
			 */
			{ 
				OccurDate		= YmCommonUtil.getCurDate("yyMMddHHmmss");
			} 
			/*
			 *	3.	권상번지
			 */ 
			{ 
				if(sUpLoc.indexOf("TR") != -1){
					LoadAdd		= YmCommonUtil.setLegacyPositionWithCurTrPI(sUpLoc,sStockId);
				}else{
					LoadAdd		= YmCommonUtil.setLegacyPositionWithCur(sUpLoc);
				}
			} 
			/*
			 *	4.	권하번지
			 */
			{ 
				if(sPutLoc.indexOf("TR") != -1){
					UnLoadAdd	= YmCommonUtil.setLegacyPositionWithCurTrPI(sPutLoc,sStockId);
				}else{
					UnLoadAdd	= YmCommonUtil.setLegacyPositionWithCur(sPutLoc);
				}
			}			
			/*
			 *	5.	군정보
			 */
			{ 
				GroupInfor	= "";
			}	
			/*
			 *	6.	제품구분
			 *		1자리-	1:입고재	2:보류재	3:장기재	4:SCRAP	5:차공정재
			 *		2자리-	1:냉각재	2:HFL		3:SPM
			 */
			{ 
				GoodsId	= "";
			}				
			/*
			 *	7.	코일번호
			 */
			{ 
				CoilNo	= sStockId;
			}	
			
			/*
			 *	8.	코일 기본정보 
			 *		-	제작번호/행번
			 *		-	두께	CHAR	5	㎜	Coil 공통 두께 * 1000
			 *		-	폭		CHAR	5	㎜	Coil 공통 폭에 정수만 가져와서 5자리로 만듦
			 *		-	길이	CHAR	5	Cm	Coil 공통 길이 * 10
			 *		-	외경	CHAR	5	㎜	Coil 공통 그대로
			 *		-	중량	CHAR	5	Kg	Coil 공통 그대로
			 */
			{ 
				JDTORecord cInfo = dao.getCoilCommonInfo(CoilNo);
		    		
	    		if(cInfo != null){
	    			
	    			ProduceNo	= YmCommonUtil.FillToString(StringHelper.evl(cInfo.getFieldString("제작번호"), ""),10)+
	    						  StringHelper.evl(cInfo.getFieldString("제작행번"), "");
					T 			= Double.valueOf((Double.parseDouble(StringHelper.evl(cInfo.getFieldString("코일두께"), "0")) * 1000) + "").longValue()+"";
					Width 		= Double.valueOf(StringHelper.evl(cInfo.getFieldString("코일폭"), "0")).longValue()+ "";
					Len 		= (Long.parseLong(StringHelper.evl(cInfo.getFieldString("코일길이"), "0"))* 10) + "";
					OutsideDia	= StringHelper.evl(cInfo.getFieldString("코일외경"), ""); 
					Wt			= StringHelper.evl(cInfo.getFieldString("코일중량"), ""); 
					ZoneGp		= StringHelper.evl(cInfo.getFieldString("YD_ZONE_GP"), ""); 
	    		}
			}
			
			
			//목적존이 존재 하는 경우 제품구분 항목에 넣어 L2차상국 화면에 표기를 해준다.
			if(!"".equals(ZoneGp)){
				GoodsId =ZoneGp ;
			}
			
			/*
			 *	9.	권상,권하 X Y 좌표 
			 */
			{
				if(sUpLoc.length() == 10){
					JDTORecord upR = dao.getStackLayerInfoWithPk(sUpLoc.substring(0, 6),
																 sUpLoc.substring(6, 8),
																 sUpLoc.substring(8,10));
			    	String sUpXPosition = "";
			    	String sUpYPosition = "";
			    	
			    	if(upR != null){
			    		sUpXPosition = StringHelper.evl(upR.getFieldString("STACK_LAYER_X_AXIS"), "");					
						sUpYPosition = StringHelper.evl(upR.getFieldString("STACK_LAYER_Y_AXIS"), "");
					}else{
						/*
		    			 * 설비정보는 권상시 적치단정보를 삭제하기 때문에 좌표정보를 가져올 수 없다.
		    			 * 따라서, 적치열정보를 가져온다.
		    			 */
						JDTORecord upSubR = dao.getStackColInfoWithPk(sUpLoc.substring(0, 6));
						if(upSubR != null){
							sUpXPosition = StringHelper.evl(upSubR.getFieldString("STACK_COL_RULE_X_AXIS"), "");					
							sUpYPosition = StringHelper.evl(upSubR.getFieldString("STACK_COL_RULE_Y_AXIS"), "");
						}
					}
					LoadXPosition	= sUpXPosition;
					LoadYPosition	= sUpYPosition;
		    	}
		    	

		    	if(sPutLoc.length() == 10){
		    		JDTORecord putR = dao.getStackLayerInfoWithPk(sPutLoc.substring(0, 6),
																  sPutLoc.substring(6, 8),
																  sPutLoc.substring(8,10));
			    	String sPutXPosition = "";
			    	String sPutYPosition = "";
			    	
			    	if(putR != null){
			    		sPutXPosition = StringHelper.evl(putR.getFieldString("STACK_LAYER_X_AXIS"), "");					
						sPutYPosition = StringHelper.evl(putR.getFieldString("STACK_LAYER_Y_AXIS"), "");
					}else{
						/*
		    			 * 설비정보는 권상시 적치단정보를 삭제하기 때문에 좌표정보를 가져올 수 없다.
		    			 * 따라서, 적치열정보를 가져온다.
		    			 */
						JDTORecord putSubR = dao.getStackColInfoWithPk(sPutLoc.substring(0, 6));
						if(putSubR != null){
							sPutXPosition = StringHelper.evl(putSubR.getFieldString("STACK_COL_RULE_X_AXIS"), "");					
							sPutYPosition = StringHelper.evl(putSubR.getFieldString("STACK_COL_RULE_Y_AXIS"), "");
						}	
					}
					UnLoadXPosition	= sPutXPosition;
					UnLoadYPosition	= sPutYPosition;
		    	}
	    	}
			
			sMsg.append(YmCommonUtil.FillToString(TC				,iTC));
			sMsg.append(YmCommonUtil.FillToString(Spare1			,iSpare1));
			sMsg.append(YmCommonUtil.FillToString(Spare2			,iSpare2));
			sMsg.append(YmCommonUtil.FillToString(OccurDate			,iOccurDate));
			sMsg.append(YmCommonUtil.FillToString(Spare3			,iSpare3));
			sMsg.append(YmCommonUtil.FillToString(LoadAdd			,iLoadAdd));
			sMsg.append(YmCommonUtil.FillToString(UnLoadAdd			,iUnLoadAdd));
			sMsg.append(YmCommonUtil.FillToString(GroupInfor		,iGroupInfor));
			sMsg.append(YmCommonUtil.FillToString(GoodsId			,iGoodsId));
			sMsg.append(YmCommonUtil.FillToString(CoilNo			,iCoilNo));
			sMsg.append(YmCommonUtil.FillToString(ProduceNo			,iProduceNo));
			sMsg.append(YmCommonUtil.FillToNumber(T					,iT));
			sMsg.append(YmCommonUtil.FillToNumber(Width				,iWidth));
			sMsg.append(YmCommonUtil.FillToNumber(Len				,iLen));
			sMsg.append(YmCommonUtil.FillToNumber(OutsideDia		,iOutsideDia));
			sMsg.append(YmCommonUtil.FillToNumber(Wt				,iWt));
			sMsg.append(YmCommonUtil.FillToString(Spare4			,iSpare4));
			sMsg.append(YmCommonUtil.FillToString(Spare5			,iSpare5));
			sMsg.append(YmCommonUtil.FillToNumber(LoadXPosition		,iLoadXPosition));
			sMsg.append(YmCommonUtil.FillToNumber(LoadYPosition		,iLoadYPosition));
			sMsg.append(YmCommonUtil.FillToNumber(UnLoadXPosition	,iUnLoadXPosition));
			sMsg.append(YmCommonUtil.FillToNumber(UnLoadYPosition	,iUnLoadYPosition));
			
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return sMsg.toString();
	}
	
	
	/**
	 *  전문코드			TC					CHAR	07 전문코드                  CN1BP01	
	 *  발생일자			Date				CHAR	10 발생일자                  YYYY-MM-DD
	 *  발생시간			Time				CHAR	08 발생시간                  HH-MM-SS
	 *  전문구분			Form				CHAR	01 전문구분                  I  : Initialize, U : Update,D : Delete,   R : Re-request
	 *  전문길이			Message_Length		CHAR	04 전문길이                  	
	 *  CRANE 번호		CraneNo				CHAR	06 CRANE번호                 YARD구분(1)+동구분(1)+설비구분:CR(2)+CR NO(2)	
	 *  처리구분			ProcessId			CHAR	01 처리구분                  SPACE:자동, B:화면
	 *  운전모드			DriveMode			CHAR	01 운전모드                  0:ON LINE, 1:OFF LINE
	 *  고장/복구			TroubleRecovery		CHAR	01 고장복구                  0:정상, 1:고장
	 *  작업지시 순번		WorkOrderSeq		CHAR	02 작업지시순번              00 ~ 99
	 *  마지막 지시 구분	LastOrderId			CHAR	01 마지막지시구분            SPACE, ‘E’
	 *  작업지시 구분		WorkOrderId			CHAR	01 작업지시구분              W:대기, U:권상지시, P:권하지시
	 *  SCHEDULE CODE		SchCode				CHAR	04 SCHEDULECODE              LEVEL3 CODE
	 *  SCHEDULE CODE 명칭	SchCodeName			CHAR	20 SCHEDULECODE명칭          	
	 *  주작업 구분			MainWorkId			CHAR	02 주작업구분                01: 주작업. 02: Dummy 작업
	 *  대차 번호			TCNo				CHAR	04 대차번호                  설비구분:TC(2)+TC NO(2)
	 *  대차작업 정지위치	TCWorkStopPosition	CHAR	06 대차작업정지위치최종목적지YARD구분(1)+동구분(1)+SPAN(2)+열 NO(2)	
	 *  차량번호			CarNo				CHAR	12 차량번호                  	
	 *  적재매수			StackCount			CHAR	02 적재매수                  	
	 *  잔여매수			RemainCount			CHAR	02 잔여매수                  	
	 *  권상번지			UpAddress			CHAR	10 권상번지                  YARD(1) + 동(1) + SPAN(2) + 열(2) + 번지(2) + 단(2)	
	 *  권하번지			PutAddress			CHAR	10 권하번지                  	
	 *  권상 X 위치			UpXAddress			CHAR	06 권상X위치                 	
	 *  허용오차(+)			UpXPlusRange		CHAR	04 권상X허용오차P            	
	 *  허용오차(-)			UpXMinusRange		CHAR	04 권상X허용오차M            	
	 *  권상 Y 위치			UpYAddress			CHAR	06 권상Y위치                 	
	 *  허용오차(+)			UpYPlusRange		CHAR	04 권상Y허용오차P            	
	 *  허용오차(-)			UpYMinusRange		CHAR	04 권상Y허용오차M            	
	 *  권하 X 위치			PutXAddress			CHAR	06 권하X위치                 	
	 *  허용오차(+)			PutXPlusRange		CHAR	04 권하X허용오차P            	
	 *  허용오차(-)			PutXMinusRange		CHAR	04 권하X허용오차M            	
	 *  권하 Y 위치			PutYAddress			CHAR	06 권하Y위치                 	
	 *  허용오차(+)			PutYPlusRange		CHAR	04 권하Y허용오차P            	
	 *  허용오차(-)			PutYMinusRange		CHAR	04 권하Y허용오차M            	
	 *  Coil No				CoilNo				CHAR	10 CoilNo                    	
	 *  제품구분			ProductId			CHAR	02 제품구분                  SM:SLAB소재,SG:SLAB제품 CM:COIL소재,CG:COIL제품
	 *  차공정 코드			AfterProcessCode	CHAR	01 차공정코드                	
	 *  냉각상태 코드		CoolStatusCode		CHAR	01 냉각상태코드              	
	 *  지시구분			OrderId				CHAR	01 지시구분                  1:장입지시, 2:이송지시, 3:출하지시
	 *  지시번호			OrderNo				CHAR	10 지시번호                  	
	 *  CARD 번호			CradNo				CHAR	06 CARD번호                  	
	 *  군정보				GroupInfo			CHAR	01 군정보                    	
	 *  제작번호/행번		ProductNo			CHAR	13 제작번호행번              	
	 *	두께				Thick				CHAR	07	㎜	소수점3자리 (###.###)
	 *	폭					Width				CHAR	06	㎜	소수점1자리 (####.#)
	 *	길이				Length				CHAR	06	㎜	
	 *	외경				Outdia				CHAR	05	㎜	
	 *	중량				Weight				CHAR	05	Kg	
	 *  MESSAGE 1			Message1			CHAR	40 MESSAGE1                  	
	 *  MESSAGE 2			Message2			CHAR	40 MESSAGE2                  	
     * @param schInfo : SCHEDULE INFO
     *
     * @return
     * @throws 
     */	
	private String setCraneBCoilMsgInfo(String sSchId,
								   String sMsg1,
								   String sMputLoc,
								   String sTcGbn){
		
		StringBuffer sMsg = new StringBuffer();

		String TC					= "";//07
		String sDate				= "";//10
		String sTime				= "";//08
		String Form					= "";//01
		String Message_Length		= "";//04
		String CraneNo				= "";//06
		String ProcessId			= "";//01
		String DriveMode			= "";//01
		String TroubleRecovery		= "";//01
		String WorkOrderSeq			= "";//02
		String LastOrderId			= "";//01
		String WorkOrderId			= "";//01
		String SchCode				= "";//04
		String SchCodeName			= "";//20
		String MainWorkId			= "";//02
		String TCNo					= "";//04
		String TCWorkStopPosition	= "";//06
		String CarNo				= "";//12
		String StackCount			= "";//02
		String RemainCount			= "";//02
		String UpAddress			= "";//10
		String PutAddress			= "";//10
		String UpXAddress			= "";//06
		String UpXPlusRange			= "";//04
		String UpXMinusRange		= "";//04
		String UpYAddress			= "";//06
		String UpYPlusRange			= "";//04
		String UpYMinusRange		= "";//04
		String PutXAddress			= "";//06
		String PutXPlusRange		= "";//04
		String PutXMinusRange		= "";//04
		String PutYAddress			= "";//06
		String PutYPlusRange		= "";//04
		String PutYMinusRange		= "";//04
		String CoilNo				= "";//10
		String ProductId			= "";//02	
		String AfterProcessCode		= "";//01
		String CoolStatusCode		= "";//01
		String OrderId				= "";//01
		String OrderNo				= "";//10
		String CradNo				= "";//06
		String GroupInfo			= "";//01
		String ProductNo			= "";//13
		String Thick				= "";//07
		String Width				= "";//06
		String Length				= "";//06
		String OutDia				= "";//05
		String Weight				= "";//05
		String Message1				= "";//40
		String Message2				= "";//40
		
		int iTC					=  7;
		int iDate				= 10;
		int iTime				=  8;
		int iForm				=  1;
		int iMessage_Length		=  4;
		int iCraneNo			=  6;
		int iProcessId			=  1;
		int iDriveMode			=  1;
		int iTroubleRecovery	=  1;
		int iWorkOrderSeq		=  2;
		int iLastOrderId		=  1;
		int iWorkOrderId		=  1;
		int iSchCode			=  4;
		int iSchCodeName		= 20;
		int iMainWorkId			=  2;
		int iTCNo				=  4;
		int iTCWorkStopPosition	=  6;
		int iCarNo				= 12;
		int iStackCount			=  2;
		int iRemainCount		=  2;
		int iUpAddress			= 10;
		int iPutAddress			= 10;
		int iUpXAddress			=  6;
		int iUpXPlusRange		=  4;
		int iUpXMinusRange		=  4;
		int iUpYAddress			=  6;
		int iUpYPlusRange		=  4;
		int iUpYMinusRange		=  4;
		int iPutXAddress		=  6;
		int iPutXPlusRange		=  4;
		int iPutXMinusRange		=  4;
		int iPutYAddress		=  6;
		int iPutYPlusRange		=  4;
		int iPutYMinusRange		=  4;
		int iCoilNo				= 10;
		int iProductId			=  2;
		int iAfterProcessCode	=  1;
		int iCoolStatusCode		=  1;
		int iOrderId			=  1;
		int iOrderNo			= 10;
		int iCradNo				=  6;
		int iGroupInfo			=  1;
		int iProductNo			= 13;
		int iThick				=  7;
		int iWidth				=  6;
		int iLength				=  6;
		int iOutDia				=  5;
		int iWeight				=  5;
		int iMessage1			= 40;
		int iMessage2			= 40;
		int iTotalLength		=295;
		
		try{
			
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			if(sSchId.length() == 6){
				
				logger.println(LogLevel.DEBUG,this, "작업지시 전문발생 대기모드.");
				
				TC					= YmCommonConst.TC_CN1BP01;
				sDate				= YmCommonUtil.getCurDate("yyyy-MM-dd");
				sTime				= YmCommonUtil.getCurDate("HH-mm-ss");
				Form				= "I";
				Message_Length		= iTotalLength+"";
				CraneNo				= sSchId;
				
				/*
				 *	1.	운전모드
				 *		0:ONLINE	1:OFFLINE
				 *	2.	고장/복구
				 *		0:정상	1:고장
				 */
				{ 
					
					JDTORecord jtR = dao.getEquipInfoWithEquipNo(sSchId.substring(0,1),//sYdGp
															 	 sSchId.substring(4)); //sEquipNo
					
					String sWorkMode  = StringHelper.evl(jtR.getFieldString("WORK_MODE"),"");
					String sEquipStat = StringHelper.evl(jtR.getFieldString("EQUIP_STAT"),"");
					
//					DriveMode		= (YmCommonConst.WORK_MODE_O.equals(sWorkMode) ? "0" : 
//									  (YmCommonConst.WORK_MODE_C.equals(sWorkMode) ? "1" : ""));
					
					if(YmCommonConst.WORK_MODE_O.equals(sWorkMode)){
						DriveMode ="0";
					}else if(YmCommonConst.WORK_MODE_C.equals(sWorkMode)){
						DriveMode ="1";
					}else if(YmCommonConst.WORK_MODE_E.equals(sWorkMode)){
						DriveMode ="2";
					}else{
						DriveMode ="";
					}
					
					
					TroubleRecovery	= (YmCommonConst.WORK_MODE_O.equals(sEquipStat)? "0" : 
									  (YmCommonConst.WORK_MODE_C.equals(sEquipStat)? "1" : ""));
				}
				WorkOrderId			= "W";
				
				Message1			= sMsg1;
				
			}else{
				logger.println(LogLevel.DEBUG,this, "작업지시 전문발생 운행모드.");
				
				JDTORecord schInfo  = dao.getCraneSchInfo(sSchId);
				
				logger.println(LogLevel.DEBUG,this, "sSchId=" + sSchId);
		    	if(schInfo == null){
		    		logger.println(LogLevel.DEBUG,this, "작업지시 전문발생 스케쥴 정보가 존재안함.");
		    		return sMsg.toString();
		    	}
		    		    	
				String sYdGp 		= StringHelper.evl(schInfo.getFieldString("YD_GP"),"");
				String sBayGp 		= StringHelper.evl(schInfo.getFieldString("BAY_GP"),"");
				String sEquipKind	= YmCommonConst.EQUIP_KIND_CR;
				String sEquipNo 	= StringHelper.evl(schInfo.getFieldString("SCH_WORK_EQUIP_NO"),"");
				String sStockId		= StringHelper.evl(schInfo.getFieldString("STOCK_ID"),"");
				String sSchCode 	= StringHelper.evl(schInfo.getFieldString("SCH_WORK_KIND"),"");
				String sMainWorkYn 	= StringHelper.evl(schInfo.getFieldString("SCH_WORK_AID_YN"),"");
				String sUpLoc 		= StringHelper.evl(schInfo.getFieldString("CRANE_WORD_UP_LOC"),"");
				String sPutLoc 		= StringHelper.evl(schInfo.getFieldString("CRANE_WORD_PUT_LOC"),"");
				String sSchWorkStat = StringHelper.evl(schInfo.getFieldString("SCH_WORK_STAT"),"");
				String sSchJisiDate = StringHelper.evl(schInfo.getFieldString("WBOOK_SCH_ACT_DDTT"),"");
				ProcessId			= StringHelper.evl(schInfo.getFieldString("SPEC_ABBSYM_CHK"),"");
				/*
				 *	A.	최초 작업지시 시점에 작업지시일자를 셋팅한다.
				 */
				logger.println(LogLevel.DEBUG,this, "setCraneBCoilMsgInfo()>> A.최초 작업지시 시점에 작업지시일자를 셋팅한다.");
				{
					if("".equals(sSchJisiDate)){
						int iWrslt = dao.updateWbookSchActDdttSchInfo(sSchId,
																  	  YmCommonUtil.getStringYMDHMS());
					}											  	  
				}										  
				/*
				 *	수정작업 재지시 처리 
				 *	sMputLoc : 화면에서 입력받은 위치정보
				 */
				logger.println(LogLevel.DEBUG,this, "setCraneBCoilMsgInfo()>> 수정작업 재지시 처리:화면에서입력받은 위치정보("+sMputLoc);
				if(!"".equals(sMputLoc) &&
				   sMputLoc.length() == 10){
					
					sPutLoc = sMputLoc;
				}
				
				/*
				 *	0.	작업지시 전문발생 전 현 저장품 위치 체크
				 */
				logger.println(LogLevel.DEBUG,this, "setCraneBCoilMsgInfo()>> 작업지시 전문발생 전 현 저장품 위치 체크");
				{
					String sDbUpLoc		= "";
			    	
					JDTORecord layerRc = dao.getUpStackLayerListWithSchId(sSchId);
			    	
			    	if(layerRc != null){
			    		sDbUpLoc	= StringHelper.evl(layerRc.getFieldString("STACK_COL_GP"), "")
							    	+ StringHelper.evl(layerRc.getFieldString("STACK_BED_GP"), "")
							    	+ StringHelper.evl(layerRc.getFieldString("STACK_LAYER_GP"), "");
		    		}
		    		
		    		if(!"".equals(sDbUpLoc) &&
		    		   !sUpLoc.equals(sDbUpLoc)){
		    		   	logger.println(LogLevel.DEBUG,this, "작업지시 전 저장품 위치 바뀜.==");
		    		   	logger.println(LogLevel.DEBUG,this, "현 위치로 권상위치 작업지시 내림.==");
		    		   	
		    		   	sUpLoc = sDbUpLoc;
		    		   	
		    		   	// 스케쥴 정보 수정
		    		   	int iSeq = dao.updateUpLocInfoWithSchId(sSchId,
		    								   				    sDbUpLoc);
		    		}
		    	}			
				/*
				 *	1.	HEADER INFO
				 */
				logger.println(LogLevel.DEBUG,this, "setCraneBCoilMsgInfo()>>HEADER INFO");
				TC					= YmCommonConst.TC_CN1BP01;
				sDate				= YmCommonUtil.getCurDate("yyyy-MM-dd");
				sTime				= YmCommonUtil.getCurDate("HH-mm-ss");
				
				if("".equals(sTcGbn)){
					Form				= YmCommonConst.TC_WORK_I;
				}else{
					Form				= sTcGbn;
				}
				
				Message_Length		= iTotalLength+"";
				/*
				 *	2.	CRANE NO 
				 */
				logger.println(LogLevel.DEBUG,this, "setCraneBCoilMsgInfo()>>CRANE NO");
				{ 
					CraneNo			= sYdGp+sBayGp+sEquipKind+sEquipNo;
				}
				/*
				 *	3.	처리구분
				 *		SPACE:자동	B:화면
				 */
//				logger.println(LogLevel.DEBUG,this, "setCraneBCoilMsgInfo()>>처리구분");
//				{ 
//					ProcessId		= "";
//				}
				/*
				 *	4.	운전모드
				 *		0:ONLINE	1:OFFLINE
				 *	5.	고장/복구
				 *		0:정상	1:고장
				 */
				logger.println(LogLevel.DEBUG,this, "setCraneBCoilMsgInfo()>>운전모드");
				{ 
					JDTORecord jtR = dao.getEquipInfoWithEquipNo(sYdGp,
															 	 sEquipNo);
					
					String sWorkMode  = StringHelper.evl(jtR.getFieldString("WORK_MODE"),"");
					String sEquipStat = StringHelper.evl(jtR.getFieldString("EQUIP_STAT"),"");
					
//					DriveMode		= (YmCommonConst.WORK_MODE_O.equals(sWorkMode) ? "0" : 
//									  (YmCommonConst.WORK_MODE_C.equals(sWorkMode) ? "1" :""));
					
					if(YmCommonConst.WORK_MODE_O.equals(sWorkMode)){
						DriveMode ="0";
					}else if(YmCommonConst.WORK_MODE_C.equals(sWorkMode)){
						DriveMode ="1";
					}else if(YmCommonConst.WORK_MODE_E.equals(sWorkMode)){
						DriveMode ="2";
					}else{
						DriveMode ="";
					}
					
					
					TroubleRecovery	= (YmCommonConst.WORK_MODE_O.equals(sEquipStat)? "0" : 
									  (YmCommonConst.WORK_MODE_C.equals(sEquipStat)? "1" : ""));
				}
				/*
				 *	6.	작업지시순번
				 *		00 ~ 99
				 */
				logger.println(LogLevel.DEBUG,this, "setCraneBCoilMsgInfo()>>작업지시순번");
				{ 
					WorkOrderSeq		= "";
				}
				/*
				 *	8.	작업지시구분
				 *		W:대기	U:권상지시	P:권하지시
				 */
				logger.println(LogLevel.DEBUG,this, "setCraneBCoilMsgInfo()>>작업지시구분");
				{ 
					String sUpDownGbn = "W";
		    	
			    	if(YmCommonConst.SCH_WORK_STAT_1.equals(sSchWorkStat)){
			    		sUpDownGbn	= "U";
			    	}else if(YmCommonConst.SCH_WORK_STAT_2.equals(sSchWorkStat)){
			    		sUpDownGbn	= "P";
			    	}else if(YmCommonConst.SCH_WORK_STAT_3.equals(sSchWorkStat)){
			    		sUpDownGbn	= "P";
			    	}
			    	
					WorkOrderId		= sUpDownGbn;
				}
				/*
				 *	9.	스케쥴코드
				 */
				logger.println(LogLevel.DEBUG,this, "setCraneBCoilMsgInfo()>>스케쥴코드:"+sSchCode);
				{ 
					SchCode			= sSchCode;
				}
				/*
				 *	9.	스케쥴명칭
				 */				
				{ 
					ymCommonDAO dao  = ymCommonDAO.getInstance();
					String sQueryId  = "ym.common.dao.ymCommonDAO.getCodeToNameNEW";	   	
				   	JDTORecord comJr = dao.getCommonInfo(sQueryId,new Object[]{sStockId ,"YM104","3", sSchCode});
																			    
					SchCodeName		 = StringHelper.evl(comJr.getFieldString("CLASS2_NAME1"), "");
				}
				logger.println(LogLevel.DEBUG,this, "setCraneBCoilMsgInfo()>>스케쥴명칭:"+SchCodeName);
				/*
				 *	10.	주작업구분
				 */				
				{ 
					MainWorkId		= YmCommonUtil.getLegacyDataWithCur(sMainWorkYn);
				}
				logger.println(LogLevel.DEBUG,this, "setCraneBCoilMsgInfo()>>주작업구분:"+MainWorkId);
				/*
				 *	11.	대차번호
				 *		설비구분:TC(2)+TCNO(2)
				 */
				logger.println(LogLevel.DEBUG,this, "setCraneBCoilMsgInfo()>>대차번호");
				{ 
					if(sPutLoc.indexOf("TC") != -1){
						
						TCNo		= sPutLoc.substring(2);
					}
				}			
				/*
				 *	12.	대차작업정지위치
				 *		최종목적지:YARD구분(1)+동구분(1)+SPAN(2)+열NO(2)
				 */
				logger.println(LogLevel.DEBUG,this, "setCraneBCoilMsgInfo()>>대차작업정지위치");
				{ 
					if(sPutLoc.indexOf("TC") != -1){
						
						JDTORecord equipJr 	= dao.getEquipInfoWithEquipGp(sPutLoc.substring(0, 1)+ "X"+ sPutLoc.substring(2,6));
						TCWorkStopPosition	= StringHelper.evl(equipJr.getFieldString("CARUNLOAD_STOP_LOC"), "");
					}
				}			
				
				/*
				 *	18.	제품구분(SM:SLAB소재,SG:SLAB제품,CM:COIL소재,CG:COIL제품 )
				 */
				/*
				 *	13.	차량번호
				 */
				/*
				 *	21.	지시구분 (1:장입지시, 2:이송지시, 3:출하지시)
				 */
				/*
				 *	22.	지시번호
				 */
				/*
				 *	23.	CARD번호
				 */
				/*
				 *	14.	적재매수
				 */
				 /*
				 *	15.	잔여매수
				 */
				logger.println(LogLevel.DEBUG,this, "setCraneBCoilMsgInfo()>>제품구분");
				{ 
					
					JDTORecord stockJR = dao.getStockInfo(sStockId);
					
					ProductId	= StringHelper.evl(stockJR.getFieldString("STOCK_ITEM"), "");
						
					if(YmCommonConst.NEW_SCH_WORK_KIND_GVFL.equals(sSchCode)|| // COIL 제품출하상차
					   YmCommonConst.NEW_SCH_WORK_KIND_GVF1.equals(sSchCode)|| // COIL 제품출하상차
			    	   YmCommonConst.NEW_SCH_WORK_KIND_GVF2.equals(sSchCode)|| // COIL 제품출하상차
			    	   YmCommonConst.NEW_SCH_WORK_KIND_GTFL.equals(sSchCode)|| // COIL 제품출하상차
			    	   YmCommonConst.NEW_SCH_WORK_KIND_GTF1.equals(sSchCode)|| // Coil 제품출하상차
			    	   YmCommonConst.NEW_SCH_WORK_KIND_GTF2.equals(sSchCode)||    		    
			    	   YmCommonConst.NEW_SCH_WORK_KIND_GPFL.equals(sSchCode)|| // COIL 제품출하상차
			    	   YmCommonConst.NEW_SCH_WORK_KIND_GPF1.equals(sSchCode)|| // Coil 제품출하상차
			    	   YmCommonConst.NEW_SCH_WORK_KIND_GPF2.equals(sSchCode)||
					   YmCommonConst.NEW_SCH_WORK_KIND_GVML.equals(sSchCode)|| // COIL 제품이송상차
					   YmCommonConst.NEW_SCH_WORK_KIND_GVM2.equals(sSchCode)|| // COIL 제품이송상차
			   		   YmCommonConst.NEW_SCH_WORK_KIND_GVM3.equals(sSchCode)|| // COIL 제품이송상차		
					   YmCommonConst.NEW_SCH_WORK_KIND_GVMU.equals(sSchCode)|| // COIL 제품이송하차
					   YmCommonConst.NEW_SCH_WORK_KIND_GVM4.equals(sSchCode)|| // COIL 제품이송하차	 	
		   			   YmCommonConst.NEW_SCH_WORK_KIND_GVM5.equals(sSchCode)|| // COIL 제품이송하차	
					   YmCommonConst.NEW_SCH_WORK_KIND_CVML.equals(sSchCode)|| // COIL 소재이송상차
					   YmCommonConst.NEW_SCH_WORK_KIND_CVM2.equals(sSchCode)|| // COIL 소재이송상차
				   	   YmCommonConst.NEW_SCH_WORK_KIND_CVM3.equals(sSchCode)|| // COIL 소재이송상차
					   YmCommonConst.NEW_SCH_WORK_KIND_CVMU.equals(sSchCode)|| // COIL 소재이송하차
					   YmCommonConst.NEW_SCH_WORK_KIND_CVM4.equals(sSchCode)|| // COIL 소재이송하차	 
		  		   	   YmCommonConst.NEW_SCH_WORK_KIND_CVM5.equals(sSchCode)){ // COIL 소재이송하차	 	
					   	
					   	JDTORecord carR = dao.getDmCarInfo(sStockId);
				    	if(carR != null){
				    		CarNo		= StringHelper.evl(carR.getFieldString("CAR_NO"), "");
				    	}
				    	
				    	if(YmCommonConst.NEW_SCH_WORK_KIND_GVFL.equals(sSchCode)|| //COIL 제품출하상차
						   YmCommonConst.NEW_SCH_WORK_KIND_GVF1.equals(sSchCode)|| //COIL 제품출하상차
				    	   YmCommonConst.NEW_SCH_WORK_KIND_GVF2.equals(sSchCode)||
				    	   YmCommonConst.NEW_SCH_WORK_KIND_GTFL.equals(sSchCode)|| // COIL 제품출하상차
				    	   YmCommonConst.NEW_SCH_WORK_KIND_GTF1.equals(sSchCode)|| // Coil 제품출하상차
				    	   YmCommonConst.NEW_SCH_WORK_KIND_GTF2.equals(sSchCode)||    		    
				    	   YmCommonConst.NEW_SCH_WORK_KIND_GPFL.equals(sSchCode)|| // COIL 제품출하상차
				    	   YmCommonConst.NEW_SCH_WORK_KIND_GPF1.equals(sSchCode)|| // Coil 제품출하상차
				    	   YmCommonConst.NEW_SCH_WORK_KIND_GPF2.equals(sSchCode)
				    		){ //COIL 제품출하상차
					       
					   		OrderId	= "3";
					   		
					   	}else if(YmCommonConst.NEW_SCH_WORK_KIND_GVML.equals(sSchCode)|| // COIL 제품이송상차
					   	         YmCommonConst.NEW_SCH_WORK_KIND_GVM2.equals(sSchCode)|| // COIL 제품이송상차
			   		   			 YmCommonConst.NEW_SCH_WORK_KIND_GVM3.equals(sSchCode)|| // COIL 제품이송상차		
								 YmCommonConst.NEW_SCH_WORK_KIND_GVMU.equals(sSchCode)|| // COIL 제품이송하차
								 YmCommonConst.NEW_SCH_WORK_KIND_GVM4.equals(sSchCode)|| // COIL 제품이송하차	 	
		   			   			 YmCommonConst.NEW_SCH_WORK_KIND_GVM5.equals(sSchCode)|| // COIL 제품이송하차	
								 YmCommonConst.NEW_SCH_WORK_KIND_CVML.equals(sSchCode)|| // COIL 소재이송상차
								 YmCommonConst.NEW_SCH_WORK_KIND_CVM2.equals(sSchCode)|| // COIL 소재이송상차
					   	   		 YmCommonConst.NEW_SCH_WORK_KIND_CVM3.equals(sSchCode)|| // COIL 소재이송상차
								 YmCommonConst.NEW_SCH_WORK_KIND_CVMU.equals(sSchCode)){ // COIL 소재이송하차
					   	
					   		OrderId	= "2";
					   	}
					   	
				    	OrderNo	= StringHelper.evl(stockJR.getFieldString("TRANS_WORD_NO"), "");
				    	CradNo	= StringHelper.evl(stockJR.getFieldString("CAR_CARD_NO"), "");
				    	
				    	{
					    	List 	   dmList  = null; // Card No,운송지시일자,순번이 같은 저장품정보
							JDTORecord dmRc    = null; // 저장품정보
							String 	   sSmt    = "";   // 저장품이동조건
							int		   iDmSize = 0;    // Card No,운송지시일자,순번이 같은 저장품 갯수
							int		   iDmFns  = 0;    // 상,하차완료인 저장품 갯수	
								
							dmList  = dao.getYmDmCommonInfo2(sStockId);
							iDmSize = dmList.size();
							
							if(iDmSize>0){
								dmRc = (JDTORecord)dmList.get(0);
								StackCount		= StringHelper.evl(dmRc.getFieldString("STACK_COUNT"), "");
								RemainCount		= StringHelper.evl(dmRc.getFieldString("REMAIN_COUNT"), "");
							}
							
//							for(int inx = 0; inx < dmList.size() ; inx++){
//						 	 	dmRc = (JDTORecord)dmList.get(inx);
//						 	 	sSmt = StringHelper.evl(dmRc.getFieldString("STOCK_MOVE_TERM"), "");
//						 	 	
//						 	 	if(YmCommonConst.NEW_STOCK_MOVE_TERM_VL.equals(sSmt)|| // 상차완료
//						 	 	   YmCommonConst.NEW_STOCK_MOVE_TERM_E1.equals(sSmt)|| // 이송완료	
//						 	 	   YmCommonConst.NEW_STOCK_MOVE_TERM_CC.equals(sSmt)|| // 정정작업대기	
//						 	 	   YmCommonConst.NEW_STOCK_MOVE_TERM_KG.equals(sSmt)){ // 출하작업지시대기 
//									
//									iDmFns++;
//						 	 	}
//						 	}
//						 	StackCount		=  iDmSize + "";
//						 	RemainCount		= (iDmSize - iDmFns)+"";
						}	
					}	
				}	
				
				logger.println(LogLevel.DEBUG,this, "차량번호 확인 @@@@@@@@@: "+CarNo);	
			
				/*
				 *	16.	권상,권하 X Y 좌표 및 오차범위
				 */
				logger.println(LogLevel.DEBUG,this, "setCraneBCoilMsgInfo()>>권상,권하 X Y 좌표 및 오차범위");
				{ 
					UpAddress = sUpLoc;
					PutAddress= sPutLoc;
					
					String sUpXLeft 	= "";
					String sUpXRight 	= "";
					String sUpYUp 		= "";
					String sUpYDown 	= "";
					 
					String sPutXLeft 	= "";
					String sPutXRight 	= "";
					String sPutYUp 		= "";
					String sPutYDown 	= ""; 
					
					JDTORecord ruleUpX = dao.getStackRuleInfo_002(sUpLoc.substring(0, 6),
																  YmCommonConst.STACK_RULE_CD_XCD);
					if(ruleUpX != null){
						sUpXLeft 	= StringHelper.evl(ruleUpX.getFieldString("STACK_RULE_MIN"), "");
						sUpXRight 	= StringHelper.evl(ruleUpX.getFieldString("STACK_RULE_MAX"), "");
					}
					JDTORecord ruleUpY = dao.getStackRuleInfo_002(sUpLoc.substring(0, 6),
																  YmCommonConst.STACK_RULE_CD_YCD);
					if(ruleUpY != null){
						sUpYUp 		= StringHelper.evl(ruleUpY.getFieldString("STACK_RULE_MAX"), "");
						sUpYDown 	= StringHelper.evl(ruleUpY.getFieldString("STACK_RULE_MIN"), "");
					}
					
					boolean isHysco = false;
					
					logger.println(LogLevel.DEBUG,this, "setCraneBCoilMsgInfo()>>PutLoc "+sPutLoc);
					
					String sUpUsageCd = YmCommonUtil.getStackColInfoWithPk(sPutLoc.substring(0, 6));
					
					if(YmCommonConst.STACK_COL_USAGE_CD_CX.equals(sUpUsageCd)){
						
						JDTORecord equipJr = dao.getToEquipState(sPutLoc.substring(0, 6));
						
						String sStopLoc = StringHelper.evl(equipJr.getFieldString("CARUNLOAD_STOP_LOC"), "");
						
						if(YmCommonConst.HYSCO_3HTC02.equals(sStopLoc)){
							isHysco = true;
						}
					}
					/*
					 *	HYSCO 대차 출하 물량일 경우 대차에 권하 오차범위는 
					 *	3으로 셋팅한다.
					 */
					if(isHysco){
							sPutXLeft 	= "3";
							sPutXRight 	= "3";
					}else{	
						JDTORecord rulePutX = dao.getStackRuleInfo_002(sPutLoc.substring(0, 6),
																	   YmCommonConst.STACK_RULE_CD_XCD);
						if(rulePutX != null){
							sPutXLeft 	= StringHelper.evl(rulePutX.getFieldString("STACK_RULE_MIN"), "");
							sPutXRight 	= StringHelper.evl(rulePutX.getFieldString("STACK_RULE_MAX"), "");
						}
					}
					
					JDTORecord rulePutY = dao.getStackRuleInfo_002(sPutLoc.substring(0, 6),
																   YmCommonConst.STACK_RULE_CD_YCD);
					if(rulePutY != null){
						sPutYUp 	= StringHelper.evl(rulePutY.getFieldString("STACK_RULE_MAX"), "");
						sPutYDown 	= StringHelper.evl(rulePutY.getFieldString("STACK_RULE_MIN"), "");
					}
					logger.println(LogLevel.DEBUG,this, "setCraneBCoilMsgInfo()>>UpAddress "+UpAddress);			
					if(UpAddress.length() == 10){
						JDTORecord upR = dao.getStackLayerInfoWithPk(UpAddress.substring(0, 6),
																	 UpAddress.substring(6, 8),
																	 UpAddress.substring(8,10));
				    	String sUpXPosition = "";
				    	String sUpYPosition = "";
				    	
				    	if(upR != null){
				    		sUpXPosition = StringHelper.evl(upR.getFieldString("STACK_LAYER_X_AXIS"), "");					
							sUpYPosition = StringHelper.evl(upR.getFieldString("STACK_LAYER_Y_AXIS"), "");
						}else{
							/*
			    			 * 설비정보는 권상시 적치단정보를 삭제하기 때문에 좌표정보를 가져올 수 없다.
			    			 * 따라서, 적치열정보를 가져온다.
			    			 */
							JDTORecord upSubR = dao.getStackColInfoWithPk(sUpLoc.substring(0, 6));
							if(upSubR != null){
								sUpXPosition = StringHelper.evl(upSubR.getFieldString("STACK_COL_RULE_X_AXIS"), "");					
								sUpYPosition = StringHelper.evl(upSubR.getFieldString("STACK_COL_RULE_Y_AXIS"), "");
							}
						}
						UpXAddress			= sUpXPosition;
						UpXPlusRange		= sUpXLeft;
						UpXMinusRange		= sUpXRight;
						UpYAddress			= sUpYPosition;
						UpYPlusRange		= sUpYUp;
						UpYMinusRange		= sUpYDown;
			    	}
					logger.println(LogLevel.DEBUG,this, "setCraneBCoilMsgInfo()>>PutAddress "+PutAddress);
			    	if(PutAddress.length() == 10){
			    		JDTORecord putR = dao.getStackLayerInfoWithPk(PutAddress.substring(0, 6),
																	  PutAddress.substring(6, 8),
																	  PutAddress.substring(8,10));
				    	String sPutXPosition = "";
				    	String sPutYPosition = "";
				    	
				    	if(putR != null){
				    		sPutXPosition = StringHelper.evl(putR.getFieldString("STACK_LAYER_X_AXIS"), "");					
							sPutYPosition = StringHelper.evl(putR.getFieldString("STACK_LAYER_Y_AXIS"), "");
						}else{
							/*
			    			 * 설비정보는 권상시 적치단정보를 삭제하기 때문에 좌표정보를 가져올 수 없다.
			    			 * 따라서, 적치열정보를 가져온다.
			    			 */
							JDTORecord putSubR = dao.getStackColInfoWithPk(sPutLoc.substring(0, 6));
							if(putSubR != null){
								sPutXPosition = StringHelper.evl(putSubR.getFieldString("STACK_COL_RULE_X_AXIS"), "");					
								sPutYPosition = StringHelper.evl(putSubR.getFieldString("STACK_COL_RULE_Y_AXIS"), "");
							}
			    		}
			    		PutXAddress			= sPutXPosition;
						PutXPlusRange		= sPutXLeft;
						PutXMinusRange		= sPutXRight;
						PutYAddress			= sPutYPosition;
						PutYPlusRange		= sPutYUp;
						PutYMinusRange		= sPutYDown;
			    	}
		    	} 
				/*
				 *	17.	코일번호
				 */
				logger.println(LogLevel.DEBUG,this, "setCraneBCoilMsgInfo()>>코일번호");
				{ 
					CoilNo	= sStockId;
				}	
				
				/*
				 *	19.	차공정코드
				 */
				{ 
					AfterProcessCode	= "";
				}				
				/*
				 *	20.	냉각상태코드
				 */
				{ 
					CoolStatusCode	= "";
				}	
				/*
				 *	24.	군정보
				 */
				{ 
					GroupInfo	= "";
				}	
				/*
				 *	25.	코일 기본정보 
				 *		-	제작번호/행번
				 *		-	두께	Thick	CHAR	07		㎜	소수점3자리 (###.###)
				 *		-	폭		Width	CHAR	06		㎜	소수점1자리 (####.#)
				 *		-	길이	Length	CHAR	06		㎜	Coil 공통 길이 * 100
				 *		-	외경	Outdia	CHAR	05		㎜	Coil 공통 그대로
				 *		-	중량	Weight	CHAR	05		Kg	Coil 공통 그대로
				 */
				logger.println(LogLevel.DEBUG,this, "setCraneBCoilMsgInfo()>>코일 기본정보");
				{ 
					JDTORecord cInfo = dao.getCoilCommonInfo(CoilNo);
			    		
		    		if(cInfo != null){
		    			
		    			ProductNo	= YmCommonUtil.FillToString(StringHelper.evl(cInfo.getFieldString("제작번호"), ""),10)+
		    						  StringHelper.evl(cInfo.getFieldString("제작행번"), "");
		    			Thick		= StringHelper.replaceStr(
									  YmCommonUtil.format(
									  StringHelper.evl(cInfo.getFieldString("코일두께"), ""),3,3),".",""); 
						Width		= StringHelper.replaceStr(
									  YmCommonUtil.format(
									  StringHelper.evl(cInfo.getFieldString("코일폭"), ""),4,1),".",""); 
						Length		= StringHelper.evl(cInfo.getFieldString("코일길이"), "");
						OutDia		= StringHelper.evl(cInfo.getFieldString("코일외경"), ""); 
						Weight		= StringHelper.evl(cInfo.getFieldString("코일중량"), ""); 
					}
				}				
				/*
				 *	26.	MESSAGE
				 */
				{ 
					Message1			= sMsg1;
					Message2			= "";
				}	
				/*
				 *	7.	마지막지시구분
				 *		SPACE	E
				 */
				logger.println(LogLevel.DEBUG,this, "setCraneBCoilMsgInfo()>>마지막지시구분");
				{
					
					if(YmCommonConst.NEW_SCH_WORK_KIND_GVFL.equals(sSchCode)|| // COIL 제품출하상차
					   YmCommonConst.NEW_SCH_WORK_KIND_GVF1.equals(sSchCode)|| // COIL 제품출하상차
			    	   YmCommonConst.NEW_SCH_WORK_KIND_GVF2.equals(sSchCode)|| // COIL 제품출하상차	
			    	   YmCommonConst.NEW_SCH_WORK_KIND_GTFL.equals(sSchCode)|| // COIL 제품출하상차
			    	   YmCommonConst.NEW_SCH_WORK_KIND_GTF1.equals(sSchCode)|| // Coil 제품출하상차
			    	   YmCommonConst.NEW_SCH_WORK_KIND_GTF2.equals(sSchCode)||    		    
			    	   YmCommonConst.NEW_SCH_WORK_KIND_GPFL.equals(sSchCode)|| // COIL 제품출하상차
			    	   YmCommonConst.NEW_SCH_WORK_KIND_GPF1.equals(sSchCode)|| // Coil 제품출하상차
			    	   YmCommonConst.NEW_SCH_WORK_KIND_GPF2.equals(sSchCode)||
					   YmCommonConst.NEW_SCH_WORK_KIND_GVML.equals(sSchCode)|| // COIL 제품이송상차
					   YmCommonConst.NEW_SCH_WORK_KIND_GVM2.equals(sSchCode)|| // COIL 제품이송상차
			   		   YmCommonConst.NEW_SCH_WORK_KIND_GVM3.equals(sSchCode)|| // COIL 제품이송상차		
					   YmCommonConst.NEW_SCH_WORK_KIND_GVMU.equals(sSchCode)|| // COIL 제품이송하차
					   YmCommonConst.NEW_SCH_WORK_KIND_GVM4.equals(sSchCode)|| // COIL 제품이송하차	 	
		   			   YmCommonConst.NEW_SCH_WORK_KIND_GVM5.equals(sSchCode)|| // COIL 제품이송하차	
					   YmCommonConst.NEW_SCH_WORK_KIND_CVML.equals(sSchCode)|| // COIL 소재이송상차
					   YmCommonConst.NEW_SCH_WORK_KIND_CVM2.equals(sSchCode)|| // COIL 소재이송상차
				   	   YmCommonConst.NEW_SCH_WORK_KIND_CVM3.equals(sSchCode)|| // COIL 소재이송상차
					   YmCommonConst.NEW_SCH_WORK_KIND_CVMU.equals(sSchCode)|| // COIL 소재이송하차
					   YmCommonConst.NEW_SCH_WORK_KIND_CVM4.equals(sSchCode)|| // COIL 소재이송하차	 
		  		   	   YmCommonConst.NEW_SCH_WORK_KIND_CVM5.equals(sSchCode)){ // COIL 소재이송하차	 	
	 					
					   	if("0".equals(RemainCount)){//잔여매수
							LastOrderId		= "E";
							logger.println(LogLevel.DEBUG,this, "마지막지시구분=>"+sSchCode);
						}else{
							LastOrderId		= "";
						}	 	
					} 
					
					
					String sUpUsageCd 	= YmCommonUtil.getStackColInfoWithPk(UpAddress.substring(0, 6));
					String sPutUsageCd 	= YmCommonUtil.getStackColInfoWithPk(PutAddress.substring(0, 6));
					
					/**
					 * 수조탱크 보급 마지막 지시구분 셋팅
					 */
					{
						if(YmCommonConst.STACK_COL_USAGE_CD_CW.equals(sPutUsageCd)){// 수냉탱크
							
							/**
							 *	1. PUT위치가 수조탱크 마지막 번지일 경우.
							 */	
							//쿼리 추가해서 리턴 2일 경우만...
							JDTORecord jcount  = dao.getCraneSchCoilCount(PutAddress.substring(0, 6));
							if("2".equals(StringHelper.evl(jcount.getFieldString("LCOUNT"),"0"))){
							//if(YmCommonConst.STACK_BED_GP_03.equals(PutAddress.substring(6, 8))){
								LastOrderId		= "E";
								logger.println(LogLevel.DEBUG,this, "마지막지시구분=>수조탱크 마지막 번지.");
							}
							
							
							if(YmCommonConst.STACK_COL_USAGE_CD_CE.equals(sUpUsageCd)){// 확장CONV
								/**
								 *	2. 확장콘베이어에 수조탱크 보급코일이 없을 경우.
								 */	 
								
								JDTORecord cTank = dao.getTankInCoilInfo(sBayGp);
								/* 
								 * 2007.06.18 이정훈
								 * 수냉 탱크 보급 Logic 변경
								 */
								//JDTORecord cTank = dao.getTankInCoilInfo_01(sBayGp);
								if(cTank != null){
									int iCount = cTank.getFieldInt("COUNT");
									
									logger.println(LogLevel.DEBUG,this, "마지막지시구분=>확장CONV COUNT="+iCount);
									
									if(iCount == 0||
									   iCount == 1){//자기자신
										LastOrderId		= "E";
										logger.println(LogLevel.DEBUG,this, "마지막지시구분=>확장콘베이어에 수조탱크 보급코일이 없을 경우.");
									}
								} 
							}else{
								/**
								 *	3.	야드에서 수조탱크 보급코일 체크
								 */
								JDTORecord schRc  = dao.getCraneSchCount(sYdGp,
							    									     sBayGp,
							    									     sSchCode);
					    		if(schRc != null){
					    			int iCount = schRc.getFieldInt("COUNT");
					    			
					    			logger.println(LogLevel.DEBUG,this, "마지막지시구분=>야드 COUNT="+iCount);
					    			
					    			if(iCount == 0||
									   iCount == 1){//자기자신
										LastOrderId		= "E";
										logger.println(LogLevel.DEBUG,this, "마지막지시구분=>야드에서 수조탱크 보급코일이 없을 경우.");
									}
						    	}
						    } 
						}
					}
					
					/**
					 * 수조탱크 추출 마지막 지시구분 셋팅
					 */
					logger.println(LogLevel.DEBUG,this, "setCraneBCoilMsgInfo()>>수조탱크 추출 마지막 지시구분 셋팅");
					{
						if(YmCommonConst.STACK_COL_USAGE_CD_CW.equals(sUpUsageCd)){// 수냉탱크
							
							/**
							 *	1. UP위치가 수조탱크 마지막 번지일 경우.
							 */
							
							if(YmCommonConst.STACK_BED_GP_03.equals(UpAddress.substring(6, 8))){
								LastOrderId		= "E";
								logger.println(LogLevel.DEBUG,this, "마지막지시구분=>수조탱크 마지막 번지.");
							}
							/**
							 *	2. 해당 수조탱크에 추출코일이 없을 경우.
							 */	 
							JDTORecord cTank = dao.getTCLoadCount(UpAddress.substring(0, 6)); 
							if(cTank != null){
								int iCount = cTank.getFieldInt("CNT");
								if(iCount == 0){
									LastOrderId		= "E";
									logger.println(LogLevel.DEBUG,this, "마지막지시구분=>수조탱크 추출코일이 없을 경우.");
								}
							} 
						}
					}
				}	
			}
			
			sMsg.append(YmCommonUtil.FillToString(TC				,iTC));
			sMsg.append(YmCommonUtil.FillToString(sDate				,iDate));
			sMsg.append(YmCommonUtil.FillToString(sTime				,iTime));
			sMsg.append(YmCommonUtil.FillToString(Form				,iForm));
			sMsg.append(YmCommonUtil.FillToNumber(Message_Length	,iMessage_Length));
			sMsg.append(YmCommonUtil.FillToString(CraneNo			,iCraneNo));
			sMsg.append(YmCommonUtil.FillToString(ProcessId			,iProcessId));
			sMsg.append(YmCommonUtil.FillToString(DriveMode			,iDriveMode));
			sMsg.append(YmCommonUtil.FillToString(TroubleRecovery	,iTroubleRecovery));
			sMsg.append(YmCommonUtil.FillToNumber(WorkOrderSeq		,iWorkOrderSeq));
			sMsg.append(YmCommonUtil.FillToString(LastOrderId		,iLastOrderId));
			sMsg.append(YmCommonUtil.FillToString(WorkOrderId		,iWorkOrderId));
			sMsg.append(YmCommonUtil.FillToString(SchCode			,iSchCode));
			sMsg.append(YmCommonUtil.FillToString(SchCodeName		,iSchCodeName));
			sMsg.append(YmCommonUtil.FillToString(MainWorkId		,iMainWorkId));
			sMsg.append(YmCommonUtil.FillToString(TCNo				,iTCNo));
			sMsg.append(YmCommonUtil.FillToString(TCWorkStopPosition,iTCWorkStopPosition));
			sMsg.append(YmCommonUtil.FillToString(CarNo				,iCarNo));
			sMsg.append(YmCommonUtil.FillToNumber(StackCount		,iStackCount));
			sMsg.append(YmCommonUtil.FillToNumber(RemainCount		,iRemainCount));
			sMsg.append(YmCommonUtil.FillToString(UpAddress			,iUpAddress));
			sMsg.append(YmCommonUtil.FillToString(PutAddress		,iPutAddress));
			sMsg.append(YmCommonUtil.FillToNumber(UpXAddress		,iUpXAddress));
			sMsg.append(YmCommonUtil.FillToNumber(UpXPlusRange		,iUpXPlusRange));
			sMsg.append(YmCommonUtil.FillToNumber(UpXMinusRange		,iUpXMinusRange));
			sMsg.append(YmCommonUtil.FillToNumber(UpYAddress		,iUpYAddress));
			sMsg.append(YmCommonUtil.FillToNumber(UpYPlusRange		,iUpYPlusRange));
			sMsg.append(YmCommonUtil.FillToNumber(UpYMinusRange		,iUpYMinusRange));
			sMsg.append(YmCommonUtil.FillToNumber(PutXAddress		,iPutXAddress));
			sMsg.append(YmCommonUtil.FillToNumber(PutXPlusRange		,iPutXPlusRange));
			sMsg.append(YmCommonUtil.FillToNumber(PutXMinusRange	,iPutXMinusRange));
			sMsg.append(YmCommonUtil.FillToNumber(PutYAddress		,iPutYAddress));
			sMsg.append(YmCommonUtil.FillToNumber(PutYPlusRange		,iPutYPlusRange));
			sMsg.append(YmCommonUtil.FillToNumber(PutYMinusRange	,iPutYMinusRange));
			sMsg.append(YmCommonUtil.FillToString(CoilNo			,iCoilNo));
			sMsg.append(YmCommonUtil.FillToString(ProductId			,iProductId));
			sMsg.append(YmCommonUtil.FillToString(AfterProcessCode	,iAfterProcessCode));
			sMsg.append(YmCommonUtil.FillToString(CoolStatusCode	,iCoolStatusCode));
			sMsg.append(YmCommonUtil.FillToString(OrderId			,iOrderId));
			sMsg.append(YmCommonUtil.FillToString(OrderNo			,iOrderNo));
			sMsg.append(YmCommonUtil.FillToString(CradNo			,iCradNo));
			sMsg.append(YmCommonUtil.FillToString(GroupInfo			,iGroupInfo));
			sMsg.append(YmCommonUtil.FillToString(ProductNo			,iProductNo));
			sMsg.append(YmCommonUtil.FillToNumber(Thick				,iThick));
			sMsg.append(YmCommonUtil.FillToNumber(Width				,iWidth));
			sMsg.append(YmCommonUtil.FillToNumber(Length			,iLength));
			sMsg.append(YmCommonUtil.FillToNumber(OutDia			,iOutDia));
			sMsg.append(YmCommonUtil.FillToNumber(Weight			,iWeight));
			sMsg.append(YmCommonUtil.FillToString(Message1			,iMessage1));
			sMsg.append(YmCommonUtil.FillToString(Message2			,iMessage2));
			logger.println(LogLevel.DEBUG,this, "setCraneBCoilMsgInfo()>>Msg:"+sMsg);
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return sMsg.toString();
	}

	
	/**
	 * 전문코드				TC					CHAR	07 전문코드                  CM1BP01
	 * 발생일자				Date				CHAR	10 발생일자                  YYYY-MM-DD
	 * 발생시간				Time				CHAR	08 발생시간                  HH-MM-SS
	 * 전문구분				Form				CHAR	01 전문구분                  I  : Initialize, U : Update,D : Delete,   R : Re-request
	 * 전문길이				Message_Length		CHAR	04 전문길이                  
	 * CRANE 번호			CraneNo				CHAR	06 CRANE번호                 YARD구분(1)+동구분(1)+설비구분:CR(2)+CR NO(2)	
	 * 처리구분				ProcessId			CHAR	01 처리구분                  SPACE:자동, B:화면
	 * 운전모드				DriveMode			CHAR	01 운전모드                  0:ON LINE, 1:OFF LINE
	 * 고장/복구			TroubleRecovery		CHAR	01 고장복구                  0:정상, 1:고장
	 * 작업지시 순번		WorkOrderSeq		CHAR	02 작업지시순번              00 ~ 99
	 * 마지막 지시 구분		LastOrderId			CHAR	01 마지막지시구분            SPACE, ‘E’
	 * 작업지시 구분		WorkOrderId			CHAR	01 작업지시구분              W:대기, U:권상지시, P:권하지시
	 * SCHEDULE CODE		SchCode				CHAR	04 SCHEDULECODE              LEVEL3 CODE
	 * SCHEDULE CODE 명칭	SchCodeName			CHAR	20 SCHEDULECODE명칭          
	 * 주작업 구분			MainWorkId			CHAR	02 주작업구분                01: 주작업. 02: Dummy 작업
	 * 대차 번호			TCNo				CHAR	04 대차번호                  설비구분:TC(2)+TC NO(2)
	 * 대차작업 정지위치	TCWorkStopPosition	CHAR	06 대차작업정지위치최종목적지YARD구분(1)+동구분(1)+SPAN(2)+열 NO(2)	 
	 * 차량번호				CarNo				CHAR	12 차량번호                  
	 * 적재매수				StackCount			CHAR	02 적재매수                  
	 * 잔여매수				RemainCount			CHAR	02 잔여매수                  
	 * 권상번지				UpAddress			CHAR	10 권상번지                  YARD(1) + 동(1) + SPAN(2) + 열(2) + 번지(2) + 단(2)	
	 * 권하번지				PutAddress			CHAR	10 권하번지                  
	 * 권상 X 위치			UpXAddress			CHAR	06 권상X위치                 
	 * 	허용오차(+)			UpXPlusRange		CHAR	04 권상X허용오차P            	
	 *	허용오차(-)			UpXMinusRange		CHAR	04 권상X허용오차M            	
	 * 권상 Y 위치			UpYAddress			CHAR	06 권상Y위치                 
	 * 	허용오차(+)			UpYPlusRange		CHAR	04 권상Y허용오차P            	
	 * 	허용오차(-)			UpYMinusRange		CHAR	04 권상Y허용오차M            	
	 * 권하 X 위치			PutXAddress			CHAR	06 권하X위치                 
	 * 	허용오차(+)			PutXPlusRange		CHAR	04 권하X허용오차P            	
	 * 	허용오차(-)			PutXMinusRange		CHAR	04 권하X허용오차M            	
	 * 권하 Y 위치			PutYAddress			CHAR	06 권하Y위치                 
	 * 	허용오차(+)			PutYPlusRange		CHAR	04 권하Y허용오차P            	
	 * 	허용오차(-)			PutYMinusRange		CHAR	04 권하Y허용오차M            	
	 * 작업 지시 매수		WorkOrderCount		CHAR	02 작업지시매수              
	 * 제품구분				ProductId			CHAR	02 제품구분                  SM:SLAB소재,SG:SLAB제품
	 * 지시구분				OrderId				CHAR	01 지시구분                  1:장입지시, 2:이송지시, 3:출하지시	
	 * 지시번호				OrderNo				CHAR	10 지시번호                  
	 * CARD 번호			CradNo				CHAR	06 CARD번호                  
	 * SLAB 번호 #1			GroupInfo			CHAR	11 SLAB번호1                 
	 * 제작번호/행번 #1		ProductNo			CHAR	13 제작번호행번1         
	 * 두께 #1				Thick				CHAR	07 두께1                     ㎜	소수점3자리 (###.###)
	 * 폭 #1				Width				CHAR	06 폭1                       ㎜	소수점1자리 (####.#)
	 * 길이 #1				Length				CHAR	06 길이1                     ㎜	
	 * 중량 #1				Weight				CHAR	05 중량1                     Kg	
	 * 구입SLABNO #1        구입SLABNO1	        CHAR	25 구입SLABNO1            
	 * SLAB 번호 #2			GroupInfo			CHAR	11 SLAB번호2                 
	 * 제작번호/행번 #2		ProductNo			CHAR	13 제작번호행번2             	
	 * 두께 #2				Thick				CHAR	07 두께2                     ㎜	소수점3자리 (###.###)
	 * 폭 #2				Width				CHAR	06 폭2                       ㎜	소수점1자리 (####.#)
	 * 길이 #2				Length				CHAR	06 길이2                     ㎜	
	 * 중량 #2				Weight				CHAR	05 중량2                     Kg	
	 * 구입SLABNO #2        구입SLABNO2	        CHAR	25 구입SLABNO2        
	 * 권상 위치 높이		UpPositionHeight	CHAR	05 권상위치높이              
	 * 권하 위치 높이		PutPositionHeight	CHAR	05 권하위치높이              
	 * MESSAGE 1			Message1			CHAR	40 MESSAGE1                  
	 * MESSAGE 2			Message2			CHAR	40 MESSAGE2      
     * @param schInfo : SCHEDULE INFO
     *
     * @return
     * @throws 
     */	
	private JDTORecord setCraneBSlabMsgInfo(String sSchId,
										String sMsg1,
										String sMputLoc,
										String sTcGbn){
		//A열연 SLAB야드 추가 (MCH) return String 이었던걸 JDTORecord로 수정했음.
		JDTORecord jRecord = JDTORecordFactory.getInstance().create();
		StringBuffer sMsg = new StringBuffer();
		
		String TC					= "";
		String sDate				= "";
		String sTime				= "";
		String Form				= "";
		String Message_Length		= "";
		String CraneNo			= "";
		String ProcessId			= "";
		String DriveMode			= "";
		String TroubleRecovery		= "";
		String WorkOrderSeq		= "";
		String LastOrderId			= "";
		String WorkOrderId			= "";
		String SchCode			= "";
		String SchCodeName		= "";
		String MainWorkId			= "";
		String TCNo				= "";
		String TCWorkStopPosition	= "";
		String CarNo				= "";
		String StackCount			= "";
		String RemainCount			= "";
		String UpAddress			= "";
		String PutAddress			= "";
		String UpXAddress			= "";
		String UpXPlusRange		= "";
		String UpXMinusRange		= "";
		String UpYAddress			= "";
		String UpYPlusRange		= "";
		String UpYMinusRange		= "";
		String PutXAddress			= "";
		String PutXPlusRange		= "";
		String PutXMinusRange		= "";
		String PutYAddress			= "";
		String PutYPlusRange		= "";
		String PutYMinusRange		= "";
		String WorkOrderCount		= "";
		String ProductId			= "";
		String OrderId				= "";
		String OrderNo				= "";
		String CradNo				= "";
		String GroupInfo1			= "";
		String ProductNo1			= "";
		String Thick1				= "";
		String Width1				= "";
		String Length1				= "";
		String Weight1				= "";
		String BuySlabNo1			= "";
		String GroupInfo2			= "";
		String ProductNo2			= "";
		String Thick2				= "";
		String Width2				= "";
		String Length2				= "";
		String Weight2				= "";
		String BuySlabNo2			= "";
		String UpPositionHeight		= "";
		String PutPositionHeight		= "";
		String Message1			= "";
		String Message2			= "";
		
		int iTC					=  7;
		int iDate					= 10;
		int iTime					=  8;
		int iForm					=  1;
		int iMessage_Length		=  4;
		int iCraneNo				=  6;
		int iProcessId				=  1;
		int iDriveMode				=  1;
		int iTroubleRecovery		=  1;
		int iWorkOrderSeq			=  2;
		int iLastOrderId			=  1;
		int iWorkOrderId			=  1;
		int iSchCode				=  4;
		int iSchCodeName			= 20;
		int iMainWorkId			=  2;
		int iTCNo					=  4;
		int iTCWorkStopPosition		=  6;
		int iCarNo					= 12;
		int iStackCount			=  2;
		int iRemainCount			=  2;
		int iUpAddress				= 10;
		int iPutAddress			= 10;
		int iUpXAddress			=  6;
		int iUpXPlusRange			=  4;
		int iUpXMinusRange		=  4;
		int iUpYAddress			=  6;
		int iUpYPlusRange			=  4;
		int iUpYMinusRange		=  4;
		int iPutXAddress			=  6;
		int iPutXPlusRange			=  4;
		int iPutXMinusRange		=  4;
		int iPutYAddress			=  6;
		int iPutYPlusRange			=  4;
		int iPutYMinusRange		=  4;
		int iWorkOrderCount			=  2;
		int iProductId				=  2;
		int iOrderId				=  1;
		int iOrderNo				= 10;
		int iCradNo				=  6;
		int iGroupInfo1				= 11;
		int iProductNo1			= 13;
		int iThick1				=  7;
		int iWidth1				=  6;
		int iLength1				=  6;
		int iWeight1				=  5;
		int iBuySlabNo1			= 25;
		int iGroupInfo2				= 11;
		int iProductNo2			= 13;
		int iThick2				=  7;
		int iWidth2				=  6;
		int iLength2				=  6;
		int iWeight2				=  5;
		int iBuySlabNo2			= 25;
		int iUpPositionHeight		=  5;
		int iPutPositionHeight		=  5;
		int iMessage1				= 40;
		int iMessage2				= 40;
		int iTotalLength			=398;
							   
		try{
			
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			
			if(sSchId.length() == 6){
				
				logger.println(LogLevel.DEBUG,this, "작업지시 전문발생 대기모드.");
				
				
				sDate				= YmCommonUtil.getCurDate("yyyy-MM-dd");
				sTime				= YmCommonUtil.getCurDate("HH-mm-ss");
				Form					= "I";
				Message_Length		= iTotalLength+"";
				CraneNo				= sSchId;
				/*
				 *	1.	운전모드
				 *		0:ONLINE	1:OFFLINE
				 *	2.	고장/복구
				 *		0:정상	1:고장
				 */
				{ 
					//A열연 SLAB야드 추가 (MCH)
					logger.println(LogLevel.DEBUG,this, "설비명일때"+sSchId);
					if(YmCommonConst.YD_GP_0.equals(sSchId.substring(0,1)) 
							&& YmCommonConst.BAY_GP_A.equals(sSchId.substring(1,2))){
						TC	= YmCommonConst.TC_HM1BP01;					
					}else if(YmCommonConst.YD_GP_0.equals(sSchId.substring(0,1)) 
								&& YmCommonConst.BAY_GP_B.equals(sSchId.substring(1,2))){
						TC	= YmCommonConst.TC_HM1BP51;					
					}else{
						TC	= YmCommonConst.TC_CM1BP01;	
					}
					
					JDTORecord jtR = dao.getEquipInfoWithEquipNo(sSchId.substring(0,1),//sYdGp
													            sSchId.substring(4)); //sEquipNo
					
					String sWorkMode  = StringHelper.evl(jtR.getFieldString("WORK_MODE"),"");
					String sEquipStat = StringHelper.evl(jtR.getFieldString("EQUIP_STAT"),"");
					
					DriveMode		= (YmCommonConst.WORK_MODE_O.equals(sWorkMode) ? "0" : 
									  (YmCommonConst.WORK_MODE_C.equals(sWorkMode) ? "1" : ""));
					TroubleRecovery	= (YmCommonConst.WORK_MODE_O.equals(sEquipStat)? "0" : 
									  (YmCommonConst.WORK_MODE_C.equals(sEquipStat)? "1" : ""));
				}
				WorkOrderId			= "W";
				
				Message1			= sMsg1;
				if(!"".equals(sMsg1)){
					Message2		= "작업요구수행불가. 다시 작업요구를 하세요.";
				}
				
			}else{
				
				JDTORecord schInfo 	= dao.getCraneSchInfo(sSchId);
				
				logger.println(LogLevel.DEBUG,this, "sSchId=" + sSchId);
			    	if(schInfo == null){
			    		logger.println(LogLevel.DEBUG,this, "작업지시 전문발생 스케쥴 정보가 존재안함.");
			    		jRecord.setField("sMessage",sMsg.toString());
			    		return jRecord;
			    	}
		    	
				String sYdGp 		= StringHelper.evl(schInfo.getFieldString("YD_GP"),"");
				String sBayGp 		= StringHelper.evl(schInfo.getFieldString("BAY_GP"),"");
				String sEquipKind	= YmCommonConst.EQUIP_KIND_CR;
				String sEquipNo 	= StringHelper.evl(schInfo.getFieldString("SCH_WORK_EQUIP_NO"),"");
				String sStockId		= StringHelper.evl(schInfo.getFieldString("STOCK_ID"),"");
				String sSchCode 	= StringHelper.evl(schInfo.getFieldString("SCH_WORK_KIND"),"");
				String sMainWorkYn 	= StringHelper.evl(schInfo.getFieldString("SCH_WORK_AID_YN"),"");
				String sUpLoc 		= StringHelper.evl(schInfo.getFieldString("CRANE_WORD_UP_LOC"),"");
				String sPutLoc 		= StringHelper.evl(schInfo.getFieldString("CRANE_WORD_PUT_LOC"),"");
				String sSchWorkStat = StringHelper.evl(schInfo.getFieldString("SCH_WORK_STAT"),"");
				String sSchJisiDate = StringHelper.evl(schInfo.getFieldString("WBOOK_SCH_ACT_DDTT"),"");
				ProcessId			= StringHelper.evl(schInfo.getFieldString("SPEC_ABBSYM_CHK"),"");
				
				logger.println(LogLevel.DEBUG,this, "JDTORecord 내용 schInfo="+schInfo);
				if(YmCommonConst.YD_GP_0.equals(sYdGp) 
						&& YmCommonConst.BAY_GP_A.equals(sBayGp)){
					TC	= YmCommonConst.TC_HM1BP01;					
				}else if(YmCommonConst.YD_GP_0.equals(sYdGp) 
							&& YmCommonConst.BAY_GP_B.equals(sBayGp)){
					TC	= YmCommonConst.TC_HM1BP51;					
				}else{
					TC	= YmCommonConst.TC_CM1BP01;	
				}
				
				/*
				 *	A.	최초 작업지시 시점에 작업지시일자를 셋팅한다.
				 */
				{
					if("".equals(sSchJisiDate)){
						int iWrslt = dao.updateWbookSchActDdttSchInfo(sSchId,
																  	  YmCommonUtil.getStringYMDHMS());
					}											  	  
				}										  
				
				/*
				 *	수정작업 재지시 처리 
				 *	sMputLoc : 화면에서 입력받은 위치정보
				 */
				if(!"".equals(sMputLoc) &&
				   sMputLoc.length() == 10){
					
					sPutLoc = sMputLoc;
				}
				
				/*
				 *	0.	작업지시 전문발생 전 현 저장품 위치 체크
				 */
				{
					String sDbUpLoc		= "";
			    	
					JDTORecord layerRc = dao.getUpStackLayerListWithSchId(sSchId);
			    	
				    	if(layerRc != null){
				    		sDbUpLoc	= StringHelper.evl(layerRc.getFieldString("STACK_COL_GP"), "")
								    	+ StringHelper.evl(layerRc.getFieldString("STACK_BED_GP"), "")
								    	+ StringHelper.evl(layerRc.getFieldString("STACK_LAYER_GP"), "");
			    		}
			    		
			    		if(!"".equals(sDbUpLoc) &&
			    		   !sUpLoc.equals(sDbUpLoc)){
			    		   	logger.println(LogLevel.DEBUG,this, "작업지시 전 저장품 위치 바뀜.==");
			    		   	logger.println(LogLevel.DEBUG,this, "현 위치로 권상위치 작업지시 내림.==");
			    		   	
			    		   	sUpLoc = sDbUpLoc;
			    		   	
			    		   	// 스케쥴 정보 수정
			    		   	int iSeq = dao.updateUpLocInfoWithSchId(sSchId,
			    								   				    sDbUpLoc);
			    		}
			    	}				    	
			    	
				/*
				 *	1.	HEADER INFO
				 *공통으로 사용하기 위해서 TC세팅을 주석처리 했음(MCH)
				 */
				sDate				= YmCommonUtil.getCurDate("yyyy-MM-dd");
				sTime				= YmCommonUtil.getCurDate("HH-mm-ss");
				
				
				if("".equals(sTcGbn)){
					Form				= YmCommonConst.TC_WORK_I;
				}else{
					Form				= sTcGbn;
				}
				
				Message_Length		= iTotalLength+""; 
				/*
				 *	2.	CRANE NO 
				 */
				{ 
					CraneNo			= sYdGp+sBayGp+sEquipKind+sEquipNo;
				}
				/*
				 *	3.	처리구분
				 *		SPACE:자동	B:화면
				 */
				{ 
//					ProcessId		= "";
				}
				/*
				 *	4.	운전모드
				 *		0:ONLINE	1:OFFLINE
				 *	5.	고장/복구
				 *		0:정상	1:고장
				 */
				{ 
					
					JDTORecord jtR = dao.getEquipInfoWithEquipNo(sYdGp,
															 	 sEquipNo);
					
					String sWorkMode  	= StringHelper.evl(jtR.getFieldString("WORK_MODE"),"");
					String sEquipStat 	= StringHelper.evl(jtR.getFieldString("EQUIP_STAT"),"");
					
					DriveMode		= (YmCommonConst.WORK_MODE_O.equals(sWorkMode) ? "0" : 
									  (YmCommonConst.WORK_MODE_C.equals(sWorkMode) ? "1" : ""));
					TroubleRecovery	= (YmCommonConst.WORK_MODE_O.equals(sEquipStat)? "0" : 
									  (YmCommonConst.WORK_MODE_C.equals(sEquipStat)? "1" : ""));
				}
				
				/*
				 *	8.	작업지시구분
				 *		W:대기	U:권상지시	P:권하지시
				 */
				{ 
					String sUpDownGbn = "W";
		    	
				    	if(YmCommonConst.SCH_WORK_STAT_1.equals(sSchWorkStat)){
				    		sUpDownGbn	= "U";
				    	}else if(YmCommonConst.SCH_WORK_STAT_2.equals(sSchWorkStat)){
				    		sUpDownGbn	= "P";
				    	}else if(YmCommonConst.SCH_WORK_STAT_3.equals(sSchWorkStat)){
				    		sUpDownGbn	= "P";
				    	}
			    	
					WorkOrderId		= sUpDownGbn;
				}
				/*
				 *	9.	스케쥴코드
				 */
				{ 
					SchCode			= sSchCode;
				}
				/*
				 *	9.	스케쥴명칭
				 */
				{ 
					ymCommonDAO dao  = ymCommonDAO.getInstance();
					String sQueryId  = "ym.common.dao.ymCommonDAO.getCodeToName";	   	
				   	JDTORecord comJr = dao.getCommonInfo(sQueryId,new Object[]{"YM104",sYdGp,
																			   sSchCode});
																			   
					SchCodeName		 = StringHelper.evl(comJr.getFieldString("CLASS2_NAME1"), "");
				}
				/*
				 *	10.	주작업구분
				 */
				{ 
					MainWorkId		= YmCommonUtil.getLegacyDataWithCur(sMainWorkYn);
				}
				/*
				 *	11.	대차번호
				 *		설비구분:TC(2)+TCNO(2)
				 */
				{ 
					if(sPutLoc.indexOf("TC") != -1){
						
						TCNo		= sPutLoc.substring(2,4)+ "0"+ sPutLoc.substring(4,5);
					}
				}			
				/*
				 *	12.	대차작업정지위치
				 *		최종목적지:YARD구분(1)+동구분(1)+SPAN(2)+열NO(2)
				 */
				{ 
					if(sPutLoc.indexOf("TC") != -1){
						
						JDTORecord equipJr 	= dao.getEquipInfoWithEquipGp(sPutLoc.substring(0,1) + "X"+ 
																		  sPutLoc.substring(2,4) + "0"+ 
																		  sPutLoc.substring(4,5));
						TCWorkStopPosition	= StringHelper.evl(equipJr.getFieldString("CARUNLOAD_STOP_LOC"), "");
					}
				}			
				/*
				 *	17.	제품구분(SM:SLAB소재,SG:SLAB제품,CM:COIL소재,CG:COIL제품 )
				 */
				/*
				 *	13.	차량번호
				 */
				/*
				 *	18.	지시구분 (1:장입지시, 2:이송지시, 3:출하지시)
				 */
				/*
				 *	19.	지시번호
				 */
				/*
				 *	20.	CARD번호
				 */
				/*
				 *	14.	적재매수
				 */
				 /*
				 *	15.	잔여매수
				 */ 
				{ 
					JDTORecord stockJR = dao.getStockInfo(sStockId);
					
					ProductId	= StringHelper.evl(stockJR.getFieldString("STOCK_ITEM"), "");
					//코일 벤딩유무(Y:벤딩표시 , S:벤딩보급) 
					OrderId		= StringHelper.evl(stockJR.getFieldString("YD_RULE_PL_RS_GP"), "");
					
					if(YmCommonConst.NEW_SCH_WORK_KIND_SVML.equals(sSchCode)|| // SLAB 이송상차
					   YmCommonConst.NEW_SCH_WORK_KIND_SVMU.equals(sSchCode)){ // SLAB 이송하차	
					   	
						/* 윤혁상수정(출하DB참조/Card No 참조)
					   	JDTORecord carR = dao.getDmCarInfo(sStockId);
					    	if(carR != null){
					    		CarNo		= StringHelper.evl(carR.getFieldString("CAR_NO"), "0");
					    	}else{
					    		CarNo		= "0";
					    	}
				    	
					   	OrderId	= "2";
					   	OrderNo	= StringHelper.evl(stockJR.getFieldString("TRANS_WORD_NO"), "");
				    		CradNo	= StringHelper.evl(stockJR.getFieldString("CAR_CARD_NO"), "");
				    	
				    		{
				    			List 	   dmList  	= null; // Card No,운송지시일자,순번이 같은 저장품정보
							JDTORecord dmRc  = null; // 저장품정보
							String 	   sSmt    	= "";   // 저장품이동조건
							int		   iDmSize = 0;    // Card No,운송지시일자,순번이 같은 저장품 갯수
							int		   iDmFns  = 0;    // 상,하차완료인 저장품 갯수	
								
							dmList  = dao.getYmDmCommonInfo(sStockId);
							iDmSize = dmList.size();
							
							for(int inx = 0; inx < dmList.size() ; inx++){
						 	 	dmRc = (JDTORecord)dmList.get(inx);
						 	 	sSmt = StringHelper.evl(dmRc.getFieldString("STOCK_MOVE_TERM"), "");
						 	 	
						 	 	if(YmCommonConst.NEW_STOCK_MOVE_TERM_VL.equals(sSmt)|| // 상차완료
						 	 	   YmCommonConst.NEW_STOCK_MOVE_TERM_C1.equals(sSmt)|| // 이송완료	
						 	 	   YmCommonConst.NEW_STOCK_MOVE_TERM_DS.equals(sSmt)|| // 정정작업대기	
				   				   YmCommonConst.NEW_STOCK_MOVE_TERM_ES.equals(sSmt)){ // 압연지시대기		
						 	 		
									iDmFns++;
						 	 	}
						 	}
						 	StackCount		=  iDmSize + "";
						 	RemainCount		= (iDmSize - iDmFns)+"";
						 	
						}	
						*/
					}
					
				}	
				/*
				 *	16.	권상,권하 X Y 좌표 및 오차범위
				 */
				{
					UpAddress = sUpLoc;
					PutAddress= sPutLoc;
					
					String sUpXLeft 	= "";
					String sUpXRight 	= "";
					String sUpYUp 	= "";
					String sUpYDown 	= "";
					 
					String sPutXLeft 	= "";
					String sPutXRight 	= "";
					String sPutYUp 	= "";
					String sPutYDown 	= ""; 
					
					JDTORecord ruleUpX = dao.getStackRuleInfo_002(sUpLoc.substring(0, 6),
																  YmCommonConst.STACK_RULE_CD_XCD);
					if(ruleUpX != null){
						sUpXLeft 	= StringHelper.evl(ruleUpX.getFieldString("STACK_RULE_MIN"), "");
						sUpXRight 	= StringHelper.evl(ruleUpX.getFieldString("STACK_RULE_MAX"), "");
					}
					JDTORecord ruleUpY = dao.getStackRuleInfo_002(sUpLoc.substring(0, 6),
																  YmCommonConst.STACK_RULE_CD_YCD);
					if(ruleUpY != null){
						sUpYUp 		= StringHelper.evl(ruleUpY.getFieldString("STACK_RULE_MAX"), "");
						sUpYDown 	= StringHelper.evl(ruleUpY.getFieldString("STACK_RULE_MIN"), "");
					}
					
					JDTORecord rulePutX = dao.getStackRuleInfo_002(sPutLoc.substring(0, 6),
																   YmCommonConst.STACK_RULE_CD_XCD);
					if(rulePutX != null){
						sPutXLeft 	= StringHelper.evl(rulePutX.getFieldString("STACK_RULE_MIN"), "");
						sPutXRight 	= StringHelper.evl(rulePutX.getFieldString("STACK_RULE_MAX"), "");
					}
					
					JDTORecord rulePutY = dao.getStackRuleInfo_002(sPutLoc.substring(0, 6),
																   YmCommonConst.STACK_RULE_CD_YCD);
					if(rulePutY != null){
						sPutYUp 	= StringHelper.evl(rulePutY.getFieldString("STACK_RULE_MAX"), "");
						sPutYDown 	= StringHelper.evl(rulePutY.getFieldString("STACK_RULE_MIN"), "");
					}
													
					if(UpAddress.length() == 10){
						JDTORecord upR = dao.getStackLayerInfoWithPk(sUpLoc.substring(0, 6),
																	 sUpLoc.substring(6, 8),
																	 sUpLoc.substring(8,10));
					    	String sUpXPosition = "";
					    	String sUpYPosition = "";
					    	
					    	if(upR != null){
				    			sUpXPosition = StringHelper.evl(upR.getFieldString("STACK_LAYER_X_AXIS"), "");					
							sUpYPosition = StringHelper.evl(upR.getFieldString("STACK_LAYER_Y_AXIS"), "");
						}else{
							/*
				    			 * 설비정보는 권상시 적치단정보를 삭제하기 때문에 좌표정보를 가져올 수 없다.
				    			 * 따라서, 적치열정보를 가져온다.
				    			 */
							JDTORecord upSubR = dao.getStackColInfoWithPk(sUpLoc.substring(0, 6));
							if(upSubR != null){
								sUpXPosition = StringHelper.evl(upSubR.getFieldString("STACK_COL_RULE_X_AXIS"), "");					
								sUpYPosition = StringHelper.evl(upSubR.getFieldString("STACK_COL_RULE_Y_AXIS"), "");
							}
						}
						UpXAddress			= sUpXPosition;
						UpXPlusRange		= sUpXLeft;
						UpXMinusRange		= sUpXRight;
						UpYAddress			= sUpYPosition;
						UpYPlusRange		= sUpYUp;
						UpYMinusRange		= sUpYDown;
				    	}
	
				    	if(PutAddress.length() == 10){
				    		JDTORecord putR = dao.getStackLayerInfoWithPk(sPutLoc.substring(0, 6),
																		  sPutLoc.substring(6, 8),
																		  sPutLoc.substring(8,10));
					    	String sPutXPosition = "";
					    	String sPutYPosition = "";
					    	
					    	if(putR != null){
					    		sPutXPosition = StringHelper.evl(putR.getFieldString("STACK_LAYER_X_AXIS"), "");					
							sPutYPosition = StringHelper.evl(putR.getFieldString("STACK_LAYER_Y_AXIS"), "");
						}else{
							/*
				    			 * 설비정보는 권상시 적치단정보를 삭제하기 때문에 좌표정보를 가져올 수 없다.
				    			 * 따라서, 적치열정보를 가져온다.
				    			 */
							JDTORecord putSubR = dao.getStackColInfoWithPk(sPutLoc.substring(0, 6));
							if(putSubR != null){
								sPutXPosition = StringHelper.evl(putSubR.getFieldString("STACK_COL_RULE_X_AXIS"), "");					
								sPutYPosition = StringHelper.evl(putSubR.getFieldString("STACK_COL_RULE_Y_AXIS"), "");
							}
						}
						PutXAddress			= sPutXPosition;
						PutXPlusRange		= sPutXLeft;
						PutXMinusRange		= sPutXRight;
						PutYAddress			= sPutYPosition;
						PutYPlusRange		= sPutYUp;
						PutYMinusRange		= sPutYDown;
				    	}
			    	}
		    	
				String sSlabNo1 	= sStockId;
			    	String sSlabNo2 	= "";
			    	String sGripCount 	= "1"; //1매 작업
			    	
			    	JDTORecord stockV = dao.getSlabGripInfo_02(sYdGp,
									    					   sBayGp,
									    					   sEquipKind,
									    					   sEquipNo,
									    					   sSlabNo1,
									    					   SchCode);
			    	if(stockV != null){
			    		sGripCount = "2"; //2매 작업
			    		sSlabNo2   = StringHelper.evl(stockV.getFieldString("STOCK_ID"), "");
			    	}
			    	
			    	/*
				 *	21.	작업지시매수
				 */
				{ 
					WorkOrderCount	= sGripCount;
				}	
		    		/*
				 *	22.	SLAB #1 기본정보 
				 *		-	제작번호/행번
				 *		-	두께
				 *		-	폭
				 *		-	길이
				 *		-	중량
				 		두께	CHAR	7	㎜	소수점3자리 (###.###)
						폭		CHAR	6	㎜	소수점1자리 (####.#)
						길이	CHAR	6	㎜	Slab 공통 그대로
						중량	CHAR	5	Kg	Slab 공통 그대로
				 */
				String sHcrGp1 = "0";
				{ 
					GroupInfo1	= sSlabNo1;
					
					JDTORecord cInfo = dao.getSlabCommonInfo(sSlabNo1);
			    		
			    		if(cInfo != null){
			    			
						ProductNo1	= YmCommonUtil.FillToString(StringHelper.evl(cInfo.getFieldString("ORD_NO"), ""),10)+	
															StringHelper.evl(cInfo.getFieldString("ORD_DTL"), "");
						Thick1		= StringHelper.replaceStr(
														YmCommonUtil.format(
														StringHelper.evl(cInfo.getFieldString("SLAB_T"), ""),3,3),".",""); 
						Width1		= StringHelper.replaceStr(
														YmCommonUtil.format(
														StringHelper.evl(cInfo.getFieldString("SLAB_W"), ""),4,1),".",""); 
						Length1		= StringHelper.evl(cInfo.getFieldString("SLAB_LEN"), "");
						Weight1		= StringHelper.evl(cInfo.getFieldString("SLAB_WT"), ""); 
						BuySlabNo1	= StringHelper.evl(cInfo.getFieldString("BUY_SLAB_NO"),"");		// 슬라브(C:고탄소재 >2)	  
						//해당슬라브 WCR/CCR재인지 구분항목 표시
						sHcrGp1		= StringHelper.evl(cInfo.getFieldString("TIMES"),"0");	
					}
				}		
				/*
				 *	23.	SLAB #2 기본정보 
				 *		-	제작번호/행번
				 *		-	두께
				 *		-	폭
				 *		-	길이
				 *		-	중량
				 */
				String sHcrGp2 = "0"; 
				{ 
					if(!"".equals(sSlabNo2)){
						
						GroupInfo2	= sSlabNo2;
						
						JDTORecord cInfo = dao.getSlabCommonInfo(sSlabNo2);
				    		
						if(cInfo != null){
						
							ProductNo2	= YmCommonUtil.FillToString(StringHelper.evl(cInfo.getFieldString("ORD_NO"), ""),10)+	
										  StringHelper.evl(cInfo.getFieldString("ORD_DTL"), "");
							Thick2		= StringHelper.replaceStr(
										  YmCommonUtil.format(
										  StringHelper.evl(cInfo.getFieldString("SLAB_T"), ""),3,3),".",""); 
							Width2		= StringHelper.replaceStr(
										  YmCommonUtil.format(
										  StringHelper.evl(cInfo.getFieldString("SLAB_W"), ""),4,1),".",""); 
							Length2		= StringHelper.evl(cInfo.getFieldString("SLAB_LEN"), "");
							Weight2		= StringHelper.evl(cInfo.getFieldString("SLAB_WT"), ""); 
							BuySlabNo2	= StringHelper.evl(cInfo.getFieldString("BUY_SLAB_NO"),"");	 // 슬라브(C:고탄소재 >2)	  	
							//해당슬라브 WCR/CCR재인지 구분항목 표시
							sHcrGp2		= StringHelper.evl(cInfo.getFieldString("TIMES"),"0");			  
						}
						}
				}	
				/*
				 *	6.	작업지시순번
				 *		00 ~ 99
				 *		참고) 현재 해당 슬라브의 WCR/CCR 구분항목으로 쓴다.
				 */
				{ 
					WorkOrderSeq		= sHcrGp1 + sHcrGp2;
				}
				/*
				 *	24.	권상위치높이
				 */
				{ 
					UpPositionHeight	= "";
				}	
				/*
				 *	25.	권하위치높이
				 */
				{ 
					PutPositionHeight	= "";
				}	
				/*
				 *	7.	마지막지시구분
				 *		SPACE	E
				 */
				boolean isNextLotNoBay 	= false;
				String sNextLotNoBay 		= "";
				{
					
					String sUpUsageCd  = YmCommonUtil.getStackColInfoWithPk(sUpLoc.substring(0, 6));
	 				String sPutUsageCd = YmCommonUtil.getStackColInfoWithPk(sPutLoc.substring(0, 6));
	 						
					if(YmCommonConst.NEW_SCH_WORK_KIND_SVMU.equals(sSchCode)	|| //Slab 이송하차
					   YmCommonConst.NEW_SCH_WORK_KIND_SYMM.equals(sSchCode)	|| //Slab 동내이적
					   YmCommonConst.NEW_SCH_WORK_KIND_SYM2.equals(sSchCode)	|| //Slab 동내이적
					   YmCommonConst.NEW_SCH_WORK_KIND_SYM3.equals(sSchCode)	|| //Slab 동내이적
					   YmCommonConst.NEW_SCH_WORK_KIND_STSL.equals(sSchCode)		|| //Slab 동간보급상차
					   YmCommonConst.NEW_SCH_WORK_KIND_STMU.equals(sSchCode)	|| //Slab 대차하차(1)
					   YmCommonConst.NEW_SCH_WORK_KIND_STM4.equals(sSchCode)	|| //Slab 대차하차(2)
					   YmCommonConst.NEW_SCH_WORK_KIND_SCLI.equals(sSchCode)		){ // Slab CTC 보급	
						
						//권하시점에 처리
	 					if("P".equals(WorkOrderId)){
	 						
	 						if(YmCommonConst.STACK_COL_USAGE_CD_CT.equals(sPutUsageCd)){// CTC
	 							
	 							LastOrderId		= "E";
	 							isNextLotNoBay 	= true;
	 							logger.println(LogLevel.DEBUG,this, "CTC보급 1매씩=>무조건 마지막지시구분 셋팅");
	 						}
	 					}
					}else if(YmCommonConst.NEW_SCH_WORK_KIND_SWLI.equals(sSchCode)){ // Slab W/B 보급
					   
	 					//권하시점에 처리
	 					if("P".equals(WorkOrderId)){
	 						
	 						if(YmCommonConst.STACK_COL_USAGE_CD_WB.equals(sPutUsageCd)){// W/B
	 							
	 							/**
	 							 *	W/B 최대적치수량을 가져온다.
	 							 */
	 							String 	sMaxCnt = "";
	 							{
		 							List infoL = dao.getStackLayerInfoWithBed(YmCommonConst.STACK_COL_GP_2CWB01,
		 																	  YmCommonConst.STACK_BED_GP_01);
		 							
		 							int	iMaxCnt = 0;
		 							
									if(infoL != null)
									{	
										JDTORecord infoV 	   = null;
									 	String sStackActiveStat= "";
									 	
										for(int inx = 0; inx < infoL.size() ; inx++)
										{
										 	infoV = (JDTORecord)infoL.get(inx);
										 	
										 	sStackActiveStat = StringHelper.evl(infoV.getFieldString("STACK_LAYER_ACTIVE_STAT"), "");	 														 
										 	
										 	if(YmCommonConst.STACK_LAYER_ACTIVE_STAT_O.equals(sStackActiveStat)){
										 		
										 		iMaxCnt++;
										 	}
										 }
									}	
									
									sMaxCnt = "0" + iMaxCnt;
									
									logger.println(LogLevel.DEBUG,this, "W/B보급 최대적치수량=>"+sMaxCnt);
		 						}
		 						
		 						if(sMaxCnt.equals(sPutLoc.substring(8,10))){
		 							
		 							LastOrderId		= "E";
		 							isNextLotNoBay 	= true;
		 							logger.println(LogLevel.DEBUG,this, "W/B보급 최대적치수량에 의거 마지막지시구분 셋팅");
		 							
		 						}else{
																			 
			 						String sEStockId  = "";
				 					String sNextLotNo = "";
				 					String sCurLotNo  = "";
				 					String sNextSlabW  = "";
				 					String sCurSlabW  = "";
				 					String sNextSlabWT  = "";
				 					String sCurSlabWT  = "";
									ymCommonDAO yCd = ymCommonDAO.getInstance();
									JDTORecord 	jtR = yCd.readCurZoinLotNo();
									 
									if(jtR != null){
										//현재 W/B보급되어야 할 장입LOT 번호
										sNextLotNo = StringHelper.evl(jtR.getFieldString("CHARGE_LOT_NO"), "");
										sNextSlabW = StringHelper.evl(jtR.getFieldString("SLAB_W"), "");
										sNextSlabWT = StringHelper.evl(jtR.getFieldString("SLAB_WT"), "");
									}
									
									sEStockId = "".equals(GroupInfo2) ? GroupInfo1 : GroupInfo2; 
									
									JDTORecord stockJR = dao.getStockInfo(sEStockId);
									
									if(stockJR != null){
										//현재 W/B보급되는 SLAB의 장입LOT 번호(임시 항목으로 저장)
										sCurLotNo	= StringHelper.evl(stockJR.getFieldString("CTS_RELAY_SADDLE"), "");
										sCurSlabW   = StringHelper.evl(stockJR.getFieldString("SLAB_W"), "");
										sCurSlabWT   = StringHelper.evl(stockJR.getFieldString("SLAB_WT"), "");
									}
									
									logger.println(LogLevel.DEBUG,this, "sNextLotNo	=>"+sNextLotNo);
									logger.println(LogLevel.DEBUG,this, "sCurLotNo	=>"+sCurLotNo);
									logger.println(LogLevel.DEBUG,this, "sNextSlabW	=>"+sNextSlabW);
									logger.println(LogLevel.DEBUG,this, "sCurSlabW	=>"+sCurSlabW);
									logger.println(LogLevel.DEBUG,this, "sNextSlabWT=>"+sNextSlabWT);
									logger.println(LogLevel.DEBUG,this, "sCurSlabWT	=>"+sCurSlabWT);
									
									double dTSlabWt = Double.parseDouble(sNextSlabWT);
									double dGSlabWt = Double.parseDouble(sCurSlabWT);
									double dTSlabW = Double.parseDouble(sNextSlabW);
									double dGSlabW = Double.parseDouble(sCurSlabW);
									
									//2016.03.10 Slab 폭 20mm 이상 시 마지막 작업으로 간주 워킹빔 이동 지시 편성
									if(!"".equals(sNextLotNo)&&
									   sNextLotNo.equals(sCurLotNo)&& 
									   Math.abs(dTSlabW - dGSlabW) <= 20.0 &&
									   Math.abs(dTSlabWt + dGSlabWt) <= 53000 
									){
										
										int iCount = 0;
										String sCOIL_NO="";
										List listCoilPos = dao.getStackLayerInfoWithBed(sPutLoc.substring(0, 6),sPutLoc.substring(6, 8)); 
										if (listCoilPos.size() > 0) {
											for(int j=0;j<listCoilPos.size();j++)
											{
												JDTORecord jtrCoilPos = (JDTORecord)listCoilPos.get(j);
												sCOIL_NO = StringHelper.evl(jtrCoilPos.getFieldString("STOCK_ID"),"");
												
												if(!sCOIL_NO.equals("")){
													iCount =iCount+1;
												}
											}											
										}
																
										if(iCount == 1){
											LastOrderId 		= "E";
											isNextLotNoBay 	= true;
											 
										}else{
											LastOrderId		= "";
											
										}
										logger.println(LogLevel.DEBUG,this, "마지막 지시구분 W/B =>"+LastOrderId+":"+iCount);
										
									}else{
										LastOrderId		= "E";
										isNextLotNoBay 	= true;
									}
									
									logger.println(LogLevel.DEBUG,this, "마지막 지시구분2 W/B =>"+LastOrderId);
									
									if(YmCommonConst.STACK_COL_USAGE_CD_CX.equals(sUpUsageCd)){// 대차정지위치
										
										int iCount = 0;
										JDTORecord cTcJr = dao.getTCLoadCount(sUpLoc.substring(0, 6)); 
										if(cTcJr != null){
											iCount = cTcJr.getFieldInt("CNT");
										}
										
										if(iCount == 0){
											LastOrderId 		= "E";
											isNextLotNoBay 	= true;
											logger.println(LogLevel.DEBUG,this, "마지막 지시구분 대차 =>"+iCount);
										}
									}
								}
							}
						}
					}else if(YmCommonConst.NEW_SCH_WORK_KIND_SVML.equals(sSchCode)|| // SLAB 이송상차
					   	    YmCommonConst.NEW_SCH_WORK_KIND_SVMU.equals(sSchCode)){ // SLAB 이송하차					
					   	
					   	if("0".equals(RemainCount)){//잔여매수
							LastOrderId		= "E";
						}else{
							LastOrderId		= "";
						}	 	
					}   		 	
				}
				/*
				 *	26.	MESSAGE
				 */
				{ 
					Message1			= sMsg1;
					Message2			= "";
					
					
					if("".equals(StringHelper.evl(Message1, ""))){
					
						JDTORecord nextWorkJR = dao.getNextCraneWorkInto(sYdGp,
															   		sBayGp,
															   		sEquipNo);
						    	
					    	if(nextWorkJR != null){
					    		Message1 = StringHelper.evl(nextWorkJR.getFieldString("SCH_WORK_KIND"), "");					
				    			logger.println(LogLevel.DEBUG,this, "NEXT-WORK-INFO =>"+Message1);
						}
					}
					
					// CTC 보급, WB보급일 때 마지막 작업지시 구분 셋팅시 
					// 후 장입작업 동을 메세지에 담아서 준다
					if(isNextLotNoBay){
						Message2 = "";
						
						ymCommonDAO yCd = ymCommonDAO.getInstance();
						JDTORecord 	jtR = yCd.readCurZoinLotNo_Bay();
						 
						if(jtR != null){
							//다음 보급되어야 할 장입LOT 번호가 적치된 동
							Message2 = StringHelper.evl(jtR.getFieldString("BAY"), "");
						}
						
					}
//					if("".equals(StringHelper.evl(Message2, "")) && sYdGp.equals("3")){
//						Message2	= StringHelper.evl(schInfo.getFieldString("YD_GP"),"");
//					}
				}	
				
			}
			
			sMsg.append(YmCommonUtil.FillToString(TC				,iTC));
			sMsg.append(YmCommonUtil.FillToString(sDate				,iDate));
			sMsg.append(YmCommonUtil.FillToString(sTime				,iTime));
			sMsg.append(YmCommonUtil.FillToString(Form				,iForm));
			sMsg.append(YmCommonUtil.FillToNumber(Message_Length	,iMessage_Length));
			sMsg.append(YmCommonUtil.FillToString(CraneNo			,iCraneNo));
			sMsg.append(YmCommonUtil.FillToString(ProcessId			,iProcessId));
			sMsg.append(YmCommonUtil.FillToString(DriveMode			,iDriveMode));
			sMsg.append(YmCommonUtil.FillToString(TroubleRecovery		,iTroubleRecovery));
			sMsg.append(YmCommonUtil.FillToNumber(WorkOrderSeq		,iWorkOrderSeq));
			sMsg.append(YmCommonUtil.FillToString(LastOrderId			,iLastOrderId));
			sMsg.append(YmCommonUtil.FillToString(WorkOrderId			,iWorkOrderId));
			sMsg.append(YmCommonUtil.FillToString(SchCode			,iSchCode));
			sMsg.append(YmCommonUtil.FillToString(SchCodeName		,iSchCodeName));
			sMsg.append(YmCommonUtil.FillToString(MainWorkId			,iMainWorkId));
			sMsg.append(YmCommonUtil.FillToString(TCNo				,iTCNo));
			sMsg.append(YmCommonUtil.FillToString(TCWorkStopPosition	,iTCWorkStopPosition));
			sMsg.append(YmCommonUtil.FillToString(CarNo				,iCarNo));
			sMsg.append(YmCommonUtil.FillToNumber(StackCount		,iStackCount));
			sMsg.append(YmCommonUtil.FillToNumber(RemainCount		,iRemainCount));
			sMsg.append(YmCommonUtil.FillToString(UpAddress			,iUpAddress));
			sMsg.append(YmCommonUtil.FillToString(PutAddress			,iPutAddress));
			sMsg.append(YmCommonUtil.FillToNumber(UpXAddress		,iUpXAddress));
			sMsg.append(YmCommonUtil.FillToNumber(UpXPlusRange		,iUpXPlusRange));
			sMsg.append(YmCommonUtil.FillToNumber(UpXMinusRange	,iUpXMinusRange));
			sMsg.append(YmCommonUtil.FillToNumber(UpYAddress		,iUpYAddress));
			sMsg.append(YmCommonUtil.FillToNumber(UpYPlusRange		,iUpYPlusRange));
			sMsg.append(YmCommonUtil.FillToNumber(UpYMinusRange	,iUpYMinusRange));
			sMsg.append(YmCommonUtil.FillToNumber(PutXAddress		,iPutXAddress));
			sMsg.append(YmCommonUtil.FillToNumber(PutXPlusRange		,iPutXPlusRange));
			sMsg.append(YmCommonUtil.FillToNumber(PutXMinusRange	,iPutXMinusRange));
			sMsg.append(YmCommonUtil.FillToNumber(PutYAddress		,iPutYAddress));
			sMsg.append(YmCommonUtil.FillToNumber(PutYPlusRange		,iPutYPlusRange));
			sMsg.append(YmCommonUtil.FillToNumber(PutYMinusRange	,iPutYMinusRange));
			sMsg.append(YmCommonUtil.FillToNumber(WorkOrderCount	,iWorkOrderCount));
			sMsg.append(YmCommonUtil.FillToString(ProductId			,iProductId));
			sMsg.append(YmCommonUtil.FillToString(OrderId				,iOrderId));
			sMsg.append(YmCommonUtil.FillToString(OrderNo			,iOrderNo));
			sMsg.append(YmCommonUtil.FillToString(CradNo				,iCradNo));
			sMsg.append(YmCommonUtil.FillToString(GroupInfo1			,iGroupInfo1));
			sMsg.append(YmCommonUtil.FillToString(ProductNo1			,iProductNo1));
			sMsg.append(YmCommonUtil.FillToNumber(Thick1			,iThick1));
			sMsg.append(YmCommonUtil.FillToNumber(Width1			,iWidth1));
			sMsg.append(YmCommonUtil.FillToNumber(Length1			,iLength1));
			sMsg.append(YmCommonUtil.FillToNumber(Weight1			,iWeight1));
			sMsg.append(YmCommonUtil.FillToString(BuySlabNo1			,iBuySlabNo1));
			sMsg.append(YmCommonUtil.FillToString(GroupInfo2			,iGroupInfo2));
			sMsg.append(YmCommonUtil.FillToString(ProductNo2			,iProductNo2));
			sMsg.append(YmCommonUtil.FillToNumber(Thick2			,iThick2));
			sMsg.append(YmCommonUtil.FillToNumber(Width2			,iWidth2));
			sMsg.append(YmCommonUtil.FillToNumber(Length2			,iLength2));
			sMsg.append(YmCommonUtil.FillToNumber(Weight2			,iWeight2));
			sMsg.append(YmCommonUtil.FillToString(BuySlabNo2			,iBuySlabNo2));
			sMsg.append(YmCommonUtil.FillToNumber(UpPositionHeight	,iUpPositionHeight));
			sMsg.append(YmCommonUtil.FillToNumber(PutPositionHeight	,iPutPositionHeight));
			sMsg.append(YmCommonUtil.FillToString(Message1			,iMessage1));
			sMsg.append(YmCommonUtil.FillToStringDesc(Message2		,iMessage2));
			
			jRecord.setField("sMessage",sMsg);  //A열연 SLAB야드 RETRUN 타입 수정(MCH)
			jRecord.setField("TC_CD",TC);
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
//	    return sMsg.toString();
		return jRecord;
	}
	
	private JDTORecord setCraneBSlabMsgInfo_backup(String sSchId){
		
		//A열연 SLAB야드 추가 (MCH) return String 이었던걸 JDTORecord로 수정했음.		
		JDTORecord jRecord = JDTORecordFactory.getInstance().create();		
		StringBuffer sMsg = new StringBuffer();
		
		String TC					= "";
		String sDate				= "";
		String sTime				= "";
		String Form				= "";
		String Message_Length		= "";
		String CraneNo			= "";
		String ProcessId			= "";
		String DriveMode			= "";
		String TroubleRecovery		= "";
		String WorkOrderSeq		= "";
		String LastOrderId			= "";
		String WorkOrderId			= "";
		String SchCode			= "";
		String SchCodeName		= "";
		String MainWorkId			= "";
		String TCNo				= "";
		String TCWorkStopPosition	= "";
		String CarNo				= "";
		String StackCount			= "";
		String RemainCount			= "";
		String UpAddress			= "";
		String PutAddress			= "";
		String UpXAddress			= "";
		String UpXPlusRange		= "";
		String UpXMinusRange		= "";
		String UpYAddress			= "";
		String UpYPlusRange		= "";
		String UpYMinusRange		= "";
		String PutXAddress			= "";
		String PutXPlusRange		= "";
		String PutXMinusRange		= "";
		String PutYAddress			= "";
		String PutYPlusRange		= "";
		String PutYMinusRange		= "";
		String WorkOrderCount		= "";
		String ProductId			= "";
		String OrderId				= "";
		String OrderNo				= "";
		String CradNo				= "";
		String GroupInfo1			= "";
		String ProductNo1			= "";
		String Thick1				= "";
		String Width1				= "";
		String Length1				= "";
		String Weight1				= "";
		String BuySlabNo1			= "";
		String GroupInfo2			= "";
		String ProductNo2			= "";
		String Thick2				= "";
		String Width2				= "";
		String Length2				= "";
		String Weight2				= "";
		String BuySlabNo2			= "";
		String UpPositionHeight		= "";
		String PutPositionHeight		= "";
		String Message1			= "";
		String Message2			= "";
		
		int iTC					=  7;
		int iDate					= 10;
		int iTime					=  8;
		int iForm					=  1;
		int iMessage_Length		=  4;
		int iCraneNo				=  6;
		int iProcessId				=  1;
		int iDriveMode				=  1;
		int iTroubleRecovery		=  1;
		int iWorkOrderSeq			=  2;
		int iLastOrderId			=  1;
		int iWorkOrderId			=  1;
		int iSchCode				=  4;
		int iSchCodeName			= 20;
		int iMainWorkId			=  2;
		int iTCNo					=  4;
		int iTCWorkStopPosition		=  6;
		int iCarNo					= 12;
		int iStackCount			=  2;
		int iRemainCount			=  2;
		int iUpAddress				= 10;
		int iPutAddress			= 10;
		int iUpXAddress			=  6;
		int iUpXPlusRange			=  4;
		int iUpXMinusRange		=  4;
		int iUpYAddress			=  6;
		int iUpYPlusRange			=  4;
		int iUpYMinusRange		=  4;
		int iPutXAddress			=  6;
		int iPutXPlusRange			=  4;
		int iPutXMinusRange		=  4;
		int iPutYAddress			=  6;
		int iPutYPlusRange			=  4;
		int iPutYMinusRange		=  4;
		int iWorkOrderCount			=  2;
		int iProductId				=  2;
		int iOrderId				=  1;
		int iOrderNo				= 10;
		int iCradNo				=  6;
		int iGroupInfo1				= 11;
		int iProductNo1			= 13;
		int iThick1				=  7;
		int iWidth1				=  6;
		int iLength1				=  6;
		int iWeight1				=  5;
		int iBuySlabNo1			= 25;
		int iGroupInfo2				= 11;
		int iProductNo2			= 13;
		int iThick2				=  7;
		int iWidth2				=  6;
		int iLength2				=  6;
		int iWeight2				=  5;
		int iBuySlabNo2			= 25;
		int iUpPositionHeight		=  5;
		int iPutPositionHeight		=  5;
		int iMessage1				= 40;
		int iMessage2				= 40;
		int iTotalLength			=398;
							   
		try{
			
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			
			/*
			 *	1.	HEADER INFO
			 *	2.	CRANE NO 
			 *	3.	처리구분
			 *		SPACE:자동	B:화면
			 *	4.	운전모드
			 *		0:ONLINE	1:OFFLINE
			 *	5.	고장/복구
			 *		0:정상	1:고장
			 *	6.	작업지시순번
			 *		00 ~ 99
			 *	8.	작업지시구분
			 *		W:대기	U:권상지시	P:권하지시
			 */
			
			sDate				= YmCommonUtil.getCurDate("yyyy-MM-dd");
			sTime				= YmCommonUtil.getCurDate("HH-mm-ss");
			Form					= "I";
			Message_Length		= iTotalLength+"";
			CraneNo				= sSchId;
			{
				//A열연 SLAB야드 추가 (MCH)
				if(YmCommonConst.YD_GP_0.equals(sSchId.substring(0,1)) 
						&& YmCommonConst.BAY_GP_A.equals(sSchId.substring(1,2))){
					TC	= YmCommonConst.TC_HM1BP01;					
				}else if(YmCommonConst.YD_GP_0.equals(sSchId.substring(0,1)) 
							&& YmCommonConst.BAY_GP_B.equals(sSchId.substring(1,2))){
					TC	= YmCommonConst.TC_HM1BP51;					
				}else{
					TC	= YmCommonConst.TC_CM1BP01;	
				}
				
				JDTORecord jtR = dao.getEquipInfoWithEquipNo(sSchId.substring(0,1),//sYdGp
												            sSchId.substring(4)); //sEquipNo
				
				String sWorkMode  = StringHelper.evl(jtR.getFieldString("WORK_MODE"),"");
				String sEquipStat = StringHelper.evl(jtR.getFieldString("EQUIP_STAT"),"");
				
				DriveMode		= (YmCommonConst.WORK_MODE_O.equals(sWorkMode) ? "0" : 
								  (YmCommonConst.WORK_MODE_C.equals(sWorkMode) ? "1" : ""));
				TroubleRecovery	= (YmCommonConst.WORK_MODE_O.equals(sEquipStat)? "0" : 
								  (YmCommonConst.WORK_MODE_C.equals(sEquipStat)? "1" : ""));
			}
			WorkOrderId			= "W";
			
			///////////////////////////////////////////////////////////////////
			///////////////////////////////////////////////////////////////////
			// W/B 보급요구와 동일쿼리 사용 단 C동 야드만을 대상으로 한다.
			String sQuery 	= "ym.steelinfo.steelinforecv.YdStockDAO.selectWBSlabSearch_03";
			List wbL		= dao.getListData(sQuery, new Object[]{YmCommonConst.SEARCH_C_BAY_GP,
	 													  YmCommonConst.SEARCH_C_BAY_GP,
														  YmCommonConst.SEARCH_C_BAY_GP,
														  YmCommonConst.SEARCH_C_BAY_GP});
			
			if (wbL		== null ||
	    		    wbL.size()	== 0){
				logger.println(LogLevel.DEBUG,this, "작업지시 전문발생 스케쥴 정보가 존재안함.");
		    	}else{	
			
				//예약정보 있은면 구분자 변경
				WorkOrderId			= "R";
				
				JDTORecord schInfo     	= (JDTORecord) wbL.get(0);
				
				String sStockId		= StringHelper.evl(schInfo.getFieldString("STOCK_ID"),"");
				String sUpLoc 			= 	StringHelper.evl(schInfo.getFieldString("STACK_COL_GP"),"")	+
									  	StringHelper.evl(schInfo.getFieldString("STACK_BED_GP"),"")	+
									  	StringHelper.evl(schInfo.getFieldString("STACK_LAYER_GP"),"");
				
				String sYdGp 		= YmCommonConst.YD_GP_2;
				String sBayGp 		= YmCommonConst.BAY_GP_C;
				String sEquipKind	= YmCommonConst.EQUIP_KIND_CR;
				String sEquipNo 	= "C2";
				String sSchCode 	= YmCommonConst.NEW_SCH_WORK_KIND_SWLI;
				String sMainWorkYn 	= YmCommonConst.MAIN_WORK_M;
				String sPutLoc 		= "2CWB010101";
				
				/*
				 *	9.	스케쥴코드
				 */
				{ 
					SchCode			= sSchCode;
				}
				/*
				 *	9.	스케쥴명칭
				 */
				{ 
					ymCommonDAO dao  = ymCommonDAO.getInstance();
					String sQueryId  = "ym.common.dao.ymCommonDAO.getCodeToName";	   	
				   	JDTORecord comJr = dao.getCommonInfo(sQueryId,new Object[]{"YM104","2",
																			   sSchCode});
																			   
					SchCodeName		 = StringHelper.evl(comJr.getFieldString("CLASS2_NAME1"), "");
				}
				/*
				 *	10.	주작업구분
				 */
				{ 
					MainWorkId		= YmCommonUtil.getLegacyDataWithCur(sMainWorkYn);
				}
				/*
				 *	16.	권상,권하 X Y 좌표 및 오차범위
				 */
				{
					UpAddress = sUpLoc;
					PutAddress= sPutLoc;
					
					String sUpXLeft 	= "";
					String sUpXRight 	= "";
					String sUpYUp 	= "";
					String sUpYDown 	= "";
					 
					String sPutXLeft 	= "";
					String sPutXRight 	= "";
					String sPutYUp 	= "";
					String sPutYDown 	= ""; 
					
					JDTORecord ruleUpX = dao.getStackRuleInfo_002(sUpLoc.substring(0, 6),
																  YmCommonConst.STACK_RULE_CD_XCD);
					if(ruleUpX != null){
						sUpXLeft 	= StringHelper.evl(ruleUpX.getFieldString("STACK_RULE_MIN"), "");
						sUpXRight 	= StringHelper.evl(ruleUpX.getFieldString("STACK_RULE_MAX"), "");
					}
					JDTORecord ruleUpY = dao.getStackRuleInfo_002(sUpLoc.substring(0, 6),
																  YmCommonConst.STACK_RULE_CD_YCD);
					if(ruleUpY != null){
						sUpYUp 		= StringHelper.evl(ruleUpY.getFieldString("STACK_RULE_MAX"), "");
						sUpYDown 	= StringHelper.evl(ruleUpY.getFieldString("STACK_RULE_MIN"), "");
					}
					
					JDTORecord rulePutX = dao.getStackRuleInfo_002(sPutLoc.substring(0, 6),
																   YmCommonConst.STACK_RULE_CD_XCD);
					if(rulePutX != null){
						sPutXLeft 	= StringHelper.evl(rulePutX.getFieldString("STACK_RULE_MIN"), "");
						sPutXRight 	= StringHelper.evl(rulePutX.getFieldString("STACK_RULE_MAX"), "");
					}
					
					JDTORecord rulePutY = dao.getStackRuleInfo_002(sPutLoc.substring(0, 6),
																   YmCommonConst.STACK_RULE_CD_YCD);
					if(rulePutY != null){
						sPutYUp 	= StringHelper.evl(rulePutY.getFieldString("STACK_RULE_MAX"), "");
						sPutYDown 	= StringHelper.evl(rulePutY.getFieldString("STACK_RULE_MIN"), "");
					}
													
					if(UpAddress.length() == 10){
						JDTORecord upR = dao.getStackLayerInfoWithPk(sUpLoc.substring(0, 6),
																	 sUpLoc.substring(6, 8),
																	 sUpLoc.substring(8,10));
					    	String sUpXPosition = "";
					    	String sUpYPosition = "";
					    	
					    	if(upR != null){
				    			sUpXPosition = StringHelper.evl(upR.getFieldString("STACK_LAYER_X_AXIS"), "");					
							sUpYPosition = StringHelper.evl(upR.getFieldString("STACK_LAYER_Y_AXIS"), "");
						}else{
							/*
				    			 * 설비정보는 권상시 적치단정보를 삭제하기 때문에 좌표정보를 가져올 수 없다.
				    			 * 따라서, 적치열정보를 가져온다.
				    			 */
							JDTORecord upSubR = dao.getStackColInfoWithPk(sUpLoc.substring(0, 6));
							if(upSubR != null){
								sUpXPosition = StringHelper.evl(upSubR.getFieldString("STACK_COL_RULE_X_AXIS"), "");					
								sUpYPosition = StringHelper.evl(upSubR.getFieldString("STACK_COL_RULE_Y_AXIS"), "");
							}
						}
						UpXAddress			= sUpXPosition;
						UpXPlusRange		= sUpXLeft;
						UpXMinusRange		= sUpXRight;
						UpYAddress			= sUpYPosition;
						UpYPlusRange		= sUpYUp;
						UpYMinusRange		= sUpYDown;
				    	}
	
				    	if(PutAddress.length() == 10){
				    		JDTORecord putR = dao.getStackLayerInfoWithPk(sPutLoc.substring(0, 6),
																		  sPutLoc.substring(6, 8),
																		  sPutLoc.substring(8,10));
					    	String sPutXPosition = "";
					    	String sPutYPosition = "";
					    	
					    	if(putR != null){
					    		sPutXPosition = StringHelper.evl(putR.getFieldString("STACK_LAYER_X_AXIS"), "");					
							sPutYPosition = StringHelper.evl(putR.getFieldString("STACK_LAYER_Y_AXIS"), "");
						}else{
							/*
				    			 * 설비정보는 권상시 적치단정보를 삭제하기 때문에 좌표정보를 가져올 수 없다.
				    			 * 따라서, 적치열정보를 가져온다.
				    			 */
							JDTORecord putSubR = dao.getStackColInfoWithPk(sPutLoc.substring(0, 6));
							if(putSubR != null){
								sPutXPosition = StringHelper.evl(putSubR.getFieldString("STACK_COL_RULE_X_AXIS"), "");					
								sPutYPosition = StringHelper.evl(putSubR.getFieldString("STACK_COL_RULE_Y_AXIS"), "");
							}
						}
						PutXAddress			= sPutXPosition;
						PutXPlusRange		= sPutXLeft;
						PutXMinusRange		= sPutXRight;
						PutYAddress			= sPutYPosition;
						PutYPlusRange		= sPutYUp;
						PutYMinusRange		= sPutYDown;
				    	}
			    	}
		    	
				String sSlabNo1 	= sStockId;
			    	String sSlabNo2 	= "";
			    	String sGripCount 	= "1"; //1매 작업
			    	
			    	/*
				 *	21.	작업지시매수
				 */
				{ 
					WorkOrderCount	= sGripCount;
				}	
		    		/*
				 *	22.	SLAB #1 기본정보 
				 *		-	제작번호/행번
				 *		-	두께
				 *		-	폭
				 *		-	길이
				 *		-	중량
				 		두께	CHAR	7	㎜	소수점3자리 (###.###)
						폭		CHAR	6	㎜	소수점1자리 (####.#)
						길이	CHAR	6	㎜	Slab 공통 그대로
						중량	CHAR	5	Kg	Slab 공통 그대로
				 */
				{ 
					GroupInfo1	= sSlabNo1;
					
					JDTORecord cInfo = dao.getSlabCommonInfo(sSlabNo1);
			    		
			    		if(cInfo != null){
			    			
						ProductNo1	= YmCommonUtil.FillToString(StringHelper.evl(cInfo.getFieldString("ORD_NO"), ""),10)+	
															StringHelper.evl(cInfo.getFieldString("ORD_DTL"), "");
						Thick1		= StringHelper.replaceStr(
														YmCommonUtil.format(
														StringHelper.evl(cInfo.getFieldString("SLAB_T"), ""),3,3),".",""); 
						Width1		= StringHelper.replaceStr(
														YmCommonUtil.format(
														StringHelper.evl(cInfo.getFieldString("SLAB_W"), ""),4,1),".",""); 
						Length1		= StringHelper.evl(cInfo.getFieldString("SLAB_LEN"), "");
						Weight1		= StringHelper.evl(cInfo.getFieldString("SLAB_WT"), ""); 
						BuySlabNo1	= StringHelper.evl(cInfo.getFieldString("BUY_SLAB_NO"),"");			  
					}
				}		
				/*
				 *	23.	SLAB #2 기본정보 
				 *		-	제작번호/행번
				 *		-	두께
				 *		-	폭
				 *		-	길이
				 *		-	중량
				 */
				{ 
					if(!"".equals(sSlabNo2)){
						
						GroupInfo2	= sSlabNo2;
						
						JDTORecord cInfo = dao.getSlabCommonInfo(sSlabNo2);
				    		
						if(cInfo != null){
						
							ProductNo2	= YmCommonUtil.FillToString(StringHelper.evl(cInfo.getFieldString("ORD_NO"), ""),10)+	
										  StringHelper.evl(cInfo.getFieldString("ORD_DTL"), "");
							Thick2		= StringHelper.replaceStr(
										  YmCommonUtil.format(
										  StringHelper.evl(cInfo.getFieldString("SLAB_T"), ""),3,3),".",""); 
							Width2		= StringHelper.replaceStr(
										  YmCommonUtil.format(
										  StringHelper.evl(cInfo.getFieldString("SLAB_W"), ""),4,1),".",""); 
							Length2		= StringHelper.evl(cInfo.getFieldString("SLAB_LEN"), "");
							Weight2		= StringHelper.evl(cInfo.getFieldString("SLAB_WT"), ""); 
							BuySlabNo2	= StringHelper.evl(cInfo.getFieldString("BUY_SLAB_NO"),"");				  
						}
						}
				}	
			}
			
			sMsg.append(YmCommonUtil.FillToString(TC				,iTC));
			sMsg.append(YmCommonUtil.FillToString(sDate				,iDate));
			sMsg.append(YmCommonUtil.FillToString(sTime				,iTime));
			sMsg.append(YmCommonUtil.FillToString(Form				,iForm));
			sMsg.append(YmCommonUtil.FillToNumber(Message_Length	,iMessage_Length));
			sMsg.append(YmCommonUtil.FillToString(CraneNo			,iCraneNo));
			sMsg.append(YmCommonUtil.FillToString(ProcessId			,iProcessId));
			sMsg.append(YmCommonUtil.FillToString(DriveMode			,iDriveMode));
			sMsg.append(YmCommonUtil.FillToString(TroubleRecovery		,iTroubleRecovery));
			sMsg.append(YmCommonUtil.FillToNumber(WorkOrderSeq		,iWorkOrderSeq));
			sMsg.append(YmCommonUtil.FillToString(LastOrderId			,iLastOrderId));
			sMsg.append(YmCommonUtil.FillToString(WorkOrderId			,iWorkOrderId));
			sMsg.append(YmCommonUtil.FillToString(SchCode			,iSchCode));
			sMsg.append(YmCommonUtil.FillToString(SchCodeName		,iSchCodeName));
			sMsg.append(YmCommonUtil.FillToString(MainWorkId			,iMainWorkId));
			sMsg.append(YmCommonUtil.FillToString(TCNo				,iTCNo));
			sMsg.append(YmCommonUtil.FillToString(TCWorkStopPosition	,iTCWorkStopPosition));
			sMsg.append(YmCommonUtil.FillToString(CarNo				,iCarNo));
			sMsg.append(YmCommonUtil.FillToNumber(StackCount		,iStackCount));
			sMsg.append(YmCommonUtil.FillToNumber(RemainCount		,iRemainCount));
			sMsg.append(YmCommonUtil.FillToString(UpAddress			,iUpAddress));
			sMsg.append(YmCommonUtil.FillToString(PutAddress			,iPutAddress));
			sMsg.append(YmCommonUtil.FillToNumber(UpXAddress		,iUpXAddress));
			sMsg.append(YmCommonUtil.FillToNumber(UpXPlusRange		,iUpXPlusRange));
			sMsg.append(YmCommonUtil.FillToNumber(UpXMinusRange	,iUpXMinusRange));
			sMsg.append(YmCommonUtil.FillToNumber(UpYAddress		,iUpYAddress));
			sMsg.append(YmCommonUtil.FillToNumber(UpYPlusRange		,iUpYPlusRange));
			sMsg.append(YmCommonUtil.FillToNumber(UpYMinusRange	,iUpYMinusRange));
			sMsg.append(YmCommonUtil.FillToNumber(PutXAddress		,iPutXAddress));
			sMsg.append(YmCommonUtil.FillToNumber(PutXPlusRange		,iPutXPlusRange));
			sMsg.append(YmCommonUtil.FillToNumber(PutXMinusRange	,iPutXMinusRange));
			sMsg.append(YmCommonUtil.FillToNumber(PutYAddress		,iPutYAddress));
			sMsg.append(YmCommonUtil.FillToNumber(PutYPlusRange		,iPutYPlusRange));
			sMsg.append(YmCommonUtil.FillToNumber(PutYMinusRange	,iPutYMinusRange));
			sMsg.append(YmCommonUtil.FillToNumber(WorkOrderCount	,iWorkOrderCount));
			sMsg.append(YmCommonUtil.FillToString(ProductId			,iProductId));
			sMsg.append(YmCommonUtil.FillToString(OrderId				,iOrderId));
			sMsg.append(YmCommonUtil.FillToString(OrderNo			,iOrderNo));
			sMsg.append(YmCommonUtil.FillToString(CradNo				,iCradNo));
			sMsg.append(YmCommonUtil.FillToString(GroupInfo1			,iGroupInfo1));
			sMsg.append(YmCommonUtil.FillToString(ProductNo1			,iProductNo1));
			sMsg.append(YmCommonUtil.FillToNumber(Thick1			,iThick1));
			sMsg.append(YmCommonUtil.FillToNumber(Width1			,iWidth1));
			sMsg.append(YmCommonUtil.FillToNumber(Length1			,iLength1));
			sMsg.append(YmCommonUtil.FillToNumber(Weight1			,iWeight1));
			sMsg.append(YmCommonUtil.FillToString(BuySlabNo1			,iBuySlabNo1));
			sMsg.append(YmCommonUtil.FillToString(GroupInfo2			,iGroupInfo2));
			sMsg.append(YmCommonUtil.FillToString(ProductNo2			,iProductNo2));
			sMsg.append(YmCommonUtil.FillToNumber(Thick2			,iThick2));
			sMsg.append(YmCommonUtil.FillToNumber(Width2			,iWidth2));
			sMsg.append(YmCommonUtil.FillToNumber(Length2			,iLength2));
			sMsg.append(YmCommonUtil.FillToNumber(Weight2			,iWeight2));
			sMsg.append(YmCommonUtil.FillToString(BuySlabNo2			,iBuySlabNo2));
			sMsg.append(YmCommonUtil.FillToNumber(UpPositionHeight	,iUpPositionHeight));
			sMsg.append(YmCommonUtil.FillToNumber(PutPositionHeight	,iPutPositionHeight));
			sMsg.append(YmCommonUtil.FillToString(Message1			,iMessage1));
			sMsg.append(YmCommonUtil.FillToString(Message2			,iMessage2));
			
			jRecord.setField("sMessage",sMsg);//A열연 SLAB야드 RETRUN 타입수정(MCH)
			jRecord.setField("TC_CD",TC);
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return jRecord;
	}
		
	/**
	 *	SLAB GRIP 가능한지를 체크해서 가능하면
	 *	SLAB GRIP 관련 항목을 셋팅한다.
	 */
	private boolean setCraneBSlabGrapInfo(String sTSchId){
		
		boolean isSuccess = false;
		
		try{
			
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			
				/*
		    	 	*	0.1	기본 스케쥴 정보를 가져온다.
		    	 	*/
					JDTORecord schInfo 	= dao.getCraneSchInfo(sTSchId);
					
					logger.println(LogLevel.DEBUG,this, "sSchId=" + sTSchId);
				    	if(schInfo == null){
				    		logger.println(LogLevel.DEBUG,this, "=SLAB GRIP => 전문발생 스케쥴 정보가 존재안함.");
				    		return false;
				    	}
			    	
					String sTYdGp 	= StringHelper.evl(schInfo.getFieldString("YD_GP"),"");
					String sTBayGp 	= StringHelper.evl(schInfo.getFieldString("BAY_GP"),"");
					String sTEquipKind	= YmCommonConst.EQUIP_KIND_CR;
					String sTEquipNo 	= StringHelper.evl(schInfo.getFieldString("SCH_WORK_EQUIP_NO"),"");
					String sTStockId	= StringHelper.evl(schInfo.getFieldString("STOCK_ID"),"");
					String sTSchCode 	= StringHelper.evl(schInfo.getFieldString("SCH_WORK_KIND"),"");
					String sTUpLoc 	= StringHelper.evl(schInfo.getFieldString("CRANE_WORD_UP_LOC"),"");
					String sTPutLoc 	= StringHelper.evl(schInfo.getFieldString("CRANE_WORD_PUT_LOC"),"");
					String sTSchStat 	= StringHelper.evl(schInfo.getFieldString("SCH_WORK_STAT"),"");
					String sTMainWorkYn 	= StringHelper.evl(schInfo.getFieldString("SCH_WORK_AID_YN"),"");
					String sTSchWprefer  	= StringHelper.evl(schInfo.getFieldString("SCH_WPREFER"),"");
					
					if(!YmCommonConst.SCH_WORK_STAT_S.equals(sTSchStat)&&
				       !YmCommonConst.SCH_WORK_STAT_1.equals(sTSchStat)){
				    
				       	logger.println(LogLevel.DEBUG,this, "=SLAB GRIP => UP지시 상태에서만 가능.");
			    		return false;
				    }
			    
			    /*
		    	 *	0.2	GRIP 스케쥴 정보를 가져온다.
		    	 */
				    JDTORecord gripJr = dao.getSlabGripInfo_01(sTYdGp,
									    					   sTBayGp,
									    					   sTEquipKind,
									    					   sTEquipNo,
									    					   sTStockId,
									    					   sTSchCode);
								    					 
			    	if(gripJr == null){
			    		logger.println(LogLevel.DEBUG,this, "=SLAB GRIP => GRIP 대상 정보가 존재안함.");
			    		return false;
			    	}
			    	
			    	String sGSchId 	= StringHelper.evl(gripJr.getFieldString("SCH_ID"),"");
			    	String sGStockId 	= StringHelper.evl(gripJr.getFieldString("STOCK_ID"),"");
			    	String sGUpLoc 	= StringHelper.evl(gripJr.getFieldString("CRANE_WORD_UP_LOC"),"");
			    	String sGPutLoc 	= StringHelper.evl(gripJr.getFieldString("CRANE_WORD_PUT_LOC"),"");
		    	
		    	/*
		    	 *	0.3	2개의 SLAB 기본정보를 가져온다.
		    	 */
		    	 
			    	JDTORecord stockJr1 = dao.getSlabCommonInfo(sTStockId);
			    	JDTORecord stockJr2 = dao.getSlabCommonInfo(sGStockId);
			    	
			    	if(stockJr1 == null){
			    		logger.println(LogLevel.DEBUG,this, "=SLAB GRIP["+sTStockId+":"+sGStockId+"] => SLAB 공통정보가 존재안함.");
			    		return false;
			    	}
			    	if(stockJr2 == null){
			    		logger.println(LogLevel.DEBUG,this, "=SLAB GRIP["+sTStockId+":"+sGStockId+"] => SLAB 공통정보가 존재안함.");
			    		return false;
			    	}
			    	
					String sTSlabWt = StringHelper.evl(stockJr1.getFieldString("SLAB_WT"),"0");
					String sGSlabWt = StringHelper.evl(stockJr2.getFieldString("SLAB_WT"),"0");
					
					String sTSlabW = StringHelper.evl(stockJr1.getFieldString("SLAB_W"),"0");
					String sGSlabW = StringHelper.evl(stockJr2.getFieldString("SLAB_W"),"0");
					
					String sTScarfingYn = StringHelper.evl(stockJr1.getFieldString("SCARFING_DONE_YN"),"N");
					String sGScarfingYn = StringHelper.evl(stockJr2.getFieldString("SCARFING_DONE_YN"),"N");
					//이송장비 구분 : PT , TR
					String sTEqpCD = StringHelper.evl(stockJr1.getFieldString("TRN_EQP_CD"),"PT");
					String sGEqpCD = StringHelper.evl(stockJr2.getFieldString("TRN_EQP_CD"),"PT");
		        
		    	/*
		    	 *	1.	크레인 능력톤수 및 가능 매수 정보를 가져온다.
		    	 */
		    		JDTORecord equipJr = dao.getEquipInfoWithEquipGp(sTYdGp+
		    														 sTBayGp+
		    														 sTEquipKind+
		    														 sTEquipNo);
		    		
		    		if(equipJr == null){
			    		logger.println(LogLevel.DEBUG,this, "=SLAB GRIP["+sTStockId+":"+sGStockId+"] => 크레인 정보가 존재안함.");
			    		return false;
			    	}
			    	
			    	String sMaxQnty = StringHelper.evl(equipJr.getFieldString("STACK_MAX_QNTY"),"0");
					String sMaxWt 	= StringHelper.evl(equipJr.getFieldString("STACK_MAX_WT"),"0");
					
					int iMaxQnty 	= Integer.parseInt(sMaxQnty);
					if(iMaxQnty < 2){
						
						logger.println(LogLevel.DEBUG,this, "=SLAB GRIP["+sTStockId+":"+sGStockId+"] => 크레인설정이 2매작업으로 되어 있지 않음.");
			    		return false;
					}
					
					double dMaxWt 	= Double.parseDouble(sMaxWt);
					double dTSlabWt = Double.parseDouble(sTSlabWt);
					double dGSlabWt = Double.parseDouble(sGSlabWt);
					double dTSlabW = Double.parseDouble(sTSlabW);
					double dGSlabW = Double.parseDouble(sGSlabW);
					
					logger.println(LogLevel.DEBUG,this, "=SLAB GRIP => dMaxWt	="+dMaxWt);
					logger.println(LogLevel.DEBUG,this, "=SLAB GRIP => dTSlabWt	="+dTSlabWt);
					logger.println(LogLevel.DEBUG,this, "=SLAB GRIP => dGSlabWt	="+dGSlabWt);
					logger.println(LogLevel.DEBUG,this, "=SLAB GRIP => dTSlabW	="+dTSlabW);
					logger.println(LogLevel.DEBUG,this, "=SLAB GRIP => dGSlabW	="+dGSlabW);
					
					if((dTSlabWt + dGSlabWt) > dMaxWt){
						
						logger.println(LogLevel.DEBUG,this, "=SLAB GRIP["+sTStockId+":"+sGStockId+"] => 크레인설정 중량을 초과함.");
			    		return false;
					}
					/*
					 * W/B인 경우 에만 적용 Slab 폭 20mm 이상 제외
					 */
					String sWB_CHK = sTPutLoc.substring(2,4);
					
					if("WB".equals(sWB_CHK)){					
						if( Math.abs(dTSlabW - dGSlabW) > 20.0){
							
							logger.println(LogLevel.DEBUG,this, "=SLAB GRIP["+sTStockId+":"+sGStockId+"] =>WB 크레인설정 폭간격20mm를 초과함.");
				    		return false;
						}
					}else {
						if( Math.abs(dTSlabW - dGSlabW) > 30.0){
							
							logger.println(LogLevel.DEBUG,this, "=SLAB GRIP["+sTStockId+":"+sGStockId+"] => 크레인설정 폭간격 30mm를 초과함.");
				    		return false;
						}
					}
					
					/*
					 *  Slab 폭 2000mm 이상 제외 2015.07.09 
					 */
					if(dTSlabW>=2000 || dGSlabW>=2000){
						logger.println(LogLevel.DEBUG,this, "=SLAB GRIP["+sTStockId+":"+sGStockId+"] => 크레인설정 폭 2000mm를 초과함.");
			    		return false;
					}
					
					/*
					 * TR인 경우 한매라도 스카핑 인 경우 그립 작업 스킵 2011.04.11 지영묵 계장 요청
					 */
//이슈ID:13241		if( sTEqpCD.equals("TR") && (sTScarfingYn.equals("Y") || sGScarfingYn.equals("Y"))){
//						
//						logger.println(LogLevel.DEBUG,this, "=SLAB GRIP["+sTStockId+":"+sGStockId+"] => TR인 경우 한매라도 스카핑 인 경우 그립 작업 스킵.");
//			    		return false;
//					}
		    	/*
		    	 *	3.	재료속성을 체크한다.
				 *		1. CAMBER_YN 이 Y이면 1매 작업
				 *		2. TAPER_SLAB_GP 이 Y이면 1매 작업
				 *		3. LONG_BOW_YN 이 Y이고
				 *		   LONG_BOW_VAL 이 50mm 이상이면 1매 작업
				 */ 
		    		
		    		String sTCamberYn 		= StringHelper.evl(stockJr1.getFieldString("CAMBER_YN"),"");
					String sTTaperSlabGp 	= StringHelper.evl(stockJr1.getFieldString("TAPER_SLAB_GP"),"");
					String sTLongBowYn 		= StringHelper.evl(stockJr1.getFieldString("LONG_BOW_YN"),"");
					String sTLongBowVal 	= StringHelper.evl(stockJr1.getFieldString("LONG_BOW_VAL"),"0");
					String sTLspecAbbsym 	= StringHelper.evl(stockJr1.getFieldString("SPEC_ABBSYM"),"");
					String sTLCwrsltYn	 	= StringHelper.evl(stockJr1.getFieldString("C_WRSLT_CHK"),"");
					
					String sGCamberYn 		= StringHelper.evl(stockJr2.getFieldString("CAMBER_YN"),"");
					String sGTaperSlabGp 	= StringHelper.evl(stockJr2.getFieldString("TAPER_SLAB_GP"),"");
					String sGLongBowYn 		= StringHelper.evl(stockJr2.getFieldString("LONG_BOW_YN"),"");
					String sGLongBowVal 	= StringHelper.evl(stockJr2.getFieldString("LONG_BOW_VAL"),"0");
					String sGLspecAbbsym 	= StringHelper.evl(stockJr2.getFieldString("SPEC_ABBSYM"),"");
					String sGLCwrsltYn	 	= StringHelper.evl(stockJr2.getFieldString("C_WRSLT_CHK"),"");
					
			    		logger.println(LogLevel.DEBUG,this, "=SLAB GRIP => sTCamberYn	="+sTCamberYn);
			    		logger.println(LogLevel.DEBUG,this, "=SLAB GRIP => sTTaperSlabGp="+sTTaperSlabGp);
			    		logger.println(LogLevel.DEBUG,this, "=SLAB GRIP => sTLongBowYn	="+sTLongBowYn);
			    		logger.println(LogLevel.DEBUG,this, "=SLAB GRIP => sTLongBowVal	="+sTLongBowVal);
			    		logger.println(LogLevel.DEBUG,this, "=SLAB GRIP => sTLspecAbbsym	="+sTLspecAbbsym);
			    		logger.println(LogLevel.DEBUG,this, "=SLAB GRIP => sTLCwrsltYn	="+sTLCwrsltYn);
			    		
			    		logger.println(LogLevel.DEBUG,this, "=SLAB GRIP => sGCamberYn	="+sGCamberYn);
			    		logger.println(LogLevel.DEBUG,this, "=SLAB GRIP => sGTaperSlabGp="+sGTaperSlabGp);
			    		logger.println(LogLevel.DEBUG,this, "=SLAB GRIP => sGLongBowYn	="+sGLongBowYn);
			    		logger.println(LogLevel.DEBUG,this, "=SLAB GRIP => sGLongBowVal	="+sGLongBowVal);
			    		logger.println(LogLevel.DEBUG,this, "=SLAB GRIP => sGLspecAbbsym	="+sGLspecAbbsym);
			    		logger.println(LogLevel.DEBUG,this, "=SLAB GRIP => sGLCwrsltYn	="+sGLCwrsltYn);
			    	
//이슈ID:13241  		if(YmCommonConst.USE_YN_Y.equals(sTLCwrsltYn)
//		    				&&(sTScarfingYn.equals("Y"))){
//						logger.println(LogLevel.DEBUG,this, "=SLAB GRIP["+sTStockId+":"+sGStockId+"] => 카본함량0.20이상 스카핑완료재(sTLCwrsltYn)1매작업대상");
//			    		return false;
//					}
		    		
//					if(YmCommonConst.USE_YN_Y.equals(sTCamberYn)){
//						logger.println(LogLevel.DEBUG,this, "=SLAB GRIP["+sTStockId+":"+sGStockId+"] => 재료속성(sTCamberYn)1매작업대상");
//			    		return false;
//					}
//					if(YmCommonConst.USE_YN_Y.equals(sTTaperSlabGp)){
//						logger.println(LogLevel.DEBUG,this, "=SLAB GRIP["+sTStockId+":"+sGStockId+"] => 재료속성(sTTaperSlabGp)1매작업대상");
//			    		return false;
//					}
//이슈ID:13241		if((sTLspecAbbsym.equals("API-J55")||sTLspecAbbsym.equals("JS-S45C")||sTLspecAbbsym.equals("JS-SS490")||sTLspecAbbsym.equals("HSC1470HPF"))
//						&&(sTScarfingYn.equals("Y"))){
//						logger.println(LogLevel.DEBUG,this, "=SLAB GRIP["+sTStockId+":"+sGStockId+"] => 재료속성(sTLspecAbbsym)1매작업대상");
//			    		return false;
//					}
//					double dTLongBowVal = Double.parseDouble(sTLongBowVal);
//					if(YmCommonConst.USE_YN_Y.equals(sTLongBowYn)&&
//					   dTLongBowVal >= 50){
//						logger.println(LogLevel.DEBUG,this, "=SLAB GRIP["+sTStockId+":"+sGStockId+"] => 재료속성(sTLONG_BOW)1매작업대상");
//			    		return false;
//					}
					
					
//이슈ID:13241		if(YmCommonConst.USE_YN_Y.equals(sGLCwrsltYn)
//							&&(sGScarfingYn.equals("Y"))){
//						logger.println(LogLevel.DEBUG,this, "=SLAB GRIP["+sTStockId+":"+sGStockId+"] => 카본함량0.20이상 스카핑완료재(sTLCwrsltYn)1매작업대상");
//			    		return false;
//					}
					
//					if(YmCommonConst.USE_YN_Y.equals(sGCamberYn)){
//						logger.println(LogLevel.DEBUG,this, "=SLAB GRIP["+sTStockId+":"+sGStockId+"] => 재료속성(sGCamberYn)1매작업대상");
//			    		return false;
//					}
//					if(YmCommonConst.USE_YN_Y.equals(sGTaperSlabGp)){
//						logger.println(LogLevel.DEBUG,this, "=SLAB GRIP["+sTStockId+":"+sGStockId+"] => 재료속성(sGTaperSlabGp)1매작업대상");
//			    		return false;
//					}
//이슈ID:13241		if((sGLspecAbbsym.equals("API-J55")||sGLspecAbbsym.equals("JS-S45C")||sGLspecAbbsym.equals("JS-SS490")||sGLspecAbbsym.equals("HSC1470HPF"))
//					   &&(sGScarfingYn.equals("Y"))){
//						logger.println(LogLevel.DEBUG,this, "=SLAB GRIP["+sTStockId+":"+sGStockId+"] => 재료속성(sTLspecAbbsym)1매작업대상");
//			    		return false;
//					}
//					double dGLongBowVal = Double.parseDouble(sGLongBowVal);
//					if(YmCommonConst.USE_YN_Y.equals(sGLongBowYn)&&
//					   dGLongBowVal >= 50){
//						logger.println(LogLevel.DEBUG,this, "=SLAB GRIP["+sTStockId+":"+sGStockId+"] => 재료속성(sGLONG_BOW)1매작업대상");
//			    		return false;
//					}
    
		    	/*
		    	 *	4.	1매 작업가능 스케쥴 코드인지를 체크한다.
		    	 *		-	주작업 대상재일 경우만 체크한다.
		    	 */ 
		    		if(YmCommonConst.MAIN_WORK_M.equals(sTMainWorkYn)){
			    		if(YmCommonConst.NEW_SCH_WORK_KIND_SSLI.equals(sTSchCode)|| //Slab Scarfing 보급
			    		   YmCommonConst.NEW_SCH_WORK_KIND_SSLO.equals(sTSchCode)|| //Slab Scarfing 추출
			    		   YmCommonConst.NEW_SCH_WORK_KIND_SSTO.equals(sTSchCode)||	//Slab Scarfing Take Out 
			    		   //YmCommonConst.NEW_SCH_WORK_KIND_SCLI.equals(sTSchCode)|| //Slab CTC 보급	
			    		   YmCommonConst.NEW_SCH_WORK_KIND_SRPI.equals(sTSchCode)|| //Slab 시편재 보급
			    		   YmCommonConst.NEW_SCH_WORK_KIND_SHSI.equals(sTSchCode)){ //Slab Hand Scarfing 보급
			    		   	
			    			//스카핑 보급 시 밴딩재들은 야드로 내려 놓는 관계로 2매 작업이 가능 함.
			    			if(YmCommonConst.NEW_SCH_WORK_KIND_SSLI.equals(sTSchCode)&&
			    					sGPutLoc.substring(4 , 6).equals("16")    //밴딩스카핑적치야드 16열 
			    			){ //Slab Scarfing 보급
			    				logger.println(LogLevel.DEBUG,this, "=SLAB GRIP["+sTStockId+":"+sGStockId+"] => 밴딩재들은 야드로 내려 놓는 관계로 2매 작업이 가능 함="+sTSchCode);
			    			}else{
				    		   	logger.println(LogLevel.DEBUG,this, "=SLAB GRIP["+sTStockId+":"+sGStockId+"] => 2매 작업종류가 아님="+sTSchCode);
					    		return false;
			    			}
			    		}
			    	}
		    	/*
		    	 *	5.	GRIP 대상재가 존재하면 해당 정보를 셋팅한다.
		    	 *		-	Grip 대상재와 To위치 정보를 수정한다.
		    	 *		-	Grip 대상재 상태정보를 수정한다.
		    	 */ 
		    		
		    		logger.println(LogLevel.DEBUG,this, "=SLAB GRIP["+sTStockId+":"+sGStockId+"] => 2매 작업 정보 셋팅.");
		    		
		    		int iSeq = 0;
		    		
		    		iSeq = dao.updatePutLocInfoWithSchId(sTSchId,
    								          			 sGPutLoc); 
					
				logger.println(LogLevel.DEBUG,this, "=SLAB GRIP["+sTStockId+":"+sGStockId+"] => 수정결과="+iSeq);
				
				iSeq = dao.updateCraneStackLayerStat(sGPutLoc.substring(0, 6),
		    										 sGPutLoc.substring(6, 8),
		    										 sGPutLoc.substring(8,10),
		    										 sTStockId,
		    										 YmCommonConst.STACK_LAYER_STAT_P);
	    			
	    			logger.println(LogLevel.DEBUG,this, "=SLAB GRIP["+sTStockId+":"+sGStockId+"] => 수정결과="+iSeq);
	    										 
				iSeq = dao.updatePutLocInfoWithSchId(sGSchId,
    								          			 sTPutLoc); 
    				
    				iSeq = dao.updateCraneStackLayerStat(sTPutLoc.substring(0, 6),
			    										 sTPutLoc.substring(6, 8),
			    										 sTPutLoc.substring(8,10),
			    										 sGStockId,
			    										 YmCommonConst.STACK_LAYER_STAT_P);
	    			
	    			logger.println(LogLevel.DEBUG,this, "=SLAB GRIP["+sTStockId+":"+sGStockId+"] => 수정결과="+iSeq);				 
    				
    				iSeq = dao.updateGripYnWithSchId(sGSchId,
    								          		 YmCommonConst.GRIP_LOT_YN_G);
    				
    				/*
				 *	최초 작업지시 시점에 작업지시일자를 셋팅한다.
				 */
    				iSeq = dao.updateWbookSchActDdttSchInfo(sGSchId,
													YmCommonUtil.getStringYMDHMS());
				
				/** YJK_ERROR
				 *	기본 스케쥴의 우선순위와 GRIP대상제의 우선순위가 틀리면 기본스케쥴 우선순위로 맞춘다.
				 *	- 일단 긴급작업일 경우만 해당한다.
				 
				if("0".equals(sTSchWprefer)){
				
					iSeq = dao.updateEmergencySchInfo_02(sGSchId);
				}								
				
				 */				  	  
				
    				logger.println(LogLevel.DEBUG,this, "=SLAB GRIP["+sTStockId+":"+sGStockId+"] => 수정결과="+iSeq);
    				
    				logger.println(LogLevel.DEBUG,this, "=SLAB GRIP["+sTStockId+":"+sGStockId+"] => 2매 작업 셋팅 완료.");
			isSuccess = true;
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess;
	}
	
	/**
     * =COIL=
     * 작업지시시점에 TO위치 다시 셋팅
     * 
     * @param String : 스케쥴ID
     * 
     * @return
     * @throws  
     */	
    private boolean setCoilCraneWorkOrderInfo(String sSchId){
		
		boolean isSuccess = false;
						   
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			logger.println(LogLevel.DEBUG,this, "=작업지시 TO LOC CREATE 시작=.");
			
			/**
 			 *	1.	스케쥴정보,저장품정보,적치열정보를 가져온다.
 			 */	
 			 
				JDTORecord schInfo 	= dao.getCraneSchInfo(sSchId);
				if(schInfo == null){
		    		logger.println(LogLevel.DEBUG,this, "=작업지시 TO LOC CREATE =>스케쥴 정보가 존재안함.");
		    		return false;
		    	}
		    	
				String sYdGp 		= StringHelper.evl(schInfo.getFieldString("YD_GP"),"");
				String sBayGp 		= StringHelper.evl(schInfo.getFieldString("BAY_GP"),"");
				String sStockId		= StringHelper.evl(schInfo.getFieldString("STOCK_ID"),"");
				String sSchCode 	= StringHelper.evl(schInfo.getFieldString("SCH_WORK_KIND"),"");
				String sMainWorkYn 	= StringHelper.evl(schInfo.getFieldString("SCH_WORK_AID_YN"),"");
				String sUpLoc 		= StringHelper.evl(schInfo.getFieldString("CRANE_WORD_UP_LOC"),"");
				String sPutLoc 		= StringHelper.evl(schInfo.getFieldString("CRANE_WORD_PUT_LOC"),"");
				String sSchWorkStat = StringHelper.evl(schInfo.getFieldString("SCH_WORK_STAT"),"");
				String sUsageCd 	= YmCommonUtil.getStackColInfoWithPk(sUpLoc.substring(0, 6));
				JDTORecord stockJR 	= dao.getStockInfo(sStockId);
				String sStockMoveTm	= StringHelper.evl(stockJR.getFieldString("STOCK_MOVE_TERM"), "");
				String sWbookId		= StringHelper.evl(stockJR.getFieldString("WBOOK_ID"), "");
				
				JDTORecord wbookInfo= dao.getWbookInfo(sWbookId);
				
				if(wbookInfo == null ){
		    		logger.println(LogLevel.DEBUG,this, "=작업지시 TO LOC CREATE =>작업예약 정보가 존재안함.");
		    		return false;
		    	}
		    	
				String sWbookDecMth = StringHelper.evl(wbookInfo.getFieldString("SCH_WORK_LOC_DECISION_METHOD"), "");
				String sWbookPutLoc = StringHelper.evl(wbookInfo.getFieldString("CRANE_WORD_PUT_LOC"), "");
			
			/**
 			 *	1.1	B열연 코일 UP지시에서는 SKIP
 			 */	
				{
					if(YmCommonConst.YD_GP_3.equals(sYdGp)){
	    		    
				    	if(YmCommonConst.SCH_WORK_STAT_S.equals(sSchWorkStat)||
				    	   YmCommonConst.SCH_WORK_STAT_1.equals(sSchWorkStat)){
				    	   	logger.println(LogLevel.DEBUG,this, "=작업지시 TO LOC CREATE =>B열연 UP지시에서 TO위치 재검색 예외 케이스.");
			    			return false;
				    	}
				    	
				    	
				    	String sQueryId_EmptyBay = "ym.facilitystatus.facilityinquiry.dao.YdStockDAO.getCardNo";
						List listCoilPos = dao.getListData(sQueryId_EmptyBay, new Object[] {StringHelper.evl(sStockId, "")});
					 
						logger.println(LogLevel.DEBUG, this, "listCoilPos.size()"+ listCoilPos.size());
						JDTORecord jtrCoilPos = null;
						if (listCoilPos.size() > 0) {
							for(int j=0;j<listCoilPos.size();j++)
							{
								jtrCoilPos = (JDTORecord)listCoilPos.get(j);
								String sCOIL_CARD_NO = StringHelper.evl(jtrCoilPos.getFieldString("CAR_CARD_NO"),"");
								
								//차량이적 차량별 상차위치 결정 작업
								if(sCOIL_CARD_NO.equals("9999")||
									sCOIL_CARD_NO.equals("9998")||
									sCOIL_CARD_NO.equals("9997")||
									sCOIL_CARD_NO.equals("9996")||
									sCOIL_CARD_NO.equals("9995")){
									logger.println(LogLevel.DEBUG,this, "=작업지시 TO LOC CREATE =>B열연 차량이적 UP지시에서 TO위치 재검색 예외 케이스.");
					    			return false;
								}
							}
						}
				    }
			    		
			    }			
			/**
 			 *	2.1	TO위치 재검색 로직 예외경우 체크
 			 *		- 스케쥴 TO위치 로직 그대로 반영
 			 */		
				{
					boolean isReTry = false;
					
					if(YmCommonConst.MAIN_WORK_M.equals(sMainWorkYn) &&
					   YmCommonUtil.isLineInWork(sSchCode)){
						
						isReTry = false;    							  
					}else{   	 
						
						if(YmCommonConst.MAIN_WORK_M.equals(sMainWorkYn) &&
					       YmCommonConst.SCH_WORK_LOC_DECISION_METHOD_O.equals(sWbookDecMth) && 
					 	   sWbookPutLoc.length() == 10){
					 		
					 		isReTry = true;   
					 	}else{
					 	 	if(YmCommonConst.MAIN_WORK_M.equals(sMainWorkYn)){ // 주작업
						 		if(YmCommonConst.SCH_WORK_LOC_DECISION_METHOD_O.equals(sWbookDecMth) && 
							 	   (sWbookPutLoc.length() == 6 || sWbookPutLoc.length() == 4)){
									isReTry = true;
							 	}else{
							 		
								 	if(YmCommonConst.NEW_SCH_WORK_KIND_CTML.equals(sSchCode)|| 
								 	   YmCommonConst.NEW_SCH_WORK_KIND_CTM2.equals(sSchCode)|| 
		  							   YmCommonConst.NEW_SCH_WORK_KIND_CTM3.equals(sSchCode)
		  							   //|| YmCommonConst.NEW_SCH_WORK_KIND_CTM5.equals(sSchCode)
		  							   //|| YmCommonConst.NEW_SCH_WORK_KIND_CTM6.equals(sSchCode)
		  							   //|| YmCommonConst.NEW_SCH_WORK_KIND_CTM7.equals(sSchCode)
							 	
		  							   
								 	  )
								 	{
								 		
								 		isReTry = true;
								 	}else{
										isReTry = true;
						 			}
						 		}
						 		
						 	}else if(YmCommonConst.SUB_WORK_S.equals(sMainWorkYn)){ // 보조작업
								isReTry = true;	
						 	}
						}
					}
					
					if(isReTry == false){
			    		logger.println(LogLevel.DEBUG,this, "=작업지시 TO LOC CREATE =>TO위치 재검색 예외 케이스.");
			    		return false;
			    	} 
			    }	 	
			/**
 			 *	2.2	TO위치 재검색 로직 예외경우 체크
 			 *		- A열연 SADDLE 이 TO위치인 경우 제외
 			 *		- B열연 대차가 TO위치인 경우 제외
 			 */		
 			 	{
 			 		if(YmCommonConst.YD_GP_1.equals(sYdGp)){
 			 			
	 			 		String sPutUsageCd 		= YmCommonUtil.getStackColInfoWithPk(sPutLoc.substring(0, 6));
						
						if(YmCommonConst.STACK_COL_USAGE_CD_FS.equals(sPutUsageCd)){// FROM SADDLE
							logger.println(LogLevel.DEBUG,this, "=작업지시 TO LOC CREATE =>A열연 SADDLE TO위치 재검색 예외 케이스.");
				    		return false;
						} 			 		
					}
					
					if(YmCommonConst.YD_GP_3.equals(sYdGp)){
 			 			
	 			 		String sPutUsageCd 		= YmCommonUtil.getStackColInfoWithPk(sPutLoc.substring(0, 6));
						
						if(YmCommonConst.STACK_COL_USAGE_CD_CX.equals(sPutUsageCd)){// 대차정지위치
							logger.println(LogLevel.DEBUG,this, "=작업지시 TO LOC CREATE =>B열연 대차정지위치 TO위치 재검색 예외 케이스.");
				    		return false;
						} 			 		
					}
 				}
			/**
 			 *	3.	스케쥴 생성시 TO위치 초기화.
 			 */	
	 			{
	 				int iReq = 0;
	 				
	 				String sTmpState   = "";
	 				String sTmpStockId = "";
	 				
	 				String sToStackColGp	= sPutLoc.substring(0, 6);
					String sToStackBedGp	= sPutLoc.substring(6, 8);
					String sToStackLayerGp  = sPutLoc.substring(8,10);
					
					JDTORecord layerJr	= dao.getStackLayerInfoWithPk(sToStackColGp,
																	  sToStackBedGp,
																	  sToStackLayerGp);
					
					if(layerJr != null){
			    		sTmpState   = StringHelper.evl(layerJr.getFieldString("STACK_LAYER_STAT"), "");
			    		sTmpStockId = StringHelper.evl(layerJr.getFieldString("STOCK_ID"), "");
		 			}
		 			
		 			if(YmCommonConst.STACK_LAYER_STAT_P.equals(sTmpState)&&
		 			   sStockId.equals(sTmpStockId)){
		 			   	
		 			   	/* 
						 * 적치단 PUT위치 Clear
						 * tb_ym_stacklayer Table : stock_id = ''(Empty)
						 * tb_ym_stacklayer Table : stack_layer_stat  = 'E'(적치가능)
						 */	
				    	iReq = dao.updateCraneStackLayerStat(sToStackColGp,
				    										 sToStackBedGp,
				    										 sToStackLayerGp,
				    										 "",
				    										 YmCommonConst.STACK_LAYER_STAT_E);
				        
				    	if(YmCommonConst.STACK_LAYER_GP_01.equals(sToStackLayerGp)){
					    	
					    	/*
					    	 * A.B열연 Coil 권상실적	
					    	 * 상단 왼쪽 상태정보를 UPDATE
					    	 * 상단 오른쪽 상태정보를 UPDATE
					    	 */	
					    	iReq = YmCommonDB.setCoilUpperState_V(sToStackColGp,
						    							 	   	  sToStackBedGp,
						    							 	   	  sToStackLayerGp);
			    		}		
			    		
		 			}
	 			}	  	   	
			/**
 			 *	4.	TO위치 결정을 위한 PARAM 값을 셋팅한다.
 			 */	
				JDTORecord FmLocV = JDTORecordFactory.getInstance().create();
				{	
					FmLocV.setField("STOCK_ID"						,sStockId);
					FmLocV.setField("GBN"							,sMainWorkYn);
					FmLocV.setField("YD_GP"							,sYdGp);
					FmLocV.setField("BAY_GP"						,sBayGp);
					FmLocV.setField("SECT_GP"						,sUpLoc.substring(2,4));
					FmLocV.setField("COL_GP"						,sUpLoc.substring(4,6));
					FmLocV.setField("STACK_COL_USAGE_CD"			,sUsageCd); 
					FmLocV.setField("SCH_WORK_KIND"					,sSchCode); 
					FmLocV.setField("STOCK_MOVE_TERM"				,sStockMoveTm); 
					FmLocV.setField("SCH_WORK_LOC_DECISION_METHOD"	,sWbookDecMth); 
					FmLocV.setField("CRANE_WORD_PUT_LOC"			,sWbookPutLoc); 
				}			
				
			/**
 			 *	5.	TO 위치 결정 EJB CALL
 			 */
				JDTORecord ToLocV    = null;
		 		{
		 			EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
					ToLocV    = (JDTORecord)ejbConn.trx("getCoilToLocInfo_001",
														 new  Class[]{JDTORecord.class,
														 			  String.class},
														 new Object[]{FmLocV,"R"});
	 				if(ToLocV == null){
			    		logger.println(LogLevel.DEBUG,this, "=작업지시 TO LOC CREATE =>TO 위치 생성 실패.");
			    		
			    		/*
			    		 *	실패시 초기 TO위치 복원문제?
			    		 */
			    		 
			    		return false;
			    	}		
	 			}
 			/**
 			 *	6.	TO 위치 반영
 			 */	 
	 			{
	 			 	int iSeq = 0;
	 			 	
	 			 	String sToStackColGp	= ToLocV.getFieldString("TO_STACK_COL_GP");	// 적치열구분
					String sToStackBedGp	= ToLocV.getFieldString("TO_STACK_BED_GP");	// 적치BED구분
					String sToStackLayerGp  = ToLocV.getFieldString("TO_STACK_LAYER_GP");	// 적치단구분
					
					/* 
					 * 적치단 PUT위치 셋팅
					 * tb_ym_stacklayer Table : stock_id = stock_id(저장품ID)
					 * tb_ym_stacklayer Table : stack_layer_stat	   = 'P'(PUT 스케쥴수행)
					 */	
			    	iSeq = dao.updateCraneStackLayerStat(sToStackColGp,
			    										 sToStackBedGp,
			    										 sToStackLayerGp,
			    										 sStockId,
			    										 YmCommonConst.STACK_LAYER_STAT_P);
			    	/*
			    	 * A.B열연 Coil 스케쥴 TO위치 예약 결정시
			    	 * 상단 좌,우 상태정보를 UPDATE
			    	 */	
			    	{
			    		if(YmCommonConst.STACK_LAYER_GP_01.equals(sToStackLayerGp)){
				    		
				    		/*
					    	 * A.B열연 Coil 스케쥴 TO위치 예약 결정시
					    	 * 상단 왼쪽 상태정보를 UPDATE
					    	 * 상단 오른쪽 상태정보를 UPDATE
					    	 */	
			    			iSeq = YmCommonDB.setCoilUpperState_E(sToStackColGp,
						    							 	   	  sToStackBedGp,
						    							 	   	  sToStackLayerGp);
			    		}
			    	}
			    	
			    	/* 
					 *	스케쥴 PUT LOC 정보 수정
					 */	
			    	iSeq = dao.updatePutLocInfoWithSchId(sSchId,
	    								     			 sToStackColGp+
						    							 sToStackBedGp+
						    							 sToStackLayerGp);
				}
			logger.println(LogLevel.DEBUG,this, "=작업지시 TO LOC CREATE 종료=.");
			isSuccess = true;			
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess;
	}
	
	/**
     * =SLAB=
     * 작업지시 시점에 하단정보를 체크해서 'S','U',Empty 이면 
     * 스케쥴 취소 후 다시 스케쥴 Call
     * 
     * @param String : 스케쥴ID
     * 
     * @return
     * @throws  
     */	
    private boolean setSlabCraneWorkOrderInfo(String sSchId){
		
		boolean isSuccess = true;
						   
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			logger.println(LogLevel.DEBUG,this, "=작업지시 RECHECK 시작=.");
			/*
			 *	1.	스케쥴 정보 검색	
			 */
				JDTORecord schInfo  = dao.getCraneSchInfo(sSchId);
			    logger.println(LogLevel.DEBUG,this, "sSchId=" + sSchId);
		    	
		    	String sPutLoc 		= StringHelper.evl(schInfo.getFieldString("CRANE_WORD_PUT_LOC"),"");
		    	String sYdGp 		= StringHelper.evl(schInfo.getFieldString("YD_GP"),"");
				String sBayGp 		= StringHelper.evl(schInfo.getFieldString("BAY_GP"),"");
				String sEquipNo 	= StringHelper.evl(schInfo.getFieldString("SCH_WORK_EQUIP_NO"),"");
				String sSchCode 	= StringHelper.evl(schInfo.getFieldString("SCH_WORK_KIND"),"");
				String sSchStat 	= StringHelper.evl(schInfo.getFieldString("SCH_WORK_STAT"),"");
				
				if(!YmCommonConst.SCH_WORK_STAT_1.equals(sSchStat)){
			    
			       	logger.println(LogLevel.DEBUG,this, "=SLAB RECHECK => UP지시 상태에서만 가능.="+sSchStat);
		    		return isSuccess;
			    }
			/*
			 *	2.	하단 적치단 정보 검색
			 */    
			    boolean isWork  = false;
			    
			    String sDnColGp = sPutLoc.substring(0, 6);
			    String sDnBedGp = sPutLoc.substring(6, 8);
			    String sDnLyrGp = YmCommonUtil.changeLayerFormat(sPutLoc.substring(8,10), "M");
			    
			    JDTORecord colJr = dao.getStackLayerInfoWithPk(sDnColGp,
															   sDnBedGp,
															   sDnLyrGp);
			
				String tLayerStat 	= "";
				
				if(colJr != null){
					
					 tLayerStat = StringHelper.evl(colJr.getFieldString("STACK_LAYER_STAT"), "");
				}										 
				
				//if("".equals(tLayerStat)||	//하단이 존재하지 않을 경우
				if(YmCommonConst.STACK_LAYER_STAT_S.equals(tLayerStat)||
				   YmCommonConst.STACK_LAYER_STAT_U.equals(tLayerStat)||
				   YmCommonConst.STACK_LAYER_STAT_E.equals(tLayerStat)){
				   	
				   	isWork = true;
				   	logger.println(LogLevel.DEBUG,this, "=SLAB RECHECK => 하단정보가 명확하지 않음.="+tLayerStat+"=");
			   	}else{
			   		logger.println(LogLevel.DEBUG,this, "=SLAB RECHECK => 하단정보가 명확함.="+tLayerStat+"=");
				}
			/*
			 *	3.	현저장품을 포함한 상단의 스케쥴을 취소 후
			 *		다시 스케쥴 CALL
			 */    	
				
				if(isWork){
					
					List loadSchs	= dao.getUpperLayerStockList(sDnColGp,
															     sDnBedGp,
															     sDnLyrGp);
					
					logger.println(LogLevel.DEBUG,this, "=SLAB RECHECK =>loadSchs="+loadSchs.size());
																		   
					String 		sTmpSchId 	= "";
					String 		sTmpWbkId 	= "";
					String 		bTmpWbkId 	= "";
					JDTORecord 	schJr 	= null;
					
					EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
					//스케쥴 취소
					{
						for(int index = loadSchs.size() -1; index >= 0 ; index--)
						{
							schJr  		= (JDTORecord)loadSchs.get(index);
							sTmpSchId 	= StringHelper.evl(schJr.getFieldString("SCH_ID"),"");
							
							Boolean isTemp  = (Boolean)ejbConn.trx("cancelSlabSchInfo",
														new  Class[]{String.class},
														new Object[]{sTmpSchId});
						}		
					}
					//스케쥴 RECALL
					{
						for(int index = 0; index < loadSchs.size(); index++)
						{
							schJr  		= (JDTORecord)loadSchs.get(index);
							sTmpWbkId 	= StringHelper.evl(schJr.getFieldString("WBOOK_ID"),"");
							bTmpWbkId  += sTmpWbkId + "-";		
						}
						
						Boolean isTrue = (Boolean)ejbConn.trx("syCraneScheduleInfoInsert",
															  new  Class[]{String.class},
															  new Object[]{bTmpWbkId});			
					}									  
					
					/**
					 *	더이상 작업지시 모듈을 처리하지 못하도록 한다.
					 */
					isSuccess = false;
				} 
			logger.println(LogLevel.DEBUG,this, "=작업지시 RECHECK 종료=.");
				
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess;
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
	public boolean setSlabAutoWorkInfo(JDTORecord jrInfo){
		
		boolean isSuccess = false;
		
		String sEquipGp	= "";
		String sMessage 	= "";
		
		String sYardId 		= "";	
		String sBayGp 	= "";
		String sEquipKind 	= "";
		String sEquipNo 	= "";
		String sWorkCnt	= "";
		String sXaxis		= "";
		String sYaxis        	= "";
		
		String sStackColGp        = "";
		String sStackBedGp	= "";
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			sYardId    	= StringHelper.evl(jrInfo.getFieldString("야드구분"), "");
			sBayGp     	= StringHelper.evl(jrInfo.getFieldString("동구분"), "");
			sEquipKind 	= StringHelper.evl(jrInfo.getFieldString("설비종류"), "");
			sEquipNo   	= StringHelper.evl(jrInfo.getFieldString("설비번호"), "");
			sWorkCnt	   	= StringHelper.evl(jrInfo.getFieldString("작업요구매수"), "0");
			sXaxis   		= StringHelper.evl(jrInfo.getFieldString("권상X위치"), "");
			sYaxis   		= StringHelper.evl(jrInfo.getFieldString("권상Y위치"), "");
			
			sEquipGp		= sYardId+sBayGp+sEquipKind+sEquipNo;
			
			logger.println(LogLevel.DEBUG,this, "sYardId="		+ sYardId);
			logger.println(LogLevel.DEBUG,this, "sBayGp="		+ sBayGp);
			logger.println(LogLevel.DEBUG,this, "sEquipKind="	+ sEquipKind);
			logger.println(LogLevel.DEBUG,this, "sEquipNo="		+ sEquipNo);
			logger.println(LogLevel.DEBUG,this, "sWorkCnt="	    	+ sWorkCnt);
			logger.println(LogLevel.DEBUG,this, "sXaxis="		+ sXaxis);
			logger.println(LogLevel.DEBUG,this, "sYaxis="		+ sYaxis);
			
			/*
			 *	1. 위치정보 가져오기
			 */
			List MapInfo	= dao.getXYLogicalInfo(sYardId+sBayGp,
										      sXaxis,
										      sYaxis);
			
			logger.println(LogLevel.DEBUG,this, "=SLAB AUTO =>MapInfo Size="+MapInfo.size());
			
			if(MapInfo.size() == 0 ){
				
				sMessage	= "논리적위치정보가 존재하지 않습니다.";
				sendMessageToSlabCrane(sEquipGp,sMessage);
				
				logger.println(LogLevel.DEBUG,this, "=SLAB AUTO => 위치정보 체크 오류");
				return false;
			}   	
			if(MapInfo.size() > 1){
				
				sMessage	= "논리적위치정보가 2개 이상 존재합니다.";
				sendMessageToSlabCrane(sEquipGp,sMessage);
				
				logger.println(LogLevel.DEBUG,this, "=SLAB AUTO => 위치정보 체크 오류");
				return false;
			}   	
			
			for(int inx = 0; inx < MapInfo.size() ; inx++)
			{
			 	JDTORecord infoV = (JDTORecord)MapInfo.get(inx);
			 	
			 	sStackColGp 	= StringHelper.evl(infoV.getFieldString("STACK_COL_GP"), "");	 														 
			 	sStackBedGp 	= StringHelper.evl(infoV.getFieldString("STACK_BED_GP"), "");	 
			 	
			 	logger.println(LogLevel.DEBUG,this, "=SLAB AUTO =>sStackColGp	="+sStackColGp);
			 	logger.println(LogLevel.DEBUG,this, "=SLAB AUTO =>sStackBedGp	="+sStackBedGp);	
			 }
			
			/*
			 *	2. SLAB 정보 가져오기
			 */
			List infoL = dao.getStackLayerInfoWithBed(sStackColGp,
											    sStackBedGp,
											    "L");
			
			if(infoL.size() == 0 ){
				
				sMessage	= "저장품정보를 가져올 수 없습니다..";
				sendMessageToSlabCrane(sEquipGp,sMessage);
				
				logger.println(LogLevel.DEBUG,this, "=SLAB AUTO =>저장품정보  체크 오류");
				return false;
			}   	
			/*
			 *	3. 크레인 정보(작업매수) 가져오기
			 */
			{
				JDTORecord equipJr = dao.getEquipInfoWithEquipGp(sEquipGp);
			    		
		    		if(equipJr == null){
		    			
		    			sMessage	= "크레인 정보가 존재하지 않습니다.";
					sendMessageToSlabCrane(sEquipGp,sMessage);
				
			    		logger.println(LogLevel.DEBUG,this, "=SLAB AUTO => 크레인 정보가 존재안함.");
			    		return false;
			    	}
				
				String sWprogStat 	= StringHelper.evl(equipJr.getFieldString("WPROG_STAT"),"");    	
				String sMaxQnty 	= StringHelper.evl(equipJr.getFieldString("STACK_MAX_QNTY"),"0");
				
				int iTotalCnt		= 0;
				int iMaxQnty 		= Integer.parseInt(sMaxQnty);	// 크레인 설정 매수
				int iWorkCnt 		= Integer.parseInt(sWorkCnt); // 크레인 요구 매수
				int iSizeCnt 		= infoL.size();				// 저장품 정보 갯수
				
				logger.println(LogLevel.DEBUG,this, "=SLAB AUTO ERROR=> sWprogStat"	+sWprogStat);
				logger.println(LogLevel.DEBUG,this, "=SLAB AUTO ERROR=> iMaxQnty"	+iMaxQnty);
				logger.println(LogLevel.DEBUG,this, "=SLAB AUTO ERROR=> iWorkCnt"	+iWorkCnt);
				logger.println(LogLevel.DEBUG,this, "=SLAB AUTO ERROR=> iSizeCnt"	+iSizeCnt);
				
				if(YmCommonConst.WORK_PROG_STAT_3.equals(sWprogStat)){
					
					sMessage	= "현재 크레인이 PUT지시 상태입니다.";
					sendMessageToSlabCrane(sEquipGp,sMessage);
				
			 		logger.println(LogLevel.DEBUG,this, "=SLAB AUTO ERROR=> PUT 지시 상태에서는 자동이적을 편성할 수 없습니다..");
					return false;
			 	}
			 	
				if(iMaxQnty < iWorkCnt){
					
					sMessage	= "크레인 설정매수보다 요구매수가 많습니다.";
					sendMessageToSlabCrane(sEquipGp,sMessage);
				
					logger.println(LogLevel.DEBUG,this, "=SLAB AUTO ERROR=> 크레인 설정매수 보다 요구매수가 큼.");
			    		return false;
				}
				
				if(iWorkCnt > iSizeCnt){
					
					sMessage	= "저장품정보 갯수보다 요구매수가 많습니다.";
					sendMessageToSlabCrane(sEquipGp,sMessage);
				
					logger.println(LogLevel.DEBUG,this, "=SLAB AUTO ERROR=> 저장품정보 갯수 보다 요구매수가 큼.");
			    		return false;
				}
			}
			/*
			 *	4. 작업예약 생성하기,스케쥴 호출하기
			 */ 
			String Glo_Sch_Call		= ""; 
			String sSchCode 		= "";
			if(infoL != null)
			{	
				JDTORecord infoV 	   = null;
				
			 	String sWbookId		= "";
			 	String sStackLayerStat	= "";
			 	String sStockId		= "";
			 	
				for(int inx = 0; inx < Integer.parseInt(sWorkCnt) ; inx++)
				{
				 	infoV = (JDTORecord)infoL.get(inx);
				 	
				 	sStackLayerStat 	= StringHelper.evl(infoV.getFieldString("STACK_LAYER_STAT"), "");	 														 
				 	sStockId 			= StringHelper.evl(infoV.getFieldString("STOCK_ID"), "");	 														 
				 	
				 	if(YmCommonConst.STACK_LAYER_STAT_U.equals(sStackLayerStat)){
				 		
				 		sMessage	= "Slab정보가 권상대기(U) 상태입니다..";
						sendMessageToSlabCrane(sEquipGp,sMessage);
					
				 		logger.println(LogLevel.DEBUG,this, "=SLAB AUTO => 상단 Slab정보 U");
				 		return false;
				 	}else if(YmCommonConst.STACK_LAYER_STAT_P.equals(sStackLayerStat)){
				 		
				 		sMessage	= "Slab정보가 권하대기(P) 상태입니다..";
						sendMessageToSlabCrane(sEquipGp,sMessage);
				 		logger.println(LogLevel.DEBUG,this, "=SLAB AUTO => 상단 Slab정보 P");
						return false;
				 	}else if(YmCommonConst.STACK_LAYER_STAT_S.equals(sStackLayerStat)){
				 		
				 		sMessage	= "Slab정보가 작업예약등록(S) 상태입니다..";
						sendMessageToSlabCrane(sEquipGp,sMessage);
				 		logger.println(LogLevel.DEBUG,this, "=SLAB AUTO => 상단 Slab정보 S");
				 		return false;
				 	}
				 }
				 
				 /*
				  * 크레인에 할당된 동내이적 스케쥴코드 가져오기
				  */
				 JDTORecord schJr = dao.getBayCrnSchInfo(sYardId,
				 								       sBayGp,
				 								       sEquipNo,"SYM");
			    		
		    		if(schJr == null){
		    			
		    			sMessage	= "해당 크레인에 동내이적코드가 없슴.";
					sendMessageToSlabCrane(sEquipGp,sMessage);
				
			    		logger.println(LogLevel.DEBUG,this, "=SLAB AUTO => 해당 크레인에 동내이적코드가 없슴.");
			    		return false;
			    	}
				
				sSchCode = StringHelper.evl(schJr.getFieldString("SCH_WORK_KIND"),"");    	
				
				 
				 for(int inx = 0; inx < Integer.parseInt(sWorkCnt) ; inx++)
				{
				 	infoV = (JDTORecord)infoL.get(inx);
				 	
				 	sStackLayerStat 	= StringHelper.evl(infoV.getFieldString("STACK_LAYER_STAT"), "");	 														 
				 	sStockId 			= StringHelper.evl(infoV.getFieldString("STOCK_ID"), "");	 														 
				 	
				 	sWbookId = ymCommonDAO.createWBook(sYardId+sBayGp, 
							    						    sSchCode, 
							    						    "O", 
							    						    "0000000000");
				 	
					int iSeq	= dao.requestupdateData("ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId", 
												  new Object[]{sWbookId, 
												  			 YmCommonUtil.getSlabCurrProgCd(sStockId,"")[1],
												  			 sStockId });			
					
					int iSeq1      = dao.requestupdateData("ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark", 
												   new Object[]{YmCommonConst.STACK_LAYER_STAT_S, 
												   		   	   sStockId });
																	   				
					Glo_Sch_Call += sWbookId +"-" ;											  			   
				 }
				 
				 if (!Glo_Sch_Call.trim().equals("")){
					//	Slab Schedule EJB Call 
					EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
					Boolean isTrue = (Boolean)ejbConn.trx("syCraneScheduleInfoInsert",new  Class[]{String.class},
																					  new Object[]{Glo_Sch_Call});
				}else{
					sMessage	= "작업대상 정보가 존재하지 않습니다.";
					sendMessageToSlabCrane(sEquipGp,sMessage);
						
					logger.println(LogLevel.DEBUG,this, "=SLAB AUTO => 스케쥴정보가 없습니다.");
				 	return false;
				}																  
			}	
			
			/*
			 *	5.1 긴급작업으로 편성하기
			 */ 
			{
				int iReq = 0;
					 
				JDTORecord equipJr = dao.getEquipInfoWithEquipGp(sEquipGp);
		    		
		    		if(equipJr == null){
			    		logger.println(LogLevel.DEBUG,this, "=SLAB AUTO => 크레인 정보가 존재안함.");
			    		return false;
			    	}
				
				String sWprogStat 	= StringHelper.evl(equipJr.getFieldString("WPROG_STAT"),"");    	
				String sSchId	   	= StringHelper.evl(equipJr.getFieldString("WBOOK_ID"),"");
				
				logger.println(LogLevel.DEBUG,this, "=SLAB AUTO ERROR=> sWprogStat"	+sWprogStat);
				logger.println(LogLevel.DEBUG,this, "=SLAB AUTO ERROR=> sSchId"		+sSchId);
				
				if(YmCommonConst.WORK_PROG_STAT_1.equals(sWprogStat)){
					
					iReq = dao.updateSubCraneEquipStat(sYardId,
		    										     sBayGp,
		    										     sEquipKind,
		    										     sEquipNo,
		    										     YmCommonConst.WORK_PROG_STAT_W,
		    										     "");
					logger.println(LogLevel.DEBUG,this,"=SLAB AUTO =>크레인 설비상태 idle 셋팅="+iReq); 		
					
					iReq = dao.updateCraneSchStat(sSchId,
			   							           YmCommonConst.SCH_WORK_STAT_S);
					logger.println(LogLevel.DEBUG,this,"=SLAB AUTO =>스케쥴 정보 초기화="+iReq);	
				}
				
				/*
				 *	3.	크레인 : 스케쥴 코드에 대해 긴급작업을 편성한다.
				 *		- 기존에 긴급작업 편성된 정보를 원복한다.
				 *		- 새로 긴급작업을 편성한다.
				 */
				 
				iReq = dao.updateCraneSchClaer(sYardId,
	    									   sBayGp,
	    									   sEquipNo);
				logger.println(LogLevel.DEBUG,this,"=SLAB AUTO =>크레인 긴급작업 정보 초기화="+iReq); 
				
				String[] arrayWbookId = Glo_Sch_Call.split("-");
				for (int iny = 0; iny < arrayWbookId.length; iny++) {
					
					iReq = dao.updateEmergencySchInfo_01(arrayWbookId[iny]);
					logger.println(LogLevel.DEBUG,this,"=SLAB AUTO =>크레인 긴급작업 정보 편성="+arrayWbookId[iny]); 
				}
				
				/*
				 *	4.	크레인 작업요구 호출
				 */ 
				if(YmCommonConst.WORK_PROG_STAT_1.equals(sWprogStat)){
					
					String sTc = "";
					
					if(	YmCommonConst.YD_GP_0.equals(sYardId) && 
					   	YmCommonConst.BAY_GP_A.equals(sBayGp)){
						
						sTc = YmCommonConst.TC_HM1PB02;
					}else if(	YmCommonConst.YD_GP_0.equals(sYardId) && 
							YmCommonConst.BAY_GP_B.equals(sBayGp)){
						
						sTc = YmCommonConst.TC_HM1PB52;
					}else if(	YmCommonConst.YD_GP_2.equals(sYardId)){
						
						sTc = YmCommonConst.TC_CM1PB02;
					}
					
					boolean isYahoo =  callCraneSchInfo(	sTc,
												     	sYardId, 
												     	sBayGp,
													YmCommonConst.EQUIP_KIND_CR,
													sEquipNo,
													sSchCode,
													"");
					logger.println(LogLevel.DEBUG,this,"=SLAB AUTO =>크레인 작업요구 호출="+isYahoo);													    
				}
			}							   				
			return isSuccess;
		
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
	public boolean setBCoilAutoWorkInfo(JDTORecord jrInfo){
		
		boolean isSuccess = false;
		
		String sEquipGp			= "";
		String sMessage 		= "";
		
		String sYardId 			= "";	
		String sBayGp 			= "";
		String sEquipKind 		= "";
		String sEquipNo 		= "";
		String sWorkCnt			= "";
		String sXaxis			= "";
		String sYaxis        	= "";
		
		String sStockId			= "";
		String sStackColGp  	= "";
		String sStackBedGp		= "";
		String sStackLayerGp	= "";
		String sStackLayerStat	= "";
		String sWbookId			= "";
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			sYardId    	= StringHelper.evl(jrInfo.getFieldString("야드구분"), "");
			sBayGp     	= StringHelper.evl(jrInfo.getFieldString("동구분"), "");
			sEquipKind 	= StringHelper.evl(jrInfo.getFieldString("설비종류"), "");
			sEquipNo   	= StringHelper.evl(jrInfo.getFieldString("설비번호"), "");
			sWorkCnt	= StringHelper.evl(jrInfo.getFieldString("작업요구매수"), "0");
			sXaxis   	= StringHelper.evl(jrInfo.getFieldString("권상X위치"), "");
			sYaxis   	= StringHelper.evl(jrInfo.getFieldString("권상Y위치"), "");
			
			sEquipGp		= sYardId+sBayGp+sEquipKind+sEquipNo;
			
			logger.println(LogLevel.DEBUG,this, "sYardId="		+ sYardId);
			logger.println(LogLevel.DEBUG,this, "sBayGp="		+ sBayGp);
			logger.println(LogLevel.DEBUG,this, "sEquipKind="	+ sEquipKind);
			logger.println(LogLevel.DEBUG,this, "sEquipNo="		+ sEquipNo);
			logger.println(LogLevel.DEBUG,this, "sWorkCnt="	    + sWorkCnt);
			logger.println(LogLevel.DEBUG,this, "sXaxis="		+ sXaxis);
			logger.println(LogLevel.DEBUG,this, "sYaxis="		+ sYaxis);
			
			/*
			 *	1. 위치정보 가져오기
			 */
			List MapInfo	= dao.getBCoilXYLogicalInfo(sYardId+sBayGp,
										      sXaxis,
										      sYaxis);
			
			logger.println(LogLevel.DEBUG,this, "=Coil AUTO =>MapInfo Size="+MapInfo.size());
			
			if(MapInfo.size() == 0 ){
				
				sMessage	= "논리적위치정보가 존재하지 않습니다.";
				sendMessageToBCoilCrane(sEquipGp,sMessage);
				
				logger.println(LogLevel.DEBUG,this, "=Coil AUTO => 정보 존재 않함");
				return false;
			}   	
			if(MapInfo.size() > 1){
				
				sMessage	= "논리적위치정보가 2개 이상 존재합니다.";
				sendMessageToBCoilCrane(sEquipGp,sMessage);
				
				logger.println(LogLevel.DEBUG,this, "=Coil AUTO => 정보 2개 존재");
				return false;
			}   	
			
			for(int inx = 0; inx < MapInfo.size() ; inx++)
			{
			 	JDTORecord infoV = (JDTORecord)MapInfo.get(inx);
			 	
			 	sStockId		= StringHelper.evl(infoV.getFieldString("STOCK_ID"), "");	
			 	sStackColGp 	= StringHelper.evl(infoV.getFieldString("STACK_COL_GP"), "");	 														 
			 	sStackBedGp 	= StringHelper.evl(infoV.getFieldString("STACK_BED_GP"), "");	 
			 	sStackLayerGp   = StringHelper.evl(infoV.getFieldString("STACK_LAYER_GP"), "");
			 	sStackLayerStat = StringHelper.evl(infoV.getFieldString("STACK_LAYER_STAT"), "");
			 	
			 	logger.println(LogLevel.DEBUG,this, "=Coil AUTO =>sStockId			="+sStockId);
			 	logger.println(LogLevel.DEBUG,this, "=Coil AUTO =>sStackColGp		="+sStackColGp);
			 	logger.println(LogLevel.DEBUG,this, "=Coil AUTO =>sStackBedGp		="+sStackBedGp);
			 	logger.println(LogLevel.DEBUG,this, "=Coil AUTO =>sStackLayerGp		="+sStackLayerGp);
			 	logger.println(LogLevel.DEBUG,this, "=Coil AUTO =>sStackLayerStat	="+sStackLayerStat);
			 }
			
			/*
			 *	2. SLAB 정보 가져오기
			
			List infoL = dao.getStackLayerInfoWithBed(sStackColGp,
											    sStackBedGp,
											    "L");
			
			if(infoL.size() == 0 ){
				
				sMessage	= "저장품정보를 가져올 수 없습니다..";
				sendMessageToSlabCrane(sEquipGp,sMessage);
				
				logger.println(LogLevel.DEBUG,this, "=SLAB AUTO =>저장품정보  체크 오류");
				return false;
			}  */
			
			/*
			 *	3. 크레인 정보 가져오기
			 */
			{
				JDTORecord equipJr = dao.getEquipInfoWithEquipGp(sEquipGp);
			    		
		    		if(equipJr == null){
		    			
		    			sMessage	= "크레인 정보가 존재하지 않습니다.";
		    			sendMessageToBCoilCrane(sEquipGp,sMessage);
				
			    		logger.println(LogLevel.DEBUG,this, "=Coil AUTO => 크레인 정보가 존재안함.");
			    		return false;
			    	}
				
				String sWprogStat 	= StringHelper.evl(equipJr.getFieldString("WPROG_STAT"),"");    	
				
				
				logger.println(LogLevel.DEBUG,this, "=Coil AUTO ERROR=> sWprogStat"	+sWprogStat);
				
				
				if(YmCommonConst.WORK_PROG_STAT_3.equals(sWprogStat)){
					
					sMessage	= "현재 크레인이 PUT지시 상태입니다.";
					sendMessageToBCoilCrane(sEquipGp,sMessage);
				
			 		logger.println(LogLevel.DEBUG,this, "=Coil AUTO ERROR=> PUT 지시 상태에서는 자동이적을 편성할 수 없습니다..");
					return false;
			 	}
			 	
				
			}
			/*
			 *	4. 작업예약 생성하기,스케쥴 호출하기
			 */ 
			String sSchCode 		= "";
			if(MapInfo.size() == 1 )
			{	
			 	
				if(YmCommonConst.STACK_LAYER_STAT_U.equals(sStackLayerStat)){
				 		
					sMessage	= "Coil정보가 권상대기(U) 상태입니다..";
					sendMessageToBCoilCrane(sEquipGp,sMessage);
					logger.println(LogLevel.DEBUG,this, "=Coil AUTO => Coil정보 U");
				 	return false;

				 }else if(YmCommonConst.STACK_LAYER_STAT_P.equals(sStackLayerStat)){
				 		
				 	sMessage	= "Coil정보가 권하대기(P) 상태입니다..";
				 	sendMessageToBCoilCrane(sEquipGp,sMessage);
				 	logger.println(LogLevel.DEBUG,this, "=Coil AUTO => Coil정보 P");
					return false;
					
				 }else if(YmCommonConst.STACK_LAYER_STAT_S.equals(sStackLayerStat)){
				 		
				 	sMessage	= "Coil정보가 작업예약등록(S) 상태입니다..";
				 	sendMessageToBCoilCrane(sEquipGp,sMessage);
				 	logger.println(LogLevel.DEBUG,this, "=Coil AUTO => Coil정보 S");
				 	return false;
				 }
				
				 
				 /*
				  * 크레인에 할당된 동내이적 스케쥴코드 가져오기
				  */
				 JDTORecord schJr = dao.getBayCrnSchInfo(sYardId,
				 								       sBayGp,
				 								       sEquipNo,"CYA");
			    		
		    		if(schJr == null){
		    			
		    			sMessage	= "해당 크레인에 동내이적코드가 없슴.";
		    			sendMessageToBCoilCrane(sEquipGp,sMessage);
				
			    		logger.println(LogLevel.DEBUG,this, "=Coil AUTO => 해당 크레인에 동내이적코드가 없슴.");
			    		return false;
			    	}
				
				sSchCode = StringHelper.evl(schJr.getFieldString("SCH_WORK_KIND"),"");    	
				
				sWbookId = ymCommonDAO.createWBook(sYardId+sBayGp, 
							    						    sSchCode, 
							    						    "O", 
							    						    "0000000000");
				
				/*
				 * 2007.08.03 이정훈
				 * 동내 이적 Crane 할당 막음
				 
				sSchCode = "CYM1";
				sWbookId = ymCommonDAO.createWBook(sYardId+sBayGp, 
						sSchCode, 
					    "O", 
					    "0000000000");
				*/
				int iSeq	= dao.requestupdateData("ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId", 
												  new Object[]{sWbookId, 
												  			 YmCommonUtil.getSlabCurrProgCd(sStockId,"")[1],
												  			 sStockId });			
					
				int iSeq1      = dao.requestupdateData("ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark", 
												   new Object[]{YmCommonConst.STACK_LAYER_STAT_S, 
												   		   	   sStockId });
																	   				
				
				 
				 if (!sWbookId.trim().equals("")){
					// Coil Schedule EJB Call 
					EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
					Boolean isTrue = (Boolean)ejbConn.trx("callCraneSchInfo",new  Class[]{String.class},
																			 new Object[]{sWbookId});
				}else{
					sMessage	= "작업대상 정보가 존재하지 않습니다.";
					sendMessageToBCoilCrane(sEquipGp,sMessage);
						
					logger.println(LogLevel.DEBUG,this, "=Coil AUTO => 스케쥴정보가 없습니다.");
				 	return false;
				}																  
			}	
			
			/*
			 *	5.1 긴급작업으로 편성하기
			 */ 
			{
				int iReq = 0;
					 
				JDTORecord equipJr = dao.getEquipInfoWithEquipGp(sEquipGp);
		    		
		    		if(equipJr == null){
			    		logger.println(LogLevel.DEBUG,this, "=Coil AUTO => 크레인 정보가 존재안함.");
			    		return false;
			    	}
				
				String sWprogStat 	= StringHelper.evl(equipJr.getFieldString("WPROG_STAT"),"");    	
				String sSchId	   	= StringHelper.evl(equipJr.getFieldString("WBOOK_ID"),"");
				
				logger.println(LogLevel.DEBUG,this, "=Coil AUTO ERROR=> sWprogStat"	+sWprogStat);
				logger.println(LogLevel.DEBUG,this, "=Coil AUTO ERROR=> sSchId"		+sSchId);
				
				if(YmCommonConst.WORK_PROG_STAT_1.equals(sWprogStat)){
					
					iReq = dao.updateSubCraneEquipStat(sYardId,
		    										     sBayGp,
		    										     sEquipKind,
		    										     sEquipNo,
		    										     YmCommonConst.WORK_PROG_STAT_W,
		    										     "");
					logger.println(LogLevel.DEBUG,this,"=Coil AUTO =>크레인 설비상태 idle 셋팅="+iReq); 		
					
					iReq = dao.updateCraneSchStat(sSchId,
			   							           YmCommonConst.SCH_WORK_STAT_S);
					logger.println(LogLevel.DEBUG,this,"=Coil AUTO =>스케쥴 정보 초기화="+iReq);	
				}
				
				/*
				 *	3.	크레인 : 스케쥴 코드에 대해 긴급작업을 편성한다.
				 *		- 기존에 긴급작업 편성된 정보를 원복한다.
				 *		- 새로 긴급작업을 편성한다.
				 */
				 
				iReq = dao.updateCraneSchClaer(sYardId,
	    									   sBayGp,
	    									   sEquipNo);
				logger.println(LogLevel.DEBUG,this,"=Coil AUTO =>크레인 긴급작업 정보 초기화="+iReq); 
				
				iReq = dao.updateEmergencySchInfo_01(sWbookId);
				logger.println(LogLevel.DEBUG,this,"=Coil AUTO =>크레인 긴급작업 정보 편성="+sWbookId); 
				
				
				/*
				 *	4.	크레인 작업요구 호출
				 */ 
		
				boolean isYahoo =  callCraneSchInfo(YmCommonConst.TC_CN1BP01,
												    sYardId, 
												    sBayGp,
													YmCommonConst.EQUIP_KIND_CR,
													sEquipNo,
													sSchCode,
													"");
				logger.println(LogLevel.DEBUG,this,"=Coil AUTO =>크레인 작업요구 호출="+isYahoo);													    
				
			}							   				
			return isSuccess;
		
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}
	
	/*
	 * 2007.07.09 이정훈
	 * B열연 Coil Message 전송
	 */
	
	private void sendMessageToBCoilCrane(String sEquipGp,
									 	String sMessage){
		try{							 	
			    	
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
			
	    		Boolean isTemp  = new Boolean(false);
	    		
	    		EJBConnector ejbConn = new EJBConnector("default","JNDICWrkOrdReg",this);
	    		
			isTemp  = (Boolean)ejbConn.trx("callBCoilCraneMsgInfo",new  Class[]{String.class,
																	     String.class},
											                           new Object[]{sEquipGp,
																  	      sMessage});
		}catch(Exception e){}
	}
	
	private void sendMessageToSlabCrane(String sEquipGp,
									 String sMessage){
		try{		
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return ;
			}
			
			    	
	    		Boolean isTemp  = new Boolean(false);
	    		
	    		EJBConnector ejbConn = new EJBConnector("default","JNDICWrkOrdReg",this);
	    		
			isTemp  = (Boolean)ejbConn.trx("callBSlabCraneMsgInfo",new  Class[]{String.class,
																	     String.class},
											                           new Object[]{sEquipGp,
																  	      sMessage});
		}catch(Exception e){}
	}	
	
	///////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	///////////////////////////YJK END/////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	      
	/**
	 * 수신항목을 체크한다.
     * @param parseData
     * @return
     */
    private void validRecDataOfBackUpReq(JDTORecord parseData, Map tc) throws Exception {
    	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        String coilNo 		= getFieldNvl(parseData, "코일번호");
        String fromAddress 	= getFieldNvl(parseData, "FROMADDRESS");
        String toAddress 	= getFieldNvl(parseData, "TOADDRESS");        
        if(coilNo.length() != getFieldLen(tc, "코일번호")) {               
            throw new Exception("수신항목 '코일번호' Error: "+ coilNo);                
        }else if(fromAddress.length() != getFieldLen(tc, "FROMADDRESS")) {               
            throw new Exception("수신항목 'FROM ADDRESS' Error: "+ fromAddress);                
        }else if(toAddress.length() != getFieldLen(tc, "TOADDRESS")) {               
            throw new Exception("수신항목 'TO ADDRESS' Error: "+ toAddress);                
        }
    }
    
    /**
     * JDTORecord 가 가지는 name parameter에 대한 값이 공백이거나 null일 경우 공백을 리턴한다.
     * @param data
     * @param name
     * @param len	공백 수
     * @return
     */
    private String getField(JDTORecord data, String name, int len) {
        if("".equals(StringHelper.evl(data.getFieldString(name), ""))) {
            return space(len);
        }
        return data.getFieldString(name);
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
     * name parameter에 대한 값을 반환한다.
     * @param data	
     * @param name  
     * @return
     */
    private int getFieldLen(Map data, String name) {
        return StringHelper.parseInt((String)data.get(name), 0);
    }

    /**
     * 공백을 cnt 만큼 리턴한다.
     * @param cnt	공백 수
     * @return
     */
    private String space(int cnt) {
        StringBuffer buffer = new StringBuffer();
        for(int i = 0; i < cnt; i++) {
            buffer.append(" ");
        }
        return buffer.toString();
    }
    
    /**
     * 에러 로그를 남긴다.
     * @param errMsg	에러메시지
     * @return
     */
    private boolean returnFalse(String errMsg) {
        logger.println(LogLevel.DEBUG, this, errMsg);
        return false;
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
    public int updateStockMoveTerm(String queryID, List listData) {    	
    	int resNum = 0;
    	try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
    		CraneSchDAO craneschDAO = new CraneSchDAO();    	
    		resNum = craneschDAO.updateStockMoveTerm(queryID, listData);
    		return resNum;
    	}catch(DAOException daoe){
    		throw daoe;
    	}catch(Exception e){
    		throw new EJBServiceException(e);
    	}
    }


}

