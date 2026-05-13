package com.inisteel.cim.yd.ydWkReq.MoveStackWkReq;

import java.util.Vector;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.dao.ydCrnSpecDao.YdCrnSpecDao;
import com.inisteel.cim.yd.common.dao.ydCrnWrkMtlDao.YdCrnWrkMtlDao;
import com.inisteel.cim.yd.common.dao.ydEqpDao.YdEqpDao;
import com.inisteel.cim.yd.common.dao.ydSchRuleDao.YdSchRuleDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookDao.YdWrkbookDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookMtlDao.YdWrkbookMtlDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdTcConst;
import com.inisteel.cim.yd.common.util.YdUtils;

/**
 * 이적작업요구 Session EJB
 * 
 * @ejb.bean name="MvStkWrkDmdSeEJB" jndi-name="MvStkWrkDmdSeEJB"
 *           type="Stateless" view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class MvStkWrkDmdSeEJBBean extends BaseSessionBean {

	// Session Name
	private String szSessionName = getClass().getName();

	private YdUtils ydUtils = new YdUtils();

	private YdTcConst ydTcConst = new YdTcConst();

	private YdDaoUtils ydDaoUtils = new YdDaoUtils();

	private YdDelegate ydDelegate = new YdDelegate();

	// [DEBUG] message flag
	private boolean bDebugFlag = true;

	/**
	 * ejbCrate()
	 * 
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	/**
	 * 오퍼레이션명 : A후판장입준비작업요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procAPlChgPrepWrkReq(JDTORecord msgRecord) throws JDTOException {

		// 스케줄기준 DAO
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		// 작업예약 재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		// 공용 METHOD
		YdUtils ydutils = new YdUtils();

		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메세지
		String szMsg = "";
		// METHOD명
		String szMethodName = "procAPlChgPrepWrkReq";
		// 사용자
		String szUser = "SYSTEM";

		// 레코드 선언
		JDTORecord recPara = null;
		JDTORecord recStkPara = null;
		// 레코드셋 선언
		JDTORecordSet rsResult = null;

		// 설비ID(열구분)
		String szYD_STK_COL_GP = null;
		// 적치BED번호
		String szYD_STK_BED_NO = null;
		// 재료매수(int)
		int intMtlCnt = 0;
		// 재료번호
		String[] szSTL_NO = null;
		// 권상모음순서
		String[] szYD_UP_COLL_SEQ = null;
		// 스케줄코드
		String szYD_SCH_CD = null;
		// 스케줄 금지 유무
		String szYD_SCH_PROH_EXN = null;
		// 작업크레인
		String szYD_WRK_CRN = null;
		// 대체크레인유무
		String szYD_ALT_CRN_YN = null;
		// 대체크레인
		String szYD_ALT_CRN = null;
		// 선택크레인
		String szCrn = null;
		// 작업예약ID
		String szYD_WBOOK_ID = null;

		// TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		// 에러 리턴
		if (szRcvTcCode == null) {

			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error ("
					+ szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;

		}
		// TC CODE DISPLAY
		if (bDebugFlag) {

			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		}

		try {

			// 받은 전문 편집
			// 스케줄코드
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			if (szYD_SCH_CD.equals("")) {

				szMsg = "[전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_COL_GP");
			if (szYD_STK_COL_GP.equals("")) {

				szMsg = "[전문 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_BED_NO");
			if (szYD_STK_BED_NO.equals("")) {

				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_LOT_GP_SH");
			if (intMtlCnt == 0) {

				szMsg = "[전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 재료번호
			szSTL_NO = new String[intMtlCnt + 1];
			// 권상모음순서
			szYD_UP_COLL_SEQ = new String[intMtlCnt + 1];
			;

			// 재료번호, 권상모음순서
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,
						"STL_NO" + Loop_i);
				if (szSTL_NO[Loop_i].equals("")) {

					szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				}
				// 권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,
						"YD_UP_COLL_SEQ" + Loop_i);
				if (szYD_UP_COLL_SEQ[Loop_i].equals("")) {

					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i]
							+ ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				}
			}

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal)
				return;

			// 레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();

			// 스케줄CD 체크
			// 스케줄 금지 유무
			szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara,
					"YD_SCH_PROH_EXN");
			// 작업크레인
			szYD_WRK_CRN = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
			// 대체크레인유무
			szYD_ALT_CRN_YN = ydDaoUtils.paraRecChkNull(recPara,
					"YD_ALT_CRN_YN");
			// 대체크레인
			szYD_ALT_CRN = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");

			// 스케줄 금지 유무가 "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if (szYD_SCH_PROH_EXN.equals("Y")) {

				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}

			// 작업크레인 설비 상태 체크
			blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);

			// 작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if (!blnRtnVal) {

				szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				// 대체크레인의 유무를 체크한다.
				// 대체크레인이 없으면 에러 리턴
				if (!szYD_ALT_CRN_YN.equals("Y")) {

					szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				}
				// 대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				// 대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {

					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				} else {
					// 대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					szCrn = szYD_ALT_CRN;

				}
			} else {
				// 작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				szCrn = szYD_WRK_CRN;

			}

			// 재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다.
			// 작업예약재료 등록 여부를 체크한다.
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 크레인사양과 저장품 사양을 체크(길이,폭,중량)
				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
				if (!blnRtnVal)
					return;

				// 다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if (!blnRtnVal)
					return;

			}

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if (!blnRtnVal)
				return;
			// 레코드추출
			rsResult.first();
			recPara = rsResult.getRecord();
			// 작업예약ID
			szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");

			// INSERT 항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			// 야드구분
			String szYD_GP = szYD_SCH_CD.substring(0, 1);
			// 동구분
			String szYD_BAY_GP = szYD_SCH_CD.substring(1, 2);

			// INSERT할 항목 SET
			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			recPara.setField("YD_GP", szYD_GP);
			recPara.setField("YD_BAY_GP", szYD_BAY_GP);
			recPara.setField("YD_SCH_CD", szYD_SCH_CD);
			recPara.setField("REGISTER", szUser);

			// 작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}

			// 조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();

			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			// recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			// recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", szUser);

			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 리턴 recordSet 생성
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				// 재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				blnRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C",
						rsResult);
				if (!blnRtnVal)
					return;

				// 레코드추출
				rsResult.first();
				recStkPara = rsResult.getRecord();

				// 재료번호
				recPara.setField("STL_NO", szSTL_NO[Loop_i]);
				// 적치열구분
				recPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(
						recStkPara, "YD_STK_COL_GP"));
				// 적치BED번호
				recPara.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(
						recStkPara, "YD_STK_BED_NO"));
				// 적치단번호
				recPara.setField("YD_STK_LYR_NO", ydDaoUtils.paraRecChkNull(
						recStkPara, "YD_STK_LYR_NO"));
				// 권상모음순서
				recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ[Loop_i]);

				// 작업예약재료 테이블에 등록한다.
				intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);

				if (intRtnVal < 1) {
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
			}

		} catch (Exception e) {
			szMsg = "A후판 장입 준비작업 요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}

	} // end of procAPlChgPrepWrkReq()

	/**
	 * 오퍼레이션명 : A후판공Bed확보Lot편성
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procAPlEmptyBedSecurLotComp(JDTORecord msgRecord)
			throws JDTOException {

		YdDelegate ydDelegate = new YdDelegate();
		// 공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		// 공용 METHOD
		YdUtils ydutils = new YdUtils();

		// 레코드 선언
		JDTORecord recPara = null;
		JDTORecord recOutPara = null;
		// 레코드셋 선언
		JDTORecordSet rsResult = null;

		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메세지
		String szMsg = "";
		// 메소드명
		String szMethodName = "procAPlEmptyBedSecurLotComp";
		// 사용자
		String szUser = "SYSTEM";

		// Lot 편성 재료 매수
		int intLotGpSh = 0;
		// 전문 생성 일시
		String szDate = null;

		// 적치열구분
		String szYD_STK_COL_GP = null;
		// 적치BED번호
		String szYD_STK_BED_NO = null;
		// to_적치열구분
		String szYD_STK_COL_GP_TO = null;
		// to_적치BED번호
		String szYD_STK_BED_NO_TO = null;
		// 스케줄코드
		String szYD_SCH_CD = null;
		// 재료번호
		String szSTL_NO = null;
		// 적치단 재료상태
		String szYD_STK_LYR_MTL_STAT = null;

		// TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		// 에러 리턴
		if (szRcvTcCode == null) {

			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error ("
					+ szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;

		}
		// TC CODE DISPLAY
		if (bDebugFlag) {

			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		}

		try {
			// 받은 전문 편집
			// 적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_COL_GP");
			if (szYD_STK_COL_GP.equals("")) {

				szMsg = "[전문 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_BED_NO");
			if (szYD_STK_BED_NO.equals("")) {

				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// to_적치열구분
			szYD_STK_COL_GP_TO = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_COL_GP_TO");
			if (szYD_STK_COL_GP_TO.equals("")) {

				szMsg = "[전문 이상] to_적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// to_적치BED번호
			szYD_STK_BED_NO_TO = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_BED_NO_TO");
			if (szYD_STK_BED_NO_TO.equals("")) {

				szMsg = "[전문 이상] to_적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}

			// =================================================================================
			// 수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			// BRE 등록 안됨...테스트용 스케줄코드 생성
			// 추후 구현..
			szYD_SCH_CD = "DASP01MM";
			// =================================================================================

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			// 보급 Lot 편성 대상 재료 Select
			blnRtnVal = chkGetAPlEmptyBedSecurLotGp(szYD_STK_COL_GP.trim(),
					szYD_STK_BED_NO.trim(), rsResult);
			if (!blnRtnVal)
				return;

			// Lot 편성 매수
			intLotGpSh = rsResult.size();

			// 레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			// JMS TC CODE
			recOutPara.setField("JMS_TC_CD", "YDYDJ267");
			// 레코드 커서 처음으로
			rsResult.first();

			// 보급 Lot 편성 재료 매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= intLotGpSh; Loop_i++) {

				// 레코드 추출
				recPara = rsResult.getRecord();

				// 재료번호
				szSTL_NO = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
				// 적치단재료상태
				szYD_STK_LYR_MTL_STAT = ydDaoUtils.paraRecChkNull(recPara,
						"YD_STK_LYR_MTL_STAT");

				// 적치단재료상태가 권상대기중(U)이거나 권하대기중(D)이면 에러리턴
				if (szYD_STK_LYR_MTL_STAT.trim().equals("U")
						|| szYD_STK_LYR_MTL_STAT.trim().equals("D")) {

					szMsg = "적치열구분 ("
							+ szYD_STK_COL_GP
							+ "), "
							+ "적치Bed번호 ("
							+ szYD_STK_BED_NO
							+ "), "
							+ "적치단번호 ("
							+ ydDaoUtils.paraRecChkNull(recPara,
									"YD_STK_LYR_NO") + "), " + "재료번호 ("
							+ szSTL_NO + ") " + "의 적치단재료상태가 ("
							+ szYD_STK_LYR_MTL_STAT + ") 입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				}

				// 다른 크레인 스케줄에 등록 되어 있는지 체크
				blnRtnVal = chkCrnWrkMtl(szSTL_NO);
				if (!blnRtnVal)
					return;

				// 재료번호
				recOutPara.setField("STL_NO" + Loop_i, szSTL_NO);
				// 권상모음순서
				recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, "" + Loop_i);

				// 다음 레코드로
				rsResult.next();

			}

			// 전문 발생 일시
			szDate = ydUtils.getCurDate("yyyy/MM/dd HH:mm:ss");

			// 발생 일시
			recOutPara.setField("JMS_TC_CREATE_DDTT", szDate);
			// 스케줄코드
			recOutPara.setField("YD_SCH_CD", szYD_SCH_CD);
			// Lot편성 매수
			recOutPara.setField("YD_LOT_GP_SH", "" + intLotGpSh);
			// 적치열구분
			recOutPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			// 적치BED번호
			recOutPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);

			// 전문 송신
			ydDelegate.sendMsg(recOutPara);
			szMsg = "A후판 공Bed확보 Lot 편성 후 A후판 공Bed확보 작업요구 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		} catch (Exception e) {
			szMsg = "A후판 공Bed확보 Lot 편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}

	} // end of procAPlEmptyBedSecurLotComp

	/**
	 * 오퍼레이션명 : A후판공Bed확보작업요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procAPlEmptyBedSecurWrkReq(JDTORecord msgRecord)
			throws JDTOException {

		// 스케줄기준 DAO
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		// 작업예약 재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		// 공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		// 공용 METHOD
		YdUtils ydutils = new YdUtils();

		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메세지
		String szMsg = "";
		// METHOD명
		String szMethodName = "procAPlEmptyBedSecurWrkReq";
		// 사용자
		String szUser = "SYSTEM";

		// 레코드 선언
		JDTORecord recPara = null;
		JDTORecord recStkPara = null;
		// 레코드셋 선언
		JDTORecordSet rsResult = null;

		// 설비ID(열구분)
		String szYD_STK_COL_GP = null;
		// 적치BED번호
		String szYD_STK_BED_NO = null;
		// 재료매수(int)
		int intMtlCnt = 0;
		// 재료번호
		String[] szSTL_NO = null;
		// 권상모음순서
		String[] szYD_UP_COLL_SEQ = null;
		// 스케줄코드
		String szYD_SCH_CD = null;
		// 스케줄 금지 유무
		String szYD_SCH_PROH_EXN = null;
		// 작업크레인
		String szYD_WRK_CRN = null;
		// 대체크레인유무
		String szYD_ALT_CRN_YN = null;
		// 대체크레인
		String szYD_ALT_CRN = null;
		// 선택크레인
		String szCrn = null;
		// 작업예약ID
		String szYD_WBOOK_ID = null;

		// TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		// 에러 리턴
		if (szRcvTcCode == null) {

			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error ("
					+ szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;

		}
		// TC CODE DISPLAY
		if (bDebugFlag) {

			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		}

		try {

			// 받은 전문 편집
			// 스케줄코드
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			if (szYD_SCH_CD.equals("")) {

				szMsg = "[전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_COL_GP");
			if (szYD_STK_COL_GP.equals("")) {

				szMsg = "[전문 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_BED_NO");
			if (szYD_STK_BED_NO.equals("")) {

				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_LOT_GP_SH");
			if (intMtlCnt == 0) {

				szMsg = "[전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 재료번호
			szSTL_NO = new String[intMtlCnt + 1];
			// 권상모음순서
			szYD_UP_COLL_SEQ = new String[intMtlCnt + 1];
			;

			// 재료번호, 권상모음순서
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,
						"STL_NO" + Loop_i);
				if (szSTL_NO[Loop_i].equals("")) {

					szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				}
				// 권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,
						"YD_UP_COLL_SEQ" + Loop_i);
				if (szYD_UP_COLL_SEQ[Loop_i].equals("")) {

					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i]
							+ ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				}
			}

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal)
				return;

			// 레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();

			// 스케줄CD 체크
			// 스케줄 금지 유무
			szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara,
					"YD_SCH_PROH_EXN");
			// 작업크레인
			szYD_WRK_CRN = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
			// 대체크레인유무
			szYD_ALT_CRN_YN = ydDaoUtils.paraRecChkNull(recPara,
					"YD_ALT_CRN_YN");
			// 대체크레인
			szYD_ALT_CRN = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");

			// 스케줄 금지 유무가 "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if (szYD_SCH_PROH_EXN.equals("Y")) {

				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}

			// 작업크레인 설비 상태 체크
			blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);

			// 작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if (!blnRtnVal) {

				szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				// 대체크레인의 유무를 체크한다.
				// 대체크레인이 없으면 에러 리턴
				if (!szYD_ALT_CRN_YN.equals("Y")) {

					szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				}
				// 대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				// 대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {

					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				} else {
					// 대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					szCrn = szYD_ALT_CRN;

				}
			} else {
				// 작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				szCrn = szYD_WRK_CRN;

			}

			// 재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다.
			// 작업예약재료 등록 여부를 체크한다.
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 크레인사양과 저장품 사양을 체크(길이,폭,중량)
				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
				if (!blnRtnVal)
					return;

				// 다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if (!blnRtnVal)
					return;

			}

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if (!blnRtnVal)
				return;
			// 레코드추출
			rsResult.first();
			recPara = rsResult.getRecord();
			// 작업예약ID
			szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");

			// INSERT 항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			// 야드구분
			String szYD_GP = szYD_SCH_CD.substring(0, 1);
			// 동구분
			String szYD_BAY_GP = szYD_SCH_CD.substring(1, 2);

			// INSERT할 항목 SET
			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			recPara.setField("YD_GP", szYD_GP);
			recPara.setField("YD_BAY_GP", szYD_BAY_GP);
			recPara.setField("YD_SCH_CD", szYD_SCH_CD);
			recPara.setField("REGISTER", szUser);

			// 작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}

			// 조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();

			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			// recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			// recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", szUser);

			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 리턴 recordSet 생성
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				// 재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				blnRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C",
						rsResult);
				if (!blnRtnVal)
					return;

				// 레코드추출
				rsResult.first();
				recStkPara = rsResult.getRecord();

				// 재료번호
				recPara.setField("STL_NO", szSTL_NO[Loop_i]);
				// 적치열구분
				recPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(
						recStkPara, "YD_STK_COL_GP"));
				// 적치BED번호
				recPara.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(
						recStkPara, "YD_STK_BED_NO"));
				// 적치단번호
				recPara.setField("YD_STK_LYR_NO", ydDaoUtils.paraRecChkNull(
						recStkPara, "YD_STK_LYR_NO"));
				// 권상모음순서
				recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ[Loop_i]);

				// 작업예약재료 테이블에 등록한다.
				intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);

				if (intRtnVal < 1) {
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
			}

		} catch (Exception e) {
			szMsg = "A후판 공Bed확보 작업요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}

	} // end of procAPlEmptyBedSecurWrkReq()

	/**
	 * 오퍼레이션명 : A후판정리Lot편성
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procAPlReadjLotComp(JDTORecord msgRecord) throws JDTOException {

		YdDelegate ydDelegate = new YdDelegate();
		// 공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		// 공용 METHOD
		YdUtils ydutils = new YdUtils();

		// 레코드 선언
		JDTORecord recPara = null;
		JDTORecord recOutPara = null;
		// 레코드셋 선언
		JDTORecordSet rsResult = null;

		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메세지
		String szMsg = "";
		// 메소드명
		String szMethodName = "procAPlReadjLotComp";
		// 사용자
		String szUser = "SYSTEM";

		// Lot 편성 재료 매수
		int intLotGpSh = 0;
		// 전문 생성 일시
		String szDate = null;

		// 적치열구분
		String szYD_STK_COL_GP = null;
		// 적치BED번호
		String szYD_STK_BED_NO = null;
		// to_적치열구분
		String szYD_STK_COL_GP_TO = null;
		// to_적치BED번호
		String szYD_STK_BED_NO_TO = null;
		// 목표행선구분
		String szYD_AIM_RT_GP = null;
		// 비교목표행선구분
		String szYD_AIM_RT_GP_COMP = null;
		// 스케줄코드
		String szYD_SCH_CD = null;
		// 재료번호
		String szSTL_NO = null;
		// 적치단 재료상태
		String szYD_STK_LYR_MTL_STAT = null;
		// 권상모음순서
		int intYD_UP_COLL_SEQ = 0;

		// TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		// 에러 리턴
		if (szRcvTcCode == null) {

			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error ("
					+ szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;

		}
		// TC CODE DISPLAY
		if (bDebugFlag) {

			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		}

		try {
			// 받은 전문 편집
			// 적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_COL_GP");
			if (szYD_STK_COL_GP.equals("")) {

				szMsg = "[전문 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_BED_NO");
			if (szYD_STK_BED_NO.equals("")) {

				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 목표행선구분
			szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_AIM_RT_GP");
			if (szYD_AIM_RT_GP.equals("")) {

				szMsg = "[전문 이상] 목표행선구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// to_적치열구분
			szYD_STK_COL_GP_TO = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_COL_GP_TO");
			if (szYD_STK_COL_GP_TO.equals("")) {

				szMsg = "[전문 이상] to_적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// to_적치BED번호
			szYD_STK_BED_NO_TO = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_BED_NO_TO");
			if (szYD_STK_BED_NO_TO.equals("")) {

				szMsg = "[전문 이상] to_적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}

			// =================================================================================
			// 수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			// BRE 등록 안됨...테스트용 스케줄코드 생성
			// 추후 구현..
			szYD_SCH_CD = "DASP01MM";
			// =================================================================================

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			// 보급 Lot 편성 대상 재료 Select
			blnRtnVal = chkGetAPlReadjLotGp(szYD_STK_COL_GP.trim(),
					szYD_STK_BED_NO.trim(), rsResult);
			if (!blnRtnVal)
				return;

			// 레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			// JMS TC CODE
			recOutPara.setField("JMS_TC_CD", "YDYDJ269");
			// 레코드 커서 처음으로
			rsResult.first();

			// 보급 Lot 편성 재료 매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {

				// 레코드 추출
				recPara = rsResult.getRecord();

				// 재료번호
				szSTL_NO = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
				// 적치단재료상태
				szYD_STK_LYR_MTL_STAT = ydDaoUtils.paraRecChkNull(recPara,
						"YD_STK_LYR_MTL_STAT");
				// 비교목표행선구분
				szYD_AIM_RT_GP_COMP = ydDaoUtils.paraRecChkNull(recPara,
						"YD_AIM_RT_GP");

				// 적치단재료상태가 권상대기중(U)이거나 권하대기중(D)이면 에러리턴
				if (szYD_STK_LYR_MTL_STAT.trim().equals("U")
						|| szYD_STK_LYR_MTL_STAT.trim().equals("D")) {

					szMsg = "적치열구분 (" + szYD_STK_COL_GP + "), " + "적치Bed번호 ("
							+ szYD_STK_BED_NO + "), " + "재료번호 (" + szSTL_NO
							+ ") " + "의 적치단재료상태가 (" + szYD_STK_LYR_MTL_STAT
							+ ") 입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				}

				// 다른 크레인 스케줄에 등록 되어 있는지 체크
				blnRtnVal = chkCrnWrkMtl(szSTL_NO);
				if (!blnRtnVal)
					return;

				// 목표행선이 다르면 Lot 편성에서 제외
				if (!szYD_AIM_RT_GP.trim().equals(szYD_AIM_RT_GP_COMP.trim())) {

					rsResult.next();
					continue;

				}
				// 권상모음순서
				intYD_UP_COLL_SEQ = intYD_UP_COLL_SEQ + 1;
				// 재료번호
				recOutPara.setField("STL_NO" + Loop_i, szSTL_NO);
				// 권상모음순서
				recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, ""
						+ intYD_UP_COLL_SEQ);

				// 다음 레코드로
				rsResult.next();

			}

			// 전문 발생 일시
			szDate = ydUtils.getCurDate("yyyy/MM/dd HH:mm:ss");

			// 발생 일시
			recOutPara.setField("JMS_TC_CREATE_DDTT", szDate);
			// 스케줄코드
			recOutPara.setField("YD_SCH_CD", szYD_SCH_CD);
			// Lot편성 매수
			recOutPara.setField("YD_LOT_GP_SH", "" + intLotGpSh);
			// 적치열구분
			recOutPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			// 적치BED번호
			recOutPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);

			// 전문 송신
			ydDelegate.sendMsg(recOutPara);
			szMsg = "A후판 정리 Lot 편성 후 A후판 정리 작업요구 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		} catch (Exception e) {
			szMsg = "A후판 정리 Lot 편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}

	} // end of procAPlReadjLotComp

	/**
	 * 오퍼레이션명 : A후판정리작업요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procAPlReadjWrkReq(JDTORecord msgRecord) throws JDTOException {

		// 스케줄기준 DAO
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		// 작업예약 재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		// 공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		// 공용 METHOD
		YdUtils ydutils = new YdUtils();

		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메세지
		String szMsg = "";
		// METHOD명
		String szMethodName = "procAPlReadjWrkReq";
		// 사용자
		String szUser = "SYSTEM";

		// 레코드 선언
		JDTORecord recPara = null;
		JDTORecord recStkPara = null;
		// 레코드셋 선언
		JDTORecordSet rsResult = null;

		// 설비ID(열구분)
		String szYD_STK_COL_GP = null;
		// 적치BED번호
		String szYD_STK_BED_NO = null;
		// 재료매수(int)
		int intMtlCnt = 0;
		// 재료번호
		String[] szSTL_NO = null;
		// 권상모음순서
		String[] szYD_UP_COLL_SEQ = null;
		// 스케줄코드
		String szYD_SCH_CD = null;
		// 스케줄 금지 유무
		String szYD_SCH_PROH_EXN = null;
		// 작업크레인
		String szYD_WRK_CRN = null;
		// 대체크레인유무
		String szYD_ALT_CRN_YN = null;
		// 대체크레인
		String szYD_ALT_CRN = null;
		// 선택크레인
		String szCrn = null;
		// 작업예약ID
		String szYD_WBOOK_ID = null;

		// TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		// 에러 리턴
		if (szRcvTcCode == null) {

			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error ("
					+ szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;

		}
		// TC CODE DISPLAY
		if (bDebugFlag) {

			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		}

		try {

			// 받은 전문 편집
			// 스케줄코드
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			if (szYD_SCH_CD.equals("")) {

				szMsg = "[전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_COL_GP");
			if (szYD_STK_COL_GP.equals("")) {

				szMsg = "[전문 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_BED_NO");
			if (szYD_STK_BED_NO.equals("")) {

				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_LOT_GP_SH");
			if (intMtlCnt == 0) {

				szMsg = "[전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 재료번호
			szSTL_NO = new String[intMtlCnt + 1];
			// 권상모음순서
			szYD_UP_COLL_SEQ = new String[intMtlCnt + 1];
			;

			// 재료번호, 권상모음순서
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,
						"STL_NO" + Loop_i);
				if (szSTL_NO[Loop_i].equals("")) {

					szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				}
				// 권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,
						"YD_UP_COLL_SEQ" + Loop_i);
				if (szYD_UP_COLL_SEQ[Loop_i].equals("")) {

					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i]
							+ ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				}
			}

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal)
				return;

			// 레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();

			// 스케줄CD 체크
			// 스케줄 금지 유무
			szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara,
					"YD_SCH_PROH_EXN");
			// 작업크레인
			szYD_WRK_CRN = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
			// 대체크레인유무
			szYD_ALT_CRN_YN = ydDaoUtils.paraRecChkNull(recPara,
					"YD_ALT_CRN_YN");
			// 대체크레인
			szYD_ALT_CRN = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");

			// 스케줄 금지 유무가 "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if (szYD_SCH_PROH_EXN.equals("Y")) {

				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}

			// 작업크레인 설비 상태 체크
			blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);

			// 작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if (!blnRtnVal) {

				szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				// 대체크레인의 유무를 체크한다.
				// 대체크레인이 없으면 에러 리턴
				if (!szYD_ALT_CRN_YN.equals("Y")) {

					szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				}
				// 대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				// 대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {

					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				} else {
					// 대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					szCrn = szYD_ALT_CRN;

				}
			} else {
				// 작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				szCrn = szYD_WRK_CRN;

			}

			// 재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다.
			// 작업예약재료 등록 여부를 체크한다.
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 크레인사양과 저장품 사양을 체크(길이,폭,중량)
				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
				if (!blnRtnVal)
					return;

				// 다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if (!blnRtnVal)
					return;

			}

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if (!blnRtnVal)
				return;
			// 레코드추출
			rsResult.first();
			recPara = rsResult.getRecord();
			// 작업예약ID
			szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");

			// INSERT 항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			// 야드구분
			String szYD_GP = szYD_SCH_CD.substring(0, 1);
			// 동구분
			String szYD_BAY_GP = szYD_SCH_CD.substring(1, 2);

			// INSERT할 항목 SET
			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			recPara.setField("YD_GP", szYD_GP);
			recPara.setField("YD_BAY_GP", szYD_BAY_GP);
			recPara.setField("YD_SCH_CD", szYD_SCH_CD);
			recPara.setField("REGISTER", szUser);

			// 작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}

			// 조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();

			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			// recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			// recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", szUser);

			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 리턴 recordSet 생성
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				// 재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				blnRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C",
						rsResult);
				if (!blnRtnVal)
					return;

				// 레코드추출
				rsResult.first();
				recStkPara = rsResult.getRecord();

				// 재료번호
				recPara.setField("STL_NO", szSTL_NO[Loop_i]);
				// 적치열구분
				recPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(
						recStkPara, "YD_STK_COL_GP"));
				// 적치BED번호
				recPara.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(
						recStkPara, "YD_STK_BED_NO"));
				// 적치단번호
				recPara.setField("YD_STK_LYR_NO", ydDaoUtils.paraRecChkNull(
						recStkPara, "YD_STK_LYR_NO"));
				// 권상모음순서
				recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ[Loop_i]);

				// 작업예약재료 테이블에 등록한다.
				intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);

				if (intRtnVal < 1) {
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
			}

		} catch (Exception e) {
			szMsg = "A후판 정리 작업요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}

	} // end of procAPlReadjWrkReq()
	
	
	
	

	/**
	 * 오퍼레이션명 : 후판창고선별작업Lot편성
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procY4SelWrkLotComp(JDTORecord msgRecord) throws JDTOException {

		YdDelegate ydDelegate = new YdDelegate();
		// 공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		// 공용 METHOD
		YdUtils ydutils = new YdUtils();

		// 레코드 선언
		JDTORecord recPara = null;
		JDTORecord recOutPara = null;
		// 레코드셋 선언
		JDTORecordSet rsResult = null;

		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메세지
		String szMsg = "";
		// 메소드명
		String szMethodName = "procY4SelWrkLotComp";
		// 사용자
		String szUser = "SYSTEM";

		// Lot 편성 재료 매수
		int intLotGpSh = 0;
		// 전문 생성 일시
		String szDate = null;

		// 설비ID
		String szYD_EQP_ID = null;
		// 저장집합코드
		String szYD_STR_GTR_CD = null;
		// 산적LOT_CODE
		String szYD_STK_LOT_CD = null;
		// 크레인 작업허용중량
		long lngYD_WRK_ABLE_WT = 0;
		// 소재 합계 중량
		long lngSumMtlWt = 0;
		// 스케줄코드
		String szYD_SCH_CD = null;
		// 재료번호
		String szSTL_NO = null;

		// TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		// 에러 리턴
		if (szRcvTcCode == null) {

			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error ("
					+ szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;

		}
		// TC CODE DISPLAY
		if (bDebugFlag) {

			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		}

		try {
			// 받은 전문 편집
			// 설비ID
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if (szYD_EQP_ID.equals("")) {

				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 저장집합코드
			szYD_STR_GTR_CD = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STR_GTR_CD");
			if (szYD_STR_GTR_CD.equals("")) {

				szMsg = "[전문 이상] 저장집합코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 산적LOT_CODE
			szYD_STK_LOT_CD = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_LOT_CD");
			if (szYD_STK_LOT_CD.equals("")) {

				szMsg = "[전문 이상] 산적LOT_CODE가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}

			// =================================================================================
			// 수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			// BRE 등록 안됨...테스트용 스케줄코드 생성
			// 추후 구현..
			szYD_SCH_CD = "KAPG01MM";
			// =================================================================================

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 설비ID로 크레인사양 테이블 조회
			blnRtnVal = chkGetCrnSpec(szYD_EQP_ID, rsResult);
			if (!blnRtnVal)
				return;

			// 레코드 커서 처음으로
			rsResult.first();
			// 레코드 추출
			recPara = rsResult.getRecord();

			// 크레인 작업허용중량
			lngYD_WRK_ABLE_WT = ydDaoUtils.paraRecChkNullLong(recPara,
					"YD_WRK_ABLE_WT");

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// Lot 편성 대상 재료 Select
			blnRtnVal = chkGetPlWhSelWrkLotGp(szYD_STR_GTR_CD.trim(),
					szYD_STK_LOT_CD.trim(), rsResult);
			if (!blnRtnVal)
				return;

			// 레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			// JMS TC CODE
			recOutPara.setField("JMS_TC_CD", "YDYDJ271");
			// 레코드 커서 처음으로
			rsResult.first();

			// 보급 Lot 편성 재료 매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {

				// 레코드 추출
				recPara = rsResult.getRecord();

				// 대상 재료 중량 합계
				lngSumMtlWt = lngSumMtlWt
						+ ydDaoUtils.paraRecChkNullLong(recPara, "YD_MTL_WT");
				// 크레인작업허용중량보다 대상재료들의 중량합이 같거나 많으면 편성 중지
				if (lngYD_WRK_ABLE_WT <= lngSumMtlWt)
					break;

				// 재료번호
				szSTL_NO = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");

				// 다른 크레인 스케줄에 등록 되어 있는지 체크
				blnRtnVal = chkCrnWrkMtl(szSTL_NO);
				if (!blnRtnVal)
					return;

				// 재료번호
				recOutPara.setField("STL_NO" + Loop_i, szSTL_NO);
				// 권상모음순서
				recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, "" + Loop_i);

				// Lot 편성 재료 매수
				intLotGpSh = Loop_i;

				// 다음 레코드로
				rsResult.next();

			}

			// 전문 발생 일시
			szDate = ydUtils.getCurDate("yyyy/MM/dd HH:mm:ss");

			// 발생 일시
			recOutPara.setField("JMS_TC_CREATE_DDTT", szDate);
			// 스케줄코드
			recOutPara.setField("YD_SCH_CD", szYD_SCH_CD);
			// Lot편성 매수
			recOutPara.setField("YD_LOT_GP_SH", "" + intLotGpSh);
			// 설비id
			recOutPara.setField("YD_EQP_ID", szYD_EQP_ID);

			// 전문 송신
			// ydDelegate.sendMsg(recOutPara);
			// 수정-이현성
			// ydDelegate.simSndMsg(recOutPara);
			ydDelegate.rmtSndMsg(recOutPara);
			szMsg = "후판창고 선별작업 Lot 편성 후 후판창고 선별작업 요구 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		} catch (Exception e) {
			szMsg = "후판창고 선별작업 Lot 편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}

	} // end of procY4SelWrkLotComp

	/**
	 * 오퍼레이션명 : 후판창고선별작업요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procY4SelWrkReq(JDTORecord msgRecord) throws JDTOException {

		// 스케줄기준 DAO
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		// 작업예약 재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		// 공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		// 공용 METHOD
		YdUtils ydutils = new YdUtils();

		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메세지
		String szMsg = "";
		// METHOD명
		String szMethodName = "procY4SelWrkReq";
		// 사용자
		String szUser = "SYSTEM";

		// 레코드 선언
		JDTORecord recPara = null;
		JDTORecord recStkPara = null;
		// 레코드셋 선언
		JDTORecordSet rsResult = null;

		// 설비ID
		String szYD_EQP_ID = null;
		// 적치열구분
		String szYD_STK_COL_GP = null;
		// 적치BED번호
		String szYD_STK_BED_NO = null;
		// 재료매수(int)
		int intMtlCnt = 0;
		// 재료번호
		String[] szSTL_NO = null;
		// 권상모음순서
		String[] szYD_UP_COLL_SEQ = null;
		// 스케줄코드
		String szYD_SCH_CD = null;
		// 스케줄 금지 유무
		String szYD_SCH_PROH_EXN = null;
		// 작업크레인
		String szYD_WRK_CRN = null;
		// 대체크레인유무
		String szYD_ALT_CRN_YN = null;
		// 대체크레인
		String szYD_ALT_CRN = null;
		// 선택크레인
		String szCrn = null;
		// 작업예약ID
		String szYD_WBOOK_ID = null;

		// TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		// 에러 리턴
		if (szRcvTcCode == null) {

			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error ("
					+ szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;

		}
		// TC CODE DISPLAY
		if (bDebugFlag) {

			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		}

		try {

			// 받은 전문 편집
			// 설비id
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if (szYD_EQP_ID.equals("")) {

				szMsg = "[전문 이상] 설비id가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 스케줄코드
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			if (szYD_SCH_CD.equals("")) {

				szMsg = "[전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_LOT_GP_SH");
			if (intMtlCnt == 0) {

				szMsg = "[전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 재료번호
			szSTL_NO = new String[intMtlCnt + 1];
			// 권상모음순서
			szYD_UP_COLL_SEQ = new String[intMtlCnt + 1];
			;

			// 재료번호, 권상모음순서
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,
						"STL_NO" + Loop_i);
				if (szSTL_NO[Loop_i].equals("")) {

					szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				}
				// 권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,
						"YD_UP_COLL_SEQ" + Loop_i);
				if (szYD_UP_COLL_SEQ[Loop_i].equals("")) {

					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i]
							+ ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				}
			}

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal)
				return;

			// 레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();

			// 스케줄CD 체크
			// 스케줄 금지 유무
			szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara,
					"YD_SCH_PROH_EXN");
			// 작업크레인
			szYD_WRK_CRN = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
			// 대체크레인유무
			szYD_ALT_CRN_YN = ydDaoUtils.paraRecChkNull(recPara,
					"YD_ALT_CRN_YN");
			// 대체크레인
			szYD_ALT_CRN = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");

			// 스케줄 금지 유무가 "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if (szYD_SCH_PROH_EXN.equals("Y")) {

				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}

			// 작업크레인 설비 상태 체크
			blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);

			// 작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if (!blnRtnVal) {

				szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				// 대체크레인의 유무를 체크한다.
				// 대체크레인이 없으면 에러 리턴
				if (!szYD_ALT_CRN_YN.equals("Y")) {

					szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				}
				// 대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				// 대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {

					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				} else {
					// 대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					szCrn = szYD_ALT_CRN;

				}
			} else {
				// 작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				szCrn = szYD_WRK_CRN;

			}

			// 재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다.
			// 작업예약재료 등록 여부를 체크한다.
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 크레인사양과 저장품 사양을 체크(길이,폭,중량)
				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
				if (!blnRtnVal)
					return;

				// 다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if (!blnRtnVal)
					return;

			}

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if (!blnRtnVal)
				return;
			// 레코드추출
			rsResult.first();
			recPara = rsResult.getRecord();
			// 작업예약ID
			szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");

			// INSERT 항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			// 야드구분
			String szYD_GP = szYD_SCH_CD.substring(0, 1);
			// 동구분
			String szYD_BAY_GP = szYD_SCH_CD.substring(1, 2);

			// INSERT할 항목 SET
			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			recPara.setField("YD_GP", szYD_GP);
			recPara.setField("YD_BAY_GP", szYD_BAY_GP);
			recPara.setField("YD_SCH_CD", szYD_SCH_CD);
			recPara.setField("REGISTER", szUser);

			// 작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}

			// 조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();

			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			// recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			// recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", szUser);

			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 리턴 recordSet 생성
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				// 재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				blnRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C",
						rsResult);
				if (!blnRtnVal)
					return;

				// 레코드추출
				rsResult.first();
				recStkPara = rsResult.getRecord();

				// 재료번호
				recPara.setField("STL_NO", szSTL_NO[Loop_i]);
				// 적치열구분
				recPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(
						recStkPara, "YD_STK_COL_GP"));
				// 적치BED번호
				recPara.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(
						recStkPara, "YD_STK_BED_NO"));
				// 적치단번호
				recPara.setField("YD_STK_LYR_NO", ydDaoUtils.paraRecChkNull(
						recStkPara, "YD_STK_LYR_NO"));
				// 권상모음순서
				recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ[Loop_i]);

				// 작업예약재료 테이블에 등록한다.
				intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);

				if (intRtnVal < 1) {
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
			}

		} catch (Exception e) {
			szMsg = "후판창고 선별작업 요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}

	} // end of procY4SelWrkReq()

	/**
	 * 오퍼레이션명 : 후판창고공Bed확보Lot편성
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procY4EmptyBedSecurLotComp(JDTORecord msgRecord)
			throws JDTOException {

		YdDelegate ydDelegate = new YdDelegate();
		// 공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		// 공용 METHOD
		YdUtils ydutils = new YdUtils();

		// 레코드 선언
		JDTORecord recPara = null;
		JDTORecord recOutPara = null;
		// 레코드셋 선언
		JDTORecordSet rsResult = null;

		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메세지
		String szMsg = "";
		// 메소드명
		String szMethodName = "procY4EmptyBedSecurLotComp";
		// 사용자
		String szUser = "SYSTEM";

		// Lot 편성 재료 매수
		int intLotGpSh = 0;
		// 전문 생성 일시
		String szDate = null;

		// 적치열구분
		String szYD_STK_COL_GP = null;
		// 적치BED번호
		String szYD_STK_BED_NO = null;
		// to_적치열구분
		String szYD_STK_COL_GP_TO = null;
		// to_적치BED번호
		String szYD_STK_BED_NO_TO = null;
		// 스케줄코드
		String szYD_SCH_CD = null;
		// 재료번호
		String szSTL_NO = null;
		// 적치단 재료상태
		String szYD_STK_LYR_MTL_STAT = null;

		// TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		// 에러 리턴
		if (szRcvTcCode == null) {

			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error ("
					+ szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;

		}
		// TC CODE DISPLAY
		if (bDebugFlag) {

			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		}

		try {
			// 받은 전문 편집
			// 적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_COL_GP");
			if (szYD_STK_COL_GP.equals("")) {

				szMsg = "[전문 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_BED_NO");
			if (szYD_STK_BED_NO.equals("")) {

				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// to_적치열구분
			szYD_STK_COL_GP_TO = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_COL_GP_TO");
			if (szYD_STK_COL_GP_TO.equals("")) {

				szMsg = "[전문 이상] to_적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// to_적치BED번호
			szYD_STK_BED_NO_TO = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_BED_NO_TO");
			if (szYD_STK_BED_NO_TO.equals("")) {

				szMsg = "[전문 이상] to_적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}

			// =================================================================================
			// 수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			// BRE 등록 안됨...테스트용 스케줄코드 생성
			// 추후 구현..
			szYD_SCH_CD = "DASP01MM";
			// =================================================================================

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			// 보급 Lot 편성 대상 재료 Select
			blnRtnVal = chkGetAPlEmptyBedSecurLotGp(szYD_STK_COL_GP.trim(),
					szYD_STK_BED_NO.trim(), rsResult);
			if (!blnRtnVal)
				return;

			// Lot 편성 매수
			intLotGpSh = rsResult.size();

			// 레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			// JMS TC CODE
			recOutPara.setField("JMS_TC_CD", "YDYDJ267");
			// 레코드 커서 처음으로
			rsResult.first();

			// 보급 Lot 편성 재료 매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= intLotGpSh; Loop_i++) {

				// 레코드 추출
				recPara = rsResult.getRecord();

				// 재료번호
				szSTL_NO = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
				// 적치단재료상태
				szYD_STK_LYR_MTL_STAT = ydDaoUtils.paraRecChkNull(recPara,
						"YD_STK_LYR_MTL_STAT");

				// 적치단재료상태가 권상대기중(U)이거나 권하대기중(D)이면 에러리턴
				if (szYD_STK_LYR_MTL_STAT.trim().equals("U")
						|| szYD_STK_LYR_MTL_STAT.trim().equals("D")) {

					szMsg = "적치열구분 ("
							+ szYD_STK_COL_GP
							+ "), "
							+ "적치Bed번호 ("
							+ szYD_STK_BED_NO
							+ "), "
							+ "적치단번호 ("
							+ ydDaoUtils.paraRecChkNull(recPara,
									"YD_STK_LYR_NO") + "), " + "재료번호 ("
							+ szSTL_NO + ") " + "의 적치단재료상태가 ("
							+ szYD_STK_LYR_MTL_STAT + ") 입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				}

				// 다른 크레인 스케줄에 등록 되어 있는지 체크
				blnRtnVal = chkCrnWrkMtl(szSTL_NO);
				if (!blnRtnVal)
					return;

				// 재료번호
				recOutPara.setField("STL_NO" + Loop_i, szSTL_NO);
				// 권상모음순서
				recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, "" + Loop_i);

				// 다음 레코드로
				rsResult.next();

			}

			// 전문 발생 일시
			szDate = ydUtils.getCurDate("yyyy/MM/dd HH:mm:ss");

			// 발생 일시
			recOutPara.setField("JMS_TC_CREATE_DDTT", szDate);
			// 스케줄코드
			recOutPara.setField("YD_SCH_CD", szYD_SCH_CD);
			// Lot편성 매수
			recOutPara.setField("YD_LOT_GP_SH", "" + intLotGpSh);
			// 적치열구분
			recOutPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			// 적치BED번호
			recOutPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);

			// 전문 송신
			ydDelegate.sendMsg(recOutPara);
			szMsg = "후판창고 공Bed확보 Lot 편성 후 후판창고 공Bed확보 작업요구 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		} catch (Exception e) {
			szMsg = "후판창고 공Bed확보 Lot 편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}

	} // end of procY4EmptyBedSecurLotComp

	/**
	 * 오퍼레이션명 : 후판창고공Bed확보작업요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procY4EmptyBedSecurWrkReq(JDTORecord msgRecord)
			throws JDTOException {

		// 스케줄기준 DAO
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		// 작업예약 재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		// 공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		// 공용 METHOD
		YdUtils ydutils = new YdUtils();

		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메세지
		String szMsg = "";
		// METHOD명
		String szMethodName = "procY4EmptyBedSecurWrkReq";
		// 사용자
		String szUser = "SYSTEM";

		// 레코드 선언
		JDTORecord recPara = null;
		JDTORecord recStkPara = null;
		// 레코드셋 선언
		JDTORecordSet rsResult = null;

		// 설비ID(열구분)
		String szYD_STK_COL_GP = null;
		// 적치BED번호
		String szYD_STK_BED_NO = null;
		// 재료매수(int)
		int intMtlCnt = 0;
		// 재료번호
		String[] szSTL_NO = null;
		// 권상모음순서
		String[] szYD_UP_COLL_SEQ = null;
		// 스케줄코드
		String szYD_SCH_CD = null;
		// 스케줄 금지 유무
		String szYD_SCH_PROH_EXN = null;
		// 작업크레인
		String szYD_WRK_CRN = null;
		// 대체크레인유무
		String szYD_ALT_CRN_YN = null;
		// 대체크레인
		String szYD_ALT_CRN = null;
		// 선택크레인
		String szCrn = null;
		// 작업예약ID
		String szYD_WBOOK_ID = null;

		// TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		// 에러 리턴
		if (szRcvTcCode == null) {

			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error ("
					+ szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;

		}
		// TC CODE DISPLAY
		if (bDebugFlag) {

			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		}

		try {

			// 받은 전문 편집
			// 스케줄코드
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			if (szYD_SCH_CD.equals("")) {

				szMsg = "[전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_COL_GP");
			if (szYD_STK_COL_GP.equals("")) {

				szMsg = "[전문 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_BED_NO");
			if (szYD_STK_BED_NO.equals("")) {

				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_LOT_GP_SH");
			if (intMtlCnt == 0) {

				szMsg = "[전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 재료번호
			szSTL_NO = new String[intMtlCnt + 1];
			// 권상모음순서
			szYD_UP_COLL_SEQ = new String[intMtlCnt + 1];
			;

			// 재료번호, 권상모음순서
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,
						"STL_NO" + Loop_i);
				if (szSTL_NO[Loop_i].equals("")) {

					szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				}
				// 권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,
						"YD_UP_COLL_SEQ" + Loop_i);
				if (szYD_UP_COLL_SEQ[Loop_i].equals("")) {

					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i]
							+ ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				}
			}

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal)
				return;

			// 레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();

			// 스케줄CD 체크
			// 스케줄 금지 유무
			szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara,
					"YD_SCH_PROH_EXN");
			// 작업크레인
			szYD_WRK_CRN = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
			// 대체크레인유무
			szYD_ALT_CRN_YN = ydDaoUtils.paraRecChkNull(recPara,
					"YD_ALT_CRN_YN");
			// 대체크레인
			szYD_ALT_CRN = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");

			// 스케줄 금지 유무가 "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if (szYD_SCH_PROH_EXN.equals("Y")) {

				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}

			// 작업크레인 설비 상태 체크
			blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);

			// 작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if (!blnRtnVal) {

				szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				// 대체크레인의 유무를 체크한다.
				// 대체크레인이 없으면 에러 리턴
				if (!szYD_ALT_CRN_YN.equals("Y")) {

					szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				}
				// 대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				// 대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {

					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				} else {
					// 대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					szCrn = szYD_ALT_CRN;

				}
			} else {
				// 작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				szCrn = szYD_WRK_CRN;

			}

			// 재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다.
			// 작업예약재료 등록 여부를 체크한다.
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 크레인사양과 저장품 사양을 체크(길이,폭,중량)
				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
				if (!blnRtnVal)
					return;

				// 다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if (!blnRtnVal)
					return;

			}

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if (!blnRtnVal)
				return;
			// 레코드추출
			rsResult.first();
			recPara = rsResult.getRecord();
			// 작업예약ID
			szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");

			// INSERT 항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			// 야드구분
			String szYD_GP = szYD_SCH_CD.substring(0, 1);
			// 동구분
			String szYD_BAY_GP = szYD_SCH_CD.substring(1, 2);

			// INSERT할 항목 SET
			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			recPara.setField("YD_GP", szYD_GP);
			recPara.setField("YD_BAY_GP", szYD_BAY_GP);
			recPara.setField("YD_SCH_CD", szYD_SCH_CD);
			recPara.setField("REGISTER", szUser);

			// 작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}

			// 조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();

			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			// recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			// recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", szUser);

			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 리턴 recordSet 생성
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				// 재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				blnRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C",
						rsResult);
				if (!blnRtnVal)
					return;

				// 레코드추출
				rsResult.first();
				recStkPara = rsResult.getRecord();

				// 재료번호
				recPara.setField("STL_NO", szSTL_NO[Loop_i]);
				// 적치열구분
				recPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(
						recStkPara, "YD_STK_COL_GP"));
				// 적치BED번호
				recPara.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(
						recStkPara, "YD_STK_BED_NO"));
				// 적치단번호
				recPara.setField("YD_STK_LYR_NO", ydDaoUtils.paraRecChkNull(
						recStkPara, "YD_STK_LYR_NO"));
				// 권상모음순서
				recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ[Loop_i]);

				// 작업예약재료 테이블에 등록한다.
				intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);

				if (intRtnVal < 1) {
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
			}

		} catch (Exception e) {
			szMsg = "후판창고 공Bed확보 작업요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}

	} // end of procY4EmptyBedSecurWrkReq()

	/**
	 * 오퍼레이션명 : 후판창고정리Lot편성
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procY4ReadjLotComp(JDTORecord msgRecord) throws JDTOException {

		YdDelegate ydDelegate = new YdDelegate();
		// 공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		// 공용 METHOD
		YdUtils ydutils = new YdUtils();

		// 레코드 선언
		JDTORecord recPara = null;
		JDTORecord recOutPara = null;
		// 레코드셋 선언
		JDTORecordSet rsResult = null;

		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메세지
		String szMsg = "";
		// 메소드명
		String szMethodName = "procY4ReadjLotComp";
		// 사용자
		String szUser = "SYSTEM";

		// Lot 편성 재료 매수
		int intLotGpSh = 0;
		// 전문 생성 일시
		String szDate = null;

		// 적치열구분
		String szYD_STK_COL_GP = null;
		// 적치BED번호
		String szYD_STK_BED_NO = null;
		// to_적치열구분
		String szYD_STK_COL_GP_TO = null;
		// to_적치BED번호
		String szYD_STK_BED_NO_TO = null;
		// 목표행선구분
		String szYD_AIM_RT_GP = null;
		// 비교목표행선구분
		String szYD_AIM_RT_GP_COMP = null;
		// 스케줄코드
		String szYD_SCH_CD = null;
		// 재료번호
		String szSTL_NO = null;
		// 적치단 재료상태
		String szYD_STK_LYR_MTL_STAT = null;
		// 권상모음순서
		int intYD_UP_COLL_SEQ = 0;

		// TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		// 에러 리턴
		if (szRcvTcCode == null) {

			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error ("
					+ szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;

		}
		// TC CODE DISPLAY
		if (bDebugFlag) {

			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		}

		try {
			// 받은 전문 편집
			// 적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_COL_GP");
			if (szYD_STK_COL_GP.equals("")) {

				szMsg = "[전문 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_BED_NO");
			if (szYD_STK_BED_NO.equals("")) {

				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 목표행선구분
			szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_AIM_RT_GP");
			if (szYD_AIM_RT_GP.equals("")) {

				szMsg = "[전문 이상] 목표행선구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// to_적치열구분
			szYD_STK_COL_GP_TO = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_COL_GP_TO");
			if (szYD_STK_COL_GP_TO.equals("")) {

				szMsg = "[전문 이상] to_적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// to_적치BED번호
			szYD_STK_BED_NO_TO = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_BED_NO_TO");
			if (szYD_STK_BED_NO_TO.equals("")) {

				szMsg = "[전문 이상] to_적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}

			// =================================================================================
			// 수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			// BRE 등록 안됨...테스트용 스케줄코드 생성
			// 추후 구현..
			szYD_SCH_CD = "DASP01MM";
			// =================================================================================

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			// 보급 Lot 편성 대상 재료 Select
			blnRtnVal = chkGetAPlReadjLotGp(szYD_STK_COL_GP.trim(),
					szYD_STK_BED_NO.trim(), rsResult);
			if (!blnRtnVal)
				return;

			// 레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			// JMS TC CODE
			recOutPara.setField("JMS_TC_CD", "YDYDJ269");
			// 레코드 커서 처음으로
			rsResult.first();

			// 보급 Lot 편성 재료 매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {

				// 레코드 추출
				recPara = rsResult.getRecord();

				// 재료번호
				szSTL_NO = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
				// 적치단재료상태
				szYD_STK_LYR_MTL_STAT = ydDaoUtils.paraRecChkNull(recPara,
						"YD_STK_LYR_MTL_STAT");
				// 비교목표행선구분
				szYD_AIM_RT_GP_COMP = ydDaoUtils.paraRecChkNull(recPara,
						"YD_AIM_RT_GP");

				// 적치단재료상태가 권상대기중(U)이거나 권하대기중(D)이면 에러리턴
				if (szYD_STK_LYR_MTL_STAT.trim().equals("U")
						|| szYD_STK_LYR_MTL_STAT.trim().equals("D")) {

					szMsg = "적치열구분 (" + szYD_STK_COL_GP + "), " + "적치Bed번호 ("
							+ szYD_STK_BED_NO + "), " + "재료번호 (" + szSTL_NO
							+ ") " + "의 적치단재료상태가 (" + szYD_STK_LYR_MTL_STAT
							+ ") 입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				}

				// 다른 크레인 스케줄에 등록 되어 있는지 체크
				blnRtnVal = chkCrnWrkMtl(szSTL_NO);
				if (!blnRtnVal)
					return;

				// 목표행선이 다르면 Lot 편성에서 제외
				if (!szYD_AIM_RT_GP.trim().equals(szYD_AIM_RT_GP_COMP.trim())) {

					rsResult.next();
					continue;

				}
				// 권상모음순서
				intYD_UP_COLL_SEQ = intYD_UP_COLL_SEQ + 1;
				// 재료번호
				recOutPara.setField("STL_NO" + Loop_i, szSTL_NO);
				// 권상모음순서
				recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, ""
						+ intYD_UP_COLL_SEQ);

				// 다음 레코드로
				rsResult.next();

			}

			// 전문 발생 일시
			szDate = ydUtils.getCurDate("yyyy/MM/dd HH:mm:ss");

			// 발생 일시
			recOutPara.setField("JMS_TC_CREATE_DDTT", szDate);
			// 스케줄코드
			recOutPara.setField("YD_SCH_CD", szYD_SCH_CD);
			// Lot편성 매수
			recOutPara.setField("YD_LOT_GP_SH", "" + intLotGpSh);
			// 적치열구분
			recOutPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			// 적치BED번호
			recOutPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);

			// 전문 송신
			ydDelegate.sendMsg(recOutPara);
			szMsg = "후판창고 정리 Lot 편성 후 후판창고 정리 작업요구 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		} catch (Exception e) {
			szMsg = "후판창고 정리 Lot 편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}

	} // end of procY4ReadjLotComp

	/**
	 * 오퍼레이션명 : 후판창고정리작업요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procY4ReadjWrkReq(JDTORecord msgRecord) throws JDTOException {

		// 스케줄기준 DAO
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		// 작업예약 재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		// 공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		// 공용 METHOD
		YdUtils ydutils = new YdUtils();

		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메세지
		String szMsg = "";
		// METHOD명
		String szMethodName = "procY4ReadjWrkReq";
		// 사용자
		String szUser = "SYSTEM";

		// 레코드 선언
		JDTORecord recPara = null;
		JDTORecord recStkPara = null;
		// 레코드셋 선언
		JDTORecordSet rsResult = null;

		// 설비ID(열구분)
		String szYD_STK_COL_GP = null;
		// 적치BED번호
		String szYD_STK_BED_NO = null;
		// 재료매수(int)
		int intMtlCnt = 0;
		// 재료번호
		String[] szSTL_NO = null;
		// 권상모음순서
		String[] szYD_UP_COLL_SEQ = null;
		// 스케줄코드
		String szYD_SCH_CD = null;
		// 스케줄 금지 유무
		String szYD_SCH_PROH_EXN = null;
		// 작업크레인
		String szYD_WRK_CRN = null;
		// 대체크레인유무
		String szYD_ALT_CRN_YN = null;
		// 대체크레인
		String szYD_ALT_CRN = null;
		// 선택크레인
		String szCrn = null;
		// 작업예약ID
		String szYD_WBOOK_ID = null;

		// TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		// 에러 리턴
		if (szRcvTcCode == null) {

			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error ("
					+ szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;

		}
		// TC CODE DISPLAY
		if (bDebugFlag) {

			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		}

		try {

			// 받은 전문 편집
			// 스케줄코드
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			if (szYD_SCH_CD.equals("")) {

				szMsg = "[전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_COL_GP");
			if (szYD_STK_COL_GP.equals("")) {

				szMsg = "[전문 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_BED_NO");
			if (szYD_STK_BED_NO.equals("")) {

				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_LOT_GP_SH");
			if (intMtlCnt == 0) {

				szMsg = "[전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 재료번호
			szSTL_NO = new String[intMtlCnt + 1];
			// 권상모음순서
			szYD_UP_COLL_SEQ = new String[intMtlCnt + 1];
			;

			// 재료번호, 권상모음순서
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,
						"STL_NO" + Loop_i);
				if (szSTL_NO[Loop_i].equals("")) {

					szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				}
				// 권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,
						"YD_UP_COLL_SEQ" + Loop_i);
				if (szYD_UP_COLL_SEQ[Loop_i].equals("")) {

					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i]
							+ ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				}
			}

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal)
				return;

			// 레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();

			// 스케줄CD 체크
			// 스케줄 금지 유무
			szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara,
					"YD_SCH_PROH_EXN");
			// 작업크레인
			szYD_WRK_CRN = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
			// 대체크레인유무
			szYD_ALT_CRN_YN = ydDaoUtils.paraRecChkNull(recPara,
					"YD_ALT_CRN_YN");
			// 대체크레인
			szYD_ALT_CRN = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");

			// 스케줄 금지 유무가 "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if (szYD_SCH_PROH_EXN.equals("Y")) {

				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}

			// 작업크레인 설비 상태 체크
			blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);

			// 작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if (!blnRtnVal) {

				szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				// 대체크레인의 유무를 체크한다.
				// 대체크레인이 없으면 에러 리턴
				if (!szYD_ALT_CRN_YN.equals("Y")) {

					szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				}
				// 대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				// 대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {

					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				} else {
					// 대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					szCrn = szYD_ALT_CRN;

				}
			} else {
				// 작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				szCrn = szYD_WRK_CRN;

			}

			// 재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다.
			// 작업예약재료 등록 여부를 체크한다.
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 크레인사양과 저장품 사양을 체크(길이,폭,중량)
				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
				if (!blnRtnVal)
					return;

				// 다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if (!blnRtnVal)
					return;

			}

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if (!blnRtnVal)
				return;
			// 레코드추출
			rsResult.first();
			recPara = rsResult.getRecord();
			// 작업예약ID
			szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");

			// INSERT 항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			// 야드구분
			String szYD_GP = szYD_SCH_CD.substring(0, 1);
			// 동구분
			String szYD_BAY_GP = szYD_SCH_CD.substring(1, 2);

			// INSERT할 항목 SET
			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			recPara.setField("YD_GP", szYD_GP);
			recPara.setField("YD_BAY_GP", szYD_BAY_GP);
			recPara.setField("YD_SCH_CD", szYD_SCH_CD);
			recPara.setField("REGISTER", szUser);

			// 작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}

			// 조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();

			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			// recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			// recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", szUser);

			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 리턴 recordSet 생성
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				// 재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				blnRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C",
						rsResult);
				if (!blnRtnVal)
					return;

				// 레코드추출
				rsResult.first();
				recStkPara = rsResult.getRecord();

				// 재료번호
				recPara.setField("STL_NO", szSTL_NO[Loop_i]);
				// 적치열구분
				recPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(
						recStkPara, "YD_STK_COL_GP"));
				// 적치BED번호
				recPara.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(
						recStkPara, "YD_STK_BED_NO"));
				// 적치단번호
				recPara.setField("YD_STK_LYR_NO", ydDaoUtils.paraRecChkNull(
						recStkPara, "YD_STK_LYR_NO"));
				// 권상모음순서
				recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ[Loop_i]);

				// 작업예약재료 테이블에 등록한다.
				intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);

				if (intRtnVal < 1) {
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
			}

		} catch (Exception e) {
			szMsg = "후판창고 정리 작업요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}

	} // end of procY4ReadjWrkReq()


	/**
	 * 오퍼레이션명 : C연주장입준비작업요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procCCsChgPrepWrkReq(JDTORecord msgRecord) throws JDTOException {

		// 스케줄기준 DAO
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		// 작업예약 재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		// 공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		// 공용 METHOD
		YdUtils ydutils = new YdUtils();

		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메세지
		String szMsg = "";
		// METHOD명
		String szMethodName = "procCCsChgPrepWrkReq";
		// 사용자
		String szUser = "SYSTEM";

		// 레코드 선언
		JDTORecord recPara = null;
		JDTORecord recStkPara = null;
		// 레코드셋 선언
		JDTORecordSet rsResult = null;

		// 설비ID(열구분)
		String szYD_STK_COL_GP = null;
		// 적치BED번호
		String szYD_STK_BED_NO = null;
		// 목표적치열구분
		String szYD_STK_COL_GP_TO = null;
		// 재료매수(int)
		int intMtlCnt = 0;
		// 재료번호
		String[] szSTL_NO = null;
		// 권상모음순서
		String[] szYD_UP_COLL_SEQ = null;
		// 스케줄코드
		String szYD_SCH_CD = null;
		// 야드스케쥴우선순위
		String szYD_SCH_PRIOR = null;
		// 스케줄 금지 유무
		String szYD_SCH_PROH_EXN = null;
		// 작업크레인
		String szYD_WRK_CRN = null;
		// 작업크레인우선순위
		String szYD_WRK_CRN_PRIOR = null;
		// 대체크레인유무
		String szYD_ALT_CRN_YN = null;
		// 대체크레인
		String szYD_ALT_CRN = null;
		// 대체크레인우선순위
		String szYD_ALT_CRN_PRIOR = null;
		// 선택크레인
		String szCrn = null;
		// 작업예약ID
		String szYD_WBOOK_ID = null;

		// TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		// 에러 리턴
		if (szRcvTcCode == null) {

			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error ("
					+ szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;

		}
		// TC CODE DISPLAY
		if (bDebugFlag) {

			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		}

		try {

			// 받은 전문 편집
			// 스케줄코드
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			if (szYD_SCH_CD.equals("")) {

				szMsg = "[전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_COL_GP");
			if (szYD_STK_COL_GP.equals("")) {

				szMsg = "[전문 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 적치BED번호
			/*
			 * szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord,
			 * "YD_STK_BED_NO"); if (szYD_STK_BED_NO.equals("")) {
			 * 
			 * szMsg = "[전문 이상] 적치BED번호가 없습니다."; ydUtils.putLog(szSessionName,
			 * szMethodName, szMsg, YdConstant.ERROR); return;
			 * 
			 * }
			 */
			// 목표적치열구분
			szYD_STK_COL_GP_TO = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_COL_GP_TO");
			if (szYD_STK_COL_GP_TO.equals("")) {

				szMsg = "[전문 이상] 목표적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_LOT_GP_SH");
			if (intMtlCnt == 0) {

				szMsg = "[전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 작업계획대차
			String szYD_WRK_PLAN_TCAR = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_WRK_PLAN_TCAR");

			// 재료번호
			szSTL_NO = new String[intMtlCnt + 1];
			// 권상모음순서
			szYD_UP_COLL_SEQ = new String[intMtlCnt + 1];
			

			// 재료번호, 권상모음순서
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,
						"STL_NO" + Loop_i);
				if (szSTL_NO[Loop_i].equals("")) {

					szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				}
				// 권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,
						"YD_UP_COLL_SEQ" + Loop_i);
				if (szYD_UP_COLL_SEQ[Loop_i].equals("")) {

					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i]
							+ ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				}
			}

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal)
				return;

			// 레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();

			// 스케줄CD 체크
			// 스케줄 금지 유무
			szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");
			// 작업크레인
			szYD_WRK_CRN = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
			// 작업크레인우선순위
			szYD_WRK_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara,	"YD_WRK_CRN_PRIOR").trim();
			// 대체크레인유무
			szYD_ALT_CRN_YN = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
			// 대체크레인
			szYD_ALT_CRN = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
			// 대체크레인우선순위
			szYD_ALT_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara,	"YD_ALT_CRN_PRIOR").trim();

			// 스케줄 금지 유무가 "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if (szYD_SCH_PROH_EXN.equals("Y")) {

				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}

			// 작업크레인 설비 상태 체크
			blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);

			// 작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if (!blnRtnVal) {

				szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				// 대체크레인의 유무를 체크한다.
				// 대체크레인이 없으면 에러 리턴
				if (!szYD_ALT_CRN_YN.equals("Y")) {

					szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				}
				// 대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				// 대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {

					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				} else {
					// 대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					szCrn = szYD_ALT_CRN;
					szYD_SCH_PRIOR = szYD_ALT_CRN_PRIOR;
				}
			} else {
				// 작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				szCrn = szYD_WRK_CRN;
				szYD_SCH_PRIOR = szYD_WRK_CRN_PRIOR;
			}

			// 재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다.
			// 작업예약재료 등록 여부를 체크한다.
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 크레인사양과 저장품 사양을 체크(길이,폭,중량)
				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
				if (!blnRtnVal)
					return;

				// 다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if (!blnRtnVal)
					return;

			}

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if (!blnRtnVal)
				return;
			// 레코드추출
			rsResult.first();
			recPara = rsResult.getRecord();
			// 작업예약ID
			szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");

			// INSERT 항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			// 야드구분
			String szYD_GP = szYD_SCH_CD.substring(0, 1);
			// 동구분
			String szYD_BAY_GP = szYD_SCH_CD.substring(1, 2);
			// 야드구분
			String szYD_GP_TO = szYD_STK_COL_GP_TO.substring(0, 1);
			// 동구분
			String szYD_BAY_GP_TO = szYD_STK_COL_GP_TO.substring(1, 2);

			// INSERT할 항목 SET
			/*
			 * recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			 * recPara.setField("YD_GP", szYD_GP); recPara.setField("YD_BAY_GP",
			 * szYD_BAY_GP); recPara.setField("YD_SCH_CD", szYD_SCH_CD);
			 * recPara.setField("REGISTER", szUser);
			 */

			// INSERT할 항목 SET
			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID); // 작업예약ID
			recPara.setField("YD_GP", szYD_GP); // 야드구분
			recPara.setField("YD_BAY_GP", szYD_BAY_GP); // 야드동구분
			recPara.setField("YD_SCH_CD", szYD_SCH_CD); // 야드스케쥴코드
			recPara.setField("YD_SCH_PRIOR", szYD_SCH_PRIOR); // 야드스케쥴우선순위
			recPara.setField("YD_AIM_YD_GP", szYD_GP_TO); // 야드목표야드구분
			recPara.setField("YD_AIM_BAY_GP", szYD_BAY_GP_TO); // 야드목표동구분
			recPara.setField("YD_WRK_PLAN_TCAR", szYD_WRK_PLAN_TCAR); // 작업계획대차(없는 경우  빈문자로  설정)
			// 추후 추가항목
			// 야드스케쥴진행상태
			// 야드스케쥴기동구분
			// 야드스케쥴요청구분
			recPara.setField("REGISTER", szUser);

			// 작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}

			// 조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();

			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			// recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			// recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", szUser);

			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 리턴 recordSet 생성
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				// 재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				blnRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C",
						rsResult);
				if (!blnRtnVal) {
					szMsg = "재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져오는 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					// 예외를 발생시켜 롤백 시킴
					throw new DAOException(szSessionName + " : " + szMethodName
							+ " - " + szMsg);
					// return;
				}

				// 레코드추출
				rsResult.first();
				recStkPara = rsResult.getRecord();

				// 재료번호
				recPara.setField("STL_NO", szSTL_NO[Loop_i]);
				// 적치열구분
				recPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(
						recStkPara, "YD_STK_COL_GP"));
				// 적치BED번호
				recPara.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(
						recStkPara, "YD_STK_BED_NO"));
				// 적치단번호
				recPara.setField("YD_STK_LYR_NO", ydDaoUtils.paraRecChkNull(
						recStkPara, "YD_STK_LYR_NO"));
				// 권상모음순서
				recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ[Loop_i]);

				// 작업예약재료 테이블에 등록한다.
				intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);

				if (intRtnVal < 1) {
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					// 예외를 발생시켜 롤백 시킴
					throw new DAOException(szSessionName + " : " + szMethodName
							+ " - " + szMsg);
				}
			}

			// C연주크레인스케쥴메인 호출 : 스케쥴코드, 설비ID(크레인)
			/*
			 * recPara = JDTORecordFactory.getInstance().create();
			 * recPara.setField("JMS_TC_CD", "YDYDJ500");
			 * recPara.setField("YD_SCH_CD", szYD_SCH_CD);
			 * recPara.setField("YD_EQP_ID", szCrn);
			 */

			/*
			 * ++++++++++++++++++++++++++++++++++++++/ 
			 * //동내이적 시는 크레인스케쥴메인 호출
			 * //동간이적 시는 대차상차스케쥴 호출 
			 * +++++++++++++++++++++++++++++++++++++++++*/
			//ydDelegate.sendMsg(recPara);

		} catch (JDTOException e) {
			szMsg = "C연주 장입 준비작업 요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(e);
		}

	} // end of procCCsChgPrepWrkReq()
	
	/**
	 * 오퍼레이션명 : C연주 이적(동내,동간) LOT편성
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procCCMvLotComp(JDTORecord msgRecord) throws JDTOException  {
		YdDelegate ydDelegate = new YdDelegate();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		//공용 METHOD
		YdUtils ydutils       = new YdUtils();
		
		//레코드 선언
		JDTORecord recPara     = null;
		JDTORecord recOutPara  = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		
		//리턴값(boolean)
		boolean blnRtnVal      = false;
		int intRtnVal 			= 0;
		
		//메세지
		String szMsg           = "";
		//메소드명
		String szMethodName    = "procCCMvLotComp";
		//오퍼레이션명
		String szOperationName = "이적(동내,동간) LOT편성";
		
		
		//Lot 편성 재료 매수
		int intLotGpSh           = 0;
		//전문 생성 일시
		String szDate             = null;
		//설비ID
		String szYD_EQP_ID			= null;
		//야드구분
		String szYD_GP			= null;
		//동구분
		String szYD_BAY_GP		= null;
		//목표야드구분
		String szYD_AIM_YD_GP			= null;
		//목표동구분
		String szYD_AIM_BAY_GP		= null;
		//목표행선구분
		String szYD_AIM_RT_GP      = null;
		//스케줄코드
		String szYD_SCH_CD         = null;
		//재료번호
		String szSTL_NO            = null;
		//재료품목
		String szYD_MTL_ITEM = null;
		//대차
		String szTCAR = null;


		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if (szRcvTcCode == null) {
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		
		}
		//TC CODE DISPLAY
		if (bDebugFlag) {
		
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		try {
			//받은 전문 편집
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").trim();
			if (szYD_EQP_ID.equals("")) {
				
				szMsg = "[전문 이상] 설비ID(크레인설비ID)가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//동구분
			/*szYD_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP").trim();
			if (szYD_BAY_GP.equals("")) {
				
				szMsg = "[전문 이상] 동구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}*/
			//목표행선구분
			szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_RT_GP").trim();
			/*if (szYD_AIM_RT_GP.equals("")) {
				
				szMsg = "[전문 이상] 목표행선구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}*/
			//목표동구분
			szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_BAY_GP").trim();
			//야드구분
			szYD_GP = szYD_EQP_ID.substring(0, 1);
			//동구분
			szYD_BAY_GP = szYD_EQP_ID.substring(1, 2);
			//목표야드구분
			szYD_AIM_YD_GP = szYD_GP;
			
			
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord recInParam = JDTORecordFactory.getInstance().create();
			recInParam.setField("YD_GP", szYD_GP);					//야드
			recInParam.setField("YD_BAY_GP", szYD_BAY_GP);			//동
			//BRE RULE
			
			intRtnVal = chkGetMvPrepLotGP(recInParam, rsResult);
			
			if( intRtnVal <=0 ) return;
				
			intLotGpSh = rsResult.size();
			
			//레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			//JMS TC CODE
			//recOutPara.setField("JMS_TC_CD",          "YDYDJ263");
			//레코드 커서 처음으로
			rsResult.first();
			
			//보급 Lot 편성 재료 매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {
				
				//레코드 추출
				recPara = rsResult.getRecord();
				
				//재료번호 
				szSTL_NO = ydDaoUtils.paraRecChkNull(recPara, "STL_NO").trim();
				//적치단재료상태
				//szYD_STK_LYR_MTL_STAT = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_MTL_STAT").trim();
				//비교목표행선구분
				//szYD_AIM_RT_GP_COMP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_RT_GP").trim();
					
				//다른 크레인 스케줄에 등록 되어 있는지 체크
				blnRtnVal = chkCrnWrkMtl(szSTL_NO);
				if (!blnRtnVal) return;
				
				
				// 재료품목[첫번째 레코드를 대표값으로 사용]
				if( Loop_i == 1 ) {
					szYD_MTL_ITEM = ydDaoUtils.paraRecChkNull(recPara, "YD_MTL_ITEM").trim();
				}
				//재료번호
				recOutPara.setField("STL_NO" + Loop_i, szSTL_NO);
				//권상모음순서
				recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, "" + Loop_i);

				//다음 레코드로
				rsResult.next();
				
			}
			/*szYD_AIM_YD_GP = szYD_GP;
			
			if( szYD_AIM_RT_GP.equals("C7") ) {			//목표행선구분 : C연주 B열연 이송대기
				szYD_BAY_GP_TO = "D";					// ---> 목표동 : D동
			}else if( szYD_AIM_RT_GP.equals("C8") || szYD_AIM_RT_GP.equals("C9")) {	//목표행선구분 : C연주 통합야드이송대기, C연주 외판출하대기
				szYD_BAY_GP_TO = "C";					// ---> 목표동 : C동
			}*/
			
			//스케쥴코드 생성
			/* 스케쥴코드 생성
			 * 현재동 = 목표동 : 동내이적
			 * 현재동 <> 목표동 : 동간이적 - 대차배정 필요
			 */
			
			recInParam = JDTORecordFactory.getInstance().create();
			YdCommonUtils.mkCrnSchCdForMv(recInParam, szYD_GP, szYD_BAY_GP, szYD_AIM_YD_GP, szYD_AIM_BAY_GP, szYD_MTL_ITEM, szYD_AIM_RT_GP);
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(recInParam, "YD_SCH_CD").trim();
			szTCAR = YdCommonUtils.sltTCarByYD(szYD_GP, szYD_BAY_GP, szYD_AIM_YD_GP, szYD_AIM_BAY_GP, szYD_MTL_ITEM, szYD_AIM_RT_GP);
			if(szYD_SCH_CD.equals("")) {
				szMsg = szOperationName + " 처리중 스케쥴코드 할당 실패";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//스케쥴코드를 할당 못 받더라도 기본 스케쥴코드 생성하는 로직이 필요함
				return;
			}
			
			//전문 발생 일시
			szDate = ydUtils.getCurDate("yyyy/MM/dd HH:mm:ss");

			//발생 일시
			recOutPara.setField("JMS_TC_CREATE_DDTT", szDate);
			//스케줄코드
			recOutPara.setField("YD_SCH_CD",          szYD_SCH_CD);
			//Lot편성 매수
			recOutPara.setField("YD_LOT_GP_SH",       "" + intLotGpSh);
			//야드구분
			recOutPara.setField("YD_GP",      szYD_GP);
			//동구분
			recOutPara.setField("YD_BAY_GP",      szYD_BAY_GP);
			//목표야드구분
			recOutPara.setField("YD_GP_TO",      szYD_AIM_YD_GP);
			//목표동구분
			recOutPara.setField("YD_BAY_GP_TO",      szYD_AIM_BAY_GP);
			//작업계획대차
			recOutPara.setField("YD_WRK_PLAN_TCAR",      szTCAR);
			
			// 전문내용 로그 출력
			ydUtils.displayRecord(szOperationName, recOutPara);

			// 전문 송신 - 내부JMS송신
			//ydDelegate.sendMsg(recOutPara);
			//메소드 콜
			procFtmvOrdLotReq(recOutPara);
			
			szMsg = szOperationName + " 후  작업요구 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		} catch(Exception e) {
			szMsg = szOperationName + " Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
	}  //end of procCCMvLotComp
	
	/**
	 * 오퍼레이션명 : 사용안함. 2010.09.14 윤재광
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procCCPrepMvLotComp(JDTORecord msgRecord) throws JDTOException  {
		YdDelegate ydDelegate = new YdDelegate();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		//공용 METHOD
		YdUtils ydutils       = new YdUtils();
		
		//레코드 선언
		JDTORecord recPara     = null;
		JDTORecord recOutPara  = null;
		JDTORecord recInParam	= null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		JDTORecordSet rsTemp = null;
		
		//리턴값(boolean)
		boolean blnRtnVal      = false;
		int intRtnVal 			= 0;
		
		//메세지
		String szMsg           = "";
		//메소드명
		String szMethodName    = "procCCPrepMvLotComp";
		//오퍼레이션명
		String szOperationName = "준비스케줄 LOT 편성";
		
		
		//Lot 편성 재료 매수
		int intLotGpSh           = 0;
		//전문 생성 일시
		String szDate             = null;
		//설비ID
		String szYD_EQP_ID			= null;
		//야드구분
		String szYD_GP			= null;
		//동구분
		String szYD_BAY_GP		= null;
		//스판구분
		String szYD_SPAN_GP		= null;
		//목표야드구분
		String szYD_AIM_YD_GP			= null;
		//목표동구분
		String szYD_AIM_BAY_GP		= null;
		//목표스판구분
		String szYD_AIM_SPAN_GP		= null;
		//목표적치열구분
		String szYD_AIM_COL_GP		= null;
		//목표적치BED구분				
		String szYD_AIM_BED_NO		= null;
		//목표행선구분
		String szYD_AIM_RT_GP      = null;
		//주작업이적구분
		String szYD_MAIN_WRK_GP		= null;
		//TO위치가이드구분
		String szYD_TO_LOC_GUIDE_GP	= null;
		//스케줄코드
		String szYD_SCH_CD         = null;
		//재료번호
		String szSTL_NO            = null;
		//재료품목
		String szYD_MTL_ITEM = null;
		//대차
		String szTCAR = "";
		//적치열구분
		String szYD_STK_COL_GP = "";
		//이전 적치열구분 - 비교를 위해서 임의의 값을 설정
		String szYD_STK_COL_GP_CMP = "XX01";
		//야드 TO위치결정방법
		String szYD_TO_LOC_DCSN_MTD = "";
		//야드TO위치가이드
		String szYD_TO_LOC_GUIDE = "";
		//권상모음으로 처리 유무
		boolean bUpCol = true;
		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if (szRcvTcCode == null) {
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		
		}
		//TC CODE DISPLAY
		if (bDebugFlag) {
		
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		try {
			/*
			 * 야드구분, 동구분, 스판(선택), 목표행선구분, 목표동, 주작업이적구분, TO위치가이드구분
			 */
			//받은 전문 편집
//			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").trim();
//			if (szYD_EQP_ID.equals("")) {
//				
//				szMsg = "[전문 이상] 설비ID(크레인설비ID)가 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return;
//				
//			}
			szYD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP").trim();
			if (szYD_GP.equals("")) {
				
				szMsg = "[전문 이상] 야드구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//동구분
			szYD_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP").trim();
			if (szYD_BAY_GP.equals("")) {
				
				szMsg = "[전문 이상] 동구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			
			//스판구분
			szYD_SPAN_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SPAN_GP").trim();
//			if (szYD_SPAN_GP.equals("")) {
//				
//				szMsg = "[전문 이상] 스판구분이 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return;
//				
//			}
			
			//목표행선구분
			szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_RT_GP").trim();
			if (szYD_AIM_RT_GP.equals("")) {
				
				szMsg = "[전문 이상] 목표행선구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			
			//주작업이적구분
			szYD_MAIN_WRK_GP  = ydDaoUtils.paraRecChkNull(msgRecord, "YD_MAIN_WRK_GP").trim();
			if (szYD_MAIN_WRK_GP.equals("")) {
				
				szMsg = "[전문 이상] 주작업이적구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}
			//TO위치가이드구분
			szYD_TO_LOC_GUIDE_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_TO_LOC_GUIDE_GP").trim();
			if (szYD_TO_LOC_GUIDE_GP.equals("")) {
				
				szMsg = "[전문 이상] TO위치가이드구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
				
			}else if(szYD_TO_LOC_GUIDE_GP.equals("Y")) {
				//목표야드구분
				szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_YD_GP").trim();
				if (szYD_AIM_YD_GP.equals("")) {
					
					szMsg = "[전문 이상] 목표야드구분이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
				}
				//목표동구분
				szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_BAY_GP").trim();
				if (szYD_AIM_BAY_GP.equals("")) {
					
					szMsg = "[전문 이상] 목표동구분이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
				}
				//목표스판구분
				szYD_AIM_SPAN_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_SPAN_GP").trim();
				if (szYD_AIM_SPAN_GP.equals("")) {
					
					szMsg = "[전문 이상] 목표스판구분이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
				}
				//목표적치열구분
				szYD_AIM_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_COL_GP").trim();
				if (szYD_AIM_COL_GP.equals("")) {
					
					szMsg = "[전문 이상] 목표적치열구분이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
					
				}
				
				//목표적치BED구분
				szYD_AIM_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_BED_NO").trim();
			}
			//야드구분
			//szYD_GP = szYD_EQP_ID.substring(0, 1);
			//동구분
			//szYD_BAY_GP = szYD_EQP_ID.substring(1, 2);
			//목표야드구분
			szYD_AIM_YD_GP = szYD_GP;
			
			
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			if(szYD_MAIN_WRK_GP.equals("Y")) {		//주작업이적인 경우
				recInParam = JDTORecordFactory.getInstance().create();
				recInParam.setField("YD_STK_COL_GP", szYD_GP + szYD_BAY_GP + szYD_SPAN_GP);					//야드 + 동 + 스판(옵션)
				recInParam.setField("YD_AIM_RT_GP", szYD_AIM_RT_GP);										//야드목표행선구분
				
				//이적준비 대상재 검색
				intRtnVal = chkMvLotGp(recInParam, rsResult);
				
				if( intRtnVal <=0 ) return;
			}else{									//상단의 더미재 이적인 경우
				// 상단 더미재 이적 Lot 편성 대상 재료 Select
				blnRtnVal = chkGetMvDummyMtlAboveTgMtlLot(szYD_GP, szYD_BAY_GP, szYD_SPAN_GP, szYD_AIM_RT_GP, -1, rsResult);
				if (!blnRtnVal) return;
			}
			
			intLotGpSh = rsResult.size();
			
			//레코드 생성
			//recOutPara = JDTORecordFactory.getInstance().create();
			//JMS TC CODE
			//recOutPara.setField("JMS_TC_CD",          "YDYDJ263");
			//레코드 커서 처음으로
			rsResult.first();
			
			Vector rsGroup = new Vector();
			//동일한 스판별로 대상재를 편성한다.
			//보급 Lot 편성 재료 매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {
				
				//레코드 추출
				recPara = rsResult.getRecord();
				
				//재료번호 
				//szSTL_NO = ydDaoUtils.paraRecChkNull(recPara, "STL_NO").trim();
				//적치열구분
				szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP").trim();
				//동일한 스판이 아닌 경우
				if( !szYD_STK_COL_GP.substring(0, 4).equals(szYD_STK_COL_GP_CMP.substring(0, 4)) ) {
					rsTemp = JDTORecordFactory.getInstance().createRecordSet("");
					rsGroup.add(rsTemp);
				}
				rsTemp.addRecord(recPara);
				szYD_STK_COL_GP_CMP = szYD_STK_COL_GP;
				//비교목표행선구분
				//szYD_AIM_RT_GP_COMP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_RT_GP").trim();
				/*
				 * 업무에 대한 정확한 정의후 결정
				 * 1. 다른 작업예약에 등록되어 있는 지 체크
				 * 
				 * 2. 다른 크레인 스케줄에 등록 되어 있는지 체크
				 *	  blnRtnVal = chkCrnWrkMtl(szSTL_NO);
				 *	  if (!blnRtnVal) return;
				 */
					

				//다음 레코드로
				rsResult.next();
				
			}
			
			
			szMsg = "대상재 작업예약 갯수 : " + rsGroup.size();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//전문 발생 일시
			szDate = YdUtils.getCurDate("yyyy/MM/dd HH:mm:ss");
			
			for(int Loop_i = 0 ; Loop_i < rsGroup.size(); Loop_i++) {
				rsTemp = (JDTORecordSet)rsGroup.get(Loop_i);
				for(int Loop_j = 0; Loop_j < rsTemp.size(); Loop_j++ ) {
					rsTemp.absolute(Loop_j + 1);
					recPara = rsTemp.getRecord();
					
					if( Loop_j == 0 ) {
						//야드 TO위치결정방법
						szYD_TO_LOC_DCSN_MTD = "";
						//야드TO위치가이드
						szYD_TO_LOC_GUIDE = "";
						//대차
						szTCAR = "";
						//권상모음으로 처리 유무
						bUpCol = true;
						//레코드 생성
						recOutPara = JDTORecordFactory.getInstance().create();
						
						szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP").trim();
						
						//TO위치GUIDE이고 목표동이 다른 경우에는 대차상차스케줄을 생성하고 대차 배정
						if(szYD_TO_LOC_GUIDE_GP.equals("Y")) {				//TO위치GUIDE 이적인 경우
							szYD_TO_LOC_DCSN_MTD = "F";
							szYD_TO_LOC_GUIDE	= szYD_AIM_YD_GP + szYD_AIM_BAY_GP + szYD_AIM_SPAN_GP + szYD_AIM_COL_GP + szYD_AIM_BED_NO;
							
							szMsg = "TO위치GUIDE 이적인 경우 szYD_TO_LOC_GUIDE : " + szYD_TO_LOC_GUIDE;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							
							if( szYD_BAY_GP.equals(szYD_AIM_BAY_GP) )	{	//목표동이 같은 경우 - 동내이적 스케줄
								szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "YD" + szYD_STK_COL_GP.substring(2, 4) + "MM";
								szMsg = "목표동이 같은 경우 - 동내이적 스케줄 : " + szYD_SCH_CD + ", " + szTCAR;
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							}else{											//목표동이 다른 경우 - 대차상차스케줄
								String[] retValue = YdCommonUtils.getSchCdNTcar(szYD_STK_COL_GP);
								szYD_SCH_CD = retValue[0];
								szTCAR = retValue[1];
								szMsg = "목표동이 다른 경우 - 대차상차스케줄[동간이적] : " + szYD_SCH_CD + ", " + szTCAR;
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							}
						}else{			//위치검색기준으로 이적인 경우
							szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_YD_GP").trim();
							szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_BAY_GP").trim();
							
							szMsg = "위치검색기준으로 이적인 경우";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							
							if(szYD_MAIN_WRK_GP.equals("Y")) {		//주작업이적인 경우
								szMsg = "주작업이적인 경우";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								if( szYD_BAY_GP.equals(szYD_AIM_BAY_GP) ) {		//목표동이 같은 경우 - 동내이적 스케줄
									szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "YD" + szYD_STK_COL_GP.substring(2, 4) + "MM";
									szMsg = "목표동이 같은 경우 - 동내이적 스케줄 : " + szYD_SCH_CD + ", " + szTCAR;
									ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								}else{											//목표동이 다른 경우 - 대차상차스케줄
									String[] retValue = YdCommonUtils.getSchCdNTcar(szYD_STK_COL_GP);
									szYD_SCH_CD = retValue[0];
									szTCAR = retValue[1];
									szMsg = "목표동이 다른 경우 - 대차상차스케줄[동간이적] : " + szYD_SCH_CD + ", " + szTCAR;
									ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								}
							}else{									//상단의 더미재 이적인 경우
								//권상모음을 제거한다.
								bUpCol = false;
								szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "YD" + szYD_STK_COL_GP.substring(2, 4) + "MM";
								szMsg = "상단의 더미재 이적인 경우[동내이적] " + szYD_SCH_CD + ", " + szTCAR;
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							}
						}
						
						//발생 일시
						recOutPara.setField("JMS_TC_CREATE_DDTT", szDate);
						//스케줄코드
						recOutPara.setField("YD_SCH_CD",          szYD_SCH_CD);
						//Lot편성 매수
						recOutPara.setField("YD_LOT_GP_SH",       "" + rsTemp.size());
						//야드구분
						recOutPara.setField("YD_GP",      szYD_GP);
						//동구분
						recOutPara.setField("YD_BAY_GP",      szYD_BAY_GP);
						//목표야드구분
						recOutPara.setField("YD_AIM_YD_GP",      szYD_AIM_YD_GP);
						//목표동구분
						recOutPara.setField("YD_AIM_BAY_GP",      szYD_AIM_BAY_GP);
						//야드 TO위치결정방법
						recOutPara.setField("YD_TO_LOC_DCSN_MTD",      szYD_TO_LOC_DCSN_MTD);
						//야드TO위치가이드
						recOutPara.setField("YD_TO_LOC_GUIDE",      szYD_TO_LOC_GUIDE);
						//작업계획대차
						recOutPara.setField("YD_WRK_PLAN_TCAR",      szTCAR);
					}
					szSTL_NO = ydDaoUtils.paraRecChkNull(recPara, "STL_NO").trim();
					//재료번호
					recOutPara.setField("STL_NO" + (Loop_j + 1), szSTL_NO);
					//권상모음순서
					recOutPara.setField("YD_UP_COLL_SEQ" + (Loop_j + 1), ( bUpCol ? "" + (Loop_j + 1) : "" ) );
				}
				// 전문내용 로그 출력
				ydUtils.displayRecord(szOperationName, recOutPara);
				
				procFtmvOrdLotReq(recOutPara);
			}
			

			
			
			// 전문내용 로그 출력
			// ydUtils.displayRecord(szOperationName, recOutPara);
			// 전문 송신 - 내부JMS송신
			//ydDelegate.sendMsg(recOutPara);
			//메소드 콜
			//procFtmvOrdLotReq(recOutPara);
			
			szMsg = szOperationName + " 후  작업요구 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		} catch(Exception e) {
			szMsg = szOperationName + " Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
	}  //end of procCCPrepMvLotComp
	
	/**
	 * 오퍼레이션명 : 준비스케줄 LOT편성 - 화면으로 부터 호출, 2009.06.25 임춘수 추가
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String procCCPrepLotCompByCapa(JDTORecord msgRecord) throws JDTOException  {
		YdDelegate ydDelegate = new YdDelegate();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		//공용 METHOD
		YdUtils ydutils       = new YdUtils();
		
		// 작업예약 DAO
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		
		//레코드 선언
		JDTORecord recPara     = null;
		JDTORecord recOutPara  = null;
		JDTORecord recInParam	= null;
		JDTORecord recResult 	= null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		JDTORecordSet rsTemp = null;
		
		//리턴값(boolean)
		boolean blnRtnVal      = false;
		int intRtnVal 			= 0;
		
		//메세지
		String szMsg           = "";
		//메소드명
		String szMethodName    = "procCCPrepLotCompByCapa";
		//오퍼레이션명
		String szOperationName = "준비스케줄 LOT 편성";
		
		
		//Lot 편성 재료 매수
		int intLotGpSh           = 0;
		//전문 생성 일시
		String szDate             = null;
		//설비ID
		String szYD_EQP_ID			= null;
		//야드구분
		String szYD_GP			= null;
		//동구분
		String szYD_BAY_GP		= null;
		//스판구분
		String szYD_SPAN_GP		= null;
		//목표야드구분
		String szYD_AIM_YD_GP			= null;
		//목표동구분
		String szYD_AIM_BAY_GP		= null;
		//목표스판구분
		String szYD_AIM_SPAN_GP		= null;
		//목표적치열구분
		String szYD_AIM_COL_GP		= null;
		//목표적치BED구분				
		String szYD_AIM_BED_NO		= null;
		//목표행선구분
		String szYD_AIM_RT_GP      = null;
		//주작업이적구분
		String szYD_MAIN_WRK_GP		= null;
		//TO위치가이드구분
		String szYD_TO_LOC_GUIDE_GP	= null;
		//스케줄코드
		String szYD_SCH_CD         = "";
		//스케줄코드
		String szPREV_YD_SCH_CD         = "";
		//재료번호
		String szSTL_NO            = null;
		//재료품목
		String szYD_MTL_ITEM = null;
		//적치열배열
		String[] arrYD_STK_COL_GP = null;
		//적치베드 배열
		String[] arrYD_STK_BED_NO = null;
		//대차
		String szTCAR = "";
		//대차
		String szPREV_TCAR = "";
		//적치열구분
		String szYD_STK_COL_GP = "";
		//이전 적치열구분 - 비교를 위해서 임의의 값을 설정
		String szYD_STK_COL_GP_CMP = "XX01";
		//야드 TO위치결정방법
		String szYD_TO_LOC_DCSN_MTD = "";
		//야드TO위치가이드
		String szYD_TO_LOC_GUIDE = "";
		//야드TO위치가이드
		String szSTL_LIST = "";
		//권상모음으로 처리 유무
		boolean bUpCol = true;
		//재료의 현재 폭
		double dblCurrWidth			= 0;
		//재료의 현재 중량
		long lngCurrWt				= 0;
		//누적중량
		long lngSumWt 				= 0;
		//크레인작업가능매수
		int intMtlSh				= 0;
		//크레인작업가능 폭
		double dblMaxWidth			= 0;
		//동내이적인 지 동간이적인 지 판단하는 변수
		boolean bIsInsideMv			= true;
		//대차적치가능중량
		int intTcarWtCapa			= YdConstant.TCAR_WT_CAPA;
		//
		String szYD_TC_GP			= null;
		//작업매수
		String szYD_EQP_WRK_SH		= null;
		//작업매수
		int intYD_EQP_WRK_SH		= -1;
		
		String szARR_WLOC_CD		= null;
		
		//리턴메세지정의
		String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if (szRcvTcCode == null) {
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_TC_ERROR;
		
		}
		//TC CODE DISPLAY
		if (bDebugFlag) {
		
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		try {
			/*
			 * 야드구분, 동구분, 스판(선택), 목표행선구분, 목표동, 주작업이적구분, TO위치가이드구분
			 */
			//받은 전문 편집
			szYD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP").trim();
			if (szYD_GP.equals("")) {
				
				szMsg = "[전문 이상] 야드구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_TC_ERROR;
				
			}
			//동구분
			szYD_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP").trim();
			if (szYD_BAY_GP.equals("")) {
				
				szMsg = "[전문 이상] 동구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_TC_ERROR;
				
			}
			
			//스판구분
			szYD_SPAN_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SPAN_GP").trim();
//			if (szYD_SPAN_GP.equals("")) {
//				
//				szMsg = "[전문 이상] 스판구분이 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return;
//				
//			}
			
			//적치열구분
			arrYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP").split(";");
			if (arrYD_STK_COL_GP == null || arrYD_STK_COL_GP.length == 0) {
				
				szMsg = "[전문 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_TC_ERROR;
				
			}
			
			//적치열구분
			arrYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO").split(";");
			if (arrYD_STK_BED_NO == null || arrYD_STK_BED_NO.length == 0) {
				
				szMsg = "[전문 이상] 적치베드번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_TC_ERROR;
				
			}
			
			//목표행선구분
			szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_RT_GP").trim();
			if (szYD_AIM_RT_GP.equals("")) {
				
				szMsg = "[전문 이상] 목표행선구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_TC_ERROR;
				
			}
			
			//주작업이적구분
			szYD_MAIN_WRK_GP  = ydDaoUtils.paraRecChkNull(msgRecord, "YD_MAIN_WRK_GP").trim();
			if (szYD_MAIN_WRK_GP.equals("")) {
				
				szMsg = "[전문 이상] 주작업이적구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_TC_ERROR;
				
			}
			//TO위치가이드구분
			szYD_TO_LOC_GUIDE_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_TO_LOC_GUIDE_GP").trim();
			if (szYD_TO_LOC_GUIDE_GP.equals("")) {
				
				szMsg = "[전문 이상] TO위치가이드구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_TC_ERROR;
				
			}else if(szYD_TO_LOC_GUIDE_GP.equals("Y")) {
				//목표야드구분
				szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_YD_GP").trim();
				if (szYD_AIM_YD_GP.equals("")) {
					
					szMsg = "[전문 이상] 목표야드구분이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_TC_ERROR;
					
				}
				//목표동구분
				szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_BAY_GP").trim();
				if (szYD_AIM_BAY_GP.equals("")) {
					
					szMsg = "[전문 이상] 목표동구분이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_TC_ERROR;
					
				}
				//목표스판구분
				szYD_AIM_SPAN_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_SPAN_GP").trim();
//				if (szYD_AIM_SPAN_GP.equals("")) {
//					
//					szMsg = "[전문 이상] 목표스판구분이 없습니다.";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//					return YdConstant.RETN_CD_TC_ERROR;
//					
//				}
				//목표적치열구분
				szYD_AIM_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_COL_GP").trim();
//				if (szYD_AIM_COL_GP.equals("")) {
//					
//					szMsg = "[전문 이상] 목표적치열구분이 없습니다.";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//					return YdConstant.RETN_CD_TC_ERROR;
//					
//				}
				
				//목표적치BED구분
				szYD_AIM_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_BED_NO").trim();
			}
			//---------------------------------------------------------------------------------------------
			//	C연주슬라브야드의 준비스케줄 등록 시 지정한 작업매수의 값을 전달하여 해당하는 매수만큼 작업예약에 등록
			//	등록자 : 임춘수
			//	등록일 : 2010.01.20
			//---------------------------------------------------------------------------------------------
			
			szYD_EQP_WRK_SH = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_WRK_SH");
			
			if( !szYD_EQP_WRK_SH.equals("") ) {
				intYD_EQP_WRK_SH			= Integer.parseInt(szYD_EQP_WRK_SH);
				
				szMsg = "사용자가 지정한 작업매수 : " + intYD_EQP_WRK_SH;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
			}
			
			//---------------------------------------------------------------------------------------------
			
			//착지개소코드
			szARR_WLOC_CD 		= ydDaoUtils.paraRecChkNull(msgRecord, "ARR_WLOC_CD");
			
			//대차선택
			szYD_TC_GP 			= ydDaoUtils.paraRecChkNull(msgRecord, "YD_TC_GP");
			
			//목표야드구분
			szYD_AIM_YD_GP = szYD_GP;
			
			
			//작업재료리스트
			szSTL_LIST 			= ydDaoUtils.paraRecChkNull(msgRecord, "STL_LIST");
			
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			if(szYD_MAIN_WRK_GP.equals("Y")) {		//주작업이적인 경우
				if("".equals(szSTL_LIST)) {
					//이적준비 대상재 검색
					intRtnVal = chkPrepLotGp(arrYD_STK_COL_GP, arrYD_STK_BED_NO, szYD_AIM_RT_GP, szARR_WLOC_CD, rsResult);
					
					if( intRtnVal ==0 ) {
						return YdConstant.RETN_CD_NOTEXIST;				//대상재가 존재하지 않는 경우
					}else if( intRtnVal < 0 ) {
						return YdConstant.RETN_CD_FAILURE;				//대상재 조회시 오류가 발생한 경우
					}
				} else {
					//작업재료LIST 검색
					intRtnVal = chkPrepLotGp_stl_list(szSTL_LIST, szYD_AIM_RT_GP, szARR_WLOC_CD, rsResult);
					
					if( intRtnVal ==0 ) {
						return YdConstant.RETN_CD_NOTEXIST;				//대상재가 존재하지 않는 경우
					}else if( intRtnVal < 0 ) {
						return YdConstant.RETN_CD_FAILURE;				//대상재 조회시 오류가 발생한 경우
					}
				}
			}else{									//상단의 더미재 이적인 경우
				if("".equals(szSTL_LIST)) {
					// 	상단 더미재 이적 Lot 편성 대상 재료 Select
					intRtnVal = chkGetMvDummyMtlAboveTgMtlLot(arrYD_STK_COL_GP, arrYD_STK_BED_NO, szYD_AIM_RT_GP, szARR_WLOC_CD, rsResult);
					if( intRtnVal ==0 ) {
						return YdConstant.RETN_CD_NOTEXIST;				//대상재가 존재하지 않는 경우
					}else if( intRtnVal < 0 ) {
						return YdConstant.RETN_CD_FAILURE;				//대상재 조회시 오류가 발생한 경우
					}
				} else {
					//작업재료LIST 검색
					intRtnVal = chkPrepLotGp_stl_list(szSTL_LIST, szYD_AIM_RT_GP, szARR_WLOC_CD, rsResult);
					
					if( intRtnVal ==0 ) {
						return YdConstant.RETN_CD_NOTEXIST;				//대상재가 존재하지 않는 경우
					}else if( intRtnVal < 0 ) {
						return YdConstant.RETN_CD_FAILURE;				//대상재 조회시 오류가 발생한 경우
					}
				}
					
			}
			
			intLotGpSh = rsResult.size();
			
			
			rsResult.first();
			
			Vector rsGroup = new Vector();
			//동일한 스판별로 대상재를 편성한다.
			//보급 Lot 편성 재료 매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {
				
				//레코드 추출
				recPara = rsResult.getRecord();
				
				//재료번호 
				szSTL_NO = ydDaoUtils.paraRecChkNull(recPara, "STL_NO").trim();
				//야드재료중량
				lngCurrWt = ydDaoUtils.paraRecChkNullLong(recPara, "YD_MTL_WT");
				//야드재료폭
				dblCurrWidth = ydDaoUtils.paraRecChkNullDouble(recPara, "YD_MTL_W");
				//적치열구분
				szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP").trim();
				
				szMsg = "작업재료확인 : 현재Count[" + Loop_i + "] 현재 재료번호[" + szSTL_NO + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				
				/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				 * 스케줄코드, 대차설비 ID 구하기 
				 ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
				//야드 TO위치결정방법
				szYD_TO_LOC_DCSN_MTD = "";
				//야드TO위치가이드
				szYD_TO_LOC_GUIDE = "";
				//대차
				szTCAR = "";
				//권상모음으로 처리 유무
				bUpCol = true;
				
				
				
				//TO위치GUIDE이고 목표동이 다른 경우에는 대차상차스케줄을 생성하고 대차 배정
				if(szYD_TO_LOC_GUIDE_GP.equals("Y")) {				//TO위치GUIDE 이적인 경우
					szYD_TO_LOC_DCSN_MTD = "F";
					szYD_TO_LOC_GUIDE	= szYD_AIM_YD_GP + szYD_AIM_BAY_GP + szYD_AIM_SPAN_GP + szYD_AIM_COL_GP + szYD_AIM_BED_NO;
					
					szMsg = "TO위치GUIDE 이적인 경우 szYD_TO_LOC_GUIDE : " + szYD_TO_LOC_GUIDE;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					if( szYD_BAY_GP.equals(szYD_AIM_BAY_GP) )	{	//목표동이 같은 경우 - 동내이적 스케줄
						bIsInsideMv = true;
						szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "YD" + szYD_STK_COL_GP.substring(2, 4) + "MM";
						szMsg = "목표동이 같은 경우 - 동내이적 스케줄 : " + szYD_SCH_CD + ", " + szTCAR;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}else{											//목표동이 다른 경우 - 대차상차스케줄
						bIsInsideMv = false;
						String[] retValue = YdCommonUtils.getSchCdNTcar(szYD_STK_COL_GP);
						szYD_SCH_CD = retValue[0];
						szTCAR = retValue[1];
						
						if( !szYD_TC_GP.equals("") ) {
							szYD_SCH_CD	= szYD_STK_COL_GP.substring(0, 2) + szYD_TC_GP.substring(2) + "UM";
							szTCAR		= szYD_TC_GP;
							szMsg = " 대차선택["+szYD_TC_GP+"]이 존재하므로 대차상차스케줄["+szYD_SCH_CD+"]";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						}
						
						szMsg = "목표동이 다른 경우 - 대차상차스케줄[동간이적] : " + szYD_SCH_CD + ", " + szTCAR;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
				}else{			//위치검색기준으로 이적인 경우
					szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_YD_GP").trim();
					szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_BAY_GP").trim();
					
					szMsg = "위치검색기준으로 이적인 경우";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					if(szYD_MAIN_WRK_GP.equals("Y")) {		//주작업이적인 경우
						szMsg = "주작업이적인 경우";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						if( szYD_BAY_GP.equals(szYD_AIM_BAY_GP) ) {		//목표동이 같은 경우 - 동내이적 스케줄
							bIsInsideMv = true;
							szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "YD" + szYD_STK_COL_GP.substring(2, 4) + "MM";
							szMsg = "목표동이 같은 경우 - 동내이적 스케줄 : " + szYD_SCH_CD + ", " + szTCAR;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						}else{											//목표동이 다른 경우 - 대차상차스케줄
							bIsInsideMv = false;
							String[] retValue = YdCommonUtils.getSchCdNTcar(szYD_STK_COL_GP);
							szYD_SCH_CD = retValue[0];
							szTCAR = retValue[1];
							
							if( !szYD_TC_GP.equals("") ) {
								szYD_SCH_CD	= szYD_STK_COL_GP.substring(0, 2) + szYD_TC_GP.substring(2) + "UM";
								szTCAR		= szYD_TC_GP;
								szMsg = " 대차선택["+szYD_TC_GP+"]이 존재하므로 대차상차스케줄["+szYD_SCH_CD+"]";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							}
							
							szMsg = "목표동이 다른 경우 - 대차상차스케줄[동간이적] : " + szYD_SCH_CD + ", " + szTCAR;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						}
					}else{									//상단의 더미재 이적인 경우
						//권상모음을 제거한다.
						bUpCol = false;
						szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "YD" + szYD_STK_COL_GP.substring(2, 4) + "MM";
						szMsg = "상단의 더미재 이적인 경우[동내이적] " + szYD_SCH_CD + ", " + szTCAR;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
				}
				
				szMsg = "[준비스케줄 LOT편성-화면] >>>>> TO위치결정방법[" + szYD_TO_LOC_DCSN_MTD + "], TO위치GUIDE[" + szYD_TO_LOC_GUIDE + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				szMsg = "[준비스케줄 LOT편성-화면] >>>>> 재료번호 [" + szSTL_NO + "] : 야드재료중량[" + lngCurrWt + "], 야드재료폭[" + dblCurrWidth + "], 적치열구분[" + szYD_STK_COL_GP + "], 목표동[" + szYD_AIM_BAY_GP + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				
				/*
				 * 스케줄기준과 크레인 할당
				 */
				if( !szPREV_YD_SCH_CD.equals(szYD_SCH_CD) ) {
					szMsg = "[준비스케줄 LOT편성-화면] 스케줄코드비교 >>>>> YD_SCH_CD[" + szYD_SCH_CD + "], PREV_YD_SCH_CD[" + szPREV_YD_SCH_CD + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					recResult = JDTORecordFactory.getInstance().create();
					intRtnVal = YdCommonUtils.getCrnInfoByCrnSchRule(szYD_SCH_CD, recResult);
					if( intRtnVal <= 0 ) return YdConstant.RETN_CD_FAILURE;
				}
				
				//대차적치가능 중량 구하기
				if( !szPREV_TCAR.equals(szTCAR) ) {
					//rsResult = JDTORecordFactory.getInstance().createRecordSet("");
					//YdCommonUtils.chkGetEqp(szTCAR, rsResult);
					//intTcarWtCapa = ?
					szMsg = "[준비스케줄 LOT편성-화면] 대차설비비교 >>>>> TCAR[" + szTCAR + "], PREV_TCAR[" + szPREV_TCAR + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
				
				/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
				
				//동일한 스판이 아닌 경우
				if( !szYD_STK_COL_GP.substring(0, 4).equals(szYD_STK_COL_GP_CMP.substring(0, 4)) ) {
					rsTemp = JDTORecordFactory.getInstance().createRecordSet("");
					rsGroup.add(rsTemp);
					dblMaxWidth = dblCurrWidth;
					lngSumWt = lngCurrWt;
					intMtlSh = 1;
					szMsg = "[준비스케줄 LOT편성-화면] 동내이적 - 총중량 합 : " + lngSumWt + ", dblMaxWidth = " + dblMaxWidth + ", dblCurrWidth = " + dblCurrWidth + ", intMtlSh = " + intMtlSh;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					szMsg = "[준비스케줄 LOT편성-화면] 스판비교 >>>>> YD_STK_COL_GP[" + szYD_STK_COL_GP + "], PREV_YD_STK_COL_GP[" + szYD_STK_COL_GP_CMP + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
				/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				 * 업무기준 : 주작업인 경우 
				 * 		1. 작업재료를 동내이적 대상재는 크레인 작업능력만큼 LOT편성
				 * 		2. 작업재료를 동간이적 대상재는 대차 작업능력만큼 LOT편성
				 * 수정자 : 임춘수
				 * 일자 : 2009.07.04
				 ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
				else if( bIsInsideMv ) {		//동내이적
					szMsg = "[준비스케줄 LOT편성-화면] 동내이적 - bIsInsideMv : " + bIsInsideMv + ", bUpCol : " + bUpCol;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					if( bUpCol ) {			//주작업인 경우에만.
						lngSumWt += lngCurrWt;
						intMtlSh++;
						szMsg = "[준비스케줄 LOT편성-화면] 동내이적 - 총중량 합 : " + lngSumWt + ", dblMaxWidth = " + dblMaxWidth + ", dblCurrWidth = " + dblCurrWidth + ", intMtlSh = " + intMtlSh;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						//크레인작업가능능력 체크
						intRtnVal = YdCommonUtils.chkGetCrnspec(dblCurrWidth, dblMaxWidth, lngSumWt, intMtlSh, recResult);
						szMsg = "[준비스케줄 LOT편성-화면] 동내이적(크레인작업가능능력 체크 : intRtnVal "+ intRtnVal + ")";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						if( intRtnVal == -1 || intRtnVal == -2 || intRtnVal == -3 ) {
							rsTemp = JDTORecordFactory.getInstance().createRecordSet("");
							rsGroup.add(rsTemp);
							dblMaxWidth = dblCurrWidth;
							lngSumWt = lngCurrWt;
							intMtlSh = 1;
							szMsg = "[준비스케줄 LOT편성-화면] 동내이적 - 총중량 합 : " + lngSumWt + ", dblMaxWidth = " + dblMaxWidth + ", dblCurrWidth = " + dblCurrWidth + ", intMtlSh = " + intMtlSh;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							szMsg = "[준비스케줄 LOT편성-화면] 크레인작업가능능력 초과";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						}
						
						if( dblMaxWidth < dblCurrWidth ) dblMaxWidth = dblCurrWidth;
					}
				}else{							//동간이적
//					lngSumWt += lngCurrWt;
//					//대차적치 능력 체크
//					if( intTcarWtCapa < lngSumWt ) {
//						rsTemp = JDTORecordFactory.getInstance().createRecordSet("");
//						rsGroup.add(rsTemp);
//						//dblMaxWidth = dblCurrWidth;
//						lngSumWt = 0;
//						szMsg = "[준비스케줄 LOT편성-화면] 대차적치 능력[" + intTcarWtCapa + "] 초과 ";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//					}
					szMsg = "[준비스케줄 LOT편성-화면] 동간이적(대차적치능력 - 크레인작업가능능력으로 체크) - bIsInsideMv : " + bIsInsideMv;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					lngSumWt += lngCurrWt;
					intMtlSh++;
					szMsg = "[준비스케줄 LOT편성-화면] 동간이적 - 총중량 합 : " + lngSumWt + ", dblMaxWidth = " + dblMaxWidth + ", dblCurrWidth = " + dblCurrWidth + ", intMtlSh = " + intMtlSh;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					//크레인작업가능능력 체크
					intRtnVal = YdCommonUtils.chkGetCrnspec(dblCurrWidth, dblMaxWidth, lngSumWt, intMtlSh, recResult);
					szMsg = "[준비스케줄 LOT편성-화면] 동간이적(대차적치능력 - 크레인작업가능능력으로 체크 : intRtnVal "+ intRtnVal + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					if( intRtnVal == -1 || intRtnVal == -2 || intRtnVal == -3 ) {
						rsTemp = JDTORecordFactory.getInstance().createRecordSet("");
						rsGroup.add(rsTemp);
						dblMaxWidth = dblCurrWidth;
						lngSumWt = lngCurrWt;
						intMtlSh = 1;
						szMsg = "[준비스케줄 LOT편성-화면] 동간이적 - 총중량 합 : " + lngSumWt + ", dblMaxWidth = " + dblMaxWidth + ", dblCurrWidth = " + dblCurrWidth + ", intMtlSh = " + intMtlSh;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						szMsg = "[준비스케줄 LOT편성-화면(대차적치능력)] 크레인작업가능능력으로 체크 후 초과";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
					
					if( dblMaxWidth < dblCurrWidth ) dblMaxWidth = dblCurrWidth;
				}
				
				rsTemp.addRecord(recPara);
				szYD_STK_COL_GP_CMP = szYD_STK_COL_GP;
//				// 업무에 대한 정확한 정의후 결정
//				// 1. 다른 작업예약에 등록되어 있는 지 체크
//				szRtnMsg = YdCommonUtils.chkYdWrkBookMtl(szSTL_NO);
//				if( szRtnMsg.equals(YdConstant.RETN_CD_EXIST) ) {
//					return szRtnMsg;
//				}
//				// 2. 다른 크레인 스케줄에 등록 되어 있는지 체크
//				szRtnMsg = YdCommonUtils.chkCrnWrkMtl(szSTL_NO); 
//				if (szRtnMsg.equals(YdConstant.RETN_CD_EXIST)) {
//					return szRtnMsg;
//				}
					

				//다음 레코드로
				rsResult.next();
				//스케줄코드
				szPREV_YD_SCH_CD = szYD_SCH_CD;
				//대차설비ID
				szPREV_TCAR = szTCAR;
				
				//---------------------------------------------------------------------------------------------
				//	C연주슬라브야드의 준비스케줄 등록 시 지정한 작업매수만큼 처리된 후에는 루프 종료
				//	등록자 : 임춘수
				//	등록일 : 2010.01.20
				//---------------------------------------------------------------------------------------------
				
				if( !szYD_EQP_WRK_SH.equals("") ) {
					if( Loop_i >= intYD_EQP_WRK_SH )	{
						
						szMsg = "사용자가 지정한 작업매수[" + intYD_EQP_WRK_SH + "]와 같으므로 루프 종료 - 반복변수값["+Loop_i+"]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						break;
					}
				}
				
				//---------------------------------------------------------------------------------------------
			}
			
			
			szMsg = "[준비스케줄 LOT편성-화면] 대상재 작업예약 그룹 갯수 : " + rsGroup.size();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//전문 발생 일시
			szDate = YdUtils.getCurDate("yyyy/MM/dd HH:mm:ss");
			
			
		
			for(int Loop_i = 0 ; Loop_i < rsGroup.size(); Loop_i++) {
				rsTemp = (JDTORecordSet)rsGroup.get(Loop_i);
				for(int Loop_j = 0; Loop_j < rsTemp.size(); Loop_j++ ) {
					rsTemp.absolute(Loop_j + 1);
					recPara = rsTemp.getRecord();
					
					if( Loop_j == 0 ) {
//						//야드 TO위치결정방법
//						szYD_TO_LOC_DCSN_MTD = "";
//						//야드TO위치가이드
//						szYD_TO_LOC_GUIDE = "";
//						//대차
//						szTCAR = "";
//						//권상모음으로 처리 유무
//						bUpCol = true;
//						//레코드 생성
//						recOutPara = JDTORecordFactory.getInstance().create();
//						
//						szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP").trim();	
//						
//						//TO위치GUIDE이고 목표동이 다른 경우에는 대차상차스케줄을 생성하고 대차 배정
//						if(szYD_TO_LOC_GUIDE_GP.equals("Y")) {				//TO위치GUIDE 이적인 경우
//							szYD_TO_LOC_DCSN_MTD = "F";
//							szYD_TO_LOC_GUIDE	= szYD_AIM_YD_GP + szYD_AIM_BAY_GP + szYD_AIM_SPAN_GP + szYD_AIM_COL_GP + szYD_AIM_BED_NO;
//							
//							szMsg = "TO위치GUIDE 이적인 경우 szYD_TO_LOC_GUIDE : " + szYD_TO_LOC_GUIDE;
//							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//							
//							if( szYD_BAY_GP.equals(szYD_AIM_BAY_GP) )	{	//목표동이 같은 경우 - 동내이적 스케줄
//								szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "YD" + szYD_STK_COL_GP.substring(2, 4) + "MM";
//								szMsg = "목표동이 같은 경우 - 동내이적 스케줄 : " + szYD_SCH_CD + ", " + szTCAR;
//								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//							}else{											//목표동이 다른 경우 - 대차상차스케줄
//								String[] retValue = YdCommonUtils.getSchCdNTcar(szYD_STK_COL_GP);
//								szYD_SCH_CD = retValue[0];
//								szTCAR = retValue[1];
//								szMsg = "목표동이 다른 경우 - 대차상차스케줄[동간이적] : " + szYD_SCH_CD + ", " + szTCAR;
//								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//							}
//						}else{			//위치검색기준으로 이적인 경우
//							szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_YD_GP").trim();
//							szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_BAY_GP").trim();
//							
//							szMsg = "위치검색기준으로 이적인 경우";
//							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//							
//							if(szYD_MAIN_WRK_GP.equals("Y")) {		//주작업이적인 경우
//								szMsg = "주작업이적인 경우";
//								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//								if( szYD_BAY_GP.equals(szYD_AIM_BAY_GP) ) {		//목표동이 같은 경우 - 동내이적 스케줄
//									szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "YD" + szYD_STK_COL_GP.substring(2, 4) + "MM";
//									szMsg = "목표동이 같은 경우 - 동내이적 스케줄 : " + szYD_SCH_CD + ", " + szTCAR;
//									ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//								}else{											//목표동이 다른 경우 - 대차상차스케줄
//									String[] retValue = YdCommonUtils.getSchCdNTcar(szYD_STK_COL_GP);
//									szYD_SCH_CD = retValue[0];
//									szTCAR = retValue[1];
//									szMsg = "목표동이 다른 경우 - 대차상차스케줄[동간이적] : " + szYD_SCH_CD + ", " + szTCAR;
//									ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//								}
//							}else{									//상단의 더미재 이적인 경우
//								//권상모음을 제거한다.
//								bUpCol = false;
//								szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "YD" + szYD_STK_COL_GP.substring(2, 4) + "MM";
//								szMsg = "상단의 더미재 이적인 경우[동내이적] " + szYD_SCH_CD + ", " + szTCAR;
//								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//							}
//						}
						
						if(szYD_TO_LOC_GUIDE_GP.equals("Y")) {
							szYD_AIM_YD_GP		= szYD_TO_LOC_GUIDE.substring(0, 1);
							szYD_AIM_BAY_GP		= szYD_TO_LOC_GUIDE.substring(1, 2);
						}
						
						//레코드 생성
						recOutPara = JDTORecordFactory.getInstance().create();
						//전문코드
						recOutPara.setField("JMS_TC_CD", "YDYDJ291");
						//발생 일시
						recOutPara.setField("JMS_TC_CREATE_DDTT", 	szDate);
						//스케줄코드
						recOutPara.setField("YD_SCH_CD",          	szYD_SCH_CD);
						//Lot편성 매수
						recOutPara.setField("YD_LOT_GP_SH",       	"" + rsTemp.size());
						//야드구분
						recOutPara.setField("YD_GP",      			szYD_GP);
						//동구분
						recOutPara.setField("YD_BAY_GP",      		szYD_BAY_GP);
						//목표야드구분
						recOutPara.setField("YD_AIM_YD_GP",      	szYD_AIM_YD_GP);
						//목표동구분
						recOutPara.setField("YD_AIM_BAY_GP",      	szYD_AIM_BAY_GP);
						//-----------------------------------------------------------------------------------------------------------
						//	대차상차스케줄인 경우에는 
						//	1. 동까지만 선택하는 경우에 대차하차스케줄의 TO위치결정 시는 위치검색베드 적용
						//	2. 베드까지 선택하는 경우에 대차하차스케줄의 TO위치결정 시는 사용자지정위치로 적용 
						//	수정자 : 임춘수
						//	수정일 : 1) 2010.01.28
						//			2) 2010.02.03
						//-----------------------------------------------------------------------------------------------------------
						if( szYD_TO_LOC_DCSN_MTD.equals("F") && szYD_TO_LOC_GUIDE.length() == 8 )	{
							//야드 TO위치결정방법
							recOutPara.setField("YD_TO_LOC_DCSN_MTD",   szYD_TO_LOC_DCSN_MTD);
							//야드TO위치가이드
							recOutPara.setField("YD_TO_LOC_GUIDE",      szYD_TO_LOC_GUIDE);
						}
						//-----------------------------------------------------------------------------------------------------------
						//작업계획대차
						recOutPara.setField("YD_WRK_PLAN_TCAR",     szTCAR);
						//-----------------------------------------------------------------------------------------------------------
						// 작업예약ID를 생성해서 전송하는 방식으로 변경
						// --> 여러 작업예약을 연속으로 JMS전송하는 경우 서버이중화로 인하여 작업예약요구 모듈에서 역전현상이 발생하여
						// 작업예약ID의 생성순서가 바뀌는 경우가 발생함
						// 임춘수 추가 - 2009.11.27
						//-----------------------------------------------------------------------------------------------------------
						//작업예약ID
						recOutPara.setField("YD_WBOOK_ID",      ydWrkbookDao.getYdWrkbookId());
						//-----------------------------------------------------------------------------------------------------------
					}
					szSTL_NO = ydDaoUtils.paraRecChkNull(recPara, "STL_NO").trim();
					//재료번호
					recOutPara.setField("STL_NO" + (Loop_j + 1), szSTL_NO);
					//권상모음순서
					recOutPara.setField("YD_UP_COLL_SEQ" + (Loop_j + 1), ( bUpCol ? "" + (Loop_j + 1) : "" ) );
				}
				recOutPara.setField("LOOP_NO", "" + (Loop_i + 1));
				// 전문내용 로그 출력
				ydUtils.displayRecord(szOperationName, recOutPara);

				// 전문 송신 - 내부JMS송신
				ydDelegate.sendMsg(recOutPara);
				//메소드 콜
				//procFtmvOrdLotReq(recOutPara);
				szMsg = szOperationName + " 후  작업요구 송신 완료!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
		} catch(Exception e) {
			szMsg = szOperationName + " Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return  YdConstant.RETN_CD_FAILURE;
		}
		return szRtnMsg;
	}  //end of procCCPrepLotCompByCapa
	
	/**
	 * 오퍼레이션명 : 준비스케줄 LOT편성 - 화면으로 부터 호출, 2009.06.25 임춘수 추가
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String procCCPrepLotComp(JDTORecord msgRecord) throws JDTOException  {
		YdDelegate ydDelegate = new YdDelegate();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		//공용 METHOD
		YdUtils ydutils       = new YdUtils();
		
		//레코드 선언
		JDTORecord recPara     = null;
		JDTORecord recOutPara  = null;
		JDTORecord recInParam	= null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		JDTORecordSet rsTemp = null;
		
		//리턴값(boolean)
		boolean blnRtnVal      = false;
		int intRtnVal 			= 0;
		
		//메세지
		String szMsg           = "";
		//메소드명
		String szMethodName    = "procCCPrepLotComp";
		//오퍼레이션명
		String szOperationName = "준비스케줄 LOT 편성";
		
		
		//Lot 편성 재료 매수
		int intLotGpSh           = 0;
		//전문 생성 일시
		String szDate             = null;
		//설비ID
		String szYD_EQP_ID			= null;
		//야드구분
		String szYD_GP			= null;
		//동구분
		String szYD_BAY_GP		= null;
		//스판구분
		String szYD_SPAN_GP		= null;
		//목표야드구분
		String szYD_AIM_YD_GP			= null;
		//목표동구분
		String szYD_AIM_BAY_GP		= null;
		//목표스판구분
		String szYD_AIM_SPAN_GP		= null;
		//목표적치열구분
		String szYD_AIM_COL_GP		= null;
		//목표적치BED구분				
		String szYD_AIM_BED_NO		= null;
		//목표행선구분
		String szYD_AIM_RT_GP      = null;
		//주작업이적구분
		String szYD_MAIN_WRK_GP		= null;
		//TO위치가이드구분
		String szYD_TO_LOC_GUIDE_GP	= null;
		//스케줄코드
		String szYD_SCH_CD         = null;
		//재료번호
		String szSTL_NO            = null;
		//재료품목
		String szYD_MTL_ITEM = null;
		//적치열배열
		String[] arrYD_STK_COL_GP = null;
		//적치베드 배열
		String[] arrYD_STK_BED_NO = null;
		//대차
		String szTCAR = "";
		//적치열구분
		String szYD_STK_COL_GP = "";
		//이전 적치열구분 - 비교를 위해서 임의의 값을 설정
		String szYD_STK_COL_GP_CMP = "XX01";
		//야드 TO위치결정방법
		String szYD_TO_LOC_DCSN_MTD = "";
		//야드TO위치가이드
		String szYD_TO_LOC_GUIDE = "";
		//권상모음으로 처리 유무
		boolean bUpCol = true;
		//재료의 현재 폭
		long lngCurrWidth			= 0;
		//재료의 현재 중량
		long lngCurrWt				= 0;
		//누적중량
		long lngSumWt 				= 0;
		//크레인작업가능매수
		int intMtlSh				= 0;
		//크레인작업가능 폭
		long lngMaxWidth			= 0;
		
		//리턴메세지정의
		String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if (szRcvTcCode == null) {
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_TC_ERROR;
		
		}
		//TC CODE DISPLAY
		if (bDebugFlag) {
		
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		try {
			/*
			 * 야드구분, 동구분, 스판(선택), 목표행선구분, 목표동, 주작업이적구분, TO위치가이드구분
			 */
			//받은 전문 편집
			szYD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP").trim();
			if (szYD_GP.equals("")) {
				
				szMsg = "[전문 이상] 야드구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_TC_ERROR;
				
			}
			//동구분
			szYD_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP").trim();
			if (szYD_BAY_GP.equals("")) {
				
				szMsg = "[전문 이상] 동구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_TC_ERROR;
				
			}
			
			//스판구분
			szYD_SPAN_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SPAN_GP").trim();
//			if (szYD_SPAN_GP.equals("")) {
//				
//				szMsg = "[전문 이상] 스판구분이 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return;
//				
//			}
			
			//적치열구분
			arrYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP").split(";");
			if (arrYD_STK_COL_GP == null || arrYD_STK_COL_GP.length == 0) {
				
				szMsg = "[전문 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_TC_ERROR;
				
			}
			
			//적치열구분
			arrYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO").split(";");
			if (arrYD_STK_BED_NO == null || arrYD_STK_BED_NO.length == 0) {
				
				szMsg = "[전문 이상] 적치베드번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_TC_ERROR;
				
			}
			
			//목표행선구분
			szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_RT_GP").trim();
			if (szYD_AIM_RT_GP.equals("")) {
				
				szMsg = "[전문 이상] 목표행선구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_TC_ERROR;
				
			}
			
			//주작업이적구분
			szYD_MAIN_WRK_GP  = ydDaoUtils.paraRecChkNull(msgRecord, "YD_MAIN_WRK_GP").trim();
			if (szYD_MAIN_WRK_GP.equals("")) {
				
				szMsg = "[전문 이상] 주작업이적구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_TC_ERROR;
				
			}
			//TO위치가이드구분
			szYD_TO_LOC_GUIDE_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_TO_LOC_GUIDE_GP").trim();
			if (szYD_TO_LOC_GUIDE_GP.equals("")) {
				
				szMsg = "[전문 이상] TO위치가이드구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_TC_ERROR;
				
			}else if(szYD_TO_LOC_GUIDE_GP.equals("Y")) {
				//목표야드구분
				szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_YD_GP").trim();
				if (szYD_AIM_YD_GP.equals("")) {
					
					szMsg = "[전문 이상] 목표야드구분이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_TC_ERROR;
					
				}
				//목표동구분
				szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_BAY_GP").trim();
				if (szYD_AIM_BAY_GP.equals("")) {
					
					szMsg = "[전문 이상] 목표동구분이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_TC_ERROR;
					
				}
				//목표스판구분
				szYD_AIM_SPAN_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_SPAN_GP").trim();
				if (szYD_AIM_SPAN_GP.equals("")) {
					
					szMsg = "[전문 이상] 목표스판구분이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_TC_ERROR;
					
				}
				//목표적치열구분
				szYD_AIM_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_COL_GP").trim();
				if (szYD_AIM_COL_GP.equals("")) {
					
					szMsg = "[전문 이상] 목표적치열구분이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_TC_ERROR;
					
				}
				
				//목표적치BED구분
				szYD_AIM_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_BED_NO").trim();
			}
			
			//목표야드구분
			szYD_AIM_YD_GP = szYD_GP;
			
			
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			if(szYD_MAIN_WRK_GP.equals("Y")) {		//주작업이적인 경우
				//이적준비 대상재 검색
				intRtnVal = chkPrepLotGp(arrYD_STK_COL_GP, arrYD_STK_BED_NO, szYD_AIM_RT_GP, rsResult);
				
				if( intRtnVal <= 0 ) {
					if( intRtnVal < 0 ) {
						return YdConstant.RETN_CD_FAILURE;
					}else{
						if( rsResult.size() == 0 ) {
							return YdConstant.RETN_CD_NOTEXIST;
						}
					}
				}
			}else{									//상단의 더미재 이적인 경우
				// 상단 더미재 이적 Lot 편성 대상 재료 Select
				intRtnVal = chkGetMvDummyMtlAboveTgMtlLot(arrYD_STK_COL_GP, arrYD_STK_BED_NO, szYD_AIM_RT_GP, rsResult);
				if( intRtnVal <=0 ) {
					if( intRtnVal < 0 ) {
						return YdConstant.RETN_CD_FAILURE;
					}else{
						if( rsResult.size() == 0 ) {
							return YdConstant.RETN_CD_NOTEXIST;
						}
					}
				}
			}
			
			intLotGpSh = rsResult.size();
			
			
			rsResult.first();
			
			Vector rsGroup = new Vector();
			//동일한 스판별로 대상재를 편성한다.
			//보급 Lot 편성 재료 매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {
				
				//레코드 추출
				recPara = rsResult.getRecord();
				
				//재료번호 
				szSTL_NO = ydDaoUtils.paraRecChkNull(recPara, "STL_NO").trim();
				//적치열구분
				szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP").trim();
				//동일한 스판이 아닌 경우
				if( !szYD_STK_COL_GP.substring(0, 4).equals(szYD_STK_COL_GP_CMP.substring(0, 4)) ) {
					rsTemp = JDTORecordFactory.getInstance().createRecordSet("");
					rsGroup.add(rsTemp);
				}
				
				rsTemp.addRecord(recPara);
				szYD_STK_COL_GP_CMP = szYD_STK_COL_GP;
//				// 업무에 대한 정확한 정의후 결정
//				// 1. 다른 작업예약에 등록되어 있는 지 체크
//				szRtnMsg = YdCommonUtils.chkYdWrkBookMtl(szSTL_NO);
//				if( szRtnMsg.equals(YdConstant.RETN_CD_EXIST) ) {
//					return szRtnMsg;
//				}
//				// 2. 다른 크레인 스케줄에 등록 되어 있는지 체크
//				szRtnMsg = YdCommonUtils.chkCrnWrkMtl(szSTL_NO); 
//				if (szRtnMsg.equals(YdConstant.RETN_CD_EXIST)) {
//					return szRtnMsg;
//				}
					

				//다음 레코드로
				rsResult.next();
				
			}
			
			
			szMsg = "대상재 작업예약 갯수 : " + rsGroup.size();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//전문 발생 일시
			szDate = YdUtils.getCurDate("yyyy/MM/dd HH:mm:ss");
			
			for(int Loop_i = 0 ; Loop_i < rsGroup.size(); Loop_i++) {
				rsTemp = (JDTORecordSet)rsGroup.get(Loop_i);
				for(int Loop_j = 0; Loop_j < rsTemp.size(); Loop_j++ ) {
					rsTemp.absolute(Loop_j + 1);
					recPara = rsTemp.getRecord();
					
					if( Loop_j == 0 ) {
						//야드 TO위치결정방법
						szYD_TO_LOC_DCSN_MTD = "";
						//야드TO위치가이드
						szYD_TO_LOC_GUIDE = "";
						//대차
						szTCAR = "";
						//권상모음으로 처리 유무
						bUpCol = true;
						//레코드 생성
						recOutPara = JDTORecordFactory.getInstance().create();
						
						szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP").trim();
						
						//TO위치GUIDE이고 목표동이 다른 경우에는 대차상차스케줄을 생성하고 대차 배정
						if(szYD_TO_LOC_GUIDE_GP.equals("Y")) {				//TO위치GUIDE 이적인 경우
							szYD_TO_LOC_DCSN_MTD = "F";
							szYD_TO_LOC_GUIDE	= szYD_AIM_YD_GP + szYD_AIM_BAY_GP + szYD_AIM_SPAN_GP + szYD_AIM_COL_GP + szYD_AIM_BED_NO;
							
							szMsg = "TO위치GUIDE 이적인 경우 szYD_TO_LOC_GUIDE : " + szYD_TO_LOC_GUIDE;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							
							if( szYD_BAY_GP.equals(szYD_AIM_BAY_GP) )	{	//목표동이 같은 경우 - 동내이적 스케줄
								szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "YD" + szYD_STK_COL_GP.substring(2, 4) + "MM";
								szMsg = "목표동이 같은 경우 - 동내이적 스케줄 : " + szYD_SCH_CD + ", " + szTCAR;
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							}else{											//목표동이 다른 경우 - 대차상차스케줄
								String[] retValue = YdCommonUtils.getSchCdNTcar(szYD_STK_COL_GP);
								szYD_SCH_CD = retValue[0];
								szTCAR = retValue[1];
								szMsg = "목표동이 다른 경우 - 대차상차스케줄[동간이적] : " + szYD_SCH_CD + ", " + szTCAR;
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							}
						}else{			//위치검색기준으로 이적인 경우
							szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_YD_GP").trim();
							szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_BAY_GP").trim();
							
							szMsg = "위치검색기준으로 이적인 경우";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							
							if(szYD_MAIN_WRK_GP.equals("Y")) {		//주작업이적인 경우
								szMsg = "주작업이적인 경우";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								if( szYD_BAY_GP.equals(szYD_AIM_BAY_GP) ) {		//목표동이 같은 경우 - 동내이적 스케줄
									szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "YD" + szYD_STK_COL_GP.substring(2, 4) + "MM";
									szMsg = "목표동이 같은 경우 - 동내이적 스케줄 : " + szYD_SCH_CD + ", " + szTCAR;
									ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								}else{											//목표동이 다른 경우 - 대차상차스케줄
									String[] retValue = YdCommonUtils.getSchCdNTcar(szYD_STK_COL_GP);
									szYD_SCH_CD = retValue[0];
									szTCAR = retValue[1];
									szMsg = "목표동이 다른 경우 - 대차상차스케줄[동간이적] : " + szYD_SCH_CD + ", " + szTCAR;
									ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								}
							}else{									//상단의 더미재 이적인 경우
								//권상모음을 제거한다.
								bUpCol = false;
								szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "YD" + szYD_STK_COL_GP.substring(2, 4) + "MM";
								szMsg = "상단의 더미재 이적인 경우[동내이적] " + szYD_SCH_CD + ", " + szTCAR;
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							}
						}
						
						//발생 일시
						recOutPara.setField("JMS_TC_CREATE_DDTT", szDate);
						//스케줄코드
						recOutPara.setField("YD_SCH_CD",          szYD_SCH_CD);
						//Lot편성 매수
						recOutPara.setField("YD_LOT_GP_SH",       "" + rsTemp.size());
						//야드구분
						recOutPara.setField("YD_GP",      szYD_GP);
						//동구분
						recOutPara.setField("YD_BAY_GP",      szYD_BAY_GP);
						//목표야드구분
						recOutPara.setField("YD_AIM_YD_GP",      szYD_AIM_YD_GP);
						//목표동구분
						recOutPara.setField("YD_AIM_BAY_GP",      szYD_AIM_BAY_GP);
						//야드 TO위치결정방법
						recOutPara.setField("YD_TO_LOC_DCSN_MTD",      szYD_TO_LOC_DCSN_MTD);
						//야드TO위치가이드
						recOutPara.setField("YD_TO_LOC_GUIDE",      szYD_TO_LOC_GUIDE);
						//작업계획대차
						recOutPara.setField("YD_WRK_PLAN_TCAR",      szTCAR);
					}
					szSTL_NO = ydDaoUtils.paraRecChkNull(recPara, "STL_NO").trim();
					//재료번호
					recOutPara.setField("STL_NO" + (Loop_j + 1), szSTL_NO);
					//권상모음순서
					recOutPara.setField("YD_UP_COLL_SEQ" + (Loop_j + 1), ( bUpCol ? "" + (Loop_j + 1) : "" ) );
				}
				// 전문내용 로그 출력
				ydUtils.displayRecord(szOperationName, recOutPara);
				
				procFtmvOrdLotReq(recOutPara);
			}
			

			
			
			// 전문내용 로그 출력
			//ydUtils.displayRecord(szOperationName, recOutPara);
			// 전문 송신 - 내부JMS송신
			//ydDelegate.sendMsg(recOutPara);
			//메소드 콜
			//procFtmvOrdLotReq(recOutPara);
			
			szMsg = szOperationName + " 후  작업요구 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		} catch(Exception e) {
			szMsg = szOperationName + " Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return  YdConstant.RETN_CD_FAILURE;
		}
		return szRtnMsg;
	}  //end of procCCPrepMvLotComp
	
	/**
	 * 오퍼레이션명 : 이적준비LOT편성
	 * @param recInParam
	 * @param rsResult
	 * @return
	 * @throws JDTOException
	 */
	public int chkMvLotGp(JDTORecord recInParam, JDTORecordSet rsResult) throws JDTOException {

		// 저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메소드명
		String szMethodName = "chkMvLotGp";
		String szOperationName = "이적준비 대상재 검색";
		String szMsg = null;

		// 레코드 선언
		//JDTORecord recPara = null;

		try {
			
			intRtnVal = ydStockDao.getYdStock(recInParam, rsResult, 74);

			// 리턴값 메세지처리
			if (intRtnVal >= 1) {

				blnRtnVal = true;

			} else if (intRtnVal == 0) {

				szMsg = szOperationName + " 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == -2) {

				szMsg = szOperationName + "  데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = szOperationName + "  데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch (Exception e) {
			szMsg = szOperationName + " 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: "
					+ e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			//blnRtnVal = false;
			intRtnVal = -100;
		}
		return intRtnVal;
	} // end of chkMvLotGp
	
	/**
	 * 오퍼레이션명 : 준비스케줄 시 대상재 검색
	 * @param recInParam
	 * @param rsResult
	 * @return
	 * @throws JDTOException
	 */
	public int chkPrepLotGp(String[] arrYD_STK_COL_GP, String[] arrYD_STK_BED_NO, String szYD_AIM_RT_GP, JDTORecordSet rsResult) throws JDTOException {
		// 저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메소드명
		String szMethodName = "chkPrepLotGp";
		String szOperationName = "준비스케줄 시 대상재 검색";
		String szLogMsg = null;
		//레코드셋 선언
		JDTORecordSet outRecSet = null;
		// 레코드 선언
		JDTORecord recPara = JDTORecordFactory.getInstance().create();

		try {
			//적치열,베드에 적치된 해당하는 야드목표행선을 가진 대상재를 조회
			for(int Loop_i = 0; Loop_i < arrYD_STK_COL_GP.length; Loop_i++ ) {
				outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
				recPara.setField("YD_STK_COL_GP", arrYD_STK_COL_GP[Loop_i]);
				recPara.setField("YD_STK_BED_NO", arrYD_STK_BED_NO[Loop_i]);
				recPara.setField("YD_AIM_RT_GP", szYD_AIM_RT_GP);
				intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 93);
				
				//조회결과를 체크
				if( intRtnVal == 0 ) {
					szLogMsg = "YD_STK_COL_GP = " + arrYD_STK_COL_GP[Loop_i] + ", YD_STK_BED_NO = " + arrYD_STK_BED_NO[Loop_i] + ", YD_AIM_RT_GP = " + szYD_AIM_RT_GP 
					+ "에 대상재가 존재하지 않습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					continue;
				}else if( intRtnVal < 0 ) {
					szLogMsg = "YD_STK_COL_GP = " + arrYD_STK_COL_GP[Loop_i] + ", YD_STK_BED_NO = " + arrYD_STK_BED_NO[Loop_i] + ", YD_AIM_RT_GP = " + szYD_AIM_RT_GP
					+ "에 대상재를 조회 시 에러가 발생했습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					continue;
				}
				szLogMsg = "YD_STK_COL_GP = " + arrYD_STK_COL_GP[Loop_i] + ", YD_STK_BED_NO = " + arrYD_STK_BED_NO[Loop_i] + ", YD_AIM_RT_GP = " + szYD_AIM_RT_GP 
				+ "에 대상재가 존재합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				szLogMsg = "1. rsResult : " + rsResult + ", outRecSet : " +  outRecSet;
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				rsResult.addAll(outRecSet);
				szLogMsg = "2. rsResult : " + rsResult + ", outRecSet : " +  outRecSet;
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			}
			intRtnVal = rsResult.size();
			
			// 리턴값 메세지처리
			if (intRtnVal >= 1) {

				blnRtnVal = true;

			} else if (intRtnVal == 0) {

				szLogMsg = szOperationName + " 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == -2) {

				szLogMsg = szOperationName + "  데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szLogMsg = szOperationName + "  데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch (Exception e) {
			szLogMsg = szOperationName + " ++++++++++++ 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			//blnRtnVal = false;
			intRtnVal = -100;
		}
		return intRtnVal;
	} // end of chkMvLotGp
	
	/**
	 * 오퍼레이션명 : 준비스케줄 시 대상재 검색
	 * @param recInParam
	 * @param rsResult
	 * @return
	 * @throws JDTOException
	 */
	public int chkPrepLotGp(String[] arrYD_STK_COL_GP, String[] arrYD_STK_BED_NO, String szYD_AIM_RT_GP, String szARR_WLOC_CD, JDTORecordSet rsResult) throws JDTOException {
		// 저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메소드명
		String szMethodName = "chkPrepLotGp";
		String szOperationName = "준비스케줄 시 대상재 검색";
		String szLogMsg = null;
		//레코드셋 선언
		JDTORecordSet outRecSet = null;
		JDTORecordSet SavRecSet = null;
		JDTORecordSet TmpRecSet = null;
		
		String szYD_GP			= null;
		String szSPOS_WLOC_CD	= null;
		String szRtnVal         = null;
		// 레코드 선언
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		SavRecSet = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecordSet rsSort = null;

		try {
			//적치열,베드에 적치된 해당하는 야드목표행선을 가진 대상재를 조회
			for(int Loop_i = 0; Loop_i < arrYD_STK_COL_GP.length; Loop_i++ ) {
				outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
				recPara.setField("YD_STK_COL_GP", arrYD_STK_COL_GP[Loop_i]);
				recPara.setField("YD_STK_BED_NO", arrYD_STK_BED_NO[Loop_i]);
				recPara.setField("YD_AIM_RT_GP", szYD_AIM_RT_GP);
				
				if( szYD_AIM_RT_GP.startsWith("E") ) {
					
					szYD_GP			= arrYD_STK_COL_GP[Loop_i].substring(0, 1);
					
					szSPOS_WLOC_CD		= YdCommonUtils.getWlocCd(szYD_GP);
					
					recPara.setField("SPOS_WLOC_CD", 			szSPOS_WLOC_CD);				//발지개소코드
					recPara.setField("ARR_WLOC_CD", 			szARR_WLOC_CD);				//착지개소코드
					
					intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 193);
				}else{
					intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 93);
				}
								
				
				//조회결과를 체크
				if( intRtnVal == 0 ) {
					szLogMsg = "YD_STK_COL_GP = " + arrYD_STK_COL_GP[Loop_i] + ", YD_STK_BED_NO = " + arrYD_STK_BED_NO[Loop_i] + ", YD_AIM_RT_GP = " + szYD_AIM_RT_GP 
					+ "에 대상재가 존재하지 않습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					continue;
				}else if( intRtnVal < 0 ) {
					szLogMsg = "YD_STK_COL_GP = " + arrYD_STK_COL_GP[Loop_i] + ", YD_STK_BED_NO = " + arrYD_STK_BED_NO[Loop_i] + ", YD_AIM_RT_GP = " + szYD_AIM_RT_GP
					+ "에 대상재를 조회 시 에러가 발생했습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					continue;
				}
				szLogMsg = "YD_STK_COL_GP = " + arrYD_STK_COL_GP[Loop_i] + ", YD_STK_BED_NO = " + arrYD_STK_BED_NO[Loop_i] + ", YD_AIM_RT_GP = " + szYD_AIM_RT_GP 
				+ "에 대상재가 존재합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				szLogMsg = "1. rsResult : " + SavRecSet + ", outRecSet : " +  outRecSet;
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				SavRecSet.addAll(outRecSet);
				szLogMsg = "2. rsResult : " + SavRecSet + ", outRecSet : " +  outRecSet;
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			}
			intRtnVal = SavRecSet.size();
			
			szLogMsg = "3. SavRecSet.size() : " + SavRecSet.size();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			// 리턴값 메세지처리
			if (intRtnVal >= 1) {

				blnRtnVal = true;
				
//				rsResult.addAll(SavRecSet);
//				
//				szLogMsg = "4. rsResult.size() : " + rsResult.size();
//				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				
				// 대상재가 2개 이상일때 목표행선에 의한 권상모음순서 정리
				// 2010.03.15 석창화
				if (intRtnVal > 1) {
					rsSort = JDTORecordFactory.getInstance().createRecordSet("");
					TmpRecSet = JDTORecordFactory.getInstance().createRecordSet("");
					
					TmpRecSet.addAll(SavRecSet);
					
					szRtnVal = this.sortUpColSeq(SavRecSet, szYD_AIM_RT_GP, rsSort);
					if(szRtnVal.equals(YdConstant.RETN_CD_SUCCESS)) {
						rsResult.addAll(rsSort);
						
						szLogMsg = szOperationName + " Sort가 완료되였습니다.";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						
						szLogMsg = "4. rsResult.size() : " + rsResult.size();
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						
					}else {
						szLogMsg = szOperationName + " Sort이 실패하였습니다.";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						rsResult.addAll(TmpRecSet);
						
						szLogMsg = "4. rsResult.size() : " + rsResult.size();
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					}
				} else {
					rsResult.addAll(SavRecSet);
					
					szLogMsg = "4. rsResult.size() : " + rsResult.size();
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}
				

			} else if (intRtnVal == 0) {

				szLogMsg = szOperationName + " 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == -2) {

				szLogMsg = szOperationName + "  데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szLogMsg = szOperationName + "  데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch (Exception e) {
			szLogMsg = szOperationName + " ++++++++++++ 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			//blnRtnVal = false;
			intRtnVal = -100;
		}
		return intRtnVal;
	} // end of chkMvLotGp
	
	/**
	 * 오퍼레이션명 : 목표행선및 적치배드의 양에 따라 작업예약재료들의 권상모음순서 정리
	 * @param recInParam
	 * @param rsResult
	 * @return
	 * @throws JDTOException
	 * 2010.03.15 석창화
	 */
	
	public String sortUpColSeq(JDTORecordSet rsWrkBookMtl, String szYD_AIM_RT_GP, JDTORecordSet rsSort) throws JDTOException {
		
		JDTORecord recInRsSet = null;
		JDTORecordSet rsBedCnt = null;
		
		String szRtnVal = YdConstant.RETN_CD_SUCCESS;
		String szMethodName = "sortUpColSeq";
		String szOperationName = "권상모음순서 정리";
		String szLogMsg = null;
		String[] szArrYdEqpGp = null;
		String[] szArrStlNo = null;
		String[] szArrYdStkPos = null;
		String[] szArrYdSerNo = null;
		
		String[] szSortStkPos = null;
		String[] szSortStlCnt = null;
		String   szTmpStkPos = null;
		String   szTmpStlCnt = null;
		String   szTmpYdSerNo = null;
		
		String   szYdAimYdGp = "";
		String   szYdAimBayGp = "";
		
		
		Vector vBedCnt = null;
		String szBeforeYdStkPos = "";
		int    intStlCnt = 0;
		
				
		String szTmpYdEqpGp = "";
		String szTmpStlNo = "";
		
		boolean blnSaveEqpId = true;
		String  szYdEqpGp   = "";
		
		int intArrSize = 0;
		
		try {
			
			szLogMsg = "[" + szOperationName + "] - sortUpColSeq]--------------------- 처리 시작 --------------------------";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			szArrYdEqpGp      = new String[rsWrkBookMtl.size()];
			szArrStlNo        = new String[rsWrkBookMtl.size()];
			szArrYdStkPos     = new String[rsWrkBookMtl.size()];
			szArrYdSerNo      = new String[rsWrkBookMtl.size()];
			
			szLogMsg = "[" + szOperationName + "] - sortUpColSeq]--대상재료수 :" + rsWrkBookMtl.size();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
						
			for(int Loop_i = 1; Loop_i <= rsWrkBookMtl.size(); Loop_i++) {
				rsWrkBookMtl.absolute(Loop_i);
				recInRsSet = JDTORecordFactory.getInstance().create();
				recInRsSet.setRecord(rsWrkBookMtl.getRecord());
				
				szArrYdEqpGp[Loop_i - 1] = ydDaoUtils.paraRecChkNull(recInRsSet, "YD_STK_COL_GP").substring(2,4);
				szArrStlNo[Loop_i - 1] = ydDaoUtils.paraRecChkNull(recInRsSet, "STL_NO");
				szArrYdStkPos[Loop_i - 1] = ydDaoUtils.paraRecChkNull(recInRsSet, "YD_STK_COL_GP") + ydDaoUtils.paraRecChkNull(recInRsSet, "YD_STK_BED_NO");
				szArrYdSerNo[Loop_i - 1] = ydDaoUtils.paraRecChkNull(recInRsSet, "REFUR_CHG_PLN_SERNO");
				szYdAimYdGp = ydDaoUtils.paraRecChkNull(recInRsSet, "YD_AIM_YD_GP");
				szYdAimBayGp = ydDaoUtils.paraRecChkNull(recInRsSet, "YD_AIM_BAY_GP");
				
			} //end of for
			
			//A후판 장입준비 이적인 경우 장입예정일련번호순으로 권상모음을 정렬한다.
			if ("D".equals(szYdAimYdGp) && "A".equals(szYdAimBayGp) && ("C3".equals(szYD_AIM_RT_GP) || "B5".equals(szYD_AIM_RT_GP))) {
				
				intArrSize = rsWrkBookMtl.size();
				
				szLogMsg = "----A후판 장입준비 이적 권상모음순서 Sorting 작업전 목표행선[" + szYD_AIM_RT_GP + "]---";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				for(int x = 0; x<intArrSize; x++ ) {
					szLogMsg = "장입예정일련번호[" + szArrYdSerNo[x] + "] - 재료번호[" + szArrStlNo[x] + "]";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}
				
				if (intArrSize < 2) {
					szLogMsg = "----A후판 장입준비 이적 권상모음대상재료가 1매입니다";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					szRtnVal = YdConstant.RETN_CD_FAILURE;
					return szRtnVal;
				}
				
				
				for(int x = 0; x<intArrSize-1; x++ ) {
					for(int y = 0; y<intArrSize-x-1; y++) {
						if(Integer.parseInt(szArrYdSerNo[y]) > Integer.parseInt(szArrYdSerNo[y+1])) {
							szTmpYdSerNo = szArrYdSerNo[y];
							szTmpStlNo = szArrStlNo[y];
							szArrYdSerNo[y] = szArrYdSerNo[y+1];
							szArrStlNo[y] = szArrStlNo[y+1];   
							szArrYdSerNo[y+1] = szTmpYdSerNo;
							szArrStlNo[y+1] = szTmpStlNo;
						}
					}
				}
				
				szLogMsg = "----A후판 장입준비 이적 권상모음순서 Sorting 작업후 목표행선[" + szYD_AIM_RT_GP + "]---";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				for(int x = 0; x<intArrSize; x++ ) {
					szLogMsg = "장입예정일련번호[" + szArrYdSerNo[x] + "] - 재료번호[" + szArrStlNo[x] + "]";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}
				
				
				for(int x = 0; x<intArrSize; x++ ) {
				
					for( int Loop_i = 1; Loop_i <= rsWrkBookMtl.size(); Loop_i++) {
						rsWrkBookMtl.absolute(Loop_i);
						recInRsSet = JDTORecordFactory.getInstance().create();
						recInRsSet.setRecord(rsWrkBookMtl.getRecord());
						
						
						if (szArrStlNo[x].equals(ydDaoUtils.paraRecChkNull(recInRsSet, "STL_NO"))){
							rsSort.addRecord(recInRsSet);
						}
						
					} //end of for
				}
				
				if (rsSort.size() != rsWrkBookMtl.size()) {
					szLogMsg = "----A후판 장입준비 이적 권상모음순서 Sorting 작업실패";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					szRtnVal = YdConstant.RETN_CD_FAILURE;
					return szRtnVal;
				}
				
				szLogMsg = "[" + szOperationName + "] - sortUpColSeq]--------------------- 처리 종료 --------------------------";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				return szRtnVal;
			}
			
			
			// 동일스판의 재료들인지를 확인한다.
			for(int Loop_i = 0; Loop_i<szArrYdEqpGp.length; Loop_i++) {
				if (Loop_i == 0) {
					szYdEqpGp = szArrYdEqpGp[Loop_i];
				} else {
					if (!szArrYdEqpGp[Loop_i].equals(szYdEqpGp)){
						blnSaveEqpId = false;
						break;
					}
				}
			}
			
			//동일스판이 아닐경우
			if (blnSaveEqpId == false) {
				// 정렬 시작
				intArrSize = rsWrkBookMtl.size();
				
				szLogMsg = "----권상모음순서 Sorting 작업전 목표행선[" + szYD_AIM_RT_GP + "]---";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				for(int x = 0; x<intArrSize; x++ ) {
					szLogMsg = "스판번호[" + szArrYdEqpGp[x] + "] - 재료번호[" + szArrStlNo[x] + "]";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}
				
				
				for(int x = 0; x<intArrSize-1; x++ ) {
					for(int y = 0; y<intArrSize-x-1; y++) {
						if (szYD_AIM_RT_GP.startsWith("A") ||szYD_AIM_RT_GP.startsWith("Y")) { // 4스판쪽으로 정렬
							if(Integer.parseInt(szArrYdEqpGp[y]) < Integer.parseInt(szArrYdEqpGp[y+1])) {
								szTmpYdEqpGp = szArrYdEqpGp[y];
								szTmpStlNo = szArrStlNo[y];
								szArrYdEqpGp[y] = szArrYdEqpGp[y+1];
								szArrStlNo[y] = szArrStlNo[y+1];   
								szArrYdEqpGp[y+1] = szTmpYdEqpGp;
								szArrStlNo[y+1] = szTmpStlNo;
							}
							
						} else if (szYD_AIM_RT_GP.startsWith("E") ||szYD_AIM_RT_GP.startsWith("G")) { //1스판쪽으로 정렬
							if(Integer.parseInt(szArrYdEqpGp[y]) > Integer.parseInt(szArrYdEqpGp[y+1])) {
								szTmpYdEqpGp = szArrYdEqpGp[y];
								szTmpStlNo = szArrStlNo[y];
								szArrYdEqpGp[y] = szArrYdEqpGp[y+1];
								szArrStlNo[y] = szArrStlNo[y+1];   
								szArrYdEqpGp[y+1] = szTmpYdEqpGp;
								szArrStlNo[y+1] = szTmpStlNo;
							}
							
						} else if (szYD_AIM_RT_GP.startsWith("B") || szYD_AIM_RT_GP.startsWith("C")) { //2스판쪽으로 정렬
							if( "02".equals(szArrYdEqpGp[y+1]) ||
								Integer.parseInt(szArrYdEqpGp[y]) > Integer.parseInt(szArrYdEqpGp[y+1])) {
								szTmpYdEqpGp = szArrYdEqpGp[y];
								szTmpStlNo = szArrStlNo[y];
								szArrYdEqpGp[y] = szArrYdEqpGp[y+1];
								szArrStlNo[y] = szArrStlNo[y+1];   
								szArrYdEqpGp[y+1] = szTmpYdEqpGp;
								szArrStlNo[y+1] = szTmpStlNo;
							}
						}
					}
				}
				
				szLogMsg = "----권상모음순서 Sorting 작업후 목표행선[" + szYD_AIM_RT_GP + "]---";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				for(int x = 0; x<intArrSize; x++ ) {
					szLogMsg = "스판번호[" + szArrYdEqpGp[x] + "] - 재료번호[" + szArrStlNo[x] + "]";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}
				
				
				for(int x = 0; x<intArrSize; x++ ) {
				
					for( int Loop_i = 1; Loop_i <= rsWrkBookMtl.size(); Loop_i++) {
						rsWrkBookMtl.absolute(Loop_i);
						recInRsSet = JDTORecordFactory.getInstance().create();
						recInRsSet.setRecord(rsWrkBookMtl.getRecord());
						
						
						if (szArrStlNo[x].equals(ydDaoUtils.paraRecChkNull(recInRsSet, "STL_NO"))){
							rsSort.addRecord(recInRsSet);
						}
						
					} //end of for
				}
				
				if (rsSort.size() != rsWrkBookMtl.size()) {
					szLogMsg = "----다른SPAN간 권상모음순서 Sorting 작업실패";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					szRtnVal = YdConstant.RETN_CD_FAILURE;
					return szRtnVal;
				}
				
				szLogMsg = "[" + szOperationName + "] - sortUpColSeq]--------------------- 처리 종료 --------------------------";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				return szRtnVal;
				
			} else { // 동일스판일 경우 예약재료가 많은 BED를 권상모음순서 순위조정
				rsBedCnt = JDTORecordFactory.getInstance().createRecordSet("Temp");
				
				if (szArrYdStkPos.length > 1) {
					szBeforeYdStkPos = szArrYdStkPos[0];
					intStlCnt = 1;
					for(int Loop_i = 1; Loop_i<szArrYdStkPos.length; Loop_i++) {
						if (szArrYdStkPos[Loop_i].equals(szBeforeYdStkPos)){
							intStlCnt++;
						} else {
							
							recInRsSet = JDTORecordFactory.getInstance().create();
							recInRsSet.setField("YD_STK_POS", szBeforeYdStkPos);
							recInRsSet.setField("YD_STK_CNT", ""+intStlCnt);
							rsBedCnt.addRecord(recInRsSet);
							
							szBeforeYdStkPos = szArrYdStkPos[Loop_i];
							intStlCnt = 1;
							
							
						}
							
					} //end for
					
					recInRsSet = JDTORecordFactory.getInstance().create();
					recInRsSet.setField("YD_STK_POS", szBeforeYdStkPos);
					recInRsSet.setField("YD_STK_CNT", ""+intStlCnt);
					rsBedCnt.addRecord(recInRsSet);
					
					
					if(rsBedCnt.size() > 1) {
						szSortStkPos = new String[rsBedCnt.size()];
						szSortStlCnt = new String[rsBedCnt.size()];
						
						intArrSize = rsBedCnt.size();
						
						for(int Loop_i = 1; Loop_i <= rsBedCnt.size(); Loop_i++){
							rsBedCnt.absolute(Loop_i);
							recInRsSet = rsBedCnt.getRecord();
							
							szSortStkPos[Loop_i - 1] = ydDaoUtils.paraRecChkNull(recInRsSet, "YD_STK_POS");
							szSortStlCnt[Loop_i - 1] = ydDaoUtils.paraRecChkNull(recInRsSet, "YD_STK_CNT");
							
						} // end for
					
						
						for(int x = 0; x<intArrSize-1; x++ ) {
							for(int y = 0; y<intArrSize-x-1; y++) {
								if(Integer.parseInt(szSortStlCnt[y]) < Integer.parseInt(szSortStlCnt[y+1])) {
									szTmpStkPos = szSortStkPos[y];
									szTmpStlCnt = szSortStlCnt[y];
									szSortStkPos[y] = szSortStkPos[y+1];
									szSortStlCnt[y] = szSortStlCnt[y+1];   
									szSortStkPos[y+1] = szTmpStkPos;
									szSortStlCnt[y+1] = szTmpStlCnt;
								}
							}
						}
						
						
						for(int x = 0; x<intArrSize; x++ ) {
							
							for( int Loop_i = 1; Loop_i <= rsWrkBookMtl.size(); Loop_i++) {
								rsWrkBookMtl.absolute(Loop_i);
								recInRsSet = JDTORecordFactory.getInstance().create();
								recInRsSet.setRecord(rsWrkBookMtl.getRecord());
								
								
								if (szSortStkPos[x].equals(ydDaoUtils.paraRecChkNull(recInRsSet, "YD_STK_COL_GP") + ydDaoUtils.paraRecChkNull(recInRsSet, "YD_STK_BED_NO"))){
									rsSort.addRecord(recInRsSet);
								}
								
							} //end of for
						}
						
						if (rsSort.size() != rsWrkBookMtl.size()) {
							szLogMsg = "----동일SPAN간 권상모음순서 Sorting 작업실패";
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
							szRtnVal = YdConstant.RETN_CD_FAILURE;
							return szRtnVal;
						}
												
					
					} else {
						szLogMsg = "[" + szOperationName + "] - sortUpColSeq]---------- SORT 대상 BED가 1개입니다 -----------------";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						szRtnVal = YdConstant.RETN_CD_FAILURE;
						return szRtnVal;
					}
				}
				
				szLogMsg = "[" + szOperationName + "] - sortUpColSeq]--------------------- 처리 종료 --------------------------";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				return szRtnVal;
			}
		} catch (Exception e) {
			szLogMsg = szOperationName + " ++++++++++++ 권상모음순서 정리 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			szRtnVal = YdConstant.RETN_CD_FAILURE;
			return szRtnVal;
		}
		
	} //end of sortUpColSeq
	
	/**
	 * 오퍼레이션명 : 준비스케줄 시 대상재 검색
	 * @param recInParam
	 * @param rsResult
	 * @return
	 * @throws JDTOException
	 */
	public int chkPrepLotGp_stl_list(String szSTL_LIST, String szYD_AIM_RT_GP, String szARR_WLOC_CD, JDTORecordSet rsResult) throws JDTOException {
		// 저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메소드명
		String szMethodName = "chkPrepLotGp";
		String szOperationName = "준비스케줄 시 대상재 검색";
		String szLogMsg = null;
		//레코드셋 선언
		JDTORecordSet outRecSet = null;
		String szYD_GP			= null;
		String szSPOS_WLOC_CD	= null;
		
		String[] strArrStlNo       = null;
		// 레코드 선언
		JDTORecord recPara = JDTORecordFactory.getInstance().create();

		try {
			
			strArrStlNo = szSTL_LIST.split(";");
			
			//적치열,베드에 적치된 해당하는 야드목표행선을 가진 대상재를 조회
			for(int Loop_i = 0; Loop_i < strArrStlNo.length; Loop_i++ ) {
				outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
				recPara.setField("STL_NO", strArrStlNo[Loop_i]);
					
				intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 110);
				
				
				//조회결과를 체크
				if( intRtnVal == 0 ) {
					szLogMsg = "재료번호 = " + strArrStlNo[Loop_i] + ", YD_AIM_RT_GP = " + szYD_AIM_RT_GP 
					+ "에 대상재가 존재하지 않습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					continue;
				}else if( intRtnVal < 0 ) {
					szLogMsg = "재료번호 = " + strArrStlNo[Loop_i] + ", YD_AIM_RT_GP = " + szYD_AIM_RT_GP
					+ "에 대상재를 조회 시 에러가 발생했습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					continue;
				}
				szLogMsg = "재료번호 = " + strArrStlNo[Loop_i] + ", YD_AIM_RT_GP = " + szYD_AIM_RT_GP 
				+ "에 대상재가 존재합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				szLogMsg = "1. rsResult : " + rsResult + ", outRecSet : " +  outRecSet;
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				rsResult.addAll(outRecSet);
				szLogMsg = "2. rsResult : " + rsResult + ", outRecSet : " +  outRecSet;
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			}
			
			intRtnVal = rsResult.size();
			
			// 리턴값 메세지처리
			if (intRtnVal >= 1) {

				blnRtnVal = true;

			} else if (intRtnVal == 0) {

				szLogMsg = szOperationName + " 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == -2) {

				szLogMsg = szOperationName + "  데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szLogMsg = szOperationName + "  데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch (Exception e) {
			szLogMsg = szOperationName + " ++++++++++++ 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			//blnRtnVal = false;
			intRtnVal = -100;
		}
		return intRtnVal;
	} // end of chkMvLotGp
	
	/**
	 * 오퍼레이션명 : 이적LOT편성
	 * @param recInParam
	 * @param rsResult
	 * @return
	 * @throws JDTOException
	 */
	public int chkGetMvPrepLotGP(JDTORecord recInParam, JDTORecordSet rsResult) throws JDTOException {
		String szYD_GP = null;
		String szYD_AIM_BAY_GP = null;
		String szYD_AIM_RT_GP = null;
		String szMsg = null;
		String szMethodName = "chkGetMvPrepLotGP";
		int intRtnVal = -1;
		try {
			szYD_GP  =  ydDaoUtils.paraRecChkNull(recInParam, "YD_GP");
			if( szYD_GP.equals("A")) {
				szYD_AIM_BAY_GP = "A";
				szYD_AIM_RT_GP = "C1";					//가열로보급대기
				recInParam.setField("YD_AIM_RT_GP", szYD_AIM_RT_GP);	//목표행선구분
				intRtnVal = chkGetCcChgPrepLotGP(recInParam, rsResult);
				if( intRtnVal == 0 ) {
					szYD_AIM_BAY_GP = "C";
					szYD_AIM_RT_GP = "C8";					//통합야드 이송대기
					recInParam.setField("YD_AIM_RT_GP", szYD_AIM_RT_GP);	//목표행선구분
					//보급 Lot 편성 대상 재료 Select
					intRtnVal = chkGetSMvLotGP(recInParam, rsResult);
					if( intRtnVal == 0 ) {
						szYD_AIM_BAY_GP = "C";
						szYD_AIM_RT_GP = "C9";				//외판출하대기
						recInParam.setField("YD_AIM_RT_GP", szYD_AIM_RT_GP);	//목표행선구분
						intRtnVal = chkGetOutplSlabMvLotGP(recInParam, rsResult);
						if( intRtnVal == 0 ) {
							szYD_AIM_BAY_GP = "C";
							szYD_AIM_RT_GP = "CC";			//충당대기
							recInParam.setField("YD_AIM_RT_GP", szYD_AIM_RT_GP);	//목표행선구분
							intRtnVal = chkGetMatchMvLotGP(recInParam, rsResult);
							if( intRtnVal == 0 ) {
								szYD_AIM_BAY_GP = "C";
								szYD_AIM_RT_GP = "CB";		//장기재
								recInParam.setField("YD_AIM_RT_GP", szYD_AIM_RT_GP);	//목표행선구분
								intRtnVal = chkGetLongTermMvLotGP(recInParam, rsResult);
							}
						}
					}
				}
			}else if( szYD_GP.equals("D")) {			//A후판슬라브야드
				szYD_AIM_BAY_GP = "A";					//A동만 존재
				//장입준비 이적 : 03, 02스판에서 01스판으로 이적
				szYD_AIM_RT_GP = "D1";									//가열로보급대기
				recInParam.setField("YD_AIM_RT_GP", szYD_AIM_RT_GP);	//목표행선구분
				intRtnVal = chkGetAPlChgPrepLotGP(recInParam, rsResult);
				if( intRtnVal == 0 ) {
					szYD_AIM_RT_GP = "D5";									//통합야드 이송대기
					recInParam.setField("YD_AIM_RT_GP", szYD_AIM_RT_GP);	//목표행선구분
					intRtnVal = chkGetSMvLotGP(recInParam, rsResult);
					if( intRtnVal == 0 ) {
						szYD_AIM_RT_GP = "D7";									//충당 대기
						recInParam.setField("YD_AIM_RT_GP", szYD_AIM_RT_GP);	//목표행선구분
						intRtnVal = chkGetLongTermMvLotGP(recInParam, rsResult);
						if( intRtnVal == 0 ) {
							szYD_AIM_RT_GP = "D6";									//장기재 대기
							recInParam.setField("YD_AIM_RT_GP", szYD_AIM_RT_GP);	//목표행선구분
							intRtnVal = chkGetLongTermMvLotGP(recInParam, rsResult);
						}
					}
				}
			}
		}catch(JDTOException e) {
			szMsg = "이적LOT편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw e;
		}
		return intRtnVal;
	}
	
	/**
	 * 오퍼레이션명 : C연주슬라브야드 장입준비 이적LOT편성
	 * @param recInParam
	 * @param rsResult
	 * @return
	 * @throws JDTOException
	 */
	public int chkGetCcChgPrepLotGP(JDTORecord recInParam, JDTORecordSet rsResult) throws JDTOException {
		String szOperationName = "C연주슬라브야드 장입준비 이적LOT편성";
		return chkGetMvLotGp(recInParam, rsResult, szOperationName);
	}
	
	/**
	 * 오퍼레이션명 : 통합야드이송대기 이적LOT편성
	 * @param recInParam
	 * @param rsResult
	 * @return
	 * @throws JDTOException
	 */
	public int chkGetSMvLotGP(JDTORecord recInParam, JDTORecordSet rsResult) throws JDTOException {
		String szOperationName = "통합야드이송대기 이적LOT편성";
		return chkGetMvLotGp(recInParam, rsResult, szOperationName);
	}
	/**
	 * 오퍼레이션명 : 외판출하대기 이적LOT편성
	 * @param recInParam
	 * @param rsResult
	 * @return
	 * @throws JDTOException
	 */
	public int chkGetOutplSlabMvLotGP(JDTORecord recInParam, JDTORecordSet rsResult) throws JDTOException {
		String szOperationName = "외판출하대기 이적LOT편성";
		return chkGetMvLotGp(recInParam, rsResult, szOperationName);
	}
	/**
	 * 오퍼레이션명 : 충당대기 이적LOT편성
	 * @param recInParam
	 * @param rsResult
	 * @return
	 * @throws JDTOException
	 */
	public int chkGetMatchMvLotGP(JDTORecord recInParam, JDTORecordSet rsResult) throws JDTOException {
		String szOperationName = "충당대기 이적LOT편성";
		return chkGetMvLotGp(recInParam, rsResult, szOperationName);
	}
	/**
	 * 오퍼레이션명 : 장기여재 이적LOT편성
	 * @param recInParam
	 * @param rsResult
	 * @return
	 * @throws JDTOException
	 */
	public int chkGetLongTermMvLotGP(JDTORecord recInParam, JDTORecordSet rsResult) throws JDTOException {
		String szOperationName = "장기여재 이적LOT편성";
		return chkGetMvLotGp(recInParam, rsResult, szOperationName);
	}
	
	/**
	 * 오퍼레이션명 : A후판슬라브야드 장입준비 이적LOT편성
	 * @param recInParam
	 * @param rsResult
	 * @return
	 * @throws JDTOException
	 */
	public int chkGetAPlChgPrepLotGP(JDTORecord recInParam, JDTORecordSet rsResult) throws JDTOException {
		String szOperationName = "A후판슬라브야드 장입준비 이적LOT편성";
		return chkGetMvLotGp(recInParam, rsResult, szOperationName);
	}

	/**
	 * 오퍼레이션명 : 이적LOT편성 데이터 유무체크 및 데이터 반환
	 * 
	 * @param recInParam
	 *            YD_GP,YD_BAY_GP,YD_AIM_RT_GP,ROW_CNT
	 * @param rsResult
	 * @param szOperationName
	 *            : 호출하는 오퍼레이션명
	 * @return
	 * @throws JDTOException
	 */
	public int chkGetMvLotGp(JDTORecord recInParam, JDTORecordSet rsResult,
			String szOperationName) throws JDTOException {

		// 저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메소드명
		String szMethodName = "chkGetMvLotGp";
		String szMsg = null;

		// 레코드 선언
		//JDTORecord recPara = null;

		try {
			// 레코드 생성
			// recPara = JDTORecordFactory.getInstance().create();

			// 적치열구분
			// recPara.setField("YD_STK_COL_GP", szYdStkColGp);
			// recPara.setField("YD_STK_COL_GP", szYdStkColGp.substring(0,
			// 4));
			// 적치Bed번호
			// recPara.setField("YD_STK_BED_NO", szYdStkBedNo);
			String szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(recInParam, "YD_AIM_RT_GP").trim();
			if( szYD_AIM_RT_GP.startsWith("C") ) {			//C연주 슬라브 야드
				recInParam.setField("ROW_CNT", "4");
			}else if( szYD_AIM_RT_GP.startsWith("D") ) {	//A후판 슬라브 야드
				recInParam.setField("ROW_CNT", "3");
			}

			// 저장품 테이블 조회
			// intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 21);
			if( szYD_AIM_RT_GP.equals("D5") || 			/* A후판 슬라브 야드 - 목표행선 : 통합야드 이송대기 - 대상재 ASC 검색 */
					szYD_AIM_RT_GP.equals("D6") || 		/* A후판 슬라브 야드 - 목표행선 : 장기재대기 */
					szYD_AIM_RT_GP.equals("D7") || 		/* A후판 슬라브 야드 - 목표행선 : 충당재대기 */
					szYD_AIM_RT_GP.equals("C1") ) {		/* A후판 슬라브 야드 - 목표행선 : 충당재대기 */
				intRtnVal = ydStockDao.getYdStock(recInParam, rsResult, 47);
			}else{
				intRtnVal = ydStockDao.getYdStock(recInParam, rsResult, 42);
			}

			// 리턴값 메세지처리
			if (intRtnVal >= 1) {

				blnRtnVal = true;

			} else if (intRtnVal == 0) {

				szMsg = szOperationName + " 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == -2) {

				szMsg = szOperationName + "  데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = szOperationName + "  데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch (Exception e) {
			szMsg = szOperationName + " 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: "
					+ e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			//blnRtnVal = false;
			intRtnVal = -100;
		}
		return intRtnVal;
	} // end of chkGetMvLotGp
	
	
	/**
	 * 오퍼레이션명 : 이적 작업요구재료 권상모음순서 정렬
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procFtmvOrdLotReqMtlSort(String[] szSTL_NO, String[] szYD_UP_COLL_SEQ, JDTORecordSet rsResult) throws JDTOException {
		// 메세지
		String szMsg = "";
		// METHOD명
		String szMethodName = "procFtmvOrdLotReqMtlSort";
		String szOperationName = "이적 작업요구재료 권상모음순서 정렬";
		// 사용자
		String szUser = "SYSTEM";
		
		try {
			
		} catch (Exception e) {
			szMsg = szOperationName + " 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: "
					+ e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}
		
	}
	
	
	/**
	 * 오퍼레이션명 : 이적 작업요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procFtmvOrdLotReq(JDTORecord msgRecord) throws JDTOException {

		// 작업예약 DAO
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		// 작업예약 재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		//저장품DAO
		YdStockDao ydStockDao = new YdStockDao();
		
		// 공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();

		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메세지
		String szMsg = "";
		// METHOD명
		String szMethodName = "procFtmvOrdLotReq";
		// 사용자
		String szUser = "SYSTEM";

		// 레코드 선언
		JDTORecord recPara = null;
		JDTORecord recStkPara = null;
		JDTORecord recStk = null;
		// 레코드셋 선언
		JDTORecordSet rsResult = null;

		/*// 설비ID(열구분)
		String szYD_STK_COL_GP = null;
		// 적치BED번호
		String szYD_STK_BED_NO = null;
		// 설비ID(목표적치열구분)
		String szYD_STK_COL_GP_TO = null;
		// 목표적치BED번호
		String szYD_STK_BED_NO_TO = null;*/
		//야드구분
		String szYD_GP = null;
		//동구분
		String szYD_BAY_GP = null;
		//목표야드구분
		String szYD_AIM_YD_GP = null;
		//목표동구분
		String szYD_AIM_BAY_GP = null;
		// 재료매수(int)
		int intMtlCnt = 0;
		// 재료번호
		String[] szSTL_NO = null;
		// 권상모음순서
		String[] szYD_UP_COLL_SEQ = null;
		// 스케줄코드
		String szYD_SCH_CD = null;
		// 야드스케쥴우선순위
		String szYD_SCH_PRIOR = null;
		// 스케줄 금지 유무
		String szYD_SCH_PROH_EXN = null;
		// 작업크레인
		String szYD_WRK_CRN = null;
		// 작업크레인스케쥴우선순위
		String szYD_WRK_CRN_PRIOR = null;
		// 대체크레인유무
		String szYD_ALT_CRN_YN = null;
		// 대체크레인스케쥴우선순위
		String szYD_ALT_CRN_PRIOR = null;
		// 대체크레인
		String szYD_ALT_CRN = null;
		// 선택크레인
		String szCrn = null;
		// 작업예약ID
		String szYD_WBOOK_ID = null;
		//야드 TO위치결정방법
		String szYD_TO_LOC_DCSN_MTD = null;
		//야드TO위치가이드
		String szYD_TO_LOC_GUIDE = null;
		//
		String szLOOP_NO		= null;
		//TC CODE
		String szTcCode			= null;

		// TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		// 에러 리턴
//		if (szRcvTcCode == null) {
//
//			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error ("
//					+ szRcvTcCode + ")";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			return;
//
//		}
		// TC CODE DISPLAY
		if (bDebugFlag) {

			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		}

		try {
			/*
			//스케줄코드
			recOutPara.setField("YD_SCH_CD",          szYD_SCH_CD);
			//Lot편성 매수
			recOutPara.setField("YD_LOT_GP_SH",       "" + rsTemp.size());
			//야드구분
			recOutPara.setField("YD_GP",      szYD_GP);
			//동구분
			recOutPara.setField("YD_BAY_GP",      szYD_BAY_GP);
			//목표야드구분
			recOutPara.setField("YD_AIM_YD_GP",      szYD_AIM_YD_GP);
			//목표동구분
			recOutPara.setField("YD_AIM_BAY_GP",      szYD_AIM_BAY_GP);
			//야드 TO위치결정방법
			recOutPara.setField("YD_TO_LOC_DCSN_MTD",      szYD_TO_LOC_DCSN_MTD);
			//야드TO위치가이드
			recOutPara.setField("YD_TO_LOC_GUIDE",      szYD_TO_LOC_GUIDE);
			//작업계획대차
			recOutPara.setField("YD_WRK_PLAN_TCAR",      szTCAR);
			*/
			// 받은 전문 편집
			// 스케줄코드
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD").trim();
			if (szYD_SCH_CD.equals("")) {

				szMsg = "[전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 야드구분
			szYD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP").trim();
			if (szYD_GP.equals("")) {

				szMsg = "[전문 이상] 야드구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 동구분
			szYD_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP").trim();
			if (szYD_BAY_GP.equals("")) {

				szMsg = "[전문 이상] 동구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}

			// 목표야드구분
			szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_YD_GP").trim();
			if (szYD_AIM_YD_GP.equals("")) {

				szMsg = "[전문 이상] 목표야드구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 목표동구분
			szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_BAY_GP").trim();
			if (szYD_AIM_BAY_GP.equals("")) {

				szMsg = "[전문 이상] 목표동구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_LOT_GP_SH");
			if (intMtlCnt == 0) {

				szMsg = "[전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			//야드 TO위치결정방법
			szYD_TO_LOC_DCSN_MTD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_TO_LOC_DCSN_MTD").trim();
			//야드TO위치가이드
			szYD_TO_LOC_GUIDE = ydDaoUtils.paraRecChkNull(msgRecord, "YD_TO_LOC_GUIDE").trim();
			//작업계획대차
			String szYD_WRK_PLAN_TCAR = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WRK_PLAN_TCAR").trim();
			//작업예약ID
			szYD_WBOOK_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");
			
			/*if (szYD_WRK_PLAN_TCAR.equals("")) {

				szMsg = "[전문 이상] 대차설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}*/
			// 재료번호
			szSTL_NO = new String[intMtlCnt + 1];
			// 권상모음순서
			szYD_UP_COLL_SEQ = new String[intMtlCnt + 1];

			// 재료번호, 권상모음순서
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,
						"STL_NO" + Loop_i);
				if (szSTL_NO[Loop_i].equals("")) {

					szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				}
				// 권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord, "YD_UP_COLL_SEQ" + Loop_i);
//				if (szYD_UP_COLL_SEQ[Loop_i].equals("")) {
//
//					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i]
//							+ ")에 대한 권상모음순서가 없습니다.";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//					return;
//
//				}
			}

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal)
				return;

			// 레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();

			// 스케줄CD 체크
			// 스케줄 금지 유무
			szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara,
					"YD_SCH_PROH_EXN").trim();
			// 작업크레인
			szYD_WRK_CRN = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN")
					.trim();
			// 작업크레인우선순위
			szYD_WRK_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara,
					"YD_WRK_CRN_PRIOR").trim();
			// 대체크레인유무
			szYD_ALT_CRN_YN = ydDaoUtils.paraRecChkNull(recPara,
					"YD_ALT_CRN_YN").trim();
			// 대체크레인
			szYD_ALT_CRN = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN")
					.trim();
			// 대체크레인우선순위
			szYD_ALT_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara,
					"YD_ALT_CRN_PRIOR").trim();

			// 스케줄 금지 유무가 "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if (szYD_SCH_PROH_EXN.equals("Y")) {

				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}

			// 작업크레인 설비 상태 체크
			blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);

			// 작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if (!blnRtnVal) {

				szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				// 대체크레인의 유무를 체크한다.
				// 대체크레인이 없으면 에러 리턴
				if (!szYD_ALT_CRN_YN.equals("Y")) {

					szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				}
				// 대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				// 대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {

					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				} else {
					// 대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					szCrn = szYD_ALT_CRN;
					szYD_SCH_PRIOR = szYD_ALT_CRN_PRIOR;
				}
			} else {
				// 작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				szCrn = szYD_WRK_CRN;
				szYD_SCH_PRIOR = szYD_WRK_CRN_PRIOR;
			}

			// 재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다.
			// 작업예약재료 등록 여부를 체크한다.
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 크레인사양과 저장품 사양을 체크(길이,폭,중량)
				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
				if (!blnRtnVal)
					return;

				// 다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if (!blnRtnVal)
					return;

			}
			
			//작업예약이 존재하지 않을 경우에 작업예약ID 생성
			if( szYD_WBOOK_ID.equals("") ) {
				// 리턴 recordSet 생성
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	
				// 작업예약ID 생성
				blnRtnVal = getYdWbookId(rsResult);
				if (!blnRtnVal)
					return;
				// 레코드추출
				rsResult.first();
				recPara = rsResult.getRecord();
			
				// 작업예약ID
				szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");
			}

			// INSERT 항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			

			// INSERT할 항목 SET
			/*
			 * recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			 * recPara.setField("YD_GP", szYD_GP); recPara.setField("YD_BAY_GP",
			 * szYD_BAY_GP); recPara.setField("YD_SCH_CD", szYD_SCH_CD);
			 * recPara.setField("REGISTER", szUser);
			 */

			// INSERT할 항목 SET
			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID); 		// 작업예약ID
			recPara.setField("YD_GP", szYD_GP); 					// 야드구분
			recPara.setField("YD_BAY_GP", szYD_BAY_GP); 			// 야드동구분
			recPara.setField("YD_SCH_CD", szYD_SCH_CD); 			// 야드스케쥴코드
			recPara.setField("YD_SCH_PRIOR", szYD_SCH_PRIOR); 		// 야드스케쥴우선순위
			recPara.setField("YD_AIM_YD_GP", szYD_AIM_YD_GP); 		// 야드목표야드구분
			recPara.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP); 	// 야드목표동구분
			recPara.setField("YD_TO_LOC_DCSN_MTD", szYD_TO_LOC_DCSN_MTD); 		//야드 TO위치결정방법
			recPara.setField("YD_TO_LOC_GUIDE", szYD_TO_LOC_GUIDE); 	//야드TO위치가이드
			recPara.setField("YD_WRK_PLAN_TCAR", szYD_WRK_PLAN_TCAR); // 작업계획대차
			// 추후 추가항목
			// 야드스케쥴진행상태
			// 야드스케쥴기동구분
			// 야드스케쥴요청구분
			recPara.setField("REGISTER", szUser);

			// 작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}

			// 조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			recStk = JDTORecordFactory.getInstance().create();

			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			// recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			// recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", szUser);

			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 리턴 recordSet 생성
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				// 재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				blnRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C",
						rsResult);
				if (!blnRtnVal) {
					szMsg = "재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져오는 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					// 예외를 발생시켜 롤백 시킴
					throw new DAOException(szSessionName + " : " + szMethodName + " - " + szMsg);
					// return;
				}

				// 레코드추출
				rsResult.first();
				recStkPara = rsResult.getRecord();

				// 재료번호
				recPara.setField("STL_NO", szSTL_NO[Loop_i]);
				// 적치열구분
				recPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(
						recStkPara, "YD_STK_COL_GP"));
				// 적치BED번호
				recPara.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(
						recStkPara, "YD_STK_BED_NO"));
				// 적치단번호
				recPara.setField("YD_STK_LYR_NO", ydDaoUtils.paraRecChkNull(
						recStkPara, "YD_STK_LYR_NO"));
				// 권상모음순서
				recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ[Loop_i]);

				// 작업예약재료 테이블에 등록한다.
				intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
				
				if (intRtnVal < 1) {
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					// 예외를 발생시켜 롤백 시킴
					throw new DAOException(szSessionName + " : " + szMethodName + " - " + szMsg);
					// return;
				}
				
				//저장품에 작업예약ID를 등록한다.
				recStk.setField("STL_NO", szSTL_NO[Loop_i]);
				recStk.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
				recStk.setField("YD_SCH_CD", szYD_SCH_CD);
				intRtnVal = ydStockDao.updYdStock(recStk, 0);
				if (intRtnVal < 1) {
					szMsg = "저장품 데이터 수정 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					// 예외를 발생시켜 롤백 시킴
					throw new DAOException(szSessionName + " : " + szMethodName + " - " + szMsg);
					// return;
				}
			}

			szLOOP_NO = ydDaoUtils.paraRecChkNull(msgRecord, "LOOP_NO");
			// C연주크레인스케쥴메인 호출 : 스케쥴코드, 설비ID(크레인) - 동내이적 시 호출
			recPara = JDTORecordFactory.getInstance().create();
			if( szYD_WRK_PLAN_TCAR.equals("")) {
				szMsg = " 동내이적 처리  : LOOP_NO - " + szLOOP_NO;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				/*
				 * 크레인이 작업중이면 크레인 작업지시를 보내지 않고 작업예약만 등록처리함
				 * 크레인 설비의 작업상태를 확인하는 모듈 호출
				 */
				if( szLOOP_NO.equals("") || szLOOP_NO.equals("1") )		{
					String szYD_EQP_STAT = YdCommonUtils.getYdEqpStat(szCrn);
					
					szMsg = " 크레인의 작업상태가 " + szYD_EQP_STAT + " 이므로 " + (szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_IDLE) ? "크레인 스케줄 생성 가능합니다." : "크레인 스케줄 생성 불가능합니다.");
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					if( szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_IDLE)) {			//크레인이 IDLE인 상태인 경우에만 크레인 스케줄메인을 호출
					
						if( szYD_GP.equals("A") ) {						//C연주슬라브야드
							//recPara.setField("JMS_TC_CD", "YDYDJ500");
							szTcCode = "YDYDJ500";
						}else if(szYD_GP.equals("D")) {					//A후판슬라브야드
							//recPara.setField("JMS_TC_CD", "YDYDJ503");
							szTcCode = "YDYDJ503";
						}else if(szYD_GP.equals("K")) {					//후판제품야드
							//recPara.setField("JMS_TC_CD", "YDYDJ506");
							szTcCode = "YDYDJ506";
						}
						
						//-------------------------------------------------------------------------------------------------
						//	전문버퍼 모듈을 사용해서 전송 - 임춘수 수정 : 2009.11.27
						//-------------------------------------------------------------------------------------------------
						
						recPara.setField("JMS_TC_CD", 					YdConstant.YDYDJ701);
						recPara.setField(YdConstant.BUFFER_TC_CD, 		szTcCode);
						recPara.setField("YD_SCH_CD", 					szYD_SCH_CD);
						recPara.setField("YD_EQP_ID", 					szCrn);
						
						ydDelegate.sendMsg(recPara);
						
						//-------------------------------------------------------------------------------------------------
					}
				}
			}else{
				
				szMsg = " 동간이적 처리 : LOOP_NO - " + szLOOP_NO;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				if( szLOOP_NO.equals("") || szLOOP_NO.equals("1") )		{
					
					//-------------------------------------------------------------------------------------------------
					//동간 이적 시는 대차상차스케쥴 호출 - 전문버퍼 모듈을 사용해서 전송하는 방식으로 변경 : 임춘수 수정 2009.11.27
					//-------------------------------------------------------------------------------------------------
					if( szYD_GP.equals("A") ) {						//C연주슬라브야드
						szTcCode = "YDYDJ520";
					}else if(szYD_GP.equals("D")) {					//A후판슬라브야드
						szTcCode = "YDYDJ522";
					}
					
					recPara.setField("JMS_TC_CD", 					YdConstant.YDYDJ701);
					recPara.setField(YdConstant.BUFFER_TC_CD, 		szTcCode);
					recPara.setField("YD_EQP_ID", 					szYD_WRK_PLAN_TCAR);
					
					//-------------------------------------------------------------------------------------------------
					//	작업예약ID항목 제거 : 임춘수 수정 2010.01.07
					//	여러번의 공대차스케줄 호출 시 해당 작업계획대차로 되어 있는 우선순위가 빠른 작업예약을 하나만 조회가 되도록 하기
					//	위해서 제거
					//-------------------------------------------------------------------------------------------------
					//recPara.setField("YD_WBOOK_ID", 				szYD_WBOOK_ID);
					//-------------------------------------------------------------------------------------------------
					
					//대차스케쥴이 존재하지 않는 경우 대차공대차 상차스케쥴 호출 필요
				
					ydDelegate.sendMsg(recPara);
					
					szMsg = " 대차공대차 상차스케쥴 호출[JMS전송] : LOOP_NO - " + szLOOP_NO;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					//-------------------------------------------------------------------------------------------------
				}
			}

		} catch (Exception e) {
			szMsg = " Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szSessionName + " : " + szMethodName + " - " + szMsg);
		}

	} // end of procFtmvOrdLotReq()

	/**
	 * 오퍼레이션명 : C연주공Bed확보Lot편성
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procCCsEmptyBedSecurLotComp(JDTORecord msgRecord)
			throws JDTOException {

		YdDelegate ydDelegate = new YdDelegate();
		// 공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		// 공용 METHOD
		YdUtils ydutils = new YdUtils();

		// 레코드 선언
		JDTORecord recPara = null;
		JDTORecord recOutPara = null;
		// 레코드셋 선언
		JDTORecordSet rsResult = null;

		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메세지
		String szMsg = "";
		// 메소드명
		String szMethodName = "procCCsEmptyBedSecurLotComp";
		// 사용자
		String szUser = "SYSTEM";

		// Lot 편성 재료 매수
		int intLotGpSh = 0;
		// 전문 생성 일시
		String szDate = null;

		// 적치열구분
		String szYD_STK_COL_GP = null;
		// 적치BED번호
		String szYD_STK_BED_NO = null;
		// to_적치열구분
		String szYD_STK_COL_GP_TO = null;
		// to_적치BED번호
		String szYD_STK_BED_NO_TO = null;
		// 스케줄코드
		String szYD_SCH_CD = null;
		// 재료번호
		String szSTL_NO = null;
		// 적치단 재료상태
		String szYD_STK_LYR_MTL_STAT = null;

		// TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		// 에러 리턴
		if (szRcvTcCode == null) {

			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error ("
					+ szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;

		}
		// TC CODE DISPLAY
		if (bDebugFlag) {

			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		}

		try {
			// 받은 전문 편집
			// 적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_COL_GP").trim();
			if (szYD_STK_COL_GP.equals("")) {

				szMsg = "[전문 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			/*
			 * //적치BED번호 szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord,
			 * "YD_STK_BED_NO").trim(); if (szYD_STK_BED_NO.equals("")) {
			 * 
			 * szMsg = "[전문 이상] 적치BED번호가 없습니다."; ydUtils.putLog(szSessionName,
			 * szMethodName, szMsg, YdConstant.ERROR); return;
			 * 
			 * } //to_적치열구분 szYD_STK_COL_GP_TO =
			 * ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP_TO").trim();
			 * if (szYD_STK_COL_GP_TO.equals("")) {
			 * 
			 * szMsg = "[전문 이상] to_적치열구분이 없습니다."; ydUtils.putLog(szSessionName,
			 * szMethodName, szMsg, YdConstant.ERROR); return;
			 * 
			 * } //to_적치BED번호 szYD_STK_BED_NO_TO =
			 * ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO_TO").trim();
			 * if (szYD_STK_BED_NO_TO.equals("")) {
			 * 
			 * szMsg = "[전문 이상] to_적치BED번호가 없습니다.";
			 * ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR); return;
			 * 
			 * }
			 */

			// =================================================================================
			// 수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			// BRE 등록 안됨...테스트용 스케줄코드 생성
			// 추후 구현..
			// szYD_SCH_CD = "AASH02MM";
			// =================================================================================

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			// 보급 Lot 편성 대상 재료 Select
			blnRtnVal = chkGetCCcEmptyBedSecurLotGp(szYD_STK_COL_GP,
					szYD_STK_BED_NO, rsResult);
			if (!blnRtnVal)
				return;

			// Lot 편성 매수
			intLotGpSh = rsResult.size();

			// 레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			// JMS TC CODE
			recOutPara.setField("JMS_TC_CD", "YDYDJ261");
			// 레코드 커서 처음으로
			rsResult.first();

			// 보급 Lot 편성 재료 매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= intLotGpSh; Loop_i++) {

				// 레코드 추출
				recPara = rsResult.getRecord();
				if (Loop_i == 1) {
					szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara,
							"YD_STK_COL_GP");
					szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recPara,
							"YD_STK_BED_NO");
				}

				// 재료번호
				szSTL_NO = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
				// 적치단재료상태
				szYD_STK_LYR_MTL_STAT = ydDaoUtils.paraRecChkNull(recPara,
						"YD_STK_LYR_MTL_STAT");

				// 적치단재료상태가 권상대기중(U)이거나 권하대기중(D)이면 에러리턴
				if (szYD_STK_LYR_MTL_STAT.trim().equals("U")
						|| szYD_STK_LYR_MTL_STAT.trim().equals("D")) {

					szMsg = "적치열구분 ("
							+ szYD_STK_COL_GP
							+ "), "
							+ "적치Bed번호 ("
							+ szYD_STK_BED_NO
							+ "), "
							+ "적치단번호 ("
							+ ydDaoUtils.paraRecChkNull(recPara,
									"YD_STK_LYR_NO") + "), " + "재료번호 ("
							+ szSTL_NO + ") " + "의 적치단재료상태가 ("
							+ szYD_STK_LYR_MTL_STAT + ") 입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				}

				// 다른 크레인 스케줄에 등록 되어 있는지 체크
				blnRtnVal = chkCrnWrkMtl(szSTL_NO);
				if (!blnRtnVal)
					return;

				// 재료번호
				recOutPara.setField("STL_NO" + Loop_i, szSTL_NO);
				// 권상모음순서
				recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, "" + Loop_i);

				// 다음 레코드로
				rsResult.next();

			}
			// 스케쥴코드 생성
			szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "YD00MM"; // 야드(1)+동(1)+스판(2)+
																	// 00 +
																	// M(이적) +
																	// M(분할없음)

			// 전문 발생 일시
			szDate = ydUtils.getCurDate("yyyy/MM/dd HH:mm:ss");

			// 발생 일시
			recOutPara.setField("JMS_TC_CREATE_DDTT", szDate);
			// 스케줄코드
			recOutPara.setField("YD_SCH_CD", szYD_SCH_CD);
			// Lot편성 매수
			recOutPara.setField("YD_LOT_GP_SH", "" + intLotGpSh);
			// 적치열구분
			recOutPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			// 적치BED번호
			recOutPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			/*
			 * //목표적치열구분 recOutPara.setField("YD_STK_COL_GP_TO",
			 * szYD_STK_COL_GP_TO); //목표적치BED번호
			 * recOutPara.setField("YD_STK_BED_NO_TO", szYD_STK_BED_NO_TO);
			 */

			// 전문 송신
			ydDelegate.sendMsg(recOutPara);
			szMsg = "C연주 공Bed확보 Lot 편성 후 C연주 공Bed확보 작업요구 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		} catch (Exception e) {
			szMsg = "C연주 공Bed확보 Lot 편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}

	} // end of procCCsEmptyBedSecurLotComp

	/**
	 * 오퍼레이션명 : C연주공Bed확보작업요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procCCsEmptyBedSecurWrkReq(JDTORecord msgRecord)
			throws JDTOException {

		// 스케줄기준 DAO
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		// 작업예약 재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		// 공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		// 공용 METHOD
		YdUtils ydutils = new YdUtils();

		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메세지
		String szMsg = "";
		// METHOD명
		String szMethodName = "procCCsEmptyBedSecurWrkReq";
		// 사용자
		String szUser = "SYSTEM";

		// 레코드 선언
		JDTORecord recPara = null;
		JDTORecord recStkPara = null;
		// 레코드셋 선언
		JDTORecordSet rsResult = null;

		// 설비ID(적치열구분)
		String szYD_STK_COL_GP = null;
		// 적치BED번호
		String szYD_STK_BED_NO = null;
		// 설비ID(목표적치열구분)
		String szYD_STK_COL_GP_TO = null;
		// 목표적치BED번호
		String szYD_STK_BED_NO_TO = null;
		// 재료매수(int)
		int intMtlCnt = 0;
		// 재료번호
		String[] szSTL_NO = null;
		// 권상모음순서
		String[] szYD_UP_COLL_SEQ = null;
		// 스케줄코드
		String szYD_SCH_CD = null;
		// 야드스케쥴우선순위
		String szYD_SCH_PRIOR = null;
		// 스케줄 금지 유무
		String szYD_SCH_PROH_EXN = null;
		// 작업크레인
		String szYD_WRK_CRN = null;
		// 작업크레인우선순위
		String szYD_WRK_CRN_PRIOR = null;
		// 대체크레인유무
		String szYD_ALT_CRN_YN = null;
		// 대체크레인
		String szYD_ALT_CRN = null;
		// 대체크레인우선순위
		String szYD_ALT_CRN_PRIOR = null;
		// 선택크레인
		String szCrn = null;
		// 작업예약ID
		String szYD_WBOOK_ID = null;

		// TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		// 에러 리턴
		if (szRcvTcCode == null) {

			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error ("
					+ szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;

		}
		// TC CODE DISPLAY
		if (bDebugFlag) {

			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		}

		try {

			// 받은 전문 편집
			// 스케줄코드
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			if (szYD_SCH_CD.equals("")) {

				szMsg = "[전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_COL_GP");
			if (szYD_STK_COL_GP.equals("")) {

				szMsg = "[전문 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_BED_NO");
			if (szYD_STK_BED_NO.equals("")) {

				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			/*
			 * //목표적치열구분 szYD_STK_COL_GP_TO =
			 * ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP_TO"); if
			 * (szYD_STK_COL_GP_TO.equals("")) {
			 * 
			 * szMsg = "[전문 이상] 목표적치열구분이 없습니다."; ydUtils.putLog(szSessionName,
			 * szMethodName, szMsg, YdConstant.ERROR); return;
			 * 
			 * } //목표적치BED번호 szYD_STK_BED_NO_TO =
			 * ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO_TO"); if
			 * (szYD_STK_BED_NO_TO.equals("")) {
			 * 
			 * szMsg = "[전문 이상] 목표적치BED번호가 없습니다."; ydUtils.putLog(szSessionName,
			 * szMethodName, szMsg, YdConstant.ERROR); return;
			 * 
			 * }
			 */
			// 재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_LOT_GP_SH");
			if (intMtlCnt == 0) {

				szMsg = "[전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 재료번호
			szSTL_NO = new String[intMtlCnt + 1];
			// 권상모음순서
			szYD_UP_COLL_SEQ = new String[intMtlCnt + 1];

			// 재료번호, 권상모음순서
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,
						"STL_NO" + Loop_i);
				if (szSTL_NO[Loop_i].equals("")) {

					szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				}
				// 권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,
						"YD_UP_COLL_SEQ" + Loop_i);
				if (szYD_UP_COLL_SEQ[Loop_i].equals("")) {

					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i]
							+ ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				}
			}

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal)
				return;

			// 레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();

			// 스케줄CD 체크
			// 스케줄 금지 유무
			szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara,
					"YD_SCH_PROH_EXN");
			// 작업크레인
			szYD_WRK_CRN = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
			// 작업크레인우선순위
			szYD_WRK_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara,
					"YD_WRK_CRN_PRIOR");
			// 대체크레인유무
			szYD_ALT_CRN_YN = ydDaoUtils.paraRecChkNull(recPara,
					"YD_ALT_CRN_YN");
			// 대체크레인
			szYD_ALT_CRN = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
			// 대체크레인우선순위
			szYD_ALT_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_PRIOR");
			// 스케줄 금지 유무가 "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if (szYD_SCH_PROH_EXN.equals("Y")) {

				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}

			// 작업크레인 설비 상태 체크
			blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);

			// 작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if (!blnRtnVal) {

				szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				// 대체크레인의 유무를 체크한다.
				// 대체크레인이 없으면 에러 리턴
				if (!szYD_ALT_CRN_YN.equals("Y")) {

					szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				}
				// 대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				// 대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {

					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				} else {
					// 대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					szCrn = szYD_ALT_CRN;
					szYD_SCH_PRIOR = szYD_ALT_CRN_PRIOR;
				}
			} else {
				// 작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				szCrn = szYD_WRK_CRN;
				szYD_SCH_PRIOR = szYD_WRK_CRN_PRIOR;
			}

			// 재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다.
			// 작업예약재료 등록 여부를 체크한다.
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 크레인사양과 저장품 사양을 체크(길이,폭,중량)
				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
				if (!blnRtnVal)
					return;

				// 다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if (!blnRtnVal)
					return;

			}

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if (!blnRtnVal)
				return;
			// 레코드추출
			rsResult.first();
			recPara = rsResult.getRecord();
			// 작업예약ID
			szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");

			// INSERT 항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			// 야드구분
			String szYD_GP = szYD_SCH_CD.substring(0, 1);
			// 동구분
			String szYD_BAY_GP = szYD_SCH_CD.substring(1, 2);
			// 목표야드구분
			// String szYD_GP_TO = szYD_STK_COL_GP_TO.substring(0, 1);
			String szYD_GP_TO = szYD_GP;
			// 목표동구분
			// String szYD_BAY_GP_TO = szYD_STK_COL_GP_TO.substring(1,2);
			String szYD_BAY_GP_TO = szYD_BAY_GP;

			// INSERT할 항목 SET
			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID); // 작업예약ID
			recPara.setField("YD_GP", szYD_GP); // 야드구분
			recPara.setField("YD_BAY_GP", szYD_BAY_GP); // 야드동구분
			recPara.setField("YD_SCH_CD", szYD_SCH_CD); // 야드스케쥴코드
			recPara.setField("YD_SCH_PRIOR", szYD_SCH_PRIOR); // 야드스케쥴우선순위
			recPara.setField("YD_AIM_YD_GP", szYD_GP_TO); // 야드목표야드구분
			recPara.setField("YD_AIM_BAY_GP", szYD_BAY_GP_TO); // 야드목표동구분
			/*
			 * recPara.setField("YD_TO_LOC_DCSN_MTD", "F"); //야드TO위치결정방법 -
			 * 지정위치(F) recPara.setField("YD_TO_LOC_GUIDE", szYD_STK_COL_GP_TO +
			 * szYD_STK_BED_NO_TO); //야드TO위치Guide
			 */// 추후 추가항목
			// 야드스케쥴진행상태
			// 야드스케쥴기동구분
			// 야드스케쥴요청구분
			recPara.setField("REGISTER", szUser);

			// 작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}

			// 조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();

			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			// recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			// recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", szUser);

			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 리턴 recordSet 생성
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				// 재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				blnRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C",
						rsResult);
				if (!blnRtnVal) {
					szMsg = "재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져오는 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					// 예외를 발생시켜 롤백 시킴
					throw new DAOException(szSessionName + " : " + szMethodName
							+ " - " + szMsg);
					// return;
				}

				// 레코드추출
				rsResult.first();
				recStkPara = rsResult.getRecord();

				// 재료번호
				recPara.setField("STL_NO", szSTL_NO[Loop_i]);
				// 적치열구분
				recPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(
						recStkPara, "YD_STK_COL_GP"));
				// 적치BED번호
				recPara.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(
						recStkPara, "YD_STK_BED_NO"));
				// 적치단번호
				recPara.setField("YD_STK_LYR_NO", ydDaoUtils.paraRecChkNull(
						recStkPara, "YD_STK_LYR_NO"));
				// 권상모음순서
				recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ[Loop_i]);

				// 작업예약재료 테이블에 등록한다.
				intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);

				if (intRtnVal < 1) {
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					// 예외를 발생시켜 롤백 시킴
					throw new DAOException(szSessionName + " : " + szMethodName
							+ " - " + szMsg);
					// return;
				}
			}

			// C연주크레인스케쥴메인 호출 : 스케쥴코드, 설비ID(크레인)
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("JMS_TC_CD", "YDYDJ500");
			recPara.setField("YD_SCH_CD", szYD_SCH_CD);
			recPara.setField("YD_EQP_ID", szCrn);

			ydDelegate.sendMsg(recPara);

		} catch (JDTOException e) {
			szMsg = "C연주 공Bed확보 작업요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(e);
		}
	} // end of procCCsEmptyBedSecurWrkReq()

	/**
	 * 오퍼레이션명 : C연주정리Lot편성
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procCCsReadjLotComp(JDTORecord msgRecord) throws JDTOException {

		YdDelegate ydDelegate = new YdDelegate();
		// 공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		// 공용 METHOD
		YdUtils ydutils = new YdUtils();

		// 레코드 선언
		JDTORecord recPara = null;
		JDTORecord recOutPara = null;
		// 레코드셋 선언
		JDTORecordSet rsResult = null;

		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메세지
		String szMsg = "";
		// 메소드명
		String szMethodName = "procCCsReadjLotComp";
		// 사용자
		String szUser = "SYSTEM";
		String szOperationName = "C연주정리Lot편성";

		// Lot 편성 재료 매수
		int intLotGpSh = 0;
		// 전문 생성 일시
		String szDate = null;

		// 적치열구분
		String szYD_STK_COL_GP = null;
		// 적치BED번호
		String szYD_STK_BED_NO = null;
		// to_적치열구분
		String szYD_STK_COL_GP_TO = null;
		// to_적치BED번호
		String szYD_STK_BED_NO_TO = null;
		// 목표행선구분
		String szYD_AIM_RT_GP = null;
		// 비교목표행선구분
		String szYD_AIM_RT_GP_COMP = null;
		// 스케줄코드
		String szYD_SCH_CD = null;
		// 재료번호
		String szSTL_NO = null;
		// 재료품목
		String szYD_MTL_ITEM = null;
		// 적치단 재료상태
		String szYD_STK_LYR_MTL_STAT = null;
		// 대차
		String szTCAR = "";
		// 권상모음순서
		int intYD_UP_COLL_SEQ = 0;

		// TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		// 에러 리턴
		if (szRcvTcCode == null) {

			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error ("
					+ szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;

		}
		// TC CODE DISPLAY
		if (bDebugFlag) {

			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		}

		try {
			// 받은 전문 편집
			// 적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_COL_GP").trim();
			if (szYD_STK_COL_GP.equals("")) {

				szMsg = "[전문 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_BED_NO").trim();
			if (szYD_STK_BED_NO.equals("")) {

				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 목표행선구분
			szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_AIM_RT_GP").trim();
			if (szYD_AIM_RT_GP.equals("")) {

				szMsg = "[전문 이상] 목표행선구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// to_적치열구분
			szYD_STK_COL_GP_TO = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_COL_GP_TO").trim();
			if (szYD_STK_COL_GP_TO.equals("")) {

				szMsg = "[전문 이상] to_적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// to_적치BED번호
			szYD_STK_BED_NO_TO = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_BED_NO_TO").trim();
			if (szYD_STK_BED_NO_TO.equals("")) {

				szMsg = "[전문 이상] to_적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}

			// =================================================================================
			// 수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			// BRE 등록 안됨...테스트용 스케줄코드 생성
			// 추후 구현..
			// szYD_SCH_CD = "AASH02MM";
			// =================================================================================

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			// 보급 Lot 편성 대상 재료 Select
			blnRtnVal = chkGetCCcReadjLotGp(szYD_STK_COL_GP, szYD_STK_BED_NO,
					rsResult);
			if (!blnRtnVal)
				return;

			// 레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			// JMS TC CODE
			recOutPara.setField("JMS_TC_CD", "YDYDJ263");
			// 레코드 커서 처음으로
			rsResult.first();

			// 보급 Lot 편성 재료 매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {

				// 레코드 추출
				recPara = rsResult.getRecord();

				// 재료번호
				szSTL_NO = ydDaoUtils.paraRecChkNull(recPara, "STL_NO").trim();
				// 적치단재료상태
				szYD_STK_LYR_MTL_STAT = ydDaoUtils.paraRecChkNull(recPara,
						"YD_STK_LYR_MTL_STAT").trim();
				// 비교목표행선구분
				szYD_AIM_RT_GP_COMP = ydDaoUtils.paraRecChkNull(recPara,
						"YD_AIM_RT_GP").trim();

				// 적치단재료상태가 권상대기중(U)이거나 권하대기중(D)이면 에러리턴
				if (szYD_STK_LYR_MTL_STAT.equals("U")
						|| szYD_STK_LYR_MTL_STAT.equals("D")) {

					szMsg = "적치열구분 (" + szYD_STK_COL_GP + "), " + "적치Bed번호 ("
							+ szYD_STK_BED_NO + "), " + "재료번호 (" + szSTL_NO
							+ ") " + "의 적치단재료상태가 (" + szYD_STK_LYR_MTL_STAT
							+ ") 입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				}

				// 다른 크레인 스케줄에 등록 되어 있는지 체크
				blnRtnVal = chkCrnWrkMtl(szSTL_NO);
				if (!blnRtnVal)
					return;

				// 목표행선이 다르면 Lot 편성에서 제외
				if (!szYD_AIM_RT_GP.equals(szYD_AIM_RT_GP_COMP)) {

					rsResult.next();
					continue;

				}

				// 재료품목[첫번째 레코드를 대표값으로 사용]
				if (Loop_i == 1) {
					szYD_MTL_ITEM = ydDaoUtils.paraRecChkNull(recPara,
							"YD_MTL_ITEM").trim();
				}
				// 권상모음순서
				intYD_UP_COLL_SEQ = intYD_UP_COLL_SEQ + 1;
				// 재료번호
				recOutPara.setField("STL_NO" + Loop_i, szSTL_NO);
				// 권상모음순서
				recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, ""
						+ intYD_UP_COLL_SEQ);

				// 다음 레코드로
				rsResult.next();

			}

			/*
			 * 스케쥴코드 생성 현재동 = 목표동 : 동내이적 현재동 <> 목표동 : 동간이적 - 대차배정 필요
			 */
			JDTORecord recInParam = JDTORecordFactory.getInstance().create();
			YdCommonUtils.mkCrnSchCdForMv(recInParam, szYD_STK_COL_GP,
					szYD_STK_COL_GP_TO, szYD_MTL_ITEM, szYD_AIM_RT_GP_COMP);
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(recInParam, "YD_SCH_CD")
					.trim();
			szTCAR = YdCommonUtils.sltTCarByYD(szYD_STK_COL_GP.substring(0, 1), szYD_STK_COL_GP.substring(1, 2), szYD_STK_COL_GP_TO.substring(0, 1), szYD_STK_COL_GP_TO.substring(1, 2),
					szYD_MTL_ITEM, szYD_AIM_RT_GP_COMP);
			if (szYD_SCH_CD.equals("")) {
				szMsg = "C연주 정리 Lot 편성 처리중 스케쥴코드 할당 실패";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				// 스케쥴코드를 할당 못 받더라도 기본 스케쥴코드 생성하는 로직이 필요함
				return;
			}
			// 전문 발생 일시
			szDate = YdUtils.getCurDate("yyyy/MM/dd HH:mm:ss");

			// 발생 일시
			recOutPara.setField("JMS_TC_CREATE_DDTT", szDate);
			// 스케줄코드
			recOutPara.setField("YD_SCH_CD", szYD_SCH_CD);
			// Lot편성 매수
			recOutPara.setField("YD_LOT_GP_SH", "" + intLotGpSh);
			// 적치열구분
			recOutPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			// 적치BED번호
			recOutPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			// 목표적치열구분
			recOutPara.setField("YD_STK_COL_GP_TO", szYD_STK_COL_GP_TO);
			// 목표적치BED번호
			recOutPara.setField("YD_STK_BED_NO_TO", szYD_STK_BED_NO_TO);
			// 작업계획대차 - 동간이적 시 값이 존재함
			recOutPara.setField("YD_WRK_PLAN_TCAR", szTCAR);

			// 전문내용 로그 출력
			ydUtils.displayRecord(szOperationName, recOutPara);

			// 전문 송신
			ydDelegate.sendMsg(recOutPara);
			szMsg = "C연주 정리 Lot 편성 후 C연주 정리 작업요구 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		} catch (Exception e) {
			szMsg = "C연주 정리 Lot 편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			// return;
		}

	} // end of procCCsReadjLotComp

	/**
	 * 오퍼레이션명 : C연주정리작업요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procCCsReadjWrkReq(JDTORecord msgRecord) throws JDTOException {

		// 스케줄기준 DAO
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		// 작업예약 재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		// 공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();

		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메세지
		String szMsg = "";
		// METHOD명
		String szMethodName = "procCCsReadjWrkReq";
		// 사용자
		String szUser = "SYSTEM";

		// 레코드 선언
		JDTORecord recPara = null;
		JDTORecord recStkPara = null;
		// 레코드셋 선언
		JDTORecordSet rsResult = null;

		// 설비ID(열구분)
		String szYD_STK_COL_GP = null;
		// 적치BED번호
		String szYD_STK_BED_NO = null;
		// 설비ID(목표적치열구분)
		String szYD_STK_COL_GP_TO = null;
		// 목표적치BED번호
		String szYD_STK_BED_NO_TO = null;
		// 재료매수(int)
		int intMtlCnt = 0;
		// 재료번호
		String[] szSTL_NO = null;
		// 권상모음순서
		String[] szYD_UP_COLL_SEQ = null;
		// 스케줄코드
		String szYD_SCH_CD = null;
		// 야드스케쥴우선순위
		String szYD_SCH_PRIOR = null;
		// 스케줄 금지 유무
		String szYD_SCH_PROH_EXN = null;
		// 작업크레인
		String szYD_WRK_CRN = null;
		// 작업크레인스케쥴우선순위
		String szYD_WRK_CRN_PRIOR = null;
		// 대체크레인유무
		String szYD_ALT_CRN_YN = null;
		// 대체크레인스케쥴우선순위
		String szYD_ALT_CRN_PRIOR = null;
		// 대체크레인
		String szYD_ALT_CRN = null;
		// 선택크레인
		String szCrn = null;
		// 작업예약ID
		String szYD_WBOOK_ID = null;

		// TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		// 에러 리턴
		if (szRcvTcCode == null) {

			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error ("
					+ szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;

		}
		// TC CODE DISPLAY
		if (bDebugFlag) {

			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		}

		try {

			// 받은 전문 편집
			// 스케줄코드
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD")
					.trim();
			if (szYD_SCH_CD.equals("")) {

				szMsg = "[전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_COL_GP").trim();
			if (szYD_STK_COL_GP.equals("")) {

				szMsg = "[전문 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_BED_NO").trim();
			if (szYD_STK_BED_NO.equals("")) {

				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}

			// 목표적치열구분
			szYD_STK_COL_GP_TO = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_COL_GP_TO").trim();
			if (szYD_STK_COL_GP_TO.equals("")) {

				szMsg = "[전문 이상] 목표적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 목표적치BED번호
			szYD_STK_BED_NO_TO = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_BED_NO_TO").trim();
			if (szYD_STK_BED_NO_TO.equals("")) {

				szMsg = "[전문 이상] 목표적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_LOT_GP_SH");
			if (intMtlCnt == 0) {

				szMsg = "[전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}

			// 작업계획대차
			String szYD_WRK_PLAN_TCAR = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_WRK_PLAN_TCAR").trim();

			// 재료번호
			szSTL_NO = new String[intMtlCnt + 1];
			// 권상모음순서
			szYD_UP_COLL_SEQ = new String[intMtlCnt + 1];
			;

			// 재료번호, 권상모음순서
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,
						"STL_NO" + Loop_i);
				if (szSTL_NO[Loop_i].equals("")) {

					szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				}
				// 권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,
						"YD_UP_COLL_SEQ" + Loop_i);
				if (szYD_UP_COLL_SEQ[Loop_i].equals("")) {

					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i]
							+ ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				}
			}

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal)
				return;

			// 레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();

			// 스케줄CD 체크
			// 스케줄 금지 유무
			szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara,
					"YD_SCH_PROH_EXN").trim();
			// 작업크레인
			szYD_WRK_CRN = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN")
					.trim();
			// 작업크레인우선순위
			szYD_WRK_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara,
					"YD_WRK_CRN_PRIOR").trim();
			// 대체크레인유무
			szYD_ALT_CRN_YN = ydDaoUtils.paraRecChkNull(recPara,
					"YD_ALT_CRN_YN").trim();
			// 대체크레인
			szYD_ALT_CRN = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN")
					.trim();
			// 대체크레인우선순위
			szYD_ALT_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara,
					"YD_ALT_CRN_PRIOR").trim();

			// 스케줄 금지 유무가 "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if (szYD_SCH_PROH_EXN.equals("Y")) {

				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}

			// 작업크레인 설비 상태 체크
			blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);

			// 작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if (!blnRtnVal) {

				szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				// 대체크레인의 유무를 체크한다.
				// 대체크레인이 없으면 에러 리턴
				if (!szYD_ALT_CRN_YN.equals("Y")) {

					szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				}
				// 대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				// 대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {

					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				} else {
					// 대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					szCrn = szYD_ALT_CRN;
					szYD_SCH_PRIOR = szYD_ALT_CRN_PRIOR;
				}
			} else {
				// 작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				szCrn = szYD_WRK_CRN;
				szYD_SCH_PRIOR = szYD_WRK_CRN_PRIOR;
			}

			// 재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다.
			// 작업예약재료 등록 여부를 체크한다.
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 크레인사양과 저장품 사양을 체크(길이,폭,중량)
				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
				if (!blnRtnVal)
					return;

				// 다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if (!blnRtnVal)
					return;

			}

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if (!blnRtnVal)
				return;
			// 레코드추출
			rsResult.first();
			recPara = rsResult.getRecord();
			// 작업예약ID
			szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");

			// INSERT 항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			// 야드구분
			String szYD_GP = szYD_SCH_CD.substring(0, 1);
			// 동구분
			String szYD_BAY_GP = szYD_SCH_CD.substring(1, 2);
			// 목표야드구분
			String szYD_GP_TO = szYD_STK_COL_GP_TO.substring(0, 1);
			// 목표동구분
			String szYD_BAY_GP_TO = szYD_STK_COL_GP_TO.substring(1, 2);

			// INSERT할 항목 SET
			/*
			 * recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			 * recPara.setField("YD_GP", szYD_GP); recPara.setField("YD_BAY_GP",
			 * szYD_BAY_GP); recPara.setField("YD_SCH_CD", szYD_SCH_CD);
			 * recPara.setField("REGISTER", szUser);
			 */

			// INSERT할 항목 SET
			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID); // 작업예약ID
			recPara.setField("YD_GP", szYD_GP); // 야드구분
			recPara.setField("YD_BAY_GP", szYD_BAY_GP); // 야드동구분
			recPara.setField("YD_SCH_CD", szYD_SCH_CD); // 야드스케쥴코드
			recPara.setField("YD_SCH_PRIOR", szYD_SCH_PRIOR); // 야드스케쥴우선순위
			recPara.setField("YD_AIM_YD_GP", szYD_GP_TO); // 야드목표야드구분
			recPara.setField("YD_AIM_BAY_GP", szYD_BAY_GP_TO); // 야드목표동구분
			recPara.setField("YD_TO_LOC_DCSN_MTD", "F"); // 야드TO위치결정방법 - 지정위치(F)
			recPara.setField("YD_TO_LOC_GUIDE", szYD_STK_COL_GP_TO
					+ szYD_STK_BED_NO_TO); // 야드TO위치Guide
			recPara.setField("YD_WRK_PLAN_TCAR", szYD_WRK_PLAN_TCAR); // 작업계획대차
			// 추후 추가항목
			// 야드스케쥴진행상태
			// 야드스케쥴기동구분
			// 야드스케쥴요청구분
			recPara.setField("REGISTER", szUser);

			// 작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}

			// 조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();

			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			// recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			// recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", szUser);

			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 리턴 recordSet 생성
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				// 재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				blnRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C",
						rsResult);
				if (!blnRtnVal) {
					szMsg = "재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져오는 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					// 예외를 발생시켜 롤백 시킴
					throw new DAOException(szSessionName + " : " + szMethodName
							+ " - " + szMsg);
					// return;
				}

				// 레코드추출
				rsResult.first();
				recStkPara = rsResult.getRecord();

				// 재료번호
				recPara.setField("STL_NO", szSTL_NO[Loop_i]);
				// 적치열구분
				recPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(
						recStkPara, "YD_STK_COL_GP"));
				// 적치BED번호
				recPara.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(
						recStkPara, "YD_STK_BED_NO"));
				// 적치단번호
				recPara.setField("YD_STK_LYR_NO", ydDaoUtils.paraRecChkNull(
						recStkPara, "YD_STK_LYR_NO"));
				// 권상모음순서
				recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ[Loop_i]);

				// 작업예약재료 테이블에 등록한다.
				intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);

				if (intRtnVal < 1) {
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					// 예외를 발생시켜 롤백 시킴
					throw new DAOException(szSessionName + " : " + szMethodName
							+ " - " + szMsg);
					// return;
				}

			}

			// C연주크레인스케쥴메인 호출 : 스케쥴코드, 설비ID(크레인)
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("JMS_TC_CD", "YDYDJ500");
			recPara.setField("YD_SCH_CD", szYD_SCH_CD);
			recPara.setField("YD_EQP_ID", szCrn);

			ydDelegate.sendMsg(recPara);

		} catch (Exception e) {
			szMsg = "C연주 정리 작업요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}

	} // end of procCCsReadjWrkReq()
	
	/**
	 * 오퍼레이션명 : 대상재 상단 더미재 이적Lot편성
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procMvDummyMtlAboveTgMtlLotComp(JDTORecord msgRecord) throws JDTOException {

		//YdDelegate ydDelegate = new YdDelegate();
		// 공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		// 공용 METHOD
		//YdUtils ydutils = new YdUtils();

		// 레코드 선언
		JDTORecord recPara = null;
		JDTORecord recOutPara = null;
		// 레코드셋 선언
		JDTORecordSet rsResult = null;

		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		//int intRtnVal = 0;
		// 메세지
		String szMsg = "";
		// 메소드명
		String szMethodName = "procMvDummyMtlAboveTgMtlLotComp";
		// 오퍼레이션명
		String szOperationName = "대상재 상단 더미재 이적Lot편성";
		// 사용자
		//String szUser = "SYSTEM";

		// Lot 편성 재료 매수
		int intLotGpSh = 0;
		// 전문 생성 일시
		String szDate = null;

		// 야드구분
		String szYD_GP = null;
		// 동구분
		String szYD_BAY_GP = null;
		// 스판번호
		String szYD_SPAN_NO = null;
		// 목표행선구분
		String szYD_AIM_RT_GP = null;
		// 운송장비코드
		String szTRN_EQP_CD = null;
		// 야드작업허용중량
		long lngYD_WRK_ALW_WT = 0;
		// 스케줄코드
		String szYD_SCH_CD = null;
		//적치열구분
		String szYD_STK_COL_GP = null;
		//적치BED
		String szYD_STK_BED_NO = null;
		//재료번호
		String szSTL_NO = null;
		// 적치단재료상태
		String szYD_STK_LYR_MTL_STAT = null;
		//대차
		String szTCAR = "";

		// TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		// 에러 리턴
		if (szRcvTcCode == null) {

			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error ("
					+ szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;

		}
		// TC CODE DISPLAY
		if (bDebugFlag) {

			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		}

		try {
			// 받은 전문 편집
			// 적치열구분
			szYD_GP = ydDaoUtils.paraRecChkNull(msgRecord,	"YD_GP").trim();
			if (szYD_GP.equals("")) {

				szMsg = "[전문 이상] 야드구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 동구분
			szYD_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP").trim();
			if (szYD_BAY_GP.equals("")) {

				szMsg = "[전문 이상] 동구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 스판번호
			szYD_SPAN_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SPAN_NO").trim();
			if (szYD_SPAN_NO.equals("")) {

				szMsg = "[전문 이상] 스판번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 대상재의 목표행선구분
			szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_RT_GP").trim();
			if (szYD_AIM_RT_GP.equals("")) {

				szMsg = "[전문 이상] 목표행선구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 운송장비코드
			szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(msgRecord,	"TRN_EQP_CD").trim();
			if (szTRN_EQP_CD.equals("")) {

				szMsg = "[전문 이상] 운송장비코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 야드작업허용중량
			lngYD_WRK_ALW_WT = ydDaoUtils.paraRecChkNullLong(msgRecord, "YD_WRK_ALW_WT");
			if (lngYD_WRK_ALW_WT == 0) {

				szMsg = "[전문 이상] 야드작업허용중량이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			// 더미재이적 Lot 편성 대상 재료 Select
			blnRtnVal = chkGetMvDummyMtlAboveTgMtlLot(szYD_GP, szYD_BAY_GP, szYD_SPAN_NO, szYD_AIM_RT_GP, lngYD_WRK_ALW_WT, rsResult);
			if (!blnRtnVal)
				return;

			// 레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			// JMS TC CODE
			recOutPara.setField("JMS_TC_CD", "YDYDJ263");
			// 레코드 커서 처음으로
			rsResult.first();

			// 보급 Lot 편성 재료 매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {

				// 레코드 추출
				recPara = rsResult.getRecord();

				// 재료번호
				szSTL_NO = ydDaoUtils.paraRecChkNull(recPara, "STL_NO").trim();
				// 적치단재료상태
				szYD_STK_LYR_MTL_STAT = ydDaoUtils.paraRecChkNull(recPara,
						"YD_STK_LYR_MTL_STAT").trim();
				//적치열구분
				szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP").trim();
				//적치BED
				szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO").trim();

				// 적치단재료상태가 권상대기중(U)이거나 권하대기중(D)이면 에러리턴
				if (szYD_STK_LYR_MTL_STAT.equals("U")
						|| szYD_STK_LYR_MTL_STAT.equals("D")) {

					szMsg = "적치열구분 (" + szYD_STK_COL_GP + "), " + "적치Bed번호 ("
							+ szYD_STK_BED_NO + "), " + "재료번호 (" + szSTL_NO
							+ ") " + "의 적치단재료상태가 (" + szYD_STK_LYR_MTL_STAT
							+ ") 입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				}

				// 다른 크레인 스케줄에 등록 되어 있는지 체크
				blnRtnVal = chkCrnWrkMtl(szSTL_NO);
				if (!blnRtnVal)
					return;

				// 목표행선이 다르면 Lot 편성에서 제외
				/*if (!szYD_AIM_RT_GP.equals(szYD_AIM_RT_GP_COMP)) {

					rsResult.next();
					continue;

				}*/

				// 재료품목[첫번째 레코드를 대표값으로 사용]
				/*if (Loop_i == 1) {
					szYD_MTL_ITEM = ydDaoUtils.paraRecChkNull(recPara,
							"YD_MTL_ITEM").trim();
				}
				// 권상모음순서
				intYD_UP_COLL_SEQ = intYD_UP_COLL_SEQ + 1;*/
				// 재료번호
				recOutPara.setField("STL_NO" + Loop_i, szSTL_NO);
				// 권상모음순서
				recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, "");

				// 다음 레코드로
				rsResult.next();
				intLotGpSh++;
			}

			/*
			 * 스케쥴코드 생성 현재동 = 목표동 : 동내이적 현재동 <> 목표동 : 동간이적 - 대차배정 필요
			 */
			JDTORecord recInParam = JDTORecordFactory.getInstance().create();
			YdCommonUtils.mkCrnSchCdForMv(recInParam, szYD_STK_COL_GP.substring(0, 1), szYD_STK_COL_GP.substring(1, 2),szYD_STK_COL_GP.substring(0, 1), szYD_STK_COL_GP.substring(1, 2), "", szYD_AIM_RT_GP);
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(recInParam, "YD_SCH_CD")
					.trim();
			szTCAR = YdCommonUtils.sltTCarByYD(szYD_STK_COL_GP.substring(0, 1), szYD_STK_COL_GP.substring(1, 2), szYD_STK_COL_GP.substring(0, 1), szYD_STK_COL_GP.substring(1, 2),
					"", szYD_AIM_RT_GP);
			if (szYD_SCH_CD.equals("")) {
				szMsg = szOperationName + " 처리중 스케쥴코드 할당 실패";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				// 스케쥴코드를 할당 못 받더라도 기본 스케쥴코드 생성하는 로직이 필요함
				return;
			}
			// 전문 발생 일시
			szDate = YdUtils.getCurDate("yyyy/MM/dd HH:mm:ss");

			// 발생 일시
			recOutPara.setField("JMS_TC_CREATE_DDTT", szDate);
			// 스케줄코드
			recOutPara.setField("YD_SCH_CD", szYD_SCH_CD);
			// Lot편성 매수
			recOutPara.setField("YD_LOT_GP_SH", "" + intLotGpSh);
			// 야드구분
			recOutPara.setField("YD_GP", szYD_GP);
			// 동구분
			recOutPara.setField("YD_BAY_GP", szYD_BAY_GP);
			// 목표적치열구분
			recOutPara.setField("YD_AIM_YD_GP", szYD_GP);
			// 목표적치BED번호
			recOutPara.setField("YD_AIM_BAY_GP", szYD_BAY_GP);
			// 작업계획대차 - 동간이적 시 값이 존재함
			recOutPara.setField("YD_WRK_PLAN_TCAR", szTCAR);

			// 전문내용 로그 출력
			ydUtils.displayRecord(szOperationName, recOutPara);

			// 전문 송신
//			ydDelegate.sendMsg(recOutPara);
			//대상재 상단 더미재 이적작업요구 호출
			procMvDummyMtlAboveTgMtlWrkReq(recOutPara);
			szMsg = szOperationName + " 후 대상재 상단 더미재 이적작업요구 호출 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		} catch (Exception e) {
			szMsg = szOperationName + " Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			// return;
		}

	} // end of procMvDummyMtlAboveTgMtlLotComp
	
	/**
	 * 오퍼레이션명 : 대상재 상단 더미재 이적작업요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procMvDummyMtlAboveTgMtlWrkReq(JDTORecord msgRecord) throws JDTOException {

		// 스케줄기준 DAO
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		// 작업예약 재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		// 공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();

		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메세지
		String szMsg = "";
		// METHOD명
		String szMethodName = "procMvDummyMtlAboveTgMtlWrkReq";
		// 사용자
		String szUser = "SYSTEM";

		// 레코드 선언
		JDTORecord recPara = null;
		JDTORecord recStkPara = null;
		// 레코드셋 선언
		JDTORecordSet rsResult = null;

		//야드구분
		String szYD_GP = null;
		//동구분
		String szYD_BAY_GP = null;
		//목표야드구분
		String szYD_AIM_YD_GP = null;
		//목표동구분
		String szYD_AIM_BAY_GP = null;
		// 재료매수(int)
		int intMtlCnt = 0;
		// 재료번호
		String[] szSTL_NO = null;
		// 권상모음순서
		String[] szYD_UP_COLL_SEQ = null;
		// 스케줄코드
		String szYD_SCH_CD = null;
		// 야드스케쥴우선순위
		String szYD_SCH_PRIOR = null;
		// 스케줄 금지 유무
		String szYD_SCH_PROH_EXN = null;
		// 작업크레인
		String szYD_WRK_CRN = null;
		// 작업크레인스케쥴우선순위
		String szYD_WRK_CRN_PRIOR = null;
		// 대체크레인유무
		String szYD_ALT_CRN_YN = null;
		// 대체크레인스케쥴우선순위
		String szYD_ALT_CRN_PRIOR = null;
		// 대체크레인
		String szYD_ALT_CRN = null;
		// 선택크레인
		String szCrn = null;
		// 작업예약ID
		String szYD_WBOOK_ID = null;

		// TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		// 에러 리턴
		if (szRcvTcCode == null) {

			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error ("
					+ szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;

		}
		// TC CODE DISPLAY
		if (bDebugFlag) {

			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		}

		try {

			// 받은 전문 편집
			// 스케줄코드
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD")
					.trim();
			if (szYD_SCH_CD.equals("")) {

				szMsg = "[전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 야드구분
			szYD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP").trim();
			if (szYD_GP.equals("")) {

				szMsg = "[전문 이상] 야드구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 동구분
			szYD_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP").trim();
			if (szYD_BAY_GP.equals("")) {

				szMsg = "[전문 이상] 동구분이	없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}

			// 목표야드구분
			szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_YD_GP").trim();
			if (szYD_AIM_YD_GP.equals("")) {

				szMsg = "[전문 이상] 목표야드구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 목표동구분
			szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_BAY_GP").trim();
			if (szYD_AIM_BAY_GP.equals("")) {

				szMsg = "[전문 이상] 목표동구분이	없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_LOT_GP_SH");
			if (intMtlCnt == 0) {

				szMsg = "[전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}

			// 작업계획대차
			String szYD_WRK_PLAN_TCAR = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WRK_PLAN_TCAR").trim();

			// 재료번호
			szSTL_NO = new String[intMtlCnt + 1];
			// 권상모음순서
			szYD_UP_COLL_SEQ = new String[intMtlCnt + 1];

			// 재료번호, 권상모음순서
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,
						"STL_NO" + Loop_i);
				if (szSTL_NO[Loop_i].equals("")) {

					szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				}
				// 권상모음순서
//				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,
//						"YD_UP_COLL_SEQ" + Loop_i);
//				if (szYD_UP_COLL_SEQ[Loop_i].equals("")) {
//
//					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i]
//							+ ")에 대한 권상모음순서가 없습니다.";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//					return;
//
//				}
			}

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal)
				return;

			// 레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();

			// 스케줄CD 체크
			// 스케줄 금지 유무
			szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara,
					"YD_SCH_PROH_EXN").trim();
			// 작업크레인
			szYD_WRK_CRN = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN")
					.trim();
			// 작업크레인우선순위
			szYD_WRK_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara,
					"YD_WRK_CRN_PRIOR").trim();
			// 대체크레인유무
			szYD_ALT_CRN_YN = ydDaoUtils.paraRecChkNull(recPara,
					"YD_ALT_CRN_YN").trim();
			// 대체크레인
			szYD_ALT_CRN = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN")
					.trim();
			// 대체크레인우선순위
			szYD_ALT_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara,
					"YD_ALT_CRN_PRIOR").trim();

			// 스케줄 금지 유무가 "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if (szYD_SCH_PROH_EXN.equals("Y")) {

				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}

			// 작업크레인 설비 상태 체크
			blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);

			// 작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if (!blnRtnVal) {

				szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				// 대체크레인의 유무를 체크한다.
				// 대체크레인이 없으면 에러 리턴
				if (!szYD_ALT_CRN_YN.equals("Y")) {

					szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				}
				// 대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				// 대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {

					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;

				} else {
					// 대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					szCrn = szYD_ALT_CRN;
					szYD_SCH_PRIOR = szYD_ALT_CRN_PRIOR;
				}
			} else {
				// 작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				szCrn = szYD_WRK_CRN;
				szYD_SCH_PRIOR = szYD_WRK_CRN_PRIOR;
			}

			// 재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다.
			// 작업예약재료 등록 여부를 체크한다.
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 크레인사양과 저장품 사양을 체크(길이,폭,중량)
				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
				if (!blnRtnVal)
					return;

				// 다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if (!blnRtnVal)
					return;

			}

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if (!blnRtnVal)
				return;
			// 레코드추출
			rsResult.first();
			recPara = rsResult.getRecord();
			// 작업예약ID
			szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");

			// INSERT 항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			

			// INSERT할 항목 SET
			/*
			 * recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			 * recPara.setField("YD_GP", szYD_GP); recPara.setField("YD_BAY_GP",
			 * szYD_BAY_GP); recPara.setField("YD_SCH_CD", szYD_SCH_CD);
			 * recPara.setField("REGISTER", szUser);
			 */

			// INSERT할 항목 SET
			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID); 			// 작업예약ID
			recPara.setField("YD_GP", szYD_GP); 						// 야드구분
			recPara.setField("YD_BAY_GP", szYD_BAY_GP); 				// 야드동구분
			recPara.setField("YD_SCH_CD", szYD_SCH_CD); 				// 야드스케쥴코드
			recPara.setField("YD_SCH_PRIOR", szYD_SCH_PRIOR); 			// 야드스케쥴우선순위
			recPara.setField("YD_AIM_YD_GP", szYD_AIM_YD_GP); 			// 야드목표야드구분
			recPara.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP); 		// 야드목표동구분
			recPara.setField("YD_WRK_PLAN_TCAR", szYD_WRK_PLAN_TCAR); 	// 작업계획대차
			// 추후 추가항목
			// 야드스케쥴진행상태
			// 야드스케쥴기동구분
			// 야드스케쥴요청구분
			recPara.setField("REGISTER", szUser);

			// 작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}

			// 조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();

			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			// recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			// recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", szUser);

			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 리턴 recordSet 생성
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				// 재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				blnRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C",
						rsResult);
				if (!blnRtnVal) {
					szMsg = "재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져오는 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					// 예외를 발생시켜 롤백 시킴
					throw new DAOException(szSessionName + " : " + szMethodName
							+ " - " + szMsg);
					// return;
				}

				// 레코드추출
				rsResult.first();
				recStkPara = rsResult.getRecord();

				// 재료번호
				recPara.setField("STL_NO", szSTL_NO[Loop_i]);
				// 적치열구분
				recPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(
						recStkPara, "YD_STK_COL_GP"));
				// 적치BED번호
				recPara.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(
						recStkPara, "YD_STK_BED_NO"));
				// 적치단번호
				recPara.setField("YD_STK_LYR_NO", ydDaoUtils.paraRecChkNull(
						recStkPara, "YD_STK_LYR_NO"));
				// 권상모음순서
				recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ[Loop_i]);

				// 작업예약재료 테이블에 등록한다.
				intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);

				if (intRtnVal < 1) {
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					// 예외를 발생시켜 롤백 시킴
					throw new DAOException(szSessionName + " : " + szMethodName
							+ " - " + szMsg);
					// return;
				}

			}
			
			if( szYD_WRK_PLAN_TCAR.equals("")) {
				szMsg = " 동내이적 처리 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				recPara.setField("JMS_TC_CD", "YDYDJ500");
				recPara.setField("YD_SCH_CD", szYD_SCH_CD);
				recPara.setField("YD_EQP_ID", szCrn);
			}else{
				//대차스케쥴이 존재하지 않는 경우 대차공대차 상차스케쥴 호출 필요
				szMsg = " 동간이적 처리 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//동간 이적 시는 대차상차스케쥴 호출
				recPara.setField("JMS_TC_CD", "YDYDJ520");
				recPara.setField("YD_EQP_ID", szYD_WRK_PLAN_TCAR);
			}
			ydDelegate.sendMsg(recPara);

		} catch (Exception e) {
			szMsg = "대상재 상단 더미재 이적작업요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}

	} // end of procMvDummyMtlAboveTgMtlWrkReq()
	
	/**
	 * 오퍼레이션명 : 해당 적치열,적치BED,적치단상단의 데이터 유무체크 및 데이터 반환
	 * @param szYdStkColGp 적치열
	 * @param szYdStkBedNo 적치BED
	 * @param szYdStkLyrNo 적치단
	 * @param rsResult	레코드셋
	 * @return int
	 * @throws JDTOException
	 */
	public int chkGetTgMtlAboveStkLyrLot(String szYdStkColGp, String szYdStkBedNo, String szYdStkLyrNo, JDTORecordSet rsResult) throws JDTOException {

		// 저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메소드명
		String szMethodName = "chkGetTgMtlAboveStkLyrLot";
		String szMsg = null;

		// 레코드 선언
		JDTORecord recPara = null;

		try {
			// 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();

			// 적치열구분
			recPara.setField("YD_STK_COL_GP", szYdStkColGp);
			// 적치 BED
			recPara.setField("YD_STK_BED_NO", szYdStkBedNo);
			// 적치단
			recPara.setField("YD_STK_LYR_NO", szYdStkLyrNo);
			// 저장품 테이블 조회
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 49);

			// 리턴값 메세지처리
			if (intRtnVal >= 1) {

				blnRtnVal = true;

			} else if (intRtnVal == 0) {
				szMsg = " 적치열( " + szYdStkColGp + ")의 적치BED(" + szYdStkBedNo + ") 적치단( " + szYdStkLyrNo + ")상단의  대상재  데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == -2) {

				szMsg = " 적치열( " + szYdStkColGp + ")의 적치BED(" + szYdStkBedNo + ") 적치단( " + szYdStkLyrNo + ")상단의 대상재  데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = " 적치열( " + szYdStkColGp + ")의 적치BED(" + szYdStkBedNo + ") 적치단( " + szYdStkLyrNo + ")상단의 대상재  데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch (JDTOException e) {
			szMsg = " 적치열( " + szYdStkColGp + ")의 적치BED(" + szYdStkBedNo + ") 적치단( " + szYdStkLyrNo + ")상단의 대상재 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: "
					+ e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
			throw e;
		}
		return intRtnVal;
	} // end of chkGetTgMtlAboveStkLyrLot
	
	/**
	 * 오퍼레이션명 : 해당스판, 목표행선에 대한 대상재의 적치열,BED,최상단, 중량합 데이터 유무체크 및 데이터 반환
	 * @param szYdGp		야드구분
	 * @param szYdBayGp		동구분
	 * @param szYdSpanNo	스판
	 * @param szYdAimRtGp	목표행선구분
	 * @param rsResult
	 * @return
	 * @throws JDTOException
	 */
	
	public boolean chkGetTgMtlLot(String szYdGp, String szYdBayGp, String szYdSpanNo, String szYdAimRtGp, JDTORecordSet rsResult) throws JDTOException {

		// 저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메소드명
		String szMethodName = "chkGetTgMtlLot";
		String szMsg = null;

		// 레코드 선언
		JDTORecord recPara = null;

		try {
			// 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();

			// 적치열구분
			recPara.setField("YD_STK_COL_GP", szYdGp + szYdBayGp + szYdSpanNo);
			// 목표행선구분
			recPara.setField("YD_AIM_RT_GP", szYdAimRtGp);
			// 저장품 테이블 조회
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 48);

			// 리턴값 메세지처리
			if (intRtnVal >= 1) {

				blnRtnVal = true;

			} else if (intRtnVal == 0) {

				szMsg = szYdGp + " 야드 " + szYdBayGp + "동 " + szYdSpanNo + "스판 " + " 목표행선이 "
						+ szYdAimRtGp + "" + "에 대한 대상재  데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == -2) {

				szMsg = szYdGp + " 야드 " + szYdBayGp + "동 " + szYdSpanNo + "스판 " + " 목표행선이 "
				+ szYdAimRtGp + "" + "에 대한 대상재  데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = szYdGp + " 야드 " + szYdBayGp + "동 " + szYdSpanNo + "스판 " + " 목표행선이 "
				+ szYdAimRtGp + "" + "에 대한 대상재  데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch (Exception e) {
			szMsg = szYdGp + " 야드 " + szYdBayGp + "동 " + szYdSpanNo + "스판 " + " 목표행선이 "
			+ szYdAimRtGp + "" + "에 대한 대상재 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: "
					+ e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} // end of chkGetTgMtlLot
	
	
	
	/**
	 * 오퍼레이션명 : 대상재 상단 더미재 이적Lot편성 데이터 유무체크 및 데이터 반환 - 임춘수추가 2009.06.25
	 * @param szYdGp		야드구분
	 * @param szYdBayGp		동구분
	 * @param szYdSpanNo	스판
	 * @param szYdAimRtGp	목표행선
	 * @param lngYD_WRK_ALW_WT 차량의 허용중량
	 * @param rsResult
	 * @return
	 * @throws JDTOException
	 */
	public int chkGetMvDummyMtlAboveTgMtlLot(String[] arrYD_STK_COL_GP, String[] arrYD_STK_BED_NO, String szYD_AIM_RT_GP, JDTORecordSet rsResult) throws JDTOException {

		// 저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메소드명
		String szMethodName = "chkGetMvDummyMtlAboveTgMtlLot";
		String szMsg = null;
		
		//적치열구분
		String szYD_STK_COL_GP = null;
		String szPRE_YD_STK_COL_GP = "";
		//적치 BED
		String szYD_STK_BED_NO = null;
		String szPRE_YD_STK_BED_NO = "";
		//적치단
		String szYD_STK_LYR_NO = null;
		//재료중량 합
		long lngYD_MTL_WT = 0;
		
		// 레코드 선언
		JDTORecord recPara = null;
		JDTORecord recTgMtl = null;
		
		// 레코드셋 선언
		JDTORecordSet rsDummy = null;

		try {
			JDTORecordSet rsTgMtl = JDTORecordFactory.getInstance().createRecordSet("");
			
			intRtnVal = chkPrepLotGp(arrYD_STK_COL_GP, arrYD_STK_BED_NO, szYD_AIM_RT_GP, rsTgMtl);
			
			//if( !blnRtnVal ) return blnRtnVal;
			
			// 레코드 생성
//			recPara = JDTORecordFactory.getInstance().create();
//
//			// 적치열구분
//			recPara.setField("YD_STK_COL_GP", szYdGp + szYdBayGp + szYdSpanNo);
//			// 목표행선구분
//			recPara.setField("YD_AIM_RT_GP", szYdAimRtGp);
//			// 저장품 테이블 조회
//			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 21);
			//recPara = JDTORecordFactory.getInstance().create();
			for( int Loop_i = 1; Loop_i <= rsTgMtl.size(); Loop_i++ ) {
				rsTgMtl.absolute(Loop_i);
				recTgMtl = rsTgMtl.getRecord();
				szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recTgMtl, "YD_STK_COL_GP");				//적치열
				szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recTgMtl, "YD_STK_BED_NO");				//적치 BED
				if( szPRE_YD_STK_COL_GP.equals(szYD_STK_COL_GP) && szPRE_YD_STK_BED_NO.equals(szYD_STK_BED_NO) ) {
					continue;
				}else{
					szPRE_YD_STK_COL_GP = szYD_STK_COL_GP;
					szPRE_YD_STK_BED_NO = szYD_STK_BED_NO;
					szYD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(recTgMtl, "YD_STK_LYR_NO");				//적치단
				}
				rsDummy = JDTORecordFactory.getInstance().createRecordSet("");
//				recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
//				recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
//				recPara.setField("YD_STK_LYR_NO", szYD_STK_LYR_NO);
				//더미재 검색
				intRtnVal = chkGetTgMtlAboveStkLyrLot(szYD_STK_COL_GP, szYD_STK_BED_NO, szYD_STK_LYR_NO, rsDummy);
				// 리턴값 메세지처리
				if (intRtnVal >= 1) {

					blnRtnVal = true;

				} else if (intRtnVal == 0) {

					szMsg = "적치열[] 베드[] 단[] 상단에 더미재가 존재하지 않습니다. ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					continue;
				} else if (intRtnVal == -2) {

					szMsg = "적치열[] 베드[] 단[] 상단에 대한 더미 대상재  데이터 조회중 parameter error 발생!";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return intRtnVal;
				} else {
					szMsg = "적치열[] 베드[] 단[] 상단 더미 대상재  데이터 조회중 오류 발생!";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return intRtnVal;

				}
				rsResult.addAll(rsDummy);
				
			}

			intRtnVal = rsResult.size();
		} catch (Exception e) {
			szMsg = "대상재 상단 더미재 이적Lot편성 데이터 유무체크 및 데이터 반환 : 에러메세지 - "
					+ e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			intRtnVal = -100;
		}
		return intRtnVal;
	} // end of chkGetMvDummyMtlAboveTgMtlLot
	
	/**
	 * 오퍼레이션명 : 대상재 상단 더미재 이적Lot편성 데이터 유무체크 및 데이터 반환 - 임춘수추가 2009.06.25
	 * @param szYdGp		야드구분
	 * @param szYdBayGp		동구분
	 * @param szYdSpanNo	스판
	 * @param szYdAimRtGp	목표행선
	 * @param lngYD_WRK_ALW_WT 차량의 허용중량
	 * @param rsResult
	 * @return
	 * @throws JDTOException
	 */
	public int chkGetMvDummyMtlAboveTgMtlLot(String[] arrYD_STK_COL_GP, String[] arrYD_STK_BED_NO, String szYD_AIM_RT_GP, String szARR_WLOC_CD, JDTORecordSet rsResult) throws JDTOException {

		// 저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메소드명
		String szMethodName = "chkGetMvDummyMtlAboveTgMtlLot";
		String szMsg = null;
		
		//적치열구분
		String szYD_STK_COL_GP = null;
		String szPRE_YD_STK_COL_GP = "";
		//적치 BED
		String szYD_STK_BED_NO = null;
		String szPRE_YD_STK_BED_NO = "";
		//적치단
		String szYD_STK_LYR_NO = null;
		//재료중량 합
		long lngYD_MTL_WT = 0;
		
		// 레코드 선언
		JDTORecord recPara = null;
		JDTORecord recTgMtl = null;
		
		// 레코드셋 선언
		JDTORecordSet rsDummy = null;

		try {
			JDTORecordSet rsTgMtl = JDTORecordFactory.getInstance().createRecordSet("");
			
			intRtnVal = chkPrepLotGp(arrYD_STK_COL_GP, arrYD_STK_BED_NO, szYD_AIM_RT_GP, szARR_WLOC_CD, rsTgMtl);
			
			//if( !blnRtnVal ) return blnRtnVal;
			
			// 레코드 생성
//			recPara = JDTORecordFactory.getInstance().create();
//
//			// 적치열구분
//			recPara.setField("YD_STK_COL_GP", szYdGp + szYdBayGp + szYdSpanNo);
//			// 목표행선구분
//			recPara.setField("YD_AIM_RT_GP", szYdAimRtGp);
//			// 저장품 테이블 조회
//			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 21);
			//recPara = JDTORecordFactory.getInstance().create();
			for( int Loop_i = 1; Loop_i <= rsTgMtl.size(); Loop_i++ ) {
				rsTgMtl.absolute(Loop_i);
				recTgMtl = rsTgMtl.getRecord();
				szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recTgMtl, "YD_STK_COL_GP");				//적치열
				szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recTgMtl, "YD_STK_BED_NO");				//적치 BED
				if( szPRE_YD_STK_COL_GP.equals(szYD_STK_COL_GP) && szPRE_YD_STK_BED_NO.equals(szYD_STK_BED_NO) ) {
					continue;
				}else{
					szPRE_YD_STK_COL_GP = szYD_STK_COL_GP;
					szPRE_YD_STK_BED_NO = szYD_STK_BED_NO;
					szYD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(recTgMtl, "YD_STK_LYR_NO");				//적치단
				}
				rsDummy = JDTORecordFactory.getInstance().createRecordSet("");
//				recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
//				recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
//				recPara.setField("YD_STK_LYR_NO", szYD_STK_LYR_NO);
				//더미재 검색
				intRtnVal = chkGetTgMtlAboveStkLyrLot(szYD_STK_COL_GP, szYD_STK_BED_NO, szYD_STK_LYR_NO, rsDummy);
				// 리턴값 메세지처리
				if (intRtnVal >= 1) {

					blnRtnVal = true;

				} else if (intRtnVal == 0) {

					szMsg = "적치열[] 베드[] 단[] 상단에 더미재가 존재하지 않습니다. ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					continue;
				} else if (intRtnVal == -2) {

					szMsg = "적치열[] 베드[] 단[] 상단에 대한 더미 대상재  데이터 조회중 parameter error 발생!";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return intRtnVal;
				} else {
					szMsg = "적치열[] 베드[] 단[] 상단 더미 대상재  데이터 조회중 오류 발생!";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return intRtnVal;

				}
				rsResult.addAll(rsDummy);
				
			}

			intRtnVal = rsResult.size();
		} catch (Exception e) {
			szMsg = "대상재 상단 더미재 이적Lot편성 데이터 유무체크 및 데이터 반환 : 에러메세지 - "
					+ e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			intRtnVal = -100;
		}
		return intRtnVal;
	} // end of chkGetMvDummyMtlAboveTgMtlLot
	
	/**
	 * 오퍼레이션명 : 대상재 상단 더미재 이적Lot편성 데이터 유무체크 및 데이터 반환 - 이송대기준비 작업시 차량 중량별로 더미대상재 선택
	 * @param szYdGp		야드구분
	 * @param szYdBayGp		동구분
	 * @param szYdSpanNo	스판
	 * @param szYdAimRtGp	목표행선
	 * @param lngYD_WRK_ALW_WT 차량의 허용중량
	 * @param rsResult
	 * @return
	 * @throws JDTOException
	 */
	public boolean chkGetMvDummyMtlAboveTgMtlLot(String szYdGp, String szYdBayGp, String szYdSpanNo, String szYdAimRtGp, long lngYD_WRK_ALW_WT, JDTORecordSet rsResult) throws JDTOException {

		// 저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메소드명
		String szMethodName = "chkGetMvDummyMtlAboveTgMtlLot";
		String szMsg = null;
		
		//적치열구분
		String szYD_STK_COL_GP = null;
		//적치 BED
		String szYD_STK_BED_NO = null;
		//적치단
		String szYD_STK_LYR_NO = null;
		//재료중량 합
		long lngYD_MTL_WT = 0;
		
		// 레코드 선언
		JDTORecord recPara = null;
		JDTORecord recTgMtl = null;
		
		// 레코드셋 선언
		JDTORecordSet rsDummy = null;

		try {
			JDTORecordSet rsTgMtl = JDTORecordFactory.getInstance().createRecordSet("");
			
			blnRtnVal = chkGetTgMtlLot(szYdGp, szYdBayGp, szYdSpanNo, szYdAimRtGp, rsTgMtl);
			
			if( !blnRtnVal ) return blnRtnVal;
			
			// 레코드 생성
//			recPara = JDTORecordFactory.getInstance().create();
//
//			// 적치열구분
//			recPara.setField("YD_STK_COL_GP", szYdGp + szYdBayGp + szYdSpanNo);
//			// 목표행선구분
//			recPara.setField("YD_AIM_RT_GP", szYdAimRtGp);
//			// 저장품 테이블 조회
//			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 21);
			//recPara = JDTORecordFactory.getInstance().create();
			for( int Loop_i = 1; Loop_i <= rsTgMtl.size(); Loop_i++ ) {
				rsTgMtl.absolute(Loop_i);
				recTgMtl = rsTgMtl.getRecord();
				szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recTgMtl, "YD_STK_COL_GP");				//적치열
				szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recTgMtl, "YD_STK_BED_NO");				//적치 BED
				szYD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(recTgMtl, "YD_STK_LYR_NO");				//적치단
				lngYD_MTL_WT = lngYD_MTL_WT + ydDaoUtils.paraRecChkNullLong(recTgMtl, "YD_MTL_WT");				//적치재료의 중량 합
				rsDummy = JDTORecordFactory.getInstance().createRecordSet("");
//				recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
//				recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
//				recPara.setField("YD_STK_LYR_NO", szYD_STK_LYR_NO);
				//더미재 검색
				intRtnVal = chkGetTgMtlAboveStkLyrLot(szYD_STK_COL_GP, szYD_STK_BED_NO, szYD_STK_LYR_NO, rsDummy);
				//if( intRtnVal < 0 ) return false;
				rsResult.addAll(rsDummy);
				//차량의 허용중량보다 클 경우 루프 종료 [중량값이 -1인 경우 중량제한없음]
				if( lngYD_WRK_ALW_WT > -1 ) {
					if( lngYD_MTL_WT > lngYD_WRK_ALW_WT ) break;
				}
			}

			// 리턴값 메세지처리
			if (intRtnVal >= 1) {

				blnRtnVal = true;

			} else if (intRtnVal == 0) {

				szMsg = szYdGp + " 야드 " + szYdBayGp + "동 " + szYdSpanNo + "스판 " + " 목표행선이 "
						+ szYdAimRtGp + "" + "에 대한 대상재  데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == -2) {

				szMsg = szYdGp + " 야드 " + szYdBayGp + "동 " + szYdSpanNo + "스판 " + " 목표행선이 "
				+ szYdAimRtGp + "" + "에 대한 대상재  데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = szYdGp + " 야드 " + szYdBayGp + "동 " + szYdSpanNo + "스판 " + " 목표행선이 "
				+ szYdAimRtGp + "" + "에 대한 대상재  데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch (Exception e) {
			szMsg = szYdGp + " 야드 " + szYdBayGp + "동 " + szYdSpanNo + "스판 " + " 목표행선이 "
			+ szYdAimRtGp + "" + "에 대한 대상재 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: "
					+ e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} // end of chkGetMvDummyMtlAboveTgMtlLot

	/**
	 * 오퍼레이션명 : C연주정리Lot편성 데이터 유무체크 및 데이터 반환
	 * 
	 * @param String
	 *            szYdStkColGp 적치열구분 String szYdStkBedNo 적치Bed번호 JDTORecordSet
	 *            rsResult 결과레코드셋
	 * @return boolean true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetCCcReadjLotGp(String szYdStkColGp,
			String szYdStkBedNo, JDTORecordSet rsResult) throws JDTOException {

		// 저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메소드명
		String szMethodName = "chkGetCCcReadjLotGp";
		String szMsg = null;

		// 레코드 선언
		JDTORecord recPara = null;

		try {
			// 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();

			// 적치열구분
			recPara.setField("YD_STK_COL_GP", szYdStkColGp);
			// 적치Bed번호
			recPara.setField("YD_STK_BED_NO", szYdStkBedNo);

			// 저장품 테이블 조회
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 21);

			// 리턴값 메세지처리
			if (intRtnVal >= 1) {

				blnRtnVal = true;

			} else if (intRtnVal == 0) {

				szMsg = "적치열구분(" + szYdStkColGp + ")," + "적치Bed번호("
						+ szYdStkBedNo + ")" + "에 대한 C연주 정리 Lot 편성 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == -2) {

				szMsg = "적치열구분(" + szYdStkColGp + ")," + "적치Bed번호("
						+ szYdStkBedNo + ")"
						+ "에 대한 C연주 정리 Lot 편성  데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = "적치열구분(" + szYdStkColGp + ")," + "적치Bed번호("
						+ szYdStkBedNo + ")"
						+ "에 대한 C연주 정리 Lot 편성  데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch (Exception e) {
			szMsg = "C연주 정리 Lot 편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: "
					+ e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} // end of chkGetCCcReadjLotGp

	/**
	 * 오퍼레이션명 : C연주 공Bed확보 Lot 편성 데이터 유무체크 및 데이터 반환
	 * 
	 * @param String
	 *            szYdStkColGp 적치열구분 String szYdStkBedNo 적치Bed번호 JDTORecordSet
	 *            rsResult 결과레코드셋
	 * @return boolean true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetCCcEmptyBedSecurLotGp(String szYdStkColGp,
			String szYdStkBedNo, JDTORecordSet rsResult) throws JDTOException {

		// 저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메소드명
		String szMethodName = "chkGetCCcEmptyBedSecurLotGp";
		String szMsg = null;

		// 레코드 선언
		JDTORecord recPara = null;

		try {
			// 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();

			// 적치열구분
			// recPara.setField("YD_STK_COL_GP", szYdStkColGp);
			recPara.setField("YD_STK_COL_GP", szYdStkColGp.substring(0, 4));
			// 적치Bed번호
			// recPara.setField("YD_STK_BED_NO", szYdStkBedNo);

			// 저장품 테이블 조회
			// intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 21);
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 40);

			// 리턴값 메세지처리
			if (intRtnVal >= 1) {

				blnRtnVal = true;

			} else if (intRtnVal == 0) {

				szMsg = "적치열구분(" + szYdStkColGp + ")," + "적치Bed번호("
						+ szYdStkBedNo + ")"
						+ "에 대한 C연주 공Bed확보 Lot 편성 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == -2) {

				szMsg = "적치열구분(" + szYdStkColGp + ")," + "적치Bed번호("
						+ szYdStkBedNo + ")"
						+ "에 대한 C연주 공Bed확보 Lot 편성  데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = "적치열구분(" + szYdStkColGp + ")," + "적치Bed번호("
						+ szYdStkBedNo + ")"
						+ "에 대한 C연주 공Bed확보 Lot 편성  데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch (Exception e) {
			szMsg = "C연주 공Bed확보 Lot 편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: "
					+ e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} // end of chkGetCCcEmptyBedSecurLotGp

	/**
	 * 오퍼레이션명 : 후판창고 선별작업 Lot 편성 데이터 유무체크 및 데이터 반환
	 * 
	 * @param String
	 *            szYdStkColGp 적치열구분 String szYdStkBedNo 적치Bed번호 String
	 *            szRefurChgLotNo 가열로장입LotNo JDTORecordSet rsResult 결과레코드셋
	 * @return boolean true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetPlWhSelWrkLotGp(String szYdStrGtrCd,
			String szYdStkLotCd, JDTORecordSet rsResult) throws JDTOException {

		// 저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메소드명
		String szMethodName = "chkGetPlWhSelWrkLotGp";
		String szMsg = null;

		// 레코드 선언
		JDTORecord recPara = null;

		try {
			// 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();

			// 행선구분
			recPara.setField("YD_ROUTE_GP", "KE");
			// 목표행선구분
			recPara.setField("YD_AIM_RT_GP", "KE");
			// 저장집합코드
			recPara.setField("YD_STR_GTR_CD", szYdStrGtrCd);
			// 산적Lot코드
			recPara.setField("YD_STK_LOT_CD", szYdStkLotCd);

			// 저장품 테이블 조회
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 23);

			// 리턴값 메세지처리
			if (intRtnVal > 1) {

				blnRtnVal = true;

			} else if (intRtnVal == 1) {

				szMsg = "저장집합코드(" + szYdStrGtrCd + ")," + "산적Lot코드("
						+ szYdStkLotCd + ") "
						+ "에 대한 후판창고 선별작업 Lot 편성 대상 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == 0) {

				szMsg = "저장집합코드(" + szYdStrGtrCd + ")," + "산적Lot코드("
						+ szYdStkLotCd + ") "
						+ "에 대한 후판창고 선별작업 Lot 편성 조회 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == -2) {

				szMsg = "저장집합코드(" + szYdStrGtrCd + ")," + "산적Lot코드("
						+ szYdStkLotCd + ") "
						+ "에 대한 후판창고 선별작업 Lot 편성  데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = "저장집합코드(" + szYdStrGtrCd + ")," + "산적Lot코드("
						+ szYdStkLotCd + ") "
						+ "에 대한 후판창고 선별작업 Lot 편성  데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch (Exception e) {
			szMsg = "후판창고 선별작업 Lot 편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: "
					+ e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} // end of chkGetPlWhSelWrkLotGp

	/**
	 * 오퍼레이션명 : A후판 정리 Lot 편성 데이터 유무체크 및 데이터 반환
	 * 
	 * @param String
	 *            szYdStkColGp 적치열구분 String szYdStkBedNo 적치Bed번호 JDTORecordSet
	 *            rsResult 결과레코드셋
	 * @return boolean true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetAPlReadjLotGp(String szYdStkColGp,
			String szYdStkBedNo, JDTORecordSet rsResult) throws JDTOException {

		// 저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메소드명
		String szMethodName = "chkGetAPlReadjLotGp";
		String szMsg = null;

		// 레코드 선언
		JDTORecord recPara = null;

		try {
			// 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();

			// 적치열구분
			recPara.setField("YD_STK_COL_GP", szYdStkColGp);
			// 적치Bed번호
			recPara.setField("YD_STK_BED_NO", szYdStkBedNo);

			// 저장품 테이블 조회
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 21);

			// 리턴값 메세지처리
			if (intRtnVal >= 1) {

				blnRtnVal = true;

			} else if (intRtnVal == 0) {

				szMsg = "적치열구분(" + szYdStkColGp + ")," + "적치Bed번호("
						+ szYdStkBedNo + ")" + "에 대한 A후판 정리 Lot 편성 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == -2) {

				szMsg = "적치열구분(" + szYdStkColGp + ")," + "적치Bed번호("
						+ szYdStkBedNo + ")"
						+ "에 대한 A후판 정리 Lot 편성  데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = "적치열구분(" + szYdStkColGp + ")," + "적치Bed번호("
						+ szYdStkBedNo + ")"
						+ "에 대한 A후판 정리 Lot 편성  데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch (Exception e) {
			szMsg = "A후판 정리 Lot 편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: "
					+ e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} // end of chkGetAPlReadjLotGp

	/**
	 * 오퍼레이션명 : 크레인작업재료 유무 체크
	 * 
	 * @param String
	 *            szStlNo 설비ID
	 * @return boolean true(작업재료없음), false(작업재료있음)
	 * @throws JDTOException
	 */
	public boolean chkCrnWrkMtl(String szStlNo) throws JDTOException {

		// 크레인 작업 재료
		YdCrnWrkMtlDao ydCrnWrkMtlDao = new YdCrnWrkMtlDao();
		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메소드명
		String szMethodName = "chkCrnWrkMtl";
		String szMsg = null;

		// 레코드 선언
		JDTORecord recPara = null;
		JDTORecordSet rsResult = null;

		try {

			// 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();
			// 레코드셋 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			// 재료번호
			recPara.setField("STL_NO", szStlNo);

			// 설비 테이블 조회
			intRtnVal = ydCrnWrkMtlDao.getYdCrnwrkmtl(recPara, rsResult, 2);

			// 리턴값 메세지처리
			if (intRtnVal >= 1) {

				szMsg = "재료번호(" + szStlNo
						+ ")의 소재(제품)이 이미 크레인스케줄 작업 재료에 등록되어 있습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == 0) {

				blnRtnVal = true;

			} else if (intRtnVal == -2) {

				szMsg = "재료번호(" + szStlNo
						+ ")에 대한 크레인스케줄 작업 재료 조회중 parameter error 발생.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = "재료번호(" + szStlNo + ")에 대한 크레인스케줄 작업 재료 조회중 오류 발생.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch (Exception e) {
			szMsg = "크레인작업재료 유무 체크 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return blnRtnVal = false;
		}
		return blnRtnVal;
	} // end of chkCrnWrkMtl

	/**
	 * 오퍼레이션명 : A후판 공Bed확보 Lot 편성 데이터 유무체크 및 데이터 반환
	 * 
	 * @param String
	 *            szYdStkColGp 적치열구분 String szYdStkBedNo 적치Bed번호 JDTORecordSet
	 *            rsResult 결과레코드셋
	 * @return boolean true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetAPlEmptyBedSecurLotGp(String szYdStkColGp,
			String szYdStkBedNo, JDTORecordSet rsResult) throws JDTOException {

		// 저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메소드명
		String szMethodName = "chkGetAPlEmptyBedSecurLotGp";
		String szMsg = null;

		// 레코드 선언
		JDTORecord recPara = null;

		try {
			// 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();

			// 적치열구분
			recPara.setField("YD_STK_COL_GP", szYdStkColGp);
			// 적치Bed번호
			recPara.setField("YD_STK_BED_NO", szYdStkBedNo);

			// 저장품 테이블 조회
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 21);

			// 리턴값 메세지처리
			if (intRtnVal >= 1) {

				blnRtnVal = true;

			} else if (intRtnVal == 0) {

				szMsg = "적치열구분(" + szYdStkColGp + ")," + "적치Bed번호("
						+ szYdStkBedNo + ")"
						+ "에 대한 A후판 공Bed확보 Lot 편성 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == -2) {

				szMsg = "적치열구분(" + szYdStkColGp + ")," + "적치Bed번호("
						+ szYdStkBedNo + ")"
						+ "에 대한 A후판 공Bed확보 Lot 편성  데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = "적치열구분(" + szYdStkColGp + ")," + "적치Bed번호("
						+ szYdStkBedNo + ")"
						+ "에 대한 A후판 공Bed확보 Lot 편성  데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch (Exception e) {
			szMsg = "A후판 공Bed확보 Lot 편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: "
					+ e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} // end of chkGetAPlEmptyBedSecurLotGp

	/**
	 * 오퍼레이션명 : 크레인작업가능사양과 재료사양을 체크
	 * 
	 * @param String
	 *            szStlNo 재료번호 String szEqpId 크레인 설비ID
	 * @return boolean true(크레인재료이송가능), false(크레인재료이송불가)
	 * @throws JDTOException
	 */
	public boolean chkCrnSpecMtlSpec(String szStlNo, String szEqpId)
			throws JDTOException {

		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 메세지
		String szMsg = null;
		// 메소드명
		String szMethodName = "chkCrnSpecMtlSpec";
		// 레코드 선언
		JDTORecord recPara = null;
		// 레코드셋 선언
		JDTORecordSet rsResult = null;

		try {
			// 레코드셋 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 저장품 유무 체크
			blnRtnVal = this.chkGetStock(szStlNo, rsResult);
			if (!blnRtnVal)
				return blnRtnVal;

			// 결과 레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			// 폭
			double lngMtlW = ydDaoUtils.paraRecChkNullDouble(recPara, "YD_MTL_W");
			// 길이
			long lngMtlL = ydDaoUtils.paraRecChkNullLong(recPara, "YD_MTL_L");
			// 중량
			long lngMtlWt = ydDaoUtils.paraRecChkNullLong(recPara, "YD_MTL_WT");

			// 레코드셋 재생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 크레인사양 체크 및 조회
			blnRtnVal = this.chkGetCrnSpec(szEqpId, rsResult);
			if (!blnRtnVal)
				return blnRtnVal;

			// 크레인사양 추출
			rsResult.first();
			recPara = rsResult.getRecord();

			// 크레인 작업 능력
			// 작업가능길이
			long lngAbleL = ydDaoUtils.paraRecChkNullLong(recPara,
					"YD_WRK_ABLE_L");
			// 작업가능폭
			double lngAbleW = ydDaoUtils.paraRecChkNullDouble(recPara,
					"YD_WRK_ABLE_W");
			// 작업가능중량
			long lngAbleWt = ydDaoUtils.paraRecChkNullLong(recPara,
					"YD_WRK_ABLE_WT");

			// 크레인 작업가능 길이와 재료의 길이 비교
			if (lngAbleL < lngMtlL) {
				szMsg = "크레인 작업가능 길이(" + lngAbleL + ") 보다 재료의 길이(" + lngMtlL
						+ ")가 더 큽니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return blnRtnVal = false;
			}

			// 크레인 작업가능 폭과 재료의 폭 비교
			if (lngAbleW < lngMtlW) {
				szMsg = "크레인 작업가능 폭(" + lngAbleW + ")보다 재료의 폭(" + lngMtlW
						+ ")이 더 큽니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return blnRtnVal = false;
			}

			// 크레인 작업가능 폭과 재료의 폭 비교
			if (lngAbleWt < lngMtlWt) {
				szMsg = "크레인 작업가능 중량(" + lngAbleWt + ")보다 재료의 중량(" + lngMtlWt
						+ ")이 더 큽니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return blnRtnVal = false;
			}

		} catch (Exception e) {
			szMsg = "크레인작업가능사양과 재료사양을 체크 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return blnRtnVal = false;
		}
		return blnRtnVal = true;
	} // end of chkCrnSpecMtlSpec

	/**
	 * 오퍼레이션명 : 크레인사양 유무체크 및 조회결과 데이터 반환
	 * 
	 * @param String
	 *            szEqpId 설비ID JDTORecordSet rsResult 결과레코드셋
	 * @return boolean true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetCrnSpec(String szEqpId, JDTORecordSet rsResult)
			throws JDTOException {

		// 크레인사양 DAO
		YdCrnSpecDao ydCrnSpecDao = new YdCrnSpecDao();
		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메소드명
		String szMethodName = "chkGetCrnSpec";
		String szMsg = null;

		// 레코드 선언
		JDTORecord recPara = null;

		try {
			// 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();

			// 크레인 설비ID
			recPara.setField("YD_EQP_ID", szEqpId);

			// 크레인사양 조회
			intRtnVal = ydCrnSpecDao.getYdCrnspec(recPara, rsResult, 0);

			// 리턴값 메세지처리
			if (intRtnVal > 1) {

				szMsg = "크레인설비ID(" + szEqpId + ")에 대한 크레인사양 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == 1) {

				blnRtnVal = true;

			} else if (intRtnVal == 0) {

				szMsg = "크레인설비ID(" + szEqpId + ")에 대한 크레인사양 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == -2) {

				szMsg = "크레인설비ID(" + szEqpId
						+ ")로 크레인사양 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = "크레인설비ID(" + szEqpId + ")로 크레인사양 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch (Exception e) {
			szMsg = "크레인사양 유무체크 및 조회결과 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} // end of chkGetCrnSpec

	/**
	 * 오퍼레이션명 : 저장품유무체크 및 조회결과 데이터 반환
	 * 
	 * @param String
	 *            szStlNo 재료번호 JDTORecordSet rsResult 결과레코드셋
	 * @return boolean true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetStock(String szStlNo, JDTORecordSet rsResult)
			throws JDTOException {

		// 저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메소드명
		String szMethodName = "chkGetStock";
		String szMsg = null;

		// 레코드 선언
		JDTORecord recPara = null;

		try {
			// 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();

			// 재료번호
			recPara.setField("STL_NO", szStlNo);

			// 저장품 테이블 조회
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 0);

			// 리턴값 메세지처리
			if (intRtnVal > 1) {

				szMsg = "재료번호(" + szStlNo + ")에 대한 저장품 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == 1) {

				blnRtnVal = true;

			} else if (intRtnVal == 0) {

				szMsg = "재료번호(" + szStlNo + ")에 대한 저장품 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == -2) {

				szMsg = "재료번호(" + szStlNo + ")로 저장품 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = "재료번호(" + szStlNo + ")로 저장품 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch (Exception e) {
			szMsg = "저장품유무체크 및 조회결과 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} // end of chkGetStock

	/**
	 * 오퍼레이션명 : 설비상태 체크
	 * 
	 * @param String
	 *            szEqpId 설비ID
	 * @return boolean true(설비사용가능), false(설비사용불가)
	 * @throws JDTOException
	 */
	public boolean eqpStatCheck(String szEqpId) throws JDTOException {

		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 메세지
		String szMsg = null;
		// 메소드명
		String szMethodName = "eqpStatCheck";
		// 설비상태
		String szYD_EQP_STAT = null;
		// 레코드 선언
		JDTORecord recPara = null;
		// 레코드셋 선언
		JDTORecordSet rsResult = null;

		try {
			// 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();
			// 레코드셋 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 설비ID를 작업크레인으로 설정
			recPara.setField("YD_EQP_ID", szEqpId);

			// 설비 체크 및 데이터 조회
			blnRtnVal = this.chkGetEqp(szEqpId, rsResult);
			if (!blnRtnVal)
				return blnRtnVal;

			// 레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();

			// 설비상태
			szYD_EQP_STAT = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_STAT");

			// 크레인의 상태가 'T'이면 false 리턴.
			// 상수 수정 [2009.12.03 이현성 ]
			if (szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_BREAK)) {

				szMsg = "설비ID(" + szEqpId + ")의 상태가 고장(" + szYD_EQP_STAT
						+ ") 입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				blnRtnVal = true;

			}
		} catch (Exception e) {
			szMsg = "설비상태 체크 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;

	} // end of eqpStatCheck

	/**
	 * 오퍼레이션명 : 설비 유무체크 및 조회결과 데이터 반환
	 * 
	 * @param String
	 *            szEqpId 설비ID JDTORecordSet rsResult 결과레코드셋
	 * @return boolean true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetEqp(String szEqpId, JDTORecordSet rsResult)
			throws JDTOException {

		// 설비 DAO
		YdEqpDao ydEqpDao = new YdEqpDao();
		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메소드명
		String szMethodName = "chkGetEqp";
		String szMsg = null;

		// 레코드 선언
		JDTORecord recPara = null;

		try {

			// 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();

			// 설비ID
			recPara.setField("YD_EQP_ID", szEqpId);

			// 설비 테이블 조회
			intRtnVal = ydEqpDao.getYdEqp(recPara, rsResult, 0);

			// 리턴값 메세지처리
			if (intRtnVal > 1) {

				szMsg = "설비ID(" + szEqpId + ")에 대한 설비 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == 1) {

				blnRtnVal = true;

			} else if (intRtnVal == 0) {

				szMsg = "설비ID(" + szEqpId + ")에 대한 설비 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == -2) {

				szMsg = "설비ID(" + szEqpId + ")로 설비 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = "설비ID(" + szEqpId + ")로 설비 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch (Exception e) {
			szMsg = "설비 유무체크 및 조회결과 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return blnRtnVal = false;
		}
		return blnRtnVal;
	} // end of chkGetEqp

	/**
	 * 오퍼레이션명 : 재료번호, 적치단재료상태에 대한 적치단 정보 유무체크 및 조회결과 데이터 반환
	 * 
	 * @param String
	 *            szStlNo 재료번호 String szMtlStat 적치단재료상태 JDTORecordSet rsResult
	 *            결과레코드셋
	 * @return boolean true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetStlStkLyr(String szStlNo, String szMtlStat,
			JDTORecordSet rsResult) throws JDTOException {

		// 적치단 DAO
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();

		String szMsg = null;
		String szMethodName = "chkGetStlStkLyr";
		int intRtnVal = 0;
		boolean blnRtnVal = false;
		JDTORecord recPara = null;

		try {

			// 조회 항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();

			// 조회 파라미터 레코드 set
			recPara.setField("STL_NO", szStlNo);
			recPara.setField("YD_STK_LYR_MTL_STAT", szMtlStat);

			// 적치단정보 조회
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsResult, 3);

			// 리턴값 메세지처리
			if (intRtnVal > 1) {

				szMsg = "재료번호(" + szStlNo + ")," + "적치단재료상태(" + szMtlStat
						+ ")," + " 에 대한 적치단 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == 1) {

				blnRtnVal = true;

			} else if (intRtnVal == 0) {
				szMsg = "재료번호(" + szStlNo + ")," + "적치단재료상태(" + szMtlStat
						+ ")," + " 에 대한 적치단 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == -2) {
				szMsg = "재료번호(" + szStlNo + ")," + "적치단재료상태(" + szMtlStat
						+ ")," + "로 적치단 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else {
				szMsg = "재료번호(" + szStlNo + ")," + "적치단재료상태(" + szMtlStat
						+ ")," + " 로 적치단 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch (Exception e) {
			szMsg = "재료번호, 적치단재료상태에 대한 적치단 정보 유무체크 및 조회결과 데이터 반환 중 Error : "
					+ e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} // end of chkGetStlStkLyr

	/**
	 * 오퍼레이션명 : 스케줄기준 체크 및 데이터 반환
	 * 
	 * @param String
	 *            szSchCd 스케줄CD
	 * @return boolean true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetSchRule(String szSchCd, JDTORecordSet rsResult)
			throws JDTOException {

		// 스케줄기준 DAO
		YdSchRuleDao ydSchRuleDao = new YdSchRuleDao();

		String szMsg = null;
		String szMethodName = "chkGetSchRule";
		int intRtnVal = 0;
		boolean blnRtnVal = false;
		JDTORecord recPara = null;

		try {

			// 조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();

			// 스케줄코드
			recPara.setField("YD_SCH_CD", szSchCd);

			// 스케줄코드로 스케줄기준 Table 조회
			intRtnVal = ydSchRuleDao.getYdSchrule(recPara, rsResult, 0);

			// 리턴값 메세지처리
			if (intRtnVal > 1) {

				szMsg = "스케줄코드(" + szSchCd + ")에 대한 스케줄기준 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == 1) {

				blnRtnVal = true;

			} else if (intRtnVal == 0) {

				szMsg = "스케줄코드(" + szSchCd + ")에 대한 스케줄기준 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == -2) {

				szMsg = "스케줄코드(" + szSchCd + ")로 스케줄기준 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = "스케줄코드(" + szSchCd + ")로 스케줄기준 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch (Exception e) {
			szMsg = "스케줄기준 체크 중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return blnRtnVal = false;
		}
		return blnRtnVal = true;

	} // end of chkGetSchRule

	/**
	 * 오퍼레이션명 : 작업예약ID생성
	 * 
	 * @param JDTORecordSet
	 *            rsResult 결과 레코드셋
	 * @return boolean true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean getYdWbookId(JDTORecordSet rsResult) throws JDTOException {

		// 작업예약 DAO
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();

		// 메세지
		String szMsg = null;
		// 메소드명
		String szMethodName = "getYdWbookId";
		// 리턴값(int)
		int intRtnVal = 0;
		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 레코드 선언
		JDTORecord recPara = null;

		try {
			// 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();

			// ================================================
			// 파라미터를 설정하지 않으면 JSPEED에서 에러발생. 추후 수정요
			recPara.setField("YD_WBOOK_ID", "1");
			// ================================================

			// 작업예약 테이블의 시퀀스를 이용해 작업예약ID를 구해온다.
			intRtnVal = ydWrkbookDao.getYdWrkbook(recPara, rsResult, 1);
			// 리턴값 메세지처리
			if (intRtnVal > 1) {

				szMsg = "작업예약ID 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == 1) {

				blnRtnVal = true;

			} else if (intRtnVal == 0) {

				szMsg = "작업예약ID를 구하지 못했습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == -2) {

				szMsg = "작업예약ID 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = "작업예약ID 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch (Exception e) {
			szMsg = "작업예약ID생성 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} // end of getYdWbookId

	/**
	 * 오퍼레이션명 : 작업예약재료 등록여부 체크
	 * 
	 * @param String
	 *            szStlNo 재료번호
	 * @return boolean true(작업예약재료등록가능), false(작업예약재료등록불가)
	 * @throws JDTOException
	 */
	public boolean chkYdWrkBookMtl(String szStlNo) throws JDTOException {

		// 작업예약재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();

		// 메세지
		String szMsg = null;
		// 메소드명
		String szMethodName = "chkYdWrkBookMtl";
		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 레코드 선언
		JDTORecord recPara = null;
		// 레코드셋 선언
		JDTORecordSet rsResult = null;

		try {
			// 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();
			// 레코드셋 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 재료번호
			recPara.setField("STL_NO", szStlNo);

			// 재료번호로 작업예약재료 테이블을 읽어온다.
			intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recPara, rsResult, 2);

			// 리턴값 메세지처리
			if (intRtnVal > 0) {

				szMsg = "재료번호(" + szStlNo + ")에 대한 작업예약재료 데이터가 이미 등록되어 있습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == 0) {

				szMsg = "재료번호(" + szStlNo + ")에 대한 작업예약재료 데이터가 등록 가능합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				blnRtnVal = true;

			} else if (intRtnVal == -2) {

				szMsg = "재료번호(" + szStlNo + ")로 작업예약재료 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = "재료번호(" + szStlNo + ")로 작업예약재료 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch (Exception e) {
			szMsg = "작업예약재료 등록여부 체크 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;

	} // end of chkYdWrkBookMtl

	/**
	 * 오퍼레이션명 : 스케줄 우선순위 추가
	 * 
	 * @param String
	 *            szStlNo 재료번호
	 * @return boolean true(스케줄 우선순위 추가 성공), false(스케줄 우선순위항목 추가 실패)
	 * @throws JDTOException
	 */
	public boolean addYdSchPrior(JDTORecord recMsg) throws JDTOException {
		// 메세지
		String szMsg = null;
		// 메소드명
		String szMethodName = "addYdSchPrior";
		// Return Value
		boolean blnRtnVal = false;

		// DAO 객체 생성
		YdSchRuleDao ydSchRuleDao = new YdSchRuleDao();

		// 레코드 생성
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecord recTemp = JDTORecordFactory.getInstance().create();

		// 레코드셋 생성
		JDTORecordSet rsResult = JDTORecordFactory.getInstance()
				.createRecordSet("");

		try {

			recPara.setField("YD_SCH_CD", recMsg.getField("YD_SCH_CD"));

			ydSchRuleDao.getYdSchrule(recPara, rsResult, 0);

			// WBOOK : YD_SCH_PRIOR <---- SCH_RULE :YD_WRK_CRN_PRIOR
			// Key 조회 이므로 실제적으로 한번만 실행된다.
			rsResult.beforeFirst();
			while (rsResult.next()) {
				recTemp = rsResult.getRecord();
				recMsg.setField("YD_SCH_PRIOR", recTemp
						.getField("YD_WRK_CRN_PRIOR"));
				blnRtnVal = true;
			}

		} catch (Exception e) {
			blnRtnVal = false;
			szMsg = "작업 예약 정보에 야드 스케줄 추가 중 예외메세지 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return blnRtnVal;
		}
		return blnRtnVal;

	} // end of addYdSchPrior
	
	

	// ---------------------------------------------------------------------------
} // end of class MvStkWrkDmdSeEJBBean
