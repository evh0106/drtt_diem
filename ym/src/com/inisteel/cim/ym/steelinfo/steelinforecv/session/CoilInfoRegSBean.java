package com.inisteel.cim.ym.steelinfo.steelinforecv.session;


import java.util.List;
import java.util.ArrayList;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.log.LogService;
import jspeed.base.log.LogServiceConfig;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordImplMap;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;
import com.inisteel.cim.common.util.CommonUtil;

import com.inisteel.cim.common.jms.model.CommonModel;
import com.inisteel.cim.common.eai.EAIHttpSender;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.common.jms.model.ym.DMYM005;
import com.inisteel.cim.common.jms.model.ym.DMYM006;
import com.inisteel.cim.common.jms.model.ym.DMYM007;
import com.inisteel.cim.common.jms.model.ym.PMYM005;
import com.inisteel.cim.common.jms.model.ym.POYM001;
import com.inisteel.cim.common.jms.model.ym.POYM002;
import com.inisteel.cim.common.jms.model.po.YMPO161;
import com.inisteel.cim.common.jms.model.dm.YMDM001;
import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.ym.ilkwan.dao.YdMtlStatModHistDao;
import com.inisteel.cim.ym.ilkwan.dao.YdStockDao;
import com.inisteel.cim.ym.bcommon.dao.YmCommDAO;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.bcommon.util.YmCommUtils;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ym.common.YmCommonDB;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO;
import com.inisteel.cim.ym.scheduling.crane.dao.YdLocSearchDAO;
import com.inisteel.cim.ym.steelinfo.steelinforecv.dao.YdSlabMoveBayRankingDAO;
import com.inisteel.cim.ym.steelinfo.steelinforecv.dao.YdStockDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdEquipDAO;
import com.inisteel.cim.ym.workrequest.wrequestaccept.dao.YdWBookDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO;
/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="CoilInfoRegEJB" jndi-name="JNDICoilInfoReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class CoilInfoRegSBean extends BaseSessionBean {
	Logger logger = null; 
	private ymCommonDAO ymCommonDAO = null;
	private String szSessionName = "JNDICoilInfoReg";
	Boolean isSuccess = new Boolean(false);
	
	private YmCommUtils commUtils = new YmCommUtils();
	private YmCommDAO commDao = new YmCommDAO();
	private YmComm ymComm = new YmComm();
	public void ejbCreate() {
		LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		logger = new Logger(config);
	}
	
       /**
	 * 오퍼레이션명 : 해당 Coil 재료번호에 해당하는 상세정보를 가져와 JDTORecord로 데이터를 리턴한다.
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public JDTORecord selectCoilDetInfo(String queryID, String stackId){
		YdStockDAO ydstockDAO = null;	    
	    JDTORecord returnRecord = null;
	    try{
	    	
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
	    	ydstockDAO = new YdStockDAO();
	    	returnRecord = ydstockDAO.getData(queryID,new Object[]{stackId}); 
	    	return returnRecord;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}
	
       /**
	 * 오퍼레이션명 : 해당 Coil 재료번호에 해당하는 변경이력을 가져와 List로 데이터를 리턴한다.
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public List selectCoilLocHis(String queryID, String stackId){
		YdStockDAO ydstockDAO = null;	    
	    List coilHis = null;
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
	    	ydstockDAO = new YdStockDAO();
	    	coilHis = ydstockDAO.getListData(queryID,new Object[]{stackId});
	    	return coilHis;
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
	public List getListaimBay_SPM(String queryID) throws EJBServiceException ,DAOException{	
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			return new YdSlabMoveBayRankingDAO().getListData(queryID);
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
	public int updateACoilmovebayRanking_SPM(String queryID, JDTORecord jrecord) throws EJBServiceException ,DAOException{
		try{
			
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			int count = 0;
			List RULE_ID 	= (List)jrecord.getField("RULE_ID");
			List SCH_CD 		= (List)jrecord.getField("SCH_CD");
			List RANKING1 		= (List)jrecord.getField("RANKING1");
			
			for(int ii =0; ii<RULE_ID.size();ii++){
				

				count+= new YdSlabMoveBayRankingDAO().updateACoilmovebayRanking_SPM(queryID,
																				 ""+RANKING1.get(ii),
																				 ""+RULE_ID.get(ii),
																				 ""+SCH_CD.get(ii)
																				 );

			}
			return count;
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
	public int updateACoilmovebayRanking_HFL(String queryID, JDTORecord jrecord) throws EJBServiceException ,DAOException{
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			int count = 0;
			List RULE_ID1 	= (List)jrecord.getField("RULE_ID1");
			List SCH_CD1 		= (List)jrecord.getField("SCH_CD1");
			List RANKING2 		= (List)jrecord.getField("RANKING2");
			
			for(int ii =0; ii<RULE_ID1.size();ii++){
				

				count+= new YdSlabMoveBayRankingDAO().updateACoilmovebayRanking_HFL(queryID,
																				 ""+RANKING2.get(ii),
																				 ""+RULE_ID1.get(ii),
																				 ""+SCH_CD1.get(ii)
																				 );

			}
			return count;
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
	public List getListaimBay_HFL(String queryID) throws EJBServiceException ,DAOException{	
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			return new YdSlabMoveBayRankingDAO().getListData(queryID);
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}
	
	
       /**
	 * 오퍼레이션명 : 야드의 저장공간별로 해당 야드에 저장된 모든 재료 정보를 가져와 List로 데이터를 리턴한다.
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public List selectProducList(String queryID, List listData) throws EJBServiceException ,DAOException{
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			YdStockDAO ydstockDAO = new YdStockDAO();
			return ydstockDAO.getListData(queryID, listData);
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}
	
	
      /**
	 * 오퍼레이션명 : 
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public int updateProducKeepStock(String queryid, List listData) throws EJBServiceException ,DAOException {   	 
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			YdStockDAO ydstockDAO = new YdStockDAO();
			return ydstockDAO.updateData(queryid, listData);
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}
	
	///////////////////////////////////////////////////////////////////////////
	/////////////////////////////////YJK START/////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
       /**
	 * 오퍼레이션명 : 
	 *  
	 * YJK - INNER INTERFACE
        * 업무 로직
        *	1.TC_CD - POYM002
        *	2.COIL 결번 실적 조업=>야드
        *	3.조업에서 미 보급된 Coil 결번 시점
        *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean receiveInCoilInfo(POYM002 yModel) { 
         
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
        logger.println(LogLevel.DEBUG,this," receiveInCoilInfo(POYM002)");
        
        String sTc 			= StringHelper.evl(yModel.getTcCode(),"");
		String sDate 		= StringHelper.evl(yModel.getTcDate(),"");
		String sTime 		= StringHelper.evl(yModel.getTcTime(),"");
		String sGoodsNo 	= StringHelper.evl(yModel.getCoilNo(),"");
		
		logger.println(LogLevel.DEBUG,this," sTc 		=" + sTc);
		logger.println(LogLevel.DEBUG,this," sDate 		=" + sDate);
		logger.println(LogLevel.DEBUG,this," sTime 		=" + sTime);
		logger.println(LogLevel.DEBUG,this," sGoodsNo 	=" + sGoodsNo);
		logger.println(LogLevel.DEBUG,this,"==COIL 결번 실적 전문수신==");
		
		boolean isVal = false;
		try{
			int iSeq = 0;
			YdStockDAO dao	= new YdStockDAO();
			/**
		     *	1.	코일공통 진도코드 Table 참조.
		     */
		     	String[] sStockInfo = YmCommonUtil.getCoilCurrProgCd(sGoodsNo,"");
			    String sProgCd   	= sStockInfo[0];
				String sStocMv   	= sStockInfo[1];
				
		    	logger.println(LogLevel.DEBUG,this,"저장품 이동조건 = "+ sStocMv );
				
				if(!"".equals(sStocMv)){ 
			    	iSeq = dao.updateStockTransInfo(sGoodsNo,
													sStocMv);	
			    }
		
		isVal = true; 
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }    				
        return isVal;
    }

       /**
	 * 오퍼레이션명 : 
	 *  
	 * YJK - INNER INTERFACE
        * 업무 로직
        *	1.TC_CD - PMYM005 
        *	2.COIL 저장조건변경 공정=>야드
        *	3.Coil 목전, 충당, 여재처리, 보류재 처리 時 공정→야드 (JMS)
        *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean receiveInCoilInfo(PMYM005 yModel) { 
         
        logger.println(LogLevel.DEBUG,this," receiveInCoilInfo(PMYM005)");
        
        
        String sTc 			= StringHelper.evl(yModel.getTcCode(),"");
		String sDate 		= StringHelper.evl(yModel.getTcDate(),"");
		String sTime 		= StringHelper.evl(yModel.getTcTime(),"");
		String sGoodsNo 	= StringHelper.evl(yModel.getCoilNo(),"");
		
		logger.println(LogLevel.DEBUG,this," sTc 		=" + sTc);
		logger.println(LogLevel.DEBUG,this," sDate 		=" + sDate);
		logger.println(LogLevel.DEBUG,this," sTime 		=" + sTime);
		logger.println(LogLevel.DEBUG,this," sGoodsNo 	=" + sGoodsNo);
		logger.println(LogLevel.DEBUG,this,"==COIL 저장조건변경 전문수신==");
		
		boolean isVal = false;
		try{
			
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			int iSeq = 0;
			YdStockDAO dao	= new YdStockDAO();
			/**
		     *	1.	코일공통 진도코드 Table 참조.
		     */
		     	String[] sStockInfo = YmCommonUtil.getCoilCurrProgCd(sGoodsNo,"");
			    String sProgCd   	= sStockInfo[0];
				String sStocMv   	= sStockInfo[1];
				
		    	logger.println(LogLevel.DEBUG,this,"저장품 이동조건 = "+ sStocMv );
				
				if(!"".equals(sStocMv)){ 
			    	iSeq = dao.updateStockTransInfo(sGoodsNo,
													sStocMv);	
			    }
		
		isVal = true; 
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }    		
				
        return isVal;
    }

       /**
	 * 오퍼레이션명 : 
	 *  
	 * YJK - INNER INTERFACE
        * 업무 로직
        *	1.TC_CD - DMYM005
        *	2.COIL 저장조건변경 (목전/충당 지시)	출하=>야드
        *	3.제품 목전, 충당, 여재 처리 시 출하→야드로
        *
        *	처리 : 해당 저장품의 삭제구분을 'Y'로 셋팅
        *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean receiveInCoilInfo(DMYM005 yModel) { 
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
       	logger.println(LogLevel.DEBUG,this," receiveInCoilInfo(DMYM005)");
        
       	String sYdGp 		= StringHelper.evl(yModel.getyardID(),"");
       	String sTc 		= StringHelper.evl(yModel.getTcCode(),"");
		String sDate 		= StringHelper.evl(yModel.getTcDate(),"");
		String sTime 		= StringHelper.evl(yModel.getTcTime(),"");
		String sGoodsEa 	= StringHelper.evl(yModel.getGoodsEa(),"");//제품갯수
		String sGoodsNo 	= StringHelper.evl(yModel.getGoodsNo(),"");//제품번호
		String sKeepSflag 	= StringHelper.evl(yModel.getkeepstockflag(),"");//보관매출유무
		
		logger.println(LogLevel.DEBUG,this," sYdGp 	=" + sYdGp);
		logger.println(LogLevel.DEBUG,this," sTc 		=" + sTc);
		logger.println(LogLevel.DEBUG,this," sDate 		=" + sDate);
		logger.println(LogLevel.DEBUG,this," sTime 		=" + sTime);
		logger.println(LogLevel.DEBUG,this," sGoodsEa 	=" + sGoodsEa);
		logger.println(LogLevel.DEBUG,this," sGoodsNo 	=" + sGoodsNo);
		logger.println(LogLevel.DEBUG,this," sKeepSflag =" + sKeepSflag);
		logger.println(LogLevel.DEBUG,this,"==COIL 저장조건변경(목전/충당 지시) 전문수신==");
		
		boolean isVal = false;
		try{
			int iSeq = 0;
			YdStockDAO   dao 		= new YdStockDAO();
			CraneSchDAO dao1 	= new CraneSchDAO();
			
			if(	YmCommonConst.YD_GP_0.equals(sYdGp)||
			   	YmCommonConst.YD_GP_2.equals(sYdGp)){
				
				/**
				*	슬라브공통 진도코드 Table 참조.
				*/
		     		String[] sStockInfo = YmCommonUtil.getSlabCurrProgCd(sGoodsNo,"");
			    	String sProgCd   	= sStockInfo[0];
				String sStocMv   	= sStockInfo[1];
				
		    		logger.println(LogLevel.DEBUG,this,"저장품 이동조건 = "+ sStocMv );
				
				if(!"".equals(sStocMv)){ 
			    		iSeq = dao.updateStockTransInfo(sGoodsNo,
											     sStocMv);	
			    	}
			    	
				/**
				 *	출하완료 시점
				 */
			 	iSeq = dao.updateStockDelYnInfo(sGoodsNo,"Y");
			 	{
			 		String sStockMoveTerm	= sStocMv;
				    	String sOrderDate		= "";
				    	String sOrderSeq		= "";
				    	String sCardStockId		= sGoodsNo;
		    
					/**
					* SMJ 추가 
					* 원인: 
					*   . PUT실적 늦게 도착 작업자가 출발처리 못하고 그냥 출발하므로 다음 차량 도착 못함.
					*   . PUT실적 늦게 도착 하여 출발처리 못하고 그냥 출발하여 다음 도착 스케쥴 편성 안됨.
					* 조치: 
					*   . 적치열테이블 CARD 번호 CLEAR
					*   . 적치단테이블 코일 번호 CLEAR
					*/
					String cardNo = "";
					
					ymCommonDAO ymCommonDAO = new ymCommonDAO();
					JDTORecord dto = dao1.getStockInfo(sGoodsNo);
					if(dto != null && dto.size() > 0) {
						cardNo = StringHelper.evl(dto.getFieldString("CAR_CARD_NO"), "");
					}
					
					if(!"".equals(cardNo)){
						ymCommonDAO.modifyCardNoOfStackCol(cardNo);
				        //차량예약 포인트 지우기
				        ymCommonDAO.modifyCardNoOfStackCol2(cardNo);
				        
				        //차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
				        EJBConnector ejbConn2 = new EJBConnector("default","JNDITsInfoReg",this);
						ejbConn2.trx("CarPointinforeg", new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class},
					  	             new Object[]{"A","",cardNo,"","","","C"});
				        
					}
					
					if(!"".equals(sGoodsNo)){
						ymCommonDAO.modifyStackStateOfLayer(sGoodsNo);
					}
			    
			 		/**
	 	 			 *	2.1	상차지시 취소 모듈 CALL
	 	 			 */
	 	 			try{
		 	 			EJBConnector ejbConn = new EJBConnector("default","JNDIWorkOrderInfoReg",this);
	    					Boolean isTem2  = (Boolean)ejbConn.trx("removeWbookSlabGoodsList",
													new  Class[]{String.class,
																 String.class,
																 String.class,
																 String.class},
													new Object[]{sStockMoveTerm,
																 sOrderDate,
																 sOrderSeq,
																 sCardStockId});
					}catch(Exception e){
						logger.println(LogLevel.DEBUG,this," 상차지시 취소 모듈 EXCEPTION");
					}											 
			 	}
			
			}else{
			
				/**
				*	코일공통 진도코드 Table 참조.
				*/
		     		String[] sStockInfo = YmCommonUtil.getCoilCurrProgCd(sGoodsNo,"");
			    	String sProgCd   	= sStockInfo[0];
				String sStocMv   	= sStockInfo[1];
				
		    		logger.println(LogLevel.DEBUG,this,"저장품 이동조건 = "+ sStocMv );
				
				if(!"".equals(sStocMv)){ 
			    		iSeq = dao.updateStockTransInfo(sGoodsNo,
											     sStocMv);	
			    	}
				    
				if("Y".equals(sKeepSflag)){
					/**
					 *	보관매출 시점.
					 */
					iSeq = dao.updateStockKeepStockStlYnInfo(sGoodsNo,sKeepSflag);
						 
				}else if("C".equals(sKeepSflag)){
					/**
					 *	목전충당 시점
					 */
				
				}else if("D".equals(sKeepSflag)){
					/**
					 *	2007.06.15 이정훈 
					 *  대차 출하 취소
					 */
					EJBConnector ejbConn = new EJBConnector("default","JNDICraneStatusReg",this);
					Boolean resultRes =  (Boolean) ejbConn.trx("setWorkIdCancle", new Class[] {String.class}, new Object[] {sGoodsNo});	
				}else if("I".equals(sKeepSflag)){
				/**
				 *	2008.01.08 이정훈 
				 *  임가공 이송
				 */
				 String cardNo = "";
			     
				 ymCommonDAO ymCommonDAO = new ymCommonDAO();
				 JDTORecord dto = ymCommonDAO.readCoilInfo(sGoodsNo);
				 if(dto != null && dto.size() > 0) {
				     cardNo = StringHelper.evl(dto.getFieldString("CAR_CARD_NO"), "");
				 }
				    
				 if(!"".equals(cardNo)){
				     ymCommonDAO.modifyCardNoOfStackCol(cardNo);
				        //차량예약 포인트 지우기
				        ymCommonDAO.modifyCardNoOfStackCol2(cardNo);
				        
				      //차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
				        EJBConnector ejbConn2 = new EJBConnector("default","JNDITsInfoReg",this);
						ejbConn2.trx("CarPointinforeg", new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class},
					  	             new Object[]{"A","",cardNo,"","","","C"});
				 }
				    
				 if(!"".equals(sGoodsNo)){
				     ymCommonDAO.modifyStackStateOfLayer(sGoodsNo);
				 }
			    
			
				}else{
					/**
					 *	출하완료 시점
					 */
				 	iSeq = dao.updateStockDelYnInfo(sGoodsNo,"Y");
				 	{
				 		String sStockMoveTerm	= sStocMv;
					    	String sOrderDate		= "";
					    	String sOrderSeq		= "";
					    	String sIsGoods		= "YJK";
					    	String sIsCardCheck	= sGoodsNo;
					    
					    
					    /**
					     * SMJ 추가 
					     * -원인: 
					     * --PUT실적 늦게 도착 작업자가 출발처리 못하고 그냥 출발하므로 다음 차량 도착 못함.
					     * --PUT실적 늦게 도착 하여 출발처리 못하고 그냥 출발하여 다음 도착 스케쥴 편성 안됨.
					     * -조치: 
					     * --적치열테이블 CARD 번호 CLEAR
					     * --적치단테이블 코일 번호 CLEAR
					     */
					    String cardNo = "";
					     
					    ymCommonDAO ymCommonDAO = new ymCommonDAO();
					    JDTORecord dto = ymCommonDAO.readCoilInfo(sGoodsNo);
					    if(dto != null && dto.size() > 0) {
					        cardNo = StringHelper.evl(dto.getFieldString("CAR_CARD_NO"), "");
					    }
					    
					    /**
		 	 			 *	2.1	L2 출하실적 전문 송신
		 	 			 *		차량 야드맵 정보처리때문에 처리
		 	 			 */
		 	 			/*
		 	 			if(!"".equals(cardNo))
		 	 			{
			 	 			String sCarPosition = "";
			 	 			
			 	 			JDTORecord layerV = dao1.getStackLayerInfoWithStockId_02(sGoodsNo);
			 	 			
			 	 			if(layerV != null){
			 	 				sCarPosition = StringHelper.evl(layerV.getFieldString("STACK_COL_GP"), "");
			 	 			} 
			 	 			
			 	 			if(sCarPosition.startsWith(YmCommonConst.YD_GP_3)){
			 	 				try{
					 	 			EJBConnector ejbConn = new EJBConnector("default","JNDICTSStatusReg",this);
				    				Boolean isTem1  = (Boolean)ejbConn.trx("carStartOrder",
																new  Class[]{String.class,
																			 String.class,
																			 String.class},
																new Object[]{" ",			//한자리공백
																			 cardNo,		//카드번호
																			 sCarPosition});//차량정지위치
								}catch(Exception e){
									logger.println(LogLevel.DEBUG,this," L2 출하실적 전문 송신 모듈 EXCEPTION");
								}					
							}
						}
						*/
						if(!"".equals(cardNo)){
					        ymCommonDAO.modifyCardNoOfStackCol(cardNo);
					        //차량예약 포인트 지우기
					        ymCommonDAO.modifyCardNoOfStackCol2(cardNo);
					        
					      //차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
					        EJBConnector ejbConn2 = new EJBConnector("default","JNDITsInfoReg",this);
							ejbConn2.trx("CarPointinforeg", new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class},
						  	             new Object[]{"A","",cardNo,"","","","C"});
					    }
					    
					    if(!"".equals(sGoodsNo)){
					        ymCommonDAO.modifyStackStateOfLayer(sGoodsNo);
					    }
				    
				 		/**
		 	 			 *	2.1	상차지시 취소 모듈 CALL
		 	 			 */
		 	 			try{
			 	 			EJBConnector ejbConn = new EJBConnector("default","JNDIWorkOrderInfoReg",this);
		    					Boolean isTem2  = (Boolean)ejbConn.trx("removeWbookGoodsList",
														new  Class[]{String.class,
																	 String.class,
																	 String.class,
																	 String.class,
																	 String.class},
														new Object[]{sStockMoveTerm,
																	 sOrderDate,
																	 sOrderSeq,
																	 sIsGoods,
																	 sIsCardCheck});
						}catch(Exception e){
							logger.println(LogLevel.DEBUG,this," 상차지시 취소 모듈 EXCEPTION");
						}											 
				 	}
				}
			}		
			isVal = true; 
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }    				
        return isVal;
    }

       /**
	 * 오퍼레이션명 : 
	 *  
	 * YJK - INNER INTERFACE
        * 업무 로직
        *	1.TC_CD - DMYM006
        *	2.COIL 반납요구/취소(반납지시) 출하=>야드
        *	3.출하에서 반납 대상재 등록, 취소 시점
        *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean receiveInCoilInfo(DMYM006 yModel) { 
         
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
        logger.println(LogLevel.DEBUG,this," receiveInCoilInfo(DMYM006)");
        
        String sTc 			= StringHelper.evl(yModel.getTcCode(),"");
		String sDate 		= StringHelper.evl(yModel.getTcDate(),"");
		String sTime 		= StringHelper.evl(yModel.getTcTime(),"");
		String sProcessId 	= StringHelper.evl(yModel.getProcessId(),"");//처리구분(1:요구,2:취소)
		String sGoodsNo 	= StringHelper.evl(yModel.getGoodsNo(),"");
	
		logger.println(LogLevel.DEBUG,this," sTc 		=" + sTc);
		logger.println(LogLevel.DEBUG,this," sDate 		=" + sDate);
		logger.println(LogLevel.DEBUG,this," sTime 		=" + sTime);
		logger.println(LogLevel.DEBUG,this," sProcessId =" + sProcessId);
		logger.println(LogLevel.DEBUG,this," sGoodsNo 	=" + sGoodsNo);
		logger.println(LogLevel.DEBUG,this,"==COIL 반납요구/취소(반납지시) 전문수신==");
		
		boolean isVal = false;
		
		try{
			int iSeq 			= 0;
		    
			YdStockDAO dao	= new YdStockDAO();
		    
		    logger.println(LogLevel.DEBUG,this,"=============반납요구/취소 처리 시작========");
		    /**
		     *	1.	저장품Table에 정보를 등록,수정한다.
		     */
		    String sStockMoveTerm = "";
		    
		    if("1".equals(sProcessId)){
		    	sStockMoveTerm = YmCommonConst.NEW_STOCK_MOVE_TERM_JG;// Coil 제품 반납대기
		    }else if("2".equals(sProcessId)){
		    	sStockMoveTerm = YmCommonConst.NEW_STOCK_MOVE_TERM_KG;// Coil 제품 출하작업지시대기
			}
			
    		iSeq = dao.updateStockTransInfo(sGoodsNo,		// 저장품ID
											sStockMoveTerm);
			
			logger.println(LogLevel.DEBUG,this,"=============반납요구/취소 처리 종료========");				            
			isVal = true; 
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }    
	    		
        return isVal;
    }
    
       /**
	 * 오퍼레이션명 : 
	 *  
	 * YJK - INNER INTERFACE
        * 업무 로직
        *	1.TC_CD - DMYM007
        *	2.COIL 재료정보(반입지시) 출하=>야드
        *	2.출하시스템으로부터 Coil 재료정보(반입지시)를 수신
        *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean receiveInCoilInfo(DMYM007 yModel) { 
         
        logger.println(LogLevel.DEBUG,this," receiveInCoilInfo(DMYM007)");
        
        String sTc 			= StringHelper.evl(yModel.getTcCode(),"");
		String sDate 		= StringHelper.evl(yModel.getTcDate(),"");
		String sTime 		= StringHelper.evl(yModel.getTcTime(),"");
		String sGoodsEa 	= StringHelper.evl(yModel.getGoodsEa(),"");
		String sGoodsNo 	= StringHelper.evl(yModel.getGoodsNo(),"");
		
		logger.println(LogLevel.DEBUG,this," sTc 		=" + sTc);
		logger.println(LogLevel.DEBUG,this," sDate 		=" + sDate);
		logger.println(LogLevel.DEBUG,this," sTime 		=" + sTime);
		logger.println(LogLevel.DEBUG,this," sGoodsEa 	=" + sGoodsEa);
		logger.println(LogLevel.DEBUG,this," sGoodsNo 	=" + sGoodsNo);
		logger.println(LogLevel.DEBUG,this,"==COIL 재료정보(반입지시) 전문수신==");
		
		boolean isVal = false;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			int iSeq 			= 0;
		    
			YdStockDAO dao	= new YdStockDAO();
		    
		    logger.println(LogLevel.DEBUG,this,"=============반입지시 처리 시작========");
		    
		    /**
		     *	1.	공통 Coil정보를 가져온다.
		     */
			    JDTORecord stockV	= dao.getCoilCommonInfo(sGoodsNo);
			    
			    if(stockV == null){ 
			    	throw new EJBServiceException("=반입지시=>NO HAVE COMMON COIL DATA");
			    }
			    
	    	/**
		     *	2.	저장품Table에 정보를 등록,수정한다.
		     */
		    	boolean isExist = dao.isExistPrimaryKey(sGoodsNo);
		    	
		    	if(isExist){
		    		logger.println(LogLevel.DEBUG,this,"EXIST STOCK TABLE COIL DATA");
					iSeq = dao.updateStockTransInfo(sGoodsNo,				      	  	  // 저장품ID
													YmCommonConst.NEW_STOCK_MOVE_TERM_K1);// Coil 제품 반입대기
					
					iSeq = dao.updateStockDelYnInfo(sGoodsNo,"N");
					
				}else{
					logger.println(LogLevel.DEBUG,this,"NOT EXIST STOCK TABLE COIL DATA");
					
					iSeq = dao.insertStockTransInfo(sGoodsNo,					      	  // 저장품ID
													YmCommonConst.ITEM_CG,		      	  // 저장품품목(코일제품)
													YmCommonConst.NEW_STOCK_MOVE_TERM_K1);// Coil 제품 반입대기
				}
			
			logger.println(LogLevel.DEBUG,this,"=============반입지시 처리 종료========");				            
			isVal = true; 
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }    
					
	    		
        return isVal;
    }
    
       /**
	 * 오퍼레이션명 : 
	 *  
	 * YJK - INNER INTERFACE
        * 내부인터페이스로부터 넘어온 전문을 파싱한 후 
        * 전문내용을 가지고 해당업무로직을 처리한다.
        * 전문내용을 JDTORecord로 파싱한다.
        * 업무 로직
        *	1.TC_CD - POYM001
        *	2.조업시스템으로부터 Coil 재료정보를 수신
        *	3.Coil 재료정보를 Key로 공정시스템,출하시스템의 
        *    Coil 상세정보 Table을 Join Read한다.
        * 
        *
	 *         처리구분		ProcessID	CHAR	02		
	 *                      '01' 압연실적 (Back-up포함)처리시점
	 *                      'DC' DC OFF   (Back-up포함)처리시점
	 *                      '02' 정정실적 (Back-up포함)처리시점
	 *                      '03' 원재료 종료 시점
	 *                      '04' 결번 시점
	 *                      '05' 보류재 처리 시점
	 *                      '06' 목전충당 시점
	 *                      '07' HFL처리시점
	 *                      '08' 모 Coil 종료
	 *                      '09' 자 Coil
	 *                      ’10’ 반납 시점
	 * 						'J2' SPM 재작업요구 : 
	 *								수신이 되면 대상재가 출측에 있다면 
	 *								입측 D5로 보내고 조업으로 보급완료실적을 송신
	 *                              대상재가 입측에 있다면 그대로 두고 보급완료실적을 송신
	 *        
	 *         결번/반납 야드구분	YardID	    CHAR	1		
	 *                       '1' A 열연
	 *                       '3' B 열연
	 *        
	 *         결번/반납 공정 코드    ProcessCode	CHAR	2		
	 *                       '4K' SPM
	 *                       '2H' HFL
	 * 
	 *			저장품상태--
	 *		    1	Coil 압연실적 처리
	 *		 	2	정정실적 처리
	 *			3	원재료 종료 시점
	 *			4	결번 시점
	 *			5	보류재 처리 시점
	 *			6	목전충당 시점
	 *			7	HFL 처리시점
	 *			O	출고완료
	 *			P	입고예정
	 *			S	야드내재고
	 *          
	 *          저장품품목 --
	 *          CM - 코일소재
	 *          CG - 코일제품
        *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean receiveInCoilInfo(POYM001 yModel) { 
		 
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		logger.println(LogLevel.DEBUG,this," receiveInCoilInfo(POYM001)");
		
		String sTc 			= StringHelper.evl(yModel.getTcCode(),"");
		String sDate 		= StringHelper.evl(yModel.getTcDate(),"");
		String sTime 		= StringHelper.evl(yModel.getTcTime(),"");
		String sProcessId	= StringHelper.evl(yModel.getProcessID(),"");
		String sCoilNo		= StringHelper.evl(yModel.getCoilNo(),"");
		String sYardID		= StringHelper.evl(yModel.getYardID(),"");
		String sProcessCode	= StringHelper.evl(yModel.getProcessCode(),"");
		
		logger.println(LogLevel.DEBUG,this," sTc 		=" + sTc);
		logger.println(LogLevel.DEBUG,this," sDate 		=" + sDate);
		logger.println(LogLevel.DEBUG,this," sTime 		=" + sTime);
		logger.println(LogLevel.DEBUG,this," sProcessId =" + sProcessId);
		logger.println(LogLevel.DEBUG,this," sCoilNo 	=" + sCoilNo);
		logger.println(LogLevel.DEBUG,this," sYardID 	=" + sYardID);
		logger.println(LogLevel.DEBUG,this," sProcessCode 	=" + sProcessCode);
		
		boolean isVal = false;
		
		/*
		 * 1. 압연실적 (Back-up포함)처리시점
		 */
		if("01".equals(sProcessId)){
			
			isVal = setInnerIFCoilInfo_01( sCoilNo, sYardID , "01");
		/*
		 * 2. 정정실적 (Back-up포함)처리시점
		 */
		}else if("02".equals(sProcessId)){
			
			isVal = setInnerIFCoilInfo_02( sCoilNo,"SPM");
		/*
		 * 3. 원재료 종료 시점
		 */
		}else if("03".equals(sProcessId)){
			//SKIP
		/*
		 * 4. 결번 시점
		 */
		}else if("04".equals(sProcessId)){
			//SKIP
		/*
		 * 5. 보류재 처리 시점
		 */
		}else if("05".equals(sProcessId)){
			
			isVal = setInnerIFCoilInfo_03( sCoilNo,  sYardID );
				
		/*
		 * 6. 목전충당 시점
		 */
		}else if("06".equals(sProcessId)){
			//SKIP
		/*
		 * 7. HFL처리시점
		 */
		}else if("07".equals(sProcessId)){
			
			isVal = setInnerIFCoilInfo_02( sCoilNo,"HFL");
			
		/*
		 * 8. 모 Coil 종료
		 */
		}else if("08".equals(sProcessId)){
			
			isVal = setInnerIFCoilInfo_05( sCoilNo );
		/*
		 * 9. 자 Coil
		 */
		}else if("09".equals(sProcessId)){
			
			isVal = setInnerIFCoilInfo_06( sCoilNo );
		/*
		 * 10. 반납 시점
		 */
		}else if("10".equals(sProcessId)){
			
			isVal = setInnerIFCoilInfo_04( sCoilNo );
		
		/*
		 * J2. SPM 재작업
		 */
		}else if("J2".equals(sProcessId)){
			
			isVal = setInnerIFCoilInfo_07( sCoilNo, sYardID);
		
		/*
		 * Q2. EQL 재작업
		 */
		}else if("Q2".equals(sProcessId)){
				
				isVal = setInnerIFCoilInfoEQL_07( sCoilNo, sYardID);	
		/*
		 * 91. 조업시스템 에러.
		 *		Coil 공통 테이블에 있는 정보만 처리한다.
		 */
		}else if("91".equals(sProcessId)){		
			
			isVal = setInnerIFCoilInfo_01( sCoilNo, sYardID , "91");						
		/*
		 * DC. DC OFF.
		 *		Coil 공통 테이블에 있는 정보만 처리한다.
		 */
		}else if("DC".equals(sProcessId)){		
			
			isVal = setInnerIFCoilInfo_01( sCoilNo, sYardID , "DC");						
		/*
		 * 11. 요구차 공정 변경.
		 *		Coil 다음 공정 정보 변경시 처리.
		 */
		}else if("11".equals(sProcessId)){		
			
			isVal = setInnerIFCoilInfo_11( sCoilNo, sYardID , sProcessCode);						
		}
		/*
		 * HP. HR-Plate 생성
		 * 2008.01.04 이정훈 
		 */
		else if("HP".equals(sProcessId)){		
			
			isVal = setInnerIFCoilInfo_HP( sCoilNo, sYardID , sProcessCode);						
		}
		/** 최규성 추가
		 * 12. SPM2 정정실적 처리
		 * 
		 */
//		else if ("12".equals(sProcessId)){
//			isVal = setInnerIFCoilInfo_02( sCoilNo, "SPM2");
//		}
		/**
		 * 최규성 추가 
		 * 13. SPM2 자Coil 실적처리
		 * */
//		else if ( "13".equals(sProcessId)){
//			isVal = setInnerIFCoilInfo_06( sCoilNo, "N" );
//		}
		/***
		 * 최규성 추가
		 * 14. 모Coil 종료 처리
		 */
//		else if("14".equals(sProcessId)){
//			isVal = setInnerIFCoilInfo_05( sCoilNo,"N" );
//		}
		/***
		 * 최규성 추가 
		 * 15. SPM2 재작업
		 * 
		 */
//		else if ("15".equals(sProcessId)){
//			isVal = setInnerIFCoilInfo_07( sCoilNo, sYardID,"N");
//		}
		/**
		 * 최규성 추가
		 * 16.요구차 공정 변경
		 * 
		 */
//		else if ("16".equals(sProcessId)){
//			isVal = setInnerIFCoilInfo_11( sCoilNo, sYardID , sProcessCode);
//		}
		return isVal;
	} 
	
       /**
	 * 오퍼레이션명 : 
	 *  
	 * YJK
        * 압연실적을 처리
        *
        * param String	: 저장품ID
        * param String	: 야드구분
        * param String	: 처리구분
        *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean setInnerIFCoilInfo_HP(String sGoodsNo,
			 							 String sYardId,
			 							 String sProcessId){

		boolean isVal = false;
		Boolean isSuccess = new Boolean(false);

		try{
			
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			JDTORecord stockV 	= null;
			int iSeq 			= 0;

			YdStockDAO dao	= new YdStockDAO();
			CraneSchDAO dao1= new CraneSchDAO();

			
			String sMsg = "";
			String sStockMoveTerm = "";
			String sPut_Position= "";
			
			logger.println(LogLevel.DEBUG,this,"=============Plate 실적  처리 시작========");
			
			/**
			*	1.	공통 Coil정보를 가져온다.
			*/
			stockV	= dao.getCoilCommonInfo(sGoodsNo);
			
			if(stockV == null){ 
				logger.println(LogLevel.DEBUG,this,"=Plate실적=>NO HAVE COMMON COIL DATA");
			
			if("91".equals(sProcessId)){	
				throw new EJBServiceException("=Plate실적=>조업 91 NO HAVE COMMON COIL DATA");
				}
			}
			
			if("".equals(sStockMoveTerm)){ 
				sStockMoveTerm =  YmCommonConst.NEW_STOCK_MOVE_TERM_A2;
			}
			/**
			*	2.	저장품Table에 정보를 등록,수정한다.
			*		최초 발생시 등록, 재 실적발생시 수정
			*/
			boolean isExist = dao.isExistPrimaryKey(sGoodsNo);
			
			if(isExist){
				logger.println(LogLevel.DEBUG,this,"=압연실적=>EXIST STOCK TABLE COIL DATA");
			
				iSeq = dao.updateStockTransInfo_06(sGoodsNo,					     // 저장품ID
									   YmCommonConst.ITEM_HP,		     // 저장품품목(코일소재)
									   sStockMoveTerm					 // 저장품이동조건
									   );  			
				return false;													         
			}else{
				logger.println(LogLevel.DEBUG,this,"=압연실적=>NOT EXIST STOCK TABLE COIL DATA");
			
				iSeq = dao.insertStockTransInfo(sGoodsNo,					      // 저장품ID
										YmCommonConst.ITEM_HP,		      // 저장품품목(코일소재)
										sStockMoveTerm					  // 저장품이동조건	
										);  			      				
			}	 	 
										
			logger.println(LogLevel.DEBUG,this,"=============Plate 실적 처리 종료========");	
			
			/*
			 * 2008.02.14 이정훈
			 * 야드 맵 확인 및 수정
			 */
			EJBConnector ejbConn1 = new EJBConnector("default","JNDICraneSchReg",this);
			sPut_Position = (String)ejbConn1.trx("getEmptyLoc",new Class[]{String.class},
					   new Object[]{sYardId});
			if ( sPut_Position == null || "".equals(sPut_Position)) 
			{
				sPut_Position = sYardId + "A01010101";
			}
			
			isSuccess = (Boolean)ejbConn1.trx("changeCoilLocationInfo",new Class[]{String.class, 
					   String.class, 
					   String.class},
				   new Object[]{sGoodsNo, 
				    			"", 
				    			sPut_Position});
			
			logger.println(LogLevel.DEBUG,this,"============================================");
			logger.println(LogLevel.DEBUG,this,"loc  "+sPut_Position);
			logger.println(LogLevel.DEBUG,this,"============================================");	
			
			/*
			* 2008.01.14 이정훈
			* 입고 TC 송신
			*/
//			YMDM001 model = new YMDM001();
//			model.setTcCode(YmCommonConst.MODEL_YMDM001);
//			model.setTcDate(YmCommonUtil.getCurDate("yyyy-MM-dd"));
//			model.setTcTime(YmCommonUtil.getCurDate("HH-mm-ss"));
//			
//			/** 입고 일자 */
//			model.setRECEIPT_DATE(YmCommonUtil.getCurDate("yyyyMMdd"));
//			
//			/** 입고 시각 */
//			model.setRECEIPT_TIME(YmCommonUtil.getCurDate("HHmmss"));
//			
//			/** YARD 구분 1:ACoil, 2:BSlab, 3:BCoil */
//			model.setYD_GP(sYardId);
//			
//			/** 제품 번호 */ 
//			model.setGOODS_NO(sGoodsNo);
//			
//			/** 권하 위치 */ 
//			model.setSTORE_LOC(sPut_Position);
//			
//			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
//			isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{CommonModel.class},
//											  	  	 new Object[]{model});
//			logger.println(LogLevel.DEBUG,this, "내부IF호출===Coil을 제품 야드로 입고시.===");
			
			
		    //일관제철 ##############################################################################################
           
  		   //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//           //임가공입고작업실적
//			JDTORecord tcRecordDM = null;
//			tcRecordDM = JDTORecordFactory.getInstance().create(); 
//			tcRecordDM.setField("GOODS_NO",sGoodsNo);
//			tcRecordDM.setField("YD_GP",sYardId);
//			tcRecordDM.setField("STORE_LOC",sPut_Position);
//			
//			//인터페이스 전문 호출
//			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
//			isSuccess = (Boolean)ejbConn.trx("getYDDMR003",new Class[]{JDTORecord.class},
//			  	  	 new Object[]{tcRecordDM}); 
//           logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 코일 임가공입고작업실적.===");
           //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
			isVal = true; 
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
	}    
	
	return isVal;
	} 
	    
     /**
	 * 오퍼레이션명 : 
	 *  
	 * YJK
        * 압연실적을 처리
        *
        * param String	: 저장품ID
        * param String	: 야드구분
        * param String	: 처리구분
        *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */   
    public boolean setInnerIFCoilInfo_01(String sGoodsNo,
    									 String sYardId,
    									 String sProcessId){
		
		boolean isVal = false;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			JDTORecord stockV 	= null;
			int iSeq 			= 0;
		    
			YdStockDAO dao	= new YdStockDAO();
			CraneSchDAO dao1= new CraneSchDAO();
		    
			String sMsg = "";
			String sStockMoveTerm = "";
			    
		    logger.println(LogLevel.DEBUG,this,"=============압연실적 처리 시작========");
		    /**
		     *	1.	공통 Coil정보를 가져온다.
		     */
			   stockV	= dao.getCoilCommonInfo(sGoodsNo);
			    
			    if(stockV == null){ 
			    	logger.println(LogLevel.DEBUG,this,"=압연실적=>NO HAVE COMMON COIL DATA");
			    	
			    	if("91".equals(sProcessId)){	
			    		throw new EJBServiceException("=압연실적=>조업 91 NO HAVE COMMON COIL DATA");
			    	}
			    }else{
			    
			    	if(YmCommonConst.YD_GP_1.equals(sYardId)){	
						
						String sProgCd 		= StringHelper.evl(stockV.getFieldString("CURR_PROG_CD"),"").trim();
						String sCoilProc    = "";
				    	String sNextProc 	= StringHelper.evl(stockV.getFieldString("차공정"),"").trim();
						String sPlanProc 	= StringHelper.evl(stockV.getFieldString("계획공정"),"").trim();
						
						if("".equals(sNextProc)){
							sCoilProc = sPlanProc;
						}else{
							sCoilProc = sNextProc;
						}
						
						if(YmCommonConst.CURR_PROG_CD_COIL_1.equals(sProgCd)){
				    		
							sStockMoveTerm   = YmCommonConst.NEW_STOCK_MOVE_TERM_1C;
				    		
				    	}else{//if(YmCommonConst.CURR_PROG_CD_COIL_A.equals(sProgCd)){
				    		
				    		if(YmCommonConst.SHEAR_SUPPLY_GP_1K.equals(sCoilProc)){		 //A열연 SPM
							
								sStockMoveTerm = YmCommonConst.NEW_STOCK_MOVE_TERM_A2;		//SPM 추출
							}else if(YmCommonConst.SHEAR_SUPPLY_GP_1H.equals(sCoilProc)){//A열연 HFL
							
								sStockMoveTerm = YmCommonConst.NEW_STOCK_MOVE_TERM_A1;		//HFL 추출
							}
						}
					}else{
						
						String sProgCd 		= StringHelper.evl(stockV.getFieldString("CURR_PROG_CD"),"").trim();
						String sCoilProc    = "";
				    	String sNextProc 	= StringHelper.evl(stockV.getFieldString("차공정"),"").trim();
						String sPlanProc 	= StringHelper.evl(stockV.getFieldString("계획공정"),"").trim();
						
						
						
						if("".equals(sNextProc)){
							sCoilProc = sPlanProc;
						}else{
							sCoilProc = sNextProc;
						}
						
						if(YmCommonConst.CURR_PROG_CD_COIL_1.equals(sProgCd)){
				    		
							sStockMoveTerm   = YmCommonConst.NEW_STOCK_MOVE_TERM_1C;
				    	
				    	}else if(YmCommonConst.CURR_PROG_CD_COIL_3.equals(sProgCd)){
						
							logger.println(LogLevel.DEBUG,this,"=압연실적=>생산종료된 정보입니다="+sGoodsNo);
							return false;			
							
						}else{
							// SPM2에 대한 코드 추가.
							// 2009-10-05 최규성
				    		if(YmCommonConst.SHEAR_SUPPLY_GP_5K.equals(sCoilProc)){		 //B열연 SPM
								sStockMoveTerm = YmCommonConst.NEW_STOCK_MOVE_TERM_A2;		//SPM 추출
							}else if(YmCommonConst.SHEAR_SUPPLY_GP_5H.equals(sCoilProc)){//B열연 HFL
							
								sStockMoveTerm = YmCommonConst.NEW_STOCK_MOVE_TERM_A1;		//HFL 추출
							}else if(YmCommonConst.SHEAR_SUPPLY_GP_5T.equals(sCoilProc)){//B열연 수냉재
							
								sStockMoveTerm = YmCommonConst.NEW_STOCK_MOVE_TERM_A3;		//수냉재 추출
							}else if(YmCommonConst.SHEAR_SUPPLY_GP_5A.equals(sCoilProc)){//B열연 공냉재
							
								sStockMoveTerm = YmCommonConst.NEW_STOCK_MOVE_TERM_A4;		//공냉재 추출
							}else if(YmCommonConst.SHEAR_SUPPLY_GP_6K.equals(sCoilProc)){//B열연 SPM2
								sStockMoveTerm = YmCommonConst.NEW_STOCK_MOVE_TERM_A6;		// SPM2 추출
							}
						}
					}
				}
				logger.println(LogLevel.DEBUG,this,"저장품 이동조건 ="+sStockMoveTerm+"=");
			    
			    if("".equals(sStockMoveTerm)){ 
					sStockMoveTerm =  YmCommonConst.NEW_STOCK_MOVE_TERM_A2;
				}
	    	/**
		     *	2.	저장품Table에 정보를 등록,수정한다.
		     *		최초 발생시 등록, 재 실적발생시 수정
		     */
		    	boolean isExist = dao.isExistPrimaryKey(sGoodsNo);
		    	
		    	if(isExist){
		    		logger.println(LogLevel.DEBUG,this,"=압연실적=>EXIST STOCK TABLE COIL DATA");
					
					iSeq = dao.updateStockTransInfo_06(sGoodsNo,					     // 저장품ID
													   YmCommonConst.ITEM_CM,		     // 저장품품목(코일소재)
													   sStockMoveTerm					 // 저장품이동조건
													   );  			
					return false;													         
				}else{
					logger.println(LogLevel.DEBUG,this,"=압연실적=>NOT EXIST STOCK TABLE COIL DATA");
					
					iSeq = dao.insertStockTransInfo(sGoodsNo,					      // 저장품ID
													YmCommonConst.ITEM_CM,		      // 저장품품목(코일소재)
													sStockMoveTerm					  // 저장품이동조건	
													);  			      				
				}
			
			/**
		     *	5.	압연실적전문을 송신한다.
		     */	
		     	if(stockV != null){
					isVal = callCoilMsgInfo(stockV,
								            sGoodsNo,
								            "");
				}
				
			/**
		     *	3.	야드Map에 저장품정보를 등록,수정한다.
		     */
 			  	String sStackColGp = "";
 			  	
 			  	if(YmCommonConst.YD_GP_1.equals(sYardId)){	
					
					sStackColGp = YmCommonConst.STACK_COL_GP_1CDC01;
							
				}else{
				
					sStackColGp = YmCommonConst.STACK_COL_GP_3XDC01;
				}
 			  	
				//	분기콘베이어 정보 생성
				iSeq = YmCommonDB.insertConveyorInfo(sStackColGp,
													 sGoodsNo,
													 YmCommonConst.GBN_MIN);
				
				logger.println(LogLevel.DEBUG,this,"=============Conveyor Create========");
				logger.println(LogLevel.DEBUG,this,"STACK_COL_GP    ="+sStackColGp);
				logger.println(LogLevel.DEBUG,this,"sGoodsNo		="+sGoodsNo);
				logger.println(LogLevel.DEBUG,this,"iSeq			="+iSeq);		
				
				if(iSeq < 1){
					//throw new EJBServiceException("=압연실적=>CONVEYOR CREATE FAIL="+iSeq);
					logger.println(LogLevel.DEBUG,this,"=압연실적=>CONVEYOR CREATE FAIL="+iSeq);
					return false;			
				}
				
		   /**
			*	4.	A열연 = 실적등록시에 작업예약을 생성한다.
			*		B열연 = 작업요구시에 작업예약을 생성한다. 
			*/
				if(YmCommonConst.YD_GP_1.equals(sYardId)){	
				
					//	작업예약을 호출한다.
					isVal = callCoilWbookInfo(sGoodsNo,
			   	 						 	  sStockMoveTerm);						  				
				}
			
			/**
		     *	6.	코일공통 위치 수정
		     */		
				iSeq = dao1.updateCoilCommonLocInfo(sGoodsNo,sStackColGp +
															 YmCommonConst.STACK_BED_GP_01 +
															 YmCommonConst.STACK_LAYER_GP_01);
			
			/**
			 *	최규성 2009-10-05
			 *주석처리. 2009-12-08
			 */		
			//========================================================================================
			// 7. 수신된 정보의 확장Conveyor코드를 변경한다.
			//   조건 : 확장Conveyor의 고장 여부
			//========================================================================================
			//	boolean bRet = false;
			//	List listBranch = new ArrayList();
			//	stockV.getFieldString("분기위치코드");
			//	stockV.getFieldString("확장분기위치코드");
			//	listBranch = checkExtHindrance(stockV, sGoodsNo);		// 고장난 설비의 분기위치코드를 반환한다.
				
			//	if(listBranch.size() > 0){
			//		bRet = modifyExtBranchCode(listBranch, sGoodsNo);	// 코일공통 테이블의 항목을 변경한다.
			//	}
				
			logger.println(LogLevel.DEBUG,this,"=============압연실적 처리 종료========");				            
			isVal = true; 
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }    
	     
	    return isVal;
	} 
	
     /**
	 * 오퍼레이션명 : 
	 *  
	 * YJK
        * 정정실적(SPM,HFL)을 처리
        *
        * param String	: 저장품ID
        * param String	: SPM,HFL 구분
        *
     * 최규성
     	* SPM2관련 분기 코드 추가. 2009-12-08      
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */  
    public boolean setInnerIFCoilInfo_02(String sGoodsNo,
    									 String sGbnWork){
		
		boolean isVal = false;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			JDTORecord stockV 	= null;
			JDTORecord poV 		= null;
			
			int iSeq 			= 0;
		    
			YdStockDAO dao	= new YdStockDAO();
			
			String sPlantGp 	  = "";
			String sStockItem     = "";
			String[] sTotalPassProc	  = new String[5];		// SPM2 통과공정
			String sFinalPassProc = "";
			String sBayGp			="";
			logger.println(LogLevel.DEBUG,this,"=============정정실적 처리 시작========");    
		    /**
		     *	1.	공통 Coil정보를 가져온다.
		     */
			    stockV	= dao.getCoilCommonInfo(sGoodsNo);
			    
			    if(stockV == null){ 
			    	logger.println(LogLevel.DEBUG,this,"NO HAVE COMMON COIL DATA");
			    	throw new EJBServiceException("=정정실적=>NO HAVE COMMON COIL DATA");
				}
			    
			    sPlantGp = StringHelper.evl(stockV.getFieldString("공장구분"),"");
			    sBayGp 	 = StringHelper.evl(stockV.getFieldString("YD_BAY_GP"),"");
			    //=================================================================================================
			    // COILCOMM TBL의 통과공정 1~5 중 가장 마지막에 입력된 통과공정을 비교하여야 한다. 최규성. 
//			    for(int i=0; i < 5; i++){
//			    	sTotalPassProc[i] = StringHelper.evl(stockV.getFieldString("통과공정"+String.valueOf(i+1)),"-");		// SPM2관련 추가 최규성
//			    	if( (sTotalPassProc[i].equals("-") || sTotalPassProc[i].equals("")	) && i != 0){
//			    		sFinalPassProc = sTotalPassProc[i-1];
//			    	}
//			    }
			    
			    sFinalPassProc = checkCoilCommonInfo(sGoodsNo);
			    
			    
			    if(sGbnWork.equals("EQLJ")){
			    	
			    } else{
				    // SPM2 실적일 경우는 작업구분을 SPM2로 변경한다. 최규성
				    if(sFinalPassProc.equals("6K"))	{
				    	sGbnWork = "SPM2"; 
				    }else if(sFinalPassProc.equals("1Q")){
				    	sGbnWork = "EQL"; 
				    }else if(sFinalPassProc.equals("5K")||
				    		 sFinalPassProc.equals("1K")||
				    		 sFinalPassProc.equals("5H")||
				    		 sFinalPassProc.equals("6H")||
				    		 sFinalPassProc.equals("1H")){
				    	;
				    }else{
						// Error 발생.
						logger.println(LogLevel.DEBUG,this,"=SPM 통과공정 이상=> 잘못된 통과공정.");
						throw new EJBServiceException("=SPM 통과공정 이상=> 잘못된 통과공정.");
	
				    }
			    }
			  //=================================================================================================
	    	/**
		     *	2.	코일공통 진도코드 Table 참조.
		     */
		     	String[] sStockInfo = YmCommonUtil.getCoilCurrProgCd(sGoodsNo,"");
			    String sProgCd   	= sStockInfo[0];
				String sStocMv   	= sStockInfo[1];
				
		    	if(YmCommonConst.NEW_STOCK_MOVE_TERM_HG.equals(sStocMv)){
		    		sStockItem	   = YmCommonConst.ITEM_CG;
		    	}else{
		    		sStockItem	   = YmCommonConst.ITEM_CM;
		    	}
		    	
		    	logger.println(LogLevel.DEBUG,this,"저장품 이동조건 = "+ sStocMv );
				
				if("".equals(sStocMv)){ 
			    	logger.println(LogLevel.DEBUG,this,"NO HAVE 코일 공통 진도코드 DATA");
			    	throw new EJBServiceException("=정정실적=>NO HAVE 코일 공통 진도코드 DATA");
			    }
			    	
				iSeq = dao.updateStockTransInfo_06(sGoodsNo,
												   sStockItem,
												   sStocMv);
				
			/**
		     *	3.	SPM 야드 MAP 정보 셋팅
		     */
			
			String sIStackColGp1 = "";
			String sIStackColGp2 = "";
			String sOStackColGp  = "";
			
			if(YmCommonConst.PLANT_GP_A.equals(sPlantGp)||
			   YmCommonConst.PLANT_GP_H.equals(sPlantGp)){
			   	 
				if("EQLJ".equals(sGbnWork)){  //EQL 재작업 추출인경우 
				   	/**
					 * A설비 적치열 정보 셋팅
					 * 1EQE01	EQL입측컨베이어
					 * 1FQE01	EQL입측컨베이어
					 * 1FQD01	EQL출측컨베이어
					 * 1GQD01	EQL출측컨베이어
					 */
				   	sIStackColGp1 = "1EQE"+YmCommonConst.STACK_BED_GP_01;
				   	sIStackColGp2 = "1FQE"+YmCommonConst.STACK_BED_GP_01;				   	
				   	sOStackColGp  = "1FQD"+YmCommonConst.STACK_BED_GP_01;
				   	
				}else if("EQL".equals(sGbnWork)){
				   	/**
					 * A설비 적치열 정보 셋팅
					 * 1EQE01	EQL입측컨베이어
					 * 1FQE01	EQL입측컨베이어
					 * 1FQD01	EQL출측컨베이어
					 * 1GQD01	EQL출측컨베이어
					 */
				   	sIStackColGp1 = "1EQE"+YmCommonConst.STACK_BED_GP_01;
				   	sIStackColGp2 = "1FQE"+YmCommonConst.STACK_BED_GP_01;
				   	
				   	if(sBayGp.equals("F")){
				   		sOStackColGp  = "1FQD"+YmCommonConst.STACK_BED_GP_01;  //한시적 사용
				   	}else{
				   		sOStackColGp  = "1GQD"+YmCommonConst.STACK_BED_GP_01;
				   	}
				}else if("SPM".equals(sGbnWork)){
				   	/**
					 * A설비 적치열 정보 셋팅
					 * 1DKE01	SPM입측컨베이어
					 * 1EKE01	SPM입측컨베이어
					 * 1EKD01	SPM출측컨베이어
					 */
				   	sIStackColGp1 = YmCommonConst.SPM_COL_1DKE+YmCommonConst.STACK_BED_GP_01;
				   	sIStackColGp2 = YmCommonConst.SPM_COL_1EKE+YmCommonConst.STACK_BED_GP_01;
				   	sOStackColGp  = YmCommonConst.SPM_COL_1EKD+YmCommonConst.STACK_BED_GP_01;
				}else if("HFL".equals(sGbnWork)){
					/**
					 * A설비 적치열 정보 셋팅
					 * 1BFE01	HFL입측컨베이어
					 * 1CFD01	HFL출측컨베이어
					 */
				   	sIStackColGp1 = YmCommonConst.HFL_COL_1BFE+YmCommonConst.STACK_BED_GP_01;
				   	sOStackColGp  = YmCommonConst.HFL_COL_1CFD+YmCommonConst.STACK_BED_GP_01;
				}
			}else{
				if("SPM".equals(sGbnWork)){
					/**
					 * B 설비 적치열 정보 셋팅
					 * 3CKE01	SPM입측컨베이어
					 * 3BKE01	SPM입측컨베이어
					 * 3BKD01	SPM출측컨베이어
					 */
					sIStackColGp1 = YmCommonConst.SPM_COL_3CKE+YmCommonConst.STACK_BED_GP_01;
					sIStackColGp2 = YmCommonConst.SPM_COL_3BKE+YmCommonConst.STACK_BED_GP_01;
					sOStackColGp  = YmCommonConst.SPM_COL_3BKD+YmCommonConst.STACK_BED_GP_01;
				}else if("HFL".equals(sGbnWork)){
					/**
					 * B 설비 적치열 정보 셋팅
					 * 3AFE01	HFL입측컨베이어
					 * 3CFD01	HFL출측컨베이어
					 */
					sIStackColGp1 = YmCommonConst.HFL_COL_3AFE+YmCommonConst.STACK_BED_GP_01;
					sOStackColGp  = YmCommonConst.HFL_COL_3CFD+YmCommonConst.STACK_BED_GP_01;
				}else if("SPM2".equals(sGbnWork)){
					/**
					 * 최규성 2009-12-08
					 * B SPM2 설비 적치열 정보 셋팅
					 * 3DKE01	SPM2 입측컨베이어
					 * 3EKD01	SPM2 출측컨베이어
					 */
					sIStackColGp1 = YmCommonConst.SPM_COL_3DKE+YmCommonConst.STACK_BED_GP_01;
					sIStackColGp2 = YmCommonConst.SPM_COL_3DKE+YmCommonConst.STACK_BED_GP_01;
					sOStackColGp  = YmCommonConst.SPM_COL_3EKD+YmCommonConst.STACK_BED_GP_01;					
				}else if ("HFL2".equals(sGbnWork)){
					sIStackColGp1 = YmCommonConst.HFL_COL_3EFE+YmCommonConst.STACK_BED_GP_01;
					sIStackColGp2 = YmCommonConst.HFL_COL_3EFE+YmCommonConst.STACK_BED_GP_01;
					sOStackColGp  = YmCommonConst.SPM_COL_3EKD+YmCommonConst.STACK_BED_GP_01;	
				}
			}
			
			/**
			 *	4.	SPM STOCK_ID 입측삭제, 출측추가
			 */
			if(!"".equals(sIStackColGp1)){ 
				iSeq = YmCommonDB.deleteConveyorInfo(sIStackColGp1,
			   						  				 sGoodsNo);
		   	}					  		
		   	if(iSeq < 1 &&
		   	   !"".equals(sIStackColGp2)){ 		 
		   		iSeq = YmCommonDB.deleteConveyorInfo(sIStackColGp2,
			   						  				 sGoodsNo);
		   	}
		   	
		   	/**
		   	 * 정정실적 발생시에 야드에 존재하는 
		   	 * 코일정보가 있으면 삭제한다.
		   	 */
		   	isVal = deleteCoilLocationInfo(sGoodsNo);
		   	
		   	/**
		   	 * 입측에 존재하지 않아도 출측 정보생성
		   	 * 출측에 이미 존재하면 생성안함
		   	 */
		   	if(!"".equals(sOStackColGp)){ 					  				 
		   		iSeq = YmCommonDB.insertConveyorInfo(sOStackColGp, 
			   						  				 sGoodsNo,
			   						  				 YmCommonConst.GBN_MIN);
			}
			
			logger.println(LogLevel.DEBUG,this,"=============정정실적========");
			logger.println(LogLevel.DEBUG,this,"sGbnWork		="+sGbnWork);
			logger.println(LogLevel.DEBUG,this,"sIStackColGp1	="+sIStackColGp1);
			logger.println(LogLevel.DEBUG,this,"sIStackColGp2	="+sIStackColGp2);
			logger.println(LogLevel.DEBUG,this,"sOStackColGp	="+sOStackColGp);
			logger.println(LogLevel.DEBUG,this,"sGoodsNo		="+sGoodsNo);
			logger.println(LogLevel.DEBUG,this,"iSeq			="+iSeq);
			
			logger.println(LogLevel.DEBUG,this,"=============정정실적 처리 종료========");   
			
			if(YmCommonConst.PLANT_GP_A.equals(sPlantGp)||
			   YmCommonConst.PLANT_GP_H.equals(sPlantGp)){
				
				if(iSeq > 0 && 
				   "HFL".equals(sGbnWork)){
					logger.println(LogLevel.DEBUG,this,"=============if(iSeq > 0 && 'HFL'.equals(sGbnWork) ) 시작========");
					/**
		    		 * HFL 추출요구 호출
		    		 * YardID 
		    		 * WorkID 
		    		 * ProcessID 
		    		 * CoilNo
		    		 */
			    	EJBConnector ejbConn = new EJBConnector("default","JNDISPMConStatReg",this);
					
					Boolean isSuccess = (Boolean)ejbConn.trx("callLineInOut",new  Class[]{String.class,
																						  String.class,
																						  String.class,
																						  String.class},
																			  new Object[]{YmCommonConst.YD_GP_1,
																			  			   YmCommonConst.WORK_HFL_H,
																			  			   YmCommonConst.PROCESS_ID_3,
																			  			   sGoodsNo});
				}
			}
					            
			isVal = true; 
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }    
	     
	    return isVal;
	} 
	
      /**
	 * 오퍼레이션명 : 
	 *  
	 * YJK
        * 보류재 실적 처리
        *
        * param String	: 저장품ID
        * param String	: 야드구분
        *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */   
    public boolean setInnerIFCoilInfo_03(String sGoodsNo,
    									 String sYardId){
		
		boolean isVal = false;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			JDTORecord stockV 	= null;
			int iSeq 			= 0;
		    
			YdStockDAO dao		= new YdStockDAO();
			CraneSchDAO dao1	= new CraneSchDAO();
		    
		    logger.println(LogLevel.DEBUG,this,"=============보류재실적 처리 시작========");
		    /**
		     *	1.	공통 Coil정보를 가져온다.
		     */
			    stockV	= dao.getCoilCommonInfo(sGoodsNo);
			    
			    if(stockV == null){ 
			    	logger.println(LogLevel.DEBUG,this,"NO HAVE COMMON COIL DATA");
			    	throw new EJBServiceException("=보류재실적=>NO HAVE COMMON COIL DATA");
			    }
			    
	    	/**
		     *	2.	저장품Table에 정보를 등록,수정한다.
		     *		최초 발생시 등록, 재 실적발생시 수정
		     */
		     	String sStockMoveTerm = "";
		     	
		     	if(YmCommonConst.YD_GP_1.equals(sYardId)){
		     		sStockMoveTerm	= YmCommonConst.NEW_STOCK_MOVE_TERM_H1; // Coil 제품 입고완료
				}else{
					sStockMoveTerm	= YmCommonConst.NEW_STOCK_MOVE_TERM_HG; // Coil 제품 입고대기	
				}
				
				iSeq = dao.updateStockTransInfo_06(sGoodsNo,
												   YmCommonConst.ITEM_CG,
												   sStockMoveTerm);
			
			/*
			 *	3.	A열연은 보류장이 없다.
			 *		따라서, 보류재 실적처리시 출하로 제품입고 TC를 송신한다.
			 */
			if(YmCommonConst.YD_GP_1.equals(sYardId))
			{
				JDTORecord dmRc = dao1.getYMDM001Info(sGoodsNo);
			    
		    	if(dmRc != null){
		    		
					String sPut_Position= StringHelper.evl(dmRc.getFieldString("PUT_POSITION"), "");
					String sCURR_PROG_CD= StringHelper.evl(dmRc.getFieldString("CURR_PROG_CD"), "");
 
					//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                    //코일입고작업실적
					JDTORecord tcRecordDM = null;
					tcRecordDM = JDTORecordFactory.getInstance().create(); 
					tcRecordDM.setField("GOODS_NO",sGoodsNo);
					tcRecordDM.setField("YD_GP",sYardId);
					tcRecordDM.setField("STORE_LOC",sPut_Position);
					tcRecordDM.setField("CURR_PROG_CD",sCURR_PROG_CD);
					
					//인터페이스 전문 호출
					EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
					isSuccess = (Boolean)ejbConn.trx("getYDDMR001",new Class[]{JDTORecord.class},
					  	  	 new Object[]{tcRecordDM}); 
                    logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 코일입고작업실적.===");
                   //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                    
	
				}
			}
												   	
			logger.println(LogLevel.DEBUG,this,"=============보류재실적 처리 종료========");				            
			isVal = true; 
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }    
	     
	    return isVal;
	} 
	 
      /**
	 * 오퍼레이션명 : 
	 *  
	 * YJK
        * 반납 실적 처리
        *
        * param String	: 저장품ID
        *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */    
    public boolean setInnerIFCoilInfo_04(String sGoodsNo){
		
		boolean isVal = false;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			JDTORecord stockV 	= null;
			int iSeq 			= 0;
		    
			YdStockDAO dao	= new YdStockDAO();
		    
		    logger.println(LogLevel.DEBUG,this,"=============반납실적 처리 시작========");
		    /**
		     *	1.	공통 Coil정보를 가져온다.
		     */
			    stockV	= dao.getCoilCommonInfo(sGoodsNo);
			    
			    if(stockV == null){ 
			    	logger.println(LogLevel.DEBUG,this,"NO HAVE COMMON COIL DATA");
			    	throw new EJBServiceException("=반납실적=>NO HAVE COMMON COIL DATA");
			    }
			    
	    	/**
		     *	2.	저장품Table에 정보를 등록,수정한다.
		     *		최초 발생시 등록, 재 실적발생시 수정
		     */
	    		iSeq = dao.updateStockTransInfo(sGoodsNo,				      	  	  // 저장품ID
												YmCommonConst.NEW_STOCK_MOVE_TERM_JR);// Coil 제품 반납대기
				
			logger.println(LogLevel.DEBUG,this,"=============반납실적 처리 종료========");				            
			isVal = true; 
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }    
	     
	    return isVal;
	} 
	
      /**
	 * 오퍼레이션명 : 
	 *  
	 * YJK
        * SPM 모 Coil 종료
        *
        * param String	: 저장품ID
        * param String	: 야드구분
        *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
    public boolean setInnerIFCoilInfo_05(String sGoodsNo){
		
		boolean isVal = false;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			JDTORecord stockV 	= null;
			JDTORecord poV 		= null;
			
			int iSeq 			= 0;
		    
			YdStockDAO dao	= new YdStockDAO();
			
			String sStockStat	  = "";
			String sStockMoveTerm = "";
			String sMsg 		  = "";
			String sFinalPassProc = "";	// SPM2를 위한 변수. 최규성
			String sGbnWork		  = "";
			logger.println(LogLevel.DEBUG,this,"=============모 Coil 종료 시작========");    
		    /**
		     *	1.	공통 Coil정보를 가져온다.
		     */
			    stockV	= dao.getCoilCommonInfo(sGoodsNo);
			    
			    if(stockV == null){ 
			    	logger.println(LogLevel.DEBUG,this,"NO HAVE COMMON COIL DATA");
			    	throw new EJBServiceException("=모 Coil 종료=>NO HAVE COMMON COIL DATA");
			    }
			    
			    String sPlantGp     = StringHelper.evl(stockV.getFieldString("공장구분"),"");	
			    String sCurrprogCd  = StringHelper.evl(stockV.getFieldString("CURR_PROG_CD"),"");	
				//============================================================================================
			    sFinalPassProc = checkCoilCommonInfo(sGoodsNo);
			    
			    // SPM2 실적일 경우는 작업구분을 SPM2로 변경한다. 최규성
			    if(sFinalPassProc.equals("6K"))	{
			    	sGbnWork = "SPM2";
			    }else {
			    	sGbnWork = "SPM";
			    }
			    //============================================================================================
			    
				/**
				 * 2. Coil 입고실적 (출하로 제품입고실적 송신 YMDM001) 공통 진도 Code가 입고대기 H 이면 출하로
				 * 입고실적송신
				 */
			   	//************************************************************************************
			   	//************************************************************************************
		   		
					/*
					SELECT   A.COIL_NO AS GOODS_NO  
					        ,A.RECEIPT_DATE  
					        ,TO_CHAR(A.COIL_CREATE_DDTT,'hh24MISS') AS RECEIPT_TIME  
					        ,SUBSTR(B.STACK_COL_GP,1,1) AS YD_GP  
					        ,B.STACK_COL_GP||B.STACK_BED_GP||B.STACK_LAYER_GP AS STORE_LOC  
					 FROM USRPTA.TB_PT_HRPLATECOMM A
					    , USRYMA.TB_YM_STACKLAYER B
					WHERE A.COIL_NO =B.STOCK_ID(+)
					  AND A.PARENT_COIL_NO =?
					ORDER BY A.COIL_NO
					*/		   		
					String query1	="ym.steelinfo.steelinforecv.session.CoilInfoRegSBean.HRPlatecommlist";
					List StockList	= new YdStockDAO().getListData(query1, new Object[]{sGoodsNo});
					
					JDTORecord 	jrecrd = JDTORecordFactory.getInstance().create();
					JDTORecord 	jrecrd2 = JDTORecordFactory.getInstance().create();
					JDTORecord 	tcRecord1 = JDTORecordFactory.getInstance().create();	
					int 		totCnt  	=0 ;
					EAIHttpSender eaiHttpSender =null;				
					eaiHttpSender =new EAIHttpSender();
					eaiHttpSender.initService(EAIHttpSender.issnd);
					
					logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶임가공HRPlate절단 건 수: "+StockList.size());	
					
					//압연실적처리
					for (int i = 0; i < StockList.size(); i++) {
				   		jrecrd = (JDTORecord)StockList.get(i);
						
						//압연실적을 처리
						isVal = setInnerIFCoilInfo_HP( StringHelper.evl(jrecrd.getFieldString("GOODS_NO"), "") 
													 , StringHelper.evl(jrecrd.getFieldString("YD_GP"), "") 
													 , "");
				   		} //for end 
					
					//진도코드가 H(입고대기) 상태만 출하로 전송 함 20091006 JKJEUNG
					query1	="ym.steelinfo.steelinforecv.session.CoilInfoRegSBean.HRPlatecommlist2";
					List StockList2	= new YdStockDAO().getListData(query1, new Object[]{sGoodsNo});
					
					//출하 임가공 입고전문 전송 
			   		for (int i = 0; i < StockList2.size(); i++) {
				   		jrecrd2 = (JDTORecord)StockList2.get(i);
				   		
				   			
	//						GOODS_NO		제품 번호
	//						RECEIPT_DATE	입고 일자
	//						RECEIPT_TIME	입고 시각
	//						YD_GP			YARD 구분
	//						STORE_LOC		저장 위치
	//						PROD_ITEM_CODE	ITEMCODE
	
						tcRecord1.setField("TC_CODE", "YDDMR003");				
						tcRecord1.setField("TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
						tcRecord1.setField("GOODS_NO", StringHelper.evl(jrecrd2.getFieldString("GOODS_NO"), "").trim());
						tcRecord1.setField("RECEIPT_DATE", StringHelper.evl(jrecrd2.getFieldString("RECEIPT_DATE"), ""));
						tcRecord1.setField("RECEIPT_TIME", StringHelper.evl(jrecrd2.getFieldString("RECEIPT_TIME"), ""));
						tcRecord1.setField("YD_GP", StringHelper.evl(jrecrd2.getFieldString("YD_GP"), ""));
						tcRecord1.setField("STORE_LOC", StringHelper.evl(jrecrd2.getFieldString("STORE_LOC"), ""));
						tcRecord1.setField("PROD_ITEM_CODE", "");
						
						//내부인터페이스 송신모듈 호출 
						//String returncode= eaiHttpSender.send(tcRecord1);
						
						//인터페이스 전문 호출
						EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
						isSuccess = (Boolean)ejbConn.trx("getYDDMR003",new Class[]{JDTORecord.class},
						  	  	 new Object[]{tcRecord1});
						
						logger.println(LogLevel.INFO, this,"==>>>eaiHttpSender 응답값: ["+isSuccess+"]" );			
						
				   			
				   		totCnt =totCnt+1 ;
			   		} //for end 
			   		
					logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶출하에 임가공입고작업실적(YDDMR003) 전송 건 수: "+ totCnt);
			   	//************************************************************************************
			   	//************************************************************************************
			     
			/**
		     *	3.	STOCK DATA DELETE
		     */    	
				iSeq = dao.updateStockDelYnInfo(sGoodsNo,"Y");
				
			/**
		     *	3.	SPM 야드 MAP 정보 셋팅
		     */
			String sYdGp		 = "";
			String sIStackColGp1 = "";
			String sIStackColGp2 = "";
			
			if(YmCommonConst.PLANT_GP_A.equals(sPlantGp)||
			   YmCommonConst.PLANT_GP_H.equals(sPlantGp)){
			   	 	sYdGp = "1";
			   	 	
			   	 	
			   	 	//=======================================================================================
					String sFinalPassProc2 = checkCoilCommonInfo(sGoodsNo);
					//=======================================================================================
				    
					if (sFinalPassProc2.equals("1K")) {
						sGbnWork = "SPM";
					} else if (sFinalPassProc2.equals("1Q")) {
						sGbnWork = "EQL";
					}

					if ("EQL".equals(sGbnWork)) {
						sIStackColGp1 = YmCommonConst.STACK_COL_GP_1EQE01;
					   	sIStackColGp2 = YmCommonConst.STACK_COL_GP_1FQE01;
					}else{						
					   	/**
						 * A설비 적치열 정보 셋팅
						 * 1DKE01	SPM입측컨베이어
						 * 1EKE01	SPM입측컨베이어
						 */
					   	sIStackColGp1 = YmCommonConst.SPM_COL_1DKE+YmCommonConst.STACK_BED_GP_01;
					   	sIStackColGp2 = YmCommonConst.SPM_COL_1EKE+YmCommonConst.STACK_BED_GP_01;
					}
			}else{
					sYdGp = "3";
					if( sGbnWork.equals("SPM2")){
						
						sIStackColGp1   = YmCommonConst.SPM_COL_3DKE+YmCommonConst.STACK_BED_GP_01;
						sIStackColGp2  = YmCommonConst.SPM_COL_3EKE+YmCommonConst.STACK_BED_GP_01;
					}else if(sGbnWork.equals("SPM")){
					/**
					 * B 설비 적치열 정보 셋팅
					 * 3CKE01	SPM입측컨베이어
					 * 3BKE01	SPM입측컨베이어
					 */
					sIStackColGp1 = YmCommonConst.SPM_COL_3CKE+YmCommonConst.STACK_BED_GP_01;
					sIStackColGp2 = YmCommonConst.SPM_COL_3BKE+YmCommonConst.STACK_BED_GP_01;
					}else{
						// Error 발생.
						logger.println(LogLevel.DEBUG,this,"=모Coil종료. SPM 통과공정 이상=> 잘못된 통과공정.");
						throw new EJBServiceException("=모Coil종료. SPM 통과공정 이상=> 잘못된 통과공정.");
					}
					logger.println(LogLevel.DEBUG,this,"=============SPM 위치 선정.========"+sIStackColGp1+","+sIStackColGp2);
			}
			
			/**
			 *	4.	SPM STOCK_ID 입측삭제
			 */
			iSeq = YmCommonDB.deleteConveyorInfo(sIStackColGp1,
				   						  		 sGoodsNo);
			iSeq = YmCommonDB.deleteConveyorInfo(sIStackColGp2,
		   						  				 sGoodsNo);
		   	
		   	/**
		   	 * 모 Coil 종료 처리 종료 발생시에 야드에 존재하는 
		   	 * 코일정보가 있으면 삭제한다.
		   	 */
		   	isVal = deleteCoilLocationInfo(sGoodsNo);
		   	
		   	logger.println(LogLevel.DEBUG,this,"=============모 Coil 종료========");
			logger.println(LogLevel.DEBUG,this,"sIStackColGp1	="+sIStackColGp1);
			logger.println(LogLevel.DEBUG,this,"sIStackColGp2	="+sIStackColGp2);
			logger.println(LogLevel.DEBUG,this,"sGoodsNo		="+sGoodsNo);
			logger.println(LogLevel.DEBUG,this,"sMsg			="+sMsg);			
			
			logger.println(LogLevel.DEBUG,this,"=============모 Coil 종료 처리 종료========");   
			
			if( sGbnWork.equals("SPM2")){
				iSeq = insertUpPutWrslRtData(sGoodsNo, sYdGp, "N");
			}else{
			iSeq = insertUpPutWrslRtData(sGoodsNo,
								  		 sYdGp);
			}
			isVal = true; 
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }    
	     
	    return isVal;
	} 
	
	/**
     * 모코일 종료시 정보를 실적 처리한다.
     *
     * @param String	: 저장품ID
     * @param String	: 야드구분
     *
     */ 
	private int insertUpPutWrslRtData(String sStockId,
									  String sYdGp)
	{	
		int iSeq = -1;
		
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			String scrane_sch_id		= "";    
            String scrane_stock_id		= "";
            String scrane_equip_gp		= "";    
            String scrane_sch_code		= "";   
            String scrane_up_loc		= ""; 
            String scrane_put_loc		= "";
            String scrane_up_func		= ""; 
            String scrane_put_func		= ""; 
            String scrane_register		= ""; 
            String scrane_modifier		= ""; 
            String scrane_yd_gp			= "";
            String scrane_work_duty		= "";
			String scrane_work_party	= "";	        
			String sch_wdemand_duty		= "";
		    String sch_wdemand_party	= "";
		    
			scrane_sch_id		= "000000000000000000";    
            scrane_stock_id		= sStockId.trim();
            scrane_equip_gp		= "";    
            scrane_sch_code		= "";   
            scrane_up_loc		= "모 종료"; 
            scrane_put_loc		= "모 종료";
            scrane_up_func		= YmCommonConst.CRANE_FUNC_N; 
            scrane_put_func		= YmCommonConst.CRANE_FUNC_N;
            scrane_register		= "SYSTEM"; 
            scrane_modifier		= "SYSTEM"; 
            scrane_yd_gp		= sYdGp;
            scrane_sch_code 	= YmCommonConst.NEW_SCH_WORK_KIND_CKLI;
        	
            scrane_equip_gp = sYdGp + "X" + YmCommonConst.EQUIP_KIND_CR + "00";
	        
	        scrane_work_duty		= YmCommonUtil.getWorkDuty();
			scrane_work_party		= YmCommonUtil.getWorkParty();				        
			sch_wdemand_duty		= YmCommonUtil.getWorkDuty();
		    sch_wdemand_party		= YmCommonUtil.getWorkParty();		
		     
		 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.insertCraneWrsltSanJuk";			
		 	iSeq  			  = ydWBookDAO.requestinsertData(queryCode, new Object[]{ 
																		scrane_sch_id,
															            scrane_stock_id,
															            scrane_equip_gp,
															            scrane_sch_code,
															            scrane_work_duty,
																		scrane_work_party,
																		sch_wdemand_duty,
																	    sch_wdemand_party,
															            scrane_up_loc,
															            scrane_put_loc,
															            scrane_up_loc,
															            scrane_up_func,
															            scrane_put_loc,
															            scrane_put_func,
															            scrane_register,
															            scrane_modifier,
															            scrane_yd_gp});	
			
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }    
	    return iSeq; 
	}
	
      /**
	 * 오퍼레이션명 : 
	 *  
	 * YJK
        * 자 Coil 실적을 처리
        *
        * param String	: 저장품ID
        * param String	: 야드구분
        *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */      
    public boolean setInnerIFCoilInfo_06(String sGoodsNo){
		
		boolean isVal = false;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			JDTORecord stockV 	= null;
			int iSeq 			= 0;
		    
			YdStockDAO dao	= new YdStockDAO();
		    
			String sMsg = "";
			String sBayGp = "";
			String sGbnWork = "";
		    logger.println(LogLevel.DEBUG,this,"=============자 Coil 실적 처리 시작========");
		    /**
		     *	1.	공통 Coil정보를 가져온다.
		     */
			    stockV	= dao.getCoilCommonInfo(sGoodsNo);
			    
			    if(stockV == null){ 
			    	logger.println(LogLevel.DEBUG,this,"NO HAVE COMMON COIL DATA");
			    	throw new EJBServiceException("=자 Coil 실적=>NO HAVE COMMON COIL DATA");
			    }
			    
			    String sPlantGp 	  = StringHelper.evl(stockV.getFieldString("공장구분"),"");
			    sBayGp 	 			  = StringHelper.evl(stockV.getFieldString("YD_BAY_GP"),"");

	    	/**
		     *	2.	저장품Table에 정보를 등록,수정한다.
		     *		최초 발생시 등록, 재 실적발생시 수정
		     */
		    	boolean isExist = dao.isExistPrimaryKey(sGoodsNo);
		    	
		    	if(isExist){
		    		logger.println(LogLevel.DEBUG,this,"EXIST STOCK TABLE COIL DATA");
					throw new EJBServiceException("=자 Coil 실적=>EXIST STOCK TABLE COIL DATA");
					
				}else{
					logger.println(LogLevel.DEBUG,this,"NOT EXIST STOCK TABLE COIL DATA");
					
					//코일공통 진도코드 Table 참조.
			     	String[] sStockInfo = YmCommonUtil.getCoilCurrProgCd(sGoodsNo,"");
				    String sProgCd   	= sStockInfo[0];
					String sStocMv   	= sStockInfo[1];
					
					String sStockItem	= "";
			    	if(YmCommonConst.NEW_STOCK_MOVE_TERM_HG.equals(sStocMv)){
			    		sStockItem	   = YmCommonConst.ITEM_CG;
			    	}else{
			    		sStockItem	   = YmCommonConst.ITEM_CM;
			    	}
			    	
			    	logger.println(LogLevel.DEBUG,this,"저장품 이동조건 = "+ sStocMv );
					
					if("".equals(sStocMv)){ 
				    	logger.println(LogLevel.DEBUG,this,"NO HAVE 코일 공통 진도코드 DATA");
				    	throw new EJBServiceException("=자 Coil 실적=>NO HAVE 코일 공통 진도코드 DATA");
				    }
				    	
				    iSeq = dao.insertStockTransInfo(sGoodsNo,		      // 저장품ID
				            						sStockItem,			  // 저장품품목(코일제품)
													sStocMv 			  // 저장품이동조건
													);  			      
				}
			
			/**
		     *	3.	야드Map에 저장품정보를 등록,수정한다.
		     */
		    String sOStackColGp  = "";
			
			if(YmCommonConst.PLANT_GP_A.equals(sPlantGp)||
			   YmCommonConst.PLANT_GP_H.equals(sPlantGp)){
				
				//=======================================================================================
				String sFinalPassProc2 = checkCoilCommonInfo(sGoodsNo);
				//=======================================================================================
			    
				if (sFinalPassProc2.equals("1K")) {
					sGbnWork = "SPM";
				} else if (sFinalPassProc2.equals("1Q")) {
					sGbnWork = "EQL";
				}

				if ("EQL".equals(sGbnWork)) {
					/**
					 * A설비 적치열 정보 셋팅 1EQE01 EQL입측컨베이어 1FQE01 EQL입측컨베이어 1FQD01 EQL출측컨베이어 1GQD01 EQL출측컨베이어
					 */

					if (sBayGp.equals("F")) {
						sOStackColGp = "1FQD" + YmCommonConst.STACK_BED_GP_01; // 한시적 사용
					} else {
						sOStackColGp = "1GQD" + YmCommonConst.STACK_BED_GP_01;
					}
					logger.println(LogLevel.DEBUG,this,"=============A열연 EQL 위치 선정.========"+sOStackColGp);
				} else {
					/**
					 * A설비 적치열 정보 셋팅 1EKD01 SPM출측컨베이어
					 */
					sOStackColGp = YmCommonConst.SPM_COL_1EKD + YmCommonConst.STACK_BED_GP_01;
					
					logger.println(LogLevel.DEBUG,this,"=============A열연 SPM 위치 선정.========"+sOStackColGp);
				}
			}else{
				
			    //=======================================================================================
			    String sFinalPassProc = checkCoilCommonInfo(sGoodsNo);
			    //=======================================================================================
			    
				// SPM2관련하여 코드 분기 추가. 최규성
				if(sFinalPassProc.equals("6K")) {
					sOStackColGp  = YmCommonConst.SPM_COL_3EKD+YmCommonConst.STACK_BED_GP_01;
				}else if(sFinalPassProc.equals("5K")){
					/**
					 * B 설비 적치열 정보 셋팅
					 * 3BKD01	SPM출측컨베이어
					 */
					sOStackColGp  = YmCommonConst.SPM_COL_3BKD+YmCommonConst.STACK_BED_GP_01;
				}else{
					// Error 발생.
					logger.println(LogLevel.DEBUG,this,"=자Coil실적. SPM 통과공정 이상=> 잘못된 통과공정. "+sFinalPassProc);
					throw new EJBServiceException("=자Coil실적. SPM 통과공정 이상=> 잘못된 통과공정."+sFinalPassProc);

				}
				logger.println(LogLevel.DEBUG,this,"=============B열연 SPM 위치 선정.========"+sOStackColGp);
			}
		
			iSeq = YmCommonDB.insertConveyorInfo(sOStackColGp,
		   						  				 sGoodsNo,
		   						  				 YmCommonConst.GBN_MIN);
		   	
		   	if(iSeq < 1){
		   		throw new EJBServiceException("=자 Coil 실적=>CONVEYOR CREATE FAIL");
		   	}		  				 
		   	logger.println(LogLevel.DEBUG,this,"=============자 Coil 실적 처리 중간검사========");
			logger.println(LogLevel.DEBUG,this,"sOStackColGp	="+sOStackColGp);
			logger.println(LogLevel.DEBUG,this,"sGoodsNo		="+sGoodsNo);
			logger.println(LogLevel.DEBUG,this,"iSeq			="+iSeq);				
			
			logger.println(LogLevel.DEBUG,this,"=============자 Coil 실적 처리 종료========");				            
			isVal = true; 
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }    
	     
	    return isVal;
	} 
	
      /**
	 * 오퍼레이션명 : 
	 *  
	 * YJK
        * COIL 정정실적 발생시에 
        * 야드MAP에 있는 코일 정보를 삭제한다.
        * 
        * param String	: 저장품ID
        * param String	: FROM LOC
        * param String	: TO LOC
        *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */      
	public boolean deleteCoilLocationInfo(String sStockId) {
		boolean isSuccess = false;
		
		CraneSchDAO dao	= new CraneSchDAO();
											
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			int iReq = 0;
			
			String sTmpStockId		= "";
			String sTmpStat			= "";
			
			String sWbookId = "";
	 		String sSchId   = "";
	 		
	 		JDTORecord stockV	= dao.getStockInfo(sStockId);
	 		
	 		/**
	 		 *	1.	작업예약 유무 체크
	 		 */
	 		if(stockV != null){
	 			sWbookId = StringHelper.evl(stockV.getFieldString("WBOOK_ID"),"");
	 		}
	 		logger.println(LogLevel.DEBUG, this, "정정실적=> 작업예약ID="+sWbookId);
	 		
		 	if(!"".equals(sWbookId)){
		 	
		 		/*
				 *	1.1	저장품 Table의 Wbook_id 항목을 Update
				 *		tb_ym_stock Table wbook_id : ''(empty)
				 */	 
			
				iReq = dao.updateStockWbookId(sStockId,""); 
		 		/**
		 		 *	2.	스케쥴정보 있으면 삭제
		 		 */
		 		JDTORecord schV	= dao.getSchInfoWithWbookId(sWbookId,sStockId);
			 	 		
	 	 		if(schV != null){
	 	 			/**
	 	 			 *	2.1	스케쥴 취소 모듈 CALL
	 	 			 */
	 	 			sSchId = StringHelper.evl(schV.getFieldString("SCH_ID"),""); 
	 	 			logger.println(LogLevel.DEBUG, this, "정정실적=> 스케쥴ID="+sSchId);
	 	 			
	 	 			EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
    				Boolean isTemp  = (Boolean)ejbConn.trx("cancelCoilSchInfo",
												new  Class[]{String.class},
												new Object[]{sSchId});
	 	 		}
	 	 		
		 		/**
		 		 *	3.	작업예약정보 있으면 삭제
		 		 */
		 		 int iSeq = dao.deleteWbookInfo(sWbookId);
		 		 logger.println(LogLevel.DEBUG, this, "정정실적=> 작업예약 삭제="+iSeq);
		 	}
		 	
			String sUpStackColGp    = "";
			String sUpStackBedGp    = "";
			String sUpStackLayerGp  = "";
			String sUpUsageCd 		= "";
			
			/**
	 		 *	4.	저장품의 MAP정보를 가져온다.
	 		 *		중복위치도 체크한다.
	 		 */
			/*
			SELECT * 
			FROM tb_ym_stacklayer
			WHERE stock_id = :stock_id
			*/
			List stockL	= dao.getStackLayerInfoWithStockId_03(sStockId);
			
			JDTORecord stackV = null;
			JDTORecord upRc   = null;
			 
			if(stockL != null)
			{	 
				for(int inx = 0; inx < stockL.size() ; inx++)
				{
				 	stackV = (JDTORecord)stockL.get(inx);
				 	
				 	sUpStackColGp   = StringHelper.evl(stackV.getFieldString("STACK_COL_GP"), "");
					sUpStackBedGp   = StringHelper.evl(stackV.getFieldString("STACK_BED_GP"), "");
					sUpStackLayerGp = StringHelper.evl(stackV.getFieldString("STACK_LAYER_GP"), "");
					
					sUpUsageCd 		= YmCommonUtil.getStackColInfoWithPk(sUpStackColGp);
					
			   		if(YmCommonConst.STACK_COL_GP_1BDC01.equals(sUpStackColGp)  ||// A열연 COIL 분기콘베이어
			   		   YmCommonConst.STACK_COL_GP_1CDC01.equals(sUpStackColGp)  ||// A열연 COIL 분기콘베이어	
			   		   YmCommonConst.STACK_COL_USAGE_CD_XX.equals(sUpUsageCd)	||// COIL 비상적치위치
				       YmCommonConst.STACK_COL_USAGE_CD_FE.equals(sUpUsageCd)	||// COIL HFL보급위치
					   YmCommonConst.STACK_COL_USAGE_CD_FI.equals(sUpUsageCd)	||// COIL HFLTAKEIN위치
					   YmCommonConst.STACK_COL_USAGE_CD_FD.equals(sUpUsageCd)	||// COIL HFL추출위치
					   YmCommonConst.STACK_COL_USAGE_CD_KE.equals(sUpUsageCd)	||// COIL SPM보급위치
					   YmCommonConst.STACK_COL_USAGE_CD_KI.equals(sUpUsageCd)	||// COIL SPMTAKEIN위치
					   YmCommonConst.STACK_COL_USAGE_CD_KD.equals(sUpUsageCd)	||
					   YmCommonConst.STACK_COL_USAGE_CD_QE.equals(sUpUsageCd)	||
					   YmCommonConst.STACK_COL_USAGE_CD_QD.equals(sUpUsageCd)					   
			   		){// COIL SPM추출위치
					   	
						int iSeq = YmCommonDB.deleteConveyorInfo(sUpStackColGp,
										  			 	 		 sStockId);
					    if(iSeq < 0){
							//throw new EJBServiceException("=권상실적=>CONVEYOR DELETE FAIL.");
							logger.println(LogLevel.DEBUG,this, "산적위치 수정=> 적치단 삭제 FAIL");
						}	
						
					}else{	
								   					   		
					 	/* 
						 * 적치단 UP위치 Clear
						 * tb_ym_stacklayer Table : stock_id 		 = ''(Empty)
						 * tb_ym_stacklayer Table : stack_layer_stat = 'E'(적치가능)
						 */	
				    	iReq = dao.updateCraneStackLayerStat(sUpStackColGp,
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
					    	iReq = YmCommonDB.setCoilUpperState_V(sUpStackColGp,
						    							 	   	  sUpStackBedGp,
						    							 	   	  sUpStackLayerGp);
			    		}	
			    	}
			    	
		    		logger.println(LogLevel.DEBUG, this, "정정실적=> FROM 위치 수정 = "+ iReq);	
					 	
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
	 * YJK
        * SPM 재작업
        *
        * param String	: 저장품ID
        * param String	: 야드구분
        *
        *	수신이 되면 대상재가 출측에 있다면 
	 *	입측 D5로 보내고 조업으로 보급완료실적을 송신
	 *     대상재가 입측에 있다면 그대로 두고 보급완료실적을 송신
        *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */       
    public boolean setInnerIFCoilInfo_07(String sGoodsNo,
    									 String sYardId){
		boolean isVal = false;
		 String sFinalPassProc2 = "";
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			int iSeq 			= 0;
		    
			CraneSchDAO dao	= new CraneSchDAO();
		        
		    logger.println(LogLevel.DEBUG,this,"=============SPM 재작업 처리 시작========");
		   

		    /**
		     *	1.	A,B열연 SPM 입,출측 셋팅
		     */
				String sIStackColGp   = "";
				String sOStackColGp1  = "";
				String sOStackColGp2  = "";
				
				if(YmCommonConst.YD_GP_1.equals(sYardId)){	
				   	 
			   	   	/**
					 * A설비 적치열 정보 셋팅
					 * 1DKE01	SPM입측컨베이어
					 * 1EKE01	SPM입측컨베이어
					 * 1EKD01	SPM출측컨베이어
					 * 1FKD01	SPM출측컨베이어 
					 */
				   	sIStackColGp   = YmCommonConst.SPM_COL_1EKE+YmCommonConst.STACK_BED_GP_01;
				   	sOStackColGp1  = YmCommonConst.SPM_COL_1EKD+YmCommonConst.STACK_BED_GP_01;
				   	sOStackColGp2  = YmCommonConst.SPM_COL_1FKD+YmCommonConst.STACK_BED_GP_01;
				   	
				   	
				   	
				   
				    sFinalPassProc2 = checkCoilCommonInfo(sGoodsNo);
				    
				    if( sFinalPassProc2.equals("1Q")){				   	
				    	isVal = setInnerIFCoilInfo_02( sGoodsNo,"SPM");
				    	 return isVal;
				    }
					
				}else{
				    //===============================================================================
				    // SPM2 관련 검사 코드 추가. 최규성.
				    String sFinalPassProc = "";
				    sFinalPassProc = checkCoilCommonInfo(sGoodsNo);

				   //===============================================================================
				    
					if( sFinalPassProc.equals("6K")){			// SPM2 관련 코드 추가함. 최규성
						logger.println(LogLevel.DEBUG,this,"=============SPM2 위치 선정.========");
						sIStackColGp   = YmCommonConst.SPM_COL_3DKE+YmCommonConst.STACK_BED_GP_01;
						sOStackColGp1  = YmCommonConst.SPM_COL_3EKD+YmCommonConst.STACK_BED_GP_01;
						sOStackColGp2  = YmCommonConst.SPM_COL_3EKD+YmCommonConst.STACK_BED_GP_01;
					
					}else if (sFinalPassProc.equals("5K")){
						/**
						 * B 설비 적치열 정보 셋팅
						 * 3CKE01	SPM입측컨베이어
						 * 3BKE01	SPM입측컨베이어
						 * 3BKD01	SPM출측컨베이어
						 * 3AKD01	SPM출측컨베이어 
						 */
						sIStackColGp   = YmCommonConst.SPM_COL_3BKE+YmCommonConst.STACK_BED_GP_01;
						sOStackColGp1  = YmCommonConst.SPM_COL_3BKD+YmCommonConst.STACK_BED_GP_01;
						sOStackColGp2  = YmCommonConst.SPM_COL_3AKD+YmCommonConst.STACK_BED_GP_01;
					}else{
						
						// Error 발생.
						logger.println(LogLevel.DEBUG,this,"=SPM재작업. SPM 통과공정 이상=> 잘못된 통과공정."+sFinalPassProc);
						throw new EJBServiceException("=SPM재작업. SPM 통과공정 이상=> 잘못된 통과공정."+ sFinalPassProc);
					}
				}
				logger.println(LogLevel.DEBUG,this,"sOStackColGp1	="+sOStackColGp1);
				logger.println(LogLevel.DEBUG,this,"sOStackColGp2	="+sOStackColGp2);
				logger.println(LogLevel.DEBUG,this,"sIStackColGp	="+sIStackColGp);
				logger.println(LogLevel.DEBUG,this,"sGoodsNo		="+sGoodsNo);
			/**
			 *	2.	출측에 저장품이 존재하는지 체크
			 */
				JDTORecord curBedV = null;
				
				curBedV = dao.getStackLayerInfoWithStockId(sOStackColGp1,sGoodsNo);
				if(curBedV == null){
					curBedV = dao.getStackLayerInfoWithStockId(sOStackColGp2,sGoodsNo);
					
					//추출 존 E,F동에도 존재를 안하는 경우
					if(curBedV == null){										   	
				    	isVal = setInnerIFCoilInfo_02( sGoodsNo,"SPM");
				    	 return isVal;					  
					}
				}
				logger.println(LogLevel.DEBUG,this,"출측 저장품 정보 확인="+curBedV);		
			/**
			 *	3.	출측에 저장품이 있으면
			 *		출측정보 삭제 후,입측정보 생성.
			 */ 
				if(curBedV != null){
					
					iSeq = YmCommonDB.deleteConveyorInfo(sOStackColGp1,
					   						  			 sGoodsNo);
					if(iSeq < 1){
					iSeq = YmCommonDB.deleteConveyorInfo(sOStackColGp2,
					   						  			 sGoodsNo);   			
					}   		
					if(iSeq > 0){				  			 
					iSeq = YmCommonDB.insertConveyorInfo(sIStackColGp, 
				   						  				 sGoodsNo,
				   						  				 YmCommonConst.GBN_MAX);   	
				   	}					  				 
					logger.println(LogLevel.DEBUG,this,"출측 삭제, 입측 추가");	
				}	   				
							  				 				  			 			  			 
			/**
			 *	4.	보급완료 실적을 송신
			 */  
				if(curBedV != null){
						
					YMPO161 model = new YMPO161();
					model.setTcCode(YmCommonConst.MODEL_YMPO161);
					model.setTcDate(YmCommonUtil.getCurDate("yyyy-MM-dd"));
					model.setTcTime(YmCommonUtil.getCurDate("HH-mm-ss"));
					
					/* 권하일자	CHAR(8)  yyyymmdd	*/
					model.setdownDate(YmCommonUtil.getCurDate("yyyyMMdd"));
					
					/* 권하시각     CHAR(6)  HHMMSS */
					model.setdownTime(YmCommonUtil.getCurDate("HHmmss"));
					
					/* 공장구분	CHAR(1)  A:A열연, B:B열연*/
					model.setplantGbn(YmCommonConst.YD_GP_1.equals(sYardId)?"A":"B");
					
					/* 공정구분	CHAR(1)  H : Hot Filnal, S :  SkinPass	*/
					model.setprocGbn("S");
					
					/* COIL번호	CHAR(11) */
					model.setcoilNo(sGoodsNo);
					
					/* 처리구분     CHAR(1)  1:보급,2:보급취소,3:추출,4:Take-Out,5:Take-In */
					model.setProcessId("5");
					  
					/* 위치포지션  CHAR(2)  */
					model.setpositionNo(YmCommonConst.PO_POSITION_D5);
					
					EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
					Boolean isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new  Class[]{CommonModel.class},
																  	  	         new Object[]{model});
					logger.println(LogLevel.DEBUG,this, "내부IF호출===SPM 재작업 실적송신.===");
					
					//품질 열연정정입측보급실적----------------------------------------------
					YdDelegate      ydDelegate      = new YdDelegate();
					JDTORecord recInTemp	=null;
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("MSG_ID",      	"YDQMJ002"); 							 
					recInTemp.setField("STL_NO",     	sGoodsNo);	  			//재료번호				
					ydDelegate.sendMsg(recInTemp);
			 
					logger.println(LogLevel.DEBUG,this, "품질 L3 열연정정입측보급실적 전송 송신 완료7"); 
					//-------------------------------------------------------------------
				}
			
			logger.println(LogLevel.DEBUG,this,"=============SPM 재작업 처리 종료========");				            
			isVal = true; 
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }    
	     
	    return isVal;
	} 
    
    
    
    /**
	 * 오퍼레이션명 : 
	 *  
	 * YJK
        * SPM 재작업
        *
        * param String	: 저장품ID
        * param String	: 야드구분
        *
        *	수신이 되면 대상재가 출측에 있다면 
	 *	입측 D5로 보내고 조업으로 보급완료실적을 송신
	 *     대상재가 입측에 있다면 그대로 두고 보급완료실적을 송신
        *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */       
    public boolean setInnerIFCoilInfoEQL_07(String sGoodsNo,
    									 	String sYardId){
		boolean isVal = false;
		 String sFinalPassProc2 = "";
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			int iSeq 			= 0;
		    
			CraneSchDAO dao	= new CraneSchDAO();
		        
		    logger.println(LogLevel.DEBUG,this,"=============EQL 재작업 처리 시작========");
		   

		    /**
		     *	1.	A,B열연 EQL 입,출측 셋팅
		     */
				String sIStackColGp   = "";
				String sOStackColGp1  = "";
				String sOStackColGp2  = "";
				
				if(YmCommonConst.YD_GP_1.equals(sYardId)){	
				   	 
			   	   	/**
					 * A설비 적치열 정보 셋팅
					 * 1EQE01	EQL입측컨베이어
					 * 1FQE01	EQL입측컨베이어
					 * 1FQD01	EQL출측컨베이어
					 * 1GQD01	EQL출측컨베이어 
					 */
				   	sIStackColGp   = YmCommonConst.EQL_COL_1FQE+YmCommonConst.STACK_BED_GP_01;
				   	sOStackColGp1  = YmCommonConst.EQL_COL_1FQD+YmCommonConst.STACK_BED_GP_01;
				   	sOStackColGp2  = YmCommonConst.EQL_COL_1GQD+YmCommonConst.STACK_BED_GP_01;
				   	

					
				} 
				logger.println(LogLevel.DEBUG,this,"sOStackColGp1	="+sOStackColGp1);
				logger.println(LogLevel.DEBUG,this,"sOStackColGp2	="+sOStackColGp2);
				logger.println(LogLevel.DEBUG,this,"sIStackColGp	="+sIStackColGp);
				logger.println(LogLevel.DEBUG,this,"sGoodsNo		="+sGoodsNo);
			/**
			 *	2.	출측에 저장품이 존재하는지 체크
			 */
				JDTORecord curBedV = null;
				
				curBedV = dao.getStackLayerInfoWithStockId(sOStackColGp1,sGoodsNo);
				if(curBedV == null){
					curBedV = dao.getStackLayerInfoWithStockId(sOStackColGp2,sGoodsNo);
					
					//추출 존 F동에도 존재를 안하는 경우
			 										   	
				    	isVal = setInnerIFCoilInfo_02( sGoodsNo,"EQLJ");
				    	 return isVal;					  
				 
				}
				logger.println(LogLevel.DEBUG,this,"출측 저장품 정보 확인="+curBedV);		
			/**
			 *	3.	출측에 저장품이 있으면
			 *		출측정보 삭제 후,입측정보 생성.
			 */ 
				if(curBedV != null){
					
					iSeq = YmCommonDB.deleteConveyorInfo(sOStackColGp1,
					   						  			 sGoodsNo);
					if(iSeq < 1){
					iSeq = YmCommonDB.deleteConveyorInfo(sOStackColGp2,
					   						  			 sGoodsNo);   			
					}   		
					if(iSeq > 0){				  			 
					iSeq = YmCommonDB.insertConveyorInfo(sIStackColGp, 
				   						  				 sGoodsNo,
				   						  				 YmCommonConst.GBN_MAX);   	
				   	}					  				 
					logger.println(LogLevel.DEBUG,this,"출측 삭제, 입측 추가");	
				}	   				
	 
			
			logger.println(LogLevel.DEBUG,this,"=============EQL 재작업 처리 종료========");				            
			isVal = true; 
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }    
	     
	    return isVal;
	} 
	
      /**
	 * 오퍼레이션명 : 
	 *  
	 * YJK
        * 요구차 공정 변경
        *
        * param String	: 저장품ID
        *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */        
    public boolean setInnerIFCoilInfo_11(String sGoodsNo,
    									 String sYardID ,
    									 String sCoilProc){
		
		boolean isVal = false;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			JDTORecord stockV 	= null;
			int iSeq 			= 0;
		    
			YdStockDAO dao		= new YdStockDAO();
		    CraneSchDAO dao1	= new CraneSchDAO();
		    
		    logger.println(LogLevel.DEBUG,this,"=============요구공정 변경 처리 시작========");
		    /**
		     *	1.	저장품의 MAP정보를 가져온다.
		     */
		    List stockL	= dao1.getStackLayerInfoWithStockId_03(sGoodsNo);
			
			if(stockL.size() == 0){ 
				
		    	logger.println(LogLevel.DEBUG,this,"NO HAVE COIL DATA");
		    	return false;
		    }
		    
			JDTORecord stackV = null;
			JDTORecord upRc   = null;
			
			String sStackColGp   	= ""; 
			String sLayerStat	 	= ""; 
			String sUsageCd	 		= ""; 
			String sStockMoveTerm 	= "";
			
			if(stockL != null)
			{	 
				for(int inx = 0; inx < stockL.size() ; inx++)
				{
				 	stackV = (JDTORecord)stockL.get(inx);
				 	
				 	sStackColGp   = StringHelper.evl(stackV.getFieldString("STACK_COL_GP"), "");
					sLayerStat    = StringHelper.evl(stackV.getFieldString("STACK_LAYER_STAT"), "");
					
					sUsageCd 	  = YmCommonUtil.getStackColInfoWithPk(sStackColGp);
					
			   		if(YmCommonConst.STACK_COL_USAGE_CD_CC.equals(sUsageCd)	||// COIL 분기콘베이어
					   YmCommonConst.STACK_COL_USAGE_CD_CE.equals(sUsageCd)	){// COIL 확장콘베이어
					   	
						if(YmCommonConst.STACK_LAYER_STAT_S.equals(sLayerStat)||
				    	   YmCommonConst.STACK_LAYER_STAT_L.equals(sLayerStat)||
				    	   YmCommonConst.STACK_LAYER_STAT_U.equals(sLayerStat)){ 	
				    		
				    		/**
						     *	2.	저장품Table에 정보를 등록,수정한다.
						     *		최초 발생시 등록, 재 실적발생시 수정
						     */
						     	if(YmCommonConst.SHEAR_SUPPLY_GP_5K.equals(sCoilProc)){		 //B열연 SPM
											
									sStockMoveTerm = YmCommonConst.NEW_STOCK_MOVE_TERM_A2;		//SPM 추출
								}else if(YmCommonConst.SHEAR_SUPPLY_GP_5H.equals(sCoilProc)){//B열연 HFL
								
									sStockMoveTerm = YmCommonConst.NEW_STOCK_MOVE_TERM_A1;		//HFL 추출
								}else if(YmCommonConst.SHEAR_SUPPLY_GP_5T.equals(sCoilProc)){//B열연 수냉재
								
									sStockMoveTerm = YmCommonConst.NEW_STOCK_MOVE_TERM_A3;		//수냉재 추출
								}else if(YmCommonConst.SHEAR_SUPPLY_GP_5A.equals(sCoilProc)){//B열연 공냉재
								
									sStockMoveTerm = YmCommonConst.NEW_STOCK_MOVE_TERM_A4;		//공냉재 추출
								}else if(YmCommonConst.SHEAR_SUPPLY_GP_6K.equals(sCoilProc)){// B열연 SPM2 최규성 
									sStockMoveTerm = YmCommonConst.NEW_STOCK_MOVE_TERM_A6;		// SPM2 추출
								}
					    		iSeq = dao1.updateStockTransInfo(sGoodsNo,		
																sStockMoveTerm);
				    	}
					}
				} 
			} 
			
			logger.println(LogLevel.DEBUG,this,"=============요구공정 변경 처리 종료========");				            
			isVal = true; 
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }    
	     
	    return isVal;
	} 
	
      /**
	 * 오퍼레이션명 : 
	 *  
	 * YJK
        * A열연 분기콘베이어 TAKE IN
        *
        * param String	: 저장품ID
        * param String	: 적치열
        * param String	: 적치BED
        *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */        
    public boolean callAConveyorTakeIn(String sGoodsNo,
    							  	   String sStackColGp,
    								   String sStackBedGp){
		
		boolean isVal = false;
		
		try{
			
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			JDTORecord stockV 	= null;
			JDTORecord layerV 	= null;
			int iSeq 			= 0;
		    
			YdStockDAO dao		= new YdStockDAO();
			CraneSchDAO dao1	= new CraneSchDAO();
		    
			String sMsg = "";
			String sStockMoveTerm = "";
			    
		    logger.println(LogLevel.DEBUG,this,"=============A열연 분기콘베이어 TAKE IN 처리 시작========");
		    /**
		     *	1.	공통 Coil정보를 가져온다.
		     */
			    stockV	= dao.getCoilCommonInfo(sGoodsNo);
			    
			    if(stockV == null){ 
			    	logger.println(LogLevel.DEBUG,this,"NO HAVE COMMON COIL DATA");
			   		throw new EJBServiceException("=A열연 분기콘베이어 TAKE IN=>NO HAVE COMMON COIL DATA");
			    }else{
			    	String sCoilProc    = "";
			    	String sNextProc 	= StringHelper.evl(stockV.getFieldString("차공정"),"").trim();
					String sPlanProc 	= StringHelper.evl(stockV.getFieldString("계획공정"),"").trim();
					
					if("".equals(sNextProc)){
						sCoilProc = sPlanProc;
					}else{
						sCoilProc = sNextProc;
					}
						
					if(YmCommonConst.SHEAR_SUPPLY_GP_1K.equals(sCoilProc)){		 //A열연 SPM
					
						sStockMoveTerm = YmCommonConst.NEW_STOCK_MOVE_TERM_A2;	//SPM 추출
					}else if(YmCommonConst.SHEAR_SUPPLY_GP_1H.equals(sCoilProc)){//A열연 HFL
					
						sStockMoveTerm = YmCommonConst.NEW_STOCK_MOVE_TERM_A1;		//HFL 추출
					}
				}
				logger.println(LogLevel.DEBUG,this,"저장품 이동조건 ="+sStockMoveTerm+"=");

	    	/**
		     *	2.	STOCK 현재위치 정보 CLEAR한다.
		     */
		     	layerV	=	dao1.getStackLayerInfoWithStockId_02(sGoodsNo);
		    	
		    	if(layerV == null){
		    		throw new EJBServiceException("=A열연 분기콘베이어 TAKE IN=>FROM 위치 존재하지 않음");
		    	}
		    	
		    	String sNowStackColGp 	= StringHelper.evl(layerV.getFieldString("STACK_COL_GP"), "");
		     	String sNowStackBedGp 	= StringHelper.evl(layerV.getFieldString("STACK_BED_GP"), "");
		     	String sNowStackLayerGp = StringHelper.evl(layerV.getFieldString("STACK_LAYER_GP"), "");
			    String sTmpStat 		= StringHelper.evl(layerV.getFieldString("STACK_LAYER_STAT"), "");
			    	
				if(YmCommonConst.STACK_LAYER_STAT_S.equals(sTmpStat)||
		    	   YmCommonConst.STACK_LAYER_STAT_U.equals(sTmpStat)||
		    	   YmCommonConst.STACK_LAYER_STAT_P.equals(sTmpStat)){ 	
					
					throw new EJBServiceException("=A열연 분기콘베이어 TAKE IN=>스케쥴 수행예정 저장품="+sTmpStat);
				}
			
				/* 
				 * 적치단 UP위치 Clear
				 * tb_ym_stacklayer Table : stock_id = ''(Empty)
				 * tb_ym_stacklayer Table : stack_layer_stat	   = 'E'(적치가능)
				 */	
		    	int iReq = dao1.updateCraneStackLayerStat(sNowStackColGp,
		    										  	  sNowStackBedGp,
		    										  	  sNowStackLayerGp,
			    										  "",
			    										  YmCommonConst.STACK_LAYER_STAT_E);
			        
				//	FROM 위치 상단 적치상태 수정
			 	if(YmCommonConst.STACK_LAYER_GP_01.equals(sNowStackLayerGp)){
			    	
			    	/*
			    	 * A.B열연 Coil 권상실적	
			    	 * 상단 왼쪽 상태정보를 UPDATE
			    	 * 상단 오른쪽 상태정보를 UPDATE
			    	 */	
			    	iReq = YmCommonDB.setCoilUpperState_V(sNowStackColGp,
				    							 	   	  sNowStackBedGp,
				    							 	   	  sNowStackLayerGp);
	    		}		
	    		
		    /**
		     *	3.	저장품Table에 정보를 등록,수정한다.
		     *		최초 발생시 등록, 재 실적발생시 수정
		     */
		    	boolean isExist = dao.isExistPrimaryKey(sGoodsNo);
		    	
		    	if(isExist){
		    		logger.println(LogLevel.DEBUG,this,"EXIST STOCK TABLE COIL DATA");
					
					iSeq = dao.updateStockTransInfo_06(sGoodsNo,					     // 저장품ID
													   YmCommonConst.ITEM_CM,		     // 저장품품목(코일소재)
													   sStockMoveTerm					 // 저장품이동조건
													   );  			      
				}else{
					logger.println(LogLevel.DEBUG,this,"NOT EXIST STOCK TABLE COIL DATA");
					
					iSeq = dao.insertStockTransInfo(sGoodsNo,					      // 저장품ID
													YmCommonConst.ITEM_CM,		      // 저장품품목(코일소재)
													sStockMoveTerm					  // 저장품이동조건	
													);  			      
				} 
			/**
		     *	4.	야드Map에 저장품정보를 등록,수정한다.
		     */
 			  	//	분기콘베이어 정보 생성
				iSeq = YmCommonDB.insertConveyorInfo(sStackColGp,
													 sGoodsNo,
													 sStackBedGp);
				
				if(iSeq < 1){
					throw new EJBServiceException("=A열연 분기콘베이어 TAKE IN=>콘베이어 정보를 생성하지 못했습니다.");
				}	
				
				logger.println(LogLevel.DEBUG,this,"=============Conveyor Create========");
				logger.println(LogLevel.DEBUG,this,"STACK_COL_GP    ="+sStackColGp);
				logger.println(LogLevel.DEBUG,this,"sGoodsNo		="+sGoodsNo);
				logger.println(LogLevel.DEBUG,this,"iSeq			="+iSeq);		
				
			   
		   /**
			*	5.	A열연 = 실적등록시에 작업예약을 생성한다.
			*/
				//	작업예약을 호출한다.
				isVal = callCoilWbookInfo(sGoodsNo,
		   	 		 				 	  sStockMoveTerm);						  				
				
			/**
		     *	6.	TAKE IN 전문을 송신한다.
		     */	
		     	isVal = callCoilMsgInfo(stockV,
							            sGoodsNo,
							            YmCommonConst.RESULT_MODE_0);
			
			/**
		     *	7.	코일공통 위치 수정
		     */		
				iSeq = dao1.updateCoilCommonLocInfo(sGoodsNo,sStackColGp +
															 sStackBedGp +
															 YmCommonConst.STACK_LAYER_GP_01);  	   
				
			logger.println(LogLevel.DEBUG,this,"=============A열연 분기콘베이어 TAKE IN 처리 종료========");		            
			isVal = true; 
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }    
	     
	    return isVal;
	} 
	
      /**
	 * 오퍼레이션명 : 
	 *  
	 * YJK
        * A열연 분기콘베이어 TAKE OUT
        *
        * param String	: 저장품ID
        * param String	: PUT 위치
        *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */         
    public boolean callAConveyorTakeOut(String sGoodsNo,
    							  	    String sPutLoc){
		
		boolean isVal = false;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			JDTORecord layerV 		= null;
			JDTORecord comStockV 	= null;
			int iSeq 			= 0;
		    
			CraneSchDAO dao	= new CraneSchDAO();
		    
			String sMsg = "";
			String sStockMoveTerm = "";
			    
		    logger.println(LogLevel.DEBUG,this,"=============A열연 분기콘베이어 TAKE OUT 처리 시작========");
		     /**
		     *	1.	공통 Coil정보를 가져온다.
		     */
		    	comStockV	= dao.getCoilCommonInfo(sGoodsNo);
			    
			    if(comStockV == null){ 
			    	logger.println(LogLevel.DEBUG,this,"NO HAVE COMMON COIL DATA");
			   		throw new EJBServiceException("=A열연 분기콘베이어 TAKE OUT=>NO HAVE COMMON COIL DATA");
			    }
			    
		    /**
		     *	2.	STOCK 현재위치 정보 CLEAR한다.
		     */
		     	layerV	=	dao.getStackLayerInfoWithStockId_02(sGoodsNo);
		    	
		    	if(layerV == null){
		    		throw new EJBServiceException("=A열연 분기콘베이어 TAKE OUT=>FROM 위치 존재하지 않음");
		    	}
		    	
		    	String sNowStackColGp 	= StringHelper.evl(layerV.getFieldString("STACK_COL_GP"), "");
		     	String sNowStackBedGp 	= StringHelper.evl(layerV.getFieldString("STACK_BED_GP"), "");
		     	String sNowStackLayerGp = StringHelper.evl(layerV.getFieldString("STACK_LAYER_GP"), "");
			    String sTmpStat 		= StringHelper.evl(layerV.getFieldString("STACK_LAYER_STAT"), "");
			    	
				if(YmCommonConst.STACK_LAYER_STAT_U.equals(sTmpStat)){ 	
					
					throw new EJBServiceException("=A열연 분기콘베이어 TAKE OUT=>스케쥴 수행예정 저장품="+sTmpStat);
				}
			/**
		     *	3.	야드Map에 저장품정보를 등록,수정한다.
		     */
 			  	//	분기콘베이어 정보 삭제
 			  	
					iSeq = YmCommonDB.deleteConveyorInfo(YmCommonConst.STACK_COL_GP_1CDC01,
											  			 sGoodsNo);
			    if(iSeq < 1){
			    	
				    iSeq = YmCommonDB.deleteConveyorInfo(YmCommonConst.STACK_COL_GP_1BDC01,
											  			 sGoodsNo);
				}	
								  			 
			    if(iSeq < 0){
					throw new EJBServiceException("=A열연 분기콘베이어 TAKE OUT=>콘베이어 정보가 존재하지 않습니다.");
				}	
				
				logger.println(LogLevel.DEBUG,this,"=============Conveyor Delete========");
				logger.println(LogLevel.DEBUG,this,"iSeq			="+iSeq);		
				
			/**
		     *	4.	PUT 위치에 저장품정보를 등록한다.
		     */	
				if(sPutLoc.length() != 10){
					throw new EJBServiceException("=A열연 분기콘베이어 TAKE OUT=>PUT 위치의 자리수가 맞지 않습니다.");	
		    	}
				
				String sPutStackColGp   = sPutLoc.substring(0, 6);
				String sPutStackBedGp   = sPutLoc.substring(6, 8);
				String sPutStackLayerGp = sPutLoc.substring(8,10);
				
				JDTORecord putJr = dao.getStackLayerInfoWithPk(sPutStackColGp,
					    									   sPutStackBedGp,
					    									   sPutStackLayerGp);
				if(putJr == null){
					throw new EJBServiceException("=A열연 분기콘베이어 TAKE OUT=>TO 위치가 존재하지 않습니다.");
				}
				
				if(!"".equals(StringHelper.evl(putJr.getFieldString("STOCK_ID"), ""))){ 	
					throw new EJBServiceException("=A열연 분기콘베이어 TAKE OUT=>TO 위치에 저장품 정보 존재함");
				}
				
				/* 
				 * 적치단 Put위치를 적치상태로 변경
				 * tb_ym_stacklayer Table : stock_id = Coil No
				 * tb_ym_stacklayer Table : stack_layer_stat	   = 'L'(적치중)
				 */	
		    	iSeq = dao.updateCraneStackLayerStat(sPutStackColGp,
		    										 sPutStackBedGp,
		    										 sPutStackLayerGp,
		    										 sGoodsNo,
		    										 YmCommonConst.STACK_LAYER_STAT_L);
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
	    		
		   /**
			*	5.	A열연 = 작업예약을 취소한다.
			*/
									  				
				JDTORecord stockV = dao.getStockInfo(sGoodsNo);
				
				if(stockV != null){
				
					iSeq = dao.deleteWbookInfo(StringHelper.evl(stockV.getFieldString("WBOOK_ID"), ""));  	 
				}
				
				iSeq = dao.updateStockWbookId(sGoodsNo,""); 
				
				iSeq = dao.updateCoilCommonLocInfo(sGoodsNo,sPutLoc);  	   
			
			/**
		     *	6.	TAKE OUT 전문을 송신한다.
		     */	
		     	isVal = callCoilMsgInfo(comStockV,
							            sGoodsNo,
							            YmCommonConst.RESULT_MODE_1);
							            		
			logger.println(LogLevel.DEBUG,this,"=============A열연 분기콘베이어 TAKE OUT 처리 종료========");		            
			isVal = true; 
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }    
	     
	    return isVal;
	} 
	
	/**
     * 압연실적 송신전문MESSAGE를 구성한다.
     *
     * @param JDTORecord : 공통코일정보
     * @param String     : 저장품ID
     * @param String     : 작업구분("" - 정상, 0 - take in, 1 - take out)
     * 
     * @return
     * @throws  
     */	
	private boolean callCoilMsgInfo(JDTORecord stockV,
							        String sGoodsNo,
							        String sGbn){
		
		Boolean isSuccess = new Boolean(false);
		CraneSchDAO dao	= new CraneSchDAO();
		String sMessage   = "";
		String sDEMANDER_NM = "";
		String sHRMILL_CMPL_DT = "";
		String sNEXT_PROC = "";
		try{
			
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String sPlantGp		= StringHelper.evl(stockV.getFieldString("공장구분"),"");		
	    	String sOrdNo		= StringHelper.evl(stockV.getFieldString("제작번호"),"");		
	    	String sOrdDtl		= StringHelper.evl(stockV.getFieldString("제작행번"),"");		
	    	String sCoilT		= StringHelper.evl(stockV.getFieldString("코일두께"),"");		
	    	String sCoilW		= StringHelper.evl(stockV.getFieldString("코일폭"),"");		
	    	String sCoilLen		= StringHelper.evl(stockV.getFieldString("코일길이"),"");		
	    	String sCoilOutdia	= StringHelper.evl(stockV.getFieldString("코일외경"),"");		
	    	String sCoilWt		= StringHelper.evl(stockV.getFieldString("코일중량"),"");		
    		String sBranchCd 	= StringHelper.evl(stockV.getFieldString("분기위치코드"),"");
    		String sExBranchCd	= StringHelper.evl(stockV.getFieldString("확장분기위치코드"),"");
			String sCoolMethod 	= StringHelper.evl(stockV.getFieldString("냉각방법"),"");
						
			logger.println(LogLevel.DEBUG,this,"sPlantGp ="	+sPlantGp);	   	
			logger.println(LogLevel.DEBUG,this,"sOrdNo ="	+sOrdNo);	   	
			logger.println(LogLevel.DEBUG,this,"sOrdDtl ="	+sOrdDtl);	   	
			logger.println(LogLevel.DEBUG,this,"sCoilT ="	+sCoilT);	   	
			logger.println(LogLevel.DEBUG,this,"sCoilW ="	+sCoilW);	   	
			logger.println(LogLevel.DEBUG,this,"sCoilLen ="	+sCoilLen);	   	
			logger.println(LogLevel.DEBUG,this,"sCoilOutdia="+sCoilOutdia);	   	
			logger.println(LogLevel.DEBUG,this,"sCoilWt ="	+sCoilWt);	   	
			logger.println(LogLevel.DEBUG,this,"sBranchCd ="	+sBranchCd);	   	
			logger.println(LogLevel.DEBUG,this,"sExBranchCd ="	+sExBranchCd);	   	
			logger.println(LogLevel.DEBUG,this,"sCoolMethod ="	+sCoolMethod);	   	
						
			if(YmCommonConst.PLANT_GP_A.equals(sPlantGp)||
			   YmCommonConst.PLANT_GP_H.equals(sPlantGp)){//A열연 압연실적 
			
				sMessage = setACoilMsgInfo(sGoodsNo,
										   sOrdNo,
										   sOrdDtl,
										   sCoilT,
										   sCoilW,
										   sCoilLen,
										   sCoilOutdia,
										   sCoilWt,
										   sGbn);
				
				EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
				isSuccess = (Boolean)ejbConn.trx("THHC171send",new Class[]{String.class},new Object[]{ sMessage });	
				
			}else{//B열연 압연실적
				
				
				JDTORecord putJr = dao.getCoilInfo(sGoodsNo);
				
				if(putJr != null){
					sDEMANDER_NM = StringHelper.evl(putJr.getFieldString("DEMANDER_NM"), "");
					sHRMILL_CMPL_DT = StringHelper.evl(putJr.getFieldString("HRMILL_CMPL_DT"), "");
					sNEXT_PROC = StringHelper.evl(putJr.getFieldString("NEXT_PROC"), "");
				} 
				
				sMessage = setBCoilMsgInfo(sGoodsNo,
										   sOrdNo,
										   sOrdDtl,
										   sCoilT,
										   sCoilW,
										   sCoilLen,
										   sCoilOutdia,
										   sCoilWt,
										   sBranchCd,
										   sExBranchCd,
										   sCoolMethod,
										   sDEMANDER_NM,
										   sHRMILL_CMPL_DT,
										   sNEXT_PROC
											);
			
				EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
				isSuccess = (Boolean)ejbConn.trx("CN1BP02send",new Class[]{String.class},new Object[]{ sMessage });	
			}
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue(); 
	}
	
	/**
	 *  A열연 실적송신 Interface
	 *  Source	야드L3	Target	야드L2	I/F방법	JMS	I/F주기	REQ	I/F유형	
	 *  T/C	THHC171	
	 *  1	전문코드		CHAR	07	전문코드    	
	 *  2	KEY				CHAR	05	KEY         	
	 *  3	군정보			CHAR	01	군정보      	
	 *  4	구분			CHAR	02	구분        	SPACE:정상실적, 1:TAKE OUT, 2:TAKE IN
	 *  5	코일번호		CHAR	10	코일번호    	
	 *  6	제작번호/행번	CHAR	13	제작번호행번	
	 *  7	두께			CHAR	05	두께 ㎜	Coil 공통 두께 * 1000 	
	 *  8	폭				CHAR	05	폭   ㎜	Coil 공통 폭에 정수만 가져와서 5자리로 만듦
	 *  9	길이			CHAR	05	길이 Cm	Coil 공통 길이 * 10	
	 *  10	외경			CHAR	05	외경 Coil 공통 그대로       	
	 *  11	중량			CHAR	05	중량 Coil 공통 그대로       	
	 *  12	SPARE			CHAR	137	SPARE       
     *	
     * @param schInfo : 압연실적 실적송신 INFO
     *
     * @return
     * @throws 
     */	 
	private String setACoilMsgInfo(String sGoodsNo,
								   String sOrdNo,
								   String sOrdDtl,
								   String sCoilT,
								   String sCoilW,
								   String sCoilLen,
								   String sCoilOutdia,
								   String sCoilWt,
								   String sGbn){
		
		StringBuffer sMsg = new StringBuffer();

		String TC			= ""; 
		String sKey			= ""; 
		String sGROUP		= ""; 
		String GBN			= ""; 
		String COILNO		= ""; 
		String ORDNODTL		= ""; 
		String COILT		= ""; 
		String COILW		= ""; 
		String COILOUTDIA	= ""; 
		String COILWT		= ""; 
		String COILLEN		= ""; 
		String SPARE		= ""; 
				
		int iTC				=  7; 
		int iKey			=  5; 
		int iGROUP			=  1; 
		int iGBN			=  2; 
		int iCOILNO			= 10; 
		int iORDNODTL		= 13; 
		int iCOILT			=  5; 
		int iCOILW			=  5; 
		int iCOILOUTDIA		=  5; 
		int iCOILWT			=  5; 
		int iCOILLEN		=  5; 
		int iSPARE			=137; 
						   
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			//VALUE SETTING
			TC			= YmCommonConst.TC_THHC171;
			sKey		= YmCommonConst.KEY_LHCVO; 
			sGROUP		= YmCommonConst.GROUP_2; 
			GBN			= sGbn; // space:압연실적,0:take in,1:take out 
			COILNO		= sGoodsNo; 
			ORDNODTL	= sOrdNo+sOrdDtl; 
			COILT 		= Double.valueOf((Double.parseDouble(sCoilT) * 1000) + "").longValue()+"";
			COILW 		= Double.valueOf(sCoilW).longValue()+ "";
			COILLEN		= (Long.parseLong(sCoilLen)* 10) + "";
			COILOUTDIA	= sCoilOutdia; 
			COILWT		= sCoilWt; 
			SPARE		= ""; 
			
			sMsg.append(YmCommonUtil.FillToString(TC		    ,iTC));
			sMsg.append(YmCommonUtil.FillToString(sKey			,iKey));
			sMsg.append(YmCommonUtil.FillToString(sGROUP	    ,iGROUP));
			sMsg.append(YmCommonUtil.FillToString(GBN		    ,iGBN));
			sMsg.append(YmCommonUtil.FillToString(COILNO	    ,iCOILNO));
			sMsg.append(YmCommonUtil.FillToString(ORDNODTL		,iORDNODTL));
			sMsg.append(YmCommonUtil.FillToNumber(COILT		    ,iCOILT));
			sMsg.append(YmCommonUtil.FillToNumber(COILW		    ,iCOILW));
			sMsg.append(YmCommonUtil.FillToNumber(COILLEN	    ,iCOILLEN));
			sMsg.append(YmCommonUtil.FillToNumber(COILOUTDIA    ,iCOILOUTDIA));
			sMsg.append(YmCommonUtil.FillToNumber(COILWT	    ,iCOILWT));
			sMsg.append(YmCommonUtil.FillToString(SPARE	    	,iSPARE));
			
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return sMsg.toString();
	}
	
	/**
	 *  B열연 실적송신 Interface
     *  Source	야드L3	Target	야드L2	I/F방법	JMS	I/F주기	REQ	I/F유형	
	 *  T/C	CN1BP02		
	 *  1	전문코드		TC				CHAR	07	전문코드    	
	 *  2	발생일자		Date			CHAR	10	발생일자    YYYY-MM-DD
	 *  3	발생시간		Time			CHAR	08	발생시간    HH-MM-SS
	 *  4	전문구분		Form			CHAR	01	전문구분    I  : Initialize, U : Update, D : Delete,   R : Re-request
	 *  5	전문길이		Message_Length	CHAR	04	전문길이    	
	 *  6	COIL NO			CoilNo			CHAR	10	COILNO      	
	 *  7	군				Group			CHAR	1	군          	
	 *  8	제작번호/행번	ProductNo		CHAR	13	제작번호행번	
	 *	9	두께			Thick			CHAR	07	㎜	소수점3자리 (###.###)
	 *	10	폭				Width			CHAR	06	㎜	소수점1자리 (####.#)
	 *	11	길이			Length			CHAR	06	㎜	Coil 공통 길이 * 100
	 *	12	외경			Outdia			CHAR	05	㎜	Coil 공통 그대로
	 *	13	중량			Weight			CHAR	05	Kg	Coil 공통 그대로
	 *	14	분기위치CODE					CHAR	02	branch_cd
	 *	15	확장분기위치CODE 				CHAR	02	extend_conveyor_branch_cd
	 *	16	냉각방법						CHAR	01	cool_method
	 *	
	 * @param schInfo : 압연실적 실적송신 INFO
     *
     * @return
     * @throws 
     */	 
	private String setBCoilMsgInfo(String sGoodsNo,
			                       String sOrdNo,
								   String sOrdDtl,
								   String sCoilT,
								   String sCoilW,
								   String sCoilLen,
								   String sCoilOutdia,
								   String sCoilWt,
								   String sBranchCd,
								   String sExBranchCd,
								   String sCoolMethod,
								   String sDEMANDER_NM,
								   String sHRMILL_CMPL_DT,
								   String sNEXT_PROC
									){
		
		StringBuffer sMsg = new StringBuffer();

		String TC				= ""; 
		String sDate			= ""; 
		String sTime			= ""; 
		String Form				= ""; 
		String Message_Length	= ""; 
		String CoilNo			= ""; 
		String sGroup			= ""; 
		String ProductNo		= ""; 
		String Thick			= ""; 
		String Width			= ""; 
		String sLength			= ""; 
		String OutDia			= ""; 
		String Weight			= ""; 
		String BranchCd			= ""; 
	    String ExBranchCd		= ""; 
	    String CoolMethod		= ""; 
	    String DEMANDER_NM 		= ""; 
	    String HRMILL_CMPL_DT	= ""; 
	    String NEXT_PROC		= ""; 
		
		int iTC				=  7; 
		int iDate			= 10; 
		int iTime			=  8; 
		int iForm			=  1; 
		int iMessage_Length	=  4; 
		int iCoilNo			= 10; 
		int iGroup			=  1; 
		int iProductNo		= 13; 
		int iThick			=  7; 
		int iWidth			=  6; 
		int iLength			=  6; 
		int iOutDia			=  5; 
		int iWeight			=  5;
		int iBranchCd		=  2; 
	    int iExBranchCd		=  2; 
	    int iCoolMethod		=  1; 
	    int iDEMANDER_NM 	= 40; 
	    int iHRMILL_CMPL_DT	= 12; 
	    int iNEXT_PROC		=  2;
	    
		int iTotalLength	= 112; //58;
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			//VALUE SETTING
			TC				= YmCommonConst.TC_CN1BP02;
			sDate			= YmCommonUtil.getCurDate("yyyy-MM-dd");
			sTime			= YmCommonUtil.getCurDate("HH-mm-ss");
			Form			= "I"; 
			Message_Length	= iTotalLength+""; 
			CoilNo			= sGoodsNo; 
			sGroup			= YmCommonConst.GROUP_2; 
			ProductNo		= sOrdNo+sOrdDtl; 
			Thick			= StringHelper.replaceStr(
						  	  YmCommonUtil.format(sCoilT,3,3),".",""); 
			Width			= StringHelper.replaceStr(
						  	  YmCommonUtil.format(sCoilW,4,1),".",""); 
			sLength			= sCoilLen;
			OutDia			= sCoilOutdia; 
			Weight			= sCoilWt; 
			BranchCd		= sBranchCd; 
	    	ExBranchCd		= sExBranchCd; 
	    	CoolMethod		= sCoolMethod; 
	    	DEMANDER_NM 	= sDEMANDER_NM; 
		    HRMILL_CMPL_DT	= sHRMILL_CMPL_DT; 
		    NEXT_PROC		= sNEXT_PROC;
	    		
			sMsg.append(YmCommonUtil.FillToString(TC		    ,iTC));
			sMsg.append(YmCommonUtil.FillToString(sDate			,iDate));
			sMsg.append(YmCommonUtil.FillToString(sTime	    	,iTime));
			sMsg.append(YmCommonUtil.FillToString(Form		    ,iForm));
			sMsg.append(YmCommonUtil.FillToNumber(Message_Length,iMessage_Length));
			sMsg.append(YmCommonUtil.FillToString(CoilNo		,iCoilNo));
			sMsg.append(YmCommonUtil.FillToString(sGroup		,iGroup));
			sMsg.append(YmCommonUtil.FillToString(ProductNo		,iProductNo));
			sMsg.append(YmCommonUtil.FillToNumber(Thick    		,iThick));
			sMsg.append(YmCommonUtil.FillToNumber(Width	    	,iWidth));
			sMsg.append(YmCommonUtil.FillToNumber(sLength	    ,iLength));
			sMsg.append(YmCommonUtil.FillToNumber(OutDia	    ,iOutDia));
			sMsg.append(YmCommonUtil.FillToNumber(Weight	    ,iWeight));
			sMsg.append(YmCommonUtil.FillToNumber(BranchCd	    ,iBranchCd));
			sMsg.append(YmCommonUtil.FillToNumber(ExBranchCd	,iExBranchCd));
			sMsg.append(YmCommonUtil.FillToNumber(CoolMethod	,iCoolMethod));
			
			sMsg.append(YmCommonUtil.FillToNumber(DEMANDER_NM	,iDEMANDER_NM));
			sMsg.append(YmCommonUtil.FillToNumber(HRMILL_CMPL_DT,iHRMILL_CMPL_DT));
			sMsg.append(YmCommonUtil.FillToNumber(NEXT_PROC		,iNEXT_PROC));
			
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return sMsg.toString();
	} 
	
       /**
	 * 오퍼레이션명 : 
	 *  
	 * YJK
        * A열연 압연실적처리 저장품에 대해 
        * 작업예약을 호출한다.
        *
        * param String : 저장품ID
        *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */         
	public boolean callCoilWbookInfo(String stockId,
								     String sStockMoveTerm){
		
		boolean isSucess = false;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			YdStockDAO ydStockDAO 	        = new YdStockDAO();
			YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
			YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		
			// 적치단(TB_YM_STACKLAYER) Table Read 
    		// STACK_COL_GP(적치열:첫번째자리 야드구분, 두번째 자리 동구분), STACK_LAYER_STAT(적치단 상태 L:적치중)
            // Select STACK_COL_GP, substr(STACK_COL_GP,1,1) STACK_COL_GP1, substr(STACK_COL_GP,2,1) STACK_COL_GP2 
    		// From TB_YM_STACKLAYER 
    		// Where STOCK_ID = ? And STACK_LAYER_STAT = "L" (적치단 상태 L:적치중)    	
			String stackLayQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.selectStackColGp";
			JDTORecord StackColGp = ydStackLayerDAO.requestgetData(stackLayQueryId, new Object[]{ stockId });

			logger.println(LogLevel.DEBUG,this, "1======================");
			logger.println(LogLevel.DEBUG,this, "StackColGp="+ StackColGp);
			logger.println(LogLevel.DEBUG,this, "1======================");
			
			if(StackColGp == null){ 	
				throw new EJBServiceException("=재료정보=>적치단 정보 존재안함.");
			}
				
			String stackCol  = StringHelper.evl(StackColGp.getFieldString("STACK_COL_GP"), "");
			String stackCol1 = StringHelper.evl(StackColGp.getFieldString("STACK_COL_GP1"), "");
			String stackCol2 = StringHelper.evl(StackColGp.getFieldString("STACK_COL_GP2"), "");
			
			logger.println(LogLevel.DEBUG,this, "2======================================================");
			logger.println(LogLevel.DEBUG,this, "stackCol="+ stackCol + "/" + stackCol1 + "/" + stackCol2);
			logger.println(LogLevel.DEBUG,this, "2======================================================");
			
			if (stackCol != null && !stackCol.equals("")){
				
                // 적치단  Table Update(작업요구상태='S'로 변경)
				// UPDATE TB_YM_STACKLAYER SET STACK_LAYER_STAT = 'S' WHERE STOCK_ID = ?
				String sStkLayerQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark";
				int stkColGp = ydStackLayerDAO.requestupdateData(sStkLayerQueryId, new Object[]{ YmCommonConst.STACK_LAYER_STAT_S, 
																								 stockId.trim() });
				
				logger.println(LogLevel.DEBUG,this, "3=======================");
				logger.println(LogLevel.DEBUG,this, "stockId="+ stockId.trim()+ "stkColGp="+stkColGp);
				logger.println(LogLevel.DEBUG,this, "3=======================");

				
				// 작업예약(TB_YM_WBOOK) Table Select WBOOK_ID Key 생성 Seq + 1
				String wBookQueryId = "ym.steelinfo.steelinforecv.dao.YdStockDAO.getWBookID";
				JDTORecord wBookSel = ydStackLayerDAO.requestFind(wBookQueryId);
				String wBookid      = wBookSel.getFieldString("WBOOK_ID");
				
				logger.println(LogLevel.DEBUG,this, "5=================");
				logger.println(LogLevel.DEBUG,this, "wBookid="+ wBookid );
				logger.println(LogLevel.DEBUG,this, "5=================");

				String sWBookQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.InsertYdWBook";
				
				// 1:보급,2:보급취소,3:추출,4:Take-Out,5:Take-In
				int wbookstockId = 0;
				wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ wBookid, 
																						 stackCol1, 
																						 stackCol2, 
																						 YmCommonConst.NEW_SCH_WORK_KIND_CDLO, 
																						 YmCommonUtil.getWorkDuty(),
																						 YmCommonUtil.getWorkParty()});	
				
				logger.println(LogLevel.DEBUG,this, "6============================================");
				logger.println(LogLevel.DEBUG,this, "stackCol1="+ stackCol1+ " stackCol2="+stackCol2);
				logger.println(LogLevel.DEBUG,this, "6============================================");
 
				// 저장품 Table(TB_YM_STOCK)에 WBOOK_ID를 Update 한다.
				// UPDATE TB_YM_STOCK SET WBOOK_ID= ?, STOCK_MOVE_TERM(저장품이동조건) = ?	 WHERE STOCK_ID = ?
				String stkQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
				
				int stkId = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ wBookid, 
																			       sStockMoveTerm, 
																			       stockId.trim() });	
				
				logger.println(LogLevel.DEBUG,this, "7======================================");
				logger.println(LogLevel.DEBUG,this, "stockId="+ stockId.trim()+ "stkId="+stkId);
				logger.println(LogLevel.DEBUG,this, "7======================================");
				
			}			
			isSucess = true;	 
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSucess; 
	}
	
	///////////////////////////////////////////////////////////////////////////
	/////////////////////////////////YJK END///////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	
	
       /**
	 * 오퍼레이션명 : 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */         
	public List getStackColGpNum(String queryID, String yd_gp, String col_gp, String span_gp){
		YdStockDAO ydstockDAO = new YdStockDAO();  
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
	    	return ydstockDAO.getListData(queryID,new Object[]{yd_gp + col_gp + span_gp});	    	
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}
	
	

	/**
	 * 오퍼레이션명 : 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public List getListprodutPerSpan(String queryID, String col_gp){
		YdStockDAO ydstockDAO = null;	    
	    List producList = null;
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
	    	ydstockDAO = new YdStockDAO();
	    	producList = ydstockDAO.getListData(queryID, new Object[]{col_gp});
	    	return producList;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public List getListprodutPerSpanLayer(String queryID, String col_gp, String Layer_gp){
		YdStockDAO ydstockDAO = null;	    
	    List producList = null;
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
	    	ydstockDAO = new YdStockDAO();
	    	producList = ydstockDAO.getListData(queryID, new Object[]{col_gp, Layer_gp});
	    	return producList;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public List getListprodutPerSpanLayer1(String queryID, String col_gp, String Layer_gp, String Layer_gp1){
		YdStockDAO ydstockDAO = null;	    
	    List producList = null;
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
	    	ydstockDAO = new YdStockDAO();
	    	producList = ydstockDAO.getListData(queryID, new Object[]{col_gp, Layer_gp, Layer_gp1});
	    	return producList;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}
	
	
	
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public List getListMoveProduct(String queryID, String stack_gp){
		YdStockDAO ydstockDAO = null;	    
	    List producList = null;
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
	    	ydstockDAO = new YdStockDAO();
	    	producList = ydstockDAO.getListData(queryID, new Object[]{stack_gp});
	    	return producList;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}

	/**
	 * 오퍼레이션명 : 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public List getListMoveProduct(String queryID, String stack_gp, String moveterm){
		YdStockDAO ydstockDAO = null;	    
	    List producList = null;
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
	    	ydstockDAO = new YdStockDAO();
	    	producList = ydstockDAO.getListData(queryID, new Object[]{stack_gp, moveterm});
	    	return producList;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}
	
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public JDTORecord getListMoveToLoc(String queryID, String stackId){
		YdStockDAO ydstockDAO = null;	    
	    JDTORecord returnRecord = null;
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
	    	ydstockDAO = new YdStockDAO();
	    	returnRecord = ydstockDAO.getData(queryID,new Object[]{stackId});
	    	return returnRecord;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public JDTORecord getMoveSchCode(String queryID, String yd_gp, String moveterm){ 
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
	    	YdStockDAO ydstockDAO = new YdStockDAO();
	    	return ydstockDAO.getData(queryID,new Object[]{yd_gp, moveterm});
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public JDTORecord getStackLayerCheck1(String queryID, String stackId){
		YdStockDAO ydstockDAO = null;	    
	    JDTORecord returnRecord = null;
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
	    	ydstockDAO = new YdStockDAO();
	    	returnRecord = ydstockDAO.getData(queryID,new Object[]{stackId});
	    	return returnRecord;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public JDTORecord getWBookID(String queryID){ 
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
	    	YdStockDAO ydstockDAO = new YdStockDAO();
	    	return ydstockDAO.getData(queryID,new Object[]{});
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */	
	public JDTORecord getCTSRelayStat(String queryID, String equip_gp, String from_bay_gp, String to_bay_gp, String from_bay_gp2, String to_bay_gp2){
		YdStockDAO ydstockDAO = null;	    
	    JDTORecord returnRecord = null;
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
	    	ydstockDAO = new YdStockDAO();
	    	returnRecord = ydstockDAO.getData(queryID,new Object[]{equip_gp, from_bay_gp, to_bay_gp, from_bay_gp2, to_bay_gp2});
	    	return returnRecord;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}

	/**
	 * 오퍼레이션명 : 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public int moveProcess(String query1, String qeury2, String query3, List insertMoveProductList, List updateStockWBookIDList, List updateStockStatList){

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return 0;
		}
		
		int resNum = 0;
		resNum = insertMoveProduct(query1, insertMoveProductList);		
		resNum = updateStockWBookID(qeury2, updateStockWBookIDList);		
		resNum = updateStockStat(query3, updateStockStatList);		
		return resNum;
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public int moveProcess( String qeury2, String query3,  List updateStockWBookIDList, List updateStockStatList){

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return 0;
		}
		
		int resNum = 0;	
		resNum = updateStockWBookID(qeury2, updateStockWBookIDList);		
		resNum = updateStockStat(query3, updateStockStatList);		
		return resNum;
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public int insertMoveProduct(String query1, List insertMoveProductList) throws EJBServiceException{   
    	try {   
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
    		YdStockDAO ydstockDAO = new YdStockDAO();  			
   			return ydstockDAO.insertMoveProduct(query1, insertMoveProductList);
    	}catch(DAOException daoe){
            throw daoe;
        }catch(Exception e){         
        	throw new EJBServiceException(e);
        }
    }
	/**
	 * 오퍼레이션명 : 보급허용기준 데이터를 테이블에 INSERT. 데이터가 없는 경우 처리. 최규성 2009-08-20 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public int insertStandardData(String query1, List insertStdDataList) throws EJBServiceException{   
    	try {   	
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
    		YdStockDAO ydstockDAO = new YdStockDAO();  			
   			return ydstockDAO.insertMoveProduct(query1, insertStdDataList);
    	}catch(DAOException daoe){
            throw daoe;
        }catch(Exception e){         
        	throw new EJBServiceException(e);
        }
    }


	/**
	 * 오퍼레이션명 : 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param query2
	 * @param updateStockWBookIDList
	 * @return
	 * @throws 
	 */
	public int updateStockWBookID(String qeury2, List updateStockWBookIDList) throws EJBServiceException{   
    	try { 		
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
    		YdStockDAO ydstockDAO = new YdStockDAO();  			
   			return ydstockDAO.updateMoveProduct(qeury2, updateStockWBookIDList);
    	}catch(DAOException daoe){
            throw daoe;
        }catch(Exception e){         
        	throw new EJBServiceException(e);
        }
    }
	// CGS
	/**
	 * 오퍼레이션명 : 수정된 데이터를 입력한다. 최규성 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param queryID
	 * @param listData
	 * @return
	 * @throws EJBServiceException
	 */
	public int updateStandardData(String queryID, List listData) throws EJBServiceException{
		try { 	
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
    		YdStockDAO ydstockDAO = new YdStockDAO();  			
   			return ydstockDAO.updateMoveProduct(queryID, listData);
    	}catch(DAOException daoe){
            throw daoe;
        }catch(Exception e){         
        	throw new EJBServiceException(e);
        }
	}
	/**
	 * 오퍼레이션명 : 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public int updateStockStat(String query3, List updateStockStatList) throws EJBServiceException{   
    	try {   		
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
    		YdStockDAO ydstockDAO = new YdStockDAO();  			
   			return ydstockDAO.updateMoveProduct(query3, updateStockStatList);
    	}catch(DAOException daoe){
            throw daoe;
        }catch(Exception e){         
        	throw new EJBServiceException(e);
        }
    }
	
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public List getListCarCardNo(String queryID, List listData) throws EJBServiceException ,DAOException{
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			YdStockDAO ydstockDAO = new YdStockDAO();
			return ydstockDAO.getListData(queryID, listData);
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public List getListSlabMakerinfo(String queryID) throws EJBServiceException ,DAOException{
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			YdStockDAO ydstockDAO = new YdStockDAO();
			return ydstockDAO.getListData2(queryID);
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */  
	public List getListBCMoveProduct(String queryID, List listData) throws EJBServiceException ,DAOException{
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			YdStockDAO ydstockDAO = new YdStockDAO();
			return ydstockDAO.getListData(queryID, listData);
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */  
	public List getListDetailCarLoading(String queryID, List listData) throws EJBServiceException ,DAOException{
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			YdStockDAO ydstockDAO = new YdStockDAO();
			return ydstockDAO.getListData(queryID, listData);
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */  
	public List getListStockLineOff(String queryID, List listData) throws EJBServiceException ,DAOException{
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			YdStockDAO ydstockDAO = new YdStockDAO();
			return ydstockDAO.getListData(queryID, listData);
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */  
	public List getListStockTakeOff(String queryID, List listData) throws EJBServiceException ,DAOException{
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			YdStockDAO ydstockDAO = new YdStockDAO();
			return ydstockDAO.getListData(queryID, listData);
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}
	/**
	 * 오퍼레이션명 : 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */  
	public List getListPdaSchedule(String queryID, List listData) throws EJBServiceException ,DAOException{
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			YdStockDAO ydstockDAO = new YdStockDAO();
			return ydstockDAO.getListData(queryID, listData);
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}
	
	
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */  
	public List getCarCardNo(String queryID, List listData) throws EJBServiceException ,DAOException{
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			YdStockDAO ydstockDAO = new YdStockDAO();
			return ydstockDAO.getListData(queryID, listData);
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}
	/**
	 * 오퍼레이션명 : 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */  
	public List getListDmMoveStock(String queryID, List listData) throws EJBServiceException ,DAOException{
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			YdStockDAO ydstockDAO = new YdStockDAO();
			return ydstockDAO.getListData(queryID, listData);
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */  
	public JDTORecord getTCStartStatusYN(String queryID, List listData) throws EJBServiceException ,DAOException{	
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			YdEquipDAO ydequipDAO = new YdEquipDAO();
			return ydequipDAO.getData(queryID, listData);
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}
	/**
	 * 오퍼레이션명 : 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */  
	public int updateTCStackStat(String queryid, List listData) throws EJBServiceException ,DAOException {   	 
		try {		
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
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
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */  
	public int updatePTCardNo(String queryid, List listData) throws EJBServiceException ,DAOException {   	 
		try {		
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
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
	 * 오퍼레이션명 : 재료번호를 이용하여 현위치를 가져온다.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */   
    public JDTORecord selectCoilMod(String queryId, String coilNo) {
            YdStockDAO ydstockDAO = null;       
        JDTORecord returnRecord = null;
        try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
            ydstockDAO = new YdStockDAO();
            returnRecord = ydstockDAO.getData(queryId, new Object[]{coilNo});
            return returnRecord;
        }catch(DAOException daoe){
            throw daoe;
        }catch(Exception e){
            throw new EJBServiceException(e);
        }
    }
    
    
    /**
	 * 오퍼레이션명 : 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */   
    public String setStockMoveList( String YD_GP,				//야드구분
									String BAY_GP,				//동구분(동간이적시 목적동)
									String SECT_GP,				//스판구분
									String COL_GP,				//열구분
									String From_BAY_GP,			//동구분(현재동)
									String Move_Kind,			//동간(S),동내(O)
									String facility_GP,			//없슴
									String facility_GP_Code,	//스케쥴코드
									String temp_stock_id,		//저장품
									String modifier	       		//수정자
									) {
        String alertString 		= "이적처리가 정상적으로 등록되었습니다.";
        
        try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			ymCommonDAO dao = ymCommonDAO.getInstance();
			
			// 웹페이지에서 넘겨받은 데이터  CGS
			/**
			 *	1. 스케쥴 코드 셋팅 
			 */ 
			String sSchCode = facility_GP_Code;
			/**
			 *	2. 동내,동간 이적 구분 셋팅
			 */ 
			String sWorkGbn = Move_Kind;
			/**
			 *	3. 야드구분
			 */ 
			String sYdGp = YD_GP;
			/**
			 *	4. 동구분
			 */ 
			String sToBayGp   = BAY_GP;
			String sFromBayGp = From_BAY_GP;
			/**
			 *	5. 스판구분
			 */ 
			String sSectGp = SECT_GP;
			/**
			 *	6. 열구분
			 */ 
			String sColGp = COL_GP;
			
			String snextProc ="";
			String scoilT ="";
			String scoilW ="";
			String sToBayGp2 =""; 
			
			/**
			 *	7. 저장품리스트
			 */ 
			String sStockList   = temp_stock_id;
			
			String vStockList[] = sStockList.split("-");
			/**
			 *	8. 수정자
			 */ 
			String sModifier = modifier;
			
			List wbookList = new ArrayList();
			List stockList = new ArrayList();
			List layerList = new ArrayList();
			JDTORecord wbookJr = null;
			// CGS 로그 추가
			logger.println(LogLevel.DEBUG, this, "facility_GP : " + facility_GP);
			logger.println(LogLevel.DEBUG, this, "수신 :스케줄코드 " + sSchCode);
			logger.println(LogLevel.DEBUG, this, "수신 :동내,동간 이적 구분 셋팅 " + sWorkGbn);
			logger.println(LogLevel.DEBUG, this, "수신 :야드구분 " + sYdGp);
			logger.println(LogLevel.DEBUG, this, "수신 :동구분(From) " + sFromBayGp);
			logger.println(LogLevel.DEBUG, this, "수신 :동구분(To) " + sToBayGp);
			logger.println(LogLevel.DEBUG, this, "수신 :스판구분 " + sSectGp);
			logger.println(LogLevel.DEBUG, this, "수신 :열구분 " + sColGp);
			logger.println(LogLevel.DEBUG, this, "수신 :저장품리스트 " + sStockList);
			logger.println(LogLevel.DEBUG, this, "수신 :수정자 " + sModifier);
			/**
			 *	B열연 SLAB
			 *  A열연 SLAB야드 (2007.01.22)추가(MCH)
			 */
			if(YmCommonConst.YD_GP_2.equals(sYdGp) 
			|| YmCommonConst.YD_GP_0.equals(sYdGp)){
												
				for(int i = 0 ; i < vStockList.length ; i++) {
					
					String sQueryId3	   = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.getStackLayerCheck1";
					JDTORecord stockJr     = dao.getCommonInfo(sQueryId3,
															   new Object[]{vStockList[i]});
					
					if(stockJr == null) {
						alertString = "저장품DB에 이적하려는 대상이 없습니다.";
						break;
					}else {
						String sQueryId4	= "ym.steelinfo.steelinforecv.dao.YdStockDAO.getWBookID";
						wbookJr			  	= dao.getCommonInfo(sQueryId4,
																new Object[]{});
					
						String sWbookId 	= StringHelper.evl(wbookJr.getFieldString("WBOOK_ID"),"");
						
						String sTcNo 		= "";
						/**
						 *	SLAB 동간이적은 facility_GP_Code 항목이 FROM 대차 정보이다.
						 *  따라서 OPERATOR 지정으로 셋팅한다.
						 */
						if("S".equals(sWorkGbn)){
							
							//sSchCode 	= YmCommonConst.NEW_SCH_WORK_KIND_STML;
							//sSchCode 	= YmCommonConst.NEW_SCH_WORK_KIND_STM2;
							//sTcNo 	 	= facility_GP_Code;
							sSchCode	= facility_GP_Code.substring(6, 10);
							sTcNo		= facility_GP_Code.substring(0, 6);
						}
							
						wbookList.add(sWbookId);
						wbookList.add(sYdGp);
						wbookList.add(sFromBayGp);
						wbookList.add(sSchCode);
						wbookList.add(YmCommonConst.SCH_WORK_LOC_DECISION_METHOD_O);
						if("S".equals(sWorkGbn)){
							wbookList.add(sTcNo);//동간이적 대차를 'O'로 셋팅.
						}else {
							wbookList.add(sYdGp+
							        	  sToBayGp+
										  sSectGp+
										  sColGp);//야드+동+스판+열(동내이적일때만입력)
						}
						wbookList.add(YmCommonUtil.getWorkDuty());
						wbookList.add(YmCommonUtil.getWorkParty());
						wbookList.add("O");
						
						if("S".equals(sWorkGbn)) {
							wbookList.add("");
							wbookList.add("");
							wbookList.add(sToBayGp);
						}else {
							wbookList.add("");
							wbookList.add("");
							wbookList.add("");
						}
						wbookList.add(sModifier);
						
						/*
						 *	저장품지정 동간이적일 경우 10자리 정보를 셋팅
						 */
						String sUnPutLoc = "";
						if(facility_GP.equals(YD_GP+BAY_GP+SECT_GP+COL_GP)){
							sUnPutLoc = facility_GP;
						}else{
							sUnPutLoc = sToBayGp;
						}
												
						stockList.add(sWbookId);
						if("S".equals(sWorkGbn)) {
							stockList.add("");
							stockList.add("");
							stockList.add(sYdGp);
							stockList.add(sToBayGp);
							stockList.add(sUnPutLoc);
							stockList.add("");
						}else {
							stockList.add("");
							stockList.add("");
							stockList.add("");
							stockList.add("");
							stockList.add("");
							stockList.add("");
						}
						stockList.add(sModifier);
						stockList.add(vStockList[i]);
						
						layerList.add(sModifier);
						layerList.add(vStockList[i]);
		
					}
					/*
					--이적대상 작업예약등록
					insert into TB_YM_WBOOK (
					    WBOOK_ID,
					    YD_GP,
					    BAY_GP,
					    SCH_WORK_KIND,
					    SCH_WORK_LOC_DECISION_METHOD,
					    CRANE_WORD_PUT_LOC,
					    WBOOK_DDTT,
					    WBOOK_DUTY,
					    WBOOK_PARTY,
					    WBOOK_SCH_TERM,
					    WBOOK_SCH_ACT_DDTT,
					    FRTOMOVE_EQUIP_GP,
					    CTS_RELAY_YN,
					    CTS_RELAY_BAY,
					    CARUNLOAD_BAY,
					    REGISTER,
					    REG_DDTT,
					    MODIFIER,
					    MOD_DDTT,
					    DEL_YN
					) values (?,
					    ?,      --FROM야드구분(1)
					    ?,      --FROM동구분(E)
					    ?,      --스케쥴코드(화면에서파라미터로받음)
					    ?,      --동간이적일때는('S') 동내이적일때는('O')
					    ?,      --put위치(1E0501)야드+동+스판+열; (동내이적일때만입력)
					    to_char(sysdate,'YYYYMMDDHH24MI'),
					    ?,      --작업예약근(근조계산모듈사용)
					    ?,      --작업예약조(근무조계산모듈사용)
					    ?,      --작업예약스케쥴조건('O')
					    null,
					    null,
					    ?,      --CTS중계구분(Y);동내이적은무조건 NULL로셋팅
					    ?,      --CTS중계동(E);동내이적은무조건 NULL로셋팅
					    ?,      --하차동(F);이적할TO동;동내이적은NULL로셋팅
					    ?,
					    sysdate,
					    null,
					    null,
					    'N')
					
					*/
					String sQueryId5 = "ym.steelinfo.steelinforecv.dao.YdStockDAO.insertMoveProduct";
					/*--작업예약ID 등록
					update TB_YM_STOCK
					   set WBOOK_ID = ?  --작업예약ID
					     , CTS_RELAY_YN = ?    --CTS중계구분(Y);동내이적은무조건 NULL로셋팅
					     , CTS_RELAY_BAY = ?   --CTS중계동(E);동내이적은무조건 NULL로셋팅
					     , CARUNLOAD_YD = ?    --하차야드(1);동내이적은NULL로셋팅
					     , CARUNLOAD_BAY = ?   --하차동(F);이적할TO동;동내이적은NULL로셋팅
					     , CARUNLOAD_PUT_LOC = ?   --하차PUT위치;싱글동간일때는 10자리, 멀티동간일때는 1자리셋팅
					     , CAR_CARD_NO = ?      --카드번호(B열연 팔레트로 동간이적할때만 등록)
					     , MODIFIER = ?
					     , MOD_DDTT = sysdate
					where  STOCK_ID = ?       --저장품ID
					*/
					String sQueryId6 = "ym.steelinfo.steelinforecv.dao.YdStockDAO.updateStockWBookID";
					/*--적치상태변경
					UPDATE TB_YM_STACKLAYER
					set STACK_LAYER_STAT = 'S'
					      , MODIFIER = ?
					      , MOD_DDTT = sysdate   
					where  STOCK_ID = ?     --저장품ID
					AND STACK_LAYER_STAT IN ('L')
					*/
					String sQueryId7 = "ym.steelinfo.steelinforecv.dao.YdStockDAO.updateStockStat";
					
					int iRt = moveProcess(sQueryId5,
										  sQueryId6,
										  sQueryId7,
										  wbookList, 
										  stockList, 
										  layerList);
								
					wbookList.clear();
					stockList.clear();
					layerList.clear();
				}
			/**
			 *	A열연 COIL,B열연 COIL
			 */
			}else if(YmCommonConst.YD_GP_1.equals(sYdGp)
				   ||YmCommonConst.YD_GP_3.equals(sYdGp)){	//


				logger.println(LogLevel.DEBUG, this, "==== 시작 : A열연/B열연 Coil 이적처리 ====");	
				logger.println(LogLevel.DEBUG, this, "==== 야드정보 : A열연/B열연 Coil 이적처리 ====");
				String sCtsYn  = ""; 
				String sCtsBay = "";
				
				//from 동 재료에서 가져 오기
				String sQueryId10 = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.getStackLayerCheck2";
				JDTORecord stockJr2 = dao.getCommonInfo(sQueryId10 , new Object[]{vStockList[0]});
				
				
				logger.println(LogLevel.DEBUG, this, "==== 입력받은 from동 ===>>:"+sFromBayGp);	
				if(stockJr2 != null){
					sFromBayGp  = StringHelper.evl(stockJr2.getFieldString("BAY_GP"),"");			 
    			}
				logger.println(LogLevel.DEBUG, this, "==== 저장위치기준 from동 ===>>:"+sFromBayGp);
				
				if(YmCommonConst.YD_GP_1.equals(sYdGp)){
					String sQueryId2	 = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.getCTSRelayYNStat";
	    			JDTORecord equipJr   = dao.getCommonInfo(sQueryId2,
	    													 new Object[]{"1XTC01",
	    																  sFromBayGp,
																		  sToBayGp,
																		  sFromBayGp,
																		  sToBayGp});
	    			if(equipJr != null){
		    			sCtsYn  = StringHelper.evl(equipJr.getFieldString("중계구분"),"");
						sCtsBay = StringHelper.evl(equipJr.getFieldString("중계동"),"");
	    			}
				}
				// vStockList.length는 웹페이지에서 이적 선택한 갯수
				for(int i = 0 ; i < vStockList.length ; i++){
					 
					// 저장품 검사.
					/*
					 * 	select STOCK_ID 
					 * 	  from TB_YM_STOCK
					 *   where STOCK_ID = :vStockList --저장품(ID)
					*/
					String sQueryId3	  = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.getStackLayerCheck1";
    				JDTORecord stockJr    = dao.getCommonInfo(sQueryId3, new Object[]{vStockList[i]});
    			
					if(stockJr == null){
						alertString = "저장품DB에 이적하려는 대상이 없습니다.";
						break;
					}else{
						
						//목적동이 전체로 선택된 경우 아래 조건으로 자동 목적동 계산 2018.09.21
						//1K: D동 
						//1Q:(2.*1530 , 1.6*1224 : F동 ,  나머지 : E동) 
						//1H: B동
						
						sToBayGp2 ="";
						
						if("".equals(sToBayGp)){ 
							logger.println(LogLevel.DEBUG, this, "==== 입력받은 to동 전체인 경우 ===>>: 자동계산 STOCK_ID:"+vStockList[i]+", NEXT_PROC:"+snextProc+", COIL_T:"+scoilT+", COIL_W:"+scoilW);	
							
							snextProc  	= StringHelper.evl(stockJr.getFieldString("NEXT_PROC"),"");
							scoilT  	= StringHelper.evl(stockJr.getFieldString("COIL_T"),"");
							scoilW  	= StringHelper.evl(stockJr.getFieldString("COIL_W"),"");
							
							if("1K".equals(snextProc)){
								sToBayGp2 ="D";
							}else if("1H".equals(snextProc)){
								sToBayGp2 ="B";
							}else if("1Q".equals(snextProc)){
								if(("1.6".equals(scoilT)&& "1224".equals(scoilW)) ||
								   ("2".equals(scoilT)&& "1530".equals(scoilW)) ){
									sToBayGp2 ="F";
								}else{
									sToBayGp2 ="E";
								}
							}else {
								alertString = "지정할 목적동이 없습니다.";
								break;
							}
							
							
							//중계동 
							if(YmCommonConst.YD_GP_1.equals(sYdGp)){
								String sQueryId2	 = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.getCTSRelayYNStat";
				    			JDTORecord equipJr   = dao.getCommonInfo(sQueryId2,
				    													 new Object[]{"1XTC01",
				    																  sFromBayGp,
				    																  sToBayGp2,
																					  sFromBayGp,
																					  sToBayGp2});
				    			if(equipJr != null){
					    			sCtsYn  = StringHelper.evl(equipJr.getFieldString("중계구분"),"");
									sCtsBay = StringHelper.evl(equipJr.getFieldString("중계동"),"");
				    			}
							}
						
						}else{
							sToBayGp2=sToBayGp ;
						}
						
						
						// 작업예약ID 가져온다.
						/*
						 * SELECT TO_CHAR(SYSDATE,'YYYYMMDDHH24MI')||LPAD(YM_WBOOK_SEQ.NEXTVAL,6,'0') AS WBOOK_ID
						 * FROM DUAL
						 */
						String sQueryId4	= "ym.steelinfo.steelinforecv.dao.YdStockDAO.getWBookID";
    					wbookJr = dao.getCommonInfo(sQueryId4, new Object[]{});
						
						String sWbookId = StringHelper.evl(wbookJr.getFieldString("WBOOK_ID"),"");
						//========================================================================================================================
						// CGS 추가
						String sToLoc = "";	// 
						
						if (sWorkGbn.equals("S"))	// 동간이적일 경우
						{
							sSchCode = facility_GP_Code.substring(0,  4);
						}
						//========================================================================================================================

						// 작업예약리스트 순서
						// 작업예약ID
						// From 야드 구분
						// From 동 구분
						// 스케줄코드
						// 위치지정방법
						// PUT위치 
						// 작업예약근
						// 작업예약조
						// 작업예약스케줄조건	
						wbookList.add(sWbookId);				// 작업예약ID
						wbookList.add(sYdGp);					// From 야드 구분
						wbookList.add(sFromBayGp);				// From 동 구분
						wbookList.add(sSchCode);				// 스케줄 코드
/*// CGS 추가 신규대차관련 코드
						if(   sSchCode.equals(YmCommonConst.NEW_SCH_WORK_KIND_CTM5) 
								|| sSchCode.equals(YmCommonConst.NEW_SCH_WORK_KIND_CTM6)
								|| sSchCode.equals(YmCommonConst.NEW_SCH_WORK_KIND_CTM7) ){
							// CGS 수정
							// 신규대차의 동간이적상차 스케줄은 To위치를 조업자 강제 지정으로 정의한다.
							sToLoc    = facility_GP_Code.substring(4, 10);
							//wbookList.add(YmCommonConst.SCH_WORK_LOC_DECISION_METHOD_O);
							wbookList.add(YmCommonConst.SCH_WORK_LOC_DECISION_METHOD_S);
							sToLoc = sToLoc + YmCommonConst.STACKER_CONST_01+YmCommonConst.STACKLAYER_CONST_01;
						}else{
							wbookList.add(sWorkGbn);				// 위치지정 방법 'S': 시스템, 'O': 조업자지정
						}
*/						
						wbookList.add(sWorkGbn);				// 위치지정 방법 'S': 시스템, 'O': 조업자지정
						if("S".equals(sWorkGbn)){		// 동간이적("S") 이면
/*// CGS 수정  신규대차관련코드
							if(   sSchCode.equals(YmCommonConst.NEW_SCH_WORK_KIND_CTM5) 
								|| sSchCode.equals(YmCommonConst.NEW_SCH_WORK_KIND_CTM6)
								|| sSchCode.equals(YmCommonConst.NEW_SCH_WORK_KIND_CTM7) ){
								wbookList.add(sToLoc);						// Put위치 지정.
							}else{
*/								
								wbookList.add("");			//야드+동+스판+열(동내이적일때만입력) 
/*//최규성 추가
							}
*/
						}else{
							wbookList.add(sYdGp+
										  sFromBayGp+
										  sSectGp+
										  sColGp);//야드+동+스판+열(동내이적일때만입력)
						}
						wbookList.add(YmCommonUtil.getWorkDuty());
						wbookList.add(YmCommonUtil.getWorkParty());
						wbookList.add("O");
						if("S".equals(sWorkGbn)){		// 동간이적
							wbookList.add(sCtsYn);
							wbookList.add(sCtsBay);
							wbookList.add(sToBayGp2);
						}else{							// 동내이적
							wbookList.add("");
							wbookList.add("");
							wbookList.add("");
						}
						wbookList.add(sModifier);
						logger.println(LogLevel.DEBUG, this, "작업예약List : " + wbookList);
						
						/*
						 *	저장품지정 동간이적일 경우 10자리 정보를 셋팅
						 */
						// 저장품 하차 위치
						String sUnPutLoc = "";
						if(facility_GP.equals(YD_GP+BAY_GP+SECT_GP+COL_GP)){
							sUnPutLoc = facility_GP;
						}else{
							sUnPutLoc = sToBayGp2;
						}
						
						stockList.add(sWbookId);
						if("S".equals(sWorkGbn)){	// 동간이적 
							stockList.add(sCtsYn);
							stockList.add(sCtsBay);
							stockList.add(sYdGp);
							stockList.add(sToBayGp2);
							stockList.add(sUnPutLoc);
							stockList.add("");
						}else{
							stockList.add("");
							stockList.add("");
							stockList.add("");
							stockList.add("");
							stockList.add("");
							stockList.add("");
						}
						stockList.add(sModifier);
						stockList.add(vStockList[i]);
		
						layerList.add(sModifier);
						layerList.add(vStockList[i]);
// 로그 추가. 최규성
						logger.println(LogLevel.DEBUG, this, "편집완료된 데이터 확인");
						logger.println(LogLevel.DEBUG, this, "작업예약List : " + wbookList);
						logger.println(LogLevel.DEBUG, this, "저장품List : "   + stockList);
						logger.println(LogLevel.DEBUG, this, "레이어List : "   + layerList);
						String sQueryId5 = "ym.steelinfo.steelinforecv.dao.YdStockDAO.insertMoveProduct";
						String sQueryId6 = "ym.steelinfo.steelinforecv.dao.YdStockDAO.updateStockWBookID";
						String sQueryId7 = "ym.steelinfo.steelinforecv.dao.YdStockDAO.updateStockStat";
						// 작업예약 처리. 각 테이블에 관련 데이터 입력 및 수정 처리.
						int iRt = moveProcess(sQueryId5,
										    sQueryId6,
										    sQueryId7,
										    wbookList, 
										    stockList, 
										    layerList);
					}
		
					wbookList.clear();
					stockList.clear();
					layerList.clear();
					
				}
			}
			logger.println(LogLevel.DEBUG, this, "setStockMoveList() 처리 완료 " );
        }catch(DAOException daoe){
            throw daoe;
        }catch(Exception e){
            throw new EJBServiceException(e);
        }
        return alertString;
    }    
	
      /**
	 * 오퍼레이션명 : 하차 PUT 위치을 삭제 한다.
	 * COIL 야드 관리 -> 산적 LOT관리 -> 저장품지정이적 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */    
    public int updateStockIdPutLoc(String queryid, String Put_Loc, String modifier) throws EJBServiceException ,DAOException {   	 
		try {	
			
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			YdStockDAO YdStockdao = new YdStockDAO();
			return YdStockdao.updateStockIdPutLoc(queryid, Put_Loc, modifier);
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
	public int updateCoillunloadBay(String queryID, JDTORecord jrecord) throws EJBServiceException ,DAOException{
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			int count = 0;
			List RULE_ID2 	= (List)jrecord.getField("RULE_ID2");
			List SCH_CD2 		= (List)jrecord.getField("SCH_CD2");
			List RANKING2 		= (List)jrecord.getField("RANKING2");
			
			for(int ii =0; ii<RULE_ID2.size();ii++){
				

				count+= new YdSlabMoveBayRankingDAO().updateCoillunloadBay(queryID,
																				 ""+RANKING2.get(ii),
																				 ""+RULE_ID2.get(ii),
																				 ""+SCH_CD2.get(ii)
																				 );

			}
			return count;
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
	public int updateCoillunloadBayA(String queryID, JDTORecord jrecord) throws EJBServiceException ,DAOException{
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			int count = 0;
			List RULE_ID2 	= (List)jrecord.getField("RULE_ID2");
			List SCH_CD2 		= (List)jrecord.getField("SCH_CD2");
			List RANKING2 		= (List)jrecord.getField("RANKING2");
			
			for(int ii =0; ii<RULE_ID2.size();ii++){
				

				count+= new YdSlabMoveBayRankingDAO().updateCoillunloadBayA(queryID,
																				 ""+RANKING2.get(ii),
																				 ""+RULE_ID2.get(ii),
																				 ""+SCH_CD2.get(ii)
																				 );

			}
			return count;
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
	public int updateCoilloadLotRanking_CM(String queryID, JDTORecord jrecord) throws EJBServiceException ,DAOException{
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			int count = 0;
			List RULE_ID3 	= (List)jrecord.getField("RULE_ID3");
			List SCH_CD3 		= (List)jrecord.getField("SCH_CD3");
			List RANKING3 		= (List)jrecord.getField("RANKING3");
			
			for(int ii =0; ii<RULE_ID3.size();ii++){
				

				count+= new YdSlabMoveBayRankingDAO().updateCoilloadLotRanking_CM(queryID,
																				 ""+RANKING3.get(ii),
																				 ""+RULE_ID3.get(ii),
																				 ""+SCH_CD3.get(ii)
																				 );

			}
			return count;
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
	public int updateCoilloadLotRankingA_CM(String queryID, JDTORecord jrecord) throws EJBServiceException ,DAOException{
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			int count = 0;
			List RULE_ID3 	= (List)jrecord.getField("RULE_ID3");
			List SCH_CD3 		= (List)jrecord.getField("SCH_CD3");
			List RANKING3 		= (List)jrecord.getField("RANKING3");
			
			for(int ii =0; ii<RULE_ID3.size();ii++){
				

				count+= new YdSlabMoveBayRankingDAO().updateCoilloadLotRankingA_CM(queryID,
																				 ""+RANKING3.get(ii),
																				 ""+RULE_ID3.get(ii),
																				 ""+SCH_CD3.get(ii)
																				 );

			}
			return count;
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
	public int updateCoilNextProc(String queryID, JDTORecord jrecord) throws EJBServiceException ,DAOException{
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			int count = 0;
			List RULE_ID4 	= (List)jrecord.getField("RULE_ID4");
			List SCH_CD4 		= (List)jrecord.getField("SCH_CD4");
			List RANKING4 		= (List)jrecord.getField("RANKING4");
			
			for(int ii =0; ii<RULE_ID4.size();ii++){
				

				count+= new YdSlabMoveBayRankingDAO().updateCoilNextProc(queryID,
																				 ""+RANKING4.get(ii),
																				 ""+RULE_ID4.get(ii),
																				 ""+SCH_CD4.get(ii)
																				 );

			}
			return count;
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
	public int updateCoilNextProcA(String queryID, JDTORecord jrecord) throws EJBServiceException ,DAOException{
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			int count = 0;
			List RULE_ID4 	= (List)jrecord.getField("RULE_ID4");
			List SCH_CD4 		= (List)jrecord.getField("SCH_CD4");
			List RANKING4 		= (List)jrecord.getField("RANKING4");
			
			for(int ii =0; ii<RULE_ID4.size();ii++){
				

				count+= new YdSlabMoveBayRankingDAO().updateCoilNextProcA(queryID,
																				 ""+RANKING4.get(ii),
																				 ""+RULE_ID4.get(ii),
																				 ""+SCH_CD4.get(ii)
																				 );

			}
			return count;
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
	public int updateCoilunloadWlocCD(String queryID, JDTORecord jrecord) throws EJBServiceException ,DAOException{
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			int count = 0;
			List RULE_ID5 	= (List)jrecord.getField("RULE_ID5");
			List SCH_CD5 		= (List)jrecord.getField("SCH_CD5");
			List RANKING5 		= (List)jrecord.getField("RANKING5");
			
			for(int ii =0; ii<RULE_ID5.size();ii++){
				

				count+= new YdSlabMoveBayRankingDAO().updateCoilunloadWlocCD(queryID,
																				 ""+RANKING5.get(ii),
																				 ""+RULE_ID5.get(ii),
																				 ""+SCH_CD5.get(ii)
																				 );

			}
			return count;
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
	public int updateCoilunloadWlocCDA(String queryID, JDTORecord jrecord) throws EJBServiceException ,DAOException{
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			int count = 0;
			List RULE_ID5 	= (List)jrecord.getField("RULE_ID5");
			List SCH_CD5 		= (List)jrecord.getField("SCH_CD5");
			List RANKING5 		= (List)jrecord.getField("RANKING5");
			
			for(int ii =0; ii<RULE_ID5.size();ii++){
				

				count+= new YdSlabMoveBayRankingDAO().updateCoilunloadWlocCDA(queryID,
																				 ""+RANKING5.get(ii),
																				 ""+RULE_ID5.get(ii),
																				 ""+SCH_CD5.get(ii)
																				 );

			}
			return count;
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
	public List getListloadLotRanking_Bay_CG(String queryID) throws EJBServiceException ,DAOException{	
		try{
			
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			return new YdSlabMoveBayRankingDAO().getListData(queryID);
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
	public List getListloadLotRanking_UnloadBay_CG(String queryID) throws EJBServiceException ,DAOException{	
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			return new YdSlabMoveBayRankingDAO().getListData(queryID);
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
	public List getListloadLotRanking_Bay_CM(String queryID) throws EJBServiceException ,DAOException{	
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			return new YdSlabMoveBayRankingDAO().getListData(queryID);
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
	public List getListloadLotRanking_next_proc(String queryID) throws EJBServiceException ,DAOException{	
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			return new YdSlabMoveBayRankingDAO().getListData(queryID);
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
	public List getListloadLotRanking_Coil_Wloc_CD(String queryID) throws EJBServiceException ,DAOException{	
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			return new YdSlabMoveBayRankingDAO().getListData(queryID);
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
	public List getListloadLotRanking_steear_GP(String queryID) throws EJBServiceException ,DAOException{	
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			return new YdSlabMoveBayRankingDAO().getListData(queryID);
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
	public int updateCoilAppearGP(String queryID, JDTORecord jrecord) throws EJBServiceException ,DAOException{
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			int count = 0;
			List RULE_ID6 	= (List)jrecord.getField("RULE_ID6");
			List SCH_CD6 		= (List)jrecord.getField("SCH_CD6");
			List RANKING6 		= (List)jrecord.getField("RANKING6");
			
			for(int ii =0; ii<RULE_ID6.size();ii++){
				

				count+= new YdSlabMoveBayRankingDAO().updateCoilAppearGP(queryID,
																				 ""+RANKING6.get(ii),
																				 ""+RULE_ID6.get(ii),
																				 ""+SCH_CD6.get(ii)
																				 );

			}
			return count;
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
	public int updateCoilAppearGPA(String queryID, JDTORecord jrecord) throws EJBServiceException ,DAOException{
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			int count = 0;
			List RULE_ID6 	= (List)jrecord.getField("RULE_ID6");
			List SCH_CD6 		= (List)jrecord.getField("SCH_CD6");
			List RANKING6 		= (List)jrecord.getField("RANKING6");
			
			for(int ii =0; ii<RULE_ID6.size();ii++){
				

				count+= new YdSlabMoveBayRankingDAO().updateCoilAppearGPA(queryID,
																				 ""+RANKING6.get(ii),
																				 ""+RULE_ID6.get(ii),
																				 ""+SCH_CD6.get(ii)
																				 );

			}
			return count;
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
	public int updateCoilloadLotRanking(String queryID, JDTORecord jrecord) throws EJBServiceException ,DAOException{
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			int count = 0;
			List RULE_ID1 		= (List)jrecord.getField("RULE_ID1");
			List SCH_CD1 		= (List)jrecord.getField("SCH_CD1");
			List RANKING1 		= (List)jrecord.getField("RANKING1");
			
			for(int ii =0; ii<RULE_ID1.size();ii++){
				

				count+= new YdSlabMoveBayRankingDAO().updateCoilloadLotRanking(queryID,
																				 ""+RANKING1.get(ii),
																				 ""+RULE_ID1.get(ii),
																				 ""+SCH_CD1.get(ii)
																				 );

			}
			return count;
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
	public int updateCoilloadLotRankingA(String queryID, JDTORecord jrecord) throws EJBServiceException ,DAOException{
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			int count = 0;
			List RULE_ID1 	= (List)jrecord.getField("RULE_ID1");
			List SCH_CD1 		= (List)jrecord.getField("SCH_CD1");
			List RANKING1 		= (List)jrecord.getField("RANKING1");
			
			for(int ii =0; ii<RULE_ID1.size();ii++){
				

				count+= new YdSlabMoveBayRankingDAO().updateCoilloadLotRankingA(queryID,
																				 ""+RANKING1.get(ii),
																				 ""+RULE_ID1.get(ii),
																				 ""+SCH_CD1.get(ii)
																				 );

			}
			return count;
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}
	
	
	
	/**
	 * 오퍼레이션명 : 차량을 이용한 동간 이적 처리 (2009.01.05 KBK 추가)
	 * 업무 로직 
	 *   1. 저장품정보, 야드, 동, 하차위치, 차량 CardNo를 읽어와서
	 *      작업 예약을 처리
	 *   >> 차량카드번호와 상관없이 작업예약 처리. CGS 취소
	 *   >> 사용하지 않음.차량이적에 대한 코드는 그대로 두고 모두 다시 작성. 2009-06-23 CGS
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param sStockList
	 * @param sYdGp
	 * @param sBayGp
	 * @param unloadLoc
	 * @param CarCardNo
	 * @return 값 String
	 */
	public String setCarMoveProcess(String sStockList, String sYdGp,
			String sBayGp, String unloadLoc, String CarCardNo, String CarTotCnt, String CarArr ) {
		
		logger.println(LogLevel.DEBUG, this, "setCarMoveProcess() 시작========");	
		String alertString="";
		String firstwBookid ="";
		String sStockId[] = sStockList.split("-");
		
		YdStockDAO ydStockDAO = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO = new YdWBookDAO();
		CraneSchDAO craneSchDAO = new CraneSchDAO();;
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			logger.println(LogLevel.DEBUG, this, "=======차량 동간 이적 작업예약 생성 시작========");	
			for (int i = 0; i < sStockId.length; i++) {
			
				/*
				 * 차량 카드 번호  등록(출하 이송 등록 시뮬레이션)
				 */			
				String sCarCardNo = CarCardNo;

				//String sTransWordDate=YmCommonUtil.getStringYMDHM();
				// 운송작업지시번호는 출하와 충돌 위험이 있으므로 형식을 변경한다. 년월일+카드번
				//String sTransWordDate=YmCommonUtil.getStringYMDHM();
				String sTransWordDate=YmCommonUtil.getStringYMD();
				String sTransWordSeqno=CarCardNo;
				//String sStockMoveTerm = "EC";
				//String sStockMoveTerm = YmCommonConst.NEW_STOCK_MOVE_TERM_EC;		// (이송작업대기)로 처리.
				// 이동조건 변경 EC -> CS  
				String sStockMoveTerm = YmCommonConst.NEW_STOCK_MOVE_TERM_CS;		// (이송대기)로 처리.

 
				
				int resNum = ydStockDAO.updateStockTransInfo_01(sStockId[i],
						sCarCardNo,
						sTransWordDate,
						sTransWordSeqno,
						sStockMoveTerm); 
				
				/*
				 * 적치단 상태 업데이트
				 */
				//-- 적치단  Table Update(작업요구상태='S'로 변경)
				//UPDATE TB_YM_STACKLAYER 
				//   SET STACK_LAYER_STAT = :stackstat
				//		, MODIFIER = 'SYSTEM'
				//		, MOD_DDTT = SYSDATE
				// WHERE STOCK_ID = :stockid
				//   AND STACK_LAYER_STAT IN ('S','L','U')
				logger.println(LogLevel.DEBUG, this, "차량동간이적: 적치 단 상태 업데이트");	
				String sStkLayerQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark_01";
				int stkColGp = ydStackLayerDAO.requestupdateData(sStkLayerQueryId, new Object[] { YmCommonConst.STACK_LAYER_STAT_S, sStockId[i] });

				// 작업예약(TB_YM_WBOOK) Table Select WBOOK_ID Key 생성 Seq + 1
				String wBookQueryId = "ym.steelinfo.steelinforecv.dao.YdStockDAO.getWBookID";
				JDTORecord wBookSel = ydStackLayerDAO.requestFind(wBookQueryId);
				String wBookid = wBookSel.getFieldString("WBOOK_ID");
				
				if(i == 0 ) firstwBookid = wBookid ;
				
				logger.println(LogLevel.DEBUG, this, "차량동간이적: 작업예약ID["+wBookid+"]");	
				/*
				 * 차량 이적 작업 예약
				 */
				// 코일소재인 경우는 소재이송상차
				// 코일제품인 경우는 제품이송상차 로 처리한다.
				String sSchCode = "";
				String sStockItem = "";
				
				// 저잠품 품목 정보를 확인한다. 
				String sQueryId_stockitem="ym.steelinfo.steelinforecv.dao.YdStockDAO.selectStockItemList";
				JDTORecord jtrStockItem = ydStockDAO.getData(sQueryId_stockitem, new Object[]{sStockId[i]});
				sStockItem = jtrStockItem.getFieldString("STOCKITEM");
				
				// 작업예약 단계에서는 대표적인 스케줄 코드로 처리함. 
				// 차량도착 등록시 좌우 도착위치에 따라서 스케줄을 변경함. 최규성 2009-11-20
				if (sStockItem.equals("CM")	){
					if(CarArr.equals("1")||CarArr.equals("2")){
						//좌(L)스케줄
						sSchCode = YmCommonConst.NEW_SCH_WORK_KIND_CVM6;	// 소재차량이송상차 스케줄 코드 사용.
					}else{
						//우(R)스케줄
						sSchCode = YmCommonConst.NEW_SCH_WORK_KIND_CVM8;	// 소재차량이송상차 스케줄 코드 사용.	
					}
				}else if(sStockItem.equals("CG")){
					if(CarArr.equals("1")||CarArr.equals("2")){
						//좌(L)스케줄
						sSchCode = YmCommonConst.NEW_SCH_WORK_KIND_GVM6;	// 제품차량이송상차 스케줄 코드 사용.
					}else{
						//우(R)스케줄
						sSchCode = YmCommonConst.NEW_SCH_WORK_KIND_GVM8;	// 제품차량이송상차 스케줄 코드 사용.
					}
				}
				
				String toBayGp = unloadLoc.substring(1,2);
				logger.println(LogLevel.DEBUG, this, "차량동간이적: 차량이적작업예약 schcode:"+sSchCode+", toBayGp:"+toBayGp);
				
				/*
				 -- 작업예약(TB_YM_WBOOK) Table Insert(Yard 구분, 동구분, 작업예약일시, 작업예약조, 등록자, 등록일시)
				INSERT INTO TB_YM_WBOOK (
				            WBOOK_ID, 
				            YD_GP, 
				            BAY_GP,
				            SCH_WORK_KIND, 
				            SCH_WORK_LOC_DECISION_METHOD,
				            CRANE_WORD_PUT_LOC, 
				            WBOOK_DDTT, 
				            WBOOK_DUTY,
				            WBOOK_PARTY, 
				            WBOOK_SCH_TERM, 
				            WBOOK_SCH_ACT_DDTT,
				            REGISTER, 
				            REG_DDTT, 
				            MODIFIER, 
				            MOD_DDTT, 
				            DEL_YN)
				VALUES (?, ?, ?, ?, 'S', null, to_char(sysdate,'YYYYMMDDHH24MI'), ?, ?, 'T', to_char(sysdate,'YYYYMMDDHH24MI'),
				              'SYSTEM', sysdate, null, null, 'N')
				 */
				String sWBookQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.InsertYdWBook";
				int wbookSeq = ydWBookDAO.requestinsertData(sWBookQueryId,
						new Object[] { wBookid, sYdGp, sBayGp, sSchCode,
						YmCommonUtil.getWorkDuty(),
						YmCommonUtil.getWorkParty() });

				// 저장품 Table(TB_YM_STOCK)에 작업예약과 관련된 정보(WBOOK_ID,CARUNLOAD_BAY,CARUNLOAD_PUT_LOC)를 Update 한다.
				logger.println(LogLevel.DEBUG, this, "차량동간이적: 저장품정보수정"+String.valueOf(i)+":"+wBookid+", "+toBayGp+", "+unloadLoc+",방향; "+CarArr+", "+sStockId[i]);
				/*
				UPDATE TB_YM_STOCK 
				    SET WBOOK_ID          = :wbook_id, 
				        CARUNLOAD_BAY     = :carunload_bay,
				        CARUNLOAD_PUT_LOC = :carunload_put_loc,
				        CTS_RELAY_BAY     = :CarTotCnt
				        CTS_RELAY_SADDLE =:CarArr
				     WHERE STOCK_ID = :stock_id
				 */
				String stkQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId_13";
				int stockSeq = ydStockDAO.requestupdateData(stkQueryId, new Object[] {
						wBookid, toBayGp, unloadLoc,CarTotCnt ,CarArr, sStockId[i] });
				
				logger.println(LogLevel.DEBUG, this, "=======차량 동간 이적 작업예약 생성 끝========");
			}	// for - END
			
			//작업예약에 해당동 상차 스케줄이 존재 하는 경우 크레인 스케줄 호출 SKIP
			String sQueryId_EmptyBay = "ym.steelinfo.steelinforecv.dao.YdStockDAO.getWBookSchskip";
			List listCoilPos = craneSchDAO.getListData(sQueryId_EmptyBay, new Object[] {firstwBookid});
		 
			logger.println(LogLevel.DEBUG, this, "listCoilPos.size()"+ listCoilPos.size());
			if (listCoilPos.size() <= 0) {
			
				if(!firstwBookid.equals("")){
					//크레인 스케줄 호출 ---------------------------------------------------------------
					EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
					isSuccess = (Boolean)ejbConn.trx("callCraneSchInfo",new  Class[]{String.class},
																		new Object[]{firstwBookid});
					}
			}
			
		}catch(DAOException daoe){
			alertString 	= "이적처리 중 DAO 오류 발생";
	        throw daoe;
	    }catch(Exception e){
	    	alertString 	= "이적처리 중 오류 발생";
	        throw new EJBServiceException(e);
	    }    
	    
	    alertString 	= "이적처리가 정상적으로 등록되었습니다.";  
		return alertString;
	}

	// 차량이적작업에 대한 작업예약을 생성한다.
	// String sStockList_IN	: 이적대상재의 코일 번호를 저장한다.
	// String sFromYdGp_IN		: 야드 정보
	// String sFromBayGp_IN		: 적치 대 정보
	// String sUnloadLoc_IN	: 목적동
	// String sCarNo_IN		: 이적 차량 번호
	// String sSchCode_IN	: 스케줄코드
	public String setCarMoveProc(String sStockList_IN		// 저장품
								,String sFromYdGp_IN		// 현재 야드
								,String sFromBayGp_IN		// 현재동
								,String sUnloadLoc_IN		// 목적동
								,String sCarNo_IN			// ""처리. 필요하지 않다.
								,String sSchCode_IN			// 스케줄 코드 CTRL
	)
	{
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		logger.println(LogLevel.DEBUG, this, "=======setCarMoveProc() 시작========");	
		logger.println(LogLevel.DEBUG, this, "========sStockList  :" + sStockList_IN);	
		logger.println(LogLevel.DEBUG, this, "========sFromYdGp   :" +sFromYdGp_IN);	
		logger.println(LogLevel.DEBUG, this, "========sFromBayGp  :" +sFromBayGp_IN);	
		logger.println(LogLevel.DEBUG, this, "========sUnloadLoc  :" +sUnloadLoc_IN);	
		logger.println(LogLevel.DEBUG, this, "========sCarNo      :" +sCarNo_IN);	
		logger.println(LogLevel.DEBUG, this, "========sSchCode    :" +sSchCode_IN);	


		// 처리상태를 저장한다. 반환문자열.
		String alertString = "";

		// 이적 대상재에 대한 코일번호를 저장한다.
		String sStockId[] = sStockList_IN.split("-");


		YdStockDAO ydStockDAO = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO = new YdWBookDAO();

		try
		{
			
			logger.println(LogLevel.DEBUG, this, "=======차량 동간 이적 작업예약 생성 시작========");
			ymCommonDAO dao = ymCommonDAO.getInstance();
			// 요구가 들어온 이적대상재의 수만큼 반복 처리
			for (int i=0;i<sStockId.length ;i++ )
			{
				// 저장품 검사
				// STOCK TBL에 저장품이 있는지 확인한다.
				String sQueryId3	  = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.getStackLayerCheck1";
				JDTORecord stockJr    = dao.getCommonInfo(sQueryId3, new Object[]{sStockId[i]});

				if(stockJr == null){
					alertString = "저장품DB에 이적하려는 대상이 없습니다.";
					logger.println(LogLevel.DEBUG, this, "=======저장품DB에 이적하려는 대상이 없습니다.");
				}else{
					String sTransWordDate = YmCommonUtil.getStringYMDHM();
					String sStockMoveTerm = "";//YmCommonConst.NEW_STOCK_MOVE_TERM_CR;	// COIL차량이적(CTRL)으로 처리 -> 도착처리시 변경됨.
					//============================================================================================
					// 작업예약ID를 가져온다.
					//============================================================================================
					String sQueryId_wbook	= "ym.steelinfo.steelinforecv.dao.YdStockDAO.getWBookID";
					JDTORecord wbookJr = dao.getCommonInfo(sQueryId_wbook, new Object[]{});
						
					String sWbookId = StringHelper.evl(wbookJr.getFieldString("WBOOK_ID"),"");
					
					
					String sYdGp = sUnloadLoc_IN.substring(0,1);
						
					String sToLoc = sUnloadLoc_IN.substring(1,2);
					String sWorkGbn = YmCommonConst.SCH_WORK_LOC_DECISION_METHOD_S;		// 위치지정방법 "S"로 지정.
					String sModifier = "SYSTEM";
					// 이적용차량 도착처리시에 해당차량의 스케줄 코드로 변경함.
					// 차량이적상차에 대한 대표 스케줄 코드 의미로 CTRL을 사용함.
					String sSchCode ="";// YmCommonConst.NEW_SCH_WORK_KIND_CTRL;				

					//============================================================================================
					// WBOOK TBL에 작업예약정보를 등록한다.
					//============================================================================================
					logger.println(LogLevel.DEBUG, this, "==== WBOOK TBL 작업예약 등록 ");	
					// -- 작업예약(TB_YM_WBOOK) Table Insert(Yard 구분, 동구분, 작업예약일시, 작업예약조, 등록자, 등록일시)
					//INSERT INTO TB_YM_WBOOK (
					//				WBOOK_ID 
					//				,YD_GP
					//				,BAY_GP
					//				,SCH_WORK_KIND
					//				,SCH_WORK_LOC_DECISION_METHOD
					//				,CRANE_WORD_PUT_LOC
					//				,WBOOK_DDTT
					//				,WBOOK_DUTY
					//				,WBOOK_PARTY 
					//				,WBOOK_SCH_TERM
					//				,WBOOK_SCH_ACT_DDTT
					//				,CARUNLOAD_BAY
					//				,REGISTER
					//				,REG_DDTT 
					//				,MODIFIER 
					//				,MOD_DDTT 
					//				,DEL_YN
					//				)
					//VALUES (?, ?, ?, ?, 'S', null, to_char(sysdate,'YYYYMMDDHH24MI'), ?, ?, 'O', to_char(sysdate,'YYYYMMDDHH24MI'),?,
					//		'SYSTEM', sysdate, null, null, 'N')					
					String sWBookQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.CarMove_InsertYdWBook";
					int wbookSeq = ydWBookDAO.requestinsertData(sWBookQueryId,
												new Object[] { sWbookId, sFromYdGp_IN, sFromBayGp_IN, sSchCode,
																YmCommonUtil.getWorkDuty(),
																YmCommonUtil.getWorkParty(), sToLoc});

					//============================================================================================
					// STOCK TBL에 작업예약정보 수정하기
					//============================================================================================
					logger.println(LogLevel.DEBUG, this, "==== STOCK TBL 작업예약 등록 ");	
					logger.println(LogLevel.DEBUG, this, "==== sWbookId "+sWbookId);	
					logger.println(LogLevel.DEBUG, this, "==== sToLoc " + sToLoc);	
					logger.println(LogLevel.DEBUG, this, "==== sUnloadLoc_IN "+sUnloadLoc_IN);	
					logger.println(LogLevel.DEBUG, this, "==== sStockId "+sStockId[i]);	
					//-- 작업예약ID와 하차위치를 업데이트
					//	UPDATE TB_YM_STOCK 
					//	SET WBOOK_ID          = :wbook_id, 
					//		CARUNLOAD_BAY     = :carunload_bay,
					//		CARUNLOAD_PUT_LOC = :carunload_put_loc
					//	 WHERE STOCK_ID = :stock_id
					//
					//String sQueryId_Stock = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId_02";
					String sQueryId_Stock = "ym.steelinfo.steelinforecv.dao.YdStockDAO.updateStockWBookID";
					//update TB_YM_STOCK
					//   set WBOOK_ID = ?  --작업예약ID
					//     , CTS_RELAY_YN = ?    --CTS중계구분(Y);동내이적은무조건 NULL로셋팅
					//     , CTS_RELAY_BAY = ?   --CTS중계동(E);동내이적은무조건 NULL로셋팅
					//     , CARUNLOAD_YD = ?    --하차야드(1);동내이적은NULL로셋팅
					//     , CARUNLOAD_BAY = ?   --하차동(F);이적할TO동;동내이적은NULL로셋팅
					//     , CARUNLOAD_PUT_LOC = ?   --하차PUT위치;싱글동간일때는 10자리, 멀티동간일때는 1자리셋팅
					//     , CAR_CARD_NO = ?      --카드번호(B열연 팔레트로 동간이적할때만 등록)
					//     , MODIFIER = ?
					//     , MOD_DDTT = sysdate
					//where  STOCK_ID = ?       --저장품ID
					int stockSeq = ydStockDAO.requestupdateData(sQueryId_Stock, 
																new Object[] {sWbookId,"","", sYdGp, sToLoc, sToLoc,"",sModifier, sStockId[i] });

					//============================================================================================
					// STACKLAYER TBL에 작업예약 상태를 변경한다.
					//============================================================================================
					logger.println(LogLevel.DEBUG, this, "==== STACKLAYER TBL 저장품 적치상태 변경.등록 ");	
					//--적치상태변경
					//
					//update TB_YM_STACKLAYER 
					//   set STACK_LAYER_STAT = 'S'
					//	  , MODIFIER = ?
					//	  , MOD_DDTT = sysdate   
					//where  STOCK_ID = ?     --저장품ID
					//AND STACK_LAYER_STAT IN ('L')
					String sQueryId_layer = "ym.steelinfo.steelinforecv.dao.YdStockDAO.updateStockStat";
					int stacklayerSeq = ydStackLayerDAO.requestupdateData(sQueryId_layer, new Object[]{sModifier, sStockId[i]});

					alertString 	= "이적처리가 정상적으로 등록되었습니다.";  
				}
			}
			logger.println(LogLevel.DEBUG, this, "=======차량 동간 이적 작업예약 생성 끝========");
		}
		catch (DAOException daoe)
		{
			alertString 	= "이적처리 중 DAO 오류 발생하였습니다.";
	        throw daoe;
		}
		catch (Exception e)
		{
			logger.println(LogLevel.DEBUG, this, "=======setCarMoveProc() 예외 발생. ========");	
			alertString = "작업 처리 중 에러가 발생하였습니다.";
			throw new EJBServiceException(e);
		}
		logger.println(LogLevel.DEBUG, this, "=======setCarMoveProc() 끝 ========");	
		return alertString;
	}
	// 최규성
	/**
	 * 오퍼레이션명 : 차량이적시 대상재의 품목을 판별한다. 소재와 제품을 동시에 이적하지 못하도록 한다. 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean checkStockItem(String sStocks){
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		boolean bRet = false;
		logger.println(LogLevel.DEBUG, this, "checkStockItem() 시작========");	
		String alertString="";
		
		String sStockId[] = sStocks.split("-");
		List listData = new ArrayList();
		JDTORecord jtrStockInfo = null;
		YdStockDAO ydStockDAO = new YdStockDAO();
		String sStockItem01 = "";
		String sStockItem = "";
		try{
			listData.clear();
			/*
			 SELECT NVL(STOCK_ITEM , '') AS STOCKITEM
			   FROM TB_YM_STOCK
			  WHERE STOCK_ID = :stockid
			 */
			String sQueryId_stockitem = "ym.steelinfo.steelinforecv.dao.YdStockDAO.selectStockItemList";
			for(int i=0; i<sStockId.length; i++){
				jtrStockInfo = ydStockDAO.getData(sQueryId_stockitem, new Object[]{sStockId[i]}); 
//				listData.add(jtrStockInfo);
				
				// JDTORecord의 값이 있으면 처리한다.
				// 구한 데이터의 stock_item 정보를 비교한다.
				if (jtrStockInfo != null){
					sStockItem01 = jtrStockInfo.getFieldString("STOCKITEM");
					if(sStockItem.equals("")){
						sStockItem = sStockItem01;
						logger.println(LogLevel.DEBUG, this, "checkStockItem() 첫소재=======true:나머지비교");
						bRet = true;
					}else{
						if( sStockItem.compareTo(sStockItem01) == 0 ){
							logger.println(LogLevel.DEBUG, this, "checkStockItem() 동일한소재=======true:나머지비교");
							bRet = true;
						}else{
							logger.println(LogLevel.DEBUG, this, "checkStockItem() 다른 소재========false:break;");
							bRet = false;
							break;
						}
					}
				}else{
					alertString = "코일 번호의 데이터가 존재하지 않습니다.";
					logger.println(LogLevel.DEBUG, this, alertString);
					bRet = false;
					break;
				}
			}
				
		}catch(DAOException daoe){
			alertString 	= "";
	        throw daoe;
		}catch (Exception e){
			logger.println(LogLevel.DEBUG, this, "=======checkStockItem() 예외 발생. ========");	
			alertString = "";
			throw new EJBServiceException(e);
		}
		return bRet;
	}

      /**
	 * 오퍼레이션명 : 산적 LOT관리 -> A열연 SLAB야드 추가 ->이송상차 지시
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
    public boolean produtPerSpanSvml(String yd_gp,				//야드 구분 
						    		 String s_donggubun, 		//동
						    		 String s_spangubun,		//스판
						    		 String s_colsgubun,		//열
						    		 String temp_stock_id,		//저장품 ID
						    		 String modifier 			//수정자 
						    		 ) throws EJBServiceException ,DAOException {
    	boolean isSucess = true;    	
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			logger.println(LogLevel.DEBUG,this, "A열연 SLAB야드 이송상차 작업예약 생성 :"+temp_stock_id);
			String returnmsg ="";
			returnmsg = setStockMoveList(yd_gp										//야드구분
										,s_donggubun								//동구분(동간이적시 목적동)
										,YmCommonConst.EQUIP_KIND_PT				//스판구분	PALLET로 설정							
										,""											//열구분
										,s_donggubun								//동구분(현재동)
										,"O"										//동간(S),동내(O),PT(T) 
										,""											//
										,YmCommonConst.NEW_SCH_WORK_KIND_SVML		//스케쥴코드
										,temp_stock_id								//저장품 배열
										,modifier);									//수정자
			
			if(returnmsg.indexOf ("정상적") == -1){
				isSucess = false;	
			}
			/*
			SELECT WBOOK_ID
			  FROM (SELECT STOCK.STOCK_ID, LAYER.STACK_BED_GP, LAYER.STACK_LAYER_GP, WBOOK.WBOOK_ID 
					FROM TB_YM_WBOOK WBOOK, TB_YM_STOCK STOCK, TB_YM_STACKLAYER LAYER
					WHERE YD_GP = ?
					AND WBOOK.WBOOK_ID = STOCK.WBOOK_ID
					AND LAYER.STOCK_ID = STOCK.STOCK_ID
					AND LAYER.STACK_LAYER_STAT IN ('L','S')
					AND WBOOK.SCH_WORK_KIND = ? 
					ORDER BY LAYER.STACK_BED_GP ASC, LAYER.STACK_LAYER_GP DESC
					) TEMP
			WHERE ROWNUM <= (SELECT COUNT(*)
			FROM TB_YM_STACKLAYER
			WHERE STACK_COL_GP LIKE ? ||'%'
			AND STOCK_ID IS NULL
			AND STACK_LAYER_ACTIVE_STAT = 'O')
			AND EXISTS(SELECT * 
			            FROM TB_YM_EQUIP
						WHERE EQUIP_GP LIKE ?||'%'
						AND WPROG_STAT = 'W'
						)
			*/
			List listWook = new YdWBookDAO().requestgetListData("ym.steelinfo.steelinforecv.YdWBookDAO.selectWbook_id"
																,new Object[]{YmCommonConst.NEW_SCH_WORK_KIND_SVML
																,yd_gp+s_donggubun+YmCommonConst.EQUIP_KIND_PT
																,yd_gp+s_donggubun+YmCommonConst.EQUIP_KIND_CR});
			
			String Wbook_id = "";
			for(int ii=0; ii < listWook.size();ii++){
				JDTORecord jRecrod = (JDTORecord) listWook.get(ii);
				
				Wbook_id= Wbook_id + jRecrod.getFieldString("WBOOK_ID")+"-";
			}
			
			if(listWook.size()> 0){
				logger.println(LogLevel.DEBUG,this, "이송상차 작업예약 생성 후 Pallet 정보 있으면 스케쥴 Call"+ Wbook_id);
				EJBConnector ejbConn = new EJBConnector("default", "JNDICraneSchReg", this);
				ejbConn.trx("syCraneScheduleInfoInsert", new Class[] { String.class }, new Object[] {Wbook_id});
			}else{				
				logger.println(LogLevel.DEBUG,this, "이송상차 작업예약 생성 후 Pallet 정보 없으므로 스케쥴 Call하지 않음");
			}

			return isSucess;
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}
    
    
    
    /**
	 * 오퍼레이션명 : 산적 LOT관리 -> A열연 SLAB야드 추가 ->이송상차 지시
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
    public boolean produtPerSpanSvml_Bslab(String yd_gp,				//야드 구분 
						    		 String s_donggubun, 		//동
						    		 String s_spangubun,		//스판
						    		 String s_colsgubun,		//열
						    		 String temp_stock_id,		//저장품 ID
						    		 String modifier, 			//수정자 
						    		 String s_equip 			//장비종류
						    		 ) throws EJBServiceException ,DAOException {
    	boolean isSucess = true;    	
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			logger.println(LogLevel.DEBUG,this, "B열연 SLAB야드 이송상차 장비종류 :"+s_equip);
			String equip_Gp = "";
		
			if(s_equip.equals("PT"))
			{
				equip_Gp = YmCommonConst.EQUIP_KIND_PT;
			}else if(s_equip.equals("TR"))
			{
				equip_Gp = YmCommonConst.EQUIP_KIND_TR;
			}
			
			logger.println(LogLevel.DEBUG,this, "B열연 SLAB야드 이송상차 작업예약 생성 :"+temp_stock_id);
			String returnmsg ="";
			returnmsg = setStockMoveList(yd_gp										//야드구분
										,s_donggubun								//동구분(동간이적시 목적동)
										,equip_Gp				//스판구분								
										,""											//열구분
										,s_donggubun								//동구분(현재동)
										,"O"										//동간(S),동내(O),PT(T) 
										,""											//
										,YmCommonConst.NEW_SCH_WORK_KIND_SVML		//스케쥴코드
										,temp_stock_id								//저장품 배열
										,modifier);									//수정자
			
			if(returnmsg.indexOf ("정상적") == -1){
				isSucess = false;	
			}
			/*
			SELECT WBOOK_ID
			  FROM (SELECT STOCK.STOCK_ID, LAYER.STACK_BED_GP, LAYER.STACK_LAYER_GP, WBOOK.WBOOK_ID 
					FROM TB_YM_WBOOK WBOOK, TB_YM_STOCK STOCK, TB_YM_STACKLAYER LAYER
					WHERE YD_GP = ?
					AND WBOOK.WBOOK_ID = STOCK.WBOOK_ID
					AND LAYER.STOCK_ID = STOCK.STOCK_ID
					AND LAYER.STACK_LAYER_STAT IN ('L','S')
					AND WBOOK.SCH_WORK_KIND = ? 
					ORDER BY LAYER.STACK_BED_GP ASC, LAYER.STACK_LAYER_GP DESC
					) TEMP
			WHERE ROWNUM <= (SELECT COUNT(*)
			FROM TB_YM_STACKLAYER
			WHERE STACK_COL_GP LIKE ? ||'%'
			AND STOCK_ID IS NULL
			AND STACK_LAYER_ACTIVE_STAT = 'O')
			AND EXISTS(SELECT * 
			            FROM TB_YM_EQUIP
						WHERE EQUIP_GP LIKE ?||'%'
						AND WPROG_STAT = 'W'
						)
			*/
			List listWook = new YdWBookDAO().requestgetListData("ym.steelinfo.steelinforecv.YdWBookDAO.selectWbook_id"
																,new Object[]{YmCommonConst.NEW_SCH_WORK_KIND_SVML
																,yd_gp+s_donggubun+equip_Gp
																,yd_gp+s_donggubun+YmCommonConst.EQUIP_KIND_CR});
			
			String Wbook_id = "";
			for(int ii=0; ii < listWook.size();ii++){
				JDTORecord jRecrod = (JDTORecord) listWook.get(ii);
				
				Wbook_id= Wbook_id + jRecrod.getFieldString("WBOOK_ID")+"-";
			}
			
			if(listWook.size()> 0){
				logger.println(LogLevel.DEBUG,this, "이송상차 작업예약 생성 후 장비 정보 있으면 스케쥴 Call"+ Wbook_id);
				EJBConnector ejbConn = new EJBConnector("default", "JNDICraneSchReg", this);
				ejbConn.trx("syCraneScheduleInfoInsert", new Class[] { String.class }, new Object[] {Wbook_id});
			}else{				
				logger.println(LogLevel.DEBUG,this, "이송상차 작업예약 생성 후 장비 정보 없으므로 스케쥴 Call하지 않음");
			}

			return isSucess;
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}
    
    
    
    
    
    // 최규성 작성중
	/**
	 * 오퍼레이션명 : 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */  
	public List checkExtHindrance(JDTORecord jtrSTOCK_IN,String sCOIL_NO_IN) throws EJBServiceException ,DAOException{
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			String queryID="";
			List listData=new ArrayList();
			YdStockDAO ydstockDAO = new YdStockDAO();
			listData.clear();
//			return ydstockDAO.getListData(queryID, listData);
			return listData;
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}
    // 최규성 작성중
	/**
	 * 오퍼레이션명 : 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */  
	public boolean modifyExtBranchCode(List listData, String queryID) throws EJBServiceException ,DAOException{
		boolean bResult = true; 
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
//			YdStockDAO ydstockDAO = new YdStockDAO();
//			return ydstockDAO.getListData(queryID, listData);
			return bResult;
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}
/**
 * 최규성.
 * SPM2 추가 사항 처리를 위한 메소드 추가. 함수오버로딩.
 * 기존 사용 메소드랑 동일한 이름. 설비구분인자만 추가.
 * 추가 메소드 :
 * 		setInnerIFCoilInfo_07(String sGoodsNo, String sYardId, String sFacilityGp)  : SPM 재작업 처리.
 * 		setInnerIFCoilInfo_05(String sGoodsNo,String sFacilityGp) : SPM2 모 Coil 종료
 * 		insertUpPutWrslRtData(String sStockId, String sYdGp, String sFacilityGp) : SPM2 모Coil 종료시 정보를 실적 처리한다
 * 		setInnerIFCoilInfo_06(String sGoodsNo,String sFacilityGp) : 자 Coil 실적을 처리
 * */

	// SPM 재작업 처리.
	/**
	 * 오퍼레이션명 : 
	 *  
	 * 최규성. 정확한 처리방법은 협의 필요.
		* SPM2 재작업. 기존 SPM 재작업 코드를 활용함.
		*
		* param String	: 저장품ID
		* param String	: 야드구분
		* param String	: 설비 구분. SPM2("N")일 경우만 사용 
		* 수신이 되면 대상재가 출측에 있다면 
		* 입측 D5로 보내고 조업으로 보급완료실적을 송신
		* 대상재가 입측에 있다면 그대로 두고 보급완료실적을 송신
		*
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */   
	public boolean setInnerIFCoilInfo_07(String sGoodsNo, String sYardId, String sFacilityGp){
		boolean isVal = false;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			int iSeq 			= 0;
			
			CraneSchDAO dao	= new CraneSchDAO();
				
			logger.println(LogLevel.DEBUG,this,"=============SPM2 재작업 처리 시작========");
			/**
			 *	1.	A,B열연 SPM 입,출측 셋팅
			 */
				String sIStackColGp   = "";
				String sOStackColGp1  = "";
				String sOStackColGp2  = "";
				if(YmCommonConst.YD_GP_3.equals(sYardId) ) {
					if(YmCommonConst.NEW_WORK_SPM_N.equals(sFacilityGp) ) {
						// SPM2 설비 적치열 설정
						sIStackColGp   = YmCommonConst.SPM_COL_3DKE+YmCommonConst.STACK_BED_GP_01;
						sOStackColGp1  = YmCommonConst.SPM_COL_3EKD+YmCommonConst.STACK_BED_GP_01;
						sOStackColGp2  = YmCommonConst.SPM_COL_3EKD+YmCommonConst.STACK_BED_GP_01;
					}
					logger.println(LogLevel.DEBUG,this,"sOStackColGp1	="+sOStackColGp1);
					logger.println(LogLevel.DEBUG,this,"sOStackColGp2	="+sOStackColGp2);
					logger.println(LogLevel.DEBUG,this,"sIStackColGp	="+sIStackColGp);
					logger.println(LogLevel.DEBUG,this,"sGoodsNo		="+sGoodsNo);
				}

			/**
			 *	2.	출측에 저장품이 존재하는지 체크
			 */
				JDTORecord curBedV = null;
				/*
					SELECT 
						   *
					FROM tb_ym_stacklayer
					WHERE stack_col_gp  = :stack_col_gp
					AND   stock_id		= :stock_id 
				*/
				curBedV = dao.getStackLayerInfoWithStockId(sOStackColGp1,sGoodsNo);

				if(curBedV == null){
					curBedV = dao.getStackLayerInfoWithStockId(sOStackColGp2,sGoodsNo);
				}
				logger.println(LogLevel.DEBUG,this,"출측 저장품 정보 확인="+curBedV);		
			/**
			 *	3.	출측에 저장품이 있으면
			 *		출측정보 삭제 후,입측정보 생성.
			 */
				if(curBedV != null){
					
					iSeq = YmCommonDB.deleteConveyorInfo(sOStackColGp1,
														 sGoodsNo);
					if(iSeq < 1){
					iSeq = YmCommonDB.deleteConveyorInfo(sOStackColGp2,
														 sGoodsNo);   			
					}   		
					if(iSeq > 0){				  			 
					iSeq = YmCommonDB.insertConveyorInfo(sIStackColGp, 
														 sGoodsNo,
														 YmCommonConst.GBN_MAX);   	
					}					  				 
					logger.println(LogLevel.DEBUG,this,"출측 삭제, 입측 추가");	
				}	   				
																								 
			/**
			 *	4.	보급완료 실적을 송신
			 *	공정구분 및 위치포지션을 변경한다. 위치포지션은?? 헙의필요
			 */
				if(curBedV != null){
						
					YMPO161 model = new YMPO161();
					model.setTcCode(YmCommonConst.MODEL_YMPO161);
					model.setTcDate(YmCommonUtil.getCurDate("yyyy-MM-dd"));
					model.setTcTime(YmCommonUtil.getCurDate("HH-mm-ss"));
					
					/* 권하일자	CHAR(8)  yyyymmdd	*/
					model.setdownDate(YmCommonUtil.getCurDate("yyyyMMdd"));
					
					/* 권하시각     CHAR(6)  HHMMSS */
					model.setdownTime(YmCommonUtil.getCurDate("HHmmss"));
					
					/* 공장구분	CHAR(1)  A:A열연, B:B열연*/
					model.setplantGbn(YmCommonConst.YD_GP_1.equals(sYardId)?"A":"B");
					
					/* 공정구분	CHAR(1)  H : Hot Filnal, S :  SkinPass	*/
					//model.setprocGbn("S");
					model.setprocGbn("N");
					
					/* COIL번호	CHAR(11) */
					model.setcoilNo(sGoodsNo);
					
					/* 처리구분     CHAR(1)  1:보급,2:보급취소,3:추출,4:Take-Out,5:Take-In */
					model.setProcessId("5");
					  
					/* 위치포지션  CHAR(2)  */
					model.setpositionNo(YmCommonConst.PO_POSITION_D5);
					
					EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
					Boolean isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new  Class[]{CommonModel.class},
																				 new Object[]{model});
					logger.println(LogLevel.DEBUG,this, "내부IF호출===SPM2 재작업 실적송신.===");
					

					//품질 열연정정입측보급실적----------------------------------------------
					YdDelegate      ydDelegate      = new YdDelegate();
					JDTORecord recInTemp	=null;
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("MSG_ID",      	"YDQMJ002"); 							 
					recInTemp.setField("STL_NO",     	sGoodsNo);	  			//재료번호				
					ydDelegate.sendMsg(recInTemp);
			 
					logger.println(LogLevel.DEBUG,this, "품질 L3 열연정정입측보급실적 전송 송신 완료7"); 
					//-------------------------------------------------------------------
					
				}
			
			logger.println(LogLevel.DEBUG,this,"=============SPM2 재작업 처리 종료========");				            
			isVal = true; 
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}    
		 
		return isVal;
	}



		/**
		 * 오퍼레이션명 : 
		 *  
		 * 최규성
			* 기존 setInnerIFCoilInfo_05(String) 메소드를 변경함. 함수오버로딩
			* SPM2 모 Coil 종료
			*
			* param String	: 저장품ID
			* param String	: 야드구분
			*
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param 
		 * @return
		 * @throws 
		 */     
	    public boolean setInnerIFCoilInfo_05(String sGoodsNo,String sFacilityGp){
			
			boolean isVal = false;
			
			try{
				/*
				 * 구자원 단계별 삭제 로직  
				 */
				String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
				if(sAPP060_OLDSRC_YN.equals("Y")){
					return false;
				}
				
				JDTORecord stockV 	= null;
				JDTORecord poV 		= null;
				
				int iSeq 			= 0;
			    
				YdStockDAO dao	= new YdStockDAO();
				
				String sStockStat	  = "";
				String sStockMoveTerm = "";
				String sMsg 		  = "";
				
				logger.println(LogLevel.DEBUG,this,"=============모 Coil 종료 시작========");    
			    /**
			     *	1.	공통 Coil정보를 가져온다.
			     */
				    stockV	= dao.getCoilCommonInfo(sGoodsNo);
				    
				    if(stockV == null){ 
				    	logger.println(LogLevel.DEBUG,this,"NO HAVE COMMON COIL DATA");
				    	throw new EJBServiceException("=모 Coil 종료=>NO HAVE COMMON COIL DATA");
				    }
				    
				    String sPlantGp     = StringHelper.evl(stockV.getFieldString("공장구분"),"");	
				    String sCurrprogCd  = StringHelper.evl(stockV.getFieldString("CURR_PROG_CD"),"");	
				    
				    
					/**
					 * 2. Coil 입고실적 (출하로 제품입고실적 송신 YMDM001) 공통 진도 Code가 입고대기 H 이면 출하로
					 * 입고실적송신
					 */
				   	//************************************************************************************
				   	//************************************************************************************
					{
						/*
						SELECT   A.COIL_NO AS GOODS_NO  
						        ,A.RECEIPT_DATE  
						        ,TO_CHAR(A.COIL_CREATE_DDTT,'hh24MISS') AS RECEIPT_TIME  
						        ,SUBSTR(B.STACK_COL_GP,1,1) AS YD_GP  
						        ,B.STACK_COL_GP||B.STACK_BED_GP||B.STACK_LAYER_GP AS STORE_LOC  
						 FROM USRPTA.TB_PT_HRPLATECOMM A
						    , USRYMA.TB_YM_STACKLAYER B
						WHERE A.COIL_NO =B.STOCK_ID(+)
						  AND A.PARENT_COIL_NO =?
						ORDER BY A.COIL_NO
						*/		   		
						String query1	="ym.steelinfo.steelinforecv.session.CoilInfoRegSBean.HRPlatecommlist";
						List StockList	= new YdStockDAO().getListData(query1, new Object[]{sGoodsNo});
						
						JDTORecord 	jrecrd = JDTORecordFactory.getInstance().create();
						JDTORecord 	jrecrd2 = JDTORecordFactory.getInstance().create();
						JDTORecord 	tcRecord1 = JDTORecordFactory.getInstance().create();	
						
						int 		totCnt  	=0 ;

						EAIHttpSender eaiHttpSender =null;				
						eaiHttpSender =new EAIHttpSender();
						eaiHttpSender.initService(EAIHttpSender.issnd);
						
						logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶임가공HRPlate절단 건 수: "+StockList.size());	
						
						//압연실적처리
						for (int i = 0; i < StockList.size(); i++) {
					   		jrecrd = (JDTORecord)StockList.get(i);
							
							//압연실적을 처리
							isVal = setInnerIFCoilInfo_HP( StringHelper.evl(jrecrd.getFieldString("GOODS_NO"), "") 
														 , StringHelper.evl(jrecrd.getFieldString("YD_GP"), "") 
														 , "");
					   		} //for end 
						
						//진도코드가 H(입고대기) 상태만 출하로 전송 함 20091006 JKJEUNG
						query1	="ym.steelinfo.steelinforecv.session.CoilInfoRegSBean.HRPlatecommlist2";
						List StockList2	= new YdStockDAO().getListData(query1, new Object[]{sGoodsNo});
						
						//출하 임가공 입고전문 전송 
				   		for (int i = 0; i < StockList2.size(); i++) {
				   		jrecrd2 = (JDTORecord)StockList2.get(i);
				   		
				   			
//							GOODS_NO		제품 번호
//							RECEIPT_DATE	입고 일자
//							RECEIPT_TIME	입고 시각
//							YD_GP			YARD 구분
//							STORE_LOC		저장 위치
//							PROD_ITEM_CODE	ITEMCODE

						tcRecord1.setField("TC_CODE", "YDDMR003");				
						tcRecord1.setField("TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
						tcRecord1.setField("GOODS_NO", StringHelper.evl(jrecrd2.getFieldString("GOODS_NO"), "").trim());
						tcRecord1.setField("RECEIPT_DATE", StringHelper.evl(jrecrd2.getFieldString("RECEIPT_DATE"), ""));
						tcRecord1.setField("RECEIPT_TIME", StringHelper.evl(jrecrd2.getFieldString("RECEIPT_TIME"), ""));
						tcRecord1.setField("YD_GP", StringHelper.evl(jrecrd2.getFieldString("YD_GP"), ""));
						tcRecord1.setField("STORE_LOC", StringHelper.evl(jrecrd2.getFieldString("STORE_LOC"), ""));
						tcRecord1.setField("PROD_ITEM_CODE", "");
						
						//내부인터페이스 송신모듈 호출 
						//String returncode= eaiHttpSender.send(tcRecord1);
						
						
						//인터페이스 전문 호출
						EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
						isSuccess = (Boolean)ejbConn.trx("getYDDMR003",new Class[]{JDTORecord.class},
						  	  	 new Object[]{tcRecord1});
						
						logger.println(LogLevel.INFO, this,"==>>>eaiHttpSender 응답값: ["+isSuccess+"]" );			
						
				   			
				   		totCnt =totCnt+1 ;
				   		} //for end 
				   		
						logger.println(LogLevel.DEBUG,this,"▶▶▶▶▶▶출하에 임가공입고작업실적(YDDMR003) 전송 건 수: "+ totCnt);
				   	//************************************************************************************
				   	//************************************************************************************
					}
				/**
			     *	3.	STOCK DATA DELETE
			     */
					 /*
					Ver4.1--
					UPDATE tb_ym_stock
					SET	
						del_yn     = :del_yn,
						modifier   = 'SYSTEM',
						mod_ddtt   = sysdate           
					WHERE stock_id = :stock_id
					*/
					iSeq = dao.updateStockDelYnInfo(sGoodsNo,"Y");
					
				/**
			     *	3.	SPM 야드 MAP 정보 셋팅
			     */
				String sYdGp		 = "";
				String sIStackColGp1 = "";
				String sIStackColGp2 = "";
				
				if(YmCommonConst.PLANT_GP_A.equals(sPlantGp)||
				   YmCommonConst.PLANT_GP_H.equals(sPlantGp)){
				   	 	sYdGp = "1";
					   	/**
						 * A설비 적치열 정보 셋팅
						 * 1DKE01	SPM입측컨베이어
						 * 1EKE01	SPM입측컨베이어
						 */
					   	sIStackColGp1 = YmCommonConst.SPM_COL_1DKE+YmCommonConst.STACK_BED_GP_01;
					   	sIStackColGp2 = YmCommonConst.SPM_COL_1EKE+YmCommonConst.STACK_BED_GP_01;
				}else{
					sYdGp = "3";
					if(YmCommonConst.NEW_WORK_SPM_N.equals(sFacilityGp)){
						/**
						 * B 설비 적치열 정보 셋팅
						 * 3DKE01	SPM2입측컨베이어
						 * 3EKE01	SPM2입측컨베이어
						 */
						sIStackColGp1 = YmCommonConst.SPM_COL_3DKE+YmCommonConst.STACK_BED_GP_01;
						sIStackColGp2 = YmCommonConst.SPM_COL_3EKE+YmCommonConst.STACK_BED_GP_01;
					}
				}
				
				/**
				 *	4.	SPM STOCK_ID 입측삭제
				 */
				iSeq = YmCommonDB.deleteConveyorInfo(sIStackColGp1,
					   						  		 sGoodsNo);
				iSeq = YmCommonDB.deleteConveyorInfo(sIStackColGp2,
			   						  				 sGoodsNo);
			   	
			   	/**
			   	 * 모 Coil 종료 처리 종료 발생시에 야드에 존재하는 
			   	 * 코일정보가 있으면 삭제한다.
			   	 */
			   	isVal = deleteCoilLocationInfo(sGoodsNo);
			   	
			   	logger.println(LogLevel.DEBUG,this,"=============모 Coil 종료========");
				logger.println(LogLevel.DEBUG,this,"sIStackColGp1	="+sIStackColGp1);
				logger.println(LogLevel.DEBUG,this,"sIStackColGp2	="+sIStackColGp2);
				logger.println(LogLevel.DEBUG,this,"sGoodsNo		="+sGoodsNo);
				logger.println(LogLevel.DEBUG,this,"sMsg			="+sMsg);			
				
				logger.println(LogLevel.DEBUG,this,"=============모 Coil 종료 처리 종료========");   
				
				iSeq = insertUpPutWrslRtData(sGoodsNo,
									  		 sYdGp,sFacilityGp);				            
				isVal = true; 
			}catch(DAOException daoe){
		        throw daoe;
		    }catch(Exception e){
		        throw new EJBServiceException(e);
		    }    
		     
		    return isVal;
		} 
		
		/**
	     * 최규성
	     * SPM2 모Coil 종료시 정보를 실적 처리한다. 인자 추가. 함수오버로딩.
	     *
	     * @param String	: 저장품ID
	     * @param String	: 야드구분
	     * @param String	: 설비구분 SPM2("N")일 경우에만 사용.
	     *
	     */ 
		private int insertUpPutWrslRtData(String sStockId, String sYdGp, String sFacilityGp)
		{	
			int iSeq = -1;
			
			YdWBookDAO ydWBookDAO           = new YdWBookDAO();
			
			try {
				/*
				 * 구자원 단계별 삭제 로직  
				 */
				String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
				if(sAPP060_OLDSRC_YN.equals("Y")){
					return 0;
				}
				
				String scrane_sch_id		= "";    
	            String scrane_stock_id		= "";
	            String scrane_equip_gp		= "";    
	            String scrane_sch_code		= "";   
	            String scrane_up_loc		= ""; 
	            String scrane_put_loc		= "";
	            String scrane_up_func		= ""; 
	            String scrane_put_func		= ""; 
	            String scrane_register		= ""; 
	            String scrane_modifier		= ""; 
	            String scrane_yd_gp			= "";
	            String scrane_work_duty		= "";
				String scrane_work_party	= "";	        
				String sch_wdemand_duty		= "";
			    String sch_wdemand_party	= "";
			    
				scrane_sch_id		= "000000000000000000";    
	            scrane_stock_id		= sStockId.trim();
	            scrane_equip_gp		= "";    
	            scrane_sch_code		= "";   
	            scrane_up_loc		= "모 종료"; 
	            scrane_put_loc		= "모 종료";
	            scrane_up_func		= YmCommonConst.CRANE_FUNC_N; 
	            scrane_put_func		= YmCommonConst.CRANE_FUNC_N;
	            scrane_register		= "SYSTEM"; 
	            scrane_modifier		= "SYSTEM"; 
	            scrane_yd_gp		= sYdGp;

				if(YmCommonConst.NEW_WORK_SPM_N.equals(sFacilityGp) ){
					scrane_sch_code 	= YmCommonConst.NEW_SCH_WORK_KIND_CNLI;
	        	}else{
					scrane_sch_code 	= YmCommonConst.NEW_SCH_WORK_KIND_CKLI;
				}

	            scrane_equip_gp = sYdGp + "X" + YmCommonConst.EQUIP_KIND_CR + "00";
		        
		        scrane_work_duty		= YmCommonUtil.getWorkDuty();
				scrane_work_party		= YmCommonUtil.getWorkParty();
				sch_wdemand_duty		= YmCommonUtil.getWorkDuty();
			    sch_wdemand_party		= YmCommonUtil.getWorkParty();
			     
			 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.insertCraneWrsltSanJuk";
			 	iSeq  			  = ydWBookDAO.requestinsertData(queryCode, new Object[]{ 
																			scrane_sch_id,
																            scrane_stock_id,
																            scrane_equip_gp,
																            scrane_sch_code,
																            scrane_work_duty,
																			scrane_work_party,
																			sch_wdemand_duty,
																		    sch_wdemand_party,
																            scrane_up_loc,
																            scrane_put_loc,
																            scrane_up_loc,
																            scrane_up_func,
																            scrane_put_loc,
																            scrane_put_func,
																            scrane_register,
																            scrane_modifier,
																            scrane_yd_gp});	
				
		    }catch(DAOException daoe){
		        throw daoe;
		    }catch(Exception e){
		        throw new EJBServiceException(e);
		    }    
		    return iSeq; 
		}
		
	      /**
		 * 오퍼레이션명 : 
		 *  
		 * 최규성 
	        * 자 Coil 실적을 처리. 함수 오버로딩.
	        *
	        * param String	: 저장품ID
	        * param String	: 설비구분
	        *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param 
		 * @return
		 * @throws 
		 */      
	    public boolean setInnerIFCoilInfo_06(String sGoodsNo,String sFacilityGp){
			
			boolean isVal = false;
			
			try{
				/*
				 * 구자원 단계별 삭제 로직  
				 */
				String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
				if(sAPP060_OLDSRC_YN.equals("Y")){
					return false;
				}
				
				JDTORecord stockV 	= null;
				int iSeq 			= 0;
			    
				YdStockDAO dao	= new YdStockDAO();
			    
				String sMsg = "";
				
			    logger.println(LogLevel.DEBUG,this,"=============자 Coil 실적 처리 시작========");
			    /**
			     *	1.	공통 Coil정보를 가져온다.
			     */
				    stockV	= dao.getCoilCommonInfo(sGoodsNo);
				    
				    if(stockV == null){ 
				    	logger.println(LogLevel.DEBUG,this,"NO HAVE COMMON COIL DATA");
				    	throw new EJBServiceException("=자 Coil 실적=>NO HAVE COMMON COIL DATA");
				    }
				    
				    String sPlantGp 	  = StringHelper.evl(stockV.getFieldString("공장구분"),"");	
				    
		    	/**
			     *	2.	저장품Table에 정보를 등록,수정한다.
			     *		최초 발생시 등록, 재 실적발생시 수정
			     */
			    	boolean isExist = dao.isExistPrimaryKey(sGoodsNo);
			    	
			    	if(isExist){
			    		logger.println(LogLevel.DEBUG,this,"EXIST STOCK TABLE COIL DATA");
						throw new EJBServiceException("=자 Coil 실적=>EXIST STOCK TABLE COIL DATA");
						
					}else{
						logger.println(LogLevel.DEBUG,this,"NOT EXIST STOCK TABLE COIL DATA");
						
						//코일공통 진도코드 Table 참조.
				     	String[] sStockInfo = YmCommonUtil.getCoilCurrProgCd(sGoodsNo,"");
					    String sProgCd   	= sStockInfo[0];
						String sStocMv   	= sStockInfo[1];
						
						String sStockItem	= "";
				    	if(YmCommonConst.NEW_STOCK_MOVE_TERM_HG.equals(sStocMv)){		// 입고대기
				    		sStockItem	   = YmCommonConst.ITEM_CG;
				    	}else{
				    		sStockItem	   = YmCommonConst.ITEM_CM;
				    	}
				    	
				    	logger.println(LogLevel.DEBUG,this,"저장품 이동조건 = "+ sStocMv );
						
						if("".equals(sStocMv)){ 
					    	logger.println(LogLevel.DEBUG,this,"NO HAVE 코일 공통 진도코드 DATA");
					    	throw new EJBServiceException("=자 Coil 실적=>NO HAVE 코일 공통 진도코드 DATA");
					    }
					    /*ym.steelinfo.steelinforecv.dao.YdStockDAO.insertStockTransInfo*/
						/*
						INSERT INTO TB_YM_STOCK
						(
						 stock_id, stock_item, stock_move_term,
						 register, reg_ddtt, del_yn
						)
						VALUES
						(
						 :stock_id, :stock_item, :stock_move_term,
						 'SYSTEM', sysdate, 'N'
						)      
						*/
					    iSeq = dao.insertStockTransInfo(sGoodsNo,		      // 저장품ID
					            						sStockItem,			  // 저장품품목(코일제품)
														sStocMv 			  // 저장품이동조건
														);  			      
					}
				/**
			     *	3.	야드Map에 저장품정보를 등록,수정한다.
			     */
			    String sOStackColGp  = "";
				if(YmCommonConst.PLANT_GP_A.equals(sPlantGp)||
				   YmCommonConst.PLANT_GP_H.equals(sPlantGp)){
					   	/**
						 * A설비 적치열 정보 셋팅
						 * 1EKD01	SPM출측컨베이어
						 */
					   	sOStackColGp  = YmCommonConst.SPM_COL_1EKD+YmCommonConst.STACK_BED_GP_01;
				}else{
					if(YmCommonConst.NEW_WORK_SPM_N.equals(sFacilityGp) ){
						sOStackColGp  = YmCommonConst.SPM_COL_3EKD+YmCommonConst.STACK_BED_GP_01;
					}else{
						/**
						 * B 설비 적치열 정보 셋팅
						 * 3BKD01	SPM출측컨베이어
						 */
						sOStackColGp  = YmCommonConst.SPM_COL_3BKD+YmCommonConst.STACK_BED_GP_01;
					}
				}
				iSeq = YmCommonDB.insertConveyorInfo(sOStackColGp,
			   						  				 sGoodsNo,
			   						  				 YmCommonConst.GBN_MIN);
			   	if(iSeq < 1){
			   		throw new EJBServiceException("=자 Coil 실적=>CONVEYOR CREATE FAIL");
			   	}		  				 
			   	logger.println(LogLevel.DEBUG,this,"=============자 Coil 실적 처리 중간검사========");
				logger.println(LogLevel.DEBUG,this,"sOStackColGp	="+sOStackColGp);
				logger.println(LogLevel.DEBUG,this,"sGoodsNo		="+sGoodsNo);
				logger.println(LogLevel.DEBUG,this,"iSeq			="+iSeq);				
				
				logger.println(LogLevel.DEBUG,this,"=============자 Coil 실적 처리 종료========");				            
				isVal = true; 
			}catch(DAOException daoe){
		        throw daoe;
		    }catch(Exception e){
		        throw new EJBServiceException(e);
		    }    
		     
		    return isVal;
		} 
	    
	    /**
		 * 오퍼레이션명 : 
		 *  
		 * 최규성 
	        * 코일공통 테이블에서 요구 받은 코일에 대한 정보를 검사하여 SPM2 관련인지 판단한다.
	        *
	        * param String	: 저장품ID
	        *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param 
		 * @return
		 * @throws 
		 */      
	    public String checkCoilCommonInfo(String sCoilNo){
	    	String sVal = "";
	    	String sEqpGp = "";
	    	String sPlntGp	="";
	    	logger.println(LogLevel.DEBUG,this,"=============코일공통에서 정보 수집 판별 시작========");
	    	try{
	    		/*
				 * 구자원 단계별 삭제 로직  
				 */
				String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
				if(sAPP060_OLDSRC_YN.equals("Y")){
					return null;
				}
				
	    		JDTORecord stockV 	= null;
	    		YdStockDAO dao	= new YdStockDAO();
	    		String sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCoilCommonInfo_SPM2";
	    		String[] sTotalPassProc = new String[5];
	    		String sFinalPassProc = "";
	    		/**
	    		1. 공통 코일 정보를 가져온다.
	    		*/
	    		stockV = dao.getData(sQueryId, new Object[]{sCoilNo});
//	    		stockV = dao.getCoilCommonInfo(sCoilNo); // 쿼리가 변경됨. 최규성 운영계 반영시 주의. 
	    		if (stockV == null)
	    		{
	    			logger.println(LogLevel.DEBUG,this,"코일공통테이블에 코일 정보가 존재하지 않음");
	    			return sVal;
	    		}
	    		
	    		sPlntGp = StringHelper.evl(stockV.getFieldString("공장구분"),"-");
    			sEqpGp = StringHelper.evl(stockV.getFieldString("YD_EQP_GP"),"-");
    			if(sPlntGp.equals("A") && sEqpGp.equals("QE")){
    				sFinalPassProc ="1Q";
    			}else{

		    		/**
		    		2. 통과공정의 정보를 비교한다.
		    		*/
		    		// COILCOMM TBL의 통과공정 1~5 중 가장 마지막에 입력된 통과공정을 비교하여야 한다. 최규성. 
		    		for(int i=0; i < 5; i++)
		    		{	 
		    			sTotalPassProc[i] = StringHelper.evl(stockV.getFieldString("통과공정"+String.valueOf(i+1)),"-");		// SPM2관련 추가 최규성
		    			if( (sTotalPassProc[i].equals("-") || sTotalPassProc[i].equals("")	)/* && i != 0*/)
		    			{
		    				logger.println(LogLevel.DEBUG,this,"A통과공정"+String.valueOf(i+1)+"은 "+sFinalPassProc );
		    				break;
		    				//sFinalPassProc = "";
		    				
		    			}else{
		    				sFinalPassProc = sTotalPassProc[i];
		    				logger.println(LogLevel.DEBUG,this,"B통과공정"+String.valueOf(i+1)+"은 "+sFinalPassProc );
		    				
		    			}
		    		}
    			}

	    		/**
	    		3. 통과공정 정보가 6K[SPM2] 일 경우 결과값 6K
	    			아닐 경우 결과값 "" 반환.
	    		*/
	    		sVal = sFinalPassProc;

	    	}catch(DAOException daoe){
	    		throw daoe;
	    	}catch(Exception e){
	    		throw new EJBServiceException(e);
	    	}    
	    	logger.println(LogLevel.DEBUG,this,"=============코일공통에서 정보 수집 판별 종료========");
	    	logger.println(LogLevel.DEBUG,this,"결과값 : "+sVal);

	    	return sVal;
	    }
	    /**
		 * 오퍼레이션명 : 
		 *  
		 * 최규성 
	        * 적치단 위치 조정을 위한 메소드. 적치열의 Max Bed를 구한다.
	        *
	        * param String	: 저장품ID
	        *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param 
		 * @return
		 * @throws 
		 */   
	    public JDTORecord getMaxBedInfo(String sQueryId, List whereData){
	    	YdStockDAO ydstockDAO = new YdStockDAO();  
		    try{
		    	/*
				 * 구자원 단계별 삭제 로직  
				 */
				String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
				if(sAPP060_OLDSRC_YN.equals("Y")){
					return null;
				}
				
		    	
		    	return ydstockDAO.getData(sQueryId, whereData);	    	
		    }catch(DAOException daoe){
		        throw daoe;
		    }catch(Exception e){
		        throw new EJBServiceException(e);
		    }
	    }
		/**
		 * 오퍼레이션명 : 등록된 인자값으로 적치열의 좌표 값을 수정한다. 
		 * 2009-12-30 최규성
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param sQeuryId
		 * @param updateStackLayerAxis
		 * @return
		 * @throws 
		 */
		public int updateStackLayerAxis(String sQeuryId, List sUpdateArgu) throws EJBServiceException{   
    		YdStockDAO ydstockDAO = new YdStockDAO();
			try { 	
				/*
				 * 구자원 단계별 삭제 로직  
				 */
				String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
				if(sAPP060_OLDSRC_YN.equals("Y")){
					return 0;
				}
				
	   			return ydstockDAO.updateMoveProduct(sQeuryId, sUpdateArgu);
	    	}catch(DAOException daoe){
	            throw daoe;
	        }catch(Exception e){         
	        	throw new EJBServiceException(e);
	        }
	    }
		
		
	    public JDTORecord getMaxBedInfo(String sStackColGp){
	    	YdStockDAO ydstockDAO = new YdStockDAO();
	    	String sQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.getMaxBedInfo";
	    	List whereData = new ArrayList();
	    	
		    try{
		    	/*
				 * 구자원 단계별 삭제 로직  
				 */
				String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
				if(sAPP060_OLDSRC_YN.equals("Y")){
					return null;
				}
				
		    	whereData.add(sStackColGp);
		    	return ydstockDAO.getData(sQueryId, whereData);	    	
		    }catch(DAOException daoe){
		        throw daoe;
		    }catch(Exception e){
		        throw new EJBServiceException(e);
		    }
	    }
	
	    
	    /**
	     * 야드관리 > A열연 Coil 야드 관리 > 차량작업관리 > 이송재상차순위관리  - 등록
	     * @ejb.interface-method
	     * @param queryID
	     * @param jrecord
	     * @return
	     * @throws EJBServiceException
	     * @throws DAOException
	     */
	    public int updateCoilloadLotRankingJip(JDTORecord jrecord) throws EJBServiceException ,DAOException{
			
			int 					count 		= 0;
			YdSlabMoveBayRankingDAO dao 		= new YdSlabMoveBayRankingDAO();
			String 					queryID 	= "com.inisteel.cim.ym.steelinfo.steelinforecv.session.updateCoilloadLotRankingJip";
			String 					queryID2 	= "com.inisteel.cim.ym.steelinfo.steelinforecv.session.updateCoilloadLotRankingJip2";
			String					fromDate 	= "";
			String					untoDate 	= "";
			String					tmp	 		= "";
			try{
				/*
				 * 구자원 단계별 삭제 로직  
				 */
				String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
				if(sAPP060_OLDSRC_YN.equals("Y")){
					return 0;
				}
				
				String 			upd_lable 	= jrecord.getFieldString("upd_lable");
				int 			listCnt 	= jrecord.getFieldInt("list"+upd_lable+"_cnt");
				//JDTORecord 		inData 		= JDTORecordFactory.getInstance().create();
				
				if(upd_lable.length()==1){ // 순위 변경 
					Object[] inData = new Object[7];
					for(int i =0; i<listCnt;i++){
						fromDate = "";
						untoDate = "";
						tmp = jrecord.getFieldString("fromDate"+upd_lable+"_"+i);
						tmp = StringHelper.replaceStr(tmp, ".", "");
						if(!tmp.equals("")){
							fromDate = tmp+jrecord.getFieldString("fromHH"+upd_lable+"_"+i)+jrecord.getFieldString("fromMI"+upd_lable+"_"+i);
						}
						tmp = jrecord.getFieldString("untoDate"+upd_lable+"_"+i);
						tmp = StringHelper.replaceStr(tmp, ".", "");
						if(!tmp.equals("")){
							untoDate = tmp+jrecord.getFieldString("toHH"+upd_lable+"_"+i)+jrecord.getFieldString("toMI"+upd_lable+"_"+i);
						}
						inData[0] = jrecord.getFieldString("RANKING"+upd_lable+"_"+i);
						inData[1] = fromDate;
						inData[2] = untoDate;
						inData[3] = jrecord.getFieldString("hdn_chk"+upd_lable+"_"+i);
						inData[4] = jrecord.getFieldString("register");
						inData[5] = jrecord.getFieldString("RULE_ID"+upd_lable+"_"+i);
						inData[6] = jrecord.getFieldString("SCH_CD"+upd_lable+"_"+i);

						count += dao.updateCoilloadLotRankingJip(queryID, inData);

					}
				}else{ //목적동변경
					Object[] inData = new Object[4];
					for(int i =0; i<listCnt;i++){
						inData[0] = jrecord.getFieldString("DEST_BAY"+upd_lable+"_"+i);
						inData[1] = jrecord.getFieldString("register");
						inData[2] = jrecord.getFieldString("ORD_YEOJAE_GP"+upd_lable+"_"+i);
						inData[3] = jrecord.getFieldString("SLAB_GP"+upd_lable+"_"+i);
						
						count += dao.updateCoilloadLotRankingJip(queryID2, inData);
					}
				}
				
			}catch(DAOException daoe){
				throw daoe;
			}catch(Exception e){
				throw new EJBServiceException(e);
			}
			return count;
		}
	    
	    
	    /**
	     *  출하검수등록
	     * @ejb.interface-method
	     * @param queryID
	     * @param jrecord
	     * @return
	     * @throws EJBServiceException
	     * @throws DAOException
	     */
	    public int updateCarExamination(JDTORecord jrecord) throws EJBServiceException ,DAOException{
			
	    	/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			int 					count 		= 0;
			JDTORecord 				jrecrd	 	= JDTORecordFactory.getInstance().create();
			JDTORecord 				jrecrd2 	= JDTORecordFactory.getInstance().create();
			YdSlabMoveBayRankingDAO dao 		= new YdSlabMoveBayRankingDAO();
			JDTORecord 				tcRecordDM 	=JDTORecordFactory.getInstance().create(); 
  
			String 					queryID 	= "";
			String 					queryID2 	= "";
			String					fromDate 	= "";
			String					untoDate 	= "";
			String					tmp	 		= "";
			String 					cnt			= "";
			String 					chk			= "";
			String 					trans_ord_no= "";
			String 					sTRANS_ORD_DATE= "";
			String 					sTRANS_ORD_SEQNO= "";
			String					szTRANS_EQUIPMENT_TYPE= "";
			JDTORecord recTemp = null;
			JDTORecord recPara = null;
			JDTORecordSet rsResult = null;
			YdStockDAO ydStockDAO = new YdStockDAO();
			int nRet = 0;
			String szCR_FRTOMOVE_GP = "";
			
			try{
 
				// PIDEV
//				String sApplyYnPI = commDao.ApplyYnPI("", "CoilInfoRegSBean => updateCarExamination", "APPPI0", "3", "*");
					
					//운송지시 번호 단위로 검수 완료 처리 작업
					trans_ord_no=jrecord.getFieldString("TRANS_ORD_NO");
					
					if("".equals(trans_ord_no)){
						Object[] inData = new Object[7];
						inData[0] = jrecord.getFieldString("loc_cd");
						inData[1] = jrecord.getFieldString("exam_cd");
						inData[2] = jrecord.getFieldString("exam_cd2");
						inData[3] = jrecord.getFieldString("label_cd");
						inData[4] = jrecord.getFieldString("trans_word_no");
						inData[5] = jrecord.getFieldString("trans_word_seqno");
						inData[6] = jrecord.getFieldString("stock_id");
						
						
						//검수 완료 처리
						queryID 	= "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.updateCarExaminationGoodsDetjlNEW2";
						count += dao.updateCoilloadLotRankingJip(queryID, inData);
						
						
						//검수 완료 TC 전송 가능 유무 체크 
						sTRANS_ORD_DATE =jrecord.getFieldString("trans_word_no");
						sTRANS_ORD_SEQNO =jrecord.getFieldString("trans_word_seqno");
						
						return count;
						
					}else{
						
						//검수 완료 TC 전송 가능 유무 체크 
						sTRANS_ORD_DATE =trans_ord_no.substring(0, 8);
						sTRANS_ORD_SEQNO =trans_ord_no.substring(8);
						Object[] inData = new Object[2];
						inData[0] = sTRANS_ORD_DATE;
						inData[1] = sTRANS_ORD_SEQNO;
						
						//운송지시 단위 검수 완료 처리
						queryID 	= "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.updateCarExaminationGoodsDetjl2";
						count += dao.updateCoilloadLotRankingJip(queryID, inData);
						
						
						
					}
					

					queryID2	="ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.CarExaminationChk";
					List StockList	= new YdStockDAO().getListData(queryID2, new Object[]{sTRANS_ORD_DATE,sTRANS_ORD_SEQNO});
					
					
					if(StockList.size()>0){
						jrecrd = (JDTORecord)StockList.get(0);
						cnt = StringHelper.evl(jrecrd.getFieldString("CNT"), "").trim();
						chk = StringHelper.evl(jrecrd.getFieldString("CHK"), "").trim();
						szTRANS_EQUIPMENT_TYPE = StringHelper.evl(jrecrd.getFieldString("TRANS_EQUIPMENT_TYPE"), "").trim();
						
						if(chk.equals("0") && !cnt.equals("0") ){
							
							logger.println(LogLevel.DEBUG,this,"=============검수완료 전문 전송 시작 ========");
							

							// 레코드생성-----------------------------------------------------------------
							rsResult = JDTORecordFactory.getInstance().createRecordSet("");
							recPara = JDTORecordFactory.getInstance().create();
							
//							if ("Y".equals(sApplyYnPI)) {
								tcRecordDM.setField("MQ_TC_CD"              , new String("M10YDLMJ1101"));
								tcRecordDM.setField("MQ_TC_CREATE_DDTT"		, new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
								//PDA출하 인경우 
								if ("P".equals(szTRANS_EQUIPMENT_TYPE)) {
									tcRecordDM.setField("SCH_YN"            , "Y");
								} else {
									tcRecordDM.setField("SCH_YN"            , "N");
								}									
								
								recPara.setField("TRN_REQ_DATE"				,  sTRANS_ORD_DATE);
								recPara.setField("TRN_REQ_SEQ"				, sTRANS_ORD_SEQNO);
								recPara.setField("CAR_NO"					, StringHelper.evl(jrecrd.getFieldString("CAR_NO"), ""));
								recPara.setField("YD_GP"					, "3");
								recPara.setField("DIST_GOODS_GP"			, "H");
								
		 	                    tcRecordDM.setField("CARLD_CHK_DONE_DATE", YmCommonUtil.getCurDate("yyyyMMdd"));
			                    tcRecordDM.setField("CARLD_CHK_DONE_TIME", YmCommonUtil.getCurDate("HHmmss"));	
			                    
//							} else {
//								//PDA출하 인경우 
//								if ("P".equals(szTRANS_EQUIPMENT_TYPE)) {
//									tcRecordDM.setField("JMS_TC_CD"             , new String("YDDMR074"));
//								} else {
//									tcRecordDM.setField("JMS_TC_CD"             , new String("YDDMR036"));
//								}									
//								
//								recPara.setField("TRANS_ORD_DATE",  sTRANS_ORD_DATE);
//								recPara.setField("TRANS_ORD_SEQNO", sTRANS_ORD_SEQNO);
//								recPara.setField("CAR_NO", StringHelper.evl(jrecrd.getFieldString("CAR_NO"), ""));
//																						 
//								tcRecordDM.setField("TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
//								tcRecordDM.setField("TRANS_WORD_DATE", sTRANS_ORD_DATE);
//			                    tcRecordDM.setField("TRANS_WORD_SEQNO", sTRANS_ORD_SEQNO);
//		 	                    tcRecordDM.setField("CARLD_CHK_DONE_DATE", YmCommonUtil.getCurDate("yyyyMMdd"));
//			                    tcRecordDM.setField("CARLD_CHK_DONE_TIME", YmCommonUtil.getCurDate("HHmmss"));	
//			                    tcRecordDM.setField("CAR_NO", StringHelper.evl(jrecrd.getFieldString("CAR_NO"), ""));
//							}

							//검수완료 TC대상 조회
							// PIDEV
//							if ("Y".equals(sApplyYnPI)) {
								queryID2	="ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.CarExaminationTCListNEW_PIDEV";
//							} else {
//								queryID2	="ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.CarExaminationTCListNEW";								
//							}
							
							List StockList2	= new YdStockDAO().getListData(queryID2, new Object[]{sTRANS_ORD_DATE,sTRANS_ORD_SEQNO});
							
							//불량코일 발생시 L2로 전문 전송
							boolean isYmAbExist = false;
							boolean isYdAbExist = false;
							for (int i = 0; i < StockList2.size(); i++) {
						   		jrecrd2 = (JDTORecord)StockList2.get(i);
						   		szCR_FRTOMOVE_GP =StringHelper.evl(jrecrd2.getFieldString("CR_FRTOMOVE_GP"), "") ;
						   		
			         			if(i ==0){
			         				tcRecordDM.setField("GOODS_NO_CNT", StringHelper.evl(jrecrd2.getFieldString("GOODS_NO_CNT"), ""));			                    
			         			}
			         			
			         			tcRecordDM.setField("GOODS_NO" 			+ (1+i),StringHelper.evl(jrecrd2.getFieldString("GOODS_NO"), ""));			         			
			                    tcRecordDM.setField("GOODS_CHK_AB_CD" 	+ (1+i),StringHelper.evl(jrecrd2.getFieldString("GOODS_CHK_AB_CD"), ""));
			                    tcRecordDM.setField("LABEL_REISSUE_YN" 	+ (1+i),StringHelper.evl(jrecrd2.getFieldString("LABEL_REISSUE_YN"), ""));
			                    tcRecordDM.setField("GDS_CARLD_LOC" 	+ (1+i),StringHelper.evl(jrecrd2.getFieldString("YD_CAR_UPP_LOC_CD"), ""));
			                    
			                    
			                    //전문 전송 여부 flag
			                    String sendTcToY5 = StringHelper.evl(jrecrd2.getFieldString("MAKE_TC_TO_Y5"), "");
			                    String sendTcToA7 = StringHelper.evl(jrecrd2.getFieldString("MAKE_TC_TO_A7"), "");
			                    //이상코드 존재시, 
			                    if(!"".equals(StringHelper.evl(jrecrd2.getFieldString("GOODS_CHK_AB_CD"), ""))){
			                    	//1열연제품 이상
			                    	if("Y".equals(sendTcToA7) 
			                    			&& "3".equals(StringHelper.evl(jrecrd2.getFieldString("YD_GP"), ""))) isYmAbExist = true;
			                    	if("Y".equals(sendTcToY5) 
			                    			&& "J".equals(StringHelper.evl(jrecrd2.getFieldString("YD_GP"), ""))) isYdAbExist = true;
			                    }
			                    
							}
							
							if(!szCR_FRTOMOVE_GP.equals("")){
								tcRecordDM.setField("CR_FRTOMOVE_GP"           , szCR_FRTOMOVE_GP);
							}
							
							if(isYmAbExist){
								JDTORecord recInTemp  = JDTORecordFactory.getInstance().create();
								String logId = jrecord.getResultCode();
								String methodNm = "코일제품검수 [CoilInfoRegS.updateCarExamination] < " + jrecord.getResultMsg();
								recInTemp.setResultCode(logId);	//Log ID
								recInTemp.setResultMsg(methodNm);	//Log Method Name
								recInTemp.setField("TRANS_ORD_DATE"  , sTRANS_ORD_DATE);
								recInTemp.setField("TRANS_ORD_SEQNO"  , sTRANS_ORD_SEQNO);
								
								logger.println(LogLevel.DEBUG,this,"=============1열연 불량코일 발생정보 전송========");
								
								JDTORecord sndRecord = JDTORecordFactory.getInstance().create();
								sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2("YMA7L014", recInTemp));
								
								EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
								sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { sndRecord });
							}
							if(isYdAbExist){
								JDTORecord recInTemp  = JDTORecordFactory.getInstance().create();
								String logId = jrecord.getResultCode();
								String methodNm = "코일제품검수 [CoilInfoRegS.updateCarExamination] < " + jrecord.getResultMsg();
								recInTemp.setResultCode(logId);	//Log ID
								recInTemp.setResultMsg(methodNm);	//Log Method Name
								
								recInTemp.setField("MSG_ID"  	      , "YDY5L009");
								recInTemp.setField("TRANS_ORD_DATE"   , sTRANS_ORD_DATE);
								recInTemp.setField("TRANS_ORD_SEQNO"  , sTRANS_ORD_SEQNO);
								
								
								logger.println(LogLevel.DEBUG,this,"=============2열연 불량코일 발생정보 전송========");
								YdDelegate ydDelegate = new YdDelegate();
								
								ydDelegate.sendMsg(recInTemp);
							}
							
							
		         			//인터페이스 전문 호출
						   EJBConnector ejbConn1 = new EJBConnector("default","JNDIYardWrkResReg",this);
						   isSuccess = (Boolean)ejbConn1.trx("sendInternalModel",new Class[]{JDTORecord.class},
						  	  	             new Object[]{tcRecordDM}); 
		                    logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 코일제품검수완료.===");
		                    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		                    
		                    Object[] inData2 = new Object[2];		
		                    if("".equals(trans_ord_no)){
			                    	                    
								inData2[0] = jrecord.getFieldString("TRANS_ORD_DATE");
								inData2[1] = jrecord.getFieldString("TRANS_ORD_SEQNO");
		                    }else{
		      
		                    	inData2[0] = sTRANS_ORD_DATE;
		                    	inData2[1] = sTRANS_ORD_SEQNO;
		                    	
		                    }
							
							//검수 완료종료 처리
							queryID 	= "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.updateCarExaminationGoodsEnd";
							count += dao.updateCoilloadLotRankingJip(queryID, inData2);
							logger.println(LogLevel.DEBUG,this,"====== 검수완료종료 처리  ========");
						}
						logger.println(LogLevel.DEBUG,this,"=============검수완료 전문 전송 완료 ========");
					}
					
 
			 
			}catch(DAOException daoe){
				throw daoe;
			}catch(Exception e){
				throw new EJBServiceException(e);
			}
			return count;
		}
	    
	    /**
		 * 오퍼레이션명 : 벤드설정 및 해제
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param 
		 * @return
		 * @throws 
		 */   
	    public String setStockBendReg( String chk		//표시:1 ,해제:2 , 보급:3
	    							  ,String temp_stock_id		//저장품
	    							  ,String sModifier
										) {
		String alertString = "정상적으로 처리되었습니다.";
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		String bendingYN = "";
		String sStocMv = "";
		String[] rVal = new String[2];
		try {
			
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			

			/**
			 * 7. 저장품리스트
			 */
			String sStockList = temp_stock_id;
			String vStockList[] = sStockList.split("-");

			// CGS 로그 추가
			logger.println(LogLevel.DEBUG , this , "수신 :저장품리스트 " + sStockList);

			for (int i = 0; i < vStockList.length; i++) {

				if (chk.equals("1")) {
					//벤딩표시
					bendingYN = "Y";
					
					// 저장품 이동조건 업데이트
					sStocMv   ="BD";
				} else if (chk.equals("2")) {
					//벤딩해제
					bendingYN = "";

					// 저장품 이동조건 업데이트
					rVal = YmCommonUtil.getSlabCurrProgCd(vStockList[i] , "");
					sStocMv = rVal[1];
				} else if (chk.equals("3")) {
					//벤딩보급
					bendingYN = "S";

					// 저장품 이동조건 업데이트
					rVal = YmCommonUtil.getSlabCurrProgCd(vStockList[i] , "");
					sStocMv = rVal[1];
					
				}

				String sQueryId_layer = "ym.steelinfo.steelinforecv.dao.YdStockDAO.updateStockBendReg";
				int stacklayerSeq = ydStackLayerDAO.requestupdateData(sQueryId_layer , new Object[]{sStocMv, bendingYN, sModifier, vStockList[i]});

			}
			logger.println(LogLevel.DEBUG , this , "setStockBendReg() 처리 완료 ");
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return alertString;
	    } 
	    
	    
	    /**
		 * 오퍼레이션명 : 열연소재회송처리 
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param 
		 * @return
		 * @throws 
		 */   
	    public String setStockMoveList2( String szlist,				//회송 대상 리스트 
										String yd_car_sch_id,		//차량 스케줄ID							 
										String modifier	       		//수정자
										) {
	    	/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
	        String alertString 		= "회송작업이 정상적으로 등록되었습니다.";
	        String sStockList   = szlist;			
			String vStockList[] = sStockList.split("-");
			/**
			 *	8. 수정자
			 */ 
			String sModifier = modifier;			
			String syd_car_sch_id = yd_car_sch_id;
			String sStlNo		="";
			String sReturnYn	="";
			String sReturnMSG	="";
			String sQueryId 	="";
			String sARR_WLOC_CD 	="";
			String sCRANE_SND 	="";
			String sYD_WRK_PROG_STAT 	="";
			String sSchId		="";
			String sYD_ROUTE_GP = "";
			int 	count		=0;
			int 	intRtnVal	=0;
			
			JDTORecord tcRecord = null;
			JDTORecord tcRecord2 = null;
			JDTORecord inRecord1 = null;
			JDTORecord recDelPara  	= null;
			
			JDTORecordSet  rsResult1 = null;
			
			
			List carSchList = null;
			YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
			YdCrnSchDao ydCrnschDao  			= new YdCrnSchDao();
			JDTORecord outRecord2     			= JDTORecordFactory.getInstance().create();
			JDTORecord outRecord5 				= JDTORecordFactory.getInstance().create();	
			ymCommonDAO dao = ymCommonDAO.getInstance();
			JDTORecord[] 	inRecordarr   	= null;		
			YdDelegate ydDelegate 				= new YdDelegate();
	        try{
				
				
				 
				
				
				List wbookList = new ArrayList();
				List stockList = new ArrayList();
				List layerList = new ArrayList();
				JDTORecord wbookJr = null;
				// CGS 로그 추가
				logger.println(LogLevel.DEBUG, this, "수신 :yd_car_sch_id : " + syd_car_sch_id);
 				logger.println(LogLevel.DEBUG, this, "수신 :수정자 " + sModifier);
				
 				for(int i = 0 ; i < vStockList.length ; i++) {
 					
 					String vStockList2[] =vStockList[i].split(";");
 					
 					if(vStockList2.length > 0) {
 						logger.println(LogLevel.DEBUG, this, "수신 :재료번호  : " + StringHelper.evl(vStockList2[0],""));
 						logger.println(LogLevel.DEBUG, this, "수신 :회송유무  : " + StringHelper.evl(vStockList2[1],""));
 						logger.println(LogLevel.DEBUG, this, "수신 :회송MSC : " + StringHelper.evl(vStockList2[2],""));
 						
 						sStlNo		=StringHelper.evl(vStockList2[0],"");
 						sReturnYn	=StringHelper.evl(vStockList2[1],"");
 						sReturnMSG	=StringHelper.evl(vStockList2[2],"");
 						
 						//차량재료정보에 회송유무 ,MSG 등록 &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
 						sQueryId = "ym.steelinfo.steelinforecv.dao.YdStockDAO.updateCarMtlReturnReg";
 						int chk = ydStackLayerDAO.requestupdateData(sQueryId , new Object[]{sReturnYn, sReturnMSG,sModifier , sStlNo, syd_car_sch_id});
 						if(chk<=0){
 							alertString	= "회송작업이 비정상적으로 차량정보에 등록되었습니다.확인요망";
 							logger.println(LogLevel.DEBUG, this, "setStockMoveList2  : "+alertString);
 							return alertString;
 						}
 						
 						
 						//회송유무 판단.&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
 						if(sReturnYn.equals("Y")){
 							/**회송대상 인경우 처리 로직
 							 * 0.스케줄 및 작업예약 취소
 							 * 1.이송실적 취소처리 
 							 * 2.진도변경 E-> B
 							 * 3.YDPTJ002 전송 
 							 */
 							
 							//크레인 스케줄 및 작업예약 취소.
 							//this.setSchWbookCancel(syd_car_sch_id,sStlNo ,sModifier );
 							EJBConnector ejbConn = new EJBConnector("default","JNDICoilInfoReg",this);
 		 			    	ejbConn.trx("setSchWbookCancel",new  Class[]{String.class,String.class,String.class}
 		 			    																,new Object[]{syd_car_sch_id,sStlNo ,sModifier});
 		 			    	
 							//이송실적 취소 처리  
 							String stkQueryId = "ym.facilitywork.putwrecord.session.updateunLoadTimeToPTCancel";
 							int iSeq = ydStackLayerDAO.requestupdateData(stkQueryId, new Object[]{ sStlNo, sStlNo}); 
 						}else{
 							/**하차대상 인경우 처리 로직
 							 * 1.이송실적 등록처리 
 							 * 2.진도변경 E-> B
 							 * 3.YDPTJ002 전송 
 							 */
 							//이송실적 등록 처리				
 							String stkQueryId = "ym.facilitywork.putwrecord.session.updateunLoadTimeToPT";
 							int iSeq = ydStackLayerDAO.requestupdateData(stkQueryId, new Object[]{  sStlNo, sStlNo}); 
 						}
 						
 						
 						//진도변경 작업&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
 						EJBConnector ejbConn = new EJBConnector("default","JNDITsInfoReg",this);
	 			    	Boolean isYd = (Boolean)ejbConn.trx("CarinfoFrtoMoveBackupSub",new  Class[]{String.class},new Object[]{sStlNo});
	 																	
	 			    	 	
 						//YDPTJ002 전송&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
 						sQueryId 			= "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getCOILCOMM";
 						List productList 	= dao.getCommonList(sQueryId, new Object[]{sStlNo});
 						JDTORecord stlRecord = (JDTORecord)productList.get(0);
 						
 						String stl_appear_gp =StringHelper.evl(stlRecord.getFieldString("STL_APPEAR_GP"), "");
 			            
 						if(!stl_appear_gp.equals("Y"))
 						{ 
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
 						    isSuccess = (Boolean)ejbConn2.trx("sendInternalModel",new Class[]{JDTORecord.class},new Object[]{tcRecord2}); 						  	  	 
 						    logger.println(LogLevel.DEBUG,this, "내부IF호출===코일소재 이송회송 작업 TC전송.===");
 						}
 						
 					}
 				}
 				
 				//이송 TC 대상값 가져 오기&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
 				//차량스케쥴ID조회
 				sQueryId = "com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.getYdCarftmvmtlSCH";
	    		carSchList = dao.getCommonList(sQueryId, new Object[]{syd_car_sch_id});
  
	    		if(carSchList.size() >0 ){
	    			JDTORecord CarSchrec = (JDTORecord)carSchList.get(0);
	    			
	    			sARR_WLOC_CD = StringHelper.evl(CarSchrec.getFieldString("ARR_WLOC_CD"),"");
	    					
					tcRecord = JDTORecordFactory.getInstance().create(); 
					tcRecord.setField("JMS_TC_CD", "YDTSJ016");
					tcRecord.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
					tcRecord.setField("TRN_EQP_CD", 		StringHelper.evl(CarSchrec.getFieldString("TRN_EQP_CD"), ""));
					tcRecord.setField("ARR_WLOC_CD", 		StringHelper.evl(CarSchrec.getFieldString("ARR_WLOC_CD"), ""));
					tcRecord.setField("ARR_YD_PNT_CD", 		StringHelper.evl(CarSchrec.getFieldString("YD_PNT_CD3"), ""));
					tcRecord.setField("CARUD_CMPL_DT", 		new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
					tcRecord.setField("CARLD_SH", 			StringHelper.evl(CarSchrec.getFieldString("CARLD_SH"), ""));
					
					
		    		for(int i=0; i < carSchList.size() ; i++){	 
				    	JDTORecord CarSchrec2 = (JDTORecord)carSchList.get(i);
				        int cnt=i+1 ;
				    	tcRecord.setField("STL_NO"+cnt, 			 new String(StringHelper.evl(CarSchrec2.getFieldString("STL_NO"), "")));
				    	
				    	//'회송':HS, '하차완료':HW
				    	sYD_ROUTE_GP = StringHelper.evl(CarSchrec2.getFieldString("YD_ROUTE_GP"), "");
				    	if(sYD_ROUTE_GP.equals("Y")){
				    		sYD_ROUTE_GP = "HS";
				    	}else{
				    		sYD_ROUTE_GP = "HW";
				    	}
						tcRecord.setField("RETHT_CARUD_CMPL_GP"+cnt, sYD_ROUTE_GP);
		    		}
		    		
		    		//이송완료 TC 전송(YDTSJ016)&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
	 				EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
					isSuccess = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{JDTORecord.class},
					  	  	 new Object[]{tcRecord});
					logger.println(LogLevel.DEBUG,this, "내부IF호출===코일소재 이송회송 작업 TC전송YDTSJ016.===");
	    		}

 
				
				//차량스케쥴테이블 - 하차완료시간 업데이트
				sQueryId = "ym.facilitywork.putwrecord.session.updateUnloadendTime";
		    	count = dao.updateData(sQueryId, new Object[]{sStlNo});
		    	
		    	//차량재료테이블 - DEL_YN 업데이트
		    	sQueryId = "ym.facilitywork.putwrecord.session.deletefrmtList";
		    	count = dao.updateData(sQueryId, new Object[]{sStlNo});
 				
				logger.println(LogLevel.DEBUG, this, "setStockMoveList2() 처리 완료 " );
	        }catch(DAOException daoe){
	            throw daoe;
	        }catch(Exception e){
	            throw new EJBServiceException(e);
	        }
	        return alertString;
	    }   
	    
	    
	    /**
		 * 오퍼레이션명 : setSchWbookCancel
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param 
		 * @return
		 * @throws 
		 * @ejb.transaction type="RequiresNew"
		 */ 
	    public String setSchWbookCancel(  String syd_car_sch_id,		//차량 스케줄ID							 
										  String sStlNo,	       		//재료버호
										  String sModifier
				) {
	    	
	    	/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
		String alertString 		= "스케줄 및 작업예약  삭제작업을 정상적으로 처리되었습니다.";
 		
  
		String sQueryId 			="";
		String sARR_WLOC_CD 		="";
		String sCRANE_SND 			="";
		String sYD_WRK_PROG_STAT 	="";
		String sSchId				="";
		String wbookId 				="";
		String sYD_WBOOK_ID			="";
		int 	intRtnVal			=0;
 
		JDTORecord inRecord1 		= null;
		JDTORecord recDelPara  		= null;
		JDTORecord[] inRecord =null;
		JDTORecordSet  rsResult1 	= null;
		 
		List carSchList 			= null;
 
		YdCrnSchDao ydCrnschDao  	= new YdCrnSchDao();
		ymCommonDAO dao = ymCommonDAO.getInstance();
		JDTORecord outRecord5 			= JDTORecordFactory.getInstance().create();	
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		Boolean isFalse 				= new Boolean(false); 
		YdDelegate ydDelegate 			= new YdDelegate();
		try{
			
			//차량스케쥴ID조회
			sQueryId = "com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.getYdCarftmvmtlSCH";
			carSchList = dao.getCommonList(sQueryId, new Object[]{syd_car_sch_id});
	
			if(carSchList.size() >0 ){
				JDTORecord CarSchrec = (JDTORecord)carSchList.get(0); 				    			
				sARR_WLOC_CD = StringHelper.evl(CarSchrec.getFieldString("ARR_WLOC_CD"),"");
			}
			
			//차량 하차완료 처리  &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
			if(sARR_WLOC_CD.equals("DJY21")||sARR_WLOC_CD.equals("DJY22")||sARR_WLOC_CD.equals("DJY1E")){
				//C열연
				rsResult1 = JDTORecordFactory.getInstance().createRecordSet("");
				inRecord1 = JDTORecordFactory.getInstance().create();			
				inRecord1.setField("STL_NO",      sStlNo);
				/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getCrnSchByStlNo*/
				intRtnVal = ydCrnschDao.getYdCrnsch(inRecord1, rsResult1, 52);
				if( intRtnVal  > 0 ) { 
					rsResult1.first();
					inRecord1 = rsResult1.getRecord();
					String sYD_CRN_SCH_ID 	= StringHelper.evl(inRecord1.getFieldString("YD_CRN_SCH_ID"), "" );
					String sYD_SCH_CD 		= StringHelper.evl(inRecord1.getFieldString("YD_SCH_CD"), "" ); 
					inRecord1   	= JDTORecordFactory.getInstance().create();
					inRecord1.setField("YD_CRN_SCH_ID"	,sYD_CRN_SCH_ID);
					inRecord1.setField("YD_SCH_CD"		,sYD_SCH_CD);
					inRecord1.setField("DEL_YN"			,"Y");
					inRecord1.setField("MODIFIER"		,sModifier);
					//스케줄 취소			
					EJBConnector ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);			
					outRecord5 = (JDTORecord)ejbConn.trx("WrkCancelloc", new Class[] { JDTORecord.class }, new Object[] { inRecord1 });
					sCRANE_SND			= StringHelper.evl(outRecord5.getFieldString("CRANE_SND"), "");
					sYD_WRK_PROG_STAT	= StringHelper.evl(outRecord5.getFieldString("YD_WRK_PROG_STAT"), "");
					
					
					//작업취소전문 송신
					if ("Y".equals(sCRANE_SND)) {
						recDelPara   = JDTORecordFactory.getInstance().create();
						recDelPara.setField("MSG_ID",           "YDY5L004"        );
						recDelPara.setField("YD_CRN_SCH_ID",    sYD_CRN_SCH_ID          ); 
						recDelPara.setField("YD_WRK_PROG_STAT", sYD_WRK_PROG_STAT    );   // 이모듈을 탈려면 항상 '1'의값이 들어옴
						recDelPara.setField("MSG_GP",           "D"                );
						ydDelegate.sendMsg(recDelPara);
					}
					
				}
				
				
				sQueryId = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarsch";
				carSchList = dao.getCommonList(sQueryId, new Object[]{syd_car_sch_id});
				
				if(carSchList.size() >0 ){
					JDTORecord CarSchrec = (JDTORecord)carSchList.get(0); 				    			
					sYD_WBOOK_ID = StringHelper.evl(CarSchrec.getFieldString("YD_CARUD_WRK_BOOK_ID"),"");
				
					inRecord = new JDTORecord[1];
					inRecord[0]= JDTORecordFactory.getInstance().create();
					inRecord[0].setField("YD_WBOOK_ID"		,sYD_WBOOK_ID);
					inRecord[0].setField("YD_USER_ID"		,sModifier);
					
					//작업예약삭제	 
					EJBConnector ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);			
					ejbConn.trx("delYdWrkbook", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				}
		 
			}else{
				//AB열연
				 
				sQueryId = "ym.facade.internal.session.getSchSearch";
				carSchList = dao.getCommonList(sQueryId, new Object[]{sStlNo});
				
				if(carSchList.size() >0 ){
					JDTORecord CarSchrec = (JDTORecord)carSchList.get(0); 				    			
					sSchId = StringHelper.evl(CarSchrec.getFieldString("SCH_ID"),"");
			 
					logger.println(LogLevel.DEBUG, this,  "스케줄취소 처리가시작");
					
					//크레인 스케줄 취소
					EJBConnector ejbConn = new EJBConnector("default", "JNDICraneSchReg", this);
					isFalse =  (Boolean)ejbConn.trx("cancelCoilSchInfo",new Class[]{ String.class}, new Object[]{ sSchId });
					
					if(isFalse.booleanValue() == true){
		        		logger.println(LogLevel.DEBUG, this,  "스케줄취소 처리가 완료되었습니다.");
		        	}else{	
		        		logger.println(LogLevel.DEBUG, this, "스케줄취소 처리도중에 에러가 발생하였습니다.");
		        	}
		 		}
		 
				//작업예약취소처리
				
				String sStockQueryId   = "ym.steelinfo.steelinforecv.YdStockDAO.selectStockID";
				JDTORecord StockCoilNo = ydStockDAO.getData(sStockQueryId, new Object[]{ sStlNo.trim() });
				
				if (StockCoilNo != null){
					wbookId = StringHelper.evl(StockCoilNo.getFieldString("WBOOK_ID"), "");
				}
				
				//저장위치 비우기 작업
				String sStkLayerQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerClear";
				int stkColGp            = ydStackLayerDAO.requestupdateData(sStkLayerQueryId, new Object[]{ sModifier, sStlNo.trim() });
					
				//저장품 작업예약 비우는 작업
				String stkQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateDeleteStockId";
				int stkId         = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ "", "", sStlNo.trim() });
				
				logger.println(LogLevel.DEBUG, this, "setSchWbookCancel() wbookId: "+wbookId );
				
				if(!wbookId.equals("")){
					//저장품에 작업예약 존재 유무 체크	
					String sListStockQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.selectListStockID";
					List ListStockCoilNo     = ydStockDAO.getListData(sListStockQueryId, new Object[]{ wbookId.trim() });
					
					//저장품에 더이상 작업예약이 존재 안하는 경우 (작업예약 취소)
					if(ListStockCoilNo.size() == 0){
						int delsch = ydWBookDAO.deleteWbookInfo( wbookId.trim() );
					}
				}
	 		 
			}
		
		logger.println(LogLevel.DEBUG, this, "setSchWbookCancel() 처리 완료 " );
		}catch(DAOException daoe){
		throw daoe;
		}catch(Exception e){
		throw new EJBServiceException(e);
		}
		return alertString;
		}  

		//////////////////////////////////////////////////////////////////////////////
		/////////////////////B열연수정시작///////////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////
    
	    
	    
	    /**
		 * 오퍼레이션명 : 
		 *  
		 * SJH - INNER INTERFACE
	     * 내부인터페이스로부터 넘어온 전문을 파싱한 후 
	     * 전문내용을 가지고 해당업무로직을 처리한다.
	     * 전문내용을 JDTORecord로 파싱한다.
	     * 업무 로직
	     *	1.TC_CD - POYM001
	     *	2.조업시스템으로부터 Coil 재료정보를 수신
	     *	3.Coil 재료정보를 Key로 공정시스템,출하시스템의 
	     *    Coil 상세정보 Table을 Join Read한다.
	     * 
	     *
		 *         처리구분		ProcessID	CHAR	02		
		 *                      '01' 압연실적 (Back-up포함)처리시점
		 *                      'DC' DC OFF   (Back-up포함)처리시점
		 *                      '02' 정정실적 (Back-up포함)처리시점
		 *                      '03' 원재료 종료 시점
		 *                      '04' 결번 시점
		 *                      '05' 보류재 처리 시점
		 *                      '06' 목전충당 시점
		 *                      '07' HFL처리시점
		 *                      '08' 모 Coil 종료
		 *                      '09' 자 Coil
		 *                      ’10’ 반납 시점
		 * 						'J2' SPM 재작업요구 : 
		 *								수신이 되면 대상재가 출측에 있다면 
		 *								입측 D5로 보내고 조업으로 보급완료실적을 송신
		 *                              대상재가 입측에 있다면 그대로 두고 보급완료실적을 송신
		 *        
		 *         결번/반납 야드구분	YardID	    CHAR	1		
		 *                       '1' A 열연
		 *                       '3' B 열연
		 *        
		 *         결번/반납 공정 코드    ProcessCode	CHAR	2		
		 *                       '4K' SPM
		 *                       '2H' HFL
		 * 
		 *			저장품상태--
		 *		    1	Coil 압연실적 처리
		 *		 	2	정정실적 처리
		 *			3	원재료 종료 시점
		 *			4	결번 시점
		 *			5	보류재 처리 시점
		 *			6	목전충당 시점
		 *			7	HFL 처리시점
		 *			O	출고완료
		 *			P	입고예정
		 *			S	야드내재고
		 *          
		 *          저장품품목 --
		 *          CM - 코일소재
		 *          CG - 코일제품
	        *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param 
		 * @return
		 * @throws 
		 */ 
		public boolean receivePOYM001(JDTORecord rcvMsg) { 
			
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
	//SJH		 
			logger.println(LogLevel.DEBUG,this," CoilInfoRegEJB:receivePOYM001(POYM001)");

//S			String sTc 			= StringHelper.evl(yModel.getTcCode(),"");
//S			String sDate 		= StringHelper.evl(yModel.getTcDate(),"");
//S			String sTime 		= StringHelper.evl(yModel.getTcTime(),"");
//S			String sProcessId	= StringHelper.evl(yModel.getProcessID(),"");
//S			String sCoilNo		= StringHelper.evl(yModel.getCoilNo(),"");
//S			String sYardID		= StringHelper.evl(yModel.getYardID(),"");
//S			String sProcessCode	= StringHelper.evl(yModel.getProcessCode(),"");

//			String sTc 			= StringHelper.evl(rcvMsg.getFieldString("tcCode"),"").trim();
//			String sDate 		= StringHelper.evl(rcvMsg.getFieldString("tcDate"),"").trim();
//			String sTime 		= StringHelper.evl(rcvMsg.getFieldString("tcTime"),"").trim();
//			String sProcessId	= StringHelper.evl(rcvMsg.getFieldString("ProcessID"),"").trim();
//			String sCoilNo		= StringHelper.evl(rcvMsg.getFieldString("coilNo"),"").trim();
//			String sYardID		= StringHelper.evl(rcvMsg.getFieldString("yardID"),"").trim();
//			String sProcessCode	= StringHelper.evl(rcvMsg.getFieldString("processCode"),"").trim();

			String sTc 			= StringHelper.evl(rcvMsg.getFieldString("TcCode"),"").trim();
			String sDate 		= StringHelper.evl(rcvMsg.getFieldString("TcDate"),"").trim();
			String sTime 		= StringHelper.evl(rcvMsg.getFieldString("TcTime"),"").trim();
			String sProcessId	= StringHelper.evl(rcvMsg.getFieldString("ProcessID"),"").trim();
			String sCoilNo		= StringHelper.evl(rcvMsg.getFieldString("CoilNo"),"").trim();
			String sYardID		= StringHelper.evl(rcvMsg.getFieldString("YardID"),"").trim();
			String sProcessCode	= StringHelper.evl(rcvMsg.getFieldString("ProcessCode"),"").trim();

			
			logger.println(LogLevel.DEBUG,this," TcCode 		=" + sTc);
			logger.println(LogLevel.DEBUG,this," TcDate 		=" + sDate);
			logger.println(LogLevel.DEBUG,this," TcTime 		=" + sTime);
			logger.println(LogLevel.DEBUG,this," ProcessID 		=" + sProcessId);
			logger.println(LogLevel.DEBUG,this," CoilNo 		=" + sCoilNo);
			logger.println(LogLevel.DEBUG,this," YardID 		=" + sYardID);
			logger.println(LogLevel.DEBUG,this," ProcessCode 	=" + sProcessCode);
			
			boolean isVal = false;
			
			/*
			 * 1. 압연실적 (Back-up포함)처리시점
			 */
			if("01".equals(sProcessId)){
				
				isVal = setInnerIFCoilInfo_01( sCoilNo, sYardID , "01");
			/*
			 * 2. 정정실적 (Back-up포함)처리시점
			 */
			}else if("02".equals(sProcessId)){
				
				isVal = setInnerIFCoilInfo_02( sCoilNo,"SPM");
			/*
			 * 3. 원재료 종료 시점
			 */
			}else if("03".equals(sProcessId)){
				//SKIP
			/*
			 * 4. 결번 시점
			 */
			}else if("04".equals(sProcessId)){
				//SKIP
			/*
			 * 5. 보류재 처리 시점
			 */
			}else if("05".equals(sProcessId)){
				
				isVal = setInnerIFCoilInfo_03( sCoilNo,  sYardID );
					
			/*
			 * 6. 목전충당 시점
			 */
			}else if("06".equals(sProcessId)){
				//SKIP
			/*
			 * 7. HFL처리시점
			 */
			}else if("07".equals(sProcessId)){
				
				isVal = setInnerIFCoilInfo_02( sCoilNo,"HFL");
				
			/*
			 * 8. 모 Coil 종료
			 */
			}else if("08".equals(sProcessId)){
				
				isVal = setInnerIFCoilInfo_05( sCoilNo );
			/*
			 * 9. 자 Coil
			 */
			}else if("09".equals(sProcessId)){
				
				isVal = setInnerIFCoilInfo_06( sCoilNo );
			/*
			 * 10. 반납 시점
			 */
			}else if("10".equals(sProcessId)){
				
				isVal = setInnerIFCoilInfo_04( sCoilNo );
			
			/*
			 * J2. SPM 재작업
			 */
			}else if("J2".equals(sProcessId)){
				
				isVal = setInnerIFCoilInfo_07( sCoilNo, sYardID);
			
			/*
			 * Q2. EQL 재작업
			 */
			}else if("Q2".equals(sProcessId)){
					
					isVal = setInnerIFCoilInfoEQL_07( sCoilNo, sYardID);	
			/*
			 * 91. 조업시스템 에러.
			 *		Coil 공통 테이블에 있는 정보만 처리한다.
			 */
			}else if("91".equals(sProcessId)){		
				
				isVal = setInnerIFCoilInfo_01( sCoilNo, sYardID , "91");						
			/*
			 * DC. DC OFF.
			 *		Coil 공통 테이블에 있는 정보만 처리한다.
			 */
			}else if("DC".equals(sProcessId)){		
				
				isVal = setInnerIFCoilInfo_01( sCoilNo, sYardID , "DC");						
			/*
			 * 11. 요구차 공정 변경.
			 *		Coil 다음 공정 정보 변경시 처리.
			 */
			}else if("11".equals(sProcessId)){		
				
				isVal = setInnerIFCoilInfo_11( sCoilNo, sYardID , sProcessCode);						
			}
			/*
			 * HP. HR-Plate 생성
			 * 2008.01.04 이정훈 
			 */
			else if("HP".equals(sProcessId)){		
				
				isVal = setInnerIFCoilInfo_HP( sCoilNo, sYardID , sProcessCode);						
			}
			/** 최규성 추가
			 * 12. SPM2 정정실적 처리
			 * 
			 */
//			else if ("12".equals(sProcessId)){
//				isVal = setInnerIFCoilInfo_02( sCoilNo, "SPM2");
//			}
			/**
			 * 최규성 추가 
			 * 13. SPM2 자Coil 실적처리
			 * */
//			else if ( "13".equals(sProcessId)){
//				isVal = setInnerIFCoilInfo_06( sCoilNo, "N" );
//			}
			/***
			 * 최규성 추가
			 * 14. 모Coil 종료 처리
			 */
//			else if("14".equals(sProcessId)){
//				isVal = setInnerIFCoilInfo_05( sCoilNo,"N" );
//			}
			/***
			 * 최규성 추가 
			 * 15. SPM2 재작업
			 * 
			 */
//			else if ("15".equals(sProcessId)){
//				isVal = setInnerIFCoilInfo_07( sCoilNo, sYardID,"N");
//			}
			/**
			 * 최규성 추가
			 * 16.요구차 공정 변경
			 * 
			 */
//			else if ("16".equals(sProcessId)){
//				isVal = setInnerIFCoilInfo_11( sCoilNo, sYardID , sProcessCode);
//			}
			return isVal;
		} 
				      /**
		 * 오퍼레이션명 : 
		 *  
		 * YJK - INNER INTERFACE
	        * 업무 로직
	        *	1.TC_CD - POYM002
	        *	2.COIL 결번 실적 조업=>야드
	        *	3.조업에서 미 보급된 Coil 결번 시점
	        *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param 
		 * @return
		 * @throws 
		 */
		public boolean receivePOYM002(JDTORecord rcvMsg) { 
	         
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
	        logger.println(LogLevel.DEBUG,this," CoilInfoRegEJB:receivePOYM002(POYM002)");
	        
//S	        String sTc 			= StringHelper.evl(yModel.getTcCode(),"");
//S			String sDate 		= StringHelper.evl(yModel.getTcDate(),"");
//S			String sTime 		= StringHelper.evl(yModel.getTcTime(),"");
//S			String sGoodsNo 	= StringHelper.evl(yModel.getCoilNo(),"");

//	        String sTc 			= StringHelper.evl(rcvMsg.getFieldString("tcCode"),"").trim();
//			String sDate 		= StringHelper.evl(rcvMsg.getFieldString("tcDate"),"").trim();
//			String sTime 		= StringHelper.evl(rcvMsg.getFieldString("tcTime"),"").trim();
//			String sGoodsNo 	= StringHelper.evl(rcvMsg.getFieldString("coilNo"),"").trim();
			
	        String sTc 			= StringHelper.evl(rcvMsg.getFieldString("TcCode"),"").trim();
			String sDate 		= StringHelper.evl(rcvMsg.getFieldString("TcDate"),"").trim();
			String sTime 		= StringHelper.evl(rcvMsg.getFieldString("TcTime"),"").trim();
			String sGoodsNo 	= StringHelper.evl(rcvMsg.getFieldString("CoilNo"),"").trim();
			
			
			logger.println(LogLevel.DEBUG,this," TcCode 		=" + sTc);
			logger.println(LogLevel.DEBUG,this," TcDate 		=" + sDate);
			logger.println(LogLevel.DEBUG,this," TcTime 		=" + sTime);
			logger.println(LogLevel.DEBUG,this," CoilNo 		=" + sGoodsNo);
			logger.println(LogLevel.DEBUG,this,"==COIL 결번 실적 전문수신==");
			
			boolean isVal = false;
			try{
				int iSeq = 0;
				YdStockDAO dao	= new YdStockDAO();
				/**
			     *	1.	코일공통 진도코드 Table 참조.
			     */
			     	String[] sStockInfo = YmCommonUtil.getCoilCurrProgCd(sGoodsNo,"");
				    String sProgCd   	= sStockInfo[0];
					String sStocMv   	= sStockInfo[1];
					
			    	logger.println(LogLevel.DEBUG,this,"저장품 이동조건 = "+ sStocMv );
					
					if(!"".equals(sStocMv)){ 
				    	iSeq = dao.updateStockTransInfo(sGoodsNo,
														sStocMv);	
				    }
			
			isVal = true; 
			}catch(DAOException daoe){
		        throw daoe;
		    }catch(Exception e){
		        throw new EJBServiceException(e);
		    }    				
	        return isVal;
	    }	    	    

	    /**
		 * 오퍼레이션명 : 
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param 
		 * @return
		 * @throws 
		 */   
	    public String setStockZoneMoveList( String YD_GP,				//야드구분
											String BAY_GP,				//동구분(동간이적시 목적동)
											String SECT_GP,				//스판구분
											String COL_GP,				//열구분
											String From_BAY_GP,			//동구분(현재동)
											String Move_Kind,			//동간(S),동내(O)
											String facility_GP,			//없슴
											String facility_GP_Code,	//스케쥴코드
											String temp_stock_id,		//저장품
											String modifier,       		//수정자
											String YD_ZONE_GP       	//ZONE구분
											) {
	        String alertString 		= "이적처리가 정상적으로 등록되었습니다.";
	        
	        try{
	        	/*
				 * 구자원 단계별 삭제 로직  
				 */
				String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
				if(sAPP060_OLDSRC_YN.equals("Y")){
					return null;
				}
				
				ymCommonDAO dao = ymCommonDAO.getInstance();
				
				// 웹페이지에서 넘겨받은 데이터  CGS
				/**
				 *	1. 스케쥴 코드 셋팅 
				 */ 
				String sSchCode = facility_GP_Code;
				/**
				 *	2. 동내,동간 이적 구분 셋팅
				 */ 
				String sWorkGbn = Move_Kind;
				/**
				 *	3. 야드구분
				 */ 
				String sYdGp = YD_GP;
				/**
				 *	4. 동구분
				 */ 
				String sToBayGp   = BAY_GP;
				String sFromBayGp = From_BAY_GP;
				/**
				 *	5. 스판구분
				 */ 
				String sSectGp = SECT_GP;
				/**
				 *	6. 열구분
				 */ 
				String sColGp = COL_GP;
				/**
				 *	7. 저장품리스트
				 */ 
				String sStockList   = temp_stock_id;
				
				String vStockList[] = sStockList.split("-");
				/**
				 *	8. 수정자
				 */ 
				String sModifier = modifier;
				
				/**
				 *	9. ZONE구분
				 */ 
				String sYdZoneGp = YD_ZONE_GP;
				
				
				
				List wbookList = new ArrayList();
				List stockList = new ArrayList();
				List layerList = new ArrayList();
				JDTORecord wbookJr = null;
				// CGS 로그 추가
				logger.println(LogLevel.DEBUG, this, "facility_GP : " + facility_GP);
				logger.println(LogLevel.DEBUG, this, "수신 :스케줄코드 " + sSchCode);
				logger.println(LogLevel.DEBUG, this, "수신 :동내,동간 이적 구분 셋팅 " + sWorkGbn);
				logger.println(LogLevel.DEBUG, this, "수신 :야드구분 " + sYdGp);
				logger.println(LogLevel.DEBUG, this, "수신 :동구분(From) " + sFromBayGp);
				logger.println(LogLevel.DEBUG, this, "수신 :동구분(To) " + sToBayGp);
				logger.println(LogLevel.DEBUG, this, "수신 :스판구분 " + sSectGp);
				logger.println(LogLevel.DEBUG, this, "수신 :열구분 " + sColGp);
				logger.println(LogLevel.DEBUG, this, "수신 :저장품리스트 " + sStockList);
				logger.println(LogLevel.DEBUG, this, "수신 :수정자 " + sModifier);
				logger.println(LogLevel.DEBUG, this, "수신 :존구분 " + sYdZoneGp);
				
		 

				logger.println(LogLevel.DEBUG, this, "==== 시작 : 박판열연 Coil 이적처리 ====");	 
				String sCtsYn  = ""; 
				String sCtsBay = "";
				
				//from 동 재료에서 가져 오기
				String sQueryId10 = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.getStackLayerCheck2";
				JDTORecord stockJr2 = dao.getCommonInfo(sQueryId10 , new Object[]{vStockList[0]});
				
				
				logger.println(LogLevel.DEBUG, this, "==== 입력받은 from동 ===>>:"+sFromBayGp);	
				if(stockJr2 != null){
					sFromBayGp  = StringHelper.evl(stockJr2.getFieldString("BAY_GP"),"");			 
    			}
				logger.println(LogLevel.DEBUG, this, "==== 저장위치기준 from동 ===>>:"+sFromBayGp);
				
				if(YmCommonConst.YD_GP_1.equals(sYdGp)){
					String sQueryId2	 = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.getCTSRelayYNStat";
	    			JDTORecord equipJr   = dao.getCommonInfo(sQueryId2,
	    													 new Object[]{"1XTC01",
	    																  sFromBayGp,
																		  sToBayGp,
																		  sFromBayGp,
																		  sToBayGp});
	    			if(equipJr != null){
		    			sCtsYn  = StringHelper.evl(equipJr.getFieldString("중계구분"),"");
						sCtsBay = StringHelper.evl(equipJr.getFieldString("중계동"),"");
	    			}
				}
				// vStockList.length는 웹페이지에서 이적 선택한 갯수
				for(int i = 0 ; i < vStockList.length ; i++){
					
					// 저장품 검사.
					/*
					 * 	select STOCK_ID 
					 * 	  from TB_YM_STOCK
					 *   where STOCK_ID = :vStockList --저장품(ID)
					*/
					String sQueryId3	  = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.getStackLayerCheck1";
    				JDTORecord stockJr    = dao.getCommonInfo(sQueryId3,
    														  new Object[]{vStockList[i]});
    			
					if(stockJr == null){
						alertString = "저장품DB에 이적하려는 대상이 없습니다.";
						break;
					}else{
						// 작업예약ID 가져온다.
						/*
						 * SELECT TO_CHAR(SYSDATE,'YYYYMMDDHH24MI')||LPAD(YM_WBOOK_SEQ.NEXTVAL,6,'0') AS WBOOK_ID
						 * FROM DUAL
						 */
						String sQueryId4	= "ym.steelinfo.steelinforecv.dao.YdStockDAO.getWBookID";
    					wbookJr = dao.getCommonInfo(sQueryId4, new Object[]{});
						
						String sWbookId = StringHelper.evl(wbookJr.getFieldString("WBOOK_ID"),"");
						//========================================================================================================================
						// CGS 추가
						String sToLoc = "";	// 
						
						if (sWorkGbn.equals("S"))	// 동간이적일 경우
						{
							sSchCode = facility_GP_Code.substring(0,  4);
						}
						//========================================================================================================================

						// 작업예약리스트 순서
						// 작업예약ID
						// From 야드 구분
						// From 동 구분
						// 스케줄코드
						// 위치지정방법
						// PUT위치 
						// 작업예약근
						// 작업예약조
						// 작업예약스케줄조건	
						wbookList.add(sWbookId);				// 작업예약ID
						wbookList.add(sYdGp);					// From 야드 구분
						wbookList.add(sFromBayGp);				// From 동 구분
						wbookList.add(sSchCode);				// 스케줄 코드 						
						wbookList.add(sWorkGbn);				// 위치지정 방법 'S': 시스템, 'O': 조업자지정
						
						if("S".equals(sWorkGbn)){		// 동간이적
							wbookList.add("");			//야드+동+스판+열(동내이적일때만입력) 
						}else{							// 동내이적
							wbookList.add(sYdGp+ sFromBayGp+ sSectGp+ sColGp);//야드+동+스판+열(동내이적일때만입력)
						}
						
						wbookList.add(YmCommonUtil.getWorkDuty());
						wbookList.add(YmCommonUtil.getWorkParty());
						wbookList.add("O");
						
						if("S".equals(sWorkGbn)){		// 동간이적
							wbookList.add(sCtsYn);
							wbookList.add(sCtsBay);
							wbookList.add(sToBayGp);
							wbookList.add(sYdZoneGp);							
						}else{							// 동내이적							
							wbookList.add("");
							wbookList.add("");
							wbookList.add("");
							wbookList.add("");
						}
						wbookList.add(sModifier);
						
						logger.println(LogLevel.DEBUG, this, "작업예약List : " + wbookList);
						
						/*
						 *	저장품지정 동간이적일 경우 10자리 정보를 셋팅
						 */
						// 저장품 하차 위치
						String sUnPutLoc = "";
						if(facility_GP.equals(YD_GP+BAY_GP+SECT_GP+COL_GP)){
							sUnPutLoc = facility_GP;
						}else{
							sUnPutLoc = sToBayGp;
						}
						
						stockList.add(sWbookId);
						if("S".equals(sWorkGbn)){	// 동간이적 
							stockList.add(sCtsYn);
							stockList.add(sCtsBay);
							stockList.add(sYdGp);
							stockList.add(sToBayGp);
							stockList.add(sUnPutLoc);
							stockList.add("");
						}else{
							stockList.add("");
							stockList.add("");
							stockList.add("");
							stockList.add("");
							stockList.add("");
							stockList.add("");
						}
						stockList.add(sModifier);
						stockList.add(sYdZoneGp);
						stockList.add(vStockList[i]);
		
						layerList.add(sModifier);
						layerList.add(vStockList[i]);
// 로그 추가. 최규성
						logger.println(LogLevel.DEBUG, this, "편집완료된 데이터 확인");
						logger.println(LogLevel.DEBUG, this, "작업예약List : " + wbookList);
						logger.println(LogLevel.DEBUG, this, "저장품List : "   + stockList);
						logger.println(LogLevel.DEBUG, this, "레이어List : "   + layerList);
						String sQueryId5 = "ym.steelinfo.steelinforecv.dao.YdStockDAO.insertStockZoneMoveWbook";
						String sQueryId6 = "ym.steelinfo.steelinforecv.dao.YdStockDAO.updateStockZoneWBookID";
						String sQueryId7 = "ym.steelinfo.steelinforecv.dao.YdStockDAO.updateStockStat";
						// 작업예약 처리. 각 테이블에 관련 데이터 입력 및 수정 처리.
						int iRt = moveProcess(sQueryId5,
										    sQueryId6,
										    sQueryId7,
										    wbookList, 
										    stockList, 
										    layerList);
					}
		
					wbookList.clear();
					stockList.clear();
					layerList.clear();
					
				}
			 
				logger.println(LogLevel.DEBUG, this, "setStockZoneMoveList() 처리 완료 " );
	        }catch(DAOException daoe){
	            throw daoe;
	        }catch(Exception e){
	            throw new EJBServiceException(e);
	        }
	        return alertString;
	    }//setStockZoneMoveList
} 



