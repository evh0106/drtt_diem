/**
 * @(#)YdCoilL3RcvPISeEJBBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2022/02/28
 *
 * @description      2열연 COIL 야드 물류진행 수신 처리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자     요청자  수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2022/02/28   정종균  송정현      최초 등록
 * 
 */
package com.inisteel.cim.ydPI.session;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.ccoil.dao.CCoilDAO;
import com.inisteel.cim.yd.ccommon.util.CConstant;
import com.inisteel.cim.yd.common.dao.ymEtcDao.YmEtcDao;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import xlib.cmc.GridData;

import com.inisteel.cim.ydPI.common.M10YdExLm99SenderFaEJBBean;

import com.inisteel.cim.ydPI.dao.YdPiDAO;
import com.inisteel.cim.ydPI.common.util.PIYdUtils;

import com.inisteel.cim.ydPI.dao.YdPICommDAO;
 
/**
 *      [A] 클래스명 : PI관련 2열연COIL야드 출하수신 처리
 *
 * @ejb.bean name="YdCoilL3RcvPISeEJB" jndi-name="YdCoilL3RcvPISeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
*/

public class YdCoilL3RcvPISeEJBBean extends BaseSessionBean {
	
	private static final long serialVersionUID = 1L;
	
	private CCoilDAO      coilDao     = new CCoilDAO();
	private PIYdUtils     commPiUtils = new PIYdUtils();
	private YdPICommDAO   ydPICommDAO   = new YdPICommDAO();
	
	private M10YdExLm99SenderFaEJBBean      M10YdExLm99Sender   = new M10YdExLm99SenderFaEJBBean();
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}	
	
	/**
	 * 차량정지위치활성/비활성처리 PI_
	 * @param recPara
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procCarPosActiveOrInActive_PIDEV(JDTORecord rcvMsg) throws DAOException { //S
		String mthdNm = "차량정지위치활성/비활성처리[YdCoilL3RcvPISeEJB.procCarPosActiveOrInActive_PIDEV] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commPiUtils.printLog(logId, mthdNm, "S+");

			//수신 항목 값
			String ydStkColGp      = commPiUtils.nvl(rcvMsg.getFieldString("YD_STK_COL_GP"      ), "");
			String ydCarUseGp      = commPiUtils.nvl(rcvMsg.getFieldString("YD_CAR_USE_GP"      ), "");
			String sTrnEqpCd       = commPiUtils.nvl(rcvMsg.getFieldString("TRN_EQP_CD"         ), "");
			String sCarNo          = commPiUtils.nvl(rcvMsg.getFieldString("CAR_NO"             ), "");
			String sCardNo         = commPiUtils.nvl(rcvMsg.getFieldString("CARD_NO"            ), "");
			String sTrnEqpStkCapa  = commPiUtils.nvl(rcvMsg.getFieldString("TRN_EQP_STK_CAPA"   ), "");
			String ydStkColActStat = commPiUtils.nvl(rcvMsg.getFieldString("YD_STK_COL_ACT_STAT"), "");
			
			String sModifier       = commPiUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)

			/**********************************************************
			* 0. 항목 값 Check
			**********************************************************/
			if ("".equals(ydStkColGp)) {
				commPiUtils.printLog(logId, "적치열이 존재하지 않습니다.....", "S-");
				return jrRtn;
			}
			
			if ("L".equals(ydCarUseGp)) { //구내운송
				if ("L".equals(ydStkColActStat) && "".equals(sTrnEqpCd)) {
					commPiUtils.printLog(logId, "구내운송은 운송장비코드가 존재해야합니다.", "S-");
					return jrRtn;
				}
			}
			
			if ("G".equals(ydCarUseGp)) { //출하
				if ("L".equals(ydStkColActStat)) {
					// if ("".equals(sCarNo) || "".equals(sCardNo)) {
					if ( "".equals(sCarNo) ) {
						commPiUtils.printLog(logId, "출하차량은 차량번호가 존재해야합니다.", "S-");
						return jrRtn;
					}
				}
			}
			
			String ydStkBedActStat = "";
			String ydStkLyrActStat = "";
			
			if ("L".equals(ydStkColActStat)) { //적치가능
				ydStkBedActStat = "L"; 
				ydStkLyrActStat = "E"; 
			} else if ("C".equals(ydStkColActStat)) { //비활성화
				ydStkBedActStat = "C"; //비활성화
				ydStkLyrActStat = "C"; //비활성화
				sTrnEqpStkCapa  = CConstant.YD_STK_BED_WT_MAX_DEFAULT; //베드의 기본 야드적치Bed중량Max 300000
				ydCarUseGp      = "";
			} else {
				commPiUtils.printLog(logId, "["+ydStkColActStat + "]사용할 수 없는 상태값입니다.", "S-");
				return jrRtn;
			}
			
			/**********************************************************
			* 1.적치열 활성/비활성 처리
			**********************************************************/			
			JDTORecord jrParam = commPiUtils.getParam(logId, mthdNm, sModifier);
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
	    	ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStkcolActYn", logId, mthdNm, "적치열 수정");
			
	    	/*
			UPDATE TB_YD_CARPOINT
			   SET MODIFIER            = :V_MODIFIER
			     , MOD_DDTT            = SYSDATE
			     , CAR_NO              = :V_CAR_NO
			     , CARD_NO             = :V_TRN_EQP_CD
			     , YD_STK_COL_ACT_STAT = :V_YD_STK_COL_ACT_STAT     
			 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
	    	 */
	    	ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdCarpointActYn", logId, mthdNm, "차량포인트 수정");
	    	
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
			ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStkbedYdStkColGp", logId, mthdNm, "적치베드 수정");
			
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
			ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStklyrYdStkColGp", logId, mthdNm, "적치단 수정");
	    	
			commPiUtils.printLog(logId, mthdNm, "S-");
			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, mthdNm, e));
		}
	}	
	
	/**
	 * 출하차량스케줄/차량Point삭제 기능 - 상차지시 취소 시 호출
	 * @param recPara
	 * @param szCaller
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord delCarSchNCarPointForDist_PIDEV(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "출하차량스케줄/차량Point삭제[YdCoilL3RcvPISeEJB.delCarSchNCarPointForDist_PIDEV] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commPiUtils.printLog(logId, mthdNm, "S+");

			//수신 항목 값
			String msgId          = commPiUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sTransOrdDate  = commPiUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"   ));
	    	String sTransOrdSeqno = commPiUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));
			
			String sModifier 	     = commPiUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }
			mthdNm = msgId.substring(0, 2) + mthdNm;
			
			/**********************************************************
			* 1. 차량스케줄 조회
			**********************************************************/
			JDTORecord jrParam = commPiUtils.getParam(logId, mthdNm, sModifier);
			
			jrParam.setField("TRANS_ORD_DATE"   , sTransOrdDate);
			jrParam.setField("TRANS_ORD_SEQNO"  , sTransOrdSeqno);

			JDTORecordSet jsRst = ydPICommDAO.selectJ(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdCarschByTransDTSeq", logId, mthdNm, "차량스케줄 조회");
			if (jsRst.size() <= 0) {
				commPiUtils.printLog(logId, "운송지시일자 :["+sTransOrdDate+"] , 운송지시순번["+sTransOrdSeqno+"]로 차량스케줄 조회 시 오류발생", "S-");
				return jrRtn;
			}
			
			String ydCarSchId     = commPiUtils.trim(jsRst.getRecord(0).getFieldString("YD_CAR_SCH_ID"    ));
			// String ydCarProgStat  = commPiUtils.trim(jsRst.getRecord(0).getFieldString("YD_CAR_PROG_STAT" ));
			String ydCarldStopLoc = commPiUtils.trim(jsRst.getRecord(0).getFieldString("YD_CARLD_STOP_LOC"));
			String sCarNo         = commPiUtils.trim(jsRst.getRecord(0).getFieldString("CAR_NO"           ));
			String sCardNo        = commPiUtils.trim(jsRst.getRecord(0).getFieldString("CARD_NO"          ));
			
			/**********************************************************
			* 2. 조회된 차량스케줄로 차량이송재료/차량스케줄 삭제
			**********************************************************/
			jrParam = commPiUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("YD_CAR_SCH_ID"     , ydCarSchId);
			jrParam.setField("DEL_YN"            , "Y"       );
			
			ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.delYdCarftmvmtl", logId, mthdNm, "이송재료 삭제");
			
			ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.delYdCarsch", logId, mthdNm, "차량스케줄 삭제");
			
			/**********************************************************
			* 3. 차량진행상태에 따른 차량정지위치 Clear 실행 - 상차도착 시에만 Clear
			**********************************************************/
			if (!"".equals(ydCarldStopLoc)) {
				jrParam = commPiUtils.getParam(logId, mthdNm, sModifier);
				
				jrParam.setField("YD_STK_COL_GP", ydCarldStopLoc);

				jsRst = ydPICommDAO.selectJ(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.getYdStkColByPk", logId, mthdNm, "차량정지위치 조회");
				if (jsRst.size() <= 0) {
					commPiUtils.printLog(logId, "차량정지위치["+ydCarldStopLoc+"]조회 오류발생", "S-");
					return jrRtn;
				}
				
				String ydStkColCarNo  = commPiUtils.trim(jsRst.getRecord(0).getFieldString("CAR_NO" )); 
				String ydStkColCardNo = commPiUtils.trim(jsRst.getRecord(0).getFieldString("CARD_NO")); 
				
				// if (ydStkColCarNo.equals(sCarNo) && ydStkColCardNo.equals(sCardNo)) {
				if ( ydStkColCarNo.equals(sCarNo) ) {
					
					commPiUtils.printLog(logId, "차량정지위치 비활성화", "SL");
					jrParam.setField("YD_STK_COL_GP"      , ydCarldStopLoc);
					jrParam.setField("YD_CAR_USE_GP"      , "G"           ); //출하차량
					jrParam.setField("YD_STK_COL_ACT_STAT", "C"           ); //비활성화

					this.procCarPosActiveOrInActive_PIDEV(jrParam);
					
					jrParam.setField("STAT"  			, "C"); 
					jrParam.setField("YD_STK_COL_GP"	, ydCarldStopLoc);
			    	
					ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilCarMvSeEJB.carpointstackcolgpupdateC", logId, mthdNm, "TB_YD_CARPOINT 수정");
					
				} else {
					commPiUtils.printLog(logId, "차량스케줄의 차량번호["+sCarNo+"]와 적치열의 차량번호["+ydStkColCarNo+"]와", "SL");
					// commPiUtils.printLog(logId, "카드번호["+ydStkColCardNo+"]가 동일하지 않으므로 차량정지위치["+ydCarldStopLoc+"]를 비활성화하지 않음", "SL");
				}
				
			}
		
			commPiUtils.printLog(logId, mthdNm, "S-");
			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, mthdNm, e));
		}
	}	
	
	/**
	 * 2열연코일야드 출하전문 취소(PI) - receiveCancel - coilReceiveCancel_
	 * @param rcvMsg
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procDmTcCnclPI(JDTORecord rcvMsg) throws DAOException { //S
		String mthdNm = "2열연코일야드 출하전문 취소[YdCoilL3RcvPISeEJB.procDmTcCnclPI] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
	    	/*
			M10LMYDJ1021(DMYDR008)	코일제품반납확정			STL_NO
			M10LMYDJ1011_1(DMYDR014)	코일제품목전				STL_NO
			M10LMYDJ1011_2(DMYDR027)	코일제품보관지시			STL_NO
			
			M10LMYDJ1031(DMYDR060)	코일제품운송상차지시
			M10LMYDJ1035(DMYDR060)	임가공운송상차지시

			M10LMYDJ1021(DMYDR008)	코일제품반납대기		1.저장품 이동 조건 변경
			M10LMYDJ1011(DMYDR014)	코일제품목전			1.저장품 이동 조건 변경
			M10LMYDJ1071(DMYDR030)	코일제품출하완료              1.저장품 이동 조건 변경

			M10LMYDJ1041(DMYDR070)	코일이송상차대기장도착PDA
			M10LMYDJ1091(DMYDR073)	이송하차도착전(PDA)
			*/			
			commPiUtils.printLog(logId, mthdNm, "S+");
			commPiUtils.printParam(logId, rcvMsg);
			
			rcvMsg.setField("TRANS_ORD_DT"   , commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE")) );      // 운송의뢰일자
			rcvMsg.setField("TRANS_ORD_SEQNO", commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ")) );       // 운송의뢰순번

			/**********************************************************
			* 0. 
			**********************************************************/
			String msgId      = commPiUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			
			String sStlGp     = "C";
			
			String sInfoGp    = commPiUtils.trim(rcvMsg.getFieldString("INFO_GP"));//정보구분
			String sModifier  = commPiUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			
			if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }
			mthdNm = msgId.substring(0, 2) + mthdNm;
			
			JDTORecord jrParam = commPiUtils.getParam(logId, mthdNm, sModifier);

			/**********************************************************
			* 1. 크레인스케줄취소, 작업예약취소
			**********************************************************/
//			if (CConstant.DMYDR070.equals(msgId)
//			||  CConstant.DMYDR073.equals(msgId)
//			||  CConstant.DMYDR060.equals(msgId)) {
				
			
			if ("M10LMYDJ1041".equals(msgId)
			||  "M10LMYDJ1091".equals(msgId)
			||  "M10LMYDJ1031".equals(msgId)
			||  "M10LMYDJ1035".equals(msgId)) {
				
				//차량스케줄 삭제 및 차량 POINT Clear
				commPiUtils.printLog(logId, "차량스케줄삭제 및 차량Point Clear", "SL");
				this.delCarSchNCarPointForDist_PIDEV(rcvMsg);
				
				//1.크레인 스케줄취소 ,2 작업예약취소
				String sTransOrdDate  = commPiUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"   ));
		    	String sTransOrdSeqno = commPiUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));
				
		    	jrParam.setField("TRANS_ORD_DATE" , sTransOrdDate );
 				jrParam.setField("TRANS_ORD_SEQNO", sTransOrdSeqno);
 				
 				JDTORecordSet jsRst = ydPICommDAO.selectJ(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdCrnschTransNo", logId, mthdNm, "크레인스케줄 조회");
 				
 				if (jsRst.size() > 0) {
 					
 					commPiUtils.printLog(logId, "스케줄 취소대상이 존재함", "SL");
 					
 					for (int i = 1; i <= jsRst.size(); ++i) {
 						jsRst.absolute(i);
 						jrParam.setField("YD_CRN_SCH_ID", jsRst.getRecord().getFieldString("YD_CRN_SCH_ID"));
 						jrParam.setField("YD_SCH_CD"    , jsRst.getRecord().getFieldString("YD_SCH_CD"    ));
 						
 						//ejbConn.trx("YdSimSeEJB", "wrkCncl", recPara);
 						//크레인 스케줄 삭제
 						EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
 						JDTORecord jrRst = (JDTORecord)ejbConn.trx("trtCrnSchCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
 						
 						jrRtn = commPiUtils.addSndData(jrRtn, jrRst);
 					}
 				} else if (jsRst.size() == 0) {
 					
 					commPiUtils.printLog(logId, "스케줄 취소대상이 존재안함", "SL");
 					
 					JDTORecordSet jsWrkBk = ydPICommDAO.selectJ(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdWrkbookTransSeq", logId, mthdNm, "작업예약조회");
 					
 					if (jsWrkBk.size() > 0) {
 	 					for (int i = 1; i <= jsRst.size(); ++i) {
 	 						jsWrkBk.absolute(i);
 	 						jrParam.setField("YD_WBOOK_ID", jsWrkBk.getRecord().getFieldString("YD_WBOOK_ID"));
 	 						
 	 						//크레인 작업예약 삭제
 	 						EJBConnector ejbConn = new EJBConnector("default", "CCoilJspSeEJB", this);
 	 						JDTORecord jrRst = (JDTORecord)ejbConn.trx("delWrkbook", new Class[] { JDTORecord.class }, new Object[] { jrParam });
 	 						
 	 						jrRtn = commPiUtils.addSndData(jrRtn, jrRst);
 	 					}
 					}
 				}
			}
			
			/**********************************************************
			* 2. 출하전문 처리(재료번호 1개 : STL_NO)
			**********************************************************/
//			if (CConstant.DMYDR008.equals(msgId) 
//			    	||	CConstant.DMYDR014.equals(msgId) 
//			    	||	CConstant.DMYDR027.equals(msgId) 
//			    	||	CConstant.DMYDR030.equals(msgId)) {			
			if ("M10LMYDJ1021".equals(msgId) 
		    	||	(
		    			"M10LMYDJ1011".equals(msgId) && 
		    			(
		    					"1".equals(sInfoGp) ||
		    					"2".equals(sInfoGp)
		    			)
		    		)
		    	||	"M10LMYDJ1071".equals(msgId)) {
				
				jrParam = commPiUtils.getParam(logId, mthdNm, sModifier);
				
				String sStlAppearGp = commPiUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP")); //재료외형구분
 	 			String sStlNo       = commPiUtils.trim(rcvMsg.getFieldString("STL_NO"       )); //재료번호
 	            String sCurrProgCd  = commPiUtils.trim(rcvMsg.getFieldString("CURR_PROG_CD" )); //현재진도코드
 	            
 	            jrParam.setField("STL_APPEAR_GP", sStlAppearGp);
		    	jrParam.setField("STL_NO"       , sStlNo);
				jrParam.setField("STL_PROG_CD"  , sCurrProgCd);
				
//				if (CConstant.DMYDR008.equals(msgId)) {				
				if ("M10LMYDJ1021".equals(msgId)) {
					String sWoCarPlntProcCd      = commPiUtils.trim(rcvMsg.getFieldString("WO_CAR_PLNT_PROC_CD"    )); //지시차공장공정코드
 	    			String sFrtomoveOrdDate      = commPiUtils.trim(rcvMsg.getFieldString("FRTOMOVE_ORD_DATE"      )); //이송지시일자
 	    			String sUrgentFrtoMoveWirdGp = commPiUtils.trim(rcvMsg.getFieldString("URGENT_FRTOMOVE_WORD_GP")); //긴급이송작업지시구분
 	   			
 		    		// 레코드에 추가
 			    	jrParam.setField("WO_CAR_PLNT_PROC_CD"    , sWoCarPlntProcCd     );
 			    	jrParam.setField("FRTOMOVE_ORD_DATE"      , sFrtomoveOrdDate     );
 			    	jrParam.setField("URGENT_FRTOMOVE_WORD_GP", sUrgentFrtoMoveWirdGp);
				}
//				if (CConstant.DMYDR014.equals(msgId) 
//				||	CConstant.DMYDR027.equals(msgId)) {				
				if ("M10LMYDJ1011".equals(msgId) && 
		    			(
		    					"1".equals(sInfoGp) ||
		    					"2".equals(sInfoGp)
		    			)
		    		) {
					String sOrdYeojaeGp      = commPiUtils.trim(rcvMsg.getFieldString("ORD_YEOJAE_GP"     ));   // 주문여재구분
 	    			String sOrdNo            = commPiUtils.trim(rcvMsg.getFieldString("ORD_NO"            ));   // 주문번호
 	    			String sOrdDtl           = commPiUtils.trim(rcvMsg.getFieldString("ORD_DTL"           ));   // 주문행번
 	    			String sOrdGp            = commPiUtils.trim(rcvMsg.getFieldString("ORD_GP"            ));   // 수주구분
 	    			String sCustCd           = commPiUtils.trim(rcvMsg.getFieldString("CUST_CD"           ));   // 고객코드
 	    			String sDestCd           = commPiUtils.trim(rcvMsg.getFieldString("DEST_CD"           ));   // 목적지코드
 	    			String sDlvrddRuleDd     = commPiUtils.trim(rcvMsg.getFieldString("DLVRDD_RULE_DD"    ));   // 납기기준일
 	    			String sDestTelNo        = commPiUtils.trim(rcvMsg.getFieldString("DEST_TEL_NO"       ));   // 목적지전화번호
 	    			String sDistShipassignGp = commPiUtils.trim(rcvMsg.getFieldString("DIST_SHIPASSIGN_GP"));   // 출하배선지시구분
 	               
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
	
//				if (CConstant.DMYDR027.equals(msgId)) {				
				if ("M10LMYDJ1011".equals(msgId) && "2".equals(sInfoGp)) {
					jrParam = commPiUtils.getParam(logId, mthdNm, sModifier);
					
					jrParam.setField("STL_NO"     , sStlNo);
					jrParam.setField("DEL_YN"     , "N");
					jrParam.setField("SCARFING_YN", "N");
					
					ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStockDmYd", logId, mthdNm, "저장품 수정");
				} else {
					ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStockDmYd", logId, mthdNm, "저장품 수정");
				}
			}
			/**********************************************************
			* 3. 출하전문 처리(재료번호 N개 : STL_NO1 ... STL_NO20)
			**********************************************************/
//			if (CConstant.DMYDR011.equals(msgId)
//		    		||  CConstant.DMYDR070.equals(msgId)
//		    		||  CConstant.DMYDR073.equals(msgId) 
//		    		||  CConstant.DMYDR060.equals(msgId)) {
			if (CConstant.DMYDR011.equals(msgId)
		    		||  "M10LMYDJ1041".equals(msgId)
					||  "M10LMYDJ1091".equals(msgId)
					||  "M10LMYDJ1031".equals(msgId)
					||  "M10LMYDJ1035".equals(msgId) ) {
									
				String sStlAppearGp   = commPiUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"  )); //재료외형구분
				String sTransOrdDate  = commPiUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"   ));
		    	String sTransOrdSeqno = commPiUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));
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
					jrParam = commPiUtils.getParam(logId, mthdNm, sModifier);
					
					jrParam.setField("STL_APPEAR_GP", sStlAppearGp);
					jrParam.setField("TRANS_ORD_DATE" , sTransOrdDate );
	 				jrParam.setField("TRANS_ORD_SEQNO", sTransOrdSeqno);
					
					sStlNo = commPiUtils.trim(rcvMsg.getFieldString("STL_NO"+(i+1)));
					
					if ("".equals(sStlNo)) {
						break;
					}
					
					jrParam.setField("STL_NO", sStlNo);
					
//					if (CConstant.DMYDR070.equals(msgId)
//					||  CConstant.DMYDR073.equals(msgId)
//					||  CConstant.DMYDR060.equals(msgId)) {					
					if ("M10LMYDJ1041".equals(msgId)
						||  "M10LMYDJ1091".equals(msgId)
						||  "M10LMYDJ1031".equals(msgId)
						||  "M10LMYDJ1035".equals(msgId)) {
						
						sDistShipassignGp    = commPiUtils.trim(rcvMsg.getFieldString("DIST_SHIPASSIGN_GP"   ));   // 출하배선지시구분
						sShipassignWirdDate  = commPiUtils.trim(rcvMsg.getFieldString("SHIPASSIGN_WORD_DATE" ));
						sShipassignWordSeqno = commPiUtils.trim(rcvMsg.getFieldString("SHIPASSIGN_WORD_SEQNO"));
						sShipCd              = commPiUtils.trim(rcvMsg.getFieldString("SHIP_CD"              ));
						sShipName            = commPiUtils.trim(rcvMsg.getFieldString("SHIP_NAME"            ));
						sRshpHoldNo          = commPiUtils.trim(rcvMsg.getFieldString("RSHP_HOLD_NO"         ));
						sSailno              = commPiUtils.trim(rcvMsg.getFieldString("SAILNO"               ));
						sCurrProgCd          = commPiUtils.trim(rcvMsg.getFieldString("CURR_PROG_CD"         )); //현재진도코드

						jrParam.setField("DIST_SHIPASSIGN_GP"   , sDistShipassignGp   );
						jrParam.setField("SHIPASSIGN_WORD_DATE" , sShipassignWirdDate );
						jrParam.setField("SHIPASSIGN_WORD_SEQNO", sShipassignWordSeqno);
						jrParam.setField("SHIP_CD"              , sShipCd             );
						jrParam.setField("SHIP_NAME"            , sShipName           );
						jrParam.setField("RSHP_HOLD_NO"         , sRshpHoldNo         );
						jrParam.setField("SAILNO"               , sSailno             );
						jrParam.setField("STL_PROG_CD"          , sCurrProgCd         );

						//야드목표행성지구분
						String[] rVal = new String[1];
		 				rVal = coilDao.getYdAimRtGp2(msgId , sStlGp, sStlNo, sCurrProgCd);		
						jrParam.setField("YD_AIM_RT_GP", rVal[0]);					
					
	    	    		jrParam.setField("CAR_NO"            , "");
	    	    		jrParam.setField("CARD_NO"           , "");
	    	    		jrParam.setField("YD_STK_BED_NO"     , "");
	    	    		jrParam.setField("YD_CAR_UPP_LOC_CD" , "");
	    	    		jrParam.setField("YD_RULE_PL_RS_GP"  , "");
	    	    	}
					
					// 운송지시일자(TRANS_ORD_DATE)와 운송지시순번(TRANS_ORD_SEQNO)은 클리어가 되어야 함
					jrParam.setField("TRANS_ORD_DATE"  , "");
					jrParam.setField("TRANS_ORD_SEQNO" , "");
					
	    	    	ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStockDmYdN_PIDEV", logId, mthdNm, "저장품 수정");
					
				} //end for
			}						
		
			commPiUtils.printLog(logId, mthdNm, "S-");
			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, mthdNm, e));
		}
	}		
	
	/**	 
	 * [A] 오퍼레이션명 :제품상태(열연코일)-출하지시대기,목전,반품,보관지시_
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public JDTORecord rcvM10LMYDJ1011(JDTORecord rcvMsg) throws DAOException { //S
		String mthdNm = "[YdCoilL3RcvPISeEJB.rcvM10LMYDJ1011] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		 
		try {
			
			commPiUtils.printLog(logId, mthdNm, "S+");
			commPiUtils.printParam(logId, rcvMsg);
	
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI0", "J", "*");
			
//			if ("N".equals(sApplyYnPI)) {
//				commPiUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return jrRtn; 
//			}						
			
			//수신 항목 값
			String msgId    	     = commPiUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydGp		         = commPiUtils.trim(rcvMsg.getFieldString("YD_GP"             )); // 정보구분
			String infoGp		     = commPiUtils.trim(rcvMsg.getFieldString("INFO_GP"           )); // 정보구분
			String sStlAppearGp      = commPiUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"     )); // 재료외형구분   
			String sStlNo            = commPiUtils.trim(rcvMsg.getFieldString("STL_NO"            )); // 재료번호     
			String sCurrProgCd       = commPiUtils.trim(rcvMsg.getFieldString("CURR_PROG_CD"      )); // 현재진도코드    
			String sOrdYeojaeGp      = commPiUtils.trim(rcvMsg.getFieldString("ORD_YEOJAE_GP"     )); // 주문여재구분    
			String sOrdNo            = commPiUtils.trim(rcvMsg.getFieldString("ORD_NO"            )); // 주문번호      
			String sOrdDtl           = commPiUtils.trim(rcvMsg.getFieldString("ORD_DTL"           )); // 주문행번      
			String sOrdGp            = commPiUtils.trim(rcvMsg.getFieldString("ORD_GP"            )); // 수주구분      
			String sCustCd           = commPiUtils.trim(rcvMsg.getFieldString("CUST_CD"           )); // 고객코드      
			String sDestCd           = commPiUtils.trim(rcvMsg.getFieldString("DEST_CD"           )); // 목적지코드     
			String sDlvrddRuleDd     = commPiUtils.trim(rcvMsg.getFieldString("DLVRDD_RULE_DD"    )); // 납기기준일     
			String sDestTelNo        = commPiUtils.trim(rcvMsg.getFieldString("DEST_TEL_NO"       )); // 목적지전화번호   
			String sDistShipassignGp = commPiUtils.trim(rcvMsg.getFieldString("DIST_SHIPASSIGN_GP")); // 출하배선지시구분 
			String sDistGoodsGp  	 = commPiUtils.trim(rcvMsg.getFieldString("DIST_GOODS_GP"     )); //출하제품구분 H:코일, T:HRPLATE
			String sOldTransOrdDt  	 = commPiUtils.trim(rcvMsg.getFieldString("OLD_TRN_REQ_DATE"  )); //구운송지시일자
			String sOldTransOrdSeqNo = commPiUtils.trim(rcvMsg.getFieldString("OLD_TRN_REQ_SEQ "));
			String sNewTransOrdDt  	 = commPiUtils.trim(rcvMsg.getFieldString("NEW_TRN_REQ_DATE" )); //신운송지시일자
			String sNewTransOrdSeqNo = commPiUtils.trim(rcvMsg.getFieldString("NEW_TRN_REQ_SEQ"));
			String sCancelYn         = commPiUtils.trim(rcvMsg.getFieldString("CANCEL_YN"         )); // Y: 취소 , N: 지시

			String sModifier 	     = commPiUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }
			mthdNm = msgId.substring(0, 2) + mthdNm;
			
			rcvMsg.setField("TC_CREATE_DDTT"      ,	commPiUtils.trim(rcvMsg.getFieldString("MQ_TC_CREATE_DDTT")) ); // 전송일시
			rcvMsg.setField("OLD_TRANS_WORD_DATE" ,	commPiUtils.trim(rcvMsg.getFieldString("OLD_TRN_REQ_DATE"))  ); 
			rcvMsg.setField("OLD_TRANS_WORD_SEQNO",	commPiUtils.trim(rcvMsg.getFieldString("OLD_TRN_REQ_SEQ"))   ); 
			rcvMsg.setField("NEW_TRANS_WORD_DATE" ,	commPiUtils.trim(rcvMsg.getFieldString("NEW_TRN_REQ_DATE"))  ); 
			rcvMsg.setField("NEW_TRANS_WORD_SEQNO",	commPiUtils.trim(rcvMsg.getFieldString("NEW_TRN_REQ_SEQ"))   ); 
			
			
			if("1".equals(infoGp)) { // 코일제품목전(DMYDR014)

				mthdNm += "[ 코일제품목전(DMYDR014) ]";			

				jrRtn = JDTORecordFactory.getInstance().create();
				
				String[] rVal      = new String[1]; //목표행선 조회용
				
				if ("Y".equals(sCancelYn)) {
				    jrRtn = this.procDmTcCnclPI(rcvMsg);
				    return jrRtn;
				}
				/**********************************************************
				* 0. 수신 항목 값 Check
				**********************************************************/
				JDTORecord jrParam = commPiUtils.getParam(logId, mthdNm, sModifier);
				
				jrParam.setField("STL_APPEAR_GP"     , sStlAppearGp     );
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
//PIDEV_S :병행가동용:PI_YD
				rcvMsg.setField("PI_YD",    	"J");					
				rVal = coilDao.getYdAimRtGp("C", rcvMsg);
				jrParam.setField("YD_AIM_RT_GP"	    , rVal[0]);	
				
				ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStockDMYDR014", logId, mthdNm, "TB_YD_STOCK 수정");

				/**********************************************************
				* 9. 저장품 제원 야드L2 전송 (YDY5L002)
				**********************************************************/
				JDTORecord jDrd = JDTORecordFactory.getInstance().create();
				jDrd.setField("JMS_TC_CD"			, "YDY5L002");
				jDrd.setField("YD_INFO_SYNC_CD"		, "5");	//1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)
				jDrd.setField("STL_NO"		        , sStlNo);
				
//PIDEV_S :병행가동용:PI_YD
				jDrd.setField("PI_YD",    	"J");				
				jrRtn = commPiUtils.addSndData(jrRtn , coilDao.getMsgL2("YDY5L002", jDrd));
				
				commPiUtils.printLog(logId, mthdNm, "S-");
				
				return jrRtn;
	
				
			} else if("2".equals(infoGp)) { // 코일제품보관지시(DMYDR027)
				
				mthdNm += "[ 코일제품보관지시(DMYDR027) ]";
				
				String[] rVal      = new String[1]; //목표행선 조회용

				if ("Y".equals(sCancelYn)) {
				    jrRtn = this.procDmTcCnclPI(rcvMsg);
				    return jrRtn;
				}
				
				/**********************************************************
				* 1. 수신 항목 값 Check
				**********************************************************/
				JDTORecord jrParam = commPiUtils.getParam(logId, mthdNm, sModifier);
				jrParam.setField("STL_APPEAR_GP"     , sStlAppearGp     );
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
//PIDEV_S :병행가동용:PI_YD
				rcvMsg.setField("PI_YD",    	"J");				
				rVal = coilDao.getYdAimRtGp("C", rcvMsg);
				jrParam.setField("YD_AIM_RT_GP"	     , rVal[0]);
				
				jrParam.setField("SCARFING_YN"       ,"Y"     );
				
				ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStockDMYDR027", logId, mthdNm, "TB_YD_STOCK 수정");
				
				/**********************************************************
				* 9. 저장품 제원 야드L2 전송 (YDY5L002)
				**********************************************************/
				JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
				sndL2Msg.setField("JMS_TC_CD"			, "YDY5L002");
				sndL2Msg.setField("YD_INFO_SYNC_CD"		, "5");	//1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)
				sndL2Msg.setField("STL_NO"		        , sStlNo);
		
//PIDEV_S :병행가동용:PI_YD
				sndL2Msg.setField("PI_YD",    	"J");				
				jrRtn = commPiUtils.addSndData(jrRtn ,coilDao.getMsgL2("YDY5L002", sndL2Msg));
				 
				commPiUtils.printLog(logId, mthdNm, "S-");
				return jrRtn;
				

			} else if("3".equals(infoGp)) { // 코일제품반품(DMYDR033)
	
				mthdNm += "[ 코일제품반품(DMYDR033) ]";
				
				String sMsg = "";
				jrRtn = JDTORecordFactory.getInstance().create();
				
				String[] rVal      = new String[1]; //목표행선 조회용
				
				/**********************************************************
				* 1. 수신 항목 값 Check
				**********************************************************/  			
				JDTORecord jrParam = commPiUtils.getParam(logId, mthdNm, sModifier);
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
				
//PIDEV_S :병행가동용:PI_YD
				rcvMsg.setField("PI_YD",    	"J");					
				rVal= coilDao.getYdAimRtGp("C",rcvMsg );		
				jrParam.setField("YD_AIM_RT_GP"      , rVal[0]);

				// 저장품 갱신
				int intRtnVal = ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStockDMYDR033", logId, mthdNm, "TB_YD_STOCK 수정");
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
                                              				
				JDTORecordSet jsStock = ydPICommDAO.selectJ(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdStockTRANS_ORD_DAT", logId, mthdNm, "(운송일자, 운송순번)로 저장품 조회");

			    if (jsStock.size() > 0) {
			    	commPiUtils.printLog(logId, mthdNm+ "[코일제품반품(DMYDR033)] 이전 운송지시번호로 변경 대상이 존재 함", "SL");
			    	
					// 레코드생성
					jrParam.setField("OLD_TRANS_WORD_DATE"			, sOldTransOrdDt   );
					jrParam.setField("OLD_TRANS_WORD_SEQNO"			, sOldTransOrdSeqNo);
					jrParam.setField("NEW_TRANS_WORD_DATE"			, sNewTransOrdDt   );
					jrParam.setField("NEW_TRANS_WORD_SEQNO"			, sNewTransOrdSeqNo);
					//--------------------------------------------------------------------------------
					//	차량스케줄 운송지시 변경
					//--------------------------------------------------------------------------------
					
					intRtnVal = ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdCarschTransOrd", logId, mthdNm, "TB_YD_CARSCH 차량스케줄 운송지시 변경");
					
					//--------------------------------------------------------------------------------
					//	검수재료 운송지시 변경
					//--------------------------------------------------------------------------------
					intRtnVal = ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdExamTransOrd", logId, mthdNm, "TB_YD_EXAMINATIONCHKLIST 검수재료 운송지시 변경");

					//--------------------------------------------------------------------------------
					//	재료정보 운송지시 변경
					//--------------------------------------------------------------------------------
					intRtnVal = ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStockTransOrd", logId, mthdNm, "TB_YD_STOCK 재료정보 운송지시 변경");
				}
				//---------------------------------------------------------------------------- 
			
				//차량스케줄ID 조회--------------------------------------------------------------
				String ydCarSchId = "";
				JDTORecordSet jsCarsch = ydPICommDAO.selectJ(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdCarschByTransDTSeq", logId, mthdNm, "(운송일자, 운송순번)로 TB_YD_CARSCH 조회");			
				if (jsCarsch.size() <= 0 ) {
					
					sMsg = "["+mthdNm+"] 운송지시일자 :["+sNewTransOrdDt+"] , 운송지시순번["+sNewTransOrdSeqNo+"]로 차량스케줄 조회 시 오류발생 - 메세지 : " + jsCarsch.size();
					commPiUtils.printLog(logId, sMsg, "SL");	
					return jrRtn ;
				} else {
					
					ydCarSchId = commPiUtils.trim(jsCarsch.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
				}			
				//--------------------------------------------------------------------------------
				
				
				//1.차량스케줄 재료 삭제--------------------------------------------------------------
				jrParam.setField("YD_CAR_SCH_ID", 	ydCarSchId);
				
				ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.delYdCarftmvmtlDMYDR033", logId, mthdNm, "TB_YD_CARFTMVMTL 삭제");
				
				//2.검수재료 삭제--------------------------------------------------------------------
				jrParam.setField("TRANS_ORD_DATE", 			sNewTransOrdDt);
				jrParam.setField("TRANS_ORD_SEQNO", 		sNewTransOrdSeqNo);
	 			
				ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.delYdExaminationmtl", logId, mthdNm, "TB_YD_EXAMINATIONCHKLIST 삭제");
					
				//3.저장품재료 --------------------------------------------------------------------
				
				jrParam.setField("TRANS_ORD_DATE"	, "");
				jrParam.setField("TRANS_ORD_SEQNO"	, "");
				jrParam.setField("CAR_NO"           , "");
				jrParam.setField("CARD_NO"          , "");
				jrParam.setField("YD_STK_BED_NO"    , "");
				jrParam.setField("YD_CAR_UPP_LOC_CD", "");
				jrParam.setField("STL_NO"           , sStlNo);
				
				ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStockDMYDR033E", logId, mthdNm, "저장품 수정");

				//======================================================
				// 저장품제원 : 코일야드L2 로 송신(YDY5L002)
				//======================================================
				JDTORecord sndL2Msg = commPiUtils.getParam(logId, mthdNm, sModifier);
				sndL2Msg.setField("YD_INFO_SYNC_CD"	, "5"       ); //야드정보동기화코드
				sndL2Msg.setField("MSG_GP"			, "I"       ); //전문구분
				sndL2Msg.setField("STL_NO"       	, sStlNo ); //재료번호
//PIDEV_S :병행가동용:PI_YD
				sndL2Msg.setField("PI_YD",    	"J");				
				jrRtn = commPiUtils.addSndData(jrRtn,coilDao.getMsgL2("YDY5L002", sndL2Msg));	 //전송 Data 생성	
				
				
				commPiUtils.printLog(logId, mthdNm, "S-");
				return jrRtn;

				
				
			} else if("4".equals(infoGp)) { // 코일제품출하지시대기(DMYDR005)

				
				mthdNm += "[ 코일제품출하지시대기(DMYDR005) ]";
				
				//수신 항목 값

				int intRtnVal = 0;
				jrRtn = JDTORecordFactory.getInstance().create();
				
				/**********************************************************
				* 0. 수신 항목 값 Check
				**********************************************************/
				if ("".equals(sStlNo)) {
					throw new Exception("저장품Id가 없습니다..");
				}

				JDTORecord jrParam = commPiUtils.getParam(logId, mthdNm, sModifier);
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
				
				/**********************************************************
				* 1. 야드목표행선지구분 수정
				**********************************************************/
				String[] rVal = new String[1];
//PIDEV_S :병행가동용:PI_YD
				rcvMsg.setField("PI_YD",    	"J");				
				rVal= coilDao.getYdAimRtGp("C",rcvMsg );		

				commPiUtils.printLog(logId, "YD_AIM_RT_GP" + rVal[0] + "  STL_PROG_CD"+  rVal[1], "SL");
				
				jrParam.setField("YD_AIM_RT_GP", rVal[0]);
				jrParam.setField("STL_PROG_CD" , rVal[1]);
				
				jrParam.setField("STL_NO"	   , sStlNo); //저장품 ID

				intRtnVal = ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStockAimRtGpStlProgCd", logId, mthdNm, "저장품 수정");
					
				if (intRtnVal == 0) {
					throw new Exception("수신한 재료번호 ["+ sStlNo+"]에 대한 저장품 DATA가 존재하지 않음");
				}
				
				/**********************************************************
				* 2. 대상 코일 위치 판단
				*  - 코일 위치가 야드가 아니면 전문 송신 안함
				**********************************************************/
			
				JDTORecordSet jsLyr = ydPICommDAO.selectJ(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getStkLyrByStlNo", logId, mthdNm, "저장품위치 조회");

				boolean bYd = true;
				String ydStkColGp = "";
				for (int i = 0; i < jsLyr.size(); ++i) {
					
					ydStkColGp = commPiUtils.trim(jsLyr.getRecord(i).getFieldString("YD_STK_COL_GP"));
					bYd = ydStkColGp.matches("[J][A-H]\\d\\d\\d\\d");
				
					if (!bYd) {
						commPiUtils.printLog(logId, mthdNm, "S-");
						return jrRtn;
					}
				}
				
				/**********************************************************
				* 99. 저장품제원 : 코일야드L2 로 송신(YDY5L002)
				**********************************************************/
				JDTORecord sndL2Msg = commPiUtils.getParam(logId, mthdNm, "");
				sndL2Msg.setField("JMS_TC_CD"       , "YDY5L002");
				sndL2Msg.setField("YD_INFO_SYNC_CD"	, "5"       ); //야드정보동기화코드
				sndL2Msg.setField("MSG_GP"			, "I"       ); //전문구분
				sndL2Msg.setField("STL_NO"       	, sStlNo ); //재료번호
				sndL2Msg.setField("YD_STK_COL_GP"   , "");
				sndL2Msg.setField("YD_STK_BED_NO"   , "");
//PIDEV_S :병행가동용:PI_YD
				sndL2Msg.setField("PI_YD",    	"J");						
				jrRtn = commPiUtils.addSndData(jrRtn,coilDao.getMsgL2("YDY5L002", sndL2Msg));	 //전송 Data 생성	
				
				commPiUtils.printLog(logId, mthdNm, "S-");
				return jrRtn;
	
			}
			
			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**	 
	 * [A] 오퍼레이션명 :코일제품반납확정(DMYDR008)_
	 * CCoilL3RcvSeEJBBean.java	-> rcvDMYDR008
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public JDTORecord rcvM10LMYDJ1021(JDTORecord rcvMsg) throws DAOException { //S
		String mthdNm = "[YdCoilL3RcvPISeEJB.rcvM10LMYDJ1021 [코일제품반납확정(DMYDR008)]] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		try {
			
			int intRtnVal = 0;
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			
			commPiUtils.printLog(logId, mthdNm, "S+");
			commPiUtils.printParam(logId, rcvMsg);

//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI0", "J", "*");
			
//			if ("N".equals(sApplyYnPI)) {
//				commPiUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return jrRtn; 
//			}						
			
			//수신 항목 값
			String msgId                 = commPiUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID

			String ydGp                  = commPiUtils.trim(rcvMsg.getFieldString("YD_GP"                  )); //야드구분
			String sStlAppearGp          = commPiUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"          )); //재료외형구분
 			String sStlNo                = commPiUtils.trim(rcvMsg.getFieldString("STL_NO"                 )); //재료번호
            String sCurrProgCd           = commPiUtils.trim(rcvMsg.getFieldString("CURR_PROG_CD"           )); //현재진도코드
			String sWoCarPlntProcCd      = commPiUtils.trim(rcvMsg.getFieldString("WO_CAR_PLNT_PROC_CD"    )); //지시차공장공정코드
			String sFrtomoveOrdDate      = commPiUtils.trim(rcvMsg.getFieldString("FRTOMOVE_ORD_DATE"      )); //이송지시일자
			String sUrgentFrtoMoveWirdGp = commPiUtils.trim(rcvMsg.getFieldString("URGENT_FRTOMOVE_WORD_GP")); //긴급이송작업지시구분
			String sCancelYn             = commPiUtils.trim(rcvMsg.getFieldString("CANCEL_YN"              )); //취소유무
			
			String sModifier   = commPiUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)

			if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }
			mthdNm = msgId.substring(0, 2) + mthdNm;
			
			/**********************************************************
			* 0. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(sStlNo)) {
				throw new Exception("저장품Id가 없습니다..");
			}

			JDTORecord jrParam = commPiUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("MQ_TC_CD"          , msgId            );
			
			if ("Y".equals(sCancelYn)) {
				jrRtn = this.procDmTcCnclPI(rcvMsg);
				return jrRtn;
			}
				
			/**********************************************************
			* 1. 야드목표행선지구분 수정
			**********************************************************/
			String[] rVal = new String[1];
			
//PIDEV_S :병행가동용:PI_YD
			rcvMsg.setField("PI_YD",    	"J");					
			rVal= coilDao.getYdAimRtGp("C",rcvMsg );		
			jrParam.setField("YD_AIM_RT_GP", rVal[0]);
			jrParam.setField("STL_NO"	   , sStlNo); //저장품 ID

			intRtnVal = ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStockAimRtGpStlProgCd", logId, mthdNm, "저장품 수정");
				
			if (intRtnVal == 0) {
				//throw new Exception("수신한 재료번호 ["+ sStlNo+"]에 대한 저장품 DATA가 존재하지 않음");
				commPiUtils.printLog(logId, "수신한 재료번호 ["+ sStlNo+"]에 대한 저장품 DATA가 존재하지 않음", "SL");
				return jrRtn;
			}
			
			/**********************************************************
			* 2. 저장품제원 : 코일야드L2 로 송신(YDY5L002)
			**********************************************************/
			JDTORecord sndL2Msg = commPiUtils.getParam(logId, mthdNm, "");
			sndL2Msg.setField("JMS_TC_CD"       , "YDY5L002");
			sndL2Msg.setField("YD_INFO_SYNC_CD"	, "5"       ); //야드정보동기화코드
			sndL2Msg.setField("MSG_GP"			, "I"       ); //전문구분
			sndL2Msg.setField("STL_NO"       	, sStlNo ); //재료번호
			sndL2Msg.setField("YD_STK_COL_GP"   , "");
			sndL2Msg.setField("YD_STK_BED_NO"   , "");
//PIDEV_S :병행가동용:PI_YD
			sndL2Msg.setField("PI_YD",    	"J");					
			jrRtn = commPiUtils.addSndData(jrRtn,coilDao.getMsgL2("YDY5L002", sndL2Msg));	 //전송 Data 생성	
			
			commPiUtils.printLog(logId, mthdNm, "S-");
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, mthdNm, e));
		}		
	}		

	/**	 
	 * [A] 오퍼레이션명 :코일제품운송상차지시(DMYDR060)_
	 * CCoilL3RcvSeEJBBean.java	-> rcvDMYDR060
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public JDTORecord rcvM10LMYDJ1031(JDTORecord rcvMsg) throws DAOException { //S
		String mthdNm = "[YdCoilL3RcvPISeEJB.rcvM10LMYDJ1031[코일제품운송상차지시(DMYDR060)]] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		try {
			
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			
			commPiUtils.printLog(logId, mthdNm, "S+");
			commPiUtils.printParam(logId, rcvMsg);

//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI0", "J", "*");
			
//			if ("N".equals(sApplyYnPI)) {
//				commPiUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return jrRtn; 
//			}						
			
			rcvMsg.setField("TC_CREATE_DDTT" , commPiUtils.trim(rcvMsg.getFieldString("MQ_TC_CREATE_DDTT")) ); // 전송일시
			rcvMsg.setField("TRANS_ORD_DT"   , commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE")) );      // 운송의뢰일자
			rcvMsg.setField("TRANS_ORD_SEQNO", commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ")) );       // 운송의뢰순번
			
			//수신 항목 값
			String msgId    = commPiUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			
			String sStlAppearGp   = commPiUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP")); //재료외형 구분
			String sCmbnCarldYn   = commPiUtils.trim(rcvMsg.getFieldString("CMBN_CARLD_YN")); //조합상차유무
			String sTransOrdDt    = commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE")); //운송지시일자
			String sTransOrdSeqno = commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ")); //운송지시순번
			String sCarNo         = commPiUtils.trim(rcvMsg.getFieldString("CAR_NO")); //차량번호
			String sCardNo        = ""; //카드번호
			String sLotNo         = commPiUtils.trim(rcvMsg.getFieldString("LOT_NO")); //LOT번호
			String sCarKind       = commPiUtils.trim(rcvMsg.getFieldString("CAR_KIND")); //차량종류
			String sCancelYn      = commPiUtils.trim(rcvMsg.getFieldString("CANCEL_YN"         )); // Y: 취소 , N: 지시
			int iYdEqpWrkSh       = Integer.parseInt(commPiUtils.nvl(rcvMsg.getFieldString("YD_EQP_WRK_SH"),"0")); //야드설비작업매수
			
			String sModifier          = commPiUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }
			mthdNm = msgId.substring(0, 2) + mthdNm;
			
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
			    jrRtn = this.procDmTcCnclPI(rcvMsg);
			    return jrRtn;
			}
			
			JDTORecord jrParam = commPiUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("MQ_TC_CD"         , msgId);	//TC_CODE
			
			jrParam.setField("YD_RULE_PL_RS_GP" , sCmbnCarldYn);
			jrParam.setField("STL_APPEAR_GP"    , sStlAppearGp);
			jrParam.setField("TRANS_ORD_DATE"   , sTransOrdDt);
			jrParam.setField("TRANS_ORD_SEQNO"  , sTransOrdSeqno); 
 			jrParam.setField("CAR_NO"           , sCarNo);
			jrParam.setField("CARD_NO"          , sCardNo);
 			jrParam.setField("CARD_NO"          , "");
 			
			jrParam.setField("CAR_LOTID"        , sLotNo);
			
			//수신된 전문의 STL_NO의 수 만큼 Loop
			for (int ii = 1 ; ii <= iYdEqpWrkSh; ii++) {
				ydGp         = commPiUtils.trim(rcvMsg.getFieldString("YD_GP"+ii)); 
				sStlNo 		 = commPiUtils.trim(rcvMsg.getFieldString("STL_NO"+ii)); //재료번호
				sGdsCarldLoc = commPiUtils.trim(rcvMsg.getFieldString("GDS_CARLD_LOC"+ii)); //상차위치
				
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
				
				if ("0".equals(ydGp)||"1".equals(ydGp)||"3".equals(ydGp)) {
					//0:A열연 SLAB야드,1:A열연 COIL야드,3:B열연 COIL야드
					
					//저장품 이동 조건 
					jrParam.setField("STOCK_ID" , sStlNo); //저장품 ID
					//PIDEV_S :병행가동용:PI_YD
					jrParam.setField("PI_YD",    	"J");								
					sStockMoveTerm = commPiUtils.trim(coilDao.getCoilCurrProgCd(jrParam).getFieldString("STOCK_MOVE_TERM"));
					
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
						ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYfStock", logId, mthdNm, "TB_YF_STOCK 수정");
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
						ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYmStock", logId, mthdNm, "TB_YM_STOCK 수정");
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
					ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updWbookCancel", logId, mthdNm, "작업예약 삭제");
					
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
					ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updWbookMtlCancel", logId, mthdNm, "작업예약제료 삭제");
					
					
//PIDEV_S :병행가동용:PI_YD
					jrParam.setField("PI_YD",    	"J");							
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
					ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStockDMYDR060", logId, mthdNm, "TB_YD_STOCK 수정");
				}
				
			} // end of for loop
			
			//야드핸들링정보 송신////////////////////////////////////////////////////////////////////////////////
			//코일야드 인 경우 우선 적용
			if ("1".equals(ydGp)||"3".equals(ydGp)||"H".equals(ydGp)||"J".equals(ydGp) ) { 
				//1:A열연 COIL야드,3:B열연 COIL야드,H:C열연 COIL소재야드,J:C열연 COIL제품야드
				
				//출하제품핸들링횟수 구하기
				jrParam.setField("TRANS_ORD_DATE"	, sTransOrdDt);
				jrParam.setField("TRANS_ORD_SEQNO"	, sTransOrdSeqno);
				
				JDTORecordSet rsResult = ydPICommDAO.selectJ(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getHandlingCnt", logId, mthdNm, "출하Handling 갯수 구하기");
				
				/*******************************
				 * 야드핸들링정보 송신 YDDMR050
				 *******************************/
				for(int ii = 0; ii < rsResult.size() ; ii++) {
					
					jrParam.setField("YD_GP"				, commPiUtils.trim(rsResult.getRecord(ii).getFieldString("YD_GP")) );
					jrParam.setField("TRANS_ORD_DT"			, sTransOrdDt);
					jrParam.setField("TRANS_ORD_SEQNO"		, sTransOrdSeqno);
					jrParam.setField("CMBN_CARLD_YN"		, sCmbnCarldYn );
					jrParam.setField("CARLD_PNT_CD"			, commPiUtils.trim(rsResult.getRecord(ii).getFieldString("CARLD_PNT_CD")) );
					jrParam.setField("CAR_NO"				, sCarNo );
					jrParam.setField("HANDLING_CNT"			, commPiUtils.trim(rsResult.getRecord(ii).getFieldString("HANDLING_CNT")) ); 
					jrParam.setField("YD_STK_BED_WHIO_STAT"	, "" );
					
					//전송 Data 생성
					jrRtn = commPiUtils.addSndData(jrRtn,coilDao.getMsgL3("M10YDLMJ1051", jrParam));						
				}
			}
			
			commPiUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, mthdNm, e));
		}	
	}		
	
	/**	 
	 * [A] 오퍼레이션명 :대기장도착실적(DMYDR061), 코일이송상차대기장도착PDA(DMYDR070)_ 
	 * CCoilL3RcvSeEJBBean.java	-> rcvDMYDR061
	 * CCoilL3RcvSeEJBBean.java	-> rcvDMYDR070
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public JDTORecord rcvM10LMYDJ1041(JDTORecord rcvMsg) throws DAOException { //S
		String mthdNm = "[YdCoilL3RcvPISeEJB.rcvM10LMYDJ1041[대기장도착실적(DMYDR061),코일이송상차대기장도착PDA(DMYDR070)]] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
	    JDTORecord	jrRtn = JDTORecordFactory.getInstance().create();

		try
		{
			commPiUtils.printLog(logId, mthdNm, "S+");
	
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI0", "J", "*");
//			
//			if ("N".equals(sApplyYnPI)) {
//				commPiUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return jrRtn; 
//			}
			
			rcvMsg.setField("TRANS_ORD_DT"   , commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE")) );      // 운송의뢰일자
			rcvMsg.setField("TRANS_ORD_SEQNO", commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ")) );       // 운송의뢰순번
			
			String msgId     = commPiUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sWorkGp	 = commPiUtils.trim(rcvMsg.getFieldString("WORK_GP"));   //야드구분
			String sModifier = commPiUtils.trim(rcvMsg.getFieldString("MODIFIER"));
//1118 추가			
			String ydSndYn   = commPiUtils.nvl (rcvMsg.getFieldString("YD_SND_YN"),"N"); //복수동시 필요
			if("Y".equals(ydSndYn)) {
				Thread.sleep(1000);
			}
			
			if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }
			
			rcvMsg.setField("MODIFIER", sModifier);
			
			if(! "9".equals(sWorkGp) ) {
				// 코일대기장도착
				jrRtn = this.procM10LMYDJ_DMYDR061(rcvMsg);	
			} else {
				// 코일이송상차대기장도착PDA
				jrRtn = this.procM10LMYDJ_DMYDR070(rcvMsg);
			}			
			
			commPiUtils.printLog(logId, mthdNm, "S-");
			
			return jrRtn;			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, mthdNm, e));
		}		
	}		
	
	/**
	 * [A] 오퍼레이션명 :코일이송상차도착PDA(DMYDR071)_
	 * CCoilL3RcvSeEJBBean.java	-> rcvDMYDR071
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public JDTORecord rcvM10LMYDJ1051(JDTORecord rcvMsg) throws DAOException { //S
		String mthdNm = "[YdCoilL3RcvPISeEJB.rcvM10LMYDJ1051 [코일이송상차도착PDA(DMYDR071)]] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		try {
			
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			
			commPiUtils.printLog(logId, mthdNm, "S+");
			commPiUtils.printParam(logId, rcvMsg);

//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI0", "J", "*");
//			
//			if ("N".equals(sApplyYnPI)) {
//				commPiUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return jrRtn; 
//			}						
			
			rcvMsg.setField("TC_CREATE_DDTT" , commPiUtils.trim(rcvMsg.getFieldString("MQ_TC_CREATE_DDTT")) ); // 전송일시
			rcvMsg.setField("TRANS_ORD_DT"   , commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE")) );      // 운송의뢰일자
			rcvMsg.setField("TRANS_ORD_SEQNO", commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ")) );       // 운송의뢰순번

			//수신 항목 값
			String msgId            = commPiUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydGp             = commPiUtils.trim(rcvMsg.getFieldString("YD_GP"          )); //야드구분
			String sTransOrdDt  	= commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE"   )); //운송실적일자
			String sTransOrdSeqno	= commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ")); //운송실적순번
			String sCarNo 			= commPiUtils.trim(rcvMsg.getFieldString("CAR_NO"         ));
//			String sCardNo          = commPiUtils.trim(rcvMsg.getFieldString("CARD_NO"        ));
			String sCardNo          = "";
			String sCarldPntCd		= commPiUtils.trim(rcvMsg.getFieldString("CARLD_PNT_CD"   )); // 상차포인트
			String sWorkGp 			= commPiUtils.trim(rcvMsg.getFieldString("WORK_GP"        )); // 작업구분
			String sCarKind		    = commPiUtils.trim(rcvMsg.getFieldString("CAR_KIND"       ));
			String sCrFrtomoveGp	= commPiUtils.trim(rcvMsg.getFieldString("CR_FRTOMOVE_GP" )); //11수출사내 41열연제품이송 63임가공 81열연소재이송  
			
			String sModifier 		= commPiUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }

			String sMsg            = "";
			String ydCarSchId       = "";
			String ydCarProgStat    = "";
			String ydCarWrkGp 		= ""; 	//야드차량작업구분
			String ydEqpWrkStat		= ""; 	//야드설비작업상태
			
			/**********************************************************
			* 0. 수신 항목 값 Check
			**********************************************************/
			JDTORecord jrParam = commPiUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("TRANS_ORD_DATE" , sTransOrdDt   );
			jrParam.setField("TRANS_ORD_SEQNO", sTransOrdSeqno);
			jrParam.setField("CARD_NO"        , sCardNo       );
			jrParam.setField("CAR_NO"         , sCarNo        );

			/***********************************
        	 * 1. 차량스케줄 중복 Check
        	 ***********************************/
			commPiUtils.printLog(logId, "1. 차량스케줄 중복 체크" , "SL");
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
			JDTORecordSet rsResult = ydPICommDAO.selectJ(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdCarYdCmbnCarldYn_PIDEV", logId, mthdNm, "차량정보 존재여부 체크");
			
			if (rsResult.size() > 0) {
				
				/*
				 * 도착전문에 중복 수신되면 Exception 처리함
				 */
				commPiUtils.printLog(logId, "이미 도착처리된 차량입니다." , "SL");
				
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
			JDTORecordSet jsCarPoint = ydPICommDAO.selectJ(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdCarPoint", logId, mthdNm, "차량스케줄 조회");
			
			if (jsCarPoint.size() != 1) {
				throw new Exception("차량포인트["+sCarldPntCd+"] 조회 실패");
			}
			
			String sCarNoOld  = jsCarPoint.getRecord(0).getFieldString("CAR_NO" ); //카번호
//			String sCardNoOld = jsCarPoint.getRecord(0).getFieldString("CARD_NO"); //카드번호
			
			// if (!sCarNo.equals(sCarNoOld) || !sCardNo.equals(sCardNoOld)) {
			if (!sCarNo.equals(sCarNoOld) ) {
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
			JDTORecordSet jsStlno = ydPICommDAO.selectJ(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdStockTransOrdDateSeqNo", logId, mthdNm, "운송실적번호에 맞는 제품번호가 존재 조회");			
			if (jsStlno.size() <= 0 ) {
				sMsg = "운송실적번호에 맞는 저장품 존재 안함: TRANS_WORD_NO:["+sTransOrdDt+sTransOrdSeqno+"]" ;
				commPiUtils.printLog(logId, sMsg, "S-");	
				throw new Exception(sMsg);
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
			ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStock2", logId, mthdNm, "저장품 수정");
			
			/*
			 * 3. 저장품이 적치된 저장위치 정보를 조회 - 저장위치가 04, 05, 06스판이면 A통로, 07스판이면 B통로로 처리한다.
			 */ 
			jrParam.setField("YD_STK_COL_GP", "");
			
			JDTORecordSet jsStock = ydPICommDAO.selectJ(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdStockYdStkLyrMtlCTransDTSeqCardNoDesc_PIDEV", logId, mthdNm, "조회");
			if (jsStock.size() <= 0) {
				commPiUtils.printLog(logId, "저장품 조회 실패", "S-");
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
				JDTORecordSet jsCarpnt = ydPICommDAO.selectJ(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdCarPoint", logId, mthdNm, "개소코드 조회");
				if (jsCarpnt.size() > 0) {
					String sWlocCd         = commPiUtils.nvl(jsCarpnt.getRecord(0).getFieldString("WLOC_CD"            ), "");
					String ydPntCd         = commPiUtils.nvl(jsCarpnt.getRecord(0).getFieldString("YD_PNT_CD"          ), "");	
					String ydStkColActStat = commPiUtils.nvl(jsCarpnt.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT"), "");
					
					commPiUtils.printLog(logId, "sWlocCd         : " + sWlocCd, "SL");
					commPiUtils.printLog(logId, "ydPntCd         : " + ydPntCd, "SL");
					commPiUtils.printLog(logId, "ydStkColActStat : " + ydStkColActStat, "SL");
					
					//TR가 도착해 있는 경우 
					if ("TR".equals(sCarKind) && "L".equals(ydStkColActStat)) {
						ydStkColActStat = "C";
					}
					
					if ("C".equals(ydStkColActStat)) {

						/* **********************************
						 * 출하차량도착실적처리 - 맵활성화
						 * **********************************/
						JDTORecord jrYdMsg = commPiUtils.getParam(logId, mthdNm, sModifier);
						jrYdMsg.setField("YD_GP"			, ydGp);
						jrYdMsg.setField("TRANS_ORD_DATE"	, sTransOrdDt);    
						jrYdMsg.setField("TRANS_ORD_SEQNO" 	, sTransOrdSeqno);
						jrYdMsg.setField("CAR_NO"			, sCarNo);
						jrYdMsg.setField("CARD_NO"			, sCardNo);
						jrYdMsg.setField("SPOS_WLOC_CD"		, sWlocCd);	
						jrYdMsg.setField("SPOS_YD_PNT_CD"	, ydPntCd);
						jrYdMsg.setField("CAR_KIND"			, sCarKind);
						jrYdMsg.setField("WORK_GP"			, sWorkGp);
						jrYdMsg.setField("IS_EJB_CALL"		, "N");
						
						EJBConnector ejbConn = new EJBConnector("default", "CCoilCarMvSeEJB", this);
						JDTORecord jrRst = (JDTORecord)ejbConn.trx("procOutCarArrWr", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
						
						String sRtnCd = commPiUtils.trim(jrRst.getFieldString("RTN_CD" ));
						
						if (!"1".equals(sRtnCd)) {
							return jrRtn;
						}
						
						String ydCarldStopLoc = commPiUtils.trim(jrRst.getFieldString("YD_STK_COL_GP" ));
						
						jrYdMsg.setField("YD_CARLD_STOP_LOC", ydCarldStopLoc);
						jrYdMsg.setField("CR_FRTOMOVE_GP"   , sCrFrtomoveGp);
						jrYdMsg.setField("YD_CAR_USE_GP"    , "G");
						jrYdMsg.setField("YD_BAY_GP"        , ydCarldStopLoc.substring(1, 2));

						/* *******************************
						 * 2열연코일출하상차LOT편성전문
						 * *******************************/
						jrYdMsg = (JDTORecord)ejbConn.trx("procCoilGdsDistCarLdComp", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
						
						jrRtn = commPiUtils.addSndData(jrRtn, jrYdMsg);
						
					} //if ("C".equals(ydStkColActStat))
				}
			}//if ("9".equals(sWorkGp))
				
			commPiUtils.printLog(logId, mthdNm, "S-");			
			
			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, mthdNm, e));
		}	
	}

	/**
	 * [A] 오퍼레이션명 :코일제품출하완료(DMYDR030)
	 * CCoilL3RcvSeEJBBean.java	-> rcvDMYDR030
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public JDTORecord rcvM10LMYDJ1071(JDTORecord rcvMsg) throws DAOException { //S
		String mthdNm = "[YdCoilL3RcvPISeEJB.rcvM10LMYDJ1071 [코일제품출하완료(DMYDR030)]] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		try {

			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			
			commPiUtils.printLog(logId, mthdNm, "S+");
			commPiUtils.printParam(logId, rcvMsg);
			
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI0", "J", "*");
//			
//			if ("N".equals(sApplyYnPI)) {
//				commPiUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return jrRtn; 
//			}						
			
			String msgId       	= commPiUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydGp        	= commPiUtils.trim(rcvMsg.getFieldString("YD_GP"        )); 
			String stlAppearGp	= commPiUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"));
			String sStlNo       = commPiUtils.trim(rcvMsg.getFieldString("STL_NO"       ));
//			String sCurrProgCd  = commPiUtils.trim(rcvMsg.getFieldString("CURR_PROG_CD" ));
//			String sCarNo    	= commPiUtils.trim(rcvMsg.getFieldString("CAR_NO"       ));
			String sBackUpYn    = commPiUtils.trim(rcvMsg.getFieldString("BACKUP_YN"    ));
			String sCancelChk   = commPiUtils.trim(rcvMsg.getFieldString("CANCEL_YN"    )); // Y: 취소 , N: 지시
			 
			String sModifier    = commPiUtils.trim(rcvMsg.getFieldString("MODIFIER"     ));//수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }
			
			String sMsg = "";
			
			String[] rVal      = new String[1]; //목표행선 조회용
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/  			
			JDTORecord jrParam = commPiUtils.getParam(logId, mthdNm, sModifier);
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
			   AND A.CAR_NO = C.CAR_NO(+) 
			 */                                                     				
			JDTORecordSet jsStock = ydPICommDAO.selectJ(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdStockJoinStkLyr2_PIDEV", logId, mthdNm, "차량정정보검색");
			
			if (jsStock.size() <= 0) {
				commPiUtils.printLog(logId, "YD_STOCK[코일제품출하완료] 조회 Error", "SL");	
				return jrRtn ;
			}

			String sCarNo    	  = commPiUtils.trim(jsStock.getRecord(0).getFieldString("CAR_NO"));
			String ydStkColGp 	  = commPiUtils.trim(jsStock.getRecord(0).getFieldString("YD_STK_COL_GP"));
			// String sCardNo        = commPiUtils.trim(jsStock.getRecord(0).getFieldString("CARD_NO"));
			String sTransOrdDate  = commPiUtils.trim(jsStock.getRecord(0).getFieldString("TRANS_ORD_DATE"));
			String sTransOrdSeqNo = commPiUtils.trim(jsStock.getRecord(0).getFieldString("TRANS_ORD_SEQNO"));
			String ydCarUppLocCd  = commPiUtils.nvl (jsStock.getRecord(0).getFieldString("YD_CAR_UPP_LOC_CD"), "01");
			
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
			ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStockCarldCmpl", logId, mthdNm, "저장품 수정");
			
			
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
			JDTORecordSet jsCarschInfo = ydPICommDAO.selectJ(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCarschByStlNoCarID", logId, mthdNm, "차량스케줄Id조회");
			String ydCarSchId = jsCarschInfo.size() > 0 ? commPiUtils.trim(jsCarschInfo.getRecord(0).getFieldString("YD_CAR_SCH_ID")) : "";				
			
			//***************************************************************************
			//  저장품이 적치된 저장위치 정보를 조회
			//***************************************************************************
			sMsg = "[" + mthdNm + "]카드번호["+sCarNo+"], 차량번호["+sCarNo+"], 운송지시일자["+sTransOrdDate
			      +"], 운송지시순번["+sTransOrdSeqNo+"] : 출하완료된 동["+ydStkColGp+"]의 저장품들 조회 시작";
			
			if ("*".equals(stlAppearGp) && "N".equals(sCancelChk)) {

				commPiUtils.printLog(logId, "[" + mthdNm + "] 마지막 상차완료 전문", "SL");
				jrParam.setField("CAR_NO" 			, sCarNo       );
//				jrParam.setField("CARD_NO"			, sCardNo      );
				jrParam.setField("TRANS_ORD_DATE"	, sTransOrdDate );
				jrParam.setField("TRANS_ORD_SEQNO"	, sTransOrdSeqNo);
				
				/*
				SELECT *
				  FROM (
				        SELECT *
				          FROM TB_YD_CARSCH
				         WHERE CAR_NO       LIKE :V_CAR_NO||'%'
				           AND TRANS_ORD_DATE  = :V_TRANS_ORD_DT
				           AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
				         ORDER BY YD_CAR_SCH_ID DESC
				       ) A
				 WHERE ROWNUM <= 1
		    	*/                                                     				
				JDTORecordSet jsCarsch = ydPICommDAO.selectJ(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCarschTransDTSeq2_PIDEV", logId, mthdNm, "차량정정보검색");
				 
				if (jsCarsch.size() > 0 ) {
//					sMsg = "차량스케쥴 조회 SELECT Error :: DO NOT EXIST"  ;
//					commPiUtils.printLog(logId, sMsg, "SL");	
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
					
					JDTORecord sndL3Msg = commPiUtils.getParam(logId, mthdNm, sModifier);
					sndL3Msg.setField("TC_CODE"			, "DMYDR040");	//전문코드
					sndL3Msg.setField("SPOS_WLOC_CD"	, commPiUtils.trim(jsCarsch.getRecord(0).getFieldString("SPOS_WLOC_CD")));
					sndL3Msg.setField("SPOS_YD_PNT_CD"	, commPiUtils.trim(jsCarsch.getRecord(0).getFieldString("YD_PNT_CD1"  )));
					sndL3Msg.setField("YD_GP"			, ydGp          );
					sndL3Msg.setField("TRANS_ORD_DT"    , commPiUtils.trim(jsCarsch.getRecord(0).getFieldString("TRANS_ORD_DATE" )));
					sndL3Msg.setField("TRANS_ORD_SEQNO" , commPiUtils.trim(jsCarsch.getRecord(0).getFieldString("TRANS_ORD_SEQNO")));
//					if ("".equals(sCarNo)) {
//						sCarNo = "XXXXX";
//					}					
					sndL3Msg.setField("CAR_NO" 			, sCarNo       );
					sndL3Msg.setField("CARD_NO"			, ""           );
					sndL3Msg.setField("MSG_ID"			, msgId        );
//					
					//전송 Data 생성
					sMsg = "차량번호[" + sCarNo + "]는 코일제품출하차량출발실적호출";
					commPiUtils.printLog(logId, sMsg, "SL");
					
					// PI_YD 병행가동
					sndL3Msg.setField("PI_YD", "J");
					
					EJBConnector ejbConn = new EJBConnector("default", "CCoilCarMvSeEJB", this);
					JDTORecord jrRtn1 = (JDTORecord)ejbConn.trx("procOutCarLevWr", new Class[] { JDTORecord.class }, new Object[] { sndL3Msg });
					jrRtn = commPiUtils.addSndData(jrRtn, jrRtn1);
				}	
			} else {
				commPiUtils.printLog(logId, "마지막 상차완료 전문이 아님", "SL");
			}
			
			/**********************************************************
			* 99. 저장품 제원 야드L2 전송 (YDY5L002)
			**********************************************************/
			JDTORecord sndL2Msg = commPiUtils.getParam(logId, mthdNm, sModifier);
			sndL2Msg.setField("JMS_TC_CD"			, "YDY5L002");
			sndL2Msg.setField("YD_INFO_SYNC_CD"		, "5");	//1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)
			sndL2Msg.setField("MSG_GP"			    , "D"       ); //전문구분
			sndL2Msg.setField("STL_NO"		        , sStlNo);
//PIDEV_S :병행가동용:PI_YD
			sndL2Msg.setField("PI_YD",    	"J");					
			jrRtn = commPiUtils.addSndData(jrRtn ,coilDao.getMsgL2("YDY5L002", sndL2Msg));
			
			/*******************************************
			 * layer정보가 존재 하는 경우 (백업으로 취소 처리 시)
			 *******************************************/
			commPiUtils.printLog(logId,"[코일제품출하완료][" + sStlNo + "] BACKUP 유무=>:+" + sBackUpYn +" CANCEL_YN=>:"+ sCancelChk, "SL");

			
			if ("Y".equals(sBackUpYn) && "N".equals(sCancelChk)) {
				jrParam.setField("COIL_NO", sStlNo);
				JDTORecordSet jsCoilcomm = ydPICommDAO.selectJ(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getPtCoilComm", logId, mthdNm, "코일공통 조회");
				
				if (jsCoilcomm.size() <= 0) {
					throw new Exception("TB_PT_COILCOMM 존재하지 않는 코일번호:" + sStlNo);
				}
				
				String ydEqpGp  = commPiUtils.trim(jsCoilcomm.getRecord(0).getFieldString("YD_EQP_GP"));
				
				if ("PT".equals(ydEqpGp)) {
					commPiUtils.printLog(logId, "정상처리된 코일입니다.", "S-");
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
				JDTORecordSet loadStacklayer = ydPICommDAO.selectJ(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdStklyrByStlNo", logId, mthdNm, "단정보검색");
				ydStkColGp        = "JX0101";
				String ydGpBay    = "";
				String ydStkBedNo = "01";
				String ydStkLyrNo = "001";
				
				if (loadStacklayer.size() > 0) {
					 
					ydStkColGp = commPiUtils.trim(loadStacklayer.getRecord(0).getFieldString("YD_STK_COL_GP"));
					ydStkBedNo = commPiUtils.trim(loadStacklayer.getRecord(0).getFieldString("YD_STK_BED_NO"));
					ydStkLyrNo = commPiUtils.trim(loadStacklayer.getRecord(0).getFieldString("YD_STK_LYR_NO"));
					/*
					UPDATE TB_YD_STKLYR
					   SET MODIFIER      = :V_MODIFIER
					     , MOD_DDTT      = SYSDATE
					     , YD_STK_LYR_MTL_STAT = 'E'
					     , YD_STK_LYR_ACT_STAT = 'E'
					     , STL_NO        = ''
					 WHERE STL_NO = :V_STL_NO
					 */
					ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStklyrInitC", logId, mthdNm, "적치단 수정");
					
					commPiUtils.printLog(logId, "[코일제품출하완료]["+sStlNo+"]에 저장위치맵을 비웁니다.+"+ydGpBay, "SL");
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
				ydPICommDAO.updateTx(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updateCoilCommonLocInfo", logId, mthdNm, "TB_PT_COILCOMM 저장위치 수정");
				
				/*************************
				 * 작업실적에 저장
				 *************************/
				JDTORecord jparam = commPiUtils.getParam(logId, mthdNm, sModifier);
				
				jparam.setField("STL_NO"           , sStlNo);
				jparam.setField("YD_EQP_ID"        , ydGpBay+"CR"+ "00");
				jparam.setField("YD_SCH_CD"        , "JX9999");
				jparam.setField("YD_WRK_DUTY"      , commPiUtils.getWorkDuty());  
				jparam.setField("YD_WRK_PARTY"     , commPiUtils.getWorkParty()); 
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
				ydPICommDAO.insert(jparam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.insYdWrkhistBackUp", logId, mthdNm, "작업실적 저장");
				
				/*************************
				 * 이송재료 정보 등록 - 백업처리대상이 반품될 경우 대비
				 *************************/
				jrParam = commPiUtils.getParam(logId, mthdNm, sModifier);
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
			
			
			/*******************************************
			 * 출하실적취소 시 상차이전 위치로 원복 한다. 2023.11.09 문제윤 주임 출하관제
			 *******************************************/
//			if ("Y".equals(sCancelChk)) {
//				jrParam.setField("COIL_NO", sStlNo);
//				JDTORecordSet jsCoilcomm = ydPICommDAO.selectJ(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getPtCoilComm", logId, mthdNm, "코일공통 조회");
//				
//				if (jsCoilcomm.size() <= 0) {
//					throw new Exception("TB_PT_COILCOMM 존재하지 않는 코일번호:" + sStlNo);
//				}
//				
//				String ydEqpGp  	 = commPiUtils.trim(jsCoilcomm.getRecord(0).getFieldString("YD_EQP_GP")); //현위치가 차량
//				String ydStrLocHis1  = commPiUtils.trim(jsCoilcomm.getRecord(0).getFieldString("YD_STR_LOC_HIS1")); //상차전 저장위치
//				
//				if (!"PT".equals(ydEqpGp)) {
//					commPiUtils.printLog(logId, "상차완료백업이 안된 코일이여서 상차전 위치로 원복 할 수 없습니다.", "S-");
//					throw new Exception("상차완료백업이 안된 코일이여서 상차전 위치로 원복 할 수 없습니다." + sStlNo);
//				}
//				 
//						
//					   ydStkColGp = commPiUtils.trim(ydStrLocHis1.substring(0, 6));
//				String ydStkBedNo = commPiUtils.trim(ydStrLocHis1.substring(6, 8));
//				String ydStkLyrNo = "0"+commPiUtils.trim(ydStrLocHis1.substring(8, 10));
//				String	ydGpBay	  = commPiUtils.trim(ydStrLocHis1.substring(1, 2));
//				
//				jrParam = commPiUtils.getParam(logId, mthdNm, sModifier);				
//				jrParam.setField("STL_NO"       , sStlNo    );
//				jrParam.setField("YD_STK_LYR_MTL_STAT", "C" );
//				jrParam.setField("YD_STK_COL_GP", ydStkColGp);
//				jrParam.setField("YD_STK_BED_NO", ydStkBedNo);  
//				jrParam.setField("YD_STK_LYR_NO", ydStkLyrNo);	
//				
//				
//				jsCoilcomm = ydPICommDAO.selectJ(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdStkLyrByPk", logId, mthdNm, "저장위치 조회");
//				
//				if (jsCoilcomm.size() > 0) {
//					String stlNoChk  	 = commPiUtils.trim(jsCoilcomm.getRecord(0).getFieldString("STL_NO"));
//					
//					if(!"".equals(stlNoChk)){
//						commPiUtils.printLog(logId, "상차전 위치로 원복 할 위치에 다른 코일번호 존재하여 원복 불가.", "S-");
//						throw new Exception("상차전 위치로 원복 할 위치에 다른 코일번호 존재하여 원복 불가." +ydStrLocHis1+" 코일번호:"+ stlNoChk);
//					}
//				}
//				
//				/***********************************
//				 * TB_YD_STKLYR 저장위치 Update 
//				 ***********************************/
//				
//				/*com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdStklyrInStlNo 
//				UPDATE TB_YD_STKLYR
//				      SET MODIFIER = :V_MODIFIER 
//				         ,MOD_DDTT = SYSDATE
//				         ,STL_NO = :V_STL_NO
//				         ,YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT 
//				  WHERE YD_STK_COL_GP =REPLACE(  :V_YD_STK_COL_GP  ,'HEKD01','HEDD01') 
//				    AND YD_STK_BED_NO = :V_YD_STK_BED_NO 
//				    AND YD_STK_LYR_NO = :V_YD_STK_LYR_NO  
//				 */
//				ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdStklyrInStlNo", logId, mthdNm, "적치단 수정");				
//				commPiUtils.printLog(logId, "[코일제품출하완료]["+sStlNo+"]에 저장위치맵을 복원합니다.", "SL");
//			 
//				
//				/***********************************
//				 * TB_YD_STOCK 저장품 수정  Update 
//				 ***********************************/
//				jrParam.setField("STL_APPEAR_GP", 	stlAppearGp);
//				jrParam.setField("DEL_YN"         , "N");
//				jrParam.setField("STL_PROG_CD"    , "M");
//				jrParam.setField("YD_AIM_RT_GP"   , "M2");
//				
//				//저장품 수정
//				/*
//				UPDATE TB_YD_STOCK
//				   SET MODIFIER      = :V_MODIFIER
//				     , MOD_DDTT      = SYSDATE
//				     , STL_APPEAR_GP = :V_STL_APPEAR_GP
//				     , YD_AIM_RT_GP  = :V_YD_AIM_RT_GP
//				     , DEL_YN        = :V_DEL_YN     
//				     , STL_PROG_CD   = :V_STL_PROG_CD
//				 WHERE STL_NO = :V_STL_NO 
//				*/
//				ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStockCarldCmpl", logId, mthdNm, "저장품 수정");
//				
//				
//				
//				/***********************************
//				 * Coil 공통 Table 저장위치 Update 
//				 ***********************************/	 
//
//				//코일 공통 상차백업 위치로 위치 변경 작업 
//				jrParam.setField("STL_NO"	, sStlNo       ); //재료번호
//				jrParam.setField("YD_LOC"	, ydStkColGp+ydStkBedNo+ydStkLyrNo ); //현재위치
//				
//				/*
//				UPDATE TB_PT_COILCOMM
//				   SET (  
//				         YD_GP                 -- 야드구분
//				       , YD_BAY_GP             -- 동
//				       , YD_EQP_GP             -- SPAN
//				       , YD_STK_COL_NO         -- 적치열번지
//				       , YD_STK_BED_NO         -- 적치번지
//				       , YD_STK_LYR_NO         -- 적치단
//				       , YD_STR_LOC            -- 현 저장위치코드
//				       , YD_STR_LOC_HIS1       -- 전 저장위치코드
//				       , YD_STR_LOC_HIS2       -- 전전 저장위치코드
//				       ) =
//				       (
//				        SELECT 
//				               SUBSTR(P_YD_LOC,1,1) AS YD_GP         -- 야드구분
//				             , SUBSTR(P_YD_LOC,2,1) AS YD_BAY_GP     -- 동
//				             , SUBSTR(P_YD_LOC,3,2) AS YD_EQP_GP     -- SPAN
//				             , SUBSTR(P_YD_LOC,5,2) AS YD_STK_COL_NO -- 적치열번지
//				             , SUBSTR(P_YD_LOC,7,2) AS YD_STK_BED_NO -- 적치번지
//				             , SUBSTR(P_YD_LOC,9,3) AS YD_STK_LYR_NO -- 적치단
//				             , SUBSTR(P_YD_LOC,1,8)||SUBSTR(P_YD_LOC,10,2) AS YD_STR_LOC         -- 현 저장위치코드   
//				             , YD_STR_LOC      AS YD_STR_LOC_HIS1    -- 전현 저장위치코드
//				             , YD_STR_LOC_HIS1 AS YD_STR_LOC_HIS2    -- 전전현 저장위치코드
//				          FROM TB_PT_COILCOMM
//				             ,(SELECT :V_YD_LOC AS P_YD_LOC FROM DUAL) 
//				         WHERE COIL_NO = :V_STL_NO
//				     )
//				 WHERE COIL_NO = :V_STL_NO
//				 */
//				ydPICommDAO.updateTx(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updateCoilCommonLocInfo", logId, mthdNm, "TB_PT_COILCOMM 저장위치 수정");
//				
//				/*************************
//				 * 작업실적에 저장 insert
//				 *************************/
//				JDTORecord jparam = commPiUtils.getParam(logId, mthdNm, sModifier);
//				
//				jparam.setField("STL_NO"           , sStlNo);
//				jparam.setField("YD_EQP_ID"        , "J"+ydGpBay+"CR"+ "00");
//				jparam.setField("YD_SCH_CD"        , "JX9999");
//				jparam.setField("YD_WRK_DUTY"      , commPiUtils.getWorkDuty());  
//				jparam.setField("YD_WRK_PARTY"     , commPiUtils.getWorkParty()); 
//				jparam.setField("YD_UP_WR_LOC"     , "J"+ydGpBay + "PT0101");
//				jparam.setField("YD_UP_WR_LAYER"   , "001"        );
//				jparam.setField("YD_DN_WR_LOC"     , ydStkColGp + ydStkBedNo);
//				jparam.setField("YD_DN_WR_LAYER"   , ydStkLyrNo           );
//				
//				/* com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.insYdWrkhistBackUp 
//				INSERT INTO TB_YD_WRKHIST
//				(
//				  YD_WRK_HIST_ID
//				, REGISTER
//				, REG_DDTT
//				, MODIFIER
//				, MOD_DDTT
//				, DEL_YN
//				, YD_GP
//				, STL_NO
//				, YD_CRN_SCH_ID
//				, YD_SCH_CD
//				, YD_UP_WR_LOC
//				, YD_UP_WR_LAYER
//				, YD_DN_WR_LOC
//				, YD_DN_WR_LAYER
//				, YD_WRK_DUTY
//				, YD_WRK_PARTY
//				, YD_EQP_ID
//				, YD_WRK_HDS_DD
//				, YD_DN_CMPL_DT
//				) VALUES (
//				  TO_CHAR(SYSDATE, 'YYYYMMDDHH24MI')||LPAD(YD_WRKHIST_SEQ.nextval,6,'0'))
//				, :V_MODIFIER
//				, SYSDATE
//				, :V_MODIFIER
//				, SYSDATE
//				, 'N'
//				, 'J'
//				, :V_STL_NO
//				, '000000000000000000'--YD_CRN_SCH_ID
//				, :V_YD_SCH_CD
//				, :V_YD_UP_WR_LOC
//				, :V_YD_UP_WR_LAYER
//				, :V_YD_DN_WR_LOC
//				, :V_YD_DN_WR_LAYER
//				, :V_YD_WRK_DUTY
//				, :V_YD_WRK_PARTY
//				, :V_YD_EQP_ID
//				, TO_CHAR(SYSDATE, 'YYYYMMDD')
//				, SYSDATE
//				)
//				*/
//				ydPICommDAO.insert(jparam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.insYdWrkhistBackUp", logId, mthdNm, "작업실적 저장");
//				  
//			}
	
			commPiUtils.printLog(logId, mthdNm, "S-");
			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, mthdNm, e));
		}	
	}

	/**
	 * [A] 오퍼레이션명 :코일이송하차대기장도착PDA(DMYDR073)
	 * CCoilL3RcvSeEJBBean.java	-> rcvDDMYDR073
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public JDTORecord rcvM10LMYDJ1091(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "[YdCoilL3RcvPISeEJB.rcvM10LMYDJ1091 [코일이송하차대기장도착PDA 송신(DMYDR073)]] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		try {
	
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			
			commPiUtils.printLog(logId, mthdNm, "S+");
			commPiUtils.printParam(logId, rcvMsg);			
			
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI0", "J", "*");
//			
//			if ("N".equals(sApplyYnPI)) {
//				commPiUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return jrRtn; 
//			}						
			
			rcvMsg.setField("TC_CREATE_DDTT" , commPiUtils.trim(rcvMsg.getFieldString("MQ_TC_CREATE_DDTT")) ); // 전송일시
			rcvMsg.setField("TRANS_ORD_DT"   , commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE")) );      // 운송의뢰일자
			rcvMsg.setField("TRANS_ORD_SEQNO", commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ")) );       // 운송의뢰순번
			
			//수신 항목 값
			String msgId            = commPiUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
//			String ydGp             = commUtils.trim(rcvMsg.getFieldString("YD_GP"          )); //야드구분
			String sStlAppearGp 	= commPiUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"  )); //재료외형
			String sTransOrdDt  	= commPiUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"   ));
			String sTransOrdSeqno	= commPiUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"));
			String sCancelYn     	= commPiUtils.trim(rcvMsg.getFieldString("CANCEL_YN"      ));
			String sCarKind		    = commPiUtils.trim(rcvMsg.getFieldString("CAR_KIND"       ));
			String sCarNo 			= commPiUtils.trim(rcvMsg.getFieldString("CAR_NO"         ));
//			String sCardNo          = commPiUtils.trim(rcvMsg.getFieldString("CARD_NO"        ));
			String sCrFrtomoveGp	= commPiUtils.trim(rcvMsg.getFieldString("CR_FRTOMOVE_GP" )); //1 운송 2 이송
			String sWorkGp 			= commPiUtils.trim(rcvMsg.getFieldString("WORK_GP"        )); // 작업구분
			String sCarudPntCd		= commPiUtils.trim(rcvMsg.getFieldString("CARUD_PNT_CD"   )); // 상차포인트
			String sTelNo           = commPiUtils.trim(rcvMsg.getFieldString("TEL_NO"         )); //전화번호
			String sDriverName      = commPiUtils.trim(rcvMsg.getFieldString("DRIVER_NAME"    )); //운전기사명
			String sUgntBayinYn     = commPiUtils.trim(rcvMsg.getFieldString("UGNT_BAYIN_YN"  )); // 복수상차 마지막 차량에 대한 구분 Y: 1순위
			int iYdEqpWrkSh 		= Integer.parseInt(commPiUtils.nvl(rcvMsg.getFieldString("YD_EQP_WRK_SH"),"0")); // 야드설비작업매수
			
			String sModifier = commPiUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }
			
			rcvMsg.setField("MODIFIER", sModifier);
			
			JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
			
			String[] rVal = new String[1];
			
			/***********************************
			 * 0. 취소
			 ***********************************/
			if ("Y".equals(sCancelYn)) {
			    jrRtn = this.procDmTcCnclPI(rcvMsg);
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
				JDTORecordSet jsPnt = ydPICommDAO.selectJ(rcvMsg, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.getCarLdBayInPointList", logId, mthdNm, "하차포인트 조회");
				if (jsPnt.size() > 0) {
					sCarudPntCd = jsPnt.getRecord(0).getFieldString("YD_CARPNT_CD");
					commPiUtils.printLog(logId, "하차포인트[sCarudPntCd] : "+sCarudPntCd, "SL");
				} else {
					throw new Exception("하차포인트 조회 실패!!");
				}
			}
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			JDTORecord jrParam = commPiUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("STL_APPEAR_GP"  , sStlAppearGp  );
			jrParam.setField("TRANS_ORD_DATE" , sTransOrdDt   );
			jrParam.setField("TRANS_ORD_SEQNO", sTransOrdSeqno);
			jrParam.setField("CARD_NO"        , ""       	  );
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
			commPiUtils.printLog(logId, "1. 차량스케줄 중복 체크" , "SL");
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
			JDTORecordSet rsResult = ydPICommDAO.selectJ(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdCarYdCmbnCarldYn61", logId, mthdNm, "차량정보 존재여부 체크");
			
			if (rsResult.size() > 0) {
				throw new DAOException("TB_YD_CARSCH 차량스케줄이 편성되어 있습니다");
			}			

			   /**********************************************************
				* 2. 저장품 이동 조건 수정
				**********************************************************/
				String sStlNo = "";
				for (int i = 1 ; i <= 20; i++) {
				
					sStlNo = commPiUtils.trim(rcvMsg.getFieldString("STL_NO"+i));
					
					if ("".equals(sStlNo)) {
						break;
					}
					
					jrParam.setField("STL_NO" 		     , sStlNo); //저장품 ID
					jrParam.setField("YD_CAR_UPP_LOC_CD" , commPiUtils.trim(rcvMsg.getFieldString("GDS_CARLD_LOC"+i))); //차상위치
					
					
					commPiUtils.printLog(logId, "저장품 등록", "SL");
	    			coilDao.stockProcCom(logId, mthdNm, sStlNo, 1);
					
//PIDEV_S :병행가동용:PI_YD
					rcvMsg.setField("PI_YD",    	"J");							
					//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
					rVal =  coilDao.getYdAimRtGp("C", rcvMsg);
					
					jrParam.setField("YD_AIM_RT_GP", "A1" );
					jrParam.setField("STL_PROG_CD" , rVal[1] );
					jrParam.setField("DEL_YN"      , "N"     );

					JDTORecordSet jsStock = ydPICommDAO.selectJ(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilDAO.getYdStock", logId, mthdNm, "저장품조회");
					
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
				    	ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStockWaitLocArr", logId, mthdNm, "TB_YD_STOCK 등록");
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
						ydPICommDAO.insert(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.insYdStockDMYDR073", logId, mthdNm, "저장품 생성");
					}
					
					/**********************************************************
					* 저장품 제원 야드L2 전송 (YDY5L002)
					**********************************************************/				
					sndL2Msg = commPiUtils.getParam(logId, mthdNm, sModifier);
					sndL2Msg.setField("JMS_TC_CD"			, "YDY5L002");
					sndL2Msg.setField("YD_INFO_SYNC_CD"		, "5");	//1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)
					sndL2Msg.setField("STL_NO"		        , sStlNo);
//PIDEV_S :병행가동용:PI_YD
					sndL2Msg.setField("PI_YD",    	"J");							
					jrRtn = commPiUtils.addSndData(jrRtn ,coilDao.getMsgL2("YDY5L002", sndL2Msg));
					
				} // end for
				
				
				//작업구분(1:내수/2:수출/3:연안해송/9:냉연이송)
				
				if ("9".equals(sWorkGp)) {
					
					jrParam = commPiUtils.getParam(logId, mthdNm, sModifier);
					jrParam.setField("TRANS_ORD_DT"		, sTransOrdDt   );
					jrParam.setField("TRANS_ORD_SEQNO"	, sTransOrdSeqno);
					jrParam.setField("CAR_NO"			, sCarNo       );
					jrParam.setField("YD_CARPNT_CD"		, sCarudPntCd   );
					
					/* 
					SELECT YD_CAR_SCH_ID 
					  FROM TB_YD_CARSCH
					 WHERE TRANS_ORD_DATE  = :V_TRANS_ORD_DT
					   AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
					   AND CRD_NO         = :V_CAR_NO
					   AND DEL_YN          = 'N'		
					*/	  	   
					JDTORecordSet jsCarSch = ydPICommDAO.selectJ(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdCarYdDEL_YN_PIDEV", logId, mthdNm, "차량스케쥴 조회");
					
					if (jsCarSch.size() > 0) {
						commPiUtils.printLog(logId, mthdNm + " TB_YD_CARSCH[차량스케줄이 편성되어 있습니다.:기존지시 삭제 " , "SL");	
						
						String ydOldCarSchId = commPiUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_SCH_ID")); //차량 작업지시
					 
						jrParam.setField("YD_CAR_SCH_ID", ydOldCarSchId);
						jrParam.setField("DEL_YN"       , "Y");
						
						/*
						UPDATE TB_YD_CARSCH
						   SET MODIFIER  = :V_MODIFIER
						     , MOD_DDTT  = SYSDATE
						     , DEL_YN    = :V_DEL_YN
						 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID   
						*/
						ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.delYdCarsch", logId, mthdNm, "TB_YD_CARSCH 차량 스케줄정보");
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
					JDTORecordSet jsCarPnt = ydPICommDAO.selectJ(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdCarPoint", logId, mthdNm, "차량스케쥴 조회");
					String sWlocCd 	    = "";
					String ydStkColGp   = "";
					String ydPntCd      = "";

					if (jsCarPnt.size() > 0) {

						sWlocCd 	          = commPiUtils.trim(jsCarPnt.getRecord(0).getFieldString("WLOC_CD"));
						ydStkColGp	          = commPiUtils.trim(jsCarPnt.getRecord(0).getFieldString("YD_STK_COL_GP"));
						ydPntCd               = commPiUtils.trim(jsCarPnt.getRecord(0).getFieldString("YD_PNT_CD"));
						String ydBayinWoSeqYn = commPiUtils.nvl (jsCarPnt.getRecord(0).getFieldString("YD_BAYIN_WO_SEQ_YN"), "N");  
						
						JDTORecord jrCarSch = commPiUtils.getParam(logId, mthdNm, sModifier);
						jrCarSch.setField("YD_CAR_SCH_ID"        , ydCarSchId);
						jrCarSch.setField("YD_EQP_WRK_STAT"      , "L");                            //야드설비작업상태(영차) 
						jrCarSch.setField("YD_EQP_ID"            , CConstant.YD_DM_CAR_EQP_ID);     //야드설비ID
						jrCarSch.setField("YD_CAR_USE_GP"        , CConstant.YD_CAR_USE_GP_DM);     //차량사용구분
//						jrCarSch.setField("SPOS_WLOC_CD"         , sWlocCd);                        //발지개소코드
						jrCarSch.setField("ARR_WLOC_CD"          , sWlocCd);						//발지개소코드
						jrCarSch.setField("CAR_NO"               , sCarNo);                         //차량번호 
						jrCarSch.setField("CARD_NO"              , "");    		                    //카드번호
						jrCarSch.setField("YD_CARUD_LEV_DT"      , commPiUtils.getDateTime14());    //하차출발일시
						jrCarSch.setField("TRANS_ORD_DATE"       , sTransOrdDt);                    //운송지시일자
						jrCarSch.setField("TRANS_ORD_SEQNO"      , sTransOrdSeqno);                 //운송지시순번
						jrCarSch.setField("YD_CARUD_STOP_LOC"    , ydStkColGp);                     //야드하차정지위치
						
						jrCarSch.setField("YD_CAR_PROG_STAT"     , "A");                            //상차출발상태
						jrCarSch.setField("YD_CAR_WRK_GP"        , sWorkGp);
						jrCarSch.setField("YD_PNT_CD3"           , ydPntCd);                        //야드포인트코드1
						jrCarSch.setField("TRANS_EQUIPMENT_TYPE" , 	"P");							//운송장비타입 P : PDA

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
						jrCarSch.setField("WAIT_ARR_DDTT"		 , commPiUtils.getDateTime14());	//대기장도착시간
						
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
						ydPICommDAO.insert(jrCarSch, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.insYdCarsch", logId, mthdNm, "TB_YD_CARSCH 등록");

						String sGdsCarlcLoc = "";
						String ydStkBedNo   = "";
						
						for (int i = 1 ; i <= iYdEqpWrkSh; i++) {	

							sGdsCarlcLoc = commPiUtils.trim(rcvMsg.getFieldString("GDS_CARLD_LOC"+i));
							
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
							jrParam = commPiUtils.getParam(logId, mthdNm, sModifier);
							jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
							jrParam.setField("STL_NO"       , commPiUtils.trim(rcvMsg.getFieldString("STL_NO"+i)));
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
							ydPICommDAO.insert(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.insCarSchmtl", logId, mthdNm, "차량재료 스케쥴 INSERT");					 
				    		
						}	// end for
					} else {
						commPiUtils.printLog(logId, mthdNm + "TB_YD_CARPOINT[차량포인트가 존재하지 않습니다]" , "SL");
					}
					
					//입동지시요구모듈 호출(trailer인 경우)
					if ("T".equals(sCarKind) || "TR".equals(sCarKind)) {
						/*
						 * 6. 차량정지위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
						 */
						commPiUtils.printLog(logId, "차량정지위치[" + ydStkColGp + "], 차량스케줄ID[" + ydCarSchId + "] -PDA AB차량입동지시요구 모듈을 호출 시작" , "SL");
						
						JDTORecord jrYdMsg = commPiUtils.getParam(logId, mthdNm, sModifier);	
						jrYdMsg.setField("JMS_TC_CD"			, "YDYDJ553");          //차량입동지시 요구 기존:YDYDJ662
						jrYdMsg.setField("JMS_TC_CREATE_DDTT"	, commPiUtils.getDateTime14()); //JMSTC생성일시
						jrYdMsg.setField("YD_CARPNT_CD"		    , sCarudPntCd);
						jrYdMsg.setField("YD_CAR_SCH_ID"		, ydCarSchId);
						jrYdMsg.setField("CR_FRTOMOVE_GP"       , sCrFrtomoveGp );
						
						EJBConnector ejbConn = new EJBConnector("default", "CCoilCarMvSeEJB", this);
						JDTORecord jrRst = (JDTORecord)ejbConn.trx("procCarBayInOrdReqTr", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
						
						jrRtn = commPiUtils.addSndData(jrRtn, jrRst);
					}				
				}
				
				commPiUtils.printLog(logId, mthdNm, "S-");			
			
			return jrRtn;			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, mthdNm, e));
		}			
	}
	
	/**
	 * [A] 오퍼레이션명 :코일이송하차도착PDA(DMYDR074)
	 * CCoilL3RcvSeEJBBean.java	-> rcvDMYDR074
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public JDTORecord rcvM10LMYDJ1101(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "[YdCoilL3RcvPISeEJB.rcvM10LMYDJ1101 [코일이송하차도착PDA 송신(DMYDR074)]] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		try {
	
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();		
			
			commPiUtils.printLog(logId, mthdNm, "S+");
			commPiUtils.printParam(logId, rcvMsg);			
			
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI0", "J", "*");
//			
//			if ("N".equals(sApplyYnPI)) {
//				commPiUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return jrRtn; 
//			}						
			
			rcvMsg.setField("TC_CREATE_DDTT" , commPiUtils.trim(rcvMsg.getFieldString("MQ_TC_CREATE_DDTT")) ); // 전송일시
			rcvMsg.setField("TRANS_ORD_DT"   , commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE")) );      // 운송의뢰일자
			rcvMsg.setField("TRANS_ORD_SEQNO", commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ")) );       // 운송의뢰순번
			
			//수신 항목 값
			String msgId    		= commPiUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydGp             = commPiUtils.trim(rcvMsg.getFieldString("YD_GP"          )); //야드구분
			String sTransOrdDt  	= commPiUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"   )); //운송실적일자
			String sTransOrdSeqno	= commPiUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO")); //운송실적순번
			String sCarKind		    = commPiUtils.trim(rcvMsg.getFieldString("CAR_KIND"       ));
			String sCarNo 			= commPiUtils.trim(rcvMsg.getFieldString("CAR_NO"         ));
//			String sCardNo          = commPiUtils.trim(rcvMsg.getFieldString("CARD_NO"        ));
			String sCrFrtomoveGp	= commPiUtils.trim(rcvMsg.getFieldString("CR_FRTOMOVE_GP" )); //1 운송 2 이송
			String sWorkGp 			= commPiUtils.trim(rcvMsg.getFieldString("WORK_GP"        )); // 작업구분
			String sCarudPntCd		= commPiUtils.trim(rcvMsg.getFieldString("CARUD_PNT_CD"   )); // 상차포인트		
			
			String sModifier = commPiUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }
			
			rcvMsg.setField("MODIFIER", sModifier);
				
			/**********************************************************
			* 0. 수신 항목 값 Check
			**********************************************************/
			JDTORecord jrParam = commPiUtils.getParam(logId, mthdNm, sModifier);
			jrParam.setField("TRANS_ORD_DATE" , sTransOrdDt   );
			jrParam.setField("TRANS_ORD_SEQNO", sTransOrdSeqno);
			jrParam.setField("CARD_NO"        , ""       );
			jrParam.setField("CAR_NO"         , sCarNo        ); 						
			jrParam.setField("YD_AIM_RT_GP"   , "A1"          );
			jrParam.setField("STL_PROG_CD"    , "E"           );
			
			/***********************************
        	 * 1. 차량스케줄 중복 Check
        	 ***********************************/
			commPiUtils.printLog(logId, "1. 차량스케줄 중복 체크" , "SL");
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
			JDTORecordSet rsResult = ydPICommDAO.selectJ(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdCarYdCmbnCarldYn_PIDEV", logId, mthdNm, "차량정보 존재여부 체크");
						
			if (rsResult.size() > 0) {
				
				/*
				 * 도착전문에 중복 수신되면 Exception 처리함
				 */
				commPiUtils.printLog(logId, "이미 도착처리된 차량입니다." , "SL");
				
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
			JDTORecordSet jsCarPoint = ydPICommDAO.selectJ(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdCarPoint", logId, mthdNm, "차량스케줄 조회");
			
			if (jsCarPoint.size() != 1) {
				throw new Exception("차량포인트["+sCarudPntCd+"] 조회 실패");
			}
			
			String sCarNoOld  = commPiUtils.trim(jsCarPoint.getRecord(0).getFieldString("CAR_NO" )); //카번호
//			String sCardNoOld = commPiUtils.trim(jsCarPoint.getRecord(0).getFieldString("CARD_NO")); //카드번호

			if (!"".equals(sCarNoOld)) {
				if (!sCarNo.equals(sCarNoOld)) {
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
			ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStock2", logId, mthdNm, "저장품 수정");
			
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
			JDTORecordSet jsStock = ydPICommDAO.selectJ(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdStockTRANS_ORD_DAT", logId, mthdNm, "(운송일자, 운송순번)로 저장품 조회");

		    if (jsStock.size() <= 0) {
		    	commPiUtils.printLog(logId, "YD_STOCK[저장품] 조회 실패", "S-");
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
				JDTORecordSet jsCarpnt = ydPICommDAO.selectJ(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdCarPoint", logId, mthdNm, "개소코드 조회");
				if (jsCarpnt.size() > 0) {
					String sWlocCd         = commPiUtils.nvl(jsCarpnt.getRecord(0).getFieldString("WLOC_CD"            ), "");
					String ydPntCd         = commPiUtils.nvl(jsCarpnt.getRecord(0).getFieldString("YD_PNT_CD"          ), "");	
					String ydStkColActStat = commPiUtils.nvl(jsCarpnt.getRecord(0).getFieldString("YD_STK_COL_ACT_STAT"), "");
					String ydStkColGp      = commPiUtils.nvl(jsCarpnt.getRecord(0).getFieldString("YD_STK_COL_GP"      ), "");
					String ydBayGp         = commPiUtils.nvl(jsCarpnt.getRecord(0).getFieldString("YD_BAY_GP"          ), "");
					
					commPiUtils.printLog(logId, "sWlocCd         : " + sWlocCd, "SL");
					commPiUtils.printLog(logId, "ydPntCd         : " + ydPntCd, "SL");
					commPiUtils.printLog(logId, "ydStkColActStat : " + ydStkColActStat, "SL");
					commPiUtils.printLog(logId, "ydStkColGp      : " + ydStkColGp, "SL");
					
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
					ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStacklayer", logId, mthdNm, "차상위치 저장위치 등록");
					
					//차량스케줄에 차량진행상태 수정
					jrParam.setField("YD_CAR_PROG_STAT",     "B");
					jrParam.setField("YD_CARUD_ARR_DT", commPiUtils.getDateTime14()); //하차도착
					
					/*
					UPDATE TB_YD_CARSCH
					   SET MODIFIER         = :V_MODIFIER
					     , MOD_DDTT         = SYSDATE
					     , YD_CAR_PROG_STAT = :V_YD_CAR_PROG_STAT
					     , YD_CARUD_ST_DT   = TO_DATE(:V_YD_CARUD_ST_DT, 'YYYYMMDDHH24MISS')
					 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
					 */
					int nCnt = ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdCarschProgStat", logId, mthdNm, "차량진행상태 수정");
					
					if (nCnt <= 0) {
				    	commPiUtils.printLog(logId, "차량스케줄에 차량진행상태 수정 UPDATE Error", "S-");
						return jrRtn;
				    }
					
					//TR가 도착해 있는 경우 
					if ("TR".equals(sCarKind) && "L".equals(ydStkColActStat)) {
						ydStkColActStat = "C";
					}
					
					if ("C".equals(ydStkColActStat)) {

						JDTORecord jrYdMsg = commPiUtils.getParam(logId, mthdNm, sModifier);
						jrYdMsg.setField("TC_CODE"          , "YDYDJ653"    ); 
						//TODO CarMvHdSeEJB - procCoilGdsDistCarArrWr   -> procOutCarArrWr  코일제품출하차량도착실적처리(DMYDR036)
						jrYdMsg.setField("TC_CREATE_DDTT"	, commPiUtils.getDateTime14());
						jrYdMsg.setField("YD_GP"			, ydGp          );
						jrYdMsg.setField("TRANS_ORD_DATE"	, sTransOrdDt   );    
						jrYdMsg.setField("TRANS_ORD_SEQNO" 	, sTransOrdSeqno);
						jrYdMsg.setField("CAR_NO"			, sCarNo        );
						jrYdMsg.setField("CARD_NO"			, ""       );
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
						
						String sRtnCd = commPiUtils.trim(jrRst.getFieldString("RTN_CD" ));
						
						if (!"1".equals(sRtnCd)) {
							return jrRtn;
						}
						
						//2열연코일출하하차LOT편성전문
						//YDYDJ282 호출 CoilIssueWrkDmdSeEJB - procCoilGdsDistCarLdComp
//						EJBConnector ejbConn = new EJBConnector("default", "CCoilCarMvSeEJB", this);
						jrYdMsg = (JDTORecord)ejbConn.trx("procCoilGdsDistCarLdComp", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
						jrRtn = commPiUtils.addSndData(jrRtn, jrYdMsg);
						
					}
				}
		    	
		    } //if ("9".equals(sWorkGp)) {		
		    
			commPiUtils.printLog(logId, mthdNm, "S-");			
			
			return jrRtn;			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, mthdNm, e));
		}			
	}
	
	
	/**
	 * [A] 오퍼레이션명 :코일이송하차완료PDA(DMYDR075)
	 * CCoilL3RcvSeEJBBean.java	-> rcvDMYDR075
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public JDTORecord rcvM10LMYDJ1111(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "[YdCoilL3RcvPISeEJB.rcvM10LMYDJ1111 [코일이송하차완료PDA(DMYDR075)]] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		try {
	
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			
			commPiUtils.printLog(logId, mthdNm, "S+");
			commPiUtils.printParam(logId, rcvMsg);			
			
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI0", "J", "*");
//			
//			if ("N".equals(sApplyYnPI)) {
//				commPiUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return jrRtn; 
//			}					
			
			rcvMsg.setField("TC_CREATE_DDTT" , commPiUtils.trim(rcvMsg.getFieldString("MQ_TC_CREATE_DDTT")) ); // 전송일시
			rcvMsg.setField("TRANS_ORD_DT"   , commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE")) );      // 운송의뢰일자
			rcvMsg.setField("TRANS_ORD_SEQNO", commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ")) );       // 운송의뢰순번
				
			//수신 항목 값
			String msgId        = commPiUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydGp		    = commPiUtils.trim(rcvMsg.getFieldString("YD_GP"));
			String sStlAppearGp = commPiUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP")); //재료외형
			String sStlNo       = commPiUtils.trim(rcvMsg.getFieldString("STL_NO"       )); //재료번호
			
			String sModifier = commPiUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			if ("".equals(sModifier)) { sModifier = msgId.substring(3,12); }
			
			rcvMsg.setField("MODIFIER", sModifier);
			
			String sMsg	= "";
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			JDTORecord jrParam = commPiUtils.getParam(logId, mthdNm, sModifier);
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
			JDTORecordSet jsStock = ydPICommDAO.selectJ(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdStockJoinStkLyr2_PIDEV", logId, mthdNm, "차량정정보검색");
			
			if (jsStock.size() <= 0) {
				commPiUtils.printLog(logId, "YD_STOCK[코일제품출하완료] 조회 Error", "SL");	
				return jrRtn ;
			}

			String sCarNo    	  = commPiUtils.trim(jsStock.getRecord(0).getFieldString("CAR_NO"));
			String ydStkColGp 	  = commPiUtils.trim(jsStock.getRecord(0).getFieldString("YD_STK_COL_GP"));
//			String sCardNo        = commPiUtils.trim(jsStock.getRecord(0).getFieldString("CARD_NO"));
			String sCardNo        = "";
			String sTransOrdDate  = commPiUtils.trim(jsStock.getRecord(0).getFieldString("TRANS_ORD_DATE"));
			String sTransOrdSeqno = commPiUtils.trim(jsStock.getRecord(0).getFieldString("TRANS_ORD_SEQNO"));

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
			ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updYdStockCarldCmpl", logId, mthdNm, "저장품 수정");
			
			/***************************************************************************
			 *  저장품이 적치된 저장위치 정보를 조회
			 ***************************************************************************/
			sMsg = "[" + mthdNm + "]카드번호[], 차량번호["+ sCarNo+"], 운송지시일자["+sTransOrdDate+"], 운송지시순번["+sTransOrdSeqno+"] : 출하완료된 동["+ydStkColGp+"]의 저장품들 조회 시작";
			commPiUtils.printLog(logId, sMsg, "SL");
			
			if ("*".equals(sStlAppearGp)) {
				
				jrParam.setField("CAR_NO" 			, sCarNo);
//				jrParam.setField("CARD_NO"			, sCardNo);
				jrParam.setField("TRANS_ORD_DATE"	, sTransOrdDate);
				jrParam.setField("TRANS_ORD_SEQNO"	, sTransOrdSeqno);
				
				/*
				SELECT *
				  FROM (
				        SELECT *
				          FROM TB_YD_CARSCH
				         WHERE CAR_NO       LIKE :V_CAR_NO||'%'
				           AND TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
				           AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
				         ORDER BY YD_CAR_SCH_ID DESC
				       ) A
				 WHERE ROWNUM <= 1     
		    	*/                                                     				
				JDTORecordSet jsCarsch = ydPICommDAO.selectJ(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.getYdCarschTransDTSeq2_PIDEV", logId, mthdNm, "차량정정보검색");
				
				if (jsCarsch.size() <= 0 ) {
					sMsg="["+mthdNm+"] 차량스케쥴 조회 SELECT Error ::  DO NOT EXIST"  ;
					commPiUtils.printLog(logId, sMsg, "S-");	
					return jrRtn ;
				}
				
				JDTORecord sndL3Msg = commPiUtils.getParam(logId, mthdNm, sModifier);
				sndL3Msg.setField("TC_CODE"         , "DMYDR040");	//전문코드
				sndL3Msg.setField("CARD_NO"         , commPiUtils.trim(jsCarsch.getRecord(0).getFieldString("CARD_NO"     )));
				sndL3Msg.setField("CAR_NO"          , commPiUtils.trim(jsCarsch.getRecord(0).getFieldString("CAR_NO"      )));
				sndL3Msg.setField("SPOS_WLOC_CD"    , commPiUtils.trim(jsCarsch.getRecord(0).getFieldString("ARR_WLOC_CD" )));
				sndL3Msg.setField("SPOS_YD_PNT_CD"  , commPiUtils.trim(jsCarsch.getRecord(0).getFieldString("YD_PNT_CD3"  )));
				sndL3Msg.setField("YD_GP"           , ydGp          );
				sndL3Msg.setField("TRANS_ORD_DT"    , commPiUtils.trim(jsCarsch.getRecord(0).getFieldString("TRANS_ORD_DATE" )));
				sndL3Msg.setField("TRANS_ORD_SEQNO" , commPiUtils.trim(jsCarsch.getRecord(0).getFieldString("TRANS_ORD_SEQNO")));
//				if ("".equals(sCardNo)) {
//					sCardNo = "XXXXX";
//				}
				sndL3Msg.setField("MSG_ID"			, msgId    );
				sMsg = "차량번호[" + sCarNo + "]는 자동차량출발";
				commPiUtils.printLog(logId, sMsg, "SL");
				
				EJBConnector ejbConn = new EJBConnector("default", "CCoilCarMvSeEJB", this); //TODO 호출로직 점검필요
				JDTORecord jrRtn1 = (JDTORecord)ejbConn.trx("procOutCarLevWr", new Class[] { JDTORecord.class }, new Object[] { sndL3Msg });
				jrRtn = commPiUtils.addSndData(jrRtn, jrRtn1);
				
			} //if ("*".equals(sStlAppearGp)) {
			
			/**********************************************************
			* 2. 저장품 제원 야드L2 전송 (YDY5L002)
			**********************************************************/				
			JDTORecord sndL2Msg = commPiUtils.getParam(logId, mthdNm, sModifier);
			sndL2Msg.setField("JMS_TC_CD"			, "YDY5L002");
			sndL2Msg.setField("YD_INFO_SYNC_CD"		, "5");	//1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)
			sndL2Msg.setField("STL_NO"		        , sStlNo);

//PIDEV_S :병행가동용:PI_YD
			sndL2Msg.setField("PI_YD",    	"J");					
			jrRtn = commPiUtils.addSndData(jrRtn ,coilDao.getMsgL2("YDY5L002", sndL2Msg));
			
			commPiUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;		
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, mthdNm, e));
		}				
			
	}
	
	/**
	 * 대기장도착실적(DMYDR061)_
	 * CCoilL3RcvSeEJBBean.java	-> rcvDMYDR061
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord procM10LMYDJ_DMYDR061(JDTORecord rcvMsg) throws DAOException { //S
		String mthdNm = "대기장도착실적 수신[YdCoilL3RcvPISeEJBBean.procM10LMYDJ_DMYDR061] < " + rcvMsg.getResultMsg();
		String logId  = rcvMsg.getResultCode();
		
		try {
			commPiUtils.printLog(logId, mthdNm, "S+");
			commPiUtils.printParam(logId, rcvMsg);
			
			//수신 항목 값
			String msgId                = commPiUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			
			String ydGp                 = commPiUtils.trim(rcvMsg.getFieldString("YD_GP"            )); //야드구분	
			String sCmbnCarldYn         = commPiUtils.nvl (rcvMsg.getFieldString("CMBN_CARLD_YN"),"N"); //조합상차유무(시작:S, 종료: E, 단일상차: N)
			String sWorkGp              = commPiUtils.trim(rcvMsg.getFieldString("WORK_GP"          )); //작업구분
			String sTelNo               = commPiUtils.trim(rcvMsg.getFieldString("TEL_NO"           )); //전화번호
			String sTransOrdDt          = commPiUtils.trim(rcvMsg.getFieldString("TRANS_ORD_DT"     )); //운송지시일자
			String sTransOrdSeqno       = commPiUtils.trim(rcvMsg.getFieldString("TRANS_ORD_SEQNO"  )); //운송지시순번
			String sCarNo               = commPiUtils.trim(rcvMsg.getFieldString("CAR_NO"           )); //차량번호
			// String sCardNo              = commPiUtils.trim(rcvMsg.getFieldString("CARD_NO"          )); //카드번호
			String sCardNo				= "";
			String sCarKind             = commPiUtils.trim(rcvMsg.getFieldString("CAR_KIND"         )); //차량종류
			String sWaitArrDdtt         = commPiUtils.trim(rcvMsg.getFieldString("WAIT_ARR_DDTT"    )); //대기장도착시간
			String sWaitArrGp           = commPiUtils.trim(rcvMsg.getFieldString("WAIT_ARR_GP"      )); //대기장도착구분
			
			String sTransFrtomoveGp     = commPiUtils.trim(rcvMsg.getFieldString("TRANS_FRTOMOVE_GP")); //1 운송 2 이송
			String sDriverName          = commPiUtils.trim(rcvMsg.getFieldString("DRIVER_NAME"      )); //운전기사명
			
			String sModifier            = commPiUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
			int iYdEqpWrkSh             = Integer.parseInt(commPiUtils.nvl(rcvMsg.getFieldString("YD_EQP_WRK_SH"),"0")); //야드설비작업매수
			String sDoubleYardYn        = "N"; //복수창고 여부
			
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

			/***********************************
        	 * 1. 차량스케줄 중복 Check
        	 ***********************************/
			commPiUtils.printLog(logId, "1. 차량스케줄 중복 체크" , "SL");
			JDTORecord jrParam  = commPiUtils.getParam(logId, mthdNm, sModifier);
			JDTORecord jrParam1 = commPiUtils.getParam(logId, mthdNm, sModifier);
			
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
			JDTORecordSet rsResult = ydPICommDAO.selectJ(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdCarYdCmbnCarldYn61", logId, mthdNm, "차량정보 존재여부 체크");
			
			if (rsResult.size() > 0) {
				
				/***********************************
	        	 * 1.1. 기존 차량스케줄 삭제
	        	 ***********************************/
				/*
				 * 도착전문에 중복 수신되면 Exception 처리함
				 */
				commPiUtils.printLog(logId, "TB_YD_CARSCH[차량스케줄이 편성되어 있습니다." , "SL");
				
				throw new DAOException("TB_YD_CARSCH[차량스케줄이 편성되어 있습니다");
				
			}
			
			/***********************************
        	 * 2. 도착가능 포인트 조회
        	 ***********************************/
			commPiUtils.printLog(logId, "2. 도착가능 포인트 조회" , "SL");
			jrParam.setField("YD_GP"          , ydGp);
			jrParam.setField("TRANS_ORD_DT"   , sTransOrdDt);
			jrParam.setField("TRANS_ORD_SEQNO", sTransOrdSeqno);
			/* com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdCarPointSelect
			 * V_YD_GP
			 * V_TRANS_ORD_DT
			 * V_TRANS_ORD_SEQNO
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
			rsResult = ydPICommDAO.selectJ(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdCarPointSelect", logId, mthdNm, "도착가능 차량포인트 조회");
			if (rsResult.size() <= 0 ) {
				throw new Exception("TB_YD_CARSCH[차량도착 가능한 포인트가 없거나 해당 운송상차지시,저장위치가 없습니다.");
			} 
			
			//도착가능 포인트 조회 결과 값
			String ydStkColGp   = commPiUtils.trim(rsResult.getRecord(0).getFieldString("YD_STK_COL_GP")); //야드적치열
			String ydCarpntCd   = commPiUtils.trim(rsResult.getRecord(0).getFieldString("YD_CARPNT_CD" )); //차량보인트
			String ydPntCd      = commPiUtils.trim(rsResult.getRecord(0).getFieldString("YD_PNT_CD"    )); //야드포인트코드
			String sSposWlocCd  = commPiUtils.trim(rsResult.getRecord(0).getFieldString("WLOC_CD"      )); //개소코드
			
			/***********************************
        	 * 3. 차량스케줄 생성
        	 ***********************************/
			commPiUtils.printLog(logId, "3. 차량스케줄 생성" , "SL");
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
			jrParam.setField("CARD_NO"				, sCardNo                   ); //카드번호
			jrParam.setField("YD_CARLD_LEV_DT"		, commPiUtils.getDateTime14() ); //상차출발일시
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
			ydPICommDAO.insert(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.insYdCarsch", logId, mthdNm, "TB_YD_CARSCH 등록");
			
			
			String sStlNo       = "";
			String sGdsCarldLoc = "";
			String sWorkState   = "";
			String ydStkBedNo   = "01";
			
			//복수 창고 인 경우 :마지막에 E로 오기로 함
			if ("E".equals(sCmbnCarldYn)) {
				
				commPiUtils.printLog(logId, "복수 창고 여부 check " , "SL");
				for (int ii = 1 ; ii <= iYdEqpWrkSh; ii++) {
					sStlNo 		 = commPiUtils.trim(rcvMsg.getFieldString("STL_NO"+ii)); //재료번호
					sWorkState   = commPiUtils.trim(rcvMsg.getFieldString("WORK_STATE"+ii));    //0: 미상차 , 1: 상차
					//재료가 없으면 종료
					if ("".equals(sStlNo)) {
						break;
					}
					
					// 복수창고 인 경우 상차된 상태
					if ("1".equals(sWorkState)) {    // 상차된 코일이 있는 경우
						//복수 창고 여부
						sDoubleYardYn = "Y";
					}
				}
				commPiUtils.printLog(logId, "복수 창고: "+ sDoubleYardYn , "SL");
				// 복수 창고 임
				if ("E".equals(sCmbnCarldYn) && "Y".equals(sDoubleYardYn)) {
/**********************************************
 *   복수창고  로직
 **********************************************/					
					commPiUtils.printLog(logId, "복수 창고인 경우 " , "SL");
					for (int ii = 1 ; ii <= iYdEqpWrkSh; ii++) {
						ydGp         = commPiUtils.trim(rcvMsg.getFieldString("YD_GP"+ii)); 
						sStlNo 		 = commPiUtils.trim(rcvMsg.getFieldString("STL_NO"+ii)); //재료번호
						sGdsCarldLoc = commPiUtils.trim(rcvMsg.getFieldString("GDS_CARLD_LOC"+ii)); //상차위치
						sWorkState   = commPiUtils.trim(rcvMsg.getFieldString("WORK_STATE"+ii));    //0: 미상차 , 1: 상차
						
						//재료가 없으면 종료
						if ("".equals(sStlNo)) {
							break;
						}
						
						if(sGdsCarldLoc.length() == 2 ) {  //일반출하시 안들어옴 . 이송일 경우 B1,B2...
							if ("A".equals(sGdsCarldLoc.substring(0, 1))) {
								
								ydStkBedNo = "0" + sGdsCarldLoc.substring(1, 2);
								
							} else if ("B".equals(sGdsCarldLoc.substring(0, 1))) {
								
								if ("1".equals(sGdsCarldLoc.substring(1, 2))) {
									ydStkBedNo = "06";
								} else if ("2".equals(sGdsCarldLoc.substring(1, 2))) {
									ydStkBedNo = "07";
								} else if ("3".equals(sGdsCarldLoc.substring(1, 2))) {
									ydStkBedNo = "08";
								} else if ("4".equals(sGdsCarldLoc.substring(1, 2))) {
									ydStkBedNo = "09";
								} else if ("5".equals(sGdsCarldLoc.substring(1, 2))) {
									ydStkBedNo = "10";
								}
								
							} else if ("C".equals(sGdsCarldLoc.substring(0, 1))) {
								
								if ("1".equals(sGdsCarldLoc.substring(1, 2))) {
									ydStkBedNo = "11";
								} else if ("2".equals(sGdsCarldLoc.substring(1, 2))) {
									ydStkBedNo = "12";
								} else if ("3".equals(sGdsCarldLoc.substring(1, 2))) {
									ydStkBedNo = "13";
								} else if ("4".equals(sGdsCarldLoc.substring(1, 2))) {
									ydStkBedNo = "14";
								} else if ("5".equals(sGdsCarldLoc.substring(1, 2))) {
									ydStkBedNo = "15";
								}
							}
						} else{
							ydStkBedNo = "0" + ii;
						}
						
						// 복수창고 인 경우 상차된 상태
						if ("1".equals(sWorkState)) {    // 상차된 상태
							
							/*************************
							 * 이송재료 정보 등록 
							 *************************/
							jrParam1 = commPiUtils.getParam(logId, mthdNm, sModifier);
							jrParam1.setField("YD_CAR_SCH_ID", ydCarSchId   );
							jrParam1.setField("STL_NO"       , sStlNo       );
							jrParam1.setField("YD_STK_BED_NO", ydStkBedNo ); //야드 차상위치코드
							jrParam1.setField("YD_STK_LYR_NO", "001"        );
							jrParam1.setField("DEL_YN"       , "Y"          );
							/* com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.insCarSchmtl
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
							ydPICommDAO.insert(jrParam1, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.insCarSchmtl", logId, mthdNm, "차량재료 스케쥴 INSERT");
							
						} else {
							// 현재 야드  저장품 table 상차 위치 수정 : 차량예정 정보를 위해서
							jrParam1 = commPiUtils.getParam(logId, mthdNm, sModifier);
							jrParam1.setField("YD_CAR_UPP_LOC_CD", ydStkBedNo   );
							jrParam1.setField("STL_NO"           , sStlNo       );

							/* com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdStockYdCarUppLocCd_PIDEV  --  저장품 table 상차 위치 수정
							UPDATE TB_YD_STOCK
							   SET YD_CAR_UPP_LOC_CD  = :V_YD_CAR_UPP_LOC_CD
							     , MODIFIER = :V_MODIFIER
							     , MOD_DDTT = SYSDATE
							 WHERE STL_NO   = :V_STL_NO
							*/
							ydPICommDAO.update(jrParam1, "com.inisteel.cim.yd.ccoil.dao.CCoilJspSeEJB.updYdStockYdCarUppLocCd_PIDEV", logId, mthdNm, "TB_YD_STOCK 삭제");
						}	
					}
				}
				
			}	
			/***********************************
        	 * 4. 복수동 마지막 도착시 상차 정보 insert
        	 ***********************************/
			
			if ("E".equals(sCmbnCarldYn) && "N".equals(sDoubleYardYn)) {
/**********************************************
 *   복수동 로직
 **********************************************/					
				commPiUtils.printLog(logId, "4. 복수동 마지막 도착시 재료 insert" , "SL");
				
				/* com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updCarFtMvMtlCmbnCarldYn_PIDEV 
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
				                                         AND CAR_NO         = :V_CAR_NO
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
				           AND A.CAR_NO          = :V_CAR_NO
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
				ydPICommDAO.insert(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updCarFtMvMtlCmbnCarldYn_PIDEV", logId, mthdNm, "이송재료 등록");
			}
			
			/***********************************
        	 * 5. 입동지시 호출 YDYDJ553
        	 ***********************************/
			commPiUtils.printLog(logId, "4. 입동지시 호출" , "SL");
			
			if (!"".equals(ydCarpntCd)) {
				//도착가능 포인트가 있으면 입동지시 호출
				commPiUtils.printLog(logId, mthdNm + " 차량입동포인트["+ydCarpntCd+"], 차량스케줄ID["+ydCarSchId+"] - 차량입동지시요구 모듈을 호출 " , "SL");
			
				JDTORecord jrYdMsg = commPiUtils.getParam(logId, mthdNm, sModifier);
				jrYdMsg.setField("JMS_TC_CD"		  , "YDYDJ553"); //차량입동지시 요구 기존 YDYDJ662
				jrYdMsg.setField("JMS_TC_CREATE_DDTT" , commPiUtils.getDateTime14()); //JMSTC생성일시
				jrYdMsg.setField("YD_CARPNT_CD"		  , ydCarpntCd); //입동포인트
				jrYdMsg.setField("YD_CAR_SCH_ID"	  , ydCarSchId);	//차량스케줄ID
				jrYdMsg.setField("CARD_NO"			  , sCardNo   );
				jrYdMsg.setField("CAR_NO"			  , sCarNo    );
				jrYdMsg.setField("CAR_KIND"			  , sCarKind  ); //차량종류
				jrYdMsg.setField("TRANS_FRTOMOVE_GP"  , sTransFrtomoveGp); //1 운송 2 이송
				
				// 20220512 현재 'N' 되어 있음
				String sAPP813_YN = coilDao.ApplyYn(logId, mthdNm, "APP813", "J", "*");
				if ("Y".equals(sAPP813_YN)) {
					jrRtn = commPiUtils.addSndData(jrRtn, jrYdMsg);
				} else {
					EJBConnector ejbConn = new EJBConnector("default", "CCoilCarMvSeEJB", this);
					JDTORecord jrRst = (JDTORecord)ejbConn.trx("rcvYDYDJ553", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
					
					jrRtn = commPiUtils.addSndData(jrRtn, jrRst);	
				}
			}
			
			commPiUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * 코일이송상차대기장도착PDA(DMYDR070)_
	 * CCoilL3RcvSeEJBBean.java	-> rcvDMYDR070
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord procM10LMYDJ_DMYDR070(JDTORecord rcvMsg) throws DAOException { //S
		String mthdNm = "코일이송상차대기장도착PDA 수신[YdCoilL3RcvPISeEJBBean.procM10LMYDJ_DMYDR070] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commPiUtils.printLog(logId, mthdNm, "S+");
			commPiUtils.printParam(logId, rcvMsg);

			//수신 항목 값
			String msgId            = commPiUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydGp             = commPiUtils.trim(rcvMsg.getFieldString("YD_GP"          )); //야드구분
			String sStlAppearGp 	= commPiUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"  )); //재료외형
			String sTransOrdDt  	= commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE"   ));
			String sTransOrdSeqno	= commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ"    ));
			String sCancelYn     	= commPiUtils.trim(rcvMsg.getFieldString("CANCEL_YN"      ));
			String sCarKind		    = commPiUtils.trim(rcvMsg.getFieldString("CAR_KIND"       ));
			String sCarNo 			= commPiUtils.trim(rcvMsg.getFieldString("CAR_NO"         ));
// 			String sCardNo          = commPiUtils.trim(rcvMsg.getFieldString("CARD_NO"        ));
			String sCardNo          = "";
			String sCrFrtomoveGp	= commPiUtils.trim(rcvMsg.getFieldString("CR_FRTOMOVE_GP" )); //11수출사내 41열연제품이송 63임가공 81열연소재이송
			String sWorkGp 			= commPiUtils.trim(rcvMsg.getFieldString("WORK_GP"        )); // 작업구분
			String sCarldPntCd		= commPiUtils.trim(rcvMsg.getFieldString("CARLD_PNT_CD"   )); // 상차포인트
			String sTelNo           = commPiUtils.trim(rcvMsg.getFieldString("TEL_NO"         )); // 전화번호
			String sDriverName      = commPiUtils.trim(rcvMsg.getFieldString("DRIVER_NAME"    )); // 운전기사명
			String sUgntBayinYn     = commPiUtils.trim(rcvMsg.getFieldString("UGNT_BAYIN_YN"  )); // 복수상차 마지막 차량에 대한 구분 Y: 1순위
			String sCarRemodelYn    = commPiUtils.trim(rcvMsg.getFieldString("CAR_REMODEL_YN" )); //차량개조여부 y,n
			String sCmbnCarldYn     = commPiUtils.trim(rcvMsg.getFieldString("CMBN_CARLD_YN"  )); //조합상차유무
			
			int iYdEqpWrkSh 		= Integer.parseInt(commPiUtils.nvl(rcvMsg.getFieldString("YD_EQP_WRK_SH"),"0"));
			String sModifier 		= commPiUtils.trim(rcvMsg.getFieldString("MODIFIER"));//수정자(Backup Only)
//복수동적용			
			String ydSndYn          = commPiUtils.nvl(rcvMsg.getFieldString("YD_SND_YN"  ),"N"); //복수동시 필요
			
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			
			String[] rVal = new String[1];
			
			/***********************************
			 * 0. 취소
			 ***********************************/
			if ("Y".equals(sCancelYn)) {
			    jrRtn = this.procDmTcCnclPI(rcvMsg);
			    return jrRtn;
			}
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			JDTORecord jrParam = commPiUtils.getParam(logId, mthdNm, sModifier);
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
			commPiUtils.printLog(logId, "1. 차량스케줄 중복 체크" , "SL");
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
			JDTORecordSet rsResult = ydPICommDAO.selectJ(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdCarYdCmbnCarldYn61", logId, mthdNm, "차량정보 존재여부 체크");
			
			if (rsResult.size() > 0) {
				throw new DAOException("TB_YD_CARSCH 차량스케줄이 편성되어 있습니다");
			}
						
		   /**********************************************************
			* 2. 저장품 이동 조건 수정
			**********************************************************/
			int iCnt = 0;
			String sStlNo = "";
			String sGdsCarldLoc = "";
			String sWorkState   = "";
//			String ydStkBedNo   = "01";
			for (int i = 1 ; i <= 20; i++) {
			
				sStlNo = commPiUtils.trim(rcvMsg.getFieldString("STL_NO"+i));
				
				if ("".equals(sStlNo)) {
					break;
				}
				
				jrParam.setField("STL_NO" 		     , sStlNo); //저장품 ID
				jrParam.setField("YD_CAR_UPP_LOC_CD" , commPiUtils.trim(rcvMsg.getFieldString("GDS_CARLD_LOC"+i))); //차상위치
		
				//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
//PIDEV_S :병행가동용:PI_YD
				rcvMsg.setField("PI_YD",    	"J");		
				rVal = coilDao.getYdAimRtGp("C", rcvMsg);
				
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
				ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updWbookCancel", logId, mthdNm, "작업예약 삭제");
				
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
				ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updWbookMtlCancel", logId, mthdNm, "작업예약제료 삭제");
//복수동적용 재 송신시 필요 없음 
				if("N".equals(ydSndYn)) {			
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
					iCnt = ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL2RcvSeEJB.updYdStockWaitLocArr", logId, mthdNm, "TB_YD_STOCK 수정");
					
					if (iCnt <= 0) {
						commPiUtils.printLog(logId, "YD_STOCK[코일이송상차대기장도착PDA] UPDATE Error", "S-");
						return jrRtn; 
					}
				}	
			}	
			
			//작업구분(1:내수/2:수출/3:연안해송/9:HYSCO스케줄)
			if ("9".equals(sWorkGp)) {
				//차량정보 존재여부 체크
				jrParam = commPiUtils.getParam(logId, mthdNm, sModifier);
				jrParam.setField("TRANS_ORD_DT"		, sTransOrdDt   );
				jrParam.setField("TRANS_ORD_SEQNO"	, sTransOrdSeqno);
				jrParam.setField("CAR_NO"			, sCarNo       );
				
				/* 
				SELECT YD_CAR_SCH_ID 
				  FROM TB_YD_CARSCH
				 WHERE TRANS_ORD_DATE  = :V_TRANS_ORD_DT
				   AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
				   AND CARD_NO         = :V_CARD_NO
				   AND DEL_YN          = 'N'		
				*/	   
				JDTORecordSet jsCarSch = ydPICommDAO.selectJ(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdCarYdDEL_YN_PIDEV", logId, mthdNm, "차량스케쥴 조회");
				
				if (jsCarSch.size() > 0) {
					commPiUtils.printLog(logId, mthdNm + " TB_YD_CARSCH[차량스케줄이 편성되어 있습니다.:기존지시 삭제 " , "SL");	
					
					String ydOldCarSchId = commPiUtils.trim(jsCarSch.getRecord(0).getFieldString("YD_CAR_SCH_ID")); //차량 작업지시
				 
					jrParam.setField("YD_CAR_SCH_ID", ydOldCarSchId);
					jrParam.setField("DEL_YN"       , "Y");
					
					/*
					UPDATE TB_YD_CARSCH
					   SET MODIFIER  = :V_MODIFIER
					     , MOD_DDTT  = SYSDATE
					     , DEL_YN    = :V_DEL_YN
					 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID   
					*/
					ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.delYdCarsch", logId, mthdNm, "TB_YD_CARSCH 차량 스케줄정보");
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
				JDTORecordSet jsCarPnt = ydPICommDAO.selectJ(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.getYdCarPoint", logId, mthdNm, "차량스케쥴 조회");
				String sWlocCd 	= "";
				String ydStkColGp   = "";
				String ydPntCd      = "";

				if (jsCarPnt.size() > 0) {

					sWlocCd 	= commPiUtils.trim(jsCarPnt.getRecord(0).getFieldString("WLOC_CD"));
					ydStkColGp	= commPiUtils.trim(jsCarPnt.getRecord(0).getFieldString("YD_STK_COL_GP"));
					ydPntCd     = commPiUtils.trim(jsCarPnt.getRecord(0).getFieldString("YD_PNT_CD"));
					String ydBayinWoSeqYn = commPiUtils.nvl (jsCarPnt.getRecord(0).getFieldString("YD_BAYIN_WO_SEQ_YN"), "N");
					
					JDTORecord jrCarSch = commPiUtils.getParam(logId, mthdNm, sModifier);
					jrCarSch.setField("YD_CAR_SCH_ID"        , ydCarSchId);
					jrCarSch.setField("YD_EQP_WRK_STAT"      , "U");                            //야드설비작업상태 
					jrCarSch.setField("YD_EQP_ID"            , CConstant.YD_DM_CAR_EQP_ID);     //야드설비ID
					jrCarSch.setField("YD_CAR_USE_GP"        , CConstant.YD_CAR_USE_GP_DM);     //차량사용구분
					jrCarSch.setField("SPOS_WLOC_CD"         , sWlocCd);                        //발지개소코드
					jrCarSch.setField("CAR_NO"               , sCarNo);                         //차량번호 
					jrCarSch.setField("CARD_NO"              , sCardNo);                        //카드번호
					jrCarSch.setField("YD_CARLD_LEV_DT"      , commPiUtils.getDateTime14());      //상차출발일시
					jrCarSch.setField("TRANS_ORD_DATE"       , sTransOrdDt);                    //운송지시일자
					jrCarSch.setField("TRANS_ORD_SEQNO"      , sTransOrdSeqno);                 //운송지시순번
					jrCarSch.setField("YD_CARLD_STOP_LOC"    , ydStkColGp);                     //야드상차정지위치
//복수동적용					
//					if ("Y".equals(sUgntBayinYn)) {
//						jrCarSch.setField("YD_BAYIN_WO_SEQ"      , "1");
//					} else if ("Y".equals(ydBayinWoSeqYn)) {
//						jrCarSch.setField("YD_BAYIN_WO_SEQ"      , "1");
//					} else {
//						jrCarSch.setField("YD_BAYIN_WO_SEQ"      , CConstant.YD_BAYIN_WO_SEQ_DEFAULT);	//입동지시순번 - 기본값으로 설정(9)
//					}
					if ("Y".equals(sUgntBayinYn)||"E".equals(sCmbnCarldYn)) {
						//긴급입동유무 (Y:입동순서 1)
						jrCarSch.setField("YD_BAYIN_WO_SEQ", "1"); //입동지시순번 - 1순위	
					} else {
						if("Y".equals(ydBayinWoSeqYn)){
							//배차차량작업관리화면 -> 제품이송우선순위 사용함 일경우
							jrCarSch.setField("YD_BAYIN_WO_SEQ", "1"); //입동지시순번 - 1순위
						} else {
							jrCarSch.setField("YD_BAYIN_WO_SEQ", CConstant.YD_BAYIN_WO_SEQ_DEFAULT);	//입동지시순번 - 기본값으로 설정(9)
						}
					}
					jrCarSch.setField("YD_CAR_PROG_STAT"     , "1"          ); //상차출발상태
					jrCarSch.setField("YD_CAR_WRK_GP"        , sWorkGp      );
					jrCarSch.setField("YD_PNT_CD1"           , ydPntCd      ); //야드포인트코드1
					jrCarSch.setField("TRANS_EQUIPMENT_TYPE" , "P"          ); //운송장비타입 P : PDA
					jrCarSch.setField("CAR_KIND"             , "TR"         ); //차량종류
					//jrCarSch.setField("CMBN_CARLD_YN"        , "N"          ); //복수차량여부
//복수동적용					
					jrCarSch.setField("CMBN_CARLD_YN"        , sCmbnCarldYn ); //복수동 적용
					jrCarSch.setField("TEL_NO"               , sTelNo       ); //연락처
					jrCarSch.setField("DRIVER_NAME"          , sDriverName  ); //운전기사명
					jrCarSch.setField("WAIT_ARR_DDTT"		 , commPiUtils.getDateTime14());	//대기장도착시간
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
					ydPICommDAO.insert(jrCarSch, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.insYdCarsch", logId, mthdNm, "TB_YD_CARSCH 등록");
		    		
				} else {
					commPiUtils.printLog(logId, mthdNm + "TB_YD_CARPOINT[차량포인트가 존재하지 않습니다..]" , "SL");
				}
				
				/***********************************
	        	 * 4. 복수동 마지막 도착시 상차 정보 insert
	        	 ***********************************/
//복수동 적용				
				commPiUtils.printLog(logId, "4. 복수동 마지막 도착시 재료 insert" , "SL");
				if ("E".equals(sCmbnCarldYn)) {
					jrParam.setField("YD_CAR_SCH_ID"        , ydCarSchId);
					jrParam.setField("CAR_NO"               , sCarNo); 
					jrParam.setField("TRANS_ORD_DATE"		, sTransOrdDt   );
					jrParam.setField("TRANS_ORD_SEQNO"      , sTransOrdSeqno);                 //운송지시순번
					/* com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updCarFtMvMtlCmbnCarldYn_PIDEV 
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
					                                         AND CAR_NO         = :V_CAR_NO
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
					           AND A.CAR_NO          = :V_CAR_NO
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
					ydPICommDAO.insert(jrParam, "com.inisteel.cim.yd.ccoil.dao.CCoilL3RcvSeEJB.updCarFtMvMtlCmbnCarldYn_PIDEV", logId, mthdNm, "이송재료 등록");
				}
								
				/*
				 * 6. 차량입동지시요구 모듈을 호출한다.
				 */
				commPiUtils.printLog(logId, "차량정지위치[" + ydStkColGp + "], 차량스케줄ID[" + ydCarSchId + "] -PDA AB차량입동지시요구 모듈을 호출 시작" , "SL");
				
				JDTORecord jrYdMsg = commPiUtils.getParam(logId, mthdNm, sModifier);	
				jrYdMsg.setField("JMS_TC_CD"			, "YDYDJ553");          //차량입동지시 요구 기존:YDYDJ662
				jrYdMsg.setField("JMS_TC_CREATE_DDTT"	, commPiUtils.getDateTime14()); //JMSTC생성일시
				jrYdMsg.setField("YD_CARPNT_CD"		    , sCarldPntCd);
				jrYdMsg.setField("YD_CAR_SCH_ID"		, ydCarSchId );
				jrYdMsg.setField("CR_FRTOMOVE_GP"       , sCrFrtomoveGp );
				
				EJBConnector ejbConn = new EJBConnector("default", "CCoilCarMvSeEJB", this);
				JDTORecord jrRst = (JDTORecord)ejbConn.trx("procCarBayInOrdReqTr", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
				
				jrRtn = commPiUtils.addSndData(jrRtn, jrRst);
			}	
			commPiUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * IFTest 전송
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord sndIfTestPI(GridData gdReq) throws DAOException {
		String mthdNm = "IFTest 전송[CoilL3RcvPISeEJB.sndIfTestPI] < " + gdReq.getNavigateValue();
		String logId  = commPiUtils.getLogId();

		try {
			commPiUtils.printLog(logId, mthdNm, "S+");
			//Return Value
			JDTORecord jrRtn = null;	
			String ifClassNm  = null;
			String ifMthNm    = null;
			
			JDTORecord jrParam = commPiUtils.getParam(logId, mthdNm, commPiUtils.trim(gdReq.getParam("userid")));
			String ydGp = gdReq.getParam("FM_YD_GP");
			//레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
		    String msgId = commPiUtils.trim(gdReq.getParam("IF_ID")); //IFID			
			for (int ii = 0; ii < rowCnt; ii++) {				
				jrParam.setField("ITM_VAL"	, commPiUtils.getValue(gdReq, "ITM_VAL", ii)); 
				jrParam.setField("IF_ID"	, msgId); 
				jrParam.setField("ITM_SEQ"	, commPiUtils.getValue(gdReq, "ITM_SEQ", ii) ); 
				jrParam.setField("REMARKS"	, commPiUtils.getValue(gdReq, "REMARKS", ii) ); 
				if(("J".equals(ydGp)) || 
				   ("T".equals(ydGp)) ||
				   ("S".equals(ydGp))) {
					ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccommon.dao.CCommDAO.updIfTestData", logId, mthdNm, "IFTest 항목값 수정");
				}
				if ("1".equals(ydGp)) { 
					ydPICommDAO.update(jrParam, "com.inisteel.cim.yf.common.dao.YfCommDAO.updIFTestData", logId, mthdNm, "IFTest 항목값 수정");
				}
				if ("3".equals(ydGp)) { 
					ydPICommDAO.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updIfTestData", logId, mthdNm, "IFTest 항목값 수정");
				}
				if ("K".equals(ydGp)) { 
					ydPICommDAO.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updIfTestData", logId, mthdNm, "IFTest 항목값 수정");
				}
			}
		
			//큐에 넣을 데이터를 생성 - Log ID, Method, 수정자 Set 
			JDTORecord sndData = JDTORecordFactory.getInstance().create();
				
			for (int ii = 0; ii < rowCnt; ii++) {
				sndData.setField(commPiUtils.trim(gdReq.getHeader("ITM_ID" ).getValue(ii)), commPiUtils.trim(gdReq.getHeader("ITM_VAL").getValue(ii)));
			}
			
			commPiUtils.printLog(logId, "sYD_GP: " + ydGp + "MQ_YN:" + gdReq.getParam("MQ_YN")+ "TRT_MTH:" + gdReq.getParam("TRT_MTH") , "SL");			  
			
			if ("Y".equals(gdReq.getParam("MQ_YN"))) {
				M10YdExLm99Sender.SendMessage(commPiUtils.jdtoRecordToLinkedHashMap(sndData));
			} else {
				
				if("Y".equals(gdReq.getParam("TRT_MTH"))) {
					//신규모듈 적용여부 테스트 CheckBox 체크시 TB_YM_Z_IF 테이블의 BEF_PGM_NM1, BEF_PGM_NM2 을 읽어 호출 한다.
					ifClassNm = gdReq.getParam("BEF_PGM_NM1");
					ifMthNm   = gdReq.getParam("BEF_PGM_NM2");	//EJB Call
					
					String sMsg="["+mthdNm+"] ifClassNm:" + ifClassNm + " , ifMthNm:" + ifMthNm ;
					commPiUtils.printLog(logId, sMsg, "SL");		
					
					//송신 공통 EJB를 이용하여 전송
					EJBConnector ejbConn = new EJBConnector("default", ifClassNm, this);
					ejbConn.trx(ifMthNm, new Class[] { JDTORecord.class }, new Object[] { sndData });
				} else {

					
				}

			}	
			commPiUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, mthdNm, e));
		}	
	} // end of sndIfTest
	
	/**
	 * IFTest 전송
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord sndIfTestPIMUL(GridData gdReq) throws DAOException {
		String mthdNm = "IFTest 전송MULTY[CoilL3RcvPISeEJB.sndIfTestPIMUL] < " + gdReq.getNavigateValue();
		String logId  = commPiUtils.getLogId();

		try {
			commPiUtils.printLog(logId, mthdNm, "S+");
			//Return Value
			JDTORecord jrRtn = null;	
			String ifClassNm  = null;
			String ifMthNm    = null;
			
			JDTORecord jrParam = commPiUtils.getParam(logId, mthdNm, commPiUtils.trim(gdReq.getParam("userid")));
			String ydGp = gdReq.getParam("FM_YD_GP");
			//레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
		    String msgId = commPiUtils.trim(gdReq.getParam("IF_ID")); //IFID
		   
		    String stlNoMultVal = commPiUtils.trim(gdReq.getParam("STL_NO_MULT_VAL"));
		   
		    
			for (int ii = 0; ii < rowCnt; ii++) {				
				jrParam.setField("ITM_VAL"	, commPiUtils.getValue(gdReq, "ITM_VAL", ii)); 
				jrParam.setField("IF_ID"	, msgId); 
				jrParam.setField("ITM_SEQ"	, commPiUtils.getValue(gdReq, "ITM_SEQ", ii) ); 
				jrParam.setField("REMARKS"	, commPiUtils.getValue(gdReq, "REMARKS", ii) ); 
				if(("J".equals(ydGp)) || 
				   ("T".equals(ydGp)) ||
				   ("S".equals(ydGp))) {
					ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccommon.dao.CCommDAO.updIfTestData", logId, mthdNm, "IFTest 항목값 수정");
				}
				if ("1".equals(ydGp)) { 
					ydPICommDAO.update(jrParam, "com.inisteel.cim.yf.common.dao.YfCommDAO.updIFTestData", logId, mthdNm, "IFTest 항목값 수정");
				}
				if ("3".equals(ydGp)) { 
					ydPICommDAO.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updIfTestData", logId, mthdNm, "IFTest 항목값 수정");
				}
				if ("K".equals(ydGp)) { 
					ydPICommDAO.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updIfTestData", logId, mthdNm, "IFTest 항목값 수정");
				}
			}
			
			if(!"".equals(stlNoMultVal)){
				String [] stlNoArr = stlNoMultVal.split(","); 
				
				commPiUtils.printLog(logId, "sYD_GP: " + ydGp + "MQ_YN:" + gdReq.getParam("MQ_YN")+ "TRT_MTH:" + gdReq.getParam("TRT_MTH") , "SL");		
				
				for(int i=0; i<stlNoArr.length; i++){
					if("".equals(stlNoArr[i])) continue;
					//큐에 넣을 데이터를 생성 - Log ID, Method, 수정자 Set 
					JDTORecord sndData = JDTORecordFactory.getInstance().create();
						
					for (int j = 0; j < rowCnt; j++) {
						//재료번호인경우 STL_NO_MULT_VAL 에서 하나씩 가져옴 
						if("GOODS_NO".equals(commPiUtils.trim(gdReq.getHeader("ITM_ID" ).getValue(j)))){
							sndData.setField(commPiUtils.trim(gdReq.getHeader("ITM_ID" ).getValue(j)), stlNoArr[i]);
						}
						else {
							sndData.setField(commPiUtils.trim(gdReq.getHeader("ITM_ID" ).getValue(j)), commPiUtils.trim(gdReq.getHeader("ITM_VAL").getValue(j)));
						}
					}
					
					if ("Y".equals(gdReq.getParam("MQ_YN"))) {
						M10YdExLm99Sender.SendMessage(commPiUtils.jdtoRecordToLinkedHashMap(sndData));
					} else {
						
						if("Y".equals(gdReq.getParam("TRT_MTH"))) {
							//신규모듈 적용여부 테스트 CheckBox 체크시 TB_YM_Z_IF 테이블의 BEF_PGM_NM1, BEF_PGM_NM2 을 읽어 호출 한다.
							ifClassNm = gdReq.getParam("BEF_PGM_NM1");
							ifMthNm   = gdReq.getParam("BEF_PGM_NM2");	//EJB Call
							
							String sMsg="["+mthdNm+"] ifClassNm:" + ifClassNm + " , ifMthNm:" + ifMthNm ;
							commPiUtils.printLog(logId, sMsg, "SL");		
							
							//송신 공통 EJB를 이용하여 전송
							EJBConnector ejbConn = new EJBConnector("default", ifClassNm, this);
							ejbConn.trx(ifMthNm, new Class[] { JDTORecord.class }, new Object[] { sndData });
						} else {

							
						}

					}		
					
				}
				
			}
				
			commPiUtils.printLog(logId, mthdNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, mthdNm, e));
		}	
	} // end of sndIfTestMUL
	
	/**
	 * IFTest Layout 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updIfTestDataPI(GridData gdReq) throws DAOException {
		String mthdNm = "IFTest Layout 변경[CCoilJspSeEJB.updIfTestData] < " + gdReq.getNavigateValue();
		String logId  = commPiUtils.getLogId();

		try {
			commPiUtils.printLog(logId, mthdNm, "S+");
			//Return Value
			JDTORecord jrRtn = null;
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commPiUtils.getParam(logId, mthdNm, commPiUtils.trim(gdReq.getParam("userid")));
			//수정할 레코드 수
			String ydGp = gdReq.getParam("FM_YD_GP");
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			for (int ii = 0; ii < rowCnt; ii++) {
				
				jrParam.setField("ITM_VAL"	,commPiUtils.getValue(gdReq, "ITM_VAL", ii)); 
				jrParam.setField("IF_ID"	,gdReq.getParam("IF_ID")); 
				jrParam.setField("ITM_SEQ"	,commPiUtils.getValue(gdReq, "ITM_SEQ", ii) );
				jrParam.setField("REMARKS"	,commPiUtils.getValue(gdReq, "REMARKS", ii) );
				//ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccommon.dao.CCommDAO.updIfTestData", logId, mthdNm, "IFTest 항목값 수정");
				if(("J".equals(ydGp)) || 
				   ("T".equals(ydGp)) ||
				   ("S".equals(ydGp))) {
					ydPICommDAO.update(jrParam, "com.inisteel.cim.yd.ccommon.dao.CCommDAO.updIfTestData", logId, mthdNm, "IFTest 항목값 수정");
				}
				if ("1".equals(ydGp)) { 
					ydPICommDAO.update(jrParam, "com.inisteel.cim.yf.common.dao.YfCommDAO.updIFTestData", logId, mthdNm, "IFTest 항목값 수정");
				}
				if ("3".equals(ydGp)) { 
					ydPICommDAO.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updIfTestData", logId, mthdNm, "IFTest 항목값 수정");
				}
				if ("K".equals(ydGp)) { 
					ydPICommDAO.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updIfTestData", logId, mthdNm, "IFTest 항목값 수정");
				}				
			}
			commPiUtils.printLog(logId, mthdNm, "S-");
			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, mthdNm, e));
		}	
	} 
	
	
	/**	 
	 * [A] 오퍼레이션명 :열연옥외야드도착실적  
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	public void rcvM10LMYDJ1161(JDTORecord rcvMsg) throws DAOException {
		String mthdNm = "[YdCoilL3RcvPISeEJB.rcvM10LMYDJ1161] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();  
		try {
			commPiUtils.printLog(logId, mthdNm, "S+");
			
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", mthdNm, "APPPI0", "J", "*");
//			if ("N".equals(sApplyYnPI)) {
//				commPiUtils.printLog(logId, "PI 적용여부가 N이라서 수신처리 불가", "SL");
//				return; 
//			}			
			
			//수신 항목 값
			String msgId  = commPiUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			
			// DAO 및 UTIL 객체 생성
			YmEtcDao YmEtcDao   = new YmEtcDao();
			// 레코드 선언
			JDTORecord recIn    = null;
			
		    String sMsg         = "";
		    String sMethodName  = "rcvM10LMYDJ1161";	    	        	
			int intRtnVal = 0;
			
	    	sMsg = "[출하] 열연옥외창고도착실적 수신";
	    	commPiUtils.printLog(logId, sMsg , "SL");
	    	
	    	/*
	    	YD_GP			야드구분	CHAR	1	V		
	    	TRANSMIT_DATE	전송일자	CHAR	8			
	    	SEND_SEQ		전송순번	NUMBER	5			
	    	CANCEL_YN		취소유무	CHAR	1	Y	Y: 취소 , N: 지시	
	    	FR_GBN			사외창고구분 CHAR  2   VA : 열연옥외야드장
	    	TABLE : TB_DM_SETTLEDOWNWRSLTIFTEMP
			*/ 
	    	
	    	String sYdGp         = commPiUtils.trim(rcvMsg.getFieldString("YD_GP"));    
	    	String sTransmitDate = commPiUtils.trim(rcvMsg.getFieldString("TRANSMIT_DATE"));    
	    	String sSendSeq      = commPiUtils.trim(rcvMsg.getFieldString("SEND_SEQ"));    
	    	String sCancelYn     = commPiUtils.trim(rcvMsg.getFieldString("CANCEL_YN"));    
	    	String sFrGbn        = commPiUtils.trim(rcvMsg.getFieldString("FR_GBN"));  
	    	String sTrnReqDate   = commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_DATE"));    
	    	String sTrnReqSeq    = commPiUtils.trim(rcvMsg.getFieldString("TRN_REQ_SEQ"));    
	    	String sCarNo        = commPiUtils.trim(rcvMsg.getFieldString("CAR_NO")); 

	    	if("N".equals(sCancelYn)){
		    	
	    		// 레코드 생성
				recIn = JDTORecordFactory.getInstance().create(); 
				recIn.setField("TRN_REQ_DATE", sTrnReqDate);
				recIn.setField("TRN_REQ_SEQ" , sTrnReqSeq);
				recIn.setField("CAR_NO"      , sCarNo);
				
				String sFrYdGp	= "";
				String sYdLoc	= "";
				
				if("".equals(sFrGbn)){
					sFrYdGp = sYdGp;
					sYdLoc  = sYdGp+"A11111111";
				}else{
					sFrYdGp = sYdGp;
					sYdLoc  = sFrGbn+"11111111";
				}

				recIn.setField("YD_GP", sFrYdGp);
				recIn.setField("YD_STR_LOC", sYdLoc);

			 if(sYdGp.equals("V")){ 
					//intRtnVal = YmEtcDao.uptYmEtcDao(recIn, 82); //열연 사외창고 인경우
					intRtnVal = ydPICommDAO.update(recIn, "com.inisteel.cim.ym.bcoil.dao.updTB_DM_SETTLEDOWNWRSLTIFTEMPCOIL_PIDEV", logId, mthdNm, "코일공통 수정");
									
					if(intRtnVal < 0){
						sMsg = "COILCOMM[COIL작업지시] Error :: TRN_REQ_DATE(" + sTrnReqDate + ") SEND_SEQ(" + sTrnReqSeq + ") [" + intRtnVal + "]" + "PARAMETER ERROR";
						commPiUtils.printLog(logId, sMsg , "SL");
						return ;
					} else if(intRtnVal == 0){
						sMsg = "COILCOMM[COIL작업지시] Error :: TRN_REQ_DATE(" + sTrnReqDate + ") SEND_SEQ(" + sTrnReqSeq + ")[" + intRtnVal + "]" + "DO NOT EXIST";
						commPiUtils.printLog(logId, sMsg , "SL");
						return ;
					}
				}
	    	}
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commPiUtils.makeErrorLog(logId, mthdNm, e));
		}		
	}

}
