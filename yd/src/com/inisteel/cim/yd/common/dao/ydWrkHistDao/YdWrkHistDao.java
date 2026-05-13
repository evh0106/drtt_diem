package com.inisteel.cim.yd.common.dao.ydWrkHistDao;

import xlib.cmc.GridData;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.ccommon.util.CCommUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;

/**
 *      [A] 클래스명 :저장이력  DAO
 * 
*/

public class YdWrkHistDao {
	
	// Dao Name
	private String szDaoName = getClass().getName();
	
	private YdUtils ydUtils = new YdUtils();	

	private DBAssistantDAO dbAssDao = new DBAssistantDAO();
	
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();
	
	private YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
	private CCommUtils	   commUtils   = new CCommUtils();		
	
	//select query id
	private String szQueryIdGet1 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getYdWrkHistDao";	
	private String szQueryIdGet2 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getCrnWrkMtl";
	private String szQueryIdGet3 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getYdCrnStockByMSlab";
	private String szQueryIdGet4 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getYdCrnStockByCoil";
	private String szQueryIdGet5 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getYdCrnStockByPlate";
	private String szQueryIdGet6 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getYdCrnStockBySlab";
	private String szQueryIdGet7 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getYdCarSch";
	private String szQueryIdGet8 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getYdTcarSch";
	private String szQueryIdGet9 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getYdWtclTnkSch";
	
	
	
	
	//2009.09.23 이현성
	private String szQueryIdGet10 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getYdWrkHistDaoStlNo";
	
	
	private String szQueryIdGet11 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getYdWrkHistDaoEventSearch";
	
	//2009.10.07  이현성
	private String szQueryIdGet12 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getYdWrkHistDaoEventSearchTot_PIDEV";
	
	//2010.01.13  석창화
	private String szQueryIdGet13 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getYdWrkHistDaoEventSearchPlateGds_PIDEV";
	
	//2010.02.03  석창화
	private String szQueryIdGet14 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getYdWrkAnalysis";
	//2010.02.03  석창화
	private String szQueryIdGet15 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getYdWrkAnalysisBySchCd";
	//2010.02.25  석창화
	private String szQueryIdGet16 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getYdWrkAnalysisBySchCdPlateGds";
	
	private String szQueryIdGet17 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getYdWrkHistDaoEventSearchTotBayGp_PIDEV";
	
	private String szQueryIdGet18 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getYdWrkAnalysisForPlate";
	
	private String szQueryIdGet19 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getYdWrkAnalysisByAbSlab";
	
	// 2017.11.17 옥외 Slab 야드 일일 장비 처리 현황 조회
	private String szQueryIdGet20 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getOuthouseSlabYdEqpHdStatList";	
	
	// 2017.11.17 옥외 Slab 야드 일일 근무자 조회
	private String szQueryIdGet21 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getOuthouseSlabYdWorkerList";
	
	// 2017.11.17 옥외 Slab 야드 일일 장비 처리 현황 - 현재고 조회
	private String szQueryIdGet22 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getOuthouseSlabYdSlabWtSum";

//	크레인작업실적현황 2010.04.21
	private String szQueryIdGet300 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getCrnWrkWrStat";
//	크레인작업실적현황 2010.04.21
	private String szQueryIdGet301 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getCrnWrkWrStatNew";
	
	private String szQueryIdGet302 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getYdWrkHistDaoEventSearchTotBayGp2_PIDEV";
	
	private String szQueryIdGet303 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getPlateYdCrnStlNoList";
	
	private String szQueryIdGet304 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getYdSlabmarkingHist_PIDEV";
	
	private String szQueryIdGet305 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getSlabYdDailyWork";
	
	private String szQueryIdGet306 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getSlabYdDailyCntByBay";
	
	private String szQueryIdGet307 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getSlabYdDailyCntByRt";
	
	private String szQueryIdGet308 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getSlabYdDailySalStk";
	
	private String szQueryIdGet309 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getSlabYdDailyCntByEQPID";
	
	private String szQueryIdGet310 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getSlabYdDailyAcceptor";
	
	private String szQueryIdGet311 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getSlabYdDailyEQPNote";
	
	private String szQueryIdGet312 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getSlabYdDailyCntByRtWait";
	
	private String szQueryIdGet313 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getSlabYdDailyCntByRtWaitPast";
	
	private String szQueryIdGet314 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYD_SALESTK_ID";
	
	private String szQueryIdGet401 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getSlabFtmvPointWaitOccrDaily";
	
	private String szQueryIdGet402 = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getSlabFtmvWaitOccrDaily";
	
	//insert query id
	private String szQueryIdIns1 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.insYdWrkHistDao";
	
	//insert query id
	private String szQueryIdIns10 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.insYdWrkHistDaoJYard";
	
	private String szQueryIdIns2 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.insYdWrkHistDaoPosFix";
	
	private String szQueryIdIns3 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.insYdWrkHistDaoCarSch";
	
	private String szQueryIdIns4 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.insYdWrkHistPortDao";
	
	private String szQueryIdIns5 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.insYdCoilWrkHistDao";
	
	private String szQueryIdIns6 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.insYdWrkHistbackup";
	
	private String szQueryIdIns7 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.insYdPilingHist";
	
	private String szQueryIdIns8 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.insYdAbSlabHist";
	
	private String szQueryIdIns9 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.insYdWrkHistCarMvMtl";

	//update query id
	private String szQueryIdUpd1 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.updYdWrkHistDao";
	private String szQueryIdUpd2 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.updYdWrkHistByCar";
	private String szQueryIdUpd3 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.updYdWrkHistByTcar";
	private String szQueryIdUpd4 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.updYdWrkHistByWtclTnk";
	private String szQueryIdUpd5 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.updYdWrkHistByCarSch";
	private String szQueryIdUpd6 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.updYdWrkHistByCrnSch";
	
	private String szQueryIdUpd7 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.updYdAbSlabHist";
	
	// 2017.11.17 옥외 Slab 야드 일일 근무자 등록(Merge문)
	private String szQueryIdUpd8 = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.updOuthouseSlabYdWorker";
	
/*------------------------------------- SELECT -------------------------------------------*/
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드저장품 SELECT
	 *      
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         int intGp                구분(0:STL_NO
	 *                                    
	 *                                     )
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int getYdWrkHist(JDTORecord inRec, JDTORecordSet outRecSet, int intGp) throws DAOException, JDTOException {
		String szMethodName = "getYdWrkHist";
		JDTORecordSet rsTemp = null;
		int intRtnVal = -100;
		boolean blnChk_Field = true;
		String szMsg = null;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkPara_getYdWrkHist(recPara, intGp);
			
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
			else if (intGp == 305)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet305);
			else if (intGp == 306)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet306);
			else if (intGp == 307)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet307);
			else if (intGp == 308)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet308);
			else if (intGp == 309)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet309);
			else if (intGp == 310)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet310);
			else if (intGp == 311)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet311);
			else if (intGp == 312)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet312);
			else if (intGp == 313)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet313);
			else if (intGp == 314)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet314);
			else if (intGp == 401)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet401);
			else if (intGp == 402)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet402);
			
//PIDEV
			//PIDEV_S :병행가동용:PI_YD
//			String sPI_YD     = commUtils.nvl(recPara.getFieldString("V_PI_YD"), "*");				
//			String toQuery_ID = ydPICommDAO.getYdRulePI("", szMethodName, "YD0001", commUtils.trim(recPara.getFieldString("JSPEED_QUERY_ID")), "APPPI0", sPI_YD, "*" );
//			recPara.setField("JSPEED_QUERY_ID", commUtils.trim(toQuery_ID));			
			
			
			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);
			
			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
				szMsg = "[getYdWrkHist] data found!";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, 4);
			}else {
				//data not found
				szMsg = "[getYdWrkHist]data not found!";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, 4);
				return intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		szMsg = "[getYdWrkHist] return value : " + intRtnVal;
		ydUtils.putLog(szDaoName, szMethodName, szMsg, 4);
		return intRtnVal;
	} //end of getYdStock
	
	/**
	 *      [A] 오퍼레이션명 : 옥외 Slab 야드 일일 장비 처리 현황 조회
	 *      
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         int intGp                구분(0:STL_NO
	 *                                    
	 *                                     )
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int getOuthouseSlabYdEqpHdStatList(JDTORecord inRec, JDTORecordSet outRecSet, int intGp) throws DAOException, JDTOException {
		String szMethodName = "getOuthouseSlabYdEqpHdStatList";
		JDTORecordSet rsTemp = null;
		int intRtnVal = -100;
		boolean blnChk_Field = true;
		String szMsg = null;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkPara_getYdWrkHist(recPara, intGp);
			
			//parameter error return
			if (!blnChk_Field)
				return intRtnVal = -2;
			
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", szQueryIdGet20);

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);
			
			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
				szMsg = "[getOuthouseSlabYdEqpHdStatList] data found!";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, 4);
			}else {
				//data not found
				szMsg = "[getOuthouseSlabYdEqpHdStatList]data not found!";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, 4);
				return intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		szMsg = "[getOuthouseSlabYdEqpHdStatList] return value : " + intRtnVal;
		ydUtils.putLog(szDaoName, szMethodName, szMsg, 4);
		return intRtnVal;
	} //end of getOuthouseSlabYdEqpHdStatList
	
	
	/**
	 *      [A] 오퍼레이션명 : 옥외 Slab 야드 일일 장비 처리 현황 - 현재고 조회
	 *      
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         int intGp                구분(0:STL_NO
	 *                                    
	 *                                     )
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int getOuthouseSlabYdSlabWtSum(JDTORecord inRec, JDTORecordSet outRecSet, int intGp) throws DAOException, JDTOException {
		String szMethodName = "getOuthouseSlabYdSlabWtSum";
		JDTORecordSet rsTemp = null;
		int intRtnVal = -100;
		boolean blnChk_Field = true;
		String szMsg = null;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			//blnChk_Field = this.chkPara_getYdWrkHist(recPara, intGp);
			
			//parameter error return
			if (!blnChk_Field)
				return intRtnVal = -2;
			
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", szQueryIdGet22);
			rsTemp = dbAssDao.getRecordSet(recPara);
			
			//result recordSet check
			if (rsTemp.size() > 0) {
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
				szMsg = "[getOuthouseSlabYdEqpHdStatList] data found!";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, 4);
			}else {
				//data not found
				szMsg = "[getOuthouseSlabYdEqpHdStatList]data not found!";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, 4);
				return intRtnVal = 0;
			}
		} catch (Exception e) {
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		szMsg = "[getOuthouseSlabYdSlabWtSum] return value : " + intRtnVal;
		ydUtils.putLog(szDaoName, szMethodName, szMsg, 4);
		return intRtnVal;
	} //end of getOuthouseSlabYdSlabWtSum
	
	
	/**
	 *      [A] 오퍼레이션명 : 옥외 Slab 야드 일일 근무자 조회
	 *      
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         int intGp                구분(0:STL_NO
	 *                                    
	 *                                     )
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int getOuthouseSlabYdWorkerList(JDTORecord inRec, JDTORecordSet outRecSet, int intGp) throws DAOException, JDTOException {
		String szMethodName = "getOuthouseSlabYdWorkerList";
		JDTORecordSet rsTemp = null;
		int intRtnVal = -100;
		boolean blnChk_Field = true;
		String szMsg = null;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkPara_getYdWrkHist(recPara, intGp);
			
			//parameter error return
			if (!blnChk_Field)
				return intRtnVal = -2;
			
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", szQueryIdGet21);

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);
			
			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
				szMsg = "[getOuthouseSlabYdWorkerList] data found!";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, 4);
			}else {
				//data not found
				szMsg = "[getOuthouseSlabYdWorkerList]data not found!";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, 4);
				return intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		szMsg = "[getOuthouseSlabYdWorkerList] return value : " + intRtnVal;
		ydUtils.putLog(szDaoName, szMethodName, szMsg, 4);
		return intRtnVal;
	} //end of getOuthouseSlabYdWorkerList
	
	/**
	 *      [A] 오퍼레이션명 : 옥외 Slab 야드 일일 근무자 등록
	 * 
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updOuthouseSlabYdWorker(JDTORecord inRec) throws DAOException, JDTOException {
		String szMethodName = "updOuthouseSlabYdWorker";
		int intRtnVal = 0;
		
		try {
			//변환용 레코드
			JDTORecord recOutPara = null;
			
			//필드명 변환 (필드명 -> V_필드명)
			recOutPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//query id setting
			recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd8);
			intRtnVal = dbAssDao.trtProcess(recOutPara);
			
			//execution error return
			if (intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updOuthouseSlabYdWorker
	
	
	
	/**
	 *    [A] 오퍼레이션명 : 통합야드 업무일지 근무자정보 및 작업내용 등록
	 * @param  GridData jrParam  parameter record
	 * @return GridData
	 * @throws DAOException 
	 */
	public GridData updSlabYdDailyWorkInfo(GridData jrParam) throws DAOException {
		
		DBAssistantDAO assistantDAO = new DBAssistantDAO();
		String methodNm = "통합야드 업무일지 근무자정보 및 작업내용 등록 [updSlabYdDailyWorkInfo] : ";
		String logId = "updSlabYdDailyWorkInfo";  //gdReq.getIPAddress();
		String trtNm = ",updSlabYdDailyWorkInfo";
		int result = 0;
		
		try {
			Object oParam[]   = null;
			String vWORK_DATE = jrParam.getParam("WORK_DATE");
			String vYD_GP = jrParam.getParam("YD_GP");
			String vDUTY_GP1 = jrParam.getParam("DUTY_GP1");
			String vDUTY_CHG1 = jrParam.getParam("DUTY_CHG1");
			String vDUTY_OT1 = jrParam.getParam("DUTY_OT1");
			String vDUTY_GP2 = jrParam.getParam("DUTY_GP2");
			String vDUTY_CHG2 = jrParam.getParam("DUTY_CHG2");
			String vDUTY_OT2 = jrParam.getParam("DUTY_OT2");
			String vDUTY_GP3 = jrParam.getParam("DUTY_GP3");
			String vDUTY_CHG3 = jrParam.getParam("DUTY_CHG3");
			String vDUTY_OT3 = jrParam.getParam("DUTY_OT3");
			String vCONTENTS = jrParam.getParam("CONTENTS");
			String vSPECIFIC1 = jrParam.getParam("SPECIFIC1");
			String vSPECIFIC2 = jrParam.getParam("SPECIFIC2");
			String vSPECIFIC3 = jrParam.getParam("SPECIFIC3");
			String userId = jrParam.getParam("userid");
			
			String vNOTE_CRN1     		=	jrParam.getParam("NOTE_CRN1");     
			String vNOTE_CRN2     		=	jrParam.getParam("NOTE_CRN2");   
			String vNOTE_CRN3     		=	jrParam.getParam("NOTE_CRN3");   
			String vNOTE_FORK1D   		=	jrParam.getParam("NOTE_FORK1D");  
			String vNOTE_FORK1N   		=	jrParam.getParam("NOTE_FORK1N");
			String vNOTE_FORK2D   		=	jrParam.getParam("NOTE_FORK2D");
			String vNOTE_FORK2N   		=	jrParam.getParam("NOTE_FORK2N");
			String vNOTE_RS1      		=	jrParam.getParam("NOTE_RS1");
			String vFORK1_SHIFT   		=	jrParam.getParam("FORK1_SHIFT");   
			String vFORK1_IN_SALE 		=	jrParam.getParam("FORK1_IN_SALE");
			String vFORK1_IN_SHORT		=	jrParam.getParam("FORK1_IN_SHORT");
			String vFORK1_IN_HEAT 		=	jrParam.getParam("FORK1_IN_HEAT");
			String vFORK1_OUT_SALE		=	jrParam.getParam("FORK1_OUT_SALE");
			String vFORK1_OUT_MOVE		=	jrParam.getParam("FORK1_OUT_MOVE");
			String vFORK1_ETC     		=	jrParam.getParam("FORK1_ETC");
			String vFORK2_SHIFT   		=	jrParam.getParam("FORK2_SHIFT");  
			String vFORK2_IN_SALE 		=	jrParam.getParam("FORK2_IN_SALE");
			String vFORK2_IN_SHORT		=	jrParam.getParam("FORK2_IN_SHORT");
			String vFORK2_IN_HEAT 		=	jrParam.getParam("FORK2_IN_HEAT");
			String vFORK2_OUT_SALE		=	jrParam.getParam("FORK2_OUT_SALE");
			String vFORK2_OUT_MOVE		=	jrParam.getParam("FORK2_OUT_MOVE");
			String vFORK2_ETC			=	jrParam.getParam("FORK2_ETC");
			String vFORK2_SHIFT_D   	=	jrParam.getParam("FORK2_SHIFT_D");  
			String vFORK2_IN_SALE_D 	=	jrParam.getParam("FORK2_IN_SALE_D");
			String vFORK2_IN_SHORT_D	=	jrParam.getParam("FORK2_IN_SHORT_D");
			String vFORK2_IN_HEAT_D 	=	jrParam.getParam("FORK2_IN_HEAT_D");
			String vFORK2_OUT_SALE_D	=	jrParam.getParam("FORK2_OUT_SALE_D");
			String vFORK2_OUT_MOVE_D	=	jrParam.getParam("FORK2_OUT_MOVE_D");
			String vFORK2_ETC_D			=	jrParam.getParam("FORK2_ETC_D");

			
			String queryId = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.updSlabYdDailyWork";
			
			oParam = new Object[] {
					vWORK_DATE
					,vYD_GP
					,vDUTY_GP1
					,vDUTY_CHG1
					,vDUTY_OT1
					,vDUTY_GP2
					,vDUTY_CHG2
					,vDUTY_OT2
					,vDUTY_GP3
					,vDUTY_CHG3
					,vDUTY_OT3
					,vCONTENTS
					,vSPECIFIC1
					,vSPECIFIC2
					,vSPECIFIC3
					,vNOTE_CRN1     	
					,vNOTE_CRN2     	
					,vNOTE_CRN3     	
					,vNOTE_FORK1D   	
					,vNOTE_FORK1N   	
					,vNOTE_FORK2D  
					,vNOTE_FORK2N 
					,vNOTE_RS1      	
					,vFORK1_SHIFT   	
					,vFORK1_IN_SALE 	
					,vFORK1_IN_SHORT	
					,vFORK1_IN_HEAT 	
					,vFORK1_OUT_SALE	
					,vFORK1_OUT_MOVE	
					,vFORK1_ETC     	
					,vFORK2_SHIFT   	
					,vFORK2_IN_SALE 	
					,vFORK2_IN_SHORT	
					,vFORK2_IN_HEAT 	
					,vFORK2_OUT_SALE	
					,vFORK2_OUT_MOVE	
					,vFORK2_ETC
					,vFORK2_SHIFT_D   
			        ,vFORK2_IN_SALE_D 
			        ,vFORK2_IN_SHORT_D
			        ,vFORK2_IN_HEAT_D 
			        ,vFORK2_OUT_SALE_D
			        ,vFORK2_OUT_MOVE_D
			        ,vFORK2_ETC_D
					,userId
					
			};

			//쿼리 실행
			result = assistantDAO.trtProcess(queryId, oParam);
			return jrParam;
			
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + ":" + e.getMessage(), e);
		}
	} //end of updSlabYdDailyWorkInfo 
	
	
	
	/**
	 *    [A] 오퍼레이션명 : 통합야드 업무일지 외부판매 재고현황 등록
	 * @param  GridData jrParam  parameter record
	 * @return GridData
	 * @throws DAOException 
	 */
	public GridData updSlabYdDailySalStk(GridData jrParam) throws DAOException {
		
		DBAssistantDAO assistantDAO = new DBAssistantDAO();
		String methodNm = "통합야드 업무일지 외부판매 재고현황 등록 [updSlabYdDailySalStk] : ";
		String logId = "updSlabYdDailySalStk";
		String trtNm = ",updSlabYdDailySalStk";
		int result = 0;
		
		try {
			Object oParam[]   = null;
			String vWORK_DATE = jrParam.getParam("WORK_DATE");
			String vYD_GP = jrParam.getParam("YD_GP");
			
			String vEXP_CNT_1 = jrParam.getParam("EXP_CNT_1");
			String vEXP_TON_1 = jrParam.getParam("EXP_TON_1");
			String vEXP_DMD_1 = jrParam.getParam("EXP_DMD_1");
			String vEXP_CNT_2 = jrParam.getParam("EXP_CNT_2");
			String vEXP_TON_2 = jrParam.getParam("EXP_TON_2");
			String vEXP_DMD_2 = jrParam.getParam("EXP_DMD_2");
			String vEXP_CNT_3 = jrParam.getParam("EXP_CNT_3");
			String vEXP_TON_3 = jrParam.getParam("EXP_TON_3");
			String vEXP_DMD_3 = jrParam.getParam("EXP_DMD_3");
			String vEXP_CNT_4 = jrParam.getParam("EXP_CNT_4");
			String vEXP_TON_4 = jrParam.getParam("EXP_TON_4");
			String vEXP_DMD_4 = jrParam.getParam("EXP_DMD_4");
			String vEXP_CNT_SUM = jrParam.getParam("EXP_CNT_SUM");
			String vEXP_TON_SUM = jrParam.getParam("EXP_TON_SUM");
			
			String vDOM_CNT_1 = jrParam.getParam("DOM_CNT_1");
			String vDOM_TON_1 = jrParam.getParam("DOM_TON_1");
			String vDOM_DMD_1 = jrParam.getParam("DOM_DMD_1");
			String vDOM_CNT_2 = jrParam.getParam("DOM_CNT_2");
			String vDOM_TON_2 = jrParam.getParam("DOM_TON_2");
			String vDOM_DMD_2 = jrParam.getParam("DOM_DMD_2");
			String vDOM_CNT_3 = jrParam.getParam("DOM_CNT_3");
			String vDOM_TON_3 = jrParam.getParam("DOM_TON_3");
			String vDOM_DMD_3 = jrParam.getParam("DOM_DMD_3");
			String vDOM_CNT_4 = jrParam.getParam("DOM_CNT_4");
			String vDOM_TON_4 = jrParam.getParam("DOM_TON_4");
			String vDOM_DMD_4 = jrParam.getParam("DOM_DMD_4");
			String vDOM_CNT_SUM = jrParam.getParam("DOM_CNT_SUM");
			String vDOM_TON_SUM = jrParam.getParam("DOM_TON_SUM");
			
			String userId = jrParam.getParam("userid");
			
			String queryId = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.updSlabYdDailySalStk";
			
			oParam = new Object[] {
					vWORK_DATE
					,vYD_GP
					,vEXP_CNT_1
					,vEXP_TON_1
					,vEXP_DMD_1
					,vEXP_CNT_2
					,vEXP_TON_2
					,vEXP_DMD_2
					,vEXP_CNT_3
					,vEXP_TON_3
					,vEXP_DMD_3
					,vEXP_CNT_4
					,vEXP_TON_4
					,vEXP_DMD_4
					,vEXP_CNT_SUM
					,vEXP_TON_SUM
					,vDOM_CNT_1
					,vDOM_TON_1
					,vDOM_DMD_1
					,vDOM_CNT_2
					,vDOM_TON_2
					,vDOM_DMD_2
					,vDOM_CNT_3
					,vDOM_TON_3
					,vDOM_DMD_3
					,vDOM_CNT_4
					,vDOM_TON_4
					,vDOM_DMD_4
					,vDOM_CNT_SUM
					,vDOM_TON_SUM
					,userId
			};

			//쿼리 실행
			result = assistantDAO.trtProcess(queryId, oParam);
			return jrParam;
			
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + ":" + e.getMessage(), e);
		}
	} //end of updSlabYdDailySalStk 
	
	
	/**
	 *    [A] 오퍼레이션명 : 통합야드 업무일지 판매재 적치 현황 기존데이터 업데이트
	 * @param  GridData jrParam  parameter record
	 * @return GridData
	 * @throws DAOException 
	 */
	public int updSlabYdDailySaleStk(GridData jrParam) throws DAOException {
		
		DBAssistantDAO assistantDAO = new DBAssistantDAO();
		String methodNm = "통합야드 업무일지 판매재 적치 현황 기존데이터 업데이트[updSlabYdDailySaleStk] : ";
		String logId = "updSlabYdDailySaleStk";
		String trtNm = "updSlabYdDailySaleStk";
		int result = 0;
		
		try {
			Object oParam[]   = null;
			String vWORK_DATE = jrParam.getParam("WORK_DATE");
			String vYD_GP = jrParam.getParam("YD_GP");
			String userId = jrParam.getParam("userid");
			
			//String
			
			String queryId = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.updSlabYdDailySaleStk";
			
			oParam = new Object[] {
					userId,
					vWORK_DATE,
					vYD_GP,
					
			};

			//쿼리 실행
			result = assistantDAO.trtProcess(queryId, oParam);
			return result;
			
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + ":" + e.getMessage(), e);
		}
	} //end of updSlabYdDailySaleStk 
	
	/**
	 *    [A] 오퍼레이션명 : 통합야드 업무일지 판매재 적치 현황 등록
	 * @param  GridData jrParam  parameter record
	 * @return GridData
	 * @throws DAOException 
	 */
	public int insSlabYdDailySaleStk(JDTORecord jrParam) throws DAOException {
		
		DBAssistantDAO assistantDAO = new DBAssistantDAO();
		String methodNm = "통합야드 업무일지 판매재 적치 현황 등록[insSlabYdDailySaleStk] : ";
		String logId = "insSlabYdDailySaleStk";
		String trtNm = ",insSlabYdDailySaleStk";
		int result = 0;
		
		try {
			Object oParam[]   = null;

			String vWORK_DATE = jrParam.getField("WORK_DATE").toString();
			String vYD_GP = jrParam.getField("YD_GP").toString();
			String userId = jrParam.getField("userid").toString();
			
			String vSaleGP     = jrParam.getField("SALE_GP").toString();
			String vDemander   = jrParam.getField("DEMANDER").toString();
			String vSpecAbbsym = jrParam.getField("SPEC_ABBSYM").toString();
			String vCnt        = jrParam.getField("CNT").toString();
			String vWt         = jrParam.getField("WT").toString();
			String vYdBayGp    = jrParam.getField("YD_BAY_GP").toString();
			
			
			String queryId = "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.insSlabYdDailySaleStk";
			
			oParam = new Object[] {
					 userId
					,userId
					,vYD_GP
					,vWORK_DATE
					,vSaleGP
					,vDemander   
					,vSpecAbbsym 
					,vCnt    
					,vWt        
					,vYdBayGp
			};

			//쿼리 실행
			result = assistantDAO.trtProcess(queryId, oParam);
			return result;
			
		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + ":" + e.getMessage(), e);
		}
	} //end of updSlabYdDailySaleStk 
	
	
	/**
	 *      [A] 오퍼레이션명 : 작업이력 SELECT parameter Check
	 * 
	 * @param  JDTORecord inRec  parameter record
	 *         int intGp                구분(0:STL_NO
	 *                                     
	 *                                     )
	 * @return boolean           true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkPara_getYdWrkHist(JDTORecord inRec, int intGp) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;

		try {
			if (intGp == 0) {
				
				szFieldName = "V_YD_WRK_HIST_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				
			} else if (intGp == 1) {
				szFieldName = "V_YD_CRN_SCH_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				
			}  else if (intGp == 2) {
				szFieldName = "V_YD_CRN_SCH_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 1, 'S', 0, 0);
				
			} else if (intGp == 3) {
				szFieldName = "V_YD_CRN_SCH_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 1, 'S', 0, 0);
				
			} else if (intGp == 4) {
				szFieldName = "V_YD_CRN_SCH_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 1, 'S', 0, 0);
				
			} else if (intGp == 5) {
				szFieldName = "V_YD_CRN_SCH_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 1, 'S', 0, 0);
				
			} else if (intGp == 6) {
				szFieldName = "V_YD_CAR_SCH_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
			} else if (intGp == 7) {
				szFieldName = "V_YD_TCAR_SCH_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
			} else if (intGp == 8) {
				szFieldName = "V_YD_WTCL_TNK_SCH_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
			} else if (intGp == 9) {
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 1, 'S', 0, 0);
			}  else if (intGp == 10) {
				
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				
				szFieldName = "V_YD_SCH_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				
				szFieldName = "V_DATE_FROM";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				
				szFieldName = "V_DATE_TO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				
				szFieldName = "V_YD_WRK_DUTY";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			
				szFieldName = "V_PAGE_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9,  1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9,  1, 'R', 0, 0);
				if (!blnErr) return blnErr;
				
			}  else if (intGp == 11 || intGp == 12) {
				
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);				
			
				szFieldName = "V_DATE_FROM";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				
				szFieldName = "V_DATE_TO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				
				szFieldName = "V_YD_WRK_DUTY";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				
				szFieldName = "V_YD_AID_WRK_YN";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				
				szFieldName = "V_YD_AID_RT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				
				szFieldName = "V_STL_PROG_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
								
				
				szFieldName = "V_YD_GNT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);
				
				szFieldName = "V_YD_EQP_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				
				szFieldName = "V_PAGE_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9,  1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9,  1, 'R', 0, 0);
				if (!blnErr) return blnErr;
				
			} else if (intGp == 13 || intGp == 14 || intGp == 15) {
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);				
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			
				szFieldName = "V_YD_WRK_FR_DD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				
				szFieldName = "V_YD_WRK_TO_DD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
			} else if (intGp == 20 || intGp == 21) {
				szFieldName = "V_DATE_FROM";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
			}
			
		}catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} // end of chkPara_getYdStock
	
	
	
/*------------------------------------- INSERT -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드저장품 INSERT
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int insYdWrkHist(JDTORecord inRec) throws DAOException, JDTOException {
		DBAssistantDAO assistantDAO = new DBAssistantDAO();
		YdDaoUtils ydDaoUtils       = new YdDaoUtils();
		JDTORecord jRecordParam     = inRec;
		Object oParam[]             = null;
		int intRtnVal               = 0;
		String szMethodName         = "insYdWrkHist";
		String szMsg                = "";
		
		try {			

			oParam = new Object[] {
				//	ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WRK_HIST_ID")
				//	,ydDaoUtils.paraRecChkNull(jRecordParam, "REGISTER")
				//	,ydDaoUtils.paraRecChkNull(jRecordParam, "REG_DDTT")
				//	 ydDaoUtils.paraRecChkNull(jRecordParam, "MODIFIER")
			    //  ,ydDaoUtils.paraRecChkNull(jRecordParam, "MOD_DDTT")
				//	,ydDaoUtils.paraRecChkNull(jRecordParam, "DEL_YN")
					 ydDaoUtils.paraRecChkNull(jRecordParam, "YD_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "STL_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_GNT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "BUY_SLAB_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "STL_APPEAR_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ITEMNAME_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_YEOJAE_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_DTL")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "STLKIND_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SPEC_ABBSYM")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "CUST_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "DEST_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "DEMANDER_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "DEST_TEL_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "STL_PROG_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "GOODS_GRADE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_W_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_T_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_L_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_T")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_W")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_L")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_WT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_COIL_OUTDIA_GRP_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "COIL_INDIA")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "COIL_OUTDIA")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SLAB_WO_RT_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_HCR_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "HCR_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SCARFING_YN")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SCARFING_DONE_YN")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "REHEAT_SLAB_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ROLL_UNIT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ROLL_UNIT_NAME")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "REFUR_CHG_LOT_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "REFUR_CHG_PLN_SERNO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CONVEYOR_BRANCH_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "HYSCO_TRANS_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "COOL_METHOD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "COOL_DONE_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "RENTPROC_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "DIST_DUE_DATE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_PILING_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_BOOK_OUT_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "PL_RCPT_LN_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "FRTOMOVE_ORD_DATE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "URGENT_FRTOMOVE_WORD_GP")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "SPOS_WLOC_CD")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "ARR_WLOC_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_AIM_RT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_AIM_BAY_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_STK_LOT_TP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_STK_LOT_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "TRANS_ORD_DATE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "PL_L2_TRK_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "DIST_SHIPASSIGN_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "EXPORT_SHIP_SET_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SHIPASSIGN_WORD_DATE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SHIPASSIGN_WORD_SEQNO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SHIP_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SHIP_NAME")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "RSHP_HOLD_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "BERTH_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SAILNO")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CAR_USE_GP")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "CAR_NO")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "TRN_EQP_CD")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "CAR_KIND")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "TRANS_EQUIPMENT_TYPE")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "CARD_NO")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CAR_SCH_ID")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_TCAR_SCH_ID")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WTCL_TNK_SCH_ID")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WBOOK_ID")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CRN_SCH_ID")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_SCH_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_SCH_ST_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_SCH_REQ_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_SCH_PRIOR")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WBOOK_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_AID_WRK_YN")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_TO_LOC_DCSN_MTD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_TO_LOC_GUIDE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_SCH_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WORD_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_WO_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_WO_LAYER")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_WR_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_WR_LAYER")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_WRK_ACT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_CMPL_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_WO_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_WO_LAYER")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_WR_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_WR_LAYER")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_WRK_ACT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_CMPL_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WRK_HDS_DD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WRK_DUTY")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WRK_PARTY")
					/*,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_LEV_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_LEV_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_PNT_WO_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_PNT_CD1")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_PNT_CD2")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_WRK_BOOK_ID")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_SCH_REQ_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_STOP_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_ARR_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_ST_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_CMPL_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_WRK_ACT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_CHK_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_LEV_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_PNT_WO_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_PNT_CD3")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_PNT_CD4")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_WRK_BOOK_ID")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_STOP_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_SCH_REQ_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_ARR_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_CHK_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_ST_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_CMPL_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_WRK_ACT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_TRN_WRK_DELY_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "WTIN_ST_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "WTIN_END_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "WTOUT_ST_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "WTOUT_END_DT")*/
					
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_EQP_ID")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_EQP_ID")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_EQP_ID")
			};

			// INSERT 쿼리 실행
			intRtnVal  = assistantDAO.trtProcess(szQueryIdIns1, oParam);
			if(intRtnVal <= 0){
				szMsg = "INSERT 처리 실패 (" + intRtnVal + ")";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
				return intRtnVal = -1;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of insYdStock
	

	/*------------------------------------- INSERT -------------------------------------------*/
		
		/**
		 *      [A] 오퍼레이션명 : 야드저장품 INSERT
		 * 
		 * @param JDTORecord inRec parameter record
		 * @return int             execution count, -2:parameter error
		 * @throws DAOException
		 * @throws JDTOException 
		 */		
		public int insYdWrkHistYD(JDTORecord inRec) throws DAOException, JDTOException {
			DBAssistantDAO assistantDAO = new DBAssistantDAO();
			YdDaoUtils ydDaoUtils       = new YdDaoUtils();
			JDTORecord jRecordParam     = inRec;
			Object oParam[]             = null;
			int intRtnVal               = 0;
			String szMethodName         = "insYdWrkHistYD";
			String szMsg                = "";
			
			try {			

				oParam = new Object[] {
					//	ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WRK_HIST_ID")
					//	,ydDaoUtils.paraRecChkNull(jRecordParam, "REGISTER")
					//	,ydDaoUtils.paraRecChkNull(jRecordParam, "REG_DDTT")
					//	 ydDaoUtils.paraRecChkNull(jRecordParam, "MODIFIER")
				    //  ,ydDaoUtils.paraRecChkNull(jRecordParam, "MOD_DDTT")
					//	,ydDaoUtils.paraRecChkNull(jRecordParam, "DEL_YN")
						 ydDaoUtils.paraRecChkNull(jRecordParam, "YD_GP")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "STL_NO")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_GNT_GP")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "BUY_SLAB_NO")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "STL_APPEAR_GP")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "ITEMNAME_CD")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_YEOJAE_GP")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_NO")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_DTL")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "STLKIND_CD")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "SPEC_ABBSYM")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_GP")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "CUST_CD")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "DEST_CD")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "DEMANDER_CD")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "DEST_TEL_NO")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "STL_PROG_CD")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "GOODS_GRADE")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_W_GP")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_T_GP")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_L_GP")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_T")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_W")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_L")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_WT")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_COIL_OUTDIA_GRP_GP")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "COIL_INDIA")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "COIL_OUTDIA")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "SLAB_WO_RT_CD")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_HCR_GP")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "HCR_GP")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "SCARFING_YN")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "SCARFING_DONE_YN")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "REHEAT_SLAB_GP")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "ROLL_UNIT_GP")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "ROLL_UNIT_NAME")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "REFUR_CHG_LOT_NO")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "REFUR_CHG_PLN_SERNO")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CONVEYOR_BRANCH_CD")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "HYSCO_TRANS_GP")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "COOL_METHOD")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "COOL_DONE_GP")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "RENTPROC_CD")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "DIST_DUE_DATE")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_PILING_CD")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_BOOK_OUT_LOC")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "PL_RCPT_LN_GP")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "FRTOMOVE_ORD_DATE")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "URGENT_FRTOMOVE_WORD_GP")
						//,ydDaoUtils.paraRecChkNull(jRecordParam, "SPOS_WLOC_CD")
						//,ydDaoUtils.paraRecChkNull(jRecordParam, "ARR_WLOC_CD")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_AIM_RT_GP")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_AIM_BAY_GP")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_STK_LOT_TP")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_STK_LOT_CD")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "TRANS_ORD_DATE")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "PL_L2_TRK_NO")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "DIST_SHIPASSIGN_GP")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "EXPORT_SHIP_SET_NO")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "SHIPASSIGN_WORD_DATE")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "SHIPASSIGN_WORD_SEQNO")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "SHIP_CD")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "SHIP_NAME")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "RSHP_HOLD_NO")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "BERTH_NO")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "SAILNO")
						//,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CAR_USE_GP")
						//,ydDaoUtils.paraRecChkNull(jRecordParam, "CAR_NO")
						//,ydDaoUtils.paraRecChkNull(jRecordParam, "TRN_EQP_CD")
						//,ydDaoUtils.paraRecChkNull(jRecordParam, "CAR_KIND")
						//,ydDaoUtils.paraRecChkNull(jRecordParam, "TRANS_EQUIPMENT_TYPE")
						//,ydDaoUtils.paraRecChkNull(jRecordParam, "CARD_NO")
						//,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CAR_SCH_ID")
						//,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_TCAR_SCH_ID")
						//,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WTCL_TNK_SCH_ID")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WBOOK_ID")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CRN_SCH_ID")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_SCH_CD")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_SCH_ST_GP")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_SCH_REQ_GP")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_SCH_PRIOR")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WBOOK_DT")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_AID_WRK_YN")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_TO_LOC_DCSN_MTD")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_TO_LOC_GUIDE")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_SCH_DT")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WORD_DT")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_WO_LOC")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_WO_LAYER")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_WR_LOC")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_WR_LAYER")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_WRK_ACT_GP")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_CMPL_DT")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_WO_LOC")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_WO_LAYER")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_WR_LOC")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_WR_LAYER")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_WRK_ACT_GP")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_CMPL_DT")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WRK_HDS_DD")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WRK_DUTY")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WRK_PARTY")
						/*,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_LEV_LOC")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_LEV_DT")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_PNT_WO_DT")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_PNT_CD1")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_PNT_CD2")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_WRK_BOOK_ID")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_SCH_REQ_GP")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_STOP_LOC")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_ARR_DT")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_ST_DT")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_CMPL_DT")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_WRK_ACT_GP")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_CHK_DT")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_LEV_DT")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_PNT_WO_DT")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_PNT_CD3")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_PNT_CD4")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_WRK_BOOK_ID")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_STOP_LOC")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_SCH_REQ_GP")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_ARR_DT")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_CHK_DT")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_ST_DT")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_CMPL_DT")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_WRK_ACT_GP")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_TRN_WRK_DELY_CD")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "WTIN_ST_DT")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "WTIN_END_DT")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "WTOUT_ST_DT")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "WTOUT_END_DT")*/
						
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_EQP_ID")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_WRK_MODE2")
						,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_WRK_MODE2")
				};

				// INSERT 쿼리 실행
				intRtnVal  = assistantDAO.trtProcess(szQueryIdIns10, oParam);
				if(intRtnVal <= 0){
					szMsg = "INSERT 처리 실패 (" + intRtnVal + ")";
					ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
					return intRtnVal = -1;
				}
			} catch (Exception e) {
				// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
				throw new DAOException(szDaoName + e.getMessage(), e);
			}
			return intRtnVal;
		} // end of insYdStock
		
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드저장품 INSERT
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int insYdWrkHistS(JDTORecord inRec) throws DAOException, JDTOException {
		DBAssistantDAO assistantDAO = new DBAssistantDAO();
		YdDaoUtils ydDaoUtils       = new YdDaoUtils();
		JDTORecord jRecordParam     = inRec;
		JDTORecord recPara 			= null;
		int intRtnVal               = 0;
		String szMethodName         = "insYdWrkHistS";
		String szMsg                = "";
		boolean blnChk_Field = true;
		
		try {			

			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkParameter(recPara);
			
			//parameter error return
			if (!blnChk_Field)
				return intRtnVal = -2;
	
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", szQueryIdIns6);
	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of insYdWrkHistS
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드저장품 INSERT
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int insYdWrkHistSC(JDTORecord inRec) throws DAOException, JDTOException {
		DBAssistantDAO assistantDAO = new DBAssistantDAO();
		YdDaoUtils ydDaoUtils       = new YdDaoUtils();
		JDTORecord jRecordParam     = inRec;
		JDTORecord recPara 			= null;
		int intRtnVal               = 0;
		String szMethodName         = "insYdWrkHistSC";
		String szMsg                = "";
		boolean blnChk_Field = true;
		
		try {			

			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkParameter(recPara);
			
			//parameter error return
			if (!blnChk_Field)
				return intRtnVal = -2;
	
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", szQueryIdIns9);
	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of insYdWrkHistS
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드COIL저장품 INSERT
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int insYdCoilWrkHist(JDTORecord inRec) throws DAOException, JDTOException {
		DBAssistantDAO assistantDAO = new DBAssistantDAO();
		YdDaoUtils ydDaoUtils       = new YdDaoUtils();
		JDTORecord jRecordParam     = inRec;
		Object oParam[]             = null;
		int intRtnVal               = 0;
		String szMethodName         = "insYdCoilWrkHist";
		String szMsg                = "";
		
		try {			

			oParam = new Object[] {
				//	ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WRK_HIST_ID")
				//	,ydDaoUtils.paraRecChkNull(jRecordParam, "REGISTER")
				//	,ydDaoUtils.paraRecChkNull(jRecordParam, "REG_DDTT")
					 ydDaoUtils.paraRecChkNull(jRecordParam, "MODIFIER")
			    //  ,ydDaoUtils.paraRecChkNull(jRecordParam, "MOD_DDTT")
				//	,ydDaoUtils.paraRecChkNull(jRecordParam, "DEL_YN")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "STL_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_GNT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "BUY_SLAB_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "STL_APPEAR_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ITEMNAME_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_YEOJAE_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_DTL")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "STLKIND_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SPEC_ABBSYM")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "CUST_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "DEST_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "DEMANDER_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "DEST_TEL_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "STL_PROG_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "GOODS_GRADE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_W_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_T_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_L_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_T")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_W")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_L")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_WT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_COIL_OUTDIA_GRP_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "COIL_INDIA")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "COIL_OUTDIA")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SLAB_WO_RT_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_HCR_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "HCR_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SCARFING_YN")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SCARFING_DONE_YN")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "REHEAT_SLAB_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ROLL_UNIT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ROLL_UNIT_NAME")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "REFUR_CHG_LOT_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "REFUR_CHG_PLN_SERNO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CONVEYOR_BRANCH_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "HYSCO_TRANS_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "COOL_METHOD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "COOL_DONE_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "RENTPROC_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "DIST_DUE_DATE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_PILING_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_BOOK_OUT_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "PL_RCPT_LN_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "FRTOMOVE_ORD_DATE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "URGENT_FRTOMOVE_WORD_GP")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "SPOS_WLOC_CD")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "ARR_WLOC_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_AIM_RT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_AIM_BAY_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_STK_LOT_TP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_STK_LOT_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "TRANS_ORD_DATE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "PL_L2_TRK_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "DIST_SHIPASSIGN_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "EXPORT_SHIP_SET_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SHIPASSIGN_WORD_DATE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SHIPASSIGN_WORD_SEQNO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SHIP_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SHIP_NAME")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "RSHP_HOLD_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "BERTH_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SAILNO")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CAR_USE_GP")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "CAR_NO")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "TRN_EQP_CD")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "CAR_KIND")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "TRANS_EQUIPMENT_TYPE")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "CARD_NO")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CAR_SCH_ID")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_TCAR_SCH_ID")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WTCL_TNK_SCH_ID")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WBOOK_ID")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CRN_SCH_ID")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_SCH_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_SCH_ST_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_SCH_REQ_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_SCH_PRIOR")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WBOOK_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_AID_WRK_YN")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_TO_LOC_DCSN_MTD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_TO_LOC_GUIDE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_SCH_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WORD_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_WO_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_WO_LAYER")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_WR_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_WR_LAYER")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_WRK_ACT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_CMPL_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_WO_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_WO_LAYER")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_WR_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_WR_LAYER")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_WRK_ACT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_CMPL_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WRK_HDS_DD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WRK_DUTY")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WRK_PARTY")
					/*,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_LEV_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_LEV_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_PNT_WO_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_PNT_CD1")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_PNT_CD2")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_WRK_BOOK_ID")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_SCH_REQ_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_STOP_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_ARR_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_ST_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_CMPL_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_WRK_ACT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_CHK_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_LEV_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_PNT_WO_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_PNT_CD3")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_PNT_CD4")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_WRK_BOOK_ID")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_STOP_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_SCH_REQ_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_ARR_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_CHK_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_ST_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_CMPL_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_WRK_ACT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_TRN_WRK_DELY_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "WTIN_ST_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "WTIN_END_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "WTOUT_ST_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "WTOUT_END_DT")*/
					
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_EQP_ID")
			};

			// INSERT 쿼리 실행
			intRtnVal  = assistantDAO.trtProcess(szQueryIdIns5, oParam);
			if(intRtnVal <= 0){
				szMsg = "INSERT 처리 실패 (" + intRtnVal + ")";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
				return intRtnVal = -1;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of insYdCoilWrkHist
	
	/**
	 *      [A] 오퍼레이션명 : 야드저장품 INSERT
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int insYdWrkHist1(JDTORecord inRec) throws DAOException, JDTOException {
		DBAssistantDAO assistantDAO = new DBAssistantDAO();
		YdDaoUtils ydDaoUtils       = new YdDaoUtils();
		JDTORecord jRecordParam     = inRec;
		Object oParam[]             = null;
		int intRtnVal               = 0;
		String szMethodName         = "insYdWrkHist1";
		String szMsg                = "";
		
		try {			

			oParam = new Object[] {
				//	ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WRK_HIST_ID")
				//	,ydDaoUtils.paraRecChkNull(jRecordParam, "REGISTER")
				//	,ydDaoUtils.paraRecChkNull(jRecordParam, "REG_DDTT")
				//	,ydDaoUtils.paraRecChkNull(jRecordParam, "MODIFIER")
			    //  ,ydDaoUtils.paraRecChkNull(jRecordParam, "MOD_DDTT")
				//	,ydDaoUtils.paraRecChkNull(jRecordParam, "DEL_YN")
					ydDaoUtils.paraRecChkNull(jRecordParam, "YD_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "STL_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_GNT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "BUY_SLAB_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "STL_APPEAR_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ITEMNAME_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_YEOJAE_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_DTL")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "STLKIND_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SPEC_ABBSYM")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "CUST_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "DEST_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "DEMANDER_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "DEST_TEL_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "STL_PROG_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "GOODS_GRADE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_W_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_T_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_L_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_T")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_W")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_L")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_WT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_COIL_OUTDIA_GRP_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "COIL_INDIA")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "COIL_OUTDIA")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SLAB_WO_RT_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_HCR_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "HCR_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SCARFING_YN")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SCARFING_DONE_YN")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "REHEAT_SLAB_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ROLL_UNIT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ROLL_UNIT_NAME")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "REFUR_CHG_LOT_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "REFUR_CHG_PLN_SERNO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CONVEYOR_BRANCH_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "HYSCO_TRANS_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "COOL_METHOD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "COOL_DONE_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "RENTPROC_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "DIST_DUE_DATE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_PILING_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_BOOK_OUT_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "PL_RCPT_LN_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "FRTOMOVE_ORD_DATE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "URGENT_FRTOMOVE_WORD_GP")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "SPOS_WLOC_CD")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "ARR_WLOC_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_AIM_RT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_AIM_BAY_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_STK_LOT_TP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_STK_LOT_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "TRANS_ORD_DATE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "PL_L2_TRK_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "DIST_SHIPASSIGN_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "EXPORT_SHIP_SET_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SHIPASSIGN_WORD_DATE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SHIPASSIGN_WORD_SEQNO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SHIP_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SHIP_NAME")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "RSHP_HOLD_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "BERTH_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SAILNO")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CAR_USE_GP")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "CAR_NO")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "TRN_EQP_CD")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "CAR_KIND")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "TRANS_EQUIPMENT_TYPE")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "CARD_NO")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CAR_SCH_ID")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_TCAR_SCH_ID")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WTCL_TNK_SCH_ID")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WBOOK_ID")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CRN_SCH_ID")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_SCH_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_SCH_ST_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_SCH_REQ_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_SCH_PRIOR")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WBOOK_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_AID_WRK_YN")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_TO_LOC_DCSN_MTD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_TO_LOC_GUIDE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_SCH_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WORD_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_WO_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_WO_LAYER")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_WR_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_WR_LAYER")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_WRK_ACT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_CMPL_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_WO_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_WO_LAYER")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_WR_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_WR_LAYER")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_WRK_ACT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_CMPL_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WRK_HDS_DD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WRK_DUTY")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WRK_PARTY")
					/*,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_LEV_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_LEV_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_PNT_WO_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_PNT_CD1")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_PNT_CD2")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_WRK_BOOK_ID")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_SCH_REQ_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_STOP_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_ARR_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_ST_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_CMPL_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_WRK_ACT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_CHK_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_LEV_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_PNT_WO_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_PNT_CD3")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_PNT_CD4")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_WRK_BOOK_ID")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_STOP_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_SCH_REQ_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_ARR_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_CHK_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_ST_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_CMPL_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_WRK_ACT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_TRN_WRK_DELY_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "WTIN_ST_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "WTIN_END_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "WTOUT_ST_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "WTOUT_END_DT")*/
					
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_EQP_ID") 
			};

			// INSERT 쿼리 실행
			intRtnVal  = assistantDAO.trtProcess(szQueryIdIns4, oParam);
			if(intRtnVal <= 0){
				szMsg = "INSERT 처리 실패 (" + intRtnVal + ")";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
				return intRtnVal = -1;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of insYdStock
	
	

	
	/**
	 *      [A] 오퍼레이션명 : 야드저장품 INSERT(저장위치 변경시 INSERT QUERY)
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int insYdWrkHistPosFix(JDTORecord inRec) throws DAOException, JDTOException {
		DBAssistantDAO assistantDAO = new DBAssistantDAO();
		YdDaoUtils ydDaoUtils       = new YdDaoUtils();
		JDTORecord jRecordParam     = inRec;
		Object oParam[]             = null;
		int intRtnVal               = 0;
		String szMethodName         = "insYdWrkHistPosFix";
		String szMsg                = "";
		
		try {			

			oParam = new Object[] {
				//	ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WRK_HIST_ID")
					ydDaoUtils.paraRecChkNull(jRecordParam, "MODIFIER")
				//	,ydDaoUtils.paraRecChkNull(jRecordParam, "REG_DDTT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "MODIFIER")
			    //  ,ydDaoUtils.paraRecChkNull(jRecordParam, "MOD_DDTT")
				//	,ydDaoUtils.paraRecChkNull(jRecordParam, "DEL_YN")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "STL_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_GNT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "BUY_SLAB_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "STL_APPEAR_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ITEMNAME_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_YEOJAE_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_DTL")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "STLKIND_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SPEC_ABBSYM")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "CUST_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "DEST_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "DEMANDER_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "DEST_TEL_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "STL_PROG_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "GOODS_GRADE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_W_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_T_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_L_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_T")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_W")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_L")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_WT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_COIL_OUTDIA_GRP_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "COIL_INDIA")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "COIL_OUTDIA")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SLAB_WO_RT_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_HCR_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "HCR_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SCARFING_YN")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SCARFING_DONE_YN")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "REHEAT_SLAB_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ROLL_UNIT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ROLL_UNIT_NAME")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "REFUR_CHG_LOT_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "REFUR_CHG_PLN_SERNO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CONVEYOR_BRANCH_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "HYSCO_TRANS_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "COOL_METHOD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "COOL_DONE_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "RENTPROC_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "DIST_DUE_DATE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_PILING_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_BOOK_OUT_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "PL_RCPT_LN_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "FRTOMOVE_ORD_DATE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "URGENT_FRTOMOVE_WORD_GP")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "SPOS_WLOC_CD")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "ARR_WLOC_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_AIM_RT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_AIM_BAY_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_STK_LOT_TP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_STK_LOT_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "TRANS_ORD_DATE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "PL_L2_TRK_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "DIST_SHIPASSIGN_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "EXPORT_SHIP_SET_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SHIPASSIGN_WORD_DATE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SHIPASSIGN_WORD_SEQNO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SHIP_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SHIP_NAME")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "RSHP_HOLD_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "BERTH_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SAILNO")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CAR_USE_GP")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "CAR_NO")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "TRN_EQP_CD")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "CAR_KIND")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "TRANS_EQUIPMENT_TYPE")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "CARD_NO")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CAR_SCH_ID")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_TCAR_SCH_ID")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WTCL_TNK_SCH_ID")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WBOOK_ID")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CRN_SCH_ID")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_SCH_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_SCH_ST_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_SCH_REQ_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_SCH_PRIOR")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WBOOK_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_AID_WRK_YN")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_TO_LOC_DCSN_MTD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_TO_LOC_GUIDE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_SCH_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WORD_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_WO_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_WO_LAYER")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_WR_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_WR_LAYER")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_WRK_ACT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_CMPL_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_WO_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_WO_LAYER")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_WR_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_WR_LAYER")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_WRK_ACT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_CMPL_DT")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WRK_HDS_DD")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WRK_DUTY")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WRK_PARTY")
					/*,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_LEV_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_LEV_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_PNT_WO_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_PNT_CD1")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_PNT_CD2")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_WRK_BOOK_ID")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_SCH_REQ_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_STOP_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_ARR_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_ST_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_CMPL_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_WRK_ACT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_CHK_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_LEV_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_PNT_WO_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_PNT_CD3")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_PNT_CD4")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_WRK_BOOK_ID")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_STOP_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_SCH_REQ_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_ARR_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_CHK_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_ST_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_CMPL_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_WRK_ACT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_TRN_WRK_DELY_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "WTIN_ST_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "WTIN_END_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "WTOUT_ST_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "WTOUT_END_DT")*/
					
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_EQP_ID")
			};

			// INSERT 쿼리 실행
			intRtnVal  = assistantDAO.trtProcess(szQueryIdIns2, oParam);
			if(intRtnVal <= 0){
				szMsg = "INSERT 처리 실패 (" + intRtnVal + ")";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
				return intRtnVal = -1;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of insYdStock
	
	
	/**
	 *      [A] 오퍼레이션명 : 작업이력 INSERT(차량스케줄 재료등록시 INSERT QUERY)
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int insYdWrkHistCarSch(JDTORecord inRec) throws DAOException, JDTOException {
		DBAssistantDAO assistantDAO = new DBAssistantDAO();
		YdDaoUtils ydDaoUtils       = new YdDaoUtils();
		JDTORecord jRecordParam     = inRec;
		Object oParam[]             = null;
		int intRtnVal               = 0;
		String szMethodName         = "insYdWrkHistPosFix";
		String szMsg                = "";
		
		try {			

			oParam = new Object[] {
				//	ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WRK_HIST_ID")
				//	,ydDaoUtils.paraRecChkNull(jRecordParam, "REGISTER")
				//	,ydDaoUtils.paraRecChkNull(jRecordParam, "REG_DDTT")
				//	,ydDaoUtils.paraRecChkNull(jRecordParam, "MODIFIER")
			    //  ,ydDaoUtils.paraRecChkNull(jRecordParam, "MOD_DDTT")
				//	,ydDaoUtils.paraRecChkNull(jRecordParam, "DEL_YN")
					ydDaoUtils.paraRecChkNull(jRecordParam, "YD_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "STL_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_GNT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "BUY_SLAB_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "STL_APPEAR_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ITEMNAME_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_YEOJAE_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_DTL")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "STLKIND_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SPEC_ABBSYM")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "CUST_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "DEST_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "DEMANDER_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "DEST_TEL_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "STL_PROG_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "GOODS_GRADE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_W_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_T_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_L_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_T")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_W")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_L")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_WT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_COIL_OUTDIA_GRP_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "COIL_INDIA")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "COIL_OUTDIA")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SLAB_WO_RT_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_HCR_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "HCR_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SCARFING_YN")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SCARFING_DONE_YN")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "REHEAT_SLAB_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ROLL_UNIT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ROLL_UNIT_NAME")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "REFUR_CHG_LOT_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "REFUR_CHG_PLN_SERNO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CONVEYOR_BRANCH_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "HYSCO_TRANS_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "COOL_METHOD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "COOL_DONE_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "RENTPROC_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "DIST_DUE_DATE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_PILING_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_BOOK_OUT_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "PL_RCPT_LN_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "FRTOMOVE_ORD_DATE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "URGENT_FRTOMOVE_WORD_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SPOS_WLOC_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ARR_WLOC_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_AIM_RT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_AIM_BAY_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_STK_LOT_TP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_STK_LOT_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "TRANS_ORD_DATE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "PL_L2_TRK_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "DIST_SHIPASSIGN_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "EXPORT_SHIP_SET_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SHIPASSIGN_WORD_DATE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SHIPASSIGN_WORD_SEQNO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SHIP_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SHIP_NAME")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "RSHP_HOLD_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "BERTH_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SAILNO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CAR_USE_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "CAR_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "TRN_EQP_CD")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "CAR_KIND")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "TRANS_EQUIPMENT_TYPE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "CARD_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CAR_SCH_ID")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_TCAR_SCH_ID")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WTCL_TNK_SCH_ID")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WBOOK_ID")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CRN_SCH_ID")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_SCH_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_SCH_ST_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_SCH_REQ_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_SCH_PRIOR")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WBOOK_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_AID_WRK_YN")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_TO_LOC_DCSN_MTD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_TO_LOC_GUIDE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_SCH_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WORD_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_WO_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_WO_LAYER")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_WR_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_WR_LAYER")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_WRK_ACT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_CMPL_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_WO_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_WO_LAYER")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_WR_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_WR_LAYER")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_WRK_ACT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_CMPL_DT")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WRK_HDS_DD")
					//,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WRK_DUTY")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WRK_PARTY")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_LEV_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_LEV_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_PNT_WO_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_PNT_CD1")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_PNT_CD2")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_WRK_BOOK_ID")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_SCH_REQ_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_STOP_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_ARR_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_ST_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_CMPL_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_WRK_ACT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_CHK_DT")
					/*
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_LEV_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_PNT_WO_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_PNT_CD3")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_PNT_CD4")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_WRK_BOOK_ID")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_STOP_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_SCH_REQ_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_ARR_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_CHK_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_ST_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_CMPL_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_WRK_ACT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_TRN_WRK_DELY_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "WTIN_ST_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "WTIN_END_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "WTOUT_ST_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "WTOUT_END_DT")
					*/
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_EQP_ID")
			};

			// INSERT 쿼리 실행
			intRtnVal  = assistantDAO.trtProcess(szQueryIdIns3, oParam);
			if(intRtnVal <= 0){
				szMsg = "INSERT 처리 실패 (" + intRtnVal + ")";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
				return intRtnVal = -1;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of insYdStock
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드저장품 INSERT parameter Check
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return boolean         true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkParameter(JDTORecord inRec) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		
		try {
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} //end of chkParameter
		
	
/*------------------------------------- UPDATE -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 :작업 이력 UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0:STL_NO)
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdWrkHist(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		DBAssistantDAO assistantDAO = new DBAssistantDAO();
		YdUtils ydUtils             = new YdUtils();
		JDTORecordSet outRecSet     = null;
		JDTORecord recInPara        = null;
		JDTORecord recOutPara       = null;
		JDTORecord outRec           = null;
		JDTORecord recPara          = null;
		JDTORecord jRecordParam     = null;
		Object oParam[]             = null;
		String szMethodName         = "updYdWrkHist";
		String szMsg                = "";
		int intRtnVal               = 0;

		try {
			// RecordSet Create
			outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			
			// 필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			// Update Data Select
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CRN_SCH_ID", inRec.getFieldString("YD_CRN_SCH_ID"));
			recPara.setField("STL_NO", inRec.getFieldString("STL_NO"));
			
			intRtnVal = this.getYdWrkHist(recPara, outRecSet, 0);
			
			if(intRtnVal < 0) {
				// Parameter Error Return
				szMsg = "Parameter Error!";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
				return intRtnVal;
			} else if(intRtnVal == 0) {
				// Data Not Found Return
				szMsg = "Data Not Found!";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
				return intRtnVal;
			} else if(intRtnVal != 1) {
				// Duplicate Data Return
				szMsg = "Duplicate Data!";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
				return intRtnVal = -1;
			}
			
			outRecSet.absolute(1);
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setRecord(outRecSet.getRecord());

			// 필드명 변환 (필드명 -> V_필드명)		
			recOutPara = ydDaoUtils.conversionFieldname(outRec, 0);
			
			// Data Mapping
			this.dataMapping(recInPara, recOutPara);
			

			jRecordParam = recOutPara;

			oParam = new Object[] {
			//		 ydDaoUtils.paraRecChkNull(jRecordParam, "REGISTER")
			//		,ydDaoUtils.paraRecChkNull(jRecordParam, "REG_DDTT")
					 ydDaoUtils.paraRecChkNull(jRecordParam, "MODIFIER")
			//		,ydDaoUtils.paraRecChkNull(jRecordParam, "MOD_DDTT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "DEL_YN")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "STL_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_GNT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "BUY_SLAB_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "STL_APPEAR_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ITEMNAME_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_YEOJAE_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_DTL")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "STLKIND_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SPEC_ABBSYM")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "CUST_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "DEST_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "DEMANDER_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "DEST_TEL_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "STL_PROG_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "GOODS_GRADE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_W_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_T_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_L_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_T")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_W")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_L")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_WT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_COIL_OUTDIA_GRP_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "COIL_INDIA")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "COIL_OUTDIA")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SLAB_WO_RT_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_HCR_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "HCR_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SCARFING_YN")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SCARFING_DONE_YN")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "REHEAT_SLAB_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ROLL_UNIT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ROLL_UNIT_NAME")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "REFUR_CHG_LOT_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "REFUR_CHG_PLN_SERNO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CONVEYOR_BRANCH_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "HYSCO_TRANS_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "COOL_METHOD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "COOL_DONE_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "RENTPROC_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "DIST_DUE_DATE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_PILING_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_BOOK_OUT_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "PL_RCPT_LN_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "FRTOMOVE_ORD_DATE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "URGENT_FRTOMOVE_WORD_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SPOS_WLOC_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ARR_WLOC_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_AIM_RT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_AIM_BAY_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_STK_LOT_TP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_STK_LOT_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "TRANS_ORD_DATE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "PL_L2_TRK_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "DIST_SHIPASSIGN_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "EXPORT_SHIP_SET_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SHIPASSIGN_WORD_DATE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SHIPASSIGN_WORD_SEQNO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SHIP_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SHIP_NAME")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "RSHP_HOLD_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "BERTH_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SAILNO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CAR_USE_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "CAR_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "TRN_EQP_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "CAR_KIND")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "TRANS_EQUIPMENT_TYPE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "CARD_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CAR_SCH_ID")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_TCAR_SCH_ID")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WTCL_TNK_SCH_ID")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WBOOK_ID")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CRN_SCH_ID")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_SCH_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_SCH_ST_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_SCH_REQ_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_SCH_PRIOR")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WBOOK_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_AID_WRK_YN")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_TO_LOC_DCSN_MTD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_TO_LOC_GUIDE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_SCH_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WORD_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_WO_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_WO_LAYER")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_WR_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_WR_LAYER")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_WRK_ACT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_UP_CMPL_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_WO_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_WO_LAYER")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_WR_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_WR_LAYER")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_WRK_ACT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DN_CMPL_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WRK_HDS_DD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WRK_DUTY")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WRK_PARTY")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_LEV_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_LEV_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_PNT_WO_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_PNT_CD1")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_PNT_CD2")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_WRK_BOOK_ID")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_SCH_REQ_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_STOP_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_ARR_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_ST_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_CMPL_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_WRK_ACT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARLD_CHK_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_LEV_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_PNT_WO_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_PNT_CD3")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_PNT_CD4")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_WRK_BOOK_ID")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_STOP_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_SCH_REQ_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_ARR_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_CHK_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_ST_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_CMPL_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CARUD_WRK_ACT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_TRN_WRK_DELY_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "WTIN_ST_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "WTIN_END_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "WTOUT_ST_DT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "WTOUT_END_DT")
					
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_EQP_ID")
					
					
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WRK_HIST_ID")   //KEY
					

			};
			
			int iRtn = assistantDAO.trtProcess(szQueryIdUpd1, oParam);
			if(iRtn <= 0){
				szMsg = "UPDATE 처리 실패 (" + iRtn + ")";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
				return intRtnVal = -1;
			}			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStock
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 공통테이블 저장위치 UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분 ( 0: 
	 *                                 1: 
	 *                                 2:
	 *                             
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdWrkHistByIdNo (JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "updYdWrkHistByIdNo";
		String szMsg = null;
		JDTORecord outRec = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		Object oParam[]             = null;
		DBAssistantDAO assistantDAO = new DBAssistantDAO();
		
		

		try {
			
			//변환용 레코드
			JDTORecord recPara = null;
			recPara = inRec;
			
			//필드명 변환 (필드명 -> V_필드명)
			//recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			//blnChk_Field = this.chkPtCommParameter_LOC(recPara);
			
			//parameter error return
			if (!blnChk_Field) return intRtnVal = -2;
			
			//query id setting
			if (intGp == 1){
								
				oParam = new Object[] {						
						 ydDaoUtils.paraRecChkNull(recPara, "SPOS_WLOC_CD")
						,ydDaoUtils.paraRecChkNull(recPara, "ARR_WLOC_CD")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CAR_USE_GP")
						,ydDaoUtils.paraRecChkNull(recPara, "CAR_NO")
						,ydDaoUtils.paraRecChkNull(recPara, "TRN_EQP_CD")
						,ydDaoUtils.paraRecChkNull(recPara, "CAR_KIND")
						,ydDaoUtils.paraRecChkNull(recPara, "TRANS_EQUIPMENT_TYPE")
						,ydDaoUtils.paraRecChkNull(recPara, "CARD_NO")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CAR_SCH_ID")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_LEV_LOC")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_LEV_DT")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_PNT_WO_DT")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_PNT_CD1")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_PNT_CD2")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_WRK_BOOK_ID")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_SCH_REQ_GP")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_STOP_LOC")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_ARR_DT")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_ST_DT")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_CMPL_DT")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_WRK_ACT_GP")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_CHK_DT")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARUD_LEV_DT")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARUD_PNT_WO_DT")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_PNT_CD3")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_PNT_CD4")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARUD_WRK_BOOK_ID")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARUD_STOP_LOC")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARUD_SCH_REQ_GP")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARUD_ARR_DT")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARUD_CHK_DT")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARUD_ST_DT")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARUD_CMPL_DT")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARUD_WRK_ACT_GP")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_TRN_WRK_DELY_CD")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CRN_SCH_ID")
						,ydDaoUtils.paraRecChkNull(recPara, "STL_NO")
						};
				
				intRtnVal = assistantDAO.trtProcess(szQueryIdUpd2,oParam);
				
			}
			else if (intGp == 2){
				
				oParam = new Object[] {						
						 ydDaoUtils.paraRecChkNull(recPara, "YD_TCAR_SCH_ID")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_LEV_LOC")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_LEV_DT")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_WRK_BOOK_ID")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_SCH_REQ_GP")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_STOP_LOC")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_ARR_DT")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_ST_DT")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_CMPL_DT")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_WRK_ACT_GP")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARUD_LEV_DT")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARUD_WRK_BOOK_ID")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARUD_STOP_LOC")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARUD_SCH_REQ_GP")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARUD_ARR_DT")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARUD_ST_DT")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARUD_CMPL_DT")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARUD_WRK_ACT_GP")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CRN_SCH_ID")
						,ydDaoUtils.paraRecChkNull(recPara, "STL_NO")
						

						};
				
				intRtnVal = assistantDAO.trtProcess(szQueryIdUpd3,oParam);	
			}
				
			else if (intGp == 3){
				
				oParam = new Object[] {						
						ydDaoUtils.paraRecChkNull(recPara, "YD_WTCL_TNK_SCH_ID")
						,ydDaoUtils.paraRecChkNull(recPara, "WTIN_ST_DT")
						,ydDaoUtils.paraRecChkNull(recPara, "WTIN_END_DT")
						,ydDaoUtils.paraRecChkNull(recPara, "WTOUT_ST_DT")
						,ydDaoUtils.paraRecChkNull(recPara, "WTOUT_END_DT")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CRN_SCH_ID")
						,ydDaoUtils.paraRecChkNull(recPara, "STL_NO")
						};
				
				intRtnVal = assistantDAO.trtProcess(szQueryIdUpd4,oParam);	
				
				
			} else if (intGp == 4){
				
				oParam = new Object[] {						
						 ydDaoUtils.paraRecChkNull(recPara, "SPOS_WLOC_CD")
						,ydDaoUtils.paraRecChkNull(recPara, "ARR_WLOC_CD")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CAR_USE_GP")
						,ydDaoUtils.paraRecChkNull(recPara, "CAR_NO")
						,ydDaoUtils.paraRecChkNull(recPara, "TRN_EQP_CD")
						,ydDaoUtils.paraRecChkNull(recPara, "CAR_KIND")
						,ydDaoUtils.paraRecChkNull(recPara, "TRANS_EQUIPMENT_TYPE")
						,ydDaoUtils.paraRecChkNull(recPara, "CARD_NO")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CAR_SCH_ID")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_LEV_LOC")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_LEV_DT")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_PNT_WO_DT")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_PNT_CD1")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_PNT_CD2")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_WRK_BOOK_ID")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_SCH_REQ_GP")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_STOP_LOC")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_ARR_DT")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_ST_DT")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_CMPL_DT")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_WRK_ACT_GP")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_CHK_DT")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARUD_LEV_DT")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARUD_PNT_WO_DT")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_PNT_CD3")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_PNT_CD4")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARUD_WRK_BOOK_ID")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARUD_STOP_LOC")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARUD_SCH_REQ_GP")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARUD_ARR_DT")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARUD_CHK_DT")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARUD_ST_DT")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARUD_CMPL_DT")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CARUD_WRK_ACT_GP")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_TRN_WRK_DELY_CD")
						,ydDaoUtils.paraRecChkNull(recPara, "UP_YD_CAR_SCH_ID")
						};
				
				intRtnVal = assistantDAO.trtProcess(szQueryIdUpd5,oParam);
				
			} else if (intGp == 5){
				
				oParam = new Object[] {						
						 ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_CD")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_CRN_SCH_ID")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_ID")
						,ydDaoUtils.paraRecChkNull(recPara, "YD_GP")
						,ydDaoUtils.paraRecChkNull(recPara, "CD_CONTENTS")
						};
				
				intRtnVal = assistantDAO.trtProcess(szQueryIdUpd6,oParam);
				
			}
				
						
			//execution error return
			if (intRtnVal < 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdWrkHistByIdNo
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드저장품 UPDATE parameter mapping
	 * 
	 * @param JDTORecord inRec
	 *        JDTORecord outRec
	 * @return void
	 * @throws JDTOException 
	 */		
	public void dataMapping(JDTORecord inRec, JDTORecord outRec) throws JDTOException {
		String szFieldName = null;
		
		try {
			szFieldName = "V_YD_WRK_HIST_ID";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_REGISTER";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_REG_DDTT";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_MODIFIER";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_DEL_YN";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_STL_NO";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_GNT_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_BUY_SLAB_NO";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_STL_APPEAR_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_ITEMNAME_CD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_ORD_YEOJAE_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_ORD_NO";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_ORD_DTL";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_STLKIND_CD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_SPEC_ABBSYM";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_ORD_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_CUST_CD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_DEST_CD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_DEMANDER_CD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_DEST_TEL_NO";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_STL_PROG_CD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_GOODS_GRADE";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_MTL_W_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_MTL_T_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_MTL_L_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "YD_MTL_T";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_MTL_W";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_MTL_L";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_MTL_WT";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_COIL_OUTDIA_GRP_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_COIL_INDIA";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_COIL_OUTDIA";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_SLAB_WO_RT_CD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_ORD_HCR_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_HCR_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_SCARFING_YN";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_SCARFING_DONE_YN";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_REHEAT_SLAB_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_ROLL_UNIT_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_ROLL_UNIT_NAME";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_REFUR_CHG_LOT_NO";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_REFUR_CHG_PLN_SERNO";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CONVEYOR_BRANCH_CD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_HYSCO_TRANS_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_COOL_METHOD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_COOL_DONE_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_RENTPROC_CD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_DIST_DUE_DATE";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_PILING_CD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_BOOK_OUT_LOC";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_PL_RCPT_LN_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_FRTOMOVE_ORD_DATE";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_URGENT_FRTOMOVE_WORD_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_SPOS_WLOC_CD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_ARR_WLOC_CD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_AIM_RT_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_AIM_BAY_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_LOT_TP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_LOT_CD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_TRANS_ORD_DATE";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_PL_L2_TRK_NO";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_DIST_SHIPASSIGN_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_EXPORT_SHIP_SET_NO";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_SHIPASSIGN_WORD_DATE";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_SHIPASSIGN_WORD_SEQNO";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_SHIP_CD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_SHIP_NAME";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_RSHP_HOLD_NO";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_BERTH_NO";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_SAILNO";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CAR_USE_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_CAR_NO";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_TRN_EQP_CD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_CAR_KIND";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_TRANS_EQUIPMENT_TYPE";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_CARD_NO";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CAR_SCH_ID";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_TCAR_SCH_ID";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_WTCL_TNK_SCH_ID";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_WBOOK_ID";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CRN_SCH_ID";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_SCH_CD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_SCH_ST_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_SCH_REQ_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_SCH_PRIOR";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_WBOOK_DT";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_AID_WRK_YN";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_TO_LOC_DCSN_MTD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_TO_LOC_GUIDE";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_SCH_DT";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_WORD_DT";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_UP_WO_LOC";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_UP_WO_LAYER";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_UP_WR_LOC";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_UP_WR_LAYER";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_UP_WRK_ACT_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_UP_CMPL_DT";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_DN_WO_LOC";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_DN_WO_LAYER";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_DN_WR_LOC";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_DN_WR_LAYER";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_DN_WRK_ACT_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_DN_CMPL_DT";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_WRK_HDS_DD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_WRK_DUTY";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_WRK_PARTY";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CARLD_LEV_LOC";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CARLD_LEV_DT";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CARLD_PNT_WO_DT";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_PNT_CD1";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_PNT_CD2";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CARLD_WRK_BOOK_ID";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CARLD_SCH_REQ_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CARLD_STOP_LOC";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CARLD_ARR_DT";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CARLD_ST_DT";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CARLD_CMPL_DT";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CARLD_WRK_ACT_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CARLD_CHK_DT";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CARUD_LEV_DT";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CARUD_PNT_WO_DT";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_PNT_CD3";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_PNT_CD4";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CARUD_WRK_BOOK_ID";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CARUD_STOP_LOC";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CARUD_SCH_REQ_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CARUD_ARR_DT";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CARUD_CHK_DT";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CARUD_ST_DT";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CARUD_CMPL_DT";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CARUD_WRK_ACT_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_TRN_WRK_DELY_CD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_WTIN_ST_DT";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);			
			
			szFieldName = "V_WTIN_END_DT";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_WTOUT_ST_DT";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_WTOUT_END_DT";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_EQP_ID";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			
			
			

		
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
	} // end of dataMapping
	
	
	

	
	/**
	 *      [A] 오퍼레이션명 : 작업이력BATCH UPDATE
	 * 
	 * @param  JDTORecord[] recArrPara parameter record array
	 *         int          intMaxCnt  실행횟수 
	 *         int          intGp      쿼리 구분
	 * @return int[]        복수개의 쿼리문에 대한 각각의 수행처리결과건수
	 * @throws JDTOException 
	 * @throws DAOException
	 */		
	public int[] updBatchStock(JDTORecord[] recArrPara, int intMaxCnt, int intGp) throws DAOException, JDTOException {
		JDTORecord[] recArrCvs = null;
		int[] intRtnVal = null;
		
		try {
			for (int Loop_i = 0; Loop_i < intMaxCnt; Loop_i++) {
				
				//필드명 변환 (필드명 -> V_필드명)
				recArrCvs[Loop_i] = ydDaoUtils.conversionFieldname(recArrPara[Loop_i], 0);
				recArrCvs[Loop_i].setField("JSPEED_QUERY_ID", szQueryIdUpd1);
	
			}
			intRtnVal = dbAssDao.trtProcess(recArrPara);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;	
	}
	
	/**
	 *      [A] 오퍼레이션명 : 야드저장품 INSERT
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int insYdPilingHist(JDTORecord inRec) throws DAOException, JDTOException {
		DBAssistantDAO assistantDAO = new DBAssistantDAO();
		YdDaoUtils ydDaoUtils       = new YdDaoUtils();
		JDTORecord jRecordParam     = inRec;
		JDTORecord recPara 			= null;
		int intRtnVal               = 0;
		String szMethodName         = "insYdPilingHist";
		String szMsg                = "";
		boolean blnChk_Field = true;
		
		try {			

			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter error return
			if (!blnChk_Field)
				return intRtnVal = -2;
	
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", szQueryIdIns7);
	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of insYdWrkHistS
	
	/**
	 *      [A] 오퍼레이션명 : 야드이상재 이력관리 INSERT
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int insYdAbSlabHist(JDTORecord inRec) throws DAOException, JDTOException {
		YdDaoUtils ydDaoUtils       = new YdDaoUtils();
		JDTORecord recPara 			= null;
		int intRtnVal               = 0;
		boolean blnChk_Field = true;
		
		try {			

			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter error return
			if (!blnChk_Field)
				return intRtnVal = -2;
	
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", szQueryIdIns8);
	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of insYdAbSlabHist
	
	/**
	 *      [A] 오퍼레이션명 : 야드이상재 이력관리 UPDATE
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdAbSlabHist(JDTORecord inRec) throws DAOException, JDTOException {
		YdDaoUtils ydDaoUtils       = new YdDaoUtils();
		JDTORecord recPara 			= null;
		int intRtnVal               = 0;
		boolean blnChk_Field = true;
		
		try {			
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			//parameter error return
			if (!blnChk_Field)
				return intRtnVal = -2;
	
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", szQueryIdUpd7);
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of insYdAbSlabHist

/*------------------------------------- DELETE -------------------------------------------*/
} // end of class
