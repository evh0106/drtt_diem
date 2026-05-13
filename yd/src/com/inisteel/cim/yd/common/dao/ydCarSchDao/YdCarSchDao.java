package com.inisteel.cim.yd.common.dao.ydCarSchDao;

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
 *      [A] 클래스명 : 야드차량스케줄 DAO
 * 
*/

public class YdCarSchDao {
	
	// Dao Name
	private String szDaoName = getClass().getName();
	
	private YdUtils ydUtils = new YdUtils();	

	private DBAssistantDAO dbAssDao = new DBAssistantDAO();
	
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();
	private YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
	private CCommUtils commUtils = new CCommUtils();
	
	//select query id
	private String szQueryIdGet1 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarsch";
	private String szQueryIdGet2 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschYDGP";
	private String szQueryIdGet3 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschYDGP_PAGE";
	private String szQueryIdGet4 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschUDLDWRKBOOKID_PIDEV";
	private String szQueryIdGet5 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarYdStockYdCarFtmvMtl";
	private String szQueryIdGet6 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschYdCarFtmvMtlCoilComm";
	private String szQueryIdGet7 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschYdCarFtmvMtlMSlabComm";	

	private String szQueryIdGet8 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschDaoTrnEqpCd";
	private String szQueryIdGet9 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschDaoEqpId";
	
	//20090310
	private String szQueryIdGet10 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschDaoByYdCar";
	
	private String szQueryIdGet11 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschDaoByYdCarNoOrTrnEqpCd";
	
	private String szQueryIdGet12 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschCarNoCardNo_PIDEV";	
	// 차량별 작업상태 관리(심명순)
	private String szQueryIdGet13 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getCoilYdCarWorkList";
    // 차량모니터링 조회 (심명순)
	private String szQueryIdGet14 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getCoilYdGdsCarMonitoring";
	// 재료상세백업(차량정보)-김창일
	private String szQueryIdGet15 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getCoilYdStlDtlBakcupCarSch";
	// 이송대상재 조회 -김창일
	private String szQueryIdGet16 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getCoilYdFrtMoveGpList";
	private String szQueryIdGet17 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getCoilYdMvMtlList";
	
	// 후판창고제품 차량모니터링 조회 (김종건)
	private String szQueryIdGet18 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarMonitoring";
	
	private String szQueryIdGet19 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschYdCarFtmvMtlSlabComm";
	
	// 후판창고제품 차량별 작업상태 관리 (김종건)
	private String szQueryIdGet20 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdGdsCarWorkStatMgt";
	
	// 후판창고제품 반납 크레인 차량 조회 (김종건)
	private String szQueryIdGet21 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdIdelCar";
	
	
	// 차량스케줄에 차량번호와 운송장비번호로 스케줄이 잡혀 있는지 체크 (이현성)
	private String szQueryIdGet22 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdIdelCarCheckSch";
	// 심명순 090723 차량작업관리 화면 배차내역 조회 
	private String szQueryIdGet23 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getCoilYdGdsOutCar";
	// 심명순 090727 차량작업관리 화면 차량스케줄  조회 
	private String szQueryIdGet24 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getCoilYdGdsCarSch";
	// 임춘수 2009.08.26 차량작업관리 화면 차량스케줄  조회

	// 권오창 20090820 
	private String szQueryIdGet25 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschTransDTSeq";

	private String szQueryIdGet26 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarSchByWlocCd";
	//배차내역 - 개소코드, 차량정지위치
	private String szQueryIdGet27 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarAsgnInfoByWlocCd";
	// 임춘수 2009.08.27 - 운송장비코드 OR 차량번호, 카드번호 로 차량스케줄 조회
	private String szQueryIdGet28 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschByTrnEqpCdCarNoCardNo_PIDEV";
	
	// 권오창 20090903
	private String szQueryIdGet29 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarYdStockYdCarFtmvMtlSTLNo";
	
	// 이현성 20090907
	private String szQueryIdGet30 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getSlabTotYdInOutList";

	// 이현성 20090907
	private String szQueryIdGet100= "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getSlabTotYdInOutListDong";

	
	// 임춘수 20090909 - 차량정지위치에 입동대기중인 차량스케줄의 입동순서 LIST
	private String szQueryIdGet31 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarSchByBayInSeq";
	// 임춘수 2009.10.20 - 통합야드 배차내역 조회 쿼리 : 조건 - 개소코드, 차량정지위치
	private String szQueryIdGet32 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarAsgnInfoNPrepSchByWlocCd";
	
	// 야드별 차량스케줄 - 차량번호 List PDA 화면
	private String szQueryIdGet33 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarNoList";

	// 2009.12.22 권오창 - 개소코드와 포인트코드로 차량번호 조회(나중에 포인트코드는 수정)
	private String szQueryIdGet34 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarNoByWLocCDPntCD";
	
	// 운송지시일자, 운송지시순번으로 차량스케줄 조회 - 임춘수 2009.12.29
	private String szQueryIdGet35 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschByTransDTSeq_PIDEV";
	
	// 재료번호로  차량스케줄 조회 - 이현성 2010.01.11
	private String szQueryIdGet36 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschByStlNo";

	// 야드별 차량스케줄 - 차량번호 List PDA 화면
	private String szQueryIdGet37 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getydInspectCarNoList";
	
	// 야드별 차량스케줄
	private String szQueryIdGet300 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarSchByWlocCdCoil";
	// 야드별 차량스케줄
	private String szQueryIdGet301 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarSchByWlocCdCoil_H";
	
	private String szQueryIdGet302 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarYdStockYdCarFtmvMtl2_PIDEV";
	
	private String szQueryIdGet303 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarYdStockYdRetnWoCmtStat_PIDEV";
	private String szQueryIdGet304 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarYdDEL_YN";
	private String szQueryIdGet305 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschYdCarMtlCoilComm";
	private String szQueryIdGet306 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschYdCarMtlYMins";

	//  - 차량정지위치에 입동대기중인 차량스케줄의 입동순서 LIST
	private String szQueryIdGet307 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarSchByBayInSeqCoil_PIDEV";
	private String szQueryIdGet308 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarSchByWlocCdCoil_J_PIDEV";

//	C열연 제품 배차내역 - 개소코드, 차량정지위치
	private String szQueryIdGet309 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarAsgnInfoByWlocCd2";
	
//	후판제품창고
	private String szQueryIdGet310 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdCarSchByInSeqCheck";
	private String szQueryIdGet311 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdCarUppRuleMgt";
	private String szQueryIdGet320 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarAsgnInfoNPrepSchByWlocCdStl_PIDEV";
	
	private String szQueryIdGet400 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgt";
	private String szQueryIdGet401 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdBayLocPlnMgt";
	
	private String szQueryIdGet402 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdCarSchhackaCheck";
	private String szQueryIdGet403 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdBayLocPlnMgtCode";
//	후판제품창고 입동지시
	private String szQueryIdGet406 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarSchByBayInSeqPlate";
//	후판제품창고 입동지시 (사용안함:2012.04.10 윤재광)
	private String szQueryIdGet407 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarSchByBayInSeqPlateLoc";
//  407번 대신사용쿼리
	private String szQueryIdGet410 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarSchByBayInSeqPlateMax";
	private String szQueryIdGet411 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarSchByWlocCdCoil_NEW_PIDEV";
	private String szQueryIdGet412 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarPoint";
//	후판제품창고 입동지시
	private String szQueryIdGet408 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarSchByBayInSeqPlateTarget";
	
	private String szQueryIdGet409 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getListloadStoppointGD";
	
	private String szQueryIdGet999 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN";
	
	
	private String szQueryIdGet404 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getCoilTotYdInOutList";

	private String szQueryIdGet405= "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getCoilTotYdInOutListDong";
	
	private String szQueryIdGet413= "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschTransDTSeq2_PIDEV";
	
	private String szQueryIdGet414= "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getCoilYdGdsOutCarCoilNEW";
	
	private String szQueryIdGet415= "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschDaoWrkBookTrnEqpCd";
	
	private String szQueryIdGet416= "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getCoilTotYdInOutListAB";
	
	private String szQueryIdGet417= "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getCoilTotYdInOutListDongB";
	
	private String szQueryIdGet418= "com.inisteel.cim.yd.ydStock.RouteModReg.procCoilGdsTrnOrdNEW.getHandlingCnt";
	
	private String szQueryIdGet419= "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarYdCmbnCarldYn_PIDEV";
	
	private String szQueryIdGet420= "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarPointSelect";
	
	private String szQueryIdGet421= "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarPointCarSchSelect";
	
	private String szQueryIdGet422= "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarPointCHK";
	
	private String szQueryIdGet423= "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarYdCmbnCarldGP";
	
	private String szQueryIdGet424= "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarYdCmbnCarSch";
	
	
	private String szQueryIdGet425= "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarwbookid";
	
	private String szQueryIdGet426= "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getCoilYdGdsOutCarCoilNEW2_PIDEV";
	
	private String szQueryIdGet427= "ym.facilitystatus.facilityinquiry.dao.YardFicilityDAO.getCoilMoveResult";
	
	private String szQueryIdGet428= "ym.facilitystatus.facilityinquiry.dao.YardFicilityDAO.getCoilMoveResultListA";
	
	private String szQueryIdGet429= "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getCoilYdCarWorkingList";
	
	private String szQueryIdGet430= "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarPointCHK2";
	
	private String szQueryIdGet431 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschCarNoCardNoTransNo_PIDEV";	
	
	private String szQueryIdGet432 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschCarNoCardNoTransNoCHK";	
	
	private String szQueryIdGet433 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschByStlNoCarID";
	
	private String szQueryIdGet434 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarYdStockYdCarFtmvMtlMES";
	
	private String szQueryIdGet435 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschCarGetInWorkInfo_PIDEV";
	
	private String szQueryIdGet436 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdGetCarNoByLoc";
	
	private String szQueryIdGet437 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschCarGetInWorkByCarNo_PIDEV";
	
	private String szQueryIdGet438 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschCarGetInWorkInfoChk";
	
	private String szQueryIdGet439 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarStlYardInfoChk_PIDEV";
	
	private String szQueryIdGet440 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschDaoCarPointByBay";
	
	private String szQueryIdGet441 = "ym.facilitystatus.facilityinquiry.dao.YardFicilityDAO.getConResultList";
	
	private String szQueryIdGet442 = "com.inisteel.cim.yd.common.dao.YdPlateCommDao.getYdCarschDaoCarStopLocByBay"; //포인트별 대기장 차량 리스트
	
	private String szQueryIdIns1 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.insYdCarsch";
	
//	insert query id
	private String szQueryIdIns2 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.insYdCarsch1";
	
	//update query id
	private String szQueryIdUpd1 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.updYdCarsch";
	//차량스케쥴고유키 생성 쿼리
	private String szQueryIdKey = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschId";
	
	//다이렉트업데이트 쿼리 20090522 김진욱
	private String szQueryIdDirUpd1 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.updYdCarschYdCarWrkBookId";
	private String szQueryIdDirUpd2 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.updYdCarschYdCarWrkBookId1";
	private String szQueryIdDirUpd3 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.updYdCarschYdCarWrkBookId2";
	private String szQueryIdDirUpd4 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.updYdCarschYdCarWrkBookId3";
	
	private String szQueryIdDirUpd5 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.updYdCarschYdCarLdWrkBookId"; //  상차 차량 작업 예약 ID CLEAR
	private String szQueryIdDirUpd6 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.updYdCarschYdCarUdWrkBookId"; //  하차 차량 작업 예약 ID CLEAR
	
	// 차량 스케줄 - 야드차량출고검수여부, 야드차량출고검수자 Update 후판제품 출고검수화면 PDA
	private String szQueryIdDirUpd7 = "com.inisteel.cim.yd.dao.ydcarschdao.ydCarSchDao.UpdateISSUECHK";

	// 권오창 2009.12.01 차량 스케줄 - 야드차량입고검수여부, 야드차량입고검수자 Update 후판제품 입고검수화면 PDA
	private String szQueryIdDirUpd8 = "com.inisteel.cim.yd.dao.ydcarschdao.ydCarSchDao.UpdateInputISSUECHK";
	
	
	private String szQueryIdDirUpd9 = "com.inisteel.cim.yd.dao.ydcarschdao.ydCarSchDao.UpdateLoadEndTime";
	private String szQueryIdDirUpd90 = "com.inisteel.cim.yd.dao.ydcarschdao.ydCarSchDao.UpdateCarSchProgStat";
	
	private String szQueryIdDirUpd10 = "com.inisteel.cim.yd.dao.ydcarschdao.ydCarSchDao.updYdCarschTransOrd";
	private String szQueryIdDirUpd11 = "com.inisteel.cim.yd.dao.ydcarschdao.ydCarSchDao.updYdExamTransOrd";
	private String szQueryIdDirUpd12 = "com.inisteel.cim.yd.dao.ydcarschdao.ydCarSchDao.updYdStockTransOrd";
	private String szQueryIdDirUpd13 = "com.inisteel.cim.yd.dao.ydcarschdao.ydCarSchDao.updYmStockTransOrd";
	private String szQueryIdDirUpd14 = "com.inisteel.cim.yd.dao.ydcarschdao.ydCarSchDao.updYdCarpoint";

	private String szQueryIdDirUpd300 = "com.inisteel.cim.yd.dao.ydcarschdao.ydCarSchDao.updYdCarschYdCoil";
	private String szQueryIdDirUpd301 = "com.inisteel.cim.yd.dao.ydcarschdao.ydCarSchDao.updPlateYdCarUppRuleMgt";
	private String szQueryIdDirUpd302 = "com.inisteel.cim.yd.dao.ydcarschdao.ydCarSchDao.updPlateYdBayLocPlnMgt";
	private String szQueryIdDirUpd303 = "com.inisteel.cim.yd.dao.ydcarschdao.ydCarSchDao.updYdCarschYdBayinWoSeq";
	private String szQueryIdDirUpd304 = "com.inisteel.cim.yd.dao.ydcarschdao.ydCarSchDao.updYdCarschYdCoil2";
	private String szQueryIdDirUpd305 = "com.inisteel.cim.yd.dao.ydcarschdao.ydCarSchDao.updYdCarschYdCarRcptChkYn";                
	
	/**
	 * 오퍼레이션명 : 야드차량스케쥴ID를 생성하여 반환하는 메소드
	 * @return String : 야드차량스케쥴ID
	 */
	public String getYdCarschId() throws DAOException {
		//메소드명
		String szMethodName = "getYdCarschId";
		//레코드
		JDTORecord recKey = JDTORecordFactory.getInstance().create();
		//차량스케쥴ID
		String szYdCarSchId = "";
		try {
			//JSPEED 쿼리ID
			recKey.setField("JSPEED_QUERY_ID", szQueryIdKey);
			//쿼리 실행
			JDTORecordSet rsTemp = dbAssDao.getRecordSet(recKey);
			if( rsTemp.size() <= 0 ) {
				throw new JDTOException("야드차량스케줄ID 레코드가 존재하지 않음");
			}
			rsTemp.first();
			recKey = rsTemp.getRecord();
		
			szYdCarSchId = ydDaoUtils.paraRecChkNull(recKey, "YD_CAR_SCH_ID");
		}catch(JDTOException e) {
			String szMsg = "야드차량스케줄ID 생성 시 에러 발생";
			ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
			throw new DAOException(szMsg, e);
		}
		return szYdCarSchId;
	}
	
/*------------------------------------- SELECT -------------------------------------------*/
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드차량스케줄 SELECT
	 *      
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         int intGp                구분(0:YD_CAR_SCH_ID,
	 *                                      1:YD_GP,
	 *                                      2:YD_GP[PAGE],
	 *                                      3:YD_CARUD_WRK_BOOK_ID,YD_CARLD_WRK_BOOK_ID,
	 *                                      4:YD_CAR_SCH_ID,
	 *                                      5:YD_CAR_SCH_ID,
	 *                                      6:YD_CAR_SCH_ID
	 *                                      7:TRN_EQP_CD
	 *                                      8:YD_EQP_ID
	 *                                      9:YD_CAR
	 *                                      10:CAR_NO, TRN_EQP_CD
	 *                                      11:CAR_NO, CARD_NO)
	 *                                      17:YD_GP, POINT_GP, YD_EQP_GP
	 *                                      18:YD_CAR_SCH_ID
	 *                                      19:YD_GP, SEARCH_GBN, PAGE_CNT1, PAGE_CNT2, ROW_CNT1, ROW_CNT2
	 *                                      20:YD_CAR_USE_GP
	 *                                      21:CAR_NO , TRN_EQP_CD
	 *                                      24:CAR_NO, CARD_NO
	 *                                      25:WLOC_CD, YD_GP, YD_BAY_GP
	 *                                      25:WLOC_CD, YD_CAR_STOP_LOC
	 *                                      27:YD_CAR_USE_GP, TRN_EQP_CD, CAR_NO, CARD_NO
	 *                                      28:STL_NO, YD_CAR_SCH_ID
	 *                                      29:DATE_FROM,DATE_TO
	 *                                      30:YD_CAR_STOP_LOC(YD_STK_COL_GP)
	 *                                      33:WLOC_CD,YD_PNT_CD
	 *                                      34:TRANS_ORD_DATE, TRANS_ORD_SEQNO
	 *                                      35:STL_NO
	 *                                      36:
	 *                                      )
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int getYdCarsch(JDTORecord inRec, JDTORecordSet outRecSet, int intGp) throws DAOException, JDTOException {
		String szMethodName = "getYdCarsch";
		JDTORecordSet rsTemp = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		String szMsg = null;
		JDTORecord recPara = null;
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			
			//parameter check
			blnChk_Field = this.chkPara_getYdCarsch(recPara, intGp);
			
			//parameter error return
			if (!blnChk_Field)
				return intRtnVal = -2;
			
			//query id setting
			if (intGp == 0)
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
			else if(intGp == 19)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet20);
			else if(intGp == 20)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet21);
			else if(intGp == 21)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet22);
			else if(intGp == 22)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet23);
			else if(intGp == 23)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet24);
			else if(intGp == 24)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet25);
			else if(intGp == 25)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet26);
			else if(intGp == 26)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet27);
			else if(intGp == 27)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet28);
			else if(intGp == 28)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet29);
			else if(intGp == 29)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet30);
			else if(intGp == 30)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet31);
			else if(intGp == 31)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet32);
			else if(intGp == 32)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet33);			
			else if(intGp == 33)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet34);
			else if(intGp == 34)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet35);
			else if(intGp == 35)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet36);
			else if(intGp == 36)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet37);
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
			else if(intGp == 305)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet305);
			else if(intGp == 306)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet306);
			else if(intGp == 307)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet307);
			else if(intGp == 308)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet308);
			else if(intGp == 309)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet309);
			else if(intGp == 310)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet310);
			else if(intGp == 311)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet311);
			else if(intGp == 320)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet320);
			else if(intGp == 400)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet400);
			else if(intGp == 401)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet401);
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
			else if(intGp == 408)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet408);
			else if(intGp == 409)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet409);
			else if(intGp == 410)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet410);
			else if(intGp == 999)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet999);
			else if(intGp == 411)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet411);
			else if(intGp == 412)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet412);
			else if(intGp == 413)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet413);
			else if(intGp == 414)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet414);
			else if(intGp == 415)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet415);
			else if(intGp == 416)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet416);
			else if(intGp == 417)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet417);
			else if(intGp == 418)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet418);
			else if(intGp == 419)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet419);
			else if(intGp == 420)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet420);
			else if(intGp == 421)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet421);
			else if(intGp == 422)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet422);
			else if(intGp == 423)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet423);
			else if(intGp == 424)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet424);
			else if(intGp == 425)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet425);
			else if(intGp == 426)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet426);
			else if(intGp == 427)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet427);
			else if(intGp == 428)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet428);
			else if(intGp == 429)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet429);
			else if(intGp == 430)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet430);
			else if(intGp == 431)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet431);
			else if(intGp == 432)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet432);
			else if(intGp == 433)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet433);
			else if(intGp == 434)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet434);
			else if(intGp == 435)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet435);
			else if(intGp == 436)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet436);
			else if(intGp == 437)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet437);
			else if(intGp == 438)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet438);
			else if(intGp == 439)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet439);
			else if(intGp == 440)
				recPara.setField("JSPEED_QUERY_ID",	szQueryIdGet440);
			else if(intGp == 441)
				recPara.setField("JSPEED_QUERY_ID",	szQueryIdGet441);
			else if(intGp == 442)
				recPara.setField("JSPEED_QUERY_ID",	szQueryIdGet442);			
			
			
			String mthdNm = "YdCarSchDao.getYdCarsch";
			
			
			// PIDEV_S :병행가동용:PI_YD
//			String sPI_YD     = commUtils.nvl(recPara.getFieldString("V_PI_YD"), "*");
//			
//			String toQuery_ID = ydPICommDAO.getYdRulePI("", mthdNm, "YD0001", recPara.getField("JSPEED_QUERY_ID").toString(), "APPPI0", sPI_YD, "*" );
//			recPara.setField("JSPEED_QUERY_ID", toQuery_ID);	

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
	} //end of getYdCarsch
	
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드차량스케줄 SELECT parameter Check
	 * 
	 * @param  JDTORecord inRec  parameter record
	 *         int intGp         구분(0:YD_CAR_SCH_ID,
	 *                               1:YD_GP,
	 *                               2:YD_GP[PAGE],
	 *                               3:YD_CARUD_WRK_BOOK_ID,YD_CARLD_WRK_BOOK_ID,
	 *                               4:YD_CAR_SCH_ID,
	 *                               5:YD_CAR_SCH_ID,
	 *                               6:YD_CAR_SCH_ID
	 *                               7:TRN_EQP_CD
	 *                               8:YD_EQP_ID
	 *                               9:YD_CAR 
	 *                               10:CAR_NO, TRN_EQP_CD)
	 *                               11:CAR_NO, CARD_NO
	 *                               17:YD_GP, POINT_GP, YD_EQP_GP
	 *                               18:YD_CAR_SCH_ID
	 *                               19:YD_GP, SEARCH_GBN, PAGE_CNT1, PAGE_CNT2, ROW_CNT1, ROW_CNT2
	 *                               20:YD_CAR_USE_GP
	 *                               21:CAR_NO , TRN_EQP_CD
	 *                               24:CAR_NO, CARD_NO
	 *                               28:STL_NO, YD_CAR_SCH_ID
	 *                               29:DATE_FROM,DATE_TO
	 *                               30:YD_CAR_STOP_LOC(YD_STK_COL_GP)
	 *                               33:WLOC_CD,YD_PNT_CD
	 *                               34:TRANS_ORD_DATE, TRANS_ORD_SEQNO
	 *                               35:STL_NO
	 *                               )
	 * @return boolean           true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkPara_getYdCarsch(JDTORecord inRec, int intGp) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;

		try {
			if (intGp == 0 || intGp == 4 || intGp == 5 || intGp == 6 || intGp == 18) {
				
				szFieldName = "V_YD_CAR_SCH_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				
			} else if(intGp == 1) {
				
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				
			} else if(intGp == 2 || intGp == 12) {
				
				szFieldName = "V_YD_GP1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_GP2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_EQP_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 3, 'S', 0, 0);
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
				
			} else if(intGp == 3) {
				szFieldName = "V_YD_CARUD_WRK_BOOK_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_CARLD_WRK_BOOK_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 2, 'S', 0, 0);
			} else if(intGp == 7) {
				
				szFieldName = "V_TRN_EQP_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);
				
			} else if(intGp == 8) {
				
				szFieldName = "V_YD_EQP_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				
			} else if(intGp == 9) {  //TEST 화면 에러체크하지않는다. 
				szFieldName = "V_TRN_EQP_CLASS"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				szFieldName = "V_YD_CAR"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			} else if(intGp == 10) {  //TEST 화면 에러체크하지않는다. 
				szFieldName = "V_CAR_NO"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 15, 3, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_TRN_EQP_CD"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 3, 'S', 0, 0);
			} else if(intGp == 11) {  
				szFieldName = "V_CAR_NO"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 15, 3, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_CARD_NO"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 3, 'S', 0, 0);
			} else if(intGp == 13) {				
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_POINT_GP"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				
				szFieldName = "V_YD_EQP_GP"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			} else if(intGp == 14) {  
				szFieldName = "V_STL_NO"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_CAR_NO"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 15, 2, 'S', 0, 0);
			} else if(intGp == 15) {  
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
			} else if(intGp == 16) {  
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_FRTOMOVE_PLANT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 2, 'S', 0, 0);
			} else if(intGp == 17) {				
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_EQP_GP"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_POINT_GP"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			} else if(intGp == 19) {
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_SEARCH_GBN";
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
			} else if(intGp == 20) {				
				szFieldName = "V_YD_CAR_USE_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				
			} else if(intGp == 21) {
				szFieldName = "V_CAR_NO"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 15, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_TRN_EQP_CD"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 15, 1, 'S', 0, 0);
			} else if(intGp == 22) {
				szFieldName = "V_WLOC_CD"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_BAY"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				
				szFieldName = "V_CAR_POINT"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
			} else if(intGp == 23) {
				szFieldName = "V_YARD"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_BAY"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			
			} else if(intGp == 24) {
				szFieldName = "V_CAR_NO"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 15, 3, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_CARD_NO"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 3, 'S', 0, 0);
			}else if(intGp == 25) {
				szFieldName = "V_WLOC_CD"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_GP"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
			}else if(intGp == 26 || intGp == 31) {
				szFieldName = "V_WLOC_CD"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_CAR_STOP_LOC"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;	
			} else if(intGp == 27) {
				szFieldName = "V_YD_CAR_USE_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_TRN_EQP_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_CAR_NO"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 15, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_CARD_NO"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);
			} else if(intGp == 28){
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;				

				szFieldName = "V_YD_CAR_SCH_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
			}  else if(intGp == 29){
				szFieldName = "V_DATE_FROM";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;				

				szFieldName = "V_DATE_TO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);
			}else if( intGp == 30 ) {
				szFieldName = "V_YD_CAR_STOP_LOC"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;	
			}else if( intGp == 32 ) {				
			}else if(intGp == 33) {				
				szFieldName = "V_WLOC_CD"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_PNT_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);
			}else if(intGp == 34) {	
				
				szFieldName = "V_TRANS_ORD_DATE"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
//				szFieldName = "V_TRANS_ORD_SEQNO";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 1, YdDaoUtils.INTEGER_TYPE, 0, 0);
//				if (!blnErr) return blnErr;
			} else if(intGp == 35) {	
				szFieldName = "V_STL_NO"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
			} else if(intGp == 36) {	
			}
			
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} // end of chkPara_getYdCarsch
	
	
	
/*------------------------------------- INSERT -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드차량스케줄 INSERT
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int insYdCarsch(JDTORecord inRec) throws DAOException, JDTOException {
	    String szMsg              		 = "";
	    String szMethodName       		 = "insYdCarsch";
	    szMsg = "insYdCarsch() In";
		ydUtils.putLog(YdCarSchDao.class.getName(), szMethodName, szMsg, 4);
	    
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		JDTORecord recPara = null;
		JDTORecord recKey = null;
		try {
			if( ydDaoUtils.paraRecChkNull(inRec, "YD_CAR_SCH_ID").equals("") ) {
				recKey = JDTORecordFactory.getInstance().create();
				//고유키 생성
				recKey.setField("JSPEED_QUERY_ID", szQueryIdKey);
				JDTORecordSet rsTemp = dbAssDao.getRecordSet(recKey);
				rsTemp.first();
				recPara = rsTemp.getRecord();
				
				inRec.setField("YD_CAR_SCH_ID", ydDaoUtils.paraRecChkNull(recPara, "YD_CAR_SCH_ID"));
			}
			
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkParameter(recPara);
			
		    szMsg = "insYdCarsch()::chkParameter -> " + blnChk_Field;
			ydUtils.putLog(YdCarSchDao.class.getName(), szMethodName, szMsg, 4);
			
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
		
	    szMsg = "insYdCarsch() Out (" + intRtnVal + ")";
		ydUtils.putLog(YdCarSchDao.class.getName(), szMethodName, szMsg, 4);
		
		return intRtnVal;
	} // end of insYdCarsch
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 출하차차도착량스케줄 INSERT
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int insYdCarsch1(JDTORecord inRec) throws DAOException, JDTOException {
	    String szMsg              		 = "";
	    String szMethodName       		 = "insYdCarsch";
	    szMsg = "insYdCarsch() In";
		ydUtils.putLog(YdCarSchDao.class.getName(), szMethodName, szMsg, 4);
	    
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		JDTORecord recPara = null;
		JDTORecord recKey = null;
		try {
			if( ydDaoUtils.paraRecChkNull(inRec, "YD_CAR_SCH_ID").equals("") ) {
				recKey = JDTORecordFactory.getInstance().create();
				//고유키 생성
				recKey.setField("JSPEED_QUERY_ID", szQueryIdKey);
				JDTORecordSet rsTemp = dbAssDao.getRecordSet(recKey);
				rsTemp.first();
				recPara = rsTemp.getRecord();
				
				inRec.setField("YD_CAR_SCH_ID", ydDaoUtils.paraRecChkNull(recPara, "YD_CAR_SCH_ID"));
			}
			
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkParameter(recPara);
			
		    szMsg = "insYdCarsch()::chkParameter -> " + blnChk_Field;
			ydUtils.putLog(YdCarSchDao.class.getName(), szMethodName, szMsg, 4);
			
			//parameter error return
			if (!blnChk_Field)
				return intRtnVal = -2;
	
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", szQueryIdIns2);
	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		
	    szMsg = "insYdCarsch() Out (" + intRtnVal + ")";
		ydUtils.putLog(YdCarSchDao.class.getName(), szMethodName, szMsg, 4);
		
		return intRtnVal;
	} // end of insYdCarsch
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드차량스케줄 INSERT parameter Check
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return boolean         true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkParameter(JDTORecord inRec) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		
	    String szMsg              		 = "";
	    String szMethodName       		 = "chkParameter";
	    szMsg = "chkParameter() In";
		ydUtils.putLog(YdCarSchDao.class.getName(), szMethodName, szMsg, 4);
	
		
		try {
			szFieldName = "V_YD_CAR_SCH_ID";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_REGISTER";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_REG_DDTT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'T', 0, 0);
			if (!blnErr) return blnErr;
	
//			szFieldName = "V_MODIFIER";
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
//			if (!blnErr) return blnErr;
//	
//			szFieldName = "V_MOD_DDTT";
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'T', 0, 0);
//			if (!blnErr) return blnErr;
//			
//			szFieldName = "V_DEL_YN";
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
//			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_EQP_ID";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CAR_USE_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_CAR_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 15, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_TRN_EQP_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_CAR_KIND";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_TRANS_EQUIPMENT_TYPE";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_EQP_WRK_STAT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_WRK_PROG_STAT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_EQP_WRK_SH";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_EQP_WRK_WT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 7, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_BED_TP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_SPOS_WLOC_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_ARR_WLOC_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CARLD_LEV_LOC";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_CARLD_LEV_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'T', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CARLD_PNT_WO_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'T', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_PNT_CD1";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_PNT_CD2";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CARLD_WRK_BOOK_ID";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CARLD_SCH_REQ_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CARLD_STOP_LOC";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_CARLD_ARR_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'T', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CARLD_ST_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'T', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CARLD_CMPL_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'T', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CARLD_WRK_ACT_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CARLD_CHK_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'T', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_CARUD_LEV_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'T', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CARUD_PNT_WO_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'T', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_PNT_CD3";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_PNT_CD4";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_CARUD_WRK_BOOK_ID";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CARUD_STOP_LOC";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CARUD_SCH_REQ_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CARUD_ARR_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'T', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CARUD_CHK_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'T', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CARUD_ST_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'T', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CARUD_CMPL_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'T', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_CARUD_WRK_ACT_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_TRN_WRK_DELY_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_CARD_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CAR_PROG_STAT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_FRTOMOVE_PLANT_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_PROC_TO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_RENTPROC_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_FRTOMOVE_YD_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_FRTOMOVE_BAY_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_URGENT_FRTOMOVE_WORD_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_DEST_TEL_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 20, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_DLVRDD_RULE_DD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_SHIPASSIGN_WORD_DATE";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_SHIPASSIGN_WORD_SEQNO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_SHIP_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 15, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_SHIP_NAME";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 50, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_RSHP_HOLD_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_BERTH_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_SAILNO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 15, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CAR_WRK_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_TRANS_ORD_DATE";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
//			szFieldName = "V_TRANS_ORD_SEQNO";
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'L', 0, 0);
//			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_BAYIN_WO_SEQ";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;			

			szFieldName = "V_YD_CAR_RCPT_CHK_YN";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CAR_ISSUE_CHK_YN";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CAR_RCPT_CHECKER";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CAR_ISSUE_CHECKER";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
		
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		
	    szMsg = "chkParameter() Out (" + blnErr + ")";
		ydUtils.putLog(YdCarSchDao.class.getName(), szMethodName, szMsg, 4);
		
		return blnErr;
	} //end of chkParameter
		
	
/*------------------------------------- UPDATE -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드차량스케줄 UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0:YD_WBOOK_ID)
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdCarsch(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "updYdCarsch";
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
			
			//수정
			//필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//update data select
			intRtnVal = this.getYdCarsch(inRec, outRecSet, 0);
			
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
	    	String carSchId  	= ydDaoUtils.paraRecChkNull(recOutPara, "YD_CAR_SCH_ID");
	    	String carSchId1  	= ydDaoUtils.paraRecChkNull(recOutPara, "V_YD_CAR_SCH_ID");
	    	szMsg=">>>>>>>>> YdCarSchDao.updYdCarsch -- Car-Sch-ID [ "+carSchId+"], ["+carSchId1+"]";
	    	ydUtils.putLog(YdCarSchDao.class.getName(), szMethodName, szMsg, YdConstant.WARNING);
			blnChk_Field = this.chkParameter(recOutPara);
			
			//parameter error return
			if (!blnChk_Field) return intRtnVal = -2;
			
			//query id setting
			if (intGp == 0)
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
	} // end of updYdCarsch
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드차량스케줄 UPDATE parameter mapping
	 * 
	 * @param JDTORecord inRec
	 *        JDTORecord outRec
	 *        JDTORecord updRec
	 * @return void
	 * @throws JDTOException 
	 */		
	public void dataMapping(JDTORecord inRec, JDTORecord outRec) throws JDTOException {
		String szFieldName = null;
		
		try {
			szFieldName = "V_YD_CAR_SCH_ID";
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
			
			szFieldName = "V_YD_EQP_ID";
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
		
			szFieldName = "V_YD_EQP_WRK_STAT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_WRK_PROG_STAT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_EQP_WRK_SH";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_EQP_WRK_WT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_STK_BED_TP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_SPOS_WLOC_CD";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_ARR_WLOC_CD";
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
			
			szFieldName = "V_CARD_NO";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CAR_PROG_STAT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_FRTOMOVE_PLANT_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_PROC_TO";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_RENTPROC_CD";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_FRTOMOVE_YD_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_FRTOMOVE_BAY_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_URGENT_FRTOMOVE_WORD_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_DEST_TEL_NO";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_DLVRDD_RULE_DD";
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
			
			szFieldName = "V_YD_CAR_WRK_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_TRANS_ORD_DATE";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_TRANS_ORD_SEQNO";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_BAYIN_WO_SEQ";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CAR_RCPT_CHK_YN";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CAR_ISSUE_CHK_YN";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CAR_RCPT_CHECKER";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CAR_ISSUE_CHECKER";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
	} // end of dataMapping
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드차량스케줄 UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0:YD_WBOOK_ID)
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdCarschYdCarWrkBookId(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "updYdCarschYdCarWrkBookId";
		String szMsg = null;
		JDTORecord outRec = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		
		String szFieldName = null;
		boolean blnErr = true;
		
		try {
			//recordSet create
			
			//변환용 레코드
			JDTORecord recInPara = null;
			
			//수정
			//필드명 변환 (필드명 -> V_필드명)
			recInPara = JDTORecordFactory.getInstance().create();
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);


			if (intGp == 0 || intGp == 1) {
				szFieldName = "V_YD_CAR_PROG_STAT";
				blnErr = ydDaoUtils.chkField(recInPara, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return intRtnVal = -2;
				
				szFieldName = "V_YD_EQP_WRK_STAT";
				blnErr = ydDaoUtils.chkField(recInPara, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return intRtnVal = -2;
				
				szFieldName = "V_YD_CARLD_WRK_BOOK_ID";
				blnErr = ydDaoUtils.chkField(recInPara, szFieldName, 18, 1, 'S', 0, 0);
				
				
			}else if(intGp == 2) {
				szFieldName = "V_YD_CAR_PROG_STAT";
				blnErr = ydDaoUtils.chkField(recInPara, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return intRtnVal = -2;
				
				szFieldName = "V_YD_CARUD_WRK_BOOK_ID";
				blnErr = ydDaoUtils.chkField(recInPara, szFieldName, 18, 1, 'S', 0, 0);
				
			}else if(intGp == 3) {
				szFieldName = "V_DEL_YN";
				blnErr = ydDaoUtils.chkField(recInPara, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return intRtnVal = -2;
				
				szFieldName = "V_YD_CAR_PROG_STAT";
				blnErr = ydDaoUtils.chkField(recInPara, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return intRtnVal = -2;
				
				szFieldName = "V_YD_EQP_WRK_STAT";
				blnErr = ydDaoUtils.chkField(recInPara, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return intRtnVal = -2;
				
				szFieldName = "V_YD_CARUD_WRK_BOOK_ID";
				blnErr = ydDaoUtils.chkField(recInPara, szFieldName, 18, 1, 'S', 0, 0);
			} else if(intGp == 4 ) {
				szFieldName = "V_MODIFIER";
				blnErr = ydDaoUtils.chkField(recInPara, szFieldName, 10, 2, 'S', 0, 0);
				if (!blnErr) return intRtnVal = -2;
								
				szFieldName = "V_YD_CARLD_WRK_BOOK_ID";
				blnErr = ydDaoUtils.chkField(recInPara, szFieldName, 18, 1, 'S', 0, 0);
				
			} else if(intGp == 5 ) {
				szFieldName = "V_MODIFIER";
				blnErr = ydDaoUtils.chkField(recInPara, szFieldName, 10, 2, 'S', 0, 0);
				if (!blnErr) return intRtnVal = -2;
								
				szFieldName = "V_YD_CARUD_WRK_BOOK_ID";
				blnErr = ydDaoUtils.chkField(recInPara, szFieldName, 18, 1, 'S', 0, 0);

			} else if(intGp == 6 ) {
				szFieldName = "V_YD_CAR_ISSUE_CHK_YN";
				blnErr = ydDaoUtils.chkField(recInPara, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return intRtnVal = -2;
								
				szFieldName = "V_YD_CAR_ISSUE_CHECKER";
				blnErr = ydDaoUtils.chkField(recInPara, szFieldName, 10, 2, 'S', 0, 0);
				if (!blnErr) return intRtnVal = -2;
				
				szFieldName = "V_YD_CAR_SCH_ID";
				blnErr = ydDaoUtils.chkField(recInPara, szFieldName, 18, 1, 'S', 0, 0);
				
			} else if(intGp == 7 ) {
				szFieldName = "V_YD_CAR_RCPT_CHK_YN";
				blnErr = ydDaoUtils.chkField(recInPara, szFieldName, 1, 2, 'S', 0, 0);   
				if (!blnErr) return intRtnVal = -2;
				
				szFieldName = "V_YD_CAR_RCPT_CHECKER";
				blnErr = ydDaoUtils.chkField(recInPara, szFieldName, 10, 2, 'S', 0, 0);
				if (!blnErr) return intRtnVal = -2;
				
				szFieldName = "V_YD_CAR_SCH_ID";
				blnErr = ydDaoUtils.chkField(recInPara, szFieldName, 18, 1, 'S', 0, 0);
				
			}

			
			//query id setting
			if (intGp == 0)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdDirUpd1);
			else if(intGp == 1)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdDirUpd2);
			else if(intGp == 2)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdDirUpd3);
			else if(intGp == 3)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdDirUpd4);
			else if(intGp == 4)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdDirUpd5);
			else if(intGp == 5)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdDirUpd6);
			else if(intGp == 6)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdDirUpd7);
			else if(intGp == 7)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdDirUpd8);
			else if(intGp == 8)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdDirUpd9);
			else if(intGp == 90)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdDirUpd90);
			else if(intGp == 300)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdDirUpd300);
			else if(intGp == 301)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdDirUpd301);
			else if(intGp == 302)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdDirUpd302);
			else if(intGp == 303)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdDirUpd303);
			else if(intGp == 304)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdDirUpd304);
			else if(intGp == 305)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdDirUpd305);
			//query execute
			intRtnVal = dbAssDao.trtProcess(recInPara);
			
			//execution error return
			if (intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdCarschYdCarWrkBookId
	
	/**
	 * 차량스케줄 삭제 시 사용
	 * @param inRec
	 * @return
	 * @throws JDTOException
	 */
	public String delYdCarsch(JDTORecord inRec) throws JDTOException {
		String szOperationName = "차량스케줄삭제";
		String szLogMsg = null;
		String szMethodName = "delYdCarsch";
		inRec.setField("DEL_YN", "N");
		int intRtnVal = updYdCarsch(inRec, 0);
		if(intRtnVal <= 0) {
			if(intRtnVal == 0) {
				szLogMsg= "[" + szOperationName + "] 차량스케줄이 존재하지 않습니다 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szDaoName, szMethodName, szLogMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NOTEXIST;
			}else if(intRtnVal == -1) {
				szLogMsg= "[" + szOperationName + "] 차량스케줄이 여러 건이 존재합니다 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szDaoName, szMethodName, szLogMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_DUPLICATE;
			}else if(intRtnVal == -2) {
				szLogMsg= "[" + szOperationName + "] 파라미터 오류 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szDaoName, szMethodName, szLogMsg, YdConstant.ERROR);
			}else if(intRtnVal == -3){
				szLogMsg= "[" + szOperationName + "] 실행 오류 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szDaoName, szMethodName, szLogMsg, YdConstant.ERROR);
			}
			return YdConstant.RETN_CD_FAILURE;
		}
		szLogMsg= "[" + szOperationName + "] 차량스케줄 삭제 성공 ";
		ydUtils.putLog(szDaoName, szMethodName, szLogMsg, YdConstant.DEBUG);
		return YdConstant.RETN_CD_SUCCESS;
	}
/*------------------------------------- DELETE -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드차량스케줄 UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0:YD_WBOOK_ID)
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdTransOrdChange(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "updYdTransOrdChange";
		String szMsg = null;
		JDTORecord outRec = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		
		String szFieldName = null;
		boolean blnErr = true;
		
		try {
			//recordSet create
			
			//변환용 레코드
			JDTORecord recInPara = null;
			
			//수정
			//필드명 변환 (필드명 -> V_필드명)
			recInPara = JDTORecordFactory.getInstance().create();
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
 
			//query id setting
			if (intGp == 0)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdDirUpd10);
			else if (intGp == 1)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdDirUpd11);
			else if (intGp == 2)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdDirUpd12);
			else if (intGp == 3)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdDirUpd13);
			else if (intGp == 4)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdDirUpd14);
			//query execute
			intRtnVal = dbAssDao.trtProcess(recInPara);
			
			//execution error return
			if (intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdCarschTransOrd
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드차량스케줄 UPDATE_PIDEV
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0:YD_WBOOK_ID)
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdCarschYdCarWrkBookId_PIDEV(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "updYdCarschYdCarWrkBookId_PIDEV";
		String szMsg = null;
		JDTORecord outRec = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		
		String szFieldName = null;
		boolean blnErr = true;
		
		try {
			//recordSet create
			
			//변환용 레코드
			JDTORecord recInPara = null;
			
			//수정
			//필드명 변환 (필드명 -> V_필드명)
			recInPara = JDTORecordFactory.getInstance().create();
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			//szQueryIdDirUpd305 PI적용
			recInPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.dao.ydcarschdao.ydCarSchDao.updYdCarschYdCarRcptChkYn_PIDEV");
			//query execute
			intRtnVal = dbAssDao.trtProcess(recInPara);
			
			//execution error return
			if (intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdCarschYdCarWrkBookId
		
} // end of class






