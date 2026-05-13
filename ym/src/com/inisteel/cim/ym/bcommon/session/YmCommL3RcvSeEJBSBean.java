/**
 * @(#)YmCommL3RcvSeEJBSBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2017/02/02
 *
 * @description      YM야드공통 L3 수신 처리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2017/02/02   정종균      조병기      최초 등록
 * 
 */
package com.inisteel.cim.ym.bcommon.session;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.ym.bcommon.util.YmCommUtils;
import com.inisteel.cim.ym.bcommon.util.YmConstant;
import com.inisteel.cim.ym.bcommon.dao.YmCommDAO;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ym.common.YmCommonUtil;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

/**
 *      [A] 클래스명 : Ym야드공통 L3수신 처리
 *
 * @ejb.bean name="YmCommL3RcvSeEJB" jndi-name="YmCommL3RcvSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300" 
 * @ejb.transaction type="Required"
*/

public class YmCommL3RcvSeEJBSBean extends BaseSessionBean {
	
	private static final long serialVersionUID = 1L;
	private YmCommUtils commUtils = new YmCommUtils();
	private YmCommDAO commDao = new YmCommDAO();
	private YmComm comm = new YmComm();

	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}	
	
/* 내수 I/F 수신 */	
	/**	
	 * [A] 오퍼레이션명 :  내부 크레인 작업지시 요구 (rcvYMYMJ001)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord rcvYMYMJ001(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인 작업지시 요구[YmCommL3RcvSeEJB.rcvYMYMJ001] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId + "크레인 작업지시 요구 수신 ", rcvMsg);

//			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			String sWPROG_STAT = "";
			//크레인설비ID
	    	String szEqpId 	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"));
	    	
	    	if (!szEqpId.equals("")) {
	    		
				String szJMS_TC_CD = "";
				String szEjb = "";
				
				JDTORecord jrParam = JDTORecordFactory.getInstance().create();
				/*
				SELECT *
				  FROM TB_YM_EQUIP    
				 WHERE EQUIP_GP = :V_EQUIP_GP
				   AND DEL_YN   = 'N'
				 */
				jrParam.setField("EQUIP_GP" , szEqpId);
				JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYmEqp");
				JDTORecord jrEqpInfo = null;
				
				if (rsResult.size() > 0) {
					rsResult.first();
					jrEqpInfo = rsResult.getRecord();
					
					sWPROG_STAT = commUtils.trim(jrEqpInfo.getFieldString("WPROG_STAT"));          // 설비 상태
				}
				commUtils.printLog(logId, methodNm + "sWPROG_STAT:"+ sWPROG_STAT, "S+");
				
				if ("W".equals(sWPROG_STAT)) {
				
			    	JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
			    	recInTemp.setResultCode(logId);	//Log ID
			    	recInTemp.setResultMsg(methodNm);	//Log Method Name	
			    	
			    	
			    	if (szEqpId.startsWith("2")) { 				//SLAB
			    		szJMS_TC_CD = "A8YML007";
			    		szEjb = "BSlabL2RcvSeEJB";
			    	} else if (szEqpId.startsWith("3") ) {	    //COIL		
			    		szJMS_TC_CD = "A7YML007";
			    		szEjb = "BCoilL2RcvSeEJB";
			    	}
			    	
			    	if (!szJMS_TC_CD.equals("")) {
				    	recInTemp.setField("JMS_TC_CD"       , szJMS_TC_CD) ;	//크레인작업지시요구
				    	recInTemp.setField("YD_EQP_ID"       , commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"         )));	//야드설비ID
				    	recInTemp.setField("YD_WRK_PROG_STAT", commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT"  )));	//야드작업진행상태(권상작업지시)
				    	recInTemp.setField("YD_SCH_CD"       , commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       	)));	//야드스케쥴코드
				    	recInTemp.setField("YD_CRN_SCH_ID"   , commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"    	)));	//야드크레인스케쥴ID
				    	
			    		EJBConnector ejbConn = new EJBConnector("default", szEjb, this);
			    		jrRtn = (JDTORecord)ejbConn.trx("rcv"+szJMS_TC_CD, new Class[] { JDTORecord.class }, new Object[] { recInTemp });
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
	 * [A] 오퍼레이션명 :  크레인스케줄 중복생성 체크(rcvYMYMJ100)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord rcvYMYMJ100(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인스케줄 중복생성 체크[YmCommL3RcvSeEJB.rcvYMYMJ100] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId + "크레인스케줄 중복생성 체크", rcvMsg);

			String sYD_EQP_ID   = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"  ));
			String sYD_SCH_CD   = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"  ));
			String sYD_WBOOK_ID = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"));
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			jrParam.setField("MODIFIER"   , modifier    ); //수정자 셋팅
			jrParam.setField("YD_EQP_ID"  , sYD_EQP_ID  );
			jrParam.setField("YD_SCH_CD"  , sYD_SCH_CD  );
			jrParam.setField("YD_WBOOK_ID", sYD_WBOOK_ID);
			
			/*
			SELECT YD_CRN_SCH_ID 
			  FROM (
			        SELECT MAX(CM.YD_CRN_SCH_ID) AS YD_CRN_SCH_ID
			             , COUNT(*) AS STOCK_CNT
			          FROM TB_YM_CRNSCH    CS
			             , TB_YM_CRNWRKMTL CM
			         WHERE CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
			           AND CS.DEL_YN = 'N'
			           AND CM.DEL_YN = 'N'
			           AND CS.YD_WBOOK_ID = :V_YD_WBOOK_ID
			         GROUP BY CM.STOCK_ID, CS.YD_SCH_CD
			       ) 
			 WHERE STOCK_CNT = 2 
			 */
			JDTORecordSet jsRst = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getDupCrnSch", logId, methodNm, "중복스케줄 조회");
			
			for (int ii = 0; ii < jsRst.size(); ++ii) {
				jrParam.setField("YD_CRN_SCH_ID", jsRst.getRecord(ii).getFieldString("YD_CRN_SCH_ID"));
				
				/*
				UPDATE TB_YM_CRNWRKMTL
				   SET MODIFIER = :V_MODIFIER
				     , MOD_DDTT = SYSDATE
				     , DEL_YN   = 'Y'
				 WHERE DEL_YN   = 'N'
				   AND YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				 */
				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updCrnWrkMgtSCCrnMtlUnitMtl", logId, methodNm, "중복스케줄 삭제MTL)");
				
				/*
				UPDATE TB_YM_CRNSCH
				   SET MODIFIER = :V_MODIFIER
				      ,MOD_DDTT = SYSDATE
				      ,DEL_YN   = 'Y'
				 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				 */
				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updCrnWrkMgtSCCrnSchUnitMtl", logId, methodNm, "중복스케줄 삭제HEAD");
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
	 * [A] 오퍼레이션명 : 차량입동지시 요구 (rcvYMYMJ801)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYMYMJ801(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "내부차량입동지시 요구[YmCommL3RcvSeEJB.rcvYMYMJ801] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = null;
		JDTORecord sndRecord = null;

		try {
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId + "내부차량입동지시 요구 수신 ", rcvMsg);

//			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			
    		EJBConnector ejbConn = new EJBConnector("default", "YmCommCarMvSeEJB", this);
    		jrRtn = (JDTORecord)ejbConn.trx("rcvYMYMJ662", new Class[] { JDTORecord.class }, new Object[] { rcvMsg });
    		
    		sndRecord = commUtils.addSndData(sndRecord,jrRtn);	
			
    		commUtils.printParam(logId + "내부차량입동지시 요구 수신 ", sndRecord);
			commUtils.printLog(logId, methodNm, "S-");
			return sndRecord;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	
	
	
	
	/**	
	 * [A] 오퍼레이션명 : 저장이동조건 수신(POYMJ007)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPOYMJ007(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "저장이동조건 수신[YmCommL3RcvSeEJB.rcvPOYMJ007] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			
			String sYD_GP    = commUtils.trim(rcvMsg.getFieldString("yardID"));
			String sSTOCK_ID = commUtils.trim(rcvMsg.getFieldString("stockid"));


			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(sYD_GP)) {
				throw new Exception("야드 구분이 없습니다..");
			}
			if ("".equals(sSTOCK_ID)) {
				throw new Exception("저장품이 없습니다..");
			}

			/////////////////////////////////////////////////////////////////////////////////////
			//박판열연 신규모듈 적용여부 체크
            if("0".equals(sYD_GP) || "1".equals(sYD_GP))
            {
                JDTORecord jrResult =  commDao.getYfNewModuleEffYn();
                String sASLAB_EFF_YN = commUtils.nvl(jrResult.getFieldString("ASLAB_EFF_YN"),"N");
                String sACOIL_EFF_YN = commUtils.nvl(jrResult.getFieldString("ACOIL_EFF_YN"),"N");

                String szMsg = "YmCommDAO.getYfNewModuleEffYn()---[[[ A열연SLAB야드신규적용:" + sASLAB_EFF_YN + " ,A열연COIL야드신규적용:" + sACOIL_EFF_YN + " ]]]---";
                commUtils.printLog(logId, methodNm + szMsg , "SL");

                if( "0".equals(sYD_GP) && "Y".equals(sASLAB_EFF_YN) )
                {
                    EJBConnector ejbCon = new EJBConnector("default", "YfRcvFaEJB", this);
                    ejbCon.trx("rcvInterface", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });

                    return jrRtn;
                }
                else if( "1".equals(sYD_GP) && "Y".equals(sACOIL_EFF_YN) )
                {
                    EJBConnector ejbCon = new EJBConnector("default", "YfRcvFaEJB", this);
                    ejbCon.trx("rcvInterface", new Class[]{ JDTORecord.class }, new Object[]{ rcvMsg });

                    return jrRtn;
                }
            }
			/////////////////////////////////////////////////////////////////////////////////////
			
			/**********************************************************
			* 2. 수신 실적 처리 - 저장조건을 (정정작업지시대기/압연지시대기) 로 변경
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setField("STOCK_ID", sSTOCK_ID);
			jrParam.setField("MODIFIER", modifier);
			jrParam.setField("TC_CD"   , YmConstant.POYMJ007);
			
			
			String sSTOCK_MOVE_TERM = "";

			if (sYD_GP.equals(YmCommonConst.YD_GP_2)) {                        // 1열연 Slab
				
				JDTORecord jrRtnProg = comm.getSlabCurrProgCd(jrParam);
				sSTOCK_MOVE_TERM = jrRtnProg.getFieldString("STOCK_MOVE_TERM");
				
			} else if (sYD_GP.equals(YmCommonConst.YD_GP_3)) {                        // 1열연 Coil 정정작업지시대기
				
				JDTORecord jrRtnProg = comm.getCoilCurrProgCd(jrParam);
				sSTOCK_MOVE_TERM = jrRtnProg.getFieldString("STOCK_MOVE_TERM");
			}
			
			
    		/*
			UPDATE TB_YM_STOCK
			   SET STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM
			     , MODIFIER   = :V_MODIFIER
			     , MOD_DDTT   = SYSDATE           
			 WHERE STOCK_ID = :V_STOCK_ID
    		 */
    		jrParam.setField("STOCK_ID"       , sSTOCK_ID);
    		jrParam.setField("STOCK_MOVE_TERM", sSTOCK_MOVE_TERM);
    		commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateStockTransInfo");
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 제품운송상차지시(DMYDR060)  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord rcvDMYDR060(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "제품운송상차지시 수신[YmCommL3RcvSeEJB.rcvDMYDR060] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
	    JDTORecord jrParam = JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord 
		JDTORecordSet rsResult 	= null;
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
		
			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅
			
			String szCMBN_CARLD_YN	 = commUtils.trim(rcvMsg.getFieldString("CMBN_CARLD_YN")); //조합상차유무
			String szTRANS_ORD_DT	 = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT")); //운송지시일자
			String szTRANS_ORD_SEQNO = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO")); //운송지시순번
			String szCAR_NO 		 = commUtils.trim(rcvMsg.getFieldString("CAR_NO")); //차량번호
			String szCARD_NO 		 = commUtils.trim(rcvMsg.getFieldString("CARD_NO")); //카드번호
			String szLOT_NO 		 = commUtils.trim(rcvMsg.getFieldString("LOT_NO")); //LOT번호
			String szCAR_KIND 		 = commUtils.trim(rcvMsg.getFieldString("CAR_KIND")); //차량종류
			
			int szYD_EQP_WRK_SH		 = Integer.parseInt(commUtils.nvl(rcvMsg.getFieldString("YD_EQP_WRK_SH"),"0")); //야드설비작업매수
			
			String szYD_GP			 = "";
			String szSTL_NO;
			String szGDS_CARLD_LOC;
			String szSTOCK_MOVE_TERM;
			
			String szYD_AIM_RT_GP;
			
			YdStockDao ydStockDao = new YdStockDao();
			
			String[] rVal = new String[1];
			
			/////////////////////////////////////////////////////////////////////////////////////
			//박판열연 신규모듈 적용여부 체크
			JDTORecord jrResult =  commDao.getYfNewModuleEffYn();
			String sACOIL_EFF_YN = commUtils.nvl(jrResult.getFieldString("ACOIL_EFF_YN"),"N");

			String szMsg = "YmCommDAO.getYfNewModuleEffYn()---[[[ A열연COIL야드신규적용:" + sACOIL_EFF_YN + " ]]]---";
			commUtils.printLog(logId, methodNm + szMsg , "SL");
			/////////////////////////////////////////////////////////////////////////////////////
			
			//수신된 전문의 STL_NO의 수 만큼 Loop
			for(int ii = 1 ; ii<=szYD_EQP_WRK_SH; ii++) 
			{
				szYD_GP 		= commUtils.trim(rcvMsg.getFieldString("YD_GP"+ii)); //야드구분
				szSTL_NO 		= commUtils.trim(rcvMsg.getFieldString("STL_NO"+ii)); //재료번호
				szGDS_CARLD_LOC = commUtils.trim(rcvMsg.getFieldString("GDS_CARLD_LOC"+ii)); //상차위치
				
				if ("S".equals(szCMBN_CARLD_YN)&&"TR".equals(szCAR_KIND)&&"".equals(szGDS_CARLD_LOC)) 
				{
					szGDS_CARLD_LOC = "0" + ii;
				}
				
				if ("".equals(szSTL_NO)) {
					break;
				}
				
				if ("0".equals(szYD_GP)||"1".equals(szYD_GP)||"2".equals(szYD_GP)||"3".equals(szYD_GP))
				{
					//0:A열연 SLAB야드,1:A열연 COIL야드,2:B열연 SLAB야드,3:B열연 COIL야드
					if("1".equals(szYD_GP) && "Y".equals(sACOIL_EFF_YN))
					{
						//1:A열연 COIL야드

						//======================================================
						// 저장품 이동 조건(STOCK_MOVE_TERM) 생성
						//======================================================
						jrParam.setField("TC_CD",	msgId);	//TC_CODE
						jrParam.setField("STL_NO",	szSTL_NO);	//저장품 ID
						szSTOCK_MOVE_TERM = commUtils.trim(comm.getCoilCurrProgCd(jrParam).getFieldString("STOCK_MOVE_TERM"));

						//======================================================
						// TB_YF_STOCK 수정
						//======================================================
						jrParam.setField("STOCK_MOVE_TERM",		szSTOCK_MOVE_TERM);
						jrParam.setField("YD_RULE_PL_RS_GP",	szCMBN_CARLD_YN);
						jrParam.setField("TRANS_ORD_DATE",		szTRANS_ORD_DT);
						jrParam.setField("TRANS_ORD_SEQNO",		szTRANS_ORD_SEQNO);
						jrParam.setField("YD_CAR_UPP_LOC_CD",	szGDS_CARLD_LOC);
						jrParam.setField("CAR_NO",				szCAR_NO);
						jrParam.setField("CAR_CARD_NO",			szCARD_NO);
						jrParam.setField("STL_NO",				szSTL_NO);

						commDao.update(jrParam, "com.inisteel.cim.yf.acommon.dao.YfCommDAO.updYfStock", logId, methodNm, "TB_YF_STOCK 수정");
					}
					else
					{
						//저장품 이동 조건 
						jrParam.setField("TC_CD"	, msgId);  //TC_CODE
						jrParam.setField("STOCK_ID" , szSTL_NO); //저장품 ID
						szSTOCK_MOVE_TERM = commUtils.trim(comm.getCoilCurrProgCd(jrParam).getFieldString("STOCK_MOVE_TERM"));
						
						//TB_YM_STOCK 수정
						jrParam.setField("STOCK_MOVE_TERM"	, szSTOCK_MOVE_TERM);
						jrParam.setField("YD_RULE_PL_RS_GP"	, szCMBN_CARLD_YN);
						jrParam.setField("TRANS_ORD_DATE2"	, szTRANS_ORD_DT);
						jrParam.setField("TRANS_ORD_SEQNO2"	, szTRANS_ORD_SEQNO);
						jrParam.setField("SHEAR_SUPPLY_SEQ"	, szGDS_CARLD_LOC);
						jrParam.setField("CAR_NO2"			, szCAR_NO);
						jrParam.setField("CAR_CARD_NO"		, szCARD_NO);
						jrParam.setField("SHEAR_SUPPLY_GP"	, szCAR_KIND);
						jrParam.setField("STOCK_ID"			, szSTL_NO);
						/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYmStock 
						UPDATE USRYMA.TB_YM_STOCK
						   SET MODIFIER         = :V_MODIFIER
						     , MOD_DDTT         = SYSDATE
						     , STOCK_MOVE_TERM  = NVL(:V_STOCK_MOVE_TERM , STOCK_MOVE_TERM)   --저장품이동조건
						     , YD_RULE_PL_RS_GP = NVL(:V_YD_RULE_PL_RS_GP, YD_RULE_PL_RS_GP)  --조합구분
						     , TRANS_WORD_NO    = :V_TRANS_ORD_DATE2 || :V_TRANS_ORD_SEQNO2
						     , TRANS_ORD_DATE2  = NVL(:V_TRANS_ORD_DATE2 , TRANS_ORD_DATE2)   --운송지시
						     , TRANS_ORD_SEQNO2 = NVL(:V_TRANS_ORD_SEQNO2, TRANS_ORD_SEQNO2)  --운송지시행번
						     , SHEAR_SUPPLY_SEQ = NVL(:V_SHEAR_SUPPLY_SEQ, SHEAR_SUPPLY_SEQ)  --차상위치
						     , CAR_NO2          = NVL(:V_CAR_NO2         , CAR_NO2)           --차량번호 
						     , CAR_CARD_NO      = NVL(:V_CAR_CARD_NO     , CAR_CARD_NO)       --카드번호
						     , SHEAR_SUPPLY_GP  = NVL(:V_SHEAR_SUPPLY_GP , SHEAR_SUPPLY_GP)   --차량종류
						     , WBOOK_ID         = NVL(:V_WBOOK_ID        , WBOOK_ID)          --작업예약 사용안함
						     , CR_FRTOMOVE_GP   = NVL(:V_CR_FRTOMOVE_GP  , CR_FRTOMOVE_GP)    -- 냉연이송구분       
						 WHERE STOCK_ID = :V_STOCK_ID */  
						commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYmStock", logId, methodNm, "TB_YM_STOCK 수정");
					}
				}
				else
				{
					//일관제철 야드 - RtModRegSeEJB.procCoilGdsTrnOrdNEW 와 동일함
					
					//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
					jrParam.setField("TC_CODE", msgId);  //TC_CODE
					jrParam.setField("STL_NO" , szSTL_NO); //저장품 ID
					rVal= YdCommonUtils.getYdAimRtGp("C",jrParam );	
					
					szYD_AIM_RT_GP	= rVal[0]; //야드목표행선
					
					//TB_YD_STOCK 수정
					jrParam.setField("STL_NO"			, szSTL_NO);
					jrParam.setField("YD_CAR_UPP_LOC_CD", szGDS_CARLD_LOC);
					jrParam.setField("TRANS_ORD_DATE"	, szTRANS_ORD_DT);
					jrParam.setField("TRANS_ORD_SEQNO"	, szTRANS_ORD_SEQNO);
					jrParam.setField("YD_AIM_RT_GP"		, szYD_AIM_RT_GP);
					jrParam.setField("CAR_NO"			, szCAR_NO);
					jrParam.setField("CARD_NO"			, szCARD_NO);
					jrParam.setField("YD_RULE_PL_RS_GP"	, szCMBN_CARLD_YN);
					jrParam.setField("CAR_LOTID"		, szLOT_NO);
					jrParam.setField("YD_STK_BED_NO"	, szCAR_KIND);
					/*com.inisteel.cim.ym.dao.ydstockdao.updYdStock
					UPDATE USRYDA.TB_YD_STOCK
					SET YD_AIM_RT_GP     =NVL(:V_YD_AIM_RT_GP,YD_AIM_RT_GP)
					  , YD_RULE_PL_RS_GP =:V_YD_RULE_PL_RS_GP
					  , TRANS_ORD_DATE   =:V_TRANS_ORD_DATE
					  , TRANS_ORD_SEQNO  =:V_TRANS_ORD_SEQNO
					  , YD_CAR_UPP_LOC_CD=:V_YD_CAR_UPP_LOC_CD
					  , MODIFIER         =NVL(:V_MODIFIER,MODIFIER)
					  , MOD_DDTT         =SYSDATE
					  , CAR_NO           =:V_CAR_NO
					  , CARD_NO          =:V_CARD_NO
					  , CAR_LOTID        =:V_CAR_LOTID
					  , CAR_LOTID_REG_DDTT =NVL(CAR_LOTID_REG_DDTT,(CASE WHEN :V_CAR_LOTID<>'' THEN SYSDATE ELSE NULL END))
					  , YD_STK_BED_NO    =NVL( :V_YD_STK_BED_NO,'TR')  
					  , CR_FRTOMOVE_GP= NULL
					WHERE STL_NO    =:V_STL_NO */
					ydStockDao.updYdStock(jrParam);
					
					//현재 com.inisteel.cim.ym.dao.ydstockdao.updYdStock 의 파라메터 지정이 안되 어 있어서
					//ydStockDao.updYdStock 메소드를 사용한 것임. 
					//jspeed에서 파라메터 지정 후 아래 와 같이 사용해도 됨!
					//commDao.update(jrParam, "com.inisteel.cim.ym.dao.ydstockdao.updYdStock, logId, methodNm, "TB_YD_STOCK 수정");
				}
				
			} // end of for loop
			
			//야드핸들링정보 송신////////////////////////////////////////////////////////////////////////////////
			//코일야드 인 경우 우선 적용
			if ("1".equals(szYD_GP)||"3".equals(szYD_GP)||"H".equals(szYD_GP)||"J".equals(szYD_GP) )
			{ 
				//1:A열연 COIL야드,3:B열연 COIL야드,H:C열연 COIL소재야드,J:C열연 COIL제품야드
				
				//출하제품핸들링횟수 구하기
				jrParam.setField("TRANS_ORD_DATE"	, szTRANS_ORD_DT);
				jrParam.setField("TRANS_ORD_SEQNO"	, szTRANS_ORD_SEQNO);
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getHandlingCnt 
				--//출하제품핸들링횟수
				WITH TEMP_TABLE AS (
				    SELECT STOCK_ID         AS STL_NO
				         , TRANS_ORD_SEQNO2 AS TRANS_ORD_SEQNO
				      FROM USRYMA.TB_YM_STOCK A
				     WHERE A.TRANS_ORD_DATE2  = :V_TRANS_ORD_DATE 
				       AND A.TRANS_ORD_SEQNO2 = :V_TRANS_ORD_SEQNO
				)
				, TEMP_TABLE2 AS (
				    SELECT SUBSTR(YD_CARPNT_CD,1,1) AS YD_GP 
				         , YD_CARPNT_CD  AS CARLD_PNT_CD
				         , COUNT(STL_NO) AS HANDLING_CNT  
				      FROM ( 
				             SELECT SUBSTR(C.YD_CARPNT_CD,1,3) AS YD_CARPNT_CD
				                  , A.STL_NO 
				                  , TRANS_ORD_SEQNO
				               FROM (
				                      SELECT 'A' AS CHK 
				                           , STL_NO 
				                           , TRANS_ORD_SEQNO
				                        FROM TEMP_TABLE
				                       UNION 
				                      SELECT 'B' 
				                           , (SELECT STL_NO_LEFT FROM VW_YM_STKLYERSEARCH B WHERE A.STL_NO=B.STL_NO) AS STL_NO_LEFT
				                           , TRANS_ORD_SEQNO
				                        FROM TEMP_TABLE A
				                       UNION 
				                      SELECT 'B'
				                           , (SELECT STL_NO_RIGHT FROM VW_YM_STKLYERSEARCH B WHERE A.STL_NO=B.STL_NO) AS STL_NO_RIGHT
				                           , TRANS_ORD_SEQNO
				                        FROM TEMP_TABLE A 
				                     ) A
				                   , TB_PT_COILCOMM B
				                   , USRYDA.TB_YD_CARPOINT C
				               WHERE A.STL_NO    = B.COIL_NO
				                 AND B.YD_GP     = C.YD_GP
				                 AND B.YD_BAY_GP = C.YD_BAY_GP
				                 AND B.YD_EQP_GP BETWEEN C.YD_SPAN_FROM AND C.YD_SPAN_TO
				               GROUP BY SUBSTR(C.YD_CARPNT_CD,1,3) , A.STL_NO ,TRANS_ORD_SEQNO
				            )
				            GROUP BY YD_CARPNT_CD
				)
				SELECT * 
				  FROM TEMP_TABLE2
				 WHERE EXISTS(SELECT 1 FROM TEMP_TABLE2)  
				UNION ALL
				SELECT B.YD_GP , '' ,  COUNT(A.STL_NO) AS HANDLING_CNT 
				  FROM TEMP_TABLE A
				     , TB_PT_COILCOMM B
				 WHERE A.STL_NO = B.COIL_NO
				   AND NOT EXISTS (SELECT 1 FROM TEMP_TABLE2)
				 GROUP BY B.YD_GP */
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getHandlingCnt", logId, methodNm, "출하Handling 갯수 구하기");
				
				for(int ii = 0; ii < rsResult.size() ; ii++) {	
					jrParam.setField("YD_GP"				, commUtils.trim(rsResult.getRecord(ii).getFieldString("YD_GP")) );
					jrParam.setField("TRANS_ORD_DT"			, szTRANS_ORD_DT);
					jrParam.setField("TRANS_ORD_SEQNO"		, szTRANS_ORD_SEQNO);
					jrParam.setField("CMBN_CARLD_YN"		, szCMBN_CARLD_YN );
					jrParam.setField("CARLD_PNT_CD"			, commUtils.trim(rsResult.getRecord(ii).getFieldString("CARLD_PNT_CD")) );
					jrParam.setField("CAR_NO"				, szCAR_NO );
					jrParam.setField("HANDLING_CNT"			, commUtils.trim(rsResult.getRecord(ii).getFieldString("HANDLING_CNT")) ); 
					jrParam.setField("YD_STK_BED_WHIO_STAT"	, "" );
					
					// PIDEV
//					String sApplyYnPI = commDao.ApplyYnPI("", "YmCommL3RcvSeEJBSBean => 핸들링횟수정보(열연코일)", "APPPI0", "3", "*");
					
//					if("Y".equals(sApplyYnPI)) {
						//전송 Data 생성
						jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL3("M10YDLMJ1051", jrParam));					
//					} else {
//						//전송 Data 생성
//						jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL3("YDDMR050", jrParam));
//					}
				}
			}
			
			//-------------------------------------------------------------------------------------------------------------
			// A동 2통로 출하대상 자동이적 YMYMJ312
			String sAPPLY060 = comm.BCoilApplyYn("APP060","3","A3TOA4_DM");
			commUtils.printLog(logId,  ">>> A동 2통로 출하작업 고도화 적용 (Y:적용, N:비적용) :" + sAPPLY060, "SL");
			if (sAPPLY060.equals("Y")) {
				
				jrParam.setField("TRANS_ORD_DATE" 	, szTRANS_ORD_DT);
				jrParam.setField("TRANS_ORD_SEQNO"	, szTRANS_ORD_SEQNO);
				
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getListA3toA4Dm", logId, methodNm, "A동 2통로 출하 자동이적 대상 리스트 조회");
				
				if (rsResult.size() > 0) {
					//A4 크레인이 갈수 없는 영역에 출하 대상이 존재하면 자동이적  YMYMJ312 호출 
				
					JDTORecord jrYMYMJ312 = JDTORecordFactory.getInstance().create();
					jrYMYMJ312.setResultCode(logId);	//Log ID
					jrYMYMJ312.setResultMsg(methodNm);	//Log Method Name
					jrYMYMJ312.setField("JMS_TC_CD" 		, "YMYMJ312");
					jrYMYMJ312.setField("TRANS_ORD_DATE" 	, szTRANS_ORD_DT);
					jrYMYMJ312.setField("TRANS_ORD_SEQNO"	, szTRANS_ORD_SEQNO);
					jrYMYMJ312.setField("MODIFIER"	        , modifier);
					
					jrRtn = commUtils.addSndData(jrRtn, jrYMYMJ312);
				
				}
				
			}
			//-------------------------------------------------------------------------------------------------------------
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;	
		}
		catch (DAOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 대기장도착실적(DMYDR061)  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord rcvDMYDR061(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "대기장도착실적 수신[YmCommL3RcvSeEJB.rcvDMYDR061] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			jrRtn = commUtils.addSndData(jrRtn, this.procDMYDR061(rcvMsg)); 
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	/**
	 * 대기장도착실적(DMYDR061)  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord procDMYDR061(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "대기장도착실적 수신[YmCommL3RcvSeEJB.procDMYDR061] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
	    JDTORecord jrParam = JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord 
		JDTORecordSet rsResult 	= null;
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			jrParam.setField("MODIFIER", modifier); //수정자 셋팅
			
			String szYD_GP 				= commUtils.trim(rcvMsg.getFieldString("YD_GP")); //야드구분	
			String szCMBN_CARLD_YN 		= commUtils.nvl(rcvMsg.getFieldString("CMBN_CARLD_YN"),"N"); //조합상차유무(시작:S , 종료: E ,  단일상차: N )
			String szWORK_GP 			= commUtils.trim(rcvMsg.getFieldString("WORK_GP")); //작업구분
			String szTEL_NO 			= commUtils.trim(rcvMsg.getFieldString("TEL_NO")); //전화번호
			String szTRANS_ORD_DT  		= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT")); //운송지시일자
			String szTRANS_ORD_SEQNO	= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO")); //운송지시순번
			String szCAR_NO 			= commUtils.trim(rcvMsg.getFieldString("CAR_NO")); //차량번호
			String szCARD_NO 			= commUtils.trim(rcvMsg.getFieldString("CARD_NO")); //카드번호
			String szCAR_KIND 			= commUtils.nvl(rcvMsg.getFieldString("CAR_KIND"),"TR"); //차량종류
			String szWAIT_ARR_DDTT		= commUtils.trim(rcvMsg.getFieldString("WAIT_ARR_DDTT")); //대기장도착시간
			String szWAIT_ARR_GP		= commUtils.trim(rcvMsg.getFieldString("WAIT_ARR_GP")); //대기장도착구분
			String szTRANS_FRTOMOVE_GP	= commUtils.trim(rcvMsg.getFieldString("TRANS_FRTOMOVE_GP")); //1 운송 2 이송
			String szDRIVER_NAME		= commUtils.trim(rcvMsg.getFieldString("DRIVER_NAME")); //운전기사명
			
	    	// PIDEV
//	    	String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "3", "*");			
			
			//차량정보 존재여부 체크 //////////////////////////////////////////////////////////////////////////////
			jrParam.setField("TRANS_ORD_DT"		, szTRANS_ORD_DT);
			jrParam.setField("TRANS_ORD_SEQNO"	, szTRANS_ORD_SEQNO);
			
	    	// PIDEV				
//			if("Y".equals(sApplyYnPI)) {
				jrParam.setField("CAR_NO"			, szCAR_NO );
//			} else {
//				jrParam.setField("CARD_NO"			, szCARD_NO );
//			}
			
			jrParam.setField("CMBN_CARLD_YN"	, szCMBN_CARLD_YN );
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarYdCmbnCarldYn_PIDEV 
			SELECT *
			  FROM TB_YD_CARSCH
			 WHERE TRANS_ORD_DATE  = :V_TRANS_ORD_DT
			   AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
			   AND CARD_NO         = :V_CARD_NO
			   AND CMBN_CARLD_YN   = :V_CMBN_CARLD_YN
			   AND DEL_YN   = 'N'	
			*/	   
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarYdCmbnCarldYn_PIDEV", logId, methodNm, "차량정보 존재여부 체크");
			
			if (rsResult.size() > 0) {
				
				commUtils.printLog(logId, methodNm + " TB_YD_CARSCH[차량스케줄이 편성되어 있습니다.:기존지시 삭제 " , "SL");	
				
				//기존 차량정보 삭제처리///////////////////////////////////////////////////////////////////////////
				jrParam.setField("YD_CAR_SCH_ID", commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CAR_SCH_ID")));
				jrParam.setField("DEL_YN"       , "Y");
				/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdCarsch
				UPDATE TB_YD_CARSCH
				   SET MODIFIER         = :V_MODIFIER
				     , MOD_DDTT         = SYSDATE
				     , DEL_YN           = NVL(:V_DEL_YN          , DEL_YN)
				     --하차
				     , YD_CAR_PROG_STAT     = NVL(:V_YD_CAR_PROG_STAT    , YD_CAR_PROG_STAT)
				     , YD_CARUD_WRK_BOOK_ID = NVL(:V_YD_CARUD_WRK_BOOK_ID, YD_CARUD_WRK_BOOK_ID)
				     , YD_CARUD_STOP_LOC    = NVL(:V_YD_CARUD_STOP_LOC   , YD_CARUD_STOP_LOC)
				     , YD_CARUD_ARR_DT      = DECODE(:V_YD_CARUD_ARR_DT,NULL,YD_CARUD_ARR_DT, TO_DATE(:V_YD_CARUD_ARR_DT,'YYYYMMDDHH24MISS')) 
				     , YD_PNT_CD3           = NVL(:V_YD_PNT_CD3          , YD_PNT_CD3)
				     --상차
				     , YD_CARLD_WRK_BOOK_ID = NVL(:V_YD_CARLD_WRK_BOOK_ID, YD_CARLD_WRK_BOOK_ID)
				     , YD_CARLD_STOP_LOC    = NVL(:V_YD_CARLD_STOP_LOC   , YD_CARLD_STOP_LOC)
				     , YD_CARLD_ARR_DT      = DECODE(:V_YD_CARLD_ARR_DT,NULL,YD_CARLD_ARR_DT, TO_DATE(:V_YD_CARLD_ARR_DT,'YYYYMMDDHH24MISS'))
				     , YD_PNT_CD1           = NVL(:V_YD_PNT_CD1          , YD_PNT_CD1)
				 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID  
				 */
				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdCarsch", logId, methodNm, "TB_YD_CARSCH 기존 차량스케줄 정보 삭제처리");
			}
			
			//도착가능 포인트 조회
			jrParam.setField("YD_GP"			, szYD_GP);
			jrParam.setField("TRANS_ORD_DT"		, szTRANS_ORD_DT);
			jrParam.setField("TRANS_ORD_SEQNO"	, szTRANS_ORD_SEQNO);
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarPointSelect 
			--AB열연
			SELECT *
			  FROM (
			         SELECT CC.YD_STK_COL_GP,CC.YD_CARPNT_CD,CC.YD_PNT_CD
			              , DECODE(CC.YD_STK_COL_ACT_STAT,'C',1,2) AS STAT_RANK
			              , (SELECT COUNT(*)
			                   FROM USRYDA.TB_YD_CARSCH A
			                 WHERE A.DEL_YN='N'
			                   AND A.YD_CARLD_STOP_LOC=CC.YD_STK_COL_GP) CARLD_RANK
			              , CC.WLOC_CD 
			           FROM ( 
			                SELECT A.STOCK_ID  
			                     , :V_YD_GP   AS YD_GP 
			                  FROM USRYMA.TB_YM_STOCK A
			                 WHERE A.TRANS_ORD_DATE2  = :V_TRANS_ORD_DT
			                   AND A.TRANS_ORD_SEQNO2 = :V_TRANS_ORD_SEQNO
			                ) AA
			              , TB_PT_COILCOMM BB
			              , USRYDA.TB_YD_CARPOINT CC
			          WHERE AA.STOCK_ID = BB.COIL_NO
			            AND CC.YD_CARPNT_CD LIKE AA.YD_GP||'%'
			            AND BB.YD_GP = CC.YD_GP
			            AND BB.YD_BAY_GP = CC.YD_BAY_GP
			            AND BB.YD_EQP_GP BETWEEN CC.YD_SPAN_FROM AND CC.YD_SPAN_TO
			            AND CC.YD_CAR_USETYPE_GP IN('TR','RT','RA','TO') --트레일러
			            AND CC.DEL_YN = 'N'
			            AND CC.YD_STK_COL_ACT_STAT <>'N'
			          ORDER BY CARLD_RANK, SUBSTR(YD_STK_COL_GP,2,1)
			       )
			 WHERE ROWNUM<=1 */
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCarPointSelect", logId, methodNm, "도착가능 차량포인트 조회");
			if (rsResult.size() <= 0 ) {
				//commUtils.printLog(logId, methodNm + " TB_YD_CARSCH[차량도착 가능한 포인트가 없거나 해당 운송상차지시,저장위치가 없습니다. " , "SL");
				//m_ctx.setRollbackOnly();
				throw new Exception("TB_YD_CARSCH[차량도착 가능한 포인트가 없거나 해당 운송상차지시,저장위치가 없습니다....");
			} 
			
			//도착가능 포인트 조회 결과 값
			String szYD_STK_COL_GP 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_COL_GP")); //야드적치열
			String szYD_CARPNT_CD 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CARPNT_CD")); //차량보인트
			String szYD_PNT_CD 		= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_PNT_CD")); //야드포인트코드
			String szSPOS_WLOC_CD	= commUtils.trim(rsResult.getRecord(0).getFieldString("WLOC_CD")); //개소코드
			
			//차량스케줄ID 생성 				
			String szYD_CAR_SCH_ID = commDao.getSeqId(logId, methodNm, "CarSch");
			
			//차량스케줄 등록
			jrParam.setField("YD_CAR_SCH_ID"		, szYD_CAR_SCH_ID);
			jrParam.setField("REGISTER"				, modifier);
			jrParam.setField("YD_EQP_WRK_STAT"		, "U");	//야드설비작업상태
			jrParam.setField("YD_EQP_ID"			, YmConstant.YD_DM_CAR_EQP_ID);	//야드설비ID
			jrParam.setField("YD_CAR_USE_GP"		, YmConstant.YD_CAR_USE_GP_DM);	//차량사용구분 
			jrParam.setField("CAR_NO"				, szCAR_NO); //차량번호
			jrParam.setField("CAR_KIND"				, szCAR_KIND); //차량종류
			jrParam.setField("SPOS_WLOC_CD"			, szSPOS_WLOC_CD); //발지개소코드
			
	    	//PIDEV				
//			if("N".equals(sApplyYnPI)) {	
//				jrParam.setField("CARD_NO"				, szCARD_NO); //카드번호
//			}
			
			jrParam.setField("YD_CARLD_LEV_DT"		, commUtils.getDateTime14());		//상차출발일시
			jrParam.setField("YD_PNT_CD1"			, szYD_PNT_CD);	//야드포인트코드1
			jrParam.setField("YD_CARLD_STOP_LOC"	, szYD_STK_COL_GP);	//야드상차정지위치 
			jrParam.setField("TRANS_ORD_DATE"		, szTRANS_ORD_DT); //운송지시일자
			jrParam.setField("TRANS_ORD_SEQNO"		, szTRANS_ORD_SEQNO); //운송지시순번 
			if ("E".equals(szCMBN_CARLD_YN)) {
				jrParam.setField("YD_BAYIN_WO_SEQ"	, "1"); //입동지시순번 - 복수상차 마지막 1순위	
			} else {
				jrParam.setField("YD_BAYIN_WO_SEQ"	, YmConstant.YD_BAYIN_WO_SEQ_DEFAULT); //입동지시순번 - 기본값으로 설정(9)
			}
			jrParam.setField("YD_CAR_PROG_STAT"		, "1");	//상차출발상태
			jrParam.setField("YD_CAR_WRK_GP"		, szWORK_GP); //야드차량작업구분
			jrParam.setField("TEL_NO"				, szTEL_NO); //기사핸드폰번호
			jrParam.setField("CMBN_CARLD_YN"		, szCMBN_CARLD_YN);	//첫번째 도착창고 : S 두번째 도착창고 : E
			jrParam.setField("WAIT_ARR_DDTT"		, szWAIT_ARR_DDTT);	//대기장도착시간
			jrParam.setField("WAIT_ARR_GP"			, szWAIT_ARR_GP); //대기장도착구분  - B:BACKUP , S:SMARTPHONE
			jrParam.setField("DRIVER_NAME"			, szDRIVER_NAME); //운전기사명
			
			/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.insYdCarsch 
			INSERT INTO USRYDA.TB_YD_CARSCH
			(	   YD_CAR_SCH_ID
			     , REGISTER
			     , REG_DDTT	
			     , DEL_YN
			     , YD_EQP_ID
			     , YD_CAR_USE_GP
			     , CAR_NO
			     , TRN_EQP_CD
			     , CAR_KIND
			     , YD_EQP_WRK_STAT
			     , SPOS_WLOC_CD
			     , YD_CARLD_LEV_LOC
			     , YD_CARLD_LEV_DT
			     , YD_PNT_CD1
			     , YD_CARLD_STOP_LOC
			     , CARD_NO
			     , YD_CAR_PROG_STAT
			     , YD_CAR_WRK_GP
			     , TRANS_ORD_DATE
			     , TRANS_ORD_SEQNO
			     , YD_BAYIN_WO_SEQ
			     , TEL_NO
			     , CMBN_CARLD_YN
			     , WAIT_ARR_DDTT
			     , WAIT_ARR_GP
			       )
			VALUES (
			       :V_YD_CAR_SCH_ID
			     , :V_REGISTER
			     , SYSDATE
			     , 'N'
			     , :V_YD_EQP_ID
			     , :V_YD_CAR_USE_GP
			     , :V_CAR_NO
			     , :V_TRN_EQP_CD
			     , :V_CAR_KIND
			     , :V_YD_EQP_WRK_STAT
			     , :V_SPOS_WLOC_CD
			     , :V_YD_CARLD_LEV_LOC
			     , :V_YD_CARLD_LEV_DT
			     , NVL(:V_YD_PNT_CD1,'0000')
			     , :V_YD_CARLD_STOP_LOC
			     , :V_CARD_NO
			     , :V_YD_CAR_PROG_STAT
			     , :V_YD_CAR_WRK_GP
			     , :V_TRANS_ORD_DATE
			     , :V_TRANS_ORD_SEQNO
			     , :V_YD_BAYIN_WO_SEQ
			     , :V_TEL_NO
			     , :V_CMBN_CARLD_YN
			     , :V_WAIT_ARR_DDTT
			     , :V_WAIT_ARR_GP     
			       )
			 */   
			// PIDEV						
//			if("Y".equals(sApplyYnPI)) {
				commDao.insert(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.insYdCarsch_PIDEV", logId, methodNm, "TB_YD_CARSCH 등록");
//			} else {
//				commDao.insert(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.insYdCarsch", logId, methodNm, "TB_YD_CARSCH 등록");	
//			}
			
			// PIDEV						
//			if ("Y".equals(sApplyYnPI)) {
				if ("E".equals(szCMBN_CARLD_YN)) {
					// 복수동 마지막 도착시 상차된 정보 INSERT 
					//이송작업재료등록
					jrParam.setField("MODIFIER"	        , modifier); 
					/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updCarFtMvMtlCmbnCarldYn_PIDEV
					MERGE INTO TB_YD_CARFTMVMTL TM USING (
					    SELECT CC.COIL_NO          AS STOCK_ID
					         , CC.HCR_GP           AS HCR_GP
					         , CC.RECORD_PROG_STAT AS STL_PROG_CD
					    --         , COIL.YD_STK_BED_NO AS STACK_BED_GP
					         , (SELECT MAX(A.YD_STK_BED_NO)
					              FROM TB_YD_CARFTMVMTL A
					             WHERE A.STL_NO        = CC.COIL_NO
					               AND A.YD_CAR_SCH_ID = (SELECT MAX(YD_CAR_SCH_ID) 
					                                        FROM TB_YD_CARSCH  
					                                       WHERE TRANS_ORD_DATE  = :V_TRANS_ORD_DT
					                                         AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
					                                         AND CARD_NO         = :V_CARD_NO
					                                         AND CMBN_CARLD_YN = 'S'
					                                       )
					            )                   AS STACK_BED_GP   
					         , '01'                 AS STACK_LAYER_GP
					         , :V_YD_CAR_SCH_ID     AS YD_CAR_SCH_ID
					         , :V_MODIFIER          AS MODIFIER
					         , SYSDATE              AS MOD_DDTT
					         , 'N'                  AS DEL_YN
					      FROM USRPTA.TB_PT_COILCOMM  CC
					     WHERE COIL_NO  IN ( 
					        SELECT B.STL_NO
					          FROM TB_YD_CARSCH A 
					             , TB_YD_CARFTMVMTL B
					         WHERE A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
					           AND A.TRANS_ORD_DATE  = :V_TRANS_ORD_DT
					           AND A.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
					           AND A.CARD_NO         = :V_CARD_NO
					           AND A.DEL_YN = 'Y'    
					           AND B.DEL_YN = 'Y' 
					           AND A.CMBN_CARLD_YN = 'S'
					       )    
					) DD ON (TM.YD_CAR_SCH_ID = DD.YD_CAR_SCH_ID AND TM.STL_NO = DD.STOCK_ID )    
					WHEN NOT MATCHED THEN
					INSERT (TM.YD_CAR_SCH_ID, TM.STL_NO, TM.REGISTER, TM.REG_DDTT,
					        TM.MODIFIER, TM.MOD_DDTT, TM.DEL_YN, TM.YD_STK_BED_NO,
					        TM.YD_STK_LYR_NO, TM.HCR_GP, TM.STL_PROG_CD)
					VALUES (DD.YD_CAR_SCH_ID, DD.STOCK_ID, DD.MODIFIER, DD.MOD_DDTT,
					        DD.MODIFIER, DD.MOD_DDTT, DD.DEL_YN, DD.STACK_BED_GP,
					        DD.STACK_LAYER_GP, DD.HCR_GP, DD.STL_PROG_CD)
					*/        
					commDao.insert(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updCarFtMvMtlCmbnCarldYn_PIDEV", logId, methodNm, "복수동이송작업재료등록");			
				}				
//			} else {
//				if ("E".equals(szCMBN_CARLD_YN)) {
//					// 복수동 마지막 도착시 상차된 정보 INSERT 
//					//이송작업재료등록
//					jrParam.setField("MODIFIER"	        , modifier); 
//					/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updCarFtMvMtlCmbnCarldYn 
//					MERGE INTO TB_YD_CARFTMVMTL TM USING (
//					    SELECT CC.COIL_NO          AS STOCK_ID
//					         , CC.HCR_GP           AS HCR_GP
//					         , CC.RECORD_PROG_STAT AS STL_PROG_CD
//					    --         , COIL.YD_STK_BED_NO AS STACK_BED_GP
//					         , (SELECT MAX(A.YD_STK_BED_NO)
//					              FROM TB_YD_CARFTMVMTL A
//					             WHERE A.STL_NO        = CC.COIL_NO
//					               AND A.YD_CAR_SCH_ID = (SELECT MAX(YD_CAR_SCH_ID) 
//					                                        FROM TB_YD_CARSCH  
//					                                       WHERE TRANS_ORD_DATE  = :V_TRANS_ORD_DT
//					                                         AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
//					                                         AND CARD_NO         = :V_CARD_NO
//					                                         AND CMBN_CARLD_YN = 'S'
//					                                       )
//					            )                   AS STACK_BED_GP   
//					         , '01'                 AS STACK_LAYER_GP
//					         , :V_YD_CAR_SCH_ID     AS YD_CAR_SCH_ID
//					         , :V_MODIFIER          AS MODIFIER
//					         , SYSDATE              AS MOD_DDTT
//					         , 'N'                  AS DEL_YN
//					      FROM USRPTA.TB_PT_COILCOMM  CC
//					     WHERE COIL_NO  IN ( 
//					        SELECT B.STL_NO
//					          FROM TB_YD_CARSCH A 
//					             , TB_YD_CARFTMVMTL B
//					         WHERE A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
//					           AND A.TRANS_ORD_DATE  = :V_TRANS_ORD_DT
//					           AND A.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
//					           AND A.CARD_NO         = :V_CARD_NO
//					           AND A.DEL_YN = 'Y'    
//					           AND B.DEL_YN = 'Y' 
//					           AND A.CMBN_CARLD_YN = 'S'
//					       )    
//					) DD ON (TM.YD_CAR_SCH_ID = DD.YD_CAR_SCH_ID AND TM.STL_NO = DD.STOCK_ID )    
//					WHEN NOT MATCHED THEN
//					INSERT (TM.YD_CAR_SCH_ID, TM.STL_NO, TM.REGISTER, TM.REG_DDTT,
//					        TM.MODIFIER, TM.MOD_DDTT, TM.DEL_YN, TM.YD_STK_BED_NO,
//					        TM.YD_STK_LYR_NO, TM.HCR_GP, TM.STL_PROG_CD)
//					VALUES (DD.YD_CAR_SCH_ID, DD.STOCK_ID, DD.MODIFIER, DD.MOD_DDTT,
//					        DD.MODIFIER, DD.MOD_DDTT, DD.DEL_YN, DD.STACK_BED_GP,
//					        DD.STACK_LAYER_GP, DD.HCR_GP, DD.STL_PROG_CD)
//					*/        
//					
//					commDao.insert(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updCarFtMvMtlCmbnCarldYn", logId, methodNm, "복수동이송작업재료등록");			
//				}
//			}
			
			//입동지시 호출/////////////////////////////////////////////////////////////////////////////////////
			if (!"".equals(szYD_CARPNT_CD)) {
				//도착가능 포인트가 있으면 입동지시 호출
				commUtils.printLog(logId, methodNm + " 차량입동포인트["+szYD_CARPNT_CD+"], 차량스케줄ID["+szYD_CAR_SCH_ID+"] - 차량입동지시요구 모듈을 호출 " , "SL");
			
				JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setResultCode(logId);	//Log ID
				recInTemp.setResultMsg(methodNm);	//Log Method Name
				recInTemp.setField("JMS_TC_CD"			, "YMYMJ662"); //차량입동지시 요구 기존:YDYDJ662
				recInTemp.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
				recInTemp.setField("YD_CARPNT_CD"		, szYD_CARPNT_CD); //입동포인트
				recInTemp.setField("YD_CAR_SCH_ID"		, szYD_CAR_SCH_ID);	//차량스케줄ID
				recInTemp.setField("CARD_NO"			, szCARD_NO);
				recInTemp.setField("CAR_NO"				, szCAR_NO);
				recInTemp.setField("CAR_KIND"			, szCAR_KIND); //차량종류
				recInTemp.setField("TRANS_FRTOMOVE_GP"	, szTRANS_FRTOMOVE_GP); //1 운송 2 이송
				//recInTemp.setField("TRANS_ORD_DATE"		, szTRANS_ORD_DT); 		//운송지시일자
				//recInTemp.setField("TRANS_ORD_SEQNO"	, szTRANS_ORD_SEQNO); 	//운송지시순번
				
				//JMS 전송
				jrRtn = commUtils.addSndData(jrRtn, recInTemp);
				
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
				
}
