package com.inisteel.cim.yd.common.dao.ydStkColDao;

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
 *      [A] 클래스명 : 야드적치열 DAO
 * 
*/

public class YdStkColDao {
	
	// Dao Name
	private String szDaoName = getClass().getName();
	
	private YdUtils ydUtils = new YdUtils();	

	private DBAssistantDAO dbAssDao = new DBAssistantDAO();
	
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();
	
	private CCommUtils commUtils = new CCommUtils();
	private YdPICommDAO ydPICommDAO = new YdPICommDAO();
	
	//select query id
	private String szQueryIdGet1 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcol_PIDEV";
	private String szQueryIdGet2 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcolEqp";
	private String szQueryIdGet3 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcolCol";
	//이현성[090305]_후판제품창고 MAP
	private String szQueryIdGet4 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcolBedInfo_PAGE";
	//이현성[090308]
	private String szQueryIdGet5 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcolWLocCdandPntCd";
	//김창일[090312]
	private String szQueryIdGet6 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getCoilYdBaySetList_PAGE";
	//김창일[090312]
	private String szQueryIdGet7 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getCoilYdColStsSetInfo";
	//김창일[090312]
	private String szQueryIdGet8 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkPosSet_PAGE";
	//김진욱[090331]
	private String szQueryIdGet9 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcolColGpLike";
	//김종건[2009.04.27] 코일제품창고 군,열 상태별 재고 조회
	private String szQueryIdGet10 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcolLineSvLocMgt";
	//김종건[2009.04.27] 코일제품창고 군,열 상태 및 SPAN별 재고 조회
	private String szQueryIdGet11 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcolLineSvLocMgtSpan";
	//김종건[2009.04.27] 코일제품창고 군,열 상태 및 SPAN별 재고 조회
	private String szQueryIdGet12 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkColYdStkBedByStkColGpTwoBedNo";
	
	//김종건[2009.07.07] FLEX_야드별 차량 정보 읽기
	private String szQueryIdGet13 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkColByYdgp_Flex";
	//심명순[2009.07.14] 저장위치 좌표설정
	private String szQueryIdGet14 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getCoilGdsYdStkcolCol";
	//김진욱[20090723] 포인트코드 개소코드 설비구분
	private String szQueryIdGet15 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcolWlocCdPntCdEqpGp";
	//김창일[2009.07.23] 야드포인트 코드로 야드적치열구분 조회
	private String szQueryIdGet16 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getCoilYdStkColGpPntCd";
	//김종건(2009.08.05) 기본조회 쿼리 (DEL_YN 삭제)
	private String szQueryIdGet17 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcolNotDelYn";
	//심명순(2009.08.19) coil입고진행관리
	private String szQueryIdGet18 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getCoilYdInPlan2_PIDEV";
	
	//이현성(2009.08.24) 분기 컨베어 조회 
	private String szQueryIdGet19 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdDivConveyorCodeName";
	
	private String szQueryIdGet20 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcolColBySchCheck";
	//임춘수 2009.09.30 - 동 or 스판 or 열 정보 조회 쿼리
	private String szQueryIdGet21 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcolForCode";
	
	//이현성 20110.02.08 - SPAN별 적치사양 조정
	private String szQueryIdGet22 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcolBaySpan";
	//윤재광 2012.02.21 - 각스판에 Max열값 가져오기
	private String szQueryIdGet23 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdMaxStkcolLike";

	//항만슬라브야드 기능추가 - 2016.02.02 LeeJY  - 미예약된 적치열 정보추출
	private String szQueryIdGet24 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdMinStkcolLike";

	private String szQueryIdGet300 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcolColGpLike2";
	
//	열연Coil상세조회(위치변경이력조회) - 2010.04.22
	private String szQueryIdGet301 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getCoilYdStrlocIdInfo";
	private String szQueryIdGet302 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getCoilYdStkColYDPntCd";
	private String szQueryIdGet303 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getCoilYdStkColYDCard";
	private String szQueryIdGet304 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcolColGpLikeCard";
	private String szQueryIdGet305 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcolColGpLikeCard_H";
	
	private String szQueryIdGet306 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdMoveMgt";
	private String szQueryIdGet307 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcolColGpLikeCardHysco";
	private String szQueryIdGet308 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkLotID";
	
//EF	
	private String szQueryIdGet400 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdMapInfo_PAGE";
	private String szQueryIdGet401 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getCoilYdStkColByYdgp_Flex";

	private String szQueryIdGet402 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcolForCode2";
	
	private String szQueryIdGet403 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdCarpointsch";
	
	private String szQueryIdGet404 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYmStkcol";
	
	private String szQueryIdGet405 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdPlateCarPoint";
	
	private String szQueryIdGet406 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcolColGpLikeTong";
	
//PIDEV	
	private String szQueryIdGet900 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcolPI_PIDEV";
	
	//insert query id
	private String szQueryIdIns1 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.insYdStkcol";
	//update query id
	private String szQueryIdUpd1 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.updYdStkcol";
	//update query id
	private String szQueryIdUpd11 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.updYdStkcol11";
	
	private String szQueryIdUpd2 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.updYdStkcolTrnEqpCdToNull";
	
	private String szQueryIdUpd3 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.delYdStkcolTrnEqpCdToNull";
	
	private String szQueryIdUpd4 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.updateCarLiftInfo";
	
	private String szQueryIdUpd5 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.updYdStkcolTrneqpCd";
	
	private String szQueryIdUpd6 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.updydcarpoint";
	
	private String szQueryIdUpd7 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.updYmStkcol";
	
	private String szQueryIdUpd10 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.updYdStkColActStat";
 
	
/*------------------------------------- SELECT -------------------------------------------*/
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치열 SELECT
	 *      
	 * @param  JDTORecord    inRec      parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         int           intGp      구분(0:YD_STK_COL_GP,
	 *                                      1:YD_GP,YD_BAY_GP,YD_EQP_GP, ,YD_STK_COL_ACT_STAT
	 *                                      2:YD_GP,YD_BAY_GP,YD_EQP_GP,YD_STK_COL_NO ,YD_STK_COL_ACT_STAT
	 *                                      3:YD_STK_COL_NO1 ,YD_STK_COL_NO2,YD_STK_COL_NO3,PAGE_CNT1,ROW_CNT1,PAGE_CNT2,ROW_CNT2
	 *                                      4:V_WLOC_CD ,  V_YD_PNT_CD
	 *                                      7:V_YD_GP, V_YD_BAY_GP, V_YD_EQP_GP, V_YD_STK_COL_NO, V_PAGE_CNT1, V_ROW_CNT1, V_PAGE_CNT2, V_ROW_CNT2
	 *                                      8:YD_STK_COL_GP
	 *                                      9:YD_GP, YD_BAY_GP, YD_EQP_GP, YD_STK_COL_NO
	 *                                      10:YD_GP, YD_BAY_GP, YD_EQP_GP, YD_STK_COL_NO
	 *                                      11:YD_STK_COL_GP, YD_STK_BED_NO, YD_STK_BED_NO_R
	 *                                      12:YD_GP
	 *                                      16:YD_STK_COL_GP
	 *                                      18:YD_GP
	 *                                      
	 *                                      21:YD_GP,YD_BAY_GP,YD_EQP_GP
	 *                                      )
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int getYdStkcol(JDTORecord inRec, JDTORecordSet outRecSet, int intGp) throws DAOException, JDTOException {
		String szMethodName = "getYdStkcol";
		JDTORecordSet rsTemp = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		String szMsg = null;
		JDTORecord recPara = null;
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkPara_getYdStkcol(recPara, intGp);
			
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
			else if (intGp == 23)  //항만슬라브야드 기능추가 - 2016.02.02 LeeJY 
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet24);
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
//E,F
			else if (intGp == 400)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet400);
			else if (intGp == 401)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet401);
			else if (intGp == 402)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet402);
			else if (intGp == 403)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet403);
			else if (intGp == 404)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet404);
			else if (intGp == 405)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet405);
			else if (intGp == 406)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet406);
//PIDEV
			else if (intGp == 900)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet900);
			
//			String sPI_YD     = commUtils.nvl(inRec.getFieldString("V_PI_YD"), "*");
//			String queryId = ydPICommDAO.getYdRulePI("", "", "YD0001", recPara.getFieldString("JSPEED_QUERY_ID"), "APPPI0", sPI_YD, "*" );
//			
//			recPara.setField("JSPEED_QUERY_ID", queryId);

			
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
	} //end of getYdStkcol
	
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치열 SELECT parameter Check
	 * 
	 * @param  JDTORecord inRec  parameter record
	 *         int        intGp  구분(0:YD_STK_COL_GP,
	 *                               1:YD_GP,YD_BAY_GP,YD_EQP_GP, YD_STK_COL_ACT_STAT
	 *                               2:YD_GP,YD_BAY_GP,YD_EQP_GP,YD_STK_COL_NO , YD_STK_COL_ACT_STAT
	 *                               3:YD_STK_COL_NO1 ,YD_STK_COL_NO2,YD_STK_COL_NO3,PAGE_CNT1,ROW_CNT1,PAGE_CNT2,ROW_CNT2
	 *                               4:V_WLOC_CD ,  V_YD_PNT_CD
	 *                               5:V_YD_GP, V_PAGE_CNT1, V_ROW_CNT1, V_PAGE_CNT2, V_ROW_CNT2
	 *                               6:V_YD_STK_COL_GP
	 *                               8:YD_STK_COL_GP
	 *                               9:V_YD_GP, V_YD_BAY_GP, V_YD_EQP_GP, V_YD_STK_COL_NO
	 *                               10:V_YD_GP, V_YD_BAY_GP, V_YD_EQP_GP, V_YD_STK_COL_NO
	 *                               11:YD_STK_COL_GP, YD_STK_BED_NO, YD_STK_BED_NO_R
	 *                               12:YD_GP
	 *                               16:YD_STK_COL_GP
	 *                               18:YD_GP
	 *                               
	 *                               21:V_YD_GP, V_YD_BAY_GP, V_YD_EQP_GP
	 *                               )
	 * @return boolean           true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkPara_getYdStkcol(JDTORecord inRec, int intGp) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		
		try {
			if (intGp == 0 || intGp == 6 || intGp == 8 || intGp == 16) {
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
			} else if (intGp == 1) {
			
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_EQP_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				
				szFieldName = "V_YD_STK_COL_ACT_STAT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				
				
			} else if(intGp == 2){
				
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_EQP_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				
				szFieldName = "V_V_YD_STK_COL_ACT_STAT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				
				
				
			}
			
			
			
			
			else if ( intGp == 13) {
				
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_EQP_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
			}else if (intGp == 3) {
				
				szFieldName = "V_YD_STK_COL_NO1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_NO2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_NO3";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_ACT_STAT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
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
			} else if (intGp == 4) {
				
				szFieldName = "V_WLOC_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_PNT_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 1, 'S', 0, 0);
			} else if (intGp == 5) {
				szFieldName = "V_YD_GP";
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

			} else if (intGp == 7) {
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;

				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_EQP_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_NO";
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

			} else if (intGp == 9) {
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1,2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_EQP_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_COIL_OUTDIA_GRP_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_W_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
			} else if (intGp == 10) { //!A 외경군, 폭별재고조회
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1,2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_EQP_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "YD_STK_COL_NO"; //!A 열정보 추가 (박지열 - 2010/03/23) 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_COIL_OUTDIA_GRP_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_W_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
			} else if (intGp == 11) {
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;

				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO_R";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
			} else if (intGp == 12 || intGp ==18 ) {
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				
			} else if (intGp == 15) {
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_PNT_CD";			
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 1, 'S', 0, 0);
			} else if (intGp == 17) {
				szFieldName = "V_BRANCH_CD1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_BRANCH_CD2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_BRANCH_CD3";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_BRANCH_CD4";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_BRANCH_CD5";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
			
			} else if (intGp == 19) {
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
			} else if (intGp == 20) {
				szFieldName = "V_START_POS";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.INTEGER_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
			} else if(intGp == 21){
				
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_EQP_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "YD_STK_COL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
			} else if (intGp == 23) {  //항만슬라브야드 기능추가 - 2016.02.02 LeeJY
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_WLOC_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
			}
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} // end of chkPara_getYdStkcol
	
	
	
/*------------------------------------- INSERT -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치열 INSERT
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int insYdStkcol(JDTORecord inRec) throws DAOException, JDTOException {
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
	} // end of insYdStkcol
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치열 INSERT parameter Check
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return boolean         true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkParameter(JDTORecord inRec) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		
		try {
			szFieldName = "V_YD_STK_COL_GP";   	//야드적치열구분
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_REG_DDTT";			// 등록일
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_REGISTER";			// 등록자
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;

//			szFieldName = "V_MOD_DDTT";			// 수정일
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
//			if (!blnErr) return blnErr;
//			
//			szFieldName = "V_MODIFIER";			// 수정자
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
//			if (!blnErr) return blnErr;
//			
//			szFieldName = "V_DEL_YN";			// 삭제유무
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
//			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_GP";			// 야드구분
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_BAY_GP";		// 야드동구분
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_EQP_GP";		// 야드설비구분
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_COL_NO";	// 야드적치열번호
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_STK_COL_ACT_STAT";		// 야드적치열활성상태
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_STK_COL_RULE_XAXIS";	// 야드적치열기준X축
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 7, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_STK_COL_RULE_YAXIS";	// 야드적치열기준Y축
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_COL_RULE_ZAXIS";	// 야드적치열기준Y축
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_STK_COL_W";				// 야드적치열폭
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_STK_COL_L";				// 야드적치열길이
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CAR_USE_GP";	// 차량사용구분
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_TRN_EQP_CD";	// 운송장비코드
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_CAR_NO";	// 차량번호
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 15, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_CARD_NO";	// 카번호
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_WLOC_CD";	// 개소코드
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_PNT_CD";	// 야드포인트코드
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_COL_W_GP";	// 야드적치열폭구분
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_COL_H_MAX";	// 야드적치열높이Max
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_COL_BED_L_TP";	// 야드적치열Bed길이Type
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_COIL_OUTDIA_GRP_GP";	// 야드코일외경군구분
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} //end of chkPara_YdStkcol
		
	
/*------------------------------------- UPDATE -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치열 UPDATE
	 * 
	 * @param  JDTORecord      inRec parameter record
	 *         int             intGp 구분(0:YD_STK_COL_GP))
	 * @return int             execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStkcol(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "updYdStkcol";
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
			intRtnVal = this.getYdStkcolForUpdate(inRec, outRecSet, 0);
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
			if (intGp == 0){
			recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd1);
			}else if (intGp == 1){
			recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd6);
			}else if (intGp == 11){
			recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd11);
			} 
			
			
			//query execute			
				intRtnVal = dbAssDao.trtProcess(recOutPara);
			
			//execution error return
			if (intRtnVal <= 0) intRtnVal = -3;
		} catch
		(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStkcol
	
	
	/**
	 *      [A] 오퍼레이션명 :차상위치 변경
	 * 
	 * @param  JDTORecord      inRec parameter record
	 *         int             intGp 구분(0:YD_STK_COL_GP))
	 * @return int             execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStkcolTrneqpCd(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "updYdStkcolTrneqpCd";
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
			recOutPara = ydDaoUtils.conversionFieldname(inRec, 0);
				 		
			//query id setting
			recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd5);
			
			//query execute
			if (intGp == 0)
				intRtnVal = dbAssDao.trtProcess(recOutPara);
			
			//execution error return
			if (intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStkcolTrneqpCd
	
	
	/**
	 *      [A] 오퍼레이션명 :차상위치 변경
	 * 
	 * @param  JDTORecord      inRec parameter record
	 *         int             intGp 구분(0:YD_STK_COL_GP))
	 * @return int             execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYmStkcol(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "updYmStkcol";
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
			recOutPara = ydDaoUtils.conversionFieldname(inRec, 0);
				 		
			//query id setting
			recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd7);
			
			//query execute
			if (intGp == 0)
				intRtnVal = dbAssDao.trtProcess(recOutPara);
			
			//execution error return
			if (intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYmStkcol
	
	
	/**
	 *      [A] 오퍼레이션명 :차상위치 변경
	 * 
	 * @param  JDTORecord      inRec parameter record
	 *         int             intGp 구분(0:YD_STK_COL_GP))
	 * @return int             execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updydcarpoint(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "updydcarpoint";
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
			recOutPara = ydDaoUtils.conversionFieldname(inRec, 0);
				 		
			//query id setting
			recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd6);
			
			//query execute
			if (intGp == 0)
				intRtnVal = dbAssDao.trtProcess(recOutPara);
			
			//execution error return
			if (intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updydcarpoint
	
	/**
	 *      [A] 오퍼레이션명 :차상위치 변경
	 * 
	 * @param  JDTORecord      inRec parameter record
	 *         int             intGp 구분(0:YD_STK_COL_GP))
	 * @return int             execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updateCarLiftInfo(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "updateCarLiftInfo";
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
			recOutPara = ydDaoUtils.conversionFieldname(inRec, 0);
				 		
			//query id setting
			recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd4);
			
			//query execute
			if (intGp == 0)
				intRtnVal = dbAssDao.trtProcess(recOutPara);
			
			//execution error return
			if (intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStkcol
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 :차상위치 삭제
	 * 
	 * @param  JDTORecord      inRec parameter record
	 *         int             intGp 구분(0:YD_STK_COL_GP))
	 * @return int             execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int deleteCarLiftInfo(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "deleteCarLiftInfo";
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
			recOutPara  = ydDaoUtils.conversionFieldname(inRec, 0);
						
			//query id setting
			recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd3);
			
			//query execute
			if (intGp == 0)
				intRtnVal = dbAssDao.trtProcess(recOutPara);
			
			//execution error return
			if (intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStkcol
	
	
	
	
	
	
	
	public int updYdStkcol1(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "updYdStkcol1";
		String szMsg = null;
		JDTORecord outRec = null;
		int intRtnVal = -3;
		boolean blnChk_Field = true;
		try {
			//recordSet create
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			
			//변환용 레코드
			JDTORecord recInPara = null;
			//JDTORecord recOutPara = null;
			
			//필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//update data select
			//intRtnVal = this.getYdStkcolForUpdate(inRec, outRecSet, 0);
			//parameter error return
//			if (intRtnVal < 0) {
//	//			szMsg = "parameter error!";
//	//			ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
//				return intRtnVal;
//			}
//			
//			//data not found return
//			if (intRtnVal == 0) {
//	//			szMsg = "data not found!";
//	//			ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
//				return intRtnVal;
//			}
//			
//			//duplicate data return
//			if (outRecSet.size() != 1) {
//	//			szMsg = "duplicate data!";
//	//			ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
//				return intRtnVal = -1;
//			}
			
			//outRecSet.first();
			//outRec = outRecSet.getRecord();
	
			//필드명 변환 (필드명 -> V_필드명)
			//recOutPara = ydDaoUtils.conversionFieldname(outRec, 0);
			
			//data mapping
			//this.dataMapping(recInPara, recOutPara);
			
			//parameter check
			//blnChk_Field = this.chkParameter(recOutPara);
			
			//parameter error return
			//if (!blnChk_Field) return intRtnVal = -2;
			
			//query id setting
			recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd2);
			
			//query execute
			if (intGp == 1)
				intRtnVal = dbAssDao.trtProcess(recInPara);
			else{
				//로그기록
				szMsg="[YdStkColDao - updYdStkcol1] 지원하지 않는 값["+intGp+"]입니다.";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			//execution error return
			//if (intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStkcol1
	
	
	public int getYdStkcolForUpdate(JDTORecord inRec, JDTORecordSet outRecSet, int intGp) throws DAOException, JDTOException {
		String szMethodName = "getYdStkcolForUpdate";
		JDTORecordSet rsTemp = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		String szMsg = null;
		JDTORecord recPara = null;
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkPara_getYdStkcol(recPara, intGp);
			
			//parameter error return
			if (!blnChk_Field)
				return intRtnVal = -2;
			
			//query id setting
			if (intGp == 0)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet17);
			else{
				//로그기록
				szMsg="[YdStkColDao - getYdStkcolForUpdate] 지원하지 않는 값["+intGp+"]입니다.";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
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
	}
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치열 UPDATE parameter mapping
	 * 
	 * @param JDTORecord inRec
	 *        JDTORecord outRec
	 * @return void
	 * @throws JDTOException 
	 */		
	public void dataMapping(JDTORecord inRec, JDTORecord outRec) throws JDTOException {
		String szFieldName = null;

		try {
			szFieldName = "V_YD_STK_COL_GP";	// 야드적치열구분
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_REGISTER";			// 등록자
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_REG_DDTT";			// 등록일
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_MODIFIER";			// 수정자
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_MOD_DDTT";			// 수정일
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_DEL_YN";			// 삭제유무
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_GP";			// 야드구분
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_BAY_GP";		// 야드동구분
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_EQP_GP";		// 야드설비구분
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_COL_NO";	// 야드적치열번호
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_STK_COL_ACT_STAT";	 // 야드적치열활성상태
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_STK_COL_RULE_XAXIS"; // 야드적치열기준X축
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_STK_COL_RULE_YAXIS"; // 야드산적Lot코드
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_COL_RULE_ZAXIS"; // 야드적치열기준Z축
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_STK_COL_W";			 // 야드적치열폭
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_STK_COL_L";			 // 야드적치열길이
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CAR_USE_GP";	// 차량사용구분
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_TRN_EQP_CD";	// 운송장비코드
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_CAR_NO";	// 차량번호
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_CARD_NO";	// 카번호
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_WLOC_CD";	// 개소코
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_PNT_CD";	// 야드포인트코드
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_COL_W_GP";	// 야드적치열폭구분
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_COL_H_MAX";	// 야드적치열높이Max
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_COL_BED_L_TP"; // 야드적치열Bed길이Type
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_COIL_OUTDIA_GRP_GP"; // V_YD_COIL_OUTDIA_GRP_GP
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STKBED_USG_CD"; // YD_STKBED_USG_CD
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_SKID_GP"; // YD_STKBED_USG_CD
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_STK_COL_TOLOC_STAT"; // YD_STK_COL_TOLOC_STAT
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_MATL_SUP_MTD_GP"; // MATL_SUP_MTD_GP
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}

	} // end of ydStkcol_DataMapping
	
	/**
	 *      [A] 오퍼레이션명 :차상위치 사용유무 변경
	 * 
	 * @param  JDTORecord      inRec parameter record
	 *         int             intGp 구분(0:YD_STK_COL_GP))
	 * @return int             execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStkColActStat(JDTORecord inRec) throws DAOException, JDTOException {
		String szMethodName = "updYdStkColActStat";
		String szMsg = null;
		int intRtnVal = 0;
		try {
			
			JDTORecord recOutPara = null;
			
			//필드명 변환 (필드명 -> V_필드명)
			recOutPara = ydDaoUtils.conversionFieldname(inRec, 0);
				 		
			//query id setting
			recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd10);
			intRtnVal = dbAssDao.trtProcess(recOutPara);
			
			//execution error return
			if (intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStkColActStat
	

	/**
	 *      [A] 오퍼레이션명 : chkAutoCrn 
	 * 
	 * @param String szYdStkColGp
	 * @return boolean     			// 고정스키드(F)일때 true
	 */	
	public boolean chkFixedSkid(String szYdStkColGp) {
		
		String szszYdStkColGpGet = "";
		String szMethodName = "YdStkColDao.chkFixedSkid";
		JDTORecord	recInTemp				= JDTORecordFactory.getInstance().create();
		JDTORecord	inRec					= JDTORecordFactory.getInstance().create();
		YdUtils ydUtils =new YdUtils();
		
    	int intRtnVal = 0;
    	JDTORecordSet		outRdSet 		= null;
    	ymCommonDAO dao = ymCommonDAO.getInstance();
		try {
			
			inRec.setField("YD_STK_SKID_GP"	, szYdStkColGp);
			
			if(!"".equals(szYdStkColGp) && !(szYdStkColGp == null)){  
				ydUtils.putLog(szDaoName, szMethodName, "szYD_EQP_ID ="+szYdStkColGp, 1);
				
				recInTemp = dao.getCodeToName("com.inisteel.cim.yd.dao.ydeqpdao.YdStkColDao.ChkStkLyrCD" , new Object[]{szYdStkColGp });
				
				szszYdStkColGpGet    = StringHelper.evl(recInTemp.getFieldString("YD_STK_SKID_GP"), "");
				
				szYdStkColGp	= ydDaoUtils.paraRecChkNull(inRec, "YD_STK_SKID_GP");
				ydUtils.putLog(szDaoName, szMethodName, "YD_STK_SKID_GP ="+szszYdStkColGpGet, 1);
				
				
				if("F".equals(szszYdStkColGpGet)){
					return true;
				}else{
					return false;
				}
			}else{
				return false;
			}
			
			
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}

	}
	
	
	
/*------------------------------------- DELETE -------------------------------------------*/
} // end of class






