package com.inisteel.cim.yd.ydWkReq.IssueWkReq;

import java.util.List;
import java.util.Vector;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.dao.ydCarSpecDao.YdCarSpecDao;
import com.inisteel.cim.yd.common.dao.ydCrnSpecDao.YdCrnSpecDao;
import com.inisteel.cim.yd.common.dao.ydCrnWrkMtlDao.YdCrnWrkMtlDao;
import com.inisteel.cim.yd.common.dao.ydEqpDao.YdEqpDao;
import com.inisteel.cim.yd.common.dao.ydSchRuleDao.YdSchRuleDao;
import com.inisteel.cim.yd.common.dao.ydStkColDao.YdStkColDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.dao.ydTcarSchDao.YdTcarSchDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookDao.YdWrkbookDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookMtlDao.YdWrkbookMtlDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO;
import com.inisteel.cim.yd.ydWkReq.ReceiptWkReq.CoilRcptWrkDmdSeEJBBean;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;

/**
 * 출고작업요구 Session EJB
 * 
 * @ejb.bean name="CoilIssueWrkDmdSeEJB" jndi-name="CoilIssueWrkDmdSeEJB"
 *           type="Stateless" view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class CoilIssueWrkDmdSeEJBBean extends BaseSessionBean {

	// Session Name
	private String szSessionName = getClass().getName();

	private YdUtils ydUtils = new YdUtils();

	private YdDaoUtils ydDaoUtils = new YdDaoUtils();

	private YdDelegate ydDelegate = new YdDelegate();

	private YdPICommDAO   ydPICommDAO   = new YdPICommDAO();	
	
	private EJBConnector ydEjbCon = new EJBConnector("default", this);

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
	 * 오퍼레이션명 : C열연정정입측Line-In요구/C열연 정정보급요구/C열연정정 Take-In요구 ( H2YDL001,
	 * HRYDJ008, H2YDL004,H2YDL051) SPM1, SPM2, SPM5, #1HFL, #2HFL, #3HFL 보급요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord procCHrShearInSupLotComp(JDTORecord msgRecord)
			throws DAOException {
		// DAO객체 선언
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		YdCrnWrkMtlDao ydCrnWrkMtlDao = new YdCrnWrkMtlDao();
		YdTcarSchDao ydTcarSchDao = new YdTcarSchDao();
		CoilJspDAO dao = new CoilJspDAO();
		ymCommonDAO dao2 = ymCommonDAO.getInstance();

		EJBConnector ejbConn = null;
		// 레코드 선언
		JDTORecord inRecord = null;
		JDTORecord inRecord1 = null;
		JDTORecord recPara = null;
		JDTORecord recEdit = null;
		JDTORecord recGetVal = null;
		JDTORecord recGetVal1 = null;
		JDTORecord outRecord = JDTORecordFactory.getInstance().create(); //
		JDTORecord outRecord2 = JDTORecordFactory.getInstance().create(); //
		JDTORecordSet rsGetStock = null;
		JDTORecordSet rsGetStock1 = null;

		JDTORecordSet outRecSet = JDTORecordFactory.getInstance()
				.createRecordSet("YD");
		JDTORecordSet outRecSet2 = JDTORecordFactory.getInstance()
				.createRecordSet("YD");
		JDTORecord[] inRecordarr = null;

		String szMsg = "";
		String szMethodName = "procCHrShearInSupLotComp";
		String szOperationName = "C열연정정입측Line-In요구/C열연 정정입측보급Lot편성/C열연정정 Take-In요구";
		String szRcvTcCode = "";

		String szYD_EQP_ID = "";
		String szYD_STK_BED_NO = "";
		String szSTL_NO = "";
		String szEQP_GP = "";
		String szTREAT_GP = "";
		String szYD_STK_COL_GP = "";
		String tmpYD_EQP_ID = "";
		String tmpDong = "";
		String tmpYD_STK_COL_GP = "";

		int intRtnVal = 0;

		// 목표행선구분
		// //야드적치Bed입출고상태
		// String szYD_STK_BED_WHIO_STAT = null;
		// 스케줄코드
		String szYD_SCH_CD = "";
		String FIRST_YD_SCH_CD = "";

		// 공정구분
		String FIRST_YD_WBOOK_ID = "";
		String YD_WBOOK_ID = "";
		String sYD_WBOOK_ID = "";
		String sRTN_CD = "";
		String sRTN_MSG = "";
		String szTCAR_YD_EQP_ID = "";

		String sSUPPLY_DEMAND_DT = "";
		String sLOC = "";
		String szPARA_STL_NO = "";

		String szWORD_PROC = "";
		String szEQP_CD = "";
		// TC CODE 추출
		szRcvTcCode = ydUtils.getTcCode(msgRecord);
		if (szRcvTcCode == null || szRcvTcCode.equals("")) {
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error ("
					+ szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return outRecord;
		}

		// TC CODE DISPLAY
		if (bDebugFlag) {
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}

		try {
			// =============================================================
			// =============================================================
			szMsg = "[C열연정정L2/열연조업] C열연정정입측Line-In요구/정정입측보급Lot편성 수신 TCCODE(" + szRcvTcCode + ")시작";
			//ydUtils.putLogMsg("H" , YdConstant.YD_MONITORING_CHANNEL_H , szMsg , "" , "" , "" , "I" , "A" , "I" , szRcvTcCode , szSessionName , szMethodName);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			// ------------------------------------------------------------------------------------------------------------
			// 증설1
			// ------------------------------------------------------------------------------------------------------------
			YdEqpDao ydEqpDao = new YdEqpDao();
			JDTORecordSet outResult9 = JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord inRecord9 = JDTORecordFactory.getInstance().create();
			JDTORecord outRecord9 = JDTORecordFactory.getInstance().create();
			String szAPPLY_YN = "N";

			inRecord9.setField("REPR_CD_GP" , "H00010"); // 증설1

			/*
			 * com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN
			 */
			intRtnVal = ydEqpDao.getYdEqp(inRecord9 , outResult9 , 999);
			if (intRtnVal > 0) {
				outResult9.first();
				outRecord9 = outResult9.getRecord();
				szAPPLY_YN = outRecord9.getFieldString("ITEM1");
			}
			szMsg = "증설1 적용 " + szAPPLY_YN;
			ydUtils.putLog(szOperationName , szMethodName , szMsg , YdConstant.DEBUG);

			// C증설
			// H2YDL001 수신처리
			// C열연정정입측Line-In요구------------------------------------------------------------------------------------
			if (szRcvTcCode.equals("H2YDL001") || szRcvTcCode.equals("H2YDL011") || szRcvTcCode.equals("H2YDL021") || szRcvTcCode.equals("H2YDL031") 
					|| szRcvTcCode.equals("H2YDL041") || szRcvTcCode.equals("H2YDL051") || szRcvTcCode.equals("H2YDL061") || szRcvTcCode.equals("H2YDL071")) {

				szMsg = szRcvTcCode + "수신처리 C열연정정Line-In요구";
				ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.DEBUG);

				szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord , "YD_EQP_ID");
				if (szYD_EQP_ID.equals("")) {
					szMsg = "[전문 이상] 설비ID가 없습니다.";
					ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);

					outRecord.setField("RTN_CD" , "0");
					outRecord.setField("RTN_MSG" , szMsg);
					return outRecord;
				}

				szYD_SCH_CD = szYD_EQP_ID.substring(0 , 4) + "01UM";
				FIRST_YD_SCH_CD = szYD_SCH_CD;
				
				
				//**************************************************************************************
				// 보급 순서를 지키기 위한 반대 크레인 스케쥴 존재 시 보급 skip
				// 1. 1호 크레인 보급 존재 시 
				// 2. 재작업 보급 존재 시 
				//**************************************************************************************
				String QueryId2 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStockDao.spmCreSchChk";
				List creSchYNChklist = dao2.getCommonList(QueryId2 , new Object[]{szYD_SCH_CD});

				if(creSchYNChklist.size()>0){
					JDTORecord creSchYNChk = (JDTORecord) creSchYNChklist.get(0);
					String CHK2 = StringHelper.evl(creSchYNChk.getFieldString("CHK") , "");
	
					if ("Y".equals(CHK2)) {
						szMsg = "반대 크레인 보급(재작업)스케쥴작업이 존재 합니다(보급순서유지).SKIP";
						ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
	
						outRecord.setField("RTN_CD" , "0");
						outRecord.setField("RTN_MSG" , szMsg);
						return outRecord;					
					}
				}
				//**************************************************************************************

				inRecord = JDTORecordFactory.getInstance().create();

				szPARA_STL_NO = ydDaoUtils.paraRecChkNull(msgRecord , "STL_NO");
				if (!szPARA_STL_NO.equals("")) {
					// 화면에서 백업처리 한 경우
					inRecord.setField("V_COIL_NO" , szPARA_STL_NO);
				}

				inRecord.setField("V_WORK_STAT" , "B");
				inRecord.setField("V_PAGE_NO1" , "1");
				inRecord.setField("V_ROWCOUNT1" , "10");
				inRecord.setField("V_PAGE_NO2" , "1");
				inRecord.setField("V_ROWCOUNT2" , "10");

				if (szYD_EQP_ID.equals("HHKE01")) { // 입측
					inRecord.setField("V_LINE" , "H");
					inRecord.setField("V_NEXT_PROC" , "E");
					inRecord.setField("V_LINE_NEW" , "SPM1");

				} else if (szYD_EQP_ID.equals("HGFE01")) { // 입측
					inRecord.setField("V_LINE" , "G");
					inRecord.setField("V_NEXT_PROC" , "E");
					inRecord.setField("V_LINE_NEW" , "HFL1");

				} else if (szYD_EQP_ID.equals("HEDE01")) { // 입측
					inRecord.setField("V_LINE" , "E");
					inRecord.setField("V_NEXT_PROC" , "E");
					inRecord.setField("V_LINE_NEW" , "SPM2");

				} else if (szYD_EQP_ID.equals("HHKD01")) { // 출측
					inRecord.setField("V_LINE" , "H");
					inRecord.setField("V_NEXT_PROC" , "D");
					inRecord.setField("V_LINE_NEW" , "SPM1");

				} else if (szYD_EQP_ID.equals("HEDD01")) { // 출측
					inRecord.setField("V_EQP_CD" , "E");
					inRecord.setField("V_NEXT_PROC" , "D");
					inRecord.setField("V_LINE_NEW" , "SPM2");
					// C증설
				} else if (szYD_EQP_ID.equals("HCKE03")) { // 입측
					inRecord.setField("V_LINE" , "C");
					inRecord.setField("V_NEXT_PROC" , "E");
					inRecord.setField("V_LINE_NEW" , "SPM3");

				} else if (szYD_EQP_ID.equals("HCKD03")) { // 출측
					inRecord.setField("V_LINE" , "C");
					inRecord.setField("V_NEXT_PROC" , "D");
					inRecord.setField("V_LINE_NEW" , "SPM3");

				} else if (szYD_EQP_ID.equals("HBKE04")) { // 입측
					inRecord.setField("V_LINE" , "B");
					inRecord.setField("V_NEXT_PROC" , "E");
					inRecord.setField("V_LINE_NEW" , "SPM4");

				} else if (szYD_EQP_ID.equals("HAKE05")) { // 입측
					inRecord.setField("V_LINE" , "B");
					inRecord.setField("V_NEXT_PROC" , "E");
					inRecord.setField("V_LINE_NEW" , "SPM5");

				} else if (szYD_EQP_ID.equals("HBKD05")) { // 출측  HBKD05 ???
					inRecord.setField("V_LINE" , "B");
					inRecord.setField("V_NEXT_PROC" , "D");
					inRecord.setField("V_LINE_NEW" , "SPM4");

				} else if (szYD_EQP_ID.equals("HAKD05")) { // 출측
					inRecord.setField("V_LINE" , "B");
					inRecord.setField("V_NEXT_PROC" , "D");
					inRecord.setField("V_LINE_NEW" , "SPM5");

				} else if (szYD_EQP_ID.equals("HCFE04")) { // 입측
					inRecord.setField("V_LINE" , "C");
					inRecord.setField("V_NEXT_PROC" , "E");
					inRecord.setField("V_LINE_NEW" , "HFL4");

				} else if (szYD_EQP_ID.equals("HBFE05")) { // 입측
					inRecord.setField("V_LINE" , "B");
					inRecord.setField("V_NEXT_PROC" , "E");
					inRecord.setField("V_LINE_NEW" , "HFL5");

				}

				szPARA_STL_NO = ydDaoUtils.paraRecChkNull(msgRecord , "STL_NO");
				if (!szPARA_STL_NO.equals("")) {
					// 화면에서 백업처리 한 경우
					inRecord.setField("V_COIL_NO" , szPARA_STL_NO);
				} else {
					inRecord.setField("V_COIL_NO" , "");

				}

				// 장애 발생시 이전 소스로 원복 하기 위한 조치
				String QueryId = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStockDao.updYdStockchklistSupLot";
				List sposYNChklist = dao2.getCommonList(QueryId , new Object[]{});

				JDTORecord unloadPointrec = (JDTORecord) sposYNChklist.get(0);
				String CHK = StringHelper.evl(unloadPointrec.getFieldString("CHK") , "");

				if (szAPPLY_YN.equals("Y")) {

					if (CHK.equals("Y")) {

						inRecord = JDTORecordFactory.getInstance().create();
						inRecord.setField("YD_EQP_ID" , szYD_EQP_ID);// 설비코드

						EJBConnector ejbConn2 = new EJBConnector("hsteelApp" , "HrShrWoMgtFaEJB" , this);
						ejbConn2.trx("rcvHrShrSupDmdAuto" , new Class[]{JDTORecord.class} , new Object[]{inRecord});

						if (!szPARA_STL_NO.equals("")) { // C열연정정 보급 요구 - 화면백업
							inRecord.setField("V_COIL_NO" , szPARA_STL_NO);
							/*
							 * com.inisteel.cim.yd.jsp.coiljsp.dao.CoilYDDao.getLineSupplyOrderNEWStlNo
							 */
							outRecSet = dao.getLineSupplyOrderNEWStlNo(inRecord);

						} else {
							/*
							 * com.inisteel.cim.yd.jsp.coiljsp.dao.CoilYDDao.getLineSupplyOrderNEW
							 */
							outRecSet = dao.getLineSupplyOrderNEW(inRecord);
						}

					} else {
						/*
						 * com.inisteel.cim.yd.jsp.coiljsp.dao.CoilYDDao. getLineSupplyOrder
						 */
						outRecSet = dao.getLineSupplyOrder(inRecord);
					}

				} else {
					if (CHK.equals("Y")) {

						// 자동보급요구 기능 추가(조업 호출)##################################################
						inRecord = JDTORecordFactory.getInstance().create();
						inRecord.setField("YD_EQP_ID" , szYD_EQP_ID);// 설비코드

						EJBConnector ejbConn2 = new EJBConnector("hsteelApp" , "HrShrWoMgtFaEJB" , this);
						ejbConn2.trx("rcvHrShrSupDmdAuto" , new Class[]{JDTORecord.class} , new Object[]{inRecord});
						// #############################################################################

						/*
						 * com.inisteel.cim.yd.jsp.coiljsp.dao.CoilYDDao. getSupplyInOrderListNEW
						 */
						outRecSet = dao.getSupplyInOrderListNEW(inRecord);

					} else {
						/*
						 * com.inisteel.cim.yd.jsp.coiljsp.dao.CoilYDDao. getSupplyInOrderList
						 */
						outRecSet = dao.getSupplyInOrderList(inRecord);
					}
				}
				if (outRecSet.size() < 0) {
					szMsg = "[C열연 정정 Line-In 데이터가 없습니다.(에러) ";
					ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
					outRecord.setField("RTN_CD" , "0");
					outRecord.setField("RTN_MSG" , szMsg);
					return outRecord;

				} else if (outRecSet.size() == 0) {
					szMsg = "C열연 정정Line-In 데이터가 없습니다. ";
					ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.WARNING);
					outRecord.setField("RTN_CD" , "0");
					outRecord.setField("RTN_MSG" , szMsg);
					return outRecord;

				} else {
					szMsg = "C열연 정정 Line-In 데이터가  조회 성공";
					ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.DEBUG);
				}

				// 커서 처음으로

				// outRecSet.first();

				String sSUPPLY_DEMAND_DT_CHK = "";
				for (int nLoop = 1; nLoop <= outRecSet.size(); nLoop++) {
					recPara = JDTORecordFactory.getInstance().create();
					outRecSet.absolute(nLoop);
					recPara = outRecSet.getRecord();

					sSUPPLY_DEMAND_DT = recPara.getFieldString("SUPPLY_DEMAND_DT");
					if (nLoop == 1) {
						sSUPPLY_DEMAND_DT_CHK = sSUPPLY_DEMAND_DT;
						FIRST_YD_WBOOK_ID = StringHelper.evl(recPara.getFieldString("YD_WBOOK_ID") , "");
					}

					if (sSUPPLY_DEMAND_DT_CHK.equals(sSUPPLY_DEMAND_DT)) { // 지시일시가
																			// 같고

						sLOC = recPara.getFieldString("LOC");

						if (sLOC.substring(0 , 1).equals(szYD_EQP_ID.substring(1 , 2))) { // 저장위치하구 설비하구
																							// 같은 동이면
							if (!sLOC.substring(1 , 3).equals("TC")) { // 대차위가
																		// 아니면
								YD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");
								szSTL_NO = recPara.getFieldString("COIL_NO");

								// 작업예약id가 존재 안 하는 경우 작업예약 편성 한다.
								if (YD_WBOOK_ID.equals("")) {
									inRecord = JDTORecordFactory.getInstance().create();
									inRecord.setField("YD_SCH_CD" , szYD_SCH_CD);// 스케줄코드
									inRecord.setField("STL_SH" , "1"); // LINE_IN
																		// 재료매수
									inRecord.setField("STL_NO1" , szSTL_NO);
									inRecord.setField("YD_TO_LOC_DCSN_MTD" , "F");
									inRecord.setField("TO_YD_STK_BED_NO" , szYD_EQP_ID.substring(0 , 6) + "100");

									// 작업예약 등록 호출
									ejbConn = new EJBConnector("default" , "CoilCrnSchSeEJB" , this);
									outRecord = (JDTORecord) ejbConn.trx("WrkBookInsertProcTX" , new Class[]{JDTORecord.class} , new Object[]{inRecord});
									sRTN_CD = StringHelper.evl(outRecord.getFieldString("RTN_CD") , "0");
									sYD_WBOOK_ID = StringHelper.evl(outRecord.getFieldString("YD_WBOOK_ID") , "");
									sRTN_MSG = StringHelper.evl(outRecord.getFieldString("RTN_MSG") , "");
									if ("0".equals(sRTN_CD)) {
										szMsg = "작업예약 등록시 ERROR";
										ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
										outRecord.setField("RTN_CD" , "0");
										outRecord.setField("RTN_MSG" , sRTN_MSG);
										return outRecord;

									}
									szMsg = "C열연정정입측Take-In요구 - C열연 정정입측 보급 작업예약  완료!";
									ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.DEBUG);

									if (FIRST_YD_WBOOK_ID.equals("")) {
										FIRST_YD_WBOOK_ID = sYD_WBOOK_ID;
										FIRST_YD_SCH_CD = szYD_SCH_CD;
									}
								}
								// break;
							}
						}
					}
				}

				// H2YDL004 수신처리 --C열연정정
				// Take-In요구----------------------------------------------------------------------------------
			} else if (szRcvTcCode.equals("H2YDL004") || szRcvTcCode.equals("H2YDL014") || szRcvTcCode.equals("H2YDL024")
			// C열연증설
					|| szRcvTcCode.equals("H2YDL034") || szRcvTcCode.equals("H2YDL044") || szRcvTcCode.equals("H2YDL054") || szRcvTcCode.equals("H2YDL064") 
					|| szRcvTcCode.equals("H2YDL074")) {

				szMsg = "H2YDL004 수신처리 --C열연정정 Take-In요구";
				ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.DEBUG);

				// ============================================================
				// C열연정정 Take-In 요구(H2YDL004)수신시 처리 (Line-In 요구와 동일)
				// ============================================================

				// 야드설비구분
				szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord , "YD_EQP_ID");
				if (szYD_EQP_ID.equals("")) {
					szMsg = "[전문 이상] 설비ID가 없습니다.";
					ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
					outRecord.setField("RTN_CD" , "0");
					outRecord.setField("RTN_MSG" , szMsg);
					return outRecord;
				}

				// 야드적치Bed번호
				szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord , "YD_STK_BED_NO");
				if (szYD_STK_BED_NO.equals("")) {
					szMsg = "[전문 이상] 적치BED번호가 없습니다.";
					ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
					outRecord.setField("RTN_CD" , "0");
					outRecord.setField("RTN_MSG" , szMsg);
					return outRecord;
				}

				// 재료번호
				szSTL_NO = ydDaoUtils.paraRecChkNull(msgRecord , "STL_NO");
				if (szSTL_NO.equals("")) {
					szMsg = "[전문 이상] 재료번호가 없습니다.";
					ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
					outRecord.setField("RTN_CD" , "0");
					outRecord.setField("RTN_MSG" , szMsg);
					return outRecord;
				}
				// SJH

				// =================================================================================
				// 설비별 보급요구 스케줄 코드 편성
				szYD_SCH_CD = szYD_EQP_ID.substring(0 , 4) + "03UM";
				// =================================================================================

				// 재료번호
				// 레코드 생성
				inRecord = JDTORecordFactory.getInstance().create();

				inRecord.setField("YD_SCH_CD" , szYD_SCH_CD);// 스케줄코드
				inRecord.setField("STL_SH" , "1"); // LINE_IN 재료매수
				inRecord.setField("STL_NO1" , szSTL_NO);
				inRecord.setField("YD_TO_LOC_DCSN_MTD" , "F");

				inRecord.setField("YD_AIM_YD_GP" , szYD_EQP_ID.substring(0 , 1));
				inRecord.setField("YD_AIM_BAY_GP" , szYD_EQP_ID.substring(1 , 2)); // 작업예약에
																					// 목표동
																					// 설정처리함
				inRecord.setField("TO_YD_STK_BED_NO" , szYD_EQP_ID.substring(0 , 6) + szYD_STK_BED_NO);

				// 작업예약 등록 호출

				// YD_SCH_CD:스케줄코드,
				// STL_SH: 재료매수,
				// YD_TO_LOC_DCSN_MTD(TO위치 결정 방법)
				// STL_NO(재료번호1,2,3,....)
				// FR_YD_STK_BED_NO(적치배드)
				// TO_YD_STK_BED_NO(가이드가 됨)

				ejbConn = new EJBConnector("default" , "CoilCrnSchSeEJB" , this);
				outRecord = (JDTORecord) ejbConn.trx("WrkBookInsertProcTX" , new Class[]{JDTORecord.class} , new Object[]{inRecord});
				sRTN_CD = StringHelper.evl(outRecord.getFieldString("RTN_CD") , "0");
				sYD_WBOOK_ID = StringHelper.evl(outRecord.getFieldString("YD_WBOOK_ID") , "");
				sRTN_MSG = StringHelper.evl(outRecord.getFieldString("RTN_MSG") , "");
				if ("0".equals(sRTN_CD)) {
					szMsg = "작업예약 등록시 ERROR";
					ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
					outRecord.setField("RTN_CD" , "0");
					outRecord.setField("RTN_MSG" , sRTN_MSG);
					return outRecord;

				}
				szMsg = "C열연정정입측Take-In요구 - C열연 정정입측 보급 작업예약  완료!";
				ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.DEBUG);

				FIRST_YD_WBOOK_ID = sYD_WBOOK_ID;
				FIRST_YD_SCH_CD = szYD_SCH_CD;

				// HRYDJ008 수신처리 C열연
				// 정정보급요구------------------------------------------------------------------------------------
			} else if (szRcvTcCode.equals("HRYDJ008")) {

				szMsg = "HRYDJ008 수신처리 C열연 정정보급요구";
				ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.DEBUG);

				// 처리구분
				szTREAT_GP = ydDaoUtils.paraRecChkNull(msgRecord , "TREAT_GP");
				if (szTREAT_GP.equals("")) {
					szMsg = "[전문 이상] 처리구분이 없습니다.";
					ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
					outRecord.setField("RTN_CD" , "0");
					outRecord.setField("RTN_MSG" , szMsg);
					return outRecord;
				}

				// 야드설비구분
				szWORD_PROC = ydDaoUtils.paraRecChkNull(msgRecord , "WORD_PROC");
				if (szWORD_PROC.equals("")) {
					szMsg = "[전문 이상] 설비구분이 없습니다.";
					ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
					outRecord.setField("RTN_CD" , "0");
					outRecord.setField("RTN_MSG" , szMsg);
					return outRecord;
				}

				recPara = JDTORecordFactory.getInstance().create();
				// intRtnVal = this.ydEqpGpConversion(msgRecord, recPara);
				intRtnVal = this.ydWordProcConversion(msgRecord , recPara);
				if (intRtnVal < 0) {
					szMsg = "C열연설비구분변경실패!!";
					ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);

					outRecord.setField("RTN_CD" , "0");
					outRecord.setField("RTN_MSG" , "[전문 이상] C열연설비구분변경실패(ydEqpGpConversion)");
					return outRecord;
				}

				szPARA_STL_NO = ydDaoUtils.paraRecChkNull(msgRecord , "STL_NO");
				if (!szPARA_STL_NO.equals("")) {
					// 화면에서 백업처리 한 경우
					szMsg = "C열연정정 보급 요구 - 화면백업 :: " + "[" + szPARA_STL_NO + "]";
					ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.DEBUG);
				}

				szYD_EQP_ID = ydDaoUtils.paraRecChkNull(recPara , "YD_EQP_ID");
				// szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recPara,
				// "YD_STK_BED_NO");
				szEQP_CD = ydDaoUtils.paraRecChkNull(recPara , "EQP_CD");

				szMsg = "C열연정정 보급 요구 - 설비 :: " + "[" + szYD_EQP_ID + "]";
				ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.DEBUG);

				recEdit = JDTORecordFactory.getInstance().create();

				szYD_SCH_CD = szYD_EQP_ID + "UM";

				recEdit.setField("YD_SCH_CD" , szYD_SCH_CD); // 스케줄코드

				szMsg = "C열연정정 보급 요구 - 스케줄코드 :: " + "[" + szYD_SCH_CD + "]";
				ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.DEBUG);

				// if (szYD_EQP_ID.equals("HHKE01")){
				// recEdit.setField("EQP_CD" , "SPM1");
				// } else if(szYD_EQP_ID.equals("HGFE01")){
				// recEdit.setField("EQP_CD" , "HFL1");
				// } else if(szYD_EQP_ID.equals("HFFE01")){
				// recEdit.setField("EQP_CD" , "HFL2");
				// } else if(szYD_EQP_ID.equals("HEDE01")){
				// recEdit.setField("EQP_CD" , "SPM2");
				// } else if(szYD_EQP_ID.equals("HDFE01")){
				// recEdit.setField("EQP_CD" , "HFL3");
				// } else if(szYD_EQP_ID.equals("HHKD01")){
				// recEdit.setField("EQP_CD" , "SPM1");
				// } else if(szYD_EQP_ID.equals("HEDD01")){
				// recEdit.setField("EQP_CD" , "SPM2");
				// }

				recEdit.setField("EQP_CD" , szEQP_CD);

				rsGetStock = JDTORecordFactory.getInstance().createRecordSet("");

				// 장애 발생시 이전 소스로 원복 하기 위한 조치
				String QueryId = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStockDao.updYdStockchklistSupLot";
				List sposYNChklist = dao2.getCommonList(QueryId , new Object[]{});

				JDTORecord unloadPointrec = (JDTORecord) sposYNChklist.get(0);
				String CHK = StringHelper.evl(unloadPointrec.getFieldString("CHK") , "");
				if (szAPPLY_YN.equals("Y")) {
					if (CHK.equals("Y")) {
						if (!szPARA_STL_NO.equals("")) { // C열연정정 보급 요구 - 화면백업
							recEdit.setField("V_COIL_NO" , szPARA_STL_NO);
							/*
							 * com.inisteel.cim.yd.jsp.coiljsp.dao.CoilYDDao.getHFLLineSupplyOrderNEWStlNo
							 */
							rsGetStock = dao.getHFLLineSupplyOrderNEWStlNo(recEdit);

						} else {
							/*
							 * com.inisteel.cim.yd.jsp.coiljsp.dao.CoilYDDao.getHFLLineSupplyOrderNEW
							 */
							rsGetStock = dao.getHFLLineSupplyOrderNEW(recEdit);
						}

					} else {
						/*
						 * com.inisteel.cim.yd.jsp.coiljsp.dao.CoilYDDao.getHFLLineSupplyOrder
						 */
						rsGetStock = dao.getHFLLineSupplyOrder(recEdit);
					}

					if (rsGetStock.size() < 0) {
						outRecord.setField("RTN_CD" , "0");
						outRecord.setField("RTN_MSG" , "조업열연정정지시실적작업관리 에러)");
						return outRecord;

					} else if (rsGetStock.size() == 0) {
						outRecord.setField("RTN_CD" , "0");
						outRecord.setField("RTN_MSG" , "조업열연정정지시실적작업관리 에러(저장위치확인))");
						return outRecord;

					}

				} else {
					if (CHK.equals("Y")) {
						/*
						 * com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao. getYdStklyrHRShearWKNEW
						 */
						intRtnVal = ydStkLyrDao.getYdStklyr(recEdit , rsGetStock , 617);
					} else {
						/*
						 * com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao. getYdStklyrHRShearWK
						 */
						intRtnVal = ydStkLyrDao.getYdStklyr(recEdit , rsGetStock , 89);
					}
					if (intRtnVal < 0) {
						szMsg = "[C열연 정정] 조업열연정정지시실적작업관리 + 작업예약 + 작업예약재료 + 적치단 Error :: PARAMETER ERROR";
						ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);

						outRecord.setField("RTN_CD" , "0");
						outRecord.setField("RTN_MSG" , "조업열연정정지시실적작업관리 에러):" + szMsg);
						return outRecord;

					} else if (intRtnVal == 0) {
						szMsg = "[C열연 정정] 조업열연정정지시실적작업관리 + 작업예약 + 작업예약재료 + 적치단 Error :: DO NOT EXIST";
						ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.DEBUG);
						outRecord.setField("RTN_CD" , "0");
						outRecord.setField("RTN_MSG" , "조업열연정정지시실적작업관리 에러):" + szMsg);
						return outRecord;
					}
				}

				// 커서 처음으로
				rsGetStock.first();
				recGetVal = rsGetStock.getRecord();

				for (int Loop_i = 1; Loop_i <= rsGetStock.size(); Loop_i++) {

					// 설비별 보급동이 다르면 동간이적으로 스케줄 변경
					szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recGetVal , "YD_STK_COL_GP");

					if (!szYD_STK_COL_GP.equals("") && szYD_STK_COL_GP != null) {

						tmpYD_STK_COL_GP = szYD_STK_COL_GP.substring(0 , 2); // 쿼리에서
																				// 추출한
																				// 적치열
						tmpYD_EQP_ID = szYD_EQP_ID.substring(0 , 2); // 수신받은 설비
																		// 적치열
						tmpDong = szYD_STK_COL_GP.substring(1 , 2); // 쿼리에서 추출한
																	// 설비 동

						szMsg = "동간 보급대상 여부 체크 ==>" + tmpYD_STK_COL_GP + ':' + tmpYD_EQP_ID;
						ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.DEBUG);

						/* 정정지시 대상재 건수 만큼 Loop 돌면서 작업예약등록 호출 */
						inRecord = JDTORecordFactory.getInstance().create();

						if (!tmpYD_STK_COL_GP.equals(tmpYD_EQP_ID)) { // 동이 같지
																		// 않으면
																		// 동간이적
																		// 스케줄
																		// 처리

							// from 위치와 to 위치를 넣어 상용가능한 대차 read

							rsGetStock1 = JDTORecordFactory.getInstance().createRecordSet("");
							inRecord1 = JDTORecordFactory.getInstance().create();

							inRecord1.setField("FR_YD_BAY_GP" , tmpYD_STK_COL_GP.substring(1 , 2));// 스케줄코드
							inRecord1.setField("TO_YD_BAY_GP" , tmpYD_EQP_ID.substring(1 , 2)); // LINE_IN
																								// 재료매수

							/*
							 * com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao. getYdTcarStat
							 */
							intRtnVal = ydTcarSchDao.getYdTcarsch(inRecord1 , rsGetStock1 , 202);

							if (intRtnVal <= 0) {
								szMsg = "사용가능한 대차가 없습니다.";
								ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.DEBUG);
								if (tmpDong.equals("D") || tmpDong.equals("E") || tmpDong.equals("F")) {
									szYD_SCH_CD = szYD_STK_COL_GP.substring(0 , 2) + "TC02MM";
									inRecord.setField("YD_WRK_PLAN_TCAR" , "JXTC02");

								} else {
									szYD_SCH_CD = szYD_STK_COL_GP.substring(0 , 2) + "TC01MM";
									inRecord.setField("YD_WRK_PLAN_TCAR" , "JXTC01");

								}
							} else {
								// 커서 처음으로
								rsGetStock1.first();
								recGetVal1 = rsGetStock1.getRecord();
								szTCAR_YD_EQP_ID = ydDaoUtils.paraRecChkNull(recGetVal1 , "YD_EQP_ID");

								if (szTCAR_YD_EQP_ID.equals("JXTC01")) {
									szYD_SCH_CD = szYD_STK_COL_GP.substring(0 , 2) + "TC01MM";
									inRecord.setField("YD_WRK_PLAN_TCAR" , "JXTC01");

								} else {
									szYD_SCH_CD = szYD_STK_COL_GP.substring(0 , 2) + "TC02MM";
									inRecord.setField("YD_WRK_PLAN_TCAR" , "JXTC02");

								}

							}

							inRecord.setField("TO_YD_STK_BED_NO" , szYD_SCH_CD.substring(0 , 4) + "01");
						} else {
							szYD_SCH_CD = szYD_EQP_ID + "UM";
							inRecord.setField("TO_YD_STK_BED_NO" , szYD_EQP_ID.substring(1 , 6) + "100");
						}

						szMsg = "작업예약 스케줄 코드  ==>" + szYD_SCH_CD;
						ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.DEBUG);

						inRecord.setField("YD_SCH_CD" , szYD_SCH_CD);// 스케줄코드
						inRecord.setField("STL_SH" , "1"); // LINE_IN 재료매수
						inRecord.setField("STL_NO1" , ydDaoUtils.paraRecChkNull(recGetVal , "STL_NO"));
						inRecord.setField("YD_TO_LOC_DCSN_MTD" , "S");
						inRecord.setField("YD_AIM_YD_GP" , szYD_EQP_ID.substring(0 , 1));
						inRecord.setField("YD_AIM_BAY_GP" , szYD_EQP_ID.substring(1 , 2)); // 작업예약에 목표동 설정처리함

						// YD_SCH_CD:스케줄코드,
						// STL_SH: 재료매수,
						// YD_TO_LOC_DCSN_MTD(TO위치 결정 방법)
						// STL_NO(재료번호1,2,3,....)
						// FR_YD_STK_BED_NO(적치배드)
						// TO_YD_STK_BED_NO(가이드가 됨)

						ejbConn = new EJBConnector("default" , "CoilCrnSchSeEJB" , this);
						outRecord = (JDTORecord) ejbConn.trx("WrkBookInsertProcTX" , new Class[]{JDTORecord.class} , new Object[]{inRecord});
						sRTN_CD = StringHelper.evl(outRecord.getFieldString("RTN_CD") , "0");
						sYD_WBOOK_ID = StringHelper.evl(outRecord.getFieldString("YD_WBOOK_ID") , "");
						sRTN_MSG = StringHelper.evl(outRecord.getFieldString("RTN_MSG") , "");
						if ("0".equals(sRTN_CD)) {
							szMsg = "작업예약 등록시 ERROR";
							ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
							outRecord.setField("RTN_CD" , "0");
							outRecord.setField("RTN_MSG" , sRTN_MSG);
							return outRecord;
						}
						szMsg = "C열연정정입측Line-In요구 - C열연 정정입측 보급 작업예약  완료!";
						ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.DEBUG);
						/*-----------------------------------------------------*/
					}

					if (Loop_i == 1) {
						FIRST_YD_WBOOK_ID = sYD_WBOOK_ID;
						FIRST_YD_SCH_CD = szYD_SCH_CD;
					}
					// 다음 레코드 추출
					rsGetStock.next();
					recGetVal = rsGetStock.getRecord();
				}
			}

			// if (
			// ((szRcvTcCode.equals("HRYDJ008")) &&
			// ( (szYD_EQP_ID.equals("HGFE01"))
			// ||(szYD_EQP_ID.equals("HHKE01"))
			// ||(szYD_EQP_ID.equals("HEDE01"))
			// ||(szYD_EQP_ID.equals("HHKD01"))
			// ||(szYD_EQP_ID.equals("HEDD01"))
			// )
			// )
			// || (szYD_SCH_CD.substring(2, 4).equals("TC"))
			// ) {
			// // 열연 조업 추출, 대차 작업요구는 스케쥴 기동 안함
			// } else {
			// C증설
			if (((szRcvTcCode.equals("HRYDJ008")) && ( 
					   (szYD_EQP_ID.equals("HBKE01"))
					|| (szYD_EQP_ID.equals("HBKD01"))
					|| (szYD_EQP_ID.equals("HCFE01"))
					|| (szYD_EQP_ID.equals("HCKE01"))
					|| (szYD_EQP_ID.equals("HCKD01"))
					|| (szYD_EQP_ID.equals("HAKE01"))
					|| (szYD_EQP_ID.equals("HAKD01"))
					|| (szYD_EQP_ID.equals("HEDE01"))
					|| (szYD_EQP_ID.equals("HEDD01"))
					|| (szYD_EQP_ID.equals("HGFE01"))
					|| (szYD_EQP_ID.equals("HHKE01")) 
					|| (szYD_EQP_ID.equals("HHKD01"))))
					|| (szYD_SCH_CD.substring(2, 4).equals("TC"))) {
				// 열연 조업 추출, 대차 작업요구는 스케쥴 기동 안함
				ydUtils.putLog(szSessionName , szMethodName , "HRYDJ008 && 열연 조업 추출, 대차 작업요구는 스케쥴 기동 안함" , YdConstant.INFO);
			} else {
				
				//HFL결속장에 대한  스케줄 기동 작업 SKIP				
				if(szYD_SCH_CD.equals("HBFE01UM")||szYD_SCH_CD.equals("HDFE01UM")||szYD_SCH_CD.equals("HFFE01UM")){
					ydUtils.putLog(szSessionName , szMethodName , "&&&&&스케줄 기동처리SKIP 완료&&&&&" , YdConstant.INFO);
				}else{				
					// 스케줄 기동처리
					if (!FIRST_YD_WBOOK_ID.equals("")) {
						inRecordarr = new JDTORecord[1];
	
						inRecordarr[0] = JDTORecordFactory.getInstance().create();
						inRecordarr[0].setField("YD_SCH_CD" , FIRST_YD_SCH_CD);
						inRecordarr[0].setField("YD_WBOOK_ID" , FIRST_YD_WBOOK_ID);
						ejbConn = new EJBConnector("default" , "CoilJspSeEJB" , this);
						outRecord2 = (JDTORecord) ejbConn.trx("trxRunSchedule" , new Class[]{JDTORecord[].class} , new Object[]{inRecordarr});
	
						sRTN_CD = StringHelper.evl(outRecord2.getFieldString("RTN_CD") , "0");
						sRTN_MSG = StringHelper.evl(outRecord2.getFieldString("RTN_MSG") , "");
						ydUtils.putLog(szSessionName , szMethodName , "스케줄 정상기동여부 : " + sRTN_MSG , YdConstant.DEBUG);
						if (!("1".equals(sRTN_CD))) {
							outRecord.setField("RTN_CD" , "0");
							outRecord.setField("RTN_MSG" , sRTN_MSG);
							m_ctx.setRollbackOnly();
							return outRecord;
						}
	
						ydUtils.putLog(szSessionName , szMethodName , "스케줄 기동처리 완료" , YdConstant.INFO);
					}
				}
			}
			
			szMsg = "[C열연정정L2/열연조업] C열연정정입측Line-In요구/정정입측보급Lot편성 수신 TCCODE(" + szRcvTcCode + ")종료"; 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			outRecord.setField("YD_SCH_CD", FIRST_YD_SCH_CD);
			outRecord.setField("YD_WBOOK_ID", FIRST_YD_WBOOK_ID);
			outRecord.setField("RTN_CD", "1");
			outRecord.setField("RTN_MSG" , "C열연정정Line-In요구/C열연 정정보급Lot편성/C열연정정 Take-In요구 수신정상처리)" + sYD_WBOOK_ID);
			return outRecord;

		} catch (Exception e) {
			szMsg = "C열연정정Line-In요구 -C열연 정정입측 보급Lot 편성 Error : "
					+ e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			// outRecord.setField("RTN_CD" , "0");
			// outRecord.setField("RTN_MSG", "조업열연정정지시실적작업관리 에러)");
			// return outRecord;

			throw new DAOException(szMsg);
		}

	} // end of procCHrShearInSupLotComp

	/**
	 * 오퍼레이션명 : C열연 차량스케줄생성
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */

	public int mkY5CarSch(JDTORecord msgRecord) throws JDTOException {
		YdDelegate ydDelegate = new YdDelegate();
		YdCarSchDao ydCarSchDao = new YdCarSchDao();
		// YdDBAssist ydDBAssist = new YdDBAssist();
		YdStkColDao ydStkColDao = new YdStkColDao();
		// JDTORecordSet rsResult = null;
		JDTORecordSet rsStkCol = null;
		JDTORecord recOutTemp = null;
		JDTORecord recInTemp = null;

		int intRtnVal = 0;
		Integer iRtn =null;
		String szMsg = "";
		String szMethodName = "mkY5CarSch";
		String szOperationName = "차량스케줄생성";

		String szWLOC_CD = "";
		// String szYD_GP = "";
		// String szQuery = "";
		String szYD_EQP_ID = "";
		String szTRN_EQP_CD = "";
		String szYD_CAR_USE_GP = "";
		String szSPOS_WLOC_CD = "";
		String szSPOS_YD_PNT_CD = "";
		String szYD_CARLD_LEV_LOC = "";
		String szPNT_DMD_DT = "";
		// String szSP_TRUCK_LOADING_LOC_TP = "";
		String szYD_WBOOK_ID = "";
		String szCAR_NO = "";
		String szCARD_NO = "";
		String szYD_CRN_EQP_ID = "";
		String szYD_SCH_CD = "";
		String szYD_CARLD_STOP_LOC = "";
		String szYD_DIRECT_CARLD_GP = "";
		String szYD_FTMV_COL = "";
		String szTRANS_ORD_DATE = "";
		String szTRANS_ORD_SEQNO = "";
		String szYD_CAR_SCH_ID = "";

		try {

			szMsg = "[차량스케줄생성] 메소드 시작 : 전문내용 보기";
			ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.DEBUG);
			ydUtils.displayRecord(szOperationName , msgRecord);

			// 파라미터 편집
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord , "YD_EQP_ID");
			szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(msgRecord , "TRN_EQP_CD");
			szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(msgRecord , "YD_CAR_USE_GP");
			szWLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord , "WLOC_CD");
			szSPOS_WLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord , "SPOS_WLOC_CD");
			szSPOS_YD_PNT_CD = ydDaoUtils.paraRecChkNull(msgRecord , "SPOS_YD_PNT_CD");
			// szSP_TRUCK_LOADING_LOC_TP = ydDaoUtils.paraRecChkNull(msgRecord,
			// "SP_TRUCK_LOADING_LOC_TP");
			szPNT_DMD_DT = ydDaoUtils.paraRecChkNull(msgRecord , "PNT_DMD_DT");
			szYD_WBOOK_ID = ydDaoUtils.paraRecChkNull(msgRecord , "YD_WBOOK_ID");
			szCAR_NO = ydDaoUtils.paraRecChkNull(msgRecord , "CAR_NO");
			szCARD_NO = ydDaoUtils.paraRecChkNull(msgRecord , "CARD_NO");
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord , "YD_SCH_CD");
			szTRANS_ORD_DATE = ydDaoUtils.paraRecChkNull(msgRecord , "TRANS_ORD_DATE");
			szTRANS_ORD_SEQNO = ydDaoUtils.paraRecChkNull(msgRecord , "TRANS_ORD_SEQNO");

			// 직상차구분 : C - 직상차, Y - 일반야드, B - 대상재 존재하지 않음 ---- 임춘수 2009.04.23 추가
			szYD_DIRECT_CARLD_GP = ydDaoUtils.paraRecChkNull(msgRecord , "YD_DIRECT_CARLD_GP");
			// 이송적치열
			szYD_FTMV_COL = ydDaoUtils.paraRecChkNull(msgRecord , "YD_FTMV_COL");

			szMsg = "[전문 내용] 직상차구분[szYD_DIRECT_CARLD_GP - " + szYD_DIRECT_CARLD_GP + " ]" + ", 이송적치열 [" + szYD_FTMV_COL + "]";
			ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.DEBUG);

			if (szYD_CAR_USE_GP.equals("G")) {
				szYD_CRN_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord , "YD_CRN_EQP_ID");
				szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord , "YD_SCH_CD");
				szYD_CARLD_STOP_LOC = ydDaoUtils.paraRecChkNull(msgRecord , "YD_CARLD_STOP_LOC");
			}

			// 발지개소코드를 변환, 발지개소Point를 변환(출발지 위치)
			rsStkCol = JDTORecordFactory.getInstance().createRecordSet("");
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("WLOC_CD" , szSPOS_WLOC_CD);
			recInTemp.setField("YD_PNT_CD" , szSPOS_YD_PNT_CD);
			intRtnVal = ydStkColDao.getYdStkcol(recInTemp , rsStkCol , 4);
			if (intRtnVal <= 0) {
				if (intRtnVal == 0) {
					szMsg = "data not found";
					ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.WARNING);
				} else if (intRtnVal == -2) {
					szMsg = "parameter error";
					ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
				}
				// return intRtnVal = -1;
			}

			if (intRtnVal > 0) {
				recOutTemp = JDTORecordFactory.getInstance().create();
				recOutTemp.setRecord(rsStkCol.getRecord(0));

				// 열구분을 조회(발지위치)
				szYD_CARLD_LEV_LOC = ydDaoUtils.paraRecChkNull(recOutTemp , "YD_STK_COL_GP");
			} else {
				szMsg = "등록되지 않은 개소코드와 포인트코드입니다. ex) A,B열연 OR 대기장";
				ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.DEBUG);
				szYD_CARLD_LEV_LOC = "";
			}

			szYD_CAR_SCH_ID = ydCarSchDao.getYdCarschId();

			szMsg = "차량스케줄ID 생성 시작 전";
			ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.DEBUG);

			// 차량스케줄INSERT 항목
			recOutTemp = JDTORecordFactory.getInstance().create();
			recOutTemp.setField("YD_CAR_SCH_ID" , szYD_CAR_SCH_ID);
			recOutTemp.setField("REGISTER" , (szMethodName.length() > 10 ? szMethodName.substring(0 , 10) : szMethodName));
			recOutTemp.setField("YD_EQP_WRK_STAT" , "U");
			recOutTemp.setField("YD_EQP_ID" , szYD_EQP_ID);
			recOutTemp.setField("TRN_EQP_CD" , szTRN_EQP_CD);
			recOutTemp.setField("YD_CAR_USE_GP" , szYD_CAR_USE_GP);

			if (szYD_CAR_USE_GP.equals("G")) {
				recOutTemp.setField("CAR_KIND" , "TR");
			} else {
				recOutTemp.setField("CAR_KIND" , "PT");
			}

			recOutTemp.setField("WLOC_CD" , szWLOC_CD);
			// 2009.05.12 임춘수 추가
			recOutTemp.setField("SPOS_WLOC_CD" , szWLOC_CD);
			// 2009.11.17 임춘수 추가
			recOutTemp.setField("YD_BAYIN_WO_SEQ" , YdConstant.YD_BAYIN_WO_SEQ_DEFAULT);

			recOutTemp.setField("YD_CARLD_WRK_BOOK_ID" , szYD_WBOOK_ID);
			recOutTemp.setField("CAR_NO" , szCAR_NO);
			recOutTemp.setField("CARD_NO" , szCARD_NO);
			recOutTemp.setField("YD_CAR_PROG_STAT" , "1");// 상차출발상태
			recOutTemp.setField("YD_CARLD_LEV_DT" , YdUtils.getCurDate("yyyyMMddHHmmss"));// 상차출발일시
			recOutTemp.setField("TRANS_ORD_DATE" , szTRANS_ORD_DATE);
			recOutTemp.setField("TRANS_ORD_SEQNO" , szTRANS_ORD_SEQNO);

			if (szYD_CAR_USE_GP.equals("G")) {
				// 차량상차정지위치를 등록한다.
				recOutTemp.setField("YD_CARLD_STOP_LOC" , szYD_CARLD_STOP_LOC);
				recOutTemp.setField("YD_CARLD_ARR_DT" , YdUtils.getCurDate("yyyyMMddHHmmss"));
				recOutTemp.setField("YD_CAR_PROG_STAT" , "2");// 상차도착상태
			} else if (szYD_CAR_USE_GP.equals("L")) {
				if (szYD_DIRECT_CARLD_GP.equals("B")) {
					recOutTemp.setField("YD_CARLD_LEV_LOC" , szYD_CARLD_LEV_LOC);
					/*
					 * 대상재가 없는 경우에도 0000 포인트코드를 전송한다.
					 */
					// 상차정지위치
					recOutTemp.setField("YD_PNT_CD1" , "0000");
					// 상차point지시일시
					recOutTemp.setField("YD_CARLD_PNT_WO_DT" , YdUtils.getCurDate("yyyyMMddHHmmss"));
					szMsg = "대상재가 없는 경우에는 포인트코드[0000]로 수정 처리 ";
					ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.DEBUG);
					// }
				}
				/* 구내운송 - 임춘수 2009.04.23 추가 */
				else if (szYD_FTMV_COL.equals("")) {
					recOutTemp.setField("YD_CARLD_STOP_LOC" , szYD_SCH_CD.substring(0 , 6)); // 작업예약에서 생성한 스케줄코드값을
																								// 잘라쓴다.
					recOutTemp.setField("YD_CARLD_LEV_LOC" , szYD_CARLD_LEV_LOC); // 대기장이
																					// 아니라면
																					// 검색되기때문에
																					// 출발위치
				} else {
					recOutTemp.setField("YD_CARLD_STOP_LOC" , szYD_FTMV_COL + "01"); // 임의 값을 먼저 설정
					recOutTemp.setField("YD_CARLD_LEV_LOC" , szYD_CARLD_LEV_LOC);
				}

			}

			// 차량스케줄 등록
			intRtnVal = ydCarSchDao.insYdCarsch(recOutTemp);
			if (intRtnVal == -2) {
				szMsg = "parameter error";
				ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
				throw new DAOException("<mkY1CarSch> insYdCarsch처리중 parameter error");
			}

			szMsg = "차량스케줄ID 생성 완료";
			ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.DEBUG);

			if (szYD_CAR_USE_GP.equals("L")) { // 구내운송
				if (!szYD_DIRECT_CARLD_GP.equals("B")) {
					// 소재차량정지Point요구 호출
					// record 생성
					recInTemp = JDTORecordFactory.getInstance().create();
					// JMS TC CODE
					recInTemp.setField("JMS_TC_CD" , "YDYDJ630");
					// 운송장비코드
					recInTemp.setField("TRN_EQP_CD" , szTRN_EQP_CD);
					// 개소코드
					recInTemp.setField("WLOC_CD" , szWLOC_CD);
					// 운송작업영공구분코드
					recInTemp.setField("TRN_WRK_FULLVOID_GP" , "E");
					// 포인트요구일시
					recInTemp.setField("PNT_DMD_DT" , szPNT_DMD_DT);
					recInTemp.setField("YD_SCH_CD" , szYD_SCH_CD);
					
					// 전문 송신
					//ydDelegate.sendMsg(recInTemp);
					
					EJBConnector ejbConn = new EJBConnector("default" , "CarMvHdSeEJB" , this);
					iRtn =(Integer)ejbConn.trx("procMatlCarArrPntReq" , new Class[]{JDTORecord.class} , new Object[]{recInTemp});
					if( iRtn.intValue() != YdConstant.RETN_INT_SUCCESS.intValue() ) {			//성공
						return intRtnVal = -1;
					}
					
					szMsg = "소재차량정지Point요구 호출 완료";
					ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.DEBUG);
				} else {

					/*
					 * 대상재가 없는 경우에도 0000 포인트코드를 전송한다.
					 */

					// EJB CALL 변경
					szMsg = "대상재가 없는 경우에는 0000포인트코드를 전송 시작";
					ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.DEBUG);

					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("JMS_TC_CD" , "YDYDJ630");
					recInTemp.setField("TRN_EQP_CD" , szTRN_EQP_CD);

					// ydDelegate.sendMsg(recInTemp);

					EJBConnector ejbConn = new EJBConnector("default" , "CarMvHdSeEJB" , this);
					iRtn =(Integer)ejbConn.trx("procMatlCarArrPntReq" , new Class[]{JDTORecord.class} , new Object[]{recInTemp});
					if( iRtn.intValue() != YdConstant.RETN_INT_SUCCESS.intValue() ) {			//성공
						return intRtnVal = -1;
					}
					
					szMsg = "대상재가 없는 경우에는 0000포인트코드를 전송 완료";
					ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.DEBUG);

				}
			} else { // 출하관리
				// 크레인 스케줄 송신
				recInTemp = JDTORecordFactory.getInstance().create();

				recInTemp.setField("JMS_TC_CD" , "YDYDJ509"); // JMS TC CODE
																// (C열연코일)
				recInTemp.setField("YD_SCH_CD" , szYD_SCH_CD); // 스케줄코드
				recInTemp.setField("YD_EQP_ID" , szYD_CRN_EQP_ID); // 설비ID

				szMsg = "크레인 스케줄 송신";
				ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.DEBUG);
				ydUtils.displayRecord(szOperationName , recInTemp);
				// 전문 송신
				ydDelegate.sendMsg(recInTemp);
			}

		} catch (Exception e) {

			szMsg = "C열연 차량스케줄 생성 중 Error:" + e.getMessage();
			ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
			throw new DAOException(e);
		}

		szMsg = "C열연  차량스케줄 생성 (" + szMethodName + ") 완료";
		ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.DEBUG);
		return intRtnVal = 1;
	} // end of mkY1CarSch()

	/**
	 * 오퍼레이션명 : 설비구분변경
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public int ydEqpGpConversion(JDTORecord msgRecord, JDTORecord resultRecord)
			throws JDTOException {
		String szMsg = null;
		String szMethodName = "ydEqpGpConversion";
		String szOperationName = "설비구분변경";
		String szEQP_GP = null;
		String szYD_EQP_ID = null;
		String szYD_STK_BED_NO = null;
		int intRtnVal = 0;

		try {
			/* 열연조업화면에서 대상재를 선택하여 C열연정정입측Line-In요구 시 */
			ydUtils.displayRecord(szOperationName, msgRecord);

			szEQP_GP = ydDaoUtils.paraRecChkNull(msgRecord, "EQP_GP");
			// #1HOT FINAL 입측(G)
			if (szEQP_GP.equals("ECC05")) {
				szYD_EQP_ID = "HGFE01";
				szYD_STK_BED_NO = "05";
			} else if (szEQP_GP.equals("ECC04")) {
				szYD_EQP_ID = "HGFE01";
				szYD_STK_BED_NO = "04";
			} else if (szEQP_GP.equals("ECC03")) {
				szYD_EQP_ID = "HGFE01";
				szYD_STK_BED_NO = "03";
			} else if (szEQP_GP.equals("ECC02")) {
				szYD_EQP_ID = "HGFE01";
				szYD_STK_BED_NO = "02";
			} else if (szEQP_GP.equals("ECC01")) {
				szYD_EQP_ID = "HGFE01";
				szYD_STK_BED_NO = "01";

				// #3HOT FINAL 출측
			} else if (szEQP_GP.equals("DCC01")) {
				szYD_EQP_ID = "HGFD01";
				szYD_STK_BED_NO = "01";
			} else if (szEQP_GP.equals("DCC02")) {
				szYD_EQP_ID = "HGFD01";
				szYD_STK_BED_NO = "02";
			} else if (szEQP_GP.equals("DCC03")) {
				szYD_EQP_ID = "HGFD01";
				szYD_STK_BED_NO = "03";
			} else if (szEQP_GP.equals("DCC04")) {
				szYD_EQP_ID = "HGFD01";
				szYD_STK_BED_NO = "04";
			} else if (szEQP_GP.equals("DCC05")) {
				szYD_EQP_ID = "HGFD01";
				szYD_STK_BED_NO = "05";
			} else if (szEQP_GP.equals("DCC06")) {
				szYD_EQP_ID = "HGFD01";
				szYD_STK_BED_NO = "06";
			} else if (szEQP_GP.equals("DCC07")) {
				szYD_EQP_ID = "HGFD01";
				szYD_STK_BED_NO = "07";
			} else if (szEQP_GP.equals("DCC08")) {
				szYD_EQP_ID = "HGFD01";
				szYD_STK_BED_NO = "08";
			} else if (szEQP_GP.equals("DCC09")) {
				szYD_EQP_ID = "HGFD01";
				szYD_STK_BED_NO = "09";
			} else if (szEQP_GP.equals("DCC10")) {
				szYD_EQP_ID = "HGFD01";
				szYD_STK_BED_NO = "10";
			} else if (szEQP_GP.equals("DCC11")) {
				szYD_EQP_ID = "HGFD01";
				szYD_STK_BED_NO = "11";

				// #2HOT FINAL 입측
			} else if (szEQP_GP.equals("K2-01")) {
				szYD_EQP_ID = "HFFE01";
				szYD_STK_BED_NO = "01";
			} else if (szEQP_GP.equals("K2-02")) {
				szYD_EQP_ID = "HFFE01";
				szYD_STK_BED_NO = "02";
			} else if (szEQP_GP.equals("K2-03")) {
				szYD_EQP_ID = "HFFE01";
				szYD_STK_BED_NO = "03";
			} else if (szEQP_GP.equals("K2-04")) {
				szYD_EQP_ID = "HFFE01";
				szYD_STK_BED_NO = "04";
			} else if (szEQP_GP.equals("K2-05")) {
				szYD_EQP_ID = "HFFE01";
				szYD_STK_BED_NO = "05";
			} else if (szEQP_GP.equals("K2-06")) {
				szYD_EQP_ID = "HFFE01";
				szYD_STK_BED_NO = "06";
			} else if (szEQP_GP.equals("K2-07")) {
				szYD_EQP_ID = "HFFE01";
				szYD_STK_BED_NO = "07";
			} else if (szEQP_GP.equals("K2-08")) {
				szYD_EQP_ID = "HFFE01";
				szYD_STK_BED_NO = "08";
			} else if (szEQP_GP.equals("K2-09")) {
				szYD_EQP_ID = "HFFE01";
				szYD_STK_BED_NO = "09";
			} else if (szEQP_GP.equals("K2-10")) {
				szYD_EQP_ID = "HFFE01";
				szYD_STK_BED_NO = "10";
			} else if (szEQP_GP.equals("K2-11")) {
				szYD_EQP_ID = "HFFE01";
				szYD_STK_BED_NO = "11";
			} else if (szEQP_GP.equals("K2-12")) {
				szYD_EQP_ID = "HFFE01";
				szYD_STK_BED_NO = "12";

				// #3HOT FINAL 입측
			} else if (szEQP_GP.equals("K3-01")) {
				szYD_EQP_ID = "HDFE01";
				szYD_STK_BED_NO = "01";
			} else if (szEQP_GP.equals("K3-02")) {
				szYD_EQP_ID = "HDFE01";
				szYD_STK_BED_NO = "02";
			} else if (szEQP_GP.equals("K3-03")) {
				szYD_EQP_ID = "HDFE01";
				szYD_STK_BED_NO = "03";
			} else if (szEQP_GP.equals("K3-04")) {
				szYD_EQP_ID = "HDFE01";
				szYD_STK_BED_NO = "04";
			} else if (szEQP_GP.equals("K3-05")) {
				szYD_EQP_ID = "HDFE01";
				szYD_STK_BED_NO = "05";
			} else if (szEQP_GP.equals("K3-06")) {
				szYD_EQP_ID = "HDFE01";
				szYD_STK_BED_NO = "06";
			} else if (szEQP_GP.equals("K3-07")) {
				szYD_EQP_ID = "HDFE01";
				szYD_STK_BED_NO = "07";
			} else if (szEQP_GP.equals("K3-08")) {
				szYD_EQP_ID = "HDFE01";
				szYD_STK_BED_NO = "08";
			} else if (szEQP_GP.equals("K3-09")) {
				szYD_EQP_ID = "HDFE01";
				szYD_STK_BED_NO = "09";
			} else if (szEQP_GP.equals("K3-10")) {
				szYD_EQP_ID = "HDFE01";
				szYD_STK_BED_NO = "10";
			} else if (szEQP_GP.equals("K3-11")) {
				szYD_EQP_ID = "HDFE01";
				szYD_STK_BED_NO = "11";
			} else if (szEQP_GP.equals("K3-12")) {
				szYD_EQP_ID = "HDFE01";
				szYD_STK_BED_NO = "12";
			} else if (szEQP_GP.equals("K3-13")) {
				szYD_EQP_ID = "HDFE01";
				szYD_STK_BED_NO = "13";
			} else if (szEQP_GP.equals("K3-14")) {
				szYD_EQP_ID = "HDFE01";
				szYD_STK_BED_NO = "14";
			} else if (szEQP_GP.equals("K3-15")) {
				szYD_EQP_ID = "HDFE01";
				szYD_STK_BED_NO = "15";
			} else if (szEQP_GP.equals("K3-16")) {
				szYD_EQP_ID = "HDFE01";
				szYD_STK_BED_NO = "16";
			} else if (szEQP_GP.equals("K3-17")) {
				szYD_EQP_ID = "HDFE01";
				szYD_STK_BED_NO = "17";
			} else if (szEQP_GP.equals("K3-18")) {
				szYD_EQP_ID = "HDFE01";
				szYD_STK_BED_NO = "18";
			} else if (szEQP_GP.equals("K3-19")) {
				szYD_EQP_ID = "HDFE01";
				szYD_STK_BED_NO = "19";
			} else if (szEQP_GP.equals("K3-20")) {
				szYD_EQP_ID = "HDFE01";
				szYD_STK_BED_NO = "20";

				// SPM1 입측
			} else if (szEQP_GP.equals("ECC01")) {
				szYD_EQP_ID = "HHKE01";
				szYD_STK_BED_NO = "01";
			} else if (szEQP_GP.equals("ECC02")) {
				szYD_EQP_ID = "HHKE01";
				szYD_STK_BED_NO = "02";
			} else if (szEQP_GP.equals("ECC03")) {
				szYD_EQP_ID = "HHKE01";
				szYD_STK_BED_NO = "03";
			} else if (szEQP_GP.equals("ECC04")) {
				szYD_EQP_ID = "HHKE01";
				szYD_STK_BED_NO = "04";
			} else if (szEQP_GP.equals("ECC05")) {
				szYD_EQP_ID = "HHKE01";
				szYD_STK_BED_NO = "05";
			} else if (szEQP_GP.equals("ECC06")) {
				szYD_EQP_ID = "HHKE01";
				szYD_STK_BED_NO = "06";

				// SPM1 출측
			} else if (szEQP_GP.equals("DCC01")) {
				szYD_EQP_ID = "HHKD01";
				szYD_STK_BED_NO = "01";
			} else if (szEQP_GP.equals("DCC02")) {
				szYD_EQP_ID = "HHKD01";
				szYD_STK_BED_NO = "02";
			} else if (szEQP_GP.equals("DCC03")) {
				szYD_EQP_ID = "HHKD01";
				szYD_STK_BED_NO = "03";
			} else if (szEQP_GP.equals("DCC04")) {
				szYD_EQP_ID = "HHKD01";
				szYD_STK_BED_NO = "04";
			} else if (szEQP_GP.equals("DCC05")) {
				szYD_EQP_ID = "HHKD01";
				szYD_STK_BED_NO = "05";
			} else if (szEQP_GP.equals("DCC06")) {
				szYD_EQP_ID = "HHKD01";
				szYD_STK_BED_NO = "06";
			} else if (szEQP_GP.equals("DCC07")) {
				szYD_EQP_ID = "HHKD01";
				szYD_STK_BED_NO = "07";
			} else if (szEQP_GP.equals("DCC08")) {
				szYD_EQP_ID = "HHKD01";
				szYD_STK_BED_NO = "08";
			} else if (szEQP_GP.equals("DCC09")) {
				szYD_EQP_ID = "HHKD01";
				szYD_STK_BED_NO = "09";
			} else if (szEQP_GP.equals("DCC10")) {
				szYD_EQP_ID = "HHKD01";
				szYD_STK_BED_NO = "10";
			} else if (szEQP_GP.equals("DCC11")) {
				szYD_EQP_ID = "HHKD01";
				szYD_STK_BED_NO = "11";

				// SPM2 입측
			} else if (szEQP_GP.equals("ECC01")) {
				szYD_EQP_ID = "HEDE01";
				szYD_STK_BED_NO = "01";
			} else if (szEQP_GP.equals("ECC02")) {
				szYD_EQP_ID = "HEDE01";
				szYD_STK_BED_NO = "02";
			} else if (szEQP_GP.equals("ECC03")) {
				szYD_EQP_ID = "HEDE01";
				szYD_STK_BED_NO = "03";
			} else if (szEQP_GP.equals("ECC04")) {
				szYD_EQP_ID = "HEDE01";
				szYD_STK_BED_NO = "04";
			} else if (szEQP_GP.equals("ECC05")) {
				szYD_EQP_ID = "HEDE01";
				szYD_STK_BED_NO = "05";
			} else if (szEQP_GP.equals("ECC06")) {
				szYD_EQP_ID = "HEDE01";
				szYD_STK_BED_NO = "06";

				// SPM2 출측
			} else if (szEQP_GP.equals("DCC01")) {
				szYD_EQP_ID = "HEDD01";
				szYD_STK_BED_NO = "01";
			} else if (szEQP_GP.equals("DCC02")) {
				szYD_EQP_ID = "HEDD01";
				szYD_STK_BED_NO = "02";
			} else if (szEQP_GP.equals("DCC03")) {
				szYD_EQP_ID = "HEDD01";
				szYD_STK_BED_NO = "03";
			} else if (szEQP_GP.equals("DCC04")) {
				szYD_EQP_ID = "HEDD01";
				szYD_STK_BED_NO = "04";
			} else if (szEQP_GP.equals("DCC05")) {
				szYD_EQP_ID = "HEDD01";
				szYD_STK_BED_NO = "05";
			} else if (szEQP_GP.equals("DCC06")) {
				szYD_EQP_ID = "HEDD01";
				szYD_STK_BED_NO = "06";
			} else if (szEQP_GP.equals("DCC07")) {
				szYD_EQP_ID = "HEDD01";
				szYD_STK_BED_NO = "07";
			} else if (szEQP_GP.equals("DCC08")) {
				szYD_EQP_ID = "HEDD01";
				szYD_STK_BED_NO = "08";
			} else if (szEQP_GP.equals("DCC09")) {
				szYD_EQP_ID = "HEDD01";
				szYD_STK_BED_NO = "09";
			} else if (szEQP_GP.equals("DCC10")) {
				szYD_EQP_ID = "HEDD01";
				szYD_STK_BED_NO = "10";
			} else if (szEQP_GP.equals("DCC11")) {
				szYD_EQP_ID = "HEDD01";
				szYD_STK_BED_NO = "11";
			}

			resultRecord.addField("YD_EQP_ID", szYD_EQP_ID);
			resultRecord.addField("YD_STK_BED_NO", szYD_STK_BED_NO);

		} catch (Exception e) {
			szMsg = "C열연 정정설비구분 변경!! Error!! " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return intRtnVal = -1;
		}
		return intRtnVal = 1;
	} // end of ydEqpGpConversion

	/**
	 * 오퍼레이션명 : 설비구분변경NEW
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public int ydWordProcConversion(JDTORecord msgRecord,
			JDTORecord resultRecord) throws JDTOException {
		String szMsg = null;
		String szMethodName = "ydWordProcConversion";
		String szOperationName = "설비구분변경NEW";
		String szWORD_PROC = null;
		String szYD_EQP_ID = null;
		String szEQP_CD = null;
		int intRtnVal = 0;

		try {
			/* 열연조업화면에서 대상재를 선택하여 C열연정정입측Line-In요구 시 */
			ydUtils.displayRecord(szOperationName, msgRecord);

			szWORD_PROC = ydDaoUtils.paraRecChkNull(msgRecord, "WORD_PROC");

			if (szWORD_PROC.equals("HK") || szWORD_PROC.equals("HR")) { // SPM1
																		// 보급존
				szYD_EQP_ID = "HHKE01";
				szEQP_CD = "SPM1";
			} else if (szWORD_PROC.equals("HH")) { // SPM1 추출존
				szYD_EQP_ID = "HHKD01";
				szEQP_CD = "SPM1";
			} else if (szWORD_PROC.equals("EK") || szWORD_PROC.equals("ER")) { // SPM2
																				// 보급존
				szYD_EQP_ID = "HEDE01";
				szEQP_CD = "SPM2";
			} else if (szWORD_PROC.equals("EH")) { // SPM2 추출존
				szYD_EQP_ID = "HEDD01";
				szEQP_CD = "SPM2";
				// C증설
			} else if (szWORD_PROC.equals("CK") || szWORD_PROC.equals("CR")) { // SPM3
																				// 보급존
				szYD_EQP_ID = "HCKE01";
				szEQP_CD = "SPM3";
			} else if (szWORD_PROC.equals("BK") || szWORD_PROC.equals("BR")) { // SPM4
																				// 보급존
				szYD_EQP_ID = "HBKE01";
				szEQP_CD = "SPM4";
			} else if (szWORD_PROC.equals("AK") || szWORD_PROC.equals("AR")) { // SPM5
																				// 보급존
				szYD_EQP_ID = "HAKE01";
				szEQP_CD = "SPM5";
			} else if (szWORD_PROC.equals("GH")) { // HFL1
				szYD_EQP_ID = "HGFE01";
				szEQP_CD = "HFL1";
			} else if (szWORD_PROC.equals("FH")) { // HFL2
				szYD_EQP_ID = "HFFE01";
				szEQP_CD = "HFL2";
			} else if (szWORD_PROC.equals("DH")) { // HFL3
				szYD_EQP_ID = "HDFE01";
				szEQP_CD = "HFL3";
			} else if (szWORD_PROC.equals("CH")) { // HFL4
				szYD_EQP_ID = "HCFE01";
				szEQP_CD = "HFL4";
			} else if (szWORD_PROC.equals("BH")) { // HFL5  A동 -> B동 변경
				szYD_EQP_ID = "HBFE01";
				szEQP_CD = "HFL5";
			}

			resultRecord.addField("YD_EQP_ID", szYD_EQP_ID);
			resultRecord.addField("EQP_CD", szEQP_CD);

		} catch (Exception e) {
			szMsg = "C열연 정정설비구분 변경!! Error!! " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return intRtnVal = -1;
		}
		return intRtnVal = 1;
	} // end of ydEqpGpConversion

	// /**
	// * 오퍼레이션명 : C열연정정보급 작업예약
	// *
	// * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	// * @param msgRecord
	// * @return
	// * @throws JDTOException
	// */
	// public void procWrkBook(JDTORecord msgRecord)throws JDTOException {
	//
	// //스케줄기준 DAO
	// YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
	// //작업예약 재료 DAO
	// YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
	// //공용 DAO METHOD
	// YdDaoUtils ydDaoUtils = new YdDaoUtils();
	// //공용 METHOD
	// YdUtils ydutils = new YdUtils();
	//
	// //리턴값(boolean)
	// boolean blnRtnVal = false;
	// //리턴값(int)
	// int intRtnVal = 0;
	// //메세지
	// String szMsg = "";
	// //METHOD명
	// String szMethodName = "procWrkBook ";
	// //사용자
	// String szUser = "SYSTEM";
	//
	// //레코드 선언
	// JDTORecord recPara = null;
	// JDTORecord recStkPara = null;
	// //레코드셋 선언
	// JDTORecordSet rsResult = null;
	//
	// //설비ID(열구분)
	// String szYD_STK_COL_GP = null;
	// //적치BED번호
	// String szYD_STK_BED_NO = null;
	// //재료매수(int)
	// int intMtlCnt = 0;
	// //재료번호
	// String szSTL_NO = null;
	// //권상모음순서
	// String szYD_UP_COLL_SEQ = null;
	// //스케줄코드
	// String szYD_SCH_CD = null;
	// //스케줄우선순위
	// String szYD_SCH_PRIOR = null;
	// //스케줄 금지 유무
	// String szYD_SCH_PROH_EXN = null;
	// //작업크레인
	// String szYD_WRK_CRN = null;
	// //작업크레인우선순위
	// String szYD_WRK_CRN_PRIOR = null;
	// //대체크레인유무
	// String szYD_ALT_CRN_YN = null;
	// //대체크레인
	// String szYD_ALT_CRN = null;
	// //대체크레인우선순위
	// String szYD_ALT_CRN_PRIOR = null;
	// //선택크레인
	// String szCrn = null;
	// //작업예약ID
	// String szYD_WBOOK_ID = null;
	//
	// try {
	//
	// //받은 전문 편집
	// //스케줄코드
	// szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
	// if(szYD_SCH_CD.equals("")){
	// szMsg = "[전문 이상] 스케줄코드가 없습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	// }
	// //적치열구분
	// szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
	// if(szYD_STK_COL_GP.equals("")){
	// szMsg = "[전문 이상] 적치열구분이 없습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	// }
	//
	// //적치BED번호
	// szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
	//
	// //재료매수
	// intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_LINE_IN_SH");
	// if(intMtlCnt == 0){
	// szMsg = "[전문 이상] 재료매수가 없습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	// }
	//
	// //재료번호
	// szSTL_NO = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO");
	// if(szSTL_NO.equals("")){
	// szMsg = "[전문 이상] " + szSTL_NO + " 재료 번호가 없습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	// }
	//
	// //권상모음순서
	// szYD_UP_COLL_SEQ = ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_COLL_SEQ");
	// if(szYD_UP_COLL_SEQ.equals("")){
	// szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ + ")에 대한 권상모음순서가 없습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	// }
	//
	// //리턴 recordSet 생성
	// rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	//
	// //스케줄 기준 체크
	// blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
	// if(!blnRtnVal) return ;
	//
	// //레코드 추출
	// rsResult.first();
	// recPara = rsResult.getRecord();
	//
	// //스케줄CD 체크
	// //스케줄 금지 유무
	// szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara,
	// "YD_SCH_PROH_EXN");
	// //작업크레인
	// szYD_WRK_CRN = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
	// // 작업크레인우선순위
	// szYD_WRK_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara,
	// "YD_WRK_CRN_PRIOR");
	// //대체크레인유무
	// szYD_ALT_CRN_YN = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
	// //대체크레인
	// szYD_ALT_CRN = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
	// // 대체크레인우선순위
	// szYD_ALT_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara,
	// "YD_ALT_CRN_PRIOR");
	//
	// //스케줄 금지 유무가 "Y"이면 처리를 중지하고 유스케이스를 종료한다.
	// if(szYD_SCH_PROH_EXN.equals("Y")){
	// szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	// }
	//
	// //작업크레인 설비 상태 체크
	// blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);
	//
	// //작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
	// if(!blnRtnVal){
	//
	// szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	//
	// //대체크레인의 유무를 체크한다.
	// //대체크레인이 없으면 에러 리턴
	// if(!szYD_ALT_CRN_YN.equals("Y")){
	// szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	// }
	//
	// //대체크레인이 있으면 대체크레인 설비 상태 체크
	// blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
	// //대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
	// if(!blnRtnVal){
	// szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	// } else {
	// //대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
	// szCrn = szYD_ALT_CRN;
	// szYD_SCH_PRIOR = szYD_ALT_CRN_PRIOR;
	// }
	// } else {
	// //작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
	// szCrn = szYD_WRK_CRN;
	// szYD_SCH_PRIOR = szYD_WRK_CRN_PRIOR;
	// }
	//
	// //다른 작업예약에 재료가 등록되어있는지 체크한다.
	// blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO);
	// if(!blnRtnVal) return ;
	//
	// //컨베어 BED SHIFT
	// // blnRtnVal = YdCommonUtils.setShiftStkBed(szYD_STK_COL_GP);
	// // if( !blnRtnVal ) return ;
	//
	// //리턴 recordSet 생성
	// rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	//
	// //작업예약ID 생성
	// blnRtnVal = getYdWbookId(rsResult);
	// if(!blnRtnVal) return ;
	//
	// //레코드추출
	// rsResult.first();
	// recPara = rsResult.getRecord();
	// //작업예약ID
	// szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");
	//
	// //INSERT 항목 record 생성
	// recPara = JDTORecordFactory.getInstance().create();
	// //야드구분
	// String szYD_GP = szYD_SCH_CD.substring(0, 1);
	// //동구분
	// String szYD_BAY_GP = szYD_SCH_CD.substring(1, 2);
	//
	// //INSERT할 항목 SET
	// recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
	// recPara.setField("YD_GP", szYD_GP);
	// recPara.setField("YD_BAY_GP", szYD_BAY_GP);
	// recPara.setField("YD_SCH_CD", szYD_SCH_CD);
	// recPara.setField("YD_SCH_PRIOR", szYD_SCH_PRIOR);
	// recPara.setField("YD_AIM_YD_GP", szYD_GP);
	// recPara.setField("YD_AIM_BAY_GP", szYD_BAY_GP);
	// /*
	// * 업무 Desc : 1. 열연조업화면으로부터 대상재를 선택해서 직접 작업요구를 함으로 베드정보를 작업요구 시 전송
	// * 2.정정L2에서 호출된 경우에는 작업요구 시 베드 정보를 사용하지 않음
	// * 수정자 : 임춘수
	// * 일자 : 2009.06.23
	// * 구현 : 베드정보가 존재하는 경우에는 야드To위치결정방법(F), 야드To위치Guide를 업데이트 처리함
	// */
	// if( !szYD_STK_BED_NO.equals("") ){
	// //야드To위치결정방법
	// recPara.setField("YD_TO_LOC_DCSN_MTD", "F");
	// //야드To위치Guide
	// recPara.setField("YD_TO_LOC_GUIDE", szYD_STK_COL_GP + szYD_STK_BED_NO);
	// }
	// recPara.setField("REGISTER", szUser);
	//
	// //작업예약 INSERT
	// intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
	// if(intRtnVal < 1){
	// szMsg = "작업예약 데이터 등록 중 에러";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// //return ;
	// throw new DAOException(szMsg);
	// }
	//
	//
	// //조회항목 record 생성
	// recPara = JDTORecordFactory.getInstance().create();
	//
	// recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
	// recPara.setField("REGISTER", szUser);
	//
	// //리턴 recordSet 생성
	// rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	// //재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
	// intRtnVal = this.chkGetStlStkLyr(szSTL_NO, "C", rsResult);
	// if(intRtnVal != 1){
	// //return ;
	// throw new DAOException("재료번호에 해당하는 적치중 확인 중 에러");
	// }
	//
	// //레코드추출
	// rsResult.first();
	// recStkPara = rsResult.getRecord();
	//
	// //재료번호
	// recPara.setField("STL_NO", szSTL_NO);
	// //적치열구분
	// recPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(recStkPara,
	// "YD_STK_COL_GP"));
	// //적치BED번호
	// recPara.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(recStkPara,
	// "YD_STK_BED_NO"));
	// //적치단번호
	// recPara.setField("YD_STK_LYR_NO", ydDaoUtils.paraRecChkNull(recStkPara,
	// "YD_STK_LYR_NO"));
	// //권상모음순서
	// recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ);
	//
	// // 작업예약재료 테이블에 등록한다.
	// intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
	//
	// if(intRtnVal < 1){
	// szMsg = "작업예약재료 데이터 등록 중 에러";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// throw new DAOException(szMsg);
	// }
	// msgRecord.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
	//
	// }catch(DAOException e){
	// szMsg = "C열연 정정입측 Line-In 작업요구 처리중 Error : " + e.getMessage();
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// throw e;
	// } catch(Exception e){
	// szMsg = "C열연 정정입측 Line-In 작업요구 처리중 Error : " + e.getMessage();
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// throw new DAOException(e);
	// }
	//
	//
	// } // end of procWrkBook ()
	//
	//
	//
	//
	//
	//
	//
	//

	// /**
	// * 오퍼레이션명 : C열연정정입측Line-In 작업요구 (YDYDJ252)
	// *
	// * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	// * @param msgRecord
	// * @return
	// * @throws JDTOException
	// */
	// public void procR3ShearInLineInReq(JDTORecord msgRecord)throws
	// JDTOException {
	//
	// //스케줄기준 DAO
	// YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
	// //작업예약 재료 DAO
	// YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
	// //공용 DAO METHOD
	// YdDaoUtils ydDaoUtils = new YdDaoUtils();
	// //공용 METHOD
	// YdUtils ydutils = new YdUtils();
	//
	// //리턴값(boolean)
	// boolean blnRtnVal = false;
	// //리턴값(int)
	// int intRtnVal = 0;
	// //메세지
	// String szMsg = "";
	// //METHOD명
	// String szMethodName = "procR3ShearInLineInReq ";
	// //사용자
	// String szUser = "SYSTEM";
	//
	// //레코드 선언
	// JDTORecord recPara = null;
	// JDTORecord recStkPara = null;
	// //레코드셋 선언
	// JDTORecordSet rsResult = null;
	//
	// //설비ID(열구분)
	// String szYD_STK_COL_GP = null;
	// //적치BED번호
	// String szYD_STK_BED_NO = null;
	// //재료매수(int)
	// int intMtlCnt = 0;
	// //재료번호
	// String[] szSTL_NO = null;
	// //권상모음순서
	// String[] szYD_UP_COLL_SEQ = null;
	// //스케줄코드
	// String szYD_SCH_CD = null;
	// //스케줄우선순위
	// String szYD_SCH_PRIOR = null;
	// //스케줄 금지 유무
	// String szYD_SCH_PROH_EXN = null;
	// //작업크레인
	// String szYD_WRK_CRN = null;
	// //작업크레인우선순위
	// String szYD_WRK_CRN_PRIOR = null;
	// //대체크레인유무
	// String szYD_ALT_CRN_YN = null;
	// //대체크레인
	// String szYD_ALT_CRN = null;
	// //대체크레인우선순위
	// String szYD_ALT_CRN_PRIOR = null;
	// //선택크레인
	// String szCrn = null;
	// //작업예약ID
	// String szYD_WBOOK_ID = null;
	//
	//
	// //TC CODE 추출
	// String szRcvTcCode = ydUtils.getTcCode(msgRecord);
	// //에러 리턴
	// if(szRcvTcCode == null){
	//
	// szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" +
	// szRcvTcCode + ")";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	//
	// }
	// //TC CODE DISPLAY
	// if(bDebugFlag){
	//
	// szMsg = "전문수신 : TCCODE=" + szRcvTcCode ;
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	//
	// }
	//
	// try {
	//
	// //받은 전문 편집
	// //스케줄코드
	// szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
	// if(szYD_SCH_CD.equals("")){
	//
	// szMsg = "[전문 이상] 스케줄코드가 없습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	//
	// }
	// //적치열구분
	// szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
	// if(szYD_STK_COL_GP.equals("")){
	//
	// szMsg = "[전문 이상] 적치열구분이 없습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	//
	// }
	// //적치BED번호
	// szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
	// // if(szYD_STK_BED_NO.equals("")){
	// //
	// // szMsg = "[전문 이상] 적치BED번호가 없습니다.";
	// // ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// // return ;
	// //
	// // }
	// //재료매수
	// intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_LINE_IN_SH");
	// if(intMtlCnt == 0){
	//
	// szMsg = "[전문 이상] 재료매수가 없습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	//
	// }
	//
	// //재료번호
	// szSTL_NO = new String[intMtlCnt + 1];
	// //권상모음순서
	// szYD_UP_COLL_SEQ = new String[intMtlCnt + 1];
	//
	// //재료번호, 권상모음순서
	// for(int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
	//
	// //재료번호
	// szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO" +
	// Loop_i);
	// if(szSTL_NO[Loop_i].equals("")){
	//
	// szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	//
	// }
	// //권상모음순서
	// szYD_UP_COLL_SEQ[Loop_i] =
	// ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_COLL_SEQ" + Loop_i);
	// if(szYD_UP_COLL_SEQ[Loop_i].equals("")){
	//
	// szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i] +
	// ")에 대한 권상모음순서가 없습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	//
	// }
	// }
	//
	// //리턴 recordSet 생성
	// rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	//
	// //스케줄 기준 체크
	// blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
	// if(!blnRtnVal) return ;
	//
	// //레코드 추출
	// rsResult.first();
	// recPara = rsResult.getRecord();
	//
	// //스케줄CD 체크
	// //스케줄 금지 유무
	// szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara,
	// "YD_SCH_PROH_EXN");
	// //작업크레인
	// szYD_WRK_CRN = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
	// // 작업크레인우선순위
	// szYD_WRK_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara,
	// "YD_WRK_CRN_PRIOR");
	// //대체크레인유무
	// szYD_ALT_CRN_YN = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
	// //대체크레인
	// szYD_ALT_CRN = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
	// // 대체크레인우선순위
	// szYD_ALT_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara,
	// "YD_ALT_CRN_PRIOR");
	//
	//
	// //스케줄 금지 유무가 "Y"이면 처리를 중지하고 유스케이스를 종료한다.
	// if(szYD_SCH_PROH_EXN.equals("Y")){
	//
	// szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	// }
	//
	// //작업크레인 설비 상태 체크
	// blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);
	//
	// //작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
	// if(!blnRtnVal){
	//
	// szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	//
	// //대체크레인의 유무를 체크한다.
	// //대체크레인이 없으면 에러 리턴
	// if(!szYD_ALT_CRN_YN.equals("Y")){
	//
	// szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	//
	// }
	// //대체크레인이 있으면 대체크레인 설비 상태 체크
	// blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
	// //대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
	// if(!blnRtnVal){
	//
	// szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	//
	// } else {
	// //대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
	// szCrn = szYD_ALT_CRN;
	// szYD_SCH_PRIOR = szYD_ALT_CRN_PRIOR;
	// }
	// } else {
	// //작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
	// szCrn = szYD_WRK_CRN;
	// szYD_SCH_PRIOR = szYD_WRK_CRN_PRIOR;
	// }
	//
	// //재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다.
	// //작업예약재료 등록 여부를 체크한다.
	// for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
	//
	// //다른 작업예약에 재료가 등록되어있는지 체크한다.
	// blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
	// if(!blnRtnVal) return ;
	//
	// }
	//
	// //컨베어 BED SHIFT
	// blnRtnVal = YdCommonUtils.setShiftStkBed(szYD_STK_COL_GP);
	// if( !blnRtnVal ) return ;
	//
	// //리턴 recordSet 생성
	// rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	//
	// //작업예약ID 생성
	// blnRtnVal = getYdWbookId(rsResult);
	// if(!blnRtnVal) return ;
	// //레코드추출
	// rsResult.first();
	// recPara = rsResult.getRecord();
	// //작업예약ID
	// szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");
	//
	// //INSERT 항목 record 생성
	// recPara = JDTORecordFactory.getInstance().create();
	// //야드구분
	// String szYD_GP = szYD_SCH_CD.substring(0, 1);
	// //동구분
	// String szYD_BAY_GP = szYD_SCH_CD.substring(1,2);
	//
	// //INSERT할 항목 SET
	// recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
	// recPara.setField("YD_GP", szYD_GP);
	// recPara.setField("YD_BAY_GP", szYD_BAY_GP);
	// recPara.setField("YD_SCH_CD", szYD_SCH_CD);
	// recPara.setField("YD_SCH_PRIOR", szYD_SCH_PRIOR);
	// recPara.setField("YD_AIM_YD_GP", szYD_GP);
	// recPara.setField("YD_AIM_BAY_GP", szYD_BAY_GP);
	// /*
	// * 업무 Desc : 1. 열연조업화면으로부터 대상재를 선택해서 직접 작업요구를 함으로 베드정보를 작업요구 시 전송
	// * 2.정정L2에서 호출된 경우에는 작업요구 시 베드 정보를 사용하지 않음
	// * 수정자 : 임춘수
	// * 일자 : 2009.06.23
	// * 구현 : 베드정보가 존재하는 경우에는 야드To위치결정방법(F), 야드To위치Guide를 업데이트 처리함
	// */
	// if( !szYD_STK_BED_NO.equals("") ){
	// //야드To위치결정방법
	// recPara.setField("YD_TO_LOC_DCSN_MTD", "F");
	// //야드To위치Guide
	// recPara.setField("YD_TO_LOC_GUIDE", szYD_STK_COL_GP + szYD_STK_BED_NO);
	// }
	// recPara.setField("REGISTER", szUser);
	//
	// //작업예약 INSERT
	// intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
	// if(intRtnVal < 1){
	// szMsg = "작업예약 데이터 등록 중 에러";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// //return ;
	// throw new DAOException(szMsg);
	// }
	//
	//
	// //조회항목 record 생성
	// recPara = JDTORecordFactory.getInstance().create();
	//
	// recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
	// recPara.setField("REGISTER", szUser);
	//
	// for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
	//
	// //리턴 recordSet 생성
	// rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	// //재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
	// intRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C", rsResult);
	// if(intRtnVal != 1){
	// //return ;
	// throw new DAOException("재료번호에 해당하는 적치중 확인 중 에러");
	// }
	//
	// //레코드추출
	// rsResult.first();
	// recStkPara = rsResult.getRecord();
	//
	// //재료번호
	// recPara.setField("STL_NO", szSTL_NO[Loop_i]);
	// //적치열구분
	// recPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(recStkPara,
	// "YD_STK_COL_GP"));
	// //적치BED번호
	// recPara.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(recStkPara,
	// "YD_STK_BED_NO"));
	// //적치단번호
	// recPara.setField("YD_STK_LYR_NO", ydDaoUtils.paraRecChkNull(recStkPara,
	// "YD_STK_LYR_NO"));
	// //권상모음순서
	// recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ[Loop_i]);
	//
	// // 작업예약재료 테이블에 등록한다.
	// intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
	//
	// if(intRtnVal < 1){
	// szMsg = "작업예약재료 데이터 등록 중 에러";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// throw new DAOException(szMsg);
	// }
	// }
	//
	// //C열연크레인스케줄Main 호출
	// recPara = JDTORecordFactory.getInstance().create();
	// recPara.setField("JMS_TC_CD", "YDYDJ509");
	// recPara.setField("YD_SCH_CD", szYD_SCH_CD);
	// recPara.setField("YD_EQP_ID", szCrn);
	// ydDelegate.sendMsg(recPara);
	// }catch(DAOException e){
	// szMsg = "C열연 정정입측 Line-In 작업요구 처리중 Error : " + e.getMessage();
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// throw e;
	// } catch(Exception e){
	// szMsg = "C열연 정정입측 Line-In 작업요구 처리중 Error : " + e.getMessage();
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// throw new DAOException(e);
	// }
	//
	//
	// } // end of procR3ShearInLineInReq ()
	//
	//

	/**
	 * 오퍼레이션명 : C열연수냉탱크보급Lot편성
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procCHrWtclTnkSupLotComp(JDTORecord msgRecord)
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
		String szMethodName = "procCHrWtclTnkSupLotComp";
		// 사용자
		String szUser = "SYSTEM";

		// Line_In 재료 기준 매수
		int intLineInCnt = 0;
		// 전문 생성 일시
		String szDate = null;

		// 설비ID
		String szYD_EQP_ID = null;
		// 목표행선구분
		String szYD_AIM_RT_GP = null;
		// //적치BED번호
		// String szYD_STK_BED_NO = null;
		// //야드적치Bed입출고상태
		// String szYD_STK_BED_WHIO_STAT = null;
		// 스케줄코드
		String szYD_SCH_CD = null;

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
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if (szYD_EQP_ID.equals("")) {

				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;

			}
			// 목표행선구분
			szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_AIM_RT_GP");
			if (szYD_AIM_RT_GP.equals("")) {

				szMsg = "[전문 이상] 목표행선구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;

			}
			// 적치Bed입출고상태
			// szYD_STK_BED_WHIO_STAT = ydDaoUtils.paraRecChkNull(msgRecord,
			// "YD_STK_BED_WHIO_STAT");
			// if(szYD_STK_BED_WHIO_STAT.equals("")){
			//
			// szMsg = "[전문 이상] 야드적치Bed입출고상태가 없습니다.";
			// ydUtils.putLog(szSessionName, szMethodName, szMsg,
			// YdConstant.ERROR);
			// return ;
			//
			// }
			// //적치BED번호
			// szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord,
			// "YD_STK_BED_NO");
			// if(szYD_STK_BED_NO.equals("")){
			//
			// szMsg = "[전문 이상] 적치BED번호가 없습니다.";
			// ydUtils.putLog(szSessionName, szMethodName, szMsg,
			// YdConstant.ERROR);
			// return ;
			//
			// }
			// =================================================================================
			// 수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			// BRE 등록 안됨...테스트용 스케줄코드 생성
			// 추후 구현..
			szYD_SCH_CD = "HGCMWTUM";
			// =================================================================================

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			// 수냉탱크 Bed수 Select
			blnRtnVal = this.chkGetStkLyr(szYD_EQP_ID, rsResult);
			if (!blnRtnVal)
				return;

			// 수냉탱크 Line In 롤수(수냉탱크 Bed수)
			intLineInCnt = rsResult.size();

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			// 보급 Lot 편성 대상 재료 Select
			blnRtnVal = this.chkGetWtClTnkSupplyLotGp(msgRecord, rsResult);
			if (!blnRtnVal)
				return;

			// 지시 재료 롤수가 수냉탱크 Bed수 보다 작으면 지시재료 롤수를 수냉탱크 Line In 롤수에 대입한다.
			if (intLineInCnt > rsResult.size())
				intLineInCnt = rsResult.size();

			// 레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			// JMS TC CODE
			recOutPara.setField("JMS_TC_CD", "YDYDJ241");
			// 레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();

			// Line In 재료기준매수(현재4매)만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= intLineInCnt; Loop_i++) {

				// 재료번호
				recOutPara.setField("STL_NO" + Loop_i,
						ydDaoUtils.paraRecChkNull(recPara, "STL_NO"));
				// 권상모음순서
				recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, "" + Loop_i);

				// 다음 레코드 추출
				rsResult.next();
				recPara = rsResult.getRecord();

			}

			// 전문 발생 일시
			szDate = ydUtils.getCurDate("yyyy/MM/dd HH:mm:ss");

			// 발생 일시
			recOutPara.setField("JMS_TC_CREATE_DDTT", szDate);
			// 스케줄코드
			recOutPara.setField("YD_SCH_CD", szYD_SCH_CD);
			// LINE_IN 재료매수
			recOutPara.setField("YD_LINE_IN_SH", "" + intLineInCnt);
			// 적치열구분
			recOutPara.setField("YD_STK_COL_GP", szYD_EQP_ID);

			// 전문 송신
			ydDelegate.sendMsg(recOutPara);
			szMsg = "C열연 수냉탱크 보급 Lot 편성 후 C열연 수냉탱크 Line_in 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		} catch (Exception e) {
			szMsg = "C열연 수냉탱크 보급 Lot 편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}

	} // end of procCHrWtclTnkSupLotComp

	/**
	 * 오퍼레이션명 : C열연수냉탱크Line-In요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procR3WtclTnkLineInReq(JDTORecord msgRecord)
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
		String szMethodName = "procR3WtclTnkLineInReq";
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
		// 스케줄우선순위
		String szYD_SCH_PRIOR = null;
		// 스케줄 금지 유무
		String szYD_SCH_PROH_EXN = null;
		// 작업크레인
		String szYD_WRK_CRN = null;
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
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;

			}
			// 적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_COL_GP");
			if (szYD_STK_COL_GP.equals("")) {

				szMsg = "[전문 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;

			}
			// //적치BED번호
			// szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord,
			// "YD_STK_BED_NO");
			// if(szYD_STK_BED_NO.equals("")){
			//
			// szMsg = "[전문 이상] 적치BED번호가 없습니다.";
			// ydUtils.putLog(szSessionName, szMethodName, szMsg,
			// YdConstant.ERROR);
			// return ;
			//
			// }
			// 재료매수
			intMtlCnt = ydDaoUtils
					.paraRecChkNullInt(msgRecord, "YD_LINE_IN_SH");
			if (intMtlCnt == 0) {

				szMsg = "[전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;

			}

			// 재료번호
			szSTL_NO = new String[intMtlCnt + 1];
			// 권상모음순서
			szYD_UP_COLL_SEQ = new String[intMtlCnt + 1];

			// 재료번호, 권상모음순서, 적치단(테스트용)
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,
						"STL_NO" + Loop_i);
				if (szSTL_NO[Loop_i].equals("")) {

					szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg,
							YdConstant.ERROR);
					return;

				}
				// 권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,
						"YD_UP_COLL_SEQ" + Loop_i);
				if (szYD_UP_COLL_SEQ[Loop_i].equals("")) {

					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i]
							+ ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg,
							YdConstant.ERROR);
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
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;
			}

			// 작업크레인 설비 상태 체크
			blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);

			// 작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if (!blnRtnVal) {

				szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);

				// 대체크레인의 유무를 체크한다.
				// 대체크레인이 없으면 에러 리턴
				if (!szYD_ALT_CRN_YN.equals("Y")) {

					szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg,
							YdConstant.ERROR);
					return;

				}
				// 대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				// 대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {

					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg,
							YdConstant.ERROR);
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
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
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
				intRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C",
						rsResult);
				if (intRtnVal != 1)
					return;

				// 레코드추출
				rsResult.first();
				recStkPara = rsResult.getRecord();

				// 재료번호
				recPara.setField("STL_NO", szSTL_NO[Loop_i]);
				// 적치열구분
				recPara.setField("YD_STK_COL_GP",
						ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_COL_GP"));
				// 적치BED번호
				recPara.setField("YD_STK_BED_NO",
						ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_BED_NO"));
				// 적치단번호
				recPara.setField("YD_STK_LYR_NO",
						ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_LYR_NO"));
				// 권상모음순서
				recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ[Loop_i]);

				// 작업예약재료 테이블에 등록한다.
				intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);

				if (intRtnVal < 1) {
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg,
							YdConstant.ERROR);
					return;
				}
			}

		} catch (Exception e) {
			szMsg = "C열연 수냉탱크 Line-In 요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}

	} // end of procR3WtclTnkLineInReq()

	/**
	 * 오퍼레이션명 : C열연수냉탱크 LOT 편성 데이터 유무체크 및 데이터 반환
	 * 
	 * @param JDTORecord
	 *            recInPara 파라미터 레코드 JDTORecordSet rsResult 결과레코드셋
	 * @return boolean true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetWtClTnkSupplyLotGp(JDTORecord recInPara,
			JDTORecordSet rsResult) throws JDTOException {

		// 저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메소드명
		String szMethodName = "chkGetWtClTnkSupplyLotGp";
		String szMsg = null;

		// 레코드 선언
		JDTORecord recPara = null;

		try {
			// 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();

			// 목표행선구분
			recPara.setField("YD_AIM_RT_GP",
					ydDaoUtils.paraRecChkNull(recInPara, "YD_AIM_RT_GP"));

			// FROM 야드동
			recPara.setField("YD_STK_COL_GP",
					ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_ID")
							.substring(0, 2));

			// 저장품 테이블 조회
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 17);

			// 리턴값 메세지처리
			if (intRtnVal >= 1) {

				blnRtnVal = true;

			} else if (intRtnVal == 0) {

				szMsg = "C열연 수냉탱크 LOT 편성 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == -2) {

				szMsg = "C열연 수냉탱크 LOT 편성 데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = "C열연 수냉탱크 LOT 편성 데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch (Exception e) {
			szMsg = "C열연 수냉탱크 LOT 편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: "
					+ e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} // end of chkGetWtClTnkSupplyLotGp

	/**
	 * 오퍼레이션명 : 적치단 유무체크 및 조회결과 데이터 반환
	 * 
	 * @param String
	 *            szStkColGp 적치열구분 JDTORecordSet rsResult 결과레코드셋
	 * @return boolean true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetStkLyr(String szStkColGp, JDTORecordSet rsResult)
			throws JDTOException {

		// 적치단 DAO
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();

		String szMsg = null;
		String szMethodName = "chkGetStkLyr";
		int intRtnVal = 0;
		boolean blnRtnVal = false;
		JDTORecord recPara = null;

		try {

			// 조회 항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();

			// 조회 파라미터 레코드 set
			recPara.setField("YD_STK_COL_GP", szStkColGp);

			// 적치단정보 조회
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsResult, 5);

			// 리턴값 메세지처리
			if (intRtnVal >= 1) {

				blnRtnVal = true;

			} else if (intRtnVal == 0) {
				szMsg = "적치열구분(" + szStkColGp + ")" + " 에 대한 적치단 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == -2) {
				szMsg = "적치열구분(" + szStkColGp + ")"
						+ "로 적치단 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			} else {
				szMsg = "적치열구분(" + szStkColGp + ")" + " 로 적치단 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch (Exception e) {
			szMsg = "적치단 유무체크 및 조회결과 데이터 반환 중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} // end of chkGetStkLyr

	/**
	 * 오퍼레이션명 : 재료번호, 적치단재료상태에 대한 적치단 정보 유무체크 및 조회결과 데이터 반환
	 * 
	 * @param String
	 *            szStlNo 재료번호 String szMtlStat 적치단재료상태 JDTORecordSet rsResult
	 *            결과레코드셋
	 * @return boolean true(성공), false(실패)
	 * @throws JDTOException
	 */
	public int chkGetStlStkLyr(String szStlNo, String szMtlStat,
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
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				// blnRtnVal = false;
				return intRtnVal;
			} else if (intRtnVal == 1) {

				// blnRtnVal = true;
				return intRtnVal;
			} else if (intRtnVal == 0) {
				szMsg = "재료번호(" + szStlNo + ")," + "적치단재료상태(" + szMtlStat
						+ ")," + " 에 대한 적치단 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				// blnRtnVal = false;
				return intRtnVal;
			} else if (intRtnVal == -2) {
				szMsg = "재료번호(" + szStlNo + ")," + "적치단재료상태(" + szMtlStat
						+ ")," + "로 적치단 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				// blnRtnVal = false;
				return intRtnVal;
			} else {
				szMsg = "재료번호(" + szStlNo + ")," + "적치단재료상태(" + szMtlStat
						+ ")," + " 로 적치단 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				// blnRtnVal = false;
				return intRtnVal;
			}
		} catch (Exception e) {
			szMsg = "재료번호, 적치단재료상태에 대한 적치단 정보 유무체크 및 조회결과 데이터 반환 중 Error : "
					+ e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			// blnRtnVal = false;
			intRtnVal = -100;
		}
		return intRtnVal;
	} // end of chkGetStlStkLyr

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
			// 상수 수정 [2009.12.03 이현성]
			if (szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_BREAK)) {

				szMsg = "설비ID(" + szEqpId + ")의 상태가 고장(" + szYD_EQP_STAT
						+ ") 입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
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
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == 1) {

				blnRtnVal = true;

			} else if (intRtnVal == 0) {

				szMsg = "스케줄코드(" + szSchCd + ")에 대한 스케줄기준 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == -2) {

				szMsg = "스케줄코드(" + szSchCd + ")로 스케줄기준 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = "스케줄코드(" + szSchCd + ")로 스케줄기준 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch (Exception e) {
			szMsg = "스케줄기준 체크 중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return blnRtnVal = false;
		}
		return blnRtnVal = true;

	} // end of chkGetSchRule

	// /**
	// * 오퍼레이션명 : C열연 정정입측 보급Lot 편성 데이터 유무체크 및 데이터 반환
	// *
	// * @param JDTORecord recInPara 파라미터 레코드
	// * JDTORecordSet rsResult 결과레코드셋
	// * @return boolean true(성공), false(실패)
	// * @throws JDTOException
	// */
	// public boolean chkGetShearInSupplyLotGp(JDTORecord recInPara,
	// JDTORecordSet rsResult)throws JDTOException {
	//
	// //저장품 DAO
	// YdStockDao ydStockDao = new YdStockDao();
	// //리턴값(boolean)
	// boolean blnRtnVal = false;
	// //리턴값(int)
	// int intRtnVal = 0;
	// //메소드명
	// String szMethodName = "chkGetShearInSupplyLotGp";
	// String szMsg = null;
	// //설비ID
	// String szYD_EQP_ID = null;
	// //공정구분
	// String szPROC_GP = null;
	// //레코드 선언
	// JDTORecord recPara = null;
	//
	// try {
	// //레코드 생성
	// recPara = JDTORecordFactory.getInstance().create();
	//
	// //목표행선구분
	// recPara.setField("YD_AIM_RT_GP", ydDaoUtils.paraRecChkNull(recInPara,
	// "YD_AIM_RT_GP"));
	// szYD_EQP_ID = ydDaoUtils.paraRecChkNull(recInPara, "YD_EQP_ID");
	// //FROM 야드동
	// // recPara.setField("YD_STK_COL_GP", szYD_EQP_ID.substring(0, 2));
	// recPara.setField("YD_STK_COL_GP", szYD_EQP_ID);
	//
	// //동구분을 공정구분으로 변환
	// //E동->R(SPM2), G동->H(#1HFL), H동->K(#1HSL)
	// szPROC_GP = szYD_EQP_ID.substring(1,2);
	// if (szPROC_GP.equals("E")) szPROC_GP = "R";
	// else if(szPROC_GP.equals("G")) szPROC_GP = "H";
	// else if(szPROC_GP.equals("H")) szPROC_GP = "K";
	// else {
	//
	// szMsg = "동구분(" + szPROC_GP + ")에는 정정라인이 없습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return blnRtnVal = false;
	//
	// }
	// //공정구분
	// recPara.setField("PROC_GP", szPROC_GP);
	// //저장품 테이블 조회
	// intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 19);
	//
	// //리턴값 메세지처리
	// if(intRtnVal >= 1){
	//
	// blnRtnVal = true;
	//
	// } else if(intRtnVal == 0){
	//
	// szMsg = "C열연 정정입측 보급Lot 편성 데이터가 없습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// blnRtnVal = false;
	//
	// } else if(intRtnVal == -2){
	//
	// szMsg = "C열연 정정입측 보급Lot 편성 데이터 조회중 parameter error 발생!";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// blnRtnVal = false;
	//
	// } else {
	//
	// szMsg = "C열연 정정입측 보급Lot 편성 데이터 조회중 오류 발생!";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// blnRtnVal = false;
	//
	// }
	// } catch(Exception e){
	// szMsg = "C열연 정정입측 보급Lot 편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: " +
	// e.getMessage();
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// blnRtnVal = false;
	// }
	// return blnRtnVal;
	// } //end of chkGetShearInSupplyLotGp
	//
	//

	// /**
	// * 오퍼레이션명 : 적치단 업데이트
	// *
	// * @param JDTORecord recPara 업데이트용 레코드
	// * int intGp 업데이트 쿼리 구분자
	// * String szStkLyrNo 적치단번호
	// * JDTORecordSet rsResult 결과레코드셋
	// * @return boolean true(성공), false(실패)
	// * @throws JDTOException
	// */
	// public boolean setStkLyr(JDTORecord recPara, int intGp)throws
	// JDTOException {
	//
	// //적치단 DAO
	// YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
	//
	// String szMsg = null;
	// String szMethodName = "setStkLyr";
	// int intRtnVal = 0;
	// boolean blnRtnVal = false;
	//
	// try {
	//
	// //적치단정보 업데이트
	// intRtnVal = ydStkLyrDao.updYdStklyr(recPara, intGp);
	//
	// //리턴값 메세지처리
	// if(intRtnVal >= 1){
	//
	// blnRtnVal = true;
	//
	// } else if(intRtnVal == 0){
	// szMsg = "적치열구분(" + recPara.getFieldString("YD_STK_COL_GP") + ")," +
	// "적치BED번호(" + recPara.getFieldString("YD_STK_BED_NO") + ")," +
	// "적치단번호(" + recPara.getFieldString("YD_STK_LYR_NO") + ")" +
	// " 에 대한 적치단 데이터가 없습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// blnRtnVal = false;
	//
	// } else if(intRtnVal == -1){
	// szMsg = "적치열구분(" + recPara.getFieldString("YD_STK_COL_GP") + ")," +
	// "적치BED번호(" + recPara.getFieldString("YD_STK_BED_NO") + ")," +
	// "적치단번호(" + recPara.getFieldString("YD_STK_LYR_NO") + ")" +
	// "로 적치단 데이터가 중복되었습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// blnRtnVal = false;
	//
	// } else if(intRtnVal == -2){
	// szMsg = "적치열구분(" + recPara.getFieldString("YD_STK_COL_GP") + ")," +
	// "적치BED번호(" + recPara.getFieldString("YD_STK_BED_NO") + ")," +
	// "적치단번호(" + recPara.getFieldString("YD_STK_LYR_NO") + ")" +
	// "로 적치단 업데이트중 parameter error 발생!";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// blnRtnVal = false;
	//
	// } else {
	// szMsg = "적치열구분(" + recPara.getFieldString("YD_STK_COL_GP") + ")," +
	// "적치BED번호(" + recPara.getFieldString("YD_STK_BED_NO") + ")," +
	// "적치단번호(" + recPara.getFieldString("YD_STK_LYR_NO") + ")" +
	// " 로 적치단 업데이트중 오류 발생!";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// blnRtnVal = false;
	//
	// }
	// } catch(Exception e){
	// szMsg = "적치단 업데이트 중 Error : " + e.getMessage();
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// blnRtnVal = false;
	// }
	// return blnRtnVal;
	// } //end of setStkLyr
	//
	//

	// /**
	// * 오퍼레이션명 : C연주C열연보급Lot편성
	// *
	// * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	// * @param msgRecord
	// * @return
	// * @throws JDTOException
	// */
	// public void procCCsCHrSupLotComp (JDTORecord msgRecord) throws
	// JDTOException {
	//
	// YdDelegate ydDelegate = new YdDelegate();
	// //공용 DAO METHOD
	// YdDaoUtils ydDaoUtils = new YdDaoUtils();
	// //공용 METHOD
	// YdUtils ydutils = new YdUtils();
	//
	// //레코드 선언
	// JDTORecord recPara = null;
	// JDTORecord recOutPara = null;
	// //레코드셋 선언
	// JDTORecordSet rsResult = null;
	//
	// //리턴값(boolean)
	// boolean blnRtnVal = false;
	// //리턴값(int)
	// int intRtnVal = 0;
	// //메세지
	// String szMsg = "";
	// //메소드명
	// String szMethodName = "procCCsCHrSupLotComp";
	// //사용자
	// String szUser = "SYSTEM";
	//
	// //Carry_In 재료 기준 매수
	// int intCarryInCnt = 4;
	// //전문 생성 일시
	// String szDate = null;
	//
	// //설비ID
	// String szYD_EQP_ID = null;
	// //목표행선구분
	// String szYD_AIM_RT_GP = null;
	// //적치BED번호
	// String szYD_STK_BED_NO = null;
	// //야드적치Bed입출고상태
	// String szYD_STK_BED_WHIO_STAT = null;
	// //스케줄코드
	// String szYD_SCH_CD = null;
	// //가열로장입LOT번호
	// String szREFUR_CHG_LOT_NO = "";
	// String szPREV_REFUR_CHG_LOT_NO = "";
	// //재료번호
	// String szSTL_NO = null;
	// //재료매수
	// int intRealSh = 0;
	//
	// //TC CODE 추출
	// String szRcvTcCode = ydUtils.getTcCode(msgRecord);
	// //에러 리턴
	// if(szRcvTcCode == null){
	//
	// szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" +
	// szRcvTcCode + ")";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	//
	// }
	// //TC CODE DISPLAY
	// if(bDebugFlag){
	//
	// szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	//
	// }
	//
	// try {
	// //받은 전문 편집
	// //설비ID
	// szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
	// if(szYD_EQP_ID.equals("")){
	//
	// szMsg = "[전문 이상] 설비ID가 없습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	//
	// }
	// //목표행선구분
	// szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_RT_GP");
	// if(szYD_AIM_RT_GP.equals("")){
	//
	// szMsg = "[전문 이상] 목표행선구분이 없습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	//
	// }
	// //적치Bed입출고상태 - 사용안함
	// szYD_STK_BED_WHIO_STAT = ydDaoUtils.paraRecChkNull(msgRecord,
	// "YD_STK_BED_WHIO_STAT");
	// // if(szYD_STK_BED_WHIO_STAT.equals("")){
	// //
	// // szMsg = "[전문 이상] 야드적치Bed입출고상태가 없습니다.";
	// // ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// // return ;
	// //
	// // }
	// //적치BED번호
	// szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
	// if(szYD_STK_BED_NO.equals("")){
	//
	// szMsg = "[전문 이상] 적치BED번호가 없습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	//
	// }
	// //=================================================================================
	// //수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
	// //BRE 등록 안됨...테스트용 스케줄코드 생성
	// //추후 구현..
	// //AAPUP4, ACPUP2설비가 보급베드또는 인출베드로 사용가능하므로 스케줄코드를 따로 생성
	// //szYD_SCH_CD = "AAPU04UM";
	// szYD_SCH_CD = YdCommonUtils.getSchCd(szYD_EQP_ID, szYD_STK_BED_NO, "U");
	// //=================================================================================
	//
	// szMsg = "[C연주 C열연 보급 Lot 편성]스케줄코드 : " + szYD_SCH_CD;
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	//
	// //리턴 recordSet 생성
	// rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	// //보급 Lot 편성 대상 재료 Select
	// blnRtnVal = chkGetCHRSupplyLotGp(szYD_SCH_CD, "HC", rsResult);
	// if(!blnRtnVal) return ;
	//
	// //지시 재료 매수가 Carry In 재료기준매수(현재4매)보다 작으면 지시 재료 매수를 재료기준매수에 대입한다.
	// if(intCarryInCnt > rsResult.size()) intCarryInCnt = rsResult.size();
	//
	// //레코드 생성
	// recOutPara = JDTORecordFactory.getInstance().create();
	// //JMS TC CODE
	// recOutPara.setField("JMS_TC_CD", "YDYDJ241");
	// //레코드 추출
	// rsResult.first();
	// recPara = rsResult.getRecord();
	//
	// //Carry In 재료기준매수(현재4매)만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
	// for (int Loop_i = 1; Loop_i <= intCarryInCnt; Loop_i++){
	// //가열로장입LOT번호
	// szREFUR_CHG_LOT_NO = ydDaoUtils.paraRecChkNull(recPara,
	// "REFUR_CHG_LOT_NO");
	//
	// if( Loop_i == 1 ){
	// szPREV_REFUR_CHG_LOT_NO = szREFUR_CHG_LOT_NO;
	// }else{
	// //가열로장입LOT번호가 다른 대상재는 제외시킴
	// if( !szREFUR_CHG_LOT_NO.equals(szPREV_REFUR_CHG_LOT_NO) ){
	// szMsg = "[C연주 C열연 보급 Lot 편성]대상재의 가열로장입LOT번호 비교 : 재료의 가열로장입LOT번호[" +
	// szREFUR_CHG_LOT_NO + "] , 이전 재료의 가열로장입LOT번호[" + szPREV_REFUR_CHG_LOT_NO +
	// "]";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	// break;
	// }
	// }
	// //재료번호
	// szSTL_NO = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
	// recOutPara.setField("STL_NO" + Loop_i, szSTL_NO);
	// //권상모음순서
	// recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, "" + Loop_i);
	//
	// szMsg = "[C연주 C열연 보급 Lot 편성]대상재의 가열로장입LOT번호 비교 : 재료[" + szSTL_NO +
	// "]의 가열로장입LOT번호[" + szREFUR_CHG_LOT_NO + "] , 이전 재료의 가열로장입LOT번호[" +
	// szPREV_REFUR_CHG_LOT_NO + "]";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	//
	// //다음 레코드 추출
	// rsResult.next();
	// recPara = rsResult.getRecord();
	// //재료매수
	// intRealSh++;
	//
	// }
	//
	// //전문 발생 일시
	// szDate = YdUtils.getCurDate("yyyy/MM/dd HH:mm:ss");
	//
	// //발생 일시
	// recOutPara.setField("JMS_TC_CREATE_DDTT", szDate);
	// //스케줄코드
	// recOutPara.setField("YD_SCH_CD", szYD_SCH_CD);
	// //CARRY_IN 재료매수
	// recOutPara.setField("YD_CARRY_IN_SH", "" + intRealSh);
	// //적치열구분
	// recOutPara.setField("YD_STK_COL_GP", szYD_EQP_ID);
	// //적치BED번호
	// recOutPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
	//
	// //전문 송신
	// ydDelegate.sendMsg(recOutPara);
	// szMsg = "C연주 C열연 보급 Lot 편성 후 CARRY_IN 송신 완료!";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	//
	// } catch(Exception e){
	// szMsg = "C연주 C열연 보급 Lot 편성 Error : " + e.getMessage();
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	// }
	//
	// } //end of procCCsCHrSupLotComp
	//
	//

	// /**
	// * 오퍼레이션명 : C연주C열연보급Carry-In작업요구
	// *
	// * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	// * @param msgRecord
	// * @return
	// * @throws JDTOException
	// */
	// public void procCCsCHrSupCarryInWrkReq(JDTORecord msgRecord)throws
	// JDTOException {
	//
	// //스케줄기준 DAO
	// YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
	// //작업예약 재료 DAO
	// YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
	// //공용 DAO METHOD
	// YdDaoUtils ydDaoUtils = new YdDaoUtils();
	// //공용 METHOD
	// YdUtils ydutils = new YdUtils();
	//
	// //리턴값(boolean)
	// boolean blnRtnVal = false;
	// //리턴값(int)
	// int intRtnVal = 0;
	// //메세지
	// String szMsg = "";
	// //METHOD명
	// String szMethodName = "procCCsCHrSupCarryInWrkReq";
	// //사용자
	// String szUser = "SYSTEM";
	//
	// //레코드 선언
	// JDTORecord recPara = null;
	// JDTORecord recStkPara = null;
	// //레코드셋 선언
	// JDTORecordSet rsResult = null;
	//
	// //설비ID(열구분)
	// String szYD_STK_COL_GP = null;
	// //적치BED번호
	// String szYD_STK_BED_NO = null;
	// //재료매수(int)
	// int intMtlCnt = 0;
	// //재료번호
	// String[] szSTL_NO = new String[6];
	// //권상모음순서
	// String[] szYD_UP_COLL_SEQ = new String[6];
	// //적치단
	// String[] szYD_STK_LYR_NO = new String[6];
	// //스케줄코드
	// String szYD_SCH_CD = null;
	// //스케줄우선순위
	// String szYD_SCH_PRIOR = "";
	// //스케줄 금지 유무
	// String szYD_SCH_PROH_EXN = null;
	// //작업크레인
	// String szYD_WRK_CRN = null;
	// // 작업크레인우선순위
	// String szYD_WRK_CRN_PRIOR = null;
	// //대체크레인유무
	// String szYD_ALT_CRN_YN = null;
	// //대체크레인
	// String szYD_ALT_CRN = null;
	// // 대체크레인우선순위
	// String szYD_ALT_CRN_PRIOR = null;
	// //선택크레인
	// String szCrn = null;
	// //작업예약ID
	// String szYD_WBOOK_ID = null;
	//
	//
	// //TC CODE 추출
	// String szRcvTcCode = ydUtils.getTcCode(msgRecord);
	// //에러 리턴
	// if(szRcvTcCode == null){
	//
	// szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" +
	// szRcvTcCode + ")";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	//
	// }
	// //TC CODE DISPLAY
	// if(bDebugFlag){
	//
	// szMsg = "전문수신 : TCCODE=" + szRcvTcCode ;
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	//
	// }
	//
	// try {
	//
	// //받은 전문 편집
	// //스케줄코드
	// szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
	// if(szYD_SCH_CD.equals("")){
	//
	// szMsg = "[전문 이상] 스케줄코드가 없습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	//
	// }
	// //적치열구분
	// szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
	// if(szYD_STK_COL_GP.equals("")){
	//
	// szMsg = "[전문 이상] 적치열구분이 없습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	//
	// }
	// //적치BED번호
	// szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
	// if(szYD_STK_BED_NO.equals("")){
	//
	// szMsg = "[전문 이상] 적치BED번호가 없습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	//
	// }
	// //재료매수
	// intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_CARRY_IN_SH");
	// if(intMtlCnt == 0){
	//
	// szMsg = "[전문 이상] 재료매수가 없습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	//
	// }
	//
	// //재료번호, 권상모음순서, 적치단(테스트용)
	// for(int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
	//
	// //재료번호
	// szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO" +
	// Loop_i);
	// if(szSTL_NO[Loop_i].equals("")){
	//
	// szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	//
	// }
	// //권상모음순서
	// szYD_UP_COLL_SEQ[Loop_i] =
	// ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_COLL_SEQ" + Loop_i);
	// if(szYD_UP_COLL_SEQ[Loop_i].equals("")){
	//
	// szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i] +
	// ")에 대한 권상모음순서가 없습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	//
	// }
	// }
	//
	// //리턴 recordSet 생성
	// rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	//
	// //스케줄 기준 체크
	// blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
	// if(!blnRtnVal) return ;
	//
	// //레코드 추출
	// rsResult.first();
	// recPara = rsResult.getRecord();
	//
	// //스케줄CD 체크
	// //스케줄 금지 유무
	// szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara,
	// "YD_SCH_PROH_EXN");
	// //작업크레인
	// szYD_WRK_CRN = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
	// //작업크레인우선순위
	// szYD_WRK_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara,
	// "YD_WRK_CRN_PRIOR");
	// //대체크레인유무
	// szYD_ALT_CRN_YN = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
	// //대체크레인
	// szYD_ALT_CRN = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
	// //대체크레인우선순위
	// szYD_ALT_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara,
	// "YD_ALT_CRN_PRIOR");
	//
	//
	// //스케줄 금지 유무가 "Y"이면 처리를 중지하고 유스케이스를 종료한다.
	// if(szYD_SCH_PROH_EXN.equals("Y")){
	//
	// szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	// }
	//
	// //작업크레인 설비 상태 체크
	// blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);
	//
	// //작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
	// if(!blnRtnVal){
	//
	// szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	//
	// //대체크레인의 유무를 체크한다.
	// //대체크레인이 없으면 에러 리턴
	// if(!szYD_ALT_CRN_YN.equals("Y")){
	//
	// szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	//
	// }
	// //대체크레인이 있으면 대체크레인 설비 상태 체크
	// blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
	// //대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
	// if(!blnRtnVal){
	//
	// szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	//
	// } else {
	// //대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
	// szCrn = szYD_ALT_CRN;
	// szYD_SCH_PRIOR = szYD_ALT_CRN_PRIOR;
	// }
	// } else {
	// //작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
	// szCrn = szYD_WRK_CRN;
	// szYD_SCH_PRIOR = szYD_WRK_CRN_PRIOR;
	// }
	//
	// //재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다.
	// //작업예약재료 등록 여부를 체크한다.
	// for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
	//
	// //크레인사양과 저장품 사양을 체크(길이,폭,중량)
	// blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
	// if(!blnRtnVal) return ;
	//
	// //다른 작업예약에 재료가 등록되어있는지 체크한다.
	// blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
	// if(!blnRtnVal) return ;
	//
	// }
	//
	// //리턴 recordSet 생성
	// rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	//
	// //작업예약ID 생성
	// blnRtnVal = getYdWbookId(rsResult);
	// if(!blnRtnVal) return ;
	// //레코드추출
	// rsResult.first();
	// recPara = rsResult.getRecord();
	// //작업예약ID
	// szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");
	//
	// //INSERT 항목 record 생성
	// recPara = JDTORecordFactory.getInstance().create();
	// //야드구분
	// String szYD_GP = szYD_SCH_CD.substring(0, 1);
	// //동구분
	// String szYD_BAY_GP = szYD_SCH_CD.substring(1,2);
	//
	// //INSERT할 항목 SET
	// recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
	// recPara.setField("YD_GP", szYD_GP);
	// recPara.setField("YD_BAY_GP", szYD_BAY_GP);
	// recPara.setField("YD_SCH_CD", szYD_SCH_CD);
	// recPara.setField("YD_SCH_PRIOR", szYD_SCH_PRIOR);
	// recPara.setField("YD_AIM_YD_GP", szYD_GP);
	// recPara.setField("YD_AIM_BAY_GP", szYD_BAY_GP);
	// //야드To위치결정방법
	// recPara.setField("YD_TO_LOC_DCSN_MTD", "F");
	// //야드To위치Guide
	// recPara.setField("YD_TO_LOC_GUIDE", szYD_STK_COL_GP + szYD_STK_BED_NO);
	// recPara.setField("REGISTER", szUser);
	//
	// //작업예약 INSERT
	// intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
	// if(intRtnVal < 1){
	// szMsg = "작업예약 데이터 등록 중 에러";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	// }
	//
	//
	// //조회항목 record 생성
	// recPara = JDTORecordFactory.getInstance().create();
	//
	// recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
	// // recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
	// // recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
	// recPara.setField("REGISTER", szUser);
	//
	// for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
	//
	// //리턴 recordSet 생성
	// rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	// //재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
	// intRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C", rsResult);
	// //if( intRtnVal != 1 ) return ;
	//
	// if(intRtnVal != 1) {
	// if( intRtnVal == 0 ) {
	// intRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "U", rsResult);
	// //보조작업으로 권상대기 인 경우
	// if(intRtnVal != 1) {
	// szMsg="적치단에 재료["+szSTL_NO[Loop_i]+"]가 존재하지 않습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// throw new DAOException(szMsg);
	// }
	// }else{
	// szMsg="적치단에 재료["+szSTL_NO[Loop_i]+"] 조회 시 오류발생";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// throw new DAOException(szMsg);
	// }
	// //return YdConstant.RETN_CD_FAILURE;
	// }
	//
	// //레코드추출
	// rsResult.first();
	// recStkPara = rsResult.getRecord();
	//
	// //재료번호
	// recPara.setField("STL_NO", szSTL_NO[Loop_i]);
	// //적치열구분
	// recPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(recStkPara,
	// "YD_STK_COL_GP"));
	// //적치BED번호
	// recPara.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(recStkPara,
	// "YD_STK_BED_NO"));
	// //적치단번호
	// recPara.setField("YD_STK_LYR_NO", ydDaoUtils.paraRecChkNull(recStkPara,
	// "YD_STK_LYR_NO"));
	// //권상모음순서
	// recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ[Loop_i]);
	//
	// // 작업예약재료 테이블에 등록한다.
	// intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
	//
	// if(intRtnVal < 1){
	// szMsg = "작업예약재료 데이터 등록 중 에러";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	// }
	// }
	//
	//
	// //스케줄코드, 설비id
	// recPara = JDTORecordFactory.getInstance().create();
	// recPara.setField("JMS_TC_CD", "YDYDJ500");
	// recPara.setField("YD_SCH_CD", szYD_SCH_CD);
	// recPara.setField("YD_EQP_ID", szCrn);
	//
	// ydDelegate.sendMsg(recPara);
	//
	//
	// } catch(Exception e){
	// szMsg = "C연주C열연보급Carry-In작업요구 처리중 Error : " + e.getMessage();
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// }
	//
	//
	// } // end of procCCsCHrSupCarryInWrkReq()
	//
	//
	// /**
	// * 배차정보로 차량스케줄 생성
	// * @param msgRecord
	// * @return String
	// */
	// public String procCarSchForDist(JDTORecord msgRecord){
	// String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
	// String szMethodName = "procCarSchForDist";
	// String szLogMsg = null;
	// String szYD_EQP_ID = null;
	// String szTRN_EQP_CD = null;
	// String szYD_CAR_USE_GP = null;
	// //String szSPOS_WLOC_CD = null;
	// String szCAR_NO = null;
	// String szCARD_NO = null;
	// String szYD_CAR_SCH_ID = null;
	// int intRtnVal = -1;
	//
	// YdCarSchDao ydCarSchDao = new YdCarSchDao();
	// try {
	// //1. 전문확인
	// szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
	// szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(msgRecord, "TRN_EQP_CD");
	// szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_USE_GP");
	// szCAR_NO = ydDaoUtils.paraRecChkNull(msgRecord, "CAR_NO");
	// szCARD_NO = ydDaoUtils.paraRecChkNull(msgRecord, "CARD_NO");
	// //2. 차량스펙에 데이타 등록 또는 수정 필요
	//
	// //3. 차량스케줄생성
	// szYD_CAR_SCH_ID = ydCarSchDao.getYdCarschId();
	// msgRecord.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
	// intRtnVal = ydCarSchDao.insYdCarsch(msgRecord);
	// //4. 상차LOT모듈 호출 - 파라미터를 넘겨받아서 호출결정
	//
	// }catch(JDTOException ex){
	// szLogMsg = "["+szMethodName+"] 차량스케줄등록 시 오류발생 - " + ex.getMessage();
	// ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
	// throw new DAOException(ex);
	// }
	// return szRtnMsg;
	// }
	//
	//

	// /**
	// * 오퍼레이션명 : 코일 매뉴얼 작업 Lot편성
	// *
	// * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	// * @param msgRecord
	// * @return
	// * @throws JDTOException
	// */
	// public void procCoilManualWrkLotComp (JDTORecord msgRecord) throws
	// JDTOException {
	// //레코드 선언
	// JDTORecord recPara = null;
	// JDTORecord recOutPara = null;
	// JDTORecord recResult = null;
	// //레코드셋 선언
	// JDTORecordSet rsResult = null;
	//
	// //리턴값(boolean)
	// boolean blnRtnVal = false;
	// int intRtnVal = 0;
	// //메세지
	// String szMsg = "";
	// //메소드명
	// String szMethodName = "procCoilManualWrkLotComp";
	// String szOperationName = "코일 매뉴얼 작업 Lot편성";
	//
	// //목표행선구분
	// String szYD_AIM_RT_GP = null;
	// //야드구분
	// String szYD_GP = null;
	// //동구분
	// String szYD_BAY_GP = null;
	// //재료번호
	// String szSTL_NO = null;
	// //스케줄코드
	// String szYD_SCH_CD = null;
	// //대상 재료 중량 합계
	// long lngSumYD_MTL_WT = 0;
	// //크레인작업허용중량
	// long lngYD_WRK_ABLE_WT = 0;
	// //검색된 대상재 개수
	// int intLotGpSh = 0;
	// //작업대상재 개수
	// int intYD_LOT_GP_SH = 0;
	//
	//
	// //TC CODE 추출
	// String szRcvTcCode = ydUtils.getTcCode(msgRecord);
	// //에러 리턴
	// if(szRcvTcCode == null){
	//
	// szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" +
	// szRcvTcCode + ")";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	//
	// }
	// //TC CODE DISPLAY
	// if(bDebugFlag){
	//
	// szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	//
	// }
	//
	// try {
	//
	//
	// ydUtils.displayRecord(szOperationName, msgRecord);
	//
	// //받은 전문 확인
	// //야드구분
	// szYD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP");
	// if(szYD_GP.equals("")){
	//
	// szMsg = "[전문 이상] 야드구분이 없습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	//
	// }
	// //동구분
	// szYD_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP");
	// if(szYD_BAY_GP.equals("")){
	//
	// szMsg = "[전문 이상] 동구분이 없습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	//
	// }
	// //설비ID
	// szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_RT_GP");
	// if(szYD_AIM_RT_GP.equals("")){
	//
	// szMsg = "[전문 이상] 목표행선구분이 없습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	//
	// }
	//
	// //
	// =================================================================================
	// // 수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
	// // BRE 등록 안됨...테스트용 스케줄코드 생성
	// // 추후 구현..
	// szYD_SCH_CD = "";
	// //
	// =================================================================================
	// //스케쥴기준 속성 조회, 크레인 사양 조회
	// recResult = JDTORecordFactory.getInstance().create();
	// intRtnVal = YdCommonUtils.getCrnInfoByCrnSchRule(szYD_SCH_CD, recResult);
	// if( intRtnVal <= 0) return ;
	// //야드작업가능중량
	// lngYD_WRK_ABLE_WT = ydDaoUtils.paraRecChkNullLong(recResult,
	// "YD_WRK_ABLE_WT");
	//
	// // 리턴 recordSet 생성
	// rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	// // Lot 편성 대상 재료 Select
	// blnRtnVal = chkGetCoilManualWrkLotGp(msgRecord, rsResult);
	// if(!blnRtnVal) return ;
	//
	// // Lot 편성 매수
	// intLotGpSh = rsResult.size();
	//
	// // 레코드 생성
	// recOutPara = JDTORecordFactory.getInstance().create();
	// // JMS TC CODE
	// //recOutPara.setField("JMS_TC_CD", "YDYDJ261");
	// // 레코드 커서 처음으로
	// //rsResult.first();
	//
	// // 보급 Lot 편성 재료 매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
	// for (int Loop_i = 1; Loop_i <= intLotGpSh; Loop_i++){
	// rsResult.absolute(Loop_i);
	// // 레코드 추출
	// recPara = rsResult.getRecord();
	// // if(Loop_i == 1){
	// // szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");
	// // szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
	// // }
	//
	// // 재료번호
	// szSTL_NO = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
	// // 재료중량
	// lngSumYD_MTL_WT += ydDaoUtils.paraRecChkNullLong(recPara, "YD_MTL_WT");
	//
	// if( lngSumYD_MTL_WT > lngYD_WRK_ABLE_WT ) break;
	// // 적치단재료상태
	// //szYD_STK_LYR_MTL_STAT = ydDaoUtils.paraRecChkNull(recPara,
	// "YD_STK_LYR_MTL_STAT");
	//
	// // 다른 크레인 스케줄에 등록 되어 있는지 체크
	// blnRtnVal = chkCrnWrkMtl(szSTL_NO);
	// if(!blnRtnVal) return ;
	//
	// // 재료번호
	// recOutPara.setField("STL_NO" + Loop_i, szSTL_NO);
	// // 권상모음순서
	// recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, "" + Loop_i);
	// intYD_LOT_GP_SH = Loop_i;
	// }
	//
	// // 발생 일시
	// recOutPara.setField("JMS_TC_CREATE_DDTT",
	// YdUtils.getCurDate("yyyy/MM/dd HH:mm:ss"));
	// // 스케줄코드
	// recOutPara.setField("YD_SCH_CD", szYD_SCH_CD);
	// // Lot편성 매수
	// recOutPara.setField("YD_LOT_GP_SH", "" + intYD_LOT_GP_SH);
	// // 야드구분
	// recOutPara.setField("YD_GP", szYD_GP);
	// // 동구분
	// recOutPara.setField("YD_BAY_GP", szYD_BAY_GP);
	// // 목표야드구분
	// recOutPara.setField("YD_AIM_YD_GP", szYD_GP);
	// // 목표동구분
	// recOutPara.setField("YD_AIM_BAY_GP", szYD_BAY_GP);
	//
	// //전문 로그 출력
	// ydUtils.displayRecord(szOperationName, recOutPara);
	//
	// // 전문 송신
	// //ydDelegate.sendMsg(recOutPara);
	// //메소드 호출
	// procCoilManualWrkReq(recOutPara);
	// szMsg = "코일 매뉴얼 작업 Lot편성 후 코일 매뉴얼 작업요구 송신 완료!";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	//
	// } catch(Exception e){
	// szMsg = "코일 매뉴얼 작업 Lot편성 Error : " + e.getMessage();
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	// }
	//
	// } //end of procCoilManualWrkLotComp
	//

	// /**
	// * 오퍼레이션명 : 코일 매뉴얼 작업 Lot편성 데이터 유무체크 및 데이터 반환
	// *
	// * @param String szSchCd 스케줄코드
	// * JDTORecordSet rsResult 결과레코드셋
	// * @return boolean true(성공), false(실패)
	// * @throws JDTOException
	// */
	// public boolean chkGetCoilManualWrkLotGp(JDTORecord recInPara,
	// JDTORecordSet rsResult)throws JDTOException {
	//
	// //저장품 DAO
	// YdStockDao ydStockDao = new YdStockDao();
	// //리턴값(boolean)
	// boolean blnRtnVal = false;
	// //리턴값(int)
	// int intRtnVal = 0;
	// //메소드명
	// String szMethodName = "chkGetCoilManualWrkLotGp";
	// String szMsg = null;
	//
	// //레코드 선언
	// JDTORecord recPara = null;
	//
	// try {
	// //레코드 생성
	// recPara = JDTORecordFactory.getInstance().create();
	//
	// //저장품 테이블 조회
	// intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 30);
	// //대상 소재가 존재하면 리턴
	// if(intRtnVal > 0){
	//
	// return blnRtnVal = true;
	//
	// } else if(intRtnVal == 0){
	//
	// szMsg = "코일 매뉴얼 작업 Lot편성 데이터가 없습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// blnRtnVal = false;
	//
	// } else if(intRtnVal == -2){
	//
	// szMsg = "코일 매뉴얼 작업 Lot편성 데이터 조회중 parameter error 발생!";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// blnRtnVal = false;
	//
	// } else {
	//
	// szMsg = "코일 매뉴얼 작업 Lot편성 데이터 조회중 오류 발생!";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// blnRtnVal = false;
	//
	// }
	//
	// } catch(Exception e){
	// szMsg = "코일 매뉴얼 작업 Lot편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: " +
	// e.getMessage();
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// blnRtnVal = false;
	// }
	// return blnRtnVal;
	// } //end of chkGetCoilManualWrkLotGp
	//
	//

	// /**
	// * 오퍼레이션명 : 코일 매뉴얼 작업요구
	// *
	// * @param msgRecord
	// * @return
	// * @throws JDTOException
	// */
	// public void procCoilManualWrkReq(JDTORecord msgRecord) throws
	// JDTOException {
	//
	// // 스케줄기준 DAO
	// YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
	// // 작업예약 재료 DAO
	// YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
	// // 공용 DAO METHOD
	// YdDaoUtils ydDaoUtils = new YdDaoUtils();
	//
	// // 리턴값(boolean)
	// boolean blnRtnVal = false;
	// // 리턴값(int)
	// int intRtnVal = 0;
	// // 메세지
	// String szMsg = "";
	// // METHOD명
	// String szMethodName = "procCoilManualWrkReq";
	// // 사용자
	// String szUser = "SYSTEM";
	//
	// // 레코드 선언
	// JDTORecord recPara = null;
	// JDTORecord recStkPara = null;
	// // 레코드셋 선언
	// JDTORecordSet rsResult = null;
	//
	// //야드구분
	// String szYD_GP = null;
	// //동구분
	// String szYD_BAY_GP = null;
	// //야드구분
	// String szYD_AIM_YD_GP = null;
	// //동구분
	// String szYD_AIM_BAY_GP = null;
	// // 재료매수(int)
	// int intMtlCnt = 0;
	// // 재료번호
	// String[] szSTL_NO = null;
	// // 권상모음순서
	// String[] szYD_UP_COLL_SEQ = null;
	// // 스케줄코드
	// String szYD_SCH_CD = null;
	// // 야드스케쥴우선순위
	// String szYD_SCH_PRIOR = null;
	// // 스케줄 금지 유무
	// String szYD_SCH_PROH_EXN = null;
	// // 작업크레인
	// String szYD_WRK_CRN = null;
	// // 작업크레인우선순위
	// String szYD_WRK_CRN_PRIOR = null;
	// // 대체크레인유무
	// String szYD_ALT_CRN_YN = null;
	// // 대체크레인
	// String szYD_ALT_CRN = null;
	// // 대체크레인우선순위
	// String szYD_ALT_CRN_PRIOR = null;
	// // 선택크레인
	// String szCrn = null;
	// // 작업예약ID
	// String szYD_WBOOK_ID = null;
	//
	// // TC CODE 추출
	// String szRcvTcCode = ydUtils.getTcCode(msgRecord);
	// // 에러 리턴
	// if(szRcvTcCode == null){
	//
	// szMsg = szSessionName + "::" + szMethodName + "() TC Code Error ("
	// + szRcvTcCode + ")";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	//
	// }
	// // TC CODE DISPLAY
	// if(bDebugFlag){
	//
	// szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	//
	// }
	//
	// try {
	//
	// // 받은 전문 편집
	// // 스케줄코드
	// szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
	// if(szYD_SCH_CD.equals("")){
	//
	// szMsg = "[전문 이상] 스케줄코드가 없습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	//
	// }
	// //야드구분
	// szYD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP");
	// if(szYD_GP.equals("")){
	//
	// szMsg = "[전문 이상] 야드구분이 없습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	//
	// }
	// //동구분
	// szYD_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP");
	// if(szYD_BAY_GP.equals("")){
	//
	// szMsg = "[전문 이상] 동구분이 없습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	//
	// }
	//
	// //야드구분
	// szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_YD_GP");
	// if(szYD_AIM_YD_GP.equals("")){
	//
	// szMsg = "[전문 이상] 목표야드구분이 없습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	//
	// }
	// //동구분
	// szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_BAY_GP");
	// if(szYD_AIM_BAY_GP.equals("")){
	//
	// szMsg = "[전문 이상] 목표동구분이 없습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	//
	// }
	//
	// // 재료매수
	// intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_LOT_GP_SH");
	// if(intMtlCnt == 0){
	//
	// szMsg = "[전문 이상] 재료매수가 없습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	//
	// }
	//
	//
	// // 재료번호
	// szSTL_NO = new String[intMtlCnt + 1];
	// // 권상모음순서
	// szYD_UP_COLL_SEQ = new String[intMtlCnt + 1];
	//
	// // 재료번호, 권상모음순서
	// for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
	//
	// // 재료번호
	// szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,
	// "STL_NO" + Loop_i);
	// if(szSTL_NO[Loop_i].equals("")){
	//
	// szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	//
	// }
	// // 권상모음순서
	// szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,
	// "YD_UP_COLL_SEQ" + Loop_i);
	// if(szYD_UP_COLL_SEQ[Loop_i].equals("")){
	//
	// szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i]
	// + ")에 대한 권상모음순서가 없습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	//
	// }
	// }
	//
	// // 리턴 recordSet 생성
	// rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	//
	// // 스케줄 기준 체크
	// blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
	// if(!blnRtnVal) return ;
	//
	// // 레코드 추출
	// rsResult.first();
	// recPara = rsResult.getRecord();
	//
	// // 스케줄CD 체크
	// // 스케줄 금지 유무
	// szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara,
	// "YD_SCH_PROH_EXN");
	//
	// // 스케줄 금지 유무가 "Y"이면 처리를 중지하고 유스케이스를 종료한다.
	// if(szYD_SCH_PROH_EXN.equals("Y")){
	//
	// szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	// }
	//
	// // 작업크레인
	// szYD_WRK_CRN = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
	// // 작업크레인우선순위
	// szYD_WRK_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara,
	// "YD_WRK_CRN_PRIOR");
	// // 대체크레인유무
	// szYD_ALT_CRN_YN = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
	// // 대체크레인
	// szYD_ALT_CRN = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
	// // 대체크레인우선순위
	// szYD_ALT_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara,
	// "YD_ALT_CRN_PRIOR");
	//
	// // 작업크레인 설비 상태 체크
	// blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);
	//
	// // 작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
	// if(!blnRtnVal){
	//
	// szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	//
	// // 대체크레인의 유무를 체크한다.
	// // 대체크레인이 없으면 에러 리턴
	// if(!szYD_ALT_CRN_YN.equals("Y")){
	//
	// szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	//
	// }
	// // 대체크레인이 있으면 대체크레인 설비 상태 체크
	// blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
	// // 대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
	// if(!blnRtnVal){
	//
	// szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	//
	// } else {
	// // 대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
	// szCrn = szYD_ALT_CRN;
	// szYD_SCH_PRIOR = szYD_ALT_CRN_PRIOR;
	// }
	// } else {
	// // 작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
	// szCrn = szYD_WRK_CRN;
	// szYD_SCH_PRIOR = szYD_WRK_CRN_PRIOR;
	// }
	//
	// // 재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다.
	// // 작업예약재료 등록 여부를 체크한다.
	// for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
	//
	// // 크레인사양과 저장품 사양을 체크(길이,폭,중량)
	// blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
	// if(!blnRtnVal)
	// return ;
	//
	// // 다른 작업예약에 재료가 등록되어있는지 체크한다.
	// blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
	// if(!blnRtnVal)
	// return ;
	//
	// }
	//
	// // 리턴 recordSet 생성
	// rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	//
	// // 작업예약ID 생성
	// blnRtnVal = getYdWbookId(rsResult);
	// if(!blnRtnVal)
	// return ;
	// // 레코드추출
	// rsResult.first();
	// recPara = rsResult.getRecord();
	// // 작업예약ID
	// szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");
	//
	// // INSERT 항목 record 생성
	// recPara = JDTORecordFactory.getInstance().create();
	//
	// // INSERT할 항목 SET
	// recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID); // 작업예약ID
	// recPara.setField("YD_GP", szYD_GP); // 야드구분
	// recPara.setField("YD_BAY_GP", szYD_BAY_GP); // 야드동구분
	// recPara.setField("YD_SCH_CD", szYD_SCH_CD); // 야드스케쥴코드
	// recPara.setField("YD_SCH_PRIOR", szYD_SCH_PRIOR); // 야드스케쥴우선순위
	// recPara.setField("YD_AIM_YD_GP", szYD_AIM_YD_GP); // 야드목표야드구분
	// recPara.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP); // 야드목표동구분
	// // 야드스케쥴진행상태
	// // 야드스케쥴기동구분
	// // 야드스케쥴요청구분
	// recPara.setField("REGISTER", szUser);
	// // 작업예약 INSERT
	// intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
	// if(intRtnVal < 1){
	// szMsg = "작업예약 데이터 등록 중 에러";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return ;
	// }
	//
	// // 조회항목 record 생성
	// recPara = JDTORecordFactory.getInstance().create();
	//
	// recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
	// // recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
	// // recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
	// recPara.setField("REGISTER", szUser);
	//
	// for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
	//
	// // 리턴 recordSet 생성
	// rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	// // 재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
	// intRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C",
	// rsResult);
	// if(intRtnVal != 1){
	// szMsg = "재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져오는 중 에러";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// // 예외를 발생시켜 롤백 시킴
	// throw new DAOException(szSessionName + " : " + szMethodName
	// + " - " + szMsg);
	// // return ;
	// }
	//
	// // 레코드추출
	// rsResult.first();
	// recStkPara = rsResult.getRecord();
	//
	// // 재료번호
	// recPara.setField("STL_NO", szSTL_NO[Loop_i]);
	// // 적치열구분
	// recPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(
	// recStkPara, "YD_STK_COL_GP"));
	// // 적치BED번호
	// recPara.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(
	// recStkPara, "YD_STK_BED_NO"));
	// // 적치단번호
	// recPara.setField("YD_STK_LYR_NO", ydDaoUtils.paraRecChkNull(
	// recStkPara, "YD_STK_LYR_NO"));
	// // 권상모음순서
	// recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ[Loop_i]);
	//
	// // 작업예약재료 테이블에 등록한다.
	// intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
	//
	// if(intRtnVal < 1){
	// szMsg = "작업예약재료 데이터 등록 중 에러";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// // 예외를 발생시켜 롤백 시킴
	// throw new DAOException(szSessionName + " : " + szMethodName
	// + " - " + szMsg);
	// // return ;
	// }
	// }
	//
	// //C열연크레인스케줄Main 호출
	// recPara = JDTORecordFactory.getInstance().create();
	// recPara.setField("JMS_TC_CD", "YDYDJ509");
	// recPara.setField("YD_SCH_CD", szYD_SCH_CD);
	// recPara.setField("YD_EQP_ID", szCrn);
	// ydDelegate.sendMsg(recPara);
	//
	// } catch (JDTOException e){
	// szMsg = "코일 매뉴얼 작업요구  처리중 Error : " + e.getMessage();
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// throw new DAOException(e);
	// }
	// } // end of procCoilManualWrkReq()
	//
	//
	//

	/**
	 * 오퍼레이션명 : 코일제품출하상차Lot편성(YDYDJ282)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String procCoilGdsDistCarLdComp(JDTORecord msgRecord)
			throws JDTOException {

		YdDelegate ydDelegate = new YdDelegate();
		// 공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		YdCarSchDao ydCarSchDao = new YdCarSchDao();		//차량스케줄DAO
		// 레코드 선언
		JDTORecord recPara = null;
		JDTORecord recOutPara = null;
		JDTORecord recInTemp = null;
		JDTORecord inRec=   JDTORecordFactory.getInstance().create();
		// 레코드셋 선언
		JDTORecordSet rsResult = null;
		JDTORecordSet outRecSet = null;
		
		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 메세지
		String szMsg = "";
		// 메소드명
		String szMethodName = "procCoilGdsDistCarLdComp";
		String szOperationName = "코일제품출하상차Lot편성(YDYDJ282)";

		// 전문 생성 일시
		String szDate = "";

		// 설비ID
		String szYD_EQP_ID = "";
		// 상차정지위치
		String szYD_CARLD_STOP_LOC = "";
		// 차량사용구분
		String szYD_CAR_USE_GP = "";
		// 차량번호
		String szCAR_NO = "";
		// 카드번호
		String szCARD_NO = "";
		// 재료품목
		// String szYD_MTL_ITEM = "";
		// 스케줄코드
		String szYD_SCH_CD = "";
		// 차량작업허용매수
		int intYD_WRK_ALW_SH = 0;
		String szTRANS_ORD_DATE = "";
		String szTRANS_ORD_SEQNO = "";
		String szYD_GP = "";
		String szYD_BAY_GP = "";
		String szSPOS_WLOC_CD = "";
		String szSPOS_YD_PNT_CD = "";
		String szCAR_KIND = "";
		String szCAR_GP = "";
		String szYDPOINT_GP = "";
		String szYD_CAR_PROG_STAT = "1";
		String szYD_CAR_CHK_YN	="N";
		String szCR_FRTOMOVE_GP ="";
		String szRtnMsg = null;
		int intRtnVal =0;
		
		
		// TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		// 에러 리턴
		if (szRcvTcCode == null || szRcvTcCode.equals("")) {
			szMsg = "[" + szOperationName + "] TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
			return YdConstant.RETN_CD_TC_ERROR;
		}

		// TC CODE DISPLAY
		if (bDebugFlag) {
			szMsg = "[" + szOperationName + "] 전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.DEBUG);
		}

		try {
			szMsg = "[" + szOperationName + "] 메소드 시작 : 전문내용 확인 시작";
			ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.DEBUG);
			ydUtils.displayRecord(szOperationName , msgRecord);

			// 상차정지위치
			szYD_CARLD_STOP_LOC = ydDaoUtils.paraRecChkNull(msgRecord , "YD_CARLD_STOP_LOC");
			if (szYD_CARLD_STOP_LOC.equals("")) {

				szMsg = "[" + szOperationName + "] [전문 이상] 상차정지위치가 없습니다.";
				ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}
			// 차량사용구분
			szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(msgRecord , "YD_CAR_USE_GP");
			if (szYD_CAR_USE_GP.equals("")) {

				szMsg = "[" + szOperationName + "] [전문 이상] 차량사용구분(" + szYD_CAR_USE_GP + ")";
				ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}
			// 차량번호
			szCAR_NO = ydDaoUtils.paraRecChkNull(msgRecord , "CAR_NO");
			if (szCAR_NO.equals("")) {

				szMsg = "[" + szOperationName + "] [전문 이상] 차량번호가 없습니다.";
				ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}
			
			// 운송지시일자
			szTRANS_ORD_DATE = ydDaoUtils.paraRecChkNull(msgRecord , "TRANS_ORD_DATE");
			if (szTRANS_ORD_DATE.equals("")) {
				szMsg = "[" + szOperationName + "] [전문 이상] 운송지시일자 없습니다.";
				ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}

			// 운송지시순번
			szTRANS_ORD_SEQNO = ydDaoUtils.paraRecChkNull(msgRecord , "TRANS_ORD_SEQNO");
			if (szTRANS_ORD_SEQNO.equals("")) {
				szMsg = "[" + szOperationName + "] [전문 이상] 운송지시순번 없습니다.";
				ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}

			// 야드구분
			szYD_GP = ydDaoUtils.paraRecChkNull(msgRecord , "YD_GP");
			if (szYD_GP.equals("")) {
				szMsg = "[전문 이상] 야드구분 없습니다.";
				ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}
			
			// PIDEV
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "", "APPPI0", "J", "*");				
			
			// PIDEV
//			if("N".equals(sApplyYnPI)) {
//				// 카드번호
//				szCARD_NO = ydDaoUtils.paraRecChkNull(msgRecord , "CARD_NO");
//			
//				if (szCARD_NO.equals("")) {
//	
//					szMsg = "[" + szOperationName + "] [전문 이상] 카드번호가 없습니다.";
//					ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
//					return YdConstant.RETN_CD_NO_PARAM;
//	
//				}
//			}

			// 동구분
			szYD_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord , "YD_BAY_GP");
			if (szYD_BAY_GP.equals("")) {
				szMsg = "[전문 이상] 동구분 없습니다.";
				ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}

			// 발지개소코드
			szSPOS_WLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord , "SPOS_WLOC_CD");
			if (szSPOS_WLOC_CD.equals("")) {
				szMsg = "[전문 이상] 발지개소코드 없습니다.";
				ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}

			// 발지야드포인트코드
			szSPOS_YD_PNT_CD = ydDaoUtils.paraRecChkNull(msgRecord , "SPOS_YD_PNT_CD");
			if (szSPOS_YD_PNT_CD.equals("")) {
				szMsg = "[전문 이상] 발지야드포인트코드 없습니다.";
				ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}

			// =================================================================================
			// 수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			// BRE 등록 안됨...테스트용 스케줄코드 생성
			// 추후 구현..
			// szYD_SCH_CD = "JATR01UM";

			// 차량구분 TT:TT CAR , T:Trailer
			szCAR_KIND = ydDaoUtils.paraRecChkNull(msgRecord, "CAR_KIND");

			//육송출하고도화
			ymCommonDAO dao = ymCommonDAO.getInstance();
			 List chkList = null;
			String QueryId 	= "com.inisteel.cim.yd.dao.chklist";
			chkList = dao.getCommonList(QueryId, new Object[]{});

		    JDTORecord unloadPointrec = (JDTORecord)chkList.get(0);
	    	String CHK   = StringHelper.evl(unloadPointrec.getFieldString("CHK"), "");	    	
	    	ydUtils.putLog(szSessionName, szMethodName, "◑◑◑◑◑ TC_CODE:"+szMethodName+" , CHK:"+CHK, YdConstant.INFO);
	    	
	    	if(CHK.equals("Y")){
	    		//---------------TO-BE----------------------
				// 차량구분
				if ("TT".equals(szCAR_KIND)) {
					szCAR_GP = "TT";
				} else if ("PT".equals(szCAR_KIND)) {
					szCAR_GP = "PT";
				} else if ("T".equals(szCAR_KIND)|| "TR".equals(szCAR_KIND)) {
					szCAR_GP = "TR";
				} else {
					szCAR_GP = szYD_CARLD_STOP_LOC.substring(2, 4);
				}
			
	    	}else{
	    		//---------------AS-IS----------------------
	    		// 차량구분
				if ("TT".equals(szCAR_KIND)) {
					szCAR_GP = "PT";
				} else if ("T".equals(szCAR_KIND)||"TR".equals(szCAR_KIND)) {
					szCAR_GP = "TR";
				} else {
					szCAR_GP = szYD_CARLD_STOP_LOC.substring(2, 4);
				}
	    	}
	    	
			

			
			// =================================================================================
			// 스케줄 코드 생성 
			// =================================================================================
			outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("TRANS_ORD_DATE" , szTRANS_ORD_DATE);
			recInTemp.setField("TRANS_ORD_SEQNO" , szTRANS_ORD_SEQNO);
//PIDEV_S :병행가동용:PI_YD
			recInTemp.setField("PI_YD",    	szYD_GP);		
			/* com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschByTransDTSeq_PIDEV */
			intRtnVal = ydCarSchDao.getYdCarsch(recInTemp , outRecSet , 34);

			if (intRtnVal > 0) {
				outRecSet.first();
				inRec = outRecSet.getRecord();
				szYD_CAR_PROG_STAT = StringHelper.evl(inRec.getFieldString("YD_CAR_PROG_STAT") , "");
				szCR_FRTOMOVE_GP = StringHelper.evl(inRec.getFieldString("CR_FRTOMOVE_GP") , "");
//				szYD_CAR_CHK_YN = StringHelper.evl(inRec.getFieldString("CHK_YN") , ""); //출하 해송유무 판단
				
			}
			
			ydUtils.putLog(szSessionName, szMethodName, "◑◑◑◑◑ szYD_CAR_PROG_STAT:"+szYD_CAR_PROG_STAT+" , szSPOS_YD_PNT_CD:"+szSPOS_YD_PNT_CD, YdConstant.INFO);

			if ("1".equals(szYD_CAR_PROG_STAT) || "2".equals(szYD_CAR_PROG_STAT)) {
				// 출하 상차 작업( 1통로 2통로 구분)
				if ("5".equals(szSPOS_YD_PNT_CD.substring(0 , 1))) { //2통로 
					szYDPOINT_GP = "5";
				}else if ("3".equals(szSPOS_YD_PNT_CD.substring(0 , 1))) { //공냉재 
					szYDPOINT_GP = "3";
				} else {
					szYDPOINT_GP = "0"; //1통로 
				}

				
				if("63".equals(szCR_FRTOMOVE_GP)|| "11".equals(szCR_FRTOMOVE_GP)){
					szYD_SCH_CD = szYD_CARLD_STOP_LOC.substring(0 , 2) + "TR" + szYDPOINT_GP + "1UM"; //1,2통로 임가공(63), 수출사내(11)
				}else{
					szYD_SCH_CD = szYD_CARLD_STOP_LOC.substring(0 , 2) + "PT" + szYDPOINT_GP + "2UM"; //1,2통로 이송출고(81)
				}
			} else {

				// 출하 하차 작업 (1통로 2통로 구분)
				if (szSPOS_YD_PNT_CD.substring(0 , 1).equals("5")) {
					szYDPOINT_GP = "1";
				} else {
					szYDPOINT_GP = "2";
				}

				szYD_SCH_CD = szYD_CARLD_STOP_LOC.substring(0 , 2) + "PT0" + szYDPOINT_GP + "LM";
			}
			// =================================================================================

			
			ydUtils.putLog(szSessionName, szMethodName, "◑◑◑◑◑ 크레인 스케쥴코드:"+szYD_SCH_CD+" , 차량분류구분:"+szCR_FRTOMOVE_GP , YdConstant.INFO);

			
			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 상차 Lot 편성 대상 재료 Select
			blnRtnVal = chkDistCarLoadLotGp(msgRecord, rsResult);
			if (!blnRtnVal)
				return YdConstant.RETN_CD_FAILURE;

			intYD_WRK_ALW_SH = rsResult.size();

			// 레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			// JMS TC CODE
			recOutPara.setField("JMS_TC_CD", "YDYDJ292");

			for (int Loop_i = 1; Loop_i <= intYD_WRK_ALW_SH; Loop_i++) {
				rsResult.absolute(Loop_i);
				recPara = rsResult.getRecord();
				// 재료번호
				recOutPara.setField("STL_NO" + Loop_i,
						ydDaoUtils.paraRecChkNull(recPara, "STL_NO"));
				recOutPara.setField("YD_STK_COL_GP" + Loop_i,
						ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP"));
				recOutPara.setField("YD_STK_BED_NO" + Loop_i,
						ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO"));
				recOutPara.setField("YD_STK_LYR_NO" + Loop_i,
						ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO"));
				// 권상모음순서
				recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, "" + Loop_i);

			}

			szYD_EQP_ID = YdConstant.YD_DM_CAR_EQP_ID;

			// 전문 발생 일시
			szDate = YdUtils.getCurDate("yyyy/MM/dd HH:mm:ss");

			// 발생 일시
			recOutPara.setField("JMS_TC_CREATE_DDTT", szDate);
			// 스케줄코드
			recOutPara.setField("YD_SCH_CD", szYD_SCH_CD);
			// 재료매수
			recOutPara.setField("YD_CARLD_SH", "" + intYD_WRK_ALW_SH);
			// 설비id
			recOutPara.setField("YD_EQP_ID", szYD_EQP_ID);
			// 차량사용구분
			recOutPara.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
			// 상차 정지위치
			recOutPara.setField("YD_CARLD_STOP_LOC", szYD_CARLD_STOP_LOC);
			// 차량번호
			recOutPara.setField("CAR_NO", szCAR_NO);
			// 카드번호
			recOutPara.setField("CARD_NO", szCARD_NO);
			// 운송지시일자
			recOutPara.setField("TRANS_ORD_DATE", szTRANS_ORD_DATE);
			// 운송지시순번
			recOutPara.setField("TRANS_ORD_SEQNO", szTRANS_ORD_SEQNO);
			// 야드구분
			// recOutPara.setField("YD_GP", szYD_GP);
			// 동구분
			// recOutPara.setField("YD_BAY_GP", szYD_BAY_GP);
			// 발지개소코드
			recOutPara.setField("SPOS_WLOC_CD", szSPOS_WLOC_CD);
			// 발지포인트코드
			recOutPara.setField("SPOS_YD_PNT_CD", szSPOS_YD_PNT_CD);
			szMsg = "코일제품출하상차Lot편성 후 차량상차 작업요구 송신 시작 - 전문내용 표시";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			ydUtils.displayRecord(szOperationName, recOutPara);
			ydDelegate.sendMsg(recOutPara);

			// szRtnMsg = this.procCoilGdsDistCarLdWrkReq(recOutPara);
			szMsg = "코일제품출하상차Lot편성 후 차량상차 작업요구 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		} catch (Exception e) {
			szMsg = "코일제품출하상차Lot편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;
		}
		return szRtnMsg;
	} // end of procCoilGdsDistCarLdComp

	/**
	 * 오퍼레이션명 : 코일제품출하차량상차작업요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String procCoilGdsDistCarLdWrkReq(JDTORecord msgRecord) throws JDTOException {

		YdDelegate ydDelegate = new YdDelegate();
		// 스케줄기준 DAO
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		// 작업예약 재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		// 차량스케줄DAO
		YdCarSchDao ydCarSchDao = new YdCarSchDao();
		// 공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메세지
		String szMsg = "";
		// METHOD명
		String szMethodName = "procCoilGdsDistCarLdWrkReq";
		String szOperationName = "코일제품출하차량상차작업요구";
		// 사용자
		String szUser = "SYSTEM";

		// 레코드 선언
		JDTORecord recPara = null;
		JDTORecord recInTemp = null;
		// 레코드셋 선언
		JDTORecordSet rsResult = null;
		JDTORecordSet rsTemp = null;

		// 설비ID
		String szYD_EQP_ID = null;
		// 차량사용구분
		String szYD_CAR_USE_GP = "";
		// 재료매수(int)
		int intMtlCnt = 0;
		// 재료번호
		String szSTL_NO = null;
		// 권상모음순서
		String szYD_UP_COLL_SEQ = null;
		// 스케줄코드
		String szYD_SCH_CD = null;
		String szCURR_YD_SCH_CD = null;
		// 야드작업크레인우선순위
		String szYD_SCH_PRIOR = null;
		// 선택크레인
		String szCrn = null;
		String szCurrCrn = null;
		// 작업예약ID
		String szYD_WBOOK_ID = null;
		String szCURR_YD_WBOOK_ID = null;
		// 발지개소코드
		String szSPOS_WLOC_CD = null;
		// 발지포인트코드
		String szSPOS_YD_PNT_CD = null;
		// 적치열구분
		String szYD_STK_COL_GP = null;
		String szPREV_YD_STK_COL_GP = null;
		String szYD_STK_BED_NO = null;
		String szYD_STK_LYR_NO = null;
		// 야드구분
		String szYD_GP = null;
		// 야드동구분
		String szYD_BAY_GP = null;
		// 야드목표야드구분
		String szYD_AIM_YD_GP = null;
		// 야드목표동구분
		String szYD_AIM_BAY_GP = null;
		// 차량스케줄ID
		String szYD_CAR_SCH_ID = null;
		
		String szTRANS_EQUIPMENT_TYPE = null;
		// 상차정지위치
		String szYD_CARLD_STOP_LOC = null;
		String szTRANS_ORD_DATE = null;
		String szTRANS_ORD_SEQNO = null;
		String szCAR_NO = "";
		String szCARD_NO = "";
		Vector vGroup = new Vector();
		String[] szTC_CODE = null;

		// PIDEV
//		String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "", "APPPI0", "J", "*");		
		
		// TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);

		// 에러 리턴
		if (szRcvTcCode == null || szRcvTcCode.equals("")) {

			szMsg = "[" + szOperationName + "] TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
			return YdConstant.RETN_CD_TC_ERROR;

		}
		// TC CODE DISPLAY
		if (bDebugFlag) {
			szMsg = "[" + szOperationName + "] 전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.DEBUG);
		}

		try {
			szMsg = "[" + szOperationName + "] 메소드 시작 : 전문내용확인 시작";
			ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.DEBUG);
			ydUtils.displayRecord(szOperationName , msgRecord);

			// 스케줄코드
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord , "YD_SCH_CD");
			if (szYD_SCH_CD.equals("")) {

				szMsg = "[" + szOperationName + "] [전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}
			// 설비ID
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord , "YD_EQP_ID");
			szMsg = "[" + szOperationName + "] hun szYD_EQP_ID =["+szYD_EQP_ID+"]";
			ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
			if (szYD_EQP_ID.equals("")) {

				szMsg = "[" + szOperationName + "] [전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}

			// 차량사용구분
			szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(msgRecord , "YD_CAR_USE_GP");
			if (szYD_CAR_USE_GP.equals("")) {

				szMsg = "[" + szOperationName + "] [전문 이상] 차량사용구분이 없습니다.";
				ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}

			// 차량번호
			szCAR_NO = ydDaoUtils.paraRecChkNull(msgRecord , "CAR_NO");
			if (szCAR_NO.equals("")) {

				szMsg = "[" + szOperationName + "] [전문 이상] 차량번호가 없습니다.";
				ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}

			// PIDEV
//			if("N".equals(sApplyYnPI)) {						
//
//				// 카드번호
//				szCARD_NO = ydDaoUtils.paraRecChkNull(msgRecord , "CARD_NO");				
//				
//				if (szCARD_NO.equals("")) {
//	
//					szMsg = "[" + szOperationName + "] [전문 이상] 카드번호가 없습니다.";
//					ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
//					return YdConstant.RETN_CD_NO_PARAM;
//	
//				}
//
//			}
			
			// 발지개소코드
			szSPOS_WLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord , "SPOS_WLOC_CD");
			if (szSPOS_WLOC_CD.equals("")) {

				szMsg = "[" + szOperationName + "] [전문 이상] 발지개소코드가 없습니다.";
				ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}

			// 발지포인트코드
			szSPOS_YD_PNT_CD = ydDaoUtils.paraRecChkNull(msgRecord , "SPOS_YD_PNT_CD");
			if (szSPOS_YD_PNT_CD.equals("")) {

				szMsg = "[" + szOperationName + "] [전문 이상] 발지포인트코드가 없습니다.";
				ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}

			// 재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord , "YD_CARLD_SH");
			if (intMtlCnt == 0) {

				szMsg = "[" + szOperationName + "] [전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}

			// 상차정지위치
			szYD_CARLD_STOP_LOC = ydDaoUtils.paraRecChkNull(msgRecord , "YD_CARLD_STOP_LOC");
			
			szMsg = "[" + szOperationName + "] [hun szYD_CARLD_STOP_LOC] ="+szYD_CARLD_STOP_LOC;
			ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
			
			if (szYD_CARLD_STOP_LOC.equals("")) {

				szMsg = "[" + szOperationName + "] [전문 이상] 상차정지위치가 없습니다.";
				ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}

			// 운송지시일자
			szTRANS_ORD_DATE = ydDaoUtils.paraRecChkNull(msgRecord , "TRANS_ORD_DATE");
			if (szTRANS_ORD_DATE.equals("")) {

				szMsg = "[" + szOperationName + "] [전문 이상] 운송지시일자가 없습니다.";
				ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}

			// 운송지시순번
			szTRANS_ORD_SEQNO = ydDaoUtils.paraRecChkNull(msgRecord , "TRANS_ORD_SEQNO");
			if (szTRANS_ORD_SEQNO.equals("")) {

				szMsg = "[" + szOperationName + "] [전문 이상] 운송지시순번이 없습니다.";
				ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}

			int seq = 0;
			// 재료번호, 권상모음순서
			for (int i = 0; i < intMtlCnt; i++) {
				// 재료번호
				szSTL_NO = ydDaoUtils.paraRecChkNull(msgRecord , "STL_NO" + (i + 1));
				if (szSTL_NO.equals("")) {
					szMsg = "[" + szOperationName + "] [전문 이상] " + i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;
				}

				szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord , "YD_STK_COL_GP" + (i + 1));
				if (i == 0) {
					seq = 1;
					szPREV_YD_STK_COL_GP = szYD_STK_COL_GP;
					rsResult = JDTORecordFactory.getInstance().createRecordSet("");
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("STL_NO" , szSTL_NO);
					recInTemp.setField("YD_UP_COLL_SEQ" , "" + seq);
					recInTemp.setField("YD_STK_COL_GP" , szYD_STK_COL_GP);
					recInTemp.setField("YD_STK_BED_NO" , ydDaoUtils.paraRecChkNull(msgRecord , "YD_STK_BED_NO" + (i + 1)));
					recInTemp.setField("YD_STK_LYR_NO" , ydDaoUtils.paraRecChkNull(msgRecord , "YD_STK_LYR_NO" + (i + 1)));
					rsResult.addRecord(recInTemp);
					vGroup.add(rsResult);
				} else {
					if (!szYD_STK_COL_GP.substring(0 , 2).equals(szPREV_YD_STK_COL_GP.substring(0 , 2))) {
						rsResult = JDTORecordFactory.getInstance().createRecordSet("");
						vGroup.add(rsResult);
						seq = 1;
					}
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("STL_NO" , szSTL_NO);
					recInTemp.setField("YD_UP_COLL_SEQ" , "" + seq);
					recInTemp.setField("YD_STK_COL_GP" , szYD_STK_COL_GP);
					recInTemp.setField("YD_STK_BED_NO" , ydDaoUtils.paraRecChkNull(msgRecord , "YD_STK_BED_NO" + (i + 1)));
					recInTemp.setField("YD_STK_LYR_NO" , ydDaoUtils.paraRecChkNull(msgRecord , "YD_STK_LYR_NO" + (i + 1)));
					rsResult.addRecord(recInTemp);
					szPREV_YD_STK_COL_GP = szYD_STK_COL_GP;
				}
				seq++;
			}

			szMsg = "[" + szOperationName + "] 전문확인 후 출하상차LOT 그룹 수 : " + vGroup.size();
			ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.DEBUG);

			for (int i = 0; i < vGroup.size(); i++) {
				rsTemp = (JDTORecordSet) vGroup.get(i);
				for (int j = 1; j <= rsTemp.size(); j++) {
					rsTemp.absolute(j);
					recInTemp = rsTemp.getRecord();
					szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recInTemp , "YD_STK_COL_GP");
					szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recInTemp , "YD_STK_BED_NO");
					szYD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(recInTemp , "YD_STK_LYR_NO");
					szYD_UP_COLL_SEQ = ydDaoUtils.paraRecChkNull(recInTemp , "YD_UP_COLL_SEQ");
					szSTL_NO = ydDaoUtils.paraRecChkNull(recInTemp , "STL_NO");
					if (j == 1) {

						szYD_SCH_CD = szYD_STK_COL_GP.substring(0 , 2) + szYD_SCH_CD.substring(2);
						recPara = JDTORecordFactory.getInstance().create();
						/* com.inisteel.cim.yd.dao.ydschruledao.YdSchruleDao.getYdSchrule */
						intRtnVal = YdCommonUtils.getCrnInfoByCrnSchRule(szYD_SCH_CD , recPara);
						if (intRtnVal < 0) {
							return YdConstant.RETN_CD_FAILURE;
						}
						// 야드작업크레인
						szCrn = ydDaoUtils.paraRecChkNull(recPara , "YD_WRK_CRN");
						// 야드스케줄우선순위
						szYD_SCH_PRIOR = ydDaoUtils.paraRecChkNull(recPara , "YD_SCH_PRIOR");

						// 리턴 recordSet 생성
						rsResult = JDTORecordFactory.getInstance().createRecordSet("");

						// 작업예약ID 생성
						blnRtnVal = getYdWbookId(rsResult);
						if (!blnRtnVal)
							return YdConstant.RETN_CD_FAILURE;

						// 레코드추출
						rsResult.first();
						recPara = rsResult.getRecord();

						// 작업예약ID
						szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");

						if (szYD_CARLD_STOP_LOC.substring(0 , 2).equals(szYD_STK_COL_GP.substring(0 , 2))) {
							// 현재작업예약이 등록되는 동과 차량정지위치가 같은 스케줄코드와
							szCURR_YD_SCH_CD = szYD_SCH_CD;
							szCURR_YD_WBOOK_ID = szYD_WBOOK_ID;
							szCurrCrn = szCrn;
						}

						// 리턴 RecordSet 생성
						rsResult = JDTORecordFactory.getInstance().createRecordSet("");

						// 저장품테이블 조회
						blnRtnVal = this.chkGetStock(szSTL_NO , rsResult);
						if (!blnRtnVal)
							return YdConstant.RETN_CD_FAILURE;

						// 레코드추출
						rsResult.first();
						recPara = rsResult.getRecord();

						// 야드목표야드구분
						szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(recPara , "YD_AIM_YD_GP");

						// 야드목표동구분
						szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(recPara , "YD_AIM_BAY_GP");
						 
						// INSERT 항목 record 생성
						recPara = JDTORecordFactory.getInstance().create();

						// 야드구분
						szYD_GP = szYD_SCH_CD.substring(0 , 1);

						// 동구분
						szYD_BAY_GP = szYD_SCH_CD.substring(1 , 2);

						// INSERT할 항목 SET
						recPara.setField("YD_WBOOK_ID" , szYD_WBOOK_ID);
						recPara.setField("YD_GP" , szYD_GP);
						recPara.setField("YD_BAY_GP" , szYD_BAY_GP);
						recPara.setField("YD_SCH_CD" , szYD_SCH_CD);
						recPara.setField("YD_SCH_PRIOR" , szYD_SCH_PRIOR);
						recPara.setField("REGISTER" , szUser);
						recPara.setField("YD_CAR_USE_GP" , szYD_CAR_USE_GP);
						recPara.setField("CAR_NO" , szCAR_NO);
						recPara.setField("CARD_NO" , szCARD_NO);
						recPara.setField("YD_AIM_YD_GP" , szYD_AIM_YD_GP);
						recPara.setField("YD_AIM_BAY_GP" , szYD_AIM_BAY_GP);
						// 작업예약 INSERT
						intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
						if (intRtnVal < 1) {
							szMsg = "[" + szOperationName + "] 작업예약 데이터 등록 중 에러";
							ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
							return YdConstant.RETN_CD_FAILURE;
						}
					}

					// recStk = JDTORecordFactory.getInstance().create();

					// 조회항목 record 생성
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_WBOOK_ID" , szYD_WBOOK_ID);
					recPara.setField("REGISTER" , szUser);

					recPara.setField("STL_NO" , szSTL_NO);

					// 적치열구분
					recPara.setField("YD_STK_COL_GP" , szYD_STK_COL_GP);

					// 적치BED번호
					recPara.setField("YD_STK_BED_NO" , szYD_STK_BED_NO);

					// 적치단번호
					recPara.setField("YD_STK_LYR_NO" , szYD_STK_LYR_NO);

					// 권상모음순서
					recPara.setField("YD_UP_COLL_SEQ" , szYD_UP_COLL_SEQ);

					// 작업예약재료 테이블에 등록한다.
					intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
					if (intRtnVal < 1) {
						szMsg = "[" + szOperationName + "] 작업예약재료 데이터 등록 중 에러";
						ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
						// 예외를 발생시켜 롤백 시킴
						throw new DAOException(szSessionName + " : " + szMethodName + " - " + szMsg);
					}
 
				}
			}

			// 스케줄 메소드 호출
			// recInTemp = JDTORecordFactory.getInstance().create();
			// rsTemp = JDTORecordFactory.getInstance().createRecordSet("");
			// recInTemp.setField("CAR_NO" , szCAR_NO);
			// recInTemp.setField("CARD_NO", szCARD_NO);
			// intRtnVal = ydCarSchDao.getYdCarsch(recInTemp, rsTemp, 11);
			// if(intRtnVal < 0){
			// szMsg = "["+szOperationName+"] 차량스케쥴 조회 에러";
			// ydUtils.putLog(szSessionName, szMethodName, szMsg,
			// YdConstant.ERROR);
			// return YdConstant.RETN_CD_FAILURE;
			// }else if(intRtnVal == 0){
			// /*
			// * 수정자 : 임춘수
			// * 수정일 : 2009.09.11
			// */
			// //차량스케줄 생성하는 부분은 제거처리 ----> 상차지시에서 차량스케줄을 먼저 생성하는 걸로 변경
			// //intRtnVal = this.mkY1CarSch(recPara);
			// szMsg =
			// "["+szOperationName+"] 차량스케줄이 존재하지 않습니다 - 차량스케쥴 생성 유무 결정 필요?";
			// ydUtils.putLog(szSessionName, szMethodName, szMsg,
			// YdConstant.ERROR);
			// if( intRtnVal != 1 ) return YdConstant.RETN_CD_FAILURE;
			//
			// }else {
			// /*
			// * 차량스케줄을 조회해서 상차작업예약ID와 차량진행상태 등의 상태값을 변경한다.
			// * 수정자 : 임춘수
			// * 수정일 : 2009.09.11
			// */
			// rsTemp.first();
			// recInTemp = rsTemp.getRecord();
			// szYD_CAR_SCH_ID =
			// StringHelper.evl(recInTemp.getFieldString("YD_CAR_SCH_ID"), "");
			// szMsg =
			// "["+szOperationName+"] 차량스케쥴["+szYD_CAR_SCH_ID+"] 조회 성공 후 차량도착상태로 변경 시작";
			// ydUtils.putLog(szSessionName, szMethodName, szMsg,
			// YdConstant.DEBUG);
			//
			// recInTemp = JDTORecordFactory.getInstance().create();
			// recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
			// recInTemp.setField("MODIFIER", szMethodName.substring(0, 10));
			// recInTemp.setField("YD_CARLD_WRK_BOOK_ID", szCURR_YD_WBOOK_ID);
			// recInTemp.setField("YD_CARLD_STOP_LOC", szYD_CARLD_STOP_LOC);
			// recInTemp.setField("YD_CARLD_ARR_DT",
			// YdUtils.getCurDate("yyyyMMddHHmmss"));
			// recInTemp.setField("YD_PNT_CD1" , szSPOS_YD_PNT_CD);
			// recInTemp.setField("YD_CAR_PROG_STAT", "2"); //상차도착상태
			//
			// intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 0);
			//
			// if( intRtnVal == 0 ) {
			// szMsg =
			// "["+szOperationName+"] 차량스케쥴["+szYD_CAR_SCH_ID+"] 조회 성공 후 차량도착상태로 변경 시 차량스케줄이 존재하지 않습니다.";
			// ydUtils.putLog(szSessionName, szMethodName, szMsg,
			// YdConstant.ERROR);
			// throw new DAOException(szMsg);
			// }else if( intRtnVal < 0 ) {
			// szMsg =
			// "["+szOperationName+"] 차량스케쥴["+szYD_CAR_SCH_ID+"] 조회 성공 후 차량도착상태로 변경 시 차량스케줄이 존재하지 않습니다.";
			// ydUtils.putLog(szSessionName, szMethodName, szMsg,
			// YdConstant.ERROR);
			// throw new DAOException(szMsg);
			// }
			//
			// szMsg =
			// "["+szOperationName+"] 차량스케쥴["+szYD_CAR_SCH_ID+"] 조회 성공 후 차량도착상태로 변경 완료";
			// ydUtils.putLog(szSessionName, szMethodName, szMsg,
			// YdConstant.DEBUG);
			//
			// /*
			// * 크레인스케줄Main을 호출한다.
			// * 수정자 : 임춘수
			// * 수정일 : 2009.09.11
			// */
			// szTC_CODE = YdCommonUtils.getCrnSchTCByYD(szYD_GP);
			// szMsg =
			// "["+szOperationName+"] 해당야드["+szYD_GP+"]의 크레인스케줄Main["+szTC_CODE[0]+"] 호출 시작";
			// ydUtils.putLog(szSessionName, szMethodName, szMsg,
			// YdConstant.DEBUG);
			// recInTemp = JDTORecordFactory.getInstance().create();
			// //전문코드
			// recInTemp.setField("JMS_TC_CD", szTC_CODE[0]);
			// //스케줄코드
			// recInTemp.setField("YD_SCH_CD", szCURR_YD_SCH_CD);
			// //설비ID
			// recInTemp.setField("YD_EQP_ID", szCurrCrn);
			// //작업예약ID
			// recInTemp.setField("YD_WBOOK_ID", szCURR_YD_WBOOK_ID);
			//
			// ydUtils.displayRecord(szOperationName, recInTemp);
			// //전문 송신
			// ydDelegate.sendMsg(recInTemp);
			//
			// szMsg =
			// "["+szOperationName+"] 해당야드["+szYD_GP+"]의 크레인스케줄Main["+szTC_CODE[0]+"] 호출 완료";
			// ydUtils.putLog(szSessionName, szMethodName, szMsg,
			// YdConstant.DEBUG);
			// }
			// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

			/*
			 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 업무기준 : 출하 공차 도착 시 저장위치 제원 야드L2로 전송 수정자 : 임춘수 수정일자 : 2009.08.24
			 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			 */
			if (!szYD_CAR_USE_GP.equals("L")) { // 출하차량(G)
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_INFO_SYNC_CD" , "3"); // 1:동,2:SPAN,3:열,4:BED
				recPara.setField("YD_GP" , szYD_CARLD_STOP_LOC.substring(0 , 1));
				recPara.setField("YD_STK_COL_GP" , szYD_CARLD_STOP_LOC);
				// recPara("YD_STK_BED_NO", szYD_CARLD_STOP_LOC);
				recPara.setField("YD_CAR_PROG_STAT" , "2");
				recPara.setField("YD_EQP_WRK_STAT" , "U");

				YdCommonUtils.sndStrPosSpecToL2(recPara);
				szMsg = "[" + szOperationName + "] 출하 공차도착 시 저장위치 제원 야드L2로 전송";
				ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.DEBUG);
				/*
				 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ +++
				 */
			}
			
			
			
			
			
			

			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("JMS_TC_CD" , "YDYDJ293");
			recInTemp.setField("CAR_NO" , szCAR_NO);
			recInTemp.setField("CARD_NO" , szCARD_NO);
			recInTemp.setField("YD_WBOOK_ID" , szCURR_YD_WBOOK_ID);
			recInTemp.setField("YD_CARLD_STOP_LOC" , szYD_CARLD_STOP_LOC);
			recInTemp.setField("SPOS_YD_PNT_CD" , szSPOS_YD_PNT_CD);
			recInTemp.setField("YD_SCH_CD" , szCURR_YD_SCH_CD);
			recInTemp.setField("YD_CRN_ID" , szCurrCrn);
			szMsg = "[" + szOperationName + "] 출하차량스케줄 수정 전문 전송 시작 - 전문내용확인";
			ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.DEBUG);
			ydUtils.displayRecord(szOperationName , recInTemp);

			ydDelegate.sendMsg(recInTemp);
			szMsg = "[" + szOperationName + "] 출하차량스케줄 수정 전문 전송 성공";
			ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.DEBUG);
			
			
			
			
			
			
			
			
			
			
			
			
			

		} catch (Exception e) {
			szMsg = "[" + szOperationName + "] 출하차량상차 작업요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;
		}
		return YdConstant.RETN_CD_SUCCESS;

	} // end of procCoilGdsDistCarLdWrkReq()
	
	

	/**
	 * 오퍼레이션명 : 코일임가공출하상차Lot편성
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procCoilOutplDistCarLdLotComp(JDTORecord msgRecord)
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
		String szMethodName = "procCoilOutplDistCarLdLotComp";
		// 사용자
		String szUser = "SYSTEM";

		// 전문 생성 일시
		String szDate = "";

		// 설비ID
		String szYD_EQP_ID = "";
		// 상차정지위치
		String szYD_CARLD_STOP_LOC = "";
		// 차량사용구분
		String szYD_CAR_USE_GP = "";
		// 차량번호
		String szCAR_NO = "";
		// 카드번호
		String szCARD_NO = "";
		// 재료품목
		String szYD_MTL_ITEM = "";
		// 스케줄코드
		String szYD_SCH_CD = "";
		// 차량이송재료매수
		int intYD_CARLD_SH = 0;
		// 차량작업허용매수
		int intYD_WRK_ALW_SH = 1;
		// //대상 재료 중량 합계
		// long lngSumMtlWt = 0;
		// //차량작업허용중량
		// long lngYD_WRK_ALW_WT = 0;

		String szTRANS_ORD_DATE = "";
		String szTRANS_ORD_SEQNO = "";
		String szYD_GP = "";
		String szYD_BAY_GP = "";
		String szSPOS_WLOC_CD = "";
		String szSPOS_YD_PNT_CD = "";
		String szYD_EQP_GP = "";
		String szYD_WRK_ALW_L = "";
		String szYD_WRK_ALW_W = "";
		String szYD_WRK_ALW_SKID_PITCH = "";
		String szYD_WRK_ALW_SH = "";
		String szYD_WRK_ALW_WT = "";
		String szSP_TRUCK_LOADING_LOC_TP = "";

		// PIDEV
//		String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "", "APPPI0", "J", "*");		
		
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
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;

			}
			// 상차정지위치
			szYD_CARLD_STOP_LOC = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_CARLD_STOP_LOC");
			if (szYD_CARLD_STOP_LOC.equals("")) {

				szMsg = "[전문 이상] 상차정지위치가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;

			}
			// 차량사용구분
			szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_CAR_USE_GP");
			if (szYD_CAR_USE_GP.equals("") || !szYD_CAR_USE_GP.equals("G")) {

				szMsg = "[전문 이상] 차량사용구분(" + szYD_CAR_USE_GP + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;

			}
			// 차량번호
			szCAR_NO = ydDaoUtils.paraRecChkNull(msgRecord, "CAR_NO");
			if (szCAR_NO.equals("")) {

				szMsg = "[전문 이상] 차량번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;

			}
			
			// PIDEV
//			if("N".equals(sApplyYnPI)) {						
//
//				// 카드번호
//				szCARD_NO = ydDaoUtils.paraRecChkNull(msgRecord, "CARD_NO");
//				
//				if (szCARD_NO.equals("")) {
//	
//					szMsg = "[전문 이상] 카드번호가 없습니다.";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg,
//							YdConstant.ERROR);
//					return;
//	
//				}
//
//			}
			
			// 운송지시일자
			szTRANS_ORD_DATE = ydDaoUtils.paraRecChkNull(msgRecord,
					"TRANS_ORD_DATE");
			if (szTRANS_ORD_DATE.equals("")) {
				szMsg = "[전문 이상] 운송지시일자 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;
			}

			// 운송지시순번
			szTRANS_ORD_SEQNO = ydDaoUtils.paraRecChkNull(msgRecord,
					"TRANS_ORD_SEQNO");
			if (szTRANS_ORD_SEQNO.equals("")) {
				szMsg = "[전문 이상] 운송지시순번 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;
			}

			// 야드구분
			szYD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP");
			if (szYD_GP.equals("")) {
				szMsg = "[전문 이상] 야드구분 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;
			}

			// 동구분
			szYD_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP");
			if (szYD_BAY_GP.equals("")) {
				szMsg = "[전문 이상] 동구분 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;
			}

			// 발지개소코드
			szSPOS_WLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord,
					"SPOS_WLOC_CD");
			if (szSPOS_WLOC_CD.equals("")) {
				szMsg = "[전문 이상] 발지개소코드 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;
			}

			// 발지야드포인트코드
			szSPOS_YD_PNT_CD = ydDaoUtils.paraRecChkNull(msgRecord,
					"SPOS_YD_PNT_CD");
			if (szSPOS_YD_PNT_CD.equals("")) {
				szMsg = "[전문 이상] 발지야드포인트코드 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;
			}

			// 야드설비구분
			szYD_EQP_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_GP");
			if (szYD_EQP_GP.equals("")) {
				szMsg = "[전문 이상] 야드설비구분 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;
			}

			// 야드작업허용길이
			szYD_WRK_ALW_L = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_WRK_ALW_L");
			if (szYD_WRK_ALW_L.equals("")) {
				szMsg = "[전문 이상] 야드작업허용길이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;
			}

			// 야드작업허용폭
			szYD_WRK_ALW_W = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_WRK_ALW_W");
			if (szYD_WRK_ALW_W.equals("")) {
				szMsg = "[전문 이상] 야드작업허용폭 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;
			}

			// 야드작업허용Skid간격
			szYD_WRK_ALW_SKID_PITCH = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_WRK_ALW_SKID_PITCH");
			if (szYD_WRK_ALW_SKID_PITCH.equals("")) {
				szMsg = "[전문 이상] 야드작업허용Skid간격 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;
			}

			// 작업가능허용매수
			intYD_WRK_ALW_SH = ydDaoUtils.paraRecChkNullInt(msgRecord,
					"YD_WRK_ALW_SH");
			if (intYD_WRK_ALW_SH <= 0) {
				szMsg = "[전문 이상] 작업가능허용매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;
			}

			// 야드작업허용중량
			szYD_WRK_ALW_WT = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_WRK_ALW_WT");
			if (szYD_WRK_ALW_WT.equals("")) {
				szMsg = "[전문 이상] 야드작업허용중량 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;
			}

			if (szYD_CAR_USE_GP.equals("L")) {
				// 운송작업영공구분코드
				szSP_TRUCK_LOADING_LOC_TP = ydDaoUtils.paraRecChkNull(
						msgRecord, "SP_TRUCK_LOADING_LOC_TP");
				if (szSP_TRUCK_LOADING_LOC_TP.equals("")) {
					szMsg = "[전문 이상] 운송작업영공구분코드가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg,
							YdConstant.ERROR);
					return;
				}
			} else {

			}

			// =================================================================================
			// 수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			// BRE 등록 안됨...테스트용 스케줄코드 생성
			// 추후 구현..
			szYD_SCH_CD = szYD_CARLD_STOP_LOC.substring(0, 2).trim() + "PT01UM";
			// =================================================================================

			// //리턴 recordSet 생성
			// rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			// //차량사양 Select
			// blnRtnVal = chkGetCarSpec(szYD_EQP_ID, szYD_CAR_USE_GP,
			// szTRN_EQP_CD, rsResult);
			// if(!blnRtnVal) return ;
			//
			// //레코드 추출
			// rsResult.first();
			// recPara = rsResult.getRecord();

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// //상차 Lot 편성 대상 재료 Select
			// blnRtnVal = chkGetOutPlDistCarLoadLotGp(msgRecord, rsResult);
			// if(!blnRtnVal) return ;

			// 상차 Lot 편성 대상 재료 Select
			blnRtnVal = chkDistCarLoadLotGp(msgRecord, rsResult);
			if (!blnRtnVal)
				return;

			// 레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			// JMS TC CODE
			recOutPara.setField("JMS_TC_CD", "YDYDJ244");
			// 레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();

			// 차량 이송 가능 매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= intYD_WRK_ALW_SH; Loop_i++) {

				// //대상 재료 중량 합계
				// lngSumMtlWt = lngSumMtlWt +
				// Long.parseLong(ydDaoUtils.paraRecChkNull(recPara,
				// "YD_MTL_WT"));
				// //차량작업허용중량보다 대상재료들의 중량이 많으면 편성 중지
				// if(lngYD_WRK_ALW_WT < lngSumMtlWt) break;
				// 재료번호
				recOutPara.setField("STL_NO" + Loop_i,
						ydDaoUtils.paraRecChkNull(recPara, "STL_NO"));
				// 권상모음순서
				recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, "" + Loop_i);
				// 차량이송재료매수
				intYD_CARLD_SH = Loop_i;

				// 다음 레코드 추출
				rsResult.next();
				recPara = rsResult.getRecord();

			}

			// 전문 발생 일시
			szDate = ydUtils.getCurDate("yyyy/MM/dd HH:mm:ss");

			// 발생 일시
			recOutPara.setField("JMS_TC_CREATE_DDTT", szDate);
			// 스케줄코드
			recOutPara.setField("YD_SCH_CD", szYD_SCH_CD);
			// CARRY_IN 재료매수
			recOutPara.setField("YD_CARLD_SH", "" + intYD_CARLD_SH);
			// 적치열구분
			recOutPara.setField("YD_STK_COL_GP", szYD_SCH_CD.substring(0, 2)
					+ szYD_EQP_ID.substring(2));
			// 설비id
			recOutPara.setField("YD_EQP_ID", szYD_EQP_ID);
			// 적치BED번호
			recOutPara.setField("YD_STK_BED_NO", "01");
			// 목표행선구분
			recOutPara.setField("YD_AIM_RT_GP", "C9");
			// 차량사용구분
			recOutPara.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
			// 상차 정지위치
			recOutPara.setField("YD_CARLD_STOP_LOC", szYD_CARLD_STOP_LOC);
			// 차량번호
			recOutPara.setField("CAR_NO", szCAR_NO);
			// 카드번호
			recOutPara.setField("CARD_NO", szCARD_NO);
			// 재료품목
			recOutPara.setField("YD_MTL_ITEM", szYD_MTL_ITEM);
			// 운송지시일자
			recOutPara.setField("TRANS_ORD_DATE", szTRANS_ORD_DATE);
			// 운송지시순번
			recOutPara.setField("TRANS_ORD_SEQNO", szTRANS_ORD_SEQNO);
			// 야드구분
			recOutPara.setField("YD_GP", szYD_GP);
			// 동구분
			recOutPara.setField("YD_BAY_GP", szYD_BAY_GP);
			// 발지개소코드
			recOutPara.setField("SPOS_WLOC_CD", szSPOS_WLOC_CD);
			// 발지포인트코드
			recOutPara.setField("SPOS_YD_PNT_CD", szSPOS_YD_PNT_CD);
			// 설비구분
			recOutPara.setField("YD_EQP_GP", szYD_EQP_GP);
			// 야드작업허용길이
			recOutPara.setField("YD_WRK_ALW_L", szYD_WRK_ALW_L);
			// 야드작업허용폭
			recOutPara.setField("YD_WRK_ALW_W", szYD_WRK_ALW_W);
			// 야드작업허용Skid간격
			recOutPara.setField("YD_WRK_ALW_SKID_PITCH",
					szYD_WRK_ALW_SKID_PITCH);
			// 야드작업허용매수
			recOutPara.setField("YD_WRK_ALW_SH", szYD_WRK_ALW_SH);
			// 야드작업허용중량
			recOutPara.setField("YD_WRK_ALW_WT", szYD_WRK_ALW_WT);
			// 운송작업영공구분코드
			recOutPara.setField("SP_TRUCK_LOADING_LOC_TP",
					szSP_TRUCK_LOADING_LOC_TP);

			this.procCCsOutCarLdWrkReq(recOutPara);
			szMsg = "코일임가공출하상차Lot편성 후 차량상차 작업요구 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		} catch (Exception e) {
			szMsg = "코일임가공출하상차Lot편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}

	} // end of procCoilOutplDistCarLdLotComp

	/**
	 * 오퍼레이션명 : C열연차량상차작업요구(소재,제품)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String procCHrCarLdWrkReq(JDTORecord msgRecord) throws JDTOException {

		YdDelegate ydDelegate = new YdDelegate();
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
		String szMethodName = "procCHrCarLdWrkReq";
		// 사용자
		String szUser = "SYSTEM";
		String szOperationName = "C열연차량상차작업요구";

		// 레코드 선언
		JDTORecord recPara = null;
		JDTORecord recStkPara = null;
		// 레코드셋 선언
		JDTORecordSet rsResult = null;

		// 설비ID
		String szYD_EQP_ID = null;
		// 목표행선구분
		String szYD_AIM_RT_GP = null;
		// 차량사용구분
		String szYD_CAR_USE_GP = null;
		// 운송장비코드
		String szTRN_EQP_CD = null;
		// 재료매수(int)
		int intMtlCnt = 0;
		// 재료번호
		String[] szSTL_NO = null;
		// 권상모음순서
		String[] szYD_UP_COLL_SEQ = null;
		// 스케줄코드
		String szYD_SCH_CD = null;
		// 야드스케줄우선순위
		String szYD_SCH_PRIOR = null;
		// 스케줄 금지 유무
		String szYD_SCH_PROH_EXN = null;
		// 작업크레인
		String szYD_WRK_CRN = null;
		// 야드작업크레인우선순위
		String szYD_WRK_CRN_PRIOR = null;
		// 대체크레인유무
		String szYD_ALT_CRN_YN = null;
		// 대체크레인
		String szYD_ALT_CRN = null;
		// 야드대체크레인우선순위
		String szYD_ALT_CRN_PRIOR = null;
		// 선택크레인
		String szCrn = null;
		// 작업예약ID
		String szYD_WBOOK_ID = null;
		// 개소코드
		String szWLOC_CD = null;
		// 운송작업영공구분코드
		String szSP_TRUCK_LOADING_LOC_TP = null;
		// 포인트요구일시
		String szPNT_DMD_DT = null;
		// 발지개소코드
		String szSPOS_WLOC_CD = null;
		// 발지포인트코드
		String szSPOS_YD_PNT_CD = null;
		// 상차정지위치
		String szYD_CARLD_STOP_LOC = null;
		// 차량번호
		String szCAR_NO = null;
		// 카드번호
		String szCARD_NO = null;

		// 야드목표야드구분
		String szYD_AIM_YD_GP = null;
		// 야드목표동구분
		String szYD_AIM_BAY_GP = null;

		// PIDEV
//		String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "", "APPPI0", "J", "*");		
		
		// TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);

		// 에러 리턴
		if (szRcvTcCode == null) {

			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error ("
					+ szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_TC_ERROR;

		}
		// TC CODE DISPLAY
		if (bDebugFlag) {

			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		}

		try {
			szMsg = "C열연 차량상차 작업요구 START!!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			ydUtils.displayRecord(szOperationName, msgRecord);

			// 받은 전문 편집
			// 스케줄코드
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			if (szYD_SCH_CD.equals("")) {

				szMsg = "[전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}
			// 설비ID
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if (szYD_EQP_ID.equals("")) {

				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}
			// 목표행선구분
			szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(msgRecord , "YD_AIM_RT_GP");
			if (szYD_AIM_RT_GP.equals("")) {

				szMsg = "[전문 이상] 목표행선구분이 없습니다.";
				ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}
			// 차량사용구분
			szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(msgRecord , "YD_CAR_USE_GP");
			if (szYD_CAR_USE_GP.equals("")) {

				szMsg = "[전문 이상] 차량사용구분이 없습니다.";
				ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}

			if (szYD_CAR_USE_GP.equals("L")) {

				// 구내운송인 경우

				// 운송장비코드
				szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(msgRecord , "TRN_EQP_CD");
				if (szTRN_EQP_CD.equals("")) {

					szMsg = "[전문 이상] 운송장비코드가 없습니다.";
					ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;
				}

				// 개소코드
				szWLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord , "WLOC_CD");
				if (szWLOC_CD.equals("")) {

					szMsg = "[전문 이상] 개소코드가 없습니다.";
					ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;

				}

				// 운송작업영공구분코드
				szSP_TRUCK_LOADING_LOC_TP = ydDaoUtils.paraRecChkNull(msgRecord , "SP_TRUCK_LOADING_LOC_TP");
				if (szSP_TRUCK_LOADING_LOC_TP.equals("")) {

					szMsg = "[전문 이상] 운송작업영공구분코드가 없습니다.";
					ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;

				}

				// 포인트요구일시
				szPNT_DMD_DT = ydDaoUtils.paraRecChkNull(msgRecord , "PNT_DMD_DT");
				if (szPNT_DMD_DT.equals("")) {

					szMsg = "[전문 이상] 포인트요구일시가 없습니다.";
					ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;

				}

			} else {

				// 출하 차량인 경우
				// 상차정지위치
				szYD_CARLD_STOP_LOC = ydDaoUtils.paraRecChkNull(msgRecord , "YD_CARLD_STOP_LOC");
				if (szYD_CARLD_STOP_LOC.equals("")) {

					szMsg = "[전문 이상] 상차정지위치가  없습니다.";
					ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
					// return ;

				}
				// 차량번호
				szCAR_NO = ydDaoUtils.paraRecChkNull(msgRecord , "CAR_NO");
				if (szCAR_NO.equals("")) {

					szMsg = "[전문 이상] 차량번호가 없습니다.";
					ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
					// return ;

				}
				
//				if("N".equals(sApplyYnPI)) {							
//
//					// 카드번호
//					szCARD_NO = ydDaoUtils.paraRecChkNull(msgRecord , "szCARD_NO");
//					
//					if (szCARD_NO.equals("")) {
//	
//						szMsg = "[전문 이상] 카드번호가 없습니다.";
//						ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
//						// return ;
//	
//					}
//				
//				}
				
			}

			// 발지개소코드
			szSPOS_WLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord , "SPOS_WLOC_CD");
			if (szSPOS_WLOC_CD.equals("")) {

				szMsg = "[전문 이상] 발지개소코드가 없습니다.";
				ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
				// return YdConstant.RETN_CD_NO_PARAM;

			}

			// 발지포인트코드
			szSPOS_YD_PNT_CD = ydDaoUtils.paraRecChkNull(msgRecord , "SPOS_YD_PNT_CD");
			if (szSPOS_YD_PNT_CD.equals("")) {

				szMsg = "[전문 이상] 발지포인트코드가 없습니다.";
				ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
				// return YdConstant.RETN_CD_NO_PARAM;

			}

			// 재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord , "YD_CARLD_SH");
			if (intMtlCnt == 0) {

				szMsg = "[전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}

			// 재료번호
			szSTL_NO = new String[intMtlCnt + 1];
			// 권상모음순서
			szYD_UP_COLL_SEQ = new String[intMtlCnt + 1];

			// 재료번호, 권상모음순서
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord , "STL_NO" + Loop_i);
				if (szSTL_NO[Loop_i].equals("")) {

					szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;

				}
				// 권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord , "YD_UP_COLL_SEQ" + Loop_i);
				if (szYD_UP_COLL_SEQ[Loop_i].equals("")) {

					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i] + ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;

				}
			}
			//*********************************************************************************************
			
			
			
			
			//***********************************************************************
			//차량스케줄생성 메소드 호출***************************************************
			//***********************************************************************
			// record 생성
			recPara = JDTORecordFactory.getInstance().create();
			// 설비ID
			recPara.setField("YD_EQP_ID" , szYD_EQP_ID);
			// 스케줄코드
			recPara.setField("YD_SCH_CD" , szYD_SCH_CD);
			// 차량사용구분
			recPara.setField("YD_CAR_USE_GP" , szYD_CAR_USE_GP);
			// 운송장비코드
			recPara.setField("TRN_EQP_CD" , szTRN_EQP_CD);
			// 개소코드
			recPara.setField("WLOC_CD" , szWLOC_CD);
			// 운송작업영공구분코드
			recPara.setField("SP_TRUCK_LOADING_LOC_TP" , szSP_TRUCK_LOADING_LOC_TP);
			// 포인트요구일시
			recPara.setField("PNT_DMD_DT" , szPNT_DMD_DT);
			// 발지개소코드
			recPara.setField("SPOS_WLOC_CD" , szSPOS_WLOC_CD);
			// 발지포인트코드
			recPara.setField("SPOS_YD_PNT_CD" , szSPOS_YD_PNT_CD);

			if (szYD_CAR_USE_GP.equals("G")) {
				// 상차정지위치
				recPara.setField("YD_CARLD_STOP_LOC" , szYD_CARLD_STOP_LOC);
				recPara.setField("YD_SCH_CD" , szYD_SCH_CD);
				recPara.setField("YD_CRN_EQP_ID" , szCrn);
			}

			szMsg = "차량스케줄생성 메소드 호출";
			ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.DEBUG);
			ydUtils.displayRecord(szOperationName , recPara);

			// 차량스케줄생성 메소드 호출
			intRtnVal = this.mkY5CarSch(recPara);
			if (intRtnVal != 1) {
				return YdConstant.RETN_CD_SUCCESS;
			}
			//***********************************************************************
			
			


			
			
			//***********************************************************************
			//스케줄 기준 체크 호출*******************************************************
			//***********************************************************************
			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD , rsResult);
			if (!blnRtnVal) {
				return YdConstant.RETN_CD_FAILURE;
			}
			//***********************************************************************
			
			
			
			
			//***********************************************************************
			//작업예약 생성 호출*********************************************************
			//***********************************************************************
			// 레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();

			// 스케줄CD 체크
			// 스케줄 금지 유무
			szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara , "YD_SCH_PROH_EXN");
			// 작업크레인
			szYD_WRK_CRN = ydDaoUtils.paraRecChkNull(recPara , "YD_WRK_CRN");
			// 작업크레인우선순위
			szYD_WRK_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara , "YD_WRK_CRN_PRIOR");
			// 대체크레인유무
			szYD_ALT_CRN_YN = ydDaoUtils.paraRecChkNull(recPara , "YD_ALT_CRN_YN");
			// 대체크레인
			szYD_ALT_CRN = ydDaoUtils.paraRecChkNull(recPara , "YD_ALT_CRN");
			// 대체크레인우선순위
			szYD_ALT_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara , "YD_ALT_CRN_PRIOR");

			// 스케줄 금지 유무가 "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if (szYD_SCH_PROH_EXN.equals("Y")) {

				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}

			// 작업크레인 설비 상태 체크
			blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);

			// 작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if (!blnRtnVal) {

				szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
				ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);

				// 대체크레인의 유무를 체크한다.
				// 대체크레인이 없으면 에러 리턴
				if (!szYD_ALT_CRN_YN.equals("Y")) {

					szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
					ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;

				}
				// 대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				// 대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {

					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;

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
				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i] , szCrn);
				if (!blnRtnVal) {
					return YdConstant.RETN_CD_FAILURE;
				}

				// 다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if (!blnRtnVal) {
					return YdConstant.RETN_CD_FAILURE;
				}

			}

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if (!blnRtnVal) {
				return YdConstant.RETN_CD_FAILURE;
			}
			// 레코드추출
			rsResult.first();
			recPara = rsResult.getRecord();
			// 작업예약ID
			szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			// 저장품테이블 조회
			blnRtnVal = this.chkGetStock(szSTL_NO[1] , rsResult);
			if (!blnRtnVal) {
				return YdConstant.RETN_CD_FAILURE;
			}
			// 레코드추출
			rsResult.first();
			recPara = rsResult.getRecord();

			// 야드목표야드구분
			szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(recPara , "YD_AIM_YD_GP");
			// 야드목표동구분
			szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(recPara , "YD_AIM_BAY_GP");

			// INSERT 항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			// 야드구분
			String szYD_GP = szYD_SCH_CD.substring(0 , 1);
			// 동구분
			String szYD_BAY_GP = szYD_SCH_CD.substring(1 , 2);

			// INSERT할 항목 SET
			recPara.setField("YD_WBOOK_ID" , szYD_WBOOK_ID);
			recPara.setField("YD_GP" , szYD_GP);
			recPara.setField("YD_BAY_GP" , szYD_BAY_GP);
			recPara.setField("YD_SCH_CD" , szYD_SCH_CD);
			recPara.setField("REGISTER" , szUser);
			recPara.setField("YD_CAR_USE_GP" , szYD_CAR_USE_GP);
			recPara.setField("TRN_EQP_CD" , szTRN_EQP_CD);
			recPara.setField("YD_AIM_YD_GP" , szYD_AIM_YD_GP);
			recPara.setField("YD_AIM_BAY_GP" , szYD_AIM_BAY_GP);
			recPara.setField("YD_SCH_PRIOR" , szYD_SCH_PRIOR);
			recPara.setField("CAR_NO" , szCAR_NO);
			recPara.setField("CARD_NO" , szCARD_NO);

			// 작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}

			// 조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();

			recPara.setField("YD_WBOOK_ID" , szYD_WBOOK_ID);
			// recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			// recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER" , szUser);

			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 리턴 recordSet 생성
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				// 재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				intRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i] , "C" , rsResult);
				if (intRtnVal != 1) {
					return YdConstant.RETN_CD_FAILURE;
				}

				// 레코드추출
				rsResult.first();
				recStkPara = rsResult.getRecord();

				// 재료번호
				recPara.setField("STL_NO" , szSTL_NO[Loop_i]);
				// 적치열구분
				recPara.setField("YD_STK_COL_GP" , ydDaoUtils.paraRecChkNull(recStkPara , "YD_STK_COL_GP"));
				// 적치BED번호
				recPara.setField("YD_STK_BED_NO" , ydDaoUtils.paraRecChkNull(recStkPara , "YD_STK_BED_NO"));
				// 적치단번호
				recPara.setField("YD_STK_LYR_NO" , ydDaoUtils.paraRecChkNull(recStkPara , "YD_STK_LYR_NO"));
				// 권상모음순서
				recPara.setField("YD_UP_COLL_SEQ" , szYD_UP_COLL_SEQ[Loop_i]);

				// 작업예약재료 테이블에 등록한다.
				intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);

				if (intRtnVal < 1) {
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;
				}
			}
			//***********************************************************************
			
			
			
			

			/*
			 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 업무기준 : 출하 공차 도착 시 저장위치 제원 야드L2로 전송 수정자 : 임춘수 수정일자 : 2009.08.24
			 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			 */
			if (!szYD_CAR_USE_GP.equals("L")) { // 출하차량(G)
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_INFO_SYNC_CD" , "3"); // 1:동,2:SPAN,3:열,4:BED
				recPara.setField("YD_GP" , szYD_EQP_ID.substring(0 , 1));
				recPara.setField("YD_STK_COL_GP" , szYD_EQP_ID);
				// recPara("YD_STK_BED_NO", szYD_CARLD_STOP_LOC);
				recPara.setField("YD_CAR_PROG_STAT" , "2");
				recPara.setField("YD_EQP_WRK_STAT" , "U");

				YdCommonUtils.sndStrPosSpecToL2(recPara);
				szMsg = "<procCHrCarLdWrkReq>  출하 공차도착 시 저장위치 제원 야드L2로 전송";
				ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.DEBUG);
				/*
				 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ +++
				 */
			}

		} catch (Exception e) {
			szMsg = "C열연 차량상차 작업요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;
		}
		return YdConstant.RETN_CD_SUCCESS;

	} // end of procCHrCarLdWrkReq()

	/**
	 * 오퍼레이션명 : C열연소재임가공LOT편성
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String procCHrMatlRentProcLotComp(JDTORecord msgRecord)
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
		String szMethodName = "procCHrMatlRentProcLotComp";
		// 사용자
		String szUser = "SYSTEM";

		// 전문 생성 일시
		String szDate = null;

		// 설비ID
		String szYD_EQP_ID = null;
		// 상차정지위치
		String szYD_CARLD_STOP_LOC = null;
		// 재료품목
		String szYD_MTL_ITEM = null;
		// 목표행선구분
		String szYD_AIM_RT_GP = null;
		// 임가공사코드
		String szRENTPROC_CD = null;
		// //목표동구분
		// String szYD_AIM_BAY_GP = null;
		// 스케줄코드
		String szYD_SCH_CD = null;
		// 발지개소크드
		String szSPOS_WLOC_CD = null;
		// 발지포인트코드
		String szSPOS_YD_PNT_CD = null;
		// 차량번호
		String szCAR_NO = null;
		// 카드번호
		String szCARD_NO = null;

		// 대차이송재료매수
		int intYD_CARLD_SH = 0;
		// 대상 재료 중량 합계
		long lngSumMtlWt = 0;
		// 야드작업허용중량
		long lngYD_WRK_ALW_WT = 0;

		// 연계모듈을 EJB Call or JMS Call 호출 유무 변수
		String szIS_EJB_CALL = null;
		// 리턴값
		String szRtnMsg = null;

		// PIDEV
//		String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "", "APPPI0", "J", "*");			
		
		// TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		// 에러 리턴
		if (szRcvTcCode == null) {

			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error ("
					+ szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_TC_ERROR;

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
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				// return YdConstant.RETN_CD_NO_PARAM;

			}
			// 상차정지위치
			szYD_CARLD_STOP_LOC = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_CARLD_STOP_LOC");
			if (szYD_CARLD_STOP_LOC.equals("")) {

				szMsg = "[전문 이상] 상차정지위치가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				// return YdConstant.RETN_CD_NO_PARAM;

			}
			// 재료품목
			szYD_MTL_ITEM = ydDaoUtils.paraRecChkNull(msgRecord, "YD_MTL_ITEM");
			if (szYD_MTL_ITEM.equals("")) {

				szMsg = "[전문 이상] 재료품목이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				// return YdConstant.RETN_CD_NO_PARAM;

			}
			// 목표행선구분
			szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_AIM_RT_GP");
			if (szYD_AIM_RT_GP.equals("")) {

				szMsg = "[전문 이상] 목표행선구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				// return YdConstant.RETN_CD_NO_PARAM;

			}
			// 야드작업허용중량
			lngYD_WRK_ALW_WT = ydDaoUtils.paraRecChkNullLong(msgRecord,
					"YD_WRK_ALW_WT");
			if (lngYD_WRK_ALW_WT == 0) {

				szMsg = "[전문 이상] 야드작업허용중량이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				// return YdConstant.RETN_CD_NO_PARAM;

			}
			// 임가공사코드
			szRENTPROC_CD = ydDaoUtils.paraRecChkNull(msgRecord, "RENTPROC_CD");
			if (szRENTPROC_CD.equals("")) {

				szMsg = "[전문 이상] 임가공사코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				// return YdConstant.RETN_CD_NO_PARAM;

			}
			// 발지개소코드
			szSPOS_WLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord,
					"SPOS_WLOC_CD");
			if (szSPOS_WLOC_CD.equals("")) {

				szMsg = "[전문 이상] 발지개소코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				// return YdConstant.RETN_CD_NO_PARAM;

			}

			// 발지포인트코드
			szSPOS_YD_PNT_CD = ydDaoUtils.paraRecChkNull(msgRecord,
					"SPOS_YD_PNT_CD");
			if (szSPOS_YD_PNT_CD.equals("")) {

				szMsg = "[전문 이상] 발지포인트코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				// return YdConstant.RETN_CD_NO_PARAM;

			}

			// 차량번호
			szCAR_NO = ydDaoUtils.paraRecChkNull(msgRecord, "CAR_NO");
			if (szCAR_NO.equals("")) {

				szMsg = "[전문 이상] 차량번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				// return YdConstant.RETN_CD_NO_PARAM;

			}
		
			// PIDEV
//			if("N".equals(sApplyYnPI)) {						
//
//				// 카드번호
//				szCARD_NO = ydDaoUtils.paraRecChkNull(msgRecord, "szCARD_NO");
//				
//				if (szCARD_NO.equals("")) {
//	
//					szMsg = "[전문 이상] 카드번호가 없습니다.";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg,
//							YdConstant.ERROR);
//					// return YdConstant.RETN_CD_NO_PARAM;
//	
//				}
//
//			}
			
			// 연계모듈을 EJB Call[Y값으로 설정] or JMS Call[값이 없거나 N값으로 설정] 호출 유무 변수 -
			// 지금은 화면으로부터 전송
			szIS_EJB_CALL = ydDaoUtils.paraRecChkNull(msgRecord, "IS_EJB_CALL");

			szMsg = "[C열연 제품고간 이송 LOT편성]szIS_EJB_CALL = " + szIS_EJB_CALL;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			// //목표동구분
			// szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord,
			// "YD_AIM_BAY_GP");
			// if(szYD_AIM_BAY_GP.equals("")){
			//
			// szMsg = "[전문 이상] 목표동구분이 없습니다.";
			// ydUtils.putLog(szSessionName, szMethodName, szMsg,
			// YdConstant.ERROR);
			// return ;
			//
			// }

			// =================================================================================
			// 수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			// BRE 등록 안됨...테스트용 스케줄코드 생성
			// 추후 구현..
			szYD_SCH_CD = "HEPT01UM";
			// =================================================================================

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 상차 Lot 편성 대상 재료 Select
			blnRtnVal = chkGetCHRMatlRentProcLotGp(msgRecord, rsResult);
			if (!blnRtnVal)
				return YdConstant.RETN_CD_NOTEXIST;

			// 레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			// JMS TC CODE
			recOutPara.setField("JMS_TC_CD", "YDYDJ248");
			// 첫 레코드로 커서이동
			rsResult.first();

			// 대상 소재 매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {

				// 레코드 추출
				recPara = rsResult.getRecord();
				// 대상 재료 중량 합계
				lngSumMtlWt = lngSumMtlWt
						+ ydDaoUtils.paraRecChkNullLong(recPara, "YD_MTL_WT");
				// 대차작업허용중량보다 대상재료들의 중량이 많으면 편성 중지
				if (lngYD_WRK_ALW_WT < lngSumMtlWt)
					break;
				// 재료번호
				recOutPara.setField("STL_NO" + Loop_i,
						ydDaoUtils.paraRecChkNull(recPara, "STL_NO"));
				// 권상모음순서
				recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, "" + Loop_i);
				// 대차이송재료매수
				intYD_CARLD_SH = Loop_i;

				// 다음 레코드로 커서 이동
				rsResult.next();

			}

			// 전문 발생 일시
			szDate = ydUtils.getCurDate("yyyy/MM/dd HH:mm:ss");

			// 발생 일시
			recOutPara.setField("JMS_TC_CREATE_DDTT", szDate);
			// 스케줄코드
			recOutPara.setField("YD_SCH_CD", szYD_SCH_CD);
			// CARRY_IN 재료매수
			recOutPara.setField("YD_CARLD_SH", "" + intYD_CARLD_SH);
			// 적치열구분
			recOutPara.setField("YD_STK_COL_GP", szYD_CARLD_STOP_LOC);
			// 적치BED번호
			recOutPara.setField("YD_STK_BED_NO", "01");
			// 차량설비ID
			recOutPara.setField("YD_EQP_ID",
					"XX" + szYD_CARLD_STOP_LOC.substring(2, 6));
			// 차량사용구분
			recOutPara.setField("YD_CAR_USE_GP", "G");
			// 목표행선구분
			recOutPara.setField("YD_AIM_RT_GP", szYD_AIM_RT_GP);
			// 발지코드
			recOutPara.setField("SPOS_WLOC_CD", szSPOS_WLOC_CD);
			// 발지포인트코드
			recOutPara.setField("SPOS_YD_PNT_CD", szSPOS_YD_PNT_CD);
			// 상차정지위치
			recOutPara.setField("YD_CARLD_STOP_LOC", szYD_CARLD_STOP_LOC);
			// 차량번호
			recOutPara.setField("CAR_NO", szCAR_NO);
			// 카드번호
			recOutPara.setField("CARD_NO", szCARD_NO);

			// 전문 송신
			// ydDelegate.sendMsg(recOutPara);

			/*
			 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			 * EJB Call or JMS Call 유무를 판단하여 호출하는 기능 수정자 : 임춘수 일자 : 2009.07.13
			 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			 */
			if (szIS_EJB_CALL.equals("Y")) {
				// EJB Call ==> 메소드 콜
				szMsg = "C열연 소재 임가공 LOT 편성 후 차량상차 작업요구 송신 - 메소드[procCHrCarLdWrkReq] 콜 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.DEBUG);

				szRtnMsg = procCHrCarLdWrkReq(recOutPara);

				szMsg = "C열연 소재 임가공 LOT 편성 후 차량상차 작업요구 송신 - 메소드[procCHrCarLdWrkReq] 콜 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.DEBUG);
			} else {
				// 전문 송신
				ydDelegate.sendMsg(recOutPara);
				szMsg = "C열연 소재 임가공 LOT 편성 후 차량상차 작업요구 송신[procCHrCarLdWrkReq] - 전문 송신 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.DEBUG);
			}

			szMsg = "C열연 소재 임가공 LOT 편성 후 차량상차 작업요구 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		} catch (Exception e) {
			szMsg = "C열연 소재 임가공 LOT 편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;
		}
		return szRtnMsg;
	} // end of procCHrMatlRentProcLotComp

	/**
	 * 오퍼레이션명 : C열연대차상차작업요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procCHrTcarLdWrkReq(JDTORecord msgRecord) throws JDTOException {

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
		String szMethodName = "procCHrTcarLdWrkReq";
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
		// 목표야드구분
		String szYD_AIM_YD_GP = null;
		// 목표동구분
		String szYD_AIM_BAY_GP = null;
		// 스케줄우선순위
		String szYD_SCH_PRIOR = null;

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
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;

			}
			// 적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_COL_GP");
			if (szYD_STK_COL_GP.equals("")) {

				szMsg = "[전문 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;

			}
			// 적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_BED_NO");
			if (szYD_STK_BED_NO.equals("")) {

				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;

			}
			// 재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_CARLD_SH");
			if (intMtlCnt == 0) {

				szMsg = "[전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;

			}
			// 목표야드
			szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_AIM_BAY_GP");
			if (szYD_AIM_YD_GP.equals("")) {

				szMsg = "[전문 이상] 목표야드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;

			}
			// 목표동
			szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_AIM_BAY_GP");
			if (szYD_AIM_BAY_GP.equals("")) {

				szMsg = "[전문 이상] 목표동이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
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
					ydUtils.putLog(szSessionName, szMethodName, szMsg,
							YdConstant.ERROR);
					return;

				}
				// 권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,
						"YD_UP_COLL_SEQ" + Loop_i);
				if (szYD_UP_COLL_SEQ[Loop_i].equals("")) {

					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i]
							+ ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg,
							YdConstant.ERROR);
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
			// 스케줄우선순위
			szYD_SCH_PRIOR = ydDaoUtils.paraRecChkNull(recPara,
					"YD_WRK_CRN_PRIOR");

			// 스케줄 금지 유무가 "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if (szYD_SCH_PROH_EXN.equals("Y")) {

				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;
			}

			// 작업크레인 설비 상태 체크
			blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);

			// 작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if (!blnRtnVal) {

				szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);

				// 대체크레인의 유무를 체크한다.
				// 대체크레인이 없으면 에러 리턴
				if (!szYD_ALT_CRN_YN.equals("Y")) {

					szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg,
							YdConstant.ERROR);
					return;

				}
				// 대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				// 대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {

					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg,
							YdConstant.ERROR);
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

			// 2009.04.07 김진욱 수정/////////////////////////////////////////
			// 스케줄 우선순위 및 목표동, 목표야드 구분
			recPara.setField("YD_SCH_PRIOR", szYD_SCH_PRIOR);
			recPara.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP);
			recPara.setField("YD_WRK_PLAN_TCAR",
					"JX" + szYD_STK_COL_GP.substring(2, 6));
			recPara.setField("YD_AIM_YD_GP", szYD_AIM_YD_GP);
			// ///////////////////////////////////////////////////////////

			// 작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
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
				intRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C",
						rsResult);
				if (intRtnVal != 1)
					return;

				// 레코드추출
				rsResult.first();
				recStkPara = rsResult.getRecord();

				// 재료번호
				recPara.setField("STL_NO", szSTL_NO[Loop_i]);
				// 적치열구분
				recPara.setField("YD_STK_COL_GP",
						ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_COL_GP"));
				// 적치BED번호
				recPara.setField("YD_STK_BED_NO",
						ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_BED_NO"));
				// 적치단번호
				recPara.setField("YD_STK_LYR_NO",
						ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_LYR_NO"));
				// 권상모음순서
				recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ[Loop_i]);

				// 작업예약재료 테이블에 등록한다.
				intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);

				if (intRtnVal < 1) {
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg,
							YdConstant.ERROR);
					return;
				}
			}

		} catch (Exception e) {
			szMsg = "C열연 대차상차 작업요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}

	} // end of procCHrTcarLdWrkReq()

	/**
	 * 오퍼레이션명 : C열연소재이송Lot편성
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return String
	 * @throws JDTOException
	 */
	public String procCHrMatlFtmvLotComp(JDTORecord msgRecord)
			throws JDTOException {
		YdDelegate ydDelegate = new YdDelegate();
		// 공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		// 공용 METHOD
		YdUtils ydutils = new YdUtils();

		// 레코드 선언
		JDTORecord recPara = null;
		JDTORecord recOutPara = null;
		JDTORecord recSpecPara = null;
		// 레코드셋 선언
		JDTORecordSet rsResult = null;
		JDTORecordSet rsSpecResult = null;

		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메세지
		String szMsg = "";
		// 메소드명
		String szMethodName = "procCHrMatlFtmvLotComp";
		String szOperationName = "C열연소재이송Lot편성";

		// 설비ID
		String szYD_EQP_ID = null;
		// 적치열구분
		String szYD_STK_COL_GP = null;
		// //상차정지위치
		// String szYD_CARLD_STOP_LOC = null;
		// 차량사용구분
		String szYD_CAR_USE_GP = null;
		// 운송장비코드
		String szTRN_EQP_CD = null;
		// 개소코드
		String szWLOC_CD = null;
		// 운송작업영공구분코드
		String szSP_TRUCK_LOADING_LOC_TP = null;
		// 포인트요구일시
		String szPNT_DMD_DT = null;
		// 발지개소코드
		String szSPOS_WLOC_CD = null;
		// 발지포인트코드
		String szSPOS_YD_PNT_CD = null;
		// //목표행선구분
		// String szYD_AIM_RT_GP = null;
		// //목표야드구분
		// String szYD_AIM_YD_GP = null;
		// //목표동구분
		// String szYD_AIM_BAY_GP = null;
		// 스케줄코드
		String szYD_SCH_CD = null;
		// 차량이송재료매수
		int intYD_CARLD_SH = 0;
		// 차량작업허용매수
		int intYD_WRK_ALW_SH = 0;
		// 대상 재료 중량 합계
		long lngSumMtlWt = 0;
		// 차량작업허용중량
		long lngYD_WRK_ALW_WT = 0;
		// 하위모듈에서 EJB CALL or JMS CALL 판단 변수 정의
		String szIS_EJB_CALL = null;
		// 리턴값
		String szRtnMsg = null;
		String szYD_DIRECT_CARLD_GP = "";
		String szYD_AUTO_LOT = "";
		// TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		// 에러 리턴
		if (szRcvTcCode == null) {

			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error ("
					+ szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_TC_ERROR;

		}
		// TC CODE DISPLAY
		if (bDebugFlag) {

			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		}

		try {

			ydUtils.displayRecord(szOperationName, msgRecord);

			// 받은 전문 편집
			// 설비ID
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if (szYD_EQP_ID.equals("")) {

				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}

			// 차량사용구분
			szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_CAR_USE_GP");
			szYD_CAR_USE_GP = "L";
			if (szYD_CAR_USE_GP.equals("") || !szYD_CAR_USE_GP.equals("L")) {

				szMsg = "[전문 이상] 차량사용구분(" + szYD_CAR_USE_GP + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}
			// 운송장비코드
			szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(msgRecord, "TRN_EQP_CD");
			if (szTRN_EQP_CD.equals("")) {

				szMsg = "[전문 이상] 운송장비코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}
			// 개소코드
			szWLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord, "WLOC_CD");
			if (szWLOC_CD.equals("")) {

				szMsg = "[전문 이상] 개소코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}
			// 운송작업영공구분코드
			szSP_TRUCK_LOADING_LOC_TP = ydDaoUtils.paraRecChkNull(msgRecord,
					"SP_TRUCK_LOADING_LOC_TP");
			if (szSP_TRUCK_LOADING_LOC_TP.equals("")) {

				szMsg = "[전문 이상] 운송작업영공구분코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}
			// 포인트요구일시
			szPNT_DMD_DT = ydDaoUtils.paraRecChkNull(msgRecord, "PNT_DMD_DT");
			if (szPNT_DMD_DT.equals("")) {

				szMsg = "[전문 이상] 포인트요구일시가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}
			// 발지개소코드
			szSPOS_WLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord,
					"SPOS_WLOC_CD");
			if (szSPOS_WLOC_CD.equals("")) {

				szMsg = "[전문 이상] 발지개소코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				// return YdConstant.RETN_CD_NO_PARAM;
			}
			// 발지포인트코드
			szSPOS_YD_PNT_CD = ydDaoUtils.paraRecChkNull(msgRecord,
					"SPOS_YD_PNT_CD");
			if (szSPOS_YD_PNT_CD.equals("")) {

				szMsg = "[전문 이상] 발지포인트코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				// return YdConstant.RETN_CD_NO_PARAM;
			}

			szIS_EJB_CALL = ydDaoUtils.paraRecChkNull(msgRecord, "IS_EJB_CALL");

			szMsg = "[C열연 소재이송상차LOT편성]szIS_EJB_CALL = " + szIS_EJB_CALL;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 상차 Lot 편성 대상 재료 Select ###################################
			blnRtnVal = chkGetCHRMatlFtMvLotGp(msgRecord, rsResult);
			if (!blnRtnVal) {
				return YdConstant.RETN_CD_NOTEXIST;
			}
			//##############################################################
			
			

			// 레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			// JMS TC CODE
			recOutPara.setField("JMS_TC_CD", "YDYDJ248");

			// 레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();

			// 리턴 recordSet 생성
			rsSpecResult = JDTORecordFactory.getInstance().createRecordSet("");
			// 적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara,"YD_STK_COL_GP");
			// =================================================================================
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP")
					.trim().substring(0, 2)
					+ "PT01UM";
			// =================================================================================

			// 차량사양 Select
			blnRtnVal = chkGetCarSpec(szYD_EQP_ID, szYD_CAR_USE_GP,
					szTRN_EQP_CD, rsSpecResult);
			if (!blnRtnVal) {
				return YdConstant.RETN_CD_FAILURE;
			}

			// 레코드 추출
			rsSpecResult.first();
			recSpecPara = rsSpecResult.getRecord();

			// 차량작업허용매수
			// intYD_WRK_ALW_SH = ydDaoUtils.paraRecChkNullInt(recSpecPara,
			// "YD_WRK_ALW_SH");
			intYD_WRK_ALW_SH = 3; // max 3매까지 이송 가능 회사정책

			// 이송 재료 매수가 차량 이송 가능 매수보다 작으면 이송 재료 매수를 차량 이송 가능 매수에 대입한다.
			if (intYD_WRK_ALW_SH > rsResult.size())
				intYD_WRK_ALW_SH = rsResult.size();

			// 차량작업허용중량
			// lngYD_WRK_ALW_WT = ydDaoUtils.paraRecChkNullLong(recSpecPara,
			// "YD_WRK_ALW_WT");
			lngYD_WRK_ALW_WT = ydDaoUtils.paraRecChkNullLong(msgRecord,
					"TRN_EQP_STK_CAPA");
			;

			// 목표행선구분
			recOutPara.setField("YD_AIM_RT_GP",ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_RT_GP"));

			// 차량 이송 가능 매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= intYD_WRK_ALW_SH; Loop_i++) {

				// 대상 재료 중량 합계
				lngSumMtlWt = lngSumMtlWt
						+ Long.parseLong(ydDaoUtils.paraRecChkNull(recPara,	"YD_MTL_WT"));
				// 차량작업허용중량보다 대상재료들의 중량이 많으면 편성 중지
				if (lngYD_WRK_ALW_WT < lngSumMtlWt)
					break;
				// 재료번호
				recOutPara.setField("STL_NO" + Loop_i,
						ydDaoUtils.paraRecChkNull(recPara, "STL_NO"));
				// 권상모음순서
				recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, "" + Loop_i);
				// 차량이송재료매수
				intYD_CARLD_SH = Loop_i;

				// 다음 레코드 추출
				rsResult.next();
				recPara = rsResult.getRecord();

			}

			// 스케줄코드
			recOutPara.setField("YD_SCH_CD", szYD_SCH_CD);
			// 상차 LOT 재료매수
			recOutPara.setField("YD_CARLD_SH", "" + intYD_CARLD_SH);
			// 설비ID
			recOutPara.setField("YD_EQP_ID", szYD_EQP_ID);
			// 차량사용구분
			recOutPara.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
			// 운송장비코드
			recOutPara.setField("TRN_EQP_CD", szTRN_EQP_CD);
			// 개소코드
			recOutPara.setField("WLOC_CD", szWLOC_CD);
			// 운송작업영공구분코드
			recOutPara.setField("SP_TRUCK_LOADING_LOC_TP",
					szSP_TRUCK_LOADING_LOC_TP);
			// 포인트요구일시
			recOutPara.setField("PNT_DMD_DT", szPNT_DMD_DT);
			// 발지개소코드
			recOutPara.setField("SPOS_WLOC_CD", szSPOS_WLOC_CD);
			// 발지포인트코드
			recOutPara.setField("SPOS_YD_PNT_CD", szSPOS_YD_PNT_CD);
			// 재료품목
			recOutPara.setField("YD_MTL_ITEM", "CM");

			recOutPara.setField("IS_EJB_CALL", szIS_EJB_CALL);

			/*
			 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			 * EJB Call or JMS Call 유무를 판단하여 호출하는 기능 수정자 : 임춘수 일자 : 2009.07.13
			 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			 */
			if (szIS_EJB_CALL.equals("Y")) {
				// EJB Call ==> 메소드 콜
				szMsg = "C열연 소재이송 상차 LOT 편성 후 차량상차 작업요구 송신 - 메소드[procCHrCarLdWrkReq] 콜 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.DEBUG);

				szRtnMsg = procCHrCarLdWrkReq(recOutPara);

				szMsg = "C열연 소재이송 상차 LOT 편성 후 차량상차 작업요구 송신 - 메소드[procCHrCarLdWrkReq] 콜 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.DEBUG);
			} else {
				// 전문 송신
				ydDelegate.sendMsg(recOutPara);
				szMsg = "C열연 소재이송 상차 LOT 편성 후 차량상차 작업요구 송신[procCHrCarLdWrkReq] - 전문 송신 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.DEBUG);
			}

			szMsg = "C열연 소재이송 상차 LOT 편성 후 차량상차 작업요구 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		} catch (Exception e) {
			szMsg = "C열연 소재이송 Lot 편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;
		}
		return szRtnMsg;
	} // end of procCHrMatlFtmvLotComp

	/**
	 * 오퍼레이션명 : C열연제품고간이송Lot편성
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String procCHrGdsWhFtmvLotComp(JDTORecord msgRecord)
			throws JDTOException {
		YdDelegate ydDelegate = new YdDelegate();
		// 공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		// 공용 METHOD
		YdUtils ydutils = new YdUtils();

		// 레코드 선언
		JDTORecord recPara = null;
		JDTORecord recOutPara = null;
		JDTORecord recSpecPara = null;
		// 레코드셋 선언
		JDTORecordSet rsResult = null;
		JDTORecordSet rsSpecResult = null;

		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메세지
		String szMsg = "";
		// 메소드명
		String szMethodName = "procCHrGdsWhFtmvLotComp";
		String szOperationName = "C열연제품고간이송Lot편성";

		// 설비ID
		String szYD_EQP_ID = null;
		// 적치열구분
		String szYD_STK_COL_GP = null;
		// //상차정지위치
		// String szYD_CARLD_STOP_LOC = null;
		// 차량사용구분
		String szYD_CAR_USE_GP = null;
		// 운송장비코드
		String szTRN_EQP_CD = null;
		// 개소코드
		String szWLOC_CD = null;
		// 운송작업영공구분코드
		String szSP_TRUCK_LOADING_LOC_TP = null;
		// 포인트요구일시
		String szPNT_DMD_DT = null;
		// 발지개소코드
		String szSPOS_WLOC_CD = null;
		// 발지포인트코드
		String szSPOS_YD_PNT_CD = null;
		// //재료품목
		// String szYD_MTL_ITEM = null;
		// //목표행선구분
		// String szYD_AIM_RT_GP = null;
		// //목표야드구분
		// String szYD_AIM_YD_GP = null;
		// //목표동구분
		// String szYD_AIM_BAY_GP = null;
		// 스케줄코드
		String szYD_SCH_CD = null;
		// 차량이송재료매수
		int intYD_CARLD_SH = 0;
		// 차량작업허용매수
		int intYD_WRK_ALW_SH = 0;
		// 대상 재료 중량 합계
		long lngSumMtlWt = 0;
		// 차량작업허용중량
		long lngYD_WRK_ALW_WT = 0;
		// 연계모듈을 EJB Call or JMS Call 호출 유무 변수
		String szIS_EJB_CALL = null;
		// 리턴값
		String szRtnMsg = null;

		// TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		// 에러 리턴
		if (szRcvTcCode == null) {

			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error ("
					+ szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_TC_ERROR;

		}
		// TC CODE DISPLAY
		if (bDebugFlag) {

			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		}

		try {

			ydUtils.displayRecord(szOperationName, msgRecord);

			// 받은 전문 편집
			// 설비ID
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if (szYD_EQP_ID.equals("")) {

				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}
			// //상차정지위치
			// szYD_CARLD_STOP_LOC = ydDaoUtils.paraRecChkNull(msgRecord,
			// "YD_CARLD_STOP_LOC");
			// if(szYD_CARLD_STOP_LOC.equals("")){
			//
			// szMsg = "[전문 이상] 상차정지위치가 없습니다.";
			// ydUtils.putLog(szSessionName, szMethodName, szMsg,
			// YdConstant.ERROR);
			// return ;
			//
			// }
			// 차량사용구분
			szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_CAR_USE_GP");
			szYD_CAR_USE_GP = "L";
			if (szYD_CAR_USE_GP.equals("") || !szYD_CAR_USE_GP.equals("L")) {

				szMsg = "[전문 이상] 차량사용구분(" + szYD_CAR_USE_GP + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}
			// 운송장비코드
			szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(msgRecord, "TRN_EQP_CD");
			if (szTRN_EQP_CD.equals("")) {

				szMsg = "[전문 이상] 운송장비코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}
			// 개소코드
			szWLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord, "WLOC_CD");
			if (szWLOC_CD.equals("")) {

				szMsg = "[전문 이상] 개소코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}
			// 운송작업영공구분코드
			szSP_TRUCK_LOADING_LOC_TP = ydDaoUtils.paraRecChkNull(msgRecord,
					"SP_TRUCK_LOADING_LOC_TP");
			if (szSP_TRUCK_LOADING_LOC_TP.equals("")) {

				szMsg = "[전문 이상] 운송작업영공구분코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}
			// 포인트요구일시
			szPNT_DMD_DT = ydDaoUtils.paraRecChkNull(msgRecord, "PNT_DMD_DT");
			if (szPNT_DMD_DT.equals("")) {

				szMsg = "[전문 이상] 포인트요구일시가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}
			// 발지개소코드
			szSPOS_WLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord,
					"SPOS_WLOC_CD");
			if (szSPOS_WLOC_CD.equals("")) {

				szMsg = "[전문 이상] 발지개소코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}
			// 발지포인트코드
			szSPOS_YD_PNT_CD = ydDaoUtils.paraRecChkNull(msgRecord,
					"SPOS_YD_PNT_CD");
			if (szSPOS_YD_PNT_CD.equals("")) {

				szMsg = "[전문 이상] 발지포인트코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}
			// //재료품목
			// szYD_MTL_ITEM = ydDaoUtils.paraRecChkNull(msgRecord,
			// "YD_MTL_ITEM");
			// if(szYD_MTL_ITEM.equals("")){
			//
			// szMsg = "[전문 이상] 재료품목이 없습니다.";
			// ydUtils.putLog(szSessionName, szMethodName, szMsg,
			// YdConstant.ERROR);
			// return ;
			//
			// }
			// //목표행선구분
			// szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(msgRecord,
			// "YD_AIM_RT_GP");
			// if(szYD_AIM_RT_GP.equals("")){
			//
			// szMsg = "[전문 이상] 목표행선구분이 없습니다.";
			// ydUtils.putLog(szSessionName, szMethodName, szMsg,
			// YdConstant.ERROR);
			// return ;
			//
			// }
			// //목표야드구분
			// szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(msgRecord,
			// "YD_AIM_YD_GP");
			// if(szYD_AIM_YD_GP.equals("")){
			//
			// szMsg = "[전문 이상] 목표야드구분이 없습니다.";
			// ydUtils.putLog(szSessionName, szMethodName, szMsg,
			// YdConstant.ERROR);
			// return ;
			//
			// }
			// //목표동구분
			// szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord,
			// "YD_AIM_BAY_GP");
			// if(szYD_AIM_BAY_GP.equals("")){
			//
			// szMsg = "[전문 이상] 목표동구분이 없습니다.";
			// ydUtils.putLog(szSessionName, szMethodName, szMsg,
			// YdConstant.ERROR);
			// return ;
			//
			// }

			// 연계모듈을 EJB Call[Y값으로 설정] or JMS Call[값이 없거나 N값으로 설정] 호출 유무 변수 -
			// 지금은 화면으로부터 전송
			szIS_EJB_CALL = ydDaoUtils.paraRecChkNull(msgRecord, "IS_EJB_CALL");

			szMsg = "[C열연 제품고간 이송 LOT편성]szIS_EJB_CALL = " + szIS_EJB_CALL;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 상차 Lot 편성 대상 재료 Select#########################################
			blnRtnVal = chkGetCHRGdsFtMvLotGp(msgRecord, rsResult);
			if (!blnRtnVal) {
				return YdConstant.RETN_CD_NOTEXIST;
			}

			// 레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			// JMS TC CODE
			recOutPara.setField("JMS_TC_CD", "YDYDJ248");

			// 레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();

			// 리턴 recordSet 생성
			rsSpecResult = JDTORecordFactory.getInstance().createRecordSet("");
			// 적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara,
					"YD_STK_COL_GP");

			if (szYD_STK_COL_GP.substring(1, 2).equals("B")
					|| szYD_STK_COL_GP.substring(1, 2).equals("C")) {
				if (Integer.parseInt(szYD_STK_COL_GP.substring(2, 4)) < 31) {
					szYD_SCH_CD = ydDaoUtils
							.paraRecChkNull(recPara, "YD_STK_COL_GP").trim()
							.substring(0, 2)
							+ "PT52UM";
				} else {
					szYD_SCH_CD = ydDaoUtils
							.paraRecChkNull(recPara, "YD_STK_COL_GP").trim()
							.substring(0, 2)
							+ "PT02UM";
				}
			} else {
				szYD_SCH_CD = ydDaoUtils
						.paraRecChkNull(recPara, "YD_STK_COL_GP").trim()
						.substring(0, 2)
						+ "PT02UM";
			}

			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP")
					.trim().substring(0, 2)
					+ "PT02UM";
			// =================================================================================

			// 차량사양 Select
			blnRtnVal = chkGetCarSpec(szYD_EQP_ID, szYD_CAR_USE_GP,
					szTRN_EQP_CD, rsSpecResult);
			if (!blnRtnVal) {
				return YdConstant.RETN_CD_FAILURE;
			}

			// 레코드 추출
			rsSpecResult.first();
			recSpecPara = rsSpecResult.getRecord();

			// 차량작업허용매수
			// intYD_WRK_ALW_SH = ydDaoUtils.paraRecChkNullInt(recSpecPara,
			// "YD_WRK_ALW_SH");
			intYD_WRK_ALW_SH = 3; // max 3매까지 이송 가능 회사정책

			// 이송 재료 매수가 차량 이송 가능 매수보다 작으면 이송 재료 매수를 차량 이송 가능 매수에 대입한다.
			if (intYD_WRK_ALW_SH > rsResult.size())
				intYD_WRK_ALW_SH = rsResult.size();

			// 차량작업허용중량
			// lngYD_WRK_ALW_WT = ydDaoUtils.paraRecChkNullLong(recSpecPara,
			// "YD_WRK_ALW_WT");
			lngYD_WRK_ALW_WT = ydDaoUtils.paraRecChkNullLong(msgRecord,
					"TRN_EQP_STK_CAPA");
			;

			// 목표행선구분
			recOutPara.setField("YD_AIM_RT_GP",
					ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_RT_GP"));

			// 차량 이송 가능 매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= intYD_WRK_ALW_SH; Loop_i++) {

				// 대상 재료 중량 합계
				lngSumMtlWt = lngSumMtlWt
						+ Long.parseLong(ydDaoUtils.paraRecChkNull(recPara,
								"YD_MTL_WT"));
				// 차량작업허용중량보다 대상재료들의 중량이 많으면 편성 중지
				if (lngYD_WRK_ALW_WT < lngSumMtlWt)
					break;
				// 재료번호
				recOutPara.setField("STL_NO" + Loop_i,
						ydDaoUtils.paraRecChkNull(recPara, "STL_NO"));
				// 권상모음순서
				recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, "" + Loop_i);
				// 차량이송재료매수
				intYD_CARLD_SH = Loop_i;

				// 다음 레코드 추출
				rsResult.next();
				recPara = rsResult.getRecord();

			}

			// 스케줄코드
			recOutPara.setField("YD_SCH_CD", szYD_SCH_CD);
			// 상차 LOT 재료매수
			recOutPara.setField("YD_CARLD_SH", "" + intYD_CARLD_SH);
			// 설비ID
			recOutPara.setField("YD_EQP_ID", szYD_EQP_ID);
			// 차량사용구분
			recOutPara.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
			// 운송장비코드
			recOutPara.setField("TRN_EQP_CD", szTRN_EQP_CD);
			// 개소코드
			recOutPara.setField("WLOC_CD", szWLOC_CD);
			// 운송작업영공구분코드
			recOutPara.setField("SP_TRUCK_LOADING_LOC_TP",
					szSP_TRUCK_LOADING_LOC_TP);
			// 포인트요구일시
			recOutPara.setField("PNT_DMD_DT", szPNT_DMD_DT);
			// 발지개소코드
			recOutPara.setField("SPOS_WLOC_CD", szSPOS_WLOC_CD);
			// 발지포인트코드
			recOutPara.setField("SPOS_YD_PNT_CD", szSPOS_YD_PNT_CD);
			// 재료품목
			recOutPara.setField("YD_MTL_ITEM", "CG");

			recOutPara.setField("IS_EJB_CALL", szIS_EJB_CALL);

			/*
			 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			 * EJB Call or JMS Call 유무를 판단하여 호출하는 기능 수정자 : 임춘수 일자 : 2009.07.13
			 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			 */
			if (szIS_EJB_CALL.equals("Y")) {
				// EJB Call ==> 메소드 콜
				szMsg = "C열연 제품고간 이송 LOT 편성 후 차량상차 작업요구 송신 - 메소드[procCHrCarLdWrkReq] 콜 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.DEBUG);

				szRtnMsg = procCHrCarLdWrkReq(recOutPara);

				szMsg = "C열연 제품고간 이송 LOT 편성 후 차량상차 작업요구 송신 - 메소드[procCHrCarLdWrkReq] 콜 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.DEBUG);
			} else {
				// 전문 송신
				ydDelegate.sendMsg(recOutPara);
				szMsg = "C열연 제품고간 이송 LOT 편성 후 차량상차 작업요구 송신[procCHrCarLdWrkReq] - 전문 송신 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.DEBUG);
			}

			szMsg = "C열연 제품고간 이송 상차 LOT 편성 후 차량상차 작업요구 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		} catch (Exception e) {
			szMsg = "C열연 제품고간 이송 Lot 편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;
		}
		return szRtnMsg;
	} // end of procCHrGdsWhFtmvLotComp

	/**
	 * 오퍼레이션명 : C열연소재대차상차Lot편성
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procCHrMatlTcarLdLotComp(JDTORecord msgRecord)
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
		String szMethodName = "procCHrMatlTcarLdLotComp";
		// 사용자
		String szUser = "SYSTEM";

		// 전문 생성 일시
		String szDate = null;

		// 설비ID
		String szYD_EQP_ID = null;
		// 상차정지위치
		String szYD_CARLD_STOP_LOC = null;
		// 재료품목
		String szYD_MTL_ITEM = null;
		// 목표행선구분
		String szYD_AIM_RT_GP = null;
		// 목표야드구분
		String szYD_AIM_YD_GP = null;
		// 목표동구분
		String szYD_AIM_BAY_GP = null;
		// 스케줄코드
		String szYD_SCH_CD = null;
		// 대차이송재료매수
		int intYD_CARLD_SH = 0;
		// 대상 재료 중량 합계
		long lngSumMtlWt = 0;
		// 대차작업허용중량(100t)
		long lngYD_WRK_ALW_WT = 100000;

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
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;

			}
			// 상차정지위치
			szYD_CARLD_STOP_LOC = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_CARLD_STOP_LOC");
			if (szYD_CARLD_STOP_LOC.equals("")) {

				szMsg = "[전문 이상] 상차정지위치가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;

			}
			// 재료품목
			szYD_MTL_ITEM = ydDaoUtils.paraRecChkNull(msgRecord, "YD_MTL_ITEM");
			if (szYD_MTL_ITEM.equals("")) {

				szMsg = "[전문 이상] 재료품목이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;

			}
			// 목표행선구분
			szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_AIM_RT_GP");
			if (szYD_AIM_RT_GP.equals("")) {

				szMsg = "[전문 이상] 목표행선구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;

			}
			// 목표동구분
			szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_AIM_BAY_GP");
			if (szYD_AIM_BAY_GP.equals("")) {

				szMsg = "[전문 이상] 목표동구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;

			}
			// 목표야드구분
			szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_AIM_YD_GP");
			if (szYD_AIM_YD_GP.equals("")) {

				szMsg = "[전문 이상] 목표야드구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;

			}

			// =================================================================================
			// 수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			// BRE 등록 안됨...테스트용 스케줄코드 생성
			// 추후 구현..
			// szYD_SCH_CD = "HDCMTCUM";
			szYD_SCH_CD = "J"
					+ ydDaoUtils.paraRecChkNull(msgRecord, "YD_CARLD_STOP_LOC")
							.substring(1, 6) + "UM";
			// =================================================================================

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 상차 Lot 편성 대상 재료 Select
			blnRtnVal = chkGetMtlFtmvCarLoadLotGp(msgRecord, rsResult);
			if (!blnRtnVal)
				return;

			// 레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			// JMS TC CODE
			recOutPara.setField("JMS_TC_CD", "YDYDJ249");
			// 첫 레코드로 커서이동
			rsResult.first();

			// 대상 소재 매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {

				// 레코드 추출
				recPara = rsResult.getRecord();
				// 대상 재료 중량 합계
				lngSumMtlWt = lngSumMtlWt
						+ Long.parseLong(ydDaoUtils.paraRecChkNull(recPara,
								"YD_MTL_WT"));
				// 대차작업허용중량보다 대상재료들의 중량이 많으면 편성 중지
				if (lngYD_WRK_ALW_WT < lngSumMtlWt)
					break;
				// 재료번호
				recOutPara.setField("STL_NO" + Loop_i,
						ydDaoUtils.paraRecChkNull(recPara, "STL_NO"));
				// 권상모음순서
				recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, "" + Loop_i);
				// 대차이송재료매수
				intYD_CARLD_SH = Loop_i;

				// 다음 레코드로 커서 이동
				rsResult.next();

			}

			// 전문 발생 일시
			szDate = ydUtils.getCurDate("yyyy/MM/dd HH:mm:ss");

			// 발생 일시
			recOutPara.setField("JMS_TC_CREATE_DDTT", szDate);
			// 스케줄코드
			recOutPara.setField("YD_SCH_CD", szYD_SCH_CD);
			// CARRY_IN 재료매수
			recOutPara.setField("YD_CARLD_SH", "" + intYD_CARLD_SH);
			// 적치열구분
			recOutPara.setField("YD_STK_COL_GP", szYD_EQP_ID);
			// 적치BED번호
			recOutPara.setField("YD_STK_BED_NO", "01");

			// 2009.02.12 김진욱
			// 수정/////////////////////////////////////////////////
			// 목표야드구분
			recOutPara.setField("YD_AIM_YD_GP", szYD_AIM_YD_GP);
			// 목표동구분
			recOutPara.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP);
			// ////////////////////////////////////////////////////////////////////

			// 전문 송신
			ydDelegate.sendMsg(recOutPara);
			szMsg = "C열연 소재 대차상차 Lot 편성 후 대차 상차 작업요구 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		} catch (Exception e) {
			szMsg = "C열연 소재 대차상차 Lot 편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}

	} // end of procCHrMatlTcarLdLotComp

	/**
	 * 오퍼레이션명 : C열연 소재이송 Lot 편성 데이터 유무체크 및 데이터 반환
	 * 
	 * @param JDTORecord
	 *            recInPara 파라미터 레코드 JDTORecordSet rsResult 결과레코드셋
	 * @return boolean true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetCHRMatlFtMvLotGp(JDTORecord recInPara,
			JDTORecordSet rsResult) throws JDTOException {

		// 저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메소드명
		String szMethodName = "chkGetCHRMatlFtMvLotGp";
		String szMsg = null;
		String szWLOC_CD = null;
		String szTRN_EQP_CD = null;
		// 레코드 선언
		JDTORecord recPara = null;

		try { 

			// 개소코드에 따라 동구분이 틀려져야한다.
			szWLOC_CD = ydDaoUtils.paraRecChkNull(recInPara, "WLOC_CD");
			szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(recInPara, "TRN_EQP_CD");

			if (szWLOC_CD.equals("DJY21") || szWLOC_CD.equals("DJY22")) {
				// C열연 D동에 있는 재공이송작업대기
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("SPOS_WLOC_CD", szWLOC_CD);
				recPara.setField("TRN_EQP_CD", szTRN_EQP_CD);
				// 저장품 테이블 조회
				/* com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrMatlLotC*/
				intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 505);
				// 대상 소재가 존재하면 리턴
				if (intRtnVal > 0)
					return blnRtnVal = true;
			} else {
				// 에러 메시지
				szMsg = "<chkGetCHRMatlFtMvLotGp> 개소코드가 C열연 개소코드가 아닙니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;
			}

		} catch (Exception e) {
			szMsg = "C열연 소재이송 Lot 편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: "
					+ e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} // end of chkGetCHRMatlFtMvLotGp

	/**
	 * 오퍼레이션명 : C열연 제품이송 Lot 편성 데이터 유무체크 및 데이터 반환
	 * 
	 * @param JDTORecord
	 *            recInPara 파라미터 레코드 JDTORecordSet rsResult 결과레코드셋
	 * @return boolean true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetCHRGdsFtMvLotGp(JDTORecord recInPara,
			JDTORecordSet rsResult) throws JDTOException {

		// 저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메소드명
		String szMethodName = "chkGetCHRGdsFtMvLotGp";
		String szMsg = null;

		// 레코드 선언
		JDTORecord recPara = null;
		String szWLOC_CD = "";
		String szTRN_EQP_CD = "";

		try {

			szWLOC_CD = ydDaoUtils.paraRecChkNull(recInPara, "WLOC_CD");
			szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(recInPara, "TRN_EQP_CD");

			// C열연 D동에 있는 B열연 이송대기인 HCR 열연주편
			// 재료품목(열연주편)
			// 레코드생성

			// recPara = JDTORecordFactory.getInstance().create();
			// recPara.setField("YD_MTL_ITEM", "CG");
			// //목표행선구분(이송대기)
			// recPara.setField("YD_AIM_RT_GP", "JB");
			// //FROM 야드동(C열연 D동)
			// recPara.setField("YD_STK_COL_GP", "J");
			// //저장품 테이블 조회
			// intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 18);
			// //대상 소재가 존재하면 리턴
			// if(intRtnVal > 0) return blnRtnVal = true;

			// C열연 D동에 있는 재공이송작업대기
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("SPOS_WLOC_CD", szWLOC_CD);
			recPara.setField("TRN_EQP_CD", szTRN_EQP_CD);
			// 저장품 테이블 조회
			/* com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrMatlLotC*/
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 505);
			// 대상 소재가 존재하면 리턴
			if (intRtnVal > 0)
				return blnRtnVal = true;

		} catch (Exception e) {
			szMsg = "C열연  제품이송 Lot 편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: "
					+ e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} // end of chkGetCHRGdsFtMvLotGp

	/**
	 * 오퍼레이션명 : C열연 제품 대차상차 Lot 편성
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procCHrGdsTcarLdLotComp(JDTORecord msgRecord)
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
		String szMethodName = "procCHrGdsTcarLdLotComp";
		// 사용자
		String szUser = "SYSTEM";

		// 전문 생성 일시
		String szDate = null;

		// 설비ID
		String szYD_EQP_ID = null;
		// 상차정지위치
		String szYD_CARLD_STOP_LOC = null;
		// 재료품목
		String szYD_MTL_ITEM = null;
		// 목표행선구분
		String szYD_AIM_RT_GP = null;
		// 목표동구분
		String szYD_AIM_BAY_GP = null;
		// 스케줄코드
		String szYD_SCH_CD = null;
		// 대차이송재료매수
		int intYD_CARLD_SH = 0;
		// 대상 재료 중량 합계
		long lngSumMtlWt = 0;
		// 대차작업허용중량(100t)
		long lngYD_WRK_ALW_WT = 100000;

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
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;

			}
			// 상차정지위치
			szYD_CARLD_STOP_LOC = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_CARLD_STOP_LOC");
			if (szYD_CARLD_STOP_LOC.equals("")) {

				szMsg = "[전문 이상] 상차정지위치가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;

			}
			// 재료품목
			szYD_MTL_ITEM = ydDaoUtils.paraRecChkNull(msgRecord, "YD_MTL_ITEM");
			if (szYD_MTL_ITEM.equals("")) {

				szMsg = "[전문 이상] 재료품목이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;

			}
			// 목표행선구분
			szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_AIM_RT_GP");
			if (szYD_AIM_RT_GP.equals("")) {

				szMsg = "[전문 이상] 목표행선구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;

			}
			// 목표동구분
			szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_AIM_BAY_GP");
			if (szYD_AIM_BAY_GP.equals("")) {

				szMsg = "[전문 이상] 목표동구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;

			}

			// =================================================================================
			// 수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			// BRE 등록 안됨...테스트용 스케줄코드 생성
			// 추후 구현..
			szYD_SCH_CD = "HDCGTCUM";
			// =================================================================================

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 상차 Lot 편성 대상 재료 Select
			blnRtnVal = chkGetCHRGdsTcarLoadLotGp(msgRecord, rsResult);
			if (!blnRtnVal)
				return;

			// 레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			// JMS TC CODE
			recOutPara.setField("JMS_TC_CD", "YDYDJ249");
			// 첫 레코드로 커서이동
			rsResult.first();

			// 대상 소재 매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {

				// 레코드 추출
				recPara = rsResult.getRecord();
				// 대상 재료 중량 합계
				lngSumMtlWt = lngSumMtlWt
						+ Long.parseLong(ydDaoUtils.paraRecChkNull(recPara,
								"YD_MTL_WT"));
				// 대차작업허용중량보다 대상재료들의 중량이 많으면 편성 중지
				if (lngYD_WRK_ALW_WT < lngSumMtlWt)
					break;
				// 재료번호
				recOutPara.setField("STL_NO" + Loop_i,
						ydDaoUtils.paraRecChkNull(recPara, "STL_NO"));
				// 권상모음순서
				recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, "" + Loop_i);
				// 대차이송재료매수
				intYD_CARLD_SH = Loop_i;

				// 다음 레코드로 커서 이동
				rsResult.next();

			}

			// 전문 발생 일시
			szDate = ydUtils.getCurDate("yyyy/MM/dd HH:mm:ss");

			// 발생 일시
			recOutPara.setField("JMS_TC_CREATE_DDTT", szDate);
			// 스케줄코드
			recOutPara.setField("YD_SCH_CD", szYD_SCH_CD);
			// CARRY_IN 재료매수
			recOutPara.setField("YD_CARLD_SH", "" + intYD_CARLD_SH);
			// 적치열구분
			recOutPara.setField("YD_STK_COL_GP", szYD_EQP_ID);
			// 적치BED번호
			recOutPara.setField("YD_STK_BED_NO", "01");

			// 전문 송신
			ydDelegate.sendMsg(recOutPara);
			szMsg = "C열연 제품 대차상차 Lot 편성 후 대차 상차 작업요구 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		} catch (Exception e) {
			szMsg = "C열연 제품 대차상차 Lot 편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}

	} // end of procCHrGdsTcarLdLotComp

	/**
	 * 오퍼레이션명 : C열연 제품 대차상차 Lot 편성 데이터 유무체크 및 데이터 반환
	 * 
	 * @param JDTORecord
	 *            recInPara 파라미터 레코드 JDTORecordSet rsResult 결과레코드셋
	 * @return boolean true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetCHRGdsTcarLoadLotGp(JDTORecord recInPara,
			JDTORecordSet rsResult) throws JDTOException {

		// 저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메소드명
		String szMethodName = "chkGetCHRGdsTcarLoadLotGp";
		String szMsg = null;

		// 레코드 선언
		JDTORecord recPara = null;

		try {
			// 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();

			// 재료품목
			recPara.setField("YD_MTL_ITEM",
					ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_ITEM"));
			// 목표행선구분
			recPara.setField("YD_AIM_RT_GP",
					ydDaoUtils.paraRecChkNull(recInPara, "YD_AIM_RT_GP"));
			// 목표야드구분
			recPara.setField("YD_AIM_YD_GP",
					ydDaoUtils.paraRecChkNull(recInPara, "YD_AIM_YD_GP"));
			// 목표동구분
			recPara.setField("YD_AIM_BAY_GP",
					ydDaoUtils.paraRecChkNull(recInPara, "YD_AIM_BAY_GP"));
			// FROM 야드동
			recPara.setField("YD_STK_COL_GP",
					ydDaoUtils.paraRecChkNull(recInPara, "YD_CARLD_STOP_LOC")
							.substring(0, 2));

			// 저장품 테이블 조회
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 17);

			// 리턴값 메세지처리
			if (intRtnVal >= 1) {

				blnRtnVal = true;

			} else if (intRtnVal == 0) {

				szMsg = "C열연 제품 대차상차 Lot 편성 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == -2) {

				szMsg = "C열연 제품 대차상차 Lot 편성 데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = "C열연 제품 대차상차 Lot 편성 데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch (Exception e) {
			szMsg = "C열연 제품 대차상차 Lot 편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: "
					+ e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} // end of chkGetCHRGdsTcarLoadLotGp

	// /**
	// * 오퍼레이션명 : 크레인작업재료 유무 체크
	// *
	// * @param String szStlNo 설비ID
	// * @return boolean true(작업재료없음), false(작업재료있음)
	// * @throws JDTOException
	// */
	// public boolean chkCrnWrkMtl(String szStlNo)throws JDTOException {
	//
	// //크레인 작업 재료
	// YdCrnWrkMtlDao ydCrnWrkMtlDao = new YdCrnWrkMtlDao();
	// //리턴값(boolean)
	// boolean blnRtnVal = false;
	// //리턴값(int)
	// int intRtnVal = 0;
	// //메소드명
	// String szMethodName = "chkCrnWrkMtl";
	// String szMsg = null;
	//
	// //레코드 선언
	// JDTORecord recPara = null;
	// JDTORecordSet rsResult = null;
	//
	// try {
	//
	// //레코드 생성
	// recPara = JDTORecordFactory.getInstance().create();
	// //레코드셋 생성
	// rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	// //재료번호
	// recPara.setField("STL_NO", szStlNo);
	//
	// //설비 테이블 조회
	// intRtnVal = ydCrnWrkMtlDao.getYdCrnwrkmtl(recPara, rsResult, 2);
	//
	// //리턴값 메세지처리
	// if(intRtnVal >= 1){
	//
	// szMsg = "재료번호("+ szStlNo +")의 소재(제품)이 이미 크레인스케줄 작업 재료에 등록되어 있습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// blnRtnVal = false;
	//
	// } else if(intRtnVal == 0){
	//
	// blnRtnVal = true;
	//
	// } else if(intRtnVal == -2){
	//
	// szMsg = "재료번호("+ szStlNo +")에 대한 크레인스케줄 작업 재료 조회중 parameter error 발생.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// blnRtnVal = false;
	//
	// } else {
	//
	// szMsg = "재료번호("+ szStlNo +")에 대한 크레인스케줄 작업 재료 조회중 오류 발생.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// blnRtnVal = false;
	//
	// }
	// } catch(Exception e){
	// szMsg = "크레인작업재료 유무 체크 중 예외발생! 예외메세지: " + e.getMessage();
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return blnRtnVal = false;
	// }
	// return blnRtnVal;
	// } //end of chkCrnWrkMtl
	//

	/**
	 * 오퍼레이션명 : C열연 소재 임가공 Lot 편성 데이터 유무체크 및 데이터 반환
	 * 
	 * @param JDTORecord
	 *            recInPara 파라미터 레코드 JDTORecordSet rsResult 결과레코드셋
	 * @return boolean true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetCHRMatlRentProcLotGp(JDTORecord recInPara,
			JDTORecordSet rsResult) throws JDTOException {

		// 저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메소드명
		String szMethodName = "chkGetCHRMatlRentProcLotGp";
		String szMsg = null;

		// 레코드 선언
		JDTORecord recPara = null;

		try {
			// 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();

			// 재료품목
			recPara.setField("YD_MTL_ITEM",
					ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_ITEM"));
			// 목표행선구분
			recPara.setField("YD_AIM_RT_GP",
					ydDaoUtils.paraRecChkNull(recInPara, "YD_AIM_RT_GP"));
			// 임가공사코드
			// recPara.setField("RENTPROC_CD",
			// ydDaoUtils.paraRecChkNull(recInPara, "RENTPROC_CD"));
			// test용 임시 임가공사코드 필드 -> TB_YD_STOCK.FRTOMOVE_PLANT_GP
			recPara.setField("FRTOMOVE_PLANT_GP",
					ydDaoUtils.paraRecChkNull(recInPara, "RENTPROC_CD"));
			// //목표야드구분
			// recPara.setField("YD_AIM_YD_GP",
			// ydDaoUtils.paraRecChkNull(recInPara, "YD_AIM_YD_GP"));
			// //목표동구분
			// recPara.setField("YD_AIM_BAY_GP",
			// ydDaoUtils.paraRecChkNull(recInPara, "YD_AIM_BAY_GP"));
			// FROM 야드동
			recPara.setField("YD_STK_COL_GP",
					ydDaoUtils.paraRecChkNull(recInPara, "YD_CARLD_STOP_LOC")
							.substring(0, 2));

			// 저장품 테이블 조회
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 17);

			// 리턴값 메세지처리
			if (intRtnVal >= 1) {

				blnRtnVal = true;

			} else if (intRtnVal == 0) {

				szMsg = "C열연 소재 임가공 Lot 편성 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == -2) {

				szMsg = "C열연 소재 임가공 Lot 편성 데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = "C열연 소재 임가공 Lot 편성 데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch (Exception e) {
			szMsg = "C열연 소재 임가공 Lot 편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: "
					+ e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} // end of chkGetCHRMatlRentProcLotGp

	/**
	 * 오퍼레이션명 : 차량사양 유무체크 및 조회결과 데이터 반환
	 * 
	 * @param String
	 *            szEqpId 설비ID String szYD_CAR_USE_GP 차량사용구분 String szTrnEqpCd
	 *            운송장비코드 JDTORecordSet rsResult 결과레코드셋
	 * @return boolean true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetCarSpec(String szEqpId, String szCarUseGp,
			String szTrnEqpCd, JDTORecordSet rsResult) throws JDTOException {

		// 차량사양 DAO
		YdCarSpecDao ydCarSpecDao = new YdCarSpecDao();
		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메소드명
		String szMethodName = "chkGetCarSpec";
		String szMsg = null;

		// 레코드 선언
		JDTORecord recPara = null;

		try {

			// 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();

			// 설비ID
			// recPara.setField("YD_EQP_ID", szEqpId);
			// 차량사용구분
			// recPara.setField("YD_CAR_USE_GP", szCarUseGp);
			// 운송장비코드
			recPara.setField("TRN_EQP_CD", szTrnEqpCd);
			// //차량번호
			// recPara.setField("CAR_NO", "");
			// 설비 테이블 조회
			intRtnVal = ydCarSpecDao.getYdCarspec(recPara, rsResult, 2);

			// 리턴값 메세지처리
			if (intRtnVal > 1) {

				szMsg = "설비ID(" + szEqpId + ") " + "운송장비코드(" + szTrnEqpCd
						+ ")에 대한 설비 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == 1) {

				blnRtnVal = true;

			} else if (intRtnVal == 0) {

				szMsg = "설비ID(" + szEqpId + ") " + "운송장비코드(" + szTrnEqpCd
						+ ")에 대한 설비 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == -2) {

				szMsg = "설비ID(" + szEqpId + ") " + "운송장비코드(" + szTrnEqpCd
						+ ")에 대한 차량사양 조회중 parameter error 발생.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = "설비ID(" + szEqpId + ") " + "운송장비코드(" + szTrnEqpCd
						+ ")에 대한 차량사양 조회중 오류 발생.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch (Exception e) {
			szMsg = "차량사양 유무체크 및 조회결과 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return blnRtnVal = false;
		}
		return blnRtnVal;
	} // end of chkGetCarSpec

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
			double lngMtlW = ydDaoUtils.paraRecChkNullDouble(recPara,
					"YD_MYD_MTL_WTL_W");
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
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return blnRtnVal = false;
			}

			// 크레인 작업가능 폭과 재료의 폭 비교
			if (lngAbleW < lngMtlW) {
				szMsg = "크레인 작업가능 폭(" + lngAbleW + ")보다 재료의 폭(" + lngMtlW
						+ ")이 더 큽니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return blnRtnVal = false;
			}

			// 크레인 작업가능 중량과 재료의 중량 비교
			if (lngAbleWt < lngMtlWt) {
				szMsg = "크레인 작업가능 중량(" + lngAbleWt + ")보다 재료의 중량(" + lngMtlWt
						+ ")이 더 큽니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return blnRtnVal = false;
			}

		} catch (Exception e) {
			szMsg = "크레인작업가능사양과 재료사양을 체크 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return blnRtnVal = false;
		}
		return blnRtnVal = true;
	} // end of chkCrnSpecMtlSpec

	// /**
	// * 오퍼레이션명 : 재료사양을 체크(재료폭)
	// *
	// * @param String szStlNo 재료번호
	// * String szEqpId 크레인 설비ID
	// * @return int 작업가능매수
	// * @throws JDTOException
	// */
	// public int chkWrkMtl_W(String[] szStlNo, String szEqpId, int
	// intMtlCnt)throws JDTOException {
	//
	// //리턴값(boolean)
	// int intRtnVal = 0;
	// boolean blnRtnVal = true;
	// //메세지
	// String szMsg = null;
	// //메소드명
	// String szMethodName = "chkWrkMtl_W";
	// //레코드 선언
	// JDTORecord recPara = null;
	// //레코드셋 선언
	// JDTORecordSet rsResult = null;
	// double dblYD_MTL_W;
	// int intStlCnt = 0;
	// double intCrnTongWTol = 0;
	// //최대 폭
	// double lngMaxWidth = 0;
	//
	// int Loop_i = 0;
	//
	// try {
	// intStlCnt = intMtlCnt;
	//
	// for(int i=1; i<=intStlCnt; i++) {
	//
	// if (szStlNo[i] != null) {
	// szMsg = "순번[" + i + "] 재료번호[" + szStlNo[i] + "] 입니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	// } else {
	// intStlCnt = i;
	// break;
	// }
	// }
	//
	//
	// //dblYD_MTL_W = new double[intStlCnt];
	//
	// //레코드셋 재생성
	// rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	//
	// //크레인사양 체크 및 조회
	// blnRtnVal = this.chkGetCrnSpec(szEqpId, rsResult);
	// if(!blnRtnVal) return 0;
	//
	// //크레인사양 추출
	// rsResult.first();
	// recPara = rsResult.getRecord();
	//
	// // 크레인 작업 능력
	// // 작업가능길이
	// //크레인 집게허용 오차
	// intCrnTongWTol = ydDaoUtils.paraRecChkNullDouble(recPara,
	// "YD_CRN_TONG_W_TOL");
	//
	//
	//
	// for(Loop_i = 1; Loop_i <= intStlCnt; Loop_i++){
	//
	// if (szStlNo[Loop_i] != null) {
	//
	// rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	// blnRtnVal = this.chkGetStock(szStlNo[Loop_i], rsResult);
	// if(!blnRtnVal) return intRtnVal;
	//
	// //결과 레코드 추출
	// rsResult.first();
	// recPara = rsResult.getRecord();
	// // 폭
	// dblYD_MTL_W = ydDaoUtils.paraRecChkNullLong(recPara, "YD_MTL_W");
	//
	// if(lngMaxWidth > dblYD_MTL_W + intCrnTongWTol) {
	//
	// szMsg = "lngMaxWidt : " + lngMaxWidth + " > lngCurrWidth : " +
	// dblYD_MTL_W + " intCrnTongWTol : " + intCrnTongWTol;
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	// return intRtnVal = Loop_i-1;
	// }
	//
	// if (dblYD_MTL_W > lngMaxWidth) lngMaxWidth = dblYD_MTL_W;
	// }
	//
	// }
	//
	//
	//
	// } catch(Exception e){
	// szMsg = "크레인작업가능사양과 재료사양을 체크 중 예외발생! 예외메세지: " + e.getMessage();
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return 0;
	// }
	// return intRtnVal = intStlCnt;
	// } //end of chkCrnSpecMtlSpec
	//

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
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == 1) {

				blnRtnVal = true;

			} else if (intRtnVal == 0) {

				szMsg = "크레인설비ID(" + szEqpId + ")에 대한 크레인사양 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == -2) {

				szMsg = "크레인설비ID(" + szEqpId
						+ ")로 크레인사양 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = "크레인설비ID(" + szEqpId + ")로 크레인사양 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
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
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == 1) {
				szMsg = "재료번호(" + szStlNo + ")에 대한 저장품 데이터 조회 성공!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.DEBUG);
				blnRtnVal = true;

			} else if (intRtnVal == 0) {

				szMsg = "재료번호(" + szStlNo + ")에 대한 저장품 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == -2) {

				szMsg = "재료번호(" + szStlNo + ")로 저장품 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = "재료번호(" + szStlNo + ")로 저장품 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
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
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == 1) {

				blnRtnVal = true;

			} else if (intRtnVal == 0) {

				szMsg = "설비ID(" + szEqpId + ")에 대한 설비 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == -2) {

				szMsg = "설비ID(" + szEqpId + ")로 설비 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = "설비ID(" + szEqpId + ")로 설비 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch (Exception e) {
			szMsg = "설비 유무체크 및 조회결과 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return blnRtnVal = false;
		}
		return blnRtnVal;
	} // end of chkGetEqp

	// /**
	// * 오퍼레이션명 : C연주 C열연 보급Lot 편성 데이터 유무체크 및 데이터 반환
	// *
	// * @param String szSchCd 스케줄코드
	// * String szAimRtGp 목표행선구분
	// * JDTORecordSet rsResult 결과레코드셋
	// * @return boolean true(성공), false(실패)
	// * @throws JDTOException
	// */
	// public boolean chkGetCHRSupplyLotGp(String szSchCd, String
	// szPTOP_PLNT_GP, JDTORecordSet rsResult)throws JDTOException {
	//
	// //저장품 DAO
	// YdStockDao ydStockDao = new YdStockDao();
	// //리턴값(boolean)
	// boolean blnRtnVal = false;
	// //리턴값(int)
	// int intRtnVal = 0;
	// //메소드명
	// String szMethodName = "chkGetCHRSupplyLotGp";
	// String szMsg = null;
	//
	// //레코드 선언
	// JDTORecord recPara = null;
	//
	// try {
	// //레코드 생성
	// recPara = JDTORecordFactory.getInstance().create();
	//
	// //재료품목
	// //recPara.setField("YD_MTL_ITEM", "SH");
	// //목표행선구분
	// //recPara.setField("YD_AIM_RT_GP", szAimRtGp);
	// recPara.setField("PTOP_PLNT_GP", szPTOP_PLNT_GP);
	// //야드구분
	// recPara.setField("YD_GP", szSchCd.substring(0, 1));
	// //동구분
	// recPara.setField("YD_BAY_GP", szSchCd.substring(1, 2));
	//
	// //저장품 테이블 조회
	// intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 91);
	//
	// //리턴값 메세지처리
	// if(intRtnVal >= 1){
	//
	// blnRtnVal = true;
	//
	// } else if(intRtnVal == 0){
	//
	// szMsg = "스케줄코드(" + szSchCd + ")에 대한 C연주 C열연 보급Lot 편성 데이터가 없습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// blnRtnVal = false;
	//
	// } else if(intRtnVal == -2){
	//
	// szMsg = "스케줄코드(" + szSchCd +
	// ")에 대한 C연주 C열연 보급Lot 편성 데이터 조회중 parameter error 발생!";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// blnRtnVal = false;
	//
	// } else {
	//
	// szMsg = "스케줄코드(" + szSchCd + ")에 대한 C연주 C열연 보급Lot 편성 데이터 조회중 오류 발생!";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// blnRtnVal = false;
	//
	// }
	// } catch(Exception e){
	// szMsg = "C연주 C열연 보급Lot 편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: " +
	// e.getMessage();
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// blnRtnVal = false;
	// }
	// return blnRtnVal;
	// } //end of chkGetCHRSupplyLotGp
	//
	//
	//
	//

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
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == 0) {

				szMsg = "재료번호(" + szStlNo + ")에 대한 작업예약재료 데이터가 등록 가능합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.DEBUG);
				blnRtnVal = true;

			} else if (intRtnVal == -2) {

				szMsg = "재료번호(" + szStlNo + ")로 작업예약재료 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = "재료번호(" + szStlNo + ")로 작업예약재료 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
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
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == 1) {

				blnRtnVal = true;

			} else if (intRtnVal == 0) {

				szMsg = "작업예약ID를 구하지 못했습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == -2) {

				szMsg = "작업예약ID 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = "작업예약ID 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch (Exception e) {
			szMsg = "작업예약ID생성 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} // end of getYdWbookId

	// /**
	// * 출하차량스케줄 수정
	// * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	// * @param msgRecord
	// * @return
	// * @throws JDTOException
	// */
	// public String procDistCarSch(JDTORecord msgRecord) {
	// /*
	// * 업무기준: 1. 차량스케줄을 조회해서 상차도착으로 수정
	// * 2. 해당 상차작업예약으로 크레인스케줄 호출
	// * 수정자 : 임춘수
	// * 수정일 :
	// * 1. 2009.11.17 - 최초등록
	// * 파라미터정의 :
	// * 1. CAR_NO - 차량번호
	// * 2. CARD_NO - 카드번호
	// * 3. YD_WBOOK_ID - 상차작업예약ID
	// * 4. YD_CARLD_STOP_LOC - 상차정지위치
	// * 5. SPOS_YD_PNT_CD - 상차정지POINT코드
	// * 6. YD_SCH_CD - 크레인스케줄코드
	// * 7. YD_CRN_ID - 크레인설비ID
	// *
	// * 호출 모듈 :
	// * 1. 코일제품출하차량상차작업요구
	// * 2. 후판제품출하차량상차작업요구
	// */
	// String szMsg = null;
	// String szMethodName = "procDistCarSch";
	// String szOperationName = "출하차량스케줄 수정";
	// //전문 전달 파라미터 항목 시작
	// String szCAR_NO = null;
	// String szCARD_NO = null;
	// String szYD_WBOOK_ID = null;
	// String szYD_CARLD_STOP_LOC = null;
	// String szSPOS_YD_PNT_CD = null;
	// String szYD_SCH_CD = null;
	// String szCurrCrn = null;
	// //전문 전달 파라미터 항목 끝
	// String[] szTC_CODE = null;
	// String szYD_GP = null;
	// String szYD_CAR_SCH_ID = null;
	// JDTORecord recInTemp = null;
	// JDTORecord recCarSchPara = null;
	// JDTORecordSet rsCarSchResult = null;
	// YdCarSchDao ydCarSchDao = new YdCarSchDao();
	// int intRtnVal = -100;
	// try {
	// szMsg = "["+szOperationName+"] 메소드 시작 : 전문확인";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	//
	// ydUtils.displayRecord(szOperationName, msgRecord);
	//
	// szCAR_NO = ydDaoUtils.paraRecChkNull(msgRecord, "CAR_NO");
	// szCARD_NO = ydDaoUtils.paraRecChkNull(msgRecord, "CARD_NO");
	// szYD_WBOOK_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");
	// szYD_CARLD_STOP_LOC = ydDaoUtils.paraRecChkNull(msgRecord,
	// "YD_CARLD_STOP_LOC");
	// szSPOS_YD_PNT_CD = ydDaoUtils.paraRecChkNull(msgRecord,
	// "SPOS_YD_PNT_CD");
	// szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
	// szCurrCrn = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_ID");
	//
	// szYD_GP = szYD_CARLD_STOP_LOC.substring(0, 1);
	// //스케줄 메소드 호출
	// recCarSchPara = JDTORecordFactory.getInstance().create();
	// rsCarSchResult = JDTORecordFactory.getInstance().createRecordSet("");
	// recCarSchPara.setField("CAR_NO" , szCAR_NO);
	// recCarSchPara.setField("CARD_NO", szCARD_NO);
	// intRtnVal = ydCarSchDao.getYdCarsch(recCarSchPara, rsCarSchResult, 11);
	// if(intRtnVal < 0){
	// szMsg = "["+szOperationName+"] 차량스케쥴 조회 에러";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return YdConstant.RETN_CD_FAILURE;
	// }else if(intRtnVal == 0){
	// /*
	// * 수정자 : 임춘수
	// * 수정일 : 2009.09.11
	// */
	// //차량스케줄 생성하는 부분은 제거처리 ----> 상차지시에서 차량스케줄을 먼저 생성하는 걸로 변경
	// //intRtnVal = this.mkY1CarSch(recPara);
	// szMsg = "["+szOperationName+"] 차량스케줄이 존재하지 않습니다 - 차량스케쥴 생성 유무 결정 필요?";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// if( intRtnVal != 1 ) return YdConstant.RETN_CD_FAILURE;
	//
	// }else {
	// /*
	// * 차량스케줄을 조회해서 상차작업예약ID와 차량진행상태 등의 상태값을 변경한다.
	// * 수정자 : 임춘수
	// * 수정일 : 2009.09.11
	// */
	// rsCarSchResult.first();
	// recCarSchPara = rsCarSchResult.getRecord();
	// szYD_CAR_SCH_ID =
	// StringHelper.evl(recCarSchPara.getFieldString("YD_CAR_SCH_ID"), "");
	// szMsg =
	// "["+szOperationName+"] 차량스케쥴["+szYD_CAR_SCH_ID+"] 조회 성공 후 차량도착상태로 변경 시작";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	//
	// recInTemp = JDTORecordFactory.getInstance().create();
	// recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
	// recInTemp.setField("MODIFIER", szMethodName.substring(0, 10));
	// recInTemp.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);
	// recInTemp.setField("YD_CARLD_STOP_LOC", szYD_CARLD_STOP_LOC);
	// recInTemp.setField("YD_CARLD_ARR_DT",
	// YdUtils.getCurDate("yyyyMMddHHmmss"));
	// recInTemp.setField("YD_PNT_CD1" , szSPOS_YD_PNT_CD);
	// recInTemp.setField("YD_CAR_PROG_STAT", "2"); //상차도착상태
	//
	// intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 0);
	//
	// if( intRtnVal == 0 ) {
	// szMsg =
	// "["+szOperationName+"] 차량스케쥴["+szYD_CAR_SCH_ID+"] 조회 성공 후 차량도착상태로 변경 시 차량스케줄이 존재하지 않습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// throw new DAOException(szMsg);
	// }else if( intRtnVal < 0 ) {
	// szMsg =
	// "["+szOperationName+"] 차량스케쥴["+szYD_CAR_SCH_ID+"] 조회 성공 후 차량도착상태로 변경 시 차량스케줄이 존재하지 않습니다.";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// throw new DAOException(szMsg);
	// }
	//
	// szMsg =
	// "["+szOperationName+"] 차량스케쥴["+szYD_CAR_SCH_ID+"] 조회 성공 후 차량도착상태로 변경 완료";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	//
	// /*
	// * 크레인스케줄Main을 호출한다.
	// * 수정자 : 임춘수
	// * 수정일 : 2009.09.11
	// */
	// szTC_CODE = YdCommonUtils.getCrnSchTCByYD(szYD_GP);
	// szMsg =
	// "["+szOperationName+"] 해당야드["+szYD_GP+"]의 크레인스케줄Main["+szTC_CODE[0]+"] 호출 시작";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	// recInTemp = JDTORecordFactory.getInstance().create();
	// //전문코드
	// recInTemp.setField("JMS_TC_CD", szTC_CODE[0]);
	// //스케줄코드
	// recInTemp.setField("YD_SCH_CD", szYD_SCH_CD);
	// //설비ID
	// recInTemp.setField("YD_EQP_ID", szCurrCrn);
	// //작업예약ID
	// recInTemp.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
	//
	// ydUtils.displayRecord(szOperationName, recInTemp);
	//
	// //전문 송신
	// ydDelegate.sendMsg(recInTemp);
	//
	// szMsg =
	// "["+szOperationName+"] 해당야드["+szYD_GP+"]의 크레인스케줄Main["+szTC_CODE[0]+"] 호출 완료";
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	// }
	// }catch(JDTOException ex) {
	// szMsg = "["+szOperationName+"] 오류발생 - 메세지 : " + ex.getMessage();
	// ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	// return YdConstant.RETN_CD_FAILURE;
	// }
	// return YdConstant.RETN_CD_SUCCESS;
	// }
	//
	//
	//
	//
	//

	/**
	 * 오퍼레이션명 : 출하차량상차작업요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String procCCsOutCarLdWrkReq(JDTORecord msgRecord)
			throws JDTOException {

		YdDelegate ydDelegate = new YdDelegate();
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
		String szMethodName = "procCCsOutCarLdWrkReq";
		// 사용자
		String szUser = "SYSTEM";

		// 레코드 선언
		JDTORecord recPara = null;
		JDTORecord recStkPara = null;
		// 레코드셋 선언
		JDTORecordSet rsResult = null;

		// 설비ID
		String szYD_EQP_ID = null;
		// 목표행선구분
		String szYD_AIM_RT_GP = null;
		// 차량사용구분
		String szYD_CAR_USE_GP = "";
		// 운송장비코드
		String szTRN_EQP_CD = "";
		// 재료매수(int)
		int intMtlCnt = 0;
		// 재료번호
		String[] szSTL_NO = null;
		// 권상모음순서
		String[] szYD_UP_COLL_SEQ = null;
		// 스케줄코드
		String szYD_SCH_CD = null;
		// 야드작업크레인우선순위
		String szYD_WRK_CRN_PRIOR = null;
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
		// 개소코드
		String szWLOC_CD = null;
		// 운송작업영공구분코드
		String szSP_TRUCK_LOADING_LOC_TP = null;
		// 포인트요구일시
		String szPNT_DMD_DT = null;
		// 발지개소코드
		String szSPOS_WLOC_CD = null;
		// 발지포인트코드
		String szSPOS_YD_PNT_CD = null;

		// 야드목표야드구분
		String szYD_AIM_YD_GP = null;
		// 야드목표동구분
		String szYD_AIM_BAY_GP = null;
		// 상차정지위치
		String szYD_CARLD_STOP_LOC = null;
		String szTRANS_ORD_DATE = null;
		String szTRANS_ORD_SEQNO = null;

		String szCAR_NO = "";
		String szCARD_NO = "";

		// PIDEV
//		String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "", "APPPI0", "J", "*");		
		
		// TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);

		// 에러 리턴
		if (szRcvTcCode == null) {

			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error ("
					+ szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_TC_ERROR;

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
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}
			// 설비ID
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if (szYD_EQP_ID.equals("")) {

				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}
			// 목표행선구분
			szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_AIM_RT_GP");
			if (szYD_AIM_RT_GP.equals("")) {

				szMsg = "[전문 이상] 목표행선구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}
			// 차량사용구분
			szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_CAR_USE_GP");
			if (szYD_CAR_USE_GP.equals("")) {

				szMsg = "[전문 이상] 차량사용구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}

			if (szYD_CAR_USE_GP.equals("L")) {
				// 운송장비코드
				szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(msgRecord,
						"TRN_EQP_CD");
				if (szTRN_EQP_CD.equals("")) {

					szMsg = "[전문 이상] 운송장비코드가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg,
							YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;
				}

				// 개소코드
				szWLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord, "WLOC_CD");
				if (szWLOC_CD.equals("")) {

					szMsg = "[전문 이상] 개소코드가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg,
							YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;

				}

				// 운송작업영공구분코드
				szSP_TRUCK_LOADING_LOC_TP = ydDaoUtils.paraRecChkNull(
						msgRecord, "SP_TRUCK_LOADING_LOC_TP");
				if (szSP_TRUCK_LOADING_LOC_TP.equals("")) {

					szMsg = "[전문 이상] 운송작업영공구분코드가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg,
							YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;

				}

				// 포인트요구일시
				szPNT_DMD_DT = ydDaoUtils.paraRecChkNull(msgRecord,
						"PNT_DMD_DT");
				if (szPNT_DMD_DT.equals("")) {

					szMsg = "[전문 이상] 포인트요구일시가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg,
							YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;

				}

			}

			// 차량번호
			szCAR_NO = ydDaoUtils.paraRecChkNull(msgRecord, "CAR_NO");
			if (szCAR_NO.equals("")) {

				szMsg = "[전문 이상] 차량번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}
			
//			if("N".equals(sApplyYnPI)) {						
//
//				// 카드번호
//				szCARD_NO = ydDaoUtils.paraRecChkNull(msgRecord, "CARD_NO");
//				
//				if (szCARD_NO.equals("")) {
//	
//					szMsg = "[전문 이상] 카드번호가 없습니다.";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg,
//							YdConstant.ERROR);
//					return YdConstant.RETN_CD_NO_PARAM;
//	
//				}
//
//			}
			
			// 발지개소코드
			szSPOS_WLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord,
					"SPOS_WLOC_CD");
			if (szSPOS_WLOC_CD.equals("")) {

				szMsg = "[전문 이상] 발지개소코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}

			// 발지포인트코드
			szSPOS_YD_PNT_CD = ydDaoUtils.paraRecChkNull(msgRecord,
					"SPOS_YD_PNT_CD");
			if (szSPOS_YD_PNT_CD.equals("")) {

				szMsg = "[전문 이상] 발지포인트코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}

			// 재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_CARLD_SH");
			if (intMtlCnt == 0) {

				szMsg = "[전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}

			// 상차정지위치
			szYD_CARLD_STOP_LOC = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_CARLD_STOP_LOC");
			if (szYD_CARLD_STOP_LOC.equals("")) {

				szMsg = "[전문 이상] 상차정지위치가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}

			// 운송지시일자
			szTRANS_ORD_DATE = ydDaoUtils.paraRecChkNull(msgRecord,
					"TRANS_ORD_DATE");
			if (szTRANS_ORD_DATE.equals("")) {

				szMsg = "[전문 이상] 운송지시일자가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}

			// 운송지시순번
			szTRANS_ORD_SEQNO = ydDaoUtils.paraRecChkNull(msgRecord,
					"TRANS_ORD_SEQNO");
			if (szTRANS_ORD_SEQNO.equals("")) {

				szMsg = "[전문 이상] 운송지시순번이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

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
					ydUtils.putLog(szSessionName, szMethodName, szMsg,
							YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;

				}
				// 권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,
						"YD_UP_COLL_SEQ" + Loop_i);
				if (szYD_UP_COLL_SEQ[Loop_i].equals("")) {

					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i]
							+ ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg,
							YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;

				}
			}

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal)
				return YdConstant.RETN_CD_FAILURE;

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

			// 야드작업크레인우선순위
			// szYD_WRK_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara,
			// "YD_WRK_CRN_PRIOR");

			// 스케줄 금지 유무가 "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if (szYD_SCH_PROH_EXN.equals("Y")) {

				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}

			// 작업크레인 설비 상태 체크
			blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);

			// 작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if (!blnRtnVal) {

				szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);

				// 대체크레인의 유무를 체크한다.
				// 대체크레인이 없으면 에러 리턴
				if (!szYD_ALT_CRN_YN.equals("Y")) {

					szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg,
							YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;

				}
				// 대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				// 대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {

					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg,
							YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;

				} else {
					// 대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					szCrn = szYD_ALT_CRN;
					szYD_WRK_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara,
							"YD_ALT_CRN_PRIOR");
				}
			} else {
				// 작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				szCrn = szYD_WRK_CRN;
				szYD_WRK_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara,
						"YD_WRK_CRN_PRIOR");
			}

			// 재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다.
			// 작업예약재료 등록 여부를 체크한다.
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 크레인사양과 저장품 사양을 체크(길이,폭,중량)
				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
				if (!blnRtnVal)
					return YdConstant.RETN_CD_FAILURE;

				// 다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if (!blnRtnVal)
					return YdConstant.RETN_CD_FAILURE;

			}

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if (!blnRtnVal)
				return YdConstant.RETN_CD_FAILURE;
			// 레코드추출
			rsResult.first();
			recPara = rsResult.getRecord();
			// 작업예약ID
			szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			// 저장품테이블 조회
			blnRtnVal = this.chkGetStock(szSTL_NO[1], rsResult);
			if (!blnRtnVal)
				return YdConstant.RETN_CD_FAILURE;
			// 레코드추출
			rsResult.first();
			recPara = rsResult.getRecord();

			// 야드목표야드구분
			szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_YD_GP");
			// 야드목표동구분
			szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(recPara,
					"YD_AIM_BAY_GP");

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
			recPara.setField("YD_SCH_PRIOR", szYD_WRK_CRN_PRIOR);
			recPara.setField("REGISTER", szUser);
			recPara.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
			recPara.setField("TRN_EQP_CD", szTRN_EQP_CD);
			recPara.setField("CAR_NO", szCAR_NO);
			recPara.setField("CARD_NO", szCARD_NO);
			recPara.setField("YD_AIM_YD_GP", szYD_AIM_YD_GP);
			recPara.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP);
			// 카드번호,차량번호
			recPara.setField("CARD_NO", szCARD_NO);
			recPara.setField("CAR_NO", szCAR_NO);
			// 운송장비코드
			recPara.setField("TRN_EQP_CD", szTRN_EQP_CD);

			// 작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
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
				intRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C",
						rsResult);
				if (intRtnVal != 1)
					return YdConstant.RETN_CD_FAILURE;

				// 레코드추출
				rsResult.first();
				recStkPara = rsResult.getRecord();

				// 재료번호
				recPara.setField("STL_NO", szSTL_NO[Loop_i]);
				// 적치열구분
				recPara.setField("YD_STK_COL_GP",
						ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_COL_GP"));
				// 적치BED번호
				recPara.setField("YD_STK_BED_NO",
						ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_BED_NO"));
				// 적치단번호
				recPara.setField("YD_STK_LYR_NO",
						ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_LYR_NO"));
				// 권상모음순서
				recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ[Loop_i]);

				// 작업예약재료 테이블에 등록한다.
				intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);

				if (intRtnVal < 1) {
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg,
							YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;
				}
			}

			// record 생성
			recPara = JDTORecordFactory.getInstance().create();
			// 설비ID
			recPara.setField("YD_EQP_ID", szYD_EQP_ID);
			// 차량사용구분
			recPara.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
			// 운송장비코드
			recPara.setField("TRN_EQP_CD", szTRN_EQP_CD);
			// 개소코드
			recPara.setField("WLOC_CD", szWLOC_CD);
			// 운송작업영공구분코드
			recPara.setField("SP_TRUCK_LOADING_LOC_TP",
					szSP_TRUCK_LOADING_LOC_TP);
			// 포인트요구일시
			recPara.setField("PNT_DMD_DT", szPNT_DMD_DT);
			// 발지개소코드
			recPara.setField("SPOS_WLOC_CD", szSPOS_WLOC_CD);
			// 발지포인트코드
			recPara.setField("SPOS_YD_PNT_CD", szSPOS_YD_PNT_CD);
			// 작업예약id
			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			// 스케줄코드
			recPara.setField("YD_SCH_CD", szYD_SCH_CD);
			// 크레인설비ID
			recPara.setField("YD_CRN_EQP_ID", szCrn);
			// 차량번호
			recPara.setField("CAR_NO", szCAR_NO);
			// 카드번호
			recPara.setField("CARD_NO", szCARD_NO);
			// 상차정지위치
			recPara.setField("YD_CARLD_STOP_LOC", szYD_CARLD_STOP_LOC);
			// 운송지시일자
			recPara.setField("TRANS_ORD_DATE", szTRANS_ORD_DATE);
			// 운송지시순번
			recPara.setField("TRANS_ORD_SEQNO", szTRANS_ORD_SEQNO);

			// 스케줄 메소드 호출
			intRtnVal = this.mkY5CarSch(recPara);

			if (intRtnVal != 1) {
				return YdConstant.RETN_CD_FAILURE;
			}

			/*
			 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			 * 업무기준 : 출하 공차 도착 시 저장위치 제원 야드L2로 전송 수정자 : 임춘수 수정일자 : 2009.08.24
			 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			 */
			if (!szYD_CAR_USE_GP.equals("L")) { // 출하차량(G)
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_INFO_SYNC_CD", "3"); // 1:동,2:SPAN,3:열,4:BED
				recPara.setField("YD_GP", szYD_EQP_ID.substring(0, 1));
				recPara.setField("YD_STK_COL_GP", szYD_EQP_ID);
				// recPara("YD_STK_BED_NO", szYD_CARLD_STOP_LOC);
				recPara.setField("YD_CAR_PROG_STAT", "2");
				recPara.setField("YD_EQP_WRK_STAT", "U");

				YdCommonUtils.sndStrPosSpecToL2(recPara);
				szMsg = "<procCCsOutCarLdWrkReq>  출하 공차도착 시 저장위치 제원 야드L2로 전송";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.DEBUG);
				/*
				 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				 * +++
				 */
			}
			/*
			 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			 * ++++++++++++++++++++++++++++++++++++++++++++ 출하관리 외판슬라브운송Lot편성실적
			 * 전송 - YDDMR010 업무기준 Desc : 1. 외판슬라브운송Lot편성후 차량상차작업요구 등록 후 기능 추가 :
			 * 임춘수 일자 : 2009.06.16
			 * ++++++++++++++++++++++++++++++++++++++++++++++
			 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			 */
			// if( szYD_GP.equals(YdConstant.YD_GP_C_SLAB_YARD) ){
			// recPara = JDTORecordFactory.getInstance().create();
			// recPara.setField("TC_CODE", "YDDMR010");
			// recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			// ydDelegate.sendMsg(recPara);
			// szMsg = "외판슬라브출하차량상차 작업요구 처리 - 출하관리 외판슬라브운송Lot편성실적 전송 완료 ";
			// ydUtils.putLog(szSessionName, szMethodName, szMsg,
			// YdConstant.DEBUG);
			// }
			/*
			 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			 * ++++++++++++++++++++++++++++++++++++++++++++
			 */

		} catch (Exception e) {
			szMsg = "출하차량상차 작업요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;
		}
		return YdConstant.RETN_CD_SUCCESS;

	} // end of procCCsOutCarLdWrkReq()

	/**
	 * 오퍼레이션명 : 메뉴얼 작업지시 요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void ydManualReq(JDTORecord msgRecord) throws JDTOException {

		// 스케줄기준 DAO
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		// 작업예약 재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		// 저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();

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
		String szMethodName = "ydManualReq";
		// 사용자
		String szUser = ydDaoUtils.paraRecChkNull(msgRecord, "REGISTER");

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
		// 목표야드구분
		String szYD_AIM_YD_GP = null;
		// 목표동구분
		String szYD_AIM_BAY_GP = null;
		// 스케줄우선순위
		String szYD_SCH_PRIOR = null;

		// 야드 구분
		String szYdGp = "";

		try {

			// 받은 전문 편집
			// 스케줄코드
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			if (szYD_SCH_CD.equals("")) {

				szMsg = "[데이터 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;

			}
			// 적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_COL_GP");
			if (szYD_STK_COL_GP.equals("")) {

				szMsg = "[데이터 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;

			}
			// 적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_BED_NO");
			if (szYD_STK_BED_NO.equals("")) {

				szMsg = "[데이터 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;

			}
			// 재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "SLAB_SH");
			if (intMtlCnt == 0) {

				szMsg = "[데이터 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
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
					ydUtils.putLog(szSessionName, szMethodName, szMsg,
							YdConstant.ERROR);
					return;

				}
				// 권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,
						"YD_UP_COLL_SEQ" + Loop_i);
				if (szYD_UP_COLL_SEQ[Loop_i].equals("")) {

					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i]
							+ ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg,
							YdConstant.ERROR);
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

			// 20090325 김진욱
			// 추가////////////////////////////////////////////////////////
			// 스케줄우선순위
			szYD_SCH_PRIOR = ydDaoUtils.paraRecChkNull(recPara,
					"YD_WRK_CRN_PRIOR");
			// //////////////////////////////////////////////////////////////////////////

			// 스케줄 금지 유무가 "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if (szYD_SCH_PROH_EXN.equals("Y")) {

				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;
			}

			// 작업크레인 설비 상태 체크
			blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);

			// 작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if (!blnRtnVal) {

				szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);

				// 대체크레인의 유무를 체크한다.
				// 대체크레인이 없으면 에러 리턴
				if (!szYD_ALT_CRN_YN.equals("Y")) {

					szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg,
							YdConstant.ERROR);
					return;

				}
				// 대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				// 대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {

					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg,
							YdConstant.ERROR);
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

				// 다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if (!blnRtnVal)
					return;

			}

			// 리턴 RecordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 20090325 김진욱
			// 추가//////////////////////////////////////////////////////
			// 저장품테이블 조회
			blnRtnVal = this.chkGetStock(szSTL_NO[1], rsResult);
			if (!blnRtnVal)
				return;

			// 레코드추출
			rsResult.first();

			recPara = rsResult.getRecord();

			// 야드목표야드구분
			szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_YD_GP");

			// 야드목표동구분
			szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(recPara,
					"YD_AIM_BAY_GP");
			// ////////////////////////////////////////////////////////////////////////

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

			// INSERT할 항목 SET (더 추가할항목이 있다고함 // 김진욱에게 재확인할것 )
			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			recPara.setField("YD_GP", szYD_GP);
			recPara.setField("YD_BAY_GP", szYD_BAY_GP);
			recPara.setField("YD_SCH_CD", szYD_SCH_CD);
			recPara.setField("REGISTER", szUser);
			// 20090325 김진욱 추가
			recPara.setField("YD_AIM_YD_GP", szYD_AIM_YD_GP);
			recPara.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP);
			recPara.setField("YD_SCH_PRIOR", szYD_SCH_PRIOR);

			// 20090330 이현성 추가
			// To위치 정보 To Guide 위치 및 To 위치 결정방법 'F': 지정위치 추가

			recPara.setField("YD_TO_LOC_GUIDE",
					ydDaoUtils.paraRecChkNull(msgRecord, "YD_TO_LOC_GUIDE"));
			if (!"".equals(ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_TO_LOC_GUIDE")))
				recPara.setField("YD_TO_LOC_DCSN_MTD", "F");

			// 계획대차
			recPara.setField("YD_WRK_PLAN_TCAR",
					ydDaoUtils.paraRecChkNull(msgRecord, "YD_WRK_PLAN_TCAR"));

			// 작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;
			}

			// 조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();

			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", szUser);

			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 리턴 recordSet 생성
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				// 재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				intRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C",
						rsResult);
				if (intRtnVal != 1)
					return;

				// 레코드추출
				rsResult.first();
				recStkPara = rsResult.getRecord();

				// 재료번호
				recPara.setField("STL_NO", szSTL_NO[Loop_i]);
				// 적치열구분
				recPara.setField("YD_STK_COL_GP",
						ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_COL_GP"));
				// 적치BED번호
				recPara.setField("YD_STK_BED_NO",
						ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_BED_NO"));
				// 적치단번호
				recPara.setField("YD_STK_LYR_NO",
						ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_LYR_NO"));
				// 권상모음순서
				recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ[Loop_i]);

				// 작업예약재료 테이블에 등록한다.
				intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);

				if (intRtnVal < 1) {
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg,
							YdConstant.ERROR);
					return;
				}
			}

			// 대차 작업일 경우는 스케줄 기동을 시키지않는다.
			if ("TC".equals(szYD_SCH_CD.substring(2, 4))) {

				if ("A".equals(szYD_GP)) {

					// 작업크레인 정보를 설비에 넣어준다.
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("JMS_TC_CD", "YDYDJ520");
					recPara.setField("YD_EQP_ID", ydDaoUtils.paraRecChkNull(
							msgRecord, "YD_WRK_PLAN_TCAR"));
					recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
					ydEjbCon.trx("TransEqpSchSeEJB", "procY1TcarSch", recPara);

				}

				return;
			}

			// 작업 예약 편성 후 스케줄 기동

			recPara = JDTORecordFactory.getInstance().create();

			if ("A".equals(szYD_GP)) {
				// C연주 슬라브 야드
				recPara.setField("JMS_TC_CD", "YDYDJ500");

			} else if ("D".equals(szYD_GP)) {
				// A후판 슬라브야드
				recPara.setField("JMS_TC_CD", "YDYDJ503");

			} else if ("K".equals(szYD_GP)) {

				recPara.setField("JMS_TC_CD", "YDYDJ506");
			} else if ("J".equals(szYD_GP)) {

				recPara.setField("JMS_TC_CD", "YDYDJ509");
			} else if ("H".equals(szYD_GP)) {

				recPara.setField("JMS_TC_CD", "YDYDJ509");
			} else if ("S".equals(szYD_GP)) {
				// 통합야드
				recPara.setField("JMS_TC_CD", "YDYDJ512");
			}

			recPara.setField("YD_SCH_CD", szYD_SCH_CD);
			// 작업크레인 정보를 설비에 넣어준다.
			recPara.setField("YD_EQP_ID", szCrn);

			ydDelegate.sendMsg(recPara);

		} catch (Exception e) {
			szMsg = "메뉴얼 작업지시 요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}

	} // end of ydManualReq()

	/**
	 * 오퍼레이션명 : C열연정정 Take-In 요구 (H2YDL004) - 권오창 2010.01.13
	 * 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procCHrShearTakeInReq(JDTORecord msgRecord)
			throws JDTOException {
		// DAO객체 선언
		YdDelegate ydDelegate = new YdDelegate();
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		YdUtils ydutils = new YdUtils();

		// 레코드 선언
		JDTORecord recPara = null;
		JDTORecord recOutPara = null;
		JDTORecordSet rsResult = null;

		String szMethodName = "procCHrShearTakeInReq";
		String szMsg = "";
		String szRcvTcCode = "";

		// TC CODE 추출
		szRcvTcCode = ydUtils.getTcCode(msgRecord);
		if (szRcvTcCode == null || szRcvTcCode.equals("")) {
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
			// HRYDJ008, H2YDL001와 같은 처리가 되어야 하므로 H2YDL004전문도 같은 처리를 태움
			this.procCHrShearInSupLotComp(msgRecord);
		} catch (Exception e) {
			szMsg = "C열연정정 Take-In 요구(H2YDL004) : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
	}

	/**
	 * 오퍼레이션명 : C열연정정 Take-Out 요구 (H2YDL005) - 권오창 2010.01.13
	 * 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procCHrShearTakeOutReq(JDTORecord msgRecord)
			throws JDTOException {
		// DAO객체 선언
		YdDelegate ydDelegate = new YdDelegate();
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		YdUtils ydutils = new YdUtils();

		// 레코드 선언
		JDTORecord recPara = null;
		JDTORecord recOutPara = null;
		JDTORecordSet rsResult = null;

		String szMethodName = "procCHrShearTakeOutReq";
		String szMsg = "";
		String szRcvTcCode = "";

		// TC CODE 추출
		szRcvTcCode = ydUtils.getTcCode(msgRecord);
		if (szRcvTcCode == null || szRcvTcCode.equals("")) {
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
			// 입고작업요구 Session EJB 객체 생성
			CoilRcptWrkDmdSeEJBBean CoilrcptObj = new CoilRcptWrkDmdSeEJBBean();

			// HRYDJ009, H2YDL003과 같은 처리가 되어야 하므로 H2YDL005전문도 같은 처리를 태움
			CoilrcptObj.procR3ShearOutLineOffReq(msgRecord);

		} catch (Exception e) {
			szMsg = "C열연정정 Take-Out 요구(H2YDL005) : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
	}

	/**
	 * 오퍼레이션명 : 코일야드메뉴얼 작업지시 요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void ydManualReqCoil(JDTORecord msgRecord) throws JDTOException {

		// 스케줄기준 DAO
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		// 작업예약 재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		// 저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();

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
		String szMethodName = "ydManualReqCoil";
		// 사용자
		String szUser = ydDaoUtils.paraRecChkNull(msgRecord, "REGISTER");

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
		// 목표야드구분
		String szYD_AIM_YD_GP = null;
		// 목표동구분
		String szYD_AIM_BAY_GP = null;
		// 스케줄우선순위
		String szYD_SCH_PRIOR = null;

		// 야드 구분
		String szYdGp = "";

		try {

			// 받은 전문 편집
			// 스케줄코드
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			if (szYD_SCH_CD.equals("")) {

				szMsg = "[데이터 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;

			}
			// 적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_COL_GP");
			if (szYD_STK_COL_GP.equals("")) {

				szMsg = "[데이터 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;

			}
			// 적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_STK_BED_NO");
			if (szYD_STK_BED_NO.equals("")) {

				szMsg = "[데이터 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;

			}
			// 재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "SLAB_SH");
			if (intMtlCnt == 0) {

				szMsg = "[데이터 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
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
					ydUtils.putLog(szSessionName, szMethodName, szMsg,
							YdConstant.ERROR);
					return;

				}
				// 권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,
						"YD_UP_COLL_SEQ" + Loop_i);
				if (szYD_UP_COLL_SEQ[Loop_i].equals("")) {

					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i]
							+ ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg,
							YdConstant.ERROR);
					return;

				}
			}

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal)
				return;

			ydUtils.putLog(szSessionName, szMethodName, "스케줄 기준 체크 완료",
					YdConstant.ERROR);

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

			// 20090325 김진욱
			// 추가////////////////////////////////////////////////////////
			// 스케줄우선순위
			szYD_SCH_PRIOR = ydDaoUtils.paraRecChkNull(recPara,
					"YD_WRK_CRN_PRIOR");
			// //////////////////////////////////////////////////////////////////////////

			// 스케줄 금지 유무가 "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if (szYD_SCH_PROH_EXN.equals("Y")) {

				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;
			}

			// 작업크레인 설비 상태 체크
			blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);

			ydUtils.putLog(szSessionName, szMethodName, "작업크레인 설비 상태 체크 완료.."
					+ blnRtnVal, YdConstant.ERROR);

			// 작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if (!blnRtnVal) {

				szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);

				// 대체크레인의 유무를 체크한다.
				// 대체크레인이 없으면 에러 리턴
				if (!szYD_ALT_CRN_YN.equals("Y")) {

					szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg,
							YdConstant.ERROR);
					return;

				}
				// 대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				// 대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {

					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg,
							YdConstant.ERROR);
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

				// 다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if (!blnRtnVal)
					return;

			}

			ydUtils.putLog(szSessionName, szMethodName, "작업예약재료 등록 여부를 체크완료..",
					YdConstant.ERROR);

			// 리턴 RecordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 20090325 김진욱
			// 추가//////////////////////////////////////////////////////
			// 저장품테이블 조회
			blnRtnVal = this.chkGetStock(szSTL_NO[1], rsResult);
			if (!blnRtnVal)
				return;

			// 레코드추출
			rsResult.first();

			recPara = rsResult.getRecord();

			// 야드목표야드구분
			szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_YD_GP");

			// 야드목표동구분
			szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(recPara,
					"YD_AIM_BAY_GP");
			// ////////////////////////////////////////////////////////////////////////

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);

			ydUtils.putLog(szSessionName, szMethodName, "작업예약ID 생성완료..",
					YdConstant.ERROR);

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

			// INSERT할 항목 SET (더 추가할항목이 있다고함 // 김진욱에게 재확인할것 )
			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			recPara.setField("YD_GP", szYD_GP);
			recPara.setField("YD_BAY_GP", szYD_BAY_GP);
			recPara.setField("YD_SCH_CD", szYD_SCH_CD);
			recPara.setField("REGISTER", szUser);
			// 20090325 김진욱 추가
			recPara.setField("YD_AIM_YD_GP", szYD_AIM_YD_GP);
			recPara.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP);
			recPara.setField("YD_SCH_PRIOR", szYD_SCH_PRIOR);

			// 20090330 이현성 추가
			// To위치 정보 To Guide 위치 및 To 위치 결정방법 'F': 지정위치 추가
			String sYD_TO_LOC_GUIDE = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_TO_LOC_GUIDE");
			recPara.setField("YD_TO_LOC_GUIDE",
					ydDaoUtils.paraRecChkNull(msgRecord, "YD_TO_LOC_GUIDE"));
			if (!"".equals(ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_TO_LOC_GUIDE")))
				recPara.setField("YD_TO_LOC_DCSN_MTD", "F");

			// 계획대차
			recPara.setField("YD_WRK_PLAN_TCAR",
					ydDaoUtils.paraRecChkNull(msgRecord, "YD_WRK_PLAN_TCAR"));

			// 작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);

			if (intRtnVal < 1) {
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return;
			}

			ydUtils.putLog(szSessionName, szMethodName, "작업예약 INSERT완료..",
					YdConstant.ERROR);

			// 조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();

			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", szUser);

			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {

				// 리턴 recordSet 생성
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				// 재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				intRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C",
						rsResult);
				if (intRtnVal != 1)
					return;

				// 레코드추출
				rsResult.first();
				recStkPara = rsResult.getRecord();

				// 재료번호
				recPara.setField("STL_NO", szSTL_NO[Loop_i]);
				// 적치열구분
				recPara.setField("YD_STK_COL_GP",
						ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_COL_GP"));
				// 적치BED번호
				recPara.setField("YD_STK_BED_NO",
						ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_BED_NO"));
				// 적치단번호
				recPara.setField("YD_STK_LYR_NO",
						ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_LYR_NO"));
				// 권상모음순서
				recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ[Loop_i]);

				// 작업예약재료 테이블에 등록한다.
				intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);

				ydUtils.putLog(szSessionName, szMethodName,
						"작업예약재료 테이블에 등록 완료..", YdConstant.ERROR);

				if (intRtnVal < 1) {
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg,
							YdConstant.ERROR);
					return;
				}
			}

			// 대차 작업일 경우는 스케줄 기동을 시키지않는다.
			if ("TC".equals(szYD_SCH_CD.substring(2, 4))) {

				if ("A".equals(szYD_GP)) {

					// 작업크레인 정보를 설비에 넣어준다.
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("JMS_TC_CD", "YDYDJ520");
					recPara.setField("YD_EQP_ID", ydDaoUtils.paraRecChkNull(
							msgRecord, "YD_WRK_PLAN_TCAR"));
					recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
					ydEjbCon.trx("TransEqpSchSeEJB", "procY1TcarSch", recPara);

				}

				return;
			}

			if (sYD_TO_LOC_GUIDE.trim().length() == 6) {
				// 열단위 작업 예약 편성 후 스케줄 기동 막음 (송)
			} else {

				// 열단위 작업 예약 편성 후 스케줄 기동 막음 (송)

				recPara = JDTORecordFactory.getInstance().create();

				if ("J".equals(szYD_GP)) {
					recPara.setField("JMS_TC_CD", "YDYDJ509"); // rcvY5CrnSchMain
				} else if ("H".equals(szYD_GP)) {
					recPara.setField("JMS_TC_CD", "YDYDJ509"); // rcvY5CrnSchMain
				}

				recPara.setField("YD_SCH_CD", szYD_SCH_CD);
				// 작업크레인 정보를 설비에 넣어준다.
				recPara.setField("YD_EQP_ID", szCrn);

				ydUtils.putLog(szSessionName, szMethodName,
						"작업 예약 편성 후 스케줄 기동  완료..JMS_TC_CD-->YDYDJ509",
						YdConstant.ERROR);

				ydDelegate.sendMsg(recPara);
			}

		} catch (Exception e) {
			szMsg = "메뉴얼 작업지시 요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}

	} // end of ydManualReq()

	/**
	 * 오퍼레이션명 : 출하상차 LOT 편성 데이터 유무체크 및 데이터 반환
	 * 
	 * @param String
	 *            szSchCd 스케줄코드 JDTORecordSet rsResult 결과레코드셋
	 * @return boolean true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkDistCarLoadLotGp(JDTORecord recInPara,
			JDTORecordSet rsResult) throws JDTOException {

		// 저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메소드명
		String szMethodName = "chkGetOutPlDistCarLoadLotGp";
		String szMsg = null;
		String szYD_STK_COL_GP = null;

		// 레코드 선언
		JDTORecord recPara = null;

		try {
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recInPara,
					"YD_CARLD_STOP_LOC");
			// 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();

			// 카드번호
			recPara.setField("CARD_NO",
					ydDaoUtils.paraRecChkNull(recInPara, "CARD_NO"));

			// 운송지시일자
			recPara.setField("TRANS_ORD_DATE",
					ydDaoUtils.paraRecChkNull(recInPara, "TRANS_ORD_DATE"));

			// 운송지시순번
			recPara.setField("TRANS_ORD_SEQNO",
					ydDaoUtils.paraRecChkNull(recInPara, "TRANS_ORD_SEQNO"));

			// 야드동
			recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP.substring(0, 2));

			// 저장품 테이블 조회
			// intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 38);
			if (szYD_STK_COL_GP.substring(0, 1).equals(
					YdConstant.YD_GP_C_SLAB_YARD) /* C연주슬라브야드 */
					|| szYD_STK_COL_GP.substring(0, 1).equals(
							YdConstant.YD_GP_C_HR_COIL_GDS_YARD) /* C열연코일제품야드 */
			) {
				// 적치열 Desc, 적치베드Asc, 적치단 Desc
//PIDEV_S :병행가동용:PI_YD
				recPara.setField("PI_YD",    	szYD_STK_COL_GP.substring(0, 1));					
				intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 126);
			} else {
//PIDEV_S :병행가동용:PI_YD
				recPara.setField("PI_YD",    	szYD_STK_COL_GP.substring(0, 1));						
				// 적치열 Asc, 적치베드Asc, 적치단 Desc
				intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 128);
			}

			// 리턴값 메세지처리
			if (intRtnVal >= 1) {

				blnRtnVal = true;

			} else if (intRtnVal == 0) {

				szMsg = "출하 상차 LOT 편성 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == -2) {

				szMsg = "출하 상차 LOT 편성 데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = "출하 상차 LOT 편성 데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch (Exception e) {
			szMsg = "출하 상차 LOT 편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: "
					+ e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} // end of chkGetOutPlDistCarLoadLotGp

	/**
	 * 오퍼레이션명 : 소재이송 상차 LOT 편성 데이터 유무체크 및 데이터 반환
	 * 
	 * @param String
	 *            szSchCd 스케줄코드 JDTORecordSet rsResult 결과레코드셋
	 * @return boolean true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetMtlFtmvCarLoadLotGp(JDTORecord recInPara,
			JDTORecordSet rsResult) throws JDTOException {

		// 저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메소드명
		String szMethodName = "chkGetMtlFtmvCarLoadLotGp";
		String szMsg = null;

		// 레코드 선언
		JDTORecord recPara = null;
		JDTORecordSet rsLotStock = null;

		try {
			// 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();

			// 재료품목
			recPara.setField("YD_MTL_ITEM",
					ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_ITEM"));
			// 목표행선구분
			recPara.setField("YD_AIM_RT_GP",
					ydDaoUtils.paraRecChkNull(recInPara, "YD_AIM_RT_GP"));
			// 목표야드구분
			recPara.setField("YD_AIM_YD_GP",
					ydDaoUtils.paraRecChkNull(recInPara, "YD_AIM_YD_GP"));
			// 목표동구분
			recPara.setField("YD_AIM_BAY_GP",
					ydDaoUtils.paraRecChkNull(recInPara, "YD_AIM_BAY_GP"));
			// FROM 야드동
			recPara.setField("YD_STK_COL_GP",
					ydDaoUtils.paraRecChkNull(recInPara, "YD_CARLD_STOP_LOC")
							.substring(0, 2));

			// 저장품 테이블 조회
			rsLotStock = JDTORecordFactory.getInstance().createRecordSet("");
			intRtnVal = ydStockDao.getYdStock(recPara, rsLotStock, 17);

			// 20090315 김진욱 로그 및 레코드셋 addAll 추가
			szMsg = "대차 상차 Lot편성 후 재료의 매수 : " + rsLotStock.size();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			rsResult.addAll(rsLotStock);

			// 리턴값 메세지처리
			if (intRtnVal >= 1) {

				blnRtnVal = true;

			} else if (intRtnVal == 0) {

				szMsg = "C연주 소재이송 상차 LOT 편성 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == -2) {

				szMsg = "C연주 소재이송 상차 LOT 편성 데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = "C연주 소재이송 상차 LOT 편성 데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch (Exception e) {
			szMsg = "C연주 소재이송 상차 LOT 편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: "
					+ e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} // end of chkGetMtlFtmvCarLoadLotGp

	/**
	 * 오퍼레이션명 : 코일HYSCO출하상차Lot편성(YDYDJ300)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String procCoilHYSCOCarLdComp(JDTORecord msgRecord)
			throws JDTOException {

		YdDelegate ydDelegate = new YdDelegate();
		// 공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		YdStockDao ydStockDao = new YdStockDao();
		// 레코드 선언recInTemp
		JDTORecord recPara = null;
		JDTORecord recInTemp = null;
		JDTORecord recOutPara = null;
		// 레코드셋 선언
		JDTORecordSet rsResult = null;

		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 메세지
		String szMsg = "";
		// 메소드명
		String szMethodName = "procCoilHYSCOCarLdComp";
		String szOperationName = "코일HYSCO출하상차Lot편성(YDYDJ300)";

		// 전문 생성 일시
		String szDate = "";

		// 설비ID
		String szYD_EQP_ID = "";
		// 상차정지위치
		String szYD_CARLD_STOP_LOC = "";
		// 차량사용구분
		String szYD_CAR_USE_GP = "";
		// 차량번호
		String szCAR_NO = "";
		// 카드번호
		String szCARD_NO = "";
		// 재료품목
		// String szYD_MTL_ITEM = "";
		// 스케줄코드
		String szYD_SCH_CD = "";
		// 차량작업허용매수
		int intYD_WRK_ALW_SH = 0;
		String szTRANS_ORD_DATE = "";
		String szTRANS_ORD_SEQNO = "";
		String szYD_GP = "";
		String szYD_BAY_GP = "";
		String szSPOS_WLOC_CD = "";
		String szSPOS_YD_PNT_CD = "";
		int intRtnVal = 0;

		String szRtnMsg = null;

		// PIDEV
//		String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "", "APPPI0", "J", "*");			
		
		// TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		// 에러 리턴
		if (szRcvTcCode == null || szRcvTcCode.equals("")) {
			szMsg = "[" + szOperationName + "] TC Code Error (" + szRcvTcCode
					+ ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_TC_ERROR;
		}

		// TC CODE DISPLAY
		if (bDebugFlag) {
			szMsg = "[" + szOperationName + "] 전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}

		try {
			szMsg = "[" + szOperationName + "] 메소드 시작 : 전문내용 확인 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			ydUtils.displayRecord(szOperationName, msgRecord);

			// 상차정지위치
			szYD_CARLD_STOP_LOC = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CARLD_STOP_LOC");
			
			if (szYD_CARLD_STOP_LOC.equals("")) {

				szMsg = "[" + szOperationName + "] [전문 이상] 상차정지위치가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}
			// 차량사용구분
			szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_CAR_USE_GP");
			if (szYD_CAR_USE_GP.equals("")) {

				szMsg = "[" + szOperationName + "] [전문 이상] 차량사용구분("
						+ szYD_CAR_USE_GP + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}
			// 차량번호
			szCAR_NO = ydDaoUtils.paraRecChkNull(msgRecord, "CAR_NO");
			if (szCAR_NO.equals("")) {

				szMsg = "[" + szOperationName + "] [전문 이상] 차량번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}
			
			// PIDEV
//			if("N".equals(sApplyYnPI)) {						
//
//				// 카드번호
//				szCARD_NO = ydDaoUtils.paraRecChkNull(msgRecord, "CARD_NO");				
//				
//				if (szCARD_NO.equals("")) {
//	
//					szMsg = "[" + szOperationName + "] [전문 이상] 카드번호가 없습니다.";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg,
//							YdConstant.ERROR);
//					return YdConstant.RETN_CD_NO_PARAM;
//	
//				}
//			
//			}
			
			
			// 운송지시일자
			szTRANS_ORD_DATE = ydDaoUtils.paraRecChkNull(msgRecord,
					"TRANS_ORD_DATE");
			if (szTRANS_ORD_DATE.equals("")) {
				szMsg = "[" + szOperationName + "] [전문 이상] 운송지시일자 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}

			// 운송지시순번
			szTRANS_ORD_SEQNO = ydDaoUtils.paraRecChkNull(msgRecord,
					"TRANS_ORD_SEQNO");
			if (szTRANS_ORD_SEQNO.equals("")) {
				szMsg = "[" + szOperationName + "] [전문 이상] 운송지시순번 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}

			// 야드구분
			szYD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP");
			if (szYD_GP.equals("")) {
				szMsg = "[전문 이상] 야드구분 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}

			// 동구분
			szYD_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP");
			if (szYD_BAY_GP.equals("")) {
				szMsg = "[전문 이상] 동구분 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}

			// 발지개소코드
			szSPOS_WLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord,
					"SPOS_WLOC_CD");
			if (szSPOS_WLOC_CD.equals("")) {
				szMsg = "[전문 이상] 발지개소코드 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}

			// 발지야드포인트코드
			szSPOS_YD_PNT_CD = ydDaoUtils.paraRecChkNull(msgRecord,
					"SPOS_YD_PNT_CD");
			if (szSPOS_YD_PNT_CD.equals("")) {
				szMsg = "[전문 이상] 발지야드포인트코드 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}

			// =================================================================================
			// 수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			// BRE 등록 안됨...테스트용 스케줄코드 생성
			//szYD_SCH_CD = szYD_CARLD_STOP_LOC.substring(0, 4) + "03UM";
			//szYD_SCH_CD = szYD_CARLD_STOP_LOC.substring(0, 4) + "02UM";
			
			if(szYD_CARLD_STOP_LOC.substring(1, 2).equals("B")||szYD_CARLD_STOP_LOC.substring(1, 2).equals("C")){
				if("04".equals(szYD_CARLD_STOP_LOC.substring(4, 6))||"05".equals(szYD_CARLD_STOP_LOC.substring(4, 6))){
					szYD_SCH_CD = szYD_CARLD_STOP_LOC.substring(0, 4) + "52UM"; //제품이송(2통로)
				} else {
					szYD_SCH_CD = szYD_CARLD_STOP_LOC.substring(0, 4) + "02UM";  //제품이송(1통로)
				}
			} else{
				szYD_SCH_CD = szYD_CARLD_STOP_LOC.substring(0, 4) + "02UM";  //제품이송(1통로)
			}
			// =================================================================================

			//szMsg = "코일HYSCO출하상차Lot편성 스케쥴!" + szYD_SCH_CD;
			szMsg = "코일공냉재 출하상차Lot편성 스케쥴!" + szYD_SCH_CD;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 상차 Lot 편성 대상 재료 Select
			// blnRtnVal = chkDistCarLoadLotGp(msgRecord, rsResult);
			// if(!blnRtnVal) return YdConstant.RETN_CD_FAILURE;

			// intYD_WRK_ALW_SH = rsResult.size();

			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_STK_COL_GP", "");
			recInTemp.setField("CARD_NO", szCARD_NO);
			recInTemp.setField("TRANS_ORD_DATE", szTRANS_ORD_DATE);
			recInTemp.setField("TRANS_ORD_SEQNO", szTRANS_ORD_SEQNO);
			// 조회조건 : CARD_NO, TRANS_ORD_DATE, TRANS_ORD_SEQNO,
			// YD_STK_COL_GP(LIKE) --> 코일제품창고의 차량진입순서 : J==>D순으로 진입
			// 정렬순서 : 적치열 Desc, 적치베드 Asc, 적치단 Desc
			/*
			 * com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.
			 * getYdStockYdStkLyrMtlCTransDTSeqCardNoDesc
			 */
//PIDEV_S :병행가동용:PI_YD
			recInTemp.setField("PI_YD",    	szYD_GP);					
			intRtnVal = ydStockDao.getYdStock(recInTemp, rsResult, 126);

			intYD_WRK_ALW_SH = intRtnVal;
			// 레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			// JMS TC CODE
			recOutPara.setField("JMS_TC_CD", "YDYDJ301");

			for (int Loop_i = 1; Loop_i <= intYD_WRK_ALW_SH; Loop_i++) {
				rsResult.absolute(Loop_i);
				recPara = rsResult.getRecord();
				// 재료번호
				recOutPara.setField("STL_NO" + Loop_i,
						ydDaoUtils.paraRecChkNull(recPara, "STL_NO"));
				recOutPara.setField("YD_STK_COL_GP" + Loop_i,
						ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP"));
				recOutPara.setField("YD_STK_BED_NO" + Loop_i,
						ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO"));
				recOutPara.setField("YD_STK_LYR_NO" + Loop_i,
						ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO"));
				// 권상모음순서
				recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, "" + Loop_i);

			}

			szYD_EQP_ID = YdConstant.YD_DM_CAR_EQP_ID;
			szDate = YdUtils.getCurDate("yyyy/MM/dd HH:mm:ss");
			recOutPara.setField("JMS_TC_CREATE_DDTT", szDate);
			recOutPara.setField("YD_SCH_CD", szYD_SCH_CD);
			recOutPara.setField("YD_CARLD_SH", "" + intYD_WRK_ALW_SH);
			recOutPara.setField("YD_EQP_ID", szYD_EQP_ID);
			recOutPara.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
			recOutPara.setField("YD_CARLD_STOP_LOC", szYD_CARLD_STOP_LOC);
			recOutPara.setField("CAR_NO", szCAR_NO);
			recOutPara.setField("CARD_NO", szCARD_NO);
			recOutPara.setField("TRANS_ORD_DATE", szTRANS_ORD_DATE);
			recOutPara.setField("TRANS_ORD_SEQNO", szTRANS_ORD_SEQNO);
			recOutPara.setField("SPOS_WLOC_CD", szSPOS_WLOC_CD);
			recOutPara.setField("SPOS_YD_PNT_CD", szSPOS_YD_PNT_CD);

			szMsg = "코일HYSCO출하상차Lot편성 후 차량상차 작업요구 송신 시작 - 전문내용 표시";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			ydUtils.displayRecord(szOperationName, recOutPara);
			ydDelegate.sendMsg(recOutPara);

			// szRtnMsg = this.procCoilGdsDistCarLdWrkReq(recOutPara);
			szMsg = "코일HYSCO출하상차Lot편성 후 차량상차 작업요구 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		} catch (Exception e) {
			szMsg = "코일HYSCO출하상차Lot편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;
		}
		return szRtnMsg;
	} // end of procCoilHYSCOCarLdComp

	/**
	 * 오퍼레이션명 : 코일HYSCO출하차량상차작업요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String procCoilHYSCOCarLdWrkReq(JDTORecord msgRecord)
			throws JDTOException {

		YdDelegate ydDelegate = new YdDelegate();
		// 스케줄기준 DAO
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		// 작업예약 재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		// 차량스케줄DAO
		YdCarSchDao ydCarSchDao = new YdCarSchDao();
		// 공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();

		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메세지
		String szMsg = "";
		// METHOD명
		String szMethodName = "procCoilHYSCOCarLdWrkReq";
		String szOperationName = "코일HYSCO출하차량상차작업요구";
		// 사용자
		String szUser = "SYSTEM";

		// 레코드 선언
		JDTORecord recPara = null;
		JDTORecord recInTemp = null;
		// 레코드셋 선언
		JDTORecordSet rsResult = null;
		JDTORecordSet rsTemp = null;

		// 설비ID
		String szYD_EQP_ID = null;
		// 차량사용구분
		String szYD_CAR_USE_GP = "";
		// 재료매수(int)
		int intMtlCnt = 0;
		// 재료번호
		String szSTL_NO = null;
		// 권상모음순서
		String szYD_UP_COLL_SEQ = null;
		// 스케줄코드
		String szYD_SCH_CD = null;
		String szCURR_YD_SCH_CD = null;
		// 야드작업크레인우선순위
		String szYD_SCH_PRIOR = null;
		// 선택크레인
		String szCrn = null;
		String szCurrCrn = null;
		// 작업예약ID
		String szYD_WBOOK_ID = null;
		String szCURR_YD_WBOOK_ID = null;
		// 발지개소코드
		String szSPOS_WLOC_CD = null;
		// 발지포인트코드
		String szSPOS_YD_PNT_CD = null;
		// 적치열구분
		String szYD_STK_COL_GP = null;
		String szPREV_YD_STK_COL_GP = null;
		String szYD_STK_BED_NO = null;
		String szYD_STK_LYR_NO = null;
		// 야드구분
		String szYD_GP = null;
		// 야드동구분
		String szYD_BAY_GP = null;
		// 야드목표야드구분
		String szYD_AIM_YD_GP = null;
		// 야드목표동구분
		String szYD_AIM_BAY_GP = null;
		// 차량스케줄ID
		String szYD_CAR_SCH_ID = null;
		// 상차정지위치
		String szYD_CARLD_STOP_LOC = null;
		String szTRANS_ORD_DATE = null;
		String szTRANS_ORD_SEQNO = null;
		String szCAR_NO = "";
		String szCARD_NO = "";
		Vector vGroup = new Vector();
		String[] szTC_CODE = null;

		// PIDEV
//		String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "", "APPPI0", "J", "*");					
		
		// TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);

		// 에러 리턴
		if (szRcvTcCode == null || szRcvTcCode.equals("")) {

			szMsg = "[" + szOperationName + "] TC Code Error (" + szRcvTcCode
					+ ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_TC_ERROR;

		}
		// TC CODE DISPLAY
		if (bDebugFlag) {
			szMsg = "[" + szOperationName + "] 전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}

		try {
			szMsg = "[" + szOperationName + "] 메소드 시작 : 전문내용확인 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			ydUtils.displayRecord(szOperationName, msgRecord);

			// 스케줄코드
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			if (szYD_SCH_CD.equals("")) {

				szMsg = "[" + szOperationName + "] [전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}
			// 설비ID
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if (szYD_EQP_ID.equals("")) {

				szMsg = "[" + szOperationName + "] [전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}

			// 차량사용구분
			szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_CAR_USE_GP");
			if (szYD_CAR_USE_GP.equals("")) {

				szMsg = "[" + szOperationName + "] [전문 이상] 차량사용구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}

			// 차량번호
			szCAR_NO = ydDaoUtils.paraRecChkNull(msgRecord, "CAR_NO");
			if (szCAR_NO.equals("")) {

				szMsg = "[" + szOperationName + "] [전문 이상] 차량번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}
			
			// PIDEV
//			if("N".equals(sApplyYnPI)) {						
//			
//				// 카드번호
//				szCARD_NO = ydDaoUtils.paraRecChkNull(msgRecord, "CARD_NO");				
//				
//				if (szCARD_NO.equals("")) {
//	
//					szMsg = "[" + szOperationName + "] [전문 이상] 카드번호가 없습니다.";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg,
//							YdConstant.ERROR);
//					return YdConstant.RETN_CD_NO_PARAM;
//	
//				}
//			
//			}
			
			// 발지개소코드
			szSPOS_WLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord,
					"SPOS_WLOC_CD");
			if (szSPOS_WLOC_CD.equals("")) {

				szMsg = "[" + szOperationName + "] [전문 이상] 발지개소코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}

			// 발지포인트코드
			szSPOS_YD_PNT_CD = ydDaoUtils.paraRecChkNull(msgRecord,
					"SPOS_YD_PNT_CD");
			if (szSPOS_YD_PNT_CD.equals("")) {

				szMsg = "[" + szOperationName + "] [전문 이상] 발지포인트코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}

			// 재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_CARLD_SH");
			if (intMtlCnt == 0) {

				szMsg = "[" + szOperationName + "] [전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}

			// 상차정지위치
			szYD_CARLD_STOP_LOC = ydDaoUtils.paraRecChkNull(msgRecord,
					"YD_CARLD_STOP_LOC");
			if (szYD_CARLD_STOP_LOC.equals("")) {

				szMsg = "[" + szOperationName + "] [전문 이상] 상차정지위치가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}

			// 운송지시일자
			szTRANS_ORD_DATE = ydDaoUtils.paraRecChkNull(msgRecord,
					"TRANS_ORD_DATE");
			if (szTRANS_ORD_DATE.equals("")) {

				szMsg = "[" + szOperationName + "] [전문 이상] 운송지시일자가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}

			// 운송지시순번
			szTRANS_ORD_SEQNO = ydDaoUtils.paraRecChkNull(msgRecord,
					"TRANS_ORD_SEQNO");
			if (szTRANS_ORD_SEQNO.equals("")) {

				szMsg = "[" + szOperationName + "] [전문 이상] 운송지시순번이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;

			}

			int seq = 0;
			// 재료번호, 권상모음순서
			for (int i = 0; i < intMtlCnt; i++) {
				// 재료번호
				szSTL_NO = ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO"
						+ (i + 1));
				if (szSTL_NO.equals("")) {
					szMsg = "[" + szOperationName + "] [전문 이상] " + i
							+ "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg,
							YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;
				}

				szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord,
						"YD_STK_COL_GP" + (i + 1));
				if (i == 0) {
					seq = 1;
					szPREV_YD_STK_COL_GP = szYD_STK_COL_GP;
					rsResult = JDTORecordFactory.getInstance().createRecordSet(
							"");
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("STL_NO", szSTL_NO);
					recInTemp.setField("YD_UP_COLL_SEQ", "" + seq);
					recInTemp.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
					recInTemp.setField("YD_STK_BED_NO", ydDaoUtils
							.paraRecChkNull(msgRecord, "YD_STK_BED_NO"
									+ (i + 1)));
					recInTemp.setField("YD_STK_LYR_NO", ydDaoUtils
							.paraRecChkNull(msgRecord, "YD_STK_LYR_NO"
									+ (i + 1)));
					rsResult.addRecord(recInTemp);
					vGroup.add(rsResult);
				} else {
					if (!szYD_STK_COL_GP.substring(0, 2).equals(
							szPREV_YD_STK_COL_GP.substring(0, 2))) {
						rsResult = JDTORecordFactory.getInstance()
								.createRecordSet("");
						vGroup.add(rsResult);
						seq = 1;
					}
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("STL_NO", szSTL_NO);
					recInTemp.setField("YD_UP_COLL_SEQ", "" + seq);
					recInTemp.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
					recInTemp.setField("YD_STK_BED_NO", ydDaoUtils
							.paraRecChkNull(msgRecord, "YD_STK_BED_NO"
									+ (i + 1)));
					recInTemp.setField("YD_STK_LYR_NO", ydDaoUtils
							.paraRecChkNull(msgRecord, "YD_STK_LYR_NO"
									+ (i + 1)));
					rsResult.addRecord(recInTemp);
					szPREV_YD_STK_COL_GP = szYD_STK_COL_GP;
				}
				seq++;
			}

			szMsg = "[" + szOperationName + "] 전문확인 후 출하상차LOT 그룹 수 : "
					+ vGroup.size();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			for (int i = 0; i < vGroup.size(); i++) {
				rsTemp = (JDTORecordSet) vGroup.get(i);
				for (int j = 1; j <= rsTemp.size(); j++) {
					rsTemp.absolute(j);
					recInTemp = rsTemp.getRecord();
					szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recInTemp,
							"YD_STK_COL_GP");
					szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recInTemp,
							"YD_STK_BED_NO");
					szYD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(recInTemp,
							"YD_STK_LYR_NO");
					szYD_UP_COLL_SEQ = ydDaoUtils.paraRecChkNull(recInTemp,
							"YD_UP_COLL_SEQ");
					szSTL_NO = ydDaoUtils.paraRecChkNull(recInTemp, "STL_NO");
					if (j == 1) {

						// sjh szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) +
						// szYD_SCH_CD.substring(2);
						recPara = JDTORecordFactory.getInstance().create();
						intRtnVal = YdCommonUtils.getCrnInfoByCrnSchRule(
								szYD_SCH_CD, recPara);
						if (intRtnVal < 0) {
							return YdConstant.RETN_CD_FAILURE;
						}
						// 야드작업크레인
						szCrn = ydDaoUtils
								.paraRecChkNull(recPara, "YD_WRK_CRN");
						// 야드스케줄우선순위
						szYD_SCH_PRIOR = ydDaoUtils.paraRecChkNull(recPara,
								"YD_SCH_PRIOR");

						// 리턴 recordSet 생성
						rsResult = JDTORecordFactory.getInstance()
								.createRecordSet("");

						// 작업예약ID 생성
						blnRtnVal = getYdWbookId(rsResult);
						if (!blnRtnVal)
							return YdConstant.RETN_CD_FAILURE;

						// 레코드추출
						rsResult.first();
						recPara = rsResult.getRecord();

						// 작업예약ID
						szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");

						// 코일HYSCO출하차량은 동만 checkgka
						if (szYD_CARLD_STOP_LOC.substring(1, 2).equals(
								szYD_STK_COL_GP.substring(1, 2))) {
							// 현재작업예약이 등록되는 동과 차량정지위치가 같은 스케줄코드와
							szCURR_YD_SCH_CD = szYD_SCH_CD;
							szCURR_YD_WBOOK_ID = szYD_WBOOK_ID;
							szCurrCrn = szCrn;
						}

						// 리턴 RecordSet 생성
						rsResult = JDTORecordFactory.getInstance()
								.createRecordSet("");

						// 저장품테이블 조회
						blnRtnVal = this.chkGetStock(szSTL_NO, rsResult);
						if (!blnRtnVal)
							return YdConstant.RETN_CD_FAILURE;

						// 레코드추출
						rsResult.first();
						recPara = rsResult.getRecord();

						// 야드목표야드구분
						szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(recPara,
								"YD_AIM_YD_GP");

						// 야드목표동구분
						szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(recPara,
								"YD_AIM_BAY_GP");

						// INSERT 항목 record 생성
						recPara = JDTORecordFactory.getInstance().create();

						// 야드구분
						szYD_GP = szYD_SCH_CD.substring(0, 1);

						// 동구분
						szYD_BAY_GP = szYD_SCH_CD.substring(1, 2);

						// INSERT할 항목 SET
						recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
						recPara.setField("YD_GP", szYD_GP);
						recPara.setField("YD_BAY_GP", szYD_BAY_GP);
						recPara.setField("YD_SCH_CD", szYD_SCH_CD);
						recPara.setField("YD_SCH_PRIOR", szYD_SCH_PRIOR);
						recPara.setField("REGISTER", szUser);
						recPara.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
						recPara.setField("CAR_NO", szCAR_NO);
						recPara.setField("CARD_NO", szCARD_NO);
						recPara.setField("YD_AIM_YD_GP", szYD_AIM_YD_GP);
						recPara.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP);
						// 작업예약 INSERT
						intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
						if (intRtnVal < 1) {
							szMsg = "[" + szOperationName
									+ "] 작업예약 데이터 등록 중 에러";
							ydUtils.putLog(szSessionName, szMethodName, szMsg,
									YdConstant.ERROR);
							return YdConstant.RETN_CD_FAILURE;
						}
					}

					// recStk = JDTORecordFactory.getInstance().create();

					// 조회항목 record 생성
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
					recPara.setField("REGISTER", szUser);
					recPara.setField("STL_NO", szSTL_NO);
					recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
					recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
					recPara.setField("YD_STK_LYR_NO", szYD_STK_LYR_NO);
					recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ);

					// 작업예약재료 테이블에 등록한다.
					intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
					if (intRtnVal < 1) {
						szMsg = "[" + szOperationName + "] 작업예약재료 데이터 등록 중 에러";
						ydUtils.putLog(szSessionName, szMethodName, szMsg,
								YdConstant.ERROR);
						// 예외를 발생시켜 롤백 시킴
						throw new DAOException(szSessionName + " : "
								+ szMethodName + " - " + szMsg);
					}
				}
			}

			/*
			 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			 * 업무기준 : 출하 공차 도착 시 저장위치 제원 야드L2로 전송 수정자 : 임춘수 수정일자 : 2009.08.24
			 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			 */
			if (!szYD_CAR_USE_GP.equals("L")) { // 출하차량(G)
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_INFO_SYNC_CD", "3"); // 1:동,2:SPAN,3:열,4:BED
				recPara.setField("YD_GP", szYD_CARLD_STOP_LOC.substring(0, 1));
				recPara.setField("YD_STK_COL_GP", szYD_CARLD_STOP_LOC);
				recPara.setField("YD_CAR_PROG_STAT", "2");
				recPara.setField("YD_EQP_WRK_STAT", "U");

				YdCommonUtils.sndStrPosSpecToL2(recPara);
				szMsg = "[" + szOperationName
						+ "] HYSCO출하 공차도착 시 저장위치 제원 야드L2로 전송";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,
						YdConstant.DEBUG);
				/*
				 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				 * +++
				 */
			}

			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("JMS_TC_CD", "YDYDJ293");
			recInTemp.setField("CAR_NO", szCAR_NO);
			recInTemp.setField("CARD_NO", szCARD_NO);
			recInTemp.setField("YD_WBOOK_ID", szCURR_YD_WBOOK_ID);
			recInTemp.setField("YD_CARLD_STOP_LOC", szYD_CARLD_STOP_LOC);
			recInTemp.setField("SPOS_YD_PNT_CD", szSPOS_YD_PNT_CD);
			recInTemp.setField("YD_SCH_CD", szCURR_YD_SCH_CD);
			recInTemp.setField("YD_CRN_ID", szCurrCrn);

			szMsg = "[" + szOperationName
					+ "] HYSCO출하차량스케줄 수정 전문 전송 시작 - 전문내용확인";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			ydUtils.displayRecord(szOperationName, recInTemp);

			ydDelegate.sendMsg(recInTemp);
			szMsg = "[" + szOperationName + "] HYSCO출하차량스케줄 수정 전문 전송 성공";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		} catch (Exception e) {
			szMsg = "[" + szOperationName + "] HYSCO출하차량상차 작업요구 처리중 Error : "
					+ e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;
		}
		return YdConstant.RETN_CD_SUCCESS;

	} // end of procCoilHYSCOCarLdWrkReq()

	// ---------------------------------------------------------------------------
} // end of class IssueWrkDmdSeEJBBean
