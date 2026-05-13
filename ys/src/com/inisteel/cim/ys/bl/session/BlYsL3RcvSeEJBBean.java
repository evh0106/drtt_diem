/**
 * @(#)BlYsL3RcvSeEJBBean
 *
 * @version          V1.00
 * @author           조병기
 * @date             2014/12/22
 *
 * @description      BLOOM 야드 L3 수신 처리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2014/12/22   윤재광      조병기      최초 등록
 */
package com.inisteel.cim.ys.bl.session;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.ys.bl.dao.BlYsDAO;
import com.inisteel.cim.ys.common.dao.YsCommDAO;
import com.inisteel.cim.ys.common.util.YsCommUtils;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

/**
 *      [A] 클래스명 : BLOOM 야드 L3수신 처리
 *
 * @ejb.bean name="BlYsL3RcvSeEJB" jndi-name="BlYsL3RcvSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
*/

public class BlYsL3RcvSeEJBBean extends BaseSessionBean {
	
	private static final long serialVersionUID = 1L;
	private YsCommUtils commUtils = new YsCommUtils();
	private YsCommDAO commDao = new YsCommDAO();
	private BlYsDAO BlYsDao = new BlYsDAO();

	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}	
	
	/**
	 *      [A] 오퍼레이션명 : BLOOM전단실적(SEYSJ001)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvSEYSJ001(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "BLOOM전단실적[BlYsL3RcvSeEJB.rcvSEYSJ001] < " + rcvMsg.getResultMsg();
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
			BlYsDao.insert(jrParam, "com.inisteel.cim.ys.bl.dao.BlYsDAO.insBlYdStock", logId, methodNm, "저장품 등록");
			

			/**********************************************************
			* 3. 저장품제원 전문을 전송
			**********************************************************/
			jrParam.setField("YD_GP"          , "B"); //야드구분
			jrParam.setField("YD_INFO_SYNC_CD", "A"); //야드정보동기화코드(생산실적)

			//저장품제원(YSN1L002) 전송 Data 생성
			JDTORecord jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN1L002", jrParam));

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	 
	/**
	 *      [A] 오퍼레이션명 : BLOOM2차전단분할실적(SEYSJ002)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvSEYSJ002(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "BLOOM2차전단분할실적[BlYsL3RcvSeEJB.rcvSEYSJ002] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		String blmNo				="" ;
		String YD_STK_LYR_MTL_STAT 	="" ;
		String szYsStkColGp 		="" ;
		String szYsStkBedNo 		="" ;
		String szYsStkLyrNo 		="" ;
		String szYsStkSeqNo 		="" ;
		int    seq					=0 ;
		JDTORecord jrRtn 			=JDTORecordFactory.getInstance().create();
		JDTORecord jrParam 			= JDTORecordFactory.getInstance().create();
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String parentBlmNo   = commUtils.trim(rcvMsg.getFieldString("PARENT_BLM_NO"    )); //모재료번호 
			String modifier = commUtils.trim("SEYSJ002"); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(parentBlmNo) ) {
				throw new Exception("잘못된 모재료번호[" + parentBlmNo + "] 입니다.");
			}
			

			
			
			/**********************************************************
			* 1. 모블룸번호 처리
			**********************************************************/
			
			//모재료번호로 저장위치 조회
			jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setField("SSTL_NO", parentBlmNo);
			jrParam.setField("YD_GP",   "B");
			
			JDTORecordSet jsStkLyrStlNo = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStrLocRegPda", logId, methodNm, "재료번호로 조회");
			
			
			
			if(jsStkLyrStlNo.size() > 0) {
				YD_STK_LYR_MTL_STAT = jsStkLyrStlNo.getRecord(0).getFieldString("YD_STK_LYR_MTL_STAT");
				
				if("C".equals(YD_STK_LYR_MTL_STAT)||"U".equals(YD_STK_LYR_MTL_STAT)){
					szYsStkColGp = jsStkLyrStlNo.getRecord(0).getFieldString("YS_STK_COL_GP");
					szYsStkBedNo = jsStkLyrStlNo.getRecord(0).getFieldString("YS_STK_BED_NO");
					szYsStkLyrNo = jsStkLyrStlNo.getRecord(0).getFieldString("YS_STK_LYR_NO");
					szYsStkSeqNo = jsStkLyrStlNo.getRecord(0).getFieldString("YS_STK_SEQ_NO");
				}

			}else{
				throw new Exception("모재료번호[" + parentBlmNo + "]저장위치가 존재 안합니다.");
			}
			
			
			
			
			/**********************************************************
			* 1. 모블룸번호 BLOOMCOMM 삭제
			**********************************************************/
			//삭제일경우 처리
			jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setField("FNL_REG_PGM"			, "blStrLocModjm" );
			jrParam.setField("YD_GP"				, "_" );
			jrParam.setField("YD_BAY_GP"			, szYsStkColGp.substring(1,2) );
			jrParam.setField("YD_EQP_GP"			, szYsStkColGp.substring(2,4) );
			jrParam.setField("YS_STK_COL_NO"		, szYsStkColGp.substring(4,6) );
			jrParam.setField("YS_STK_BED_NO"		, szYsStkBedNo );
			jrParam.setField("YS_STK_LYR_NO"		, szYsStkLyrNo );
			jrParam.setField("YS_STK_SEQ_NO"		, szYsStkSeqNo );
			jrParam.setField("YS_STR_LOC"			, "_" + szYsStkColGp.substring(1,6) + szYsStkBedNo + szYsStkLyrNo + szYsStkSeqNo );
			jrParam.setField("SSTL_NO"				, parentBlmNo ); //모블룸번호
			
			//commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBlCommYsStrLoc", logId, methodNm, "BLOOM공통 야드저장위치 수정");
			
			/**********************************************************
			* 1.2공통 저장위치 Update (별도 Transaction 으로 처리)
			**********************************************************/
			EJBConnector tranConn = new EJBConnector("default", "BlYsL2RcvSeEJB", this);
			tranConn.trx("updBlCommYsStrLoc", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			
			
			
			
			/**********************************************************
			* 1. 자블룸번호 처리
			**********************************************************/
			for (int ii = 0; ii <3; ii++) {
				seq     =ii+1;
				blmNo 	= commUtils.trim(rcvMsg.getFieldString("BLM_NO"+seq) ); //분할재료번호
				
				/**********************************************************
				* 2. 적치단 등록
				**********************************************************/
				jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("SSTL_NO"					, blmNo); //szStlNo 에는 삭제일경우 빈값이 들어있고 그외는 제품번호가 들어 있다.
				jrParam.setField("YD_STK_LYR_MTL_STAT"		, "C"); //적치중
				jrParam.setField("YS_STK_COL_GP"			, szYsStkColGp );
				jrParam.setField("YS_STK_BED_NO"			, szYsStkBedNo );
				jrParam.setField("YS_STK_LYR_NO"			, szYsStkLyrNo );
				jrParam.setField("YS_STK_SEQ_NO"			, ""+seq );
				
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyr", logId, methodNm, "적치단정보 수정");
				
				
				
				/**********************************************************
				* 2. 저장품 등록
				**********************************************************/  
				jrParam.setField("MODIFIER", modifier); //수정자

				//저장품 등록
				BlYsDao.insert(jrParam, "com.inisteel.cim.ys.bl.dao.BlYsDAO.insBlYdStock", logId, methodNm, "저장품 등록");
				
				
	
				/**********************************************************
				* 2. BLOOMCOMM 등록
				**********************************************************/
				jrParam.setField("FNL_REG_PGM"			, "rcvSEYSJ002" );
				jrParam.setField("YD_GP"				, "B" );
				jrParam.setField("YD_BAY_GP"			, szYsStkColGp.substring(1,2) );
				jrParam.setField("YD_EQP_GP"			, szYsStkColGp.substring(2,4) );
				jrParam.setField("YS_STK_COL_NO"		, szYsStkColGp.substring(4,6) );
				jrParam.setField("YS_STK_BED_NO"		, szYsStkBedNo );
				jrParam.setField("YS_STK_LYR_NO"		, szYsStkLyrNo );
				jrParam.setField("YS_STK_SEQ_NO"		, ""+seq );
				jrParam.setField("YS_STR_LOC"			, "B" + szYsStkColGp.substring(1,6) + szYsStkBedNo + szYsStkLyrNo + (""+seq) );
				jrParam.setField("SSTL_NO"				, blmNo ); 
				
				//commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBlCommYsStrLoc", logId, methodNm, "BLOOM공통 야드저장위치 수정");
				
				/**********************************************************
				* 1.2공통 저장위치 Update (별도 Transaction 으로 처리)
				**********************************************************/
				EJBConnector tranConn2 = new EJBConnector("default", "BlYsL2RcvSeEJB", this);
				tranConn2.trx("updBlCommYsStrLoc", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				
				/**********************************************************
				* 3. 저장품제원 전문을 전송
				**********************************************************/
				jrParam.setField("YD_GP"          , "B"); //야드구분
				jrParam.setField("YD_INFO_SYNC_CD", "A"); //야드정보동기화코드(생산실적)
	
				//저장품제원(YSN1L002) 전송 Data 생성
				jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN1L002", jrParam));

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
	 *      [A] 오퍼레이션명 : BLOOM압연지시확정(CUYSJ001)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvCUYSJ001(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "BLOOM압연지시확정[BlYsL3RcvSeEJB.rcvCUYSJ001] < " + rcvMsg.getResultMsg();
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
//			String ydGp       = "B";	//야드구분
			
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
				BlYsDao.update(jrParam, "com.inisteel.cim.ys.bl.dao.BlYsDAO.updBlYdStockChgNoClear", logId, methodNm, "대형압연지시 취소 저장품 등록");

				/**********************************************************
				* 2.2 저장품제원 전문을 생성
				**********************************************************/
				jrParam.setField("YD_INFO_SYNC_CD", "5" ); //야드정보동기화코드(지정저장품)

				//저장품제원(YSN1L002) 전문 생성
				//jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN1L002", jrParam));
				
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
				BlYsDao.update(jrParam, "com.inisteel.cim.ys.bl.dao.BlYsDAO.updBlYdStockChgNoClearAll", logId, methodNm, "대형압연지시 확정 저장품 수정 (기존 압연지시 Clear)");
				
				/**********************************************************
				* 3.2 저장품 수정(신규 입연지시 )
				**********************************************************/
				jrParam.setField("CHG_WO_FR_PNT", chgWoFrPnt); //장입지시FromPoint
				jrParam.setField("CHG_WO_TO_PNT", chgWoToPnt); //장입지시ToPoint
	
				BlYsDao.update(jrParam, "com.inisteel.cim.ys.bl.dao.BlYsDAO.updBlYdStockChgNo", logId, methodNm, "대형압연지시 확정 저장품 수정 (신규 압연지시)");

	
				/**********************************************************
				* 3.3 후판압연지시 확정이면 장입대상품만 저장품제원 전문을 전송
				**********************************************************/
				//저장품제원(YSN1L002) 전문 생성
				jrParam.setField("YD_INFO_SYNC_CD", "5" ); //야드정보동기화코드(지정저장품)
				
				//jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN1L002ChgWo", jrParam));
				
				//크레인작업계획 전송을 위하여
				endGp = "*";
			}

			/**********************************************************
			* 4. 크레인작업계획(YSN1L007) 전문을 생성
			**********************************************************/
// 9/11 막음 : 윤차장 요청			
//			if ("*".equals(endGp)) {
//				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN1L007", jrParam));
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
		String methodNm = "저장품 수정[BlYsL3RcvSeEJB.updStock] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			//저장품 등록
			BlYsDao.insert(jrParam, "com.inisteel.cim.ys.bl.dao.BlYsDAO.insBlYdStock", logId, methodNm, "저장품 등록");
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : BLOOM충당실적(PAYSJ001)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPAYSJ001(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "BLOOM충당실적[BlYsL3RcvSeEJB.rcvPAYSJ001] < " + rcvMsg.getResultMsg();
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
				//throw new Exception("작업계상일자1(WRK_HDS_DD1) 값이 없습니다.");
				commUtils.printLog(logId, "[" + methodNm + "] 작업계상일자1(WRK_HDS_DD1) 값이 없습니다.", "SL");
				return null;
			} else if ("".equals(stepNo)) {
				//throw new Exception("차수1(STEP_NO1) 값이 없습니다.");
				commUtils.printLog(logId, "[" + methodNm + "] 차수1(STEP_NO1) 값이 없습니다.", "SL");
				return null;
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
			//CARRY_OUT 시점에 저장픔 YD_RCPT_DATE UPDATE
			jrParam.setField("CARRY_OUT"	    , "N"       ); 
			JDTORecordSet jsStl = commDao.select(jrParam, "com.inisteel.cim.ys.bl.dao.BlYsDAO.getPAYSJ001Stl", logId, methodNm, "BLOOM충당실적 조회");

			if (jsStl != null && jsStl.size() > 0) {
				int stlSh = jsStl.size(); //재료매수

				for (int ii = 0; ii < stlSh; ii++) {
					jrParam.setField("SSTL_NO", commUtils.trim(jsStl.getRecord(ii).getFieldString("SSTL_NO"))); //재료번호

					//저장품 등록 (※ BLOOM충당실적과 BLOOM이송지시가 동시에 같은 재료번호로 수신되어 DeadLock이 발생하여 EJB Call실행으로 변경)
					EJBConnector tranConn = new EJBConnector("default", "BlYsL3RcvSeEJB", this);
					tranConn.trx("updStock", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				}
			} else {
				//throw new Exception("목적충당여재처리이력 테이블(TB_PB_ORDERTRANSMATCHLOG)에 충당할 재료가 조회되지 않습니다.");
				commUtils.printLog(logId, "[" + methodNm + "] 목적충당여재처리이력 테이블(TB_PB_ORDERTRANSMATCHLOG)에 충당할 재료가 조회되지 않습니다.", "SL");
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
	 *      [A] 오퍼레이션명 : BLOOM이송지시(PAYSJ002)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPAYSJ002(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "BLOOM이송지시[BlYsL3RcvSeEJB.rcvPAYSJ002] < " + rcvMsg.getResultMsg();
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

			JDTORecordSet jsStl = commDao.select(jrParam, "com.inisteel.cim.ys.bl.dao.BlYsDAO.getPAYSJ002Stl", logId, methodNm, "BLOOM이송지시 조회");

			if (jsStl != null && jsStl.size() > 0) {
				int stlSh = jsStl.size(); //재료매수

				for (int ii = 0; ii < stlSh; ii++) {
					jrParam.setField("SSTL_NO", commUtils.trim(jsStl.getRecord(ii).getFieldString("SSTL_NO"))); //재료번호

					//저장품 등록 (※ BLOOM충당실적과 BLOOM이송지시가 동시에 같은 재료번호로 수신되어 DeadLock이 발생하여 EJB Call실행으로 변경)
					EJBConnector tranConn = new EJBConnector("default", "BlYsL3RcvSeEJB", this);
					tranConn.trx("updStock", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				}

				//소재이송지시 Update
				commDao.update(jrParam, "com.inisteel.cim.ys.bl.dao.BlYsDAO.updPAYSJ002Stl", logId, methodNm, "소재이송지시 Update");
				
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