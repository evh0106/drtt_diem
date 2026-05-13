package com.inisteel.cim.ym.facilitystatus.facilityinquiry.session;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.log.LogService;
import jspeed.base.log.LogServiceConfig;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.util.StringHelper; // cgs

import com.inisteel.cim.common.util.CommonUtil;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.common.jms.model.CommonModel;
import com.inisteel.cim.common.jms.model.dm.YMDM002;
import com.inisteel.cim.common.jms.model.pc.ZZPC001;
import com.inisteel.cim.common.parser.Level2Parser;
import com.inisteel.cim.common.util.CommonUtil; // cgs
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdEquipDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdStackColDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO;
import com.inisteel.cim.ym.steelinfo.steelinforecv.dao.YdStockDAO;
/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="CTSStatusRegEJB" jndi-name="JNDICTSStatusReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class CTSStatusRegSBean extends BaseSessionBean {
	private Logger logger 			= null;
    private ymCommonDAO ymCommonDAO = null;
    private CraneSchDAO dao = null;
	private YmComm ymComm = new YmComm();
	
	public void ejbCreate() {
		LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		logger = new Logger(config);
		ymCommonDAO = new ymCommonDAO();
		
	}
		
	/**
	 * 오퍼레이션명 : 
	 *
	 * 야드 Level-2로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * 1.TC_CD	: 미정.
        * 2.I/F ID	: 미정.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */          
	public boolean vicCarMoveResult(String msg) {
        logger.println(LogLevel.DEBUG, this, "대차 출발 실적 처리");
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
	        if(msg == null) {
	            logger.println(LogLevel.DEBUG, this, "수신 데이터: "+ msg);
	            return false;
	        }else if(YmCommonConst.BAY_GP_A.equals(msg)) {
	            editCarLoadState(YmCommonConst.STACK_COL_GP_1ATC03);	            
	        }else if(YmCommonConst.BAY_GP_B.equals(msg)) {
	            editCarLoadState(YmCommonConst.STACK_COL_GP_1BTC03);
	        }else {
		        ymCommonDAO.modifyStockStatOfLayer("", YmCommonConst.STACK_LAYER_STAT_E, msg);	            
	        }
        }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
	    return true;
	}

	/**
	 * 오퍼레이션명 : 
	 *
	 * 차량 출발 지시를 처리한다.
        * 1.TC_CD	: 없음.
        * 2.I/F ID	: 없음.
        * param 	gp		이송상차1,이송하차2 구분
        * param 	cardNo	차량카드번호
        * param 	pos		정지위치
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */               
	public boolean carStartOrder(String gp, String cardNo, String pos) {
        logger.println(LogLevel.DEBUG, this, "차량 출발 지시 처리");
        logger.println(LogLevel.DEBUG, this, "수신MSG: "+ (gp + cardNo + pos));
        
        JDTORecord inRecord = JDTORecordFactory.getInstance().create();
        int       intRtnVal    = 0;
		JDTORecordSet    outRecSet	= null;
		JDTORecordSet    rsTemp     = null;
		JDTORecordSet    rsResult    = null;
		CoilGdsJspDao dao = new CoilGdsJspDao();
 
		
		String sYD_CAR_SCH_ID 	  = "";
		String sSTOCK_ID 		  = "";
		String queryID 			  = "";
		String szCARD_NO_CHK	  = "";
	    String szCHK_YN		  	  = "N";
	    
		List loadList = null;
	    try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
	        /**
	         * 차량카드번호, 정지위치 체크 
	         */
	        validCarArrivalAndStart(cardNo, pos);
	        	        
	        
	        //장비번호 가져오기 					
			queryID	= "ym.tsinfo.getLoadendLayer";
			loadList = ymCommonDAO.getCommonList(queryID, new Object[]{pos});
			
			if(loadList.size()>0){
				JDTORecord FrtoProduct = (JDTORecord)loadList.get(0);	    	
				szCARD_NO_CHK = StringHelper.evl(FrtoProduct.getFieldString("CARD_NO"),"");
		    	
				logger.println(LogLevel.DEBUG, this, "carStartOrder():다른 차량 존재여부 현재 카드체크:"+szCARD_NO_CHK+" , 입력 카드번호:"+cardNo);
				
				//다른 차량이 존재 하는 경우 
		    	if(!szCARD_NO_CHK.equals(cardNo) && !"".equals(szCARD_NO_CHK)&&  !"".equals(cardNo)){
		    		szCHK_YN ="Y";
		    	}
			}
			logger.println(LogLevel.DEBUG, this, "carStartOrder():다른 차량 존재여부 szCHK_YN:"+szCHK_YN );
 
	        //차량ID값을 가져 온다 
	        List 	list = ymCommonDAO.readcarinfoOfwloc(cardNo,pos);			
			if(list.size()> 0){
				JDTORecord jtR = (JDTORecord)list.get(0);
				sYD_CAR_SCH_ID 	= YmCommonUtil.paraRecChkNull(jtR,"YD_CAR_SCH_ID");
	 		 
			}
			
			if(szCHK_YN.equals("N")){
		        /**
		         * 차량에 저장품이
		         * 1. 적치되어 있으면 하차동으로 출발
		         * 2. 적치되어 있지 않으면 하차 출발
		         */	        
		        List stocks 	= ymCommonDAO.readStockOfCarLoad(pos);
		        int stocksCnt 	= stocks != null ? stocks.size() : 0;
		        logger.println(LogLevel.DEBUG, this, "carStartOrder(): "+String.valueOf(stocksCnt));
		        
		        if(stocksCnt > 0) {
		        	 
					
			        /**
			         * 상차완료인지 확인한다.
			         */
		        	logger.println(LogLevel.DEBUG, this, "carStartOrder():상차완료인지 확인한다.");
		            confirmCarStart(stocks, stocksCnt);
			        /**
			         * 저장품 정보 UPDATE
			         * 1. 설비 테이블 UPDATE
			         * 2. 적치열 차량CARD번호 CLEAR
			         * 3. 적치단 저장품ID CLEAR
			         */
			        editCardReferenceOfStock(pos);
			        
			        //차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
			        EJBConnector ejbConn2 = new EJBConnector("default","JNDITsInfoReg",this);
					ejbConn2.trx("CarPointinforeg", new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class},
				  	             new Object[]{"B","","",pos,"","","C"});
			        
 
			        /**
			         * 저장품에 현재 위치 셋팅
			         */
			        logger.println(LogLevel.DEBUG, this, "carStartOrder():저장품에 현재 위치 셋팅");
			        setCardReferenceOfStock(stocks, stocksCnt);
			        
			        
			        /**
			         * 야드 L-2 송신
			         * YD_GP 1,0 일 경우는 송신 않함
			         */
			        if(! YmCommonConst.YD_GP_1.equals(pos.substring(0, 1)) &&  ! YmCommonConst.YD_GP_0.equals(pos.substring(0, 1))) {
			        	/*
			        	 * [기능 추가 : (2009.01.10 KBK)] 최규성 
			        	 * 차량카드번호가 '9999~9995(차량동간이적)'가 아닐때만 야드 L-2에 송신
			        	 */    	
			        	 /* ======== Start ============*/
			        	logger.println(LogLevel.DEBUG, this, "carStartOrder():YD_GP 1,0 일 경우, 차량이적일 경우는 송신 않함");
	
			        	if ((!(cardNo.equals(YmCommonConst.CAR_BAY_TRANS_CARD_NO_1))
			 	    			&& !(cardNo.equals(YmCommonConst.CAR_BAY_TRANS_CARD_NO_2))
			 	    			&& !(cardNo.equals(YmCommonConst.CAR_BAY_TRANS_CARD_NO_3))
			 	    			&& !(cardNo.equals(YmCommonConst.CAR_BAY_TRANS_CARD_NO_4))
			 	    			&& !(cardNo.equals(YmCommonConst.CAR_BAY_TRANS_CARD_NO_5)))) {
			        		
			        		sendStartAndArrivalOrder(stocks, (JDTORecord)stocks.get(0), cardNo, pos, YmCommonConst.CAR_GP_S);
						}
			        	
			        	 /* ======== End ==============*/ 
			        	 
			        }
			        
			        //A열연 SLAB야드 PALLET 출발시 맵정보 전송
			        if(YmCommonConst.YD_GP_0.equals(pos.substring(0, 1))){
			        	logger.println(LogLevel.DEBUG, this, "차량 출발 지시 처리 A열연 SLAB야드 일경우 MAP정보 전송");
						
			        	String sMsg = YmCommonUtil.setBSlabMapMsgInfo(pos+YmCommonConst.STACK_BED_GP_01+YmCommonConst.STACK_LAYER_GP_01);
						
						EJBConnector ejbConn = new EJBConnector("default", "JNDICraneStatusReg", this);
						Boolean isSucf = (Boolean) ejbConn.trx("bsyYdMapInfo", new Class[] { String.class }, new Object[] { sMsg });		        	
			        }
		        }else {
		        	
		        	logger.println(LogLevel.DEBUG, this, "stocksCnt <= 0");
		            // 맵 정리 
		        	editUnloadState(pos);
		        	
		        	  //차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
			        EJBConnector ejbConn2 = new EJBConnector("default","JNDITsInfoReg",this);
					ejbConn2.trx("CarPointinforeg", new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class},
				  	             new Object[]{"B","","",pos,"","","C"});
		        	
			        /**
			         * 야드 L-2 송신
			         */
		            stocks = ymCommonDAO.readStockInfoOfCardNo(cardNo);
			        unloadClear(stocks);
			        //A열연 SLAB야드 PALLET_NO로 입력할 경우도 있기때문에 DB에서 CAR_CARD_NO를 가져옴.(MCH)
			        if(stocks.size()>0){
			        	JDTORecord jrecrd = (JDTORecord)stocks.get(0);
			        	cardNo = StringHelper.evl(jrecrd.getFieldString("CAR_CARD_NO"),cardNo);
			        }
			        /*
		        	 * [기능 추가 : (2009.01.10 KBK)] 최규성 
		        	 * 차량카드번호가 '9999~9995(차량동간이적)'가 아닐때만 야드 L-2에 송신
		        	 */    	
		        	 /* ======== Start ============*/
		        	logger.println(LogLevel.DEBUG, this, "carStartOrder():차량이적일 경우는 송신 않함");
		        	
		        	if ((!(cardNo.equals(YmCommonConst.CAR_BAY_TRANS_CARD_NO_1))
		 	    			&& !(cardNo.equals(YmCommonConst.CAR_BAY_TRANS_CARD_NO_2))
		 	    			&& !(cardNo.equals(YmCommonConst.CAR_BAY_TRANS_CARD_NO_3))
		 	    			&& !(cardNo.equals(YmCommonConst.CAR_BAY_TRANS_CARD_NO_4))
		 	    			&& !(cardNo.equals(YmCommonConst.CAR_BAY_TRANS_CARD_NO_5)))) {
		        		
		        		sendStartAndArrivalOrder(cardNo, pos, gp, YmCommonConst.CAR_GP_S);	
		        	}
		        	 /* ======== End ==============*/ 
		        }
		        
		 
		        /**
		         * 적치대의 적재능력을 초기화
		         */
		        ymCommonDAO.modifyPossibleOfStacker(pos, YmCommonConst.STACK_BED_GP_01);
			}
	        
				
			//차량스케줄 종료처리 
	        logger.println(LogLevel.DEBUG, this, "carStartOrder():차량스케줄 종료처리:"+sYD_CAR_SCH_ID);
				
	        if(!"".equals(sYD_CAR_SCH_ID)) {

				inRecord = JDTORecordFactory.getInstance().create(); // 
				inRecord.setField("V_MODIFIER"		, "carStart");
				inRecord.setField("V_YD_CAR_SCH_ID"	, sYD_CAR_SCH_ID);
				
				/* com.inisteel.cim.ym.jsp.coiljsp.dao.CoilGdsJspDao.delCarSchMtlLayer_PIDEV */
			    intRtnVal = dao.delCarSchMtlLayer(inRecord);
			    
			    /* com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.delCarWrMgtCarSchMtl */
			    intRtnVal = dao.delCarWrMgtCarSchMtl(inRecord);
			    
			    /* com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.delCarWrMgtCarSch */
			    intRtnVal = dao.delCarWrMgtCarSch(inRecord);
				if (intRtnVal <= 0) {				 
					logger.println(LogLevel.DEBUG, this, "차량SCH 삭제중 ERROR 발생 ");
					return false;
				} 
				
			}
	        
	        
	    }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
	    return true;
	}
		/**
		 * 오퍼레이션명 : 
		 *
		 * 차량 출발 지시를 처리한다.(ET,해송차량)
	        * 1.TC_CD	: 없음.
	        * 2.I/F ID	: 없음.
	        * param 	gp		이송상차1,이송하차2 구분
	        * param 	cardNo	차량카드번호
	        * param 	pos		정지위치
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param 
		 * @return
		 * @throws 
		 */               
		public boolean hscarStartOrder(JDTORecord dModel) {
	        logger.println(LogLevel.DEBUG, this, "차량 출발 지시 처리");
	        
			String cardNo	 		= StringHelper.evl(dModel.getFieldString("CARD_NO"),"");
			String SPOS_WLOC_CD 	= StringHelper.evl(dModel.getFieldString("SPOS_WLOC_CD"),"");
			String SPOS_YD_PNT_CD 	= StringHelper.evl(dModel.getFieldString("SPOS_YD_PNT_CD"),"");
		    try {
				/*
				 * 구자원 단계별 삭제 로직  
				 */
				String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
				if(sAPP060_OLDSRC_YN.equals("Y")){
					return false;
				}
				
		    	/**
		    	 * 적치열 정보 조회
		    	 */
		    	JDTORecord jReocd = ymCommonDAO.readStackCol(SPOS_WLOC_CD,SPOS_YD_PNT_CD);

		    	String pos = jReocd.getFieldString("STACK_COL_GP");
		    	
		    	/**
		    	 * 차량 출발 지시를 처리한다.
		    	 */		    	
		    	boolean chk = this.carStartOrder("", cardNo, pos);
			    if(!chk){
			        	return false;
			        }
			    
		    	/**
		    	 * 차량포인트예약정보 비우기
		    	 */	
//			    ymCommonDAO dao = ymCommonDAO.getInstance();
//			    String sQueryId = "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.updYdStackcolCarPint2";
//			    int count = dao.updateData(sQueryId,new Object[]{cardNo});
			    
		    }catch(DAOException daoe) {
	            throw daoe;
	        }catch(Exception e) {
	            throw new EJBServiceException(e);
	        }
		    return true;
		}
		
		
	
	/**
	 * 차량 하차 출발시에 저장품/적치열/적치단 테이블의 관련 정보를 CLEAR
	 * @param pos 하차정지위치
	 */
	private void editUnloadState(String pos) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        ymCommonDAO.modifyCardNoOfStackCol("", pos);
        
        ymCommonDAO.modifyStockStatOfLayer("", YmCommonConst.STACK_LAYER_ACTIVE_STAT_C, YmCommonConst.STACK_LAYER_STAT_E, pos);	    
	}

    /**
     * 차량 출발이 가능한지 저장품 정보를 확인한다.
     * CE	Coil소재 이송상차 완료
     * GE	Coil제품 이송상차 완료
     * H5	Coil 출하 상차완료
     * @param stocks	저장품 이동 조건 정보
     * @throws Exception
     */
    private void confirmCarStart(List stocks, int stocksCnt) throws Exception {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        String term 		= null;
        JDTORecord moveTerm = null;
        for(int i = 0; i < stocksCnt; i++) {
            moveTerm 	= (JDTORecord)stocks.get(i);
            term		= getField(moveTerm, "STOCK_MOVE_TERM");
            if(! YmCommonConst.NEW_STOCK_MOVE_TERM_VL.equals(term) &&
               ! YmCommonConst.NEW_STOCK_MOVE_TERM_MG.equals(term) &&
               ! YmCommonConst.NEW_STOCK_MOVE_TERM_E1.equals(term) &&
               ! YmCommonConst.NEW_STOCK_MOVE_TERM_C1.equals(term)) {
                //throw new Exception("저장품 정보가 출발 완료 상태가 아닙니다");
            }
        }
    }
    
    /**
     * 설비/적치열/적치단 테이블의 '적재상태', '차량카드번호', '적치상태'를 UPDATE
     * @param pos	차량 정지 위치
     */
    private void editCardReferenceOfStock(String pos) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
    	logger.println(LogLevel.DEBUG, this, "설비 테이블 UPDATE");
        ymCommonDAO.modifyWBookAndLoadSchOfEquip(YmCommonUtil.getStringYMDHM(), "", pos);
        logger.println(LogLevel.DEBUG, this, "적치열 차량CARD번호 CLEAR");
        ymCommonDAO.modifyCardNoOfStackCol("", pos);		
        logger.println(LogLevel.DEBUG, this, "적치단 저장품ID CLEAR");
        ymCommonDAO.modifyStockStatOfLayer("", YmCommonConst.STACK_LAYER_ACTIVE_STAT_C, YmCommonConst.STACK_LAYER_STAT_E, pos);
    }

    /**
     * 복수동의 작업이 존재하는지 리턴한다.
     * @param cardNo	차량CARD번호
     * @param pos		차량정지위치
     * @return
     */
    private JDTORecord getMultyConfirm(String cardNo, String pos) {        
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        return ymCommonDAO.readMultyConfirm(cardNo, pos.substring(0, 1), pos.substring(1, 2));
    }

    /**
     * 멀티동이면 현재MAP 정보를 저장품에 UPDATE 한다.
     * @param stocks		차량 출발 조건 정보
     */
    private void setCardReferenceOfStock(List stocks, int stocksCnt) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
    	logger.println(LogLevel.DEBUG, this, "저장품에 현재 위치 설정:List"+stocks);
    	logger.println(LogLevel.DEBUG, this, "저장품에 현재 위치 설정:INT"+stocksCnt);
        for(int i = 0; i < stocksCnt; i++) {
            ymCommonDAO.modifyTermAndMoveEquipOfStock(
                    getField((JDTORecord)stocks.get(i), "STACK_COL_GP"), 
                    getField((JDTORecord)stocks.get(i), "STACK_BED_GP"), 
                    getField((JDTORecord)stocks.get(i), "STACK_LAYER_GP"), 
                    getField((JDTORecord)stocks.get(i), "STOCK_MOVE_TERM"),
                    getField((JDTORecord)stocks.get(i), "STOCK_ID"));
        }
    }

	/**
	 * 오퍼레이션명 : 
	 *
	 * 차량도착 정보를 처리한다.
        * 1.TC_CD	: 없음.
        * 2.I/F ID	: 없음.
        * param 	moveGp	차량 출하'1', 이송'2', 이적'3' 구분
        * param 	cardNo	카드번호
        * param 	pos		정지위치
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                    
	public boolean carArrival(String moveGp, String cardNo, String pos) {         
        logger.println(LogLevel.DEBUG, this, "차량도착 정보 처리 수신MSG: "+ (moveGp + cardNo + pos));
	    try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
	    	
	    	//육송인 경우에만 처리 함.
	    	if(!"T".equals(cardNo.substring(0, 1))&& !"P".equals(cardNo.substring(0, 1))){
	        /**
	         * 차량카드번호, 정지위치 체크
	         */
	        validCarArrivalAndStart(cardNo, pos);
	    	}
	    	
	        
	    	//육송인 경우에만 처리 함. 
    		if(!"T".equals(cardNo.substring(0, 1))
					&& !"P".equals(cardNo.substring(0, 1))
					&& !"A".equals(cardNo.substring(0, 1))
					&& !"B".equals(cardNo.substring(0, 1))
					&& !"C".equals(cardNo.substring(0, 1))
					&& !"K".equals(cardNo.substring(0, 1))
					&& !"S".equals(cardNo.substring(0, 1))
					){
		       /*
		        * 최규성 2009-10-16
		        * 카드번호에 따라서 if문의 처리를 다르게 한다.
		        * if문 조건 추가. 
		        */
		        if(Integer.parseInt(cardNo) >= 9995 && Integer.parseInt(cardNo) <= 9999 ){
		        	logger.println(LogLevel.DEBUG, this, "차량이적에 대한 조건 처리 ");
		        	if(isCarStart01(pos, cardNo)) {
	//		        	if(isCarArriveBay(pos,cardNo)){
			        		arrivalOfShippingOrTrans(cardNo, pos, moveGp);
	//		        	}else{
	//		        		return false;
	//		        	}
			        }else {
			        	return false;
			        }
		        }else{
		        	logger.println(LogLevel.DEBUG, this, "차량이적 외 조건 처리 ");
		        	
		        	if(!"T".equals(moveGp)&&!"R".equals(moveGp)){
		        		//내수 출하인 경우
		        		logger.println(LogLevel.DEBUG, this, "내수 출하인 경우 ");
				        if(isCarStartT(pos, cardNo)) {
				        	logger.println(LogLevel.DEBUG, this, "하이스코 2냉연 차량예약이 존재 안 합니다.");
				        }else {
				        	logger.println(LogLevel.DEBUG, this, "하이스코 2냉연 차량예약이 존재 합니다.");
				        	return false;
				        }
		        	} 
		        	
		        	if("R".equals(moveGp)){
		        		logger.println(LogLevel.DEBUG, this, "2냉연 트레일러 도착처리 ***********");
		        		arrivalOfShippingOrTrans(cardNo, pos, moveGp);   
		        	}else{
			        	if(isCarStart(pos, cardNo)) {
				        	
				        	arrivalOfShippingOrTrans(cardNo, pos, moveGp);        
				        }else {
				        	return false;
				        }
		        	}
		        }
	    	}else {
	        	logger.println(LogLevel.DEBUG, this, "차량이적 외 조건 처리2 ");
		        if(isCarStart(pos, cardNo)) {
		        	
		        	arrivalOfShippingOrTrans(cardNo, pos, moveGp);        
		        }else {
		        	return false;
		        }
	    	}
	    }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
	    return true;
	}
	
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 차량도착 정보를 BACKUP처리한다.
        * 1.TC_CD	: 없음.
        * 2.I/F ID	: 없음.
        * param 	moveGp	차량 출하'1', 이송'2', 이적'3' 구분
        * param 	cardNo	카드번호
        * param 	pos		정지위치
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                    
	public boolean carArrivalBackup(String cardNo) {
        logger.println(LogLevel.DEBUG, this, "차량도착 정보 BACKUP처리");
        logger.println(LogLevel.DEBUG, this, "수신MSG: "+ (cardNo));
	    try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
	    	JDTORecord model =null; 
	    	 ymCommonDAO dao = ymCommonDAO.getInstance();
	    	
	    	 String trnEqpQueryId = "ym.facilitystatus.facilityinquiry.session.CTSStatusRegSBean.stockcardYn2";
			 List stocklist = dao.getCommonList(trnEqpQueryId, new Object[]{cardNo});   
			 if(stocklist.size()>0){
				 logger.println(LogLevel.DEBUG,this, "차량정보의 작업예약이  이미 존재 함.");
				 return false ;
			 }
	    	
			 //차량번호로 운송지시 편성(출하쪽 정보을 읽어다가 처리)
	    	 trnEqpQueryId = "ym.facilitystatus.facilityinquiry.session.CTSStatusRegSBean.stockcardUpdate";
			 int chk = dao.updateData(trnEqpQueryId, new Object[]{cardNo});
			 
	    	
	    	 trnEqpQueryId = "ym.facilitystatus.facilityinquiry.session.CTSStatusRegSBean.stockcardYn";
			 stocklist = dao.getCommonList(trnEqpQueryId, new Object[]{cardNo});   
			 if(stocklist.size()<=0){
				 logger.println(LogLevel.DEBUG,this, "차량정보가 존재 안함..");
				 return false ;
			 }else {
				 
				 JDTORecord FrtoSltrec = (JDTORecord)stocklist.get(0);
				 
				 model = JDTORecordFactory.getInstance().create(); 
				 model.setField("YD_GP", 			StringHelper.evl(FrtoSltrec.getFieldString("YD_GP"), ""));
				 model.setField("TRANS_ORD_DT", 	StringHelper.evl(FrtoSltrec.getFieldString("TRANS_ORD_DT"), ""));
				 model.setField("TRANS_ORD_SEQNO", 	StringHelper.evl(FrtoSltrec.getFieldString("TRANS_ORD_SEQNO"), ""));
				 
				 EJBConnector ejbCon = new EJBConnector("default", "JNDIWorkOrderInfoReg", this);
				 Boolean isSucf = (Boolean)ejbCon.trx("hsCyGoodsChulHaSangChaGisiRegistInfo", 
						  new Class[]{ JDTORecord.class }, new Object[]{ model });
				 
				 if(isSucf.booleanValue()){
					 return true ;
				 } else {
					 return false ;
				 }
				 
			 }
			 
	    }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
	}
	
	
	/**
	 * 오퍼레이션명 : 
	 * 일관제철용 (ET카)
	 * 차량도착 정보를 처리한다.
        * 1.TC_CD	: 없음.
        * 2.I/F ID	: 없음.
        * param 	moveGp	차량 출하'1', 이송'2', 이적'3' 구분
        * param 	cardNo	카드번호
        * param 	pos		정지위치
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                    
	public boolean hscarArrival(JDTORecord dModel) {
        logger.println(LogLevel.DEBUG, this, "차량도착 정보 처리");
        Boolean isSuccess = new Boolean(false);
        
		String cardNo	 	= StringHelper.evl(dModel.getFieldString("CARD_NO"),"");
		String SPOS_WLOC_CD	= StringHelper.evl(dModel.getFieldString("SPOS_WLOC_CD"),"");
		String SPOS_YD_PNT_CD 	= StringHelper.evl(dModel.getFieldString("SPOS_YD_PNT_CD"),"");

		String sJmsTcCd = StringHelper.evl(dModel.getFieldString("JMS_TC_CD"), StringHelper.evl(dModel.getFieldString("TC_CODE"),""));
		String chk_yn ="";
	    try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
	    	/**
	    	 * 적치열 정보 조회
	    	 */
	    	JDTORecord jReocd = ymCommonDAO.readStackCol(SPOS_WLOC_CD,SPOS_YD_PNT_CD);

	    	String pos = jReocd.getFieldString("STACK_COL_GP");
			
	    	
	    	if(sJmsTcCd.equals("DMYDR036")){
	    		chk_yn ="Y";
	    	}
	    	/**
	    	 * 차량도착 정보를 처리한다.
	    	 */
	    	 boolean chk = this.carArrival(chk_yn, cardNo,  pos) ;
		     if(!chk){
		        	return false;
		        }

	    }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
	    return true;
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * A열연 SLAB 야드 차량도착 정보를 처리한다.
	 * - 공 팔레트 도착처리로직
	 * param 	moveGp				차량 출하'1', 이송'2',  이적'3' 구분
	 * param 	Palletno(cardNo)		카드번호(A열연 SLAB야드 Pallet = 카드번호)
	 * param 	pos					정지위치
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean AslabcarArrival(String moveGp, 
							  String Palletno, 
							  String pos) {
		
		logger.println(LogLevel.DEBUG, this, "A열연야드 차량도착 정보 처리 시작");
		logger.println(LogLevel.DEBUG, this, "수신MSG: "+ (moveGp + Palletno + pos));
	    	try {
		        if(isCarStart(pos, Palletno)) {
		        	setEmptyPalleteArriveInfo(Palletno, pos);    
		        }else {
		            return false;
		        }
		                 
		}catch(DAOException daoe) {
	       	throw daoe;
	       }catch(Exception e) {
	       	throw new EJBServiceException(e);
	       }
	    	return true;
	}	
	/**
	 * SLAB야드 공팔레트의 도착을 처리한다.
	 * @param Palletno	팔레트 번호
	 * @param pos		팔레트 정지위치
	 */
	private void setEmptyPalleteArriveInfo(String Palletno, 
								    String pos) throws Exception {
           
	   	String yd 		= pos.substring(0, 1);
		String bay 	= pos.substring(1, 2);
		int multysCnt 	= 0;
		List stocks   	= null;
	       String sSchCd	= ""; 
	        
		sSchCd = YmCommonConst.NEW_SCH_WORK_KIND_SVML;	// 이송상차
	       
	       logger.println(LogLevel.DEBUG, this, "공팔레트 : 야드구분=>"+yd);
	       logger.println(LogLevel.DEBUG, this, "공팔레트 :    동구분=>"+bay);
	       logger.println(LogLevel.DEBUG, this, "공팔레트 :    스케쥴=>"+sSchCd);
	       
	       /*
	        *	1. 공팔레트에 실어야 할 저장품정보 가져오기
	        */ 
		stocks = ymCommonDAO.readStockOfPalletLoad(sSchCd, yd, bay, pos);
		
		/*
	        *	2. 팔레트단 오픈 갯수
	        */ 
	       JDTORecord jReocd = ymCommonDAO.readbedmax(pos);

		int stocksCnt = jReocd.getFieldInt("STACK_MAX_QNTY");
		
		/*
	        *	3. 팔레트 도착정보를 MAP에 셋팅한다.
	        */ 
		editCardMapOpen( multysCnt,	// 이미 실려있는 저장품 정보 갯수 	
						stocksCnt, 	// 실어야 될 저장품 정보 갯수
						Palletno, 		// 팔레트 번호
						pos, 		// 팔레트 위치정보
						yd);			// 야드구분
		
		  //차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
        EJBConnector ejbConn2 = new EJBConnector("default","JNDITsInfoReg",this);
		ejbConn2.trx("CarPointinforeg", new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class},
	  	             new Object[]{"C","",Palletno,pos,"","","L"});
       	/*
	        *	4. 팔레트도착처리를 처리한다.
	        *		- 작업예약 정보 체크 후 사용자지정으로 수정
	        *		- 스케쥴 호출
	        */ 
		if(stocks != null && stocks.size() > 0){
        	
        		JDTORecord stock = (JDTORecord)stocks.get(0);
		       
		       doArrival(stocks, stock, Palletno, pos);
	       
        	}
    	}
    	
    	/**
	 * 차량 도착 정보를 처리한다.
	 * 1. 작업예약 UPDATE(MCH)
	 * 2. SCH CALL
	 * @param stocks	차량 도착 정보
	 * @param pos		차량 정지 위치
	 * @param gp		출하,이송 구분
	 * @throws Exception
	 */
	private void doArrival(	List stocks, 
						JDTORecord dto, 
						String cardNo, 
						String pos) throws Exception {
	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
		String yd = pos.substring(0, 1);
		String sSchCode ="";
		logger.println(LogLevel.DEBUG, this, "작업예약의 스케쥴 수행 방법을 stocks-"+stocks);
		logger.println(LogLevel.DEBUG, this, "작업예약의 스케쥴 수행 방법을 SCH_WORK_KIND-"+getField(dto, "SCH_WORK_KIND"));
		logger.println(LogLevel.DEBUG, this, "작업예약의 스케쥴 수행 방법을 pos-"+pos);
		logger.println(LogLevel.DEBUG, this, "작업예약의 스케쥴 수행 방법을 STOCK_ITEM-"+getField(dto, "STOCK_ITEM"));
		logger.println(LogLevel.DEBUG, this, "작업예약의 스케쥴 수행 방법을 cardNo-"+cardNo);
		
		sSchCode = getField(dto, "SCH_WORK_KIND");
		
    	//육송인 경우에만 처리 함.
		if(!"T".equals(cardNo.substring(0, 1))
				&& !"P".equals(cardNo.substring(0, 1))
				&& !"A".equals(cardNo.substring(0, 1))
				&& !"B".equals(cardNo.substring(0, 1))
				&& !"C".equals(cardNo.substring(0, 1))
				&& !"K".equals(cardNo.substring(0, 1))
				&& !"S".equals(cardNo.substring(0, 1))
				){
			if(Integer.parseInt(cardNo)>= 9995 && Integer.parseInt(cardNo) <= 9999){
				logger.println(LogLevel.DEBUG, this, "차량이적시 처리");
				editSchMethodOfWBook(stocks
						, getField(dto, "SCH_WORK_KIND")	
						, pos
						, getField(dto, "STOCK_ITEM")
						, "Y");
		
				ymCommonDAO.modifyWBookAndLoadSchOfEquip(YmCommonUtil.getStringYMDHM(), 
								                 getField(dto, "SCH_WORK_KIND"), 
								                 pos);
			}else{ 
				//제품이송 하차인 경우 처리 
				if(sSchCode.equals(YmCommonConst.NEW_SCH_WORK_KIND_GVM4)||sSchCode.equals(YmCommonConst.NEW_SCH_WORK_KIND_GVMU)){
					
					for(int i = 0; i < stocks.size(); i++) {
				       	ymCommonDAO.modifyOperatorOfWBook(sSchCode,
								                          	YmCommonConst.SCH_WORK_LOC_DECISION_METHOD_S,
								                          	"",
								                          	getField((JDTORecord)stocks.get(i), "WBOOK_ID"));                
					}
				}else{
					editSchMethodOfWBook(stocks
				    					, getField(dto, "SCH_WORK_KIND")
				    					, pos
				    					, getField(dto, "STOCK_ITEM"));
				}
				
				ymCommonDAO.modifyWBookAndLoadSchOfEquip(YmCommonUtil.getStringYMDHM(), 
		                 getField(dto, "SCH_WORK_KIND"), 
		                 pos);
	
			}
    	} else {
			editSchMethodOfWBook(stocks
					, getField(dto, "SCH_WORK_KIND")
					, pos
					, getField(dto, "STOCK_ITEM"));

			ymCommonDAO.modifyWBookAndLoadSchOfEquip(YmCommonUtil.getStringYMDHM(), 
			         getField(dto, "SCH_WORK_KIND"), 
			         pos);
    	}
		
		logger.println(LogLevel.DEBUG, this, "차량 도착 후 예약된 작업의 스케쥴을 CALL");
		callCarArrivalSch(stocks, stocks.size(), yd);
		
		if(!YmCommonConst.YD_GP_4.equals(yd) &&
		   !YmCommonConst.YD_GP_0.equals(yd)) {
			/* cgs주석 추가
				차량동간이적 CardNo일 때 송신하지 않음.
				일관제철용 소스에서는 sendDM() 이 주석처리되어 있음. 2009-09-29
			*/
			//sendDM(dto, cardNo);
			
		}
	}

	/**
	 * 작업예약의 스케쥴 수행 방법을 UPDATE
	 * @param carInfos	차량 도착 정보
	 * @param pos		차량 정지 위치
	 */
	private void editSchMethodOfWBook(	List carInfos, 
									String schKind, 
									String pos, 
									String item) {
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
										
		if(! getUnloadSchKind(item, pos).equals(schKind)) {		// 하차스케줄인지 검사. 아니면..
			for(int i = 0; i < carInfos.size(); i++) {
		       	ymCommonDAO.modifyOperatorOfWBook(
		       									getSchWorkKind(schKind, pos),
								                          	YmCommonConst.SCH_WORK_LOC_DECISION_METHOD_O,
								                          	pos,
								                          	getField((JDTORecord)carInfos.get(i), "WBOOK_ID"));                
			}
		}
	}
		
	/**
	 * 차량 도착 후 예약된 작업의 스케쥴을 CALL 한다.(MCH)
	 * @param stocks		차량 작업 예약 정보
	 * @throws Exception
	 */
	private void callCarArrivalSch(List stocks, int stocksCnt, String yd) throws Exception {
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
		if(YmCommonConst.YD_GP_2.equals(yd) ||
		   YmCommonConst.YD_GP_0.equals(yd)) {
			callCRSchedule(stocks, "syCraneScheduleInfoInsert", stocksCnt, yd);
		}else if(YmCommonConst.YD_GP_4.equals(yd)) {
			callCRSchedule(stocks, "syCraneScheduleInfoInsert", stocksCnt, yd);
		}else {
			callCRSchedule(stocks, "callCraneSchInfo", stocksCnt, yd);
		}
	}

    /**A열연 SLAB야드 추가(MCH)
     * 차량 하차 스케쥴을 콜한다.
     * @param stocks		저장품정보
     * @param methodName	메소드이름
     * @param stocksCnt		저장품 수
     */
    private void callCRSchedule(List stocks, String methodName, int stocksCnt, String yd) throws Exception {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        EJBConnector ejbConn = new EJBConnector("default", "JNDICraneSchReg", this);
        if(YmCommonConst.YD_GP_2.equals(yd)
           || YmCommonConst.YD_GP_0.equals(yd)) {
            StringBuffer wbIds = new StringBuffer();
            for(int i = 0; i < stocksCnt; i++) {
                wbIds.append(getField((JDTORecord)stocks.get(i), "WBOOK_ID")).append("-");
            }            
            ejbConn.trx(methodName, new Class[]{ String.class }, new Object[]{ wbIds.toString() });
            wbIds.setLength(0);
        }else {
            for(int i = 0; i < stocksCnt; i++) {
                ejbConn.trx(
                        methodName, 
                        new Class[]{ String.class }, 
                        new Object[]{ getField((JDTORecord)stocks.get(i), "WBOOK_ID") });
            }
        }
    }
    
       
	/**
	 * 차량 정지 위치의 차량카드번호가 존재하는지 리턴한다.
     * @param pos		차량정지위치
     * @param cardNo	차량카드번호
     * @return
     */
    public boolean isCarStart(String pos, String cardNo) {
    	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
        //해당 정지포인트에 차량카드번호가 존재하는지 확인
        JDTORecord dto = ymCommonDAO.readCardNo(pos);
        logger.println(LogLevel.DEBUG, this, "isCarStart() 점유차량 카드번호:"+getField(dto, "CAR_CARD_NO"));
        if(! "".equals(getField(dto, "CAR_CARD_NO"))) {
            return false;
        }else {
            //동일 차량카드번호 존재하는지 확인
            dto = ymCommonDAO.readCardNo(pos.substring(0, 1), cardNo);
            if(dto != null) {
                return false;
            }
            return true;
        }
    }
    
	/**
	 * 차량 정지 위치의 차량카드번호가 존재하는지 리턴한다.
     * @param pos		차량정지위치
     * @param cardNo	차량카드번호
     * @return
     */
    public boolean isCarStartT(String pos, String cardNo) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
        //해당 정지포인트에 차량카드번호가 존재하는지 확인
        JDTORecord dto = ymCommonDAO.readCardNoT(pos);
        logger.println(LogLevel.DEBUG, this, "isCarStartT() 점유차량 카드번호:"+getField(dto, "CAR_CARD_NO"));
        if(! "".equals(getField(dto, "CAR_CARD_NO"))) {
            return false;
        }else {
            //동일 차량카드번호 존재하는지 확인
            dto = ymCommonDAO.readCardNo(pos.substring(0, 1), cardNo);
            if(dto != null) {
                return false;
            }
            return true;
        }
    }
    /**
     * 차량 정지 위치의 차량카드번호가 존재하는지 리턴한다. 최규성 2009-10-15
     * 차량 도착 위치의 카드번호를 검사한다.
     * 기능 추가. 현재 도착처리 동이 정확한 동인지 검사. 최규성 2009-11-11
     * 
     * @param pos 		차량정지위치 
     * @param cardNo    차량카드번호
     * @return
     */
	private boolean isCarStart01(String pos, String cardNo) {
		
		logger.println(LogLevel.DEBUG, this, "isCarStart01() 차량 정지 위치의 차량카드번호 검사.");
		logger.println(LogLevel.DEBUG, this, "isCarStart01() 차량 정지 위치 검사.");
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		/*
		 * SELECT CARD_NO  AS CAR_CARD_NO
  			 FROM TB_YM_STACKCOL
 			WHERE STACK_COL_GP = :stackcol*/
		//String sQueryId_cardno = "ym.common.dao.selectCardNo2";
		//JDTORecord dto = ymCommonDAO.readCardNo(sQueryId_cardno, pos, cardNo);
		JDTORecord dto = ymCommonDAO.readCardNo( pos );
		
		if(! "".equals(getField(dto, "CAR_CARD_NO"))) {		// 카드번호가 없으면 
			return false;
		}else {												// 카드번호가 존재함
			//동일 차량카드번호 존재하는지 확인
			logger.println(LogLevel.DEBUG, this, "동일 차량카드번호 존재하는지 확인");
			
			/*
			 * SELECT  STACK_COL_GP, NVL(NVL(CARD_NO,TRN_EQP_CD),CAR_CARD_NO) AS CAR_CARD_NO
				 FROM    TB_YM_STACKCOL
				WHERE   YD_GP       = :yd
				  AND   CARD_NO = :cardno
				  AND   SECT_GP IN ('TR', 'PT')
				  AND   STACK_COL_GP = :stackcol
			 */
			String sQueryId_pos = "ym.common.dao.selectCardNo3";
			dto = ymCommonDAO.readCardNo(sQueryId_pos, pos, cardNo);
			
			
			if(dto != null) {
				logger.println(LogLevel.DEBUG, this, "동일 차량카드번호 존재함. (dto != null)");
				return false;
			}
			logger.println(LogLevel.DEBUG, this, "동일 차량카드번호 존재안함");
			return true;
		}
	}
	// 도착 처리동이 정보상의 현재동 또는 목적동과 동일한지 판별한다.
	// 최규성 2009-11-11
	/**
     * 현재 도착처리 동이 정확한 동인지 검사. 최규성 2009-11-11
     * 
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
     * @param pos 		차량정지위치 
     * @param cardNo    차량카드번호
     * @return
     */
	public boolean isCarArriveBay(String pos,String cardNo) throws Exception {
		String sQueryId_unload = "ym.facilitystatus.facilityinquiry.selectCarArriveBay";
		String sQueryId_load = "ym.facilitystatus.facilityinquiry.selectCarArriveBay2";
		YdStockDAO ydStockDAO	= new YdStockDAO();
		
		List listUnload = new ArrayList();	// 하차도착처리에 대한 리스트
		List listLoad = new ArrayList();	// 상차도착처리에 대한 리스트
		JDTORecord jtrCompRec = null;
		JDTORecord jtrLoadCompRec = null;
		
		String sStockMoveTerm = "";
		//String sFrtoMoveGp = "";
		String sUnloadBay = "";
		String sLoadBay = "";
		try{
			
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			listUnload.clear();
			listLoad.clear();
			listUnload = ydStockDAO.getListData(sQueryId_unload, new Object[]{cardNo});
			
			logger.println(LogLevel.DEBUG, this, "select unload data:"+listUnload);
			int nlistDataSize = listUnload.size();
			if(nlistDataSize>0){
				listLoad = ydStockDAO.getListData(sQueryId_load, new Object[]{cardNo});
				logger.println(LogLevel.DEBUG, this, "select load data:"+listLoad);
				for(int i=0; i<nlistDataSize;i++){
					jtrCompRec = (JDTORecord)listUnload.get(i);
					
					
					sStockMoveTerm = jtrCompRec.getFieldString("STOCK_MOVE_TERM");
					//sFrtoMoveGp = jtrCompRec.getFieldString("FRTOMOVE_EQUIP_GP");
					sUnloadBay = jtrCompRec.getFieldString("CARUNLOAD_BAY");
					
					
					
					if(sStockMoveTerm.equals(YmCommonConst.NEW_STOCK_MOVE_TERM_VL) ){	// 상차완료 -> 하차 도착처리
						logger.println(LogLevel.DEBUG, this, "저장품이동조건:VL(상차완료): 하차도착처리 " + sUnloadBay);
						if(sUnloadBay.equals(pos.substring(1,2))){
							return true;
						}else{
							return false;
						}
					}else{	// 상차 도착 처리.
						logger.println(LogLevel.DEBUG, this, "저장품이동조건:VL아님::: 상차도착처리. Bay:"+sLoadBay);
						jtrLoadCompRec = (JDTORecord)listLoad.get(i);
						sLoadBay = jtrLoadCompRec.getFieldString("BAYGP");
						if(sLoadBay.equals(pos.substring(1,2))){
							return true;
						}else{
							return false;
						}
					}
				}
			}else{
				logger.println(LogLevel.DEBUG, this, "해당 데이터가 존재하지 않습니다.");
				return false;
			}
		}catch(Exception e){
			logger.println(LogLevel.DEBUG, this, "차량 도착 처리중 에러 발생");
    		e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * 저장품에  팔레트 '적치열', '번지', '단', '카드번호' 셋팅
	 * @param pos
	 */
	private void setStockOfPallet(List pallet, String cardNo, int palletCnt) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
	    for(int i = 0; i < palletCnt; i++) {
	        ymCommonDAO.modifyMoveEquipOfStock(
	                getField((JDTORecord)pallet.get(i), "STACK_COL_GP"),
	                getField((JDTORecord)pallet.get(i), "STACK_BED_GP"),
	                getField((JDTORecord)pallet.get(i), "STACK_LAYER_GP"),
	                cardNo,
	                getField((JDTORecord)pallet.get(i), "STOCK_ID"));
	    }	    
	}
	
	/**
	 * 차량 및 팔레트 출하/이송/이적 도착을 처리한다.
	 *	- A/B열연 코일 출하/이송상차/이송하차
	 *	- B열연 슬라브 이송상차/이송하차/팔레트이적하차 도착처리
	 *  - 차량이적에 관련된 내용 추가    최규성 
	 *  - 일관제철에서 moveGp인자 추가됨. cgs 2009-09-29
	 * @param cardNo	카드번호
	 * @param pos		차량정지위치
	 * @param moveGp                    // cgs
	 */
	private void arrivalOfShippingOrTrans(String cardNo, 
								    String pos,
								    String moveGp) throws Exception {
		boolean isSendOrder = true;
		JDTORecord recInTemp			= null;
		YdCarSchDao	ydCarSchDao			= new YdCarSchDao();
		String szOperationName="arrivalOfShippingOrTrans";
    	try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
			
	        //팔레트번호 <=> 카드번호
	        cardNo = changpalletnocardno(cardNo);
	        
	        logger.println(LogLevel.DEBUG, this, "도착처리 : 카드번호=>"+cardNo);
	        
	        /** 
	         *	카드번호로 복수동 도착처리인지 확인
	         *	=> 차량 출발시 저장품에 차량MAP 정보를 셋팅하므로 이를 기준으로 복수동인지 체크.
	         * 	=> 복수동이면 현재MAP에 저장품 정보를 UPDATE
	         */
	    	List multys 	= ymCommonDAO.readMultyBay(cardNo);
	    	
	    	//육송인 경우에만 처리 함.
	    	if(!"T".equals(cardNo.substring(0, 1))
					&& !"P".equals(cardNo.substring(0, 1))
					&& !"A".equals(cardNo.substring(0, 1))
					&& !"B".equals(cardNo.substring(0, 1))
					&& !"C".equals(cardNo.substring(0, 1))
					&& !"K".equals(cardNo.substring(0, 1))
					&& !"S".equals(cardNo.substring(0, 1))
					){
		    	//=============================================================================================
		    	// 차량이적을 위해 코드 추가. 최규성 2009-10-19
		    	// 기존의 복수동 관련 쿼리가 변경되어 예전 쿼리로 조회하도록 추가함.
		    	if(Integer.parseInt(cardNo)>= 9995 && Integer.parseInt(cardNo) <= 9999){
		    		multys = ymCommonDAO.readMultyBay(cardNo,18);
		    	}
		    	//=============================================================================================
	    	}
	        int multysCnt 	= multys != null ? multys.size() : 0;
	        
//	        if(multysCnt > 0) {
//	        	setCurrMap(multys, multysCnt, pos);
//	        }
	        
	        logger.println(LogLevel.DEBUG, this, "도착처리 : 복수동 정보"+String.valueOf(multysCnt));
	        
	        /**
	         *	차량 상차 저장품 정보를 가져온다.
	         *	=> 카드번호를 기준으로 작업예약된 정보를 가져온다.
	         */
	        String yd 	= pos.substring(0, 1);
	        String bay 	= pos.substring(1, 2);
	        List stocks 	= ymCommonDAO.readStockOfCarLoad(cardNo, yd, bay);
	        int stocksCnt 	= stocks != null ? stocks.size() : 0;  
	        
	        
	        
	        JDTORecord stock3 = null;
			stock3 = (JDTORecord)stocks.get(0); 
            String szCAR_NO = StringHelper.evl(getField(stock3, "CAR_NO"),"");
	        
	        /**
	         *	현물의 위치와 차량 정지위치가 맞는지 확인한다.
	         */
	        if(stocksCnt > 0) {
	        	
	        	//TT CAR인경우 15개 차상위치를 활성화 
		        if(moveGp.equals("T")){
		        	stocksCnt=15 ;
		        }
		        
	        	considerHasStockOfPoint((JDTORecord)stocks.get(0), 
	        						  pos.substring(0, 2));            
	        }else if(multysCnt == 0) {
	            	throw new Exception("### 산적위치를 확인 하십시요.");                
	        }
	        
	        
	        
	        /**
	         *	설비 MAP '적재상태', 적치열 '차량 CARD 번호' UPDATE 
	         *	=> 차량이 도착 되었으므로 차량MAP을 활성화 하고 적치열에 카드번호를 MAPPING 한다.
	         */
	        editCardMapOpen(multysCnt, 
	        				stocksCnt, 
	        				cardNo, 
	        				pos, 
	        			    yd);
	        
			  //차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
	        EJBConnector ejbConn2 = new EJBConnector("default","JNDITsInfoReg",this);
			ejbConn2.trx("CarPointinforeg", new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class},
		  	             new Object[]{"C",szCAR_NO,cardNo,pos,"","","L"});
	        
	        /**
	         * 차량 도착 정보를 처리한다.
	         * 1. 작업예약을 오퍼레이터 지정으로 UPDATE
	         * 2. SCH CALL
	         */
	        JDTORecord stock = null;
	        
	        if(stocksCnt == 0) {
	        	logger.println(LogLevel.DEBUG, this, "하차작업 예약");
	        		
	        	if(multysCnt > 0) {
	        			
	        		 /*
	        		  * [기능 추가 : (2009.01.10 KBK)]
	        		  * 
  		        	  *  1. 차량을 이용한 동간 이적일 경우 목적동을 확인
  		        	  */   		
	        		 /* ======== Start ============*/
	        		if ((cardNo.equals(YmCommonConst.CAR_BAY_TRANS_CARD_NO_1))
	       	    			|| (cardNo.equals(YmCommonConst.CAR_BAY_TRANS_CARD_NO_2))
	       	    			|| (cardNo.equals(YmCommonConst.CAR_BAY_TRANS_CARD_NO_3))
	       	    			|| (cardNo.equals(YmCommonConst.CAR_BAY_TRANS_CARD_NO_4))
	       	    			|| (cardNo.equals(YmCommonConst.CAR_BAY_TRANS_CARD_NO_5))) {
	        			   
	        			logger.println(LogLevel.DEBUG, this, "====>Info : 차량 이적 도착 처리====");
	        			   
	        			
	        			EJBConnector ejbConn = new EJBConnector("default", "JNDICoilInfoReg", this);
	        			  
	        			List whereData = new ArrayList();


	        			// 차량카드번호 존재 여부를 검사한다.
	        			/*
	        			  SELECT A.CAR_CARD_NO, A.CARUNLOAD_BAY
	        	            FROM TB_YM_STOCK A, TB_YM_STACKLAYER B
	        			   WHERE A.CAR_CARD_NO = :CAR_CARD_NO
	        	             AND A.STOCK_ID = B.STOCK_ID(+)
	        	             AND (A.DEL_YN = 'N' OR A.DEL_YN IS NULL)
	        	        */
	        			whereData.add(cardNo);
	        			List cardNumList = (List)ejbConn.trx("getCarCardNo",new Class[]{String.class, List.class},
	        					                   new Object[]{"ym.steelinfo.steelinforecv.dao.YdStockDAO.getCarCardNo2" , whereData});

	        			
	        			logger.println(LogLevel.DEBUG, this, "차량이적 List: "+cardNumList);	
	        			
	        			JDTORecord jrecrd = (JDTORecord)cardNumList.get(0);
	        			
	        			logger.println(LogLevel.DEBUG, this, "차량이적 Record:"+jrecrd);	
	        			  
	        			if (!(bay.equals(jrecrd.getField("CARUNLOAD_BAY")))) {
	        				throw new Exception("[차량동간이적] ### 목적동이 아닙니다. 정지위치 ERROR");       
	        			}
	        	
	        			isSendOrder = false;
	        		}
	        		   /* ======== End ============*/
	        		// 차량이적용으로 인자 추가 . 최규성 2009-12-03
	        		// 기존 함수 주석처리. 
	        		/*
	        		unloadReservation(multys, 
	        							pos, 
	        							pos.substring(0, 1));
	        		*/
	        		unloadReservation(multys, 
										pos, 
										pos.substring(0, 1)
										,cardNo);
	        		
	        		logger.println(LogLevel.DEBUG, this, "차량 상차 저장품을 확인한다.인자:"+cardNo+yd+bay);
	        		if ((cardNo.equals(YmCommonConst.CAR_BAY_TRANS_CARD_NO_1))
	       	    			|| (cardNo.equals(YmCommonConst.CAR_BAY_TRANS_CARD_NO_2))
	       	    			|| (cardNo.equals(YmCommonConst.CAR_BAY_TRANS_CARD_NO_3))
	       	    			|| (cardNo.equals(YmCommonConst.CAR_BAY_TRANS_CARD_NO_4))
	       	    			|| (cardNo.equals(YmCommonConst.CAR_BAY_TRANS_CARD_NO_5))) {
	        			stocks 		= ymCommonDAO.readStockOfCarLoad3(cardNo, yd, bay);
	        			logger.println(LogLevel.DEBUG, this, "차량이적 데이터 확인 List:"+stocks);
	        			stocksCnt 	= stocks != null ? stocks.size() : 0;
	        			logger.println(LogLevel.DEBUG, this, "Select List Count"+String.valueOf(stocksCnt));
	        			stock 		= (JDTORecord)stocks.get(0);
	        			isSendOrder = false;
	        		}else{
	        			stocks 		= ymCommonDAO.readStockOfCarLoad(cardNo, yd, bay);
	        			logger.println(LogLevel.DEBUG, this, "데이터확인 List:"+stocks);
	        			stocksCnt 	= stocks != null ? stocks.size() : 0;
	        			logger.println(LogLevel.DEBUG, this, "Select List Count"+String.valueOf(stocksCnt));
	        			stock 		= (JDTORecord)stocks.get(0);
	        			isSendOrder = true;
	        		}
	        	}else {
	        		throw new Exception("정지위치를 확인 하십시요.");
	        	}
	        	
	        }else {
	        	stock = (JDTORecord)stocks.get(0);
	        }        
	        /**
	         * 출하/이송/이적 구분.
	         */
	        logger.println(LogLevel.DEBUG, this, "출하/이송/이적 구분");
	        
	        doArrival(stocks, 
	        		 stock, 
	        		 cardNo, 
	        		 pos);
	        // 조건 추가 CGS
	        // 차량이적 카드번호(9999~9995) 일 땐 출하관련 전송 처리를 하지 않음.
	        if ((!cardNo.equals(YmCommonConst.CAR_BAY_TRANS_CARD_NO_1))
   	    			&& (!cardNo.equals(YmCommonConst.CAR_BAY_TRANS_CARD_NO_2))
   	    			&& (!cardNo.equals(YmCommonConst.CAR_BAY_TRANS_CARD_NO_3))
   	    			&& (!cardNo.equals(YmCommonConst.CAR_BAY_TRANS_CARD_NO_4))
   	    			&& (!cardNo.equals(YmCommonConst.CAR_BAY_TRANS_CARD_NO_5))) 
	        {
    			   
	        //DMYDR036 ET차량도착처리인 경우 출하에 전송을 안함
			if(!moveGp.equals("Y")){
			if(yd.equals("1") || yd.equals("2")|| yd.equals("3")) { //AB열연 
			//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            //출하차량도착실적(YDDMR029)
			
//	    	YD_GP			야드구분
//	    	TRANS_ORD_DT	운송지시일자
//	    	TRANS_ORD_SEQNO	운송지시순번
//	    	CAR_NO			차량번호
//	    	CARD_NO			카드번호
//	    	ARR_WLOC_CD     착지개소코드
//	    	ARR_YD_PNT_CD   착지야드포인트코드
				
			List wloccd 		= ymCommonDAO.readStockOfwloc(pos);
			
			JDTORecord stock2 = null;
			stock2 = (JDTORecord)wloccd.get(0);
          
			Boolean isSuccess = new Boolean(false);
			
 			JDTORecord tcRecordDM = JDTORecordFactory.getInstance().create(); 
 			tcRecordDM.setField("YD_GP"				, yd); 
 			tcRecordDM.setField("TRANS_WORD_DATE"		, StringHelper.evl(getField(stock, "TRANS_WORD_DATE"),"")); 
 			tcRecordDM.setField("TRANS_WORD_SEQNO"	, StringHelper.evl(getField(stock, "TRANS_WORD_SEQNO"),"")); 
 			tcRecordDM.setField("CAR_NO"			, StringHelper.evl(getField(stock, "CAR_NO"),"")); 
 			tcRecordDM.setField("CARD_NO"			, cardNo); 
 			tcRecordDM.setField("ARR_WLOC_CD"		, StringHelper.evl(getField(stock2, "WLOC_CD"),"")); 
 			tcRecordDM.setField("ARR_YD_PNT_CD"		, StringHelper.evl(getField(stock2, "YD_PNT_CD"),"")); 
 			
 			//인터페이스 전문 호출
			EJBConnector ejbConn1 = new EJBConnector("default","JNDIYardWrkResReg",this);    
 			isSuccess = (Boolean)ejbConn1.trx("getYDDMR029",new Class[]{JDTORecord.class},
 			  	  	 new Object[]{tcRecordDM}); 
            logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 AB열연 출하차량도착실적.===");
            
            
            String szYD_EQP_WRK_STAT = StringHelper.evl(getField(stock2, "YD_EQP_WRK_STAT"),"");
            
          //차량스케줄 도착등록,
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_CAR_SCH_ID", 		StringHelper.evl(getField(stock2, "YD_CAR_SCH_ID"),""));
			recInTemp.setField("MODIFIER", 				"arrivalOf");
			
			
			if(szYD_EQP_WRK_STAT.equals("L") ){
				recInTemp.setField("YD_CAR_PROG_STAT", "B");									//하차도착상태		
				recInTemp.setField("YD_CARUD_WRK_BOOK_ID", 	StringHelper.evl(getField(stock2, "WBOOK_ID"),""));
				recInTemp.setField("YD_CARUD_STOP_LOC", 	pos);
				recInTemp.setField("YD_CARUD_ARR_DT", 		StringHelper.evl(getField(stock2, "CURDATE"),"")); 
				recInTemp.setField("YD_PNT_CD3", 			StringHelper.evl(getField(stock2, "YD_PNT_CD"),""));
			}else{
				recInTemp.setField("YD_CAR_PROG_STAT", "2");									//상차도착상태
				recInTemp.setField("YD_CARLD_WRK_BOOK_ID", 	StringHelper.evl(getField(stock2, "WBOOK_ID"),""));
				recInTemp.setField("YD_CARLD_STOP_LOC", 	pos);
				recInTemp.setField("YD_CARLD_ARR_DT", 		StringHelper.evl(getField(stock2, "CURDATE"),"")); 
				recInTemp.setField("YD_PNT_CD1", 			StringHelper.evl(getField(stock2, "YD_PNT_CD"),""));
			}
			
			
			int intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 0);
			if( intRtnVal <= 0 ){
			String szMsg="[" + szOperationName + "] 차량스케줄 도착등록 시 오류발생[반환값 : " + intRtnVal + "]";
			logger.println(LogLevel.DEBUG,this, szMsg);
			
			}
            //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			}
			}
	        }
	        /**
	         * 야드 L-2 송신(부두송신 안함)
	         *  [기능 추가 : (2009.01.10 KBK)]
	         *   ==> 차량동간 이적일때(송신안함)
	         *   isSendOrder == true;
	         */	
	        if (isSendOrder) { 
	        	logger.println(LogLevel.DEBUG, this, "차량동간 이적일 때 송신 안함. 조건상태:TRUE");	
	        	sendStartAndArrivalOrder(stocks, 
	        					   stock, 
	        					   cardNo, 
	        					   pos, 
	        					   YmCommonConst.CAR_GP_D);
	        }	
	        
    	}catch (Exception e) {
    		logger.println(LogLevel.DEBUG, this, "차량 도착 처리중 에러 발생");
    		e.printStackTrace();
    	}
	}
    
      /**
	 * 오퍼레이션명 : 
	 *
	 * B열연 슬라브 팔레트번호로 도착처리시 
	 *	=> 카드번호를 리턴한다.
	 *	=> 팔레트번호를 삭제한다.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public String changpalletnocardno(String cardNo){
		
		logger.println(LogLevel.DEBUG, this, "card_no가 Pallet_No인지 검색 시작"+cardNo);
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		/*
		  	SELECT CAR_CARD_NO 
			FROM TB_YM_STOCK
			WHERE SHEAR_SUPPLY_DEMAND_DDTT = ?
		*/
		String query 		= "ym.facilitystatus.facilityinquiry.StockDAO.changpalletnocardno";
		List cardnoList 	= ymCommonDAO.getCommonList(query,new Object[]{cardNo});
		int stocksCnt 		= cardnoList != null ? cardnoList.size() : 0;
		if(stocksCnt > 0){
			/*
				 UPDATE TB_YM_STOCK
				 SET SHEAR_SUPPLY_DEMAND_DDTT= ''
				 WHERE SHEAR_SUPPLY_DEMAND_DDTT = ?
			 */
			String query1 		= "ym.facilitystatus.facilityinquiry.StockDAO.updatePalletNO";
			int count 			= ymCommonDAO.updateData(query1, new Object[]{cardNo});
			JDTORecord jrecrd 	= (JDTORecord)cardnoList.get(0);
			cardNo 			= StringHelper.evl(jrecrd.getFieldString("CAR_CARD_NO"),cardNo);
		}
		logger.println(LogLevel.DEBUG, this, "반환 되는 CARD_NO :"+cardNo);
		return cardNo;
	}
	
    /**
     * 현물의 위치와 차량 정지위치가 맞는지 확인한다.
     * @param stocks
     */
    private void considerHasStockOfPoint(JDTORecord dto, String pos) throws Exception {     
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        if(! pos.equals(getField(dto, "CURR_STOCK_LOC"))) {
        	logger.println(LogLevel.DEBUG, this, "현물의 위치: 차량 정지위치=>"+pos+":"+getField(dto, "CURR_STOCK_LOC"));
            throw new Exception("상차 위치가 다릅니다. 차량정지위치를 확인하십시요.");
        }
    }

    /**
     * 차량CARD번호와 설비정보를 UPDATE
     * @param cardNo	차량CARD번호
     * @param pos		차량정지위치
     */
    private void editCardMapOpen(String cardNo, String pos) {
        ymCommonDAO.modifyActiveStatOfLayer(YmCommonConst.STACK_LAYER_ACTIVE_STAT_O, pos);
        ymCommonDAO.modifyCardNoOfStackCol(cardNo, pos);
        //차량예약 포인트 지우기
        ymCommonDAO.modifyCardNoOfStackCol2(cardNo);
    }

    /**
     * 차량CARD번호와 설비정보를 UPDATE
     * @param multyCnt	복수동 차량상차 저장품 정보
     * @param stocks	차량상차 저장품 정보
     * @param stocksCnt	차량상차 저장품 수 
     * @param cardNo	차량CARD번호
     * @param pos		차량정지위치
     */
    private void editCardMapOpen(int multyCnt, int stocksCnt, String cardNo, String pos, String yd) {   
    	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        if(YmCommonConst.YD_GP_4.equals(yd)) {
            editCardMapOpen(cardNo, pos);
        }else {
            /**
             * COIL STACKER와 SLAB STACKER가 상이
             * -COIL: 적치대의 BED가 1씩 증가, 적치단은 '01'로 고정
             * -SLAB: 적치대는 '01'로 고정, 적치단의 단 수가 1씩 증가
             */
            ymCommonDAO.modifyCardNoOfStackCol(cardNo, pos);
	        //차량예약 포인트 지우기
	        ymCommonDAO.modifyCardNoOfStackCol2(cardNo);
	        
            for(int i = 0; i < stocksCnt; i++) {
            	
                if(YmCommonConst.YD_GP_2.equals(yd)){
                    ymCommonDAO.modifyActiveStatOfLayer(YmCommonConst.STACK_LAYER_ACTIVE_STAT_O, pos, "01", "0"+ (i + 1));
                }else if(YmCommonConst.YD_GP_0.equals(yd)){
                	//2007-02-09 A열연 SLAB 야드 추가(MCH)
                	ymCommonDAO.modifyActiveStatOfLayer(YmCommonConst.STACK_LAYER_ACTIVE_STAT_O, pos, "01", "0"+ (i + 1));
                }else
                	/*
                	 * 2007.04.30
                	 * 차량 도착시 STOCK_ID is null 처리
                	 * 
                	 */
                	{
                    if(multyCnt > 0) {
                        ymCommonDAO.modifyActiveStatOfLayer_02(YmCommonConst.STACK_LAYER_ACTIVE_STAT_O, pos, "0"+ (multyCnt + (i + 1)));
                    }else {
                        ymCommonDAO.modifyActiveStatOfLayer_02(YmCommonConst.STACK_LAYER_ACTIVE_STAT_O, pos, "0"+ (i + 1));                        
                    }
                }
            }            
        }
    }

    /**
     * 차량 도착시 멀티동 정보를 처리한다.
     * @param multyBays	멀티동 정보
     * @param pos		차량 정지 위치
     */
    private void setCurrMap(List multyBays, int multysCnt, String pos) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        JDTORecord dto = null;
        for(int i = 0; i < multysCnt; i++) {            
            dto = (JDTORecord)multyBays.get(i);
            if("X".equals(getField(dto, "FRTOMOVE_EQUIP_GP").substring(1, 2))) {
                continue;
            }else if("TC".equals(getField(dto, "FRTOMOVE_EQUIP_GP").substring(2,4))) {
                continue;
            }
            logger.println(LogLevel.DEBUG, this, "차량 도착시 멀티동 정보를 처리 Record:"+dto);
            ymCommonDAO.modifyStockStatOfLayer( 
                    getField(dto, "STOCK_ID"),
                    YmCommonConst.STACK_LAYER_ACTIVE_STAT_O,
                    YmCommonConst.STACK_LAYER_STAT_L,
                    pos,
                    getField(dto, "FRTOMOVE_EQUIP_BED_GP"),
                    getField(dto, "FRTOMOVE_EQUIP_LAYER_GP"));
        }
    }

    /**
     * 차량 하차 작업 예약을 처리한다. 
     * A열연 SLAB야드는 필요 없을뜻
     * @param stocks	저장품이동조건
     * @param pos		차량정지위치
     * @param yd		야드구분
     * @throws Exception
     */
    private void unloadReservation(List stocks, String pos, String yd) throws Exception {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        int stocksCnt = stocks != null ? stocks.size() : 0;
        
        if(YmCommonConst.YD_GP_2.equals(yd) ||
           YmCommonConst.YD_GP_0.equals(yd)) {
            slabUnloadWork(stocks, pos, stocksCnt);
        }else {
            coilUnloadWork(stocks, pos, stocksCnt);
        }
    }
    
    
    /**
     * 슬라브 하차 작업 예약을 처리한다.
     * @param stocks	저장품정보
     * @param pos		차량정지위치
     * @param stocksCnt	저장품 개수
     */
    private void slabUnloadWork(List stocks, String pos, int stocksCnt) {
    	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        JDTORecord dto 		= null;
        String nextWBookId 	= null;
        for(int i = 0; i < stocksCnt; i++) { 
            dto 		= (JDTORecord)stocks.get(i);
            nextWBookId = ymCommonDAO.createWBook(
                    pos, 
                    getUnloadSchKind(getField(dto, "STOCK_ITEM"), pos),
                    YmCommonConst.SCH_WORK_LOC_DECISION_METHOD_S, 
                    "");
            ymCommonDAO.modifyTermAndWBookIdOfStock(
                    nextWBookId, 
                    getStockMoveTerm(getField(dto, "STOCK_ITEM"),getField(dto, "FRTOMOVE_EQUIP_GP").substring(0, 1)), 
                    getField(dto, "STOCK_ID"));
            ymCommonDAO.modifyLayerStatOfLayer(
                    YmCommonConst.STACK_LAYER_STAT_S, 
                    pos, 
                    getField(dto, "STOCK_ID"));
        }
    }

    /**
     * 코일 하차 작업 예약을 처리한다.
     * @param stocks	저장품정보
     * @param pos		차량정지위치
     * @param stocksCnt	저장품 개수
     */
    private void coilUnloadWork(List stocks, String pos, int stocksCnt) {
    	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        JDTORecord dto 		= null;
        String nextWBookId 	= null;
        logger.println(LogLevel.DEBUG,this, "coilUnloadWork() 코일 하차 작업 예약을 처리");
        for(int i = 0; i < stocksCnt; i++) {
            dto 		= (JDTORecord)stocks.get(i);
            nextWBookId = ymCommonDAO.createWBook(
                    pos, 
                    getUnloadSchKind(getField(dto, "STOCK_ITEM"), pos),		// 저장품상태에 따라서 스케줄코드를 변경한다.
                    YmCommonConst.SCH_WORK_LOC_DECISION_METHOD_S, 
                    "");
            ymCommonDAO.modifyTermAndWBookIdOfStock(
                    nextWBookId, 
                    getStockMoveTerm(getField(dto, "STOCK_ITEM"),getField(dto, "FRTOMOVE_EQUIP_GP").substring(0, 1)), 
                    getField(dto, "STOCK_ID"));
            ymCommonDAO.modifyLayerStatOfLayer(
                    YmCommonConst.STACK_LAYER_STAT_S, 
                    pos, 
                    getField(dto, "STOCK_ID"));
        }
    }
    
    /**
     *	YJK.	
     *	차량도착후 하차작업예약을 생성하는 시점에서
     *	저장품의 이동조건을 셋팅하는 부분.
     *	저장품 이동조건에 대한 코드를 수정한다. KG->CS 최규성
     * 저장품 이동조건을 리턴한다.
     * @param item	저장품품목
     * @return
     */
    private String getStockMoveTerm(String item, String yd) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        if(YmCommonConst.ITEM_CM.equals(item)) {
            return YmCommonConst.NEW_STOCK_MOVE_TERM_CS;
        }else if(YmCommonConst.ITEM_CG.equals(item)) {
        	// 차량이적을 위해서 CS로 코드변경 최규성 2009-11-11
            //return YmCommonConst.NEW_STOCK_MOVE_TERM_KG;
        	return YmCommonConst.NEW_STOCK_MOVE_TERM_CS;
        }else if(YmCommonConst.ITEM_SM.equals(item)) {
           if(YmCommonConst.YD_GP_0.equals(yd)) {
           	return YmCommonConst.NEW_STOCK_MOVE_TERM_VW;
           }else{
              return YmCommonConst.NEW_STOCK_MOVE_TERM_VM;
           } 
        }
        return "";
    }
    
    /**
     * 스케쥴코드를 리턴한다.
     * @param col	차량정지위치
     * @return
     */
    private String getUnloadSchKind(String item, String pos) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        if(YmCommonConst.ITEM_SM.equals(item)) {
            return YmCommonConst.NEW_SCH_WORK_KIND_SVMU;
        }else {
            if(YmCommonConst.YD_GP_1.equals(pos.substring(0, 1))) {
                if(YmCommonConst.ITEM_CG.equals(item)) {
                    return YmCommonConst.NEW_SCH_WORK_KIND_GVMU;
                }else {
                    return YmCommonConst.NEW_SCH_WORK_KIND_CVMU;
                }                
            }else if(YmCommonConst.YD_GP_3.equals(pos.substring(0, 1))) {

            	if("3".equals(pos.substring(5, 6)) ||
                   "4".equals(pos.substring(5, 6))) {

            		if(YmCommonConst.ITEM_CG.equals(item)) {

                        return YmCommonConst.NEW_SCH_WORK_KIND_GVM4;
                    }else {
                        return YmCommonConst.NEW_SCH_WORK_KIND_CVM4;
                    }
                }else {
                    if(YmCommonConst.ITEM_CG.equals(item)) {
                        return YmCommonConst.NEW_SCH_WORK_KIND_GVMU;
                    }else {
                        return YmCommonConst.NEW_SCH_WORK_KIND_CVMU;
                    }                    
                }
            }
        }
        return "";
    }
    
    /**
     * 차량 하차시 차량MAP정보, 차량카드번호를 CLEAR
     * @param stocks	차량도착정보
     */
    private void unloadClear(List stocks) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        JDTORecord dto = null;
        int stocksCnt = stocks != null ? stocks.size() : 0;
        for(int i = 0; i < stocksCnt; i++) {
            dto = (JDTORecord)stocks.get(i);
            ymCommonDAO.modifyUnloadInfoOfStock("", "", "", "", "", "", getField(dto, "STOCK_ID"));
        }            
    }

    /**
     * @param schKind
     * @param pos
     * @return
     */
    private String getSchWorkKind(String schKind, String pos) {
    	logger.println(LogLevel.DEBUG, this, "◆◆◆위치별 스케줄코드 변경 작업 전=>schKind:"+schKind+",pos:"+pos);
    	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        if(YmCommonConst.YD_GP_1.equals(pos.substring(0, 1))) {
        	 
        	if(YmCommonConst.NEW_SCH_WORK_KIND_CVML.equals(schKind)||
    			YmCommonConst.NEW_SCH_WORK_KIND_GVML.equals(schKind)||
    			YmCommonConst.NEW_SCH_WORK_KIND_GVM2.equals(schKind)||
    			YmCommonConst.NEW_SCH_WORK_KIND_GVM3.equals(schKind)) {
        		return schKind;
        	}
            if(YmCommonConst.BAY_GP_F.equals(pos.subSequence(1, 2))	||
               YmCommonConst.BAY_GP_G.equals(pos.subSequence(1, 2))	||
               YmCommonConst.BAY_GP_H.equals(pos.subSequence(1, 2))) {
            	
            	//H동 크레인 추가 에따른 출하 작업 스케줄 변경(gate 4,5)
            	if(YmCommonConst.BAY_GP_H.equals(pos.subSequence(1, 2))){
            		if("3".equals(pos.substring(5, 6))	||
        				"4".equals(pos.substring(5, 6))	||
        				"5".equals(pos.substring(5, 6))	||
        				"6".equals(pos.substring(5, 6))	||
        				"7".equals(pos.substring(5, 6))	||
        				"8".equals(pos.substring(5, 6))) {
//                        return YmCommonConst.NEW_SCH_WORK_KIND_GVF1;
            			return schKind.substring(0, 3)+"1";
                         }
            	}else{
            	
	                if("3".equals(pos.substring(5, 6))	||
	                   "4".equals(pos.substring(5, 6))) {
//	                    return YmCommonConst.NEW_SCH_WORK_KIND_GVF1;
	                	return schKind.substring(0, 3)+"1";
	                }
            	}
            }
        }else if(YmCommonConst.YD_GP_3.equals(pos.substring(0, 1))) {
        	if("3".equals(pos.substring(5, 6)) ||
               "4".equals(pos.substring(5, 6))
        	) {
                if(YmCommonConst.NEW_SCH_WORK_KIND_GVFL.equals(schKind)||
                		YmCommonConst.NEW_SCH_WORK_KIND_GTFL.equals(schKind)||
                		YmCommonConst.NEW_SCH_WORK_KIND_GPFL.equals(schKind)) {
//                	return YmCommonConst.NEW_SCH_WORK_KIND_GVF1;  
                	return schKind.substring(0, 3)+"1";
                }else if(YmCommonConst.NEW_SCH_WORK_KIND_CVML.equals(schKind)) {
                	return YmCommonConst.NEW_SCH_WORK_KIND_CVM2;
                }else if(YmCommonConst.NEW_SCH_WORK_KIND_GVML.equals(schKind)) {
                	return YmCommonConst.NEW_SCH_WORK_KIND_GVM2;
                }
            } else if (YmCommonConst.BAY_GP_D.equals(pos.subSequence(1, 2))||
            		   YmCommonConst.BAY_GP_E.equals(pos.subSequence(1, 2))
                  ) {
            	if("5".equals(pos.substring(5, 6)) ||
                        "6".equals(pos.substring(5, 6))) {
            		if(YmCommonConst.NEW_SCH_WORK_KIND_GVFL.equals(schKind)||
                    		YmCommonConst.NEW_SCH_WORK_KIND_GTFL.equals(schKind)||
                    		YmCommonConst.NEW_SCH_WORK_KIND_GPFL.equals(schKind)) {
            			// Coil 제품출하상차  => 1,2
//            			return YmCommonConst.NEW_SCH_WORK_KIND_GVF2; 
            			return schKind.substring(0, 3)+"2";
                    }else if(YmCommonConst.NEW_SCH_WORK_KIND_CVML.equals(schKind)) {
                    	// Coil 소재이송상차 => 2,3
                    	return YmCommonConst.NEW_SCH_WORK_KIND_CVM3;
                    }else if(YmCommonConst.NEW_SCH_WORK_KIND_GVML.equals(schKind)) {
                    	// Coil 제품이송상차 => 2,3
                    	return YmCommonConst.NEW_SCH_WORK_KIND_GVM3;
                    }
            	}
            }
        }
        
        return schKind;
    }

    private void callCRSchedule(String wbookId) {
        EJBConnector ejbConn = null;
        try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return ;
			}
			
            ejbConn = new EJBConnector("default", "JNDICraneSchReg", this);
            ejbConn.trx("callCraneSchInfo", new Class[]{ String.class }, new Object[]{ wbookId });
        }catch (Exception e) {
            logger.println(LogLevel.DEBUG, this, "스케쥴 편성 작업예약ID ERROR: "+ wbookId);
            e.printStackTrace();
        }
    }

    /**
     * 수신항목 '카드번호', '차량 정지 위치'를 체크
     * @param cardNo	카드번호
     * @param pos		차량 정지 위치
     * @return
     */
    private void validCarArrivalAndStart(String cardNo, String pos) throws Exception {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return ;
		}
		
        if(cardNo == null || cardNo.length() > 10) {
            throw new Exception("카드번호 ERROR: "+ cardNo);
        }else if(pos == null || pos.length() != 6) {
            throw new Exception("정지위치 ERROR: "+ pos);
        }
    }
    
    /**
     * 수신항목 '카드번호', '차량 정지 위치'를 체크
     * @param cardNo	카드번호
     * @param pos		차량 정지 위치
     * @return
     */
    private boolean validCarArrivalAndStart2(String cardNo, String pos){
    	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
        if(cardNo == null || cardNo.length() > 10) {
            logger.println(LogLevel.DEBUG, this, "카드번호 ERROR: "+ cardNo);
            return false;
        }else if(pos == null || pos.length() != 6) {
        	logger.println(LogLevel.DEBUG, this, "정지위치 ERROR: "+ pos);
            return false;
        }
        return true ;
    }

    /**
     * 차량 출발/도착지시 전문을 편성한다.
     * 1. A열연 차량진입/출발 정보		THHC190
     * 2. B열연 
     *    2.1 차량 도착/출발 정보 COIL  CN1BP06
     *    2.2 차량 도착/출발 정보 SLAB	 CM1BP06
     * @param stocks	저장품 정보 
     * @param cardNo	차량CARD번호
     * @param pos		차량 정지위치
     * @param gp		지시구분
     * @param carGp		도착출발구분
     */
    private void sendStartAndArrivalOrder(List stocks, JDTORecord stock, String cardNo, String pos, String carGp) {

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        String ydGp = pos.substring(0, 1);
        String bay_gp = pos.substring(1, 2);
        int stocksCnt = stocks != null ? stocks.size() : 0;
        if(YmCommonConst.YD_GP_4.equals(ydGp)) {
            editTakeOutTimeOfSlabComm(stocks, ydGp, carGp, stocksCnt);
            return;
        }
        
        String tcCd 	= YmCommonConst.TC_THHC190;
        String schKind 	= getField(stock, "SCH_WORK_KIND");
        StringBuffer sendMsg 	= new StringBuffer();  
        JDTORecord cardInfo 	= ymCommonDAO.readCarNo(ydGp, cardNo);
        if(YmCommonConst.YD_GP_1.equals(ydGp)) {//A열연 COIL
            /**
             * THHC190
             * 1	전문코드			CHAR	07		
             * 2	작업동			CHAR	01		
             * 3	진입위치 SEQ NO	CHAR	01		
             * 4	차량구분			CHAR	01		1:반입, 2:출하
             * 5	운송회사 코드		CHAR	05		
             * 6	차량번호			CHAR	05		
             * 7	작업대상 수량		CHAR	02		
             * 8	CARD 번호		CHAR	04		
             * 9	코일번호			CHAR	10		8회 반복
             * 10	권상, 권하 위치	CHAR	08		
             * 11	SPARE			CHAR	30		
             */            
            Map tc = ymCommonDAO.readColumnLenOfTc(tcCd);
            sendMsg.append(tcCd);									//전문코드
            sendMsg.append(pos.substring(1, 2));					//작업동
            appendMsg(sendMsg, "", 									getFieldLen(tc, "진입위치SEQNO"));
            if(YmCommonConst.NEW_SCH_WORK_KIND_CVRU.equals(schKind)) {
                carGp = "1";
            }else {
                carGp = "2";
            }
            appendMsg(sendMsg, carGp, 								getFieldLen(tc, "차량구분"));
            appendMsg(sendMsg, getField(cardInfo, "TRANS_COM_CD"), 	getFieldLen(tc, "운송회사코드"));
            appendMsg(sendMsg, getField(cardInfo, "CAR_NO"), 		getFieldLen(tc, "차량번호"));            
            appendMsgNum(sendMsg, ""+ stocksCnt,					getFieldLen(tc, "작업대상수량"));
            appendMsg(sendMsg, cardNo, 								getFieldLen(tc, "CARD번호"));
            sendAMsg(sendMsg, stocks, tc);							//코일번호
            appendMsg(sendMsg, "",									getFieldLen(tc, "SPARE"));
            sendQueue(tcCd, sendMsg.toString());
        }else {	//B열연 COIL/SLAB
            /**
             * CM1BP06
             * 1	전문코드	TC					CHAR	07		
             * 2	발생일자	Date				CHAR	10		YYYY-MM-DD
             * 3	발생시간	Time				CHAR	08		HH-MM-SS
             * 4	전문구분	Form				CHAR	01		I  : Initialize, U : Update, D : Delete,   R : Re-request
             * 5	전문길이	Message_Length		CHAR	04		
             * 6	차량 번호	CarNo				CHAR	12		
             * 7	차량 TYPE	CarType			CHAR	01		조업기준
             * 8	출발/도착 구분	ArriveId		CHAR	01		‘D’:도착 ‘S’:출발
             * 9	차량 진입 위치	CarInPosition	CHAR	02		YARD구분(1)+동구분(1)+SPAN(2)+열 NO(2)	
             * 10	지시구분	OrderId				CHAR	01		1:장입지시, 2:이송지시, 3:출하지시	
             * 11	지시번호	OrderNo				CHAR	10		
             * 12	CARD 번호	CardNo			CHAR	06		
             * 13	작업 매수	WorkCount			CHAR	02		Slab 10매 정보를 보낼 때작업매수는 10매, 송신 Seq는 현재 보내는 건의 순서를 말함
             * 14	송신 Seq	RecevieSeq			CHAR	01		
             * 15	작업 SLAB No	WorkSlabNo		CHAR	11		
             */
            String ymd = YmCommonUtil.getStringYMD("-");
            String hms = YmCommonUtil.getStringHMS("-");
            if(YmCommonConst.YD_GP_3.equals(ydGp)) {
                tcCd = YmCommonConst.TC_CN1BP06;
            }else {
                tcCd = YmCommonConst.TC_CM1BP06;
            }
            Map tc = ymCommonDAO.readColumnLenOfTc(tcCd);
            sendMsg.append(tcCd);
            sendMsg.append(ymd);
            sendMsg.append(hms);
            sendMsg.append(YmCommonConst.FORM_I);
            appendMsgNum(sendMsg, ""+ (YmCommonUtil.getTotalLenOfTc(tc) - 30), getFieldLen(tc, "전문길이"));
            appendMsg(sendMsg, getField(cardInfo, "CAR_NO"), getFieldLen(tc, "차량번호"));
            if("TR".equals(pos.substring(2, 4))) {
                appendMsg(sendMsg, "T",		getFieldLen(tc, "차량TYPE"));                
            }else if("PT".equals(pos.substring(2, 4))) {
                //appendMsg(sendMsg, "P",		getFieldLen(tc, "차량TYPE"));
                appendMsg(sendMsg, "T",		getFieldLen(tc, "차량TYPE"));
            }else {
                appendMsg(sendMsg, "",		getFieldLen(tc, "차량TYPE"));
            }
            appendMsg(sendMsg, carGp, 								getFieldLen(tc, "출발도착구분"));
            appendMsg(sendMsg, pos, 								getFieldLen(tc, "차량진입위치"));
            appendMsg(sendMsg, getOrderGp(schKind), 				getFieldLen(tc, "지시구분"));            
            appendMsg(sendMsg, getField(stock, "TRANS_WORD_DATE_NO"), 	getFieldLen(tc, "지시번호"));
            appendMsg(sendMsg, cardNo, 								getFieldLen(tc, "CARD번호"));
            appendMsgNum(sendMsg, ""+ stocksCnt, 					getFieldLen(tc, "작업매수"));
            if(YmCommonConst.YD_GP_3.equals(ydGp)) {
                sendBCMsg(tcCd, sendMsg, stocks, tc, "작업COILNo", 8);
            }else {
                sendBMsg(tcCd, sendMsg, stocks, tc, "작업SLABNo", 10);
            }
        }
    }
    
    
    /**
     * 구내운송 L2차량 출발/도착지시 전문을 편성한다.
     * 1. A열연 차량진입/출발 정보		THHC190
     * 2. B열연 
     *    2.1 차량 도착/출발 정보 COIL  CN1BP06
     *    2.2 차량 도착/출발 정보 SLAB	 CM1BP06
     * @param stocks	저장품 정보 
     * @param cardNo	차량CARD번호
     * @param pos		차량 정지위치
     * @param gp		지시구분
     * @param carGp		도착출발구분
     */
    public void ArrivalOrder(List stocks, JDTORecord stock, String cardNo, String pos, String carGp) {   
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        String ydGp = pos.substring(0, 1);
        String bay_gp = pos.substring(1, 2);
        int stocksCnt = stocks != null ? stocks.size() : 0;
        if(YmCommonConst.YD_GP_4.equals(ydGp)) {
            editTakeOutTimeOfSlabComm(stocks, ydGp, carGp, stocksCnt);
            return;
        }
        
        ymCommonDAO dao = ymCommonDAO.getInstance();
        
        String tcCd 	= YmCommonConst.TC_THHC190;
        String schKind 	= getField(stock, "SCH_WORK_KIND");
        JDTORecord cardInfo 	= dao.readCarNo3(cardNo);
        StringBuffer sendMsg 	= new StringBuffer();  
        if(YmCommonConst.YD_GP_1.equals(ydGp)) {//A열연 COIL
            /**
             * THHC190
             * 1	전문코드			CHAR	07		
             * 2	작업동			CHAR	01		
             * 3	진입위치 SEQ NO	CHAR	01		
             * 4	차량구분			CHAR	01		1:반입, 2:출하
             * 5	운송회사 코드		CHAR	05		
             * 6	차량번호			CHAR	05		
             * 7	작업대상 수량		CHAR	02		
             * 8	CARD 번호		CHAR	04		
             * 9	코일번호			CHAR	10		8회 반복
             * 10	권상, 권하 위치	CHAR	08		
             * 11	SPARE			CHAR	30		
             */            
            Map tc = dao.readColumnLenOfTc(tcCd);
            sendMsg.append(tcCd);									//전문코드
            sendMsg.append(pos.substring(1, 2));					//작업동
            appendMsg(sendMsg, "", 									getFieldLen(tc, "진입위치SEQNO"));
            if(YmCommonConst.NEW_SCH_WORK_KIND_CVRU.equals(schKind)) {
                carGp = "1";
            }else {
                carGp = "2";
            }
            appendMsg(sendMsg, carGp, 								getFieldLen(tc, "차량구분"));
            appendMsg(sendMsg, getField(cardInfo, "TRANS_COM_CD"), 	getFieldLen(tc, "운송회사코드"));
            appendMsg(sendMsg, getField(cardInfo, "CAR_NO"), 		getFieldLen(tc, "차량번호"));           
            appendMsgNum(sendMsg, ""+ stocksCnt,					getFieldLen(tc, "작업대상수량"));
            appendMsg(sendMsg, cardNo.substring(4,8)+"  ", 				getFieldLen(tc, "CARD번호"));
            sendAMsg(sendMsg, stocks, tc);							//코일번호
            appendMsg(sendMsg, "",									getFieldLen(tc, "SPARE"));
            sendQueue(tcCd, sendMsg.toString());
        }else {	//B열연 COIL/SLAB
            /**
             * CM1BP06
             * 1	전문코드	TC					CHAR	07		
             * 2	발생일자	Date				CHAR	10		YYYY-MM-DD
             * 3	발생시간	Time				CHAR	08		HH-MM-SS
             * 4	전문구분	Form				CHAR	01		I  : Initialize, U : Update, D : Delete,   R : Re-request
             * 5	전문길이	Message_Length		CHAR	04		
             * 6	차량 번호	CarNo				CHAR	12		
             * 7	차량 TYPE	CarType			CHAR	01		조업기준
             * 8	출발/도착 구분	ArriveId		CHAR	01		‘D’:도착 ‘S’:출발
             * 9	차량 진입 위치	CarInPosition	CHAR	02		YARD구분(1)+동구분(1)+SPAN(2)+열 NO(2)	
             * 10	지시구분	OrderId				CHAR	01		1: 소재이송지시, 2: 제품이송지시, 3: 출하지시, 4:장입지시	
             * 11	지시번호	OrderNo				CHAR	10		
             * 12	CARD 번호	CardNo			CHAR	06		
             * 13	작업 매수	WorkCount			CHAR	02		Slab 10매 정보를 보낼 때작업매수는 10매, 송신 Seq는 현재 보내는 건의 순서를 말함
             * 14	송신 Seq	RecevieSeq			CHAR	01		
             * 15	작업 SLAB No	WorkSlabNo		CHAR	11		
             */
        	if(YmCommonConst.YD_GP_3.equals(ydGp)||
        			YmCommonConst.YD_GP_2.equals(ydGp)	) {        		
        
            String ymd = YmCommonUtil.getStringYMD("-");
            String hms = YmCommonUtil.getStringHMS("-");
            if(YmCommonConst.YD_GP_3.equals(ydGp)) {
                tcCd = YmCommonConst.TC_CN1BP06;
            }else {
                tcCd = YmCommonConst.TC_CM1BP06;
            }
            Map tc = dao.readColumnLenOfTc(tcCd);
            sendMsg.append(tcCd);
            sendMsg.append(ymd);
            sendMsg.append(hms); 
            sendMsg.append(YmCommonConst.FORM_I);
            appendMsgNum(sendMsg, ""+ (YmCommonUtil.getTotalLenOfTc(tc) - 30), getFieldLen(tc, "전문길이"));
            appendMsg(sendMsg, getField(cardInfo, "CAR_NO"), getFieldLen(tc, "차량번호"));
            if("TR".equals(pos.substring(2, 4))) {
                appendMsg(sendMsg, "P",		getFieldLen(tc, "차량TYPE"));                
            }else if("PT".equals(pos.substring(2, 4))) {
                //appendMsg(sendMsg, "P",		getFieldLen(tc, "차량TYPE"));
                appendMsg(sendMsg, "P",		getFieldLen(tc, "차량TYPE"));
            }else {
                appendMsg(sendMsg, "",		getFieldLen(tc, "차량TYPE"));
            }
            
            appendMsg(sendMsg, carGp, 								getFieldLen(tc, "출발도착구분"));
            appendMsg(sendMsg, pos, 								getFieldLen(tc, "차량진입위치"));
            appendMsg(sendMsg, getOrderGp(schKind), 				getFieldLen(tc, "지시구분"));            
            appendMsg(sendMsg, getField(stock, "TRANS_WORD_DATE_NO"), 	getFieldLen(tc, "지시번호"));
            appendMsg(sendMsg, cardNo.substring(4,8)+"  ", 				getFieldLen(tc, "CARD번호"));
            appendMsgNum(sendMsg, ""+ stocksCnt, 					getFieldLen(tc, "작업매수"));
            if(YmCommonConst.YD_GP_3.equals(ydGp)) {
                sendBCMsg(tcCd, sendMsg, stocks, tc, "작업COILNo", 8);
            }else {
                sendBMsg(tcCd, sendMsg, stocks, tc, "작업SLABNo", 10);
            }
        	}
        }
    }
    
    /**
     * TB_PM_SLABCOMM '부두 YARD 반출 일자', '부두 YARD 반출 시각' UPDATE
     * @param stocks	차량상차 저장품정보
     * @param ydGp		야드구분
     * @param carGp		차량도착/출발구분
     * @param stocksCnt	차량상차 저장품정보 개수
     */
    private void editTakeOutTimeOfSlabComm(List stocks, String ydGp, String carGp, int stocksCnt) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        if(YmCommonConst.CAR_GP_D.equals(carGp)) {
            for(int i = 0; i < stocksCnt; i++) {
                ymCommonDAO.modifyLieTakeOutTimeOfSlabComm(
                        YmCommonUtil.getStringYMD(), 
                        YmCommonUtil.getStringHMS(), 
                        getField((JDTORecord)stocks.get(i), "STOCK_ID"));
           }
        }
    }

    /**
     * 지시구분을 리턴한다.
     * @param schKind
     * @param yd
     * @return 
     */
    private String getOrderGp(String schKind) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
    	if(YmCommonConst.NEW_SCH_WORK_KIND_GVML.equals(schKind)|| // COIL 제품이송상차
			     YmCommonConst.NEW_SCH_WORK_KIND_GVM2.equals(schKind)|| // COIL 제품이송상차
	   		     YmCommonConst.NEW_SCH_WORK_KIND_GVM3.equals(schKind)|| // COIL 제품이송상차		
                 YmCommonConst.NEW_SCH_WORK_KIND_GVMU.equals(schKind)|| // COIL 제품이송하차
			     YmCommonConst.NEW_SCH_WORK_KIND_GVM4.equals(schKind)|| // COIL 제품이송하차	 	
  			     YmCommonConst.NEW_SCH_WORK_KIND_GVM5.equals(schKind)){ // COIL 제품이송하차
    		return YmCommonConst.COIL_ORDER_GP_2;
       }else if(YmCommonConst.NEW_SCH_WORK_KIND_CVML.equals(schKind)|| // COIL 소재이송상차
			     YmCommonConst.NEW_SCH_WORK_KIND_CVM2.equals(schKind)|| // COIL 소재이송상차
	   		     YmCommonConst.NEW_SCH_WORK_KIND_CVM3.equals(schKind)|| // COIL 소재이송상차
			     YmCommonConst.NEW_SCH_WORK_KIND_CVMU.equals(schKind)|| // COIL 소재이송하차
			     YmCommonConst.NEW_SCH_WORK_KIND_CVM4.equals(schKind)|| // COIL 소재이송하차	 
 		   	     YmCommonConst.NEW_SCH_WORK_KIND_CVM5.equals(schKind)){ // COIL 소재이송하차	 
           return YmCommonConst.COIL_ORDER_GP_1;
       }else if(YmCommonConst.NEW_SCH_WORK_KIND_GVFL.equals(schKind)|| // COIL 제품출하상차
    		    YmCommonConst.NEW_SCH_WORK_KIND_GVF1.equals(schKind)|| // Coil 제품출하상차
    		    YmCommonConst.NEW_SCH_WORK_KIND_GVF2.equals(schKind)||
    		    
    		    YmCommonConst.NEW_SCH_WORK_KIND_GTFL.equals(schKind)|| // COIL 제품출하상차
    		    YmCommonConst.NEW_SCH_WORK_KIND_GTF1.equals(schKind)|| // Coil 제품출하상차
    		    YmCommonConst.NEW_SCH_WORK_KIND_GTF2.equals(schKind)||
    		    
    		    YmCommonConst.NEW_SCH_WORK_KIND_GPFL.equals(schKind)|| // COIL 제품출하상차
    		    YmCommonConst.NEW_SCH_WORK_KIND_GPF1.equals(schKind)|| // Coil 제품출하상차
    		    YmCommonConst.NEW_SCH_WORK_KIND_GPF2.equals(schKind)
    		    
       			){ // Coil 제품출하상차	
            return YmCommonConst.COIL_ORDER_GP_3;
        }else if(YmCommonConst.NEW_SCH_WORK_KIND_SVFL.equals(schKind)) {
            return YmCommonConst.SLAB_ORDER_GP_4;
        }else if(YmCommonConst.NEW_SCH_WORK_KIND_SVML.equals(schKind)) {
            return YmCommonConst.SLAB_ORDER_GP_4;    
        }else if(YmCommonConst.NEW_SCH_WORK_KIND_SVMU.equals(schKind)) {
            return YmCommonConst.SLAB_ORDER_GP_2;    
        } 
        return "3";
    }

    /**
     * @param sendMsg
     * @param stocks
     * @param tc
     */
    private void sendBCMsg(String tcCd, StringBuffer sendMsg, List stocks, Map tc, String field, int loofCnt) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        JDTORecord dto = null;
        int stocksCnt = stocks != null ? stocks.size() : 0;
        if(stocksCnt > 0) {
            for(int i = 0; i < stocksCnt; i++) {
                dto = (JDTORecord)stocks.get(i);
//                appendMsgNum(sendMsg, getField(dto, "STACK_BED_GP"), 	getFieldLen(tc, "송신Seq"));
                appendMsgNum(sendMsg, ""+ (i + 1), 	getFieldLen(tc, "송신Seq"));
                appendMsg(sendMsg, getField(dto, "STOCK_ID"), 			getFieldLen(tc, field));
                sendQueue(tcCd, sendMsg.toString());
                deleteSendMsg(sendMsg, loofCnt);
            }
        }else {
            for(int i = 0; i < loofCnt; i++) {
                appendMsgNum(sendMsg, ""+ (i + 1), 	getFieldLen(tc, "송신Seq"));
                appendMsg(sendMsg, "", 				getFieldLen(tc, field));
                sendQueue(tcCd, sendMsg.toString());
                deleteSendMsg(sendMsg, loofCnt);
            }
        }        
    }

    /**
     * @param sendMsg
     * @param stocks
     * @param tc
     */
    private void sendBMsg(String tcCd, StringBuffer sendMsg, List stocks, Map tc, String field, int loofCnt) {
    	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        JDTORecord dto = null;
        int stocksCnt = stocks != null ? stocks.size() : 0;
        if(stocksCnt > 0) {
            int i = 0;            
            for(i = 0; i < stocksCnt; i++) {
                dto = (JDTORecord)stocks.get(i);
                appendMsgNum(sendMsg, getField(dto, "STACK_LAYER_GP"), 	getFieldLen(tc, "송신Seq"));
                appendMsg(sendMsg, getField(dto, "STOCK_ID"), 			getFieldLen(tc, field));
                sendQueue(tcCd, sendMsg.toString());
                deleteSendMsg(sendMsg, loofCnt);
            }
        }else {
            for(int i = 0; i < loofCnt; i++) {
                appendMsgNum(sendMsg, ""+ (i + 1), 	getFieldLen(tc, "송신Seq"));
                appendMsg(sendMsg, "", 				getFieldLen(tc, field));
                sendQueue(tcCd, sendMsg.toString());
                deleteSendMsg(sendMsg, loofCnt);
            }
        }        
    }

    /**
     * @param loofCnt
     */
    private void deleteSendMsg(StringBuffer sendMsg, int loofCnt) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        if(loofCnt == 8) {
            sendMsg.delete(sendMsg.length() - 12, sendMsg.length());
        }else {
            sendMsg.delete(sendMsg.length() - 13, sendMsg.length());
        }
    }

    /**
     * @param sendMsg
     * @param stocks
     * @param tc
     */
    private void sendAMsg(StringBuffer sendMsg, List stocks, Map tc) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        JDTORecord dto 	= null;
        int loofCnt		= 8;
        int stocksCnt 	= stocks != null ? stocks.size() : 0;
        if(stocksCnt > 0) {
            int i = 0;            
            for(i = 0; i < stocksCnt; i++) {
                dto = (JDTORecord)stocks.get(i);
                appendMsg(sendMsg, getField(dto, "STOCK_ID"), 	getFieldLen(tc, "코일번호1"));
                appendMsg(sendMsg, YmCommonUtil.setLegacyPositionWithCur(getField(dto, "UP_LOC")),
                        getFieldLen(tc, "권상권하위치1"));
            }
            for(int j = i; j < loofCnt; j++) {
                appendMsg(sendMsg, "", 	getFieldLen(tc, "코일번호1"));
                appendMsg(sendMsg, "", 	getFieldLen(tc, "권상권하위치1"));                    
            }
        }else {
            for(int i = 0; i < loofCnt; i++) {
                appendMsg(sendMsg, "", 	getFieldLen(tc, "코일번호1"));
                appendMsg(sendMsg, "", 	getFieldLen(tc, "권상권하위치1"));
            }
        }
    }

    /**
     * 차량 출발/도착지시 전문을 편성한다.
     * 1. A열연 차량진입/출발 정보		THHC190
     * 2. B열연 
     *    2.1 차량 도착/출발 정보 COIL  CN1BP06
     *    2.2 차량 도착/출발 정보 SLAB	CM1BP06
     * @param cardNo	차량CARD번호
     * @param pos		차량 정지위치
     * @param carGp		도착출발구분
     */
    private void sendStartAndArrivalOrder(String cardNo, String pos, String gp, String carGp) {
    	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        String ydGp = pos.substring(0, 1);
        if(YmCommonConst.YD_GP_4.equals(ydGp)) {
            return;
        }

        String tcCd = YmCommonConst.TC_THHC190;
        StringBuffer sendMsg = new StringBuffer();
        JDTORecord dto = ymCommonDAO.readCarNo(ydGp, cardNo);
        if(YmCommonConst.YD_GP_1.equals(ydGp)) {		//A열연 COIL
            /**
             * THHC190
             * 1	전문코드			CHAR	07		
             * 2	작업동			CHAR	01		
             * 3	진입위치 SEQ NO	CHAR	01		
             * 4	차량구분			CHAR	01		1:반입, 2:출하
             * 5	운송회사 코드		CHAR	05		
             * 6	차량번호			CHAR	05		
             * 7	작업대상 수량		CHAR	02		
             * 8	CARD 번호		CHAR	04		
             * 9	코일번호			CHAR	10		8회 반복
             * 10	권상, 권하 위치	CHAR	08		
             * 11	SPARE			CHAR	30		
             */
            Map tc = ymCommonDAO.readColumnLenOfTc(tcCd);
            sendMsg.append(tcCd);
            sendMsg.append(pos.substring(1, 2));
            appendMsg(sendMsg, "", getFieldLen(tc, "진입위치SEQNO"));
            sendMsg.append(YmCommonConst.CAR_GP_2);
            appendMsg(sendMsg, "", getFieldLen(tc, "운송회사코드"));
            appendMsg(sendMsg, getField(dto, "CAR_NO"), getFieldLen(tc, "차량번호"));
            appendMsgNum(sendMsg, "", getFieldLen(tc, "작업대상수량"));
            appendMsg(sendMsg, cardNo, getFieldLen(tc, "CARD번호"));
            
            for(int i = 0; i < 8; i++) {
                appendMsg(sendMsg, "", getFieldLen(tc, "코일번호1"));
                appendMsg(sendMsg, "", getFieldLen(tc, "권상권하위치1"));
            }
            appendMsg(sendMsg, "", getFieldLen(tc, "SPARE"));
            
            sendQueue(tcCd, sendMsg.toString());
        }else {	//B열연 COIL/SLAB
            /**
             * CM1BP06
             * 1	전문코드	TC					CHAR	07		
             * 2	발생일자	Date				CHAR	10	YYYY-MM-DD
             * 3	발생시간	Time				CHAR	08	HH-MM-SS
             * 4	전문구분	Form				CHAR	01	I: Initialize, U: Update, D: Delete, R: Re-request
             * 5	전문길이	Message_Length		CHAR	04		
             * 6	차량 번호	CarNo				CHAR	12		
             * 7	차량 TYPE	CarType				CHAR	01	조업기준
             * 8	출발/도착 구분	ArriveId		CHAR	01	‘D’:도착 ‘S’:출발
             * 9	차량 진입 위치	CarInPosition	CHAR	02	YARD구분(1)+동구분(1)+SPAN(2)+열 NO(2)	
             * 10	지시구분	OrderId				CHAR	01	1:장입지시, 2:이송지시, 3:출하지시	
             * 11	지시번호	OrderNo				CHAR	10		
             * 12	CARD 번호	CardNo			CHAR	06		
             * 13	작업 매수	WorkCount			CHAR	02	Slab 10매 정보를 보낼 때작업매수는 10매, 송신 Seq는 현재 보내는 건의 순서를 말함
             * 14	송신 Seq	RecevieSeq			CHAR	01		
             * 15	작업 SLAB No	WorkSlabNo		CHAR	11		
             */
            if(YmCommonConst.YD_GP_3.equals(ydGp)) {
                tcCd = YmCommonConst.TC_CN1BP06;
            }else {
                tcCd = YmCommonConst.TC_CM1BP06;
            }
            
            Map tc = ymCommonDAO.readColumnLenOfTc(tcCd);
            sendMsg.append(tcCd);							//전문코드
            sendMsg.append(YmCommonUtil.getStringYMD("-"));	//발생일자
            sendMsg.append(YmCommonUtil.getStringHMS("-"));	//발생시간
            sendMsg.append(YmCommonConst.FORM_I);			//전문구분
            appendMsgNum(sendMsg, ""+ (YmCommonUtil.getTotalLenOfTc(tc) - 30), 
                    									getFieldLen(tc, "전문길이"));	
            appendMsg(sendMsg, getField(dto, "CAR_NO"), getFieldLen(tc, "차량번호"));
            if("TR".equals(pos.substring(2, 4))) {
                appendMsg(sendMsg, "T",		getFieldLen(tc, "차량TYPE"));                
            }else if("PT".equals(pos.substring(2, 4))) {
                //appendMsg(sendMsg, "P",		getFieldLen(tc, "차량TYPE"));
                appendMsg(sendMsg, "T",		getFieldLen(tc, "차량TYPE"));
            }else {
                appendMsg(sendMsg, "",		getFieldLen(tc, "차량TYPE"));
            }
            sendMsg.append(carGp);							//출발도착구분
            appendMsg(sendMsg, pos, 					getFieldLen(tc, "차량진입위치"));
            appendMsg(sendMsg, getOrderGp(""), 			getFieldLen(tc, "지시구분"));
            appendMsg(sendMsg, "", 						getFieldLen(tc, "지시번호"));
            appendMsg(sendMsg, cardNo, getFieldLen(tc, "CARD번호"));
            appendMsgNum(sendMsg, "", getFieldLen(tc, "작업매수"));
            appendMsgNum(sendMsg, "", getFieldLen(tc, "송신Seq"));
            if(YmCommonConst.YD_GP_3.equals(ydGp)) {
                appendMsg(sendMsg, "", getFieldLen(tc, "작업COILNo"));
            }else {
                appendMsg(sendMsg, "", getFieldLen(tc, "작업SLABNo"));
            }
            sendQueue(tcCd, sendMsg.toString());
            sendMsg.setLength(0);
        }
    }

    /**
     * 출하로 차량 도착정보를 송신한다.
     * @param carNo
     * @param cardNo
     * @param record
     * @throws Exception
     * @throws RemoteException
     */
    private void sendDM(JDTORecord dto, String cardNo) throws Exception {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        /**
         * 차량도착정보(공차) 출하 송신
         * TC		: YMDM002
         * 인터페이스	: YM-LIF-009	 
         * 1	전문코드	TC					CHAR	07		
         * 2	발생일자	Date				CHAR	10		YYYY-MM-DD
         * 3	발생시간	Time				CHAR	08		HH-MM-SS
         * 		소재구분   MATERIAL_GOODS		CHAR	1		제품출하지시1, 제품이송지시2, 소재이송지시3, slab이송 4
         * 4	카드 번호	CARD_NO				CHAR	4		
         * 5	차량 번호	CAR_NO				CHAR	15		
         * 6	도착 일자	BAYIN_DATE			CHAR	8		
         * 7	도착 시각	BAYIN_TIME			CHAR	6		
         * 8	운송지시일자	TRANS_WORD_DATE	CHAR	8		
         * 9	운송지시순번	TRANS_WORD_SEQNO NUM	4		
         */
        String date = YmCommonUtil.getStringYMD("-");
        String time = YmCommonUtil.getStringHMS("-");
        YMDM002 ymdm002 = new YMDM002();
        ymdm002.setTcCode(YmCommonConst.MODEL_YMDM002);
        ymdm002.setTcDate(date);
        ymdm002.setTcTime(time);
        ymdm002.setMATERIAL_GOODS(getMaterialGoods(getField(dto, "SCH_WORK_KIND")));
        ymdm002.setCARD_NO(cardNo);
        ymdm002.setCAR_NO(getField(ymCommonDAO.readCarNo(dto.getFieldString("YD_GP"), cardNo), "CAR_NO"));
        ymdm002.setBAYIN_DATE(date.replaceAll("-", ""));
        ymdm002.setBAYIN_TIME(time.replaceAll("-", ""));
        ymdm002.setTRANS_WORD_DATE(getField(dto, "TRANS_WORD_DATE"));
        ymdm002.setTRANS_WORD_SEQNO(getField(dto, "TRANS_WORD_SEQNO"));       

    	EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
    	ejbConn.trx("sendInternalModel", new Class[]{ CommonModel.class }, new Object[]{ ymdm002 });
    }

    /**
     * @param field
     * @return
     */
    private String getMaterialGoods(String schKind) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        if(YmCommonConst.NEW_SCH_WORK_KIND_SVFL.equals(schKind)|| // Slab 외판출하상차
        	YmCommonConst.NEW_SCH_WORK_KIND_GVFL.equals(schKind)|| // COIL 제품출하상차
          	YmCommonConst.NEW_SCH_WORK_KIND_GVF1.equals(schKind)|| // Coil 제품출하상차
    	    YmCommonConst.NEW_SCH_WORK_KIND_GVF2.equals(schKind)||
    	    
    	    YmCommonConst.NEW_SCH_WORK_KIND_GTFL.equals(schKind)|| // COIL 제품출하상차
		    YmCommonConst.NEW_SCH_WORK_KIND_GTF1.equals(schKind)|| // Coil 제품출하상차
		    YmCommonConst.NEW_SCH_WORK_KIND_GTF2.equals(schKind)||
		    
		    YmCommonConst.NEW_SCH_WORK_KIND_GPFL.equals(schKind)|| // COIL 제품출하상차
		    YmCommonConst.NEW_SCH_WORK_KIND_GPF1.equals(schKind)|| // Coil 제품출하상차
		    YmCommonConst.NEW_SCH_WORK_KIND_GPF2.equals(schKind)    	    
             ){ // Coil 제품출하상차	
    	    return "1";
        }else if(YmCommonConst.NEW_SCH_WORK_KIND_GVML.equals(schKind)|| // COIL 제품이송상차
			     YmCommonConst.NEW_SCH_WORK_KIND_GVM2.equals(schKind)|| // COIL 제품이송상차
	   		     YmCommonConst.NEW_SCH_WORK_KIND_GVM3.equals(schKind)){ // COIL 제품이송상차	
            return "2";            
        }else if(YmCommonConst.NEW_SCH_WORK_KIND_GVMU.equals(schKind)|| // COIL 제품이송하차
			     YmCommonConst.NEW_SCH_WORK_KIND_GVM4.equals(schKind)|| // COIL 제품이송하차	 	
   			     YmCommonConst.NEW_SCH_WORK_KIND_GVM5.equals(schKind)){ // COIL 제품이송하차			
            return "2";            
        }else if(YmCommonConst.NEW_SCH_WORK_KIND_CVML.equals(schKind)|| // COIL 소재이송상차
			     YmCommonConst.NEW_SCH_WORK_KIND_CVM2.equals(schKind)|| // COIL 소재이송상차
	   		     YmCommonConst.NEW_SCH_WORK_KIND_CVM3.equals(schKind)){ // COIL 소재이송상차
            return "3";            
        }else if(YmCommonConst.NEW_SCH_WORK_KIND_CVMU.equals(schKind)|| // COIL 소재이송하차
			     YmCommonConst.NEW_SCH_WORK_KIND_CVM4.equals(schKind)|| // COIL 소재이송하차	 
  		   	     YmCommonConst.NEW_SCH_WORK_KIND_CVM5.equals(schKind)){ // COIL 소재이송하차	 
            return "3";            
        }else if(YmCommonConst.NEW_SCH_WORK_KIND_SVMU.equals(schKind)) {
            return "4";            
        }
  		   	     	
        return "";
    }

      /**
	 * 오퍼레이션명 : 
	 *
	 * 야드 Level-2로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * 1.TC_CD	: THTH430.
        * 2.I/F ID	: YM-AIF-039.
        * 
        * 전문코드		CHAR	7	
        * saddle 명		CHAR	5	
        * 코일 유무		CHAR	1	
        * filler			CHAR	37			
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
	public boolean acySkidStatus(String msg) {
        logger.println(LogLevel.DEBUG, this, "ACY_SKID STATUS 처리");
        ymCommonDAO dao  = ymCommonDAO.getInstance();
        String szSaddleUseYn ="";
        String szSaddleUsage ="";
        String sQueryId  ="";
        String szSaddle ="";
        String szStock_id ="";
        String szStockid ="";
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
	        Map tc = ymCommonDAO.readColumnLenOfTc(getField(parseData, "전문코드"));            
	        /**
	         * valid check
	         */
	        validRecDataOfSkidStatus(parseData, tc);            
            /**
             * 수신항목의 'saddle 명', '코일 유무'를 가져온다.
             */
            String rCoilYN 		= getField(parseData, "coil유무");
            String saddleName 	= getField(parseData, "saddle명");            
            String szfiller 	= StringHelper.evl(getField(parseData, "filler"), ""); 
           
          //CTS SKIP 대상
            //if(rCoilYN.equals("3")||rCoilYN.equals("4")||rCoilYN.equals("9")){
            if(rCoilYN.equals("3")||rCoilYN.equals("9")){
            	logger.println(LogLevel.DEBUG, this, "#####>> 생략 대상 전문 입니다. 코드:"+rCoilYN);
            	return true;
            }
            
            /**
             * SADDLE 설비 정보를 가져온다.
             */
            JDTORecord skidInfo = ymCommonDAO.readSkidStatInfo(YmCommonConst.YD_GP_1, saddleName);
            if(skidInfo == null || skidInfo.size() == 0) {
                throw new Exception("설비정보가 없습니다: "+ saddleName);
            }
            szSaddle = StringHelper.evl(getField(skidInfo, "EQUIP_GP"),"");
            szStockid = StringHelper.evl(getField(skidInfo, "STOCK_ID2"),"");
            
            
            /**
             * SADDLE 설비에 0000000000으로 존재 시 SKID 비우는 정보(4)가 오는 경우 삭제
             */
            if(rCoilYN.equals("4") && "0000000000".equals(szStockid)){
            	
            	logger.println(LogLevel.DEBUG, this, "#####>> SADDLE 설비에 0000000000 비우기:");
            	
            	ymCommonDAO.modifyStackStatOfEquip(YmCommonConst.STACK_STAT_U, szSaddle);
            	
            	sQueryId  = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateStackLayerStockDel";	        	    	
    	    	int iSeq = dao.updateData(sQueryId,new Object[]{szSaddle});
    	    	
    	    	return true;
            }else if(rCoilYN.equals("4")){
            	logger.println(LogLevel.DEBUG, this, "#####>> 생략 대상 전문 입니다. 코드2:"+rCoilYN);
            	return true;
            }
            
            
      
            if("8".equals(rCoilYN)){
	            if(!"".equals(szfiller)){
	            	szSaddleUsage =szfiller.substring(0,1); //FN(1자리:TO ,FROM  )
	            	
	            	
	            	//용도 수정######################################################################################
	            	if("F".equals(szSaddleUsage)){
	            		szSaddleUsage="FS";
	            	}else if("T".equals(szSaddleUsage)){
	            		szSaddleUsage="TS";
	            	}
	            
	            	if("FS".equals(szSaddleUsage)||"TS".equals(szSaddleUsage)){
		            	/*
		        	     *	1.	적치열 용도 수정
		        	     */
		        	    	sQueryId  = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateStackColUsageInfo";	        	    	
		        	    	int iSeq1 = dao.updateData(sQueryId,new Object[]{szSaddleUsage,szSaddle});
		        	    
		        	    /*
		        	     *	2.	용도수정으로 인한 저장영역 수정
		        	     */
		        	     	sQueryId  = "ym.facilitystatus.facilityinquiry.CraneSchDAO.deleteLocSearchInfo";	        	     	
		        	     	int iSeq2 = dao.updateData(sQueryId,new Object[]{szSaddle});            
	            	}
	            	//######################################################################################
	            	
	            	
	            	
	            	szSaddleUseYn =szfiller.substring(1,2);	//FN(2자리: N ,Y)
	            	
	            	//적치상태######################################################################################
	            	if("Y".equals(szSaddleUseYn)){
	            		szSaddleUseYn="O";
	            	}else if("N".equals(szSaddleUseYn)){
	            		szSaddleUseYn="C";
	            	}
	            	
	            	if("O".equals(szSaddleUseYn)||"C".equals(szSaddleUseYn)){
	            		/*
	            	     *	1.	적치상태 수정
	            	     */
	            	    	sQueryId  = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateCraneStackLayerActivStat";            	    	
	            	    	int iSeq1 = dao.updateData(sQueryId,new Object[]{szSaddleUseYn,szSaddle,"01","01"});
	            	    /*
	            	     *	2.	설비상태 수정
	            	     	
	            	     */ 
	            	     	sQueryId  = "ym.facilitystatus.facilityinquiry.CraneSchDAO.UpdateEquipStatInfo";            	     	
	            	     	int iSeq2 = dao.updateData(sQueryId,new Object[]{szSaddleUseYn,szSaddle});            
	            	}
	            	//######################################################################################
	            
	            }
	            return true;
            }
            
          
            
            /**
             * 수신항목의 '코일 유무'에 따라서
             * 1. 만약 '코일 유무'가 '1'이면 
             *    1.1 설비테이블에 '적재상태'를 영차('L')로 UPDATE
             *    1.2 최초동/중계동/목적동 SADDLE 정보를 가져온다.
             *        1.2.1 SADDLE 정보가 중계동이면  CTS 작업지시를 CALL 한다.
             *        1.2.2 SADDLE 정보가 목적동이면
             *              1.2.2.1 'SPM/HFL 보급' 작업예약이 존재하는지 확인하여
             * 						1.2.2.1.1 존재하면 SKIP
             * 						1.2.2.1.2 존재하지 않으면  '동간이적하차[CTCU]' 작업예약을 한다.
             * 2. 만약 '코일 유무'가 '0'이면 
             *    2.1 설비테이블에  '적재상태'를 공차('U')로 UPDATE
             *    2.2 적치단테이블에 코일번호를 UPDATE 한다.
             */
            
            if(!"".equals(szfiller)){
            	szSaddleUsage =szfiller.substring(0,1); //FN(1자리:TO ,FROM  )
            }
            
            logger.println(LogLevel.DEBUG, this, "##### 조회stock_id : "+getField(skidInfo, "STOCK_ID")+" szSaddleUsage:"+szSaddleUsage);
            
            if(("1".equals(rCoilYN) || "7".equals(rCoilYN))&& "T".equals(szSaddleUsage)){
            	if(szfiller.trim().length()==12){
            		szStock_id =szfiller.substring(2,12);
            	}else if(szfiller.trim().length()==11){
            		szStock_id =szfiller.substring(2,11);
            	}else if(szfiller.trim().length()==10){
            		szStock_id =szfiller.substring(2,10);
            	}else if(szfiller.trim().length()==9){
            		szStock_id =szfiller.substring(2,9);
            	}
            	 
            	//saddle위치를 l2 기준으로 변경 하기 
            	if(!"".equals(szStock_id) && "".equals(getField(skidInfo, "STOCK_ID"))){ 
            		logger.println(LogLevel.DEBUG, this, "##### saddle위치를 l2 기준으로 변경 하기  : "+szSaddle);
            		ymCommonDAO.modifyRelayOfStock(szSaddle, szStock_id);
            	} 
            	 
            }            
            
            logger.println(LogLevel.DEBUG, this, "##### 입력stock_id : "+szStock_id);
            
            JDTORecord sendInfo = null;
            if("9".equals(rCoilYN)) {
                ymCommonDAO.modifyEquipStatOfEquip(
                        YmCommonConst.EQUIP_STAT_C, 
                        getField(skidInfo, "EQUIP_GP"));
                ymCommonDAO.modifyStockStatOfLayer(
                        getField(skidInfo, "STOCK_ID"),
                        YmCommonConst.STACK_LAYER_ACTIVE_STAT_C,
                        getField(skidInfo, "STACK_LAYER_STAT"),
                        getField(skidInfo, "STACK_COL_GP"),
                        getField(skidInfo, "STACK_BED_GP"),
                        getField(skidInfo, "STACK_LAYER_GP"));
            }else if("7".equals(rCoilYN)) { //중계동 코일 정보 표기 작업
                ymCommonDAO.modifyEquipStatOfEquip(
                        YmCommonConst.EQUIP_STAT_O, 
                        getField(skidInfo, "EQUIP_GP"));
                ymCommonDAO.modifyStockStatOfLayer(
                		szStock_id,
                        YmCommonConst.STACK_LAYER_ACTIVE_STAT_O,
                        "L",
                        getField(skidInfo, "STACK_COL_GP"),
                        getField(skidInfo, "STACK_BED_GP"),
                        getField(skidInfo, "STACK_LAYER_GP"));
            }else {
                ymCommonDAO.modifyEquipStatOfEquip(
                        YmCommonConst.EQUIP_STAT_O, 
                        getField(skidInfo, "EQUIP_GP"));
                ymCommonDAO.modifyStockStatOfLayer(
                        getField(skidInfo, "STOCK_ID"),
                        YmCommonConst.STACK_LAYER_ACTIVE_STAT_O,
                        getField(skidInfo, "STACK_LAYER_STAT"),
                        getField(skidInfo, "STACK_COL_GP"),
                        getField(skidInfo, "STACK_BED_GP"),
                        getField(skidInfo, "STACK_LAYER_GP"));
                sendInfo = editSaddleInfo(skidInfo, getField(skidInfo, "EQUIP_GP"), rCoilYN,szStock_id);
            }
            /**
             * I/F ID	: YM-AIF-037
             * T/C		: THHC260
             * 01	전문코드		CHAR	07		
             * 02	SKID NO		CHAR	05		
             * 03	SKID 상태	CHAR	01		0:무, 1:유, 9:고장
             * 04	코일번호		CHAR	10		
             * 05	제작번호/행번	CHAR	13		
             * 06	두께			CHAR	05		
             * 07	폭			CHAR	05		
             * 08	외경			CHAR	05		
             * 09	중량			CHAR	05		
             * 10	길이			CHAR	05		
             * 11	SPARE		CHAR	939				
             */
            tc = ymCommonDAO.readColumnLenOfTc(YmCommonConst.TC_THHC260);
            StringBuffer sendMsg = new StringBuffer();
            sendMsg.append(YmCommonConst.TC_THHC260);
            appendMsg(sendMsg, saddleName,	getFieldLen(tc, "SKIDNO"));
            appendMsg(sendMsg, rCoilYN,		getFieldLen(tc, "SKID상태"));
            /**
             * 목적 saddle 정보가 없을 경우 저장품 0000000000 으로 하고 
             * 결과를 송신. 
             */
            if(sendInfo == null) {
                rCoilYN = "9";
            }
            /*
             * 2007.02.12 이정훈
             * coil 유무 '0' [없을 때] 추가 
             */
            logger.println(LogLevel.DEBUG, this, rCoilYN);
            if("9".equals(rCoilYN) || "0".equals(rCoilYN) ) {
            	logger.println(LogLevel.DEBUG, this, "확인");
                appendMsg(sendMsg, "",		getFieldLen(tc, "코일번호"));                                
                appendMsg(sendMsg, "",		getFieldLen(tc, "제작번호행번"));
                appendMsgNum(sendMsg, "",	getFieldLen(tc, "두께"));
                appendMsgNum(sendMsg, "",	getFieldLen(tc, "폭"));
                appendMsgNum(sendMsg, "",	getFieldLen(tc, "외경"));
                appendMsgNum(sendMsg, "",	getFieldLen(tc, "중량"));
                appendMsgNum(sendMsg, "",	getFieldLen(tc, "길이"));
                appendMsg(sendMsg, "",		getFieldLen(tc, "SPARE"));
            }else {
                appendMsg(sendMsg, getField(sendInfo, "STOCK_ID"),	 getFieldLen(tc, "코일번호"));                
                appendMsg(sendMsg, getField(sendInfo, "PRODUCT_NO"), getFieldLen(tc, "제작번호행번"));
                String t = YmCommonUtil.deletePoint(""+ (getfieldFloat(sendInfo, "COIL_T") * 1000));
                String w = YmCommonUtil.deletePoint(getField(sendInfo, "COIL_W"));
                appendMsgNum(sendMsg, t, 									getFieldLen(tc, "두께"));
                appendMsgNum(sendMsg, w, 									getFieldLen(tc, "폭"));
                appendMsgNum(sendMsg, getField(sendInfo, "COIL_OUTDIA"), 	getFieldLen(tc, "외경"));
                appendMsgNum(sendMsg, getField(sendInfo, "NET_WEIGH_WT"), 	getFieldLen(tc, "중량"));
                appendMsgNum(sendMsg, ""+ (getFieldInt(sendInfo, "COIL_LEN") * 10), 		getFieldLen(tc, "길이"));
                appendMsg(sendMsg, "",										getFieldLen(tc, "SPARE"));
            }
            sendQueue(YmCommonConst.TC_THHC260, sendMsg.toString());
            
            
            //CTS 작업실적 관리 2014.12.01
            if((rCoilYN.equals("1")||rCoilYN.equals("7")) && !szStock_id.equals("") ){
            /**
             * 이전 실적정보를 가져온다.
             */
            JDTORecord wrsltInfo = ymCommonDAO.readRastWrslt(szStock_id);
            if(wrsltInfo == null || wrsltInfo.size() == 0) {
            	logger.println(LogLevel.DEBUG, this, "##### 이전 실적정보를 가져온다.  : "+wrsltInfo.size());
            }else{
                 
            	
            	String sUpLoc = StringHelper.evl(getField(wrsltInfo, "CRANE_WRSLT_PUT_LOC"),"");
            	String sPutLoc = StringHelper.evl(getField(skidInfo, "STACK_COL_GP")+getField(skidInfo, "STACK_BED_GP")+getField(skidInfo, "STACK_LAYER_GP"),"");
            	String sCurSchCode =StringHelper.evl(getField(wrsltInfo, "SCH_WORK_KIND"),"");
            	String sPutYardGp ="1";
            	String sUserId ="THHC260";
        
            	            	
            	EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
	    	 	ejbConn.trx("insertUpPutWrslRtData",new  Class[]{String.class,String.class,String.class,
	    	 													 String.class,String.class,String.class },
						    	 					new Object[]{szStock_id, sUpLoc, sPutLoc
						    	 								, sCurSchCode, sPutYardGp, sUserId });
				
            	
            }
             
            }
            
	    }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
	    return true;
	}

    /**
     * 수신항목 'coil유무', 'saddle명'을 체크한다.
     * @param parseData	수신정보
     * @param tc		수신항목 길이 정보
     * @return
     */
    private void validRecDataOfSkidStatus(JDTORecord parseData, Map tc) throws Exception {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        String coilLoadYN = getFieldNvl(parseData, "coil유무");
        String saddleName = getFieldNvl(parseData, "saddle명");
        if(coilLoadYN.length() != getFieldLen(tc, "coil유무")) {
            throw new Exception("수신항목: 'coil 유무' Error: "+ coilLoadYN);
        }else if(saddleName.length() != getFieldLen(tc, "saddle명")) {
            throw new Exception("수신항목 'saddle 명' Error: "+ saddleName);
        }
    }

    /**
     * 정보 이중 발생 Skip
     * @param parseData	수신정보
     * @param tc		수신항목 길이 정보
     * @return
     */
    /*private JDTORecord validDataOfSkidStatus(String ydGp,String CoilYN, String saddleName) throws Exception {
    	JDTORecord saddleInfo = readSkidStatLayerInfo(ydGp, CoilYN, saddleName);
        String saddleName = getFieldNvl(parseData, "saddle명");
        if(coilLoadYN.length() != getFieldLen(tc, "coil유무")) {
            throw new Exception("수신항목: 'coil 유무' Error: "+ coilLoadYN);
        }else if(saddleName.length() != getFieldLen(tc, "saddle명")) {
            throw new Exception("수신항목 'saddle 명' Error: "+ saddleName);
        }
    }*/

    
    /**
     * 코일유무에 따라 설비/적치단의 SADDLE 정보를 수정한다. 
     * @param skidInfo
     * @param field
     * @return
     * @throws Exception
     */
    private JDTORecord editSaddleInfo(

            JDTORecord skidInfo, String equipGp, String coilYN, String szStock_id) throws Exception {
    	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		
        JDTORecord saddleInfo = null;
        if(YmCommonConst.COIL_YN_Y.equals(coilYN)) {
            ymCommonDAO.modifyStackStatOfEquip(YmCommonConst.STACK_STAT_L, equipGp);
            saddleInfo = getSaddleBayInfo(skidInfo, szStock_id);
        }else {
            saddleInfo = skidInfo;
            ymCommonDAO.modifyStackStatOfEquip(YmCommonConst.STACK_STAT_U, equipGp);
            ymCommonDAO.modifyStockStatOfLayer0("", YmCommonConst.STACK_LAYER_STAT_E, equipGp);
            setSchInfoFromWbook_03(equipGp.substring(0, 1), equipGp.substring(1, 2));
        }
        return saddleInfo;
    }

      /**
	 * 오퍼레이션명 : 
	 *
	 * SADDLE 스케쥴 생성
        * param yd	야드구분
        * param bay	동구분
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */      
    public void setSchInfoFromWbook_03(String yd, String bay) {
        try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
			
            new EJBConnector("default","JNDICWrkOrdReg",this).trx(
                    "setSchInfoFromWbook_03",
                    new Class[]{ String.class, String.class, String.class }, 
                    new Object[]{ yd, bay, YmCommonConst.NEW_SCH_WORK_KIND_CTML });
        }catch (Exception e) {
            logger.println(LogLevel.DEBUG, this, "##### setSchInfoFromWbook_03 ERROR");
            e.printStackTrace();
        }
    }
    
    /**
     * 최초동/중계동/목적동 SADDLE 정보를 가져온다.
     * @param skidInfo		SADDLE 설비 정보
     * @return
     * @throws Exception
     */
    private JDTORecord getSaddleBayInfo(JDTORecord skidInfo , String szStock_id) throws Exception {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        JDTORecord readInfo = null;
        String equipGp 		= getField(skidInfo, "EQUIP_GP");
        if(isFirstSaddle(skidInfo)) { 	//최초 SADDLE에 위치
            readInfo = skidInfo;
        }else {							//중계 또는 최종 SADDLE 위치	
//            editStockStatOfLayer(ymCommonDAO.readRelayInfo(equipGp), equipGp);
	    	  ymCommonDAO.modifyStockStatOfLayer(szStock_id,YmCommonConst.STACK_LAYER_STAT_L, equipGp);
	    	  considerSupply(equipGp.substring(1, 2));
	    	  readInfo = ymCommonDAO.readRelayInfo2(szStock_id);
            if(readInfo == null || readInfo.size() == 0) {
                ymCommonDAO.modifyStockStatOfLayer("0000000000", "L", equipGp, "01", "01");
                //throw new Exception("목적동이 저장품에 존재하지 않습니다.");
            }else {
                destinationSaddle(readInfo, "", equipGp, "1");
            }
        }
        return readInfo;
    }

      /**
	 * 오퍼레이션명 : 
	 *
	 * SADDLE 목적동 작업을 고려한다.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */      
    public void destinationSaddle(JDTORecord readInfo, String stockId, String equipGp, String gp) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        if("2".equals(gp)) {
            considerSupply(equipGp.substring(1, 2));
            readInfo = ymCommonDAO.readCoilInfo(stockId);
            editStockStatOfLayer(readInfo, equipGp);
        }
        ymCommonDAO.modifyRelayOfStock("", getField(readInfo, "STOCK_ID"));
        new CraneSchDAO().updateCoilCommonLocInfo(
                getField(readInfo, "STOCK_ID"),
                equipGp +"0101");
        String realyGp = getField(readInfo, "CTS_RELAY_YN"); 
        if("2".equals(gp)) {
            realyGp = "N";
        }
        
        if(YmCommonConst.CTS_RELAY_GP_Y.equals(realyGp)) {	//중계  SADDLE 위치		                    
            callCTSWorkOrder(equipGp, getField(readInfo, "STOCK_ID"));
        }else {					//목적 SADDLE 위치
            ymCommonDAO.modifyMoveEquipOfStock(
                    "", "", "", getField(readInfo, "STOCK_ID"));
            if(! isSupply(getField(readInfo, "SCH_WORK_KIND"), equipGp)) {			                
                createWBookOfChange(readInfo, equipGp);
            }	                    
        }
    }

    /**
     * 중계동일경우 CTS 작업지시를 CALL 한다.
     * @param equipGp	설비구분
     * @param stockId	저장품ID	
     */
    private void callCTSWorkOrder(String equipGp, String stockId) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        callCtsWorkOrder(equipGp, stockId);
    }

    /**
     * @param equipGp
     * @param stockId
     */
    private void callCtsWorkOrder(String equipGp, String stockId) {        
        try {
        	
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
            new EJBConnector("default","JNDICTSSchReg",this).trx(
                    "callCtsWorkInfo",
                    new Class[]{ String.class, String.class, String.class }, 
                    new Object[]{ equipGp, stockId, "2" });
        }catch (Exception e) {
            logger.println(LogLevel.DEBUG, this, "##### CTS 작업지시 ERROR");
            e.printStackTrace();
        }	                    
    }

    /**
     * 직보급 작업 검색을 CALL 한다.
     * @param equipGp	설비구분
     */
    private void considerSupply(String bayGp) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        if(YmCommonConst.BAY_GP_B.equals(bayGp)) {
            callRequestSearch(
                    YmCommonConst.YD_GP_1,
                    YmCommonConst.WORK_HFL_H,
                    YmCommonConst.SUPPLY_1,
                    "SPMHFLLineInRequestSearch");
        }else if(YmCommonConst.BAY_GP_D.equals(bayGp)) {
            callRequestSearch(
                    YmCommonConst.YD_GP_1,
                    YmCommonConst.WORK_SPM_S,
                    YmCommonConst.SUPPLY_1,
                    "SPMHFLLineInRequestSearch");
        }
    }
    /**
     * 직보급 작업 검색을 CALL 한다. 최규성
     * @param equipGp	설비구분
     */
    private void isConsiderSupply(String bayGp)
    {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
    	if(YmCommonConst.BAY_GP_A.equals(bayGp)) {		// A동 HFL 
            callRequestSearch(
                    YmCommonConst.YD_GP_3,
                    YmCommonConst.WORK_HFL_H,
                    YmCommonConst.SUPPLY_1,
                    "SPMHFLLineInRequestSearch");
        }else if(YmCommonConst.BAY_GP_C.equals(bayGp)) { // C동 SPM1
            callRequestSearch(
                    YmCommonConst.YD_GP_3,
                    YmCommonConst.WORK_SPM_S,
                    YmCommonConst.SUPPLY_1,
                    "SPMHFLLineInRequestSearch");
        }else if(YmCommonConst.BAY_GP_D.equals(bayGp)) {	// D동 SPM2
            callRequestSearch2(
                    YmCommonConst.YD_GP_3,
                    YmCommonConst.NEW_WORK_SPM_N,
                    YmCommonConst.SUPPLY_1,
                    "SPMLineInRequestSearch");
        }
    }
    
    /**
     * SPM2 보급 검색을 CALL 한다. 최규성 
     * @param yd		야드구분
     * @param work		작업구분
     * @param supplyGp	보급구분
     * @param mName		메소드이름
     */
    private void callRequestSearch2(String yd, String work, String supplyGp, String mName) {
        EJBConnector ejbConn = null;
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
			
	        ejbConn = new EJBConnector("default","JNDISPMConStatMReg",this);
            ejbConn.trx(
                    mName,
            		new Class[]{ String.class, String.class, String.class },
            		new Object[]{ yd, work, supplyGp });
        }catch (Exception e) {
            logger.println(LogLevel.DEBUG, this, "##### SPM2 보급 검색 ERROR");
            e.printStackTrace();
        }
    }
    /**
     * SPM/HFL 보급 검색을 CALL 한다.
     * @param yd		야드구분
     * @param work		작업구분
     * @param supplyGp	보급구분
     * @param mName		메소드이름
     */
    private void callRequestSearch(String yd, String work, String supplyGp, String mName) {
        EJBConnector ejbConn = null;
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
			
	        ejbConn = new EJBConnector("default","JNDISPMConStatReg",this);
            ejbConn.trx(
                    mName,
            		new Class[]{ String.class, String.class, String.class },
            		new Object[]{ yd, work, supplyGp });
        }catch (Exception e) {
            logger.println(LogLevel.DEBUG, this, "##### SPM/HFL 보급 검색 ERROR");
            e.printStackTrace();
        }
    }

    /**
     * COIL동간이적하차 작업예약을 생성한다.
     * @param readInfo	SADDLE 정보
     * @param equipGp	설비구분
     */
    private void createWBookOfChange(JDTORecord readInfo, String equipGp) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        if(10 == getField(readInfo, "CARUNLOAD_PUT_LOC").length()) {
            createWBook(readInfo, equipGp, 
                    YmCommonConst.SCH_WORK_LOC_DECISION_METHOD_O, 
                    getField(readInfo, "CARUNLOAD_PUT_LOC"));
        }else {
            createWBook(readInfo, equipGp, YmCommonConst.SCH_WORK_LOC_DECISION_METHOD_S, "");
        }
    }

    /**
     * 현재진도 코드에 따른 저장품이동조건을 리턴한다.
     * @param currProg	코일 현재 진도코드
     */
    private String getSaddleUnloadTerm(String stockId, String item, String keepYN) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
        String[] unload = YmCommonUtil.getCoilCurrProgCd(stockId,"");
        if(YmCommonConst.CURR_PROG_CD_COIL_M.equals(unload[0])||
        		YmCommonConst.CURR_PROG_CD_COIL_P.equals(unload[0])) {
            if("Y".equals(keepYN)) {
                if(YmCommonConst.ITEM_CG.equals(item)) {
                    return YmCommonConst.NEW_STOCK_MOVE_TERM_M2;
                }else {
                    return YmCommonConst.NEW_STOCK_MOVE_TERM_M1;
                }                
            }else {
                return unload[1];
            }
        }
        return unload[1];
    }

    /**
     * CTS 목적동 SADDLE에 올려진 저장품에 대해 하차 작업을 처리한다. 
     * @param readInfo	저장품정보
     * @param colGp		적치열
     * @param operGp	오퍼레이터 지정 구분
     * @param loc		PUT위치
     */
    private void createWBook(JDTORecord readInfo, String colGp, String operGp, String loc) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
    	String sYdZoneGp="";
    	String sYdSchCd="";
    	String sYdGateGp="1";
    	
    	String queryid = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCoilCommonInfo";
        JDTORecord jrecord = ymCommonDAO.getCommonInfo(queryid, new Object[]{getField(readInfo, "STOCK_ID")});
 
		if (jrecord != null) { 
			sYdZoneGp 	= StringHelper.evl(jrecord.getFieldString("YD_ZONE_GP"), "");	
			sYdGateGp 	= StringHelper.evl(jrecord.getFieldString("통로구분"), "1");	 
		}
		
		logger.println(LogLevel.DEBUG, this, "CTS 하차 작업예약 생성(목적존):>>>"+sYdZoneGp );
		logger.println(LogLevel.DEBUG, this, "CTS 하차 작업예약 생성(통로구분):>>>"+sYdGateGp );
		
		//if(!"BZ".equals(sYdZoneGp) && !"HZ".equals(sYdZoneGp)&& !"".equals(sYdZoneGp)){
		if("2".equals(sYdGateGp)){
			sYdSchCd = YmCommonConst.NEW_SCH_WORK_KIND_CCMR ; //CTS 하차(2) --CTS기준 왼쪽
		}else {
			sYdSchCd = YmCommonConst.NEW_SCH_WORK_KIND_CCMU ; //CTS 하차	--CTS기준 오를쪽
		}
		
		logger.println(LogLevel.DEBUG, this, "CTS 하차 작업예약 생성(스케쥴코드):>>>"+sYdSchCd );
		
    	// 작업예약 ID 생성.
        String nextWBookId = ymCommonDAO.createWBook(colGp, sYdSchCd, operGp, loc);
        
        // 작업예약 정보 STOCK TBL에 수정.
        ymCommonDAO.modifyWbookIdOfStock(
										nextWBookId, 
										getSaddleUnloadTerm( getField(readInfo, "STOCK_ID"),getField(readInfo, "STOCK_ITEM"),getField(readInfo, "KEEPSTOCK_STL_GP")), 
										getField(readInfo, "STOCK_ID"));
        
        // 적치 단 상태 변경.
        ymCommonDAO.modifyLayerStatOfLayer(YmCommonConst.STACK_LAYER_STAT_S, colGp, getField(readInfo, "STOCK_ID"));
        
        // Schedule 기동.
        callCRSchedule(nextWBookId);
    }

      /**
	 * 오퍼레이션명 : 
	 *
	 * 야드 Level-2로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * 1.TC_CD	: THTH440.
        * 2.I/F ID	: YM-AIF-040.
        * 
        * 전문코드		CHAR	7	
        * 코일 상차 유무	CHAR	1	
        * CTS 명		CHAR	5	
        * saddle 명		CHAR	5	
        * filler		CHAR	32			
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */           
	public boolean acyCTSCoilInfo(String msg) {
        logger.println(LogLevel.DEBUG, this, "### ACY_CTS 코일정보 처리");
	    try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
//            /**
//             * Message Parsing.
//             */
//            JDTORecord parseData = new Level2Parser().parse(msg);
//            logger.println(LogLevel.DEBUG, this, parseData);
//	        Map tc = ymCommonDAO.readColumnLenOfTc(getField(parseData, "TC code"));
//	        
//	        /**
//	         * valid check
//	         */
//            validRecDataOfCTSCoilInfoReq(parseData, tc);
//
//            /**
//             * 수신항목의 '코일 상차 유무', 'CTS 명'을 가져온다.
//             */
//            String ctsName 		= getField(parseData, "CTS 명");
//            String coilLoadYN 	= getField(parseData, "코일 상차 유무");
//                        
//            /**
//             * I/F ID	: YM-AIF-025
//             * T/C		: THHC131
//             * 1	전문코드	TC				CHAR	7
//             * 2	발생일시	OccurDateTime	CHAR	12
//             * 3	코일번호	CoilNo			CHAR	10
//             */
//            tc = ymCommonDAO.readColumnLenOfTc(YmCommonConst.TC_THHC131);
//            StringBuffer sendMsg = new StringBuffer();
//            sendMsg.append(YmCommonConst.TC_THHC131);		//전문코드
//            sendMsg.append(YmCommonUtil.getStringYMDHM());	//발생일시            
//
//            /**
//             * 수신항목의 '코일 상차 유무'에 따라서 코일정보를 셋팅한다.
//             */
//            JDTORecord coilInfo = ymCommonDAO.readCTSCoilInfo(YmCommonConst.YD_GP_1, ctsName);
//
//            //코일번호
//            if(YmCommonConst.COIL_YN_Y.equals(coilLoadYN)) {
//                appendMsg(sendMsg, getField(coilInfo, "COIL_NO"), getFieldLen(tc, "코일번호"));
//            }else {
//                appendMsg(sendMsg, "", getFieldLen(tc, "코일번호"));
//            }
//
//            sendQueue(YmCommonConst.TC_THHC131, sendMsg.toString());        	
	    }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
	    return true;
	}

      /**
	 * 오퍼레이션명 : 
	 *
	 * 야드 Level-2로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * 1.TC_CD	: THTH420.
        * 2.I/F ID	: YM-AIF-038.
        * 
        * 전문코드		CHAR	7-THTH420
        * 운전 mode  		CHAR	1
        * 위치			CHAR	6
        * coil 유무		CHAR	1
        * coil no			CHAR	10
        * 운전 mode  		CHAR	1
        * 위치			CHAR	6
        * coil 유무		CHAR	1
        * coil no			CHAR	10
        * saddle 번호		CHAR	2
        * saddle 번호		CHAR	2
        * 중계구역 사용 여부	CHAR	2
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                
	public boolean acyCTSStatus(String msg) {
        logger.println(LogLevel.DEBUG, this, "TODO: ACY_C.T.S STATUS 처리");
	    try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
	        /**
	         * Message Parsing
	         */
            JDTORecord parseData = new Level2Parser().parse(msg);
            logger.println(LogLevel.DEBUG, this, parseData);
	        Map tc = ymCommonDAO.readColumnLenOfTc(getField(parseData, "TCcode"));
	        
            /**
             * vlaid check.
             */
            validRecDataOfCTSStatusReq(parseData, tc);

            /**
             * 수신항목을 가져온다.
             */
            String loc1		= getField(parseData, "위치1");
            String loc2 	= getField(parseData, "위치2");
            String mode1 	= getField(parseData, "운전mode1");
            String mode2 	= getField(parseData, "운전mode2");
            String coilYN1 	= getField(parseData, "coil유무1");
            String coilYN2 	= getField(parseData, "coil유무2");
            String coilNo1 	= getField(parseData, "coilno1");
            String coilNo2 	= getField(parseData, "coilno2");
			int sad1 = getFieldLen(tc, "saddle번호1");
			int sad2 = getFieldLen(tc, "saddle번호2");
			int use  = getFieldLen(tc, "중계구역사용여부");
            String saddle1 	= getField(parseData, "saddle번호1");
            String saddle2 	= getField(parseData, "saddle번호2");            
            String useYN 	= getField(parseData, "중계구역사용여부");

            /**
             * C.T.S 상태정보를 UPDATE.
             * -코일 공통정보를 읽어서 해당 진도코드로 
             * --'저장품상태' UPDATE
             * --'저장품이동조건' CM[CTS이동]으로 UPDATE
             */
            editCurrProgAndTerm(coilNo1, coilNo2);
			editCTSStatus(mode1, coilYN1, useYN, coilNo1, YmCommonConst.CTS_GP_1XTC01);
			editCTSStatus(mode2, coilYN2, useYN, coilNo2, YmCommonConst.CTS_GP_1XTC02);
			editStockOfLayer(coilNo1, coilYN1, YmCommonConst.CTS_GP_1XTC01);
			editStockOfLayer(coilNo2, coilYN2, YmCommonConst.CTS_GP_1XTC02);			

            /**
             * I/F ID	: YM-AIF-036
             * T/C		: THHC250	
             * 01	전문코드			CHAR	07		
             * 02	C.T.S 1 상태		CHAR	01		1:ON LINE, 2:OFF LINE, 9:고장
             * 03	C.T.S 1 ADDRESS	CHAR	06		
             * 04	C.T.S 1 코일유무	CHAR	01		0:무, 1:유
             * 05	C.T.S 1 코일번호	CHAR	10		
             * 06	C.T.S 2 상태		CHAR	01		1:ON LINE, 2:OFF LINE, 9:고장
             * 07	C.T.S 2 ADDRESS	CHAR	06		
             * 08	C.T.S 2 코일유무	CHAR	01		0:무, 1:유
             * 09	C.T.S 2 코일번호	CHAR	10		
             * 10	SPARE			CHAR	957			
             */
            tc = ymCommonDAO.readColumnLenOfTc(YmCommonConst.TC_THHC250);
			StringBuffer sendMsg = new StringBuffer();
			sendMsg.append(YmCommonConst.TC_THHC250);
			appendMsg(sendMsg, mode1, 	getFieldLen(tc, "CTS1상태"));
			appendMsg(sendMsg, loc1, 	getFieldLen(tc, "CTS1ADDRESS"));
			appendMsg(sendMsg, coilYN1, getFieldLen(tc, "CTS1코일유무"));	
			appendMsg(sendMsg, coilNo1, getFieldLen(tc, "CTS1코일번호"));	
			appendMsg(sendMsg, mode2, 	getFieldLen(tc, "CTS2상태"));	
			appendMsg(sendMsg, loc2, 	getFieldLen(tc, "CTS2ADDRESS"));
			appendMsg(sendMsg, coilYN2, getFieldLen(tc, "CTS2코일유무"));
			appendMsg(sendMsg, coilNo2, getFieldLen(tc, "CTS2코일번호"));
			appendMsg(sendMsg, saddle1, sad1);
			appendMsg(sendMsg, saddle2, sad2);
			appendMsg(sendMsg, useYN, use);
			appendMsg(sendMsg, "", getFieldLen(tc, "SPARE") - (sad1 + sad2 + use));
			
			sendQueue(YmCommonConst.TC_THHC250, sendMsg.toString());
	    }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
	    return true;
	}

    /**
     * '저장품상태', '저장품이동조건'을 UPDATE
     * @param coilNo1	CTS1 코일번호
     * @param coilNo2	CTS2 코일번호
     */
    private void editCurrProgAndTerm(String coilNo1, String coilNo2) {
        JDTORecord dto = null;
        if(! "".equals(coilNo1)) {
            ymCommonDAO.modifyStockTermOfStock(
                    YmCommonUtil.getCoilCurrProgCd(coilNo1,"")[1],
                    coilNo1);
        }
        if(! "".equals(coilNo2)) {
            ymCommonDAO.modifyStockTermOfStock(
                    YmCommonUtil.getCoilCurrProgCd(coilNo2,"")[1],
                    coilNo1);
        }
    }

    /**
     * 수신항목 '위치1', '위치2', '운전mode1', '운전mode2', 'coil유무1', 'coil유무2', 
     * 'coilno1', 'coilno2' 체크한다.
     * @param parseData	수신정보
     * @param tc		수신항목  길이 정보
     * @return
     */
    private void validRecDataOfCTSStatusReq(JDTORecord parseData, Map tc) throws Exception {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        String loc1 	= getFieldNvl(parseData, "위치1");
        String loc2 	= getFieldNvl(parseData, "위치2");
        String mode1 	= getFieldNvl(parseData, "운전mode1");
        String mode2 	= getFieldNvl(parseData, "운전mode2");
        String coilYN1 	= getFieldNvl(parseData, "coil유무1");
        String coilYN2 	= getFieldNvl(parseData, "coil유무2");
        String coilNo1 	= getFieldNvl(parseData, "coilno1");
        String coilNo2 	= getFieldNvl(parseData, "coilno2");        
        if(mode1.length() != getFieldLen(tc, "운전mode1")) {
            throw new Exception("수신항목 '운전mode1' ERROR: "+ mode1);
        }else if(mode2.length() != getFieldLen(tc, "운전mode2")) {
            throw new Exception("수신항목 '운전mode2' ERROR: "+ mode2);
        }else if(coilYN1.length() != getFieldLen(tc, "coil유무1")) {
            throw new Exception("수신항목 'coil유무1' ERROR: "+ coilYN1);
        }else if(coilYN2.length() != getFieldLen(tc, "coil유무2")) {
            throw new Exception("수신항목 'coil유무2' ERROR: "+ coilYN2);
        }else if(coilNo1.length() != getFieldLen(tc, "coilno1")) {
            throw new Exception("수신항목 'coilno1' ERROR: "+ coilNo1);
        }else if(coilNo2.length() != getFieldLen(tc, "coilno2")) {
            throw new Exception("수신항목 'coilno2' ERROR: "+ coilNo2);
        }else if(loc1.length() != getFieldLen(tc, "위치1")) {
            throw new Exception("수신항목 '위치1' ERROR: "+ loc1);
        }else if(loc2.length() != getFieldLen(tc, "위치2")) {
            throw new Exception("수신항목 '위치2' ERROR: "+ loc2);
        } 
    }

    /**
     * 설비테이블에 CTS 정보를 UPDATE
     * @param mode		운전mode
     * @param coilYN	coil유무
     * @param useYN		중계구역사용여부
     * @param coilNo	coilno
     * @param equipGp	설비구분
     * 
     */
    private void editCTSStatus(
            String mode, String coilYN, String useYN, String coilNo, String equipGp){
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
		if(YmCommonConst.COIL_YN_Y.equals(coilYN)) {
			ymCommonDAO.modifyCTSStatusOfEquip(mode, coilYN, useYN, coilNo, equipGp);				
		}else {
		    ymCommonDAO.modifyCTSStatusOfEquip(mode, coilYN, useYN, equipGp);
		}
    }

    /**
     * 적치단의 저장품 정보를 수정한다.
     * @param coilNo	코일번호
     * @param coilYN	코일유무
     * @param colGp		적치열
     */
    private void editStockOfLayer(String coilNo, String coilYN, String colGp) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
		if(YmCommonConst.COIL_YN_N.equals(coilYN)) {
		    ymCommonDAO.modifyStockStatOfLayer("", YmCommonConst.STACK_LAYER_STAT_E, colGp.trim());
		}else {
		    ymCommonDAO.modifyStockStatOfLayer(coilNo, YmCommonConst.STACK_LAYER_STAT_L, colGp);
		}
    }
    
      /**
	 * 오퍼레이션명 : 
	 *
	 * 야드 Level-2로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * 1.TC_CD	: THCH620.
        * 2.I/F ID	: YM-AIF-032.
        * 
        * 전문코드		CHAR	07		
        * CRANE 번호	CHAR	04		
        * SPARE		CHAR	04		
        * 발생일			CHAR	06		YYMMDD
        * 발생시			CHAR	06		HHMMSS
        * C.T.S 명		CHAR	05		
        * SPARE		CHAR	118		
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                     
	public boolean acyCTSIniReq(String msg) {
        logger.println(LogLevel.DEBUG, this, " TODO: ACY_C.T.S 초기정보 요구 실적 처리");
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
	        Map tc = ymCommonDAO.readColumnLenOfTc(getField(parseData, "전문코드"));

            /**
             * valid check
             */            
            String ctsName = getFieldNvl(parseData, "C.T.S명");
            if(ctsName.length() != getFieldLen(tc, "C.T.S명")) {
                throw new Exception("수신항목 중 'C.T.S명' ERROR: "+ ctsName);
            }            

            /**
             * 1. CTS 초기화 정보를 가져온다.
             * 2. 초기정보 송신
             */            
            tc = ymCommonDAO.readColumnLenOfTc(YmCommonConst.TC_THHC172);
            if(YmCommonConst.EQUIP_KIND_ALL.equals(ctsName.substring(0, 1))) {
                sendAllCTSIniInfo(ymCommonDAO.readAllCTSIniInfo(), tc);
            }else {
                sendCTSIniInfo(
                        ymCommonDAO.readCTSIniInfo(YmCommonConst.YD_GP_1, ctsName), tc, ctsName);
            }
	    }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
	    return true;
	}

    /**
     * @param ctsInfo
     * @param tc
     * @param ctsName
     */
    private void sendCTSIniInfo(JDTORecord ctsInfo, Map tc, String ctsName) {
        /**
         * I/F ID	: YM-AIF-032
         * T/C		: THHC172
         * 01	전문 코드		CHAR	07		
         * 02	C.T.S 명		CHAR	05		
         * 03	코일적치 유무	CHAR	01		0:무, 1:적치, 9:고장
         * 04	군정보		CHAR	01		
         * 05	코일번호		CHAR	10
         * 06	제작번호/행번	CHAR	13		
         * 07	두께			CHAR	05		
         * 08	폭			CHAR	05		
         * 09	외경			CHAR	05		
         * 10	중량			CHAR	05		
         * 11	길이			CHAR	05		
         * 12	FROM ADDRESS	CHAR	05		
         * 13	TO ADDRESS		CHAR	05		
         * 14	SPARE			CHAR	4024				
         */        
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        StringBuffer sendMsg = new StringBuffer();
        sendMsg.append(YmCommonConst.TC_THHC172);
        sendMsg.append(ctsName);            
        appendMsg(sendMsg, getField(ctsInfo, "COIL_YN"), 	getFieldLen(tc, "코일적치유무"));
        appendMsg(sendMsg, "", 								getFieldLen(tc, "군정보"));
        String stockId = getField(ctsInfo, "STACK_STOCK");
        appendMsg(sendMsg, stockId,							getFieldLen(tc, "코일번호"));
        appendMsg(sendMsg, getField(ctsInfo, "PRODUCT_NO"), getFieldLen(tc, "제작번호행번"));
        String t = YmCommonUtil.deletePoint(""+ (getfieldFloat(ctsInfo, "COIL_T") * 1000));
        String w = YmCommonUtil.deletePoint(getField(ctsInfo, "COIL_W"));
        appendMsgNum(sendMsg, t, 								getFieldLen(tc, "두께"));
        appendMsgNum(sendMsg, w, 								getFieldLen(tc, "폭"));
        appendMsgNum(sendMsg, getField(ctsInfo, "COIL_OUTDIA"), getFieldLen(tc, "외경"));
        appendMsgNum(sendMsg, getField(ctsInfo, "NET_WEIGH_WT"),getFieldLen(tc, "중량"));
        appendMsgNum(sendMsg, ""+ (getFieldInt(ctsInfo, "COIL_LEN") * 10), 	getFieldLen(tc, "길이"));        
        appendMsg(sendMsg, "", 									getFieldLen(tc, "FROMADDRESS"));
        String toAdd = null;
        if("".equals(stockId)) {
            toAdd = "";
        }else {
            toAdd = ymCommonDAO.readEquipGpOfToBe(
                    YmCommonConst.YD_GP_1, getField(ctsInfo, "CTS_RELAY_SADDLE"));
        }
        appendMsg(sendMsg, toAdd,	getFieldLen(tc, "TOADDRESS"));
        appendMsg(sendMsg, "",		getFieldLen(tc, "SPARE"));

        sendQueue(YmCommonConst.TC_THHC172, sendMsg.toString());
    }

    /**
     * @param ctsInfo
     * @param tc
     * @param ctsName
     */
    private void sendAllCTSIniInfo(List ctsAllInfo, Map tc) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        int ctsInfoCnt = ctsAllInfo != null ? ctsAllInfo.size() : 0;
        for(int i = 0; i < ctsInfoCnt; i++) {
            sendCTSIniInfo(
                    (JDTORecord)ctsAllInfo.get(i), 
                    tc, 
                    ymCommonDAO.readEquipGpOfToBe(YmCommonConst.YD_GP_1, 
                            getField((JDTORecord)ctsAllInfo.get(i), "EQUIP_GP")));
        }
    }

    /**
     * @param stackCol
     * @return
     */
    private boolean isHISCOVicCar(String stackCol) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
        if(YmCommonConst.YD_GP_3.equals(stackCol.substring(0, 1))) {
            if("02".equals(stackCol.substring(4, 6))) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param msg
     */
    // CGS
    // HYSCO 대차에 대한 하차 작업 검사.
    private void considerCTMU(String msg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
    	logger.println(LogLevel.DEBUG, this, "HYSCO 대차에 대한 하차 작업 검사."); 
    	
        JDTORecord dto = ymCommonDAO.readEquipInfo("3XTC02");
        logger.println(LogLevel.DEBUG, this, "3XTC02 장비정보[REC]"+dto);
        logger.println(LogLevel.DEBUG, this, "Para"+msg);
        String hmiMode = getField(dto, "HMI_STAT");
        if("C".equals(hmiMode)) {
            List lst = ymCommonDAO.readVicCarStock(msg.substring(18, 24));
            int lstCnt = lst != null ? lst.size() : 0;
            String schKind = null;
            for(int i = 0; i < lstCnt; i++) {
                dto = (JDTORecord)lst.get(i);
                if("P".equals(getField(dto, "STACK_LAYER_STAT"))) {
                    dto = ymCommonDAO.readCancelSchStock(getField(dto, "STOCK_ID"));                    
                    schKind = getField(dto, "SCH_WORK_KIND");
                    if("CTFL".equals(schKind)) {
                        callCancelSchdule(getField(dto, "SCH_ID"));
                    }
                }
            }
            if("CTFL".equals(schKind)) {
                for(int i = 0; i < lstCnt; i++) {
                    dto = (JDTORecord)lst.get(i);
                    if("L".equals(getField(dto, "STACK_LAYER_STAT"))) {
                        callCRSchedule(
                                createWBook(
                                        "CTMU", getField(dto, "STOCK_ID"), msg.substring(18, 24)));
                    }
                }                
            }
        }else {
            vicCarMoveOrder(msg);
        }
    }

    /**
     * @param stockId
     */
    private void cancelSchdule(String stockId) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        JDTORecord dto = ymCommonDAO.readCancelSchStock(stockId);
        if(dto != null) {
            if("".equals(getField(dto, "SCH_WORK_KIND"))) {
                callCancelSchdule("");                
            }
        }
    }

    /**
     * 작업예약을 생성한다.
     *
     * @param string
     * @param string2
     * @param field
     */
    private String createWBook(String schKind, String stockId, String col) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
      	/**
      	 *	작업예약을 생성한다.
      	 */  
        String nextWBookId = ymCommonDAO.createWBook(col, 
        											 schKind, 
        											 "", 
        											 "");
        /**
         * 저장품 TABLE에 작업예약 정보를 UPDATE한다.
         */
        ymCommonDAO.modifyWbookIdOfStock(nextWBookId, 
							             YmCommonUtil.getSlabCurrProgCd(stockId,"")[1], 
							             stockId);
		/**
		 *	적치단 TABLE에 상태를 'S'로 UPDATE한다.
		 */							             
		ymCommonDAO.modifyLayerStatOfLayer(YmCommonConst.STACK_LAYER_STAT_S, col, stockId);
		
        return nextWBookId;
    }

    /**
     * 동간보급상차 작업예약을 생성한다.
     *
     * @param dto
     */
    private void createWBook(String stockId, String col) {
    	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        createWBook(YmCommonConst.NEW_SCH_WORK_KIND_STSL, stockId, col);
    }
	
	/**
     * 최초 SADDLE 위치인지 확인한다.
     * @param skidInfo	SADDLE 설비 정보
     */
    private boolean isFirstSaddle(JDTORecord skidInfo) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
        return ! "".equals(getField(skidInfo, "STOCK_ID"));
    }
    
      /*
	 *  코일 대차 출발지시(권상/권하)
	 *
	 *	EJB 	: CTSStatusRegSBean
	 *	METHOD 	: bcyVicCarMoveOrder -> vicCarMoveOrder
	 */
	 	
	/*
	 *	슬라브 대차 출발지시(권상/권하)
	 *
	 *	EJB 	: CTSStatusRegSBean
	 *	METHOD 	: bsyVicCarMoveOrder -> vicCarMoveOrder
	 */	
		
	/**
	 * 오퍼레이션명 : 
	 *
	 * 야드 Level-2로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * 1.TC_CD	: CM1BP04.
        * 2.I/F ID	: YM-BIF-016.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                        
	public boolean bsyVicCarMoveOrder(String msg) {
        logger.println(LogLevel.DEBUG, this, "### BSY_대차이동지시 처리"); 
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
	        if(msg == null || msg.length() != 26) {
	            throw new Exception("출발지시전문 ERROR: "+ msg);
	        }
	        logger.println(LogLevel.DEBUG, this, "###msg: "+msg);
	        
	        
	        vicCarMoveOrder(msg);
        
	    }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
	    return true;
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 야드 Level-2로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다.
        * 1.TC_CD	: 없음
        * 2.I/F ID	: 없음
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */      
	public boolean bcyVicCarMoveOrder(String msg) {
        logger.println(LogLevel.DEBUG, this, "### BCY_대차이동지시 처리");
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
	        if(msg == null || msg.length() != 26) {
	            throw new Exception("출발지시전문 ERROR: "+ msg);
	        }
	        /**
	         * B열연 COIL 대차이면 스케쥴 편성 여부를 확인하여 처리한다.
	         */	        
	        if(isHISCOVicCar(msg.substring(18, 24))) {
	            //considerCTMU(msg);
	            vicCarMoveOrder(msg);
	        }else {
	            vicCarMoveOrder(msg);
	        }
        }catch(DAOException daoe) {
            throw daoe;
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
	    return true;
	}
 
 	/**
	 * 오퍼레이션명 : 
	 *
	 * MAIN => 화면 대차출발지시 버튼
        * 대차 출발지시전문을 셋팅한다.
        *
        * 전문코드	TC					CHAR	07		
        * 발생일자	Date				CHAR	10	YYYY-MM-DD
        * 발생시간	Time				CHAR	08	HH-MM-SS
        * 전문구분	Form				CHAR	01	I: Initialize, U: Update, D: Delete, R: Re-request
        * 전문길이	Message_Length		CHAR	04		
        * 대차번호	TCNo				CHAR	04	설비구분:TC(2)+TC NO(2)
        * 대차 현재동	TCPosition		CHAR	06	YARD구분(1)+동구분(1)+SPAN(2)+열 NO(2)
        * 대차 TO동	TCToPosition	CHAR	06		
        *
        * param vicCarNo	대차번호
        * param curBay	현재동
        * param toBay		목적동
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */           
    public boolean sendStartOrder(String sTcColGp, 
    							  String sCurColGp, 
    							  String sToColGp) {
    	
    	logger.println(LogLevel.DEBUG, this, "### 대차 출발지시전문을 셋팅"+sTcColGp+sCurColGp+sToColGp);
        
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
        /**
         *	1.	EQUIP TABLE의 대차 설정을 수정한다.
         *		1.1	대차에 저장품이 있으면 U(하차) , T(출발)
         *		1.2	대차에 저장품이 없으면 L(상차) , M(이동)
         *	2.	적치대의 적치능력을 초기화한다.
         *	3.	적치단 활성상태를 CLOSE한다.
         */ 
        	editCarLoadState(sTcColGp, sCurColGp);
        
        /**
         *	4.	대차출발전문을 송신한다.
         */
	        String sTc 		= "";
	        String sYdGp 	= sTcColGp.substring(0, 1);
	        
	        if(YmCommonConst.YD_GP_2.equals(sYdGp)) {
	            sTc = YmCommonConst.TC_CM1BP04;//SLAB 대차 출발지시
	        }else{
	        	sTc = YmCommonConst.TC_CN1BP04;//COIL 대차 출발지시
	    	}
	        
	        Map tc 	= ymCommonDAO.readColumnLenOfTc(sTc);
	        
	        StringBuffer sendMsg = new StringBuffer();
	        
	        sendMsg.append(sTc);
	        sendMsg.append(YmCommonUtil.getStringYMD("-"));
	        sendMsg.append(YmCommonUtil.getStringHMS("-"));
	        sendMsg.append(YmCommonConst.FORM_I);
	        appendMsgNum(sendMsg, ""+ (YmCommonUtil.getTotalLenOfTc(tc) - 30), getFieldLen(tc, "전문길이"));        
	        sendMsg.append(sTcColGp.substring(2, 6));        
	        sendMsg.append(sCurColGp);
	        sendMsg.append(sToColGp);        
	
	        sendQueue(sTc, sendMsg.toString());
        
        /**
         *	5.	적치단 현재위치 정보를 CLEAR(출발실적 CALL)
         */
        	vicCarMoveResult(sCurColGp);
        	
        return true;
    }

    /**
     * 대차 출발지시를 한다.
     * @param msg	출발지시전문[작업예약ID + 적치열 + 번지]
     */
    private void vicCarMoveOrder(String sMsg) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
    	
    	logger.println(LogLevel.DEBUG, this, "vicCarMoveOrder()==대차출발지시 START = vicCarMoveOrder");        
        logger.println(LogLevel.DEBUG, this, "### 수신MSG: "+ sMsg);        
        
        /**
         * 매개변수를 사용가능한 데이터로 변환한다.
         */
        String sStackCol = sMsg.substring(18, 24);
        String sYdGp	 = sStackCol.substring(0, 1); 
        String sBedGp  	 = "";
        
        if(YmCommonConst.YD_GP_2.equals(sYdGp)) {
            sBedGp = YmCommonConst.STACK_BED_GP_01;
        }else{
    		sBedGp = sMsg.substring(24, 26);
    	}
        logger.println(LogLevel.DEBUG, this, "vicCarMoveOrder() Bed 구분: "+ sBedGp);
        /**
         * 야드맵 정보 표현이 다르므로 구분하여 대차 정보를 가져온다.
         * 1. 야드구분이 '1','3'이면 A/B열연 COIL 대차
         * 2. 야드구분이 '2'이면 B열연 대차
         */
        /*
        --코일 대차정보를 리턴한다. "ym.common.dao.selectVicCarInfo"
        WITH TEMP AS (
		    SELECT  STACK_COL_GP,
		            STACK_BED_GP,
		            STACK_BED_QNTY_MAX
		    FROM    TB_YM_STACKER
		    WHERE   STACK_COL_GP = ?
		    AND     STACK_BED_GP = ?
		)
		SELECT  TEMP.STACK_COL_GP,                  --적치 열 구분
		        TEMP.STACK_BED_QNTY_MAX,
		        (
			        SELECT  COUNT(STOCK_ID)
			        FROM    TEMP,
			                TB_YM_STACKLAYER LAYER
			        WHERE   TEMP.STACK_COL_GP = LAYER.STACK_COL_GP
			        AND     LAYER.STACK_LAYER_STAT IN ('L','S','U')
		        ) AS CUR_QNTY,                      --적치 BED 수량 현재        
		        EQUIP.EQUIP_GP,                     --적재 상태
		        EQUIP.STACK_STAT,                   --적재 상태
		        EQUIP.CARLOAD_ASSIGN_YN,            --상차 지정 구분
		        EQUIP.CARUNLOAD_ASSIGN_YN,          --하차 지정 구분
		        EQUIP.CARLOAD_SCH_WORK_KIND,        --상차 SCHEDULE 작업 종류
		        EQUIP.CARUNLOAD_SCH_WORK_KIND,      --하차 SCHEDULE 작업 종류
		        NVL(EQUIP.EQUIP_KIND, '') || 
		        NVL(EQUIP.EQUIP_NO, '') AS TC_NO,       --대차번호
		        EQUIP.STACK_MAX_QNTY    AS MAX_QNTY,    --설비 적재 MAX
		        EQUIP.CURR_STOP_LOC,                --현재 정지 위치
		        EQUIP.CARLOAD_STOP_LOC,             --상차 정지 위치
		        EQUIP.CARUNLOAD_STOP_LOC            --하차 정지 위치
		FROM    TEMP,
		        TB_YM_EQUIP EQUIP
		WHERE   EQUIP.EQUIP_GP = SUBSTR(TEMP.STACK_COL_GP, 1, 1) || 'X' || SUBSTR(TEMP.STACK_COL_GP, 3, 4)
		*/
        JDTORecord tcInfoJr = ymCommonDAO.readVicCarInfo(sYdGp, 
        												 sStackCol, 
        												 sBedGp);
        logger.println(LogLevel.DEBUG, this, "대차 정보 결과: "+ tcInfoJr);  
        /**
         * 상차작업[L]이면 상차 출발지시, 하차작업[U]이면 하차 출발지시 한다. 
         */
        String sStackStat = StringHelper.evl(tcInfoJr.getFieldString("STACK_STAT"),"");
        
        if(YmCommonConst.STACK_STAT_L.equals(sStackStat)) {	
        	/*
        	 * 상차 후 출발지시 처리로직
        	 * 대차에 1매를 상차한 경우 
        	 */
            doLoadStartOfCar(tcInfoJr, sStackCol, sBedGp);
        }else if(YmCommonConst.STACK_STAT_U.equals(sStackStat)) {	
        	/*
        	 * 하차 후 출발지시 처리로직
        	 * 대차에 2~3매 상차한 경우
        	 */
            doUnLoadStartOfCar(tcInfoJr, sStackCol, sBedGp);	                
        }
    }

    /**
     * 상차 후 출발지시를 수행한다.
     * @param carInfo	대차정보
     * @param stackCol	적치열
     * @param bedGp		번지
     */
    // 적재 수량을 검사한다.
    private void doLoadStartOfCar(JDTORecord carInfo, 
    							  String stackCol, 
    							  String bedGp) { 
    	logger.println(LogLevel.DEBUG, this, "doLoadStartOfCar() ### 상차 후 출발 지시.");        

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
    	YdStockDAO ydStockDAO	= new YdStockDAO();
        logger.println(LogLevel.DEBUG, this, "### 상차 후 출발 지시.");        
        /**
         * 대차에 최대 적치 되었는지 확인하여 
         * 1. 최대 적치 되었으면 출발 한다. 
         * 2. 최대 적치 되지 않았으면 
         * 2.1 스케쥴종류가 LINE OFF 작업이면 
         *     2.1.1 야드맵을 확인하여 없으면 출발 한다.
         *     2.1.2 야드맵을 확인하여 있으면 대기 한다.
         * 2.2 스케쥴종류가 LINE OFF 작업이 아니면 대기한다.
         */
        
        String sYdGp = stackCol.substring(0, 1);
        
        /**
         *	현재 대차의 MAX COUNT 와 CUR COUNT를 비교한다.
         *	현재 대차의 적재가 가능하면(MAX COUNT가 아니면)
         */
        if(notMaxOfCarLoad(carInfo,stackCol)) {
        	
        	/**
        	 *	B열연 COIL/SLAB
        	 */

        	if(YmCommonConst.YD_GP_2.equals(sYdGp)||
        			YmCommonConst.YD_GP_3.equals(sYdGp)){
        		/**
        		 *	해당 대차가 PUT위치로 잡힌 스케쥴을 검색한다.
        		 */

			
        		List loadSchs 	= ymCommonDAO.readLoadSlabOfSch(stackCol);
        		int loadSchsCnt = loadSchs != null ? loadSchs.size() : 0;
        		logger.println(LogLevel.DEBUG, this, "대차가 PUT위치로 잡힌 스케쥴을 검색 Sch_CNT: " + String.valueOf(loadSchsCnt) );

        		/**
        		 *	해당 대차가 PUT위치로 잡힌 스케쥴이 없으면 대차출발한다.
        		 */

        		if(loadSchsCnt == 0) {
        			/**
        			 *	현재 대차의 EQUIP 및 LAYER 정보를 설정한다.
        			 */
        			editCarLoadState(carInfo); 
        			/**
        			 *	현재 대차를 하차동으로 출발처리한다.
        			 */
        			// 최규성.2010-01-14
        			// 신규대차의 출발지시는 L2에서 처리함.
//        			if(getField(carInfo,"TC_NO").equals("3XTC03") ||
//        					getField(carInfo,"TC_NO").equals("3XTC04") ||
//        					getField(carInfo,"TC_NO").equals("3XTC05")){
//        				logger.println(LogLevel.DEBUG, this, "신규대차: 대차를 하차동으로 출발처리 SKIP ");
//        			}else{
        			logger.println(LogLevel.DEBUG, this, "현재 대차를 하차동으로 출발처리 " + carInfo + ",하차동:"+getField(carInfo, "CARUNLOAD_STOP_LOC")); 	
        			sendStartOrder(carInfo, getField(carInfo, "CARUNLOAD_STOP_LOC"));                    

        			/**
        			 *	현재 대차를 상차동에 도착처리를 한다.
        			 */
        			logger.println(LogLevel.DEBUG, this, "현재 대차를 상차동에 도착처리 " + carInfo + ",적치열:"+stackCol+",적치대:"+bedGp);
        			receiveTcInfo(carInfo,
        					"L",
		                			  stackCol,
		                			  bedGp);
//        			}  // if(신규대차조건)
        		}
        	/**
        	 *	A열연 COIL
       		 */    
        	}else {
        		/**
            	 *	현재 대차의 상차 스케쥴이 CDLO인지를 체크.
            	 */	
                if(isSchKindOfLineOff(carInfo)){		                    
                    /**
	            	 *	현재 대차번지에 코일이 적치중인지를 체크.
	            	 *	적치중이면 true
	            	 */	
	            	logger.println(LogLevel.DEBUG,this,"==A대차상차 => "+stackCol); 
	            	logger.println(LogLevel.DEBUG,this,"==A대차상차 => "+bedGp); 
	            	 
                    boolean hasCoil = ymCommonDAO.readCoilOfLineOff(stackCol, 
                    												bedGp, 
                    												YmCommonConst.STACK_LAYER_STAT_L);
                    if(! hasCoil){
                        editCarLoadState(carInfo);
                        sendStartOrder(carInfo, getField(carInfo, "CARUNLOAD_STOP_LOC"));
                    }else{
                        logger.println(LogLevel.DEBUG, this, "### Line Off: 적치대에 코일 존재.");
                    }
                }else{
                	/**
			         *	해당 대차가 PUT위치로 잡힌 스케쥴을 검색한다.
			         */
	                List loadSchs 	= ymCommonDAO.readLoadSlabOfSch(stackCol);
	                int loadSchsCnt = loadSchs != null ? loadSchs.size() : 0;
	                /**
			         *	해당 대차가 PUT위치로 잡힌 스케쥴이 없으면 대차출발한다.
			         */
	                if(loadSchsCnt == 0) {
	                	/**
				         *	현재 대차의 EQUIP 및 LAYER 정보를 설정한다.
				         */
	                    editCarLoadState(carInfo); 
	                    /**
				         *	현재 대차를 하차동으로 출발처리한다.
				         */
	                    sendStartOrder(carInfo, getField(carInfo, "CARUNLOAD_STOP_LOC"));                    
	                }
                }                
            }
        /**
         *	현재 대차가 MAX COUNT의 수량이 적재되어 있으면.
         */    
        }else {
        	logger.println(LogLevel.DEBUG, this, "대차에 MAX COUNT의 수량이 적재 " );
        	/** 
	         *	B열연 COIL/SLAB
	         */
        	if(YmCommonConst.YD_GP_2.equals(sYdGp)
        	//		|| YmCommonConst.YD_GP_3.equals(sYdGp) 2013.06.19 임경빈 주임 요청 막음
                 ){
        		logger.println(LogLevel.DEBUG, this, "대차 스케쥴 취소-cancelCarLoadStateSchInfo() 호출" );
        		//대차 스케쥴 취소   	
        		cancelCarLoadStateSchInfo(sYdGp,stackCol);
        	}
        	
        	
        	if(YmCommonConst.YD_GP_3.equals(sYdGp)// 2013.06.19 임경빈 주임 요청 막음
              ){
            		logger.println(LogLevel.DEBUG, this, "B열연 코일야드 대차 스케쥴 취소-cancelCarLoadStateSchInfo() 호출" );
            		//대차 스케쥴 취소   	
            		cancelCarLoadStateSchInfo2(sYdGp,stackCol);
            	}
        	
        	logger.println(LogLevel.DEBUG, this,  "상차 작업에 대한 Status변경 - editCarLoadState() 호출" );
        	editCarLoadState(carInfo);
        	
			// 최규성.2010-01-14
			// 신규대차의 출발지시는 L2에서 처리함.
//			if(getField(carInfo,"TC_NO").equals("3XTC03") ||
//					getField(carInfo,"TC_NO").equals("3XTC04") ||
//					getField(carInfo,"TC_NO").equals("3XTC05")){
//				logger.println(LogLevel.DEBUG, this, "신규대차: 상차완료 후 출발지시 SKIP ");
//			}else{
        	logger.println(LogLevel.DEBUG, this, "상차 작업완료 대차 출발지시 호출 - sendStartOrder() 호출" );
        	sendStartOrder(carInfo, getField(carInfo, "CARUNLOAD_STOP_LOC"));
//			}  // if(신규대차조건)            
        	/**
        	 *	현재 대차를 상차동에 도착처리를 한다.
        	 */
        	// 함수 내부에 YD_GP_3에 관련된 부분은 주석처리되어있음. CGS
        	receiveTcInfo(carInfo,
            			  "L",
            			  stackCol,
            			  bedGp);
        }
    }
    
    /**
	* 대차 출발시에 대차가 PUT위치로 잡힌 스케쥴을 취소한다.
	*
	* @param stackCol : 대차위치정보
	*/
    private void cancelCarLoadStateSchInfo(String sYdGp,
    									String sTcCd) {
    	
    	try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
			
	    	/**
	         *	1.	해당 대차가 PUT위치로 잡힌 스케쥴을 검색한다.
	         */
	        List loadSchs	= ymCommonDAO.readLoadSlabOfSch(sTcCd);
	        
	        /**
	         *	2.	스케쥴 취소 모듈 CALL
	         */
	        String sMethod 	= "";
	        String sSchId 		= "";
	        JDTORecord schJr 	= null;
		
		if(YmCommonConst.YD_GP_2.equals(sYdGp)){
			sMethod = "cancelSlabSchInfo";	
		}else{
			sMethod = "cancelCoilSchInfo";	
		}
		
		EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
		
		for(int index = 0; index < loadSchs.size(); index++)
		{
			schJr  = (JDTORecord)loadSchs.get(index);
			sSchId = StringHelper.evl(schJr.getFieldString("SCH_ID"),"");
			Boolean isTemp  = (Boolean)ejbConn.trx(sMethod,
										new  Class[]{String.class},
										new Object[]{sSchId});
		}		
		
	}catch(DAOException daoe) {
            throw daoe;
    	}catch(Exception e) {
            throw new EJBServiceException(e);
    	}					
    }
    
    /**
	* 대차 출발시에 대차가 PUT위치로 잡힌 스케쥴을 취소한다.
	*(주작업에 대한 취소 작업 진행)
	* @param stackCol : 대차위치정보
	*/
    private void cancelCarLoadStateSchInfo2(String sYdGp,
    									String sTcCd) {
    	
    	try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
			
	    	/**
	         *	1.	해당 대차가 PUT위치로 잡힌 스케쥴을 검색한다.
	         */
	        List loadSchs	= ymCommonDAO.readLoadSlabOfSch2(sTcCd);
	        
	        /**
	         *	2.	스케쥴 취소 모듈 CALL
	         */
	        String sMethod 	= "";
	        String sSchId 		= "";
	        JDTORecord schJr 	= null;
		
	        sMethod = "cancelCoilSchInfo";
		
			EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
			
			for(int index = 0; index < loadSchs.size(); index++)
			{
				schJr  = (JDTORecord)loadSchs.get(index);
				sSchId = StringHelper.evl(schJr.getFieldString("SCH_ID"),"");
				Boolean isTemp  = (Boolean)ejbConn.trx(sMethod,
											new  Class[]{String.class},
											new Object[]{sSchId});
			}		
		
	}catch(DAOException daoe) {
            throw daoe;
    	}catch(Exception e) {
            throw new EJBServiceException(e);
    	}					
    }
	
	/**
	* 하차 후 출발지시를 실행한다.
     	* @param carInfo
	* @param stackCol
	* @param bedGp
	*/
    private void doUnLoadStartOfCar(JDTORecord carInfo, String stackCol, String bedGp) {
	
		logger.println(LogLevel.DEBUG, this, "doUnLoadStartOfCar() ### 하차 후 출발 지시.");
	        
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
		YdStockDAO ydStockDAO	= new YdStockDAO();
	        
		String sYdGp 	= stackCol.substring(0, 1);
		int maxQnty	= 0;
		
		/**
		 *	현재 대차에 적재되어 있는 수량이 0이면
		 *	즉,하차할 정보가 없으면
		 */
		if(isEndOfCarUnload(carInfo)) {
			/**
			 *	현재 대차의 상차스케쥴이 지정으로 셋팅되어있는지 체크
			 */
			if(isCarLoadGpY(carInfo)) {
				/**
		         *	현재 대차의 EQUIP 및 LAYER 정보를 설정한다.
		         *	-	CUR MAP INFO 를 CLOSE한다.
		         *	-	대차설정을 'L(상차)','M(이동중)'으로 셋팅한다.
		         *
		         *	-	B열연 SLAB일 경우 
		         *		상차동이 D동이면 D동 장입예정 SLAB 작업예약 생성
		         *		상차동이 E동이면 E동 장입예정 SLAB 작업예약 생성
		         *		현재는 무조건 대차작업에 대해서 장입예약을 생성한다.
		         *
		         *		수정 YJK : 대차 도착 처리 모듈에서 처리함.
		         */
				editCarUnloadState(carInfo);

//				if(YmCommonConst.YD_GP_3.equals(sYdGp))
//				/************************************************************************************************
//            	* 2007.1.2 이정훈 대차하차 시 상차동 Setting
//            	************************************************************************************************/
//				{  
//					// CGS
//					// Coil대차출하상차
//					String scheduleQuery   = "ym.steelinfo.steelinforecv.YdStockDAO.selectStockSchIDCount";
//					JDTORecord schWorkStat = ydStockDAO.getData(scheduleQuery, new Object[]{ YmCommonConst.NEW_SCH_WORK_KIND_CTFL, sYdGp   });
//
//	    			String tmpSchcodeCount      = StringHelper.evl(schWorkStat.getFieldString("SCHCODECOUNT"), "");
//
//	    			int schedulecount           = Integer.parseInt(tmpSchcodeCount);
//
//	    			logger.println(LogLevel.DEBUG,this,  " 스케줄테이블 스케줄 코드 갯수 = "+ schedulecount);
//
//	    			
//	    			if(YmCommonConst.YD_GP_3.equals(sYdGp) && schedulecount == 0) {
//	    				// CGS
//	    				// 대차 스케줄을 설정한다.
//	    				logger.println(LogLevel.DEBUG,this,  " 대차스케줄을설정:setCoilTcSchedule_03(carInfoRecord,"+stackCol+","+bedGp+")");
//	    				carInfo = setCoilTcSchedule_03(carInfo,stackCol,bedGp);
//	    			}
//				}
//				/*************************************************************************************************/
	           	            
				/**
		         *	현재 대차를 상차동으로 출발처리한다.
		         */
				// 최규성.2010-01-14
				// 신규대차의 출발지시는 L2에서 처리함.
//				if(getField(carInfo,"TC_NO").equals("3XTC03") ||
//						getField(carInfo,"TC_NO").equals("3XTC04") ||
//						getField(carInfo,"TC_NO").equals("3XTC05")){
//					logger.println(LogLevel.DEBUG, this, "신규대차: 대차를 상차동으로 출발처리 SKIP");
//				}else{
				sendStartOrder(carInfo, getField(carInfo, "CARLOAD_STOP_LOC"));
//				}  // if(신규대차조건)  
	            /**
		         *	현재 대차를 상차동에 도착처리를 한다.
		         */
				receiveTcInfo(carInfo,"U", stackCol, bedGp);

			}else {
				/**
		         *	현재 대차를 현재동에 대기시킨다.
		         */
				editCarIdleState(carInfo);
				/**
				 * CHECKME: TTLSMJ 2005. 10. 25.
				 * 1. 현재동에서 대차작업이 있으면 SCH 기동
				 * 2. 전체동에서 대차작업이 있으면 출발지시
				 */
				logger.println(LogLevel.DEBUG, this, "### 현재동/전체동 대차작업 검색: 미정의");
			}
			/**
			 *	현재 대차에 적재되어 있는 수량이 있으면
			 *	즉,하차할 정보가 있으면
			 */
		}else {
			logger.println(LogLevel.DEBUG, this, "### 하차: 저장품 하차중.");
		}
    }
	
	/**
	 * 상차동에 도착처리를 미리한다.
	 *
	 * @param JDTORecord : 대차정보
	 * @param String     : 	L => 상차 후 하차출발시 호출
	 *                     		U => 하차 후 상차출발시 호출
	 */
    private void receiveTcInfo(JDTORecord carInfo,
    						   String sGbn,
    						   String stackCol, 
    						   String bedGp)
    {  	
    	try
    	{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
			
			/*
			 ******************************************************************
			 *	선작업지시 처리여부 셋팅 체크
			 *	=> Tb_Ym_Equip Table 의 Pallet_No 항목을 가지고 체크한다.
			 *	1 : 선작업지시 실행
			 *	2 : 선작업지시 미실행
			 *
			 *	※	SLAB 에 대해서만 체크한다.
			 */
			
			String sYdGp = getField(carInfo, "STACK_COL_GP").substring(0, 1);
			 
			String sTmpGbn	= "";
			
			JDTORecord tempJr = new CraneSchDAO().getEquipInfoWithEquipGp(getField(carInfo, "EQUIP_GP"));
			if(tempJr != null){
				sTmpGbn	 = StringHelper.evl(tempJr.getFieldString("PALLET_NO"), "");
			}
			if(YmCommonConst.YD_GP_2.equals(sYdGp)&&"1".equals(sTmpGbn)){
				
				logger.println(LogLevel.DEBUG, this, "=COIL/SLAB 대차스케쥴==>선 도착처리 실행 ");
				
			}else if(YmCommonConst.YD_GP_2.equals(sYdGp)&&"2".equals(sTmpGbn)){
				
				logger.println(LogLevel.DEBUG, this, "=COIL/SLAB 대차스케쥴==>선 도착처리 미실행");
				return;
			}
		    	/*
			 ******************************************************************
			 */
			if("L".equals(sGbn))
			{
			    	
				if(YmCommonConst.YD_GP_2.equals(sYdGp)){
		            		
					carInfo = setSlabTcSchedule_01(carInfo,
					            				        stackCol,
					            				        bedGp);
			     
				}else if(YmCommonConst.YD_GP_3.equals(sYdGp)){
		            	
	            	     	/*
				    carInfo = setCoilTcSchedule_01(carInfo,
					            				   stackCol,
					            				   bedGp);
		            	*/
				}

		        String sTcNo	 		= getField(carInfo, "TC_NO");
				String sEquipGp		= getField(carInfo, "EQUIP_GP");
				String sCurrStopLoc 	= getField(carInfo, "CURR_STOP_LOC");
				String sCarLoadLoc 	= getField(carInfo, "CARLOAD_STOP_LOC");
				String sCarUnLoadLoc	= getField(carInfo, "CARUNLOAD_STOP_LOC");
				String sStackMaxQnty	= StringHelper.evl(carInfo.getFieldString("MAX_QNTY"), "0");
				
				logger.println(LogLevel.DEBUG, this, "=COIL/SLAB자동화==>미리 상차도착처리 ="+sCarLoadLoc);
				logger.println(LogLevel.DEBUG, this, "=COIL/SLAB자동화==>상차동 ="+sCarLoadLoc);
				logger.println(LogLevel.DEBUG, this, "=COIL/SLAB자동화==>하차동 ="+sCarUnLoadLoc);
		
				/*
				 *	중요함YJK : 현재 저장품의 하차동과 다음작업대상재의 상차동이 같을 경우 .
				 *	
				 *	=>	현 저장품의 하차작업을 먼저 수행해야 하기 때문에 미리 상차도착처리를 하지 않는다.
				 *	=>	현 저장품의 하차작업을 완료 후에 상차도착처리를 수행한다.		
		 		 */ 
				if(sCarUnLoadLoc.equals(sCarLoadLoc)){
					logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> 현저장품의 하차동과 다음작업대상재의 상차동이 같음.");
				 	logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> 미리 상차도착처리를 하지 않는다.");
					return;
				} 
				
				JDTORecord dataJr = JDTORecordFactory.getInstance().create();
				dataJr.setField("대차번호"	 ,sTcNo);
				dataJr.setField("대차도착동" ,sCarLoadLoc);
					
				if(YmCommonConst.YD_GP_2.equals(sYdGp)){	 
				   
					if(YmCommonConst.STACK_STAT_L.equals(sGbn)){
							
						/*
						 * 상차 후 하차출발시에 미리 상차스케쥴 처리
						 */
						
						EJBConnector ejbConn = new EJBConnector("default","JNDISCLdWrkOrdReg",this);
						Boolean isTemp  = (Boolean)ejbConn.trx("bookDummyWorkInfo_02",
																new  Class[]{JDTORecord.class},
																new Object[]{dataJr});
					}										
						
				}else if(YmCommonConst.YD_GP_3.equals(sYdGp)){	 
						
					if(YmCommonConst.STACK_STAT_L.equals(sGbn)){
							
						/*
						 * 상차 후 하차출발시에 미리 상차스케쥴 처리
						 */
						int iMaxCount = Integer.parseInt(sStackMaxQnty);
						if(iMaxCount == 1){
							EJBConnector ejbConn = new EJBConnector("default","JNDICCLdWrkOrdReg",this);
							Boolean isTemp  = (Boolean)ejbConn.trx("bookDummyWorkInfo_02",
																	new  Class[]{JDTORecord.class},
   																	new Object[]{dataJr});
						}
					}										
				}			
			}else if("U".equals(sGbn))											
			{

		       	if(YmCommonConst.YD_GP_2.equals(sYdGp)){
		            	
					
					carInfo = setSlabTcSchedule_02(carInfo,
					            				   	    stackCol,
					            				   	    bedGp);
			            	
				}else if(YmCommonConst.YD_GP_3.equals(sYdGp)){
		            	
        				/*
					carInfo = setCoilTcSchedule_02(carInfo,
					            				   	   stackCol,
					            				   	   bedGp);
					*/
				}

				String sTcNo	 		= getField(carInfo, "TC_NO");
				String sEquipGp		= getField(carInfo, "EQUIP_GP");
				String sCurrStopLoc 	= getField(carInfo, "CURR_STOP_LOC");
				String sCarLoadLoc 	= getField(carInfo, "CARLOAD_STOP_LOC");
				String sCarUnLoadLoc	= getField(carInfo, "CARUNLOAD_STOP_LOC");
				String sStackMaxQnty	= StringHelper.evl(carInfo.getFieldString("MAX_QNTY"), "0");
				
				logger.println(LogLevel.DEBUG, this, "=COIL/SLAB자동화==>상차동 ="+sCarLoadLoc);
				logger.println(LogLevel.DEBUG, this, "=COIL/SLAB자동화==>하차동 ="+sCarUnLoadLoc);
		
				/**
			         *	중요함YJK : 
			         *
			         *	=> 이 경우는 현 대차가 하차작업을 완료하고 다음작업대상재가 있는 상차동으로
			         *	   출발하려고 보니까 상차동이 현재동일 경우.
			         *	=> 이 경우는 미리 상차도착처리를 하지 않았기 때문에 여기서 상차도착처리를 한다.
			         *		   	
			         *	Case1 : 2ETC12 -> 2ETC12
			         *	Case2 : 2ETC12 -> 2ETC13
			         */
				JDTORecord dataJr = JDTORecordFactory.getInstance().create();
				dataJr.setField("대차번호"	,sTcNo);
				dataJr.setField("대차도착동" ,sCarLoadLoc);
					
				if(sCurrStopLoc.equals(sCarLoadLoc)){
			        	
			        	if(YmCommonConst.YD_GP_2.equals(sYdGp)){	 
						   
						EJBConnector ejbConn = new EJBConnector("default","JNDISCLdWrkOrdReg",this);
						Boolean isTemp  = (Boolean)ejbConn.trx("bookDummyWorkInfo_02",
																new  Class[]{JDTORecord.class},
																new Object[]{dataJr});
					}else if(YmCommonConst.YD_GP_3.equals(sYdGp)){	 
						
						EJBConnector ejbConn = new EJBConnector("default","JNDICCLdWrkOrdReg",this);
						Boolean isTemp  = (Boolean)ejbConn.trx("bookDummyWorkInfo_02",
																new  Class[]{JDTORecord.class},
																new Object[]{dataJr});
					}											
				}else{
			        	
					if(YmCommonConst.YD_GP_3.equals(sYdGp)){	 
			        		int iMaxCount = Integer.parseInt(sStackMaxQnty);
						if(iMaxCount > 1){
						EJBConnector ejbConn = new EJBConnector("default","JNDICCLdWrkOrdReg",this);
						Boolean isTemp  = (Boolean)ejbConn.trx("bookDummyWorkInfo_02",
																new  Class[]{JDTORecord.class},
																new Object[]{dataJr});
						}

					}
				}		
			}
    	}catch(DAOException daoe) {
	            throw daoe;
    	}catch(Exception e) {
	            throw new EJBServiceException(e);
    	}					
    }
    
    /**
     * 설비테이블의 설비 최대 '적재 최대 수량'과 적치대의 '적치 BED 수량 현재'가 같은지 리턴한다.
     * @param 	carInfo	대차정보
     * @return	true: NOT MAX, false: MAX
     */
    private boolean notMaxOfCarLoad(JDTORecord carInfo,String sTcNo) {
        
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false; 
		}
		
        boolean isSuccess = false;
        
        int maxQnty	= StringHelper.parseInt(carInfo.getFieldString("MAX_QNTY"), 0);
        int curQnty = StringHelper.parseInt(carInfo.getFieldString("CUR_QNTY"), -1);
        
        logger.println(LogLevel.DEBUG, this, "=COIL/SLAB자동화==>적재수량MAX ="+maxQnty);
		logger.println(LogLevel.DEBUG, this, "=COIL/SLAB자동화==>적재수량CUR ="+curQnty);
			
        if(maxQnty != curQnty){
        	isSuccess = true;
        }else{
        	isSuccess = false;
    	}
    	
        /**
         *	B열연 SLAB 연속작업 횟수를 체크
         *  연속작업횟수(CTS_RELAY_YN)와 현재작업횟수(CTS_RELAY_BAY)가 같으면 강제출발처리
         *	나머지 작업 스케쥴 정보는 취소처리한다.
         */
        
        if(isSuccess && //현재 MAX만큼 적재가 되지 않음.
           YmCommonConst.YD_GP_2.equals(sTcNo.substring(0, 1))){
        	
        	JDTORecord tJr = new CraneSchDAO().getEquipInfoWithEquipGp(sTcNo.substring(0,1) + "X"+ 
																	   sTcNo.substring(2,4) + "0"+ 
																	   sTcNo.substring(4,5)); 
					
			int iMaxCnt =  0;
			int iCurCnt = -1;
			
			if(tJr != null){
				iMaxCnt = StringHelper.parseInt(tJr.getFieldString("CTS_RELAY_YN"),   0);
				iCurCnt = StringHelper.parseInt(tJr.getFieldString("CTS_RELAY_BAY"), -1);
			}
			
			logger.println(LogLevel.DEBUG, this, "=SLAB자동화==>연속작업MAX ="+iMaxCnt);
			logger.println(LogLevel.DEBUG, this, "=SLAB자동화==>연속작업CUR ="+iCurCnt);
			
			/*
			 *	연속작업 횟수 만큼 작업 완료 => 강제 출발
			 */
			if(iMaxCnt == iCurCnt){
				
				isSuccess = false;
			}
		}
        
        return isSuccess;
    }

    /**
     * 스케쥴작업종류가 LINE OFF 작업인지 리턴한다.
     * @param 	carInfo	대차정보
     * @return	true: LINE OFF, false: NOT LINE OFF
     */
    private boolean isSchKindOfLineOff(JDTORecord carInfo) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
        String schKind = getField(carInfo, "CARLOAD_SCH_WORK_KIND");
        return YmCommonConst.NEW_SCH_WORK_KIND_CDLO.equals(schKind);
    }
    
    /**
     * 대차 상차출발지시 상태를 UPDATE
     * 1. 적치단 테이블의 '적치 단 활성 상태' UPDATE
     * 2. 설비 테이블의 '적재 상태', '작업진행 상태' UPDATE
     * 3. 
     * @param carInfo	대차정보
     */
    private void editCarLoadState(String curBay) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        List stocks 	= ymCommonDAO.readVicCarStock(curBay);
        int stocksCnt 	= stocks != null ? stocks.size() : 0; 
        if(stocksCnt > 0) {
            /**
             * 작업자가 대차 출발시킬 때 다음 작업의 스케쥴이 편성된 상태에서 출발을 시킬경우 해당 스케쥴 삭제
             */
            JDTORecord dto = null;
            String stockId = null;
            for(int i = 0; i < stocksCnt; i++) {
                dto = (JDTORecord)stocks.get(i);
                if(YmCommonConst.STACK_LAYER_STAT_P.equals(
                        getField(dto, "STACK_LAYER_STAT"))) {
                    stockId = getField(dto, "STOCK_ID");
                    dto = ymCommonDAO.readCancelSchStock(stockId);
                    callCancelSchdule(getField(dto, "SCH_ID"));
                }
            }
            ymCommonDAO.modifyStackAndProgStatOfEquip(
                    YmCommonConst.STACK_STAT_U, 
                    YmCommonConst.WPROG_STAT_T, 
                    YmCommonConst.EQUIP_GP_1XTC03);
        }else {
            ymCommonDAO.modifyStackAndProgStatOfEquip(
                    YmCommonConst.STACK_STAT_L, 
                    YmCommonConst.WPROG_STAT_M, 
                    YmCommonConst.EQUIP_GP_1XTC03);
        }
        editPossibleOfStacker(ymCommonDAO.readMaxOfCarMap(curBay));
        ymCommonDAO.modifyStockStatOfLayer(
                "",
                YmCommonConst.STACK_LAYER_ACTIVE_STAT_C,
                YmCommonConst.STACK_LAYER_STAT_E, 
                curBay);
    }

    /**
     * 스케쥴취소를 CALL 한다.
     * @param stockId
     */
    private void callCancelSchdule(String schId) {
        EJBConnector ejbConn = null;
    	try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
			
    	    ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
            ejbConn.trx("cancelCoilSchInfo", new Class[]{ String.class }, new Object[]{ schId });
        }catch(Exception e) {
            logger.println(LogLevel.DEBUG, this, "스케쥴 취소 ERROR");
            e.printStackTrace();
        }
    }

    /**
     * 대차 상차출발지시 상태를 UPDATE
     * 1. 적치단 테이블의 '적치 단 활성 상태' UPDATE
     * 2. 설비 테이블의 '적재 상태', '작업진행 상태' UPDATE
     * 3. 
     * @param carInfo	대차정보
     */
    private void editCarLoadState(String vicCarNo, String curBay) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        List stocks 	= ymCommonDAO.readVicCarStock(curBay);
        int stocksCnt 	= stocks != null ? stocks.size() : 0; 
        if(stocksCnt > 0) {
            ymCommonDAO.modifyStackAndProgStatOfEquip(
                    YmCommonConst.STACK_STAT_U, 
                    YmCommonConst.WPROG_STAT_T, 
                    vicCarNo);
            logger.println(LogLevel.DEBUG, this, "대차 상차출발지시 상태를 UPDATE: U,"+vicCarNo);
        }else {
            ymCommonDAO.modifyStackAndProgStatOfEquip(
                    YmCommonConst.STACK_STAT_L, 
                    YmCommonConst.WPROG_STAT_M, 
                    vicCarNo);
            logger.println(LogLevel.DEBUG, this, "대차 상차출발지시 상태를 UPDATE: L,"+vicCarNo);
        }
        editPossibleOfStacker(ymCommonDAO.readMaxOfCarMap(curBay));
        ymCommonDAO.modifyActiveStatOfLayer(YmCommonConst.STACK_LAYER_ACTIVE_STAT_C, curBay);
    }

    /**
     * 대차 상차출발지시 상태를 UPDATE
     * 1. 적치단 테이블의 '적치 단 활성 상태' UPDATE
     * 2. 설비 테이블의 '적재 상태', '작업진행 상태' UPDATE
     * 3. 
     * @param carInfo	대차정보
     */
    private void editCarLoadState(JDTORecord carInfo) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        editPossibleOfStacker(ymCommonDAO.readMaxOfCarMap(getField(carInfo, "STACK_COL_GP")));
        // CGS
        // StackLayer, equip 테이블의 데이터를 수정한다.
        editStatOfLayerAndEquip(carInfo, 
                YmCommonConst.STACK_LAYER_ACTIVE_STAT_C,// 비활성화
                YmCommonConst.STACK_STAT_U, 			// 
                YmCommonConst.WPROG_STAT_T);			// 출발지시
        // CGS
        // Slab에 대한 처리.코일과 상관없음.
        considerPCSend(carInfo);
    }

    /**
     * @param carInfo
     */
    // CGS
    // B 열연 Slab에 대한 처리.
    private void considerPCSend(JDTORecord carInfo) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        String col= getField(carInfo, "STACK_COL_GP");
        String yd = col.substring(0, 1);
        if(! YmCommonConst.YD_GP_2.equals(yd)) {
            return;
        }        
        String unloadBay = getField(carInfo, "CARUNLOAD_STOP_LOC");
        String schKind   = getField(carInfo, "CARUNLOAD_SCH_WORK_KIND");
        
        if("A".equals(unloadBay.substring(1, 2)) ||
           "B".equals(unloadBay.substring(1, 2)) ||
           "C".equals(unloadBay.substring(1, 2))) {
           	
           	if (schKind.equals(YmCommonConst.NEW_SCH_WORK_KIND_SWLI) ||
				schKind.equals(YmCommonConst.NEW_SCH_WORK_KIND_SCLI)){
				
			    List list = ymCommonDAO.readVicCarStock(col);
	            int listCnt = list != null ? list.size() : 0;
	            JDTORecord dto = null;
	            for(int i = 0; i <listCnt; i++) {            
	                dto = (JDTORecord)list.get(i);
	                sendPCStat(getField(dto, "STOCK_ID"), "20");                
	            }
	        }    
        }
    }

    /**
     * @param carInfo
     */
    private void editPossibleOfStacker(List cars) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        int carsCnt = cars != null ? cars.size() : 0;
        for(int i = 0; i < carsCnt; i++) {
            ymCommonDAO.modifyPossibleOfStacker(
                    getField((JDTORecord)cars.get(i), "STACK_COL_GP"),
                    getField((JDTORecord)cars.get(i), "STACK_BED_GP"));

        }
    }

    /**
     * 대차 하차가 완료되었는지 리턴한다.
     * @param 	carInfo	대차정보
     * @return	true: 완료, false: 미완료
     */
    private boolean isEndOfCarUnload(JDTORecord carInfo) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
        int curQnty = StringHelper.parseInt(carInfo.getFieldString("CUR_QNTY"), -1);
        return curQnty == 0 ? true : false;
    }

    /**
     * 상차지정구분이 되었는지 리턴한다.
     * @param carInfo	대차정보
     * @return
     */
    private boolean isCarLoadGpY(JDTORecord carInfo) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
        return YmCommonConst.CARLOAD_ASSIGN_GP_Y.equals(getField(carInfo, "CARLOAD_ASSIGN_YN"));
    }

    /**
     * 대차 하차출발지시 상태를 UPDATE
     * @param carInfo	대차정보
     */
    private void editCarUnloadState(JDTORecord carInfo) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
        editStatOfLayerAndEquip(
                carInfo, 
                YmCommonConst.STACK_LAYER_ACTIVE_STAT_C,
                YmCommonConst.STACK_STAT_L, 
                YmCommonConst.WPROG_STAT_M);
        //대차 도착 모듈에서 처리
        //considerVicCarLoad(carInfo);
    }
    
    /**
     * 대차 하차 후 출발시에 D/E동에 동간이적 대상을 고려한다.
     * @param carInfo	대차정보
     */
    private void considerVicCarLoad(JDTORecord carInfo) {
        logger.println(LogLevel.DEBUG, this, "### 하차 출발 동간보급 상차 체크.");
        
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        String sYdGp		= getField(carInfo, "STACK_COL_GP").substring(0, 1);
        String sCarLoadBay 	= getField(carInfo, "CARLOAD_STOP_LOC").substring(1, 2);
        
        //CARUNLOAD_SCH_WORK_KIND
        if(YmCommonConst.YD_GP_2.equals(sYdGp)) {
        	/*
        	 * 모든동 작업예약 처리
        	 */
        	considerCreateWBook(sCarLoadBay);
        	/*
        	    if(YmCommonConst.BAY_GP_D.equals(sCarLoadBay)) {
	                considerCreateWBook(YmCommonConst.BAY_GP_D);
	            }else if(YmCommonConst.BAY_GP_E.equals(sCarLoadBay)) {
	                considerCreateWBook(YmCommonConst.BAY_GP_E);
	            }
            */
        }
    }

    /**
     * 현재 보급 대상인 LOT NO를 작업예약한다.
     */
    private void considerCreateWBook(String sBay) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        JDTORecord dto = null;
        List zoinStocks = ymCommonDAO.readZoneInStockList(sBay);
        int zoinStocksCnt = zoinStocks != null ? zoinStocks.size() : 0;            
        if(zoinStocksCnt > 0) {
            for(int i = 0; i < zoinStocksCnt; i++) {
                dto = (JDTORecord)zoinStocks.get(i);
                createWBook(getField(dto, "STOCK_ID"), getField(dto, "STACK_COL_GP"));
                sendPCStat(getField(dto, "STOCK_ID"), "10");
            }
        }
    }
    
    /**
     * @param stockId
     */
    private void sendPCStat(String stockId, String stat) {
    	/*
    	 * 	2009.07 YJK 생산통제 장입진행정보 기존모듈 삭제.
    	 * 
	    	EJBConnector ejbConn = null;
	    	try {
	    	    ejbConn = new EJBConnector("default", "JNDIYardWrkResReg", this);
	            ZZPC001 model = new ZZPC001();
	            model.setTcCode(YmCommonConst.MODEL_YMPC100);
	            model.setTcDate(YmCommonUtil.getStringYMD("-"));
	            model.setTcTime(YmCommonUtil.getStringHMS("-"));
	            model.setrealStlNo(stockId);
	            model.setplanStlNo("");
	            model.seteventStat(stat);
	            model.seteventOccurDDTT("");
	            ejbConn.trx("sendInternalModel", 
	                    new Class[]{ CommonModel.class }, new Object[]{ model });
	        }catch (Exception e) {
	            e.printStackTrace();
	        }
	    *    
        */
    }

    /**
     * 대차 현재 위치에 작업이 없을경우 상태를 UPDATE
     * @param carInfo	대차정보
     */
    private void editCarIdleState(JDTORecord carInfo) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        editStatOfLayerAndEquip(
                carInfo, 
                YmCommonConst.STACK_LAYER_ACTIVE_STAT_O,
                YmCommonConst.STACK_STAT_L, 
                YmCommonConst.WPROG_STAT_W);        
    }

    /**
     * 설비/적치단의 상태정보를 수정한다.
     * @param ydEquipDAO
     * @param carInfo
     * @param activeStat
     * @param stackStat
     * @param progStat
     */
    private void editStatOfLayerAndEquip(
            JDTORecord carInfo, String activeStat, String stackStat, String progStat) {
    	logger.println(LogLevel.DEBUG, this, "설비/적치단의 상태정보를 수정한다.");
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
    	String equipGp 	= getField(carInfo, "EQUIP_GP");
        String stackCol = getField(carInfo, "STACK_COL_GP");
        ymCommonDAO.modifyActiveStatOfLayer(activeStat, stackCol);
        ymCommonDAO.modifyStackAndProgStatOfEquip(stackStat, progStat, equipGp);
        
    }

    /**
     * 대차 출발지시전문을 셋팅한다.
     * @param ydEquipDAO
     * @param carInfo
     * @param toLoc
     */
    private void sendStartOrder(JDTORecord carInfo, String toLoc) {
        /**
         * A열연
         * 전문코드	전문코드	CHAR	7
         * CRANE NO	CRANENO	CHAR	4
         * SPARE	SPARE1	CHAR	4
         * 일자		일자		CHAR	6
         * 시간		시간		CHAR	6
         * SPARE	SPARE2	CHAR	123
         * 
         * B열연
	     * 전문코드	TC		CHAR	07		
	     * 발생일자	Date	CHAR	10		YYYY-MM-DD
	     * 발생시간	Time	CHAR	08		HH-MM-SS
	     * 전문구분	Form	CHAR	01		I: Initialize, U: Update, D: Delete, R: Re-request
	     * 전문길이	Message_Length	CHAR	04		
	     * 대차번호	TCNo			CHAR	04		설비구분:TC(2)+TC NO(2)
	     * 대차 현재동	TCPosition		CHAR	06		YARD구분(1)+동구분(1)+SPAN(2)+열 NO(2)
	     * 대차 TO동	TCToPosition	CHAR	06		
         */
    	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        String tcName = null;
        String ydGp = getField(carInfo, "EQUIP_GP").substring(0, 1);
        if(YmCommonConst.YD_GP_2.equals(ydGp)) {
            tcName = YmCommonConst.TC_CM1BP04;	//B열연SLAB
        }else if(YmCommonConst.YD_GP_3.equals(ydGp)) {
            tcName = YmCommonConst.TC_CN1BP04;	//B열연COIL
        }

        Map tc = ymCommonDAO.readColumnLenOfTc(tcName);
        StringBuffer sendMsg = new StringBuffer();
        sendMsg.append(tcName);
        sendMsg.append(YmCommonUtil.getStringYMD("-"));
        sendMsg.append(YmCommonUtil.getStringHMS("-"));
        sendMsg.append(YmCommonConst.FORM_I);            
        sendMsg.append("00"+ (YmCommonUtil.getTotalLenOfTc(tc) - 30));        
        appendMsg(sendMsg, getField(carInfo, "TC_NO"), 			getFieldLen(tc, "대차번호"));        
        appendMsg(sendMsg, getField(carInfo, "CURR_STOP_LOC"), 	getFieldLen(tc, "대차현재동"));
        appendMsg(sendMsg, "", 									getFieldLen(tc, "SPARE"));
        appendMsg(sendMsg, toLoc,								getFieldLen(tc, "대차TO동"));
        
        /**
         *	중요함YJK : 현재동과 목적동이 같으면 전문송신 안함.
         *
         *	Case1 : 2ETC12 -> 2ETC12
         *	Case2 : 2ETC12 -> 2ETC13
         */
        String sCLoc = getField(carInfo, "CURR_STOP_LOC");
        String sULoc = toLoc;
        
        if(sCLoc.equals(sULoc)){
        	logger.println(LogLevel.DEBUG, this, "대차전문송신안함=>현재동="+sCLoc+"/목적동="+sULoc);
        }else{ 
	        logger.println(LogLevel.DEBUG, this, "대차전문송신  함=>현재동="+sCLoc+"/목적동="+sULoc);
	        sendQueue(tcName, sendMsg.toString());
	    }
        /**
         * HYSCO 대차 실적 송신을 고려한다.
         */
        considerSendHYSCO(getField(carInfo, "STACK_COL_GP"));
        /**
         * 출발실적 CALL
         */
        vicCarMoveResult(getField(carInfo, "CURR_STOP_LOC"));
    }

    /**
     * HYSCO 대차 실적을 송신한다.
     * @param col
     * 
     */
    private void considerSendHYSCO(String col) {
        if(! (YmCommonConst.YD_GP_3.equals(col.substring(0, 1)) &&
                "02".equals(col.substring(4, 6)))) {
            return;
        }
        EJBConnector ejbConn = null;
    	try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
			
    	    ejbConn = new EJBConnector("default","JNDICUpResReg",this);
            ejbConn.trx(
                    "callHyscoTcRtInfo",
                    new Class[]{ String.class },
                    new Object[]{ YmCommonConst.HYSCO_3XTC02 });
        }catch(Exception e) {
            e.printStackTrace();
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
    public List getListTransCarRoute(String queryID, String yd_gp){
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
	    	YdEquipDAO ydequipDAO = new YdEquipDAO();
	    	return ydequipDAO.getListTransCarRoute(queryID, yd_gp);	    	
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
	public int updateTransCarRoute(String queryID, List listData) throws EJBServiceException{   
    	try {   	
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
    		YdEquipDAO ydequipDAO = new YdEquipDAO();	
   			return ydequipDAO.updateTransCarRoute(queryID, listData);
    	}catch(DAOException daoe){
            throw daoe;
        }catch(Exception e){         
        	throw new EJBServiceException(e);
        }
    }
	
    /**
     * 보급인지 체크한다.
     * SPM2 보급 코드 추가.최규성.2010-01-13
     * @param schKind	스케쥴종류
     * @param equipGp	설비구분
     * @return
     */
    private boolean isSupply(String schKind, String equipGp) {
    	
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
        String bayGp = equipGp.substring(1, 2);
        if(YmCommonConst.BAY_GP_B.equals(bayGp)) {
            if(YmCommonConst.NEW_SCH_WORK_KIND_CFLI.equals(schKind)) {
                return true;
            }
        }else if(YmCommonConst.BAY_GP_D.equals(bayGp)) {
            if(YmCommonConst.NEW_SCH_WORK_KIND_CKLI.equals(schKind) || YmCommonConst.NEW_SCH_WORK_KIND_CNLI.equals(schKind) ) {
            	logger.println(LogLevel.DEBUG, this, "보급인지 검사" + schKind );
                return true;
            }            
        }
        return false;
    }

    /**
     * 적치단의 저장품 정보를 수정한다.
     * @param ydEquipDAO
     * @param readInfo
     * @param equipGp
     */
    private void editStockStatOfLayer(JDTORecord readInfo, String equipGp) { 
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        if(readInfo == null || readInfo.size() == 0) {
            return;
        }
        ymCommonDAO.modifyStockStatOfLayer(
                getField(readInfo, "CTS_COIL_NO"),
                YmCommonConst.STACK_LAYER_STAT_L,
                equipGp);
    }

    /**
     * 수신항목을 체크한다.
     * @param parseData
     * @param tc
     * @return
     */
    private void validRecDataOfCTSCoilInfoReq(JDTORecord parseData, Map tc) throws Exception {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
        String ctsName 		= getFieldNvl(parseData, "CTS 명");
        String coilLoadYN 	= getFieldNvl(parseData, "코일 상차 유무");
        if(ctsName.length() != getFieldLen(tc, "CTS 명")) {
            throw new Exception("수신항목 'CTS 명' Error: "+ ctsName);
        }else if(coilLoadYN.length() != getFieldLen(tc, "코일 상차 유무")) {
            throw new Exception("수신항목 '코일 상차 유무' Error: "+ coilLoadYN);
        }
    }

    /**
     * 송신 EJB를 이용하여 송신데이터를 송신한다.
     * @param methodName	TC명
     * @param sendMsg		송신데이터
     * @throws Exception
     */
    private void sendQueue(String methodName, String sendMsg) {
        EJBConnector ejbConn = null;
    	try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
			
    	    ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
            ejbConn.trx("send"+ methodName, new Class[]{ String.class }, new Object[]{ sendMsg });
        }catch(Exception e) {
            throw new EJBServiceException(e);
        }
    }
    
    /**
     * JDTORecord 가 가지는 name parameter에 대한 값을 리턴한다.
     * @param data
     * @param name
     * @return
     */
    private String getField(JDTORecord data, String name) {
    	if(data == null){
    		return "";	
    	}else{
       	return StringHelper.evl(data.getFieldString(name), "").trim();
    	}
    }

    /**
     * JDTORecord 가 가지는 name parameter에 대한 값을 리턴한다.
     * @param data
     * @param name
     * @return
     */
    private int getFieldInt(JDTORecord data, String name) {
        return StringHelper.parseInt(data.getFieldString(name), 0);
    }

    /**
     * JDTORecord 가 가지는 name parameter에 대한 값을 리턴한다.
     * @param data
     * @param name
     * @return
     */
    private float getfieldFloat(JDTORecord data, String name) {
        return StringHelper.parseFloat(data.getFieldString(name), 0);
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
    private void appendMsg(StringBuffer buffer, String field, int cnt) {
	    try{	
	    	if("".equals(field)) {
	            fillSpace(buffer, cnt);
	        }else if(CommonUtil.getLength(field) > cnt) {
	        	buffer.append(CommonUtil.substr(field, 0, cnt));
	        }else if(CommonUtil.getLength(field) < cnt) {
	            buffer.append(field);
	            fillSpace(buffer, cnt - CommonUtil.getLength(field));
	        }else {
	            buffer.append(field);
	        }
	    }catch(Exception e){}
    }
    
    /**
     * 0을 cnt 만큼 리턴한다.
     * @param cnt	공백 수
     * @return
     */
    private void appendMsgNum(StringBuffer buffer, String field, int cnt) {
	    try{    
	        if("".equals(field)) {
	            fillZeroSpace(buffer, cnt);
	        }else if(CommonUtil.getLength(field) > cnt) {
	            buffer.append(CommonUtil.substr(field, 0, cnt));
	        }else if(CommonUtil.getLength(field) < cnt) {
	            fillZeroSpace(buffer, cnt - CommonUtil.getLength(field));
	            buffer.append(field);
	        }else {
	            buffer.append(field);
	        }
	    }catch(Exception e){}
    }
    /**
     * 공백을 cnt 만큼 리턴한다.
     * @param cnt	공백 수
     * @return
     */
    private void fillSpace(StringBuffer buffer, int cnt) {
        for(int i = 0; i < cnt; i++) {
            buffer.append(" ");
        }
    }

    /**
     * 0을 cnt 만큼 리턴한다.
     * @param cnt	공백 수
     * @return
     */
    private void fillZeroSpace(StringBuffer buffer, int cnt) {
        for(int i = 0; i < cnt; i++) {
            buffer.append("0");
        }
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
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */    
    public List upCarRes(String queryID, List listData) throws EJBServiceException ,DAOException{
    	try{
    		
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
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
    
    /*
     * 	SLAB : YJK
     *		SLAB 대차스케쥴 반영 시작
     *		=> SLAB 대차 스케쥴 기준정보 셋팅
     */
    private JDTORecord setSlabTcSchedule_01(JDTORecord tcInfoJr, 		// 대차 TB_YM_EQUIP TABLE INFO
										String sStackColGp,  	// 대차 현재위치정보
										String sStackBedGp){ 	// 대차 현재번지정보
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		CraneSchDAO dao = new CraneSchDAO();
		
		String sYdGp 		= sStackColGp.substring(0, 1);
		String sEquipGp	= StringHelper.evl(tcInfoJr.getFieldString("EQUIP_GP"),"");     				//대차코드
		String sUYn 		= StringHelper.evl(tcInfoJr.getFieldString("CARLOAD_ASSIGN_YN"),"");     		//상차 지정 구분
		String sPYn 		= StringHelper.evl(tcInfoJr.getFieldString("CARUNLOAD_ASSIGN_YN"),"");  		//하차 지정 구분
		String sUSchCd 	= StringHelper.evl(tcInfoJr.getFieldString("CARLOAD_SCH_WORK_KIND"),"");	//상차 SCHEDULE 작업 종류
		String sPSchCd 	= StringHelper.evl(tcInfoJr.getFieldString("CARUNLOAD_SCH_WORK_KIND"),"");	//하차 SCHEDULE 작업 종류
		String sCurLoc 	= StringHelper.evl(tcInfoJr.getFieldString("CURR_STOP_LOC"),"");			//현재 정지 위치
		String sUpLoc 		= StringHelper.evl(tcInfoJr.getFieldString("CARLOAD_STOP_LOC"),"");		//상차 정지 위치
		String sPutLoc 	= StringHelper.evl(tcInfoJr.getFieldString("CARUNLOAD_STOP_LOC"),"");		//하차 정지 위치
			
		logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> 대차코드	="+sEquipGp);
		logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> sUYn 	="+sUYn);
		logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> sPYn 	="+sPYn);
		logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> sUSchCd 	="+sUSchCd);
		logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> sPSchCd 	="+sPSchCd);
		logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> sCurLoc 	="+sCurLoc);
		logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> sUpLoc 	="+sUpLoc);
		logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> sPutLoc	="+sPutLoc);
		
		/*  
		 *	동간보급상차(STSL) / W/B 보급(SWLI),CTC 보급(SCLI)
		 *	동간이적상차(STML,STM2) / 대차하차(STMU,STM4)
		 *  	일단 C동 W/B보급을 전제로 한다. 
		 */
	        int iGbn 		= 0;
	    	JDTORecord cJr	= null;
	    	
	    	if(YmCommonConst.NEW_SCH_WORK_KIND_STSL.equals(sUSchCd)){
	    		
	    		String sParamBay = "";
	    		
	    		if(YmCommonConst.EQUIP_GP_2XTC01.equals(sEquipGp)){
	    			sParamBay = YmCommonConst.BAY_GP_E;
	    		}else{
	    			sParamBay = YmCommonConst.BAY_GP_D;
	    		}
	    		/*
	    		 *	1.	상차스케쥴이 동간보급상차이면 상차동의 가장 빠른 장입대상재를 검색한다.		
	    		 *		-	대차01    이면 상차동 A,B,E동
	         	 *		-	대차02/03 이면 상차동 A,B,D동
	    		 */
	    		cJr	= dao.selectWBSlabSearch_06(sParamBay);	    		
	    		
	    		/*
	    		 *	2.	동간보급대상재가 없을 경우 동간이적상차로 스케쥴 코드 변경후 처리
	    		if(cJr == null){
	    			
	    			sUSchCd = YmCommonConst.NEW_SCH_WORK_KIND_STML;
	    			sPSchCd = YmCommonConst.NEW_SCH_WORK_KIND_STMU;
	    			
	    			logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> 동간보급 > 동간이적으로 스케쥴 코드 변경");
	    		}
	    		*/
	    		iGbn	= 1;						  
		}
	        
	        if(YmCommonConst.NEW_SCH_WORK_KIND_STML.equals(sUSchCd)||
	           YmCommonConst.NEW_SCH_WORK_KIND_STM2.equals(sUSchCd)){
				
			 /*
		         *	1.	작업예약ID가 가장빠른 동간이적상차작업이 있는 동을 검색한다.
		         *		작업예약을 검색할때 위치지정정보로 현 대차에 할당된 정보만을 검색한다.
		         */
			cJr	= dao.selectWBSlabSearch_07(sYdGp,
	         								  sUSchCd,
	         								  "2_TC"+sEquipGp.substring(5, 6)+"_");//대차설비번호	    		
	    		
	    		iGbn	= 2;
	        }
	        
	        if(cJr != null){
			
			String sBay1 = sUpLoc.substring(1 , 2);							// 현재 셋팅된 상차동
			String sBay2 = StringHelper.evl(cJr.getFieldString("BAY_GP"),"");   	// 앞으로 셋팅할 상차동
			
	    		logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> 셋팅된 상차동 ="+sBay1);
	    		logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> 셋팅할 상차동 ="+sBay2);
				
			String sTgUpLoc 	= "";
			String sTgUYn   	= "";
			String sTgUSchCd 	= "";
			String sTgPutLoc 	= "";
			String sTgPYn 	= "";
			String sTgPSchCd 	= "";
			
			/*
			 *	상차동 위치
			 *
			 *	1번 대차 E동 Default 위치 3번열
			 *	2번 대차 D동 Default 위치 3번열
			 *	3번 대차 D동 Default 위치 2번열
			 *
			 */
			if(iGbn == 1){
				
				String sDcLoc 	= YmCommonConst.LOCATION_2;
				String sDcCd 	= sEquipGp.substring(5, 6);
				
				if(YmCommonConst.LOCATION_1.equals(sDcCd)&&
				   YmCommonConst.BAY_GP_E.equals(sBay2)){
					sDcLoc = YmCommonConst.LOCATION_3;
				}else if(YmCommonConst.LOCATION_2.equals(sDcCd)&&
				    	 YmCommonConst.BAY_GP_D.equals(sBay2)){
					sDcLoc = YmCommonConst.LOCATION_3;
				}
				
				logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> 셋팅할 상차동위치 ="+sDcLoc);
				
				//동간보급
				sTgUpLoc = YmCommonConst.YD_GP_2 + 
			  			   sBay2 + 
			  			   YmCommonConst.EQUIP_KIND_TC + 
			  			   sDcCd + 
			  			   sDcLoc;
			}else{
				//동간이적
				sTgUpLoc = StringHelper.evl(cJr.getFieldString("CRANE_WORD_PUT_LOC"),"");
			}	
				
			/*
			 *	상차동 지정 구분
			 */
			if(iGbn == 1){
				//동간보급
				sTgUYn = sUYn;
			}else{
				//동간이적
				sTgUYn = sUYn;
			}	 
			
			/*
			 *	상차동 스케쥴코드
			 */
			if(iGbn == 1){
				//동간보급
				sTgUSchCd = sUSchCd;
			}else{
				//동간이적
				sTgUSchCd = sUSchCd;
			}	 
			
			/*
			 *	하차동 위치
			 *
			 *	1번 대차 E동 Default 위치 3번열
			 *	2번 대차 D동 Default 위치 3번열
			 *	3번 대차 D동 Default 위치 2번열
			 */
			if(iGbn == 1){
				//동간보급
				sTgPutLoc = sPutLoc;
			}else{
				
				String sDcLoc 	= YmCommonConst.LOCATION_2;
				String sDcCd 	= sEquipGp.substring(5, 6);
				String sDcBay   = StringHelper.evl(cJr.getFieldString("CARUNLOAD_PUT_LOC"),"");
				
				if(YmCommonConst.LOCATION_1.equals(sDcCd)&&
				   YmCommonConst.BAY_GP_E.equals(sDcBay)){
					sDcLoc = YmCommonConst.LOCATION_3;
				}else if(YmCommonConst.LOCATION_2.equals(sDcCd)&&
				    	 YmCommonConst.BAY_GP_D.equals(sDcBay)){
					sDcLoc = YmCommonConst.LOCATION_3;
				}
				
				logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> 셋팅할 하차동위치 ="+sDcLoc);
				
				//동간이적 
				sTgPutLoc 	 = YmCommonConst.YD_GP_2 + 			//야드구분
							   sDcBay + 						//저장품TABLE 목적동
							   YmCommonConst.EQUIP_KIND_TC + 	//TC코드	
							   sDcCd + 							//대차설비번호
							   sDcLoc;							//위치번호	
			}	 
			 	
			/*
			 *	하차동 지정 구분
			 */
			if(iGbn == 1){
				//동간보급
				sTgPYn = sPYn;
			}else{
				//동간이적
				sTgPYn = sPYn;
			}	 
			
			/*
			 *	하차동 스케쥴코드
			 */
			if(iGbn == 1){
				//동간보급
				sTgPSchCd = sPSchCd;
			}else{
				//동간이적
				sTgPSchCd = sPSchCd;
			}	
								
			logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> sTgUpLoc 	="+sTgUpLoc);
			logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> sTgUYn 		="+sTgUYn);
			logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> sTgUSchCd 	="+sTgUSchCd);
			logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> sTgPutLoc 	="+sTgPutLoc);
			logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> sTgPYn 		="+sTgPYn);
			logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> sTgPSchCd 	="+sTgPSchCd);
			/*
		         *	3.	작업대상재가 있는 동으로 대차설정정보를 변경한다.
		         *		하차동관련정보는 대기항목에 셋팅한다.
		         *		하차후 상차출발시점에 대기항목정보를 하차항목정보에 셋팅한다.
		         *
		         *		현재동 		:  	CURR_STOP_LOC
		         *		상차동 		:	CARLOAD_STOP_LOC
		         *		상차지정유무	:	CARLOAD_ASSIGN_YN
		         *		상차스케쥴	:	CARLOAD_SCH_WORK_KIND
		         *		하차동		:	WAIT_STOP_LOC(*)
		         *		하차스케쥴	: 	WBOOK_ID(*)
		         *		하차지정유무	:	CARUNLOAD_ASSIGN_YN
		         */ 
			int iSeq = dao.updateEquipSchInfo_02(  sEquipGp,
		                                             			sCurLoc,
											sTgUpLoc,	
											sTgUYn,
											sTgUSchCd,
											sTgPutLoc,
											sTgPYn,
											sTgPSchCd);
		
			tcInfoJr = ymCommonDAO.readVicCarInfo(sYdGp, 
										 	   sStackColGp, 
											   sStackBedGp);														   
		}	
			
		/*
		 *	Q2 : 동간이적을 작업예약ID순으로 처리할때의 문제점.
		 *		- 이동 저동 왔다갔다 처리됨
		 *		- 특정동의 작업을 먼저 처리하려고 할때 안됨
		 */
		/*
		 *	Q3 : SLAB 대차도착 처리시 스케쥴이 동간이적이면
		 *		 대차코드와 일치하는 작업예약에 대해서만 처리한다.
		 *		- A,B,C동 일 경우 같은 동간이적 스케쥴코드로 
		 *		  각가 다른 대차로 상차하는 작업예약이 설정된다.
		 */ 
		/*
		 *	Q5 : SLAB 동간이적 권상작업 후에 이후의 
		 *		 동간이적작업에 대해 스케쥴 호출하는 부분을 제외함.
		 *		- 이후의 작업은 대차가 출발시점에 처리한다.
		 */  
		/*
		 *	Q4 : 현재 저장품의 하차동과 다음작업대상재의 상차동이 같을 경우 .
		 *		- 상단에 '중요함YJK' 참조
		 *
		 */ 
		
		return tcInfoJr;
	}
	
	
      /*
       *	SLAB : YJK
	*	SLAB 대차스케쥴 반영 시작
	*/
	private JDTORecord setSlabTcSchedule_02(JDTORecord tcInfoJr, 	// 대차 TB_YM_EQUIP TABLE INFO
										String sStackColGp,  	// 대차 현재위치정보
										String sStackBedGp){ 	// 대차 현재번지정보
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		CraneSchDAO dao = new CraneSchDAO();
		
		String sYdGp 		= sStackColGp.substring(0, 1);	
		String sEquipGp	= StringHelper.evl(tcInfoJr.getFieldString("EQUIP_GP"),""); //대차코드
	    	
	    	 /*
	         *	1.	작업대상재가 있는 동으로 대차설정정보를 변경한다.
	         *		하차동관련정보는 대기항목에 셋팅한다.
	         *		하차후 상차출발시점에 대기항목정보를 하차항목정보에 셋팅한다.
	         *
	         *		하차동		:	WAIT_STOP_LOC 	=> CARUNLOAD_STOP_LOC
	         *		하차스케쥴	: 	WBOOK_ID	  	=> CARUNLOAD_SCH_WORK_KIND
	         */ 
		int iSeq = dao.updateEquipSchInfo_03(sEquipGp);
			
		tcInfoJr = ymCommonDAO.readVicCarInfo(sYdGp, 
									 	   sStackColGp, 
										   sStackBedGp);														   
    
		return tcInfoJr;
	}
    
    /*
     *	COIL 대차스케쥴 반영 시작
     */
//    private JDTORecord setCoilTcSchedule_01(JDTORecord tcInfoJr, // 대차 TB_YM_EQUIP TABLE INFO
//										    String sStackColGp,  // 대차 현재위치정보
//										    String sStackBedGp){ // 대차 현재번지정보
//		
//		CraneSchDAO dao = new CraneSchDAO();
//		
//		String sYdGp 	= sStackColGp.substring(0, 1);
//    	String sEquipGp	= StringHelper.evl(tcInfoJr.getFieldString("EQUIP_GP"),"");     			//대차코드
//    	String sUYn 	= StringHelper.evl(tcInfoJr.getFieldString("CARLOAD_ASSIGN_YN"),"");     	//상차 지정 구분
//    	String sPYn 	= StringHelper.evl(tcInfoJr.getFieldString("CARUNLOAD_ASSIGN_YN"),"");  	//하차 지정 구분
//    	String sUSchCd 	= StringHelper.evl(tcInfoJr.getFieldString("CARLOAD_SCH_WORK_KIND"),"");	//상차 SCHEDULE 작업 종류
//    	String sPSchCd 	= StringHelper.evl(tcInfoJr.getFieldString("CARUNLOAD_SCH_WORK_KIND"),"");	//하차 SCHEDULE 작업 종류
//    	String sCurLoc 	= StringHelper.evl(tcInfoJr.getFieldString("CURR_STOP_LOC"),"");			//현재 정지 위치
//    	String sUpLoc 	= StringHelper.evl(tcInfoJr.getFieldString("CARLOAD_STOP_LOC"),"");			//상차 정지 위치
//    	String sPutLoc 	= StringHelper.evl(tcInfoJr.getFieldString("CARUNLOAD_STOP_LOC"),"");		//하차 정지 위치
//		
//		logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> 대차코드="+sEquipGp);
//		logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> sUYn 	="+sUYn);
//		logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> sPYn 	="+sPYn);
//		logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> sUSchCd ="+sUSchCd);
//		logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> sPSchCd ="+sPSchCd);
//		logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> sCurLoc ="+sCurLoc);
//		logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> sUpLoc 	="+sUpLoc);
//		logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> sPutLoc ="+sPutLoc);
//		
//		int iGbn 		= 0;
//    	JDTORecord cJr	= null;
//    	
//		/*
//		 *
//		 *	3XTC01
//		 *		1순위 : CTML - Coil 동간이적상차(L)
//		 *		2순위 : CFLO - Coil HFL 추출
//		 *				
//		 *	3XTC02		
//		 *		1순위 : CTFL - Coil 대차출하상차
//		 *		2순위 : CTM2 - Coil 동간이적상차(R)
//		 *		3순위 : CKLO - Coil SPM 추출
//		 */
//		{ 
//	    	if("3XTC01".equals(sEquipGp)){
//	    	
//	    		cJr = dao.selectTCCoilSearch_01(sYdGp,YmCommonConst.NEW_SCH_WORK_KIND_CTML);
//	    		
//	    		if(false&&cJr == null){
//	    			
//	    			cJr = dao.selectTCCoilSearch_01(sYdGp,YmCommonConst.NEW_SCH_WORK_KIND_CFLO);
//	    		}
//	    		
//	    	}else
//	    	 if("3XTC02".equals(sEquipGp)){
//	    		
//	    		cJr = dao.selectTCCoilSearch_01(sYdGp, YmCommonConst.NEW_SCH_WORK_KIND_CTFL);
//	    		if(cJr == null){
//	    			
//	    			cJr = dao.selectTCCoilSearch_01(sYdGp,
//	    											YmCommonConst.NEW_SCH_WORK_KIND_CTM2);
//					if(false&&cJr == null){
//	    			
//		    			cJr = dao.selectTCCoilSearch_01(sYdGp,
//		    											YmCommonConst.NEW_SCH_WORK_KIND_CKLO);
//		    		}    											
//	    		}
//	    	}
//	    }
//	    
//        if(cJr != null){
//		
//			String cStockId = StringHelper.evl(cJr.getFieldString("STOCK_ID"),"");   
//			String cWbookId = StringHelper.evl(cJr.getFieldString("WBOOK_ID"),"");   
//			String cPutBay  = StringHelper.evl(cJr.getFieldString("CARUNLOAD_PUT_LOC"),"");   
//			String cCurBay  = StringHelper.evl(cJr.getFieldString("BAY_GP"),"");   
//			String cSchCd   = StringHelper.evl(cJr.getFieldString("SCH_WORK_KIND"),"");   
//				   	
//			logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> 작업예약정보 저장품	="+cStockId);
//			logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> 작업예약정보 ID		="+cWbookId);
//			logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> 작업예약정보 현재동	="+cCurBay);
//			logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> 작업예약정보 목적동	="+cPutBay);
//			logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> 작업예약정보 스케쥴	="+cSchCd);
//			
//			String sBay1 = sUpLoc.substring(1 , 2);	// 현재 셋팅된 상차동
//			String sBay2 = cCurBay;   				// 앞으로 셋팅할 상차동
//			
//    		logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> 셋팅된 상차동 ="+sBay1);
//    		logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> 셋팅할 상차동 ="+sBay2);
//			
//			String sTgUpLoc 	= "";
//			String sTgUYn   	= "";
//			String sTgUSchCd 	= "";
//			String sTgPutLoc 	= "";
//			String sTgPYn 		= "";
//			String sTgPSchCd 	= "";
//			
//			/*
//			 *	상차동 위치
//			 */
//			{
//				sTgUpLoc = YmCommonConst.YD_GP_3 + 
//			  			   sBay2 + 
//			  			   sEquipGp.substring(2);
//			}	
//			
//			/*
//			 *	상차동 지정 구분
//			 */
//			{
//				sTgUYn = sUYn;
//			}
//			
//			/*
//			 *	상차동 스케쥴코드
//			 */
//			{
//				sTgUSchCd = cSchCd;
//			}	 
//			
//			/*
//			 *	하차동 위치
//			 */
//			{	
//				//	01.	목적동이 6자리 이상일 경우
//				cPutBay	= cPutBay.length()> 2 ? cPutBay.substring(1,2) : cPutBay;
//				
//				//	02.	상차스케쥴이 대차출하상차이면 목적동은 무조건 H
//				if(YmCommonConst.NEW_SCH_WORK_KIND_CTFL.equals(cSchCd)){
//					cPutBay	= YmCommonConst.BAY_GP_H;
//				}
//				
//				//	03.	상차스케쥴이 HFL 추출이면 목적동은 HFL설비에 목적동
//				if(YmCommonConst.NEW_SCH_WORK_KIND_CFLO.equals(cSchCd)){
//					
//					JDTORecord tJr = dao.getEquipInfoWithEquipGp(YmCommonConst.STACK_COL_GP_3CFD01);
//					if(tJr != null){
//						cPutBay	 = StringHelper.evl(tJr.getFieldString("CARUNLOAD_BAY"), "");
//					}
//				}
//				
//				//	04.	상차스케쥴이 SPM 추출이면 목적동은 SPM설비에 목적동
//				if(YmCommonConst.NEW_SCH_WORK_KIND_CKLO.equals(cSchCd)){
//					
//					JDTORecord tJr = dao.getEquipInfoWithEquipGp(YmCommonConst.STACK_COL_GP_3AKD01);
//					if(tJr != null){
//						cPutBay	 = StringHelper.evl(tJr.getFieldString("CARUNLOAD_BAY"), "");
//					}
//				}
//					
//				sTgPutLoc = YmCommonConst.YD_GP_3 + 
//			  			    cPutBay + 
//			  			    sEquipGp.substring(2);
//			}	 
//			 	
//			/*
//			 *	하차동 지정 구분
//			 */
//			{
//				sTgPYn = sPYn;
//			}	 
//			
//			/*
//			 *	하차동 스케쥴코드
//			 */
//			if("3XTC01".equals(sEquipGp)){
//	    		sTgPSchCd = YmCommonConst.NEW_SCH_WORK_KIND_CTMU; //Coil 대차하차(L)
//	    	}else if("3XTC02".equals(sEquipGp)){
//	    		sTgPSchCd = YmCommonConst.NEW_SCH_WORK_KIND_CTM4; //Coil 대차하차(R)	
//	    	}
//			
//			logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> sTgUpLoc 	="+sTgUpLoc);
//			logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> sTgUYn 		="+sTgUYn);
//			logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> sTgUSchCd 	="+sTgUSchCd);
//			logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> sTgPutLoc 	="+sTgPutLoc);
//			logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> sTgPYn 		="+sTgPYn);
//			logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> sTgPSchCd 	="+sTgPSchCd);
//			
//			if(sTgUpLoc.equals(sTgPutLoc)){
//				logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> 에러 = 상/하차동이 같음.");
//				return tcInfoJr;
//			}
//			
//			/*
//	         *	3.	작업대상재가 있는 동으로 대차설정정보를 변경한다.
//	         *		하차동관련정보는 대기항목에 셋팅한다.
//	         *		하차후 상차출발시점에 대기항목정보를 하차항목정보에 셋팅한다.
//	         *
//	         *		현재동 		:  	CURR_STOP_LOC
//	         *		상차동 		:	CARLOAD_STOP_LOC
//	         *		상차지정유무:	CARLOAD_ASSIGN_YN
//	         *		상차스케쥴	:	CARLOAD_SCH_WORK_KIND
//	         *		하차동		:	WAIT_STOP_LOC(*)
//	         *		하차스케쥴	: 	WBOOK_ID(*)
//	         *		하차지정유무:	CARUNLOAD_ASSIGN_YN
//	         */ 
//	         int iSeq = dao.updateEquipSchInfo_02(sEquipGp,
//	                                              sCurLoc,
//												  sTgUpLoc,	
//												  sTgUYn,
//												  sTgUSchCd,
//												  sTgPutLoc,
//												  sTgPYn,
//												  sTgPSchCd);
//			 
//			 tcInfoJr = ymCommonDAO.readVicCarInfo(sYdGp, 
//											 	   sStackColGp, 
//												   sStackBedGp);														   
//		}	
//		
//        return tcInfoJr;
//    }
	
	
	/*
     *	COIL 대차스케쥴 반영 시작
     */
    private JDTORecord setCoilTcSchedule_02(JDTORecord tcInfoJr, // 대차 TB_YM_EQUIP TABLE INFO
										    String sStackColGp,  // 대차 현재위치정보
										    String sStackBedGp){ // 대차 현재번지정보
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		CraneSchDAO dao = new CraneSchDAO();
		
		String sYdGp 	= sStackColGp.substring(0, 1);	
    	String sEquipGp	= StringHelper.evl(tcInfoJr.getFieldString("EQUIP_GP"),""); //대차코드
    	/*
         *	1.	작업대상재가 있는 동으로 대차설정정보를 변경한다.
         *		하차동관련정보는 대기항목에 셋팅한다.
         *		하차후 상차출발시점에 대기항목정보를 하차항목정보에 셋팅한다.
         *
         *		하차동		:	WAIT_STOP_LOC => CARUNLOAD_STOP_LOC
         *		하차스케쥴	: 	WBOOK_ID	  => CARUNLOAD_SCH_WORK_KIND
         */ 
         int iSeq = dao.updateEquipSchInfo_03(sEquipGp);
		
		 tcInfoJr = ymCommonDAO.readVicCarInfo(sYdGp, 
										 	   sStackColGp, 
											   sStackBedGp);														   
    
        return tcInfoJr;
    }

    /*
     *	COIL 대차스케쥴 반영 시작
     */
//    private JDTORecord setCoilTcSchedule_03(JDTORecord tcInfoJr, // 대차 TB_YM_EQUIP TABLE INFO
//										    String sStackColGp,  // 대차 현재위치정보
//										    String sStackBedGp){ // 대차 현재번지정보
//		
//		CraneSchDAO dao = new CraneSchDAO();
//		
//		String sYdGp 	= sStackColGp.substring(0, 1);
//    	String sEquipGp	= StringHelper.evl(tcInfoJr.getFieldString("EQUIP_GP"),"");     			//대차코드
//    	String sUYn 	= StringHelper.evl(tcInfoJr.getFieldString("CARLOAD_ASSIGN_YN"),"");     	//상차 지정 구분
//    	String sPYn 	= StringHelper.evl(tcInfoJr.getFieldString("CARUNLOAD_ASSIGN_YN"),"");  	//하차 지정 구분
//    	String sUSchCd 	= StringHelper.evl(tcInfoJr.getFieldString("CARLOAD_SCH_WORK_KIND"),"");	//상차 SCHEDULE 작업 종류
//    	String sPSchCd 	= StringHelper.evl(tcInfoJr.getFieldString("CARUNLOAD_SCH_WORK_KIND"),"");	//하차 SCHEDULE 작업 종류
//    	String sCurLoc 	= StringHelper.evl(tcInfoJr.getFieldString("CURR_STOP_LOC"),"");			//현재 정지 위치
//    	String sUpLoc 	= StringHelper.evl(tcInfoJr.getFieldString("CARLOAD_STOP_LOC"),"");			//상차 정지 위치
//    	String sPutLoc 	= StringHelper.evl(tcInfoJr.getFieldString("CARUNLOAD_STOP_LOC"),"");		//하차 정지 위치
//    	String sAutoYn 	= StringHelper.evl(tcInfoJr.getFieldString("AUTO_YN"),"");					//Y:자동으로 다음 작업예약을 우선순위에 따라 찾는다.
//    	
//		
//    	String sTgUpLoc 	= "";
//		String sTgUYn   	= "";
//		String sTgUSchCd 	= "";
//		String sTgPutLoc 	= "";
//		String sTgPYn 		= "";
//		String sTgPSchCd 	= "";
//		
//		logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> 대차코드="+sEquipGp);
//		logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> sUYn 	="+sUYn);
//		logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> sPYn 	="+sPYn);
//		logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> sUSchCd ="+sUSchCd);
//		logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> sPSchCd ="+sPSchCd);
//		logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> sCurLoc ="+sCurLoc);
//		logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> sUpLoc 	="+sUpLoc);
//		logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> sPutLoc ="+sPutLoc);
//		logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> sAutoYn ="+sAutoYn);
//		
//		int iGbn 		= 0;
//    	JDTORecord cJr	= null;
//		/*
//		 *	3XTC01
//		 *		1순위 : CTML - Coil 동간이적상차(L)
//		 *		2순위 : CFLO - Coil HFL 추출
//		 *				
//		 *	3XTC02		
//		 *		1순위 : CTFL - Coil 대차출하상차
//		 *		2순위 : CTM2 - Coil 동간이적상차(R)
//		 *		3순위 : CKLO - Coil SPM 추출
//		 *
//		 *  3XTC03
//		 *		1순위 : CTM5 - Coil 동간이적상차(N1)
//		 *		2순위 : CKLO - Coil SPM 추출
//		 *		3순위 :
//		 *
//		 *  3XTC04
//		 *  	1순위 : CTM6 - Coil 동간이적상차(N2)
//		 *  	2순위 : CFLO - Coil HFL 추출
//		 *  	3순위 :
//		 *  
//		 *  3XTC05
//		 *		1순위 : CTM7 - Coil 동간이적상차(N3)
//		 *		2순위 : CNLO - Coil SPM2 추출
//		 *		3순위 :
//		 *
//		 */
//		{ 
//			
//			cJr = dao.selectTCCoilSearch(sYdGp, sUSchCd,sUpLoc);
//			//AUTO 조건이 Y 이거나 상차스케줄 코드가 존재 안 하는 경우 기존 스케줄 편성 로직을 탄다 (정종균)
//			if(sAutoYn.equals("Y")|| cJr == null ){			
//				//-----------------------------------------------------------------------------------
//				logger.println(LogLevel.DEBUG, this, "AUTO 조건이 Y 이거나 상차스케줄 코드가 존재 안 하는 경우 : "+sEquipGp);
//		    	if("3XTC01".equals(sEquipGp)){
//		    	
//		    		cJr = dao.selectTCCoilSearch_01(sYdGp,YmCommonConst.NEW_SCH_WORK_KIND_CTML);
//		    		
//		    		if(false&&cJr == null){
//		    			
//		    			cJr = dao.selectTCCoilSearch_01(sYdGp,YmCommonConst.NEW_SCH_WORK_KIND_CFLO);
//		    		}
//		    		
//		    	}else
//		    	 if("3XTC02".equals(sEquipGp)){
//		    		
//		    		cJr = dao.selectTCCoilSearch_01(sYdGp, YmCommonConst.NEW_SCH_WORK_KIND_CTFL);
//		    		if(cJr == null){
//		    			
//		    			cJr = dao.selectTCCoilSearch_01(sYdGp,
//		    											YmCommonConst.NEW_SCH_WORK_KIND_CTM2);
//						if(false&&cJr == null){
//		    			
//			    			cJr = dao.selectTCCoilSearch_01(sYdGp,
//			    											YmCommonConst.NEW_SCH_WORK_KIND_CKLO);
//			    		}    											
//		    		}
//		    	}else if("3XTC03".equals(sEquipGp)){
//		    		logger.println(LogLevel.DEBUG, this, "신규대차1 경우 : "+sEquipGp);
//		    		cJr = dao.selectTCCoilSearch_01(sYdGp,YmCommonConst.NEW_SCH_WORK_KIND_CTM5);
//		    		
//		    		if(false&&cJr == null){
//		    			
//		    			cJr = dao.selectTCCoilSearch_01(sYdGp,YmCommonConst.NEW_SCH_WORK_KIND_CKLO);
//		    		}
//		    		
//		    	}else if("3XTC04".equals(sEquipGp)){
//		    		logger.println(LogLevel.DEBUG, this, "신규대차2 경우 : "+sEquipGp);
//		    		cJr = dao.selectTCCoilSearch_01(sYdGp,YmCommonConst.NEW_SCH_WORK_KIND_CTM6);
//		    		
//		    		if(false&&cJr == null){
//		    			
//		    			cJr = dao.selectTCCoilSearch_01(sYdGp,YmCommonConst.NEW_SCH_WORK_KIND_CFLO);
//		    		}
//
//		    		
//		    	}else if("3XTC05".equals(sEquipGp)){
//		    		logger.println(LogLevel.DEBUG, this, "신규대차3 경우 : "+sEquipGp);
//		    		cJr = dao.selectTCCoilSearch_01(sYdGp,YmCommonConst.NEW_SCH_WORK_KIND_CTM7);
//		    		
//		    		if(false&&cJr == null){
//		    			
//		    			cJr = dao.selectTCCoilSearch_01(sYdGp,YmCommonConst.NEW_SCH_WORK_KIND_CNLO);
//		    		}
//
//		    		
//		    	}
//
//				//-----------------------------------------------------------------------------------
//			}
//	    }
//	    
//        if(cJr != null){
//		
//			String cStockId = StringHelper.evl(cJr.getFieldString("STOCK_ID"),"");   
//			String cWbookId = StringHelper.evl(cJr.getFieldString("WBOOK_ID"),"");   
//			String cPutBay  = StringHelper.evl(cJr.getFieldString("CARUNLOAD_PUT_LOC"),"");   
//			String cCurBay  = StringHelper.evl(cJr.getFieldString("BAY_GP"),"");   
//			String cSchCd   = StringHelper.evl(cJr.getFieldString("SCH_WORK_KIND"),"");   
//				   	
//			logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> 작업예약정보 저장품	="+cStockId);
//			logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> 작업예약정보 ID		="+cWbookId);
//			logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> 작업예약정보 현재동	="+cCurBay);
//			logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> 작업예약정보 목적동	="+cPutBay);
//			logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> 작업예약정보 스케쥴	="+cSchCd);
//			
//			String sBay1 = sUpLoc.substring(1 , 2);	// 현재 셋팅된 상차동
//			String sBay2 = cCurBay;   				// 앞으로 셋팅할 상차동
//			
//    		logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> 셋팅된 상차동 ="+sBay1);
//    		logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> 셋팅할 상차동 ="+sBay2);
//		
//			/*
//			 *	상차동 위치
//			 */
//			{
//				sTgUpLoc = YmCommonConst.YD_GP_3 + 
//			  			   sBay2 + 
//			  			   sEquipGp.substring(2);
//			}	
//			
//			/*
//			 *	상차동 지정 구분
//			 */
//			{
//				sTgUYn = sUYn;
//			}
//			
//			/*
//			 *	상차동 스케쥴코드
//			 */
//			{
//				sTgUSchCd = cSchCd;
//			}	 
//			
//			/*
//			 *	하차동 위치
//			 */
//			{	
//				//	01.	목적동이 6자리 이상일 경우
//				cPutBay	= cPutBay.length()> 2 ? cPutBay.substring(1,2) : cPutBay;
//				
//				//	02.	상차스케쥴이 대차출하상차이면 목적동은 무조건 H
//				if(YmCommonConst.NEW_SCH_WORK_KIND_CTFL.equals(cSchCd)){
//					cPutBay	= YmCommonConst.BAY_GP_H;
//				}
//				
//				//	03.	상차스케쥴이 HFL 추출이면 목적동은 HFL설비에 목적동
//				if(YmCommonConst.NEW_SCH_WORK_KIND_CFLO.equals(cSchCd)){
//					
//					JDTORecord tJr = dao.getEquipInfoWithEquipGp(YmCommonConst.STACK_COL_GP_3CFD01);
//					if(tJr != null){
//						cPutBay	 = StringHelper.evl(tJr.getFieldString("CARUNLOAD_BAY"), "");
//					}
//				}
//				
//				//	04.	상차스케쥴이 SPM 추출이면 목적동은 SPM설비에 목적동
//				if(YmCommonConst.NEW_SCH_WORK_KIND_CKLO.equals(cSchCd)){
//					
//					JDTORecord tJr = dao.getEquipInfoWithEquipGp(YmCommonConst.STACK_COL_GP_3AKD01);
//					if(tJr != null){
//						cPutBay	 = StringHelper.evl(tJr.getFieldString("CARUNLOAD_BAY"), "");
//					}
//				}
//					
//				sTgPutLoc = YmCommonConst.YD_GP_3 + 
//			  			    cPutBay + 
//			  			    sEquipGp.substring(2);
//			}	 
//			 	
//			/*
//			 *	하차동 지정 구분
//			 */
//			{
//				sTgPYn = sPYn;
//			}	 
//			
//			/*
//			 *	하차동 스케쥴코드
//			 */
//			if("3XTC01".equals(sEquipGp)){
//	    		sTgPSchCd = YmCommonConst.NEW_SCH_WORK_KIND_CTMU; //Coil 대차하차(L)
//	    	}else if("3XTC02".equals(sEquipGp)){
//	    		sTgPSchCd = YmCommonConst.NEW_SCH_WORK_KIND_CTM4; //Coil 대차하차(R)	
//	    	}else if("3XTC03".equals(sEquipGp)){
//	    		sTgPSchCd = YmCommonConst.NEW_SCH_WORK_KIND_CTM8; //Coil 대차하차(R)
//	    	}else if("3XTC04".equals(sEquipGp)){
//	    		sTgPSchCd = YmCommonConst.NEW_SCH_WORK_KIND_CTM9; //Coil 대차하차(R)
//	    	}else if("3XTC05".equals(sEquipGp)){
//	    		sTgPSchCd = YmCommonConst.NEW_SCH_WORK_KIND_CTMX; //Coil 대차하차(R)
//	    	}
//			
//			logger.println(LogLevel.DEBUG, this, "=대차스케쥴 NN => sTgUpLoc 	    ="+sTgUpLoc);
//			logger.println(LogLevel.DEBUG, this, "=대차스케쥴 NN => sTgUYn 		="+sTgUYn);
//			logger.println(LogLevel.DEBUG, this, "=대차스케쥴 NN => sTgUSchCd 	="+sTgUSchCd);
//			logger.println(LogLevel.DEBUG, this, "=대차스케쥴 NN => sTgPutLoc 	="+sTgPutLoc);
//			logger.println(LogLevel.DEBUG, this, "=대차스케쥴 NN => sTgPYn 		="+sTgPYn);
//			logger.println(LogLevel.DEBUG, this, "=대차스케쥴 NN => sTgPSchCd 	="+sTgPSchCd);
//			
//			if(sTgUpLoc.equals(sTgPutLoc)){
//				logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> 에러 = 상/하차동이 같음.");
//				return tcInfoJr;
//			}
//        } else
//        {
//        	sTgUpLoc 	= sUpLoc;
//    		sTgUYn   	= sUYn;
//    		sTgUSchCd 	= sUSchCd;
//    		sTgPutLoc 	= sPutLoc;
//    		sTgPYn 		= sPYn;
//    		sTgPSchCd 	= sPSchCd;
//    		
//    		logger.println(LogLevel.DEBUG, this, "=대차스케쥴  => sTgUpLoc 	="+sTgUpLoc);
//			logger.println(LogLevel.DEBUG, this, "=대차스케쥴  => sTgUYn 		="+sTgUYn);
//			logger.println(LogLevel.DEBUG, this, "=대차스케쥴 => sTgUSchCd 	="+sTgUSchCd);
//			logger.println(LogLevel.DEBUG, this, "=대차스케쥴 => sTgPutLoc 	="+sTgPutLoc);
//			logger.println(LogLevel.DEBUG, this, "=대차스케쥴 => sTgPYn 		="+sTgPYn);
//			logger.println(LogLevel.DEBUG, this, "=대차스케쥴 => sTgPSchCd 	="+sTgPSchCd);
//			
//			if(sTgUpLoc.equals(sTgPutLoc)){
//				logger.println(LogLevel.DEBUG, this, "=대차스케쥴=> 에러 = 상/하차동이 같음.");
//				return tcInfoJr;
//			}
//        }
//			/*
//	         *	3.	작업대상재가 있는 동으로 대차설정정보를 변경한다.
//	         *		하차동관련정보는 대기항목에 셋팅한다.
//	         *		하차후 상차출발시점에 대기항목정보를 하차항목정보에 셋팅한다.
//	         *
//	         *		현재동 		:  	CURR_STOP_LOC
//	         *		상차동 		:	CARLOAD_STOP_LOC
//	         *		상차지정유무:	CARLOAD_ASSIGN_YN
//	         *		상차스케쥴	:	CARLOAD_SCH_WORK_KIND
//	         *		하차동		:	WAIT_STOP_LOC(*)
//	         *		하차스케쥴	: 	WBOOK_ID(*)
//	         *		하차지정유무:	CARUNLOAD_ASSIGN_YN
//	         */ 
//	         int iSeq = dao.updateEquipSchInfo_01(sEquipGp,
//	                                              sCurLoc,
//												  sTgUpLoc,	
//												  sTgUYn,
//												  sTgUSchCd, 
//												  sTgPutLoc,
//												  sTgPYn,
//												  sTgPSchCd);
//			 
//			 tcInfoJr = ymCommonDAO.readVicCarInfo(sYdGp, 
//											 	   sStackColGp, 
//												   sStackBedGp);														   
//		
//		
//        return tcInfoJr;
//    }

 	/**
	 * 오퍼레이션명 : 
	 *
	 * B열연 SLAB 야드관리 -> Pallet조회 -> 차량 카드번호 Null 처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
    public int CarCardNoCanel(String queryID, String register, String stack_col_gp){ // 대차 현재번지정보
    	
    	boolean isSuccess 	= false;
    	int count		  		= 0;
    	String sSchWorkStat 	= "";

    	try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
    		/*
    		 * A열연 SLAB 야드 팔레트 도착처리 취소 => 스케쥴 정보 취소
    		 */
    		if(YmCommonConst.YD_GP_0.equals(stack_col_gp.substring(0,1))){
	       
	        	/*
				SELECT DISTINCT
					   SCH.SCH_ID AS SCH_ID,
					   STOCK.WBOOK_ID AS WBOOK_ID
				 FROM TB_YM_STACKLAYER LAYER, 
					  TB_YM_STOCK STOCK, 
					  TB_YM_SCH SCH
				WHERE STACK_COL_GP 	= ?
				  AND LAYER.STOCK_ID 	= STOCK.STOCK_ID
				  AND STOCK.WBOOK_ID 	= SCH.WBOOK_ID
	    		*/
	    		String query = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStockSchInfo";
	    		/*
		    		UPDATE TB_YM_WBOOK A
				SET CRANE_WORD_PUT_LOC = SUBSTR(A.CRANE_WORD_PUT_LOC,0,4)
				WHERE A.WBOOK_ID =?
			*/
	    		String wbookquery 	= "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateWbookPutloc";
	    		List Stacklayerinfo 	= new YdStackLayerDAO().findList(query, new Object[]{stack_col_gp});
	    		
	    		for(int ii = 0; ii < Stacklayerinfo.size(); ii++){
	    			
	    			JDTORecord jrecord = (JDTORecord)Stacklayerinfo.get(ii);
	    			
	    			JDTORecord schInfo = new CraneSchDAO().getSchInfoWithSchId(jrecord.getFieldString("SCH_ID"));
	    			sSchWorkStat 		  = StringHelper.evl(schInfo.getFieldString("SCH_WORK_STAT"), "");

				//Update필요 TB_YM_STOCK 권하위치 세팅값을 4자리만 0APT이런 식으로....
				if(!YmCommonConst.SCH_WORK_STAT_S.equals(sSchWorkStat)
				|| !YmCommonConst.SCH_WORK_STAT_3.equals(sSchWorkStat)){
					ymCommonDAO.updateData(wbookquery, new Object[]{jrecord.getFieldString("WBOOK_ID")});
		    			
		    			EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
		    			ejbConn.trx("cancelSlabSchInfo", new Class[]{ String.class }, new Object[]{jrecord.getFieldString("SCH_ID")});
				}
	    		}
	    	}else if(YmCommonConst.YD_GP_2.equals(stack_col_gp.substring(0,1))){
		    	/*
		    	 *	2007.09.03 YJK 
		    	 *
	    		 *	B열연 SLAB 야드 팔레트 도착처리 취소 => 스케쥴 정보 취소 , 작업예약 정보 삭제
	    		 */	
	    		CraneSchDAO dao = new CraneSchDAO();
	    		EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
	    		
	    		String query1 	= "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStockSchInfo";
		    		
		    	List schList 	= new YdStackLayerDAO().findList(query1, new Object[]{stack_col_gp});
	    		
	    		 /*
	    		  *	1. 스케쥴 정보 취소처리
	    		  *   2. 작업예약 정보 삭제처리
	    		  */
	    		for(int inx = 0; inx < schList.size(); inx++){
	    			
	    			JDTORecord jrecord = (JDTORecord)schList.get(inx);
	    			
	    			JDTORecord schInfo 	= dao.getSchInfoWithSchId(jrecord.getFieldString("SCH_ID"));
	    			sSchWorkStat 		  	= StringHelper.evl(schInfo.getFieldString("SCH_WORK_STAT"), "");

				if(!YmCommonConst.SCH_WORK_STAT_S.equals(sSchWorkStat)
				|| !YmCommonConst.SCH_WORK_STAT_3.equals(sSchWorkStat)){
					
		    			ejbConn.trx("cancelSlabSchInfo", new Class[]{ String.class }, 
		    									      new Object[]{ jrecord.getFieldString("SCH_ID")});
		    			
		    			ejbConn.trx("clearStockWbookInfo", new Class[]{ String.class }, 
		    										  new Object[]{ jrecord.getFieldString("WBOOK_ID")});
				}
	    		}
		}

    		/*
			TALBE : TB_YM_STACKCOL에 CAR_CARD_NO를 ''처리한다
			ym.facilitystatus.facilityinquiry.dao.YdStackColDAO.updateCarCardNo
			UPDATE TB_YM_STACKCOL
			SET CAR_CARD_NO 	= ''
			     , MODIFIER 		= ?
			     , REG_DDTT 		= Sysdate
			 WHERE STACK_COL_GP = ?
		*/
	    	count = new YdStackColDAO().requestupdateData(queryID, new Object[]{register, stack_col_gp});
	    	
	    	/*
			UPDATE TB_YM_STACKLAYER
			SET STOCK_ID		   			= '',
				STACK_LAYER_ACTIVE_STAT 	= 'C',
				STACK_LAYER_STAT			= 'E',
				MODIFIER					=  ?,
				MOD_DDTT					= SYSDATE
			WHERE STACK_COL_GP = ?
		*/
	    	String queryID1 ="ym.facilitystatus.facilityinquiry.YdStackLayerDAO.updateCarCardNo";
	       
	       //TABLE : TB_YM_STACKLAYER에있는 STOCK_ID를 ''처리하고 적치 상태를 확성화 시키고 적치가능으로 UPDATE
	       count += new YdStackLayerDAO().requestupdateData(queryID1, new Object[]{register, stack_col_gp});
	        
    	}catch(DAOException daoe){
			throw new DAOException(daoe);
		}catch(Exception ie){
			throw new EJBServiceException(ie);
		}       	   
    	return count;
    }
    
 	/**
	 * 오퍼레이션명 : 
	 *
	 * 2007-03-28 A열연 SLAB야드 추가(MCH)
        * 강제 차량 출발 지시를 처리한다.
        * slab_noList = SLAB_NO LIST 받고
        * cardNo 	   = 카드번호
        * pos 	       = 정지위치
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */          
	public boolean effectcarStartOrder(String cardNo, String pos) throws DAOException, EJBServiceException {
		boolean isTemp = true;
		logger.println(LogLevel.DEBUG, this, "=강제 차량 출발  => cardNo 	="+cardNo);
		logger.println(LogLevel.DEBUG, this, "=강제 차량 출발  => 출발위치 	="+pos);		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			// 현 CAR_CARD_NO가 출하에서 편성된 NO인지 체그
			JDTORecord PtInfo = new CraneSchDAO().getStockcolStartOrder(cardNo, pos);
			
			if(PtInfo == null){
				throw new DAOException("상차 지시 편성을 하시고 출발하세요");
			}else{
				//	출발 처리 로직 호출
				isTemp = carStartOrder("", cardNo, pos);	
			}
		}catch(DAOException daoe){
			
			throw new DAOException(daoe);
		}catch(Exception ie){
			throw new EJBServiceException(ie);
		}
    	return isTemp;
    }

 	/**
	 * 오퍼레이션명 : 
	 *
	 * 2007-04-19 A열연 SLAB야드 추가(MCH)설비의 STACK_MAX_QNTY를 수정한다.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */               
	public int UpdateStackMaxQnty(String register, String Maxequip, String equip_gp) throws DAOException, EJBServiceException {
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			return new YdEquipDAO().UpdateStackMaxQnty(equip_gp, Maxequip, register);
		}catch(DAOException daoe){
			throw new DAOException(daoe);
		}catch(Exception ie){
			throw new EJBServiceException(ie);
		}
    }
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 2010-05-13 B열연 SLAB야드 추가(MCH)설비의 STACK_MAX_QNTY를 수정한다.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */               
	public int UpdateStackMaxQnty_Bslab(String register, String Maxequip, String equip_gp, String equip_gp1) throws DAOException, EJBServiceException {
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			return new YdEquipDAO().UpdateStackMaxQnty_Bslab(equip_gp,equip_gp1, Maxequip, register);
		}catch(DAOException daoe){
			throw new DAOException(daoe);
		}catch(Exception ie){
			throw new EJBServiceException(ie);
		}
    }
	
	

 	/**
	 * 오퍼레이션명 : 
	 *
	 * 2007-04-19 A열연 SLAB야드 추가 정상모드, 우천모드 구분을 가져온다.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                    
	public JDTORecord getEquipInfoWithEquipGp(String equip_gp) throws DAOException, EJBServiceException {
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			return new CraneSchDAO().getEquipInfoWithEquipGp(equip_gp);
		}catch(DAOException daoe){
			throw new DAOException(daoe);
		}catch(Exception ie){
			throw new EJBServiceException(ie);
		}
    }
	
 	/**
	 * 오퍼레이션명 : 
	 *
	 * 2007-04-19 A열연 SLAB야드 추가 정상모드, 우천모드 구분을 가져온다.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                         
	public int updateEquipInfoWithEquipGp(String query, String yd_gp, String equip_gp) throws DAOException, EJBServiceException {
		try{

			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			List equipmode = new ArrayList();
			equipmode.add(equip_gp);
			equipmode.add(yd_gp);
			/*
			UPDATE TB_YM_EQUIP
			SET PALLET_NO = ?
			WHERE EQUIP_GP LIKE '0'||?||PT%'
			*/
			String query1 ="ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.updateEquipmodechange";
			int count = new YdEquipDAO().updateData(query1, equipmode);
			
			count = new YdStackLayerDAO().requestupdateData(query, new Object[]{"0"+yd_gp+"PT0_"});
			return count;
		}catch(DAOException daoe){
			throw new DAOException(daoe);
		}catch(Exception ie){
			throw new EJBServiceException(ie);
		}
    }


 	/**
	 * 오퍼레이션명 : 최규성
	 * 대차 설정 정보를 가져온다.
	 * 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                         
	public JDTORecord getEquipInfo(String sQueryId, String equip_gp) throws DAOException, EJBServiceException {
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			List whereData = new ArrayList();
			whereData.add(equip_gp);

			return new YdEquipDAO().getData(sQueryId, whereData);
		}catch(DAOException daoe){
			throw new DAOException(daoe);
		}catch(Exception ie){
			throw new EJBServiceException(ie);
		}
    }

 	/**
	 * 오퍼레이션명 : 
	 *
	 * 2007-07-02 A열연 SLAB야드 PDA PALLET 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return 
	 * @throws 
	 */                              
	public List getListASlabPallet(String query, String stack_col_gp) throws DAOException, EJBServiceException {
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			return new YdStackLayerDAO().requestgetListData(query, new Object[]{stack_col_gp});
		}catch(DAOException daoe){
			throw new DAOException(daoe);
		}catch(Exception ie){
			throw new EJBServiceException(ie);
		}
    }
	/**
	 * 현재 사용중인 차량이적용 차량카드번호를 조회한다.
	 * @param queryid
	 * @param listArgData
	 * @return
	 * @throws DAOException
	 * @throws EJBServiceException
	 */
	public List getUsedCardNo(String queryid, List listArgData) throws DAOException, EJBServiceException {
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			YdStackLayerDAO ydstacklayerDAO = new YdStackLayerDAO();
			return ydstacklayerDAO.getListData(queryid, listArgData);
			
			// return new YdStackLayerDAO().getListData(queryid, listArgData);
		}catch(DAOException daoe){
			throw new DAOException(daoe);
		}catch(Exception ie){
			throw new EJBServiceException(ie);
		}
	
	}

	//=================================================================================================
	/// 차량이적 상차일 경우 처리 함수 최규성. 2009-12-03
		/**
		 * 작업예약의 스케쥴 수행 방법을 UPDATE
		 * @param carInfos	차량 도착 정보
		 * @param pos		차량 정지 위치
		 */
		private void editSchMethodOfWBook(	List carInfos, 
										String schKind, 
										String pos, 
										String item,
										String carMoveYN) {
			logger.println(LogLevel.DEBUG, this, "Start - editSchMethodOfWBook(List,String,String,String,String) :overloading ");
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
			
			if ("Y".equals(carMoveYN) )
			{
				if(! getUnloadSchKind(item, pos,carMoveYN).equals(schKind)) {		// 하차스케줄인지 검사. 아니면..
					for(int i = 0; i < carInfos.size(); i++) {
						ymCommonDAO.modifyOperatorOfWBook(
														getSchWorkKind(schKind, pos, carMoveYN),
																	YmCommonConst.SCH_WORK_LOC_DECISION_METHOD_O,
																	pos,
																	getField((JDTORecord)carInfos.get(i), "WBOOK_ID"));                
					}
				}
			}
			logger.println(LogLevel.DEBUG, this, "End - editSchMethodOfWBook(List,String,String,String,String) :overloading ");
		}
	// 차량이적 최규성 2009-12-03
	  /**
	     * @param schKind
	     * @param pos
	     * @return
	     */
	    private String getSchWorkKind(String schKind, String pos,String carMoveYN ) {
	    	logger.println(LogLevel.DEBUG, this, "Start - getSchWorkKind(String,String,String) :overloading ");
	    	
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
	    	if("Y".equals(carMoveYN)) {
		        if(YmCommonConst.YD_GP_3.equals(pos.substring(0, 1))) {
		        	if("2".equals(pos.substring(5, 6)) ||
		               "4".equals(pos.substring(5, 6))) {
		        		logger.println(LogLevel.DEBUG, this, "차량정지위치 우측"+pos.substring(5, 6) +"-"+schKind);
		                if(YmCommonConst.NEW_SCH_WORK_KIND_GVFL.equals(schKind)||
		                		YmCommonConst.NEW_SCH_WORK_KIND_GPFL.equals(schKind)||
		                		YmCommonConst.NEW_SCH_WORK_KIND_GTFL.equals(schKind)) {
		                	//return YmCommonConst.NEW_SCH_WORK_KIND_GVF1;   
		                	 return schKind.substring(0, 3)+"1";
		                }else if(YmCommonConst.NEW_SCH_WORK_KIND_CVML.equals(schKind)) {
		                	return YmCommonConst.NEW_SCH_WORK_KIND_CVM2;
		                }else if(YmCommonConst.NEW_SCH_WORK_KIND_GVML.equals(schKind)) {
		                	return YmCommonConst.NEW_SCH_WORK_KIND_GVM2;
		                }
		            } else if (YmCommonConst.BAY_GP_D.equals(pos.subSequence(1, 2))) {
		            	if("5".equals(pos.substring(5, 6)) ||
		                   "6".equals(pos.substring(5, 6))) {
		            		if(YmCommonConst.NEW_SCH_WORK_KIND_CVML.equals(schKind)) {
		                    	// Coil 소재이송상차 => 2,3
		                    	return YmCommonConst.NEW_SCH_WORK_KIND_CVM3;
		                    }else if(YmCommonConst.NEW_SCH_WORK_KIND_GVML.equals(schKind)) {
		                    	// Coil 제품이송상차 => 2,3
		                    	return YmCommonConst.NEW_SCH_WORK_KIND_GVM3;
		                    }
		            	}
		            }
		        }
	    	}
	        logger.println(LogLevel.DEBUG, this, "End - getSchWorkKind(String,String,String) : ");
	        return schKind;
	    }

	//=======================================================================================================
	 //=======================================================================================================
	 // 차량이적 하차의 경우 처리될 함수들. 최규성 2009-12-03
	 // 기존 함수를 오버로딩하여 사용함. 인자만 추가.

	     private void unloadReservation(List stocks, String pos, String yd,String cardNo) throws Exception {
	    	 logger.println(LogLevel.DEBUG, this, "Start - unloadReservation(List,String,string,string) :overloading "+stocks+":"+pos+":"+yd+":"+cardNo);
	    	 logger.println(LogLevel.DEBUG, this, "카드번호 인자 추가."+cardNo);
	    	 
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
			
	    	 int stocksCnt = stocks != null ? stocks.size() : 0;
	         
	         if(YmCommonConst.YD_GP_2.equals(yd) ||
	            YmCommonConst.YD_GP_0.equals(yd)) {
	        	 logger.println(LogLevel.DEBUG, this, "카드번호 인자 추가."+cardNo);
	             slabUnloadWork(stocks, pos, stocksCnt);
	         }else {
	 			if ((cardNo.equals(YmCommonConst.CAR_BAY_TRANS_CARD_NO_1))
	 	       	    || (cardNo.equals(YmCommonConst.CAR_BAY_TRANS_CARD_NO_2))
	 	       	    || (cardNo.equals(YmCommonConst.CAR_BAY_TRANS_CARD_NO_3))
	 	       	    || (cardNo.equals(YmCommonConst.CAR_BAY_TRANS_CARD_NO_4))
	 	       	    || (cardNo.equals(YmCommonConst.CAR_BAY_TRANS_CARD_NO_5))) {
	 				logger.println(LogLevel.DEBUG, this, "coilUnloadWork(stocks,pos,stocksCnt,'Y') 실행");
	 				coilUnloadWork(stocks,pos,stocksCnt,"Y");
	 			}else{
	 				logger.println(LogLevel.DEBUG, this, "coilUnloadWork(stocks,pos,stocksCnt,'N') 실행");
	 				coilUnloadWork(stocks,pos,stocksCnt,"N");
	 			}
//	                 coilUnloadWork(stocks, pos, stocksCnt);
	         }
	         
	         logger.println(LogLevel.DEBUG, this, "End - unloadReservation(List,String,string,string) : ");
	     }

	     /** 차량이적 최규성 2009-12-03
	      * 코일 하차 작업 예약을 처리한다.
	      * @param stocks	저장품정보
	      * @param pos		차량정지위치
	      * @param stocksCnt	저장품 개수
	      */
	     private void coilUnloadWork(List stocks, String pos, int stocksCnt,String carMoveYN) {
	         JDTORecord dto 		= null;
	         String nextWBookId 	= null;
	         logger.println(LogLevel.DEBUG,this, "coilUnloadWork(List,String,int,String):overloading 코일 하차 작업 예약을 처리 ");
	         
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
			
	 		for(int i = 0; i < stocksCnt; i++) {
	             dto 		= (JDTORecord)stocks.get(i);
	             nextWBookId = ymCommonDAO.createWBook(
	                     pos, 
	                     getUnloadSchKind(getField(dto, "STOCK_ITEM"), pos,carMoveYN),		// 저장품상태에 따라서 스케줄코드를 변경한다.
	                     YmCommonConst.SCH_WORK_LOC_DECISION_METHOD_S, 
	                     "");
	             ymCommonDAO.modifyTermAndWBookIdOfStock(
	                     nextWBookId, 
	                     getStockMoveTerm(getField(dto, "STOCK_ITEM"),getField(dto, "FRTOMOVE_EQUIP_GP").substring(0, 1)), 
	                     getField(dto, "STOCK_ID"));
	             ymCommonDAO.modifyLayerStatOfLayer(
	                     YmCommonConst.STACK_LAYER_STAT_S, 
	                     pos, 
	                     getField(dto, "STOCK_ID"));
	         }
	 		
	 		logger.println(LogLevel.DEBUG, this, "End - coilUnloadWork() ");
	     }


	 // 하차 스케줄 코드를 변경한다. 2009-12-03 최규성
	 // carMoveYN 인자 추가. getU법loadSchKind(String item, String pos)와 비교.
	 private String getUnloadSchKind(String item, String pos, String carMoveYN) {
		 logger.println(LogLevel.DEBUG, this, "Start - getUnloadSchKind():overloading ");
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
		 if("Y".equals(carMoveYN) ) {		// 차량이적용으로 사용. 
	         if(YmCommonConst.YD_GP_3.equals(pos.substring(0, 1))) {
	 			if("2".equals(pos.substring(5, 6)) ||
	               "4".equals(pos.substring(5, 6))) {
	 				logger.println(LogLevel.DEBUG, this, "getUnloadSchKind(CM) 실행");
	         		if(YmCommonConst.ITEM_CG.equals(item)) {
	                     return YmCommonConst.NEW_SCH_WORK_KIND_GVM4;
	                 }else {
	                     return YmCommonConst.NEW_SCH_WORK_KIND_CVM4;
	                 }
	             }else {
	            	 logger.println(LogLevel.DEBUG, this, "getUnloadSchKind(CG) 실행");
	                 if(YmCommonConst.ITEM_CG.equals(item)) {
	                     return YmCommonConst.NEW_SCH_WORK_KIND_GVMU;
	                 }else {
	                     return YmCommonConst.NEW_SCH_WORK_KIND_CVMU;
	                 }                    
	             }
	         }
	     }else{						// 기존 소스와 동일함.
	    	 logger.println(LogLevel.DEBUG, this, "getUnloadSchKind() 기존소스로 실행됨.");
	         if(YmCommonConst.ITEM_SM.equals(item)) {
	             return YmCommonConst.NEW_SCH_WORK_KIND_SVMU;
	         }else {
	             if(YmCommonConst.YD_GP_1.equals(pos.substring(0, 1))) {
	                 if(YmCommonConst.ITEM_CG.equals(item)) {
	                     return YmCommonConst.NEW_SCH_WORK_KIND_GVMU;
	                 }else {
	                     return YmCommonConst.NEW_SCH_WORK_KIND_CVMU;
	                 }                
	             }else if(YmCommonConst.YD_GP_3.equals(pos.substring(0, 1))) {
	            	
	             	if("3".equals(pos.substring(5, 6)) ||
	                    "4".equals(pos.substring(5, 6))) {

	             		if(YmCommonConst.ITEM_CG.equals(item)) {

	                         return YmCommonConst.NEW_SCH_WORK_KIND_GVM4;
	                     }else {
	                         return YmCommonConst.NEW_SCH_WORK_KIND_CVM4;
	                     }
	                 }else {
	                     if(YmCommonConst.ITEM_CG.equals(item)) {
	                         return YmCommonConst.NEW_SCH_WORK_KIND_GVMU;
	                     }else {
	                         return YmCommonConst.NEW_SCH_WORK_KIND_CVMU;
	                     }                    
	                 }
	             }
	         }
	 	}
	     return "";
	 }
	 
	 
	 
	 /**
	     * 차량 출발/도착지시 전문을 편성한다.
	     * 1. A열연 차량진입/출발 정보		THHC190
	     * 2. B열연 
	     *    2.1 차량 도착/출발 정보 COIL  CN1BP06
	     *    2.2 차량 도착/출발 정보 SLAB	 CM1BP06
	     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	     * @param stocks	저장품 정보 
	     * @param cardNo	차량CARD번호
	     * @param pos		차량 정지위치
	     * @param gp		지시구분
	     * @param carGp		도착출발구분
	     */
	 public void sendL2ArrivalOrder(List stocks, JDTORecord stock, String cardNo, String pos, String carGp) {    
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return;
			}
			
	        String ydGp = pos.substring(0, 1);
	        String bay_gp = pos.substring(1, 2);
	        int stocksCnt = stocks != null ? stocks.size() : 0;
	        if(YmCommonConst.YD_GP_4.equals(ydGp)) {
	            editTakeOutTimeOfSlabComm(stocks, ydGp, carGp, stocksCnt);
	            return;
	        }
	        
	        String tcCd 	= YmCommonConst.TC_THHC190;
	        String schKind 	= getField(stock, "SCH_WORK_KIND");
	        StringBuffer sendMsg 	= new StringBuffer();  
	        JDTORecord cardInfo 	= ymCommonDAO.readCarNo(ydGp, cardNo);
	        if(YmCommonConst.YD_GP_1.equals(ydGp)) {//A열연 COIL
	            /**
	             * THHC190
	             * 1	전문코드			CHAR	07		
	             * 2	작업동			CHAR	01		
	             * 3	진입위치 SEQ NO	CHAR	01		
	             * 4	차량구분			CHAR	01		1:반입, 2:출하
	             * 5	운송회사 코드		CHAR	05		
	             * 6	차량번호			CHAR	05		
	             * 7	작업대상 수량		CHAR	02		
	             * 8	CARD 번호		CHAR	04		
	             * 9	코일번호			CHAR	10		8회 반복
	             * 10	권상, 권하 위치	CHAR	08		
	             * 11	SPARE			CHAR	30		
	             */            
	            Map tc = ymCommonDAO.readColumnLenOfTc(tcCd);
	            sendMsg.append(tcCd);									//전문코드
	            sendMsg.append(pos.substring(1, 2));					//작업동
	            appendMsg(sendMsg, "", 									getFieldLen(tc, "진입위치SEQNO"));
	            if(YmCommonConst.NEW_SCH_WORK_KIND_CVRU.equals(schKind)) {
	                carGp = "1";
	            }else {
	                carGp = "2";
	            }
	            appendMsg(sendMsg, carGp, 								getFieldLen(tc, "차량구분"));
	            appendMsg(sendMsg, getField(cardInfo, "TRANS_COM_CD"), 	getFieldLen(tc, "운송회사코드"));
	            appendMsg(sendMsg, getField(cardInfo, "CAR_NO"), 		getFieldLen(tc, "차량번호"));            
	            appendMsgNum(sendMsg, ""+ stocksCnt,					getFieldLen(tc, "작업대상수량"));
	            appendMsg(sendMsg, cardNo, 								getFieldLen(tc, "CARD번호"));
	            sendAMsg(sendMsg, stocks, tc);							//코일번호
	            appendMsg(sendMsg, "",									getFieldLen(tc, "SPARE"));
	            sendQueue(tcCd, sendMsg.toString());
	        }else {	//B열연 COIL/SLAB
	            /**
	             * CM1BP06
	             * 1	전문코드	TC					CHAR	07		
	             * 2	발생일자	Date				CHAR	10		YYYY-MM-DD
	             * 3	발생시간	Time				CHAR	08		HH-MM-SS
	             * 4	전문구분	Form				CHAR	01		I  : Initialize, U : Update, D : Delete,   R : Re-request
	             * 5	전문길이	Message_Length		CHAR	04		
	             * 6	차량 번호	CarNo				CHAR	12		
	             * 7	차량 TYPE	CarType			CHAR	01		조업기준
	             * 8	출발/도착 구분	ArriveId		CHAR	01		‘D’:도착 ‘S’:출발
	             * 9	차량 진입 위치	CarInPosition	CHAR	02		YARD구분(1)+동구분(1)+SPAN(2)+열 NO(2)	
	             * 10	지시구분	OrderId				CHAR	01		1:장입지시, 2:이송지시, 3:출하지시	
	             * 11	지시번호	OrderNo				CHAR	10		
	             * 12	CARD 번호	CardNo			CHAR	06		
	             * 13	작업 매수	WorkCount			CHAR	02		Slab 10매 정보를 보낼 때작업매수는 10매, 송신 Seq는 현재 보내는 건의 순서를 말함
	             * 14	송신 Seq	RecevieSeq			CHAR	01		
	             * 15	작업 SLAB No	WorkSlabNo		CHAR	11		
	             */
	            String ymd = YmCommonUtil.getStringYMD("-");
	            String hms = YmCommonUtil.getStringHMS("-");
	            if(YmCommonConst.YD_GP_3.equals(ydGp)) {
	                tcCd = YmCommonConst.TC_CN1BP06;
	            }else {
	                tcCd = YmCommonConst.TC_CM1BP06;
	            }
	            Map tc = ymCommonDAO.readColumnLenOfTc(tcCd);
	            sendMsg.append(tcCd);
	            sendMsg.append(ymd);
	            sendMsg.append(hms);
	            sendMsg.append(YmCommonConst.FORM_I);
	            appendMsgNum(sendMsg, ""+ (YmCommonUtil.getTotalLenOfTc(tc) - 30), getFieldLen(tc, "전문길이"));
	            appendMsg(sendMsg, getField(cardInfo, "CAR_NO"), getFieldLen(tc, "차량번호"));
	            if("TR".equals(pos.substring(2, 4))) {
	                appendMsg(sendMsg, "T",		getFieldLen(tc, "차량TYPE"));                
	            }else if("PT".equals(pos.substring(2, 4))) {
	                //appendMsg(sendMsg, "P",		getFieldLen(tc, "차량TYPE"));
	                appendMsg(sendMsg, "T",		getFieldLen(tc, "차량TYPE"));
	            }else {
	                appendMsg(sendMsg, "",		getFieldLen(tc, "차량TYPE"));
	            }
	            appendMsg(sendMsg, carGp, 								getFieldLen(tc, "출발도착구분"));
	            appendMsg(sendMsg, pos, 								getFieldLen(tc, "차량진입위치"));
	            appendMsg(sendMsg, getOrderGp(schKind), 				getFieldLen(tc, "지시구분"));            
	            appendMsg(sendMsg, getField(stock, "TRANS_WORD_DATE_NO"), 	getFieldLen(tc, "지시번호"));
	            appendMsg(sendMsg, cardNo, 								getFieldLen(tc, "CARD번호"));
	            appendMsgNum(sendMsg, ""+ stocksCnt, 					getFieldLen(tc, "작업매수"));
	            if(YmCommonConst.YD_GP_3.equals(ydGp)) {
	                sendBCMsg(tcCd, sendMsg, stocks, tc, "작업COILNo", 8);
	            }else {
	                sendBMsg(tcCd, sendMsg, stocks, tc, "작업SLABNo", 10);
	            }
	        }
	    }
	 //=======================================================================================================
	
}