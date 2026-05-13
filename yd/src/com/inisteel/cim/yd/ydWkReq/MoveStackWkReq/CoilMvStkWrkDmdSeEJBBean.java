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
 * @ejb.bean name="CoilMvStkWrkDmdSeEJB" jndi-name="CoilMvStkWrkDmdSeEJB"
 *           type="Stateless" view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class CoilMvStkWrkDmdSeEJBBean extends BaseSessionBean {

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
	 * 오퍼레이션명 : C열연정정보급준비Lot편성
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procCHrShearSupPrepLotComp(JDTORecord msgRecord)
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
		String szMethodName = "procCHrShearSupPrepLotComp";
		// 사용자
		String szUser = "SYSTEM";

		// Lot 편성 재료 매수
		int intLotGpSh = 0;
		// 전문 생성 일시
		String szDate = null;

		// 설비ID
		String szYD_EQP_ID = null;
		// 동구분
		String szYD_BAY_GP = null;
		// 적치열구분
		String szYD_STK_COL_GP = null;
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
			// 적치BED번호
			szYD_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP");
			if (szYD_BAY_GP.equals("")) {

				szMsg = "[전문 이상] 동구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			// 동간이적대상 Lot 편성 대상 재료 Select
			blnRtnVal = chkGetCHrShearSupPrepLotComp(szYD_EQP_ID
					.substring(0, 2).trim(), rsResult, 0);
			if (!blnRtnVal)
				return;

			// 동간이적대상 소재가 없을경우 동내이적 재료 조회
			if (rsResult.size() == 0) {
				// 동내이적대상 Lot 편성 대상 재료 Select
				blnRtnVal = chkGetCHrShearSupPrepLotComp(szYD_EQP_ID.substring(
						0, 2).trim(), rsResult, 1);
				if (!blnRtnVal)
					return;
				// 동간이적대상 소재도 없을 경우 프로그램 종료
				if (rsResult.size() == 0)
					return;

				// =================================================================================
				// BRE 등록 안됨...테스트용 스케줄코드 생성
				// 동내이적스케줄코드 생성
				szYD_SCH_CD = "HGSP02MM";
				// =================================================================================

			} else {

				// =================================================================================
				// BRE 등록 안됨...테스트용 스케줄코드 생성
				// 동간이적스케줄코드생성
				szYD_SCH_CD = "HHCMTCUL";
				// =================================================================================

			}

			// 레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			// JMS TC CODE
			recOutPara.setField("JMS_TC_CD", "YDYDJ277");
			// 레코드 커서 처음으로
			rsResult.first();

			// 레코드 추출(첫 레코드는 비교 기준 데이터임:같은 동에 있는 소재들끼리 Lot편성하기 위해)
			recPara = rsResult.getRecord();
			// 적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara,
					"YD_STK_COL_GP").substring(0, 2);

			// 보급 Lot 편성 재료 매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {

				// 레코드 추출
				recPara = rsResult.getRecord();

				// 동이 다르면 Lot에 편성하지 않는다.
				if (!szYD_STK_COL_GP.trim().equals(
						ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP")
								.substring(0, 2))) {

					// 다음 레코드로
					rsResult.next();
					continue;

				}

				// 재료매수
				intLotGpSh = intLotGpSh + 1;

				// 재료번호
				szSTL_NO = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");

				// 다른 크레인 스케줄에 등록 되어 있는지 체크
				blnRtnVal = chkCrnWrkMtl(szSTL_NO);
				if (!blnRtnVal)
					return;

				// 재료번호
				recOutPara.setField("STL_NO" + intLotGpSh, szSTL_NO);
				// 권상모음순서
				recOutPara.setField("YD_UP_COLL_SEQ" + intLotGpSh, ""
						+ intLotGpSh);

				// 재료매수가 3을 초과하면 편성 중지
				if (intLotGpSh >= 3)
					break;

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
			// 설비ID
			recOutPara.setField("YD_EQP_ID", szYD_EQP_ID);

			// 전문 송신
			ydDelegate.sendMsg(recOutPara);
			szMsg = "C열연정정보급준비Lot편성 후 C열연정정보급준비작업요구 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		} catch (Exception e) {
			szMsg = "C열연정정보급준비Lot편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}

	} // end of procCHrShearSupPrepLotComp

	/**
	 * 오퍼레이션명 : C열연정정보급준비작업요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procCHrShearSupPrepWrkReq(JDTORecord msgRecord)
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
		String szMethodName = "procCHrShearSupPrepWrkReq";
		// 사용자
		String szUser = "SYSTEM";

		// 레코드 선언
		JDTORecord recPara = null;
		JDTORecord recStkPara = null;
		// 레코드셋 선언
		JDTORecordSet rsResult = null;

		// 설비ID(열구분)
		String szYD_EQP_ID = null;

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
			// 설비ID
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if (szYD_EQP_ID.equals("")) {

				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// //적치열구분
			// szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord,
			// "YD_STK_COL_GP");
			// if (szYD_STK_COL_GP.equals("")) {
			//				
			// szMsg = "[전문 이상] 적치열구분이 없습니다.";
			// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			// return;
			//				
			// }
			// //적치BED번호
			// szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord,
			// "YD_STK_BED_NO");
			// if (szYD_STK_BED_NO.equals("")) {
			//				
			// szMsg = "[전문 이상] 적치BED번호가 없습니다.";
			// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			// return;
			//				
			// }
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
			szMsg = "C열연정정보급준비작업요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}

	} // end of procCHrShearSupPrepWrkReq()

	/**
	 * 오퍼레이션명 : C열연소재정리Lot편성
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procCHrMatlAdjLotComp(JDTORecord msgRecord)
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
		// 리턴값(int)l
		int intRtnVal = 0;
		// 메세지
		String szMsg = "";
		// 메소드명
		String szMethodName = "procCHrMatlAdjLotComp";
		// 사용자
		String szUser = "SYSTEM";

		// Lot 편성 재료 매수
		int intLotGpSh = 0;
		// 전문 생성 일시
		String szDate = null;

		// 동구분
		String szYD_BAY_GP = null;
		// 설비ID
		String szYD_EQP_ID = null;
		// 적치열구분
		String szYD_STK_COL_GP = null;
		// 재료번호
		String szSTL_NO = null;
		// 해당열의 최소 기준매수
		int intColMinCnt = 5;
		// 스케줄코드
		String szYD_SCH_CD = null;
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
			// 설비ID
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if (szYD_EQP_ID.equals("")) {

				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 동구분
			szYD_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP");
			if (szYD_BAY_GP.equals("")) {

				szMsg = "[전문 이상] 동구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			// 보급 Lot 편성 대상 재료 Select
			blnRtnVal = chkGetCHrMatlAdjLotComp(szYD_EQP_ID.substring(0, 2)
					.trim(), intColMinCnt, rsResult);
			if (!blnRtnVal)
				return;

			// 레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			// JMS TC CODE
			recOutPara.setField("JMS_TC_CD", "YDYDJ279");

			// 레코드 커서 처음으로
			rsResult.first();

			// 레코드 추출
			recPara = rsResult.getRecord();

			// 기준적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara,
					"YD_STK_COL_GP");

			// =================================================================================
			// BRE 등록 안됨...테스트용 스케줄코드 생성
			// 추후 구현..
			szYD_SCH_CD = "HECM02MM";
			// =================================================================================

			// 보급 Lot 편성 재료 매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {

				// 레코드 추출
				recPara = rsResult.getRecord();

				// 같은 적치열의 재료만 Lot에 편성(적치열구분이 다르면 루프 탈출)
				if (!szYD_STK_COL_GP.trim().equals(
						ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP")
								.trim()))
					break;

				szSTL_NO = ydDaoUtils.paraRecChkNull(recPara, "STL_NO").trim();

				// 다른 크레인 스케줄에 등록 되어 있는지 체크
				blnRtnVal = chkCrnWrkMtl(szSTL_NO);
				if (!blnRtnVal)
					return;

				// 권상모음순서
				intYD_UP_COLL_SEQ = intYD_UP_COLL_SEQ + 1;
				// 재료번호
				recOutPara.setField("STL_NO" + Loop_i, szSTL_NO);
				// 권상모음순서
				recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, "" + Loop_i);
				// 재료매수
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

			// 전문 송신
			// ydDelegate.sendMsg(recOutPara);
			// 수정 - 이현성
			// ydDelegate.simSndMsg(recOutPara);
			ydDelegate.rmtSndMsg(recOutPara);
			szMsg = "C열연소재정리Lot편성 후 C열연소재정리작업요구 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		} catch (Exception e) {
			szMsg = "C열연소재정리Lot편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}

	} // end of procCHrMatlAdjLotComp

	/**
	 * 오퍼레이션명 : C열연소재정리작업요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procCHrMatlAdjWrkReq(JDTORecord msgRecord) throws JDTOException {

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
		String szMethodName = "procCHrMatlAdjWrkReq";
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
			// if (szYD_STK_COL_GP.equals("")) {
			//				
			// szMsg = "[전문 이상] 적치열구분이 없습니다.";
			// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			// return;
			//				
			// }
			// //적치BED번호
			// szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord,
			// "YD_STK_BED_NO");
			// if (szYD_STK_BED_NO.equals("")) {
			//				
			// szMsg = "[전문 이상] 적치BED번호가 없습니다.";
			// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			// return;
			//				
			// }
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

				// //크레인사양과 저장품 사양을 체크(길이,폭,중량)
				// blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
				// if (!blnRtnVal) return;

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
			szMsg = "C열연소재정리작업요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}

	} // end of procCHrMatlAdjWrkReq()

	/**
	 * 오퍼레이션명 : C열연제품정리Lot편성
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procCHrGdsAdjLotComp(JDTORecord msgRecord) throws JDTOException {

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
		String szMethodName = "procCHrGdsAdjLotComp";
		// 사용자
		String szUser = "SYSTEM";

		// Lot 편성 재료 매수
		int intLotGpSh = 0;
		// 전문 생성 일시
		String szDate = null;

		// 동구분
		String szYD_BAY_GP = null;
		// 설비ID
		String szYD_EQP_ID = null;
		// 적치열구분
		String szYD_STK_COL_GP = null;
		// 재료번호
		String szSTL_NO = null;
		// 해당열의 최소 기준매수
		int intColMinCnt = 5;
		// 스케줄코드
		String szYD_SCH_CD = null;
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
			// 설비ID
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if (szYD_EQP_ID.equals("")) {

				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}
			// 동구분
			szYD_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP");
			if (szYD_BAY_GP.equals("")) {

				szMsg = "[전문 이상] 동구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			}

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			// 보급 Lot 편성 대상 재료 Select
			blnRtnVal = chkGetCHrGdsAdjLotComp(szYD_EQP_ID.substring(0, 2)
					.trim(), intColMinCnt, rsResult);
			if (!blnRtnVal)
				return;

			// 레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			// JMS TC CODE
			recOutPara.setField("JMS_TC_CD", "YDYDJ279");
			// 레코드 커서 처음으로
			rsResult.first();

			// 레코드 추출
			recPara = rsResult.getRecord();

			// 기준적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara,
					"YD_STK_COL_GP");

			// =================================================================================
			// BRE 등록 안됨...테스트용 스케줄코드 생성
			// 추후 구현..
			szYD_SCH_CD = "JECG02MM";
			// =================================================================================

			// 보급 Lot 편성 재료 매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {

				// 레코드 추출
				recPara = rsResult.getRecord();

				// 같은 적치열의 재료만 Lot에 편성(적치열구분이 다르면 루프 탈출)
				if (!szYD_STK_COL_GP.trim().equals(
						ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP")
								.trim()))
					break;

				szSTL_NO = ydDaoUtils.paraRecChkNull(recPara, "STL_NO").trim();

				// 다른 크레인 스케줄에 등록 되어 있는지 체크
				blnRtnVal = chkCrnWrkMtl(szSTL_NO);
				if (!blnRtnVal)
					return;

				// 권상모음순서
				intYD_UP_COLL_SEQ = intYD_UP_COLL_SEQ + 1;
				// 재료번호
				recOutPara.setField("STL_NO" + Loop_i, szSTL_NO);
				// 권상모음순서
				recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, "" + Loop_i);
				// 재료매수
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

			// 전문 송신
			ydDelegate.sendMsg(recOutPara);
			szMsg = "C열연제품정리Lot편성 후 C열연제품정리작업요구 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		} catch (Exception e) {
			szMsg = "C열연제품정리Lot편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}

	} // end of procCHrGdsAdjLotComp

	/**
	 * 오퍼레이션명 : C열연제품정리작업요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procCHrGdsAdjWrkReq(JDTORecord msgRecord) throws JDTOException {

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
		String szMethodName = "procCHrGdsAdjWrkReq";
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
			// if (szYD_STK_COL_GP.equals("")) {
			//				
			// szMsg = "[전문 이상] 적치열구분이 없습니다.";
			// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			// return;
			//				
			// }
			// //적치BED번호
			// szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord,
			// "YD_STK_BED_NO");
			// if (szYD_STK_BED_NO.equals("")) {
			//				
			// szMsg = "[전문 이상] 적치BED번호가 없습니다.";
			// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			// return;
			//				
			// }
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

				// //크레인사양과 저장품 사양을 체크(길이,폭,중량)
				// blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
				// if (!blnRtnVal) return;

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
			szMsg = "C열연제품정리작업요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}

	} // end of procCHrGdsAdjWrkReq()


	/**
	 * 오퍼레이션명 : C열연제품정리Lot편성 데이터 유무체크 및 데이터 반환
	 * 
	 * @param String
	 *            szYdBay 야드,동구분 int intColMinCnt 적치열최소기준매수 JDTORecordSet
	 *            rsResult 결과레코드셋
	 * @return boolean true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetCHrGdsAdjLotComp(String szYdBay, int intColMinCnt,
			JDTORecordSet rsResult) throws JDTOException {

		// 저장품 DAO
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메소드명
		String szMethodName = "chkGetCHrGdsAdjLotComp";
		String szMsg = null;

		// 레코드 선언
		JDTORecord recPara = null;

		try {
			// 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();

			// 적치열구분
			recPara.setField("YD_STK_COL_GP", szYdBay);
			// 적치Bed번호
			recPara.setField("CNT", "" + intColMinCnt);

			// 저장품 테이블 조회
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsResult, 7);

			// 리턴값 메세지처리
			if (intRtnVal >= 1) {

				blnRtnVal = true;

			} else if (intRtnVal == 0) {

				szMsg = "야드(" + szYdBay.substring(0, 1) + ")," + "동("
						+ szYdBay.substring(1, 2) + ")"
						+ "에 대한 Lot 편성 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == -2) {

				szMsg = "야드(" + szYdBay.substring(0, 1) + ")," + "동("
						+ szYdBay.substring(1, 2) + ")"
						+ "에 대한 Lot 편성  데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = "야드(" + szYdBay.substring(0, 1) + ")," + "동("
						+ szYdBay.substring(1, 2) + ")"
						+ "에 대한 Lot 편성  데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch (Exception e) {
			szMsg = "C열연제품정리Lot편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: "
					+ e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} // end of chkGetCHrGdsAdjLotComp

	/**
	 * 오퍼레이션명 : C열연소재정리Lot편성 데이터 유무체크 및 데이터 반환
	 * 
	 * @param String
	 *            szYdBay 야드,동구분 int intColMinCnt 적치열최소기준매수 JDTORecordSet
	 *            rsResult 결과레코드셋
	 * @return boolean true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetCHrMatlAdjLotComp(String szYdBay, int intColMinCnt,
			JDTORecordSet rsResult) throws JDTOException {

		// 저장품 DAO
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메소드명
		String szMethodName = "chkGetCHrMatlAdjLotComp";
		String szMsg = null;

		// 레코드 선언
		JDTORecord recPara = null;

		try {
			// 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();

			// 적치열구분
			recPara.setField("YD_STK_COL_GP", szYdBay);
			// 적치Bed번호
			recPara.setField("CNT", "" + intColMinCnt);

			// 저장품 테이블 조회
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsResult, 7);

			// 리턴값 메세지처리
			if (intRtnVal >= 1) {

				blnRtnVal = true;

			} else if (intRtnVal == 0) {

				szMsg = "야드(" + szYdBay.substring(0, 1) + ")," + "동("
						+ szYdBay.substring(1, 2) + ")"
						+ "에 대한 Lot 편성 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == -2) {

				szMsg = "야드(" + szYdBay.substring(0, 1) + ")," + "동("
						+ szYdBay.substring(1, 2) + ")"
						+ "에 대한 Lot 편성  데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = "야드(" + szYdBay.substring(0, 1) + ")," + "동("
						+ szYdBay.substring(1, 2) + ")"
						+ "에 대한 Lot 편성  데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch (Exception e) {
			szMsg = "C열연소재정리Lot편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: "
					+ e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} // end of chkGetCHrMatlAdjLotComp

	/**
	 * 오퍼레이션명 : C열연정정보급준비Lot편성 데이터 유무체크 및 데이터 반환
	 * 
	 * @param String
	 *            szYdBay 야드,동 JDTORecordSet rsResult 결과레코드셋 int intGp
	 *            구분(0:동간이적조회,1:동내이적조회)
	 * @return boolean true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetCHrShearSupPrepLotComp(String szYdBay,
			JDTORecordSet rsResult, int intGp) throws JDTOException {

		// 저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메소드명
		String szMethodName = "chkGetCHrShearSupPrepLotOut";
		String szMsg = null;
		String szMsgGp = null;
		// 설비ID
		String szYD_EQP_ID = null;
		// 공정구분
		String szPROC_GP = null;
		// 레코드 선언
		JDTORecord recPara = null;

		try {
			// 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();

			// 목표행선구분(C열연정정지시대기)
			recPara.setField("YD_AIM_RT_GP", "H3");
			// 야드동
			recPara.setField("YD_STK_COL_GP", szYdBay);

			// 동구분을 공정구분으로 변환
			// E동->R(SPM2), G동->H(#1HFL), H동->K(#1HSL)
			szPROC_GP = szYdBay.substring(1, 2);
			if (szPROC_GP.equals("E"))
				szPROC_GP = "R";
			else if (szPROC_GP.equals("G"))
				szPROC_GP = "H";
			else if (szPROC_GP.equals("H"))
				szPROC_GP = "K";
			else {

				szMsg = "동구분(" + szPROC_GP + ")에는 정정라인이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return blnRtnVal = false;

			}
			// 공정구분
			recPara.setField("PROC_GP", szPROC_GP);
			// 저장품 테이블 조회
			if (intGp == 0) {

				intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 24);
				szMsgGp = "동간이적";

			} else if (intGp == 1) {

				intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 25);
				szMsgGp = "동내이적";

			}

			// 리턴값 메세지처리
			if (intRtnVal >= 1) {

				blnRtnVal = true;

			} else if (intRtnVal == 0) {

				szMsg = "C열연정정보급준비Lot편성(" + szMsgGp + ") 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = true;

			} else if (intRtnVal == -2) {

				szMsg = "C열연정정보급준비Lot편성(" + szMsgGp
						+ ") 데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = "C열연정정보급준비Lot편성(" + szMsgGp + ") 데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch (Exception e) {
			szMsg = "C열연정정보급준비Lot편성(" + szMsgGp
					+ ") 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} // end of chkGetCHrShearSupPrepLotOut

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
