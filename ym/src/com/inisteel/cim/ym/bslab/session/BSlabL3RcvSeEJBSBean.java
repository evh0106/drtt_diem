/**
 * @(#)BSlabL3RcvSeEJBSBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2017/02/02
 *
 * @description      B열연 SLAB 야드 L3 수신 처리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2017/02/02   정종균      조병기      최초 등록
 * 
 */
package com.inisteel.cim.ym.bslab.session;

import java.util.List;

import xlib.cmc.GridData;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.plate.PlateGdsYdUtil;
import com.inisteel.cim.ym.bcommon.dao.YmCommDAO;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.bcommon.util.YmCommUtils;
import com.inisteel.cim.ym.bslab.dao.BSlabDAO;
import com.inisteel.cim.ym.bcommon.util.YmConstant;
import com.inisteel.cim.ym.common.ModelWarning;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ym.common.YmCommonUtil;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;


/**
 *      [A] 클래스명 : B열연 SLAB 야드 L3수신 처리
 *
 * @ejb.bean name="BSlabL3RcvSeEJB" jndi-name="BSlabL3RcvSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
*/

public class BSlabL3RcvSeEJBSBean extends BaseSessionBean {
	
	private static final long serialVersionUID = 1L;
	private YmCommUtils commUtils = new YmCommUtils();
	private YmCommDAO commDao = new YmCommDAO();
	private BSlabDAO bSlabDao = new BSlabDAO();
	private BSlabComm bSlabComm = new BSlabComm();
	private YmComm ymComm = new YmComm();

	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}	
	
	/**
	 *      [A] 오퍼레이션명 : 1열연조업-슬라브전단실적(POYMJ005) 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPOYMJ005(JDTORecord rcvMsg)throws DAOException  {
		
		String methodNm = "1열연조업-슬라브전단실적[BSlabL3RcvSeEJB.rcvPOYMJ005] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
	
	    String szMsg           		= "";

	    JDTORecordSet rsResult    	= null;
	    
		JDTORecord sndRecord		= JDTORecordFactory.getInstance().create();
	    JDTORecord jrParam			= null;	//Query 실행시 파라메터 전달용 JDTORecord 
		JDTORecord jrTemp			= null; //임시  JDTORecord 
		
	    String sSLAB_NO;	//슬라브번호
	    String sPROCESS_ID; //처리구분
	    String sYD_GP; 		//야드구분
	    
        String msgId = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
        String modifier = msgId;
        
        if(msgId == null || msgId.equals("")) {
        	return sndRecord;
        }
		
	    try{

			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId + " 1열연조업-슬라브전단실적(POYMJ005) 수신 ", rcvMsg);

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			sSLAB_NO 	= commUtils.trim(rcvMsg.getFieldString("SlabNo")); 	//슬라브번호
			sPROCESS_ID = commUtils.trim(rcvMsg.getFieldString("ProcessID")); 	//처리구분
			sYD_GP		= commUtils.trim(rcvMsg.getFieldString("yardID")); 	//야드구분
			
			if("".equals(sSLAB_NO)) {
				throw new Exception("슬라브번호 정보가 없습니다.");
			}
			if(sSLAB_NO.length() > 11 ) {
				throw new Exception("슬라브번호의 길이가 11보다 작아야 합니다.");
			}
			
			/////////////////////////////////////////////////////////////////////////////////////
			//박판열연 신규모듈 적용여부 체크
			if("0".equals(sYD_GP))
			{
				JDTORecord jrResult =  commDao.getYfNewModuleEffYn();
				String sASLAB_EFF_YN = commUtils.nvl(jrResult.getFieldString("ASLAB_EFF_YN"),"N");
				String sACOIL_EFF_YN = commUtils.nvl(jrResult.getFieldString("ACOIL_EFF_YN"),"N");

				szMsg = "YmCommDAO.getYfNewModuleEffYn()---[[[ A열연SLAB야드신규적용:" + sASLAB_EFF_YN + " ,A열연COIL야드신규적용:" + sACOIL_EFF_YN + " ]]]---";
				commUtils.printLog(logId, methodNm + szMsg , "SL");
				
				if("Y".equals(sASLAB_EFF_YN))
				{
					EJBConnector ejbCon = new EJBConnector("default", "YfRcvFaEJB", this);
					ejbCon.trx("rcvMessage", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
					
					return sndRecord;
				}
			}
			/////////////////////////////////////////////////////////////////////////////////////
			
			if("7".equals(sPROCESS_ID)) { //7:모 슬라브 종료처리
				
				szMsg="["+methodNm+"] PROCESS_ID가 7(:모 슬라브 종료)일 경우.. ";
				commUtils.printLog(logId, szMsg, "SL");
				
				jrParam = JDTORecordFactory.getInstance().create();
				
				/**********************************************************
				* 2. TB_YM_STACKLAYER의 적치상태를 적치가능으로 변경
				**********************************************************/
				jrParam.setField("STOCK_ID"			, sSLAB_NO );
				jrParam.setField("MODIFIER"			, modifier );
				
				bSlabDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updClrLyrByStockId", logId, methodNm, "TB_YM_STACKLAYER에 SLAB_NO가 위치하는 단을 CLEAR");
				
				/**********************************************************
				* 3. TB_YM_STOCK의 DEL_YN = 'Y' 설정
				**********************************************************/
				jrParam.setField("STOCK_ID"			, sSLAB_NO );
				jrParam.setField("MODIFIER"			, modifier );
				jrParam.setField("DEL_YN"			, "Y" ); //모슬라브 삭제
				
				bSlabDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStockDelYn", logId, methodNm, "저장품id로 TB_YM_STOCK의 DEL_YN = 'Y' 설정");
				
				if(!"0".equals(sYD_GP)) {
					
					szMsg="["+methodNm+"] A열연 SLAB야드가 아닐경우 저장품제원정보(YMA8L002) L2 송신 ";
					commUtils.printLog(logId, szMsg, "SL");
					
					/**********************************************************
					* 4. 저장품제원정보(YMA8L002)  L2 송신
					**********************************************************/
					jrParam.setField("STOCK_ID"			, sSLAB_NO); 	//재료번호(SLAB번호)
					jrParam.setField("MSG_GP"			, "I"); 		//정보구분(I:신규)
					jrParam.setField("YD_INFO_SYNC_CD"	, "D"); 		//야드정보동기화코드(D:생산종료(삭제))
		
					sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2("YMA8L002", jrParam));
				}
				
			} else {
				
				szMsg="["+methodNm+"] PROCESS_ID가 7(:모 슬라브 종료)이 아닌 경우 연주전단 실적 처리 모듈 호출한다. ";
				commUtils.printLog(logId, szMsg, "SL");
				
				/**********************************************************
				* 5. 슬라브 연주전단 실적 (CSYDJ001) 전송
				**********************************************************/
				jrTemp = JDTORecordFactory.getInstance().create();

				jrTemp.setResultCode(logId);	//Log ID
				jrTemp.setResultMsg(methodNm);	//Log Method Name
				jrTemp.setField("JMS_TC_CD"				, "CSYDJ001");
				jrTemp.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
				jrTemp.setField("STL_NO"				, sSLAB_NO);

				//CSYDJ001 슬라브 연주전단 실적 전송 ->rcvCSYDJ001()호출
				sndRecord = commUtils.addSndData(sndRecord,jrTemp);	
			}
			
			commUtils.printLog(logId, methodNm, "S-");
			return sndRecord;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	
	} //end of rcvPOYMJ005()
		
	/**
	 *      [A] 오퍼레이션명 : 슬라브연주전단실적(CSYDJ001) ---> 이전 소스 SlabRegSBean.java 의 procCcFsWr 메소드
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvCSYDJ001(JDTORecord rcvMsg)throws DAOException  {
		
		String methodNm = "슬라브연주전단실적[BSlabL3RcvSeEJB.rcvCSYDJ001] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
	    String szMsg           		= "";

	    JDTORecordSet rsResult    	= null;
	    
		JDTORecord sndRecord		= JDTORecordFactory.getInstance().create();
	    JDTORecord jrParam			= null;	//Query 실행시 파라메터 전달용 JDTORecord 
		
	    String sSTL_NO;	//재료번호
	    String sPLAN_SLAB_NO = ""; //예정SLAB번호
	    String sCURR_PROG_CD = ""; //현재진도코드
	    String sWO_MSLAB_RPR_MTD = ""; //Scarfing Pattern
		
	    
        String msgId = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
        String modifier = msgId;
        
        if(msgId == null || msgId.equals("")) {
        	return sndRecord;
        }

	    try{

			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId + " 슬라브연주전단실적(CSYDJ001) 수신 ", rcvMsg);
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			sSTL_NO = commUtils.trim(rcvMsg.getFieldString("STL_NO")); //재료번호
			
			if("".equals(sSTL_NO)) {
				throw new Exception("재료번호 정보가 없습니다.");
			}
			
			jrParam = JDTORecordFactory.getInstance().create();
			
			/**********************************************************
			* 2. VW_YD_SLABCOMM 에서  STL_NO로 필요정보 조회
			**********************************************************/
			jrParam.setField("SLAB_NO"	, sSTL_NO);
			
			rsResult = bSlabDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getInitSlabInfo", logId, methodNm, "VW_YD_SLABCOMM 에서  STL_NO로 필요정보 조회");
			
			if(rsResult.size() > 0) {
				sPLAN_SLAB_NO 		= commUtils.trim(rsResult.getRecord(0).getFieldString("PLAN_SLAB_NO")); //예정SLAB번호
				sCURR_PROG_CD 		= commUtils.trim(rsResult.getRecord(0).getFieldString("CURR_PROG_CD")); //현재진도코드
				sWO_MSLAB_RPR_MTD   = commUtils.trim(rsResult.getRecord(0).getFieldString("WO_MSLAB_RPR_MTD")); //Scarfing Pattern
				
				szMsg="["+methodNm+"] 검색 결과  >> PLAN_SLAB_NO: " + sPLAN_SLAB_NO + ", CURR_PROG_CD: " + sCURR_PROG_CD + ", WO_MSLAB_RPR_MTD: " + sWO_MSLAB_RPR_MTD;
				commUtils.printLog(logId, szMsg, "SL");
			}
	    
			/**********************************************************
			* 3. PLAN_SLAB_NO로 TB_YM_STOCK를 조회한다.
			**********************************************************/
			jrParam.setField("STOCK_ID"	, sPLAN_SLAB_NO);
			
			rsResult = bSlabDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getYmStockInfo", logId, methodNm, "PLAN_SLAB_NO로 TB_YM_STOCK를 조회");
			
			if(rsResult.size() > 0) {
				//TB_YM_STOCK 에 예정SLAB번호 존재할 경우...
				szMsg="["+methodNm+"] TB_YM_STOCK에 PLAN_SLAB_NO가 존재함 ";
				commUtils.printLog(logId, szMsg, "SL");
				
				/**********************************************************
				* 3-1. TB_YM_STOCK의 PLAN_SLAB_NO를 STL_NO로 변경
				**********************************************************/
				jrParam.setField("PLAN_SLAB_NO"		, sPLAN_SLAB_NO );
				jrParam.setField("SLAB_NO"			, sSTL_NO );
				jrParam.setField("STOCK_MOVE_TERM"	, bSlabComm.getStockMoveTerm(sCURR_PROG_CD,sWO_MSLAB_RPR_MTD) );
				jrParam.setField("MODIFIER"			, modifier );
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockSlabNo 
				UPDATE TB_YM_STOCK
				   SET STOCK_ID = :V_SLAB_NO
				      ,STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM
				      ,MODIFIER = :V_MODIFIER
				      ,MOD_DDTT = SYSDATE
				 WHERE STOCK_ID = :V_PLAN_SLAB_NO  */  
				bSlabDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockSlabNo", logId, methodNm, "TB_YM_STOCK의 PLAN_SLAB_NO를 STL_NO로 변경");
				
				///**********************************************************
				//* 3-2. 저장품제원정보(YMA8L002)  L2 송신
				//**********************************************************/
				//jrParam.setField("STOCK_ID"			, sSTL_NO); //재료번호(SLAB번호)
				//jrParam.setField("MSG_GP"			, "I"); 	//정보구분(I:신규)
				//jrParam.setField("YD_INFO_SYNC_CD"	, "A"); 	//야드정보동기화코드(A:생산실적)
	
				//sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2("YMA8L002", jrParam));
				
			} else {
				//TB_YM_STOCK 에 예정SLAB번호 존재 하지 않을 경우...
				szMsg="["+methodNm+"] TB_YM_STOCK에 PLAN_SLAB_NO가 존재하지 않음 ";
				commUtils.printLog(logId, szMsg, "SL");
				
				/**********************************************************
				* 4. STL_NO로 TB_YM_STOCK를 조회한다.
				**********************************************************/
				jrParam.setField("STOCK_ID"	, sSTL_NO);
				//저장품 테이블에 실주편번호로 저장품이 있는지 체크
				rsResult = bSlabDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getYmStockInfo", logId, methodNm, "STL_NO로 TB_YM_STOCK를 조회-저장품 테이블에 실주편번호로 저장품이 있는지 체크");
				
				if(rsResult.size() > 0) {
					//실주편번호 존재
					szMsg="["+methodNm+"] TB_YM_STOCK에 STL_NO가 존재함 ";
					commUtils.printLog(logId, szMsg, "SL");
					
					/**********************************************************
					* 4-1. TB_YM_STOCK의 STL_NO에 저장품 이동 조건 갱신
					**********************************************************/
					jrParam.setField("STOCK_ID"			, sSTL_NO );
					jrParam.setField("STOCK_MOVE_TERM"	, bSlabComm.getStockMoveTerm(sCURR_PROG_CD,sWO_MSLAB_RPR_MTD) );
					jrParam.setField("MODIFIER"			, modifier );
					
					bSlabDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockMoveTerm", logId, methodNm, "TB_YM_STOCK의 STL_NO에 저장품 이동 조건 갱신");
				
				} else {
					//실주편번호 미 존재시
					szMsg="["+methodNm+"] TB_YM_STOCK에 STL_NO가 존재하지 않음 ";
					commUtils.printLog(logId, szMsg, "SL");
					
					/**********************************************************
					* 4-2. TB_YM_STOCK에 STL_NO를 신규생성
					**********************************************************/
					jrParam.setField("STOCK_ID"			, sSTL_NO );
					jrParam.setField("STOCK_MOVE_TERM"	, bSlabComm.getStockMoveTerm(sCURR_PROG_CD,sWO_MSLAB_RPR_MTD) );
					jrParam.setField("MODIFIER"			, modifier );
					
					bSlabDao.insert(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.insStockInfo", logId, methodNm, "TB_YM_STOCK에 STL_NO를 신규생성");
					
					///**********************************************************
					//* 4-3. 저장품제원정보(YMA8L002)  L2 송신
					//**********************************************************/
					//jrParam.setField("STOCK_ID"			, sSTL_NO); //재료번호(SLAB번호)
					//jrParam.setField("MSG_GP"			, "I"); 	//정보구분(I:신규)
					//jrParam.setField("YD_INFO_SYNC_CD"	, "A"); 	//야드정보동기화코드(A:생산실적)
		
					//sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2("YMA8L002", jrParam));
				}
			}
	    
			commUtils.printLog(logId, methodNm, "S-");
			return sndRecord;
		
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	
	} //end of rcvCSYDJ001()
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 슬라브 장입예정번호취소 (PCYM001)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPCYM001(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "슬라브 장입예정번호취소 [BSlabL3RcvSeEJB.rcvPCYM001] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = null;	//전문 Return
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID

			String sYD_GP   = commUtils.trim(rcvMsg.getFieldString("yardID"));  //야드구분
			String sSLAB_NO = commUtils.trim(rcvMsg.getFieldString("slabNo"));  //SLAB_NO


			//methodNm = msgId.substring(0, 2) + methodNm;
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(sSLAB_NO)) {
				throw new Exception("수신한 SLAB_NO 없음");
			}

			/**********************************************************
			* 2. 관제 ReSchedul 취소에 따른 '장입LOT번호'를 UPDATE
			**********************************************************/
			JDTORecord inRec = JDTORecordFactory.getInstance().create();
			/**
			SELECT A.SLAB_NO
			     , A.CURR_PROG_CD
			     , A.WO_MSLAB_RPR_MTD
			  FROM VW_YD_SLABCOMM A
			     , TB_YM_STOCK B
			 WHERE A.SLAB_NO = B.STOCK_ID
			   AND A.SLAB_NO = :V_SLAB_NO
			 */
			inRec.setField("SLAB_NO", sSLAB_NO);
			JDTORecord rst = commDao.select(inRec, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectSlabMatirialInfo").getRecord(0);
			
			String sCURR_PROG_CD     = rst.getFieldString("CURR_PROG_CD");
			String sWO_MSLAB_RPR_MTD = rst.getFieldString("WO_MSLAB_RPR_MTD");
			String sSTOCK_MOVE_TERM  = "";
			
	    	/* 일관제철 진도코드 */
	    	if (YmConstant.DMYDR016.equals(msgId)) {				//외판슬라브운송지시대기
	    		sSTOCK_MOVE_TERM = YmConstant.NEW_STOCK_MOVE_TERM_NS;
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_0.equals(sCURR_PROG_CD)) {    		
	    		sSTOCK_MOVE_TERM = YmConstant.NEW_STOCK_MOVE_TERM_11; 
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_1.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM = YmConstant.NEW_STOCK_MOVE_TERM_12;	
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_A.equals(sCURR_PROG_CD)) {
	    		if ("Q".equals(sSTOCK_MOVE_TERM)) {
	    			sWO_MSLAB_RPR_MTD = YmConstant.NEW_STOCK_MOVE_TERM_D3;	//핸드스카핑작업대기
	    		} else {
	    			sWO_MSLAB_RPR_MTD = YmConstant.NEW_STOCK_MOVE_TERM_DS;
	    		}
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_B.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_ES;
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_C.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_FS;
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_D.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_BS;
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_E.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_CS;
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_F.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_YS;
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_G.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_GS; // 종합판정대기
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_H.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_HS; // 입고대기
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_J.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_JS; // 반납대기
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_K.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_KS;
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_L.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_LS;
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_M.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_MS;    		
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_N.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_NS;    	
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_Y.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_ZS;    	
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_Z.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_ZS;
	    	}		
	    	
	    	/**
			UPDATE TB_YM_STOCK
			   SET CHARGE_LOT_NO   = :V_CHARGE_LOT_NO
			     , STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM
			     , MODIFIER        = 'SYSTEM'
			     , MOD_DDTT        = SYSDATE			     
			 WHERE STOCK_ID        = :V_STOCK_ID
	    	 */
	    	inRec.setField("CHARGE_LOT_NO"  , "");
	    	inRec.setField("STOCK_MOVE_TERM", sWO_MSLAB_RPR_MTD);
			inRec.setField("STOCK_ID"       , sSLAB_NO);
			commDao.update(inRec, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateZoneInOfStock");
	    	
			/**********************************************************
			* 3. 야드 L-2에 압연취소 정보를 송신한다.
			**********************************************************/
			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name

			/* 확인 해야함 !!*/
			jrYdMsg.setField("STOCK_ID"			, sSLAB_NO); 	//재료번호(SLAB번호)
			jrYdMsg.setField("MSG_GP"			, "I"); 		//정보구분(I:신규)
			jrYdMsg.setField("YD_INFO_SYNC_CD"	, "5"); 		//야드정보동기화코드(5:지정저장품))
			jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L002", jrYdMsg)); //저장품 재원
	

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	}	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 슬라브 장입예정번호등록  (PCYM002)
	 *
	 * 		@ejb.interfce-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPCYM002(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "슬라브 장입예정번호등록  [BSlabL3RcvSeEJB.rcvPCYM002] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = null;	//전문 Return
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID

			String sYD_GP   = commUtils.trim(rcvMsg.getFieldString("yardID"));  //야드구분
			String sSLAB_NO = commUtils.trim(rcvMsg.getFieldString("slabNo"));  //SLAB_NO

			//methodNm = msgId.substring(0, 2) + methodNm;
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
//			if ("".equals(sSLAB_NO)) {
//				throw new Exception("수신한 SLAB_NO 없음");
//			}

			/**********************************************************
			* 2. L2 삭제 장입정보 송신
			**********************************************************/
			JDTORecord inRec = JDTORecordFactory.getInstance().create();
			/**
			SELECT STOCK.STOCK_ID AS STL_NO           --저장품ID
			     , COMM.ORD_YEOJAE_GP                 --주문여재구분
			     , NVL(COMM.ORD_NO, '') || NVL(COMM.ORD_DTL, '') AS PRODUC_NO --제작번호행번
			     , COMM.SLAB_T                        --두께
			     , COMM.SLAB_W                        --폭
			     , COMM.SLAB_LEN                      --길이
			     , COMM.SLAB_WT                       --중량
			     , COMM.COIL_NO                       --예정COILNO
			     , COMM.STACK_LOT_NO AS STACK_LOT_CD --산적 LOT CODE
			     , COMM.BUY_SLAB_NO                	--구입슬라브번호
			  FROM TB_YM_STOCK         STOCK
			     , (SELECT * 
			          FROM VW_YD_SLABCOMM    A
			             , TB_QM_BUYSLABINFO B
			         WHERE A.MSLAB_NO = B.MSLAB_NO(+)
			       ) COMM 
			 WHERE STOCK.CHARGE_LOT_NO IS NOT NULL
			   AND STOCK.STOCK_ID = COMM.SLAB_NO 
			 */
			JDTORecordSet rsResult = commDao.select(inRec, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectZoneInStocks_Del", logId, methodNm, "");
			
			int cnt = rsResult != null ? rsResult.size() : 0;
			
			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name
			
			for (int idx = 0; idx < cnt; idx++) {
				/* 확인 해야함 !!*/
				jrYdMsg.setField("STOCK_ID"			, rsResult.getRecord(idx).getFieldString("STL_NO")); 	//재료번호(SLAB번호)
				jrYdMsg.setField("MSG_GP"			, "I"); 		//정보구분(I:신규)
				jrYdMsg.setField("YD_INFO_SYNC_CD"	, "D"); 		//야드정보동기화코드(D:생산종료(삭제)) 
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L002", jrYdMsg)); //저장품 재원                     
        	} //end for
			
			/**********************************************************
			* 3. 저장품의 장입순번을 CLEAR 한다.
			**********************************************************/
			/**
			SELECT STOCK_ID
			  FROM TB_YM_STOCK
			 WHERE CHARGE_LOT_NO IS NOT NULL
			 */
			rsResult = commDao.select(inRec, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.readZoneInNoOfStock", logId, methodNm, "");
			
			cnt = rsResult != null ? rsResult.size() : 0;
			
			for (int idx = 0; idx < cnt; idx++) {
				/**
				UPDATE TB_YM_STOCK
				   SET CHARGE_LOT_NO = NULL
				     , MODIFIER        = 'SYSTEM'
				     , MOD_DDTT        = SYSDATE				   
				 WHERE STOCK_ID = :V_STOCK_ID
				 */
				inRec.setField("STOCK_ID", rsResult.getRecord(idx).getFieldString("STOCK_ID"));
				commDao.update(inRec, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateZoneInNoOfStock", logId, methodNm, "");
	        }
			
			/**********************************************************
			* 4. 장입예정번호를 READ 한다.
			**********************************************************/
			/**
			SELECT NVL(SLABPRIOR.ROLL_UNIT_NAME, '')            ||
			       NVL(SLABPRIOR.LARGE_CHARGE_LOT_NAME, '')     ||
			       NVL(SLABPRIOR.SMALL_CHARGE_LOT_NAME, '') AS LOT_NO --LOT 번호 
			     , LPAD(ROLLPRIOR.YD_CHARGE_PRIOR,'3','0') || 
			       LPAD(LOTPRIOR.YD_CHARGE_PRIOR,'3','0') AS LOT_PRIOR        --LOT 순위        
			     , STOCK.STOCK_ID             --저장품ID
			     , TO_CHAR(SPEC.MILL_PLAN_DDTT, 'YYYYMMDDHH24MISS') AS MILL_PLAN_DDTT--압연 예정 일시
			     , SLABPRIOR.LOT_IN_SLAB_PRIOR    --LOT내작업순위
			     , COMM.ORD_YEOJAE_GP             --주문여재구분
			     , NVL(COMM.ORD_NO, '') ||
			       NVL(COMM.ORD_DTL, '') AS PRODUC_NO --제작번호행번
			     , COMM.SLAB_T                        --두께
			     , COMM.SLAB_W                        --폭
			     , COMM.SLAB_LEN                      --길이
			     , COMM.SLAB_WT                       --중량
			     , COMM.COIL_NO                       --예정COILNO
			     , COMM.STACK_LOT_NO AS STACK_LOT_CD --산적 LOT CODE
			     , BUYS.BUY_SLAB_NO                	--구입슬라브번호
			  FROM TB_PC_ROLLPRIOR     ROLLPRIOR
			     , TB_PC_LOTPRIOR      LOTPRIOR
			     , TB_PC_LOTSLABPRIOR  SLABPRIOR
			     , TB_YM_STOCK         STOCK
			     , VW_YD_SLABCOMM      COMM
			     , TB_PC_MILLSPEC      SPEC
			     , TB_QM_BUYSLABINFO   BUYS
			 WHERE ROLLPRIOR.PLANT_GP          = 'B'
			   AND ROLLPRIOR.ROLL_WORK_STAT    = '2'
			   AND ROLLPRIOR.ROLL_UNIT_NAME    = LOTPRIOR.ROLL_UNIT_NAME
			   AND LOTPRIOR.WORK_STAT          = '2'
			   AND LOTPRIOR.ROLL_UNIT_NAME     = SLABPRIOR.ROLL_UNIT_NAME
			   AND LOTPRIOR.LARGE_CHARGE_LOT_NAME  = SLABPRIOR.LARGE_CHARGE_LOT_NAME
			   AND LOTPRIOR.SMALL_CHARGE_LOT_NAME  = SLABPRIOR.SMALL_CHARGE_LOT_NAME
			   AND SLABPRIOR.WORK_STAT         = '2'
			   AND SLABPRIOR.PROD_GP           IN ('C','D','E','F','G')
			   AND SLABPRIOR.SLAB_NO           = STOCK.STOCK_ID
			   AND STOCK.STOCK_ID              = COMM.SLAB_NO
			   AND STOCK.STOCK_ID              = SPEC.PLAN_REAL_SLAB_NO
			   AND COMM.MSLAB_NO               = BUYS.MSLAB_NO(+)
			 ORDER BY ROLLPRIOR.YD_CHARGE_PRIOR
			        , LOTPRIOR.YD_CHARGE_PRIOR
			        , SLABPRIOR.LOT_IN_SLAB_PRIOR
			 */
			JDTORecordSet rsResult4 = commDao.select(inRec, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectZoneInStocks", logId, methodNm, "");
			
			int cnt4 = rsResult != null ? rsResult.size() : 0;
			if (cnt4 == 0) {
				//장입예정번호가 존재하지 않습니다
				return null;
	        }   
			
			
			/** 
			 * #3CTC,#4CTC,W/B에 올려진 SLAB는 장입순번을 변경하지 않는다.
			 */
			
			/**
			SELECT STOCK_ID
			  FROM TB_YM_STACKLAYER
			 WHERE STACK_COL_GP    = '2BCT03'
			   AND STACK_BED_GP    = '01'
			   AND STACK_LAYER_GP  = '01'
			   AND STACK_LAYER_STAT= 'L'
			UNION
			SELECT STOCK_ID
			  FROM TB_YM_STACKLAYER
			 WHERE STACK_COL_GP    = '2CCT04'
			   AND STACK_BED_GP    = '01'
			   AND STACK_LAYER_GP  = '01'
			   AND STACK_LAYER_STAT= 'L'
			UNION
			SELECT STOCK_ID
			  FROM TB_YM_STACKLAYER
			 WHERE STACK_COL_GP LIKE '2CWB%'
			   AND STACK_LAYER_STAT= 'L'
			 */
			rsResult = commDao.select(inRec, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectLoadWBCTC1", logId, methodNm, "");
			cnt = rsResult != null ? rsResult.size() : 0;
			
			/**
             * 저장품 테이블에 READ한 Slab No를 Update 한다.
             * -저장품 상태	: "F".
             * -장입 LOT 번호	: READ한 예정번호. 
             */
            for (int i = 0; i < cnt4; i++) {
                
            	String sSTOCK_ID  = rsResult4.getRecord(i).getFieldString("STOCK_ID");
            	String sLOT_PRIOR = rsResult4.getRecord(i).getFieldString("LOT_PRIOR");
                
            	boolean notStock = true;
                for (int j = 0; j < cnt; j++) {
                    if (sSTOCK_ID.equals(rsResult.getRecord(j).getFieldString("STOCK_ID"))) {
                        notStock = false;
                        break;
                    }
                }
            	
                if (notStock) {
        			/**
        			SELECT A.SLAB_NO
        			     , A.CURR_PROG_CD
        			     , A.WO_MSLAB_RPR_MTD
        			  FROM VW_YD_SLABCOMM A
        			     , TB_YM_STOCK B
        			 WHERE A.SLAB_NO = B.STOCK_ID
        			   AND A.SLAB_NO = :V_SLAB_NO
        			 */
        			inRec.setField("SLAB_NO", sSTOCK_ID);
        			JDTORecord rst = commDao.select(inRec, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectSlabMatirialInfo", logId, methodNm, "").getRecord(0);
        			
        			String sCURR_PROG_CD     = rst.getFieldString("CURR_PROG_CD");
        			String sWO_MSLAB_RPR_MTD = rst.getFieldString("WO_MSLAB_RPR_MTD");
        			String sSTOCK_MOVE_TERM  = "";
        			
        	    	/* 일관제철 진도코드 */
        	    	if (YmConstant.DMYDR016.equals(msgId)) {				//외판슬라브운송지시대기
        	    		sSTOCK_MOVE_TERM = YmConstant.NEW_STOCK_MOVE_TERM_NS;
        	    	} else if (YmConstant.CURR_PROG_CD_SLAB_0.equals(sCURR_PROG_CD)) {    		
        	    		sSTOCK_MOVE_TERM = YmConstant.NEW_STOCK_MOVE_TERM_11; 
        	    	} else if (YmConstant.CURR_PROG_CD_SLAB_1.equals(sCURR_PROG_CD)) {
        	    		sSTOCK_MOVE_TERM = YmConstant.NEW_STOCK_MOVE_TERM_12;	
        	    	} else if (YmConstant.CURR_PROG_CD_SLAB_A.equals(sCURR_PROG_CD)) {
        	    		if ("Q".equals(sSTOCK_MOVE_TERM)) {
        	    			sWO_MSLAB_RPR_MTD = YmConstant.NEW_STOCK_MOVE_TERM_D3;	//핸드스카핑작업대기
        	    		} else {
        	    			sWO_MSLAB_RPR_MTD = YmConstant.NEW_STOCK_MOVE_TERM_DS;
        	    		}
        	    	} else if (YmConstant.CURR_PROG_CD_SLAB_B.equals(sCURR_PROG_CD)) {
        	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_ES;
        	    	} else if (YmConstant.CURR_PROG_CD_SLAB_C.equals(sCURR_PROG_CD)) {
        	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_FS;
        	    	} else if (YmConstant.CURR_PROG_CD_SLAB_D.equals(sCURR_PROG_CD)) {
        	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_BS;
        	    	} else if (YmConstant.CURR_PROG_CD_SLAB_E.equals(sCURR_PROG_CD)) {
        	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_CS;
        	    	} else if (YmConstant.CURR_PROG_CD_SLAB_F.equals(sCURR_PROG_CD)) {
        	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_YS;
        	    	} else if (YmConstant.CURR_PROG_CD_SLAB_G.equals(sCURR_PROG_CD)) {
        	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_GS; // 종합판정대기
        	    	} else if (YmConstant.CURR_PROG_CD_SLAB_H.equals(sCURR_PROG_CD)) {
        	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_HS; // 입고대기
        	    	} else if (YmConstant.CURR_PROG_CD_SLAB_J.equals(sCURR_PROG_CD)) {
        	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_JS; // 반납대기
        	    	} else if (YmConstant.CURR_PROG_CD_SLAB_K.equals(sCURR_PROG_CD)) {
        	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_KS;
        	    	} else if (YmConstant.CURR_PROG_CD_SLAB_L.equals(sCURR_PROG_CD)) {
        	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_LS;
        	    	} else if (YmConstant.CURR_PROG_CD_SLAB_M.equals(sCURR_PROG_CD)) {
        	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_MS;    		
        	    	} else if (YmConstant.CURR_PROG_CD_SLAB_N.equals(sCURR_PROG_CD)) {
        	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_NS;    	
        	    	} else if (YmConstant.CURR_PROG_CD_SLAB_Y.equals(sCURR_PROG_CD)) {
        	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_ZS;    	
        	    	} else if (YmConstant.CURR_PROG_CD_SLAB_Z.equals(sCURR_PROG_CD)) {
        	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_ZS;
        	    	}		
        	    	
        	    	/**
        			UPDATE TB_YM_STOCK
        			   SET CHARGE_LOT_NO   = :V_CHARGE_LOT_NO
        			     , STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM
					     , MODIFIER        = 'SYSTEM'
					     , MOD_DDTT        = SYSDATE        			     
        			 WHERE STOCK_ID        = :V_STOCK_ID
        	    	 */
        	    	inRec.setField("CHARGE_LOT_NO"  , sLOT_PRIOR);
        	    	inRec.setField("STOCK_MOVE_TERM", sWO_MSLAB_RPR_MTD);
        			inRec.setField("STOCK_ID"       , sSTOCK_ID);
        			commDao.update(inRec, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateZoneInOfStock", logId, methodNm, "");
        			

        			jrYdMsg.setResultCode(logId);	//Log ID
        			jrYdMsg.setResultMsg(methodNm);	//Log Method Name

        			/* 확인 해야함 !!*/
        			jrYdMsg.setField("STOCK_ID"			, sSTOCK_ID); 	//재료번호(SLAB번호)
        			jrYdMsg.setField("MSG_GP"			, "I"); 		//정보구분(I:신규)
        			jrYdMsg.setField("YD_INFO_SYNC_CD"	, "5"); 		//야드정보동기화코드
        			jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L002", jrYdMsg)); //저장품 재원
        			
                } // if (notStock)
                
            } // end for
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	}		
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 슬라브 미처리,반송 Slab 결번 (PCYM003)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPCYM003(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "슬라브 미처리,반송 Slab 결번 [BSlabL3RcvSeEJB.rcvPCYM003] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = null;	//전문 Return
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			
			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sSLAB_NO = commUtils.trim(rcvMsg.getFieldString("slabNo"));  //SLAB_NO

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(sSLAB_NO)) {
				throw new Exception("수신한 SLAB_NO 없음");
			} else if (sSLAB_NO.length() > 11) {
				throw new Exception("SLAB_NO의 길이가 11보다 큼");
			}

			/**********************************************************
			* 2. 슬라브 공통 테이블의 진도코드를 참조해서 저장품이동조건을 가져온다. 
			**********************************************************/
			JDTORecord jparam = JDTORecordFactory.getInstance().create();
			/**
			SELECT A.SLAB_NO
			     , A.CURR_PROG_CD
			     , A.WO_MSLAB_RPR_MTD
			  FROM VW_YD_SLABCOMM A
			     , TB_YM_STOCK B
			 WHERE A.SLAB_NO = B.STOCK_ID
			   AND A.SLAB_NO = :V_SLAB_NO
			 */
			jparam.setField("SLAB_NO", sSLAB_NO);
			JDTORecord rst = commDao.select(jparam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectSlabMatirialInfo", logId, mthdNm, "").getRecord(0);
			
			String sCURR_PROG_CD     = rst.getFieldString("CURR_PROG_CD");
			String sWO_MSLAB_RPR_MTD = rst.getFieldString("WO_MSLAB_RPR_MTD");
			String sSTOCK_MOVE_TERM  = "";
			
			/* 일관제철 진도코드 */
	    	if (YmConstant.DMYDR016.equals(msgId)) {				//외판슬라브운송지시대기
	    		sSTOCK_MOVE_TERM = YmConstant.NEW_STOCK_MOVE_TERM_NS;
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_0.equals(sCURR_PROG_CD)) {    		
	    		sSTOCK_MOVE_TERM = YmConstant.NEW_STOCK_MOVE_TERM_11; 
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_1.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM = YmConstant.NEW_STOCK_MOVE_TERM_12;	
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_A.equals(sCURR_PROG_CD)) {
	    		if ("Q".equals(sSTOCK_MOVE_TERM)) {
	    			sWO_MSLAB_RPR_MTD = YmConstant.NEW_STOCK_MOVE_TERM_D3;	//핸드스카핑작업대기
	    		} else {
	    			sWO_MSLAB_RPR_MTD = YmConstant.NEW_STOCK_MOVE_TERM_DS;
	    		}
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_B.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_ES;
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_C.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_FS;
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_D.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_BS;
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_E.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_CS;
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_F.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_YS;
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_G.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_GS; // 종합판정대기
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_H.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_HS; // 입고대기
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_J.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_JS; // 반납대기
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_K.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_KS;
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_L.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_LS;
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_M.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_MS;    		
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_N.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_NS;    	
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_Y.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_ZS;    	
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_Z.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_ZS;
	    	}		
			
	    	/**
			UPDATE TB_YM_STOCK
			   SET STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM
			     , CHARGE_LOT_NO   = NULL
			     , MODIFIER        = 'SYSTEM'
			     , MOD_DDTT        = SYSDATE
			 WHERE STOCK_ID = :V_STOCK_ID
	    	 */
	    	jparam.setField("STOCK_MOVE_TERM", sSTOCK_MOVE_TERM);
	    	jparam.setField("SLAB_NO"        , sSLAB_NO);
	    	commDao.update(jparam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateMoveTermOfStock", logId, mthdNm, "");
	    	
			/**********************************************************
			* 3. 야드 L2 전송 장입순번 Clear CALL
			**********************************************************/
			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(mthdNm);	//Log Method Name

			/* 확인 해야함 !!*/
			jrYdMsg.setField("STOCK_ID"			, sSLAB_NO); 	//재료번호(SLAB번호)
			jrYdMsg.setField("MSG_GP"			, "I"); 		//정보구분(I:신규)
			jrYdMsg.setField("YD_INFO_SYNC_CD"	, "5"); 		//야드정보동기화코드
			jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L002", jrYdMsg)); //저장품 재원
	

			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}

	}	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 슬라브 Scarfing 출측 Line Off 요구 정보 수신 (POYMJ003)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPOYMJ003(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "슬라브 Scarfing 출측 Line Off 요구 정보 수신 [BSlabL3RcvSeEJB.rcvPOYMJ003] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = null;	//전문 Return
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			
			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String modifier = msgId;
			
			String sPlantGp      = commUtils.trim(rcvMsg.getFieldString("PlantGp"));
			String sProcGp       = commUtils.trim(rcvMsg.getFieldString("ProcGp"));
			String sWordUnitName = commUtils.trim(rcvMsg.getFieldString("WordUnitName"));  //작업지시단위명

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
//			if ("".equals(sSLAB_NO)) {
//				commUtils.printLog(logId, "수신한 SLAB_NO 없음", "[ERROR]");
//				return null;
//			} 

			/**********************************************************
			* 2.  조업 정정Table에서 저장품정보를 읽어서 해당 저장품의 저장품이동경로 항목을 UPDATE한다.
			**********************************************************/
			String sSTOCK_MOVE_TERM = YmConstant.NEW_STOCK_MOVE_TERM_CC; // COIL 정정작업대기
			String sSTOCK_ID = "";
			
			if(YmConstant.YD_GP_1.equals(sPlantGp)){
				sPlantGp = YmConstant.YD_GP_A;
			}else{
				sPlantGp = YmConstant.YD_GP_B;
			}
			
			/////////////////////////////////////////////////////////////////////////////////////
			//박판열연 신규모듈 적용여부 체크
			if(YmConstant.YD_GP_A.equals(sPlantGp))
			{ 
				JDTORecord jrResult =  commDao.getYfNewModuleEffYn();
				String sASLAB_EFF_YN = commUtils.nvl(jrResult.getFieldString("ASLAB_EFF_YN"),"N");
				String sACOIL_EFF_YN = commUtils.nvl(jrResult.getFieldString("ACOIL_EFF_YN"),"N");

				String szMsg = "YmCommDAO.getYfNewModuleEffYn()---[[[ A열연SLAB야드신규적용:" + sASLAB_EFF_YN + " ,A열연COIL야드신규적용:" + sACOIL_EFF_YN + " ]]]---";
				commUtils.printLog(logId, mthdNm + szMsg , "SL");
				
				if("Y".equals(sASLAB_EFF_YN))
				{
					EJBConnector ejbCon = new EJBConnector("default", "YfRcvFaEJB", this);
					ejbCon.trx("rcvMessage", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });
					
					return jrRtn;
				}
			}
			/////////////////////////////////////////////////////////////////////////////////////
			
			JDTORecord jparam = JDTORecordFactory.getInstance().create();
	    	/**
			SELECT A.COIL_NO    -- 코일번호
			     , A.STEP_NO    -- 차수
			     , A.WORK_STAT  -- 작업상태(<> 0)
			     , A.WORD_UNIT_NAME -- 작업지시 단위 명
			     , A.HR_PLNT_GP AS PLANT_GP -- 공장구분(A-A열연,B-B열연)
			     , A.PROC_GP    -- 공정구분(K-SPM,H-HFL)
			  FROM TB_HR_C_SHEARWOPRIOR A
			     , (SELECT COIL_NO
			             , MAX(STEP_NO) AS STEP_NO
			          FROM TB_HR_C_SHEARWOPRIOR  --HR열연정정지시순위
			         WHERE HR_PLNT_GP     = :V_HR_PLNT_GP
			           AND PROC_GP        = :V_PROC_GP
			           AND WORD_UNIT_NAME = :V_WORD_UNIT_NAME
			           AND WORK_STAT NOT IN ('0','1')
			         GROUP BY COIL_NO
			       ) B
			 WHERE A.COIL_NO = B.COIL_NO
			   AND A.STEP_NO = B.STEP_NO       
			   AND A.HR_PLNT_GP     = :V_HR_PLNT_GP 
			   AND A.PROC_GP        = :V_PROC_GP
			   AND A.WORD_UNIT_NAME = :V_WORD_UNIT_NAME
			   AND A.WORK_STAT NOT IN ('0','1')
	    	 */
	    	jparam.setField("HR_PLNT_GP"     , sPlantGp);
	    	jparam.setField("PROC_GP"        , sProcGp);
	    	jparam.setField("WORD_UNIT_NAME" , sWordUnitName);
	    	JDTORecordSet rst = commDao.select(jparam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getPoPmStockInfo", logId, mthdNm, "조업정보 검색");
	    	
	    	if (rst.size() == 0) {
	    		commUtils.printLog(logId, "=인터페이스 작업요구=>조업TABLE 저장품정보 존재안함.", "[ERROR]");
	    		return null;
	    	}
	    	
	    	for (int idx = 0; idx < rst.size() ; idx++) {
	    		sSTOCK_ID = rst.getRecord(idx).getFieldString("STOCK_ID");
	    		
	    		/**
				UPDATE TB_YM_STOCK
				   SET STOCK_MOVE_TERM = DECODE(STOCK_MOVE_TERM,'BD',STOCK_MOVE_TERM, :V_STOCK_MOVE_TERM)
				     , MODIFIER   = 'SYSTEM'
				     , MOD_DDTT   = SYSDATE     
				 WHERE STOCK_ID = :V_STOCK_ID
	    		 */
	    		jparam.setField("STOCK_MOVE_TERM" , sSTOCK_MOVE_TERM);
	    		jparam.setField("STOCK_ID"        , sSTOCK_ID);
		    	commDao.update(jparam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateMoveTermOfStock", logId, mthdNm, "STOCK TABLE UPDATE");
	    	}
	    	

			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}

	}	
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 슬라브 결번실적 (POYMJ006)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPOYMJ006(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "슬라브 결번실적 [BSlabL3RcvSeEJB.rcvPOYMJ006] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = null;	//전문 Return
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			
			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String modifier = msgId;
			
			String sYD_GP   = commUtils.trim(rcvMsg.getFieldString("yardID"));
			String sSLAB_NO = commUtils.trim(rcvMsg.getFieldString("SlabNo"));  

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(sSLAB_NO)) {
				throw new Exception("수신한 SLAB_NO 없음");
			} 
			if (sSLAB_NO.length() > 11) {
				throw new Exception("슬라브번호의 길이가 11보다 작아야 합니다.");
			}
			
			/////////////////////////////////////////////////////////////////////////////////////
			//박판열연 신규모듈 적용여부 체크
            if("0".equals(sYD_GP))
            {
                JDTORecord jrResult =  commDao.getYfNewModuleEffYn();
                String sASLAB_EFF_YN = commUtils.nvl(jrResult.getFieldString("ASLAB_EFF_YN"),"N");
                String sACOIL_EFF_YN = commUtils.nvl(jrResult.getFieldString("ACOIL_EFF_YN"),"N");

                String szMsg = "YmCommDAO.getYfNewModuleEffYn()---[[[ A열연SLAB야드신규적용:" + sASLAB_EFF_YN + " ,A열연COIL야드신규적용:" + sACOIL_EFF_YN + " ]]]---";
                commUtils.printLog(logId, mthdNm + szMsg , "SL");

                if("Y".equals(sASLAB_EFF_YN))
                {
                    EJBConnector ejbCon = new EJBConnector("default", "YfRcvFaEJB", this);
                    ejbCon.trx("rcvMessage", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });

                    return jrRtn;
                }
            }
			/////////////////////////////////////////////////////////////////////////////////////
			
			/**********************************************************
			* 2.  저장품 테이블의 '저장품 이동 조건'을 UPDATE
			**********************************************************/
			JDTORecord jparam = JDTORecordFactory.getInstance().create();
			/**
			SELECT A.SLAB_NO
			     , A.CURR_PROG_CD
			     , A.WO_MSLAB_RPR_MTD
			  FROM VW_YD_SLABCOMM A
			     , TB_YM_STOCK B
			 WHERE A.SLAB_NO = B.STOCK_ID
			   AND A.SLAB_NO = :V_SLAB_NO
			 */
			jparam.setField("SLAB_NO", sSLAB_NO);
			JDTORecord rst = commDao.select(jparam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectSlabMatirialInfo", logId, mthdNm, "").getRecord(0);
			
			String sCURR_PROG_CD     = rst.getFieldString("CURR_PROG_CD");
			String sWO_MSLAB_RPR_MTD = rst.getFieldString("WO_MSLAB_RPR_MTD");
			String sSTOCK_MOVE_TERM  = "";
			
			/* 일관제철 진도코드 */
	    	if (YmConstant.DMYDR016.equals(msgId)) {				//외판슬라브운송지시대기
	    		sSTOCK_MOVE_TERM = YmConstant.NEW_STOCK_MOVE_TERM_NS;
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_0.equals(sCURR_PROG_CD)) {    		
	    		sSTOCK_MOVE_TERM = YmConstant.NEW_STOCK_MOVE_TERM_11; 
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_1.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM = YmConstant.NEW_STOCK_MOVE_TERM_12;	
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_A.equals(sCURR_PROG_CD)) {
	    		if ("Q".equals(sSTOCK_MOVE_TERM)) {
	    			sWO_MSLAB_RPR_MTD = YmConstant.NEW_STOCK_MOVE_TERM_D3;	//핸드스카핑작업대기
	    		} else {
	    			sWO_MSLAB_RPR_MTD = YmConstant.NEW_STOCK_MOVE_TERM_DS;
	    		}
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_B.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_ES;
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_C.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_FS;
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_D.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_BS;
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_E.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_CS;
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_F.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_YS;
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_G.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_GS; // 종합판정대기
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_H.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_HS; // 입고대기
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_J.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_JS; // 반납대기
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_K.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_KS;
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_L.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_LS;
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_M.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_MS;    		
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_N.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_NS;    	
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_Y.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_ZS;    	
	    	} else if (YmConstant.CURR_PROG_CD_SLAB_Z.equals(sCURR_PROG_CD)) {
	    		sSTOCK_MOVE_TERM   = YmConstant.NEW_STOCK_MOVE_TERM_ZS;
	    	}		
			
	    	/**
			UPDATE TB_YM_STOCK
			   SET STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM
			     , CHARGE_LOT_NO   = NULL
			     , MODIFIER        = 'SYSTEM'
			     , MOD_DDTT        = SYSDATE
			 WHERE STOCK_ID = :V_STOCK_ID
	    	 */
	    	jparam.setField("STOCK_MOVE_TERM", sSTOCK_MOVE_TERM);
	    	jparam.setField("STOCK_ID"       , sSLAB_NO);
	    	jparam.setField("MODIFIER"       , modifier);
	    	
	    	commDao.update(jparam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateMoveTermOfStock", logId, mthdNm, "TB_YM_STOCK의 STOCK_MOVE_TERM을 UPDATE");
	    	

			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}

	}	
	
	
	/**
	 *      [A] 오퍼레이션명 : 1열연 슬라브 압연지시확정 (CTYDJ032)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvCTYDJ032(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "슬라브 압연지시확정 [BSlabL3RcvSeEJB.rcvCTYDJ032] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = null;	//전문 Return
		JDTORecord jparam = JDTORecordFactory.getInstance().create();
		
		//수신 항목 값
		String rcvSeq	  		= commUtils.trim(rcvMsg.getFieldString("CT_RCV_SEQ"   )); //지시ID(NEW)
		String ptopPlntGp 		= commUtils.trim(rcvMsg.getFieldString("PTOP_PLNT_GP" )); //조업공장구분
		String msgId    	 	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
		String sMOD_GP        	= commUtils.trim(rcvMsg.getFieldString("MOD_GP"));
		String chgWoFrPnt 		= commUtils.trim(rcvMsg.getFieldString("CHG_WO_FR_PNT")); //장입지시FromPoint
		String chgWoToPnt 		= commUtils.trim(rcvMsg.getFieldString("CHG_WO_TO_PNT")); //장입지시ToPoint
		String sPTOP_PLNT_GP  	= commUtils.trim(rcvMsg.getFieldString("PTOP_PLNT_GP"));
		String sCHG_WO_FR_PNT 	= commUtils.trim(rcvMsg.getFieldString("CHG_WO_FR_PNT"));
		String sCHG_WO_TO_PNT 	= commUtils.trim(rcvMsg.getFieldString("CHG_WO_TO_PNT"));
		String sEND_GP        	= commUtils.trim(rcvMsg.getFieldString("END_GP"));
		String modifier   		= commUtils.trim(rcvMsg.getFieldString("MODIFIER"   )); //수정자(Backup Only)
		String woMtlCnt   		= commUtils.trim(rcvMsg.getFieldString("CT_WO_MTL_CNT")); //건수
		
		try {
			
			commUtils.printLog(logId, mthdNm, "S+");
			
			
			
			if ("".equals(modifier)) { modifier = msgId; }
			
			/*
        	 * I : 생산통제 압연지시
        	 * D : 생산통제 압연지시 취소
        	 */
			if ("D".equals(sMOD_GP)) {
				
				/**********************************************************
				* D.1 지시취소이면 압연지시결번실적 처리
				**********************************************************/
				String sSLAB_NO       	= commUtils.trim(rcvMsg.getFieldString("SLAB_NO"));
				String sPLAN_SLAB_NO  	= commUtils.trim(rcvMsg.getFieldString("PLAN_SLAB_NO")); 
				
				//* 2022.07.04 열연1부 김기훈 주임님 요청. 장입정보 변경시 알림기능
				//저장품 테이블에서 장입번호 select
				jparam.setField("STOCK_ID"		, sSLAB_NO	);
				JDTORecordSet rst = commDao.select(jparam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getYmStockInfo", logId, mthdNm, "저장품 정보 검색");
				
				String chargeLotNo = "";//전문 수신한 장입번호
				String delYn = "";
				String modGp = "";
				String lotNo = ""; //기존 메세지 rule의 장입번호
				String stlNo = "";
				int cnt = 0;
	
				if(rst != null && rst.size() > 0) {
					chargeLotNo = commUtils.trim(rst.getRecord(0).getFieldString("CHARGE_LOT_NO"));
					
					jparam.setField("REPR_CD_GP"	, "MSG001"		);
					jparam.setField("CD_GP"			, "*"			);
					jparam.setField("ITEM"			, "*"			);
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmRule2
					SELECT REPR_CD_GP
					     , CD_GP
					     , ITEM
					     , REPR_CD_CONTENTS
					     , REGISTER
					     , REG_DDTT
					     , MODIFIER
					     , TO_CHAR(MOD_DDTT, 'YYYY/MM/DD HH24:MI:SS') AS MOD_DDTT
					     , DEL_YN
					     , DTL_ITM1
					     , DTL_ITM2
					     , DTL_ITM3
					     , DTL_ITM4
					     , DTL_ITM5
					     , DTL_ITM6
					     , DTL_ITM7
					     , DTL_ITM8
					     , DTL_ITM9
					     , DTL_ITM10
					  FROM USRYMA.TB_YM_RULE
					 WHERE REPR_CD_GP = :V_REPR_CD_GP                  
					   AND CD_GP      = NVL(:V_CD_GP, CD_GP)
					   AND ITEM       = NVL(:V_ITEM , ITEM )
					 ORDER BY CD_GP
					        , ITEM
					*/
					rst = commDao.select(jparam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmRule2", logId, mthdNm, "기준정보 검색");
					
					if(rst != null && rst.size() > 0) {
						delYn = rst.getRecord(0).getFieldString("DEL_YN");
						modGp = rst.getRecord(0).getFieldString("DTL_ITM1");
						lotNo = rst.getRecord(0).getFieldString("DTL_ITM2");
						stlNo = rst.getRecord(0).getFieldString("DTL_ITM3");
						cnt   = Integer.parseInt(rst.getRecord(0).getFieldString("DTL_ITM4"));
					}
					
					//메세지가 삭제되어있거나, 삭제알림이 아닌 경우 삭제알림 새로 set
					if("Y".equals(delYn) || ("N".equals(delYn) && !"삭제".equals(modGp))){
						jparam.setField("DTL_ITM1"		, "삭제"		);
						jparam.setField("DTL_ITM2"		, chargeLotNo	);
						jparam.setField("DTL_ITM3"		, sSLAB_NO		);
						jparam.setField("DTL_ITM4"		, "1"			);
					}
					//삭제처리 중복인경우 lotno, slabNo, cnt 업데이트
					else{
						if("".equals(lotNo)) lotNo = "9999";
						if("".equals(chargeLotNo)) chargeLotNo = "0";
						
						int prevLotNo = Integer.parseInt(lotNo);
						int curLotNo = Integer.parseInt(chargeLotNo);
						
						//기존 저장된 lotNo가 새로 삭제처리되는 lotNo보다 빠른거나 같은경우
						if(curLotNo >= prevLotNo) {
							jparam.setField("DTL_ITM2"		, lotNo	);
							jparam.setField("DTL_ITM3"		, stlNo	);
							jparam.setField("DTL_ITM4"		, Integer.toString(cnt+1));
						}
						//기존 저장된 lotNo가 새로 삭제처리되는 lotNo보다 느린경우
						else {
							jparam.setField("DTL_ITM2"		, chargeLotNo	);
							jparam.setField("DTL_ITM3"		, sSLAB_NO	);
							jparam.setField("DTL_ITM4"		, Integer.toString(cnt+1));
						}
					}
					jparam.setField("DTL_ITM1"		, "삭제"		);
					jparam.setField("MODIFIER"		, modifier		);
					jparam.setField("DEL_YN"		, "N"			);
					jparam.setField("REPR_CD_GP"	, "MSG001"		);
					jparam.setField("CD_GP"			, "*"			);
					jparam.setField("ITEM"			, "*"			);
					
					/*com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updYmRule
					UPDATE TB_YM_RULE
					   SET DTL_ITM1 = NVL(:V_DTL_ITM1, DTL_ITM1)
					      ,DTL_ITM2 = NVL(:V_DTL_ITM2, DTL_ITM2)
					      ,DTL_ITM3 = NVL(:V_DTL_ITM3, DTL_ITM3)
					      ,DTL_ITM4 = NVL(:V_DTL_ITM4, DTL_ITM4)
					      ,DTL_ITM5 = NVL(:V_DTL_ITM5, DTL_ITM5)
					      ,DTL_ITM6 = NVL(:V_DTL_ITM6, DTL_ITM6)
					      ,DTL_ITM7 = NVL(:V_DTL_ITM7, DTL_ITM7)
					      ,DTL_ITM8 = NVL(:V_DTL_ITM8, DTL_ITM8)
					      ,DTL_ITM9 = NVL(:V_DTL_ITM9, DTL_ITM9)
					      ,DTL_ITM10 = NVL(:V_DTL_ITM10, DTL_ITM10)
					      ,MODIFIER = :V_MODIFIER
					      ,MOD_DDTT = SYSDATE
					      ,DEL_YN = NVL(:V_DEL_YN, DEL_YN) --추가
					 WHERE REPR_CD_GP = :V_REPR_CD_GP
					   AND CD_GP = :V_CD_GP
					   AND ITEM = :V_ITEM
					*/
					
					commDao.update(jparam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updYmRule", logId, mthdNm, "생산통제MSG RULE 수정");
				}
				//*/
				
				
				//저장품 TABLE에서 SLAB_NO로  CHARGE_LOT_NO 항목 CLEAR
				jparam.setField("CHARGE_LOT_NO"	, ""		);
				jparam.setField("MODIFIER"		, modifier	);
				jparam.setField("STOCK_ID"		, sSLAB_NO	);
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateStockLotNoWithStockId
				UPDATE TB_YM_STOCK
				   SET CHARGE_LOT_NO = :V_CHARGE_LOT_NO	
				     , MODIFIER      = :V_MODIFIER
				     , MOD_DDTT      = SYSDATE     
				 WHERE STOCK_ID = :V_STOCK_ID   */
				commDao.update(jparam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateStockLotNoWithStockId", logId, mthdNm, "저장품 TABLE에서 SLAB_NO로 CHARGE_LOT_NO 항목 CLEAR");
				
				//저장품 TABLE에서 PLAN_SLAB_NO로 CHARGE_LOT_NO 항목 CLEAR
				jparam.setField("CHARGE_LOT_NO"	, ""			);
				jparam.setField("MODIFIER"		, modifier		);
				jparam.setField("STOCK_ID"		, sPLAN_SLAB_NO	);
				commDao.update(jparam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateStockLotNoWithStockId", logId, mthdNm, "저장품 TABLE에서 PLAN_SLAB_NO로 CHARGE_LOT_NO 항목 CLEAR");
				
				//if ("*".equals(sEND_GP)) {
					
					/**********************************************************
					* D.2 L2 저장품제원 전문을 생성
					**********************************************************/
					jparam.setField("MSG_GP"			, "U" 		); //전문구분(U:수정)
					jparam.setField("YD_INFO_SYNC_CD"	, "R" 		); //야드정보동기화코드(지정저장품) R:압연지시(저장위치변경안함)
					jparam.setField("STOCK_ID"		 	, sSLAB_NO	);

					//저장품제원(YMA8L002) 전문 생성
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L002", jparam));
				//}
				
				return jrRtn;
			}
			
			/**********************************************************
			* I.1 압연지시확정 처리
			**********************************************************/
			if (!"HB".equals(sPTOP_PLNT_GP)) {
				commUtils.printLog(logId, "1열연 SLAB 압연작업지시 확정 전문 수신에러 => 조업공장구분 항목 에러 =>"+sPTOP_PLNT_GP, "[info]");
				return jrRtn;
			} else if ("".equals(sCHG_WO_FR_PNT)) {
				commUtils.printLog(logId, "1열연 SLAB 압연작업지시 확정 전문 수신에러 => 장입지시 FROM POINT 에러  ", "[info]");
                return jrRtn;
            } else if ("".equals(sCHG_WO_TO_PNT)) {
            	commUtils.printLog(logId, "1열연 SLAB 압연작업지시 확정 전문 수신에러 => 장입지시 TO POINT 에러  ", "[info]");
                return jrRtn;
            }
	    	
			/**********************************************************
			* I.2 L2에 기존 장입순번 Clear 정보 송신
			**********************************************************/
			//저장품제원(YMA8L002) 전문 생성
			jparam.setField("MSG_GP"			, "U" 		); //전문구분(U:수정)
			jparam.setField("YD_INFO_SYNC_CD"	, "R" 		); //야드정보동기화코드(지정저장품) R:압연지시(저장위치변경안함)
			jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L002ChgWoClear", jparam));
			
			
			//* 2022.07.04 열연1부 김기훈 주임님 요청. 장입정보 변경시 알림기능
			
			jparam.setField("PTOP_PLNT_GP"		, sPTOP_PLNT_GP			);
			//기존 장입지시 정보 select
			JDTORecordSet rstYd = commDao.select(jparam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.selectSlabStockChargeLotNoFromYd", logId, mthdNm, "YD 장입번호 검색");
			//최신 장입지시 정보 select from CT
			JDTORecordSet rstCt = commDao.select(jparam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.selectSlabStockChargeLotNo", logId, mthdNm, "CT 장입번호 검색");
			
			if(rstYd != null && rstYd.size() > 0 && rstCt != null && rstCt.size() >0 )  {
				commUtils.printLog(logId, "보유압연지시 수 : "+ rstYd.size()+"개, 신규압연지시 수:"+ rstCt.size()+"개", "[info]");
				
				String [][] arrYd = new String[rstYd.size()][2];
				String [][] arrCt = new String[rstCt.size()][2];
				
				for(int i=0; i< rstYd.size(); i++) {
					arrYd[i][0] = rstYd.getRecord(i).getFieldString("STOCK_ID");
					arrYd[i][1] = commUtils.nvl(rstYd.getRecord(i).getFieldString("CHARGE_LOT_NO"),"9999");
				}
				for(int i=0; i< rstCt.size(); i++) {
					arrCt[i][0] = rstCt.getRecord(i).getFieldString("STOCK_ID");
					arrCt[i][1] = commUtils.nvl(rstCt.getRecord(i).getFieldString("CHARGE_LOT_NO"),"9999");
				}
				
				//신규지시의 첫번째 lot번호를 제외하고 비교 시작.
				String minLot = commUtils.nvl(rstCt.getRecord(0).getFieldString("CHARGE_LOT_NO"),"9999");
				int ydIdx = 0;
				int ctIdx = 0;
				boolean isDiffer = false;
				
				//생산통제 신규 장입순서와, 야드 보유 장입순서 비교
				while(ydIdx < rstYd.size() && ctIdx < rstCt.size()){
					if(minLot.equals(arrYd[ydIdx][1])) {
						ydIdx++;
						continue;
					}
					else if(minLot.equals(arrCt[ctIdx][1])) {
						ctIdx++;
						continue;
					}
					
					//불일치 포인트 발견
					if(!arrYd[ydIdx][1].equals(arrCt[ctIdx][1])){
						isDiffer = true;
						break;
					}
					ctIdx++;
					ydIdx++;
					
				}
				
				//모두 같은 상태로 끝났지만 ctIdx 가 남은경우 -- 신규
				if(!isDiffer && ctIdx < rstCt.size()){
					jparam.setField("DTL_ITM1"		, "신규"		);
					jparam.setField("DTL_ITM2"		, arrYd[ydIdx-1][1]);
					jparam.setField("DTL_ITM3"		, arrYd[ydIdx-1][0]);
					jparam.setField("DTL_ITM4"		, rstCt.size()-	ctIdx);
				}
				
				
				//idx가 끝나지 않은 상태에서 서로 다른 point가 나온경우 -- 변경
				if(isDiffer){
					jparam.setField("DTL_ITM1"		, "변경"		);
					jparam.setField("DTL_ITM2"		, arrYd[ydIdx][1]);
					jparam.setField("DTL_ITM3"		, arrYd[ydIdx][0]);
					jparam.setField("DTL_ITM4"		, rstCt.size()-	ctIdx);
				}
				
				jparam.setField("MODIFIER"		, modifier		);
				jparam.setField("DEL_YN"		, "N"			);
				jparam.setField("REPR_CD_GP"	, "MSG001"		);
				jparam.setField("CD_GP"			, "*"			);
				jparam.setField("ITEM"			, "*"			);
				
				/*com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updYmRule
				UPDATE TB_YM_RULE
				   SET DTL_ITM1 = NVL(:V_DTL_ITM1, DTL_ITM1)
				      ,DTL_ITM2 = NVL(:V_DTL_ITM2, DTL_ITM2)
				      ,DTL_ITM3 = NVL(:V_DTL_ITM3, DTL_ITM3)
				      ,DTL_ITM4 = NVL(:V_DTL_ITM4, DTL_ITM4)
				      ,DTL_ITM5 = NVL(:V_DTL_ITM5, DTL_ITM5)
				      ,DTL_ITM6 = NVL(:V_DTL_ITM6, DTL_ITM6)
				      ,DTL_ITM7 = NVL(:V_DTL_ITM7, DTL_ITM7)
				      ,DTL_ITM8 = NVL(:V_DTL_ITM8, DTL_ITM8)
				      ,DTL_ITM9 = NVL(:V_DTL_ITM9, DTL_ITM9)
				      ,DTL_ITM10 = NVL(:V_DTL_ITM10, DTL_ITM10)
				      ,MODIFIER = :V_MODIFIER
				      ,MOD_DDTT = SYSDATE
				      ,DEL_YN = NVL(:V_DEL_YN, DEL_YN) --추가
				 WHERE REPR_CD_GP = :V_REPR_CD_GP
				   AND CD_GP = :V_CD_GP
				   AND ITEM = :V_ITEM
				*/
				
				commDao.update(jparam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updYmRule", logId, mthdNm, "생산통제MSG RULE 수정");
			}
			
			
			//*/
			
			
			
			/**************************
			 * 트랜잭션 분리 20190501
			 **************************/
			String sAPP501_YN = ymComm.BCoilApplyYn("APP501","2","CTYDJ032");
			
			jparam.setResultCode(logId);	//Logging 을 위한 ID
			jparam.setResultMsg(mthdNm);	//Log Method Name
			jparam.setField("PTOP_PLNT_GP"		, sPTOP_PLNT_GP			);
			jparam.setField("STOCK_MOVE_TERM"	, YmConstant.NEW_STOCK_MOVE_TERM_FS	); //이동조건 : 압연작업대기
			jparam.setField("STOCK_ITEM"		, YmConstant.ITEM_SM				); //저장품 품목 : SLAB 소재
			jparam.setField("MODIFIER"			, modifier		);
			
			if ("Y".equals(sAPP501_YN)) {
				// 트랜잭션 분리 updSlabStockChargeLotNo에서 처리				
				EJBConnector ejbConnS = new EJBConnector("default", "BSlabL3RcvSeEJB", this);
				ejbConnS.trx("updSlabStockChargeLotNo", new Class[] { JDTORecord.class }, new Object[] { jparam });
			} else {
				
				/**********************************************************
				* I.2 저장품의 기존 장입순번 Clear
				**********************************************************/
				commDao.update(jparam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updClearSlabStockChargeLotNo", logId, mthdNm, "1열연 SLAB 저장품의 기존 장입순번 Clear");
				
				/**********************************************************
				* I.3 저장품에  새 장입순번 설정하기
				*  1) #3CTC,#4CTC,W/B에 올려진 SLAB는 장입순번을 변경하지 않는다.
				*  2) 저장품 테이블에 존재하면 UPDATE 없으면 INSERT
				**********************************************************/
				commDao.update(jparam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updSlabStockChargeLotNo", logId, mthdNm, "1열연 SLAB 저장품에  새 장입순번 설정하기");
			}

			/**********************************************************
			* I.4 L2 저장품 제원정보 송신
			**********************************************************/
			//저장품제원(YMA8L002) 전문 생성
			jparam.setField("MSG_GP"			, "U" 		); //전문구분(U:수정)
			jparam.setField("YD_INFO_SYNC_CD"	, "R" 		); //야드정보동기화코드(지정저장품) R:압연지시(저장위치변경안함)
			jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L002ChgWoSet", jparam));
			
			
			/**********************************************************
			* 5. 생산통제 작업여부 TC전송(YDCTJ037)
			**********************************************************/
          //전송할 Data가 있으면 전송 처리
			JDTORecord recPara2 = JDTORecordFactory.getInstance().create();
			recPara2.setField("JMS_TC_CD","YDCTJ037");
			recPara2.setField("JMS_TC_CREATE_DDTT"		, YdUtils.getCurDate("yyyyMMddHHmmss"));
			
			if ("D".equals(sMOD_GP)) {
				recPara2.setField("MILL_WO_MD","C"); //취소인 경우 처리
			}else if("I".equals(sMOD_GP) && "1".equals(woMtlCnt)){
				recPara2.setField("MILL_WO_MD","K");	//후판에정지시재 인 경우(2차 전단 시 발생)
			}else{
				recPara2.setField("MILL_WO_MD","B");
			}
			
			recPara2.setField("CHG_WO_FR_PNT",chgWoFrPnt); 
			recPara2.setField("CHG_WO_TO_PNT",chgWoToPnt); 
			recPara2.setField("SLAB_NO",""); 
			recPara2.setField("PLAN_SLAB_NO",""); 
			recPara2.setField("CT_RCV_SEQ", rcvSeq); // PK: 지시ID 
			recPara2.setField("CT_WO_MTL_CNT",woMtlCnt); 
			recPara2.setField("WO_SND_YN", "Y"); 
			 
			EJBConnector sndConn = new EJBConnector("default", "CoilCraneUdHdSeEJB", this);
			sndConn.trx("sndJMSInfo", new Class[] { JDTORecord.class }, new Object[] { recPara2 });
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
			
		} catch (DAOException e) {
			try {
				//생산통제 IT담당자에게 SMS전송 
				JDTORecord recPara2 = JDTORecordFactory.getInstance().create();				 
				recPara2.setField("CT_RCV_SEQ", rcvSeq); // PK: 지시ID
				recPara2.setField("PTOP_PLNT_GP", ptopPlntGp); // PK: 공장구분 
				recPara2.setField("MODIFIER2", 	 msgId);
				recPara2.setField("CHG_WO_FR_PNT", 	 chgWoFrPnt);
				recPara2.setField("CHG_WO_TO_PNT", 	 chgWoToPnt);
				recPara2.setField("CT_WO_MTL_CNT",	 woMtlCnt); 

				EJBConnector SMSMSG = new EJBConnector("default", "BSlabL3RcvSeEJB", this);
				SMSMSG.trx( "rcvCTYMSMS" , new Class[] { JDTORecord.class }, new Object[] { recPara2 });
			
			} catch (Exception ex) {
				throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, ex));
			}
			
			throw e;
		} catch (Exception e) {
			try {
					//생산통제 IT담당자에게 SMS전송 
					JDTORecord recPara2 = JDTORecordFactory.getInstance().create();					
					recPara2.setField("CT_RCV_SEQ", rcvSeq); // PK: 지시ID
					recPara2.setField("PTOP_PLNT_GP", ptopPlntGp); // PK: 공장구분 
					recPara2.setField("MODIFIER2", 	 msgId);
					recPara2.setField("CHG_WO_FR_PNT", 	 chgWoFrPnt);
					recPara2.setField("CHG_WO_TO_PNT", 	 chgWoToPnt);
					recPara2.setField("CT_WO_MTL_CNT",	 woMtlCnt); 


					EJBConnector SMSMSG = new EJBConnector("default", "BSlabL3RcvSeEJB", this);
					SMSMSG.trx( "rcvCTYMSMS" , new Class[] { JDTORecord.class }, new Object[] { recPara2 });
				
			} catch (Exception ex) {
				throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, ex));
			}
				
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	} //rcvCTYDJ032
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 압연지시 실패 시 SMS 문자 전송 기능
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvCTYMSMS(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "1열연압연지시확정 SMS 문자 전송 기능[BSlabL3RcvSeEJB.rcvCTYMSMS] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = null;
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+");
			
			String rcvSeq       = commUtils.trim(rcvMsg.getFieldString("CT_RCV_SEQ"   )); 
			String ptopPlntGp   = commUtils.trim(rcvMsg.getFieldString("PTOP_PLNT_GP" )); 
			String msgId      	= commUtils.trim(rcvMsg.getFieldString("MODIFIER2"    )); 
			String chgWoFrPnt 	= commUtils.trim(rcvMsg.getFieldString("CHG_WO_FR_PNT")); //장입지시FromPoint
			String chgWoToPnt 	= commUtils.trim(rcvMsg.getFieldString("CHG_WO_TO_PNT")); //장입지시ToPoint
			String woMtlCnt   	= commUtils.trim(rcvMsg.getFieldString("CT_WO_MTL_CNT")); //건수
			
			//생산통제 IT담당자에게 SMS전송
			String rtnMsg = "";
			String rtnMsg2 = "";
			JDTORecord recPara1 = JDTORecordFactory.getInstance().create();	 
			JDTORecordSet smsListSet; 
			
			/**********************************************************
			* 1. SMS 전송 목록 조회
			**********************************************************/ 
			recPara1.setField("REGISTER" , msgId);
			smsListSet = commDao.select(recPara1, "com.inisteel.cim.yd.monitoring.dao.MonitoringCommDAO.getCTSMSLog", logId, methodNm, "CT SMS 전송 목록 조회"); 
			
			rtnMsg2 =msgId+ " 1열연 압연지시 야드 수신 중 오류 발생\n 지시번호:" + rcvSeq;
			
			/**********************************************************
			* 2. SMS 전송
			**********************************************************/
			if(smsListSet != null && smsListSet.size() > 0) {
				rtnMsg = "";
				
				for(int i=0; i<smsListSet.size(); i++) {
					JDTORecord recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("FROM_PHONE_NO", "0416801616");	
					recPara.setField("TO_PHONE_NO"  , smsListSet.getRecord(i).getFieldString("HANDPHONE_NO")); // 010-XXXX-XXXX
					recPara.setField("TO_CONTENT"   , rtnMsg2);
					//rtnMsg = PlateGdsYdUtil.updSmsMsgSend(recPara); // SMS 송신 //2025.07.02 주석처리
				}
			}
			
		 
			/**********************************************************
			* 3. 생산통제 UPDATE
			**********************************************************/
			//생산통제 테이블 update
//			JDTORecord recPara2 = JDTORecordFactory.getInstance().create();
//			recPara2.setField("CT_RCV_SEQ", rcvSeq); // PK: 지시ID
//			recPara2.setField("PTOP_PLNT_GP", ptopPlntGp); // PK: 공장구분
//			recPara2.setField("WO_SND_YN2", "N");
//			recPara2.setField("MODIFIER2", 	 msgId);
			
//			commDao.update(recPara2, "com.inisteel.cim.yd.common.dao.YdCommDAO.updCtMillwoidxTbl", logId, methodNm, "작업지시 성공여부 update");
			
			 
			/**********************************************************
			* 4. 생산통제 작업여부 TC전송(YDCTJ037)
			**********************************************************/
          //전송할 Data가 있으면 전송 처리
			JDTORecord recPara2 = JDTORecordFactory.getInstance().create();
			recPara2.setField("JMS_TC_CD","YDCTJ037");
			recPara2.setField("JMS_TC_CREATE_DDTT"		, YdUtils.getCurDate("yyyyMMddHHmmss"));
			
			if ("".equals(rcvSeq)) {
				recPara2.setField("MILL_WO_MD","C"); //취소인 경우 처리
			}else{
				recPara2.setField("MILL_WO_MD","B");
			}
			recPara2.setField("CHG_WO_FR_PNT",chgWoFrPnt); 
			recPara2.setField("CHG_WO_TO_PNT",chgWoToPnt); 
			recPara2.setField("SLAB_NO",""); 
			recPara2.setField("PLAN_SLAB_NO",""); 
			recPara2.setField("CT_RCV_SEQ", rcvSeq); // PK: 지시ID 
			recPara2.setField("CT_WO_MTL_CNT",woMtlCnt); 
			recPara2.setField("WO_SND_YN", "N"); 
				
			EJBConnector sndConn = new EJBConnector("default", "CoilCraneUdHdSeEJB", this);
			sndConn.trx("sndJMSInfo", new Class[] { JDTORecord.class }, new Object[] { recPara2 });
			 
			
			
			commUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 * 저장품 새 장입순번 설정하기
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord updSlabStockChargeLotNo(JDTORecord rcvMsg) throws DAOException {
		
		String mthdNm = "저장품 새 장입순번 설정[BSlabL3RcvSeEJB.updSlabStockChargeLotNo] <" + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		
		try {
			
			commUtils.printLog(logId, mthdNm, "S+");
			
			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

			String sPTOP_PLNT_GP	= rcvMsg.getFieldString("PTOP_PLNT_GP");	 
			String sSTOCK_MOVE_TERM = rcvMsg.getFieldString("STOCK_MOVE_TERM");
			String sSTOCK_ITEM	    = rcvMsg.getFieldString("STOCK_ITEM");	   
			String sMODIFIER        = rcvMsg.getFieldString("MODIFIER");       		

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jparam = JDTORecordFactory.getInstance().create();
			jparam.setField("PTOP_PLNT_GP"		, sPTOP_PLNT_GP    );	
			jparam.setField("STOCK_MOVE_TERM"	, sSTOCK_MOVE_TERM );
			jparam.setField("STOCK_ITEM"		, sSTOCK_ITEM	   );
			jparam.setField("MODIFIER"			, sMODIFIER        );
			
			/* 저장품의 기존 장입순번 Clear
			 * 
			UPDATE TB_YM_STOCK
			   SET  MODIFIER      = :V_MODIFIER
			       ,MOD_DDTT      = SYSDATE
			       ,CHARGE_LOT_NO = ''
			 WHERE  CHARGE_LOT_NO IS NOT NULL   
			 */
			commDao.update(jparam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updClearSlabStockChargeLotNo", logId, mthdNm, "B열연 SLAB 저장품의 기존 장입순번 Clear");
			
			/* 저장품에  새 장입순번 설정하기
 			 *  1) #3CTC,#4CTC,W/B에 올려진 SLAB는 장입순번을 변경하지 않는다.
			 *  2) 저장품 테이블에 존재하면 UPDATE 없으면 INSERT
			 *  
			MERGE INTO TB_YM_STOCK ST USING (
			
			    SELECT  C.STL_NO     AS STOCK_ID        -- 재료번호
			           ,C.YD_CHG_NO  AS CHARGE_LOT_NO   -- 야드장입순위
			      FROM  USRCTA.TB_CT_L_HRMILLWO C  	    -- CT_열연압연지시
			           ,(SELECT  CHG_WO_FR_PNT AS CHG_WO_FR_PNT
			                    ,CHG_WO_TO_PNT AS CHG_WO_TO_PNT
			               FROM  USRCTA.TB_CT_J_MILLWOIDX
			               WHERE CT_RCV_SEQ = (SELECT MAX(CT_RCV_SEQ) AS MAX_CT_RCV_SEQ
			                                     FROM USRCTA.TB_CT_J_MILLWOIDX
			                                    WHERE PTOP_PLNT_GP= :V_PTOP_PLNT_GP ) 
			                 AND PTOP_PLNT_GP= :V_PTOP_PLNT_GP) D
			           ,TB_CT_K_HRMILLSPEC     E	 -- CT_열연압연사양
			     WHERE  C.REFUR_CHG_PLN_SERNO >=D.CHG_WO_FR_PNT
			       AND  C.REFUR_CHG_PLN_SERNO <=D.CHG_WO_TO_PNT 
			       AND  C.PTOP_PLNT_GP= :V_PTOP_PLNT_GP
			       AND  C.CT_MILL_SPEC_WRK_STAT_GP IN ('3','4')   -- 생산통제사양작업상태구분
			       AND  E.CT_MILL_SPEC_WRK_STAT_GP IN ('3','4')   -- 생산통제사양작업상태구분
			       AND  E.CT_MILL_SCH_WRK_STAT_GP  IN ('3','4')   -- 생산통제스케줄작업상태구분 
			       AND  E.STL_NO = C.STL_NO 
			       AND  C.STL_NO NOT IN (
			       
			                --관제확정시 순위변경이 안되어야 할 슬라브정보 리턴한다.
			                SELECT  NVL(STOCK_ID,1) AS STOCK_ID
			                FROM    TB_YM_STACKLAYER
			                WHERE   STACK_COL_GP    = '2BCT03'
			                AND     STACK_BED_GP    = '01'
			                AND     STACK_LAYER_GP  = '01'
			                AND     STACK_LAYER_STAT IN ('C', 'L')
			                UNION
			                SELECT  NVL(STOCK_ID,1) AS STOCK_ID
			                FROM    TB_YM_STACKLAYER
			                WHERE   STACK_COL_GP    = '2CCT04'
			                AND     STACK_BED_GP    = '01'
			                AND     STACK_LAYER_GP  = '01'
			                AND     STACK_LAYER_STAT IN ('C', 'L')
			                UNION
			                SELECT  NVL(STOCK_ID,1) AS STOCK_ID
			                FROM    TB_YM_STACKLAYER
			                WHERE   STACK_COL_GP LIKE '2CWB%'
			                AND     STACK_LAYER_STAT IN ('C', 'L')
			       
			            )
			
			) DD ON (ST.STOCK_ID = DD.STOCK_ID)
			
			WHEN MATCHED THEN 
			
			    UPDATE SET CHARGE_LOT_NO    = DD.CHARGE_LOT_NO
			             , STOCK_MOVE_TERM  = :V_STOCK_MOVE_TERM
			             , YD_AIM_RT_GP     = :V_STOCK_MOVE_TERM
			             , MODIFIER         = :V_MODIFIER
			             , MOD_DDTT         = SYSDATE     
			
			WHEN NOT MATCHED THEN
			
			    INSERT (
			          STOCK_ID
			         ,STOCK_ITEM
			         ,STOCK_MOVE_TERM
			         ,YD_AIM_RT_GP
			         ,CHARGE_LOT_NO
			         ,REGISTER
			         ,REG_DDTT
			         ,DEL_YN
			    ) VALUES (
			          DD.STOCK_ID
			         ,:V_STOCK_ITEM
			         ,:V_STOCK_MOVE_TERM
			         ,:V_STOCK_MOVE_TERM
			         ,DD.CHARGE_LOT_NO
			         ,:V_MODIFIER
			         ,SYSDATE
			         ,'N'
			    ) 
			 */
			commDao.update(jparam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updSlabStockChargeLotNo", logId, mthdNm, "B열연 SLAB 저장품에  새 장입순번 설정하기");
			
			commUtils.printLog(logId, mthdNm, "S-");
			
			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	} // end of updSlabStockChargeLotNo	
	
	
	/**	
	 *      [A] 오퍼레이션명 : SLAB No Action Method
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvNoActMtd(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "SLAB No Action Methode[BSlabSchSeEJB.rcvNoActMtd] < " + rcvMsg.getResultMsg();
		
		String logId = rcvMsg.getResultCode();
		JDTORecordSet jsWbook = JDTORecordFactory.getInstance().createRecordSet("Temp");
		int intRtnVal = 0;
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();;	//전문 Return
		try {
			commUtils.printLog(logId, methodNm, "S+");

			/*
			 YDYDJ630 
			 YD에서 수행되고 YM에서는 SKIP 하는 TC
			*/
			
			commUtils.printLog(logId, methodNm, "S-");
			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	//rcvNoActMtd
	
	/**	
	 *      [A] 오퍼레이션명 : SLAB 고도화 실행 로그(YMYMJ204)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYMYMJ204(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "SLAB 고도화 실행 로그 [BSlabSchSeEJB.rcvYMYMJ204] < " + rcvMsg.getResultMsg();
		
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();;	//전문 Return
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			
			String sYM2011_ADV_LOG_YN = ymComm.BCoilApplyYn("YM2011","2","ADV_LOG_YN");
			
			if("Y".equals(sYM2011_ADV_LOG_YN)) {
				
				//TB_YM_IFTCLOG 테이블에 INSERT
				jrParam.setField("YD_GP"		 , commUtils.nvl(rcvMsg.getFieldString("YD_GP"),"2"));
				jrParam.setField("TC_CD"		 , commUtils.nvl(rcvMsg.getFieldString("TC_CD"),"YMYM204"));
				jrParam.setField("CONTENTS"		 , commUtils.trim(rcvMsg.getFieldString("CONTENTS")));
				jrParam.setField("MSG"			 , commUtils.trim(rcvMsg.getFieldString("MSG")));
				jrParam.setField("TYPE"			 , commUtils.trim(rcvMsg.getFieldString("TYPE")));
				jrParam.setField("TYPE_CONTENTS" , commUtils.trim(rcvMsg.getFieldString("TYPE_CONTENTS")));
				
				bSlabDao.insert(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.insIFTCLOG", logId, methodNm, "TB_YM_IFTCLOG 테이블에 INSERT");
				
			}

			commUtils.printLog(logId, methodNm, "S-");
			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 *      [A] 오퍼레이션명 : SLAB 장입대상재 동간이적 자동 편성(YMYMJ207)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYMYMJ207(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "SLAB 장입대상재 동간이적 자동 편성 [BSlabSchSeEJB.rcvYMYMJ207] < " + rcvMsg.getResultMsg();
		
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();;	//전문 Return
		JDTORecordSet rsResult    	= null;
		JDTORecordSet rsResult2    	= null;
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			String sAPP103_2_ADV_YN = ymComm.BCoilApplyYn("APP103","2","1_ADV_YN");  
			
			commUtils.printLog(logId,  "==========[[[ SLAB 장입대상재 동간이적 자동 편성여부 :" + sAPP103_2_ADV_YN + " ]]]============", "SL");
			
			if("Y".equals(sAPP103_2_ADV_YN)) {

				//------------------------------------------------------------------------------------------
				String sTG_SH = "14"; //장입대상재 자동편성 대상 매수
				
				jrParam.setField("REPR_CD_GP"	, "APP108");
				jrParam.setField("CD_GP"		, "2");
				jrParam.setField("ITEM"			, "1_TG_SH");
				rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmRule", logId, methodNm, "장입대상재 자동편성 대상 매수 조회"); 
				
				if(rsResult2.size() > 0) {
					sTG_SH = rsResult2.getRecord(0).getFieldString("DTL_ITM1");
				}							
				
				//------------------------------------------------------------------------------------------
				String sA_BAY_YN = "N"; //A동 장입대상재 포함여부
				String sB_BAY_YN = "N"; //B동 장입대상재 포함여부
				String sD_BAY_YN = "N"; //D동 장입대상재 포함여부
				String sE_BAY_YN = "N"; //E동 장입대상재 포함여부
				
				jrParam.setField("REPR_CD_GP"	, "APP103");
				jrParam.setField("CD_GP"		, "2");
				
				jrParam.setField("ITEM"			, "A_BAY_YN");
				rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmRule", logId, methodNm, "A동 장입대상재 포함여부 조회"); 
				if(rsResult2.size() > 0) {
					sA_BAY_YN = rsResult2.getRecord(0).getFieldString("DTL_ITM1");
				}
				
				jrParam.setField("ITEM"			, "B_BAY_YN");
				rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmRule", logId, methodNm, "B동 장입대상재 포함여부 조회"); 
				if(rsResult2.size() > 0) {
					sB_BAY_YN = rsResult2.getRecord(0).getFieldString("DTL_ITM1");
				}
				
				jrParam.setField("ITEM"			, "D_BAY_YN");
				rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmRule", logId, methodNm, "D동 장입대상재 포함여부 조회"); 
				if(rsResult2.size() > 0) {
					sD_BAY_YN = rsResult2.getRecord(0).getFieldString("DTL_ITM1");
				}
				
				jrParam.setField("ITEM"			, "E_BAY_YN");
				rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmRule", logId, methodNm, "E동 장입대상재 포함여부 조회"); 
				if(rsResult2.size() > 0) {
					sE_BAY_YN = rsResult2.getRecord(0).getFieldString("DTL_ITM1");
				}
				
				//------------------------------------------------------------------------------------------
				String sA_BAY_TC = "2XTC02"; //A동 장입재 동가이적 자동편성 대차지정
				String sB_BAY_TC = "2XTC02"; //B동 장입재 동가이적 자동편성 대차지정
				String sD_BAY_TC = "2XTC03"; //D동 장입재 동가이적 자동편성 대차지정
				String sE_BAY_TC = "2XTC01"; //E동 장입재 동가이적 자동편성 대차지정
				
				jrParam.setField("REPR_CD_GP"	, "APP109");
				jrParam.setField("CD_GP"		, "2");
				
				jrParam.setField("ITEM"			, "A_BAY_TC");
				rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmRule", logId, methodNm, "A동 장입재 동가이적 자동편성 대차지정 조회"); 
				if(rsResult2.size() > 0) {
					sA_BAY_TC = commUtils.nvl(rsResult2.getRecord(0).getFieldString("DTL_ITM1"),"2XTC02");
				}
				
				jrParam.setField("ITEM"			, "B_BAY_TC");
				rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmRule", logId, methodNm, "B동 장입재 동가이적 자동편성 대차지정 조회"); 
				if(rsResult2.size() > 0) {
					sB_BAY_TC = commUtils.nvl(rsResult2.getRecord(0).getFieldString("DTL_ITM1"),"2XTC02");
				}
				
				jrParam.setField("ITEM"			, "D_BAY_TC");
				rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmRule", logId, methodNm, "D동 장입재 동가이적 자동편성 대차지정 조회"); 
				if(rsResult2.size() > 0) {
					sD_BAY_TC = commUtils.nvl(rsResult2.getRecord(0).getFieldString("DTL_ITM1"),"2XTC03");
				}
				
				jrParam.setField("ITEM"			, "E_BAY_TC");
				rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmRule", logId, methodNm, "E동 장입재 동가이적 자동편성 대차지정 조회"); 
				if(rsResult2.size() > 0) {
					sE_BAY_TC = commUtils.nvl(rsResult2.getRecord(0).getFieldString("DTL_ITM1"),"2XTC01");
				}
				
				//------------------------------------------------------------------------------------------
				String sBAY = "";
				if("Y".equals(sA_BAY_YN)) {
					sBAY += "A,";
				}
				if("Y".equals(sB_BAY_YN)) {
					sBAY += "B,";
				}
				if("Y".equals(sD_BAY_YN)) {
					sBAY += "D,";
				}
				if("Y".equals(sE_BAY_YN)) {
					sBAY += "E,";
				}
				
				//------------------------------------------------------------------------------------------
				// 장입 대상재 조회
				jrParam.setField("BAY"		, sBAY);
				jrParam.setField("TG_SH"	, sTG_SH);
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getChgTgList", logId, methodNm, "장입 대상재 조회");
				
				if(rsResult.size() > 0) {
					
					String sA_ARR_STOCK_ID = "";
					String sB_ARR_STOCK_ID = "";
					String sD_ARR_STOCK_ID = "";
					String sE_ARR_STOCK_ID = "";
					
					String sSTOCK_ID = "";
					String sFROM_ADDR = "";
					
					for (int ii = 0; ii < rsResult.size(); ii++ ) {
					
						sSTOCK_ID	= rsResult.getRecord(ii).getFieldString("STOCK_ID");
						sFROM_ADDR	= rsResult.getRecord(ii).getFieldString("FROM_ADDR");
						
						if("A".equals(sFROM_ADDR.substring(1,2))) {
							sA_ARR_STOCK_ID = sA_ARR_STOCK_ID + sSTOCK_ID + "," ;
							
						} else if("B".equals(sFROM_ADDR.substring(1,2))) {
							sB_ARR_STOCK_ID = sB_ARR_STOCK_ID + sSTOCK_ID + "," ;
							
						} else if("D".equals(sFROM_ADDR.substring(1,2))) {
							sD_ARR_STOCK_ID = sD_ARR_STOCK_ID + sSTOCK_ID + "," ;
						
						} else if("E".equals(sFROM_ADDR.substring(1,2))) {
							sE_ARR_STOCK_ID = sE_ARR_STOCK_ID + sSTOCK_ID + "," ;
							
						}
					}
					
					
					if(!"".equals(sA_ARR_STOCK_ID)) {
						
						commUtils.printLog(logId,  "==========[[[ A동 procYMYMJ207_SUB 호출 :" + sA_ARR_STOCK_ID + " ]]]============", "SL");
						JDTORecord jrYdMsg1 = JDTORecordFactory.getInstance().create();
						jrYdMsg1.setResultCode(logId);	//Log ID
						jrYdMsg1.setResultMsg(methodNm);	//Log Method Name
						jrYdMsg1.setField("ARR_STOCK_ID"			, sA_ARR_STOCK_ID);
						jrYdMsg1.setField("STACK_COL_GP"			, "2A");
						jrYdMsg1.setField("YD_TO_LOC_GUIDE"			, "2C");
						jrYdMsg1.setField("YD_WRK_PLAN_TCAR"		, sA_BAY_TC);
						jrYdMsg1.setField("YD_WRK_PLAN_CRN"			, "2ACRA1");
						jrYdMsg1.setField("YD_WRK_PLAN_CRN2"		, "");
						jrYdMsg1.setField("CHARGE_LOT_NO_DIV_YN"	, "Y");
						jrRtn = commUtils.addSndData(jrRtn			, this.procYMYMJ207_SUB(jrYdMsg1));
						
					} else if(!"".equals(sB_ARR_STOCK_ID)) {
						commUtils.printLog(logId,  "==========[[[ B동 procYMYMJ207_SUB 호출 :" + sB_ARR_STOCK_ID + " ]]]============", "SL");
						JDTORecord jrYdMsg1 = JDTORecordFactory.getInstance().create();
						jrYdMsg1.setResultCode(logId);	//Log ID
						jrYdMsg1.setResultMsg(methodNm);	//Log Method Name
						jrYdMsg1.setField("ARR_STOCK_ID"			, sB_ARR_STOCK_ID);
						jrYdMsg1.setField("STACK_COL_GP"			, "2B");
						jrYdMsg1.setField("YD_TO_LOC_GUIDE"			, "2C");
						jrYdMsg1.setField("YD_WRK_PLAN_TCAR"		, sB_BAY_TC);
						jrYdMsg1.setField("YD_WRK_PLAN_CRN"			, "2BCRB1");
						jrYdMsg1.setField("YD_WRK_PLAN_CRN2"		, "");
						jrYdMsg1.setField("CHARGE_LOT_NO_DIV_YN"	, "Y");
						jrRtn = commUtils.addSndData(jrRtn, this.procYMYMJ207_SUB(jrYdMsg1));
					}
					
					if(!"".equals(sD_ARR_STOCK_ID)) {
						commUtils.printLog(logId,  "==========[[[ D동 procYMYMJ207_SUB 호출 :" + sD_ARR_STOCK_ID + " ]]]============", "SL");
						JDTORecord jrYdMsg1 = JDTORecordFactory.getInstance().create();
						jrYdMsg1.setResultCode(logId);	//Log ID
						jrYdMsg1.setResultMsg(methodNm);	//Log Method Name
						jrYdMsg1.setField("ARR_STOCK_ID"			, sD_ARR_STOCK_ID);
						jrYdMsg1.setField("STACK_COL_GP"			, "2D");
						jrYdMsg1.setField("YD_TO_LOC_GUIDE"			, "2C");
						jrYdMsg1.setField("YD_WRK_PLAN_TCAR"		, sD_BAY_TC);
						jrYdMsg1.setField("YD_WRK_PLAN_CRN"			, "2DCRD1");
						jrYdMsg1.setField("YD_WRK_PLAN_CRN2"		, "");
						jrYdMsg1.setField("CHARGE_LOT_NO_DIV_YN"	, "Y");
						jrRtn = commUtils.addSndData(jrRtn, this.procYMYMJ207_SUB(jrYdMsg1));
					}
					
					if(!"".equals(sE_ARR_STOCK_ID)) {
						commUtils.printLog(logId,  "==========[[[ E동 procYMYMJ207_SUB 호출 :" + sE_ARR_STOCK_ID + " ]]]============", "SL");
						JDTORecord jrYdMsg1 = JDTORecordFactory.getInstance().create();
						jrYdMsg1.setResultCode(logId);	//Log ID
						jrYdMsg1.setResultMsg(methodNm);	//Log Method Name
						jrYdMsg1.setField("ARR_STOCK_ID"			, sE_ARR_STOCK_ID);
						jrYdMsg1.setField("STACK_COL_GP"			, "2E");
						jrYdMsg1.setField("YD_TO_LOC_GUIDE"			, "2C");
						jrYdMsg1.setField("YD_WRK_PLAN_TCAR"		, sE_BAY_TC);
						jrYdMsg1.setField("YD_WRK_PLAN_CRN"			, "2ECRE1");
						jrYdMsg1.setField("YD_WRK_PLAN_CRN2"		, "");
						jrYdMsg1.setField("CHARGE_LOT_NO_DIV_YN"	, "Y");
						jrRtn = commUtils.addSndData(jrRtn, this.procYMYMJ207_SUB(jrYdMsg1));
					}
				}
			}
			
			commUtils.printLog(logId, methodNm, "S-");
			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 *      [A] 오퍼레이션명 : SLAB 장입대상재 동간이적 자동 편성 SUB (YMYMJ207)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord procYMYMJ207_SUB(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "SLAB 장입대상재 동간이적 자동 편성 SUB [BSlabSchSeEJB.procYMYMJ207_SUB] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn = null;

			String stlNos        = commUtils.trim(rcvMsg.getFieldString("ARR_STOCK_ID"    )); //재료번호들
			String sSTACK_COL_GP = commUtils.trim(rcvMsg.getFieldString("STACK_COL_GP"    )); //야드적치열구분(동까지 2자리 EX: 2D, 2E)
			String ydToLocGuide  = commUtils.trim(rcvMsg.getFieldString("YD_TO_LOC_GUIDE" )); //야드To위치Guide
			String ydWrkPlanTcar = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PLAN_TCAR")); //야드작업계획대차
			String ydWrkPlanCrn  = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PLAN_CRN" )); //야드지정크레인
			String ydWrkPlanCrn2 = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PLAN_CRN2")); //야드지정크레인(멀티크레인일 경우 사용)
			String sCHARGE_LOT_NO_DIV_YN = commUtils.trim(rcvMsg.getFieldString("CHARGE_LOT_NO_DIV_YN")); //장입순번 분리여부
			
			if (sSTACK_COL_GP.length() < 4) {
				//혹시 이적 적치열구분 값이 잘못되어 있으면 무조건 01 Span 으로 처리
				sSTACK_COL_GP = sSTACK_COL_GP.substring(0, 2) + "01";
			} else if (sSTACK_COL_GP.length() > 6) {
				sSTACK_COL_GP = sSTACK_COL_GP.substring(0, 6);
			}

			if ("".equals(stlNos)) {
				throw new Exception("이적 재료번호가 없습니다.");
			} else if ("".equals(sSTACK_COL_GP) || sSTACK_COL_GP.length() < 4) {
				throw new Exception("Span[" + sSTACK_COL_GP + "] 정보가 없습니다.");
			} 
//			else if ("".equals(ydWrkPlanCrn)) {
//				throw new Exception("지정된 크레인이 없습니다.");
//			}
			
			//DAO Parameter - Log ID, Method, 수정자 Set 
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(rcvMsg.getFieldString("MODIFIER")));
			
			/**********************************************************
			* 1. 스케줄코드 설정
			**********************************************************/
			String ydSchCd    = ""; //야드스케쥴코드
			String ydBayGp    = sSTACK_COL_GP.substring(1, 2); //야드동구분
			String ydAimBayGp = ""; //야드목표동구분

			if ("".equals(ydToLocGuide)) {
				//위치검색Bed기준 적용
				ydAimBayGp = sSTACK_COL_GP.substring(1, 2);
			} else {
				//To위치지정
				ydAimBayGp = ydToLocGuide.substring(1, 2);
				//To위치가 동까지만 있으면 위치검색Bed 기준 적용
				if (ydToLocGuide.length() < 4) {
					ydToLocGuide = "";
				} 
			}

			//스케쥴코드
			String sCraneNo = ydWrkPlanCrn.substring(5, 6);
			
			if (ydBayGp.equals(ydAimBayGp)) {  
				
				//동내이적
				if (!"".equals(ydWrkPlanCrn)) { //지정크레인 유무
					ydSchCd = sSTACK_COL_GP.substring(0, 2) + "YD" + sCraneNo + "1MM";	
				} else {
					ydSchCd = sSTACK_COL_GP.substring(0, 2) + "YD11MM";
				}
				
				ydWrkPlanTcar = "";
				
			} else {
				
				if ("".equals(ydWrkPlanTcar)) {
					throw new Exception("To위치지정 동간이적 대차 정보가 없습니다.");
				}
				
				//동간이적
				if (!"".equals(ydWrkPlanCrn)) { //지정크레인 유무
					if ("1".equals(ydWrkPlanCrn.substring(5, 6))) {
						ydSchCd = sSTACK_COL_GP.substring(0, 2) + "TC11UM";
					} else if ("2".equals(ydWrkPlanCrn.substring(5, 6))) {
						ydSchCd = sSTACK_COL_GP.substring(0, 2) + "TC22UM";
					} else {
						ydSchCd = sSTACK_COL_GP.substring(0, 2) + "TC11UM";
					}
				} else {
					ydSchCd = sSTACK_COL_GP.substring(0, 2) + "TC11UM";
				}
			} 
			
			/**********************************************************
			 * 2. 동간이적일 경우 대차기준 변경 및 대차스케줄 수정
			 **********************************************************/
			if (!ydBayGp.equals(ydAimBayGp)) {  
				
				jrParam.setField("EQUIP_GP"        , ydWrkPlanTcar); //대차번호
				jrParam.setField("YD_WRK_PLAN_TCAR", ydWrkPlanTcar); //대차번호
				JDTORecordSet equipInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.selectEquipInfo");
				
				if (equipInfo.size() <= 0) {
					throw new Exception("지정된 대차설비 정보가 없습니다.");
				} 
				
				String sCURR_STOP_LOC      = commUtils.trim(equipInfo.getRecord(0).getFieldString("CURR_STOP_LOC"     )); //현재 정지 위치  
				String sCARLD_SCH_CD	   = commUtils.trim(equipInfo.getRecord(0).getFieldString("CARLD_SCH_CD"	  )); //상차스케줄코드
				String sCARUD_SCH_CD	   = commUtils.trim(equipInfo.getRecord(0).getFieldString("CARUD_SCH_CD"	  )); //하차스케줄코드
				
				String sCURR_BAY = sCURR_STOP_LOC.substring(1, 2); //대차현재위치동
				
				/*
				SELECT COUNT(*) AS WRK_CNT
				  FROM TB_YM_WRKBOOK
				 WHERE YD_GP  = '2'
				   AND DEL_YN = 'N'
				   AND YD_WRK_PLAN_TCAR = :V_YD_WRK_PLAN_TCAR
				 */
				JDTORecordSet jrWbCnt = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getWrkBookCntByTcar", logId, methodNm, "대차별 작업예약 개수");
				
				int nWRK_CNT = 99;
				if (jrWbCnt.size() > 0) {
					nWRK_CNT = Integer.parseInt(jrWbCnt.getRecord(0).getFieldString("WRK_CNT"));
				}
				
				if (nWRK_CNT == 0) {
					
					if(!sCARLD_SCH_CD.equals(sCARUD_SCH_CD)) {
						//상차스케줄과 하차스케줄이 다를 경우만
						
						//해당 대차의 작업이 존재 하지 않을 경우 대차기준 상하차동 수정 
						/*
						UPDATE TB_YM_EQUIP
						   SET MODIFIER          = :V_MODIFIER
						     , MOD_DDTT          = SYSDATE
						     , CARLOAD_STOP_LOC  =(SELECT ITEM 
						                             FROM TB_YM_RULE
						                            WHERE REPR_CD_GP = 'TCAR01'
						                              AND DEL_YN     = 'N'
						                              AND DTL_ITM1   = :V_EQUIP_GP
						                              AND DTL_ITM2   = :V_CARLOAD_STOP_LOC
						                              AND ROWNUM     = 1)
						     , CARUNLOAD_STOP_LOC =(SELECT ITEM 
						                             FROM TB_YM_RULE
						                            WHERE REPR_CD_GP = 'TCAR01'
						                              AND DEL_YN     = 'N'
						                              AND DTL_ITM1   = :V_EQUIP_GP
						                              AND DTL_ITM2   = :V_CARUNLOAD_STOP_LOC
						                              AND ROWNUM     = 1)                             
						 WHERE YD_GP = '2'
						   AND EQUIP_GP = :V_EQUIP_GP
						 */
						jrParam.setField("CARLOAD_STOP_LOC"  , ydBayGp); // 상차동
						jrParam.setField("CARUNLOAD_STOP_LOC", ydAimBayGp); // 하차동
						commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updTcarInfo");
					}
					
					//대차가 현재동에 있으면 대차스케줄 상/하차위치 수정
					if (ydBayGp.equals(sCURR_BAY)) {
						/*
						UPDATE TB_YM_TCARSCH
						   SET YD_CARLD_STOP_LOC =(SELECT ITEM 
						                             FROM TB_YM_RULE
						                            WHERE REPR_CD_GP = 'TCAR01'
						                              AND DEL_YN     = 'N'
						                              AND DTL_ITM1   = :V_EQUIP_GP
						                              AND DTL_ITM2   = :V_YD_CARLD_STOP_LOC
						                              AND ROWNUM     = 1)
						     , YD_CARUD_STOP_LOC =(SELECT ITEM 
						                             FROM TB_YM_RULE
						                            WHERE REPR_CD_GP = 'TCAR01'
						                              AND DEL_YN     = 'N'
						                              AND DTL_ITM1   = :V_EQUIP_GP
						                              AND DTL_ITM2   = :V_YD_CARUD_STOP_LOC
						                              AND ROWNUM     = 1)       
						     , MODIFIER          = :V_MODIFIER
						     , MOD_DDTT          = SYSDATE
						 WHERE DEL_YN    = 'N'
						   AND YD_EQP_ID = :V_YD_EQP_ID
						 */
						jrParam.setField("YD_CARLD_STOP_LOC", ydBayGp); //
						jrParam.setField("YD_CARUD_STOP_LOC", ydAimBayGp); //
						jrParam.setField("YD_EQP_ID"        , ydWrkPlanTcar); //대차번호
						commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updTcarSch");
					}
				}
			}			
			
			/**********************************************************
			* 3. 이적 작업예약 등록
			**********************************************************/
			jrParam.setField("ARR_STOCK_ID", stlNos    ); //재료번호들
			jrParam.setField("STACK_COL_GP", sSTACK_COL_GP); //야드적치열구분

			//작업예약 대상재료 조회
			/*
			SELECT SL.STOCK_ID
			     , SL.STACK_COL_GP
			     , SL.STACK_BED_GP
			     , SL.STACK_LAYER_GP
			     , SC.SLAB_WT
			     , SC.SLAB_T
			     , SC.SLAB_W
			     , SC.SLAB_LEN
			     , TO_CHAR(SC.SLAB_T)||' X '||TO_CHAR(SC.SLAB_W,'FM9,999') || ' X '||TO_CHAR(SC.SLAB_LEN,'FM99,999') AS MTL_SIZE
			     , SL.STACK_COL_GP||SL.STACK_BED_GP||'-'||SL.STACK_LAYER_GP AS YM_STR_LOC
			     , MIN(NVL(CHARGE_LOT_NO, '999999')) OVER(PARTITION BY STACK_COL_GP, STACK_BED_GP) AS MIN_CHARGE_LOT_NO
			  FROM TB_YM_STACKLAYER SL
			     , TB_YM_STOCK      ST
			     ,(SELECT REGEXP_SUBSTR(SSTL_NOS, '[^,]+', 1, LEVEL) AS STOCK_ID
			         FROM (SELECT :V_ARR_STOCK_ID AS SSTL_NOS FROM DUAL)
			      CONNECT BY REGEXP_SUBSTR(SSTL_NOS, '[^,]+', 1, LEVEL) IS NOT NULL) SN  -- 입력 배열
			     , VW_YD_SLABCOMM   SC
			 WHERE SL.STOCK_ID = SN.STOCK_ID
			   AND SL.STOCK_ID = ST.STOCK_ID
			   AND SL.STOCK_ID = SC.SLAB_NO
			   AND SL.STACK_COL_GP LIKE SUBSTR(:V_STACK_COL_GP,1,2)||'%'
			   AND SL.STACK_LAYER_STAT = 'C'
			 ORDER BY MIN_CHARGE_LOT_NO
			        , SL.STACK_COL_GP
			        , SL.STACK_BED_GP
			        , SL.STACK_LAYER_GP DESC
			 */
			JDTORecordSet jsWbMtl = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getMvStkWrkBookMtl", logId, methodNm, "재료번호로 조회");

			int rowCnt = jsWbMtl.size();

			if (rowCnt <= 0) {
				throw new Exception("이적 재료 정보가 없습니다.");
			}
			
			jrParam.setField("YD_SCH_CD"           , ydSchCd              ); //야드스케쥴코드
			jrParam.setField("YD_AIM_BAY_GP"       , ydAimBayGp           ); //야드목표동구분
			jrParam.setField("YD_TO_LOC_GUIDE"     , ydToLocGuide         ); //야드To위치Guide
			jrParam.setField("YD_WRK_PLAN_TCAR"    , ydWrkPlanTcar        ); //야드작업계획대차
			jrParam.setField("YD_WRK_PLAN_CRN"     , ydWrkPlanCrn         ); //야드작업계획크레인
			jrParam.setField("CHARGE_LOT_NO_DIV_YN", sCHARGE_LOT_NO_DIV_YN); //장입순번 분리여부
			jrParam.setField("YD_WRK_PLAN_CRN2"    , ydWrkPlanCrn2        ); //야드작업계획크레인
			
			//작업예약등록
			//jsMsg.addRecord(this.insMvstkWrkBook(jrParam, jsWbMtl));
			EJBConnector ejbConn = new EJBConnector("default", "BSlabJspSeEJB", this);
			ejbConn.trx("insMvstkWrkBook", new Class[] { JDTORecord.class, JDTORecordSet.class }, new Object[] { jrParam, jsWbMtl });
			
			/**********************************************************
			* 4. 대차작업이 있으면 공대차출발지시 처리
			**********************************************************/
			if (!"".equals(ydWrkPlanTcar)) {
				
				//공대차출발지시 처리시 Exception을 발생시키지 않기위해 미리 Check
				String msgTcar = ""; //공대차출발지시 처리 메세지
			 			
				//대차스케쥴정보(공대차출발지시) 조회
				jrParam.setField("YD_EQP_ID", ydWrkPlanTcar);	//야드설비ID(대차)
				
				/*
				SELECT TS.YD_TCAR_SCH_ID
				      ,EQ.WPROG_STAT                AS YD_EQP_STAT
				      ,EQ.WORK_MODE                 AS YD_EQP_WRK_MODE
				      ,NVL(SUBSTR(CURR_STOP_LOC,2,1),WB.YD_BAY_GP) 
				                                    AS YD_CURR_BAY_GP --이동중이면 상차동을 현재동으로
				      ,SUBSTR(EQ.WAIT_STOP_LOC,2,1) AS YD_HOME_BAY_GP
				      ,WB.YD_WBOOK_ID               AS YD_WBOOK_ID_CURR   --현재 대차스케줄 상차작업예약ID
				      ,WB.YD_BAY_GP                 AS YD_BAY_GP_CURR     --현재 대차스케줄 상차동
				      ,WB.YD_AIM_BAY_GP             AS YD_AIM_BAY_GP_CURR --현재 대차스케줄 하차동
				      ,XB.YD_WBOOK_ID               AS YD_WBOOK_ID_NEXT   --다음 상차작업예약ID
				      ,XB.YD_BAY_GP                 AS YD_BAY_GP_NEXT     --다음 상차동
				      ,XB.YD_AIM_BAY_GP             AS YD_AIM_BAY_GP_NEXT --다음 하차동
				      ,(SELECT CASE WHEN COUNT(*) > 0 THEN 'Y' ELSE 'N' END
				          FROM TB_YM_TCARFTMVMTL TM
				         WHERE TM.YD_TCAR_SCH_ID = TS.YD_TCAR_SCH_ID
				           AND TM.DEL_YN = 'N')     AS TC_MTL_YN
				--      ,NVL(EQ.AUTO_TCAR_SCH_YN,'N') AS AUTO_TCAR_SCH_YN   --자동대차스케줄여부
				  FROM TB_YM_EQUIP   EQ
				      ,TB_YM_TCARSCH TS
				      ,TB_YM_WRKBOOK WB
				      ,(SELECT MIN(YD_WBOOK_ID  ) AS YD_WBOOK_ID
				              ,MIN(YD_BAY_GP    ) AS YD_BAY_GP
				              ,MIN(YD_AIM_BAY_GP) AS YD_AIM_BAY_GP
				          FROM (SELECT YD_WBOOK_ID
				                      ,YD_BAY_GP
				                      ,YD_AIM_BAY_GP
				                  FROM TB_YM_WRKBOOK
				                 WHERE YD_WRK_PLAN_TCAR = :V_YD_EQP_ID
				                   AND YD_WBOOK_ID NOT IN
				                      (SELECT NVL(YD_CARLD_WRK_BOOK_ID,YD_CARUD_WRK_BOOK_ID) AS YD_WBOOK_ID
				                         FROM TB_YM_TCARSCH
				                        WHERE DEL_YN = 'N'
				                          AND (YD_CARLD_WRK_BOOK_ID IS NOT NULL	OR YD_CARUD_WRK_BOOK_ID IS NOT NULL))
				--                   AND YD_SCH_CD LIKE '__TC__U%'
				                   AND ((SUBSTR(YD_SCH_CD,1,2) <> (NVL(SUBSTR(YD_TO_LOC_GUIDE,1,2),SUBSTR(YD_SCH_CD,1,2))))
				                         OR 
				                        (YD_SCH_CD LIKE SUBSTR(YD_SCH_CD,1,2)|| 'TC__U%')
				                       )
				                   AND DEL_YN = 'N'
				                 ORDER BY YD_SCH_PRIOR, YD_WBOOK_ID)
				         WHERE ROWNUM = 1) XB
				 WHERE EQ.EQUIP_GP             = TS.YD_EQP_ID(+)
				   AND 'N'                     = TS.DEL_YN(+)
				   AND TS.YD_CARLD_WRK_BOOK_ID = WB.YD_WBOOK_ID(+)
				   AND 'N'                     = WB.DEL_YN(+)
				   AND EQ.EQUIP_GP             = :V_YD_EQP_ID
				 */
				JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarSchLevWoSlab", logId, methodNm, "공대차출발지시 조회");
				
				if (jsChk != null && jsChk.size() > 0) {
					JDTORecord jrChk = jsChk.getRecord(0);

					String ydTcarSchId   = commUtils.trim(jrChk.getFieldString("YD_TCAR_SCH_ID"  ));
					String ydWbookIdCurr = commUtils.trim(jrChk.getFieldString("YD_WBOOK_ID_CURR"));

					if ("B".equals(commUtils.trim(jrChk.getFieldString("YD_EQP_STAT")))) {
						msgTcar = "고장";
					} else if (!"1".equals(commUtils.trim(jrChk.getFieldString("YD_EQP_WRK_MODE")))) {
						msgTcar = "Off-Line";
					} else if ("Y".equals(commUtils.trim(jrChk.getFieldString("TC_MTL_YN")))) {
						msgTcar = "대차스케줄[" + ydTcarSchId + "] 이송재료 존재";
					} else if (!"".equals(ydWbookIdCurr)) {
						msgTcar = "대차스케줄[" + ydTcarSchId + "] 상차작업예약[" + ydWbookIdCurr + "] 존재";
					}
				} else {
					msgTcar = "정보 없음";
			    }
				
				//공대차출발지시 처리
				if ("".equals(msgTcar)) {
					jrParam.setField("YD_EQP_ID", ydWrkPlanTcar); //야드설비ID(대차)
					jrParam.setField("YD_BAY_GP", ydBayGp      ); //야드동구분(상차동)
					jrParam.setField("OPR_YN"   , "Y"          ); //화면에서 작업예약 생성
					
					jrRtn = ymComm.trtTcarSchLevWo_Slab(jrParam);
				} else {
					commUtils.printLog(logId, "대차[" + ydWrkPlanTcar + "] 공대차출발지시 불가 : " + msgTcar, "SL");
				}
			}

			/**********************************************************
			* 4. 동내이적(대차작업이 없음)작업 크레인별 첫번째 스케줄 전송
			**********************************************************/
			// 202171030 현업요청 스케줄 기동 금지
			//jrRtn = commUtils.addSndData(jrRtn, this.setCrnSchMsg(jsMsg, logId, methodNm)); 
			
			commUtils.printLog(logId, methodNm, "S-");
			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}			
}	
	
