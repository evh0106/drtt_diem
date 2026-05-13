/**
 * @(#)BtYsL3RcvSeEJBBean
 *
 * @version          V1.00
 * @author           조병기
 * @date             2014/12/22
 *
 * @description      BILLET 야드 L3 수신 처리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2014/12/22   윤재광      조병기      최초 등록
 */
package com.inisteel.cim.ys.bt.session;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.ys.bt.dao.BtYsDAO;
import com.inisteel.cim.ys.common.dao.YsCommDAO;
import com.inisteel.cim.ys.common.util.YsCommUtils;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

/**
 *      [A] 클래스명 : BILLET 야드 L3수신 처리
 *
 * @ejb.bean name="BtYsL3RcvSeEJB" jndi-name="BtYsL3RcvSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
*/

public class BtYsL3RcvSeEJBBean extends BaseSessionBean {
	
	private static final long serialVersionUID = 1L;
	private YsCommUtils commUtils = new YsCommUtils();
	private YsCommDAO commDao = new YsCommDAO();
	private BtYsDAO btYsDao = new BtYsDAO();
    
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}	
	
	/**
	 *      [A] 오퍼레이션명 : BILLET전단실적(SBYSJ001)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvSBYSJ001(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "BILLET전단실적[BtYsL3RcvSeEJB.rcvSBYSJ001] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String stlNo    = commUtils.trim(rcvMsg.getFieldString("SSTL_NO"    )); //재료번호
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(stlNo) ) {
				throw new Exception("잘못된 재료번호[" + stlNo + "] 입니다.");
			}

			/**********************************************************
			* 2. 저장품 등록
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("SSTL_NO"  , stlNo   ); //재료번호
			jrParam.setField("MODIFIER", modifier); //수정자

			//저장품 등록
			btYsDao.insert(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.insBtYdStock", logId, methodNm, "저장품 등록");
			

			/**********************************************************
			* 3. 저장품제원 전문을 전송
			**********************************************************/
			jrParam.setField("YD_GP"          , "B"); //야드구분
			jrParam.setField("YD_INFO_SYNC_CD", "A"); //야드정보동기화코드(생산실적)

			//저장품제원(YSN2L002) 전송 Data 생성
			JDTORecord jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN2L002", jrParam));
			
			if(stlNo.startsWith("P")){
				//포항BILLET라벨정보(YSN2L007) 전송 Data 생성
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN2L007", jrParam));
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
	 *      [A] 오퍼레이션명 : BILLET압연지시확정(CUYSJ002)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvCUYSJ002(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "BILLET압연지시확정[BtYsL3RcvSeEJB.rcvCUYSJ002] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId      = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String modGp      = commUtils.trim(rcvMsg.getFieldString("MOD_GP"       )); //수정구분
			String ptopPlntGp = commUtils.trim(rcvMsg.getFieldString("PTOP_PLNT_GP" )); //조업공장구분
			String chgWoFrPnt = commUtils.trim(rcvMsg.getFieldString("CHG_WO_FR_PNT")); //장입지시FromPoint
			String chgWoToPnt = commUtils.trim(rcvMsg.getFieldString("CHG_WO_TO_PNT")); //장입지시ToPoint
			String stlNo      = commUtils.trim(rcvMsg.getFieldString("SSTL_NO"      )); //재료(Slab)번호
			String endGp      = commUtils.trim(rcvMsg.getFieldString("END_GP"       )); //종료구분
			String modifier   = commUtils.trim(rcvMsg.getFieldString("MODIFIER"   )); //수정자(Backup Only)
//			String ydGp       = "C";	//야드구분
			
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(modGp)) {
				throw new Exception("수정구분(MOD_GP) 값이 없습니다.");
			} else if ("I".equals(modGp) && "".equals(ptopPlntGp)) {
				throw new Exception("조업공장구분(PTOP_PLNT_GP) 값이 없습니다.");
			}

			JDTORecord jrRtn = null;
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("PTOP_PLNT_GP", ptopPlntGp); //조업공장구분
			jrParam.setField("MODIFIER"    , modifier  ); //수정자

			if ("D".equals(modGp)) {
				/**********************************************************
				* 2. 지시취소이면 압연지시결번실적 처리
				**********************************************************/
				if ("".equals(stlNo) || stlNo.length() < 9) {
					throw new Exception("잘못된 재료번호[" + stlNo + "] 입니다.");
				}

				/**********************************************************
				* 2.1 저장품 수정
				**********************************************************/
				jrParam.setField("SSTL_NO", stlNo); //재료번호

				//저장품 수정  REFUR_CHG_LOT_NO, REFUR_CHG_PLN_SERNO 를 Clear 한다.
				btYsDao.update(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.updBtYdStockChgNoClear", logId, methodNm, "소형압연지시 취소 저장품 등록");

				/**********************************************************
				* 2.2 저장품제원 전문을 생성
				**********************************************************/
				jrParam.setField("YD_INFO_SYNC_CD", "5" ); //야드정보동기화코드(지정저장품)

				//저장품제원(YSM3L002) 전문 생성
				//jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN2L002", jrParam));
				
				
				/**********************************************************
				* 2.3 선재,BIC 생산예정 저장품 삭제
				**********************************************************/
				jrParam.setField("SSTL_NO", stlNo); //재료번호 (BILLET번호)
				
				btYsDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.delPlnSRWStock", logId, methodNm, "선재,BIC 생산예정 저장품 등록");
				
				
			} else {
				/**********************************************************
				* 3. 압연지시확정 처리
				**********************************************************/
				if ("".equals(chgWoFrPnt)) {
					throw new Exception("장입지시FromPoint(CHG_WO_FR_PNT) 값이 없습니다.");
				} else if ("".equals(chgWoToPnt)) {
					throw new Exception("장입지시ToPoint(CHG_WO_TO_PNT) 값이 없습니다.");
				}
				
				/**********************************************************
				* 3.1 저장품 수정 (기존 압연지시 Clear)
				**********************************************************/
				btYsDao.insert(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.updBtYdStockChgNoClearAll", logId, methodNm, "소형압연지시 확정 저장품 수정 (기존 압연지시 Clear)");

				/**********************************************************
				* 3.2 저장품 수정(신규 입연지시 )
				**********************************************************/
				jrParam.setField("CHG_WO_FR_PNT", chgWoFrPnt); //장입지시FromPoint
				jrParam.setField("CHG_WO_TO_PNT", chgWoToPnt); //장입지시ToPoint
	
				btYsDao.insert(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.updBtYdStockChgNo", logId, methodNm, "소형압연지시 확정 저장품 수정 (신규 압연지시)");

	
				/**********************************************************
				* 3.3 압연지시 확정이면 장입대상품만 저장품제원 전문을 전송
				**********************************************************/
				//저장품제원(YSN2L002) 전문 생성
				jrParam.setField("YD_INFO_SYNC_CD", "5" ); //야드정보동기화코드(지정저장품)
				
				//jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN2L002ChgWo", jrParam));
				
				//크레인작업계획 전송을 위하여
				endGp = "*";
				
				/**********************************************************
				* 3.4 선재,BIC 생산예정 저장품 생성 
				**********************************************************/
				jrParam.setField("PTOP_PLNT_GP", ptopPlntGp); //조업공장구분
				jrParam.setField("CHG_WO_FR_PNT", chgWoFrPnt); //장입지시FromPoint
				jrParam.setField("CHG_WO_TO_PNT", chgWoToPnt); //장입지시ToPoint
	
				btYsDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insPlnSRWStock", logId, methodNm, "선재,BIC 생산예정 저장품 등록");
				
			}

			/**********************************************************
			* 4. 크레인작업계획(YSN2L005) 전문을 생성
			**********************************************************/
// 9/11 막음 : 윤차장 요청			
//			if ("*".equals(endGp)) {
//				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN2L005", jrParam));
//			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 저장품 수정
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return void
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public void updStock(JDTORecord jrParam) throws DAOException {
		String methodNm = "저장품 수정[BtYsL3RcvSeEJB.updStock] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			//저장품 등록
			btYsDao.insert(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.insBtYdStock", logId, methodNm, "저장품 등록");
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : BILLET충당실적(PAYSJ003)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPAYSJ003(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "BILLET충당실적[BtYsL3RcvSeEJB.rcvPAYSJ003] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String wrkHdsDd = commUtils.trim(rcvMsg.getFieldString("WRK_HDS_DD1")); //작업계상일자1
			String stepNo   = commUtils.trim(rcvMsg.getFieldString("STEP_NO1"   )); //차수1
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER" )); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(wrkHdsDd)) {
				throw new Exception("작업계상일자1(WRK_HDS_DD1) 값이 없습니다.");
			} else if ("".equals(stepNo)) {
				throw new Exception("차수1(STEP_NO1) 값이 없습니다.");
			}

			/**********************************************************
			* 2. 저장품 수정
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("WRK_HDS_DD", wrkHdsDd); //작업계상일자
			jrParam.setField("STEP_NO"   , stepNo  ); //차수
			jrParam.setField("MODIFIER"  , modifier); //수정자

			JDTORecordSet jsStl = commDao.select(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.getPAYSJ003Stl", logId, methodNm, "BLOOM충당실적 조회");

			if (jsStl != null && jsStl.size() > 0) {
				int stlSh = jsStl.size(); //재료매수

				for (int ii = 0; ii < stlSh; ii++) {
					jrParam.setField("SSTL_NO", commUtils.trim(jsStl.getRecord(ii).getFieldString("SSTL_NO"))); //재료번호

					//저장품 등록 (※ BILLET충당실적과 BILLET이송지시가 동시에 같은 재료번호로 수신되어 DeadLock이 발생하여 EJB Call실행으로 변경)
					EJBConnector tranConn = new EJBConnector("default", "BtYsL3RcvSeEJB", this);
					tranConn.trx("updStock", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				}
			} else {
				throw new Exception("목적충당여재처리이력 테이블(TB_PB_ORDERTRANSMATCHLOG)에 충당할 재료가 조회되지 않습니다.");
			}

			commUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : BILLET이송지시(PAYSJ004)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPAYSJ004(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "BILLET이송지시[BlYsL3RcvSeEJB.rcvPAYSJ004] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId            = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String frtomoveWordDate = commUtils.trim(rcvMsg.getFieldString("FRTOMOVE_WORD_DATE1")); //이송작업지시일자1
			String transwordSeqNo   = commUtils.trim(rcvMsg.getFieldString("TRANSWORD_SEQNO1"   )); //이송지시차수1
			String modifier         = commUtils.trim(rcvMsg.getFieldString("MODIFIER"         )); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(frtomoveWordDate)) {
				throw new Exception("이송작업지시일자1(FRTOMOVE_WORD_DATE1) 값이 없습니다.");
			} else if ("".equals(transwordSeqNo)) {
				throw new Exception("이송지시차수1(TRANSWORD_SEQNO1) 값이 없습니다.");
			}

			/**********************************************************
			* 2. 저장품, 소재이송지시 수정
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("FRTOMOVE_WORD_DATE", frtomoveWordDate); //이송작업지시일자1
			jrParam.setField("TRANSWORD_SEQNO"   , transwordSeqNo  ); //이송지시차수1
			jrParam.setField("MODIFIER"          , modifier        ); //수정자

			JDTORecordSet jsStl = commDao.select(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.getPAYSJ004Stl", logId, methodNm, "BILLET이송지시 조회");

			if (jsStl != null && jsStl.size() > 0) {
				int stlSh = jsStl.size(); //재료매수

				for (int ii = 0; ii < stlSh; ii++) {
					jrParam.setField("SSTL_NO", commUtils.trim(jsStl.getRecord(ii).getFieldString("SSTL_NO"))); //재료번호

					//저장품 등록 (※ 충당실적과 송지시가 동시에 같은 재료번호로 수신되어 DeadLock이 발생하여 EJB Call실행으로 변경)
					EJBConnector tranConn = new EJBConnector("default", "BtYsL3RcvSeEJB", this);
					tranConn.trx("updStock", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				}

				//소재이송지시 Update
				commDao.update(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.updPAYSJ004Stl", logId, methodNm, "소재이송지시 Update");
				
			} else {
				throw new Exception("소재이송지시 테이블(TB_PB_STLFRTOMOVE)에 이송지시 재료가 존재하지 않습니다.");
			}

			commUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

}
