/**
 * @(#)BSlabComm
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2017/02/02
 *
 * @description      B열연 SLAB 야드 공통 처리 EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2017/02/02   정종균      조병기      최초 등록
 * 
 */
package com.inisteel.cim.ym.bslab.session;

import java.util.Hashtable;
import java.util.Vector;

import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.log.Logger;
import jspeed.base.property.PropertyService;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.ExceptionMessageUtil;
import com.inisteel.cim.ym.bcommon.dao.YmCommDAO;
import com.inisteel.cim.ym.bcommon.util.YmCommUtils;
import com.inisteel.cim.ym.bcommon.util.YmConstant;
import com.inisteel.cim.ym.bslab.dao.BSlabDAO;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
import com.metis.rapi4j.RAPI4J;
import com.metis.rapi4j.ResultData;
import com.metis.rapi4j.RuleException;
import com.metis.rapi5j.RAPI5J;

/**
 *      [A] 클래스명 : B열연 SLAB 야드 공통 처리
 *
*/

public class BSlabComm {
 
	private YmCommUtils commUtils = new YmCommUtils();
	private YmCommDAO commDao = new YmCommDAO();	
	private BSlabDAO bSlabDao = new BSlabDAO();
	private Hashtable retTable = new Hashtable();

	private static Logger logger = new Logger("ym");
	
	/**
	 *      [A] 오퍼레이션명 : 저장품 이동 조건 (현재진도코드와 Scarfing Pattern 으로 이동조건 판단) 
	 *
	 *      @param  sCURR_PROG_CD : 현재진도코드
	 *      @param  sWO_MSLAB_RPR_MTD : Scarfing Pattern
	 *      @return String
	 *      @throws DAOException
	*/
	public String getStockMoveTerm(String sCURR_PROG_CD, String sWO_MSLAB_RPR_MTD) throws DAOException {
		
		String sSTOCK_MOVE_TERM = ""; //결과 

		try {

			if(YmConstant.CURR_PROG_CD_SLAB_0.equals(sCURR_PROG_CD)){    		
				sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_11; 
			}else if(YmConstant.CURR_PROG_CD_SLAB_1.equals(sCURR_PROG_CD)){
				sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_12;	
			}else if(YmConstant.CURR_PROG_CD_SLAB_A.equals(sCURR_PROG_CD)){
				if("Q".equals(sWO_MSLAB_RPR_MTD)){
		    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_D3;	
				}else{
					sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_DS;
				}
			}else if(YmConstant.CURR_PROG_CD_SLAB_B.equals(sCURR_PROG_CD)){
				sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_ES;
			}else if(YmConstant.CURR_PROG_CD_SLAB_C.equals(sCURR_PROG_CD)){
				sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_FS;
			}else if(YmConstant.CURR_PROG_CD_SLAB_D.equals(sCURR_PROG_CD)){
				sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_BS;
			}else if(YmConstant.CURR_PROG_CD_SLAB_E.equals(sCURR_PROG_CD)){
				sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_CS;
			}else if(YmConstant.CURR_PROG_CD_SLAB_F.equals(sCURR_PROG_CD)){
				sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_YS;
			}else if(YmConstant.CURR_PROG_CD_SLAB_G.equals(sCURR_PROG_CD)){
				sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_GS; 
			}else if(YmConstant.CURR_PROG_CD_SLAB_H.equals(sCURR_PROG_CD)){
				sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_HS; 
			}else if(YmConstant.CURR_PROG_CD_SLAB_J.equals(sCURR_PROG_CD)){
				sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_JS; 
			}else if(YmConstant.CURR_PROG_CD_SLAB_K.equals(sCURR_PROG_CD)){
				sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_KS;
			}else if(YmConstant.CURR_PROG_CD_SLAB_L.equals(sCURR_PROG_CD)){
				sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_LS;
			}else if(YmConstant.CURR_PROG_CD_SLAB_M.equals(sCURR_PROG_CD)){
				sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_MS;    		
			}else if(YmConstant.CURR_PROG_CD_SLAB_N.equals(sCURR_PROG_CD)){
				sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_NS;    	
			}else if(YmConstant.CURR_PROG_CD_SLAB_Y.equals(sCURR_PROG_CD)){
				sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_ZS;    	
			}else if(YmConstant.CURR_PROG_CD_SLAB_Z.equals(sCURR_PROG_CD)){
				sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_ZS;
			}															
			
			return sSTOCK_MOVE_TERM;
			
		} catch (DAOException e) {
			logger.println(LogLevel.ERROR, getClass().getName(), e.getMessage(), e);
			return sSTOCK_MOVE_TERM;
		} catch (Exception e) {
			logger.println(LogLevel.ERROR, getClass().getName(), e.getMessage(), e);
			return sSTOCK_MOVE_TERM;
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 저장품 이동 조건  
	 *
	 *      @param  sSLAB_NO : SLAB번호
	 *      @return String
	 *      @throws DAOException
	*/
	public String getStockMoveTerm(String sSLAB_NO) throws DAOException {
		
		JDTORecord jrParam			= null;	//Query 실행시 파라메터 전달용 JDTORecord
	    JDTORecordSet rsResult    	= null;

		String sSTOCK_MOVE_TERM = ""; //결과 
	    String sCURR_PROG_CD = ""; //현재진도코드
	    String sWO_MSLAB_RPR_MTD = ""; //Scarfing Pattern

		try {

			jrParam = JDTORecordFactory.getInstance().create();
			
			/**********************************************************
			* 2. VW_YD_SLABCOMM 에서  STL_NO로 필요정보 조회
			**********************************************************/
			jrParam.setField("SLAB_NO"	, sSLAB_NO);
			
			rsResult = bSlabDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getInitSlabInfo");
			
			if(rsResult.size() > 0) {
				sCURR_PROG_CD 		= commUtils.trim(rsResult.getRecord(0).getFieldString("CURR_PROG_CD")); //현재진도코드
				sWO_MSLAB_RPR_MTD	= commUtils.trim(rsResult.getRecord(0).getFieldString("WO_MSLAB_RPR_MTD")); //Scarfing Pattern
			}
			sSTOCK_MOVE_TERM = getStockMoveTerm(sCURR_PROG_CD, sWO_MSLAB_RPR_MTD);
			
			return sSTOCK_MOVE_TERM;
			
		} catch (DAOException e) {
			logger.println(LogLevel.ERROR, getClass().getName(), e.getMessage(), e);
			return sSTOCK_MOVE_TERM;
		} catch (Exception e) {
			logger.println(LogLevel.ERROR, getClass().getName(), e.getMessage(), e);
			return sSTOCK_MOVE_TERM;
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : B열연 SLAB 크레인작업실적 응답(YMA8L005)전문 생성 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord getYMA8L005(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "B열연 SLAB 크레인작업실적 응답 전문 생성[BCoilComm.getYMA8L005] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			//수신 항목 값
			String msgId      = ""; //전문ID
			String ydEqpId    = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"     )); //야드설비ID
			String ydL2WrGp   = commUtils.trim(rcvMsg.getFieldString("YD_L2_WR_GP"   )); //야드L2실적구분
			String ydL3HdRsCd = commUtils.trim(rcvMsg.getFieldString("YD_L3_HD_RS_CD")); //야드L3처리결과코드
			String ydL3Msg    = commUtils.trim(rcvMsg.getFieldString("YD_L3_MSG"     )); //야드L3MESSAGE

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				return null;
			}

			if (ydEqpId.startsWith("2")) {
				msgId = "YMA8L005";
			} else {
				return null;
			}

			/**********************************************************
			* 2. 크레인작업실적응답 전문 생성
			**********************************************************/
			//야드L3Message가 없으면 생성
			if ("".equals(ydL3Msg)) {
				if ("U".equals(ydL2WrGp)) {
					ydL3Msg = "권상실적";
				} else if ("D".equals(ydL2WrGp)) {
					ydL3Msg = "권하실적";
				} else if ("E".equals(ydL2WrGp)) {
					ydL3Msg = "비상조업실적";
				} else if ("R".equals(ydL2WrGp)) {
					ydL3Msg = "고장복구실적";
				} else if ("M".equals(ydL2WrGp)) {
					ydL3Msg = "운전모드전환";
				} else if ("J".equals(ydL2WrGp)) {
					ydL3Msg = "지시요구";
				} else if ("F".equals(ydL2WrGp)) {
					ydL3Msg = "강제권하";
				} else if ("G".equals(ydL2WrGp)) {
					ydL3Msg = "강제권상요구";
				} else {
					ydL3Msg = ydL2WrGp;
				}

				if ("0000".equals(ydL3HdRsCd)) {
					ydL3Msg = ydL3Msg + " 정상 처리";
				} else if ("9999".equals(ydL3HdRsCd)) {
					ydL3Msg = ydL3Msg + " 정보 없음";
				} else {
					ydL3Msg = ydL3Msg + " 오류 <" + logId + ">";
				}
			}

			StringBuffer sbMsg = new StringBuffer();

			sbMsg = sbMsg.append(msgId                                 ); //전문ID
			sbMsg = sbMsg.append(commUtils.getDateTime18()             ); //생성일,생성시간(yyyy-MM-ddHH:mm:ss)
			sbMsg = sbMsg.append("I"                                   ); //전문구분
			sbMsg = sbMsg.append("0078"                                ); //전문길이
			sbMsg = sbMsg.append(commUtils.getRPad(" "       , 29, " ")); //임시
			sbMsg = sbMsg.append(commUtils.getRPad(ydEqpId   ,  6, " ")); //야드설비ID
			sbMsg = sbMsg.append(commUtils.getRPad(commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")),  1, " ")); //야드작업진행상태
			sbMsg = sbMsg.append(commUtils.getRPad(commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )),  8, " ")); //야드스케쥴코드
			sbMsg = sbMsg.append(commUtils.getRPad(commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )), 18, " ")); //야드크레인스케쥴ID
			sbMsg = sbMsg.append(commUtils.getRPad(ydL2WrGp  ,  1, " ")); //야드L2실적구분
			sbMsg = sbMsg.append(commUtils.getRPad(ydL3HdRsCd,  4, " ")); //야드L3처리결과코드
			sbMsg = sbMsg.append(commUtils.getRPad(ydL3Msg   , 40, " ")); //야드L3Message

			JDTORecord sndMsg = JDTORecordFactory.getInstance().create();

			sndMsg.setResultCode(logId);	//Log ID
			sndMsg.setResultMsg(methodNm);	//Log Method Name
			sndMsg.addField("JMS_TC_CD"          , msgId                    ); //JMSTC코드
			sndMsg.addField("JMS_TC_CREATE_DDTT" , commUtils.getDateTime14()); //JMSTC생성일시(yyyyMMddHHmmss)
			sndMsg.addField("JMS_TC_MESSAGE"     , sbMsg.toString()         ); //JMSTCMessage

			//전송 Data Return
			return commUtils.addSndData(sndMsg);
		} catch (Exception e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return null;
		}
	}


	/**
	 *      SLAB바로 위 상단 상태정보를 '적치불가' 으로 UPDATE
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public int setSlabUpperState_X(String sStackColGp,
							   	   String sStackBedGp,
							 	   String sStackLayerGp,
							 	   String logId) throws DAOException {
		String methodNm = "B열연 SLAB 상단 상태정보를 '적치불가[BCoilComm.gsetSlabUpperState] < " ;
		 
		int iSeq = 0;
		try {

	  	    JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setField("STACK_COL_GP"    , sStackColGp);  
			jrParam.setField("STACK_BED_GP"    , sStackBedGp);  
			jrParam.setField("STACK_LAYER_GP"    , YmCommUtils.changeLayerFormat(sStackLayerGp  , "M") );  
				
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getStackLayerInfoWithPk  
			SELECT *
			  FROM TB_YM_STACKLAYER
			 WHERE STACK_COL_GP  	= :V_STACK_COL_GP
			   AND STACK_BED_GP	= :V_STACK_BED_GP 
			   AND STACK_LAYER_GP	= :V_STACK_LAYER_GP 
		    */	
			
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getStackLayerInfoWithPk", logId, methodNm, "적치대정보조회");
			if (jsChk != null &&  jsChk.size()> 0) {
				
				/**
	    		 * 상단정보 수정시에 하단이 적치가능이면('E')
	    		 * 현위치정보도 적치불가('V')로 셋팅한다.
	    		 */
	    		if("".equals(commUtils.trim(jsChk.getRecord(0).getFieldString("STOCK_ID")))){ 	
					/*
					 * 적치단 UP위치의 바로 위 상단 번지 Clear
					 * tb_ym_stacklayer Table : stock_id = ''(Empty)
					 * tb_ym_stacklayer Table : stack_layer_stat	   = 'X'(하단에 적치되지 않은 위치)
					 */	
					jrParam.setField("STOCK_ID"                  , "");								
					jrParam.setField("STACK_LAYER_STAT"          , YmConstant.STACK_LAYER_STAT_X);  	    	
					jrParam.setField("STACK_COL_GP"              , sStackColGp);  	    	
					jrParam.setField("STACK_BED_GP"              , sStackBedGp);  	    	
					jrParam.setField("STACK_LAYER_GP"            , sStackLayerGp);  	    	
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateCraneStackLayerStat 
					UPDATE TB_YM_STACKLAYER
					   SET STOCK_ID			= :V_STOCK_ID
						 , STACK_LAYER_STAT	= :V_STACK_LAYER_STAT
						 , MODIFIER         = 'SYSTEM'
					 	 , MOD_DDTT         = SYSDATE     
					 WHERE STACK_COL_GP   = :V_STACK_COL_GP 
					   AND STACK_BED_GP   = :V_STACK_BED_GP 
					   AND STACK_LAYER_GP = :V_STACK_LAYER_GP  
					*/
					iSeq = commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateCraneStackLayerStat", logId, methodNm, "적치단 수정");	
				}
			}
			jrParam.setField("STACK_LAYER_GP"    , YmCommUtils.changeLayerFormat(sStackLayerGp  , "P") );  
			
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getStackLayerInfoWithPk  
			SELECT *
			  FROM TB_YM_STACKLAYER
			 WHERE STACK_COL_GP  	= :V_STACK_COL_GP
			   AND STACK_BED_GP	= :V_STACK_BED_GP 
			   AND STACK_LAYER_GP	= :V_STACK_LAYER_GP 
			*/	
		
			JDTORecordSet jsChk1 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getStackLayerInfoWithPk", logId, methodNm, "적치대정보조회");
			if (jsChk1 != null &&  jsChk1.size()> 0) {
			
				/**
				 * 상단정보 수정시에 하단이 적치가능이면('E')
				 * 현위치정보도 적치불가('V')로 셋팅한다.
				 */
				if("".equals(commUtils.trim(jsChk1.getRecord(0).getFieldString("STOCK_ID")))){ 	
					/*
					 * 적치단 UP위치의 바로 위 상단 번지 Clear
					 * tb_ym_stacklayer Table : stock_id = ''(Empty)
					 * tb_ym_stacklayer Table : stack_layer_stat	   = 'X'(하단에 적치되지 않은 위치)
					 */	
					jrParam.setField("STOCK_ID"                  , "");								
					jrParam.setField("STACK_LAYER_STAT"          , YmConstant.STACK_LAYER_STAT_X);  	    	
					jrParam.setField("STACK_COL_GP"              , sStackColGp);  	    	
					jrParam.setField("STACK_BED_GP"              , sStackBedGp);  	    	
					jrParam.setField("STACK_LAYER_GP"            , sStackLayerGp);  	    	
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateCraneStackLayerStat 
					UPDATE TB_YM_STACKLAYER
					   SET STOCK_ID			= :V_STOCK_ID
						 , STACK_LAYER_STAT	= :V_STACK_LAYER_STAT
						 , MODIFIER         = 'SYSTEM'
					 	 , MOD_DDTT         = SYSDATE     
					 WHERE STACK_COL_GP   = :V_STACK_COL_GP 
					   AND STACK_BED_GP   = :V_STACK_BED_GP 
					   AND STACK_LAYER_GP = :V_STACK_LAYER_GP  
					*/
					iSeq = commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateCraneStackLayerStat", logId, methodNm, "적치단 수정");	
				}
			}
			//전송 Data Return
			return iSeq;
		} catch (Exception e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return -1;
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : MSLAB공통 Table 저장위치 UPDATE Transaction 분리메소드 호출 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
     */			
	public boolean updMSlabCommLocInfo(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "주편공통 저장위치 update[BSlabComm.updMSlabCommLocInfo] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
	
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			// YD_LOC : 야드구분(1)+동(1)+SPAN(1)+적치열(2)+Bed(2)+적치단(2)
			// STOCK_ID : SLAB번호 or 주편번호

			EJBConnector ejbConn1 = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
			ejbConn1.trx("updMSlabCommLocInfoTx", new Class[] { JDTORecord.class }, new Object[] { rcvMsg });
			
			commUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return true;
	}

	/**
	 *      [A] 오퍼레이션명 : MSLAB공통 Table 진도코드 UPDATE Transaction 분리메소드 호출 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
     */			
	public boolean updMSlabCommCurrProgCd(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "주편공통 진도코드 update[BSlabComm.updMSlabCommCurrProgCd] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
	
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			// STOCK_ID : SLAB번호 or 주편번호

			EJBConnector ejbConn1 = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
			ejbConn1.trx("updMSlabCommCurrProgCdTx", new Class[] { JDTORecord.class }, new Object[] { rcvMsg });
			
			commUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return true;
	}
	
	/**
	 *      [A] 오퍼레이션명 : MSLAB공통 Table 보온뱅크(BK)추출시간  UPDATE Transaction 분리메소드 호출 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
     */			
	public boolean updMSlabCommBkTimeEnd(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "주편공통 보온뱅크(BK)추출시간  update[BSlabComm.updMSlabCommBkTimeEnd] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
	
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			// YD_CRN_SCH_ID : Crane 스케줄ID

			EJBConnector ejbConn1 = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
			ejbConn1.trx("updMSlabCommBkTimeEndTx", new Class[] { JDTORecord.class }, new Object[] { rcvMsg });
			
			commUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return true;
	}
	
	/**
	 *      [A] 오퍼레이션명 : MSLAB공통 Table 보온뱅크(BK)장입시간  UPDATE Transaction 분리메소드 호출 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
     */			
	public boolean updMSlabCommBkTimeStart(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "주편공통 보온뱅크(BK)장입시간  update[BSlabComm.updMSlabCommBkTimeStart] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
	
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			// YD_CRN_SCH_ID : Crane 스케줄ID

			EJBConnector ejbConn1 = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
			ejbConn1.trx("updMSlabCommBkTimeStartTx", new Class[] { JDTORecord.class }, new Object[] { rcvMsg });
			
			commUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return true;
	}	
	
	/**
	 *      [A] 오퍼레이션명 : SLAB공통 Table 보온뱅크(BK)장입시간  UPDATE Transaction 분리메소드 호출 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
     */			
	public boolean updSlabCommBkTimeStart(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "SLAB공통 보온뱅크(BK)장입시간  update[BSlabComm.updSlabCommBkTimeStart] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
	
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			// YD_CRN_SCH_ID : Crane 스케줄ID

			EJBConnector ejbConn1 = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
			ejbConn1.trx("updSlabCommBkTimeStartTx", new Class[] { JDTORecord.class }, new Object[] { rcvMsg });
			
			commUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return true;
	}	
	
	/**
	 *      [A] 오퍼레이션명 : SLAB공통 Table 저장위치 UPDATE Transaction 분리메소드 호출 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
     */			
	public boolean updSlabCommLocInfo(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "SLAB공통 저장위치 update[BSlabComm.updSlabCommLocInfo] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
	
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			// YD_LOC : 야드구분(1)+동(1)+SPAN(1)+적치열(2)+Bed(2)+적치단(2)
			// STOCK_ID : SLAB번호 or 주편번호

			EJBConnector ejbConn1 = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
			ejbConn1.trx("updSlabCommLocInfoTx", new Class[] { JDTORecord.class }, new Object[] { rcvMsg });
			
			commUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return true;
	}

	/**
	 *      [A] 오퍼레이션명 : SLAB공통 Table 진도코드 UPDATE Transaction 분리메소드 호출 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
     */			
	public boolean updSlabCommCurrProgCd(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "SLAB공통 진도코드 update[BSlabComm.updSlabCommCurrProgCd] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
	
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			// STOCK_ID : SLAB번호 or 주편번호

			EJBConnector ejbConn1 = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
			ejbConn1.trx("updSlabCommCurrProgCdTx", new Class[] { JDTORecord.class }, new Object[] { rcvMsg });
			
			commUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return true;
	}
	
	/**
	 *      [A] 오퍼레이션명 : 작업예약 INSERT Transaction 분리메소드 호출 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
     */			
	public boolean insWrkBook(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "작업예약 INSERT[BSlabComm.insWrkBook] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
	
		try {
			commUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbConn1 = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
			ejbConn1.trx("insWrkBookTx", new Class[] { JDTORecord.class }, new Object[] { rcvMsg });
			
			commUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return true;
	}	
	
	/**
	 *      [A] 오퍼레이션명 : 작업예약재료 INSERT Transaction 분리메소드 호출 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
     */			
	public boolean insWrkBookMtl(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "작업예약재료 INSERT[BSlabComm.insWrkBookMtl] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
	
		try {
			commUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbConn1 = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
			ejbConn1.trx("insWrkBookMtlTx", new Class[] { JDTORecord.class }, new Object[] { rcvMsg });
			
			commUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return true;
	}
	
	/**
	 *      [A] 오퍼레이션명 : 적치단(TB_YM_STACKLAYER) INSERT Transaction 분리메소드 호출 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
     */			
	public boolean insStackLayer(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "적치단(TB_YM_STACKLAYER) INSERT[BSlabComm.insStackLayer] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
	
		try {
			commUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbConn1 = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
			ejbConn1.trx("insStackLayerTx", new Class[] { JDTORecord.class }, new Object[] { rcvMsg });
			
			commUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return true;
	}		

	/**
	 *      [A] 오퍼레이션명 : INSERT,UPDATE Transaction 분리메소드 호출 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
     */			
	public boolean execQueryId(JDTORecord rcvMsg,String queryId) throws DAOException {
		String methodNm = "INSERT,UPDATE Transaction 분리메소드 호출[BSlabComm.execQueryId] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
	
		try {
			commUtils.printLog(logId, methodNm, "S+");

			EJBConnector ejbConn1 = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
			ejbConn1.trx("execQueryIdTx", new Class[] { JDTORecord.class, String.class }, new Object[] { rcvMsg, queryId });
			
			commUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return true;
	}		
	
	
	/**
	 *      [A] 오퍼레이션명 : W/B 적치단 정보를 One Pitch Shift 한다. 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
     */			
	public boolean shiftWbLayer(String sSTACK_COL_GP, String sSTACK_BED_GP, String sSTACK_LAYER_GP, String logId, String mthdNm, String modifier) throws DAOException {
		String methodNm = "W/B 적치단 정보 One Pitch Shift[BSlabComm.shiftWbLayer] < " + mthdNm;
		
	
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			String sSTOCK_ID = null;
			String sSTACK_LAYER_STAT = null;
			String sSTACK_LAYER_ACTIVE_STAT = null;
			String sNEXT_BED_GP = null;
			
			JDTORecordSet rsResult 	= null;
			JDTORecord jrParam	 	= JDTORecordFactory.getInstance().create(); //Query 실행시 파라메터 전달용 JDTORecord
			jrParam.setField("MODIFIER", modifier); //수정자
			jrParam.setResultCode(logId);	//Logging 을 위한 ID
			jrParam.setResultMsg(methodNm);	//상위 Method 명
			
			//W/B의 sSTACK_BED_GP, sSTACK_LAYER_GP 의 적치정보를 읽어온다.
			jrParam.setField("STACK_COL_GP"		, sSTACK_COL_GP); 
			jrParam.setField("STACK_BED_GP"		, sSTACK_BED_GP); 
			jrParam.setField("STACK_LAYER_GP"	, sSTACK_LAYER_GP); 
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getStockIdByLoc
			SELECT  STOCK_ID
			       ,STACK_LAYER_STAT
			       ,STACK_LAYER_ACTIVE_STAT
			       ,TRIM(TO_CHAR(STACK_BED_GP + 1,'00')) AS NEXT_BED_GP
			  FROM  TB_YM_STACKLAYER
			 WHERE  STACK_COL_GP = :V_STACK_COL_GP
			   AND  STACK_BED_GP = :V_STACK_BED_GP
			   AND  STACK_LAYER_GP LIKE :V_STACK_LAYER_GP || '%' */
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getStockIdByLoc", logId, methodNm, "W/B 지정위치 정보 조회 "); 
			if(rsResult.size() > 0) {
				
				sSTOCK_ID 				 = rsResult.getRecord(0).getFieldString("STOCK_ID");	
				sSTACK_LAYER_STAT 		 = rsResult.getRecord(0).getFieldString("STACK_LAYER_STAT");	
				sSTACK_LAYER_ACTIVE_STAT = rsResult.getRecord(0).getFieldString("STACK_LAYER_ACTIVE_STAT");	
				sNEXT_BED_GP 			 = rsResult.getRecord(0).getFieldString("NEXT_BED_GP");	
				
				//W/B의 sNEXT_BED_GP, sSTACK_LAYER_GP 의 적치정보를 설정한다.
				jrParam.setField("STOCK_ID"					, sSTOCK_ID); 
				jrParam.setField("STACK_LAYER_STAT"			, sSTACK_LAYER_STAT); 
				jrParam.setField("STACK_LAYER_ACTIVE_STAT"	, sSTACK_LAYER_ACTIVE_STAT); 
				jrParam.setField("STACK_COL_GP"				, sSTACK_COL_GP); 
				jrParam.setField("STACK_BED_GP"				, sNEXT_BED_GP); 
				jrParam.setField("STACK_LAYER_GP"			, sSTACK_LAYER_GP); 
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLyrByLoc
				UPDATE TB_YM_STACKLAYER
				   SET 
				       MODIFIER = :V_MODIFIER
				      ,MOD_DDTT = SYSDATE
				      ,STOCK_ID = :V_STOCK_ID
				      ,STACK_LAYER_STAT = :V_STACK_LAYER_STAT
				      ,STACK_LAYER_ACTIVE_STAT = :V_STACK_LAYER_ACTIVE_STAT
				 WHERE  STACK_COL_GP = :V_STACK_COL_GP
				   AND  STACK_BED_GP = :V_STACK_BED_GP
				   AND  STACK_LAYER_GP = :V_STACK_LAYER_GP  */
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLyrByLoc", logId, methodNm, "W/B 지정위치 정보 설정 ");

				if(!"".equals(sSTOCK_ID)) {
					
					//주편공통 진행 상태가 진행중(2)인 경우 주편공통을 update 
					jrParam.setField("RECORD_PROG_STAT"	, "2"); 
					jrParam.setField("MSLAB_NO"			, sSTOCK_ID); 
					/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getMSlabByRecordProgStat 
					SELECT MSLAB_NO
					  FROM TB_PT_MSLABCOMM
					 WHERE RECORD_PROG_STAT = :V_RECORD_PROG_STAT --진행중:2
					   AND MSLAB_NO= :V_MSLAB_NO */
					rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getMSlabByRecordProgStat", logId, methodNm, "주편공통에 레코드상태가 진행중(2)인지 확인 "); 
					if(rsResult.size() > 0) {
						//주편공통의 LOC 정보를 Transaction 분리 하여 변경처리 한다.
						jrParam.setField("STOCK_ID"	, sSTOCK_ID);
						jrParam.setField("YD_LOC"	, sSTACK_COL_GP + sNEXT_BED_GP + sSTACK_LAYER_GP);
						this.updMSlabCommLocInfo(jrParam);
					}
					
					//SLAB공통이 존재 하는 경우 SLAB공통 update 
					jrParam.setField("SLAB_NO"	, sSTOCK_ID); 
					/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabNoBySlabNo 
					SELECT SLAB_NO
					  FROM TB_PT_SLABCOMM
					 WHERE SLAB_NO= :V_SLAB_NO */
					rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabNoBySlabNo", logId, methodNm, "SLAB공통에 존재하는지 확인 "); 
					if(rsResult.size() > 0) {
						//SLAB공통의 LOC 정보를 Transaction 분리 하여 변경처리 한다.
						jrParam.setField("STOCK_ID"	, sSTOCK_ID);
						jrParam.setField("YD_LOC"	, sSTACK_COL_GP + sNEXT_BED_GP + sSTACK_LAYER_GP);
						this.updSlabCommLocInfo(jrParam);
					}
				}
			}			
		
			commUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return true;
	}
	
	/**
	 *      [A] 오퍼레이션명 : 생산통제장입실적 전문 생성
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord makeYDCTJ032(String szSTOCK_ID, String szCHG_SUP_PROG_STAT, String logId)throws DAOException  {
		String methodNm = "장입진행실적(YDCTJ032)전문 생성[BSlabComm.makeYDCTJ032] ";
	    
	    JDTORecord jrParam			= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord 
		JDTORecord jrTemp			= null;
		
	    try{
			commUtils.printLog(logId, methodNm, "S+");
			
			////////////////////////////////////////////////////////////////////////////////////////
			jrTemp = JDTORecordFactory.getInstance().create();

			jrTemp.setResultCode(logId);	//Log ID
			jrTemp.setResultMsg(methodNm);	//Log Method Name
			jrTemp.setField("JMS_TC_CD"				, "YDCTJ032");
			jrTemp.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
			jrTemp.setField("PTOP_PLNT_GP"			, "HB");
			jrTemp.setField("STL_APPEAR_GP"			, "C");
			jrTemp.setField("CHG_SUP_PROG_STAT"		, szCHG_SUP_PROG_STAT);
			jrTemp.setField("WR_OCCR_DT"			, commUtils.getDateTime14());
			jrTemp.setField("YD_EQP_WR_CNT"			, "1");
			jrTemp.setField("STL_NO1"				, szSTOCK_ID); 
			////////////////////////////////////////////////////////////////////////////////////////
			
			commUtils.printLog(logId, methodNm, "S-");
			return jrTemp;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} //end of makeYDCTJ032()		
	
	 /**
	 *
	 **import com.metis.rapi4j.*; 
	 **import java.util.*; 
	 * item코드허용값 :
	 * @작성 날짜: (2009-07-10 11:46:19)
	 * @param	scarfing_pattern	주편손질방법
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 * @return 정상처리 여부
	 */
	public JDTORecord getRuleQMB518(String scarfing_pattern) throws DAOException {
		JDTORecord jrReturn = JDTORecordFactory.getInstance().create();

		try{

			String sRtnCol [] = new String[] {
				
					 "SCARFING_ORD_DEEF"	
					,"SCARFING_ORD_TEMP"
					,"SCARFING_ORD_UP"
					,"SCARFING_ORD_DOWN"		
					,"SCARFING_ORD_LEFT"	
					,"SCARFING_ORD_RIGHT"	
					,"SCARFING_ORD_LEFT_CORNER"	
					,"SCARFING_ORD_RIGHT_CORNER"	
	
//					 *			<li>QMB518[0] :Scarfing깊이
//					 *			<li>QMB518[1] :Scarfing온도
//					 *			<li>QMB518[2] :Scarfing지시상
//					 *			<li>QMB518[3] :Scarfing지시하
//					 *			<li>QMB518[4] :Scarfing지시좌
//					 *			<li>QMB518[5] :Scarfing지시우
//					 *			<li>QMB518[6] :Scarfing지시좌Corner
//					 *			<li>QMB518[7] :Scarfing지시우Corner
			};
			
			// BRE 호출 
			PropertyService jprop = PropertyService.getInstance();			
			String javaVersion = jprop.getProperty("cm.properties","java.version");
			
			if("1.8".equals(javaVersion)){ 
				this.QMB518_NEW(retTable,scarfing_pattern);
			}else{
				this.QMB518(retTable,scarfing_pattern);
			}
			
			jrReturn= this.convToJDTORecord("QMB518", sRtnCol, retTable);
			
		
		} catch(Exception e) {
			logger.println(LogLevel.ERROR, this, ExceptionMessageUtil.getStackTrace(e)) ;
			throw new DAOException(e); 
		}
		
		return jrReturn;
		
	}
	
	/**
	 *
	 **import com.metis.rapi4j.*; 
	 **import java.util.*; 
	 * item코드허용값 :
	 * @작성 날짜: (2009-07-10 11:46:19)
	 * @param	item1	주편손질방법
	 * @param	table 인수값 혹은 결과값(리턴정보)
	 *		<ul>
	 *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용A, ....
	 *			<li>QMB518[0] :Scarfing깊이
	 *			<li>QMB518[1] :Scarfing온도
	 *			<li>QMB518[2] :Scarfing지시상
	 *			<li>QMB518[3] :Scarfing지시하
	 *			<li>QMB518[4] :Scarfing지시좌
	 *			<li>QMB518[5] :Scarfing지시우
	 *			<li>QMB518[6] :Scarfing지시좌Corner
	 *			<li>QMB518[7] :Scarfing지시우Corner
	 *			<li>QMB518_ColCnt :8
	 *		<ul>
	 * @return 정상처리 여부
	 */
	    public boolean QMB518(Hashtable table,
	                        String item1 // 주편손질방법
	                      ) throws RuleException {     
	        Vector vt = new Vector();
	        int rc = 0;

	        RAPI4J  RCaller=null;
	        try {
	           RCaller = new RAPI4J( false,  "" );
	            RCaller.Initialize("QMB518");
	            /* 사용자 입력값 설정 시작 */ 
	            RCaller.AddItemCount(1); 
	            RCaller.AddItemString( item1);
	            /* 사용자 입력값 설정  */ 
	            if (!RCaller.MBRS_Call(2)){                                                    						
	               	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
	            }																		 									
	            byte resColTypes[] = new byte[RCaller.getColCount()];                     
	            for (int j = 0; j < RCaller.getColCount(); j++) {                            				
	                resColTypes[j] = RCaller.getInBuffer().ReadByte();                         
	            }                     																						
	            ResultData    result=new ResultData();	
	            result.setRowCol(RCaller.getRowCount() , RCaller.getColCount()); 
	            for (int i = 0; i < RCaller.getRowCount(); i++) { 
	                //System.out.println("  ROW[" + i + "] COL[1]:"+ RCaller.ReadString());	//Scarfing깊이
	                result.add( i ,RCaller.ReadString() );	//Scarfing깊이
	                //System.out.println("  ROW[" + i + "] COL[2]:"+ RCaller.ReadInt() );	//Scarfing온도
	                result.add(  i ,new Integer(RCaller.ReadInt()) );	//Scarfing온도
	                //System.out.println("  ROW[" + i + "] COL[3]:"+ RCaller.ReadString());	//Scarfing지시상
	                result.add( i ,RCaller.ReadString() );	//Scarfing지시상
	                //System.out.println("  ROW[" + i + "] COL[4]:"+ RCaller.ReadString());	//Scarfing지시하
	                result.add( i ,RCaller.ReadString() );	//Scarfing지시하
	                //System.out.println("  ROW[" + i + "] COL[5]:"+ RCaller.ReadString());	//Scarfing지시좌
	                result.add( i ,RCaller.ReadString() );	//Scarfing지시좌
	                //System.out.println("  ROW[" + i + "] COL[6]:"+ RCaller.ReadString());	//Scarfing지시우
	                result.add( i ,RCaller.ReadString() );	//Scarfing지시우
	                //System.out.println("  ROW[" + i + "] COL[7]:"+ RCaller.ReadString());	//Scarfing지시좌Corner
	                result.add( i ,RCaller.ReadString() );	//Scarfing지시좌Corner
	                //System.out.println("  ROW[" + i + "] COL[8]:"+ RCaller.ReadString());	//Scarfing지시우Corner
	                result.add( i ,RCaller.ReadString() );	//Scarfing지시우Corner
	            } 
	            table.put("QMB518_ColCnt", new Integer(resColTypes.length));					
	            table.put("QMB518", result);																					
	            if (result.size() == 0) {																								
	                table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");						
	            }																															
	            return true;																										
	        } catch (Exception e) {																						
	               	throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
	        }																																						
	         																																
	    }	
	    
	    
	    /**
	    *
	    import com.metis.rapi5j.*; 
	    import java.util.*; 
	    * item코드허용값 :
	    * @작성 날짜: (2022-03-17 10:13:55)
	    * @param	item1	주편손질방법
	    * @param	table 인수값 혹은 결과값(리턴정보)
	    *		<ul>
	    *			<li>CHECK_VAL : 프로세스 중간에 발생하는 오류 확인용, ....
	    *			<li>QMB518[0] :Scarfing깊이
	    *			<li>QMB518[1] :Scarfing온도
	    *			<li>QMB518[2] :Scarfing지시상
	    *			<li>QMB518[3] :Scarfing지시하
	    *			<li>QMB518[4] :Scarfing지시좌
	    *			<li>QMB518[5] :Scarfing지시우
	    *			<li>QMB518[6] :Scarfing지시좌Corner
	    *			<li>QMB518[7] :Scarfing지시우Corner
	    *			<li>QMB518_ColCnt :8
	    *		<ul>
	    * @return 정상처리 여부
	    */
	       public boolean QMB518_NEW(Hashtable table,
	                           String item1 // 주편손질방법
	                         ) throws RuleException {     
	           Vector vt = new Vector();
	           int rc = 0;

	           RAPI5J  RCaller=new RAPI5J() ;
	           RCaller.Initialize("QMB518");
	           /* 사용자 입력값 설정 시작 */ 
	           RCaller.AddItemCount(1); 
	           RCaller.AddItemString( item1);
	           /* 사용자 입력값 설정  */ 
	          try{                                                    						
	               RCaller.MBRS_Run();                                                      
	               ResultData    result=new ResultData();	
	               result.setRowCol(RCaller.getRowCount() , RCaller.getColCount()); 
	               for (int i = 0; i < RCaller.getRowCount(); i++) { 
	                   result.add( i , RCaller.ReadString() );	//Scarfing깊이
	                   result.add(  i ,new Integer(RCaller.ReadInt()) );	//Scarfing온도
	                   result.add( i , RCaller.ReadString() );	//Scarfing지시상
	                   result.add( i , RCaller.ReadString() );	//Scarfing지시하
	                   result.add( i , RCaller.ReadString() );	//Scarfing지시좌
	                   result.add( i , RCaller.ReadString() );	//Scarfing지시우
	                   result.add( i , RCaller.ReadString() );	//Scarfing지시좌Corner
	                   result.add( i , RCaller.ReadString() );	//Scarfing지시우Corner
	               } 
	               table.put("QMB518_ColCnt", new Integer( RCaller.getColCount() ));					
	               table.put("QMB518", result);																					
	               if (result.size() == 0) {
	                   table.put("CHECK_VAL", "호출한 Rule 정보가 없습니다.");
	               }																															
	               return true;																										
	           } catch (Exception e) {																						
	                throw new RuleException(RCaller.getErrorCode(),RCaller.getErrorMessage());
	           }																																						
	       }  ;   

	    
	    
		public JDTORecord convToJDTORecord(String sRullID, String sRtnCol[],  Hashtable srcTable ) throws DAOException {
			JDTORecord jrReturn = JDTORecordFactory.getInstance().create();

			try{

				
				ResultData rData = (ResultData)srcTable.get(sRullID);
				
				for (int jj = 0 ; jj < rData.getColumnCount(); jj++) {
					
					if (jj > sRtnCol.length ) {
						jrReturn.setField(jj + "" , rData.get(0, jj));
					} else {
						jrReturn.setField(sRtnCol[jj], rData.get(0, jj));
					}
				}
				
				if ( rData.size() > 0) {
					jrReturn.setResultCode("SUCCESS") ; 
				} else {
					jrReturn.setResultCode("FAILURE") ;
					jrReturn.setResultMsg((String)srcTable.get("CHECK_VAL")) ; 
				}
				
				return jrReturn;
			
			} catch(Exception e) {
				logger.println(LogLevel.ERROR, this, ExceptionMessageUtil.getStackTrace(e)) ;
				throw new DAOException(e); 
				
			}
			
			
		}
		 
		//2018년 2월 9일 크레인주행금지구간 I/F을 위한 크레인작업실적 응답(I/F) java처리
		/**
		 *      [A] 오퍼레이션명 : B열연크레인주행금지구간작업실적응답(getYMA8L005_recv) 전문 조회
		 *
		 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 *      @param JDTORecord rcvMsg
		 *      @return JDTORecord
		 *      @throws DAOException
		*/
		public JDTORecord getYMA8L005_recv(JDTORecord rcvMsg) throws DAOException {
			String methodNm = "크레인주행금지구간작업실적응답 조회[BCoilComm.getYMA8L005_recv] < " + rcvMsg.getResultMsg();
			String logId = rcvMsg.getResultCode();
			
			try {
				//수신 항목 값
				String msgId      = "YMA8L005"; //전문ID
				
				String ydL3HdRsCd = commUtils.trim(rcvMsg.getFieldString("YD_L3_HD_RS_CD"));//야드L3처리결과코드
				String ydL3Msg    = "";														//야드L3처리결과메세지
				
				String ydBayGP = commUtils.trim(rcvMsg.getFieldString("BAY_GP"));
				String ydRepA     = commUtils.trim(rcvMsg.getFieldString("A"        ));//A동 대표크레인
				String ydRepB     = commUtils.trim(rcvMsg.getFieldString("B"        ));//B동 대표크레인
				String ydRepC     = commUtils.trim(rcvMsg.getFieldString("C"        ));//C동 대표크레인
				String ydRepD     = commUtils.trim(rcvMsg.getFieldString("D"        ));//D동 대표크레인
				String ydRepE     = commUtils.trim(rcvMsg.getFieldString("E"        ));//E동 대표크레인
				
				
				
				if ("0000".equals(ydL3HdRsCd)) {
					ydL3Msg = ydL3Msg + "주행금지구역 설정 처리완료";
				} /*else if ("9999".equals(ydL3HdRsCd)) {
					ydL3Msg = ydL3Msg + "주행거리오류발생";
				}*/ else {
					ydL3Msg = ydL3Msg + " 오류 <" + logId + ">";
				}
				
				
				/**********************************************************
				* 1. 수신 항목 값 Check
				**********************************************************/
				// 없음
				
				
				/**********************************************************
				* 2. 크레인작업실적응답 전문 생성
				**********************************************************/
				//야드L3Message가 없으면 생성
				

				StringBuffer sbMsg = new StringBuffer();

				sbMsg = sbMsg.append(msgId                                      ); //전문ID
				sbMsg = sbMsg.append(commUtils.getDateTime18()                  ); //생성일,생성시간(yyyy-MM-ddHH:mm:ss)
				sbMsg = sbMsg.append("I"                                        ); //전문구분
				sbMsg = sbMsg.append("0078"                                     ); //전문길이
				sbMsg = sbMsg.append(commUtils.getRPad(" "	     , 29, " ")     ); //임시
				if("A".equals(ydBayGP)){
					sbMsg = sbMsg.append(commUtils.getRPad(ydRepA	     ,  6, " ")     ); //A동 대표크레인
				}else if("B".equals(ydBayGP)){
					sbMsg = sbMsg.append(commUtils.getRPad(ydRepB	     ,  6, " ")     ); //B동 대표크레인
				}else if("C".equals(ydBayGP)){
					sbMsg = sbMsg.append(commUtils.getRPad(ydRepC	     ,  6, " ")     ); //C동 대표크레인
				}else if("D".equals(ydBayGP)){
					sbMsg = sbMsg.append(commUtils.getRPad(ydRepD	     ,  6, " ")     ); //D동 대표크레인
				}else if("E".equals(ydBayGP)){
					sbMsg = sbMsg.append(commUtils.getRPad(ydRepE	     ,  6, " ")     ); //E동 대표크레인
				}
				
				sbMsg = sbMsg.append(commUtils.getRPad(" "       ,  1, " ")     ); //야드작업진행상태
				sbMsg = sbMsg.append(commUtils.getRPad(" "       ,  8, " ")     ); //야드스케쥴코드
				sbMsg = sbMsg.append(commUtils.getRPad(" "       , 18, " ")     ); //야드크레인스케쥴ID
				sbMsg = sbMsg.append(commUtils.getRPad("X"       ,  1, " ")     ); //야드L2실적구분
				sbMsg = sbMsg.append(commUtils.getRPad(ydL3HdRsCd,  4, " ")     ); //야드L3처리결과코드
				sbMsg = sbMsg.append(commUtils.getRPad(ydL3Msg   , 40, " ")     ); //야드L3Message

				JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();

				sndL2Msg.setResultCode(logId);		//Log ID
				sndL2Msg.setResultMsg(methodNm);	//Log Method Name
				sndL2Msg.addField("JMS_TC_CD"          , msgId                    ); //JMSTC코드
				sndL2Msg.addField("JMS_TC_CREATE_DDTT" , commUtils.getDateTime14()); //JMSTC생성일시(yyyyMMddHHmmss)
				sndL2Msg.addField("JMS_TC_MESSAGE"     , sbMsg.toString()         ); //JMSTCMessage

				//전송 Data Return
				return commUtils.addSndData(sndL2Msg);
			} catch (Exception e) {
				commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
				return null;
			}
		}//End getYMA8L005New	
		
		
		/**
		 *      [A] 오퍼레이션명 : 고도화 로그 전문 생성
		 *
		 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 *      @param JDTORecord rcvMsg
		 *      @return JDTORecord
		 *      @throws DAOException
		*/
		public JDTORecord makeYMYMJ204(String szMSG, String szCONTENTS, String szTYPE, String szTYPE_CONTENTS, String logId)throws DAOException  {
			String methodNm = "고도화 로그(YMYMJ204)전문 생성[BSlabComm.makeYMYMJ204] ";
		    
			JDTORecord jrTemp			= null;
			
		    try{
				commUtils.printLog(logId, methodNm, "S+");
				
				if(szMSG.length()>100) {
					szMSG = szMSG.substring(0,100);
				}
				if(szCONTENTS.length()>100) {
					szCONTENTS = szCONTENTS.substring(0,100);
				}
				if(szTYPE.length()>1) {
					szTYPE = szTYPE.substring(0,1);
				}
				if(szTYPE_CONTENTS.length()>100) {
					szTYPE_CONTENTS = szTYPE_CONTENTS.substring(0,100);
				}
				
				////////////////////////////////////////////////////////////////////////////////////////
				jrTemp = JDTORecordFactory.getInstance().create();

				jrTemp.setResultCode(logId);	//Log ID
				jrTemp.setResultMsg(methodNm);	//Log Method Name
				jrTemp.setField("JMS_TC_CD"				, "YMYMJ204");
				jrTemp.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
				jrTemp.setField("YD_GP"					, "2");
				jrTemp.setField("TC_CD"					, "YMYM204");
				jrTemp.setField("CONTENTS"				, szCONTENTS);
				jrTemp.setField("MSG"					, szMSG);
				jrTemp.setField("TYPE"					, szTYPE);
				jrTemp.setField("TYPE_CONTENTS"			, szTYPE_CONTENTS); 
				////////////////////////////////////////////////////////////////////////////////////////
				
				commUtils.printLog(logId, methodNm, "S-");
				return jrTemp;
				
			} catch (DAOException e) {
				throw e;
			} catch (Exception e) {
				throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
			}
		} //end of makeYMYMJ204()		
		
}