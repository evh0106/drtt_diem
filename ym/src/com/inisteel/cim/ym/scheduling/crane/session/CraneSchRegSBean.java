package com.inisteel.cim.ym.scheduling.crane.session;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import com.inisteel.cim.common.util.CommonUtil;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.common.jms.model.CommonModel;
import com.inisteel.cim.common.jms.model.dm.YMDM001;
import com.inisteel.cim.common.jms.model.dm.YMDM008;
import com.inisteel.cim.common.jms.model.dm.YMDM009;
import com.inisteel.cim.common.jms.model.pm.ZZPM001;
import com.inisteel.cim.common.jms.model.po.YMPO155;
import com.inisteel.cim.common.jms.model.po.YMPO159;
import com.inisteel.cim.common.jms.model.po.YMPO161;
import com.inisteel.cim.common.jms.model.pc.ZZPC001;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.ym.bcommon.dao.YmCommDAO;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ym.common.YmCommonDB;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdSchDAO;
import com.inisteel.cim.ym.scheduling.crane.dao.StockingBlncBasDAO;
import com.inisteel.cim.ym.steelinfo.steelinforecv.dao.YdStockDAO;
import com.inisteel.cim.ym.workrequest.wrequestaccept.dao.YdWBookDAO;
/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="CraneSchRegEJB" jndi-name="JNDICraneSchReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class CraneSchRegSBean extends BaseSessionBean {
	private Logger logger = null;

	private ymCommonDAO ymCommonDAO = null;
	private YmCommDAO commDao = new YmCommDAO();
	private CraneSchDAO dao = null;
	private YmComm ymComm = new YmComm();
	
	public void ejbCreate() {
		LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		logger = new Logger(config);
		ymCommonDAO = new ymCommonDAO();
		dao = new CraneSchDAO();
	}

      /**
	 * 오퍼레이션명 : 부두야드에서 차량에 슬라브 적재 처리를 한다.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */        
	public boolean lieSchResult(List stocks) {
		//2007-03-28 A열연 SLAB야드 추가(MCH)
		logger.println(LogLevel.DEBUG, this, "TODO: 부두 실적 등록 처리//A열연 SLAB야드 ");
		logger.println(LogLevel.DEBUG, this, "수신: " + stocks);
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			/**
			 * 정의 정보: 저장품ID, 적치열, 번지, 단, from위치, 작업예약ID, 스케쥴ID
			 */
			int stocksCnt = stocks != null ? stocks.size() : 0;
			if (stocksCnt == 0) {
				throw new Exception("수신 정보가 없습니다: " + stocks);
			}

			/**
			 * valid check
			 */
			String stockId = StringHelper.evl((String) stocks.get(0), "");
			String col = StringHelper.evl((String) stocks.get(1), "");
			String bed = StringHelper.evl((String) stocks.get(2), "");
			String layer = StringHelper.evl((String) stocks.get(3), "");
			String from = StringHelper.evl((String) stocks.get(4), "");
			String wbookId = StringHelper.evl((String) stocks.get(5), "");
			String schId = StringHelper.evl((String) stocks.get(6), "");
			String to = (col + bed + layer);
			if (from.length() != 10) {
				throw new Exception("FROM 위치 ERROR: " + from);
			} else if (wbookId.length() != 18) {
				throw new Exception("작업예약 ID ERROR: " + wbookId);
			} else if (schId.length() != 18) {
				throw new Exception("스케쥴 ID ERROR: " + schId);
			}

			/**
			 * 저장품의 이동조건 상차완료, 작업예약 ID CLEAR
			 */
			ymCommonDAO.modifyMoveTermAndWBookOfStock("",
					YmCommonConst.NEW_STOCK_MOVE_TERM_VL, stockId);
			/**
			 * 부두야드 MAP CLEAR//A열연 SLAB야드 MAP CLEAR
			 */
			ymCommonDAO.modifyStockStatOfLayer("", // 저장품ID
					YmCommonConst.STACK_LAYER_STAT_E, // 적치단상태
					from.substring(0, 6), // 적치열
					from.substring(6, 8), // 번지
					from.substring(8, 10)); // 단
			/**
			 * 차량 MAP 등록
			 */
			ymCommonDAO.modifyStackStateOfLayer(stockId,
					YmCommonConst.STACK_LAYER_STAT_L, col, bed, layer);
			/**
			 * 작업예약/스케쥴 DELETE
			 */
			ymCommonDAO.removeWBook(wbookId);
			ymCommonDAO.removeSchdule(schId);
			/**
			 * SLAB이송의 상차 실적을 송신
			 */
			sendSlabLoadResult(stockId, to, YmCommonConst.CAR_GP_U);
			/**
			 * SLAB 공통 테이블 UPDATE	
			 */
			ymCommonDAO.modifyStoreLocOfSlabComm(col.substring(0, 1), col
					.substring(1, 2), col.substring(2, 4), col.substring(4, 6),
					bed, layer, to, stockId);
			/**
			 * 실적등록
			 */
			ymCommonDAO.createBackUpWrslt(schId, stockId,
					(col.substring(0, 2) + "CR01"),
					YmCommonConst.NEW_SCH_WORK_KIND_SVML, "1", from, to);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return true;
	}

	/**
	 * SLAB이송의 상차 실적을 송신
	 */
	private void sendSlabLoadResult(String slabNo, String downLoc, String sUpDownFlag) throws Exception {
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
		YMPO155 model = new YMPO155();
		String ymd = YmCommonUtil.getStringYMD("-");
		model.setTcCode(YmCommonConst.MODEL_YMPO155);
		model.setTcDate(ymd);
		model.setTcTime(YmCommonUtil.getStringHMS("-"));
		model.setslabNo(slabNo);
		model.setupDownGbn(sUpDownFlag);
		model.setupDownDate(ymd.replaceAll("-", ""));
		model.setupDownLoc(downLoc);

		EJBConnector ejbConn = new EJBConnector("default", "JNDIYardWrkResReg",	this);
		ejbConn.trx("sendInternalModel", new Class[] { CommonModel.class },
				new Object[] { model });
	}

      /**
	 * 오퍼레이션명 : 
	 *
	 * MAIN 야드 Level-2, 야드관리운영자로부터 수신한 전문을 파싱하여 해당업무로직을 처리한다. 
	 * 1.TC_CD : 없음.
	 * 2.I/F ID : 없음.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */          
	public boolean syCraneScheduleInfoInsert(String wbookId) {
		logger.println(LogLevel.DEBUG, this, "TODO: Crane Schedule 등록 처리");
		logger.println(LogLevel.DEBUG, this, "수신: " + wbookId);
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			List wbooks = getWbookList(wbookId.split("-"));
			int wbooksCnt = wbooks != null ? wbooks.size() : 0;
			JDTORecord dto = null;
			JDTORecord dtoTmp = JDTORecordFactory.getInstance().create();
			for (int i = 0; i < wbooksCnt; i++) {
				dto = callSlabSchedule((String) wbooks.get(i));
				
				if(dto == null){
					// 하단 정보가 스케쥴 실패시 상단의 스케쥴정보는 
					// 작업지시를 송신해야한다.
					if(i != 0){
						logger.println(LogLevel.DEBUG, this, "하단 정보가 스케쥴 실패 ="+dtoTmp);
						callCRWorkOrder(dtoTmp);
					}
					// 스케쥴 실패시 하단정보 스케쥴 생성하지 않음.
					break;
				}else{
					// 이전 스케쥴정보를 가지고 있는다.
					dtoTmp.setField("YD_GP"					, getField(dto, "YD_GP"));
					dtoTmp.setField("BAY_GP"					, getField(dto, "BAY_GP"));
					dtoTmp.setField("SCH_RULE_ACTIVE_STAT"		, getField(dto, "SCH_RULE_ACTIVE_STAT"));
					dtoTmp.setField("SCH_RULE_CRANE_NO"		, getField(dto, "SCH_RULE_CRANE_NO"));
					dtoTmp.setField("SCH_RULE_ALTER_CRANE_NO"	, getField(dto, "SCH_RULE_ALTER_CRANE_NO"));
					dtoTmp.setField("SCH_WORK_KIND"			, getField(dto, "SCH_WORK_KIND"));
				}
			}
			if (dto != null) {
				/**
				 * IDLE 상태일때 Crane 작업지시요구 호출
				 */
				callCRWorkOrder(dto);
			}
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return true;
	}

	/**
	 * SLAB MAIN
	 * 
	 * @param string
	 */
	private JDTORecord callSlabSchedule(String wbookId) throws Exception {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		String schKind ="";
		/**
		 * valid check.
		 */
		if (wbookId == null || wbookId.length() != 18) {
			// throw new Exception("##### 수신항목 작업예약 ID Error: "+ wbookId);
			logger.println(LogLevel.DEBUG, this, "SLAB SCH =>수신항목 작업예약 ID Error: " + wbookId);
			return null;
		}

		/**
		 * 작업예약 정보를 가져온다.
		 */
		JDTORecord wbookInfo = ymCommonDAO.readWBookInfo(wbookId);
		if (wbookInfo == null || wbookInfo.size() == 0) {
			// throw new Exception("##### 작업예약 정보가 없습니다.");
			logger.println(LogLevel.DEBUG, this, "SLAB SCH =>작업예약 정보가 없습니다.");
			return null;
		}
		
		//벤딩표시 유무
		String bendingYN 	= StringHelper.evl( getField(wbookInfo, "YD_RULE_PL_RS_GP"), ""); 
		String SLAB_WT 		= StringHelper.evl( getField(wbookInfo, "SLAB_WT"), "0");
		String YD_GP 		= StringHelper.evl( getField(wbookInfo, "YD_GP"), "");
		double dGSlabWt 	= Double.parseDouble(SLAB_WT);
		
		
		/*
		 * Slab 중량 53000 이상 제외 2015.05.06 임채만 주임
		 */
		if(YD_GP.equals("2")){
			if(dGSlabWt > 53000){			
				logger.println(LogLevel.DEBUG,this, "=[작업예약] => 크레인설정 중량(53000)을 초과함. 작업불가");
				throw new EJBServiceException("=[작업예약] => 크레인설정 중량(53000)을 초과함. 작업불가");
			}
		}
		
		
		/**
		 * SCARFING 작업을 고려한다. -작업예약이 SCARFING 이면 입측 MAP을 생성한다.
		 */
		if(!bendingYN.equals("Y")){
			schKind = getField(wbookInfo, "SCH_WORK_KIND");
			considerScarfing(schKind);
		}

		/**
		 * 주작업 저장품 검색.
		 */
		List mainCSWorks = getMainCSWork(wbookInfo);
		if (mainCSWorks == null || mainCSWorks.size() == 0) {
			// throw new Exception("##### 주작업 저장품 검색 실패: 설비정보 및 스케쥴 기준 확인 요망");
			logger.println(LogLevel.DEBUG, this, "SLAB SCH =>주작업 저장품 검색 실패: 설비정보 및 스케쥴 기준 확인 요망");
			return null;
		}

		/**
		 * YJK 주작업에 대한 FROM 위치의 크레인 영역을 CHECK --아래 주석처리함
		 * considerFromCRRange((JDTORecord)mainCSWorks.get(0));
		 */

		/**
		 * Crane 할당. -Schedule 기준 활성 상태가 "X" 이면 Error Logging 처리 한다.
		 */
		JDTORecord mainCSWork = (JDTORecord) mainCSWorks.get(0);
		if (notValidSchRule(mainCSWork)) {
			// throw new Exception("##### Crane 기준 상태가 사용금지[X] 또는 고장 입니다.");
			logger.println(LogLevel.DEBUG, this,"SLAB SCH =>Crane 기준 상태가 사용금지[X] 또는 고장 입니다.");
			return null;
		}

		/*
		 * A열연 SLAB ROT LINE OFF, B열연 SLAB 이송하차인 경우 처리
		 * 각각의 항목에 따라 이동조건을 세분화한다.
		 * 시편재 및 핸드 스카핑재인지를 체크한다. 
		 * 시편재이면 저장품이동조건을 'D1' 핸드스카핑재이면 저장품이동조건을 'D2'로 셋팅 후 TO위치를 검색한다.
		 */
		String sYDGP = getField(wbookInfo, "YD_GP");
		
		if(YmCommonConst.YD_GP_0.equals(sYDGP)&&(YmCommonConst.NEW_SCH_WORK_KIND_SRLO.equals(schKind)||
												 YmCommonConst.NEW_SCH_WORK_KIND_SVML.equals(schKind))){
			mainCSWorks = getReagentPickScarfingInfo(mainCSWorks, schKind);
		}else if(YmCommonConst.YD_GP_2.equals(sYDGP)&&YmCommonConst.NEW_SCH_WORK_KIND_SVMU.equals(schKind)){
			mainCSWorks = getReagentPickScarfingInfo(mainCSWorks, schKind);
		}
		
		/**
		 * 작업대상을 결정 한다. - 주작업에 대한 보조작업 결정 - 보조작업은 야드일 경우만 결정
		 */
		List csWorkTarget = getWorkTargetInfo(mainCSWorks, schKind);
		logger.println(LogLevel.DEBUG, this, "SLAB SCH =>작업대상: "+ csWorkTarget.size());

		/**
		 * To 영역 주/보조작업 적치열 검색. -주작업 결과를 이용하여 YM_저장품이동경로, YM_위치검색 테이블, YM_적치열을
		 * 조인하여 READ 한다. -보조작업을 결과를 이용하여 YM_적치열을 READ 한다.
		 */
		Map toLoc = getMainAndSubStockCol(csWorkTarget);
		if (toLoc == null || toLoc.size() == 0) {
			logger.println(LogLevel.DEBUG, this,"SLAB SCH =>TO 위치 적치열 검색 실패. ");
			return null;
		}
		logger.println(LogLevel.DEBUG, this, "SLAB SCH =>작업대상 적치열: "+ toLoc.size());

		/**
		 * To 위치 검색 및 스케쥴 셋팅.
		 */
		boolean isSuccess = getToStoreLoc(mainCSWork, csWorkTarget, toLoc,bendingYN);
		
		if (!isSuccess) {
			return null;
		}

		return mainCSWork;
	}

	/**
	 * @param schKind
	 * @throws Exception
	 */
	private boolean considerEquipOnOff(String schKind, String col)
			throws Exception {

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		boolean isTrue = true;

		JDTORecord dto = null;

		if (YmCommonConst.NEW_SCH_WORK_KIND_SWLI.equals(schKind)) {
			dto = ymCommonDAO.readEquipInfo(col);
			if ("C".equals(getField(dto, "HMI_STAT"))) {
				// throw new Exception("##### W/B OFF 상태 입니다.");
				logger.println(LogLevel.DEBUG, this, "##### W/B OFF 상태 입니다.");
				return false;
			} else if ("C".equals(getField(dto, "EQUIP_STAT"))) {
				// throw new Exception("##### W/B 고장 입니다.");
				logger.println(LogLevel.DEBUG, this, "##### W/B 고장 입니다.");
				return false;
			}
		} else if (YmCommonConst.NEW_SCH_WORK_KIND_STSL.equals(schKind)
			  || YmCommonConst.NEW_SCH_WORK_KIND_STML.equals(schKind)
			  || YmCommonConst.NEW_SCH_WORK_KIND_STM2.equals(schKind)) {	
			if ("1".equals(col.substring(4, 5))) {
				dto = ymCommonDAO.readEquipInfo("2XTC01");
			} else if ("2".equals(col.substring(4, 5))) {
				dto = ymCommonDAO.readEquipInfo("2XTC02");
			} else if ("3".equals(col.substring(4, 5))) {
				dto = ymCommonDAO.readEquipInfo("2XTC03");
			}
			if ("C".equals(getField(dto, "EQUIP_STAT"))) {
				// throw new Exception("##### 0"+ col +" 대차가 고장 입니다.");
				logger.println(LogLevel.DEBUG, this, "##### 0" + col
						+ " 대차가 고장 입니다.");
				return false;
			}
		} else if (YmCommonConst.NEW_SCH_WORK_KIND_SCLI.equals(schKind)) {
			if ("2ACT01".equals(col)) {
				dto = ymCommonDAO.readEquipInfo(col);
			} else if ("2ACT02".equals(col)) {
				dto = ymCommonDAO.readEquipInfo(col);
			} else if ("2BCT03".equals(col)) {
				dto = ymCommonDAO.readEquipInfo(col);
			} else if ("2CCT04".equals(col)) {
				dto = ymCommonDAO.readEquipInfo(col);
			}
			if ("C".equals(getField(dto, "EQUIP_STAT"))) {
				// throw new Exception("##### "+ col.substring(5, 6) +"CTC 고장
				// 입니다.");
				logger.println(LogLevel.DEBUG, this, "##### "
						+ col.substring(5, 6) + "CTC 고장 입니다.");
				return false;
			}
		} else if (YmCommonConst.NEW_SCH_WORK_KIND_SVMU.equals(schKind)||
			      YmCommonConst.NEW_SCH_WORK_KIND_STMU.equals(schKind)||
			      YmCommonConst.NEW_SCH_WORK_KIND_STM4.equals(schKind)) {
			if ("2ABK01".equals(col)||"2ABK02".equals(col)) {
				
				dto = ymCommonDAO.readEquipInfo(col);
				
				if ("C".equals(getField(dto, "EQUIP_STAT"))) {
					logger.println(LogLevel.DEBUG, this, "##### 보온카바설비  "
							+ col + " 고장 입니다.");
					return false;
				}
			}
		}
		return isTrue;
	}

	/**
	 * @param strings
	 * @return
	 */
	private ArrayList getWbookList(String[] list) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		int listCnt = list != null ? list.length : 0;
		ArrayList arr = new ArrayList();
		for (int i = 0; i < listCnt; i++) {
			arr.add(list[i]);
		}
		return arr;
	}

	/**
	 * 작업예약이 SCARFING 이면 입측 MAP을 생성한다. - 입측에 적치가능번지가 있으면 생략한다.
	 * 
	 * @param schKind
	 *            스케쥴작업종류
	 */
	private void considerScarfing(String schKind) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
		if (isSupplyScarfing(schKind)) {

			JDTORecord layerJr = dao.getStackLayerInfoWithPk(
					YmCommonConst.STACK_COL_GP_2ESE01,
					YmCommonConst.STACK_BED_GP_01,
					YmCommonConst.STACK_LAYER_GP_01);
			String sStockId = "";
			if (layerJr != null) {
				sStockId = StringHelper.evl(layerJr.getFieldString("STOCK_ID"),"");
			}
			if (layerJr == null || !"".equals(sStockId)) {
				YmCommonDB.shiftConveyorInfo(YmCommonConst.STACK_COL_GP_2ESE01,
						YmCommonConst.GBN_MIN);
			}
		}
	}

	/**
	 * 스케쥴 작업종류가 스카핑 보급인지 리턴한다.
	 * 
	 * @param schKind
	 *            스케쥴작업종류
	 * @return
	 */
	private boolean isSupplyScarfing(String schKind) {
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		if (YmCommonConst.NEW_SCH_WORK_KIND_SSLI.equals(schKind)||YmCommonConst.NEW_SCH_WORK_KIND_SHSI.equals(schKind))
		{	
			return true;
		}else
		{
            return false;
		}
	}

	/**
	 * 스케쥴 작업종류가 스카핑 보급인지 리턴한다.
	 * 
	 * @param schKind
	 *            스케쥴작업종류
	 * @return
	 */
	private boolean isEquipSch(String schKind, String toLoc) {

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		if (YmCommonConst.NEW_SCH_WORK_KIND_SVML.equals(schKind)) { // 이송상차
			return true;
		} else if (YmCommonConst.NEW_SCH_WORK_KIND_SWLI.equals(schKind)) {// W/B보급
			if ("WB".equals(toLoc.substring(2, 4))) {
				return true;
			} else {
				return false;
			}
		} else if (YmCommonConst.NEW_SCH_WORK_KIND_SVMU.equals(schKind)) {	// 이송하차
			if ("BK".equals(toLoc.substring(2, 4))) {							// B열연 A동 보온카바
				return true;
			}else if ("TC".equals(toLoc.substring(2, 4))) {						// B열연 A동 대차상차
				return true;
			} else {
				return false;
			}	
		} else if (YmCommonConst.NEW_SCH_WORK_KIND_STMU.equals(schKind)||
				YmCommonConst.NEW_SCH_WORK_KIND_STM4.equals(schKind)) {	// 대차하차
			if ("BK".equals(toLoc.substring(2, 4))) {							// B열연 A동 보온카바
				return true;
			} else {
				return false;
			}		
		} else if (YmCommonConst.NEW_SCH_WORK_KIND_STSL.equals(schKind)) {// 동간보급상차
			return true;
		} else if (YmCommonConst.NEW_SCH_WORK_KIND_STML.equals(schKind)||
			      YmCommonConst.NEW_SCH_WORK_KIND_STM2.equals(schKind)) {// 동간이적상차
			return true;
		} else if (YmCommonConst.NEW_SCH_WORK_KIND_SCLI.equals(schKind)) {// CTC보급
			if ("CT".equals(toLoc.substring(2, 4))) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * 스케쥴 작업종류에 따른 주작업 대상을 리턴한다.
	 * 
	 * @param wbookInfo
	 *            작업예약 데이터
	 * @return
	 */
	private List getMainCSWork(JDTORecord wbookInfo) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		String wbookId = getField(wbookInfo, "WBOOK_ID");
		return ymCommonDAO.readMainCSWorkInfo(wbookId, YmCommonConst.ITEM_SM);
	}

	/**
	 * 스케쥴기준 활성상태가 사용금지인지 확인한다.
	 * 
	 * @param ruleStat
	 *            스케쥴기준 활성상태
	 * @return
	 */
	private boolean notValidSchRule(String ruleStat) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		if (YmCommonConst.SCH_RULE_STAT_X.equals(ruleStat)) {
			return true;
		} else if (YmCommonConst.SCH_RULE_ACTIVE_STAT_B.equals(ruleStat)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 스케쥴기준 활성상태가 사용금지인지 확인한다.
	 * 
	 * @param mainCSWork
	 *            주작업정보
	 * @return
	 */
	private boolean notValidSchRule(JDTORecord mainCSWork) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		String ruleStat = getField(mainCSWork, "SCH_RULE_ACTIVE_STAT");
		if (YmCommonConst.SCH_RULE_STAT_X.equals(ruleStat)) {
			return true;
		} else if (YmCommonConst.SCH_RULE_ACTIVE_STAT_B.equals(ruleStat)) {
			String yd = getField(mainCSWork, "YD_GP");
			String bay = getField(mainCSWork, "BAY_GP");
			String alter = getField(mainCSWork, "SCH_RULE_ALTER_CRANE_NO");
			JDTORecord dto = ymCommonDAO.readEquipInfo(yd + bay + "CR" + alter);
			
			if ("C".equals(getField(dto, "EQUIP_STAT")) || "C".equals(getField(dto, "WORK_MODE"))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 대차상차 주작업에 대한 대차 하차 정지 위치를 확인하여 'CTC보급'에 따른 순서를 정렬한다.
	 * 
	 * @param ydSchDAO
	 * @param mainCSWork
	 *            주작업 리스트
	 */
	private void mainCSWorksOrderBy(List csWorks, String bay, String schKind) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
		if (isVicCarLoadWork(schKind)) {
			if (YmCommonConst.BAY_GP_C.equals(bay)) {
				logger.println(LogLevel.DEBUG, this, "Walking Beam 보급 순서 정렬.");
				Collections.reverse(csWorks);
			}
		}
	}

	/**
	 * 각각의 주작업에 대한 보조작업을 결정하여 스케쥴 작업 대상을 리턴한다. 1. 보조작업은 야드일 경우만 고려한다.
	 * 
	 * @param mainCSWorks
	 *            주작업 리스트.
	 * @return List 작업대상
	 */
	private List getWorkTargetInfo(List mainCSWorks, String schKind) {
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		logger.println(LogLevel.DEBUG, this, "SLAB SCH =>주/보조작업 대상재 검색 ");
		List csWorks = new ArrayList();
		List tempWorks = new ArrayList();
		JDTORecord mainCSWork = null;
		String mainStockId = null;
		String fromLoc = null;

		logger.println(LogLevel.DEBUG, this, "SLAB SCH =>주작업대상수=>" + mainCSWorks.size());

		for (int i = 0; i < mainCSWorks.size(); i++) {
			mainCSWork = (JDTORecord) mainCSWorks.get(i);
			fromLoc = getField(mainCSWork, "FORM_LOC");
			mainStockId = getField(mainCSWork, "STOCK_ID");

			logger.println(LogLevel.DEBUG, this, "SLAB SCH =>MAIN-" + (i + 1)+ "=>" + fromLoc + "/" + mainStockId);

			setSubWork(csWorks, tempWorks, mainStockId, schKind, fromLoc);
			tempWorks.add(mainStockId);
			csWorks.add(mainCSWork);
		}

		return csWorks;
	}

	/**
	 * 주작업에 대한 보조작업 대상을 셋팅한다.
	 * 
	 * @param cs			작업대상
	 * @param temp			비교대상
	 * @param mainStockId	주작업 저장품ID
	 * @param schKind		스케쥴종류
	 */
	private void setSubWork(List cs, List temp, String mainStockId,	String schKind, String from) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
		List subCSWorks = null;
		JDTORecord subCSWork = null;
		String subStockId = null;
		int subCSWorkQuentyCnt = 0;
		if (isSubWork(schKind, from)) {
			subCSWorks = ymCommonDAO.readSubCSWorkInfo(mainStockId);
			logger.println(LogLevel.DEBUG, this, "SLAB SCH =>보조작업대상수=>"	+ subCSWorks.size());
			subCSWorkQuentyCnt = subCSWorks != null ? subCSWorks.size() : 0;
			for (int i = 0; i < subCSWorkQuentyCnt; i++) {
				subCSWork = (JDTORecord) subCSWorks.get(i);
				subStockId = getField(subCSWork, "STOCK_ID");
				logger.println(LogLevel.DEBUG, this, "SLAB SCH =>BEFOR-SUB-" + (i + 1) + "=>" + subStockId);
				if (!temp.contains(subStockId)) {
					temp.add(subStockId);
					cs.add(subCSWork);
					logger.println(LogLevel.DEBUG, this, "SLAB SCH =>AFTER-SUB-" + (i + 1) + "=>" + subStockId);
				}
			}
		}
	}

	/**
	 * 작업대상에 대한 FROM MAP을 UPDATE
	 * 
	 * @param workTarget
	 *            작업대상
	 */
	private void editStackStatOfLayer(List workTarget) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
		for (int i = 0; i < workTarget.size(); i++) {
			ymCommonDAO.modifyStackStateOfLayer(
					YmCommonConst.STACK_LAYER_STAT_U, getField(
							(JDTORecord) workTarget.get(i), "STACK_COL_GP"),
					getField((JDTORecord) workTarget.get(i), "STACK_BED_GP"),
					getField((JDTORecord) workTarget.get(i), "STACK_LAYER_GP"));
		}
	}

	private void editStackStatOfLayerPer(JDTORecord workTarget, String sLayerStat) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
		ymCommonDAO.modifyStackStateOfLayer(sLayerStat
										  , getField(workTarget,"STACK_COL_GP")
										  , getField(workTarget, "STACK_BED_GP")
										  , getField(workTarget, "STACK_LAYER_GP"));
	}

	/**
	 * 작업대상에 대해서 To 위치를 셋팅한다.
	 */
	private void editStackStatOfLayer(List csWorkTarget, Map toLoc) {

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
		JDTORecord work = null;
		JDTORecord toLocDefine = null;
		String sStockId = "";

		for (int i = 0; i < csWorkTarget.size(); i++) {

			work = (JDTORecord) csWorkTarget.get(i);
			sStockId = getField(work, "STOCK_ID");
			toLocDefine = (JDTORecord) toLoc.get("" + i);

			/**
			 * 적치단 테이블 '저장품ID', '적치상태' UPDATE
			 */
			String sColGp = getField(toLocDefine, "STACK_COL_GP");
			String sBedGp = getField(toLocDefine, "STACK_BED_GP");
			String sLayerGp = getField(toLocDefine, "STACK_LAYER_GP");

			ymCommonDAO.modifyStockStatOfLayer(sStockId,
					YmCommonConst.STACK_LAYER_STAT_P, sColGp, sBedGp, sLayerGp);
			/*
			 * B열연 Slab 바로 위 상단 상태정보를 UPDATE
			 */
			int iReq = YmCommonDB.setSlabUpperState_E(sColGp, sBedGp, sLayerGp);
		}
	}

	/**
	 * 작업대상에 대해서 To 위치를 셋팅한다.
	 */
	private void editStackStatOfLayerPer(JDTORecord work, JDTORecord toLocDefine) {

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
		String sStockId = getField(work, "STOCK_ID");
		/**
		 * 적치단 테이블 '저장품ID', '적치상태' UPDATE
		 */
		String sColGp = getField(toLocDefine, "STACK_COL_GP");
		String sBedGp = getField(toLocDefine, "STACK_BED_GP");
		String sLayerGp = getField(toLocDefine, "STACK_LAYER_GP");

		ymCommonDAO.modifyStockStatOfLayer(sStockId, YmCommonConst.STACK_LAYER_STAT_P, sColGp, sBedGp, sLayerGp);
		/*
		 * B열연 Slab 바로 위 상단 상태정보를 UPDATE
		 */
		int iReq = YmCommonDB.setSlabUpperState_E(sColGp, sBedGp, sLayerGp);

	}

	/**
	 * 작업대상에 대하여 To 위치 결정을 위한 적치열을 리턴한다.
	 * 
	 * @param csWorkTarget
	 *            작업대상 리스트
	 * @return Map 작업대상에 대한 TO 위치
	 */
	private Map getMainAndSubStockCol(List csWorkTarget) throws Exception {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		logger.println(LogLevel.DEBUG, this, "SLAB SCH =>To 영역 주/보조작업 적치 열 검색 ");
		Map toLoc = new HashMap();

		JDTORecord work = null;
		String mainSubGp = "";
		String preLoc = "";

		for (int i = 0; i < csWorkTarget.size(); i++) {
			work = (JDTORecord) csWorkTarget.get(i);
			mainSubGp = getField(work, "IS_MAIN");
			if (YmCommonConst.MAIN.equals(mainSubGp)) {
				if (getMainStockCol(work, preLoc, toLoc, i) == null) {
					logger.println(LogLevel.DEBUG, this, "SLAB SCH =>주작업 적치열 검색 실패: 스케쥴 기준 및 저장영역 검색기준 확인요망");
				}
			} else if (YmCommonConst.SUB.equals(mainSubGp)) {
				if (getSubStockCol(work, toLoc, i) == null) {
					logger.println(LogLevel.DEBUG, this, "SLAB SCH =>보조작업 적치열 검색 실패: 해당 적치열에 적치가능 정보 체크 요망");
				}
			}
		}
		return toLoc;
	}

	/**
	 * 주작업 적치열을 리턴한다.
	 * 
	 * @param ydSchDAO
	 * @param work		현재 작업대상
	 * @param preLoc	이전 작업의 TO 위치
	 * @param toLoc		검색한 적치열을 저장할 Map
	 * @param i 		Map KEY
	 * @return List 	주작업 적치열
	 */
	private List getMainStockCol(JDTORecord work, String preLoc, Map toLoc,	int i) {

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		List mainColLst = null;
		String sStockId = "";
		if (!preLoc.equals(getField(work, "SEARCH_LOC"))) {
			preLoc = getField(work, "SEARCH_LOC");
			sStockId = getField(work, "STOCK_ID");

			logger.println(LogLevel.DEBUG, this, "SLAB SCH =>주작업 적치열 검색=" + sStockId);

			mainColLst = getMainCol(work);
			if (mainColLst == null || mainColLst.size() == 0) {
				return null;
			}
			toLoc.put("" + i, mainColLst);
			logger.println(LogLevel.DEBUG, this, "SLAB SCH =>주작업 적치열 검색수=" + mainColLst.size());
		}
		return mainColLst;
	}

	/**
	 * 주작업의 적치열을 리턴한다. 
	 * 1. 작업예약이 오퍼레이터 지정일 경우 해당 TO 위치가 1.1 6자리이면 이동위치 검색을 하고, 10자리이면 이동위치 검색을 하지 않는다.
	 * 2. 작업예약이 스케쥴 지정일 경우 이동위치 검색을 한다.
	 * 
	 * @param work		현재 작업대상
	 * @param ydSchDAO
	 * @return List 	주작업의 적치열
	 */
	private List getMainCol(JDTORecord work) {
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		String decision = getField(work, "SCH_WORK_LOC_DECISION_METHOD");
		if (YmCommonConst.SCH_WORK_LOC_DECISION_METHOD_O.equals(decision)) {

			return getSlabColLocPutList(getField(work, "CRANE_WORD_PUT_LOC"));
		} else {
			return ymCommonDAO.readMainStockCol(getField(work,"YD_GP"),
												getField(work,"BAY_GP"),
												getField(work,"STACK_COL_USAGE_CD"),
												getField(work,"SCH_WORK_KIND"), 
												getField(work,"STOCK_MOVE_TERM"));
		}
	}

	/**
	 * @param String :	PUT위치(10,8,6,4자리)
	 * @return JDTORecord
	 * @throws Exception
	 */
	private List getSlabColLocPutList(String sPutLoc) {
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		List rList = new ArrayList();

		JDTORecord jRecord = JDTORecordFactory.getInstance().create();

		try {

			if (sPutLoc.length() >= 4) {
				if (sPutLoc.length() >= 6) {
					jRecord.setField("STACK_COL_GP", sPutLoc.substring(0, 6));
				} else {
					jRecord.setField("STACK_COL_GP", sPutLoc.substring(0, 4));
				}
			}

			if (sPutLoc.length() >= 8) {

				jRecord.setField("STACK_BED_GP", sPutLoc.substring(6, 8));
			}

			if (sPutLoc.length() >= 10) {

				jRecord.setField("STACK_LAYER_GP", sPutLoc.substring(8, 10));
			}

			rList.add(jRecord);

		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return rList;
	}

	/*
	 * 시편재 및 핸드 스카핑재인지를 체크한다. 시편재이면 저장품이동조건을 'D1' 핸드스카핑재이면 저장품이동조건을 'D2' 로 셋팅 후
	 * TO위치를 검색한다.
	 * 
	 * REAGENTPICK_TARGET_YN, -- 시편채취유무 REAGENTPICK_DONE_YN, -- 시편완료유무
	 * SCARFING_YN, -- Scarfing유무 SCARFING_DONE_YN -- Scarfing완료유무
	 */
	private List getReagentPickScarfingInfo(List mainCSWorks, String schKind) {

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		List csWorks = new ArrayList();

		JDTORecord mainCSWork 	= null;
		JDTORecord work 		= null;
		JDTORecord slabJr 		= null;

		String sStockId = "";

		boolean isTrue0 = false; // 스카핑재
		boolean isTrue1 = false; // 시편재
		boolean isTrue2 = false; // 핸드스카핑재
		boolean isTrue3 = false; // 보류재
		boolean isTrue4 = false; // 주편(A)
		boolean isTrue5 = false; // 압연지시대상재

		String sCurrProgCd 			= "";
		String sReagentPickYn 		= "";
		String sReagentPickDoneYn 	= "";
		String sScarfingYn 			= "";
		String sScarfingDoneYn 		= "";
		String sScarfingPattern 	= "";
		String sHcrGp 				= "";
		String sIngrStampGrade		= "";
		String sSlabWoRtCd			= "";
		String sChargeLotNo         = "";
		String sC3_SCARF_TRF_YN     = "";
		
		int iSeq = 0;

		for (int i = 0; i < mainCSWorks.size(); i++) {

			// 초기화
			{
				isTrue0 = false; // 스카핑재
				isTrue1 = false; // 시편재
				isTrue2 = false; // 핸드스카핑재
				isTrue3 = false; // 보류재
				isTrue4 = false; // 주편(A)
				isTrue5 = false; // 압연지시대상재)
				sCurrProgCd 		= "";
				sReagentPickYn 		= "";
				sReagentPickDoneYn 	= "";
				sScarfingYn 		= "";
				sScarfingDoneYn 	= "";
				sScarfingPattern 	= "";
				sHcrGp 				= "";
				sSlabWoRtCd			= "";
				sChargeLotNo        = "";
			}

			work = (JDTORecord) mainCSWorks.get(i);
			sStockId = getField(work, "STOCK_ID");

			slabJr = dao.getSlabCommonInfo(sStockId);

			if (slabJr != null) {
				sCurrProgCd 		= StringHelper.evl(slabJr.getFieldString("CURR_PROG_CD"), "");
				sReagentPickYn 		= StringHelper.evl(slabJr.getFieldString("REAGENTPICK_TARGET_YN"), "");
				sReagentPickDoneYn 	= StringHelper.evl(slabJr.getFieldString("REAGENTPICK_DONE_YN"), "");
				sScarfingYn 		= StringHelper.evl(slabJr.getFieldString("SCARFING_YN"), "");
				sScarfingDoneYn 	= StringHelper.evl(slabJr.getFieldString("SCARFING_DONE_YN"), "");
				sScarfingPattern 	= StringHelper.evl(slabJr.getFieldString("WO_MSLAB_RPR_MTD"), "");
				sHcrGp 				= StringHelper.evl(slabJr.getFieldString("ORD_HCR_GP"), "");
				sIngrStampGrade		= StringHelper.evl(slabJr.getFieldString("INGR_STAMP_GRADE"), "");
				sSlabWoRtCd			= StringHelper.evl(slabJr.getFieldString("SLAB_WO_RT_CD"), "");
				sChargeLotNo		= StringHelper.evl(slabJr.getFieldString("CHARGE_LOT_NO"), "");
				sC3_SCARF_TRF_YN	= StringHelper.evl(slabJr.getFieldString("C3_SCARF_TRF_YN"), "");//chito 2016.08.30
			}
			
			logger.println(LogLevel.DEBUG, this,"시편재/핸드스카핑재 체크 sCurrProgCd			=" + sCurrProgCd);
			logger.println(LogLevel.DEBUG, this,"시편재/핸드스카핑재 체크 sReagentPickYn		=" + sReagentPickYn);
			logger.println(LogLevel.DEBUG, this,"시편재/핸드스카핑재 체크 sReagentPickDoneYn	=" + sReagentPickDoneYn);
			logger.println(LogLevel.DEBUG, this,"시편재/핸드스카핑재 체크 sScarfingYn			=" + sScarfingYn);
			logger.println(LogLevel.DEBUG, this,"시편재/핸드스카핑재 체크 sScarfingDoneYn		=" + sScarfingDoneYn);
			logger.println(LogLevel.DEBUG, this,"시편재/핸드스카핑재 체크 sScarfingPattern	=" + sScarfingPattern);
			logger.println(LogLevel.DEBUG, this,"WCR/CCR 구분							=" + sHcrGp);
			logger.println(LogLevel.DEBUG, this,"성분판정등급								=" + sIngrStampGrade);
			logger.println(LogLevel.DEBUG, this,"SLAB지시행선 							=" + sSlabWoRtCd);
			logger.println(LogLevel.DEBUG, this,"C3#핸드작업 지시여부 						=" + sC3_SCARF_TRF_YN);//chito 2016.08.30
			
			
			if(YmCommonConst.NEW_SCH_WORK_KIND_SVMU.equals(schKind)){
			// B열연 Slab 이송하차인 경우만 해당	
			
				/*
				 * 스카핑 체크
				 */
				{
					if (YmCommonConst.SCARFING_Y.equals(sScarfingYn)
							&& !YmCommonConst.SCARFING_Y.equals(sScarfingDoneYn)
							&& (!"S".equals(sScarfingPattern)&&!"G".equals(sScarfingPattern)&&
								!"X".equals(sScarfingPattern)&&!"Y".equals(sScarfingPattern)&& 	
							    !"Q".equals(sScarfingPattern)&&!"Y".equals(sC3_SCARF_TRF_YN) )) {//chito 2016.08.30
	
						isTrue0 = true;
						logger.println(LogLevel.DEBUG, this, "스카핑 체크 YES");
					} else {
						logger.println(LogLevel.DEBUG, this, "스카핑 체크 NO");
					}
				}
	
				/*
				 * 시편재 체크
				 */
				{
					if (YmCommonConst.SCARFING_Y.equals(sReagentPickYn)
							&& !YmCommonConst.SCARFING_Y.equals(sReagentPickDoneYn)) {
	
						isTrue1 = true;
						logger.println(LogLevel.DEBUG, this, "시편재 체크 YES");
					} else {
						logger.println(LogLevel.DEBUG, this, "시편재 체크 NO");
					}
				}
	
				/*
				 * 핸드스카핑 체크
				 */
				{
					if (YmCommonConst.SCARFING_Y.equals(sScarfingYn)
							&& !YmCommonConst.SCARFING_Y.equals(sScarfingDoneYn)
							&& ("S".equals(sScarfingPattern) ||"G".equals(sScarfingPattern)|| 
								"X".equals(sScarfingPattern) ||"Y".equals(sScarfingPattern)|| 
								"Q".equals(sScarfingPattern) ||"Y".equals(sC3_SCARF_TRF_YN)   //chito 2016.08.30							 
							  )) {
	
						isTrue2 = true;
						logger.println(LogLevel.DEBUG, this, "핸드스카핑 체크 YES");
					} else {
						logger.println(LogLevel.DEBUG, this, "핸드스카핑 체크 NO");
					}
				}
				
				/*
				 * 압연작업대상재 체크
				 */
				{
					if (YmCommonConst.SCARFING_Y.equals(sChargeLotNo)) {
	
						isTrue5 = true;
						logger.println(LogLevel.DEBUG, this, "압연작업대상재 체크 YES");
					} else {
						logger.println(LogLevel.DEBUG, this, "압연작업대상재 체크 NO");
					}
				}
				
				// 2008.10.10 YJK 시편재 우선으로 순위 변경
				try {
	
					if (isTrue0) {
						iSeq = dao.updateStockTransInfo(sStockId, // 저장품ID
								YmCommonConst.NEW_STOCK_MOVE_TERM_DS); // 저장품이동조건
	
						work.setField("STOCK_MOVE_TERM",YmCommonConst.NEW_STOCK_MOVE_TERM_DS);
					}
	
					if (isTrue2) {
	
						iSeq = dao.updateStockTransInfo(sStockId, // 저장품ID
								YmCommonConst.NEW_STOCK_MOVE_TERM_D3); // 저장품이동조건
	
						work.setField("STOCK_MOVE_TERM",YmCommonConst.NEW_STOCK_MOVE_TERM_D3);
					}
					
					if (isTrue1) {
						iSeq = dao.updateStockTransInfo(sStockId, // 저장품ID
								YmCommonConst.NEW_STOCK_MOVE_TERM_D2); // 저장품이동조건
	
						work.setField("STOCK_MOVE_TERM",YmCommonConst.NEW_STOCK_MOVE_TERM_D2);
					}
					
					if (isTrue5) {
						iSeq = dao.updateStockTransInfo(sStockId, // 저장품ID
								YmCommonConst.NEW_STOCK_MOVE_TERM_FS); // 저장품이동조건
	
						work.setField("STOCK_MOVE_TERM",YmCommonConst.NEW_STOCK_MOVE_TERM_FS);
					}
						
				} catch (DAOException daoe) {
					throw daoe;
				} catch (Exception e) {
					throw new EJBServiceException(e);
				}
				
			}else if(YmCommonConst.NEW_SCH_WORK_KIND_SRLO.equals(schKind)||
					 YmCommonConst.NEW_SCH_WORK_KIND_SVML.equals(schKind)){
			// A열연 Slab ROT LINE OFF인 경우만 해당	
			
				try {
					if(YmCommonConst.CURR_PROG_CD_SLAB_E.equals(sCurrProgCd)){
						if(YmCommonConst.ORD_HCR_GP_V.equals(sHcrGp)||
	 					   YmCommonConst.ORD_HCR_GP_H.equals(sHcrGp)||
						   YmCommonConst.ORD_HCR_GP_W.equals(sHcrGp)){
							
							if(YmCommonConst.SLAB_WO_RT_CD_PA.equals(sSlabWoRtCd)){
								iSeq = dao.updateStockTransInfo(sStockId, 								// 저장품ID
									     YmCommonConst.NEW_STOCK_MOVE_TERM_B0); 						// 후판WCR재추출
								work.setField("STOCK_MOVE_TERM",YmCommonConst.NEW_STOCK_MOVE_TERM_B0);
							}else if(YmCommonConst.SLAB_WO_RT_CD_HB.equals(sSlabWoRtCd)){
								iSeq = dao.updateStockTransInfo(sStockId, 								// 저장품ID
									     YmCommonConst.NEW_STOCK_MOVE_TERM_B1); 						// B열연WCR재추출
								work.setField("STOCK_MOVE_TERM",YmCommonConst.NEW_STOCK_MOVE_TERM_B1);
							}else if(YmCommonConst.SLAB_WO_RT_CD_HC.equals(sSlabWoRtCd)){
								iSeq = dao.updateStockTransInfo(sStockId, 								// 저장품ID
									     YmCommonConst.NEW_STOCK_MOVE_TERM_B4); 						// C열연WCR재추출
								work.setField("STOCK_MOVE_TERM",YmCommonConst.NEW_STOCK_MOVE_TERM_B4);
							}
						}else if(YmCommonConst.ORD_HCR_GP_C.equals(sHcrGp)){
							
							if(YmCommonConst.SLAB_WO_RT_CD_PA.equals(sSlabWoRtCd)){
								iSeq = dao.updateStockTransInfo(sStockId, 								// 저장품ID
									     YmCommonConst.NEW_STOCK_MOVE_TERM_B3); 						// 후판CCR재추출
								work.setField("STOCK_MOVE_TERM",YmCommonConst.NEW_STOCK_MOVE_TERM_B3);
							}else if(YmCommonConst.SLAB_WO_RT_CD_HB.equals(sSlabWoRtCd)){
								iSeq = dao.updateStockTransInfo(sStockId, 								// 저장품ID
									     YmCommonConst.NEW_STOCK_MOVE_TERM_B2); 						// B열연CCR재추출
								work.setField("STOCK_MOVE_TERM",YmCommonConst.NEW_STOCK_MOVE_TERM_B2);
							}else if(YmCommonConst.SLAB_WO_RT_CD_HC.equals(sSlabWoRtCd)){
								iSeq = dao.updateStockTransInfo(sStockId, 								// 저장품ID
									     YmCommonConst.NEW_STOCK_MOVE_TERM_B5); 						// C열연CCR재추출
								work.setField("STOCK_MOVE_TERM",YmCommonConst.NEW_STOCK_MOVE_TERM_B5);
							}
						}
					}
					/*
					 * 보류재 체크
					 */
					{
						if ("".equals(sIngrStampGrade)) {
		
							isTrue2 = true;
							logger.println(LogLevel.DEBUG, this, "보류재 YES");
						} else {
							logger.println(LogLevel.DEBUG, this, "보류재 NO");
						}
					}
					
					/*
					 * 스카핑 체크
					 */
					{
						if (YmCommonConst.SCARFING_Y.equals(sScarfingYn)
						    && !YmCommonConst.SCARFING_Y.equals(sScarfingDoneYn)) {
							isTrue0 = true;
							logger.println(LogLevel.DEBUG, this, "스카핑 체크 YES");
						} else {
							logger.println(LogLevel.DEBUG, this, "스카핑 체크 NO");
						}
					}
		
					/*
					 * 시편재 체크
					 */
					{
						if (YmCommonConst.SCARFING_Y.equals(sReagentPickYn)
						    && !YmCommonConst.SCARFING_Y.equals(sReagentPickDoneYn)) {
		
							isTrue1 = true;
							logger.println(LogLevel.DEBUG, this, "시편재 체크 YES");
						} else {
							logger.println(LogLevel.DEBUG, this, "시편재 체크 NO");
						}
					}
					
					/*
					 * 주편(A) 체크
					 */
					{
						if(YmCommonConst.CURR_PROG_CD_SLAB_A.equals(sCurrProgCd)){
						    
							isTrue4 = true;
							logger.println(LogLevel.DEBUG, this, "주편(A) 체크 YES");
						} else {
							logger.println(LogLevel.DEBUG, this, "주편(A) 체크 NO");
						}
					}
					
					
					if (isTrue2) {
	
						iSeq = dao.updateStockTransInfo(sStockId, // 저장품ID
								YmCommonConst.NEW_STOCK_MOVE_TERM_D4); // 저장품이동조건
	
						work.setField("STOCK_MOVE_TERM",YmCommonConst.NEW_STOCK_MOVE_TERM_D4);
					}
					
					if (isTrue0) {
						iSeq = dao.updateStockTransInfo(sStockId, // 저장품ID
								YmCommonConst.NEW_STOCK_MOVE_TERM_DS); // 저장품이동조건
	
						work.setField("STOCK_MOVE_TERM",YmCommonConst.NEW_STOCK_MOVE_TERM_DS);
					}
	
					if (isTrue1) {
						iSeq = dao.updateStockTransInfo(sStockId, // 저장품ID
								YmCommonConst.NEW_STOCK_MOVE_TERM_D2); // 저장품이동조건
	
						work.setField("STOCK_MOVE_TERM",YmCommonConst.NEW_STOCK_MOVE_TERM_D2);
					}
					
					if (isTrue4) {
						iSeq = dao.updateStockTransInfo(sStockId, // 저장품ID
								YmCommonConst.NEW_STOCK_MOVE_TERM_AS); // 저장품이동조건
	
						work.setField("STOCK_MOVE_TERM",YmCommonConst.NEW_STOCK_MOVE_TERM_AS);
					}
					
				} catch (DAOException daoe) {
					throw daoe;
				} catch (Exception e) {
					throw new EJBServiceException(e);
				}
			}
			
			csWorks.add(work);

		}

		return csWorks;
	}

	/**
	 * 보조작업 적치열을 리턴한다.
	 * 
	 * @param ydSchDAO
	 * @param work
	 *            현재 작업대상
	 * @param toLoc
	 *            검색한 적치열을 저장할 Map
	 * @param i
	 *            Map KEY
	 * @return List 보조작업 적치열
	 */
	private List getSubStockCol(JDTORecord work, Map toLoc, int i) {
		logger.println(LogLevel.DEBUG, this, "SLAB SCH =>보조작업 적치열 검색="	+ getField(work, "STOCK_ID"));

		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		String sStackColGp = getField(work, "STACK_COL_GP");
		String sYdGp = sStackColGp.substring(0, 1);
		String sBayGp = sStackColGp.substring(1, 2);
		String sSectGp = sStackColGp.substring(2, 4);
		String sColGp = sStackColGp.substring(4, 6);

		if ("TC".equals(sSectGp)) {
			// 설비일 경우 보조작업 없슴
			return null;
		}

		List subColLst = dao.getCoilColLocSubList_02(sYdGp, // 야드구분
				sBayGp, // 동구분
				sSectGp, // SPAN구분
				sColGp, // 열구분
				sStackColGp);

		if (subColLst == null || subColLst.size() == 0) {
			return null;
		}

		toLoc.put("" + i, subColLst);
		logger.println(LogLevel.DEBUG, this, "SLAB SCH =>보조작업 적치열 검색="	+ subColLst.size());
		return subColLst;
	}

	/**
	 * 작업대상에 대해서 To 위치를 셋팅한다.
	 * 
	 * @param ydSchDAO
	 * @param csWorkTarget 	작업대상
	 * @param toLoc 		TO 적치열
	 * @return 				TO 위치
	 */
	private boolean getToStoreLoc(JDTORecord mainCSWork, List csWorkTarget,	Map toLoc, String bendingYN) throws Exception {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		boolean isSuccess = true;
		List toList = null;
		JDTORecord work = null;
		JDTORecord toLocDefine = null;
		String stockId = null;

		for (int i = 0; i < csWorkTarget.size(); i++) {

			work = (JDTORecord) csWorkTarget.get(i);
			stockId = getField(work, "STOCK_ID");
			toList = (List) toLoc.get("" + i);

			logger.println(LogLevel.DEBUG, this, "▶▶▶▶▶▶▶▶▶▶SLAB SCH =>작업대상 TO위치 검색◀◀◀◀◀◀◀◀◀◀"	+ stockId);
			/**
			 * FROM 위치 'U' 셋팅.
			 */
			setPreStackLayer(work);

			toLocDefine = getToLocDefine(mainCSWork, stockId, toList, work, bendingYN);

			if (toLocDefine == null) {
				logger.println(LogLevel.DEBUG, this,"SLAB SCH =>적치가능 TO 위치 검색실패=" + stockId);

				/**
				 * FROM 위치 'S/L' 셋팅.
				 */
				setRollBackStackLayer(work);
				return false;
			}

			/**
			 * TO 위치 'P' 셋팅.
			 */
			editStackStatOfLayerPer(work, toLocDefine);
			logger.println(LogLevel.DEBUG, this, "SLAB SCH =>작업대상 TO위치 셋팅="	+ stockId);

			/**
			 * C/R 확인
			 * 
			 */
			mainCSWork = convertMain(mainCSWork, work, toLocDefine,bendingYN);
			logger.println(LogLevel.DEBUG, this, "Crane Main => " + getField(mainCSWork, "SCH_WORK_EQUIP_NO"));		
			/**
			 * 스케쥴등록
			 */
			//createSchedulePer(mainCSWork, work, toLocDefine);
			
			EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
 			ejbConn.trx( "createSchedulePer",new  Class[]{JDTORecord.class,JDTORecord.class,JDTORecord.class},
 											 new Object[]{mainCSWork, work, toLocDefine });
 			
			logger.println(LogLevel.DEBUG, this, "SLAB SCH =>작업대상 스케쥴 셋팅="	+ stockId);

		}
		return isSuccess;
	}

	/**
	 * 현재 저장품을 'U' 셋팅한다.
	 * 
	 * @param work
	 *            스케쥴정보
	 * @return
	 */
	private void setPreStackLayer(JDTORecord work) {

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
		String stockId = getField(work, "STOCK_ID");

		editStackStatOfLayerPer(work, YmCommonConst.STACK_LAYER_STAT_U);
		logger.println(LogLevel.DEBUG, this, "SLAB SCH =>저장품 U 셋팅=" + stockId);
	}

	/**
	 * 현재 저장품을 초기화한다.
	 * 
	 * @param work
	 *            스케쥴정보
	 * @return
	 */
	private void setRollBackStackLayer(JDTORecord work) {

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
		String subMainGp = getField(work, "IS_MAIN");
		String stockId = getField(work, "STOCK_ID");
		String sLayerStat = "";

		if (YmCommonConst.MAIN.equals(subMainGp)) {
			sLayerStat = YmCommonConst.STACK_LAYER_STAT_S;
		} else if (YmCommonConst.SUB.equals(subMainGp)) {
			sLayerStat = YmCommonConst.STACK_LAYER_STAT_L;
			/**
			 * 작업예약이 존재하면 'S'
			 */
			JDTORecord stockJr = dao.getStockInfo(stockId);
			if (stockJr != null) {
				String sWbookId = StringHelper.evl(stockJr.getFieldString("WBOOK_ID"), "");
				if (!"".equals(sWbookId)) {
					sLayerStat = YmCommonConst.STACK_LAYER_STAT_S;
				}
			}
		}
		editStackStatOfLayerPer(work, sLayerStat);
		logger.println(LogLevel.DEBUG, this, "SLAB SCH =>저장품 적치단 상태 백업="+ sLayerStat);
	}

	/**
	 * 스케쥴종류가 'W/B보급'인지 확인한다.
	 * 
	 * @param schKind
	 *            스케쥴종류
	 * @return
	 */
	private boolean isWBSupply(String schKind) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		if (YmCommonConst.NEW_SCH_WORK_KIND_SWLI.equals(schKind)) {
			return true;
		}
		return false;
	}

	/**
	 * 동간보급 대차상차 작업인지 확인한다.
	 * 
	 * @param workKind
	 *            스케쥴 작업종류
	 * @return
	 */
	private boolean isVicCarLoadWork(String workKind) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		if (YmCommonConst.NEW_SCH_WORK_KIND_STSL.equals(workKind)) {
			return true;
		}
		return false;
	}

	/**
	 * 스케쥴기준 테이블의 기준상태에 따른 CRANE NO를 리턴한다.
	 * 
	 * @param assign
	 *            스케쥴기준 데이터
	 * @param curWork
	 * @param schActiveStat
	 *            스케쥴기준 상태
	 * @return
	 * @throws Exception
	 */
	private String getSchWorkEquipNo(JDTORecord assign, JDTORecord curWork)
			throws Exception {
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		if (YmCommonConst.NEW_SCH_WORK_KIND_SWLI.equals(getField(curWork,
				"SCH_WORK_KIND"))
				|| YmCommonConst.NEW_SCH_WORK_KIND_SCLI.equals(getField(
						curWork, "SCH_WORK_KIND"))) {
			JDTORecord dto = ymCommonDAO.readCRRange(getField(curWork, "YD_GP")
					+ getField(curWork, "BAY_GP") + "CR"
					+ getSchWorkEquipNo(curWork), getField(curWork, "FORM_LOC")
					.substring(0, 6), getField(curWork, "FORM_LOC").substring(
					6, 8), getField(curWork, "FORM_LOC").substring(8, 10));
			if ("F".equals(getField(dto, "RESULT"))) {
				if ("C".equals(getField(curWork, "BAY_GP"))) {
					dto = ymCommonDAO.readEquipInfo("2CCRC1");
					if ("C".equals(getField(dto, "EQUIP_STAT"))) {
						throw new Exception("##### C1 크레인이 고장입니다.");
					}
				} else if ("B".equals(getField(curWork, "BAY_GP"))) {
					dto = ymCommonDAO.readEquipInfo("2BCRB1");
					if ("C".equals(getField(dto, "EQUIP_STAT"))) {
						throw new Exception("##### B1 크레인이 고장입니다.");
					}
				}
				return getField(curWork, "BAY_GP") + "1";
			} else {
				return getSchWorkEquipNo(assign);
			}
		} else {
			return getSchWorkEquipNo(assign);
		}
	}

	/**
	 * 스케쥴기준 테이블의 기준상태에 따른 CRANE NO를 리턴한다.
	 * 
	 * @param assign
	 *            스케쥴기준 데이터
	 * @param curWork
	 * @param schActiveStat
	 *            스케쥴기준 상태
	 * @return
	 */
	private String getSchWorkEquipNo(JDTORecord assign) {
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		String schActiveStat = getField(assign, "SCH_RULE_ACTIVE_STAT");
		if (YmCommonConst.SCH_RULE_ACTIVE_STAT_A.equals(schActiveStat)) {
			return getField(assign, "SCH_RULE_CRANE_NO");
		} else if (YmCommonConst.SCH_RULE_ACTIVE_STAT_B.equals(schActiveStat)) {
			return getField(assign, "SCH_RULE_ALTER_CRANE_NO");
		}
		return null;
	}

	/**
	 * 스케쥴기준 테이블의 기준상태에 따른 CRANE 우선순위를 리턴한다.
	 * 
	 * @param assign
	 *            스케쥴기준 데이터
	 * @return
	 */
	private String getSchWprefer(JDTORecord assign) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		String schActiveStat = getField(assign, "SCH_RULE_ACTIVE_STAT");
		if (YmCommonConst.SCH_RULE_ACTIVE_STAT_A.equals(schActiveStat)) {
			return getField(assign, "SCH_RULE_WPREFER");
		} else if (YmCommonConst.SCH_RULE_ACTIVE_STAT_B.equals(schActiveStat)) {
			return getField(assign, "SCH_RULE_ALTER_WPREFER");
		}
		return null;
	}

	/**
	 * 장입순서를 조절해야 하는 작업인지 확인하여 리턴한다.
	 * 
	 * @param schKind
	 *            스케쥴작업종류
	 * @return
	 */
	private boolean isDeseWokr(String schKind) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		if (YmCommonConst.NEW_SCH_WORK_KIND_SWLI.equals(schKind)) {
			return true;
		}
		return false;
	}

	/**
	 * 보조작업인지 확인하여 리턴한다.
	 * 
	 * @param schKind
	 *            스케쥴작업종류
	 * @return
	 */
	private boolean isSubWork(String schKind, String from) {
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		if (YmCommonConst.NEW_SCH_WORK_KIND_SVML.equals(schKind)) { // 이송상차
			return true;	//false이었으나 A열연 SLAB야드 이송상차시 보조작업 검색을 위해서 true로 수정.
		} else if (YmCommonConst.NEW_SCH_WORK_KIND_SVMU.equals(schKind)) { // 이송하차
			return false;
		} else if (YmCommonConst.NEW_SCH_WORK_KIND_SSLO.equals(schKind)) { // 스카핑추출
			return false;
		} else if (YmCommonConst.NEW_SCH_WORK_KIND_SWLI.equals(schKind)) { // W/B보급
			if ("TC".equals(from.substring(2, 4))) {
				return false;
			} else {
				return true;
			}
		} else if (YmCommonConst.NEW_SCH_WORK_KIND_SCLI.equals(schKind)) { // CTC보급
			if ("TC".equals(from.substring(2, 4))) {
				return false;
			} else {
				return true;
			}
		} else if (YmCommonConst.NEW_SCH_WORK_KIND_SHLO.equals(schKind)) { // H/B
																			// LINE
																			// OFF
			return false;
		} else if (YmCommonConst.NEW_SCH_WORK_KIND_SWTO.equals(schKind)) { // W/B
																			// TAKE
																			// OUT
			return false;
		} else if (YmCommonConst.NEW_SCH_WORK_KIND_STMU.equals(schKind)||
				YmCommonConst.NEW_SCH_WORK_KIND_STM4.equals(schKind)) { // 대차하차
			return false;
		}
		return true;
	}

      /**
	 * 오퍼레이션명 : 
	 *
	 * TO 위치를 결정하고 적치대/적치단 테이블에 관련 항목을 UPDATE 한다.
	 * 
	 * param ydSchDAO
	 * param stockId
	 *            현재 작업대상 저장품ID
	 * param toList
	 *            현재 작업대상 TO 적치열
	 * param work
	 *            현재 작업대상
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */           
	public JDTORecord getToLocDefine(JDTORecord assign, String sStockId, List toList,JDTORecord work ,String bendingYN) {

		String curColGp = "";
		String curBedGp = "";
		String curlayerGp = "";
        String schkind = "";
        
		JDTORecord curRecord = null;
		JDTORecord toLoc = null;

		int toListCnt = toList != null ? toList.size() : 0;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			logger.println(LogLevel.DEBUG, this, "SLAB TO LOC=>" + sStockId + "=> TO 위치 검색 START");

			for (int i = 0; i < toListCnt; i++) {

				curRecord = (JDTORecord) toList.get(i);

				curColGp = StringHelper.evl(curRecord.getFieldString("STACK_COL_GP"), "").trim();
				curBedGp = StringHelper.evl(curRecord.getFieldString("STACK_BED_GP"), "").trim();
				curlayerGp = StringHelper.evl(curRecord.getFieldString("STACK_LAYER_GP"), "").trim();
                		
                		logger.println(LogLevel.DEBUG, this, "SLAB TO LOC=>" + curColGp + "=> TO 위치 검색 START");
				/**
				 * 작업예약이 오퍼레이터 지정인지 구분한다. 
				 * 1. 오퍼레이터 지정이 10 자리이면 적치가능한 TO 위치를 찾지 않는다.
				 */
				if (!"".equals(curlayerGp)) {

					JDTORecord toTmp = getSlabColLocPutInfo(curColGp, curBedGp,curlayerGp);

					/*
					 * 2.1 스케쥴지정 10자리 TO위치가 적치중일때 해당 적치열에 적치가능위치를 검색한다.
					 */
					if (toTmp == null) {

						toLoc = ymCommonDAO.readToLocDefine(curColGp);

					} else {

						curRecord.setField("TO_LOC", curColGp + curBedGp + curlayerGp);
						toLoc = curRecord;
					}
					logger.println(LogLevel.DEBUG, this, "SLAB TO LOC=>" + sStockId + "=> 10자리 지정 =" + curColGp);

				} else if ("".equals(curlayerGp) && !"".equals(curBedGp)) {

					toLoc = ymCommonDAO.readBasicToLocInfo(curColGp, curBedGp);

					logger.println(LogLevel.DEBUG, this, "SLAB TO LOC=>" + sStockId + "=> 8자리 지정=" + curColGp);

				} else {

					JDTORecord dto = null;
					/**
					 * 1. 스케쥴코드가 스카핑보급인지 체크.
					 */
					if (isSupplyScarfing(getField(work, "SCH_WORK_KIND"))) {
						
						//bending 존 표기가 된경우
						if(bendingYN.equals("Y")){
							toLoc = ymCommonDAO.readToLocDefinebending(curColGp);
							logger.println(LogLevel.DEBUG, this, "SLAB TO LOC=>" + sStockId + "=> bending존 보급=" + curColGp);
						}else{						
							toLoc = ymCommonDAO.readToLocDefine(curColGp);
							logger.println(LogLevel.DEBUG, this, "SLAB TO LOC=>" + sStockId + "=> 스카핑보급=" + curColGp);
						}
						
						
						
						/**
						 * 2. TO위치가 설비인지를 체크한다.
						 */
					} else if (isEquipSch(getField(work, "SCH_WORK_KIND"), curColGp)) {
						/**
						 * 2.1 TO위치가 W/B, CTC인지를 체크한다.
						 */
						if (isZoinWork(curColGp)) {

							/**
							 * 2.1.1 현 대상재 장입번호를 보급할 수 있는지 체크
							 */
							String sValue = considerLotNo(work, curColGp);

							if ("1".equals(sValue)) {
								/**
								 * 2.1.2 현 대상재는 보급재
								 */
								toLoc = ymCommonDAO.readBasicToLocInfo(curColGp,YmCommonConst.STACK_BED_GP_01);

								/**
								 * 2.1.3 현 대상재는 보급재나 TO위치가 없음 => 대기
								 */
								if (toLoc == null) {
									logger.println(LogLevel.DEBUG, this, "SLAB TO LOC=> 현 보급대상재이지만 설비 TO LOC 실패=" 	+ sStockId);
									logger.println(LogLevel.DEBUG, this,"SLAB TO LOC=> 현재 위치에서 대기 TO LOC 검색 SKIP1");
									break;
								}
							} else if ("2".equals(sValue)) {
								/**
								 * 2.1.2 현 대상재는 보급재
								 */
								toLoc = null;

								/**
								 * 2.1.3 현 대상재는 보급재나 TO위치가 없음 => 대기
								 */
								if (toLoc == null) {
									logger.println(LogLevel.DEBUG, this, "SLAB TO LOC=> 현 보급대상재이지만 설비 TO LOC 실패=" 	+ sStockId);
									logger.println(LogLevel.DEBUG, this, "SLAB TO LOC=> 현재 위치에서 대기 TO LOC 검색 SKIP2");
									break;
								}
							} else {
								/**
								 * 2.1.3 현 대상재는 보급재 아님
								 */
								toLoc = null;
							}
							logger.println(LogLevel.DEBUG, this, "SLAB TO LOC=>" + sStockId + "=> W/B,CTC=" + curColGp);
						/**
						 * 2.2 TO위치가 보온카바(BK)인지를 체크한다.
						 */
						} else if ("BK".equals(curColGp.substring(2, 4))) { // B열연 A동 보온카바
							logger.println(LogLevel.DEBUG, this, "▶▶▶▶▶▶▶▶▶▶SLAB TO LOC=> 보온카바 TO위치 검색◀◀◀◀◀◀◀◀◀◀" );
							logger.println(LogLevel.DEBUG, this, "▶▶▶▶▶▶▶▶▶▶SLAB TO LOC=> 보온카바 TO위치 검색 => 01단 이상 동일장입순번검색◀◀◀◀◀◀◀◀◀◀" );
							toLoc = ymCommonDAO.readSlabToLocInfo(	sStockId, 
																curColGp, 
																YmCommonConst.SLAB_TO_LOC_G, 
																"",   // 스케쥴 코드 필요없슴
																"");  // 저장품 이동조건 필요없슴
							
							if (toLoc == null || toLoc.size() == 0) {
								
								logger.println(LogLevel.DEBUG, this, "▶▶▶▶▶▶▶▶▶▶SLAB TO LOC=> 보온카바 TO위치 검색 => 산적LOT코드로 검색◀◀◀◀◀◀◀◀◀◀" );
								//동일강종으로 검색 -> B열연 진행반 요청으로 산적LOT로 변경	(2010.03.16)							
								toLoc = ymCommonDAO.readSlabToLocInfo(	sStockId, 
																	curColGp, 
																	YmCommonConst.SLAB_TO_LOC_S, 
																	"",   // 스케쥴 코드 필요없슴
																	"");  // 저장품 이동조건 필요없슴
							}
							
							if (toLoc == null || toLoc.size() == 0) {
								
								logger.println(LogLevel.DEBUG, this, "▶▶▶▶▶▶▶▶▶▶SLAB TO LOC=> 보온카바 TO위치 검색 => 01단 빈베드검색◀◀◀◀◀◀◀◀◀◀" );
								toLoc = ymCommonDAO.readSlabToLocInfo(	sStockId, 
																	curColGp, 
																	YmCommonConst.SLAB_TO_LOC_E, 
																	"",   // 스케쥴 코드 필요없슴
																	"");  // 저장품 이동조건 필요없슴
							}
							
							
							/**
							 * 3.3 SLAB의 산적번호로 TO 위치 FAIL 시 중량,폭,길이,두께항목을 가지고
							 * 적치가능한 곳을 검색한다.
							 */
							if (toLoc == null || toLoc.size() == 0) {
								logger.println(LogLevel.DEBUG, this, "▶▶▶▶▶▶▶▶▶▶SLAB TO LOC=> 보온카바 TO위치 검색 => 폭,길이 참조 검색◀◀◀◀◀◀◀◀◀◀" );
								String ScheduelCode 	= getField(work,"SCH_WORK_KIND");
								
								toLoc = getBasicToLocInfo_03(sStockId, 	// 저장품ID
															 toList, 	// TO위치 LIST INFO
															 toListCnt,// TO위치 LIST COUNT
															 ScheduelCode); 

								logger.println(LogLevel.DEBUG, this,"SLAB TO LOC=>" + sStockId + "=> 폭,길이 참조 검색 ");
							}
							
							
							
							logger.println(LogLevel.DEBUG, this, "SLAB TO LOC=>" + sStockId + "=> 보온카바=" + curColGp+ "=> 스케쥴=" + 
															getField(work, "SCH_WORK_KIND")+ "=> 이동조건=" + getField(work, "STOCK_MOVE_TERM"));
						/**
						 * 2.3 TO위치가 차량,대차(TC)인지를 체크한다.
						 */
						} else {
							toLoc = ymCommonDAO.readToLocDefine(curColGp);
							logger.println(LogLevel.DEBUG, this, "SLAB TO LOC=>" + sStockId + "=> 차량,대차=" + curColGp);
						}
						/**
						 * 설비 고장,영역을 고려한다.
						 */
						if (toLoc != null && toLoc.size() != 0) {
							// JDTORecord equipJr =
							// considerEquipOnOff(getField(work,
							// "SCH_WORK_KIND"), curColGp);
							boolean isEquip = considerEquipOnOff(getField(work,"SCH_WORK_KIND"), curColGp);
							if (isEquip == false) {
								//20080513 YJK To위치 결정시 설비가 고장일때 계속해서 다음 To위치를 검색할 수 있도록 수정
								//return null;
								continue;
							}
							/**
							 * YJK TO 위치 영역 -아래 주석처리함.
							 * if(considerToCRRange(work, toLoc, toListCnt, i)) {
							 * continue; }
							 */ 
						}
						/**
						 * 3. TO위치가 야드인경우.
						 */
					} else {
						/**
						 * 3.1 SLAB의 장입LOT번호가 존재하지 않는 경우. (산적LOT번호 단위로 TO위치 결정)
						 */
						if ("".equals(getField(work, "CHARGE_LOT_NO"))) {
							
							/*
							 * 스케쥴코드 : 이송하차
							 * D동 D1, D2 	=> 1베드부터 적치
							 * D동 D3, E동 	=> 마지막베드부터 적치
							 * 이송하차 스케쥴코드를 넘기면 마지막 베드부터 적치
							 */
							
							String ScheduelCode 	= getField(work,"SCH_WORK_KIND");
							String sSelCraneNo 		= getSchWorkEquipNo(assign); 
							
							logger.println(LogLevel.DEBUG, this,"SLAB TO LOC 스케쥴코드=>" + ScheduelCode);
							logger.println(LogLevel.DEBUG, this,"SLAB TO LOC 크레인정보=>" + sSelCraneNo);
							
							//장애 발생시 이전 소스로 원복 하기 위한 조치
							String QueryId 	= "com.inisteel.cim.yd.dao.ydstkcoldao.YdStockDao.updYdchklist";
						    List sposYNChklist = ymCommonDAO.getCommonList(QueryId, new Object[]{});

						    JDTORecord unloadPointrec = (JDTORecord)sposYNChklist.get(0);
					    	String CHK   = StringHelper.evl(unloadPointrec.getFieldString("CHK"), "");
							
							if (YmCommonConst.NEW_SCH_WORK_KIND_SVMU.equals(ScheduelCode) && CHK.equals("Y")) {
								
								if ((YmCommonConst.NEW_SCH_WORK_KIND_SVMU.equals(ScheduelCode)&&"E".equals(curColGp.substring(1,2)))||
									    (YmCommonConst.NEW_SCH_WORK_KIND_SVMU.equals(ScheduelCode)&&"D3".equals(sSelCraneNo))) {
									logger.println(LogLevel.DEBUG, this,"SLAB TO LOC 스케쥴코드=>" + ScheduelCode); 
								}else {
									ScheduelCode="";
									logger.println(LogLevel.DEBUG, this,"SLAB TO LOC 스케쥴코드=>" + ScheduelCode); 
								}
									
									toLoc = getToLocOfStackLoc_03(sStockId, 		// 저장품ID
															    toList, 			// TO위치 LIST INFO
															    toListCnt,			// TO위치 LIST COUNT
															    ScheduelCode); 		// Schedule Code
								
							}else if ((YmCommonConst.NEW_SCH_WORK_KIND_SVMU.equals(ScheduelCode)&&"E".equals(curColGp.substring(1,2)))||
						    (YmCommonConst.NEW_SCH_WORK_KIND_SVMU.equals(ScheduelCode)&&"D3".equals(sSelCraneNo))) {
								
								toLoc = getToLocOfStackLoc_01(sStockId, 		// 저장품ID
														    toList, 			// TO위치 LIST INFO
														    toListCnt,			// TO위치 LIST COUNT
														    ScheduelCode); 		// Schedule Code
							
							}else if(YmCommonConst.NEW_SCH_WORK_KIND_SRLO.equals(ScheduelCode)) {
								
								toLoc = getToLocOfStackLoc_02(sStockId, 							// 저장품ID
														    toList, 								// TO위치 LIST INFO
														    toListCnt,								// TO위치 LIST COUNT
														    ScheduelCode, 							// Schedule Code
														    getField(work, "STOCK_MOVE_TERM")); 	// 저장품이동조건
																		
							}else{
								
								toLoc = getToLocOfStackLoc(	sStockId, 	// 저장품ID
															toList, 	// TO위치 LIST INFO
															toListCnt); // TO위치 LIST COUNT
							}			
							logger.println(LogLevel.DEBUG, this,"SLAB TO LOC=>" + sStockId + "=> 산적LOT 검색");
							
							/**
							 * 3.3 SLAB의 산적번호로 TO 위치 FAIL 시 중량,폭,길이,두께항목을 가지고
							 * 적치가능한 곳을 검색한다.
							 */
							if (toLoc == null || toLoc.size() == 0) {
								toLoc = getBasicToLocInfo_01(sStockId, 	// 저장품ID
															 toList, 	// TO위치 LIST INFO
															 toListCnt); // TO위치 LIST COUNT

								logger.println(LogLevel.DEBUG, this,"SLAB TO LOC=>" + sStockId + "=> 폭,길이 참조 검색 ");
							}

							/**
							 * 3.2 SLAB의 장입LOT번호가 존재하는 경우.
							 */
						} else {
							toLoc = getToLocPriority(sStockId, 	// 저장품ID
													 toList, 	// TO위치 LIST INFO
													 toListCnt); // TO위치 LIST COUNT

							logger.println(LogLevel.DEBUG, this, "SLAB TO LOC=>" + sStockId + "=> 장입LOT 검색");
						}

						/**
						 * 3.3 SLAB의 장입순번,산적번호로 TO 위치 FAIL 시 중량,폭,길이,두께항목을 가지고
						 * 적치가능한 곳을 검색한다.
						 */ 
						if (toLoc == null || toLoc.size() == 0) {
							/*
							toLoc = getBasicToLocInfo_01(sStockId, // 저장품ID
									toList, // TO위치 LIST INFO
									toListCnt); // TO위치 LIST COUNT
							*/
							logger.println(LogLevel.DEBUG, this,"SLAB TO LOC=>" +  sStockId + "=> 폭,길이 참조 검색 ");
						}
                         			
						/**
						 * 3.4 SLAB의 장입순번,산적번호로 TO 위치 FAIL 시
						 * 중량(0),폭(0),길이(0),두께(0)항목을 가지고 적치가능한 곳을 검색한다.
						 */
						if (toLoc == null || toLoc.size() == 0) {
							/*
							 * 2007.01.22 이정훈 
							 * B Slab 진행반 요청으로  막음
							 
							toLoc = getBasicToLocInfo_02(sStockId, // 저장품ID
									toList, // TO위치 LIST INFO
									toListCnt); // TO위치 LIST COUNT
							*/
							logger.println(LogLevel.DEBUG, this, "SLAB TO LOC=>" + sStockId + "=> 적치가능(E) 검색 ");
						}
						
						/**
						 * 2007.04.26 YJK
						 * 3.5 SLAB의 보조작업 대상재가 TO 위치 FAIL 시
						 * 중량(0),폭(0),길이(0),두께(0)항목을 가지고 적치가능한 곳을 검색한다.
						 */
						if (toLoc == null || toLoc.size() == 0) {
							
							if (YmCommonConst.SUB.equals(getField(work, "IS_MAIN"))) {
								
								toLoc = getBasicToLocInfo_02(sStockId, // 저장품ID
										toList, // TO위치 LIST INFO
										toListCnt); // TO위치 LIST COUNT
							
								logger.println(LogLevel.DEBUG, this, "SLAB TO LOC=>" + sStockId + "=> 보조작업 적치가능(E) 검색 ");
							}
						}
						
						/**
						 * 크레인 영역을 고려한다.
						 */
						if (toLoc != null && toLoc.size() != 0) {
							/**
							 * YJK TO 위치 영역 -아래 주석처리함.
							 * if(considerToCRRange(work, toLoc, toListCnt,
							 * 99999)) { throw new Exception("##### 크레인 TO 위치
							 * 영역을 확인 하십시요."); }
							 */
						}

						if (toLoc == null || toLoc.size() == 0) {
							return null;
						}
					}
				}
				if (toLoc != null && toLoc.size() != 0) {
					break;
				}
			}

			logger.println(LogLevel.DEBUG, this, "SLAB TO LOC=>" + sStockId + "=> TO 위치 검색 END");

			if (toLoc == null || toLoc.size() == 0) {
				return null;
			}
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}

		return toLoc;
	}

	/**
	 * @param work
	 * @param toLoc
	 * @param i
	 * @throws Exception
	 */
	private boolean considerToCRRange(JDTORecord work, JDTORecord toLoc, int toListCnt, int i) throws Exception {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		JDTORecord dto = ymCommonDAO.readCRRange(getField(work, "YD_GP")+ getField(work, "BAY_GP") + "CR" + getSchWorkEquipNo(work),
												 getField(toLoc, "STACK_COL_GP"),
												 getField(toLoc, "STACK_BED_GP"), 
												 getField(toLoc, "STACK_LAYER_GP"));
		if (i == (toListCnt - 1)) {
			if ("F".equals(getField(dto, "RESULT"))) {
				throw new Exception("##### 크레인 TO 위치 영역을 확인 하십시요.");
			} else {
				return false;
			}
		} else {
			if ("F".equals(getField(dto, "RESULT"))) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * @param work
	 * @param toLoc
	 * @param i
	 * @throws Exception
	 */
	private void considerFromCRRange(JDTORecord work) throws Exception {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
		if ((!YmCommonConst.NEW_SCH_WORK_KIND_SWLI.equals(getField(work,"SCH_WORK_KIND")))
			&& (!YmCommonConst.NEW_SCH_WORK_KIND_SCLI.equals(getField(work,"SCH_WORK_KIND")))) {
			JDTORecord dto = ymCommonDAO.readCRRange(getField(work, "YD_GP") + getField(work, "BAY_GP") + "CR" + getSchWorkEquipNo(work),
													 getField(work,"FORM_LOC").substring(0, 6), 
													 getField(work,"FORM_LOC").substring(6, 8), 
													 getField(work,"FORM_LOC").substring(8, 10));
			if ("F".equals(getField(dto, "RESULT"))) {
				throw new Exception("##### " + getSchWorkEquipNo(work) + " 크레인 FROM 영역을 확인 요망.");
			}
		}
	}

	/**
	 * @param curColGp
	 * @return
	 */
	private boolean isZoinWork(String curColGp) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		if ("WB".equals(curColGp.substring(2, 4))) {
			return true;
		} else if ("CT".equals(curColGp.substring(2, 4))) {
			return true;
		}
		return false;
	}

	/**
	 * @param work
	 * @param curColGp
	 * @param toLoc
	 */
	private String considerLotNo(JDTORecord work, String curColGp) {

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		String sResult = "";

		/*
		 * 1.1 현재 보급할 장입LOT순번을 가져온다.
		 */
		String sCurLotNo 	= "";
		JDTORecord dto1 	= null; 
		
		if ("WB".equals(curColGp.substring(2, 4))) {
			/*
			 * 2010.09.02 윤재광 - 다른 장입LOT대상재 작업가능 처리
			 */
			{
				String isLot = StringHelper.evl(getField(work, "CTS_RELAY_SADDLE"),"");
				if("LOT".equals(isLot)){
					logger.println(LogLevel.DEBUG, this, "현 대상재 장입번호를 보급할 수 있는지 체크 SKIP");
					return "1";
				}
			}
			
			dto1 = ymCommonDAO.readCurZoinLotNo();
			if (dto1 != null) {
				sCurLotNo = getField(dto1, "CHARGE_LOT_NO");
			}
		} else if ("CT".equals(curColGp.substring(2, 4))) {
			//현재동에서 가장빠른 장입순번을 검색
			dto1 = ymCommonDAO.readCurZoinLotNo_CurBay(curColGp.substring(0, 2));
			if (dto1 != null) {
				sCurLotNo = getField(dto1, "CHARGE_LOT_NO");
			}
		}
		
		/*
		 * 2.1 CTC, W/B 에 올려진 SLAB 정보를 가져온다.
		 */
		String sWbLotNo = "";
		JDTORecord dto2 = ymCommonDAO.readLoadWBCTC(curColGp);
		if (dto2 != null) {
			/*
			 * 2.1.1 CTC,W/B 입측에 SLAB 정보가 있을 경우 해당 SLAB의 장입LOT순번을 가져온다.
			 */
			String sStockId = getField(dto2, "STOCK_ID");
			JDTORecord dto3 = ymCommonDAO.readCurZoinLotNo(sStockId);
			if (dto3 != null) {
				sWbLotNo = getField(dto3, "CHARGE_LOT_NO");
			}
		}

		String sSlabLotNo = getField(work, "CHARGE_LOT_NO");

		/*
		 * sSlabLotNo = 현재 저장품 장입번호 sCurLotNo = 현재 보급해야할 장입번호 sWbLotNo = 현재 WB
		 * 01번지에 있는 저장품의 장입번호
		 * 
		 * 3. 현재 작업할 SLAB 의 장입LOT순번과 CTC,W/B에 있는 또는 없으면 현재 보급할 장입LOT순번을 비교한다. 즉,
		 * 같으면 TO위치 적치 가능하다.
		 */
		logger.println(LogLevel.DEBUG, this, "재료의 장입번호 =>"+sSlabLotNo);
		logger.println(LogLevel.DEBUG, this, "설비의 장입번호 =>"+sWbLotNo);
		logger.println(LogLevel.DEBUG, this, "보급할 장입번호 =>"+sCurLotNo);
		if (sSlabLotNo.equals(sCurLotNo)) {

			if ("".equals(sWbLotNo)) {

				sResult = "1";
				logger.println(LogLevel.DEBUG, this, "CHARGE_LOT_NO =>장입대상=> WB 01번지 01단에 적치가능.");
			} else {

				if (sSlabLotNo.equals(sWbLotNo)) {

					sResult = "1";
					logger.println(LogLevel.DEBUG, this, "CHARGE_LOT_NO =>장입대상=> WB 01번지 상단에 적치가능.");

				} else {

					sResult = "2";
					logger.println(LogLevel.DEBUG, this, "CHARGE_LOT_NO =>장입대상=> WB 01번지 장입LOT 번호와 다릅니다.");
				}
			}
		} else {

			sResult = "3";
			logger.println(LogLevel.DEBUG, this, "CHARGE_LOT_NO =>장입대상 아님=> 장입LOT 번호가 다릅니다.");
		}

		return sResult;
	}

	/**
	 * 장입LOT번호 단위로 TO위치를 검색한다.
	 * 
	 * @param record
	 */
	private JDTORecord getToLocPriority(String stockId, List toList, int toListCnt) {
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		String curColGp = "";
		/*
		 * 1. 장입LOT번호TO위치 동일한 장입LOT번호 TO위치 검색
		 */
		JDTORecord dto = getCurToLoc(toList, stockId, YmCommonConst.SLAB_TO_LOC_G, toListCnt);
		logger.println(LogLevel.DEBUG, this, "SLAB SUB TOLOC=>" + stockId + "▶▶▶▶▶▶▶▶▶▶장입LOT 순위가 동일한 TO 위치 검색◀◀◀◀◀◀◀◀◀◀");

		if (dto == null) {

			/*
			 * 2. 장입LOT번호TO위치 하단에 후순위 장입LOT번호 TO위치 검색
			 */
			dto = getCurToLoc(toList, stockId, YmCommonConst.SLAB_TO_LOC_P,	toListCnt);
			logger.println(LogLevel.DEBUG, this, "TSLAB SUB TOLOC=>" + stockId + "▶▶▶▶▶▶▶▶▶▶하단에 후순위 장입LOT번호 TO위치 검색◀◀◀◀◀◀◀◀◀◀");

			if (dto == null) {
				/*
				 * 3. 장입LOT번호TO위치 01단 TO위치 검색
				 */
				dto = getCurToLoc(toList, stockId, YmCommonConst.SLAB_TO_LOC_E,	toListCnt);
				logger.println(LogLevel.DEBUG, this, "SLAB SUB TOLOC=>" + stockId + "▶▶▶▶▶▶▶▶▶▶장입LOT 순위 01단 TO 위치 검색◀◀◀◀◀◀◀◀◀◀");

				if (dto == null) {
					/*
					 * 4. 장입LOT번호TO위치 하단에 장입LOT번호가 없는 TO위치 검색
					 */
					dto = getCurToLoc(toList, stockId, YmCommonConst.SLAB_TO_LOC_N, toListCnt);
					logger.println(LogLevel.DEBUG, this, "TSLAB SUB TOLOC=>" + stockId + "▶▶▶▶▶▶▶▶▶▶하단에 장입LOT번호가 없는 TO위치 검색◀◀◀◀◀◀◀◀◀◀");

					if (dto == null) {
						/*
						 * 5. 산적LOT번호 단위로 TO위치를 검색메소드 호출
						 * 2007.02.02 이정훈 
						 * 
						 * dto = getToLocOfStackLoc(stockId, toList, toListCnt);
						 */
						curColGp = getField((JDTORecord) toList.get(0), "STACK_COL_GP");
						
						if (!"C".equals(curColGp.substring(1,2))) {
							dto = getToLocOfStackLoc(stockId, toList, toListCnt);
						}
					}
				}
			}
		}
		return dto;
	}
    
	/**
	 * 2007.02.07 이정훈
	 * schedule Code 추가 
	 * 
	 * @param stockId
	 * @param toList
	 * @param toListCnt
	 * @param schkind
	 * @return
	 */
	private JDTORecord getToLocOfStackLoc_01(String stockId, List toList, int toListCnt,String schkind) {

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		String sMthType_01 = YmCommonConst.SLAB_TO_LOC_S;
		String sMthType_02 = YmCommonConst.SLAB_TO_LOC_E;
		String sMthStr_01 = "산적LOT가 동일한 TO 위치 검색";
		String sMthStr_02 = "01단 TO 위치 검색";

		String[] sStockInfo = YmCommonUtil.getCoilCurrProgCd(stockId,"");
		String sProgCd = sStockInfo[0];
		String sStocMv = sStockInfo[1];

		if (YmCommonConst.NEW_STOCK_MOVE_TERM_D2.equals(sStocMv) || // 시편작업대기
		    YmCommonConst.NEW_STOCK_MOVE_TERM_D3.equals(sStocMv)) { // 핸드스카핑재
			sMthType_01 = YmCommonConst.SLAB_TO_LOC_E;
			sMthType_02 = YmCommonConst.SLAB_TO_LOC_S;
			sMthStr_01 = "01단 TO 위치 검색";
			sMthStr_02 = "산적LOT가 동일한 TO 위치 검색";
		} else {
			sMthType_01 = YmCommonConst.SLAB_TO_LOC_S;
			sMthType_02 = YmCommonConst.SLAB_TO_LOC_E;
			sMthStr_01 = "산적LOT가 동일한 TO 위치 검색";
			sMthStr_02 = "01단 TO 위치 검색";
		}

		/*
		 * 1. 산적LOT번호TO위치검색방법 동일한 산적LOT번호 TO위치 검색/01단 TO위치 검색
		 */
		JDTORecord dto = getCurToLoc(toList, stockId, sMthType_01, toListCnt, schkind);
		logger.println(LogLevel.DEBUG, this, "SLAB SUB TOLOC=>" + stockId + "=> " + sMthStr_01);

		if (dto == null) {
			/*
			 * 2. 산적LOT번호TO위치검색방법 동일한 산적LOT번호 TO위치 검색/01단 TO위치 검색
			 */
			dto = getCurToLoc(toList, stockId, sMthType_02, toListCnt, schkind);
			logger.println(LogLevel.DEBUG, this, "SLAB SUB TOLOC=>" + stockId + "=> " + sMthStr_02);

			if (dto == null) {
				/*
				 * 3. 산적LOT번호TO위치검색방법(5) 적치기준,BED TYPE 가능한 TO위치 검색
				 */
				dto = getCurToLoc(toList, stockId, YmCommonConst.SLAB_TO_LOC_U, toListCnt, schkind);
				logger.println(LogLevel.DEBUG, this, "SLAB SUB TOLOC=>" + stockId + "=> 적치기준,BED TYPE 가능한 TO 위치 검색");
			}
		}
		return dto;
	}
	
	/**
	 *	A열연 Slab 검색로직
	 */
	private JDTORecord getToLocOfStackLoc_02(String stockId, List toList, int toListCnt,String schkind,String stockmt) {

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		String sMthType_01 	= YmCommonConst.SLAB_TO_LOC_B;
		String sMthType_02 	= YmCommonConst.SLAB_TO_LOC_E;
		String sMthType_03 	= YmCommonConst.SLAB_TO_LOC_A;
		String sMthStr_01 	= "A열연 Slab 야드 위치검색[저장품이동조건 체크]";
		String sMthStr_02 	= "01단 TO 위치 검색";
		String sMthStr_03 	= "A열연 Slab 야드 위치검색[두께,폭,길이 허용오차 체크]";
				
		String curColGp 	= "";
		String sMoveRp 		= "";
		JDTORecord dto 		= null;
		
		logger.println(LogLevel.DEBUG, this, "A SLAB  TOLOC   => 1순위 검색");
		
		for (int i = 0; i < toListCnt; i++) {

			curColGp 	= getField((JDTORecord) toList.get(i), "STACK_COL_GP");
			sMoveRp 	= getField((JDTORecord) toList.get(i), "STOCK_MOVE_ROUTE_PRIOR");
			
			if("1".equals(sMoveRp)){
				
				if(curColGp.indexOf("PT") != -1){
					
					dto = ymCommonDAO.readSlabToLocInfo(stockId, curColGp.substring(0, 4), sMthType_01, schkind, stockmt);
					logger.println(LogLevel.DEBUG, this, "A SLAB  TOLOC   =>" + sMthType_01 + "=>" + stockId + "=>" + curColGp);
					
					if (dto == null) {
						
						dto = ymCommonDAO.readSlabToLocInfo(stockId, curColGp.substring(0, 4), sMthType_02, schkind, stockmt);
						logger.println(LogLevel.DEBUG, this, "A SLAB  TOLOC   =>" + sMthType_02 + "=>" + stockId + "=>" + curColGp);
					}
					
				}else{
					dto = ymCommonDAO.readSlabToLocInfo(stockId, curColGp, sMthType_03, schkind, stockmt);
					logger.println(LogLevel.DEBUG, this, "A SLAB  TOLOC   =>" + sMthType_03 + "=>" + stockId + "=>" + curColGp);
				}
			}
			
			if (dto != null) {
				break;
			}
		}
		
		
		if (dto == null) {
			
		logger.println(LogLevel.DEBUG, this, "A SLAB  TOLOC   => 2순위 검색");
			
		for (int i = 0; i < toListCnt; i++) {

			curColGp 	= getField((JDTORecord) toList.get(i), "STACK_COL_GP");
			sMoveRp 	= getField((JDTORecord) toList.get(i), "STOCK_MOVE_ROUTE_PRIOR");
			
			if("2".equals(sMoveRp)){
				
				if(curColGp.indexOf("PT") != -1){
					
					dto = ymCommonDAO.readSlabToLocInfo(stockId, curColGp.substring(0, 4), sMthType_01, schkind, stockmt);
					logger.println(LogLevel.DEBUG, this, "A SLAB  TOLOC   =>" + sMthType_01 + "=>" + stockId + "=>" + curColGp);
					
					if (dto == null) {
						
						dto = ymCommonDAO.readSlabToLocInfo(stockId, curColGp.substring(0, 4), sMthType_02, schkind, stockmt);
						logger.println(LogLevel.DEBUG, this, "A SLAB  TOLOC   =>" + sMthType_02 + "=>" + stockId + "=>" + curColGp);
					}
					
				}else{
					dto = ymCommonDAO.readSlabToLocInfo(stockId, curColGp, sMthType_03, schkind, stockmt);
					logger.println(LogLevel.DEBUG, this, "A SLAB  TOLOC   =>" + sMthType_03 + "=>" + stockId + "=>" + curColGp);
				}
			}
			
			if (dto != null) {
				break;
			}
		}
		}
		
		return dto;
	}
	
	/**
	 * 2007.02.07 이정훈
	 * schedule Code 추가 
	 * 
	 * @param stockId
	 * @param toList
	 * @param toListCnt
	 * @param schkind
	 * @return
	 */
	private JDTORecord getToLocOfStackLoc_03(String stockId, List toList, int toListCnt,String schkind) {
 
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		
		/*
		 * 1. (동일강종,동일 생산폭+-30 , 동일 주문두께,주문폭 ) TO위치 검색/01단 TO위치 검색
		 */
		JDTORecord dto = getCurToLoc(toList, stockId, "L", toListCnt, schkind);
		logger.println(LogLevel.DEBUG, this, "SLAB SUB TOLOC=>" + stockId + "=> " + "동일강종,동일 생산폭+-30 , 동일 주문두께,주문폭 ) TO위치 검색/01단 TO위치 검색");

		if (dto == null) {
			/*
			 * 2. 빈 Bed
			 */
			dto = getCurToLoc(toList, stockId, "E", toListCnt, schkind);
			logger.println(LogLevel.DEBUG, this, "SLAB SUB TOLOC=>" + stockId + "=> " + "빈 BED TO위치 검색/01단 TO위치 검색");
			
					if (dto == null) {

						/*
						 * 3. (동일강종,동일 생산폭+-30 ) TO위치 검색/01단 TO위치 검색
						 */
						dto = getCurToLoc(toList, stockId, "L2", toListCnt, schkind);
						logger.println(LogLevel.DEBUG, this, "SLAB SUB TOLOC=>" + stockId + "=> " + "동일강종,동일 생산폭+-30 ) TO위치 검색/01단 TO위치 검색");

						if (dto == null) {
							/*
							 * 4. 1단에 주문두께 와 폭이 작은거 가능한 TO 위치 검색
							 */
							dto = getCurToLoc(toList, stockId, "M", toListCnt, schkind);
							logger.println(LogLevel.DEBUG, this, "SLAB SUB TOLOC=>" + stockId + "=> " + "1단에 주문두께 와 폭이 작은거 가능한 TO 위치 검색");
							
							if (dto == null) {
								/*
								 * 5. 1bed 상단 
								 */
								dto = getCurToLoc(toList, stockId, "U", toListCnt, schkind);
								logger.println(LogLevel.DEBUG, this, "SLAB SUB TOLOC=>" + stockId + "=> " + "1BED 상단  TO 위치 검색");
							}
						}
			}

		}
		return dto;
	}
	
	/**
	 * 산적LOT번호 단위로 TO위치를 검색한다.
	 * 
	 * @param stockId
	 * @param col
	 * @return
	 */
	private JDTORecord getToLocOfStackLoc(String stockId, List toList, int toListCnt) {

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		String sMthType_01 = YmCommonConst.SLAB_TO_LOC_S;
		String sMthType_02 = YmCommonConst.SLAB_TO_LOC_E;
		String sMthStr_01 = "산적LOT가 동일한 TO 위치 검색";
		String sMthStr_02 = "01단 TO 위치 검색";

		String[] sStockInfo = YmCommonUtil.getCoilCurrProgCd(stockId,"");
		String sProgCd = sStockInfo[0];
		String sStocMv = sStockInfo[1];

		if (YmCommonConst.NEW_STOCK_MOVE_TERM_D2.equals(sStocMv) || // 시편작업대기
		    YmCommonConst.NEW_STOCK_MOVE_TERM_D3.equals(sStocMv)) { // 핸드스카핑재
			sMthType_01 = YmCommonConst.SLAB_TO_LOC_E;
			sMthType_02 = YmCommonConst.SLAB_TO_LOC_S;
			sMthStr_01 = "01단 TO 위치 검색";
			sMthStr_02 = "산적LOT가 동일한 TO 위치 검색";
		} else {
			sMthType_01 = YmCommonConst.SLAB_TO_LOC_S;
			sMthType_02 = YmCommonConst.SLAB_TO_LOC_E;
			sMthStr_01 = "산적LOT가 동일한 TO 위치 검색";
			sMthStr_02 = "01단 TO 위치 검색";
		}

		/*
		 * 1. 산적LOT번호TO위치검색방법 동일한 산적LOT번호 TO위치 검색/01단 TO위치 검색
		 */
		JDTORecord dto = getCurToLoc(toList, stockId, sMthType_01, toListCnt);
		logger.println(LogLevel.DEBUG, this, "SLAB SUB TOLOC=>" + stockId + "=> " + sMthStr_01);

		if (dto == null) {
			/*
			 * 2. 산적LOT번호TO위치검색방법 동일한 산적LOT번호 TO위치 검색/01단 TO위치 검색
			 */
			dto = getCurToLoc(toList, stockId, sMthType_02, toListCnt);
			logger.println(LogLevel.DEBUG, this, "SLAB SUB TOLOC=>" + stockId + "=> " + sMthStr_02);

			if (dto == null) {
				/*
				 * 3. 산적LOT번호TO위치검색방법(5) 적치기준,BED TYPE 가능한 TO위치 검색
				 */
				dto = getCurToLoc(toList, stockId, YmCommonConst.SLAB_TO_LOC_U,
						toListCnt);
				logger.println(LogLevel.DEBUG, this, "SLAB SUB TOLOC=>" + stockId + "=> 적치기준,BED TYPE 가능한 TO 위치 검색");
			}
		}
		return dto;
	}

	/**
	 * SLAB의 장입순번,산적번호로 TO 위치 FAIL 시 중량,폭,길이,두께항목을 가지고 적치가능한 곳을 검색.
	 * 
	 * @param stockId
	 * @param col
	 * @return
	 */
	private JDTORecord getBasicToLocInfo_01(String stockId, List toList,
			int toListCnt) {

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		JDTORecord toLoc = null;
		String curColGp = "";
		String sSlabT = "0";
		String sSlabW = "0";
		String sSlabLen = "0";
		String sSlabWt = "0";

		JDTORecord slabJr = dao.getSlabCommonInfo(stockId);
		if (slabJr != null) {
			sSlabT 	= StringHelper.evl(slabJr.getFieldString("SLAB_T"), "0");  	//SLAB 두께
			sSlabW 	= StringHelper.evl(slabJr.getFieldString("SLAB_W"), "0"); 	//SLAB 폭
			sSlabLen = StringHelper.evl(slabJr.getFieldString("SLAB_LEN"), "0"); //SLAB 길이
			sSlabWt 	= StringHelper.evl(slabJr.getFieldString("SLAB_WT"), "0"); 	//SLAB 중량
		} 

		for (int i = 0; i < toListCnt; i++) {

			curColGp = getField((JDTORecord) toList.get(i), "STACK_COL_GP");

			if (!YmCommonUtil.isEquipLoc(YmCommonUtil.getStackColInfoWithPk(curColGp))) {

				toLoc = ymCommonDAO.readBasicToLocInfo(curColGp, // 적치열
														sSlabW, // 폭
														sSlabW, // 폭
														sSlabLen, // 길이
														sSlabLen); // 길이
				logger.println(LogLevel.DEBUG, this, "SLAB SUB SUB TOLOC =>" + stockId + "=>" + curColGp);

				if (toLoc != null) {
					break;
				}
			}else{
				//2008.11.25 YJK 추가기능 테스트 검토 후 반영
				
				/* 
				 *	임시로 특정설비 HMI상태값으로 체크
				 *	TB_YM_EQUIP(2AMT01)  WORK_MODE 체크 
				 */
				String sQuery1	= "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.SelectWBHMISTAT";
				JDTORecord wbJr = dao.getData(sQuery1, new Object[]{ "2AMT01" });
				
				boolean isExecute 	= false;
				String sHmiStat  	= "";
				
				if (wbJr != null){ 
					sHmiStat  = StringHelper.evl(wbJr.getFieldString("WORK_MODE"), "");
				}
				
				if ("O".equals(sHmiStat)){
					isExecute = true;
				}
				
				logger.println(LogLevel.DEBUG, this, "SLAB SUB SUB TOLOC=> HMI 상태 모드 ="+ sHmiStat);
				
		    		if (isExecute){
			    			
					toLoc = ymCommonDAO.readToLocDefine(curColGp);
					logger.println(LogLevel.DEBUG, this, "SLAB SUB SUB TOLOC=>" + stockId + "=> 차량,대차=" + curColGp);
					
					if (toLoc != null) {
						break;
					}
				}
			}
			
		}

		return toLoc;
	}
	
	
	
	/**
	 * SLAB의 장입순번,산적번호로 TO 위치 FAIL 시 중량,폭,길이,두께항목을 가지고 적치가능한 곳을 검색(보온뱅크).
	 * 
	 * @param stockId
	 * @param col
	 * @return
	 */
	private JDTORecord getBasicToLocInfo_03(String stockId, List toList,
			int toListCnt ,String ScheduelCode) {

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		JDTORecord toLoc = null;
		String curColGp = "";
		String sSlabT = "0";
		String sSlabW = "0";
		String sSlabLen = "0";
		String sSlabWt = "0";

		JDTORecord slabJr = dao.getSlabCommonInfo(stockId);
		if (slabJr != null) {
			sSlabT 	= StringHelper.evl(slabJr.getFieldString("SLAB_T"), "0");  	//SLAB 두께
			sSlabW 	= StringHelper.evl(slabJr.getFieldString("SLAB_W"), "0"); 	//SLAB 폭
			sSlabLen = StringHelper.evl(slabJr.getFieldString("SLAB_LEN"), "0"); //SLAB 길이
			sSlabWt 	= StringHelper.evl(slabJr.getFieldString("SLAB_WT"), "0"); 	//SLAB 중량
		} 

		for (int i = 0; i < toListCnt; i++) {

			curColGp = getField((JDTORecord) toList.get(i), "STACK_COL_GP");

			if (!YmCommonUtil.isEquipLoc(YmCommonUtil.getStackColInfoWithPk(curColGp))) {
				
				

				//2012.11.12 A동에 대한 기준 변경
				if(ScheduelCode.equals("SVMU")){
					//신규방식(동일강종1,2,3,4,5, 기준))
					toLoc = getToLocOfStackLoc_03(stockId, 		// 저장품ID
											    toList, 			// TO위치 LIST INFO
											    toListCnt,			// TO위치 LIST COUNT
											    ScheduelCode); 		// Schedule Code
				}else{
					//기존 방식(가용 폭,길이 범위)
					toLoc = ymCommonDAO.readBasicToLocInfo(curColGp, // 적치열
															sSlabW, // 폭
															sSlabW, // 폭
															sSlabLen, // 길이
															sSlabLen); // 길이
				}

				
				logger.println(LogLevel.DEBUG, this, "SLAB SUB SUB TOLOC =>" + stockId + "=>" + curColGp);

				if (toLoc != null) {
					break;
				}
			}else{
				    toLoc = ymCommonDAO.readToLocDefine(curColGp);
					logger.println(LogLevel.DEBUG, this, "SLAB SUB SUB TOLOC=>" + stockId + "=> 차량,대차=" + curColGp);
					
					if (toLoc != null) {
						break;
					}
			}
			
		}

		return toLoc;
	}

	
	

	/**
	 * SLAB의 장입순번,산적번호로 TO 위치 FAIL 시 중량(0),폭(0),길이(0),두께(0)항목을 가지고 적치가능한 곳을 검색.
	 * 
	 * @param stockId
	 * @param col
	 * @return
	 */
	private JDTORecord getBasicToLocInfo_02(String stockId, List toList,
			int toListCnt) {

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		JDTORecord toLoc = null;
		String curColGp = "";

		for (int i = 0; i < toListCnt; i++) {

			curColGp = getField((JDTORecord) toList.get(i), "STACK_COL_GP");

			if (!YmCommonUtil.isEquipLoc(YmCommonUtil.getStackColInfoWithPk(curColGp))) {

				toLoc = ymCommonDAO.readBasicToLocInfo(curColGp, 	// 적치열
														"999999", 	// 폭(MIN)
														"0", 		// 폭(MAX)
														"999999", 	// 길이(MIN)
														"0"); 		// 길이(MAX)

				logger.println(LogLevel.DEBUG, this, "SLAB SUB SUB TOLOC =>" + stockId + "=>" + curColGp);

				if (toLoc != null) {
					break;
				}
			}else{
				//2008.11.25 YJK 추가기능 테스트 검토 후 반영
				
				/* 
				 *	임시로 특정설비 HMI상태값으로 체크
				 *	TB_YM_EQUIP(2AMT01)  WORK_MODE 체크 
				 */
				String sQuery1	= "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.SelectWBHMISTAT";
				JDTORecord wbJr = dao.getData(sQuery1, new Object[]{ "2AMT01" });
				
				boolean isExecute 	= false;
				String sHmiStat  	= "";
				
				if (wbJr != null){ 
					sHmiStat  = StringHelper.evl(wbJr.getFieldString("WORK_MODE"), "");
				}
				
				if ("O".equals(sHmiStat)){
					isExecute = true;
				}
				
				logger.println(LogLevel.DEBUG, this, "SLAB SUB SUB TOLOC=> HMI 상태 모드 ="+ sHmiStat);
				
		    		if (isExecute){
			    			
					toLoc = ymCommonDAO.readToLocDefine(curColGp);
					logger.println(LogLevel.DEBUG, this, "SLAB SUB SUB TOLOC=>" + stockId + "=> 차량,대차=" + curColGp);
					
					if (toLoc != null) {
						break;
					}
				}
			}
		}

		return toLoc;
	}
    
	private JDTORecord getCurToLoc(List toList, String stockId, String gp, int toListCnt) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		return getCurToLoc(toList, stockId, gp, toListCnt, "", "");
	}
	private JDTORecord getCurToLoc(List toList, String stockId, String gp, int toListCnt, String schkind) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		return getCurToLoc(toList, stockId, gp, toListCnt, schkind, "");
	}
	private JDTORecord getCurToLoc(List toList, String stockId, String gp, int toListCnt, String schkind, String stockmt) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		String curColGp = null;
		JDTORecord dto = null;

		for (int i = 0; i < toListCnt; i++) {

			curColGp = getField((JDTORecord) toList.get(i), "STACK_COL_GP");

			if (!YmCommonUtil.isEquipLoc(YmCommonUtil.getStackColInfoWithPk(curColGp))) {

				dto = ymCommonDAO.readSlabToLocInfo(stockId, curColGp, gp, schkind, stockmt);

				logger.println(LogLevel.DEBUG, this, "SLAB SUB SUB TOLOC =>" + stockId + "=>" + curColGp);
				if (dto != null) {
					break;
				}
			}else{
				//2008.11.25 YJK 추가기능 테스트 검토 후 반영
				
				/* 
				 *	임시로 특정설비 HMI상태값으로 체크
				 *	TB_YM_EQUIP(2AMT01)  WORK_MODE 체크 
				 */
				String sQuery1	= "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.SelectWBHMISTAT";
				JDTORecord wbJr = dao.getData(sQuery1, new Object[]{ "2AMT01" });
				
				boolean isExecute 	= false;
				String sHmiStat  	= "";
				
				if (wbJr != null){ 
					sHmiStat  = StringHelper.evl(wbJr.getFieldString("WORK_MODE"), "");
				}
				
				if ("O".equals(sHmiStat)){
					isExecute = true;
				}
				
				logger.println(LogLevel.DEBUG, this, "SLAB SUB SUB TOLOC=> HMI 상태 모드 ="+ sHmiStat);
				
		    		if (isExecute){
			    			
					dto = ymCommonDAO.readToLocDefine(curColGp);
					logger.println(LogLevel.DEBUG, this, "SLAB SUB SUB TOLOC=>" + stockId + "=> 차량,대차=" + curColGp);
					
					if (dto != null) {
						break;
					}
				}
			}
		}
		return dto;
	}
	/**
	 * @param record
	 * @param j
	 * @param string
	 * @param recordCnt
	 */
	private JDTORecord emptyStackLot(List record, String layer, int cnt, int j) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		JDTORecord dto = null;
		if (j == cnt) {
			return dto;
		}
		for (int i = j; i < cnt; i++) {
			dto = (JDTORecord) record.get(i);
			if (layer.equals(getField(dto, "STACK_LAYER_GP"))) {
				break;
			}
		}
		return dto;
	}

	/**
	 * @param record
	 * @param cnt
	 * @param j
	 * @param field
	 */
	private JDTORecord equalStackLot(List record, String currLot, int cnt, int j) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		JDTORecord dto = null;
		if (j == cnt) {
			return dto;
		}
		for (int i = j; i < cnt; i++) {
			dto = (JDTORecord) record.get(i);
			if (currLot.equals(getField(dto, "BUTTOM_LOT"))) {
				break;
			}
		}
		return dto;
	}

	/**
	 * 적치대 테이블의 가능 관련 항목을 UPDATE
	 * 
	 * @param toLoc 	= TO 위치
	 * @param slabWt	= 가능 중량
	 * @param slabT 	= 가능 높이[두께]
	 * @param slabW 	= 가능 폭
	 * @param slabLen 	= 가능 길이
	 */
	private void editPossibleColumnOfStacker(JDTORecord toLoc, float slabWt, float slabT, float slabW, float slabLen) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
		String colGp = getField(toLoc, "STACK_COL_GP");
		String bedGp = getField(toLoc, "STACK_BED_GP");

		float ableT 		= getFieldFloat(toLoc, "STACK_BED_ABLE_HIGH"); 	// 적치 BED 가능 높이[두께]
		float ableW 		= getFieldFloat(toLoc, "STACK_BED_ABLE_W"); 		// 적치 BED 가능 폭
		float ableLen 		= getFieldFloat(toLoc, "STACK_BED_ABLE_LEN"); 	// 적치 BED 가능 길이
		int ableQnty 		= getFieldInt(toLoc, "STACK_BED_ABLE_QNTY"); 		// 적치 BED 가능 수량
		float ableQntyWt 	= getFieldFloat(toLoc, "STACK_BED_ABLE_WT"); 		// 적치 BED  가능 중량

		if (ableQnty == 1) {
			ymCommonDAO.modifyPossibleOfStacker(colGp, bedGp);
		} else {
			ymCommonDAO.modifyPossibleOfStacker("" + (ableQnty - 1),
												YmCommonUtil.format("" + (ableQntyWt - slabWt), 4, 3),
												YmCommonUtil.format("" + (ableT - slabT), 4, 3),
												colGp,
												bedGp);
		}
	}

	/**
	 * 적치단 테이블에서 'E'상태인 단의 상단의 상태가 'V'가 존재하면 'E'로 UPDATE.
	 * 
	 * @param ydSchDAO
	 * @param colGp		= 적치열
	 * @param badGp		= 번지
	 * @param nextGp	= 다음 단
	 */
	private void editNextLayer(String colGp, String badGp, int nextGp) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
		String nextLayerGp = nextGp > 9 ? "" + nextGp : "0" + nextGp;
		JDTORecord nextStat = ymCommonDAO.readNextLayerStat(colGp, badGp, nextLayerGp);
		if (nextStat != null) {
			ymCommonDAO.modifyLayerStateOfLayer(YmCommonConst.STACK_LAYER_STAT_E, colGp, badGp, nextLayerGp);
		}
	}

	/**
	 * 대차상차시 대차의 하차작업이 'CTC 보급'인지 리턴한다.
	 * 
	 * @param searchLoc
	 *            하차동을 가지는 데이터
	 * @return true: CTC작업이 아니다. false: CTC 작업이다
	 */
	private boolean notCTCWork(String searchLoc) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		JDTORecord unloadLoc = ymCommonDAO.readCarUnloadLoc(searchLoc);
		if (YmCommonConst.BAY_GP_B.equals(getField(unloadLoc, "BAY"))) {
			return false;
		}
		return true;
	}

	/**
	 * 장입스케쥴인지 확인한다. -대차상차, W/B 보급
	 * 
	 * @param schKind
	 * @return
	 */
	private boolean isZoinSchdule(String schKind) {
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		if (YmCommonConst.NEW_SCH_WORK_KIND_STSL.equals(schKind)) {
			return true; // 동간보급상차
		} else if (YmCommonConst.NEW_SCH_WORK_KIND_SWLI.equals(schKind)) {
			return true; // W/B보급
		} else if (YmCommonConst.NEW_SCH_WORK_KIND_SCLI.equals(schKind)) {
			return true; // CTC보급
		}
		return false;
	}

	/**
	 * 2매작업이 가능한 위치에 대해서 저장품 정보를 UPDATE
	 * 
	 * @param csWorkTarget	= 작업대상
	 * @param toStoreLoc	= TO 위치
	 * @param grip			= GRIP 대상
	 */
	private void applyGripOfLayer(List csWorkTarget, Map toStoreLoc, Map grip) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
		String key = null;
		String value = null;
		JDTORecord to = null;
		Iterator iter = grip.keySet().iterator();
		while (iter.hasNext()) {
			key = (String) iter.next();
			value = (String) grip.get(key);
			to = (JDTORecord) toStoreLoc.get(key);
			editStockIdOfLayer(csWorkTarget, getField(to, "TO_LOC"), Integer.parseInt(value));
			to = (JDTORecord) toStoreLoc.get(value);
			editStockIdOfLayer(csWorkTarget, getField(to, "TO_LOC"), Integer.parseInt(key));
		}
	}

	/**
	 * 적치단의 저장품ID를 UPDATE
	 * 
	 * @param csWorkTarget
	 *            작업대상
	 * @param toLoc
	 *            TO 위치
	 */
	private void editStockIdOfLayer(List csWorkTarget, String toLoc, int idx) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
		ymCommonDAO.modifyStockIdOfLayer(getField((JDTORecord) csWorkTarget.get(idx), "STOCK_ID"),
										toLoc.substring(0, 6), // 열
										toLoc.substring(6, 8), // 번지
										toLoc.substring(8, 10)); // 단
	}

	/**
	 * 스케쥴 테이블에 INSERT
	 * 
	 * @param ydSchDAO
	 * @param assign		= 스케쥴기준정보
	 * @param csWorkTarget	= 작업대상
	 * @param toStoreLoc	= TO 위치
	 * @param grip			= GRIP 대상
	 * @throws Exception
	 */
	private void createSchedule(JDTORecord assign, List csWorkTarget, Map toStoreLoc) throws Exception {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
		logger.println(LogLevel.DEBUG, this, "스케쥴등록: " + csWorkTarget.size());
		List editData = null;
		JDTORecord curWork = null;

		String toLoc = null;
		String stockId = null;
		String mainSubGp = null;
		String wbookSchActDt = null;
		for (int i = 0; i < csWorkTarget.size(); i++) {
			editData = new ArrayList();
			curWork = (JDTORecord) csWorkTarget.get(i);
			stockId = getField(curWork, "STOCK_ID");
			mainSubGp = getField(curWork, "IS_MAIN");

			// STOCK_ID --VARCHAR2(11) Not Null 저장품 ID
			editData.add(stockId);

			// SCH_RULE_ID --VARCHAR2(18) Not Null SCHEDULE 기준 ID
			editData.add(getField(assign, "SCH_RULE_ID"));

			// YD_GP --VARCHAR2(1) YARD 구분
			editData.add(getField(assign, "YD_GP"));

			// BAY_GP --VARCHAR2(1) 동 구분
			editData.add(getField(assign, "BAY_GP"));

			// /SCH_WORK_EQUIP_NO --VARCHAR2(2) SCHEDULE 작업 설비 번호
			/**
			 * C2 불가영역 W/B 보급인경우 C1 할당 스케쥴 등록 -아래 주석처리함.
			 */
			// editData.add(getSchWorkEquipNo(assign, curWork));
			editData.add(getSchWorkEquipNo(assign));

			// SCH_WORK_STAT --VARCHAR2(1) SCHEDULE 작업 상태
			// "S":Schedule

			// SCH_WPREFER --NUMBER(2) SCHEDULE 작업우선순위
			editData.add(new Integer(getSchWprefer(assign)));

			// SCH_WORK_KIND --VARCHAR2(4) SCHEDULE 작업 종류
			editData.add(getField(assign, "SCH_WORK_KIND"));

			// SCH_WORK_AID_YN --VARCHAR2(1) SCHEDULE 작업 보조 유무
			if (YmCommonConst.MAIN.equals(mainSubGp)) {
				editData.add(YmCommonConst.MAIN_WORK_M);
			} else if (YmCommonConst.SUB.equals(mainSubGp)) {
				editData.add(YmCommonConst.SUB_WORK_S);
			}

			// SCH_WORK_GRIP_LOT_YN --VARCHAR2(1) SCHEDULE 작업 GRIP LOT 유무
			editData.add(YmCommonConst.GRIP_LOT_YN_T);

			// CRANE_WORD_UP_LOC --VARCHAR2(10) CRANE 작업지시 UP 위치
			editData.add(getField(curWork, "FORM_LOC"));

			// WBOOK_LOC_DECISION_METHOD --VARCHAR2(1) 작업예약 위치 결정 방법
			editData.add(getField(curWork, "SCH_WORK_LOC_DECISION_METHOD"));

			// CRANE_WORD_PUT_LOC --VARCHAR2(10) CRANE 작업지시 PUT 위치
			editData
					.add(getField((JDTORecord) toStoreLoc.get("" + i), "TO_LOC"));

			// SCH_WORK_CAR_NO --VARCHAR2(12) SCHEDULE 작업 차량 번호
			editData.add("");

			// SCH_WDEMAND_TYPE --VARCHAR2(1) SCHEDULE 작업요구 형태
			editData.add(YmCommonConst.SCH_WDEMAND_TYPE_S);

			// WBOOK_SCH_ACT_DDTT --VARCHAR2(12) 작업예약 SCHEDULE 실행 일시
			if (i == 0) {
				wbookSchActDt = "";// YmCommonUtil.getStringYMDHM();
			}
			editData.add(wbookSchActDt);

			// SCH_WORK_DEMAND_DDTT --VARCHAR2(12) SCHEDULE 작업 요구 일시
			editData.add(getField(curWork, "WBOOK_DDTT"));

			// SCH_WORK_DEMAND_PARTY --VARCHAR2(2) SCHEDULE 작업 요구 조
			editData.add(getField(curWork, "WBOOK_DUTY"));

			editData.add(getField(curWork, "WBOOK_PARTY"));

			// WBOOK_ID VARCHAR2(18) 작업예약 ID
			editData.add(getField(curWork, "WBOOK_ID"));

			// FRTOMOVE_EQUIP_GP VARCHAR2(6) 이송 설비 구분
			editData.add(getField(curWork, "FRTOMOVE_EQUIP_GP"));

			// CAR_CARD_NO VARCHAR2(10) 차량 CARD 번호
			editData.add(getField(curWork, "CAR_CARD_NO"));

			// STACK_STAT VARCHAR2(1) 적재 상태
			editData.add(getField(curWork, "STACK_STAT"));

			// REGISTER --VARCHAR2(10) 등록자
			editData.add("SYSTEM");

			// REG_DDTT --DATE 등록 일시
			// SYSDATE

			// MODIFIER --VARCHAR2(10) 수정자
			// ''

			// MOD_DDTT --DATE 수정 일시
			// ''

			// DEL_YN --VARCHAR2(1) 삭제 유무
			// 'N';
			ymCommonDAO.createSlabSchedule(editData);
		}
	}

	private JDTORecord convertMain(JDTORecord assign, JDTORecord curWork, JDTORecord toStoreLoc ,String bendingYN) throws Exception {


		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		String ToLoc 			= null;
		String FromLoc 			= null;
		String stockId 			= null;
		String mainSubGp 		= null;
		String wbookSchActDt	= null;
		String craneGp 			= null;


		stockId 		= getField(curWork, "STOCK_ID");
		mainSubGp 	= getField(curWork, "IS_MAIN");
		ToLoc 		= getField(toStoreLoc, "TO_LOC");
		FromLoc 		= getField(assign, "STACK_COL_GP"); // From Location 적치열

		logger.println(LogLevel.DEBUG, this, "SLAB From LOC =>" + FromLoc);

		
		if(bendingYN.equals("S")){
			assign.setField("SCH_RULE_WPREFER", "1");
			assign.setField("SCH_RULE_ALTER_WPREFER", "1");
			assign.setField("SCH_RULE_CRANE_NO", "E3");
			assign.setField("SCH_RULE_ALTER_CRANE_NO", "E2");
		}else{
			/*
			 * 에외 조건 해당 1:  W/B 보급, CTC보급 -> 대차 하차
			 *			     2:, C동 CTC 보급
			 * 
			 */
			craneGp = convertCR(getField(assign, "SCH_WORK_KIND"), 
					            ToLoc, 
					            mainSubGp,
					            FromLoc);
	
			if ("1".equals(craneGp)) {
				JDTORecord infoJr = dao.getConvertCraneInfo(ToLoc.substring(0, 1), 
															ToLoc.substring(1, 2),
															YmCommonConst.NEW_SCH_WORK_KIND_STMU);
	
				if (infoJr != null) {
					assign.setField("SCH_RULE_ACTIVE_STAT", infoJr.getFieldString("SCH_RULE_ACTIVE_STAT"));
					assign.setField("SCH_RULE_CRANE_NO", infoJr.getFieldString("SCH_RULE_CRANE_NO"));
					assign.setField("SCH_RULE_ALTER_CRANE_NO", infoJr.getFieldString("SCH_RULE_ALTER_CRANE_NO"));
					//assign.setField("SCH_WORK_KIND",YmCommonConst.NEW_SCH_WORK_KIND_STMU);
				}
			} else if ("2".equals(craneGp)) {
					assign.setField("SCH_RULE_WPREFER", "1");
					assign.setField("SCH_RULE_ALTER_WPREFER", "1");
					assign.setField("SCH_RULE_CRANE_NO", "C1");
					assign.setField("SCH_RULE_ALTER_CRANE_NO", "C1");
			}
		
		}
		
		return assign;

	}

	/**
	 * 스케쥴 테이블에 INSERT
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param ydSchDAO
	 * @param assign		= 스케쥴기준정보
	 * @param csWorkTarget	= 작업대상
	 * @param toStoreLoc	= TO 위치
	 * @param grip			= GRIP 대상
	 * @throws Exception
	 * @ejb.transaction type="RequiresNew"
	 */
	public void createSchedulePer(JDTORecord assign, JDTORecord curWork, JDTORecord toStoreLoc) throws Exception {

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
		List editData = null;

		String FromLoc = null;
		String ToLoc = null;
		String stockId = null;
		String mainSubGp = null;
		String wbookSchActDt = null;
		JDTORecord temp = null;
		String craneGp = null;

		editData = new ArrayList();

		stockId = getField(curWork, "STOCK_ID");
		mainSubGp = getField(curWork, "IS_MAIN");
		ToLoc = getField(toStoreLoc, "TO_LOC");
		FromLoc = getField(assign, "STACK_COL_GP"); // From Location 적치열
		
		logger.println(LogLevel.DEBUG, this, "SLAB From LOC =>" + FromLoc);
		
		// STOCK_ID --VARCHAR2(11) Not Null 저장품 ID
		editData.add(stockId);

		// SCH_RULE_ID --VARCHAR2(18) Not Null SCHEDULE 기준 ID
		editData.add(getField(assign, "SCH_RULE_ID"));

		// YD_GP --VARCHAR2(1) YARD 구분
		editData.add(getField(assign, "YD_GP"));

		// BAY_GP --VARCHAR2(1) 동 구분
		editData.add(getField(assign, "BAY_GP"));

		// /SCH_WORK_EQUIP_NO --VARCHAR2(2) SCHEDULE 작업 설비 번호
		/**
		 * C2 불가영역 W/B 보급인경우 C1 할당 스케쥴 등록 -아래 주석처리함.
		 */
		// editData.add(getSchWorkEquipNo(assign, curWork));
		
		
		/*
		 * 에외 조건 해당 1:  W/B 보급, CTC보급 -> 대차 하차
		 *			     2:, C동 CTC 보급
		 * 
		 */
		craneGp = convertCR(getField(assign, "SCH_WORK_KIND"), ToLoc, mainSubGp, FromLoc);
		logger.println(LogLevel.DEBUG, this, "craneGp =>" + craneGp);
		logger.println(LogLevel.DEBUG, this, "craneGp =>" + getField(assign, "SCH_WORK_KIND"));
		logger.println(LogLevel.DEBUG, this, "craneGp =>" + ToLoc);
		logger.println(LogLevel.DEBUG, this, "craneGp =>" + mainSubGp);
		logger.println(LogLevel.DEBUG, this, "craneGp =>" + FromLoc);
		
		if ("1".equals(craneGp)) {
			JDTORecord infoJr = dao.getCoilCraneInfo(ToLoc.substring(0, 1), ToLoc.substring(1,2), YmCommonConst.NEW_SCH_WORK_KIND_STMU);

			if (infoJr != null) {
				// SCH_WORK_EQUIP_NO --VARCHAR2(2) SCHEDULE 작업 설비 번호
				editData.add(infoJr.getFieldString("SELECT_CRANE_NO"));

				// SCH_WPREFER --NUMBER(2) SCHEDULE 작업우선순위
				String sPrior = StringHelper.evl(infoJr.getFieldString("SELECT_WPREFER"), "9");
				editData.add(new Integer(sPrior));
				logger.println(LogLevel.DEBUG, this, "sPrior =>" + sPrior);

			}
		} else {
			// SCH_WORK_EQUIP_NO --VARCHAR2(2) SCHEDULE 작업 설비 번호
			editData.add(getSchWorkEquipNo(assign));

			// SCH_WPREFER --NUMBER(2) SCHEDULE 작업우선순위
			editData.add(new Integer(getSchWprefer(assign)));
			logger.println(LogLevel.DEBUG, this, "sPrior1 =>" + getSchWprefer(assign));
		}

		/*
		 * 2006.11.29 설비 번호 변환
		 * 
		 * //SCH_WORK_EQUIP_NO --VARCHAR2(2) SCHEDULE 작업 설비 번호
		 * editData.add(getSchWorkEquipNo(assign));
		 * 
		 * 
		 * //SCH_WORK_STAT --VARCHAR2(1) SCHEDULE 작업 상태 //"S":Schedule
		 * 
		 * 
		 * //SCH_WPREFER --NUMBER(2) SCHEDULE 작업우선순위 //editData.add(new
		 * Integer(getSchWprefer(assign)));
		 * 
		 * editData.add(convertSchPrefer(getField(assign, "SCH_WORK_KIND"),
		 * getField(toStoreLoc, "TO_LOC"), mainSubGp, getSchWprefer(assign)));
		 */

		// SCH_WORK_KIND --VARCHAR2(4) SCHEDULE 작업 종류
		editData.add(convertSchCd(getField(assign, "SCH_WORK_KIND"), getField(toStoreLoc, "TO_LOC"), mainSubGp));

		// SCH_WORK_AID_YN --VARCHAR2(1) SCHEDULE 작업 보조 유무
		if (YmCommonConst.MAIN.equals(mainSubGp)) {
			editData.add(YmCommonConst.MAIN_WORK_M);
		} else if (YmCommonConst.SUB.equals(mainSubGp)) {
			editData.add(YmCommonConst.SUB_WORK_S);
		}

		// SCH_WORK_GRIP_LOT_YN --VARCHAR2(1) SCHEDULE 작업 GRIP LOT 유무
		editData.add(YmCommonConst.GRIP_LOT_YN_T);

		// CRANE_WORD_UP_LOC --VARCHAR2(10) CRANE 작업지시 UP 위치
		editData.add(getField(curWork, "FORM_LOC"));

		// WBOOK_LOC_DECISION_METHOD --VARCHAR2(1) 작업예약 위치 결정 방법
		editData.add(getField(curWork, "SCH_WORK_LOC_DECISION_METHOD"));

		// CRANE_WORD_PUT_LOC --VARCHAR2(10) CRANE 작업지시 PUT 위치
		editData.add(getField(toStoreLoc, "TO_LOC"));

		// SCH_WORK_CAR_NO --VARCHAR2(12) SCHEDULE 작업 차량 번호
		editData.add("");

		// SCH_WDEMAND_TYPE --VARCHAR2(1) SCHEDULE 작업요구 형태
		editData.add(YmCommonConst.SCH_WDEMAND_TYPE_S);

		// WBOOK_SCH_ACT_DDTT --VARCHAR2(12) 작업예약 SCHEDULE 실행 일시
		wbookSchActDt = "";// YmCommonUtil.getStringYMDHM();

		editData.add(wbookSchActDt);

		// SCH_WORK_DEMAND_DDTT --VARCHAR2(12) SCHEDULE 작업 요구 일시
		editData.add(getField(curWork, "WBOOK_DDTT"));

		// SCH_WORK_DEMAND_PARTY --VARCHAR2(2) SCHEDULE 작업 요구 조
		editData.add(getField(curWork, "WBOOK_DUTY"));

		editData.add(getField(curWork, "WBOOK_PARTY"));

		// WBOOK_ID VARCHAR2(18) 작업예약 ID
		editData.add(getField(curWork, "WBOOK_ID"));

		// FRTOMOVE_EQUIP_GP VARCHAR2(6) 이송 설비 구분
		editData.add(getField(curWork, "FRTOMOVE_EQUIP_GP"));

		// CAR_CARD_NO VARCHAR2(10) 차량 CARD 번호
		editData.add(getField(curWork, "CAR_CARD_NO"));

		// STACK_STAT VARCHAR2(1) 적재 상태
		editData.add(getField(curWork, "STACK_STAT"));

		// REGISTER --VARCHAR2(10) 등록자
		editData.add("SYSTEM");

		// REG_DDTT --DATE 등록 일시
		// SYSDATE

		// MODIFIER --VARCHAR2(10) 수정자
		// ''

		// MOD_DDTT --DATE 수정 일시
		// ''

		// DEL_YN --VARCHAR2(1) 삭제 유무
		// 'N';
		ymCommonDAO.createSlabSchedule(editData);

	}

	/**
	 * 스케쥴 작업종류가 W/B보급,CTC보급이고 To위치가 W/B, CTC가 아니면 스케쥴코드를 변경해서 등록한다.
	 * 
	 * @param String
	 *            스케쥴작업종류
	 * @param String
	 *            TO LOC
	 * @return
	 */
	private String convertCR(String sSchCd, String sToLoc, String sMainSubGp, String sFromLoc) {

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		String craneGp = "0";
        
		logger.println(LogLevel.DEBUG, this, "SCHEDULE  =>" + sSchCd);
		logger.println(LogLevel.DEBUG, this, "sFromLoc  =>" + sFromLoc.substring(0, 5));
		
		if (YmCommonConst.MAIN.equals(sMainSubGp)) {

			if (YmCommonConst.NEW_SCH_WORK_KIND_SWLI.equals(sSchCd)) { // W/B보급
				if (!"WB".equals(sToLoc.substring(2, 4))) {
					logger.println(LogLevel.DEBUG, this, "Crane 변경 => 1Case ");
					return "1";
				} else if ("2CTC3".equals(sFromLoc.substring(0, 5))) {
					logger.println(LogLevel.DEBUG, this, "Crane 변경 => 2Case ");
					return "2";
				}
			} else if (YmCommonConst.NEW_SCH_WORK_KIND_SCLI.equals(sSchCd)) { // CTC보급
				if (!"CT".equals(sToLoc.substring(2, 4))) {
					logger.println(LogLevel.DEBUG, this, "Crane 변경 => 1Case ");
					return "1";
				}
			} 
		}
		
		return "0";
	}

	/**
	 * 스케쥴 작업종류가 W/B보급,CTC보급이고 To위치가 W/B, CTC가 아니면 스케쥴코드를 변경해서 등록한다.
	 * 
	 * @param String
	 *            스케쥴작업종류
	 * @param String
	 *            TO LOC
	 * @return
	 */
	private String convertSchCd(String sSchCd, String sToLoc, String sMainSubGp) {

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		if (YmCommonConst.MAIN.equals(sMainSubGp)) {

			if (YmCommonConst.NEW_SCH_WORK_KIND_SWLI.equals(sSchCd)) { // W/B보급
				if (!"WB".equals(sToLoc.substring(2, 4))) {
					sSchCd = YmCommonConst.NEW_SCH_WORK_KIND_STMU; // 대차하차
					logger.println(LogLevel.DEBUG, this, "스케쥴코드변경= W/B보급(SWLI) => 대차하차(STMU)");
				}
			} else if (YmCommonConst.NEW_SCH_WORK_KIND_SCLI.equals(sSchCd)) { // CTC보급
				if (!"CT".equals(sToLoc.substring(2, 4))) {
					sSchCd = YmCommonConst.NEW_SCH_WORK_KIND_STMU; // 대차하차
					logger.println(LogLevel.DEBUG, this, "스케쥴코드변경= CTC보급(SCLI) => 대차하차(STMU)");
				}
			}
		}
		return sSchCd;
	}

	/**
	 * 스케쥴 작업종류가 W/B보급,CTC보급이고 To위치가 W/B, CTC가 아니면 스케쥴코드를 변경해서 등록한다.
	 * 
	 * @param String
	 *            스케쥴 우선순위
	 * @param String
	 *            TO LOC
	 * @return
	 */
	private Integer convertSchPrefer(String sSchCd, String sToLoc, String sMainSubGp, String sPrior) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		boolean isTrue = false;

		if (YmCommonConst.MAIN.equals(sMainSubGp)) {

			if (YmCommonConst.NEW_SCH_WORK_KIND_SWLI.equals(sSchCd)) { // W/B보급
				if (!"WB".equals(sToLoc.substring(2, 4))) {
					isTrue = true;
					logger.println(LogLevel.DEBUG, this, "스케쥴우선순위변경= W/B보급(SWLI) => 대차하차(STMU)");
				}
			} else if (YmCommonConst.NEW_SCH_WORK_KIND_SCLI.equals(sSchCd)) { // CTC보급
				if (!"CT".equals(sToLoc.substring(2, 4))) {
					isTrue = true;
					logger.println(LogLevel.DEBUG, this, "스케쥴우선순위변경= CTC보급(SCLI) => 대차하차(STMU)");
				}
			}
		}

		if (isTrue) {

			JDTORecord infoJr = dao.getCoilCraneInfo(sToLoc.substring(0, 1),
													 sToLoc.substring(1, 2),
													 YmCommonConst.NEW_SCH_WORK_KIND_STMU);

			if (infoJr != null) {
				sPrior = StringHelper.evl(infoJr.getFieldString("SELECT_WPREFER"), "9");
			}
		}

		return new Integer(sPrior);
	}

	/**
	 * @param mainCSWork
	 */
	private void callCRWorkOrder(JDTORecord mainCSWork) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return;
		}
		
		if (mainCSWork == null) {
			return;
		}
		JDTORecord dto = null;
		EJBConnector ejbConn = null;
		try {
			String yd = getField(mainCSWork, "YD_GP");
			String bay = getField(mainCSWork, "BAY_GP");
			String equipNo = getSchWorkEquipNo(mainCSWork);
			dto = ymCommonDAO.readEquipInfo(yd + bay + YmCommonConst.EQUIP_KIND_CR + equipNo);
			String L2Tcname ="";
			if("0".equals(yd) && YmCommonConst.BAY_GP_A.equals(bay)){
				L2Tcname = YmCommonConst.TC_HM1PB02;
			}else if("0".equals(yd) && YmCommonConst.BAY_GP_B.equals(bay)){
				L2Tcname = YmCommonConst.TC_HM1PB52;
			}else{
				L2Tcname = YmCommonConst.TC_CM1PB02;
			}
			
			logger.println(LogLevel.DEBUG, this, "크레인 작업요구 한다.");
			if (YmCommonConst.WPROG_STAT_W.equals(getField(dto, "WPROG_STAT"))) {
				ejbConn = new EJBConnector("default", "JNDICWrkOrdReg", this);
				ejbConn.trx("callCraneSchInfo", new Class[] { String.class,	String.class, String.class, String.class, String.class,	String.class, String.class }
											  , new Object[] {L2Tcname									//전문번호
															, yd										//야드구분
															, bay										//동구분
															, YmCommonConst.EQUIP_KIND_CR				//설비종류
															, equipNo									//설비번호
															, getField(mainCSWork, "SCH_WORK_KIND")		//스케쥴코드
															, "" });									//작업예약ID
			}
		} catch (Exception e) {
			logger.println(LogLevel.DEBUG, this, "##### 스케쥴 작업지시 ERROR");
			e.printStackTrace();
		}
	}

	/**
	 * 오퍼레이션명 : 한건 데이타를 update한다.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */            
	public int updateBlncBas(String queryid, List listData)	throws EJBServiceException, DAOException {
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			StockingBlncBasDAO ydstackcolDAO = new StockingBlncBasDAO();
			return ydstackcolDAO.updateData(queryid, listData);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
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
	public int deleteBlncBas(String queryid, List whereData) throws EJBServiceException, DAOException {
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			StockingBlncBasDAO stockingblncbasDAO = new StockingBlncBasDAO();
			return stockingblncbasDAO.deleteData(queryid, whereData);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
	}

	/**
	 * 오퍼레이션명 : 적치 발란스 데이타를 insert한다.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */             
	public int insertBlncBas(String queryID, List listData)	throws EJBServiceException, DAOException {
		try {
			StockingBlncBasDAO stockingblncbasDAO = new StockingBlncBasDAO();
			return stockingblncbasDAO.insertData(queryID, listData);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
	}

	/**
	 * 오퍼레이션명 : A열연 B동 냉각장 적치 크레인 동시작업을 위한 임시 메소드
	 *
	 * param String :
	 *            작업예약ID
	 * param String :
	 *            크레인번호
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                
	public boolean callCraneSchInfo(String sWbookId, String sCraneNo) {

		boolean isSuccess = false;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			/*
			 * 1. 스케쥴 생성
			 */
			isSuccess = callCraneSchInfo_01(sWbookId);

			/*
			 * 2. 작업지시요구
			 */
			isSuccess = callCwrkOrdReg(sWbookId, sCraneNo);

		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return isSuccess;
	}

	/**
	 * 오퍼레이션명 : 작업예약 업무 수행후 스케쥴 생성 CALL METHOD
	 *
	 * param String :
	 *            작업예약ID
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                
	public boolean callCraneSchInfo(String sWbookId) {

		boolean isSuccess = false;

		try {
			
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			/*
			 * 1. 스케쥴 생성
			 */
			isSuccess = callCraneSchInfo_01(sWbookId);

			/*
			 * 2. 작업지시요구
			 */
			isSuccess = callCwrkOrdReg(sWbookId, "");

		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return isSuccess;
	}
	
	
	/**
	 * 오퍼레이션명 : 작업예약 업무 수행후 스케쥴 생성 CALL METHOD
	 *
	 * param String :
	 *            작업예약ID
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean callCraneSchInfoRecord(JDTORecord msgRecord)throws JDTOException  { 

		boolean isSuccess = false;
		String szWBOOK_ID	="";
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			szWBOOK_ID 	= StringHelper.evl(msgRecord.getFieldString("WBOOK_ID"), "");
			/*
			 * 1. 스케쥴 호출 
			 */
			isSuccess = callCraneSchInfo(szWBOOK_ID);
 

		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return isSuccess;
	}

	/**
	 * 작업예약 업무 수행후 스케쥴 생성 CALL METHOD
	 * 
	 * @param String :
	 *            작업예약ID
	 * 
	 * @return true(성공)/false(실패)
	 * @throws
	 */
	private boolean callCraneSchInfo_01(String sWbookId) {

		boolean isSuccess = false;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			// /////////////////////////////////////////////////////////////////////////////
			// ///////////////////[1].작업예약TABLE정보 가져오기.///////////////////////////////
			// /////////////////////////////////////////////////////////////////////////////
			/*
			 * 1. 작업예약 ID 를 FK로 가지고 있는 저장품 ID 정보를 가져온다. 주작업대상재료만 일단 가져온다. 
			 * 2. 저장품의 작업순서항목(정정보급순서)으로 순위를 정한다. 
			 * 3. 해당 작업예약ID의 YD_GP, BAY_GP에 현재저장품이 존재해야 한다. 
			 * 4. 다른 동에 있는 저장품은 가져오지 못한다.
			 */
			logger.println(LogLevel.DEBUG, this, "=COIL SCHEDULE STEP 1 START=");
			List stock_list = dao.getCoilBookedStockList(sWbookId);

			if (stock_list == null || stock_list.size() < 1) {
				logger.println(LogLevel.DEBUG, this, "#######################");
				logger.println(LogLevel.DEBUG, this, "코일스케쥴 에러.");
				logger.println(LogLevel.DEBUG, this, "주작업대상 저장품 없슴.");
				logger.println(LogLevel.DEBUG, this, "#######################");
		 		return isSuccess;
			} else {
				JDTORecord stockV1 = null;
				JDTORecord stockV2 = null;
				for (int inx = 0; inx < stock_list.size(); inx++) {
					stockV1 = (JDTORecord) stock_list.get(inx);
					stockV2 = dao.getSchInfoWithWbookId(sWbookId, StringHelper.evl(stockV1.getFieldString("STOCK_ID"), ""));
					if (stockV2 != null) {
						logger.println(LogLevel.DEBUG, this, "################");
						logger.println(LogLevel.DEBUG, this, "코일스케쥴 에러.");
						logger.println(LogLevel.DEBUG, this, "주작업대상 저장품 이미 스케쥴 등록됨.");
						logger.println(LogLevel.DEBUG, this, "################");
						return isSuccess;
					}
				}
			}
			logger.println(LogLevel.DEBUG, this, "=COIL SCHEDULE STEP 1 END=");
			// /////////////////////////////////////////////////////////////////////////////
			// //////////////////[2].작업대상재료별로 FROM위치 확인하기.////////////////////
			// /////////////////////////////////////////////////////////////////////////////
			/*
			 * 1. 주작업대상재료의 From위치를 확인하다. 주작업대상재료의 저장품ID항목을 가지고 적치단TABLE의 해당 위치를검색한다. 
			 * 2. 보조대상재료가 존재하는지를 체크한다. 
			 *   1) 주작업대상재료가 2단일 경우 - 보조작업대상재료는 존재하지 않는다. 
			 *   2) 주작업대상재료가 1단일 경우 - -1번지의 2단을 체크한다. - 같은번지의 2단을 체크한다. 
			 *   3) 보조대상재료의 상태값에 따른 처리방법 a. 'E'적치가능한위치 - 보조작업대상에서 제외 b. 'V','X'적치불가 -보조작업대상에서 제외 b.
			 *    'L'적치중 - 보조작업대상 c. 'S','U'스케쥴수행 
			 *    - c_1.같은스케쥴코드일 경우처리방법
			 *    - 같은 작업단위로 묶인 저장품이든, 다른 작업단위로 묶인 저장품이든 이미 같은 스케쥴 처리로 예약이 되어 있으므로 보조작업대상에서 제외한다. 
			 *    - 주작업,보조작업 구분은 저장품TABLE의 저장품이동경로 항목으로 체크할 수있으명 적치단TABLE의 보조작업구분 항목으로도 체크할 수 있다. 
			 *    c_2. 다른스케쥴코드일 경우 처리방법 
			 *    - 처리해야할 여러 경우의 수를 나중에 추가한다.
			 *    - 보조작업으로 수행해야 하는경우, 아니면 전체 스케쥴 자체를 취소해야하는 경우등이 존재할 수 있다.
			 *    - 일단 보조작업에서 제외한다. c. 'P'스케쥴수행 
			 *    - 일단 무시 
			 * 3. 보조작업대상재료의 스케쥴코드 및 보조작업구분 및 적치상태값 
			 *   1) 스케쥴코드는 주작업대상재료의 스케쥴코드를 셋팅한다.
			 *   2) 보조작업구분은 보조작업으로 셋팅한다. 
			 *   3) 적치상태값은 스케쥴처리중 항목으로 셋팅한다.
			 *  - 위 사항은 작업대상조정이 끝난 후 스케쥴 등록시점에 처리하도록 한다. 
			 *  - 스케쥴등록시점에 주작업,보조작업 대상재의 상태정보를 UPDATE한다. 
			 * 4. 저장품ID에 대한 적치단 정보는 작업요구등록시 적치단 상태정보를 'S'-스케쥴수행 값으로 셋팅(예정)하기 때문에 'S'인 정보를 검색한다. 
			 * 5. 적치단TABLE에 보조작업일 경우 주작업대상재료의 스케쥴코드로셋팅하고, 보조작업대상재료는 저장품TABLE의 저장품이동조건은 보조작업항목으로 셋팅 
			 *  - 지금은 주작업인지 보조작업인지는 GBN(구분항목)값으로 우선 체크한다.
			 */
			logger.println(LogLevel.DEBUG, this, "=COIL SCHEDULE STEP 2 START=");
			List fromStock_list = new ArrayList();
			{
				JDTORecord stockV = null;

				for (int inx = 0; inx < stock_list.size(); inx++) {

					stockV = (JDTORecord) stock_list.get(inx);

					fromStock_list = getCoilStockFromLoc(inx + 1, stockV, fromStock_list);
				}
			}
			{
				logger.println(LogLevel.DEBUG, this, "작업대상 재료 선정 BEFORE=====");

				JDTORecord stockV = null;
				String sGbn = "";
				String sOrderBy = "";
				String sTmpStockId = "";
				String sTmpColGp = "";
				String sTmpBedGp = "";
				String sTmpLayerGp = "";
				for (int iny = 0; iny < fromStock_list.size(); iny++) {

					stockV = (JDTORecord) fromStock_list.get(iny);

					if (stockV != null) {
						sGbn 		= StringHelper.evl(stockV.getFieldString("GBN"),"");
						sOrderBy 	= StringHelper.evl(stockV.getFieldString("ORDER_BY"), "");
						sTmpStockId = StringHelper.evl(stockV.getFieldString("STOCK_ID"), "");
						sTmpColGp 	= StringHelper.evl(stockV.getFieldString("STACK_COL_GP"), "");
						sTmpBedGp 	= StringHelper.evl(stockV.getFieldString("STACK_BED_GP"), "");
						sTmpLayerGp = StringHelper.evl(stockV.getFieldString("STACK_LAYER_GP"), "");
						logger.println(LogLevel.DEBUG, this, "BEFORE  =" + sGbn
															 + "=" + sOrderBy 
															 + "=" + sTmpStockId 
															 + "=" + sTmpColGp 
															 + "=" + sTmpBedGp 
															 + "=" + sTmpLayerGp);
					}
				}
				logger.println(LogLevel.DEBUG, this, "작업대상 재료 선정 AFTER=====");
			}
			logger.println(LogLevel.DEBUG, this, "=COIL SCHEDULE STEP 2 END=");
			// /////////////////////////////////////////////////////////////////////////////
			// ///////////////////[3]작업대상을 조정한다.(Grip Lot)/////////////////////////////
			// /////////////////////////////////////////////////////////////////////////////
			/*
			 * 1. 주작업대상재료 및 보조작업대상재료 중 중복으로 선정된 대상재료를 제거하는 기능이다.
			 */
			logger.println(LogLevel.DEBUG, this, "=COIL SCHEDULE STEP 3 START=");
			{
				fromStock_list = gripCoilLot(fromStock_list);
			}
			{
				JDTORecord stockV = null;
				String sGbn = "";
				String sOrderBy = "";
				String sTmpStockId = "";
				String sTmpColGp = "";
				String sTmpBedGp = "";
				String sTmpLayerGp = "";
				for (int iny = 0; iny < fromStock_list.size(); iny++) {

					stockV = (JDTORecord) fromStock_list.get(iny);

					if (stockV != null) {
						sGbn 		= StringHelper.evl(stockV.getFieldString("GBN"),"");
						sOrderBy 	= StringHelper.evl(stockV.getFieldString("ORDER_BY"), "");
						sTmpStockId = StringHelper.evl(stockV.getFieldString("STOCK_ID"), "");
						sTmpColGp 	= StringHelper.evl(stockV.getFieldString("STACK_COL_GP"), "");
						sTmpBedGp 	= StringHelper.evl(stockV.getFieldString("STACK_BED_GP"), "");
						sTmpLayerGp = StringHelper.evl(stockV.getFieldString("STACK_LAYER_GP"), "");
						logger.println(LogLevel.DEBUG, this, "AFTER    =" + sGbn
															 + "=" + sOrderBy 
															 + "=" + sTmpStockId 
															 + "=" + sTmpColGp 
															 + "=" + sTmpBedGp 
															 + "=" + sTmpLayerGp);
					}
				}
				logger.println(LogLevel.DEBUG, this, "작업대상 재료 선정 END======");
			}
			logger.println(LogLevel.DEBUG, this, "=COIL SCHEDULE STEP 3 END=");
			// /////////////////////////////////////////////////////////////////////////////
			// ///////////////////[4]Crane 할당한다./////////////////////////////////////////
			// /////////////////////////////////////////////////////////////////////////////
			/*
			 * 1. 스케쥴기준TABLE에 야드구분+동구분+스케쥴코드 별로 Crane이 등록되어 있다. 
			 * 따라서 작업예약TABLE의 야드구분+동구분+스케쥴코드에 따른 Crane을 할당하면 된다. 
			 * 2. 스케쥴기준활성상태가 'A'이면 정상적인스케쥴기준으로 등록하고, 스케쥴기준활성상태가 'B'이면 대체 스케쥴기준으로 등록하고, 스케쥴기준활성상태가 'X'이면 스케쥴기준으로 제외한다. 
			 * 3. 스케쥴기준에 등록된 Crane상태를 Check한다. - 설비TABLE의설비상태(EQUIP_STAT)항목(작업대기,작업중,휴지또는고장) 
			 * 4. 야드구분+동구분+스케쥴코드에 따른 Crane의 작업우선순위 정보도 검색한다. 
			 * 5. 작업수행은 스케쥴TABLE의 작업우선순위,작업요구일시 순으로 작업을 처리한다.
			 * 6. Crane고장 및 휴지시 처리방법 - 기 스케쥴 등록 - 스케쥴TABLE의 Crane정보를 대체Crane정보로전환한다. 
			 *   - 대체Crane의 스케쥴과 대체Crane으로 넘어온 스케쥴의 작업우선순위 정리는 따로 처리한다. 
			 *   - Crane이 복귀되면 대체Crane의 스케쥴을 다시 가져온다. 
			 *   - 다시 가져올 경우 대체Crane에 의해 수행되던 작업우선순위는 이미 수정이 되었으므로,원 작업우선순위를 따로 관리할 필요가 있다. 
			 *   - 스케쥴 등록예정 
			 *   - 스케쥴등록처리시 스케쥴기준TABLE의 대체Crane으로 스케쥴TABLE을 등록한다. 
			 * 7. 작업우선순위 변경에 따른 기존 스케쥴정보의 처리방법 
			 *   - 기존 스케쥴정보의 작업우선순위 항목을 변경된 작업우선순위 값으로 수정한다.
			 */
			logger.println(LogLevel.DEBUG, this, "=COIL SCHEDULE STEP 4 START=");
			List craneStock_list = new ArrayList();
			{
				JDTORecord stockV = null;
				JDTORecord craneV = null;

				for (int inx = 0; inx < fromStock_list.size(); inx++) {

					stockV = (JDTORecord) fromStock_list.get(inx);
					craneV = getCoilCraneInfo(stockV);

					if (craneV == null) {
						logger.println(LogLevel.DEBUG, this, "################");
						logger.println(LogLevel.DEBUG, this, "코일스케쥴 에러.");
						logger.println(LogLevel.DEBUG, this, "크레인 정보를 가져오지 못했습니다.");
						logger.println(LogLevel.DEBUG, this, "################");
						return isSuccess;
					}

					craneStock_list.add(mixJDTORecord(stockV, craneV));
				}
			}
			logger.println(LogLevel.DEBUG, this, "=COIL SCHEDULE STEP 4 END=");
			// /////////////////////////////////////////////////////////////////////////////
			// /////////////////////[5]TO 위치  결정하기/////////////////////////////////////
			// /////////////////////////////////////////////////////////////////////////////
			/*
			 * Q1. 작업요구시 To위치검색영역이 지정이 된 경우의 처리 
			 * A1. To위치 지정의 경우의 수에 따라 다르다. 열단위 범위를 지정하느냐, 번지까지 상세하게 정하느냐 등... 
			 * A2. 한건의 작업요구에 N개의 저장품정보가 묶여 있다면 작업예약TABLE 의 CRANE_WORD_PUT_LOC 항목에 번지정보를 입력할 수 없다. 
			 * 적치열이나,적치단정보를 가지고 다시 번지정보를 검색해야 한다. 
			 * 1. 주작업,보조작업 별로 To 영역 검색기준을 체크해서 영역을 설정한다.
			 * 2. 저장품이동경로TABLE CHECK
			 *  - 작업대상재료의 현재위치정보의 적치열TABLE에서 적치열용도Code를 가져온다. 
			 *  - 작업대상재료의 저장품TABLE에서 저장품이동조건Code를 가져온다. 
			 *  - 작업대상재료의 작업예약TABLE에서 SCHEDULE Code를 가져온다. 
			 *  - 위의 조건과 YARD구분, 동구분, 저장품품목 항목을 검색조건으로 작업대상재료의 적합한 저장품이동경로를 찾는다. 
			 * 3. 위치검색TABLE CHECK 
			 *   - 저장품이동경로값을 가지고 위치검색테이블에서 정보를 검색한다. 
			 *  - 적치열구분항목과 적치열순서항목을 가지고 작업대상재료의 적합한 적치열정보를 찾은 후 적치단TABLE의 번지정보를 차례로 검색한다. 
			 * Q2. 가장 적합한 번지정보는? A1. 우선순위에의한 번지정보를 검색한 후 그 번지에 대한 적합성 모듈은 따로 존재해야 한다. 
			 * Q3. 저장품이동경로TABLE의저장품이동경로상태값(STACK_MOVE_ROUTE_STAT)을 조건에 반영여부를 체크 
			 * Q4. 적치대TABLE의적치BED활성상태값(STACK_BED_ACTIVE_STAT)을 조건에 반영여부를 체크
			 */
			/*
			 * [6]TO 위치 결정방법
			 * 1. To 영역 내 적치가능위치를 체크한다. 
			 *   - 적치열 항목값을 가지고 적치단TABLE을 검색한다. 
			 *   - 검색된 적치단정보를 가지고 가장 적합한 번지정보를 찾는다. 
			 *   - 가장 적합한 번지정보를 찾으면 해당 번지에 대해 스케쥴 등록시 예약 처리한다. 
			 *   - From 위치
			 *   - 'U' Up Schedule 수행으로 셋팅 
			 *   - To 위치
			 *   - 'P' Put Schedule 수행으로 셋팅 Q1. 번지정보 검색시 예약된 정보가 포함된다. 따라서 적합성 CHECK시 예약처리를 해야 하지 않는지? 
			 * 2. 2단에 대한 정보를 먼저 검색할지,1단에 대한 정보를 먼저 검색할지는 기준정보 로서 관리한다. 
			 *   - Order by stack_layer_gp desc 쿼리조건으로 우선순위 단을 설정한다.
			 * 3. 적치단 정보를 검색할 때 적치활성상태가 'O' OPEN 이고 적치상태가 'E' 적치가능상태인것을 검색한다. 
			 * 4. To위치검색에 실패했을 경우에는 비상적치위치에 셋팅한다. Q1. 비상적치 위치란?
			 */
			logger.println(LogLevel.DEBUG, this, "=COIL SCHEDULE STEP 5 START=");
			List schedule_list = new ArrayList();
			{
				int iSeq = 0;

				JDTORecord stockV = null;
				JDTORecord stockToV = null;

				for (int inx = 0; inx < craneStock_list.size(); inx++) {

					stockV = (JDTORecord) craneStock_list.get(inx);
					//TO위치 검색 
					stockToV = getCoilToLocInfo_001(stockV, "F");

					if (stockToV == null) {
						logger.println(LogLevel.DEBUG, this, "코일스케쥴 에러.");
						logger.println(LogLevel.DEBUG, this, "TO 위치를 가져오지 못했습니다.");

						return isSuccess;
					} else {
						logger.println(LogLevel.DEBUG, this, "DATA확인 :"+stockV);
						logger.println(LogLevel.DEBUG, this, "처리DATA확인 :"+ stockToV);
						iSeq = setCoilLocResumeInfo(stockV, stockToV);
						
						//스케줄 등록 
						iSeq = dao.insertScheduleInfo(getSchData(mixJDTORecord(stockV, stockToV)));
						//===============================================================================================================================================
						// SPM2추출(CNLO) 작업일 경우. 
						// 작업우선순위(sch_wprefer)항목에 stack_bed_gp(Position) 값을 입력한다.
						// 최규성 2010-01-27
						if (iSeq > 0)
						{
							
							String sYdGp = stockV.getFieldString("YD_GP");	// 야드구분
							String sSchWorkKind = stockV.getFieldString("SCH_WORK_KIND");
							
							if(YmCommonConst.YD_GP_3.equals(sYdGp) && YmCommonConst.NEW_SCH_WORK_KIND_CNLO.equals(sSchWorkKind)){
								String sStockId = stockV.getFieldString("STOCK_ID"); // 저장품ID
								String sStackBedGp = stockV.getFieldString("STACK_BED_GP"); // 적치BED구분
								logger.println(LogLevel.DEBUG, this, "SPM2 추출 => 작업우선순위 데이터 수정 :"+ sStackBedGp +" | "+sStockId);
								
								String sQueryId_wprefer = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateCraneStackLayerWprefer";
								/*
								 * UPDATE TB_YM_SCH
									SET SCH_WPREFER = :bed_gp
									WHERE SCH_WORK_KIND = 'CNLO'
									AND YD_GP='3'
									AND BAY_GP = 'E'
									AND STOCK_ID = :stockid
								 */
								int iVal = dao.updateData(sQueryId_wprefer, new Object[]{sStackBedGp,sStockId});
								logger.println(LogLevel.DEBUG, this, "SPM2 추출 => 작업우선순위 데이터 수정 후 :"+ sStackBedGp +" | "+sStockId);
							}
						}
						//===============================================================================================================================================
					}
				}
			}
			logger.println(LogLevel.DEBUG, this, "=COIL SCHEDULE STEP 5 END=");
			/*
			 * { String key = ""; String sTmpStockId = "";
			 * 
			 * JDTORecord tmpV = null; java.util.Map mMap = null; java.util.Set
			 * set = null; Object []hmKeys = null;
			 * 
			 * for(int iny = 0; iny < schedule_list.size() ; iny++){
			 * 
			 * tmpV = (JDTORecord)schedule_list.get(iny); if(tmpV != null){ try{
			 * mMap = tmpV.getMap(); set = mMap.keySet(); hmKeys =
			 * set.toArray();
			 * 
			 * logger.println(LogLevel.DEBUG,this,"================================================================");
			 * logger.println(LogLevel.DEBUG,this,"
			 * 1="+(String)mMap.get("STOCK_ID")); // 저장품ID
			 * logger.println(LogLevel.DEBUG,this,"
			 * 2="+(String)mMap.get("CRANE_SCH_RULE_ID")); // SCH 기준 ID
			 * logger.println(LogLevel.DEBUG,this,"
			 * 3="+(String)mMap.get("YD_GP")); // YARD 구분
			 * logger.println(LogLevel.DEBUG,this,"
			 * 4="+(String)mMap.get("BAY_GP")); // 동 구분
			 * logger.println(LogLevel.DEBUG,this,"
			 * 5="+(String)mMap.get("SELECT_CRANE_NO")); // SCH 작업설비번호
			 * logger.println(LogLevel.DEBUG,this,"
			 * 7="+(String)mMap.get("SELECT_WPREFER")); // SCH 작업우선순위
			 * logger.println(LogLevel.DEBUG,this,"
			 * 8="+(String)mMap.get("SCH_WORK_KIND")); // SCH CODE
			 * logger.println(LogLevel.DEBUG,this,"
			 * 9="+(String)mMap.get("GBN")); // SCH 작업 보조유무
			 * logger.println(LogLevel.DEBUG,this,"10="+(String)mMap.get("STACK_COL_GP")); //
			 * CRANE 작업지시 UP위치
			 * logger.println(LogLevel.DEBUG,this,"11="+(String)mMap.get("STACK_BED_GP")); //
			 * CRANE 작업지시 UP위치
			 * logger.println(LogLevel.DEBUG,this,"12="+(String)mMap.get("STACK_LAYER_GP")); //
			 * CRANE 작업지시 UP위치
			 * logger.println(LogLevel.DEBUG,this,"13="+(String)mMap.get("TO_STACK_COL_GP")); //
			 * CRANE 작업지시 PUT위치
			 * logger.println(LogLevel.DEBUG,this,"14="+(String)mMap.get("TO_STACK_BED_GP")); //
			 * CRANE 작업지시 PUT위치
			 * logger.println(LogLevel.DEBUG,this,"15="+(String)mMap.get("TO_STACK_LAYER_GP")); //
			 * CRANE 작업지시 PUT위치
			 * logger.println(LogLevel.DEBUG,this,"16="+(String)mMap.get("SCH_WORK_LOC_DECISION_METHOD"));//
			 * 작업예약위치 결정방법
			 * logger.println(LogLevel.DEBUG,this,"car_no="+(String)mMap.get("CAR_CARD_NO")); //
			 * 차량카드번호
			 * logger.println(LogLevel.DEBUG,this,"17="+(String)mMap.get("XXXXXXXXXXX")); //
			 * 작업예약 위치 결정방법
			 * logger.println(LogLevel.DEBUG,this,"18="+(String)mMap.get("XXXXXXXXXXX")); //
			 * SCH 작업 GRIP LOT 유무
			 * logger.println(LogLevel.DEBUG,this,"19="+(String)mMap.get("XXXXXXXXXXX")); //
			 * SCH 작업 차량번호
			 * logger.println(LogLevel.DEBUG,this,"20="+(String)mMap.get("XXXXXXXXXXX")); //
			 * SCH 작업요구 형태
			 * logger.println(LogLevel.DEBUG,this,"21="+(String)mMap.get("XXXXXXXXXXX")); //
			 * SCH 작업예약 SCH 실행 일시
			 * 
			 * }catch(Exception e){
			 *  } } } }
			 */
			// /////////////////////////////////////////////////////////////////////////////
			// //////////////////////[7] Schedule 등록///////////////////////////////////////
			// /////////////////////////////////////////////////////////////////////////////
			/*
			 * 1.Schedule Table 에 등록한다. 
			 *  - 스케쥴처리 기준 -동일작업Max건수를 Over하는가? 아니면 동일작업 Max건수만큼 수행한다. 
			 *  - Over하거나 동일작업이 없으면 전체검색 -> 다른 순위의 작업수행 
			 *  - 전체검색해서 작업이 없으면 Crane Idle 상태
			 */
			/*
			 * logger.println(LogLevel.DEBUG,this,"=COIL SCHEDULE STEP 6
			 * START="); { JDTORecord stockV = null;
			 * 
			 * int iSeq = -1;
			 * 
			 * for(int inx = 0; inx < schedule_list.size() ; inx++){
			 * 
			 * stockV = (JDTORecord)schedule_list.get(inx); iSeq =
			 * dao.insertScheduleInfo(getSchData(stockV)); }
			 * 
			 * if(iSeq > 0) isSuccess = true; }
			 * logger.println(LogLevel.DEBUG,this,"=COIL SCHEDULE STEP 6 END=");
			 */
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return isSuccess;
	}

	/**
	 * 보조작업대상재료중 중복되는 정보를 정리하는 method 이다. STOCK_ID 항목값을 비교해서 중보대상을 제외한다.
	 * 삭제대상(같은저장품) : 보조작업 -> 보조작업이면 삭제 주작업 -> 보조작업이면 삭제 보조작업 -> 주작업이면 보존
	 * @param map : List
	 * @return 	  : List
	 */
	private List gripCoilLot(List subList) {

		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		JDTORecord stockV1 = null;
		JDTORecord stockV2 = null;

		String sStockId1 = "";
		String sStockId2 = "";
		String sGbn1 = ""; // 주작업,보조작업
		String sGbn2 = ""; // 주작업,보조작업

		for (int inx = 0; inx < subList.size(); inx++) {

			stockV1 = (JDTORecord) subList.get(inx);
			sStockId1 = StringHelper.evl(stockV1.getFieldString("STOCK_ID"), "");
			sGbn1 = StringHelper.evl(stockV1.getFieldString("GBN"), "");

			for (int iny = inx + 1; iny < subList.size(); iny++) {

				stockV2 = (JDTORecord) subList.get(iny);
				sStockId2 = StringHelper.evl(stockV2.getFieldString("STOCK_ID"), "");
				sGbn2 = StringHelper.evl(stockV2.getFieldString("GBN"), "");

				if (sStockId1.equals(sStockId2)) {

					if (YmCommonConst.SUB_WORK_S.equals(sGbn1)
						&& YmCommonConst.SUB_WORK_S.equals(sGbn2)) {
						subList.remove(iny);
						iny--;
					} else if (YmCommonConst.MAIN_WORK_M.equals(sGbn1)
							&& YmCommonConst.SUB_WORK_S.equals(sGbn2)) {
						subList.remove(iny);
						iny--;
					}
				}
			}
		}

		return subList;
	}

	/**
	 * 두개의 JDTORecord를 하나의 JDTORecord로 합치는 method
	 * 
	 * @param map :
	 *            JDTORecord, JDTORecord
	 * 
	 * @return : JDTORecord
	 */
	private JDTORecord mixJDTORecord(JDTORecord a, JDTORecord b) {

		try {
			
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			String key = "";

			Map mMap = b.getMap();
			Set set = mMap.keySet();
			Object[] hmKeys = set.toArray();
			for (int i = 0; i < hmKeys.length; i++) {
				key = (String) hmKeys[i];
				a.setField(key, (String) mMap.get(key));
			}
		} catch (Exception e) {

		}
		return a;
	}

	/**
	 * 주작업대상 재료의 FROM 위치와 보조작업대상 재료를 체크한다.
	 * 
	 * @param DAO
	 * @param JDTORecord
	 * @param List
	 * 
	 * @return List
	 * @throws Exception
	 */
	private List getCoilStockFromLoc(int iOrder, JDTORecord stockV, List stockList) throws Exception {
		JDTORecord stockCurLocV = null;
		JDTORecord stockPreLocV = null;
		JDTORecord stockBacLocV = null;

		boolean isRight = false;
		boolean isLeft = false;
		try {
			
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			String sStockId = StringHelper.evl(stockV.getFieldString("STOCK_ID"), "");

			logger.println(LogLevel.DEBUG, this, "STOCK_ID =" + sStockId);

			/*
			 * 저장품ID 를 가지고 적치단정보를 가져온다. tb_ym_stacklayer Table
			 * stack_layer_active_stat : 'O'(Open) SADDLE인 경우 입측은
			 * stack_layer_active_stat = 'C' 이다. 따라서 위 조건은 제외시킨다.
			 * tb_ym_stacklayer Table stack_layer_stat : 'S'(Schedule 수행예정)
			 */
			JDTORecord layerRc = null;

			layerRc = dao.getStackLayerListWithStockId(sStockId,
			// YmCommonConst.STACK_LAYER_ACTIVE_STAT_O,
					YmCommonConst.STACK_LAYER_STAT_S);

			if (layerRc == null) {
				logger.println(LogLevel.DEBUG, this, "################");
				logger.println(LogLevel.DEBUG, this, "코일스케쥴 에러.");
				logger.println(LogLevel.DEBUG, this, "코일ID=" + sStockId);
				logger.println(LogLevel.DEBUG, this, "적치단 정보를 가져오지 못했습니다.");
				logger.println(LogLevel.DEBUG, this, "################");
				return stockList;
			}

			String sStackColGp 			= StringHelper.evl(layerRc.getFieldString("CUR_STACK_COL_GP"), "");
			String sStackBedGp 			= StringHelper.evl(layerRc.getFieldString("CUR_STACK_BED_GP"), "");
			String sStackLayerGp 		= StringHelper.evl(layerRc.getFieldString("CUR_STACK_LAYER_GP"), "");
			String sPreStackBedGp 		= StringHelper.evl(layerRc.getFieldString("PRE_STACK_BED_GP"), "");
			String sPreStackLayerGp 	= StringHelper.evl(layerRc.getFieldString("PRE_STACK_LAYER_GP"), "");
			String sBackStackBedGp 		= StringHelper.evl(layerRc.getFieldString("BACK_STACK_BED_GP"), "");
			String sBackStackLayerGp 	= StringHelper.evl(layerRc.getFieldString("BACK_STACK_LAYER_GP"), "");

			logger.println(LogLevel.DEBUG, this, "sPreStackBedGp 	=" + sPreStackBedGp);
			logger.println(LogLevel.DEBUG, this, "sPreStackLayerGp 	=" + sPreStackLayerGp);
			logger.println(LogLevel.DEBUG, this, "sBackStackBedGp 	=" + sBackStackBedGp);
			logger.println(LogLevel.DEBUG, this, "sBackStackLayerGp =" + sBackStackLayerGp);
			/*
			 * 주작업 대상재료의 정보 가져오기
			 */
			stockCurLocV = dao.getCoilMainStockFromLoc(sStackColGp, sStackBedGp, sStackLayerGp);

			String sStackColUsageCd = StringHelper.evl(stockCurLocV.getFieldString("STACK_COL_USAGE_CD"), "");

			int iStackColUsageCd = 0;
			try {
				iStackColUsageCd = sStackColUsageCd.charAt(1);
			} catch (java.lang.StringIndexOutOfBoundsException e) {
				iStackColUsageCd = 99;
			}

			/*
			 * 보조작업 대상재료의 정보 가져오기
			 */
			if (iStackColUsageCd < 58 && // 적치열 용도가 : 야드
				YmCommonConst.STACK_LAYER_GP_01.equals(sStackLayerGp)) { // 적치단
																				// :
																				// 01단

				if (!"".equals(sPreStackBedGp)) {
					/*
					 * 적치단 2단 1번지 보조작업 대상재료 정보 가져오기
					 */
					stockPreLocV = dao.getCoilSubStockFromLoc(sStackColGp, sPreStackBedGp, sPreStackLayerGp);
					if (stockPreLocV != null) {
						/*
						 * 보조작업 대상재료인지를 체크한다.
						 */
						isLeft = checkSubWork(stockCurLocV, stockPreLocV);

						/*
						 * 보조작업 대상재료이면 저장품 TABLE 에 작업예약 ID를 셋팅한다. tb_ym_stock
						 * Table wbook_id :주작업대상재료 wbook_id
						 * stockPreLocV.sch_work_kind :주작업대상재료 sch_work_kind
						 * stockPreLocV.sch_work_loc_decision_method :주작업대상재료
						 * sch_work_loc_decision_method
						 * stockPreLocV.crane_word_put_loc :주작업대상재료
						 * crane_word_put_loc
						 */
						if (isLeft) {
							stockPreLocV = setSubStockWorkId(stockCurLocV, stockPreLocV);
						}
					}
				}
				if (!"".equals(sBackStackBedGp)) {
					/*
					 * 적치단 2단 -1번지 보조작업 대상재료 정보 가져오기
					 */
					stockBacLocV = dao.getCoilSubStockFromLoc(sStackColGp, sBackStackBedGp, sBackStackLayerGp);
					if (stockBacLocV != null) {
						/*
						 * 보조작업 대상재료인지를 체크한다.
						 */
						isRight = checkSubWork(stockCurLocV, stockBacLocV);
						/*
						 * 보조작업 대상재료이면 저장품 TABLE 에 작업예약 ID를 셋팅한다. tb_ym_stock
						 * Table wbook_id :주작업대상재료 wbook_id
						 * stockBacLocV.sch_work_kind :주작업대상재료 sch_work_kind
						 * stockBacLocV.sch_work_loc_decision_method :주작업대상재료
						 * sch_work_loc_decision_method
						 * stockBacLocV.crane_word_put_loc :주작업대상재료
						 * crane_word_put_loc
						 */
						if (isRight) {
							stockBacLocV = setSubStockWorkId(stockCurLocV, stockBacLocV);
						}
					}
				}
			}

			if (isLeft) {
				stockPreLocV.setField("ORDER_BY", ((iOrder * 10)) - 2 + "");
				stockList.add(stockPreLocV);
			}
			if (isRight) {
				stockBacLocV.setField("ORDER_BY", ((iOrder * 10) - 1) + "");
				stockList.add(stockBacLocV);
			}
			if (stockCurLocV != null) {
				stockCurLocV.setField("ORDER_BY", (iOrder * 10) + "");
				stockList.add(stockCurLocV);
			}

		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}

		return stockList;
	}

	/**
	 * 보조대상작업 유효성을 체크한다.
	 * 
	 * @param JDTORecord :
	 *            주작업대상재료
	 * @param JDTORecord :
	 *            보조작업대상재료
	 * 
	 * @return JDTORecord
	 */
	private boolean checkSubWork(JDTORecord stockMainV, JDTORecord stockSubV) {

		boolean isTrue = false;

		try {
			
			
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String sSchWorkKind = StringHelper.evl(stockMainV
					.getFieldString("SCH_WORK_KIND"), "");
			String sSubStackLayerStat = StringHelper.evl(stockSubV
					.getFieldString("STACK_LAYER_STAT"), "");
			String sSubSchWorkKind = StringHelper.evl(stockSubV
					.getFieldString("SCH_WORK_KIND"), "");

			logger.println(LogLevel.DEBUG, this, "sSchWorkKind  	=" + sSchWorkKind);
			logger.println(LogLevel.DEBUG, this, "sSubStackLayerStat=" + sSubStackLayerStat);
			logger.println(LogLevel.DEBUG, this, "sSubSchWorkKind  	=" + sSubSchWorkKind);

			if (YmCommonConst.STACK_LAYER_STAT_L.equals(sSubStackLayerStat)) {
				/**
				 * 적치중('L')이면 보조작업대상
				 */
				logger.println(LogLevel.DEBUG, this, "적치중(L) = 보조작업 대상");
				isTrue = true;
			} else if (YmCommonConst.STACK_LAYER_STAT_P.equals(sSubStackLayerStat)) {
				/**
				 * PUT스케쥴수행예정('P')이면 보조작업대상에서 제외(일단 무시)
				 */
				logger.println(LogLevel.DEBUG, this, "PUT(P) = 보조작업 제외");
				isTrue = false;
			} else if (YmCommonConst.STACK_LAYER_STAT_U.equals(sSubStackLayerStat)) {

				if (sSchWorkKind.equals(sSubSchWorkKind)) {
					/**
					 * UP스케쥴수행예정('U')이고 주작업스케쥴코드와 같은 스케쥴코드이면 보조작업대상에서 제외
					 */
					logger.println(LogLevel.DEBUG, this, "같은 스케쥴코드에 U = 보조작업 제외");
					isTrue = false;
				} else {

					if ("".equals(sSubSchWorkKind)) {
						/**
						 * 적치중('L') 상태에서 UP스케쥴수행예정('U')으로 등록 (다른 작업예정의 보조작업 대상)
						 * 보조작업대상에서 제외
						 */
						logger.println(LogLevel.DEBUG, this, "스케쥴코드 없이 U = 보조작업 제외");
						isTrue = false;
					} else {

						JDTORecord cnM = null;
						JDTORecord cnS = null;

						if (cnM != null && cnS != null) {

							int iPriorM = Integer.parseInt(StringHelper.evl(cnM.getFieldString("SELECT_WPREFER"), "0"));
							int iPriorS = Integer.parseInt(StringHelper.evl(cnS.getFieldString("SELECT_WPREFER"), "0"));

							if (iPriorS != 0 && iPriorS < iPriorM) {
								/**
								 * UP스케쥴수행예정('U')이고 주작업스케쥴코드와 다른 스케쥴코드이면
								 * 크레인작업우선순위를 체크한다. 크레인작업우선순위가 빠르면 보조작업대상에서 제외
								 */
								logger.println(LogLevel.DEBUG, this, "다른 스케쥴코드 우선순위 빠르고 U = 보조작업 제외");
								isTrue = false;
							} else {
								/**
								 * UP스케쥴수행예정('U')이고 주작업스케쥴코드와 다른 스케쥴코드이면
								 * 크레인작업우선순위를 체크한다. 크레인작업우선순위가 늦으면 보조작업대상
								 */
								logger.println(LogLevel.DEBUG, this, "다른 스케쥴코드 우선순위 느리고 U = 보조작업 제외");
								isTrue = false;
							}
						}
					}
				}
			} else if (YmCommonConst.STACK_LAYER_STAT_S
					.equals(sSubStackLayerStat)) {

				/**
				 * 스케쥴수행예정('S')이면 보조작업대상
				 */
				if (YmCommonConst.NEW_SCH_WORK_KIND_GVFL.equals(sSubSchWorkKind)
						|| // COIL 제품출하상차
						YmCommonConst.NEW_SCH_WORK_KIND_GVF1.equals(sSubSchWorkKind)
						|| // COIL 제품출하상차
						YmCommonConst.NEW_SCH_WORK_KIND_GVF2.equals(sSubSchWorkKind)
						|| // COIL 제품출하상차
						YmCommonConst.NEW_SCH_WORK_KIND_GTFL.equals(sSubSchWorkKind)|| // COIL 제품출하상차
			    	   YmCommonConst.NEW_SCH_WORK_KIND_GTF1.equals(sSubSchWorkKind)|| // Coil 제품출하상차
			    	   YmCommonConst.NEW_SCH_WORK_KIND_GTF2.equals(sSubSchWorkKind)||    		    
			    	   YmCommonConst.NEW_SCH_WORK_KIND_GPFL.equals(sSubSchWorkKind)|| // COIL 제품출하상차
			    	   YmCommonConst.NEW_SCH_WORK_KIND_GPF1.equals(sSubSchWorkKind)|| // Coil 제품출하상차
			    	   YmCommonConst.NEW_SCH_WORK_KIND_GPF2.equals(sSubSchWorkKind)||
						YmCommonConst.NEW_SCH_WORK_KIND_CVML.equals(sSubSchWorkKind)
						|| // COIL 소재이송상차
						YmCommonConst.NEW_SCH_WORK_KIND_CVM2.equals(sSubSchWorkKind)
						|| // COIL 소재이송상차
						YmCommonConst.NEW_SCH_WORK_KIND_CVM3.equals(sSubSchWorkKind)
						|| // COIL 소재이송상차
						YmCommonConst.NEW_SCH_WORK_KIND_CVMU.equals(sSubSchWorkKind)
						|| // COIL 소재이송하차
						YmCommonConst.NEW_SCH_WORK_KIND_CVM4.equals(sSubSchWorkKind)
						|| // COIL 소재이송하차
						YmCommonConst.NEW_SCH_WORK_KIND_CVM5.equals(sSubSchWorkKind)
						|| // COIL 소재이송하차
						YmCommonConst.NEW_SCH_WORK_KIND_GVML.equals(sSubSchWorkKind)
						|| // COIL 제품이송상차
						YmCommonConst.NEW_SCH_WORK_KIND_GVM2.equals(sSubSchWorkKind)
						|| // COIL 제품이송상차
						YmCommonConst.NEW_SCH_WORK_KIND_GVM3.equals(sSubSchWorkKind)
						|| // COIL 제품이송상차
						YmCommonConst.NEW_SCH_WORK_KIND_GVMU.equals(sSubSchWorkKind)
						|| // COIL 제품이송하차
						YmCommonConst.NEW_SCH_WORK_KIND_GVM4.equals(sSubSchWorkKind)
						|| // COIL 제품이송하차
						YmCommonConst.NEW_SCH_WORK_KIND_GVM5.equals(sSubSchWorkKind)) { // COIL 제품이송하차

					logger.println(LogLevel.DEBUG, this, "스케쥴수행예정('S') = 출하스케쥴 => 보조작업 대상");

					isTrue = true;

				} else {

					logger.println(LogLevel.DEBUG, this, "스케쥴수행예정('S') = 보조작업 대상");

					isTrue = true;

				}

			}
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}

		return isTrue;
	}

	/**
	 * 
	 * tb_ym_stock Table wbook_id : 주작업대상재료 wbook_id stockBacLocV.sch_work_kind
	 * :주작업대상재료 sch_work_kind stockBacLocV.sch_work_loc_decision_method :주작업대상재료
	 * sch_work_loc_decision_method stockBacLocV.crane_word_put_loc :주작업대상재료
	 * crane_word_put_loc
	 * 
	 * @param JDTORecord :
	 *            주작업대상재료
	 * @param JDTORecord :
	 *            보조작업대상재료
	 * 
	 * @return JDTORecord
	 */
	private JDTORecord setSubStockWorkId(JDTORecord stockMainV,	JDTORecord stockSubV) {
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			String sWbookId = StringHelper.evl(stockMainV.getFieldString("WBOOK_ID"), "");
			String sSchWorkKind = StringHelper.evl(stockMainV.getFieldString("SCH_WORK_KIND"), "");
			String sSchWorkLocDecisionMethod = StringHelper.evl(stockMainV.getFieldString("SCH_WORK_LOC_DECISION_METHOD"), "");
			String sCraneWordPutLoc = StringHelper.evl(stockMainV.getFieldString("CRANE_WORD_PUT_LOC"), "");

			logger.println(LogLevel.DEBUG, this, "sWbookId  					=" + sWbookId);
			logger.println(LogLevel.DEBUG, this, "sSchWorkKind  				=" + sSchWorkKind);
			logger
					.println(LogLevel.DEBUG, this,"sSchWorkLocDecisionMethod  	=" + sSchWorkLocDecisionMethod);
			logger.println(LogLevel.DEBUG, this, "sCraneWordPutLoc  			=" + sCraneWordPutLoc);

			/*
			 * 보조작업 대상재료에 주작업대상재료 항목 셋팅
			 */
			stockSubV.setField("WBOOK_ID", sWbookId);
			stockSubV.setField("SCH_WORK_KIND", sSchWorkKind);
			stockSubV.setField("SCH_WORK_LOC_DECISION_METHOD",sSchWorkLocDecisionMethod);
			stockSubV.setField("CRANE_WORD_PUT_LOC", sCraneWordPutLoc);

		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}

		return stockSubV;
	}

	/**
	 * 작업대상 재료에 대한 CRANE 정보를 할당한다.
	 * 
	 * @param DAO
	 * @param JDTORecord
	 * 
	 * @return JDTORecord
	 * @throws Exception
	 */
	private JDTORecord getCoilCraneInfo(JDTORecord stockV) throws Exception {
		JDTORecord craneV = null;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			String sYdGp = "";
			String sBayGp = "";
			String sSchWorkKind = "";

			logger.println(LogLevel.DEBUG, this, "YD_GP  			=" + stockV.getFieldString("YD_GP"));
			logger.println(LogLevel.DEBUG, this, "BAY_GP  			=" + stockV.getFieldString("BAY_GP"));
			logger.println(LogLevel.DEBUG, this, "SCH_WORK_KIND 	=" + stockV.getFieldString("SCH_WORK_KIND"));

			sYdGp 		= StringHelper.evl(stockV.getFieldString("YD_GP"), "");
			sBayGp 		= StringHelper.evl(stockV.getFieldString("BAY_GP"), "");
			sSchWorkKind = StringHelper.evl(stockV.getFieldString("SCH_WORK_KIND"), "");
            
		
			craneV = dao.getCoilCraneInfo(sYdGp, sBayGp, sSchWorkKind);
			
			if (craneV == null) {

				setToLocFailLog(StringHelper.evl(stockV.getFieldString("STOCK_ID"), "")
								, StringHelper.evl(stockV.getFieldString("GBN"), "")
								, StringHelper.evl(stockV.getFieldString("SCH_WORK_KIND"), "")
								, YmCommonConst.MSG_TO_05);

			} else {

				String sCraneNo = StringHelper.evl(craneV.getFieldString("SELECT_CRANE_NO"), "");

				JDTORecord jtR = dao.getEquipInfoWithEquipNo(sYdGp, sCraneNo);

				String sWorkMode = StringHelper.evl(jtR.getFieldString("WORK_MODE"), "");
				String sEquipStat = StringHelper.evl(jtR.getFieldString("EQUIP_STAT"), "");

				if (YmCommonConst.WORK_MODE_C.equals(sWorkMode)
					|| YmCommonConst.WORK_MODE_C.equals(sEquipStat)) {

					setToLocFailLog(StringHelper.evl(stockV.getFieldString("STOCK_ID"), "")
									, StringHelper.evl(stockV.getFieldString("GBN"), "")
									, StringHelper.evl(stockV.getFieldString("SCH_WORK_KIND"), "")
									, YmCommonConst.MSG_TO_06);

					craneV = null;
				}
			}
			
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}

		return craneV;
	}

	/**
	 * 오퍼레이션명 : 
	 *
	 * TO 위치 번지정보를 가져온다.
	 * 
	 * param JDTORecord :
	 *            Stock Info
	 * param String :
	 *            스케쥴(F) / 리스케쥴(R) 구분
	 *                                                                                                                                                                                                                                                                     
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                 
	public JDTORecord getCoilToLocInfo_001(JDTORecord stockV, String sSchGbn) {
		JDTORecord stockToLocV = null;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			List stackColL = null;

			String sGbn 				= StringHelper.evl(stockV.getFieldString("GBN"), "");
			String sYdGp 				= StringHelper.evl(stockV.getFieldString("YD_GP"), "");
			String sBayGp 			= StringHelper.evl(stockV.getFieldString("BAY_GP"),"");
			String sSectGp 			= StringHelper.evl(stockV.getFieldString("SECT_GP"),	"");
			String sColGp 				= StringHelper.evl(stockV.getFieldString("COL_GP"),"");
			String sStackColUsageCd 	= StringHelper.evl(stockV.getFieldString("STACK_COL_USAGE_CD"), "");
			String sSchWorkKind 		= StringHelper.evl(stockV.getFieldString("SCH_WORK_KIND"), "");
			String sStockMoveTerm 		= StringHelper.evl(stockV.getFieldString("STOCK_MOVE_TERM"), "");
			String sSchWorkLocDecisionMethod = StringHelper.evl(stockV.getFieldString("SCH_WORK_LOC_DECISION_METHOD"), "");
			String sCraneWordPutLoc 	= StringHelper.evl(stockV.getFieldString("CRANE_WORD_PUT_LOC"), "");
			String sStockId 			= StringHelper.evl(stockV.getFieldString("STOCK_ID"), "");

			logger.println(LogLevel.DEBUG, this, "스케쥴링구분		=" + sSchGbn);
			logger.println(LogLevel.DEBUG, this, "주,보조작업구분	=" + sGbn);
			logger.println(LogLevel.DEBUG, this, "야드구분			=" + sYdGp);
			logger.println(LogLevel.DEBUG, this, "동구분	  		=" + sBayGp);
			logger.println(LogLevel.DEBUG, this, "스판구분  			=" + sSectGp);
			logger.println(LogLevel.DEBUG, this, "열구분	  		=" + sColGp);
			logger.println(LogLevel.DEBUG, this, "적치열용도코드 	=" + sStackColUsageCd);
			logger.println(LogLevel.DEBUG, this, "스케쥴코드  		=" + sSchWorkKind);
			logger.println(LogLevel.DEBUG, this, "저장품이동조건		=" + sStockMoveTerm);
			logger.println(LogLevel.DEBUG, this, "스케쥴결정방법		=" + sSchWorkLocDecisionMethod);
			logger.println(LogLevel.DEBUG, this, "스케쥴지정PUT위치	=" + sCraneWordPutLoc);
			logger.println(LogLevel.DEBUG, this, "저장품ID			=" + sStockId);

			String sOrderBy 	= "0";
			String sGroupBy 	= "1";
			
			
			String sCoilW = "0";
			String sCoilO = "0";
			String sJJANGGU_CHK = "N";
			String sYdZoneGp	="";

			JDTORecord coilV = dao.getCoilCommonInfo(sStockId);
			if (coilV != null) {
				sCoilW 		= StringHelper.evl(coilV.getFieldString("코일폭"), "0");
				sCoilO 		= StringHelper.evl(coilV.getFieldString("코일외경"), "0");
				sJJANGGU_CHK= StringHelper.evl(coilV.getFieldString("JJANGGU_CHK"), "N");				
				
				//CTS 하차,HFL추출  인 경우에만 존정보 셋팅
				if("CCMU".equals(sSchWorkKind)|| "CCMR".equals(sSchWorkKind)|| "CFLO".equals(sSchWorkKind)){
					
					sYdZoneGp 	= StringHelper.evl(coilV.getFieldString("YD_ZONE_GP"), "");	
					
					logger.println(LogLevel.DEBUG, this, "["+sSchWorkKind+"]ZONE 정보로 조회=>>" + sYdZoneGp);
					
					//HFL 추출 시 후물재가 아닌경우 
					if("CFLO".equals(sSchWorkKind) && !"HZ".equals(sYdZoneGp)){
						sYdZoneGp ="";
					}
				}
			}
			
			logger.println(LogLevel.DEBUG, this, "["+sSchWorkKind+"]CTS 하차 /HFL추출 인 경우에 만 ZONE 정보로 조회=>>" + sYdZoneGp);
			
			
			
			//=============================================================================================================
			// 2009-03-31 CGS
			// Scrap에 대한 처리.
			// 특별한 조건 검사없이 지정된 위치로 추출한다.
//*
			if ( sStockId.substring(0,1).equals("S") && sYdGp.equals("3")  ) {
				logger.println(LogLevel.DEBUG, this, "===============================" );
				logger.println(LogLevel.DEBUG, this, "B열연 Scrap에 대한 스케줄 To위치 정보 처리");
				logger.println(LogLevel.DEBUG, this, "저장품ID:[" + sStockId +"], Yard["+sYdGp+"](3:B열연)");
				// Scrap은 임의로 지정된 적치열로 추출한다.
				// Parameters:
				//	String : 저장품ID
				//	String : 야드구분
				//	String : 동구분
				//	String : 적치열용도코드
				//	String : SCH CODE
				//	String : 저장품이동조건
				logger.println(LogLevel.DEBUG, this,"작업예약에서 지정된 위치(스케쥴지정PUT위치)를 TO위치로 지정.=====");
				logger.println(LogLevel.DEBUG, this,"스케쥴지정PUT위치=====" + sCraneWordPutLoc);
				
				//stackColL = dao.getNewStackRuleInfo_002(sStockId,							
				//	     								sCraneWordPutLoc.substring(0, 6),   
				//									    sOrderBy,
				//									    sSchGbn, 							
				//									    sGroupBy);

				//stockToLocV = getNewCoilToLocInfo_002(stackColL, stockV);

				// 적치열의 데이터를 모두 초기화한다.
				// 권하시에도 변경 코드가 실행됨.
	    		logger.println(LogLevel.DEBUG, this, "Scrap 적치 단의 상태를 'E'로 변경");
	    		int iReq = dao.updateCraneStackLayerStat(sCraneWordPutLoc.substring(0,6),		// Col
	    												 sCraneWordPutLoc.substring(6,8),							// Bed
	    												 sCraneWordPutLoc.substring(8,10),
	    												 "",
	    												 YmCommonConst.STACK_LAYER_STAT_E);
	    	
	    		
				
				stockToLocV = getCoilColLocPutInfo(sCraneWordPutLoc);
				logger.println(LogLevel.DEBUG, this,"Scrap스케쥴 처리 결과 레코드=" + stockToLocV);
				return stockToLocV;
			}
//*/
			//=============================================================================================================
			/**
			 * 0. 적치 우선순위 기준정보를 가져온다. - 0 => 번지순 - 1 => 1단우선 - 2 => 2단 우선 1.
			 * 협폭,분할코일에 대한 2단 우선기준 적용 2. 해당코일의 군정보를 체크해서 관련COL정보만 검색
			 */
			{
				JDTORecord ruleV = dao.getStackRuleInfo_004(sYdGp, sBayGp, sSchWorkKind);
				if (ruleV != null) {
					sOrderBy = StringHelper.evl(ruleV.getFieldString("SCH_RULE_VAL"), "0");
				}

				String sRwith 	= "0"; // 폭 - 1
				String sRout 	= "0"; // 길이 - 2

				// ==> 협폭,분할코일 기준 적용
				JDTORecord rule1V = dao.getStackRuleInfo_005(sYdGp, "1");
				if (rule1V != null) {
					sRwith = StringHelper.evl(rule1V.getFieldString("SCH_CD"),"0");
				}

				JDTORecord rule2V = dao.getStackRuleInfo_005(sYdGp, "2");
				if (rule2V != null) {
					sRout = StringHelper.evl(rule2V.getFieldString("SCH_CD"),"0");
				}

				

				double dRwith 	= 0;
				double dRout 	= 0;
				double dCoilW 	= 0;
				double dCoilO 	= 0;

				logger.println(LogLevel.DEBUG, this, "폭 기준  	=>=" + sRwith	+ "=");
				logger.println(LogLevel.DEBUG, this, "폭 공통  	=>=" + sCoilW	+ "=");
				logger.println(LogLevel.DEBUG, this, "외경 기준	=>=" + sRout 	+ "=");
				logger.println(LogLevel.DEBUG, this, "외경 공통	=>=" + sCoilO 	+ "=");

				dRwith 	= Double.parseDouble(sRwith);
				dRout 	= Double.parseDouble(sRout);
				dCoilW 	= Double.parseDouble(sCoilW);
				dCoilO 	= Double.parseDouble(sCoilO);

				if (dRwith > dCoilW) {
					/**
					 * 현코일의 폭이 기준폭보다 작을 경우 2단우선순위 적용
					 */
					sOrderBy = "2";
					logger.println(LogLevel.DEBUG, this, "폭 기준적용 2단우선=>기준="	+ dRwith + "/" + sStockId + "=" + dCoilW);
				}

				if (dRout > dCoilO) {
					/**
					 * 현코일의 외경이 기준외경보다 작을 경우 2단우선순위 적용
					 */
					sOrderBy = "2";
					logger.println(LogLevel.DEBUG, this, "외경 기준적용 2단우선=>기준="+ dRout + "/" + sStockId + "=" + dCoilO);
				}
				
				/*
				 * COIL 야드의 군관련 정보를 가져온다. 군정보 비교는 TB_YM_STACKCOL TABLE에
				 * STACK_COL_BED_DIRECTION 항목을 참고한다.
				 * 
				 * 일단 B열연 COIL야드만 적용한다.
				 */
				if (YmCommonConst.YD_GP_3.equals(sYdGp)) {
					
					String sGroupMi1 	= "";
					String sGroupMx1 	= "";
					String sGroupMi2 	= "";
					String sGroupMx2 	= "";
					String sGroupMi3 	= "";
					String sGroupMx3	= "";
					String sGroupMi4 	= "";
					String sGroupMx4 	= "";
					String sGroupMi5 	= "";
					String sGroupMx5 	= "";

					JDTORecord grpV = null;

					List grpL = dao.getStackRuleInfo_006(sYdGp);

					for (int inx = 0; inx < grpL.size(); inx++) {
						grpV = (JDTORecord) grpL.get(inx);

						if (YmCommonConst.COIL_GROUP_1.equals(StringHelper.evl(grpV.getFieldString("STACK_RULE_CD"), ""))) {
							sGroupMi1 	= StringHelper.evl(grpV	.getFieldString("STACK_RULE_MIN"), "0");
							sGroupMx1 	= StringHelper.evl(grpV.getFieldString("STACK_RULE_MAX"), "0");
						} else if (YmCommonConst.COIL_GROUP_2.equals(StringHelper.evl(grpV.getFieldString("STACK_RULE_CD"), ""))) {
							sGroupMi2 	= StringHelper.evl(grpV	.getFieldString("STACK_RULE_MIN"), "0");
							sGroupMx2 	= StringHelper.evl(grpV.getFieldString("STACK_RULE_MAX"), "0");
						} else if (YmCommonConst.COIL_GROUP_3.equals(StringHelper.evl(grpV.getFieldString("STACK_RULE_CD"), ""))) {
							sGroupMi3 	= StringHelper.evl(grpV	.getFieldString("STACK_RULE_MIN"), "0");
							sGroupMx3 	= StringHelper.evl(grpV.getFieldString("STACK_RULE_MAX"), "0");
						} else if (YmCommonConst.COIL_GROUP_4.equals(StringHelper.evl(grpV.getFieldString("STACK_RULE_CD"), ""))) {
							sGroupMi4 	= StringHelper.evl(grpV.getFieldString("STACK_RULE_MIN"), "0");
							sGroupMx4 	= StringHelper.evl(grpV.getFieldString("STACK_RULE_MAX"), "0");
						} else if (YmCommonConst.COIL_GROUP_5.equals(StringHelper.evl(grpV.getFieldString("STACK_RULE_CD"), ""))) {
							sGroupMi5 	= StringHelper.evl(grpV.getFieldString("STACK_RULE_MIN"), "0");
							sGroupMx5 	= StringHelper.evl(grpV.getFieldString("STACK_RULE_MAX"), "0");
						}
					}

					double dCoil1Mi 	= 0;
					double dCoil1MX 	= 0;
					double dCoil2Mi 	= 0;
					double dCoil2MX 	= 0;
					double dCoil3Mi 	= 0;
					double dCoil3MX 	= 0;
					double dCoil4Mi 	= 0;
					double dCoil4MX 	= 0;
					double dCoil5Mi 	= 0;
					double dCoil5MX 	= 0;

					dCoil1Mi 		= Double.parseDouble(sGroupMi1);
					dCoil1MX 	= Double.parseDouble(sGroupMx1);
					dCoil2Mi 		= Double.parseDouble(sGroupMi2);
					dCoil2MX 	= Double.parseDouble(sGroupMx2);
					dCoil3Mi 		= Double.parseDouble(sGroupMi3);
					dCoil3MX 	= Double.parseDouble(sGroupMx3);
					dCoil4Mi 		= Double.parseDouble(sGroupMi4);
					dCoil4MX 	= Double.parseDouble(sGroupMx4);
					dCoil5Mi 		= Double.parseDouble(sGroupMi5);
					dCoil5MX 	= Double.parseDouble(sGroupMx5);

					if (dCoilO >= dCoil1Mi && dCoilO <= dCoil1MX) {
						/**
						 * 현코일의 외경이 1군기준에 적용될 경우
						 */
						sGroupBy = YmCommonConst.COIL_GROUP_1;
						logger.println(LogLevel.DEBUG, this, "코일 1군관리적용 =>"+ sStockId + "=" + dCoilO + "/" + dCoil1Mi+ "/" + dCoil1MX);

					} else if (dCoilO >= dCoil2Mi && dCoilO <= dCoil2MX) {
						/**
						 * 현코일의 외경이 2군기준에 적용될 경우
						 */
						sGroupBy = YmCommonConst.COIL_GROUP_2;
						logger.println(LogLevel.DEBUG, this, "코일 2군관리적용 =>"+ sStockId + "=" + dCoilO + "/" + dCoil2Mi+ "/" + dCoil2MX);

					} else if (dCoilO >= dCoil3Mi && dCoilO <= dCoil3MX) {
						/**
						 * 현코일의 외경이 3군기준에 적용될 경우
						 */
						sGroupBy = YmCommonConst.COIL_GROUP_3;
						logger.println(LogLevel.DEBUG, this, "코일 3군관리적용 =>"+ sStockId + "=" + dCoilO + "/" + dCoil3Mi+ "/" + dCoil3MX);

					} else if (dCoilO >= dCoil4Mi && dCoilO <= dCoil4MX) {
						/**
						 * 현코일의 외경이 4군기준에 적용될 경우
						 */
						sGroupBy = YmCommonConst.COIL_GROUP_4;
						logger.println(LogLevel.DEBUG, this, "코일 4군관리적용 =>"+ sStockId + "=" + dCoilO + "/" + dCoil4Mi+ "/" + dCoil4MX);

					} else if (dCoilO >= dCoil5Mi && dCoilO <= dCoil5MX) {
						/**
						 * 현코일의 외경이 5군기준에 적용될 경우
						 */
						sGroupBy = YmCommonConst.COIL_GROUP_5;
						logger.println(LogLevel.DEBUG, this, "코일 5군관리적용 =>"+ sStockId + "=" + dCoilO + "/" + dCoil5Mi+ "/" + dCoil5MX);
					}
				}
			}

			//주작업 & 설비 보급 ,TAKE IN 
			if (YmCommonConst.MAIN_WORK_M.equals(sGbn) && 
			    YmCommonUtil.isLineInWork(sSchWorkKind)) {

				/*
				 * 1. LINE IN 작업인지 체크 LINE IN 작업일 경우 해당 CONVEYOR 정보 생성 생성된
				 * CONVEYOR 가 TO 위치 대상은 주작업인 경우만 해당한다.
				 */
				logger.println(LogLevel.DEBUG, this, "▶▶▶▶▶▶▶▶▶▶LINE IN  TO위치 검색◀◀◀◀◀◀◀◀◀◀");

// 2009-06-22 기존 함수에 sBayGp 인자를 추가함. 함수 자체 변경 및 수정.
				stockToLocV = getConveyorToLocInfo(sSchWorkKind, sYdGp, sBayGp);
				logger.println(LogLevel.DEBUG, this, "LINE IN TO위치 검색 결과="+stockToLocV);
//=======
//				stockToLocV = getConveyorToLocInfo(sSchWorkKind, sYdGp);


			} else {

				if (YmCommonConst.MAIN_WORK_M.equals(sGbn) && 
				    YmCommonConst.SCH_WORK_LOC_DECISION_METHOD_O.equals(sSchWorkLocDecisionMethod) && 
				    sCraneWordPutLoc.length() == 10) {

					/*
					 * 2. 스케쥴 결정방법 : 사용자 지정('O') 스케쥴지정PUT위치 자리수 10자리 대상은 주작업인
					 * 경우만 해당한다.
					 */
					logger.println(LogLevel.DEBUG, this,"▶▶▶▶▶▶▶▶▶▶사용자 지정(10) TO위치 검색◀◀◀◀◀◀◀◀◀◀");
					stockToLocV = getCoilColLocPutInfo(sCraneWordPutLoc);
					
					logger.println(LogLevel.DEBUG, this,"Record확인:" + stockToLocV);
					/*
					 * 2.1 스케쥴지정 10자리 TO위치가 적치중일때 해당 적치열에 적치가능위치를 검색한다.
					 */
					if (stockToLocV == null) {
						logger.println(LogLevel.DEBUG, this,"getNewStackRuleInfo_002()호출인자 검사:" + sCraneWordPutLoc.substring(0, 6));
						stackColL = dao.getNewStackRuleInfo_002(sStockId,
														     sCraneWordPutLoc.substring(0, 6), 
														     sOrderBy,
														     sSchGbn, 
														     sGroupBy);

						stockToLocV = getNewCoilToLocInfo_002(stackColL, stockV);
					}
				} else {

					if (YmCommonConst.MAIN_WORK_M.equals(sGbn)) { // 주작업

						if (YmCommonConst.SCH_WORK_LOC_DECISION_METHOD_O.equals(sSchWorkLocDecisionMethod) &&
						   (sCraneWordPutLoc.length() == 6 || sCraneWordPutLoc.length() == 4)) {
							/*
							 * 3. 스케쥴 결정방법 : 사용자 지정('O') 스케쥴지정PUT위치 자리수 6자리(적치열)
							 */
							logger.println(LogLevel.DEBUG, this,"▶▶▶▶▶▶▶▶▶▶사용자 지정(6) TO위치 검색◀◀◀◀◀◀◀◀◀◀");

							logger.println(LogLevel.DEBUG, this,"정렬순서: "+sOrderBy);
							stackColL = dao.getNewStackRuleInfo_002(sStockId,
															     sCraneWordPutLoc, 
															     sOrderBy, 
															     sSchGbn,
															     sGroupBy);

							stockToLocV = getNewCoilToLocInfo_002(stackColL,stockV);

						} else {
							/*
							 * 5. 스케쥴 결정방법 : SYSTEM 지정('S') 동간이적 상차스케쥴코드인 경우는
							 * 이동조건을 체크하지 않고 TO위치를 찾는다. 즉, 저장품이동조건 체크가 필요없는 경우
							 * 예) CTML
							 */
							// CGS 추가 
							// 신규대차 #1,#2,#3에 대한 스케줄 코드 추가  등록함. CTM5, CTM6, CTM7
							if (YmCommonConst.NEW_SCH_WORK_KIND_CTML.equals(sSchWorkKind) 
							    || YmCommonConst.NEW_SCH_WORK_KIND_CTM2.equals(sSchWorkKind) 
							    || YmCommonConst.NEW_SCH_WORK_KIND_CTM3.equals(sSchWorkKind)
							    || YmCommonConst.NEW_SCH_WORK_KIND_CTM5.equals(sSchWorkKind)
							    || YmCommonConst.NEW_SCH_WORK_KIND_CTM6.equals(sSchWorkKind)
							    || YmCommonConst.NEW_SCH_WORK_KIND_CTM7.equals(sSchWorkKind)
							  //  || (YmCommonConst.NEW_SCH_WORK_KIND_CKLO.equals(sSchWorkKind) && "A".equals(sBayGp)	)			// SPM1 추출
							//	|| (YmCommonConst.NEW_SCH_WORK_KIND_CFLO.equals(sSchWorkKind)  && "C".equals(sBayGp))			// HFL 추출
							//	|| (YmCommonConst.NEW_SCH_WORK_KIND_CNLO.equals(sSchWorkKind) && "E".equals(sBayGp)	)			// SPM2 추출
							    ) {

								logger.println(LogLevel.DEBUG, this,"▶▶▶▶▶▶▶▶▶▶SYSTEM TO위치 검색◀◀◀◀◀◀◀◀◀◀");
								logger.println(LogLevel.DEBUG, this,"동간이적 상차시 =====");

								JDTORecord tempJ = dao.getCoilColLocMainList(	sYdGp, 
																		sBayGp, 
																		sStackColUsageCd,
																		sSchWorkKind,
																		// "%");
																		sStockMoveTerm);
								// 저장영역 등록정보 체크
								if ("0".equals(StringHelper.evl(tempJ.getFieldString("COUNT"), "0"))) {
									/**
									 * TO 위치 FAIL ERROR MESSAGE 처리 01
									 */
									setToLocFailLog(StringHelper.evl(stockV.getFieldString("STOCK_ID"),""),
												    StringHelper.evl(stockV.getFieldString("GBN"), ""),
												    StringHelper.evl(stockV.getFieldString("SCH_WORK_KIND"),""),
												    YmCommonConst.MSG_TO_01);
									logger.println(LogLevel.DEBUG, this,"저장영역별검색순서조회 화면에서 저장영역을 등록하세요");
									
								} else {
									logger.println(LogLevel.DEBUG, this,"저장영역등록정보체크 Count"+StringHelper.evl(tempJ.getFieldString("COUNT"), "0"));
									logger.println(LogLevel.DEBUG, this,"저장영역등록정보체크 정렬순서: "+sOrderBy);
									stackColL = dao.getNewStackRuleInfo_001(  sStockId, 				// 저장품ID
																		sYdGp, 				// 야드구분
																		sBayGp, 				// 동구분
																		sStackColUsageCd,	// 적치열용도코드
																		sSchWorkKind, 		// SCH CODE
																		// "%", 				//저장품이동조건
																		sStockMoveTerm, 		// 저장품이동조건
																		sOrderBy, 			// 정렬순서
																		sSchGbn, 			// 스케쥴구분																		 
																		sGroupBy,			// 군관리
																		sYdZoneGp); 		// ZONE구분									
		 
									logger.println(LogLevel.DEBUG, this,"저장영역등록정보체크 Record: "+stackColL);
									stockToLocV = getNewCoilToLocInfo_002(stackColL, stockV);
								}
							} else {
								/*
								 * 6. 스케쥴 결정방법 : SYSTEM 지정('S') 스케쥴코드에 따라
								 * 저장품이동조건항목을 제외하고 검색열을 찾는다. 예) CTCL
								 */
								logger.println(LogLevel.DEBUG, this,"▶▶▶▶▶▶▶▶▶▶SYSTEM TO위치 검색◀◀◀◀◀◀◀◀◀◀");

//<<<<<<< CraneSchRegSBean.java
								logger.println(LogLevel.DEBUG, this,"저장품이동조건항목을 제외하고 검색열을 찾는다. 예) CTCL");
								//SELECT  COUNT(*) AS COUNT
								//FROM tb_ym_stockmoveroute a,
		                        //     tb_ym_locsearch      b
		                        //WHERE  a.yd_gp                 = :yd_gp                -- 야드구분(1)
		                        //AND    a.bay_gp                = :bay_gp               -- 동구분(E)
		                        //AND    a.stack_col_usage_cd    = :stack_col_usage_cd   -- 적치열용도코드(CS)
		                        //AND    a.sch_work_kind         = :sch_work_kind        -- SCH CODE(CCTO)
								//AND    a.stock_move_term like  :stock_move_term
								//AND    a.stock_move_route_stat = 'A'
								//AND    a.stock_move_route_id   = b.stock_move_route_id
//=======
								// 
								JDTORecord tempJ = dao.getCoilColLocMainList(	sYdGp, 
																		sBayGp, 
																		sStackColUsageCd,
																		sSchWorkKind, 
																		sStockMoveTerm);
								logger.println(LogLevel.DEBUG, this,"구한 Record: "+ tempJ);
								
								// 저장영역 등록정보 체크
								if ("0".equals(StringHelper.evl(tempJ.getFieldString("COUNT"), "0"))) {
									/**
									 * TO 위치 FAIL ERROR MESSAGE 처리 01
									 */
									setToLocFailLog(StringHelper.evl(stockV.getFieldString("STOCK_ID"),""),
												StringHelper.evl(stockV.getFieldString("GBN"), ""),
												StringHelper.evl(stockV.getFieldString("SCH_WORK_KIND"),""),
												YmCommonConst.MSG_TO_01);
								} else {
									logger.println(LogLevel.DEBUG, this,"저장영역 등록정보 체크 Count"+StringHelper.evl(tempJ.getFieldString("COUNT"), "0"));
									logger.println(LogLevel.DEBUG, this,"저장품ID(sStockId): "+sStockId);
									logger.println(LogLevel.DEBUG, this,"야드구분(sYdGp):   "+sYdGp);
									logger.println(LogLevel.DEBUG, this,"동구분(sBayGp):   "+sBayGp);
									logger.println(LogLevel.DEBUG, this,"적치열용도코드():  "+sStackColUsageCd);
									logger.println(LogLevel.DEBUG, this,"SCHCODE():      "+sSchWorkKind);
									logger.println(LogLevel.DEBUG, this,"저장품이동조건:   "+sStockMoveTerm);
									logger.println(LogLevel.DEBUG, this,"정렬순서(sOrderBy): "+sOrderBy);
									logger.println(LogLevel.DEBUG, this,"스케쥴구분(sSchGbn): "+sSchGbn);
									logger.println(LogLevel.DEBUG, this,"군관리(sGroupBy):   "+sGroupBy);
									logger.println(LogLevel.DEBUG, this,"짱구코일대상(sJJANGGU_CHK):   "+sJJANGGU_CHK);
									logger.println(LogLevel.DEBUG, this,"ZONE구분(sYdZoneGp):   "+sYdZoneGp);
									
									//=============================================================================================================================
									// 최규성
									// 직상차 가능하다면 정렬순서를 0로 설정하고
									String sOldOrderBy = sOrderBy;		// 변경전 데이터를 가지고 있는다.
									if (sYdGp.equals(YmCommonConst.YD_GP_3)){
										// SCH_Code가 추출일 경우
										if (YmCommonConst.NEW_SCH_WORK_KIND_CKLO.equals(sSchWorkKind) 				// SPM1 추출
											    || YmCommonConst.NEW_SCH_WORK_KIND_CFLO.equals(sSchWorkKind) 		// HFL 추출
											    || YmCommonConst.NEW_SCH_WORK_KIND_CNLO.equals(sSchWorkKind) )		// SPM2 추출
										{
											
											//if(기준Auto == Y){// 직상차 가능토록
												logger.println(LogLevel.DEBUG, this,"[직상차]B열연 정렬순서를 변경한다. 현재: "+sOrderBy);
												JDTORecord jtrTcStat = getTcStatusInfo(sBayGp); // 직상차가능한 대차코드 가져온다.
												int nJtrSize = jtrTcStat.size();

												if(nJtrSize > 0){
													sOrderBy = "0";
													logger.println(LogLevel.DEBUG, this,"[직상차]B열연 정렬순서를 변경한다. 변경: "+sOrderBy);
												}
											//}
										}
										
										//짱구코일 2단 적치 부터 시작
										if("Y".equals(sJJANGGU_CHK)){
											sOrderBy = "9";
											logger.println(LogLevel.DEBUG, this,"[짱구]B열연 정렬순서를 2단부터 작업한다. 변경: "+sOrderBy);
										}
									}
									
									//=============================================================================================================================
									
									
									//=============================================================================================================================
									//SPM 재작업 추출 권하위치 생성 
									if (sYdGp.equals(YmCommonConst.YD_GP_1)){
										// SCH_Code가 추출일 경우
										if (YmCommonConst.NEW_SCH_WORK_KIND_CKLR.equals(sSchWorkKind)||YmCommonConst.NEW_SCH_WORK_KIND_EQLR.equals(sSchWorkKind) ){ 				// Coil SPM/EQL 재작업 추출
										
											ymCommonDAO dao = ymCommonDAO.getInstance();
											
											String sQueryId = "ym.common.YmCommonDB.getMaxStackBedInfo2";
											String sStackColGp ="1EKE02";
											
											if(YmCommonConst.NEW_SCH_WORK_KIND_EQLR.equals(sSchWorkKind)){
												sStackColGp ="1FQE02";
											}
											
											JDTORecord curBedV	 = dao.getCommonInfo(sQueryId,new Object[]{sStackColGp});
											
											if(curBedV == null){												
											
												//적치열 MAX번지 정보를 가져온다.
												/*
												select NVL(MAX(stack_bed_gp), '00') as MAXBED
												from tb_ym_stacker
												where STACK_COL_GP = :stack_col_gp
												*/
												
												JDTORecord colXyV  				= null;
												int iSeq 						= -1;
												sQueryId = "ym.common.YmCommonDB.getMaxStackBedInfo";
										 
												JDTORecord maxBedV	 = dao.getCommonInfo(sQueryId,new Object[]{sStackColGp});
												
											    //적치열 MAX+1번지 정보를 생성한다.
												String sMaxStackBedGp = StringHelper.evl(maxBedV.getFieldString("MAXBED"),"");
											    sMaxStackBedGp = YmCommonUtil.changeLayerFormat(sMaxStackBedGp,"P");
											    
											    logger.println(LogLevel.DEBUG, "SPM/EQL 재작업 추출위치 최대BED: "+sMaxStackBedGp);
											    if(sMaxStackBedGp.length() > 2){
											    	return stockToLocV;
											    }
											    
											  //***************************************************************************************
											    //적치대정보가 존재 안 하는 경우 생성해준다.
											 	sQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackerDAO.selectWBSTACKER";
											 	List chkList = dao.getCommonList(sQueryId,new Object[]{sStackColGp,sMaxStackBedGp});
											 	
											 	if(chkList.size()<=0 ){
											 		//TB_YM_STACKER TABLE를 생성한다.
											 		sQueryId = "ym.common.YmCommonDB.createStakerInfo";						 
											 		iSeq	 = dao.insertData(sQueryId,new Object[]{sStackColGp,
																								sMaxStackBedGp,
																								"SYSTEM"});
											 	}
											 	//***************************************************************************************
											 	
											 	
											 	//***************************************************************************************
											 	sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerInfoWithPk";
											 	List chkList2 = dao.getCommonList(sQueryId,new Object[]{sStackColGp,
											 															sMaxStackBedGp,
																	 									YmCommonConst.STACK_LAYER_GP_01});
											 	//dao.getCommonInfo(queryCode, objs);
											 	if(chkList2.size()<=0 ){
											 		
										 		sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackColInfoWithPk";
												colXyV	 = dao.getCommonInfo(sQueryId,new Object[]{sStackColGp});
													
												//TB_YM_STACKLAYER TABLE를 생성한다.
												sQueryId = "ym.common.YmCommonDB.createStakLayerInfo";
												iSeq	 = dao.insertData(sQueryId,new Object[]{sStackColGp,
																								sMaxStackBedGp,
																								YmCommonConst.STACK_LAYER_GP_01,
																								StringHelper.evl(colXyV.getFieldString("STACK_COL_RULE_X_AXIS"),"9999"),
																								StringHelper.evl(colXyV.getFieldString("STACK_COL_RULE_Y_AXIS"),"9999"),
																								"SYSTEM"});
											 	}
										 	
											}
										    
										}
										
									}
									//=============================================================================================================================
									
									
									
									stackColL = dao.getNewStackRuleInfo_001(  sStockId, 			// 저장품ID
																				sYdGp, 				// 야드구분
																				sBayGp, 			// 동구분
																				sStackColUsageCd, 	// 적치열용도코드
																				sSchWorkKind, 		// SCH CODE
																				sStockMoveTerm, 	// 저장품이동조건
																				sOrderBy, 			// 정렬순서
																				sSchGbn, 			// 스케쥴구분
																				sGroupBy,			// 군관리
																				sYdZoneGp); 		// ZONE구분			
									//logger.println(LogLevel.DEBUG, this,"결과List: "+stackColL);
									logger.println(LogLevel.DEBUG, this,"전달값: "+stockV);
									
									stockToLocV = getNewCoilToLocInfo_002(stackColL, stockV);
									
									logger.println(LogLevel.DEBUG, this,"결과값: "+stockToLocV);
								}
							}
						}

					} else if (YmCommonConst.SUB_WORK_S.equals(sGbn)) { // 보조작업
						/*
						 * 6. 보조작업인 경우는 어떠한 상황에서도 해당 SPAN에 To위치를 찾는다.
						 */
						logger.println(LogLevel.DEBUG, this,"▶▶▶▶▶▶▶▶▶▶보조작업 TO위치 검색◀◀◀◀◀◀◀◀◀◀");

						stackColL = dao.getNewStackRuleInfo_003(sStockId, 		// 저장품ID
															sYdGp, 		// 야드구분
															sBayGp, 		// 동구분
															sSectGp, 	// SPAN구분
															sColGp, 		// 열구분
															sSchGbn, 	// 스케쥴구분
															sGroupBy, 	// 군관리
															sOrderBy); 	// 정렬순서

						stockToLocV = getNewCoilToLocInfo_002(stackColL, stockV);
					}
				}
			}
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		logger.println(LogLevel.DEBUG, this,"getCoilToLocInfo_001() returnData:"+stockToLocV);
		return stockToLocV;
	}
	/*
	 * 최규성 
	 * 직상차 사용 여부를 판별하기 위해서 대차기준정보를 가져옵니다. 
	 * 조건 : 대차코드
	 */
	private List getListTransCarStandardInfo(String sTcCode1, String sTcCode2){
		List listStdInfo = new ArrayList();
		String sQueryId_std = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getEquipInfoWithEquipGp2";
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			// 대차 기준 정보를 가져온다.
			listStdInfo = dao.getListData(sQueryId_std, new Object[]{sTcCode1,sTcCode2});
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return listStdInfo;
	}
	/* 최규성
	 * 현재동의 대차 상태를 확인하여 대차 정보를 가져온다.
	 * 사용가능한 대차의 정보를 반환한다.
	 * 대차검사 조건
	 *     1. 작업예약, 스케줄에 해당 대차의 작업이 없다.
	 *     2. 대차의 상태 검사 stack_stat : L, I
	 */

	
	private JDTORecord getTcStatusInfo(String sBayGp){
		JDTORecord jtrTcStat = JDTORecordFactory.getInstance().create();
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			// 검사 대상
			// A동 HYSCO대차, 신규대차1
			// C동 HFL대차, 신규대차2
			// E동 신규대차3
			/*
				SELECT *
				  FROM TB_YM_EQUIP
				 WHERE EQUIP_GP LIKE '3XTC%'
				   AND SUBSTR(CURR_STOP_LOC,2,1) = :bay
				   AND STACK_STAT IN ('L', 'I')
				   AND WPROG_STAT = 'W'
			 */
			String sQueryId_tc = "ym.scheduling.crane.dao.CraneSchReg.getTcStatusInfo";
			String sQueryId_sch = "ym.scheduling.crane.dao.CraneSchReg.getTcSchCount";
			List listArgu_sch =  new ArrayList();
			List listArgu_tc =  new ArrayList();
			listArgu_sch.clear();
			listArgu_tc.clear();
			if(sBayGp.equals("A") ){
				// 대차코드 : 3XTC02, 3XTC03
				// 관련 스케줄 코드 : CTFL, CTM2, CTM4, CTM5, CTM8
				listArgu_tc.add("3XTC02");
				listArgu_tc.add("3XTC03");
				listArgu_sch.add("CTFL");
				listArgu_sch.add("CTM2");
				listArgu_sch.add("CTM4");
				listArgu_sch.add("CTM5");
				listArgu_sch.add("CTM8");
				listArgu_sch.add("CTFL");
				listArgu_sch.add("CTM2");
				listArgu_sch.add("CTM4");
				listArgu_sch.add("CTM5");
				listArgu_sch.add("CTM8");
			}else if(sBayGp.equals("C")){
				listArgu_tc.add("3XTC01");
				listArgu_tc.add("3XTC04");
				listArgu_sch.add("CTML");
				listArgu_sch.add("CTML");
				listArgu_sch.add("CTMU");
				listArgu_sch.add("CTM6");
				listArgu_sch.add("CTM9");
				listArgu_sch.add("CTML");
				listArgu_sch.add("CTML");
				listArgu_sch.add("CTMU");
				listArgu_sch.add("CTM6");
				listArgu_sch.add("CTM9");
			}else if(sBayGp.equals("E")){
				;
				listArgu_tc.add("3XTC05");
				listArgu_tc.add("3XTC05");
				listArgu_sch.add("CTM7");
				listArgu_sch.add("CTM7");
				listArgu_sch.add("CTMX");
				listArgu_sch.add("CTM7");
				listArgu_sch.add("CTMX");
				listArgu_sch.add("CTM7");
				listArgu_sch.add("CTM7");
				listArgu_sch.add("CTMX");
				listArgu_sch.add("CTM7");
				listArgu_sch.add("CTMX");
			}
			
			List listTcStat = dao.getListData(sQueryId_tc, listArgu_tc);
			JDTORecord jtrCount = new YdStockDAO().getData(sQueryId_sch, listArgu_sch);
			
			
			listArgu_sch.clear();
			listArgu_tc.clear();
			JDTORecord jtrCheck = null;
			int nCount = Integer.parseInt(StringHelper.evl( jtrCount.getFieldString("CNT"),"0"));
			
			String[] sTcGp = {"",""};
			for( int i=0; i<listTcStat.size(); i++)
			{
				jtrCheck = (JDTORecord)listTcStat.get(i);
				sTcGp[i] = jtrCheck.getFieldString("EQUIP_GP");
				jtrTcStat.setField("EQUIPCODE"+String.valueOf(i), sTcGp[i]);
			}
			if (nCount == 0 &&  listTcStat.size() != 0){
				// 현재 사용가능한 대차가 존재한다.
				return jtrTcStat;
			
			}else if (nCount != 0 && listTcStat.size()==0){
				// 현재 사용가능한 대차가 존재하지 않는다.
				jtrTcStat = null;
				
			}
		}catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return jtrTcStat;
	}
	
	/**
	 * LINE IN 작업일 경우에는 해당 스케쥴코드에 해당하는 콘베이어 정보를 생성하고 해당 콘베이어 정보를 To위치로 셋팅한다.
	 * 
	 * A열연 SPM 1. 보급(Line In) : 적치열(1DKE01), Schedule Code(CSLI), 저장품이동조건(D2) 2.
	 * Take-In : 적치열(1EKE01), Schedule Code(CSTI), 저장품이동조건(D5) B열연 SPM 1.
	 * 보급(Line In) : 적치열(3CKE01), Schedule Code(CSLI), 저장품이동조건(D2) 2. Take-In :
	 * 적치열(3BKE01), Schedule Code(CSTI), 저장품이동조건(D5)
	 * 
	 * A열연 HFL 1. 보급(Line In) : 적치열(1BFE01), Schedule Code(CHLI), 저장품이동조건(E2) 2.
	 * Take-In : 적치열(1BFE01), Schedule Code(CHTI), 저장품이동조건(E5) B열연 HFL 1.
	 * 보급(Line In) : 적치열(3AFE01), Schedule Code(CHLI), 저장품이동조건(E2) 2. Take-In :
	 * 적치열(3AFE01), Schedule Code(CHTI), 저장품이동조건(E5)
	 * 
	 * @param String
	 * @return JDTORecord
	 * @throws Exception
	 */
	private JDTORecord getConveyorToLocInfo(String sSchCode, String sYdGp) {
		JDTORecord ToLocV = JDTORecordFactory.getInstance().create();
		String sToLoc = "";
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			if (YmCommonConst.YD_GP_1.equals(sYdGp)) {

				if (YmCommonConst.NEW_SCH_WORK_KIND_CKLI.equals(sSchCode)) {
					sToLoc = YmCommonConst.SPM_COL_1DKE	+ YmCommonConst.STACK_BED_GP_01;
				} else if (YmCommonConst.NEW_SCH_WORK_KIND_CKTI.equals(sSchCode)) {
					sToLoc = YmCommonConst.SPM_COL_1EKE	+ YmCommonConst.STACK_BED_GP_01;
				} else if (YmCommonConst.NEW_SCH_WORK_KIND_CFLI.equals(sSchCode)) {
					sToLoc = YmCommonConst.HFL_COL_1BFE	+ YmCommonConst.STACK_BED_GP_01;
				} else if (YmCommonConst.NEW_SCH_WORK_KIND_CFTI.equals(sSchCode)) {
					sToLoc = YmCommonConst.HFL_COL_1BFE	+ YmCommonConst.STACK_BED_GP_01;
				}

			} else if (YmCommonConst.YD_GP_3.equals(sYdGp)) {

				if (YmCommonConst.NEW_SCH_WORK_KIND_CKLI.equals(sSchCode)) {
					sToLoc = YmCommonConst.SPM_COL_3CKE+ YmCommonConst.STACK_BED_GP_01;
				} else if (YmCommonConst.NEW_SCH_WORK_KIND_CKTI.equals(sSchCode)) {
					sToLoc = YmCommonConst.SPM_COL_3BKE	+ YmCommonConst.STACK_BED_GP_01;
				} else if (YmCommonConst.NEW_SCH_WORK_KIND_CFLI.equals(sSchCode)) {
					sToLoc = YmCommonConst.HFL_COL_3AFE	+ YmCommonConst.STACK_BED_GP_01;
				} else if (YmCommonConst.NEW_SCH_WORK_KIND_CFTI.equals(sSchCode)) {
					sToLoc = YmCommonConst.HFL_COL_3BFE	+ YmCommonConst.STACK_BED_GP_01;
				}
			}

			if (!"".equals(sToLoc)) {
				int iSeq = YmCommonDB.shiftConveyorInfo(sToLoc,
												    YmCommonConst.GBN_MIN);

				if (iSeq < 1) {
					throw new EJBServiceException("=COIL-SCHEDULE=>CONVEYOR CREATE FAIL=" + iSeq);
				}

				ToLocV.setField("TO_STACK_COL_GP"		, sToLoc);
				ToLocV.setField("TO_STACK_BED_GP"		, YmCommonConst.STACK_BED_GP_01);
				ToLocV.setField("TO_STACK_LAYER_GP"	, YmCommonConst.STACK_LAYER_GP_01);
			}
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return ToLocV;
	}
	/**
	 * LINE IN 작업일 경우에는 해당 스케쥴코드에 해당하는 콘베이어 정보를 생성하고 해당 콘베이어 정보를 To위치로 셋팅한다.
	 * 
	 * A열연 SPM 1. 보급(Line In) : 적치열(1DKE01), Schedule Code(CSLI), 저장품이동조건(D2) 2.
	 * Take-In : 적치열(1EKE01), Schedule Code(CSTI), 저장품이동조건(D5) B열연 SPM 1.
	 * 보급(Line In) : 적치열(3CKE01), Schedule Code(CSLI), 저장품이동조건(D2) 2. Take-In :
	 * 적치열(3BKE01), Schedule Code(CSTI), 저장품이동조건(D5)
	 * 
	 * A열연 HFL 1. 보급(Line In) : 적치열(1BFE01), Schedule Code(CHLI), 저장품이동조건(E2) 2.
	 * Take-In : 적치열(1BFE01), Schedule Code(CHTI), 저장품이동조건(E5) B열연 HFL 1.
	 * 보급(Line In) : 적치열(3AFE01), Schedule Code(CHLI), 저장품이동조건(E2) 2. Take-In :
	 * 적치열(3AFE01), Schedule Code(CHTI), 저장품이동조건(E5)
	 * 
	 * 함수 오버로딩. 인자 추가.
	 * 
	 * @param String
	 * @param String
	 * @param String
	 * @return JDTORecord
	 * @throws Exception
	 */
	private JDTORecord getConveyorToLocInfo(String sSchCode, String sYdGp,   String sBayGp) {
		
		JDTORecord ToLocV = JDTORecordFactory.getInstance().create();
		String sToLoc = "";
		String sPutLoc = "";
		
		JDTORecord jtrCoilLoc= JDTORecordFactory.getInstance().create();

		// CGS 추가 ========
		String sPos = "";						// conveyor 위치 정보 
		boolean bLineInCheckLoc = false;		// 보급위치 파악 검사 여부
		String sPutLocStat = "";		// 적치 대 상황 플래그.
		boolean bLineInSpm2 = false;
		JDTORecord jtrTuInfo = null;
		//==================
		

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			logger.println(LogLevel.DEBUG, this,">>Start - getConveyorToLocInfo("+sSchCode+", "+sYdGp+", "+sBayGp+")");
			if (YmCommonConst.YD_GP_1.equals(sYdGp)) {										// A열연 
				logger.println(LogLevel.DEBUG, this,"getConveyorToLocInfo()>>A열연 정보");

				if (YmCommonConst.NEW_SCH_WORK_KIND_EQLI.equals(sSchCode)) {
					if(sBayGp.equals("E")){
						sToLoc ="1EQE"	+ YmCommonConst.STACK_BED_GP_01;
					}else{
						sToLoc = "1FQE"	+ YmCommonConst.STACK_BED_GP_01;
					}
				} else if (YmCommonConst.NEW_SCH_WORK_KIND_CKLI.equals(sSchCode)) {
					sToLoc = YmCommonConst.SPM_COL_1DKE	+ YmCommonConst.STACK_BED_GP_01;
				} else if (YmCommonConst.NEW_SCH_WORK_KIND_EQTI.equals(sSchCode)) {
					if(sBayGp.equals("E")){
						sToLoc ="1EQE"	+ YmCommonConst.STACK_BED_GP_01;
					}else{
						sToLoc = "1FQE"	+ YmCommonConst.STACK_BED_GP_01;
					}
				} else if (YmCommonConst.NEW_SCH_WORK_KIND_CKTI.equals(sSchCode)) {
					sToLoc = YmCommonConst.SPM_COL_1EKE	+ YmCommonConst.STACK_BED_GP_01;
				} else if (YmCommonConst.NEW_SCH_WORK_KIND_CFLI.equals(sSchCode)) {
					sToLoc = YmCommonConst.HFL_COL_1BFE	+ YmCommonConst.STACK_BED_GP_01;
				} else if (YmCommonConst.NEW_SCH_WORK_KIND_CFTI.equals(sSchCode)) {
					sToLoc = YmCommonConst.HFL_COL_1BFE	+ YmCommonConst.STACK_BED_GP_01;
				}

			} else if (YmCommonConst.YD_GP_3.equals(sYdGp)) {								// B열연 
				logger.println(LogLevel.DEBUG, this,"getConveyorToLocInfo()>>B열연 정보");

				// 신규 #2 SPM 관련 내용 추가 CGS 
				if (YmCommonConst.NEW_SCH_WORK_KIND_CKLI.equals(sSchCode)) {	// #1 SPM 보급
					// 기존 C동 보급
					if (sBayGp.equals(YmCommonConst.BAY_GP_C)){
						//====Start============================================================================

//						// CGS
//						// SPM 설비의 ECC1, ECC2의 보급 상태를 검사한다.CGS
//						// CGS 추가 
//						// 보급 위치 결정하기 전에 조업의 ECC1, ECC2 위치의 소재 정보와 야드의 소재 위치를 동기화 
						EJBConnector ejbCon = null;
						try {	        
							ejbCon = new EJBConnector("default", "JNDISPMConStatReg", this);
					        ejbCon.trx("SyncTrackingConv", new Class[]{ String.class, String.class, String.class}, 
					        		                            new Object[]{ YmCommonConst.YD_GP_3, YmCommonConst.WORK_SPM_S, YmCommonConst.PROCESS_ID_1 });
					        bLineInCheckLoc = true;
						}catch (Exception e) {
							logger.println(LogLevel.DEBUG, this, "위치 동기화 오류 발생.SyncTrackingConv EJB 호출 실패");
							bLineInCheckLoc = false;
					    }
//						// CGS 추가
//						// SPM설비 내의 위치 판단 [ ECC1/ECC2 ]
//					    // sPos 	"ECC1"
//					    //			"ECC2"
//					    // 			""						
//
//						bLineInCheckLoc = true;

						sToLoc = YmCommonConst.SPM_COL_3CKE+ YmCommonConst.STACK_BED_GP_01;	
					}
					
					// B동 보급 추가
					if (sBayGp.equals(YmCommonConst.BAY_GP_B)){
						//====Start============================================================================

//						// CGS
//						// SPM 설비의 ECC1, ECC2의 보급 상태를 검사한다.CGS
//						// CGS 추가 
//						// 보급 위치 결정하기 전에 조업의 ECC1, ECC2 위치의 소재 정보와 야드의 소재 위치를 동기화 
						EJBConnector ejbCon = null;
						try {	        
							ejbCon = new EJBConnector("default", "JNDISPMConStatReg", this);
					        ejbCon.trx("SyncTrackingConv2", new Class[]{ String.class, String.class, String.class}, 
					        		                            new Object[]{ YmCommonConst.YD_GP_3, YmCommonConst.WORK_SPM_S, YmCommonConst.PROCESS_ID_1 });
					        bLineInCheckLoc = true;
						}catch (Exception e) {
							logger.println(LogLevel.DEBUG, this, "위치 동기화 오류 발생.SyncTrackingConv EJB 호출 실패");
							bLineInCheckLoc = false;
					    }

						sToLoc = YmCommonConst.SPM_COL_3BKE+ YmCommonConst.STACK_BED_GP_01;	
					}
				} else if (YmCommonConst.NEW_SCH_WORK_KIND_CKTI.equals(sSchCode)) {			// SPM Take In
					// 기존 B동 TakeIn
					if (sBayGp.equals(YmCommonConst.BAY_GP_B)) {
						sToLoc = YmCommonConst.SPM_COL_3BKE	+ YmCommonConst.STACK_BED_GP_01;
					}

				} else if (YmCommonConst.NEW_SCH_WORK_KIND_CFLI.equals(sSchCode)) {		// HFL 보급
					sToLoc = YmCommonConst.HFL_COL_3AFE	+ YmCommonConst.STACK_BED_GP_01;
					
				} else if (YmCommonConst.NEW_SCH_WORK_KIND_CFTI.equals(sSchCode)) {		// HFL Take In
					sToLoc = YmCommonConst.HFL_COL_3BFE	+ YmCommonConst.STACK_BED_GP_01;
					
				} else if (YmCommonConst.NEW_SCH_WORK_KIND_CNLI.equals(sSchCode)) {		// #2 SPM 보급
					// 신규 D동 보급
					if (sBayGp.equals(YmCommonConst.BAY_GP_D)) {
						// CGS
						// 나중에 설비 위치하고 받으면 보급 1,2 위치 판단 코드 작성.
						// 01 적치단의 
						//====================================================================================================================
						String queryid = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.getListTurnerStat";
						List listArgu = new ArrayList();
						listArgu.clear();
						listArgu.add("3DTU01");
						listArgu.add("3");
						EJBConnector ejbConn1 = new EJBConnector("default","JNDICraneStatusReg",this);
						List listTuInfo2 = (List)ejbConn1.trx("getCoilYmBCraneStatFlex",
																		new Class[]{String.class, List.class},new Object[]{queryid, listArgu});
						if (listTuInfo2.size() > 0){
							jtrTuInfo = (JDTORecord)listTuInfo2.get(0);
						}
						
						//====================================================================================================================
						if( jtrTuInfo != null ){
						if (jtrTuInfo.getFieldString("EQUIPSTAT").equals("정상")){
							sToLoc = YmCommonConst.SPM_COL_3DKE+ YmCommonConst.STACK_BED_GP_01;
						}else if (jtrTuInfo.getFieldString("EQUIPSTAT").equals("고장")){
							sToLoc = YmCommonConst.SPM_COL_3DKE+ YmCommonConst.STACK_BED_GP_01/*+YmCommonConst.STACK_LAYER_GP_02*/;
							bLineInSpm2 = true;
						}
						}else {
							sToLoc = YmCommonConst.SPM_COL_3DKE+ YmCommonConst.STACK_BED_GP_01;
							bLineInSpm2 = false;
						}
						logger.println(LogLevel.DEBUG, this,"#2 SPM 보급[D동]  "+sPos + ", To위치 지정 :"+sToLoc);
					}else if(sBayGp.equals(YmCommonConst.BAY_GP_E)) {
						sToLoc = YmCommonConst.SPM_COL_3EKE+ YmCommonConst.STACK_BED_GP_01;
						logger.println(LogLevel.DEBUG, this,"#2 SPM 보급[E동]  "+sPos + ", To위치 지정 :"+sToLoc);
					}
				} else if (YmCommonConst.NEW_SCH_WORK_KIND_CNTI.equals(sSchCode)) {		// #2 SPM Take In
					// 신규 E동 TakeIn
					if (sBayGp.equals(YmCommonConst.BAY_GP_D)) {
						sToLoc = YmCommonConst.SPM_COL_3DKE	+ YmCommonConst.STACK_BED_GP_01;
						logger.println(LogLevel.DEBUG, this,"#2 SPM Take-In  "+sPos + ", To위치 지정 :"+sToLoc);
					}else
					if (sBayGp.equals(YmCommonConst.BAY_GP_E)) {
						sToLoc = YmCommonConst.SPM_COL_3EKE	+ YmCommonConst.STACK_BED_GP_01;
						logger.println(LogLevel.DEBUG, this,"#2 SPM Take-In  "+sPos + ", To위치 지정 :"+sToLoc);
					}
				}else if (YmCommonConst.NEW_SCH_WORK_KIND_CFSI.equals(sSchCode)) {		// HFL 결속대 보급
					
					String sQueryId = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.getCoilInfo_HFL";
					List listCoilLoc = dao.getListData(sQueryId, new Object[] {sBayGp});
					
					jtrCoilLoc = (JDTORecord)listCoilLoc.get(0);
					sToLoc = StringHelper.evl(jtrCoilLoc.getFieldString("STACK_COL_GP"),"");
					sPutLoc = StringHelper.evl(jtrCoilLoc.getFieldString("STACK_BED_GP"),"");
					
					ToLocV.setField("TO_STACK_COL_GP"		, sToLoc);
					ToLocV.setField("TO_STACK_BED_GP"		, sPutLoc);
					ToLocV.setField("TO_STACK_LAYER_GP"		, YmCommonConst.STACK_LAYER_GP_01);
					
					logger.println(LogLevel.DEBUG, this,"설비위치:HFL결속대  sToLoc:" + sToLoc + sPutLoc);
					logger.println(LogLevel.DEBUG, this,"getConveyorToLocInfo() TO위치 결정:" + ToLocV);
					return ToLocV;
				}
			}

			
			logger.println(LogLevel.DEBUG, this,"설비위치:선정된 sToLoc:" + sToLoc);
			
			// To 위치(적치열)가 지정이 되면. 
			if (!"".equals(sToLoc)) {
//최규성 2
				if( YmCommonConst.NEW_SCH_WORK_KIND_CNLI.equals(sSchCode) && YmCommonConst.YD_GP_3.equals(sYdGp) && bLineInSpm2){
					// LAYER_02 에 적치해야 하는 경우.
					logger.println(LogLevel.DEBUG, this,"TO위치를 새로 생성한다. shiftConveyorInfo("+sToLoc+","+"02"+") 호출");
					// sToLoc : 6자리 ?
					int iSeq = YmCommonDB.shiftConveyorInfo(sToLoc,
															YmCommonConst.STACK_LAYER_GP_01);

					if (iSeq < 1) {
						throw new EJBServiceException("=COIL-SCHEDULE=>CONVEYOR CREATE FAIL=" + iSeq);
					}
					// 위치가 지정되지 않은 경우엔 기존의 방식대로 shift한 후에  01위치로 보급위치를 설정한다.
					ToLocV.setField("TO_STACK_COL_GP"		, sToLoc);
					ToLocV.setField("TO_STACK_BED_GP"		, YmCommonConst.STACK_BED_GP_02);
					ToLocV.setField("TO_STACK_LAYER_GP"	, YmCommonConst.STACK_LAYER_GP_01);
//기존 소스로 복귀함. 보급과 관련된 코드를 적용할 때 주석 처리되어 있어야 함. 
//2009-06-22 CGS

				}else if (YmCommonConst.NEW_SCH_WORK_KIND_CKLI.equals(sSchCode) && YmCommonConst.YD_GP_3.equals(sYdGp)) {
					// CGS 정리
					// 기존 처리 방법 
					// 설비 보급시 TO위치 지정은 현물과 상관없이 야드정보상 Shift하여 항상 3CKE010101 위치가 TO위치가 되도록 정한다.
					// 수정된 처리 방법
					// 
					
	// CGS 주석처리 				
	//				int iSeq = YmCommonDB.shiftConveyorInfo(sToLoc,
	//												    YmCommonConst.GBN_MIN);
	//
	//				if (iSeq < 1) {
	//					throw new EJBServiceException("=COIL-SCHEDULE=>CONVEYOR CREATE FAIL=" + iSeq);
	//				}
					
					//SELECT SCH_ID, STOCK_ID,CRANE_WORD_PUT_LOC 
					//FROM TB_YM_SCH
					//WHERE CRANE_WORD_PUT_LOC LIKE :sToLoc||'%'
					//AND SCH_WORK_KIND = :SchCode
					//ORDER BY SCH_ID DESC
//					String sQueryId_cmp = "ym.scheduling.crane.dao.CrandSchDAO.getschinfo";
//					List listSchToLoc = dao.getListData(sQueryId_cmp, new Object[] {sToLoc, sSchCode});
//					JDTORecord jtrSchInfo = null;
					String sCranePutLoc = "";
					String[] sSchBayInfo = {"",""};
					
//					if (listSchToLoc.size() > 0) {
//						for(int i=0;i<listSchToLoc.size();i++)
//						{
//							jtrSchInfo = (JDTORecord)listSchToLoc.get(i);
//							logger.println(LogLevel.DEBUG, this,"스케줄 정보 레코드 " + jtrSchInfo);
//							sCranePutLoc = StringHelper.evl(jtrSchInfo.getFieldString("CRANE_WORD_PUT_LOC"),"");
//							// 빈 곳의 위치를 찾는다.
//							// 생성된 스케줄의 TO위치를 가져온다.
//							sSchBayInfo[i] = sCranePutLoc.substring(6,8);
//							logger.println(LogLevel.DEBUG, this,"스케줄 Bay정보 레코드 " + sSchBayInfo[i]);
//						}
//					}

					// 오직 적치 단 상태(STACK_LAYER_STAT)가 적치 가능("E") 인 데이터만 가져온다.
					// SELECT STACK_COL_GP,STOCK_ID, STACK_BED_GP,STACK_LAYER_STAT
					//	FROM TB_YM_STACKLAYER
					//	WHERE STACK_COL_GP = ?
					//	AND STACK_BED_GP IN ('01', '02')
					//	AND STACK_LAYER_STAT = 'E'
					//	ORDER BY STACK_COL_GP ASC
					//
					String sQueryId_EmptyBay = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.getCoilInfo_spm";
					List listCoilPos = dao.getListData(sQueryId_EmptyBay, new Object[] {sToLoc});
					
					JDTORecord jtrCoilPos = null;
					if (listCoilPos.size() > 0) {
						for(int j=0;j<listCoilPos.size();j++)
						{
							jtrCoilPos = (JDTORecord)listCoilPos.get(j);
							sCranePutLoc = StringHelper.evl(jtrCoilPos.getFieldString("STACK_BED_GP"),"");
							sSchBayInfo[j] = sCranePutLoc;
							logger.println(LogLevel.DEBUG, this,"스케줄 Bay정보 " + sSchBayInfo[j]);
						}
					}
					//sSchBayInfo 데이터 01 또는 02
					// sPutLocStat  1 : 01 적치대가 비었음
					//              2 : 02 적치대가 비었음
					//              A : 01,02 적치대가 비었음
					//              N : 비어있는 적치대가 없음.
					
					if( sSchBayInfo[0].equals("01") && sSchBayInfo[1].equals("")  ) 
					{
						sPutLocStat = "1";		// 01이 비었음
					}else if(	sSchBayInfo[0].equals("02") && sSchBayInfo[1].equals("") ) 
					{
						sPutLocStat = "2";		// 02가 비었음
					}else if(  !sSchBayInfo[0].equals("") &&  !sSchBayInfo[1].equals("") ) 
					{
						sPutLocStat = "A";		// 01,02 비었음
					}else if(	sSchBayInfo[0].equals("") &&   sSchBayInfo[1].equals("") ) 
					{
						sPutLocStat = "N";		// 빈 곳 없음
					}else 
					{
						sPutLocStat = "F";		// 잘못됨.
					}
					logger.println(LogLevel.DEBUG, this,"야드 적치열 상황: " + sPutLocStat);
				}

				// CGS 수정
				// 보급 위치를 결정한다.
				// 보급 위치가 강제로 결정된다.
				if (bLineInCheckLoc) {		// SPM보급일 때만 적용.
					if (sPutLocStat.equals("1")) {
						logger.println(LogLevel.DEBUG, this,"야드 적치 대 상황: 0"+sPutLocStat+"위치가 비었음" );
						ToLocV.setField("TO_STACK_COL_GP"		, sToLoc);
						ToLocV.setField("TO_STACK_BED_GP"		, YmCommonConst.STACK_BED_GP_01);
						ToLocV.setField("TO_STACK_LAYER_GP"		, YmCommonConst.STACK_LAYER_GP_01);
					}else if (sPutLocStat.equals("A")) {
						logger.println(LogLevel.DEBUG, this,"야드 적치 대 상황: 모두 비었음 " + sPutLocStat);
						ToLocV.setField("TO_STACK_COL_GP"		, sToLoc);
						ToLocV.setField("TO_STACK_BED_GP"		, YmCommonConst.STACK_BED_GP_02);
						ToLocV.setField("TO_STACK_LAYER_GP"		, YmCommonConst.STACK_LAYER_GP_01);
					}else if (sPutLocStat.equals("N")) {
						logger.println(LogLevel.DEBUG, this,"야드 적치 대 상황: 빈 곳 없음" + sPutLocStat);
						CraneSchDAO craneDao = new CraneSchDAO();

						List listTrk_po = new ArrayList();
						listTrk_po.clear();
						// 조업 DB의 위치정보 데이터 조회 쿼리
						/*
						select EQUIP_GP, STL_NO from TB_PO_ABHRTRACKING
						where equip_gp IN ( :sEquip_gp01, :sEquip_gp02 )
						and plant_gp = 'B'
						and proc_gp = :sProcGp
						order by equip_gp asc
						*/
						String sProcGp = "K";
						String sEquipGp01 = "ECC1";
						String sEquipGp02 = "ECC2";
						logger.println(LogLevel.DEBUG, this,"조업의 설비측 TRK 정보 조회" ); 
						String sQueryId_po = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.getTrackingLoc_po";
						
						if (sYdGp.equals(YmCommonConst.YD_GP_3) )
						{
							if (YmCommonConst.NEW_SCH_WORK_KIND_CKLI.equals(sSchCode) ) 					// #1 SPM 보급 
							{
								if (sBayGp.equals(YmCommonConst.BAY_GP_C)){
									sProcGp = "K";
									sEquipGp01 = "ECC1";
									sEquipGp02 = "ECC2";
								}else if (sBayGp.equals(YmCommonConst.BAY_GP_B)){
									sProcGp = "K";
									sEquipGp01 = "ECC7";
									sEquipGp02 = "ECC7";
								}
							}
						}

						listTrk_po = craneDao.getListData(sQueryId_po,new Object[]{sEquipGp01,sEquipGp02,sProcGp});

						logger.println(LogLevel.DEBUG, this,"조업의 설비측 TRK 정보 조회" + listTrk_po);
						
						String[] sPO_CoilNo = {"","",""};
						JDTORecord jtrPo_val = null;
						
						// 조업DB에서 가져온 데이터 처리
						for(int idx=0;idx<listTrk_po.size();idx++)
						{
							jtrPo_val = (JDTORecord)listTrk_po.get(idx);
							sPO_CoilNo[idx] = StringHelper.evl(jtrPo_val.getFieldString("STL_NO"),"");
							logger.println(LogLevel.DEBUG,this, "조업DB 조회 "+String.valueOf(idx+1)+":"+sPO_CoilNo[idx]);
						}

						String sQueryId_Sync = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.getCoilInfo_Sync";
						List listCoilPosStat = dao.getListData(sQueryId_Sync, new Object[] {sToLoc});

						String[] sStackStat = new String[2];
						//==================================================================================================================
						// java.lang.NullPointerException 발생으로 로직 추가. 최규성 2009-12-16
						int nlistCoilPosStat = listCoilPosStat.size();
						
						if(nlistCoilPosStat < 1 || listCoilPosStat.isEmpty())
						{
							logger.println(LogLevel.DEBUG, this,"YM의 정보를 가져오지 못함."+String.valueOf(nlistCoilPosStat)+" list:"+listCoilPosStat);
							logger.println(LogLevel.DEBUG, this,"YM의 정보를 가져오지 못함. shiftConveyorInfo("+sToLoc+","+YmCommonConst.GBN_MIN+") 호출");
							// sToLoc : 6자리 ?
							int iSeq = YmCommonDB.shiftConveyorInfo(sToLoc,
															    YmCommonConst.GBN_MIN);

							if (iSeq < 1) {
								throw new EJBServiceException("=COIL-SCHEDULE=>CONVEYOR CREATE FAIL=" + iSeq);
							}
							// 위치가 지정되지 않은 경우엔 기존의 방식대로 shift한 후에  01위치로 보급위치를 설정한다.
							ToLocV.setField("TO_STACK_COL_GP"		, sToLoc);
							ToLocV.setField("TO_STACK_BED_GP"		, YmCommonConst.STACK_BED_GP_01);
							ToLocV.setField("TO_STACK_LAYER_GP"	, YmCommonConst.STACK_LAYER_GP_01);
							
							return ToLocV;
						}
						//==================================================================================================================

						for (int i=0; i< listCoilPosStat.size(); i++ )
						{
							JDTORecord jtrS = (JDTORecord)listCoilPosStat.get(i);
							sStackStat[i] = jtrS.getFieldString("STACK_LAYER_STAT");
							logger.println(LogLevel.DEBUG, this,"야드의 설비측 적치정보 조회" +String.valueOf(i+1)+":"+ sStackStat[i]);
						}

						if (   ( sPO_CoilNo[0] == null || sPO_CoilNo[0].equals("")) 
							&& ( sPO_CoilNo[1] == null || sPO_CoilNo[1].equals("")) 			// 조업 01,02 모두 비었음.
						   )
						{
							// 02위치부터 2번 Shift 처리한다.

								logger.println(LogLevel.DEBUG, this,"두 곳 가능으로 간주 " + sPutLocStat);
								ToLocV.setField("TO_STACK_COL_GP"		, sToLoc);
								ToLocV.setField("TO_STACK_BED_GP"		, YmCommonConst.STACK_BED_GP_02);
								ToLocV.setField("TO_STACK_LAYER_GP"		, YmCommonConst.STACK_LAYER_GP_01);
												
								if (sBayGp.equals(YmCommonConst.BAY_GP_C)){
									EJBConnector ejbCon = new EJBConnector("default", "JNDISPMConStatReg", this);
									ejbCon.trx("SyncTrackingConv", new Class[]{ String.class, String.class, String.class}, 
														new Object[]{ YmCommonConst.YD_GP_3, YmCommonConst.WORK_SPM_S, YmCommonConst.PROCESS_ID_1 });
								}else if (sBayGp.equals(YmCommonConst.BAY_GP_B)){
									EJBConnector ejbCon = new EJBConnector("default", "JNDISPMConStatReg", this);
									ejbCon.trx("SyncTrackingConv2", new Class[]{ String.class, String.class, String.class}, 
														new Object[]{ YmCommonConst.YD_GP_3, YmCommonConst.WORK_SPM_S, YmCommonConst.PROCESS_ID_1 });
								}

						}else if (   (sPO_CoilNo[0] == null ||  sPO_CoilNo[0].equals("") )
								  && (sPO_CoilNo[1] != null && !sPO_CoilNo[1].equals("") )		// 조업 01만 비었음.
								 )
						{
							// 01위치부터 1번 shift하여 처리한다.
//							if (sStackStat[0].equals("P") && sStackStat[1].equals("L") ){
								logger.println(LogLevel.DEBUG, this,"한 곳 가능으로 간주 " + sPutLocStat);
								ToLocV.setField("TO_STACK_COL_GP"		, sToLoc);
								ToLocV.setField("TO_STACK_BED_GP"		, YmCommonConst.STACK_BED_GP_01);
								ToLocV.setField("TO_STACK_LAYER_GP"		, YmCommonConst.STACK_LAYER_GP_01);
													
								if (sBayGp.equals(YmCommonConst.BAY_GP_C)){
									EJBConnector ejbCon = new EJBConnector("default", "JNDISPMConStatReg", this);
									ejbCon.trx("SyncTrackingConv", new Class[]{ String.class, String.class, String.class}, 
														new Object[]{ YmCommonConst.YD_GP_3, YmCommonConst.WORK_SPM_S, YmCommonConst.PROCESS_ID_1 });
								}else if (sBayGp.equals(YmCommonConst.BAY_GP_B)){
									EJBConnector ejbCon = new EJBConnector("default", "JNDISPMConStatReg", this);
									ejbCon.trx("SyncTrackingConv2", new Class[]{ String.class, String.class, String.class}, 
														new Object[]{ YmCommonConst.YD_GP_3, YmCommonConst.WORK_SPM_S, YmCommonConst.PROCESS_ID_1 });
								}
//							}
						}else {																	// 적치 불가 정보
							logger.println(LogLevel.DEBUG, this,"적치 가능한 곳 없음." + sPutLocStat);
							logger.println(LogLevel.DEBUG, this,"기존 방식대로 01 위치 이동." + sPutLocStat);
							//
							int iSeq = YmCommonDB.shiftConveyorInfo(sToLoc, YmCommonConst.GBN_MIN);

							if (iSeq < 1) {
								throw new EJBServiceException("=COIL-SCHEDULE=>CONVEYOR CREATE FAIL=" + iSeq);
							}
							// 위치가 지정되지 않은 경우엔 기존의 방식대로 shift한 후에  01위치로 보급위치를 설정한다.
							ToLocV.setField("TO_STACK_COL_GP"		, sToLoc);
							ToLocV.setField("TO_STACK_BED_GP"		, YmCommonConst.STACK_BED_GP_01);
							ToLocV.setField("TO_STACK_LAYER_GP"	, YmCommonConst.STACK_LAYER_GP_01);
							//ToLocV = null;
						}
						
						//ToLocV = null;						
					}
				} else
				{
					if( bLineInSpm2 == false){
					logger.println(LogLevel.DEBUG, this,"TO위치를 새로 생성한다. shiftConveyorInfo("+sToLoc+","+YmCommonConst.GBN_MIN+") 호출");
					// sToLoc : 6자리 ?
					int iSeq = YmCommonDB.shiftConveyorInfo(sToLoc,
													    YmCommonConst.GBN_MIN);

					if (iSeq < 1) {
						throw new EJBServiceException("=COIL-SCHEDULE=>CONVEYOR CREATE FAIL=" + iSeq);
					}
					// 위치가 지정되지 않은 경우엔 기존의 방식대로 shift한 후에  01위치로 보급위치를 설정한다.
					ToLocV.setField("TO_STACK_COL_GP"		, sToLoc);
					ToLocV.setField("TO_STACK_BED_GP"		, YmCommonConst.STACK_BED_GP_01);
					ToLocV.setField("TO_STACK_LAYER_GP"	, YmCommonConst.STACK_LAYER_GP_01);
					
					}

				}
			}
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		logger.println(LogLevel.DEBUG, this,"getConveyorToLocInfo() TO위치 결정:" + ToLocV);
		return ToLocV;
	}
	/**
	 * OPERATOR 지정위치를 TO위치로 셋팅한다.
	 * 
	 * @param String :
	 *            PUT위치(10자리)
	 * @return JDTORecord
	 * @throws Exception
	 */
	private JDTORecord getCoilColLocPutInfo(String sPutLoc) throws Exception {
		JDTORecord jRecord = JDTORecordFactory.getInstance().create();

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			String sStackColGp 	= sPutLoc.substring(0, 6);
			String sStackBedGp 	= sPutLoc.substring(6, 8);
			String sStackLayerGp 	= sPutLoc.substring(8, 10);

			JDTORecord colJr = dao.getStackLayerInfoWithPk(sStackColGp,
													sStackBedGp, 
													sStackLayerGp);

			String tStockId = "";
			String tLayerStat = "";

			if (colJr != null) {

				tStockId 	 = StringHelper.evl(colJr.getFieldString("STOCK_ID"),"");
				tLayerStat = StringHelper.evl(colJr.getFieldString("STACK_LAYER_STAT"), "");
			}

			//20091126 정종균 : 재료번호고 존재 하는 경우에도 에러처리
			if ((!"".equals(tStockId) && YmCommonConst.STACK_LAYER_STAT_L.equals(tLayerStat))||
				 !"".equals(tStockId)	) {

				jRecord = null;
				logger.println(LogLevel.DEBUG, this, "==10자리 위치 검색 FAIL");
			} else {

				jRecord.setField("TO_STACK_COL_GP"		, sStackColGp);
				jRecord.setField("TO_STACK_BED_GP"		, sStackBedGp);
				jRecord.setField("TO_STACK_LAYER_GP"	, sStackLayerGp);
				logger.println(LogLevel.DEBUG, this, "==10자리 위치 검색 SUCCESS");
			}

		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return jRecord;
	}

	/**
	 * OPERATOR 지정위치를 TO위치로 셋팅한다.
	 * 
	 * @param String :
	 *            PUT위치(10자리)
	 * @return JDTORecord
	 * @throws Exception
	 */
	private JDTORecord getSlabColLocPutInfo(String sStackColGp,
											String sStackBedGp, 
											String sStackLayerGp) throws Exception {
		JDTORecord jRecord = JDTORecordFactory.getInstance().create();

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			JDTORecord colJr = dao.getStackLayerInfoWithPk(sStackColGp,
															sStackBedGp, 
															sStackLayerGp);

			String tStockId = "";
			String tLayerStat = "";

			if (colJr != null) {

				tStockId   = StringHelper.evl(colJr.getFieldString("STOCK_ID"),"");
				tLayerStat = StringHelper.evl(colJr.getFieldString("STACK_LAYER_STAT"), "");
			}

			if (!"".equals(tStockId)
				&& YmCommonConst.STACK_LAYER_STAT_L.equals(tLayerStat)) {

				jRecord = null;
				logger.println(LogLevel.DEBUG, this, "==10자리 위치 검색 FAIL");
			} else {
				jRecord.setField("STACK_COL_GP", sStackColGp);
				jRecord.setField("STACK_BED_GP", sStackBedGp);
				jRecord.setField("STACK_LAYER_GP", sStackLayerGp);
				logger.println(LogLevel.DEBUG, this, "==10자리 위치 검색 SUCCESS");
			}

		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return jRecord;
	}

	/**
	 * 검색열에서 유효한 적치단 정보를 검색한다. 
	 * 1. 적치열단위로 적합성여부를 체크해서 유효한 적치단 정보를 셋팅한다. 
	 * 2. 적치단 정보를 가지고 적합한 적치단 정보를 찾는다.
	 * 
	 * @param DAO : DAO
	 * @param JDTORecord : To Location
	 * @return JDTORecord
	 * @throws Exception
	 */
	private JDTORecord getNewCoilToLocInfo_002(List stackColL, JDTORecord stockV)throws Exception {
		String sErrorMsg = "";
		JDTORecord stockToLocV = null;

		try {
			
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			JDTORecord stackBedV = null;
			JDTORecord stackBedV2 = null;
			JDTORecord ruleV	  = null;
			String sStackColGp = "";
			String sStackColGp2 = "1";
			String sCOIL_CARD_NO ="";
			
			boolean isColGp = false;
			boolean isBedGp = false;

			String sUsageCd = ""; // 용도코드
			String sProgCd = ""; // 진도코드
			String sToLayerGp = ""; // 적치단구분
			String sPRIOR1  = "";   // 이동경로 순위
			int iTotalN = 0; 
			int iTotalSaveN = -1;
			int iFmN = 0;

			/**
			 * TO 위치 FAIL ERROR MESSAGE 처리 02
			 */
			if (stackColL.size() == 0) {
				setToLocFailLog(StringHelper.evl(stockV.getFieldString("STOCK_ID"), ""), 
								StringHelper.evl(stockV.getFieldString("GBN"), ""), 
								StringHelper.evl(stockV.getFieldString("SCH_WORK_KIND"), ""),
								YmCommonConst.MSG_TO_02);
			}else {
				
				stackBedV2 = (JDTORecord) stackColL.get(0);
				sStackColGp2 = StringHelper.evl(stackBedV2.getFieldString("TO_STACK_COL_GP"), "");
				
			}
			
			//평점계산 점수체크 기준을 가져 온다.
			if(sStackColGp2.substring(0,1).equals("3")){
				ruleV = dao.getStackRuleInfo_004();	//B열연 기준
			}else{
				ruleV = dao.getStackRuleInfo_003();	//A열연 기준
			}

			for (int inx = 0; inx < stackColL.size(); inx++) {

				stackBedV = (JDTORecord) stackColL.get(inx);
				sStackColGp = StringHelper.evl(stackBedV.getFieldString("TO_STACK_COL_GP"), "");				

				String sSchWorkKind = StringHelper.evl(stockV.getFieldString("SCH_WORK_KIND"), "");
				
				sPRIOR1	= StringHelper.evl(stackBedV.getFieldString("PRIOR1"), "");
				
				//차량상차스케줄 인 경우 
				if(sSchWorkKind.equals("CVM8") ||sSchWorkKind.equals("GVM8")){

					String sQueryId_EmptyBay = "ym.facilitystatus.facilityinquiry.dao.YdStockDAO.getCardNo";
					List listCoilPos = dao.getListData(sQueryId_EmptyBay, new Object[] {StringHelper.evl(stockV.getFieldString("STOCK_ID"), "")});
				 
					logger.println(LogLevel.DEBUG, this, "listCoilPos.size()"+ listCoilPos.size());
					JDTORecord jtrCoilPos = null;
					if (listCoilPos.size() > 0) {
						for(int j=0;j<listCoilPos.size();j++)
						{
							jtrCoilPos = (JDTORecord)listCoilPos.get(j);
							sCOIL_CARD_NO = StringHelper.evl(jtrCoilPos.getFieldString("CAR_CARD_NO"),"");
							
							//차량이적 차량별 상차위치 결정 작업
							if(sCOIL_CARD_NO.equals("9999")){
								sStackColGp =sStackColGp.substring(0, 4) + "09" ;
								stackBedV.setField("TO_STACK_COL_GP"	, sStackColGp);
							}else if(sCOIL_CARD_NO.equals("9998")){
								sStackColGp =sStackColGp.substring(0, 4) + "08" ;
								stackBedV.setField("TO_STACK_COL_GP"	, sStackColGp);
							}else if(sCOIL_CARD_NO.equals("9997")){
								sStackColGp =sStackColGp.substring(0, 4) + "07" ;
								stackBedV.setField("TO_STACK_COL_GP"	, sStackColGp);
							}else if(sCOIL_CARD_NO.equals("9996")){
								sStackColGp =sStackColGp.substring(0, 4) + "06" ;
								stackBedV.setField("TO_STACK_COL_GP"	, sStackColGp);
							}else if(sCOIL_CARD_NO.equals("9995")){
								sStackColGp =sStackColGp.substring(0, 4) + "05" ;
								stackBedV.setField("TO_STACK_COL_GP"	, sStackColGp);
							}
							
							//차량별 상차위치에 따른 상차 단위치 베드 정보 조회 작업 
							if(	sCOIL_CARD_NO.equals("9999")||
								sCOIL_CARD_NO.equals("9998")||
								sCOIL_CARD_NO.equals("9997")||
								sCOIL_CARD_NO.equals("9996")||
								sCOIL_CARD_NO.equals("9995") ){
								sQueryId_EmptyBay = "ym.facilitystatus.facilityinquiry.dao.YdStockDAO.getCarLayer";
								listCoilPos = dao.getListData(sQueryId_EmptyBay, new Object[] {sStackColGp});
								
								if (listCoilPos.size() > 0) {								
									jtrCoilPos = (JDTORecord)listCoilPos.get(0);
									stackBedV.setField("TO_STACK_BED_GP"	, StringHelper.evl(jtrCoilPos.getFieldString("TO_STACK_BED_GP"),""));
								}
								
							}
						}
					}
				}
				
				logger.println(LogLevel.DEBUG, this, sCOIL_CARD_NO +"-"+sStackColGp + "▶▶▶▶▶▶▶▶▶▶적치번지 TO위치 검색◀◀◀◀◀◀◀◀◀◀");
				/*
				 * To위치가 설비일 경우 설비상태도 체크한다. 
				 * A열연은 설비를 체크하지 않는다 B열연은 대차 및 수조탱크를 체크한다.
				 */
				isColGp = getCoilToLocInfo_003(sStackColGp, stockV);

				/**
				 * TO 위치 FAIL ERROR MESSAGE 처리 03
				 */
				if (!isColGp)
					sErrorMsg = YmCommonConst.MSG_TO_03;

				if (isColGp) {

					/*
					 * MAP정보를 확인해서 적치가능한지를 체크한다.
					 */
					isBedGp = getCoilToLocInfo_004(stackBedV);

					/**
					 * TO 위치 FAIL ERROR MESSAGE 처리 04
					 */
					if (!isBedGp)
						sErrorMsg = YmCommonConst.MSG_TO_04;

					if (isBedGp) {

						sProgCd = StringHelper.evl(stackBedV.getFieldString("COIL_PROG_CD"), "");
						sToLayerGp = StringHelper.evl(stackBedV.getFieldString("TO_STACK_LAYER_GP"), "");

						sUsageCd = YmCommonUtil.getStackColInfoWithPk(sStackColGp);

						if (YmCommonConst.STACK_COL_USAGE_CD_CX.equals(sUsageCd)
								|| // 대차정지위치
								YmCommonConst.STACK_COL_USAGE_CD_TX.equals(sUsageCd)
								|| // 차량정지위치
								//YmCommonConst.STACK_COL_USAGE_CD_FS.equals(sUsageCd)
								(YmCommonConst.STACK_COL_USAGE_CD_FS.equals(sUsageCd) && !sSchWorkKind.equals("CKLO") && !sSchWorkKind.equals("CFLO")) //SPM/HFL추출은 우선순위에 따른 TO위치 결정
								|| // CTS FROM SADDLE
								YmCommonConst.STACK_COL_USAGE_CD_TS.equals(sUsageCd) // CTS TO SADDLE
						) {
		 
								iFmN = 0;
								iTotalN = 999;
		 
						} else {
							/*
							 * 각 진도코드별로 적치기준점수를 가져온다.
							 */
							iFmN = getCoilToLocFmInfo(ruleV, sProgCd, sToLayerGp);

							/*
							 * TO위치의 적치기준 종합점수를 가져온다.
							 */
							iTotalN = getCoilToLocInfo_006(stackBedV, ruleV, sProgCd, sToLayerGp);
							
							//HFL/SPM추출 
							if((sSchWorkKind.equals("CKLO") || sSchWorkKind.equals("CFLO")) && YmCommonConst.STACK_COL_USAGE_CD_FS.equals(sUsageCd) ){
								//저장영역별검색순위가 1순위인 경우 에만 적용(설비 인경우)
								if(sPRIOR1.equals("1")){
									iFmN = 0;
									iTotalN = 999;
								}else{
									iTotalN = 20;
								}
							}
						}
					} else {
						iTotalN = -1;
					}

					/*
					 * 종합점수가 기준점수보다 높으면 해당 To위치정보를 셋팅하고 종료한다.
					 */
					if (iTotalN >= iFmN) {
						stockToLocV = stackBedV;
						iTotalSaveN = iTotalN;
						break;
						/*
						 * 종합점수가 기준점수보다 낮으면 해당 To위치정보를 셋팅하고 계속 To위치정보를 찾는다.
						 */
					} else {
						/*
						 * 기존에 셋팅된 To위치정보보다 우선순위가 앞서면 To위치정보를 다시 셋팅한다.
						 */
						if (iTotalN > iTotalSaveN) {
							stockToLocV = stackBedV;
							iTotalSaveN = iTotalN;
						}
					}
					logger.println(LogLevel.DEBUG, this, "=최종 종합판정점수=>" + iTotalSaveN);

				}
			}
			
			String sMsg = "저장품=" + StringHelper.evl(stockV.getFieldString("STOCK_ID"), "") + "[" + StringHelper.evl(stockV.getFieldString("GBN"), "") + "/" + StringHelper.evl(stockV.getFieldString("SCH_WORK_KIND"), "") + "]=>" + sErrorMsg;

			/**
			 * TO 위치 FAIL ERROR MESSAGE 처리 05
			 */
			if (stockToLocV == null && !"".equals(sErrorMsg)) {
				setToLocFailLog(StringHelper.evl(stockV.getFieldString("STOCK_ID"), ""), 
								StringHelper.evl(stockV.getFieldString("GBN"), ""), 
								StringHelper.evl(stockV.getFieldString("SCH_WORK_KIND"), ""),
								sErrorMsg);
			}
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}

		return stockToLocV;
	}

	/**
	 * To위치가 설비일 경우 설비상태도 체크한다. 
	 * - 현재는 A열연 SADDLE설비상태 체크한다. 
	 * - B열연 대차 설비상태 체크한다. 
	 * - 하차작업완료 직후인 경우,즉 설비적재상태가 'U'-하차작업,'I'-Idle상태인 경우는 To위치 설정 불가 'L'인 경우에만 To위치 가능하다. 
	 * - 상차스케쥴지정이면 상차스케쥴과 지정스케쥴이 같은지를 체크 같은 경우에만 To 위치 가능하다. 
	 * - 스케쥴미지정이면 현재 대차에 실려있는 코일이 있는지 체크 코일이 존재하면 코일의 스케쥴코드와 지정 스케쥴이 같은지를 체크 같은경우에만 To 위치 가능하다. 
	 * 코일이 존재하지 않으면 To 위치 가능하다.
	 * 
	 * @param DAO
	 * @param String : To 위치 적치열
	 * @param JDTORecord : 저장품 정보
	 * @return int 적합성 우선순위
	 * @throws Exception
	 */
	private boolean getCoilToLocInfo_003(String sStackColGp, JDTORecord stockV) throws Exception {
		boolean isVal = false;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String sGbn = StringHelper.evl(stockV.getFieldString("GBN"), "");
			String sYdGp = StringHelper.evl(stockV.getFieldString("YD_GP"), "");
			String sSchWorkKind = StringHelper.evl(stockV.getFieldString("SCH_WORK_KIND"), "");

			logger.println(LogLevel.DEBUG, this, sStackColGp + "=적치번지 TO위치 설비상태 체크====="+sSchWorkKind);

			if (YmCommonConst.SUB_WORK_S.equals(sGbn)) {

				/*
				 * 1. 보조작업인 경우는 무조건 To위치 True
				 */
				isVal = true;
				logger.println(LogLevel.DEBUG, this, "=보조작업 설비상태 To위치가능=====");

			} else {
				/*
				 * A열연인 경우 설비는 대차인 경우만 체크
				 */
				if (YmCommonConst.YD_GP_1.equals(sYdGp)) {

					String sUpUsageCd = YmCommonUtil.getStackColInfoWithPk(sStackColGp);

					if (YmCommonConst.STACK_COL_USAGE_CD_CX.equals(sUpUsageCd)) {
						/*
						 * 2.1 A열연 대차인 경우 CHECK
						 */
						JDTORecord equipJr = dao.getToEquipState(sStackColGp);

						String sStackStat   = StringHelper.evl(equipJr.getFieldString("STACK_STAT"), ""); // 적재상태
						String sAssignYn    = StringHelper.evl(equipJr.getFieldString("CARLOAD_ASSIGN_YN"), ""); // 상차스케쥴지정여부
						String sLoadSchCode = StringHelper.evl(equipJr.getFieldString("CARLOAD_SCH_WORK_KIND"), ""); // 상차스케쥴

						/*
						 * 3. A열연 대차인 경우는 적재상태 체크 'L'인 경우에 To위치 선정 가능한다.
						 */
						if ("L".equals(sStackStat)) {
							/*
							 * 상차 스케쥴 지정일 경우만 체크한다. 상차 스케쥴 미지정이면 FALSE
							 */
							if ("Y".equals(sAssignYn)) {
								/*
								 * 4. A열연 대차이고 상차스케쥴 지정이면 저장품 스케쥴코드와 상차스케쥴 코드를
								 * 체크 같으면 To 위치 True
								 */
								if (sSchWorkKind.equals(sLoadSchCode)) {

									isVal = true;
									logger.println(LogLevel.DEBUG, this,"=A열연 대차설비 적재상태 'L' =====");
									logger.println(LogLevel.DEBUG, this,"=A열연 대차설비 상차스케쥴 지정=====");
									logger.println(LogLevel.DEBUG, this,"=A열연 대차설비 저장품 스케쥴코드=상차스케쥴코드 동일");
									logger.println(LogLevel.DEBUG, this,"=A열연 대차설비 설비상태 To위치가능=====");
								} else {
									logger.println(LogLevel.DEBUG, this,"=A열연 대차설비 적재상태 'L' =====");
									logger.println(LogLevel.DEBUG, this,"=A열연 대차설비 상차스케쥴 지정=====");
									logger.println(LogLevel.DEBUG, this,"=A열연 대차설비 저장품 스케쥴코드<>상차스케쥴코드="+ sLoadSchCode);
									logger.println(LogLevel.DEBUG, this,"=A열연 대차설비 설비상태 To위치불가능=====");
								}
							}
						} else {
							logger.println(LogLevel.DEBUG, this,"=A열연 대차설비 적재상태 =" + sStackStat);
							logger.println(LogLevel.DEBUG, this,"=A열연 대차설비 설비상태 To위치불가능=====");
						}
					} else {
						isVal = true;
						logger.println(LogLevel.DEBUG, this,"=A열연 대차가 아니면 To위치가능=====");
					}

					/*
					 * B열연인 경우 설비는 TO위치가 대차 및 수조탱크를 체크한다.
					 */
				} else if (YmCommonConst.YD_GP_3.equals(sYdGp)) {

					String sUpUsageCd = YmCommonUtil
							.getStackColInfoWithPk(sStackColGp);

					if (YmCommonConst.STACK_COL_USAGE_CD_CW.equals(sUpUsageCd)) {
						/*
						 * 2.1 B열연 수조탱크인 경우 CHECK
						 */
						isVal = true;

						JDTORecord equipJr = dao
								.getEquipInfoWithEquipGp(sStackColGp);

						String sEquipStat = "";
						String sWaterInOutGp = "";
						String sStartEndGp = "";

						if (equipJr != null) {
							sEquipStat = StringHelper.evl(equipJr
									.getFieldString("EQUIP_STAT"), "");
							sWaterInOutGp = StringHelper.evl(equipJr
									.getFieldString("WATERIN_WATEROUT_GP"), "");
							sStartEndGp = StringHelper.evl(equipJr
									.getFieldString("START_END_GP"), "");
						}

						if (YmCommonConst.WORK_MODE_C.equals(sEquipStat)) {
							isVal = false;
							logger.println(LogLevel.DEBUG, this,
									"=B열연 수조탱크설비 상태 고장중 =====");
						}

						if ("2".equals(sWaterInOutGp)
								&& "E".equals(sStartEndGp)) {
						} else {
							isVal = false;
							logger.println(LogLevel.DEBUG, this,
									"=B열연 수조탱크설비 상태 배수종료 상태 아님 =====");
						}

						/*
						 * JDTORecord equipJr =
						 * dao.getToWtState(sStackColGp.substring(2,6));
						 * 
						 * String sSReady = ""; String sEReady = "";
						 * 
						 * if(equipJr != null){ sSReady =
						 * StringHelper.evl(equipJr.getFieldString("START_DDTT"),
						 * ""); sEReady =
						 * StringHelper.evl(equipJr.getFieldString("RECOVER_DDTT"),
						 * ""); }
						 * 
						 * if(!"".equals(sSReady)){ if("".equals(sEReady)){
						 * isVal = false;
						 * logger.println(LogLevel.DEBUG,this,"=B열연 수조탱크설비 상태
						 * 휴지중 ====="); } }
						 */
					} else if (YmCommonConst.STACK_COL_USAGE_CD_CX
							.equals(sUpUsageCd)) {
						/*
						 * 2.1 B열연 대차인 경우 CHECK
						 */
						JDTORecord equipJr = dao.getToEquipState(sStackColGp);

						String sStackStat = StringHelper.evl(equipJr
								.getFieldString("STACK_STAT"), ""); // 적재상태
						String sAssignYn = StringHelper.evl(equipJr
								.getFieldString("CARLOAD_ASSIGN_YN"), ""); // 상차스케쥴지정여부
						String sLoadSchCode = StringHelper.evl(equipJr
								.getFieldString("CARLOAD_SCH_WORK_KIND"), ""); // 상차스케쥴
						String sCurrStopLoc = StringHelper.evl(equipJr
								.getFieldString("CURR_STOP_LOC"), ""); // 현재동
						String sCarLoadLoc = StringHelper.evl(equipJr
								.getFieldString("CARLOAD_STOP_LOC"), ""); // 상차동

						logger.println(LogLevel.DEBUG, this,"=B열연 동간작업기준조회 상차스케줄 코드 ▶▶▶▶▶:"+sLoadSchCode+"작업스케줄"+sSchWorkKind);
						
						//2010.07.08 SPM 추출,HFL 추출 로 대차지정이 되어 있는 경우에만 대차위치로 to위치를 결정 한다.
						//if(sLoadSchCode.equals("CKLO") || sLoadSchCode.equals("CFLO") ){
						/*
						 * 3. B열연 대차인 경우는 적재상태 체크 'L'인 경우에 To위치 선정 가능한다.
						 */
						if ("L".equals(sStackStat)) {

							if ("Y".equals(sAssignYn)) {
								/*
								 * 4. B열연 대차이고 상차스케쥴 지정이면 저장품 스케쥴코드와 상차스케쥴 코드를 체크 같으면 To 위치 True
								 */
								// 최규성.  2010-01-21
								// 직상차 때문에 추출관련 조건 추가.
								// 직상차 사용여부 판별 조건 추가.
//								String sAutoYn = StringHelper.evl(equipJr.getFieldString("AUTO_YN"),"N");
								if (  
										sSchWorkKind.equals(sLoadSchCode)  
//									   ||(sSchWorkKind.substring(2,4).equals("LO") && sAutoYn.equals("Y") )	//추출코드, 
								   ) {
									
									isVal = true;
									logger.println(LogLevel.DEBUG, this,"=B열연 대차설비 적재상태 'L' =====");
									logger.println(LogLevel.DEBUG, this,"=B열연 대차설비 상차스케쥴 지정=====");
									logger.println(LogLevel.DEBUG, this,"=B열연 대차설비 저장품 스케쥴코드=상차스케쥴코드 동일");
									logger.println(LogLevel.DEBUG, this,"=B열연 대차설비 설비상태 To위치가능=====");
								} else {
									logger.println(LogLevel.DEBUG, this,"=B열연 대차설비 적재상태 'L' =====");
									logger.println(LogLevel.DEBUG, this,"=B열연 대차설비 상차스케쥴 지정=====");
									logger.println(LogLevel.DEBUG, this,"=B열연 대차설비 저장품 스케쥴코드<>상차스케쥴코드="+ sLoadSchCode);
									logger.println(LogLevel.DEBUG, this,"=B열연 대차설비 설비상태 To위치불가능=====");
								}
							} else {
								/*
								 * 5. B열연 대차이고 스케쥴 미지정이면 현재 대차에 실려있는 코일이 있는지 체크 코일이 존재하지 않으면 To위치 true
								 */
								JDTORecord tcJr = dao.getTCLoadCount(sStackColGp);
								int iCnt = tcJr.getFieldInt("CNT"); // 대차에 실려있는
																	// 코일 갯수

								if (iCnt == 0) {

									isVal = true;
									logger.println(LogLevel.DEBUG, this,"=B열연 대차설비 적재상태 'L' =====");
									logger.println(LogLevel.DEBUG, this,"=B열연 대차설비 상차스케쥴 미지정=====");
									logger.println(LogLevel.DEBUG, this,"=B열연 대차설비 현재 대차에 코일이 존재하지 않음");
									logger.println(LogLevel.DEBUG, this,"=B열연 대차설비 설비상태 To위치가능=====");
								} else {
									logger.println(LogLevel.DEBUG, this,"=B열연 대차설비 적재상태 'L' =====");
									logger.println(LogLevel.DEBUG, this,"=B열연 대차설비 상차스케쥴 미지정=====");
									logger.println(LogLevel.DEBUG, this,"=B열연 대차설비 현재 대차에 코일이 존재=" + iCnt);
									logger.println(LogLevel.DEBUG, this,"=B열연 대차설비 설비상태 To위치불가능=====");
								}
							}
						} else {
							if (sStackColGp.equals(sCarLoadLoc)) {

								isVal = true;
								logger.println(LogLevel.DEBUG, this,"=B열연 대차설비 적재상태 =" + sStackStat);
								logger.println(LogLevel.DEBUG, this,"=B열연 대차 상차후 하차출발시 상차동 선작업등록 처리=====");
								logger.println(LogLevel.DEBUG, this,"=B열연 대차설비 설비상태 To위치가능=====");
							} else {

								logger.println(LogLevel.DEBUG, this,"=B열연 대차설비 적재상태 =" + sStackStat);
								logger.println(LogLevel.DEBUG, this,"=B열연 대차설비 설비상태 To위치불가능=====");
							}
						}
					//}
					} else {
						isVal = true;
						logger.println(LogLevel.DEBUG, this,"=B열연 대차나 수조탱크가 아니면 To위치가능=====");
					}
				}
			}
			logger.println(LogLevel.DEBUG, this, sStackColGp+ "=적치번지 TO위치 설비상태 체크 결과 =" + isVal);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return isVal;
	}

	/**
	 * 1. 적치열,BED,단 정보를 가지고 적치가능한 위치인지를 체크한다. TO적치단이 '02'단일 경우 To적치단의 하단 앞,뒤
	 * 적치단에 저장품ID 정보를 가져온다. 저장품ID가 존재하고, 하단 적치단상태가 'L','P' 인 경우 TO적치단 정보는 To위치로
	 * 적용 가능한다.
	 * 
	 * TO적치단이 '01'단일 경우는 조건없이 To위치로 적용 가능하다.
	 * 
	 * @param DAO
	 * @param JDTORecord
	 * 
	 * @return boolean 적치가능
	 * @throws Exception
	 */
	private boolean getCoilToLocInfo_004(JDTORecord stackV) throws Exception {
		boolean isCan = false;
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String sToColGp = StringHelper.evl(stackV
					.getFieldString("TO_STACK_COL_GP"), ""); // 적치열구분
			String sToBedGp = StringHelper.evl(stackV
					.getFieldString("TO_STACK_BED_GP"), ""); // 적치BED구분
			String sToLayerGp = StringHelper.evl(stackV
					.getFieldString("TO_STACK_LAYER_GP"), ""); // 적치단구분
			logger.println(LogLevel.DEBUG, this,"getCoilToLocInfo_004>>"+ sToColGp + "=" + sToBedGp
					+ "=" + sToLayerGp + "=적치번지 TO위치 가능 체크=====");

			if (YmCommonConst.STACK_LAYER_GP_01.equals(sToLayerGp)) {

				isCan = true;
				logger.println(LogLevel.DEBUG, this, "getCoilToLocInfo_004>>"+ "=1단 적치가능=====");

			} else if (YmCommonConst.STACK_LAYER_GP_02.equals(sToLayerGp)) {

				if (true) {
					String sLLayerStat = StringHelper.evl(stackV
							.getFieldString("LEFT_STACK_LAYER_STAT"), "");
					String sLMoveTerm = StringHelper.evl(stackV
							.getFieldString("LEFT_STOCK_MOVE_TERM"), "");
					String sLStockId = StringHelper.evl(stackV
							.getFieldString("LEFT_STOCK_ID"), "");

					// 적치단상태가 'L','P' 이면 상단 적치단 정보를 To위치 적용 가능상태
					if (YmCommonConst.STACK_LAYER_STAT_L.equals(sLLayerStat)
						|| YmCommonConst.STACK_LAYER_STAT_P.equals(sLLayerStat)) {
						isCan = getCoilToLocInfo_005(sLMoveTerm, sLStockId);
						logger.println(LogLevel.DEBUG, this,"=2단 하단 저장품존재 적치가능여부=" + isCan);
					}
				}

				if (isCan) {
					String sRLayerStat = StringHelper.evl(stackV.getFieldString("RIGHT_STACK_LAYER_STAT"), "");
					String sRMoveTerm  = StringHelper.evl(stackV.getFieldString("RIGHT_STOCK_MOVE_TERM"), "");
					String sRStockId   = StringHelper.evl(stackV.getFieldString("RIGHT_STOCK_ID"), "");

					// 적치단상태가 'L','P' 이면 상단 적치단 정보를 To위치 적용 가능상태
					if (YmCommonConst.STACK_LAYER_STAT_L.equals(sRLayerStat)
							|| YmCommonConst.STACK_LAYER_STAT_P.equals(sRLayerStat)) {
						isCan = getCoilToLocInfo_005(sRMoveTerm, sRStockId);
						logger.println(LogLevel.DEBUG, this,"=2단+1 하단 저장품존재 적치가능여부=" + isCan);
					}
				}
			}
			logger.println(LogLevel.DEBUG, this, "getCoilToLocInfo_004>>"+ "적치번지 TO위치 가능 체크 결과 ="+ isCan);

		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return isCan;
	}

	/**
	 * 1. 01단에 적치된 코일의 저장품 이동조건을 검색한다. - 특정 저장품이동조건에 대해서 상단에 코일을 적치하지 않는다.
	 * 
	 * @param DAO
	 * @param JDTORecord
	 * 
	 * @return int 적합성 우선순위
	 * @throws Exception
	 */
	private boolean getCoilToLocInfo_005(String sStockMoveTerm, String sStockId)
			throws Exception {
		boolean isCan = true;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			logger.println(LogLevel.DEBUG, this, "getCoilToLocInfo_005>>적치번지 TO위치 하단 저장품 작업예약 체크 시작="+ sStockMoveTerm);
			if (YmCommonConst.NEW_STOCK_MOVE_TERM_CC.equals(sStockMoveTerm)  //  정정작업대기																				
				|| YmCommonConst.NEW_STOCK_MOVE_TERM_CS.equals(sStockMoveTerm)// Coil 이송대기
				|| YmCommonConst.NEW_STOCK_MOVE_TERM_LG.equals(sStockMoveTerm)) { // 출하작업대기

				/**
				 * 작업예약이 존재하면 상단에 TO 위치 FALSE
				 */
				JDTORecord stockJr = dao.getStockInfo(sStockId);
				if (stockJr != null) {
					String sWbookId = StringHelper.evl(stockJr.getFieldString("WBOOK_ID"), "");
					if (!"".equals(sWbookId)) {
						isCan = false;
						logger.println(LogLevel.DEBUG, this, "getCoilToLocInfo_005>>WBOOK_ID="+ sWbookId);
					}
				}
			}
			logger.println(LogLevel.DEBUG, this, "getCoilToLocInfo_005>>적치번지 TO위치 하단 저장품 작업예약 체크 종료=>>작업예약이 존재 함"+ isCan);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return isCan;
	}

	/**
	 * 각 TO위치 정보의 적치기준 정보를 대상으로 적합성 여부를 체크한다.
	 * 
	 * @param JDTORecord :
	 *            TO위치정보
	 * @param JDTORecord :
	 *            기준정보
	 * @param String :
	 *            진도코드
	 * @param String :
	 *            적치단정보
	 * 
	 * @return int 종합점수
	 * @throws Exception
	 */
	private int getCoilToLocInfo_006(JDTORecord stackV, JDTORecord ruleV,String sProgCd, String sToLayerGp) throws Exception {
		int iTotalN = 0;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			logger.println(LogLevel.DEBUG, this, "getCoilToLocInfo_006>>적치번지 TO위치 적합성 체크====="+sProgCd);

			if (YmCommonConst.CURR_PROG_CD_COIL_1.equals(sProgCd)) {
				if (YmCommonConst.STACK_LAYER_GP_01.equals(sToLayerGp)) {
					iTotalN = getCoilToLocInfo_006_1_01(stackV, ruleV);
				} else {
					iTotalN = getCoilToLocInfo_006_1_02(stackV, ruleV);
				}
			} else if (YmCommonConst.CURR_PROG_CD_COIL_3.equals(sProgCd)) {
				if (YmCommonConst.STACK_LAYER_GP_01.equals(sToLayerGp)) {
					iTotalN = getCoilToLocInfo_006_3_01(stackV, ruleV);
				} else {
					iTotalN = getCoilToLocInfo_006_3_02(stackV, ruleV);
				}
			} else if (YmCommonConst.CURR_PROG_CD_COIL_A.equals(sProgCd)) {
				if (YmCommonConst.STACK_LAYER_GP_01.equals(sToLayerGp)) {
					iTotalN = getCoilToLocInfo_006_A_01(stackV, ruleV);
				} else {
					iTotalN = getCoilToLocInfo_006_A_02(stackV, ruleV);
				}
			} else if (YmCommonConst.CURR_PROG_CD_COIL_B.equals(sProgCd)) {
				if (YmCommonConst.STACK_LAYER_GP_01.equals(sToLayerGp)) {
					iTotalN = getCoilToLocInfo_006_B_01(stackV, ruleV);
				} else {
					iTotalN = getCoilToLocInfo_006_B_02(stackV, ruleV);
				}
			} else if (YmCommonConst.CURR_PROG_CD_COIL_C.equals(sProgCd)) {
				if (YmCommonConst.STACK_LAYER_GP_01.equals(sToLayerGp)) {
					iTotalN = getCoilToLocInfo_006_C_01(stackV, ruleV);
				} else {
					iTotalN = getCoilToLocInfo_006_C_02(stackV, ruleV);
				}
			} else if (YmCommonConst.CURR_PROG_CD_COIL_D.equals(sProgCd)) {
				if (YmCommonConst.STACK_LAYER_GP_01.equals(sToLayerGp)) {
					iTotalN = getCoilToLocInfo_006_D_01(stackV, ruleV);
				} else {
					iTotalN = getCoilToLocInfo_006_D_02(stackV, ruleV);
				}
			} else if (YmCommonConst.CURR_PROG_CD_COIL_E.equals(sProgCd)) {
				if (YmCommonConst.STACK_LAYER_GP_01.equals(sToLayerGp)) {
					iTotalN = getCoilToLocInfo_006_E_01(stackV, ruleV);
				} else {
					iTotalN = getCoilToLocInfo_006_E_02(stackV, ruleV);
				}
			} else if (YmCommonConst.CURR_PROG_CD_COIL_F.equals(sProgCd)) {
				if (YmCommonConst.STACK_LAYER_GP_01.equals(sToLayerGp)) {
					iTotalN = getCoilToLocInfo_006_F_01(stackV, ruleV);
				} else {
					iTotalN = getCoilToLocInfo_006_F_02(stackV, ruleV);
				}
			} else if (YmCommonConst.CURR_PROG_CD_COIL_G.equals(sProgCd)) {
				if (YmCommonConst.STACK_LAYER_GP_01.equals(sToLayerGp)) {
					iTotalN = getCoilToLocInfo_006_G_01(stackV, ruleV);
				} else {
					iTotalN = getCoilToLocInfo_006_G_02(stackV, ruleV);
				}
			} else if (YmCommonConst.CURR_PROG_CD_COIL_H.equals(sProgCd)) {
				if (YmCommonConst.STACK_LAYER_GP_01.equals(sToLayerGp)) {
					iTotalN = getCoilToLocInfo_006_H_01(stackV, ruleV);
				} else {
					iTotalN = getCoilToLocInfo_006_H_02(stackV, ruleV);
				}
			} else if (YmCommonConst.CURR_PROG_CD_COIL_J.equals(sProgCd)) {
				if (YmCommonConst.STACK_LAYER_GP_01.equals(sToLayerGp)) {
					iTotalN = getCoilToLocInfo_006_J_01(stackV, ruleV);
				} else {
					iTotalN = getCoilToLocInfo_006_J_02(stackV, ruleV);
				}
			} else if (YmCommonConst.CURR_PROG_CD_COIL_K.equals(sProgCd)) {
				if (YmCommonConst.STACK_LAYER_GP_01.equals(sToLayerGp)) {
					iTotalN = getCoilToLocInfo_006_K_01(stackV, ruleV);
				} else {
					iTotalN = getCoilToLocInfo_006_K_02(stackV, ruleV);
				}
			} else if (YmCommonConst.CURR_PROG_CD_COIL_L.equals(sProgCd)) {
				if (YmCommonConst.STACK_LAYER_GP_01.equals(sToLayerGp)) {
					iTotalN = getCoilToLocInfo_006_L_01(stackV, ruleV);
				} else {
					iTotalN = getCoilToLocInfo_006_L_02(stackV, ruleV);
				}
			} else if (YmCommonConst.CURR_PROG_CD_COIL_M.equals(sProgCd)||
					    YmCommonConst.CURR_PROG_CD_COIL_P.equals(sProgCd)) {
				if (YmCommonConst.STACK_LAYER_GP_01.equals(sToLayerGp)) {
					iTotalN = getCoilToLocInfo_006_M_01(stackV, ruleV);
				} else {
					iTotalN = getCoilToLocInfo_006_M_02(stackV, ruleV);
				}
			} else if (YmCommonConst.CURR_PROG_CD_COIL_X.equals(sProgCd)) {
				if (YmCommonConst.STACK_LAYER_GP_01.equals(sToLayerGp)) {
					iTotalN = getCoilToLocInfo_006_X_01(stackV, ruleV);
				} else {
					iTotalN = getCoilToLocInfo_006_X_02(stackV, ruleV);
				}
			} else if (YmCommonConst.CURR_PROG_CD_COIL_Y.equals(sProgCd)) {
				if (YmCommonConst.STACK_LAYER_GP_01.equals(sToLayerGp)) {
					iTotalN = getCoilToLocInfo_006_Y_01(stackV, ruleV);
				} else {
					iTotalN = getCoilToLocInfo_006_Y_02(stackV, ruleV);
				}
			} else if (YmCommonConst.CURR_PROG_CD_COIL_Z.equals(sProgCd)) {
				if (YmCommonConst.STACK_LAYER_GP_01.equals(sToLayerGp)) {
					iTotalN = getCoilToLocInfo_006_Z_01(stackV, ruleV);
				} else {
					iTotalN = getCoilToLocInfo_006_Z_02(stackV, ruleV);
				}
			}

			logger.println(LogLevel.DEBUG, this,"getCoilToLocInfo_006>>적치번지 TO위치 적합성 체크 결과 우선순위=====" + iTotalN);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return iTotalN;
	}

	/**
	 * 각 진도코드별 기준점수를 리턴한다.
	 * 
	 * @param JDTORecord :
	 *            기준정보
	 * @param String :
	 *            진도코드
	 * @param String :
	 *            적치단정보
	 * 
	 * @return int 각 진도코드별 기준점수
	 * @throws Exception
	 */
	private int getCoilToLocFmInfo(JDTORecord ruleV, String sProgCd,
			String sToLayerGp) throws Exception {
		int iFmN = 0;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			String sParam = "MX" + Integer.parseInt(sToLayerGp) + sProgCd;

			String sNum = StringHelper.evl(ruleV.getFieldString(sParam), "0"); // 진도코드

			iFmN = Integer.parseInt(sNum);

			logger.println(LogLevel.DEBUG, this, "적치번지 TO위치 적합성 기준점수====="
					+ iFmN+"-"+sParam);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return iFmN;
	}

	/**
	 * 생산예정 01단 TO위치 적합성 체크
	 * 
	 * @param JDTORecord :
	 *            TO위치정보
	 * @param JDTORecord :
	 *            기준정보
	 * 
	 * @return int 종합점수
	 * @throws Exception
	 */
	private int getCoilToLocInfo_006_1_01(JDTORecord stackV, JDTORecord ruleV)
			throws Exception {
		int iTotalN = 0;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			logger.println(LogLevel.DEBUG, this, "생산예정 01단 TO위치 적합성 체크=====");

			int iLPROG_CD = ruleV.getFieldInt("LPROG_CD");
			int iLNEXT_PROC = ruleV.getFieldInt("LNEXT_PROC");
			int iLSTAT = ruleV.getFieldInt("LSTAT");

			int iRPROG_CD = ruleV.getFieldInt("RPROG_CD");
			int iRNEXT_PROC = ruleV.getFieldInt("RNEXT_PROC");
			int iRSTAT = ruleV.getFieldInt("RSTAT");

			String sProgCd = StringHelper.evl(stackV
					.getFieldString("COIL_PROG_CD"), ""); // 진도코드
			String sNextPc = StringHelper.evl(stackV
					.getFieldString("COIL_NEXT_PROC"), ""); // 차공정

			String sLProgCd = StringHelper.evl(stackV
					.getFieldString("LCOIL_PROG_CD"), ""); // 진도코드
			String sLNextPc = StringHelper.evl(stackV
					.getFieldString("LCOIL_NEXT_PROC"), ""); // 차공정

			String sRProgCd = StringHelper.evl(stackV
					.getFieldString("RCOIL_PROG_CD"), ""); // 진도코드
			String sRNextPc = StringHelper.evl(stackV
					.getFieldString("RCOIL_NEXT_PROC"), ""); // 차공정

			String sLLayerStat = StringHelper.evl(stackV
					.getFieldString("LEFT_STACK_LAYER_STAT"), "");
			String sRLayerStat = StringHelper.evl(stackV
					.getFieldString("RIGHT_STACK_LAYER_STAT"), "");

			logger.println(LogLevel.DEBUG, this, "=코일 진도코드	=" + sProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 차공정	=" + sNextPc);
			logger.println(LogLevel.DEBUG, this, "=코일 좌진도코드=" + sLProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 좌차공정	=" + sLNextPc);
			logger.println(LogLevel.DEBUG, this, "=코일 우진도코드=" + sRProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 우차공정	=" + sRNextPc);
			logger.println(LogLevel.DEBUG, this, "=코일 좌적치상태=" + sLLayerStat);
			logger.println(LogLevel.DEBUG, this, "=코일 우적치상태=" + sRLayerStat);

			int iEmpty = Integer.parseInt(StringHelper.evl(ruleV
					.getFieldString("MXE" + sProgCd), "0"));

			if ("".equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_E.equals(sLLayerStat)) {
				iTotalN += iEmpty;
			} else {
				if (sProgCd.equals(sLProgCd)) {
					iTotalN += iLPROG_CD;
				}
				if (sNextPc.equals(sLNextPc)) {
					iTotalN += iLNEXT_PROC;
				}
			}

			if ("".equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_E.equals(sRLayerStat)) {
				iTotalN += iEmpty;
			} else {

				if (sProgCd.equals(sRProgCd)) {
					iTotalN += iRPROG_CD;
				}
				if (sNextPc.equals(sRNextPc)) {
					iTotalN += iRNEXT_PROC;
				}
			}
			if (YmCommonConst.STACK_LAYER_STAT_L.equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_P.equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_X.equals(sLLayerStat)) {
				iTotalN += iLSTAT;
			}
			if (YmCommonConst.STACK_LAYER_STAT_L.equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_P.equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_X.equals(sRLayerStat)) {
				iTotalN += iRSTAT;
			}
			logger.println(LogLevel.DEBUG, this,
					"생산예정 01단 TO위치 적합성 체크 결과 종합점수=====" + iTotalN);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return iTotalN;
	}

	/**
	 * 생산예정 02단 TO위치 적합성 체크
	 * 
	 * @param JDTORecord :
	 *            TO위치정보
	 * @param JDTORecord :
	 *            기준정보
	 * 
	 * @return int 종합점수
	 * @throws Exception
	 */
	private int getCoilToLocInfo_006_1_02(JDTORecord stackV, JDTORecord ruleV)
			throws Exception {
		int iTotalN = 0;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			logger.println(LogLevel.DEBUG, this, "생산예정 02단 TO위치 적합성 체크=====");

			int iUPROG_CD = ruleV.getFieldInt("UPROG_CD");
			int iUNEXT_PROC = ruleV.getFieldInt("UNEXT_PROC");

			String sProgCd = StringHelper.evl(stackV
					.getFieldString("COIL_PROG_CD"), ""); // 진도코드
			String sNextPc = StringHelper.evl(stackV
					.getFieldString("COIL_NEXT_PROC"), ""); // 차공정

			String sLProgCd = StringHelper.evl(stackV
					.getFieldString("LCOIL_PROG_CD"), ""); // 진도코드
			String sLNextPc = StringHelper.evl(stackV
					.getFieldString("LCOIL_NEXT_PROC"), ""); // 차공정

			String sRProgCd = StringHelper.evl(stackV
					.getFieldString("RCOIL_PROG_CD"), ""); // 진도코드
			String sRNextPc = StringHelper.evl(stackV
					.getFieldString("RCOIL_NEXT_PROC"), ""); // 차공정

			logger.println(LogLevel.DEBUG, this, "=코일 진도코드	=" + sProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 차공정	=" + sNextPc);
			logger.println(LogLevel.DEBUG, this, "=코일 좌진도코드=" + sLProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 좌차공정	=" + sLNextPc);
			logger.println(LogLevel.DEBUG, this, "=코일 우진도코드=" + sRProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 우차공정	=" + sRNextPc);

			if (sProgCd.equals(sLProgCd)) {
				iTotalN += iUPROG_CD;
			}
			if (sNextPc.equals(sLNextPc)) {
				iTotalN += iUNEXT_PROC;
			}
			if (sProgCd.equals(sRProgCd)) {
				iTotalN += iUPROG_CD;
			}
			if (sNextPc.equals(sRNextPc)) {
				iTotalN += iUNEXT_PROC;
			}

			logger.println(LogLevel.DEBUG, this,
					"생산예정 02단 TO위치 적합성 체크 결과 종합점수=====" + iTotalN);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return iTotalN;
	}

	/**
	 * 생산종료 01단 TO위치 적합성 체크
	 * 
	 * @param JDTORecord :
	 *            TO위치정보
	 * @param JDTORecord :
	 *            기준정보
	 * 
	 * @return int 종합점수
	 * @throws Exception
	 */
	private int getCoilToLocInfo_006_3_01(JDTORecord stackV, JDTORecord ruleV)
			throws Exception {
		int iTotalN = 0;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			logger.println(LogLevel.DEBUG, this, "생산종료 01단 TO위치 적합성 체크=====");

			iTotalN = 999;

			logger.println(LogLevel.DEBUG, this,
					"생산종료 01단 TO위치 적합성 체크 결과 종합점수=====" + iTotalN);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return iTotalN;
	}

	/**
	 * 생산종료 02단 TO위치 적합성 체크
	 * 
	 * @param JDTORecord :
	 *            TO위치정보
	 * @param JDTORecord :
	 *            기준정보
	 * 
	 * @return int 종합점수
	 * @throws Exception
	 */
	private int getCoilToLocInfo_006_3_02(JDTORecord stackV, JDTORecord ruleV)
			throws Exception {
		int iTotalN = 0;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			logger.println(LogLevel.DEBUG, this, "생산종료 02단 TO위치 적합성 체크=====");

			iTotalN = 999;

			logger.println(LogLevel.DEBUG, this,
					"생산종료 02단 TO위치 적합성 체크 결과 종합점수=====" + iTotalN);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return iTotalN;
	}

	/**
	 * 재질판정대기 01단 TO위치 적합성 체크
	 * 
	 * @param JDTORecord :
	 *            TO위치정보
	 * @param JDTORecord :
	 *            기준정보
	 * 
	 * @return int 종합점수
	 * @throws Exception
	 */
	private int getCoilToLocInfo_006_A_01(JDTORecord stackV, JDTORecord ruleV)
			throws Exception {
		int iTotalN = 0;

		try {
			
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			logger.println(LogLevel.DEBUG, this, "재질판정대기 01단 TO위치 적합성 체크=====");

			int iLPROG_CD = ruleV.getFieldInt("LPROG_CD");
			int iLNEXT_PROC = ruleV.getFieldInt("LNEXT_PROC");
			int iLSPEC = ruleV.getFieldInt("LSPEC");
			int iLORD_NO = ruleV.getFieldInt("LORD_NO");
			int iLORD_DTL = ruleV.getFieldInt("LORD_DTL");
			int iLMILL_DT = ruleV.getFieldInt("LMILL_DT");
			int iLSTAT = ruleV.getFieldInt("LSTAT");

			int iRPROG_CD = ruleV.getFieldInt("RPROG_CD");
			int iRNEXT_PROC = ruleV.getFieldInt("RNEXT_PROC");
			int iRSPEC = ruleV.getFieldInt("RSPEC");
			int iRORD_NO = ruleV.getFieldInt("RORD_NO");
			int iRORD_DTL = ruleV.getFieldInt("RORD_DTL");
			int iRMILL_DT = ruleV.getFieldInt("RMILL_DT");
			int iRSTAT = ruleV.getFieldInt("RSTAT");

			String sProgCd = StringHelper.evl(stackV
					.getFieldString("COIL_PROG_CD"), ""); // 진도코드
			String sNextPc = StringHelper.evl(stackV
					.getFieldString("COIL_NEXT_PROC"), ""); // 차공정
			String sSpec = StringHelper.evl(stackV
					.getFieldString("COIL_SPEC_ABBSYM"), ""); // 규격약호
			String sOrdNo = StringHelper.evl(stackV
					.getFieldString("COIL_ORD_NO"), ""); // 주문번호
			String sOrdDtl = StringHelper.evl(stackV
					.getFieldString("COIL_ORD_DTL"), ""); // 주문행번
			String sMillDt = StringHelper.evl(stackV
					.getFieldString("COIL_MILL_DT"), ""); // 압연일시

			String sLProgCd = StringHelper.evl(stackV
					.getFieldString("LCOIL_PROG_CD"), ""); // 진도코드
			String sLNextPc = StringHelper.evl(stackV
					.getFieldString("LCOIL_NEXT_PROC"), ""); // 차공정
			String sLSpec = StringHelper.evl(stackV
					.getFieldString("LCOIL_SPEC_ABBSYM"), ""); // 규격약호
			String sLOrdNo = StringHelper.evl(stackV
					.getFieldString("LCOIL_ORD_NO"), ""); // 주문번호
			String sLOrdDtl = StringHelper.evl(stackV
					.getFieldString("LCOIL_ORD_DTL"), ""); // 주문행번
			String sLMillDt = StringHelper.evl(stackV
					.getFieldString("LCOIL_MILL_DT"), ""); // 압연일시

			String sRProgCd = StringHelper.evl(stackV
					.getFieldString("RCOIL_PROG_CD"), ""); // 진도코드
			String sRNextPc = StringHelper.evl(stackV
					.getFieldString("RCOIL_NEXT_PROC"), ""); // 차공정
			String sRSpec = StringHelper.evl(stackV
					.getFieldString("RCOIL_SPEC_ABBSYM"), ""); // 규격약호
			String sROrdNo = StringHelper.evl(stackV
					.getFieldString("RCOIL_ORD_NO"), ""); // 주문번호
			String sROrdDtl = StringHelper.evl(stackV
					.getFieldString("RCOIL_ORD_DTL"), ""); // 주문행번
			String sRMillDt = StringHelper.evl(stackV
					.getFieldString("RCOIL_MILL_DT"), ""); // 압연일시

			String sLLayerStat = StringHelper.evl(stackV
					.getFieldString("LEFT_STACK_LAYER_STAT"), "");
			String sRLayerStat = StringHelper.evl(stackV
					.getFieldString("RIGHT_STACK_LAYER_STAT"), "");

			logger.println(LogLevel.DEBUG, this, "=코일 진도코드	=" + sProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 차공정	=" + sNextPc);
			logger.println(LogLevel.DEBUG, this, "=코일 규격약호	=" + sSpec);
			logger.println(LogLevel.DEBUG, this, "=코일 주문번호	=" + sOrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 주문행번	=" + sOrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 압연일시	=" + sMillDt);

			logger.println(LogLevel.DEBUG, this, "=코일 좌진도코드=" + sLProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 좌차공정	=" + sLNextPc);
			logger.println(LogLevel.DEBUG, this, "=코일 좌규격약호=" + sLSpec);
			logger.println(LogLevel.DEBUG, this, "=코일 좌주문번호=" + sLOrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 좌주문행번=" + sLOrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 좌압연일시=" + sLMillDt);

			logger.println(LogLevel.DEBUG, this, "=코일 우진도코드=" + sRProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 우차공정	=" + sRNextPc);
			logger.println(LogLevel.DEBUG, this, "=코일 우규격약호=" + sRSpec);
			logger.println(LogLevel.DEBUG, this, "=코일 우주문번호=" + sROrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 우주문행번=" + sROrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 우압연일시=" + sRMillDt);

			logger.println(LogLevel.DEBUG, this, "=코일 좌적치상태=" + sLLayerStat);
			logger.println(LogLevel.DEBUG, this, "=코일 우적치상태=" + sRLayerStat);

			int iEmpty = Integer.parseInt(StringHelper.evl(ruleV
					.getFieldString("MXE" + sProgCd), "0"));

			if ("".equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_E.equals(sLLayerStat)) {
				iTotalN += iEmpty;
			} else {

				if (sProgCd.equals(sLProgCd)) {
					iTotalN += iLPROG_CD;
				}
				if (sNextPc.equals(sLNextPc)) {
					iTotalN += iLNEXT_PROC;
				}
				if (sSpec.equals(sLSpec)) {
					iTotalN += iLSPEC;
				}
				if (sOrdNo.equals(sLOrdNo)) {
					iTotalN += iLORD_NO;
				}
				if (sOrdDtl.equals(sLOrdDtl)) {
					iTotalN += iLORD_DTL;
				}
				if (sMillDt.equals(sLMillDt)) {
					iTotalN += iLMILL_DT;
				}
			}

			if ("".equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_E.equals(sRLayerStat)) {
				iTotalN += iEmpty;
			} else {

				if (sProgCd.equals(sRProgCd)) {
					iTotalN += iRPROG_CD;
				}
				if (sNextPc.equals(sRNextPc)) {
					iTotalN += iRNEXT_PROC;
				}
				if (sSpec.equals(sRSpec)) {
					iTotalN += iRSPEC;
				}
				if (sOrdNo.equals(sROrdNo)) {
					iTotalN += iRORD_NO;
				}
				if (sOrdDtl.equals(sROrdDtl)) {
					iTotalN += iRORD_DTL;
				}
				if (sMillDt.equals(sRMillDt)) {
					iTotalN += iRMILL_DT;
				}
			}

			if (YmCommonConst.STACK_LAYER_STAT_L.equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_P.equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_X.equals(sLLayerStat)) {
				iTotalN += iLSTAT;
			}
			if (YmCommonConst.STACK_LAYER_STAT_L.equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_P.equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_X.equals(sRLayerStat)) {
				iTotalN += iRSTAT;
			}
			logger.println(LogLevel.DEBUG, this,
					"재질판정대기 01단 TO위치 적합성 체크 결과 종합점수=====" + iTotalN);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return iTotalN;
	}

	/**
	 * 재질판정대기 02단 TO위치 적합성 체크
	 * 
	 * @param JDTORecord :
	 *            TO위치정보
	 * @param JDTORecord :
	 *            기준정보
	 * 
	 * @return int 종합점수
	 * @throws Exception
	 */
	private int getCoilToLocInfo_006_A_02(JDTORecord stackV, JDTORecord ruleV)
			throws Exception {
		int iTotalN = 0;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			logger.println(LogLevel.DEBUG, this, "재질판정대기 02단 TO위치 적합성 체크=====");

			int iUPROG_CD = ruleV.getFieldInt("UPROG_CD");
			int iUNEXT_PROC = ruleV.getFieldInt("UNEXT_PROC");
			int iUSPEC = ruleV.getFieldInt("USPEC");
			int iUORD_NO = ruleV.getFieldInt("UORD_NO");
			int iUORD_DTL = ruleV.getFieldInt("UORD_DTL");
			int iUMILL_DT = ruleV.getFieldInt("UMILL_DT");

			String sProgCd = StringHelper.evl(stackV
					.getFieldString("COIL_PROG_CD"), ""); // 진도코드
			String sNextPc = StringHelper.evl(stackV
					.getFieldString("COIL_NEXT_PROC"), ""); // 차공정
			String sSpec = StringHelper.evl(stackV
					.getFieldString("COIL_SPEC_ABBSYM"), ""); // 규격약호
			String sOrdNo = StringHelper.evl(stackV
					.getFieldString("COIL_ORD_NO"), ""); // 주문번호
			String sOrdDtl = StringHelper.evl(stackV
					.getFieldString("COIL_ORD_DTL"), ""); // 주문행번
			String sMillDt = StringHelper.evl(stackV
					.getFieldString("COIL_MILL_DT"), ""); // 압연일시

			String sLProgCd = StringHelper.evl(stackV
					.getFieldString("LCOIL_PROG_CD"), ""); // 진도코드
			String sLNextPc = StringHelper.evl(stackV
					.getFieldString("LCOIL_NEXT_PROC"), ""); // 차공정
			String sLSpec = StringHelper.evl(stackV
					.getFieldString("LCOIL_SPEC_ABBSYM"), ""); // 규격약호
			String sLOrdNo = StringHelper.evl(stackV
					.getFieldString("LCOIL_ORD_NO"), ""); // 주문번호
			String sLOrdDtl = StringHelper.evl(stackV
					.getFieldString("LCOIL_ORD_DTL"), ""); // 주문행번
			String sLMillDt = StringHelper.evl(stackV
					.getFieldString("LCOIL_MILL_DT"), ""); // 압연일시

			String sRProgCd = StringHelper.evl(stackV
					.getFieldString("RCOIL_PROG_CD"), ""); // 진도코드
			String sRNextPc = StringHelper.evl(stackV
					.getFieldString("RCOIL_NEXT_PROC"), ""); // 차공정
			String sRSpec = StringHelper.evl(stackV
					.getFieldString("RCOIL_SPEC_ABBSYM"), ""); // 규격약호
			String sROrdNo = StringHelper.evl(stackV
					.getFieldString("RCOIL_ORD_NO"), ""); // 주문번호
			String sROrdDtl = StringHelper.evl(stackV
					.getFieldString("RCOIL_ORD_DTL"), ""); // 주문행번
			String sRMillDt = StringHelper.evl(stackV
					.getFieldString("RCOIL_MILL_DT"), ""); // 압연일시

			logger.println(LogLevel.DEBUG, this, "=코일 진도코드	=" + sProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 차공정	=" + sNextPc);
			logger.println(LogLevel.DEBUG, this, "=코일 규격약호	=" + sSpec);
			logger.println(LogLevel.DEBUG, this, "=코일 주문번호	=" + sOrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 주문행번	=" + sOrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 압연일시	=" + sMillDt);

			logger.println(LogLevel.DEBUG, this, "=코일 좌진도코드=" + sLProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 좌차공정	=" + sLNextPc);
			logger.println(LogLevel.DEBUG, this, "=코일 좌규격약호=" + sLSpec);
			logger.println(LogLevel.DEBUG, this, "=코일 좌주문번호=" + sLOrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 좌주문행번=" + sLOrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 좌압연일시=" + sLMillDt);

			logger.println(LogLevel.DEBUG, this, "=코일 우진도코드=" + sRProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 우차공정	=" + sRNextPc);
			logger.println(LogLevel.DEBUG, this, "=코일 우규격약호=" + sRSpec);
			logger.println(LogLevel.DEBUG, this, "=코일 우주문번호=" + sROrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 우주문행번=" + sROrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 우압연일시=" + sRMillDt);

			if (sProgCd.equals(sLProgCd)) {
				iTotalN += iUPROG_CD;
			}
			if (sNextPc.equals(sLNextPc)) {
				iTotalN += iUNEXT_PROC;
			}
			if (sSpec.equals(sLSpec)) {
				iTotalN += iUSPEC;
			}
			if (sOrdNo.equals(sLOrdNo)) {
				iTotalN += iUORD_NO;
			}
			if (sOrdDtl.equals(sLOrdDtl)) {
				iTotalN += iUORD_DTL;
			}
			if (sMillDt.equals(sLMillDt)) {
				iTotalN += iUMILL_DT;
			}

			if (sProgCd.equals(sRProgCd)) {
				iTotalN += iUPROG_CD;
			}
			if (sNextPc.equals(sRNextPc)) {
				iTotalN += iUNEXT_PROC;
			}
			if (sSpec.equals(sRSpec)) {
				iTotalN += iUSPEC;
			}
			if (sOrdNo.equals(sROrdNo)) {
				iTotalN += iUORD_NO;
			}
			if (sOrdDtl.equals(sROrdDtl)) {
				iTotalN += iUORD_DTL;
			}
			if (sMillDt.equals(sRMillDt)) {
				iTotalN += iUMILL_DT;
			}

			logger.println(LogLevel.DEBUG, this,
					"재질판정대기 02단 TO위치 적합성 체크 결과 종합점수=====" + iTotalN);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return iTotalN;
	}

	/**
	 * 정작업지시대기 01단 TO위치 적합성 체크
	 * 
	 * @param JDTORecord :
	 *            TO위치정보
	 * @param JDTORecord :
	 *            기준정보
	 * 
	 * @return int 종합점수
	 * @throws Exception
	 */
	private int getCoilToLocInfo_006_B_01(JDTORecord stackV, JDTORecord ruleV)
			throws Exception {
		int iTotalN = 0;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			logger.println(LogLevel.DEBUG, this,
					"정정작업지시대기 01단 TO위치 적합성 체크=====");

			int iLPROG_CD = ruleV.getFieldInt("LPROG_CD");
			int iLNEXT_PROC = ruleV.getFieldInt("LNEXT_PROC");
			int iLSPEC = ruleV.getFieldInt("LSPEC");
			int iLORD_NO = ruleV.getFieldInt("LORD_NO");
			int iLORD_DTL = ruleV.getFieldInt("LORD_DTL");
			int iLMILL_DT = ruleV.getFieldInt("LMILL_DT");
			int iLSTAT = ruleV.getFieldInt("LSTAT");

			int iRPROG_CD = ruleV.getFieldInt("RPROG_CD");
			int iRNEXT_PROC = ruleV.getFieldInt("RNEXT_PROC");
			int iRSPEC = ruleV.getFieldInt("RSPEC");
			int iRORD_NO = ruleV.getFieldInt("RORD_NO");
			int iRORD_DTL = ruleV.getFieldInt("RORD_DTL");
			int iRMILL_DT = ruleV.getFieldInt("RMILL_DT");
			int iRSTAT = ruleV.getFieldInt("RSTAT");

			String sProgCd = StringHelper.evl(stackV
					.getFieldString("COIL_PROG_CD"), ""); // 진도코드
			String sNextPc = StringHelper.evl(stackV
					.getFieldString("COIL_NEXT_PROC"), ""); // 차공정
			String sSpec = StringHelper.evl(stackV
					.getFieldString("COIL_SPEC_ABBSYM"), ""); // 규격약호
			String sOrdNo = StringHelper.evl(stackV
					.getFieldString("COIL_ORD_NO"), ""); // 주문번호
			String sOrdDtl = StringHelper.evl(stackV
					.getFieldString("COIL_ORD_DTL"), ""); // 주문행번
			String sMillDt = StringHelper.evl(stackV
					.getFieldString("COIL_MILL_DT"), ""); // 압연일시

			String sLProgCd = StringHelper.evl(stackV
					.getFieldString("LCOIL_PROG_CD"), ""); // 진도코드
			String sLNextPc = StringHelper.evl(stackV
					.getFieldString("LCOIL_NEXT_PROC"), ""); // 차공정
			String sLSpec = StringHelper.evl(stackV
					.getFieldString("LCOIL_SPEC_ABBSYM"), ""); // 규격약호
			String sLOrdNo = StringHelper.evl(stackV
					.getFieldString("LCOIL_ORD_NO"), ""); // 주문번호
			String sLOrdDtl = StringHelper.evl(stackV
					.getFieldString("LCOIL_ORD_DTL"), ""); // 주문행번
			String sLMillDt = StringHelper.evl(stackV
					.getFieldString("LCOIL_MILL_DT"), ""); // 압연일시

			String sRProgCd = StringHelper.evl(stackV
					.getFieldString("RCOIL_PROG_CD"), ""); // 진도코드
			String sRNextPc = StringHelper.evl(stackV
					.getFieldString("RCOIL_NEXT_PROC"), ""); // 차공정
			String sRSpec = StringHelper.evl(stackV
					.getFieldString("RCOIL_SPEC_ABBSYM"), ""); // 규격약호
			String sROrdNo = StringHelper.evl(stackV
					.getFieldString("RCOIL_ORD_NO"), ""); // 주문번호
			String sROrdDtl = StringHelper.evl(stackV
					.getFieldString("RCOIL_ORD_DTL"), ""); // 주문행번
			String sRMillDt = StringHelper.evl(stackV
					.getFieldString("RCOIL_MILL_DT"), ""); // 압연일시

			String sLLayerStat = StringHelper.evl(stackV
					.getFieldString("LEFT_STACK_LAYER_STAT"), "");
			String sRLayerStat = StringHelper.evl(stackV
					.getFieldString("RIGHT_STACK_LAYER_STAT"), "");

			logger.println(LogLevel.DEBUG, this, "=코일 진도코드	=" + sProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 차공정	=" + sNextPc);
			logger.println(LogLevel.DEBUG, this, "=코일 규격약호	=" + sSpec);
			logger.println(LogLevel.DEBUG, this, "=코일 주문번호	=" + sOrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 주문행번	=" + sOrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 압연일시	=" + sMillDt);

			logger.println(LogLevel.DEBUG, this, "=코일 좌진도코드=" + sLProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 좌차공정	=" + sLNextPc);
			logger.println(LogLevel.DEBUG, this, "=코일 좌규격약호=" + sLSpec);
			logger.println(LogLevel.DEBUG, this, "=코일 좌주문번호=" + sLOrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 좌주문행번=" + sLOrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 좌압연일시=" + sLMillDt);

			logger.println(LogLevel.DEBUG, this, "=코일 우진도코드=" + sRProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 우차공정	=" + sRNextPc);
			logger.println(LogLevel.DEBUG, this, "=코일 우규격약호=" + sRSpec);
			logger.println(LogLevel.DEBUG, this, "=코일 우주문번호=" + sROrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 우주문행번=" + sROrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 우압연일시=" + sRMillDt);

			logger.println(LogLevel.DEBUG, this, "=코일 좌적치상태=" + sLLayerStat);
			logger.println(LogLevel.DEBUG, this, "=코일 우적치상태=" + sRLayerStat);

			int iEmpty = Integer.parseInt(StringHelper.evl(ruleV
					.getFieldString("MXE" + sProgCd), "0"));

			if ("".equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_E.equals(sLLayerStat)) {
				iTotalN += iEmpty;
			} else {

				if (sProgCd.equals(sLProgCd)) {
					iTotalN += iLPROG_CD;
				}
				if (sNextPc.equals(sLNextPc)) {
					iTotalN += iLNEXT_PROC;
				}
				if (sSpec.equals(sLSpec)) {
					iTotalN += iLSPEC;
				}
				if (sOrdNo.equals(sLOrdNo)) {
					iTotalN += iLORD_NO;
				}
				if (sOrdDtl.equals(sLOrdDtl)) {
					iTotalN += iLORD_DTL;
				}
				if (sMillDt.equals(sLMillDt)) {
					iTotalN += iLSTAT;
				}
			}

			if ("".equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_E.equals(sRLayerStat)) {
				iTotalN += iEmpty;
			} else {
				if (sProgCd.equals(sRProgCd)) {
					iTotalN += iRPROG_CD;
				}
				if (sNextPc.equals(sRNextPc)) {
					iTotalN += iRNEXT_PROC;
				}
				if (sSpec.equals(sRSpec)) {
					iTotalN += iRSPEC;
				}
				if (sOrdNo.equals(sROrdNo)) {
					iTotalN += iRORD_NO;
				}
				if (sOrdDtl.equals(sROrdDtl)) {
					iTotalN += iRORD_DTL;
				}
				if (sMillDt.equals(sRMillDt)) {
					iTotalN += iRMILL_DT;
				}
			}

			if (YmCommonConst.STACK_LAYER_STAT_L.equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_P.equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_X.equals(sLLayerStat)) {
				iTotalN += iLSTAT;
			}
			if (YmCommonConst.STACK_LAYER_STAT_L.equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_P.equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_X.equals(sRLayerStat)) {
				iTotalN += iRSTAT;
			}
			logger.println(LogLevel.DEBUG, this,
					"정정작업지시대기 01단 TO위치 적합성 체크 결과 종합점수=====" + iTotalN);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return iTotalN;
	}

	/**
	 * 정작업지시대기 02단 TO위치 적합성 체크
	 * 
	 * @param JDTORecord :
	 *            TO위치정보
	 * @param JDTORecord :
	 *            기준정보
	 * 
	 * @return int 종합점수
	 * @throws Exception
	 */
	private int getCoilToLocInfo_006_B_02(JDTORecord stackV, JDTORecord ruleV)
			throws Exception {
		int iTotalN = 0;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			logger.println(LogLevel.DEBUG, this,"정정작업지시대기 02단 TO위치 적합성 체크=====");

			int iUPROG_CD = ruleV.getFieldInt("UPROG_CD");
			int iUNEXT_PROC = ruleV.getFieldInt("UNEXT_PROC");
			int iUSPEC = ruleV.getFieldInt("USPEC");
			int iUORD_NO = ruleV.getFieldInt("UORD_NO");
			int iUORD_DTL = ruleV.getFieldInt("UORD_DTL");
			int iUMILL_DT = ruleV.getFieldInt("UMILL_DT");
			int iSPEZ	  = ruleV.getFieldInt("SPEZ");  //짱구코일 점수
			

			String sProgCd = StringHelper.evl(stackV.getFieldString("COIL_PROG_CD") , ""); // 진도코드
			String sNextPc = StringHelper.evl(stackV.getFieldString("COIL_NEXT_PROC") , ""); // 차공정
			String sSpec = StringHelper.evl(stackV.getFieldString("COIL_SPEC_ABBSYM") , ""); // 규격약호
			String sOrdNo = StringHelper.evl(stackV.getFieldString("COIL_ORD_NO") , ""); // 주문번호
			String sOrdDtl = StringHelper.evl(stackV.getFieldString("COIL_ORD_DTL") , ""); // 주문행번
			String sMillDt = StringHelper.evl(stackV.getFieldString("COIL_MILL_DT") , ""); // 압연일시
			String sJjangGuChk= StringHelper.evl(stackV.getFieldString("JJANGGU_COIL_CHK") , ""); // 짱구유무
			

			String sLProgCd = StringHelper.evl(stackV.getFieldString("LCOIL_PROG_CD") , ""); // 진도코드
			String sLNextPc = StringHelper.evl(stackV.getFieldString("LCOIL_NEXT_PROC") , ""); // 차공정
			String sLSpec = StringHelper.evl(stackV.getFieldString("LCOIL_SPEC_ABBSYM") , ""); // 규격약호
			String sLOrdNo = StringHelper.evl(stackV.getFieldString("LCOIL_ORD_NO") , ""); // 주문번호
			String sLOrdDtl = StringHelper.evl(stackV.getFieldString("LCOIL_ORD_DTL") , ""); // 주문행번
			String sLMillDt = StringHelper.evl(stackV.getFieldString("LCOIL_MILL_DT") , ""); // 압연일시

			String sRProgCd = StringHelper.evl(stackV.getFieldString("RCOIL_PROG_CD") , ""); // 진도코드
			String sRNextPc = StringHelper.evl(stackV.getFieldString("RCOIL_NEXT_PROC") , ""); // 차공정
			String sRSpec = StringHelper.evl(stackV.getFieldString("RCOIL_SPEC_ABBSYM") , ""); // 규격약호
			String sROrdNo = StringHelper.evl(stackV.getFieldString("RCOIL_ORD_NO") , ""); // 주문번호
			String sROrdDtl = StringHelper.evl(stackV.getFieldString("RCOIL_ORD_DTL") , ""); // 주문행번
			String sRMillDt = StringHelper.evl(stackV.getFieldString("RCOIL_MILL_DT") , ""); // 압연일시

			logger.println(LogLevel.DEBUG, this, "=코일 진도코드	=" + sProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 차공정	=" + sNextPc);
			logger.println(LogLevel.DEBUG, this, "=코일 규격약호	=" + sSpec);
			logger.println(LogLevel.DEBUG, this, "=코일 주문번호	=" + sOrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 주문행번	=" + sOrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 압연일시	=" + sMillDt);
			logger.println(LogLevel.DEBUG, this, "=코일 짱구유무	=" + sJjangGuChk);
			

			logger.println(LogLevel.DEBUG, this, "=코일 좌진도코드=" + sLProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 좌차공정	=" + sLNextPc);
			logger.println(LogLevel.DEBUG, this, "=코일 좌규격약호=" + sLSpec);
			logger.println(LogLevel.DEBUG, this, "=코일 좌주문번호=" + sLOrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 좌주문행번=" + sLOrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 좌압연일시=" + sLMillDt);

			logger.println(LogLevel.DEBUG, this, "=코일 우진도코드=" + sRProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 우차공정	=" + sRNextPc);
			logger.println(LogLevel.DEBUG, this, "=코일 우규격약호=" + sRSpec);
			logger.println(LogLevel.DEBUG, this, "=코일 우주문번호=" + sROrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 우주문행번=" + sROrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 우압연일시=" + sRMillDt);

			if (sProgCd.equals(sLProgCd)) {
				iTotalN += iUPROG_CD;
			}
			if (sNextPc.equals(sLNextPc)) {
				iTotalN += iUNEXT_PROC;
			}
			if (sSpec.equals(sLSpec)) {
				iTotalN += iUSPEC;
			}
			if (sOrdNo.equals(sLOrdNo)) {
				iTotalN += iUORD_NO;
			}
			if (sOrdDtl.equals(sLOrdDtl)) {
				iTotalN += iUORD_DTL;
			}
			if (sMillDt.equals(sLMillDt)) {
				iTotalN += iUMILL_DT;
			}

			if (sProgCd.equals(sRProgCd)) {
				iTotalN += iUPROG_CD;
			}
			if (sNextPc.equals(sRNextPc)) {
				iTotalN += iUNEXT_PROC;
			}
			if (sSpec.equals(sRSpec)) {
				iTotalN += iUSPEC;
			}
			if (sOrdNo.equals(sROrdNo)) {
				iTotalN += iUORD_NO;
			}
			if (sOrdDtl.equals(sROrdDtl)) {
				iTotalN += iUORD_DTL;
			}
			if (sMillDt.equals(sRMillDt)) {
				iTotalN += iUMILL_DT;
			}
			
			if ("Y".equals(sJjangGuChk)) {
				iTotalN += iSPEZ;
			}
			

			logger.println(LogLevel.DEBUG, this,"정정작업지시대기 02단 TO위치 적합성 체크 결과 종합점수=====" + iTotalN);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return iTotalN;
	}

	/**
	 * 정정작업대기 01단 TO위치 적합성 체크
	 * 
	 * @param JDTORecord :
	 *            TO위치정보
	 * @param JDTORecord :
	 *            기준정보
	 * 
	 * @return int 종합점수
	 * @throws Exception
	 */
	private int getCoilToLocInfo_006_C_01(JDTORecord stackV, JDTORecord ruleV)
			throws Exception {
		int iTotalN = 0;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			logger.println(LogLevel.DEBUG, this, "정정작업대기 01단 TO위치 적합성 체크=====");

			int iLPROG_CD = ruleV.getFieldInt("LPROG_CD");
			int iLNEXT_PROC = ruleV.getFieldInt("LNEXT_PROC");
			int iLSPEC = ruleV.getFieldInt("LSPEC");
			int iLORD_NO = ruleV.getFieldInt("LORD_NO");
			int iLORD_DTL = ruleV.getFieldInt("LORD_DTL");
			int iLMILL_DT = ruleV.getFieldInt("LMILL_DT");
			int iLSTAT = ruleV.getFieldInt("LSTAT");

			int iRPROG_CD = ruleV.getFieldInt("RPROG_CD");
			int iRNEXT_PROC = ruleV.getFieldInt("RNEXT_PROC");
			int iRSPEC = ruleV.getFieldInt("RSPEC");
			int iRORD_NO = ruleV.getFieldInt("RORD_NO");
			int iRORD_DTL = ruleV.getFieldInt("RORD_DTL");
			int iRMILL_DT = ruleV.getFieldInt("RMILL_DT");
			int iRSTAT = ruleV.getFieldInt("RSTAT");

			String sProgCd = StringHelper.evl(stackV
					.getFieldString("COIL_PROG_CD"), ""); // 진도코드
			String sNextPc = StringHelper.evl(stackV
					.getFieldString("COIL_NEXT_PROC"), ""); // 차공정
			String sSpec = StringHelper.evl(stackV
					.getFieldString("COIL_SPEC_ABBSYM"), ""); // 규격약호
			String sOrdNo = StringHelper.evl(stackV
					.getFieldString("COIL_ORD_NO"), ""); // 주문번호
			String sOrdDtl = StringHelper.evl(stackV
					.getFieldString("COIL_ORD_DTL"), ""); // 주문행번
			String sMillDt = StringHelper.evl(stackV
					.getFieldString("COIL_MILL_DT"), ""); // 압연일시

			String sLProgCd = StringHelper.evl(stackV
					.getFieldString("LCOIL_PROG_CD"), ""); // 진도코드
			String sLNextPc = StringHelper.evl(stackV
					.getFieldString("LCOIL_NEXT_PROC"), ""); // 차공정
			String sLSpec = StringHelper.evl(stackV
					.getFieldString("LCOIL_SPEC_ABBSYM"), ""); // 규격약호
			String sLOrdNo = StringHelper.evl(stackV
					.getFieldString("LCOIL_ORD_NO"), ""); // 주문번호
			String sLOrdDtl = StringHelper.evl(stackV
					.getFieldString("LCOIL_ORD_DTL"), ""); // 주문행번
			String sLMillDt = StringHelper.evl(stackV
					.getFieldString("LCOIL_MILL_DT"), ""); // 압연일시

			String sRProgCd = StringHelper.evl(stackV
					.getFieldString("RCOIL_PROG_CD"), ""); // 진도코드
			String sRNextPc = StringHelper.evl(stackV
					.getFieldString("RCOIL_NEXT_PROC"), ""); // 차공정
			String sRSpec = StringHelper.evl(stackV
					.getFieldString("RCOIL_SPEC_ABBSYM"), ""); // 규격약호
			String sROrdNo = StringHelper.evl(stackV
					.getFieldString("RCOIL_ORD_NO"), ""); // 주문번호
			String sROrdDtl = StringHelper.evl(stackV
					.getFieldString("RCOIL_ORD_DTL"), ""); // 주문행번
			String sRMillDt = StringHelper.evl(stackV
					.getFieldString("RCOIL_MILL_DT"), ""); // 압연일시

			String sLLayerStat = StringHelper.evl(stackV
					.getFieldString("LEFT_STACK_LAYER_STAT"), "");
			String sRLayerStat = StringHelper.evl(stackV
					.getFieldString("RIGHT_STACK_LAYER_STAT"), "");

			logger.println(LogLevel.DEBUG, this, "=코일 진도코드	=" + sProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 차공정	=" + sNextPc);
			logger.println(LogLevel.DEBUG, this, "=코일 규격약호	=" + sSpec);
			logger.println(LogLevel.DEBUG, this, "=코일 주문번호	=" + sOrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 주문행번	=" + sOrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 압연일시	=" + sMillDt);

			logger.println(LogLevel.DEBUG, this, "=코일 좌진도코드=" + sLProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 좌차공정	=" + sLNextPc);
			logger.println(LogLevel.DEBUG, this, "=코일 좌규격약호=" + sLSpec);
			logger.println(LogLevel.DEBUG, this, "=코일 좌주문번호=" + sLOrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 좌주문행번=" + sLOrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 좌압연일시=" + sLMillDt);

			logger.println(LogLevel.DEBUG, this, "=코일 우진도코드=" + sRProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 우차공정	=" + sRNextPc);
			logger.println(LogLevel.DEBUG, this, "=코일 우규격약호=" + sRSpec);
			logger.println(LogLevel.DEBUG, this, "=코일 우주문번호=" + sROrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 우주문행번=" + sROrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 우압연일시=" + sRMillDt);

			logger.println(LogLevel.DEBUG, this, "=코일 좌적치상태=" + sLLayerStat);
			logger.println(LogLevel.DEBUG, this, "=코일 우적치상태=" + sRLayerStat);

			int iEmpty = Integer.parseInt(StringHelper.evl(ruleV
					.getFieldString("MXE" + sProgCd), "0"));

			if ("".equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_E.equals(sLLayerStat)) {
				iTotalN += iEmpty;
			} else {

				if (sProgCd.equals(sLProgCd)) {
					iTotalN += iLPROG_CD;
				}
				if (sNextPc.equals(sLNextPc)) {
					iTotalN += iLNEXT_PROC;
				}
				if (sSpec.equals(sLSpec)) {
					iTotalN += iLSPEC;
				}
				if (sOrdNo.equals(sLOrdNo)) {
					iTotalN += iLORD_NO;
				}
				if (sOrdDtl.equals(sLOrdDtl)) {
					iTotalN += iLORD_DTL;
				}
				if (sMillDt.equals(sLMillDt)) {
					iTotalN += iLMILL_DT;
				}
			}

			if ("".equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_E.equals(sRLayerStat)) {
				iTotalN += iEmpty;
			} else {
				if (sProgCd.equals(sRProgCd)) {
					iTotalN += iRPROG_CD;
				}
				if (sNextPc.equals(sRNextPc)) {
					iTotalN += iRNEXT_PROC;
				}
				if (sSpec.equals(sRSpec)) {
					iTotalN += iRSPEC;
				}
				if (sOrdNo.equals(sROrdNo)) {
					iTotalN += iRORD_NO;
				}
				if (sOrdDtl.equals(sROrdDtl)) {
					iTotalN += iRORD_DTL;
				}
				if (sMillDt.equals(sRMillDt)) {
					iTotalN += iRMILL_DT;
				}
			}

			if (YmCommonConst.STACK_LAYER_STAT_L.equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_P.equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_X.equals(sLLayerStat)) {
				iTotalN += iLSTAT;
			}
			if (YmCommonConst.STACK_LAYER_STAT_L.equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_P.equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_X.equals(sRLayerStat)) {
				iTotalN += iRSTAT;
			}
			logger.println(LogLevel.DEBUG, this,
					"정정작업대기 01단 TO위치 적합성 체크 결과 종합점수=====" + iTotalN);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return iTotalN;
	}

	/**
	 * 정정작업대기 02단 TO위치 적합성 체크
	 * 
	 * @param JDTORecord :
	 *            TO위치정보
	 * @param JDTORecord :
	 *            기준정보
	 * 
	 * @return int 종합점수
	 * @throws Exception
	 */
	private int getCoilToLocInfo_006_C_02(JDTORecord stackV, JDTORecord ruleV)
			throws Exception {
		int iTotalN = 0;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			logger.println(LogLevel.DEBUG, this, "정정작업대기 02단 TO위치 적합성 체크=====");

			int iUPROG_CD = ruleV.getFieldInt("UPROG_CD");
			int iUNEXT_PROC = ruleV.getFieldInt("UNEXT_PROC");
			int iUSPEC = ruleV.getFieldInt("USPEC");
			int iUORD_NO = ruleV.getFieldInt("UORD_NO");
			int iUORD_DTL = ruleV.getFieldInt("UORD_DTL");
			int iUMILL_DT = ruleV.getFieldInt("UMILL_DT");
			int iSPEZ	  = ruleV.getFieldInt("SPEZ");  //짱구코일 점수

			String sProgCd = StringHelper.evl(stackV.getFieldString("COIL_PROG_CD") , ""); // 진도코드
			String sNextPc = StringHelper.evl(stackV.getFieldString("COIL_NEXT_PROC") , ""); // 차공정
			String sSpec = StringHelper.evl(stackV.getFieldString("COIL_SPEC_ABBSYM") , ""); // 규격약호
			String sOrdNo = StringHelper.evl(stackV.getFieldString("COIL_ORD_NO") , ""); // 주문번호
			String sOrdDtl = StringHelper.evl(stackV.getFieldString("COIL_ORD_DTL") , ""); // 주문행번
			String sMillDt = StringHelper.evl(stackV.getFieldString("COIL_MILL_DT") , ""); // 압연일시
			String sJjangGuChk= StringHelper.evl(stackV.getFieldString("JJANGGU_COIL_CHK") , ""); // 짱구유무

			String sLProgCd = StringHelper.evl(stackV.getFieldString("LCOIL_PROG_CD") , ""); // 진도코드
			String sLNextPc = StringHelper.evl(stackV.getFieldString("LCOIL_NEXT_PROC") , ""); // 차공정
			String sLSpec = StringHelper.evl(stackV.getFieldString("LCOIL_SPEC_ABBSYM") , ""); // 규격약호
			String sLOrdNo = StringHelper.evl(stackV.getFieldString("LCOIL_ORD_NO") , ""); // 주문번호
			String sLOrdDtl = StringHelper.evl(stackV.getFieldString("LCOIL_ORD_DTL") , ""); // 주문행번
			String sLMillDt = StringHelper.evl(stackV.getFieldString("LCOIL_MILL_DT") , ""); // 압연일시

			String sRProgCd = StringHelper.evl(stackV.getFieldString("RCOIL_PROG_CD") , ""); // 진도코드
			String sRNextPc = StringHelper.evl(stackV.getFieldString("RCOIL_NEXT_PROC") , ""); // 차공정
			String sRSpec = StringHelper.evl(stackV.getFieldString("RCOIL_SPEC_ABBSYM") , ""); // 규격약호
			String sROrdNo = StringHelper.evl(stackV.getFieldString("RCOIL_ORD_NO") , ""); // 주문번호
			String sROrdDtl = StringHelper.evl(stackV.getFieldString("RCOIL_ORD_DTL") , ""); // 주문행번
			String sRMillDt = StringHelper.evl(stackV.getFieldString("RCOIL_MILL_DT") , ""); // 압연일시

			logger.println(LogLevel.DEBUG, this, "=코일 진도코드	=" + sProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 차공정	=" + sNextPc);
			logger.println(LogLevel.DEBUG, this, "=코일 규격약호	=" + sSpec);
			logger.println(LogLevel.DEBUG, this, "=코일 주문번호	=" + sOrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 주문행번	=" + sOrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 압연일시	=" + sMillDt);
			logger.println(LogLevel.DEBUG, this, "=코일 짱구유무	=" + sJjangGuChk);

			logger.println(LogLevel.DEBUG, this, "=코일 좌진도코드=" + sLProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 좌차공정	=" + sLNextPc);
			logger.println(LogLevel.DEBUG, this, "=코일 좌규격약호=" + sLSpec);
			logger.println(LogLevel.DEBUG, this, "=코일 좌주문번호=" + sLOrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 좌주문행번=" + sLOrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 좌압연일시=" + sLMillDt);

			logger.println(LogLevel.DEBUG, this, "=코일 우진도코드=" + sRProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 우차공정	=" + sRNextPc);
			logger.println(LogLevel.DEBUG, this, "=코일 우규격약호=" + sRSpec);
			logger.println(LogLevel.DEBUG, this, "=코일 우주문번호=" + sROrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 우주문행번=" + sROrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 우압연일시=" + sRMillDt);

			if (sProgCd.equals(sLProgCd)) {
				iTotalN += iUPROG_CD;
			}
			if (sNextPc.equals(sLNextPc)) {
				iTotalN += iUNEXT_PROC;
			}
			if (sSpec.equals(sLSpec)) {
				iTotalN += iUSPEC;
			}
			if (sOrdNo.equals(sLOrdNo)) {
				iTotalN += iUORD_NO;
			}
			if (sOrdDtl.equals(sLOrdDtl)) {
				iTotalN += iUORD_DTL;
			}
			if (sMillDt.equals(sLMillDt)) {
				iTotalN += iUMILL_DT;
			}

			if (sProgCd.equals(sRProgCd)) {
				iTotalN += iUPROG_CD;
			}
			if (sNextPc.equals(sRNextPc)) {
				iTotalN += iUNEXT_PROC;
			}
			if (sSpec.equals(sRSpec)) {
				iTotalN += iUSPEC;
			}
			if (sOrdNo.equals(sROrdNo)) {
				iTotalN += iUORD_NO;
			}
			if (sOrdDtl.equals(sROrdDtl)) {
				iTotalN += iUORD_DTL;
			}
			if (sMillDt.equals(sRMillDt)) {
				iTotalN += iUMILL_DT;
			}
			
			if ("Y".equals(sJjangGuChk)) {
				iTotalN += iSPEZ;
			}

			logger.println(LogLevel.DEBUG, this,
					"정정작업대기 02단 TO위치 적합성 체크 결과 종합점수=====" + iTotalN);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return iTotalN;
	}

	/**
	 * 이송작업지시대기 01단 TO위치 적합성 체크
	 * 
	 * @param JDTORecord :
	 *            TO위치정보
	 * @param JDTORecord :
	 *            기준정보
	 * 
	 * @return int 종합점수
	 * @throws Exception
	 */
	private int getCoilToLocInfo_006_D_01(JDTORecord stackV, JDTORecord ruleV)
			throws Exception {
		int iTotalN = 0;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			logger.println(LogLevel.DEBUG, this,
					"이송작업지시대기 01단 TO위치 적합성 체크=====");

			int iLPROG_CD = ruleV.getFieldInt("LPROG_CD");
			int iLNEXT_PROC = ruleV.getFieldInt("LNEXT_PROC");
			int iLSTAT = ruleV.getFieldInt("LSTAT");

			int iRPROG_CD = ruleV.getFieldInt("RPROG_CD");
			int iRNEXT_PROC = ruleV.getFieldInt("RNEXT_PROC");
			int iRSTAT = ruleV.getFieldInt("RSTAT");

			String sProgCd = StringHelper.evl(stackV
					.getFieldString("COIL_PROG_CD"), ""); // 진도코드
			String sNextPc = StringHelper.evl(stackV
					.getFieldString("COIL_NEXT_PROC"), ""); // 차공정

			String sLProgCd = StringHelper.evl(stackV
					.getFieldString("LCOIL_PROG_CD"), ""); // 진도코드
			String sLNextPc = StringHelper.evl(stackV
					.getFieldString("LCOIL_NEXT_PROC"), ""); // 차공정

			String sRProgCd = StringHelper.evl(stackV
					.getFieldString("RCOIL_PROG_CD"), ""); // 진도코드
			String sRNextPc = StringHelper.evl(stackV
					.getFieldString("RCOIL_NEXT_PROC"), ""); // 차공정

			String sLLayerStat = StringHelper.evl(stackV
					.getFieldString("LEFT_STACK_LAYER_STAT"), "");
			String sRLayerStat = StringHelper.evl(stackV
					.getFieldString("RIGHT_STACK_LAYER_STAT"), "");

			logger.println(LogLevel.DEBUG, this, "=코일 진도코드	=" + sProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 차공정	=" + sNextPc);
			logger.println(LogLevel.DEBUG, this, "=코일 좌진도코드=" + sLProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 좌차공정	=" + sLNextPc);
			logger.println(LogLevel.DEBUG, this, "=코일 우진도코드=" + sRProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 우차공정	=" + sRNextPc);
			logger.println(LogLevel.DEBUG, this, "=코일 좌적치상태=" + sLLayerStat);
			logger.println(LogLevel.DEBUG, this, "=코일 우적치상태=" + sRLayerStat);

			int iEmpty = Integer.parseInt(StringHelper.evl(ruleV
					.getFieldString("MXE" + sProgCd), "0"));

			if ("".equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_E.equals(sLLayerStat)) {
				iTotalN += iEmpty;
			} else {
				if (sProgCd.equals(sLProgCd)) {
					iTotalN += iLPROG_CD;
				}
				if (sNextPc.equals(sLNextPc)) {
					iTotalN += iLNEXT_PROC;
				}
			}

			if ("".equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_E.equals(sRLayerStat)) {
				iTotalN += iEmpty;
			} else {
				if (sProgCd.equals(sRProgCd)) {
					iTotalN += iRPROG_CD;
				}
				if (sNextPc.equals(sRNextPc)) {
					iTotalN += iRNEXT_PROC;
				}
			}

			if (YmCommonConst.STACK_LAYER_STAT_L.equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_P.equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_X.equals(sLLayerStat)) {
				iTotalN += iLSTAT;
			}
			if (YmCommonConst.STACK_LAYER_STAT_L.equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_P.equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_X.equals(sRLayerStat)) {
				iTotalN += iRSTAT;
			}
			logger.println(LogLevel.DEBUG, this,
					"이송작업지시대기 01단 TO위치 적합성 체크 결과 종합점수=====" + iTotalN);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return iTotalN;
	}

	/**
	 * 이송작업지시대기 02단 TO위치 적합성 체크
	 * 
	 * @param JDTORecord :
	 *            TO위치정보
	 * @param JDTORecord :
	 *            기준정보
	 * 
	 * @return int 종합점수
	 * @throws Exception
	 */
	private int getCoilToLocInfo_006_D_02(JDTORecord stackV, JDTORecord ruleV)
			throws Exception {
		int iTotalN = 0;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			logger.println(LogLevel.DEBUG, this,
					"이송작업지시대기 02단 TO위치 적합성 체크=====");

			int iUPROG_CD = ruleV.getFieldInt("UPROG_CD");
			int iUNEXT_PROC = ruleV.getFieldInt("UNEXT_PROC");

			String sProgCd = StringHelper.evl(stackV
					.getFieldString("COIL_PROG_CD"), ""); // 진도코드
			String sNextPc = StringHelper.evl(stackV
					.getFieldString("COIL_NEXT_PROC"), ""); // 차공정

			String sLProgCd = StringHelper.evl(stackV
					.getFieldString("LCOIL_PROG_CD"), ""); // 진도코드
			String sLNextPc = StringHelper.evl(stackV
					.getFieldString("LCOIL_NEXT_PROC"), ""); // 차공정

			String sRProgCd = StringHelper.evl(stackV
					.getFieldString("RCOIL_PROG_CD"), ""); // 진도코드
			String sRNextPc = StringHelper.evl(stackV
					.getFieldString("RCOIL_NEXT_PROC"), ""); // 차공정

			logger.println(LogLevel.DEBUG, this, "=코일 진도코드	=" + sProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 차공정	=" + sNextPc);
			logger.println(LogLevel.DEBUG, this, "=코일 좌진도코드=" + sLProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 좌차공정	=" + sLNextPc);
			logger.println(LogLevel.DEBUG, this, "=코일 우진도코드=" + sRProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 우차공정	=" + sRNextPc);

			if (sProgCd.equals(sLProgCd)) {
				iTotalN += iUPROG_CD;
			}
			if (sNextPc.equals(sLNextPc)) {
				iTotalN += iUNEXT_PROC;
			}
			if (sProgCd.equals(sRProgCd)) {
				iTotalN += iUPROG_CD;
			}
			if (sNextPc.equals(sRNextPc)) {
				iTotalN += iUNEXT_PROC;
			}

			logger.println(LogLevel.DEBUG, this,
					"이송작업지시대기 02단 TO위치 적합성 체크 결과 종합점수=====" + iTotalN);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return iTotalN;
	}

	/**
	 * 이송작업대기 01단 TO위치 적합성 체크
	 * 
	 * @param JDTORecord :
	 *            TO위치정보
	 * @param JDTORecord :
	 *            기준정보
	 * 
	 * @return int 종합점수
	 * @throws Exception
	 */
	private int getCoilToLocInfo_006_E_01(JDTORecord stackV, JDTORecord ruleV)
			throws Exception {
		int iTotalN = 0;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			logger.println(LogLevel.DEBUG, this, "이송작업대기 01단 TO위치 적합성 체크=====");

			int iLPROG_CD = ruleV.getFieldInt("LPROG_CD");
			int iLNEXT_PROC = ruleV.getFieldInt("LNEXT_PROC");
			int iLSTAT = ruleV.getFieldInt("LSTAT");

			int iRPROG_CD = ruleV.getFieldInt("RPROG_CD");
			int iRNEXT_PROC = ruleV.getFieldInt("RNEXT_PROC");
			int iRSTAT = ruleV.getFieldInt("RSTAT");

			String sProgCd = StringHelper.evl(stackV
					.getFieldString("COIL_PROG_CD"), ""); // 진도코드
			String sNextPc = StringHelper.evl(stackV
					.getFieldString("COIL_NEXT_PROC"), ""); // 차공정

			String sLProgCd = StringHelper.evl(stackV
					.getFieldString("LCOIL_PROG_CD"), ""); // 진도코드
			String sLNextPc = StringHelper.evl(stackV
					.getFieldString("LCOIL_NEXT_PROC"), ""); // 차공정

			String sRProgCd = StringHelper.evl(stackV
					.getFieldString("RCOIL_PROG_CD"), ""); // 진도코드
			String sRNextPc = StringHelper.evl(stackV
					.getFieldString("RCOIL_NEXT_PROC"), ""); // 차공정

			String sLLayerStat = StringHelper.evl(stackV
					.getFieldString("LEFT_STACK_LAYER_STAT"), "");
			String sRLayerStat = StringHelper.evl(stackV
					.getFieldString("RIGHT_STACK_LAYER_STAT"), "");

			logger.println(LogLevel.DEBUG, this, "=코일 진도코드	=" + sProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 차공정	=" + sNextPc);
			logger.println(LogLevel.DEBUG, this, "=코일 좌진도코드=" + sLProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 좌차공정	=" + sLNextPc);
			logger.println(LogLevel.DEBUG, this, "=코일 우진도코드=" + sRProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 우차공정	=" + sRNextPc);
			logger.println(LogLevel.DEBUG, this, "=코일 좌적치상태=" + sLLayerStat);
			logger.println(LogLevel.DEBUG, this, "=코일 우적치상태=" + sRLayerStat);

			int iEmpty = Integer.parseInt(StringHelper.evl(ruleV
					.getFieldString("MXE" + sProgCd), "0"));

			if ("".equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_E.equals(sLLayerStat)) {
				iTotalN += iEmpty;
			} else {

				if (sProgCd.equals(sLProgCd)) {
					iTotalN += iLPROG_CD;
				}
				if (sNextPc.equals(sLNextPc)) {
					iTotalN += iLNEXT_PROC;
				}
			}

			if ("".equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_E.equals(sRLayerStat)) {
				iTotalN += iEmpty;
			} else {
				if (sProgCd.equals(sRProgCd)) {
					iTotalN += iRPROG_CD;
				}
				if (sNextPc.equals(sRNextPc)) {
					iTotalN += iRNEXT_PROC;
				}
			}

			if (YmCommonConst.STACK_LAYER_STAT_L.equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_P.equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_X.equals(sLLayerStat)) {
				iTotalN += iLSTAT;
			}
			if (YmCommonConst.STACK_LAYER_STAT_L.equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_P.equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_X.equals(sRLayerStat)) {
				iTotalN += iRSTAT;
			}
			logger.println(LogLevel.DEBUG, this,
					"이송작업대기 01단 TO위치 적합성 체크 결과 종합점수=====" + iTotalN);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return iTotalN;
	}

	/**
	 * 이송작업대기 02단 TO위치 적합성 체크
	 * 
	 * @param JDTORecord :
	 *            TO위치정보
	 * @param JDTORecord :
	 *            기준정보
	 * 
	 * @return int 종합점수
	 * @throws Exception
	 */
	private int getCoilToLocInfo_006_E_02(JDTORecord stackV, JDTORecord ruleV)
			throws Exception {
		int iTotalN = 0;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			logger.println(LogLevel.DEBUG, this, "이송작업대기 02단 TO위치 적합성 체크=====");

			int iUPROG_CD = ruleV.getFieldInt("UPROG_CD");
			int iUNEXT_PROC = ruleV.getFieldInt("UNEXT_PROC");

			String sProgCd = StringHelper.evl(stackV
					.getFieldString("COIL_PROG_CD"), ""); // 진도코드
			String sNextPc = StringHelper.evl(stackV
					.getFieldString("COIL_NEXT_PROC"), ""); // 차공정

			String sLProgCd = StringHelper.evl(stackV
					.getFieldString("LCOIL_PROG_CD"), ""); // 진도코드
			String sLNextPc = StringHelper.evl(stackV
					.getFieldString("LCOIL_NEXT_PROC"), ""); // 차공정

			String sRProgCd = StringHelper.evl(stackV
					.getFieldString("RCOIL_PROG_CD"), ""); // 진도코드
			String sRNextPc = StringHelper.evl(stackV
					.getFieldString("RCOIL_NEXT_PROC"), ""); // 차공정

			logger.println(LogLevel.DEBUG, this, "=코일 진도코드	=" + sProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 차공정	=" + sNextPc);
			logger.println(LogLevel.DEBUG, this, "=코일 좌진도코드=" + sLProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 좌차공정	=" + sLNextPc);
			logger.println(LogLevel.DEBUG, this, "=코일 우진도코드=" + sRProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 우차공정	=" + sRNextPc);

			if (sProgCd.equals(sLProgCd)) {
				iTotalN += iUPROG_CD;
			}
			if (sNextPc.equals(sLNextPc)) {
				iTotalN += iUNEXT_PROC;
			}
			if (sProgCd.equals(sRProgCd)) {
				iTotalN += iUPROG_CD;
			}
			if (sNextPc.equals(sRNextPc)) {
				iTotalN += iUNEXT_PROC;
			}

			logger.println(LogLevel.DEBUG, this,
					"이송작업대기 02단 TO위치 적합성 체크 결과 종합점수=====" + iTotalN);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return iTotalN;
	}

	/**
	 * 판정보류 01단 TO위치 적합성 체크
	 * 
	 * @param JDTORecord :
	 *            TO위치정보
	 * @param JDTORecord :
	 *            기준정보
	 * 
	 * @return int 종합점수
	 * @throws Exception
	 */
	private int getCoilToLocInfo_006_F_01(JDTORecord stackV, JDTORecord ruleV)
			throws Exception {
		int iTotalN = 0;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			logger.println(LogLevel.DEBUG, this, "판정보류 01단 TO위치 적합성 체크=====");

//			int iLPROG_CD = ruleV.getFieldInt("LPROG_CD");
//			int iLSTAT = ruleV.getFieldInt("LSTAT");
//
//			int iRPROG_CD = ruleV.getFieldInt("RPROG_CD");
//			int iRSTAT = ruleV.getFieldInt("RSTAT");
//
//			String sProgCd = StringHelper.evl(stackV
//					.getFieldString("COIL_PROG_CD"), ""); // 진도코드
//
//			String sLProgCd = StringHelper.evl(stackV
//					.getFieldString("LCOIL_PROG_CD"), ""); // 진도코드
//
//			String sRProgCd = StringHelper.evl(stackV
//					.getFieldString("RCOIL_PROG_CD"), ""); // 진도코드
//
//			String sLLayerStat = StringHelper.evl(stackV
//					.getFieldString("LEFT_STACK_LAYER_STAT"), "");
//			String sRLayerStat = StringHelper.evl(stackV
//					.getFieldString("RIGHT_STACK_LAYER_STAT"), "");
//
//			logger.println(LogLevel.DEBUG, this, "=코일 진도코드	=" + sProgCd);
//
//			logger.println(LogLevel.DEBUG, this, "=코일 좌진도코드=" + sLProgCd);
//
//			logger.println(LogLevel.DEBUG, this, "=코일 우진도코드=" + sRProgCd);
//
//			logger.println(LogLevel.DEBUG, this, "=코일 좌적치상태=" + sLLayerStat);
//			logger.println(LogLevel.DEBUG, this, "=코일 우적치상태=" + sRLayerStat);
//
//			int iEmpty = Integer.parseInt(StringHelper.evl(ruleV
//					.getFieldString("MXE" + sProgCd), "0"));
//
//			if ("".equals(sLLayerStat)
//					|| YmCommonConst.STACK_LAYER_STAT_E.equals(sLLayerStat)) {
//				iTotalN += iEmpty;
//			} else {
//
//				if (sProgCd.equals(sLProgCd)) {
//					iTotalN += iLPROG_CD;
//				}
//			}
//
//			if ("".equals(sRLayerStat)
//					|| YmCommonConst.STACK_LAYER_STAT_E.equals(sRLayerStat)) {
//				iTotalN += iEmpty;
//			} else {
//				if (sProgCd.equals(sRProgCd)) {
//					iTotalN += iRPROG_CD;
//				}
//			}
			
			int iLPROG_CD = ruleV.getFieldInt("LPROG_CD");
			int iLORD_NO = ruleV.getFieldInt("LORD_NO");
			int iLORD_DTL = ruleV.getFieldInt("LORD_DTL");
			int iLSTAT = ruleV.getFieldInt("LSTAT");

			int iRPROG_CD = ruleV.getFieldInt("RPROG_CD");
			int iRORD_NO = ruleV.getFieldInt("RORD_NO");
			int iRORD_DTL = ruleV.getFieldInt("RORD_DTL");
			int iRSTAT = ruleV.getFieldInt("RSTAT");

			String sProgCd = StringHelper.evl(stackV
					.getFieldString("COIL_PROG_CD"), ""); // 진도코드
			String sOrdNo = StringHelper.evl(stackV
					.getFieldString("COIL_ORD_NO"), ""); // 주문번호
			String sOrdDtl = StringHelper.evl(stackV
					.getFieldString("COIL_ORD_DTL"), ""); // 주문행번

			String sLProgCd = StringHelper.evl(stackV
					.getFieldString("LCOIL_PROG_CD"), ""); // 진도코드
			String sLOrdNo = StringHelper.evl(stackV
					.getFieldString("LCOIL_ORD_NO"), ""); // 주문번호
			String sLOrdDtl = StringHelper.evl(stackV
					.getFieldString("LCOIL_ORD_DTL"), ""); // 주문행번

			String sRProgCd = StringHelper.evl(stackV
					.getFieldString("RCOIL_PROG_CD"), ""); // 진도코드
			String sROrdNo = StringHelper.evl(stackV
					.getFieldString("RCOIL_ORD_NO"), ""); // 주문번호
			String sROrdDtl = StringHelper.evl(stackV
					.getFieldString("RCOIL_ORD_DTL"), ""); // 주문행번

			String sLLayerStat = StringHelper.evl(stackV
					.getFieldString("LEFT_STACK_LAYER_STAT"), "");
			String sRLayerStat = StringHelper.evl(stackV
					.getFieldString("RIGHT_STACK_LAYER_STAT"), "");

			String sDemanderCd = StringHelper.evl(stackV
					.getFieldString("COIL_DEMANDER_CD"), ""); // 수요가코드
			String sLDemanderCd = StringHelper.evl(stackV
					.getFieldString("LCOIL_DEMANDER_CD"), ""); // 수요가코드 
			String sRDemanderCd = StringHelper.evl(stackV
					.getFieldString("RCOIL_DEMANDER_CD"), ""); // 수요가코드 
			

			logger.println(LogLevel.DEBUG, this, "=코일 진도코드	=" + sProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 주문번호	=" + sOrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 주문행번	=" + sOrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 수요가		=" + sDemanderCd);

			logger.println(LogLevel.DEBUG, this, "=코일 좌진도코드=" + sLProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 좌주문번호=" + sLOrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 좌주문행번=" + sLOrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일  좌수요가  =" + sLDemanderCd);

			logger.println(LogLevel.DEBUG, this, "=코일 우진도코드=" + sRProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 우주문번호=" + sROrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 우주문행번=" + sROrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 우수요가   =" + sRDemanderCd);

			logger.println(LogLevel.DEBUG, this, "=코일 좌적치상태=" + sLLayerStat);
			logger.println(LogLevel.DEBUG, this, "=코일 우적치상태=" + sRLayerStat);

			int iEmpty = Integer.parseInt(StringHelper.evl(ruleV
					.getFieldString("MXE" + sProgCd), "0"));

			if ("".equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_E.equals(sLLayerStat)) {
				iTotalN += iEmpty;
				logger.println(LogLevel.DEBUG, this, "★좌공백 평점:" + iEmpty);
			} else {

				if (sProgCd.equals(sLProgCd)) {
					iTotalN += iLPROG_CD;
					logger.println(LogLevel.DEBUG, this, "★좌진도 평점:" + iLPROG_CD);
				}
				if (sOrdNo.equals(sLOrdNo)) {
					iTotalN += iLORD_NO;
					logger.println(LogLevel.DEBUG, this, "★좌주문번호  평점:" + iLORD_NO);
				}
				if (sOrdNo.equals(sLOrdNo) && sOrdDtl.equals(sLOrdDtl)) {
					iTotalN += iLORD_DTL;
					logger.println(LogLevel.DEBUG, this, "★좌주문번호행번   평점:" + iLORD_DTL);
				}
				//수요가 평점(50점)
				if (sDemanderCd.equals(sLDemanderCd)) {
					iTotalN += 50;
					logger.println(LogLevel.DEBUG, this, "★좌수요가   평점:" + 50);
				}
			}

			if ("".equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_E.equals(sRLayerStat)) {
				iTotalN += iEmpty;
				logger.println(LogLevel.DEBUG, this, "★우공백 평점:" + iEmpty);
			} else {

				if (sProgCd.equals(sRProgCd)) {
					iTotalN += iRPROG_CD;
					logger.println(LogLevel.DEBUG, this, "★우진도 평점:" + iRPROG_CD);
				}
				if (sOrdNo.equals(sROrdNo)) {
					iTotalN += iRORD_NO;
					logger.println(LogLevel.DEBUG, this, "★우주문번호  평점:" + iRORD_NO);
				}
				if (sOrdNo.equals(sROrdNo) && sOrdDtl.equals(sROrdDtl)) {
					iTotalN += iRORD_DTL;
					logger.println(LogLevel.DEBUG, this, "★우주문번호행번  평점:" + iRORD_DTL);
				}
				//수요가 평점(50점)
				if (sDemanderCd.equals(sRDemanderCd)) {
					iTotalN += 50;
					logger.println(LogLevel.DEBUG, this, "★우수요가   평점:" + 50);
				}
			}

			if (YmCommonConst.STACK_LAYER_STAT_L.equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_P.equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_X.equals(sLLayerStat)) {
				iTotalN += iLSTAT;
				logger.println(LogLevel.DEBUG, this, "★좌상태   평점:" + iLSTAT);
			}
			if (YmCommonConst.STACK_LAYER_STAT_L.equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_P.equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_X.equals(sRLayerStat)) {
				iTotalN += iRSTAT;
				logger.println(LogLevel.DEBUG, this, "★우상태   평점:" + iRSTAT);
			}
			logger.println(LogLevel.DEBUG, this,
					"판정보류 01단 TO위치 적합성 체크 결과 종합점수=====" + iTotalN);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return iTotalN;
	}

	/**
	 * 판정보류 02단 TO위치 적합성 체크
	 * 
	 * @param JDTORecord :
	 *            TO위치정보
	 * @param JDTORecord :
	 *            기준정보
	 * 
	 * @return int 종합점수
	 * @throws Exception
	 */
	private int getCoilToLocInfo_006_F_02(JDTORecord stackV, JDTORecord ruleV)
			throws Exception {
		int iTotalN = 0;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			logger.println(LogLevel.DEBUG, this, "판정보류 02단 TO위치 적합성 체크=====");

//			int iUPROG_CD = ruleV.getFieldInt("UPROG_CD");
//
//			String sProgCd = StringHelper.evl(stackV
//					.getFieldString("COIL_PROG_CD"), ""); // 진도코드
//
//			String sLProgCd = StringHelper.evl(stackV
//					.getFieldString("LCOIL_PROG_CD"), ""); // 진도코드
//
//			String sRProgCd = StringHelper.evl(stackV
//					.getFieldString("RCOIL_PROG_CD"), ""); // 진도코드
//
//			logger.println(LogLevel.DEBUG, this, "=코일 진도코드	=" + sProgCd);
//
//			logger.println(LogLevel.DEBUG, this, "=코일 좌진도코드=" + sLProgCd);
//
//			logger.println(LogLevel.DEBUG, this, "=코일 우진도코드=" + sRProgCd);
//
//			if (sProgCd.equals(sLProgCd)) {
//				iTotalN += iUPROG_CD;
//			}
//
//			if (sProgCd.equals(sRProgCd)) {
//				iTotalN += iUPROG_CD;
//			}
			
			
			int iUPROG_CD = ruleV.getFieldInt("UPROG_CD");
			int iUORD_NO = ruleV.getFieldInt("UORD_NO");
			int iUORD_DTL = ruleV.getFieldInt("UORD_DTL");

			String sProgCd = StringHelper.evl(stackV
					.getFieldString("COIL_PROG_CD"), ""); // 진도코드
			String sOrdNo = StringHelper.evl(stackV
					.getFieldString("COIL_ORD_NO"), ""); // 주문번호
			String sOrdDtl = StringHelper.evl(stackV
					.getFieldString("COIL_ORD_DTL"), ""); // 주문행번

			String sLProgCd = StringHelper.evl(stackV
					.getFieldString("LCOIL_PROG_CD"), ""); // 진도코드
			String sLOrdNo = StringHelper.evl(stackV
					.getFieldString("LCOIL_ORD_NO"), ""); // 주문번호
			String sLOrdDtl = StringHelper.evl(stackV
					.getFieldString("LCOIL_ORD_DTL"), ""); // 주문행번

			String sRProgCd = StringHelper.evl(stackV
					.getFieldString("RCOIL_PROG_CD"), ""); // 진도코드
			String sROrdNo = StringHelper.evl(stackV
					.getFieldString("RCOIL_ORD_NO"), ""); // 주문번호
			String sROrdDtl = StringHelper.evl(stackV
					.getFieldString("RCOIL_ORD_DTL"), ""); // 주문행번

			String sDemanderCd = StringHelper.evl(stackV
					.getFieldString("COIL_DEMANDER_CD"), ""); // 수요가코드
			String sLDemanderCd = StringHelper.evl(stackV
					.getFieldString("LCOIL_DEMANDER_CD"), ""); // 수요가코드 
			String sRDemanderCd = StringHelper.evl(stackV
					.getFieldString("RCOIL_DEMANDER_CD"), ""); // 수요가코드 
			

			logger.println(LogLevel.DEBUG, this, "=코일 진도코드	=" + sProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 주문번호	=" + sOrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 주문행번	=" + sOrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 수요가		=" + sDemanderCd);

			logger.println(LogLevel.DEBUG, this, "=코일 좌진도코드=" + sLProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 좌주문번호=" + sLOrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 좌주문행번=" + sLOrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일  좌수요가  =" + sLDemanderCd);

			logger.println(LogLevel.DEBUG, this, "=코일 우진도코드=" + sRProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 우주문번호=" + sROrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 우주문행번=" + sROrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 우수요가   =" + sRDemanderCd);

			if (sProgCd.equals(sLProgCd)) {
				iTotalN += iUPROG_CD;
				logger.println(LogLevel.DEBUG, this, "★좌진도 평점:" + iUPROG_CD);
			}
			if (sOrdNo.equals(sLOrdNo)) {
				iTotalN += iUORD_NO;
				logger.println(LogLevel.DEBUG, this, "★좌주문번호 평점:" + iUORD_NO);
			}
			if (sOrdNo.equals(sLOrdNo) && sOrdDtl.equals(sLOrdDtl)) {
				iTotalN += iUORD_DTL;
				logger.println(LogLevel.DEBUG, this, "★좌주문번호행번 평점:" + iUORD_DTL);
			}
			//수요가 평점(50점)
			if (sDemanderCd.equals(sLDemanderCd)) {
				iTotalN += 50;
				logger.println(LogLevel.DEBUG, this, "★좌수요가 평점:" + 50);
			}

			if (sProgCd.equals(sRProgCd)) {
				iTotalN += iUPROG_CD;
				logger.println(LogLevel.DEBUG, this, "★우진도 평점:" + iUPROG_CD);
			}
			if (sOrdNo.equals(sROrdNo)) {
				iTotalN += iUORD_NO;
				logger.println(LogLevel.DEBUG, this, "★우주문번호 평점:" + iUORD_NO);
			}
			if (sOrdNo.equals(sROrdNo) && sOrdDtl.equals(sROrdDtl)) {
				iTotalN += iUORD_DTL;
				logger.println(LogLevel.DEBUG, this, "★우주문번호행번 평점:" + iUORD_DTL);
			}
			//수요가 평점(50점)
			if (sDemanderCd.equals(sRDemanderCd)) {
				iTotalN += 50;
				logger.println(LogLevel.DEBUG, this, "★우수요가 평점:" + 50);
			}

			logger.println(LogLevel.DEBUG, this,
					"판정보류 02단 TO위치 적합성 체크 결과 종합점수=====" + iTotalN);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return iTotalN;
	}

	/**
	 * 종합판정대기 01단 TO위치 적합성 체크
	 * 
	 * @param JDTORecord :
	 *            TO위치정보
	 * @param JDTORecord :
	 *            기준정보
	 * 
	 * @return int 종합점수
	 * @throws Exception
	 */
	private int getCoilToLocInfo_006_G_01(JDTORecord stackV, JDTORecord ruleV)
			throws Exception {
		int iTotalN = 0;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			logger.println(LogLevel.DEBUG, this, "종합판정대기 01단 TO위치 적합성 체크=====");

			int iLPROG_CD = ruleV.getFieldInt("LPROG_CD");
			int iLORD_NO = ruleV.getFieldInt("LORD_NO");
			int iLORD_DTL = ruleV.getFieldInt("LORD_DTL");
			int iLSTAT = ruleV.getFieldInt("LSTAT");

			int iRPROG_CD = ruleV.getFieldInt("RPROG_CD");
			int iRORD_NO = ruleV.getFieldInt("RORD_NO");
			int iRORD_DTL = ruleV.getFieldInt("RORD_DTL");
			int iRSTAT = ruleV.getFieldInt("RSTAT");

			String sProgCd = StringHelper.evl(stackV
					.getFieldString("COIL_PROG_CD"), ""); // 진도코드
			String sOrdNo = StringHelper.evl(stackV
					.getFieldString("COIL_ORD_NO"), ""); // 주문번호
			String sOrdDtl = StringHelper.evl(stackV
					.getFieldString("COIL_ORD_DTL"), ""); // 주문행번

			String sLProgCd = StringHelper.evl(stackV
					.getFieldString("LCOIL_PROG_CD"), ""); // 진도코드
			String sLOrdNo = StringHelper.evl(stackV
					.getFieldString("LCOIL_ORD_NO"), ""); // 주문번호
			String sLOrdDtl = StringHelper.evl(stackV
					.getFieldString("LCOIL_ORD_DTL"), ""); // 주문행번

			String sRProgCd = StringHelper.evl(stackV
					.getFieldString("RCOIL_PROG_CD"), ""); // 진도코드
			String sROrdNo = StringHelper.evl(stackV
					.getFieldString("RCOIL_ORD_NO"), ""); // 주문번호
			String sROrdDtl = StringHelper.evl(stackV
					.getFieldString("RCOIL_ORD_DTL"), ""); // 주문행번

			String sLLayerStat = StringHelper.evl(stackV
					.getFieldString("LEFT_STACK_LAYER_STAT"), "");
			String sRLayerStat = StringHelper.evl(stackV
					.getFieldString("RIGHT_STACK_LAYER_STAT"), "");

			String sDemanderCd = StringHelper.evl(stackV
					.getFieldString("COIL_DEMANDER_CD"), ""); // 수요가코드
			String sLDemanderCd = StringHelper.evl(stackV
					.getFieldString("LCOIL_DEMANDER_CD"), ""); // 수요가코드 
			String sRDemanderCd = StringHelper.evl(stackV
					.getFieldString("RCOIL_DEMANDER_CD"), ""); // 수요가코드 
			

			logger.println(LogLevel.DEBUG, this, "=코일 진도코드	=" + sProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 주문번호	=" + sOrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 주문행번	=" + sOrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 수요가		=" + sDemanderCd);

			logger.println(LogLevel.DEBUG, this, "=코일 좌진도코드=" + sLProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 좌주문번호=" + sLOrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 좌주문행번=" + sLOrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일  좌수요가  =" + sLDemanderCd);

			logger.println(LogLevel.DEBUG, this, "=코일 우진도코드=" + sRProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 우주문번호=" + sROrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 우주문행번=" + sROrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 우수요가   =" + sRDemanderCd);

			logger.println(LogLevel.DEBUG, this, "=코일 좌적치상태=" + sLLayerStat);
			logger.println(LogLevel.DEBUG, this, "=코일 우적치상태=" + sRLayerStat);

			int iEmpty = Integer.parseInt(StringHelper.evl(ruleV
					.getFieldString("MXE" + sProgCd), "0"));

			if ("".equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_E.equals(sLLayerStat)) {
				iTotalN += iEmpty;
				logger.println(LogLevel.DEBUG, this, "★좌공백 평점:" + iEmpty);
			} else {

				if (sProgCd.equals(sLProgCd)) {
					iTotalN += iLPROG_CD;
					logger.println(LogLevel.DEBUG, this, "★좌진도 평점:" + iLPROG_CD);
				}
				if (sOrdNo.equals(sLOrdNo)) {
					iTotalN += iLORD_NO;
					logger.println(LogLevel.DEBUG, this, "★좌주문번호  평점:" + iLORD_NO);
				}
				if (sOrdNo.equals(sLOrdNo) && sOrdDtl.equals(sLOrdDtl)) {
					iTotalN += iLORD_DTL;
					logger.println(LogLevel.DEBUG, this, "★좌주문번호행번   평점:" + iLORD_DTL);
				}
				//수요가 평점(50점)
				if (sDemanderCd.equals(sLDemanderCd)) {
					iTotalN += 50;
					logger.println(LogLevel.DEBUG, this, "★좌수요가   평점:" + 50);
				}
			}

			if ("".equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_E.equals(sRLayerStat)) {
				iTotalN += iEmpty;
				logger.println(LogLevel.DEBUG, this, "★우공백 평점:" + iEmpty);
			} else {

				if (sProgCd.equals(sRProgCd)) {
					iTotalN += iRPROG_CD;
					logger.println(LogLevel.DEBUG, this, "★우진도 평점:" + iRPROG_CD);
				}
				if (sOrdNo.equals(sROrdNo)) {
					iTotalN += iRORD_NO;
					logger.println(LogLevel.DEBUG, this, "★우주문번호  평점:" + iRORD_NO);
				}
				if (sOrdNo.equals(sROrdNo) && sOrdDtl.equals(sROrdDtl)) {
					iTotalN += iRORD_DTL;
					logger.println(LogLevel.DEBUG, this, "★우주문번호행번   평점:" + iRORD_DTL);
				}
				//수요가 평점(50점)
				if (sDemanderCd.equals(sRDemanderCd)) {
					iTotalN += 50;
					logger.println(LogLevel.DEBUG, this, "★우수요가   평점:" + 50);
				}
			}
			if (YmCommonConst.STACK_LAYER_STAT_L.equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_P.equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_X.equals(sLLayerStat)) {
				iTotalN += iLSTAT;
				logger.println(LogLevel.DEBUG, this, "★좌상태   평점:" + iLSTAT);
			}
			if (YmCommonConst.STACK_LAYER_STAT_L.equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_P.equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_X.equals(sRLayerStat)) {
				iTotalN += iRSTAT;
				logger.println(LogLevel.DEBUG, this, "★우상태   평점:" + iRSTAT);
			}
			logger.println(LogLevel.DEBUG, this,
					"종합판정대기 01단 TO위치 적합성 체크 결과 종합점수=====" + iTotalN);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return iTotalN;
	}

	/**
	 * 종합판정대기 02단 TO위치 적합성 체크
	 * 
	 * @param JDTORecord :
	 *            TO위치정보
	 * @param JDTORecord :
	 *            기준정보
	 * 
	 * @return int 종합점수
	 * @throws Exception
	 */
	private int getCoilToLocInfo_006_G_02(JDTORecord stackV, JDTORecord ruleV)
			throws Exception {
		int iTotalN = 0;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			logger.println(LogLevel.DEBUG, this, "종합판정대기 02단 TO위치 적합성 체크=====");

			int iUPROG_CD = ruleV.getFieldInt("UPROG_CD");
			int iUORD_NO = ruleV.getFieldInt("UORD_NO");
			int iUORD_DTL = ruleV.getFieldInt("UORD_DTL");

			String sProgCd = StringHelper.evl(stackV
					.getFieldString("COIL_PROG_CD"), ""); // 진도코드
			String sOrdNo = StringHelper.evl(stackV
					.getFieldString("COIL_ORD_NO"), ""); // 주문번호
			String sOrdDtl = StringHelper.evl(stackV
					.getFieldString("COIL_ORD_DTL"), ""); // 주문행번

			String sLProgCd = StringHelper.evl(stackV
					.getFieldString("LCOIL_PROG_CD"), ""); // 진도코드
			String sLOrdNo = StringHelper.evl(stackV
					.getFieldString("LCOIL_ORD_NO"), ""); // 주문번호
			String sLOrdDtl = StringHelper.evl(stackV
					.getFieldString("LCOIL_ORD_DTL"), ""); // 주문행번

			String sRProgCd = StringHelper.evl(stackV
					.getFieldString("RCOIL_PROG_CD"), ""); // 진도코드
			String sROrdNo = StringHelper.evl(stackV
					.getFieldString("RCOIL_ORD_NO"), ""); // 주문번호
			String sROrdDtl = StringHelper.evl(stackV
					.getFieldString("RCOIL_ORD_DTL"), ""); // 주문행번

			String sDemanderCd = StringHelper.evl(stackV
					.getFieldString("COIL_DEMANDER_CD"), ""); // 수요가코드
			String sLDemanderCd = StringHelper.evl(stackV
					.getFieldString("LCOIL_DEMANDER_CD"), ""); // 수요가코드 
			String sRDemanderCd = StringHelper.evl(stackV
					.getFieldString("RCOIL_DEMANDER_CD"), ""); // 수요가코드 
			

			logger.println(LogLevel.DEBUG, this, "=코일 진도코드	=" + sProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 주문번호	=" + sOrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 주문행번	=" + sOrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 수요가		=" + sDemanderCd);

			logger.println(LogLevel.DEBUG, this, "=코일 좌진도코드=" + sLProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 좌주문번호=" + sLOrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 좌주문행번=" + sLOrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일  좌수요가  =" + sLDemanderCd);

			logger.println(LogLevel.DEBUG, this, "=코일 우진도코드=" + sRProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 우주문번호=" + sROrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 우주문행번=" + sROrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 우수요가   =" + sRDemanderCd);

			if (sProgCd.equals(sLProgCd)) {
				iTotalN += iUPROG_CD;
				logger.println(LogLevel.DEBUG, this, "★좌진도코드 평점:" + iUPROG_CD);
			}
			if (sOrdNo.equals(sLOrdNo)) {
				iTotalN += iUORD_NO;
				logger.println(LogLevel.DEBUG, this, "★좌주문번호 평점:" + iUORD_NO);
			}
			if (sOrdNo.equals(sLOrdNo) && sOrdDtl.equals(sLOrdDtl)) {
				iTotalN += iUORD_DTL;
				logger.println(LogLevel.DEBUG, this, "★좌주문번호행번 평점:" + iUORD_DTL);
			}
			//수요가 평점(50점)
			if (sDemanderCd.equals(sLDemanderCd)) {
				iTotalN += 50;
				logger.println(LogLevel.DEBUG, this, "★좌수요가 평점:" + 50);
			}

			if (sProgCd.equals(sRProgCd)) {
				iTotalN += iUPROG_CD;
				logger.println(LogLevel.DEBUG, this, "★우진도코드 평점:" + iUPROG_CD);
			}
			if (sOrdNo.equals(sROrdNo)) {
				iTotalN += iUORD_NO;
				logger.println(LogLevel.DEBUG, this, "★우주문번호 평점:" + iUORD_NO);
			}
			if (sOrdNo.equals(sROrdNo) && sOrdDtl.equals(sROrdDtl)) {
				iTotalN += iUORD_DTL;
				logger.println(LogLevel.DEBUG, this, "★우주문번호행번 평점:" + iUORD_DTL);
			}
			//수요가 평점(50점)
			if (sDemanderCd.equals(sRDemanderCd)) {
				iTotalN += 50;
				logger.println(LogLevel.DEBUG, this, "★우수요가 평점:" + 50);
			}

			logger.println(LogLevel.DEBUG, this,
					"종합판정대기 02단 TO위치 적합성 체크 결과 종합점수=====" + iTotalN);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return iTotalN;
	}

	/**
	 * 입고대기 01단 TO위치 적합성 체크
	 * 
	 * @param JDTORecord :
	 *            TO위치정보
	 * @param JDTORecord :
	 *            기준정보
	 * 
	 * @return int 종합점수
	 * @throws Exception
	 */
	private int getCoilToLocInfo_006_H_01(JDTORecord stackV, JDTORecord ruleV)
			throws Exception {
		int iTotalN = 0;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			logger.println(LogLevel.DEBUG, this, "입고대기 01단 TO위치 적합성 체크=====");

			int iLPROG_CD = ruleV.getFieldInt("LPROG_CD");
			int iLORD_NO = ruleV.getFieldInt("LORD_NO");
			int iLORD_DTL = ruleV.getFieldInt("LORD_DTL");
			int iLINPUT_DT = ruleV.getFieldInt("LINPUT_DT");
			int iLSTAT = ruleV.getFieldInt("LSTAT");

			int iRPROG_CD = ruleV.getFieldInt("RPROG_CD");
			int iRORD_NO = ruleV.getFieldInt("RORD_NO");
			int iRORD_DTL = ruleV.getFieldInt("RORD_DTL");
			int iRINPUT_DT = ruleV.getFieldInt("RINPUT_DT");
			int iRSTAT = ruleV.getFieldInt("RSTAT");

			String sProgCd = StringHelper.evl(stackV
					.getFieldString("COIL_PROG_CD"), ""); // 진도코드
			String sOrdNo = StringHelper.evl(stackV
					.getFieldString("COIL_ORD_NO"), ""); // 주문번호
			String sOrdDtl = StringHelper.evl(stackV
					.getFieldString("COIL_ORD_DTL"), ""); // 주문행번
			String sReceiptDt = StringHelper.evl(stackV
					.getFieldString("COIL_RECEIPT_DT"), ""); // 입고일자

			String sLProgCd = StringHelper.evl(stackV
					.getFieldString("LCOIL_PROG_CD"), ""); // 진도코드
			String sLOrdNo = StringHelper.evl(stackV
					.getFieldString("LCOIL_ORD_NO"), ""); // 주문번호
			String sLOrdDtl = StringHelper.evl(stackV
					.getFieldString("LCOIL_ORD_DTL"), ""); // 주문행번
			String sLReceiptDt = StringHelper.evl(stackV
					.getFieldString("LCOIL_RECEIPT_DT"), ""); // 입고일자

			String sRProgCd = StringHelper.evl(stackV
					.getFieldString("RCOIL_PROG_CD"), ""); // 진도코드
			String sROrdNo = StringHelper.evl(stackV
					.getFieldString("RCOIL_ORD_NO"), ""); // 주문번호
			String sROrdDtl = StringHelper.evl(stackV
					.getFieldString("RCOIL_ORD_DTL"), ""); // 주문행번
			String sRReceiptDt = StringHelper.evl(stackV
					.getFieldString("RCOIL_RECEIPT_DT"), ""); // 입고일자

			String sLLayerStat = StringHelper.evl(stackV
					.getFieldString("LEFT_STACK_LAYER_STAT"), "");
			String sRLayerStat = StringHelper.evl(stackV
					.getFieldString("RIGHT_STACK_LAYER_STAT"), "");

			String sDemanderCd = StringHelper.evl(stackV
					.getFieldString("COIL_DEMANDER_CD"), ""); // 수요가코드
			String sLDemanderCd = StringHelper.evl(stackV
					.getFieldString("LCOIL_DEMANDER_CD"), ""); // 수요가코드 
			String sRDemanderCd = StringHelper.evl(stackV
					.getFieldString("RCOIL_DEMANDER_CD"), ""); // 수요가코드 
			

			logger.println(LogLevel.DEBUG, this, "=코일 진도코드	=" + sProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 주문번호	=" + sOrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 주문행번	=" + sOrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 입고일자	=" + sReceiptDt);
			logger.println(LogLevel.DEBUG, this, "=코일 수요가		=" + sDemanderCd);

			logger.println(LogLevel.DEBUG, this, "=코일 좌진도코드=" + sLProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 좌주문번호=" + sLOrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 좌주문행번=" + sLOrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 좌입고일자=" + sLReceiptDt);
			logger.println(LogLevel.DEBUG, this, "=코일  좌수요가  =" + sLDemanderCd);

			logger.println(LogLevel.DEBUG, this, "=코일 우진도코드=" + sRProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 우주문번호=" + sROrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 우주문행번=" + sROrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 우입고일자=" + sRReceiptDt);
			logger.println(LogLevel.DEBUG, this, "=코일 우수요가   =" + sRDemanderCd);

			logger.println(LogLevel.DEBUG, this, "=코일 좌적치상태=" + sLLayerStat);
			logger.println(LogLevel.DEBUG, this, "=코일 우적치상태=" + sRLayerStat);

			int iEmpty = Integer.parseInt(StringHelper.evl(ruleV
					.getFieldString("MXE" + sProgCd), "0"));

			if ("".equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_E.equals(sLLayerStat)) {
				iTotalN += iEmpty;
				logger.println(LogLevel.DEBUG, this, "★좌공백 평점:" + iEmpty);
			} else {
				if (sProgCd.equals(sLProgCd)
						|| YmCommonConst.CURR_PROG_CD_COIL_K.equals(sLProgCd)) {
					iTotalN += iLPROG_CD;
					logger.println(LogLevel.DEBUG, this, "★좌진도 평점:" + iLPROG_CD);
				}
				if (sOrdNo.equals(sLOrdNo)) {
					iTotalN += iLORD_NO;
					logger.println(LogLevel.DEBUG, this, "★좌주문번호  평점:" + iLORD_NO);
				}
				if (sOrdNo.equals(sLOrdNo) && sOrdDtl.equals(sLOrdDtl)) {
					iTotalN += iLORD_DTL;
					logger.println(LogLevel.DEBUG, this, "★좌주문번호행번   평점:" + iLORD_DTL);
				}
				if (sReceiptDt.equals(sLReceiptDt)) {
					iTotalN += iLINPUT_DT;
					logger.println(LogLevel.DEBUG, this, "★좌입고일자   평점:" + iLINPUT_DT);
				}
				//수요가 평점(50점)
				if (sDemanderCd.equals(sLDemanderCd)) {
					iTotalN += 50;
					logger.println(LogLevel.DEBUG, this, "★좌수요가   평점:" + 50);
				}
			}

			if ("".equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_E.equals(sRLayerStat)) {
				iTotalN += iEmpty;
				logger.println(LogLevel.DEBUG, this, "★우공백 평점:" + iEmpty);
			} else {

				if (sProgCd.equals(sRProgCd)
						|| YmCommonConst.CURR_PROG_CD_COIL_K.equals(sRProgCd)) {
					iTotalN += iRPROG_CD;
					logger.println(LogLevel.DEBUG, this, "★우진도 평점:" + iRPROG_CD);
				}
				if (sOrdNo.equals(sROrdNo)) {
					iTotalN += iRORD_NO;
					logger.println(LogLevel.DEBUG, this, "★우주문번호  평점:" + iRORD_NO);
				}
				if (sOrdNo.equals(sROrdNo) && sOrdDtl.equals(sROrdDtl)) {
					iTotalN += iRORD_DTL;
					logger.println(LogLevel.DEBUG, this, "★우주문번호행번   평점:" + iRORD_DTL);
				}
				if (sReceiptDt.equals(sRReceiptDt)) {
					iTotalN += iRINPUT_DT;
					logger.println(LogLevel.DEBUG, this, "★우입고일자   평점:" + iRINPUT_DT);
				}
				//수요가 평점(50점)
				if (sDemanderCd.equals(sRDemanderCd)) {
					iTotalN += 50;
					logger.println(LogLevel.DEBUG, this, "★우수요가   평점:" + 50);
				}
			}

			if (YmCommonConst.STACK_LAYER_STAT_L.equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_P.equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_X.equals(sLLayerStat)) {
				iTotalN += iLSTAT;
				logger.println(LogLevel.DEBUG, this, "★좌상태   평점:" + iLSTAT);
			}
			if (YmCommonConst.STACK_LAYER_STAT_L.equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_P.equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_X.equals(sRLayerStat)) {
				iTotalN += iRSTAT;
				logger.println(LogLevel.DEBUG, this, "★우상태   평점:" + iRSTAT);
			}
			logger.println(LogLevel.DEBUG, this,
					"입고대기 01단 TO위치 적합성 체크 결과 종합점수=====" + iTotalN);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return iTotalN;
	}

	/**
	 * 입고대기 02단 TO위치 적합성 체크
	 * 
	 * @param JDTORecord :
	 *            TO위치정보
	 * @param JDTORecord :
	 *            기준정보
	 * 
	 * @return int 종합점수
	 * @throws Exception
	 */
	private int getCoilToLocInfo_006_H_02(JDTORecord stackV, JDTORecord ruleV)
			throws Exception {
		int iTotalN = 0;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			logger.println(LogLevel.DEBUG, this, "입고대기 02단 TO위치 적합성 체크=====");

			int iUPROG_CD = ruleV.getFieldInt("UPROG_CD");
			int iUORD_NO = ruleV.getFieldInt("UORD_NO");
			int iUORD_DTL = ruleV.getFieldInt("UORD_DTL");
			int iUINPUT_DT = ruleV.getFieldInt("UINPUT_DT");

			String sProgCd = StringHelper.evl(stackV
					.getFieldString("COIL_PROG_CD"), ""); // 진도코드
			String sOrdNo = StringHelper.evl(stackV
					.getFieldString("COIL_ORD_NO"), ""); // 주문번호
			String sOrdDtl = StringHelper.evl(stackV
					.getFieldString("COIL_ORD_DTL"), ""); // 주문행번
			String sReceiptDt = StringHelper.evl(stackV
					.getFieldString("COIL_RECEIPT_DT"), ""); // 입고일자

			String sLProgCd = StringHelper.evl(stackV
					.getFieldString("LCOIL_PROG_CD"), ""); // 진도코드
			String sLOrdNo = StringHelper.evl(stackV
					.getFieldString("LCOIL_ORD_NO"), ""); // 주문번호
			String sLOrdDtl = StringHelper.evl(stackV
					.getFieldString("LCOIL_ORD_DTL"), ""); // 주문행번
			String sLReceiptDt = StringHelper.evl(stackV
					.getFieldString("LCOIL_RECEIPT_DT"), ""); // 입고일자

			String sRProgCd = StringHelper.evl(stackV
					.getFieldString("RCOIL_PROG_CD"), ""); // 진도코드
			String sROrdNo = StringHelper.evl(stackV
					.getFieldString("RCOIL_ORD_NO"), ""); // 주문번호
			String sROrdDtl = StringHelper.evl(stackV
					.getFieldString("RCOIL_ORD_DTL"), ""); // 주문행번
			String sRReceiptDt = StringHelper.evl(stackV
					.getFieldString("RCOIL_RECEIPT_DT"), ""); // 입고일자

			String sDemanderCd = StringHelper.evl(stackV
					.getFieldString("COIL_DEMANDER_CD"), ""); // 수요가코드
			String sLDemanderCd = StringHelper.evl(stackV
					.getFieldString("LCOIL_DEMANDER_CD"), ""); // 수요가코드 
			String sRDemanderCd = StringHelper.evl(stackV
					.getFieldString("RCOIL_DEMANDER_CD"), ""); // 수요가코드 
			

			logger.println(LogLevel.DEBUG, this, "=코일 진도코드	=" + sProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 주문번호	=" + sOrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 주문행번	=" + sOrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 입고일자	=" + sReceiptDt);
			logger.println(LogLevel.DEBUG, this, "=코일 수요가		=" + sDemanderCd);

			logger.println(LogLevel.DEBUG, this, "=코일 좌진도코드=" + sLProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 좌주문번호=" + sLOrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 좌주문행번=" + sLOrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 좌입고일자=" + sLReceiptDt);
			logger.println(LogLevel.DEBUG, this, "=코일  좌수요가  =" + sLDemanderCd);

			logger.println(LogLevel.DEBUG, this, "=코일 우진도코드=" + sRProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 우주문번호=" + sROrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 우주문행번=" + sROrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 우입고일자=" + sRReceiptDt);
			logger.println(LogLevel.DEBUG, this, "=코일 우수요가   =" + sRDemanderCd);

			if (sProgCd.equals(sLProgCd)
					|| YmCommonConst.CURR_PROG_CD_COIL_K.equals(sLProgCd)) {
				iTotalN += iUPROG_CD;
				logger.println(LogLevel.DEBUG, this, "★좌진도 평점:" + iUPROG_CD);
			}
			if (sOrdNo.equals(sLOrdNo)) {
				iTotalN += iUORD_NO;
				logger.println(LogLevel.DEBUG, this, "★좌주문번호 평점:" + iUORD_NO);
			}
			if (sOrdNo.equals(sLOrdNo) && sOrdDtl.equals(sLOrdDtl)) {
				iTotalN += iUORD_DTL;
				logger.println(LogLevel.DEBUG, this, "★좌주문번호행번 평점:" + iUORD_DTL);
			}
			if (sReceiptDt.equals(sLReceiptDt)) {
				iTotalN += iUINPUT_DT;
				logger.println(LogLevel.DEBUG, this, "★좌입고일자 평점:" + iUINPUT_DT);
			}
			//수요가 평점(50점)
			if (sDemanderCd.equals(sLDemanderCd)) {
				iTotalN += 50;
				logger.println(LogLevel.DEBUG, this, "★좌수요가 평점:" + 50);
			}

			if (sProgCd.equals(sRProgCd)
					|| YmCommonConst.CURR_PROG_CD_COIL_K.equals(sRProgCd)) {
				iTotalN += iUPROG_CD;
				logger.println(LogLevel.DEBUG, this, "★우진도 평점:" + iUPROG_CD);
			}
			if (sOrdNo.equals(sROrdNo)) {
				iTotalN += iUORD_NO;
				logger.println(LogLevel.DEBUG, this, "★우주문번호 평점:" + iUORD_NO);
			}
			if (sOrdNo.equals(sROrdNo) && sOrdDtl.equals(sROrdDtl)) {
				iTotalN += iUORD_DTL;
				logger.println(LogLevel.DEBUG, this, "★우주문번호행번 평점:" + iUORD_DTL);
			}
			if (sReceiptDt.equals(sRReceiptDt)) {
				iTotalN += iUINPUT_DT;
				logger.println(LogLevel.DEBUG, this, "★우입고일자 평점:" + iUINPUT_DT);
			}
			//수요가 평점(50점)
			if (sDemanderCd.equals(sRDemanderCd)) {
				iTotalN += 50;
				logger.println(LogLevel.DEBUG, this, "★우수요가 평점:" + 50);
			}

			logger.println(LogLevel.DEBUG, this,
					"입고대기 02단 TO위치 적합성 체크 결과 종합점수=====" + iTotalN);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return iTotalN;
	}

	/**
	 * 반납대기 01단 TO위치 적합성 체크
	 * 
	 * @param JDTORecord :
	 *            TO위치정보
	 * @param JDTORecord :
	 *            기준정보
	 * 
	 * @return int 종합점수
	 * @throws Exception
	 */
	private int getCoilToLocInfo_006_J_01(JDTORecord stackV, JDTORecord ruleV)
			throws Exception {
		int iTotalN = 0;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			logger.println(LogLevel.DEBUG, this, "반납대기 01단 TO위치 적합성 체크=====");

			int iLPROG_CD = ruleV.getFieldInt("LPROG_CD");
			int iLSTAT = ruleV.getFieldInt("LSTAT");

			int iRPROG_CD = ruleV.getFieldInt("RPROG_CD");
			int iRSTAT = ruleV.getFieldInt("RSTAT");

			String sProgCd = StringHelper.evl(stackV
					.getFieldString("COIL_PROG_CD"), ""); // 진도코드

			String sLProgCd = StringHelper.evl(stackV
					.getFieldString("LCOIL_PROG_CD"), ""); // 진도코드

			String sRProgCd = StringHelper.evl(stackV
					.getFieldString("RCOIL_PROG_CD"), ""); // 진도코드

			String sLLayerStat = StringHelper.evl(stackV
					.getFieldString("LEFT_STACK_LAYER_STAT"), "");
			String sRLayerStat = StringHelper.evl(stackV
					.getFieldString("RIGHT_STACK_LAYER_STAT"), "");

			logger.println(LogLevel.DEBUG, this, "=코일 진도코드	=" + sProgCd);

			logger.println(LogLevel.DEBUG, this, "=코일 좌진도코드=" + sLProgCd);

			logger.println(LogLevel.DEBUG, this, "=코일 우진도코드=" + sRProgCd);

			logger.println(LogLevel.DEBUG, this, "=코일 좌적치상태=" + sLLayerStat);
			logger.println(LogLevel.DEBUG, this, "=코일 우적치상태=" + sRLayerStat);

			int iEmpty = Integer.parseInt(StringHelper.evl(ruleV
					.getFieldString("MXE" + sProgCd), "0"));

			if ("".equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_E.equals(sLLayerStat)) {
				iTotalN += iEmpty;
			} else {

				if (sProgCd.equals(sLProgCd)) {
					iTotalN += iLPROG_CD;
				}
			}

			if ("".equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_E.equals(sRLayerStat)) {
				iTotalN += iEmpty;
			} else {
				if (sProgCd.equals(sRProgCd)) {
					iTotalN += iRPROG_CD;
				}
			}

			if (YmCommonConst.STACK_LAYER_STAT_L.equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_P.equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_X.equals(sLLayerStat)) {
				iTotalN += iLSTAT;
			}
			if (YmCommonConst.STACK_LAYER_STAT_L.equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_P.equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_X.equals(sRLayerStat)) {
				iTotalN += iRSTAT;
			}
			logger.println(LogLevel.DEBUG, this,
					"반납대기 01단 TO위치 적합성 체크 결과 종합점수=====" + iTotalN);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return iTotalN;
	}

	/**
	 * 반납대기 02단 TO위치 적합성 체크
	 * 
	 * @param JDTORecord :
	 *            TO위치정보
	 * @param JDTORecord :
	 *            기준정보
	 * 
	 * @return int 종합점수
	 * @throws Exception
	 */
	private int getCoilToLocInfo_006_J_02(JDTORecord stackV, JDTORecord ruleV)
			throws Exception {
		int iTotalN = 0;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			logger.println(LogLevel.DEBUG, this, "반납대기 02단 TO위치 적합성 체크=====");

			int iUPROG_CD = ruleV.getFieldInt("UPROG_CD");

			String sProgCd = StringHelper.evl(stackV
					.getFieldString("COIL_PROG_CD"), ""); // 진도코드

			String sLProgCd = StringHelper.evl(stackV
					.getFieldString("LCOIL_PROG_CD"), ""); // 진도코드

			String sRProgCd = StringHelper.evl(stackV
					.getFieldString("RCOIL_PROG_CD"), ""); // 진도코드

			logger.println(LogLevel.DEBUG, this, "=코일 진도코드	=" + sProgCd);

			logger.println(LogLevel.DEBUG, this, "=코일 좌진도코드=" + sLProgCd);

			logger.println(LogLevel.DEBUG, this, "=코일 우진도코드=" + sRProgCd);

			if (sProgCd.equals(sLProgCd)) {
				iTotalN += iUPROG_CD;
			}

			if (sProgCd.equals(sRProgCd)) {
				iTotalN += iUPROG_CD;
			}

			logger.println(LogLevel.DEBUG, this,
					"반납대기 02단 TO위치 적합성 체크 결과 종합점수=====" + iTotalN);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return iTotalN;
	}

	/**
	 * 출하작업지시대기 01단 TO위치 적합성 체크
	 * 
	 * @param JDTORecord :
	 *            TO위치정보
	 * @param JDTORecord :
	 *            기준정보
	 * 
	 * @return int 종합점수
	 * @throws Exception
	 */
	private int getCoilToLocInfo_006_K_01(JDTORecord stackV, JDTORecord ruleV)
			throws Exception {
		int iTotalN = 0;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			logger.println(LogLevel.DEBUG, this,
					"출하작업지시대기 01단 TO위치 적합성 체크=====");

			int iLPROG_CD = ruleV.getFieldInt("LPROG_CD");
			int iLORD_NO = ruleV.getFieldInt("LORD_NO");
			int iLORD_DTL = ruleV.getFieldInt("LORD_DTL");
			int iLINPUT_DT = ruleV.getFieldInt("LINPUT_DT");
			int iLSTAT = ruleV.getFieldInt("LSTAT");

			int iRPROG_CD = ruleV.getFieldInt("RPROG_CD");
			int iRORD_NO = ruleV.getFieldInt("RORD_NO");
			int iRORD_DTL = ruleV.getFieldInt("RORD_DTL");
			int iRINPUT_DT = ruleV.getFieldInt("RINPUT_DT");
			int iRSTAT = ruleV.getFieldInt("RSTAT");

			String sProgCd = StringHelper.evl(stackV
					.getFieldString("COIL_PROG_CD"), ""); // 진도코드
			String sOrdNo = StringHelper.evl(stackV
					.getFieldString("COIL_ORD_NO"), ""); // 주문번호
			String sOrdDtl = StringHelper.evl(stackV
					.getFieldString("COIL_ORD_DTL"), ""); // 주문행번
			String sReceiptDt = StringHelper.evl(stackV
					.getFieldString("COIL_RECEIPT_DT"), ""); // 입고일자

			String sLProgCd = StringHelper.evl(stackV
					.getFieldString("LCOIL_PROG_CD"), ""); // 진도코드
			String sLOrdNo = StringHelper.evl(stackV
					.getFieldString("LCOIL_ORD_NO"), ""); // 주문번호
			String sLOrdDtl = StringHelper.evl(stackV
					.getFieldString("LCOIL_ORD_DTL"), ""); // 주문행번
			String sLReceiptDt = StringHelper.evl(stackV
					.getFieldString("LCOIL_RECEIPT_DT"), ""); // 입고일자

			String sRProgCd = StringHelper.evl(stackV
					.getFieldString("RCOIL_PROG_CD"), ""); // 진도코드
			String sROrdNo = StringHelper.evl(stackV
					.getFieldString("RCOIL_ORD_NO"), ""); // 주문번호
			String sROrdDtl = StringHelper.evl(stackV
					.getFieldString("RCOIL_ORD_DTL"), ""); // 주문행번
			String sRReceiptDt = StringHelper.evl(stackV
					.getFieldString("RCOIL_RECEIPT_DT"), ""); // 입고일자
			
			String sDemanderCd = StringHelper.evl(stackV
					.getFieldString("COIL_DEMANDER_CD"), ""); // 수요가코드
			String sLDemanderCd = StringHelper.evl(stackV
					.getFieldString("LCOIL_DEMANDER_CD"), ""); // 수요가코드 
			String sRDemanderCd = StringHelper.evl(stackV
					.getFieldString("RCOIL_DEMANDER_CD"), ""); // 수요가코드 
			
			String sLLayerStat = StringHelper.evl(stackV
					.getFieldString("LEFT_STACK_LAYER_STAT"), "");
			String sRLayerStat = StringHelper.evl(stackV
					.getFieldString("RIGHT_STACK_LAYER_STAT"), "");

			logger.println(LogLevel.DEBUG, this, "=코일 진도코드	=" + sProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 주문번호	=" + sOrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 주문행번	=" + sOrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 입고일자	=" + sReceiptDt);
			logger.println(LogLevel.DEBUG, this, "=코일 수요가		=" + sDemanderCd);

			logger.println(LogLevel.DEBUG, this, "=코일 좌진도코드=" + sLProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 좌주문번호=" + sLOrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 좌주문행번=" + sLOrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 좌입고일자=" + sLReceiptDt);
			logger.println(LogLevel.DEBUG, this, "=코일  좌수요가  =" + sLDemanderCd);

			logger.println(LogLevel.DEBUG, this, "=코일 우진도코드=" + sRProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 우주문번호=" + sROrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 우주문행번=" + sROrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 우입고일자=" + sRReceiptDt);
			logger.println(LogLevel.DEBUG, this, "=코일 우수요가   =" + sRDemanderCd);

			logger.println(LogLevel.DEBUG, this, "=코일 좌적치상태=" + sLLayerStat);
			logger.println(LogLevel.DEBUG, this, "=코일 우적치상태=" + sRLayerStat);

			int iEmpty = Integer.parseInt(StringHelper.evl(ruleV
					.getFieldString("MXE" + sProgCd), "0"));

			if ("".equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_E.equals(sLLayerStat)) {
				iTotalN += iEmpty;
				logger.println(LogLevel.DEBUG, this, "★좌공백 평점:" + iEmpty);
			} else {
				if (sProgCd.equals(sLProgCd)) {
					iTotalN += iLPROG_CD;
					logger.println(LogLevel.DEBUG, this, "★좌진도 평점:" + iLPROG_CD);
				}
				if (sOrdNo.equals(sLOrdNo)) {
					iTotalN += iLORD_NO;
					logger.println(LogLevel.DEBUG, this, "★좌주문번호  평점:" + iLORD_NO);
				}
				if (sOrdNo.equals(sLOrdNo) && sOrdDtl.equals(sLOrdDtl)) {
					iTotalN += iLORD_DTL;
					logger.println(LogLevel.DEBUG, this, "★좌주문번호행번   평점:" + iLORD_DTL);
				}
				if (sReceiptDt.equals(sLReceiptDt)) {
					iTotalN += iLINPUT_DT;
					logger.println(LogLevel.DEBUG, this, "★좌입고일자   평점:" + iLINPUT_DT);
				}
				//수요가 평점(50점)
				if (sDemanderCd.equals(sLDemanderCd)) {
					iTotalN += 50;
					logger.println(LogLevel.DEBUG, this, "★좌수요가   평점:" + 50);
				}
			}

			if ("".equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_E.equals(sRLayerStat)) {
				iTotalN += iEmpty;
				logger.println(LogLevel.DEBUG, this, "★우공백 평점:" + iEmpty);
			} else {
				if (sProgCd.equals(sRProgCd)) {
					iTotalN += iRPROG_CD;
					logger.println(LogLevel.DEBUG, this, "★우진도 평점:" + iRPROG_CD);
				}
				if (sOrdNo.equals(sROrdNo)) {
					iTotalN += iRORD_NO;
					logger.println(LogLevel.DEBUG, this, "★우주문번호  평점:" + iRORD_NO);
				}
				if (sOrdNo.equals(sROrdNo) && sOrdDtl.equals(sROrdDtl)) {
					iTotalN += iRORD_DTL;
					logger.println(LogLevel.DEBUG, this, "★우주문번호행번   평점:" + iRORD_DTL);
				}
				if (sReceiptDt.equals(sRReceiptDt)) {
					iTotalN += iRINPUT_DT;
					logger.println(LogLevel.DEBUG, this, "★우입고일자   평점:" + iRINPUT_DT);
				}
				//수요가 평점(50점)
				if (sDemanderCd.equals(sRDemanderCd)) {
					iTotalN += 50;
					logger.println(LogLevel.DEBUG, this, "★우수요가   평점:" + 50);
				}
			}

			if (YmCommonConst.STACK_LAYER_STAT_L.equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_P.equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_X.equals(sLLayerStat)) {
				iTotalN += iLSTAT;
				logger.println(LogLevel.DEBUG, this, "★좌상태   평점:" + iLSTAT);
			}
			if (YmCommonConst.STACK_LAYER_STAT_L.equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_P.equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_X.equals(sRLayerStat)) {
				iTotalN += iRSTAT;
				logger.println(LogLevel.DEBUG, this, "★우상태   평점:" + iRSTAT);
			}
			logger.println(LogLevel.DEBUG, this,
					"출하작업지시대기 01단 TO위치 적합성 체크 결과 종합점수=====" + iTotalN);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return iTotalN;
	}

	/**
	 * 출하작업지시대기 02단 TO위치 적합성 체크
	 * 
	 * @param JDTORecord :
	 *            TO위치정보
	 * @param JDTORecord :
	 *            기준정보
	 * 
	 * @return int 종합점수
	 * @throws Exception
	 */
	private int getCoilToLocInfo_006_K_02(JDTORecord stackV, JDTORecord ruleV)
			throws Exception {
		int iTotalN = 0;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			logger.println(LogLevel.DEBUG, this,
					"출하작업지시대기 02단 TO위치 적합성 체크=====");

			int iUPROG_CD = ruleV.getFieldInt("UPROG_CD");
			int iUORD_NO = ruleV.getFieldInt("UORD_NO");
			int iUORD_DTL = ruleV.getFieldInt("UORD_DTL");
			int iUINPUT_DT = ruleV.getFieldInt("UINPUT_DT");

			String sProgCd = StringHelper.evl(stackV
					.getFieldString("COIL_PROG_CD"), ""); // 진도코드
			String sOrdNo = StringHelper.evl(stackV
					.getFieldString("COIL_ORD_NO"), ""); // 주문번호
			String sOrdDtl = StringHelper.evl(stackV
					.getFieldString("COIL_ORD_DTL"), ""); // 주문행번
			String sReceiptDt = StringHelper.evl(stackV
					.getFieldString("COIL_RECEIPT_DT"), ""); // 입고일자

			String sLProgCd = StringHelper.evl(stackV
					.getFieldString("LCOIL_PROG_CD"), ""); // 진도코드
			String sLOrdNo = StringHelper.evl(stackV
					.getFieldString("LCOIL_ORD_NO"), ""); // 주문번호
			String sLOrdDtl = StringHelper.evl(stackV
					.getFieldString("LCOIL_ORD_DTL"), ""); // 주문행번
			String sLReceiptDt = StringHelper.evl(stackV
					.getFieldString("LCOIL_RECEIPT_DT"), ""); // 입고일자

			String sRProgCd = StringHelper.evl(stackV
					.getFieldString("RCOIL_PROG_CD"), ""); // 진도코드
			String sROrdNo = StringHelper.evl(stackV
					.getFieldString("RCOIL_ORD_NO"), ""); // 주문번호
			String sROrdDtl = StringHelper.evl(stackV
					.getFieldString("RCOIL_ORD_DTL"), ""); // 주문행번
			String sRReceiptDt = StringHelper.evl(stackV
					.getFieldString("RCOIL_RECEIPT_DT"), ""); // 입고일자
			
			String sDemanderCd = StringHelper.evl(stackV
					.getFieldString("COIL_DEMANDER_CD"), ""); // 수요가코드
			String sLDemanderCd = StringHelper.evl(stackV
					.getFieldString("LCOIL_DEMANDER_CD"), ""); // 수요가코드 
			String sRDemanderCd = StringHelper.evl(stackV
					.getFieldString("RCOIL_DEMANDER_CD"), ""); // 수요가코드 
			

			logger.println(LogLevel.DEBUG, this, "=코일 진도코드	=" + sProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 주문번호	=" + sOrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 주문행번	=" + sOrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 입고일자	=" + sReceiptDt);
			logger.println(LogLevel.DEBUG, this, "=코일 수요가		=" + sDemanderCd);

			logger.println(LogLevel.DEBUG, this, "=코일 좌진도코드=" + sLProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 좌주문번호=" + sLOrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 좌주문행번=" + sLOrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 좌입고일자=" + sLReceiptDt);
			logger.println(LogLevel.DEBUG, this, "=코일  좌수요가  =" + sLDemanderCd);

			logger.println(LogLevel.DEBUG, this, "=코일 우진도코드=" + sRProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 우주문번호=" + sROrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 우주문행번=" + sROrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 우입고일자=" + sRReceiptDt);
			logger.println(LogLevel.DEBUG, this, "=코일 우수요가   =" + sRDemanderCd);

			if (sProgCd.equals(sLProgCd)) {
				iTotalN += iUPROG_CD;
				logger.println(LogLevel.DEBUG, this, "★좌진도 평점:" + iUPROG_CD);
			}
			if (sOrdNo.equals(sLOrdNo)) {
				iTotalN += iUORD_NO;
				logger.println(LogLevel.DEBUG, this, "★좌주문번호 평점:" + iUORD_NO);
			}
			if (sOrdNo.equals(sLOrdNo) && sOrdDtl.equals(sLOrdDtl)) {
				iTotalN += iUORD_DTL;
				logger.println(LogLevel.DEBUG, this, "★좌주문행번 평점:" + iUORD_DTL);
			}
			if (sReceiptDt.equals(sLReceiptDt)) {
				iTotalN += iUINPUT_DT;
				logger.println(LogLevel.DEBUG, this, "★좌입고일자 평점:" + iUINPUT_DT);
			}
//			수요가 평점(50점)
			if (sDemanderCd.equals(sLDemanderCd)) {
				iTotalN += 50;
				logger.println(LogLevel.DEBUG, this, "★좌수요가 평점:" + 50);
			}

			if (sProgCd.equals(sRProgCd)) {
				iTotalN += iUPROG_CD;
				logger.println(LogLevel.DEBUG, this, "★우진도 평점:" + iUPROG_CD);
			}
			if (sOrdNo.equals(sROrdNo)) {
				iTotalN += iUORD_NO;
				logger.println(LogLevel.DEBUG, this, "★우주문번호 평점:" + iUORD_NO);
			}
			if (sOrdNo.equals(sROrdNo) && sOrdDtl.equals(sROrdDtl)) {
				iTotalN += iUORD_DTL;
				logger.println(LogLevel.DEBUG, this, "★우주문행번 평점:" + iUORD_DTL);
			}
			if (sReceiptDt.equals(sRReceiptDt)) {
				iTotalN += iUINPUT_DT;
				logger.println(LogLevel.DEBUG, this, "★우입고일자 평점:" + iUINPUT_DT);
			}
//			수요가 평점(50점)
			if (sDemanderCd.equals(sRDemanderCd)) {
				iTotalN += 50;
				logger.println(LogLevel.DEBUG, this, "★우수요가 평점:" + 50);
			}

			logger.println(LogLevel.DEBUG, this,
					"출하작업지시대기 02단 TO위치 적합성 체크 결과 종합점수=====" + iTotalN);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return iTotalN;
	}

	/**
	 * 출하작업대기 01단 TO위치 적합성 체크
	 * 
	 * @param JDTORecord :
	 *            TO위치정보
	 * @param JDTORecord :
	 *            기준정보
	 * 
	 * @return int 종합점수
	 * @throws Exception
	 */
	private int getCoilToLocInfo_006_L_01(JDTORecord stackV, JDTORecord ruleV)
			throws Exception {
		int iTotalN = 0;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			logger.println(LogLevel.DEBUG, this, "출하작업대기 01단 TO위치 적합성 체크=====");

			int iLPROG_CD = ruleV.getFieldInt("LPROG_CD");
			int iLORD_NO = ruleV.getFieldInt("LORD_NO");
			int iLORD_DTL = ruleV.getFieldInt("LORD_DTL");
			int iLINPUT_DT = ruleV.getFieldInt("LINPUT_DT");
			int iLCARD_NO = ruleV.getFieldInt("LCARD_NO");
			int iLSTAT = ruleV.getFieldInt("LSTAT");

			int iRPROG_CD = ruleV.getFieldInt("RPROG_CD");
			int iRORD_NO = ruleV.getFieldInt("RORD_NO");
			int iRORD_DTL = ruleV.getFieldInt("RORD_DTL");
			int iRINPUT_DT = ruleV.getFieldInt("RINPUT_DT");
			int iRCARD_NO = ruleV.getFieldInt("RCARD_NO");
			int iRSTAT = ruleV.getFieldInt("RSTAT");

			String sProgCd = StringHelper.evl(stackV
					.getFieldString("COIL_PROG_CD"), ""); // 진도코드
			String sOrdNo = StringHelper.evl(stackV
					.getFieldString("COIL_ORD_NO"), ""); // 주문번호
			String sOrdDtl = StringHelper.evl(stackV
					.getFieldString("COIL_ORD_DTL"), ""); // 주문행번
			String sReceiptDt = StringHelper.evl(stackV
					.getFieldString("COIL_RECEIPT_DT"), ""); // 입고일자
			String sCardNo = StringHelper.evl(stackV
					.getFieldString("COIL_CARD_NO"), ""); // 카드NO

			String sLProgCd = StringHelper.evl(stackV
					.getFieldString("LCOIL_PROG_CD"), ""); // 진도코드
			String sLOrdNo = StringHelper.evl(stackV
					.getFieldString("LCOIL_ORD_NO"), ""); // 주문번호
			String sLOrdDtl = StringHelper.evl(stackV
					.getFieldString("LCOIL_ORD_DTL"), ""); // 주문행번
			String sLReceiptDt = StringHelper.evl(stackV
					.getFieldString("LCOIL_RECEIPT_DT"), ""); // 입고일자
			String sLCardNo = StringHelper.evl(stackV
					.getFieldString("LCOIL_CARD_NO"), ""); // 카드NO

			String sRProgCd = StringHelper.evl(stackV
					.getFieldString("RCOIL_PROG_CD"), ""); // 진도코드
			String sROrdNo = StringHelper.evl(stackV
					.getFieldString("RCOIL_ORD_NO"), ""); // 주문번호
			String sROrdDtl = StringHelper.evl(stackV
					.getFieldString("RCOIL_ORD_DTL"), ""); // 주문행번
			String sRReceiptDt = StringHelper.evl(stackV
					.getFieldString("RCOIL_RECEIPT_DT"), ""); // 입고일자
			String sRCardNo = StringHelper.evl(stackV
					.getFieldString("RCOIL_CARD_NO"), ""); // 카드NO

			String sLLayerStat = StringHelper.evl(stackV
					.getFieldString("LEFT_STACK_LAYER_STAT"), "");
			String sRLayerStat = StringHelper.evl(stackV
					.getFieldString("RIGHT_STACK_LAYER_STAT"), "");
			
			String sDemanderCd = StringHelper.evl(stackV
					.getFieldString("COIL_DEMANDER_CD"), ""); // 수요가코드
			String sLDemanderCd = StringHelper.evl(stackV
					.getFieldString("LCOIL_DEMANDER_CD"), ""); // 수요가코드 
			String sRDemanderCd = StringHelper.evl(stackV
					.getFieldString("RCOIL_DEMANDER_CD"), ""); // 수요가코드 

			logger.println(LogLevel.DEBUG, this, "=코일 진도코드	=" + sProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 주문번호	=" + sOrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 주문행번	=" + sOrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 입고일자	=" + sReceiptDt);
			logger.println(LogLevel.DEBUG, this, "=코일 카드No	=" + sCardNo);
			logger.println(LogLevel.DEBUG, this, "=코일 수요가		=" + sDemanderCd);

			logger.println(LogLevel.DEBUG, this, "=코일 좌진도코드=" + sLProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 좌주문번호=" + sLOrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 좌주문행번=" + sLOrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 좌입고일자=" + sLReceiptDt);
			logger.println(LogLevel.DEBUG, this, "=코일 좌카드No =" + sLCardNo);
			logger.println(LogLevel.DEBUG, this, "=코일 좌수요가   =" + sLDemanderCd);

			logger.println(LogLevel.DEBUG, this, "=코일 우진도코드=" + sRProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 우주문번호=" + sROrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 우주문행번=" + sROrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 우입고일자=" + sRReceiptDt);
			logger.println(LogLevel.DEBUG, this, "=코일 우카드No =" + sRCardNo);
			logger.println(LogLevel.DEBUG, this, "=코일 우수요가   =" + sRDemanderCd);

			logger.println(LogLevel.DEBUG, this, "=코일 좌적치상태=" + sLLayerStat);
			logger.println(LogLevel.DEBUG, this, "=코일 우적치상태=" + sRLayerStat);

			int iEmpty = Integer.parseInt(StringHelper.evl(ruleV
					.getFieldString("MXE" + sProgCd), "0"));

			if ("".equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_E.equals(sLLayerStat)) {
				iTotalN += iEmpty;
				logger.println(LogLevel.DEBUG, this, "★좌공백 평점:" + iEmpty);
			} else {
				if (sProgCd.equals(sLProgCd)) {
					iTotalN += iLPROG_CD;
					logger.println(LogLevel.DEBUG, this, "★좌진도 평점:" + iLPROG_CD);
				}
				if (sOrdNo.equals(sLOrdNo)) {
					iTotalN += iLORD_NO;
					logger.println(LogLevel.DEBUG, this, "★좌주문번호  평점:" + iLORD_NO);
				}
				if (sOrdNo.equals(sLOrdNo) && sOrdDtl.equals(sLOrdDtl)) {
					iTotalN += iLORD_DTL;
					logger.println(LogLevel.DEBUG, this, "★좌주문번호행번   평점:" + iLORD_DTL);
				}
				if (sReceiptDt.equals(sLReceiptDt)) {
					iTotalN += iLINPUT_DT;
					logger.println(LogLevel.DEBUG, this, "★좌입고일자  평점:" + iLORD_NO);
				}
				if (sCardNo.equals(sLCardNo)) {
					iTotalN += iLCARD_NO;
					logger.println(LogLevel.DEBUG, this, "★좌카드No  평점:" + iLCARD_NO);
				}
//				수요가 평점(50점)
				if (sDemanderCd.equals(sLDemanderCd)) {
					iTotalN += 50;
					logger.println(LogLevel.DEBUG, this, "★좌수요가   평점:" + 50);
				}
			}

			if ("".equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_E.equals(sRLayerStat)) {
				iTotalN += iEmpty;
				logger.println(LogLevel.DEBUG, this, "★우공백 평점:" + iEmpty);
			} else {
				if (sProgCd.equals(sRProgCd)) {
					iTotalN += iRPROG_CD;
					logger.println(LogLevel.DEBUG, this, "★우진도 평점:" + iRPROG_CD);
				}
				if (sOrdNo.equals(sROrdNo)) {
					iTotalN += iRORD_NO;
					logger.println(LogLevel.DEBUG, this, "★우주문번호  평점:" + iRORD_NO);
				}
				if (sOrdNo.equals(sROrdNo) && sOrdDtl.equals(sROrdDtl)) {
					iTotalN += iRORD_DTL;
					logger.println(LogLevel.DEBUG, this, "★우주문번호행번   평점:" + iRORD_DTL);
				}
				if (sReceiptDt.equals(sRReceiptDt)) {
					iTotalN += iRINPUT_DT;
					logger.println(LogLevel.DEBUG, this, "★우입고일자  평점:" + iRINPUT_DT);
				}
				if (sCardNo.equals(sRCardNo)) {
					iTotalN += iRCARD_NO;
					logger.println(LogLevel.DEBUG, this, "★우카드No  평점:" + iRCARD_NO);
				}
//				수요가 평점(50점)
				if (sDemanderCd.equals(sRDemanderCd)) {
					iTotalN += 50;
					logger.println(LogLevel.DEBUG, this, "★우수요가   평점:" + 50);
				}
			}

			if (YmCommonConst.STACK_LAYER_STAT_L.equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_P.equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_X.equals(sLLayerStat)) {
				iTotalN += iLSTAT;
				logger.println(LogLevel.DEBUG, this, "★좌상태   평점:" + iLSTAT);
			}
			if (YmCommonConst.STACK_LAYER_STAT_L.equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_P.equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_X.equals(sRLayerStat)) {
				iTotalN += iRSTAT;
				logger.println(LogLevel.DEBUG, this, "★우상태   평점:" + iRSTAT);
			}
			logger.println(LogLevel.DEBUG, this,
					"출하작업대기 01단 TO위치 적합성 체크 결과 종합점수=====" + iTotalN);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return iTotalN;
	}

	/**
	 * 출하작업대기 02단 TO위치 적합성 체크
	 * 
	 * @param JDTORecord :
	 *            TO위치정보
	 * @param JDTORecord :
	 *            기준정보
	 * 
	 * @return int 종합점수
	 * @throws Exception
	 */
	private int getCoilToLocInfo_006_L_02(JDTORecord stackV, JDTORecord ruleV)
			throws Exception {
		int iTotalN = 0;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			logger.println(LogLevel.DEBUG, this, "출하작업대기 02단 TO위치 적합성 체크=====");

			int iUPROG_CD = ruleV.getFieldInt("UPROG_CD");
			int iUORD_NO = ruleV.getFieldInt("UORD_NO");
			int iUORD_DTL = ruleV.getFieldInt("UORD_DTL");
			int iUINPUT_DT = ruleV.getFieldInt("UINPUT_DT");
			int iUCARD_NO = ruleV.getFieldInt("UCARD_NO");

			String sProgCd = StringHelper.evl(stackV
					.getFieldString("COIL_PROG_CD"), ""); // 진도코드
			String sOrdNo = StringHelper.evl(stackV
					.getFieldString("COIL_ORD_NO"), ""); // 주문번호
			String sOrdDtl = StringHelper.evl(stackV
					.getFieldString("COIL_ORD_DTL"), ""); // 주문행번
			String sReceiptDt = StringHelper.evl(stackV
					.getFieldString("COIL_RECEIPT_DT"), ""); // 입고일자
			String sCardNo = StringHelper.evl(stackV
					.getFieldString("COIL_CARD_NO"), ""); // 카드NO

			String sLProgCd = StringHelper.evl(stackV
					.getFieldString("LCOIL_PROG_CD"), ""); // 진도코드
			String sLOrdNo = StringHelper.evl(stackV
					.getFieldString("LCOIL_ORD_NO"), ""); // 주문번호
			String sLOrdDtl = StringHelper.evl(stackV
					.getFieldString("LCOIL_ORD_DTL"), ""); // 주문행번
			String sLReceiptDt = StringHelper.evl(stackV
					.getFieldString("LCOIL_RECEIPT_DT"), ""); // 입고일자
			String sLCardNo = StringHelper.evl(stackV
					.getFieldString("LCOIL_CARD_NO"), ""); // 카드NO

			String sRProgCd = StringHelper.evl(stackV
					.getFieldString("RCOIL_PROG_CD"), ""); // 진도코드
			String sROrdNo = StringHelper.evl(stackV
					.getFieldString("RCOIL_ORD_NO"), ""); // 주문번호
			String sROrdDtl = StringHelper.evl(stackV
					.getFieldString("RCOIL_ORD_DTL"), ""); // 주문행번
			String sRReceiptDt = StringHelper.evl(stackV
					.getFieldString("RCOIL_RECEIPT_DT"), ""); // 입고일자
			String sRCardNo = StringHelper.evl(stackV
					.getFieldString("RCOIL_CARD_NO"), ""); // 카드NO

			String sDemanderCd = StringHelper.evl(stackV
					.getFieldString("COIL_DEMANDER_CD"), ""); // 수요가코드
			String sLDemanderCd = StringHelper.evl(stackV
					.getFieldString("LCOIL_DEMANDER_CD"), ""); // 수요가코드 
			String sRDemanderCd = StringHelper.evl(stackV
					.getFieldString("RCOIL_DEMANDER_CD"), ""); // 수요가코드 

			logger.println(LogLevel.DEBUG, this, "=코일 진도코드	=" + sProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 주문번호	=" + sOrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 주문행번	=" + sOrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 입고일자	=" + sReceiptDt);
			logger.println(LogLevel.DEBUG, this, "=코일 카드No	=" + sCardNo);
			logger.println(LogLevel.DEBUG, this, "=코일 수요가		=" + sDemanderCd);

			logger.println(LogLevel.DEBUG, this, "=코일 좌진도코드=" + sLProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 좌주문번호=" + sLOrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 좌주문행번=" + sLOrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 좌입고일자=" + sLReceiptDt);
			logger.println(LogLevel.DEBUG, this, "=코일 좌카드No =" + sLCardNo);
			logger.println(LogLevel.DEBUG, this, "=코일 좌수요가   =" + sLDemanderCd);

			logger.println(LogLevel.DEBUG, this, "=코일 우진도코드=" + sRProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 우주문번호=" + sROrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 우주문행번=" + sROrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 우입고일자=" + sRReceiptDt);
			logger.println(LogLevel.DEBUG, this, "=코일 우카드No =" + sRCardNo);
			logger.println(LogLevel.DEBUG, this, "=코일 우수요가   =" + sRDemanderCd);

			if (sProgCd.equals(sLProgCd)) {
				iTotalN += iUPROG_CD;
			}
			if (sOrdNo.equals(sLOrdNo)) {
				iTotalN += iUORD_NO;
			}
			if (sOrdNo.equals(sLOrdNo) && sOrdDtl.equals(sLOrdDtl)) {
				iTotalN += iUORD_DTL;
			}
			if (sReceiptDt.equals(sLReceiptDt)) {
				iTotalN += iUINPUT_DT;
			}
			if (sCardNo.equals(sLCardNo)) {
				iTotalN += iUCARD_NO;
			}
//			수요가 평점(50점)
			if (sDemanderCd.equals(sLDemanderCd)) {
				iTotalN += 50;
			}

			if (sProgCd.equals(sRProgCd)) {
				iTotalN += iUPROG_CD;
			}
			if (sOrdNo.equals(sROrdNo)) {
				iTotalN += iUORD_NO;
			}
			if (sOrdNo.equals(sROrdNo) && sOrdDtl.equals(sROrdDtl)) {
				iTotalN += iUORD_DTL;
			}
			if (sReceiptDt.equals(sRReceiptDt)) {
				iTotalN += iUINPUT_DT;
			}
			if (sCardNo.equals(sRCardNo)) {
				iTotalN += iUCARD_NO;
			}
//			수요가 평점(50점)
			if (sDemanderCd.equals(sRDemanderCd)) {
				iTotalN += 50;
			}

			logger.println(LogLevel.DEBUG, this,
					"출하작업대기 02단 TO위치 적합성 체크 결과 종합점수=====" + iTotalN);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return iTotalN;
	}

	/**
	 * 출하완료 01단 TO위치 적합성 체크
	 * 
	 * @param JDTORecord :
	 *            TO위치정보
	 * @param JDTORecord :
	 *            기준정보
	 * 
	 * @return int 종합점수
	 * @throws Exception
	 */
	private int getCoilToLocInfo_006_M_01(JDTORecord stackV, JDTORecord ruleV)
			throws Exception {
		int iTotalN = 0;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			logger.println(LogLevel.DEBUG, this, "출하완료 01단 TO위치 적합성 체크=====");

			int iLPROG_CD = ruleV.getFieldInt("LPROG_CD");
			int iLORD_NO = ruleV.getFieldInt("LORD_NO");
			int iLORD_DTL = ruleV.getFieldInt("LORD_DTL");
			int iLINPUT_DT = ruleV.getFieldInt("LINPUT_DT");
			int iLCARD_NO = ruleV.getFieldInt("LCARD_NO");
			int iLSTAT = ruleV.getFieldInt("LSTAT");

			int iRPROG_CD = ruleV.getFieldInt("RPROG_CD");
			int iRORD_NO = ruleV.getFieldInt("RORD_NO");
			int iRORD_DTL = ruleV.getFieldInt("RORD_DTL");
			int iRINPUT_DT = ruleV.getFieldInt("RINPUT_DT");
			int iRCARD_NO = ruleV.getFieldInt("RCARD_NO");
			int iRSTAT = ruleV.getFieldInt("RSTAT");

			String sProgCd = StringHelper.evl(stackV
					.getFieldString("COIL_PROG_CD"), ""); // 진도코드
			String sOrdNo = StringHelper.evl(stackV
					.getFieldString("COIL_ORD_NO"), ""); // 주문번호
			String sOrdDtl = StringHelper.evl(stackV
					.getFieldString("COIL_ORD_DTL"), ""); // 주문행번
			String sReceiptDt = StringHelper.evl(stackV
					.getFieldString("COIL_RECEIPT_DT"), ""); // 입고일자
			String sCardNo = StringHelper.evl(stackV
					.getFieldString("COIL_CARD_NO"), ""); // 카드NO

			String sLProgCd = StringHelper.evl(stackV
					.getFieldString("LCOIL_PROG_CD"), ""); // 진도코드
			String sLOrdNo = StringHelper.evl(stackV
					.getFieldString("LCOIL_ORD_NO"), ""); // 주문번호
			String sLOrdDtl = StringHelper.evl(stackV
					.getFieldString("LCOIL_ORD_DTL"), ""); // 주문행번
			String sLReceiptDt = StringHelper.evl(stackV
					.getFieldString("LCOIL_RECEIPT_DT"), ""); // 입고일자
			String sLCardNo = StringHelper.evl(stackV
					.getFieldString("LCOIL_CARD_NO"), ""); // 카드NO

			String sRProgCd = StringHelper.evl(stackV
					.getFieldString("RCOIL_PROG_CD"), ""); // 진도코드
			String sROrdNo = StringHelper.evl(stackV
					.getFieldString("RCOIL_ORD_NO"), ""); // 주문번호
			String sROrdDtl = StringHelper.evl(stackV
					.getFieldString("RCOIL_ORD_DTL"), ""); // 주문행번
			String sRReceiptDt = StringHelper.evl(stackV
					.getFieldString("RCOIL_RECEIPT_DT"), ""); // 입고일자
			String sRCardNo = StringHelper.evl(stackV
					.getFieldString("RCOIL_CARD_NO"), ""); // 카드NO

			String sLLayerStat = StringHelper.evl(stackV
					.getFieldString("LEFT_STACK_LAYER_STAT"), "");
			String sRLayerStat = StringHelper.evl(stackV
					.getFieldString("RIGHT_STACK_LAYER_STAT"), "");

			String sDemanderCd = StringHelper.evl(stackV
					.getFieldString("COIL_DEMANDER_CD"), ""); // 수요가코드
			String sLDemanderCd = StringHelper.evl(stackV
					.getFieldString("LCOIL_DEMANDER_CD"), ""); // 수요가코드 
			String sRDemanderCd = StringHelper.evl(stackV
					.getFieldString("RCOIL_DEMANDER_CD"), ""); // 수요가코드 

			logger.println(LogLevel.DEBUG, this, "=코일 진도코드	=" + sProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 주문번호	=" + sOrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 주문행번	=" + sOrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 입고일자	=" + sReceiptDt);
			logger.println(LogLevel.DEBUG, this, "=코일 카드No	=" + sCardNo);
			logger.println(LogLevel.DEBUG, this, "=코일 수요가		=" + sDemanderCd);

			logger.println(LogLevel.DEBUG, this, "=코일 좌진도코드=" + sLProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 좌주문번호=" + sLOrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 좌주문행번=" + sLOrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 좌입고일자=" + sLReceiptDt);
			logger.println(LogLevel.DEBUG, this, "=코일 좌카드No =" + sLCardNo);
			logger.println(LogLevel.DEBUG, this, "=코일 좌수요가   =" + sLDemanderCd);

			logger.println(LogLevel.DEBUG, this, "=코일 우진도코드=" + sRProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 우주문번호=" + sROrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 우주문행번=" + sROrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 우입고일자=" + sRReceiptDt);
			logger.println(LogLevel.DEBUG, this, "=코일 우카드No =" + sRCardNo);
			logger.println(LogLevel.DEBUG, this, "=코일 우수요가   =" + sRDemanderCd);

			logger.println(LogLevel.DEBUG, this, "=코일 좌적치상태=" + sLLayerStat);
			logger.println(LogLevel.DEBUG, this, "=코일 우적치상태=" + sRLayerStat);

			int iEmpty = Integer.parseInt(StringHelper.evl(ruleV
					.getFieldString("MXE" + sProgCd), "0"));

			if ("".equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_E.equals(sLLayerStat)) {
				iTotalN += iEmpty;
			} else {
				if (sProgCd.equals(sLProgCd)) {
					iTotalN += iLPROG_CD;
				}
				if (sOrdNo.equals(sLOrdNo)) {
					iTotalN += iLORD_NO;
				}
				if (sOrdNo.equals(sLOrdNo) && sOrdDtl.equals(sLOrdDtl)) {
					iTotalN += iLORD_DTL;
				}
				if (sReceiptDt.equals(sLReceiptDt)) {
					iTotalN += iLINPUT_DT;
				}
				if (sCardNo.equals(sLCardNo)) {
					iTotalN += iLCARD_NO;
				}
//				수요가 평점(50점)
				if (sDemanderCd.equals(sLDemanderCd)) {
					iTotalN += 50;
				}
			}

			if ("".equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_E.equals(sRLayerStat)) {
				iTotalN += iEmpty;
			} else {
				if (sProgCd.equals(sRProgCd)) {
					iTotalN += iRPROG_CD;
				}
				if (sOrdNo.equals(sROrdNo)) {
					iTotalN += iRORD_NO;
				}
				if (sOrdNo.equals(sROrdNo) && sOrdDtl.equals(sROrdDtl)) {
					iTotalN += iRORD_DTL;
				}
				if (sReceiptDt.equals(sRReceiptDt)) {
					iTotalN += iRINPUT_DT;
				}
				if (sCardNo.equals(sRCardNo)) {
					iTotalN += iRCARD_NO;
				}
//				수요가 평점(50점)
				if (sDemanderCd.equals(sRDemanderCd)) {
					iTotalN += 50;
				}
			}

			if (YmCommonConst.STACK_LAYER_STAT_L.equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_P.equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_X.equals(sLLayerStat)) {
				iTotalN += iLSTAT;
			}
			if (YmCommonConst.STACK_LAYER_STAT_L.equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_P.equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_X.equals(sRLayerStat)) {
				iTotalN += iRSTAT;
			}
			logger.println(LogLevel.DEBUG, this,
					"출하완료 01단 TO위치 적합성 체크 결과 종합점수=====" + iTotalN);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return iTotalN;
	}

	/**
	 * 출하완료 02단 TO위치 적합성 체크
	 * 
	 * @param JDTORecord :
	 *            TO위치정보
	 * @param JDTORecord :
	 *            기준정보
	 * 
	 * @return int 종합점수
	 * @throws Exception
	 */
	private int getCoilToLocInfo_006_M_02(JDTORecord stackV, JDTORecord ruleV)
			throws Exception {
		int iTotalN = 0;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			logger.println(LogLevel.DEBUG, this, "출하완료 02단 TO위치 적합성 체크=====");

			int iUPROG_CD = ruleV.getFieldInt("UPROG_CD");
			int iUORD_NO = ruleV.getFieldInt("UORD_NO");
			int iUORD_DTL = ruleV.getFieldInt("UORD_DTL");
			int iUINPUT_DT = ruleV.getFieldInt("UINPUT_DT");
			int iUCARD_NO = ruleV.getFieldInt("UCARD_NO");

			String sProgCd = StringHelper.evl(stackV
					.getFieldString("COIL_PROG_CD"), ""); // 진도코드
			String sOrdNo = StringHelper.evl(stackV
					.getFieldString("COIL_ORD_NO"), ""); // 주문번호
			String sOrdDtl = StringHelper.evl(stackV
					.getFieldString("COIL_ORD_DTL"), ""); // 주문행번
			String sReceiptDt = StringHelper.evl(stackV
					.getFieldString("COIL_RECEIPT_DT"), ""); // 입고일자
			String sCardNo = StringHelper.evl(stackV
					.getFieldString("COIL_CARD_NO"), ""); // 카드NO

			String sLProgCd = StringHelper.evl(stackV
					.getFieldString("LCOIL_PROG_CD"), ""); // 진도코드
			String sLOrdNo = StringHelper.evl(stackV
					.getFieldString("LCOIL_ORD_NO"), ""); // 주문번호
			String sLOrdDtl = StringHelper.evl(stackV
					.getFieldString("LCOIL_ORD_DTL"), ""); // 주문행번
			String sLReceiptDt = StringHelper.evl(stackV
					.getFieldString("LCOIL_RECEIPT_DT"), ""); // 입고일자
			String sLCardNo = StringHelper.evl(stackV
					.getFieldString("LCOIL_CARD_NO"), ""); // 카드NO

			String sRProgCd = StringHelper.evl(stackV
					.getFieldString("RCOIL_PROG_CD"), ""); // 진도코드
			String sROrdNo = StringHelper.evl(stackV
					.getFieldString("RCOIL_ORD_NO"), ""); // 주문번호
			String sROrdDtl = StringHelper.evl(stackV
					.getFieldString("RCOIL_ORD_DTL"), ""); // 주문행번
			String sRReceiptDt = StringHelper.evl(stackV
					.getFieldString("RCOIL_RECEIPT_DT"), ""); // 입고일자
			String sRCardNo = StringHelper.evl(stackV
					.getFieldString("RCOIL_CARD_NO"), ""); // 카드NO

			String sDemanderCd = StringHelper.evl(stackV
					.getFieldString("COIL_DEMANDER_CD"), ""); // 수요가코드
			String sLDemanderCd = StringHelper.evl(stackV
					.getFieldString("LCOIL_DEMANDER_CD"), ""); // 수요가코드 
			String sRDemanderCd = StringHelper.evl(stackV
					.getFieldString("RCOIL_DEMANDER_CD"), ""); // 수요가코드 

			logger.println(LogLevel.DEBUG, this, "=코일 진도코드	=" + sProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 주문번호	=" + sOrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 주문행번	=" + sOrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 입고일자	=" + sReceiptDt);
			logger.println(LogLevel.DEBUG, this, "=코일 카드No	=" + sCardNo);
			logger.println(LogLevel.DEBUG, this, "=코일 수요가		=" + sDemanderCd);

			logger.println(LogLevel.DEBUG, this, "=코일 좌진도코드=" + sLProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 좌주문번호=" + sLOrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 좌주문행번=" + sLOrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 좌입고일자=" + sLReceiptDt);
			logger.println(LogLevel.DEBUG, this, "=코일 좌카드No =" + sLCardNo);
			logger.println(LogLevel.DEBUG, this, "=코일 좌수요가   =" + sLDemanderCd);

			logger.println(LogLevel.DEBUG, this, "=코일 우진도코드=" + sRProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 우주문번호=" + sROrdNo);
			logger.println(LogLevel.DEBUG, this, "=코일 우주문행번=" + sROrdDtl);
			logger.println(LogLevel.DEBUG, this, "=코일 우입고일자=" + sRReceiptDt);
			logger.println(LogLevel.DEBUG, this, "=코일 우카드No =" + sRCardNo);
			logger.println(LogLevel.DEBUG, this, "=코일 우수요가   =" + sRDemanderCd);

			if (sProgCd.equals(sLProgCd)) {
				iTotalN += iUPROG_CD;
			}
			if (sOrdNo.equals(sLOrdNo)) {
				iTotalN += iUORD_NO;
			}
			if (sOrdNo.equals(sLOrdNo) && sOrdDtl.equals(sLOrdDtl)) {
				iTotalN += iUORD_DTL;
			}
			if (sReceiptDt.equals(sLReceiptDt)) {
				iTotalN += iUINPUT_DT;
			}
			if (sCardNo.equals(sLCardNo)) {
				iTotalN += iUCARD_NO;
			}
//			수요가 평점(50점)
			if (sDemanderCd.equals(sLDemanderCd)) {
				iTotalN += 50;
			}

			if (sProgCd.equals(sRProgCd)) {
				iTotalN += iUPROG_CD;
			}
			if (sOrdNo.equals(sROrdNo)) {
				iTotalN += iUORD_NO;
			}
			if (sOrdNo.equals(sROrdNo) && sOrdDtl.equals(sROrdDtl)) {
				iTotalN += iUORD_DTL;
			}
			if (sReceiptDt.equals(sRReceiptDt)) {
				iTotalN += iUINPUT_DT;
			}
			if (sCardNo.equals(sRCardNo)) {
				iTotalN += iUCARD_NO;
			}
//			수요가 평점(50점)
			if (sDemanderCd.equals(sRDemanderCd)) {
				iTotalN += 50;
			}

			logger.println(LogLevel.DEBUG, this,
					"출하완료 02단 TO위치 적합성 체크 결과 종합점수=====" + iTotalN);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return iTotalN;
	}

	/**
	 * 경매대상선정 01단 TO위치 적합성 체크
	 * 
	 * @param JDTORecord :
	 *            TO위치정보
	 * @param JDTORecord :
	 *            기준정보
	 * 
	 * @return int 종합점수
	 * @throws Exception
	 */
	private int getCoilToLocInfo_006_X_01(JDTORecord stackV, JDTORecord ruleV)
			throws Exception {
		int iTotalN = 0;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			logger.println(LogLevel.DEBUG, this, "경매대상선정 01단 TO위치 적합성 체크=====");

			iTotalN = 999;

			logger.println(LogLevel.DEBUG, this,
					"경매대상선정 01단 TO위치 적합성 체크 결과 종합점수=====" + iTotalN);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return iTotalN;
	}

	/**
	 * 경매대상선정 02단 TO위치 적합성 체크
	 * 
	 * @param JDTORecord :
	 *            TO위치정보
	 * @param JDTORecord :
	 *            기준정보
	 * 
	 * @return int 종합점수
	 * @throws Exception
	 */
	private int getCoilToLocInfo_006_X_02(JDTORecord stackV, JDTORecord ruleV)
			throws Exception {
		int iTotalN = 0;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			logger.println(LogLevel.DEBUG, this, "경매대상선정 02단 TO위치 적합성 체크=====");

			iTotalN = 999;

			logger.println(LogLevel.DEBUG, this,
					"경매대상선정 02단 TO위치 적합성 체크 결과 종합점수=====" + iTotalN);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return iTotalN;
	}

	/**
	 * 제공충당대기 01단 TO위치 적합성 체크
	 * 
	 * @param JDTORecord :
	 *            TO위치정보
	 * @param JDTORecord :
	 *            기준정보
	 * 
	 * @return int 종합점수
	 * @throws Exception
	 */
	private int getCoilToLocInfo_006_Y_01(JDTORecord stackV, JDTORecord ruleV)
			throws Exception {
		int iTotalN = 0;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			logger.println(LogLevel.DEBUG, this, "제공충당대기 01단 TO위치 적합성 체크=====");

			int iLPROG_CD = ruleV.getFieldInt("LPROG_CD");
			int iLSPEC = ruleV.getFieldInt("LSPEC");
			int iLSTAT = ruleV.getFieldInt("LSTAT");

			int iRPROG_CD = ruleV.getFieldInt("RPROG_CD");
			int iRSPEC = ruleV.getFieldInt("RSPEC");
			int iRSTAT = ruleV.getFieldInt("RSTAT");

			String sProgCd = StringHelper.evl(stackV
					.getFieldString("COIL_PROG_CD"), ""); // 진도코드
			String sSpec = StringHelper.evl(stackV
					.getFieldString("COIL_SPEC_ABBSYM"), ""); // 규격약호

			String sLProgCd = StringHelper.evl(stackV
					.getFieldString("LCOIL_PROG_CD"), ""); // 진도코드
			String sLSpec = StringHelper.evl(stackV
					.getFieldString("LCOIL_SPEC_ABBSYM"), ""); // 규격약호

			String sRProgCd = StringHelper.evl(stackV
					.getFieldString("RCOIL_PROG_CD"), ""); // 진도코드
			String sRSpec = StringHelper.evl(stackV
					.getFieldString("RCOIL_SPEC_ABBSYM"), ""); // 규격약호

			String sLLayerStat = StringHelper.evl(stackV
					.getFieldString("LEFT_STACK_LAYER_STAT"), "");
			String sRLayerStat = StringHelper.evl(stackV
					.getFieldString("RIGHT_STACK_LAYER_STAT"), "");

			logger.println(LogLevel.DEBUG, this, "=코일 진도코드	=" + sProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 규격약호	=" + sSpec);

			logger.println(LogLevel.DEBUG, this, "=코일 좌진도코드=" + sLProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 좌규격약호=" + sLSpec);

			logger.println(LogLevel.DEBUG, this, "=코일 우진도코드=" + sRProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 우규격약호=" + sRSpec);

			logger.println(LogLevel.DEBUG, this, "=코일 좌적치상태=" + sLLayerStat);
			logger.println(LogLevel.DEBUG, this, "=코일 우적치상태=" + sRLayerStat);

			int iEmpty = Integer.parseInt(StringHelper.evl(ruleV
					.getFieldString("MXE" + sProgCd), "0"));

			if ("".equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_E.equals(sLLayerStat)) {
				iTotalN += iEmpty;
			} else {

				if (sProgCd.equals(sLProgCd)) {
					iTotalN += iLPROG_CD;
				}
				if (sSpec.equals(sLSpec)) {
					iTotalN += iLSPEC;
				}
			}

			if ("".equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_E.equals(sRLayerStat)) {
				iTotalN += iEmpty;
			} else {
				if (sProgCd.equals(sRProgCd)) {
					iTotalN += iRPROG_CD;
				}
				if (sSpec.equals(sRSpec)) {
					iTotalN += iRSPEC;
				}
			}

			if (YmCommonConst.STACK_LAYER_STAT_L.equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_P.equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_X.equals(sLLayerStat)) {
				iTotalN += iLSTAT;
			}
			if (YmCommonConst.STACK_LAYER_STAT_L.equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_P.equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_X.equals(sRLayerStat)) {
				iTotalN += iRSTAT;
			}
			logger.println(LogLevel.DEBUG, this,
					"제공충당대기 01단 TO위치 적합성 체크 결과 종합점수=====" + iTotalN);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return iTotalN;
	}

	/**
	 * 제공충당대기 02단 TO위치 적합성 체크
	 * 
	 * @param JDTORecord :
	 *            TO위치정보
	 * @param JDTORecord :
	 *            기준정보
	 * 
	 * @return int 종합점수
	 * @throws Exception
	 */
	private int getCoilToLocInfo_006_Y_02(JDTORecord stackV, JDTORecord ruleV)
			throws Exception {
		int iTotalN = 0;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			logger.println(LogLevel.DEBUG, this, "제공충당대기 02단 TO위치 적합성 체크=====");

			int iUPROG_CD = ruleV.getFieldInt("UPROG_CD");
			int iUSPEC = ruleV.getFieldInt("USPEC");

			String sProgCd = StringHelper.evl(stackV
					.getFieldString("COIL_PROG_CD"), ""); // 진도코드
			String sSpec = StringHelper.evl(stackV
					.getFieldString("COIL_SPEC_ABBSYM"), ""); // 규격약호

			String sLProgCd = StringHelper.evl(stackV
					.getFieldString("LCOIL_PROG_CD"), ""); // 진도코드
			String sLSpec = StringHelper.evl(stackV
					.getFieldString("LCOIL_SPEC_ABBSYM"), ""); // 규격약호

			String sRProgCd = StringHelper.evl(stackV
					.getFieldString("RCOIL_PROG_CD"), ""); // 진도코드
			String sRSpec = StringHelper.evl(stackV
					.getFieldString("RCOIL_SPEC_ABBSYM"), ""); // 규격약호

			logger.println(LogLevel.DEBUG, this, "=코일 진도코드	=" + sProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 규격약호	=" + sSpec);

			logger.println(LogLevel.DEBUG, this, "=코일 좌진도코드=" + sLProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 좌규격약호=" + sLSpec);

			logger.println(LogLevel.DEBUG, this, "=코일 우진도코드=" + sRProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 우규격약호=" + sRSpec);

			if (sProgCd.equals(sLProgCd)) {
				iTotalN += iUPROG_CD;
			}
			if (sSpec.equals(sLSpec)) {
				iTotalN += iUSPEC;
			}

			if (sProgCd.equals(sRProgCd)) {
				iTotalN += iUPROG_CD;
			}
			if (sSpec.equals(sRSpec)) {
				iTotalN += iUSPEC;
			}

			logger.println(LogLevel.DEBUG, this,
					"제공충당대기 02단 TO위치 적합성 체크 결과 종합점수=====" + iTotalN);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return iTotalN;
	}

	/**
	 * 제품충당대기 01단 TO위치 적합성 체크
	 * 
	 * @param JDTORecord :
	 *            TO위치정보
	 * @param JDTORecord :
	 *            기준정보
	 * 
	 * @return int 종합점수
	 * @throws Exception
	 */
	private int getCoilToLocInfo_006_Z_01(JDTORecord stackV, JDTORecord ruleV)
			throws Exception {
		int iTotalN = 0;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			logger.println(LogLevel.DEBUG, this, "제품충당대기 01단 TO위치 적합성 체크=====");

			int iLPROG_CD = ruleV.getFieldInt("LPROG_CD");
			int iLINPUT_DT = ruleV.getFieldInt("LINPUT_DT");
			int iLSTAT = ruleV.getFieldInt("LSTAT");

			int iRPROG_CD = ruleV.getFieldInt("RPROG_CD");
			int iRINPUT_DT = ruleV.getFieldInt("RINPUT_DT");
			int iRSTAT = ruleV.getFieldInt("RSTAT");

			String sProgCd = StringHelper.evl(stackV
					.getFieldString("COIL_PROG_CD"), ""); // 진도코드
			String sReceiptDt = StringHelper.evl(stackV
					.getFieldString("COIL_RECEIPT_DT"), ""); // 입고일자

			String sLProgCd = StringHelper.evl(stackV
					.getFieldString("LCOIL_PROG_CD"), ""); // 진도코드
			String sLReceiptDt = StringHelper.evl(stackV
					.getFieldString("LCOIL_RECEIPT_DT"), ""); // 입고일자

			String sRProgCd = StringHelper.evl(stackV
					.getFieldString("RCOIL_PROG_CD"), ""); // 진도코드
			String sRReceiptDt = StringHelper.evl(stackV
					.getFieldString("RCOIL_RECEIPT_DT"), ""); // 입고일자

			String sLLayerStat = StringHelper.evl(stackV
					.getFieldString("LEFT_STACK_LAYER_STAT"), "");
			String sRLayerStat = StringHelper.evl(stackV
					.getFieldString("RIGHT_STACK_LAYER_STAT"), "");

			logger.println(LogLevel.DEBUG, this, "=코일 진도코드	=" + sProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 입고일자	=" + sReceiptDt);

			logger.println(LogLevel.DEBUG, this, "=코일 좌진도코드=" + sLProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 좌입고일자=" + sLReceiptDt);

			logger.println(LogLevel.DEBUG, this, "=코일 우진도코드=" + sRProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 우입고일자=" + sRReceiptDt);

			logger.println(LogLevel.DEBUG, this, "=코일 좌적치상태=" + sLLayerStat);
			logger.println(LogLevel.DEBUG, this, "=코일 우적치상태=" + sRLayerStat);

			int iEmpty = Integer.parseInt(StringHelper.evl(ruleV
					.getFieldString("MXE" + sProgCd), "0"));

			if ("".equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_E.equals(sLLayerStat)) {
				iTotalN += iEmpty;
			} else {

				if (sProgCd.equals(sLProgCd)) {
					iTotalN += iLPROG_CD;
				}
				if (sReceiptDt.equals(sLReceiptDt)) {
					iTotalN += iLINPUT_DT;
				}
			}

			if ("".equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_E.equals(sRLayerStat)) {
				iTotalN += iEmpty;
			} else {
				if (sProgCd.equals(sRProgCd)) {
					iTotalN += iRPROG_CD;
				}
				if (sReceiptDt.equals(sRReceiptDt)) {
					iTotalN += iRINPUT_DT;
				}
			}

			if (YmCommonConst.STACK_LAYER_STAT_L.equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_P.equals(sLLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_X.equals(sLLayerStat)) {
				iTotalN += iLSTAT;
			}
			if (YmCommonConst.STACK_LAYER_STAT_L.equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_P.equals(sRLayerStat)
					|| YmCommonConst.STACK_LAYER_STAT_X.equals(sRLayerStat)) {
				iTotalN += iRSTAT;
			}
			logger.println(LogLevel.DEBUG, this,
					"제품충당대기 01단 TO위치 적합성 체크 결과 종합점수=====" + iTotalN);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return iTotalN;
	}

	/**
	 * 제품충당대기 02단 TO위치 적합성 체크
	 * 
	 * @param JDTORecord :
	 *            TO위치정보
	 * @param JDTORecord :
	 *            기준정보
	 * 
	 * @return int 종합점수
	 * @throws Exception
	 */
	private int getCoilToLocInfo_006_Z_02(JDTORecord stackV, JDTORecord ruleV)
			throws Exception {
		int iTotalN = 0;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			logger.println(LogLevel.DEBUG, this, "제품충당대기 02단 TO위치 적합성 체크=====");

			int iUPROG_CD = ruleV.getFieldInt("UPROG_CD");
			int iUINPUT_DT = ruleV.getFieldInt("UINPUT_DT");

			String sProgCd = StringHelper.evl(stackV
					.getFieldString("COIL_PROG_CD"), ""); // 진도코드
			String sReceiptDt = StringHelper.evl(stackV
					.getFieldString("COIL_RECEIPT_DT"), ""); // 입고일자

			String sLProgCd = StringHelper.evl(stackV
					.getFieldString("LCOIL_PROG_CD"), ""); // 진도코드
			String sLReceiptDt = StringHelper.evl(stackV
					.getFieldString("LCOIL_RECEIPT_DT"), ""); // 입고일자

			String sRProgCd = StringHelper.evl(stackV
					.getFieldString("RCOIL_PROG_CD"), ""); // 진도코드
			String sRReceiptDt = StringHelper.evl(stackV
					.getFieldString("RCOIL_RECEIPT_DT"), ""); // 입고일자

			logger.println(LogLevel.DEBUG, this, "=코일 진도코드	=" + sProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 입고일자	=" + sReceiptDt);

			logger.println(LogLevel.DEBUG, this, "=코일 좌진도코드=" + sLProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 좌입고일자=" + sLReceiptDt);

			logger.println(LogLevel.DEBUG, this, "=코일 우진도코드=" + sRProgCd);
			logger.println(LogLevel.DEBUG, this, "=코일 우입고일자=" + sRReceiptDt);

			if (sProgCd.equals(sLProgCd)) {
				iTotalN += iUPROG_CD;
			}
			if (sReceiptDt.equals(sLReceiptDt)) {
				iTotalN += iUINPUT_DT;
			}

			if (sProgCd.equals(sRProgCd)) {
				iTotalN += iUPROG_CD;
			}
			if (sReceiptDt.equals(sRReceiptDt)) {
				iTotalN += iUINPUT_DT;
			}

			logger.println(LogLevel.DEBUG, this,
					"제품충당대기 02단 TO위치 적합성 체크 결과 종합점수=====" + iTotalN);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return iTotalN;
	}

	/**
	 * SCHEDULE에 등록할 항목 및 값을 정의한다. JDTORecord Type 의 값을 List Type 으로 변환한다. 추후
	 * 스케쥴등록과 관련된 항목정의는 아래의 Method에서 정의함.
	 * 
	 * @param JDTORecord
	 * @return List
	 * @throws
	 */
	private List getSchData(JDTORecord stockV) {
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		}
		
		List param = new ArrayList();

		String sStockId = StringHelper.evl(stockV.getFieldString("STOCK_ID"),
				""); // 저장품ID
		String sYdGp = StringHelper.evl(stockV.getFieldString("YD_GP"), ""); // 야드구분
		String sBayGp = StringHelper.evl(stockV.getFieldString("BAY_GP"), ""); // 동구분
		String sSchRuleId = StringHelper.evl(stockV
				.getFieldString("CRANE_SCH_RULE_ID"), ""); // SCH 기준 ID
		String sStackColGp = StringHelper.evl(stockV
				.getFieldString("STACK_COL_GP"), ""); // 적치열 구분
		String sSchRuleCraneNo = StringHelper.evl(stockV
				.getFieldString("SELECT_CRANE_NO"), ""); // SCH 작업설비번호
		String sSchRuleWprefer = StringHelper.evl(stockV
				.getFieldString("SELECT_WPREFER"), ""); // SCH 작업우선순위
		String sSchWorkKind = StringHelper.evl(stockV
				.getFieldString("SCH_WORK_KIND"), ""); // SCH CODE
		String sSchWorkAidYn = StringHelper.evl(stockV.getFieldString("GBN"),
				""); // SCH 작업 보조유무
		String sFoStackColGp = StringHelper.evl(stockV
				.getFieldString("STACK_COL_GP"), ""); // CRANE 작업지시 UP위치
		String sFoStackBedGp = StringHelper.evl(stockV
				.getFieldString("STACK_BED_GP"), ""); // CRANE 작업지시 UP위치
		String sFoStackLayerGp = StringHelper.evl(stockV
				.getFieldString("STACK_LAYER_GP"), ""); // CRANE 작업지시 UP위치
		String sToStackColGp = StringHelper.evl(stockV
				.getFieldString("TO_STACK_COL_GP"), ""); // CRANE 작업지시 PUT위치
		String sToStackBedGp = StringHelper.evl(stockV
				.getFieldString("TO_STACK_BED_GP"), ""); // CRANE 작업지시 PUT위치
		String sToStackLayerGp = StringHelper.evl(stockV
				.getFieldString("TO_STACK_LAYER_GP"), ""); // CRANE 작업지시 PUT위치
		String sSchWorkLocDsMethod = StringHelper.evl(stockV
				.getFieldString("SCH_WORK_LOC_DECISION_METHOD"), "");// 작업예약위치
																		// 결정방법
		String sWbookId = StringHelper.evl(stockV.getFieldString("WBOOK_ID"),
				"");
		String sCarCardNo = StringHelper.evl(stockV
				.getFieldString("CAR_CARD_NO"), ""); // 차량카드번호

		String sUpLoc = sFoStackColGp + sFoStackBedGp + sFoStackLayerGp;
		String sPutLoc = sToStackColGp + sToStackBedGp + sToStackLayerGp;

		String sSchWorkStat = "S"; // 스케쥴 수행
		String sSchWorkGripLotYn = "N"; // 해당사항없슴
		String sSchWorkCarNo = ""; // 스케쥴작업차량번호
		String sSchWdemandType = ""; // 스케쥴작업요구형태
		String sWbookSchActDdtt = ""; // YmCommonUtil.getCurDate("yyyyMMddHHmm");
		// 작업예약 스케쥴 실행일시
		String sWbookDdtt = YmCommonUtil.getCurDate("yyyyMMddHHmm");
		String sWbookDuty = YmCommonUtil.getWorkDuty();
		String sWbookParty = YmCommonUtil.getWorkParty();

		// param.add(XXXXX); //sch_id
		param.add(sStockId); // stock_id
		param.add(sSchRuleId); // sch_rule_id
		param.add(sYdGp); // yd_gp
		param.add(sBayGp); // bay_gp
		param.add(sSchRuleCraneNo); // sch_work_equip_no
		param.add(sSchWorkStat); // sch_work_stat
		param.add(sSchRuleWprefer); // sch_wprefer
		param.add(sSchWorkKind); // sch_work_kind
		param.add(sSchWorkAidYn); // sch_work_aid_yn
		param.add(sSchWorkGripLotYn); // sch_work_grip_lot_yn
		param.add(sUpLoc); // crane_word_up_loc
		param.add(sSchWorkLocDsMethod); // wbook_loc_decision_method
		param.add(sPutLoc); // crane_word_put_loc
		param.add(sSchWorkCarNo); // sch_work_car_no
		param.add(sSchWdemandType); // sch_wdemand_type
		param.add(sWbookSchActDdtt); // wbook_sch_act_ddtt
		param.add(sWbookDdtt); // sch_wdemand_ddtt
		param.add(sWbookDuty); // sch_wdemand_duty
		param.add(sWbookParty); // sch_wdemand_party
		param.add(sWbookId); // wbook_id
		param.add(sCarCardNo); // car_card_no
		param.add("SYSTEM"); // register
		// param.add(XXXXX); //reg_ddtt
		// param.add(XXXXX); //modifier
		// param.add(XXXXX); //mod_ddtt
		param.add("N"); // del_yn

		return param;
	}

	/**
	 * 적합한 번지단 정보이면 해당 번지단을 예약상태로 셋팅한다. 적치단TABLE FROM 위치 - STOCK_ID : ID -
	 * STACK_LAYER_ACTIVE_STAT : 'C' - STACK_LAYER_STAT : 'U' 적치단TABLE TO 위치 -
	 * STOCK_ID : ID - STACK_LAYER_ACTIVE_STAT : 'C' - STACK_LAYER_STAT : 'P'
	 * 
	 * @param DAO :
	 * @param JDTORecord :
	 * @param JDTORecord :
	 * 
	 * @return int
	 * @throws Exception
	 */
	private int setCoilLocResumeInfo(JDTORecord stockV, JDTORecord stockToV)
			throws Exception {
		int iSeq = 0;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return 0;
			}
			
			String sStockId = stockV.getFieldString("STOCK_ID"); // 저장품ID

			String sStackColGp = stockV.getFieldString("STACK_COL_GP"); // 적치열구분
			String sStackBedGp = stockV.getFieldString("STACK_BED_GP"); // 적치BED구분
			String sStackLayerGp = stockV.getFieldString("STACK_LAYER_GP"); // 적치단구분

			String sToStackColGp = stockToV.getFieldString("TO_STACK_COL_GP"); // 적치열구분
			String sToStackBedGp = stockToV.getFieldString("TO_STACK_BED_GP"); // 적치BED구분
			String sToStackLayerGp = stockToV.getFieldString("TO_STACK_LAYER_GP"); // 적치단구분

			/*
			 * 적치단 UP위치 셋팅 tb_ym_stacklayer Table : stock_id = stock_id(저장품ID)
			 * tb_ym_stacklayer Table : stack_layer_stat = 'U'(UP 스케쥴수행)
			 */
			iSeq = dao.updateCraneStackLayerStat(sStackColGp, sStackBedGp,
					sStackLayerGp, sStockId, YmCommonConst.STACK_LAYER_STAT_U);

			/*
			 * 적치단 PUT위치 셋팅 tb_ym_stacklayer Table : stock_id = stock_id(저장품ID)
			 * tb_ym_stacklayer Table : stack_layer_stat = 'P'(PUT 스케쥴수행)
			 */
			iSeq = dao.updateCraneStackLayerStat(sToStackColGp, sToStackBedGp,
							sToStackLayerGp, sStockId,
							YmCommonConst.STACK_LAYER_STAT_P);
			/*
			 * A.B열연 Coil 스케쥴 TO위치 예약 결정시 상단 좌,우 상태정보를 UPDATE
			 */
			{
				if (YmCommonConst.STACK_LAYER_GP_01.equals(sToStackLayerGp)) {

					/*
					 * A.B열연 Coil 스케쥴 TO위치 예약 결정시 상단 왼쪽 상태정보를 UPDATE 상단 오른쪽
					 * 상태정보를 UPDATE
					 */
					iSeq = YmCommonDB.setCoilUpperState_E(sToStackColGp,
							sToStackBedGp, sToStackLayerGp);
				}
			}

		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return iSeq;
	}

	/**
	 * CRANE이 IDLE 상태일 때 작업지시를 한다.
	 * 
	 * @param String :
	 *            작업예약ID
	 * @param String :
	 *            CRANE NO
	 * @param JDTORecord
	 * 
	 * @return boolean
	 * @throws Exception
	 */
	private boolean callCwrkOrdReg(String sWbookId, String sCraneNo) throws Exception {
		Boolean isSuccess = new Boolean(false);
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			logger.println(LogLevel.DEBUG, this, "=COIL SCHEDULE STEP 7 작업요구 START=");
			JDTORecord wbookV = dao.getWbookInfo(sWbookId);

			if (wbookV != null) {

				String sYdGp 	= StringHelper.evl(wbookV.getFieldString("YD_GP"),"");
				String sBayGp 	= StringHelper.evl(wbookV.getFieldString("BAY_GP"), "");
				String sSchCode = StringHelper.evl(wbookV.getFieldString("SCH_WORK_KIND"), "");

				if ("".equals(sCraneNo)) {
					JDTORecord craneV = dao.getCoilCraneInfo(sYdGp, sBayGp, sSchCode);
					if (craneV != null) {
						sCraneNo = StringHelper.evl(craneV.getFieldString("SELECT_CRANE_NO"), "");
					}
				}

				logger.println(LogLevel.DEBUG, this, "sYdGp  	 	=" + sYdGp);
				logger.println(LogLevel.DEBUG, this, "sCraneNo 		=" + sCraneNo);
				logger.println(LogLevel.DEBUG, this, "sSchCode 		=" + sSchCode);
				logger.println(LogLevel.DEBUG, this, "sWbookId 		=" + sWbookId);

				JDTORecord craneV = dao.getEquipInfoWithEquipNo(sYdGp, sCraneNo);

				if (craneV != null) {

					// IDLE 상태일때 작업지시요구 호출
					logger.println(LogLevel.DEBUG, this, "WPROG_STAT =" + StringHelper.evl(craneV.getFieldString("WPROG_STAT"), ""));

					if (YmCommonConst.WPROG_STAT_W.equals(StringHelper.evl(craneV.getFieldString("WPROG_STAT"), ""))
							|| ( YmCommonConst.NEW_SCH_WORK_KIND_CNLO.equals(sSchCode) 
									&& YmCommonConst.YD_GP_3.equals(sYdGp) 
									&& ( YmCommonConst.WPROG_STAT_W.equals(StringHelper.evl(craneV.getFieldString("WPROG_STAT"), ""))
									|| "1".equals(StringHelper.evl(craneV.getFieldString("WPROG_STAT"), "")	) )
							   )		
					   ) {

						String sYard_Id 	= StringHelper.evl(craneV.getFieldString("YD_GP"), "");
						String sBay_Gp 		= StringHelper.evl(craneV.getFieldString("BAY_GP"), "");
						String sEquip_Kind 	= StringHelper.evl(craneV.getFieldString("EQUIP_KIND"), "");
						String sEquip_No 	= StringHelper.evl(craneV.getFieldString("EQUIP_NO"), "");
						String sTc 			= "";

						if (YmCommonConst.YD_GP_1.equals(sYard_Id)) {// A열연
																		// Coil
																		// 작업지시요구
							sTc = YmCommonConst.TC_THCH520;
						} else if (YmCommonConst.YD_GP_3.equals(sYard_Id)) { // B열연
																				// Coil
																				// 작업지시요구
							sTc = YmCommonConst.TC_CN1PB02;
						}

						logger.println(LogLevel.DEBUG, this, "스케쥴 등록 후 "+ sCraneNo + ":CRANE IDLE 상태에서 작업지시요구");

						/**
						 * 작업지시를 요구한다.
						 */
						EJBConnector ejbConn = new EJBConnector("default","JNDICWrkOrdReg", this);
						isSuccess = (Boolean) ejbConn.trx("callCraneSchInfo",
										new Class[] { String.class, String.class,
													String.class, String.class,
													String.class, String.class,
													String.class }, 
										new Object[] { sTc,
													sYard_Id, sBay_Gp, sEquip_Kind,
													sEquip_No, sSchCode, "" });// sWbookId});
/*
					// SPM2 추출시 조건 추가. 최규성 
					}else if ( YmCommonConst.NEW_SCH_WORK_KIND_CNLO.equals(sSchCode) && YmCommonConst.YD_GP_3.equals(sYdGp)  ){
						// SPM2 추출시 조건 추가함. 최규성 2010-01-27
						logger.println(LogLevel.DEBUG, this, "SPM2 추출 작업지시요구");
						
						String sYard_Id 	= StringHelper.evl(craneV.getFieldString("YD_GP"), "");
						String sBay_Gp 		= StringHelper.evl(craneV.getFieldString("BAY_GP"), "");
						String sEquip_Kind 	= StringHelper.evl(craneV.getFieldString("EQUIP_KIND"), "");
						String sEquip_No 	= StringHelper.evl(craneV.getFieldString("EQUIP_NO"), "");
						JDTORecord ordJr = dao.getFirstSchInfo_Spm2( sYard_Id,	sBay_Gp,		sSchCode,	sEquip_No);
						
						int iSeq = dao.updateCraneSchStat_spm2(YmCommonConst.SCH_WORK_STAT_S);
						logger.println(LogLevel.DEBUG,this,"SPM2 스케쥴 정보 초기화="+iSeq);
						
			    	 	int iReq = -1;
				    	 	 
			    		iReq = setCraneStatInfo_spm2(ordJr);
				    		
			    		boolean isMsg =	callCraneMsgInfo(YmCommonConst.TC_CN1PB02, ordJr, YmCommonConst.TC_WORK_I);
*/						
						
					}
				}
			}
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return isSuccess.booleanValue();
	}

	/**
	 * 최규
     * Crane 작업지시에 관련된 상태정보를 등록 및 수정한다.
     * 
     * @param dao 			: DAO
     * @param jDTORecord 	: 스케쥴정보
     *
     * @return
     * @throws 
     */	
	private int setCraneStatInfo_spm2(JDTORecord schRc){
		int iReq = -1;
		
		try{ 
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
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
	 * COIL 스케쥴 취소 메소드.
	 * 
	 * param String :
	 *            SCH_ID
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                  
	public boolean cancelCoilSchInfo(String sSchId) {
		ymCommonDAO dao2 = ymCommonDAO.getInstance();
		boolean isSuccess = false;
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			int iSeq = 0;

			String sSchWorkStat = "";
			String sStockId 	= "";
			String sSchWorkKind = "";
			String sWork_Id 	= "";

			JDTORecord schInfo = dao.getSchInfoWithSchId(sSchId);

			if (schInfo == null) {
				throw new EJBServiceException("=스케쥴 취소=>스케쥴정보 존재안함.");
			}

			sStockId 	 = StringHelper.evl(schInfo.getFieldString("STOCK_ID"), "");
			sSchWorkKind = StringHelper.evl(schInfo.getFieldString("SCH_WORK_KIND"), "");
			sWork_Id 	 = StringHelper.evl(schInfo.getFieldString("SCH_WORK_AID_YN"), "");
			/**
			 * 1. 스케쥴 정보 가져온다.
			 */
			{
				sSchWorkStat = StringHelper.evl(schInfo.getFieldString("SCH_WORK_STAT"), "");

				if (!YmCommonConst.SCH_WORK_STAT_S.equals(sSchWorkStat)
						&& !YmCommonConst.SCH_WORK_STAT_1.equals(sSchWorkStat)) {

					// throw new EJBServiceException("=스케쥴 취소=>스케쥴정보를 취소할 수
					// 없슴.");
					logger.println(LogLevel.DEBUG, this, "=스케쥴 취소=>스케쥴정보를 취소할 수 없슴.");
				}
			}

			/**
			 * 6. 작업취소 전문 전송
			 */
			{
				String sYdGp = StringHelper.evl(schInfo.getFieldString("YD_GP"), "");

				if (YmCommonConst.SCH_WORK_STAT_1.equals(sSchWorkStat)) {

					if (YmCommonConst.YD_GP_1.equals(sYdGp)) {
						EJBConnector ejbConn = new EJBConnector("default","JNDICWrkOrdReg", this);
						Boolean isTemp = (Boolean) ejbConn.trx("callACoilCraneMsgInfo", new Class[] {String.class, String.class },
																						new Object[] {YmCommonConst.TC_THHC120,sSchId });
					} else if (YmCommonConst.YD_GP_3.equals(sYdGp)) {
						EJBConnector ejbConn = new EJBConnector("default","JNDICWrkOrdReg", this);
						Boolean isTemp = (Boolean) ejbConn.trx("callBCoilCraneMsgInfo",new Class[] { String.class },
										new Object[] { StringHelper.evl(schInfo.getFieldString("YD_GP"), "")
														+ StringHelper.evl(schInfo.getFieldString("BAY_GP"),"")
														+ YmCommonConst.EQUIP_KIND_CR
														+ StringHelper.evl(schInfo.getFieldString("SCH_WORK_EQUIP_NO"),"") });

					}
				}
			}

			/**
			 * 2. FROM 위치 초기화
			 */
			{
				JDTORecord layerRc = dao.getUpStackLayerListWithSchId(sSchId);

				if (layerRc != null) {
					// throw new EJBServiceException("=스케쥴 취소=>FROM 적치단정보
					// 존재안함.");

					String sUpColGp = StringHelper.evl(layerRc.getFieldString("STACK_COL_GP"), "");
					String sUpBedGp = StringHelper.evl(layerRc.getFieldString("STACK_BED_GP"), "");
					String sUpLayerGp = StringHelper.evl(layerRc.getFieldString("STACK_LAYER_GP"), "");
					String sUpStat = "";

					if (YmCommonConst.MAIN_WORK_M.equals(sWork_Id)) {
						sUpStat = YmCommonConst.STACK_LAYER_STAT_S;
					} else {
						sUpStat = YmCommonConst.STACK_LAYER_STAT_L;
						/**
						 * 작업예약이 존재하면 'S'
						 */
						JDTORecord stockJr = dao.getStockInfo(sStockId);
						if (stockJr != null) {
							String sWbookId = StringHelper.evl(stockJr.getFieldString("WBOOK_ID"), "");
							if (!"".equals(sWbookId)) {
								sUpStat = YmCommonConst.STACK_LAYER_STAT_S;
							}
						}
					}
					/*
					 * 적치단 UP위치 초기화 tb_ym_stacklayer Table : stock_id = sStockId
					 * tb_ym_stacklayer Table : stack_layer_stat= 'S'(예약상태)
					 */
					iSeq = dao.updateCraneStackLayerStat(sUpColGp, sUpBedGp, sUpLayerGp, sStockId, sUpStat);

					/**
					 * FROM 위치 상단 적치상태 수정
					 */
					if (YmCommonConst.STACK_LAYER_GP_01.equals(sUpLayerGp)) {

						/*
						 * A.B열연 Coil 권상실적 상단 왼쪽 상태정보를 UPDATE 상단 오른쪽 상태정보를
						 * UPDATE
						 */
						iSeq = YmCommonDB.setCoilUpperState_E(sUpColGp, sUpBedGp, sUpLayerGp);
					}
				}
				logger.println(LogLevel.DEBUG, this, "=스케쥴 취소=>FROM 위치 초기화.");
			}
			/**
			 * 3. TO 위치 초기화
			 */
			{
				JDTORecord layerRc = dao.getPutStackLayerListWithSchId2(sSchId);
				JDTORecord jtrBedV = null;
				JDTORecord jtrBedV02 = null;

				if (layerRc != null) {
					// throw new EJBServiceException("=스케쥴 취소=>TO 적치단정보 존재안함.");

					String sPutColGp = StringHelper.evl(layerRc.getFieldString("STACK_COL_GP"), "");
					String sPutBedGp = StringHelper.evl(layerRc.getFieldString("STACK_BED_GP"), "");
					String sPutLayerGp = StringHelper.evl(layerRc.getFieldString("STACK_LAYER_GP"), "");

					// if(YmCommonConst.MAIN_WORK_M.equals(sWork_Id)&&
					// YmCommonUtil.isLineInWork(sSchWorkKind)){
					String sPutUsageCd = YmCommonUtil.getStackColInfoWithPk(sPutColGp);

					if (YmCommonConst.STACK_COL_GP_1BDC01.equals(sPutColGp)
							|| // A열연 COIL 분기콘베이어
							YmCommonConst.STACK_COL_GP_1CDC01.equals(sPutColGp)
							|| // A열연 COIL 분기콘베이어
							YmCommonConst.STACK_COL_USAGE_CD_XX.equals(sPutUsageCd)
							|| // COIL 비상적치위치
							YmCommonConst.STACK_COL_USAGE_CD_FE.equals(sPutUsageCd)
							|| // COIL HFL보급위치
							YmCommonConst.STACK_COL_USAGE_CD_FI.equals(sPutUsageCd)
							|| // COIL HFLTAKEIN위치
							YmCommonConst.STACK_COL_USAGE_CD_FD.equals(sPutUsageCd)
							|| // COIL HFL추출위치
							YmCommonConst.STACK_COL_USAGE_CD_KE.equals(sPutUsageCd)
							|| // COIL SPM보급위치
							YmCommonConst.STACK_COL_USAGE_CD_KI.equals(sPutUsageCd)
							|| // COIL SPMTAKEIN위치
							YmCommonConst.STACK_COL_USAGE_CD_KD.equals(sPutUsageCd)) {// COIL SPM추출위치

						logger.println(LogLevel.DEBUG, this, "=스케쥴 취소=>적치단 삭제="+ sSchWorkKind);
						logger.println(LogLevel.DEBUG, this, "=스케쥴 취소=>적치단 삭제="+ sPutColGp);
						logger.println(LogLevel.DEBUG, this, "=스케쥴 취소=>적치단 삭제="+ sStockId);

						iSeq = YmCommonDB.deleteConveyorInfo(sPutColGp, sStockId);
						
						if( sPutColGp.equals("3CKE01") && iSeq >= 0){
							ymCommonDAO dao1 = ymCommonDAO.getInstance();
							// 3CKE01의 소재정보를 검사한다.
							/*
							 * select * 
							 * from tb_ym_stacklayer
							 * where stack_col_gp = :stackcol
							 * and stack_bed_gp = stackbed
							 * */
							String sQueryId_01 = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerInfoWithStackGp";
							jtrBedV	 = dao1.getCommonInfo(sQueryId_01, new Object[]{sPutColGp,"01"});
							
							// 01 Bed에 데이터가 비어있는지 검사.
							if(jtrBedV != null){
								String sStockId_01 = StringHelper.evl(jtrBedV.getFieldString("STOCK_ID"),"");
								logger.println(LogLevel.DEBUG, this, "=스케쥴취소 추가=>01stockID="+ sStockId_01);
								if( sStockId_01.equals("") || sStockId_01 == null ){
									
									//String sQueryId_02 = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerInfoWithStackGp";
									//jtrBedV02	 = dao1.getCommonInfo(sQueryId_02, new Object[]{sPutColGp,"02"});
									
									//logger.println(LogLevel.DEBUG, this, "=스케쥴취소 추가=>02record="+ jtrBedV02);
									
									//if(jtrBedV02 != null){
									//	String sStockId_02 = StringHelper.evl(jtrBedV02.getFieldString("STOCK_ID"),"");
									//	logger.println(LogLevel.DEBUG, this, "=스케쥴취소 추가=>02stockID="+ sStockId_02);
									logger.println(LogLevel.DEBUG, this, "=스케쥴취소 추가=>02 deleteConveyorInfo(String)=" );	
										//if( !sStockId_02.equals("") && sStockId_02 != null )
											iSeq = YmCommonDB.deleteConveyorInfo(sPutColGp);
									//}
									
								}
							}
						}
						
						
						if (iSeq < 0) {
							// throw new EJBServiceException("=스케쥴 취소=>CONVEYOR
							// DELETE FAIL.");
							logger.println(LogLevel.DEBUG, this,"=스케쥴 취소=>적치단 삭제 FAIL");
						}

					} else if (YmCommonConst.MAIN_WORK_M.equals(sWork_Id)
							&& (YmCommonConst.NEW_SCH_WORK_KIND_GVFL.equals(sSchWorkKind)
									|| // Coil 제품출하상차
									YmCommonConst.NEW_SCH_WORK_KIND_GVF1.equals(sSchWorkKind)
									|| // Coil 제품출하상차
									YmCommonConst.NEW_SCH_WORK_KIND_GVF2.equals(sSchWorkKind)
									|| // Coil 제품출하상차
									YmCommonConst.NEW_SCH_WORK_KIND_GTFL.equals(sSchWorkKind)|| // COIL 제품출하상차
							    	   YmCommonConst.NEW_SCH_WORK_KIND_GTF1.equals(sSchWorkKind)|| // Coil 제품출하상차
							    	   YmCommonConst.NEW_SCH_WORK_KIND_GTF2.equals(sSchWorkKind)||    		    
							    	   YmCommonConst.NEW_SCH_WORK_KIND_GPFL.equals(sSchWorkKind)|| // COIL 제품출하상차
							    	   YmCommonConst.NEW_SCH_WORK_KIND_GPF1.equals(sSchWorkKind)|| // Coil 제품출하상차
							    	   YmCommonConst.NEW_SCH_WORK_KIND_GPF2.equals(sSchWorkKind)||
									YmCommonConst.NEW_SCH_WORK_KIND_GVML.equals(sSchWorkKind)
									|| // Coil 제품이송상차
									YmCommonConst.NEW_SCH_WORK_KIND_GVM2.equals(sSchWorkKind)
									|| // COIL 제품이송상차
									YmCommonConst.NEW_SCH_WORK_KIND_GVM3.equals(sSchWorkKind)
									|| // COIL 제품이송상차
									YmCommonConst.NEW_SCH_WORK_KIND_CVML.equals(sSchWorkKind)
									|| // Coil 소재이송상차
									YmCommonConst.NEW_SCH_WORK_KIND_CVM2.equals(sSchWorkKind)  // COIL
									||									// 소재이송상차
									YmCommonConst.NEW_SCH_WORK_KIND_CVM3.equals(sSchWorkKind) // COIL 소재이송상차
							)) {
						
						
						
						String sQueryId  = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerInfoWithPkCHK";	   	
					   	JDTORecord lyrJr = dao2.getCommonInfo(sQueryId,new Object[]{sPutColGp,sPutBedGp,sPutLayerGp,sStockId});
					   						   	
			    		if(lyrJr != null){
							/*
							 * 적치단 UP위치 Clear tb_ym_stacklayer Table : stock_id =
							 * ''(Empty) tb_ym_stacklayer Table : stack_layer_stat =
							 * 'E'(적치가능)
							 */
							iSeq = dao.updateCraneStackLayerStat(sPutColGp,
									sPutBedGp, sPutLayerGp, "",
									YmCommonConst.STACK_LAYER_STAT_E);
	
							/*
							 * 적치단 PUT 위치 CLOSE tb_ym_stacklayer Table :
							 * STACK_LAYER_ACTIVE_STAT_C = 'C'(적치가능)
							 */
							if(YmCommonConst.NEW_SCH_WORK_KIND_GVFL.equals(sSchWorkKind)
								|| // Coil 제품출하상차
								YmCommonConst.NEW_SCH_WORK_KIND_GVF1.equals(sSchWorkKind)
								|| // Coil 제품출하상차
								YmCommonConst.NEW_SCH_WORK_KIND_GVF2.equals(sSchWorkKind)
								  // Coil 제품출하상차)
								){
								iSeq = dao.updateCraneStackLayerActivStat(sPutColGp,
										sPutBedGp, sPutLayerGp,
										YmCommonConst.STACK_LAYER_ACTIVE_STAT_O);
							}else{
								iSeq = dao.updateCraneStackLayerActivStat(sPutColGp,
										sPutBedGp, sPutLayerGp,
										YmCommonConst.STACK_LAYER_ACTIVE_STAT_C);
							}

			    		}

					} else { 
						
						String sQueryId  = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerInfoWithPkCHK";	   	
					   	JDTORecord lyrJr = dao2.getCommonInfo(sQueryId,new Object[]{sPutColGp,sPutBedGp,sPutLayerGp,sStockId});
				  
					   	
			    		if(lyrJr != null){
							/*
							 * 적치단 UP위치 Clear tb_ym_stacklayer Table : stock_id =
							 * ''(Empty) tb_ym_stacklayer Table : stack_layer_stat =
							 * 'E'(적치가능)
							 */
							iSeq = dao.updateCraneStackLayerStat(sPutColGp,
									sPutBedGp, sPutLayerGp, "",
									YmCommonConst.STACK_LAYER_STAT_E);
	
							/**
							 * FROM 위치 상단 적치상태 수정
							 */
							if (YmCommonConst.STACK_LAYER_GP_01.equals(sPutLayerGp)) {
	
								/*
								 * A.B열연 Coil 권상실적 상단 왼쪽 상태정보를 UPDATE 상단 오른쪽 상태정보를
								 * UPDATE
								 */
								iSeq = YmCommonDB.setCoilUpperState_V(sPutColGp, sPutBedGp, sPutLayerGp);
							}
			    		}
					}
				}
				logger.println(LogLevel.DEBUG, this, "=스케쥴 취소=>TO 위치 초기화.");
			}
			/**
			 * 4. 크레인 설비 초기화
			 */
			{
				if (YmCommonConst.SCH_WORK_STAT_1.equals(sSchWorkStat)) {

					iSeq = dao.updateSubCraneEquipStat(StringHelper.evl(schInfo.getFieldString("YD_GP"), ""), 
														StringHelper.evl(schInfo.getFieldString("BAY_GP"), ""),
														YmCommonConst.EQUIP_KIND_CR,
														StringHelper.evl(schInfo.getFieldString("SCH_WORK_EQUIP_NO"), ""),
														YmCommonConst.WORK_PROG_STAT_W, "");
					logger.println(LogLevel.DEBUG, this,"=스케쥴 취소=>크레인 설비 초기화.");
				}
			}
			
			/**
			 * 5. 작업예약  to위치  정보 삭제
			 */
			{	
				if(YmCommonConst.NEW_SCH_WORK_KIND_CVM8.equals(sSchWorkKind)|| // COIL 소재차량이송상차(R)
				   YmCommonConst.NEW_SCH_WORK_KIND_CVM9.equals(sSchWorkKind)|| // COIL 소재차량이송하차(R)	
				   YmCommonConst.NEW_SCH_WORK_KIND_GVM8.equals(sSchWorkKind)|| // COIL 제품차량이송상차(R)
				   YmCommonConst.NEW_SCH_WORK_KIND_GVM9.equals(sSchWorkKind)|| // COIL 제품차량이송하차(R)	
				   YmCommonConst.NEW_SCH_WORK_KIND_CVM6.equals(sSchWorkKind)|| // COIL 소재차량이송상차(L)
				   YmCommonConst.NEW_SCH_WORK_KIND_CVM7.equals(sSchWorkKind)|| // COIL 소재차량이송하차(L)	
				   YmCommonConst.NEW_SCH_WORK_KIND_GVM6.equals(sSchWorkKind)|| // COIL 제품차량이송상차(L)
				   YmCommonConst.NEW_SCH_WORK_KIND_GVM7.equals(sSchWorkKind)){ // COIL 제품차량이송하차(L)
		
					iSeq = dao.updateputlocWbookId(sSchId);
					logger.println(LogLevel.DEBUG, this, "=스케쥴 취소=>작업예약  to위치  정보 삭제.");
				}
			}
			
			/**
			 * 5. 스케쥴 정보 삭제
			 */
			{
				iSeq = dao.deleteSchInfo(sSchId);
				logger.println(LogLevel.DEBUG, this, "=스케쥴 취소=>스케쥴 정보 삭제.");
			}

			isSuccess = true;
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return isSuccess;
	}


	/**
	 * 오퍼레이션명 : 
	 * 임가공 PIDEV
	 * COIL 스케쥴 취소 메소드.
	 * 
	 * param String :
	 *            SCH_ID
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                  
	public boolean cancelCoilSchInfoPI(String sSchId) {
		ymCommonDAO dao2 = ymCommonDAO.getInstance();
		boolean isSuccess = false;
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			int iSeq = 0;

			String sSchWorkStat = "";
			String sStockId 	= "";
			String sSchWorkKind = "";
			String sWork_Id 	= "";

			JDTORecord schInfo = dao.getSchInfoWithSchId(sSchId);

			if (schInfo == null) {
				throw new EJBServiceException("=스케쥴 취소=>스케쥴정보 존재안함.");
			}

			sStockId 	 = StringHelper.evl(schInfo.getFieldString("STOCK_ID"), "");
			sSchWorkKind = StringHelper.evl(schInfo.getFieldString("SCH_WORK_KIND"), "");
			sWork_Id 	 = StringHelper.evl(schInfo.getFieldString("SCH_WORK_AID_YN"), "");
			/**
			 * 1. 스케쥴 정보 가져온다.
			 */
			{
				sSchWorkStat = StringHelper.evl(schInfo.getFieldString("SCH_WORK_STAT"), "");

				if (!YmCommonConst.SCH_WORK_STAT_S.equals(sSchWorkStat)
						&& !YmCommonConst.SCH_WORK_STAT_1.equals(sSchWorkStat)) {

					// throw new EJBServiceException("=스케쥴 취소=>스케쥴정보를 취소할 수
					// 없슴.");
					logger.println(LogLevel.DEBUG, this, "=스케쥴 취소=>스케쥴정보를 취소할 수 없슴.");
				}
			}

			/**
			 * 6. 작업취소 전문 전송
			 */
			{
				String sYdGp = StringHelper.evl(schInfo.getFieldString("YD_GP"), "");

				if (YmCommonConst.SCH_WORK_STAT_1.equals(sSchWorkStat)) {

					if (YmCommonConst.YD_GP_1.equals(sYdGp)) {
						EJBConnector ejbConn = new EJBConnector("default","JNDICWrkOrdReg", this);
						Boolean isTemp = (Boolean) ejbConn.trx("callACoilCraneMsgInfo", new Class[] {String.class, String.class },
																						new Object[] {YmCommonConst.TC_THHC120,sSchId });
					} else if (YmCommonConst.YD_GP_3.equals(sYdGp)) {
						EJBConnector ejbConn = new EJBConnector("default","JNDICWrkOrdReg", this);
						Boolean isTemp = (Boolean) ejbConn.trx("callBCoilCraneMsgInfo",new Class[] { String.class },
										new Object[] { StringHelper.evl(schInfo.getFieldString("YD_GP"), "")
														+ StringHelper.evl(schInfo.getFieldString("BAY_GP"),"")
														+ YmCommonConst.EQUIP_KIND_CR
														+ StringHelper.evl(schInfo.getFieldString("SCH_WORK_EQUIP_NO"),"") });

					}
				}
			}

			/**
			 * 2. FROM 위치 초기화
			 */
			{
				JDTORecord layerRc = dao.getUpStackLayerListWithSchId(sSchId);

				if (layerRc != null) {
					// throw new EJBServiceException("=스케쥴 취소=>FROM 적치단정보
					// 존재안함.");

					String sUpColGp = StringHelper.evl(layerRc.getFieldString("STACK_COL_GP"), "");
					String sUpBedGp = StringHelper.evl(layerRc.getFieldString("STACK_BED_GP"), "");
					String sUpLayerGp = StringHelper.evl(layerRc.getFieldString("STACK_LAYER_GP"), "");
					String sUpStat = "";

					if (YmCommonConst.MAIN_WORK_M.equals(sWork_Id)) {
						sUpStat = YmCommonConst.STACK_LAYER_STAT_S;
					} else {
						sUpStat = YmCommonConst.STACK_LAYER_STAT_L;
						/**
						 * 작업예약이 존재하면 'S'
						 */
						JDTORecord stockJr = dao.getStockInfoPI(sStockId);
						if (stockJr != null) {
							String sWbookId = StringHelper.evl(stockJr.getFieldString("WBOOK_ID"), "");
							if (!"".equals(sWbookId)) {
								sUpStat = YmCommonConst.STACK_LAYER_STAT_S;
							}
						}
					}
					/*
					 * 적치단 UP위치 초기화 tb_ym_stacklayer Table : stock_id = sStockId
					 * tb_ym_stacklayer Table : stack_layer_stat= 'S'(예약상태)
					 */
					iSeq = dao.updateCraneStackLayerStat(sUpColGp, sUpBedGp, sUpLayerGp, sStockId, sUpStat);

					/**
					 * FROM 위치 상단 적치상태 수정
					 */
					if (YmCommonConst.STACK_LAYER_GP_01.equals(sUpLayerGp)) {

						/*
						 * A.B열연 Coil 권상실적 상단 왼쪽 상태정보를 UPDATE 상단 오른쪽 상태정보를
						 * UPDATE
						 */
						iSeq = YmCommonDB.setCoilUpperState_E(sUpColGp, sUpBedGp, sUpLayerGp);
					}
				}
				logger.println(LogLevel.DEBUG, this, "=스케쥴 취소=>FROM 위치 초기화.");
			}
			/**
			 * 3. TO 위치 초기화
			 */
			{
				JDTORecord layerRc = dao.getPutStackLayerListWithSchId2(sSchId);
				JDTORecord jtrBedV = null;
				JDTORecord jtrBedV02 = null;

				if (layerRc != null) {
					// throw new EJBServiceException("=스케쥴 취소=>TO 적치단정보 존재안함.");

					String sPutColGp = StringHelper.evl(layerRc.getFieldString("STACK_COL_GP"), "");
					String sPutBedGp = StringHelper.evl(layerRc.getFieldString("STACK_BED_GP"), "");
					String sPutLayerGp = StringHelper.evl(layerRc.getFieldString("STACK_LAYER_GP"), "");

					// if(YmCommonConst.MAIN_WORK_M.equals(sWork_Id)&&
					// YmCommonUtil.isLineInWork(sSchWorkKind)){
					String sPutUsageCd = YmCommonUtil.getStackColInfoWithPk(sPutColGp);

					if (YmCommonConst.STACK_COL_GP_1BDC01.equals(sPutColGp)
							|| // A열연 COIL 분기콘베이어
							YmCommonConst.STACK_COL_GP_1CDC01.equals(sPutColGp)
							|| // A열연 COIL 분기콘베이어
							YmCommonConst.STACK_COL_USAGE_CD_XX.equals(sPutUsageCd)
							|| // COIL 비상적치위치
							YmCommonConst.STACK_COL_USAGE_CD_FE.equals(sPutUsageCd)
							|| // COIL HFL보급위치
							YmCommonConst.STACK_COL_USAGE_CD_FI.equals(sPutUsageCd)
							|| // COIL HFLTAKEIN위치
							YmCommonConst.STACK_COL_USAGE_CD_FD.equals(sPutUsageCd)
							|| // COIL HFL추출위치
							YmCommonConst.STACK_COL_USAGE_CD_KE.equals(sPutUsageCd)
							|| // COIL SPM보급위치
							YmCommonConst.STACK_COL_USAGE_CD_KI.equals(sPutUsageCd)
							|| // COIL SPMTAKEIN위치
							YmCommonConst.STACK_COL_USAGE_CD_KD.equals(sPutUsageCd)) {// COIL SPM추출위치

						logger.println(LogLevel.DEBUG, this, "=스케쥴 취소=>적치단 삭제="+ sSchWorkKind);
						logger.println(LogLevel.DEBUG, this, "=스케쥴 취소=>적치단 삭제="+ sPutColGp);
						logger.println(LogLevel.DEBUG, this, "=스케쥴 취소=>적치단 삭제="+ sStockId);

						iSeq = YmCommonDB.deleteConveyorInfo(sPutColGp, sStockId);
						
						if( sPutColGp.equals("3CKE01") && iSeq >= 0){
							ymCommonDAO dao1 = ymCommonDAO.getInstance();
							// 3CKE01의 소재정보를 검사한다.
							/*
							 * select * 
							 * from tb_ym_stacklayer
							 * where stack_col_gp = :stackcol
							 * and stack_bed_gp = stackbed
							 * */
							String sQueryId_01 = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerInfoWithStackGp";
							jtrBedV	 = dao1.getCommonInfo(sQueryId_01, new Object[]{sPutColGp,"01"});
							
							// 01 Bed에 데이터가 비어있는지 검사.
							if(jtrBedV != null){
								String sStockId_01 = StringHelper.evl(jtrBedV.getFieldString("STOCK_ID"),"");
								logger.println(LogLevel.DEBUG, this, "=스케쥴취소 추가=>01stockID="+ sStockId_01);
								if( sStockId_01.equals("") || sStockId_01 == null ){
									
									//String sQueryId_02 = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerInfoWithStackGp";
									//jtrBedV02	 = dao1.getCommonInfo(sQueryId_02, new Object[]{sPutColGp,"02"});
									
									//logger.println(LogLevel.DEBUG, this, "=스케쥴취소 추가=>02record="+ jtrBedV02);
									
									//if(jtrBedV02 != null){
									//	String sStockId_02 = StringHelper.evl(jtrBedV02.getFieldString("STOCK_ID"),"");
									//	logger.println(LogLevel.DEBUG, this, "=스케쥴취소 추가=>02stockID="+ sStockId_02);
									logger.println(LogLevel.DEBUG, this, "=스케쥴취소 추가=>02 deleteConveyorInfo(String)=" );	
										//if( !sStockId_02.equals("") && sStockId_02 != null )
											iSeq = YmCommonDB.deleteConveyorInfo(sPutColGp);
									//}
									
								}
							}
						}
						
						
						if (iSeq < 0) {
							// throw new EJBServiceException("=스케쥴 취소=>CONVEYOR
							// DELETE FAIL.");
							logger.println(LogLevel.DEBUG, this,"=스케쥴 취소=>적치단 삭제 FAIL");
						}

					} else if (YmCommonConst.MAIN_WORK_M.equals(sWork_Id)
							&& (YmCommonConst.NEW_SCH_WORK_KIND_GVFL.equals(sSchWorkKind)
									|| // Coil 제품출하상차
									YmCommonConst.NEW_SCH_WORK_KIND_GVF1.equals(sSchWorkKind)
									|| // Coil 제품출하상차
									YmCommonConst.NEW_SCH_WORK_KIND_GVF2.equals(sSchWorkKind)
									|| // Coil 제품출하상차
									YmCommonConst.NEW_SCH_WORK_KIND_GTFL.equals(sSchWorkKind)|| // COIL 제품출하상차
							    	   YmCommonConst.NEW_SCH_WORK_KIND_GTF1.equals(sSchWorkKind)|| // Coil 제품출하상차
							    	   YmCommonConst.NEW_SCH_WORK_KIND_GTF2.equals(sSchWorkKind)||    		    
							    	   YmCommonConst.NEW_SCH_WORK_KIND_GPFL.equals(sSchWorkKind)|| // COIL 제품출하상차
							    	   YmCommonConst.NEW_SCH_WORK_KIND_GPF1.equals(sSchWorkKind)|| // Coil 제품출하상차
							    	   YmCommonConst.NEW_SCH_WORK_KIND_GPF2.equals(sSchWorkKind)||
									YmCommonConst.NEW_SCH_WORK_KIND_GVML.equals(sSchWorkKind)
									|| // Coil 제품이송상차
									YmCommonConst.NEW_SCH_WORK_KIND_GVM2.equals(sSchWorkKind)
									|| // COIL 제품이송상차
									YmCommonConst.NEW_SCH_WORK_KIND_GVM3.equals(sSchWorkKind)
									|| // COIL 제품이송상차
									YmCommonConst.NEW_SCH_WORK_KIND_CVML.equals(sSchWorkKind)
									|| // Coil 소재이송상차
									YmCommonConst.NEW_SCH_WORK_KIND_CVM2.equals(sSchWorkKind)  // COIL
									||									// 소재이송상차
									YmCommonConst.NEW_SCH_WORK_KIND_CVM3.equals(sSchWorkKind) // COIL 소재이송상차
							)) {
						
						
						
						String sQueryId  = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerInfoWithPkCHK";	   	
					   	JDTORecord lyrJr = dao2.getCommonInfo(sQueryId,new Object[]{sPutColGp,sPutBedGp,sPutLayerGp,sStockId});
					   						   	
			    		if(lyrJr != null){
							/*
							 * 적치단 UP위치 Clear tb_ym_stacklayer Table : stock_id =
							 * ''(Empty) tb_ym_stacklayer Table : stack_layer_stat =
							 * 'E'(적치가능)
							 */
							iSeq = dao.updateCraneStackLayerStat(sPutColGp,
									sPutBedGp, sPutLayerGp, "",
									YmCommonConst.STACK_LAYER_STAT_E);
	
							/*
							 * 적치단 PUT 위치 CLOSE tb_ym_stacklayer Table :
							 * STACK_LAYER_ACTIVE_STAT_C = 'C'(적치가능)
							 */
							if(YmCommonConst.NEW_SCH_WORK_KIND_GVFL.equals(sSchWorkKind)
								|| // Coil 제품출하상차
								YmCommonConst.NEW_SCH_WORK_KIND_GVF1.equals(sSchWorkKind)
								|| // Coil 제품출하상차
								YmCommonConst.NEW_SCH_WORK_KIND_GVF2.equals(sSchWorkKind)
								  // Coil 제품출하상차)
								){
								iSeq = dao.updateCraneStackLayerActivStat(sPutColGp,
										sPutBedGp, sPutLayerGp,
										YmCommonConst.STACK_LAYER_ACTIVE_STAT_O);
							}else{
								iSeq = dao.updateCraneStackLayerActivStat(sPutColGp,
										sPutBedGp, sPutLayerGp,
										YmCommonConst.STACK_LAYER_ACTIVE_STAT_C);
							}

			    		}

					} else { 
						
						String sQueryId  = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerInfoWithPkCHK";	   	
					   	JDTORecord lyrJr = dao2.getCommonInfo(sQueryId,new Object[]{sPutColGp,sPutBedGp,sPutLayerGp,sStockId});
				  
					   	
			    		if(lyrJr != null){
							/*
							 * 적치단 UP위치 Clear tb_ym_stacklayer Table : stock_id =
							 * ''(Empty) tb_ym_stacklayer Table : stack_layer_stat =
							 * 'E'(적치가능)
							 */
							iSeq = dao.updateCraneStackLayerStat(sPutColGp,
									sPutBedGp, sPutLayerGp, "",
									YmCommonConst.STACK_LAYER_STAT_E);
	
							/**
							 * FROM 위치 상단 적치상태 수정
							 */
							if (YmCommonConst.STACK_LAYER_GP_01.equals(sPutLayerGp)) {
	
								/*
								 * A.B열연 Coil 권상실적 상단 왼쪽 상태정보를 UPDATE 상단 오른쪽 상태정보를
								 * UPDATE
								 */
								iSeq = YmCommonDB.setCoilUpperState_V(sPutColGp, sPutBedGp, sPutLayerGp);
							}
			    		}
					}
				}
				logger.println(LogLevel.DEBUG, this, "=스케쥴 취소=>TO 위치 초기화.");
			}
			/**
			 * 4. 크레인 설비 초기화
			 */
			{
				if (YmCommonConst.SCH_WORK_STAT_1.equals(sSchWorkStat)) {

					iSeq = dao.updateSubCraneEquipStat(StringHelper.evl(schInfo.getFieldString("YD_GP"), ""), 
														StringHelper.evl(schInfo.getFieldString("BAY_GP"), ""),
														YmCommonConst.EQUIP_KIND_CR,
														StringHelper.evl(schInfo.getFieldString("SCH_WORK_EQUIP_NO"), ""),
														YmCommonConst.WORK_PROG_STAT_W, "");
					logger.println(LogLevel.DEBUG, this,"=스케쥴 취소=>크레인 설비 초기화.");
				}
			}
			
			/**
			 * 5. 작업예약  to위치  정보 삭제
			 */
			{	
				if(YmCommonConst.NEW_SCH_WORK_KIND_CVM8.equals(sSchWorkKind)|| // COIL 소재차량이송상차(R)
				   YmCommonConst.NEW_SCH_WORK_KIND_CVM9.equals(sSchWorkKind)|| // COIL 소재차량이송하차(R)	
				   YmCommonConst.NEW_SCH_WORK_KIND_GVM8.equals(sSchWorkKind)|| // COIL 제품차량이송상차(R)
				   YmCommonConst.NEW_SCH_WORK_KIND_GVM9.equals(sSchWorkKind)|| // COIL 제품차량이송하차(R)	
				   YmCommonConst.NEW_SCH_WORK_KIND_CVM6.equals(sSchWorkKind)|| // COIL 소재차량이송상차(L)
				   YmCommonConst.NEW_SCH_WORK_KIND_CVM7.equals(sSchWorkKind)|| // COIL 소재차량이송하차(L)	
				   YmCommonConst.NEW_SCH_WORK_KIND_GVM6.equals(sSchWorkKind)|| // COIL 제품차량이송상차(L)
				   YmCommonConst.NEW_SCH_WORK_KIND_GVM7.equals(sSchWorkKind)){ // COIL 제품차량이송하차(L)
		
					iSeq = dao.updateputlocWbookId(sSchId);
					logger.println(LogLevel.DEBUG, this, "=스케쥴 취소=>작업예약  to위치  정보 삭제.");
				}
			}
			
			/**
			 * 5. 스케쥴 정보 삭제
			 */
			{
				iSeq = dao.deleteSchInfo(sSchId);
				logger.println(LogLevel.DEBUG, this, "=스케쥴 취소=>스케쥴 정보 삭제.");
			}

			isSuccess = true;
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return isSuccess;
	}
	
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * SLAB 스케쥴 취소 메소드
	 * 
	 * param String : SCH_ID
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                   
	public boolean cancelSlabSchInfo(String sSchId) {
		ymCommonDAO dao2 = ymCommonDAO.getInstance();
		boolean isSuccess = false;
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			int iSeq = 0;

			String sSchWorkStat = "";
			String sStockId = "";
			String sSchWorkKind = "";
			String sWork_Id = "";

			JDTORecord schInfo = dao.getSchInfoWithSchId(sSchId);

			if (schInfo == null) {
				throw new EJBServiceException("=스케쥴 취소=>스케쥴정보 존재안함.");
			}

			sStockId 		= StringHelper.evl(schInfo.getFieldString("STOCK_ID"), "");
			sSchWorkKind 	= StringHelper.evl(schInfo.getFieldString("SCH_WORK_KIND"), "");
			sWork_Id 		= StringHelper.evl(schInfo.getFieldString("SCH_WORK_AID_YN"), "");
			/**
			 * 1. 스케쥴 정보 가져온다
			 */
			{
				sSchWorkStat = StringHelper.evl(schInfo.getFieldString("SCH_WORK_STAT"), "");

				if (!YmCommonConst.SCH_WORK_STAT_S.equals(sSchWorkStat)
						&& !YmCommonConst.SCH_WORK_STAT_1.equals(sSchWorkStat)) {

					// throw new EJBServiceException("=스케쥴 취소=>스케쥴정보를 취소할 수 없슴.");
					logger.println(LogLevel.DEBUG, this, "=스케쥴 취소=>스케쥴정보를 취소할 수 없슴.");
				}
			}

			/**
			 * 6. 작업취소 전문 전송
			 */
			{

				String sYdGp = StringHelper.evl(schInfo.getFieldString("YD_GP"), "");

				if (YmCommonConst.SCH_WORK_STAT_1.equals(sSchWorkStat)) {

					if (YmCommonConst.YD_GP_2.equals(sYdGp)
						||YmCommonConst.YD_GP_0.equals(sYdGp)) {
						EJBConnector ejbConn = new EJBConnector("default", "JNDICWrkOrdReg", this);
						Boolean isTemp = (Boolean) ejbConn.trx(	"callBSlabCraneMsgInfo",new Class[] { String.class },
										new Object[] { StringHelper.evl(schInfo.getFieldString("YD_GP"), "")
													+ StringHelper.evl(schInfo.getFieldString("BAY_GP"),"")
													+ YmCommonConst.EQUIP_KIND_CR
													+ StringHelper.evl(schInfo.getFieldString("SCH_WORK_EQUIP_NO"),"") });
					}
				}
			}

			/**
			 * 2. FROM 위치 초기화
			 */
			{
				JDTORecord layerRc = dao.getUpStackLayerListWithSchId(sSchId);

				if (layerRc != null) {
					// throw new EJBServiceException("=스케쥴 취소=>FROM 적치단정보
					// 존재안함.");

					String sUpColGp = StringHelper.evl(layerRc.getFieldString("STACK_COL_GP"), "");
					String sUpBedGp = StringHelper.evl(layerRc.getFieldString("STACK_BED_GP"), "");
					String sUpLayerGp = StringHelper.evl(layerRc.getFieldString("STACK_LAYER_GP"), "");
					String sUpStat = "";

					if (YmCommonConst.MAIN_WORK_M.equals(sWork_Id)) {
						sUpStat = YmCommonConst.STACK_LAYER_STAT_S;
					} else {
						sUpStat = YmCommonConst.STACK_LAYER_STAT_L;
						/**
						 * 작업예약이 존재하면 'S'
						 */
						JDTORecord stockJr = dao.getStockInfo(sStockId);
						if (stockJr != null) {
							String sWbookId = StringHelper.evl(stockJr.getFieldString("WBOOK_ID"), "");
							if (!"".equals(sWbookId)) {
								sUpStat = YmCommonConst.STACK_LAYER_STAT_S;
							}
						}
					}
					/*
					 * 적치단 UP위치 초기화 tb_ym_stacklayer Table : stock_id = sStockId
					 * tb_ym_stacklayer Table : stack_layer_stat= 'S'(예약상태)
					 */
					iSeq = dao.updateCraneStackLayerStat(sUpColGp, sUpBedGp, sUpLayerGp, sStockId, sUpStat);

					/*
					 * B열연 Slab 바로 위 상단 상태정보를 UPDATE
					 */
					iSeq = YmCommonDB.setSlabUpperState_E(sUpColGp, sUpBedGp,sUpLayerGp);
				}
				logger.println(LogLevel.DEBUG, this, "=스케쥴 취소=>FROM 위치 초기화.");
			}
			/**
			 * 3. TO 위치 초기화
			 */
			{
				JDTORecord layerRc = dao.getPutStackLayerListWithSchId2(sSchId);

				if (layerRc != null) {
					// throw new EJBServiceException("=스케쥴 취소=>TO 적치단정보 존재안함.");

					String sPutColGp = StringHelper.evl(layerRc.getFieldString("STACK_COL_GP"), "");
					String sPutBedGp = StringHelper.evl(layerRc.getFieldString("STACK_BED_GP"), "");
					String sPutLayerGp = StringHelper.evl(layerRc.getFieldString("STACK_LAYER_GP"), "");

					// if(YmCommonConst.MAIN_WORK_M.equals(sWork_Id)&&
					// YmCommonUtil.isLineInWork(sSchWorkKind)){
					String sPutUsageCd = YmCommonUtil.getStackColInfoWithPk(sPutColGp);

					if (YmCommonConst.STACK_COL_USAGE_CD_XX.equals(sPutUsageCd)|| // SLAB 비상적치위치
						YmCommonConst.STACK_COL_USAGE_CD_SE.equals(sPutUsageCd)|| // SLAB Scafing 입측
						YmCommonConst.STACK_COL_USAGE_CD_SD.equals(sPutUsageCd)) {// SLAB Scafing 출측

						logger.println(LogLevel.DEBUG, this, "=스케쥴 취소=>적치단 삭제="+ sSchWorkKind);
						logger.println(LogLevel.DEBUG, this, "=스케쥴 취소=>적치단 삭제="+ sPutColGp);
						logger.println(LogLevel.DEBUG, this, "=스케쥴 취소=>적치단 삭제="+ sStockId);

						iSeq = YmCommonDB.deleteConveyorInfo(sPutColGp,
															sStockId);
						if (iSeq < 0) {
							// throw new EJBServiceException("=스케쥴 취소=>CONVEYOR
							// DELETE FAIL.");
							logger.println(LogLevel.DEBUG, this, "=스케쥴 취소=>적치단 삭제 FAIL");
						}

					} else if (YmCommonConst.MAIN_WORK_M.equals(sWork_Id)
							&& (YmCommonConst.NEW_SCH_WORK_KIND_SVML.equals(sSchWorkKind) // Slab 이송상차
							// ||YmCommonConst.NEW_SCH_WORK_KIND_SVMU.equals(sSchWorkKind)
							// //Slab 이송하차
							)) {

						/*
						 * 적치단 UP위치 Clear tb_ym_stacklayer Table : stock_id =
						 * ''(Empty) tb_ym_stacklayer Table : stack_layer_stat =
						 * 'E'(적치가능)
						 */
						iSeq = dao.updateCraneStackLayerStat(sPutColGp
															, sPutBedGp
															, sPutLayerGp
															, ""
															, YmCommonConst.STACK_LAYER_STAT_E);

						/*
						 * 적치단 PUT 위치 CLOSE tb_ym_stacklayer Table :
						 * STACK_LAYER_ACTIVE_STAT_C = 'C'(적치가능)
						 */
					//	iSeq = dao.updateCraneStackLayerActivStat(sPutColGp
					//											, sPutBedGp
					//											, sPutLayerGp
					//											, YmCommonConst.STACK_LAYER_ACTIVE_STAT_C);

						/*
						 * 적치열 CARD_NO 삭제 tb_ym_stackcol Table : car_card_no =
						 * ''(삭제)
						 */
						iSeq = dao.updateStackColCardNo(sPutColGp);
						
						//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
				        EJBConnector ejbConn2 = new EJBConnector("default","JNDITsInfoReg",this);
						ejbConn2.trx("CarPointinforeg", new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class},
					  	             new Object[]{"B","","",sPutColGp,"","","C"});
						
						//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
				        EJBConnector ejbConn3 = new EJBConnector("default","JNDITsInfoReg",this);
						ejbConn3.trx("CarPointinforeg", new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class},
					  	             new Object[]{"2","","",sPutColGp,"","","C"});

					} else {
						
						String sQueryId  = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerInfoWithPkCHK";	   	
					   	JDTORecord lyrJr = dao2.getCommonInfo(sQueryId,new Object[]{sPutColGp,sPutBedGp,sPutLayerGp,sStockId});
					    
					   	
			    		if(lyrJr != null){			    			
				    		 
							/*
							 * 적치단 UP위치 Clear tb_ym_stacklayer Table : stock_id =
							 * ''(Empty) tb_ym_stacklayer Table : stack_layer_stat =
							 * 'E'(적치가능)
							 */
							iSeq = dao.updateCraneStackLayerStat(sPutColGp,
									sPutBedGp, sPutLayerGp, "",
									YmCommonConst.STACK_LAYER_STAT_E);
	
							/*
							 * B열연 Slab 바로 위 상단 상태정보를 UPDATE
							 */
							iSeq = YmCommonDB.setSlabUpperState_V(sPutColGp, sPutBedGp, sPutLayerGp);
			    		}
					}
				}
				logger.println(LogLevel.DEBUG, this, "=스케쥴 취소=>TO 위치 초기화.");
			}
			/**
			 * 4. 크레인 설비 초기화
			 */
			{
				if (YmCommonConst.SCH_WORK_STAT_1.equals(sSchWorkStat)) {

					iSeq = dao.updateSubCraneEquipStat(StringHelper.evl(schInfo.getFieldString("YD_GP"), "")
							, StringHelper.evl(schInfo.getFieldString("BAY_GP"), "")
							, YmCommonConst.EQUIP_KIND_CR
							, StringHelper.evl(schInfo.getFieldString("SCH_WORK_EQUIP_NO"), "")
							, YmCommonConst.WORK_PROG_STAT_W, "");
					logger.println(LogLevel.DEBUG, this,"=스케쥴 취소=>크레인 설비 초기화.");
				}
			}
			/**
			 * 5. 스케쥴 정보 삭제
			 */
			{
				iSeq = dao.deleteSchInfo(sSchId);
				logger.println(LogLevel.DEBUG, this, "=스케쥴 취소=>스케쥴 정보 삭제.");
			}

			isSuccess = true;
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return isSuccess;
	}
	

	/**
	 * 오퍼레이션명 : 
	 * 임가공 PIDEV
	 * SLAB 스케쥴 취소 메소드
	 * 
	 * param String : SCH_ID
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                   
	public boolean cancelSlabSchInfoPI(String sSchId) {
		ymCommonDAO dao2 = ymCommonDAO.getInstance();
		boolean isSuccess = false;
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			int iSeq = 0;

			String sSchWorkStat = "";
			String sStockId = "";
			String sSchWorkKind = "";
			String sWork_Id = "";

			JDTORecord schInfo = dao.getSchInfoWithSchId(sSchId);

			if (schInfo == null) {
				throw new EJBServiceException("=스케쥴 취소=>스케쥴정보 존재안함.");
			}

			sStockId 		= StringHelper.evl(schInfo.getFieldString("STOCK_ID"), "");
			sSchWorkKind 	= StringHelper.evl(schInfo.getFieldString("SCH_WORK_KIND"), "");
			sWork_Id 		= StringHelper.evl(schInfo.getFieldString("SCH_WORK_AID_YN"), "");
			/**
			 * 1. 스케쥴 정보 가져온다
			 */
			{
				sSchWorkStat = StringHelper.evl(schInfo.getFieldString("SCH_WORK_STAT"), "");

				if (!YmCommonConst.SCH_WORK_STAT_S.equals(sSchWorkStat)
						&& !YmCommonConst.SCH_WORK_STAT_1.equals(sSchWorkStat)) {

					// throw new EJBServiceException("=스케쥴 취소=>스케쥴정보를 취소할 수 없슴.");
					logger.println(LogLevel.DEBUG, this, "=스케쥴 취소=>스케쥴정보를 취소할 수 없슴.");
				}
			}

			/**
			 * 6. 작업취소 전문 전송
			 */
			{

				String sYdGp = StringHelper.evl(schInfo.getFieldString("YD_GP"), "");

				if (YmCommonConst.SCH_WORK_STAT_1.equals(sSchWorkStat)) {

					if (YmCommonConst.YD_GP_2.equals(sYdGp)
						||YmCommonConst.YD_GP_0.equals(sYdGp)) {
						EJBConnector ejbConn = new EJBConnector("default", "JNDICWrkOrdReg", this);
						Boolean isTemp = (Boolean) ejbConn.trx(	"callBSlabCraneMsgInfo",new Class[] { String.class },
										new Object[] { StringHelper.evl(schInfo.getFieldString("YD_GP"), "")
													+ StringHelper.evl(schInfo.getFieldString("BAY_GP"),"")
													+ YmCommonConst.EQUIP_KIND_CR
													+ StringHelper.evl(schInfo.getFieldString("SCH_WORK_EQUIP_NO"),"") });
					}
				}
			}

			/**
			 * 2. FROM 위치 초기화
			 */
			{
				JDTORecord layerRc = dao.getUpStackLayerListWithSchId(sSchId);

				if (layerRc != null) {
					// throw new EJBServiceException("=스케쥴 취소=>FROM 적치단정보
					// 존재안함.");

					String sUpColGp = StringHelper.evl(layerRc.getFieldString("STACK_COL_GP"), "");
					String sUpBedGp = StringHelper.evl(layerRc.getFieldString("STACK_BED_GP"), "");
					String sUpLayerGp = StringHelper.evl(layerRc.getFieldString("STACK_LAYER_GP"), "");
					String sUpStat = "";

					if (YmCommonConst.MAIN_WORK_M.equals(sWork_Id)) {
						sUpStat = YmCommonConst.STACK_LAYER_STAT_S;
					} else {
						sUpStat = YmCommonConst.STACK_LAYER_STAT_L;
						/**
						 * 작업예약이 존재하면 'S'
						 */
						JDTORecord stockJr = dao.getStockInfoPI(sStockId);
						if (stockJr != null) {
							String sWbookId = StringHelper.evl(stockJr.getFieldString("WBOOK_ID"), "");
							if (!"".equals(sWbookId)) {
								sUpStat = YmCommonConst.STACK_LAYER_STAT_S;
							}
						}
					}
					/*
					 * 적치단 UP위치 초기화 tb_ym_stacklayer Table : stock_id = sStockId
					 * tb_ym_stacklayer Table : stack_layer_stat= 'S'(예약상태)
					 */
					iSeq = dao.updateCraneStackLayerStat(sUpColGp, sUpBedGp, sUpLayerGp, sStockId, sUpStat);

					/*
					 * B열연 Slab 바로 위 상단 상태정보를 UPDATE
					 */
					iSeq = YmCommonDB.setSlabUpperState_E(sUpColGp, sUpBedGp,sUpLayerGp);
				}
				logger.println(LogLevel.DEBUG, this, "=스케쥴 취소=>FROM 위치 초기화.");
			}
			/**
			 * 3. TO 위치 초기화
			 */
			{
				JDTORecord layerRc = dao.getPutStackLayerListWithSchId2(sSchId);

				if (layerRc != null) {
					// throw new EJBServiceException("=스케쥴 취소=>TO 적치단정보 존재안함.");

					String sPutColGp = StringHelper.evl(layerRc.getFieldString("STACK_COL_GP"), "");
					String sPutBedGp = StringHelper.evl(layerRc.getFieldString("STACK_BED_GP"), "");
					String sPutLayerGp = StringHelper.evl(layerRc.getFieldString("STACK_LAYER_GP"), "");

					// if(YmCommonConst.MAIN_WORK_M.equals(sWork_Id)&&
					// YmCommonUtil.isLineInWork(sSchWorkKind)){
					String sPutUsageCd = YmCommonUtil.getStackColInfoWithPk(sPutColGp);

					if (YmCommonConst.STACK_COL_USAGE_CD_XX.equals(sPutUsageCd)|| // SLAB 비상적치위치
						YmCommonConst.STACK_COL_USAGE_CD_SE.equals(sPutUsageCd)|| // SLAB Scafing 입측
						YmCommonConst.STACK_COL_USAGE_CD_SD.equals(sPutUsageCd)) {// SLAB Scafing 출측

						logger.println(LogLevel.DEBUG, this, "=스케쥴 취소=>적치단 삭제="+ sSchWorkKind);
						logger.println(LogLevel.DEBUG, this, "=스케쥴 취소=>적치단 삭제="+ sPutColGp);
						logger.println(LogLevel.DEBUG, this, "=스케쥴 취소=>적치단 삭제="+ sStockId);

						iSeq = YmCommonDB.deleteConveyorInfo(sPutColGp,
															sStockId);
						if (iSeq < 0) {
							// throw new EJBServiceException("=스케쥴 취소=>CONVEYOR
							// DELETE FAIL.");
							logger.println(LogLevel.DEBUG, this, "=스케쥴 취소=>적치단 삭제 FAIL");
						}

					} else if (YmCommonConst.MAIN_WORK_M.equals(sWork_Id)
							&& (YmCommonConst.NEW_SCH_WORK_KIND_SVML.equals(sSchWorkKind) // Slab 이송상차
							// ||YmCommonConst.NEW_SCH_WORK_KIND_SVMU.equals(sSchWorkKind)
							// //Slab 이송하차
							)) {

						/*
						 * 적치단 UP위치 Clear tb_ym_stacklayer Table : stock_id =
						 * ''(Empty) tb_ym_stacklayer Table : stack_layer_stat =
						 * 'E'(적치가능)
						 */
						iSeq = dao.updateCraneStackLayerStat(sPutColGp
															, sPutBedGp
															, sPutLayerGp
															, ""
															, YmCommonConst.STACK_LAYER_STAT_E);

						/*
						 * 적치단 PUT 위치 CLOSE tb_ym_stacklayer Table :
						 * STACK_LAYER_ACTIVE_STAT_C = 'C'(적치가능)
						 */
					//	iSeq = dao.updateCraneStackLayerActivStat(sPutColGp
					//											, sPutBedGp
					//											, sPutLayerGp
					//											, YmCommonConst.STACK_LAYER_ACTIVE_STAT_C);

						/*
						 * 적치열 CARD_NO 삭제 tb_ym_stackcol Table : car_card_no =
						 * ''(삭제)
						 */
						iSeq = dao.updateStackColCardNo(sPutColGp);
						
						//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
				        EJBConnector ejbConn2 = new EJBConnector("default","JNDITsInfoReg",this);
						ejbConn2.trx("CarPointinforeg", new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class},
					  	             new Object[]{"B","","",sPutColGp,"","","C"});
						
						//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
				        EJBConnector ejbConn3 = new EJBConnector("default","JNDITsInfoReg",this);
						ejbConn3.trx("CarPointinforeg", new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class},
					  	             new Object[]{"2","","",sPutColGp,"","","C"});

					} else {
						
						String sQueryId  = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerInfoWithPkCHK";	   	
					   	JDTORecord lyrJr = dao2.getCommonInfo(sQueryId,new Object[]{sPutColGp,sPutBedGp,sPutLayerGp,sStockId});
					    
					   	
			    		if(lyrJr != null){			    			
				    		 
							/*
							 * 적치단 UP위치 Clear tb_ym_stacklayer Table : stock_id =
							 * ''(Empty) tb_ym_stacklayer Table : stack_layer_stat =
							 * 'E'(적치가능)
							 */
							iSeq = dao.updateCraneStackLayerStat(sPutColGp,
									sPutBedGp, sPutLayerGp, "",
									YmCommonConst.STACK_LAYER_STAT_E);
	
							/*
							 * B열연 Slab 바로 위 상단 상태정보를 UPDATE
							 */
							iSeq = YmCommonDB.setSlabUpperState_V(sPutColGp, sPutBedGp, sPutLayerGp);
			    		}
					}
				}
				logger.println(LogLevel.DEBUG, this, "=스케쥴 취소=>TO 위치 초기화.");
			}
			/**
			 * 4. 크레인 설비 초기화
			 */
			{
				if (YmCommonConst.SCH_WORK_STAT_1.equals(sSchWorkStat)) {

					iSeq = dao.updateSubCraneEquipStat(StringHelper.evl(schInfo.getFieldString("YD_GP"), "")
							, StringHelper.evl(schInfo.getFieldString("BAY_GP"), "")
							, YmCommonConst.EQUIP_KIND_CR
							, StringHelper.evl(schInfo.getFieldString("SCH_WORK_EQUIP_NO"), "")
							, YmCommonConst.WORK_PROG_STAT_W, "");
					logger.println(LogLevel.DEBUG, this,"=스케쥴 취소=>크레인 설비 초기화.");
				}
			}
			/**
			 * 5. 스케쥴 정보 삭제
			 */
			{
				iSeq = dao.deleteSchInfo(sSchId);
				logger.println(LogLevel.DEBUG, this, "=스케쥴 취소=>스케쥴 정보 삭제.");
			}

			isSuccess = true;
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return isSuccess;
	}
	
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 임가공 업체 야드 관리를 위한 위치 정보 
	 * 
	 * param String : 임가공 업체
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                   
	public String getEmptyLoc (String sYdGp){
		String sLoc = "";
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			JDTORecord locV = dao.getEmptyLoc(sYdGp);
			
			if (locV != null) {
				sLoc = StringHelper.evl(locV.getFieldString("LOCATION"), "");
				
			}
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return sLoc;
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * COIL 산적위치 수정 메소드
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                    
	public boolean changeCoilLocationInfo(String sStockId, 
									String sUpLoc,
									String sPutLoc) {
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		boolean isSuccess = false;
		
		isSuccess= changeCoilLocationInfo(sStockId, 
				 sUpLoc,
				 sPutLoc,
				 "SYSTEM");	
		
		try {
			 ymCommonDAO dao = ymCommonDAO.getInstance();
			
			 String trnEqpQueryId = "ym.tsinfo.getListStlfrtoMovechk";
			 List stocklist = dao.getCommonList(trnEqpQueryId, new Object[]{sStockId});   
			 if(stocklist.size()<=0){
				 logger.println(LogLevel.DEBUG,this, "이미 상차실적 처리가 완료 된 경우 "+sStockId);
			 } else {			
			    JDTORecord tcRecord=null;
			    tcRecord = JDTORecordFactory.getInstance().create(); 
			    tcRecord.setField("sStockList", sStockId);
			     
			    EJBConnector ejbConn 	= new EJBConnector("default","JNDITsInfoReg",this);
			    Boolean isSucf = (Boolean)ejbConn.trx("CarinfoFrtoMoveBackup",new Class[]{JDTORecord.class},
													 new Object[]{tcRecord});
			 		}
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		
		return isSuccess;
	}
	
	/**
	 * 오퍼레이션명 : 
	 * 임가공 PIDEV
	 * COIL 산적위치 수정 메소드
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                    
	public boolean changeCoilLocationInfoPI(String sStockId, 
									String sUpLoc,
									String sPutLoc) {
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		boolean isSuccess = false;
		
		isSuccess= changeCoilLocationInfoPI(sStockId, 
				 sUpLoc,
				 sPutLoc,
				 "SYSTEM");	
		
		try {
			 ymCommonDAO dao = ymCommonDAO.getInstance();
			
			 String trnEqpQueryId = "ym.tsinfo.getListStlfrtoMovechk";
			 List stocklist = dao.getCommonList(trnEqpQueryId, new Object[]{sStockId});   
			 if(stocklist.size()<=0){
				 logger.println(LogLevel.DEBUG,this, "이미 상차실적 처리가 완료 된 경우 "+sStockId);
			 } else {			
			    JDTORecord tcRecord=null;
			    tcRecord = JDTORecordFactory.getInstance().create(); 
			    tcRecord.setField("sStockList", sStockId);
			     
			    EJBConnector ejbConn 	= new EJBConnector("default","JNDITsInfoReg",this);
			    Boolean isSucf = (Boolean)ejbConn.trx("CarinfoFrtoMoveBackup",new Class[]{JDTORecord.class},
													 new Object[]{tcRecord});
			 		}
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		
		return isSuccess;
	}
		
	/**
	 * 오퍼레이션명 : 
	 *
	 * COIL 산적위치 수정 메소드(트랜젝션 분리 작업)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                    
	public boolean changeCoilLocationInfoTX(String sStockId, 
									String sUpLoc,
									String sPutLoc,
									String smodifier,
									String schecked) {
		boolean isSuccess = false;
		 JDTORecord dmRc = null;

		try {
			
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			//산적위치 수정 CALL
			EJBConnector ejbConn 	= new EJBConnector("default","JNDICraneSchReg",this);
		    Boolean isSucf = (Boolean)ejbConn.trx("changeCoilLocationInfo",new Class[]{String.class, 
																		   String.class, 
																		   String.class,
																		   String.class},
																	   new Object[]{sStockId, 
																    		sUpLoc, 
																    		sPutLoc,
																    		smodifier});
		    
		     
		    isSuccess = isSucf.booleanValue();		    

		    String sPutLoc_Chk =sPutLoc.substring(0, 1);
		    logger.println(LogLevel.DEBUG,this, "★★★★산적위치 수정 시 공장변경 후★★★★= "+sPutLoc);
		    
		    //실적처리여부 체크 
//			 ymCommonDAO dao = ymCommonDAO.getInstance();			
//			 String trnEqpQueryId = "ym.tsinfo.getListStlfrtoMovechk2";
//			 List stocklist = dao.getCommonList(trnEqpQueryId, new Object[]{sStockId,sPutLoc_Chk});  
//			 
//			 if(stocklist.size()<=0){
//				 logger.println(LogLevel.DEBUG,this, "★★★★이미 상차실적 처리가 완료 된 경우★★★★ "+sStockId);
//			 } else {
		    
		    if(schecked.equals("Y")){
		    	logger.println(LogLevel.DEBUG,this, "★★★★이송백업 실적 처리 체크한 경우★★★★ "+sStockId);
			    JDTORecord tcRecord=null;
			    tcRecord = JDTORecordFactory.getInstance().create(); 
			    tcRecord.setField("sStockList", sStockId);
			     
			    //실적BACKUP처리 CALL
			    EJBConnector ejbConn2 	= new EJBConnector("default","JNDITsInfoReg",this);
			    Boolean isSucf2 = (Boolean)ejbConn2.trx("CarinfoFrtoMoveBackup",new Class[]{JDTORecord.class},
													 new Object[]{tcRecord});
			    
			    isSuccess = isSucf2.booleanValue();
			  }
		    
		    
	       
			
			if(!"".equals(sStockId)){
				dmRc = dao.getYMDM001Info(sStockId);
		    }
	    	if(dmRc != null){
	    		logger.println(LogLevel.DEBUG,this, "★★★★출하 입고TC 전송 (산적위치수정)★★★★ "+sStockId);
				String sPut_Position= StringHelper.evl(dmRc.getFieldString("PUT_POSITION"), "");
				String sCURR_PROG_CD= StringHelper.evl(dmRc.getFieldString("CURR_PROG_CD"), "");
			
				String sYardGp = sPut_Position.substring(0, 1);
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                //코일입고작업실적
				JDTORecord tcRecordDM = null;
				tcRecordDM = JDTORecordFactory.getInstance().create(); 
				tcRecordDM.setField("GOODS_NO",sStockId);
				tcRecordDM.setField("YD_GP",sYardGp);
				tcRecordDM.setField("STORE_LOC",sPut_Position);
				tcRecordDM.setField("CURR_PROG_CD",sCURR_PROG_CD);
				
				
				//인터페이스 전문 호출
				EJBConnector ejbConn3 = new EJBConnector("default","JNDIYardWrkResReg",this);
				Boolean isSuccess2 = (Boolean)ejbConn3.trx("getYDDMR001",new Class[]{JDTORecord.class}, new Object[]{tcRecordDM}); 
		        logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 코일입고작업실적.===");
		        
		        isSuccess = isSuccess2.booleanValue();
		       //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                						
			}

		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		
		return isSuccess;
	}

	
	/**
	 * 오퍼레이션명 : 
	 * 임가공 PIDEV
	 * COIL 산적위치 수정 메소드(트랜젝션 분리 작업)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                    
	public boolean changeCoilLocationInfoTXPI(String sStockId, 
									String sUpLoc,
									String sPutLoc,
									String smodifier,
									String schecked) {
		boolean isSuccess = false;
		 JDTORecord dmRc = null;
	
		try {
			
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			//산적위치 수정 CALL
			EJBConnector ejbConn 	= new EJBConnector("default","JNDICraneSchReg",this);
		    Boolean isSucf = (Boolean)ejbConn.trx("changeCoilLocationInfoPI",new Class[]{String.class, 
																		   String.class, 
																		   String.class,
																		   String.class},
																	   new Object[]{sStockId, 
																    		sUpLoc, 
																    		sPutLoc,
																    		smodifier});
		    
		     
		    isSuccess = isSucf.booleanValue();		    
	
		    String sPutLoc_Chk =sPutLoc.substring(0, 1);
		    logger.println(LogLevel.DEBUG,this, "★★★★산적위치 수정 시 공장변경 후★★★★= "+sPutLoc);
		    
		    //실적처리여부 체크 
	//		 ymCommonDAO dao = ymCommonDAO.getInstance();			
	//		 String trnEqpQueryId = "ym.tsinfo.getListStlfrtoMovechk2";
	//		 List stocklist = dao.getCommonList(trnEqpQueryId, new Object[]{sStockId,sPutLoc_Chk});  
	//		 
	//		 if(stocklist.size()<=0){
	//			 logger.println(LogLevel.DEBUG,this, "★★★★이미 상차실적 처리가 완료 된 경우★★★★ "+sStockId);
	//		 } else {
		    
		    if(schecked.equals("Y")){
		    	logger.println(LogLevel.DEBUG,this, "★★★★이송백업 실적 처리 체크한 경우★★★★ "+sStockId);
			    JDTORecord tcRecord=null;
			    tcRecord = JDTORecordFactory.getInstance().create(); 
			    tcRecord.setField("sStockList", sStockId);
			     
			    //실적BACKUP처리 CALL
			    EJBConnector ejbConn2 	= new EJBConnector("default","JNDITsInfoReg",this);
			    Boolean isSucf2 = (Boolean)ejbConn2.trx("CarinfoFrtoMoveBackup",new Class[]{JDTORecord.class},
													 new Object[]{tcRecord});
			    
			    isSuccess = isSucf2.booleanValue();
			  }
		    
		    
	       
			
			if(!"".equals(sStockId)){
				dmRc = dao.getYMDM001Info(sStockId);
		    }
	    	if(dmRc != null){
	    		logger.println(LogLevel.DEBUG,this, "★★★★출하 입고TC 전송 (산적위치수정)★★★★ "+sStockId);
				String sPut_Position= StringHelper.evl(dmRc.getFieldString("PUT_POSITION"), "");
				String sCURR_PROG_CD= StringHelper.evl(dmRc.getFieldString("CURR_PROG_CD"), "");
			
				String sYardGp = sPut_Position.substring(0, 1);
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	            //코일입고작업실적
				JDTORecord tcRecordDM = null;
				tcRecordDM = JDTORecordFactory.getInstance().create(); 
				tcRecordDM.setField("GOODS_NO",sStockId);
				tcRecordDM.setField("YD_GP",sYardGp);
				tcRecordDM.setField("STORE_LOC",sPut_Position);
				tcRecordDM.setField("CURR_PROG_CD",sCURR_PROG_CD);
				
				
				//인터페이스 전문 호출
				EJBConnector ejbConn3 = new EJBConnector("default","JNDIYardWrkResReg",this);
				Boolean isSuccess2 = (Boolean)ejbConn3.trx("getM10YDLMJ1011",new Class[]{JDTORecord.class}, new Object[]{tcRecordDM}); 
		        logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 코일입고작업실적.===");
		        
		        isSuccess = isSuccess2.booleanValue();
		       //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	            						
			}
	
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		
		return isSuccess;
	}
	
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * COIL 산적위치 수정 메소드
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 * @ejb.transaction type="RequiresNew"
	 */                    									 
	public boolean changeCoilLocationInfo(String sStockId, 
									String sUpLoc,
									String sPutLoc,
									String sUserId) {
		boolean isSuccess = false;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			int iReq = 0;

			String sTmpStockId = "";
			String sTmpStat = "";

			String sWbookId = "";
			String sDelYn = "";
			String sSchId = "";
			String sCurSchCode = "";
			String sCurBayGp = "";

			String sPutStackColGp = "";
			String sPutStackBedGp = "";
			String sPutStackLayerGp = "";

			String sLayerStat = YmCommonConst.STACK_LAYER_STAT_L;

			String[] sStockInfo = YmCommonUtil.getCoilCurrProgCd(sStockId,"");
			String sProgCd = sStockInfo[0];
			String sStocMv = sStockInfo[1];

			String sUpYardGp = "";
			String sPutYardGp = "";

			if (sUpLoc.length() > 1) {
				sUpYardGp = sUpLoc.substring(0, 1);
			}

			if (sPutLoc.length() > 1) {
				sPutYardGp = sPutLoc.substring(0, 1);
			}

//			if (!"".equals(sUpYardGp) && !"".equals(sPutYardGp)
//					&& !sUpYardGp.equals(sPutYardGp)) {
//				if (!sProgCd.equals(YmCommonConst.CURR_PROG_CD_COIL_E)) {
//					throw new EJBServiceException("산적위치 수정=> 공장간 산적위치 수정은 할 수 없습니다.");
//				}
//			}

			JDTORecord stockV = dao.getStockInfo(sStockId);

			/**
			 * 0. 입력한 To 위치 정합성 점검
			 */
			if (sPutLoc.length() == 10) {
				sPutStackColGp = sPutLoc.substring(0, 6);
				sPutStackBedGp = sPutLoc.substring(6, 8);
				sPutStackLayerGp = sPutLoc.substring(8, 10);
			}

			JDTORecord vColGp = dao.getStackColInfoWithPk(sPutStackColGp);

			if (vColGp == null) {
				throw new EJBServiceException("산적위치 수정=> To위치정보가 잘못 입력되었습니다.");
			}
			
			if ("6".equals(sPutYardGp) || "8".equals(sPutYardGp)|| "Z".equals(sPutYardGp) || "W".equals(sPutYardGp)) {
				
			}else {
				if (YmCommonConst.STACK_LAYER_GP_01.equals(sPutStackLayerGp)
					|| YmCommonConst.STACK_LAYER_GP_02.equals(sPutStackLayerGp)
					|| YmCommonConst.STACK_LAYER_GP_03.equals(sPutStackLayerGp)) {
				} else {
					throw new EJBServiceException(
						"산적위치 수정=> 적치단(01단/02단) 정보가 잘못 입력되었습니다.");
				}
			}
			
			/**
			 * 1. 작업예약 유무 체크
			 */
			if (stockV != null) {
				sWbookId = StringHelper.evl(stockV.getFieldString("WBOOK_ID"), "");
				sDelYn = StringHelper.evl(stockV.getFieldString("DEL_YN"), "");
				
			} else {
				//throw new EJBServiceException("산적위치 수정=> 저장품정보고 존재 안합니다.");				      
			    
			    JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			    jrParam.setField("STOCK_ID"	,sStockId); 
			    commDao.insert(jrParam, "ym.tsinfo.insertstock",sUserId, "changeCoilLocationInfo", "STOCK등록");
			    
			}
			logger.println(LogLevel.DEBUG, this, "산적위치 수정=> 작업예약ID=" + sWbookId);
			logger.println(LogLevel.DEBUG, this, "산적위치 수정=> 삭제유무	=" + sDelYn);

			if (YmCommonConst.DELETE_STOCK.equals(sDelYn)) {
				throw new EJBServiceException("산적위치 수정=> 출하완료 저장품정보입니다.");
			}

			if (!"".equals(sWbookId)) {

				/**
				 * 1.1 스케쥴정보 있으면 삭제
				 */
				JDTORecord schV = dao.getSchInfoWithWbookId(sWbookId, sStockId);

				if (schV != null) {
					/**
					 * 1.1.1 스케쥴 취소 모듈 CALL
					 */
					sSchId 		= StringHelper.evl(schV.getFieldString("SCH_ID"), "");
					sCurSchCode = StringHelper.evl(schV.getFieldString("SCH_WORK_KIND"), "");

					logger.println(LogLevel.DEBUG, this, "산적위치 수정=> 스케쥴ID=" + sSchId);

					isSuccess = cancelCoilSchInfo(sSchId);
				}

				JDTORecord wbookV = dao.getWbookInfo(sWbookId);
				/**
				 * 1.2 작업예약 스케쥴 코드 체크
				 */
				if (wbookV != null) {
					sCurSchCode = StringHelper.evl(wbookV.getFieldString("SCH_WORK_KIND"), "");
					sCurBayGp 	= StringHelper.evl(wbookV.getFieldString("BAY_GP"), "");
				}
				logger.println(LogLevel.DEBUG, this, "산적위치 수정=> 스케쥴코드=" + sCurSchCode);

				if (YmCommonConst.NEW_SCH_WORK_KIND_GVFL.equals(sCurSchCode)
						|| // COIL 제품출하상차
						YmCommonConst.NEW_SCH_WORK_KIND_GVF1.equals(sCurSchCode)
						|| // COIL 제품출하상차
						YmCommonConst.NEW_SCH_WORK_KIND_GVF2.equals(sCurSchCode)
						|| // COIL 제품출하상차
						YmCommonConst.NEW_SCH_WORK_KIND_GTFL.equals(sCurSchCode)|| // COIL 제품출하상차
			    	   YmCommonConst.NEW_SCH_WORK_KIND_GTF1.equals(sCurSchCode)|| // Coil 제품출하상차
			    	   YmCommonConst.NEW_SCH_WORK_KIND_GTF2.equals(sCurSchCode)||    		    
			    	   YmCommonConst.NEW_SCH_WORK_KIND_GPFL.equals(sCurSchCode)|| // COIL 제품출하상차
			    	   YmCommonConst.NEW_SCH_WORK_KIND_GPF1.equals(sCurSchCode)|| // Coil 제품출하상차
			    	   YmCommonConst.NEW_SCH_WORK_KIND_GPF2.equals(sCurSchCode)||
						YmCommonConst.NEW_SCH_WORK_KIND_GVML.equals(sCurSchCode)
						|| // COIL 제품이송상차
						YmCommonConst.NEW_SCH_WORK_KIND_GVM2.equals(sCurSchCode)
						|| // COIL 제품이송상차
						YmCommonConst.NEW_SCH_WORK_KIND_GVM3.equals(sCurSchCode)
						|| // COIL 제품이송상차
						YmCommonConst.NEW_SCH_WORK_KIND_CVML.equals(sCurSchCode)
						|| // COIL 소재이송상차
						YmCommonConst.NEW_SCH_WORK_KIND_CVM2.equals(sCurSchCode)
						|| // COIL 소재이송상차
						YmCommonConst.NEW_SCH_WORK_KIND_CVM3.equals(sCurSchCode)) { // COIL 소재이송상차

					// 즉 상차지시 편성된 시점의 작업예약은 삭제하지 않는다.
					sLayerStat = YmCommonConst.STACK_LAYER_STAT_S;

					if (sCurBayGp.equals(sPutStackColGp.substring(1, 2))) {
						// 같은 동에 산적위치 수정을 한 경우
					} else {
						// 다른 동으로 산적위치 수정을 한 경우.
						// 이 경우에 작업예약 동구분 항목도 수정을 해준다.
						iReq = dao.updateBayGpWithWbookId(sWbookId, sPutStackColGp.substring(1, 2));
					}

					logger.println(LogLevel.DEBUG, this, "산적위치 수정=> 작업예약 삭제안함.");
				} else {
					/*
					 * 1.2.1 저장품 Table의 Wbook_id 항목을 Update tb_ym_stock Table
					 * wbook_id : ''(empty)
					 */

					iReq = dao.updateStockWbookId(sStockId, "");

					/**
					 * 1.2.2 작업예약정보 있으면 삭제
					 */
					int iSeq = dao.deleteWbookInfo(sWbookId);
					logger.println(LogLevel.DEBUG, this, "산적위치 수정=> 작업예약 삭제=" + iSeq);
				}
			}

			String sUpStackColGp = "";
			String sUpStackBedGp = "";
			String sUpStackLayerGp = "";
			String sUpUsageCd = "";

			/**
			 * 2. 저장품의 MAP정보를 가져온다. 중복위치도 체크한다.
			 */
			List stockL = dao.getStackLayerInfoWithStockId_03(sStockId);

			JDTORecord stackV = null;
			JDTORecord upRc = null;

			if (stockL != null) {
				for (int inx = 0; inx < stockL.size(); inx++) {
					stackV = (JDTORecord) stockL.get(inx);

					sUpStackColGp 	= StringHelper.evl(stackV.getFieldString("STACK_COL_GP"), "");
					sUpStackBedGp 	= StringHelper.evl(stackV.getFieldString("STACK_BED_GP"), "");
					sUpStackLayerGp = StringHelper.evl(stackV.getFieldString("STACK_LAYER_GP"), "");

					sUpUsageCd = YmCommonUtil.getStackColInfoWithPk(sUpStackColGp);

					if (YmCommonConst.STACK_COL_GP_1BDC01.equals(sUpStackColGp)
							|| // A열연 COIL 분기콘베이어
							YmCommonConst.STACK_COL_GP_1CDC01.equals(sUpStackColGp)
							|| // A열연 COIL 분기콘베이어
							YmCommonConst.STACK_COL_USAGE_CD_XX.equals(sUpUsageCd)
							|| // COIL 비상적치위치
							YmCommonConst.STACK_COL_USAGE_CD_FE.equals(sUpUsageCd)
							|| // COIL HFL보급위치
							YmCommonConst.STACK_COL_USAGE_CD_FI.equals(sUpUsageCd)
							|| // COIL HFLTAKEIN위치
							YmCommonConst.STACK_COL_USAGE_CD_FD.equals(sUpUsageCd)
							|| // COIL HFL추출위치
							YmCommonConst.STACK_COL_USAGE_CD_KE.equals(sUpUsageCd)
							|| // COIL SPM보급위치
							YmCommonConst.STACK_COL_USAGE_CD_KI.equals(sUpUsageCd)
							|| // COIL SPMTAKEIN위치
							YmCommonConst.STACK_COL_USAGE_CD_KD.equals(sUpUsageCd)) {// COIL SPM추출위치

						int iSeq = YmCommonDB.deleteConveyorInfo(sUpStackColGp,sStockId);
						if (iSeq < 0) {
							// throw new EJBServiceException("=권상실적=>CONVEYOR
							// DELETE FAIL.");
							logger.println(LogLevel.DEBUG, this, "산적위치 수정=> 적치단 삭제 FAIL");
						}

					} else {

						if (YmCommonConst.STACK_COL_USAGE_CD_CX.equals(sUpUsageCd)) {// COIL 대차정지위치

							/**
							 * 대차위치 CLEAR
							 */
							String sCurrQty = "0";

							sCurrQty = "-1";

							iReq = dao.updateStackerQtyInfo(sUpStackColGp, sUpStackBedGp, sCurrQty);
						}

						iReq = dao.updateStockMoveEquipInfo(sStockId, "", "", "", "", "");
						/*
						 * 적치단 UP위치 Clear tb_ym_stacklayer Table : stock_id =
						 * ''(Empty) tb_ym_stacklayer Table : stack_layer_stat =
						 * 'E'(적치가능)
						 */
						iReq = dao.updateCraneStackLayerStat(sUpStackColGp,
								sUpStackBedGp, sUpStackLayerGp, "",
								YmCommonConst.STACK_LAYER_STAT_E);

						/**
						 * FROM 위치 상단 적치상태 수정
						 */
						if (YmCommonConst.STACK_LAYER_GP_01.equals(sUpStackLayerGp)) {

							/*
							 * A.B열연 Coil 권상실적 상단 왼쪽 상태정보를 UPDATE 상단 오른쪽 상태정보를
							 * UPDATE
							 */
							iReq = YmCommonDB.setCoilUpperState_V(
									sUpStackColGp, sUpStackBedGp,
									sUpStackLayerGp);
						}
					}

					logger.println(LogLevel.DEBUG, this, "산적위치 수정=> FROM 위치 수정 = " + iReq);

				}
			}

			/**
			 * 3. TO 위치 수정
			 */
			String sPutUsageCd = YmCommonUtil.getStackColInfoWithPk(sPutStackColGp);

			if (YmCommonConst.STACK_COL_GP_1BDC01.equals(sPutStackColGp) || // A열연
																			// COIL
																			// 분기콘베이어
					YmCommonConst.STACK_COL_GP_1CDC01.equals(sPutStackColGp) || // A열연
																				// COIL
																				// 분기콘베이어
					YmCommonConst.STACK_COL_USAGE_CD_XX.equals(sPutUsageCd) || // COIL
																				// 비상적치위치
					YmCommonConst.STACK_COL_USAGE_CD_FE.equals(sPutUsageCd) || // COIL
																				// HFL보급위치
					YmCommonConst.STACK_COL_USAGE_CD_FI.equals(sPutUsageCd) || // COIL
																				// HFLTAKEIN위치
					YmCommonConst.STACK_COL_USAGE_CD_FD.equals(sPutUsageCd) || // COIL
																				// HFL추출위치
					YmCommonConst.STACK_COL_USAGE_CD_KE.equals(sPutUsageCd) || // COIL
																				// SPM보급위치
					YmCommonConst.STACK_COL_USAGE_CD_KI.equals(sPutUsageCd) || // COIL
																				// SPMTAKEIN위치
					YmCommonConst.STACK_COL_USAGE_CD_KD.equals(sPutUsageCd)) {// COIL
																				// SPM추출위치

				int iSeq = YmCommonDB.insertConveyorInfo(sPutStackColGp,sStockId, sPutStackBedGp);
				if (iSeq < 0) {
					logger.println(LogLevel.DEBUG, this, "산적위치 수정=> 적치단 생성 FAIL");
				}

			} else {

				/**
				 * 3. TO 위치 정보 체크
				 */
				/*
				 * 적치단 Put위치에 다른 코일이 있을 경우. 해당동의 XX번지로 저장품 MAP을 수정한다.
				 */
				iReq = YmCommonDB.updateLegacyStockId_Coil(dao, sPutStackColGp, sPutStackBedGp, sPutStackLayerGp, sStockId);

//				Crane 작업 실적 등록
				String sTempLayer = sPutStackColGp.substring(0,2)+
									YmCommonConst.STACK_COL_USAGE_CD_XX+
									YmCommonConst.STACK_BED_GP_01;
				
				JDTORecord putJr = dao.getStackLayerInfoWithPk(sPutStackColGp,
																sPutStackBedGp,
																sPutStackLayerGp);

				String sToStockId 	= "00";
				String sToStat 		= "";
				
				if(putJr != null){
				
				sToStockId 	= StringHelper.evl(putJr.getFieldString("STOCK_ID"), "");
				sToStat 	= StringHelper.evl(putJr.getFieldString("STACK_LAYER_STAT"), "");
				}
				
				if(!"".equals(sToStockId)&&
						   !sStockId.equals(sToStockId)){ 
				EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
	    	 	ejbConn.trx("insertUpPutWrslRtData",new  Class[]{String.class,String.class,String.class,String.class,String.class,String.class},
															new Object[]{sToStockId,sPutStackColGp,sTempLayer,"CYMM", sPutStackColGp.substring(0,1),sUserId});
				}
				
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	            //코일제품이적작업실적
	 			JDTORecord tcRecordDM = null;
	 			tcRecordDM = JDTORecordFactory.getInstance().create(); 
	 			tcRecordDM.setField("GOODS_NO",sToStockId);
	 			tcRecordDM.setField("BEFO_STORE_LOC",sPutStackColGp+sPutStackBedGp+sPutStackLayerGp);
	 			tcRecordDM.setField("TO_STORE_LOC",sTempLayer+"0101");
	 			
	 			//인터페이스 전문 호출
	 			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
	 			ejbConn.trx("getYDDMR004",new Class[]{JDTORecord.class},
	 			  	  	 new Object[]{tcRecordDM}); 
	            logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 코일제품이적작업실적XX3.===");
	            //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	            
	    	 	
				if (YmCommonConst.STACK_COL_USAGE_CD_CX.equals(sPutUsageCd)) {// COIL
																				// 대차정지위치
					/**
					 * 대차위치 CLEAR
					 */
					String sCurrQty = "0";

					sCurrQty = "1";

					iReq = dao.updateStackerQtyInfo(sPutStackColGp, sPutStackBedGp, sCurrQty);

					iReq = dao.updateStockMoveEquipInfo(sStockId,
							sPutStackColGp.substring(0, 1) + "X" + sPutStackColGp.substring(2),
							sPutStackBedGp, sPutStackLayerGp, "", "");
				}

				/*
				 * 적치단 Put위치를 적치상태로 변경 tb_ym_stacklayer Table : stock_id = Coil
				 * No tb_ym_stacklayer Table : stack_layer_stat = 'L'(적치중)
				 */
				iReq = dao.updateCraneStackLayerStat(sPutStackColGp, sPutStackBedGp, sPutStackLayerGp, sStockId, sLayerStat);
				/*
				 * 적치단이 '01'단일 경우 적치단상태가 'L', 'P' 이면 상단 적치단 정보를 적치가능상태로 변경
				 */
				if (YmCommonConst.STACK_LAYER_GP_01.equals(sPutStackLayerGp)) {

					/*
					 * A.B열연 Coil 권하실적 상단 왼쪽 상태정보를 UPDATE 상단 오른쪽 상태정보를 UPDATE
					 */
					iReq = YmCommonDB.setCoilUpperState_E(sPutStackColGp, sPutStackBedGp, sPutStackLayerGp);
				}

			}
			logger.println(LogLevel.DEBUG, this, "산적위치 수정=> TO 위치 수정 = "+ iReq);

			/**
			 * 4. COIL 저장품 TABLE 수정 저장품TABLE의 이동설비항목에 권하위치값을 삭제한다. tb_ym_stock
			 * Table frtomove_equip_gp : empty('') tb_ym_stock Table
			 * frtomove_equip_bed_gp : empty('') tb_ym_stock Table
			 * frtomove_equip_layer_gp : empty('')
			 */

			iReq = dao.updateStockMoveEquipInfo(sStockId, "", "", "", "", "");

			/**
			 * 4. COIL 공통 TABLE 수정
			 */
			/*
			 * Coil 공통 Table 저장위치 Update
			 */
			iReq = dao.updateCoilCommonLocInfo(sStockId, sPutLoc);

			logger.println(LogLevel.DEBUG, this, "산적위치 수정=> 코일공통 UPDATE = " + iReq);

			/*
			 * 저장품 예상PUT 위치 CLEAR 저장품지정조회화면에서 예약상태로 남아있는것을 방지하기 위해
			 */
			{
				JDTORecord stockRc = dao.getStockInfo(sStockId);
				if (stockRc != null) {
					String sCarunloadBay = StringHelper.evl(stockRc.getFieldString("CARUNLOAD_PUT_LOC"), "");// 하차PUT위치
					if (sPutLoc.equals(sCarunloadBay)) {
						iReq = dao.updateStockPutLocWithStockId(sStockId, "");
					}
					logger.println(LogLevel.DEBUG, this,"저장품 예상PUT 위치 CLEAR A=" + sCarunloadBay);
					logger.println(LogLevel.DEBUG, this,"저장품 예상PUT 위치 CLEAR B=" + sPutLoc);
				}
			}

			/**
			 * 5. 저장품이동조건 수정
			 */
			iReq = dao.updateStockTransInfo(sStockId, sStocMv);
			logger.println(LogLevel.DEBUG, this, "산적위치 수정=> 저장품 이동조건 = "+ sStocMv + "=iReq=" + iReq);

			/**
			 * 6. Crane 작업 실적 등록
			 */
			iReq = insertUpPutWrslRtData(sStockId.trim(), sUpLoc.trim(), sPutLoc.trim(), sCurSchCode, sPutYardGp,sUserId);

			/**
			 * 7. Coil 이적실적 (출하로 이적실적 송신 YMDM008) 공통 진도 Code가 출하작업지시대기 K, 제품충당대기
			 * Z, 출하작업대기 L, 보관매출 M 이면 출하로 "이적실적 송신"
			 */

			if (sProgCd.equals(YmCommonConst.CURR_PROG_CD_COIL_K)
					|| sProgCd.equals(YmCommonConst.CURR_PROG_CD_COIL_P)  // 인도완료 추가 20091006
					|| sProgCd.equals(YmCommonConst.CURR_PROG_CD_COIL_Z)
					|| sProgCd.equals(YmCommonConst.CURR_PROG_CD_COIL_J)
					|| sProgCd.equals(YmCommonConst.CURR_PROG_CD_COIL_L)
					|| sProgCd.equals(YmCommonConst.CURR_PROG_CD_COIL_X)
					|| sProgCd.equals(YmCommonConst.CURR_PROG_CD_COIL_M)) {

				isSuccess = SendYMDM008(sPutYardGp, sStockId.trim(), sUpLoc.trim(), sPutLoc.trim());
			}

			/**
			 * 8. Coil 입고실적 (출하로 제품입고실적 송신 YMDM001) 공통 진도 Code가 입고대기 H 이면 출하로
			 * 입고실적송신
			 */
//			if (sProgCd.equals(YmCommonConst.CURR_PROG_CD_COIL_H)) {
//
//				isSuccess = SendYMDM001(sPutYardGp, sStockId.trim(), sPutLoc.trim());
//			}

			/**
			 * 9. TO위치가 B열연이고, 공통 진도 Code가 이송 작업대기 E 이면 조업으로 이송실적송신
			 * 
			 * if (sPutLoc.startsWith(YmCommonConst.YD_GP_3)
					&& sProgCd.equals(YmCommonConst.CURR_PROG_CD_COIL_E)) {
				if (!sUpYardGp.equals(sPutYardGp)) {
					isSuccess = SendYMPO159(sStockId.trim(), "D", sPutLoc.trim());
				}
			}
			 * 
			 */
			if (!sUpYardGp.equals(sPutYardGp)
					&& sProgCd.equals(YmCommonConst.CURR_PROG_CD_COIL_E)) {
					isSuccess = SendYMPO159(sStockId.trim(), "D", sPutLoc.trim());
			}
			
			/**
			 * 10. TO위치가 SPM,HFL 입측이면 보급실적을 조업으로 송신한다.
			 */
			// 코드 추가해야하지 않을까?. 최규성 2010-02-24 
			// 확인 필요.
			if (YmCommonConst.STACK_COL_USAGE_CD_FE.equals(sPutUsageCd)) {// COIL
																			// HFL보급위치
//				if(sPutLoc.startsWith(YmCommonConst.YD_GP_3) && YmCommonConst.NEW_WORK_HFL_F.equals("X")){	// B 열연 SPM2 내 HFL 일 경우
//					isSuccess = SendYMPO161(sStockId.trim(), sPutYardGp, "F", "1");
//				}else{
					isSuccess = SendYMPO161(sStockId.trim(), sPutYardGp, "H", "1");
//				}
			} else if (YmCommonConst.STACK_COL_USAGE_CD_FI.equals(sPutUsageCd)) {// COIL
																					// HFLTAKEIN위치

				isSuccess = SendYMPO161(sStockId.trim(), sPutYardGp, "H", "5");
			} else if (YmCommonConst.STACK_COL_USAGE_CD_KE.equals(sPutUsageCd)) {// COIL
																					// SPM보급위치
//				if(sPutLoc.startsWith(YmCommonConst.YD_GP_3) && YmCommonConst.NEW_WORK_SPM_N.equals("X")){	// B 열연 SPM2 내 HFL 일 경우
//					isSuccess = SendYMPO161(sStockId.trim(), sPutYardGp, "N", "1");
//				}else{
					isSuccess = SendYMPO161(sStockId.trim(), sPutYardGp, "S", "1");
//				}
			} else if (YmCommonConst.STACK_COL_USAGE_CD_KI.equals(sPutUsageCd)) {// COIL
																					// SPMTAKEIN위치
//				if(sPutLoc.startsWith(YmCommonConst.YD_GP_3) && YmCommonConst.NEW_WORK_SPM_N.equals("X")){	// B 열연 SPM2 내 HFL 일 경우
//					isSuccess = SendYMPO161(sStockId.trim(), sPutYardGp, "N", "5");
//				}else{
					isSuccess = SendYMPO161(sStockId.trim(), sPutYardGp, "S", "5");
//				}
			}

			/**
			 * 11. BACK UP 실적 전문 송신 A열연인 경우..
			 */

			if (sPutLoc.startsWith(YmCommonConst.YD_GP_1)) {

				EJBConnector ejbConn = new EJBConnector("default", "JNDICWrkOrdReg", this);
				Boolean isSuccesy = (Boolean) ejbConn.trx("callACoilCraneBackUpMsgInfo", 
												new Class[] {String.class, String.class, String.class },
												new Object[] { sStockId, sUpLoc, sPutLoc });
			}

			/**
			 * 12. YARD MAP 정보 실적 등록
			 */
			if (sPutLoc.startsWith(YmCommonConst.YD_GP_3)) {
				String sMsg = YmCommonUtil.setBCoilMapMsgInfo(sPutLoc);

				EJBConnector ejbConn = new EJBConnector("default", "JNDICraneStatusReg", this);
				Boolean isSucf = (Boolean) ejbConn.trx("bcyYdMapInfo", new Class[] { String.class }, new Object[] { sMsg });
			}
			
			
			//////////////////////////////////////////////////////////////////////////////////////////////
			//***********************이송대상재 BACKUP으로 산적위치 변경 시 실적처리 작업***************************
			//////////////////////////////////////////////////////////////////////////////////////////////
//			 ymCommonDAO dao = ymCommonDAO.getInstance();
//
//			 String trnEqpQueryId = "ym.tsinfo.getListStlfrtoMovechk";
//			 List stocklist = dao.getCommonList(trnEqpQueryId, new Object[]{sStockId});   
//			 if(stocklist.size()<=0){
//				 logger.println(LogLevel.DEBUG,this, "이미 상차실적 처리가 완료 된 경우 "+sStockId);
//			 } else {			
//			    JDTORecord tcRecord=null;
//			    tcRecord = JDTORecordFactory.getInstance().create(); 
//			    tcRecord.setField("sStockList", sStockId);
//			     
//			    EJBConnector ejbConn 	= new EJBConnector("default","JNDITsInfoReg",this);
//			    Boolean isSucf = (Boolean)ejbConn.trx("CarinfoFrtoMoveBackup",new Class[]{JDTORecord.class},
//													 new Object[]{tcRecord});
//			 }
			 /////////////////////////////////////////////////////////////////////////////////////////////
			 
			isSuccess = true;
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return isSuccess;
	}

	/**
	 * 오퍼레이션명 : 
	 * 임가공 PI
	 * COIL 산적위치 수정 메소드
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 * @ejb.transaction type="RequiresNew"
	 */                    									 
	public boolean changeCoilLocationInfoPI(String sStockId, 
									String sUpLoc,
									String sPutLoc,
									String sUserId) {
		boolean isSuccess = false;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			int iReq = 0;

			String sTmpStockId = "";
			String sTmpStat = "";

			String sWbookId = "";
			String sDelYn = "";
			String sSchId = "";
			String sCurSchCode = "";
			String sCurBayGp = "";

			String sPutStackColGp = "";
			String sPutStackBedGp = "";
			String sPutStackLayerGp = "";

			String sLayerStat = YmCommonConst.STACK_LAYER_STAT_L;

			String[] sStockInfo = YmCommonUtil.getCoilCurrProgCd(sStockId,"");
			String sProgCd = sStockInfo[0];
			String sStocMv = sStockInfo[1];

			String sUpYardGp = "";
			String sPutYardGp = "";

			if (sUpLoc.length() > 1) {
				sUpYardGp = sUpLoc.substring(0, 1);
			}

			if (sPutLoc.length() > 1) {
				sPutYardGp = sPutLoc.substring(0, 1);
			}

//			if (!"".equals(sUpYardGp) && !"".equals(sPutYardGp)
//					&& !sUpYardGp.equals(sPutYardGp)) {
//				if (!sProgCd.equals(YmCommonConst.CURR_PROG_CD_COIL_E)) {
//					throw new EJBServiceException("산적위치 수정=> 공장간 산적위치 수정은 할 수 없습니다.");
//				}
//			}

			JDTORecord stockV = dao.getStockInfoPI(sStockId);

			/**
			 * 0. 입력한 To 위치 정합성 점검
			 */
			if (sPutLoc.length() == 10) {
				sPutStackColGp = sPutLoc.substring(0, 6);
				sPutStackBedGp = sPutLoc.substring(6, 8);
				sPutStackLayerGp = sPutLoc.substring(8, 10);
			}

			JDTORecord vColGp = dao.getStackColInfoWithPk(sPutStackColGp);

			if (vColGp == null) {
				throw new EJBServiceException("산적위치 수정=> To위치정보가 잘못 입력되었습니다.");
			}
			
			if ("6".equals(sPutYardGp) || "8".equals(sPutYardGp)|| "Z".equals(sPutYardGp) || "W".equals(sPutYardGp)) {
				
			}else {
				if (YmCommonConst.STACK_LAYER_GP_01.equals(sPutStackLayerGp)
					|| YmCommonConst.STACK_LAYER_GP_02.equals(sPutStackLayerGp)
					|| YmCommonConst.STACK_LAYER_GP_03.equals(sPutStackLayerGp)) {
				} else {
					throw new EJBServiceException(
						"산적위치 수정=> 적치단(01단/02단) 정보가 잘못 입력되었습니다.");
				}
			}
			
			/**
			 * 1. 작업예약 유무 체크
			 */
			if (stockV != null) {
				sWbookId = StringHelper.evl(stockV.getFieldString("WBOOK_ID"), "");
				sDelYn = StringHelper.evl(stockV.getFieldString("DEL_YN"), "");
				
			} else {
				//throw new EJBServiceException("산적위치 수정=> 저장품정보고 존재 안합니다.");				      
			    
			    JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			    jrParam.setField("STOCK_ID"	,sStockId); 
			    commDao.insert(jrParam, "ym.tsinfo.insertstock",sUserId, "changeCoilLocationInfo", "STOCK등록");
			    
			}
			logger.println(LogLevel.DEBUG, this, "산적위치 수정=> 작업예약ID=" + sWbookId);
			logger.println(LogLevel.DEBUG, this, "산적위치 수정=> 삭제유무	=" + sDelYn);

			if (YmCommonConst.DELETE_STOCK.equals(sDelYn)) {
				throw new EJBServiceException("산적위치 수정=> 출하완료 저장품정보입니다.");
			}

			if (!"".equals(sWbookId)) {

				/**
				 * 1.1 스케쥴정보 있으면 삭제
				 */
				JDTORecord schV = dao.getSchInfoWithWbookId(sWbookId, sStockId);

				if (schV != null) {
					/**
					 * 1.1.1 스케쥴 취소 모듈 CALL
					 */
					sSchId 		= StringHelper.evl(schV.getFieldString("SCH_ID"), "");
					sCurSchCode = StringHelper.evl(schV.getFieldString("SCH_WORK_KIND"), "");

					logger.println(LogLevel.DEBUG, this, "산적위치 수정=> 스케쥴ID=" + sSchId);

					isSuccess = cancelCoilSchInfoPI(sSchId);
				}

				JDTORecord wbookV = dao.getWbookInfo(sWbookId);
				/**
				 * 1.2 작업예약 스케쥴 코드 체크
				 */
				if (wbookV != null) {
					sCurSchCode = StringHelper.evl(wbookV.getFieldString("SCH_WORK_KIND"), "");
					sCurBayGp 	= StringHelper.evl(wbookV.getFieldString("BAY_GP"), "");
				}
				logger.println(LogLevel.DEBUG, this, "산적위치 수정=> 스케쥴코드=" + sCurSchCode);

				if (YmCommonConst.NEW_SCH_WORK_KIND_GVFL.equals(sCurSchCode)
						|| // COIL 제품출하상차
						YmCommonConst.NEW_SCH_WORK_KIND_GVF1.equals(sCurSchCode)
						|| // COIL 제품출하상차
						YmCommonConst.NEW_SCH_WORK_KIND_GVF2.equals(sCurSchCode)
						|| // COIL 제품출하상차
						YmCommonConst.NEW_SCH_WORK_KIND_GTFL.equals(sCurSchCode)|| // COIL 제품출하상차
			    	   YmCommonConst.NEW_SCH_WORK_KIND_GTF1.equals(sCurSchCode)|| // Coil 제품출하상차
			    	   YmCommonConst.NEW_SCH_WORK_KIND_GTF2.equals(sCurSchCode)||    		    
			    	   YmCommonConst.NEW_SCH_WORK_KIND_GPFL.equals(sCurSchCode)|| // COIL 제품출하상차
			    	   YmCommonConst.NEW_SCH_WORK_KIND_GPF1.equals(sCurSchCode)|| // Coil 제품출하상차
			    	   YmCommonConst.NEW_SCH_WORK_KIND_GPF2.equals(sCurSchCode)||
						YmCommonConst.NEW_SCH_WORK_KIND_GVML.equals(sCurSchCode)
						|| // COIL 제품이송상차
						YmCommonConst.NEW_SCH_WORK_KIND_GVM2.equals(sCurSchCode)
						|| // COIL 제품이송상차
						YmCommonConst.NEW_SCH_WORK_KIND_GVM3.equals(sCurSchCode)
						|| // COIL 제품이송상차
						YmCommonConst.NEW_SCH_WORK_KIND_CVML.equals(sCurSchCode)
						|| // COIL 소재이송상차
						YmCommonConst.NEW_SCH_WORK_KIND_CVM2.equals(sCurSchCode)
						|| // COIL 소재이송상차
						YmCommonConst.NEW_SCH_WORK_KIND_CVM3.equals(sCurSchCode)) { // COIL 소재이송상차

					// 즉 상차지시 편성된 시점의 작업예약은 삭제하지 않는다.
					sLayerStat = YmCommonConst.STACK_LAYER_STAT_S;

					if (sCurBayGp.equals(sPutStackColGp.substring(1, 2))) {
						// 같은 동에 산적위치 수정을 한 경우
					} else {
						// 다른 동으로 산적위치 수정을 한 경우.
						// 이 경우에 작업예약 동구분 항목도 수정을 해준다.
						iReq = dao.updateBayGpWithWbookId(sWbookId, sPutStackColGp.substring(1, 2));
					}

					logger.println(LogLevel.DEBUG, this, "산적위치 수정=> 작업예약 삭제안함.");
				} else {
					/*
					 * 1.2.1 저장품 Table의 Wbook_id 항목을 Update tb_ym_stock Table
					 * wbook_id : ''(empty)
					 */

					iReq = dao.updateStockWbookId(sStockId, "");

					/**
					 * 1.2.2 작업예약정보 있으면 삭제
					 */
					int iSeq = dao.deleteWbookInfo(sWbookId);
					logger.println(LogLevel.DEBUG, this, "산적위치 수정=> 작업예약 삭제=" + iSeq);
				}
			}

			String sUpStackColGp = "";
			String sUpStackBedGp = "";
			String sUpStackLayerGp = "";
			String sUpUsageCd = "";

			/**
			 * 2. 저장품의 MAP정보를 가져온다. 중복위치도 체크한다.
			 */
			List stockL = dao.getStackLayerInfoWithStockId_03(sStockId);

			JDTORecord stackV = null;
			JDTORecord upRc = null;

			if (stockL != null) {
				for (int inx = 0; inx < stockL.size(); inx++) {
					stackV = (JDTORecord) stockL.get(inx);

					sUpStackColGp 	= StringHelper.evl(stackV.getFieldString("STACK_COL_GP"), "");
					sUpStackBedGp 	= StringHelper.evl(stackV.getFieldString("STACK_BED_GP"), "");
					sUpStackLayerGp = StringHelper.evl(stackV.getFieldString("STACK_LAYER_GP"), "");

					sUpUsageCd = YmCommonUtil.getStackColInfoWithPk(sUpStackColGp);

					if (YmCommonConst.STACK_COL_GP_1BDC01.equals(sUpStackColGp)
							|| // A열연 COIL 분기콘베이어
							YmCommonConst.STACK_COL_GP_1CDC01.equals(sUpStackColGp)
							|| // A열연 COIL 분기콘베이어
							YmCommonConst.STACK_COL_USAGE_CD_XX.equals(sUpUsageCd)
							|| // COIL 비상적치위치
							YmCommonConst.STACK_COL_USAGE_CD_FE.equals(sUpUsageCd)
							|| // COIL HFL보급위치
							YmCommonConst.STACK_COL_USAGE_CD_FI.equals(sUpUsageCd)
							|| // COIL HFLTAKEIN위치
							YmCommonConst.STACK_COL_USAGE_CD_FD.equals(sUpUsageCd)
							|| // COIL HFL추출위치
							YmCommonConst.STACK_COL_USAGE_CD_KE.equals(sUpUsageCd)
							|| // COIL SPM보급위치
							YmCommonConst.STACK_COL_USAGE_CD_KI.equals(sUpUsageCd)
							|| // COIL SPMTAKEIN위치
							YmCommonConst.STACK_COL_USAGE_CD_KD.equals(sUpUsageCd)) {// COIL SPM추출위치

						int iSeq = YmCommonDB.deleteConveyorInfo(sUpStackColGp,sStockId);
						if (iSeq < 0) {
							// throw new EJBServiceException("=권상실적=>CONVEYOR
							// DELETE FAIL.");
							logger.println(LogLevel.DEBUG, this, "산적위치 수정=> 적치단 삭제 FAIL");
						}

					} else {

						if (YmCommonConst.STACK_COL_USAGE_CD_CX.equals(sUpUsageCd)) {// COIL 대차정지위치

							/**
							 * 대차위치 CLEAR
							 */
							String sCurrQty = "0";

							sCurrQty = "-1";

							iReq = dao.updateStackerQtyInfo(sUpStackColGp, sUpStackBedGp, sCurrQty);
						}

						iReq = dao.updateStockMoveEquipInfo(sStockId, "", "", "", "", "");
						/*
						 * 적치단 UP위치 Clear tb_ym_stacklayer Table : stock_id =
						 * ''(Empty) tb_ym_stacklayer Table : stack_layer_stat =
						 * 'E'(적치가능)
						 */
						iReq = dao.updateCraneStackLayerStat(sUpStackColGp,
								sUpStackBedGp, sUpStackLayerGp, "",
								YmCommonConst.STACK_LAYER_STAT_E);

						/**
						 * FROM 위치 상단 적치상태 수정
						 */
						if (YmCommonConst.STACK_LAYER_GP_01.equals(sUpStackLayerGp)) {

							/*
							 * A.B열연 Coil 권상실적 상단 왼쪽 상태정보를 UPDATE 상단 오른쪽 상태정보를
							 * UPDATE
							 */
							iReq = YmCommonDB.setCoilUpperState_V(
									sUpStackColGp, sUpStackBedGp,
									sUpStackLayerGp);
						}
					}

					logger.println(LogLevel.DEBUG, this, "산적위치 수정=> FROM 위치 수정 = " + iReq);

				}
			}

			/**
			 * 3. TO 위치 수정
			 */
			String sPutUsageCd = YmCommonUtil.getStackColInfoWithPk(sPutStackColGp);

			if (YmCommonConst.STACK_COL_GP_1BDC01.equals(sPutStackColGp) || // A열연
																			// COIL
																			// 분기콘베이어
					YmCommonConst.STACK_COL_GP_1CDC01.equals(sPutStackColGp) || // A열연
																				// COIL
																				// 분기콘베이어
					YmCommonConst.STACK_COL_USAGE_CD_XX.equals(sPutUsageCd) || // COIL
																				// 비상적치위치
					YmCommonConst.STACK_COL_USAGE_CD_FE.equals(sPutUsageCd) || // COIL
																				// HFL보급위치
					YmCommonConst.STACK_COL_USAGE_CD_FI.equals(sPutUsageCd) || // COIL
																				// HFLTAKEIN위치
					YmCommonConst.STACK_COL_USAGE_CD_FD.equals(sPutUsageCd) || // COIL
																				// HFL추출위치
					YmCommonConst.STACK_COL_USAGE_CD_KE.equals(sPutUsageCd) || // COIL
																				// SPM보급위치
					YmCommonConst.STACK_COL_USAGE_CD_KI.equals(sPutUsageCd) || // COIL
																				// SPMTAKEIN위치
					YmCommonConst.STACK_COL_USAGE_CD_KD.equals(sPutUsageCd)) {// COIL
																				// SPM추출위치

				int iSeq = YmCommonDB.insertConveyorInfo(sPutStackColGp,sStockId, sPutStackBedGp);
				if (iSeq < 0) {
					logger.println(LogLevel.DEBUG, this, "산적위치 수정=> 적치단 생성 FAIL");
				}

			} else {

				/**
				 * 3. TO 위치 정보 체크
				 */
				/*
				 * 적치단 Put위치에 다른 코일이 있을 경우. 해당동의 XX번지로 저장품 MAP을 수정한다.
				 */
				iReq = YmCommonDB.updateLegacyStockId_Coil(dao, sPutStackColGp, sPutStackBedGp, sPutStackLayerGp, sStockId);

//				Crane 작업 실적 등록
				String sTempLayer = sPutStackColGp.substring(0,2)+
									YmCommonConst.STACK_COL_USAGE_CD_XX+
									YmCommonConst.STACK_BED_GP_01;
				
				JDTORecord putJr = dao.getStackLayerInfoWithPk(sPutStackColGp,
																sPutStackBedGp,
																sPutStackLayerGp);

				String sToStockId 	= "00";
				String sToStat 		= "";
				
				if(putJr != null){
				
				sToStockId 	= StringHelper.evl(putJr.getFieldString("STOCK_ID"), "");
				sToStat 	= StringHelper.evl(putJr.getFieldString("STACK_LAYER_STAT"), "");
				}
				
				if(!"".equals(sToStockId)&&
						   !sStockId.equals(sToStockId)){ 
				EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
	    	 	ejbConn.trx("insertUpPutWrslRtData",new  Class[]{String.class,String.class,String.class,String.class,String.class,String.class},
															new Object[]{sToStockId,sPutStackColGp,sTempLayer,"CYMM", sPutStackColGp.substring(0,1),sUserId});
				}
				
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	            //코일제품이적작업실적
	 			JDTORecord tcRecordDM = null;
	 			tcRecordDM = JDTORecordFactory.getInstance().create(); 
	 			tcRecordDM.setField("GOODS_NO",sToStockId);
	 			tcRecordDM.setField("BEFO_STORE_LOC",sPutStackColGp+sPutStackBedGp+sPutStackLayerGp);
	 			tcRecordDM.setField("TO_STORE_LOC",sTempLayer+"0101");
	 			
	 			//인터페이스 전문 호출
	 			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
	 			ejbConn.trx("getYDDMR004PI",new Class[]{JDTORecord.class},
	 			  	  	 new Object[]{tcRecordDM}); 
	            logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 코일제품이적작업실적XX3[getM10YDLMJ1031].===");
	            //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	            
	    	 	
				if (YmCommonConst.STACK_COL_USAGE_CD_CX.equals(sPutUsageCd)) {// COIL
																				// 대차정지위치
					/**
					 * 대차위치 CLEAR
					 */
					String sCurrQty = "0";

					sCurrQty = "1";

					iReq = dao.updateStackerQtyInfo(sPutStackColGp, sPutStackBedGp, sCurrQty);

					iReq = dao.updateStockMoveEquipInfo(sStockId,
							sPutStackColGp.substring(0, 1) + "X" + sPutStackColGp.substring(2),
							sPutStackBedGp, sPutStackLayerGp, "", "");
				}

				/*
				 * 적치단 Put위치를 적치상태로 변경 tb_ym_stacklayer Table : stock_id = Coil
				 * No tb_ym_stacklayer Table : stack_layer_stat = 'L'(적치중)
				 */
				iReq = dao.updateCraneStackLayerStat(sPutStackColGp, sPutStackBedGp, sPutStackLayerGp, sStockId, sLayerStat);
				/*
				 * 적치단이 '01'단일 경우 적치단상태가 'L', 'P' 이면 상단 적치단 정보를 적치가능상태로 변경
				 */
				if (YmCommonConst.STACK_LAYER_GP_01.equals(sPutStackLayerGp)) {

					/*
					 * A.B열연 Coil 권하실적 상단 왼쪽 상태정보를 UPDATE 상단 오른쪽 상태정보를 UPDATE
					 */
					iReq = YmCommonDB.setCoilUpperState_E(sPutStackColGp, sPutStackBedGp, sPutStackLayerGp);
				}

			}
			logger.println(LogLevel.DEBUG, this, "산적위치 수정=> TO 위치 수정 = "+ iReq);

			/**
			 * 4. COIL 저장품 TABLE 수정 저장품TABLE의 이동설비항목에 권하위치값을 삭제한다. tb_ym_stock
			 * Table frtomove_equip_gp : empty('') tb_ym_stock Table
			 * frtomove_equip_bed_gp : empty('') tb_ym_stock Table
			 * frtomove_equip_layer_gp : empty('')
			 */

			iReq = dao.updateStockMoveEquipInfo(sStockId, "", "", "", "", "");

			/**
			 * 4. COIL 공통 TABLE 수정
			 */
			/*
			 * Coil 공통 Table 저장위치 Update
			 */
			iReq = dao.updateCoilCommonLocInfo(sStockId, sPutLoc);

			logger.println(LogLevel.DEBUG, this, "산적위치 수정=> 코일공통 UPDATE = " + iReq);

			/*
			 * 저장품 예상PUT 위치 CLEAR 저장품지정조회화면에서 예약상태로 남아있는것을 방지하기 위해
			 */
			{
				JDTORecord stockRc = dao.getStockInfo(sStockId);
				if (stockRc != null) {
					String sCarunloadBay = StringHelper.evl(stockRc.getFieldString("CARUNLOAD_PUT_LOC"), "");// 하차PUT위치
					if (sPutLoc.equals(sCarunloadBay)) {
						iReq = dao.updateStockPutLocWithStockId(sStockId, "");
					}
					logger.println(LogLevel.DEBUG, this,"저장품 예상PUT 위치 CLEAR A=" + sCarunloadBay);
					logger.println(LogLevel.DEBUG, this,"저장품 예상PUT 위치 CLEAR B=" + sPutLoc);
				}
			}

			/**
			 * 5. 저장품이동조건 수정
			 */
			iReq = dao.updateStockTransInfo(sStockId, sStocMv);
			logger.println(LogLevel.DEBUG, this, "산적위치 수정=> 저장품 이동조건 = "+ sStocMv + "=iReq=" + iReq);

			/**
			 * 6. Crane 작업 실적 등록
			 */
			iReq = insertUpPutWrslRtData(sStockId.trim(), sUpLoc.trim(), sPutLoc.trim(), sCurSchCode, sPutYardGp,sUserId);

			/**
			 * 7. Coil 이적실적 (출하로 이적실적 송신 YMDM008) 공통 진도 Code가 출하작업지시대기 K, 제품충당대기
			 * Z, 출하작업대기 L, 보관매출 M 이면 출하로 "이적실적 송신"
			 */

			if (sProgCd.equals(YmCommonConst.CURR_PROG_CD_COIL_K)
					|| sProgCd.equals(YmCommonConst.CURR_PROG_CD_COIL_P)  // 인도완료 추가 20091006
					|| sProgCd.equals(YmCommonConst.CURR_PROG_CD_COIL_Z)
					|| sProgCd.equals(YmCommonConst.CURR_PROG_CD_COIL_J)
					|| sProgCd.equals(YmCommonConst.CURR_PROG_CD_COIL_L)
					|| sProgCd.equals(YmCommonConst.CURR_PROG_CD_COIL_X)
					|| sProgCd.equals(YmCommonConst.CURR_PROG_CD_COIL_M)) {

				isSuccess = SendYMDM008(sPutYardGp, sStockId.trim(), sUpLoc.trim(), sPutLoc.trim());
			}

			/**
			 * 8. Coil 입고실적 (출하로 제품입고실적 송신 YMDM001) 공통 진도 Code가 입고대기 H 이면 출하로
			 * 입고실적송신
			 */
//			if (sProgCd.equals(YmCommonConst.CURR_PROG_CD_COIL_H)) {
//
//				isSuccess = SendYMDM001(sPutYardGp, sStockId.trim(), sPutLoc.trim());
//			}

			/**
			 * 9. TO위치가 B열연이고, 공통 진도 Code가 이송 작업대기 E 이면 조업으로 이송실적송신
			 * 
			 * if (sPutLoc.startsWith(YmCommonConst.YD_GP_3)
					&& sProgCd.equals(YmCommonConst.CURR_PROG_CD_COIL_E)) {
				if (!sUpYardGp.equals(sPutYardGp)) {
					isSuccess = SendYMPO159(sStockId.trim(), "D", sPutLoc.trim());
				}
			}
			 * 
			 */
			if (!sUpYardGp.equals(sPutYardGp)
					&& sProgCd.equals(YmCommonConst.CURR_PROG_CD_COIL_E)) {
					isSuccess = SendYMPO159(sStockId.trim(), "D", sPutLoc.trim());
			}
			
			/**
			 * 10. TO위치가 SPM,HFL 입측이면 보급실적을 조업으로 송신한다.
			 */
			// 코드 추가해야하지 않을까?. 최규성 2010-02-24 
			// 확인 필요.
			if (YmCommonConst.STACK_COL_USAGE_CD_FE.equals(sPutUsageCd)) {// COIL
																			// HFL보급위치
//				if(sPutLoc.startsWith(YmCommonConst.YD_GP_3) && YmCommonConst.NEW_WORK_HFL_F.equals("X")){	// B 열연 SPM2 내 HFL 일 경우
//					isSuccess = SendYMPO161(sStockId.trim(), sPutYardGp, "F", "1");
//				}else{
					isSuccess = SendYMPO161(sStockId.trim(), sPutYardGp, "H", "1");
//				}
			} else if (YmCommonConst.STACK_COL_USAGE_CD_FI.equals(sPutUsageCd)) {// COIL
																					// HFLTAKEIN위치

				isSuccess = SendYMPO161(sStockId.trim(), sPutYardGp, "H", "5");
			} else if (YmCommonConst.STACK_COL_USAGE_CD_KE.equals(sPutUsageCd)) {// COIL
																					// SPM보급위치
//				if(sPutLoc.startsWith(YmCommonConst.YD_GP_3) && YmCommonConst.NEW_WORK_SPM_N.equals("X")){	// B 열연 SPM2 내 HFL 일 경우
//					isSuccess = SendYMPO161(sStockId.trim(), sPutYardGp, "N", "1");
//				}else{
					isSuccess = SendYMPO161(sStockId.trim(), sPutYardGp, "S", "1");
//				}
			} else if (YmCommonConst.STACK_COL_USAGE_CD_KI.equals(sPutUsageCd)) {// COIL
																					// SPMTAKEIN위치
//				if(sPutLoc.startsWith(YmCommonConst.YD_GP_3) && YmCommonConst.NEW_WORK_SPM_N.equals("X")){	// B 열연 SPM2 내 HFL 일 경우
//					isSuccess = SendYMPO161(sStockId.trim(), sPutYardGp, "N", "5");
//				}else{
					isSuccess = SendYMPO161(sStockId.trim(), sPutYardGp, "S", "5");
//				}
			}

			/**
			 * 11. BACK UP 실적 전문 송신 A열연인 경우..
			 */

			if (sPutLoc.startsWith(YmCommonConst.YD_GP_1)) {

				EJBConnector ejbConn = new EJBConnector("default", "JNDICWrkOrdReg", this);
				Boolean isSuccesy = (Boolean) ejbConn.trx("callACoilCraneBackUpMsgInfoPI", 
												new Class[] {String.class, String.class, String.class },
												new Object[] { sStockId, sUpLoc, sPutLoc });
			}

			/**
			 * 12. YARD MAP 정보 실적 등록
			 */
			if (sPutLoc.startsWith(YmCommonConst.YD_GP_3)) {
				String sMsg = YmCommonUtil.setBCoilMapMsgInfo(sPutLoc);

				EJBConnector ejbConn = new EJBConnector("default", "JNDICraneStatusReg", this);
				Boolean isSucf = (Boolean) ejbConn.trx("bcyYdMapInfoPI", new Class[] { String.class }, new Object[] { sMsg });
			}
			
			
			//////////////////////////////////////////////////////////////////////////////////////////////
			//***********************이송대상재 BACKUP으로 산적위치 변경 시 실적처리 작업***************************
			//////////////////////////////////////////////////////////////////////////////////////////////
//			 ymCommonDAO dao = ymCommonDAO.getInstance();
//
//			 String trnEqpQueryId = "ym.tsinfo.getListStlfrtoMovechk";
//			 List stocklist = dao.getCommonList(trnEqpQueryId, new Object[]{sStockId});   
//			 if(stocklist.size()<=0){
//				 logger.println(LogLevel.DEBUG,this, "이미 상차실적 처리가 완료 된 경우 "+sStockId);
//			 } else {			
//			    JDTORecord tcRecord=null;
//			    tcRecord = JDTORecordFactory.getInstance().create(); 
//			    tcRecord.setField("sStockList", sStockId);
//			     
//			    EJBConnector ejbConn 	= new EJBConnector("default","JNDITsInfoReg",this);
//			    Boolean isSucf = (Boolean)ejbConn.trx("CarinfoFrtoMoveBackup",new Class[]{JDTORecord.class},
//													 new Object[]{tcRecord});
//			 }
			 /////////////////////////////////////////////////////////////////////////////////////////////
			 
			isSuccess = true;
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return isSuccess;
	}

	/**
	 * 오퍼레이션명 : 
	 *
	 * SLAB 산적위치 수정 메소드
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                     
	public boolean changeSlabLocationInfo(String sStockId,	//저장품ID 
									 String sUpLoc,	//FROM LOC
									 String sPutLoc,	//TO LOC 
									 String sGbn) {	//상단(U)/교체(R) 구분
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		return changeSlabLocationInfo("",
								  "",
								  "",
								  sStockId,	
								  sUpLoc,
								  sPutLoc,
								  sGbn,	
								  "SYSTEM");									 	
	}	
	/**
	 * 오퍼레이션명 : 
	 *
	 * SLAB 산적위치 수정 메소드
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                     								 	 
	public boolean changeSlabLocationInfo(String sStockId,	//저장품ID 
									 String sUpLoc,	//FROM LOC
									 String sPutLoc,	//TO LOC 
									 String sGbn,		//상단(U)/교체(R) 구분	
									 String sUserId) {	//사용자
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		return changeSlabLocationInfo("",
								  "",
								  "",
								  sStockId,	
								  sUpLoc,
								  sPutLoc,
								  sGbn,	
								  sUserId);									 	
	}
	

	/**
	 * 오퍼레이션명 : 
	 *
	 * SLAB 산적위치 수정 메소드
	 * 임가공 PIDEV
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                     
	public boolean changeSlabLocationInfoPI(String sStockId,	//저장품ID 
									 String sUpLoc,	//FROM LOC
									 String sPutLoc,	//TO LOC 
									 String sGbn) {	//상단(U)/교체(R) 구분
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		return changeSlabLocationInfoPI("",
								  "",
								  "",
								  sStockId,	
								  sUpLoc,
								  sPutLoc,
								  sGbn,	
								  "SYSTEM");									 	
	}	
	/**
	 * 오퍼레이션명 : 
	 * 임가공 PIDEV
	 * SLAB 산적위치 수정 메소드
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                     								 	 
	public boolean changeSlabLocationInfoPI(String sStockId,	//저장품ID 
									 String sUpLoc,	//FROM LOC
									 String sPutLoc,	//TO LOC 
									 String sGbn,		//상단(U)/교체(R) 구분	
									 String sUserId) {	//사용자
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		return changeSlabLocationInfoPI("",
								  "",
								  "",
								  sStockId,	
								  sUpLoc,
								  sPutLoc,
								  sGbn,	
								  sUserId);									 	
	}
		
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * SLAB 산적위치 수정 메소드
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                     								 	 
	public boolean changeSlabLocationInfoTX(String sStockId,	//저장품ID 
									 String sUpLoc,	//FROM LOC
									 String sPutLoc,	//TO LOC 
									 String sGbn,		//상단(U)/교체(R) 구분	
									 String sUserId,	//사용자
									 String schecked
									 ) {	
		boolean isSuccess = false;
		
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			
			 if(schecked.equals("Y")){
			    	logger.println(LogLevel.DEBUG,this, "★★★★이송백업 실적 처리 체크한 경우★★★★ "+sStockId);
				    JDTORecord tcRecord=null;
				    tcRecord = JDTORecordFactory.getInstance().create(); 
				    tcRecord.setField("sStockList", sStockId);
				     
				    //실적BACKUP처리 CALL
				    EJBConnector ejbConn2 	= new EJBConnector("default","JNDITsInfoReg",this);
				    Boolean isSucf2 = (Boolean)ejbConn2.trx("CarinfoSlabFrtoMoveBackup",new Class[]{JDTORecord.class}, new Object[]{tcRecord});
				    
				    isSuccess = isSucf2.booleanValue();
			 }
			
			 
			//기존 저장위치 수정 기능 
			isSuccess = this.changeSlabLocationInfo("",
													  "",
													  "",
													  sStockId,	
													  sUpLoc,
													  sPutLoc,
													  sGbn,	
													  sUserId);		
			
	 
			
		   
			
			
		
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return isSuccess;
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * SLAB 산적위치 수정 메소드
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                     
	public boolean changeSlabLocationInfo(String sBTcGbn,		//비상조업처리구분(U:L2시스템,B:L2산적위치)
									 String sBCraneNo,		//비상조업크레인번호
									 String sBSchCd,		//비상조업스케쥴코드
									 String sStockId,		//저장품ID 
									 String sUpLoc,		//FROM LOC
									 String sPutLoc,		//TO LOC 
									 String sGbn,			//상단(U)/교체(R) 구분	
									 String sUserId) {		//사용자
		boolean isSuccess = false;
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			int iReq = 0;
			String sMsg 		= "";
			String sTmpStockId 	= "";
			String sTmpStat 	= "";

			String sWbookId 	= "";
			String sSchId 		= "";
			String sCurSchCode 	= "";
			String sCurBayGp 	= "";

			String sPutStackColGp 	= "";
			String sPutStackBedGp 	= "";
			String sPutStackLayerGp = "";

			String sLayerStat = YmCommonConst.STACK_LAYER_STAT_L;
			//슬라브 공통 테이블의 진도코드를 참조해서 저장품이동조건을 가져온다.
			//sStockInfo[0]=진도코드
			//sStockInfo[1]=이동조건코드
			String[] sStockInfo = YmCommonUtil.getSlabCurrProgCd(sStockId,"");
			String sProgCd = sStockInfo[0];
			String sStocMv = sStockInfo[1];

			String sUpYardGp = "";
			String sPutYardGp = "";

			if (sUpLoc.length() > 1) {
				sUpYardGp = sUpLoc.substring(0, 1);
			}

			if (sPutLoc.length() > 1) {
				sPutYardGp = sPutLoc.substring(0, 1);
			}

			if (sProgCd.equals(YmCommonConst.CURR_PROG_CD_SLAB_3)) {
				throw new EJBServiceException("산적위치 수정=> 생산종료된 저장품은 수정을 할 수 없습니다.");
			}

			if (!"".equals(sUpYardGp) && !"".equals(sPutYardGp)	&& !sUpYardGp.equals(sPutYardGp)) {
				if (!sProgCd.equals(YmCommonConst.CURR_PROG_CD_SLAB_C)) {
					throw new EJBServiceException("산적위치 수정=> 공장간 산적위치 수정은 할 수 없습니다.");
				}
			}

			JDTORecord stockV = dao.getStockInfo(sStockId);

			/**
			 * 0. 입력한 To 위치 정합성 점검
			 */
			if (sPutLoc.length() == 10) {
				sPutStackColGp = sPutLoc.substring(0, 6);
				sPutStackBedGp = sPutLoc.substring(6, 8);
				sPutStackLayerGp = sPutLoc.substring(8, 10);
			}

			JDTORecord vColGp = dao.getStackColInfoWithPk(sPutStackColGp);

			if (vColGp == null) {
				throw new EJBServiceException("산적위치 수정=> To위치정보가 잘못 입력되었습니다.");
			}

			/**
			 * 1. 작업예약 유무 체크
			 */
			if (stockV != null) {
				sWbookId = StringHelper.evl(stockV.getFieldString("WBOOK_ID"),"");
			}
			logger.println(LogLevel.DEBUG, this, "산적위치 수정=> 작업예약ID=" + sWbookId);

			if (!"".equals(sWbookId)) {

				/**
				 * 1.1 스케쥴정보 있으면 삭제
				 */
				JDTORecord schV = dao.getSchInfoWithWbookId(sWbookId, sStockId);

				if (schV != null) {
					/**
					 * 1.1.1 스케쥴 취소 모듈 CALL
					 */
					sSchId = StringHelper.evl(schV.getFieldString("SCH_ID"), "");
					sCurSchCode = StringHelper.evl(schV.getFieldString("SCH_WORK_KIND"), "");

					logger.println(LogLevel.DEBUG, this, "산적위치 수정=> 스케쥴ID=" + sSchId);

					isSuccess = cancelSlabSchInfo(sSchId);
				}

				JDTORecord wbookV = dao.getWbookInfo(sWbookId);
				/**
				 * 1.2 작업예약 스케쥴 코드 체크
				 */
				if (wbookV != null) {
					sCurSchCode = StringHelper.evl(wbookV.getFieldString("SCH_WORK_KIND"), "");
					sCurBayGp = StringHelper.evl(wbookV.getFieldString("BAY_GP"), "");
				}
				logger.println(LogLevel.DEBUG, this, "산적위치 수정=> 스케쥴코드=" + sCurSchCode);

				if (YmCommonConst.NEW_SCH_WORK_KIND_SVML.equals(sCurSchCode)	// Slab 이송상차
				  ||YmCommonConst.NEW_SCH_WORK_KIND_SVMU.equals(sCurSchCode)) { // Slab 이송하차

					// 즉 상차지시 편성된 시점의 작업예약은 삭제하지 않는다.
					sLayerStat = YmCommonConst.STACK_LAYER_STAT_S;

					if (sCurBayGp.equals(sPutStackColGp.substring(1, 2))) {
						// 같은 동에 산적위치 수정을 한 경우
					} else {
						// 다른 동으로 산적위치 수정을 한 경우.
						// 이 경우에 작업예약 동구분 항목도 수정을 해준다.
						iReq = dao.updateBayGpWithWbookId(sWbookId, sPutStackColGp.substring(1, 2));
					}

					logger.println(LogLevel.DEBUG, this,"산적위치 수정=> 작업예약 삭제안함.");
				} else {
					/*
					 * 1.2.1 저장품 Table의 Wbook_id 항목을 Update tb_ym_stock Table
					 * wbook_id : ''(empty)
					 */

					iReq = dao.updateStockWbookId(sStockId, "");

					/**
					 * 1.2.2 작업예약정보 있으면 삭제
					 */
					int iSeq = dao.deleteWbookInfo(sWbookId);
					logger.println(LogLevel.DEBUG, this, "산적위치 수정=> 작업예약 삭제=" + iSeq);
				}
			}

			String sUpStackColGp = "";
			String sUpStackBedGp = "";
			String sUpStackLayerGp = "";
			String sUpUsageCd = "";

			/**
			 * 2. 저장품의 MAP정보를 가져온다. 중복위치도 체크한다.
			 */
			List stockL = dao.getStackLayerInfoWithStockId_03(sStockId);

			JDTORecord stackV = null;
			JDTORecord upRc = null;

			if (stockL != null) {
				for (int inx = 0; inx < stockL.size(); inx++) {
					stackV = (JDTORecord) stockL.get(inx);

					sUpStackColGp = StringHelper.evl(stackV.getFieldString("STACK_COL_GP"), "");
					sUpStackBedGp = StringHelper.evl(stackV.getFieldString("STACK_BED_GP"), "");
					sUpStackLayerGp = StringHelper.evl(stackV.getFieldString("STACK_LAYER_GP"), "");

					sUpUsageCd = YmCommonUtil.getStackColInfoWithPk(sUpStackColGp);

					if (YmCommonConst.STACK_COL_USAGE_CD_XX.equals(sUpUsageCd)		// SLAB 비상적치위치
						|| YmCommonConst.STACK_COL_USAGE_CD_SE.equals(sUpUsageCd)	// SLAB Scafing 입측
						|| YmCommonConst.STACK_COL_USAGE_CD_SD.equals(sUpUsageCd)) {// SLAB Scafing 출측

						int iSeq = YmCommonDB.deleteConveyorInfo(sUpStackColGp, sStockId);
						if (iSeq < 0) {
							// throw new EJBServiceException("=권상실적=>CONVEYOR DELETE FAIL.");
							logger.println(LogLevel.DEBUG, this, "산적위치 수정=> 적치단 삭제 FAIL");
						}

					} else {

						if (YmCommonConst.STACK_COL_USAGE_CD_CX.equals(sUpUsageCd)) {// COIL 대차정지위치

							/**
							 * 대차위치 CLEAR
							 */
							String sCurrQty = "0";
							sCurrQty = "-1";
							iReq = dao.updateStackerQtyInfo(sUpStackColGp, sUpStackBedGp, sCurrQty);
						}

						iReq = dao.updateStockMoveEquipInfo(sStockId, "", "", "", "", "");

						/*
						 * 적치단 UP위치 Clear tb_ym_stacklayer Table : stock_id =
						 * ''(Empty) tb_ym_stacklayer Table : stack_layer_stat =
						 * 'E'(적치가능)
						 */
						iReq = dao.updateCraneStackLayerStat(sUpStackColGp,
															sUpStackBedGp,
															sUpStackLayerGp,
															"",
															YmCommonConst.STACK_LAYER_STAT_E);

						/*
						 * B열연 Slab 바로 위 상단 상태정보를 UPDATE
						 */
						iReq = YmCommonDB.setSlabUpperState_V(sUpStackColGp,
															sUpStackBedGp, 
															sUpStackLayerGp);
					}
				}
				logger.println(LogLevel.DEBUG, this, "산적위치 수정=> 수정전 BED정보 송신 :"+sUpStackColGp+sUpStackBedGp);
				
				if(!"".equals(sUpStackColGp) && YmCommonConst.YD_GP_0.equals(sUpStackColGp.substring(0,1))){
					sMsg = YmCommonUtil.setBSlabMapMsgInfo(sUpStackColGp+sUpStackBedGp);
					EJBConnector ejbConn = new EJBConnector("default", "JNDICraneStatusReg", this);
					Boolean isSucf = (Boolean) ejbConn.trx("bsyYdMapInfo", new Class[] { String.class }, new Object[] { sMsg });
				}
				
				/**
				 * 일관제철 B-CAST 산적위치 수정에 따른 차량재료정보 동기화 처리 
				 */
				if(	"0APT01".equals(sUpStackColGp)||
					"0APT02".equals(sUpStackColGp)||
					"0BPT01".equals(sUpStackColGp)||
					"0BPT02".equals(sUpStackColGp)){
					
					int iCar = dao.deleteCarMtlInfo(sUpStackColGp,sStockId);
				}
			}

			/**
			 * 4. TO 위치 수정
			 */
			String sPutUsageCd = YmCommonUtil.getStackColInfoWithPk(sPutStackColGp);

			if (YmCommonConst.STACK_COL_USAGE_CD_XX.equals(sPutUsageCd) || // SLAB
																			// 비상적치위치
					YmCommonConst.STACK_COL_USAGE_CD_SE.equals(sPutUsageCd) || // SLAB
																				// Scafing
																				// 입측
					YmCommonConst.STACK_COL_USAGE_CD_SD.equals(sPutUsageCd)) {// SLAB
																				// Scafing
																				// 출측

				int iSeq = YmCommonDB.insertConveyorInfo(sPutStackColGp, sStockId, sPutStackBedGp);
				if (iSeq < 0) {
					logger.println(LogLevel.DEBUG, this, "산적위치 수정=> 적치단 생성 FAIL");
				}

			} else {

				/**
				 * 4.1 TO 위치 정보 체크
				 */
				if ("U".equals(sGbn)) {
					/*
					 * 적치단 Put위치정보부터 상단으로 정보를 SHIFT한다.
					 */
					iReq = YmCommonDB.updateLegacyStockId_Slab_01(dao,
																sPutStackColGp, 
																sPutStackBedGp, 
																sPutStackLayerGp,
																sStockId);
				} else if ("R".equals(sGbn)) {
					/*
					 * 적치단 Put위치에 다른 SLAB가 있을 경우. 해당동의 XX번지로 저장품 MAP을 수정한다.
					 */
					iReq = YmCommonDB.updateLegacyStockId_Slab(dao,
																sPutStackColGp, 
																sPutStackBedGp, 
																sPutStackLayerGp,
																sStockId);
				}

				if (YmCommonConst.STACK_COL_USAGE_CD_CX.equals(sPutUsageCd)) {// COIL
																				// 대차정지위치
					/**
					 * 대차위치 CLEAR
					 */
					String sCurrQty = "0";

					sCurrQty = "1";

					iReq = dao.updateStackerQtyInfo(sPutStackColGp,
													sPutStackBedGp, 
													sCurrQty);

					iReq = dao.updateStockMoveEquipInfo(sStockId,
														sPutStackColGp.substring(0, 1) + "X"+ sPutStackColGp.substring(2, 4) + "0" + sPutStackColGp.substring(4, 5),
														sPutStackBedGp, 
														sPutStackLayerGp,
														"", 
														"");
				}

				/*
				 * 적치단 Put위치를 적치상태로 변경 tb_ym_stacklayer Table : stock_id = Coil
				 * No tb_ym_stacklayer Table : stack_layer_stat = 'L'(적치중)
				 */
				iReq = dao.updateCraneStackLayerStat(sPutStackColGp,
													sPutStackBedGp, 
													sPutStackLayerGp, 
													sStockId, 
													sLayerStat);
				/*
				 * B열연 Slab 바로 위 상단 상태정보를 UPDATE
				 */
				iReq = YmCommonDB.setSlabUpperState_E(sPutStackColGp,
													sPutStackBedGp, 
													sPutStackLayerGp);

			}
			logger.println(LogLevel.DEBUG, this, "산적위치 수정=> TO 위치 수정 = " + iReq);

			/**
			 * 5.1 장입대상재 산적위치 수정으로 야드로 다시 적치시 관재로 취소전문을 송신한다.
			 */
			if (YmCommonConst.STACK_COL_USAGE_CD_WB.equals(sUpUsageCd) || // W/B
				YmCommonConst.STACK_COL_USAGE_CD_CT.equals(sUpUsageCd)) {// CTC
				
				/*
		    	 * 	2009.07 YJK 생산통제 장입진행정보 기존모듈 삭제.
		    	 * 
					ZZPC001 model = new ZZPC001();
					model.setTcCode("YMPC100");
					model.setrealStlNo(sStockId);
					model.seteventStat("09");
					model.seteventOccurDDTT(YmCommonUtil.getStringYMDHMS());
					EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg", this);
					Boolean isTrue = (Boolean) ejbConn.trx("sendInternalModel",	
									new Class[] { CommonModel.class }, new Object[] { model });
				  *						
				  */
			}
			/**
			 * 5.2 장입대상재 산적위치 수정으로 보급시 장입순번을 CLEAR한다.
			 */
			if (YmCommonConst.STACK_COL_USAGE_CD_WB.equals(sPutUsageCd) || // W/B
					YmCommonConst.STACK_COL_USAGE_CD_CT.equals(sPutUsageCd)) {// CTC

				// 저장품 TABLE CHARGE_LOT_NO 항목 CLEAR
				iReq = dao.updateStockLotNoWithStockId(sStockId, "");
				
				//야드 L2 전송 장입순번 Clear CALL
				boolean isTrue = callL2LotEndInfo_Slab(sStockId);	
			}

			/**
			 * 5.3 SLAB이송의 상차 실적을 송신
			 */
			if (sPutLoc.startsWith(YmCommonConst.YD_GP_2)
					&& sProgCd.equals(YmCommonConst.CURR_PROG_CD_SLAB_C)) {

				sendSlabLoadResult(sStockId, sPutLoc, YmCommonConst.CAR_GP_D);
			}

			/**
			 * 5. SLAB 공통 TABLE 수정
			 */

			iReq = dao.updateSlabCommonLocInfo(sStockId, sPutLoc);

			/*
			 * 저장품 예상PUT 위치 CLEAR 저장품지정조회화면에서 예약상태로 남아있는것을 방지하기 위해
			 */
			{
				JDTORecord stockRc = dao.getStockInfo(sStockId);
				if (stockRc != null) {
					String sCarunloadBay = StringHelper.evl(stockRc.getFieldString("CARUNLOAD_PUT_LOC"), "");// 하차PUT위치
					if (sPutLoc.equals(sCarunloadBay)) {
						iReq = dao.updateStockPutLocWithStockId(sStockId, "");
					}
					logger.println(LogLevel.DEBUG, this, "저장품 예상PUT 위치 CLEAR A=" + sCarunloadBay);
					logger.println(LogLevel.DEBUG, this, "저장품 예상PUT 위치 CLEAR B=" + sPutLoc);
				}
			}

			/**
			 * 6. 저장품이동조건 수정
			 */
			iReq = dao.updateStockTransInfo(sStockId, sStocMv);

			/**
			 * 7. Crane 작업 실적 등록
			 */
			 				     
			iReq = insertUpPutWrslRtData(sStockId, sUpLoc, sPutLoc, sCurSchCode, sPutYardGp, sUserId, sBTcGbn, sBCraneNo, sBSchCd);

			/**
			 * 8. SLAB 이적실적 (출하로 이적실적 송신 YMDM009) 
			 * 공통 진도 Code가 1 - 생산예정, A - 수입검사대기 이면 출하로 "이적실적 송신"
			 */

			if (sProgCd.equals(YmCommonConst.CURR_PROG_CD_SLAB_1)
					|| sProgCd.equals(YmCommonConst.CURR_PROG_CD_SLAB_A)) {

				isSuccess = SendYMDM009(sPutYardGp, sStockId.trim(), sUpLoc.trim(), sPutLoc.trim());
			}

			/**
			 * 9. YARD MAP 정보 실적 등록
			 */
//			if (sPutLoc.startsWith(YmCommonConst.YD_GP_2) || sPutLoc.startsWith(YmCommonConst.YD_GP_0)) {
				sMsg = YmCommonUtil.setBSlabMapMsgInfo(sPutLoc);

				EJBConnector ejbConn = new EJBConnector("default", "JNDICraneStatusReg", this);
				Boolean isSucf = (Boolean) ejbConn.trx("bsyYdMapInfo", new Class[] { String.class }, new Object[] { sMsg });
//			}
				//A열연 SLAB 야드 추가 (MCH)	
/*			}else if (sPutLoc.startsWith(YmCommonConst.YD_GP_0)) {
				String sMsg = YmCommonUtil.setBSlabMapMsgInfo(sPutLoc);

				EJBConnector ejbConn = new EJBConnector("default", "JNDICraneStatusReg", this);
				Boolean isSucf = (Boolean) ejbConn.trx("bsyYdMapInfo", new Class[] { String.class }, new Object[] { sMsg });
			}
			*/
			
			/**
			 * 일관제철 B-CAST 산적위치 수정에 따른 차량재료정보 동기화 처리 
			 */
			if(	"0APT01".equals(sPutStackColGp)||
				"0APT02".equals(sPutStackColGp)||
				"0BPT01".equals(sPutStackColGp)||
				"0BPT02".equals(sPutStackColGp)){
				
				int iCar = dao.insertCarMtlInfo(sPutStackColGp,sPutStackBedGp,sPutStackLayerGp,sStockId);
			}
			
			isSuccess = true;
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return isSuccess;
	}
	
	/**
	 * 오퍼레이션명 : 
	 * 임가공 PIDEV
	 * SLAB 산적위치 수정 메소드
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                     
	public boolean changeSlabLocationInfoPI(String sBTcGbn,		//비상조업처리구분(U:L2시스템,B:L2산적위치)
									 String sBCraneNo,		//비상조업크레인번호
									 String sBSchCd,		//비상조업스케쥴코드
									 String sStockId,		//저장품ID 
									 String sUpLoc,		//FROM LOC
									 String sPutLoc,		//TO LOC 
									 String sGbn,			//상단(U)/교체(R) 구분	
									 String sUserId) {		//사용자
		boolean isSuccess = false;
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			int iReq = 0;
			String sMsg 		= "";
			String sTmpStockId 	= "";
			String sTmpStat 	= "";

			String sWbookId 	= "";
			String sSchId 		= "";
			String sCurSchCode 	= "";
			String sCurBayGp 	= "";

			String sPutStackColGp 	= "";
			String sPutStackBedGp 	= "";
			String sPutStackLayerGp = "";

			String sLayerStat = YmCommonConst.STACK_LAYER_STAT_L;
			//슬라브 공통 테이블의 진도코드를 참조해서 저장품이동조건을 가져온다.
			//sStockInfo[0]=진도코드
			//sStockInfo[1]=이동조건코드
			String[] sStockInfo = YmCommonUtil.getSlabCurrProgCd(sStockId,"");
			String sProgCd = sStockInfo[0];
			String sStocMv = sStockInfo[1];

			String sUpYardGp = "";
			String sPutYardGp = "";

			if (sUpLoc.length() > 1) {
				sUpYardGp = sUpLoc.substring(0, 1);
			}

			if (sPutLoc.length() > 1) {
				sPutYardGp = sPutLoc.substring(0, 1);
			}

			if (sProgCd.equals(YmCommonConst.CURR_PROG_CD_SLAB_3)) {
				throw new EJBServiceException("산적위치 수정=> 생산종료된 저장품은 수정을 할 수 없습니다.");
			}

			if (!"".equals(sUpYardGp) && !"".equals(sPutYardGp)	&& !sUpYardGp.equals(sPutYardGp)) {
				if (!sProgCd.equals(YmCommonConst.CURR_PROG_CD_SLAB_C)) {
					throw new EJBServiceException("산적위치 수정=> 공장간 산적위치 수정은 할 수 없습니다.");
				}
			}

			JDTORecord stockV = dao.getStockInfoPI(sStockId);

			/**
			 * 0. 입력한 To 위치 정합성 점검
			 */
			if (sPutLoc.length() == 10) {
				sPutStackColGp = sPutLoc.substring(0, 6);
				sPutStackBedGp = sPutLoc.substring(6, 8);
				sPutStackLayerGp = sPutLoc.substring(8, 10);
			}

			JDTORecord vColGp = dao.getStackColInfoWithPk(sPutStackColGp);

			if (vColGp == null) {
				throw new EJBServiceException("산적위치 수정=> To위치정보가 잘못 입력되었습니다.");
			}

			/**
			 * 1. 작업예약 유무 체크
			 */
			if (stockV != null) {
				sWbookId = StringHelper.evl(stockV.getFieldString("WBOOK_ID"),"");
			}
			logger.println(LogLevel.DEBUG, this, "산적위치 수정=> 작업예약ID=" + sWbookId);

			if (!"".equals(sWbookId)) {

				/**
				 * 1.1 스케쥴정보 있으면 삭제
				 */
				JDTORecord schV = dao.getSchInfoWithWbookId(sWbookId, sStockId);

				if (schV != null) {
					/**
					 * 1.1.1 스케쥴 취소 모듈 CALL
					 */
					sSchId = StringHelper.evl(schV.getFieldString("SCH_ID"), "");
					sCurSchCode = StringHelper.evl(schV.getFieldString("SCH_WORK_KIND"), "");

					logger.println(LogLevel.DEBUG, this, "산적위치 수정=> 스케쥴ID=" + sSchId);

					isSuccess = cancelSlabSchInfoPI(sSchId);
				}

				JDTORecord wbookV = dao.getWbookInfo(sWbookId);
				/**
				 * 1.2 작업예약 스케쥴 코드 체크
				 */
				if (wbookV != null) {
					sCurSchCode = StringHelper.evl(wbookV.getFieldString("SCH_WORK_KIND"), "");
					sCurBayGp = StringHelper.evl(wbookV.getFieldString("BAY_GP"), "");
				}
				logger.println(LogLevel.DEBUG, this, "산적위치 수정=> 스케쥴코드=" + sCurSchCode);

				if (YmCommonConst.NEW_SCH_WORK_KIND_SVML.equals(sCurSchCode)	// Slab 이송상차
				  ||YmCommonConst.NEW_SCH_WORK_KIND_SVMU.equals(sCurSchCode)) { // Slab 이송하차

					// 즉 상차지시 편성된 시점의 작업예약은 삭제하지 않는다.
					sLayerStat = YmCommonConst.STACK_LAYER_STAT_S;

					if (sCurBayGp.equals(sPutStackColGp.substring(1, 2))) {
						// 같은 동에 산적위치 수정을 한 경우
					} else {
						// 다른 동으로 산적위치 수정을 한 경우.
						// 이 경우에 작업예약 동구분 항목도 수정을 해준다.
						iReq = dao.updateBayGpWithWbookId(sWbookId, sPutStackColGp.substring(1, 2));
					}

					logger.println(LogLevel.DEBUG, this,"산적위치 수정=> 작업예약 삭제안함.");
				} else {
					/*
					 * 1.2.1 저장품 Table의 Wbook_id 항목을 Update tb_ym_stock Table
					 * wbook_id : ''(empty)
					 */

					iReq = dao.updateStockWbookId(sStockId, "");

					/**
					 * 1.2.2 작업예약정보 있으면 삭제
					 */
					int iSeq = dao.deleteWbookInfo(sWbookId);
					logger.println(LogLevel.DEBUG, this, "산적위치 수정=> 작업예약 삭제=" + iSeq);
				}
			}

			String sUpStackColGp = "";
			String sUpStackBedGp = "";
			String sUpStackLayerGp = "";
			String sUpUsageCd = "";

			/**
			 * 2. 저장품의 MAP정보를 가져온다. 중복위치도 체크한다.
			 */
			List stockL = dao.getStackLayerInfoWithStockId_03(sStockId);

			JDTORecord stackV = null;
			JDTORecord upRc = null;

			if (stockL != null) {
				for (int inx = 0; inx < stockL.size(); inx++) {
					stackV = (JDTORecord) stockL.get(inx);

					sUpStackColGp = StringHelper.evl(stackV.getFieldString("STACK_COL_GP"), "");
					sUpStackBedGp = StringHelper.evl(stackV.getFieldString("STACK_BED_GP"), "");
					sUpStackLayerGp = StringHelper.evl(stackV.getFieldString("STACK_LAYER_GP"), "");

					sUpUsageCd = YmCommonUtil.getStackColInfoWithPk(sUpStackColGp);

					if (YmCommonConst.STACK_COL_USAGE_CD_XX.equals(sUpUsageCd)		// SLAB 비상적치위치
						|| YmCommonConst.STACK_COL_USAGE_CD_SE.equals(sUpUsageCd)	// SLAB Scafing 입측
						|| YmCommonConst.STACK_COL_USAGE_CD_SD.equals(sUpUsageCd)) {// SLAB Scafing 출측

						int iSeq = YmCommonDB.deleteConveyorInfo(sUpStackColGp, sStockId);
						if (iSeq < 0) {
							// throw new EJBServiceException("=권상실적=>CONVEYOR DELETE FAIL.");
							logger.println(LogLevel.DEBUG, this, "산적위치 수정=> 적치단 삭제 FAIL");
						}

					} else {

						if (YmCommonConst.STACK_COL_USAGE_CD_CX.equals(sUpUsageCd)) {// COIL 대차정지위치

							/**
							 * 대차위치 CLEAR
							 */
							String sCurrQty = "0";
							sCurrQty = "-1";
							iReq = dao.updateStackerQtyInfo(sUpStackColGp, sUpStackBedGp, sCurrQty);
						}

						iReq = dao.updateStockMoveEquipInfo(sStockId, "", "", "", "", "");

						/*
						 * 적치단 UP위치 Clear tb_ym_stacklayer Table : stock_id =
						 * ''(Empty) tb_ym_stacklayer Table : stack_layer_stat =
						 * 'E'(적치가능)
						 */
						iReq = dao.updateCraneStackLayerStat(sUpStackColGp,
															sUpStackBedGp,
															sUpStackLayerGp,
															"",
															YmCommonConst.STACK_LAYER_STAT_E);

						/*
						 * B열연 Slab 바로 위 상단 상태정보를 UPDATE
						 */
						iReq = YmCommonDB.setSlabUpperState_V(sUpStackColGp,
															sUpStackBedGp, 
															sUpStackLayerGp);
					}
				}
				logger.println(LogLevel.DEBUG, this, "산적위치 수정=> 수정전 BED정보 송신 :"+sUpStackColGp+sUpStackBedGp);
				
				if(!"".equals(sUpStackColGp) && YmCommonConst.YD_GP_0.equals(sUpStackColGp.substring(0,1))){
					sMsg = YmCommonUtil.setBSlabMapMsgInfo(sUpStackColGp+sUpStackBedGp);
					EJBConnector ejbConn = new EJBConnector("default", "JNDICraneStatusReg", this);
					Boolean isSucf = (Boolean) ejbConn.trx("bsyYdMapInfoPI", new Class[] { String.class }, new Object[] { sMsg });
				}
				
				/**
				 * 일관제철 B-CAST 산적위치 수정에 따른 차량재료정보 동기화 처리 
				 */
				if(	"0APT01".equals(sUpStackColGp)||
					"0APT02".equals(sUpStackColGp)||
					"0BPT01".equals(sUpStackColGp)||
					"0BPT02".equals(sUpStackColGp)){
					
					int iCar = dao.deleteCarMtlInfo(sUpStackColGp,sStockId);
				}
			}

			/**
			 * 4. TO 위치 수정
			 */
			String sPutUsageCd = YmCommonUtil.getStackColInfoWithPk(sPutStackColGp);

			if (YmCommonConst.STACK_COL_USAGE_CD_XX.equals(sPutUsageCd) || // SLAB
																			// 비상적치위치
					YmCommonConst.STACK_COL_USAGE_CD_SE.equals(sPutUsageCd) || // SLAB
																				// Scafing
																				// 입측
					YmCommonConst.STACK_COL_USAGE_CD_SD.equals(sPutUsageCd)) {// SLAB
																				// Scafing
																				// 출측

				int iSeq = YmCommonDB.insertConveyorInfo(sPutStackColGp, sStockId, sPutStackBedGp);
				if (iSeq < 0) {
					logger.println(LogLevel.DEBUG, this, "산적위치 수정=> 적치단 생성 FAIL");
				}

			} else {

				/**
				 * 4.1 TO 위치 정보 체크
				 */
				if ("U".equals(sGbn)) {
					/*
					 * 적치단 Put위치정보부터 상단으로 정보를 SHIFT한다.
					 */
					iReq = YmCommonDB.updateLegacyStockId_Slab_01(dao,
																sPutStackColGp, 
																sPutStackBedGp, 
																sPutStackLayerGp,
																sStockId);
				} else if ("R".equals(sGbn)) {
					/*
					 * 적치단 Put위치에 다른 SLAB가 있을 경우. 해당동의 XX번지로 저장품 MAP을 수정한다.
					 */
					iReq = YmCommonDB.updateLegacyStockId_Slab(dao,
																sPutStackColGp, 
																sPutStackBedGp, 
																sPutStackLayerGp,
																sStockId);
				}

				if (YmCommonConst.STACK_COL_USAGE_CD_CX.equals(sPutUsageCd)) {// COIL
																				// 대차정지위치
					/**
					 * 대차위치 CLEAR
					 */
					String sCurrQty = "0";

					sCurrQty = "1";

					iReq = dao.updateStackerQtyInfo(sPutStackColGp,
													sPutStackBedGp, 
													sCurrQty);

					iReq = dao.updateStockMoveEquipInfo(sStockId,
														sPutStackColGp.substring(0, 1) + "X"+ sPutStackColGp.substring(2, 4) + "0" + sPutStackColGp.substring(4, 5),
														sPutStackBedGp, 
														sPutStackLayerGp,
														"", 
														"");
				}

				/*
				 * 적치단 Put위치를 적치상태로 변경 tb_ym_stacklayer Table : stock_id = Coil
				 * No tb_ym_stacklayer Table : stack_layer_stat = 'L'(적치중)
				 */
				iReq = dao.updateCraneStackLayerStat(sPutStackColGp,
													sPutStackBedGp, 
													sPutStackLayerGp, 
													sStockId, 
													sLayerStat);
				/*
				 * B열연 Slab 바로 위 상단 상태정보를 UPDATE
				 */
				iReq = YmCommonDB.setSlabUpperState_E(sPutStackColGp,
													sPutStackBedGp, 
													sPutStackLayerGp);

			}
			logger.println(LogLevel.DEBUG, this, "산적위치 수정=> TO 위치 수정 = " + iReq);

			/**
			 * 5.1 장입대상재 산적위치 수정으로 야드로 다시 적치시 관재로 취소전문을 송신한다.
			 */
			if (YmCommonConst.STACK_COL_USAGE_CD_WB.equals(sUpUsageCd) || // W/B
				YmCommonConst.STACK_COL_USAGE_CD_CT.equals(sUpUsageCd)) {// CTC
				
				/*
		    	 * 	2009.07 YJK 생산통제 장입진행정보 기존모듈 삭제.
		    	 * 
					ZZPC001 model = new ZZPC001();
					model.setTcCode("YMPC100");
					model.setrealStlNo(sStockId);
					model.seteventStat("09");
					model.seteventOccurDDTT(YmCommonUtil.getStringYMDHMS());
					EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg", this);
					Boolean isTrue = (Boolean) ejbConn.trx("sendInternalModel",	
									new Class[] { CommonModel.class }, new Object[] { model });
				  *						
				  */
			}
			/**
			 * 5.2 장입대상재 산적위치 수정으로 보급시 장입순번을 CLEAR한다.
			 */
			if (YmCommonConst.STACK_COL_USAGE_CD_WB.equals(sPutUsageCd) || // W/B
					YmCommonConst.STACK_COL_USAGE_CD_CT.equals(sPutUsageCd)) {// CTC

				// 저장품 TABLE CHARGE_LOT_NO 항목 CLEAR
				iReq = dao.updateStockLotNoWithStockId(sStockId, "");
				
				//야드 L2 전송 장입순번 Clear CALL
				boolean isTrue = callL2LotEndInfo_Slab(sStockId);	
			}

			/**
			 * 5.3 SLAB이송의 상차 실적을 송신
			 */
			if (sPutLoc.startsWith(YmCommonConst.YD_GP_2)
					&& sProgCd.equals(YmCommonConst.CURR_PROG_CD_SLAB_C)) {

				sendSlabLoadResult(sStockId, sPutLoc, YmCommonConst.CAR_GP_D);
			}

			/**
			 * 5. SLAB 공통 TABLE 수정
			 */

			iReq = dao.updateSlabCommonLocInfo(sStockId, sPutLoc);

			/*
			 * 저장품 예상PUT 위치 CLEAR 저장품지정조회화면에서 예약상태로 남아있는것을 방지하기 위해
			 */
			{
				JDTORecord stockRc = dao.getStockInfo(sStockId);
				if (stockRc != null) {
					String sCarunloadBay = StringHelper.evl(stockRc.getFieldString("CARUNLOAD_PUT_LOC"), "");// 하차PUT위치
					if (sPutLoc.equals(sCarunloadBay)) {
						iReq = dao.updateStockPutLocWithStockId(sStockId, "");
					}
					logger.println(LogLevel.DEBUG, this, "저장품 예상PUT 위치 CLEAR A=" + sCarunloadBay);
					logger.println(LogLevel.DEBUG, this, "저장품 예상PUT 위치 CLEAR B=" + sPutLoc);
				}
			}

			/**
			 * 6. 저장품이동조건 수정
			 */
			iReq = dao.updateStockTransInfo(sStockId, sStocMv);

			/**
			 * 7. Crane 작업 실적 등록
			 */
			 				     
			iReq = insertUpPutWrslRtData(sStockId, sUpLoc, sPutLoc, sCurSchCode, sPutYardGp, sUserId, sBTcGbn, sBCraneNo, sBSchCd);

			/**
			 * 8. SLAB 이적실적 (출하로 이적실적 송신 YMDM009) 
			 * 공통 진도 Code가 1 - 생산예정, A - 수입검사대기 이면 출하로 "이적실적 송신"
			 */

			if (sProgCd.equals(YmCommonConst.CURR_PROG_CD_SLAB_1)
					|| sProgCd.equals(YmCommonConst.CURR_PROG_CD_SLAB_A)) {

				isSuccess = SendYMDM009(sPutYardGp, sStockId.trim(), sUpLoc.trim(), sPutLoc.trim());
				
			}

			/**
			 * 9. YARD MAP 정보 실적 등록
			 */
//			if (sPutLoc.startsWith(YmCommonConst.YD_GP_2) || sPutLoc.startsWith(YmCommonConst.YD_GP_0)) {
				sMsg = YmCommonUtil.setBSlabMapMsgInfo(sPutLoc);

				EJBConnector ejbConn = new EJBConnector("default", "JNDICraneStatusReg", this);
				Boolean isSucf = (Boolean) ejbConn.trx("bsyYdMapInfoPI", new Class[] { String.class }, new Object[] { sMsg });
//			}
				//A열연 SLAB 야드 추가 (MCH)	
/*			}else if (sPutLoc.startsWith(YmCommonConst.YD_GP_0)) {
				String sMsg = YmCommonUtil.setBSlabMapMsgInfo(sPutLoc);

				EJBConnector ejbConn = new EJBConnector("default", "JNDICraneStatusReg", this);
				Boolean isSucf = (Boolean) ejbConn.trx("bsyYdMapInfo", new Class[] { String.class }, new Object[] { sMsg });
			}
			*/
			
			/**
			 * 일관제철 B-CAST 산적위치 수정에 따른 차량재료정보 동기화 처리 
			 */
			if(	"0APT01".equals(sPutStackColGp)||
				"0APT02".equals(sPutStackColGp)||
				"0BPT01".equals(sPutStackColGp)||
				"0BPT02".equals(sPutStackColGp)){
				
				int iCar = dao.insertCarMtlInfo(sPutStackColGp,sPutStackBedGp,sPutStackLayerGp,sStockId);
			}
			
			isSuccess = true;
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return isSuccess;
	}
	
	private boolean callL2LotEndInfo_Slab(String sStockId){
		
		Boolean isSuccess = new Boolean(false);
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			ymCommonDAO ymDao 	= ymCommonDAO.getInstance();
			JDTORecord slabInfo = ymDao.readZoneInStocks_Lot(sStockId);
			String sSendMsg 	= YmCommonUtil.getSlabMsgInfo(slabInfo,YmCommonConst.FORM_R);
			   
		    EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
			isSuccess = (Boolean)ejbConn.trx("sendCM1BP02", new Class[]{String.class},
													     	new Object[]{sSendMsg});
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return isSuccess.booleanValue();
	}
		
	private void fillZeroSpace(StringBuffer buffer, int cnt) {
		for(int i = 0; i < cnt; i++) {
		    buffer.append("0");
		}
	}
	private void fillSpace(StringBuffer buffer, int cnt) {
		for(int i = 0; i < cnt; i++) {
		    buffer.append(" ");
		}
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
	 * 스케쥴 실패시 실패원인 로그기록
	 * 
	 * @param String :
	 *            저장품ID
	 * @param String :
	 *            주/보조작업구분
	 * @param String :
	 *            스케쥴코드
	 * @param String :
	 *            메세지
	 * 
	 */
	private void setToLocFailLog(String sStockId, String sGbn, String sSchCode,
			String sErrorMsg) {

		String sMsg = "저장품=" + sStockId + "[" + sGbn + "/" + sSchCode + "]=>" + sErrorMsg;

		EJBConnector ejbCon = null;
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return ;
			}
			
			ejbCon = new EJBConnector("default", "JNDIYMLog", this);
			ejbCon.trx("createLog", new Class[] { String.class, String.class }, new Object[] { "POYM001", sMsg });
		} catch (Exception e) {
		}
	}

	/**
	 * 오퍼레이션명 : 
	 *
	 *  스케쥴 처리할수 없는 LEGACY 작업예약 삭제기능
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                      
	public void clearStockWbookInfo(String sWbookId) {

		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return ;
		}
		
		String CoilNo = "";

		/**
		 * 1. 작업예약 TABLE CLEAR
		 */
		int iReq = dao.deleteWbookInfo(sWbookId);

		JDTORecord sJr = dao.getStockWbookId(sWbookId);
		if (sJr != null) {
			CoilNo = StringHelper.evl(sJr.getFieldString("STOCK_ID"), "");
		}
		/**
		 * 2. 저장품 TABLE CLEAR
		 */
		iReq = dao.updateStockWbookId(CoilNo, "");

		/**
		 * 3. MAP TABLE CLEAR
		 */
		JDTORecord stackV = dao.getStackLayerInfoWithStockId_02(CoilNo);

		if (stackV != null) {
			String sStackColGp = StringHelper.evl(stackV
					.getFieldString("STACK_COL_GP"), "");
			String sStackBedGp = StringHelper.evl(stackV
					.getFieldString("STACK_BED_GP"), "");
			String sStackLayerGp = StringHelper.evl(stackV
					.getFieldString("STACK_LAYER_GP"), "");

			iReq = dao.updateCraneStackLayerStat(sStackColGp, sStackBedGp,
					sStackLayerGp, CoilNo, YmCommonConst.STACK_LAYER_STAT_L);
		}
	}

	/**
	 * 산적위치 수정 화면에서 From 위치와 To 위치 정보를 실적 처리한다.
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param String :
	 *            저장품ID
	 * @param String :
	 *            UP LOC
	 * @param String :
	 *            PUT LOC
	 * @param String :
	 *            스케쥴 코드
	 * @param String :
	 *            야드구분
	 * 
	 */
	public int insertUpPutWrslRtData(String sStockId, 
							     String sUpLoc,
							     String sPutLoc, 
							     String sSchCode, 
							     String sYdGp,
							     String sUserId){
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return 0;
		}
		
		return insertUpPutWrslRtData(sStockId, 
							      sUpLoc,
							      sPutLoc, 
							      sSchCode, 
							      sYdGp,
							      sUserId,
							      "",
							      "",
							      "");
	} 
	private int insertUpPutWrslRtData(String sStockId, 
							     String sUpLoc,
							     String sPutLoc, 
							     String sSchCode, 
							     String sYdGp,
							     String sUserId,
							     String sBTcGbn,		//비상조업처리구분(U:L2시스템,B:L2산적위치)
							     String sBCraneNo,		//비상조업크레인번호
							     String sBSchCd			//비상조업스케쥴코드
							     ) {
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return 0;
		}
		
		int iSeq = -1;

		YdWBookDAO ydWBookDAO = new YdWBookDAO();

		try {
			String scrane_sch_id = "";
			String scrane_stock_id = "";
			String scrane_equip_gp = "";
			String scrane_sch_code = "";
			String scrane_up_loc = "";
			String scrane_put_loc = "";
			String scrane_up_func = "";
			String scrane_put_func = "";
			String scrane_register = "";
			String scrane_modifier = "";
			String scrane_yd_gp = "";
			String scrane_work_duty = "";
			String scrane_work_party = "";
			String sch_wdemand_duty = "";
			String sch_wdemand_party = "";

			scrane_sch_id 	= "000000000000000000";
			scrane_stock_id 	= sStockId.trim();
			scrane_equip_gp 	= "";
			scrane_sch_code 	= "";
			scrane_up_loc 	= sUpLoc;
			scrane_put_loc 	= sPutLoc;
			scrane_up_func 	= YmCommonConst.CRANE_FUNC_S;
			scrane_put_func 	= YmCommonConst.CRANE_FUNC_S;
			scrane_register 	= sUserId;
			scrane_modifier 	= sUserId;
			scrane_yd_gp 		= sYdGp;

			String sUpBay = sUpLoc.length() > 2 ? sUpLoc.substring(1, 2) : "";
			String sPutBay = sPutLoc.length() > 2 ? sPutLoc.substring(1, 2)
					: "";

			if ("".equals(sSchCode)) {
				
				//2007-04-06 A열연 SLAB야드 추가 (MCH) 
				if(YmCommonConst.YD_GP_0.equals(sYdGp)){
					scrane_sch_code = YmCommonConst.NEW_SCH_WORK_KIND_SYMM;
				}
				
				if (sUpBay.equals(sPutBay)) {
					if (YmCommonConst.YD_GP_1.equals(sYdGp)
							|| YmCommonConst.YD_GP_3.equals(sYdGp)) {
						scrane_sch_code = YmCommonConst.NEW_SCH_WORK_KIND_CYMM;
					} else if (YmCommonConst.YD_GP_2.equals(sYdGp)
							|| YmCommonConst.YD_GP_4.equals(sYdGp)) {
						scrane_sch_code = YmCommonConst.NEW_SCH_WORK_KIND_SYMM;
					}
				} else {
					if (YmCommonConst.YD_GP_1.equals(sYdGp)
							|| YmCommonConst.YD_GP_3.equals(sYdGp)) {
						scrane_sch_code = YmCommonConst.NEW_SCH_WORK_KIND_CTML;
					} else if (YmCommonConst.YD_GP_2.equals(sYdGp)
							|| YmCommonConst.YD_GP_4.equals(sYdGp)) {
						scrane_sch_code = YmCommonConst.NEW_SCH_WORK_KIND_STML;
						//scrane_sch_code = YmCommonConst.NEW_SCH_WORK_KIND_STM2;
					}
				}
			} else {
				scrane_sch_code = sSchCode;
			}

			scrane_equip_gp = sYdGp + sPutBay + YmCommonConst.EQUIP_KIND_CR + "00";

			scrane_work_duty = YmCommonUtil.getWorkDuty();
			scrane_work_party = YmCommonUtil.getWorkParty();
			sch_wdemand_duty = YmCommonUtil.getWorkDuty();
			sch_wdemand_party = YmCommonUtil.getWorkParty();
			
			/*
			 *	L2 비상조업관련 실적 백업처리
			 */
			if("U".equals(sBTcGbn)){
				
				scrane_up_func 	= YmCommonConst.CRANE_FUNC_U;
				scrane_put_func 	= YmCommonConst.CRANE_FUNC_U;
				
				scrane_sch_code	= sBSchCd;
				scrane_equip_gp	= sBCraneNo;
				
			}else if("B".equals(sBTcGbn)){
				
				scrane_up_func 	= YmCommonConst.CRANE_FUNC_B;
				scrane_put_func 	= YmCommonConst.CRANE_FUNC_B;
			}
			
			/*
			 * INSERT INTO TB_YM_WRSLT ( CRANE_WRSLT_ID, SCH_ID, STOCK_ID,
			 * EQUIP_GP, SCH_WORK_KIND, CRANE_WORK_DDTT, CRANE_WORK_DUTY,
			 * CRANE_WORK_PARTY, CRANE_WORD_DDTT, CRANE_WRSLT_CD, SCH_WPREFER,
			 * SCH_WDEMAND_DDTT, SCH_WDEMAND_DUTY, SCH_WDEMAND_PARTY,
			 * CRANE_WORD_UP_LOC, CRANE_WORD_PUT_LOC, CRANE_WRSLT_UP_LOC,
			 * CRANE_WRSLT_UP_FUNC, CRANE_WRSLT_UP_DDTT, CRANE_WRSLT_PUT_LOC,
			 * CRANE_WRSLT_PUT_FUNC, CRANE_WRSLT_PUT_DDTT, REGISTER, REG_DDTT,
			 * MODIFIER, MOD_DDTT, DEL_YN, YD_GP) VALUES
			 * (to_char(sysdate,'YYYYMMDDHH24MI')||YM_WRSLT_SEQ.nextval,
			 * :sch_id, :stock_id, :equip_gp, :sch_work_kind,
			 * to_char(sysdate,'YYYYMMDDHH24MISS'), :scrane_work_duty,
			 * :scrane_work_party, to_char(sysdate,'YYYYMMDDHH24MISS'), 'N',
			 * '1', to_char(sysdate,'YYYYMMDDHH24MISS'), :sch_wdemand_duty,
			 * :sch_wdemand_party, :up_loc, :put_loc, :up_loc, :up_func,
			 * to_char(sysdate,'YYYYMMDDHH24MISS'), :put_loc, :put_func,
			 * to_char(sysdate,'YYYYMMDDHH24MISS'), :register, sysdate,
			 * :modifier, sysdate, 'N', :yd_gp)
			 */

			String queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.insertCraneWrsltSanJuk";
			iSeq = ydWBookDAO.requestinsertData(queryCode, new Object[] {
					scrane_sch_id, scrane_stock_id, scrane_equip_gp,
					scrane_sch_code, scrane_work_duty, scrane_work_party,
					sch_wdemand_duty, sch_wdemand_party, scrane_up_loc,
					scrane_put_loc, scrane_up_loc, scrane_up_func,
					scrane_put_loc, scrane_put_func, scrane_register,
					scrane_modifier, scrane_yd_gp });

		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return iSeq;
	}

	/**
	 * 오퍼레이션명 : 
	 *
	 * 분기 conveyor 상에 있는 Coil의 작업 순서 조정하는 메소드. 만약 Coil이 존재하지 않는다면 저장품을 생성하고
	 * 처리한다.
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                      	 
	public boolean callCoveyorMoveProcess(String sFStockId, String sToBay,
			String sToBed) {
		boolean isSuccess = false;

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String sTStockId = "";
			String sTStackColGp = "";
			String sTStackBedGp = sToBed;
			String sTStackLayerGp = YmCommonConst.STACK_LAYER_GP_01;

			if (sToBay.equals(YmCommonConst.BAY_GP_B)) {
				sTStackColGp = YmCommonConst.STACK_COL_GP_1BDC01;
			} else if (sToBay.equals(YmCommonConst.BAY_GP_C)) {
				sTStackColGp = YmCommonConst.STACK_COL_GP_1CDC01;
			}
			logger.println(LogLevel.DEBUG, this, "Coil No= " + sFStockId
					+ " / To동=" + sToBay + " / To번지=" + sToBed);

			String sFStackColGp = "";
			String sFStackBedGp = "";
			String sFStackLayerGp = "";

			JDTORecord stackV = dao.getStackLayerInfoWithStockId_02(sFStockId);

			if (stackV != null) {
				sFStackColGp = StringHelper.evl(stackV
						.getFieldString("STACK_COL_GP"), "");
				sFStackBedGp = StringHelper.evl(stackV
						.getFieldString("STACK_BED_GP"), "");
				sFStackLayerGp = StringHelper.evl(stackV
						.getFieldString("STACK_LAYER_GP"), "");
			}

			if (!YmCommonConst.STACK_COL_GP_1BDC01.equals(sFStackColGp)
					&& !YmCommonConst.STACK_COL_GP_1CDC01.equals(sFStackColGp)) {

				throw new EJBServiceException("=작업순서조정=> 저장품이 설비에 존재하지 않음.");
			}

			JDTORecord layerV = dao.getStackLayerInfoWithPk(sTStackColGp,
					sTStackBedGp, sTStackLayerGp);
			if (layerV != null) {
				sTStockId = StringHelper.evl(layerV.getFieldString("STOCK_ID"),
						"");
			}
			int iReq = 0;
			{
				iReq = dao.updateCraneStackLayerStat(sFStackColGp,
						sFStackBedGp, sFStackLayerGp, sTStockId,
						YmCommonConst.STACK_LAYER_STAT_S);
			}

			{
				iReq = dao.updateCraneStackLayerStat(sTStackColGp,
						sTStackBedGp, sTStackLayerGp, sFStockId,
						YmCommonConst.STACK_LAYER_STAT_S);
			}

			isSuccess = true;
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return isSuccess;
	}

	/**
	 * 오퍼레이션명 : 
	 *
	 * 분기 Conveyor 작업예약을 취소 한상태에서 다시 작업요구를 하는 메소드
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                      	  
	public boolean callCoveyorWorkProcess(String StockId) {
		Boolean isSuccess = new Boolean(false);

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String[] sStockInfo = YmCommonUtil.getCoilCurrProgCd(StockId,"");
			String sProgCd = sStockInfo[0];
			String sStocMv = sStockInfo[1];

			/**
			 * 압연실적 작업예약 모듈을 호출한다.
			 */
			EJBConnector ejbConn = new EJBConnector("default","JNDICoilInfoReg", this);
			isSuccess = (Boolean) ejbConn.trx("callCoilWbookInfo", new Class[] {String.class, String.class }, new Object[] { StockId, sStocMv });

		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return isSuccess.booleanValue();
	}

	/**
	 * 오퍼레이션명 : 
	 *
	 * Coil 이적실적 (출하로 이적실적 송신 YMDM008) 공통 진도 Code가 출하작업지시대기, 제품충당대기, 출하작업대기,
	 * 보관매출이면 출하로 "이적실적 송신"
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */        
	public boolean SendYMDM008(String YardGp, String StockId, String UpPosition, String PutPosition) {
		Boolean isSuccess = new Boolean(false);

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String[] sStockInfo = YmCommonUtil.getCoilCurrProgCd(StockId,"");
			String sProgCd = sStockInfo[0];
			String sStocMv = sStockInfo[1];

		    //AB열연  ##############################################################################################	
			
//			YMDM008 model = new YMDM008();
//			model.setTcCode(YmCommonConst.MODEL_YMDM008);
//			model.setTcDate(YmCommonUtil.getCurDate("yyyy-MM-dd"));
//			model.setTcTime(YmCommonUtil.getCurDate("HH-mm-ss"));
//
//			/** 이적 일자 */
//			model.setMOVENSTACK_DATE(YmCommonUtil.getCurDate("yyyyMMdd"));
//
//			/** 이적 시각 */
//			model.setMOVENSTACK_TIME(YmCommonUtil.getCurDate("HHmmss"));
//
//			/** 코일 번호 */
//			model.setGOODS_NO(StockId);
//
//			/** FROM 저장 위치 1F02030901 */
//			model.setBEFO_STORE_LOC(UpPosition);
//
//			/** TO 저장 위치 1F02030901 */
//			model.setSTORE_LOC(PutPosition);
//
//			EJBConnector ejbConn = new EJBConnector("default", "JNDIYardWrkResReg", this);
//			isSuccess = (Boolean) ejbConn.trx("sendInternalModel", new Class[] { CommonModel.class }, new Object[] { model });
//			logger.println(LogLevel.DEBUG, this, "산적위치 수정===Coil을 제품 야드에서 동내, 동간 이적 처리시.===");
			
		    //일관제철 ##############################################################################################	
			logger.println(LogLevel.DEBUG, this, "산적위치 수정===Coil을 제품 야드에서 동내, 동간 이적 처리시.===");
            
   		    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            //코일제품이적작업실적
 			JDTORecord tcRecordDM = null;
 			tcRecordDM = JDTORecordFactory.getInstance().create(); 
 			tcRecordDM.setField("GOODS_NO",StockId);
 			tcRecordDM.setField("BEFO_STORE_LOC",UpPosition);
 			tcRecordDM.setField("TO_STORE_LOC",PutPosition);
 			
 			//인터페이스 전문 호출
 			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
 			isSuccess = (Boolean)ejbConn.trx("getYDDMR004",new Class[]{JDTORecord.class},
 			  	  	 new Object[]{tcRecordDM}); 
            logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 코일제품이적작업실적.===");
            //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            

		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return isSuccess.booleanValue();
	}

	/**
	 * 오퍼레이션명 : 
	 *
	 * Coil 입고실적 (출하로 제품입고실적 송신 YMDM001) 공통 진도 Code가 입고대기이면 출하로 "이적실적 송신"
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */         
	public boolean SendYMDM001(String YardGp, String StockId, String PutPosition) {
		Boolean isSuccess = new Boolean(false);

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			String[] sStockInfo = YmCommonUtil.getCoilCurrProgCd(StockId,"");
			String sProgCd = sStockInfo[0];
			String sStocMv = sStockInfo[1];
			

		    //AB열연  ##############################################################################################
//			YMDM001 model = new YMDM001();
//			model.setTcCode(YmCommonConst.MODEL_YMDM001);
//			model.setTcDate(YmCommonUtil.getCurDate("yyyy-MM-dd"));
//			model.setTcTime(YmCommonUtil.getCurDate("HH-mm-ss"));
//
//			//입고 일자
//			model.setRECEIPT_DATE(YmCommonUtil.getCurDate("yyyyMMdd"));
//
//			//입고 시각
//			model.setRECEIPT_TIME(YmCommonUtil.getCurDate("HHmmss"));
//
//			//YARD 구분 1:ACoil, 2:BSlab, 3:BCoil
//			model.setYD_GP(YardGp);
//
//			// 제품 번호 
//			model.setGOODS_NO(StockId);
//
//			// 저장 위치 1F02030901 
//			model.setSTORE_LOC(PutPosition);
//
//			EJBConnector ejbConn = new EJBConnector("default", "JNDIYardWrkResReg", this);
//			isSuccess = (Boolean) ejbConn.trx("sendInternalModel", new Class[] { CommonModel.class }, new Object[] { model });
//			logger.println(LogLevel.DEBUG, this,"산적위치 수정===Coil을 제품 야드로 입고시.===");

			//일관제철 ##############################################################################################
				
 	            
		 //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
           //임가공입고작업실적
			JDTORecord tcRecordDM = null;
			tcRecordDM = JDTORecordFactory.getInstance().create(); 
			tcRecordDM.setField("GOODS_NO",StockId);
			tcRecordDM.setField("YD_GP",YardGp);
			tcRecordDM.setField("STORE_LOC",PutPosition);
			
			//인터페이스 전문 호출
			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
			isSuccess = (Boolean)ejbConn.trx("getYDDMR003",new Class[]{JDTORecord.class},
			  	  	 new Object[]{tcRecordDM}); 
           logger.println(LogLevel.DEBUG,this, "내부IF호출=== 일관제철 코일 임가공입고작업실적.===");
          //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
           
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return isSuccess.booleanValue();
	}
	

	/**
	 * 오퍼레이션명 : 
	 *
	 * TO위치가 B열연이고, 공통 진도 Code가 이송 작업대기 E 이면 조업으로 이송실적송신
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */          
	public boolean SendYMPO159(String sStockId, String sUpDown, String sPut_Position) {
		Boolean isSuccess = new Boolean(false);

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			YMPO159 model = new YMPO159();
			model.setTcCode(YmCommonConst.MODEL_YMPO159);
			model.setTcDate(YmCommonUtil.getCurDate("yyyy-MM-dd"));
			model.setTcTime(YmCommonUtil.getCurDate("HH-mm-ss"));

			/* 상하차처리일자 CHAR(8) yyyymmdd */
			model.setupDownDate(YmCommonUtil.getCurDate("yyyyMMdd"));

			/* COIL번호 CHAR(11) */
			model.setcoilNo(sStockId);

			/* 상하차구분 CHAR(1) U:상차, D:하차 */
			model.setupDownGbn(sUpDown);

			/* 상하차위치 CHAR(10) 상차,하차 위치 */
			model.setupDownLoc(sPut_Position);

			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg", this);
			isSuccess = (Boolean) ejbConn.trx("sendInternalModel", new Class[] { CommonModel.class }, new Object[] { model });
			logger.println(LogLevel.DEBUG, this,"내부IF호출===Coil 이송 상/하차 완료 시.===");

		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return isSuccess.booleanValue();
	}

	/**
	 * 오퍼레이션명 : 
	 *
	 * 8. 조업 SPM, HFL 입측에 권하시 발생.
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */           
	public boolean SendYMPO161(String sStockId, String sYdGp, String sProc, String sPosition) {
		Boolean isSuccess = new Boolean(false);

		try {

			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			YMPO161 model = new YMPO161();
			model.setTcCode(YmCommonConst.MODEL_YMPO161);
			model.setTcDate(YmCommonUtil.getCurDate("yyyy-MM-dd"));
			model.setTcTime(YmCommonUtil.getCurDate("HH-mm-ss"));

			/* 권하일자 CHAR(8) yyyymmdd */
			model.setdownDate(YmCommonUtil.getCurDate("yyyyMMdd"));

			/* 권하시각 CHAR(6) HHMMSS */
			model.setdownTime(YmCommonUtil.getCurDate("HHmmss"));

			/* 공장구분 CHAR(1) A:A열연, B:B열연 */
			model.setplantGbn(YmCommonConst.YD_GP_1.equals(sYdGp) ? "A" : "B");

			/* 공정구분 CHAR(1) H : Hot Filnal, S : SkinPass */
			model.setprocGbn(sProc);

			/* COIL번호 CHAR(11) */
			model.setcoilNo(sStockId);

			/* 처리구분 CHAR(1) 1:보급,2:보급취소,3:추출,4:Take-Out,5:Take-In */
			model.setProcessId(sPosition);

			/* 위치포지션 CHAR(2) */
			model.setpositionNo("5".equals(sPosition) ? YmCommonConst.PO_POSITION_D5 : YmCommonConst.PO_POSITION_D1);

			EJBConnector ejbConn = new EJBConnector("default", "JNDIYardWrkResReg", this);
			isSuccess = (Boolean) ejbConn.trx("sendInternalModel", new Class[] { CommonModel.class }, new Object[] { model });
			logger.println(LogLevel.DEBUG, this, "내부IF호출===SPM, HFL 입측에 권하시 발생.===");
			
			
			
			//품질 열연정정입측보급실적----------------------------------------------
			YdDelegate      ydDelegate      = new YdDelegate();
			JDTORecord recInTemp	=null;
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("MSG_ID",      	"YDQMJ002"); 							 
			recInTemp.setField("STL_NO",     	sStockId);	  			//재료번호				
			ydDelegate.sendMsg(recInTemp);
	 
			logger.println(LogLevel.DEBUG,this, "품질 L3 열연정정입측보급실적 전송 송신 완료8"); 
			//-------------------------------------------------------------------

		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return isSuccess.booleanValue();
	}

	/**
	 * 오퍼레이션명 : 
	 *
	 * 8. SLAB 이적실적 (출하로 이적실적 송신 YMDM009) 공통 진도 Code가 1 - 생산예정, A - 수입검사대기 이면
	 * 출하로 "이적실적 송신"
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */            
	public boolean SendYMDM009(String YardGp, String StockId,
			String UpPosition, String PutPosition) {
		Boolean isSuccess = new Boolean(false);

		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			YMDM009 model = new YMDM009();
			model.setTcCode(YmCommonConst.MODEL_YMDM009);
			model.setTcDate(YmCommonUtil.getStringYMD("-"));
			model.setTcTime(YmCommonUtil.getStringHMS("-"));
			model.setSLAB_NO(StockId);
			model.setRECEIPT_DATE(YmCommonUtil.getStringYMD());// 입고일자
			model.setRECEIPT_TIME(YmCommonUtil.getStringHMS());// 입고시간
			model.setCURR_PROG_CD("1");
			model.setYD_GP(PutPosition.substring(0, 1));
			model.setBAY(PutPosition.substring(1, 2));
			model.setSPAN(PutPosition.substring(2, 4));
			model.setCOL(PutPosition.substring(4, 6));
			model.setCELLNO(PutPosition.substring(6, 8));
			model.setSTACK_LAYER(PutPosition.substring(8, 10));
			model.setSTORE_LOC_CD(PutPosition);

			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg", this);
			isSuccess = (Boolean) ejbConn.trx("sendInternalModel", new Class[] { CommonModel.class }, new Object[] { model });
			logger.println(LogLevel.DEBUG, this,"산적위치 수정===SLAB 제품 야드에서 동내, 동간 이적 처리시.===");

		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return isSuccess.booleanValue();
	}

	/**
	 * JDTORecord 가 가지는 name parameter에 대한 값이 공백이거나 null일 경우 공백을 리턴한다.
	 * 
	 * @param data
	 * @param name
	 * @param len
	 *            공백 수
	 * @return
	 */
	private String getField(JDTORecord data, String name, int len) {
		if ("".equals(StringHelper.evl(data.getFieldString(name), ""))) {
			return space(len);
		}
		return data.getFieldString(name);
	}

	/**
	 * 공백을 cnt 만큼 리턴한다.
	 * 
	 * @param cnt
	 *            공백 수
	 * @return
	 */
	private String space(int cnt) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < cnt; i++) {
			buffer.append(" ");
		}
		return buffer.toString();
	}

	/**
	 * JDTORecord 가 가지는 name parameter에 대한 값을 리턴한다.
	 * 
	 * @param data
	 * @param name
	 * @return
	 */
	private String getField(JDTORecord data, String name) {
		if (data != null) {
			return StringHelper.evl(data.getFieldString(name), "");
		} else {
			logger.println(LogLevel.DEBUG, this, "##### JDTORecord is NULL.");
			return null;
		}
	}

	/**
	 * JDTORecord 가 가지는 name parameter에 대한 값을 리턴한다.
	 * 
	 * @param data
	 * @param name
	 * @return
	 */
	private String getFieldNvl(JDTORecord data, String name) {
		if (data != null) {
			return StringHelper.nvl(data.getFieldString(name), "");
		} else {
			logger.println(LogLevel.DEBUG, this, "##### JDTORecord is NULL.");
			return null;
		}
	}

	/**
	 * JDTORecord 가 가지는 name parameter에 대한 값을 float type으로 리턴한다.
	 * 
	 * @param data
	 * @param name
	 * @return
	 */
	private float getFieldFloat(JDTORecord data, String name) {
		if (data != null) {
			return StringHelper.parseFloat(data.getFieldString(name), 0.0f);
		} else {
			logger.println(LogLevel.DEBUG, this, "##### JDTORecord is NULL.");
			return 0;
		}
	}

	/**
	 * JDTORecord 가 가지는 name parameter에 대한 값을 int type으로 리턴한다.
	 * 
	 * @param data
	 * @param name
	 * @return
	 */
	private int getFieldInt(JDTORecord data, String name) {
		if (data != null) {
			return StringHelper.parseInt(data.getFieldString(name), 0);
		} else {
			logger.println(LogLevel.DEBUG, this, "##### JDTORecord is NULL.");
			return 0;
		}
	}

	/**
	 * name parameter에 대한 값을 반환한다.
	 * 
	 * @param data
	 * @param name
	 * @return
	 */
	private int getFieldLen(Map data, String name) {
		if (data != null) {
			return StringHelper.parseInt((String) data.get(name), 0);
		} else {
			logger.println(LogLevel.DEBUG, this, "##### Map is NULL.");
			return 0;
		}
	}

	/**
	 * 오퍼레이션명 : 
	 *
	 * 적치발란스 기준을 가져온다.
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */             
	public List getListBlncBas(String queryID, List listData)
			throws EJBServiceException, DAOException {
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			StockingBlncBasDAO stockingblncbasDAO = new StockingBlncBasDAO();
			return stockingblncbasDAO.getListData(queryID, listData);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 적치발란스 기준을 가져온다.
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */             
	public JDTORecord getBlncBas(String queryID, List listData) throws EJBServiceException, DAOException {
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			StockingBlncBasDAO stockingblncbasDAO = new StockingBlncBasDAO();
			return stockingblncbasDAO.getData(queryID, listData);
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 2007-04-06 A열연 SLAB야드 (MCH)
	 * 상차지시 편성(산적위치 수정 포함)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */             
	public boolean getListLoadDirect(List sStock_id, 		//저장품 ID
									 List Slab_layerList,	//저장품layer //stack_col_gp
									 String to_pos) throws EJBServiceException, DAOException {
		boolean isSuccess = false;
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			for (int i = 0; i < sStock_id.size(); i++) {
				//현재위치 가져오기
				/*
				SELECT A.YD_GP AS 야드,
				       A.STOCK_ID,
				       ADDR AS 현재위치,
				       DECODE(ADDR, NULL, '없음', '적치') AS 적치상태,
				       NVL(B.SCH_WORK_KIND, ' ') AS SCH, 
				       DECODE(A.STOCK_ITEM, 'SM', D.CURR_PROG_CD,
				                            'CM', C.CURR_PROG_CD,
				                            'CG', C.CURR_PROG_CD,
				                            '-') AS 진도코드,
				       A.STOCK_MOVE_TERM AS 이동조건
				FROM (        
				       SELECT A.STOCK_ID, A.STOCK_ITEM, A.STOCK_MOVE_TERM,
				              NVL(A.WBOOK_ID, '20060101000000') AS WBOOK_ID,
				              NVL(SUBSTR(B.STACK_COL_GP, 1, 1), '-') AS YD_GP,
				              NVL(B.STACK_COL_GP, '-') AS COL,
				              NVL(B.STACK_BED_GP, '-') AS BED,
				              NVL(B.STACK_LAYER_GP, '-') AS LAYER,
				              B.STACK_COL_GP||B.STACK_BED_GP||B.STACK_LAYER_GP AS ADDR
				       FROM TB_YM_STOCK A, TB_YM_STACKLAYER B
				       WHERE A.STOCK_ID = B.STOCK_ID(+)
				         AND A.STOCK_ID = ? --저장품번호(HB00001)
				     ) A, TB_YM_WBOOK B, USRPMA.TB_PM_COILCOMM C, USRPMA.TB_PM_SLABCOMM D
				WHERE A.WBOOK_ID = B.WBOOK_ID(+)
				  AND A.STOCK_ID = C.COIL_NO(+)
				  AND A.STOCK_ID = D.SLAB_NO(+)
				*/
				String Stock_id = (String) sStock_id.get(i);
				String layer = (String)Slab_layerList.get(i);
				String query = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.getStockLoc";
				JDTORecord StockRecord = new YdStockDAO().getData(query, new Object[]{Stock_id});
				
				if(StockRecord == null) throw new DAOException(Stock_id + "저장품은 없는 저장품입니다.");
				
				String f_addr 			= StringHelper.evl(StockRecord.getFieldString("현재위치"),"");
				String SCH 				= StringHelper.evl(StockRecord.getFieldString("SCH"),"");
				String STOCK_ITEM 		= StringHelper.evl(StockRecord.getFieldString("진도코드"),"");
				String STOCK_MOVE_TERM 	= StringHelper.evl(StockRecord.getFieldString("이동조건"),"");
				String YD_GP 			= StringHelper.evl(StockRecord.getFieldString("야드"),"");
				String StockState 		= StringHelper.evl(StockRecord.getFieldString("적치상태"),"");
				
				/* To 위치 검색
				SELECT *
				FROM tb_ym_stacklayer
				WHERE stack_col_gp  	= :stack_col_gp
				AND   stack_bed_gp		= :stack_bed_gp 
				AND   stack_layer_gp	= :stack_layer_gp 	
				*/
				String query1  = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerInfoWithPk";
				JDTORecord StockLocRecord = new YdStockDAO().getData(query1, new Object[]{to_pos,
																						"01",
																						layer});
				
				String sToStockId 	= StringHelper.evl(StockLocRecord.getFieldString("STOCK_ID"), "");
				String sToStat 		= StringHelper.evl(StockLocRecord.getFieldString("STACK_LAYER_STAT"), "");
				String sToAtiveStat	= StringHelper.evl(StockLocRecord.getFieldString("STACK_LAYER_ACTIVE_STAT"), "");
				
				if(!"".equals(sToStockId)){ 
					
					if(YmCommonConst.STACK_LAYER_STAT_P.equals(sToStat)){ 
						throw new DAOException("재료번호"+sToStockId+"가 "+to_pos+"01"+layer+"위치에 권하예약 스케쥴이 존재합니다.");
					}
					
					if(YmCommonConst.STACK_LAYER_STAT_L.equals(sToStat)||
					   YmCommonConst.STACK_LAYER_STAT_S.equals(sToStat)||
					   YmCommonConst.STACK_LAYER_STAT_U.equals(sToStat)){ 
						throw new DAOException("재료번호"+sToStockId+"가 "+to_pos+"01"+layer+"위치에 존재합니다.");
					}
				}	
				
				if(YmCommonConst.STACK_LAYER_ACTIVE_STAT_C.equals(sToAtiveStat)||
				   "X".equals(sToAtiveStat)){ 
					throw new DAOException(to_pos+"01"+layer+"위치는 CLOSE로 설정되어 있습니다.");
				}
				 
				if(YmCommonConst.STACK_LAYER_STAT_X.equals(sToStat)||
				   YmCommonConst.STACK_LAYER_STAT_V.equals(sToStat)){ 
					throw new DAOException(to_pos+"01"+layer+"위치는 적치불가 "+sToStat+" 위치입니다.");
				}
				
				//기존에 산적위치 수정 사용
				isSuccess =  changeSlabLocationInfo(Stock_id,				//저장품ID 
													f_addr,					//FROM LOC
													to_pos+"01"+layer,		//TO LOC 
													"R");					//상단(U)/교체(R) 구분
			}
			
			logger.println(LogLevel.DEBUG, this, "공정으로 이송차량 상차 완료 JMS 전송 시작");
			isSuccess = SendYMPM002(to_pos);
			
		}catch(DAOException daoe){
			throw new DAOException(daoe);
		}catch(Exception ie){
			throw new EJBServiceException(ie);
		}
		return isSuccess;
	}

	/**
	 * 오퍼레이션명 : 
	 *
	 * PDA Pallet 에서 산적 위치 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
	public boolean changePalletLocationInfo(String bay_gp, 
									  String Pallet1, 
									  String Pallet2) throws EJBServiceException, DAOException {
		return changePalletLocationInfo(bay_gp, 
								   Pallet1, 
								   Pallet2,
								   "SYSTEM");
	} 
	/**
	 * 오퍼레이션명 : 
	 *
	 * PDA Pallet 에서 산적 위치 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */     
	public boolean changePalletLocationInfo(String bay_gp, 
									  String Pallet1, 
									  String Pallet2,
									  String sUserId) throws EJBServiceException, DAOException {
		boolean isSuccess = false;
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String query1	="ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.getListASlabPalletloc";
			List StockList	= new YdStockDAO().getListData(query1, new Object[]{"0"+bay_gp});
			String Pallet1List[] = Pallet1.split(",");
			String Pallet2List[] = Pallet2.split(",");
			
			JDTORecord jrecrd = null;
			
			int count = StockList.size()/2;
			
			for(int inx=0; inx < count; inx++){

				jrecrd = (JDTORecord)StockList.get(inx);

				if(!Pallet1List[inx].equals(StringHelper.evl(jrecrd.getFieldString("STOCK_ID"),"")) 
					&& !"".equals(StringHelper.evl(Pallet1List[inx],""))){

					
					isSuccess =  changeSlabLocationInfo(Pallet1List[inx],							//저장품ID 
												     jrecrd.getFieldString("FOPOS"),			//FROM LOC
												    "0"+bay_gp+"PT0101"+"0"+(count-inx),		//TO LOC 
												    "R",									//상단(U)/교체(R) 구분
												    sUserId);								//사용자	
					if(isSuccess == false){
						throw new DAOException("산적위치 수정중에 오류 발생");
					}
				}
			}
			for(int iny=0; iny < count; iny++){

				jrecrd = (JDTORecord)StockList.get(count+iny);
				
				if(!Pallet2List[iny].equals(StringHelper.evl(jrecrd.getFieldString("STOCK_ID"),""))
					&& !"".equals(StringHelper.evl(Pallet2List[iny],""))){
					
					isSuccess =  changeSlabLocationInfo(StringHelper.evl(Pallet2List[iny],""),					//저장품ID 
												      StringHelper.evl(jrecrd.getFieldString("FOPOS"),""),	//FROM LOC
												      "0"+bay_gp+"PT0201"+"0"+(count-iny),				//TO LOC 
												      "R",											//상단(U)/교체(R) 구분
												      sUserId);										//사용자	
					if(isSuccess == false){
						throw new DAOException("산적위치 수정중에 오류 발생");
					}
				}
			}
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return isSuccess;
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * YMPM002 공정으로 이송 상차 완료 JMS 전송
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */      
	public boolean SendYMPM002(String Position) {
		Boolean isSuccess = new Boolean(false);
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}

			ZZPM001 model = new ZZPM001();
			model.setTcCode(YmCommonConst.MODEL_YMPM002);
			model.setTcDate(YmCommonUtil.getStringYMD("-"));
			model.setTcTime(YmCommonUtil.getStringHMS("-"));
			model.setlocation_no(Position);
			
			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg", this);
			isSuccess = (Boolean) ejbConn.trx("sendInternalModel", new Class[] { CommonModel.class }, new Object[] { model });
			logger.println(LogLevel.DEBUG, this," 이송 상차 완료  공정의 이송 편성 요청 TC 전송 완료");

		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return isSuccess.booleanValue();
	}
	/*
	 * 최규성
	 * 신규대차상태를 확인한다.
	 * 조건:  
	 */

	private List checkNewTransferCarStat(String sWbookId){
		List listResult = null;
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			// 1. sWbookId(작업예약ID)로 스케줄코드, 코일번호, ProcessId, WorkId, YardId를 확인한다.
			String sQueryId_wbook = "ym.scheduling.crane.dao.YdSchRuleDAO.getListTcCoilInfoByWbook";
			List listData	= new YdStockDAO().getListData(sQueryId_wbook, new Object[]{sWbookId});
			
			if(listData.size() > 2 || listData.size() < 1) return listResult ;
			
			JDTORecord jtrData = (JDTORecord)listData.get(0);
		
			String sSchCode = jtrData.getFieldString("SCH_WORK_KIND");
			
			JDTORecord jtrEquipStat = null;
			String sStackColGp = "";
			String sEnableBay = "";
			String sSchCode_TC = "";
			// 스케줄 코드에 따라 관련된 대차상태를 확인한다.
			if(sSchCode.equals(YmCommonConst.NEW_SCH_WORK_KIND_CFLO)){						// HFL 추출
				// 신규대차#2와 HFL대차의 상태를 확인한다.
				// 대차정보 확인 : 현재동 C동, 상태 IDLE
				String sEquip = YmCommonConst.NEW_TC_3XTC04;
				sEnableBay = YmCommonConst.BAY_GP_C;
				sSchCode_TC = YmCommonConst.NEW_SCH_WORK_KIND_CTM6;		// 동간이적상차
				sStackColGp = sEquip+"0101";
				jtrEquipStat = dao.getToEquipState(sStackColGp);
				
			}else if( sSchCode.equals(YmCommonConst.NEW_SCH_WORK_KIND_CKLO) ){				// SPM1 추출
				// 신규대차#1과 HYSCO대차의 상태를 확인한다.
				// 대차정보 확인 : 현재동 A동, 상태 IDLE
				String sEquip = YmCommonConst.NEW_TC_3XTC03;
				sEnableBay = YmCommonConst.BAY_GP_A;
				sSchCode_TC = YmCommonConst.NEW_SCH_WORK_KIND_CTM5;		// 동간이적상차
				sStackColGp = sEquip+"0101";
				
				jtrEquipStat = dao.getToEquipState(sStackColGp);
			}else if( sSchCode.equals(YmCommonConst.NEW_SCH_WORK_KIND_CNLO)){				// SPM2 추출
				// 신규대차#3의 상태를 확인한다.
				// 대차정보 확인 : 현재동 E동, 상태 IDLE
				String sEquip = YmCommonConst.NEW_TC_3XTC05;
				sEnableBay = YmCommonConst.BAY_GP_E;
				sSchCode_TC = YmCommonConst.NEW_SCH_WORK_KIND_CTM7;		// 동간이적상차
				sStackColGp = sEquip+"0101";
				jtrEquipStat = dao.getToEquipState(sStackColGp);
			}
			String sStackStat 	= StringHelper.evl(jtrEquipStat.getFieldString("STACK_STAT"), ""); 				// 적재상태
			String sAssignYn 	= StringHelper.evl(jtrEquipStat.getFieldString("CARLOAD_ASSIGN_YN"), ""); 		// 상차스케쥴지정여부
			String sLoadSchCode = StringHelper.evl(jtrEquipStat.getFieldString("CARLOAD_SCH_WORK_KIND"), ""); 	// 상차스케쥴
			String sCurrStopLoc = StringHelper.evl(jtrEquipStat.getFieldString("CURR_STOP_LOC"), ""); 			// 현재동
			String sCarLoadLoc 	= StringHelper.evl(jtrEquipStat.getFieldString("CARLOAD_STOP_LOC"), ""); 		// 상차동
			
			if("L".equals(sStackStat)){
				if ("Y".equals(sAssignYn)){
					
				}else{
					JDTORecord jtrTc = dao.getTCLoadCount(sStackColGp);
					int nCnt = jtrTc.getFieldInt("CNT");
					if(sCurrStopLoc.equals(sEnableBay)){
						if(nCnt == 0)
						{
							// 설비상태 TO위치 가능
							
						}else{
							// 설비상태 TO위치 불가능
						}
					}else{		// 대차가 적치불가능한 동에 위치하여 있다.
						
					}
				}
			}
			// 추출하는 소재의 정보를 확인한다.
			// 정보 확인 : 
			/* 저장품이동조건 비교
			 * 
			 */
					
		}catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		
		return listResult;
	}
	/**
	 * 스케쥴 취소 테이블에 INSERT
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param ydSchDAO 
	 * @throws Exception
	 */
	public boolean  createCancelSchedule(String sch_id , String modifier, String scancelMent ) {
		boolean isSuccess = false;
		try {
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC2");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			List editData = null;
			editData = new ArrayList();
			editData.add(modifier);
			editData.add(scancelMent);
			editData.add(sch_id);
			
			int insertCnt = ymCommonDAO.createCancelSchedule(editData);
			
			logger.println(LogLevel.DEBUG, this,"createCancelSchedule insertCnt2 = "+insertCnt);
			isSuccess = true;
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new EJBServiceException(e);
		}
		return isSuccess;
	}
}
