package com.inisteel.cim.yd.common.dao.ydWrkbookDao;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.ccommon.util.CCommUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;

/**
 *      [A] 클래스명 : 야드작업예약 DAO
 * 
*/

public class YdWrkbookDao {
	
	// Dao Name
	private String szDaoName = getClass().getName();
	
	private YdUtils ydUtils = new YdUtils();	

	private DBAssistantDAO dbAssDao = new DBAssistantDAO();
	
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();
	
	private YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
	private CCommUtils	   commUtils   = new CCommUtils();	
	
	//select query id
	private String szQueryIdGet1 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbook";
	private String szQueryIdGet2 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYD_WBOOK_ID";
	// 20090302 권오창 
	private String szQueryIdGet3 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookYdWrkbookMtl_PIDEV";
	// 20090305 연은정
	private String szQueryIdGet4 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWbookYdStockYdWbookMtlBookId";
	// 20090306 이현성 
	private String szQueryIdGet5 = "com.inisteel.cim.yd.dao.ydWrkbookDao.getWorkTbRefNONE";
	private String szQueryIdGet6 = "com.inisteel.cim.yd.dao.ydWrkbookDao.getCrnSchNONE";
	
	//20090306 김진욱
	private String szQueryIdGet7 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookCarUseTrnEqp";
	//20090324 김진욱
	private String szQueryIdGet8 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookYdGpBayGp";	

	//20090330 이현성 
	private String szQueryIdGet9 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookBySchCd";
	
	//20090413 권오창 
	private String szQueryIdGet10 = "com.inisteel.cim.yd.dao.ydWrkbookDao.getWrkBookByCrnSchID";

	//20090507 김진욱 
	private String szQueryIdGet11 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookExceptDelYN";
	//20090601 김진욱
	private String szQueryIdGet12 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookYdWrkPlanTcar";
	private String szQueryIdGet13 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookYdWrkPlanTcarNotIn";
	//20090602 김창일
	private String szQueryIdGet14 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWkrBookByStlNo";
	//20090701 김창일
	private String szQueryIdGet15 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWkrBookCode";
	private String szQueryIdGet16 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getSlabYdWBookSchList";
	//20090812 심명순
	private String szQueryIdGet17 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getSangchaLot";
	//20090907 권오창
	private String szQueryIdGet18 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookYdWrkbookMtlByWrkbookID";

	//20090909 권오창
	private String szQueryIdGet19 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookYdWrkbookMtlYdStockByWrkbookID";
	
	//20090914 이현성
	private String szQueryIdGet20 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbook_page";
	
	private String szQueryIdGet21 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookCarUseTrnEqpYdBay";
	
	//20090918 김종건 - 이적작업 진행관리 이적작업 스케줄 전체 현황 조회 
	private String szQueryIdGet22 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookRmvWrkProcMgt";
	
	
	private String szQueryIdGet23 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.chkCrnSchEffectCondition01";
	
	private String szQueryIdGet24 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getTCarWrkWaitList";
	//임춘수 2009.10.08 - 차량상차작업예약의 상위단에 이미 차량상차작업예약이 존재하는 지 조회 쿼리
	private String szQueryIdGet25 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookForCarSch";
	//임춘수 2009.10.27 - 차량상차작업예약의 상위단에 이미 차량상차작업예약이 존재하는 지 조회 쿼리 - 운송장비타입추가[TR/PT]
	private String szQueryIdGet26 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getUpperYdWrkbookForCarSch";
	
	//임춘수 2009.12.02 - 크레인 작업예약 관리 화면의 페이징 쿼리
	private String szQueryIdGet27 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookSearchPage";
	
	//임춘수 2010.01.11 - 우선순위가 빠르고, 작업예약순서가 빠른 대차상차작업예약 조회
	private String szQueryIdGet28 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookForTcarLoad";
	
	//임춘수 2010.02.25 - 지정된 상차동, 하차동에 해당하는 우선순위가 빠르고, 작업예약순서가 빠른 대차상차작업예약 조회
	private String szQueryIdGet29 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookForTcarLoadWithAimBay";

	private String szQueryIdGet30 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdSchCdCrnMtl";
	private String szQueryIdGet31 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdSchCdWrkMtl";
	
	private String szQueryIdGet32 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkPilingInfo";
	
	private String szQueryIdGet300 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookSearchPageCoil";
	private String szQueryIdGet301 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookYdWrkPlanTcarCoil";
	private String szQueryIdGet304 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookYdWrkPlanTcarCoilH";
	private String szQueryIdGet302 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getTCarWrkWaitListCoil";
	private String szQueryIdGet303 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookTCarArrCoil";
	private String szQueryIdGet400 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getTCarWrkWaitListCoilSchCd";
	private String szQueryIdGet500 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getTsMtlArrWloc";
	private String szQueryIdGet501 = "com.inisteel.cim.yd.dao.ydWrkbookDao.getWorkTbRefNONECoil";
	private String szQueryIdGet502 = "com.inisteel.cim.yd.dao.ydWrkbookDao.getWorkTbRefNONESchCd";
	private String szQueryIdGet503 = "com.inisteel.cim.yd.dao.ydWrkbookDao.getWorkTbRefYN";
	private String szQueryIdGet504 = "com.inisteel.cim.yd.dao.ydWrkbookDao.wrkbookydschflag";
	private String szQueryIdGet505 = "com.inisteel.cim.yd.dao.ydWrkbookDao.procY5CrnSchRequest";
	private String szQueryIdGet506 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookCntCarCoil";
	private String szQueryIdGet507 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookTransSeq";
	private String szQueryIdGet508 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookTCcarChk";
	private String szQueryIdGet509 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookHFLChk";
	
	//insert query id
	private String szQueryIdIns1 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.insYdWrkbook";
	//update query id
	private String szQueryIdUpd1 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.updYdWrkbook";
	private String szQueryIdUpd2 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.updYdWrkbookSch";
	private String szQueryIdUpd3 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.updYdWrkbookYD_SCH_PRIOR";

	
	/**
	 * 오퍼레이션명 : 작업예약ID를 생성하여 반환하는 메소드
	 * @return String : 작업예약ID
	 * @throws DAOException
	 */
	public String getYdWrkbookId() throws DAOException {
		//메소드명
		String szMethodName = "getYdWrkbookId";
		//레코드
		JDTORecord recKey = JDTORecordFactory.getInstance().create();
		//작업예약ID
		String szYdWrkbookId = "";
		try {
			//JSPEED 쿼리ID
			recKey.setField("JSPEED_QUERY_ID", szQueryIdGet2);
			recKey.setField("V_YD_WBOOK_ID", "1");
			//쿼리 실행
			JDTORecordSet rsTemp = dbAssDao.getRecordSet(recKey);
			if( rsTemp.size() <= 0 ) {
				throw new JDTOException("작업예약ID 레코드가 존재하지 않음");
			}
			rsTemp.first();
			recKey = rsTemp.getRecord();
		
			szYdWrkbookId = ydDaoUtils.paraRecChkNull(recKey, "YD_WBOOK_ID");
		}catch(JDTOException e) {
			String szMsg = "작업예약ID 생성 시 에러 발생";
			ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
			throw new DAOException(szMsg, e);
		}
		return szYdWrkbookId;
	}
/*------------------------------------- SELECT -------------------------------------------*/
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드작업예약 SELECT
	 *      
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         int intGp                구분(0:YD_WBOOK_ID
	 *                                      1:YD_WBOOK_ID[CREATE YD_WBOOK_ID]
	 *                                      2:YD_CRN_SCH_ID
	 *                                      3:YD_WBOOK_ID
	 *                                      4:NONE
	 *                                      5:NONE
	 *                                      6:YD_CAR_USE_GP, TRN_EQP_CD
	 *                                      7:YD_GP, YD_BAY_GP
	 *                                      8:YD_SCH_CD
	 *                                      9:YD_CRN_SCH_ID
	 *                                      10:YD_WBOOK_ID
	 *                                      11:YD_WRK_PLAN_TCAR
	 *                                      12:YD_WRK_PLAN_TCAR, YD_WBOOK_ID
	 *                                      17:YD_WBOOK_ID
	 *                                      18:YD_WBOOK_ID
	 *                                      19:YD_GP, YD_BAY_GP, YD_SCH_CD, DEL_YN, DATE_FROM, DATE_TO, PAGE_CNT, ROW_CNT
	 *                                      20:YD_CAR_USE_GP, TRN_EQP_CD, YD_GP, YD_BAY_GP
	 *                                      
	 *                                      
	 *                                      23:V_YD_WRK_PLAN_TCAR
	 *                                      24: YD_GP, YD_WBOOK_ID
	 *                                      25: YD_GP, TRN_EQP_GP, YD_WBOOK_ID 
	 *                                      26:YD_GP, YD_BAY_GP, YD_SCH_CD, DEL_YN, DATE_FROM, DATE_TO, PAGE_CNT, ROW_CNT
	 *                                      27:YD_WRK_PLAN_TCAR
	 *                                      28:YD_WRK_PLAN_TCAR, YD_AIM_BAY_GP, YD_GP, YD_BAY_GP
	 *                                      )
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int getYdWrkbook(JDTORecord inRec, JDTORecordSet outRecSet, int intGp) throws DAOException, JDTOException {
		String szMethodName = "getYdWrkbook";
		JDTORecordSet rsTemp = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		String szMsg = null;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkPara_getYdWrkbook(recPara, intGp);
			
			//parameter error return
			if (!blnChk_Field)
				return intRtnVal = -2;
			
			//query id setting
			if (intGp == 0)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet1);
			else if (intGp == 1)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet2);
			else if (intGp == 2)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet3);
			else if (intGp == 3)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet4);
			else if (intGp == 4)
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
			else if (intGp == 24)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet25);
			else if (intGp == 25)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet26);
			else if (intGp == 26)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet27);
			else if (intGp == 27)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet28);
			else if (intGp == 28)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet29);
			else if (intGp == 32)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet32);
			else if (intGp == 300)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet300);
			else if (intGp == 301)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet301);
			else if (intGp == 302)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet302);
			else if (intGp == 303)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet303);
			else if (intGp == 304)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet304);
			else if (intGp == 400)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet400);
			else if (intGp == 500)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet500);
			else if (intGp == 501)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet501);
			else if (intGp == 502)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet502);
			else if (intGp == 503)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet503);
			else if (intGp == 504)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet504);
			else if (intGp == 505)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet505);
			else if (intGp == 506)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet506);
			else if (intGp == 507)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet507);
			else if (intGp == 508)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet508);
			else if (intGp == 509)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet509);
			
//PIDEV
			//PIDEV_S :병행가동용:PI_YD
//			String sPI_YD     = commUtils.nvl(recPara.getFieldString("V_PI_YD"), "*");				
//			String toQuery_ID = ydPICommDAO.getYdRulePI("", szMethodName, "YD0001", commUtils.trim(recPara.getFieldString("JSPEED_QUERY_ID")), "APPPI0", sPI_YD, "*" );
//			recPara.setField("JSPEED_QUERY_ID", commUtils.trim(toQuery_ID));
			
			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);
			
			//result recordSet check
			if (rsTemp.size() > 0)
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
			else {
				//data not found
	//			szMsg = "data not found!";
	//			ydUtils.putLog(szDaoName, szMethodName, szMsg, 3);
				return intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal = rsTemp.size();
	} //end of getYdWrkbook
	
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드작업예약 SELECT parameter Check
	 * 
	 * @param  JDTORecord inRec  parameter record
	 *         int intGp                구분(0:YD_WBOOK_ID
	 *                                      1:YD_WBOOK_ID[CREATE YD_WBOOK_ID]
	 *                                      2:YD_CRN_SCH_ID
	 *                                      3:YD_WBOOK_ID
	 *                                      4:NONE
	 *                                      5:NONE
	 *                                      6:YD_CAR_USE_GP, TRN_EQP_CD
	 *                                      7:YD_GP, YD_BAY_GP
	 *                                      8:YD_SCH_CD
	 *                                      9:YD_CRN_SCH_ID
	 *                                      10:YD_WBOOK_ID
	 *                                      11:V_YD_WRK_PLAN_TCAR
	 *                                      12:V_YD_WRK_PLAN_TCAR, V_YD_WBOOK_ID
	 *                                      17:YD_WBOOK_ID
	 *                                      18:YD_WBOOK_ID
	 *                                      19:YD_GP, YD_BAY_GP, YD_SCH_CD, DEL_YN, DATE_FROM, DATE_TO, PAGE_CNT, ROW_CNT
	 *                                      20:YD_CAR_USE_GP, TRN_EQP_CD, YD_GP, YD_BAY_GP
	 *                                      
	 *                                      23:V_YD_WRK_PLAN_TCAR
	 *                                      24: YD_GP, YD_WBOOK_ID
	 *                                      25: YD_GP, TRN_EQP_GP, YD_WBOOK_ID
	 *                                      26:YD_GP, YD_BAY_GP, YD_SCH_CD, DEL_YN, DATE_FROM, DATE_TO, PAGE_CNT, ROW_CNT
	 *                                      27:YD_WRK_PLAN_TCAR
	 *                                      28:YD_WRK_PLAN_TCAR, YD_AIM_BAY_GP, YD_GP, YD_BAY_GP
	 *                                      )
	 * @return boolean           true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkPara_getYdWrkbook(JDTORecord inRec, int intGp) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		
		try {
			if (intGp == 0 || intGp == 1 || intGp == 3 || intGp == 10 || intGp == 15 || intGp == 17 || intGp == 18) {
				szFieldName = "V_YD_WBOOK_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
			} else if(intGp == 2 || intGp == 9){
				szFieldName = "V_YD_CRN_SCH_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);			
			
				//20090330 김진욱
			} else if(intGp == 4){
				szFieldName = "V_YD_EQP_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				
			} else if(intGp == 6){
				szFieldName = "V_TRN_EQP_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);	
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_CAR_USE_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);	
			} else if(intGp == 7){
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);	
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);				
			}  else if(intGp == 8){
				
				szFieldName = "V_YD_SCH_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);
			}  else if(intGp == 11 || intGp == 27 ) {
				
				szFieldName = "V_YD_WRK_PLAN_TCAR";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				
			}  else if(intGp == 12){
				
				szFieldName = "V_YD_WRK_PLAN_TCAR";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_WBOOK_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
			}  else if(intGp == 13){
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 1, 'S', 0, 0);
			} else if(intGp == 14){
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);	
			}  else if(intGp == 16){
				szFieldName = "V_TRN_CAR_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 15, 1, 'S', 0, 0);	
				if (!blnErr) return blnErr;
				
				
			} else if(intGp == 19 || intGp == 26){
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);	
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);	
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SCH_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);	
				if (!blnErr) return blnErr;
				
				szFieldName = "V_DEL_YN";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);	
				if (!blnErr) return blnErr;
			
				
				szFieldName = "V_DATE_FROM";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);	
				if (!blnErr) return blnErr;
				
				
				szFieldName = "V_DATE_TO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);	
				if (!blnErr) return blnErr;
				
				
				szFieldName = "V_PAGE_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
				
			} else if(intGp == 20){
				szFieldName = "V_YD_CAR_USE_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);	
				if (!blnErr) return blnErr;
				
				szFieldName = "V_TRN_EQP_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, YdDaoUtils.STRING_TYPE, 0, 0);	
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);	
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);	
				if (!blnErr) return blnErr;
			}  else if(intGp == 23){
				szFieldName = "V_YD_WRK_PLAN_TCAR";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
			} else if(intGp == 24){
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);	
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_WBOOK_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);	
				if (!blnErr) return blnErr;
			} else if(intGp == 25){
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);	
				if (!blnErr) return blnErr;
				
				szFieldName = "V_TRN_EQP_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);	
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_WBOOK_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);	
				if (!blnErr) return blnErr;
			} else if(intGp == 28){
				szFieldName = "V_YD_WRK_PLAN_TCAR";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				
				szFieldName = "V_YD_AIM_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);	
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);	
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);	
				if (!blnErr) return blnErr;
			}
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} // end of chkPara_getYdWrkbook
	
	
	
/*------------------------------------- INSERT -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드작업예약 INSERT
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int insYdWrkbook(JDTORecord inRec) throws DAOException, JDTOException {
		int intRtnVal = 0;
		boolean blnChk_Field = true;
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
	} // end of insYdWrkbook
	

		
	
/*------------------------------------- UPDATE -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드작업예약 UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0:YD_WBOOK_ID)
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdWrkbook(JDTORecord inRec, int intGp ) throws DAOException, JDTOException {
		String szMethodName = "updYdWrkbook";
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
			intRtnVal = this.getYdWrkbook(inRec, outRecSet, 0);
			
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
			
			//duplicate data return
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
			recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd1);
	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recOutPara);
			
			//execution error return
			if (intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdWrkbook
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드작업예약 UPDATE_YD_SCH_CD[K]
	 *      
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), 0:data not found, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdWrkbook_YD_SCH_CD(JDTORecord inRec) throws DAOException, JDTOException {
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
	
			//parameter check
			blnChk_Field = this.chkParameter_YD_SCH_CD(recPara);
			
			//parameter error return
			if (!blnChk_Field)
				return intRtnVal = -2;
			
			
			//쿼리 아이디 세팅
			recPara.setField("JSPEED_QUERY_ID", szQueryIdUpd2);
	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
			
			//execution error return
			if (intRtnVal < 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdWrkbook_YD_SCH_CD
	
	

	/**
	 *      [A] 오퍼레이션명 : 야드작업예약 UPDATE_YD_SCH_CD[K]
	 *      
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), 0:data not found, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdWrkbook_YD_SCH_PRIOR(JDTORecord inRec) throws DAOException, JDTOException {
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
	
			//parameter check
			blnChk_Field = this.chkParameter_YD_SCH_PRIOR(recPara);
			
			//parameter error return
			if (!blnChk_Field)
				return intRtnVal = -2;
			
			
			//쿼리 아이디 세팅
			recPara.setField("JSPEED_QUERY_ID", szQueryIdUpd3);
	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
			
			//execution error return
			if (intRtnVal < 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdWrkbook_YD_SCH_PRIOR
	
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드작업예약 parameter Check
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return boolean         true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkParameter(JDTORecord inRec) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		
		try {
			szFieldName = "V_YD_WBOOK_ID";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_REGISTER";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_REG_DDTT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
//			szFieldName = "V_MODIFIER";
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
//			if (!blnErr) return blnErr;
//	
//			szFieldName = "V_MOD_DDTT";
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
//			if (!blnErr) return blnErr;
//			
//			szFieldName = "V_DEL_YN";
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
//			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_BAY_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_SCH_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_SCH_PRIOR";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_SCH_PROG_STAT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_SCH_ST_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_SCH_REQ_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_AIM_YD_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_AIM_BAY_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CTS_RELAY_YN";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CTS_RELAY_BAY_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_TO_LOC_DCSN_MTD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_TO_LOC_GUIDE";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_WRK_PLAN_TCAR";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CAR_USE_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			szFieldName = "V_DIST_SHIPASSIGN_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_TRN_EQP_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_CAR_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 15, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_CARD_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;

/*
 			szFieldName = "V_PTOP_PLNT_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_DEST_TEL_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 20, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_DIST_SHIPASSIGN_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);			
*/
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} //end of chkParameter
	
	
	
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드작업예약 parameter Check
	 *      V_MODIFIER, V_DEL_YN, V_YD_SCH_CD[K]
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return boolean         true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkParameter_YD_SCH_CD(JDTORecord inRec) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		
		szFieldName = "V_MODIFIER";
		blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
		if (!blnErr) return blnErr;
		
		szFieldName = "V_DEL_YN";
		blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
		if (!blnErr) return blnErr;
		
		szFieldName = "V_YD_SCH_CD";
		blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);
		
		return blnErr;
	} //end of chkParameter_YD_SCH_CD
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드작업예약 parameter Check
	 *      V_MODIFIER, V_DEL_YN, V_YD_SCH_CD[K]
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return boolean         true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkParameter_YD_SCH_PRIOR(JDTORecord inRec) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		
		szFieldName = "V_MODIFIER";
		blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
		if (!blnErr) return blnErr;
		
		szFieldName = "V_YD_SCH_PRIOR";
		blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
		if (!blnErr) return blnErr;
		
		szFieldName = "V_YD_SCH_CD";
		blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);
		
		return blnErr;
	} //end of chkParameter_YD_SCH_PRIOR
	
	
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드작업예약 UPDATE parameter mapping
	 * 
	 * @param JDTORecord inRec
	 *        JDTORecord outRec
	 * @return void
	 * @throws JDTOException 
	 */		
	public void dataMapping(JDTORecord inRec, JDTORecord outRec) throws JDTOException {
		String szFieldName = null;

		szFieldName = "V_YD_WBOOK_ID";
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
		
		szFieldName = "V_YD_BAY_GP";		// 야드동구분
		ydDaoUtils.mappingData(inRec, outRec, szFieldName);
		
		szFieldName = "V_YD_SCH_CD";
		ydDaoUtils.mappingData(inRec, outRec, szFieldName);
		
		szFieldName = "V_YD_SCH_PRIOR";
		ydDaoUtils.mappingData(inRec, outRec, szFieldName);

		szFieldName = "V_YD_SCH_PROG_STAT";
		ydDaoUtils.mappingData(inRec, outRec, szFieldName);

		szFieldName = "V_YD_SCH_ST_GP";
		ydDaoUtils.mappingData(inRec, outRec, szFieldName);

		szFieldName = "V_YD_SCH_REQ_GP";
		ydDaoUtils.mappingData(inRec, outRec, szFieldName);

		szFieldName = "V_YD_AIM_YD_GP";
		ydDaoUtils.mappingData(inRec, outRec, szFieldName);

		szFieldName = "V_YD_AIM_BAY_GP";
		ydDaoUtils.mappingData(inRec, outRec, szFieldName);
		
		szFieldName = "V_YD_CTS_RELAY_YN";
		ydDaoUtils.mappingData(inRec, outRec, szFieldName);
		
		szFieldName = "V_YD_CTS_RELAY_BAY_GP";
		ydDaoUtils.mappingData(inRec, outRec, szFieldName);
		
		szFieldName = "V_YD_TO_LOC_DCSN_MTD";
		ydDaoUtils.mappingData(inRec, outRec, szFieldName);
		
		szFieldName = "V_YD_TO_LOC_GUIDE";
		ydDaoUtils.mappingData(inRec, outRec, szFieldName);
		
		szFieldName = "V_YD_WRK_PLAN_TCAR";
		ydDaoUtils.mappingData(inRec, outRec, szFieldName);
		
		szFieldName = "V_YD_CAR_USE_GP";
		ydDaoUtils.mappingData(inRec, outRec, szFieldName);
		
		szFieldName = "V_TRN_EQP_CD";
		ydDaoUtils.mappingData(inRec, outRec, szFieldName);
		
		szFieldName = "V_CAR_NO";
		ydDaoUtils.mappingData(inRec, outRec, szFieldName);
		
		szFieldName = "V_CARD_NO";
		ydDaoUtils.mappingData(inRec, outRec, szFieldName);

/*
 		szFieldName = "V_PTOP_PLNT_GP";
		ydDaoUtils.mappingData(inRec, outRec, szFieldName);
		
		szFieldName = "V_DEST_TEL_NO";
		ydDaoUtils.mappingData(inRec, outRec, szFieldName);
		
		szFieldName = "V_DIST_SHIPASSIGN_GP";
		ydDaoUtils.mappingData(inRec, outRec, szFieldName);	
*/		
	} // end of dataMapping
/*------------------------------------- DELETE -------------------------------------------*/
	/*
	 * 후판슬라브 자동준비작업스케쥴 대상검색
	 * - 해당스케쥴로 크레인스케쥴에 해당재료가 있는지 체크한다.
	 * 리턴값 : 해당 스케쥴ID
	 */
	public int getYdSchCrnMtl(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		
		JDTORecordSet rsTemp 	= null;
		JDTORecord recPara 		= null;
		
		int intRtnVal = 0;
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			recPara.setField("JSPEED_QUERY_ID", szQueryIdGet30);
			
			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);
			
			//result recordSet check
			if (rsTemp.size() > 0)
				outRecSet.addAll(rsTemp);
			else {
				return intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal = rsTemp.size();
	} //end of getYdSchCrnMtl
	
	/*
	 * 후판슬라브 자동준비작업스케쥴 대상검색
	 * - 해당스케쥴로 작업예약이 등록되어 있는지 체크
	 */
	public int getYdSchWrkMtl(JDTORecord inRec) throws DAOException, JDTOException {
		
		JDTORecordSet rsTemp 	= null;
		JDTORecord recPara 		= null;
		
		int intRtnVal = 0;
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			recPara.setField("JSPEED_QUERY_ID", szQueryIdGet31);
			
			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);
			
			//result recordSet check
			if (rsTemp.size() > 0)
				intRtnVal = rsTemp.size();
			else {
				return intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal = rsTemp.size();
	} //end of getYdSchCrnMtl
	
} // end of class






