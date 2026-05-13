package com.inisteel.cim.yd.common.dao.ydPrepSchDao;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;
import com.inisteel.cim.yd.ccommon.util.CCommUtils;
    
/**
 *      [A] 클래스명 : 야드준비스케줄 DAO
 * 
*/

public class YdPrepSchDao {
	// PIDEV
	private YdPICommDAO ydPICommDAO = new YdPICommDAO();
	private CCommUtils commUtils = new CCommUtils();
	
	// Dao Name
	private String szDaoName = getClass().getName();
	
	private YdUtils ydUtils = new YdUtils();	

	private DBAssistantDAO dbAssDao = new DBAssistantDAO();
	
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();
	//고유키를 생성하는 쿼리 - 임춘수 2009.09.28
	private String szQueryIdKey = "com.inisteel.cim.yd.dao.ydprepschdao.YdPrepschDao.getYdPrepschId";
	
	//select query id
	// 수정 - 20090929 이현성(테이블 컬럼변경)
	private String szQueryIdGet1 = "com.inisteel.cim.yd.dao.ydprepschdao.YdPrepschDao.getYdPrepsch";
	//후판제품야드 선별작업 SIMULATION 결과 쿼리 (김종건 20090421)_수정(이현성 20090929 컬럼명변경)
	private String szQueryIdGet2 = "com.inisteel.cim.yd.dao.ydprepschdao.YdPrepschDao.getYdPrepschSortWrkSimRlt_PIDEV";
	//코일제품야드 이적작업진행관리 (김창일 20090423) - 이쿼리는 사용할수 없음 (재개발되어야함)
	//!A 이적작업진행 관리 > 이적작업진행관리 > 작업진행 조회  - 박지열 2010/03/22 수정
	private String szQueryIdGet3 = "com.inisteel.cim.yd.dao.ydprepschdao.YdPrepschDao.getCoilYdGdsMvWorkList";	
	
	//이적작업진행관리 - 동내이적(김창일 20090423) - 이쿼리는 사용할수 없음 (재개발되어야함 -> szQueryIdGet401로 변경)
	private String szQueryIdGet4 = "com.inisteel.cim.yd.dao.ydprepschdao.YdPrepschDao.getCoilYdGdsMvWorkDtlList1";	
	//이적작업진행관리 - 동간이적(김창일 20090423) - 이쿼리는 사용할수 없음 (재개발되어야함 -> szQueryIdGet402로 변경)
	private String szQueryIdGet5 = "com.inisteel.cim.yd.dao.ydprepschdao.YdPrepschDao.getCoilYdGdsMvWorkDtlList2";
	
	//!A 이적작업진행 관리 > 동별 이적목록 조회 (로딩시 조회) - 박지열 2010/03/19
	private String szQueryIdGet400 = "com.inisteel.cim.yd.dao.ydprepschdao.YdPrepschDao.getCoilYdGdsMvWorkDongList";
	//!A 이적작업진행 관리 > 동내이동 - 박지열 2010/03/22
	private String szQueryIdGet401 = "com.inisteel.cim.yd.dao.ydprepschdao.YdPrepschDao.getCoilYdGdsMvWorkDtlList.QueryIdGet401";
	//!A 이적작업진행 관리 > 동간이동 - 박지열 2010/03/22
	private String szQueryIdGet402 = "com.inisteel.cim.yd.dao.ydprepschdao.YdPrepschDao.getCoilYdGdsMvWorkDtlList.QueryIdGet402";
	
	private String szQueryIdGet403 = "com.inisteel.cim.yd.dao.ydprepschdao.YdPrepschDao.getYdPrepschNWordCancelListByCrnPageCoil";
	
	
	
	//	후판제품야드 이적작업진행관리 (심명순 20090518) - 이쿼리는 사용할수 없음 (재개발되어야함)
	private String szQueryIdGet6 = "com.inisteel.cim.yd.dao.ydprepschdao.YdPrepschDao.getPlateYdGdsMvWorkList";
	// 준비스케줄 조회 페이징 쿼리 - 임춘수 2009.09.28
	private String szQueryIdGet7 = "com.inisteel.cim.yd.dao.ydprepschdao.YdPrepschDao.getYdPrepschListPage";
	// 준비스케줄ID LIST 쿼리 - 임춘수 2009.09.28
	private String szQueryIdGet8 = "com.inisteel.cim.yd.dao.ydprepschdao.YdPrepschDao.getYdPrepschIDList";
	//작업예약ID로 준비스케줄 조회
	private String szQueryIdGet9 = "com.inisteel.cim.yd.dao.ydprepschdao.YdPrepschDao.getYdPrepschByYdWbookId";
	//준비스케줄 조회 페이징 쿼리 : 크레인별 - 임춘수 2009.09.28
	private String szQueryIdGet10 = "com.inisteel.cim.yd.dao.ydprepschdao.YdPrepschDao.getYdPrepschListByCrnPage";
	//준비스케줄 조회 페이징 쿼리 - 이송지시취소재료 표시 : 크레인별 - 임춘수 2009.10.27
	private String szQueryIdGet11 = "com.inisteel.cim.yd.dao.ydprepschdao.YdPrepschDao.getYdPrepschNWordCancelListByCrnPage";
	// 준비스케줄ID LIST 쿼리 - 임춘수 2009.10.30
	private String szQueryIdGet12 = "com.inisteel.cim.yd.dao.ydprepschdao.YdPrepschDao.getYdPrepschIDList1";
	// 준비스케줄ID LIST 쿼리 - 임춘수 2009.10.30
	private String szQueryIdGet13 = "com.inisteel.cim.yd.dao.ydprepschdao.YdPrepschDao.getYdPrepschSumListPage";
	// 준비스케줄ID LIST 쿼리- C연주 스카핑/정정보급LOT - 임춘수 2009.10.30
	private String szQueryIdGet14 = "com.inisteel.cim.yd.dao.ydprepschdao.YdPrepschDao.getYdPrepschSumSupplyListPage";
	// BED별 이적대상 조회 쿼리 - 석창화 2009.11.11
	private String szQueryIdGet15 = "com.inisteel.cim.yd.dao.ydprepschdao.YdPrepschDao.getYdPrepschMvPdtList_PIDEV";
	// JMS_구내운송_제품운송요구 - 석창화 2009.11.17
	private String szQueryIdGet16 = "com.inisteel.cim.yd.dao.ydprepschdao.YdPrepschDao.SendJMS_YDTSJ015";
	// JMS_구내운송_제품운송요구취소 - 석창화 2009.11.17
	private String szQueryIdGet17 = "com.inisteel.cim.yd.dao.ydprepschdao.YdPrepschDao.CancelJMS_YDTSJ015";
	//준비스케줄 조회 페이징 쿼리 - 이송지시취소재료 표시 : 후판창고 - 임춘수 2009.11.19
	private String szQueryIdGet19 = "com.inisteel.cim.yd.dao.ydprepschdao.YdPrepschDao.getYdPrepschNWordCancelListPage";
	
	private String szQueryIdGet18 = "com.inisteel.cim.yd.dao.ydprepschdao.YdPrepschDao.getYdPrepschDelYn";
	
	private String szQueryIdGet20 = "com.inisteel.cim.yd.dao.ydprepschdao.YdPrepschDao.getYdSortWrkPrepschListPage";
	// BED별 주작업구분 조회 쿼리 - 석창화 2009.12.04
	private String szQueryIdGet21 = "com.inisteel.cim.yd.dao.ydprepschdao.YdPrepschDao.getYdBedInfoByGrpNm_PIDEV";
	
	// 권오창 2009.12.14 - 재료번호로 준비재료테이블의 야드준비스케쥴ID를 뽑아서 준비스케쥴테이블과 준비재료테이블을 JOIN하여 조회 (N건)
	private String szQueryIdGet22 = "com.inisteel.cim.yd.dao.ydprepschdao.YdPrepschDao.getYdPrepSchYdPrepMtlByPrepMtlStlNo";
	
	//스카핑/정정보급LOT LIST 조회 - 임춘수 2010.01.13
	private String szQueryIdGet23 = "com.inisteel.cim.yd.dao.ydprepschdao.YdPrepschDao.getYdPrepschScarfShearSupplyLotPage";
	
	// Depiler장입 - 준비스케줄기동 - 석창화 2010.01.18
	private String szQueryIdGet24 = "com.inisteel.cim.yd.dao.ydprepschdao.YdPrepschDao.getYdPrepschByDepiler";
	
	//insert query id
	private String szQueryIdIns1 = "com.inisteel.cim.yd.dao.ydprepschdao.YdPrepschDao.insYdPrepsch";
	//update query id - 20090929 이현성(테이블 컬럼변경)
	private String szQueryIdUpd1 = "com.inisteel.cim.yd.dao.ydprepschdao.YdPrepschDao.updYdPrepsch";
	//실제적으로 삭제하는 쿼리 - 임춘수 2009.09.28
	private String szQueryIdDel1 = "com.inisteel.cim.yd.dao.ydprepschdao.YdPrepschDao.delYdPrepsch";
	//레코드의 작업예약ID와 DEL_YN항목에 Y/N를 설정하는 쿼리
	private String szQueryIdDel2 = "com.inisteel.cim.yd.dao.ydprepschdao.YdPrepschDao.delYdPrepsch1";
	
	private String szQueryIdDel3 = "com.inisteel.cim.yd.dao.ydprepschdao.YdPrepschDao.delYdPrepsch_CTYDJ031";
/*------------------------------------- SELECT -------------------------------------------*/
	
	/**
	 * 야드준비스케줄ID
	 */
	public String getYdPrepschId() throws DAOException {
		//메소드명
		String szMethodName = "getYdPrepschId";
		//레코드
		JDTORecord recKey = JDTORecordFactory.getInstance().create();
		//차량스케쥴ID
		String szYdPrepschId = "";
		try {
			//JSPEED 쿼리ID
			recKey.setField("JSPEED_QUERY_ID", szQueryIdKey);
			//쿼리 실행
			JDTORecordSet rsTemp = dbAssDao.getRecordSet(recKey);
			if( rsTemp.size() <= 0 ) {
				throw new JDTOException("야드준비스케줄ID 레코드가 존재하지 않음");
			}
			rsTemp.first();
			recKey = rsTemp.getRecord();
		
			szYdPrepschId = ydDaoUtils.paraRecChkNull(recKey, "YD_PREP_SCH_ID");
		}catch(JDTOException e) {
			String szMsg = "야드준비스케줄ID 생성 시 에러 발생";
			ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
			throw new DAOException(szMsg, e);
		}
		return szYdPrepschId;
	}
	
	/**
	 * 물리적으로 저장속성레코드를 삭제하는 함수
	 * @param inRec
	 * @return
	 * @throws DAOException
	 */
	public int delYdPrepsch(JDTORecord inRec) throws DAOException {
		int intRtnVal = 0;
		boolean blnChk_Field = false;
		JDTORecord recPara = null;
		try {
			
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkPara_getYdPrepsch(recPara, 0);
			
			//parameter error return
			if (!blnChk_Field)
				return intRtnVal = -2;
			
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", szQueryIdDel1);
			
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	}
	
	/**
	 * 레코드의 작업예약ID와 DEL_YN항목에 Y/N를 설정
	 * @param inRec
	 * @return
	 * @throws DAOException
	 */
	public int updDelYdPrepsch(JDTORecord inRec) throws DAOException {
		int intRtnVal = 0;
		boolean blnChk_Field = false;
		JDTORecord recPara = null;
		try {
			
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkPara_getYdPrepsch(recPara, 0);
			
			//parameter error return
			if (!blnChk_Field)
				return intRtnVal = -2;
			
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", szQueryIdDel2);
	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	}
	
	
	/**
	 * 레코드의 작업예약ID와 DEL_YN항목에 Y/N를 설정
	 * @param inRec
	 * @return
	 * @throws DAOException
	 */
	public int updDelYdPrepsch_CTYDJ031(JDTORecord inRec) throws DAOException {
		int intRtnVal = 0;
		JDTORecord recPara = null;
		try {
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", szQueryIdDel3);
	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	}
	
	/**
	 *      [A] 오퍼레이션명 : 야드준비스케줄 SELECT
	 *      
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         int intGp                구분(0:YD_STK_COL_GP,YD_STK_BED_NO,YD_PREP_SCH_ID
	 *         								6:YD_GP, YD_SCH_CD, PAGE_NO, ROW_CNT
	 *         								7:YD_GP, YD_SCH_CD
	 *         								8:YD_WBOOK_ID
	 *         								9:YD_GP, YD_SCH_CD, YD_WRK_PLAN_CRN, YD_PREP_WK_ST, PAGE_NO, ROW_CNT, PAGE_NO, ROW_CNT
	 *         								10:SPOS_WLOC_CD, YD_GP, YD_SCH_CD, YD_WRK_PLAN_CRN, YD_PREP_WK_ST, PAGE_NO, ROW_CNT, PAGE_NO, ROW_CNT
	 *         								11:YD_GP, YD_SCH_CD, CAR_GP
	 *         								12:YD_GP, YD_BAY_GP, YD_SCH_CD, YD_WRK_PLAN_CRN, YD_PREP_WK_ST, CAR_GP, PAGE_NO, ROW_CNT
	 *         								13:YD_GP, YD_BAY_GP, SCH_SEARCH_GP, YD_SCH_CD, YD_WRK_PLAN_CRN, YD_PREP_WK_ST, CAR_GP, PAGE_NO, ROW_CNT
	 *                                      14:YD_STK_COL_GP,YD_STK_BED_NO
	 *                                      15:YD_PREP_SCH_ID
	 *                                      16:YD_PREP_SCH_ID
	 *                                      18:SPOS_WLOC_CD, YD_GP, YD_SCH_CD, YD_PREP_WK_ST, PAGE_NO, ROW_CNT, PAGE_NO, ROW_CNT
	 *                                      17:YD_PREP_SCH_ID, DEL_YN
	 *                                      19:YD_GP,  YD_SCH_CD, YD_PREP_WK_ST, PAGE_NO, ROW_CNT
	 *                                      21:STL_NO
	 *                                      22:YD_GP, YD_BAY_GP, SCH_SEARCH_GP, YD_SCH_CD, YD_WRK_PLAN_CRN, YD_PREP_WK_ST, CAR_GP, YD_AIM_RT_GP, PAGE_NO, ROW_CNT
	 *         								)
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int getYdPrepsch(JDTORecord inRec, JDTORecordSet outRecSet, int intGp) throws DAOException, JDTOException {
		String szMethodName = "getYdPrepsch";
		JDTORecordSet rsTemp = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		String szMsg = null;
		JDTORecord recPara = null;
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkPara_getYdPrepsch(recPara, intGp);
			
			//parameter error return
			if (!blnChk_Field)
				return intRtnVal = -2;
			
			//query id setting
			if (intGp == 0)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet1);
			else if (intGp == 1)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet2);
			else if (intGp == 2)//!A 이적작업진행 관리 > 이적작업진행관리 > 작업진행 조회  - 박지열 2010/03/22 수정
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet3);
			else if (intGp == 3)//!A 동내 이적 --> 사용안함
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet4);
			else if (intGp == 4)//!A 동간 이적 --> 사용안함
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet5);
			else if (intGp == 5)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet6);
			else if (intGp == 6)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet7);
			else if (intGp == 7)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet8);
			else if (intGp == 8)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet9);
			else if (intGp == 9)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet10);
			else if (intGp == 10)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet11);
			else if (intGp == 11)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet12);
			else if (intGp == 12)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet13);
			else if (intGp == 13)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet14);
			else if (intGp == 14)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet15);
			else if (intGp == 15)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet16);
			else if (intGp == 16)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet17);
			else if (intGp == 17)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet18);
			else if (intGp == 18)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet19);
			else if (intGp == 19)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet20);
			else if (intGp == 20)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet21);
			else if (intGp == 21)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet22);
			else if (intGp == 22)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet23);
			else if (intGp == 23)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet24);
			else if (intGp == 400) // 
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet400);
			else if (intGp == 401)//!A 동내이동
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet401);
			else if (intGp == 402)//!A 동간이동
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet402);
			else if (intGp == 403)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet403);
			
			//PIDEV_S :병행가동용:PI_YD
//			String sPI_YD     = commUtils.nvl(recPara.getFieldString("V_PI_YD"), "*");
//			String toQuery_ID = ydPICommDAO.getYdRulePI("", szMethodName, "YD0001", recPara.getFieldString("JSPEED_QUERY_ID"), "APPPI0", sPI_YD, "*" );
//			recPara.setField("JSPEED_QUERY_ID", toQuery_ID);
			
			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);
			
			//result recordSet check
			if (rsTemp.size() > 0)
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
			else {
	//			szMsg = "data not found!";
	//			ydUtils.putLog(szDaoName, szMethodName, szMsg, 3);
				return intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal = rsTemp.size();
	} //end of getYdPrepsch
	
	/**
	 *      [A] 오퍼레이션명 : 야드준비스케줄 SELECT parameter Check
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0:YD_STK_COL_GP,YD_STK_BED_NO,YD_PREP_SCH_ID
	 *         						6:YD_GP, YD_SCH_CD, PAGE_NO, ROW_CNT
	 *         						7:YD_GP, YD_SCH_CD
	 *         						8:YD_WBOOK_ID
	 *         						9:YD_GP, YD_SCH_CD, YD_WRK_PLAN_CRN, YD_PREP_WK_ST, PAGE_NO, ROW_CNT, PAGE_NO, ROW_CNT
	 *         						10:SPOS_WLOC_CD, YD_GP, YD_SCH_CD, YD_WRK_PLAN_CRN, YD_PREP_WK_ST, PAGE_NO, ROW_CNT, PAGE_NO, ROW_CNT
	 *         						11:YD_GP, YD_SCH_CD, CAR_GP
	 *         						12:YD_GP, YD_BAY_GP, YD_SCH_CD, YD_WRK_PLAN_CRN, YD_PREP_WK_ST, CAR_GP, PAGE_NO, ROW_CNT
	 *         						13:YD_GP, YD_BAY_GP, SCH_SEARCH_GP, YD_SCH_CD, YD_WRK_PLAN_CRN, YD_PREP_WK_ST, CAR_GP, PAGE_NO, ROW_CNT
	 *                              14:YD_STK_COL_GP,YD_STK_BED_NO
	 *                              15:YD_PREP_SCH_ID
	 *                              16:YD_PREP_SCH_ID
	 *                              18:SPOS_WLOC_CD, YD_GP, YD_SCH_CD, YD_PREP_WK_ST, PAGE_NO, ROW_CNT, PAGE_NO, ROW_CNT
	 *                              17:YD_PREP_SCH_ID, DEL_YN
	 *                              19:YD_GP,  YD_SCH_CD, YD_PREP_WK_ST, PAGE_NO, ROW_CNT
	 *                              21:STL_NO
	 *                              22:YD_GP, YD_BAY_GP, SCH_SEARCH_GP, YD_SCH_CD, YD_WRK_PLAN_CRN, YD_PREP_WK_ST, CAR_GP, YD_AIM_RT_GP, PAGE_NO, ROW_CNT
	 *         						)
	 * @return boolean          true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkPara_getYdPrepsch(JDTORecord inRec, int intGp) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		try {
			if (intGp == 0 || intGp == 15 || intGp == 16 || intGp == 23) {
				
				szFieldName = "V_YD_PREP_SCH_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
			}else if(intGp == 1){
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
//				szFieldName = "V_PAGE_CNT1";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
//				if (!blnErr) return blnErr;
				
//				szFieldName = "V_ROW_CNT1";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
//				if (!blnErr) return blnErr;
				
//				szFieldName = "V_PAGE_CNT2";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
//				if (!blnErr) return blnErr;
				
//				szFieldName = "V_ROW_CNT2";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
			}else if(intGp == 2){
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_EQP_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_EQP_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_STK_COL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
			}else if(intGp == 3 || intGp == 4){
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_EQP_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_EQP_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_STK_COL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
			}else if(intGp == 5 ){
				szFieldName = "V_SCH_CD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_CRN";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SPAN";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
								
				szFieldName = "V_PAGE_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
			}else if(intGp == 6 ){
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SCH_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
			}else if(intGp == 7 ){
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SCH_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
			}else if(intGp == 8 ){
				szFieldName = "V_YD_WBOOK_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
			}else if(intGp == 9 ){
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SCH_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_WRK_PLAN_CRN";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_PREP_WK_ST";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_CAR_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
			}else if(intGp == 10 ){
				szFieldName = "V_SPOS_WLOC_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SCH_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_WRK_PLAN_CRN";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_PREP_WK_ST";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_CAR_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
			}else if(intGp == 11 ){
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SCH_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_CAR_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
			}else if(intGp == 12 ){
				//12:YD_GP, YD_BAY_GP, YD_SCH_CD, YD_WRK_PLAN_CRN, YD_PREP_WK_ST, CAR_GP, PAGE_NO, ROW_CNT
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SCH_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_WRK_PLAN_CRN";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_PREP_WK_ST";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_CAR_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
			}else if(intGp == 13 ){
				//12:YD_GP, YD_BAY_GP, SCH_SEARCH_GP, YD_SCH_CD, YD_WRK_PLAN_CRN, YD_PREP_WK_ST, CAR_GP, PAGE_NO, ROW_CNT
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_SCH_SEARCH_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SCH_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_WRK_PLAN_CRN";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_PREP_WK_ST";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_CAR_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
			}else if(intGp == 14){
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
			}else if(intGp == 18 ){
				szFieldName = "V_SPOS_WLOC_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SCH_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_PREP_WK_ST";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_CAR_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
			}else if(intGp == 19 ){
				//12:YD_GP,  YD_SCH_CD, YD_PREP_WK_ST, CAR_GP, PAGE_NO, ROW_CNT
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				//szFieldName = "V_YD_BAY_GP";
				//blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				//if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SCH_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				//szFieldName = "V_YD_WRK_PLAN_CRN";
				//blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				//if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_PREP_WK_ST";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				//szFieldName = "V_CAR_GP";
				//blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				//if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
			}else if(intGp == 20){
				
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_GRP_NM";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 13, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
			} else if(intGp == 21){
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 1, 'S', 0, 0);
				
			}else if(intGp == 22 ){
				//12:YD_GP, YD_BAY_GP, SCH_SEARCH_GP, YD_SCH_CD, YD_WRK_PLAN_CRN, YD_PREP_WK_ST, CAR_GP, PAGE_NO, ROW_CNT
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_SCH_SEARCH_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SCH_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_WRK_PLAN_CRN";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_PREP_WK_ST";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_CAR_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} // end of chkPara_getYdPrepsch
	
	
	
	
	
/*------------------------------------- INSERT -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드준비스케줄 INSERT
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int insYdPrepsch(JDTORecord inRec) throws DAOException, JDTOException {
		int intRtnVal = 0;
		boolean blnChk_Field = false;
		JDTORecord recPara = null;
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkParameter(recPara);
			
			//parameter error return
			if (!blnChk_Field)
				return intRtnVal = -2;
			
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", szQueryIdIns1);
	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of insYdPrepsch
	
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드준비스케줄 parameter Check
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return boolean         true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkParameter(JDTORecord inRec) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		
		try {
			szFieldName = "V_YD_PREP_SCH_ID";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_SCH_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_REGISTER";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;	
			
			szFieldName = "V_MODIFIER";	
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;			
		
			szFieldName = "V_DEL_YN";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;			
			
			
			szFieldName = "V_YD_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_PREP_WK_ST";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_TO_LOC_DCSN_MTD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_TO_LOC_GUIDE";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);	
			if (!blnErr) return blnErr;
			
			szFieldName = "V_ARR_WLOC_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;

			
			szFieldName = "V_YD_AIM_BAY_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CARASGN_SEQ";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_EQP_WRK_SH";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_WRK_PLAN_CRN";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_WBOOK_ID";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_AIM_YD_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_INV_SUM_WT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_CAR_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}

		return blnErr;
	} //end of chkPara_YdPrepsch
		
	
/*------------------------------------- UPDATE -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드준비스케줄 UPDATE
	 * 
	 * @param JDTORecord inRec parameter record
	 *         int       intGp 구분(0:YD_STK_COL_GP,YD_STK_BED_NO,YD_PREP_SCH_ID)
	 * @return int             execution count(성공), 0:data not found, -1:duplicate data, 
	 *                         -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdPrepsch(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "updYdPrepsch";
		String szMsg = null;
		JDTORecord outRec = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;

		try {
			//recordSet create
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			
			//변환용 레코드
			JDTORecord recInPara = null;
			JDTORecord recOutPara = null;
			
			//필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			
			//update data select
			intRtnVal = this.getYdPrepsch(inRec, outRecSet, 0);
			
			//parameter error return
			if (intRtnVal < 0) {
	//			szMsg = "parameter error!";
	//			ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
				return intRtnVal;
			}
			
			//data not found return
			if (intRtnVal == 0) {
	//			szMsg = "data not found!";
	//			ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
				return intRtnVal;
			}
			
			if (outRecSet.size() != 1) {
	//			szMsg = "duplicate data!";
	//			ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
				return intRtnVal = -1;
			}
			
			outRecSet.first();
			outRec = outRecSet.getRecord();
			
			//필드명 변환 (필드명 -> V_필드명)
			recOutPara = ydDaoUtils.conversionFieldname(outRec, 0);
			
			//data mapping
			this.dataMapping(recInPara, recOutPara);
		
		
			//parameter check
			blnChk_Field = this.chkParameter(recOutPara);
			
			//parameter error return
			if (!blnChk_Field) return intRtnVal = -2;
			
			//query id setting
			if (intGp == 0)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd1);
			
			
			//query execute
			intRtnVal = dbAssDao.trtProcess(recOutPara);
			
			if (intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdPrepsch
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드준비스케줄 UPDATE parameter mapping
	 * 
	 * @param JDTORecord inRec
	 *        JDTORecord outRec
	 * @return void
	 * @throws JDTOException 
	 */		
	public void dataMapping(JDTORecord inRec, JDTORecord outRec) throws JDTOException {
		String szFieldName = null;

		try {
			szFieldName = "V_YD_PREP_SCH_ID";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_SCH_CD";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);			
					
			szFieldName = "V_REGISTER";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_REG_DDTT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_MODIFIER";	
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_MOD_DDTT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_DEL_YN";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_PREP_WK_ST";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);			
		
			szFieldName = "V_YD_TO_LOC_DCSN_MTD";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_TO_LOC_GUIDE";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_ARR_WLOC_CD";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_AIM_BAY_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CARASGN_SEQ";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_EQP_WRK_SH";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_WRK_PLAN_CRN";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_WBOOK_ID";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_AIM_YD_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_INV_SUM_WT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_CAR_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		
	} // end of YdPrepsch_DataMapping
	
	
	
/*------------------------------------- DELETE -------------------------------------------*/
} // end of class






