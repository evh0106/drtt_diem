/**
 * @(#)YdEqpDao.java
 * 
 * @version			1.0
 * @author 			현대제철
 * @date			2011/06/07
 * 
 * @description		이클래스는 야드설비 DAO 클래스입니다.
 * ------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2011/06/07                    최초 등록
 * V1.01  2013/02/28   조병기       조병기       2후판 선별작업관련 메소드 수정
 *                                      : ProcedureSelYdAll, ProcedureSelDong,
 *                                        ProcedureSelYd
 *                                    
 */

package com.inisteel.cim.yd.common.dao.ydEqpDao;

import java.sql.Types;

import jspeed.base.ejb.EJBConnector;
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
 *      [A] 클래스명 : 야드설비 DAO
 * 
*/

public class YdEqpDao {
	
	// Dao Name
	private String szDaoName = getClass().getName();
	
	private YdUtils ydUtils = new YdUtils();	

	private DBAssistantDAO dbAssDao = new DBAssistantDAO();
	
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();
//PIDEV
	private YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
	private CCommUtils	   commUtils   = new CCommUtils();
	
	//select query id
	private String szQueryIdGet1 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqp";
	
	//이현성[090304]
	private String szQueryIdGet2 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getCodeName";
	private String szQueryIdGet3 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getJspYdEqpList";

	//이현성[090305]
	private String szQueryIdGet4 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getSlabYdCrnStsSetID";
	
	//이현성[090310]
	private String szQueryIdGet5 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getTcarSearchByYdGp";
	
	//이현성[090317]
	private String szQueryIdGet6 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpPlateFlex";
	
	//이현성[090325]
	private String szQueryIdGet7 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getSlabYdCrnStsSetByEqpId";
	
	//이현성_LocSrcRngDataSet
	private String szQueryIdGet8 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.LocSrcRngDataSet01";
	
	//이현성 (20090.09.29) 차량스케줄정보 
	private String szQueryIdGet9 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpDaoCarSchInfo";
	
	//임춘수 2009.10.13 - 크레인별 배차기준 정보 조회 쿼리
	private String szQueryIdGet10 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getCarAsgnStdByCrn";
	
	//임춘수 2009.10.13 - 크레인별 배차기준에서 우선순위가 빠르고 배차가능한 크레인정보 조회 쿼리
	private String szQueryIdGet11 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getEarliestCarAsgnStdByCrn";
	//임춘수 2009.10.15 - 크레인별 배차기준에서 우선순위가 낮은순으로 조회 쿼리
	private String szQueryIdGet12 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getLatestCarAsgnStdByCrn";

	//권오창 (2009.11.05) - 해당 설비ID의 현재 설비상태와 휴지테이블의 MAX차수에 대한 데이터 추출
	private String szQueryIdGet13 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getEqpStatofMAX";

	//권오창 (2009.11.11) - 해당 야드의 크레인과 대차의 설비ID와 설비명을 조회 
	private String szQueryIdGet14 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getEqpIdEqpNamebyYdGp";
	
	//설비테이블 - 야드 + 동  = 크레인 추출
	private String szQueryIdGet15 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getEqpIdEqpNamebyYdGp_Crane";
	
	//권오창 (2009.11.19) - 해당 야드와 동으로 크레인과 대차의 설비ID와 설비명을 조회 
	private String szQueryIdGet16 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getEqpIdEqpNamebyYdGpYdBayGp";	
	
	private String szQueryIdGet17 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpwithEqpIdFlex";
	
	private String szQueryIdGet18 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getBookoutMgt";
	
	private String szQueryIdGet19 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getpPlateYdStkMgt";
	
	private String szQueryIdGet100 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpTcYmCommon";

	private String szQueryIdGet300 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpTcBre";

	private String szQueryIdGet301 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpCoilTc";
	
	private String szQueryIdGet302 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpDaoCarSchInfoCoil";

	private String szQueryIdGet303 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpTCarStatCoil";
	
	private String szQueryIdGet304 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpTCarStatRcptCoil";

	private String szQueryIdGet400 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpCoil";
	private String szQueryIdGet401 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpTcBreYDB500";
	private String szQueryIdGet500 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpTcBreYDB500";
	private String szQueryIdGet521 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpTcBreYDB521";
//	후판제품창고 가적베드 관리기준	
	private String szQueryIdGet601 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpTcBreYDB601";	
//	후판제품창고저장속성그룹폭그룹부여기준 	
	private String szQueryIdGet651 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpTcBreYDB651";
//	후판제품창고저장속성그룹길이그룹부여기준 	
	private String szQueryIdGet652 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpTcBreYDB652";
	
	private String szQueryIdGet650 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpTcBreYDB650";
	private String szQueryIdGet653 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpTcBreYDB653";
	private String szQueryIdGet654 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpTcBreYDB654";

	private String szQueryIdGet659 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpTcBreYDB659";
	
	private String szQueryIdGet701 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpTcBreYDB701";
	private String szQueryIdGet704 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpTcBreYDB704";
	private String szQueryIdGet800 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpTcBreYDB800";
	
	private String szQueryIdGet801 = "com.inisteel.cim.yd.dao.ydeqpdao.getYdMgtList1";
	private String szQueryIdGet802 = "com.inisteel.cim.yd.dao.ydeqpdao.getYdMgtList2";
	private String szQueryIdGet803 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getCoilTotYdNextproctList";
	private String szQueryIdGet804 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getCoilTotYdNextproctList2";
	private String szQueryIdGet805 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getCoilTotYdNextprocList2Pop";
	private String szQueryIdGet806 = "com.inisteel.cim.yd.dao.ydeqpdao.getYdMgtTotalList";
	private String szQueryIdGet807 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getCoilTotYdNextproctPOList";
	private String szQueryIdGet808 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdGFList";
	
	private String szQueryIdGet901 = "com.inisteel.cim.yd.dao.ydeqpdao.getYdMgtList3";
	private String szQueryIdGet902 = "com.inisteel.cim.yd.dao.ydeqpdao.getYdMgtList4";
	private String szQueryIdGet903 = "com.inisteel.cim.yd.dao.ydeqpdao.getYdMgtList5";
	
	private String szQueryIdGet904 = "com.inisteel.cim.yd.dao.ydeqpdao.getYdTotalMgtList_PIDEV";
	private String szQueryIdGet905 = "com.inisteel.cim.yd.dao.ydeqpdao.getYdTotalMgtListXL";
	private String szQueryIdGet906 = "com.inisteel.cim.yd.dao.ydeqpdao.getYdTotalMgtList2_PIDEV";
	private String szQueryIdGet907 = "com.inisteel.cim.yd.dao.ydeqpdao.getYdTotalMgtListXL2";

//선별 
	private String szQueryIdGet402 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpCoilFlex";
	private String szQueryIdGet403 = "com.inisteel.cim.yd.dao.ydstklyrdao.hotcoilautolist";
	private String szQueryIdGet404 = "com.inisteel.cim.yd.dao.ydstklyrdao.hotcoilautolist2";
	private String szQueryIdGet405 = "com.inisteel.cim.yd.dao.ydstklyrdao.hotcoilchklist";
	private String szQueryIdGet406 = "com.inisteel.cim.yd.dao.ydstklyrdao.hotcoilautolistIN";
	private String szQueryIdGet407 = "com.inisteel.cim.yd.dao.ydstklyrdao.hotcoilautolistOUT";
	private String szQueryIdGet908 = "com.inisteel.cim.yd.dao.ydeqpdao.getPlateYdRuleMgtSel";

	private String szQueryIdGet909 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYNH00031";
	private String szQueryIdGet999 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN";

	
	//insert query id
	private String szQueryIdIns1 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.insYdEqp";
	//update query id
	private String szQueryIdUpd1 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.updYdEqp";
	//배차실적 증가 쿼리
	private String szQueryIdUpd2 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.updYdEqpCarAsgnWr";
	//배차실적 초기화 쿼리
	private String szQueryIdUpd3 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.updYdEqpCarAsgnWrZero";
	//배차실적 초기화 쿼리
	private String szQueryIdUpd300 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.updYdEqpTCar";

	//배차실적 초기화 쿼리
	private String szQueryIdUpd400 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.updYdEqpTCarCond";
	
	//선별
	private String szQueryIdUpd900 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.updYdRuleSel";
	//선별
	private String szQueryIdUpd901 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.updYdRuleSelFlag";
	
	//c2 스카핑 검사장 인터락 여부 업데이트
	private String szQueryIdUpd902 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.updYdEqpInterlockFlag";
	
	
	//후판제품 바코드 인식정보 UPDATE
	private String szQueryIdUpd4 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.updYdEqpBarCodeInfo";
	
	//작업선택 호출시 설비 상태관리 UPDATE
	private String szQueryIdUpd5 = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.updYdEqpModDdtt";
/*------------------------------------- SELECT -------------------------------------------*/
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드설비 SELECT
	 *      
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         int intGp                구분(
	 *         								0:YD_EQP_ID
	 *         								1:YD_GP ,YD_BAY_GP, YD_EQP_GP
	 *                                      2:YD_GP ,YD_BAY_GP, YD_EQP_GP
	 *         								3:YD_EQP_ID
	 *         								4:YD_GP
	 *         								5:YD_GP
	 *         								9:YD_GP, YD_BAY_GP
	 *         								10:9:YD_GP, YD_BAY_GP
	 *         								11:YD_GP, YD_BAY_GP, YD_PREP_WK_ST
	 *         								12:YD_EQP_ID
	 *         								13:YD_GP1, YD_GP2
	 *                                      14:YD_GP,  YD_BAY_GP
	 *                                      15:YD_GP1, YD_BAY_GP, YD_GP2
	 *                                      16:YD_EQP_ID (플렉스 전용)
	 *         				)
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int getYdEqp(JDTORecord inRec, JDTORecordSet outRecSet, int intGp) throws DAOException, JDTOException {
		String szMethodName = "getYdEqp";
		JDTORecordSet rsTemp = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		String szMsg = null;
		JDTORecord recPara = null;
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkPara_getYdEqp(recPara, intGp);
			
			//parameter error return
			if(!blnChk_Field)
				return intRtnVal = -2;
			
			//query id setting
			if(intGp ==0)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet1);
			else if(intGp == 1)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet2);
			else if(intGp == 2)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet3);
			else if(intGp == 3)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet4);
			else if(intGp == 4)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet5);
			else if(intGp == 5)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet6);
			else if(intGp == 6)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet7);
			else if(intGp == 7)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet8);
			else if(intGp == 8)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet9);
			else if(intGp == 9)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet10);
			else if(intGp == 10)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet11);
			else if(intGp == 11)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet12);
			else if(intGp == 12)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet13);
			else if(intGp == 13)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet14);
			else if(intGp == 14)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet15);
			else if(intGp == 15)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet16);
			else if(intGp == 16)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet17);
			else if(intGp == 17)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet18);
			else if(intGp == 18)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet19);
			else if(intGp == 100)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet100);
			else if(intGp == 300)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet300);
			else if(intGp == 301)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet301);
			else if(intGp == 302)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet302);
			else if(intGp == 303)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet303);
			else if(intGp == 304)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet304);
			else if(intGp == 400)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet400);   //szQueryIdGet1 동일
			else if(intGp == 401)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet401);   //szQueryIdGet1 동일
			else if(intGp == 402)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet402);
			else if(intGp == 403)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet403);
			else if(intGp == 404)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet404);
			else if(intGp == 405)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet405);
			else if(intGp == 406)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet406);
			else if(intGp == 407)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet407);
			else if(intGp == 500)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet500);   
			else if(intGp == 521)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet521);
			else if(intGp == 601)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet601);   
			else if(intGp == 651)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet651);  
			else if(intGp == 650)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet650);  
			else if(intGp == 652)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet652);   
			else if(intGp == 653)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet653);   
			else if(intGp == 654)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet654);   
			else if(intGp == 659)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet659);   
			else if(intGp == 701)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet701);   
			else if(intGp == 704)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet704);   
			else if(intGp == 800)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet800);   
			else if(intGp == 801)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet801);   
			else if(intGp == 802)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet802);   
			else if(intGp == 803)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet803);   
			else if(intGp == 804)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet804); 
			else if(intGp == 805)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet805); 
			else if(intGp == 806)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet806);
			else if(intGp == 807)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet807);
			else if(intGp == 808)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet808);
			else if(intGp == 901)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet901);   
			else if(intGp == 902)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet902);   
			else if(intGp == 903)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet903); 
			else if(intGp == 904)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet904);
			else if(intGp == 905)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet905);
			else if(intGp == 906)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet906);
			else if(intGp == 907)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet907);
			else if(intGp == 908)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet908);
			else if(intGp == 909)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet909);
			else if(intGp == 999)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet999);   
			
//PIDEV
//PIDEV_S :병행가동용:PI_YD
//			if ((intGp == 904) ||(intGp == 906))  {
//				String sPI_YD     = commUtils.nvl(recPara.getFieldString("V_PI_YD"), "*");			
//				String toQuery_ID = ydPICommDAO.getYdRulePI("", szMethodName, "YD0001", commUtils.trim(recPara.getFieldString("JSPEED_QUERY_ID")), "APPPI0", sPI_YD, "*" );
//				recPara.setField("JSPEED_QUERY_ID", commUtils.trim(toQuery_ID));	
//			}	

			
			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);
			
			//result recordSet check
			if(rsTemp.size() > 0)
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
	} //end of getYdEqp
	
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드설비 SELECT parameter Check
	 * 
	 * @param  JDTORecord inRec  parameter record
	 *         int        intGp  구분(
	 *         						0:YD_EQP_ID
	 *         						1:YD_GP ,YD_BAY_GP, YD_EQP_GP
	 *         					    2:YD_GP ,YD_BAY_GP, YD_EQP_GP
	 *         						3:YD_EQP_ID
	 *         						4:YD_GP
	 *                              5:YD_GP
	 *                              6:YD_EQP_ID
	 *                              8:YD_GP
	 *                              9:YD_GP, YD_BAY_GP
	 *                              10:YD_GP, YD_BAY_GP
	 *                              11:YD_GP, YD_BAY_GP, YD_PREP_WK_ST
	 *    							12:YD_EQP_ID
	 *     							13:YD_GP1, YD_GP2
	 *                              14:YD_GP, YD_BAY_GP
	 *                              15:YD_GP1, YD_BAY_GP, YD_GP2
	 *                              16:YD_EQP_ID (플렉스 전용)
	 *								)
	 * @return boolean           true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkPara_getYdEqp(JDTORecord inRec, int intGp) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;

		try {
			if(intGp == 0 || intGp == 12 || intGp ==16) {
				szFieldName = "V_YD_EQP_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
			}
			else if(intGp == 1){
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_EQP_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
			} else if(intGp == 2){
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_EQP_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
			} else if(intGp == 3 || intGp == 6){
				szFieldName = "V_YD_EQP_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				
			} else if(intGp == 4  || intGp == 8 ){
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
		    }  else if(intGp == 5){
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
		    }   else if(intGp == 7){
				szFieldName = "V_YD_WBOOK_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_EQP_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
		    } else if(intGp == 9 || intGp == 10 ){
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				
		    }  else if( intGp == 11 ){
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_PREP_WK_ST";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
		    } else if(intGp == 13){
				szFieldName = "V_YD_GP1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
		    	
				szFieldName = "V_YD_GP2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
		    } else if(intGp == 14){
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;				
		    } else if(intGp == 15){
				szFieldName = "V_YD_GP1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
		    	
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;				

				szFieldName = "V_YD_GP2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
		    }
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} // end of chkPara_getYdEqp
	
	
	
/*------------------------------------- INSERT -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드설비 INSERT
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int insYdEqp(JDTORecord inRec) throws DAOException, JDTOException {
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		JDTORecord recPara   = null;
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			
			//parameter check
			blnChk_Field = this.chkParameter(recPara);
			
			//parameter error return
			if(!blnChk_Field)
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
	} // end of insYdEqp
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드설비 INSERT parameter Check
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return boolean         true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkParameter(JDTORecord inRec) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		
		try {
			szFieldName = "V_YD_EQP_ID";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
			if(!blnErr) return blnErr;
	
			szFieldName = "V_REGISTER";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
			if(!blnErr) return blnErr;
			
			szFieldName = "V_REG_DDTT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
			if(!blnErr) return blnErr;
	
//			szFieldName = "V_MODIFIER";
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
//			if(!blnErr) return blnErr;
//	
//			szFieldName = "V_MOD_DDTT";
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
//			if(!blnErr) return blnErr;
//			
//			szFieldName = "V_DEL_YN";
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
//			if(!blnErr) return blnErr;
			
			szFieldName = "V_YD_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if(!blnErr) return blnErr;
			
			szFieldName = "V_YD_BAY_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if(!blnErr) return blnErr;
			
			szFieldName = "V_YD_EQP_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if(!blnErr) return blnErr;
			
			szFieldName = "V_YD_EQP_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if(!blnErr) return blnErr;
	
			szFieldName = "V_YD_WRK_ALW_XAXIS_TO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 7, 2, 'L', 0, 0);
			if(!blnErr) return blnErr;
	
			szFieldName = "V_YD_EQP_NAME";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 50, 2, 'S', 0, 0);
			if(!blnErr) return blnErr;
	
			szFieldName = "V_YD_EQP_STAT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if(!blnErr) return blnErr;
	
			szFieldName = "V_YD_EQP_WRK_MODE";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if(!blnErr) return blnErr;
			
			szFieldName = "V_YD_UP_WRK_MODE2";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if(!blnErr) return blnErr;
			
			szFieldName = "V_YD_DN_WRK_MODE2";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if(!blnErr) return blnErr;
			
			szFieldName = "V_YD_EQP_AUTO_CRN_MODE";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if(!blnErr) return blnErr;
	
			szFieldName = "V_YD_WRK_ALW_XAXIS_FR";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 7, 2, 'L', 0, 0);
			if(!blnErr) return blnErr;
			
			szFieldName = "V_YD_WRK_ALW_YAXIS_FR";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if(!blnErr) return blnErr;
			
			szFieldName = "V_YD_WRK_ALW_YAXIS_TO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if(!blnErr) return blnErr;
			
			szFieldName = "V_YD_WRK_ALW_ZAXIS_FR";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if(!blnErr) return blnErr;
			
			szFieldName = "V_YD_WRK_ALW_ZAXIS_TO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if(!blnErr) return blnErr;
			
			szFieldName = "V_YD_CRN_TRAVL_OFFSET";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 7, 2, 'L', 0, 0);
			if(!blnErr) return blnErr;
			
			szFieldName = "V_YD_CRN_GRAB_TP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if(!blnErr) return blnErr;
			
			szFieldName = "V_YD_CRN_TRAVS_OFFSET";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if(!blnErr) return blnErr;
			
			szFieldName = "V_YD_L2_HMI_STAT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if(!blnErr) return blnErr;
			
			szFieldName = "V_YD_CTS_RELAY_YN";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if(!blnErr) return blnErr;
			
			szFieldName = "V_YD_CTS_RELAY_BAY_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if(!blnErr) return blnErr;
			
			szFieldName = "V_YD_CRN_GRAB1_ACT_STAT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 4, 1);
			if(!blnErr) return blnErr;
			
			szFieldName = "V_YD_CRN_GRAB2_ACT_STAT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 4, 1);
			if(!blnErr) return blnErr;
			
			szFieldName = "V_YD_WRK_ABLE_XAXIS_FR";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 7, 2, 'L', 0, 0);
			if(!blnErr) return blnErr;
			
			szFieldName = "V_YD_WRK_ABLE_XAXIS_TO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 7, 2, 'L', 0, 0);
			if(!blnErr) return blnErr;
			
			szFieldName = "V_YD_WRK_ABLE_YAXIS_FR";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if(!blnErr) return blnErr;
			
			szFieldName = "V_YD_WRK_ABLE_YAXIS_TO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if(!blnErr) return blnErr;
			
			szFieldName = "V_YD_WRK_ABLE_ZAXIS_FR";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if(!blnErr) return blnErr;
			
			szFieldName = "V_YD_WRK_ABLE_ZAXIS_TO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if(!blnErr) return blnErr;

			szFieldName = "V_YD_CURR_BAY_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if(!blnErr) return blnErr;
		
			szFieldName = "V_YD_HOME_BAY_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if(!blnErr) return blnErr;
			
			szFieldName = "V_YD_TCAR_WRK_ABLE_BAY1";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if(!blnErr) return blnErr;
			
			szFieldName = "V_YD_TCAR_WRK_ABLE_BAY2";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if(!blnErr) return blnErr;
			
			szFieldName = "V_YD_TCAR_WRK_ABLE_BAY3";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if(!blnErr) return blnErr;
			
			szFieldName = "V_YD_TCAR_WRK_ABLE_BAY4";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if(!blnErr) return blnErr;
			
			szFieldName = "V_YD_TCAR_WRK_ABLE_BAY5";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			
			
			szFieldName = "V_YD_CRN_USE_SEQ";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'L', 0, 0);
			
			szFieldName = "V_YD_CRN_CONT_CARASGN_CNT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'L', 0, 0);
			
			szFieldName = "V_YD_CRN_CONT_CARASGN_WR";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'L', 0, 0);
			
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} //end of chkParameter
		
	
/*------------------------------------- UPDATE -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드설비 UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0:YD_EQP_ID)
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdEqp(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "updYdEqp";
		EJBConnector ejbConn 		= null;
		String szMsg = null;
		JDTORecord outRec = null;
		Integer iRtn               	= null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		
		try {
//			150903 hun 무인화 이후 작업지시 겹치는 현상 발생 트랜젝션 분리
			if( StringHelper.evl(inRec.getFieldString("YD_EQP_ID"),"").startsWith("H") || StringHelper.evl(inRec.getFieldString("YD_EQP_ID"),"") .startsWith("J")){ 
			
				ejbConn = new EJBConnector("default", "EqpTrackingSeEJB", this);
				iRtn =(Integer)ejbConn.trx("updYdEqpTX", new Class[] { JDTORecord.class }, new Object[] { inRec });
				if( iRtn.intValue() != YdConstant.RETN_INT_SUCCESS.intValue() ) {			//성공
					szMsg = "UPDATE 처리 실패 (" + iRtn + ")";
					ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
					return intRtnVal = -1;
				}
			}else{
				this.updYdEqpTX(inRec , intGp);
			}
			intRtnVal = 1;
			

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdEqp
	

	/**
	 *      [A] 오퍼레이션명 : 야드설비 UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0:YD_EQP_ID)
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdEqpTX(JDTORecord inRec, int intGp ) throws DAOException, JDTOException {
		String szMethodName = "updYdEqp";
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
			intRtnVal = this.getYdEqp(inRec, outRecSet, 0);
			
			//parameter error return
			if(intRtnVal < 0) {
	//			szMsg = "parameter error!";
	//			ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
				return intRtnVal;
			}
			
			//data not found return
			if(intRtnVal == 0) {
	//			szMsg = "data not found!";
	//			ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
				return intRtnVal;
			}
			
			//duplicate data return
			if(outRecSet.size() != 1) {
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
			if(!blnChk_Field) return intRtnVal = -2;
			
			//query id setting
			if(intGp == 0)
			recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd1);
			
			//query execute
			intRtnVal = dbAssDao.trtProcess(recOutPara);
			
			//execution error return
			if(intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdEqp
	
	/**
	 *      [A] 오퍼레이션명 : 야드설비 UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0:YD_EQP_ID)
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdEqpDirect(JDTORecord inRec, int intGp) throws DAOException, JDTOException {

		int intRtnVal = 0;
		
		try {
			JDTORecord recOutPara = null;
			//필드명 변환 (필드명 -> V_필드명)
			recOutPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//query id setting
			if(intGp == 1)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd2);
			else if(intGp == 2)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd3);
			else if(intGp == 3)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd4);
			else if(intGp == 4)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd5);
			else if(intGp == 300)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd300);
			else if(intGp == 400)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd400);
			else if(intGp == 900)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd900);
			else if(intGp == 901)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd901);
			else if(intGp == 902)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd902);
		
			//query execute
			intRtnVal = dbAssDao.trtProcess(recOutPara);
			
			//execution error return
			if(intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdEqpDirect
	
	/**
	 *      [A] 오퍼레이션명 : 야드설비 UPDATE parameter mapping
	 * 
	 * @param JDTORecord inRec
	 *        JDTORecord outRec
	 * @return void
	 * @throws JDTOException 
	 */		
	public void dataMapping(JDTORecord inRec, JDTORecord outRec) throws JDTOException {
		String szFieldName = null;

		try {
			szFieldName = "V_YD_EQP_ID";
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
			
			szFieldName = "V_YD_BAY_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_EQP_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_EQP_NO";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_WRK_ALW_XAXIS_TO";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_EQP_NAME";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_EQP_STAT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_EQP_WRK_MODE";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_EQP_WRK_MODE2";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_WRK_ALW_XAXIS_FR";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_WRK_ALW_YAXIS_FR";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_WRK_ALW_YAXIS_TO";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_WRK_ALW_ZAXIS_FR";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_WRK_ALW_ZAXIS_FR";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_WRK_ALW_ZAXIS_TO";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CRN_TRAVL_OFFSET";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CRN_GRAB_TP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CRN_TRAVS_OFFSET";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_L2_HMI_STAT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CTS_RELAY_YN";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CTS_RELAY_BAY_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CRN_GRAB1_ACT_STAT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CRN_GRAB2_ACT_STAT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_WRK_ABLE_XAXIS_FR";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_WRK_ABLE_XAXIS_TO";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_WRK_ABLE_YAXIS_FR";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_WRK_ABLE_YAXIS_TO";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_WRK_ABLE_ZAXIS_FR";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_WRK_ABLE_ZAXIS_TO";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_CURR_BAY_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_HOME_BAY_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			
			szFieldName = "V_YD_TCAR_WRK_ABLE_BAY1";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_TCAR_WRK_ABLE_BAY2";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_TCAR_WRK_ABLE_BAY3";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_TCAR_WRK_ABLE_BAY4";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_TCAR_WRK_ABLE_BAY5";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CRN_USE_SEQ";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CRN_CONT_CARASGN_CNT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CRN_CONT_CARASGN_WR";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_EQP_AUTO_CRN_MODE";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}

	} // end of dataMapping
	
	
	/**
	 * [A] 오퍼레이션명 : 프로시져 호출처리
	 * 프로시져 호출시
	 *  @author  권영원
	 * @date 	2009.01.19
	 * 
	 * @param jspeed_query_id
	 *            프로시저를 수행할 JSPEED에 등록되어 있는 QUERY KEY 정보입니다.
	 * @param add_query
	 *            프로시저를 수행할 쿼리에 동적으로 추가할 add_query 정보입니다.
	 * @param inParam
	 *            프로시저의 IN 파라미터에 매핑할 IN 파라미터 배열정보입니다.
	 * @param inParamIndex
	 *            프로시저의 IN 파라미터가 매핑되는 인덱스 정보입니다.
	 * @param outParamKey
	 *            프로시저의 OUT 파라미터가 리턴되는 JDTORecord의 KEY값 정보입니다.
	 * @param outParamType
	 *            프로시저의 OUT 파라미터가 리턴되는 오라클 데이터 베이스의 SQL TYPE 정보입니다.
	 * @param outParamIndex
	 *            프로시저의 OUT 파라미터가 매핑되는 인덱스 정보입니다.
	 */
	
	public JDTORecord ProcedureMaxLot(JDTORecord inDto) {
		 DBAssistantDAO assistantDAO = new DBAssistantDAO();
		/*
		 * { call SP_YD_CAR_MAXLOT(?,?,?,?,?) } --삭제
		 */
		try {
			//com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarSchByBayInSeqPlateLoc
			String jspeed_query_id = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.ProcedureMaxLot";
			String add_query = "";
			Object[] inParam = { StringHelper.evl(inDto.getFieldString("YD_STOP_LOC"),"")
								,StringHelper.evl(inDto.getFieldString("CAR_LOTID"),"")
								,StringHelper.evl(inDto.getFieldString("YD_CAR_SCH_ID"),"")
								,StringHelper.evl(inDto.getFieldString("FIRST_CAR_LOT_ID"),"")};
			int[] inParamIndex = {1,2,3,4};
	 		String[] outParamKey = {"OUT_RTN_CODE"};
	 		int[] outParamType = {Types.VARCHAR}; // Types.INTEGER, Types.NUMERIC, Types.DECIMAL
	 		int[] outParamIndex = {5};
	 		
			return assistantDAO.trtProcedure(jspeed_query_id, add_query, inParam, inParamIndex, outParamKey, outParamType, outParamIndex);

		} catch (Exception e) {

			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally{

		}
	}
	
	public JDTORecord ProcedureSelDong(JDTORecord inDto) {
		 DBAssistantDAO assistantDAO = new DBAssistantDAO();
		/*
		 * { call SP_YD_SEL_002(?,?,?,?) }
		 */
		try {

			String jspeed_query_id = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.ProcedureSelDong";
			String add_query = "";
			//--2013.02.27 수정 (3기)
			Object[] inParam = { StringHelper.evl(inDto.getFieldString("YD_GP"),YdConstant.YD_GP_PLATE_GDS_YARD)
								,StringHelper.evl(inDto.getFieldString("DONG"),"")
								,StringHelper.evl(inDto.getFieldString("GATE"),"")};
			
			int[] inParamIndex = {1,2,3};
	 		String[] outParamKey = {"OUT_RTN_CODE"};
	 		int[] outParamType = {Types.VARCHAR}; // Types.INTEGER, Types.NUMERIC, Types.DECIMAL
	 		int[] outParamIndex = {4};
	 		
			return assistantDAO.trtProcedure(jspeed_query_id, add_query, inParam, inParamIndex, outParamKey, outParamType, outParamIndex);

		} catch (Exception e) {

			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally{

		}
	}
	
	public JDTORecord ProcedureSelYdAll(JDTORecord inDto) {
		 DBAssistantDAO assistantDAO = new DBAssistantDAO();
		/*
		 * { call SP_YD_SEL_004(?,?,?) }
		 */
		try {

			String jspeed_query_id = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.ProcedureSelYdAll_PIDEV";
			String add_query = "";
			//--2013.02.28 수정 (3기)
			Object[] inParam = { StringHelper.evl(inDto.getFieldString("YD_GP"),YdConstant.YD_GP_PLATE_GDS_YARD) ,
					             StringHelper.evl(inDto.getFieldString("YD_ALL_SEND_GBN"),"")};
			
			int[] inParamIndex = {1,2};
	 		String[] outParamKey = {"OUT_RTN_CODE"};
	 		int[] outParamType = {Types.VARCHAR}; // Types.INTEGER, Types.NUMERIC, Types.DECIMAL
	 		int[] outParamIndex = {3};
	 		
//			return assistantDAO.trtProcedure(jspeed_query_id, add_query, inParam, inParamIndex, outParamKey, outParamType, outParamIndex);
	 		//PIDEV
	 		//PIDEV_S :병행가동용:PI_YD
	 			 					
//			String toQuery_ID = ydPICommDAO.getYdRulePI("", "", "YD0001", jspeed_query_id, "APPPI0", "T", "*" );
			return assistantDAO.trtProcedure(jspeed_query_id, add_query, inParam, inParamIndex, outParamKey, outParamType, outParamIndex);
	 		

		} catch (Exception e) {

			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally{

		}
	}

	public JDTORecord ProcedureSelYd(JDTORecord inDto) {
		 DBAssistantDAO assistantDAO = new DBAssistantDAO();
		/*
		 * { call SP_YD_SEL_001(?,?) }
		 */
		try {

			String jspeed_query_id = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.ProcedureSelYd_PIDEV";
			String add_query = "";
			
			String sYdGp = StringHelper.evl(inDto.getFieldString("WH_GP"), YdConstant.YD_GP_PLATE_GDS_YARD);  //--2013.02.27 추가 (3기)
			
			Object[] inParam = { sYdGp }; //--2013.02.27 수정 (3기)
			 
			int[] inParamIndex = {1};
	 		String[] outParamKey = {"OUT_RTN_CODE"};
	 		int[] outParamType = {Types.VARCHAR}; // Types.INTEGER, Types.NUMERIC, Types.DECIMAL
	 		int[] outParamIndex = {2}; 
	 		
//			return assistantDAO.trtProcedure(jspeed_query_id, add_query, inParam, inParamIndex, outParamKey, outParamType, outParamIndex);
//PIDEV
//PIDEV_S :병행가동용:PI_YD
	 			 					
//			String toQuery_ID = ydPICommDAO.getYdRulePI("", "", "YD0001", jspeed_query_id, "APPPI0", "T", "*" );
			return assistantDAO.trtProcedure(jspeed_query_id, add_query, inParam, inParamIndex, outParamKey, outParamType, outParamIndex);

		} catch (Exception e) {

			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally{

		}
	}
	
	public JDTORecord ProcedureSetOffPilingInfo(JDTORecord inDto) {
		 DBAssistantDAO assistantDAO = new DBAssistantDAO();
		/*
		 * { call SP_YD_PLATE_PILING_CHANGE_OFF(?,?,?) }- 삭제
		 */
		try {

			String jspeed_query_id = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.ProcedureSetOffPilingInfo";
			String add_query = "";
			Object[] inParam = { StringHelper.evl(inDto.getFieldString("PLATE_NO"),"")
								,StringHelper.evl(inDto.getFieldString("YD_STK_COL_GP"),"")};
			
			int[] inParamIndex = {1,2};
	 		String[] outParamKey = {"OUT_RTN_CODE"};
	 		int[] outParamType = {Types.VARCHAR}; // Types.INTEGER, Types.NUMERIC, Types.DECIMAL
	 		int[] outParamIndex = {3};
	 		
			return assistantDAO.trtProcedure(jspeed_query_id, add_query, inParam, inParamIndex, outParamKey, outParamType, outParamIndex);

		} catch (Exception e) {

			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally{

		}
	}
	
	public JDTORecord ProcedureSelYdLotid(JDTORecord inDto) {
		 DBAssistantDAO assistantDAO = new DBAssistantDAO();
		/*
		 * { call SP_YD_SEL_003(?,?,?) }
		 */
		try {

			
			String jspeed_query_id = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.ProcedureSelYdLotid_PIDEV";
			String add_query = "";
			Object[] inParam = { StringHelper.evl(inDto.getFieldString("YD_STK_COL_GP"),"")
								,StringHelper.evl(inDto.getFieldString("YD_STK_BED_NO"),"")};
			
			int[] inParamIndex = {1,2};
	 		String[] outParamKey = {"OUT_RTN_CODE"};
	 		int[] outParamType = {Types.VARCHAR}; // Types.INTEGER, Types.NUMERIC, Types.DECIMAL
	 		int[] outParamIndex = {3};
	 		
//PIDEV			
//			return assistantDAO.trtProcedure(jspeed_query_id, add_query, inParam, inParamIndex, outParamKey, outParamType, outParamIndex);	 						
//PIDEV
//PIDEV_S :병행가동용:PI_YD
	 					
//			String toQuery_ID = ydPICommDAO.getYdRulePI("", "", "YD0001", jspeed_query_id, "APPPI0", "T", "*" );
			return assistantDAO.trtProcedure(jspeed_query_id, add_query, inParam, inParamIndex, outParamKey, outParamType, outParamIndex);

		} catch (Exception e) {

			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally{

		}
	}
	
	public JDTORecord ProcedureAbmtlPmCall(JDTORecord inDto) {
		 DBAssistantDAO assistantDAO = new DBAssistantDAO();
		 
		 /*
            USRPMA.SP_PMC3_606_여재처리메인 (IN_JOB_GBN             IN  VARCHAR2         
                                          ,IN_STL_NO            IN  VARCHAR2         
                                          ,IN_YEOJAE_CAUSE_CD   IN  VARCHAR2    
                                          ,IN_STR_GP 			IN  VARCHAR2      
                                          ,W_ERR_CODE           OUT VARCHAR2 ) 

            IN_JOB_GBN : Y ( YARD JOB으로 로그 관리 )
            IN_STL_NO   : 재료 단위 한 매씩 호출 함. 
            IN_YEOJAE_CAUSE_CD : 여재 원인코드 ( 이상재 코드 일단 넣어주삼 => 변경이 필요할지도.....?? )
            IN_STR_GP : 비축재구분 Default 'N'
            W_ERR_CODE : 이건 ERROR 가 발생했을때 야드에서 OUTPUT 으로 활용하기 위함 ( NULL 이 아닌 값 )
		*/
		 
		/*
		 * { call USRPMA.SP_PMC3_606_여재처리메인(?,?,?,?,?) }
		 */
		try {

			String jspeed_query_id = "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.ProcedureAbmtlPmCall";
			String add_query = "";
			Object[] inParam = {"Y" 
					            ,StringHelper.evl(inDto.getFieldString("SLAB_NO"),"")
								,StringHelper.evl(inDto.getFieldString("AB_OCCR_RSN_CD"),"")
								,"N"};
			
			int[] inParamIndex = {1,2,3,4};
	 		String[] outParamKey = {"OUT_RTN_CODE"};
	 		int[] outParamType = {Types.VARCHAR}; // Types.INTEGER, Types.NUMERIC, Types.DECIMAL
	 		int[] outParamIndex = {5};
	 		
			return assistantDAO.trtProcedure(jspeed_query_id, add_query, inParam, inParamIndex, outParamKey, outParamType, outParamIndex);

		} catch (Exception e) {

			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally{

		}
	}
	
	

	/**
	 *      [A] 오퍼레이션명 : chkAutoCrn 
	 * 
	 * @param int intMaxLen         // Field Length
	 * @return String     			// 데이터 길이로 보정된 String
	 */	
	public boolean chkAutoCrn(String szYD_EQP_ID) {
		
		String szYD_EQP_ID_GET = "";
		String szMethodName = "YdEqpDao.chkAutoCrn";
		JDTORecord	recInTemp					= JDTORecordFactory.getInstance().create();
		JDTORecord	inRec					= JDTORecordFactory.getInstance().create();
		YdUtils ydUtils =new YdUtils();
		
    	int intRtnVal = 0;
    	JDTORecordSet		outRdSet 		= null;
    	ymCommonDAO dao = ymCommonDAO.getInstance();
		try {
			
			inRec.setField("YD_EQP_ID"	, szYD_EQP_ID);
			
			if(!"".equals(szYD_EQP_ID) && !(szYD_EQP_ID == null)){  
				ydUtils.putLog(szDaoName, szMethodName, "szYD_EQP_ID ="+szYD_EQP_ID, 1);
				
				recInTemp = dao.getCodeToName("com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.ChkCrnMode2" , new Object[]{szYD_EQP_ID });
				
				szYD_EQP_ID_GET    = StringHelper.evl(recInTemp.getFieldString("YD_EQP_WRK_MODE2"), "");
				
				szYD_EQP_ID	= ydDaoUtils.paraRecChkNull(inRec, "YD_EQP_ID");
				ydUtils.putLog(szDaoName, szMethodName, "YD_EQP_WRK_MODE2 ="+szYD_EQP_ID_GET, 1);
				
				
				if("A".equals(szYD_EQP_ID_GET) ||"R".equals(szYD_EQP_ID_GET)){
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
	

	/**
	 *      [A] 오퍼레이션명 : chkAutoCrn 
	 * 
	 * @param String szYdStkColGp
	 * @return boolean     			// 고정스키드(F)일때 true
	 */	
	public boolean chkFixedSkid(String szYdStkColGp) {
		
		String szszYdStkColGpGet = "";
		String szMethodName = "YdEqpDao.chkFixedSkid";
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
	

	/**
	 *      [A] 오퍼레이션명 : chkCoilSupMtdGp 
	 * 
	 * @param String szYdStkColGp
	 * @return boolean     			// 고정스키드(F)일때 true
	 */	
	public boolean chkCoilSupMtdGp(String szCoilNo, String szYdStkColGp, String szYdStkBedNo, String szYdStkLyrNo ) {
		
		String szYdCoilNoGet = "";
		String szMethodName = "YdEqpDao.chkCoilSupMtdGp";
		JDTORecord	recInTemp				= JDTORecordFactory.getInstance().create();
		YdUtils ydUtils =new YdUtils();
		
    	ymCommonDAO dao = ymCommonDAO.getInstance();
		try {
			
			if(!"".equals(szCoilNo) && !(szCoilNo == null)){  
				ydUtils.putLog(szDaoName, szMethodName, "szCoilNo ="+szCoilNo, 1);
				
				recInTemp = dao.getCodeToName("com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getCoilSupmtdGp" , new Object[]{szYdStkColGp, szYdStkBedNo, szYdStkLyrNo, szCoilNo });
				
				if(recInTemp != null){
					szYdCoilNoGet    = StringHelper.evl(recInTemp.getFieldString("COIL_NO"), "");
					
					ydUtils.putLog(szDaoName, szMethodName, "COIL_NO ="+szYdCoilNoGet, 1);
				}
				
				if(!"".equals(szYdCoilNoGet)){
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






