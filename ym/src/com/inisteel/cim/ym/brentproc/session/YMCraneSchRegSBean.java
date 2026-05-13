package com.inisteel.cim.ym.brentproc.session;

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
 * @ejb.bean name="YMCraneSchRegEJB" jndi-name="JNDIYMCraneSchReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class YMCraneSchRegSBean extends BaseSessionBean {
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
				EJBConnector ejbConn = new EJBConnector("default","JNDIYMCraneSchReg",this);
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
	 			EJBConnector ejbConn = new EJBConnector("default","JNDIYMYardWrkResReg",this);
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
	 * 오퍼레이션명 : PIDEV
	 *
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
									String sUserId,
									String sYdGp) {
		boolean isSuccess = false;

		try {
			
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
				EJBConnector ejbConn = new EJBConnector("default","JNDIYMCraneSchReg",this);
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
	 			tcRecordDM.setField("YD_GP", sYdGp);
	 			
	 			//인터페이스 전문 호출	 	
	 			EJBConnector ejbConn = new EJBConnector("default","JNDIYMYardWrkResReg",this);
	 			ejbConn.trx("getM10YDLMJ1031",new Class[]{JDTORecord.class},
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
				
				JDTORecord stockRc = dao.getStockInfoPI(sStockId);
				
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

				isSuccess = SendYMDM008PI(sPutYardGp, sStockId.trim(), sUpLoc.trim(), sPutLoc.trim());
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
				
				// 임가공 PI 용
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
	 * 오퍼레이션명 : PIDEV
	 *
	 * Coil 이적실적 (출하로 이적실적 송신 YMDM008) 공통 진도 Code가 출하작업지시대기, 제품충당대기, 출하작업대기,
	 * 보관매출이면 출하로 "이적실적 송신"
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */        
	public boolean SendYMDM008PI(String YardGp, String StockId, String UpPosition, String PutPosition) {
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
 			isSuccess = (Boolean)ejbConn.trx("getYDDMR004PI",new Class[]{JDTORecord.class},
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
}
