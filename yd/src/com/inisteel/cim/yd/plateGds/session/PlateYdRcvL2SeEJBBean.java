/**
 * @(#)PlateYdRcvL2SeEJBSBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2021/01/06
 *
 * @description      후판제품야드 자동화 크레인 L2 수신측 Facade Session EJB클래스
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2021/01/06   			윤재광       김광철      최초 등록
 * 
 */ 
package com.inisteel.cim.yd.plateGds.session;

import java.util.ArrayList;
import java.util.List;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.StringUtils;
import com.inisteel.cim.yd.common.dao.YdPlateCommDAO;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdSlabUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.plate.PlateGdsYdUtil;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO;
 
/**
 *      [A] 클래스명 : 후판제품야드 자동화 크레인 L2 수신 처리
 *
 * @ejb.bean name="PlateYdRcvL2SeEJB" jndi-name="PlateYdRcvL2SeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
*/

public class PlateYdRcvL2SeEJBBean extends BaseSessionBean  {
	
	private static final long serialVersionUID = 1L;
	private Logger logger = new Logger("yd");
	
	
	private YdPlateCommDAO  commDao 		= new YdPlateCommDAO();
	private YdSlabUtils ydSlabUtils = new YdSlabUtils();
	
	private EJBConnector ydEjbCon = new EJBConnector("default", this);
	private YdDelegate   ydDelegate =new YdDelegate();
	
	private YdUtils ydUtils 			= new YdUtils();
	private YdDaoUtils ydDaoUtils   	 = new YdDaoUtils();
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}	
    
	/**
	 *      [A] 오퍼레이션명 : 저장위치제원요구(Y9YDL001) 
	 *      yd/src/com/inisteel/clm/yd/ydEquipStat/MapSync/MapSyncFaEJB/rcvY4StrLocSpecReq
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord inRecord
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY9YDL001(JDTORecord inRecord) throws DAOException {
		// TKOVLOC
		// YD-UC-????
		// TC : Y8YDL001
		// 후판제품야드L2시스템으로부터 저장위치제원요구 수신
		//
		String sUniqueId = "";
		String logId = inRecord.getResultCode();
		
		String methodNm = "저장위치제원요구[PlateYdRcvL2SeEJBBean.rcvY9YDL001] < " + inRecord.getResultMsg();
		ydSlabUtils.printLog(logId, methodNm, "S+");
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return

		try {
				sUniqueId = StringUtils.trim(inRecord.getFieldString("UNIQUE_ID"));
				if("".equals(sUniqueId)){
					sUniqueId = logId;
				}
				
				inRecord.setResultCode(sUniqueId);
				inRecord.setResultMsg(methodNm);
				
				ydEjbCon = new EJBConnector("default", "MapSyncSeEJB", this); 
				ydEjbCon.trx("procY4StrLocSpecReq",   new Class[] { JDTORecord.class }, new Object[] { inRecord });
				ydSlabUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(ydSlabUtils.makeErrorLog(logId, methodNm, e));
		}
		
		return jrRtn;
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 저장품제원요구(Y9YDL002)
	 *      yd/src/com/inisteel/clm/yd/ydEquipStat/MapSync/MapSyncFaEJB/rcvY4StockSpecReq
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord inRecord
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY9YDL002(JDTORecord inRecord) throws DAOException {
		// TKOVLOC
		// YD-UC-????
		// TC : Y8YDL002
		// 후판제품야드L2시스템으로부터 저장품제원요구 수신
		//
		String sUniqueId = "";
		String logId = inRecord.getResultCode();
		
		String methodNm = "저장품제원요구[PlateYdRcvL2SeEJBBean.rcvY9YDL002] < " + inRecord.getResultMsg();
		ydSlabUtils.printLog(logId, methodNm, "S+");
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return

		try {
				sUniqueId = StringUtils.trim(inRecord.getFieldString("UNIQUE_ID"));
				if("".equals(sUniqueId)){
				    sUniqueId = logId;
			    }
			
				inRecord.setResultCode(sUniqueId);
				inRecord.setResultMsg(methodNm);
				
				ydEjbCon = new EJBConnector("default", "MapSyncSeEJB", this); 
				ydEjbCon.trx("procY4StockSpecReq",   new Class[] { JDTORecord.class }, new Object[] { inRecord });
				ydSlabUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(ydSlabUtils.makeErrorLog(logId, methodNm, e));
		}
		
		return jrRtn;
	}
    
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 설비운전모드전환(Y9YDL003)
	 *      yd/src/com/inisteel/clm/yd/ydEquipStat/EquipTrack/EqpTrackingFaEJBBean/rcvY8EqpTrblRcvrWr
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord inRecord
	 *      @return JDTORecord
	 *      @throws DAOException  
	 *      @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvY9YDL003(JDTORecord inRecord) throws DAOException {
		// 
		// TC : Y8YDL003
		// 2후판제품야드L2시스템으로부터 설비운전모드전환 수신
		//
		String sUniqueId = "";
		String logId = inRecord.getResultCode();
		
		String methodNm = "설비운전모드전환[PlateYdRcvL2SeEJBBean.rcvY9YDL003] < " + inRecord.getResultMsg();
		ydSlabUtils.printLog(logId, methodNm, "S+");
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		
		try {
				sUniqueId = StringUtils.trim(inRecord.getFieldString("UNIQUE_ID"));
				if("".equals(sUniqueId)){
				    sUniqueId = logId;
			    }
			
			
				inRecord.setResultCode(sUniqueId);
				inRecord.setResultMsg(methodNm);
				
				ydEjbCon = new EJBConnector("default", "EqpTrackingSeEJB", this); 
				ydEjbCon.trx("procY9EqpDrvMdTurnov",   new Class[] { JDTORecord.class }, new Object[] { inRecord });
				ydSlabUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(ydSlabUtils.makeErrorLog(logId, methodNm, e));
		}
		
		return jrRtn;
	}
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 설비고장복구실적(Y9YDL004)
	 *      yd/src/com/inisteel/clm/yd/ydEquipStat/EquipTrack/EqpTrackingFaEJBBean/rcvY8EqpTrblRcvrWr
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord inRecord
	 *      @return JDTORecord
	 *      @throws DAOException  
	 *      @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvY9YDL004(JDTORecord inRecord) throws DAOException {
		// 
		// TC : Y9YDL004
		// 2후판제품야드L2시스템으로부터 설비고장복구실적 수신
		//
		String sUniqueId = "";
		String logId = inRecord.getResultCode();
		
		String methodNm = "설비고장복구실적[PlateYdRcvL2SeEJBBean.rcvY9YDL004] < " + inRecord.getResultMsg();
		ydSlabUtils.printLog(logId, methodNm, "S+");
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		
		try {
				sUniqueId = StringUtils.trim(inRecord.getFieldString("UNIQUE_ID"));
				if("".equals(sUniqueId)){
				    sUniqueId = logId;
			    }
			
				inRecord.setResultCode(sUniqueId);
				inRecord.setResultMsg(methodNm);
				
				ydEjbCon = new EJBConnector("default", "EqpTrackingSeEJB", this); 
				ydEjbCon.trx("procY9EqpTrblRcvrWr",   new Class[] { JDTORecord.class }, new Object[] { inRecord });
				
				ydSlabUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(ydSlabUtils.makeErrorLog(logId, methodNm, e));
		}
		
		return jrRtn;
	}

	
	/**
	 *      [A] 오퍼레이션명 : 크레인 현재위치(Y9YDL005)
	 *      yd/src/com/inisteel/clm/yd/ydEquipStat/EquipTrack/EqpTrackingFaEJBBean/rcvY8CrnCurrLoc
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord inRecord
	 *      @return JDTORecord
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvY9YDL005(JDTORecord inRecord) throws DAOException {
		// 
		// TC : Y9YDL005
		// 2후판제품야드L2시스템으로부터 크레인현재위치 수신
		//
		String sUniqueId = "";
		String logId = inRecord.getResultCode();
		
		String methodNm = "크레인 현재위치[PlateYdRcvL2SeEJBBean.rcvY9YDL005] < " + inRecord.getResultMsg();
		ydSlabUtils.printLog(logId, methodNm, "S+");

		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		try {
				sUniqueId = StringUtils.trim(inRecord.getFieldString("UNIQUE_ID"));
				if("".equals(sUniqueId)){
				    sUniqueId = logId;
			    }
			
				inRecord.setResultCode(sUniqueId);
				inRecord.setResultMsg(methodNm);
				
				int iEqp_cnt = inRecord.getFieldInt("YD_EQP_QNTY");
				String sCRN_WRK_PROC_STAT = "";
				for(int Loop_i = 1; Loop_i <= iEqp_cnt; Loop_i++) {
					
					String ydEqpId = ydSlabUtils.trim(inRecord.getFieldString("YD_EQP_ID"+Loop_i ));
					if("".equals(ydEqpId)){
						break;
					}
					JDTORecord parms                = JDTORecordFactory.getInstance().create();	//전문 Return
					JDTORecordSet rsResult 		    = JDTORecordFactory.getInstance().createRecordSet("temp");
					parms.setField("YD_EQP_ID", ydEqpId);
					if (commDao.select(parms, rsResult,"com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqp") > 0) {
						// 레코드 추출
						rsResult.first();
						//JDTORecord rsResult = rsResult.getRecord();
						sCRN_WRK_PROC_STAT = ydSlabUtils.trim(rsResult.getRecord(0).getFieldString("YD_EQP_STAT")); 
					}
					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();								
					jrYdMsg.setField("YD_EQP_ID"     		, ydEqpId );  //YD_EQP_ID 
					jrYdMsg.setField("CRN_WRK_PROC_STAT"  	, sCRN_WRK_PROC_STAT);
					jrYdMsg.setField("CURR_XAXIS"     		, ydSlabUtils.trim(inRecord.getFieldString("YD_CRN_XAXIS" + Loop_i )) );  //CURR_XAXIS
					jrYdMsg.setField("FROM_XAXIS"     		, "00000000" );  //FROM_XAXIS 
					jrYdMsg.setField("TO_XAXIS"     		, "00000000" );  //TO_XAXIS
					jrYdMsg.setField("MODIFIER"     		, "Y9YDL005");					//수정자
					/*후판제품 야드 크레인 위치정보 등록*/
					commDao.update(jrYdMsg,"com.inisteel.cim.yd.acommon.dao.YdCommDAO.updYmCrnLoc");
				}
				ydSlabUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(ydSlabUtils.makeErrorLog(logId, methodNm, e));
		}
		
		return jrRtn;
	}
	/**
	 *      [A] 오퍼레이션명 : 크레인작업지시요구(Y9YDL007)
	 *      yd/src/com/inisteel/clm/yd/ydWkAct/CraneLoadWkrHd/CraneLdHdFaEJB/rcvY8CrnWrkOrdReq
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord inRecord
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvY9YDL007(JDTORecord inRecord) throws DAOException {
		// 
		// TC : Y8YDL007
		// 2후판제품야드L2시스템으로부터 크레인작업지시요구 수신
		//
		//┏━┓
		//┃ 2후판제품야드 L2에서 크레인작업지시를 요구를 수신하여 크레인의 현재 스케줄을 작업지시시
		//┗━┛
		String sUniqueId = "";
		String logId = inRecord.getResultCode();
		String methodNm = "크레인작업지시요구[PlateYdRcvL2SeEJBBean.rcvY9YDL007] < " + inRecord.getResultMsg();

		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		ydSlabUtils.printLog(logId, methodNm, "S+");
		
		try {
				sUniqueId = StringUtils.trim(inRecord.getFieldString("UNIQUE_ID"));
				if("".equals(sUniqueId)){
				    sUniqueId = logId;
			    }
			
				inRecord.setResultCode(sUniqueId);
				inRecord.setResultMsg(methodNm);	

				ydEjbCon = new EJBConnector("default", "CraneLdHdSeEJB", this); 
				ydEjbCon.trx("procY4CrnWrkOrdReq",   new Class[] { JDTORecord.class }, new Object[] { inRecord });

				ydSlabUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(ydSlabUtils.makeErrorLog(logId, methodNm, e));
		}
		
		return jrRtn;
	}	

	/**
	 *      [A] 오퍼레이션명 : 크레인권상실적(Y9YDL008)
	 *      yd/src/com/inisteel/clm/yd/ydWkAct/CraneLoadWkrHd/CraneLdHdFaEJB/rcvY8CrnLdWr
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord inRecord
	 *      @return JDTORecord
	 *      @throws DAOException 
	*/
	public JDTORecord rcvY9YDL008(JDTORecord inRecord) throws DAOException {
		// 
		// TC : Y9YDL008, YDYDJ604
		// 2후판제품야드L2시스템으로부터 크레인권상실적 수신
		//
		//┏━┓
		//┃ 2후판제품야드 L2에서 크레인 권상처리 결과를 수신하여 권상실적을 등록
		//┗━┛
		String sUniqueId = "";
		String logId = inRecord.getResultCode();
		
		String methodNm = "크레인권상실적[PlateYdRcvL2SeEJBBean.rcvY9YDL008] < " + inRecord.getResultMsg();
		ydSlabUtils.printLog(logId, methodNm, "S+");

		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
			
		try {
				sUniqueId = StringUtils.trim(inRecord.getFieldString("UNIQUE_ID"));
				if("".equals(sUniqueId)){
				    sUniqueId = logId;
			    }
			
				inRecord.setResultCode(sUniqueId);
				inRecord.setResultMsg(methodNm);	
			
				// 권상실적처리 요청
				ydEjbCon = new EJBConnector("default", "CraneLdHdSeEJB", this); 
				ydEjbCon.trx("procY4CrnLdWr",   new Class[] { JDTORecord.class }, new Object[] { inRecord });
				
				ydSlabUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(ydSlabUtils.makeErrorLog(logId, methodNm, e));
		}
		
		return jrRtn;
	}
	
	/**
	 *      [A] 오퍼레이션명 : 크레인권하실적(Y9YDL009)
	 *	    yd/src/com/inisteel/clm/yd/ydWkAct/CraneLoadWkrHd/CraneLdHdFaEJB/rcvY8CrnUdWr
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord inRecord
	 *      @return JDTORecord
	 *      @throws DAOException 
	*/
	public JDTORecord rcvY9YDL009(JDTORecord inRecord) throws DAOException { 
		// 
		// TC : Y8YDL009, YDYDJ605
		// 2후판제품야드L2시스템으로부터 크레인권하실적 수신
		//
		//┏━┓
		//┃ 2후판제품야드 L2에서 크레인 권하처리 결과를 수신하여 권상실적을 등록
		//┗━┛
		String logId = inRecord.getResultCode();
		String sUniqueId = "";
		String methodNm = "크레인권하실적[PlateYdRcvL2SeEJBBean.rcvY9YDL009] < " + inRecord.getResultMsg();
		ydSlabUtils.printLog(logId, methodNm, "S+");

		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		try {
				sUniqueId = StringUtils.trim(inRecord.getFieldString("UNIQUE_ID"));
				if("".equals(sUniqueId)){
				    sUniqueId = logId;
			    }
			
				inRecord.setResultCode(sUniqueId);
				inRecord.setResultMsg(methodNm);
				
				String headMsgGp      = ydSlabUtils.trim(inRecord.getFieldString("MSG_GP")); 		//head msg구분
				String ydEqpId        = ydSlabUtils.trim(inRecord.getFieldString("YD_EQP_ID"));     		//설비ID
				String sCrnWrkMode2  = ydSlabUtils.trim(inRecord.getFieldString("YD_EQP_WRK_MODE"));
				String szYD_SCH_CD   = ydSlabUtils.trim(inRecord.getFieldString("YD_SCH_CD"));
				String szCrnSchId    = ydSlabUtils.trim(inRecord.getFieldString("YD_CRN_SCH_ID"));
				
				ydSlabUtils.printLog(logId, methodNm, "YD_EQP_ID :: MSG_GP :: YD_EQP_WRK_MODE :: "+ ydEqpId + "::"+headMsgGp + "::" + sCrnWrkMode2);
								
				// 2202.10.06 head msg구분 "N"일 경우 B동 B3,B4 크레인 무인 MODE  파일일 작업 권하실적 수신 시 해당 권하실적
				// 으로 작업지시 CALL만 하도록 한다.
				if ("N".equals(headMsgGp))
				{
					YdDelegate ydDelegate = new YdDelegate();
					
					JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
					
					recInTemp.setField("MSG_ID"             , "YDY9L005");
					recInTemp.setField("YD_EQP_ID"     		, ydEqpId);										//야드설비ID
			        recInTemp.setField("YD_WRK_PROG_STAT"   , YdConstant.YD_EQP_STAT_DN_CMPL);			//야드작업진행상태
			        recInTemp.setField("YD_SCH_CD"   		, szYD_SCH_CD);									//야드스케줄코드
			        recInTemp.setField("YD_CRN_SCH_ID"   	, szCrnSchId);	
			        recInTemp.setField("YD_L2_WR_GP"        , YdConstant.CRN_WRK_RE_DN_WR);			            // 야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
			        recInTemp.setField("YD_L3_HD_RS_CD"     , YdConstant.CRN_WRK_RE_CD_NORMAL_HD);		//야드L3처리결과코드
			         
			        //권하실적 응답처리
					ydDelegate.sendMsg(recInTemp);
					
					
					recInTemp.setField("MSG_ID",           "YDYDJ642");		      			
		      		recInTemp.setField("YD_EQP_ID",        ydEqpId);
		      		recInTemp.setField("YD_EQP_WRK_MODE",  sCrnWrkMode2);
		      		recInTemp.setField("YD_WRK_PROG_STAT", "W");
		      		recInTemp.setField("YD_SCH_CD",        "");
		      		recInTemp.setField("YD_CRN_SCH_ID",    "");
		      		recInTemp.setField("YD_CRN_XAXIS",     "");
		      		recInTemp.setField("YD_CRN_YAXIS",     "");
		      				
		      		//크레인작업지시 송신
		      		ydDelegate.sendMsg(recInTemp);
				}
				else
				{
					// 권하처리 요청 
				    ydEjbCon = new EJBConnector("default", "CraneUdHdSeEJB", this); 
					ydEjbCon.trx("procY4CrnUdWr",   new Class[] { JDTORecord.class }, new Object[] { inRecord });
				}

				ydSlabUtils.printLog(logId, methodNm, "S-");
				
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(ydSlabUtils.makeErrorLog(logId, methodNm, e));
		}
		
		return jrRtn;
	}

	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업지시응답(rcvY9YDL015)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvY9YDL015(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인작업지시응답[PlateYdRcvL2SeEJBBean.rcvY9YDL015] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		try {
			ydSlabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값ydEqpId
			String msgId        = ydSlabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId     	= ydSlabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"));     		//설비ID
			String ydWrkProgStat= ydSlabUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT"));    //야드작업진행상태(5:권하위치변경)만 올라옴
			String ydSchCd   	= ydSlabUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"));    		//야드스케쥴코드
			String ydCrnSchId  	= ydSlabUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID")); 		//야드크레인스케쥴ID
			String reqYn  		= ydSlabUtils.trim(rcvMsg.getFieldString("REQ_YN")); 				//유무응답
			String ReqMsg  		= ydSlabUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_REQ_MSG")); //메시지
			String modifier     = ydSlabUtils.trim(rcvMsg.getFieldString("MODIFIER"     )); 		//수정자(Backup Only)
			String headMsgGp    = ydSlabUtils.trim(rcvMsg.getFieldString("MSG_GP"     )); 		//head msg구분
			String errCd    	= ydSlabUtils.trim(rcvMsg.getFieldString("ERR_CD"     )); 		//에러코드
			String ydCrnSchIdOld    = ydSlabUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID_OLD"     )); //이전스케줄ID
			String ydChgStlNo 		= "";
			String ydChgDnWoLoc	    = "";
			String ydChgDnWoLayer	= "";
			String ydBefDnWoLoc		= "";
			String ydBefDnWoLayer	= "";
			String ydWbookId 		= "";
			String ydWrkProgStst	= "";	 //철자유의..kbs
			String ydUpWrLoc	    = "";	
			String ydL2RequestStat	= "";
			
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

					
			
			/**********************************************************
			* 1. 무인 크레인이 이면 처리
			*    - 별도 분기처리
			**********************************************************/
			JDTORecord parms                = JDTORecordFactory.getInstance().create();	//전문 Return
			JDTORecordSet rsResult 		    = JDTORecordFactory.getInstance().createRecordSet("temp");
			parms.setField("YD_EQP_ID", ydEqpId);
			
			if (commDao.select(parms, rsResult,"com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqp") > 0) {
				// 레코드 추출
				rsResult.first();
				//JDTORecord rsResult = rsResult.getRecord();
				String sCrnWrkMode2 = ydSlabUtils.trim(rsResult.getFieldString("YD_EQP_WRK_MODE2")); 
				if ("A".equals(sCrnWrkMode2) ) {
					jrRtn = this.rcvY9YDL015_Auto(rcvMsg);
					return jrRtn;
				} 
			}
			
			ydSlabUtils.printLog(logId,  "★★기존 유인 작업 함 -->  크레인 작업지시번호★★" + ydCrnSchId, "SL");
			
			
			/**********************************************************
			* 2. 수신 항목 값 Check
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER"                 , modifier);	
			jrParam.setField("YD_EQP_ID"         		, ydEqpId);  					
			jrParam.setField("YD_CRN_SCH_ID"      		, ydCrnSchId); 
			jrParam.setField("YD_WRK_PROG_STAT"    		, ydWrkProgStat); 
			jrParam.setField("YD_WRK_PROG_REQ_MSG"   	, ReqMsg);
			
			JDTORecordSet jsCrnSch = JDTORecordFactory.getInstance().createRecordSet("temp");
			ydSlabUtils.printLog(logId, methodNm, "대상작업 조회");
			int intRtnVal = commDao.select(jrParam, jsCrnSch, "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnsch");
			if (intRtnVal <= 0) {
				return jrRtn;
			} else {
				jsCrnSch.first();
				ydCrnSchId 		= jsCrnSch.getRecord(0).getFieldString("YD_CRN_SCH_ID");   
				ydWbookId 		= jsCrnSch.getRecord(0).getFieldString("YD_WBOOK_ID");     
				ydSchCd 		= jsCrnSch.getRecord(0).getFieldString("YD_SCH_CD");       
				ydWrkProgStst	= jsCrnSch.getRecord(0).getFieldString("YD_WRK_PROG_STAT");
				ydUpWrLoc	    = jsCrnSch.getRecord(0).getFieldString("YD_UP_WR_LOC");    //권상위치
				
				ydChgStlNo   	= jsCrnSch.getRecord(0).getFieldString("STL_NO_TEMP");     //sTAG_STL_NO
				ydChgDnWoLoc	= jsCrnSch.getRecord(0).getFieldString("YD_DN_WO_LOC_TO"); //변경후저장위치szStkPos
				ydChgDnWoLayer	= jsCrnSch.getRecord(0).getFieldString("STK_LYR_NO_TEMP"); //변경후저장위치szStkLyrNo
				ydBefDnWoLoc 	= jsCrnSch.getRecord(0).getFieldString("YD_DN_WO_LOC");    //변경전저장위치szOldStkPos
				ydBefDnWoLayer	= jsCrnSch.getRecord(0).getFieldString("YD_DN_WO_LYR");  //변경전저장위치szOldStkLyrNo
				ydL2RequestStat	= jsCrnSch.getRecord(0).getFieldString("YD_L2_REQUEST_STAT");  //야드L2요구상태
			} 
					
			
			/**********************************************************
			* 3. L2 응답메시지 UPDATE 
			**********************************************************/
			ydSlabUtils.printLog(logId, methodNm, "L2 응답메시지 UPDATE");
			commDao.update(jrParam,"com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnSchProgStatMsgNo");	
			
			/**********************************************************
			* 4. 권하위치 변경 요청 일 경우
			**********************************************************/		
			if (YdConstant.YD_EQP_STAT_DN_CHANGE.equals(ydWrkProgStat)) 
			{
				
				/**********************************************************
				* 4-1. 응답전문 N 일때(작업 불가메세지)
				**********************************************************/
				if ("N".equals(reqYn)) 
				{
					ydSlabUtils.printLog(logId, methodNm + "권하위치 변경 불가 일경우", "SL");
					return jrRtn;
				}
				
				/**********************************************************
				* 4-2. 신규 저장위치 CHECK
				**********************************************************/
				String ydChgStkColGp = ydChgDnWoLoc.substring(0, 6); 
				String ydChgStkBedNo = ydChgDnWoLoc.substring(6, 8);
//				String ydBefStkColGp = ""; 
//				String ydBefStkBedNo = "";
//				if ( ydBefDnWoLoc.length() == 8 && (!ydBefDnWoLoc.equals("XX010101"))) {	
//					ydBefStkColGp = ydBefDnWoLoc.substring(0, 6); 
//					ydBefStkBedNo = ydBefDnWoLoc.substring(6, 8);
//				}
//				ydSlabUtils.printLog(logId, methodNm + "전저장위치"+ ydBefDnWoLoc, "SL");
				
				JDTORecord inRecord = JDTORecordFactory.getInstance().create();
				inRecord.setField("YD_STK_COL_GP" 	, ydChgStkColGp);	
				inRecord.setField("YD_STK_BED_NO" 	, ydChgStkBedNo);	
				inRecord.setField("YD_STK_LYR_NO" 	, ydChgDnWoLayer);
				//파라미터 jrParam 이거 맞는지???? kbs

				ydSlabUtils.printLog(logId, methodNm, "신규 적재위치 조회");
				JDTORecordSet jsChgStkLay =  JDTORecordFactory.getInstance().createRecordSet("temp");
                intRtnVal = commDao.select(inRecord,jsChgStkLay,"com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrCurStlNo");	

				if (intRtnVal == 0) {
					ydSlabUtils.printLog(logId, methodNm + "저장위치 이상", "SL");
					return jrRtn;
				}
				
				
				JDTORecord[] jtoChgDnLocInfo = new JDTORecord[1];
				JDTORecord transJTOTmp = JDTORecordFactory.getInstance().create();;
				transJTOTmp.setField("YD_GP"		, YdConstant.YD_GP_PLATE2_GDS_YARD);  
				transJTOTmp.setField("YD_CRN_SCH_ID"		, ydCrnSchId);   
				transJTOTmp.setField("YD_EQP_ID"			, ydEqpId);
				transJTOTmp.setField("YD_DN_WO_LOC"			, ydChgDnWoLoc); //변경후저장위치szStkPos
				transJTOTmp.setField("YD_DN_WO_LAYER"		, ydChgDnWoLayer); // 변경후저장위치szStkLyrNo 없으면 단정보는 재계산된다.
				transJTOTmp.setField("YD_L2_REQUEST_STAT"  , ydL2RequestStat	);  //야드L2요구상태
				transJTOTmp.setField("CALL_PGM"  		  , "Y9YL015_AUTO"	);  //프로그램 CALL 구분
				jtoChgDnLocInfo[0] = transJTOTmp;
				EJBConnector ydEjbCon = new EJBConnector("default", "YdJspCommonSeEJB", this); 
				ydEjbCon.trx("updToPosFix4G",   new Class[] { JDTORecord[].class }, new Object[] { jtoChgDnLocInfo });
	    		
				return jrRtn;
			} 
			/**********************************************************
			* 5. 권하위치 변경이 아닌 경우
			**********************************************************/	
			else 
			{		
				/**********************************************************
				* 5-1. 작업 불가인 경우
				**********************************************************/	
				if ("N".equals(reqYn)) 
				{
					
					ydSlabUtils.printLog(logId, methodNm + "불가 일경우", "SL");
					
					
					/**********************************************************
					* 5-1-1. 권하위치 변경 N응답시 메세지 update ..위에서 이미 처리된 중복 갱신 kbs
					**********************************************************/	
					//ydSlabUtils.printLog(logId, methodNm, "크레인스케쥴 응답정보 수정");					
					//commDao.update(jrParam, "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnSchProgStatMsgNo");					
					if (errCd != null && !"".equals(errCd)) 
					{
						
						/**********************************************************
						* 5-1-2. 작업지시가 내려와 이미 코일을 집었는데 다음작업지시가 내려온 경우
						* 다음작업지시를 수행할 수 없는 상황 (차상국은 이전 작업지시를 그대로 실행)
						* L3 는 'N' 응답에 'E001' 메세지 받은 크레인 스케줄 ID의 작업진행상태를  'W'로 변경한다.
						**********************************************************/
						if ("E001".equals(errCd)) 
						{
							
							ydSlabUtils.printLog(logId, methodNm + "E001 불가 일경우", "SL");
							
							jrParam.setField("YD_WRK_PROG_STAT"	, "W");  					
							jrParam.setField("YD_CRN_SCH_ID"   	, ydCrnSchId);  	
							commDao.update(jrParam, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updYdCrnSchYdWrkProgStat");
							
							
							/**********************************************************
							* 5-1-3. 이전 스케줄ID의 상태를 '1'로 변경한다. 
							* 이전 스케줄 상태를 왜 수정하는지?? 
							**********************************************************/
							if (ydCrnSchIdOld != null && !"".equals(ydCrnSchIdOld)) 
							{
								jrParam.setField("YD_WRK_PROG_STAT"	, "1");  					
								jrParam.setField("YD_CRN_SCH_ID"   	, ydCrnSchIdOld);  					
								commDao.update(jrParam, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updYdCrnSchYdWrkProgStat");
								
							}									
						}
					}
					
					return jrRtn;
				} 
				
				/**********************************************************
				* 5-2. 작업 가능인 경우
				**********************************************************/	
				else 
				{

					/**********************************************************
					* 5-2-1. 요청응답 갱신
					**********************************************************/	
					// 2022.02.08 선택해제(L2 응답)시 크레인 상태값 변경 하지 않도록 수정
					if (!"D".equals(headMsgGp)) 
					{
						jrParam.setResultCode(logId);	//Log ID
						jrParam.setResultMsg(methodNm);	//Log Method Name
						jrParam.setField("MODIFIER"                 , modifier);	
						jrParam.setField("YD_CRN_SCH_ID"      		, ydCrnSchId);
						jrParam.setField("YD_WRK_PROG_REQ_MSG"   	, ReqMsg);  					
						ydSlabUtils.printLog(logId, methodNm, "크레인스케쥴 응답정보 수정");
						commDao.update(jrParam, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updYdCrnSchProgStatMsg");
					}
 
					/**********************************************************
					 * 5-2-2. 요청정보 Clear
					 **********************************************************/	
					// 지시가 내려간 것에 대한 응답결과라면
					if("Y".equals(reqYn) && "1".equals(ydWrkProgStat)){
						if(YdConstant.YD_EQP_STAT_UP_WO.equals(ydWrkProgStat)){
							if(!"".equals(ydCrnSchId) && !"".equals(ydSchCd)){
								JDTORecord param = JDTORecordFactory.getInstance().create();
								param = JDTORecordFactory.getInstance().create();
								param.setField("YD_DN_WO_LOC_TO"    , "" );
								param.setField("STL_NO_TEMP"        , "");
								param.setField("STK_LYR_NO_TEMP"    , "");
								param.setField("YD_L2_REQUEST_STAT" , "");
								param.setField("YD_CRN_SCH_ID"      , ydCrnSchId);
								commDao.update(param,"com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.upYdCrnSchLocStat1");
							}
						}
					}
						
					/**********************************************************
					 * 5-2-1. 스케줄 취소인 경우
					 **********************************************************/
					if ("Y".equals(reqYn) && "D".equals(headMsgGp))
					{
						
						// 크레인스케쥴 취소 및 작업취소일경우
						if( "D".equals(ydL2RequestStat) || "X".equals(ydL2RequestStat) ){
							
							ydSlabUtils.printLog(logId, "스케쥴취소 [ " + ydWbookId + " - " + ydCrnSchId + " ]", "SL");
							
							jrParam.setField("YD_WBOOK_ID"  	, ydWbookId );
							jrParam.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
							jrParam.setField("YD_EQP_ID"    	, ydEqpId   );
							jrParam.setField("YD_SCH_CD"    	, ydSchCd   );
							jrParam.setField("YD_L2_RETURN_FLAG", "Y"       );
							jrParam.setField("IS_LAST_SELECTED"	, "1");
							jrParam.setField("IS_SCH_MTL"    	, "Y"); // 스케줄 단위 취소
							
							
							
							/**********************************************************
							 * 5-2-2. 크레인스케줄 취소
							 **********************************************************/
							JDTORecord params = JDTORecordFactory.getInstance().create();
							params.setField("YD_CRN_SCH_ID", ydCrnSchId);
							params.setField("YD_SCH_CD",     ydSchCd);
							params.setField("DEL_YN",        "Y");		
							params.setField("MODIFIER",      modifier);
							
							EJBConnector ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);			
							JDTORecord outRecord1 	= (JDTORecord)ejbConn.trx("PlateSchCncl4G", new Class[] { JDTORecord.class }, new Object[] { params });
							String sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
							String sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
							String sCANCEL_SEND= StringHelper.evl(outRecord1.getFieldString("CANCEL_SEND"), "N");
							String szYD_EQP_ID	= StringHelper.evl(outRecord1.getFieldString("YD_EQP_ID"), "");
							
							ydSlabUtils.printLog(logId, methodNm, "스케쥴취소  RTN_CD :: RTN_MSG :: CANCEL_SEND :: YD_EQP_ID"+ sRTN_CD + "::"+sRTN_MSG + "" + sCANCEL_SEND + "" + szYD_EQP_ID);
							if(sCANCEL_SEND.equals("Y")) {
								//--------------------------------------------------------------------------------
								// 설비가 고장 또는 OFF 라인 상태가 아닐경우 
								// 선택된 설비가 취소 되었으므로 해당설비의 설비 테이블정보에
								// 작업대기 상태로 UPDATE 해준다.
								//--------------------------------------------------------------------------------
								String szRtnMsg = YdCommonUtils.checkCrnStat(szYD_EQP_ID);
								
								if(szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS)){
									JDTORecord recEqpPara   = JDTORecordFactory.getInstance().create();
									recEqpPara.setField("YD_EQP_ID"		, szYD_EQP_ID);
									recEqpPara.setField("YD_EQP_STAT"	, YdConstant.YD_EQP_STAT_IDLE);
									recEqpPara.setField("MODIFIER"		,"Y9YDL015");
									ydSlabUtils.printLog(logId, methodNm, "크레인("+ szYD_EQP_ID +") 설비상태 [" + YdConstant.YD_EQP_STAT_IDLE +"]로 변경 ------------------");
									EJBConnector ejbConn2 = new EJBConnector("default","SlabJspSeEJB",this);
									ejbConn2.trx("RequiresUpdYdEqp",new Class[]{JDTORecord.class}, new Object[]{recEqpPara});
								}
							}
							
							/**********************************************************
							 * 5-2-3. 작업예약 취소
							 **********************************************************/
							// 작업취소를 요청했다면 작업예약을 추가적으로 삭제한다.
							if("X".equals(ydL2RequestStat)){
								
								ydSlabUtils.printLog(logId, "스케쥴 취소 종료!! 작업예약 취소 시작", "");
								ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);			
								outRecord1 	= (JDTORecord)ejbConn.trx("PlateDelWBook", new Class[] { JDTORecord.class }, new Object[] { outRecord1 });
								ydSlabUtils.printLog(logId, "작업예약 취소 종료!!", "");
								
								sRTN_CD				= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
								szYD_EQP_ID			= StringHelper.evl(outRecord1.getFieldString("YD_EQP_ID"), "");
								String szRTN_SND	= StringHelper.evl(outRecord1.getFieldString("RTN_SND"), "N");
								
								if("Y".equals(szRTN_SND) && "Y".equals(sCANCEL_SEND)){
	//								YdDelegate ydDelegate = new YdDelegate();
									
									ydSlabUtils.printLog(logId, "크레인 작업지시 정보를 내부QUEUE로 송신 합니다","");
									
									JDTORecord recDelPara   = JDTORecordFactory.getInstance().create();
									recDelPara.setField("MSG_ID"				, StringHelper.evl(outRecord1.getFieldString("MSG_ID"), ""));
									recDelPara.setField("YD_EQP_ID"				, szYD_EQP_ID            );					   
									recDelPara.setField("YD_WRK_PROG_STAT"		, StringHelper.evl(outRecord1.getFieldString("YD_WRK_PROG_STAT"), ""));
									recDelPara.setField("YD_SCH_CD"				, StringHelper.evl(outRecord1.getFieldString("YD_SCH_CD"), "") );  
	//								ydDelegate.sendMsg(recDelPara);
									this.addSndData(jrRtn, recDelPara);
								}
							}
						}
					}
				}
			}

			ydSlabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(ydSlabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
 
	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업지시응답무인크레인(Y9YDL015)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord inRecord
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvY9YDL015_Auto(JDTORecord rcvMsg) throws DAOException {
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		
		
		String logId = rcvMsg.getResultCode();
		String methodNm = "크레인작업가능응답[PlateYdRcvL2SeEJBBean.rcvY9YL015_Auto] < " + rcvMsg.getResultMsg();
 		try {
			ydSlabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값ydEqpId
			String msgId         = ydSlabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId     	 = ydSlabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"));     		//설비ID
			String ydWrkProgStat = ydSlabUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT"));    //야드작업진행상태(5:권하위치변경)만 올라옴
			String ydSchCd   	 = ydSlabUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"));    		//야드스케쥴코드
			String ydCrnSchId  	 = ydSlabUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID")); 		//야드크레인스케쥴ID
			String reqYn  		 = ydSlabUtils.trim(rcvMsg.getFieldString("REQ_YN")); 				//유무응답
			String ReqMsg  		 = ydSlabUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_REQ_MSG")); //메시지
			String modifier      = ydSlabUtils.trim(rcvMsg.getFieldString("MODIFIER")); 		//수정자(Backup Only)
			String headMsgGp     = ydSlabUtils.trim(rcvMsg.getFieldString("MSG_GP")); 		//head msg구분
			String errCd    	 = ydSlabUtils.trim(rcvMsg.getFieldString("ERR_CD")); 		//에러코드
			String ydCrnSchIdOld = ydSlabUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID_OLD")); //이전스케줄ID		
			String ydChgStlNo 		= "";
			String ydChgDnWoLoc	    = "";
			String ydChgDnWoLayer	= "";
			String ydBefDnWoLoc		= "";
			String ydBefDnWoLayer	= "";
			String ydWbookId 		= "";
			String ydWrkProgStst	= ""; //철자유의..kbs	
			String ydUpWrLoc	    = "";	
			String ydL2RequestStat	= "";
			String sYD_SCH_PRIOR    = ""; //스케줄 우선순위
			String ydL3Msg			= ""; 	//야드L3MESSAGE
			
			ydSlabUtils.printLog(logId,  "★★자동화 작업---> 크레인 작업지시번호★★" + ydCrnSchId, "SL");
			ydSlabUtils.printLog(logId, "요청구분 [ " + reqYn + " - 작업취소 " + headMsgGp + " ]", "SL");
			
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			*   무인 크레인이 아니면 SKIP
			**********************************************************/
			JDTORecord parms                = JDTORecordFactory.getInstance().create();	//전문 Return
			JDTORecordSet rsResult 		    = JDTORecordFactory.getInstance().createRecordSet("temp");
			parms.setField("YD_EQP_ID", ydEqpId);
			if (commDao.select(parms, rsResult,"com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqp") > 0) {
				rsResult.first();
				String sCrnWrkMode2 = ydSlabUtils.trim(rsResult.getFieldString("YD_EQP_WRK_MODE2")); 
				if (!"A".equals(sCrnWrkMode2) ) {
					throw new Exception("무인 크레인이 아닙니다.! [" + ydEqpId + "]");
				} 
			}
			ydSlabUtils.printLog(logId,  "★★자동화 작업 함---> 크레인 작업지시번호★★" + ydCrnSchId, "SL");
			ydSlabUtils.printLog(logId, "요청구분 [ " + reqYn + " - 작업취소 " + headMsgGp + " ]", "SL");
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER"                 , modifier);	
			jrParam.setField("YD_EQP_ID"         		, ydEqpId);  					
			jrParam.setField("YD_CRN_SCH_ID"      		, ydCrnSchId);  					
			jrParam.setField("YD_WRK_PROG_STAT"    		, ydWrkProgStat);  					
			jrParam.setField("YD_WRK_PROG_REQ_MSG"   	, ReqMsg);    
			jrParam.setField("ERR_CD"   				, errCd);    
			jrParam.setField("YD_CRN_SCH_ID_OLD"   		, ydCrnSchIdOld);    
			
			/**************************************
			 * 2. L2 응답메시지 UPDATE 
			 **************************************/
			ydSlabUtils.printLog(logId, methodNm, "L2 응답메시지 UPDATE");
			commDao.update(jrParam,"com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnSchProgStatMsgNo");	

			JDTORecordSet jsCrnSch = JDTORecordFactory.getInstance().createRecordSet("temp");
			int intRtnVal = commDao.select(jrParam, jsCrnSch, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.getYdCrnSchLocLog");
			if (intRtnVal <= 0) {
				throw new Exception("크레인 스케쥴 번호가 없습니다..! [" + ydCrnSchId + "]");
			} else {
				jsCrnSch.first();
				ydCrnSchId 		= jsCrnSch.getRecord(0).getFieldString("YD_CRN_SCH_ID");   
				ydWbookId 		= jsCrnSch.getRecord(0).getFieldString("YD_WBOOK_ID");     
				ydSchCd 		= jsCrnSch.getRecord(0).getFieldString("YD_SCH_CD");       
				ydWrkProgStst	= jsCrnSch.getRecord(0).getFieldString("YD_WRK_PROG_STAT");
				ydUpWrLoc	    = jsCrnSch.getRecord(0).getFieldString("YD_UP_WR_LOC");    //권상위치
				
				ydChgStlNo   	= jsCrnSch.getRecord(0).getFieldString("STL_NO_TEMP");     //변경할 코일번호
				ydChgDnWoLoc	= jsCrnSch.getRecord(0).getFieldString("YD_DN_WO_LOC_TO"); //변경후저장위치szStkPos
				ydChgDnWoLayer	= jsCrnSch.getRecord(0).getFieldString("STK_LYR_NO_TEMP"); //변경후저장위치szStkLyrNo
				ydBefDnWoLoc 	= jsCrnSch.getRecord(0).getFieldString("YD_DN_WO_LOC");    //변경전저장위치szOldStkPos
				ydBefDnWoLayer	= jsCrnSch.getRecord(0).getFieldString("YD_DN_WO_LYR");  //변경전저장위치szOldStkLyrNo
				ydL2RequestStat	= jsCrnSch.getRecord(0).getFieldString("YD_L2_REQUEST_STAT");  //야드L2요구상태
				
				sYD_SCH_PRIOR   = jsCrnSch.getRecord(0).getFieldString("YD_SCH_PRIOR");    //스케줄우선순위
			} 
			
			
			/********************************
			 * 3. 스케줄 상태와 설비상태 동기화
			 ********************************/
			String sSCH_STAT = ydWrkProgStat;
			if (YdConstant.YD_EQP_STAT_DN_CHANGE.equals(ydWrkProgStat)) //강제권하
			{ 
				sSCH_STAT = "2"; //권상완료
			}
			
			if ("Y".equals(reqYn)) 
			{
				if (!"D".equals(headMsgGp)){
					jrParam.setField("YD_EQP_PROG_STAT"	, sSCH_STAT); 
					jrParam.setField("YD_EQP_ID"		, ydEqpId  );
					ydSlabUtils.printLog(logId, methodNm, "설비상태변경처리");
		//			commDao.update(jrParam, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updEqpStat");
		    		EJBConnector ejbConn = new EJBConnector("default", "PlateYdRcvL2SeEJB", this);
					ejbConn.trx("updateTx", new Class[] { JDTORecord.class, String.class }, new Object[] { jrParam, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updEqpStat" });
				}
				
				/**********************************************************
				* 지시에 대한 응답일 경우
				**********************************************************/	
				// 지시가 내려간 것에 대한 응답결과라면
				if(!YdConstant.YD_EQP_STAT_DN_CHANGE.equals(ydL2RequestStat)){
					if(YdConstant.YD_EQP_STAT_UP_WO.equals(ydWrkProgStat) && !"D".equals(headMsgGp)){
						if(!"".equals(ydCrnSchId) && !"".equals(ydSchCd)){
	        				JDTORecord param = JDTORecordFactory.getInstance().create();
	        				param = JDTORecordFactory.getInstance().create();
			    	    	param.setField("YD_L2_REQUEST_STAT" , "");
			    	    	param.setField("YD_CRN_SCH_ID"      , ydCrnSchId);
			    	    	commDao.update(param,"com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.upYdCrnSchLocStat1");
						 }
					}
				}
			}
			
			
			/**********************************************************
			* 4. 권하위치 변경 요청 결과
			**********************************************************/
			if (("1".equals(ydWrkProgStat) || "5".equals(ydWrkProgStat)) && YdConstant.YD_EQP_STAT_DN_CHANGE.equals(ydL2RequestStat)) 
			{
				
				/**********************************************************
				* 4-1. 응답전문 N 일때(작업 불가메세지)
				**********************************************************/
				if ("N".equals(reqYn)) 
				{
					ydSlabUtils.printLog(logId, methodNm + "권하위치 변경 불가 일경우", "SL");
					return jrRtn;
				}
				ydSlabUtils.printLog(logId, methodNm + "==>> 권하위치 변경 시작  <<==", "SL");
				JDTORecord[] jtoChgDnLocInfo = new JDTORecord[1];
				JDTORecord transJTOTmp = JDTORecordFactory.getInstance().create();;
				transJTOTmp.setField("YD_GP"		, YdConstant.YD_GP_PLATE2_GDS_YARD);  
				transJTOTmp.setField("YD_CRN_SCH_ID"		, ydCrnSchId);   
				transJTOTmp.setField("YD_EQP_ID"			, ydEqpId);
				transJTOTmp.setField("YD_DN_WO_LOC"			, ydChgDnWoLoc); //변경후저장위치szStkPos
				transJTOTmp.setField("YD_DN_WO_LAYER"		, ydChgDnWoLayer); // 변경후저장위치szStkLyrNo 없으면 단정보는 재계산된다.
				transJTOTmp.setField("YD_L2_REQUEST_STAT"  , ydL2RequestStat	);  //야드L2요구상태
				transJTOTmp.setField("CALL_PGM"  		  , "Y9YL015_AUTO"	);  //프로그램 CALL 구분
				
				jtoChgDnLocInfo[0] = transJTOTmp;
				
				EJBConnector ydEjbCon = new EJBConnector("default", "YdJspCommonSeEJB", this); 
				ydEjbCon.trx("updToPosFix4G",   new Class[] { JDTORecord[].class }, new Object[] { jtoChgDnLocInfo });
				ydSlabUtils.printLog(logId, methodNm + "==>> 권하위치 변경 끝  <<==", "SL");	 
			} 
			
			
			/**********************************************************
			* 5. 권하위치 변경이 아닌 경우
			**********************************************************/
			else 
			{
				/**********************************************************
				* 5-1. 작업 가능인 경우
				**********************************************************/
				if ("Y".equals(reqYn)) 
				{
				  //AT0000 물류시스템개선 작업지시 삭제 시 'W': 작업대기로 상태 변경 2023.02.10
					if (PlateGdsYdUtil.isApplyYn("작업지시 해제 및 취소 정상 처리 여부")) {
						if ("D".equals(headMsgGp)) jrParam.setField("YD_WRK_PROG_STAT" ,"W"); 
						ydSlabUtils.printLog(logId, methodNm + "가능인 경우", "SL");
						ydSlabUtils.printLog(logId, methodNm, "크레인스케쥴 응답정보 수정 New");
						commDao.update(jrParam, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updYdCrnSchProgStatMsg");
					}
					else
					{
						ydSlabUtils.printLog(logId, methodNm + "가능인 경우", "SL");
						ydSlabUtils.printLog(logId, methodNm, "크레인스케쥴 응답정보 수정");
						//commDao.update(jrParam, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updYdCrnSchProgStatMsg");
					}
					
					ydSlabUtils.printLog(logId, "요청구분 [ " + reqYn + " - 작업취소 " + headMsgGp + " ]", "");
					
					/********************
					 * 5-1-1. 스케줄 취소인 경우
					 ********************/
					if ("Y".equals(reqYn) && "D".equals(headMsgGp)) 
					{
						ydSlabUtils.printLog(logId, "스케쥴취소 [ " + ydWbookId + " - " + ydCrnSchId + " ]", "");
						

						// 크레인스케쥴 취소 및 작업취소일경우
						if( "D".equals(ydL2RequestStat) || "X".equals(ydL2RequestStat) ){
 
							jrParam.setField("YD_WBOOK_ID"  	, ydWbookId );
							jrParam.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
							jrParam.setField("YD_EQP_ID"    	, ydEqpId   );
							jrParam.setField("YD_SCH_CD"    	, ydSchCd   );
							jrParam.setField("YD_L2_RETURN_FLAG", "Y"       );
							jrParam.setField("IS_LAST_SELECTED"	, "1");
							jrParam.setField("IS_SCH_MTL"    	, "Y"); // 스케줄 단위 취소
							
							/**********************************************************
							 * 5-2-2. 크레인스케줄 취소
							 **********************************************************/
							JDTORecord params = JDTORecordFactory.getInstance().create();
							params.setField("YD_CRN_SCH_ID", ydCrnSchId);
							params.setField("YD_SCH_CD",     ydSchCd);
							params.setField("DEL_YN",        "Y");		
							params.setField("MODIFIER",      modifier);
							
							// 스케쥴취소 구분자 
							// 단위취소, 작업취소로 구분되며
							if("X".equals(ydL2RequestStat)){
								params.setField("IS_SCH_MTL",        "N");	
							}else{
								params.setField("IS_SCH_MTL",        "Y");
							}
							params.setField("REQ_YN",        reqYn);	
							
							EJBConnector ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);			
							JDTORecord outRecord1 	= (JDTORecord)ejbConn.trx("PlateSchCncl4G", new Class[] { JDTORecord.class }, new Object[] { params });
							String sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
							String sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
							String sCANCEL_SEND= StringHelper.evl(outRecord1.getFieldString("CANCEL_SEND"), "N");
							String szYD_EQP_ID	= StringHelper.evl(outRecord1.getFieldString("YD_EQP_ID"), "");
							ydSlabUtils.printLog(logId, "스케쥴취소  RTN_CD :: RTN_MSG :: CANCEL_SEND :: YD_EQP_ID"+ sRTN_CD + "::"+sRTN_MSG + "" + sCANCEL_SEND + "" + szYD_EQP_ID, "SL");
							
							if(sCANCEL_SEND.equals("Y")) {
								//--------------------------------------------------------------------------------
								// 설비가 고장 또는 OFF 라인 상태가 아닐경우 
								// 선택된 설비가 취소 되었으므로 해당설비의 설비 테이블정보에
								// 작업대기 상태로 UPDATE 해준다.
								//--------------------------------------------------------------------------------
								String szRtnMsg = YdCommonUtils.checkCrnStat(szYD_EQP_ID);
								
								if(szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS)){
									JDTORecord recEqpPara   = JDTORecordFactory.getInstance().create();
									recEqpPara.setField("YD_EQP_ID"		, szYD_EQP_ID);
									recEqpPara.setField("YD_EQP_STAT"	, YdConstant.YD_EQP_STAT_IDLE);
									recEqpPara.setField("MODIFIER"		,"Y9YDL015");
									ydSlabUtils.printLog(logId, methodNm, "크레인("+ szYD_EQP_ID +") 설비상태 [" + YdConstant.YD_EQP_STAT_IDLE +"]로 변경 ------------------");
									EJBConnector ejbConn2 = new EJBConnector("default","SlabJspSeEJB",this);
									ejbConn2.trx("RequiresUpdYdEqp",new Class[]{JDTORecord.class}, new Object[]{recEqpPara});
								}
							}
							
							/**********************************************************
							 * 5-2-3. 작업예약 취소
							 **********************************************************/
							// 작업취소를 요청했다면 작업예약을 추가적으로 삭제한다.
							if("X".equals(ydL2RequestStat)){
								
								ydSlabUtils.printLog(logId, "스케쥴 취소 종료!! 작업예약 취소 시작", "");
								ejbConn = new EJBConnector("default", "PlateJspSeEJB", this);			
								outRecord1 	= (JDTORecord)ejbConn.trx("PlateDelWBook", new Class[] { JDTORecord.class }, new Object[] { outRecord1 });
								ydSlabUtils.printLog(logId, "작업예약 취소 종료!!", "");
								
								sRTN_CD				= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
								szYD_EQP_ID			= StringHelper.evl(outRecord1.getFieldString("YD_EQP_ID"), "");
								String szRTN_SND	= StringHelper.evl(outRecord1.getFieldString("RTN_SND"), "N");
								
								if("Y".equals(szRTN_SND) && "Y".equals(sCANCEL_SEND)){
//								YdDelegate ydDelegate = new YdDelegate();
									
									ydSlabUtils.printLog(logId, "크레인 작업지시 정보를 내부QUEUE로 송신 합니다","");
									
									JDTORecord recDelPara   = JDTORecordFactory.getInstance().create();
									recDelPara.setField("MSG_ID"				, StringHelper.evl(outRecord1.getFieldString("MSG_ID"), ""));
									recDelPara.setField("YD_EQP_ID"				, szYD_EQP_ID            );					   
									recDelPara.setField("YD_WRK_PROG_STAT"		, StringHelper.evl(outRecord1.getFieldString("YD_WRK_PROG_STAT"), ""));
									recDelPara.setField("YD_SCH_CD"				, StringHelper.evl(outRecord1.getFieldString("YD_SCH_CD"), "") );  
//								ydDelegate.sendMsg(recDelPara);
									this.addSndData(jrRtn, recDelPara);
								}
							}
						}
					}	
					
					/***************************
					 *5-1-4. 일시정지-긴급작업(S1)
					 * 해당기능 일단 제외시킴
					 ***************************/
//					ydSlabUtils.printLog(logId, "[Y9YDL015] 일시정지-긴급작업(S1) 응답 시작", "[INFO]");
//					if (PlateGdsYdUtil.isApplyYn("자동크레인긴급작업적용여부") && "0".equals(sYD_SCH_PRIOR)) {
//						// 해당 스케줄 1로 변경
//						jrParam.setField("YD_WRK_PROG_STAT"	, "1"       ); // 긴급작업 응답이 Y로 왔으므로 선택(1)
//						jrParam.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
//						ydSlabUtils.printLog(logId, methodNm + "일시정지-긴급작업(S1)", "");
//						commDao.update(jrParam, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updCrnWrkMgt1Auto");
//						
//						// 동일크레인의 해당 스케줄외 W로 변경 
//					    jrParam.setField("YD_WRK_PROG_STAT"	, "W"       );  
//						jrParam.setField("YD_EQP_ID"    	, ydEqpId   );
//						ydSlabUtils.printLog(logId, methodNm + "일시정지-긴급작업(S1)", "");
//						commDao.update(jrParam, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updCrnSchW");
//					}
//					ydSlabUtils.printLog(logId, "[Y9YDL015] 일시정지-긴급작업(S1) 응답 끝", "[INFO]");
				}
				
				/**********************************************************
				* 5-2. 작업 불가능인 경우
				**********************************************************/
				else if ("N".equals(reqYn)) 
				{
					 
					ydSlabUtils.printLog(logId, methodNm + "권하위치 변경이 아닐경우  N 응답 ", "SL");
					/**********************************************************
					* 5-2-1. 응답 메시지 갱신 ..위에서 이미 처리된 중복 갱신 kbs
					**********************************************************/
					ydSlabUtils.printLog(logId, methodNm, "크레인스케쥴 응답정보 수정");
					commDao.update(jrParam, "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnSchProgStatMsgNo");
					 
					
					/**********************************************************
					* 6. 오류코드 정의
					**********************************************************/
					if ( "E003".equals(errCd)) 
					{
 
					}
					
					return jrRtn;
				}
			}
			
			ydSlabUtils.printLog(logId, methodNm, "====> 크레인 작업지시응답(Y9YDL015) End <====");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(ydSlabUtils.makeErrorLog(logId, methodNm, e));
		}		
	}
	
	/**
	 *      [A] 오퍼레이션명 : 상차도 작업불가(Y9YDL018)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord inRecord
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvY9YDL018(JDTORecord inRecord) throws DAOException { 
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		
		String s_STAT= "";
		String sUniqueId = "";
		String logId = inRecord.getResultCode();
		
		String methodNm = "상차도 작업불가[PlateYdRcvL2SeEJBBean.rcvY9YDL018] < " + inRecord.getResultMsg();
		ydSlabUtils.printLog(logId, methodNm, "S+");

		
		try {
			
			sUniqueId = StringUtils.trim(inRecord.getFieldString("UNIQUE_ID"));
			if("".equals(sUniqueId)){
			    sUniqueId = logId;
		    }
		
		
			inRecord.setResultCode(sUniqueId);
			inRecord.setResultMsg(methodNm);	
			
			//수신 항목 값
			String sPT_LOAD_LOC = PlateGdsYdUtil.trim(inRecord.getFieldString("PT_LOAD_LOC")); //상차도위치(6)
			String sUSE_YN		= PlateGdsYdUtil.trim(inRecord.getFieldString("USE_YN")); //Y:사용가능, N:사용불가
			String modifier 	= PlateGdsYdUtil.trim(inRecord.getFieldString("MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = "Y9YDL018"; }
			
			if(!"".equals(sPT_LOAD_LOC)){
				//수신항목 Check
				if (sPT_LOAD_LOC.length() != 6) {
					throw new Exception("상차도 위치 PT_LOAD_LOC 가 6자리가 아닙니다!! [" + sPT_LOAD_LOC + "]");
				}
				if (!"T".equals(sPT_LOAD_LOC.substring(0,1)) || !"PT".equals(sPT_LOAD_LOC.substring(2,4))) {
					throw new Exception("상차도 위치 PT_LOAD_LOC 가 야드구분이 [T]가 아니거나 SECT_GP가 'PT'가 아닙니다!! [" + sPT_LOAD_LOC + "]");
				}
				if (!"Y".equals(sUSE_YN) && !"N".equals(sUSE_YN)) {
					throw new Exception("상차도 사용유무 USE_YN 이 'Y','N' 이 아닌 값이 들어 왔습니다!! [" + sUSE_YN + "]");
				}			
				
				// 차량포인트 적치열활성상태 UPDATE
			//	jrParam.setField("MODIFIER"			, modifier		); //수정자
			//	jrParam.setField("YD_STK_COL_GP"	, sPT_LOAD_LOC	); //상차도위치
				if ("Y".equals(sUSE_YN)) {
					s_STAT = "C"; //야드적치열활성상태 L:적치가능
				} else {
					s_STAT = "N"; //야드적치열활성상태 N:사용불가
				}
				
				JDTORecord jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setField("MODIFIER"			, modifier		); //수정자
				jrParam.setField("YD_STK_COL_ACT_STAT", s_STAT);
				jrParam.setField("YD_STK_COL_GP", sPT_LOAD_LOC);
				commDao.update(jrParam, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updYdStkColActStat");
				 
				
				jrParam			= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord 
				// 차량포인트 적치열활성상태 UPDATE
				jrParam.setField("MODIFIER"			, modifier		); //수정자
				jrParam.setField("YD_STK_COL_GP"	, sPT_LOAD_LOC	); //상차도위치
				if ("Y".equals(sUSE_YN)) {
					jrParam.setField("YD_STK_COL_ACT_STAT"	, "L"); //야드적치열활성상태 L:적치가능,  C아닌지...확인 필요 kbs
				} else {
					jrParam.setField("YD_STK_COL_ACT_STAT"	, "N"); //야드적치열활성상태 N:사용불가
				}
				commDao.update(jrParam, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updYdCarPointActStat"); 
				ydSlabUtils.printLog(logId, methodNm, "S-");
			}
			
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(e);
		}
		return jrRtn;
	}
	
 
	/**
	 *      [A] 오퍼레이션명 : 차량작업예정정보요구(Y9YDL019)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord inRecord
	 *      @return JDTORecord
	 *      @throws DAOException 
	*/
	public JDTORecord rcvY9YDL019(JDTORecord inRecord) throws DAOException
	{ 
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		try
		{	
			
			// 상차정보가 50개가 넘어서
//			if(PlateGdsYdUtil.isApplyYn("차량예정정보복수모듈")){
				return this.rcvY9YDL019_More(inRecord);
//			}
			
//			String sUniqueId = "";
//			String logId = inRecord.getResultCode();
//			
//			String methodNm = "차량작업예정정보요구[PlateYdRcvL2SeEJBBean.rcvY9YDL019] < " + inRecord.getResultMsg();
//			ydSlabUtils.printLog(logId, methodNm, "S+");
//			
//			JDTORecord jtoYDY9L008 = null;
//			JDTORecordSet rstYDY9L008 = null; 
//		
//		
//			sUniqueId = StringUtils.trim(inRecord.getFieldString("UNIQUE_ID"));
//			if("".equals(sUniqueId)){
//			    sUniqueId = logId;
//		    }
//		
//			inRecord.setResultCode(sUniqueId);
//			inRecord.setResultMsg(methodNm);	
//			
//			String sPT_LOAD_LOC   	= PlateGdsYdUtil.trim(ydDaoUtils.paraRecChkNull(inRecord, "PT_LOAD_LOC"));     	//상차도 위치
//			String sYD_CAR_SCH_ID   	= PlateGdsYdUtil.trim(ydDaoUtils.paraRecChkNull(inRecord, "YD_CAR_SCH_ID"));     	//상차도 위치
//			String sSEARCH_FLAG  = PlateGdsYdUtil.trim(ydDaoUtils.paraRecChkNull(inRecord, "SEARCH_FLAG"));
//			
//			// 상차의 경우 YDYDJ284(작업예약생성) -> YDYDJ293(크레인스케쥴호출 및 차량스케쥴 상차작업예약업데이트)
//			// 위와 같은 절차로 인하여 부득이하게 작업예약 아이디를 받아서 처리한다.
//			// YDYDJ284에서 상차작업예약 생성 후 차량예정정보를 호출함
//			String sYD_WBOOK_ID  = PlateGdsYdUtil.trim(ydDaoUtils.paraRecChkNull(inRecord, "YD_WBOOK_ID"));
//			String sCAR_NO  = PlateGdsYdUtil.trim(ydDaoUtils.paraRecChkNull(inRecord, "CAR_NO"));
//			
//			/**********************************************************
//			* 1. 수신 항목 값 Check
//			**********************************************************/
//			
//			if ( "".equals(sPT_LOAD_LOC) && "".equals(sYD_CAR_SCH_ID) )
//			{
//				ydSlabUtils.printLog(logId, methodNm + "상차도 정보가 없습니다. PT_LOAD_LOC["+sPT_LOAD_LOC+"]YD_CAR_SCH_ID["+ sYD_CAR_SCH_ID+"]" , "[ERROR]");
//				return jrRtn;
//			} 
//			
//			if("".equals(sSEARCH_FLAG) && !"".equals(sPT_LOAD_LOC)){
//				sSEARCH_FLAG = "1";
//			}
//	 
//			if("".equals(sSEARCH_FLAG) && !"".equals(sYD_CAR_SCH_ID)){
//				sSEARCH_FLAG = "2";
//			}
//			
//			JDTORecord parms = JDTORecordFactory.getInstance().create();  
//			rstYDY9L008 = JDTORecordFactory.getInstance().createRecordSet("temp");
//			
//			int nRowCnt = 0;
//			if("1".equals(sSEARCH_FLAG)) 
//			{
//
//				// 트랜잭션이슈로 인하여 부득이하게 차량번호까지 정합성에 사용한다.
//				if("".equals(sCAR_NO)){
//					ydSlabUtils.printLog(logId, methodNm + "현재 차량포인트["+sPT_LOAD_LOC+"]의 차량번호를 갖고 온다." , "[INFO]");
////					parms.setField("YD_STK_COL_GP", sPT_LOAD_LOC);
//					if(commDao.select(parms, rstYDY9L008, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.getYdCarPoint") > 0){	
//						sCAR_NO = rstYDY9L008.getRecord(0).getFieldString("CAR_NO");
//						ydSlabUtils.printLog(logId, methodNm + "현재 차량포인트["+sPT_LOAD_LOC+"]의 차량번호["+sCAR_NO+"] 조회완료" , "[INFO]");
//					}
//				}
//				
//				//상차위치로 차량예정정보 조회
//				parms.setField("YD_STOP_LOC",	sPT_LOAD_LOC);
//				parms.setField("YD_WBOOK_ID",	sYD_WBOOK_ID);
//				parms.setField("CAR_NO",	sCAR_NO);
//				ydSlabUtils.printLog(logId, methodNm + "상차위치["+ sPT_LOAD_LOC +"]를 전달받아 차량예정 정보를 송신한다." , "[INFO]");
//				nRowCnt = commDao.select(parms, rstYDY9L008, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.getYDY9L008List");
//			}
//			else 
//			{
//				//차량스케줄ID로 차량예정정보 조회
//				parms.setField("YD_CAR_SCH_ID",		sYD_CAR_SCH_ID);
//				parms.setField("YD_WBOOK_ID",	sYD_WBOOK_ID);
//				ydSlabUtils.printLog(logId, methodNm + "차량스케쥴ID["+ sYD_CAR_SCH_ID +"]를 전달받아 차량예정 정보를 송신한다." , "[INFO]");
//				nRowCnt = commDao.select(parms, rstYDY9L008, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.getYDY9L008List");
//			}
//			
//			ydSlabUtils.printLog(logId, methodNm + "차량예정정보전송 건 수 :: " + nRowCnt , "[INFO]");
//			
//			jtoYDY9L008 = JDTORecordFactory.getInstance().create();
//			jtoYDY9L008.setField("MSG_ID", 						"YDY9L008" );
//			jtoYDY9L008.setField("DATE", 						YdUtils.getCurDate("yyyy-MM-dd") );
//			jtoYDY9L008.setField("TIME", 						YdUtils.getCurDate("HH-mm-ss") );
//			jtoYDY9L008.setField("MSG_GP" , 					"I");
//			jtoYDY9L008.setField("MSG_LEN", 					YdUtils.fillSpZr("2680", 4, 0) );
//			jtoYDY9L008.setField("TEMP", 						YdUtils.fillSpZr(" ", 29, 1) );
//			jtoYDY9L008.setField("PT_LOAD_LOC", 				YdUtils.fillSpZr(sPT_LOAD_LOC, 6, 1));
//			jtoYDY9L008.setField("CAR_NO", 						YdUtils.fillSpZr_KOR(" ", 15, 1));
//			jtoYDY9L008.setField("CARD_NO", 					YdUtils.fillSpZr(" ", 4, 1));
//			jtoYDY9L008.setField("PT_CLS", 						YdUtils.fillSpZr(" ", 2, 1));
//			jtoYDY9L008.setField("WORK_CLS", 					YdUtils.fillSpZr(" ", 1, 1));
//			jtoYDY9L008.setField("WORK_COIL_MAX_CNT", 			"00");
//			
//			if(nRowCnt>0){
//				if("".equals(rstYDY9L008.getRecord(0).getFieldString("PT_LOAD_LOC").trim()) && "".equals(sPT_LOAD_LOC)){
//					ydSlabUtils.printLog(logId, methodNm + "차량예정정보중 상차정지위치를 찾을 수 없어 L2전송을 Skip처리한다. :: " , "[INFO]");
//					return jrRtn;
//				}
//				else{
//					JDTORecord sMsgJto = null; 
//					for(int i=0, nMakeId = 1; i<nRowCnt; i++, nMakeId++){
//						sMsgJto = rstYDY9L008.getRecord(i);
//						// Header부만 발췌하자.
//						if(i==0){
//							if(!"".equals(sMsgJto.getFieldString("PT_LOAD_LOC").trim())){
//								jtoYDY9L008.setField("PT_LOAD_LOC", 				sMsgJto.getFieldString("PT_LOAD_LOC"));
//							}
//							jtoYDY9L008.setField("CAR_NO", 						YdUtils.fillSpZr_KOR(sMsgJto.getFieldString("CAR_NO"),15,1) );
//							jtoYDY9L008.setField("CARD_NO", 					sMsgJto.getFieldString("CARD_NO"));
//							jtoYDY9L008.setField("PT_CLS", 						sMsgJto.getFieldString("PT_CLS") );
//							jtoYDY9L008.setField("WORK_CLS", 					sMsgJto.getFieldString("WORK_CLS") );
//							jtoYDY9L008.setField("WORK_COIL_MAX_CNT", 			sMsgJto.getFieldString("WORK_COIL_MAX_CNT") );
//						}
//						
//						
//						jtoYDY9L008.setField("STOCK_ID"+nMakeId, 			sMsgJto.getFieldString("STOCK_ID") );
//						jtoYDY9L008.setField("LOAD_LOC_CD"+nMakeId, 		sMsgJto.getFieldString("LOAD_LOC_CD") ); 
//						jtoYDY9L008.setField("MAT_WGT"+nMakeId, 			sMsgJto.getFieldString("MAT_WGT") );
//						jtoYDY9L008.setField("MAT_THK"+nMakeId, 			sMsgJto.getFieldString("MAT_THK") );
//						jtoYDY9L008.setField("MAT_WTH"+nMakeId, 			sMsgJto.getFieldString("MAT_WTH") );
//						jtoYDY9L008.setField("MAT_LEN"+nMakeId, 			sMsgJto.getFieldString("MAT_LEN") );
//						jtoYDY9L008.setField("MAT_ODIA"+nMakeId, 			sMsgJto.getFieldString("MAT_ODIA") );
//						jtoYDY9L008.setField("MAT_IDIA"+nMakeId, 			sMsgJto.getFieldString("MAT_IDIA") );
//						
//						jtoYDY9L008.setField("WORK_STATE"+nMakeId, 			sMsgJto.getFieldString("WORK_STATE") );
//						jtoYDY9L008.setField("YD_CURR_BAY_GP"+nMakeId, 		sMsgJto.getFieldString("YD_CURR_BAY_GP") );
//					}
//				}
//			} 
//			
//			// 재료총 건수와 전문상 최대 발송갯수 50개
//			if( nRowCnt < 50 ){
//				int nMakeId = nRowCnt;
//				for( int i=(nRowCnt+1); i <= 50; i++){
//					nMakeId++;
// 					
//					jtoYDY9L008.setField("STOCK_ID"+nMakeId, 			YdUtils.fillSpZr(" ", 11, 1) );
//					jtoYDY9L008.setField("LOAD_LOC_CD"+nMakeId,  			YdUtils.fillSpZr(" ", 2, 1));
//					jtoYDY9L008.setField("MAT_WGT"+nMakeId, 			PlateGdsYdUtil.genDecmailFomatter("00000", ""));
//					jtoYDY9L008.setField("MAT_THK"+nMakeId, 			PlateGdsYdUtil.genDecmailFomatter("000.000",""));
//					jtoYDY9L008.setField("MAT_WTH"+nMakeId, 			PlateGdsYdUtil.genDecmailFomatter("0000.0", ""));
//					jtoYDY9L008.setField("MAT_LEN"+nMakeId, 			PlateGdsYdUtil.genDecmailFomatter("0000000",""));
//					jtoYDY9L008.setField("MAT_ODIA"+nMakeId, 			PlateGdsYdUtil.genDecmailFomatter("00000","0"));
//					jtoYDY9L008.setField("MAT_IDIA"+nMakeId, 			PlateGdsYdUtil.genDecmailFomatter("0000.0","0"));
//					
//					jtoYDY9L008.setField("WORK_STATE"+nMakeId, 			YdUtils.fillSpZr(" ", 1, 1) );
//					jtoYDY9L008.setField("YD_CURR_BAY_GP"+nMakeId, 		YdUtils.fillSpZr(" ", 6, 1) );
//				}
//			} 
//			//차량예정정보 백업 송신
//			ydDelegate.sendMsg_NoMakeTc(jtoYDY9L008);
//			ydSlabUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(e);
		} 
	}
 
	
	/**
	 *      [A] 오퍼레이션명 : 차량작업예정정보요구(Y9YDL019)
	 *       - 차량예정정보가 왔을 경우
	 *       차량 이송재료가 50개를 초과할때 수행하는 Method이다.
	 *       - 최초 : YDY9L008, 2번째 : YDY9L009로 전송되어진다.
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord inRecord
	 *      @return JDTORecord
	 *      @throws DAOException 
	*/
	public JDTORecord rcvY9YDL019_More(JDTORecord inRecord) throws DAOException
	{ 
		String sUniqueId = "";
		String sYD_FRM_YN = "";
		String logId = inRecord.getResultCode();
		
		String methodNm = "차량작업예정정보요구[PlateYdRcvL2SeEJBBean.rcvY9YDL019_More] < " + inRecord.getResultMsg();
		ydSlabUtils.printLog(logId, methodNm, "S+");
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		JDTORecordSet rstYDY9L008 = null; 
		JDTORecordSet checkRecodeSet = null; 
		
		try
		{	
			sUniqueId = StringUtils.trim(inRecord.getFieldString("UNIQUE_ID"));
			if("".equals(sUniqueId)){
			    sUniqueId = logId;
		    }
		
			inRecord.setResultCode(sUniqueId);
			inRecord.setResultMsg(methodNm);	
			
			String sPT_LOAD_LOC   	= PlateGdsYdUtil.trim(ydDaoUtils.paraRecChkNull(inRecord, "PT_LOAD_LOC"));     	//상차도 위치
			String sYD_CAR_SCH_ID   	= PlateGdsYdUtil.trim(ydDaoUtils.paraRecChkNull(inRecord, "YD_CAR_SCH_ID"));     	//상차도 위치
			String sSEARCH_FLAG  = PlateGdsYdUtil.trim(ydDaoUtils.paraRecChkNull(inRecord, "SEARCH_FLAG"));
			
			// 상차의 경우 YDYDJ284(작업예약생성) -> YDYDJ293(크레인스케쥴호출 및 차량스케쥴 상차작업예약업데이트)
			// 위와 같은 절차로 인하여 부득이하게 작업예약 아이디를 받아서 처리한다.
			// YDYDJ284에서 상차작업예약 생성 후 차량예정정보를 호출함
			String sYD_WBOOK_ID  = PlateGdsYdUtil.trim(ydDaoUtils.paraRecChkNull(inRecord, "YD_WBOOK_ID"));
			String sCAR_NO  = PlateGdsYdUtil.trim(ydDaoUtils.paraRecChkNull(inRecord, "CAR_NO"));
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			
			if ( "".equals(sPT_LOAD_LOC) && "".equals(sYD_CAR_SCH_ID) )
			{
				ydSlabUtils.printLog(logId, methodNm + "상차도 정보가 없습니다. PT_LOAD_LOC["+sPT_LOAD_LOC+"]YD_CAR_SCH_ID["+ sYD_CAR_SCH_ID+"]" , "[ERROR]");
				return jrRtn;
			} 
			
			if("".equals(sSEARCH_FLAG) && !"".equals(sPT_LOAD_LOC)){
				sSEARCH_FLAG = "1";
			}
	 
			if("".equals(sSEARCH_FLAG) && !"".equals(sYD_CAR_SCH_ID)){
				sSEARCH_FLAG = "2";
			}
			
			JDTORecord parms = JDTORecordFactory.getInstance().create();  
			rstYDY9L008 = JDTORecordFactory.getInstance().createRecordSet("temp");
			
			int nRowCnt = 0;
			if("1".equals(sSEARCH_FLAG)) 
			{

				// 트랜잭션이슈로 인하여 부득이하게 차량번호까지 정합성에 사용한다.
				if("".equals(sCAR_NO)){
					ydSlabUtils.printLog(logId, methodNm + "현재 차량포인트["+sPT_LOAD_LOC+"]의 차량번호를 갖고 온다." , "[INFO]");
					parms.setField("YD_STOP_LOC",	sPT_LOAD_LOC);
					if(commDao.select(parms, rstYDY9L008, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.getYdCarPoint") > 0){	
						sCAR_NO = rstYDY9L008.getRecord(0).getFieldString("CAR_NO");
						ydSlabUtils.printLog(logId, methodNm + "현재 차량포인트["+sPT_LOAD_LOC+"]의 차량번호["+sCAR_NO+"] 조회완료" , "[INFO]");
					}
				}
				
				//상차위치로 차량예정정보 조회
				parms.setField("YD_STOP_LOC",	sPT_LOAD_LOC);
				parms.setField("YD_WBOOK_ID",	sYD_WBOOK_ID);
				parms.setField("CAR_NO",	sCAR_NO);
				ydSlabUtils.printLog(logId, methodNm + "상차위치["+ sPT_LOAD_LOC +"]를 전달받아 차량예정 정보를 송신한다." , "[INFO]");
				nRowCnt = commDao.select(parms, rstYDY9L008, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.getYDY9L008List_more");
			}
			else 
			{
				//차량스케줄ID로 차량예정정보 조회
				parms.setField("YD_CAR_SCH_ID",		sYD_CAR_SCH_ID);
				parms.setField("YD_WBOOK_ID",	sYD_WBOOK_ID);
				ydSlabUtils.printLog(logId, methodNm + "차량스케쥴ID["+ sYD_CAR_SCH_ID +"]를 전달받아 차량예정 정보를 송신한다." , "[INFO]");
				nRowCnt = commDao.select(parms, rstYDY9L008, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.getYDY9L008List_more");
			}
			
			ydSlabUtils.printLog(logId, methodNm + "차량예정정보전송 건 수 :: " + nRowCnt , "[INFO]");
			
			if(nRowCnt>0){
				if("".equals(rstYDY9L008.getRecord(0).getFieldString("PT_LOAD_LOC").trim()) && "".equals(sPT_LOAD_LOC)){
					ydSlabUtils.printLog(logId, methodNm + "차량예정정보중 상차정지위치를 찾을 수 없어 L2전송을 Skip처리한다. :: " , "[INFO]");
					return jrRtn;
				}
			}
			
			
			// 220623 박성열 차량 전문 연속으로 2번 들어올 시 체크하여 2번째 것은 형상 촬영 한걸로 설정 하는 로직. (1분 이내)
			//------------------------------------------------------------------------------------------------------------
			//	전사물류개선
			//------------------------------------------------------------------------------------------------------------
			String sMSG_GP = "I";
			String sYD_SCH_CD = "";
			String szCAR_KIND = "";
			String szYD_CAR_SCH_ID = "";
			int intRtnVal3 =0;
			JDTORecord param = JDTORecordFactory.getInstance().create();  
			checkRecodeSet = JDTORecordFactory.getInstance().createRecordSet("temp");
			param.setField("CAR_NO",	sCAR_NO);
			//param.setField("YD_WBOOK_ID",	sYD_WBOOK_ID);
			if(commDao.select(param, checkRecodeSet, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.getYDY9L008List_more_checkcar") > 0){	
				
				checkRecodeSet.first();
				
				//차량형상 완료 표시 업데이트
				JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();
				jrParam1.setResultCode(logId);	    //Log ID
	 			jrParam1.setResultMsg(methodNm);	//Log Method Name
				jrParam1.setField("YD_WBOOK_ID",  sYD_WBOOK_ID);
				jrParam1.setField("MODIFIER"    , "Y9YDL019");	//수정자10자리로 변경	
				jrParam1.setField("YD_CTS_RELAY_YN" , "Y");	//형상완료로 인한 스케줄 기동
				commDao.update(jrParam1, "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.updYdWrkbookCTS");
				
				
				
				//AT000 물류시스템 개선 2022.11.25  같은차량 연속 출하시 이전 차량 TYPE SET	
				sYD_SCH_CD      = checkRecodeSet.getFieldString("YD_SCH_CD"); 
				szCAR_KIND      = checkRecodeSet.getFieldString("BEF_CAR_KIND"); 
				szYD_CAR_SCH_ID = checkRecodeSet.getFieldString("YD_CAR_SCH_ID");	
				sYD_FRM_YN      = checkRecodeSet.getFieldString("YD_FRM_YN");	
				if ("Y".equals(sYD_FRM_YN)) sMSG_GP = "A";  // AT000 2022.11.29 L2조부장님 요청(형상이 off 일때는 sMSG_GP ="A" 전송 않음)
			}
						
			
			//차량예정정보 백업 송신 
			List rtnMsgList = makeL2SendToCarStlInfo(rstYDY9L008, inRecord, sMSG_GP);
			if(rtnMsgList != null && rtnMsgList.size() > 0){
				for(int i=0; i<rtnMsgList.size(); i++ ){
					ydDelegate.sendMsg_NoMakeTc((JDTORecord)rtnMsgList.get(i));
				}
			}
			
			ydSlabUtils.printLog(logId, methodNm + "sMSG_GP : ["+sMSG_GP+"]" , "[INFO]");
			
			if ("A".equals(sMSG_GP))
			{
				//20220725 박성열 차량이 두대 연속 같은 번호로 들어올 때, 하나 끝나고 작업지시를 새롭게 내린다.
//				JDTORecord jsCrnSch = JDTORecordFactory.getInstance().create();
//				jsCrnSch.setField("JMS_TC_CD"          , "YDYDJ506");
//			    jsCrnSch.setField("YD_EQP_ID"    	, "");
//			    jsCrnSch.setField("YD_SCH_CD"    	, "");
//				jsCrnSch.setField("YD_WBOOK_ID"    	, sYD_WBOOK_ID);	
////				
//				jrRtn = this.addSndData(jrRtn, jsCrnSch);
//				// 위의 것이 안되서 수정 220812 박성열 
				//AT000 물류시스템 개선 2022.11.25  같은차량 연속 출하시 이전 차량 TYPE SET	1
				JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();
				jrParam1.setField("TRANS_EQUIPMENT_TYPE" ,szCAR_KIND);	//형상완료로 인한 스케줄 기동
				jrParam1.setField("MODIFIER"    , "Y9YDL019"  );	//수정자		
				jrParam1.setField("YD_CAR_SCH_ID" ,szYD_CAR_SCH_ID);	//형상완료로 인한 스케줄 기동
				intRtnVal3 = commDao.update(jrParam1, "com.inisteel.cim.yd.dao.ydcarschdao.ydCarSchDao.updYdCarTrans_Type");
				
				
				if (intRtnVal3 > 0)
				{
					ydSlabUtils.printLog(logId, methodNm + "sYD_SCH_CD : ["+sYD_SCH_CD+"]" , "[INFO]");
				}
				else {
					ydSlabUtils.printLog(logId, methodNm + "차량스케쥴 UPDATE Error : ["+ intRtnVal3 +"]" , "[INFO]");
				}
					
				
				YdDelegate ydDelegate = new YdDelegate();
				
				JDTORecord recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("JMS_TC_CD","YDYDJ506");
				recPara.setField("YD_EQP_ID"    	, "");
				recPara.setField("YD_SCH_CD"    	, sYD_SCH_CD);
				recPara.setField("YD_WBOOK_ID"    	, sYD_WBOOK_ID);	
				
				ydSlabUtils.printLog(logId, methodNm + "ydDelegate.sendMsg 시작", "[INFO]");
				ydDelegate.sendMsg(recPara);
			}
			//------------------------------------------------------------------------------------------------------------
			//	전사물류개선
			//------------------------------------------------------------------------------------------------------------
			
			ydSlabUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(e);
		}
		return jrRtn;
	}
	
	/**
	 * 차량예정정보(YDY9L008) 전문을 생성한다.
	 * @param rsCarStlInfo
	 * @param inRecord
	 * @return
	 */
	private List makeL2SendToCarStlInfo(JDTORecordSet rsCarStlInfo, JDTORecord inRecord , String sMSG_GP){
		
		List rtnMsgList = new ArrayList();

		if(rsCarStlInfo != null){
			rtnMsgList.add(makeL2SendToCarStlInfo(rsCarStlInfo, inRecord,0, 50, "YDY9L008", sMSG_GP));
			
			if(rsCarStlInfo.size() > 50 ){
				rtnMsgList.add(makeL2SendToCarStlInfo(rsCarStlInfo, inRecord, 50, 100, "YDY9L009", sMSG_GP));
			}
		}
		
		return rtnMsgList;
	}
	
	/**
	 * 차량예정정보(YDY9L008) 전문을 생성한다.
	 * @param rsCarStlInfo
	 * @param inRecord
	 * @return
	 */
	private JDTORecord makeL2SendToCarStlInfo(JDTORecordSet rsCarStlInfo, JDTORecord inRecord, int startRow, int nMaxRow, String sMsgId , String sMSG_GP){
		
		JDTORecord rtnMsg = null;
		String logId = inRecord.getResultCode();
		String methodNm = "차량작업예정정보요구[PlateYdRcvL2SeEJBBean.rcvY9YDL019] < " + inRecord.getResultMsg();
		
		try{
			ydSlabUtils.printLog(logId, methodNm+ " 차량예정정보 정보생성 :: startRow :: " + startRow + " nMaxRow :: " + nMaxRow, "INFO");
			if(rsCarStlInfo != null){
				int nRowCnt = rsCarStlInfo.size();
				String sPT_LOAD_LOC = PlateGdsYdUtil.trim(ydDaoUtils.paraRecChkNull(inRecord, "PT_LOAD_LOC"));
				
				rtnMsg = JDTORecordFactory.getInstance().create();
				rtnMsg.setField("MSG_ID", 						sMsgId );
				rtnMsg.setField("DATE", 						YdUtils.getCurDate("yyyy-MM-dd") );
				rtnMsg.setField("TIME", 						YdUtils.getCurDate("HH-mm-ss") );
				//rtnMsg.setField("MSG_GP" , 						"I");
				rtnMsg.setField("MSG_GP" , 						sMSG_GP);
				rtnMsg.setField("MSG_LEN", 						YdUtils.fillSpZr("2230", 4, 0) );
				rtnMsg.setField("TEMP", 						YdUtils.fillSpZr(" ", 29, 1) );
				rtnMsg.setField("PT_LOAD_LOC", 					YdUtils.fillSpZr(sPT_LOAD_LOC, 6, 1));
				rtnMsg.setField("CAR_NO", 						YdUtils.fillSpZr_KOR(" ", 15, 1));
				rtnMsg.setField("TEL_NO", 						YdUtils.fillSpZr(" ", 15, 1));
				rtnMsg.setField("PT_CLS", 						YdUtils.fillSpZr(" ", 2, 1));
				rtnMsg.setField("WORK_CLS", 					YdUtils.fillSpZr(" ", 1, 1));
				rtnMsg.setField("WORK_COIL_MAX_CNT", 			"00");
				
				int nExistsRow = startRow;
				int nMakeId = 1;
				if(nRowCnt>0){
					
					// 조회된 갯수가 최대 갯수를 초과할때 입력받은 최대갯수만큼 
					if(nMaxRow == 50){
						if(nRowCnt>nMaxRow){
							nRowCnt = 50;
						}
					}
					 
					// 조회된 갯수가 최대 갯수를 초과할때 입력받은 최대갯수만큼 
					if(nMaxRow == 100){
						if(nRowCnt>nMaxRow){
							nRowCnt = 100;
						}
					}
					
					JDTORecord sMsgJto = null; 
					for(int i=startRow; i<nRowCnt; i++, nMakeId++){
						sMsgJto = rsCarStlInfo.getRecord(i);
						// Header부만 발췌하자.
						if(i==startRow){
							
							if("".equals(sPT_LOAD_LOC)){
								if(!"".equals( (sMsgJto.getFieldString("PT_LOAD_LOC")).trim() )){
									rtnMsg.setField("PT_LOAD_LOC", 				sMsgJto.getFieldString("PT_LOAD_LOC"));
								}
							}
							
							rtnMsg.setField("CAR_NO", 					YdUtils.fillSpZr_KOR(sMsgJto.getFieldString("CAR_NO"),15,1) );
							rtnMsg.setField("TEL_NO", 					sMsgJto.getFieldString("TEL_NO"));
							rtnMsg.setField("PT_CLS", 					sMsgJto.getFieldString("PT_CLS") );
							rtnMsg.setField("WORK_CLS", 				sMsgJto.getFieldString("WORK_CLS") );
							rtnMsg.setField("WORK_COIL_MAX_CNT", 		sMsgJto.getFieldString("WORK_COIL_MAX_CNT") );
						}
						
						rtnMsg.setField("STOCK_ID"+nMakeId, 		sMsgJto.getFieldString("STOCK_ID") );
						rtnMsg.setField("LOAD_LOC_CD"+nMakeId, 		sMsgJto.getFieldString("LOAD_LOC_CD") ); 
						rtnMsg.setField("MAT_WGT"+nMakeId, 			sMsgJto.getFieldString("MAT_WGT") );
						rtnMsg.setField("MAT_THK"+nMakeId, 			sMsgJto.getFieldString("MAT_THK") );
						rtnMsg.setField("MAT_WTH"+nMakeId, 			sMsgJto.getFieldString("MAT_WTH") );
						rtnMsg.setField("MAT_LEN"+nMakeId, 			sMsgJto.getFieldString("MAT_LEN") ); 

						rtnMsg.setField("WORK_STATE"+nMakeId, 		sMsgJto.getFieldString("WORK_STATE") );
						rtnMsg.setField("YD_CURR_BAY_GP"+nMakeId, 	sMsgJto.getFieldString("YD_CURR_BAY_GP") );
						
						nExistsRow++;
					}
				}
				// 재료총 건수와 전문상 최대 발송갯수 50개
				if( nExistsRow < nMaxRow ){
					 
					for( int i=nExistsRow; i < nMaxRow; i++){
						nMakeId++;
	 					
						rtnMsg.setField("STOCK_ID"+nMakeId, 		YdUtils.fillSpZr(" ", 11, 1) );
						rtnMsg.setField("LOAD_LOC_CD"+nMakeId,  	YdUtils.fillSpZr(" ", 2, 1));
						rtnMsg.setField("MAT_WGT"+nMakeId, 			PlateGdsYdUtil.genDecmailFomatter("00000", ""));
						rtnMsg.setField("MAT_THK"+nMakeId, 			PlateGdsYdUtil.genDecmailFomatter("000.000",""));
						rtnMsg.setField("MAT_WTH"+nMakeId, 			PlateGdsYdUtil.genDecmailFomatter("0000.0", ""));
						rtnMsg.setField("MAT_LEN"+nMakeId, 			PlateGdsYdUtil.genDecmailFomatter("0000000","")); 
						
						rtnMsg.setField("WORK_STATE"+nMakeId, 		YdUtils.fillSpZr(" ", 1, 1) );
						rtnMsg.setField("YD_CURR_BAY_GP"+nMakeId, 	YdUtils.fillSpZr(" ", 6, 1) );
					}
				}
				rtnMsg.setField("SPARE", 		YdUtils.fillSpZr(" ", 39, 1) );
			}
			
		}catch(Exception e){
			ydSlabUtils.printLog(logId, methodNm+ " 차량예정정보 생성중 오류가 발생", "ERROR");
			throw new DAOException(e);
		}
		
		return rtnMsg;
		
	}
	 
	/**
	 *      [A] 오퍼레이션명 : 차량중심좌표수신(형상정보수신)(Y9YDL029)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord inRecord
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvY9YDL029(JDTORecord inRecord) throws DAOException { 
        YdPlateCommDAO  commDao 		= new YdPlateCommDAO();
		
		JDTORecord jrRtn          = JDTORecordFactory.getInstance().create();	//전문 Return
		JDTORecordSet 	rsResult  = JDTORecordFactory.getInstance().createRecordSet("temp");
		
		String szMsg="";
		String szYD_STK_COL_GP = "";
		String szCAR_NO = "";
		String sUniqueId = "";
		String szCAR_KIND  = "";
		String szYD_CAR_SCH_ID = "";
		String szAgain_SENDYDL007_YN = "";
		
//		int szYD_EQP_WRK_SH = 0;
		int intRtnVal 			= 0 ;
		int szYD_STK_BED_L_MAX = 0;

		String logId = inRecord.getResultCode();
		String methodNm = "차량중심좌표수신[PlateYdRcvL2SeEJBBean.rcvY9YDL029] < " + inRecord.getResultMsg();
		ydSlabUtils.printLog(logId, methodNm, "S+");
				
		try {
			sUniqueId = StringUtils.trim(inRecord.getFieldString("UNIQUE_ID"));
			if("".equals(sUniqueId)){
			    sUniqueId = logId;
		    }
		
			inRecord.setResultCode(sUniqueId);
			inRecord.setResultMsg(methodNm);
	        
	        // 형상 정보 YD_적치단에 , 수신한 X,Y,Z값 등록 한다.
	        szYD_STK_COL_GP =  PlateGdsYdUtil.trim(inRecord.getFieldString("PT_LOAD_LOC"));
	        szCAR_KIND      =  PlateGdsYdUtil.trim(inRecord.getFieldString("CAR_KIND"));
			szCAR_NO        =  PlateGdsYdUtil.trim(inRecord.getFieldString("CAR_NO"));
			
			if ("".equals(szCAR_NO)){
				szMsg="차량번호이상 ";
				ydSlabUtils.printLog(logId, methodNm + szMsg, "[ERROR]");
	        	return jrRtn;
			}
			if ("".equals(szYD_STK_COL_GP)){
				szMsg="상차도 위치정보 이상 ";
				ydSlabUtils.printLog(logId, methodNm + szMsg, "[ERROR]");
	        	return jrRtn;
			}
//			if ("".equals(szCAR_KIND)){
//				szMsg="차량종류정보 이상 ";
//				ydSlabUtils.printLog(logId, methodNm + szMsg, "[ERROR]");
//	        	return jrRtn;
//			}
			
			// 상차point로 해당 설비 정보 검색
			JDTORecord jsCrnSch = JDTORecordFactory.getInstance().create();
			JDTORecord jsCrnSch1 = JDTORecordFactory.getInstance().create();
			jsCrnSch1.setField("YD_CAR_STOP_LOC", szYD_STK_COL_GP);
			jsCrnSch1.setField("CAR_NO", szCAR_NO);
			intRtnVal = commDao.select(jsCrnSch1, rsResult,"com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarSchByBayInSeqPlate1");
			if (intRtnVal <= 0) {
				szMsg =  "해당 상차 point에 작업예약이 생성되지 않았습니다.";
				ydSlabUtils.printLog(logId, methodNm + szMsg, "[ERROR]");
				throw new DAOException(szMsg);
			}
			
			szYD_CAR_SCH_ID = rsResult.getRecord(0).getFieldString("YD_CAR_SCH_ID");
			szAgain_SENDYDL007_YN = rsResult.getRecord(0).getFieldString("SENDYDL007_YN");
			// 수신한 형상정보 Set
			JDTORecord jsXY = JDTORecordFactory.getInstance().create();			
			
			jsXY.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			jsXY.setField("MODIFIER", "Y9YDL029");
			
			// 가변기차량 VS , TV
			if ("VS".equals(szCAR_KIND) || "TV".equals(szCAR_KIND))
			{
				jsXY.setField("YD_STK_BED_XAXIS" ,   PlateGdsYdUtil.trim(inRecord.getFieldString("CAR_FRONT_EDGE_XAXIS")) );
				jsXY.setField("YD_STK_BED_YAXIS" ,   PlateGdsYdUtil.trim(inRecord.getFieldString("CAR_FRONT_EDGE_YAXIS")) );
			}//팔레트
			else if ("PT".equals(szCAR_KIND))
			{
				jsXY.setField("YD_STK_BED_XAXIS" ,   PlateGdsYdUtil.trim(inRecord.getFieldString("CAR_CENTER_XAXIS")) );
				jsXY.setField("YD_STK_BED_YAXIS" ,   PlateGdsYdUtil.trim(inRecord.getFieldString("CAR_CENTER_YAXIS")) );
			}
			else {
				jsXY.setField("YD_STK_BED_XAXIS" ,   PlateGdsYdUtil.trim(inRecord.getFieldString("CAR_CENTER_XAXIS")) );//CAR_CENTER_XAXIS
				jsXY.setField("YD_STK_BED_YAXIS" ,   PlateGdsYdUtil.trim(inRecord.getFieldString("CAR_FRONT_EDGE_YAXIS")) );
			}
			//inRecord.getFielin,Integer.parseInt
			szYD_STK_BED_L_MAX =  Integer.parseInt(inRecord.getFieldString("CAR_SPEC_END_YAXIS"));
			jsXY.setField("YD_STK_BED_ZAXIS" ,   PlateGdsYdUtil.trim(inRecord.getFieldString("CAR_SPEC_ZAXIS")));
						
			// 형상정보 UPDATE
			int intRtnVal1 = commDao.update(jsXY, "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.updYdstklyrxyz");	
//			EJBConnector ejbConn = new EJBConnector("default", "PlateYdRcvL2SeEJB", this);
			//차량형상 완료 표시
			if (intRtnVal1 > 0)
			{
				JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();
				jrParam1.setResultCode(logId);	    //Log ID
	 			jrParam1.setResultMsg(methodNm);	//Log Method Name
				jrParam1.setField("YD_WBOOK_ID",  rsResult.getRecord(0).getFieldString("YD_WBOOK_ID"));
				jrParam1.setField("MODIFIER"    , "Y9YDL029"  );	//수정자			
				jrParam1.setField("YD_CTS_RELAY_YN" , "Y");	//형상완료로 인한 스케줄 기동
				int intRtnVal2 = commDao.update(jrParam1, "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.updYdWrkbookCTS");
	//			ejbConn.trx("updateTx", new Class[] { JDTORecord.class, String.class }, new Object[] { jrParam1, "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.updYdWrkbookCTS" });
	
				//차량 스케쥴에 차량type Set  220623 박성열  주석 해제
				//------------------------------------------------------------------------------------------------------------
				//	전사물류개선
				//------------------------------------------------------------------------------------------------------------
				if (intRtnVal2 > 0)
				{
					jrParam1 = JDTORecordFactory.getInstance().create();
					jrParam1.setField("TRANS_EQUIPMENT_TYPE" ,szCAR_KIND);	//형상완료로 인한 스케줄 기동
					jrParam1.setField("MODIFIER"    , "Y9YDL029"  );	//수정자		
					jrParam1.setField("YD_CAR_SCH_ID" ,szYD_CAR_SCH_ID);	//형상완료로 인한 스케줄 기동
					int intRtnVal3 = commDao.update(jrParam1, "com.inisteel.cim.yd.dao.ydcarschdao.ydCarSchDao.updYdCarTrans_Type");
		//			ejbConn.trx("updateTx", new Class[] { JDTORecord.class, String.class }, new Object[] { jsCrnSch, "com.inisteel.cim.yd.dao.ydcarschdao.ydCarSchDao.updYdCarTrans_Type" });
					
					// YD_STOCK 차량TYPE정보 Set	 220623 박성열  주석 해제	
	//				jrParam1 = JDTORecordFactory.getInstance().create();
	//				jrParam1.setField("YD_CONVEYOR_BRANCH_CD", szCAR_KIND); 
	//				jrParam1.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
	//				jrParam1.setField("MODIFIER"    , "Y9YDL029"  );	//수정자
	//				commDao.update(jrParam1, "com.inisteel.cim.yd.dao.ydstockdao.updYdCarTransType");
		//			ejbConn.trx("updateTx", new Class[] { JDTORecord.class, String.class }, new Object[] { jrParam1, "com.inisteel.cim.yd.dao.ydstockdao.updYdCarTransType" });
					
					if (intRtnVal3 > 0)
					{
						if( PlateGdsYdUtil.isApplyYn("출하차량 Y 좌표 신규로직 적용 여부") ){
							jrParam1 = JDTORecordFactory.getInstance().create();
							jrParam1.setField("MODIFIER"    , "Y9YDL029"  );	//수정자		
							jrParam1.setField("YD_STK_BED_L_MAX" ,szYD_STK_BED_L_MAX);	//형상완료 차량 전체길이? CAR_SPEC_END_YAXIS(차량제원Y끝점)값 
							jrParam1.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
							int intRtnVal4 = commDao.update(jrParam1, "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.updYdstkbmax");
							// 크레인스케쥴이생성되어 있다면 작업지시요구 
							if (intRtnVal4 > 0)
							{
								if ("Y".equals(szAgain_SENDYDL007_YN)){
									jsCrnSch = JDTORecordFactory.getInstance().create();
									jsCrnSch.setField("MSG_ID",           "YDYDJ642");
									jsCrnSch.setField("YD_EQP_ID",        rsResult.getRecord(0).getFieldString("YD_EQP_ID"));
									jsCrnSch.setField("YD_WRK_PROG_STAT", rsResult.getRecord(0).getFieldString("YD_WRK_PROG_STAT"));
									jsCrnSch.setField("YD_EQP_WRK_MODE",  "");
									jsCrnSch.setField("YD_SCH_CD",        "");
									jsCrnSch.setField("YD_CRN_SCH_ID",    "");
									jsCrnSch.setField("YD_CRN_XAXIS",     "");
									jsCrnSch.setField("YD_CRN_YAXIS",     "");
								    
								    jrRtn = this.addSndData(jrRtn, jsCrnSch);
					//				this.rcvY9YDL007(jsCrnSch);
								}
								else{ 
									
									jsCrnSch = JDTORecordFactory.getInstance().create();
									jsCrnSch.setField("JMS_TC_CD"          , "YDYDJ506");
								    jsCrnSch.setField("YD_EQP_ID"    	, "");
								    jsCrnSch.setField("YD_SCH_CD"    	, rsResult.getRecord(0).getFieldString("YD_SCH_CD"));
									jsCrnSch.setField("YD_WBOOK_ID"    	, rsResult.getRecord(0).getFieldString("YD_WBOOK_ID"));			
									
									jrRtn = this.addSndData(jrRtn, jsCrnSch);
					//				ydEjbCon = new EJBConnector("default", "CrnSchSeEJB", this); 
					//				ydEjbCon.trx("procY4CrnSchMainNEW",   new Class[] { JDTORecord.class }, new Object[] { jsCrnSch });
					//				
								}
							}
						}
						else { // 크레인스케쥴이생성되어 있다면 작업지시요구 
							if ("Y".equals(szAgain_SENDYDL007_YN)){
								jsCrnSch = JDTORecordFactory.getInstance().create();
								jsCrnSch.setField("MSG_ID",           "YDYDJ642");
								jsCrnSch.setField("YD_EQP_ID",        rsResult.getRecord(0).getFieldString("YD_EQP_ID"));
								jsCrnSch.setField("YD_WRK_PROG_STAT", rsResult.getRecord(0).getFieldString("YD_WRK_PROG_STAT"));
								jsCrnSch.setField("YD_EQP_WRK_MODE",  "");
								jsCrnSch.setField("YD_SCH_CD",        "");
								jsCrnSch.setField("YD_CRN_SCH_ID",    "");
								jsCrnSch.setField("YD_CRN_XAXIS",     "");
								jsCrnSch.setField("YD_CRN_YAXIS",     "");
							    
							    jrRtn = this.addSndData(jrRtn, jsCrnSch);
				//				this.rcvY9YDL007(jsCrnSch);
							}
							else{ 
								
								jsCrnSch = JDTORecordFactory.getInstance().create();
								jsCrnSch.setField("JMS_TC_CD"          , "YDYDJ506");
							    jsCrnSch.setField("YD_EQP_ID"    	, "");
							    jsCrnSch.setField("YD_SCH_CD"    	, rsResult.getRecord(0).getFieldString("YD_SCH_CD"));
								jsCrnSch.setField("YD_WBOOK_ID"    	, rsResult.getRecord(0).getFieldString("YD_WBOOK_ID"));			
								
								jrRtn = this.addSndData(jrRtn, jsCrnSch);
				//				ydEjbCon = new EJBConnector("default", "CrnSchSeEJB", this); 
				//				ydEjbCon.trx("procY4CrnSchMainNEW",   new Class[] { JDTORecord.class }, new Object[] { jsCrnSch });
				//				
							}
						}
					}   
				}
			}
			
			ydSlabUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(e);
		}
		return jrRtn;
	}
	
	/**
	 *      [A] 오퍼레이션명 : Transaction 분리하여 Insert, Update처리한다.
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
     */			
	public int updateTx(JDTORecord rcvMsg, String queryId) throws DAOException 
	{
		String methodNm = "Transaction 분리 수행 [PlateYdRcvL2SeEJB.updateTx] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		int intRtnVal = 0;
		JPlateYdCommDAO ydCommDao = null;
		try 
		{
			ydCommDao = new JPlateYdCommDAO();
			ydSlabUtils.printLog(logId, methodNm, "S+");
			intRtnVal = ydCommDao.update(rcvMsg, queryId);
			
			ydSlabUtils.printLog(logId, methodNm, "S-");
		}
		catch (DAOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new DAOException(ydSlabUtils.makeErrorLog(logId, methodNm, e));
		}
		
		return intRtnVal;
	}
	
	/**
	 *      [A] 오퍼레이션명 : 전송할 전문을 추가
	 *
	 *      @param JDTORecord jrExt
	 *      @param JDTORecordSet jsAdd
	 *      @return JDTORecord
	*/
	public JDTORecord addSndData(JDTORecord jrExt, JDTORecordSet jsAdd) 
	{
		try 
		{
			//추가할 전문이 없으면 기존 그대로
			if (PlateGdsYdUtil.isEmpty(jsAdd)) 
			{
				return jrExt;
			}

			//Return Data
			JDTORecordSet rtnData = JDTORecordFactory.getInstance().createRecordSet("");

			//기존 전문이 있으면 기존 먼저 추가
			if (!PlateGdsYdUtil.isEmpty(jrExt)) 
			{
				JDTORecordSet extData = (JDTORecordSet)jrExt.getField("SEND_DATA");

				if (!PlateGdsYdUtil.isEmpty(extData)) 
				{
					rtnData.addAll(extData);
				}
			}

			//추가할 전문 추가
			rtnData.addAll(jsAdd);

			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			jrRtn.addField("SEND_DATA", rtnData);

			return jrRtn;
		} 
		catch (Exception e) 
		{
			return jrExt;
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 전송할 전문을 추가
	 *
	 *      @param JDTORecord jrExt
	 *      @param JDTORecord jrAdd
	 *      @return JDTORecord
	*/
	public JDTORecord addSndData(JDTORecord jrExt, JDTORecord jrAdd) 
	{
		try 
		{
			//추가할 전문이 없으면 기존 그대로
			if (PlateGdsYdUtil.isEmpty(jrAdd)) 
			{
				return jrExt;
			}

			JDTORecordSet addData = null;

			//I/F ID를 먼저 Check
			String msgId = ydSlabUtils.getMsgId(jrAdd);

			if (!PlateGdsYdUtil.isEmpty(msgId)) 
			{
				//I/F ID가 존재할 경우는 전문 1건 추가
				addData = JDTORecordFactory.getInstance().createRecordSet("");
				addData.addRecord(jrAdd);
			} 
			else 
			{
				//SEND_DATA로 있을 경우
				addData = (JDTORecordSet)jrAdd.getField("SEND_DATA");
			}

			return addSndData(jrExt, addData);
		} 
		catch (Exception e) 
		{
			return jrExt;
		}
	}
}	
