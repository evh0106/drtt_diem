/**
 * @(#)CCoilL3RcvSeEJBBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2019/05/02
 *
 * @description      2열연 COIL 야드 L3 수신 처리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자     요청자  수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2019/05/02   정종균  이현진      최초 등록
 *  
 */
package com.inisteel.cim.yd.ccoil.session;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;

import com.inisteel.cim.yd.ccoil.dao.CCoilDAO;
import com.inisteel.cim.yd.ccommon.dao.CCommDAO;
import com.inisteel.cim.yd.ccommon.util.CCommUtils;
import com.inisteel.cim.yd.ccommon.util.CConstant;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;


/**
 *      [A] 클래스명 : 2열연 COIL 야드 L3수신 처리
 *
 * @ejb.bean name="CCoilL3RcvSeEJB" jndi-name="CCoilL3RcvSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
*/

public class CCoilL3RcvSeEJBBean extends BaseSessionBean {
	
	private static final long serialVersionUID = 1L;
	private CCommUtils commUtils = new CCommUtils();
	private CCommDAO   commDao   = new CCommDAO();
	private CCoilDAO   coilDao   = new CCoilDAO();
	private YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}	
	
	/**	
	 * [A] 오퍼레이션명 : 코일소재이송지시 (PTYDJ002)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord rcvPTYDJ002(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "코일소재이송지시 수신[CCoilL3RcvSeEJB.rcvPTYDJ002] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			//수신 항목 값
			String msgId     = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sFmWordDt = commUtils.trim(rcvMsg.getFieldString("FRTOMOVE_WORD_DATE"));	//이송작업지시일자
			String sModifier  = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }
			
			String sMsg     = "";

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(sFmWordDt)) {
				sMsg = "["+mthdNm+"] 이송작업지시일자가 없습니다..";
		      	commUtils.printLog(logId, sMsg, "SL");
		      	 
			    return jrRtn;	
			}

			/**********************************************************
			* 2. 저장품 이동 조건 수정
			**********************************************************/
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			
			jrParam.setField("FRTOMOVE_WORD_DATE"	, sFmWordDt); //이송작업지시일자
			
			/* 
			SELECT C.STL_NO             AS STL_NO
			     , D.FRTOMOVE_STAT_CD   AS FRTOMOVE_STAT_CD
			     , D.ARR_WLOC_CD        AS ARR_WLOC_CD
			     , E.CURR_PROG_CD       AS STL_PROG_CD
			     , E.PLNT_PROC_CD       AS PLNT_PROC_CD
			   FROM TB_YD_STOCK C
			       ,(SELECT A.*
			           FROM TB_PT_STLFRTOMOVE A
			               ,(SELECT STL_NO
			                       ,MAX(TRANSWORD_SEQNO) AS TRANSWORD_SEQNO
			                   FROM TB_PT_STLFRTOMOVE
			                  WHERE FRTOMOVE_WORD_DATE = :FRTOMOVE_WORD_DATE
			                    AND FRTOMOVE_STAT_CD IN ('1','C','0')
			                  GROUP BY STL_NO ) B
			          WHERE B.STL_NO = A.STL_NO
			            AND A.TRANSWORD_SEQNO = B.TRANSWORD_SEQNO 
			            AND A.STL_APPEAR_GP = 'E'
			            AND A.RENTPROC_COMCD IS NULL ) D
			        ,TB_PT_COILCOMM E   
			  WHERE C.STL_NO = D.STL_NO
			  AND D.STL_NO = E.COIL_NO
			 */ 
			JDTORecordSet jsSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdStockFRTOMOVE_WORD_DATE_COIL", logId, mthdNm, "크레인스케줄 조회");
			
			
			String sFrToMoveStatCd  = "";
			String ydAimRtGp     	= "";
			String sStlAppearGp  	= "";
			String ydMtlItem      	= "";
			String sStlNo          	= "";
					
			JDTORecord jrSch = JDTORecordFactory.getInstance().create();
			
			for (int ii = 1; ii <= jsSch.size(); ii++) {
				jsSch.absolute(ii);
				jrSch = jsSch.getRecord();

				sFrToMoveStatCd = commUtils.nvl(jrSch.getFieldString("FRTOMOVE_STAT_CD"),"");
				ydAimRtGp     	= commUtils.nvl(jrSch.getFieldString("YD_AIM_RT_GP"    ),"");
				sStlAppearGp 	= commUtils.nvl(jrSch.getFieldString("STL_APPEAR_GP"   ),"");
				sStlNo          = commUtils.nvl(jrSch.getFieldString("STL_NO"          ),"");
				
				//압연완료 ~ 제품창고 입고 이전 (종합판정)
				if ("E".equals(sStlAppearGp)) {
					ydMtlItem  	= "CM";
				} else {
					ydMtlItem  	= "CG";
				}
				
				//이송지시등록
				if (!"C".equals(sFrToMoveStatCd) && !ydAimRtGp.startsWith("E")) {
					jrParam.setField("YD_MTL_ITEM"  , ydMtlItem);
					jrParam.setField("YD_AIM_RT_GP"	, this.getCoilCurYdAimRtGpInfo_002(logId, mthdNm, sStlNo));	
					jrParam.setField("STL_NO"       , jrSch.getFieldString("STL_NO"));
					
					/*
					UPDATE TB_YD_STOCK
					   SET MODIFIER     = :V_MODIFIER
					     , MOD_DDTT     = SYSDATE
					     , YD_MTL_ITEM  = NVL(:V_YD_MTL_ITEM, YD_MTL_ITEM)
					     , YD_AIM_RT_GP = :V_YD_AIM_RT_GP
					 WHERE STL_NO = :V_STL_NO    
					 */
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdSTockAimRtGp", logId, mthdNm, "이송지시등록");
				}
				//이송지시취소
				else if ("C".equals(sFrToMoveStatCd) && ydAimRtGp.startsWith("E")) {
					String[] rVal = new String[1];
					//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
					rVal= coilDao.getYdAimRtGp("C", jrParam);
					
					jrParam.setField("YD_AIM_RT_GP"	, rVal[0]);	
					jrParam.setField("STL_NO"       , sStlNo);
					
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdSTockAimRtGp", logId, mthdNm, "이송지시취소");
					
				}
				
				//목표행선이 이송상태가 아닌 경우
				else if ("C".equals(sFrToMoveStatCd) && !ydAimRtGp.startsWith("E")) {
					sMsg = sStlNo + "이송지시취소 불가 : 목표행선이 이송지시 상태가 아님";
					commUtils.printLog(logId, sMsg, "SL");
				}
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
	 * 목표행선 조회
	 * (코일 이송지시 시점에 해당 재료의 목표행선 정보를 가져온다)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return JDTORecord
	 * @throws JDTOException
	 */
	public String getCoilCurYdAimRtGpInfo_002(String logId , String mthdNm, String sStlNo) throws DAOException {
		mthdNm = "목표행선 조회[CCoilL3RcvSeEJB.getCoilCurYdAimRtGpInfo_002] < " + mthdNm;
		
		String ydAimRtGp = "XX";
		
		try {
			
			commUtils.printLog(logId, mthdNm, "S+");
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, "");
			jrParam.setField("COIL_NO", sStlNo);
			
			/*
			SELECT RECORD_PROG_STAT
			     , PLNT_PROC_CD
			     , CURR_PROG_CD
			     , NEXT_PROC
			     , PLAN_PROC1
			  FROM USRPTA.TB_PT_COILCOMM 
			 WHERE COIL_NO = :V_COIL_NO
			 */
			JDTORecordSet jsCoilComm = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getPtCoilComm", logId, mthdNm, "코일공통조회");

			if (jsCoilComm.size() == 0) {
				return ydAimRtGp;
			}
			jsCoilComm.first();
			String sRecordProgStat = jsCoilComm.getRecord().getFieldString("RECORD_PROG_STAT");
			
			if ("2".equals(sRecordProgStat)) {
//				String sPLNT_PROC_CD = rsRst.getRecord().getFieldString("PLNT_PROC_CD");		
//				String sCURR_PROG_CD = rsRst.getRecord().getFieldString("CURR_PROG_CD");		
				String sNextProc    = jsCoilComm.getRecord().getFieldString("NEXT_PROC");		
				String sPlanProc1   = jsCoilComm.getRecord().getFieldString("PLAN_PROC1");
				
				String sWorkProc = "";
				
				if (!"".equals(sNextProc)) {
					sWorkProc = sNextProc; 
				} else {
					sWorkProc = sPlanProc1;
				}
				
				if (sWorkProc.startsWith("1")) {
					ydAimRtGp = "EA";
				} else if (sWorkProc.startsWith("5") || sWorkProc.startsWith("6")) {
					ydAimRtGp = "EB";
				} else if (sWorkProc.startsWith("9S")) {
					ydAimRtGp = "ED";
				} else {
					ydAimRtGp = "EC";
				}
			}
			
			commUtils.printLog(logId, mthdNm, "S-");
			
			return ydAimRtGp;
			
		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, mthdNm, e), commUtils, e);
			return ydAimRtGp;
		} catch (Exception e) {
			return ydAimRtGp;
		}
	}
		
	/**	
	 * [A] 오퍼레이션명 : 코일소재 임가공 이송지시 수신(PTYDJ003)
	 * LHJ - 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord rcvPTYDJ003(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "코일소재 임가공 이송지시 수신[CCoilL3RcvSeEJB.rcvPTYDJ003] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			//수신 항목 값
			String msgId     = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
//			String ModGp 	= commUtils.trim(rcvMsg.getFieldString("MOD_GP"));				//작업구분
			String sFmWordDt = commUtils.trim(rcvMsg.getFieldString("FRTOMOVE_WORD_DATE"));	//이송작업지시일자
			String sModifier  = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			String sMsg      = "";
			if ("".equals(sModifier)) { sModifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(sFmWordDt)) {
				sMsg = "["+mthdNm+"] 이송작업지시일자가 없습니다..";
		      	commUtils.printLog(logId, sMsg, "SL");
		        return jrRtn; 
			}

			/**********************************************************
			* 2. 저장품 이동 조건 수정
			**********************************************************/
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("FRTOMOVE_WORD_DATE", sFmWordDt); //이송작업지시일자
			
			/* 
			SELECT C.STL_NO                    AS STL_NO
			     , C.YD_AIM_RT_GP              AS YD_AIM_RT_GP
			     , D.FRTOMOVE_WORD_DATE        AS FRTOMOVE_WORD_DATE
			     , D.URGENT_FRTOMOVE_WORD_GP   AS URGENT_FRTOMOVE_WORD_GP
			     , D.WO_CAR_PLNT_PROC_CD       AS PLNT_PROC_CD
			     , D.FRTOMOVE_STAT_CD          AS FRTOMOVE_STAT_CD
			     , D.FRTOMOVE_ORD_CANCEL_DATE  AS FRTOMOVE_ORD_CANCEL_DATE
			     , D.RENTPROC_COMCD
			  FROM TB_YD_STOCK C
			     ,(SELECT A.STL_NO                    AS STL_NO
			            , A.TRANSWORD_SEQNO           AS TRANSWORD_SEQNO
			            , A.FRTOMOVE_WORD_DATE        AS FRTOMOVE_WORD_DATE
			            , A.WO_CAR_PLNT_PROC_CD       AS WO_CAR_PLNT_PROC_CD
			            , A.URGENT_FRTOMOVE_WORD_GP   AS URGENT_FRTOMOVE_WORD_GP
			            , A.FRTOMOVE_STAT_CD          AS FRTOMOVE_STAT_CD
			            , A.SLAB_WO_RT_CD             AS SLAB_WO_RT_CD
			            , A.FRTOMOVE_ORD_CANCEL_DATE  AS FRTOMOVE_ORD_CANCEL_DATE
			            , A.RENTPROC_COMCD
			         FROM TB_PT_STLFRTOMOVE A
			            ,(SELECT STL_NO
			                   , MAX(TRANSWORD_SEQNO) AS TRANSWORD_SEQNO
			                FROM TB_PT_STLFRTOMOVE
			               WHERE FRTOMOVE_WORD_DATE = :V_FRTOMOVE_WORD_DATE
			                 AND FRTOMOVE_STAT_CD IN ('1','C')
			               GROUP BY STL_NO ) B
			        WHERE B.STL_NO = A.STL_NO
			          AND A.TRANSWORD_SEQNO = B.TRANSWORD_SEQNO 
			          AND A.STL_APPEAR_GP   = 'E'
			          AND A.RENTPROC_COMCD IS NOT NULL
			      ) D
			 WHERE C.STL_NO = D.STL_NO
			 */ 
			JDTORecordSet jsStlFrToMove = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdStockFRTOMOVE_WORD_DATE_RENTCOIL", logId, mthdNm, "이송지시 조회");
			
			JDTORecord jrStlFrToMove = JDTORecordFactory.getInstance().create();
			
			String sFrToMoveStatCd 	= "";
			String ydAimRtGp     	= "";
			String sStlNo           = "";
			
			for (int ii = 0; ii < jsStlFrToMove.size(); ii++) {
				
				jrParam = commUtils.getParam(logId, mthdNm, sModifier);

				jrStlFrToMove = jsStlFrToMove.getRecord(ii);

				sFrToMoveStatCd = commUtils.nvl(jrStlFrToMove.getFieldString("FRTOMOVE_STAT_CD"),"");
				ydAimRtGp     	= commUtils.nvl(jrStlFrToMove.getFieldString("YD_AIM_RT_GP"    ),"");
				sStlNo          = commUtils.nvl(jrStlFrToMove.getFieldString("STL_NO"          ),"");

				
				//이송지시등록
				if ("1".equals(sFrToMoveStatCd) && ydAimRtGp.startsWith("E")) {
					sMsg = sStlNo + "등록불가 :이미 이송지시 상태임";
					commUtils.printLog(logId, sMsg, "SL");
				}
				else if ("1".equals(sFrToMoveStatCd) && !ydAimRtGp.startsWith("E")) {
					
					JDTORecordSet jsYdStock = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYdStock", logId, mthdNm, "저장품 조회");
					if (jsYdStock.size() <= 0) {
						sMsg = "저장품 조회 실패";
				      	commUtils.printLog(logId, sMsg, "SL");
				        return jrRtn;
					}
					jsYdStock.first();
					jrParam.setField("STL_NO" 				  , jsYdStock.getRecord().getFieldString("STL_NO" 				  ));
					jrParam.setField("FRTOMOVE_ORD_DATE" 	  , jsYdStock.getRecord().getFieldString("FRTOMOVE_ORD_DATE" 	  ));
					jrParam.setField("PLNT_PROC_CD" 		  , jsYdStock.getRecord().getFieldString("PLNT_PROC_CD" 		  ));
					jrParam.setField("URGENT_FRTOMOVE_WORD_GP", jsYdStock.getRecord().getFieldString("URGENT_FRTOMOVE_WORD_GP"));
					
					String[] rVal = new String[1];
					//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
					rVal= coilDao.getYdAimRtGp("C", jrParam);
					
					jrParam.setField("YD_AIM_RT_GP"	, rVal[0]);
					
					/*
					UPDATE TB_YD_STOCK
					   SET MODIFIER     = :V_MODIFIER
					     , MOD_DDTT     = SYSDATE
					     , FRTOMOVE_ORD_DATE       = :V_FRTOMOVE_ORD_DATE
					     , PLNT_PROC_CD            = :V_PLNT_PROC_CD
					     , URGENT_FRTOMOVE_WORD_GP = :v_URGENT_FRTOMOVE_WORD_GP
					 WHERE STL_NO = :V_STL_NO    
					 */
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdSTockFrtoMoveOrdWo", logId, mthdNm, "저장품 이송지시등록");
				}
				
				//이송지시취소
				else if ("C".equals(sFrToMoveStatCd) && ydAimRtGp.startsWith("E")) {
					String[] rVal = new String[1];
					//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
					rVal= coilDao.getYdAimRtGp("C", jrParam);
					
					jrParam.setField("YD_AIM_RT_GP"	, rVal[0]);	
					jrParam.setField("STL_NO"       , sStlNo);
					
					/*
					UPDATE TB_YD_STOCK
					   SET MODIFIER     = :V_MODIFIER
					     , MOD_DDTT     = SYSDATE
					     , YD_MTL_ITEM  = NVL(:V_YD_MTL_ITEM, YD_MTL_ITEM)
					     , YD_AIM_RT_GP = :V_YD_AIM_RT_GP
					 WHERE STL_NO = :V_STL_NO  
					 */
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdSTockAimRtGp", logId, mthdNm, "이송지시취소");
				}
				
				//목표행선이 이송상태가 아닌 경우
				else if ("C".equals(sFrToMoveStatCd) && !ydAimRtGp.startsWith("E")) {
					sMsg = sStlNo + "이송지시취소 불가 : 목표행선이 이송지시 상태가 아님";
					commUtils.printLog(logId, sMsg, "SL");
				}

			} // end for
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 * 코일제품출하지시대기 (DMYDR005)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvDMYDR005(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "코일제품출하지시대기[CCoilL3RcvSeEJB.rcvDMYDR005] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		int intRtnVal = 0;
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			//수신 항목 값
			String msgId             = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
            String ydGp              = commUtils.trim(rcvMsg.getFieldString("YD_GP"             ));    // 야드구분
            String sStlAppearGp      = commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"     ));   // 재료외형구분
            String sStlNo            = commUtils.trim(rcvMsg.getFieldString("STL_NO"            ));   // 재료번호
            String sCurrProgCd       = commUtils.trim(rcvMsg.getFieldString("CURR_PROG_CD"      ));   // 현재진도코드
            String sOrdYeojaeGp      = commUtils.trim(rcvMsg.getFieldString("ORD_YEOJAE_GP"     ));   // 주문여재구분
            String sOrdNo            = commUtils.trim(rcvMsg.getFieldString("ORD_NO"            ));   // 주문번호
            String sOrdDtl           = commUtils.trim(rcvMsg.getFieldString("ORD_DTL"           ));   // 주문행번
            String sOrdGp            = commUtils.trim(rcvMsg.getFieldString("ORD_GP"            ));   // 수주구분
            String sCustCd           = commUtils.trim(rcvMsg.getFieldString("CUST_CD"           ));   // 고객코드
            String sDestCd           = commUtils.trim(rcvMsg.getFieldString("DEST_CD"           ));   // 목적지코드
            String sDlvrddRuleDd     = commUtils.trim(rcvMsg.getFieldString("DLVRDD_RULE_DD"    ));   // 납기기준일
            String sDestTelNo        = commUtils.trim(rcvMsg.getFieldString("DEST_TEL_NO"       ));   // 목적지전화번호
            String sDistShipassignGp = commUtils.trim(rcvMsg.getFieldString("DIST_SHIPASSIGN_GP"));   // 출하배선지시구분
			
			String sModifier          = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)

			if ("".equals(sModifier)) { sModifier = msgId; }
			mthdNm = msgId.substring(0, 2) + mthdNm;
			
			/**********************************************************
			* 0. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(sStlNo)) {
				throw new Exception("저장품Id가 없습니다..");
			}

			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			
			/**********************************************************
			* 1. 야드목표행선지구분 수정
			**********************************************************/
			String[] rVal = new String[1];
			rVal= coilDao.getYdAimRtGp("C",jrParam );		
			jrParam.setField("YD_AIM_RT_GP", rVal[0]);
			jrParam.setField("STL_PROG_CD" , rVal[1]);
			
			jrParam.setField("STL_NO"	   , sStlNo); //저장품 ID

			/*
			UPDATE TB_YD_STOCK
			   SET MOD_DDTT     = SYSDATE
			     , sModifier     = :V_MODIFIER
			     , YD_AIM_RT_GP = :V_YD_AIM_RT_GP
			     , STL_PROG_CD  = NVL(:V_STL_PROG_CD, STL_PROG_CD)
			 WHERE STL_NO = :V_STL_NO         
			*/ 
			intRtnVal = commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStockAimRtGpStlProgCd", logId, mthdNm, "저장품 수정");
				
			if (intRtnVal == 0) {
				throw new Exception("수신한 재료번호 ["+ sStlNo+"]에 대한 저장품 DATA가 존재하지 않음");
			}
			
			/**********************************************************
			* 2. 대상 코일 위치 판단
			*  - 코일 위치가 야드가 아니면 전문 송신 안함
			**********************************************************/
			/*
			SELECT *
			  FROM TB_YD_STKLYR
			 WHERE STL_NO = :V_STL_NO
			   AND DEL_YN = 'N'
			   AND YD_STK_COL_GP LIKE 'J%'
			 */
			JDTORecordSet jsLyr = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getStkLyrByStlNo", logId, mthdNm, "저장품위치 조회");

			boolean bYd = true;
			String ydStkColGp = "";
			for (int i = 0; i < jsLyr.size(); ++i) {
				
				ydStkColGp = commUtils.trim(jsLyr.getRecord(i).getFieldString("YD_STK_COL_GP"));
				bYd = ydStkColGp.matches("[J][A-H]\\d\\d\\d\\d");
			
				if (!bYd) {
					commUtils.printLog(logId, mthdNm, "S-");
					return jrRtn;
				}
			}
			
			/**********************************************************
			* 99. 저장품제원 : 코일야드L2 로 송신(YDY5L002)
			**********************************************************/
			JDTORecord sndL2Msg = commUtils.getParam(logId, mthdNm, "");
			sndL2Msg.setField("JMS_TC_CD"       , "YDY5L002");
			sndL2Msg.setField("YD_INFO_SYNC_CD"	, "5"       ); //야드정보동기화코드
			sndL2Msg.setField("MSG_GP"			, "I"       ); //전문구분
			sndL2Msg.setField("STL_NO"       	, sStlNo ); //재료번호
			sndL2Msg.setField("YD_STK_COL_GP"   , "");
			sndL2Msg.setField("YD_STK_BED_NO"   , "");
				
			jrRtn = commUtils.addSndData(jrRtn,coilDao.getMsgL2("YDY5L002", sndL2Msg));	 //전송 Data 생성	
			
			commUtils.printLog(logId, mthdNm, "S-");
			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 * 코일제품반납대기
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvDMYDR008(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "코일제품반납대기 수신[CCoilL3RcvSeEJB.rcvDMYDR008] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		int intRtnVal = 0;
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			//수신 항목 값
			String msgId      = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID

			String ydGp                  = commUtils.trim(rcvMsg.getFieldString("YD_GP"                  )); //야드구분
			String sStlAppearGp          = commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"          )); //재료외형구분
 			String sStlNo                = commUtils.trim(rcvMsg.getFieldString("STL_NO"                 )); //재료번호
            String sCurrProgCd           = commUtils.trim(rcvMsg.getFieldString("CURR_PROG_CD"           )); //현재진도코드
			String sWoCarPlntProcCd      = commUtils.trim(rcvMsg.getFieldString("WO_CAR_PLNT_PROC_CD"    )); //지시차공장공정코드
			String sFrtomoveOrdDate      = commUtils.trim(rcvMsg.getFieldString("FRTOMOVE_ORD_DATE"      )); //이송지시일자
			String sUrgentFrtoMoveWirdGp = commUtils.trim(rcvMsg.getFieldString("URGENT_FRTOMOVE_WORD_GP")); //긴급이송작업지시구분
			String sCancelYn             = commUtils.trim(rcvMsg.getFieldString("CANCEL_YN"              )); //취소유무
			
			String sModifier   = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)

			if ("".equals(sModifier)) { sModifier = msgId; }
			mthdNm = msgId.substring(0, 2) + mthdNm;
			
			/**********************************************************
			* 0. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(sStlNo)) {
				throw new Exception("저장품Id가 없습니다..");
			}

			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);

			if ("Y".equals(sCancelYn)) {
				jrRtn = this.procDmTcCncl(rcvMsg);
				return jrRtn;
			}
				
			/**********************************************************
			* 1. 야드목표행선지구분 수정
			**********************************************************/
			String[] rVal = new String[1];
			rVal= coilDao.getYdAimRtGp("C",jrParam );		
			jrParam.setField("YD_AIM_RT_GP", rVal[0]);
			jrParam.setField("STL_NO"	   , sStlNo); //저장품 ID

			/*
			UPDATE TB_YD_STOCK
			   SET MOD_DDTT     = SYSDATE
			     , sModifier     = :V_MODIFIER
			     , YD_AIM_RT_GP = :V_YD_AIM_RT_GP
			     , STL_PROG_CD  = NVL(:V_STL_PROG_CD, STL_PROG_CD)
			 WHERE STL_NO = :V_STL_NO       
			*/ 
			intRtnVal = commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStockAimRtGpStlProgCd", logId, mthdNm, "저장품 수정");
				
			if (intRtnVal == 0) {
				throw new Exception("수신한 재료번호 ["+ sStlNo+"]에 대한 저장품 DATA가 존재하지 않음");
			}
			
			/**********************************************************
			* 2. 저장품제원 : 코일야드L2 로 송신(YDY5L002)
			**********************************************************/
			JDTORecord sndL2Msg = commUtils.getParam(logId, mthdNm, "");
			sndL2Msg.setField("JMS_TC_CD"       , "YDY5L002");
			sndL2Msg.setField("YD_INFO_SYNC_CD"	, "5"       ); //야드정보동기화코드
			sndL2Msg.setField("MSG_GP"			, "I"       ); //전문구분
			sndL2Msg.setField("STL_NO"       	, sStlNo ); //재료번호
			sndL2Msg.setField("YD_STK_COL_GP"   , "");
			sndL2Msg.setField("YD_STK_BED_NO"   , "");
				
			jrRtn = commUtils.addSndData(jrRtn,coilDao.getMsgL2("YDY5L002", sndL2Msg));	 //전송 Data 생성	
			
			commUtils.printLog(logId, mthdNm, "S-");
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	} 
	
	
	/**
	 * 2열연코일야드 출하전문 취소 - receiveCancel - coilReceiveCancel
	 * @param rcvMsg
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procDmTcCncl(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "2열연코일야드 출하전문 취소[CCoilL3RcvSeEJB.procDmTcCncl] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
	    	/*
			DMYDR008	코일제품반납확정			STL_NO
			DMYDR011	코일제품고간이송지시		STL_NO1 ~ STL_NO20
			DMYDR014	코일제품목전				STL_NO
			DMYDR027	코일제품보관지시			STL_NO
			
			DMYDR060	제품운송상차지시
			DMYDR070	코일이송상차대기장도착PDA
			DMYDR073	코일이송하차대기장도착PDA

			DMYDR008	코일제품반납대기		1.저장품 이동 조건 변경
			DMYDR011	코일제품고간이송지시	1.운송지시번호 삭제 (운송지시번호로 찾아서 삭제 함),저장품 이동 조건 변경
			DMYDR014	코일제품목전			1.저장품 이동 조건 변경
			DMYDR027	코일제품보관지시		KEEPSTOCK_STL_YN= ''
			DMYDR030	코일제품출하완료              1.저장품 이동 조건 변경
			DMYDR060	제품운송상차지시
			DMYDR070	코일이송상차대기장도착PDA
			DMYDR073	코일이송하차대기장도착PDA
			*/			
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			/**********************************************************
			* 0. 
			**********************************************************/
			String msgId      = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sStlGp     = "C";
			
			String sModifier   = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			
			if ("".equals(sModifier)) { sModifier = msgId; }
			
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);

			/**********************************************************
			* 1. 크레인스케줄취소, 작업예약취소
			**********************************************************/
			if (CConstant.DMYDR070.equals(msgId)
			||  CConstant.DMYDR073.equals(msgId)
			||  CConstant.DMYDR060.equals(msgId)) {
				//차량스케줄 삭제 및 차량 POINT Clear
				commUtils.printLog(logId, "차량스케줄삭제 및 차량Point Clear", "SL");
				this.delCarSchNCarPointForDist(rcvMsg);
				
				//1.크레인 스케줄취소 ,2 작업예약취소
				String sTransOrdDate  = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"   ));
		    	String sTransOrdSeqno = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));
				
		    	jrParam.setField("TRANS_ORD_DATE" , sTransOrdDate );
 				jrParam.setField("TRANS_ORD_SEQNO", sTransOrdSeqno);
 				
 				/*
				SELECT A.YD_CRN_SCH_ID
				     , A.YD_SCH_CD
				  FROM TB_YD_CRNSCH    A
				     , TB_YD_CRNWRKMTL B
				     , TB_YD_STOCK     C
				 WHERE A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID
				   AND B.STL_NO        = C.STL_NO
				   AND A.DEL_YN        = 'N'
				   AND C.TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
				   AND C.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
 				 */
 				JDTORecordSet jsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdCrnschTransNo", logId, mthdNm, "크레인스케줄 조회");
 				
 				if (jsRst.size() > 0) {
 					
 					commUtils.printLog(logId, "스케줄 취소대상이 존재함", "SL");
 					
 					for (int i = 1; i <= jsRst.size(); ++i) {
 						jsRst.absolute(i);
 						jrParam.setField("YD_CRN_SCH_ID", jsRst.getRecord().getFieldString("YD_CRN_SCH_ID"));
 						jrParam.setField("YD_SCH_CD"    , jsRst.getRecord().getFieldString("YD_SCH_CD"    ));
 						
 						//ejbConn.trx("YdSimSeEJB", "wrkCncl", recPara);
 						//크레인 스케줄 삭제
 						EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
 						JDTORecord jrRst = (JDTORecord)ejbConn.trx("trtCrnSchCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
 						
 						jrRtn = commUtils.addSndData(jrRtn, jrRst);
 					}
 				} else if (jsRst.size() == 0) {
 					
 					commUtils.printLog(logId, "스케줄 취소대상이 존재안함", "SL");
 					/*
					SELECT B.YD_WBOOK_ID
					  FROM TB_YD_STOCK      A
					     , TB_YD_WRKBOOKMTL B
					 WHERE A.STL_NO =B.STL_NO
					   AND A.TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
					   AND A.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
					   AND B.DEL_YN = 'N'
					   AND ROWNUM <= 1
 					 */
 					JDTORecordSet jsWrkBk = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdWrkbookTransSeq", logId, mthdNm, "작업예약조회");
 					
 					if (jsWrkBk.size() > 0) {
 	 					for (int i = 1; i <= jsRst.size(); ++i) {
 	 						jsWrkBk.absolute(i);
 	 						jrParam.setField("YD_WBOOK_ID", jsWrkBk.getRecord().getFieldString("YD_WBOOK_ID"));
 	 						
 	 						//크레인 작업예약 삭제
 	 						EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
 	 						JDTORecord jrRst = (JDTORecord)ejbConn.trx("delWrkbook", new Class[] { JDTORecord.class }, new Object[] { jrParam });
 	 						
 	 						jrRtn = commUtils.addSndData(jrRtn, jrRst);
 	 					}
 					}
 				}
			}
			
			/**********************************************************
			* 2. 출하전문 처리(재료번호 1개 : STL_NO)
			**********************************************************/
			if (CConstant.DMYDR008.equals(msgId) 
	    	||	CConstant.DMYDR014.equals(msgId) 
	    	||	CConstant.DMYDR027.equals(msgId) 
	    	||	CConstant.DMYDR030.equals(msgId)) {
				
				jrParam = commUtils.getParam(logId, mthdNm, sModifier);
				
				String sStlAppearGp = commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP")); //재료외형구분
 	 			String sStlNo       = commUtils.trim(rcvMsg.getFieldString("STL_NO"       )); //재료번호
 	            String sCurrProgCd  = commUtils.trim(rcvMsg.getFieldString("CURR_PROG_CD" )); //현재진도코드
 	            
 	            jrParam.setField("STL_APPEAR_GP", sStlAppearGp);
		    	jrParam.setField("STL_NO"       , sStlNo);
				jrParam.setField("STL_PROG_CD"  , sCurrProgCd);
				
				if (CConstant.DMYDR008.equals(msgId)) {
					String sWoCarPlntProcCd      = commUtils.trim(rcvMsg.getFieldString("WO_CAR_PLNT_PROC_CD"    )); //지시차공장공정코드
 	    			String sFrtomoveOrdDate      = commUtils.trim(rcvMsg.getFieldString("FRTOMOVE_ORD_DATE"      )); //이송지시일자
 	    			String sUrgentFrtoMoveWirdGp = commUtils.trim(rcvMsg.getFieldString("URGENT_FRTOMOVE_WORD_GP")); //긴급이송작업지시구분
 	   			
 		    		// 레코드에 추가
 			    	jrParam.setField("WO_CAR_PLNT_PROC_CD"    , sWoCarPlntProcCd     );
 			    	jrParam.setField("FRTOMOVE_ORD_DATE"      , sFrtomoveOrdDate     );
 			    	jrParam.setField("URGENT_FRTOMOVE_WORD_GP", sUrgentFrtoMoveWirdGp);
				}
				
				if (CConstant.DMYDR014.equals(msgId) 
				||	CConstant.DMYDR027.equals(msgId)) {
					String sOrdYeojaeGp      = commUtils.trim(rcvMsg.getFieldString("ORD_YEOJAE_GP"     ));   // 주문여재구분
 	    			String sOrdNo            = commUtils.trim(rcvMsg.getFieldString("ORD_NO"            ));   // 주문번호
 	    			String sOrdDtl           = commUtils.trim(rcvMsg.getFieldString("ORD_DTL"           ));   // 주문행번
 	    			String sOrdGp            = commUtils.trim(rcvMsg.getFieldString("ORD_GP"            ));   // 수주구분
 	    			String sCustCd           = commUtils.trim(rcvMsg.getFieldString("CUST_CD"           ));   // 고객코드
 	    			String sDestCd           = commUtils.trim(rcvMsg.getFieldString("DEST_CD"           ));   // 목적지코드
 	    			String sDlvrddRuleDd     = commUtils.trim(rcvMsg.getFieldString("DLVRDD_RULE_DD"    ));   // 납기기준일
 	    			String sDestTelNo        = commUtils.trim(rcvMsg.getFieldString("DEST_TEL_NO"       ));   // 목적지전화번호
 	    			String sDistShipassignGp = commUtils.trim(rcvMsg.getFieldString("DIST_SHIPASSIGN_GP"));   // 출하배선지시구분
 	               
 		    		// 레코드에 추가
 			    	jrParam.setField("ORD_YEOJAE_GP"     , sOrdYeojaeGp     );
 			    	jrParam.setField("ORD_NO"            , sOrdNo           );
 			    	jrParam.setField("ORD_DTL"           , sOrdDtl          );
 			    	jrParam.setField("ORD_GP"            , sOrdGp           );
 			    	jrParam.setField("CUST_CD"           , sCustCd          );
 			    	jrParam.setField("DEST_CD"           , sDestCd          );
 			    	jrParam.setField("YD_DLVRDD_RULE_DD" , sDlvrddRuleDd    );
 			    	jrParam.setField("DEST_TEL_NO"       , sDestTelNo       );
 			    	jrParam.setField("DIST_SHIPASSIGN_GP", sDistShipassignGp);
				}
				
				//야도목표행성지구분
				String[] rVal = new String[1];
 				rVal = coilDao.getYdAimRtGp2(msgId , sStlGp, sStlNo, sCurrProgCd);		
				jrParam.setField("YD_AIM_RT_GP", rVal[0]);
				
				if (CConstant.DMYDR027.equals(msgId)) {
					jrParam = commUtils.getParam(logId, mthdNm, sModifier);
					
					jrParam.setField("STL_NO"     , sStlNo);
					jrParam.setField("DEL_YN"     , "N");
					jrParam.setField("SCARFING_YN", "N");
					
					/*
					UPDATE TB_YD_STOCK
					   SET MODIFIER      = :V_MODIFIER
					     , MOD_DDTT      = SYSDATE
					     , STL_APPEAR_GP = :V_STL_APPEAR_GP
					     , STL_PROG_CD   = :V_STL_PROG_CD  
					     , YD_AIM_RT_GP  = :V_YD_AIM_RT_GP
					     
					     --DMYDR003, DMYDR008, DMYDR009
					     , WO_CAR_PLNT_PROC_CD      = NVL(:V_WO_CAR_PLNT_PROC_CD    , WO_CAR_PLNT_PROC_CD    )
					     , FRTOMOVE_ORD_DATE        = NVL(:V_FRTOMOVE_ORD_DATE      , FRTOMOVE_ORD_DATE      )
					     , URGENT_FRTOMOVE_WORD_GP  = NVL(:V_URGENT_FRTOMOVE_WORD_GP, URGENT_FRTOMOVE_WORD_GP)
					     --DMYDR013  DMYDR014  DMYDR015
					     --DMYDR016  DMYDR018  DMYDR026
					     --DMYDR027  DMYDR028
					     , ORD_YEOJAE_GP            = NVL(:V_ORD_YEOJAE_GP          , ORD_YEOJAE_GP          )
					     , ORD_NO                   = NVL(:V_ORD_NO                 , ORD_NO                 )
					     , ORD_DTL                  = NVL(:V_ORD_DTL                , ORD_DTL                )
					     , ORD_GP                   = NVL(:V_ORD_GP                 , ORD_GP                 )
					     , CUST_CD                  = NVL(:V_CUST_CD                , CUST_CD                )
					     , DEST_CD                  = NVL(:V_DEST_CD                , DEST_CD                )
					     , YD_DLVRDD_RULE_DD        = NVL(:V_YD_DLVRDD_RULE_DD      , YD_DLVRDD_RULE_DD      )
					     , DEST_TEL_NO              = NVL(:V_DEST_TEL_NO            , DEST_TEL_NO            )
					     , DIST_SHIPASSIGN_GP       = NVL(:V_DIST_SHIPASSIGN_GP     , DIST_SHIPASSIGN_GP     )
					     --DMYDR018
					     , SHIPASSIGN_WORD_DATE     = NVL(:V_SHIPASSIGN_WORD_DATE   , SHIPASSIGN_WORD_DATE   )
					     , SHIPASSIGN_WORD_SEQNO    = NVL(:V_SHIPASSIGN_WORD_SEQNO  , SHIPASSIGN_WORD_SEQNO  )
					     , SHIP_CD                  = NVL(:V_SHIP_CD                , SHIP_CD                )
					     , SHIP_NAME                = NVL(:V_SHIP_NAME              , SHIP_NAME              )
					     , SAILNO                   = NVL(:V_SAILNO                 , SAILNO                 )
					     , PRE_AR_STAT_CD           = NVL(:V_PRE_AR_STAT_CD         , PRE_AR_STAT_CD         )
					     , CAR_LOTID                = NVL(:V_CAR_LOTID              , CAR_LOTID              )
					     --DMYDR016
					     , CAR_NO                   = NVL(:V_CAR_NO                 , CAR_NO                 )
					     , CARD_NO                  = NVL(:V_CARD_NO                , CARD_NO                )
					     --DMYDR026  DMYDR027  DMYDR028
					     , DEL_YN                   = NVL(:V_DEL_YN                 , DEL_YN                 )
					     , SCARFING_YN              = NVL(:V_SCARFING_YN            , SCARFING_YN            )
					 WHERE STL_NO = :V_STL_NO
					 */
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStockDmYd", logId, mthdNm, "저장품 수정");
				} else {
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStockDmYd", logId, mthdNm, "저장품 수정");
				}
			}
			/**********************************************************
			* 3. 출하전문 처리(재료번호 N개 : STL_NO1 ... STL_NO20)
			**********************************************************/
			if (CConstant.DMYDR011.equals(msgId)
    		||  CConstant.DMYDR070.equals(msgId)
    		||  CConstant.DMYDR073.equals(msgId) 
    		||  CConstant.DMYDR060.equals(msgId)) {
				
				String sStlAppearGp   = commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"  )); //재료외형구분
				String sTransOrdDate  = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"   ));
		    	String sTransOrdSeqno = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));
		    	String sStlNo         = "";
		    	
		    	String sDistShipassignGp    = "";
	    		String sShipassignWirdDate  = "";
	    		String sShipassignWordSeqno = "";
	    		String sShipCd              = "";
	    		String sShipName            = "";
	    		String sRshpHoldNo          = "";
	    		String sSailno              = "";
	    		String sCurrProgCd          = "";
		    			
				for (int i = 0; i < 20; ++i) {
					jrParam = commUtils.getParam(logId, mthdNm, sModifier);
					
					jrParam.setField("STL_APPEAR_GP", sStlAppearGp);
					jrParam.setField("TRANS_ORD_DATE" , sTransOrdDate );
	 				jrParam.setField("TRANS_ORD_SEQNO", sTransOrdSeqno);
					
					sStlNo = commUtils.trim(rcvMsg.getFieldString("STL_NO"+(i+1)));
					
					if ("".equals(sStlNo)) {
						break;
					}
					
					jrParam.setField("STL_NO", sStlNo);
					
					if (CConstant.DMYDR070.equals(msgId)
					||  CConstant.DMYDR073.equals(msgId)
					||  CConstant.DMYDR060.equals(msgId)) {
						
						sDistShipassignGp    = commUtils.trim(rcvMsg.getFieldString("DIST_SHIPASSIGN_GP"   ));   // 출하배선지시구분
						sShipassignWirdDate  = commUtils.trim(rcvMsg.getFieldString("SHIPASSIGN_WORD_DATE" ));
						sShipassignWordSeqno = commUtils.trim(rcvMsg.getFieldString("SHIPASSIGN_WORD_SEQNO"));
						sShipCd              = commUtils.trim(rcvMsg.getFieldString("SHIP_CD"              ));
						sShipName            = commUtils.trim(rcvMsg.getFieldString("SHIP_NAME"            ));
						sRshpHoldNo          = commUtils.trim(rcvMsg.getFieldString("RSHP_HOLD_NO"         ));
						sSailno              = commUtils.trim(rcvMsg.getFieldString("SAILNO"               ));
						sCurrProgCd          = commUtils.trim(rcvMsg.getFieldString("CURR_PROG_CD"         )); //현재진도코드

						jrParam.setField("DIST_SHIPASSIGN_GP"   , sDistShipassignGp   );
						jrParam.setField("SHIPASSIGN_WORD_DATE" , sShipassignWirdDate );
						jrParam.setField("SHIPASSIGN_WORD_SEQNO", sShipassignWordSeqno);
						jrParam.setField("SHIP_CD"              , sShipCd             );
						jrParam.setField("SHIP_NAME"            , sShipName           );
						jrParam.setField("RSHP_HOLD_NO"         , sRshpHoldNo         );
						jrParam.setField("SAILNO"               , sSailno             );
						jrParam.setField("STL_PROG_CD"          , sCurrProgCd         );
					}
					
//					if (CConstant.DMYDR025.equals(msgId)) {
//						
//						String sCarNo  = commUtils.trim(rcvMsg.getFieldString("CAR_NO" ));
//	    				String scARDnO = commUtils.trim(rcvMsg.getFieldString("CARD_NO"));
//	    				
//	    				// 레코드에 추가
//	    				jrParam.setField("CAR_NO"       , sCarNo);
//	    				jrParam.setField("CARD_NO"      , scARDnO);
//					}
					
					if (CConstant.DMYDR011.equals(msgId)
					||  CConstant.DMYDR025.equals(msgId)) {
						//처리없음
					} else {
						//야드목표행성지구분
						String[] rVal = new String[1];
		 				rVal = coilDao.getYdAimRtGp2(msgId , sStlGp, sStlNo, sCurrProgCd);		
						jrParam.setField("YD_AIM_RT_GP", rVal[0]);
					}
					
					
					// 운송지시일자(TRANS_ORD_DATE)와 운송지시순번(TRANS_ORD_SEQNO)은 클리어가 되어야 함
					jrParam.setField("TRANS_ORD_DATE"  , "");
					jrParam.setField("TRANS_ORD_SEQNO" , "");
					
					//C열연취소 인경우 
	    	    	if (CConstant.DMYDR070.equals(msgId)
	    	    	||  CConstant.DMYDR073.equals(msgId)
	    	    	||  CConstant.DMYDR060.equals(msgId)) {
	    	    		jrParam.setField("CAR_NO"            , "");
	    	    		jrParam.setField("CARD_NO"           , "");
	    	    		jrParam.setField("YD_STK_BED_NO"     , "");
	    	    		jrParam.setField("YD_CAR_UPP_LOC_CD" , "");
	    	    		jrParam.setField("YD_RULE_PL_RS_GP"  , "");
	    	    	}
					
	    	    	commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStockDmYdN", logId, mthdNm, "저장품 수정");
					
				} //end for
			}
			/**********************************************************
			* 4. 
			**********************************************************/
			if (CConstant.DMYDR023.equals(msgId)){
 	    		// 레코드 생성
 		    	jrParam = commUtils.getParam(logId, mthdNm, sModifier);
 		    	
 		    	// 전문에서 공통적인 항목의 값을 추출
 	    		String sTransOrdDate  = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"   ));
		    	String sTransOrdSeqno = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));
 				String sCarNo         = commUtils.trim(rcvMsg.getFieldString("CAR_NO"         ));
 				String sCardNo        = commUtils.trim(rcvMsg.getFieldString("CARD_NO"        ));	    		
 				String sArrWlocCd     = commUtils.trim(rcvMsg.getFieldString("ARR_WLOC_CD"    ));
 				String sCurrProgCd    = commUtils.trim(rcvMsg.getFieldString("CURR_PROG_CD"   )); //현재진도코드
 				
 		    	// 레코드에 추가
 		    	jrParam.setField("TRANS_ORD_DATE" , sTransOrdDate );
 				jrParam.setField("TRANS_ORD_SEQNO", sTransOrdSeqno);				
     			jrParam.setField("ARR_WLOC_CD"    , sArrWlocCd    );
     			jrParam.setField("STL_PROG_CD"    , sCurrProgCd   );	    	
     			
     			// 운송지시날짜와 운송지시순번으로  재료번호 조회
     			
     			JDTORecordSet jsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdStockTransOrdDateSeqNo", logId, mthdNm, "저장품 조회");
     			
     			if (jsRst.size() <= 0){
     				commUtils.printLog(logId, "운송지시일자와 운송지시순번으로 재료번호 조회 실패", "SL");
     			} else {
     				
     				commUtils.printLog(logId, "운송지시일자와 운송지시순번으로 재료번호 조회 성공", "SL");
     				
     				String sStlNo = jsRst.getRecord(0).getFieldString("STL_NO");
     				// 차량번호와 카드번호 클리어
     				jrParam.setField("STL_NO"   , sStlNo);
         			jrParam.setField("CAR_NO"   , ""    );
         			jrParam.setField("CARD_NO"  , ""    );	
 	    	    	
 	    	    	//  저장품 업데이트 처리
         			/*
					UPDATE TB_YD_STOCK
					   SET MODIFIER        = :V_MODIFIER
					     , MOD_DDTT        = SYSDATE
					     , STL_APPEAR_GP   = NVL(:V_STL_APPEAR_GP, STL_APPEAR_GP)
					     , TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
					     , TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
					     , CAR_NO          = :V_CAR_NO
					     , CARD_NO         = :V_CARD_NO
					     , ARR_WLOC_CD     = NVL(:V_ARR_WLOC_CD, ARR_WLOC_CD)
					     , STL_PROG_CD     = NVL(:V_STL_PROG_CD, STL_PROG_CD)
					 WHERE STL_NO = :V_STL_NO
         			 */
         			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStockDMYDCncl", logId, mthdNm, "저장품 수정");	
     			}
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
	 * 출하차량스케줄/차량Point삭제 기능 - 상차지시 취소 시 호출
	 * @param recPara
	 * @param szCaller
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord delCarSchNCarPointForDist(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "출하차량스케줄/차량Point삭제[CCoilL3RcvSeEJB.delCarSchNCarPointForDist] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, mthdNm, "S+");

			//수신 항목 값
			String msgId          = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sTransOrdDate  = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"   ));
	    	String sTransOrdSeqno = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));
			String sModifier      = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)

			if ("".equals(sModifier)) { sModifier = msgId; }
			
			/**********************************************************
			* 1. 차량스케줄 조회
			**********************************************************/
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			
			jrParam.setField("TRANS_ORD_DATE"   , sTransOrdDate);
			jrParam.setField("TRANS_ORD_SEQNO"  , sTransOrdSeqno);
			/*
			SELECT *
			  FROM TB_YD_CARSCH
			 WHERE TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
			   AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
			   AND DEL_YN = 'N'
			 */
			JDTORecordSet jsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdCarschByTransDTSeq", logId, mthdNm, "차량스케줄 조회");
			if (jsRst.size() <= 0) {
				commUtils.printLog(logId, "운송지시일자 :["+sTransOrdDate+"] , 운송지시순번["+sTransOrdSeqno+"]로 차량스케줄 조회 시 오류발생", "S-");
				return jrRtn;
			}
			
			String ydCarSchId     = commUtils.trim(jsRst.getRecord(0).getFieldString("YD_CAR_SCH_ID"    ));
			String ydCarProgStat  = commUtils.trim(jsRst.getRecord(0).getFieldString("YD_CAR_PROG_STAT" ));
			String ydCarldStopLoc = commUtils.trim(jsRst.getRecord(0).getFieldString("YD_CARLD_STOP_LOC"));
			String sCarNo         = commUtils.trim(jsRst.getRecord(0).getFieldString("CAR_NO"           ));
			String sCardNo        = commUtils.trim(jsRst.getRecord(0).getFieldString("CARD_NO"          ));
			
			/**********************************************************
			* 2. 조회된 차량스케줄로 차량이송재료/차량스케줄 삭제
			**********************************************************/
			jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_CAR_SCH_ID"     , ydCarSchId);
			jrParam.setField("DEL_YN"            , "Y"       );
			
			/*
			UPDATE TB_YD_CARFTMVMTL
			   SET MODIFIER  = :V_MODIFIER
			     , MOD_DDTT  = SYSDATE
			     , DEL_YN    = :V_DEL_YN
			 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID     
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.delYdCarftmvmtl", logId, mthdNm, "이송재료 삭제");
			
			/*
			UPDATE TB_YD_CARSCH
			   SET MODIFIER  = :V_MODIFIER
			     , MOD_DDTT  = SYSDATE
			     , DEL_YN    = :V_DEL_YN
			 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID  
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.delYdCarsch", logId, mthdNm, "차량스케줄 삭제");
			
			/**********************************************************
			* 3. 차량진행상태에 따른 차량정지위치 Clear 실행 - 상차도착 시에만 Clear
			**********************************************************/
			if (!"".equals(ydCarldStopLoc)) {
				jrParam = commUtils.getParam(logId, mthdNm, sModifier);
				
				jrParam.setField("YD_STK_COL_GP", ydCarldStopLoc);
				/*
				SELECT *
				  FROM TB_YD_STKCOL
				 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
				   AND DEL_YN ='N'
				 */
				jsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdStkColByPk", logId, mthdNm, "차량정지위치 조회");
				if (jsRst.size() <= 0) {
					commUtils.printLog(logId, "차량정지위치["+ydCarldStopLoc+"]조회 오류발생", "S-");
					return jrRtn;
				}
				
				String ydStkColCarNo  = commUtils.trim(jsRst.getRecord(0).getFieldString("CAR_NO" )); 
				String ydStkColCardNo = commUtils.trim(jsRst.getRecord(0).getFieldString("CARD_NO")); 
				
				if (ydStkColCarNo.equals(sCarNo) && ydStkColCardNo.equals(sCardNo)) {
					
					commUtils.printLog(logId, "차량정지위치 비활성화", "SL");
					jrParam.setField("YD_STK_COL_GP"      , ydCarldStopLoc);
					jrParam.setField("YD_CAR_USE_GP"      , "G"           ); //출하차량
					jrParam.setField("YD_STK_COL_ACT_STAT", "C"           ); //비활성화

					this.procCarPosActiveOrInActive(jrParam);
					
					//--------------------------------------------------------------------------------
					//	차량포인트통합관리 Clear 실행 - 상차도착 시에만 Clear
					//--------------------------------------------------------------------------------
		    		//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태) 
					//저장위치로 초기화 하는 경우(출하)
					/* 
					UPDATE TB_YD_CARPOINT
					   SET CARD_NO = NULL
					     , CAR_NO  = NULL
					     , YD_STK_COL_ACT_STAT=DECODE(TRN_EQP_CD,NULL,:V_STAT,YD_STK_COL_ACT_STAT)
					     , MOD_DDTT = SYSDATE
					     , MODIFIER = :V_MODIFIER
					 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP    
					   AND DEL_YN = 'N'
					*/ 
					jrParam.setField("STAT"  			, "C"); 
					jrParam.setField("YD_STK_COL_GP"	, ydCarldStopLoc);
			    	
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.carpointstackcolgpupdateC", logId, mthdNm, "TB_YD_CARPOINT 수정");
					
				} else {
					commUtils.printLog(logId, "차량스케줄의 차량번호["+sCarNo+"]와 카드번호["+sCardNo+"]와 적치열의 차량번호["+ydStkColCarNo+"]와", "SL");
					commUtils.printLog(logId, "카드번호["+ydStkColCardNo+"]가 동일하지 않으므로 차량정지위치["+ydCarldStopLoc+"]를 비활성화하지 않음", "SL");
				}
				
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
	 * 차량정지위치활성/비활성처리
	 * @param recPara
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procCarPosActiveOrInActive(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "차량정지위치활성/비활성처리[CCoilL3RcvSeEJB.procCarPosActiveOrInActive] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, mthdNm, "S+");

			//수신 항목 값
			String ydStkColGp      = commUtils.nvl(rcvMsg.getFieldString("YD_STK_COL_GP"      ), "");
			String ydCarUseGp      = commUtils.nvl(rcvMsg.getFieldString("YD_CAR_USE_GP"      ), "");
			String sTrnEqpCd       = commUtils.nvl(rcvMsg.getFieldString("TRN_EQP_CD"         ), "");
			String sCarNo          = commUtils.nvl(rcvMsg.getFieldString("CAR_NO"             ), "");
			String sCardNo         = commUtils.nvl(rcvMsg.getFieldString("CARD_NO"            ), "");
			String sTrnEqpStkCapa  = commUtils.nvl(rcvMsg.getFieldString("TRN_EQP_STK_CAPA"   ), "");
			String ydStkColActStat = commUtils.nvl(rcvMsg.getFieldString("YD_STK_COL_ACT_STAT"), "");
			
			String sModifier        = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)

			/**********************************************************
			* 0. 항목 값 Check
			**********************************************************/
			if ("".equals(ydStkColGp)) {
				commUtils.printLog(logId, "적치열이 존재하지 않습니다.", "S-");
				return jrRtn;
			}
			
			if ("L".equals(ydCarUseGp)) { //구내운송
				if ("L".equals(ydStkColActStat) && "".equals(sTrnEqpCd)) {
					commUtils.printLog(logId, "구내운송은 운송장비코드가 존재해야합니다.", "S-");
					return jrRtn;
				}
			}
			
			if ("G".equals(ydCarUseGp)) { //출하
				if ("L".equals(ydStkColActStat)) {
					if ("".equals(sCarNo) || "".equals(sCardNo)) {
						commUtils.printLog(logId, "출하차량은 차량번호, 카드번호가 존재해야합니다.", "S-");
						return jrRtn;
					}
				}
			}
			
			String ydStkBedActStat = "";
			String ydStkLyrActStat = "";
			
			if ("L".equals(ydStkColActStat)) { //적치가능
				ydStkBedActStat = "L"; //적치가능
				ydStkLyrActStat = "E"; //적치가능
			} else if ("C".equals(ydStkColActStat)) { //비활성화
				ydStkBedActStat = "C"; //비활성화
				ydStkLyrActStat = "C"; //비활성화
				sTrnEqpStkCapa  = CConstant.YD_STK_BED_WT_MAX_DEFAULT; //베드의 기본 야드적치Bed중량Max 300000
				ydCarUseGp      = "";
			} else {
				commUtils.printLog(logId, "["+ydStkColActStat + "]사용할 수 없는 상태값입니다.", "S-");
				return jrRtn;
			}
			
			/**********************************************************
			* 1.적치열 활성/비활성 처리
			**********************************************************/			
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_STK_COL_GP"      , ydStkColGp     );
	    	jrParam.setField("YD_CAR_USE_GP"      , ydCarUseGp     );
	    	jrParam.setField("TRN_EQP_CD"         , sTrnEqpCd      );
	    	jrParam.setField("CAR_NO"             , sCarNo         );
	    	jrParam.setField("CARD_NO"            , sCardNo        );
	    	jrParam.setField("YD_STK_COL_ACT_STAT", ydStkColActStat);
	    	
	    	/*
			UPDATE TB_YD_STKCOL
			   SET MODIFIER            = :V_MODIFIER
			     , MOD_DDTT            = SYSDATE
			     , YD_CAR_USE_GP       = :V_YD_CAR_USE_GP
			     , TRN_EQP_CD          = :V_TRN_EQP_CD
			     , CAR_NO              = :V_CAR_NO
			     , CARD_NO             = :V_CARD_NO
			     , YD_STK_COL_ACT_STAT = :V_YD_STK_COL_ACT_STAT
			 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
	    	 */
	    	commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStkcolActYn", logId, mthdNm, "적치열 수정");
			
	    	/*
			UPDATE TB_YD_CARPOINT
			   SET MODIFIER            = :V_MODIFIER
			     , MOD_DDTT            = SYSDATE
			     , CAR_NO              = :V_CAR_NO
			     , CARD_NO             = :V_TRN_EQP_CD
			     , YD_STK_COL_ACT_STAT = :V_YD_STK_COL_ACT_STAT     
			 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
	    	 */
	    	commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdCarpointActYn", logId, mthdNm, "차량포인트 수정");
	    	
	    	/**********************************************************
			* 2.적치베드 활성/비활성 처리
			**********************************************************/
	    	jrParam.setField("YD_STK_BED_WT_MAX"  , sTrnEqpStkCapa );
			jrParam.setField("YD_STK_BED_ACT_STAT", ydStkBedActStat);
			
			/*
			UPDATE TB_YD_STKBED
			   SET MODIFIER            = :V_MODIFIER
			     , MOD_DDTT            = SYSDATE
			     , YD_STK_BED_ACT_STAT = :V_YD_STK_BED_ACT_STAT
			     , YD_STK_BED_WT_MAX   = :V_YD_STK_BED_WT_MAX
			 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStkbedYdStkColGp", logId, mthdNm, "적치베드 수정");
			
	    	/**********************************************************
			* 3.적치단 활성/비활성 처리
			**********************************************************/
			jrParam.setField("YD_STK_LYR_ACT_STAT", ydStkLyrActStat);
			jrParam.setField("STL_NO"             , "");
			jrParam.setField("YD_STK_LYR_MTL_STAT", "E");
	    	
			/*
			UPDATE TB_YD_STKLYR   
			   SET MODIFIER            = :V_MODIFIER
			     , MOD_DDTT            = SYSDATE
			     , YD_STK_LYR_ACT_STAT = :V_YD_STK_LYR_ACT_STAT
			     , YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
			     , STL_NO              = :V_STL_NO
			 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStklyrYdStkColGp", logId, mthdNm, "적치단 수정");
	    	
		
			commUtils.printLog(logId, mthdNm, "S-");
			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}	
	
	
	/**
	 * 코일제품목전 DMYDR014
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvDMYDR014(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "코일제품목전 수신[CCoilL3RcvSeEJB.rcvDMYDR014] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			//수신 항목 값
			String msgId    	     = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String stlAppearGp       = commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"     )); // 재료외형구분   
			String sStlNo            = commUtils.trim(rcvMsg.getFieldString("STL_NO"            )); // 재료번호     
			String sCurrProgCd       = commUtils.trim(rcvMsg.getFieldString("CURR_PROG_CD"      )); // 현재진도코드    
			String sOrdYeojaeGp      = commUtils.trim(rcvMsg.getFieldString("ORD_YEOJAE_GP"     )); // 주문여재구분    
			String sOrdNo            = commUtils.trim(rcvMsg.getFieldString("ORD_NO"            )); // 주문번호      
			String sOrdDtl           = commUtils.trim(rcvMsg.getFieldString("ORD_DTL"           )); // 주문행번      
			String sOrdGp            = commUtils.trim(rcvMsg.getFieldString("ORD_GP"            )); // 수주구분      
			String sCustCd           = commUtils.trim(rcvMsg.getFieldString("CUST_CD"           )); // 고객코드      
			String sDestCd           = commUtils.trim(rcvMsg.getFieldString("DEST_CD"           )); // 목적지코드     
			String sDlvrddRuleDd     = commUtils.trim(rcvMsg.getFieldString("DLVRDD_RULE_DD"    )); // 납기기준일     
			String sDestTelNo        = commUtils.trim(rcvMsg.getFieldString("DEST_TEL_NO"       )); // 목적지전화번호   
			String sDistShipassignGp = commUtils.trim(rcvMsg.getFieldString("DIST_SHIPASSIGN_GP")); // 출하배선지시구분 
			String sCancelYn         = commUtils.trim(rcvMsg.getFieldString("CANCEL_YN"         )); // Y: 취소 , N: 지시

			String sModifier 	     = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }

			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			
			String[] rVal      = new String[1]; //목표행선 조회용
			
			if ("Y".equals(sCancelYn)) {
			    jrRtn = this.procDmTcCncl(rcvMsg);
			    return jrRtn;
			}
			/**********************************************************
			* 0. 수신 항목 값 Check
			**********************************************************/
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("STL_APPEAR_GP"     , stlAppearGp      );
			jrParam.setField("STL_NO"            , sStlNo           );
			jrParam.setField("STL_PROG_CD"       , sCurrProgCd      );
			jrParam.setField("ORD_YEOJAE_GP"     , sOrdYeojaeGp     );
			jrParam.setField("ORD_NO"            , sOrdNo           );
			jrParam.setField("ORD_DTL"           , sOrdDtl          );
			jrParam.setField("ORD_GP"            , sOrdGp           );
			jrParam.setField("CUST_CD"           , sCustCd          );
			jrParam.setField("DEST_CD"           , sDestCd          );
			jrParam.setField("YD_DLVRDD_RULE_DD" , sDlvrddRuleDd    );
			jrParam.setField("DEST_TEL_NO"       , sDestTelNo       );
			jrParam.setField("DIST_SHIPASSIGN_GP", sDistShipassignGp);
			
			/**********************************************************
			* 1. 저장품 수정
			**********************************************************/
			//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
			rVal = coilDao.getYdAimRtGp("C", jrParam);
			
			jrParam.setField("YD_AIM_RT_GP"	    , rVal[0]);	
			
			/*
			UPDATE TB_YD_STOCK
			   SET MODIFIER           = :V_MODIFIER
			     , MOD_DDTT           = SYSDATE
			     , STL_APPEAR_GP      = :V_STL_APPEAR_GP
			     , STL_PROG_CD        = :V_STL_PROG_CD     
			     , ORD_YEOJAE_GP      = :V_ORD_YEOJAE_GP     
			     , ORD_NO             = :V_ORD_NO            
			     , ORD_DTL            = :V_ORD_DTL           
			     , ORD_GP             = :V_ORD_GP            
			     , CUST_CD            = :V_CUST_CD           
			     , DEST_CD            = :V_DEST_CD           
			     , YD_DLVRDD_RULE_DD  = :V_YD_DLVRDD_RULE_DD
			     , DEST_TEL_NO        = :V_DEST_TEL_NO       
			     , DIST_SHIPASSIGN_GP = :V_DIST_SHIPASSIGN_GP
			     , YD_AIM_RT_GP       = :V_YD_AIM_RT_GP
			 WHERE STL_NO = :V_STL_NO
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStockDMYDR014", logId, mthdNm, "TB_YD_STOCK 수정");

			/**********************************************************
			* 9. 저장품 제원 야드L2 전송 (YDY5L002)
			**********************************************************/
			JDTORecord jDrd = JDTORecordFactory.getInstance().create();
			jDrd.setField("JMS_TC_CD"			, "YDY5L002");
			jDrd.setField("YD_INFO_SYNC_CD"		, "5");	//1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)
			jDrd.setField("STL_NO"		        , sStlNo);
			
			jrRtn = commUtils.addSndData(jrRtn ,coilDao.getMsgL2("YDY5L002", jDrd));
			
			commUtils.printLog(logId, mthdNm, "S-");
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 * 코일제품보관지시 - DMYDR027
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvDMYDR027(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "코일제품보관지시 수신[CCoilL3RcvSeEJB.rcvDMYDR027] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
 
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			String msgId       = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String stlAppearGp       = commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"     )); // 재료외형구분   
			String sStlNo            = commUtils.trim(rcvMsg.getFieldString("STL_NO"            )); // 재료번호     
			String sCurrProgCd       = commUtils.trim(rcvMsg.getFieldString("CURR_PROG_CD"      )); // 현재진도코드    
			String sOrdYeojaeGp      = commUtils.trim(rcvMsg.getFieldString("ORD_YEOJAE_GP"     )); // 주문여재구분    
			String sOrdNo            = commUtils.trim(rcvMsg.getFieldString("ORD_NO"            )); // 주문번호      
			String sOrdDtl           = commUtils.trim(rcvMsg.getFieldString("ORD_DTL"           )); // 주문행번      
			String sOrdGp            = commUtils.trim(rcvMsg.getFieldString("ORD_GP"            )); // 수주구분      
			String sCustCd           = commUtils.trim(rcvMsg.getFieldString("CUST_CD"           )); // 고객코드      
			String sDestCd           = commUtils.trim(rcvMsg.getFieldString("DEST_CD"           )); // 목적지코드     
			String sDlvrddRuleDd     = commUtils.trim(rcvMsg.getFieldString("DLVRDD_RULE_DD"    )); // 납기기준일     
			String sDestTelNo        = commUtils.trim(rcvMsg.getFieldString("DEST_TEL_NO"       )); // 목적지전화번호   
			String sDistShipassignGp = commUtils.trim(rcvMsg.getFieldString("DIST_SHIPASSIGN_GP")); // 출하배선지시구분 
			String sCancelYn         = commUtils.trim(rcvMsg.getFieldString("CANCEL_YN"         )); // Y: 취소 , N: 지시

			String sModifier 	     = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }

			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			
			String[] rVal      = new String[1]; //목표행선 조회용

			if ("Y".equals(sCancelYn)) {
			    jrRtn = this.procDmTcCncl(rcvMsg);
			    return jrRtn;
			}
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("STL_APPEAR_GP"     , stlAppearGp      );
			jrParam.setField("STL_NO"            , sStlNo           );
			jrParam.setField("STL_PROG_CD"       , sCurrProgCd      );
			jrParam.setField("ORD_YEOJAE_GP"     , sOrdYeojaeGp     );
			jrParam.setField("ORD_NO"            , sOrdNo           );
			jrParam.setField("ORD_DTL"           , sOrdDtl          );
			jrParam.setField("ORD_GP"            , sOrdGp           );
			jrParam.setField("CUST_CD"           , sCustCd          );
			jrParam.setField("DEST_CD"           , sDestCd          );
			jrParam.setField("YD_DLVRDD_RULE_DD" , sDlvrddRuleDd    );
			jrParam.setField("DEST_TEL_NO"       , sDestTelNo       );
			jrParam.setField("DIST_SHIPASSIGN_GP", sDistShipassignGp);

			//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
			rVal = coilDao.getYdAimRtGp("C", jrParam);
			jrParam.setField("YD_AIM_RT_GP"	     , rVal[0]);
			
			jrParam.setField("SCARFING_YN"       ,"Y"     );
			/*
			UPDATE TB_YD_STOCK
			   SET MODIFIER           = :V_MODIFIER
			     , MOD_DDTT           = SYSDATE
			     , STL_APPEAR_GP      = :V_STL_APPEAR_GP
			     , STL_PROG_CD        = :V_STL_PROG_CD     
			     , ORD_YEOJAE_GP      = :V_ORD_YEOJAE_GP     
			     , ORD_NO             = :V_ORD_NO            
			     , ORD_DTL            = :V_ORD_DTL           
			     , ORD_GP             = :V_ORD_GP            
			     , CUST_CD            = :V_CUST_CD           
			     , DEST_CD            = :V_DEST_CD           
			     , YD_DLVRDD_RULE_DD  = :V_YD_DLVRDD_RULE_DD
			     , DEST_TEL_NO        = :V_DEST_TEL_NO       
			     , DIST_SHIPASSIGN_GP = :V_DIST_SHIPASSIGN_GP
			     , YD_AIM_RT_GP       = :V_YD_AIM_RT_GP
			     , SCARFING_YN        = :V_SCARFING_YN
			 WHERE STL_NO = :V_STL_NO
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStockDMYDR027", logId, mthdNm, "TB_YD_STOCK 수정");
			
			/**********************************************************
			* 9. 저장품 제원 야드L2 전송 (YDY5L002)
			**********************************************************/
			JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
			sndL2Msg.setField("JMS_TC_CD"			, "YDY5L002");
			sndL2Msg.setField("YD_INFO_SYNC_CD"		, "5");	//1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)
			sndL2Msg.setField("STL_NO"		        , sStlNo);
			
			jrRtn = commUtils.addSndData(jrRtn ,coilDao.getMsgL2("YDY5L002", sndL2Msg));
			 
			commUtils.printLog(logId, mthdNm, "S-");
			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 * 코일제품출하완료 DMYDR030
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvDMYDR030(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "코일제품출하완료 수신[CCoilL3RcvSeEJB.rcvDMYDR030] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			String msgId       	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydGp        	= commUtils.trim(rcvMsg.getFieldString("YD_GP"        )); 
			String stlAppearGp	= commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"));
			String sStlNo       = commUtils.trim(rcvMsg.getFieldString("STL_NO"       ));
//			String sCurrProgCd  = commUtils.trim(rcvMsg.getFieldString("CURR_PROG_CD" ));
//			String sCarNo    	= commUtils.trim(rcvMsg.getFieldString("CAR_NO"       ));
			String sBackUpYn    = commUtils.trim(rcvMsg.getFieldString("BACKUP_YN"    ));
			String sCancelChk   = commUtils.trim(rcvMsg.getFieldString("CANCEL_YN"    )); // Y: 취소 , N: 지시
			 
			String sModifier    = commUtils.trim(rcvMsg.getFieldString("MODIFIER"     ));//수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }
			
			String sMsg = "";
			
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			
			String[] rVal      = new String[1]; //목표행선 조회용
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/  			
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("STL_NO"	    , sStlNo     ); //재료번호  

			/*
			SELECT A.*
			     , NVL(B.YD_STK_COL_GP,C.YD_STK_COL_GP) AS YD_STK_COL_GP
			     , B.YD_STK_BED_NO                AS YD_STK_BED_NO
			     , B.YD_STK_LYR_NO                AS YD_STK_LYR_NO
			  FROM TB_YD_STOCK  A
			     , TB_YD_STKLYR B
			     , TB_YD_STKCOL C
			 WHERE A.STL_NO  = :V_STL_NO
			   AND A.STL_NO  = B.STL_NO(+)
			   AND A.CARD_NO = C.CARD_NO(+) 
			 */                                                     				
			JDTORecordSet jsStock = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdStockJoinStkLyr2_PIDEV", logId, mthdNm, "차량정정보검색");
			
			if (jsStock.size() <= 0) {
				commUtils.printLog(logId, "YD_STOCK[코일제품출하완료] 조회 Error", "SL");	
				return jrRtn ;
			}

			String sCarNo    	  = commUtils.trim(jsStock.getRecord(0).getFieldString("CAR_NO"));
			String ydStkColGp 	  = commUtils.trim(jsStock.getRecord(0).getFieldString("YD_STK_COL_GP"));
			String sCardNo        = commUtils.trim(jsStock.getRecord(0).getFieldString("CARD_NO"));
			String sTransOrdDate  = commUtils.trim(jsStock.getRecord(0).getFieldString("TRANS_ORD_DATE"));
			String sTransOrdSeqNo = commUtils.trim(jsStock.getRecord(0).getFieldString("TRANS_ORD_SEQNO"));
			String ydCarUppLocCd  = commUtils.nvl (jsStock.getRecord(0).getFieldString("YD_CAR_UPP_LOC_CD"), "01");
			
			jrParam.setField("STL_APPEAR_GP", 	stlAppearGp);
			jrParam.setField("DEL_YN"         , "Y");
			jrParam.setField("STL_PROG_CD"    , "M");
			jrParam.setField("YD_AIM_RT_GP"   , "M2");
			
			//저장품 수정
			/*
			UPDATE TB_YD_STOCK
			   SET MODIFIER      = :V_MODIFIER
			     , MOD_DDTT      = SYSDATE
			     , STL_APPEAR_GP = :V_STL_APPEAR_GP
			     , YD_AIM_RT_GP  = :V_YD_AIM_RT_GP
			     , DEL_YN        = :V_DEL_YN     
			     , STL_PROG_CD   = :V_STL_PROG_CD
			 WHERE STL_NO = :V_STL_NO 
			*/
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStockCarldCmpl", logId, mthdNm, "저장품 수정");
			
			
			/*
			SELECT *
			  FROM(SELECT * 
			         FROM(SELECT *  
			                FROM TB_YD_CARSCH 
			               WHERE DEL_YN     = 'N'
			                 AND 'L'        = :V_YD_CAR_USE_GP  
			                 AND TRN_EQP_CD = :V_TRN_EQP_CD
			               UNION ALL
			              SELECT A.*
			                FROM TB_YD_CARSCH A
			                   , TB_YD_STOCK B
			               WHERE A.TRANS_ORD_DATE  = B.TRANS_ORD_DATE
			                 AND A.TRANS_ORD_SEQNO = B.TRANS_ORD_SEQNO
			                 AND A.DEL_YN = 'N'
			                 AND 'G'      = :V_YD_CAR_USE_GP  
			                 AND B.STL_NO = :V_STL_NO 
			               ) A
			          ORDER BY YD_CAR_SCH_ID DESC
			      ) B
			 WHERE ROWNUM <= 1
			 */
			jrParam.setField("YD_CAR_USE_GP", "G");
			JDTORecordSet jsCarschInfo = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCarschByStlNoCarID", logId, mthdNm, "차량스케줄Id조회");
			String ydCarSchId = jsCarschInfo.size() > 0 ? commUtils.trim(jsCarschInfo.getRecord(0).getFieldString("YD_CAR_SCH_ID")) : "";				
			
			//***************************************************************************
			//  저장품이 적치된 저장위치 정보를 조회
			//***************************************************************************
			sMsg = "[" + mthdNm + "]카드번호["+sCardNo+"], 차량번호["+sCardNo+"], 운송지시일자["+sTransOrdDate
			      +"], 운송지시순번["+sTransOrdSeqNo+"] : 출하완료된 동["+ydStkColGp+"]의 저장품들 조회 시작";
			
			if ("*".equals(stlAppearGp)) {

				commUtils.printLog(logId, "[" + mthdNm + "] 마지막 상차완료 전문", "SL");
				
				//PIDEV
//				String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI0", "J", "*");
				
				jrParam.setField("CAR_NO" 			, sCarNo       );
				
//				if("N".equals(sApplyYnPI)) {
//					jrParam.setField("CARD_NO"			, sCardNo      );
//				}
				
				jrParam.setField("TRANS_ORD_DATE"	, sTransOrdDate );
				jrParam.setField("TRANS_ORD_SEQNO"	, sTransOrdSeqNo);
				
				/*
				SELECT *
				  FROM (
				        SELECT *
				          FROM TB_YD_CARSCH
				         WHERE CAR_NO       LIKE :V_CAR_NO||'%'
				           AND CARD_NO         = :V_CARD_NO
				           AND TRANS_ORD_DATE  = :V_TRANS_ORD_DT
				           AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
				         ORDER BY YD_CAR_SCH_ID DESC
				       ) A
				 WHERE ROWNUM <= 1
		    	*/                                                     				
				JDTORecordSet jsCarsch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCarschTransDTSeq2_PIDEV", logId, mthdNm, "차량정정보검색");
				 
				if (jsCarsch.size() > 0 ) {
//					sMsg = "차량스케쥴 조회 SELECT Error :: DO NOT EXIST"  ;
//					commUtils.printLog(logId, sMsg, "SL");	
//					return jrRtn ;
					
					/*
					YD_GP	야드구분
					TRANS_ORD_DT	운송지시일자
					TRANS_ORD_SEQNO	운송지시순번
					CAR_NO			차량번호
					CARD_NO			카드번호
					SPOS_WLOC_CD    발지개소코드
					SPOS_YD_PNT_CD  발지야드포인트코드
					 */
					
					JDTORecord sndL3Msg = commUtils.getParam(logId, mthdNm, sModifier);
					sndL3Msg.setField("TC_CODE"			, "DMYDR040");	//전문코드
					sndL3Msg.setField("SPOS_WLOC_CD"	, commUtils.trim(jsCarsch.getRecord(0).getFieldString("SPOS_WLOC_CD")));
					sndL3Msg.setField("SPOS_YD_PNT_CD"	, commUtils.trim(jsCarsch.getRecord(0).getFieldString("YD_PNT_CD1"  )));
					sndL3Msg.setField("YD_GP"			, ydGp          );
					sndL3Msg.setField("TRANS_ORD_DT"    , commUtils.trim(jsCarsch.getRecord(0).getFieldString("TRANS_ORD_DATE" )));
					sndL3Msg.setField("TRANS_ORD_SEQNO" , commUtils.trim(jsCarsch.getRecord(0).getFieldString("TRANS_ORD_SEQNO")));
					if ("".equals(sCardNo)) {
						sCardNo = "XXXXX";
					}					
					sndL3Msg.setField("CAR_NO" 			, sCarNo       );
					sndL3Msg.setField("CARD_NO"			, sCardNo      );
					sndL3Msg.setField("MSG_ID"			, msgId        );
//					
					//전송 Data 생성
					sMsg = "차량번호[" + sCarNo + "]는 코일제품출하차량출발실적호출";
					commUtils.printLog(logId, sMsg, "SL");
					
					EJBConnector ejbConn = new EJBConnector("default", "CCoilCarMvSeEJB", this);
					JDTORecord jrRtn1 = (JDTORecord)ejbConn.trx("procOutCarLevWr", new Class[] { JDTORecord.class }, new Object[] { sndL3Msg });
					jrRtn = commUtils.addSndData(jrRtn, jrRtn1);
				}	
			} else {
				commUtils.printLog(logId, "마지막 상차완료 전문이 아님", "SL");
			}
			
			/**********************************************************
			* 99. 저장품 제원 야드L2 전송 (YDY5L002)
			**********************************************************/
			JDTORecord sndL2Msg = commUtils.getParam(logId, mthdNm, sModifier);
			sndL2Msg.setField("JMS_TC_CD"			, "YDY5L002");
			sndL2Msg.setField("YD_INFO_SYNC_CD"		, "5");	//1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)
			sndL2Msg.setField("MSG_GP"			    , "D"       ); //전문구분
			sndL2Msg.setField("STL_NO"		        , sStlNo);
			
			jrRtn = commUtils.addSndData(jrRtn ,coilDao.getMsgL2("YDY5L002", sndL2Msg));
			
			/*******************************************
			 * layer정보가 존재 하는 경우 (백업으로 취소 처리 시)
			 *******************************************/
			if ("Y".equals(sBackUpYn)) {
				jrParam.setField("COIL_NO", sStlNo);
				JDTORecordSet jsCoilcomm = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getPtCoilComm", logId, mthdNm, "코일공통 조회");
				
				if (jsCoilcomm.size() <= 0) {
					throw new Exception("TB_PT_COILCOMM 존재하지 않는 코일번호:" + sStlNo);
				}
				
				String ydEqpGp  = commUtils.trim(jsCoilcomm.getRecord(0).getFieldString("YD_EQP_GP"));
				
				if ("PT".equals(ydEqpGp)) {
					commUtils.printLog(logId, "정상처리된 코일입니다.", "S-");
					return jrRtn;
				}
				
				/*
				SELECT YD_STK_COL_GP
				     , YD_STK_BED_NO
				     , YD_STK_LYR_NO
				     , STL_NO
				     , YD_STK_COL_GP||YD_STK_BED_NO||YD_STK_LYR_NO AS YD_STK_LOC
				     , SUBSTR(YD_STK_COL_GP, 1, 2) AS YD_GP_BAY
				  FROM TB_YD_STKLYR
				 WHERE STL_NO = :V_STL_NO
				   AND SUBSTR(YD_STK_COL_GP,3,2) NOT IN ('TR','PT','TT') --//차량위치가 아닌경우
				   AND YD_STK_COL_GP LIKE 'J%'
				 */
				JDTORecordSet loadStacklayer = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdStklyrByStlNo", logId, mthdNm, "단정보검색");
				ydStkColGp        = "JX0101";
				String ydGpBay    = "";
				String ydStkBedNo = "01";
				String ydStkLyrNo = "001";
				
				if (loadStacklayer.size() > 0) {
					 
					ydStkColGp = commUtils.trim(loadStacklayer.getRecord(0).getFieldString("YD_STK_COL_GP"));
					ydStkBedNo = commUtils.trim(loadStacklayer.getRecord(0).getFieldString("YD_STK_BED_NO"));
					ydStkLyrNo = commUtils.trim(loadStacklayer.getRecord(0).getFieldString("YD_STK_LYR_NO"));
					/*
					UPDATE TB_YD_STKLYR
					   SET MODIFIER      = :V_MODIFIER
					     , MOD_DDTT      = SYSDATE
					     , YD_STK_LYR_MTL_STAT = 'E'
					     , YD_STK_LYR_ACT_STAT = 'E'
					     , STL_NO        = ''
					 WHERE STL_NO = :V_STL_NO
					 */
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStklyrInitC", logId, mthdNm, "적치단 수정");
					
					commUtils.printLog(logId, "[코일제품출하완료]["+sStlNo+"]에 저장위치맵을 비웁니다.+"+ydGpBay, "SL");
				}
				
				/***********************************
				 * Coil 공통 Table 저장위치 Update 
				 ***********************************/	 
				ydGpBay = ydStkColGp.substring(0, 2);

				//코일 공통 상차백업 위치로 위치 변경 작업 
				jrParam.setField("STL_NO"	, sStlNo       ); //재료번호
				jrParam.setField("YD_LOC"	, ydGpBay + "PT01" + ydCarUppLocCd + "001"); //현재위치
				
				/*
				UPDATE TB_PT_COILCOMM
				   SET (  
				         YD_GP                 -- 야드구분
				       , YD_BAY_GP             -- 동
				       , YD_EQP_GP             -- SPAN
				       , YD_STK_COL_NO         -- 적치열번지
				       , YD_STK_BED_NO         -- 적치번지
				       , YD_STK_LYR_NO         -- 적치단
				       , YD_STR_LOC            -- 현 저장위치코드
				       , YD_STR_LOC_HIS1       -- 전 저장위치코드
				       , YD_STR_LOC_HIS2       -- 전전 저장위치코드
				       ) =
				       (
				        SELECT 
				               SUBSTR(P_YD_LOC,1,1) AS YD_GP         -- 야드구분
				             , SUBSTR(P_YD_LOC,2,1) AS YD_BAY_GP     -- 동
				             , SUBSTR(P_YD_LOC,3,2) AS YD_EQP_GP     -- SPAN
				             , SUBSTR(P_YD_LOC,5,2) AS YD_STK_COL_NO -- 적치열번지
				             , SUBSTR(P_YD_LOC,7,2) AS YD_STK_BED_NO -- 적치번지
				             , SUBSTR(P_YD_LOC,9,3) AS YD_STK_LYR_NO -- 적치단
				             , SUBSTR(P_YD_LOC,1,8)||SUBSTR(P_YD_LOC,10,2) AS YD_STR_LOC         -- 현 저장위치코드   
				             , YD_STR_LOC      AS YD_STR_LOC_HIS1    -- 전현 저장위치코드
				             , YD_STR_LOC_HIS1 AS YD_STR_LOC_HIS2    -- 전전현 저장위치코드
				          FROM TB_PT_COILCOMM
				             ,(SELECT :V_YD_LOC AS P_YD_LOC FROM DUAL) 
				         WHERE COIL_NO = :V_STL_NO
				     )
				 WHERE COIL_NO = :V_STL_NO
				 */
				commDao.updateTx(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updateCoilCommonLocInfo", logId, mthdNm, "TB_PT_COILCOMM 저장위치 수정");
				
				/*************************
				 * 작업실적에 저장
				 *************************/
				JDTORecord jparam = commUtils.getParam(logId, mthdNm, sModifier);
				
				jparam.setField("STL_NO"           , sStlNo);
				jparam.setField("YD_EQP_ID"        , ydGpBay+"CR"+ "00");
				jparam.setField("YD_SCH_CD"        , "JX9999");
				jparam.setField("YD_WRK_DUTY"      , CCommUtils.getWorkDuty());  
				jparam.setField("YD_WRK_PARTY"     , CCommUtils.getWorkParty()); 
				jparam.setField("YD_UP_WR_LOC"     , ydStkColGp + ydStkBedNo);
				jparam.setField("YD_UP_WR_LAYER"   , ydStkLyrNo        );
				jparam.setField("YD_DN_WR_LOC"     , ydGpBay + "PT0101");
				jparam.setField("YD_DN_WR_LAYER"   , "001"             );
				
				/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.insYdWrkhistBackUp 
				INSERT INTO TB_YD_WRKHIST
				(
				  YD_WRK_HIST_ID
				, REGISTER
				, REG_DDTT
				, MODIFIER
				, MOD_DDTT
				, DEL_YN
				, YD_GP
				, STL_NO
				, YD_CRN_SCH_ID
				, YD_SCH_CD
				, YD_UP_WR_LOC
				, YD_UP_WR_LAYER
				, YD_DN_WR_LOC
				, YD_DN_WR_LAYER
				, YD_WRK_DUTY
				, YD_WRK_PARTY
				, YD_EQP_ID
				, YD_WRK_HDS_DD
				, YD_DN_CMPL_DT
				) VALUES (
				  TO_CHAR(SYSDATE, 'YYYYMMDDHH24MI')||LPAD(YD_WRKHIST_SEQ.nextval,6,'0'))
				, :V_MODIFIER
				, SYSDATE
				, :V_MODIFIER
				, SYSDATE
				, 'N'
				, 'J'
				, :V_STL_NO
				, '000000000000000000'--YD_CRN_SCH_ID
				, :V_YD_SCH_CD
				, :V_YD_UP_WR_LOC
				, :V_YD_UP_WR_LAYER
				, :V_YD_DN_WR_LOC
				, :V_YD_DN_WR_LAYER
				, :V_YD_WRK_DUTY
				, :V_YD_WRK_PARTY
				, :V_YD_EQP_ID
				, TO_CHAR(SYSDATE, 'YYYYMMDD')
				, SYSDATE
				)
				*/
				commDao.insert(jparam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.insYdWrkhistBackUp", logId, mthdNm, "작업실적 저장");
				
				/*************************
				 * 이송재료 정보 등록 - 백업처리대상이 반품될 경우 대비
				 *************************/
				jrParam = commUtils.getParam(logId, mthdNm, sModifier);
				jrParam.setField("YD_CAR_SCH_ID", ydCarSchId   );
				jrParam.setField("STL_NO"       , sStlNo       );
				jrParam.setField("YD_STK_BED_NO", ydCarUppLocCd); //야드 차상위치코드
				jrParam.setField("YD_STK_LYR_NO", "001"        );
				jrParam.setField("DEL_YN"       , "Y"          );
				/*
				INSERT INTO TB_YD_CARFTMVMTL(
				       YD_CAR_SCH_ID
				     , STL_NO
				     , REGISTER
				     , REG_DDTT
				     , MODIFIER
				     , MOD_DDTT
				     , DEL_YN
				     , YD_CAR_UPP_LOC_CD
				     , YD_STK_BED_NO
				     , YD_STK_LYR_NO
				     , HCR_GP
				     , STL_PROG_CD
				     , YD_MTL_ITEM
				     , YD_ROUTE_GP
				) VALUES ( 
				       :V_YD_CAR_SCH_ID
				     , :V_STL_NO
				     , :V_MODIFIER
				     , SYSDATE
				     , :V_MODIFIER
				     , SYSDATE
				     , 'N'
				     , :V_YD_CAR_UPP_LOC_CD
				     , :V_YD_STK_BED_NO
				     , :V_YD_STK_LYR_NO
				     , :V_HCR_GP
				     , :V_STL_PROG_CD
				     , :V_YD_MTL_ITEM
				     , :V_YD_ROUTE_GP     
				)
				 */
				// 백업처리 할 경우 차량스케줄ID가 삭제되어 오류발생. 반품경우 이전 코일 정보 가져오지 않으므로 삭제
				//commDao.insert(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.insCarSchmtl", logId, mthdNm, "차량재료 스케쥴 INSERT");					 
	    		
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
	 * 코일제품반품 DMYDR033
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvDMYDR033(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "코일제품반품 수신[CCoilL3RcvSeEJB.rcvDMYDR033] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			//수신 항목 값
			String msgId    		 = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sStlAppearGp 	 = commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"       )); //재료외형
			String sStlNo            = commUtils.trim(rcvMsg.getFieldString("STL_NO"              )); //재료번호
			String sCurrProgCd       = commUtils.trim(rcvMsg.getFieldString("CURR_PROG_CD"        )); // 현재진도코드
			String sOrdYeojaeGp      = commUtils.trim(rcvMsg.getFieldString("ORD_YEOJAE_GP"       )); // 주문여재구분    
			String sOrdNo            = commUtils.trim(rcvMsg.getFieldString("ORD_NO"              )); // 주문번호      
			String sOrdDtl           = commUtils.trim(rcvMsg.getFieldString("ORD_DTL"             )); // 주문행번      
			String sOrdGp            = commUtils.trim(rcvMsg.getFieldString("ORD_GP"              )); // 수주구분      
			String sCustCd           = commUtils.trim(rcvMsg.getFieldString("CUST_CD"             )); // 고객코드      
			String sDestCd           = commUtils.trim(rcvMsg.getFieldString("DEST_CD"             )); // 목적지코드
			String sDestTelNo        = commUtils.trim(rcvMsg.getFieldString("DEST_TEL_NO"         )); // 목적지전화번호
			String sDistShipassignGp = commUtils.trim(rcvMsg.getFieldString("DIST_SHIPASSIGN_GP"  )); // 출하배선지시구분
			String sDistGoodsGp  	 = commUtils.trim(rcvMsg.getFieldString("DIST_GOODS_GP"       )); //출하제품구분 H:코일, T:HRPLATE
			String sOldTransOrdDt  	 = commUtils.trim(rcvMsg.getFieldString("OLD_TRANS_WORD_DATE" )); //구운송지시일자
			String sOldTransOrdSeqNo = commUtils.trim(rcvMsg.getFieldString("OLD_TRANS_WORD_SEQNO"));
			String sNewTransOrdDt  	 = commUtils.trim(rcvMsg.getFieldString("NEW_TRANS_WORD_DATE" )); //신운송지시일자
			String sNewTransOrdSeqNo = commUtils.trim(rcvMsg.getFieldString("NEW_TRANS_WORD_SEQNO"));
			
			String sModifier 		 = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }
			
			String sMsg = "";
			
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			
			String[] rVal      = new String[1]; //목표행선 조회용
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/  			
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			
			jrParam.setField("STL_APPEAR_GP"     , sStlAppearGp 	); 
			jrParam.setField("STL_NO"            , sStlNo           ); 
			jrParam.setField("STL_PROG_CD"       , sCurrProgCd      ); 
			jrParam.setField("ORD_YEOJAE_GP"     , sOrdYeojaeGp     ); 
			jrParam.setField("ORD_NO"            , sOrdNo           ); 
			jrParam.setField("ORD_DTL"           , sOrdDtl          ); 
			jrParam.setField("ORD_GP"            , sOrdGp           ); 
			jrParam.setField("CUST_CD"           , sCustCd          ); 
			jrParam.setField("DEST_CD"           , sDestCd          ); 
			jrParam.setField("DEST_TEL_NO"       , sDestTelNo       ); 
			jrParam.setField("DIST_SHIPASSIGN_GP", sDistShipassignGp); 
			jrParam.setField("DEL_YN"            ,  "N"             );
			
			rVal= coilDao.getYdAimRtGp("C",jrParam );		
			jrParam.setField("YD_AIM_RT_GP"      , rVal[0]);

			// 저장품 갱신
			/*
			UPDATE TB_YD_STOCK
			   SET MODIFIER           = :V_MODIFIER
			     , MOD_DDTT           = SYSDATE
			     , STL_APPEAR_GP      = :V_STL_APPEAR_GP
			     , STL_PROG_CD        = :V_STL_PROG_CD     
			     , ORD_YEOJAE_GP      = :V_ORD_YEOJAE_GP     
			     , ORD_NO             = :V_ORD_NO            
			     , ORD_DTL            = :V_ORD_DTL           
			     , ORD_GP             = :V_ORD_GP            
			     , CUST_CD            = :V_CUST_CD           
			     , DEST_CD            = :V_DEST_CD           
			     , DEST_TEL_NO        = :V_DEST_TEL_NO       
			     , DIST_SHIPASSIGN_GP = :V_DIST_SHIPASSIGN_GP
			     , YD_AIM_RT_GP       = :V_YD_AIM_RT_GP
			 WHERE STL_NO = :V_STL_NO
			*/
			int intRtnVal = commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStockDMYDR033", logId, mthdNm, "TB_YD_STOCK 수정");
			if (intRtnVal <= 0){
				throw new Exception("YD_STOCK[코일제품반품] UPDATE Error :: [" + intRtnVal + "]");
			}
			
			jrParam.setField("TRANS_ORD_DATE"  , sOldTransOrdDt);
			jrParam.setField("TRANS_ORD_SEQNO" , sOldTransOrdSeqNo);
			
			//=====================================================================================================
			// 1.저장품 운송지시 변경
			// 2.차량스케줄 운송지시 변경
			// 3.검수운송지시 변경 및 재검수 상태로 변경
			//=====================================================================================================			
			/*
			SELECT *
			  FROM TB_YD_STOCK A
			 WHERE TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
			   AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
			   AND DEL_YN = 'N'
				 */                                                     				
			JDTORecordSet jsStock = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdStockTRANS_ORD_DAT", logId, mthdNm, "(운송일자, 운송순번)로 저장품 조회");

		    if (jsStock.size() > 0) {
		    	commUtils.printLog(logId, mthdNm+ "[코일제품반품(DMYDR033)] 이전 운송지시번호로 변경 대상이 존재 함", "SL");
		    	
				// 레코드생성
				jrParam.setField("OLD_TRANS_WORD_DATE"			, sOldTransOrdDt   );
				jrParam.setField("OLD_TRANS_WORD_SEQNO"			, sOldTransOrdSeqNo);
				jrParam.setField("NEW_TRANS_WORD_DATE"			, sNewTransOrdDt   );
				jrParam.setField("NEW_TRANS_WORD_SEQNO"			, sNewTransOrdSeqNo);
				//--------------------------------------------------------------------------------
				//	차량스케줄 운송지시 변경
				//--------------------------------------------------------------------------------
				/* 
				UPDATE TB_YD_CARSCH
				   SET MODIFIER = :V_MODIFIER
				     , MOD_DDTT = SYSDATE
				     , TRANS_ORD_DATE  = :V_NEW_TRANS_WORD_DATE
				     , TRANS_ORD_SEQNO = :V_NEW_TRANS_WORD_SEQNO
				 WHERE TRANS_ORD_DATE  = :V_OLD_TRANS_WORD_DATE
				   AND TRANS_ORD_SEQNO = :V_OLD_TRANS_WORD_SEQNO
				*/   
				
				intRtnVal = commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdCarschTransOrd", logId, mthdNm, "TB_YD_CARSCH 차량스케줄 운송지시 변경");
				
				//--------------------------------------------------------------------------------
				//	검수재료 운송지시 변경
				//--------------------------------------------------------------------------------
				/*
				UPDATE TB_YD_EXAMINATIONCHKLIST
				   SET TRANS_ORD_DATE = :V_NEW_TRANS_WORD_DATE
				     , TRANS_ORD_SEQNO = :V_NEW_TRANS_WORD_SEQNO
				     , sModifier  = :V_MODIFIER
				     , MOD_DDTT = sysdate
				     , DEL_YN = 'N'
				     , CHECKING_YN = 'N'
				     , LABEL_YN = NULL
				     , YD_AB_CD = NULL
				 WHERE TRANS_ORD_DATE  = :V_OLD_TRANS_WORD_DATE
				   AND TRANS_ORD_SEQNO = :V_OLD_TRANS_WORD_SEQNO
				*/   
				intRtnVal = commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdExamTransOrd", logId, mthdNm, "TB_YD_EXAMINATIONCHKLIST 검수재료 운송지시 변경");

				//--------------------------------------------------------------------------------
				//	재료정보 운송지시 변경
				//--------------------------------------------------------------------------------
				/*
				UPDATE TB_YD_STOCK
				   SET TRANS_ORD_DATE  = :V_NEW_TRANS_WORD_DATE
				     , TRANS_ORD_SEQNO = :V_NEW_TRANS_WORD_SEQNO
				     , MODIFIER = :V_MODIFIER
				     , MOD_DDTT = SYSDATE
				 WHERE TRANS_ORD_DATE  = :V_OLD_TRANS_WORD_DATE
				   AND TRANS_ORD_SEQNO = :V_OLD_TRANS_WORD_SEQNO 
				*/
				intRtnVal = commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStockTransOrd", logId, mthdNm, "TB_YD_STOCK 재료정보 운송지시 변경");
			}
			//---------------------------------------------------------------------------- 
		
			//차량스케줄ID 조회--------------------------------------------------------------
		    /* 
		    SELECT YD_CAR_SCH_ID  AS YD_CAR_SCH_ID
		      FROM TB_YD_CARSCH
		     WHERE TRANS_ORD_DATE  = :V_NEW_TRANS_WORD_DATE
		       AND TRANS_ORD_SEQNO = :V_NEW_TRANS_WORD_SEQNO
		       AND DEL_YN='N'
		    */
			String ydCarSchId = "";
			JDTORecordSet jsCarsch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdCarschByTransDTSeq", logId, mthdNm, "(운송일자, 운송순번)로 TB_YD_CARSCH 조회");			
			if (jsCarsch.size() <= 0 ) {
				
				sMsg = "["+mthdNm+"] 운송지시일자 :["+sNewTransOrdDt+"] , 운송지시순번["+sNewTransOrdSeqNo+"]로 차량스케줄 조회 시 오류발생 - 메세지 : " + jsCarsch.size();
				commUtils.printLog(logId, sMsg, "SL");	
				return jrRtn ;
			} else {
				
				ydCarSchId = commUtils.trim(jsCarsch.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
			}			
			//--------------------------------------------------------------------------------
			
			
			//1.차량스케줄 재료 삭제--------------------------------------------------------------
			jrParam.setField("YD_CAR_SCH_ID", 	ydCarSchId);
			/* 
			DELETE FROM USRYDA.TB_YD_CARFTMVMTL
			 WHERE YD_CAR_SCH_ID=:V_YD_CAR_SCH_ID
			   AND STL_NO=:V_STL_NO
			*/   
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.delYdCarftmvmtlDMYDR033", logId, mthdNm, "TB_YD_CARFTMVMTL 삭제");
			
			//2.검수재료 삭제--------------------------------------------------------------------
			jrParam.setField("TRANS_ORD_DATE", 			sNewTransOrdDt);
			jrParam.setField("TRANS_ORD_SEQNO", 		sNewTransOrdSeqNo);
 			
			/* 
			DELETE FROM TB_YD_EXAMINATIONCHKLIST
			 WHERE TRANS_ORD_DATE=:V_TRANS_ORD_DATE
			   AND TRANS_ORD_SEQNO=:V_TRANS_ORD_SEQNO
			   AND STL_NO =:V_STL_NO
			*/
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.delYdExaminationmtl", logId, mthdNm, "TB_YD_EXAMINATIONCHKLIST 삭제");
				
			//3.저장품재료 --------------------------------------------------------------------
			
			jrParam.setField("TRANS_ORD_DATE"	, "");
			jrParam.setField("TRANS_ORD_SEQNO"	, "");
			jrParam.setField("CAR_NO"           , "");
			jrParam.setField("CARD_NO"          , "");
			jrParam.setField("YD_STK_BED_NO"    , "");
			jrParam.setField("YD_CAR_UPP_LOC_CD", "");
			jrParam.setField("STL_NO"           , sStlNo);
			
			/*  
			UPDATE TB_YD_STOCK
			   SET MODIFIER          = :V_MODIFIER
			     , MOD_DDTT          = SYSDATE
			     , TRANS_ORD_DATE    = :V_TRANS_ORD_DATE
			     , TRANS_ORD_SEQNO   = :V_TRANS_ORD_SEQNO
			     , CAR_NO            = :V_CAR_NO
			     , CARD_NO           = :V_CARD_NO
			     , YD_STK_BED_NO     = :V_YD_STK_BED_NO
			     , YD_CAR_UPP_LOC_CD = :V_YD_CAR_UPP_LOC_CD
			 WHERE STL_NO = :V_STL_NO   
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStockDMYDR033E", logId, mthdNm, "저장품 수정");

			//======================================================
			// 저장품제원 : 코일야드L2 로 송신(YDY5L002)
			//======================================================
			JDTORecord sndL2Msg = commUtils.getParam(logId, mthdNm, sModifier);
			sndL2Msg.setField("YD_INFO_SYNC_CD"	, "5"       ); //야드정보동기화코드
			sndL2Msg.setField("MSG_GP"			, "I"       ); //전문구분
			sndL2Msg.setField("STL_NO"       	, sStlNo ); //재료번호
				
			jrRtn = commUtils.addSndData(jrRtn,coilDao.getMsgL2("YDY5L002", sndL2Msg));	 //전송 Data 생성	
			
			
			commUtils.printLog(logId, mthdNm, "S-");
			return jrRtn;
			 
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	

	/**
	 * 제품운송상차지시(DMYDR060)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvDMYDR060(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "제품운송상차지시 수신[CCoilL3RcvSeEJB.rcvDMYDR060] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
		
			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			
			String sStlAppearGp   = commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP")); //재료외형 구분
			String sCmbnCarldYn   = commUtils.trim(rcvMsg.getFieldString("CMBN_CARLD_YN")); //조합상차유무
			String sTransOrdDt    = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT")); //운송지시일자
			String sTransOrdSeqno = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO")); //운송지시순번
			String sCarNo         = commUtils.trim(rcvMsg.getFieldString("CAR_NO")); //차량번호
			String sCardNo        = commUtils.trim(rcvMsg.getFieldString("CARD_NO")); //카드번호
			String sLotNo         = commUtils.trim(rcvMsg.getFieldString("LOT_NO")); //LOT번호
			String sCarKind       = commUtils.trim(rcvMsg.getFieldString("CAR_KIND")); //차량종류
			String sCancelYn      = commUtils.trim(rcvMsg.getFieldString("CANCEL_YN"         )); // Y: 취소 , N: 지시
			int iYdEqpWrkSh       = Integer.parseInt(commUtils.nvl(rcvMsg.getFieldString("YD_EQP_WRK_SH"),"0")); //야드설비작업매수
			
			String sModifier          = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }
			
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			
			String[] rVal = new String[1];
			
			String ydGp           = "";
			String sStlNo         = "";
			String sGdsCarldLoc   = "";
			String sStockMoveTerm = "";
			String ydAimRtGp      = "";
			String sStlProgCd     = "";
			
			/***********************************
			 * 취소
			 ***********************************/
			if ("Y".equals(sCancelYn)) {
			    jrRtn = this.procDmTcCncl(rcvMsg);
			    return jrRtn;
			}
			
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_RULE_PL_RS_GP" , sCmbnCarldYn);
			jrParam.setField("STL_APPEAR_GP"    , sStlAppearGp);
			jrParam.setField("TRANS_ORD_DATE"   , sTransOrdDt);
			jrParam.setField("TRANS_ORD_SEQNO"  , sTransOrdSeqno); 
 			jrParam.setField("CAR_NO"           , sCarNo);
			jrParam.setField("CARD_NO"          , sCardNo);
			jrParam.setField("CAR_LOTID"        , sLotNo);
			jrParam.setField("TC_CD"            , msgId);	//TC_CODE
			
			//수신된 전문의 STL_NO의 수 만큼 Loop
			for (int ii = 1 ; ii <= iYdEqpWrkSh; ii++) {
				ydGp         = commUtils.trim(rcvMsg.getFieldString("YD_GP"+ii)); 
				sStlNo 		 = commUtils.trim(rcvMsg.getFieldString("STL_NO"+ii)); //재료번호
				sGdsCarldLoc = commUtils.trim(rcvMsg.getFieldString("GDS_CARLD_LOC"+ii)); //상차위치
				
				jrParam.setField("STL_NO"           , sStlNo      );
				jrParam.setField("YD_CAR_UPP_LOC_CD", sGdsCarldLoc);
				
				//차상위치 세팅
				if ("S".equals(sCmbnCarldYn) && "TR".equals(sCarKind) && "".equals(sGdsCarldLoc)) {
					sGdsCarldLoc = "0" + ii;
					jrParam.setField("YD_CAR_UPP_LOC_CD", sGdsCarldLoc);
				}
				
				//재료가 없으면 종료
				if ("".equals(sStlNo)) {
					break;
				}
				
				if ("0".equals(ydGp)||"1".equals(ydGp)||"2".equals(ydGp)||"3".equals(ydGp)) {
					//0:A열연 SLAB야드,1:A열연 COIL야드,2:B열연 SLAB야드,3:B열연 COIL야드
					
					//저장품 이동 조건 
					jrParam.setField("STOCK_ID" , sStlNo); //저장품 ID
					sStockMoveTerm = commUtils.trim(coilDao.getCoilCurrProgCd(jrParam).getFieldString("STOCK_MOVE_TERM"));
					
					if ("1".equals(ydGp)) {
						jrParam.setField("STOCK_MOVE_TERM"  , sStockMoveTerm);
						jrParam.setField("YD_RULE_PL_RS_GP" , sCmbnCarldYn  );
						jrParam.setField("TRANS_ORD_DATE"   , sTransOrdDt   );
						jrParam.setField("TRANS_ORD_SEQNO"  , sTransOrdSeqno);
						jrParam.setField("YD_CAR_UPP_LOC_CD", sGdsCarldLoc  );
						jrParam.setField("CAR_NO"			, sCarNo        );
						jrParam.setField("CAR_CARD_NO"		, sCardNo       );
						jrParam.setField("STL_NO"           , sStlNo        ); //저장품 ID
						/*
						UPDATE USRYFA.TB_YF_STOCK
						SET
							MODIFIER            = :V_MODIFIER,
							MOD_DDTT            = SYSDATE,
							DEL_YN              = 'N',
							STOCK_MOVE_TERM     = NVL(:V_STOCK_MOVE_TERM ,  STOCK_MOVE_TERM),   --저장품이동조건
							YD_RULE_PL_RS_GP    = NVL(:V_YD_RULE_PL_RS_GP,  YD_RULE_PL_RS_GP),  --조합구분
							TRANS_ORD_DATE      = NVL(:V_TRANS_ORD_DATE,    TRANS_ORD_DATE),    --운송지시
							TRANS_ORD_SEQNO     = NVL(:V_TRANS_ORD_SEQNO,   TRANS_ORD_SEQNO),   --운송지시행번
							YD_CAR_UPP_LOC_CD   = CASE
												  WHEN :V_MODIFIER = 'DMYDR060' AND :V_YD_CAR_UPP_LOC_CD IS NULL THEN NULL
												  ELSE NVL(:V_YD_CAR_UPP_LOC_CD, YD_CAR_UPP_LOC_CD) END,  --차상위치
							CAR_NO              = NVL(:V_CAR_NO,            CAR_NO),            --차량번호
							CAR_CARD_NO         = NVL(:V_CAR_CARD_NO,       CAR_CARD_NO)        --카드번호
						WHERE 1=1
						AND STL_NO              = :V_STL_NO
						 */
						commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYfStock", logId, mthdNm, "TB_YF_STOCK 수정");
					} else {
						//TB_YM_STOCK 수정
						jrParam.setField("STOCK_MOVE_TERM"	, sStockMoveTerm);
						jrParam.setField("TRANS_ORD_DATE2"	, sTransOrdDt   );
						jrParam.setField("TRANS_ORD_SEQNO2"	, sTransOrdSeqno);
						jrParam.setField("SHEAR_SUPPLY_SEQ"	, sGdsCarldLoc  );
						jrParam.setField("CAR_NO2"			, sCarNo        );
						jrParam.setField("CAR_CARD_NO"		, sCardNo       );
						jrParam.setField("SHEAR_SUPPLY_GP"	, sCarKind      );
						jrParam.setField("STOCK_ID"			, sStlNo        );
						/* 
						UPDATE USRYMA.TB_YM_STOCK
						   SET MODIFIER         = :V_MODIFIER 
						     , MOD_DDTT         = SYSDATE
						     , DEL_YN               = 'N'
						     , STOCK_MOVE_TERM  = NVL(:V_STOCK_MOVE_TERM , STOCK_MOVE_TERM)   --저장품이동조건
						     , YD_RULE_PL_RS_GP = NVL(:V_YD_RULE_PL_RS_GP, YD_RULE_PL_RS_GP)  --조합구분
						     , TRANS_WORD_NO    = :V_TRANS_ORD_DATE2 || :V_TRANS_ORD_SEQNO2
						     , TRANS_ORD_DATE2  = NVL(:V_TRANS_ORD_DATE2 , TRANS_ORD_DATE2)   --운송지시
						     , TRANS_ORD_SEQNO2 = NVL(:V_TRANS_ORD_SEQNO2, TRANS_ORD_SEQNO2)  --운송지시행번
						
						     , SHEAR_SUPPLY_SEQ = CASE WHEN :V_MODIFIER = 'DMYDR060' AND :V_SHEAR_SUPPLY_SEQ IS NULL THEN NULL
						                               ELSE NVL(:V_SHEAR_SUPPLY_SEQ, SHEAR_SUPPLY_SEQ) END --차상위치
						     , CAR_NO2          = NVL(:V_CAR_NO2         , CAR_NO2)           --차량번호 
						     , CAR_CARD_NO      = NVL(:V_CAR_CARD_NO     , CAR_CARD_NO)       --카드번호
						     , SHEAR_SUPPLY_GP  = NVL(:V_SHEAR_SUPPLY_GP , SHEAR_SUPPLY_GP)   --차량종류
						     , WBOOK_ID         = NVL(:V_WBOOK_ID        , WBOOK_ID)          --작업예약 사용안함
						     , CR_FRTOMOVE_GP   = NVL(:V_CR_FRTOMOVE_GP  , CR_FRTOMOVE_GP)    -- 냉연이송구분       
						 WHERE STOCK_ID = :V_STOCK_ID
						*/   
						commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYmStock", logId, mthdNm, "TB_YM_STOCK 수정");
					}

				} else {

					jrParam.setField("STL_NO" , sStlNo); //저장품 ID
					
					//작업예약만 존재 하는 경우 강제 삭제처리
					/*
					UPDATE TB_YD_WRKBOOK C
					   SET DEL_YN   = 'Y'
					     , MODIFIER = :V_MODIFIER
					     , MOD_DDTT = SYSDATE 
					 WHERE C.YD_WBOOK_ID IN(
					                        SELECT A.YD_WBOOK_ID 
					                          FROM TB_YD_WRKBOOKMTL A 
					                         WHERE A.DEL_YN = 'N'
					                          AND NOT EXISTS(SELECT 1 FROM TB_YD_CRNSCH B
					                                          WHERE B.YD_WBOOK_ID = A.YD_WBOOK_ID
					                                            AND B.DEL_YN = 'N'
					                                        )
					                          AND A.STL_NO = :V_STL_NO
					                        )
					 */
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updWbookCancel", logId, mthdNm, "작업예약 삭제");
					
					/*
					UPDATE TB_YD_WRKBOOKMTL C
					   SET DEL_YN   = 'Y'
					     , MODIFIER = :V_MODIFIER
					     , MOD_DDTT = SYSDATE 
					 WHERE C.YD_WBOOK_ID IN(
					                        SELECT A.YD_WBOOK_ID 
					                          FROM TB_YD_WRKBOOKMTL A 
					                         WHERE A.DEL_YN = 'N'
					                          AND NOT EXISTS(SELECT 1 FROM TB_YD_CRNSCH B
					                                          WHERE B.YD_WBOOK_ID = A.YD_WBOOK_ID
					                                            AND B.DEL_YN = 'N'
					                                        )
					                          AND A.STL_NO = :V_STL_NO
					                       )
					 */
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updWbookMtlCancel", logId, mthdNm, "작업예약제료 삭제");
					
					
					rVal= coilDao.getYdAimRtGp("C",jrParam );	
					
					ydAimRtGp  = rVal[0]; //야드목표행선
					sStlProgCd = rVal[1]; //
					
					//TB_YD_STOCK 수정
					jrParam.setField("YD_AIM_RT_GP"		, ydAimRtGp);
					jrParam.setField("STL_PROG_CD"		, sStlProgCd);
					jrParam.setField("YD_STK_BED_NO"	, sCarKind);
					
					//차상위치
					if ("".equals(sGdsCarldLoc)) {
						sGdsCarldLoc = "0" + ii;
						jrParam.setField("YD_CAR_UPP_LOC_CD", sGdsCarldLoc);
					}

					/*
					UPDATE TB_YD_STOCK
					   SET MOD_DDTT           = SYSDATE
					     , MODIFIER           = :V_MODIFIER
					     , YD_AIM_RT_GP       = NVL(:V_YD_AIM_RT_GP,YD_AIM_RT_GP)
					     , YD_RULE_PL_RS_GP   = :V_YD_RULE_PL_RS_GP
					     , TRANS_ORD_DATE     = :V_TRANS_ORD_DATE
					     , TRANS_ORD_SEQNO    = :V_TRANS_ORD_SEQNO
					     , YD_CAR_UPP_LOC_CD  = :V_YD_CAR_UPP_LOC_CD
					     , CAR_NO             = :V_CAR_NO
					     , CARD_NO            = :V_CARD_NO
					     , CAR_LOTID          = :V_CAR_LOTID
					     , CAR_LOTID_REG_DDTT = NVL(CAR_LOTID_REG_DDTT,(CASE WHEN :V_CAR_LOTID<>'' THEN SYSDATE ELSE NULL END))
					     , YD_STK_BED_NO      = NVL(:V_YD_STK_BED_NO, 'TR')  
					     , CR_FRTOMOVE_GP     = NULL
					     , DEL_YN             = 'N'
					 WHERE STL_NO = :V_STL_NO 
					*/
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStockDMYDR060", logId, mthdNm, "TB_YD_STOCK 수정");
				}
				
			} // end of for loop
			
			//야드핸들링정보 송신////////////////////////////////////////////////////////////////////////////////
			//코일야드 인 경우 우선 적용
			if ("1".equals(ydGp)||"3".equals(ydGp)||"H".equals(ydGp)||"J".equals(ydGp) ) { 
				//1:A열연 COIL야드,3:B열연 COIL야드,H:C열연 COIL소재야드,J:C열연 COIL제품야드
				
				//출하제품핸들링횟수 구하기
				jrParam.setField("TRANS_ORD_DATE"	, sTransOrdDt);
				jrParam.setField("TRANS_ORD_SEQNO"	, sTransOrdSeqno);
				/*
				--//출하제품핸들링횟수
				WITH TEMP_TABLE AS (
				SELECT STL_NO
				     , TRANS_ORD_SEQNO
				  FROM TB_YD_STOCK A
				 WHERE A.TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
				   AND A.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
				)
				, TEMP_TABLE2 AS (
				SELECT SUBSTR(YD_CARPNT_CD,1,1) AS YD_GP 
				     , YD_CARPNT_CD             AS CARLD_PNT_CD
				     , COUNT(STL_NO)            AS HANDLING_CNT  
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
				                     , (SELECT STL_NO_LEFT FROM VW_YD_STKLYERSEARCH B WHERE A.STL_NO = B.STL_NO) AS STL_NO_LEFT
				                     , TRANS_ORD_SEQNO
				                  FROM TEMP_TABLE A
				                 UNION 
				                SELECT 'B'
				                     , (SELECT STL_NO_RIGHT FROM VW_YD_STKLYERSEARCH B WHERE A.STL_NO = B.STL_NO) AS STL_NO_RIGHT
				                     , TRANS_ORD_SEQNO
				                  FROM TEMP_TABLE A 
				               ) A
				             , TB_PT_COILCOMM B
				             , TB_YD_CARPOINT C
				         WHERE A.STL_NO    = B.COIL_NO
				           AND B.YD_GP     = C.YD_GP
				           AND B.YD_BAY_GP = C.YD_BAY_GP
				           AND (CASE --WHEN B.YD_EQP_GP = '70' AND B.YD_GP = 'J' AND B.YD_BAY_GP IN('B','C') THEN '01'
				                     WHEN B.YD_EQP_GP = '80' AND B.YD_GP = 'J' AND B.YD_BAY_GP IN('B','C') THEN '46'
				                     ELSE B.YD_EQP_GP END) BETWEEN C.YD_SPAN_FROM AND C.YD_SPAN_TO
				           AND C.DEL_YN = 'N'
				           AND C.YD_STK_COL_GP2 LIKE 'J%' --제품야드
				         GROUP BY SUBSTR(C.YD_CARPNT_CD,1,3)
				                , A.STL_NO
				                , TRANS_ORD_SEQNO
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
				   AND NOT EXISTS(SELECT 1 FROM TEMP_TABLE2)
				 GROUP BY B.YD_GP
				  */
				JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getHandlingCnt", logId, mthdNm, "출하Handling 갯수 구하기");
				
				/*******************************
				 * 야드핸들링정보 송신 YDDMR050
				 *******************************/
				for(int ii = 0; ii < rsResult.size() ; ii++) {
					
					jrParam.setField("JMS_TC_CD"			, "YDDMR050");
					jrParam.setField("YD_GP"				, commUtils.trim(rsResult.getRecord(ii).getFieldString("YD_GP")) );
					jrParam.setField("TRANS_ORD_DT"			, sTransOrdDt);
					jrParam.setField("TRANS_ORD_SEQNO"		, sTransOrdSeqno);
					jrParam.setField("CMBN_CARLD_YN"		, sCmbnCarldYn );
					jrParam.setField("CARLD_PNT_CD"			, commUtils.trim(rsResult.getRecord(ii).getFieldString("CARLD_PNT_CD")) );
					jrParam.setField("CAR_NO"				, sCarNo );
					jrParam.setField("HANDLING_CNT"			, commUtils.trim(rsResult.getRecord(ii).getFieldString("HANDLING_CNT")) ); 
					jrParam.setField("YD_STK_BED_WHIO_STAT"	, "" );
					

					//PIDEV
//					String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "CCoilL3RcvSeEJBBean => 핸들링횟수정보(열연코일)", "APPPI0", "J", "*");
					
//					if("Y".equals(sApplyYnPI)) {
						//전송 Data 생성
						jrRtn = commUtils.addSndData(jrRtn,coilDao.getMsgL3("M10YDLMJ1051", jrParam));						
//					} else {
//						//전송 Data 생성
//						jrRtn = commUtils.addSndData(jrRtn,coilDao.getMsgL3("YDDMR050", jrParam));
//					}
				}
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
	 * 대기장도착실적(DMYDR061)  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvDMYDR061(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "대기장도착실적 수신[CCoilL3RcvSeEJB.rcvDMYDR061] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			//수신 항목 값
			String msgId                = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			
			String ydGp                 = commUtils.trim(rcvMsg.getFieldString("YD_GP"            )); //야드구분	
			String sCmbnCarldYn         = commUtils.nvl (rcvMsg.getFieldString("CMBN_CARLD_YN"),"N"); //조합상차유무(시작:S, 종료: E, 단일상차: N)
			String sWorkGp              = commUtils.trim(rcvMsg.getFieldString("WORK_GP"          )); //작업구분
			String sTelNo               = commUtils.trim(rcvMsg.getFieldString("TEL_NO"           )); //전화번호
			String sTransOrdDt          = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"     )); //운송지시일자
			String sTransOrdSeqno       = commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"  )); //운송지시순번
			String sCarNo               = commUtils.trim(rcvMsg.getFieldString("CAR_NO"           )); //차량번호
			String sCardNo              = commUtils.trim(rcvMsg.getFieldString("CARD_NO"          )); //카드번호
			String sCarKind             = commUtils.trim(rcvMsg.getFieldString("CAR_KIND"         )); //차량종류
			String sWaitArrDdtt         = commUtils.trim(rcvMsg.getFieldString("WAIT_ARR_DDTT"    )); //대기장도착시간
			String sWaitArrGp           = commUtils.trim(rcvMsg.getFieldString("WAIT_ARR_GP"      )); //대기장도착구분
			
			String sTransFrtomoveGp     = commUtils.trim(rcvMsg.getFieldString("TRANS_FRTOMOVE_GP")); //1 운송 2 이송
			String sDriverName          = commUtils.trim(rcvMsg.getFieldString("DRIVER_NAME"      )); //운전기사명
			
			String sModifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }
			
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			
			/***********************************
        	 * 1. 차량스케줄 중복 Check
        	 ***********************************/
			commUtils.printLog(logId, "1. 차량스케줄 중복 체크" , "SL");
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("TRANS_ORD_DT"   , sTransOrdDt   );
			jrParam.setField("TRANS_ORD_SEQNO", sTransOrdSeqno);
			jrParam.setField("CAR_NO"         , sCarNo        );
			jrParam.setField("CMBN_CARLD_YN"  , sCmbnCarldYn  );
			/*
			SELECT *
			  FROM TB_YD_CARSCH
			 WHERE TRANS_ORD_DATE  = :V_TRANS_ORD_DT
			   AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
			   AND CAR_NO          = :V_CAR_NO
			   AND CMBN_CARLD_YN   = :V_CMBN_CARLD_YN
			   AND DEL_YN   = 'N'	
			*/	   
			JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdCarYdCmbnCarldYn61", logId, mthdNm, "차량정보 존재여부 체크");
			
			if (rsResult.size() > 0) {
				
				/***********************************
	        	 * 1.1. 기존 차량스케줄 삭제
	        	 ***********************************/
				/*
				 * 도착전문에 중복 수신되면 Exception 처리함
				 */
				commUtils.printLog(logId, "TB_YD_CARSCH[차량스케줄이 편성되어 있습니다." , "SL");
				
				throw new DAOException("TB_YD_CARSCH[차량스케줄이 편성되어 있습니다");
				
				//기존 차량정보 삭제처리
//				jrParam.setField("YD_CAR_SCH_ID", commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CAR_SCH_ID")));
//				jrParam.setField("DEL_YN"       , "Y");
				/*
				UPDATE TB_YD_CARSCH
				   SET MODIFIER  = :V_MODIFIER
				     , MOD_DDTT  = SYSDATE
				     , DEL_YN    = :V_DEL_YN
				 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID   
				 */
//				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.delYdCarsch", logId, mthdNm, "TB_YD_CARSCH 기존 차량스케줄 정보 삭제처리");
			}
			
			/***********************************
        	 * 2. 도착가능 포인트 조회
        	 ***********************************/
			commUtils.printLog(logId, "2. 도착가능 포인트 조회" , "SL");
			jrParam.setField("YD_GP"          , ydGp);
			jrParam.setField("TRANS_ORD_DT"   , sTransOrdDt);
			jrParam.setField("TRANS_ORD_SEQNO", sTransOrdSeqno);
			/* com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdCarPointSelect
			WITH TEMP_TABLE AS (
			SELECT :V_YD_GP AS YD_GP
			     , :V_TRANS_ORD_DATE  AS TRANS_ORD_DATE
			     , :V_TRANS_ORD_SEQNO AS TRANS_ORD_SEQNO
			  FROM DUAL
			)
			--C열연
			SELECT *
			  FROM (
			        SELECT CC.YD_STK_COL_GP
			             , CC.YD_CARPNT_CD
			             , CC.YD_PNT_CD
			             , DECODE(CC.YD_STK_COL_ACT_STAT,'C',1,2) AS STAT_RANK
			             , (SELECT COUNT(*)
			                  FROM TB_YD_CARSCH A
			                 WHERE A.DEL_YN = 'N'
			                   AND A.YD_CARLD_STOP_LOC = CC.YD_STK_COL_GP) AS CARLD_RANK
			             , CC.WLOC_CD     
			             , SUBSTR(CC.YD_CARPNT_CD,2,1) AS PASS_GP --통로
			             , (SELECT ITEM1 FROM TB_YD_RULE
			                 WHERE REPR_CD_GP = 'APP002' 
			                   AND CD_GP = 'J' AND ITEM = '*') AS APP002_YN
			          FROM (
			                SELECT A.STL_NO
			                     , B.YD_GP
			                  FROM TB_YD_STOCK A
			                     , TEMP_TABLE B
			                 WHERE 'J' = B.YD_GP
			                   AND A.TRANS_ORD_DATE  = B.TRANS_ORD_DATE
			                   AND A.TRANS_ORD_SEQNO = B.TRANS_ORD_SEQNO
			               ) AA
			             , TB_PT_COILCOMM BB
			             , TB_YD_CARPOINT CC
			         WHERE AA.STL_NO = BB.COIL_NO
			           AND AA.YD_GP  = CC.YD_GP
			           AND BB.YD_BAY_GP = CC.YD_BAY_GP
			           AND (CASE WHEN BB.YD_BAY_GP IN('D','E','F','G','H') AND BB.YD_EQP_GP = '80' THEN '51'  --//가상스판 포함 처리
			                     WHEN BB.YD_BAY_GP IN('A','B','C')         AND BB.YD_EQP_GP = '80' THEN '51'
			                     WHEN BB.YD_BAY_GP IN('B','C')             AND BB.YD_EQP_GP = '01' THEN '02'
			                     ELSE  BB.YD_EQP_GP END)
			                     BETWEEN CC.YD_SPAN_FROM AND CC.YD_SPAN_TO
			          AND CC.YD_CAR_USETYPE_GP IN('TR','RT','RA','TO') --트레일러
			          AND CC.DEL_YN='N'
			          AND CC.YD_STK_COL_ACT_STAT <> 'N'
			        ORDER BY CARLD_RANK  --대기차량 적은 
			               , SUBSTR(CC.YD_CARPNT_CD, 2, 1) DESC  --//2통로 우선
			               , CASE WHEN PASS_GP IN('1','3') THEN SUBSTR(YD_STK_COL_GP,2,1) END 
			               , CASE WHEN PASS_GP = '2'       THEN SUBSTR(YD_STK_COL_GP,2,1) END DESC 
			       )
			 WHERE 1 = 1
			   AND ROWNUM <= 1 
			   AND 1 = CASE WHEN APP002_YN = 'N' AND PASS_GP = '3' THEN 0 --야드통합이 아닐시 소재통로 금지
			                ELSE 1 END
			   
			 */
			rsResult = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdCarPointSelect", logId, mthdNm, "도착가능 차량포인트 조회");
			if (rsResult.size() <= 0 ) {
				throw new Exception("TB_YD_CARSCH[차량도착 가능한 포인트가 없거나 해당 운송상차지시,저장위치가 없습니다.");
			} 
			
			//도착가능 포인트 조회 결과 값
			String ydStkColGp   = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_COL_GP")); //야드적치열
			String ydCarpntCd   = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CARPNT_CD" )); //차량보인트
			String ydPntCd      = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_PNT_CD"    )); //야드포인트코드
			String sSposWlocCd  = commUtils.trim(rsResult.getRecord(0).getFieldString("WLOC_CD"      )); //개소코드
			
			/***********************************
        	 * 3. 차량스케줄 생성
        	 ***********************************/
			commUtils.printLog(logId, "3. 차량스케줄 생성" , "SL");
			//차량스케줄ID 생성 				
			String ydCarSchId = coilDao.getSeqId(logId, mthdNm, "CarSch");
			
			//차량스케줄 등록
			jrParam.setField("YD_CAR_SCH_ID"		, ydCarSchId                );
			jrParam.setField("YD_EQP_WRK_STAT"		, "U"                       ); //야드설비작업상태
			jrParam.setField("YD_EQP_ID"			, CConstant.YD_DM_CAR_EQP_ID); //야드설비ID
			jrParam.setField("YD_CAR_USE_GP"		, CConstant.YD_CAR_USE_GP_DM); //차량사용구분 
			jrParam.setField("CAR_NO"				, sCarNo                    ); //차량번호
			jrParam.setField("CAR_KIND"				, sCarKind                  ); //차량종류
			jrParam.setField("SPOS_WLOC_CD"			, sSposWlocCd               ); //발지개소코드
			
			//PIDEV
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI0", "J", "*");
//			if("N".equals(sApplyYnPI)) {
//				jrParam.setField("CARD_NO"				, sCardNo                   ); //카드번호	
//			}
			
			jrParam.setField("YD_CARLD_LEV_DT"		, commUtils.getDateTime14() ); //상차출발일시
			jrParam.setField("YD_PNT_CD1"			, ydPntCd                   ); //야드포인트코드1
			jrParam.setField("YD_CARLD_STOP_LOC"	, ydStkColGp                ); //야드상차정지위치 
			jrParam.setField("TRANS_ORD_DATE"		, sTransOrdDt               ); //운송지시일자
			jrParam.setField("TRANS_ORD_SEQNO"		, sTransOrdSeqno            ); //운송지시순번 
			if ("E".equals(sCmbnCarldYn)) {
				jrParam.setField("YD_BAYIN_WO_SEQ"	, "1"); //입동지시순번 - 복수상차 마지막 1순위	
			} else {
				jrParam.setField("YD_BAYIN_WO_SEQ"	, CConstant.YD_BAYIN_WO_SEQ_DEFAULT); //입동지시순번 - 기본값으로 설정(9)
			}
			jrParam.setField("YD_CAR_PROG_STAT"		, "1");	//상차출발상태
			jrParam.setField("YD_CAR_WRK_GP"		, sWorkGp); //야드차량작업구분
			jrParam.setField("TEL_NO"				, sTelNo); //기사핸드폰번호
			jrParam.setField("CMBN_CARLD_YN"		, sCmbnCarldYn);	//첫번째 도착창고 : S 두번째 도착창고 : E
			jrParam.setField("WAIT_ARR_DDTT"		, sWaitArrDdtt);	//대기장도착시간
			jrParam.setField("WAIT_ARR_GP"			, sWaitArrGp); //대기장도착구분  - B:BACKUP , S:SMARTPHONE
			jrParam.setField("DRIVER_NAME"			, sDriverName); //운전기사명
			
			/* 
			INSERT INTO USRYDA.TB_YD_CARSCH
			(	   YD_CAR_SCH_ID
			     , REGISTER
			     , REG_DDTT
			     , sModifier
			     , MOD_DDTT
			     , DEL_YN
			     , YD_EQP_ID
			     , YD_CAR_USE_GP
			     , CAR_NO
			     , TRN_EQP_CD
			     , CAR_KIND
			     , YD_EQP_WRK_STAT
			     , SPOS_WLOC_CD
			     , ARR_WLOC_CD
			     , YD_CARLD_LEV_LOC
			     , YD_CARLD_LEV_DT
			     , YD_CARUD_LEV_DT
			     , YD_PNT_CD1
			     , YD_PNT_CD3
			     , YD_CARLD_STOP_LOC
			     , YD_CARUD_STOP_LOC
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
			     , TRANS_EQUIPMENT_TYPE
			     , DRIVER_NAME
			) VALUES (
			       :V_YD_CAR_SCH_ID
			     , :V_MODIFIER
			     , SYSDATE
			     , :V_MODIFIER
			     , SYSDATE
			     , 'N'
			     , :V_YD_EQP_ID
			     , :V_YD_CAR_USE_GP
			     , :V_CAR_NO
			     , :V_TRN_EQP_CD
			     , :V_CAR_KIND
			     , :V_YD_EQP_WRK_STAT
			     , :V_SPOS_WLOC_CD
			     , :V_ARR_WLOC_CD              --
			     , :V_YD_CARLD_LEV_LOC
			     , TO_DATE(:V_YD_CARLD_LEV_DT,'YYYYMMDDHH24MISS')
			     , TO_DATE(:V_YD_CARUD_LEV_DT,'YYYYMMDDHH24MISS')
			     , NVL(:V_YD_PNT_CD1,'0000')
			     , NVL(:V_YD_PNT_CD3,'0000')
			     , :V_YD_CARLD_STOP_LOC
			     , :V_YD_CARUD_STOP_LOC        --
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
			     , :V_TRANS_EQUIPMENT_TYPE
			     , :V_DRIVER_NAME
			)
			 */   
			// PIDEV
//			if("Y".equals(sApplyYnPI)) {
				commDao.insert(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.insYdCarsch_PIDEV", logId, mthdNm, "TB_YD_CARSCH 등록");
//			} else {
//				commDao.insert(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.insYdCarsch", logId, mthdNm, "TB_YD_CARSCH 등록");				
//			}
			
			/***********************************
        	 * 4. 복수동 마지막 도착시 상차 정보 insert
        	 ***********************************/
			commUtils.printLog(logId, "4. 복수동 마지막 도착시 재료 insert" , "SL");
			if ("E".equals(sCmbnCarldYn)) {
				/*
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
				                                       WHERE TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
				                                         AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
				                                         AND CARD_NO         = :V_CARD_NO
				                                         AND CMBN_CARLD_YN = 'S'
				                                       )
				            )                   AS STACK_BED_GP   
				         , '001'                 AS STACK_LAYER_GP
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
				           AND A.TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
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
//				commDao.insert(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updCarFtMvMtlCmbnCarldYn", logId, mthdNm, "이송재료 등록");

				//PIDEV
//				if("Y".equals(sApplyYnPI)) {
					commDao.insert(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updCarFtMvMtlCmbnCarldYn_PIDEV", logId, mthdNm, "이송재료 등록");
//				} else {
//					commDao.insert(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updCarFtMvMtlCmbnCarldYn", logId, mthdNm, "이송재료 등록");
//				}
			}
			
			/***********************************
        	 * 5. 입동지시 호출 YDYDJ553
        	 ***********************************/
			commUtils.printLog(logId, "4. 입동지시 호출" , "SL");
			if (!"".equals(ydCarpntCd)) {
				//도착가능 포인트가 있으면 입동지시 호출
				commUtils.printLog(logId, mthdNm + " 차량입동포인트["+ydCarpntCd+"], 차량스케줄ID["+ydCarSchId+"] - 차량입동지시요구 모듈을 호출 " , "SL");
			
				JDTORecord jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
				jrYdMsg.setField("JMS_TC_CD"		  , "YDYDJ553"); //차량입동지시 요구 기존 YDYDJ662
				jrYdMsg.setField("JMS_TC_CREATE_DDTT" , commUtils.getDateTime14()); //JMSTC생성일시
				jrYdMsg.setField("YD_CARPNT_CD"		  , ydCarpntCd); //입동포인트
				jrYdMsg.setField("YD_CAR_SCH_ID"	  , ydCarSchId);	//차량스케줄ID
				jrYdMsg.setField("CARD_NO"			  , sCardNo   );
				jrYdMsg.setField("CAR_NO"			  , sCarNo    );
				jrYdMsg.setField("CAR_KIND"			  , sCarKind  ); //차량종류
				jrYdMsg.setField("TRANS_FRTOMOVE_GP"  , sTransFrtomoveGp); //1 운송 2 이송
				
				String sAPP813_YN = coilDao.ApplyYn(logId, mthdNm, "APP813", "J", "*");
				if ("Y".equals(sAPP813_YN)) {
					jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
				} else {
					EJBConnector ejbConn = new EJBConnector("default", "CCoilCarMvSeEJB", this);
					JDTORecord jrRst = (JDTORecord)ejbConn.trx("rcvYDYDJ553", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
					
					jrRtn = commUtils.addSndData(jrRtn, jrRst);	
				}
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
	 * 코일이송상차대기장도착PDA(DMYDR070)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvDMYDR070(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "코일이송상차대기장도착PDA 수신[CCoilL3RcvSeEJB.rcvDMYDR070] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			//수신 항목 값
			String msgId            = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydGp             = commUtils.trim(rcvMsg.getFieldString("YD_GP"          )); //야드구분
			String sStlAppearGp 	= commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"  )); //재료외형
			String sTransOrdDt  	= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"   ));
			String sTransOrdSeqno	= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));
			String sCancelYn     	= commUtils.trim(rcvMsg.getFieldString("CANCEL_YN"      ));
			String sCarKind		    = commUtils.trim(rcvMsg.getFieldString("CAR_KIND"       ));
			String sCarNo 			= commUtils.trim(rcvMsg.getFieldString("CAR_NO"         ));
			String sCardNo          = commUtils.trim(rcvMsg.getFieldString("CARD_NO"        ));
			String sCrFrtomoveGp	= commUtils.trim(rcvMsg.getFieldString("CR_FRTOMOVE_GP" )); //11수출사내 41열연제품이송 63임가공 81열연소재이송
			String sWorkGp 			= commUtils.trim(rcvMsg.getFieldString("WORK_GP"        )); // 작업구분
			String sCarldPntCd		= commUtils.trim(rcvMsg.getFieldString("CARLD_PNT_CD"   )); // 상차포인트
			String sTelNo           = commUtils.trim(rcvMsg.getFieldString("TEL_NO"         )); // 전화번호
			String sDriverName      = commUtils.trim(rcvMsg.getFieldString("DRIVER_NAME"    )); // 운전기사명
			String sUgntBayinYn     = commUtils.trim(rcvMsg.getFieldString("UGNT_BAYIN_YN"  )); // 복수상차 마지막 차량에 대한 구분 Y: 1순위
			String sCarRemodelYn    = commUtils.trim(rcvMsg.getFieldString("CAR_REMODEL_YN" )); //차량개조여부 y,n
			
			int iYdEqpWrkSh 		= Integer.parseInt(commUtils.nvl(rcvMsg.getFieldString("YD_EQP_WRK_SH"),"0"));
			String sModifier 		= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			
			if ("".equals(sModifier)) { sModifier = msgId; }

			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			
			String[] rVal = new String[1];
			
			/***********************************
			 * 0. 취소
			 ***********************************/
			if ("Y".equals(sCancelYn)) {
			    jrRtn = this.procDmTcCncl(rcvMsg);
			    return jrRtn;
			}
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("STL_APPEAR_GP"  , sStlAppearGp  );
			jrParam.setField("TRANS_ORD_DATE" , sTransOrdDt   );
			jrParam.setField("TRANS_ORD_SEQNO", sTransOrdSeqno);
			jrParam.setField("CARD_NO"        , sCardNo       );
			jrParam.setField("CAR_NO"         , sCarNo        );
			jrParam.setField("REHEAT_SLAB_GP" , sWorkGp       );
			jrParam.setField("COIL_CAR_NO"    , sCarldPntCd   );
			jrParam.setField("CR_FRTOMOVE_GP" , sCrFrtomoveGp );
			jrParam.setField("YD_STK_BED_NO"  , "TR"          );
			
			/***********************************
        	 * 1. 차량스케줄 중복 Check
        	 ***********************************/
			commUtils.printLog(logId, "1. 차량스케줄 중복 체크" , "SL");
			jrParam.setField("CMBN_CARLD_YN"  , "N");
			jrParam.setField("TRANS_ORD_DT"   , sTransOrdDt   );
			/*
			SELECT *
			  FROM TB_YD_CARSCH
			 WHERE TRANS_ORD_DATE  = :V_TRANS_ORD_DT
			   AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
			   AND CAR_NO          = :V_CAR_NO
			   AND CMBN_CARLD_YN   = :V_CMBN_CARLD_YN
			   AND DEL_YN   = 'N'	
			*/	   
			JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdCarYdCmbnCarldYn61", logId, mthdNm, "차량정보 존재여부 체크");
			
			if (rsResult.size() > 0) {
				throw new DAOException("TB_YD_CARSCH 차량스케줄이 편성되어 있습니다");
			}
						
		   /**********************************************************
			* 2. 저장품 이동 조건 수정
			**********************************************************/
			int iCnt = 0;
			String sStlNo = "";
			for (int i = 1 ; i <= 20; i++) {
			
				sStlNo = commUtils.trim(rcvMsg.getFieldString("STL_NO"+i));
				
				if ("".equals(sStlNo)) {
					break;
				}
				
				jrParam.setField("STL_NO" 		     , sStlNo); //저장품 ID
				jrParam.setField("YD_CAR_UPP_LOC_CD" , commUtils.trim(rcvMsg.getFieldString("GDS_CARLD_LOC"+i))); //차상위치
				
				//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
				rVal = coilDao.getYdAimRtGp("C", jrParam);
				
				jrParam.setField("YD_AIM_RT_GP", rVal[0] );
				jrParam.setField("STL_PROG_CD" , rVal[1] );
				jrParam.setField("DEL_YN"      , "N"     );
				
				//작업예약만 존재 하는 경우 강제 삭제처리
				/*
				UPDATE TB_YD_WRKBOOK C
				   SET DEL_YN   = 'Y'
				     , MODIFIER = :V_MODIFIER
				     , MOD_DDTT = SYSDATE 
				 WHERE C.YD_WBOOK_ID IN(
				                        SELECT A.YD_WBOOK_ID 
				                          FROM TB_YD_WRKBOOKMTL A 
				                         WHERE A.DEL_YN = 'N'
				                          AND NOT EXISTS(SELECT 1 FROM TB_YD_CRNSCH B
				                                          WHERE B.YD_WBOOK_ID = A.YD_WBOOK_ID
				                                            AND B.DEL_YN = 'N'
				                                        )
				                          AND A.STL_NO = :V_STL_NO
				                        )
				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updWbookCancel", logId, mthdNm, "작업예약 삭제");
				
				/*
				UPDATE TB_YD_WRKBOOKMTL C
				   SET DEL_YN   = 'Y'
				     , MODIFIER = :V_MODIFIER
				     , MOD_DDTT = SYSDATE 
				 WHERE C.YD_WBOOK_ID IN(
				                        SELECT A.YD_WBOOK_ID 
				                          FROM TB_YD_WRKBOOKMTL A 
				                         WHERE A.DEL_YN = 'N'
				                          AND NOT EXISTS(SELECT 1 FROM TB_YD_CRNSCH B
				                                          WHERE B.YD_WBOOK_ID = A.YD_WBOOK_ID
				                                            AND B.DEL_YN = 'N'
				                                        )
				                          AND A.STL_NO = :V_STL_NO
				                       )
				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updWbookMtlCancel", logId, mthdNm, "작업예약제료 삭제");
							
				/*
				UPDATE TB_YD_STOCK
				   SET MODIFIER = :V_MODIFIER
				     , MOD_DDTT = SYSDATE
				     , STL_APPEAR_GP     = :V_STL_APPEAR_GP
				     , TRANS_ORD_DATE    = :V_TRANS_ORD_DATE
				     , TRANS_ORD_SEQNO   = :V_TRANS_ORD_SEQNO
				     , CARD_NO           = :V_CARD_NO
				     , CAR_NO            = :V_CAR_NO
				     , REHEAT_SLAB_GP    = :V_REHEAT_SLAB_GP
				     , COIL_CAR_NO       = :V_COIL_CAR_NO
				     , CR_FRTOMOVE_GP    = :V_CR_FRTOMOVE_GP
				     , YD_STK_BED_NO     = :V_YD_STK_BED_NO
				     , YD_CAR_UPP_LOC_CD = :V_YD_CAR_UPP_LOC_CD
				     , YD_AIM_RT_GP      = :V_YD_AIM_RT_GP
				     , STL_PROG_CD       = :V_STL_PROG_CD
				     , DEL_YN            = :V_DEL_YN
				     , YD_ABMTL_REM      = :V_MSG_CONTENTS
				     , SNDBK_REGISTER    = :V_SNDBK_REGISTER
				     , YD_MTL_WT         = NVL(:V_YD_MTL_WT,YD_MTL_WT)
				 WHERE STL_NO = :V_STL_NO
				*/
				iCnt = commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStockWaitLocArr", logId, mthdNm, "TB_YD_STOCK 수정");
				
				if (iCnt <= 0) {
					commUtils.printLog(logId, "YD_STOCK[코일이송상차대기장도착PDA] UPDATE Error", "S-");
					return jrRtn; 
				}
			}	
			
			//작업구분(1:내수/2:수출/3:연안해송/9:HYSCO스케줄)
			if ("9".equals(sWorkGp)) {
				//차량정보 존재여부 체크
				jrParam = commUtils.getParam(logId, mthdNm, sModifier);
				jrParam.setField("TRANS_ORD_DT"		, sTransOrdDt   );
				jrParam.setField("TRANS_ORD_SEQNO"	, sTransOrdSeqno);
				jrParam.setField("CAR_NO"			, sCarNo       );
				
				//PIDEV
//				String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI0", "J", "*");
				
//				if("N".equals(sApplyYnPI)) {
//					jrParam.setField("CARD_NO"			, sCardNo       );
//				}
				
				/* 
				SELECT YD_CAR_SCH_ID 
				  FROM TB_YD_CARSCH
				 WHERE TRANS_ORD_DATE  = :V_TRANS_ORD_DT
				   AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
				   AND CARD_NO         = :V_CARD_NO
				   AND DEL_YN          = 'N'		
				*/	   
				JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdCarYdDEL_YN_PIDEV", logId, mthdNm, "차량스케쥴 조회");
				
				if (jsCarSch.size() > 0) {
					commUtils.printLog(logId, mthdNm + " TB_YD_CARSCH[차량스케줄이 편성되어 있습니다.:기존지시 삭제 " , "SL");	
					
					String ydOldCarSchId = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_SCH_ID")); //차량 작업지시
				 
					jrParam.setField("YD_CAR_SCH_ID", ydOldCarSchId);
					jrParam.setField("DEL_YN"       , "Y");
					
					/*
					UPDATE TB_YD_CARSCH
					   SET MODIFIER  = :V_MODIFIER
					     , MOD_DDTT  = SYSDATE
					     , DEL_YN    = :V_DEL_YN
					 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID   
					*/
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.delYdCarsch", logId, mthdNm, "TB_YD_CARSCH 차량 스케줄정보");
				}
				
				//차량스케줄 생성				
				String ydCarSchId = coilDao.getSeqId(logId, mthdNm, "CarSch");
				
				
				//차량정보 존재여부 체크
				jrParam.setField("YD_CARPNT_CD"		, sCarldPntCd);
				/*
				SELECT SUBSTR(YD_STK_COL_GP,1,4)||'0'||SUBSTR(YD_STK_COL_GP,6,1) AS YD_STK_COL_GP2
				     , DECODE(YD_STK_COL_ACT_STAT,'R','C',YD_STK_COL_ACT_STAT)   AS YD_STK_COL_ACT_STAT
				     , YD_CARPNT_CD
				     , DEL_YN
				     , YD_CAR_USETYPE_GP
				     , YD_GP
				     , YD_BAY_GP
				     , YD_STK_COL_GP
				     , TRN_EQP_CD
				     , CAR_NO
				     , CARD_NO
				     , WLOC_CD
				     , YD_PNT_CD
				     , YD_CARPNT_DESC
				     , YD_SPAN_FROM
				     , YD_SPAN_TO
				     , (SELECT ITEM1
				          FROM TB_YD_RULE 
				         WHERE REPR_CD_GP = 'J00005'
				           AND CD_GP      = 'J' --2열연 코일야드
				           AND ITEM       = '*' 
				           AND DEL_YN     = 'N' ) AS YD_BAYIN_WO_SEQ_YN --입동지시순번 적용여부
				  FROM TB_YD_CARPOINT A
				 WHERE YD_CARPNT_CD = :V_YD_CARPNT_CD
				   AND DEL_YN = 'N'
				*/	   
				JDTORecordSet jsCarPnt = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdCarPoint", logId, mthdNm, "차량스케쥴 조회");
				String sWlocCd 	= "";
				String ydStkColGp   = "";
				String ydPntCd      = "";

				if (jsCarPnt.size() > 0) {

					sWlocCd 	= commUtils.trim(jsCarPnt.getRecord(0).getFieldString("WLOC_CD"));
					ydStkColGp	= commUtils.trim(jsCarPnt.getRecord(0).getFieldString("YD_STK_COL_GP"));
					ydPntCd     = commUtils.trim(jsCarPnt.getRecord(0).getFieldString("YD_PNT_CD"));
					String ydBayinWoSeqYn = commUtils.nvl (jsCarPnt.getRecord(0).getFieldString("YD_BAYIN_WO_SEQ_YN"), "N");
					
					JDTORecord jrCarSch = commUtils.getParam(logId, mthdNm, sModifier);
					jrCarSch.setField("YD_CAR_SCH_ID"        , ydCarSchId);
					jrCarSch.setField("YD_EQP_WRK_STAT"      , "U");                            //야드설비작업상태 
					jrCarSch.setField("YD_EQP_ID"            , CConstant.YD_DM_CAR_EQP_ID);     //야드설비ID
					jrCarSch.setField("YD_CAR_USE_GP"        , CConstant.YD_CAR_USE_GP_DM);     //차량사용구분
					jrCarSch.setField("SPOS_WLOC_CD"         , sWlocCd);                        //발지개소코드
					jrCarSch.setField("CAR_NO"               , sCarNo);                         //차량번호 
					jrCarSch.setField("CARD_NO"              , sCardNo);                        //카드번호
					jrCarSch.setField("YD_CARLD_LEV_DT"      , commUtils.getDateTime14());      //상차출발일시
					jrCarSch.setField("TRANS_ORD_DATE"       , sTransOrdDt);                    //운송지시일자
					jrCarSch.setField("TRANS_ORD_SEQNO"      , sTransOrdSeqno);                 //운송지시순번
					jrCarSch.setField("YD_CARLD_STOP_LOC"    , ydStkColGp);                     //야드상차정지위치
					
					if ("Y".equals(sUgntBayinYn)) {
						jrCarSch.setField("YD_BAYIN_WO_SEQ"      , "1");
					} else if ("Y".equals(ydBayinWoSeqYn)) {
						jrCarSch.setField("YD_BAYIN_WO_SEQ"      , "1");
					} else {
						jrCarSch.setField("YD_BAYIN_WO_SEQ"      , CConstant.YD_BAYIN_WO_SEQ_DEFAULT);	//입동지시순번 - 기본값으로 설정(9)
					}

					jrCarSch.setField("YD_CAR_PROG_STAT"     , "1"          ); //상차출발상태
					jrCarSch.setField("YD_CAR_WRK_GP"        , sWorkGp      );
					jrCarSch.setField("YD_PNT_CD1"           , ydPntCd      ); //야드포인트코드1
					jrCarSch.setField("TRANS_EQUIPMENT_TYPE" , "P"          ); //운송장비타입 P : PDA
					jrCarSch.setField("CAR_KIND"             , "TR"         ); //차량종류
					jrCarSch.setField("CMBN_CARLD_YN"        , "N"          ); //복수차량여부
					jrCarSch.setField("TEL_NO"               , sTelNo       ); //연락처
					jrCarSch.setField("DRIVER_NAME"          , sDriverName  ); //운전기사명
					jrCarSch.setField("WAIT_ARR_DDTT"		 , commUtils.getDateTime14());	//대기장도착시간
					jrCarSch.setField("CAR_REMODEL_YN"       , sCarRemodelYn); //가변차량 여부
					
		    		//차량스케줄 등록
					/* 
					INSERT INTO USRYDA.TB_YD_CARSCH
					(	   YD_CAR_SCH_ID
					     , REGISTER
					     , REG_DDTT
					     , MODIFIER
					     , MOD_DDTT
					     , DEL_YN
					     , YD_EQP_ID
					     , YD_CAR_USE_GP
					     , CAR_NO
					     , TRN_EQP_CD
					     , CAR_KIND
					     , YD_EQP_WRK_STAT
					     , SPOS_WLOC_CD
					     , ARR_WLOC_CD
					     , YD_CARLD_LEV_LOC
					     , YD_CARLD_LEV_DT
					     , YD_CARUD_LEV_DT
					     , YD_PNT_CD1
					     , YD_PNT_CD3
					     , YD_CARLD_STOP_LOC
					     , YD_CARUD_STOP_LOC
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
					     , TRANS_EQUIPMENT_TYPE
					     , DRIVER_NAME
					     , CAR_REMODEL_YN 
					) VALUES (
					       :V_YD_CAR_SCH_ID
					     , :V_MODIFIER
					     , SYSDATE
					     , :V_MODIFIER
					     , SYSDATE
					     , 'N'
					     , :V_YD_EQP_ID
					     , :V_YD_CAR_USE_GP
					     , :V_CAR_NO
					     , :V_TRN_EQP_CD
					     , :V_CAR_KIND
					     , :V_YD_EQP_WRK_STAT
					     , :V_SPOS_WLOC_CD
					     , :V_ARR_WLOC_CD              --
					     , :V_YD_CARLD_LEV_LOC
					     , TO_DATE(:V_YD_CARLD_LEV_DT,'YYYYMMDDHH24MISS')
					     , TO_DATE(:V_YD_CARUD_LEV_DT,'YYYYMMDDHH24MISS')
					     , NVL(:V_YD_PNT_CD1,'0000')
					     , NVL(:V_YD_PNT_CD3,'0000')
					     , :V_YD_CARLD_STOP_LOC
					     , :V_YD_CARUD_STOP_LOC        --
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
					     , :V_TRANS_EQUIPMENT_TYPE
					     , :V_DRIVER_NAME
					     , :V_CAR_REMODEL_YN
					)
					 */   
					commDao.insert(jrCarSch, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.insYdCarsch", logId, mthdNm, "TB_YD_CARSCH 등록");
		    		
				} else {
					commUtils.printLog(logId, mthdNm + "TB_YD_CARPOINT[차량포인트가 존재하지 않습니다..]" , "SL");
				}
				
				/*
				 * 6. 차량입동지시요구 모듈을 호출한다.
				 */
				commUtils.printLog(logId, "차량정지위치[" + ydStkColGp + "], 차량스케줄ID[" + ydCarSchId + "] -PDA AB차량입동지시요구 모듈을 호출 시작" , "SL");
				
				JDTORecord jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);	
				jrYdMsg.setField("JMS_TC_CD"			, "YDYDJ553");          //차량입동지시 요구 기존:YDYDJ662
				jrYdMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
				jrYdMsg.setField("YD_CARPNT_CD"		    , sCarldPntCd);
				jrYdMsg.setField("YD_CAR_SCH_ID"		, ydCarSchId );
				jrYdMsg.setField("CR_FRTOMOVE_GP"       , sCrFrtomoveGp );
				
				EJBConnector ejbConn = new EJBConnector("default", "CCoilCarMvSeEJB", this);
				JDTORecord jrRst = (JDTORecord)ejbConn.trx("procCarBayInOrdReqTr", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
				
				jrRtn = commUtils.addSndData(jrRtn, jrRst);
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
	 * 코일이송상차도착PDA(DMYDR071)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvDMYDR071(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "코일이송상차도착PDA 수신[CCoilL3RcvSeEJB.rcvDMYDR071] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			//수신 항목 값
			String msgId            = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydGp             = commUtils.trim(rcvMsg.getFieldString("YD_GP"          )); //야드구분
			String sTransOrdDt  	= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"   )); //운송실적일자
			String sTransOrdSeqno	= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO")); //운송실적순번
			String sCarKind		    = commUtils.trim(rcvMsg.getFieldString("CAR_KIND"       ));
			String sCarNo 			= commUtils.trim(rcvMsg.getFieldString("CAR_NO"         ));
			String sCardNo          = commUtils.trim(rcvMsg.getFieldString("CARD_NO"        ));
			String sCrFrtomoveGp	= commUtils.trim(rcvMsg.getFieldString("CR_FRTOMOVE_GP" )); //11수출사내 41열연제품이송 63임가공 81열연소재이송  
			String sWorkGp 			= commUtils.trim(rcvMsg.getFieldString("WORK_GP"        )); // 작업구분
			String sCarldPntCd		= commUtils.trim(rcvMsg.getFieldString("CARLD_PNT_CD"   )); // 상차포인트
			String sTelNo           = commUtils.trim(rcvMsg.getFieldString("TEL_NO"         )); //전화번호
			String sDriverName      = commUtils.trim(rcvMsg.getFieldString("DRIVER_NAME"    )); //운전기사명
			
			String sModifier 		= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }

			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

			String sMsg            = "";
			String ydCarSchId       = "";
			String ydCarProgStat    = "";
			String ydCarWrkGp 		= ""; 	//야드차량작업구분
			String ydEqpWrkStat		= ""; 	//야드설비작업상태
			
			/**********************************************************
			* 0. 수신 항목 값 Check
			**********************************************************/
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("TRANS_ORD_DATE" , sTransOrdDt   );
			jrParam.setField("TRANS_ORD_SEQNO", sTransOrdSeqno);
			jrParam.setField("CAR_NO"         , sCarNo        );
			
			// PIDEV
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI0", "J", "*");			
			
//			if("N".equals(sApplyYnPI)) {
//				jrParam.setField("CARD_NO"        , sCardNo       );			
//			}

			/***********************************
        	 * 1. 차량스케줄 중복 Check
        	 ***********************************/
			commUtils.printLog(logId, "1. 차량스케줄 중복 체크" , "SL");
			jrParam.setField("TRANS_ORD_DT"   , sTransOrdDt );
			jrParam.setField("CMBN_CARLD_YN"  , "N"         ); //복수차량여부
			/*
			SELECT *
			  FROM TB_YD_CARSCH  CS
			     , TB_YD_WRKBOOK WB
			 WHERE CS.TRANS_ORD_DATE  = :V_TRANS_ORD_DT
			   AND CS.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
			   AND CS.CAR_NO          = :V_CAR_NO
			   AND CS.CMBN_CARLD_YN   = :V_CMBN_CARLD_YN
			   AND CS.DEL_YN   = 'N'	
			   AND WB.DEL_YN   = 'N'	
			   AND CS.CAR_NO   = WB.CAR_NO
			   AND CS.CARD_NO  = WB.CARD_NO
			   AND WB.YD_WBOOK_ID = CASE WHEN CS.YD_CAR_PROG_STAT IN ('0','1','2') THEN YD_CARLD_WRK_BOOK_ID 
			                             ELSE YD_CARUD_WRK_BOOK_ID END  
			*/	   
			JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdCarYdCmbnCarldYn_PIDEV", logId, mthdNm, "차량정보 존재여부 체크");
			
			if (rsResult.size() > 0) {
				
				/*
				 * 도착전문에 중복 수신되면 Exception 처리함
				 */
				commUtils.printLog(logId, "이미 도착처리된 차량입니다." , "SL");
				
				throw new DAOException("이미 도착처리된 차량입니다.");
			}
			
			/**********************************************************
			* 1. 차량포인트 조회
			**********************************************************/
			jrParam.setField("YD_CARPNT_CD"    , sCarldPntCd);
			/*
			SELECT SUBSTR(YD_STK_COL_GP,1,4)||'0'||SUBSTR(YD_STK_COL_GP,6,1) AS YD_STK_COL_GP2
			     , DECODE(YD_STK_COL_ACT_STAT,'R','C',YD_STK_COL_ACT_STAT)   AS YD_STK_COL_ACT_STAT
			     , YD_CARPNT_CD
			     , DEL_YN
			     , YD_CAR_USETYPE_GP
			     , YD_GP
			     , YD_BAY_GP
			     , YD_STK_COL_GP
			     , TRN_EQP_CD
			     , CAR_NO
			     , CARD_NO
			     , WLOC_CD
			     , YD_PNT_CD
			     , YD_CARPNT_DESC
			     , YD_SPAN_FROM
			     , YD_SPAN_TO
			     , (SELECT ITEM1
			          FROM TB_YD_RULE 
			         WHERE REPR_CD_GP = 'J00005'
			           AND CD_GP      = 'J' --2열연 코일야드
			           AND ITEM       = '*' 
			           AND DEL_YN     = 'N' ) AS YD_BAYIN_WO_SEQ_YN --입동지시순번 적용여부
			  FROM TB_YD_CARPOINT A
			 WHERE YD_CARPNT_CD = :V_YD_CARPNT_CD
			   AND DEL_YN = 'N'
			 */
			JDTORecordSet jsCarPoint = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdCarPoint", logId, mthdNm, "차량스케줄 조회");
			
			if (jsCarPoint.size() != 1) {
				throw new Exception("차량포인트["+sCarldPntCd+"] 조회 실패");
			}
			
			String sCarNoOld  = jsCarPoint.getRecord(0).getFieldString("CAR_NO" ); //카번호
			String sCardNoOld = jsCarPoint.getRecord(0).getFieldString("CARD_NO"); //카드번호
			
			if (!sCarNo.equals(sCarNoOld) || !sCardNo.equals(sCardNoOld)) {
				throw new Exception("차량포인트의 차량번호가 다릅니다. 차량포인트를 확인하세요");
			}
						
		   /**********************************************************
			* 2. 운송실적번호로 저장품 조회
			**********************************************************/
			/* 
			WITH TEMP_TABLE AS (
			SELECT A.STL_NO
			     , SUBSTR(B.YD_STK_COL_GP,2,1) AS YD_BAY_GP
			     , A.YD_AIM_YD_GP
			     , A.YD_AIM_BAY_GP
			     , B.YD_STK_COL_GP
			     , B.YD_STK_BED_NO
			     , B.YD_STK_LYR_NO
			     , A.TRANS_ORD_SEQNO
			  FROM USRYDA.TB_YD_STOCK A
			     , USRYDA.TB_YD_STKLYR B
			 WHERE A.STL_NO          = B.STL_NO
			   AND A.TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
			   AND A.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
			)
			SELECT *
			  FROM TEMP_TABLE A
			 WHERE (YD_STK_COL_GP,YD_STK_BED_NO,YD_STK_LYR_NO)=(SELECT YD_STK_COL_GP,YD_STK_BED_NO,YD_STK_LYR_NO
			                                                      FROM TEMP_TABLE B
			                                                     WHERE A.STL_NO = B.STL_NO 
			                                                       AND ROWNUM <= 1)
		 	*/
			JDTORecordSet jsStlno = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdStockTransOrdDateSeqNo", logId, mthdNm, "운송실적번호에 맞는 제품번호가 존재 조회");			
			if (jsStlno.size() <= 0 ) {
				sMsg = "운송실적번호에 맞는 저장품 존재 안함: TRANS_WORD_NO:["+sTransOrdDt+sTransOrdSeqno+"]" ;
				commUtils.printLog(logId, sMsg, "S-");	
				throw new Exception(sMsg);
//				return jrRtn;
			}
			// 저장품 수정
			/*
			UPDATE TB_YD_STOCK
			   SET MODIFIER = :V_MODIFIER
			     , MOD_DDTT = SYSDATE
			     , CAR_NO   = :V_CAR_NO
			     , CARD_NO  = :V_CARD_NO
			     , YD_AIM_RT_GP = NVL(:V_YD_AIM_RT_GP, YD_AIM_RT_GP)
			     , STL_PROG_CD  = NVL(:V_STL_PROG_CD , STL_PROG_CD)
			 WHERE TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
			   AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
			 */
			
			// PIDEV
//			if("Y".equals(sApplyYnPI)) {
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStock2_PIDEV", logId, mthdNm, "저장품 수정");
//			} else {
//				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStock2", logId, mthdNm, "저장품 수정");
//			}
			
			
			/*
			 * 3. 저장품이 적치된 저장위치 정보를 조회 - 저장위치가 04, 05, 06스판이면 A통로, 07스판이면 B통로로 처리한다.
			 */ 
			jrParam.setField("YD_STK_COL_GP", "");
			
			JDTORecordSet jsStock = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdStockYdStkLyrMtlCTransDTSeqCardNoDesc_PIDEV", logId, mthdNm, "조회");
			if (jsStock.size() <= 0) {
				commUtils.printLog(logId, "저장품 조회 실패", "S-");
				return jrRtn;
			}
			
			String ydStkColGp = jsStock.getRecord(0).getFieldString("YD_STK_COL_GP");

			/*
			 * 4. 위의 저장위치정보를 가지고 차량이 입동가능한 차량정지위치를 조회한다
			 *	카드번호 : E,P,T 로 시작 ->PT  , 숫자 로 시작 ->TR
			 */
			//작업구분(1:내수/2:수출/3:연안해송/9:HYSCO스케줄)
			if ("9".equals(sWorkGp)) {
			
				jrParam.setField("YD_CARPNT_CD", sCarldPntCd);
				
				/*
				SELECT SUBSTR(YD_STK_COL_GP,1,4)||'0'||SUBSTR(YD_STK_COL_GP,6,1) AS YD_STK_COL_GP2
				     , DECODE(YD_STK_COL_ACT_STAT,'R','C',YD_STK_COL_ACT_STAT)   AS YD_STK_COL_ACT_STAT
				     , YD_CARPNT_CD
				     , DEL_YN
				     , YD_CAR_USETYPE_GP
				     , YD_GP
				     , YD_BAY_GP
				     , YD_STK_COL_GP
				     , TRN_EQP_CD
				     , CAR_NO
				     , CARD_NO
				     , WLOC_CD
				     , YD_PNT_CD
				     , YD_CARPNT_DESC
				     , YD_SPAN_FROM
				     , YD_SPAN_TO
				  FROM TB_YD_CARPOINT A
				 WHERE YD_CARPNT_CD = :V_YD_CARPNT_CD
				 */
				JDTORecordSet jsCarpnt = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdCarPoint", logId, mthdNm, "개소코드 조회");
				if (jsCarpnt.size() > 0) {
					String sWlocCd         = commUtils.nvl(jsCarpnt.getRecord(0).getFieldString("WLOC_CD"            ), "");
					String ydPntCd         = commUtils.nvl(jsCarpnt.getRecord(0).getFieldString("YD_PNT_CD"          ), "");	
					String ydStkColActStat = commUtils.nvl(jsCarpnt.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT"), "");
					
					commUtils.printLog(logId, "sWlocCd         : " + sWlocCd, "SL");
					commUtils.printLog(logId, "ydPntCd         : " + ydPntCd, "SL");
					commUtils.printLog(logId, "ydStkColActStat : " + ydStkColActStat, "SL");
					
					//TR가 도착해 있는 경우 
					if ("TR".equals(sCarKind) && "L".equals(ydStkColActStat)) {
						ydStkColActStat = "C";
					}
					
					if ("C".equals(ydStkColActStat)) {

						/* **********************************
						 * 출하차량도착실적처리 - 맵활성화
						 * **********************************/
						JDTORecord jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
						jrYdMsg.setField("YD_GP"			, ydGp);
						jrYdMsg.setField("TRANS_ORD_DATE"	, sTransOrdDt);    
						jrYdMsg.setField("TRANS_ORD_SEQNO" 	, sTransOrdSeqno);
						jrYdMsg.setField("CAR_NO"			, sCarNo);
						jrYdMsg.setField("CARD_NO"			, sCardNo);
						jrYdMsg.setField("SPOS_WLOC_CD"		, sWlocCd);	
						jrYdMsg.setField("SPOS_YD_PNT_CD"	, ydPntCd);
						jrYdMsg.setField("CAR_KIND"			, sCarKind);
						jrYdMsg.setField("WORK_GP"			, sWorkGp);
						jrYdMsg.setField("IS_EJB_CALL"		,"N");
						
						EJBConnector ejbConn = new EJBConnector("default", "CCoilCarMvSeEJB", this);
						JDTORecord jrRst = (JDTORecord)ejbConn.trx("procOutCarArrWr", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
						
						String sRtnCd = commUtils.trim(jrRst.getFieldString("RTN_CD" ));
						
						if (!"1".equals(sRtnCd)) {
							return jrRtn;
						}
						
						String ydCarldStopLoc = commUtils.trim(jrRst.getFieldString("YD_STK_COL_GP" ));
						
						jrYdMsg.setField("YD_CARLD_STOP_LOC", ydCarldStopLoc);
						jrYdMsg.setField("CR_FRTOMOVE_GP",  sCrFrtomoveGp);
						jrYdMsg.setField("YD_CAR_USE_GP"  , "G");
						jrYdMsg.setField("YD_BAY_GP"      , ydCarldStopLoc.substring(1, 2));

						/* *******************************
						 * 2열연코일출하상차LOT편성전문
						 * *******************************/
						jrYdMsg = (JDTORecord)ejbConn.trx("procCoilGdsDistCarLdComp", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
						jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
						
					} //if ("C".equals(ydStkColActStat))
				}
			}//if ("9".equals(sWorkGp))
				
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}	
	
	
	/**
	 * 코일이송상차완료PDA(DMYDR072)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvDMYDR072(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "코일이송상차완료PDA 수신[CCoilL3RcvSeEJB.rcvDMYDR072] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			//수신 항목 값
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydGp         = commUtils.trim(rcvMsg.getFieldString("YD_GP"        ));
			String sStlAppearGp = commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP")); //재료외형
			String sStlNo       = commUtils.trim(rcvMsg.getFieldString("STL_NO"       )); //재료번호

			String sModifier 	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"     ));//수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }

			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			
			String sMsg	= "";
			
			/**********************************************************
			* 0. 수신 항목 값 Check
			**********************************************************/  			
			// 수신한 전문이 null이라면 error
			if ("".equals(msgId)) {
				sMsg = "[ERROR] "+mthdNm+"::"+mthdNm+"() TC Code Error (NULL)";	
				commUtils.printLog(logId, sMsg, "S-");
				return jrRtn;
			}
			
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("STL_NO"        , sStlNo);
			jrParam.setField("STL_APPEAR_GP" , sStlAppearGp);
			
			/*
			SELECT A.*
			     , NVL(B.YD_STK_COL_GP,C.YD_STK_COL_GP)   AS YD_STK_COL_GP
			     , B.YD_STK_BED_NO                AS YD_STK_BED_NO
			     , B.YD_STK_LYR_NO                AS YD_STK_LYR_NO
			  FROM TB_YD_STOCK  A
			     , TB_YD_STKLYR B
			     , TB_YD_STKCOL C
			 WHERE A.STL_NO  = :V_STL_NO
			   AND A.STL_NO  = B.STL_NO(+)
			   AND A.CARD_NO = C.CARD_NO(+) 
			 */
			JDTORecordSet jsStock = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdStockJoinStkLyr2_PIDEV", logId, mthdNm, "저장품 조회");
			
			if (jsStock.size() <= 0) {
				sMsg = "YD_STOCK 조회[코일이송상차완료] Error";	
				commUtils.printLog(logId, sMsg, "S-");
				return jrRtn;
			}
			
			String sCarNo         = commUtils.trim(jsStock.getRecord(0).getFieldString("CAR_NO"));
			String ydStkColGp     = commUtils.trim(jsStock.getRecord(0).getFieldString("YD_STK_COL_GP"));
			String sCardNo        = commUtils.trim(jsStock.getRecord(0).getFieldString("CARD_NO"));
			String sTransOrdDate  = commUtils.trim(jsStock.getRecord(0).getFieldString("TRANS_ORD_DATE"));
			String sTransOrdSeqno = commUtils.trim(jsStock.getRecord(0).getFieldString("TRANS_ORD_SEQNO"));
			
			jrParam.setField("DEL_YN"         	, "Y");
			jrParam.setField("YD_AIM_RT_GP"   	, "M2");
			jrParam.setField("STL_PROG_CD"    	, "M"); 

			//저장품갱신		
			/*
			UPDATE TB_YD_STOCK
			   SET MODIFIER      = :V_MODIFIER
			     , MOD_DDTT      = SYSDATE
			     , STL_APPEAR_GP = :V_STL_APPEAR_GP
			     , YD_AIM_RT_GP  = :V_YD_AIM_RT_GP
			     , DEL_YN        = :V_DEL_YN     
			     , STL_PROG_CD   = :V_STL_PROG_CD
			 WHERE STL_NO = :V_STL_NO  
			*/
			int nCnt = commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStockCarldCmpl", logId, mthdNm, "저장품 삭제");
			if (nCnt <= 0) {
				sMsg = "YD_STOCK[코일제품출하완료] UPDATE Error";           
				commUtils.printLog(logId, sMsg, "S-");
				return jrRtn;
	 		}				
			/***************************************************************************
			 *  저장품이 적치된 저장위치 정보를 조회
			 ***************************************************************************/
			sMsg = "[" + mthdNm + "]카드번호["+ sCardNo+"], 차량번호["+ sCardNo+"], 운송지시일자["+sTransOrdDate+"], 운송지시순번["+sTransOrdSeqno+"] : 출하완료된 동["+ydStkColGp+"]의 저장품들 조회 시작";
			commUtils.printLog(logId, sMsg, "SL");
			
			if ("*".equals(sStlAppearGp)) {

				commUtils.printLog(logId, "[" + mthdNm + "] 마지막 상차완료 전문", "SL");
				
				jrParam.setField("CAR_NO" 			, sCarNo);
				
				//PIDEV
//				String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI0", "J", "*");
				
//				if("N".equals(sApplyYnPI)) {
//					jrParam.setField("CARD_NO"			, sCardNo);
//				}
				
				jrParam.setField("TRANS_ORD_DATE"	, sTransOrdDate);
				jrParam.setField("TRANS_ORD_SEQNO"	, sTransOrdSeqno);
				
				/*
				SELECT *
				  FROM (
				        SELECT *
				          FROM TB_YD_CARSCH
				         WHERE CAR_NO       LIKE :V_CAR_NO||'%'
				           AND CARD_NO         = :V_CARD_NO
				           AND TRANS_ORD_DATE  = :V_TRANS_ORD_DT
				           AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
				         ORDER BY YD_CAR_SCH_ID DESC
				       ) A
				 WHERE ROWNUM <= 1   
		    	*/                                                     				
				JDTORecordSet jsCarsch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCarschTransDTSeq2_PIDEV", logId, mthdNm, "차량정정보검색");
				
				if (jsCarsch.size() <= 0 ) {
					sMsg = "차량스케쥴 조회 SELECT Error ::  DO NOT EXIST"  ;
					commUtils.printLog(logId, sMsg, "SL");	
					return jrRtn;
				}	

				JDTORecord sndL3Msg = commUtils.getParam(logId, mthdNm, sModifier);
				sndL3Msg.setField("TC_CODE"			, "DMYDR040");	//전문코드
				sndL3Msg.setField("SPOS_WLOC_CD"	, commUtils.trim(jsCarsch.getRecord(0).getFieldString("SPOS_WLOC_CD")));
				sndL3Msg.setField("SPOS_YD_PNT_CD"	, commUtils.trim(jsCarsch.getRecord(0).getFieldString("YD_PNT_CD1"  )));
				sndL3Msg.setField("YD_GP"			, ydGp          );
				sndL3Msg.setField("TRANS_ORD_DT"    , commUtils.trim(jsCarsch.getRecord(0).getFieldString("TRANS_ORD_DATE" )));
				sndL3Msg.setField("TRANS_ORD_SEQNO" , commUtils.trim(jsCarsch.getRecord(0).getFieldString("TRANS_ORD_SEQNO")));
				sndL3Msg.setField("CAR_NO"          , sCarNo   );
				sndL3Msg.setField("CARD_NO"         , sCardNo  );				
				if ("".equals(sCardNo)) {
					sCardNo = "XXXXX";
				}					
				sndL3Msg.setField("MSG_ID"			, msgId    );
//				commUtils.printParam(logId, jrParam);
				//전송 Data 생성
				sMsg = "차량번호[" + sCarNo + "]는 코일제품출하차량출발실적호출";
				commUtils.printLog(logId, sMsg, "SL");
				
				EJBConnector ejbConn = new EJBConnector("default", "CCoilCarMvSeEJB", this); //TODO 호출로직 점검필요
				JDTORecord jrRtn1 = (JDTORecord)ejbConn.trx("procOutCarLevWr", new Class[] { JDTORecord.class }, new Object[] { sndL3Msg });
				jrRtn = commUtils.addSndData(jrRtn, jrRtn1);
				
			} else {
				
				sMsg="[" + mthdNm + "] 마지막 상차완료 전문이 아님";
				commUtils.printLog(logId, sMsg, "SL");
				
			}
			// 차량 출발후 저장품 정보 갱신
			// L2저장품재원 정보 송신
			/**********************************************************
			* 2. 저장품 제원 야드L2 전송 (YDY5L002)
			**********************************************************/				
			JDTORecord sndL2Msg = commUtils.getParam(logId, mthdNm, sModifier);
			sndL2Msg.setField("JMS_TC_CD"			, "YDY5L002");
			sndL2Msg.setField("YD_INFO_SYNC_CD"		, "5");	//1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)
			sndL2Msg.setField("STL_NO"		        , sStlNo);
			
			jrRtn = commUtils.addSndData(jrRtn ,coilDao.getMsgL2("YDY5L002", sndL2Msg));
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}	
	
	
	/**
	 * 코일이송하차대기장도착PDA(DMYDR073)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvDMYDR073(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "코일이송하차지시PDA 수신[CCoilL3RcvSeEJB.rcvDMYDR073] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			//수신 항목 값
			String msgId            = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
//			String ydGp             = commUtils.trim(rcvMsg.getFieldString("YD_GP"          )); //야드구분
			String sStlAppearGp 	= commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"  )); //재료외형
			String sTransOrdDt  	= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"   ));
			String sTransOrdSeqno	= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));
			String sCancelYn     	= commUtils.trim(rcvMsg.getFieldString("CANCEL_YN"      ));
			String sCarKind		    = commUtils.trim(rcvMsg.getFieldString("CAR_KIND"       ));
			String sCarNo 			= commUtils.trim(rcvMsg.getFieldString("CAR_NO"         ));
			String sCardNo          = commUtils.trim(rcvMsg.getFieldString("CARD_NO"        ));
			String sCrFrtomoveGp	= commUtils.trim(rcvMsg.getFieldString("CR_FRTOMOVE_GP" )); //1 운송 2 이송
			String sWorkGp 			= commUtils.trim(rcvMsg.getFieldString("WORK_GP"        )); // 작업구분
			String sCarudPntCd		= commUtils.trim(rcvMsg.getFieldString("CARUD_PNT_CD"   )); // 상차포인트
			String sTelNo           = commUtils.trim(rcvMsg.getFieldString("TEL_NO"         )); //전화번호
			String sDriverName      = commUtils.trim(rcvMsg.getFieldString("DRIVER_NAME"    )); //운전기사명
			String sUgntBayinYn     = commUtils.trim(rcvMsg.getFieldString("UGNT_BAYIN_YN"  )); // 복수상차 마지막 차량에 대한 구분 Y: 1순위
			
			int iYdEqpWrkSh 		= Integer.parseInt(commUtils.nvl(rcvMsg.getFieldString("YD_EQP_WRK_SH"),"0"));
			String sModifier 		= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			
			if ("".equals(sModifier)) { sModifier = msgId; }

			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			
			JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
			
			String[] rVal = new String[1];
			
			/***********************************
			 * 0. 취소
			 ***********************************/
			if ("Y".equals(sCancelYn)) {
			    jrRtn = this.procDmTcCncl(rcvMsg);
			    return jrRtn;
			}
			
			/**********************************************************
			 * 코일이송하차시 하차포인트는 야드에서 정한다 20210607
			 * 적치율, 대기챠랑 적은 순
			 **********************************************************/
			if ("".equals(sCarudPntCd)) {
				/*
				WITH TEMP_TABLE_HY AS (
				SELECT SUBSTR(DONG,1,1)                    AS DONG_H
				     , SUM(HYUN_CNT) AS HYUN_CNT
				  FROM (SELECT (CASE WHEN A.YD_LOC_GP = 'J' AND A.YD_BAY_GP IN('B', 'C')
				                                            AND A.YD_EQP_GP BETWEEN '01' AND '30'
				                     THEN REPLACE(REPLACE(SUBSTR(A.YD_STK_COL_GP, 2, 5), 'B', 'X'), 'C', 'Y')
				                     ELSE SUBSTR(A.YD_STK_COL_GP, 2, 5) END
				               ) AS DONG
				             , SUM(1) AS HYUN_CNT
				          FROM TB_YD_STKCOL   A
				             , TB_YD_STKLYR   B
				             , TB_PT_COILCOMM C
				         WHERE A.YD_STK_COL_GP       = B.YD_STK_COL_GP
				           AND A.YD_GP               = 'J'
				           AND A.YD_LOC_GP           = 'J'
				           AND B.STL_NO              = C.COIL_NO(+)
				           AND A.DEL_YN              = 'N'
				           AND B.YD_STK_LYR_ACT_STAT = 'E'
				           AND A.YD_EQP_GP BETWEEN '01' AND '99'
				         GROUP BY (CASE WHEN A.YD_LOC_GP = 'J' AND A.YD_BAY_GP IN('B', 'C')
				                                               AND A.YD_EQP_GP BETWEEN '01' AND '30'
				                        THEN REPLACE(REPLACE(SUBSTR(A.YD_STK_COL_GP, 2, 5), 'B', 'X'), 'C', 'Y')
				                        ELSE SUBSTR(A.YD_STK_COL_GP, 2, 5) END)
				      ) AA
				 GROUP BY SUBSTR(DONG, 1, 1)
				)
				SELECT (CASE WHEN YD_LOC_GP = 'J' THEN DECODE(DONG,'B','B1','C','C1','X','B2','Y','C2',DONG) ELSE DONG END) AS DONG
				     , ROUND(H.HYUN_CNT) AS HYUN_CNT
				     , LYR_HYUN_CNT
				     , ROUND(H.HYUN_CNT- LYR_HYUN_CNT) AS POSS_CNT
				     , ROUND(TRUNC(LYR_HYUN_CNT / DECODE( H.HYUN_CNT ,0,1, H.HYUN_CNT ),4) * 100 , 2) AS STK_YEL
				     , CAR.CAR_CNT
				     , CAR.YD_CARPNT_CD
				  FROM (SELECT SUBSTR(DONG,1,1) AS DONG
				             , SUM(LYR_HYUN_CNT) AS LYR_HYUN_CNT
				             , MAX(YD_LOC_GP) AS YD_LOC_GP
				          FROM (SELECT MAX(A.YD_LOC_GP) AS YD_LOC_GP
				                     , (CASE WHEN A.YD_LOC_GP = 'J' AND SUBSTR(A.YD_STK_COL_GP,2,1) IN('B','C')
				                                                    AND SUBSTR(A.YD_STK_COL_GP,3,2) BETWEEN '01' AND '30'
				                             THEN REPLACE(REPLACE(SUBSTR(A.YD_STK_COL_GP,2,5),'B','X'),'C','Y')
				                             ELSE SUBSTR(A.YD_STK_COL_GP,2,5) END
				                       ) AS DONG
				                     , SUM(CASE WHEN B.STL_NO IS NOT NULL AND B.YD_STK_LYR_MTL_STAT IN ('C','U') THEN 1 ELSE 0 END) AS LYR_HYUN_CNT
				                  FROM TB_YD_STKCOL A
				                     , TB_YD_STKLYR B
				                     , TB_PT_COILCOMM C
				                 WHERE A.YD_STK_COL_GP = B.YD_STK_COL_GP
				                   AND A.YD_GP         = 'J'
				                   AND A.YD_LOC_GP     = 'J'--:V_YD_LOC_GP
				                   AND B.STL_NO        = C.COIL_NO(+)
				                   AND A.DEL_YN        = 'N'
				                 GROUP BY (CASE WHEN A.YD_LOC_GP='J' AND SUBSTR(A.YD_STK_COL_GP,2,1) IN('B','C')
				                                                     AND SUBSTR(A.YD_STK_COL_GP,3,2) BETWEEN '01' AND '30'
				                                THEN REPLACE(REPLACE(SUBSTR(A.YD_STK_COL_GP,2,5),'B','X'),'C','Y')
				                                ELSE SUBSTR(A.YD_STK_COL_GP,2,5) END)
				              ) AA
				         GROUP BY SUBSTR(DONG, 1, 1)
				      ) A
				     , TEMP_TABLE_HY H
				     , (SELECT CASE WHEN YD_CARPNT_CD = 'J2B1' THEN 'X'
				                    WHEN YD_CARPNT_CD = 'J2C1' THEN 'Y'
				                    ELSE YD_BAY_GP END YD_BAY_GP
				             , CAR_CNT
				             , YD_CARPNT_CD
				          FROM USRYDA.VW_YD_CARPOINT
				         WHERE YD_GP = 'J'
				           AND YD_CAR_USETYPE_GP IN('TO', 'TR')
				           AND YD_PNT_CD NOT LIKE '3%' --3통로 제외
				       ) CAR
				     , (SELECT * FROM TB_YD_RULE
				         WHERE REPR_CD_GP = 'APP833') R
				     , (SELECT * FROM TB_YD_RULE
				         WHERE REPR_CD_GP = 'J00007') X
				 WHERE A.DONG  = H.DONG_H(+)
				   AND A.DONG  = CAR.YD_BAY_GP
				   AND R.ITEM1 = 'Y'
				   AND X.CD_GP = DECODE(A.DONG,'B','B1','C','C1','X','B2','Y','C2', A.DONG)
				   AND X.ITEM1 = 'N'
				 ORDER BY CASE WHEN STK_YEL < R.ITEM_VALUE1 THEN 1 ELSE 2 END
				        , CAR_CNT 
				 */
				JDTORecordSet jsPnt = commDao.select(rcvMsg, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getCarLdBayInPointList", logId, mthdNm, "하차포인트 조회");
				if (jsPnt.size() > 0) {
					sCarudPntCd = jsPnt.getRecord(0).getFieldString("YD_CARPNT_CD");
					commUtils.printLog(logId, "하차포인트[sCarudPntCd] : "+sCarudPntCd, "SL");
				} else {
					throw new Exception("하차포인트 조회 실패!!");
				}
			}
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("STL_APPEAR_GP"  , sStlAppearGp  );
			jrParam.setField("TRANS_ORD_DATE" , sTransOrdDt   );
			jrParam.setField("TRANS_ORD_SEQNO", sTransOrdSeqno);
			jrParam.setField("CARD_NO"        , sCardNo       );
			jrParam.setField("CAR_NO"         , sCarNo        );
			jrParam.setField("REHEAT_SLAB_GP" , sWorkGp       );
			jrParam.setField("COIL_CAR_NO"    , sCarudPntCd   );
			jrParam.setField("CR_FRTOMOVE_GP" , sCrFrtomoveGp );
			
			if (sCarKind.length() > 2) { //장비구분: Trailer-T , TT Trailer -TT
				jrParam.setField("YD_STK_BED_NO", sCarKind.substring(0, 2));
			} else {
				jrParam.setField("YD_STK_BED_NO", sCarKind);
			}
			
			/***********************************
        	 * 1. 차량스케줄 중복 Check
        	 ***********************************/
			commUtils.printLog(logId, "1. 차량스케줄 중복 체크" , "SL");
			jrParam.setField("CMBN_CARLD_YN"  , "N");
			jrParam.setField("TRANS_ORD_DT"   , sTransOrdDt   );
			/*
			SELECT *
			  FROM TB_YD_CARSCH
			 WHERE TRANS_ORD_DATE  = :V_TRANS_ORD_DT
			   AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
			   AND CAR_NO          = :V_CAR_NO
			   AND CMBN_CARLD_YN   = :V_CMBN_CARLD_YN
			   AND DEL_YN   = 'N'	
			*/	   
			JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdCarYdCmbnCarldYn61", logId, mthdNm, "차량정보 존재여부 체크");
			
			if (rsResult.size() > 0) {
				throw new DAOException("TB_YD_CARSCH 차량스케줄이 편성되어 있습니다");
			}
						
		   /**********************************************************
			* 2. 저장품 이동 조건 수정
			**********************************************************/
			String sStlNo = "";
			for (int i = 1 ; i <= 20; i++) {
			
				sStlNo = commUtils.trim(rcvMsg.getFieldString("STL_NO"+i));
				
				if ("".equals(sStlNo)) {
					break;
				}
				
				jrParam.setField("STL_NO" 		     , sStlNo); //저장품 ID
				jrParam.setField("YD_CAR_UPP_LOC_CD" , commUtils.trim(rcvMsg.getFieldString("GDS_CARLD_LOC"+i))); //차상위치
				
				
				commUtils.printLog(logId, "저장품 등록", "SL");
    			coilDao.stockProcCom(logId, mthdNm, sStlNo, 1);
				
				
				//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
				rVal = coilDao.getYdAimRtGp("C", jrParam);
				
				jrParam.setField("YD_AIM_RT_GP", "A1" );
				jrParam.setField("STL_PROG_CD" , rVal[1] );
				jrParam.setField("DEL_YN"      , "N"     );

				JDTORecordSet jsStock = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYdStock", logId, mthdNm, "저장품조회");
				
				if (jsStock.size() > 0) {
					/* 
					UPDATE TB_YD_STOCK
					   SET MODIFIER = :V_MODIFIER
					     , MOD_DDTT = SYSDATE
					     , STL_APPEAR_GP     = :V_STL_APPEAR_GP
					     , TRANS_ORD_DATE    = :V_TRANS_ORD_DATE
					     , TRANS_ORD_SEQNO   = :V_TRANS_ORD_SEQNO
					     , CARD_NO           = :V_CARD_NO
					     , CAR_NO            = :V_CAR_NO
					     , REHEAT_SLAB_GP    = :V_REHEAT_SLAB_GP
					     , COIL_CAR_NO       = :V_COIL_CAR_NO
					     , CR_FRTOMOVE_GP    = :V_CR_FRTOMOVE_GP
					     , YD_STK_BED_NO     = :V_YD_STK_BED_NO
					     , YD_CAR_UPP_LOC_CD = :V_YD_CAR_UPP_LOC_CD
					     , YD_AIM_RT_GP      = :V_YD_AIM_RT_GP
					     , STL_PROG_CD       = :V_STL_PROG_CD
					     , DEL_YN            = :V_DEL_YN
					     , YD_ABMTL_REM      = :V_MSG_CONTENTS
					     , SNDBK_REGISTER    = :V_SNDBK_REGISTER
					     , YD_MTL_WT         = NVL(:V_YD_MTL_WT,YD_MTL_WT)
					 WHERE STL_NO = :V_STL_NO
					*/         
			    	commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStockWaitLocArr", logId, mthdNm, "TB_YD_STOCK 등록");
				} else {
					/*
					INSERT INTO TB_YD_STOCK (
					       STL_NO 
					     , REGISTER 
					     , REG_DDTT 
					     , MODIFIER 
					     , MOD_DDTT 
					     , DEL_YN
					     , STL_APPEAR_GP
					     , TRANS_ORD_DATE
					     , TRANS_ORD_SEQNO
					     , CARD_NO
					     , CAR_NO
					     , REHEAT_SLAB_GP
					     , COIL_CAR_NO
					     , CR_FRTOMOVE_GP
					     , YD_STK_BED_NO
					     , YD_CAR_UPP_LOC_CD
					     , YD_AIM_RT_GP
					     , STL_PROG_CD
					) VALUES (
					       :V_STL_NO
					     , :V_MODIFIER
					     , SYSDATE 
					     , :V_MODIFIER
					     , SYSDATE
					     , 'N'
					     ,:V_STL_APPEAR_GP
					     ,:V_TRANS_ORD_DATE
					     ,:V_TRANS_ORD_SEQNO
					     ,:V_CARD_NO
					     ,:V_CAR_NO
					     ,:V_REHEAT_SLAB_GP
					     ,:V_COIL_CAR_NO
					     ,:V_CR_FRTOMOVE_GP
					     ,:V_YD_STK_BED_NO 
					     ,:V_YD_CAR_UPP_LOC_CD
					     ,:V_YD_AIM_RT_GP
					     ,:V_STL_PROG_CD
					)  
					 */
					commDao.insert(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.insYdStockDMYDR073", logId, mthdNm, "저장품 생성");
				}
				
				/**********************************************************
				* 저장품 제원 야드L2 전송 (YDY5L002)
				**********************************************************/				
				sndL2Msg = commUtils.getParam(logId, mthdNm, sModifier);
				sndL2Msg.setField("JMS_TC_CD"			, "YDY5L002");
				sndL2Msg.setField("YD_INFO_SYNC_CD"		, "5");	//1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)
				sndL2Msg.setField("STL_NO"		        , sStlNo);
				
				jrRtn = commUtils.addSndData(jrRtn ,coilDao.getMsgL2("YDY5L002", sndL2Msg));
				
			} // end for
			
			
			//작업구분(1:내수/2:수출/3:연안해송/9:냉연이송)
			
			if ("9".equals(sWorkGp)) {
				
				jrParam = commUtils.getParam(logId, mthdNm, sModifier);
				jrParam.setField("TRANS_ORD_DT"		, sTransOrdDt   );
				jrParam.setField("TRANS_ORD_SEQNO"	, sTransOrdSeqno);
				jrParam.setField("YD_CARPNT_CD"		, sCarudPntCd   );

				jrParam.setField("CAR_NO"			, sCarNo       );
				
				//PIDEV
//				String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI0", "J", "*");				
				
//				if("N".equals(sApplyYnPI)) {
//					jrParam.setField("CARD_NO"			, sCardNo       );
//				}				
				
				/* 
				SELECT YD_CAR_SCH_ID 
				  FROM TB_YD_CARSCH
				 WHERE TRANS_ORD_DATE  = :V_TRANS_ORD_DT
				   AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
				   AND CARD_NO         = :V_CARD_NO
				   AND DEL_YN          = 'N'		
				*/	  	   
				JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdCarYdDEL_YN_PIDEV", logId, mthdNm, "차량스케쥴 조회");
				
				if (jsCarSch.size() > 0) {
					commUtils.printLog(logId, mthdNm + " TB_YD_CARSCH[차량스케줄이 편성되어 있습니다.:기존지시 삭제 " , "SL");	
					
					String ydOldCarSchId = commUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_SCH_ID")); //차량 작업지시
				 
					jrParam.setField("YD_CAR_SCH_ID", ydOldCarSchId);
					jrParam.setField("DEL_YN"       , "Y");
					
					/*
					UPDATE TB_YD_CARSCH
					   SET MODIFIER  = :V_MODIFIER
					     , MOD_DDTT  = SYSDATE
					     , DEL_YN    = :V_DEL_YN
					 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID   
					*/
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.delYdCarsch", logId, mthdNm, "TB_YD_CARSCH 차량 스케줄정보");
				}
				
				//차량스케줄ID 생성				
				String ydCarSchId = coilDao.getSeqId(logId, mthdNm, "CarSch");
				
				
				//차량정보 존재여부 체크
				/*
				SELECT SUBSTR(YD_STK_COL_GP,1,4)||'0'||SUBSTR(YD_STK_COL_GP,6,1)  AS YD_STK_COL_GP2
				     , DECODE(A.YD_STK_COL_ACT_STAT,'R','C',YD_STK_COL_ACT_STAT)  AS YD_STK_COL_ACT_STAT
				     , YD_CARPNT_CD
				     , REG_DDTT
				     , REGISTER
				     , MOD_DDTT
				     , MODIFIER
				     , DEL_YN
				     , YD_CAR_USETYPE_GP
				     , YD_GP
				     , YD_BAY_GP
				     , YD_STK_COL_GP
				     , TRN_EQP_CD
				     , CAR_NO
				     , CARD_NO
				     , WLOC_CD
				     , YD_PNT_CD
				     , YD_CARPNT_DESC
				     , YD_SPAN_FROM
				     , YD_SPAN_TO
				     , (SELECT ITEM1
				          FROM TB_YD_RULE 
				         WHERE REPR_CD_GP = 'J00005'
				           AND CD_GP      = 'J' --2열연 코일야드
				           AND ITEM       = '*' 
				           AND DEL_YN     = 'N' ) AS YD_BAYIN_WO_SEQ_YN --입동지시순번 적용여부				     
				  FROM TB_YD_CARPOINT A
				 WHERE YD_CARPNT_CD=:V_YD_CARPNT_CD	
				*/	   
				JDTORecordSet jsCarPnt = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdCarPoint", logId, mthdNm, "차량스케쥴 조회");
				String sWlocCd 	    = "";
				String ydStkColGp   = "";
				String ydPntCd      = "";

				if (jsCarPnt.size() > 0) {

					sWlocCd 	          = commUtils.trim(jsCarPnt.getRecord(0).getFieldString("WLOC_CD"));
					ydStkColGp	          = commUtils.trim(jsCarPnt.getRecord(0).getFieldString("YD_STK_COL_GP"));
					ydPntCd               = commUtils.trim(jsCarPnt.getRecord(0).getFieldString("YD_PNT_CD"));
					String ydBayinWoSeqYn = commUtils.nvl (jsCarPnt.getRecord(0).getFieldString("YD_BAYIN_WO_SEQ_YN"), "N");  
					
					JDTORecord jrCarSch = commUtils.getParam(logId, mthdNm, sModifier);
					jrCarSch.setField("YD_CAR_SCH_ID"        , ydCarSchId);
					jrCarSch.setField("YD_EQP_WRK_STAT"      , "L");                            //야드설비작업상태(영차) 
					jrCarSch.setField("YD_EQP_ID"            , CConstant.YD_DM_CAR_EQP_ID);     //야드설비ID
					jrCarSch.setField("YD_CAR_USE_GP"        , CConstant.YD_CAR_USE_GP_DM);     //차량사용구분
//					jrCarSch.setField("SPOS_WLOC_CD"         , sWlocCd);                        //발지개소코드
					jrCarSch.setField("ARR_WLOC_CD"          , sWlocCd);						//발지개소코드
					jrCarSch.setField("CAR_NO"               , sCarNo);                         //차량번호 
					jrCarSch.setField("CARD_NO"              , sCardNo);                        //카드번호
					jrCarSch.setField("YD_CARUD_LEV_DT"      , commUtils.getDateTime14());      //하차출발일시
					jrCarSch.setField("TRANS_ORD_DATE"       , sTransOrdDt);                    //운송지시일자
					jrCarSch.setField("TRANS_ORD_SEQNO"      , sTransOrdSeqno);                 //운송지시순번
					jrCarSch.setField("YD_CARUD_STOP_LOC"    , ydStkColGp);                     //야드하차정지위치
					
					jrCarSch.setField("YD_CAR_PROG_STAT"     , "A");                            //상차출발상태
					jrCarSch.setField("YD_CAR_WRK_GP"        , sWorkGp);
					jrCarSch.setField("YD_PNT_CD3"           , ydPntCd);                        //야드포인트코드1
					jrCarSch.setField("TRANS_EQUIPMENT_TYPE" , 	"P");			//운송장비타입 P : PDA

					if ("Y".equals(sUgntBayinYn)) {
						jrCarSch.setField("YD_BAYIN_WO_SEQ"      , "1");
					} else if ("Y".equals(ydBayinWoSeqYn)) {
						jrCarSch.setField("YD_BAYIN_WO_SEQ"      , "1");
					} else {
						jrCarSch.setField("YD_BAYIN_WO_SEQ"      , CConstant.YD_BAYIN_WO_SEQ_DEFAULT);	//입동지시순번 - 기본값으로 설정(9)
					}
					
					jrCarSch.setField("CAR_KIND"              , "TR"       );			//차량종류
					jrCarSch.setField("TEL_NO"                , sTelNo     );			//연락처
					jrCarSch.setField("DRIVER_NAME"           , sDriverName);			//운전기사명
					jrCarSch.setField("WAIT_ARR_DDTT"		 , commUtils.getDateTime14());	//대기장도착시간
					
		    		//차량스케줄 등록
					/* 
					INSERT INTO USRYDA.TB_YD_CARSCH
					(	   YD_CAR_SCH_ID
					     , REGISTER
					     , REG_DDTT
					     , MODIFIER
					     , MOD_DDTT
					     , DEL_YN
					     , YD_EQP_ID
					     , YD_CAR_USE_GP
					     , CAR_NO
					     , TRN_EQP_CD
					     , CAR_KIND
					     , YD_EQP_WRK_STAT
					     , SPOS_WLOC_CD
					     , ARR_WLOC_CD
					     , YD_CARLD_LEV_LOC
					     , YD_CARLD_LEV_DT
					     , YD_CARUD_LEV_DT
					     , YD_PNT_CD1
					     , YD_PNT_CD3
					     , YD_CARLD_STOP_LOC
					     , YD_CARUD_STOP_LOC
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
					     , TRANS_EQUIPMENT_TYPE
					     , DRIVER_NAME
					) VALUES (
					       :V_YD_CAR_SCH_ID
					     , :V_MODIFIER
					     , SYSDATE
					     , :V_MODIFIER
					     , SYSDATE
					     , 'N'
					     , :V_YD_EQP_ID
					     , :V_YD_CAR_USE_GP
					     , :V_CAR_NO
					     , :V_TRN_EQP_CD
					     , :V_CAR_KIND
					     , :V_YD_EQP_WRK_STAT
					     , :V_SPOS_WLOC_CD
					     , :V_ARR_WLOC_CD              --
					     , :V_YD_CARLD_LEV_LOC
					     , TO_DATE(:V_YD_CARLD_LEV_DT,'YYYYMMDDHH24MISS')
					     , TO_DATE(:V_YD_CARUD_LEV_DT,'YYYYMMDDHH24MISS')
					     , NVL(:V_YD_PNT_CD1,'0000')
					     , NVL(:V_YD_PNT_CD3,'0000')
					     , :V_YD_CARLD_STOP_LOC
					     , :V_YD_CARUD_STOP_LOC        --
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
					     , :V_TRANS_EQUIPMENT_TYPE
					     , :V_DRIVER_NAME
					)
					 */   
					commDao.insert(jrCarSch, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.insYdCarsch", logId, mthdNm, "TB_YD_CARSCH 등록");

					String sGdsCarlcLoc = "";
					String ydStkBedNo   = "";
					
					for (int i = 1 ; i <= iYdEqpWrkSh; i++) {	

						sGdsCarlcLoc = commUtils.trim(rcvMsg.getFieldString("GDS_CARLD_LOC"+i));
						
						if ("A".equals(sGdsCarlcLoc.substring(0, 1))) {
							
							ydStkBedNo = "0" + sGdsCarlcLoc.substring(1, 2);
							
						} else if ("B".equals(sGdsCarlcLoc.substring(0, 1))) {
							
							if ("1".equals(sGdsCarlcLoc.substring(1, 2))) {
								ydStkBedNo = "06";
							} else if ("2".equals(sGdsCarlcLoc.substring(1, 2))) {
								ydStkBedNo = "07";
							} else if ("3".equals(sGdsCarlcLoc.substring(1, 2))) {
								ydStkBedNo = "08";
							} else if ("4".equals(sGdsCarlcLoc.substring(1, 2))) {
								ydStkBedNo = "09";
							} else if ("5".equals(sGdsCarlcLoc.substring(1, 2))) {
								ydStkBedNo = "10";
							}
							
						} else if ("C".equals(sGdsCarlcLoc.substring(0, 1))) {
							
							if ("1".equals(sGdsCarlcLoc.substring(1, 2))) {
								ydStkBedNo = "11";
							} else if ("2".equals(sGdsCarlcLoc.substring(1, 2))) {
								ydStkBedNo = "12";
							} else if ("3".equals(sGdsCarlcLoc.substring(1, 2))) {
								ydStkBedNo = "13";
							} else if ("4".equals(sGdsCarlcLoc.substring(1, 2))) {
								ydStkBedNo = "14";
							} else if ("5".equals(sGdsCarlcLoc.substring(1, 2))) {
								ydStkBedNo = "15";
							}
						} 
						jrParam = commUtils.getParam(logId, mthdNm, sModifier);
						jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
						jrParam.setField("STL_NO"       , commUtils.trim(rcvMsg.getFieldString("STL_NO"+i)));
						jrParam.setField("YD_STK_BED_NO", ydStkBedNo); //야드 차상위치코드
						jrParam.setField("YD_STK_LYR_NO", "001");
						jrParam.setField("DEL_YN"       , "N");
						/*
						INSERT INTO TB_YD_CARFTMVMTL(
						       YD_CAR_SCH_ID
						     , STL_NO
						     , REGISTER
						     , REG_DDTT
						     , MODIFIER
						     , MOD_DDTT
						     , DEL_YN
						     , YD_CAR_UPP_LOC_CD
						     , YD_STK_BED_NO
						     , YD_STK_LYR_NO
						     , HCR_GP
						     , STL_PROG_CD
						     , YD_MTL_ITEM
						     , YD_ROUTE_GP
						) VALUES ( 
						       :V_YD_CAR_SCH_ID
						     , :V_STL_NO
						     , :V_MODIFIER
						     , SYSDATE
						     , :V_MODIFIER
						     , SYSDATE
						     , 'N'
						     , :V_YD_CAR_UPP_LOC_CD
						     , :V_YD_STK_BED_NO
						     , :V_YD_STK_LYR_NO
						     , :V_HCR_GP
						     , :V_STL_PROG_CD
						     , :V_YD_MTL_ITEM
						     , :V_YD_ROUTE_GP     
						)
						 */
						commDao.insert(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.insCarSchmtl", logId, mthdNm, "차량재료 스케쥴 INSERT");					 
			    		
					}	// end for
				} else {
					commUtils.printLog(logId, mthdNm + "TB_YD_CARPOINT[차량포인트가 존재하지 않습니다]" , "SL");
				}
				
				//입동지시요구모듈 호출(trailer인 경우)
				if ("T".equals(sCarKind) || "TR".equals(sCarKind)) {
					/*
					 * 6. 차량정지위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
					 */
					commUtils.printLog(logId, "차량정지위치[" + ydStkColGp + "], 차량스케줄ID[" + ydCarSchId + "] -PDA AB차량입동지시요구 모듈을 호출 시작" , "SL");
					
					JDTORecord jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);	
					jrYdMsg.setField("JMS_TC_CD"			, "YDYDJ553");          //차량입동지시 요구 기존:YDYDJ662
					jrYdMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
					jrYdMsg.setField("YD_CARPNT_CD"		    , sCarudPntCd);
					jrYdMsg.setField("YD_CAR_SCH_ID"		, ydCarSchId);
					jrYdMsg.setField("CR_FRTOMOVE_GP"       , sCrFrtomoveGp );
					
					EJBConnector ejbConn = new EJBConnector("default", "CCoilCarMvSeEJB", this);
					JDTORecord jrRst = (JDTORecord)ejbConn.trx("procCarBayInOrdReqTr", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
					
					jrRtn = commUtils.addSndData(jrRtn, jrRst);
				}				
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
	 * 코일이송하차도착PDA(DMYDR074)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvDMYDR074(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "코일이송하차도착PDA 수신[CCoilL3RcvSeEJB.rcvDMYDR074] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			//수신 항목 값
			String msgId    		= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydGp             = commUtils.trim(rcvMsg.getFieldString("YD_GP"          )); //야드구분
			String sTransOrdDt  	= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"   )); //운송실적일자
			String sTransOrdSeqno	= commUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO")); //운송실적순번
			String sCarKind		    = commUtils.trim(rcvMsg.getFieldString("CAR_KIND"       ));
			String sCarNo 			= commUtils.trim(rcvMsg.getFieldString("CAR_NO"         ));
			String sCardNo          = commUtils.trim(rcvMsg.getFieldString("CARD_NO"        ));
			String sCrFrtomoveGp	= commUtils.trim(rcvMsg.getFieldString("CR_FRTOMOVE_GP" )); //1 운송 2 이송
			String sWorkGp 			= commUtils.trim(rcvMsg.getFieldString("WORK_GP"        )); // 작업구분
			String sCarudPntCd		= commUtils.trim(rcvMsg.getFieldString("CARUD_PNT_CD"   )); // 상차포인트
//			String sTelNo           = commUtils.trim(rcvMsg.getFieldString("TEL_NO"         )); //전화번호
//			String sDriverName      = commUtils.trim(rcvMsg.getFieldString("DRIVER_NAME"    )); //운전기사명
			
			String sModifier 		= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			
			if ("".equals(sModifier)) { sModifier = msgId; }
		
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			
			/**********************************************************
			* 0. 수신 항목 값 Check
			**********************************************************/
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("TRANS_ORD_DATE" , sTransOrdDt   );
			jrParam.setField("TRANS_ORD_SEQNO", sTransOrdSeqno);
			jrParam.setField("CARD_NO"        , sCardNo       );
			jrParam.setField("CAR_NO"         , sCarNo        ); 						
			jrParam.setField("YD_AIM_RT_GP"   , "A1"          );
			jrParam.setField("STL_PROG_CD"    , "E"           );
			
			/***********************************
        	 * 1. 차량스케줄 중복 Check
        	 ***********************************/
			commUtils.printLog(logId, "1. 차량스케줄 중복 체크" , "SL");
			jrParam.setField("TRANS_ORD_DT"   , sTransOrdDt );
			jrParam.setField("CMBN_CARLD_YN"  , "N"         ); //복수차량여부
			/*
			SELECT *
			  FROM TB_YD_CARSCH  CS
			     , TB_YD_WRKBOOK WB
			 WHERE CS.TRANS_ORD_DATE  = :V_TRANS_ORD_DT
			   AND CS.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
			   AND CS.CAR_NO          = :V_CAR_NO
			   AND CS.CMBN_CARLD_YN   = :V_CMBN_CARLD_YN
			   AND CS.DEL_YN   = 'N'	
			   AND WB.DEL_YN   = 'N'	
			   AND CS.CAR_NO   = WB.CAR_NO
			   AND CS.CARD_NO  = WB.CARD_NO
			   AND WB.YD_WBOOK_ID = CASE WHEN CS.YD_CAR_PROG_STAT IN ('0','1','2') THEN YD_CARLD_WRK_BOOK_ID 
			                             ELSE YD_CARUD_WRK_BOOK_ID END  
			*/	   
			JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdCarYdCmbnCarldYn_PIDEV", logId, mthdNm, "차량정보 존재여부 체크");
			
			if (rsResult.size() > 0) {
				
				/*
				 * 도착전문에 중복 수신되면 Exception 처리함
				 */
				commUtils.printLog(logId, "이미 도착처리된 차량입니다." , "SL");
				
				throw new DAOException("이미 도착처리된 차량입니다.");
			}
			
			/**********************************************************
			* 1. 차량포인트 조회
			**********************************************************/
			jrParam.setField("YD_CARPNT_CD"    , sCarudPntCd);
			/*
			SELECT SUBSTR(YD_STK_COL_GP,1,4)||'0'||SUBSTR(YD_STK_COL_GP,6,1) AS YD_STK_COL_GP2
			     , DECODE(YD_STK_COL_ACT_STAT,'R','C',YD_STK_COL_ACT_STAT)   AS YD_STK_COL_ACT_STAT
			     , YD_CARPNT_CD
			     , DEL_YN
			     , YD_CAR_USETYPE_GP
			     , YD_GP
			     , YD_BAY_GP
			     , YD_STK_COL_GP
			     , TRN_EQP_CD
			     , CAR_NO
			     , CARD_NO
			     , WLOC_CD
			     , YD_PNT_CD
			     , YD_CARPNT_DESC
			     , YD_SPAN_FROM
			     , YD_SPAN_TO
			     , (SELECT ITEM1
			          FROM TB_YD_RULE 
			         WHERE REPR_CD_GP = 'J00005'
			           AND CD_GP      = 'J' --2열연 코일야드
			           AND ITEM       = '*' 
			           AND DEL_YN     = 'N' ) AS YD_BAYIN_WO_SEQ_YN --입동지시순번 적용여부
			  FROM TB_YD_CARPOINT A
			 WHERE YD_CARPNT_CD = :V_YD_CARPNT_CD
			   AND DEL_YN = 'N'
			 */
			JDTORecordSet jsCarPoint = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdCarPoint", logId, mthdNm, "차량스케줄 조회");
			
			if (jsCarPoint.size() != 1) {
				throw new Exception("차량포인트["+sCarudPntCd+"] 조회 실패");
			}
			
			String sCarNoOld  = commUtils.trim(jsCarPoint.getRecord(0).getFieldString("CAR_NO" )); //카번호
			String sCardNoOld = commUtils.trim(jsCarPoint.getRecord(0).getFieldString("CARD_NO")); //카드번호

			if (!"".equals(sCarNoOld) || !"".equals(sCardNoOld)) {
				if (!sCarNo.equals(sCarNoOld) || !sCardNo.equals(sCardNoOld)) {
					throw new Exception("차량포인트의 차량번호가 다릅니다. 차량포인트를 확인하세요");
				}
			}
			
			/**********************************************************
			* 2. 저장품 수정
			**********************************************************/
			/*
			UPDATE TB_YD_STOCK
			   SET MODIFIER = :V_MODIFIER
			     , MOD_DDTT = SYSDATE
			     , CAR_NO   = :V_CAR_NO
			     , CARD_NO  = :V_CARD_NO
			     , YD_AIM_RT_GP = NVL(:V_YD_AIM_RT_GP, YD_AIM_RT_GP)
			     , STL_PROG_CD  = NVL(:V_STL_PROG_CD , STL_PROG_CD)
			 WHERE TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
			   AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStock2", logId, mthdNm, "저장품 수정");
			
			/**********************************************************
			 * 3. 저장품이 적치된 저장위치 정보를 조회 
			 * - 저장위치가 04, 05, 06스판이면 A통로, 07스판이면 B통로로 처리한다.
			 **********************************************************/
			/*
			SELECT A.*
			     , (SELECT YD_CAR_SCH_ID 
			          FROM TB_YD_CARSCH D
			         WHERE D.CAR_NO = A.CAR_NO
			           AND D.TRANS_ORD_DATE  = A.TRANS_ORD_DATE
			           AND D.TRANS_ORD_SEQNO = A.TRANS_ORD_SEQNO
			           AND D.DEL_YN = 'N'
			           AND ROWNUM  <= 1) AS  YD_CAR_SCH_ID
			  FROM TB_YD_STOCK A
			 WHERE TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
			   AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
			   AND DEL_YN = 'N'
				 */                                                     				
			JDTORecordSet jsStock = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdStockTRANS_ORD_DAT", logId, mthdNm, "(운송일자, 운송순번)로 저장품 조회");

		    if (jsStock.size() <= 0) {
		    	commUtils.printLog(logId, "YD_STOCK[저장품] 조회 실패", "S-");
				return jrRtn;
		    }
			
		    String ydCarSchId = jsStock.getRecord(0).getFieldString("YD_CAR_SCH_ID");
		    
		  //작업구분(1:내수/2:수출/3:연안해송/9:HYSCO스케줄)
		    if ("9".equals(sWorkGp)) {
		    	
		    	jrParam.setField("YD_CARPNT_CD" , sCarudPntCd);
		    	jrParam.setField("YD_CAR_SCH_ID", ydCarSchId );
		    	
		    	/*
				SELECT SUBSTR(YD_STK_COL_GP,1,4)||'0'||SUBSTR(YD_STK_COL_GP,6,1) AS YD_STK_COL_GP2
				     , DECODE(YD_STK_COL_ACT_STAT,'R','C',YD_STK_COL_ACT_STAT)   AS YD_STK_COL_ACT_STAT
				     , YD_CARPNT_CD
				     , DEL_YN
				     , YD_CAR_USETYPE_GP
				     , YD_GP
				     , YD_BAY_GP
				     , YD_STK_COL_GP
				     , TRN_EQP_CD
				     , CAR_NO
				     , CARD_NO
				     , WLOC_CD
				     , YD_PNT_CD
				     , YD_CARPNT_DESC
				     , YD_SPAN_FROM
				     , YD_SPAN_TO
				  FROM TB_YD_CARPOINT A
				 WHERE YD_CARPNT_CD = :V_YD_CARPNT_CD
				   AND DEL_YN = 'N'
				 */
				JDTORecordSet jsCarpnt = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdCarPoint", logId, mthdNm, "개소코드 조회");
				if (jsCarpnt.size() > 0) {
					String sWlocCd         = commUtils.nvl(jsCarpnt.getRecord(0).getFieldString("WLOC_CD"            ), "");
					String ydPntCd         = commUtils.nvl(jsCarpnt.getRecord(0).getFieldString("YD_PNT_CD"          ), "");	
					String ydStkColActStat = commUtils.nvl(jsCarpnt.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT"), "");
					String ydStkColGp      = commUtils.nvl(jsCarpnt.getRecord(0).getFieldString("YD_STK_COL_GP"      ), "");
					String ydBayGp         = commUtils.nvl(jsCarpnt.getRecord(0).getFieldString("YD_BAY_GP"          ), "");
					
					commUtils.printLog(logId, "sWlocCd         : " + sWlocCd, "SL");
					commUtils.printLog(logId, "ydPntCd         : " + ydPntCd, "SL");
					commUtils.printLog(logId, "ydStkColActStat : " + ydStkColActStat, "SL");
					commUtils.printLog(logId, "ydStkColGp      : " + ydStkColGp, "SL");
					
					jrParam.setField("YD_STK_COL_GP", ydStkColGp);
					/*
					UPDATE
					(
					SELECT A.*
					     , B.STL_NO AS STL_NO2
					  FROM TB_YD_STKLYR     A
					     , TB_YD_CARFTMVMTL B
					 WHERE A.YD_STK_COL_GP = :V_YD_STK_COL_GP
					   AND B.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
					   AND B.YD_STK_BED_NO = A.YD_STK_BED_NO
					   AND A.YD_STK_LYR_NO = '001'
					)
					SET STL_NO = STL_NO2
					  , YD_STK_LYR_ACT_STAT = 'E'
					  , YD_STK_LYR_MTL_STAT = 'C'
					  , MODIFIER = :V_MODIFIER
					  , MOD_DDTT =  SYSDATE 
					 */
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStacklayer", logId, mthdNm, "차상위치 저장위치 등록");
					
					//차량스케줄에 차량진행상태 수정
					jrParam.setField("YD_CAR_PROG_STAT",     "B");
					jrParam.setField("YD_CARUD_ARR_DT", commUtils.getDateTime14()); //하차도착
					
					/*
					UPDATE TB_YD_CARSCH
					   SET MODIFIER         = :V_MODIFIER
					     , MOD_DDTT         = SYSDATE
					     , YD_CAR_PROG_STAT = :V_YD_CAR_PROG_STAT
					     , YD_CARUD_ST_DT   = TO_DATE(:V_YD_CARUD_ST_DT, 'YYYYMMDDHH24MISS')
					 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
					 */
					int nCnt = commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdCarschProgStat", logId, mthdNm, "차량진행상태 수정");
					
					if (nCnt <= 0) {
				    	commUtils.printLog(logId, "차량스케줄에 차량진행상태 수정 UPDATE Error", "S-");
						return jrRtn;
				    }
					
					//TR가 도착해 있는 경우 
					if ("TR".equals(sCarKind) && "L".equals(ydStkColActStat)) {
						ydStkColActStat = "C";
					}
					
					if ("C".equals(ydStkColActStat)) {

						JDTORecord jrYdMsg = commUtils.getParam(logId, mthdNm, sModifier);
						jrYdMsg.setField("TC_CODE"          , "YDYDJ653"    ); 
						//TODO CarMvHdSeEJB - procCoilGdsDistCarArrWr   -> procOutCarArrWr  코일제품출하차량도착실적처리(DMYDR036)
						jrYdMsg.setField("TC_CREATE_DDTT"	, commUtils.getDateTime14());
						jrYdMsg.setField("YD_GP"			, ydGp          );
						jrYdMsg.setField("TRANS_ORD_DATE"	, sTransOrdDt   );    
						jrYdMsg.setField("TRANS_ORD_SEQNO" 	, sTransOrdSeqno);
						jrYdMsg.setField("CAR_NO"			, sCarNo        );
						jrYdMsg.setField("CARD_NO"			, sCardNo       );
						jrYdMsg.setField("SPOS_WLOC_CD"		, sWlocCd       );	
						jrYdMsg.setField("SPOS_YD_PNT_CD"	, ydPntCd       );
						jrYdMsg.setField("CAR_KIND"			, sCarKind      );
						jrYdMsg.setField("WORK_GP"			, sWorkGp       );
						jrYdMsg.setField("YD_CARLD_STOP_LOC", ydStkColGp    );	//차량상차정지위치
						jrYdMsg.setField("CR_FRTOMOVE_GP"   , sCrFrtomoveGp );
						jrYdMsg.setField("YD_BAY_GP"        , ydBayGp       );
						jrYdMsg.setField("IS_EJB_CALL"		,"N"            );
						
						EJBConnector ejbConn = new EJBConnector("default", "CCoilCarMvSeEJB", this);
						JDTORecord jrRst = (JDTORecord)ejbConn.trx("procOutCarArrWr", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
						
						String sRtnCd = commUtils.trim(jrRst.getFieldString("RTN_CD" ));
						
						if (!"1".equals(sRtnCd)) {
							return jrRtn;
						}
						
						//2열연코일출하하차LOT편성전문
						//YDYDJ282 호출 CoilIssueWrkDmdSeEJB - procCoilGdsDistCarLdComp
//						EJBConnector ejbConn = new EJBConnector("default", "CCoilCarMvSeEJB", this);
						jrYdMsg = (JDTORecord)ejbConn.trx("procCoilGdsDistCarLdComp", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
						jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
						
					}
				}
		    	
		    } //if ("9".equals(sWorkGp)) {
		    
		    
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 * 코일이송하차완료PDA(DMYDR075) 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord rcvDMYDR075(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "코일이송하차완료PDA 수신[CCoilL3RcvSeEJB.rcvDMYDR075] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			//수신 항목 값
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydGp		    = commUtils.trim(rcvMsg.getFieldString("YD_GP"));
			String sStlAppearGp = commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP")); //재료외형
			String sStlNo       = commUtils.trim(rcvMsg.getFieldString("STL_NO"       )); //재료번호

			String sModifier 	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }

			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			
			String sMsg	= "";
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("STL_NO"        , sStlNo);
			jrParam.setField("STL_APPEAR_GP" , sStlAppearGp);
			
			jrParam.setField("YD_AIM_RT_GP"  , "M2");
			jrParam.setField("DEL_YN"        , "N" );
			
			/*
			SELECT A.*
			     , NVL(B.YD_STK_COL_GP,C.YD_STK_COL_GP) AS YD_STK_COL_GP
			     , B.YD_STK_BED_NO                AS YD_STK_BED_NO
			     , B.YD_STK_LYR_NO                AS YD_STK_LYR_NO
			  FROM TB_YD_STOCK  A
			     , TB_YD_STKLYR B
			     , TB_YD_STKCOL C
			 WHERE A.STL_NO  = :V_STL_NO
			   AND A.STL_NO  = B.STL_NO(+)
			   AND A.CARD_NO = C.CARD_NO(+) 
			 */                                                     				
			JDTORecordSet jsStock = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdStockJoinStkLyr2_PIDEV", logId, mthdNm, "차량정정보검색");
			
			if (jsStock.size() <= 0) {
				commUtils.printLog(logId, "YD_STOCK[코일제품출하완료] 조회 Error", "SL");	
				return jrRtn ;
			}

			String sCarNo    	  = commUtils.trim(jsStock.getRecord(0).getFieldString("CAR_NO"));
			String ydStkColGp 	  = commUtils.trim(jsStock.getRecord(0).getFieldString("YD_STK_COL_GP"));
			String sCardNo        = commUtils.trim(jsStock.getRecord(0).getFieldString("CARD_NO"));
			String sTransOrdDate  = commUtils.trim(jsStock.getRecord(0).getFieldString("TRANS_ORD_DATE"));
			String sTransOrdSeqno = commUtils.trim(jsStock.getRecord(0).getFieldString("TRANS_ORD_SEQNO"));

			jrParam.setField("DEL_YN"         , "N");
			jrParam.setField("STL_PROG_CD"    , "M");
			jrParam.setField("YD_AIM_RT_GP"   , "M2");
			
			//저장품 수정
			/*
			UPDATE TB_YD_STOCK
			   SET MODIFIER      = :V_MODIFIER
			     , MOD_DDTT      = SYSDATE
			     , STL_APPEAR_GP = :V_STL_APPEAR_GP
			     , YD_AIM_RT_GP  = :V_YD_AIM_RT_GP
			     , DEL_YN        = :V_DEL_YN     
			     , STL_PROG_CD   = :V_STL_PROG_CD
			 WHERE STL_NO = :V_STL_NO 
			*/
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStockCarldCmpl", logId, mthdNm, "저장품 수정");
			
			/***************************************************************************
			 *  저장품이 적치된 저장위치 정보를 조회
			 ***************************************************************************/
			sMsg = "[" + mthdNm + "]카드번호["+ sCardNo+"], 차량번호["+ sCardNo+"], 운송지시일자["+sTransOrdDate+"], 운송지시순번["+sTransOrdSeqno+"] : 출하완료된 동["+ydStkColGp+"]의 저장품들 조회 시작";
			commUtils.printLog(logId, sMsg, "SL");
			
			if ("*".equals(sStlAppearGp)) {
				
				jrParam.setField("CAR_NO" 			, sCarNo);
				
				//PIDEV
//				String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI0", "J", "*");
				
//				if("N".equals(sApplyYnPI)) {
//					jrParam.setField("CARD_NO"			, sCardNo      );
//				}
				
				jrParam.setField("TRANS_ORD_DATE"	, sTransOrdDate);
				jrParam.setField("TRANS_ORD_SEQNO"	, sTransOrdSeqno);
				
				/*
				SELECT *
				  FROM (
				        SELECT *
				          FROM TB_YD_CARSCH
				         WHERE CAR_NO       LIKE :V_CAR_NO||'%'
				           AND CARD_NO         = :V_CARD_NO
				           AND TRANS_ORD_DATE  = :V_TRANS_ORD_DT
				           AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
				         ORDER BY YD_CAR_SCH_ID DESC
				       ) A
				 WHERE ROWNUM <= 1   
		    	*/                                                     				
				JDTORecordSet jsCarsch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCarschTransDTSeq2_PIDEV", logId, mthdNm, "차량정정보검색");
				
				if (jsCarsch.size() <= 0 ) {
					sMsg="["+mthdNm+"] 차량스케쥴 조회 SELECT Error ::  DO NOT EXIST"  ;
					commUtils.printLog(logId, sMsg, "S-");	
					return jrRtn ;
				}
				
				JDTORecord sndL3Msg = commUtils.getParam(logId, mthdNm, sModifier);
				sndL3Msg.setField("TC_CODE"         , "DMYDR040");	//전문코드
				sndL3Msg.setField("CARD_NO"         , commUtils.trim(jsCarsch.getRecord(0).getFieldString("CARD_NO"     )));
				sndL3Msg.setField("CAR_NO"          , commUtils.trim(jsCarsch.getRecord(0).getFieldString("CAR_NO"      )));
				sndL3Msg.setField("SPOS_WLOC_CD"    , commUtils.trim(jsCarsch.getRecord(0).getFieldString("ARR_WLOC_CD" )));
				sndL3Msg.setField("SPOS_YD_PNT_CD"  , commUtils.trim(jsCarsch.getRecord(0).getFieldString("YD_PNT_CD3"  )));
				sndL3Msg.setField("YD_GP"           , ydGp          );
				sndL3Msg.setField("TRANS_ORD_DT"    , commUtils.trim(jsCarsch.getRecord(0).getFieldString("TRANS_ORD_DATE" )));
				sndL3Msg.setField("TRANS_ORD_SEQNO" , commUtils.trim(jsCarsch.getRecord(0).getFieldString("TRANS_ORD_SEQNO")));
				if ("".equals(sCardNo)) {
					sCardNo = "XXXXX";
				}
				sndL3Msg.setField("MSG_ID"			, msgId    );
				sMsg = "차량번호[" + sCarNo + "]는 자동차량출발";
				commUtils.printLog(logId, sMsg, "SL");
				
				EJBConnector ejbConn = new EJBConnector("default", "CCoilCarMvSeEJB", this); //TODO 호출로직 점검필요
				JDTORecord jrRtn1 = (JDTORecord)ejbConn.trx("procOutCarLevWr", new Class[] { JDTORecord.class }, new Object[] { sndL3Msg });
				jrRtn = commUtils.addSndData(jrRtn, jrRtn1);
				
			} //if ("*".equals(sStlAppearGp)) {
			
			/**********************************************************
			* 2. 저장품 제원 야드L2 전송 (YDY5L002)
			**********************************************************/				
			JDTORecord sndL2Msg = commUtils.getParam(logId, mthdNm, sModifier);
			sndL2Msg.setField("JMS_TC_CD"			, "YDY5L002");
			sndL2Msg.setField("YD_INFO_SYNC_CD"		, "5");	//1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)
			sndL2Msg.setField("STL_NO"		        , sStlNo);
			
			jrRtn = commUtils.addSndData(jrRtn ,coilDao.getMsgL2("YDY5L002", sndL2Msg));
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}	
	
	
	
	/**	
	 * [A] 오퍼레이션명 : 2열연압연생산실적 수신 (HRYDJ003)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord rcvHRYDJ003(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "2열연압연생산실적 수신[CCoilL3RcvSeEJB.rcvHRYDJ003] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			//수신 항목 값
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sStlNo       = commUtils.trim(rcvMsg.getFieldString("STL_NO"     )); //재료번호 
//			String sCOIL_INDIA  = commUtils.trim(rcvMsg.getFieldString("COIL_INDIA" )); //내경
//			String sCOIL_OUTDIA = commUtils.trim(rcvMsg.getFieldString("COIL_OUTDIA")); //외경

			String sModifier 	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }
			
			String sMsg         = "";

			JDTORecord jrCoilComm = JDTORecordFactory.getInstance().create();
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			
			JDTORecord jrYdStock = commUtils.getParam(logId, mthdNm, sModifier);
			
			/**********************************************************
			* 0. 수신 항목 값 Check
			**********************************************************/  			
			if ("".equals(sStlNo)) {
				throw new Exception("재료번호가 없습니다.");
//				return jrRtn;
			}
			
			/**********************************************************
			* 1. 저장품 등록
			**********************************************************/  
			
			jrParam.setField("COIL_NO", sStlNo);
			/*
			SELECT *
			  FROM USRPTA.TB_PT_COILCOMM 
			 WHERE COIL_NO = :V_COIL_NO
			 */
			JDTORecordSet jsCoilcomm = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getPtCoilComm", logId, mthdNm, "코일공통조회");
			
			if (jsCoilcomm.size() <= 0) {
				sMsg = "코일공통 DATA 없음";
				commUtils.printLog(logId, mthdNm + sMsg, "S-");
				return jrRtn;
			}
			
			jrCoilComm = jsCoilcomm.getRecord(0);
			
			jrParam.setField("STL_NO", sStlNo);
			JDTORecordSet jsYdStock = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYdStock", logId, mthdNm, "저장품조회");
			
			if (jsYdStock.size() < 0) {
				throw new Exception("저장품 조회중 Error.");
			}
			
			else {
				
				String sYD_MTL_ITEM	 = ""; 
				double dW_GP         = 0;
				String sW_GP         = "";
				int    iOUTDIA       = 0;
				String sOUTDIA       = "";
				
				
				jrParam.setField("CURR_PROG_CD", jrCoilComm.getFieldString("CURR_PROG_CD"));
				String[] rVal = new String[1];
				//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
				rVal = coilDao.getYdAimRtGp("C", jrParam);
				
				//실적발생 후 진도코드에 따른 목표야드 변경 작업
				if (rVal[1].equals("H") ||rVal[1].equals("G") ||rVal[1].equals("F")) {
					sYD_MTL_ITEM = "CG";
				} else {
					sYD_MTL_ITEM = "CM";
				}
				
				jrYdStock.setField("STL_NO"                 , commUtils.nvl(jrCoilComm.getFieldString("COIL_NO"       ), ""));
				jrYdStock.setField("YD_MTL_T"               , commUtils.nvl(jrCoilComm.getFieldString("COIL_T"        ), "")); 
				jrYdStock.setField("YD_MTL_W"               , commUtils.nvl(jrCoilComm.getFieldString("COIL_W"        ), ""));     
				jrYdStock.setField("YD_MTL_L"               , commUtils.nvl(jrCoilComm.getFieldString("COIL_LEN"      ), ""));
				jrYdStock.setField("YD_MTL_WT"              , commUtils.nvl(jrCoilComm.getFieldString("COIL_WT"       ), ""));
				jrYdStock.setField("COIL_INDIA"             , commUtils.nvl(jrCoilComm.getFieldString("COIL_INDIA"    ), ""));
				jrYdStock.setField("COIL_OUTDIA"            , commUtils.nvl(jrCoilComm.getFieldString("COIL_OUTDIA"   ), ""));
				jrYdStock.setField("PLNT_PROC_CD"           , commUtils.nvl(jrCoilComm.getFieldString("PLNT_PROC_CD"  ), ""));
				jrYdStock.setField("ORD_YEOJAE_GP"          , commUtils.nvl(jrCoilComm.getFieldString("ORD_YEOJAE_GP" ), ""));
				jrYdStock.setField("ORD_NO"                 , commUtils.nvl(jrCoilComm.getFieldString("ORD_NO"        ), "")); 
				jrYdStock.setField("ORD_DTL"                , commUtils.nvl(jrCoilComm.getFieldString("ORD_DTL"       ), "")); 
				jrYdStock.setField("HYSCO_TRANS_GP"         , commUtils.nvl(jrCoilComm.getFieldString("HYSCO_TRANS_GP"), ""));
				jrYdStock.setField("COOL_METHOD"            , commUtils.nvl(jrCoilComm.getFieldString("COOL_METHOD"   ), ""));
				jrYdStock.setField("COOL_DONE_GP"           , commUtils.nvl(jrCoilComm.getFieldString("COOL_DONE_GP"  ), ""));
				jrYdStock.setField("YD_CONVEYOR_BRANCH_CD"  , commUtils.nvl(jrCoilComm.getFieldString("BRANCH_CD"     ), ""));
				jrYdStock.setField("CUST_CD"                , commUtils.nvl(jrCoilComm.getFieldString("CUST_CD"       ), "")); 
				jrYdStock.setField("DEMANDER_CD"            , commUtils.nvl(jrCoilComm.getFieldString("DEMANDER_CD"   ), ""));
				jrYdStock.setField("HCR_GP"                 , commUtils.nvl(jrCoilComm.getFieldString("HCR_GP"        ), ""));
				jrYdStock.setField("ITEMNAME_CD"            , commUtils.nvl(jrCoilComm.getFieldString("ITEMNAME_CD"   ), ""));
				jrYdStock.setField("PTOP_PLNT_GP"           , commUtils.nvl(jrCoilComm.getFieldString("PTOP_PLNT_GP"  ), ""));

				jrYdStock.setField("STL_PROG_CD"			, commUtils.nvl(jrCoilComm.getFieldString("STL_PROG_CD"   ), ""));
				jrYdStock.setField("STL_APPEAR_GP"			, commUtils.nvl(jrCoilComm.getFieldString("STL_APPEAR_GP" ), ""));
				jrYdStock.setField("YD_MTL_ITEM"			, sYD_MTL_ITEM);
				jrYdStock.setField("YD_AIM_YD_GP"			, "J");	
				jrYdStock.setField("YD_AIM_RT_GP"           , rVal[0]);
				
				//폭구분
				dW_GP = Double.parseDouble(commUtils.nvl(jrCoilComm.getFieldString("COIL_W" ), "0"));
				commUtils.printLog(logId, "dW_GP:" + dW_GP, "SL");

				if (dW_GP < 1601) {
					sW_GP = "M";
				} else {
					sW_GP = "L";
				}
				jrYdStock.setField("YD_MTL_W_GP"           , sW_GP);
				
				//외경그룹
				iOUTDIA = Integer.parseInt(commUtils.nvl(jrCoilComm.getFieldString("COIL_OUTDIA" ), "0"));
				commUtils.printLog(logId, "iOUTDIA:" + iOUTDIA, "SL");
				if (iOUTDIA <= 1280) {
					sOUTDIA = "A";
				} else if (( iOUTDIA > 1280 )&&( iOUTDIA <= 1930)) { 
					sOUTDIA = "B";
				} else if ( iOUTDIA > 1930 ) {
					sOUTDIA = "C";
				}
				jrYdStock.setField("YD_COIL_OUTDIA_GRP_GP"	, sOUTDIA);
				
				if (jsYdStock.size() == 0) { //insert
					
					commDao.insert(jrYdStock, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.insYdStock", logId, mthdNm, "저장품등록");
				
				} else { //update
					
					commDao.update(jrYdStock, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.updYdStock", logId, mthdNm, "저장품수정");
				}
			}
			
			/**********************************************************
			* 2. 저장품 제원 야드L2 전송 (YDY5L002)
			**********************************************************/
			JDTORecord jDrd = JDTORecordFactory.getInstance().create();
			jDrd.setField("JMS_TC_CD"			, "YDY5L002");
			jDrd.setField("YD_INFO_SYNC_CD"		, "A");	//1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)
			jDrd.setField("STL_NO"		        , sStlNo);
			jDrd.setField("YD_STK_COL_GP"		, "J");
			
			jrRtn = commUtils.addSndData(jrRtn ,coilDao.getMsgL2("YDY5L002", jDrd));
			
			commUtils.printLog(logId, mthdNm, "S-");
			
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**	
	 * [A] 오퍼레이션명 : 2열연압연작업실적 수신 (HRYDJ004)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord rcvHRYDJ004(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "2열연압연작업실적 수신[CCoilL3RcvSeEJB.rcvHRYDJ004] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			//수신 항목 값
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sStlNo       = commUtils.trim(rcvMsg.getFieldString("STL_NO"     )); //재료번호 
//			String sCOIL_INDIA  = commUtils.trim(rcvMsg.getFieldString("COIL_INDIA" )); //내경
//			String sCOIL_OUTDIA = commUtils.trim(rcvMsg.getFieldString("COIL_OUTDIA")); //외경

			String sModifier 	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }
			
			String sMsg         = "";

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			
			JDTORecord jrYdStock = commUtils.getParam(logId, mthdNm, sModifier);
			
			/**********************************************************
			* 0. 수신 항목 값 Check
			**********************************************************/  			
			if ("".equals(sStlNo)) {
				throw new Exception("재료번호가 없습니다.");
//				return jrRtn;
			}
			
			/**********************************************************
			* 1. 저장품 등록
			**********************************************************/  
			
			jrParam.setField("COIL_NO", sStlNo);
			/*
			SELECT *
			  FROM USRPTA.TB_PT_COILCOMM 
			 WHERE COIL_NO = :V_COIL_NO
			 */
			JDTORecordSet jsCoilcomm = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getPtCoilComm", logId, mthdNm, "코일공통조회");
			
			if (jsCoilcomm.size() <= 0) {
				sMsg = "코일공통 DATA 없음";
				commUtils.printLog(logId, mthdNm + sMsg, "S-");
				return jrRtn;
			}
			
			JDTORecord jrCoilComm = jsCoilcomm.getRecord(0);
			
			jrParam.setField("STL_NO", sStlNo);
			JDTORecordSet rsYdStock = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYdStock", logId, mthdNm, "저장품조회");
			
			if (rsYdStock.size() < 0) {
				throw new Exception("저장품 조회중 Error.");
			}
			
			else {
				
				String ydMtlItem	 = ""; 
				String ydAimYdGp 	 = "";
				double dW_GP         = 0;
				String sW_GP         = "";
				int    iOutDia       = 0;
				String sOutDia       = "";
				
				
				jrParam.setField("CURR_PROG_CD", jrCoilComm.getFieldString("CURR_PROG_CD"));
				String[] rVal = new String[1];
				//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
				rVal= coilDao.getYdAimRtGp("C", jrParam);
				
				//실적발생 후 진도코드에 따른 목표야드 변경 작업
				if (rVal[1].equals("H") ||rVal[1].equals("G") ||rVal[1].equals("F")) {
					ydMtlItem = "CG";
				} else {
					ydMtlItem = "CM";
				}
				
				jrYdStock.setField("STL_NO"                 , commUtils.nvl(jrCoilComm.getFieldString("COIL_NO"       ), ""));
				jrYdStock.setField("YD_MTL_T"               , commUtils.nvl(jrCoilComm.getFieldString("COIL_T"        ), "")); 
				jrYdStock.setField("YD_MTL_W"               , commUtils.nvl(jrCoilComm.getFieldString("COIL_W"        ), ""));     
				jrYdStock.setField("YD_MTL_L"               , commUtils.nvl(jrCoilComm.getFieldString("COIL_LEN"      ), ""));
				jrYdStock.setField("YD_MTL_WT"              , commUtils.nvl(jrCoilComm.getFieldString("COIL_WT"       ), ""));
				jrYdStock.setField("COIL_INDIA"             , commUtils.nvl(jrCoilComm.getFieldString("COIL_INDIA"    ), ""));
				jrYdStock.setField("COIL_OUTDIA"            , commUtils.nvl(jrCoilComm.getFieldString("COIL_OUTDIA"   ), ""));
				jrYdStock.setField("PLNT_PROC_CD"           , commUtils.nvl(jrCoilComm.getFieldString("PLNT_PROC_CD"  ), ""));
				jrYdStock.setField("ORD_YEOJAE_GP"          , commUtils.nvl(jrCoilComm.getFieldString("ORD_YEOJAE_GP" ), ""));
				jrYdStock.setField("ORD_NO"                 , commUtils.nvl(jrCoilComm.getFieldString("ORD_NO"        ), "")); 
				jrYdStock.setField("ORD_DTL"                , commUtils.nvl(jrCoilComm.getFieldString("ORD_DTL"       ), "")); 
				jrYdStock.setField("HYSCO_TRANS_GP"         , commUtils.nvl(jrCoilComm.getFieldString("HYSCO_TRANS_GP"), ""));
				jrYdStock.setField("COOL_METHOD"            , commUtils.nvl(jrCoilComm.getFieldString("COOL_METHOD"   ), ""));
				jrYdStock.setField("COOL_DONE_GP"           , commUtils.nvl(jrCoilComm.getFieldString("COOL_DONE_GP"  ), ""));
				jrYdStock.setField("YD_CONVEYOR_BRANCH_CD"  , commUtils.nvl(jrCoilComm.getFieldString("BRANCH_CD"     ), ""));
				jrYdStock.setField("CUST_CD"                , commUtils.nvl(jrCoilComm.getFieldString("CUST_CD"       ), "")); 
				jrYdStock.setField("DEMANDER_CD"            , commUtils.nvl(jrCoilComm.getFieldString("DEMANDER_CD"   ), ""));
				jrYdStock.setField("HCR_GP"                 , commUtils.nvl(jrCoilComm.getFieldString("HCR_GP"        ), ""));
				jrYdStock.setField("ITEMNAME_CD"            , commUtils.nvl(jrCoilComm.getFieldString("ITEMNAME_CD"   ), ""));
				jrYdStock.setField("PTOP_PLNT_GP"           , commUtils.nvl(jrCoilComm.getFieldString("PTOP_PLNT_GP"  ), ""));

				jrYdStock.setField("STL_PROG_CD"			, commUtils.nvl(jrCoilComm.getFieldString("STL_PROG_CD"   ), ""));
				jrYdStock.setField("STL_APPEAR_GP"			, commUtils.nvl(jrCoilComm.getFieldString("STL_APPEAR_GP" ), ""));
				jrYdStock.setField("YD_MTL_ITEM"			, ydMtlItem);
				jrYdStock.setField("YD_AIM_YD_GP"			, "J");	
				jrYdStock.setField("YD_AIM_RT_GP"           , rVal[0]);
				
				//폭구분
				dW_GP = Double.parseDouble(commUtils.nvl(jrCoilComm.getFieldString("COIL_W" ), "0"));
				if (dW_GP < 1601) {
					sW_GP = "M";
				} else {
					sW_GP = "L";
				}
				jrYdStock.setField("YD_MTL_W_GP"           , sW_GP);
				
				//외경그룹
				iOutDia = Integer.parseInt(commUtils.nvl(jrCoilComm.getFieldString("COIL_OUTDIA" ), "0"));
				if (iOutDia <= 1280) {
					sOutDia = "A";
				} else if (( iOutDia > 1280 )&&( iOutDia <= 1930)) { 
					sOutDia = "B";
				} else if ( iOutDia > 1930 ) {
					sOutDia = "C";
				}
				jrYdStock.setField("YD_COIL_OUTDIA_GRP_GP"	, sOutDia);
				
				if (rsYdStock.size() == 0) { //insert
					
					commDao.insert(jrYdStock, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.insYdStock", logId, mthdNm, "저장품등록");
				
				} else { //update
					
					commDao.update(jrYdStock, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.updYdStock", logId, mthdNm, "저장품수정");
				}
			}
			
			/**********************************************************
			* 2. 저장품 제원 야드L2 전송 (YDY5L002)
			**********************************************************/
			JDTORecord jDrd = JDTORecordFactory.getInstance().create();
			jDrd.setField("JMS_TC_CD"			, "YDY5L002");
			jDrd.setField("YD_INFO_SYNC_CD"		, "5");	//1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)
			jDrd.setField("STL_NO"		        , sStlNo);
			jDrd.setField("YD_STK_COL_GP"		, "");
			jDrd.setField("YD_STK_BED_NO"       , "");
			
			jrRtn = commUtils.addSndData(jrRtn ,coilDao.getMsgL2("YDY5L002", jDrd));
			
			commUtils.printLog(logId, mthdNm, "S-");
			
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}	
	
	
	/**	
	 * [A] 오퍼레이션명 : 2열연정정작업지시 수신 (HRYDJ005)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord rcvHRYDJ005(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "2열연정정작업지시 수신[CCoilL3RcvSeEJB.rcvHRYDJ005] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			//수신 항목 값
			String msgId         = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sWordUnitName = commUtils.trim(rcvMsg.getFieldString("WORD_UNIT_NAME")); //작업지시단위명 
			String sModifier 	 = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }
			
			String sMsg = "";

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			
			/**********************************************************
			* 0. 수신 항목 값 Check
			**********************************************************/  			
			if ("".equals(sWordUnitName)) {
				throw new Exception("작업지시 단위명이 없습니다.");
//				return jrRtn;
			}
			
			/**********************************************************
			* 1. 목표행선 수정
			**********************************************************/  	
			jrParam.setField("WORD_UNIT_NAME", sWordUnitName);
			/*
			SELECT ST.STL_NO       AS STL_NO
				 , ST.STL_PROG_CD  AS STL_PROG_CD
				 , PT.CURR_PROG_CD AS CURR_PROG_CD
				 , PT.PLNT_PROC_CD AS PLNT_PROC_CD
			  FROM TB_YD_STOCK ST
				 ,(SELECT D.COIL_NO
					    , D.CURR_PROG_CD
					    , D.PLNT_PROC_CD
			         FROM TB_PT_COILCOMM D
					    ,(SELECT COIL_NO
			    	  	    FROM TB_HR_C_SHEARWOWR
			               WHERE WORD_UNIT_NAME = :V_WORD_UNIT_NAME
			                 AND WORK_STAT      IN ('2','B','E','R','X'))  C
				    WHERE C.COIL_NO = D.COIL_NO) PT
			 WHERE ST.STL_NO       = PT.COIL_NO
			   AND ST.STL_PROG_CD <> PT.CURR_PROG_CD
			 */
			JDTORecordSet rsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdStockHrCShear", logId, mthdNm, "저장품 조회");
			
			if (rsRst.size() <= 0) {
				sMsg = "정정작업지시를 이미 수신 하였습니다.";
				commUtils.printLog(logId, sMsg, "SL");
				return jrRtn;
			}
			
			for (int ii = 0; ii < rsRst.size(); ++ii) {
				
				String[] rVal = new String[1];
				//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
				rVal= coilDao.getYdAimRtGp("C", jrParam);
				
				jrParam.setField("YD_AIM_RT_GP"	, rVal[0]);	
				jrParam.setField("STL_PROG_CD"  , rsRst.getRecord(ii).getFieldString("CURR_PROG_CD"));
				jrParam.setField("STL_NO"       , rsRst.getRecord(ii).getFieldString("STL_NO"));
				
				/*
				UPDATE TB_YD_STOCK
				   SET MODIFIER               = :V_MODIFIER
				     , MOD_DDTT               = SYSDATE
				     , STL_PROG_CD            = :V_STL_PROG_CD
				     , YD_AIM_RT_GP           = :V_YD_AIM_RT_GP
				 WHERE STL_NO = :V_STL_NO 
				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.updYdStockStlProgCd", logId, mthdNm, "목표행선 수정");
				
			} //end for
			
			commUtils.printLog(logId, mthdNm, "S-");
			
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**	
	 * [A] 오퍼레이션명 : 2열연정정지시결번실적 수신 (HRYDJ006)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord rcvHRYDJ006(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "2열연정정지시결번실적 수신[CCoilL3RcvSeEJB.rcvHRYDJ006] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			//수신 항목 값
			String msgId  = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sStlNo    = commUtils.trim(rcvMsg.getFieldString("STL_NO")); // 
			String sModifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }
			
			String sMsg = "";

		
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			
			/**********************************************************
			* 0. 수신 항목 값 Check
			**********************************************************/  			
			if ("".equals(sStlNo)) {
				throw new Exception("재료번호가 없습니다.");
//				return jrRtn;
			}
			
			/**********************************************************
			* 1. 저장품 조회
			**********************************************************/  	
			jrParam.setField("STL_NO", sStlNo);
			
			JDTORecordSet jsStock = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYdStock", logId, mthdNm, "저장품 조회");
			if (jsStock.size() <= 0) {
				sMsg = "저장품 조회 실패";
		      	commUtils.printLog(logId, sMsg, "S-");
		        return jrRtn;
			}
			
			jsStock.first();
			JDTORecord jrStock = jsStock.getRecord();
			
			/**********************************************************
			* 2. 작업예약 조회
			**********************************************************/
			/*
			SELECT A.YD_WBOOK_ID         AS YD_WBOOK_ID
			     , A.DEL_YN              AS DEL_YN
			     , A.YD_GP               AS YD_GP
			     , A.YD_BAY_GP           AS YD_BAY_GP
			     , A.YD_SCH_CD           AS YD_SCH_CD
			     , A.YD_SCH_PRIOR        AS YD_SCH_PRIOR
			     , A.YD_SCH_PROG_STAT    AS YD_SCH_PROG_STAT
			     , A.YD_SCH_ST_GP        AS YD_SCH_ST_GP
			     , A.YD_SCH_REQ_GP       AS YD_SCH_REQ_GP
			     , A.YD_AIM_YD_GP        AS YD_AIM_YD_GP
			     , A.YD_AIM_BAY_GP       AS YD_AIM_BAY_GP
			     , A.YD_CTS_RELAY_YN     AS YD_CTS_RELAY_YN
			     , A.YD_CTS_RELAY_BAY_GP AS YD_CTS_RELAY_BAY_GP
			     , A.YD_TO_LOC_DCSN_MTD  AS YD_TO_LOC_DCSN_MTD
			     , A.YD_TO_LOC_GUIDE     AS YD_TO_LOC_GUIDE
			     , B.STL_NO              AS STL_NO
			     , B.YD_STK_COL_GP       AS YD_STK_COL_GP
			     , B.YD_STK_BED_NO       AS YD_STK_BED_NO
			     , B.YD_STK_LYR_NO       AS YD_STK_LYR_NO
			     , B.YD_UP_COLL_SEQ      AS YD_UP_COLL_SEQ
			  FROM TB_YD_WRKBOOK    A
			     , TB_YD_WRKBOOKMTL B
			 WHERE A.YD_WBOOK_ID = B.YD_WBOOK_ID
			   AND B.STL_NO = :V_STL_NO
			   AND A.DEL_YN = 'N'
			   AND B.DEL_YN = 'N'
			 */
			JDTORecordSet jsWrkBook = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYdWrkbookmtlSTLNO", logId, mthdNm, "작업예약 조회");
			
			if (jsWrkBook.size() <= 0) {
				sMsg = "["+sStlNo+"] 해당 재료번호 작업예약 없음";
		      	commUtils.printLog(logId, sMsg, "S-");
		        return jrRtn;
			}
			
			/**********************************************************
			* 3. 스케쥴코드가 정정보급의 결번처리일 경우 처리
			**********************************************************/
			String ydSchCd     = commUtils.trim(jsWrkBook.getRecord(0).getFieldString("YD_SCH_CD"    ));
			String ydStkColGp  = commUtils.trim(jsWrkBook.getRecord(0).getFieldString("YD_STK_COL_GP"));
			String ydStkBedNo  = commUtils.trim(jsWrkBook.getRecord(0).getFieldString("YD_STK_BED_NO"));
			String ydStkLyrNo  = commUtils.trim(jsWrkBook.getRecord(0).getFieldString("YD_STK_LYR_NO"));
			String ydWbookId   = commUtils.trim(jsWrkBook.getRecord(0).getFieldString("YD_WBOOK_ID"  ));
			
			jrParam.setField("YD_WBOOK_ID", ydWbookId);
			jrParam.setField("YD_SCH_CD"  , ydSchCd  );
			
			/*스케줄코드가 보급/직보급
			 * 		크레인스케줄 상태 W, S, 1인경우 스케줄 취소
			 * 		아니면 나둠
			 * 보급이 아니면
			 * 		저장품 수정
			 */
			
			if (
			    "JBKE01UH".equals(ydSchCd) || "JBKD01UH".equals(ydSchCd) || "JBFE01UH".equals(ydSchCd) || 
			    "JAKE01UH".equals(ydSchCd) || "JAKD01UH".equals(ydSchCd) ||           
			    "JBTC01MH".equals(ydSchCd) || "JBTC02MH".equals(ydSchCd) ||           
			    "JCKE01UH".equals(ydSchCd) || "JCKD01UH".equals(ydSchCd) || "JCFE01UH".equals(ydSchCd) ||
			    "JCTC01MH".equals(ydSchCd) || "JCTC02MH".equals(ydSchCd) ||           
			    "JDFE01UH".equals(ydSchCd) || "JDTC01MH".equals(ydSchCd) || "JDTC02MH".equals(ydSchCd) ||
			    "JEDE01UH".equals(ydSchCd) || "JEDD01UH".equals(ydSchCd) ||           
			    "JETC01MH".equals(ydSchCd) || "JETC02MH".equals(ydSchCd) ||           
			    "JFFE01UH".equals(ydSchCd) || "JFTC01MH".equals(ydSchCd) || "JFTC02MH".equals(ydSchCd) ||
			    "JGFE01UH".equals(ydSchCd) || "JGTC01MH".equals(ydSchCd) || "JGTC02MH".equals(ydSchCd) ||
			    "JHKE01UH".equals(ydSchCd) || "JHKD01UH".equals(ydSchCd) ||
			    "JHTC01MH".equals(ydSchCd) || "JHTC02MH".equals(ydSchCd) ){
				
				/*
				SELECT A.YD_CRN_SCH_ID
				     , A.YD_WBOOK_ID
				     , A.YD_BAY_GP
				     , A.YD_SCH_CD
				     , A.YD_WRK_PROG_STAT
				     , A.YD_EQP_WRK_STAT
				     , B.STL_NO
				     , B.YD_AID_WRK_YN
				     , B.YD_STK_LYR_NO
				     , B.YD_MTL_ITEM
				  FROM TB_YD_CRNSCH    A
				     , TB_YD_CRNWRKMTL B
				 WHERE A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID
				   AND A.YD_WBOOK_ID   = :V_YD_WBOOK_ID
				   AND B.STL_NO = :V_STL_NO
				   AND A.DEL_YN = 'N'
				   AND B.DEL_YN = 'N'
				 */
				JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdCrnschYdCrnWrkMtlByWBookIdSTLNo", logId, mthdNm, "크레인 스케줄 조회");
				if (jsCrnSch.size() > 0) {
					
				String ydCrnSchId    = commUtils.trim(jsCrnSch.getRecord(0).getFieldString("YD_CRN_SCH_ID"   ));
				String ydWrkProgStat = commUtils.trim(jsCrnSch.getRecord(0).getFieldString("YD_WRK_PROG_STAT"));
				
				jrParam.setField("YD_CRN_SCH_ID"   , ydCrnSchId);
				jrParam.setField("YD_WRK_PROG_STAT", ydWrkProgStat);
				
				/**********************************************************
				* 3. 스케쥴 취소
				**********************************************************/
				EJBConnector ejbConn1 = new EJBConnector("default", "CCoilJspSeEJB", this);
				JDTORecord jrRtn1 = (JDTORecord)ejbConn1.trx("trtCrnSchCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				
				jrRtn = commUtils.addSndData(jrRtn, jrRtn1);
				
				}
			}
					
			
			/**********************************************************
			* 4. 작업예약 삭제
			**********************************************************/
			EJBConnector ejbConn2 = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrRtn2 = (JDTORecord)ejbConn2.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			
			jrRtn = commUtils.addSndData(jrRtn, jrRtn2);
			
			
			/**********************************************************
			* 9. 저장품 제원 야드L2 전송 (YDY5L002)
			**********************************************************/
			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setField("JMS_TC_CD"			, "YDY5L002");
			jrYdMsg.setField("YD_INFO_SYNC_CD"		, "5");	//1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)
			jrYdMsg.setField("STL_NO"		        , sStlNo);
			jrYdMsg.setField("YD_STK_COL_GP"		, "");
			jrYdMsg.setField("YD_STK_BED_NO"        , "");
			
			jrRtn = commUtils.addSndData(jrRtn ,coilDao.getMsgL2("YDY5L002", jrYdMsg));
			
			
			commUtils.printLog(logId, mthdNm, "S-");
			
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}	
	
	
	/**	
	 * [A] 오퍼레이션명 : 2열연정정작업실적 수신 (HRYDJ007)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord rcvHRYDJ007(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "2열연정정작업실적 수신[CCoilL3RcvSeEJB.rcvHRYDJ007] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			//수신 항목 값
			String msgId   			= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sStlNo  			= commUtils.trim(rcvMsg.getFieldString("STL_NO"            )); //재료번호 
			// 전문 항목에 없음 LHJ
			String sLocChangeYn     = commUtils.trim(rcvMsg.getFieldString("LOC_CHANGE_YN"     )); //야드위치 변경대상 여부(Y일 경우 야드에서 해당)
			String sLocChangeCoilNo = commUtils.trim(rcvMsg.getFieldString("LOC_CHANGE_COIL_NO")); //야드위치 변경대상 코일(모코일)
		
			String sModifier 	    = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }
			
			String sMsg = "";
			
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			
			String ydBayGp    = "A";
			String ydEqpGp    = "";
			String ydStkColGp = "";
			String ydStkBedNo = "";
			String ydStkLyrNo = "";
			
			/**********************************************************
			* 0. 수신 항목 값 Check
			**********************************************************/  			
			if ("".equals(sStlNo)) {
				throw new Exception("재료번호가 없습니다.");
//				return jrRtn;
			}
			
			/**********************************************************
			* 1. 모코일 대체작업
			**********************************************************/  	
			jrParam.setField("STL_NO"            , sStlNo);
			jrParam.setField("LOC_CHANGE_COIL_NO", sLocChangeCoilNo);	
			
			if ("Y".equals(sLocChangeYn)) {
				
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStklyrChangeStlNo", logId, mthdNm, "모코일 대체 작업");
				
				commDao.updateTx(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updCoilcommChangeStlNo", logId, mthdNm, "모코일 대체 작업2");
			}
			
			/**********************************************************
			* 2. 저장위치 삭제
			**********************************************************/
			/*
			SELECT A.YD_STK_COL_GP        AS YD_STK_COL_GP
			     , A.YD_STK_BED_NO        AS YD_STK_BED_NO
			     , A.YD_STK_LYR_NO        AS YD_STK_LYR_NO     
			     , A.STL_NO               AS STL_NO
			     , A.YD_STK_LYR_ACT_STAT  AS YD_STK_LYR_ACT_STAT
			     , A.YD_STK_LYR_MTL_STAT  AS YD_STK_LYR_MTL_STAT
			     , C.YD_LOC_GP            AS YD_LOC_GP
			     , C.YD_EQP_GP            AS YD_EQP_GP
			     , C.YD_BAY_GP            AS YD_BAY_GP
			  FROM TB_YD_STKLYR    A
			     , TB_YD_STOCK     B
			     , TB_YD_STKCOL    C
			 WHERE A.STL_NO        = B.STL_NO
			   AND A.STL_NO        = :V_STL_NO
			   AND A.DEL_YN        = 'N'
			   AND A.YD_STK_COL_GP = C.YD_STK_COL_GP
			 */
			JDTORecordSet jsLoc = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getCoilYdStkPosInfo_PIDEV", logId, mthdNm, "저장위치 조회");
			if (jsLoc.size() > 0) {
				jsLoc.first();
//				String sYD_LOC_GP     = rsLoc.getRecord().getFieldString("YD_LOC_GP"    );//소재제품장 구분
				ydBayGp     = jsLoc.getRecord().getFieldString("YD_BAY_GP"    );//
				ydEqpGp     = jsLoc.getRecord().getFieldString("YD_EQP_GP"    );//
				ydStkColGp  = jsLoc.getRecord().getFieldString("YD_STK_COL_GP");
				ydStkBedNo = jsLoc.getRecord().getFieldString("YD_STK_BED_NO");
				ydStkLyrNo = jsLoc.getRecord().getFieldString("YD_STK_LYR_NO");
				
				jrParam.setField("YD_STK_COL_GP"       , ydStkColGp);
				jrParam.setField("YD_STK_BED_NO"       , ydStkBedNo);
				jrParam.setField("YD_STK_LYR_NO"       , ydStkLyrNo);
				jrParam.setField("YD_STK_LYR_MTL_STAT" , "E");
				jrParam.setField("STL_NO"              , "");
				
				
				if ("FD".equals(ydEqpGp) || "FE".equals(ydEqpGp) || "KD".equals(ydEqpGp) || "KE".equals(ydEqpGp)) {
					
					if ("F".equals(ydBayGp) || "D".equals(ydBayGp) || ("FE".equals(ydEqpGp) && "B".equals(ydBayGp))) {
						sMsg = "코일야드 결속장인 경우 Skip";
						commUtils.printLog(logId, sMsg, "SL");
					} else {
						/*
						UPDATE TB_YD_STKLYR
						   SET MODIFIER = :V_MODIFIER 
						     , MOD_DDTT = SYSDATE
						     , STL_NO   = :V_STL_NO
						     , YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT 
						 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
						   AND YD_STK_BED_NO = :V_YD_STK_BED_NO 
						   AND YD_STK_LYR_NO = :V_YD_STK_LYR_NO  
						 */
						commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStklyrInStlNo", logId, mthdNm, "저장위치 삭제");
					}
				}
				
			}

			/**********************************************************
			* 3. 저장품 조회 /  코일 공통 조회
			**********************************************************/
			jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("COIL_NO" , sStlNo);
			jrParam.setField("STL_NO"  , sStlNo);
			
			JDTORecordSet jsYdStock = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYdStock", logId, mthdNm, "저장품 조회");
			
			JDTORecordSet jsCoilcomm = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getPtCoilComm", logId, mthdNm, "코일공통 조회");
			
			if (jsCoilcomm.size() <= 0) {
				sMsg = "코일공통 조회 실패";
		      	commUtils.printLog(logId, sMsg, "SL");
		        return jrRtn;
			}
			JDTORecord jrCoilComm = jsCoilcomm.getRecord(0);
			
			/**********************************************************
			* 4. 저장품 수정
			**********************************************************/
			
			if (jsYdStock.size() > 0) {
				
				String sW_GP = "";
				
				//폭구분
				double dW_GP = Double.parseDouble(commUtils.nvl(jrCoilComm.getFieldString("COIL_W" ), "0"));
				if (dW_GP < 1601) {
					sW_GP = "M";
				} else {
					sW_GP = "L";
				}
				jrParam.setField("YD_MTL_W_GP"           , sW_GP);
				
				//외경그룹
				int iOUTDIA = Integer.parseInt(commUtils.nvl(jrCoilComm.getFieldString("COIL_OUTDIA" ), "0"));
				String sOUTDIA = "";
				
				if (iOUTDIA <= 1280) {
					sOUTDIA = "A";
				} else if (( iOUTDIA > 1280 )&&( iOUTDIA <= 1930)) { 
					sOUTDIA = "B";
				} else if ( iOUTDIA > 1930 ) {
					sOUTDIA = "C";
				}
				jrParam.setField("YD_COIL_OUTDIA_GRP_GP"	, sOUTDIA);
				
				String[] rVal = new String[1];
				//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
				rVal= coilDao.getYdAimRtGp("C", jrParam);
				
				//판정보류,종합판정대기,입고인 경우 제품장으로 적치
				if (rVal[1].equals("F") || 
					rVal[1].equals("G") || 
					rVal[1].equals("H")	) { 
					jrParam.setField("YD_AIM_YD_GP", 	"J");
				} else {
					jrParam.setField("YD_AIM_YD_GP", 	"H");						
				}
					
				jrParam.setField("YD_MTL_W"    , commUtils.nvl(jrCoilComm.getFieldString("COIL_W"    ), ""));
				jrParam.setField("COIL_OUTDIA" , commUtils.nvl(jrCoilComm.getFieldString("COIL_OUTDIA"), ""));
				jrParam.setField("YD_AIM_RT_GP", rVal[0]);
				jrParam.setField("STL_PROG_CD" , rVal[1]);
				
				/*
				UPDATE TB_YD_STOCK
				   SET MODIFIER               = :V_MODIFIER
				     , MOD_DDTT               = SYSDATE
				     , YD_MTL_W               = :V_YD_MTL_W
				     , COIL_OUTDIA            = :V_COIL_OUTDIA
				     , STL_PROG_CD            = :V_STL_PROG_CD
				     , YD_AIM_YD_GP           = :V_YD_AIM_YD_GP
				     , YD_AIM_RT_GP           = :V_YD_AIM_RT_GP
				     , YD_MTL_W_GP            = :V_YD_MTL_W_GP
				     , YD_COIL_OUTDIA_GRP_GP  = :V_YD_COIL_OUTDIA_GRP_GP
				 WHERE STL_NO = :V_STL_NO 
				 */
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStock", logId, mthdNm, "저장품수정");
				
				/**********************************************************
				* 4. 저장품 제원 야드L2 전송 (YDY5L002)
				**********************************************************/
				JDTORecord jDrd = JDTORecordFactory.getInstance().create();
				jDrd.setField("JMS_TC_CD"			, "YDY5L002");
				jDrd.setField("YD_INFO_SYNC_CD"		, "5");	//1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)
				jDrd.setField("STL_NO"		        , sStlNo);
				jDrd.setField("YD_STK_COL_GP"  		, "");
				jDrd.setField("YD_STK_BED_NO"  		, "");
				
				jrRtn = commUtils.addSndData(jrRtn ,coilDao.getMsgL2("YDY5L002", jDrd));
				
				return jrRtn;
				
			} //end if (rsRst.size() > 0) {
			
			/**********************************************************
			* 5. 저장품 수정 (모재료번호 정보 삭제)
			**********************************************************/
			
			String sMmatlFeeNo= jrCoilComm.getFieldString("MMATL_FEE_NO"    );
			
			jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("STL_NO"  , sMmatlFeeNo);
			
			jsYdStock = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYdStock", logId, mthdNm, "저장품 조회");
			
			if (jsYdStock.size() < 0) {
				sMsg = "저장품정보 조회 실패";
		      	commUtils.printLog(logId, sMsg, "SL");
		        return jrRtn;
			}
			
//			jsYdStock.first();
//			JDTORecord jrYdStock = jsYdStock.getRecord();
			
			jrParam.setField("DEL_YN"  , "Y");
			/*
			UPDATE TB_YD_STOCK
			   SET MODIFIER = :V_MODIFIER 
			     , MOD_DDTT = SYSDATE
			     , DEL_YN   = :V_DEL_YN
			 WHERE STL_NO   = :V_STL_NO
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.delYdStock", logId, mthdNm, "모재료번호 정보삭제");
			
			/**********************************************************
			* 6. 저장품 생성
			**********************************************************/
			// 2열연 코일 저장품 등록
			if (!coilDao.stockProcCom(logId, mthdNm, sStlNo,  1)) {
				// 실패해도 진행
				commUtils.printLog(logId, "저장품 생성실패", "SL");
			}
			
			/**********************************************************
			* 7. 저장품 제원 야드L2 전송 (YDY5L002)
			**********************************************************/
			JDTORecord jDrd = JDTORecordFactory.getInstance().create();
			jDrd.setField("JMS_TC_CD"			, "YDY5L002");
			jDrd.setField("YD_INFO_SYNC_CD"		, "5");	//1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)
			jDrd.setField("STL_NO"		        , sStlNo);
			jDrd.setField("YD_STK_COL_GP"  		, "");
			jDrd.setField("YD_STK_BED_NO"  		, "");
			
			jrRtn = commUtils.addSndData(jrRtn ,coilDao.getMsgL2("YDY5L002", jDrd));
			
			
			commUtils.printLog(logId, mthdNm, "S-");
			
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}		
	
	
	/**	
	 * [A] 오퍼레이션명 : 2열연 정정입측 보급Lot 편성 백업 수신 (HRYDJ008)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord rcvHRYDJ008(JDTORecord rcvMsg) throws DAOException {
		String mthdNm    = "2열연 정정입측 보급Lot 편성 백업 수신[CCoilL3RcvSeEJB.rcvHRYDJ008] < " + rcvMsg.getResultMsg();
		String logId     = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			jrRtn = this.procCCoilShearInSupLotComp(rcvMsg);
			
			commUtils.printLog(logId, mthdNm, "S-");
			
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}	
	
	
	/**	
	 * [A] 오퍼레이션명 : HR열연정정Line-Off요구 수신 (HRYDJ009)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord rcvHRYDJ009(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "HR열연정정Line-Off요구 수신[CCoilL3RcvSeEJB.rcvHRYDJ009] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			jrRtn = this.procCCoilShearOutLineOffReq(rcvMsg);
			
			commUtils.printLog(logId, mthdNm, "S-");
			
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}	
	
	
	/**	
	 * [A] 오퍼레이션명 : 2열연정정출측LINE-OFF요구/2열연정정추출요구/2열연정정 Take-Out요구(procR3ShearOutLineOffReq)
	 *                    H2YDL003 H2YDL023 H2YDL033 H2YDL043 H2YDL073 H2YDL013 H2YDL053 Line-off
	 *                    H2YDL005 H2YDL025 H2YDL035 H2YDL045 H2YDL075 H2YDL015 H2YDL055 Take-out
	 *                    SPM1   , SPM2   , SPM3   , SPM4   , SPM5   , #1HFL  , #4HFL 
	 *                    HRYDJ009 C열연 정정추출요구
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord procCCoilShearOutLineOffReq(JDTORecord rcvMsg) throws DAOException {
		String mthdNm    = "2열연정정LINE-OFF요구 처리[CCoilL3RcvSeEJB.proccCoilShearOutLineOffReq] < " + rcvMsg.getResultMsg();
		String logId     = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, mthdNm, "S+");
			
			//수신 항목 값
			String msgId      = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sTreatGp   = commUtils.trim(rcvMsg.getFieldString("TREAT_GP"     ));	//처리구분 1:보급, 2:보급취소, 3:추출, 4:Take-Out, 5:Take-In
			String sStlNo     = commUtils.trim(rcvMsg.getFieldString("STL_NO"       ));	//재료번호
			String sEqpGp     = commUtils.trim(rcvMsg.getFieldString("EQP_GP"       ));	//설비구분 - 추출, TakeOut요구시 Coil위치
			String ydEqpId    = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"    ));
			String ydStkBedNo = commUtils.trim(rcvMsg.getFieldString("YD_STK_BED_NO"));
			String sIsptor    = commUtils.trim(rcvMsg.getFieldString("ISPTOR"       )); //사번          
			String sTakeOutDt = commUtils.trim(rcvMsg.getFieldString("TAKE_OUT_DT"  )); //TakeOut시간
			String sTakeOutCd = commUtils.trim(rcvMsg.getFieldString("TAKE_OUT_CD"  )); //TakeOut원인명
			
			String sModifier  = commUtils.trim(rcvMsg.getFieldString("MODIFIER" )); //수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }
			
			String sMsg       = "";
			String ydSchCd           = "";
			String sRcptYdEqpId      = "";
			String sRcptTcarAimBayGp = "";
			String sYdEqpStat 		 = "";
			
			
			/**********************************************************
			* 0. 수신 항목 값 Check
			**********************************************************/
			// 2열연정정출측LINE-OFF요구(L2)
			if ("H2YDL003".equals(msgId)
			 || "H2YDL013".equals(msgId)
			 || "H2YDL023".equals(msgId)
			 || "H2YDL033".equals(msgId)
			 || "H2YDL043".equals(msgId)
			 || "H2YDL053".equals(msgId)
			 || "H2YDL073".equals(msgId)
			) {
				if ("".equals(ydEqpId)) {
					jrRtn.setField("RTN_CD"         , "0");	
					jrRtn.setField("RTN_MSG"        , "설비 항목값이 없습니다");
			      	return jrRtn;
				}
				
				if ("".equals(sStlNo)) {
					jrRtn.setField("RTN_CD"         , "0");	
					jrRtn.setField("RTN_MSG"        , "재료번호 항목값이 없습니다");
			      	return jrRtn;
				}
			}
			// 2열연정정 Take-Out요구 (L2)
			if ("H2YDL005".equals(msgId)
			 || "H2YDL015".equals(msgId)
			 || "H2YDL025".equals(msgId)
			 || "H2YDL035".equals(msgId)
			 || "H2YDL045".equals(msgId)
			 || "H2YDL055".equals(msgId)
			 || "H2YDL075".equals(msgId)
			) {
				if ("".equals(ydEqpId)) {
			      	jrRtn.setField("RTN_CD"         , "0");	
					jrRtn.setField("RTN_MSG"        , "설비 항목값이 없습니다");
			      	return jrRtn;
				}
				
				if ("".equals(ydStkBedNo)) {
					jrRtn.setField("RTN_CD"         , "0");	
					jrRtn.setField("RTN_MSG"        , "적치Bed번호 항목값이 없습니다");
			      	return jrRtn;
				}
				
				if ("".equals(sStlNo)) {
					jrRtn.setField("RTN_CD"         , "0");	
					jrRtn.setField("RTN_MSG"        , "재료번호 항목값이 없습니다");
			      	return jrRtn;
				}
			}
			
			// HR열연정정Line-Off요구
			if ("HRYDJ009".equals(msgId)) {
				if ("".equals(sTreatGp)) {
			      	jrRtn.setField("RTN_CD"         , "0");	
			      	jrRtn.setField("RTN_MSG"        , "처리구분 항목값이 없습니다");
			      	return jrRtn;
				}
				
				if ("".equals(sStlNo)) {
					jrRtn.setField("RTN_CD"         , "0");	
					jrRtn.setField("RTN_MSG"        , "재료번호 항목값이 없습니다");
					return jrRtn;
				}
				
				if ("".equals(sEqpGp)) {
					jrRtn.setField("RTN_CD"         , "0");	
					jrRtn.setField("RTN_MSG"        , "설비구분 항목값이 없습니다");
					return jrRtn;
				}

				String sEqpLoc = (String)CCommUtils.h_hRvsstEqpGpMatch.get(sEqpGp);
				
				if ("".equals(sEqpLoc) || commUtils.isEmpty(sEqpLoc)) {
					jrRtn.setField("RTN_CD"         , "0");	
					jrRtn.setField("RTN_MSG"        , "설비구분으로 맵핑된 설비위치정보 값이 없습니다");
					return jrRtn;
				}
				
				ydEqpId    = sEqpLoc.substring(0,6);
				ydStkBedNo = sEqpLoc.substring(6,8);
			}
			
			/*
			 * DE KD -> KE KD
			 */
			String ydBayGp = ydEqpId.substring(1, 2);
			String sTmpEqp = ydEqpId.substring(2, 3); //E동 SPM2 경우 DD, DE -> KE, KD로 변환
			if ("D".equals(sTmpEqp)) {
				ydEqpId = "JEK" + ydEqpId.substring(3, 4)+ "02";
			} else {
				ydEqpId = "J" + ydEqpId.substring(1);
			}
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, commUtils.trim(sModifier));
			
			/**********************************************************
			* 1. 현재진도코드 조회
			**********************************************************/
			jrParam.setField("COIL_NO", sStlNo);
			/*
			SELECT A.*
			     , NVL((SELECT (CASE WHEN RECEIPT_HOLD_SCRAP_CAUSE_GP  IN('I','M') AND YD_AIM_RT_GP='F4' THEN NULL
			                         WHEN YD_AIM_RT_GP='F4' AND A.CURR_PROG_CD='D' 
			                         THEN NULL ELSE YD_AIM_RT_GP  END)
			              FROM TB_HR_C_SHEARWOWR SR
			              WHERE SR.STEP_NO = (SELECT 
			                                     MAX(STEP_NO)
			                                    FROM TB_HR_C_SHEARWOWR A
			                                   WHERE COIL_NO = SR.COIL_NO
			                                     AND WORK_STAT IN( '*')
			                                     AND ROWNUM<=1 
			                                  )
			                AND SR.COIL_NO = A.COIL_NO
			            ),'F3') AS YD_AIM_RT_GP2
			     , (SELECT EQP_CD FROM USRHRA.TB_HR_D_TRK WHERE COIL_NO =A.COIL_NO AND ROWNUM<=1) AS EQP_CD
			     , (CASE WHEN (SELECT ITEM2 FROM TB_YD_RULE WHERE REPR_CD_GP = 'J00001') = 'N' THEN 'N'
			        ELSE
			             CASE WHEN (SELECT WRAP_METHOD_CD FROM TB_PT_OSCOMM 
			                         WHERE ORD_NO = A.ORD_NO 
			                           AND ORD_DTL= A.ORD_DTL
			                           AND ROWNUM =1) = 'EB' AND A.ITEMNAME_CD NOT IN ('HAP','HBP','HCP','HAS','HBS','HCS','HAT','HBT','HCT')
			                  THEN 'Y'
			             ELSE 'N' END 
			         END) AS GPACK_FLAG 
			     , (CASE WHEN STL_APPEAR_GP='Y' THEN NVL((SELECT SUBSTR(ARR_YD_PNT_CD,2,2) 
			                                                FROM USRTSA.TB_TS_MATL_FTMV_WO C
			                                               WHERE C.STL_NO=A.COIL_NO
			                                                 AND TS_MATL_FTMV_STAT_GP IN ('1','3')
			                                                 AND C.TRANSWORD_SEQNO = (SELECT MAX(TRANSWORD_SEQNO)
			                                                                            FROM TB_TS_MATL_FTMV_WO B
			                                                                           WHERE B.STL_NO=C.STL_NO)
			                                                               
			                                             ) ,NEXT_PROC)
			        ELSE NEXT_PROC END ) AS NEXT_PROC         
			  FROM USRPTA.TB_PT_COILCOMM  A
			 WHERE COIL_NO = :V_COIL_NO
			 */
			JDTORecordSet jsCoilcomm = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getPtCoilComm", logId, mthdNm, "코일공통 조회");
			if (jsCoilcomm.size() <= 0) {
				commUtils.printLog(logId, "코일공통에 해당 재료정보 없음", "S-");
				jrRtn.setField("RTN_CD"         , "0");	
				jrRtn.setField("RTN_MSG"        , "코일공통에 해당 재료정보 없음");
				return jrRtn;
			}
			
			String sCurrProgCd   = commUtils.trim(jsCoilcomm.getRecord(0).getFieldString("CURR_PROG_CD"  ));
			String sScrapCauseCd = commUtils.trim(jsCoilcomm.getRecord(0).getFieldString("SCRAP_CAUSE_CD")); //스크랩원인코드
			String sGpackFlag    = commUtils.trim(jsCoilcomm.getRecord(0).getFieldString("GPACK_FLAG"    )); //지포장FLAG
			
			jrParam.setField("STL_NO"      	, sStlNo);
			jrParam.setField("STL_PROG_CD"	, sCurrProgCd);
			jrParam.setField("CURR_PROG_CD"	, sCurrProgCd);
			

			String[] rVal = new String[1];
			rVal= coilDao.getYdAimRtGp("C", jrParam);	//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
			
			commUtils.printLog(logId, "rVal[0]:" + rVal[0], "SL");
			commUtils.printLog(logId, "rVal[1]:" + rVal[1], "SL");
			commUtils.printLog(logId, "sGpackFlag:" + sGpackFlag, "SL");
			commUtils.printLog(logId, "sScrapCauseCd:" + sScrapCauseCd, "SL");
			commUtils.printLog(logId, "sCurrProgCd:" + sCurrProgCd, "SL");
			
			
			jrParam.setField("YD_AIM_RT_GP"	, rVal[0]);	
			jrParam.setField("YD_AIM_YD_GP" , "J");
			
			if ("EA".equals(rVal[0])) {
				jrParam.setField("YD_AIM_RT_GP", "EA");
			}
			
			if ("Y".equals(sGpackFlag)) { //지포장
				//재작업지시가 아닌경우
				if (!"F4".equals(rVal[0]) && !"F5".equals(rVal[0])) {
					// 지포장 대상재 보급
					jrParam.setField("YD_AIM_RT_GP", "G0");
				}
			}
			
			jrParam.setField("YD_RCPT_STR_LOC", ydEqpId); //설비명 : 입고대차시 사용
			/*
			UPDATE TB_YD_STOCK
			   SET MODIFIER               = :V_MODIFIER
			     , MOD_DDTT               = SYSDATE
			     , STL_PROG_CD            = :V_STL_PROG_CD
			     , YD_AIM_YD_GP           = :V_YD_AIM_YD_GP
			     , YD_AIM_RT_GP           = :V_YD_AIM_RT_GP
			     , YD_RCPT_STR_LOC        = :V_YD_RCPT_STR_LOC
			 WHERE STL_NO = :V_STL_NO 
			 */
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStockAimRt", logId, mthdNm, "저장품 수정");
			
			/**********************************************************
			* 2. C열연정정출측Line-Off요구(L2)
			**********************************************************/
			
			//C열연정정출측Line-Off요구(L2)
			if ("H2YDL003".equals(msgId)
			 || "H2YDL013".equals(msgId)
			 || "H2YDL023".equals(msgId)
			 || "H2YDL033".equals(msgId)
			 || "H2YDL043".equals(msgId)
			 || "H2YDL053".equals(msgId)
			 || "H2YDL073".equals(msgId)
			 ) {
				if ("YC".equals(rVal[0]) && ! "".equals(sScrapCauseCd)) {  //스크랩코일
						
					ydSchCd = ydEqpId.substring(0,2) + "KD05LH";
					
				} else if ("F4".equals(rVal[0]) || "EA".equals(rVal[0])) {      //재작업C , 2PASS 재작업
					
					if (!"D".equals(rVal[1])) {      //보급존으로 재작업 지시
						ydSchCd = ydEqpId.substring(0,4) + "02LH";
					} else {                         //소재 야드로 재작업 지시
						ydSchCd = ydEqpId.substring(0,4) + "01LH";
					}
					
				} else if ("F5".equals(rVal[0])) {      //재작업C
			
					ydSchCd = ydEqpId.substring(0,4) + "01LH";
					
				} else if ((rVal[0].equals("B4") || rVal[0].equals("B3")|| rVal[0].equals("BC")
						  ||rVal[0].equals("CE") || rVal[0].equals("CF")|| rVal[0].equals("CG") 
				          )) {

					ydSchCd = ydEqpId.substring(0,4) + "04LH";
					
				} else {
				
					if ("Y".equals(sGpackFlag)) {
						
						ydSchCd = ydEqpId.substring(0,4) + "06LH";
						
					} else if ("G".equals(sCurrProgCd) || "H".equals(sCurrProgCd) || rVal[0].equals("F3") || rVal[1].equals("D")) { //임가공 이송재 추가
						//대차작업 입고동 확인
						
						jrParam.setField("YD_EQP_ID"    , ydEqpId);
						jrParam.setField("RCPT_TCAR_BAY", ydEqpId.substring(1,2));
						jrParam.setField("STL_NO"       , sStlNo);
						
						/* com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdEqpTCarStatRcptCoil 
						SELECT A.YD_EQP_ID
						     , A.RCPT_TCAR_AIM_BAY_GP
						     , A.RCPT_TCAR_USE_YN
						     , A.RCPT_TCAR_BAY
						     , B.YD_EQP_GP
						  FROM TB_YD_EQP A
						     , (SELECT SUBSTR(:V_YD_EQP_ID,2,5) AS YD_EQP_GP  FROM DUAL) B
						 WHERE A.YD_EQP_ID LIKE 'JXTC'||'%'
						   AND A.YD_LOC_GP = 'J'
						   AND A.RCPT_TCAR_USE_YN = 'Y'
						   AND A.RCPT_TCAR_BAY  = SUBSTR(B.YD_EQP_GP,1,1)
						   AND 'Y' = CASE WHEN B.YD_EQP_GP = 'HKD01' AND A.YD_EQP_ID IN ('JXTC01','JXTC02','JXTC04','JXTC06') THEN 'Y' --SPM#1
						                  WHEN B.YD_EQP_GP = 'EKD02' AND A.YD_EQP_ID IN ('JXTC01','JXTC02','JXTC03')          THEN 'Y' --SPM#2
						                  WHEN B.YD_EQP_GP = 'CKD03' AND A.YD_EQP_ID IN ('JXTC05')                            THEN 'Y' --SPM#3
						                  WHEN B.YD_EQP_GP = 'BKD04' AND A.YD_EQP_ID IN ('JXTC05')                            THEN 'Y' --SPM#4
						                  WHEN B.YD_EQP_GP = 'AKD05' AND A.YD_EQP_ID IN ('JXTC01','JXTC02','JXTC03')          THEN 'Y' --SPM#5
						                 
						                  WHEN B.YD_EQP_GP = 'GFE01' AND A.YD_EQP_ID IN ('JXTC01','JXTC02','JXTC04','JXTC06') THEN 'Y' --HFL#1 
						                  WHEN B.YD_EQP_GP = 'FFE02' AND A.YD_EQP_ID IN ('JXTC01','JXTC02','JXTC03','JXTC06') THEN 'Y' --HFL#2 
						                  WHEN B.YD_EQP_GP = 'DFE03' AND A.YD_EQP_ID IN ('JXTC01','JXTC02','JXTC03')          THEN 'Y' --HFL#3 
						                  WHEN B.YD_EQP_GP = 'CFE04' AND A.YD_EQP_ID IN ('JXTC01','JXTC02','JXTC03')          THEN 'Y' --HFL#4 
						                  WHEN B.YD_EQP_GP = 'BFE05' AND A.YD_EQP_ID IN ('JXTC01','JXTC02','JXTC03')          THEN 'Y' --HFL#5 
						                  ELSE 'N' END
						 */
						JDTORecordSet jsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdEqpTCarStatRcptCoil", logId, mthdNm, "대차 조회");
						
						if (jsRst.size() <= 0) {
							//입고대차가 없을 경우
							commUtils.printLog(logId, "입고대차가 없는 경우", "SL");
							ydSchCd = ydEqpId.substring(0,3) + "D01LM";
						} else {
							sRcptYdEqpId      = jsRst.getRecord(0).getFieldString("YD_EQP_ID"           );
							sRcptTcarAimBayGp = jsRst.getRecord(0).getFieldString("RCPT_TCAR_AIM_BAY_GP");
							sYdEqpStat 		  = jsRst.getRecord(0).getFieldString("YD_EQP_STAT");

							//REQ202407592703 야드설비상태(B:고장, N:정상, R:복구, P:롤이송 등)   
							if("P".equals(sYdEqpStat)||"B".equals(sYdEqpStat)){
								String sLogMsg	= "현재 대차가 고장 또는 롤이송 중이라 입고대차를 사용 할 수 없습니다!";
								commUtils.printLog(logId, sLogMsg, "SL"); 
								ydSchCd = ydEqpId.substring(0,3) + "D01LM";
								
							}else{	
								commUtils.printLog(logId, "sRcptYdEqpId:" + sRcptYdEqpId, "SL");
	
								if ("JXTC01".equals(sRcptYdEqpId)) {
									ydSchCd = ydEqpId.substring(0,2) + "TC01MM";
								} else if ("JXTC02".equals(sRcptYdEqpId)) {
									ydSchCd = ydEqpId.substring(0,2) + "TC02MM";
								} else if ("JXTC05".equals(sRcptYdEqpId)) {
									ydSchCd = ydEqpId.substring(0,2) + "TC05MM";
								}
							}
						}
						
					} else {
						commUtils.printLog(logId, "입고 대상재가 아닙니다.", "SL");
						jrRtn.setField("RTN_CD" , "0");	
						jrRtn.setField("RTN_MSG", "입고 대상재가 아닙니다.");
						return jrRtn;
					}
				}
				
			}
	
			commUtils.printLog(logId, "2열연정정출측Line-Off요구(L2) sYD_SCH_CD = "+ ydSchCd, "SL");
			
			/**********************************************************
			* 3. 2열연정정 Take-Out요구 (L2)
			**********************************************************/
			//C열연정정 Take-Out요구 (L2)
			if ("H2YDL005".equals(msgId)
			 || "H2YDL015".equals(msgId)
			 || "H2YDL025".equals(msgId)
			 || "H2YDL035".equals(msgId)
			 || "H2YDL045".equals(msgId)
			 || "H2YDL055".equals(msgId)
			 || "H2YDL075".equals(msgId)
			 ) {
				ydSchCd = ydEqpId.substring(0,4) + "03LH";
			}
			
			/**********************************************************
			* 4. 2열연정정추출요구(L3)
			**********************************************************/
			if ("HRYDJ009".equals(msgId)) {
				if ("JFTD".equals(ydEqpId.substring(0,4))){   // F동 텔레스코프 교정기 추출 위치 24.11.13
					ydSchCd = ydEqpId.substring(0,4) + "01LH";
					
				} else if ((sEqpGp.startsWith("G1-") && "3".equals(sTreatGp)) 
				 || (sEqpGp.startsWith("G2-") && "3".equals(sTreatGp))
				 || (sEqpGp.startsWith("G3-") && "3".equals(sTreatGp))
				 || (sEqpGp.startsWith("G4-") && "3".equals(sTreatGp))
				 || (sEqpGp.startsWith("G5-") && "3".equals(sTreatGp))  //F동 지포장 추가 2024.02.22
				 ) {
					ydSchCd = ydEqpId.substring(0,2) + "GF01LM";
					
				} else if ("EA".equals(rVal[0])) {      //재작업C , 2PASS 재작업
					
					ydSchCd = ydEqpId.substring(0,4) + "02LH";
						
				} else if ("YC".equals(rVal[0]) && !"".equals(sScrapCauseCd)) {  //스크랩코일
					
					ydSchCd = ydEqpId.substring(0,2) + "KD05LH";
						
				} else if ("F5".equals(rVal[0])) {
					
					ydSchCd = ydEqpId.substring(0,4) + "01LH";
				
				} else if ("B4".equals(rVal[0]) || "B3".equals(rVal[0])|| "BC".equals(rVal[0]) 
						|| "CE".equals(rVal[0]) || "CF".equals(rVal[0])|| "CG".equals(rVal[0])
						 ) {
					
					ydSchCd = ydEqpId.substring(0,4) + "04LH";
					
				} else {
					if ("G".equals(sCurrProgCd) || "H".equals(sCurrProgCd)|| rVal[0].equals("F3")|| rVal[1].equals("D") ) { //임가공 이송재
							
						//대차작업 입고동 확인
						jrParam.setField("YD_EQP_ID"    , ydEqpId);
						jrParam.setField("RCPT_TCAR_BAY", ydEqpId.substring(1,2));
						jrParam.setField("STL_NO"       , sStlNo);
						
						JDTORecordSet jsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdEqpTCarStatRcptCoil", logId, mthdNm, "대차 조회");
						
						if (jsRst.size() <= 0) {
							//입고대차가 없을 경우
							if ("Y".equals(sGpackFlag)) {
								ydSchCd = ydEqpId.substring(0,4) + "06LH";  //지포장 추출
							} else {
								ydSchCd = ydEqpId.substring(0,3) + "D01LM";  //입고
							}
						} else {
					
							sRcptYdEqpId      = jsRst.getRecord(0).getFieldString("YD_EQP_ID"      );
							sRcptTcarAimBayGp = jsRst.getRecord(0).getFieldString("RCPT_TCAR_AIM_BAY_GP");
							
							if ("JXTC01".equals(sRcptYdEqpId)) {
								ydSchCd = ydEqpId.substring(0,2) + "TC01MM";
							} else if ("JXTC02".equals(sRcptYdEqpId)) {
								ydSchCd = ydEqpId.substring(0,2) + "TC02MM";
							} else if ("JXTC05".equals(sRcptYdEqpId)) {
								ydSchCd = ydEqpId.substring(0,2) + "TC05MM";
							}
							
						}
					} else {
						commUtils.printLog(logId, "입고 대상재가 아닙니다.", "SL");
						jrRtn.setField("RTN_CD" , "0");	
						jrRtn.setField("RTN_MSG", "입고 대상재가 아닙니다.");
						return jrRtn;
					}
				}
				
			} //if (msgId.equals("HRYDJ009"))
			
			/**********************************************************
			* 5. 적치단 정보 수정
			**********************************************************/
			if (("HRYDJ009".equals(msgId))
			 || ("H2YDL005".equals(msgId))
			 || ("H2YDL015".equals(msgId))
			 || ("H2YDL025".equals(msgId))
			 || ("H2YDL035".equals(msgId))
			 || ("H2YDL045".equals(msgId))
			 || ("H2YDL055".equals(msgId))
			 || ("H2YDL075".equals(msgId))
			 ) {
			
				jrParam.setField("YD_STK_COL_GP", 	ydEqpId);
				jrParam.setField("YD_STK_BED_NO", 	ydStkBedNo);
				jrParam.setField("YD_STK_LYR_NO", 	"001");
				
				JDTORecordSet jsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYdStklyr", logId, mthdNm, "적치단 조회");
				
				if (jsRst.size() <= 0) {
					commUtils.printLog(logId, "적치단 조회 실패", "SL");
					jrRtn.setField("RTN_CD" , "0");	
					jrRtn.setField("RTN_MSG", "적치단 조회 실패");
					return jrRtn;
				}
				
				String ydStkLyrMtlStat = jsRst.getRecord(0).getFieldString("YD_STK_LYR_MTL_STAT");
				String sLyrStlNo       = jsRst.getRecord(0).getFieldString("STL_NO");
				
				if (!("E".equals(ydStkLyrMtlStat) || "C".equals(ydStkLyrMtlStat))) {
					
					if (!sStlNo.equals(sLyrStlNo)) {
						sMsg = "적치단 재료상태(" + ydStkLyrMtlStat + ") 적치가능 상태가 아닙니다.";
						commUtils.printLog(logId, sMsg, "SL");
						jrRtn.setField("RTN_CD" , "0");	
						jrRtn.setField("RTN_MSG", sMsg);
						return jrRtn;
					}
				}
				
				jsRst   = JDTORecordFactory.getInstance().createRecordSet("");
				jrParam = commUtils.getParam(logId, mthdNm, commUtils.trim(sModifier));
				jrParam.setField("STL_NO", 	sStlNo);
				
				jsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getCoilYdStkPosInfo_PIDEV", logId, mthdNm, "적치정보 조회");
				
				if (jsRst.size() > 0) {
					//적치되어 있는 정보 삭제처리
					jrParam.setField("YD_STK_COL_GP"      ,	jsRst.getRecord(0).getFieldString("YD_STK_COL_GP"));
					jrParam.setField("YD_STK_BED_NO"      ,	jsRst.getRecord(0).getFieldString("YD_STK_BED_NO"));
					jrParam.setField("YD_STK_LYR_NO"      ,	jsRst.getRecord(0).getFieldString("YD_STK_LYR_NO"));
					jrParam.setField("YD_STK_LYR_MTL_STAT",	"E");
					jrParam.setField("STL_NO"             ,	"");
					
					commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStklyrInStlNo", logId, mthdNm, "저장위치 삭제");
				}
				
				//적치단 재료상태가 적치 가능이면 재료 등록
				//적치단 테이블 업데이트
				//적치열구분 = 설비ID
				jrParam.setField("YD_STK_COL_GP", 	    ydEqpId);
				jrParam.setField("YD_STK_BED_NO", 	    ydStkBedNo);
				jrParam.setField("YD_STK_LYR_NO", 	    "001");
				jrParam.setField("YD_STK_LYR_MTL_STAT", "C");
				jrParam.setField("STL_NO", 			    sStlNo);
				jrParam.setField("DEL_YN", 			    "N");
				
				commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStklyrInStlNo", logId, mthdNm, "재료등록");
				
				if (("H2YDL005".equals(msgId))
				 || ("H2YDL015".equals(msgId))
				 || ("H2YDL025".equals(msgId))
				 || ("H2YDL035".equals(msgId))
				 || ("H2YDL045".equals(msgId))
				 || ("H2YDL055".equals(msgId))
				 || ("H2YDL075".equals(msgId))
				) {
					
					/**********************************************************
	     	        * 정정작업메세지이력등록 시작
	     	        **********************************************************/
	     	        JDTORecord rcvMsgArgs = JDTORecordFactory.getInstance().create();
	     	        rcvMsgArgs.setField("COIL_NO"         , sStlNo);
	     	        rcvMsgArgs.setField("SHEAR_WRK_MSG_GP", "T");
	     	        rcvMsgArgs.setField("MSG_CONTENTS"    , sTakeOutCd);
	     	        rcvMsgArgs.setField("userid"          , sIsptor);
	     	        
	    	        EJBConnector ejbConn2 = new EJBConnector("hsteelApp", "HrCommMgtFaEJB", this);
	    	        JDTORecord jrEjb = (JDTORecord)ejbConn2.trx("insHrShrMsgLog", new Class[] { JDTORecord.class }, new Object[] { rcvMsgArgs });
					jrRtn = commUtils.addSndData(jrRtn, jrEjb);
				}
				
			}

			/**********************************************************
			* 6.스케줄 기준 체크 
			**********************************************************/
			jrParam.setField("YD_SCH_CD", ydSchCd);
			JDTORecordSet jsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYdSchRule", logId, mthdNm, "스케줄기준 조회");
			if (jsRst.size() <= 0) {
				commUtils.printLog(logId, "스케줄 기준 체크 에러", "SL");
				jrRtn.setField("RTN_CD" , "0");	
				jrRtn.setField("RTN_MSG", "스케줄 기준 체크 에러");
				return jrRtn;
			}
			
			/**********************************************************
			* 7.결로재 대상여부 판단 
			**********************************************************/
			jrParam = commUtils.getParam(logId, mthdNm, commUtils.trim(sModifier));
			jrParam.setField("BAY_GP"	, ydEqpId.substring(1,2));
			jrParam.setField("STL_NO"	, sStlNo);
			
			/*
			SELECT (SELECT YD_STK_COL_GP
			          FROM TB_YD_STKLYR A
			         WHERE A.YD_STK_COL_GP LIKE 'J%'
			           AND SUBSTR(A.YD_STK_COL_GP,2,1) LIKE :V_BAY_GP||'%'
			           AND A.YD_STK_LYR_ACT_STAT = 'S'
			           AND A.DEL_YN = 'N'
			           AND A.STL_NO IS NULL
			           AND SUBSTR(A.YD_STK_COL_GP,2,1) IN('A','D','E','G','H')
			           AND SUBSTR(A.YD_STK_COL_GP,3,2) BETWEEN '00' AND '99' 
			           AND ROWNUM <= 1
			       ) AS TO_YD_STK_BED_NO
			  FROM VW_YD_CONDENSATION2
			 WHERE COIL_NO = :V_STL_NO
			 */
			jsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getHotcoilAuto_PIDEV", logId, mthdNm, "결로재 대상 판단");
			
			String sToYdStkBedNo = "";
			if (jsRst.size() > 0) {
				sToYdStkBedNo = commUtils.nvl(jsRst.getRecord(0).getFieldString("TO_YD_STK_BED_NO"), "");
			}
			
			jrParam = commUtils.getParam(logId, mthdNm, commUtils.trim(sModifier));
			jrParam.setField("YD_SCH_CD",          	ydSchCd); //스케줄코드
			jrParam.setField("STL_SH"   ,      		"1"    ); //LINE_IN 재료매수
			jrParam.setField("STL_NO1"  , 	  		sStlNo );
			
			
			//결로재 대상 해당위치 지정
			if ("".equals(sToYdStkBedNo)) {
				jrParam.setField("YD_TO_LOC_DCSN_MTD", "S");
			} else {
				jrParam.setField("YD_TO_LOC_DCSN_MTD", "F");
				jrParam.setField("TO_YD_STK_BED_NO"  , sToYdStkBedNo); 
			}
			
			commUtils.printLog(logId, "가이드 정보 ["+sToYdStkBedNo+"]", "SL");
			
			jrParam.setField("YD_AIM_YD_GP"    , ydSchCd.substring(0,1));
			jrParam.setField("YD_AIM_BAY_GP"   , sRcptTcarAimBayGp     );
			jrParam.setField("YD_WRK_PLAN_TCAR", sRcptYdEqpId          );
			jrParam.setField("ISPTOR"          , sIsptor               );
			jrParam.setField("TAKE_OUT_DT"     , sTakeOutDt            );
			jrParam.setField("TAKE_OUT_CD"     , sTakeOutCd            ); 
			
			EJBConnector ejbConn = new EJBConnector("default", "CCoilWrkBookSeEJB", this);
			JDTORecord outRecord = (JDTORecord)ejbConn.trx("insWrkBookTx", new Class[] { JDTORecord.class }, new Object[] { jrParam });

			String ydWookId	= commUtils.nvl(outRecord.getFieldString("YD_WBOOK_ID"), "");
			String ydWrkCrn	= commUtils.nvl(outRecord.getFieldString("YD_WRK_CRN"), "");
			sMsg            = commUtils.trim(outRecord.getFieldString("RTN_MSG"));
			
			/**********************************************************
			* 8.스케줄 기동 
			**********************************************************/
			// REQ202406587903 '24.07.08  SPM 재작업 입측 트래킹 미인식
			if("JAKD02LH".equals(ydSchCd)||
			   "JBKD02LH".equals(ydSchCd)||
			   "JCKD02LH".equals(ydSchCd)||
			   "JEKD02LH".equals(ydSchCd)||
			   "JHKD02LH".equals(ydSchCd) ){
				commUtils.printLog(logId, "SPM재작업 보급은 스케줄 기동 안하고 종료 함.", "SL");
				commUtils.printLog(logId, mthdNm, "S-");
				jrRtn.setField("RTN_CD"  , "1");	
				jrRtn.setField("RTN_MSG" , sMsg);
				return jrRtn;
			}
			
			
			if ("HRYDJ009".equals(msgId)) {
				jrParam = commUtils.getParam(logId, mthdNm, commUtils.trim(sModifier));
				jrParam.setField("YD_WBOOK_ID", ydWookId);
				jrParam.setField("YD_SCH_CD"  , ydSchCd);
				
				/*
				SELECT YD_WBOOK_ID   
				  FROM TB_YD_WRKBOOK 
				 WHERE YD_SCH_CD = :V_YD_SCH_CD 
				   AND DEL_YN    = 'N'
				   AND ((YD_SCH_CD     IN('JBFE04LH','JDFE04LH','JFFE04LH') AND 1 = 0)
				        OR
				        (YD_SCH_CD NOT IN('JBFE04LH','JDFE04LH','JFFE04LH') AND 1 = 1)
				       )
				   AND YD_WBOOK_ID != :V_YD_WBOOK_ID  
				 */
				jsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getWorkTbRefYN", logId, mthdNm, "작업예약조회");
				
				if (jsRst.size() <= 0) {
					JDTORecord jrSnd = JDTORecordFactory.getInstance().create();
					jrSnd.setField("JMS_TC_CD"	, "YDYDJ551");
					jrSnd.setField("YD_WBOOK_ID", ydWookId);
					jrSnd.setField("YD_SCH_CD"	, ydSchCd);
					jrSnd.setField("YD_EQP_ID"  , ydWrkCrn);
					
					jrRtn = commUtils.addSndData(jrRtn, jrSnd);
					
					// 텔레스코프 작업 전달
					if("JFTD01LH".equals(ydSchCd)){
						jrRtn.setField("JMS_TC_CD"	, "YDYDJ551");
						jrRtn.setField("YD_WBOOK_ID", ydWookId);
						jrRtn.setField("YD_SCH_CD"	, ydSchCd);
						jrRtn.setField("YD_EQP_ID"  , ydWrkCrn);
					}
				}
				
			} else {
				JDTORecord jrSnd = JDTORecordFactory.getInstance().create();
				jrSnd.setField("JMS_TC_CD"	, "YDYDJ551");
				jrSnd.setField("YD_WBOOK_ID", ydWookId);
				jrSnd.setField("YD_SCH_CD"	, ydSchCd);
				jrSnd.setField("YD_EQP_ID"  , ydWrkCrn);
				
				jrRtn = commUtils.addSndData(jrRtn, jrSnd);
			}

			commUtils.printLog(logId, mthdNm, "S-");
			jrRtn.setField("RTN_CD"  , "1");	
			jrRtn.setField("RTN_MSG" , sMsg);
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**	
	 * [A] 오퍼레이션명 : 2열연정정입측Line-In요구/C열연 정정보급요구/C열연정정 Take-In요구(procCHrShearInSupLotComp) 
	 *                  ( H2YDL001 H2YDL021 H2YDL031 H2YDL041 H2YDL071 H2YDL011 H2YDL051 Line-In
	 *                    H2YDL004 H2YDL024 H2YDL034 H2YDL044 H2YDL074 H2YDL014 H2YDL054 Take-In
	 *                    SPM1   , SPM2   , SPM3   , SPM4   , SPM5   , #1HFL  , #4HFL 
	 *                    HRYDJ008 C열연 정정입측 보급Lot 편성 백업 )
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord procCCoilShearInSupLotComp(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "열연정정Line-In요구 수신[CCoilL3RcvSeEJB.procCCoilShearInSupLotComp] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, mthdNm, "S+");
			
			//수신 항목 값
			String msgId			= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID

			//HRYDJ008
			String sTreatGp			= commUtils.trim(rcvMsg.getFieldString("TREAT_GP" ));		//처리구분 1:보급, 2:보급취소, 3:추출, 4:Take-Out, 5:Take-In
			String sWordProc		= commUtils.trim(rcvMsg.getFieldString("WORD_PROC"));		//작업지시공정
			String sWordUnitName	= commUtils.trim(rcvMsg.getFieldString("WORD_UNIT_NAME"));	//작업단위 - 크래들롤 보급만 사용 FHsampl, DHsampl
			
			//H2YDL001 H2YDL021 H2YDL031 H2YDL041 H2YDL071 H2YDL011 H2YDL051
			//H2YDL004 H2YDL024 H2YDL034 H2YDL044 H2YDL074 H2YDL014 H2YDL054
			String ydEqpId			= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"    ));	//설비ID
			String ydStkBedNo		= commUtils.trim(rcvMsg.getFieldString("YD_STK_BED_NO"));	//야드적치Bed번호
			String sStlNo			= commUtils.trim(rcvMsg.getFieldString("STL_NO"       )); //재료번호(백업시에만 값이 들어옴)
			
			String sMsg				= "";
			String ydSchCd			= "";
			String ydWookId			= "";
			String sFirstYdWookId	= "";
			String sFirstYdSchCd	= "";
			
			String sModifier   = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam  = commUtils.getParam(logId, mthdNm, commUtils.trim(sModifier));
			JDTORecord jrRst    = commUtils.getParam(logId, mthdNm, commUtils.trim(sModifier));
			
			JDTORecordSet jsRst = JDTORecordFactory.getInstance().createRecordSet("");

			/**********************************************************
			* 0. 수신 항목 값 Check
			**********************************************************/
			if ("H2YDL001".equals(msgId) //SPM1 입측 Line-In요구
			 || "H2YDL011".equals(msgId) //HFL1 입측 Line-In요구
			 ||	"H2YDL021".equals(msgId) //SPM2 입측 Line-In요구 (기존설비 HEDE01)
			 ||	"H2YDL031".equals(msgId) //SPM3 입측 Line-In요구
			 ||	"H2YDL041".equals(msgId) //SPM4 입측 Line-In요구
			 || "H2YDL051".equals(msgId) //HFL4 입측 Line-In요구
			 || "H2YDL071".equals(msgId) //SPM5 입측 Line-In요구
			 ) {
				if ("".equals(ydEqpId)) {
					jrRtn.setField("RTN_CD"         , "0");	
					jrRtn.setField("RTN_MSG"        , "설비ID 정보 없음");
					return jrRtn;
				}
			}
			
			if ("H2YDL004".equals(msgId) //SPM1 입측 Take-In요구
			 || "H2YDL014".equals(msgId) //HFL1 입측 Take-In요구
			 ||	"H2YDL024".equals(msgId) //SPM2 입측 Take-In요구
			 ||	"H2YDL034".equals(msgId) //SPM3 입측 Take-In요구
			 ||	"H2YDL044".equals(msgId) //SPM4 입측 Take-In요구
			 || "H2YDL054".equals(msgId) //HFL4 입측 Take-In요구
			 || "H2YDL074".equals(msgId) //SPM5 입측 Take-In요구
			) {
				if ("".equals(ydEqpId)) {
					jrRtn.setField("RTN_CD"         , "0");	
					jrRtn.setField("RTN_MSG"        , "설비 항목값이 없습니다");
					return jrRtn;
				}
				if ("".equals(ydStkBedNo)) {
					jrRtn.setField("RTN_CD"         , "0");	
					jrRtn.setField("RTN_MSG"        , "적치BED번호 항목값이 없습니다");
					return jrRtn;
				}
				if ("".equals(sStlNo)) {
					jrRtn.setField("RTN_CD"         , "0");	
					jrRtn.setField("RTN_MSG"        , "재료번호 항목값이 없습니다");
					return jrRtn;
				}
			}
			
			if ("HRYDJ008".equals(msgId)) {
				
				if ("".equals(sTreatGp)) {
					jrRtn.setField("RTN_CD"         , "0");	
					jrRtn.setField("RTN_MSG"        , "처리구분 항목값이 없습니다");
					return jrRtn;
				}
				if ("".equals(sWordProc)) {
					jrRtn.setField("RTN_CD"         , "0");	
					jrRtn.setField("RTN_MSG"        , "설비구분 항목값이 없습니다");
					return jrRtn;
				}
			}
			
			String ydBayGp    = "";
			String sTmpEqp    = "";
			String rcvYdEqpId = ydEqpId;
			if (!"HRYDJ008".equals(msgId)) {
				ydBayGp = ydEqpId.substring(1, 2);
				sTmpEqp = ydEqpId.substring(2, 3); //E동 SPM2 경우 DD, DE -> KE, KD로 변환
				if ("D".equals(sTmpEqp)) {
					ydEqpId = "JEK" + ydEqpId.substring(3, 4) + "02";
				} else {
					ydEqpId = "J" + ydEqpId.substring(1);
				}
			}
			
			/**********************************************************
			* 1. 정정 입측 Line-In 요구
			**********************************************************/
			if ("H2YDL001".equals(msgId) //SPM1 입측 Line-In요구
			 || "H2YDL011".equals(msgId) //HFL1 입측 Line-In요구
			 ||	"H2YDL021".equals(msgId) //SPM2 입측 Line-In요구 (기존설비 HEDE01)
			 ||	"H2YDL031".equals(msgId) //SPM3 입측 Line-In요구
			 ||	"H2YDL041".equals(msgId) //SPM4 입측 Line-In요구
			 || "H2YDL051".equals(msgId) //HFL4 입측 Line-In요구
			 || "H2YDL071".equals(msgId) //SPM5 입측 Line-In요구
			) {
				ydSchCd = ydEqpId.substring(0, 4) + "01UH";
				
				commUtils.printLog(logId, "생성 YD_SCH_CD : " + ydSchCd, "SL");

				// 20.10.26 현업요청 : 보급순서 체크 안함. 보급 요구시 작업예약 상시 생성
				/* com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getShearSupSch
				SELECT *
				  FROM USRYDA.TB_YD_CRNSCH
				 WHERE DEL_YN    = 'N'
				   AND YD_BAY_GP = :V_YD_BAY_GP
				   AND 'Y' = CASE WHEN YD_SCH_CD IN ('JAKD02LH', 'JBKD02LH', 'JCKD02LH', 'JEKD02LH', 'JHKD02LH'
				                                   , 'JCFD02LH', 'JGFD02LH') THEN 'Y' -- 재작업보급
				                  WHEN YD_WRK_PROG_STAT = 'W' -- 보급은 권상중인 스케쥴 제외
				                   AND YD_SCH_CD IN ('JAKE01UH', 'JBKE01UH', 'JCKE01UH', 'JEKE01UH', 'JHKE01UH'
				                                   , 'JCFE01UH', 'JGFE01UH')
				                  THEN 'Y'
				                  ELSE 'N'
				             END
				*/
				/*
				jrParam.setField("YD_BAY_GP", ydBayGp);
				jsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getShearSupSch", logId, mthdNm, "보급스케줄 조회");
				if (jsRst.size() > 0) {
					sMsg = "보급(재작업)스케줄작업이 존재합니다.(보급순서유지)";
					commUtils.printLog(logId, sMsg, "SL");
					jrRtn.setField("RTN_CD"         , "0");	
					jrRtn.setField("RTN_MSG"        , sMsg);
					return jrRtn;
				}
				*/
				
				jrParam.setField("YD_EQP_ID", rcvYdEqpId);
				
				EJBConnector ejbConn = new EJBConnector("default", "HrShrWoMgtFaEJB", this);
				ejbConn.trx("rcvHrShrSupDmdAuto", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				
				jsRst = JDTORecordFactory.getInstance().createRecordSet("");
				
				jrParam.setField("YD_EQP_ID", ydEqpId);
				
				if (!"".equals(sStlNo)) { //화면 백업(재료번호 있음)
					jrParam.setField("COIL_NO"  , sStlNo);
					
					/*
					SELECT A.*
					     , D.*
					     , (SELECT YD_WBOOK_ID
					          FROM USRYDA.TB_YD_WRKBOOKMTL
					         WHERE STL_NO = A.COIL_NO AND DEL_YN ='N' AND ROWNUM <= 1
					       ) AS YD_WBOOK_ID
					  FROM(
					       SELECT A.COIL_NO
					            , A.STEP_NO
					            , A.WORK_STAT
					            , A.WORD_UNIT_NAME
					            , B.PUT_PRIOR
					            , A.WORD_UNIT_SEQNO AS WORD_UNIT_SEQNOA
					            , SYSDATE AS SUPPLY_DEMAND_DT
					            , NVL(SUBSTR(SL.YD_STK_COL_GP,2,5)||SUBSTR(SL.YD_STK_LYR_NO,3,1)||SL.YD_STK_BED_NO 
					               ,(SELECT SUBSTR(YD_STK_COL_GP,2,5)||SUBSTR(YD_STK_LYR_NO,3,1)||YD_STK_BED_NO 
					                   FROM TB_YD_WRKBOOKMTL 
					                  WHERE STL_NO = A.COIL_NO 
					                    AND DEL_YN = 'N'
					                    AND ROWNUM<=1)
					             ) AS LOC  -- 저장위치 
					        FROM TB_HR_C_SHEARWOWR   A        
					           , TB_HR_C_SHEARWOUNIT B
					           , TB_YD_STKLYR        SL
							   , (SELECT :V_YD_EQP_ID AS P_YD_EQP_ID FROM DUAL) D
					       WHERE A.WORD_UNIT_NAME = B.WORD_UNIT_NAME(+)
					         AND A.COIL_NO        = SL.STL_NO 
					         AND A.PTOP_PLNT_GP   = 'HC'
					         AND A.WORD_UNIT_NAME NOT LIKE DECODE( D.P_YD_EQP_ID ,'JEKE02','EH' ,'JCKE03','CH','JBKE04','BH','JAKE05','AH',' ')||'%'
     				         AND A.WORD_UNIT_NAME LIKE DECODE( D.P_YD_EQP_ID ,'JAKE05','A','JHKE01','H','JBKE04','B','JCKE03','C'
                                                ,'JGFE01','GH','JBFE05','BH','JCFE04','CH','JEKE02','E'
                                                ,'JFFE02','FH','JDFE03','DH'
                                                ,'JHKD01','HH','JEKD02','EH','JAKD05','AH'   --// 공냉재추가 2020.02.12
                                                ,'JCKD03','XH','JBKD04','YH' --// 공냉재추가2 2020.07.29
                                                ,'XX')||'%'         
					         AND A.WORK_STAT = 'B'        
					         AND A.COIL_NO   = :V_COIL_NO      
					         AND SUBSTR(SL.YD_STK_COL_GP,3,2) BETWEEN '01' AND '99' --야드 위치에서만 대상이 나오도록
					         AND SL.YD_STK_LYR_MTL_STAT IN ('C','U')
					         AND SL.YD_STK_COL_GP LIKE 'J%' 
					         AND A.STEP_NO = (SELECT MAX(Y.STEP_NO)  
					                            FROM TB_HR_C_SHEARWOWR Y   
					                           WHERE Y.COIL_NO = A.COIL_NO )  
					      ) A
					     ,(SELECT A.YD_SCH_CD 
					            , B.STL_NO 
					            , A.YD_TO_LOC_GUIDE 
					            , A.YD_CRN_SCH_ID
					         FROM TB_YD_CRNSCH    A
					            , TB_YD_CRNWRKMTL B
					        WHERE A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID
					          AND A.DEL_YN ='N' 
					          AND B.DEL_YN ='N' 
					          AND A.YD_GP  ='J'
					      ) D 
					 WHERE A.COIL_NO = D.STL_NO(+)                         
					   AND D.YD_CRN_SCH_ID IS NULL                  
					 ORDER BY A.PUT_PRIOR
					        , A.WORD_UNIT_SEQNOA
					 */
					//기존com.inisteel.cim.yd.jsp.coiljsp.dao.CoilYDDao.getLineSupplyOrderNEWStlNo 
					jsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getShearSupTrgtStlNo", logId, mthdNm, "보급대상 조회(백업)");
					
				} else {

					/*
					SELECT A.*
					     , D.*
					     , (SELECT WK.YD_WBOOK_ID  --보급 대상 작업 예약만 가져 온다
					          FROM USRYDA.TB_YD_WRKBOOK WK
					             , USRYDA.TB_YD_WRKBOOKMTL WL
					         WHERE WK.YD_WBOOK_ID = WL.YD_WBOOK_ID
					           AND WK.YD_SCH_CD LIKE SUBSTR(A.P_YD_EQP_ID,1,4) ||'%'
					           AND WL.STL_NO = A.COIL_NO 
					           AND WL.DEL_YN ='N' 
					           AND ROWNUM <= 1
					       ) AS YD_WBOOK_ID
					  FROM(
					        SELECT A.COIL_NO
					             , A.STEP_NO
					             , A.WORK_STAT
					             , A.WORD_UNIT_NAME
					             , B.PUT_PRIOR
					             , A.WORD_UNIT_SEQNO AS WORD_UNIT_SEQNOA
					             , SYSDATE AS SUPPLY_DEMAND_DT
					             , NVL(SUBSTR(SL.YD_STK_COL_GP,2,5)||SUBSTR(SL.YD_STK_LYR_NO,3,1)||SL.YD_STK_BED_NO 
					                 ,(SELECT SUBSTR(YD_STK_COL_GP,2,5)||SUBSTR(YD_STK_LYR_NO,3,1)||YD_STK_BED_NO 
					                     FROM TB_YD_WRKBOOKMTL 
					                    WHERE STL_NO = A.COIL_NO 
					                      AND DEL_YN = 'N'
					                      AND ROWNUM <= 1)
					              ) AS LOC  -- 저장위치 
					             , P_YD_EQP_ID          
					          FROM TB_HR_C_SHEARWOWR   A        
					             , TB_HR_C_SHEARWOUNIT B
					             , TB_YD_STKLYR        SL
					             ,(SELECT :V_YD_EQP_ID AS P_YD_EQP_ID FROM DUAL) D
					         WHERE A.WORD_UNIT_NAME = B.WORD_UNIT_NAME(+)
					           AND A.COIL_NO        = SL.STL_NO 
					           AND A.PTOP_PLNT_GP   = 'HC'
					           AND A.WORD_UNIT_NAME NOT LIKE DECODE(D.P_YD_EQP_ID,'HEDE01','EH' ,'HCKE03','CH','HBKE04','BH','HAKE05','AH','  ')||'%'
			                   AND A.WORD_UNIT_NAME LIKE DECODE( D.P_YD_EQP_ID ,'JAKE05','A','JHKE01','H','JBKE04','B','JCKE03','C'
                                                ,'JGFE01','GH','JBFE05','BH','JCFE04','CH','JEKE02','E'
                                                ,'JFFE02','FH','JDFE03','DH'
                                                ,'JHKD01','HH','JEKD02','EH','JAKD05','AH'   --// 공냉재추가 2020.02.12
                                                ,'JCKD03','XH','JBKD04','YH' --// 공냉재추가2 2020.07.29
                                                ,'XX')||'%'         
					           AND A.WORK_STAT      = 'B' 
					           AND SUBSTR(SL.YD_STK_COL_GP,3,2) BETWEEN '01' AND '99' --야드 위치에서만 대상이 나오도록
					           AND SL.YD_STK_LYR_MTL_STAT IN ('C','U')
					           AND SL.YD_STK_COL_GP LIKE 'J%' 
					           AND A.STEP_NO = (SELECT MAX(Y.STEP_NO)  
					                              FROM TB_HR_C_SHEARWOWR Y   
					                             WHERE Y.COIL_NO = A.COIL_NO )  
					      ) A
					     ,(SELECT A.YD_SCH_CD 
					            , B.STL_NO 
					            , A.YD_TO_LOC_GUIDE 
					            , A.YD_CRN_SCH_ID
					         FROM TB_YD_CRNSCH    A
					            , TB_YD_CRNWRKMTL B
					        WHERE A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID
					          AND A.DEL_YN = 'N' 
					          AND B.DEL_YN = 'N' 
					          AND A.YD_GP  = 'J'
					      ) D 
					 WHERE A.COIL_NO = D.STL_NO(+)                         
					   AND D.YD_CRN_SCH_ID IS NULL                  
					 ORDER BY A.PUT_PRIOR
					        , A.WORD_UNIT_SEQNOA 
					 */
					//기존 com.inisteel.cim.yd.jsp.coiljsp.dao.CoilYDDao.getLineSupplyOrderNEW
					jsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getShearSupTrgt", logId, mthdNm, "보급대상 조회");
					
				}
				
				int nRst = jsRst.size();
				
				if (nRst > 0) {
					commUtils.printLog(logId, "열연 정정 Line-In 대상 ["+nRst+"]건 조회 성공", "SL");
				} else  {
					
					sMsg = "2열연 정정Line-In 데이터가 없습니다."; 
					//throw new Exception(sMsg);
					
					commUtils.printLog(logId, sMsg, "SL");
			      	jrRtn.setField("RTN_CD"         , "0");	
			      	jrRtn.setField("RTN_MSG"        , sMsg);
					commUtils.printLog(logId, mthdNm, "S-");
					return jrRtn;
				}
				
				/*
				 * 작업예약 생성
				 */
				String sSupplyDemandDtChk  	= "";
				String sSupplyDemandDt     	= "";
				String sLoc                	= "";
				String sCoilNo      		= "";
				
				for (int ii = 1; ii <= nRst; ++ii) {
					jsRst.absolute(ii);
					
					sSupplyDemandDt  = jsRst.getRecord().getFieldString("SUPPLY_DEMAND_DT");
					
					if (ii == 1) {
						sSupplyDemandDtChk = sSupplyDemandDt;
						sFirstYdWookId   = commUtils.nvl(jsRst.getRecord().getFieldString("YD_WBOOK_ID"), "");
						sFirstYdSchCd    = ydSchCd;
						commUtils.printLog(logId, "sFirstYdWookId 가이드 :"+sFirstYdWookId, "SL");
					}
					
					if (sSupplyDemandDt.equals(sSupplyDemandDtChk)) { //지시일시
						
						sLoc = jsRst.getRecord().getFieldString("LOC");
						
						if (sLoc.substring(0,1).equals(ydEqpId.substring(1,2))) { // 저정위치와 설비 같은 동
						
							if (!"TC".equals(sLoc.substring(1, 3))) { //대차위가 아니면
								
								ydWookId = jsRst.getRecord().getFieldString("YD_WBOOK_ID");
								sCoilNo  = jsRst.getRecord().getFieldString("COIL_NO");
								commUtils.printLog(logId, "스케줄코드:["+ydSchCd+"](첫번째)작업예약 생성 완료:["+sFirstYdWookId+"] >> 예약생성후 번호 : ["+ydWookId+"]", "SL");
					            
								if ("".equals(ydWookId)) {
									
									jrParam.setField("YD_SCH_CD"         , ydSchCd); //스케줄코드
									jrParam.setField("STL_SH"            , "1"       ); //재료매수
									jrParam.setField("STL_NO1"           , sCoilNo  );
									jrParam.setField("YD_TO_LOC_DCSN_MTD", "F"       );
									commUtils.printLog(logId, "가이드 :"+ydEqpId.substring(0 , 6), "SL");
									jrParam.setField("TO_YD_STK_BED_NO"  , ydEqpId.substring(0 , 6) + "00");
									// 작업예약 등록 호출
									ejbConn = new EJBConnector("default" , "CCoilWrkBookSeEJB" , this);
									JDTORecord outRecord = (JDTORecord) ejbConn.trx("insWrkBookTx" , new Class[]{JDTORecord.class} , new Object[]{jrParam});
									
									ydWookId = commUtils.nvl(outRecord.getFieldString("YD_WBOOK_ID") , "");
									commUtils.printLog(logId, ydSchCd+" >> 2열연 Line-In 작업예약 생성 완료"+ydWookId, "SL");
									
									if ("".equals(sFirstYdWookId)) {
										sFirstYdWookId = ydWookId;
										sFirstYdSchCd  = ydSchCd;
									}
									
									
									commUtils.printLog(logId, "적용후(첫번째)작업예약 생성 완료:"+sFirstYdWookId, "SL");
								}
								
							}
						}
					}
				} //END FOR
			} // 1. 2열연 정정 입측 Line-In요구
			
			/**********************************************************
			* 2. 2열연 정정 입측 Take-In요구
			**********************************************************/
			if ("H2YDL004".equals(msgId) //SPM1 입측 Take-In요구
			 || "H2YDL014".equals(msgId) //HFL1 입측 Take-In요구
			 ||	"H2YDL024".equals(msgId) //SPM2 입측 Take-In요구
			 ||	"H2YDL034".equals(msgId) //SPM3 입측 Take-In요구
			 ||	"H2YDL044".equals(msgId) //SPM4 입측 Take-In요구
			 || "H2YDL054".equals(msgId) //HFL4 입측 Take-In요구
			 || "H2YDL074".equals(msgId) //SPM5 입측 Take-In요구
			) {
				
				// 설비별 보급요구 스케줄 코드 생성
				ydSchCd = ydEqpId.substring(0, 4) + "03UH";
				commUtils.printLog(logId, "생성 YD_SCH_CD : " + ydSchCd, "SL");
				
				jrParam = commUtils.getParam(logId, mthdNm, sModifier);
				
				jrParam.setField("YD_SCH_CD"         , ydSchCd);// 스케줄코드
				jrParam.setField("STL_SH"            , "1"); // LINE_IN 재료매수
				jrParam.setField("STL_NO1"           , sStlNo);
				jrParam.setField("YD_TO_LOC_DCSN_MTD", "F");

				jrParam.setField("YD_AIM_YD_GP"      , ydEqpId.substring(0, 1));
				jrParam.setField("YD_AIM_BAY_GP"     , ydEqpId.substring(1, 2)); // 작업예약에 목표동 설정처리함
				jrParam.setField("TO_YD_STK_BED_NO"  , ydEqpId.substring(0, 6) + ydStkBedNo);
				
				// 작업예약 등록 호출
				EJBConnector ejbConn = new EJBConnector("default" , "CCoilWrkBookSeEJB" , this);
				JDTORecord outRecord = (JDTORecord) ejbConn.trx("insWrkBookTx" , new Class[]{JDTORecord.class} , new Object[]{jrParam});
				
				commUtils.printLog(logId, "2열연 Take-In 작업예약 생성 완료", "SL");
				
				ydWookId = commUtils.trim(outRecord.getFieldString("YD_WBOOK_ID"));
				commUtils.printLog(logId, ydSchCd+" >> 2열연  Take-In 작업예약 생성 완료"+ydWookId, "SL");
				if ("".equals(sFirstYdWookId)) {
					sFirstYdWookId = ydWookId;
					sFirstYdSchCd  = ydSchCd;
				}
			}
			
			/**********************************************************
			* 3. 2열연 정정입측 보급Lot 편성 백업
			*    C열연정정보급요구
			**********************************************************/
			if ("HRYDJ008".equals(msgId)) {
				
				jrParam = commUtils.getParam(logId, mthdNm, sModifier);
				jrParam.setField("WORD_PROC"		, sWordProc);
				jrParam.setField("WORD_UNIT_NAME"	, sWordUnitName); // FHsampl(F동크래들롤), DHsampl(D동크래들롤)
				jrParam.setField("STL_NO"			, sStlNo);
				
				/* com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getSpmHflLineSupplyOrderNew
				WITH P_PARAM AS (
				    SELECT :V_WORD_PROC      AS WORD_PROC
				         , :V_WORD_UNIT_NAME AS WORD_UNIT_NAME
				         , :V_STL_NO         AS STL_NO
				      FROM DUAL
				)
				SELECT A.SUPPLY_DEMAND_DT
				     , B.STL_NO
				     , A.PUT_PRIOR
				     , A.WORD_UNIT_SEQNO
				     , A.WORD_UNIT_NAME
				     , B.YD_STK_COL_GP
				     , CASE WHEN SUBSTR(A.YD_EQP_ID, 0, 2) = SUBSTR(B.YD_STK_COL_GP, 0, 2) THEN 'Y'
				            ELSE 'N'
				       END AS LOC_CHK
				     , A.YD_EQP_ID || '01UH' AS YD_SCH_CD
				     , A.YD_EQP_ID || DECODE(SUBSTR(A.YD_EQP_ID, 0, 3)
				                           , 'JHK', '01', 'JEK', '02', 'JCK', '03', 'JBK', '04', 'JAK', '05'
				                           , 'JGF', '01', 'JFF', '02', 'JDF', '03', 'JCF', '04', 'JBF', '05'
				                           , 'JFC', '01', 'JDC', '01', 'XX' ) AS TO_YD_STK_COL_GP
				  FROM (
				        SELECT AA.SUPPLY_DEMAND_DT
				             , AA.COIL_NO
				             , BB.PUT_PRIOR
				             , AA.WORD_UNIT_SEQNO
				             , AA.WORD_UNIT_NAME
				             , CASE WHEN AA.WORD_UNIT_NAME = 'FHsampl' THEN 'JFCD' -- F동 크래들롤
				                    WHEN AA.WORD_UNIT_NAME = 'DHsampl' THEN 'JDCD' -- D동 크래들롤
				                    -- HFL ex) [J][G][FE]
				                    WHEN SUBSTR(AA.WORD_UNIT_NAME, 0, 2) IN ('GH', 'FH', 'DH', 'CH', 'BH')
				                         THEN 'J' || SUBSTR(AA.WORD_UNIT_NAME, 0, 1)
				                            ||'FE'
				                    -- SPM ex) [J][H][K][E]
				                    ELSE
				                         'J'|| DECODE(SUBSTR(AA.WORD_UNIT_NAME, 0, 1), 'X', 'C', 'Y', 'B', SUBSTR(AA.WORD_UNIT_NAME, 0, 1))
				                       ||'K'|| DECODE(SUBSTR(AA.WORD_UNIT_NAME, 2, 1), 'H', 'D', 'E')
				               END AS YD_EQP_ID
				          FROM USRHRA.TB_HR_C_SHEARWOWR   AA
				             , USRHRA.TB_HR_C_SHEARWOUNIT BB
				             , P_PARAM                    P
				         WHERE AA.WORD_UNIT_NAME = BB.WORD_UNIT_NAME(+)
				           AND AA.PTOP_PLNT_GP   = 'HC'
				           AND AA.WORK_STAT      = 'B'
				           AND AA.COIL_NO     LIKE P.STL_NO ||'%'
				           AND 'Y' = CASE WHEN  P.WORD_UNIT_NAME IS NULL
				                           AND AA.WORD_UNIT_NAME LIKE P.WORD_PROC ||'%'
				                           AND AA.WORD_UNIT_NAME NOT IN ('FHsampl', 'DHsampl') THEN 'Y'
				                          WHEN  P.WORD_UNIT_NAME IS NOT NULL
				                           AND AA.WORD_UNIT_NAME = P.WORD_UNIT_NAME  THEN 'Y'
				                          ELSE 'N'
				                     END
				           AND NOT EXISTS (SELECT  1
				                             FROM TB_YD_WRKBOOK    WB
				                                , TB_YD_WRKBOOKMTL WM
				                            WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
				                              AND WB.YD_GP       = 'J'
				                              AND WB.DEL_YN      = 'N'
				                              AND WM.DEL_YN      = 'N'
				                              AND WM.STL_NO      = AA.COIL_NO
				                          )
				       ) A
				     , TB_YD_STKLYR B
				     , TB_YD_STKCOL C
				 WHERE A.COIL_NO       = B.STL_NO
				   AND B.YD_STK_COL_GP = C.YD_STK_COL_GP(+)
				   AND C.YD_GP         = 'J'
				   AND C.YD_LOC_GP     = 'H'
				   AND B.DEL_YN        = 'N'
				   AND C.DEL_YN        = 'N'
				   AND SUBSTR(A.YD_EQP_ID, 0, 2) = SUBSTR(B.YD_STK_COL_GP, 0, 2)
				   AND SUBSTR(B.YD_STK_COL_GP, 3, 2) BETWEEN '00' AND '99'
				 ORDER BY A.PUT_PRIOR, A.WORD_UNIT_SEQNO
				*/
				jsRst = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getSpmHflLineSupplyOrderNew", logId, mthdNm, "2열연 정정보급요구 조회");
				if (jsRst.size() < 1) {
					throw new Exception("조업열연정정지시실적작업관리 정보 없음");
				}
				
				String sToYdStkColGp	= "";
				String tmpYdStkColGp	= "";
				String sLocChk			= "";
				for (int ii = 1; ii <= jsRst.size(); ii++) {
					
					jsRst.absolute(ii);
					
					ydSchCd			= commUtils.trim(jsRst.getRecord().getFieldString("YD_SCH_CD"));
					sToYdStkColGp	= commUtils.trim(jsRst.getRecord().getFieldString("TO_YD_STK_COL_GP"));
					sStlNo			= commUtils.trim(jsRst.getRecord().getFieldString("STL_NO"));
					tmpYdStkColGp	= commUtils.trim(jsRst.getRecord().getFieldString("YD_STK_COL_GP"));
					sLocChk			= commUtils.trim(jsRst.getRecord().getFieldString("LOC_CHK"));
					
					sMsg = "   >> "+ ii +".STL_NO["+ sStlNo +"] LOC_CHK["+ sLocChk +"] YD_STK_COL_GP["+ tmpYdStkColGp +"] YD_SCH_CD["+ ydSchCd +"] TO_YD_STK_COL_GP["+ sToYdStkColGp +"]";
			      	commUtils.printLog(logId, sMsg, "SL");
			      	
					if( !"Y".equals(sLocChk) ) {
						sMsg = "   >> error 설비동과 적치동의 위치가 다릅니다.";
						commUtils.printLog(logId, sMsg, "SL");
						continue;
					}
					
					// ex) JHKE01, JEKE02... JGFE01, JBFE05
					ydEqpId			= commUtils.trim(jsRst.getRecord().getFieldString("TO_YD_STK_COL_GP"));
					
					if( "".equals(ydSchCd) || "".equals(sToYdStkColGp) ) {
						sMsg = "   ERROR YD_SCH_CD["+ ydSchCd +"] TO_YD_STK_COL_GP["+ sToYdStkColGp +"] STL_NO["+ sStlNo +"]";
						commUtils.printLog(logId, sMsg, "SL");
				      	jrRtn.setField("RTN_CD"		, "0");	
				      	jrRtn.setField("RTN_MSG"	, sMsg);
						commUtils.printLog(logId, mthdNm, "S-");
						return jrRtn;
					}
					
					// 정정지시 대상재 건수 만큼 Loop 돌면서 작업예약등록 호출
					jrParam = commUtils.getParam(logId, mthdNm, sModifier);
					jrParam.setField("YD_SCH_CD"			, ydSchCd);// 스케줄코드
					jrParam.setField("STL_SH"				, "1"); // LINE_IN 재료매수
					jrParam.setField("STL_NO1"				, sStlNo);
					jrParam.setField("YD_TO_LOC_DCSN_MTD"	, "S");
					jrParam.setField("YD_AIM_YD_GP"			, sToYdStkColGp.substring(0, 1));
					jrParam.setField("YD_AIM_BAY_GP"		, sToYdStkColGp.substring(1, 2)); // 작업예약에 목표동 설정처리함
					jrParam.setField("TO_YD_STK_BED_NO"		, sToYdStkColGp );
					
					// 작업예약 등록 호출
					EJBConnector ejbConn = new EJBConnector("default", "CCoilWrkBookSeEJB", this);
					JDTORecord jrRecord = (JDTORecord)ejbConn.trx("insWrkBookTx", new Class[]{JDTORecord.class}, new Object[]{jrParam});
					
					String sRtnCd = commUtils.trim(jrRecord.getFieldString("RTN_CD"));
					if ("0".equals(sRtnCd)) {
						jrRtn.setField("RTN_CD" , "0");	
						jrRtn.setField("RTN_MSG", "작업예약 등록 실패");
						return jrRtn;							
					}
					
					commUtils.printLog(logId, "2열연 Line-In 작업예약 생성 완료", "SL");
					
					ydWookId = commUtils.trim(jrRecord.getFieldString("YD_WBOOK_ID"));
					commUtils.printLog(logId, ydSchCd+" >> 2열연 Line-In 작업예약 생성 완료"+ydWookId, "SL");
					if (ii == 1) {					
						
			    		// 2023.10.30 가장 최근 작업 예약 호출-------------------------------------------
						/* com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getCrnSchWbookSchCd 
						SELECT A.YD_WBOOK_ID
						  FROM (SELECT WB.YD_WBOOK_ID
						             , SR.YD_SCH_DIV_GP
						          FROM TB_YD_SCHRULE SR
						              ,TB_YD_WRKBOOK WB
						         WHERE SR.YD_SCH_CD       = WB.YD_SCH_CD
						           AND WB.YD_SCH_CD      = :V_YD_SCH_CD
						           AND SR.YD_SCH_PROH_EXN = 'N'
						           AND SR.DEL_YN          = 'N'
						           AND WB.DEL_YN          = 'N'
						           AND WB.YD_WBOOK_ID NOT IN (SELECT YD_WBOOK_ID
						                                        FROM TB_YD_CRNSCH
						                                       WHERE DEL_YN = 'N')
						         ORDER BY WB.YD_SCH_PRIOR, WB.YD_WBOOK_ID) A
						 WHERE ROWNUM = 1
						*/ 
						JDTORecordSet jsYdCrnSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilSchSeEJB.getCrnSchWbookSchCd", logId, mthdNm, "작업예약 조회");
						
						if (jsYdCrnSch != null && jsYdCrnSch.size() > 0) {
							ydWookId	= commUtils.trim(jsYdCrnSch.getRecord(0).getFieldString("YD_WBOOK_ID"));
						} 
						//----------------------------------------------------------------------
						
						sFirstYdWookId = ydWookId;
						sFirstYdSchCd   = ydSchCd;
						
					}
				}
			}
			
			/**********************************************************
			* 4. 스케줄 기동
			**********************************************************/
			if (("HRYDJ008".equals(msgId) && (
				     "JHKE01".equals(ydEqpId) 
				  || "JHKD01".equals(ydEqpId)
				  || "JEKE02".equals(ydEqpId)
				  || "JEKD02".equals(ydEqpId)
				  || "JCKE03".equals(ydEqpId)
				  || "JCKD03".equals(ydEqpId)
				  || "JBKE04".equals(ydEqpId)
				  || "JBKD04".equals(ydEqpId)
				  || "JAKE05".equals(ydEqpId)
				  || "JAKD05".equals(ydEqpId)
				  || "JGFE01".equals(ydEqpId)
				  || "JCFE04".equals(ydEqpId))
			   ) || "TC".equals(ydSchCd.substring(2, 4))) {
				commUtils.printLog(logId, "HRYDJ008 && 열연 조업 추출, 대차 작업요구는 스케쥴 기동 안함", "SL");
			} else {
				// 20.06.01 결속장 스케쥴도 기동처리함
//				if ("JBFE01UH".equals(ydSchCd)
//				 || "JDFE01UH".equals(ydSchCd)
//				 || "JFFE01UH".equals(ydSchCd)) {
					//HFL결속장에 대한  스케줄 기동 작업 SKIP	
//					commUtils.printLog(logId, "스케쥴 기동 안함", "SL");
//				} else {
				
				if ("JFFE02".equals(ydEqpId) || "JDFE03".equals(ydEqpId) || "JBFE05".equals(ydEqpId)
				 || "JFCD01".equals(ydEqpId) || "JDCD01".equals(ydEqpId)) {
					jrParam.setField("YD_EQP_ID", ydEqpId);
					/*
					SELECT *
					  FROM TB_YD_STKLYR
					 WHERE DEL_YN = 'N'
					   AND YD_STK_COL_GP = :V_YD_EQP_ID
					   AND YD_STK_BED_NO != '00'
					   AND YD_STK_LYR_ACT_STAT = 'E'
					   AND YD_STK_LYR_MTL_STAT = 'E'
					 */
					JDTORecordSet jsAblLoc = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getBindPlcStkAbleCnt", logId, mthdNm, "결속장 적치가능 bed 조회");
					
					if (jsAblLoc.size() == 0) {
						jrRtn.setField("RTN_CD"  , "1");	
						jrRtn.setField("RTN_MSG" , "결속장,크래들롤 작업가능 위치 없음. 작업예약만 생성 후 종료");
						commUtils.printLog(logId, mthdNm, "S-");
						return jrRtn;
					}
				}
				 
				 /**********************************************
				 *   보급 크레인스케줄 기동 YYS 202303
				 *  - 크레인작업이 없을 때 작업예약에 있는것 기동
				 **********************************************/
				String sAPP021_YN = coilDao.ApplyYn(logId, mthdNm, "APP001","J","021"); 
				
				String ydEqpKD = "KE";
				if ("Y".equals(sAPP021_YN)){
					ydEqpKD = ydEqpId.substring(2, 4);
				}
				commUtils.printLog(logId, "정보작업 스케줄기동 : " + sAPP021_YN+" >> ydEqpKD : "+ydEqpKD, "SL");
				
				if ( ( "H2YDL011".equals(msgId) 
						|| "H2YDL051".equals(msgId)
						|| "H2YDL001".equals(msgId) //SPM1
						|| "H2YDL021".equals(msgId) //SPM2
						|| "H2YDL031".equals(msgId) //SPM3
						|| "H2YDL041".equals(msgId) //SPM4
						|| "H2YDL071".equals(msgId) //SPM5
						) && !"KD".equals(ydEqpKD)
					) {
					commUtils.printLog(logId, "작업예약만 진행 >>>>> 전문 : " + msgId+" >> 설비위치 : "+ydEqpId+" >> ydSchCd : "+ydSchCd, "SL"); 
					
					//H2YDL004 H2YDL024 H2YDL034 H2YDL044 H2YDL074 H2YDL014 H2YDL054 Take-In
					//H2YDL001 H2YDL021 H2YDL031 H2YDL041 H2YDL071 H2YDL011 H2YDL051 Line-In
					//SPM1   , SPM2   , SPM3   , SPM4   , SPM5   , #1HFL  , #4HFL JAKD05
				}else{
					if (!"".equals(sFirstYdWookId)){
						jrParam = commUtils.getParam(logId, mthdNm, sModifier);
						
						jrParam.setField("YD_SCH_CD"    , sFirstYdSchCd );// 스케줄코드
						jrParam.setField("YD_WBOOK_ID"  , sFirstYdWookId);
						commUtils.printLog(logId, "스케줄코드 : ["+sFirstYdSchCd+"] >> 스케줄생성시 예약아이디 : "+sFirstYdWookId, "SL");
						
						EJBConnector ejbConn = new EJBConnector("default" , "CCoilSchSeEJB" , this);
						JDTORecord jrEjb = (JDTORecord)ejbConn.trx("procYDYDJ551" , new Class[]{JDTORecord.class} , new Object[]{ jrParam });
						
						jrRtn = commUtils.addSndData(jrRtn, jrEjb);
						
						String rtnCd	= commUtils.nvl(jrEjb.getFieldString("RTN_CD"), "0");
						String rtnMsg	= commUtils.nvl(jrEjb.getFieldString("RTN_MSG"), "");
						commUtils.printLog(logId, "◆◆◆ rtnCd  :"+ rtnCd , "SL");
						commUtils.printLog(logId, "◆◆◆ rtnMsg :"+ rtnMsg, "SL");
					}
				
			   }
					
			}
			
			jrRtn.setField("YD_SCH_CD"  , sFirstYdSchCd);
			jrRtn.setField("YD_WBOOK_ID", sFirstYdWookId);
			jrRtn.setField("RTN_CD"  , "1");	
			jrRtn.setField("RTN_MSG" , "2열연정정Line-In요구/2열연 정정보급Lot편성/2열연정정 Take-In요구 수신정상처리");
			commUtils.printLog(logId, mthdNm, "S-");
			
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}	
	
	/**
	 *      [A] 오퍼레이션명 : H1압연분기Line-Off요구(H1YDL001)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvH1YDL001(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "H1압연분기Line-Off요구[CCoilL3RcvSeEJB.rcvH1YDL001] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
    
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			String msgId     = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId   = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID" )); //야드설비ID
			String sStlNo    = commUtils.trim(rcvMsg.getFieldString("STL_NO"    )); //재료번호
			String ydDstrGp	 = commUtils.trim(rcvMsg.getFieldString("YD_DSTR_GP")); //야드분기구분

			String sModifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }

			/**********************************************************
			* 0. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				jrRtn.setField("RTN_CD" , "0");	
		      	jrRtn.setField("RTN_MSG", "야드설비ID 정보가 없습니다");	
		        return jrRtn;
			}
			if ("".equals(sStlNo)) {
				jrRtn.setField("RTN_CD" , "0");	
		      	jrRtn.setField("RTN_MSG", "재료번호 정보가 없습니다");	
		        return jrRtn;
			}
			if ("".equals(ydDstrGp)) {
				jrRtn.setField("RTN_CD" , "0");	
		      	jrRtn.setField("RTN_MSG", "야드분기구분 정보가 없습니다");	
		        return jrRtn;
			}
			
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_EQP_ID"  , ydEqpId );
			jrParam.setField("STL_NO"     , sStlNo  );
			jrParam.setField("COIL_NO"    , sStlNo  );
			
			jrParam.setField("YD_DSTR_GP" , ydDstrGp); //?

			/*************************************** 
			 * 	1.작업예약이 존재하면 ERROR
			 **************************************/	
			/* com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYdWBookYN   
			SELECT DECODE(COUNT(*),0,'N','Y') AS WB_STL_YN --작업예약재료여부
			  FROM TB_YD_WRKBOOKMTL WM
			     , TB_YD_WRKBOOK    WB
			WHERE WM.YD_WBOOK_ID    = WB.YD_WBOOK_ID
			  AND WM.STL_NO         = :V_STL_NO
			  AND WM.DEL_YN         = 'N'
			  AND WB.DEL_YN         = 'N'
			*/ 
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYdWBookYN", logId, mthdNm, "작업예약 등록여부");

			commUtils.printLog(logId, mthdNm, "S+");
			if (jsChk != null && jsChk.size() > 0) {
				if ("Y".equals(jsChk.getRecord(0).getFieldString("WB_STL_YN"))) {
					jrRtn.setField("RTN_CD" , "0");	
			      	jrRtn.setField("RTN_MSG", "이미 작업예약에 등록된 재료:" + sStlNo);	
			        return jrRtn;
				}
			}	
			
			/**********************************************************
			* 1. 저장품 유무 Check
			*   -- 코일 공통을 Read 하여 없으면 생성 있으면 갱신
			**********************************************************/			
			/*
			SELECT *
			  FROM USRPTA.TB_PT_COILCOMM 
			 WHERE COIL_NO = :V_COIL_NO
			 */
			JDTORecordSet jsCoilcomm = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getPtCoilComm", logId, mthdNm, "코일공통조회");
			
			if (jsCoilcomm.size() <= 0) {
				jrRtn.setField("RTN_CD" , "0");	
		      	jrRtn.setField("RTN_MSG","코일공통 DATA 없음" + sStlNo);	
		        return jrRtn;
			}
			
			JDTORecord jrCoilComm = jsCoilcomm.getRecord(0);
			
			JDTORecordSet jsYdStock = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYdStock", logId, mthdNm, "저장품조회");
			if (jsYdStock.size() < 0) {
				jrRtn.setField("RTN_CD" , "0");	
		      	jrRtn.setField("RTN_MSG","저장품 조회중 Error." + sStlNo);	
		        return jrRtn;
			} 
			
			double dW_GP         = 0;
			String sW_GP         = "";
			String ydMtlItem	 = ""; 
						
			String[] rVal = new String[1];
			//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
			rVal = coilDao.getYdAimRtGp("C", jrParam);
			
			//실적발생 후 진도코드에 따른 목표야드 변경 작업
			if (rVal[1].equals("H") ||rVal[1].equals("G") ||rVal[1].equals("F")) {
				ydMtlItem = "CG"; // 제품
			} else {
				ydMtlItem = "CM"; // 소재
			}
			JDTORecord jrYdStock = commUtils.getParam(logId, mthdNm, sModifier);
			jrYdStock.setField("STL_NO"                 , commUtils.nvl(jrCoilComm.getFieldString("COIL_NO"       ), ""));
			jrYdStock.setField("YD_MTL_T"               , commUtils.nvl(jrCoilComm.getFieldString("COIL_T"        ), "")); 
			jrYdStock.setField("YD_MTL_W"               , commUtils.nvl(jrCoilComm.getFieldString("COIL_W"        ), ""));     
			jrYdStock.setField("YD_MTL_L"               , commUtils.nvl(jrCoilComm.getFieldString("COIL_LEN"      ), ""));
			jrYdStock.setField("YD_MTL_WT"              , commUtils.nvl(jrCoilComm.getFieldString("COIL_WT"       ), ""));
			jrYdStock.setField("COIL_INDIA"             , commUtils.nvl(jrCoilComm.getFieldString("COIL_INDIA"    ), ""));
			jrYdStock.setField("COIL_OUTDIA"            , commUtils.nvl(jrCoilComm.getFieldString("COIL_OUTDIA"   ), ""));
			jrYdStock.setField("PLNT_PROC_CD"           , commUtils.nvl(jrCoilComm.getFieldString("PLNT_PROC_CD"  ), ""));
			jrYdStock.setField("ORD_YEOJAE_GP"          , commUtils.nvl(jrCoilComm.getFieldString("ORD_YEOJAE_GP" ), ""));
			jrYdStock.setField("ORD_NO"                 , commUtils.nvl(jrCoilComm.getFieldString("ORD_NO"        ), "")); 
			jrYdStock.setField("ORD_DTL"                , commUtils.nvl(jrCoilComm.getFieldString("ORD_DTL"       ), "")); 
			jrYdStock.setField("HYSCO_TRANS_GP"         , commUtils.nvl(jrCoilComm.getFieldString("HYSCO_TRANS_GP"), ""));
			jrYdStock.setField("COOL_METHOD"            , commUtils.nvl(jrCoilComm.getFieldString("COOL_METHOD"   ), ""));
			jrYdStock.setField("COOL_DONE_GP"           , commUtils.nvl(jrCoilComm.getFieldString("COOL_DONE_GP"  ), ""));
			jrYdStock.setField("YD_CONVEYOR_BRANCH_CD"  , commUtils.nvl(jrCoilComm.getFieldString("BRANCH_CD"     ), ""));
			jrYdStock.setField("CUST_CD"                , commUtils.nvl(jrCoilComm.getFieldString("CUST_CD"       ), "")); 
			jrYdStock.setField("DEMANDER_CD"            , commUtils.nvl(jrCoilComm.getFieldString("DEMANDER_CD"   ), ""));
			jrYdStock.setField("HCR_GP"                 , commUtils.nvl(jrCoilComm.getFieldString("HCR_GP"        ), ""));
			jrYdStock.setField("ITEMNAME_CD"            , commUtils.nvl(jrCoilComm.getFieldString("ITEMNAME_CD"   ), ""));
			jrYdStock.setField("PTOP_PLNT_GP"           , commUtils.nvl(jrCoilComm.getFieldString("PTOP_PLNT_GP"  ), ""));

			jrYdStock.setField("STL_PROG_CD"			, commUtils.nvl(jrCoilComm.getFieldString("STL_PROG_CD"   ), ""));
			jrYdStock.setField("STL_APPEAR_GP"			, commUtils.nvl(jrCoilComm.getFieldString("STL_APPEAR_GP" ), ""));
			jrYdStock.setField("YD_MTL_ITEM"			, ydMtlItem);
			jrYdStock.setField("YD_AIM_YD_GP"			, "J");	
			jrYdStock.setField("YD_AIM_RT_GP"           , rVal[0]);
			
			//폭구분
			dW_GP = Double.parseDouble(commUtils.nvl(jrCoilComm.getFieldString("COIL_W" ), "0"));
			commUtils.printLog(logId, "dW_GP:" + dW_GP, "SL");

			if (dW_GP < 1601) {
				sW_GP = "M";
			} else {
				sW_GP = "L";
			}
			jrYdStock.setField("YD_MTL_W_GP"           , sW_GP);
			
			//외경그룹
			int    iOutDia = Integer.parseInt(commUtils.nvl(jrCoilComm.getFieldString("COIL_OUTDIA" ), "0"));
			String sOutDia = "";
			commUtils.printLog(logId, "iOUTDIA:" + iOutDia, "SL");
			if (iOutDia <= 1280) {
				sOutDia = "A";
			} else if (( iOutDia > 1280 )&&( iOutDia <= 1930)) { 
				sOutDia = "B";
			} else if ( iOutDia > 1930 ) {
				sOutDia = "C";
			}
			jrYdStock.setField("YD_COIL_OUTDIA_GRP_GP"	, sOutDia);
			
			if (jsYdStock.size() == 0) { //insert
				
				commDao.insert(jrYdStock, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.insYdStock", logId, mthdNm, "저장품등록");
			
			} else { //update
				
				commDao.update(jrYdStock, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.updYdStock", logId, mthdNm, "저장품수정");
			}
			
			/**********************************************************
			* 2. 저장품 제원 야드L2 전송 (YDY5L002)
			**********************************************************/
			JDTORecord jrSndMsg = JDTORecordFactory.getInstance().create();
			jrSndMsg.setField("JMS_TC_CD"			, "YDY5L002");
			jrSndMsg.setField("YD_INFO_SYNC_CD"		, "A");	//1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)
			jrSndMsg.setField("STL_NO"		        , sStlNo);
			jrSndMsg.setField("YD_STK_COL_GP"		, "J");
			
			jrRtn = commUtils.addSndData(jrRtn ,coilDao.getMsgL2("YDY5L002", jrSndMsg));
			
			/****************************************** 
			 * 3.MAP에 존재하는 저장품 삭제 후 신규 MAP 등록	 
			 *****************************************/
			/* com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdStklyrSTLNO 
			UPDATE TB_YD_STKLYR
			   SET MODIFIER = :V_MODIFIER
			     , MOD_DDTT = SYSDATE
			     , STL_NO   = NULL
			     , YD_STK_LYR_MTL_STAT = 'E' 
			 WHERE STL_NO = :V_STL_NO
			 */
			
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdStklyrSTLNO", logId, mthdNm, "MAP에 존재하는 저장품 삭제");

			// 	신규MAP에 등록

			String ydStkBedNo  = "00";
			ydEqpId            = "J" + ydEqpId.substring(1, 6);//2열연 H -> J
			//적치열구분 = 설비ID
			jrParam.setField("YD_STK_COL_GP"      , ydEqpId);
			jrParam.setField("YD_STK_BED_NO"      , ydStkBedNo);
			jrParam.setField("YD_STK_LYR_NO"      , "001");
			jrParam.setField("YD_STK_LYR_MTL_STAT", "C");
			
			/*  
			UPDATE TB_YD_STKLYR
			   SET MODIFIER = :V_MODIFIER 
			     , MOD_DDTT = SYSDATE
			     , STL_NO   = :V_STL_NO
			     , YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT 
			 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
			   AND YD_STK_BED_NO = :V_YD_STK_BED_NO 
			   AND YD_STK_LYR_NO = :V_YD_STK_LYR_NO  
			*/			
			commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStklyrInStlNo", logId, mthdNm, "적치단 수정");
			
			/**********************************************************
			* 4. 작업예약 생성
			**********************************************************/
			String ydSchCd = "J" + ydEqpId.substring(1, 6) + "LH";
			
			// 2025.10.20 RITM1477310 D동 수입 스케줄 분개 요청
			
			jrParam.setField("YD_BAY_GP"  , ydEqpId.substring(1, 2) );
			
			JDTORecordSet ydSchCdChange = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getydSchCdChangeYN", logId, mthdNm, "수입스케줄 분개처리여부 조회");
			
			String ydSchCdChangeYN = "N";
			if (ydSchCdChange.size() > 0) {
				ydSchCdChangeYN = commUtils.nvl(ydSchCdChange.getRecord(0).getFieldString("USE_YN"), "N");
			}		
			commUtils.printLog(logId, "ydSchCd : " +ydSchCd  + ", ydEqpId : " + ydEqpId + ", YD_BAY_GP : " + ydEqpId.substring(1, 2) + ", ydSchCdChangeYN : " + ydSchCdChangeYN , "SL") ;
			
			if ("JDCV01LH".equals(ydSchCd) && "Y".equals(ydSchCdChangeYN)) {
				String ydRouteGp    = coilDao.getCoilYdRouteGpCV(logId,mthdNm,ydSchCd,sStlNo);       //검색조건 행선
				if ("DA".equals(ydRouteGp)) {
					ydSchCd = "JDCV01LH";
				} else { // DH, DZ ( BK, EK ... )
					ydSchCd = "JDCV02LH";
				}
			}
			
			commUtils.printLog(logId, "수입스케쥴코드:"+ ydSchCd, "SL");
			
			jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_SCH_CD"         , ydSchCd); //스케줄코드
			jrParam.setField("STL_SH"            , "1"    ); //LINE_IN 재료매수
			jrParam.setField("STL_NO1"           , sStlNo );
			jrParam.setField("YD_TO_LOC_DCSN_MTD", "S"    );
			
			// 작업예약 생성 EJB 호출
			EJBConnector ejbConn = new EJBConnector("default" , "CCoilWrkBookSeEJB" , this);
			JDTORecord outRecord = (JDTORecord) ejbConn.trx("insWrkBookTx" , new Class[]{JDTORecord.class} , new Object[]{jrParam});
			
			String ydWbookId = commUtils.trim(outRecord.getFieldString("YD_WBOOK_ID"));
			
			commUtils.printLog(logId, "2열연압연분기Line-Off요구 수신 작업예약 생성 완료 YD_WBOOK_ID : " + ydWbookId, "SL");
				
			/**********************************************************
			* 크레인 작업 지시 가 있는 경우 기동
			**********************************************************/
			String sSchStartYn  = "N";
			/* com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getTrxRunSchYn 
			SELECT CASE WHEN COUNT(STL_NO) = COUNT(YD_WBOOK_ID) AND COUNT(STL_NO) = COUNT(YD_CRN_SCH_ID) THEN 'Y'
			            ELSE 'N' END SCH_START_YN
			            
			     , COUNT(STL_NO)        AS STL_CNT
			     , COUNT(YD_WBOOK_ID)   AS WB_CNT
			     , COUNT(YD_CRN_SCH_ID) AS CRN_CNT
			  FROM (
			SELECT ET.EQP_GP
			     , ET.STL_NO
			     , ET.SORT_SEQ
			     , (SELECT A.YD_WBOOK_ID
			          FROM TB_YD_WRKBOOK    A
			             , TB_YD_WRKBOOKMTL B
			         WHERE A.YD_WBOOK_ID = B.YD_WBOOK_ID
			           AND A.DEL_YN = 'N'
			           AND B.DEL_YN = 'N'
			           AND B.STL_NO = ET.STL_NO) AS YD_WBOOK_ID
			     , (SELECT A.YD_CRN_SCH_ID
			          FROM TB_YD_CRNSCH    A
			             , TB_YD_CRNWRKMTL B
			         WHERE A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID
			           AND A.DEL_YN = 'N'
			           AND B.DEL_YN = 'N'
			           AND B.STL_NO = ET.STL_NO) AS YD_CRN_SCH_ID
			  FROM TB_YD_EQPTRACKING ET
			 WHERE ET.EQP_CD = 'CV' ||SUBSTR(:V_YD_SCH_CD,2,1)
			   AND ET.SORT_SEQ > (SELECT SORT_SEQ FROM TB_YD_EQPTRACKING WHERE EQP_CD = 'CV' ||SUBSTR(:V_YD_SCH_CD,2,1) AND STL_NO = :V_STL_NO1 ) 
			 ORDER BY ET.SORT_SEQ DESC 
			) 
            */
			JDTORecordSet jsTrxRunSch = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getTrxRunSchYn", logId, mthdNm, "크레인 작업지시");
			if (jsTrxRunSch.size() > 0) {
				sSchStartYn    = commUtils.trim(jsTrxRunSch.getRecord(0).getFieldString("SCH_START_YN"));
			}
			
			if ("N".equals(sSchStartYn)) {
				/*****  스케쥴 기동 안함 *****/
				commUtils.printLog(logId, "수입시 스케쥴 기동 안함 " + ydWbookId, "SL");
				ydWbookId = "";
			}

			if (!"".equals(ydWbookId)) {
				/**********************************************************
				* 5. 스케줄 기동
				**********************************************************/
				jrParam = commUtils.getParam(logId, mthdNm, sModifier); 
				
				jrParam.setField("YD_SCH_CD"    , ydSchCd  );// 스케줄코드
				jrParam.setField("YD_WBOOK_ID"  , ydWbookId);
				
				EJBConnector ejbConnS = new EJBConnector("default" , "CCoilJspSeEJB" , this);
				JDTORecord jrRst = (JDTORecord)ejbConnS.trx("trxRunSchedule" , new Class[]{JDTORecord.class} , new Object[]{ jrParam });
				
				jrRtn = commUtils.addSndData(jrRtn, jrRst);
			}
			
			jrRtn.setField("RTN_CD"	, "1");	
			commUtils.printLog(logId, mthdNm, "S-");
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : SPM1 입측 Line-In요구(H2YDL001)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvH2YDL001(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "SPM1 입측 Line-In요구[CCoilL3RcvSeEJB.rcvH2YDL001] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
    
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			jrRtn = this.procCCoilShearInSupLotComp(rcvMsg);
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : SPM1 출측 Line-Off요구(H2YDL003)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvH2YDL003(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "SPM1 출측 Line-Off요구[CCoilL3RcvSeEJB.rcvH2YDL003] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
    
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			jrRtn = this.procCCoilShearOutLineOffReq(rcvMsg);
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : SPM1 Take-In요구(H2YDL004)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvH2YDL004(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "SPM1 Take-In요구[CCoilL3RcvSeEJB.rcvH2YDL004] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
    
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			jrRtn = this.procCCoilShearInSupLotComp(rcvMsg);
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : SPM1 Take-Out요구(H2YDL005)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvH2YDL005(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "SPM1 Take-Out요구[CCoilL3RcvSeEJB.rcvH2YDL005] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
    
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			jrRtn = this.procCCoilShearOutLineOffReq(rcvMsg);
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : SPM2 입측 Line-In요구(H2YDL021)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvH2YDL021(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "SPM2 입측 Line-In요구[CCoilL3RcvSeEJB.rcvH2YDL021] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
    
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			jrRtn = this.procCCoilShearInSupLotComp(rcvMsg);
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : SPM2 출측 Line-Off요구(H2YDL023)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvH2YDL023(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "SPM2 출측 Line-Off요구[CCoilL3RcvSeEJB.rcvH2YDL023] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
    
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			jrRtn = this.procCCoilShearOutLineOffReq(rcvMsg);
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : SPM2 Take-In요구(H2YDL024)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvH2YDL024(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "SPM2 Take-In요구[CCoilL3RcvSeEJB.rcvH2YDL024] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
    
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			jrRtn = this.procCCoilShearInSupLotComp(rcvMsg);
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : SPM2 Take-Out요구(H2YDL025)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvH2YDL025(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "SPM2 Take-Out요구[CCoilL3RcvSeEJB.rcvH2YDL025] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
    
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			jrRtn = this.procCCoilShearOutLineOffReq(rcvMsg); 
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}	
		
	/**
	 *      [A] 오퍼레이션명 : SPM3 입측 Line-In요구(H2YDL031)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvH2YDL031(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "SPM3 입측 Line-In요구[CCoilL3RcvSeEJB.rcvH2YDL031] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
    
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			jrRtn = this.procCCoilShearInSupLotComp(rcvMsg);
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : SPM3 출측 Line-Off요구(H2YDL033)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvH2YDL033(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "SPM3 출측 Line-Off요구[CCoilL3RcvSeEJB.rcvH2YDL033] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
    
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			jrRtn = this.procCCoilShearOutLineOffReq(rcvMsg);
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : SPM3 Take-In요구(H2YDL034)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvH2YDL034(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "SPM3 Take-In요구[CCoilL3RcvSeEJB.rcvH2YDL034] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
    
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			jrRtn = this.procCCoilShearInSupLotComp(rcvMsg);
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : SPM3 Take-Out요구(H2YDL035)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvH2YDL035(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "SPM1 Take-Out요구[CCoilL3RcvSeEJB.rcvH2YDL035] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
    
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			jrRtn = this.procCCoilShearOutLineOffReq(rcvMsg);
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : SPM4 입측 Line-In요구(H2YDL041)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvH2YDL041(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "SPM4 입측 Line-In요구[CCoilL3RcvSeEJB.rcvH2YDL041] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
    
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			jrRtn = this.procCCoilShearInSupLotComp(rcvMsg);
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}	
	
	/**
	 *      [A] 오퍼레이션명 : SPM4 출측 Line-Off요구(H2YDL043)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvH2YDL043(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "SPM4 출측 Line-Off요구[CCoilL3RcvSeEJB.rcvH2YDL043] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
    
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			jrRtn = this.procCCoilShearOutLineOffReq(rcvMsg);
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : SPM4 Take-In요구(H2YDL044)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvH2YDL044(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "SPM4 Take-In요구[CCoilL3RcvSeEJB.rcvH2YDL044] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
    
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			jrRtn = this.procCCoilShearInSupLotComp(rcvMsg);
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : SPM4 Take-Out요구(H2YDL045)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvH2YDL045(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "SPM4 Take-Out요구[CCoilL3RcvSeEJB.rcvH2YDL045] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
    
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			jrRtn = this.procCCoilShearOutLineOffReq(rcvMsg);
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : SPM5 입측 Line-In요구(H2YDL071)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvH2YDL071(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "SPM5 입측 Line-In요구[CCoilL3RcvSeEJB.rcvH2YDL071] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
    
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			jrRtn = this.procCCoilShearInSupLotComp(rcvMsg);
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}	
	
	/**
	 *      [A] 오퍼레이션명 : SPM5 출측 Line-Off요구(H2YDL073)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvH2YDL073(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "SPM5 출측 Line-Off요구[CCoilL3RcvSeEJB.rcvH2YDL073] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
    
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			jrRtn = this.procCCoilShearOutLineOffReq(rcvMsg);
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : SPM5 Take-In요구(H2YDL074)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvH2YDL074(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "SPM5 Take-In요구[CCoilL3RcvSeEJB.rcvH2YDL074] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
    
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			jrRtn = this.procCCoilShearInSupLotComp(rcvMsg);
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : SPM5 Take-Out요구(H2YDL075)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvH2YDL075(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "SPM5 Take-Out요구[CCoilL3RcvSeEJB.rcvH2YDL075] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
    
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			jrRtn = this.procCCoilShearOutLineOffReq(rcvMsg);
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}	
	
	/**
	 *      [A] 오퍼레이션명 : HFL1 입측 Line-In요구(H2YDL011)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvH2YDL011(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "HFL1 입측 Line-In요구[CCoilL3RcvSeEJB.rcvH2YDL011] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
    
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			jrRtn = this.procCCoilShearInSupLotComp(rcvMsg);
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}	
	
	/**
	 *      [A] 오퍼레이션명 : HFL1 출측 Line-Off요구(H2YDL013)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvH2YDL013(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "HFL1 출측 Line-Off요구[CCoilL3RcvSeEJB.rcvH2YDL013] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
    
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			jrRtn = this.procCCoilShearOutLineOffReq(rcvMsg);
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : HFL1 Take-In요구(H2YDL014)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvH2YDL014(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "HFL1 Take-In요구[CCoilL3RcvSeEJB.rcvH2YDL014] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
    
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			jrRtn = this.procCCoilShearInSupLotComp(rcvMsg);
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : HFL1 Take-Out요구(H2YDL015)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvH2YDL015(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "HFL1 Take-Out요구[CCoilL3RcvSeEJB.rcvH2YDL015] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
    
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			jrRtn = this.procCCoilShearOutLineOffReq(rcvMsg);
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}	
	
	/**
	 *      [A] 오퍼레이션명 : HFL4 입측 Line-In요구(H2YDL051)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvH2YDL051(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "HFL4 입측 Line-In요구[CCoilL3RcvSeEJB.rcvH2YDL051] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
    
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			jrRtn = this.procCCoilShearInSupLotComp(rcvMsg);
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}	
	
	/**
	 *      [A] 오퍼레이션명 : HFL4 출측 Line-Off요구(H2YDL053)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvH2YDL053(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "HFL4 출측 Line-Off요구[CCoilL3RcvSeEJB.rcvH2YDL053] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
    
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			jrRtn = this.procCCoilShearOutLineOffReq(rcvMsg);
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : HFL4 Take-In요구(H2YDL054)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvH2YDL054(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "HFL4 Take-In요구[CCoilL3RcvSeEJB.rcvH2YDL054] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
    
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			jrRtn = this.procCCoilShearInSupLotComp(rcvMsg);
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : HFL4 Take-Out요구(H2YDL055)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvH2YDL055(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "HFL4 Take-Out요구[CCoilL3RcvSeEJB.rcvH2YDL055] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
    
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			jrRtn = this.procCCoilShearOutLineOffReq(rcvMsg);
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}

	/**
	 * 오퍼레이션명 : 열연조업L3 정정보급취소
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord updcoilYdLineWrCancelPpHR(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "열연조업L3 정정보급취소[CCoilL3RcvSeEJB.updcoilYdLineWrCancelPpHR] < " + rcvMsg.getResultMsg();
		String logId = commUtils.getLogId();

		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);

			JDTORecord jrRtn	= JDTORecordFactory.getInstance().create();
			String sModifier	= commUtils.trim(rcvMsg.getFieldString("YD_USER_ID"));
			String sStlNo		= commUtils.trim(rcvMsg.getFieldString("COIL_NO"));
			String sMsg			= "";

			// 전문송신용
			JDTORecord jrSnd	= commUtils.getParam(logId, mthdNm, sModifier);
			/*********************************************
			 * 1. 보급 스케쥴 조회
			 *********************************************/
			JDTORecord jrParam	= commUtils.getParam(logId, mthdNm, sModifier);
			/* com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getCrnSchByStlNo
			SELECT B.STL_NO
			     , A.YD_SCH_CD
			     , A.YD_WBOOK_ID
			     , C.YD_CRN_SCH_ID
			     , C.YD_WRK_PROG_STAT
			     , E.YD_EQP_ID
			     , E.YD_EQP_STAT          -- 설비상태
			     , E.YD_EQP_AUTO_CRN_MODE -- 1:On-Line, 2:Off-Line, 3:일시정지, 4:비상정지
			     , E.YD_EQP_WRK_MODE2     -- A:무인, R:리모컨, M:유인
			     , E.YD_EQP_WRK_MODE
			  FROM TB_YD_WRKBOOK    A
			     , TB_YD_WRKBOOKMTL B
			     , TB_YD_CRNSCH     C
			     , TB_YD_CRNWRKMTL  D
			     , TB_YD_EQP        E
			 WHERE A.YD_WBOOK_ID   = B.YD_WBOOK_ID
			   AND A.YD_WBOOK_ID   = C.YD_WBOOK_ID(+)
			   AND C.YD_CRN_SCH_ID = D.YD_CRN_SCH_ID(+)
			   AND C.YD_EQP_ID     = E.YD_EQP_ID(+)
			   AND A.YD_SCH_CD  LIKE 'J__E_1UH'
			   AND B.STL_NO        = :V_STL_NO
			   AND A.DEL_YN        = 'N'
			   AND B.DEL_YN        = 'N'
			   AND C.DEL_YN(+)     = 'N'
			   AND D.DEL_YN(+)     = 'N'
			*/
			jrParam.setField("STL_NO", sStlNo);
        	JDTORecordSet jsCrnWrkInfo = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getCrnSchByStlNo", logId, mthdNm, "크레인스케쥴 정보조회");
        	if( jsCrnWrkInfo.size() < 1 ) {

        		sMsg = "코일번호["+ sStlNo +"] 크레인작업정보 없음";
        		commUtils.printLog(logId, sMsg, "SL");
        		jrRtn.setField("RTN_CD", sMsg);

    			/*********************************************
    			 * 크레인작업 없어도 보급취소 실적 송신
    			 *********************************************/

    			JDTORecord jrSndHR = JDTORecordFactory.getInstance().create();
    			jrSndHR.setField("JMS_TC_CD"	, "YDHRJ001"); 	// 열연정정보급완료실적
    			jrSndHR.setField("TREAT_GP"		, "2");   		// 취소
    			jrSndHR.setField("STL_NO"		, sStlNo);		// 재료번호

    			jrSnd = commUtils.addSndData(jrSnd, jrSndHR);

    			//전문 송신 처리
    			commUtils.printLog(logId, "크레인작업 없음. 열연정정보급취소 전문 송신", "SL");
    			EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
    			sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrSnd });

        		commUtils.printLog(logId, mthdNm, "S-");
				return jrRtn;
        	}

        	JDTORecord jrCrnWrkInfo = jsCrnWrkInfo.getRecord(0);

        	String ydSchCd			= commUtils.trim(jrCrnWrkInfo.getFieldString("YD_SCH_CD"));
        	String ydWbookId		= commUtils.trim(jrCrnWrkInfo.getFieldString("YD_WBOOK_ID"));
        	String ydCrnSchId		= commUtils.trim(jrCrnWrkInfo.getFieldString("YD_CRN_SCH_ID"));
        	String ydWrkProgStat	= commUtils.trim(jrCrnWrkInfo.getFieldString("YD_WRK_PROG_STAT"));
        	String ydEqpId			= commUtils.trim(jrCrnWrkInfo.getFieldString("YD_EQP_ID"));
        	String ydEqpWrkMode2	= commUtils.trim(jrCrnWrkInfo.getFieldString("YD_EQP_WRK_MODE2"));	// A:무인, R:리모컨, M:유인

        	commUtils.printLog(logId, "YD_SCH_CD["+ ydSchCd +"] YD_WBOOK_ID["+ ydWbookId +"] YD_CRN_SCH_ID["+ ydCrnSchId +"] YD_EQP_ID["+ ydEqpId +"] YD_WRK_PROG_STAT["+ ydWrkProgStat +"] YD_EQP_WRK_MODE2["+ ydEqpWrkMode2 +"]", "SL");

			/*********************************************
			 * 2. 크레인스케쥴 취소
			 *********************************************/
        	if( !"".equals(ydCrnSchId) ) {
				/*********************************************
				 * 2.1 권상 전 작업상태 체크
				 *********************************************/
				if (!"W".equals(ydWrkProgStat)   //대기
				  &&!"S".equals(ydWrkProgStat)   //대기
				  &&!"1".equals(ydWrkProgStat)) {//선택(권상지시)

					sMsg = "스케쥴 취소를 할 수 없는 상태 입니다.";
					if( "2".equals(ydWrkProgStat) )	sMsg = "권하지시 상태는 취소 할 수 없습니다.";
					commUtils.printLog(logId, sMsg, "SL");

					jrRtn.setField("RTN_CD"  , sMsg);
					commUtils.printLog(logId, mthdNm, "S-");
					return jrRtn;
				}

				/*********************************************
				 * 2.2 무인크레인 선택상태 체크
				 *********************************************/
				if( !"W".equals(ydWrkProgStat) && ("A".equals(ydEqpWrkMode2) || "R".equals(ydEqpWrkMode2)) ) {

					sMsg = "자동화크레인 선택상태는 취소할 수 없습니다.";
					commUtils.printLog(logId, sMsg, "SL");

					jrRtn.setField("RTN_CD"  , sMsg);
					commUtils.printLog(logId, mthdNm, "S-");
					return jrRtn;
				}

				/*********************************************
				 * 2.3 크레인스케쥴 취소
				 *********************************************/
				JDTORecord jrCancelParam = commUtils.getParam(logId, mthdNm, sModifier);
				jrCancelParam.setField("YD_CRN_SCH_ID", ydCrnSchId);
				if( "1".equals(ydWrkProgStat) ) {
					jrCancelParam.setField("IS_LAST_SELECTED", "1");
				}

				EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
				JDTORecord jrCrnSchCncl = (JDTORecord)ejbConn.trx("trtCrnSchCncl", new Class[] { JDTORecord.class }, new Object[] { jrCancelParam });
				String rtnCd	= commUtils.nvl(jrCrnSchCncl.getFieldString("RTN_CD"), "0");
				String rtnMsg	= commUtils.nvl(jrCrnSchCncl.getFieldString("RTN_MSG"), "");

				if ("0".equals(rtnCd)) {

					sMsg = "["+ rtnCd +"]크레인스케쥴취소 에러:"+ rtnMsg;
					commUtils.printLog(logId, sMsg, "SL");

					jrRtn.setField("RTN_CD"		, sMsg);
					commUtils.printLog(logId, mthdNm, "S-");
					return jrRtn;
				}
				jrSnd = commUtils.addSndData(jrSnd, jrCrnSchCncl);
        	}

			/*********************************************
			 * 3. 작업예약 취소
			 *********************************************/
        	JDTORecord jrCancelParam = commUtils.getParam(logId, mthdNm, sModifier);
        	jrCancelParam.setField("YD_EQP_ID"		, ydEqpId);
        	jrCancelParam.setField("YD_WBOOK_ID"	, ydWbookId);

			EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
			JDTORecord jrWrkDelRtn = (JDTORecord)ejbConn.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrCancelParam });
			String rtnCd	= commUtils.nvl(jrWrkDelRtn.getFieldString("RTN_CD"), "0");
			String rtnMsg	= commUtils.nvl(jrWrkDelRtn.getFieldString("RTN_MSG"), "");
			if ("0".equals(rtnCd)) {

				sMsg = "["+ rtnCd +"]크레인작업예약취소 에러:"+ rtnMsg;
				commUtils.printLog(logId, sMsg, "SL");

				jrRtn.setField("RTN_CD"		, sMsg);
				commUtils.printLog(logId, mthdNm, "S-");
				return jrRtn;
			}

			/*********************************************
			 * 4. 조업 보급취소 전문
			 *********************************************/
			JDTORecord jrSndHR = JDTORecordFactory.getInstance().create();
			jrSndHR.setField("JMS_TC_CD"	, "YDHRJ001"); 	// 열연정정보급완료실적
			jrSndHR.setField("TREAT_GP"		, "2");   		// 취소
			jrSndHR.setField("STL_NO"		, sStlNo);		// 재료번호

			jrSnd = commUtils.addSndData(jrSnd, jrSndHR);

			//전문 송신 처리
			commUtils.printLog(logId, "정상처리 전문 송신", "SL");
			EJBConnector sndConn = new EJBConnector("default", "CCommSeEJB", this);
			sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrSnd });

			jrRtn.setField("RTN_CD", "열연조업L3 정정보급취소 실적 전송 송신 완료.");
			
			commUtils.printLog(logId, mthdNm, "S-");
			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	} 
 
	/**
	 *      [A] 오퍼레이션명 : SPM3 작업 라인이상 발생정보(H2YDL037)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvH2YDL037(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "SPM3 작업 라인이상 발생정보[CCoilL3RcvSeEJB.rcvH2YDL037] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
    
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			jrRtn = this.procWrkLineAbOccrInfo(rcvMsg);
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : SPM4 작업 라인이상 발생정보(H2YDL047)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvH2YDL047(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "SPM4 작업 라인이상 발생정보[CCoilL3RcvSeEJB.rcvH2YDL047] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
    
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			jrRtn = this.procWrkLineAbOccrInfo(rcvMsg);
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 작업 라인이상 발생정보
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord procWrkLineAbOccrInfo(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "작업 라인이상 발생정보[CCoilL3RcvSeEJB.procWrkLineAbOccrInfo] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
    
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			//수신 항목 값
			String msgId      = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId    = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"    )); //설비명 SPM3(JCKE03), SPM4(JBKE04)
			String sWrkAbleYn = commUtils.trim(rcvMsg.getFieldString("WRK_ABLE_YN"  )); //작업가능여부 Y, N          
			
			String sModifier  = commUtils.trim(rcvMsg.getFieldString("MODIFIER" )); //수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }

			/**********************************************************
			* 0. 수신 항목 값 Check
			**********************************************************/
            
            if ("".equals(ydEqpId)) {
				jrRtn.setField("RTN_CD"         , "0");	
				jrRtn.setField("RTN_MSG"        , "설비 항목값이 없습니다");
		      	return jrRtn;
			}
            
            if ("".equals(sWrkAbleYn) || !("Y".equals(sWrkAbleYn) || "N".equals(sWrkAbleYn))) {
				jrRtn.setField("RTN_CD"         , "0");	
				jrRtn.setField("RTN_MSG"        , "작업가능여부 항목값이 없거나 잘못되었습니다");
		      	return jrRtn;
			}
            
            /**********************************************************
			* 1. 작업가능여부 수정
			**********************************************************/
            
            JDTORecord jrParam	= commUtils.getParam(logId, mthdNm, sModifier);
            
            jrParam.setField("WRK_ABLE_YN", sWrkAbleYn);
            jrParam.setField("YD_EQP_ID"  , ydEqpId);
            
            /*
			UPDATE TB_YD_EQP
			   SET MODIFIER      = :V_MODIFIER
			     , MOD_DDTT      = SYSDATE
			     , WRK_ABLE_YN   = :V_WRK_ABLE_YN
			 WHERE YD_EQP_ID = :V_YD_EQP_ID
             */
            commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdEqpWrkAbleYn", logId, mthdNm, "SPM 590Y 작업가능 수정");
            
			 
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : SPM1 일시보급중단요구(H2YDL008)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvH2YDL008(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "SPM1 일시보급중단요구[CCoilL3RcvSeEJB.rcvH2YDL008] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
    
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			jrRtn = this.procWrkLineAbOccrInfoSpmOF(rcvMsg);
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : SPM2 일시보급중단요구(H2YDL028)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvH2YDL028(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "SPM2 일시보급중단요구[CCoilL3RcvSeEJB.rcvH2YDL028] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
    
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			jrRtn = this.procWrkLineAbOccrInfoSpmOF(rcvMsg);
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : SPM3 일시보급중단요구(H2YDL038)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvH2YDL038(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "SPM3 일시보급중단요구[CCoilL3RcvSeEJB.rcvH2YDL038] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
    
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			jrRtn = this.procWrkLineAbOccrInfoSpmOF(rcvMsg);
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : SPM4 일시보급중단요구(H2YDL048)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvH2YDL048(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "SPM4 일시보급중단요구[CCoilL3RcvSeEJB.rcvH2YDL048] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
    
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			jrRtn = this.procWrkLineAbOccrInfoSpmOF(rcvMsg);
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : SPM5 일시보급중단요구(H2YDL078)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvH2YDL078(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "SPM5 일시보급중단요구[CCoilL3RcvSeEJB.rcvH2YDL078] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
    
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			jrRtn = this.procWrkLineAbOccrInfoSpmOF(rcvMsg);
			
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : SPM 일시정지 발생정보
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord procWrkLineAbOccrInfoSpmOF(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "SPM 일시정지 발생정보[CCoilL3RcvSeEJB.procWrkLineAbOccrInfoSpmOF] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
    
		try {
			commUtils.printLog(logId, mthdNm, "S+");
			commUtils.printParam(logId, rcvMsg);
			
			//수신 항목 값
			String msgId      = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId    = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"    )); //설비명 SPM1(JHKE01 JHKE01UH), SPM2(JEKE02 JEKE01UH),SPM3(JCKE03), SPM4(JBKE04 JBKE01UH),SPM5(JAKE05)
			String sWrkAbleYn = commUtils.trim(rcvMsg.getFieldString("WRK_ABLE_YN"  )); //작업가능여부 Y, N          
			int intRtnVal     = 0;
			String sModifier  = commUtils.trim(rcvMsg.getFieldString("MODIFIER" )); //수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId; }

			/**********************************************************
			* 0. 수신 항목 값 Check
			**********************************************************/
            
            if ("".equals(ydEqpId) || ydEqpId.length() < 6) {
            	commUtils.printLog(logId, "설비["+ydEqpId+"] 설비 항목값이 없습니다", "SL");				
				jrRtn.setField("RTN_CD"         , "0");	
				jrRtn.setField("RTN_MSG"        , "설비 항목값이 없습니다");
		      	return jrRtn;
			}
           
            if ("".equals(sWrkAbleYn) || !("Y".equals(sWrkAbleYn) || "N".equals(sWrkAbleYn))) {
            	commUtils.printLog(logId, "작업가능여부["+sWrkAbleYn+"] 작업가능여부 항목값이 없거나 잘못되었습니다", "SL");				
				jrRtn.setField("RTN_CD"         , "0");	
				jrRtn.setField("RTN_MSG"        , "작업가능여부 항목값이 없거나 잘못되었습니다");
		      	return jrRtn;
			}
            
            /**********************************************************
			* 1. 작업가능여부 수정
			**********************************************************/
            
            JDTORecord jrParam	= commUtils.getParam(logId, mthdNm, sModifier);
            String ydSchCd    = ydEqpId.substring(0, 4)+"01UH";
            if("Y".equals(sWrkAbleYn)){//보급가능
            	sWrkAbleYn = "N";// 스케줄 기동
            }else{ //보급중단
            	sWrkAbleYn = "Y";//스케줄기동 금지
            }
            jrParam.setField("YD_SCH_PROH_EXN", sWrkAbleYn);
            jrParam.setField("YD_EQP_ID"  , ydEqpId);
            jrParam.setField("YD_SCH_CD"  , ydSchCd);
            
            String sMsg = "스케쥴 금지유무 업데이트  YD_EQP_ID["+ ydEqpId+"] YD_SCH_CD["+ ydSchCd +"] YD_SCH_PROH_EXN["+ sWrkAbleYn +"]";
            commUtils.printLog(logId, sMsg, "SL");
            
            /***********************************
			 * 보급 스케쥴 삭제
			 ***********************************/			
            
			/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getScrapMvWrkInfoSPM
			SELECT A.YD_WBOOK_ID
			     , A.YD_SCH_CD
			     , B.YD_CRN_SCH_ID
			     , NVL(B.YD_WRK_PROG_STAT, 'W') AS YD_WRK_PROG_STAT
			  FROM TB_YD_WRKBOOK A
			     , TB_YD_CRNSCH  B
			 WHERE A.YD_WBOOK_ID = B.YD_WBOOK_ID(+)
			   AND A.DEL_YN      = 'N'
			   AND B.DEL_YN(+)   = 'N'
			   AND A.YD_SCH_CD   = :V_YD_SCH_CD
			   AND B.YD_CRN_SCH_ID IS NOT NULL
			   AND B.YD_WRK_PROG_STAT IN ('W','S','1')
			 ORDER BY B.YD_WRK_PROG_STAT DESC, A.YD_WBOOK_ID DESC
			*/
			JDTORecordSet jsScrapWrk = commDao.select(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getScrapMvWrkInfoSPM", logId, mthdNm, "보급 작업예약 조회");
			String sAPP018_YN = coilDao.ApplyYn(logId, mthdNm, "APP001","J","018"); 
			commUtils.printLog(logId, "보급일시정지시 스케줄 취소 : [" + sAPP018_YN+"]", "SL");
			
			for(int i=0; i<jsScrapWrk.size(); i++) {

				String ydWbookId		= commUtils.trim(jsScrapWrk.getRecord(i).getFieldString("YD_WBOOK_ID"));
				String ydCrnSchId		= commUtils.trim(jsScrapWrk.getRecord(i).getFieldString("YD_CRN_SCH_ID"));
				String ydWrkProgStat	= commUtils.trim(jsScrapWrk.getRecord(i).getFieldString("YD_WRK_PROG_STAT"));
				
				
				// 지시대기 작업 삭제
				if("Y".equals(sAPP018_YN) && "Y".equals(sWrkAbleYn)) {

					// 크레인스케쥴 삭제
					if( !"".equals(ydCrnSchId) ) {

						// Parameter Grid Setting
						GridData gdParam = new GridData();
						gdParam.addParam("YD_USER_ID", sModifier);
						gdParam.createHeader("CHECK", OperateGridData.t_checkbox);
						gdParam.createHeader("YD_CRN_SCH_ID", "" );

						gdParam.getHeader("CHECK").addValue("1", "");
						gdParam.getHeader("YD_CRN_SCH_ID").addValue(ydCrnSchId, "");
						gdParam.setNavigateValue(mthdNm); // 상위 Method 명
						gdParam.setIPAddress(logId);      // Logging 을 위한 ID
						
						EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
						JDTORecord jrCrnSchCncl = (JDTORecord)ejbConn.trx("cancelSchCoilYdCrnWorkMgtHNew", new Class[] { GridData.class }, new Object[] { gdParam });
		    			
						String rtnCd	= commUtils.nvl(jrCrnSchCncl.getFieldString("RTN_CD"), "0");
						String rtnMsg	= commUtils.nvl(jrCrnSchCncl.getFieldString("RTN_MSG"), "");
						commUtils.printLog(logId, (i+1)+". YD_WBOOK_ID["+ ydWbookId +"] YD_CRN_SCH_ID["+ ydCrnSchId +"] YD_WRK_PROG_STAT["+ ydWrkProgStat +"] 스케쥴취소 RTN_CD["+ rtnCd +"] RTN_MSG["+ rtnMsg +"]", "SL");
						
						if( !"1".equals(rtnCd)) {
							sMsg = "보급스케줄 취소 실패 -YD_CRN_SCH_ID["+ ydCrnSchId +"] >> "+rtnMsg;
							commUtils.printLog(logId, sMsg, "SL");
							// Error Message 만 찍고 진행
						} else {
							jrRtn = commUtils.addSndData(jrRtn, jrCrnSchCncl);
						}
					}

				}
			}
			
            /***************************************
             * 1. 스케쥴 기동/금지 UPDATE
             ***************************************/
            /* com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updSchProhExn
            UPDATE TB_YD_SCHRULE
               SET YD_SCH_PROH_EXN = :V_YD_SCH_PROH_EXN
                 , MODIFIER        = :V_MODIFIER
                 , MOD_DDTT        = SYSDATE
             WHERE YD_SCH_CD       = :V_YD_SCH_CD
               AND DEL_YN          = 'N'
            */
            intRtnVal = commDao.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updSchProhExn", logId, mthdNm, "스케쥴 기동/금지 UPDATE");
            if( intRtnVal < 1 ) {

                sMsg = "스케쥴 금지유무 업데이트 실패! YD_SCH_CD["+ ydSchCd +"] YD_SCH_PROH_EXN["+ sWrkAbleYn +"]";
                commUtils.printLog(logId, sMsg, "SL");

                jrRtn.setField("RTN_CD" , "0");
                jrRtn.setField("RTN_MSG", sMsg);
                commUtils.printLog(logId, mthdNm, "S-");
                return jrRtn;
            } 
			 
			commUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
}
