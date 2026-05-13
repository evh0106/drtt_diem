/**
 * @(#)SlabYdL3RcvSeEJBBean
 *
 * @version          V1.00
 * @author           허철호
 * @date             2012/11/22
 *
 * @description      Slab야드 L3수신 처리
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2012/11/22   허철호      허철호      최초 등록
 */
package com.inisteel.cim.yd.slabyd.session;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdSlabUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.plate.PlateGdsYdUtil;
import com.inisteel.cim.yd.pSlabYd.dao.PSlabYdCommDAO;
import com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO;
import com.inisteel.cim.yd.slabyd.dao.SlabYdL3RcvDAO;
import com.inisteel.cim.yd.common.dao.YdPlateCommDAO;
import com.inisteel.cim.yd.common.dao.ydEqpDao.YdEqpDao;
import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
//yd\src\com\inisteel\cim\yd\common\dao\ydCrnSchDao\YdCrnSchDao.java
//d\src\com\inisteel\cim\yd\common\dao\ydEqpDao
import com.inisteel.cim.ydPI.dao.YdPICommDAO;
import com.inisteel.cim.yd.message.MessageSenderTalk;

/**
 *      [A] 클래스명 : Slab야드 L3수신 처리
 *
 * @ejb.bean name="SlabYdL3RcvSeEJB" jndi-name="SlabYdL3RcvSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
*/

public class SlabYdL3RcvSeEJBBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;
	private YdSlabUtils  slabUtils = new YdSlabUtils();
	private SlabYdComm    slabComm = new SlabYdComm();
	private SlabYdCommDAO  commDao = new SlabYdCommDAO();
	private SlabYdL3RcvDAO rcv3Dao = new SlabYdL3RcvDAO();
	private YdEqpDao ydeqpDao = new YdEqpDao();
	private YdCrnSchDao ydcrnschDao= new YdCrnSchDao();
	private YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
	
	private PSlabYdCommDAO  PcommDao = new PSlabYdCommDAO();
	
	
		
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	/***************************************************************************
	 * 연주조업(CS)
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 연주전단실적(CSYDJ001)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvCSYDJ001(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "연주전단실적[SlabYdL3RcvSeEJB.rcvCSYDJ001] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");
			//수신 항목 값
			String msgId    = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String stlNo    = slabUtils.trim(rcvMsg.getFieldString("STL_NO"    )); //재료번호
			String modifier = slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(stlNo) || stlNo.length() < 9) {
				throw new Exception("잘못된 재료번호[" + stlNo + "] 입니다.");
			}

			/**********************************************************
			* 2. 저장품 등록
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("V_STL_NO"  , stlNo   ); //재료번호
			jrParam.setField("V_MODIFIER", modifier); //수정자

			//저장품 등록
			commDao.insSlabYd("Stock", jrParam);

			/**********************************************************
			* 3. 저장품제원 전문을 전송(C연주, 항만야드)
			**********************************************************/
			//C연주
			jrParam.setField("V_YD_GP"          , "A"); //야드구분
			jrParam.setField("V_YD_INFO_SYNC_CD", "A"); //야드정보동기화코드(생산실적)
			//저장품제원(YDY1L002 or YDY3L002) 전송Data 조회
			JDTORecord jrRtn = slabUtils.addSndData(commDao.getMsgL2("YDY1L002", jrParam));
			
			//항만슬라브야드 기능추가 - 2016.03.09 LEEJY  --> 삭제(2016.03.11) : 영차도착시 전송함
			//jrParam.setField("V_YD_GP"          , "M"); //야드구분
			//jrParam.setField("V_YD_INFO_SYNC_CD", "A"); //야드정보동기화코드(생산실적)
			//저장품제원(YDE7L002) 전송Data 조회
			//jrRtn = slabUtils.addSndData(jrRtn,commDao.getMsgL2("YDE7L002", jrParam));

			/**********************************************************
			* 4. B연주 전단실적 처리
			**********************************************************/
			slabUtils.printLog(logId, "연주전단실적 AB열연야드 수신 처리[JNDISlabReg.procCcFsWr] 시작", "SL");
			
			
				/***************************************************************
				 * B열연 신규모듈 적용 여부 
				 **************************************************************/
				String sBSLAB_EFF_YN = "N";
				String sBCOIL_EFF_YN = "N";
	
				YdPlateCommDAO commDao = new YdPlateCommDAO();
				JDTORecord jrResult = commDao.getNewModuleEffYn();
				
				sBSLAB_EFF_YN = slabUtils.nvl(jrResult.getFieldString("BSLAB_EFF_YN"),"N");
				sBCOIL_EFF_YN = slabUtils.nvl(jrResult.getFieldString("BCOIL_EFF_YN"),"N");
				
				slabUtils.printLog(logId,"YdPlateCommDAO.getNewModuleEffYn()---[[[ B열연SLAB야드신규적용:" + sBSLAB_EFF_YN + " ,B열연COIL야드신규적용:" + sBCOIL_EFF_YN + " ]]]---", "SL");
				
				if(sBSLAB_EFF_YN.equals("Y")) {
					//B열연  신규모듈 적용
					EJBConnector abConn = new EJBConnector("default", "YmCommEJB", this);
					abConn.trx("rcvInterface", new Class[] { JDTORecord.class }, new Object[] { rcvMsg });
				} else  {
					//기존모듈 호출
					EJBConnector abConn = new EJBConnector("default", "JNDISlabReg", this);
					abConn.trx("procCcFsWr", new Class[] { JDTORecord.class }, new Object[] { rcvMsg });
				}
			
				
			slabUtils.printLog(logId, "연주전단실적 AB열연야드 수신 처리[JNDISlabReg.procCcFsWr] 종료", "SL");

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 연주Scarfing실적(CSYDJ002)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvCSYDJ002(JDTORecord rcvMsg) throws DAOException {
		String methodNm = rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			
			//수신 항목 값
			String msgId    = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String stlNo    = slabUtils.trim(rcvMsg.getFieldString("STL_NO"    )); //재료번호
			String modifier = slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			if ("CSYDJ002".equals(msgId)) {
				methodNm = "연주Scarfing실적[SlabYdL3RcvSeEJB.rcvCSYDJ002] < " + methodNm;
			} else if ("QMYDJ003".equals(msgId)) {
				methodNm = "품질Scarfing대상재변경[SlabYdL3RcvSeEJB.rcvQMYDJ003] < " + methodNm;
			} else if ("QMYDJ004".equals(msgId)) {
				methodNm = "품질Scarfing실적[SlabYdL3RcvSeEJB.rcvCSYDJ002] < " + methodNm;
			}

			slabUtils.printLog(logId, methodNm, "S+");

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(stlNo) || stlNo.length() < 9) {
				throw new Exception("잘못된 재료번호[" + stlNo + "] 입니다.");
			}

			/**********************************************************
			* 2. 저장품 등록
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("V_STL_NO"  , stlNo   ); //재료번호
			jrParam.setField("V_MODIFIER", modifier); //수정자

			//저장품 등록
			commDao.insSlabYd("Stock", jrParam);

			/**********************************************************
			* 3. 저장품제원 전문을 전송
			**********************************************************/
			jrParam.setField("V_YD_GP"          , "A"); //야드구분
			jrParam.setField("V_YD_INFO_SYNC_CD", "5"); //야드정보동기화코드(지정저장품)

			//저장품제원 전송Data 조회
			JDTORecord jrRtn = slabUtils.addSndData(commDao.getMsgL2("YDY1L002", jrParam));


			//항만슬라브야드 기능추가 : 저장품제원 전송  - 2016.02.15 LEEJY 
			//항만슬라브야드 기능추가 - 2016.03.09 LEEJY  --> 삭제(2016.03.11) : 야드구분자 필요
			//jrParam.setField("V_YD_GP"          , "M"); //야드구분:항만
			//jrRtn = slabUtils.addSndData(jrRtn,commDao.getMsgL2("YDE7L002", jrParam));

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
		
	/**
	 *      [A] 오퍼레이션명 : 연주정정실적(CSYDJ003)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvCSYDJ003(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "연주정정실적[SlabYdL3RcvSeEJB.rcvCSYDJ003] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");
			
			//수신 항목 값
			String msgId        = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String stlNo        = slabUtils.trim(rcvMsg.getFieldString("STL_NO"        )); //재료번호
			String parentSlabNo = slabUtils.trim(rcvMsg.getFieldString("PARENT_SLAB_NO")); //모Slab번호
			String modifier     = slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER"    )); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(stlNo) || stlNo.length() < 9) {
				throw new Exception("잘못된 재료번호[" + stlNo + "] 입니다.");
			} else if (!"".equals(parentSlabNo) && parentSlabNo.length() < 9) {
				throw new Exception("잘못된 모Slab번호[" + parentSlabNo + "] 입니다.");
			}

			/**********************************************************
			* 2. 모Slab번호 저장품 Table 종료 처리
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("V_MODIFIER", modifier); //수정자

			if (!"".equals(parentSlabNo) && !stlNo.equals(parentSlabNo)) {
				jrParam.setField("V_STL_NO", parentSlabNo); //재료번호(모Slab번호)

				//저장품 Table 삭제
				commDao.updDelYn("Stock", jrParam);
			}

			/**********************************************************
			* 3. 저장품 등록 - 아래는 연주전단실적 수신처리와 동일
			**********************************************************/
			jrParam.setField("V_STL_NO", stlNo); //재료번호

			//저장품 등록
			commDao.insSlabYd("Stock", jrParam);

			/**********************************************************
			* 4. 저장품제원 전문을 전송
			**********************************************************/
			
			//재료 저장위치정보 조회
			String stlLocYdGp = "";
			jrParam.setField("V_STL_NO", stlNo); //재료번호
			
			//저장위치 조회
			JDTORecordSet jsChk = commDao.getStrLocInfo("Stock", jrParam);
			
			if (jsChk.size() > 0) {
				stlLocYdGp       = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_STK_COL_GP"        ));	//야드구분
				
				if(!"".equals(stlLocYdGp)) {
					stlLocYdGp = stlLocYdGp.substring(0,1);
				}
			}

			String ydGp = null;
			
			if("".equals(stlLocYdGp)) {
				ydGp = "A"; //연주야드
				
				if (stlNo.length() > 9) {
					//2차절단완료
					ydGp = "D";	//후판Slab야드
				}
			}
			
			else {
				if(stlLocYdGp.equals("A")) {
					ydGp = "A";
				}
				else if(stlLocYdGp.equals("D")) {
					ydGp = "D";
				}
			}

			JDTORecord jrRtn = null;
			
			if(ydGp != null) {
				jrParam.setField("V_YD_INFO_SYNC_CD", "A" ); //야드정보동기화코드(생산실적)
				jrParam.setField("V_YD_GP"          , ydGp); //야드구분
	
				//저장품제원(YDY1L002 or YDY3L002) 전송Data 조회
				jrRtn = slabUtils.addSndData(commDao.getMsgL2("YDY1L002", jrParam));
			}

			//항만슬라브야드 기능추가 : 저장품제원 전송 - 2016.03.15 LEEJY 
			//jrParam.setField("V_YD_GP"          , "M"); //야드항만
			//jrRtn = slabUtils.addSndData(jrRtn,commDao.getMsgL2("YDE7L002", jrParam));

			/**********************************************************
			* 5. Hand S/F후(AA01 Span) 정정마감 4매 이상 적치된 재료
			*    자동 Carry-Out 작업예약 등록 
			**********************************************************/
			if ("A".equals(ydGp)) {
				jrRtn = slabUtils.addSndData(jrRtn, this.insHsCoWrkBook(jrParam));
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : Hand스카핑인출 작업예약등록
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord jrParam
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord insHsCoWrkBook(JDTORecord jrParam) throws DAOException {
		String methodNm = "Hand스카핑인출 작업예약등록[SlabYdL3RcvSeEJB.insHsCoWrkBook] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			String modifier   = slabUtils.trim(jrParam.getFieldString("V_MODIFIER")); //수정자
			String ydSchCd    = "AASA01LM"; //야드스케쥴코드(A동 Hand스카핑 인출)
			String ydSchPrior = "";	//야드스케쥴우선순위
			String ydEqpId    = "";	//야드설비ID

			/**********************************************************
			* 1. 스케줄코드 Check
			**********************************************************/
			jrParam.setField("V_YD_SCH_CD", ydSchCd); //야드스케쥴코드

			//야드스케쥴금지유무 조회
			JDTORecordSet jsChk = commDao.getStat("SchCd", jrParam);

			if (jsChk.size() > 0) {
				if ("Y".equals(slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_SCH_PROH_EXN")))) {
					//스케줄 금지여부 Check
					slabUtils.printLog(logId, "Hand스카핑인출 스케쥴코드[" + ydSchCd + "] 기동금지 상태 입니다.", "SL");
					slabUtils.printLog(logId, methodNm, "S-");
					return null;
				}

				ydSchPrior = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_SCH_PRIOR"));	//야드스케쥴우선순위
				ydEqpId    = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_ID"   ));	//야드설비ID
			} else {
				//스케줄기준 Table 정보 Check
				slabUtils.printLog(logId, "Hand스카핑인출 스케쥴코드[" + ydSchCd + "] 정보가 없습니다.", "SL");
				slabUtils.printLog(logId, methodNm, "S-");
				return null;
			}

			/**********************************************************
			* 2. 작업예약 대상재료 조회
			**********************************************************/
			//대상재 조회
			JDTORecordSet jsWbMtl = rcv3Dao.getCSYDJ003("WM", jrParam);

			//작업예약재료매수
			int wbMtlSh = jsWbMtl.size();
			
			if (wbMtlSh <= 0) {
				slabUtils.printLog(logId, "Hand스카핑인출 작업예약 대상재가 없습니다.", "SL");
				slabUtils.printLog(logId, methodNm, "S-");
				return null;
			}

			/**********************************************************
			* 3. 작업예약 등록
			**********************************************************/
			//작업예약ID 생성
			String ydWbookId = commDao.getSeqId(logId, methodNm, "WrkBook");

			if ("".equals(ydWbookId)) {
				throw new Exception("작업예약ID 생성 실패");
			}

			//작업예약 등록
			jrParam.setField("V_YD_WBOOK_ID"       , ydWbookId ); //야드작업예약ID
			jrParam.setField("V_MODIFIER"          , modifier  ); //수정자
			jrParam.setField("V_YD_GP"             , "A"       ); //야드구분
			jrParam.setField("V_YD_BAY_GP"         , "A"       ); //야드동구분
			jrParam.setField("V_YD_SCH_CD"         , ydSchCd   ); //야드스케쥴코드
			jrParam.setField("V_YD_SCH_PRIOR"      , ydSchPrior); //야드스케쥴우선순위
			jrParam.setField("V_YD_SCH_PROG_STAT"  , "W"       ); //야드스케쥴진행상태(스케줄수행대기)
			jrParam.setField("V_YD_SCH_ST_GP"      , "A"       ); //야드스케쥴기동구분(자동)
			jrParam.setField("V_YD_SCH_REQ_GP"     , "H"       ); //야드스케쥴요청구분(Hand S/F)
			jrParam.setField("V_YD_AIM_YD_GP"      , "A"       ); //야드목표야드구분
			jrParam.setField("V_YD_AIM_BAY_GP"     , "A"       ); //야드목표동구분
			jrParam.setField("V_YD_TO_LOC_DCSN_MTD", "S"       ); //야드TO위치결정방법(스케줄지정)

			commDao.insSlabYd("WrkBook", jrParam);

			//작업예약재료 등록
			String[][] wmParam = new String[wbMtlSh][8];
			JDTORecord jrRow = null;
			
			for (int ii = 0; ii < wbMtlSh; ii++) {
				jrRow = jsWbMtl.getRecord(ii);
				
				wmParam[ii][0] = ydWbookId;													//야드작업예약ID
				wmParam[ii][1] = slabUtils.trim(jrRow.getFieldString("STL_NO"        ));	//재료번호
				wmParam[ii][2] = modifier;													//등록자
				wmParam[ii][3] = modifier;													//수정자
				wmParam[ii][4] = slabUtils.trim(jrRow.getFieldString("YD_STK_COL_GP" ));	//야드적치열구분
				wmParam[ii][5] = slabUtils.trim(jrRow.getFieldString("YD_STK_BED_NO" ));	//야드적치Bed번호
				wmParam[ii][6] = slabUtils.trim(jrRow.getFieldString("YD_STK_LYR_NO" ));	//야드적치단번호
				wmParam[ii][7] = slabUtils.trim(jrRow.getFieldString("YD_UP_COLL_SEQ"));	//야드권상모음순서
			}
			
			commDao.upsBatch("WrkBookMtl", wmParam, logId, methodNm);

			/**********************************************************
			* 4. 크레인스케줄(YDYDJ400) 전문 조회
			**********************************************************/
			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name

			jrYdMsg.setField("YD_WBOOK_ID"  , ydWbookId); //야드작업예약ID
			jrYdMsg.setField("YD_SCH_CD"    , ydSchCd  ); //야드스케쥴코드
			jrYdMsg.setField("YD_EQP_ID"    , ydEqpId  ); //야드설비ID
			jrYdMsg.setField("YD_SCH_ST_GP" , "A"      ); //야드스케쥴기동구분(자동)
			jrYdMsg.setField("YD_SCH_REQ_GP", "H"      ); //야드스케쥴요청구분(Hand S/F)
			jrYdMsg.setField("V_MODIFIER"   , modifier ); //수정자

			JDTORecord jrRtn = slabUtils.addSndData(slabComm.getCrnSchMsg(jrYdMsg));
			
			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/***************************************************************************
	 * 생산통제(CT)
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 연주전단지시확정(CTYDJ011) : 처리 내용 없음
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvCTYDJ011(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "연주전단지시확정[SlabYdL3RcvSeEJB.rcvCTYDJ011] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");
			slabUtils.printLog(logId, "처리 내용 없음 : 연주Machine코드[" + slabUtils.trim(rcvMsg.getFieldString("CC_MC_CD")) + "]", "SL");
			slabUtils.printLog(logId, methodNm, "S-");
			return null;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : 주편재설계확정지시(CTYDJ012) : 처리 내용 없음
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvCTYDJ012(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "주편재설계확정지시[SlabYdL3RcvSeEJB.rcvCTYDJ012] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");
			slabUtils.printLog(logId, "처리 내용 없음 : 재료번호[" + slabUtils.trim(rcvMsg.getFieldString("STL_NO")) + "]", "SL");
			slabUtils.printLog(logId, methodNm, "S-");
			return null;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : 외판행선변경확정(CTYDJ013) : 삭제
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvCTYDJ013(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "외판행선변경확정[SlabYdL2RcvSeEJB.rcvCTYDJ013] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");
			slabUtils.printLog(logId, "삭제된 전문입니다.", "SL");
			slabUtils.printLog(logId, methodNm, "S-");
			return null;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 후판압연사양확정(CTYDJ021) : 처리 내용 없음
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvCTYDJ021(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "후판압연사양확정[SlabYdL3RcvSeEJB.rcvCTYDJ021] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");
			slabUtils.printLog(logId, "처리 내용 없음 : 조업공장구분[" + slabUtils.trim(rcvMsg.getFieldString("PTOP_PLNT_GP")) + "]", "SL");
			slabUtils.printLog(logId, methodNm, "S-");
			return null;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : (후판,C열연)압연지시확정(CTYDJ031, CTYDJ033)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvCTYDJ031(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "압연지시확정[SlabYdL3RcvSeEJB.rcvCTYDJ031] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		String rcvSeq	  = slabUtils.trim(rcvMsg.getFieldString("CT_RCV_SEQ"   )); //지시ID(NEW)
		String ptopPlntGp = slabUtils.trim(rcvMsg.getFieldString("PTOP_PLNT_GP" )); //조업공장구분
		String msgId      = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
		
		//수신 항목 값
		String modGp      = slabUtils.trim(rcvMsg.getFieldString("MOD_GP"       )); //수정구분
		String chgWoFrPnt = slabUtils.trim(rcvMsg.getFieldString("CHG_WO_FR_PNT")); //장입지시FromPoint
		String chgWoToPnt = slabUtils.trim(rcvMsg.getFieldString("CHG_WO_TO_PNT")); //장입지시ToPoint
		String stlNo      = slabUtils.trim(rcvMsg.getFieldString("SLAB_NO"      )); //재료(Slab)번호
		String endGp      = slabUtils.trim(rcvMsg.getFieldString("END_GP"       )); //종료구분
		String modifier   = slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER"   )); //수정자(Backup Only)
		String woMtlCnt   = slabUtils.trim(rcvMsg.getFieldString("CT_WO_MTL_CNT")); //건수
		String millWoMd   = slabUtils.trim(rcvMsg.getFieldString("MILL_WO_MD")); //예정지시구분 :S
		
		try {
			slabUtils.printLog(logId, methodNm, "S+");
//////////////////////////////////////////
			 String APPLY_YN34   = PcommDao.PSlabApplyYn("APPLY_YN34");
//////////////////////////////////////////			
			
			String ydGp       = "";	//야드구분
			String msgId3     = "";	//크레인작업계획 I/F ID
			if ("".equals(modifier)) { modifier = msgId; }

			if ("HC".equals(ptopPlntGp)) {
				ydGp     = "A";			//야드구분(C연주야드)
				msgId3   = "YDY1L003";	//크레인작업계획 I/F ID
				methodNm = "C열연" + methodNm;
			}
			
			if("Y".equals(APPLY_YN34)){
				if ("PA".equals(ptopPlntGp)) {
					ydGp     = "D";			//야드구분(후판Slab야드)
					msgId3   = "YDY3L003";	//크레인작업계획 I/F ID
					methodNm = "1후판" + methodNm;
				} else if ("PB".equals(ptopPlntGp)) {
					ydGp     = "D";			//야드구분(후판Slab야드)
					msgId3   = "YDY3L003";	//크레인작업계획 I/F ID
					methodNm = "2후판" + methodNm;
				}
	         }
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(modGp)) {
				throw new Exception("수정구분(MOD_GP) 값이 없습니다.");
			} else if ("".equals(ptopPlntGp)) {
				throw new Exception("조업공장구분(PTOP_PLNT_GP) 값이 없습니다.");
			}

			JDTORecord jrRtn = null;
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("V_PTOP_PLNT_GP", ptopPlntGp); //조업공장구분
			jrParam.setField("V_MODIFIER"    , modifier  ); //수정자

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
				jrParam.setField("V_STL_NO", stlNo); //재료번호

				//저장품 등록
				commDao.insSlabYd("Stock", jrParam);
				
				
				/**********************************************************
				* 2.1 저장품 메시지 비우기
				**********************************************************/
				JDTORecord jrParam2 = JDTORecordFactory.getInstance().create();
				jrParam2.setField("SNDBK_GP_ETC", ""); 
				jrParam2.setField("MODIFIER", 	 "");
				jrParam2.setField("STL_NO", 	  stlNo);
				//수정
				commDao.update(jrParam2, "com.inisteel.cim.ys.common.dao.YsCommDAO.updMessage", logId, methodNm, "메시지 수정");
				
				/**********************************************************
				* 2.2 저장품제원, 크레인작업계획 전문을 전송
				**********************************************************/
				jrParam.setField("V_YD_GP"          , ydGp); //야드구분
				jrParam.setField("V_YD_INFO_SYNC_CD", "5" ); //야드정보동기화코드(지정저장품)

				//저장품제원(YDY1L002 or YDY3L002) 전송Data 조회
				jrRtn = slabUtils.addSndData(commDao.getMsgL2("YDY1L002", jrParam));
				
				/**********************************************************
				* 3.2 후판압연지시 취소이면서 마지만 구분자일경우..
				**********************************************************/
				if("Y".equals(APPLY_YN34)){
					if ("YDY3L003".equals(msgId3) && "*".equals(endGp)) {
				
					//후판제품야드 압연지시확정(YDYDJ031)처리 전송
					rcvMsg.setField("JMS_TC_CD", "YDYDJ031"); //JMSTC코드
					
					jrRtn = slabUtils.addSndData(jrRtn,rcvMsg);
				   }
				}
				
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
				* 3.1 저장품 등록
				**********************************************************/
				jrParam.setField("V_CHG_WO_FR_PNT", chgWoFrPnt); //장입지시FromPoint
				jrParam.setField("V_CHG_WO_TO_PNT", chgWoToPnt); //장입지시ToPoint
	
				if(!"S".equals(millWoMd)){
					//저장품(기존지시) 수정
					rcv3Dao.updCTYDJ031("StockOld", jrParam);
				}

				//저장품(신규지시) 수정
				rcv3Dao.updCTYDJ031("StockNew", jrParam);
	
				/**********************************************************
				* 3.2 후판압연지시 확정이면 후판제품야드 압연지시확정, 후판Slab야드 저장품제원 전문을 전송
				**********************************************************/
				if("Y".equals(APPLY_YN34)){
					if ("YDY3L003".equals(msgId3)) {
					//후판제품야드 압연지시확정(YDYDJ031)처리 전송
					rcvMsg.setField("JMS_TC_CD", "YDYDJ031"); //JMSTC코드
					
					jrRtn = slabUtils.addSndData(rcvMsg);
				   }
				}
				//크레인작업계획 전송을 위하여
				endGp = "*";
			}

			/**********************************************************
			* 4. 크레인작업계획(YDY1L003, YDY3L003) 전문을 전송
			**********************************************************/
			if ("*".equals(endGp)) {
				jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2(msgId3, jrParam));
			}
			
//			/**********************************************************
//			 * 5. 작업지시 성공여부 UPDATE (TB_CT_J_MILLWOIDX)
//			 *********************************************************/
//			JDTORecord recPara2 = JDTORecordFactory.getInstance().create();
//			recPara2.setField("CT_RCV_SEQ", rcvSeq); // PK: 지시ID
//			recPara2.setField("PTOP_PLNT_GP", ptopPlntGp); // PK: 공장구분
//			recPara2.setField("WO_SND_YN2", "Y");
//			recPara2.setField("MODIFIER2", 	 msgId);
//			
//			commDao.update(recPara2, "com.inisteel.cim.yd.common.dao.YdCommDAO.updCtMillwoidxTbl", logId, methodNm, "작업지시 성공여부 update");
			
			/**********************************************************
			* 5. 생산통제 작업여부 TC전송(YDCTJ037)
			**********************************************************/
          //전송할 Data가 있으면 전송 처리
			JDTORecord recPara2 = JDTORecordFactory.getInstance().create();
			recPara2.setField("JMS_TC_CD","YDCTJ037");
			recPara2.setField("JMS_TC_CREATE_DDTT"		, YdUtils.getCurDate("yyyyMMddHHmmss"));
			
			if ("D".equals(modGp)) {
				recPara2.setField("MILL_WO_MD","C"); //취소인 경우 처리
			//}else if("I".equals(modGp) && "1".equals(woMtlCnt)){
			}else if("S".equals(millWoMd)){
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
			
			
			slabUtils.printLog(logId, methodNm, "S-");

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

				EJBConnector SMSMSG = new EJBConnector("default", "SlabYdL3RcvSeEJB", this);
				SMSMSG.trx( "rcvCTYDSMS" , new Class[] { JDTORecord.class }, new Object[] { recPara2 });
			
			} catch (Exception ex) {
				throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, ex));
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


					EJBConnector SMSMSG = new EJBConnector("default", "SlabYdL3RcvSeEJB", this);
					SMSMSG.trx( "rcvCTYDSMS" , new Class[] { JDTORecord.class }, new Object[] { recPara2 });
				
			} catch (Exception ex) {
				throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, ex));
			}
				
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	
	/**
	 *      [A] 오퍼레이션명 : 압연지시 실패 시 SMS 문자 전송 기능
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvCTYDSMS(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "C열연압연지시확정 SMS 문자 전송 기능[SlabYdL3RcvSeEJB.rcvCTYDSMS] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = null;
		
		try {
			
			slabUtils.printLog(logId, methodNm, "S+");
			
			String rcvSeq       = slabUtils.trim(rcvMsg.getFieldString("CT_RCV_SEQ"   )); 
			String ptopPlntGp   = slabUtils.trim(rcvMsg.getFieldString("PTOP_PLNT_GP" )); 
			String msgId      	= slabUtils.trim(rcvMsg.getFieldString("MODIFIER2"    )); 
			String chgWoFrPnt 	= slabUtils.trim(rcvMsg.getFieldString("CHG_WO_FR_PNT")); //장입지시FromPoint
			String chgWoToPnt 	= slabUtils.trim(rcvMsg.getFieldString("CHG_WO_TO_PNT")); //장입지시ToPoint
			String woMtlCnt   	= slabUtils.trim(rcvMsg.getFieldString("CT_WO_MTL_CNT")); //건수
			
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
//////////////////////////////////////////
			 String APPLY_YN34   = PcommDao.PSlabApplyYn("APPLY_YN34");
//////////////////////////////////////////			
	
			if("CTYDJ033".equals(msgId)){
				rtnMsg2 =msgId+ " 2열연 압연지시 야드 수신 중 오류 발생\n 지시번호:" + rcvSeq;
			}else{
				if("Y".equals(APPLY_YN34)){
					if("PA".equals(ptopPlntGp)){
						rtnMsg2 =msgId+ " 1후판 압연지시 야드 수신 중 오류 발생\n 지시번호:" + rcvSeq;
					}else{
						rtnMsg2 =msgId+ " 2후판 압연지시 야드 수신 중 오류 발생\n 지시번호:" + rcvSeq;
					}
				}
			}
			
			/**********************************************************
			* 2. SMS 전송
			**********************************************************/
			if(smsListSet != null && smsListSet.size() > 0) {
				rtnMsg = "";
				
				for(int i=0; i<smsListSet.size(); i++) {
					JDTORecord recPara = JDTORecordFactory.getInstance().create();
					//recPara.setField("FROM_PHONE_NO", "0416801616");	
					//recPara.setField("TO_PHONE_NO"  , smsListSet.getRecord(i).getFieldString("HANDPHONE_NO")); // 010-XXXX-XXXX
					//recPara.setField("TO_CONTENT"   , rtnMsg2);
					//rtnMsg = PlateGdsYdUtil.updSmsMsgSend(recPara); // SMS 송신 
					
					// 2025.09.29 SMS송신 -> 카카오톡 전환
                    MessageSenderTalk    sender = new MessageSenderTalk();				
                    String subJect = "C열연압연지시확정";
                    recPara.setField("PHONE_NUM", new String(smsListSet.getRecord(i).getFieldString("HANDPHONE_NO"))); // 010-XXXX-XXXX
                    recPara.setField("TMPL_CD", new String("CM1"));
                    recPara.setField("SND_MSG", new String("[현대제철 공지사항]\n" + rtnMsg2));
                    recPara.setField("SUBJECT", new String(subJect));
                    recPara.setField("SMS_SND_NUM", new String("0416801616"));
                    recPara.setField("RECV_ID","1524711");
                    recPara.setField("GROUP_ID","KaKao");
                    recPara.setField("PROGRAM_ID","udttalk");
					sender.sendTalk(recPara);
					
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
			 
			
			
			slabUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	

	/**
	 *      [A] 오퍼레이션명 : C열연압연지시확정(CTYDJ033)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvCTYDJ033(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "C열연압연지시확정[SlabYdL3RcvSeEJB.rcvCTYDJ033] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			//압연지시확정 처리
			return this.rcvCTYDJ031(rcvMsg);
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/***************************************************************************
	 * 열연조업(HR)
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : C열연 압연지시결번실적(HRYDJ001)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvHRYDJ001(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "C열연압연지시결번실적[SlabYdL3RcvSeEJB.rcvHRYDJ001] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");
			slabUtils.printLog(logId, "처리 내용 없음 : 재료번호[" + slabUtils.trim(rcvMsg.getFieldString("STL_NO")) + "]", "SL");
			slabUtils.printLog(logId, methodNm, "S-");
			return null;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : C열연가열로추출실적(HRYDJ002) : 처리 내용 없음
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvHRYDJ002(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "C열연가열로추출실적[SlabYdL3RcvSeEJB.rcvHRYDJ002] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");
			slabUtils.printLog(logId, "처리 내용 없음 : 재료번호[" + slabUtils.trim(rcvMsg.getFieldString("STL_NO")) + "]", "SL");
			slabUtils.printLog(logId, methodNm, "S-");
			return null;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : C열연재열재및압연오작실적(HRYDJ010)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvHRYDJ010(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "C열연재열재및압연오작실적[SlabYdL3RcvSeEJB.rcvHRYDJ010] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			/**********************************************************
			* 재열재 중 Reject Table로 야드로 오는 것만 처리해야 함
			* 1. 차량이송되는 재열재는 처리하면 안됨
			*    - HRYDJ010 전문으로 오는 재열재는 Reject Table로 운반되는 재열재라고 함(?)
			* 2. 변형재열재도  Reject Table로 처리 될 수 있는가?
			*    - 기존 소스에는 변형재열재도 처리 되도록 되어 있음
			* 3. 재열재 Carry-Out은 01 Bed에서만 가능하고 처리후 자동으로 Bed Shift 되어야 함
			**********************************************************/

			//수신 항목 값
			String msgId       = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String slabNo      = slabUtils.trim(rcvMsg.getFieldString("SLAB_NO"       )); //Slab번호
			String abOccurGpCd = slabUtils.trim(rcvMsg.getFieldString("AB_OCCUR_GP_CD")); //이상발생구분코드(1:원형재열재, 2:변형재열재)
			String modifier    = slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER"    )); //수정자(Backup Only)
			String ydStkColGp  = "AAPS01"; //야드적치열구분
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(slabNo) || slabNo.length() < 9) {
				throw new Exception("잘못된 Slab번호[" + slabNo + "] 입니다.");
			}

			if (!"1".equals(abOccurGpCd) && !"2".equals(abOccurGpCd)) {
				slabUtils.printLog(logId, "이상발생구분코드[" + abOccurGpCd + "]가 재열재가 아니므로 종료합니다.", "SL");
				slabUtils.printLog(logId, methodNm, "S-");
				return null;
			}

			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("V_STL_NO"       , slabNo    ); //재료번호
			jrParam.setField("V_YD_STK_COL_GP", ydStkColGp); //야드적치열구분
			jrParam.setField("V_MODIFIER"     , modifier  ); //수정자

			/**********************************************************
			* 2. Bed상태 조회
			**********************************************************/
			String carryOutYn = "N";	//Carry-Out가능여부
			String shiftYn    = "N";	//Shift필요여부
			String ydStkBedNo = "01";	//야드적치Bed번호
			String ydStrLoc   = "";		//야드저장위치
			int bedCnt        = 0;		//재열재인출Bed수
			JDTORecord jrChk = null;

			//Bed상태 조회
			JDTORecordSet jsChk = commDao.getStat("RehtBed", jrParam);
			
			if (jsChk.size() > 0) {
				jrChk = jsChk.getRecord(0);
				
				carryOutYn = slabUtils.trim(jrChk.getFieldString("CARRY_OUT_YN" )); //Carry-Out가능여부
				shiftYn    = slabUtils.trim(jrChk.getFieldString("SHIFT_YN"     )); //Shift필요여부
				ydStkBedNo = slabUtils.trim(jrChk.getFieldString("YD_STK_BED_NO")); //야드적치Bed번호
				ydStrLoc   = slabUtils.trim(jrChk.getFieldString("YD_STR_LOC"   )); //(기등록)저장위치
				bedCnt     = jrChk.getFieldInt("BED_CNT"); //재열재인출Bed수
			}

			if (!"".equals(ydStrLoc)) {
				throw new Exception("이미 적치되어 있는 재료 : " + slabNo + " - " + ydStrLoc);
			} else if ("".equals(ydStkBedNo) || Integer.parseInt(ydStkBedNo) > bedCnt) {
				throw new Exception("재열재 적치가능 Bed 없음 : " + ydStkColGp + " - " + bedCnt + " Bed");
			}

			/**********************************************************
			* 3. 적치단 Bed Shift 및 신규 재열재 등록, 저장품 수정
			**********************************************************/
			//적치단 재료 Shift(중간에 공Bed가 있으면)
			if ("Y".equals(shiftYn)) {
				commDao.updSlabYd("StkLyrShift", jrParam);
			}

			//신규 재열재 등록
			jrParam.setField("V_YD_STK_BED_NO"      , ydStkBedNo); //야드적치Bed번호
			jrParam.setField("V_YD_STK_LYR_NO"      , "001"     ); //야드적치단번호
			jrParam.setField("V_YD_STK_LYR_MTL_STAT", "C"       ); //야드적치단재료상태(적치중)
			
			commDao.updSlabYd("StkLyrStlNo", jrParam);
			
			//저장품 등록
			commDao.insSlabYd("Stock", jrParam);

			/**********************************************************
			* 4. 열연재열재재료정보, 저장품제원 전문을 전송
			**********************************************************/
			jrParam.setField("V_YD_GP"          , "A"); //야드구분
			jrParam.setField("V_YD_INFO_SYNC_CD", "5"); //야드정보동기화코드(지정저장품)

			//저장품제원 전송Data 조회
			JDTORecord jrRtn = slabUtils.addSndData(commDao.getMsgL2("YDY1L002", jrParam));

			//열연재열재재료정보 전송Data 조회
			jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDC3L009", jrParam));

			/**********************************************************
			* 5. 설비인출요구 전문을 전송
			*  - 01 Bed에 재열재가 오거나, 이미 적치된 재료가 있으면
			**********************************************************/
			if ("01".equals(ydStkBedNo) || "Y".equals(carryOutYn)) {
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();

				//설비인출요구
				jrYdMsg.setField("JMS_TC_CD"         , "YDYDJ410"); //JMSTC코드
				jrYdMsg.setField("JMS_TC_CREATE_DDTT", slabUtils.getDateTime14()); //JMSTC생성일시
				jrYdMsg.setField("YD_EQP_ID"         , ydStkColGp); //야드설비ID
				jrYdMsg.setField("YD_STK_BED_NO"     , "01"      ); //야드적치Bed번호
				jrYdMsg.setField("YD_SCH_ST_GP"      , "A"       ); //야드스케쥴기동구분(Auto)
				jrYdMsg.setField("V_MODIFIER"        , modifier  ); //수정자
				
				//전송할 전문에 추가
				jrRtn = slabUtils.addSndData(jrRtn, jrYdMsg);
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/***************************************************************************
	 * 후판조업(PR,PP)
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 1후판압연지시결번실적(PRYDJ001)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPRYDJ001(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "1후판압연지시결번실적[SlabYdL3RcvSeEJB.rcvPRYDJ001] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");
			slabUtils.printLog(logId, "처리 내용 없음 : 재료번호[" + slabUtils.trim(rcvMsg.getFieldString("STL_NO")) + "]", "SL");
			slabUtils.printLog(logId, methodNm, "S-");
			return null;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : 2후판압연지시결번실적(PPYDJ001)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPPYDJ001(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "2후판압연지시결번실적[SlabYdL3RcvSeEJB.rcvPPYDJ001] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");
			slabUtils.printLog(logId, "처리 내용 없음 : 재료번호[" + slabUtils.trim(rcvMsg.getFieldString("STL_NO")) + "]", "SL");
			slabUtils.printLog(logId, methodNm, "S-");
			return null;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 후판압연실적(PRYDJ002, PPYDJ002)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPRYDJ002(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "후판압연실적[SlabYdL3RcvSeEJB.rcvPRYDJ002] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");
//////////////////////////////////////////
			 String APPLY_YN34   = PcommDao.PSlabApplyYn("APPLY_YN34");
//////////////////////////////////////////
	
	     if("Y".equals(APPLY_YN34)){
	    	
          }else{
        	  return null; 
          }

			//수신 항목 값
			String msgId        = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String stlNo        = slabUtils.trim(rcvMsg.getFieldString("STL_NO"        )); //재료번호
			String ptopPlntGp   = slabUtils.trim(rcvMsg.getFieldString("PTOP_PLNT_GP"  )); //조업공장구분
			String reheatSlabGp = slabUtils.trim(rcvMsg.getFieldString("REHEAT_SLAB_GP")); //재열재구분
			String modifier     = slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER"    )); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			//조업공장구분 결정 : 생산통제 압연지시확정 전문 수신 시 값이 있음
			if ("".equals(ptopPlntGp) && ptopPlntGp.length() != 2) {
				if (msgId.startsWith("PR")) {
					ptopPlntGp = "PA";	//1후판
				} else if (msgId.startsWith("PP")) {
					ptopPlntGp = "PB";	//2후판
				}
			}

			if (!"".equals(ptopPlntGp)) {
				if ("PA".equals(ptopPlntGp)) {
					methodNm = "1" + methodNm;
				} else if ("PB".equals(ptopPlntGp)) {
					methodNm = "2" + methodNm;
				}
			}

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(stlNo) || stlNo.length() < 9) {
				throw new Exception("잘못된 재료번호[" + stlNo + "] 입니다.");
			} else if ("".equals(ptopPlntGp)) {
				throw new Exception("조업공장구분 값을 알 수 없습니다.");
			}

			if (!"1".equals(reheatSlabGp)) {
				slabUtils.printLog(logId, "원형재열재[재열재구분:" + reheatSlabGp + "]가 아니므로 종료합니다.", "SL");
				slabUtils.printLog(logId, methodNm, "S-");
				return null;
			}

			/**********************************************************
			* 2. 저장품 수정
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("V_MODIFIER", modifier); //수정자
			jrParam.setField("V_STL_NO"  , stlNo   ); //재료번호

			//저장품 등록
			commDao.insSlabYd("Stock", jrParam);

			/**********************************************************
			* 3. 저장품제원 전문을 전송
			**********************************************************/
			jrParam.setField("V_YD_GP"          , "D"); //야드구분
			jrParam.setField("V_YD_INFO_SYNC_CD", "5"); //야드정보동기화코드(지정저장품)

			//저장품제원 전송Data 조회
			JDTORecord jrRtn = slabUtils.addSndData(commDao.getMsgL2("YDY3L002", jrParam));

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : 2후판압연실적(PPYDJ002)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPPYDJ002(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "2후판압연실적[SlabYdL3RcvSeEJB.rcvPPYDJ002] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
//////////////////////////////////////////
			 String APPLY_YN34   = PcommDao.PSlabApplyYn("APPLY_YN34");
//////////////////////////////////////////
	
	     if("Y".equals(APPLY_YN34)){
	    	
         }else{
       	  return null; 
         }
			//후판압연실적 처리
			return this.rcvPRYDJ002(rcvMsg);
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 후판슬라브분할실적(PRYDJ003) : 삭제
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvPRYDJ003(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "후판슬라브분할실적[SlabYdL3RcvSeEJB.rcvPRYDJ003] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");
			slabUtils.printLog(logId, "삭제된 전문입니다.", "SL");
			slabUtils.printLog(logId, methodNm, "S-");
			return null;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/***************************************************************************
	 * 공정계획(PM)
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 슬라브충당실적(PMYDJ001)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPMYDJ001(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "슬라브충당실적[SlabYdL3RcvSeEJB.rcvPMYDJ001] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");
			
			//수신 항목 값
			String msgId    = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String wrkHdsDd = slabUtils.trim(rcvMsg.getFieldString("WRK_HDS_DD1")); //작업계상일자1
			String stepNo   = slabUtils.trim(rcvMsg.getFieldString("STEP_NO1"   )); //차수1
			String modifier = slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER" )); //수정자(Backup Only)
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

			jrParam.setField("V_WRK_HDS_DD", wrkHdsDd); //작업계상일자
			jrParam.setField("V_STEP_NO"   , stepNo  ); //차수
			jrParam.setField("V_MODIFIER"  , modifier); //수정자

			JDTORecordSet jsStl = rcv3Dao.getPMYDJ001("Stl", jrParam);

			if (jsStl != null && jsStl.size() > 0) {
				int stlSh = jsStl.size(); //재료매수

				for (int ii = 0; ii < stlSh; ii++) {
					jrParam.setField("V_STL_NO", slabUtils.trim(jsStl.getRecord(ii).getFieldString("STL_NO"))); //재료번호

					//저장품 등록 (※ 슬라브충당실적과 슬라브이송지시가 동시에 같은 재료번호로 수신되어 DeadLock이 발생)
					EJBConnector tranConn = new EJBConnector("default", "SlabYdL3RcvSeEJB", this);
					tranConn.trx("updStock", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				}
			} else {
				//throw new Exception("충당할 재료가 존재하지 않습니다.");
				slabUtils.printLog(logId, "충당할 재료가 존재하지 않습니다.", "SL");
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 슬라브이송지시(PMYDJ002)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPMYDJ002(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "슬라브이송지시[SlabYdL3RcvSeEJB.rcvPMYDJ002] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");
			
			//수신 항목 값
			String msgId            = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String frtomoveWordDate = slabUtils.trim(rcvMsg.getFieldString("FRTOMOVE_WORD_DATE1")); //이송작업지시일자1
			String transwordSeqNo   = slabUtils.trim(rcvMsg.getFieldString("TRANSWORD_SEQNO1"   )); //이송지시차수1
			String modifier         = slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER"         )); //수정자(Backup Only)
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

			jrParam.setField("V_FRTOMOVE_WORD_DATE", frtomoveWordDate); //이송작업지시일자1
			jrParam.setField("V_TRANSWORD_SEQNO"   , transwordSeqNo  ); //이송지시차수1
			jrParam.setField("V_MODIFIER"          , modifier        ); //수정자

			JDTORecordSet jsStl = rcv3Dao.getPMYDJ002("Stl", jrParam);

			if (jsStl != null && jsStl.size() > 0) {
				int stlSh = jsStl.size(); //재료매수

				for (int ii = 0; ii < stlSh; ii++) {
					jrParam.setField("V_STL_NO", slabUtils.trim(jsStl.getRecord(ii).getFieldString("STL_NO"))); //재료번호

					//저장품 등록 (※ 슬라브충당실적과 슬라브이송지시가 동시에 같은 재료번호로 수신되어 DeadLock이 발생)
					EJBConnector tranConn = new EJBConnector("default", "SlabYdL3RcvSeEJB", this);
					tranConn.trx("updStock", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				}

				//소재이송지시 Update
				rcv3Dao.updPMYDJ002("FM", jrParam);
			} else {
				//throw new Exception("이송지시 재료가 존재하지 않습니다.");
				slabUtils.printLog(logId, "이송지시 재료가 존재하지 않습니다.", "SL");
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 슬라브진행변경(PMYDJ003)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvPMYDJ003(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "슬라브진행변경[SlabYdL3RcvSeEJB.rcvPMYDJ003] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");
			
			//수신 항목 값
			String msgId    = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String stlNo    = slabUtils.trim(rcvMsg.getFieldString("STL_NO"    )); //재료번호
			String modifier = slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(stlNo) || stlNo.length() < 9) {
				throw new Exception("잘못된 재료번호[" + stlNo + "] 입니다.");
			}

			/**********************************************************
			* 2. 저장품 등록
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("V_STL_NO"  , stlNo   ); //재료번호
			jrParam.setField("V_MODIFIER", modifier); //수정자

			//저장품 등록
			EJBConnector tranConn = new EJBConnector("default", "SlabYdL3RcvSeEJB", this);
			tranConn.trx("updStock", new Class[] { JDTORecord.class }, new Object[] { jrParam });

			/**********************************************************
			* 3. AB열연야드 슬라브진행변경 처리
			**********************************************************/
			slabUtils.printLog(logId, "슬라브진행변경 AB열연야드 수신 처리[JNDIInternal.receiveInternal] 시작", "SL");
			EJBConnector abConn = new EJBConnector("default", "JNDIInternal", this);
			abConn.trx("receiveInternal", new Class[] { JDTORecord.class }, new Object[] { rcvMsg });
			slabUtils.printLog(logId, "슬라브진행변경 AB열연야드 수신 처리[JNDIInternal.receiveInternal] 종료", "SL");

			slabUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
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
		String methodNm = "저장품 수정[SlabYdL3RcvSeEJB.updStock] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			
			//저장품 수정
			commDao.insSlabYd("Stock", jrParam);
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/***************************************************************************
	 * 품질관리(QM)
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : Scarfing대상재변경(QMYDJ003)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvQMYDJ003(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "품질Scarfing대상재변경[SlabYdL3RcvSeEJB.rcvQMYDJ003] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			
			//연주Scarfing실적 처리
			return this.rcvCSYDJ002(rcvMsg);
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 품질Scarfing실적(QMYDJ004)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvQMYDJ004(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "품질Scarfing실적[SlabYdL3RcvSeEJB.rcvQMYDJ004] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			
			//연주Scarfing실적 처리
			return this.rcvCSYDJ002(rcvMsg);
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	/**
	 *      [A] 오퍼레이션명 :  C2 검사장 크레인 인터락 작동 여부(QMYDJ006)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvQMYDJ006(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "C2검사장크레인 인터락작동여부[SlabYdL3RcvSeEJB.rcvQMYDJ006] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");
			
			//수신 항목 값
			String msgId    = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String interlockYN    = slabUtils.trim(rcvMsg.getFieldString("C2CR_INTERLOCK_YN"    ));//인터락 작동 여부
		    String ydEqpId =     slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"    ));//장비 ID 
		    
		    String chkinterlock = ""; // 현 작업 인터락 여부 체크용
			String chkinterlock_sect = "";//현 작업 인터락구간 여부 체크용
			
			slabUtils.printLog(logId, "장비 ["+ydEqpId+"] 인터락 여부["+interlockYN+"] 변경 시작", "SL");
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(interlockYN) || !( "Y".equals(interlockYN) || "N".equals(interlockYN)) ) {
				throw new Exception("잘못된 인터락 flag[" + interlockYN + "] 입니다.");
			}
			else if ("".equals(ydEqpId)){
				throw new Exception("장비 ID가 비었습니다.");
			}

			/**********************************************************
			* 2. 인터락 여부에 따라, TB_YD_EQP 의 C2CR_INTERLOCK_YN 값 UPDATE 
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("C2CR_INTERLOCK_YN"  , interlockYN   ); //인터락 여부
			jrParam.setField("YD_EQP_ID"  ,  ydEqpId); //장비ID
			jrParam.setField("V_YD_EQP_ID"  ,  ydEqpId); //장비ID
			
			//ydeqpDao.updYdEqpDirect(jrParam, 902); 업데이트 하지 않음(품질에서,,)

			/**********************************************************
			* 3. 인터락 여부에 따라, L2로 인터락 전문 전송
			**********************************************************/
			JDTORecord jrRtn = null;
			
			//현재 크레인 작업이 인터락 구간 작업인지 확인
			JDTORecordSet jsChk = JDTORecordFactory.getInstance().createRecordSet("YD");

			slabUtils.printLog(logId, "장비 ["+ydEqpId+"] 현 작업중인 크레인 스케줄 인터락 확인", "SL");
			ydcrnschDao.getYdCrnsch(jrParam, jsChk,701);
			
			if(jsChk != null && jsChk.size()>0){
				//chkinterlock = slabUtils.trim(jsChk.getRecord(0).getFieldString("INTERLOCK_YN"));
				chkinterlock_sect = slabUtils.trim(jsChk.getRecord(0).getFieldString("INTERLOCK_WRK_YN"));
	
				slabUtils.printLog(logId, "장비 ["+ydEqpId+"] 현 작업 인터락 구간 작업 여부["+chkinterlock_sect+"]", "SL");
				//인터락 여부 및 인터락구간 여부 모두 Y가 아니라면 l2로 바로 전문 전송.
				if(!"Y".equals(chkinterlock_sect)){
					jrRtn = slabUtils.addSndData(commDao.getMsgL2("YDY1L006", jrParam));
				}
			}
			//현재 크레인 작업이 인터락 구간 아닌 경우 L2로 바로 인터락 전문 전송
			else{
				slabUtils.printLog(logId, "장비 ["+ydEqpId+"] 현 작업 인터락 구간 작업 아니므로 L2로 바로 전문 전송", "SL");
				//C2 인터락 전문 전송
			    jrRtn = slabUtils.addSndData(commDao.getMsgL2("YDY1L006", jrParam));
			}


			slabUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/***************************************************************************
	 * 출하관리(DM) : 외판슬라브 관련 전문은 기존 수신처리 프로그램으로 처리
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 외판슬라브출하완료(DMYDR029)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvQMYDR029(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "외판슬라브출하완료[SlabYdL3RcvSeEJB.rcvQMYDR029] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			String ydGp = slabUtils.trim(rcvMsg.getFieldString("YD_GP")); //야드구분
			
			/**********************************************************
			* 0. 야드구분이 AB열연 야드이면 AB열연 일관제철내부수신 호출
			**********************************************************/
			if ("0".equals(ydGp) || "1".equals(ydGp) || "2".equals(ydGp) || "3".equals(ydGp)) {
				slabUtils.printLog(logId, "AB열연야드 외판슬라브출하완료 수신 처리[JNDIInternal.receiveInternal] 시작 >> [ 야드구분 : " + ydGp + " ]", "SL");
				EJBConnector abConn = new EJBConnector("default", "JNDIInternal", this);
				abConn.trx("receiveInternal", new Class[] { JDTORecord.class }, new Object[] { rcvMsg });
				slabUtils.printLog(logId, "AB열연야드 외판슬라브출하완료 수신 처리[JNDIInternal.receiveInternal] 종료", "SL");
				return null;
			}

			slabUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn = null;

			//수신 항목 값
			String msgId    = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String stlNo    = slabUtils.trim(rcvMsg.getFieldString("STL_NO"    )); //재료번호
			String modifier = slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(stlNo) || stlNo.length() < 9) {
				throw new Exception("잘못된 재료번호[" + stlNo + "] 입니다.");
			}

			/**********************************************************
			* 2. 저장품 등록
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("V_MODIFIER", modifier); //수정자
			jrParam.setField("V_STL_NO"  , stlNo   ); //재료번호

			//저장품 등록
			rcv3Dao.updDMYDR029(jrParam);

			/**********************************************************
			* 3. 차량에 대한 모든 재료가 출하 완료된 경우 자동출발 처리
			**********************************************************/
			//외판슬라브출하차량출발실적(DMYDR039) 수신Data 조회
			JDTORecordSet jsMsgYD = commDao.getMsgL3("DMYDR039", jrParam);

			//외판슬라브출하차량출발실적 수신처리
			if (jsMsgYD.size() > 0) {
				JDTORecord jrMsgIn = jsMsgYD.getRecord(0);
				jrMsgIn.setResultCode(logId);   //Log ID
				jrMsgIn.setResultMsg(methodNm); //Log Method Name
				jrMsgIn.setField("V_MODIFIER", modifier); //수정자

				JDTORecord jrRst = this.rcvDMYDR039(jrMsgIn);

				//전송할 전문을 추가
				jrRtn = slabUtils.addSndData(jrRtn, jrRst);
			}

			/**********************************************************
			* 4. 저장품제원 전문을 전송
			**********************************************************/
			jrParam.setField("V_YD_INFO_SYNC_CD", "A"); //야드정보동기화코드(생산실적)

			//저장품제원(YDY1L002 or YDY3L002) 전송Data 조회
			jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDY1L002", jrParam));

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 외판슬라브반품(DMYDR032)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvDMYDR032(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "외판슬라브반품[SlabYdL3RcvSeEJB.rcvDMYDR032] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			String ydGp = slabUtils.trim(rcvMsg.getFieldString("YD_GP")); //야드구분
			
			/**********************************************************
			* 0. 야드구분이 AB열연 야드이면 AB열연 일관제철내부수신 호출
			**********************************************************/
			if ("0".equals(ydGp) || "1".equals(ydGp) || "2".equals(ydGp) || "3".equals(ydGp)) {
				slabUtils.printLog(logId, "AB열연야드 외판슬라브반품 수신 처리[JNDIInternal.receiveInternal] 시작 >> [ 야드구분 : " + ydGp + " ]", "SL");
				EJBConnector abConn = new EJBConnector("default", "JNDIInternal", this);
				abConn.trx("receiveInternal", new Class[] { JDTORecord.class }, new Object[] { rcvMsg });
				slabUtils.printLog(logId, "AB열연야드 외판슬라브반품 수신 처리[JNDIInternal.receiveInternal] 종료", "SL");
				return null;
			}

			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String stlNo    = slabUtils.trim(rcvMsg.getFieldString("STL_NO"    )); //재료번호
			String modifier = slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(stlNo) || stlNo.length() < 9) {
				throw new Exception("잘못된 재료번호[" + stlNo + "] 입니다.");
			}

			/**********************************************************
			* 2. 저장품 등록 및 차량스케줄 삭제
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("V_STL_NO"            , stlNo                                                      ); //재료번호
			jrParam.setField("V_ORD_GP"            , slabUtils.trim(rcvMsg.getFieldString("ORD_GP"            ))); //수주구분
			jrParam.setField("V_CUST_CD"           , slabUtils.trim(rcvMsg.getFieldString("CUST_CD"           ))); //고객코드
			jrParam.setField("V_DEST_CD"           , slabUtils.trim(rcvMsg.getFieldString("DEST_CD"           ))); //목적지코드
			jrParam.setField("V_DEST_TEL_NO"       , slabUtils.trim(rcvMsg.getFieldString("DEST_TEL_NO"       ))); //목적지전화번호
			jrParam.setField("V_DIST_SHIPASSIGN_GP", slabUtils.trim(rcvMsg.getFieldString("DIST_SHIPASSIGN_GP"))); //출하배선지시구분
			jrParam.setField("V_YD_GP"             , ydGp                                                       ); //야드구분
			jrParam.setField("V_MODIFIER"          , modifier                                                   ); //수정자

			//저장품 등록
			rcv3Dao.updDMYDR032("ST", jrParam);

			//차량이송재료 삭제
			rcv3Dao.updDMYDR032("CM", jrParam);

			//차량스케줄 삭제
			rcv3Dao.updDMYDR032("CS", jrParam);

			/**********************************************************
			* 3. 저장품제원 전문을 전송
			**********************************************************/
			jrParam.setField("V_YD_INFO_SYNC_CD", "5"); //야드정보동기화코드(지정저장품)

			//저장품제원(YDY1L002 or YDY3L002) 전송Data 조회
			JDTORecord jrRtn = slabUtils.addSndData(commDao.getMsgL2("YDY1L002", jrParam));

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 외판슬라브출하차량출발실적(DMYDR039)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvDMYDR039(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "외판슬라브출하차량출발실적[SlabYdL3RcvSeEJB.rcvDMYDR039] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			String ydGp = slabUtils.trim(rcvMsg.getFieldString("YD_GP")); //야드구분
			
			/**********************************************************
			* 0. 야드구분이 AB열연 야드이면 AB열연 일관제철내부수신 호출
			**********************************************************/
			if ("0".equals(ydGp) || "1".equals(ydGp) || "2".equals(ydGp) || "3".equals(ydGp)) {
				slabUtils.printLog(logId, "AB열연야드 외판슬라브출하차량출발실적 수신 처리[JNDIInternal.receiveInternal] 시작 >> [ 야드구분 : " + ydGp + " ]", "SL");
				EJBConnector abConn = new EJBConnector("default", "JNDIInternal", this);
				abConn.trx("receiveInternal", new Class[] { JDTORecord.class }, new Object[] { rcvMsg });
				slabUtils.printLog(logId, "AB열연야드 외판슬라브출하차량출발실적 수신 처리[JNDIInternal.receiveInternal] 종료", "SL");
				return null;
			}

			slabUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId       = slabUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String carNo       = slabUtils.trim(rcvMsg.getFieldString("CAR_NO"        )); //차량번호
			String cardNo      = slabUtils.trim(rcvMsg.getFieldString("CARD_NO"       )); //카드번호
			String sposWlocCd  = slabUtils.trim(rcvMsg.getFieldString("SPOS_WLOC_CD"  )); //발지개소코드
			String sposYdPntCd = slabUtils.trim(rcvMsg.getFieldString("SPOS_YD_PNT_CD")); //발지야드포인트코드
			String modifier    = slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER"    )); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			//PIDEV
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "SlabYdL3RcvSeEJBBean => rcvDMYDR039 수신", "APPPI0", "S", "*");
			
//			if( "Y".equals(sApplyYnPI) ) {
				
				if ("".equals(carNo)) {
					throw new Exception("차량번호가 존재하지 않습니다.");
				} else if ("".equals(sposWlocCd)) {
					throw new Exception("발지개소코드가 존재하지 않습니다.");
				} else if ("".equals(sposYdPntCd)) {
					throw new Exception("발지야드포인트코드가 존재하지 않습니다.");
				}				
				
//			} else {
//
//				if ("".equals(carNo)) {
//					throw new Exception("차량번호가 존재하지 않습니다.");
//				} else if ("".equals(cardNo)) {
//					throw new Exception("카드번호가 존재하지 않습니다.");
//				} else if ("".equals(sposWlocCd)) {
//					throw new Exception("발지개소코드가 존재하지 않습니다.");
//				} else if ("".equals(sposYdPntCd)) {
//					throw new Exception("발지야드포인트코드가 존재하지 않습니다.");
//				}				
//				
//			}

			/**********************************************************
			* 2. 적치열 및 차량스케줄정보 조회
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			if (carNo.startsWith("ET")) { carNo = "ET"; }
			jrParam.setField("V_WLOC_CD"  , sposWlocCd ); //개소코드
			jrParam.setField("V_YD_PNT_CD", sposYdPntCd); //야드포인트코드
			jrParam.setField("V_CAR_NO"   , carNo      ); //차량번호
			jrParam.setField("V_CARD_NO"  , cardNo     ); //카드번호

			String ydStkColGp = ""; //야드적치열구분
			String ydCarSchId = ""; //야드차량스케쥴ID

			//실적처리할 적치열정보 조회
			JDTORecordSet jsStkCol = rcv3Dao.getDMYDR039(jrParam);

			if (jsStkCol != null && jsStkCol.size() > 0) {
				ydStkColGp = slabUtils.trim(jsStkCol.getRecord(0).getFieldString("YD_STK_COL_GP")); //야드적치열구분
				ydCarSchId = slabUtils.trim(jsStkCol.getRecord(0).getFieldString("YD_CAR_SCH_ID")); //야드차량스케쥴ID
			}

			//적치열정보 Check
			if ("".equals(ydStkColGp)) {
				slabUtils.printLog(logId, "실적처리할 적치열정보가 존재하지 않습니다. [ 개소코드 : " + sposWlocCd + ", 야드포인트코드 : " + sposYdPntCd + " ]", "SL");
				slabUtils.printLog(logId, methodNm, "S-");
				return null;
			}

			jrParam.setField("V_MODIFIER"     , modifier  ); //수정자
			jrParam.setField("V_YD_STK_COL_GP", ydStkColGp); //저장위치정보 수정 및 입동지시(차량정지위치) 조회용
			jrParam.setField("V_YD_CAR_SCH_ID", ydCarSchId); //차량스케줄 삭제용

			/**********************************************************
			* 2. 저장위치정보 비활성화 수정
			**********************************************************/
			//적치열 비활성화 수정
			rcv3Dao.updDMYDR039("SC", jrParam);

			//적치Bed 비활성화 수정
			rcv3Dao.updDMYDR039("SB", jrParam);

			//적치단 비활성화 수정
			rcv3Dao.updDMYDR039("SL", jrParam);

			/**********************************************************
			* 3. 출하 영차 출발 시 야드L2로 저장위치제원 전송
			**********************************************************/
			//야드정보동기화코드
			// 1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제)
			jrParam.setField("V_YD_INFO_SYNC_CD" , "3");
			jrParam.setField("V_YD_CAR_PROG_STAT", "A"); //야드차량진행상태 하차출발
			jrParam.setField("V_YD_EQP_WRK_STAT" , "L"); //야드설비작업상태 영차

			//저장위치제원(YDY1L001 or YDY3L001) 전송Data 조회
			JDTORecordSet jsSndMsg = commDao.getMsgL2("YDY1L001", jrParam);

			//전송할 전문을 추가
			JDTORecord jrRtn = slabUtils.addSndData(null, jsSndMsg);

			/**********************************************************
			* 4. 차량스케줄 삭제
			**********************************************************/
			if ("".equals(ydCarSchId)) {
				slabUtils.printLog(logId, "삭제할 차량스케줄정보가 존재하지 않습니다. [ 차량번호 : " + carNo + ", 카드번호 : " + cardNo + " ]", "SL");
			} else {
				//차량이송재료 삭제
				commDao.updDelYn("CarFtmvMtl", jrParam);

				//차량스케줄 삭제
				commDao.updDelYn("CarSch", jrParam);
			}

			/**********************************************************
			* 5. 외판슬라브출하차량 입동지시요구 전송
			*    차량정지위치에 대한 입동대기 차량들 중에서 가장 빠른 입동순서
			*    차량이 출하차량이면 입동지시 처리
			**********************************************************/
			//차량입동지시(YDDMR028) 전송Data 조회
		
			// PIDEV
//			if("Y".equals(sApplyYnPI)) {
				
				JDTORecordSet jsM10YDLMJ1061 = commDao.getMsgL3("M10YDLMJ1061A", jrParam);

				//전송할 전문을 추가
				jrRtn = slabUtils.addSndData(jrRtn, jsM10YDLMJ1061);				
				
//			} else {
//				
//				JDTORecordSet jsDM028 = commDao.getMsgL3("YDDMR028", jrParam);
//	
//				//전송할 전문을 추가
//				jrRtn = slabUtils.addSndData(jrRtn, jsDM028);
//				
//			}

			/**********************************************************
			* 6. 구내운송 소재차량Point개폐 전송
			*    출하관리와 구내운송간의 차량Point 정보공유
			*    구내운송에서 출하차량이 도착한 Point를 사용하지 않게 하기위함
			**********************************************************/
			String curDateTime = slabUtils.getDateTime14();
			JDTORecord jrTS012 = JDTORecordFactory.getInstance().create();
			jrTS012.setField("JMS_TC_CD"         , "YDTSJ012" ); //JMSTC코드
			jrTS012.setField("JMS_TC_CREATE_DDTT", curDateTime); //JMSTC생성일시
			jrTS012.setField("PRSNT_LOC_WLOC_CD" , sposWlocCd ); //현위치개소코드
			jrTS012.setField("YD_PNT_CD"         , sposYdPntCd); //야드포인트코드
			jrTS012.setField("PNT_UNIT_CL_GP"    , "O"        ); //포인트개폐구분(개)
			jrTS012.setField("YD_PNT_OP_CL_TT"   , curDateTime); //야드포인트개폐시각

			//전송할 전문을 추가
			jrRtn = slabUtils.addSndData(jrRtn, jrTS012);

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

}
