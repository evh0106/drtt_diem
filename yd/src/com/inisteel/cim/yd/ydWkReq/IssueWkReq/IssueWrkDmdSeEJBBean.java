package com.inisteel.cim.yd.ydWkReq.IssueWkReq;

import java.util.List;
import java.util.Vector;

import javax.ejb.EJBException;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.dao.YdPlateCommDAO;
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.dao.ydCarSpecDao.YdCarSpecDao;
import com.inisteel.cim.yd.common.dao.ydCrnSpecDao.YdCrnSpecDao;
import com.inisteel.cim.yd.common.dao.ydCrnWrkMtlDao.YdCrnWrkMtlDao;
import com.inisteel.cim.yd.common.dao.ydEqpDao.YdEqpDao;
import com.inisteel.cim.yd.common.dao.ydPrepSchDao.YdPrepSchDao;
import com.inisteel.cim.yd.common.dao.ydSchRuleDao.YdSchRuleDao;
import com.inisteel.cim.yd.common.dao.ydStkBedDao.YdStkBedDao;
import com.inisteel.cim.yd.common.dao.ydStkColDao.YdStkColDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookDao.YdWrkbookDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookMtlDao.YdWrkbookMtlDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.rule.GetBreRule1;
import com.inisteel.cim.yd.common.util.DaoManager;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDBAssist;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdTcConst;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.plate.PlateGdsYdUtil;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdCommonUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdConst;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;


/**
 * 출고작업요구 Session EJB
 *
 * @ejb.bean name="IssueWrkDmdSeEJB" jndi-name="IssueWrkDmdSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required" 
 */
public class IssueWrkDmdSeEJBBean extends BaseSessionBean {
	
	// Session Name
	private String szSessionName=getClass().getName();
	
	private YdUtils ydUtils =new YdUtils();
	
	private YdTcConst ydTcConst =new YdTcConst();
	
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();
	
	private YdDelegate ydDelegate = new YdDelegate();
	
	private EJBConnector ydEjbCon = new EJBConnector("default", this);
	
	// [DEBUG] message flag
	private boolean bDebugFlag=true;
	
	private YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	
	
	
	
	/**
	 * 오퍼레이션명 : A후판 Take-In재료등록 (Y3YDL013)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procY3TakeInCmpl (JDTORecord msgRecord) throws JDTOException  {
		// TC_CODE: Y3YDL013
    	// A후판 TAKE_IN 완료 전문을 수신 받아 저장위치를 Clear 하고 마지막 TAKE_IN 재료이면
    	// 보급로트편성 및 Carry_in 요구를 한다
		
		// DAO 및 UTIL 객체 생성
		YdDelegate ydDelegate         = new YdDelegate();
		YdDaoUtils ydDaoUtils         = new YdDaoUtils();
		YdStockDao ydStockDao     	  = new YdStockDao();
		YdStkLyrDao ydStkLyrDao       = new YdStkLyrDao();
		
		// 레코드 선언
		JDTORecordSet rsGetMslabComm  = null;
		JDTORecordSet rsGetSlabComm   = null;
		JDTORecordSet outRecSet       = null;
		JDTORecord recPara            = null;
		JDTORecord recGetVal          = null;
		JDTORecord recBre          	  = null;
		
		// 변수선언
		String szMethodName   		  = "procY3TakeInCmpl";
		String szMsg          		  = "";
		String szUser          		  = "SYSTEM";
		String szYD_BAY_GP            = "";
		String szYD_EQP_ID            = "";
		String szYD_STK_BED_NO        = "";
		String szYD_STK_BED_WHIO_STAT = "";
		String szTAKE_IN_STL_NO       = "";
		String szYD_STK_BED_STL_SH    = "";
		String szCARRY_IN_REQ_GP      = "N";
		String szRECORD_PROG_STAT     = "";
		String szSTL_NO               = "";
		String szYD_STK_LYR_NO        = "";
		String szTempLyr              = "";
		int intMtlCnt                 = 0;
		int intRtnVal				  = 0;
		int nMslabOrSlab              = 0;     						// 0:MSLABCOMM    1:SLABCOMM
		int nRet                      = 0;
		boolean blnRtnVal             = false;
		boolean bReadSlab             = false;
		boolean bRtnVal               = false;
		
		// TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		if(szRcvTcCode == null){
			// 에러 리턴		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
		
		if(bDebugFlag){
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		try {
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[후판슬라브야드] Take-In재료등록 수신";
			ydUtils.putLogMsg("D", YdConstant.YD_MONITORING_CHANNEL_D, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

			//================================================================================
			// 받은 전문 데이터 유효성 검사
			//================================================================================
			// 설비ID
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if(szYD_EQP_ID.equals("")){
				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			
			szYD_BAY_GP = szYD_EQP_ID.substring(1, 2);
			
			// 적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
			if(szYD_STK_BED_NO.equals("")){
				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			
			// TAKE_IN 재료번호
			szTAKE_IN_STL_NO = ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO");
			if(szTAKE_IN_STL_NO.equals("")){
				szMsg = "[전문 이상] TAKE_IN 재료번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			
			// 적치 재료 매수
			szYD_STK_BED_STL_SH = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_STL_SH");
			if(szYD_STK_BED_STL_SH.equals("")){
				szMsg = "[전문 이상] 적치 재료 매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			
			// 재료 매수
			intMtlCnt = Integer.parseInt(szYD_STK_BED_STL_SH) + 1;
			
			// 적치단
			szYD_STK_LYR_NO = YdUtils.fillSpZr("" + intMtlCnt, 3, 0);
			
			// 야드적치Bed입출고상태
			szYD_STK_BED_WHIO_STAT = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_WHIO_STAT");
			
			//====================================================================================
			// 현재 테스트를 위해 마지막 재료 이면 CARRY_IN 요구 구분을 "Y"로 바꾼다.
			// 추후 기준 설정(기준설정완료)
			//  
			// 수정일 : 2010.02.22
			// 수정자 : 석창화
			// 수정내용 : 보급요구 자동유무를 BRE Rule로 지정하여 처리 (YDB230)
			// 수정일 : 2010.06.30
			// 수정자 : 윤재광
			// 수정내용 : 보급요구 자동유무를 야드테이블에 지정하여 처리 (TB_YM_EQUIP:2DRT02)
			//====================================================================================
			if(intMtlCnt == 1) {
				/*
				recBre = JDTORecordFactory.getInstance().create();
				bRtnVal = GetBreRule2.getYDB230(recBre);
		    	if( bRtnVal ) {
		    		szCARRY_IN_REQ_GP = ydDaoUtils.paraRecChkNull(recBre, "AUTO_SUP_YN");
		    		szMsg="BRE RULE -- A후판슬라브야드 보급요구 자동유무[" + szCARRY_IN_REQ_GP + "]";
		    		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    	} else {
		    		szCARRY_IN_REQ_GP = "N";
		    	}
		    	*/
				String sQuery1			= "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.SelectWBHMISTAT";
				JDTORecord wbJr 		= (new com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO()).getData(sQuery1, new Object[]{ "2DRT02" });
				if (wbJr != null){ 
					szCARRY_IN_REQ_GP	= StringHelper.evl(wbJr.getFieldString("HMI_STAT"), "");
				}
				
		    }
			
			// 2009.10.28  김진욱 수정 : 실적정보의 재료번호와 야드맵상의 재료정보가 틀린경우 스킵...
			outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szYD_EQP_ID);
			recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("YD_STK_LYR_NO", szYD_STK_LYR_NO);
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 0);
			if(intRtnVal <= 0 ) {
				szMsg = "[A후판 Take-In재료등록](Y3YDL013) 적치단위치정보 조회중 실패 YD_STK_COL_GP(" + szYD_EQP_ID + ") YD_STK_BED_NO(" + szYD_STK_BED_NO + ") YD_STK_LYR_NO(" + szYD_STK_LYR_NO + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			} else {
				szMsg = "[A후판 Take-In재료등록](Y3YDL013) 적치단위치정보 조회중 성공 YD_STK_COL_GP(" + szYD_EQP_ID + ") YD_STK_BED_NO(" + szYD_STK_BED_NO + ") YD_STK_LYR_NO(" + szYD_STK_LYR_NO + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				
			}
			
			outRecSet.absolute(1);
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setRecord(outRecSet.getRecord());
			if(!szTAKE_IN_STL_NO.equals(ydDaoUtils.paraRecChkNull(recPara, "STL_NO"))){
				szMsg = "[A후판 SLAB Take-In]Take-In 실적 재료정보[" + szTAKE_IN_STL_NO + "] 와 조회한 적치단 위치의 재료정보[" + ydDaoUtils.paraRecChkNull(recPara, "STL_NO") + "]가 틀립니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			
			//===================================================================
			// 적치단 클리어 업데이트
			//===================================================================
			// 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP"      , szYD_EQP_ID);
			recPara.setField("YD_STK_BED_NO"      , szYD_STK_BED_NO);
			recPara.setField("YD_STK_LYR_NO"      , szYD_STK_LYR_NO);
			recPara.setField("STL_NO"             , "");
			recPara.setField("YD_STK_LYR_MTL_STAT", "E");
			recPara.setField("MODIFIER"           , szUser);
			blnRtnVal = this.setStkLyr(recPara, 0);
			if(!blnRtnVal){
				szMsg = "[A후판 Take-In재료등록](Y3YDL013) 적치단 클리어 업데이트 실패 YD_STK_COL_GP(" + szYD_EQP_ID + ") YD_STK_BED_NO(" + szYD_STK_BED_NO + ") YD_STK_LYR_NO(" + szYD_STK_LYR_NO + ") STL_NO() YD_STK_LYR_MTL_STAT(E)";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);				
				return ;
			}else {
				szMsg = "[A후판 Take-In재료등록](Y3YDL013) 적치단 클리어 업데이트 성공 YD_STK_COL_GP(" + szYD_EQP_ID + ") YD_STK_BED_NO(" + szYD_STK_BED_NO + ") YD_STK_LYR_NO(" + szYD_STK_LYR_NO + ") STL_NO() YD_STK_LYR_MTL_STAT(E)";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);								
			}
			
			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * 			생산통제 A후판장입진행실적 전송  - YDCTJ031
	         * 업무기준 Desc : 1. A후판가열로 장입보급 TAKE-IN완료
	         * 				  2. 보급베드[DAPU01]에서 RT로 TAKE-IN
	         * 기능 추가 : 임춘수
	         * 일자 : 2009.06.18
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			JDTORecord recParaTemp = JDTORecordFactory.getInstance().create();
			// TC CODE
			recParaTemp.setField("JMS_TC_CD"        , "YDCTJ031");
			// 장입보급진행상태
			recParaTemp.setField("CHG_SUP_PROG_STAT", "40");
			// 실적발생일시 [TAKE-IN 완료]
			recParaTemp.setField("WR_OCCR_DT"       , YdUtils.getCurDate("yyyyMMddHHmmss"));
			// 적치 재료 매수
			recParaTemp.setField("YD_STK_BED_STL_SH", "1");
			// 재료번호
			recParaTemp.setField("STL_NO1"          , szTAKE_IN_STL_NO);
			// 전문 송신
			ydDelegate.sendMsg(recParaTemp);
			
			szMsg = "[A후판 Take-In재료등록](Y3YDL013) - 생산통제 A후판장입진행실적(YDCTJ031) 전송 완료";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			 
			//===================================================================
			// 주편 혹은 슬라브공통 테이블을 읽음
			//===================================================================
			szSTL_NO = szTAKE_IN_STL_NO;
			//=========================================================================================================
			// 슬라브공통 조회 (GP : 2)
			//=========================================================================================================
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("SLAB_NO", szSTL_NO);
			rsGetSlabComm = JDTORecordFactory.getInstance().createRecordSet("");
			intRtnVal = ydStockDao.getYdStock(recPara, rsGetSlabComm, 2);
			if(intRtnVal < 0){
				szMsg = "[A후판 Take-In재료등록](Y3YDL013) 슬라브공통 테이블 조회 파라미터 에러 nRet(" + intRtnVal + ") SLAB_NO(" + szSTL_NO + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			} else if(intRtnVal == 0){
				szMsg = "[A후판 Take-In재료등록](Y3YDL013) 슬라브공통 테이블 조회 데이터가 없음 nRet(" + intRtnVal + ") SLAB_NO(" + szSTL_NO + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return ;
			} 
			 
			//===================================================================
			// 주편/슬라브 테이블에 야드구분, 저장위치 업데이트
			//
			// To위치가 DAPU01면 
			//     야드구분 '*', 저장위치 DART02 + Bed(2자리) + 단(2자리) 
			//===================================================================
			if(szYD_EQP_ID.equals("DAPU01")){
				// 야드구분 '*'	
				// 저장위치 DART02 + Bed(2자리) + 단(2자리) Update
				
				szTempLyr = YdUtils.fillSpZr(szYD_STK_LYR_NO, 2, 0);
				
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_GP"  		, "*");										//야드구분(1)		
				recPara.setField("YD_BAY_GP"	, "A");										//야드동구분(1)
				recPara.setField("YD_EQP_GP"	, "RT");									//야드설비구분(2)
				recPara.setField("YD_STK_COL_NO", "02");									//야드적치열번호(2)
				recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);							//야드적치Bed번호(2)
				recPara.setField("YD_STK_LYR_NO", szYD_STK_LYR_NO);							//야드적치단번호(3)
				recPara.setField("YD_STR_LOC"	, "DART02" + szYD_STK_BED_NO + szTempLyr);	//야드저장위치(10)
				recPara.setField("SLAB_NO", szSTL_NO);	
				nRet = ydStockDao.updYdSlabCommYdGp(recPara, 0);
				if(nRet <= 0){
					szMsg = "[A후판 Take-In재료등록](Y3YDL013) 슬라브공통 테이블 야드구분 업데이트 처리 오류 STL_NO(" + szSTL_NO + ") YD_GP(*)";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);						
					return ;
				} else {
					szMsg = "[A후판 Take-In재료등록](Y3YDL013) 슬라브공통 테이블 야드구분 업데이트 처리 성공 STL_NO(" + szSTL_NO + ") YD_GP(*)";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
				}									
			} else if(szYD_EQP_ID.equals("DBPU05")){
				szTempLyr = YdUtils.fillSpZr(szYD_STK_LYR_NO, 2, 0);
				
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_GP"  		, "*");										//야드구분(1)		
				recPara.setField("YD_BAY_GP"	, "B");										//야드동구분(1)
				recPara.setField("YD_EQP_GP"	, "RT");									//야드설비구분(2)
				recPara.setField("YD_STK_COL_NO", "02");									//야드적치열번호(2)
				recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);							//야드적치Bed번호(2)
				recPara.setField("YD_STK_LYR_NO", szYD_STK_LYR_NO);							//야드적치단번호(3)
				recPara.setField("YD_STR_LOC"	, "DBRT02" + szYD_STK_BED_NO + szTempLyr);	//야드저장위치(10)
				recPara.setField("SLAB_NO", szSTL_NO);	
				nRet = ydStockDao.updYdSlabCommYdGp(recPara, 0);
				if(nRet <= 0){
					szMsg = "[2후판 Take-In재료등록](Y3YDL013) 슬라브공통 테이블 야드구분 업데이트 처리 오류 STL_NO(" + szSTL_NO + ") YD_GP(*)";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);						
					return ;
				} else {
					szMsg = "[2후판 Take-In재료등록](Y3YDL013) 슬라브공통 테이블 야드구분 업데이트 처리 성공 STL_NO(" + szSTL_NO + ") YD_GP(*)";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
				}									
			}
						
			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * 			A후판슬라브야드 L2로 저장위치제원 전송  - YDY3L001
	         * 업무기준 Desc : 1. 보급베드[DAPU01]에서 RT로 TAKE-IN인 시 저장위치 제원을 L2로 전송
	         * 					++++++++++++++++후판슬라브야드L2에서 관리하므로 전송 필요없음 ++++++++++++++++++++
	         * 기능 추가 : 임춘수
	         * 일자 : 2009.08.25
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//			recParaTemp = JDTORecordFactory.getInstance().create();
//			recParaTemp.setField("YD_INFO_SYNC_CD", "4");							//1:동,2:SPAN,3:열,4:BED
//			recParaTemp.setField("YD_GP", szYD_EQP_ID.substring(0, 1));
//			recParaTemp.setField("YD_STK_COL_GP", szYD_EQP_ID);
//			recParaTemp.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
//			
//			YdCommonUtils.sndStrPosSpecToL2(recParaTemp);
//			
//			szMsg = "A후판가열로 장입보급 TAKE-IN - A후판슬라브야드 L2로 저장위치제원 전송 완료";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			
			//CARRY_IN 요구 구분이 "Y"이면 CARRY_IN 요구 전문 생성 및 전송
			if(szCARRY_IN_REQ_GP.equals("Y")){
				
				//레코드 생성
				recPara = JDTORecordFactory.getInstance().create();
				//JMS TC CODE
				if ("B".equals(szYD_BAY_GP)) {
					recPara.setField("JMS_TC_CD"             , "YDYDJ497");
				} else {
					recPara.setField("JMS_TC_CD"             , "YDYDJ237");
				}
				//전문 발생 일시
				recPara.setField("JMS_TC_CREATE_DDTT"    , YdUtils.getCurDate("yyyy/MM/dd HH:mm:ss"));
				//설비ID 
				recPara.setField("YD_EQP_ID"             , szYD_EQP_ID);
				//야드적치Bed입출고상태
				recPara.setField("szYD_STK_BED_WHIO_STAT", szYD_STK_BED_WHIO_STAT);
				//적치BED번호
				recPara.setField("YD_STK_BED_NO"         , szYD_STK_BED_NO);
				//목표행선구분(wan ) -어떻게 보낼 것인가
				if ("B".equals(szYD_BAY_GP)) {
					recPara.setField("YD_AIM_RT_GP"          , "C5");				//작업대기(2후판압연)
				} else {
					recPara.setField("YD_AIM_RT_GP"          , YdConstant.AR_WRK_WAIT_A_MILL);				//작업대기(A후판압연)
				}
				//=====================================================================
				
				//전문 송신
				ydDelegate.sendMsg(recPara);
				szMsg = "[A후판 Take-In재료등록](Y3YDL013) 재료등록 후 A후판 가열로 보급 Lot 편성 송신 완료!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
		} catch(Exception e){
			szMsg = "[A후판 Take-In재료등록](Y3YDL013) 예외발생, 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}
		
	} //end of procY3TakeInCmpl
	


	
	
	/**
	 * 오퍼레이션명 : C연주정정L2 Take-In완료 (C3YDL005, C7YDL005)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procC3TakeInCmpl(JDTORecord msgRecord) throws JDTOException  {
		// DAO 및 UTIL 객체생성
		YdDelegate ydDelegate         = new YdDelegate();
		YdDaoUtils ydDaoUtils         = new YdDaoUtils();
		YdStockDao ydStockDao     	  = new YdStockDao();
		YdStkLyrDao ydStkLyrDao       = new YdStkLyrDao();
		
		// 레코드 선언
		JDTORecordSet rsGetMslabComm  = null;
		JDTORecordSet rsGetSlabComm   = null;
		JDTORecordSet outRecSet       = null;
		JDTORecordSet rsResult        = null;
		JDTORecord recPara            = null;
		JDTORecord recParaTemp        = null;
		JDTORecord recGetVal          = null;
		
		// 변수 선언
		String szMethodName           = "procC3TakeInCmpl";
		String szMsg                  = "";
		String szUser                 = "C3YDL005";
		String szYD_EQP_ID            = "";
		String szCARRY_IN_REQ_GP	  = "N";
		String szYD_STK_BED_NO        = "";
		String szYD_STK_BED_WHIO_STAT = "";
		String szTAKE_IN_STL_NO       = "";
		String szYD_STK_BED_STL_SH    = "";
		String szYD_STK_LYR_NO        = "";
		String szRECORD_PROG_STAT     = "";
		String szBuzMsg				  = "";
		String szSTL_NO               = "";
		String szTempLyr              = "";
		String szSTRLOC               = "";
		String szYD_GP                = "";
		String szYD_BAY_GP            = "";
		String szYD_EQP_GP            = "";
		String szYD_STK_COL_NO        = "";
		String szYD_MTL_ITEM          = "";
		String szSTL_APPEAR_GP        = "";
		String szSLAB_WO_RT_CD        = "";
		String szRet                  = "";
		
		int intMtlCnt                 = 0;
		int intRtnVal				  = 0;
		int nMslabOrSlab              = 0;     						// 0:MSLABCOMM    1:SLABCOMM
		int nRet                      = 0;
		boolean blnRtnVal             = false;
		boolean bReadSlab             = false;
		
		// TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		if(szRcvTcCode == null){
			// 에러 리턴
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}

		
		if(bDebugFlag){
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}

		
		try {
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[C연주정정L2] Take-In재료등록 수신";
			ydUtils.putLogMsg("A", YdConstant.YD_MONITORING_CHANNEL_A, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

			//=============================================================
			// 받은 전문 항목 유효성 검사
			//=============================================================
			// 설비ID
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if(szYD_EQP_ID.equals("")){
				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			} else {			
				szMsg = "[1] 설비ID : " + szYD_EQP_ID;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			// Carry-In요구구분 ---> 사용하지 않음, 실제 베드에 적치된 재료 매수가 0인 경우 CARRY-IN요구 Y로 처리하도록 로직 수정
			szCARRY_IN_REQ_GP = ydDaoUtils.paraRecChkNull(msgRecord, "CARRY_IN_REQ_GP");
			if(szCARRY_IN_REQ_GP.equals("")){
				szMsg = "[전문 이상] Carry-In요구구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return ;
			} else {
				szMsg = "[2] Carry-In요구구분 : " + szCARRY_IN_REQ_GP;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				
			}

			// 적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
			if(szYD_STK_BED_NO.equals("")){
				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			} else {
				szMsg = "[3] 적치BED번호 : " + szYD_STK_BED_NO;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			// TAKE_IN 재료번호
			szTAKE_IN_STL_NO = ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO");
			if(szTAKE_IN_STL_NO.equals("")){
				szMsg = "[전문 이상] TAKE_IN 재료번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			} else {
				szMsg = "[4] 재료번호 : " + szTAKE_IN_STL_NO;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			// 적치 재료 매수
			szYD_STK_BED_STL_SH = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_STL_SH");
			if(szYD_STK_BED_STL_SH.equals("")){
				szMsg = "[전문 이상] 적치 재료 매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			} else {
				szMsg = "[5] 적치 재료 매수 : " + szYD_STK_BED_STL_SH;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}

			// 야드적치Bed입출고상태
			szYD_STK_BED_WHIO_STAT = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_WHIO_STAT");
			if(szYD_STK_BED_WHIO_STAT.equals("")){
				szMsg = "[전문 이상] 야드적치Bed입출고상태가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);				
			} else {
				szMsg = "[6] 야드적치Bed입출고상태 : " + szYD_STK_BED_WHIO_STAT;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				
			}
			
			// 재료 매수
			intMtlCnt = Integer.parseInt(szYD_STK_BED_STL_SH) + 1;
			
			// 적치단
			szYD_STK_LYR_NO = YdUtils.fillSpZr(""+intMtlCnt, 3, 0);
			
			//2009 10 28 김진욱 수정 : 실적정보의 재료번호와 야드맵상의 재료정보가 틀린경우 스킵...
			outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szYD_EQP_ID);
			recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("YD_STK_LYR_NO", szYD_STK_LYR_NO);
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 0);
			if(intRtnVal <= 0 ){
				szMsg = "[C연주정정L2 Take-In완료](C3YDL005) 적치단위치정보 조회 중 실패!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			
			outRecSet.absolute(1);
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setRecord(outRecSet.getRecord());
			if(!szTAKE_IN_STL_NO.equals(ydDaoUtils.paraRecChkNull(recPara, "STL_NO"))){
				szMsg = "[C연주정정L2 Take-In완료](C3YDL005) 실적 재료정보[" + szTAKE_IN_STL_NO + "] 와 조회한 적치단 위치의 재료정보[" + ydDaoUtils.paraRecChkNull(recPara, "STL_NO") + "]가 틀립니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			
			//===========================================================
			// 적치단 클리어 업데이트
			//===========================================================
			// 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();			
			recPara.setField("YD_STK_COL_GP"      , szYD_EQP_ID);
			recPara.setField("YD_STK_BED_NO"      , szYD_STK_BED_NO);
			recPara.setField("YD_STK_LYR_NO"      , szYD_STK_LYR_NO);
			recPara.setField("STL_NO"             , "");
			recPara.setField("YD_STK_LYR_MTL_STAT", "E");
			recPara.setField("MODIFIER"           , szUser);
			blnRtnVal = this.setStkLyr(recPara, 0);
			if(!blnRtnVal){
				szMsg = "[C연주정정L2 Take-In완료](C3YDL005) 적치단 클리어 업데이트 실패 YD_STK_COL_GP(" + szYD_EQP_ID + ") YD_STK_BED_NO(" + szYD_STK_BED_NO + ") YD_STK_LYR_NO(" + szYD_STK_LYR_NO + ") STL_NO() YD_STK_LYR_MTL_STAT(E)";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);				
				return ;
			}else {
				szMsg = "[C연주정정L2 Take-In완료](C3YDL005) 적치단 클리어 업데이트 성공 YD_STK_COL_GP(" + szYD_EQP_ID + ") YD_STK_BED_NO(" + szYD_STK_BED_NO + ") YD_STK_LYR_NO(" + szYD_STK_LYR_NO + ") STL_NO() YD_STK_LYR_MTL_STAT(E)";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);								
			}
			
			//===========================================================
			// 불출구에 적치된 재료매수 조회
			//===========================================================
			rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
			recPara 	= JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szYD_EQP_ID);
			recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			intRtnVal 	= ydStkLyrDao.getYdStklyr(recPara, rsResult, 6);
			if(intRtnVal == 0){
				szCARRY_IN_REQ_GP = "Y";
			} else {
				szCARRY_IN_REQ_GP = "N";				
			}
			
			szMsg = "[C연주정정L2 Take-In완료](C3YDL005) 처리 후 해당하는 불출구에 적치된 재료매수 조회 성공 - [" + intRtnVal + "]매";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//====================================================================================
			// 현재 테스트를 위해 마지막 재료 이면 CARRY_IN 요구 구분을 "Y"로 바꾼다.
			// 추후 기준 설정
			// 수정일 : 2010.06.30
			// 수정자 : 윤재광
			// 수정내용 : C열연장입베드 보급요구 자동유무를 야드테이블에 지정하여 처리 (TB_YM_EQUIP:2ART02,2BRT02)
			//====================================================================================
			if(intMtlCnt == 1) {
				if(szYD_EQP_ID.equals(YdConstant.EQP_A_PU4)||			
				   szYD_EQP_ID.equals(YdConstant.EQP_A_PU6)){
					String sQuery1			= "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.SelectWBHMISTAT";
					JDTORecord wbJr 		= (new com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO()).getData(sQuery1, new Object[]{ "2ART02" });
					if (wbJr != null){ 
						szCARRY_IN_REQ_GP	= StringHelper.evl(wbJr.getFieldString("HMI_STAT"), "");
					}
				}
			}
			
			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * 			생산통제 C열연장입진행실적 전송  - YDCTJ033
	         * 업무기준 Desc : 1. C열연가열로 장입보급 TAKE-IN완료
	         * 				  2. 보급베드[AAPUP4,ABPUP6,ACPUP2]에서 RT로 TAKE-IN
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			if(szYD_EQP_ID.equals(YdConstant.EQP_A_PU2)|| 
			   szYD_EQP_ID.equals(YdConstant.EQP_A_PU4)||		
			   szYD_EQP_ID.equals(YdConstant.EQP_A_PU6)){
				recParaTemp = JDTORecordFactory.getInstance().create();
				//TC CODE
				recParaTemp.setField("JMS_TC_CD", "YDCTJ033");
				//장입보급진행상태
				recParaTemp.setField("CHG_SUP_PROG_STAT", "40");
				// 실적발생일시 [TAKE-IN 완료]
				recParaTemp.setField("WR_OCCR_DT", YdUtils.getCurDate("yyyyMMddHHmmss"));
				//적치 재료 매수
				recParaTemp.setField("YD_STK_BED_STL_SH", "1");
				//재료번호
				recParaTemp.setField("STL_NO1", szTAKE_IN_STL_NO);
				//전문 송신
				ydDelegate.sendMsg(recParaTemp);
				
				szMsg = "[C연주Take-In]C열연가열로 장입보급 TAKE-IN완료 - 생산통제 C열연장입진행실적[YDCTJ033] 전송 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			
			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * 			C연주슬라브야드 L2로 저장위치제원 전송  - YDY1L001
	         * 업무기준 Desc : 1. 보급베드에서 RT로 TAKE-IN인 시 저장위치 제원을 L2로 전송
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			recParaTemp = JDTORecordFactory.getInstance().create();
			recParaTemp.setField("YD_INFO_SYNC_CD", "4");							//1:동,2:SPAN,3:열,4:BED
			recParaTemp.setField("YD_GP"          , szYD_EQP_ID.substring(0, 1));
			recParaTemp.setField("YD_STK_COL_GP"  , szYD_EQP_ID);
			recParaTemp.setField("YD_STK_BED_NO"  , szYD_STK_BED_NO);
			
			YdCommonUtils.sndStrPosSpecToL2(recParaTemp);
			
			szMsg = "[C연주Take-In]C열연가열로 장입보급 TAKE-IN - C연주슬라브야드 L2로 저장위치제원 전송 완료";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
//          2009.10.29일 제원정보 내리면 안됨
//
//			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//	         * 			C연주슬라브야드 L2로 저장품제원 전송  - YDY1L002
//	         * 업무기준 Desc : 1. 보급베드에서 RT로 TAKE-IN인 시 저장품 제원을 L2로 전송
//	         * 기능 추가 : 김진욱
//	         * 일자 : 2009.09.21
//	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//	        //20090921 김진욱 저장품제원요구 송신!! YDY1L002 
//			recParaTemp = JDTORecordFactory.getInstance().create();
//			recParaTemp.setField("MSG_ID",          "YDY1L002");
//			recParaTemp.setField("STL_NO",          szTAKE_IN_STL_NO);
//			recParaTemp.setField("YD_STK_COL_GP",   szYD_EQP_ID);
//			recParaTemp.setField("YD_STK_BED_NO",   szYD_STK_BED_NO);
//			recParaTemp.setField("YD_INFO_SYNC_CD", "5");
//			
//			ydDelegate.sendMsg(recParaTemp);
//			
//			szMsg = "[C연주Take-In]C열연가열로 장입보급 TAKE-IN - C연주슬라브야드 L2로 저장품제원요구 전송 완료";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

			//===================================================================
			// 주편 혹은 슬라브공통 테이블을 읽음
			//===================================================================
			szSTL_NO = szTAKE_IN_STL_NO;
			//=========================================================================================================
			// 주편조회 (GP : 6)
			//=========================================================================================================
			rsGetMslabComm = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("MSLAB_NO", szSTL_NO);
			intRtnVal = ydStockDao.getYdStock(recPara, rsGetMslabComm, 6);
			if(intRtnVal < 0){
				szMsg = "[C연주정정L2 Take-In완료](C3YDL005) 주편공통 테이블 조회 파라미터 에러 nRet(" + intRtnVal + ") MSLAB_NO(" + szSTL_NO + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			} else if(intRtnVal == 0){
				szMsg = "[C연주정정L2 Take-In완료](C3YDL005) 주편공통테이블 조회 데이터가 없음 nRet(" + intRtnVal + ") MSLAB_NO(" + szSTL_NO + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				//=========================================================================================================
				// 슬라브공통 조회 (GP : 2)
				//=========================================================================================================
				rsGetSlabComm = JDTORecordFactory.getInstance().createRecordSet("");
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("SLAB_NO", szSTL_NO);
				intRtnVal = ydStockDao.getYdStock(recPara, rsGetSlabComm, 2);
				if(intRtnVal < 0){
					szMsg = "[C연주정정L2 Take-In완료](C3YDL005) 슬라브공통 테이블 조회 파라미터 에러 nRet(" + intRtnVal + ") SLAB_NO(" + szSTL_NO + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				} else if(intRtnVal == 0){
					szMsg = "[C연주정정L2 Take-In완료](C3YDL005) 슬라브공통 테이블 조회 데이터가 없음 nRet(" + intRtnVal + ") SLAB_NO(" + szSTL_NO + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					return ;
				} else {
					nMslabOrSlab = 1;
					
					// 슬라브의 레코드 선택
					rsGetSlabComm.first();
					recGetVal = rsGetSlabComm.getRecord();
					
					szSTL_APPEAR_GP = ydDaoUtils.paraRecChkNull(recGetVal, "STL_APPEAR_GP");
					szSLAB_WO_RT_CD = ydDaoUtils.paraRecChkNull(recGetVal, "SLAB_WO_RT_CD");
					
					szMsg = "[C연주정정L2 Take-In완료](C3YDL005) 슬라브공통 테이블 조회 SUCCESS";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					bReadSlab = true;
				}
			} else {
				nMslabOrSlab = 0;
				
				// 주편의 레코드 선택
				rsGetMslabComm.first();
				recGetVal = rsGetMslabComm.getRecord();

				szMsg = "[C연주정정L2 Take-In완료](C3YDL005) 주편공통 테이블 조회 SUCCESS";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				
			}

			
			if(bReadSlab == false){
				szMsg = "======================= 주편공통(o),슬라브공통(x) 조회 처리 =======================";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				szRECORD_PROG_STAT = ydDaoUtils.paraRecChkNull(recGetVal, "RECORD_PROG_STAT");
				if(szRECORD_PROG_STAT.equals("3")){
					//=========================================================================================================
					// 슬라브공통 조회 (GP : 2)
					//=========================================================================================================
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("SLAB_NO", szSTL_NO);
					rsGetSlabComm = JDTORecordFactory.getInstance().createRecordSet("");
					intRtnVal = ydStockDao.getYdStock(recPara, rsGetSlabComm, 2);
					if(intRtnVal < 0){
						szMsg = "[C연주정정L2 Take-In완료](C3YDL005) 슬라브공통 테이블 조회 파라미터 에러 nRet(" + intRtnVal + ") SLAB_NO(" + szSTL_NO + ")";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return ;
					} else if(intRtnVal == 0){
						szMsg = "[C연주정정L2 Take-In완료](C3YDL005) 슬라브공통 테이블 조회 데이터가 없음 nRet(" + intRtnVal + ") SLAB_NO(" + szSTL_NO + ")";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return ;
					} else {	
						nMslabOrSlab = 1;
						
						// 슬라브의 레코드 선택
						rsGetSlabComm.first();
						recGetVal = rsGetSlabComm.getRecord();
						
						// 슬라브의 레코드에서 항목추출
						szSTL_APPEAR_GP = ydDaoUtils.paraRecChkNull(recGetVal, "STL_APPEAR_GP");
						szSLAB_WO_RT_CD = ydDaoUtils.paraRecChkNull(recGetVal, "SLAB_WO_RT_CD");					

						szMsg = "[C연주정정L2 Take-In완료](C3YDL005) 슬라브공통 테이블 조회 SUCCESS";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
				} else {
					nMslabOrSlab = 0;

					// 주편의 레코드에서 항목추출
					szSTL_APPEAR_GP = ydDaoUtils.paraRecChkNull(recGetVal, "STL_APPEAR_GP");
					szSLAB_WO_RT_CD = ydDaoUtils.paraRecChkNull(recGetVal, "SLAB_WO_RT_CD");					
				}			 
			} else {
				nMslabOrSlab = 1;			
			}			
			
			//===================================================================
			// 주편/슬라브 테이블에 야드구분, 저장위치 업데이트
			//
			// To위치에 따라서 야드구분과 저장위치 값 추출 
			//===================================================================
			if("ACPUP2".equals(szYD_EQP_ID)){
				//C연주 C동 #2 M/C Pickup02
				szSTRLOC        = "ACRT02";
				szYD_GP         = "*";
			} else if("AAPUP4".equals(szYD_EQP_ID)){
				//C연주 A동 #2 M/C Pickup04
				szSTRLOC        = "AART04";
				szYD_GP         = "*";
			} else if("ABPUP6".equals(szYD_EQP_ID)){
				//C연주 B동 #2 M/C Pickup06
				szSTRLOC        = "ABRT03";
				szYD_GP         = "*";
			} else if("ACDP01".equals(szYD_EQP_ID)){
				//C연주 C동 #1 Scarfer Depiler01
				szSTRLOC        = "ACDP01";
				szYD_GP         = "A";
			} else if("ABDP03".equals(szYD_EQP_ID)){
				//C연주 A동 #2 Scarfer Depiler03
				szSTRLOC        = "ABDP03";
				szYD_GP         = "A";
			} else if("AADP02".equals(szYD_EQP_ID)){
				//C연주 A동 #1 2차절단 Depiler02
				szSTRLOC        = "DART01";
				szYD_GP         = "D";
			} else if("AAPUP9".equals(szYD_EQP_ID)){
				//C연주 A동 #2 2차절단 Pickup09
				szSTRLOC        = "DBRT02";
				szYD_GP         = "D";
			} else if("AAPUPA".equals(szYD_EQP_ID)){
				//C연주 A동 #3 2차절단 Pickup10
				szSTRLOC        = "DART03";
				szYD_GP         = "D";
			} else {
				szMsg = "[C연주정정L2 Take-In완료](C3YDL005) To위치에 따라서 야드구분과 저장위치 값 추출에 매칭되는 항목이 없음";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);										
			}

			//아래는 사용하지 않는데 쓸데 없이 있는 것으로 주석처리
			//ABPUP5 Take-In 실적 발생 시 오류
//			if(!"".equals(szYD_EQP_ID)) {
//				szYD_BAY_GP     = szSTRLOC.substring(1, 2);
//				szYD_EQP_GP     = szSTRLOC.substring(2, 4);
//				szYD_STK_COL_NO = szSTRLOC.substring(4, 6);
//			}

			//============================================================================
			// 해당위치 업데이트가 아닌 쉬프트 시켜야 됨
			// 임춘수차장님 공통 모듈에 함수 호출
			//============================================================================
			if(!szSTRLOC.equals("")){
				// 레코드 생성
				recPara = JDTORecordFactory.getInstance().create();

				if(nMslabOrSlab == 0){		
					// 주편
					recPara.setField("MSLAB_NO", szSTL_NO);

					if(szSTL_APPEAR_GP.equals("B")&& szSLAB_WO_RT_CD.startsWith("H")){
						szYD_MTL_ITEM  	= "BH";
					}else if(szSTL_APPEAR_GP.equals("B")&& szSLAB_WO_RT_CD.startsWith("P")){
						szYD_MTL_ITEM  	= "BP";
					}				
				} else if(nMslabOrSlab == 1){
					// 슬라브
					recPara.setField("SLAB_NO", szSTL_NO);

					if(szSTL_APPEAR_GP.equals("C")&& szSLAB_WO_RT_CD.startsWith("H")){
						szYD_MTL_ITEM  	= "SH";
					}else if(szSTL_APPEAR_GP.equals("C")&& szSLAB_WO_RT_CD.startsWith("P")){
						szYD_MTL_ITEM  	= "SP";
					}
				}
				
				recPara.setField("YD_MTL_ITEM"  , szYD_MTL_ITEM);					
				recPara.setField("YD_STK_COL_GP", szSTRLOC);
				recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
				recPara.setField("YD_STK_LYR_NO", szYD_STK_LYR_NO);
				szRet = YdCommonUtils.setYdStrLocToPtComm(recPara, szMethodName);
				if(szRet.equals(YdConstant.RETN_CD_SUCCESS)){
					szMsg = "[C연주정정L2 Take-In완료](C3YDL005) SHIFT업데이트 처리 성공[" + szRet + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);										
					
				} else {
					szMsg = "[C연주정정L2 Take-In완료](C3YDL005) SHIFT업데이트 처리 실패[" + szRet + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);										
				}
			} else {
				szMsg = "[C연주정정L2 Take-In완료](C3YDL005) 매칭되는 값이 없으므로 공통 테이블  업데이트 처리는 하지 않는다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);										
			}
			
			// CARRY_IN 요구 구분이 "Y"이면 CARRY_IN 요구 전문 생성 및 전송
			if(szCARRY_IN_REQ_GP.equals("Y")){
				
			    //=============================================================================
				// 매수가 0으로 왔다는 것은 지금 단에 쌓여 있는 재료가 아무것도 없다는 것이므로 
				// 전문을 어떤 이유에서 못받아서 적치단에 공중부양이 되어 있는 재료가 있을 수 있으므로
				// 매수 0일 시에는 해당 BED에 적치된 재료들을 클리어 해줌
				//=============================================================================
				recPara = JDTORecordFactory.getInstance().create();			
				recPara.setField("YD_STK_COL_GP", szYD_EQP_ID);
				recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
				recPara.setField("MODIFIER"     , szUser);
				nRet 	= ydStkLyrDao.updYdStklyrClearStkColGpStkBedNo(recPara);
				if(nRet <= 0){
					szMsg = "[C연주정정L2 Take-In완료](C3YDL005) 적치단테이블 해당 BED위치의 모든 적치재료 클리어 업데이트 실패 YD_STK_COL_GP(" + szYD_EQP_ID + ") YD_STK_BED_NO(" + szYD_STK_BED_NO + ")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);				
				}else {
					szMsg = "[C연주정정L2 Take-In완료](C3YDL005) 적치단테이블 해당 BED위치의 모든 적치재료 클리어 업데이트 성공 YD_STK_COL_GP(" + szYD_EQP_ID + ") YD_STK_BED_NO(" + szYD_STK_BED_NO + ") STL_NO() YD_STK_LYR_MTL_STAT(E)";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);								
				}
				
				// 레코드 생성
				recPara = JDTORecordFactory.getInstance().create();
				// JMS TC CODE					
				if(szYD_EQP_ID.equals(YdConstant.EQP_A_PU2)|| 
				   szYD_EQP_ID.equals(YdConstant.EQP_A_PU4)|| 
				   szYD_EQP_ID.equals(YdConstant.EQP_A_PU6)){	//AAPUP4, ABPUP6, ACPUP2
					// 열연가열로 장입
					recPara.setField("JMS_TC_CD"   , "YDYDJ231");
					recPara.setField("YD_AIM_RT_GP", YdConstant.AR_WRK_WAIT_C_MILL);		 //작업대기(C열연압연)
					szBuzMsg 	= "C연주 C열연가열로";
					szMsg 		= "[C연주Take-In]  " + szBuzMsg + " TAKE-IN완료 - C열연가열로";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				} else if("ACDP01".equals(szYD_EQP_ID)){
					//C연주 #1 Scarfer
					recPara.setField("JMS_TC_CD"   , "YDYDJ232");
					recPara.setField("YD_AIM_RT_GP", YdConstant.AR_CORRCETION_WRK_WAIT_A_BP_SF);//정정작업대기(A후판주편스카핑)
					szBuzMsg 	= "C연주 1,2후판 스카핑";
					szMsg 		= "[C연주Take-In] " + szBuzMsg + " TAKE-IN완료 - 1,2후판 스카핑";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				} else if("ABDP03".equals(szYD_EQP_ID)){
					//C연주 #2 Scarfer
					recPara.setField("JMS_TC_CD"   , "YDYDJ232");
					recPara.setField("YD_AIM_RT_GP", YdConstant.AR_CORRCETION_WRK_WAIT_C_CCR_SF);//정정작업대기(C열연CCR스카핑)
					szBuzMsg 	= "C연주 C열연 스카핑";
					szMsg 		= "[C연주Take-In] " + szBuzMsg + " TAKE-IN완료 - C열연 스카핑";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				} else if("AADP02".equals(szYD_EQP_ID) || "AAPUP9".equals(szYD_EQP_ID) || "AAPUPA".equals(szYD_EQP_ID)){
					//C연주 #1,2,3 2차절단 
					recPara.setField("JMS_TC_CD"   , "YDYDJ233");
					recPara.setField("YD_AIM_RT_GP", YdConstant.AR_CORRCETION_WRK_WAIT_A_BP); //정정작업대기(A후판주편정정)
					szBuzMsg 	= "C연주 1,2후판 2차절단";
					szMsg 		= "[C연주Take-In] " + szBuzMsg + " TAKE-IN완료 - 1,2후판 2차절단";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
				
				// 전문 발생 일시
				recPara.setField("JMS_TC_CREATE_DDTT"    , YdUtils.getCurDate("yyyy/MM/dd HH:mm:ss"));
				// 설비ID 
				recPara.setField("YD_EQP_ID"             , szYD_EQP_ID);
				// 야드적치Bed입출고상태
				recPara.setField("szYD_STK_BED_WHIO_STAT", szYD_STK_BED_WHIO_STAT);
				// 적치BED번호
				recPara.setField("YD_STK_BED_NO"         , szYD_STK_BED_NO);
				// 전문 송신
				ydDelegate.sendMsg(recPara);
				szMsg = "[C연주정정L2 Take-In완료](C3YDL005) 재료등록 후 " + szBuzMsg + " 보급 Lot 편성 송신 완료!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
		} catch(Exception e){
			szMsg = "[C연주정정L2 Take-In완료](C3YDL005) 예외발생, 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}
		
	} //end of procC3TakeInCmpl
	
	/**
	 * 오퍼레이션명 : 재료번호, 적치단재료상태에 대한 적치단 정보 유무체크 및 조회결과 데이터 반환
	 *  
	 * @param  String        szStlNo   재료번호
	 *         String        szMtlStat 적치단재료상태
	 *         JDTORecordSet rsResult  결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public int chkGetStlStkLyr(String szStlNo, String szMtlStat, JDTORecordSet rsResult)throws JDTOException  {
		
		//적치단 DAO
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();

		String szMsg        = null;
		String szMethodName = "chkGetStlStkLyr";
		int intRtnVal       = 0;
		boolean blnRtnVal   = false;
		JDTORecord recPara  = null;
		
		try {
			
			//조회 항목  record 생성
			recPara = JDTORecordFactory.getInstance().create();

			//조회 파라미터 레코드 set
			recPara.setField("STL_NO",              szStlNo);
			recPara.setField("YD_STK_LYR_MTL_STAT", szMtlStat);
			
			//적치단정보 조회
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsResult, 3);

			//리턴값 메세지처리
			if(intRtnVal > 1){
				
				szMsg = "재료번호("      + szStlNo   + ")," +
				        "적치단재료상태(" + szMtlStat + ")," +
				        " 에 대한 적치단 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				//blnRtnVal = false;
				return intRtnVal;
			} else if(intRtnVal == 1){
				
				//blnRtnVal = true;
				return intRtnVal;
			} else if(intRtnVal == 0){
				szMsg = "재료번호("      + szStlNo   + ")," +
		                "적치단재료상태(" + szMtlStat + ")," +
						" 에 대한 적치단 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				//blnRtnVal = false;
				return intRtnVal;
			} else if(intRtnVal == -2){
				szMsg = "재료번호("      + szStlNo   + ")," +
                        "적치단재료상태(" + szMtlStat + ")," +
				        "로 적치단 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				//blnRtnVal = false;
				return intRtnVal;
			} else {
				szMsg = "재료번호("      + szStlNo   + ")," +
                        "적치단재료상태(" + szMtlStat + ")," +
				        " 로 적치단 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				//blnRtnVal = false;
				return intRtnVal;
			}
		} catch(Exception e){
			szMsg = "재료번호, 적치단재료상태에 대한 적치단 정보 유무체크 및 조회결과 데이터 반환 중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			//blnRtnVal = false;
			intRtnVal = -100; 
		}
		return intRtnVal;
	} //end of chkGetStlStkLyr
	
	
	/**
	 * 오퍼레이션명 : 설비상태 체크
	 *  
	 * @param   String szEqpId 설비ID
	 * @return boolean true(설비사용가능), false(설비사용불가)
	 * @throws JDTOException
	 */
	public boolean eqpStatCheck(String szEqpId)throws JDTOException  {

		//리턴값(boolean)
		boolean blnRtnVal      = false;
		//메세지
		String szMsg           = null;
		//메소드명
		String szMethodName    = "eqpStatCheck";		
		//설비상태
		String szYD_EQP_STAT   = null;
		//레코드 선언
		JDTORecord recPara     = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;		
		
		try {
			//레코드 생성
			recPara = JDTORecordFactory.getInstance().create();
			//레코드셋 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//설비ID를 작업크레인으로 설정
			recPara.setField("YD_EQP_ID", szEqpId);
			
			//설비 체크 및 데이터 조회
			blnRtnVal = this.chkGetEqp(szEqpId, rsResult);
			if(!blnRtnVal) return blnRtnVal;
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//설비상태
			szYD_EQP_STAT = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_STAT");
			
			//크레인의 상태가 'T'이면 false 리턴.
			//상수 수정 [2009.12.03 이현성]
			if(szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_BREAK)){
				
				szMsg = "설비ID(" + szEqpId + ")의 상태가 고장(" + szYD_EQP_STAT + ") 입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
	
				blnRtnVal = true;
	
			}
		} catch(Exception e){
			szMsg = "설비상태 체크 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
		
	} //end of eqpStatCheck
	
	
	
	/**
	 * 오퍼레이션명 : 스케줄기준 체크 및 데이터 반환
	 *  
	 * @param  String     szSchCd 스케줄CD
	 * @return boolean    true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetSchRule(String szSchCd, JDTORecordSet rsResult)throws JDTOException  {
		
		//스케줄기준 DAO
		YdSchRuleDao ydSchRuleDao = new YdSchRuleDao();

		String szMsg              = null;
		String szMethodName       = "chkGetSchRule";
		int intRtnVal             = 0;
		boolean blnRtnVal         = false;
		JDTORecord recPara        = null;
		
		try {
			
			//조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			//스케줄코드
			recPara.setField("YD_SCH_CD", szSchCd);

			//스케줄코드로 스케줄기준 Table 조회
			intRtnVal = ydSchRuleDao.getYdSchrule(recPara, rsResult, 0);

			//리턴값 메세지처리
			if(intRtnVal > 1){
				
				szMsg = "스케줄코드(" + szSchCd + ")에 대한 스케줄기준 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == 1){

				blnRtnVal = true;
				
			} else if(intRtnVal == 0){
				
				szMsg = "스케줄코드(" + szSchCd + ")에 대한 스케줄기준 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == -2){
				
				szMsg = "스케줄코드(" + szSchCd + ")로 스케줄기준 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "스케줄코드(" + szSchCd + ")로 스케줄기준 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e){
			szMsg = "스케줄기준 체크 중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return blnRtnVal = false;
		}
		return blnRtnVal = true;
		
	} //end of chkGetSchRule
	
	
	/**
	 * 오퍼레이션명 : 적치단 업데이트
	 *  
	 * @param  JDTORecord recPara 업데이트용 레코드
	 *         int        intGp   업데이트 쿼리 구분자
	 *         String        szStkLyrNo 적치단번호
	 *         JDTORecordSet rsResult   결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean setStkLyr(JDTORecord recPara, int intGp)throws JDTOException  {
		
		//적치단 DAO 
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();

		String szMsg        = null;
		String szMethodName = "setStkLyr";
		int intRtnVal       = 0;
		boolean blnRtnVal   = false;
		
		try {
			
			//적치단정보 업데이트
			intRtnVal = ydStkLyrDao.updYdStklyr(recPara, intGp);

			//리턴값 메세지처리
			if(intRtnVal >= 1){
				
				blnRtnVal = true;
				
			} else if(intRtnVal == 0){
				szMsg = "적치열구분("  + recPara.getFieldString("YD_STK_COL_GP") + ")," +
				        "적치BED번호(" + recPara.getFieldString("YD_STK_BED_NO") + ")," +
				        "적치단번호("  + recPara.getFieldString("YD_STK_LYR_NO") + ")" +
						" 에 대한 적치단 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == -1){
				szMsg = "적치열구분("  + recPara.getFieldString("YD_STK_COL_GP") + ")," +
				        "적치BED번호(" + recPara.getFieldString("YD_STK_BED_NO") + ")," +
				        "적치단번호("  + recPara.getFieldString("YD_STK_LYR_NO") + ")" +
				        "로 적치단 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == -2){
				szMsg = "적치열구분("  + recPara.getFieldString("YD_STK_COL_GP") + ")," +
				        "적치BED번호(" + recPara.getFieldString("YD_STK_BED_NO") + ")," +
				        "적치단번호("  + recPara.getFieldString("YD_STK_LYR_NO") + ")" +
				        "로 적치단 업데이트중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				szMsg = "적치열구분("  + recPara.getFieldString("YD_STK_COL_GP") + ")," +
				        "적치BED번호(" + recPara.getFieldString("YD_STK_BED_NO") + ")," +
				        "적치단번호("  + recPara.getFieldString("YD_STK_LYR_NO") + ")" +
				        " 로 적치단 업데이트중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e){
			szMsg = "적치단 업데이트 중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of setStkLyr

	/**
	 * 오퍼레이션명 : C연주C열연보급Lot편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procCCsCHrSupLotComp (JDTORecord msgRecord) throws JDTOException  {
		
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils 	= new YdDaoUtils();
		YdStkBedDao ydStkBedDao = new YdStkBedDao();
		
		//레코드 선언
		JDTORecord recPara     = null;
		JDTORecord recOutPara  = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		
		//리턴값(boolean)
		boolean blnRtnVal      = false;
		//메세지
		String szMsg           = "";
		//메소드명
		String szMethodName    = "procCCsCHrSupLotComp";
		
		//Carry_In 재료 기준 매수
		int intCarryInCnt         		= 5;
		
		//전문 생성 일시
		String szDate             		= null;
		//설비ID
		String szYD_EQP_ID             	= null;
		//목표행선구분
		String szYD_AIM_RT_GP          	= null;
		//적치BED번호
		String szYD_STK_BED_NO         	= null;
		//스케줄코드
		String szYD_SCH_CD             	= null;
		//재료매수
		int intRealSh					= 0;
		int intRtnVal					= 0;
		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if(szRcvTcCode == null){
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		//TC CODE DISPLAY
		if(bDebugFlag){
		
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		try {
			//설비ID
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if(szYD_EQP_ID.equals("")){
				
				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}
			//목표행선구분
			szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_RT_GP");
			if(szYD_AIM_RT_GP.equals("")){
				
				szMsg = "[전문 이상] 목표행선구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}
			//적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
			if(szYD_STK_BED_NO.equals("")){
				
				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			//레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			
			//적치Bed조회한다. (베드 조회시 적치베드의 상태를 고려하고 조회를 한다.)
			recOutPara = JDTORecordFactory.getInstance().create();
			recOutPara.setField("YD_STK_COL_GP", szYD_EQP_ID);
			recOutPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
        	
    		intRtnVal = ydStkBedDao.getYdStkbed(recOutPara, rsResult, 0) ;

    		if(intRtnVal <= 0) {
    			szMsg = "<procCCsCHrSupLotComp> 적치Bed 조회가 되지 않았습니다.";
    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			return;
    		}
    		
    		rsResult.absolute(1);
    		recOutPara = rsResult.getRecord();
    		
    		if (!recOutPara.getFieldString("YD_STK_BED_USG_GP").equals("B") ) {
    			szMsg="<procCCsCHrSupLotComp> 적치베드 입출고 구분이 불출구가 아닙니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			return;
    		}
			
			//=================================================================================
			//AAPUP4,ABPUP6,ACPUP2설비가 보급베드또는 인출베드로 사용가능하므로 스케줄코드를 따로 생성
			szYD_SCH_CD  = YdCommonUtils.getSchCd(szYD_EQP_ID, szYD_STK_BED_NO, "U");
			//=================================================================================
			szMsg = "[C연주 C열연 보급 Lot 편성]스케줄코드 : " + szYD_SCH_CD;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//보급 Lot 편성 대상 재료 Select
			blnRtnVal = this.chkGetCHRSupplyLotGp(szYD_SCH_CD, "HC", rsResult);
			if(!blnRtnVal) return ;
			
			//A동 장입베드는 재료기준매수(4매)로 셋팅
			if("AAPUP4".equals(szYD_EQP_ID)) intCarryInCnt = 4;
			
			//지시 재료 매수가 Carry In 재료기준매수(현재5매)보다 작으면 지시 재료 매수를 재료기준매수에 대입한다.
			if(intCarryInCnt > rsResult.size()) intCarryInCnt = rsResult.size();
			
			//레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			//JMS TC CODE
			recOutPara.setField("JMS_TC_CD","YDYDJ241");
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			String sSpanGbn 	= szYD_SCH_CD.substring(7);
			String sPreSpanGbn 	= "";
			String sCurSpanGbn 	= "";
			//Carry In 재료기준매수(현재5매)만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= intCarryInCnt; Loop_i++){
				
				sCurSpanGbn = ydDaoUtils.paraRecChkNull(recPara, "SPAN_SEQ");
				
				if( Loop_i == 1 ){
					sPreSpanGbn = sCurSpanGbn;
				}else{
					if( !sCurSpanGbn.equals(sPreSpanGbn) ){
						szMsg = "[C연주 C열연 보급 Lot 편성]대상재의 대상스판 우선순위  비교 ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						break;
					}
				}
				
				//재료번호
				recOutPara.setField("STL_NO" + Loop_i, ydDaoUtils.paraRecChkNull(recPara, "STL_NO"));
				recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, "" + Loop_i);
				
				//다음 레코드 추출
				rsResult.next();
				recPara = rsResult.getRecord();
				//재료매수
				intRealSh++;
			}
			
			if("2".equals(sPreSpanGbn)){
				/*
				 * A동 장입베드일 경우만 제외(설비 제약조건)
				 */ 
				if("AAPUP4".equals(szYD_EQP_ID)){
					if("01".equals(szYD_STK_BED_NO)||
				       "02".equals(szYD_STK_BED_NO)||
				       "06".equals(szYD_STK_BED_NO)||
				       "07".equals(szYD_STK_BED_NO)){
						
						szMsg = "[C연주 C열연 보급 Lot 편성]우선순위 2번째 > 대상재의 대상베드 물리적으로 적치불가함.["+szYD_STK_BED_NO+"]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						return;
					}
				}
				if("L".equals(sSpanGbn)){
					szYD_SCH_CD = szYD_SCH_CD.substring(0, 7)+ "R";
				}else if("R".equals(sSpanGbn)){
					szYD_SCH_CD = szYD_SCH_CD.substring(0, 7)+ "L";
				}
				szMsg = "[C연주 C열연 보급 Lot 편성] 스케쥴코드 변경[ "+szYD_SCH_CD+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			//전문 발생 일시
			szDate = YdUtils.getCurDate("yyyy/MM/dd HH:mm:ss");
			//발생 일시
			recOutPara.setField("JMS_TC_CREATE_DDTT", szDate);
			//스케줄코드
			recOutPara.setField("YD_SCH_CD",          szYD_SCH_CD);
			//CARRY_IN 재료매수
			recOutPara.setField("YD_CARRY_IN_SH",     "" + intRealSh);
			//적치열구분
			recOutPara.setField("YD_STK_COL_GP",      szYD_EQP_ID);
			//적치BED번호
			recOutPara.setField("YD_STK_BED_NO",      szYD_STK_BED_NO);
			
			//전문 송신
			ydDelegate.sendMsg(recOutPara);
			//this.procCCsCHrSupCarryInWrkReq(recOutPara);
			
			szMsg = "C연주 C열연 보급 Lot 편성 후 CARRY_IN 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch(Exception e){
			szMsg = "C연주 C열연 보급 Lot 편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}
		
	} //end of procCCsCHrSupLotComp
	

	/**
	 * 오퍼레이션명 : C연주C열연보급Carry-In작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procCCsCHrSupCarryInWrkReq(JDTORecord msgRecord)throws JDTOException  {
		
		String szMsg           = "";
		String szMethodName    = "procCCsCHrSupCarryInWrkReq";
		
		try{
			JDTORecord recRtn = JDTORecordFactory.getInstance().create();
			
			EJBConnector ydEjbCon = new EJBConnector("default", this);
			recRtn = (JDTORecord)ydEjbCon.trx("IssueWrkDmdSeEJB", "procCCsCHrSupCarryInWrkReq_Rnew", msgRecord);
			
			String sEqpId = ydDaoUtils.paraRecChkNull(recRtn, "YD_EQP_ID");
			
			if(!"".equals(sEqpId)){
				// 스케쥴 모듈 호출
				JDTORecord recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("JMS_TC_CD", 	"YDYDJ500");
				recPara.setField("YD_SCH_CD", 	ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD"));
				recPara.setField("YD_EQP_ID", 	ydDaoUtils.paraRecChkNull(recRtn, "YD_EQP_ID"));
				
				ydDelegate.sendMsg(recPara);
			}
		} catch (Exception e) {
			szMsg="["+szMethodName+"] Exception발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMsg);
		}	// end try catch문
	}	
	/**
	 * 오퍼레이션명 : C연주C열연보급Carry-In작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord procCCsCHrSupCarryInWrkReq_Rnew(JDTORecord msgRecord)throws JDTOException  {
		
		//스케줄기준 DAO
		YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();
		//작업예약 재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils           = new YdDaoUtils();
		//공용 METHOD
		YdUtils ydutils                 = new YdUtils();
		
		//리턴값(boolean)
		boolean blnRtnVal      = false;
		//리턴값(int)
		int intRtnVal          = 0;
		//메세지
		String szMsg           = "";
		//METHOD명
		String szMethodName    = "procCCsCHrSupCarryInWrkReq";
		//사용자
		String szUser          = "SYSTEM";
		
		//레코드 선언
		JDTORecord recPara     = null;
		JDTORecord recStkPara  = null;
		JDTORecord recRtn      = JDTORecordFactory.getInstance().create();
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		
		//설비ID(열구분)
		String szYD_STK_COL_GP     = null;
		//적치BED번호
		String szYD_STK_BED_NO     = null;
		//재료매수(int)
		int intMtlCnt              = 0;
		//재료번호
		String[] szSTL_NO          = new String[6];
		//권상모음순서
		String[] szYD_UP_COLL_SEQ  = new String[6];
		//스케줄코드
		String szYD_SCH_CD         = null;
		//스케줄우선순위
		String szYD_SCH_PRIOR		= "";
		//스케줄 금지 유무
		String szYD_SCH_PROH_EXN   = null;
		//작업크레인
		String szYD_WRK_CRN        = null;
		// 작업크레인우선순위
		String szYD_WRK_CRN_PRIOR = null;
		//대체크레인유무
		String szYD_ALT_CRN_YN     = null;
		//대체크레인
		String szYD_ALT_CRN        = null;
		// 대체크레인우선순위
		String szYD_ALT_CRN_PRIOR = null;
		//선택크레인
		String szCrn               = null;
		//작업예약ID
		String szYD_WBOOK_ID       = null;
		// 크레인 작업가능 매수
		int  intWrkAblCnt          = 0;
		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if(szRcvTcCode == null){
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return recRtn;
		}
		//TC CODE DISPLAY
		if(bDebugFlag){
		
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		try {
		
			//받은 전문 편집
			//스케줄코드
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			if(szYD_SCH_CD.equals("")){
				
				szMsg = "[전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return recRtn;
				
			}
			//적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
			if(szYD_STK_COL_GP.equals("")){
				
				szMsg = "[전문 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return recRtn;
				
			}
			//적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
			if(szYD_STK_BED_NO.equals("")){
				
				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return recRtn;
				
			}
			//재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_CARRY_IN_SH");
			if(intMtlCnt == 0){
				
				szMsg = "[전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return recRtn;
				
			}

			//재료번호, 권상모음순서, 적치단(테스트용)
			for(int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
				
				//재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO" + Loop_i);
				if(szSTL_NO[Loop_i].equals("")){
					
					szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return recRtn;
					
				}
				//권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_COLL_SEQ" + Loop_i);
				if(szYD_UP_COLL_SEQ[Loop_i].equals("")){
					
					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i] + ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return recRtn;	
					
				}
			}

			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if(!blnRtnVal) return recRtn;
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//스케줄CD 체크
			//스케줄 금지 유무
			szYD_SCH_PROH_EXN 	= ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");
			//작업크레인
			szYD_WRK_CRN      	= ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
			//작업크레인우선순위
			szYD_WRK_CRN_PRIOR  = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN_PRIOR");
			//대체크레인유무
			szYD_ALT_CRN_YN   	= ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
			//대체크레인
			szYD_ALT_CRN      	= ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
			//대체크레인우선순위
			szYD_ALT_CRN_PRIOR  = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_PRIOR");
			
			//스케줄 금지 유무가  "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if(szYD_SCH_PROH_EXN.equals("Y")){
				
				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return recRtn;
			}
			
			//작업크레인 설비 상태 체크
			blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);
			
			//작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if(!blnRtnVal){
				
				szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
				//대체크레인의 유무를 체크한다.
				//대체크레인이 없으면 에러 리턴
				if(!szYD_ALT_CRN_YN.equals("Y")){
					
					szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return recRtn;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if(!blnRtnVal){
					
					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return recRtn;
					
				} else {
					//대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					szCrn = szYD_ALT_CRN;
					szYD_SCH_PRIOR = szYD_ALT_CRN_PRIOR;
				}
			} else {
				//작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				szCrn = szYD_WRK_CRN;
				szYD_SCH_PRIOR = szYD_WRK_CRN_PRIOR;
			}

			/////////////////////////////////////////////////////////////////////////////
			////////////////////보급 대상재를 가지고 여러가지 체크를한다..중요사항/////////////////
			/////////////////////////////////////////////////////////////////////////////
			/*
			 * 2010.07.05 YJK 단순체크사항 - 삭제해도 괜찮을거 같음.
			 */
			/*
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
				
				//크레인사양과 저장품 사양을 체크(길이,폭,중량)
				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
				if(!blnRtnVal) return ;
				
				//다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if(!blnRtnVal) return ;
				
			}	
			*/
			/*
			 * 2010.07.05 YJK 폭/중량기준으로 대상재를 체크한다.
			 */
			intWrkAblCnt = this.chkWrkMtl_W(szSTL_NO, szCrn, intMtlCnt);
			if (intWrkAblCnt == 0) {
				szMsg = "작업예약 재료 폭체크 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return recRtn;
			}
			/////////////////////////////////////////////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////////
			
			//리턴 recordSet 생성
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			
			//작업예약ID 생성
			blnRtnVal = this.getYdWbookId(rsResult);
			if(!blnRtnVal) return recRtn;
			//레코드추출
			rsResult.first();
			recPara = rsResult.getRecord();
			//작업예약ID
			szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");
			
			//INSERT 항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			//야드구분
			String szYD_GP       = szYD_SCH_CD.substring(0, 1);
			//동구분
			String szYD_BAY_GP   = szYD_SCH_CD.substring(1,2);
			
			//INSERT할 항목 SET
			recPara.setField("YD_WBOOK_ID", 	szYD_WBOOK_ID);
			recPara.setField("YD_GP", 			szYD_GP);
			recPara.setField("YD_BAY_GP", 		szYD_BAY_GP);
			recPara.setField("YD_SCH_CD", 		szYD_SCH_CD);
			recPara.setField("YD_SCH_PRIOR", 	szYD_SCH_PRIOR);
			recPara.setField("YD_AIM_YD_GP", 	szYD_GP);
			recPara.setField("YD_AIM_BAY_GP", 	szYD_BAY_GP);
			recPara.setField("YD_TO_LOC_DCSN_MTD", "F");							//야드To위치결정방법
			recPara.setField("YD_TO_LOC_GUIDE", szYD_STK_COL_GP + szYD_STK_BED_NO);	//야드To위치Guide
			recPara.setField("REGISTER", 		szUser);
	
			//작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			
			//조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
			recPara.setField("REGISTER", 	  szUser);

			for (int Loop_i = 1; Loop_i <= intWrkAblCnt; Loop_i++){
				
				//리턴 recordSet 생성
				rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
				//재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				intRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C", rsResult);
				//if( intRtnVal != 1 ) return ;
				
				if(intRtnVal != 1) {
					if( intRtnVal == 0 ) {
						intRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "U", rsResult);			//보조작업으로 권상대기 인 경우
						if(intRtnVal != 1) {
							szMsg="적치단에 재료["+szSTL_NO[Loop_i]+"]가 존재하지 않습니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							throw new JDTOException(szMsg);
						}
					}else{
						szMsg="적치단에 재료["+szSTL_NO[Loop_i]+"] 조회 시 오류발생";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						throw new JDTOException(szMsg);
					}
				}
				
				//레코드추출
				rsResult.first();
				recStkPara = rsResult.getRecord();
				
				//재료번호
				recPara.setField("STL_NO", 		   szSTL_NO[Loop_i]);
				//적치열구분
				recPara.setField("YD_STK_COL_GP",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_COL_GP"));
				//적치BED번호
				recPara.setField("YD_STK_BED_NO",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_BED_NO"));
				//적치단번호
				recPara.setField("YD_STK_LYR_NO",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_LYR_NO"));
				//권상모음순서
				recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ[Loop_i]);
				
				// 작업예약재료 테이블에 등록한다.
				intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
			}
			
			recRtn.setField("YD_EQP_ID", 	szCrn);
			
			return recRtn;
		} catch(Exception e){
			szMsg = "C연주C열연보급Carry-In작업요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMsg);
		}
	} // end of procCCsCHrSupCarryInWrkReq()
	
	
	/**
	 * 오퍼레이션명 : C연주M-Scarfing보급Lot편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procCCsMScarfingSupLotComp (JDTORecord msgRecord) throws JDTOException  {
		
		YdDelegate ydDelegate = new YdDelegate();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		//공용 METHOD
		YdUtils ydutils       = new YdUtils();
		
		//레코드 선언
		JDTORecord recPara     = null;
		JDTORecord recOutPara  = null;
		JDTORecord       jdtoRcd        = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		
		//리턴값(boolean)
		boolean blnRtnVal      = false;
		boolean bRtnVal			= false;
		//리턴값(int)
		int intRtnVal          = 0;
		//메세지
		String szMsg           = "";
		//메소드명
		String szMethodName    = "procCCsMScarfingSupLotComp";
		String szOperationName	= "C연주M-Scarfing보급Lot편성";
		//사용자
		String szUser          = "SYSTEM";
		
		//Carry_In 재료 기준 매수
		int intCarryInCnt         = 4;
		//전문 생성 일시
		String szDate             = null;

		//설비ID
		String szYD_EQP_ID             = null;
		//목표행선구분
		String szYD_AIM_RT_GP          = null;
		//적치BED번호
		String szYD_STK_BED_NO         = null;
		//야드적치Bed입출고상태
		String szYD_STK_BED_WHIO_STAT  = null;
		//스케줄코드
		String szYD_SCH_CD             = null;
		//재료품목
		String szYD_MTL_ITEM           = null;
		//재료품목
		String szNext_YD_MTL_ITEM = null;
		//목표행선구분
		String szNext_YD_AIM_RT_GP          = null;
		
		String szAUTO_ORD_YN			= null;
		
		String szYD_PREP_SCH_ID			= null;
		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if(szRcvTcCode == null || szRcvTcCode.equals("") ){
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		
		}
		//TC CODE DISPLAY
		if(bDebugFlag){
		
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			szMsg = "["+szOperationName+"] 메소드 시작 - 전문확인";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			ydUtils.displayRecord(szOperationName, msgRecord);
			
			//받은 전문 편집
			//스케줄코드
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if(szYD_EQP_ID.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}
			//목표행선구분
			szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_RT_GP");
			if(szYD_AIM_RT_GP.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 목표행선구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}
			//적치Bed입출고상태
			szYD_STK_BED_WHIO_STAT = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_WHIO_STAT");
//			if(szYD_STK_BED_WHIO_STAT.equals("")){
//				
//				szMsg = "[전문 이상] 야드적치Bed입출고상태가 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return ;
//				
//			}
			//적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
			if(szYD_STK_BED_NO.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}
			//재료품목
			szYD_MTL_ITEM = ydDaoUtils.paraRecChkNull(msgRecord, "YD_MTL_ITEM");
			
			jdtoRcd = JDTORecordFactory.getInstance().create();
			bRtnVal = GetBreRule1.getYDB196(jdtoRcd);
	    	if( bRtnVal ) {
	    		szAUTO_ORD_YN = ydDaoUtils.paraRecChkNull(jdtoRcd, "AUTO_ORD_YN");
	    		szMsg = "["+szOperationName+"] 보급LOT편성 시 자동여부를 BRE Rule 조회 성공 - 자동지시여부["+szAUTO_ORD_YN+"] ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	}else{
	    		szAUTO_ORD_YN = "Y";
	    		szMsg = "["+szOperationName+"] 보급LOT편성 시 자동여부를 BRE Rule 조회 시 오류발생 - 자동지시여부 기본값["+szAUTO_ORD_YN+"] 사용";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    	}
	    	
	    	if( szAUTO_ORD_YN.equals("N") ) {
	    		szMsg = "["+szOperationName+"] 보급LOT편성 시 자동여부["+szAUTO_ORD_YN+"]가 수동[N]이므로 업무종료처리";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return ;
	    	}
			
			
			/*
			 * 재료품목이 없으면 BP, BH순으로 조회
			 * 재료품목이 존재 시 BP -> BH, BH -> BP순으로 조회
			 */
//			if(szYD_MTL_ITEM.equals("") ){
//				szYD_MTL_ITEM = "BP";
//				msgRecord.setField("YD_MTL_ITEM", szYD_MTL_ITEM);
//			}
//			
//			if( szYD_MTL_ITEM.equals("BP") ){
//				szNext_YD_MTL_ITEM = "BH";
//				szNext_YD_AIM_RT_GP = YdConstant.AR_CORRCETION_WRK_WAIT_C_CCR_SF;					//정정작업대기(C열연CCR스카핑)
//			}else{
//				szNext_YD_MTL_ITEM = "BP";
//				szNext_YD_AIM_RT_GP = YdConstant.AR_CORRCETION_WRK_WAIT_A_BP_SF;					//정정작업대기(A후판주편스카핑)
//			}
			//=================================================================================
			//수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			//BRE 등록 안됨...테스트용 스케줄코드 생성
			//추후 구현..
			//szYD_SCH_CD  = "ACPU04UM";
			szYD_SCH_CD = szYD_EQP_ID.substring(0, 4) + "0" + szYD_EQP_ID.substring(5, 6) + "UM";
			//=================================================================================
			//msgRecord.setField("YD_SCH_CD", szYD_SCH_CD);
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			//보급 Lot 편성 대상 재료 Select
			//blnRtnVal = chkGetMScarfingSupplyLotGp(szYD_SCH_CD, rsResult);
//			intRtnVal = chkMScarfingSupplyLotGp(msgRecord, rsResult);
//			//if(!blnRtnVal) return ;
//			if( intRtnVal == 0 ){
//				msgRecord.setField("YD_MTL_ITEM", szNext_YD_MTL_ITEM);
//				msgRecord.setField("YD_AIM_RT_GP", szNext_YD_AIM_RT_GP);
//				intRtnVal = chkMScarfingSupplyLotGp(msgRecord, rsResult);
//				if( intRtnVal <= 0 ) return ;
//			}else if( intRtnVal < 0 ){
//				return ;
//			}
			
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_GP",          		szYD_EQP_ID.substring(0, 1));
			recPara.setField("YD_SCH_CD",          	szYD_SCH_CD);
			recPara.setField("YD_PREP_WK_ST",       "S");
			recPara.setField("QUERY_TYPE",       	"COL_DESC");
			String szRtnMsg = YdCommonUtils.getYdStockFromEarliestPrepSch(recPara, rsResult);
			
			if( rsResult.size() == 0 ) {
				szMsg = "["+szOperationName+"] 준비스케줄에 보급편성된 LOT가 존재하지 않습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return ;
			}
			rsResult.first();
			recPara = rsResult.getRecord();
			szYD_PREP_SCH_ID = ydDaoUtils.paraRecChkNull(recPara, "YD_PREP_SCH_ID");
			
			szMsg = "["+szOperationName+"] 준비스케줄에 보급편성된 LOT["+szYD_PREP_SCH_ID+"]가 존재합니다.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//지시 재료 매수가 Carry In 재료기준매수(현재4매)보다 작으면 지시 재료 매수를 재료기준매수에 대입한다.
			if(intCarryInCnt > rsResult.size()) intCarryInCnt = rsResult.size();
			
			//레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			//JMS TC CODE
			recOutPara.setField("JMS_TC_CD",          "YDYDJ242");
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//Carry In 재료기준매수(현재4매)만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= intCarryInCnt; Loop_i++){
				
				//재료번호
				recOutPara.setField("STL_NO" + Loop_i, ydDaoUtils.paraRecChkNull(recPara, "STL_NO"));
				//권상모음순서
				recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, "" + Loop_i);

				//다음 레코드 추출
				rsResult.next();
				recPara = rsResult.getRecord();
				
			}
			
			//전문 발생 일시
			szDate = YdUtils.getCurDate("yyyy/MM/dd HH:mm:ss");

			//발생 일시
			recOutPara.setField("JMS_TC_CREATE_DDTT", szDate);
			//스케줄코드
			recOutPara.setField("YD_SCH_CD",          szYD_SCH_CD);
			//CARRY_IN 재료매수
			recOutPara.setField("YD_CARRY_IN_SH",     "" + intCarryInCnt);
			//적치열구분
			recOutPara.setField("YD_STK_COL_GP",      szYD_EQP_ID);
			//적치BED번호
			recOutPara.setField("YD_STK_BED_NO",      szYD_STK_BED_NO);
			//준비스케줄ID - 임춘수 2009.11.12
			recOutPara.setField("YD_PREP_SCH_ID",      szYD_PREP_SCH_ID);
			
			//전문 송신
			ydDelegate.sendMsg(recOutPara);
			szMsg = "["+szOperationName+"] CARRY_IN작업요구 송신 완료! - 메소드 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch(Exception e){
			szMsg = "["+szOperationName+"] Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
	} //end of procCCsMScarfingSupLotComp
	
	
	
	/**
	 * 오퍼레이션명 : C연주정정보급Lot편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procCCsShearSupLotComp (JDTORecord msgRecord) throws JDTOException  {
		
		YdDelegate ydDelegate = new YdDelegate();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		//공용 METHOD
		YdUtils ydutils       = new YdUtils();
		
		//레코드 선언
		JDTORecord recPara     = null;
		JDTORecord recOutPara  = null;
		JDTORecord       jdtoRcd        = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		
		//리턴값(boolean)
		boolean blnRtnVal      = false;
		boolean bRtnVal			= false;
		//리턴값(int)
		int intRtnVal          = 0;
		//메세지
		String szMsg           = "";
		//메소드명
		String szMethodName    = "procCCsShearSupLotComp";
		String szOperationName	= "C연주정정보급Lot편성";
		//사용자
		String szUser          = "SYSTEM";
		
		//Carry_In 재료 기준 매수
		int intCarryInCnt         = 4;
		//전문 생성 일시
		String szDate             = null;

		//설비ID
		String szYD_EQP_ID             = null;
		//목표행선구분
		String szYD_AIM_RT_GP          = null;
		//적치BED번호
		String szYD_STK_BED_NO         = null;
		//야드적치Bed입출고상태
		String szYD_STK_BED_WHIO_STAT  = null;
		//스케줄코드
		String szYD_SCH_CD             = null;
		//자동지시유무
		String szAUTO_ORD_YN			= null;
		//준비스케줄ID
		String szYD_PREP_SCH_ID			= null;
		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if(szRcvTcCode == null || szRcvTcCode.equals("")){
		
			szMsg = "["+szOperationName+"]  TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		
		}
		//TC CODE DISPLAY
		if(bDebugFlag){
		
			szMsg="["+szOperationName+"] 전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			szMsg = "["+szOperationName+"] 메소드 시작 - 전문확인";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			ydUtils.displayRecord(szOperationName, msgRecord);
			
			//받은 전문 편집
			//스케줄코드
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if(szYD_EQP_ID.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}
//			//목표행선구분
//			szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_RT_GP");
//			if(szYD_AIM_RT_GP.equals("")){
//				
//				szMsg = "["+szOperationName+"] [전문 이상] 목표행선구분이 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return ;
//				
//			}
			//적치Bed입출고상태
			szYD_STK_BED_WHIO_STAT = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_WHIO_STAT");
//			if(szYD_STK_BED_WHIO_STAT.equals("")){
//				
//				szMsg = "[전문 이상] 야드적치Bed입출고상태가 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return ;
//				
//			}
			//적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
			if(szYD_STK_BED_NO.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}
			
			jdtoRcd = JDTORecordFactory.getInstance().create();
			bRtnVal = GetBreRule1.getYDB195(jdtoRcd);
	    	if( bRtnVal ) {
	    		szAUTO_ORD_YN = ydDaoUtils.paraRecChkNull(jdtoRcd, "AUTO_ORD_YN");
	    		szMsg = "["+szOperationName+"] 보급LOT편성 시 자동여부를 BRE Rule 조회 성공 - 자동지시여부["+szAUTO_ORD_YN+"] ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	}else{
	    		szAUTO_ORD_YN = "Y";
	    		szMsg = "["+szOperationName+"] 보급LOT편성 시 자동여부를 BRE Rule 조회 시 오류발생 - 자동지시여부 기본값["+szAUTO_ORD_YN+"] 사용";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    	}
	    	
	    	if( szAUTO_ORD_YN.equals("N") ) {
	    		szMsg = "["+szOperationName+"] 보급LOT편성 시 자동여부["+szAUTO_ORD_YN+"]가 수동[N]이므로 업무종료처리";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return ;
	    	}
			
			
			//=================================================================================
			//수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			//BRE 등록 안됨...테스트용 스케줄코드 생성
			//추후 구현..
//			szYD_SCH_CD  = "AAPU02UM";
	    	if ("AAPUPA".equals(szYD_EQP_ID)) {
	    		szYD_SCH_CD = "AAPU10UM";
	    	} else {
				szYD_SCH_CD = szYD_EQP_ID.substring(0, 4) + "0" + szYD_EQP_ID.substring(5, 6) + "UM";
	    	}
			//=================================================================================
			
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			//보급 Lot 편성 대상 재료 Select
//			blnRtnVal = chkGetSHEARSupplyLotGp(szYD_SCH_CD, rsResult);
//			if(!blnRtnVal) return ;
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_GP",          		szYD_EQP_ID.substring(0, 1));
			recPara.setField("YD_SCH_CD",          	szYD_SCH_CD);
			recPara.setField("YD_PREP_WK_ST",       "S");
			recPara.setField("QUERY_TYPE",       	"COL_DESC");
			String szRtnMsg = YdCommonUtils.getYdStockFromEarliestPrepSch(recPara, rsResult);
			
			if( rsResult.size() == 0 ) {
				szMsg = "["+szOperationName+"] 준비스케줄에 보급편성된 LOT가 존재하지 않습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return ;
			}
			
			rsResult.first();
			recPara = rsResult.getRecord();
			szYD_PREP_SCH_ID = ydDaoUtils.paraRecChkNull(recPara, "YD_PREP_SCH_ID");
			
			szMsg = "["+szOperationName+"] 준비스케줄에 보급편성된 LOT["+szYD_PREP_SCH_ID+"]가 존재합니다.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//지시 재료 매수가 Carry In 재료기준매수(현재4매)보다 작으면 지시 재료 매수를 재료기준매수에 대입한다.
			if(intCarryInCnt > rsResult.size()) intCarryInCnt = rsResult.size();
			
			//레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			//JMS TC CODE
			recOutPara.setField("JMS_TC_CD",          "YDYDJ243");
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//Carry In 재료기준매수(현재4매)만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= intCarryInCnt; Loop_i++){
				
				//재료번호
				recOutPara.setField("STL_NO" + Loop_i, ydDaoUtils.paraRecChkNull(recPara, "STL_NO"));
				//권상모음순서
				recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, "" + Loop_i);

				//다음 레코드 추출
				rsResult.next();
				recPara = rsResult.getRecord();
				
			}
			
			//전문 발생 일시
			szDate = YdUtils.getCurDate("yyyy/MM/dd HH:mm:ss");

			//발생 일시
			recOutPara.setField("JMS_TC_CREATE_DDTT", szDate);
			//스케줄코드
			recOutPara.setField("YD_SCH_CD",          szYD_SCH_CD);
			//CARRY_IN 재료매수
			recOutPara.setField("YD_CARRY_IN_SH",     "" + intCarryInCnt);
			//적치열구분
			recOutPara.setField("YD_STK_COL_GP",      szYD_EQP_ID);
			//적치BED번호
			recOutPara.setField("YD_STK_BED_NO",      szYD_STK_BED_NO);
			//준비스케줄ID - 임춘수 2009.11.12
			recOutPara.setField("YD_PREP_SCH_ID",      szYD_PREP_SCH_ID);
			
			//전문 송신
			ydDelegate.sendMsg(recOutPara);
			
			szMsg = "["+szOperationName+"] CARRY_IN 작업요구 송신 완료! - 메소드끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch(Exception e){
			szMsg = "["+szOperationName+"] Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
	} //end of procCCsShearSupLotComp
	
	/**
	 * 오퍼레이션명 : C연주M-Scarfing보급Carry-In작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procCCsScarfingSupCarryInWrkReq(JDTORecord msgRecord)throws JDTOException  {
		
		//스케줄기준 DAO
		YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();
		//작업예약 재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils           = new YdDaoUtils();
		//공용 METHOD
		YdUtils ydutils                 = new YdUtils();
		
		//리턴값(boolean)
		boolean blnRtnVal      = false;
		//리턴값(int)
		int intRtnVal          = 0;
		//메세지
		String szMsg           = "";
		//METHOD명
		String szMethodName    = "procCCsScarfingSupCarryInWrkReq";
		String szOperationName	= "C연주M-Scarfing보급Carry-In작업요구";
		//사용자
		String szUser          = "SYSTEM";
		
		//레코드 선언
		JDTORecord recPara     = null;
		JDTORecord recStkPara  = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		
		//설비ID(열구분)
		String szYD_STK_COL_GP     = null;
		//적치BED번호
		String szYD_STK_BED_NO     = null;
		//재료매수(int)
		int intMtlCnt              = 0;
		//재료번호
		String[] szSTL_NO          = new String[6];
		//권상모음순서
		String[] szYD_UP_COLL_SEQ  = new String[6];
		//적치단
		String[] szYD_STK_LYR_NO   = new String[6];
		//스케줄코드
		String szYD_SCH_CD         = null;
		//스케줄우선순위
		String szYD_SCH_PRIOR			= "";
		//스케줄 금지 유무
		String szYD_SCH_PROH_EXN   = null;
		//작업크레인
		String szYD_WRK_CRN        = null;
		// 작업크레인우선순위
		String szYD_WRK_CRN_PRIOR = null;
		//대체크레인유무
		String szYD_ALT_CRN_YN     = null;
		//대체크레인
		String szYD_ALT_CRN        = null;
		// 대체크레인우선순위
		String szYD_ALT_CRN_PRIOR = null;
		//선택크레인
		String szCrn               = null;
		//작업예약ID
		String szYD_WBOOK_ID       = null;
		//준비스케줄ID
		String szYD_PREP_SCH_ID		= null;
		
		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if(szRcvTcCode == null || szRcvTcCode.equals("")){
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		
		}
		//TC CODE DISPLAY
		if(bDebugFlag){
		
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			szMsg = "["+szOperationName+"] 메소드 시작 - 전문확인";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			ydUtils.displayRecord(szOperationName, msgRecord);
			
		
			//받은 전문 편집
			//스케줄코드
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			if(szYD_SCH_CD.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}
			//적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
			if(szYD_STK_COL_GP.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}
			//적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
			if(szYD_STK_BED_NO.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}
			//재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_CARRY_IN_SH");
			if(intMtlCnt == 0){
				
				szMsg = "["+szOperationName+"] [전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}

			//재료번호, 권상모음순서, 적치단(테스트용)
			for(int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
				
				//재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO" + Loop_i);
				if(szSTL_NO[Loop_i].equals("")){
					
					szMsg = "["+szOperationName+"] [전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
					
				}
				//권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_COLL_SEQ" + Loop_i);
				if(szYD_UP_COLL_SEQ[Loop_i].equals("")){
					
					szMsg = "["+szOperationName+"] [전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i] + ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;	
					
				}
			}
			//준비스케줄ID
			szYD_PREP_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_PREP_SCH_ID");

			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if(!blnRtnVal) return ;
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//스케줄CD 체크
			//스케줄 금지 유무
			szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");
			//작업크레인
			szYD_WRK_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
			//작업크레인우선순위
			szYD_WRK_CRN_PRIOR     = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN_PRIOR");
			//대체크레인유무
			szYD_ALT_CRN_YN   = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
			//대체크레인
			szYD_ALT_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
			//대체크레인우선순위
			szYD_ALT_CRN_PRIOR     = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_PRIOR");
			
			
			//스케줄 금지 유무가  "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if(szYD_SCH_PROH_EXN.equals("Y")){
				
				szMsg = "["+szOperationName+"] 스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				//return ;
			}
			
			//작업크레인 설비 상태 체크
			blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);
			
			//작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if(!blnRtnVal){
				
				szMsg = "["+szOperationName+"] 작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
				//대체크레인의 유무를 체크한다.
				//대체크레인이 없으면 에러 리턴
				if(!szYD_ALT_CRN_YN.equals("Y")){
					
					szMsg = "["+szOperationName+"] 대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if(!blnRtnVal){
					
					szMsg = "["+szOperationName+"] 대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
					
				} else {
					//대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					szCrn = szYD_ALT_CRN;
					szYD_SCH_PRIOR = szYD_ALT_CRN_PRIOR;
				}
			} else {
				//작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				szCrn = szYD_WRK_CRN;
				szYD_SCH_PRIOR = szYD_WRK_CRN_PRIOR;
			}

			//재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다.
			//작업예약재료 등록 여부를 체크한다.
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
				
				//크레인사양과 저장품 사양을 체크(길이,폭,중량)
				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
				if(!blnRtnVal) return ;
				
				//다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if(!blnRtnVal) return ;
				
			}	
			
			//리턴 recordSet 생성
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			
			//작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if(!blnRtnVal) return ;
			//레코드추출
			rsResult.first();
			recPara = rsResult.getRecord();
			//작업예약ID
			szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");
			
			//INSERT 항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			//야드구분
			String szYD_GP       = szYD_SCH_CD.substring(0, 1);
			//동구분
			String szYD_BAY_GP   = szYD_SCH_CD.substring(1,2);
			
			//INSERT할 항목 SET
			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			recPara.setField("YD_GP", 		szYD_GP);
			recPara.setField("YD_BAY_GP", 	szYD_BAY_GP);
			recPara.setField("YD_SCH_CD", 	szYD_SCH_CD);
			recPara.setField("YD_SCH_PRIOR", 	szYD_SCH_PRIOR);
			//야드To위치결정방법
			recPara.setField("YD_TO_LOC_DCSN_MTD", "F");
			//야드To위치Guide
			recPara.setField("YD_TO_LOC_GUIDE", szYD_STK_COL_GP + szYD_STK_BED_NO);
			recPara.setField("YD_AIM_YD_GP", 	szYD_GP);
			recPara.setField("YD_AIM_BAY_GP", 	szYD_BAY_GP);
			recPara.setField("REGISTER", 	szUser);
	
			//작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if(intRtnVal < 1){
				szMsg = "["+szOperationName+"] 작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			
			
			//조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
			//recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			//recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", 	  szUser);

			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
				
				//리턴 recordSet 생성
				rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
				//재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				intRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C", rsResult);
				if(intRtnVal != 1) {
					if( intRtnVal == 0 ) {
						intRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "U", rsResult);
						if(intRtnVal != 1) {
							szMsg="["+szOperationName+"] 적치단에 재료["+szSTL_NO[Loop_i]+"]가 존재하지 않습니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							throw new DAOException(szMsg);
						}
					}else{
						szMsg="["+szOperationName+"] 적치단에 재료["+szSTL_NO[Loop_i]+"] 조회 시 오류발생";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						throw new DAOException(szMsg);
					}
				}
				
				//레코드추출
				rsResult.first();
				recStkPara = rsResult.getRecord();
				
				//재료번호
				recPara.setField("STL_NO", 		   szSTL_NO[Loop_i]);
				//적치열구분
				recPara.setField("YD_STK_COL_GP",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_COL_GP"));
				//적치BED번호
				recPara.setField("YD_STK_BED_NO",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_BED_NO"));
				//적치단번호
				recPara.setField("YD_STK_LYR_NO",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_LYR_NO"));
				//권상모음순서
				recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ[Loop_i]);
				
				// 작업예약재료 테이블에 등록한다.
				intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
				
				if(intRtnVal < 1){
					szMsg = "["+szOperationName+"] 작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					throw new DAOException(szMsg);
					//return ;
				}
			}
			
			if( !szYD_PREP_SCH_ID.equals("") ) {
				try {
					String szRtnMsg = YdCommonUtils.deletePreSch(szYD_PREP_SCH_ID, szYD_WBOOK_ID, szMethodName);
				}catch(JDTOException ex) {
					throw new DAOException(ex);
				}
			}
			
			
			//스케줄코드, 설비id - 크레인스케쥴메인 호출
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("JMS_TC_CD", 	"YDYDJ500");
			recPara.setField("YD_SCH_CD", 	szYD_SCH_CD);
			recPara.setField("YD_EQP_ID", 	szCrn);
			
			ydDelegate.sendMsg(recPara);
			
			szMsg = "["+szOperationName+"] 크레인스케줄 호출 성공 - 메소드 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		} catch(Exception e){
			szMsg = "["+szOperationName+"] 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}

	
	} // end of procCCsScarfingSupCarryInWrkReq()
	
	
	/**
	 * 오퍼레이션명 : C연주정정보급Carry-In작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procCCSShearSupCarryInWrkReq(JDTORecord msgRecord)throws JDTOException  {
		
		//스케줄기준 DAO
		YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();
		//작업예약 재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils           = new YdDaoUtils();
		//공용 METHOD
		YdUtils ydutils                 = new YdUtils();
		
		//리턴값(boolean)
		boolean blnRtnVal      = false;
		//리턴값(int)
		int intRtnVal          = 0;
		//메세지
		String szMsg           = "";
		//METHOD명
		String szMethodName    = "procCCSShearSupCarryInWrkReq";
		String szOperationName	= "C연주정정보급Carry-In작업요구";
		//사용자
		String szUser          = "SYSTEM";
		
		//레코드 선언
		JDTORecord recPara     = null;
		JDTORecord recStkPara  = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		
		//설비ID(열구분)
		String szYD_STK_COL_GP     = null;
		//적치BED번호
		String szYD_STK_BED_NO     = null;
		//재료매수(int)
		int intMtlCnt              = 0;
		//재료번호
		String[] szSTL_NO          = new String[6];
		//권상모음순서
		String[] szYD_UP_COLL_SEQ  = new String[6];
		//적치단
		String[] szYD_STK_LYR_NO   = new String[6];
		//스케줄코드
		String szYD_SCH_CD         = null;
		//스케줄우선순위
		String szYD_SCH_PRIOR			= "";
		//스케줄 금지 유무
		String szYD_SCH_PROH_EXN   = null;
		//작업크레인
		String szYD_WRK_CRN        = null;
		// 작업크레인우선순위
		String szYD_WRK_CRN_PRIOR = null;
		//대체크레인유무
		String szYD_ALT_CRN_YN     = null;
		//대체크레인
		String szYD_ALT_CRN        = null;
		// 대체크레인우선순위
		String szYD_ALT_CRN_PRIOR = null;
		//선택크레인
		String szCrn               = null;
		//작업예약ID
		String szYD_WBOOK_ID       = null;
		//준비스케줄ID
		String szYD_PREP_SCH_ID		= null;
		
		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if(szRcvTcCode == null || szRcvTcCode.equals("")){
		
			szMsg = "["+szOperationName+"]  TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		
		}
		//TC CODE DISPLAY
		if(bDebugFlag){
		
			szMsg = "["+szOperationName+"] 전문수신 : TCCODE=" + szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			szMsg = "["+szOperationName+"] 메소드 시작 - 전문확인";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			ydUtils.displayRecord(szOperationName, msgRecord);
		
			//받은 전문 편집
			//스케줄코드
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			if(szYD_SCH_CD.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}
			//적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
			if(szYD_STK_COL_GP.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}
			//적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
			if(szYD_STK_BED_NO.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}
			//재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_CARRY_IN_SH");
			if(intMtlCnt == 0){
				
				szMsg = "["+szOperationName+"] [전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}

			//재료번호, 권상모음순서, 적치단(테스트용)
			for(int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
				
				//재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO" + Loop_i);
				if(szSTL_NO[Loop_i].equals("")){
					
					szMsg = "["+szOperationName+"] [전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
					
				}
				//권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_COLL_SEQ" + Loop_i);
				if(szYD_UP_COLL_SEQ[Loop_i].equals("")){
					
					szMsg = "["+szOperationName+"] [전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i] + ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;	
					
				}
			}
			//준비스케줄ID
			szYD_PREP_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_PREP_SCH_ID");

			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if(!blnRtnVal) return ;
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//스케줄CD 체크
			//스케줄 금지 유무
			szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");
			//작업크레인
			szYD_WRK_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
			//작업크레인우선순위
			szYD_WRK_CRN_PRIOR     = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN_PRIOR");
			//대체크레인유무
			szYD_ALT_CRN_YN   = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
			//대체크레인
			szYD_ALT_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
			//대체크레인우선순위
			szYD_ALT_CRN_PRIOR     = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_PRIOR");
			
			
			//스케줄 금지 유무가  "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if(szYD_SCH_PROH_EXN.equals("Y")){
				
				szMsg = "["+szOperationName+"] 스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				//return ;
			}
			
			//작업크레인 설비 상태 체크
			blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);
			
			//작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if(!blnRtnVal){
				
				szMsg = "["+szOperationName+"] 작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
				//대체크레인의 유무를 체크한다.
				//대체크레인이 없으면 에러 리턴
				if(!szYD_ALT_CRN_YN.equals("Y")){
					
					szMsg = "["+szOperationName+"] 대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if(!blnRtnVal){
					
					szMsg = "["+szOperationName+"] 대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
					
				} else {
					//대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					szCrn = szYD_ALT_CRN;
					szYD_SCH_PRIOR = szYD_ALT_CRN_PRIOR;
				}
			} else {
				//작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				szCrn = szYD_WRK_CRN;
				szYD_SCH_PRIOR = szYD_WRK_CRN_PRIOR;
			}

			//재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다.
			//작업예약재료 등록 여부를 체크한다.
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
				
				//크레인사양과 저장품 사양을 체크(길이,폭,중량)
				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
				if(!blnRtnVal) return ;
				
				//다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if(!blnRtnVal) return ;
				
			}	
			
			//리턴 recordSet 생성
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			
			//작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if(!blnRtnVal) return ;
			//레코드추출
			rsResult.first();
			recPara = rsResult.getRecord();
			//작업예약ID
			szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");
			
			//INSERT 항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			//야드구분
			String szYD_GP       = szYD_SCH_CD.substring(0, 1);
			//동구분
			String szYD_BAY_GP   = szYD_SCH_CD.substring(1,2);
			
			//INSERT할 항목 SET
			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			recPara.setField("YD_GP", 		szYD_GP);
			recPara.setField("YD_BAY_GP", 	szYD_BAY_GP);
			recPara.setField("YD_SCH_CD", 	szYD_SCH_CD);
			recPara.setField("YD_SCH_PRIOR", 	szYD_SCH_PRIOR);
			//야드To위치결정방법
			recPara.setField("YD_TO_LOC_DCSN_MTD", "F");
			//야드To위치Guide
			recPara.setField("YD_TO_LOC_GUIDE", szYD_STK_COL_GP + szYD_STK_BED_NO);
			recPara.setField("YD_AIM_YD_GP", 	szYD_GP);
			recPara.setField("YD_AIM_BAY_GP", 	szYD_BAY_GP);
			recPara.setField("REGISTER", 	szUser);
	
			//작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if(intRtnVal < 1){
				szMsg = "["+szOperationName+"] 작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			
			
			//조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
			//recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			//recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", 	  szUser);

			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
				
				//리턴 recordSet 생성
				rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
				//재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				intRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C", rsResult);
				if(intRtnVal != 1) {
					if( intRtnVal == 0 ) {
						intRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "U", rsResult);
						if(intRtnVal != 1) {
							szMsg="["+szOperationName+"] 적치단에 재료["+szSTL_NO[Loop_i]+"]가 존재하지 않습니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							throw new DAOException(szMsg);
						}
					}else{
						szMsg="["+szOperationName+"] 적치단에 재료["+szSTL_NO[Loop_i]+"] 조회 시 오류발생";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						throw new DAOException(szMsg);
					}
					//return ;
				}
				
				//레코드추출
				rsResult.first();
				recStkPara = rsResult.getRecord();
				
				//재료번호
				recPara.setField("STL_NO", 		   szSTL_NO[Loop_i]);
				//적치열구분
				recPara.setField("YD_STK_COL_GP",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_COL_GP"));
				//적치BED번호
				recPara.setField("YD_STK_BED_NO",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_BED_NO"));
				//적치단번호
				recPara.setField("YD_STK_LYR_NO",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_LYR_NO"));
				//권상모음순서
				recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ[Loop_i]);
				
				// 작업예약재료 테이블에 등록한다.
				intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
				
				if(intRtnVal < 1){
					szMsg = "["+szOperationName+"] 작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					throw new DAOException(szMsg);
					//return ;
				}
			}
		
			if( !szYD_PREP_SCH_ID.equals("") ) {
				try {
					String szRtnMsg = YdCommonUtils.deletePreSch(szYD_PREP_SCH_ID, szYD_WBOOK_ID, szMethodName);
				}catch(JDTOException ex) {
					throw new DAOException(ex);
				}
			}
			
			//스케줄코드, 설비id
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("JMS_TC_CD", 	"YDYDJ500");
			recPara.setField("YD_SCH_CD", 	szYD_SCH_CD);
			recPara.setField("YD_EQP_ID", 	szCrn);
			
			ydDelegate.sendMsg(recPara);
			
			szMsg = "["+szOperationName+"] 크레인스케줄 호출 - 메소드 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		} catch(Exception e){
			szMsg = "["+szOperationName+"] 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}
	
	} // end of procCCSShearSupCarryInWrkReq()
	
	/**
	 * 오퍼레이션명 : C연주소재이송상차LOT편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return String
	 * @throws JDTOException
	 */
		
	public String procCCsMatlFtmvCarLdLotComp1(JDTORecord msgRecord) throws JDTOException  {
		/*
		 * 업무기준 : 1. 상차LOT편성이 자동이면 호출됨
		 * 			 2. 이송대상재가 존재하는 지 조회
		 * 				2-1. 존재하면서 직상차이면 아직 PICKUP BED에 있으므로 LOT편성작업을 진행하지 않고 상차작업요구 모듈을 호출
		 * 				2-2. 존재하면서 일반야드에 적치중이면 LOT편성을 해서 상차작업요구 모듈을 호출
		 * 				2-3. 존재하지 않으면 LOT편성작업을 진행하지 않고 상차작업요구 모듈을 호출
		 */
		//Delegate 변수 선언
		YdDelegate ydDelegate 			= new YdDelegate();
		//JDTO 변수 선언
		JDTORecord recPara     			= null;
		JDTORecord recOutPara  			= null;
		JDTORecord recSpecPara  		= null;
		JDTORecordSet rsResult 			= null;
		JDTORecordSet rsSpecResult 		= null;
		//기본적으로 사용되는 변수 정의
		String szMethodName    			= "procCCsMatlFtmvCarLdLotComp1";
		String szOperationName 			= "C연주소재이송상차LOT편성";
		boolean blnRtnVal      			= false;
		int intRtnVal          			= -100;
		String szMsg           			= "";
		//로컬변수 정의
		String szYD_CAR_SCH_ID			= null;									//차량스케줄ID
		String szYD_STK_COL_GP     		= null;									//적치열구분
		String szYD_STK_BED_NO			= null;									//적치베드
		String szYD_STK_LYR_NO			= null;									//적치단
		String szYD_AIM_YD_GP			= "";									//야드목표야드구분
		String szYD_AIM_BAY_GP			= "";									//야드목표동구분
		String szYD_GP					= "";									//야드구분
		String szYD_BAY_GP				= "";									//야드동구분
		String szYD_EQP_ID         		= null;									//설비ID
		
		/* 직상차 구분 
		 * C - 직상차 (작업요구 없음 - 차량스케줄만 생성)
		 * Y - 야드로부터 상차(대상재에 대한 작업요구, 차량스케줄 생성) - 기본값으로 설정
		 * B - 대상재없이 공차 도착(작업요구 없음 - 차량스케줄만 생성)
		 */
		String szYD_CARLD_GP 		= "Y";	
		//차량사용구분
		String szYD_CAR_USE_GP     = null;
		//운송장비코드
		String szTRN_EQP_CD        = null;
		//차량이송재료매수
		int intYD_CARLD_SH         = 0;
		//차량작업허용매수
		int intYD_WRK_ALW_SH       = 0;
		//대상 재료 중량 합계
		long lngSumMtlWt           = 0;
		//차량작업허용중량
		long lngYD_WRK_ALW_WT      = 0;
		//재료중량
		long lngYD_MTL_WT			= 0;
		//연계모듈을 EJB Call or JMS Call 호출 유무 변수
		String szIS_EJB_CALL = null;
		//재료번호
		String szSTL_NO = null;
		//리턴값
		String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if(szRcvTcCode == null || szRcvTcCode.equals("")){
		
			szMsg = "["+szOperationName+"] TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_TC_ERROR;
		
		}
		//TC CODE DISPLAY
		if(bDebugFlag){
		
			szMsg="["+szOperationName+"] 전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			szMsg="["+szOperationName+"] 메소드 시작 : 전문확인";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			ydUtils.displayRecord(szOperationName, msgRecord);

			//받은 전문 편집
			//설비ID
			szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_SCH_ID");
			if(szYD_CAR_SCH_ID.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 차량스케줄ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			
			szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(msgRecord, "TRN_EQP_CD");
			if(szTRN_EQP_CD.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 운송장비코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			szMsg = "["+szOperationName+"] 이송대상재 조회 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			intRtnVal = chkGetCCsMtlFtmvCarLoadLotGp(msgRecord, rsResult);
			intYD_WRK_ALW_SH = rsResult.size();
			szMsg = "["+szOperationName+"] 조회된 대상재 매수["+intYD_WRK_ALW_SH+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//임춘수 2009.04.23 추가
			if( intRtnVal < 0 ){
				szMsg="["+szOperationName+"] 대상재 조회 시 오류발생 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;		
			}else if( intRtnVal == 0 ){				//대상재가 존재하지 않음
				szYD_CARLD_GP = "B";
				szMsg="["+szOperationName+"] 대상재가 존재하지 않음";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}else if( intRtnVal == 100 ){			//PICKUP BED에 대상재 존재 - 직상차
				szYD_CARLD_GP = "C";
				szMsg="["+szOperationName+"] PICKUP BED에 대상재 존재 - 직상차";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				intYD_WRK_ALW_SH = 0;
			}else{									//일반야드에서 대상재 존재
				szMsg="["+szOperationName+"] 일반야드에서 대상재 존재";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				szYD_CARLD_GP = "Y";
			}
			szMsg = "["+szOperationName+"] 이송대상재 조회 성공";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szMsg = "["+szOperationName+"] 운송장비코드["+szTRN_EQP_CD+"]로 차량사양 조회 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//리턴 recordSet 생성
			rsSpecResult = JDTORecordFactory.getInstance().createRecordSet("");

			//차량사양 Select
			blnRtnVal = chkGetCarSpec(szYD_EQP_ID, szYD_CAR_USE_GP, szTRN_EQP_CD, rsSpecResult);
			if(!blnRtnVal) return YdConstant.RETN_CD_FAILURE;

			//레코드 추출
			rsSpecResult.first();
			recSpecPara = rsSpecResult.getRecord();
			
			//차량작업허용매수
			//intYD_WRK_ALW_SH = ydDaoUtils.paraRecChkNullInt(recSpecPara, "YD_WRK_ALW_SH");
			//차량작업허용중량
			lngYD_WRK_ALW_WT = ydDaoUtils.paraRecChkNullLong(recSpecPara, "YD_WRK_ALW_WT");
			
			szMsg = "["+szOperationName+"] 운송장비코드["+szTRN_EQP_CD+"]로 차량사양 조회 - 차량작업허용중량 [" + lngYD_WRK_ALW_WT + "], 조회된 대상재 매수["+intYD_WRK_ALW_SH+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


			//레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			//JMS TC CODE
			recOutPara.setField("JMS_TC_CD",     "YDYDJ244");
			
			
			//차량 이송 가능 매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= intYD_WRK_ALW_SH; Loop_i++){
				rsResult.absolute(Loop_i);
				recPara = rsResult.getRecord();
				
				lngYD_MTL_WT 		= Long.parseLong(ydDaoUtils.paraRecChkNull(recPara, "YD_MTL_WT"));
				szSTL_NO 			= ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
				szYD_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");
				szYD_STK_BED_NO 	= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
				szYD_STK_LYR_NO 	= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO");
				szYD_GP 			= ydDaoUtils.paraRecChkNull(recPara, "YD_GP");
				szYD_BAY_GP 		= ydDaoUtils.paraRecChkNull(recPara, "YD_BAY_GP");
				szYD_AIM_YD_GP 		= ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_YD_GP");
				szYD_AIM_BAY_GP 	= ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_BAY_GP");
				
				szMsg = "["+szOperationName+"] 차량작업허용중량  - lngYD_WRK_ALW_WT[" + lngYD_WRK_ALW_WT + "], ";
				szMsg += "\n 현재 총중량["+lngSumMtlWt+"] ------ 조회된 대상재["+szSTL_NO+"] - 중량["+lngYD_MTL_WT+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				
				//대상 재료 중량 합계
				lngSumMtlWt = lngSumMtlWt + lngYD_MTL_WT;
				//차량작업허용중량보다 대상재료들의 중량이 많으면 편성 중지
				if(lngYD_WRK_ALW_WT < lngSumMtlWt) break;
				//재료번호
				recOutPara.setField("STL_NO" + Loop_i, szSTL_NO);
				recOutPara.setField("YD_STK_COL_GP" + Loop_i, szYD_STK_COL_GP);
				recOutPara.setField("YD_STK_BED_NO" + Loop_i, szYD_STK_BED_NO);
				recOutPara.setField("YD_STK_LYR_NO" + Loop_i, szYD_STK_LYR_NO);
				//권상모음순서
				recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, "" + Loop_i);
				//차량이송재료매수
				intYD_CARLD_SH = Loop_i;
			}
			//직상차인 경우
			if( szYD_CARLD_GP.equals("C") ) {
				rsResult.absolute(1);
				recPara = rsResult.getRecord();
				szYD_GP 	= ydDaoUtils.paraRecChkNull(recPara, "YD_GP");
				szYD_BAY_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_BAY_GP");
			}

			recOutPara.setField("YD_CAR_SCH_ID",    szYD_CAR_SCH_ID);
			recOutPara.setField("YD_CARLD_SH",   	"" + intYD_CARLD_SH);
			recOutPara.setField("YD_GP", 			szYD_GP);
			recOutPara.setField("YD_BAY_GP",     	szYD_BAY_GP);
			recOutPara.setField("YD_AIM_YD_GP", 	szYD_AIM_YD_GP);
			recOutPara.setField("YD_AIM_BAY_GP",     szYD_AIM_BAY_GP);
			recOutPara.setField("TRN_EQP_CD",     	szTRN_EQP_CD);
			recOutPara.setField("YD_CARLD_GP", 		szYD_CARLD_GP);
			
			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	    	 * EJB Call or JMS Call 유무를 판단하여 호출하는 기능
	    	 * 수정자 : 임춘수
	    	 * 일자 : 2009.07.09
	    	 ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			szIS_EJB_CALL = "N";
	    	if( szIS_EJB_CALL.equals("Y")){
	    		//EJB Call ==> 메소드 콜
	    		szMsg = "["+szOperationName+"] 차량상차 작업요구 송신 - 메소드[procCCsCarLdWrkReq] 콜 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
	    		szRtnMsg = procCCsCarLdWrkReq(recOutPara);
				
				szMsg = "["+szOperationName+"] 차량상차 작업요구 송신 - 메소드[procCCsCarLdWrkReq] 콜 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	}else{
				//전문 송신
				ydDelegate.sendMsg(recOutPara);
				szMsg = "["+szOperationName+"] 차량상차 작업요구 송신[procCCsCarLdWrkReq] - 전문 송신 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	}
			
		} catch(Exception e){
			szMsg = "["+szOperationName+"] Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;
		}
		return szRtnMsg;
	} //end of procCCsMatlFtmvCarLdLotComp1
	
	/**
	 * 오퍼레이션명 : C연주차량상차작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return String
	 * @throws JDTOException
	 */
	public String procCCsCarLdWrkReq1(JDTORecord msgRecord)throws JDTOException  {
		/*
		 * 업무기준 : 1. 상차LOT가 존재하는 경우에는 작업예약과 작업예약재료에 등록
		 * 			 2. 직상차이거나 일반야드에 대상재가 존재하는 경우에는 사용가능한 차량정지POINT를 조회
		 * 			 	2-1. 사용가능한 차량정지POINT가 존재하는 지 판단
		 * 					2-1-1. 존재하는 경우 해당 차량정지POINT를 설정하고 차량POINT요구지시 모듈을 호출
		 * 					2-1-2. 존재하지 않는 경우 차량정지POINT가 없음을 표시하는 [0000]을 설정하여 차량POINT요구지시 모듈을 호출
		 * 			 3. 대상재가 존재하지 않는 경우에는 차량POINT요구지시 모듈을 호출하지 않음
		 */
		//YdDelegate ydDelegate = new YdDelegate();
		//JDTO 정의
		JDTORecord recPara     				= null;
		//DAO 정의
		YdWrkbookDao ydWrkbookDao       	= new YdWrkbookDao();			//작업예약DAO
		YdWrkbookMtlDao ydWrkbookMtlDao 	= new YdWrkbookMtlDao();		//작업예약 재료 DAO
		YdCarSchDao ydCarSchDao 			= new YdCarSchDao();			//차량스케줄DAO
		//기본변수 정의
		int intRtnVal          				= 0;
		String szMsg           				= "";
		String szMethodName    				= "procCCsCarLdWrkReq";
		String szOperationName 				= "C연주/통합차량상차작업요구";
		String szRtnMsg						= null;
		//로컬변수 정의
		String szYD_CAR_SCH_ID 				= null;
		String szYD_STK_COL_GP 				= "";
		String szYD_STK_BED_NO 				= null;
		String szYD_STK_LYR_NO 				= null;
		String szYD_PNT_CD1					= "";
		String  szYD_CARLD_STOP_LOC 		= "";
		String szYD_UP_COLL_SEQ  			= null;							//권상모음순서		
		String szTRN_EQP_CD       			= null;							//운송장비코드
		int intMtlCnt              			= 0;							//재료매수(int)
		//재료번호
		String szSTL_NO          = null;
		//스케줄코드
		String szYD_SCH_CD         = null;
		//스케줄우선순위
		String szYD_SCH_PRIOR		= "";
		//작업예약ID
		String szYD_WBOOK_ID       = null;
		//야드목표야드구분
		String szYD_AIM_YD_GP        = null;
		//야드목표동구분
		String szYD_AIM_BAY_GP       = null;
		//야드목표야드구분
		String szYD_GP        = null;
		//야드목표동구분
		String szYD_BAY_GP       = null;
		//직상차구분
		String szYD_CARLD_GP = "";
		//EJB CALL or JMS CALL 판단 변수
		String szIS_EJB_CALL		= null;
		EJBConnector ejbConn 		= null;
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		
		//에러 리턴
		if(szRcvTcCode == null || szRcvTcCode.equals("")){
		
			szMsg = "[" + szOperationName + "] TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_TC_ERROR;
		
		}
		//TC CODE DISPLAY
		if(bDebugFlag){
		
			szMsg = "[" + szOperationName + "] 전문수신 : TCCODE=" + szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			szMsg = "[" + szOperationName + "] 메소드 시작 : 전문내용 확인";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			ydUtils.displayRecord(szOperationName, msgRecord);
			//받은 전문 편집
			//차량스케줄ID
			szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_SCH_ID");
			if(szYD_CAR_SCH_ID.equals("")){
				
				szMsg = "[" + szOperationName + "] [전문 이상] 차량스케줄ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(msgRecord, "TRN_EQP_CD");
			if(szTRN_EQP_CD.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 운송장비코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			szYD_CARLD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CARLD_GP");
			//야드목표야드구분
			szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_YD_GP");
			//야드목표동구분
			szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_BAY_GP");
			//야드구분
			szYD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP");
			//야드동구분
			szYD_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP");
			//재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_CARLD_SH");

			if( intMtlCnt > 0 ) {
				szMsg="["+szOperationName+"] 상차LOT편성 대상재가 존재합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP1");
				szYD_GP = szYD_STK_COL_GP.substring(0, 1);
				szYD_BAY_GP = szYD_STK_COL_GP.substring(1, 2);
				szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT01UM";
				
				szMsg="["+szOperationName+"] 야드구분["+szYD_GP+"], 동구분["+szYD_BAY_GP+"], 스케줄코드["+szYD_SCH_CD+"] - 스케줄기준체크 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//스케줄기준체크
				recPara = JDTORecordFactory.getInstance().create();
				intRtnVal = YdCommonUtils.getCrnInfoByCrnSchRule(szYD_SCH_CD, recPara);
				if( intRtnVal < 1 ) {
					return YdConstant.RETN_CD_FAILURE;
				}
				szYD_SCH_PRIOR = StringHelper.evl(recPara.getFieldString("YD_SCH_PRIOR"), "");
				szYD_WBOOK_ID = ydWrkbookDao.getYdWrkbookId();
				
				szMsg="["+szOperationName+"] 작업예약["+ szYD_WBOOK_ID +"]등록 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//INSERT할 항목 SET
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
				recPara.setField("YD_GP", 		  szYD_GP);
				recPara.setField("YD_BAY_GP", 	  szYD_BAY_GP);
				recPara.setField("YD_SCH_PRIOR", 	szYD_SCH_PRIOR);
				recPara.setField("YD_SCH_CD", 	  szYD_SCH_CD);
				recPara.setField("REGISTER", 	  szMethodName.substring(0, 10));
				recPara.setField("YD_CAR_USE_GP", YdConstant.YD_CAR_USE_GP_TS);
				recPara.setField("TRN_EQP_CD", 	  szTRN_EQP_CD);
				recPara.setField("YD_AIM_YD_GP",  szYD_AIM_YD_GP);
				recPara.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP);
		
				//작업예약 INSERT
				intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
				if(intRtnVal < 1){
					szMsg = "["+szOperationName+"] 작업예약["+ szYD_WBOOK_ID +"] 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;
				}
				
				szMsg="["+szOperationName+"] 작업예약["+ szYD_WBOOK_ID +"]등록 성공";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//조회항목 record 생성
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
				recPara.setField("REGISTER", 	  szMethodName.substring(0, 10));
				
				szMsg="["+szOperationName+"] 작업예약["+ szYD_WBOOK_ID +"]재료 등록 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
					
					szSTL_NO = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO" + Loop_i);
					szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord,"YD_STK_COL_GP" + Loop_i);
					szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord,"YD_STK_BED_NO" + Loop_i);
					szYD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(msgRecord,"YD_STK_LYR_NO" + Loop_i);
					szYD_UP_COLL_SEQ = ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_COLL_SEQ" + Loop_i);
					//재료번호
					recPara.setField("STL_NO", 		   szSTL_NO);
					//적치열구분
					recPara.setField("YD_STK_COL_GP",  szYD_STK_COL_GP);
					//적치BED번호
					recPara.setField("YD_STK_BED_NO",  szYD_STK_BED_NO);
					//적치단번호
					recPara.setField("YD_STK_LYR_NO",  szYD_STK_LYR_NO);
					//권상모음순서
					recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ);
					
					// 작업예약재료 테이블에 등록한다.
					intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
					
					if(intRtnVal < 1){
						szMsg = "["+szOperationName+"] 작업예약재료 데이터 등록 중 에러";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return YdConstant.RETN_CD_FAILURE;
					}
					szMsg="["+szOperationName+"] 작업예약["+ szYD_WBOOK_ID +"] - [" + Loop_i + "]재료[" + szSTL_NO + "] 등록 성공";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
				
				szMsg="["+szOperationName+"] 작업예약["+ szYD_WBOOK_ID +"]재료 등록 성공";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			//사용가능한 차량정지위치 구하기
			//차량정지위치 구하기
			if( szYD_CARLD_GP.equals("C") || szYD_CARLD_GP.equals("Y") ) {
				szMsg="["+szOperationName+"] 사용가능한 차량정지위치 메소드 호출 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				recPara = JDTORecordFactory.getInstance().create();
				szRtnMsg = YdCommonUtils.getUsableCarStopLoc(szYD_GP, szYD_BAY_GP, recPara);
				if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) return szRtnMsg;
				szYD_PNT_CD1 = ydDaoUtils.paraRecChkNull(recPara,"YD_PNT_CD");
				szYD_CARLD_STOP_LOC = ydDaoUtils.paraRecChkNull(recPara,"YD_CARLD_STOP_LOC");
				szMsg="["+szOperationName+"] 사용가능한 차량정지위치 메소드 호출 완료 - YD_CARLD_STOP_LOC["+szYD_CARLD_STOP_LOC+"], YD_PNT_CD["+szYD_PNT_CD1+"] ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			szMsg="["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"]에 차량상차정지위치["+szYD_CARLD_STOP_LOC+"]와 야드포인트코드["+szYD_PNT_CD1+"] 수정 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//차량스케줄 수정.
			//rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CAR_SCH_ID",    szYD_CAR_SCH_ID);
			recPara.setField("MODIFIER",    	szMethodName.substring(0, 10));
			recPara.setField("YD_PNT_CD1",    	szYD_PNT_CD1);
			recPara.setField("YD_CARLD_STOP_LOC",    szYD_CARLD_STOP_LOC);
			//intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult, 0);
			intRtnVal = ydCarSchDao.updYdCarsch(recPara, 0);
			if( intRtnVal < 0  ) {
				szMsg="["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"] 수정 시 오류발생 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}else if( intRtnVal == 0 ) {
				szMsg="["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"] 수정 시 차량스케줄이 존재하지 않습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}
			szMsg="["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"]에 차량상차정지위치["+szYD_CARLD_STOP_LOC+"]와 야드포인트코드["+szYD_PNT_CD1+"] 수정 성공";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//++++++++++++++++++++++++++++++++++++++++++++++
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("TRN_EQP_CD",    szTRN_EQP_CD);
			if(  szYD_CARLD_GP.equals("C") || szYD_CARLD_GP.equals("Y") )  {
					if( szYD_PNT_CD1.equals("")  ) {
						//직상차이거나 일반야드에 대상재가 존재하고 차량정지POINT가 존재하지 않을 경우
						recPara.setField("YD_PNT_CD",    "0000");
						szMsg="["+szOperationName+"] 차량정지POINT가 존재하지 않을 경우 [0000]을 설정하고 차량POINT요구지시 모듈 호출";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}else{
						szMsg="["+szOperationName+"] 차량정지POINT가 존재하는 경우 [" + szYD_PNT_CD1 + "]을 설정하고 차량POINT요구지시 모듈 호출";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
					ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);
					ejbConn.trx("procMatlCarArrPntReq", new Class[] { JDTORecord.class }, new Object[] { recPara });
			}else{
				//대상재가 존재하지 않는 경우에는 차량POINT요구지시 모듈을 호출하지 않음
				szMsg="["+szOperationName+"] 대상재가 존재하지 않는 경우에는 차량POINT요구지시 모듈을 호출하지 않음";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
		
		} catch(Exception e){
			szMsg = "["+szOperationName+"] 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;
		}

		return YdConstant.RETN_CD_SUCCESS;
	} // end of procCCsCarLdWrkReq()
	
	/**
	 * 오퍼레이션명 : C연주소재이송상차LOT편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return String
	 * @throws JDTOException
	 */
		
	public String procCCsMatlFtmvCarLdLotComp(JDTORecord msgRecord) throws JDTOException  {
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils 			= new YdDaoUtils();
		//레코드 선언
		JDTORecord recPara     			= null;
		JDTORecord recOutPara  			= null;
		//레코드셋 선언
		JDTORecordSet rsResult 			= null;
		//리턴값(int)
		int intRtnVal          			= 0;
		//메세지
		String szMsg           			= "";
		//메소드명
		String szMethodName    			= "procCCsMatlFtmvCarLdLotComp";
		String szOperationName 			= "C연주소재이송상차LOT편성";
		String szREG_MOD_USER			= szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName;

		//운송장비코드
		String szTRN_EQP_CD        		= null;
		//발지개소코드
		String szSPOS_WLOC_CD      		= null;
		//차량스케줄ID
		String szYD_CAR_SCH_ID			= null;
		//스케줄코드
		String szYD_SCH_CD         		= null;
		
		String szYD_PREP_SCH_ID			= null;
		String szYD_WBOOK_ID			= null;
		String szSTL_NO 				= null;
		String szYD_GP					= null;
		String szYD_BAY_GP				= null;
		String szYD_STK_COL_GP     		= null;
		String szYD_STK_BED_NO			= null;
		String szYD_STK_LYR_NO			= null;
		String szYD_AIM_YD_GP			= null;
		String szYD_AIM_BAY_GP			= null;
		String szYD_SCH_PRIOR			= null;
		
		YdWrkbookDao		ydWrkbookDao	= new YdWrkbookDao();
		YdWrkbookMtlDao		ydWrkbookMtlDao	= new YdWrkbookMtlDao();
		
		//리턴값
		String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if(szRcvTcCode == null){
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_TC_ERROR;
		
		}
		//TC CODE DISPLAY
		if(bDebugFlag){
		
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			//-------------------------------------------------------------------------------------------------
			//	파라미터 확인
			//-------------------------------------------------------------------------------------------------
			
			szMsg = "["+szOperationName+"] ---------------------- 메소드 시작 : 파라미터 확인 ----------------------";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			ydUtils.displayRecord(szOperationName, msgRecord);
			
			//운송장비코드
			szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(msgRecord, "TRN_EQP_CD");
			if(szTRN_EQP_CD.equals("")){
				
				szMsg = "["+szOperationName+"] 운송장비코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			
			//발지개소코드
			szSPOS_WLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_WLOC_CD");
			if(szSPOS_WLOC_CD.equals("")){
				
				szMsg = "["+szOperationName+"] 발지개소코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			
			//차량스케줄ID
			szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_SCH_ID");
			if(szYD_CAR_SCH_ID.equals("")){
				
				szMsg = "["+szOperationName+"] 차량스케줄ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			
			//-------------------------------------------------------------------------------------------------
			
			
			//-------------------------------------------------------------------------------------------------
			//	배차순서가 빠르고 준비스케줄ID가 빠른 차량 이송 대상재를 준비스케줄에서 조회
			//-------------------------------------------------------------------------------------------------
			
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			recPara  = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_GP", 			YdConstant.YD_GP_C_SLAB_YARD);
			recPara.setField("YD_SCH_CD", 		YdConstant.YD_GP_C_SLAB_YARD + "_PT");
			recPara.setField("YD_WRK_PLAN_CRN", "");
			recPara.setField("YD_PREP_WK_ST", 	"L");
			//항목추가
			recPara.setField("CAR_GP", 	szTRN_EQP_CD.substring(1, 2));
			
			szMsg = "["+szOperationName+"] 배차순서가 빠르고 준비스케줄ID가 빠른 차량 이송 대상재를 준비스케줄에서 조회 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szRtnMsg			= DaoManager.getYdStock(recPara, rsResult, 151);
			
			szMsg = "["+szOperationName+"] 배차순서가 빠르고 준비스케줄ID가 빠른 차량 이송 대상재를 준비스케줄에서 조회 완료 - 메세지 : " + szRtnMsg + ", 대상재건수 : " + rsResult.size();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			if( szRtnMsg.equals(YdConstant.RETN_CD_NOTEXIST) ) {
				
				//-------------------------------------------------------------------------------------------------
				//	대상재가 존재하지 않는 경우에는 차량정지POINT요구 모듈 호출
				//-------------------------------------------------------------------------------------------------
				
				szMsg="["+szOperationName+"] 대상재가 존재하지 않는 경우 소재차량도착Point요구 모듈 호출 시작 - JMS Call";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				YdCommonUtils.callMatlCarArrPntReq(szTRN_EQP_CD, szSPOS_WLOC_CD, "N", this);
				
				szMsg="["+szOperationName+"] 대상재가 존재하지 않는 경우 소재차량도착Point요구 모듈 호출 완료 - JMS Call";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//-------------------------------------------------------------------------------------------------
				
				return szRtnMsg;
				
			}else if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				
				szMsg = "["+szOperationName+"] 배차순서가 빠르고 준비스케줄ID가 빠른 차량 이송 대상재를 준비스케줄에서 조회 시 오류발생 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
				return szRtnMsg;
			}
			
			//-------------------------------------------------------------------------------------------------
			
			
			//-------------------------------------------------------------------------------------------------
			//	작업예약/작업예약재료 등록
			//-------------------------------------------------------------------------------------------------
			
			
			
			for(int i = 1; i <= rsResult.size(); i++ ) {
				
				rsResult.absolute(i);
				recPara			= rsResult.getRecord();
				
				szSTL_NO				= ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
				
				szYD_SCH_CD				= ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_CD");
				szYD_GP					= szYD_SCH_CD.substring(0, 1);
				szYD_BAY_GP				= szYD_SCH_CD.substring(1, 2);
				szYD_AIM_YD_GP			= ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_YD_GP");
				szYD_AIM_BAY_GP			= ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_BAY_GP");
				
				szYD_STK_COL_GP			= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");
				szYD_STK_BED_NO			= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
				szYD_STK_LYR_NO			= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO");
				
				if( i == 1 ) {
					
					szYD_PREP_SCH_ID				= ydDaoUtils.paraRecChkNull(recPara, "YD_PREP_SCH_ID");
					
					//-------------------------------------------------------------------------------------------------
					//	스케줄코드 조회
					//-------------------------------------------------------------------------------------------------
					
					szMsg = "["+szOperationName+"] 스케줄코드["+szYD_SCH_CD+"]에 대한 스케줄 기준 조회 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					recOutPara = JDTORecordFactory.getInstance().create();
					
					szRtnMsg			= YdCommonUtils.getWrkableCrnBySchRule(szYD_SCH_CD, recOutPara);
					
					szMsg = "["+szOperationName+"] 스케줄코드["+szYD_SCH_CD+"]에 대한 스케줄 기준 조회 완료 - 메세지 : " + szRtnMsg + ", 대상재건수 : " + rsResult.size();
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					if( szRtnMsg.equals(YdConstant.RETN_CRN_NO_ALT_CRN)
							|| szRtnMsg.equals(YdConstant.YD_EQP_STAT_BREAK) 
							|| szRtnMsg.equals(YdConstant.YD_EQP_WRK_MODE_OFF_LINE)
							) {
						szYD_SCH_PRIOR				= ydDaoUtils.paraRecChkNull(recOutPara, "YD_WRK_CRN_PRIOR");
					}else if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
						
						szMsg = "["+szOperationName+"] 스케줄코드["+szYD_SCH_CD+"]에 대한 스케줄 기준 조회 시 오류발생 - 메세지 : " + szRtnMsg;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						
						return szRtnMsg;
					}else{
						szYD_SCH_PRIOR				= ydDaoUtils.paraRecChkNull(recOutPara, "YD_SCH_PRIOR");
					}
					
					//-------------------------------------------------------------------------------------------------
					
					//-------------------------------------------------------------------------------------------------
					//	작업예약 등록
					//-------------------------------------------------------------------------------------------------
					
					szYD_WBOOK_ID			= ydWrkbookDao.getYdWrkbookId();
					
					recOutPara = JDTORecordFactory.getInstance().create();
					
					recOutPara.setField("YD_WBOOK_ID", 			szYD_WBOOK_ID);
					recOutPara.setField("REGISTER", 			szREG_MOD_USER);
					recOutPara.setField("YD_GP", 				szYD_GP);
					recOutPara.setField("YD_BAY_GP", 			szYD_BAY_GP);
					recOutPara.setField("YD_SCH_CD", 			szYD_SCH_CD);
					recOutPara.setField("YD_SCH_PRIOR", 		szYD_SCH_PRIOR);
					recOutPara.setField("YD_AIM_YD_GP", 		szYD_AIM_YD_GP);
					recOutPara.setField("YD_AIM_BAY_GP", 		szYD_AIM_BAY_GP);
					recOutPara.setField("YD_CAR_USE_GP", 		YdConstant.YD_CAR_USE_GP_TS);
					recOutPara.setField("TRN_EQP_CD", 			szTRN_EQP_CD);
					
					szMsg = "["+szOperationName+"] 작업예약["+szYD_WBOOK_ID+"]등록 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					intRtnVal			= ydWrkbookDao.insYdWrkbook(recOutPara);
					
					if( intRtnVal <= 0 ) {
						
						szMsg = "["+szOperationName+"] 작업예약["+szYD_WBOOK_ID+"]등록 시 오류발생 - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						
						return YdConstant.RETN_CD_FAILURE;
					}
					
					szMsg = "["+szOperationName+"] 작업예약["+szYD_WBOOK_ID+"]등록 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					//-------------------------------------------------------------------------------------------------
				}
				
				//-------------------------------------------------------------------------------------------------
				//	작업예약재료 등록
				//-------------------------------------------------------------------------------------------------
				
				recOutPara = JDTORecordFactory.getInstance().create();
				
				recOutPara.setField("YD_WBOOK_ID", 				szYD_WBOOK_ID);
				recOutPara.setField("REGISTER", 				szREG_MOD_USER);
				recOutPara.setField("STL_NO", 					szSTL_NO);
				recOutPara.setField("YD_STK_COL_GP", 			szYD_STK_COL_GP);
				recOutPara.setField("YD_STK_BED_NO", 			szYD_STK_BED_NO);
				recOutPara.setField("YD_STK_LYR_NO", 			szYD_STK_LYR_NO);
				recOutPara.setField("YD_UP_COLL_SEQ", 			String.valueOf(i));
				
				szMsg = "["+szOperationName+"] ["+i+"] 작업예약["+szYD_WBOOK_ID+"]의 작업재료["+szSTL_NO+"] 등록 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				intRtnVal			= ydWrkbookMtlDao.insYdWrkbookmtl(recOutPara);
				
				if( intRtnVal <= 0 ) {
					
					szMsg = "["+szOperationName+"] ["+i+"] 작업예약["+szYD_WBOOK_ID+"]의 작업재료["+szSTL_NO+"] 등록 시 오류발생 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					
					throw new DAOException(szMsg);
				}
				
				szMsg = "["+szOperationName+"] ["+i+"] 작업예약["+szYD_WBOOK_ID+"]의 작업재료["+szSTL_NO+"] 등록 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//-------------------------------------------------------------------------------------------------
			}
			
			//-------------------------------------------------------------------------------------------------

			
			//-------------------------------------------------------------------------------------------------
			//	준비스케줄 삭제
			//-------------------------------------------------------------------------------------------------
			
			szMsg = "["+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"] 삭제 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szRtnMsg			= YdCommonUtils.deletePreSch(szYD_PREP_SCH_ID, szYD_WBOOK_ID, szMethodName);
			
			if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				
				szMsg = "["+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"] 삭제 시 오류발생 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
				throw new DAOException(szMsg);
			}
			
			szMsg = "["+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"] 삭제 완료 - 메세지 : " + szRtnMsg;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//-------------------------------------------------------------------------------------------------
			
			
			//-------------------------------------------------------------------------------------------------
			//	차량스케줄에 상차작업예약 등록
			//-------------------------------------------------------------------------------------------------
			
			recOutPara = JDTORecordFactory.getInstance().create();
			
			recOutPara.setField("YD_CAR_SCH_ID", 				szYD_CAR_SCH_ID);
			recOutPara.setField("YD_CARLD_WRK_BOOK_ID", 		szYD_WBOOK_ID);
			recOutPara.setField("MODIFIER", 					szREG_MOD_USER);
			
			szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"]에 상차작업예약["+szYD_WBOOK_ID+"] 등록 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szRtnMsg			= DaoManager.updYdCarsch(recOutPara, 0);
			
			if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				
				szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"]에 상차작업예약["+szYD_WBOOK_ID+"] 등록 시 오류발생 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
				throw new DAOException(szMsg);
			}
			
			szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"]에 상차작업예약["+szYD_WBOOK_ID+"] 등록 완료 - 메세지 : " + szRtnMsg;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//-------------------------------------------------------------------------------------------------
			
			
			//-------------------------------------------------------------------------------------------------
			//	차량정지POINT요구 모듈 호출
			//-------------------------------------------------------------------------------------------------
			
			szMsg="["+szOperationName+"] 작업예약["+szYD_WBOOK_ID+"] 등록 후 소재차량도착Point요구 모듈 호출 시작 - EJB Call";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			YdCommonUtils.callMatlCarArrPntReq(szTRN_EQP_CD, szSPOS_WLOC_CD, "Y", this);
			
			szMsg="["+szOperationName+"] 작업예약["+szYD_WBOOK_ID+"] 등록 후 소재차량도착Point요구 모듈 호출 완료 - EJB Call";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//-------------------------------------------------------------------------------------------------
			
	    	szMsg = "["+szOperationName+"] ---------------------- 메소드 끝 ----------------------";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
		}catch(DAOException e){
			szMsg = "["+szOperationName+"] DAOException 예외발생[1] : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw e;
		} catch(Exception e){
			szMsg = "["+szOperationName+"] 예외발생[2] : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMsg);
			//return YdConstant.RETN_CD_FAILURE;
		}
		//return szRtnMsg;
		return YdConstant.RETN_CD_SUCCESS;
	} //end of procCCsMatlFtmvCarLdLotComp
	
	
	/**
	 * 오퍼레이션명 : 통합야드소재이송상차LOT편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return String
	 * @throws JDTOException
	 */
		
	public String procSUnMatlFtmvCarLdLotComp1(JDTORecord msgRecord) throws JDTOException  {
		/*
		 * 업무기준 : 1. 상차LOT편성이 자동이면 호출됨
		 * 			 2. 이송대상재가 존재하는 지 조회
		 * 				2-1. 존재하면서 직상차이면 아직 PICKUP BED에 있으므로 LOT편성작업을 진행하지 않고 상차작업요구 모듈을 호출
		 * 				2-2. 존재하면서 일반야드에 적치중이면 LOT편성을 해서 상차작업요구 모듈을 호출
		 * 				2-3. 존재하지 않으면 LOT편성작업을 진행하지 않고 상차작업요구 모듈을 호출
		 */
		//Delegate 변수 선언
		YdDelegate ydDelegate 			= new YdDelegate();
		//JDTO 변수 선언
		JDTORecord recPara     			= null;
		JDTORecord recOutPara  			= null;
		JDTORecord recSpecPara  		= null;
		JDTORecordSet rsResult 			= null;
		JDTORecordSet rsSpecResult 		= null;
		//기본적으로 사용되는 변수 정의
		String szMethodName    			= "procSUnMatlFtmvCarLdLotComp";
		String szOperationName 			= "통합야드소재이송상차LOT편성";
		boolean blnRtnVal      			= false;
		int intRtnVal          			= -100;
		String szMsg           			= "";
		//로컬변수 정의
		String szYD_CAR_SCH_ID			= null;									//차량스케줄ID
		String szYD_STK_COL_GP     		= null;									//적치열구분
		String szYD_STK_BED_NO			= null;									//적치베드
		String szYD_STK_LYR_NO			= null;									//적치단
		String szYD_AIM_YD_GP			= "";									//야드목표야드구분
		String szYD_AIM_BAY_GP			= "";									//야드목표동구분
		String szYD_GP					= "";									//야드구분
		String szYD_BAY_GP				= "";									//야드동구분
		String szYD_EQP_ID         		= null;									//설비ID
		
		/* 직상차 구분 
		 * C - 직상차 (작업요구 없음 - 차량스케줄만 생성)
		 * Y - 야드로부터 상차(대상재에 대한 작업요구, 차량스케줄 생성) - 기본값으로 설정
		 * B - 대상재없이 공차 도착(작업요구 없음 - 차량스케줄만 생성)
		 */
		String szYD_CARLD_GP 		= "Y";	
		//차량사용구분
		String szYD_CAR_USE_GP     = null;
		//운송장비코드
		String szTRN_EQP_CD        = null;
		//차량이송재료매수
		int intYD_CARLD_SH         = 0;
		//차량작업허용매수
		int intYD_WRK_ALW_SH       = 0;
		//대상 재료 중량 합계
		long lngSumMtlWt           = 0;
		//차량작업허용중량
		long lngYD_WRK_ALW_WT      = 0;
		//재료중량
		long lngYD_MTL_WT			= 0;
		//연계모듈을 EJB Call or JMS Call 호출 유무 변수
		String szIS_EJB_CALL = null;
		//재료번호
		String szSTL_NO = null;
		//리턴값
		String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if(szRcvTcCode == null || szRcvTcCode.equals("")){
		
			szMsg = "["+szOperationName+"] TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_TC_ERROR;
		
		}
		//TC CODE DISPLAY
		if(bDebugFlag){
		
			szMsg="["+szOperationName+"] 전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			szMsg="["+szOperationName+"] 메소드 시작 : 전문확인";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			ydUtils.displayRecord(szOperationName, msgRecord);

			//받은 전문 편집
			//설비ID
			szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_SCH_ID");
			if(szYD_CAR_SCH_ID.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 차량스케줄ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			
			szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(msgRecord, "TRN_EQP_CD");
			if(szTRN_EQP_CD.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 운송장비코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			szMsg = "["+szOperationName+"] 이송대상재 조회 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//상차 Lot 편성 대상 재료 Select
//			intRtnVal = chkGetSUnMtlFtmvCarLoadLotGp(msgRecord, rsResult);
			
			intRtnVal = chkGetSUnMtlFtmvCarLoadLotGp(msgRecord, rsResult);
			intYD_WRK_ALW_SH = rsResult.size();
			szMsg = "["+szOperationName+"] 조회된 대상재 매수["+intYD_WRK_ALW_SH+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//임춘수 2009.04.23 추가
			if( intRtnVal < 0 ){
				szMsg="["+szOperationName+"] 대상재 조회 시 오류발생 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;		
			}else if( intRtnVal == 0 ){				//대상재가 존재하지 않음
				szYD_CARLD_GP = "B";
				szMsg="["+szOperationName+"] 대상재가 존재하지 않음";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}else if( intRtnVal == 100 ){			//PICKUP BED에 대상재 존재 - 직상차
				szYD_CARLD_GP = "C";
				szMsg="["+szOperationName+"] PICKUP BED에 대상재 존재 - 직상차";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				intYD_WRK_ALW_SH = 0;
			}else{									//일반야드에서 대상재 존재
				szMsg="["+szOperationName+"] 일반야드에서 대상재 존재";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				szYD_CARLD_GP = "Y";
			}
			szMsg = "["+szOperationName+"] 이송대상재 조회 성공";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szMsg = "["+szOperationName+"] 운송장비코드["+szTRN_EQP_CD+"]로 차량사양 조회 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//리턴 recordSet 생성
			rsSpecResult = JDTORecordFactory.getInstance().createRecordSet("");

			//차량사양 Select
			blnRtnVal = chkGetCarSpec(szYD_EQP_ID, szYD_CAR_USE_GP, szTRN_EQP_CD, rsSpecResult);
			if(!blnRtnVal) return YdConstant.RETN_CD_FAILURE;

			//레코드 추출
			rsSpecResult.first();
			recSpecPara = rsSpecResult.getRecord();
			
			//차량작업허용매수
			//intYD_WRK_ALW_SH = ydDaoUtils.paraRecChkNullInt(recSpecPara, "YD_WRK_ALW_SH");
			//차량작업허용중량
			lngYD_WRK_ALW_WT = ydDaoUtils.paraRecChkNullLong(recSpecPara, "YD_WRK_ALW_WT");
			
			szMsg = "["+szOperationName+"] 운송장비코드["+szTRN_EQP_CD+"]로 차량사양 조회 - 차량작업허용중량 [" + lngYD_WRK_ALW_WT + "], 조회된 대상재 매수["+intYD_WRK_ALW_SH+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


			//레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			//JMS TC CODE
			recOutPara.setField("JMS_TC_CD",     "YDYDJ244");
			
			
			//차량 이송 가능 매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= intYD_WRK_ALW_SH; Loop_i++){
				rsResult.absolute(Loop_i);
				recPara = rsResult.getRecord();
				
				lngYD_MTL_WT 		= Long.parseLong(ydDaoUtils.paraRecChkNull(recPara, "YD_MTL_WT"));
				szSTL_NO 			= ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
				szYD_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");
				szYD_STK_BED_NO 	= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
				szYD_STK_LYR_NO 	= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO");
				szYD_GP 			= ydDaoUtils.paraRecChkNull(recPara, "YD_GP");
				szYD_BAY_GP 		= ydDaoUtils.paraRecChkNull(recPara, "YD_BAY_GP");
				szYD_AIM_YD_GP 		= ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_YD_GP");
				szYD_AIM_BAY_GP 	= ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_BAY_GP");
				
				szMsg = "["+szOperationName+"] 차량작업허용중량  - lngYD_WRK_ALW_WT[" + lngYD_WRK_ALW_WT + "], ";
				szMsg += "\n 현재 총중량["+lngSumMtlWt+"] ------ 조회된 대상재["+szSTL_NO+"] - 중량["+lngYD_MTL_WT+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				
				//대상 재료 중량 합계
				lngSumMtlWt = lngSumMtlWt + lngYD_MTL_WT;
				//차량작업허용중량보다 대상재료들의 중량이 많으면 편성 중지
				if(lngYD_WRK_ALW_WT < lngSumMtlWt) break;
				//재료번호
				recOutPara.setField("STL_NO" + Loop_i, szSTL_NO);
				recOutPara.setField("YD_STK_COL_GP" + Loop_i, szYD_STK_COL_GP);
				recOutPara.setField("YD_STK_BED_NO" + Loop_i, szYD_STK_BED_NO);
				recOutPara.setField("YD_STK_LYR_NO" + Loop_i, szYD_STK_LYR_NO);
				//권상모음순서
				recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, "" + Loop_i);
				//차량이송재료매수
				intYD_CARLD_SH = Loop_i;
			}
			//직상차인 경우
			if( szYD_CARLD_GP.equals("C") ) {
				rsResult.absolute(1);
				recPara = rsResult.getRecord();
				szYD_GP 	= ydDaoUtils.paraRecChkNull(recPara, "YD_GP");
				szYD_BAY_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_BAY_GP");
			}

			recOutPara.setField("YD_CAR_SCH_ID",    szYD_CAR_SCH_ID);
			recOutPara.setField("YD_CARLD_SH",   	"" + intYD_CARLD_SH);
			recOutPara.setField("YD_GP", 			szYD_GP);
			recOutPara.setField("YD_BAY_GP",     	szYD_BAY_GP);
			recOutPara.setField("YD_AIM_YD_GP", 	szYD_AIM_YD_GP);
			recOutPara.setField("YD_AIM_BAY_GP",     szYD_AIM_BAY_GP);
			recOutPara.setField("TRN_EQP_CD",     	szTRN_EQP_CD);
			recOutPara.setField("YD_CARLD_GP", 		szYD_CARLD_GP);
			
			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	    	 * EJB Call or JMS Call 유무를 판단하여 호출하는 기능
	    	 * 수정자 : 임춘수
	    	 * 일자 : 2009.07.09
	    	 ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			szIS_EJB_CALL = "N";
	    	if( szIS_EJB_CALL.equals("Y")){
	    		//EJB Call ==> 메소드 콜
	    		szMsg = "["+szOperationName+"] 차량상차 작업요구 송신 - 메소드[procCCsCarLdWrkReq] 콜 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
	    		szRtnMsg = procCCsCarLdWrkReq(recOutPara);
				
				szMsg = "["+szOperationName+"] 차량상차 작업요구 송신 - 메소드[procCCsCarLdWrkReq] 콜 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	}else{
				//전문 송신
				ydDelegate.sendMsg(recOutPara);
				szMsg = "["+szOperationName+"] 차량상차 작업요구 송신[procCCsCarLdWrkReq] - 전문 송신 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	}
			
		} catch(Exception e){
			szMsg = "["+szOperationName+"] Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;
		}
		return szRtnMsg;

	} //end of procSUnMatlFtmvCarLdLotComp
	
	
	/**
	 * 오퍼레이션명 : 통합야드소재이송상차LOT편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return String
	 * @throws JDTOException
	 */
		
	public String procSUnMatlFtmvCarLdLotComp (JDTORecord msgRecord) throws JDTOException  {
		YdDelegate ydDelegate = new YdDelegate();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		//공용 METHOD
		YdUtils ydutils       = new YdUtils();
		
		//레코드 선언
		JDTORecord recPara     = null;
		JDTORecord recOutPara  = null;
		JDTORecord recSpecPara  = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		JDTORecordSet rsSpecResult = null;
		
		//리턴값(boolean)
		boolean blnRtnVal      = false;
		//리턴값(int)
		int intRtnVal          = 0;
		//메세지
		String szMsg           = "";
		//메소드명
		String szMethodName    = "procSUnMatlFtmvCarLdLotComp";
		//오퍼레이션명
		String szOperationName		= "통합야드소재이송상차LOT편성";

		//설비ID
		String szYD_EQP_ID         = null;
		//적치열구분
		String szYD_STK_COL_GP     = null;
//		//상차정지위치
//		String szYD_CARLD_STOP_LOC = null;
		//차량사용구분
		String szYD_CAR_USE_GP     = null;
		//운송장비코드
		String szTRN_EQP_CD        = null;
		//개소코드
		String szWLOC_CD           = null;
		//운송작업영공구분코드
		String szSP_TRUCK_LOADING_LOC_TP = null;
		//포인트요구일시
		String szPNT_DMD_DT        = null;
		//발지개소코드
		String szSPOS_WLOC_CD      = null;
		//발지포인트코드
		String szSPOS_YD_PNT_CD    = null;
//		//재료품목
//		String szYD_MTL_ITEM       = null;
//		//목표행선구분
//		String szYD_AIM_RT_GP      = null;
//		//목표야드구분
//		String szYD_AIM_YD_GP      = null;
//		//목표동구분
//		String szYD_AIM_BAY_GP     = null;
		//스케줄코드
		String szYD_SCH_CD         = null;
		//차량이송재료매수
		int intYD_CARLD_SH         = 0;
		//차량작업허용매수
		int intYD_WRK_ALW_SH       = 0;
		//대상 재료 중량 합계
		long lngSumMtlWt           = 0;
		//차량작업허용중량
		long lngYD_WRK_ALW_WT      = 0;
		//자동LOT편성 유무[직상차값을 사용]  - 2009.05.11 임춘수 추가
		String szYD_DIRECT_CARLD_GP		= "";
		String szYD_AUTO_LOT = null;
		//차량스케줄 생성유무  - 2009.05.11 임춘수 추가
		String szYD_CAR_SCH_YN		= "";
		//하위 모듈을 EJB CALL or JMS CALL 결정하는 변수 정의
		String szIS_EJB_CALL = null;
		String szRtnMsg = null;
		//차량이송 준비스케줄ID - 2009.10.02 임춘수 추가
		String szYD_PREP_SCH_ID		= "";
		//크레인설비ID
		String szYD_CRN_EQP_ID		= "";

		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if(szRcvTcCode == null || szRcvTcCode.equals("")){
		
			szMsg = "["+szOperationName+"] TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_TC_ERROR;
		
		}
		//TC CODE DISPLAY
		if(bDebugFlag){
		
			szMsg="["+szOperationName+"] 전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			szMsg="["+szOperationName+"] 메소드 시작 - 전문내용확인";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			ydUtils.displayRecord(szOperationName, msgRecord);
			
			//받은 전문 편집
			//설비ID
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if(szYD_EQP_ID.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
//			//상차정지위치
//			szYD_CARLD_STOP_LOC = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CARLD_STOP_LOC");
//			if(szYD_CARLD_STOP_LOC.equals("")){
//				
//				szMsg = "[전문 이상] 상차정지위치가 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return ;
//				
//			}
			//차량사용구분
			szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_USE_GP");
			szYD_CAR_USE_GP = "L";
			if(szYD_CAR_USE_GP.equals("") || !szYD_CAR_USE_GP.equals("L")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 차량사용구분(" + szYD_CAR_USE_GP + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return ;
				
			}
			//운송장비코드
			szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(msgRecord, "TRN_EQP_CD");
			if(szTRN_EQP_CD.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 운송장비코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return ;
				
			}
			//개소코드
			szWLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord, "WLOC_CD");
			if(szWLOC_CD.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 개소코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return ;
				
			}
			//운송작업영공구분코드
			szSP_TRUCK_LOADING_LOC_TP = ydDaoUtils.paraRecChkNull(msgRecord, "SP_TRUCK_LOADING_LOC_TP");
			if(szSP_TRUCK_LOADING_LOC_TP.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 운송작업영공구분코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return ;
				
			}
			//포인트요구일시
			szPNT_DMD_DT = ydDaoUtils.paraRecChkNull(msgRecord, "PNT_DMD_DT");
			if(szPNT_DMD_DT.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 포인트요구일시가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return ;
				
			}
			//발지개소코드
			szSPOS_WLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_WLOC_CD");
			if(szSPOS_WLOC_CD.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 발지개소코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return ;
				
			}
			//발지포인트코드
			szSPOS_YD_PNT_CD = ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_YD_PNT_CD");
			if(szSPOS_YD_PNT_CD.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 발지포인트코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return ;
				
			}

			
			//자동LOT편성 유무[화면에서 공차배차 시에 상차LOT편성을 할 지 판단]  - 2009.05.11 임춘수 추가
			szYD_DIRECT_CARLD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_DIRECT_CARLD_GP");
			szYD_AUTO_LOT = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AUTO_LOT");
			
			szMsg="["+szOperationName+"] 자동LOT편성구분(YD_AUTO_LOT) = " + szYD_AUTO_LOT;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			if( !szYD_AUTO_LOT.equals("Y")) {
				//szYD_DIRECT_CARLD_GP = "C";
				szYD_DIRECT_CARLD_GP = "B";
			}else{
				szYD_DIRECT_CARLD_GP = "Y";
			}
			
			//차량스케줄 생성유무[화면에서 상차LOT편성만 요구하는 지를 판단해서 차량 스케줄을 호출 판단]  - 2009.05.11 임춘수 추가
			szYD_CAR_SCH_YN		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_SCH_YN");
			
			//연계모듈을 EJB Call[Y값으로 설정] or JMS Call[값이 없거나 N값으로 설정] 호출 유무 변수  - 지금은 화면으로부터 전송(임춘수 추가 2009.07.13)
			szIS_EJB_CALL = ydDaoUtils.paraRecChkNull(msgRecord, "IS_EJB_CALL");
			
			szMsg="["+szOperationName+"] szIS_EJB_CALL = " + szIS_EJB_CALL + ", YD_DIRECT_CARLD_GP[YD_AUTO_LOT-수동LOT편성(C), 자동LOT편성(Y)] = " + szYD_DIRECT_CARLD_GP;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			if( szYD_AUTO_LOT.equals("Y")) {
				//상차 Lot 편성 대상 재료 Select
				intRtnVal = chkGetSUnMtlFtmvCarLoadLotGp(msgRecord, rsResult);			//버전1
				//intRtnVal = chkGetSUnMtlFtmvCarLoadLotGp1(msgRecord, rsResult);				//버전2
				if( intRtnVal < 0 ){
					return YdConstant.RETN_CD_FAILURE;
				}else if( intRtnVal == 0 ) {
					szYD_DIRECT_CARLD_GP = "B";			
				}else{
					rsResult.first();
					recOutPara = rsResult.getRecord();
					//크레인설비ID
					szYD_CRN_EQP_ID = ydDaoUtils.paraRecChkNull(recOutPara, "YD_CRN_EQP_ID");
				}
			}
			
			//레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			//JMS TC CODE
			
			recOutPara.setField("JMS_TC_CD",     "YDYDJ244");
			//레코드 추출
			//rsResult.first();
			//recPara = rsResult.getRecord();
			
			//리턴 recordSet 생성
			rsSpecResult = JDTORecordFactory.getInstance().createRecordSet("");
			//적치열구분
			//szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");
			//=================================================================================
			//수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			//BRE 등록 안됨...테스트용 스케줄코드 생성
			//추후 구현..
			//szYD_SCH_CD  = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP").trim().substring(0, 2) + "PT01UM";
//			if(szYD_STK_COL_GP.substring(0,2).trim().equals("SA")){
//		    	if(szYD_STK_COL_GP.substring(0,2).equals("SA")){
//		    		int intColNo = Integer.parseInt(szYD_STK_COL_GP.substring(4, 6));
//		    		if(intColNo <= 12) szYD_SCH_CD = "SAPT01UM";
//		    		else szYD_SCH_CD = "SAPT02UM";
//		    	}
//			}
//			szYD_SCH_CD  = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP").trim().substring(0, 2) +
//			               ydDaoUtils.paraRecChkNull(recPara, "YD_MTL_ITEM").trim() + "PTUM";
			//=================================================================================
			if( szYD_DIRECT_CARLD_GP.equals("Y") ){
				//차량사양 Select
				blnRtnVal = chkGetCarSpec(szYD_EQP_ID, szYD_CAR_USE_GP, szTRN_EQP_CD, rsSpecResult);
				if(!blnRtnVal){
					return YdConstant.RETN_CD_FAILURE;
				}
	
				//레코드 추출
				rsSpecResult.first();
				recSpecPara = rsSpecResult.getRecord();

				//차량작업허용매수
				//intYD_WRK_ALW_SH = ydDaoUtils.paraRecChkNullInt(recSpecPara, "YD_WRK_ALW_SH");
				
				//이송 재료 매수가 차량 이송 가능 매수보다 작으면 이송 재료 매수를 차량 이송 가능 매수에 대입한다.
				//if(intYD_WRK_ALW_SH > rsResult.size()) intYD_WRK_ALW_SH = rsResult.size();
				
				intYD_WRK_ALW_SH = rsResult.size();
				
				//차량작업허용중량
				lngYD_WRK_ALW_WT = ydDaoUtils.paraRecChkNullLong(recSpecPara, "YD_WRK_ALW_WT");
				
				
				rsResult.first();
				recPara = rsResult.getRecord();
				//차량이송 준비스케줄ID - 2009.10.02 임춘수 추가
				szYD_PREP_SCH_ID  = ydDaoUtils.paraRecChkNull(recPara, "YD_PREP_SCH_ID");
				
				szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");
				
				szYD_SCH_CD  = szYD_STK_COL_GP.substring(0, 2) + "PT01UM";
				//목표행선구분
				recOutPara.setField("YD_AIM_RT_GP", ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_RT_GP"));
				
				//차량 이송 가능 매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
				for (int Loop_i = 1; Loop_i <= intYD_WRK_ALW_SH; Loop_i++){
					rsResult.absolute(Loop_i);
					recPara = rsResult.getRecord();
					//대상 재료 중량 합계
					lngSumMtlWt = lngSumMtlWt + Long.parseLong(ydDaoUtils.paraRecChkNull(recPara, "YD_MTL_WT"));
					//차량작업허용중량보다 대상재료들의 중량이 많으면 편성 중지
					if(lngYD_WRK_ALW_WT < lngSumMtlWt) break;
					//재료번호
					recOutPara.setField("STL_NO" + Loop_i, ydDaoUtils.paraRecChkNull(recPara, "STL_NO"));
					//권상모음순서
					recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, "" + Loop_i);
					//차량이송재료매수
					intYD_CARLD_SH = Loop_i;
					
					//다음 레코드 추출
					//rsResult.next();
					
					
				}
			}else{
				szMsg = "["+szOperationName+"] 통합야드 소재이송 상차 LOT 편성을 하지 않고 차량스케줄 호출!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				recOutPara.setField("STL_NO1", "DUMMY");
				recOutPara.setField("YD_UP_COLL_SEQ1", "1");
				recOutPara.setField("YD_AIM_RT_GP", "@@");
				//2009.05.11 임춘수 추가 - LOT편성 호출 시 화면에서 공차배차버튼을 통해서 호출되는 상태값을 정의
				//아래의 항목이 없는 경우는 구내운송을 통해서 호출될 때는 값이 존재하지 않음
				//야드의 차량진행관리화면으로부터 호출되는 경우 값을 설정함
				//자동LOT편성 - Y, 수동LOT편성(사용자가 화면에서 LOT편성업무처리) - N
				
				recOutPara.setField("YD_FTMV_COL", "SAPT01");
				intYD_CARLD_SH = 1;
				szYD_SCH_CD = "SAPT01UM";
			}

			//스케줄코드
			recOutPara.setField("YD_SCH_CD",     szYD_SCH_CD);
			//상차 LOT 재료매수
			recOutPara.setField("YD_CARLD_SH",   "" + intYD_CARLD_SH);
			//설비ID
			recOutPara.setField("YD_EQP_ID",     szYD_EQP_ID);
			//차량사용구분
			recOutPara.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
			//운송장비코드
			recOutPara.setField("TRN_EQP_CD",    szTRN_EQP_CD);
			//개소코드
			recOutPara.setField("WLOC_CD",       szWLOC_CD);
			//운송작업영공구분코드
			recOutPara.setField("SP_TRUCK_LOADING_LOC_TP", szSP_TRUCK_LOADING_LOC_TP);
			//포인트요구일시
			recOutPara.setField("PNT_DMD_DT",    szPNT_DMD_DT);
			//발지개소코드
			recOutPara.setField("SPOS_WLOC_CD",  szSPOS_WLOC_CD);
			//발지포인트코드
			recOutPara.setField("SPOS_YD_PNT_CD", szSPOS_YD_PNT_CD);
			//차량스케줄 생성유무  - 2009.05.11 임춘수 추가
			recOutPara.setField("YD_CAR_SCH_YN", szYD_CAR_SCH_YN);
			
			recOutPara.setField("YD_DIRECT_CARLD_GP", szYD_DIRECT_CARLD_GP);
			//차량이송 준비스케줄ID - 2009.10.02 임춘수 추가
			recOutPara.setField("YD_PREP_SCH_ID", szYD_PREP_SCH_ID);
			//크레인설비ID - 2009.10.14 임춘수 추가
			recOutPara.setField("YD_CRN_EQP_ID", szYD_CRN_EQP_ID);
			
			szMsg = "["+szOperationName+"] 소재이송 상차 LOT 편성 후 전문 보기";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			ydUtils.displayRecord(szOperationName, recOutPara);
			
			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	    	 * EJB Call or JMS Call 유무를 판단하여 호출하는 기능
	    	 * 수정자 : 임춘수
	    	 * 일자 : 2009.07.13
	    	 ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	    	if( szIS_EJB_CALL.equals("Y")){
	    		//EJB Call ==> 메소드 콜
	    		szMsg = "["+szOperationName+"] 소재이송 상차 LOT 편성 후 차량상차 작업요구 송신 - 메소드[procCCsCarLdWrkReq] 콜 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				recOutPara.setField("IS_EJB_CALL", szIS_EJB_CALL);
				
	    		szRtnMsg = procCCsCarLdWrkReq(recOutPara);
				
				szMsg = "["+szOperationName+"] 소재이송 상차 LOT 편성 후 차량상차 작업요구 송신 - 메소드[procCCsCarLdWrkReq] 콜 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	}else{
				//전문 송신
				ydDelegate.sendMsg(recOutPara);
				szMsg = "["+szOperationName+"] 소재이송 상차 LOT 편성 후 차량상차 작업요구 송신[procCCsCarLdWrkReq] - 전문 송신 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	}
			
			szMsg = "["+szOperationName+"] 소재이송 상차 LOT 편성 후 차량상차 작업요구 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch(Exception e){
			szMsg = "["+szOperationName+"]  소재이송 상차 LOT 편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;
		}
		return YdConstant.RETN_CD_SUCCESS;
	} //end of procSUnMatlFtmvCarLdLotComp
	
	
	/**
	 * 오퍼레이션명 : 통합야드소재이송상차LOT편성 - 크레인별
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return String
	 * @throws JDTOException
	 */
		
	public String procSUnMatlFtmvCarLdLotCompCrn(JDTORecord msgRecord) throws JDTOException  {
		YdDelegate ydDelegate = new YdDelegate();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		//공용 METHOD
		YdUtils ydutils       = new YdUtils();
		ymCommonDAO dao = ymCommonDAO.getInstance();
		//레코드 선언
		JDTORecord recPara     = null;
		JDTORecord recOutPara  = null;
		JDTORecord recSpecPara  = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		JDTORecordSet rsSpecResult = null;
		
		//리턴값(boolean)
		boolean blnRtnVal      = false;
		//리턴값(int)
		int intRtnVal          = 0;
		//메세지
		String szMsg           = "";
		//메소드명
		String szMethodName    = "procSUnMatlFtmvCarLdLotCompCrn";
		//오퍼레이션명
		String szOperationName		= "통합야드소재이송상차LOT편성(크레인별)";

		//설비ID
		String szYD_EQP_ID         = null;
		//적치열구분
		String szYD_STK_COL_GP     = null;
//		//상차정지위치
//		String szYD_CARLD_STOP_LOC = null;
		//차량사용구분
		String szYD_CAR_USE_GP     = null;
		//운송장비코드
		String szTRN_EQP_CD        = null;
		//개소코드
		String szWLOC_CD           = null;
		//운송작업영공구분코드
		String szSP_TRUCK_LOADING_LOC_TP = null;
		//포인트요구일시
		String szPNT_DMD_DT        = null;
		//발지개소코드
		String szSPOS_WLOC_CD      = null;
		//발지포인트코드
		String szSPOS_YD_PNT_CD    = null;
//		//재료품목
//		String szYD_MTL_ITEM       = null;
//		//목표행선구분
//		String szYD_AIM_RT_GP      = null;
//		//목표야드구분
//		String szYD_AIM_YD_GP      = null;
//		//목표동구분
//		String szYD_AIM_BAY_GP     = null;
		//스케줄코드
		String szYD_SCH_CD         = null;
		//차량이송재료매수
		int intYD_CARLD_SH         = 0;
		//차량작업허용매수
		int intYD_WRK_ALW_SH       = 0;
		//대상 재료 중량 합계
		long lngSumMtlWt           = 0;
		//차량작업허용중량
		long lngYD_WRK_ALW_WT      = 0;
		//자동LOT편성 유무[직상차값을 사용]  - 2009.05.11 임춘수 추가
		String szYD_DIRECT_CARLD_GP		= "";
		String szYD_AUTO_LOT = null;
		//차량스케줄 생성유무  - 2009.05.11 임춘수 추가
		String szYD_CAR_SCH_YN		= "";
		//하위 모듈을 EJB CALL or JMS CALL 결정하는 변수 정의
		String szIS_EJB_CALL = null;
		String szRtnMsg = null;
		//차량이송 준비스케줄ID - 2009.10.02 임춘수 추가
		String szYD_PREP_SCH_ID		= "";
		//크레인설비ID
		String szYD_CRN_EQP_ID		= "";
		//상차출발[1]/도착[2]시 LOT편성 결정 변수
		String szYD_CARLD_LEV_ARR_LOT	= null;
		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if(szRcvTcCode == null || szRcvTcCode.equals("")){
		
			szMsg = "["+szOperationName+"] TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_TC_ERROR;
		
		}
		//TC CODE DISPLAY
		if(bDebugFlag){
		
			szMsg="["+szOperationName+"] 전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			szMsg="["+szOperationName+"] 메소드 시작 - 전문내용확인";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			ydUtils.displayRecord(szOperationName, msgRecord);
			
			//받은 전문 편집
			//설비ID
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if(szYD_EQP_ID.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
//			//상차정지위치
//			szYD_CARLD_STOP_LOC = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CARLD_STOP_LOC");
//			if(szYD_CARLD_STOP_LOC.equals("")){
//				
//				szMsg = "[전문 이상] 상차정지위치가 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return ;
//				
//			}
			//차량사용구분
			szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_USE_GP");
			szYD_CAR_USE_GP = "L";
			if(szYD_CAR_USE_GP.equals("") || !szYD_CAR_USE_GP.equals("L")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 차량사용구분(" + szYD_CAR_USE_GP + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return ;
				
			}
			//운송장비코드
			szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(msgRecord, "TRN_EQP_CD");
			if(szTRN_EQP_CD.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 운송장비코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return ;
				
			}
			//개소코드
			szWLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord, "WLOC_CD");
			if(szWLOC_CD.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 개소코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return ;
				
			}
			//운송작업영공구분코드
			szSP_TRUCK_LOADING_LOC_TP = ydDaoUtils.paraRecChkNull(msgRecord, "SP_TRUCK_LOADING_LOC_TP");
			if(szSP_TRUCK_LOADING_LOC_TP.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 운송작업영공구분코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return ;
				
			}
			//포인트요구일시
			szPNT_DMD_DT = ydDaoUtils.paraRecChkNull(msgRecord, "PNT_DMD_DT");
			if(szPNT_DMD_DT.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 포인트요구일시가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return ;
				
			}
			//발지개소코드
			szSPOS_WLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_WLOC_CD");
			if(szSPOS_WLOC_CD.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 발지개소코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return ;
				
			}
			//발지포인트코드
			szSPOS_YD_PNT_CD = ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_YD_PNT_CD");
			if(szSPOS_YD_PNT_CD.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 발지포인트코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return ;
				
			}

			
			//자동LOT편성 유무[화면에서 공차배차 시에 상차LOT편성을 할 지 판단]  - 2009.05.11 임춘수 추가
			szYD_DIRECT_CARLD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_DIRECT_CARLD_GP");
			//szYD_AUTO_LOT = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AUTO_LOT");
			
			/*
			 * 차량출발/도착 시 LOT편성 유무, 자동LOT편성 유무 판단값을 RULE로부터 조회
			 * 수정자 : 임춘수
			 * 수정일 : 2009.11.02
			 */
			recOutPara = JDTORecordFactory.getInstance().create();
			YdCommonUtils.getCarLdLotRuleFromBRE(YdConstant.YD_GP_INTGR_YARD, recOutPara);
			
			szYD_AUTO_LOT = ydDaoUtils.paraRecChkNull(recOutPara, "YD_AUTO_LOT");
			//szYD_CARLD_LEV_ARR_LOT = ydDaoUtils.paraRecChkNull(recOutPara, "YD_CARLD_LEV_ARR_LOT");
			
			szMsg="["+szOperationName+"] 자동LOT편성구분(YD_AUTO_LOT) = " + szYD_AUTO_LOT;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			if( !szYD_AUTO_LOT.equals("Y")) {
				//szYD_DIRECT_CARLD_GP = "C";
				szYD_DIRECT_CARLD_GP = "B";
			}else{
				szYD_DIRECT_CARLD_GP = "Y";
			}
			
//			if( szYD_CARLD_LEV_ARR_LOT.equals(YdConstant.YD_CARLD_LEV) && szYD_AUTO_LOT.equals("Y")) {
//				szYD_DIRECT_CARLD_GP = "Y";
//				szMsg="["+szOperationName+"] 야드에서 대상재 조회 처리";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//			}else{
//				szYD_DIRECT_CARLD_GP = "B";
//				szMsg="["+szOperationName+"] 야드에서 대상재 조회하지 않고 공차 출발 처리";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//			}
			
			//차량스케줄 생성유무[화면에서 상차LOT편성만 요구하는 지를 판단해서 차량 스케줄을 호출 판단]  - 2009.05.11 임춘수 추가
			szYD_CAR_SCH_YN		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_SCH_YN");
			
			//연계모듈을 EJB Call[Y값으로 설정] or JMS Call[값이 없거나 N값으로 설정] 호출 유무 변수  - 지금은 화면으로부터 전송(임춘수 추가 2009.07.13)
			szIS_EJB_CALL = ydDaoUtils.paraRecChkNull(msgRecord, "IS_EJB_CALL");
			
			szMsg="["+szOperationName+"] szIS_EJB_CALL = " + szIS_EJB_CALL + ", YD_DIRECT_CARLD_GP[YD_AUTO_LOT-수동LOT편성(C), 자동LOT편성(Y)] = " + szYD_DIRECT_CARLD_GP;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			if( szYD_AUTO_LOT.equals("Y")) {
			//if( szYD_DIRECT_CARLD_GP.equals("Y") ) {
				//상차 Lot 편성 대상 재료 Select
				//intRtnVal = chkGetSUnMtlFtmvCarLoadLotGp(msgRecord, rsResult);			//버전1
				intRtnVal = chkGetSUnMtlFtmvCarLoadLotGpByCrn(msgRecord, rsResult);				//버전2
				if( intRtnVal < 0 ){
					return YdConstant.RETN_CD_FAILURE;
				}else if( intRtnVal == 0 ) {
					szYD_DIRECT_CARLD_GP = "B";			
				}else{
					rsResult.first();
					recOutPara = rsResult.getRecord();
					//크레인설비ID
					szYD_CRN_EQP_ID = ydDaoUtils.paraRecChkNull(recOutPara, "YD_CRN_EQP_ID");
				}
			}
			
			//레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			//JMS TC CODE
			
			//recOutPara.setField("JMS_TC_CD",     "YDYDJ244");
			recOutPara.setField("JMS_TC_CD",     "YDYDJ296");
			
			//레코드 추출
			//rsResult.first();
			//recPara = rsResult.getRecord();
			
			//리턴 recordSet 생성
			rsSpecResult = JDTORecordFactory.getInstance().createRecordSet("");
			//적치열구분
			//szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");
			//=================================================================================
			//수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			//BRE 등록 안됨...테스트용 스케줄코드 생성
			//추후 구현..
			//szYD_SCH_CD  = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP").trim().substring(0, 2) + "PT01UM";
//			if(szYD_STK_COL_GP.substring(0,2).trim().equals("SA")){
//		    	if(szYD_STK_COL_GP.substring(0,2).equals("SA")){
//		    		int intColNo = Integer.parseInt(szYD_STK_COL_GP.substring(4, 6));
//		    		if(intColNo <= 12) szYD_SCH_CD = "SAPT01UM";
//		    		else szYD_SCH_CD = "SAPT02UM";
//		    	}
//			}
//			szYD_SCH_CD  = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP").trim().substring(0, 2) +
//			               ydDaoUtils.paraRecChkNull(recPara, "YD_MTL_ITEM").trim() + "PTUM";
			//=================================================================================
			if( szYD_DIRECT_CARLD_GP.equals("Y") ){
				//차량사양 Select
				blnRtnVal = chkGetCarSpec(szYD_EQP_ID, szYD_CAR_USE_GP, szTRN_EQP_CD, rsSpecResult);
				if(!blnRtnVal){
					return YdConstant.RETN_CD_FAILURE;
				}
	
				//레코드 추출
				rsSpecResult.first();
				recSpecPara = rsSpecResult.getRecord();

				//차량작업허용매수
				//intYD_WRK_ALW_SH = ydDaoUtils.paraRecChkNullInt(recSpecPara, "YD_WRK_ALW_SH");
				
				//이송 재료 매수가 차량 이송 가능 매수보다 작으면 이송 재료 매수를 차량 이송 가능 매수에 대입한다.
				//if(intYD_WRK_ALW_SH > rsResult.size()) intYD_WRK_ALW_SH = rsResult.size();
				
				intYD_WRK_ALW_SH = rsResult.size();
				
				//차량작업허용중량
				lngYD_WRK_ALW_WT = ydDaoUtils.paraRecChkNullLong(recSpecPara, "YD_WRK_ALW_WT");
				
				
				rsResult.first();
				recPara = rsResult.getRecord();
				//차량이송 준비스케줄ID - 2009.10.02 임춘수 추가
				szYD_PREP_SCH_ID  = ydDaoUtils.paraRecChkNull(recPara, "YD_PREP_SCH_ID");
				
				szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");
				
				//szYD_SCH_CD  = szYD_STK_COL_GP.substring(0, 2) + "PT01UM";
				szYD_SCH_CD  = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_CD");
				//목표행선구분
				recOutPara.setField("YD_AIM_RT_GP", ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_RT_GP"));
				
				//차량 이송 가능 매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
				for (int Loop_i = 1; Loop_i <= intYD_WRK_ALW_SH; Loop_i++){
					rsResult.absolute(Loop_i);
					recPara = rsResult.getRecord();
					//대상 재료 중량 합계
					lngSumMtlWt = lngSumMtlWt + Long.parseLong(ydDaoUtils.paraRecChkNull(recPara, "YD_MTL_WT"));
					//차량작업허용중량보다 대상재료들의 중량이 많으면 편성 중지
					if(lngYD_WRK_ALW_WT < lngSumMtlWt) break;
					//재료번호
					recOutPara.setField("STL_NO" + Loop_i, ydDaoUtils.paraRecChkNull(recPara, "STL_NO"));
					//권상모음순서
					recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, "" + Loop_i);
					//차량이송재료매수
					intYD_CARLD_SH = Loop_i;
					
					//다음 레코드 추출
					//rsResult.next();
					
					
				}
			}else{
				szMsg = "["+szOperationName+"] 소재이송 상차 LOT 편성을 하지 않고 차량스케줄 호출!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				recOutPara.setField("STL_NO1", "DUMMY");
				recOutPara.setField("YD_UP_COLL_SEQ1", "1");
				recOutPara.setField("YD_AIM_RT_GP", "@@");
				//2009.05.11 임춘수 추가 - LOT편성 호출 시 화면에서 공차배차버튼을 통해서 호출되는 상태값을 정의
				//아래의 항목이 없는 경우는 구내운송을 통해서 호출될 때는 값이 존재하지 않음
				//야드의 차량진행관리화면으로부터 호출되는 경우 값을 설정함
				//자동LOT편성 - Y, 수동LOT편성(사용자가 화면에서 LOT편성업무처리) - N
				
				recOutPara.setField("YD_FTMV_COL", "SAPT01");
				intYD_CARLD_SH = 1;
				szYD_SCH_CD = "SAPT01UM";
			}

			//스케줄코드
			recOutPara.setField("YD_SCH_CD",     szYD_SCH_CD);
			//상차 LOT 재료매수
			recOutPara.setField("YD_CARLD_SH",   "" + intYD_CARLD_SH);
			//설비ID
			recOutPara.setField("YD_EQP_ID",     szYD_EQP_ID);
			//차량사용구분
			recOutPara.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
			//운송장비코드
			recOutPara.setField("TRN_EQP_CD",    szTRN_EQP_CD);
			//개소코드
			recOutPara.setField("WLOC_CD",       szWLOC_CD);
			//운송작업영공구분코드
			recOutPara.setField("SP_TRUCK_LOADING_LOC_TP", szSP_TRUCK_LOADING_LOC_TP);
			//포인트요구일시
			recOutPara.setField("PNT_DMD_DT",    szPNT_DMD_DT);
			//발지개소코드
			recOutPara.setField("SPOS_WLOC_CD",  szSPOS_WLOC_CD);
			//발지포인트코드
			recOutPara.setField("SPOS_YD_PNT_CD", szSPOS_YD_PNT_CD);
			//차량스케줄 생성유무  - 2009.05.11 임춘수 추가
			recOutPara.setField("YD_CAR_SCH_YN", szYD_CAR_SCH_YN);
			
			recOutPara.setField("YD_DIRECT_CARLD_GP", szYD_DIRECT_CARLD_GP);
			//차량이송 준비스케줄ID - 2009.10.02 임춘수 추가
			recOutPara.setField("YD_PREP_SCH_ID", szYD_PREP_SCH_ID);
			//크레인설비ID - 2009.10.14 임춘수 추가
			recOutPara.setField("YD_CRN_EQP_ID", szYD_CRN_EQP_ID);
			
			szMsg = "["+szOperationName+"] 소재이송 상차 LOT 편성 후 전문 보기";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			ydUtils.displayRecord(szOperationName, recOutPara);
			
			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	    	 * EJB Call or JMS Call 유무를 판단하여 호출하는 기능
	    	 * 수정자 : 임춘수
	    	 * 일자 : 2009.07.13
	    	 ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			//장애 발생시 이전 소스로 원복 하기 위한 조치
			String QueryId 	= "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.chklist";
		    List sposYNChklist = dao.getCommonList(QueryId, new Object[]{});

		    JDTORecord unloadPointrec = (JDTORecord)sposYNChklist.get(0);
	    	String CHK   = StringHelper.evl(unloadPointrec.getFieldString("CHK"), "");
	    	if(CHK.equals("Y")){
	    		szRtnMsg = procSlabTotCarLdWrkReqS(recOutPara);
	    	}else{	    	
			
		    	if( szIS_EJB_CALL.equals("Y")){
		    		//EJB Call ==> 메소드 콜
		    		szMsg = "["+szOperationName+"] 소재이송 상차 LOT 편성 후 차량상차 작업요구 송신 - 메소드[procSlabTotCarLdWrkReq] 콜 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					recOutPara.setField("IS_EJB_CALL", szIS_EJB_CALL);
					
		    		szRtnMsg = procSlabTotCarLdWrkReq(recOutPara);
		    		
					szMsg = "["+szOperationName+"] 소재이송 상차 LOT 편성 후 차량상차 작업요구 송신 - 메소드[procSlabTotCarLdWrkReq] 콜 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    	}else{
					//전문 송신
					ydDelegate.sendMsg(recOutPara);
					szMsg = "["+szOperationName+"] 소재이송 상차 LOT 편성 후 차량상차 작업요구 송신[procSlabTotCarLdWrkReq] - 전문 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    	}
	    	}
			
			szMsg = "["+szOperationName+"] 소재이송 상차 LOT 편성 후 차량상차 작업요구 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch(Exception e){
			szMsg = "["+szOperationName+"]  소재이송 상차 LOT 편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;
		}
		return YdConstant.RETN_CD_SUCCESS;
	} //end of procSUnMatlFtmvCarLdLotCompCrn
	
	/**
	 * 오퍼레이션명 : 통합야드차량상차작업요구
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return String
	 * @throws JDTOException
	 */
	public String procSlabTotCarLdWrkReq(JDTORecord msgRecord)throws JDTOException  {
		
		YdDelegate ydDelegate = new YdDelegate();
		//스케줄기준 DAO
		YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();
		//작업예약 재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils           = new YdDaoUtils();
		//공용 METHOD
		YdUtils ydutils                 = new YdUtils();
		
		//리턴값(boolean)
		boolean blnRtnVal      = false;
		//리턴값(int)
		int intRtnVal          = 0;
		//메세지
		String szMsg           = "";
		//METHOD명
		String szMethodName    = "procSlabTotCarLdWrkReq";
		String szOperationName	= "통합야드차량상차작업요구";
		//사용자
		String szUser          = "SYSTEM";
		
		//레코드 선언
		JDTORecord recPara     = null;
		JDTORecord recStkPara  = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		
		//설비ID
		String szYD_EQP_ID         = null;
		//목표행선구분
		String szYD_AIM_RT_GP      = null;
		//차량사용구분
		String szYD_CAR_USE_GP     = null;
		//운송장비코드
		String szTRN_EQP_CD        = null;
		//재료매수(int)
		int intMtlCnt              = 0;
		//재료번호
		String[] szSTL_NO          = null;
		//권상모음순서
		String[] szYD_UP_COLL_SEQ  = null;
		//스케줄코드
		String szYD_SCH_CD         = null;
		//스케줄우선순위
		String szYD_SCH_PRIOR		= "";
		//야드작업크레인우선순위
		String szYD_WRK_CRN_PRIOR  = null;
		//스케줄 금지 유무
		String szYD_SCH_PROH_EXN   = null;
		//작업크레인
		String szYD_WRK_CRN        = null;
		//대체크레인유무
		String szYD_ALT_CRN_YN     = null;
		//대체크레인
		String szYD_ALT_CRN        = null;
		//야드대체크레인우선순위
		String szYD_ALT_CRN_PRIOR  = null;
		//선택크레인
		String szCrn               = null;
		//작업예약ID
		String szYD_WBOOK_ID       = null;
		//개소코드
		String szWLOC_CD           = null;
		//운송작업영공구분코드
		String szSP_TRUCK_LOADING_LOC_TP = null;
		//포인트요구일시
		String szPNT_DMD_DT          = null;
		//발지개소코드
		String szSPOS_WLOC_CD      = null;
		//발지포인트코드
		String szSPOS_YD_PNT_CD    = null;
		
		//야드목표야드구분
		String szYD_AIM_YD_GP        = null;
		//야드목표동구분
		String szYD_AIM_BAY_GP       = null;
		
		//직상차구분
		String szYD_DIRECT_CARLD_GP = "";
		//이송적치열
		String szYD_FTMV_COL		= "";
		//차량스케줄 생성유무  - 2009.05.11 임춘수 추가
		String szYD_CAR_SCH_YN		= "";
		//EJB CALL or JMS CALL 판단 변수
		String szIS_EJB_CALL		= null;
		//차량이송준비스케줄ID
		String szYD_PREP_SCH_ID		= null;
		//크레인설비ID
		String szYD_CRN_EQP_ID		= null;
		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		
		//에러 리턴
		if(szRcvTcCode == null || szRcvTcCode.equals("")){
		
			szMsg = "["+szOperationName+"] TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_TC_ERROR;
		
		}
		//TC CODE DISPLAY
		if(bDebugFlag){
		
			szMsg = "["+szOperationName+"] 전문수신 : TCCODE=" + szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			szMsg = "["+szOperationName+"] 메소드 시작 - 전문내용확인";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			ydUtils.displayRecord(szOperationName, msgRecord);
			//받은 전문 편집
			//스케줄코드
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			if(szYD_SCH_CD.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			//설비ID - 차량설비ID
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if(szYD_EQP_ID.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			//목표행선구분
			szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_RT_GP");
//			if(szYD_AIM_RT_GP.equals("")){
//				
//				szMsg = "["+szOperationName+"] [전문 이상] 목표행선구분이 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return YdConstant.RETN_CD_NO_PARAM;
//			}
			//차량사용구분
			szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_USE_GP");
			if(szYD_CAR_USE_GP.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 차량사용구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			
			if(szYD_CAR_USE_GP.equals("L")){
				//운송장비코드
				szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(msgRecord, "TRN_EQP_CD");
				if(szTRN_EQP_CD.equals("")){
					
					szMsg = "["+szOperationName+"] [전문 이상] 운송장비코드가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;
				}
				
				//개소코드
				szWLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord, "WLOC_CD");
				if(szWLOC_CD.equals("")){
					
					szMsg = "["+szOperationName+"] [전문 이상] 개소코드가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;
					
				}
				
				//운송작업영공구분코드
				szSP_TRUCK_LOADING_LOC_TP = ydDaoUtils.paraRecChkNull(msgRecord, "SP_TRUCK_LOADING_LOC_TP");
				if(szSP_TRUCK_LOADING_LOC_TP.equals("")){
					
					szMsg = "["+szOperationName+"] [전문 이상] 운송작업영공구분코드가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;
					
				}
				
				//포인트요구일시
				szPNT_DMD_DT = ydDaoUtils.paraRecChkNull(msgRecord, "PNT_DMD_DT");
				if(szPNT_DMD_DT.equals("")){
					
					szMsg = "["+szOperationName+"] [전문 이상] 포인트요구일시가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;
					
				}
				
			}
			//차량이송 준비스케줄ID - 2009.10.02 임춘수 추가
			szYD_PREP_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_PREP_SCH_ID");
			//크레인설비ID - 2009.10.14 임춘수 추가
			szYD_CRN_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_EQP_ID");
			
			//발지개소코드
			szSPOS_WLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_WLOC_CD");
//			if(szSPOS_WLOC_CD.equals("")){
//				
//				szMsg = "[전문 이상] 발지개소코드가 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return YdConstant.RETN_CD_NO_PARAM;
//				
//			}
			
			//발지포인트코드
			szSPOS_YD_PNT_CD = ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_YD_PNT_CD");
//			if(szSPOS_YD_PNT_CD.equals("")){
//				
//				szMsg = "[전문 이상] 발지포인트코드가 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return YdConstant.RETN_CD_NO_PARAM;
//				
//			}
			
			//재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_CARLD_SH");
			if(intMtlCnt == 0){
				
				szMsg = "["+szOperationName+"] [전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}

			//재료번호
			szSTL_NO          = new String[intMtlCnt + 1];
			//권상모음순서
			szYD_UP_COLL_SEQ  = new String[intMtlCnt + 1];
			
			
			//재료번호, 권상모음순서
			for(int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
				
				//재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO" + Loop_i);
				if(szSTL_NO[Loop_i].equals("")){
					
					szMsg = "["+szOperationName+"] [전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;
					
				}
				//권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_COLL_SEQ" + Loop_i);
				if(szYD_UP_COLL_SEQ[Loop_i].equals("")){
					
					szMsg = "["+szOperationName+"] [전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i] + ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;
					
				}
			}
			
			//직상차구분 : C - 직상차, Y - 일반야드, B - 대상재 존재 않함
			szYD_DIRECT_CARLD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_DIRECT_CARLD_GP");
			//이송적치열
			szYD_FTMV_COL = ydDaoUtils.paraRecChkNull(msgRecord, "YD_FTMV_COL");
			//차량스케줄 생성유무  - 2009.05.11 임춘수 추가
			szYD_CAR_SCH_YN		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_SCH_YN");
			
			szMsg = "["+szOperationName+"] [전문 내용] 직상차구분[szYD_DIRECT_CARLD_GP - " + szYD_DIRECT_CARLD_GP + " ]" + ", 이송적치열 [" + szYD_FTMV_COL + "]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//연계모듈을 EJB Call[Y값으로 설정] or JMS Call[값이 없거나 N값으로 설정] 호출 유무 변수  - 지금은 화면으로부터 전송(임춘수 추가 2009.07.13)
			szIS_EJB_CALL = ydDaoUtils.paraRecChkNull(msgRecord, "IS_EJB_CALL");
			
			szMsg="["+szOperationName+"] szIS_EJB_CALL = " + szIS_EJB_CALL;

			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if(!blnRtnVal) return YdConstant.RETN_CD_FAILURE;
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//스케줄CD 체크
			//스케줄 금지 유무
			szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");
			//작업크레인
			szYD_WRK_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
			//작업크레인우선순위
			szYD_WRK_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN_PRIOR");
			//대체크레인유무
			szYD_ALT_CRN_YN   = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
			//대체크레인
			szYD_ALT_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
			//야드대체크레인우선순위
			szYD_ALT_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_PRIOR");
			
			//스케줄 금지 유무가  "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if(szYD_SCH_PROH_EXN.equals("Y")){
				
				szMsg = "["+szOperationName+"] 스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				/*
				 * 비록 스케줄금지 상태이더라도 작업과 차량스케줄은 등록 후 POINT전송
				 * 수정자 : 임춘수
				 * 수정일 : 2009.10.14
				 */
				//return YdConstant.RETN_CRN_SCH_PROH;
			}
			
			//작업크레인 설비 상태 체크
			blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);
			
			//작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if(!blnRtnVal){
				
				szMsg = "["+szOperationName+"] 작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
				//대체크레인의 유무를 체크한다.
				//대체크레인이 없으면 에러 리턴
				if(!szYD_ALT_CRN_YN.equals("Y")){
					
					szMsg = "["+szOperationName+"] 대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if(!blnRtnVal){
					
					szMsg = "["+szOperationName+"] 대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;
					
				} else {
					//대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					szCrn = szYD_ALT_CRN;
					szYD_SCH_PRIOR	= szYD_ALT_CRN_PRIOR;
				}
			} else {
				//작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				szCrn = szYD_WRK_CRN;
				szYD_SCH_PRIOR	= szYD_WRK_CRN_PRIOR;
			}
			/*
			* 대상재가 일반 야드에 적치되어 있는 경우에만 작업예약에 등록 - 임춘수 2009.04.23 추가
			 */
			if( !szYD_DIRECT_CARLD_GP.equals("C") && !szYD_DIRECT_CARLD_GP.equals("B")){

				//재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다.
				//작업예약재료 등록 여부를 체크한다.
				for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
					
					//크레인사양과 저장품 사양을 체크(길이,폭,중량)
					blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
					if(!blnRtnVal) return YdConstant.RETN_CD_FAILURE;
					
					//다른 작업예약에 재료가 등록되어있는지 체크한다.
					blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
					if(!blnRtnVal) return YdConstant.RETN_CD_FAILURE;
					
				}	
				
				//리턴 recordSet 생성
				rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
				
				//작업예약ID 생성
				blnRtnVal = getYdWbookId(rsResult);
				if(!blnRtnVal) return YdConstant.RETN_CD_FAILURE;
				//레코드추출
				rsResult.first();
				recPara = rsResult.getRecord();
				//작업예약ID
				szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");
				
				//리턴 recordSet 생성
				rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
				//저장품테이블 조회
				blnRtnVal = this.chkGetStock(szSTL_NO[1], rsResult);
				if(!blnRtnVal) return YdConstant.RETN_CD_FAILURE;
				//레코드추출
				rsResult.first();
				recPara = rsResult.getRecord();
				
				//야드목표야드구분
				szYD_AIM_YD_GP  = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_YD_GP");
				//야드목표동구분
				szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_BAY_GP");
				
				szMsg = "["+szOperationName+"] 재료번호["+szSTL_NO[1]+"]의 목표야드["+szYD_AIM_YD_GP+"], 목표동["+szYD_AIM_BAY_GP+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				if( !szYD_PREP_SCH_ID.equals("") ) {
					/*
					 * 준비스케줄ID가 존재하는 경우는 이송LOT편성된 정보에서 목표야드와 목표동 구분을 조회해서 사용
					 * 수정자 : 임춘수
					 * 수정일 : 2009.10.26
					 */
					rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_PREP_SCH_ID",   szYD_PREP_SCH_ID);
					YdPrepSchDao ydPrepSchDao = new YdPrepSchDao();
					intRtnVal = ydPrepSchDao.getYdPrepsch(recPara, rsResult, 0);
					if( intRtnVal <= 0) {
						szMsg = "["+szOperationName+"] 준비스케줄ID["+szYD_PREP_SCH_ID+"]로 조회 시 오류발생 - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}else{
						rsResult.first();
						recPara = rsResult.getRecord();
						
						//야드목표야드구분
						szYD_AIM_YD_GP  = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_YD_GP");
						//야드목표동구분
						szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_BAY_GP");
						
						szMsg = "["+szOperationName+"] 준비스케줄ID["+szYD_PREP_SCH_ID+"]로 조회 시 데이타가 존재합니다. - 목표야드["+szYD_AIM_YD_GP+"], 목표동["+szYD_AIM_BAY_GP+"]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
					}
				}
				
				//INSERT 항목 record 생성
				recPara = JDTORecordFactory.getInstance().create();
				//야드구분
				String szYD_GP       = szYD_SCH_CD.substring(0, 1);
				//동구분
				String szYD_BAY_GP   = szYD_SCH_CD.substring(1, 2);
				
				//INSERT할 항목 SET
				recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
				recPara.setField("YD_GP", 		  szYD_GP);
				recPara.setField("YD_BAY_GP", 	  szYD_BAY_GP);
				recPara.setField("YD_SCH_PRIOR", 	szYD_SCH_PRIOR);
				recPara.setField("YD_SCH_CD", 	  szYD_SCH_CD);
				recPara.setField("REGISTER", 	  szUser);
				recPara.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
				recPara.setField("TRN_EQP_CD", 	  szTRN_EQP_CD);
				recPara.setField("YD_AIM_YD_GP",  szYD_AIM_YD_GP);
				recPara.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP);
		
				//작업예약 INSERT
				intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
				if(intRtnVal < 1){
					szMsg = "["+szOperationName+"] 작업예약 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;
				}
				
				
				//조회항목 record 생성
				recPara = JDTORecordFactory.getInstance().create();
				
				recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
	//			recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
	//			recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
				recPara.setField("REGISTER", 	  szUser);
	
				for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
					
					//리턴 recordSet 생성
					rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
					//재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
					intRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C", rsResult);
					if(intRtnVal != 1) {
						if( intRtnVal == 0 ) {
							intRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "U", rsResult);
							if(intRtnVal != 1) {
								szMsg="["+szOperationName+"] 적치단에 재료["+szSTL_NO[Loop_i]+"]가 존재하지 않습니다.";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
								throw new DAOException(szMsg);
							}
						}else{
							szMsg="["+szOperationName+"] 적치단에 재료["+szSTL_NO[Loop_i]+"] 조회 시 오류발생";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							throw new DAOException(szMsg);
						}
						//return YdConstant.RETN_CD_FAILURE;
					}
					
					//레코드추출
					rsResult.first();
					recStkPara = rsResult.getRecord();
					
					//재료번호
					recPara.setField("STL_NO", 		   szSTL_NO[Loop_i]);
					//적치열구분
					recPara.setField("YD_STK_COL_GP",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_COL_GP"));
					//적치BED번호
					recPara.setField("YD_STK_BED_NO",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_BED_NO"));
					//적치단번호
					recPara.setField("YD_STK_LYR_NO",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_LYR_NO"));
					//권상모음순서
					recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ[Loop_i]);
					
					// 작업예약재료 테이블에 등록한다.
					intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
					
					if(intRtnVal < 1){
						szMsg = "["+szOperationName+"] 작업예약재료 데이터 등록 중 에러";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						throw new DAOException(szMsg);
						//return YdConstant.RETN_CD_FAILURE;
					}
				}
				
			} //대상재가 일반 야드에 적치되어 있는 경우에만 작업예약에 등록 if end
			szMsg="["+szOperationName+"] 통합야드인 경우 작업예약 등록 후 차량이송준비스케줄["+szYD_PREP_SCH_ID+"]이 존재하는 지 확인";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			if( !szYD_PREP_SCH_ID.equals("") ) {
				/*
				 * 업무기준 : 통합야드인 경우 차량이송준비스케줄ID가 파라미터로 전달되는 경우에는 삭제처리 필요
				 * 업무기준 변경 : 차량이송준비스케줄 실제 삭제처리 대신 DEL_YN항목에 Y 설정
				 * 				작업예약 삭제 시 다시 차량이송준비스케줄의 DEL_YN항목에 N 설정되도록 처리
				 * 수정자 : 임춘수
				 * 수정일 : 2009.10.14
				 */
				szMsg="["+szOperationName+"] 통합야드인 경우 작업예약 등록 후 차량이송준비스케줄["+szYD_PREP_SCH_ID+"] 삭제 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				String szReturnMsg = YdCommonUtils.deletePreSch(szYD_PREP_SCH_ID, szYD_WBOOK_ID, szMethodName);
				
//				YdPrepMtlDao ydPrepMtlDao = new YdPrepMtlDao();
//				
//				recPara         = JDTORecordFactory.getInstance().create();
//				recPara.setField("YD_PREP_SCH_ID",   	szYD_PREP_SCH_ID);
//				recPara.setField("DEL_YN",   			"Y");
//				recPara.setField("MODIFIER",   			szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
//				//준비재료 삭제처리
//				intRtnVal = ydPrepMtlDao.uptDelYdPrepmtlByPrepSchId(recPara);
//				
//				YdPrepSchDao ydPrepSchDao = new YdPrepSchDao();
//				
//				//준비스케줄 삭제처리
//				recPara.setField("YD_WBOOK_ID",   		szYD_WBOOK_ID);
//				intRtnVal =  ydPrepSchDao.updDelYdPrepsch(recPara);
				
				szMsg="["+szOperationName+"] 통합야드인 경우 작업예약 등록 후 차량이송준비스케줄["+szYD_PREP_SCH_ID+"] 삭제, 작업예약ID["+szYD_WBOOK_ID+"] 등록 성공 : " + szReturnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				if( !szYD_CRN_EQP_ID.equals("")) {
					/*
					 * 업무기준 : 크레인의 현재동에 존재하는 이송준비스케줄을 작업예약에 등록 후 해당크레인의 배차실적을 증가시킴
					 * 수정자 : 임춘수
					 * 수정일 : 2009.10.14
					 */
					szMsg="["+szOperationName+"] 통합야드인 경우 해당크레인["+szYD_CRN_EQP_ID+"]의 배차실적을 증가시킴";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					YdEqpDao ydEqpDao = new YdEqpDao();
					recPara         = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_EQP_ID",   	szYD_CRN_EQP_ID);
					intRtnVal = ydEqpDao.updYdEqpDirect(recPara, 1);
					if( intRtnVal > 0 ) {
						szMsg="["+szOperationName+"] 통합야드인 경우 해당크레인["+szYD_CRN_EQP_ID+"]의 배차실적을 증가 성공";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}else{
						szMsg="["+szOperationName+"] 통합야드인 경우 해당크레인["+szYD_CRN_EQP_ID+"]의 배차실적을 증가 실패 - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					
					/*
					 * 크레인의 배차기준을 조회해서 모든 크레인의 배차대수와 배차실적이 동일한 경우에는 모든 크레인의 배차실적을 0으로 설정한다.
					 * 
					 */
					szMsg="["+szOperationName+"] 크레인의 배차기준을 조회 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					rsResult = JDTORecordFactory.getInstance().createRecordSet("");
					recPara  = JDTORecordFactory.getInstance().create();
					
					//야드구분
//					recPara.setField("YD_GP", YdConstant.YD_GP_INTGR_YARD);
//					recPara.setField("YD_BAY_GP", "");
//					
//					//설비 테이블 조회
//					intRtnVal = ydEqpDao.getYdEqp(recPara, rsResult, 10);
//					
//					if( intRtnVal == 0 ) {
//						szMsg="[차량상차 작업요구] 모든 크레인의 배차대수와 배차실적이 동일하므로 모든크레인의 배차실적을 0으로 설정 시작";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//						recPara         = JDTORecordFactory.getInstance().create();
//						recPara.setField("YD_GP",   	YdConstant.YD_GP_INTGR_YARD);
//						intRtnVal = ydEqpDao.updYdEqpDirect(recPara, 2);
//						
//						szMsg="[차량상차 작업요구] 모든 크레인의 배차대수와 배차실적이 동일하므로 모든크레인의 배차실적을 0으로 설정 완료";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//					}else if( intRtnVal > 0 ) {
//						szMsg="[차량상차 작업요구] 크레인의 배차대수와 배차실적이 다른 크레인이 존재하므로 모든 크레인의 배차실적을 0으로 설정을 하지 않음";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//					}
					/*
					 * 업무기준 : 1. 배차대수가 0보다 크고 우선순위가 낮은 순으로 배차기준을 조회 후 루프
					 * 				1-1. 크레인의 현재동의 작업가능한 LOT 수가 존재하는 지 확인
					 * 					1-1-1. 0이면 다음 크레인으로 루프 진행
					 * 					1-1-2. 0이상이면
					 * 						1-1-2-1. 배차대수와 배차실적이 같으면 모든 크레인의 배차실적을 0으로 설정
					 * 						1-1-2-2. 배차대수와 배차실적이 같지않으면 skip
					 * 						1-1-2-3. 루프 종료
					 * 배차대수와 배차실적이 같은 지를 비교하여 모든 배차실적을 0으로 설정
					 * 수정자 : 임춘수
					 * 수정일 : 2009.10.15
					 */
					recPara.setField("YD_GP", YdConstant.YD_GP_INTGR_YARD);
					recPara.setField("YD_BAY_GP", "");
					recPara.setField("YD_PREP_WK_ST", "L");
					//설비 테이블 조회
					intRtnVal = ydEqpDao.getYdEqp(recPara, rsResult, 11);
					
					if( intRtnVal > 0 ) {
						long lngYD_CRN_CONT_CARASGN_CNT = 0;
						long lngYD_CRN_CONT_CARASGN_WR 	= 0;
						long lngYD_LOT_CNT_OF_BAY 		= 0;
						String szYD_CURR_BAY_GP			= null;
						String szYD_CRN_USE_SEQ			= null;
						for(int i = 1; i <= rsResult.size(); i++ ) {
							rsResult.absolute(i);
							recPara = rsResult.getRecord();
							
							szYD_CRN_EQP_ID = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_ID");								//크레인설비ID
							szYD_CURR_BAY_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_CURR_BAY_GP");							//현재동
							szYD_CRN_USE_SEQ = ydDaoUtils.paraRecChkNull(recPara, "YD_CRN_USE_SEQ");							//크레인작업순위
							lngYD_CRN_CONT_CARASGN_CNT = ydDaoUtils.paraRecChkNullLong(recPara, "YD_CRN_CONT_CARASGN_CNT");		//배차대수
							lngYD_CRN_CONT_CARASGN_WR 	= ydDaoUtils.paraRecChkNullLong(recPara, "YD_CRN_CONT_CARASGN_WR");		//배차실적
							lngYD_LOT_CNT_OF_BAY	= ydDaoUtils.paraRecChkNullLong(recPara, szYD_CURR_BAY_GP + "_FRTOMOVE_LOT_CNT");	//현재동의 작업가능한 LOT 수
							
							szMsg="["+szOperationName+"] 우선순위["+szYD_CRN_USE_SEQ+"]가 가장 낮은 크레인["+szYD_CRN_EQP_ID+"] 배차대수["+lngYD_CRN_CONT_CARASGN_CNT+"]와 배차실적["+lngYD_CRN_CONT_CARASGN_WR+"]";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							
							szMsg="["+szOperationName+"] 현재동["+szYD_CURR_BAY_GP+"]의 작업가능한 LOT 수["+lngYD_LOT_CNT_OF_BAY+"]";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							
							if( lngYD_LOT_CNT_OF_BAY == 0 ) {
								if( i < rsResult.size() ) {
									szMsg="["+szOperationName+"] 현재동["+szYD_CURR_BAY_GP+"]의 작업가능한 LOT 수["+lngYD_LOT_CNT_OF_BAY+"]가 0이므로 다음 우선순위가 낮은 크레인을 찾기 위해 CONTINUE";
									ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
									continue;
								}else{
									szMsg="["+szOperationName+"] 현재동["+szYD_CURR_BAY_GP+"]의 작업가능한 LOT 수["+lngYD_LOT_CNT_OF_BAY+"]가 0이므로 우선순위가 낮은 마지막 크레인이므로 배차 실적 0으로 설정 시작 함";
									ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
									recPara         = JDTORecordFactory.getInstance().create();
									recPara.setField("YD_GP",   	YdConstant.YD_GP_INTGR_YARD);
									intRtnVal = ydEqpDao.updYdEqpDirect(recPara, 2);
									break;
								}
							}
							
							szMsg="["+szOperationName+"] 우선순위["+szYD_CRN_USE_SEQ+"]가 가장 낮은 크레인["+szYD_CRN_EQP_ID+"] 배차대수["+lngYD_CRN_CONT_CARASGN_CNT+"]와 배차실적["+lngYD_CRN_CONT_CARASGN_WR+"]이 같은 경우에는 모든크레인의 배차실적을 0으로 설정 시작";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							
							if( lngYD_CRN_CONT_CARASGN_CNT == lngYD_CRN_CONT_CARASGN_WR ) {
							
								szMsg="["+szOperationName+"] 우선순위가 가장 낮은 크레인 배차대수["+lngYD_CRN_CONT_CARASGN_CNT+"]와 배차실적["+lngYD_CRN_CONT_CARASGN_WR+"]이 동일하므로 모든크레인의 배차실적을 0으로 설정 시작";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								recPara         = JDTORecordFactory.getInstance().create();
								recPara.setField("YD_GP",   	YdConstant.YD_GP_INTGR_YARD);
								intRtnVal = ydEqpDao.updYdEqpDirect(recPara, 2);
								
								szMsg="["+szOperationName+"] 우선순위가 가장 낮은 크레인 배차대수["+lngYD_CRN_CONT_CARASGN_CNT+"]와 배차실적["+lngYD_CRN_CONT_CARASGN_WR+"]이 동일하므로 모든크레인의 배차실적을 0으로 설정 완료";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							}else{
								szMsg="["+szOperationName+"] 우선순위가 가장 낮은 크레인 배차대수["+lngYD_CRN_CONT_CARASGN_CNT+"]와 배차실적["+lngYD_CRN_CONT_CARASGN_WR+"]이 동일하지 않으므로 모든크레인의 배차실적을 0으로 설정하지 않음";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							}
							
							break;
						}
					}else if( intRtnVal == 0 ) {
						szMsg="["+szOperationName+"] 크레인의 배차대수와 배차실적이 다른 크레인이 존재하므로 모든 크레인의 배차실적을 0으로 설정을 하지 않음";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
				}
			}
			
			//record 생성
			recPara = JDTORecordFactory.getInstance().create();
			//설비ID - 차량설비ID
			recPara.setField("YD_EQP_ID",    szYD_EQP_ID);
			//차량사용구분
			recPara.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
			//운송장비코드
			recPara.setField("TRN_EQP_CD",    szTRN_EQP_CD);
			//개소코드
			recPara.setField("WLOC_CD",       szWLOC_CD);
			//운송작업영공구분코드
			recPara.setField("SP_TRUCK_LOADING_LOC_TP", szSP_TRUCK_LOADING_LOC_TP);
			//포인트요구일시
			recPara.setField("PNT_DMD_DT",    szPNT_DMD_DT);
			//발지개소코드
			recPara.setField("SPOS_WLOC_CD",  szSPOS_WLOC_CD);
			//발지포인트코드
			recPara.setField("SPOS_YD_PNT_CD", szSPOS_YD_PNT_CD);
			//직상차 구분 - 임춘수 2009.04.23 추가
			recPara.setField("YD_DIRECT_CARLD_GP", szYD_DIRECT_CARLD_GP);
			//이송적치열 - 임춘수 2009.04.23 추가
			recPara.setField("YD_FTMV_COL", szYD_FTMV_COL);
			//상차작업예약ID 20090617.김진욱
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
			//스케줄코드 20090617.김진욱
			recPara.setField("YD_SCH_CD",   szYD_SCH_CD);
			
			
			
			//스케줄 메소드 호출
			if( !szYD_CAR_SCH_YN.equals("N")){
				intRtnVal = this.mkY0CarSchForFrtoMove(recPara);
				if( szIS_EJB_CALL.equals("Y") ){
					if( intRtnVal != 1 ){
						szMsg = "["+szOperationName+"] 차량스케줄 생성 시 예외발생[리턴처리] : 리턴값 =" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return YdConstant.RETN_CD_FAILURE;
					}
				}else{
					//테스트용 
					//intRtnVal = -1;
					if( intRtnVal != 1 ){
						szMsg = "["+szOperationName+"] 차량스케줄 생성 시 예외발생[DAOException Throw] : 리턴값 =" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						throw new DAOException(YdConstant.RETN_CD_FAILURE);
					}
				}
			}
		
		} catch(Exception e){
			szMsg = "["+szOperationName+"] 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			if( szIS_EJB_CALL.equals("Y") ){
				return YdConstant.RETN_CD_FAILURE;
			}else{
				throw new DAOException(e.getMessage());
			}
		}

		return YdConstant.RETN_CD_SUCCESS;
	} // end of procSlabTotCarLdWrkReq()
	
	
	/**
	 * 오퍼레이션명 : 통합야드차량상차작업요구
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return String
	 * @throws JDTOException
	 */
	public String procSlabTotCarLdWrkReqS(JDTORecord msgRecord)throws JDTOException  {
		
		JDTORecord recPara    			= null;	//레코드 선언
		YdDaoUtils ydDaoUtils           = new YdDaoUtils();	//공용 DAO METHOD
		
		int intRtnVal          			= 0;	//리턴값(int)		
		int intMtlCnt              		= 0;	//재료매수(int)		
		String szMsg           			= "";	//메세지		
		String szMethodName    			= "procSlabTotCarLdWrkReqS";	//METHOD명
		String szOperationName			= "통합야드차량상차작업요구";
		String szYD_EQP_ID         		= null;	//설비ID
		String szYD_CAR_USE_GP     		= null;	//차량사용구분		
		String szTRN_EQP_CD        		= null;	//운송장비코드				
		String szYD_SCH_CD         		= null;	//스케줄코드		
		String szYD_WBOOK_ID      	 	= null;	//작업예약ID	
		String szWLOC_CD           		= null;	//개소코드		
		String szSP_TRUCK_LOADING_LOC_TP = null;//운송작업영공구분코드		
		String szPNT_DMD_DT          	= null;	//포인트요구일시		
		String szSPOS_WLOC_CD      		= null;	//발지개소코드		
		String szSPOS_YD_PNT_CD    		= null;	//발지포인트코드			
		String szYD_DIRECT_CARLD_GP 	= "";	//직상차구분	
		String szYD_FTMV_COL			= "";	//이송적치열		
		String szIS_EJB_CALL			= null;	//EJB CALL or JMS CALL 판단 변수		
		String szYD_PREP_SCH_ID			= null;	//차량이송준비스케줄ID

		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		
		//에러 리턴
		if(szRcvTcCode == null || szRcvTcCode.equals("")){
		
			szMsg = "["+szOperationName+"] TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_TC_ERROR;
		
		}
		//TC CODE DISPLAY
		if(bDebugFlag){
		
			szMsg = "["+szOperationName+"] 전문수신 : TCCODE=" + szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			szMsg = "["+szOperationName+"] 메소드 시작 - 전문내용확인";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			ydUtils.displayRecord(szOperationName, msgRecord);
			//받은 전문 편집
			//스케줄코드
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			if(szYD_SCH_CD.equals("")){				
				szMsg = "["+szOperationName+"] [전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;				
			}
			
			//설비ID - 차량설비ID
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if(szYD_EQP_ID.equals("")){				
				szMsg = "["+szOperationName+"] [전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;				
			}

			//차량사용구분
			szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_USE_GP");
			if(szYD_CAR_USE_GP.equals("")){				
				szMsg = "["+szOperationName+"] [전문 이상] 차량사용구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;				
			}
			
			if(szYD_CAR_USE_GP.equals("L")){
				//운송장비코드
				szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(msgRecord, "TRN_EQP_CD");
				if(szTRN_EQP_CD.equals("")){
					
					szMsg = "["+szOperationName+"] [전문 이상] 운송장비코드가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;
				}
				
				//개소코드
				szWLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord, "WLOC_CD");
				if(szWLOC_CD.equals("")){
					
					szMsg = "["+szOperationName+"] [전문 이상] 개소코드가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;					
				}
				
				//운송작업영공구분코드
				szSP_TRUCK_LOADING_LOC_TP = ydDaoUtils.paraRecChkNull(msgRecord, "SP_TRUCK_LOADING_LOC_TP");
				if(szSP_TRUCK_LOADING_LOC_TP.equals("")){
					
					szMsg = "["+szOperationName+"] [전문 이상] 운송작업영공구분코드가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;					
				}
				
				//포인트요구일시
				szPNT_DMD_DT = ydDaoUtils.paraRecChkNull(msgRecord, "PNT_DMD_DT");
				if(szPNT_DMD_DT.equals("")){
					
					szMsg = "["+szOperationName+"] [전문 이상] 포인트요구일시가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;					
				}
				
			}
			//차량이송 준비스케줄ID - 2009.10.02 임춘수 추가
			szYD_PREP_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_PREP_SCH_ID");
			
			//발지개소코드
			szSPOS_WLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_WLOC_CD");

			//발지포인트코드
			szSPOS_YD_PNT_CD = ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_YD_PNT_CD");
			
			//재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_CARLD_SH");
			if(intMtlCnt == 0){
				
				szMsg = "["+szOperationName+"] [전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;				
			}

			
			//직상차구분 : C - 직상차, Y - 일반야드, B - 대상재 존재 않함
			szYD_DIRECT_CARLD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_DIRECT_CARLD_GP");
			//이송적치열
			szYD_FTMV_COL = ydDaoUtils.paraRecChkNull(msgRecord, "YD_FTMV_COL");
			
			szMsg = "["+szOperationName+"] [전문 내용] 직상차구분[szYD_DIRECT_CARLD_GP - " + szYD_DIRECT_CARLD_GP + " ]" + ", 이송적치열 [" + szYD_FTMV_COL + "]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//연계모듈을 EJB Call[Y값으로 설정] or JMS Call[값이 없거나 N값으로 설정] 호출 유무 변수  - 지금은 화면으로부터 전송(임춘수 추가 2009.07.13)
			szIS_EJB_CALL = ydDaoUtils.paraRecChkNull(msgRecord, "IS_EJB_CALL");

			
				if( !szYD_PREP_SCH_ID.equals("") ) {
				/*
				 * 업무기준 : 통합야드인 경우 차량이송준비스케줄ID가 파라미터로 전달되는 경우에는 삭제처리 필요
				 * 업무기준 변경 : 차량이송준비스케줄 실제 삭제처리 대신 DEL_YN항목에 Y 설정
				 * 				작업예약 삭제 시 다시 차량이송준비스케줄의 DEL_YN항목에 N 설정되도록 처리
				 * 수정자 : 임춘수
				 * 수정일 : 2009.10.14
				 */
				szMsg="["+szOperationName+"] 통합야드인 경우 작업예약 등록 후 차량이송준비스케줄["+szYD_PREP_SCH_ID+"] 삭제 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				String szReturnMsg = YdCommonUtils.deletePreSch(szYD_PREP_SCH_ID, szYD_WBOOK_ID, szMethodName);
				
				szMsg="["+szOperationName+"] 통합야드인 경우 작업예약 등록 후 차량이송준비스케줄["+szYD_PREP_SCH_ID+"] 삭제, 작업예약ID["+szYD_WBOOK_ID+"] 등록 성공 : " + szReturnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				
			}
			
			//record 생성
			recPara = JDTORecordFactory.getInstance().create();
			//설비ID - 차량설비ID
			recPara.setField("YD_EQP_ID",    szYD_EQP_ID);
			//차량사용구분
			recPara.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
			//운송장비코드
			recPara.setField("TRN_EQP_CD",    szTRN_EQP_CD);
			//개소코드
			recPara.setField("WLOC_CD",       szWLOC_CD);
			//운송작업영공구분코드
			recPara.setField("SP_TRUCK_LOADING_LOC_TP", szSP_TRUCK_LOADING_LOC_TP);
			//포인트요구일시
			recPara.setField("PNT_DMD_DT",    szPNT_DMD_DT);
			//발지개소코드
			recPara.setField("SPOS_WLOC_CD",  szSPOS_WLOC_CD);
			//발지포인트코드
			recPara.setField("SPOS_YD_PNT_CD", szSPOS_YD_PNT_CD);
			//직상차 구분 - 임춘수 2009.04.23 추가
			recPara.setField("YD_DIRECT_CARLD_GP", szYD_DIRECT_CARLD_GP);
			//이송적치열 - 임춘수 2009.04.23 추가
			recPara.setField("YD_FTMV_COL", szYD_FTMV_COL);
			//상차작업예약ID 20090617.김진욱
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
			//스케줄코드 20090617.김진욱
			recPara.setField("YD_SCH_CD",   szYD_SCH_CD);
			
			
			
			//차량스케줄 메소드 호출
			intRtnVal = this.mkY0CarSchForFrtoMove(recPara);
			if( intRtnVal != 1 ){
				szMsg = "["+szOperationName+"] 차량스케줄 생성 시 예외발생[리턴처리] : 리턴값 =" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}
		
		} catch(Exception e){
			szMsg = "["+szOperationName+"] 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			if( szIS_EJB_CALL.equals("Y") ){
				return YdConstant.RETN_CD_FAILURE;
			}else{
				throw new DAOException(e.getMessage());
			}
		}

		return YdConstant.RETN_CD_SUCCESS;
	} // end of procSlabTotCarLdWrkReqS()
	
	/**
	 * 오퍼레이션명 : 통합야드차량스케줄생성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
		
		public int mkY0CarSchForFrtoMove(JDTORecord msgRecord)throws JDTOException  {
		YdDelegate  ydDelegate  = new YdDelegate();
		YdCarSchDao ydCarSchDao = new YdCarSchDao();
		//YdDBAssist  ydDBAssist  = new YdDBAssist();
		YdStkColDao ydStkColDao = new YdStkColDao();
		//JDTORecordSet rsResult 		     = null;
		JDTORecordSet rsStkCol  		 = null;
		JDTORecord    recOutTemp		 = null;
		JDTORecord    recInTemp  		 = null;
		
		
	    int intRtnVal 		      		 = 0 ;
	    
	    String szMsg              		 = "";
	    String szMethodName       		 = "mkY0CarSchForFrtoMove";
	    String szOperationName    		 = "통합야드차량스케줄생성(구내운송)";
	    
	    String szWLOC_CD          		 = "";
	    //String szYD_GP            		 = "";
	    //String szQuery            		 = ""; 
	    String szYD_EQP_ID        		 = ""; 
	    String szTRN_EQP_CD       		 = "";
	    String szYD_CAR_USE_GP    		 = "";
	    String szSPOS_WLOC_CD            = "";
	    String szSPOS_YD_PNT_CD          = "";
	    String szYD_CARLD_LEV_LOC        = "";
	    String szPNT_DMD_DT              = "";
	    //String szSP_TRUCK_LOADING_LOC_TP = "";
	    String szYD_WBOOK_ID             = "";
	    String szCAR_NO                  = "";
	    String szCARD_NO                 = "";
	    String szYD_CRN_EQP_ID           = "";
	    String szYD_SCH_CD               = "";
	    String szYD_CARLD_STOP_LOC       = "";
	    String szYD_DIRECT_CARLD_GP		 = "";
	    String szYD_FTMV_COL			 = "";
	    String szTRANS_ORD_DATE          = "";
	    String szTRANS_ORD_SEQNO         = "";
	    String szYD_CAR_SCH_ID			 = "";
	    
	    try{
	    	
	    	szMsg = "["+szOperationName+"] 메소드 시작 : 전문내용 보기";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			ydUtils.displayRecord(szOperationName, msgRecord);
	    	
    		//파라미터 편집
	    	szYD_EQP_ID               = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
	    	szTRN_EQP_CD              = ydDaoUtils.paraRecChkNull(msgRecord, "TRN_EQP_CD"); 
	    	szYD_CAR_USE_GP           = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_USE_GP");
	    	szWLOC_CD                 = ydDaoUtils.paraRecChkNull(msgRecord, "WLOC_CD");
	    	szSPOS_WLOC_CD            = ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_WLOC_CD");
	    	szSPOS_YD_PNT_CD          = ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_YD_PNT_CD");
	    	szPNT_DMD_DT              = ydDaoUtils.paraRecChkNull(msgRecord, "PNT_DMD_DT");
	    	szYD_WBOOK_ID             = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");     
	    	//szCAR_NO                  = ydDaoUtils.paraRecChkNull(msgRecord, "CAR_NO");
	    	//szCARD_NO                 = ydDaoUtils.paraRecChkNull(msgRecord, "CARD_NO");
	    	szYD_SCH_CD               = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
	    	//szTRANS_ORD_DATE          = ydDaoUtils.paraRecChkNull(msgRecord, "TRANS_ORD_DATE");
	    	//szTRANS_ORD_SEQNO         = ydDaoUtils.paraRecChkNull(msgRecord, "TRANS_ORD_SEQNO");
	    	
	    	
	    	
	    	//직상차구분 : C - 직상차, Y - 일반야드, B - 대상재 존재하지 않음 ---- 임춘수 2009.04.23 추가
			szYD_DIRECT_CARLD_GP 		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_DIRECT_CARLD_GP");
			//이송적치열
			szYD_FTMV_COL 				= ydDaoUtils.paraRecChkNull(msgRecord, "YD_FTMV_COL");
	    	
			szMsg = "["+szOperationName+"] 직상차구분[szYD_DIRECT_CARLD_GP - " + szYD_DIRECT_CARLD_GP + " ]" + ", 이송적치열 [" + szYD_FTMV_COL + "]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
//	    	if(szYD_CAR_USE_GP.equals("G")){
//	    		szYD_CRN_EQP_ID     = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_EQP_ID"); 
//	    		szYD_SCH_CD         = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
//	    		szYD_CARLD_STOP_LOC = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CARLD_STOP_LOC");
//	    	}
	    	
	    	
	    	//발지개소코드를 변환, 발지개소Point를 변환(출발지 위치)
	    	rsStkCol = JDTORecordFactory.getInstance().createRecordSet("");
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("WLOC_CD",   szSPOS_WLOC_CD);
	    	recInTemp.setField("YD_PNT_CD", szSPOS_YD_PNT_CD);
	    	intRtnVal = ydStkColDao.getYdStkcol(recInTemp, rsStkCol, 4);
			if(intRtnVal <= 0){
				if(intRtnVal == 0){
					szMsg="data not found";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				}else if(intRtnVal == -2){
					szMsg="parameter error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
//				return intRtnVal = -1;
			}
	    	
	    	if(intRtnVal > 0){
		    	recOutTemp = JDTORecordFactory.getInstance().create();
		    	recOutTemp.setRecord(rsStkCol.getRecord(0));
		    	
			    //열구분을 조회(발지위치)
			    szYD_CARLD_LEV_LOC = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_COL_GP");
	    	}else{
		    	szMsg="["+szOperationName+"] 등록되지 않은 개소코드와 포인트코드입니다. ex) A,B열연 OR 대기장";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				szYD_CARLD_LEV_LOC = "";
	    	}
	    	
	    	szYD_CAR_SCH_ID = ydCarSchDao.getYdCarschId();
	    	
	    	szMsg="["+szOperationName+"] 차량스케줄생성 시작 전";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
	    	//차량스케줄INSERT 항목
			recOutTemp = JDTORecordFactory.getInstance().create();
			recOutTemp.setField("YD_CAR_SCH_ID",    szYD_CAR_SCH_ID);
	    	recOutTemp.setField("REGISTER",         (szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName));
	    	recOutTemp.setField("YD_EQP_WRK_STAT",  "U");
	    	recOutTemp.setField("YD_EQP_ID",        szYD_EQP_ID);
	    	recOutTemp.setField("TRN_EQP_CD",       szTRN_EQP_CD);
	    	recOutTemp.setField("YD_CAR_USE_GP",    szYD_CAR_USE_GP);
	    	
	    	if(szYD_CAR_USE_GP.equals("G")) {
				recOutTemp.setField("CAR_KIND","TR");
			}else{
				recOutTemp.setField("CAR_KIND","PT");
			}
	    	
	    	recOutTemp.setField("WLOC_CD",          szWLOC_CD);
	    	//2009.05.12 임춘수 추가
	    	recOutTemp.setField("SPOS_WLOC_CD",          szWLOC_CD);
	    	//2009.11.17 임춘수 추가
	    	recOutTemp.setField("YD_BAYIN_WO_SEQ",  YdConstant.YD_BAYIN_WO_SEQ_DEFAULT);
	    	
	    	recOutTemp.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);
	    	//recOutTemp.setField("CAR_NO",           	szCAR_NO);
	    	//recOutTemp.setField("CARD_NO",          	szCARD_NO);
	    	recOutTemp.setField("YD_CAR_PROG_STAT", 	YdConstant.YD_CARLD_LEV);//상차출발상태
	    	recOutTemp.setField("YD_CARLD_LEV_DT", 		YdUtils.getCurDate("yyyyMMddHHmmss"));//상차출발일시
	    	//recOutTemp.setField("TRANS_ORD_DATE",           szTRANS_ORD_DATE);
	    	//recOutTemp.setField("TRANS_ORD_SEQNO",          szTRANS_ORD_SEQNO);
	    	
	    	
//	    	if(szYD_CAR_USE_GP.equals("G")){
//	    		//차량상차정지위치를 등록한다.
//	    		recOutTemp.setField("YD_CARLD_STOP_LOC", szYD_CARLD_STOP_LOC);
//	    		recOutTemp.setField("YD_CARLD_ARR_DT", YdUtils.getCurDate("yyyyMMddHHmmss"));
//	    		recOutTemp.setField("YD_CAR_PROG_STAT", "2");//상차도착상태
//	    	}else if( szYD_CAR_USE_GP.equals("L") ){
	    		if( szYD_DIRECT_CARLD_GP.equals("B") ){
	    			recOutTemp.setField("YD_CARLD_LEV_LOC", szYD_CARLD_LEV_LOC);
	    			//if( szYD_SCH_CD.subSequence(0, 1).equals(YdConstant.YD_GP_INTGR_YARD)) {
    				/*
					 * 대상재가 없는 경우에도 0000 포인트코드를 전송한다.
					 */
    				//상차정지위치
    				recOutTemp.setField("YD_PNT_CD1",  "0000");
    				//상차point지시일시
    				recOutTemp.setField("YD_CARLD_PNT_WO_DT", YdUtils.getCurDate("yyyyMMddHHmmss"));
    				szMsg="["+szOperationName+"] 대상재가 없는 경우에는 포인트코드[0000]로 수정 처리 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			//}
	    		}
	    		/*구내운송 - 임춘수 2009.04.23 추가*/
	    		else if(szYD_FTMV_COL.equals("")){
		    		recOutTemp.setField("YD_CARLD_STOP_LOC", szYD_SCH_CD.substring(0,6));	//작업예약에서 생성한 스케줄코드값을 잘라쓴다.
		    		recOutTemp.setField("YD_CARLD_LEV_LOC", szYD_CARLD_LEV_LOC);            //대기장이 아니라면 검색되기때문에 출발위치
	    		}else{
		    		recOutTemp.setField("YD_CARLD_STOP_LOC", szYD_FTMV_COL + "01");			//임의 값을 먼저 설정
		    		recOutTemp.setField("YD_CARLD_LEV_LOC", szYD_CARLD_LEV_LOC);
	    		}

	    	//}
	    	
	    	//차량스케줄 등록
	    	intRtnVal = ydCarSchDao.insYdCarsch(recOutTemp);
    		if(intRtnVal == -2){
				szMsg="["+szOperationName+"] 차량스케줄 등록 시 parameter error";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException(szMsg);
    		}
    		
    		szMsg="["+szOperationName+"] 차량스케줄생성 완료";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    		
    		if(szYD_CAR_USE_GP.equals("L") ){							//구내운송
    			if( !szYD_DIRECT_CARLD_GP.equals("B") ){
		    		//소재차량정지Point요구 호출
					//record 생성
					recInTemp = JDTORecordFactory.getInstance().create();
					//JMS TC CODE
		    		recInTemp.setField("JMS_TC_CD",               "YDYDJ630");
					//운송장비코드
		    		recInTemp.setField("TRN_EQP_CD",              szTRN_EQP_CD);
					//개소코드
		    		recInTemp.setField("WLOC_CD",                 szWLOC_CD);
					//운송작업영공구분코드
		    		recInTemp.setField("TRN_WRK_FULLVOID_GP",     "E");
					//포인트요구일시
		    		recInTemp.setField("PNT_DMD_DT",              szPNT_DMD_DT);
		    		
					//전문 송신
					ydDelegate.sendMsg(recInTemp);
					
					szMsg="["+szOperationName+"] 소재차량정지Point요구 호출 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			}else{
    				
    				// EJB CALL 변경
    				szMsg="["+szOperationName+"] 대상재가 없는 경우에는 0000포인트코드를 전송 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    				
    				recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("JMS_TC_CD",        		"YDYDJ630");
					recInTemp.setField("TRN_EQP_CD", 			szTRN_EQP_CD);
					
					//ydDelegate.sendMsg(recInTemp);
					
					EJBConnector ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);
					ejbConn.trx("procMatlCarArrPntReq", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
					
					
					szMsg="["+szOperationName+"] 대상재가 없는 경우에는 0000포인트코드를 전송 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
    				
    			}
    		}
//    		else{															//출하관리
//    			//크레인 스케줄 송신
//    			recInTemp = JDTORecordFactory.getInstance().create();
//    			
//    			if(szYD_SCH_CD.subSequence(0, 1).equals("A") ){
//    				//JMS TC CODE (C연주)
//    	    		recInTemp.setField("JMS_TC_CD", "YDYDJ500");
//    	    		
//    			}else if(szYD_SCH_CD.subSequence(0, 1).equals("D")){
//    				//JMS TC CODE (A후판슬라브)
//    	    		recInTemp.setField("JMS_TC_CD", "YDYDJ503");
//    	    		
//    			}else if(szYD_SCH_CD.subSequence(0, 1).equals("K")){
//    				//JMS TC CODE (후판제품)
//    	    		recInTemp.setField("JMS_TC_CD", "YDYDJ506");
//    	    		
//    			}else if(szYD_SCH_CD.subSequence(0, 1).equals("H") || szYD_SCH_CD.subSequence(0, 1).equals("J")){
//    				//JMS TC CODE (C열연코일)
//    	    		recInTemp.setField("JMS_TC_CD", "YDYDJ509");
//    	    		
//	    		}else if( szYD_SCH_CD.subSequence(0, 1).equals("S")){
//    				//JMS TC CODE (통합)
//    	    		recInTemp.setField("JMS_TC_CD", "YDYDJ512");
//    	    		
//    			}
//
//	    		//스케줄코드
//	    		recInTemp.setField("YD_SCH_CD", szYD_SCH_CD);
//	    		//설비ID
//	    		recInTemp.setField("YD_EQP_ID", szYD_CRN_EQP_ID);
//	    		
//				szMsg="크레인 스케줄 송신";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//				ydUtils.displayRecord(szOperationName, recInTemp);
//				//전문 송신
//				ydDelegate.sendMsg(recInTemp);
//    		}
			
		}catch(Exception e){
			
			szMsg="["+szOperationName+"] 예외발생 : " +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(e);
		}
	
	
		szMsg="["+szOperationName+"] 메소드 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		return intRtnVal = 1;
	} //end of mkY0CarSchForFrtoMove()
	
	
	
	/**
	 * 오퍼레이션명 : 차량상차작업요구
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return String
	 * @throws JDTOException
	 */
	public String procCCsCarLdWrkReq(JDTORecord msgRecord)throws JDTOException  {
		
		YdDelegate ydDelegate = new YdDelegate();
		//스케줄기준 DAO
		YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();
		//작업예약 재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils           = new YdDaoUtils();
		//공용 METHOD
		YdUtils ydutils                 = new YdUtils();
		
		//리턴값(boolean)
		boolean blnRtnVal      = false;
		//리턴값(int)
		int intRtnVal          = 0;
		//메세지
		String szMsg           = "";
		//METHOD명
		String szMethodName    = "procCCsCarLdWrkReq";
		String szOperationName	= "차량상차작업요구";
		//사용자
		String szUser          = "SYSTEM";
		
		//레코드 선언
		JDTORecord recPara     = null;
		JDTORecord recStkPara  = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		
		//설비ID
		String szYD_EQP_ID         = null;
		//목표행선구분
		String szYD_AIM_RT_GP      = null;
		//차량사용구분
		String szYD_CAR_USE_GP     = null;
		//운송장비코드
		String szTRN_EQP_CD        = null;
		//재료매수(int)
		int intMtlCnt              = 0;
		//재료번호
		String[] szSTL_NO          = null;
		//권상모음순서
		String[] szYD_UP_COLL_SEQ  = null;
		//스케줄코드
		String szYD_SCH_CD         = null;
		//스케줄우선순위
		String szYD_SCH_PRIOR		= "";
		//야드작업크레인우선순위
		String szYD_WRK_CRN_PRIOR  = null;
		//스케줄 금지 유무
		String szYD_SCH_PROH_EXN   = null;
		//작업크레인
		String szYD_WRK_CRN        = null;
		//대체크레인유무
		String szYD_ALT_CRN_YN     = null;
		//대체크레인
		String szYD_ALT_CRN        = null;
		//야드대체크레인우선순위
		String szYD_ALT_CRN_PRIOR  = null;
		//선택크레인
		String szCrn               = null;
		//작업예약ID
		String szYD_WBOOK_ID       = null;
		//개소코드
		String szWLOC_CD           = null;
		//운송작업영공구분코드
		String szSP_TRUCK_LOADING_LOC_TP = null;
		//포인트요구일시
		String szPNT_DMD_DT          = null;
		//발지개소코드
		String szSPOS_WLOC_CD      = null;
		//발지포인트코드
		String szSPOS_YD_PNT_CD    = null;
		
		//야드목표야드구분
		String szYD_AIM_YD_GP        = null;
		//야드목표동구분
		String szYD_AIM_BAY_GP       = null;
		
		//직상차구분
		String szYD_DIRECT_CARLD_GP = "";
		//이송적치열
		String szYD_FTMV_COL		= "";
		//차량스케줄 생성유무  - 2009.05.11 임춘수 추가
		String szYD_CAR_SCH_YN		= "";
		//EJB CALL or JMS CALL 판단 변수
		String szIS_EJB_CALL		= null;
		//차량이송준비스케줄ID
		String szYD_PREP_SCH_ID		= null;
		//크레인설비ID
		String szYD_CRN_EQP_ID		= null;
		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		
		//에러 리턴
		if(szRcvTcCode == null || szRcvTcCode.equals("")){
		
			szMsg = "["+szOperationName+"] TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_TC_ERROR;
		
		}
		//TC CODE DISPLAY
		if(bDebugFlag){
		
			szMsg = "["+szOperationName+"] 전문수신 : TCCODE=" + szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			szMsg = "["+szOperationName+"] 메소드 시작 - 전문내용확인";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			ydUtils.displayRecord(szOperationName, msgRecord);
			//받은 전문 편집
			//스케줄코드
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			if(szYD_SCH_CD.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			//설비ID - 차량설비ID
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if(szYD_EQP_ID.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			//목표행선구분
			szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_RT_GP");
//			if(szYD_AIM_RT_GP.equals("")){
//				
//				szMsg = "["+szOperationName+"] [전문 이상] 목표행선구분이 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return YdConstant.RETN_CD_NO_PARAM;
//			}
			//차량사용구분
			szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_USE_GP");
			if(szYD_CAR_USE_GP.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 차량사용구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			
			if(szYD_CAR_USE_GP.equals("L")){
				//운송장비코드
				szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(msgRecord, "TRN_EQP_CD");
				if(szTRN_EQP_CD.equals("")){
					
					szMsg = "["+szOperationName+"] [전문 이상] 운송장비코드가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;
				}
				
				//개소코드
				szWLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord, "WLOC_CD");
				if(szWLOC_CD.equals("")){
					
					szMsg = "["+szOperationName+"] [전문 이상] 개소코드가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;
					
				}
				
				//운송작업영공구분코드
				szSP_TRUCK_LOADING_LOC_TP = ydDaoUtils.paraRecChkNull(msgRecord, "SP_TRUCK_LOADING_LOC_TP");
				if(szSP_TRUCK_LOADING_LOC_TP.equals("")){
					
					szMsg = "["+szOperationName+"] [전문 이상] 운송작업영공구분코드가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;
					
				}
				
				//포인트요구일시
				szPNT_DMD_DT = ydDaoUtils.paraRecChkNull(msgRecord, "PNT_DMD_DT");
				if(szPNT_DMD_DT.equals("")){
					
					szMsg = "["+szOperationName+"] [전문 이상] 포인트요구일시가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;
					
				}
				
			}
			//차량이송 준비스케줄ID - 2009.10.02 임춘수 추가
			szYD_PREP_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_PREP_SCH_ID");
			//크레인설비ID - 2009.10.14 임춘수 추가
			szYD_CRN_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_EQP_ID");
			
			//발지개소코드
			szSPOS_WLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_WLOC_CD");
//			if(szSPOS_WLOC_CD.equals("")){
//				
//				szMsg = "[전문 이상] 발지개소코드가 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return YdConstant.RETN_CD_NO_PARAM;
//				
//			}
			
			//발지포인트코드
			szSPOS_YD_PNT_CD = ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_YD_PNT_CD");
//			if(szSPOS_YD_PNT_CD.equals("")){
//				
//				szMsg = "[전문 이상] 발지포인트코드가 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return YdConstant.RETN_CD_NO_PARAM;
//				
//			}
			
			//재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_CARLD_SH");
			if(intMtlCnt == 0){
				
				szMsg = "["+szOperationName+"] [전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}

			//재료번호
			szSTL_NO          = new String[intMtlCnt + 1];
			//권상모음순서
			szYD_UP_COLL_SEQ  = new String[intMtlCnt + 1];
			
			
			//재료번호, 권상모음순서
			for(int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
				
				//재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO" + Loop_i);
				if(szSTL_NO[Loop_i].equals("")){
					
					szMsg = "["+szOperationName+"] [전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;
					
				}
				//권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_COLL_SEQ" + Loop_i);
				if(szYD_UP_COLL_SEQ[Loop_i].equals("")){
					
					szMsg = "["+szOperationName+"] [전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i] + ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;
					
				}
			}
			
			//직상차구분 : C - 직상차, Y - 일반야드, B - 대상재 존재 않함
			szYD_DIRECT_CARLD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_DIRECT_CARLD_GP");
			//이송적치열
			szYD_FTMV_COL = ydDaoUtils.paraRecChkNull(msgRecord, "YD_FTMV_COL");
			//차량스케줄 생성유무  - 2009.05.11 임춘수 추가
			szYD_CAR_SCH_YN		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_SCH_YN");
			
			szMsg = "["+szOperationName+"] [전문 내용] 직상차구분[szYD_DIRECT_CARLD_GP - " + szYD_DIRECT_CARLD_GP + " ]" + ", 이송적치열 [" + szYD_FTMV_COL + "]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//연계모듈을 EJB Call[Y값으로 설정] or JMS Call[값이 없거나 N값으로 설정] 호출 유무 변수  - 지금은 화면으로부터 전송(임춘수 추가 2009.07.13)
			szIS_EJB_CALL = ydDaoUtils.paraRecChkNull(msgRecord, "IS_EJB_CALL");
			
			szMsg="[차량상차작업요구]szIS_EJB_CALL = " + szIS_EJB_CALL;

			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if(!blnRtnVal) return YdConstant.RETN_CD_FAILURE;
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//스케줄CD 체크
			//스케줄 금지 유무
			szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");
			//작업크레인
			szYD_WRK_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
			//작업크레인우선순위
			szYD_WRK_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN_PRIOR");
			//대체크레인유무
			szYD_ALT_CRN_YN   = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
			//대체크레인
			szYD_ALT_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
			//야드대체크레인우선순위
			szYD_ALT_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_PRIOR");
			
			//스케줄 금지 유무가  "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if(szYD_SCH_PROH_EXN.equals("Y")){
				
				szMsg = "["+szOperationName+"] 스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				/*
				 * 비록 스케줄금지 상태이더라도 작업과 차량스케줄은 등록 후 POINT전송
				 * 수정자 : 임춘수
				 * 수정일 : 2009.10.14
				 */
				//return YdConstant.RETN_CRN_SCH_PROH;
			}
			
			//작업크레인 설비 상태 체크
			blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);
			
			//작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if(!blnRtnVal){
				
				szMsg = "["+szOperationName+"] 작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
				//대체크레인의 유무를 체크한다.
				//대체크레인이 없으면 에러 리턴
				if(!szYD_ALT_CRN_YN.equals("Y")){
					
					szMsg = "["+szOperationName+"] 대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if(!blnRtnVal){
					
					szMsg = "["+szOperationName+"] 대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;
					
				} else {
					//대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					szCrn = szYD_ALT_CRN;
					szYD_SCH_PRIOR	= szYD_ALT_CRN_PRIOR;
				}
			} else {
				//작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				szCrn = szYD_WRK_CRN;
				szYD_SCH_PRIOR	= szYD_WRK_CRN_PRIOR;
			}
			/*
			* 대상재가 일반 야드에 적치되어 있는 경우에만 작업예약에 등록 - 임춘수 2009.04.23 추가
			 */
			if( !szYD_DIRECT_CARLD_GP.equals("C") && !szYD_DIRECT_CARLD_GP.equals("B")){

				//재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다.
				//작업예약재료 등록 여부를 체크한다.
				for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
					
					//크레인사양과 저장품 사양을 체크(길이,폭,중량)
					blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
					if(!blnRtnVal) return YdConstant.RETN_CD_FAILURE;
					
					//다른 작업예약에 재료가 등록되어있는지 체크한다.
					blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
					if(!blnRtnVal) return YdConstant.RETN_CD_FAILURE;
					
				}	
				
				//리턴 recordSet 생성
				rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
				
				//작업예약ID 생성
				blnRtnVal = getYdWbookId(rsResult);
				if(!blnRtnVal) return YdConstant.RETN_CD_FAILURE;
				//레코드추출
				rsResult.first();
				recPara = rsResult.getRecord();
				//작업예약ID
				szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");
				
				//리턴 recordSet 생성
				rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
				//저장품테이블 조회
				blnRtnVal = this.chkGetStock(szSTL_NO[1], rsResult);
				if(!blnRtnVal) return YdConstant.RETN_CD_FAILURE;
				//레코드추출
				rsResult.first();
				recPara = rsResult.getRecord();
				
				//야드목표야드구분
				szYD_AIM_YD_GP  = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_YD_GP");
				//야드목표동구분
				szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_BAY_GP");
				
				szMsg = "["+szOperationName+"] 재료번호["+szSTL_NO[1]+"]의 목표야드["+szYD_AIM_YD_GP+"], 목표동["+szYD_AIM_BAY_GP+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				if( !szYD_PREP_SCH_ID.equals("") ) {
					/*
					 * 준비스케줄ID가 존재하는 경우는 이송LOT편성된 정보에서 목표야드와 목표동 구분을 조회해서 사용
					 * 수정자 : 임춘수
					 * 수정일 : 2009.10.26
					 */
					rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_PREP_SCH_ID",   szYD_PREP_SCH_ID);
					YdPrepSchDao ydPrepSchDao = new YdPrepSchDao();
					intRtnVal = ydPrepSchDao.getYdPrepsch(recPara, rsResult, 0);
					if( intRtnVal <= 0) {
						szMsg = "["+szOperationName+"] 준비스케줄ID["+szYD_PREP_SCH_ID+"]로 조회 시 오류발생 - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}else{
						rsResult.first();
						recPara = rsResult.getRecord();
						
						//야드목표야드구분
						szYD_AIM_YD_GP  = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_YD_GP");
						//야드목표동구분
						szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_BAY_GP");
						
						szMsg = "["+szOperationName+"] 준비스케줄ID["+szYD_PREP_SCH_ID+"]로 조회 시 데이타가 존재합니다. - 목표야드["+szYD_AIM_YD_GP+"], 목표동["+szYD_AIM_BAY_GP+"]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
					}
				}
				
				//INSERT 항목 record 생성
				recPara = JDTORecordFactory.getInstance().create();
				//야드구분
				String szYD_GP       = szYD_SCH_CD.substring(0, 1);
				//동구분
				String szYD_BAY_GP   = szYD_SCH_CD.substring(1, 2);
				
				//INSERT할 항목 SET
				recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
				recPara.setField("YD_GP", 		  szYD_GP);
				recPara.setField("YD_BAY_GP", 	  szYD_BAY_GP);
				recPara.setField("YD_SCH_PRIOR", 	szYD_SCH_PRIOR);
				recPara.setField("YD_SCH_CD", 	  szYD_SCH_CD);
				recPara.setField("REGISTER", 	  szUser);
				recPara.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
				recPara.setField("TRN_EQP_CD", 	  szTRN_EQP_CD);
				recPara.setField("YD_AIM_YD_GP",  szYD_AIM_YD_GP);
				recPara.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP);
		
				//작업예약 INSERT
				intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
				if(intRtnVal < 1){
					szMsg = "["+szOperationName+"] 작업예약 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;
				}
				
				
				//조회항목 record 생성
				recPara = JDTORecordFactory.getInstance().create();
				
				recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
	//			recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
	//			recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
				recPara.setField("REGISTER", 	  szUser);
	
				for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
					
					//리턴 recordSet 생성
					rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
					//재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
					intRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C", rsResult);
					if(intRtnVal != 1) {
						if( intRtnVal == 0 ) {
							intRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "U", rsResult);
							if(intRtnVal != 1) {
								szMsg="["+szOperationName+"] 적치단에 재료["+szSTL_NO[Loop_i]+"]가 존재하지 않습니다.";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
								throw new DAOException(szMsg);
							}
						}else{
							szMsg="["+szOperationName+"] 적치단에 재료["+szSTL_NO[Loop_i]+"] 조회 시 오류발생";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							throw new DAOException(szMsg);
						}
						//return YdConstant.RETN_CD_FAILURE;
					}
					
					//레코드추출
					rsResult.first();
					recStkPara = rsResult.getRecord();
					
					//재료번호
					recPara.setField("STL_NO", 		   szSTL_NO[Loop_i]);
					//적치열구분
					recPara.setField("YD_STK_COL_GP",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_COL_GP"));
					//적치BED번호
					recPara.setField("YD_STK_BED_NO",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_BED_NO"));
					//적치단번호
					recPara.setField("YD_STK_LYR_NO",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_LYR_NO"));
					//권상모음순서
					recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ[Loop_i]);
					
					// 작업예약재료 테이블에 등록한다.
					intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
					
					if(intRtnVal < 1){
						szMsg = "["+szOperationName+"] 작업예약재료 데이터 등록 중 에러";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return YdConstant.RETN_CD_FAILURE;
					}
				}
				
			} //대상재가 일반 야드에 적치되어 있는 경우에만 작업예약에 등록 if end
			szMsg="["+szOperationName+"] 통합야드인 경우 작업예약 등록 후 차량이송준비스케줄["+szYD_PREP_SCH_ID+"]이 존재하는 지 확인";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			if( !szYD_PREP_SCH_ID.equals("") ) {
				/*
				 * 업무기준 : 통합야드인 경우 차량이송준비스케줄ID가 파라미터로 전달되는 경우에는 삭제처리 필요
				 * 업무기준 변경 : 차량이송준비스케줄 실제 삭제처리 대신 DEL_YN항목에 Y 설정
				 * 				작업예약 삭제 시 다시 차량이송준비스케줄의 DEL_YN항목에 N 설정되도록 처리
				 * 수정자 : 임춘수
				 * 수정일 : 2009.10.14
				 */
				szMsg="["+szOperationName+"] 통합야드인 경우 작업예약 등록 후 차량이송준비스케줄["+szYD_PREP_SCH_ID+"] 삭제 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				String szReturnMsg = YdCommonUtils.deletePreSch(szYD_PREP_SCH_ID, szYD_WBOOK_ID, szMethodName);
				
//				YdPrepMtlDao ydPrepMtlDao = new YdPrepMtlDao();
//				
//				recPara         = JDTORecordFactory.getInstance().create();
//				recPara.setField("YD_PREP_SCH_ID",   	szYD_PREP_SCH_ID);
//				recPara.setField("DEL_YN",   			"Y");
//				recPara.setField("MODIFIER",   			szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
//				//준비재료 삭제처리
//				intRtnVal = ydPrepMtlDao.uptDelYdPrepmtlByPrepSchId(recPara);
//				
//				YdPrepSchDao ydPrepSchDao = new YdPrepSchDao();
//				
//				//준비스케줄 삭제처리
//				recPara.setField("YD_WBOOK_ID",   		szYD_WBOOK_ID);
//				intRtnVal =  ydPrepSchDao.updDelYdPrepsch(recPara);
				
				szMsg="["+szOperationName+"] 통합야드인 경우 작업예약 등록 후 차량이송준비스케줄["+szYD_PREP_SCH_ID+"] 삭제, 작업예약ID["+szYD_WBOOK_ID+"] 등록 성공 : " + szReturnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				if( !szYD_CRN_EQP_ID.equals("")) {
					/*
					 * 업무기준 : 크레인의 현재동에 존재하는 이송준비스케줄을 작업예약에 등록 후 해당크레인의 배차실적을 증가시킴
					 * 수정자 : 임춘수
					 * 수정일 : 2009.10.14
					 */
					szMsg="["+szOperationName+"] 통합야드인 경우 해당크레인["+szYD_CRN_EQP_ID+"]의 배차실적을 증가시킴";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					YdEqpDao ydEqpDao = new YdEqpDao();
					recPara         = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_EQP_ID",   	szYD_CRN_EQP_ID);
					intRtnVal = ydEqpDao.updYdEqpDirect(recPara, 1);
					if( intRtnVal > 0 ) {
						szMsg="["+szOperationName+"] 통합야드인 경우 해당크레인["+szYD_CRN_EQP_ID+"]의 배차실적을 증가 성공";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}else{
						szMsg="["+szOperationName+"] 통합야드인 경우 해당크레인["+szYD_CRN_EQP_ID+"]의 배차실적을 증가 실패 - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					
					/*
					 * 크레인의 배차기준을 조회해서 모든 크레인의 배차대수와 배차실적이 동일한 경우에는 모든 크레인의 배차실적을 0으로 설정한다.
					 * 
					 */
					szMsg="["+szOperationName+"] 크레인의 배차기준을 조회 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					rsResult = JDTORecordFactory.getInstance().createRecordSet("");
					recPara  = JDTORecordFactory.getInstance().create();
					
					//야드구분
//					recPara.setField("YD_GP", YdConstant.YD_GP_INTGR_YARD);
//					recPara.setField("YD_BAY_GP", "");
//					
//					//설비 테이블 조회
//					intRtnVal = ydEqpDao.getYdEqp(recPara, rsResult, 10);
//					
//					if( intRtnVal == 0 ) {
//						szMsg="[차량상차 작업요구] 모든 크레인의 배차대수와 배차실적이 동일하므로 모든크레인의 배차실적을 0으로 설정 시작";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//						recPara         = JDTORecordFactory.getInstance().create();
//						recPara.setField("YD_GP",   	YdConstant.YD_GP_INTGR_YARD);
//						intRtnVal = ydEqpDao.updYdEqpDirect(recPara, 2);
//						
//						szMsg="[차량상차 작업요구] 모든 크레인의 배차대수와 배차실적이 동일하므로 모든크레인의 배차실적을 0으로 설정 완료";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//					}else if( intRtnVal > 0 ) {
//						szMsg="[차량상차 작업요구] 크레인의 배차대수와 배차실적이 다른 크레인이 존재하므로 모든 크레인의 배차실적을 0으로 설정을 하지 않음";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//					}
					/*
					 * 업무기준 : 1. 배차대수가 0보다 크고 우선순위가 낮은 순으로 배차기준을 조회 후 루프
					 * 				1-1. 크레인의 현재동의 작업가능한 LOT 수가 존재하는 지 확인
					 * 					1-1-1. 0이면 다음 크레인으로 루프 진행
					 * 					1-1-2. 0이상이면
					 * 						1-1-2-1. 배차대수와 배차실적이 같으면 모든 크레인의 배차실적을 0으로 설정
					 * 						1-1-2-2. 배차대수와 배차실적이 같지않으면 skip
					 * 						1-1-2-3. 루프 종료
					 * 배차대수와 배차실적이 같은 지를 비교하여 모든 배차실적을 0으로 설정
					 * 수정자 : 임춘수
					 * 수정일 : 2009.10.15
					 */
					recPara.setField("YD_GP", YdConstant.YD_GP_INTGR_YARD);
					recPara.setField("YD_BAY_GP", "");
					recPara.setField("YD_PREP_WK_ST", "L");
					//설비 테이블 조회
					intRtnVal = ydEqpDao.getYdEqp(recPara, rsResult, 11);
					
					if( intRtnVal > 0 ) {
						long lngYD_CRN_CONT_CARASGN_CNT = 0;
						long lngYD_CRN_CONT_CARASGN_WR 	= 0;
						long lngYD_LOT_CNT_OF_BAY 		= 0;
						String szYD_CURR_BAY_GP			= null;
						String szYD_CRN_USE_SEQ			= null;
						for(int i = 1; i <= rsResult.size(); i++ ) {
							rsResult.absolute(i);
							recPara = rsResult.getRecord();
							
							szYD_CRN_EQP_ID = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_ID");								//크레인설비ID
							szYD_CURR_BAY_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_CURR_BAY_GP");							//현재동
							szYD_CRN_USE_SEQ = ydDaoUtils.paraRecChkNull(recPara, "YD_CRN_USE_SEQ");							//크레인작업순위
							lngYD_CRN_CONT_CARASGN_CNT = ydDaoUtils.paraRecChkNullLong(recPara, "YD_CRN_CONT_CARASGN_CNT");		//배차대수
							lngYD_CRN_CONT_CARASGN_WR 	= ydDaoUtils.paraRecChkNullLong(recPara, "YD_CRN_CONT_CARASGN_WR");		//배차실적
							lngYD_LOT_CNT_OF_BAY	= ydDaoUtils.paraRecChkNullLong(recPara, szYD_CURR_BAY_GP + "_FRTOMOVE_LOT_CNT");	//현재동의 작업가능한 LOT 수
							
							szMsg="[차량상차 작업요구] 우선순위["+szYD_CRN_USE_SEQ+"]가 가장 낮은 크레인["+szYD_CRN_EQP_ID+"] 배차대수["+lngYD_CRN_CONT_CARASGN_CNT+"]와 배차실적["+lngYD_CRN_CONT_CARASGN_WR+"]";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							
							szMsg="[차량상차 작업요구] 현재동["+szYD_CURR_BAY_GP+"]의 작업가능한 LOT 수["+lngYD_LOT_CNT_OF_BAY+"]";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							
							if( lngYD_LOT_CNT_OF_BAY == 0 ) {
								if( i < rsResult.size() ) {
									szMsg="[차량상차 작업요구] 현재동["+szYD_CURR_BAY_GP+"]의 작업가능한 LOT 수["+lngYD_LOT_CNT_OF_BAY+"]가 0이므로 다음 우선순위가 낮은 크레인을 찾기 위해 CONTINUE";
									ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
									continue;
								}else{
									szMsg="[차량상차 작업요구] 현재동["+szYD_CURR_BAY_GP+"]의 작업가능한 LOT 수["+lngYD_LOT_CNT_OF_BAY+"]가 0이므로 우선순위가 낮은 마지막 크레인이므로 배차 실적 0으로 설정 시작 함";
									ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
									recPara         = JDTORecordFactory.getInstance().create();
									recPara.setField("YD_GP",   	YdConstant.YD_GP_INTGR_YARD);
									intRtnVal = ydEqpDao.updYdEqpDirect(recPara, 2);
									break;
								}
							}
							
							szMsg="[차량상차 작업요구] 우선순위["+szYD_CRN_USE_SEQ+"]가 가장 낮은 크레인["+szYD_CRN_EQP_ID+"] 배차대수["+lngYD_CRN_CONT_CARASGN_CNT+"]와 배차실적["+lngYD_CRN_CONT_CARASGN_WR+"]이 같은 경우에는 모든크레인의 배차실적을 0으로 설정 시작";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							
							if( lngYD_CRN_CONT_CARASGN_CNT == lngYD_CRN_CONT_CARASGN_WR ) {
							
								szMsg="[차량상차 작업요구] 우선순위가 가장 낮은 크레인 배차대수["+lngYD_CRN_CONT_CARASGN_CNT+"]와 배차실적["+lngYD_CRN_CONT_CARASGN_WR+"]이 동일하므로 모든크레인의 배차실적을 0으로 설정 시작";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								recPara         = JDTORecordFactory.getInstance().create();
								recPara.setField("YD_GP",   	YdConstant.YD_GP_INTGR_YARD);
								intRtnVal = ydEqpDao.updYdEqpDirect(recPara, 2);
								
								szMsg="[차량상차 작업요구] 우선순위가 가장 낮은 크레인 배차대수["+lngYD_CRN_CONT_CARASGN_CNT+"]와 배차실적["+lngYD_CRN_CONT_CARASGN_WR+"]이 동일하므로 모든크레인의 배차실적을 0으로 설정 완료";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							}else{
								szMsg="[차량상차 작업요구] 우선순위가 가장 낮은 크레인 배차대수["+lngYD_CRN_CONT_CARASGN_CNT+"]와 배차실적["+lngYD_CRN_CONT_CARASGN_WR+"]이 동일하지 않으므로 모든크레인의 배차실적을 0으로 설정하지 않음";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							}
							
							break;
						}
					}else if( intRtnVal == 0 ) {
						szMsg="[차량상차 작업요구] 크레인의 배차대수와 배차실적이 다른 크레인이 존재하므로 모든 크레인의 배차실적을 0으로 설정을 하지 않음";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
				}
			}
			
			//record 생성
			recPara = JDTORecordFactory.getInstance().create();
			//설비ID - 차량설비ID
			recPara.setField("YD_EQP_ID",    szYD_EQP_ID);
			//차량사용구분
			recPara.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
			//운송장비코드
			recPara.setField("TRN_EQP_CD",    szTRN_EQP_CD);
			//개소코드
			recPara.setField("WLOC_CD",       szWLOC_CD);
			//운송작업영공구분코드
			recPara.setField("SP_TRUCK_LOADING_LOC_TP", szSP_TRUCK_LOADING_LOC_TP);
			//포인트요구일시
			recPara.setField("PNT_DMD_DT",    szPNT_DMD_DT);
			//발지개소코드
			recPara.setField("SPOS_WLOC_CD",  szSPOS_WLOC_CD);
			//발지포인트코드
			recPara.setField("SPOS_YD_PNT_CD", szSPOS_YD_PNT_CD);
			//직상차 구분 - 임춘수 2009.04.23 추가
			recPara.setField("YD_DIRECT_CARLD_GP", szYD_DIRECT_CARLD_GP);
			//이송적치열 - 임춘수 2009.04.23 추가
			recPara.setField("YD_FTMV_COL", szYD_FTMV_COL);
			//상차작업예약ID 20090617.김진욱
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
			//스케줄코드 20090617.김진욱
			recPara.setField("YD_SCH_CD",   szYD_SCH_CD);
			
			
			
			//스케줄 메소드 호출
			if( !szYD_CAR_SCH_YN.equals("N")){
				intRtnVal = this.mkY1CarSch(recPara);
				if( szIS_EJB_CALL.equals("Y") ){
					if( intRtnVal != 1 ){
						szMsg = "[차량상차 작업요구]차량스케줄 생성 시 예외발생[리턴처리] : 리턴값 =" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return YdConstant.RETN_CD_FAILURE;
					}
				}else{
					//테스트용 
					//intRtnVal = -1;
					if( intRtnVal != 1 ){
						szMsg = "[차량상차 작업요구]차량스케줄 생성 시 예외발생[DAOException Throw] : 리턴값 =" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						throw new DAOException(YdConstant.RETN_CD_FAILURE);
					}
				}
			}
		
		} catch(Exception e){
			szMsg = "["+szOperationName+"]  처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			if( szIS_EJB_CALL.equals("Y") ){
				return YdConstant.RETN_CD_FAILURE;
			}else{
				throw new DAOException(e.getMessage());
			}
		}

		return YdConstant.RETN_CD_SUCCESS;
	} // end of procCCsCarLdWrkReq()
	
	/**
	 * 배차정보로 차량스케줄 생성
	 * @param msgRecord
	 * @return String
	 */
	public String procCarSchForDist(JDTORecord msgRecord){
		String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		String szMethodName = "procCarSchForDist";
		String szLogMsg = null;
		String szYD_EQP_ID = null;
		String szTRN_EQP_CD = null;
		String szYD_CAR_USE_GP = null;
		//String szSPOS_WLOC_CD = null;
		String szCAR_NO = null;
		String szCARD_NO = null;
		String szYD_CAR_SCH_ID = null;
		int intRtnVal = -1;
		
		YdCarSchDao ydCarSchDao = new YdCarSchDao();
		try {
			//1. 전문확인
	    	szYD_EQP_ID               = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
	    	szTRN_EQP_CD              = ydDaoUtils.paraRecChkNull(msgRecord, "TRN_EQP_CD"); 
	    	szYD_CAR_USE_GP           = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_USE_GP");
	    	szCAR_NO                  = ydDaoUtils.paraRecChkNull(msgRecord, "CAR_NO");
	    	szCARD_NO                 = ydDaoUtils.paraRecChkNull(msgRecord, "CARD_NO");
	    	//2. 차량스펙에 데이타 등록 또는 수정 필요
	    	
			//3. 차량스케줄생성
	    	szYD_CAR_SCH_ID = ydCarSchDao.getYdCarschId();
	    	msgRecord.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
	    	
	    	if(szYD_CAR_USE_GP.equals("G")) {
	    		msgRecord.setField("CAR_KIND","TR");
			}else{
				msgRecord.setField("CAR_KIND","PT");
			}
	    	intRtnVal = ydCarSchDao.insYdCarsch(msgRecord);
			//4. 상차LOT모듈 호출 - 파라미터를 넘겨받아서 호출결정
	    	
		}catch(JDTOException ex){
			szLogMsg = "["+szMethodName+"] 차량스케줄등록 시 오류발생 - " + ex.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			throw new DAOException(ex);
		}
		return szRtnMsg;
	}
	
	
	/**
	 * 오퍼레이션명 : 차량스케줄생성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
		
		public int mkY1CarSch(JDTORecord msgRecord)throws JDTOException  {
		YdDelegate  ydDelegate  = new YdDelegate();
		YdCarSchDao ydCarSchDao = new YdCarSchDao();
		//YdDBAssist  ydDBAssist  = new YdDBAssist();
		YdStkColDao ydStkColDao = new YdStkColDao();
		//JDTORecordSet rsResult 		     = null;
		JDTORecordSet rsStkCol  		 = null;
		JDTORecord    recOutTemp		 = null;
		JDTORecord    recInTemp  		 = null;
		
		
	    int intRtnVal 		      		 = 0 ;
	    
	    String szMsg              		 = "";
	    String szMethodName       		 = "mkY1CarSch";
	    String szOperationName           = "차량스케줄생성";
	    
	    String szWLOC_CD          		 = "";
	    //String szYD_GP            		 = "";
	    //String szQuery            		 = ""; 
	    String szYD_EQP_ID        		 = ""; 
	    String szTRN_EQP_CD       		 = "";
	    String szYD_CAR_USE_GP    		 = "";
	    String szSPOS_WLOC_CD            = "";
	    String szSPOS_YD_PNT_CD          = "";
	    String szYD_CARLD_LEV_LOC        = "";
	    String szPNT_DMD_DT              = "";
	    //String szSP_TRUCK_LOADING_LOC_TP = "";
	    String szYD_WBOOK_ID             = "";
	    String szCAR_NO                  = "";
	    String szCARD_NO                 = "";
	    String szYD_CRN_EQP_ID           = "";
	    String szYD_SCH_CD               = "";
	    String szYD_CARLD_STOP_LOC       = "";
	    String szYD_DIRECT_CARLD_GP		 = "";
	    String szYD_FTMV_COL			 = "";
	    String szTRANS_ORD_DATE          = "";
	    String szTRANS_ORD_SEQNO         = "";
	    String szYD_CAR_SCH_ID			 = "";
	    
	    try{
	    	
	    	szMsg = "[차량스케줄생성] 메소드 시작 : 전문내용 보기";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	ydUtils.displayRecord(szOperationName, msgRecord);
	    	
    		//파라미터 편집
	    	szYD_EQP_ID               = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
	    	szTRN_EQP_CD              = ydDaoUtils.paraRecChkNull(msgRecord, "TRN_EQP_CD"); 
	    	szYD_CAR_USE_GP           = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_USE_GP");
	    	szWLOC_CD                 = ydDaoUtils.paraRecChkNull(msgRecord, "WLOC_CD");
	    	szSPOS_WLOC_CD            = ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_WLOC_CD");
	    	szSPOS_YD_PNT_CD          = ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_YD_PNT_CD");
	    	//szSP_TRUCK_LOADING_LOC_TP = ydDaoUtils.paraRecChkNull(msgRecord, "SP_TRUCK_LOADING_LOC_TP");
	    	szPNT_DMD_DT              = ydDaoUtils.paraRecChkNull(msgRecord, "PNT_DMD_DT");
	    	szYD_WBOOK_ID             = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");     
	    	szCAR_NO                  = ydDaoUtils.paraRecChkNull(msgRecord, "CAR_NO");
	    	szCARD_NO                 = ydDaoUtils.paraRecChkNull(msgRecord, "CARD_NO");
	    	szYD_SCH_CD               = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
	    	szTRANS_ORD_DATE          = ydDaoUtils.paraRecChkNull(msgRecord, "TRANS_ORD_DATE");
	    	szTRANS_ORD_SEQNO         = ydDaoUtils.paraRecChkNull(msgRecord, "TRANS_ORD_SEQNO");
	    	
	    	
	    	
	    	//직상차구분 : C - 직상차, Y - 일반야드, B - 대상재 존재하지 않음 ---- 임춘수 2009.04.23 추가
			szYD_DIRECT_CARLD_GP 		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_DIRECT_CARLD_GP");
			//이송적치열
			szYD_FTMV_COL 				= ydDaoUtils.paraRecChkNull(msgRecord, "YD_FTMV_COL");
	    	
			szMsg = "[전문 내용] 직상차구분[szYD_DIRECT_CARLD_GP - " + szYD_DIRECT_CARLD_GP + " ]" + ", 이송적치열 [" + szYD_FTMV_COL + "]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
	    	if(szYD_CAR_USE_GP.equals("G")){
	    		szYD_CRN_EQP_ID     = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_EQP_ID"); 
	    		szYD_SCH_CD         = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
	    		szYD_CARLD_STOP_LOC = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CARLD_STOP_LOC");
	    	}
	    	
	    	
	    	//발지개소코드를 변환, 발지개소Point를 변환(출발지 위치)
	    	rsStkCol = JDTORecordFactory.getInstance().createRecordSet("");
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("WLOC_CD",   szSPOS_WLOC_CD);
	    	recInTemp.setField("YD_PNT_CD", szSPOS_YD_PNT_CD);
	    	intRtnVal = ydStkColDao.getYdStkcol(recInTemp, rsStkCol, 4);
			if(intRtnVal <= 0){
				if(intRtnVal == 0){
					szMsg="data not found";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				}else if(intRtnVal == -2){
					szMsg="parameter error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
//				return intRtnVal = -1;
			}
	    	
	    	if(intRtnVal > 0){
		    	recOutTemp = JDTORecordFactory.getInstance().create();
		    	recOutTemp.setRecord(rsStkCol.getRecord(0));
		    	
			    //열구분을 조회(발지위치)
			    szYD_CARLD_LEV_LOC = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_COL_GP");
	    	}else{
		    	szMsg="등록되지 않은 개소코드와 포인트코드입니다. ex) A,B열연 OR 대기장";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				szYD_CARLD_LEV_LOC = "";
	    	}
	    	
	    	szYD_CAR_SCH_ID = ydCarSchDao.getYdCarschId();
	    	
	    	szMsg="차량스케줄ID 생성 시작 전";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
	    	//차량스케줄INSERT 항목
			recOutTemp = JDTORecordFactory.getInstance().create();
			recOutTemp.setField("YD_CAR_SCH_ID",    szYD_CAR_SCH_ID);
	    	recOutTemp.setField("REGISTER",         (szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName));
	    	recOutTemp.setField("YD_EQP_WRK_STAT",  "U");
	    	recOutTemp.setField("YD_EQP_ID",        szYD_EQP_ID);
	    	recOutTemp.setField("TRN_EQP_CD",       szTRN_EQP_CD);
	    	recOutTemp.setField("YD_CAR_USE_GP",    szYD_CAR_USE_GP);
	    	
	    	if(szYD_CAR_USE_GP.equals("G")) {
	    		recOutTemp.setField("CAR_KIND","TR");
			}else{
				recOutTemp.setField("CAR_KIND","PT");
			}
	    	
	    	recOutTemp.setField("WLOC_CD",          szWLOC_CD);
	    	//2009.05.12 임춘수 추가
	    	recOutTemp.setField("SPOS_WLOC_CD",          szWLOC_CD);
	    	//2009.11.17 임춘수 추가
	    	recOutTemp.setField("YD_BAYIN_WO_SEQ",  YdConstant.YD_BAYIN_WO_SEQ_DEFAULT);
	    	
	    	recOutTemp.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);
	    	recOutTemp.setField("CAR_NO",           szCAR_NO);
	    	recOutTemp.setField("CARD_NO",          szCARD_NO);
	    	recOutTemp.setField("YD_CAR_PROG_STAT", "1");//상차출발상태
	    	recOutTemp.setField("YD_CARLD_LEV_DT", YdUtils.getCurDate("yyyyMMddHHmmss"));//상차출발일시
	    	recOutTemp.setField("TRANS_ORD_DATE",           szTRANS_ORD_DATE);
	    	recOutTemp.setField("TRANS_ORD_SEQNO",          szTRANS_ORD_SEQNO);
	    	
	    	
	    	if(szYD_CAR_USE_GP.equals("G")){
	    		//차량상차정지위치를 등록한다.
	    		recOutTemp.setField("YD_CARLD_STOP_LOC", szYD_CARLD_STOP_LOC);
	    		recOutTemp.setField("YD_CARLD_ARR_DT", YdUtils.getCurDate("yyyyMMddHHmmss"));
	    		recOutTemp.setField("YD_CAR_PROG_STAT", "2");//상차도착상태
	    	}else if( szYD_CAR_USE_GP.equals("L") ){
	    		if( szYD_DIRECT_CARLD_GP.equals("B") ){
	    			recOutTemp.setField("YD_CARLD_LEV_LOC", szYD_CARLD_LEV_LOC);
	    			//if( szYD_SCH_CD.subSequence(0, 1).equals(YdConstant.YD_GP_INTGR_YARD)) {
    				/*
					 * 대상재가 없는 경우에도 0000 포인트코드를 전송한다.
					 */
    				//상차정지위치
    				recOutTemp.setField("YD_PNT_CD1",  "0000");
    				//상차point지시일시
    				recOutTemp.setField("YD_CARLD_PNT_WO_DT", YdUtils.getCurDate("yyyyMMddHHmmss"));
    				szMsg="대상재가 없는 경우에는 포인트코드[0000]로 수정 처리 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			//}
	    		}
	    		/*구내운송 - 임춘수 2009.04.23 추가*/
	    		else if(szYD_FTMV_COL.equals("")){
		    		recOutTemp.setField("YD_CARLD_STOP_LOC", szYD_SCH_CD.substring(0,6));	//작업예약에서 생성한 스케줄코드값을 잘라쓴다.
		    		recOutTemp.setField("YD_CARLD_LEV_LOC", szYD_CARLD_LEV_LOC);            //대기장이 아니라면 검색되기때문에 출발위치
	    		}else{
		    		recOutTemp.setField("YD_CARLD_STOP_LOC", szYD_FTMV_COL + "01");			//임의 값을 먼저 설정
		    		recOutTemp.setField("YD_CARLD_LEV_LOC", szYD_CARLD_LEV_LOC);
	    		}

	    	}
	    	
	    	//차량스케줄 등록
	    	intRtnVal = ydCarSchDao.insYdCarsch(recOutTemp);
    		if(intRtnVal == -2){
				szMsg="parameter error";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException("<mkY1CarSch> insYdCarsch처리중 parameter error");
    		}
    		
    		szMsg="차량스케줄ID 생성 완료";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    		
    		if(szYD_CAR_USE_GP.equals("L") ){							//구내운송
    			if( !szYD_DIRECT_CARLD_GP.equals("B") ){
		    		//소재차량정지Point요구 호출
					//record 생성
					recInTemp = JDTORecordFactory.getInstance().create();
					//JMS TC CODE
		    		recInTemp.setField("JMS_TC_CD",               "YDYDJ630");
					//운송장비코드
		    		recInTemp.setField("TRN_EQP_CD",              szTRN_EQP_CD);
					//개소코드
		    		recInTemp.setField("WLOC_CD",                 szWLOC_CD);
					//운송작업영공구분코드
		    		recInTemp.setField("TRN_WRK_FULLVOID_GP",     "E");
					//포인트요구일시
		    		recInTemp.setField("PNT_DMD_DT",              szPNT_DMD_DT);
		    		
					//전문 송신
					ydDelegate.sendMsg(recInTemp);
					
					szMsg="소재차량정지Point요구 호출 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			}else{
    				
    				// EJB CALL 변경
    				szMsg="대상재가 없는 경우에는 0000포인트코드를 전송 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    				
    				recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("JMS_TC_CD",        		"YDYDJ630");
					recInTemp.setField("TRN_EQP_CD", 			szTRN_EQP_CD);
					
					//ydDelegate.sendMsg(recInTemp);
					
					EJBConnector ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);
					ejbConn.trx("procMatlCarArrPntReq", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
					
					
					szMsg="대상재가 없는 경우에는 0000포인트코드를 전송 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
    				
    			}
    		}else{															//출하관리
    			//크레인 스케줄 송신
    			recInTemp = JDTORecordFactory.getInstance().create();
    			
    			if(szYD_SCH_CD.subSequence(0, 1).equals("A") ){
    				//JMS TC CODE (C연주)
    	    		recInTemp.setField("JMS_TC_CD", "YDYDJ500");
    	    		
    			}else if(szYD_SCH_CD.subSequence(0, 1).equals("D")){
    				//JMS TC CODE (A후판슬라브)
    	    		recInTemp.setField("JMS_TC_CD", "YDYDJ503");
    	    		
    			}else if(szYD_SCH_CD.subSequence(0, 1).equals("K")){
    				//JMS TC CODE (후판제품)
    	    		recInTemp.setField("JMS_TC_CD", "YDYDJ506");
    	    		
    			}else if(szYD_SCH_CD.subSequence(0, 1).equals("H") || szYD_SCH_CD.subSequence(0, 1).equals("J")){
    				//JMS TC CODE (C열연코일)
    	    		recInTemp.setField("JMS_TC_CD", "YDYDJ509");
    	    		
	    		}else if( szYD_SCH_CD.subSequence(0, 1).equals("S")){
    				//JMS TC CODE (통합)
    	    		recInTemp.setField("JMS_TC_CD", "YDYDJ512");
    	    		
    			}

	    		//스케줄코드
	    		recInTemp.setField("YD_SCH_CD", szYD_SCH_CD);
	    		//설비ID
	    		recInTemp.setField("YD_EQP_ID", szYD_CRN_EQP_ID);
	    		
				szMsg="크레인 스케줄 송신";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				ydUtils.displayRecord(szOperationName, recInTemp);
				//전문 송신
				ydDelegate.sendMsg(recInTemp);
    		}
			
		}catch(Exception e){
			
			szMsg="C연주 차량스케줄 생성 중 Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(e);
		}
	
	
		szMsg="C연주 차량스케줄 생성 ("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		return intRtnVal = 1;
	} //end of mkY1CarSch()
	
	
	
	
	
	
	
	
	/**
	 * 오퍼레이션명 : A후판차량스케줄생성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public int mkY3CarSch(JDTORecord msgRecord)throws JDTOException  {
		YdDelegate  ydDelegate  = new YdDelegate();
		YdCarSchDao ydCarSchDao = new YdCarSchDao();
		YdDBAssist  ydDBAssist  = new YdDBAssist();
		YdStkColDao ydStkColDao = new YdStkColDao();
		JDTORecordSet rsResult 		     = null;
		JDTORecordSet rsStkCol  		 = null;
		JDTORecord    recOutTemp		 = null;
		JDTORecord    recInTemp  		 = null;
		
		
	    int intRtnVal 		      		 = 0 ;
	    
	    String szMsg              		 = "";
	    String szMethodName       		 = "mkY3CarSch";
	    
	    String szWLOC_CD          		 = "";
	    String szYD_GP            		 = "";
	    String szQuery            		 = ""; 
	    String szYD_EQP_ID        		 = ""; 
	    String szTRN_EQP_CD       		 = "";
	    String szYD_CAR_USE_GP    		 = "";
	    String szSPOS_WLOC_CD            = "";
	    String szSPOS_YD_PNT_CD          = "";
	    String szYD_CARLD_LEV_LOC        = "";
	    String szPNT_DMD_DT              = "";
	    String szSP_TRUCK_LOADING_LOC_TP = "";
	    String szYD_WBOOK_ID             = "";
	    String szCAR_NO                  = "";
	    String szCARD_NO                 = "";
	    String szYD_CRN_EQP_ID           = "";
	    String szYD_SCH_CD               = "";
	    
	    try{
    		//파라미터 편집
	    	szYD_EQP_ID               = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
	    	szTRN_EQP_CD              = ydDaoUtils.paraRecChkNull(msgRecord, "TRN_EQP_CD"); 
	    	szYD_CAR_USE_GP           = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_USE_GP");
	    	szWLOC_CD                 = ydDaoUtils.paraRecChkNull(msgRecord, "WLOC_CD");
	    	szSPOS_WLOC_CD            = ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_WLOC_CD");
	    	szSPOS_YD_PNT_CD          = ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_YD_PNT_CD");
	    	szSP_TRUCK_LOADING_LOC_TP = ydDaoUtils.paraRecChkNull(msgRecord, "SP_TRUCK_LOADING_LOC_TP");
	    	szPNT_DMD_DT              = ydDaoUtils.paraRecChkNull(msgRecord, "PNT_DMD_DT");
	    	szYD_WBOOK_ID             = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");     
	    	szCAR_NO                  = ydDaoUtils.paraRecChkNull(msgRecord, "CAR_NO");
	    	szCARD_NO                 = ydDaoUtils.paraRecChkNull(msgRecord, "CARD_NO");
	    	
	    	if(szYD_CAR_USE_GP.equals("G")){
	    		szYD_CRN_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_EQP_ID"); 
	    		szYD_SCH_CD     = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
	    	}
	    	
	    	
	    	
	    	//발지개소코드를 변환, 발지개소Point를 변환(출발지 위치)
	    	rsStkCol = JDTORecordFactory.getInstance().createRecordSet("");
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("WLOC_CD",   szSPOS_WLOC_CD);
	    	recInTemp.setField("YD_PNT_CD", szSPOS_YD_PNT_CD);
	    	intRtnVal = ydStkColDao.getYdStkcol(recInTemp, rsStkCol, 4);
			if(intRtnVal <= 0){
				if(intRtnVal == 0){
					szMsg="data not found";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				}else if(intRtnVal == -2){
					szMsg="parameter error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
//				return intRtnVal = -1;
			}
	    	
		    //열구분을 조회(발지위치)
		    szYD_CARLD_LEV_LOC = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_COL_GP");
		    
		    //차량스케줄ID 생성
//	    	szQuery  = " SELECT TO_CHAR(SYSDATE,'YYYYMMDDHH24MI') ||  LPAD(YD_CARSCH_SEQ.nextval,6,'0') AS YD_CAR_SCH_ID ";
//	    	szQuery += "   FROM DUAL ";
//	    	szQuery += "  WHERE '1' = '1' ";
//	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("");
//	    	intRtnVal = ydDBAssist.getData(szQuery, rsResult, null);
//	    	rsResult.absolute(1);
	    	recOutTemp = JDTORecordFactory.getInstance().create();
//	    	recOutTemp.setRecord(rsResult.getRecord());
	    	
	    	//차량스케줄INSERT 항목
	    	recOutTemp.setField("REGISTER",         "SYSTEM");
	    	recOutTemp.setField("DEL_YN",           "N");
	    	recOutTemp.setField("YD_EQP_WRK_STAT",  "U");
	    	recOutTemp.setField("YD_EQP_ID",        szYD_EQP_ID);
	    	recOutTemp.setField("TRN_EQP_CD",       szTRN_EQP_CD);
	    	recOutTemp.setField("YD_CAR_USE_GP",    szYD_CAR_USE_GP);
	    	
	    	if(szYD_CAR_USE_GP.equals("G")) {
	    		recOutTemp.setField("CAR_KIND","TR");
			}else{
				recOutTemp.setField("CAR_KIND","PT");
			}
	    	recOutTemp.setField("WLOC_CD",          szWLOC_CD);
	    	recOutTemp.setField("YD_CARLD_LEV_LOC", szYD_CARLD_LEV_LOC);
	    	//2009.11.17 임춘수 추가
	    	recOutTemp.setField("YD_BAYIN_WO_SEQ",  YdConstant.YD_BAYIN_WO_SEQ_DEFAULT);
	    	
	    	recOutTemp.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);
	    	recOutTemp.setField("CAR_NO",           szCAR_NO);
	    	recOutTemp.setField("CARD_NO",          szCARD_NO);
	    	
	    	
	    	
	    	//차량스케줄 등록
	    	intRtnVal = ydCarSchDao.insYdCarsch(recOutTemp);
    		if(intRtnVal == -2){
				szMsg="parameter error";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException("<mkY3CarSch> insYdCarsch처리중 parameter error");
    		}
    		
    		if(szYD_CAR_USE_GP.equals("L")){
	    		//A후판 소재차량정지Point요구 호출
				//record 생성
				recInTemp = JDTORecordFactory.getInstance().create();
				//JMS TC CODE
	    		recInTemp.setField("JMS_TC_CD",               "YDYDJ631");
				//운송장비코드
	    		recInTemp.setField("TRN_EQP_CD",              szTRN_EQP_CD);
				//개소코드
	    		recInTemp.setField("WLOC_CD",                 szWLOC_CD);
				//운송작업영공구분코드
	    		recInTemp.setField("SP_TRUCK_LOADING_LOC_TP", szSP_TRUCK_LOADING_LOC_TP);
				//포인트요구일시
	    		recInTemp.setField("PNT_DMD_DT",              szPNT_DMD_DT);
	    		
				//전문 송신
				ydDelegate.sendMsg(recInTemp);
    		}else{
    			//크레인 스케줄 송신
    			recInTemp = JDTORecordFactory.getInstance().create();
				//JMS TC CODE
	    		recInTemp.setField("JMS_TC_CD", "YDYDJ503");
	    		//스케줄코드
	    		recInTemp.setField("YD_SCH_CD", szYD_SCH_CD);
	    		//설비ID
	    		recInTemp.setField("YD_EQP_ID", szYD_CRN_EQP_ID);
	    		
	    		
	    		//전문 송신
				ydDelegate.sendMsg(recInTemp);
    		}
			
		}catch(Exception e){
			
			szMsg="A후판 차량스케줄 생성 중 Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(e);
		}
	
	
		szMsg="A후판 차량스케줄 생성 ("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		return intRtnVal = 1;
	} //end of mkY3CarSch()
	
	
	
	
	
	
	/**
	 * 오퍼레이션명 : 외판출하상차LOT편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String procCCsOutplDistCarLdlotComp(JDTORecord msgRecord) throws JDTOException  {
		
		YdDelegate ydDelegate          = new YdDelegate();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils          = new YdDaoUtils();
		//공용 METHOD
		YdUtils ydutils                = new YdUtils();
		
		//레코드 선언
		JDTORecord recPara             = null;
		JDTORecord recOutPara          = null;
		//레코드셋 선언
		JDTORecordSet rsResult         = null;
		
		//리턴값(boolean)
		boolean blnRtnVal              = false;
		//리턴값(int)
		int intRtnVal                  = 0;
		//메세지
		String szMsg                   = "";
		//메소드명
		String szMethodName            = "procCCsOutplDistCarLdlotComp";
		//사용자
		String szUser                  = "SYSTEM";

		//전문 생성 일시
		String szDate                  = "";

		//설비ID
		String szYD_EQP_ID             = "";
		//상차정지위치
		String szYD_CARLD_STOP_LOC     = "";
		//차량사용구분
		String szYD_CAR_USE_GP         = "";
		//차량번호
		String szCAR_NO                = "";
		//카드번호
		String szCARD_NO               = "";
		//재료품목
		String szYD_MTL_ITEM           = "";
		//스케줄코드
		String szYD_SCH_CD             = "";
		//차량이송재료매수
		int intYD_CARLD_SH             = 0;
		//차량작업허용매수
		int intYD_WRK_ALW_SH           = 1;
//		//대상 재료 중량 합계
//		long lngSumMtlWt               = 0;
//		//차량작업허용중량
//		long lngYD_WRK_ALW_WT          = 0;

		String szTRANS_ORD_DATE        = "";
		String szTRANS_ORD_SEQNO       = "";
		String szYD_GP                 = "";
		String szYD_BAY_GP             = "";
		String szSPOS_WLOC_CD          = "";
		String szSPOS_YD_PNT_CD        = "";
		String szYD_EQP_GP             = "";
		String szYD_WRK_ALW_L          = "";
		String szYD_WRK_ALW_W          = "";
		String szYD_WRK_ALW_SKID_PITCH = "";
		String szYD_WRK_ALW_SH         = "";
		String szYD_WRK_ALW_WT         = "";
		String szSP_TRUCK_LOADING_LOC_TP = "";
		
		String szRtnVal				= "";
		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if(szRcvTcCode == null){
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_TC_ERROR;
		}

		//TC CODE DISPLAY
		if(bDebugFlag){
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		try {
			
			//야드구분
			szYD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP");
			if(szYD_GP.equals("")){
				szMsg = "[전문 이상] 야드구분 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}
			
			// PIDEV
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "", "APPPI0", szYD_GP, "*");					
			
			//받은 전문 편집
			//상차정지위치
			szYD_CARLD_STOP_LOC = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CARLD_STOP_LOC");
			if(szYD_CARLD_STOP_LOC.equals("")){
				
				szMsg = "[전문 이상] 상차정지위치가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			//차량사용구분
			szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_USE_GP");
			if(szYD_CAR_USE_GP.equals("") || !szYD_CAR_USE_GP.equals("G")){
				
				szMsg = "[전문 이상] 차량사용구분(" + szYD_CAR_USE_GP + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			//차량번호
			szCAR_NO = ydDaoUtils.paraRecChkNull(msgRecord, "CAR_NO");
			if(szCAR_NO.equals("")){
				
				szMsg = "[전문 이상] 차량번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			
			// PIDEV
//			if("N".equals(sApplyYnPI)) {			
//
//				//카드번호
//				szCARD_NO = ydDaoUtils.paraRecChkNull(msgRecord, "CARD_NO");				
//				
//				if(szCARD_NO.equals("")){
//					
//					szMsg = "[전문 이상] 카드번호가 없습니다.";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//					return YdConstant.RETN_CD_NO_PARAM;
//					
//				}
//			
//			}
			//재료품목
			/*szYD_MTL_ITEM = ydDaoUtils.paraRecChkNull(msgRecord, "YD_MTL_ITEM");
			if(szYD_MTL_ITEM.equals("")){
				szMsg = "[전문 이상] 재료품목이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}*/
	
			//운송지시일자
			szTRANS_ORD_DATE = ydDaoUtils.paraRecChkNull(msgRecord, "TRANS_ORD_DATE");
			if(szTRANS_ORD_DATE.equals("")){
				szMsg = "[전문 이상] 운송지시일자 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}			
			
			//운송지시순번
			szTRANS_ORD_SEQNO = ydDaoUtils.paraRecChkNull(msgRecord, "TRANS_ORD_SEQNO");
			if(szTRANS_ORD_SEQNO.equals("")){
				szMsg = "[전문 이상] 운송지시순번 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}
			

			
			//동구분
			szYD_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP");
			if(szYD_BAY_GP.equals("")){
				szMsg = "[전문 이상] 동구분 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}
			
			//발지개소코드
			szSPOS_WLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_WLOC_CD");
			if(szSPOS_WLOC_CD.equals("")){
				szMsg = "[전문 이상] 발지개소코드 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}
			
			//발지야드포인트코드
			szSPOS_YD_PNT_CD = ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_YD_PNT_CD");
			if(szSPOS_YD_PNT_CD.equals("")){
				szMsg = "[전문 이상] 발지야드포인트코드 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}
			
			
			if(szYD_CAR_USE_GP.equals("L")){
				//운송작업영공구분코드
				szSP_TRUCK_LOADING_LOC_TP = ydDaoUtils.paraRecChkNull(msgRecord, "SP_TRUCK_LOADING_LOC_TP");
				if(szSP_TRUCK_LOADING_LOC_TP.equals("")){
					szMsg = "[전문 이상] 운송작업영공구분코드가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;
				}

			}else{
				//작업가능허용매수
				intYD_WRK_ALW_SH = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_WRK_ALW_SH");
				if(intYD_WRK_ALW_SH <= 0){
					szMsg = "[전문 이상] 작업가능허용매수가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//					return YdConstant.RETN_CD_NO_PARAM;
				}	
			}
			
			

			

			//=================================================================================
			//수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			//BRE 등록 안됨...테스트용 스케줄코드 생성
			//추후 구현..
			//szYD_SCH_CD  = "JATR01UM";
			szYD_SCH_CD  = szYD_CARLD_STOP_LOC.trim() + "UM";
			//=================================================================================
				
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//상차 Lot 편성 대상 재료 Select
			blnRtnVal = chkDistCarLoadLotGp(msgRecord, rsResult);
			if(!blnRtnVal) return YdConstant.RETN_CD_FAILURE;	
			
			
			
			if(intYD_WRK_ALW_SH > rsResult.size() || intYD_WRK_ALW_SH == 0){
				intYD_WRK_ALW_SH = rsResult.size();
			}
			
			//레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			//JMS TC CODE
			recOutPara.setField("JMS_TC_CD",          "YDYDJ248");
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//차량 이송 가능 매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			if(szYD_CAR_USE_GP.equals("L")){
				intYD_WRK_ALW_SH = intYD_WRK_ALW_SH;
			}
			
			//현재는 상차가 1매만되기때문에 1매만 나오도록 되도록...
//			for (int Loop_i = 1; Loop_i <= intYD_WRK_ALW_SH; Loop_i++){
			for (int Loop_i = 1; Loop_i <= intYD_WRK_ALW_SH; Loop_i++){
				
//				//대상 재료 중량 합계
//				lngSumMtlWt = lngSumMtlWt + Long.parseLong(ydDaoUtils.paraRecChkNull(recPara, "YD_MTL_WT"));
//				//차량작업허용중량보다 대상재료들의 중량이 많으면 편성 중지
//				if(lngYD_WRK_ALW_WT < lngSumMtlWt) break;
				//재료번호
				recOutPara.setField("STL_NO" + Loop_i, ydDaoUtils.paraRecChkNull(recPara, "STL_NO"));
				//권상모음순서
				recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, "" + Loop_i);
				//차량이송재료매수
				intYD_CARLD_SH = Loop_i;
				
				//다음 레코드 추출
				rsResult.next();
				recPara = rsResult.getRecord();
				
			}
			
			szYD_EQP_ID = YdConstant.YD_DM_CAR_EQP_ID;
			
			//전문 발생 일시
			szDate = YdUtils.getCurDate("yyyy/MM/dd HH:mm:ss");

			//발생 일시
			recOutPara.setField("JMS_TC_CREATE_DDTT", szDate);
			//스케줄코드
			recOutPara.setField("YD_SCH_CD",          szYD_SCH_CD);
			//CARRY_IN 재료매수
			recOutPara.setField("YD_CARLD_SH",        "" + intYD_CARLD_SH);
			//적치열구분
			recOutPara.setField("YD_STK_COL_GP",      szYD_CARLD_STOP_LOC);   
			// 설비id
			recOutPara.setField("YD_EQP_ID",          szYD_EQP_ID);
			//적치BED번호
			recOutPara.setField("YD_STK_BED_NO",      "01");
			//목표행선구분
			recOutPara.setField("YD_AIM_RT_GP",       "JA");
			//차량사용구분 
			recOutPara.setField("YD_CAR_USE_GP",      szYD_CAR_USE_GP);
			//상차 정지위치
			recOutPara.setField("YD_CARLD_STOP_LOC",      szYD_CARLD_STOP_LOC);
			//차량번호
			recOutPara.setField("CAR_NO",      szCAR_NO);
			//카드번호 
			recOutPara.setField("CARD_NO",      szCARD_NO);
			//운송지시일자 
			recOutPara.setField("TRANS_ORD_DATE",      szTRANS_ORD_DATE);
			//운송지시순번 
			recOutPara.setField("TRANS_ORD_SEQNO",      szTRANS_ORD_SEQNO);
			//야드구분 
			recOutPara.setField("YD_GP",      szYD_GP);
			//동구분 
			recOutPara.setField("YD_BAY_GP",      szYD_BAY_GP);
			//발지개소코드 
			recOutPara.setField("SPOS_WLOC_CD",      szSPOS_WLOC_CD);
			//발지포인트코드 
			recOutPara.setField("SPOS_YD_PNT_CD",      szSPOS_YD_PNT_CD);
			//설비구분
			//recOutPara.setField("YD_EQP_GP",      szYD_EQP_GP);
			//야드작업허용길이 
//			recOutPara.setField("YD_WRK_ALW_L",      szYD_WRK_ALW_L);
//			//야드작업허용폭 
//			recOutPara.setField("YD_WRK_ALW_W",      szYD_WRK_ALW_W);
//			//야드작업허용Skid간격 
//			recOutPara.setField("YD_WRK_ALW_SKID_PITCH",      szYD_WRK_ALW_SKID_PITCH);
//			//야드작업허용매수 
//			recOutPara.setField("YD_WRK_ALW_SH",      szYD_WRK_ALW_SH);				
//			//야드작업허용중량 
//			recOutPara.setField("YD_WRK_ALW_WT",      szYD_WRK_ALW_WT);
			//운송작업영공구분코드
			recOutPara.setField("SP_TRUCK_LOADING_LOC_TP", szSP_TRUCK_LOADING_LOC_TP);
			
			szRtnVal = this.procCCsOutCarLdWrkReq(recOutPara);
			szMsg = "외판출하 상차 LOT 편성 후 외판출하 차량상차 작업요구 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			

			
		} catch(Exception e){
			szMsg = "외판출하 상차 LOT 편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;
		}
		return szRtnVal;
	} //end of procCCsOutplDistCarLdlotComp
	
	
	/**
	 * 오퍼레이션명 : 코일 매뉴얼 작업 Lot편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procCoilManualWrkLotComp (JDTORecord msgRecord) throws JDTOException  {
		//레코드 선언
		JDTORecord recPara     = null;
		JDTORecord recOutPara  = null;
		JDTORecord recResult     = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		
		//리턴값(boolean)
		boolean blnRtnVal      = false;
		int intRtnVal			= 0;
		//메세지
		String szMsg           = "";
		//메소드명
		String szMethodName    = "procCoilManualWrkLotComp";
		String szOperationName = "코일 매뉴얼 작업 Lot편성";

		//목표행선구분
		String szYD_AIM_RT_GP		= null;
		//야드구분
		String szYD_GP     = null;
		//동구분
		String szYD_BAY_GP     = null;
		//재료번호
		String szSTL_NO				= null;
		//스케줄코드
		String szYD_SCH_CD         = null;
		//대상 재료 중량 합계
		long lngSumYD_MTL_WT           = 0;
		//크레인작업허용중량
		long lngYD_WRK_ABLE_WT      = 0;
		//검색된 대상재 개수
		int intLotGpSh				= 0;
		//작업대상재 개수
		int intYD_LOT_GP_SH			= 0;

		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if(szRcvTcCode == null){
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		
		}
		//TC CODE DISPLAY
		if(bDebugFlag){
		
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			
			
			ydUtils.displayRecord(szOperationName, msgRecord);
			
			//받은 전문 확인
			//야드구분
			szYD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP");
			if(szYD_GP.equals("")){
				
				szMsg = "[전문 이상] 야드구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}
			//동구분
			szYD_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP");
			if(szYD_BAY_GP.equals("")){
				
				szMsg = "[전문 이상] 동구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}
			//설비ID
			szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_RT_GP");
			if(szYD_AIM_RT_GP.equals("")){
				
				szMsg = "[전문 이상] 목표행선구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}
			
			// =================================================================================
			// 수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			// BRE 등록 안됨...테스트용 스케줄코드 생성
			// 추후 구현..
			 szYD_SCH_CD = "";
			// =================================================================================
			 //스케쥴기준 속성 조회, 크레인 사양 조회
			recResult = JDTORecordFactory.getInstance().create();
			intRtnVal = YdCommonUtils.getCrnInfoByCrnSchRule(szYD_SCH_CD, recResult);
			if( intRtnVal <= 0) return ;
			//야드작업가능중량
			lngYD_WRK_ABLE_WT = ydDaoUtils.paraRecChkNullLong(recResult, "YD_WRK_ABLE_WT");

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			// Lot 편성 대상 재료 Select
			blnRtnVal = chkGetCoilManualWrkLotGp(msgRecord, rsResult);
			if(!blnRtnVal)	return ;

			// Lot 편성 매수
			intLotGpSh = rsResult.size();

			// 레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			// JMS TC CODE
			//recOutPara.setField("JMS_TC_CD", "YDYDJ261");
			// 레코드 커서 처음으로
			//rsResult.first();

			// 보급 Lot 편성 재료 매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= intLotGpSh; Loop_i++){
				rsResult.absolute(Loop_i);
				// 레코드 추출
				recPara = rsResult.getRecord();
//				if(Loop_i == 1){
//					szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");
//					szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
//				}

				// 재료번호
				szSTL_NO = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
				// 재료중량
				lngSumYD_MTL_WT += ydDaoUtils.paraRecChkNullLong(recPara, "YD_MTL_WT");
				
				if( lngSumYD_MTL_WT > lngYD_WRK_ABLE_WT ) break;
				// 적치단재료상태
				//szYD_STK_LYR_MTL_STAT = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_MTL_STAT");

				// 다른 크레인 스케줄에 등록 되어 있는지 체크
				blnRtnVal = chkCrnWrkMtl(szSTL_NO);
				if(!blnRtnVal) return ;

				// 재료번호
				recOutPara.setField("STL_NO" + Loop_i, szSTL_NO);
				// 권상모음순서
				recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, "" + Loop_i);
				intYD_LOT_GP_SH = Loop_i;
			}

			// 발생 일시
			recOutPara.setField("JMS_TC_CREATE_DDTT", YdUtils.getCurDate("yyyy/MM/dd HH:mm:ss"));
			// 스케줄코드
			recOutPara.setField("YD_SCH_CD", szYD_SCH_CD);
			// Lot편성 매수
			recOutPara.setField("YD_LOT_GP_SH", "" + intYD_LOT_GP_SH);
			// 야드구분
			recOutPara.setField("YD_GP", szYD_GP);
			// 동구분
			recOutPara.setField("YD_BAY_GP", szYD_BAY_GP);
			// 목표야드구분
			recOutPara.setField("YD_AIM_YD_GP", szYD_GP);
			// 목표동구분
			recOutPara.setField("YD_AIM_BAY_GP", szYD_BAY_GP);
			
			//전문 로그 출력
			ydUtils.displayRecord(szOperationName, recOutPara);
			
			// 전문 송신
			//ydDelegate.sendMsg(recOutPara);
			//메소드 호출
			procCoilManualWrkReq(recOutPara);
			szMsg = "코일 매뉴얼 작업 Lot편성 후 코일 매뉴얼 작업요구 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch(Exception e){
			szMsg = "코일 매뉴얼 작업 Lot편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
	} //end of procCoilManualWrkLotComp
	
	
	/**
	 * 오퍼레이션명 : 코일 매뉴얼 작업 Lot편성 데이터 유무체크 및 데이터 반환
	 *  
	 * @param  String        szSchCd   스케줄코드
	 *         JDTORecordSet rsResult  결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetCoilManualWrkLotGp(JDTORecord recInPara, JDTORecordSet rsResult)throws JDTOException  {
		
		//저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		//리턴값(boolean)
		boolean blnRtnVal     = false;
		//리턴값(int)
		int intRtnVal         = 0;
		//메소드명
		String szMethodName   = "chkGetCoilManualWrkLotGp";
		String szMsg          = null;
		
		//레코드 선언
		JDTORecord recPara        = null;	

		try {
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();
			
			//저장품 테이블 조회
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 30);
			//대상 소재가 존재하면 리턴
			if(intRtnVal > 0){
				
				return blnRtnVal = true;
				
			} else if(intRtnVal == 0){
				
				szMsg = "코일 매뉴얼 작업 Lot편성 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == -2){
				
				szMsg = "코일 매뉴얼 작업 Lot편성 데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "코일 매뉴얼 작업 Lot편성 데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
			
		} catch(Exception e){
			szMsg = "코일 매뉴얼 작업 Lot편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of chkGetCoilManualWrkLotGp
	
	
	
	
	/**
	 * 오퍼레이션명 : 코일 매뉴얼 작업요구
	 * 
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procCoilManualWrkReq(JDTORecord msgRecord) throws JDTOException {

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
		String szMethodName = "procCoilManualWrkReq";
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
		//야드구분
		String szYD_AIM_YD_GP = null;
		//동구분
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
		if(szRcvTcCode == null){

			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error ("
					+ szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;

		}
		// TC CODE DISPLAY
		if(bDebugFlag){

			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		}

		try {

			// 받은 전문 편집
			// 스케줄코드
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			if(szYD_SCH_CD.equals("")){

				szMsg = "[전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;

			}
			//야드구분
			szYD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP");
			if(szYD_GP.equals("")){

				szMsg = "[전문 이상] 야드구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;

			}
			//동구분
			szYD_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP");
			if(szYD_BAY_GP.equals("")){

				szMsg = "[전문 이상] 동구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;

			}
			
			//야드구분
			szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_YD_GP");
			if(szYD_AIM_YD_GP.equals("")){

				szMsg = "[전문 이상] 목표야드구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;

			}
			//동구분
			szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_BAY_GP");
			if(szYD_AIM_BAY_GP.equals("")){

				szMsg = "[전문 이상] 목표동구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;

			}
			
			// 재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_LOT_GP_SH");
			if(intMtlCnt == 0){

				szMsg = "[전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;

			}

			
			// 재료번호
			szSTL_NO = new String[intMtlCnt + 1];
			// 권상모음순서
			szYD_UP_COLL_SEQ = new String[intMtlCnt + 1];

			// 재료번호, 권상모음순서
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){

				// 재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,
						"STL_NO" + Loop_i);
				if(szSTL_NO[Loop_i].equals("")){

					szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;

				}
				// 권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,
						"YD_UP_COLL_SEQ" + Loop_i);
				if(szYD_UP_COLL_SEQ[Loop_i].equals("")){

					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i]
							+ ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;

				}
			}

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if(!blnRtnVal) return ;

			// 레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();

			// 스케줄CD 체크
			// 스케줄 금지 유무
			szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");
			
			// 스케줄 금지 유무가 "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if(szYD_SCH_PROH_EXN.equals("Y")){

				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			
			// 작업크레인
			szYD_WRK_CRN = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
			// 작업크레인우선순위
			szYD_WRK_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN_PRIOR");
			// 대체크레인유무
			szYD_ALT_CRN_YN = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
			// 대체크레인
			szYD_ALT_CRN = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
			// 대체크레인우선순위
			szYD_ALT_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_PRIOR");
			
			// 작업크레인 설비 상태 체크
			blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);

			// 작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if(!blnRtnVal){

				szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				// 대체크레인의 유무를 체크한다.
				// 대체크레인이 없으면 에러 리턴
				if(!szYD_ALT_CRN_YN.equals("Y")){

					szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;

				}
				// 대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				// 대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if(!blnRtnVal){

					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;

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
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){

				// 크레인사양과 저장품 사양을 체크(길이,폭,중량)
				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
				if(!blnRtnVal)
					return ;

				// 다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if(!blnRtnVal)
					return ;

			}

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if(!blnRtnVal)
				return ;
			// 레코드추출
			rsResult.first();
			recPara = rsResult.getRecord();
			// 작업예약ID
			szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");

			// INSERT 항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();

			// INSERT할 항목 SET
			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID); 			// 작업예약ID
			recPara.setField("YD_GP", szYD_GP);	 						// 야드구분
			recPara.setField("YD_BAY_GP", szYD_BAY_GP); 				// 야드동구분
			recPara.setField("YD_SCH_CD", szYD_SCH_CD); 				// 야드스케쥴코드
			recPara.setField("YD_SCH_PRIOR", szYD_SCH_PRIOR); 			// 야드스케쥴우선순위
			recPara.setField("YD_AIM_YD_GP", szYD_AIM_YD_GP); 			// 야드목표야드구분
			recPara.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP); 		// 야드목표동구분
			// 야드스케쥴진행상태
			// 야드스케쥴기동구분
			// 야드스케쥴요청구분
			recPara.setField("REGISTER", szUser);
			// 작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if(intRtnVal < 1){
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}

			// 조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();

			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			// recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			// recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", szUser);

			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){

				// 리턴 recordSet 생성
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				// 재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				intRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C",
						rsResult);
				if(intRtnVal != 1){
					szMsg = "재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져오는 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					// 예외를 발생시켜 롤백 시킴
					throw new DAOException(szSessionName + " : " + szMethodName
							+ " - " + szMsg);
					// return ;
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

				if(intRtnVal < 1){
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					// 예외를 발생시켜 롤백 시킴
					throw new DAOException(szSessionName + " : " + szMethodName
							+ " - " + szMsg);
					// return ;
				}
			}

			//C열연크레인스케줄Main 호출
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("JMS_TC_CD", "YDYDJ509");
			recPara.setField("YD_SCH_CD", szYD_SCH_CD);
			recPara.setField("YD_EQP_ID", szCrn);
			ydDelegate.sendMsg(recPara);

		} catch (JDTOException e){
			szMsg = "코일 매뉴얼 작업요구  처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(e);
		}
	} // end of procCoilManualWrkReq()
	
	

	
	/**
	 * 오퍼레이션명 : 코일제품출하상차Lot편성(YDYDJ282)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String procCoilGdsDistCarLdComp(JDTORecord msgRecord) throws JDTOException  {
		
		YdDelegate ydDelegate          = new YdDelegate();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils          = new YdDaoUtils();
		
		//레코드 선언
		JDTORecord recPara             = null;
		JDTORecord recOutPara          = null;
		//레코드셋 선언
		JDTORecordSet rsResult         = null;
		
		//리턴값(boolean)
		boolean blnRtnVal              = false;
		//메세지
		String szMsg                   = "";
		//메소드명
		String szMethodName            = "procCoilGdsDistCarLdComp";
		String szOperationName			= "코일제품출하상차Lot편성(YDYDJ282)";
		
		//전문 생성 일시
		String szDate                  = "";

		//설비ID
		String szYD_EQP_ID             = "";
		//상차정지위치
		String szYD_CARLD_STOP_LOC     = "";
		//차량사용구분
		String szYD_CAR_USE_GP         = "";
		//차량번호
		String szCAR_NO                = "";
		//카드번호
		String szCARD_NO               = "";
		//재료품목
		//String szYD_MTL_ITEM           = "";
		//스케줄코드
		String szYD_SCH_CD             = "";
		//차량작업허용매수
		int intYD_WRK_ALW_SH           = 0;
		String szTRANS_ORD_DATE        = "";
		String szTRANS_ORD_SEQNO       = "";
		String szYD_GP                 = "";
		String szYD_BAY_GP             = "";
		String szSPOS_WLOC_CD          = "";
		String szSPOS_YD_PNT_CD        = "";
		
		
		
		String szRtnMsg = null;
		
		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if(szRcvTcCode == null || szRcvTcCode.equals("")){
			szMsg = "["+szOperationName+"] TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_TC_ERROR;
		}

		//TC CODE DISPLAY
		if(bDebugFlag){
			szMsg="["+szOperationName+"] 전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		try {
			szMsg="["+szOperationName+"] 메소드 시작 : 전문내용 확인 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			ydUtils.displayRecord(szOperationName, msgRecord);

			//야드구분
			szYD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP");
			if(szYD_GP.equals("")){
				szMsg = "[전문 이상] 야드구분 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}
			
			// PIDEV
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "", "APPPI0", szYD_GP, "*");		
			
			//상차정지위치
			szYD_CARLD_STOP_LOC = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CARLD_STOP_LOC");
			if(szYD_CARLD_STOP_LOC.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 상차정지위치가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			//차량사용구분
			szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_USE_GP");
			if( szYD_CAR_USE_GP.equals("") ){
				
				szMsg = "["+szOperationName+"] [전문 이상] 차량사용구분(" + szYD_CAR_USE_GP + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			//차량번호
			szCAR_NO = ydDaoUtils.paraRecChkNull(msgRecord, "CAR_NO");
			if(szCAR_NO.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 차량번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			
			// PIDEV
//			if("N".equals(sApplyYnPI)) {			
//
//				//카드번호
//				szCARD_NO = ydDaoUtils.paraRecChkNull(msgRecord, "CARD_NO");				
//				
//				if(szCARD_NO.equals("")){
//					
//					szMsg = "["+szOperationName+"] [전문 이상] 카드번호가 없습니다.";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//					return YdConstant.RETN_CD_NO_PARAM;
//					
//				}
//				
//			}
			//운송지시일자
			szTRANS_ORD_DATE = ydDaoUtils.paraRecChkNull(msgRecord, "TRANS_ORD_DATE");
			if(szTRANS_ORD_DATE.equals("")){
				szMsg = "["+szOperationName+"] [전문 이상] 운송지시일자 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}			
			
			//운송지시순번
			szTRANS_ORD_SEQNO = ydDaoUtils.paraRecChkNull(msgRecord, "TRANS_ORD_SEQNO");
			if(szTRANS_ORD_SEQNO.equals("")){
				szMsg = "["+szOperationName+"] [전문 이상] 운송지시순번 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}
			
			
			//동구분
			szYD_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP");
			if(szYD_BAY_GP.equals("")){
				szMsg = "[전문 이상] 동구분 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}
			
			//발지개소코드
			szSPOS_WLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_WLOC_CD");
			if(szSPOS_WLOC_CD.equals("")){
				szMsg = "[전문 이상] 발지개소코드 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}
			
			//발지야드포인트코드
			szSPOS_YD_PNT_CD = ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_YD_PNT_CD");
			if(szSPOS_YD_PNT_CD.equals("")){
				szMsg = "[전문 이상] 발지야드포인트코드 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}

			//=================================================================================
			//수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			//BRE 등록 안됨...테스트용 스케줄코드 생성
			//추후 구현..
			//szYD_SCH_CD  = "JATR01UM";
			szYD_SCH_CD  = szYD_CARLD_STOP_LOC.substring(0, 4) + "01UM";
			//=================================================================================
				
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//상차 Lot 편성 대상 재료 Select
			blnRtnVal = chkDistCarLoadLotGp(msgRecord, rsResult);
			if(!blnRtnVal) return YdConstant.RETN_CD_FAILURE;	
			
			intYD_WRK_ALW_SH = rsResult.size();
			
			//레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			//JMS TC CODE
			recOutPara.setField("JMS_TC_CD",          "YDYDJ292");
						
			for (int Loop_i = 1; Loop_i <= intYD_WRK_ALW_SH; Loop_i++){
				rsResult.absolute(Loop_i);
				recPara = rsResult.getRecord();
				//재료번호
				recOutPara.setField("STL_NO" + Loop_i, ydDaoUtils.paraRecChkNull(recPara, "STL_NO"));
				recOutPara.setField("YD_STK_COL_GP" + Loop_i, ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP"));
				recOutPara.setField("YD_STK_BED_NO" + Loop_i, ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO"));
				recOutPara.setField("YD_STK_LYR_NO" + Loop_i, ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO"));
				//권상모음순서
				recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, "" + Loop_i);
				
			}
			
			szYD_EQP_ID = YdConstant.YD_DM_CAR_EQP_ID;
			
			//전문 발생 일시
			szDate = YdUtils.getCurDate("yyyy/MM/dd HH:mm:ss");

			//발생 일시
			recOutPara.setField("JMS_TC_CREATE_DDTT", szDate);
			//스케줄코드
			recOutPara.setField("YD_SCH_CD",          szYD_SCH_CD);
			//재료매수
			recOutPara.setField("YD_CARLD_SH",        "" + intYD_WRK_ALW_SH);
			// 설비id
			recOutPara.setField("YD_EQP_ID",          szYD_EQP_ID);
			//차량사용구분 
			recOutPara.setField("YD_CAR_USE_GP",      szYD_CAR_USE_GP);
			//상차 정지위치
			recOutPara.setField("YD_CARLD_STOP_LOC",      szYD_CARLD_STOP_LOC);
			//차량번호
			recOutPara.setField("CAR_NO",      szCAR_NO);
			//카드번호 
			recOutPara.setField("CARD_NO",      szCARD_NO);
			//운송지시일자 
			recOutPara.setField("TRANS_ORD_DATE",      szTRANS_ORD_DATE);
			//운송지시순번 
			recOutPara.setField("TRANS_ORD_SEQNO",      szTRANS_ORD_SEQNO);
			//야드구분 
			//recOutPara.setField("YD_GP",      szYD_GP);
			//동구분 
			//recOutPara.setField("YD_BAY_GP",      szYD_BAY_GP);
			//발지개소코드 
			recOutPara.setField("SPOS_WLOC_CD",      szSPOS_WLOC_CD);
			//발지포인트코드 
			recOutPara.setField("SPOS_YD_PNT_CD",      szSPOS_YD_PNT_CD);
			szMsg = "코일제품출하상차Lot편성 후 차량상차 작업요구 송신 시작 - 전문내용 표시";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			ydUtils.displayRecord(szOperationName, recOutPara);
			ydDelegate.sendMsg(recOutPara);
			
			//szRtnMsg = this.procCoilGdsDistCarLdWrkReq(recOutPara);
			szMsg = "코일제품출하상차Lot편성 후 차량상차 작업요구 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			

			
		} catch(Exception e){
			szMsg = "코일제품출하상차Lot편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;	
		}
		return szRtnMsg;
	} //end of procCoilGdsDistCarLdComp
	
	/**
	 * 오퍼레이션명 : C연주대차상차LOT편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procCCsTcarLdLotComp (JDTORecord msgRecord) throws JDTOException  {
		
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
		//리턴값(int)
		int intRtnVal          = 0;
		//메세지
		String szMsg           = "";
		//메소드명
		String szMethodName    = "procCCsTcarLdLotComp";
		//사용자
		String szUser          = "SYSTEM";

		//전문 생성 일시
		String szDate             = null;

		//설비ID
		String szYD_EQP_ID         = null;
		//상차정지위치
		String szYD_CARLD_STOP_LOC = null;
		//재료품목
		String szYD_MTL_ITEM       = null;
		//목표행선구분
		String szYD_AIM_RT_GP      = null;
		//목표동구분
		String szYD_AIM_BAY_GP     = null;
		//스케줄코드
		String szYD_SCH_CD         = null;
		//대차이송재료매수
		int intYD_CARLD_SH         = 0;
		//대상 재료 중량 합계
		long lngSumMtlWt           = 0;
		//대차작업허용중량(100t)
		long lngYD_WRK_ALW_WT      = 100000;
		


		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if(szRcvTcCode == null){
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		
		}
		//TC CODE DISPLAY
		if(bDebugFlag){
		
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			//받은 전문 편집
			//설비ID
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if(szYD_EQP_ID.equals("")){
				
				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}
			//상차정지위치
			szYD_CARLD_STOP_LOC = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CARLD_STOP_LOC");
			if(szYD_CARLD_STOP_LOC.equals("")){
				
				szMsg = "[전문 이상] 상차정지위치가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}
			//재료품목
			szYD_MTL_ITEM = ydDaoUtils.paraRecChkNull(msgRecord, "YD_MTL_ITEM");
			if(szYD_MTL_ITEM.equals("")){
				
				szMsg = "[전문 이상] 재료품목이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}
			//목표행선구분
			szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_RT_GP");
			if(szYD_AIM_RT_GP.equals("")){
				
				szMsg = "[전문 이상] 목표행선구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}
			//목표동구분
			szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_BAY_GP");
			if(szYD_AIM_BAY_GP.equals("")){
				
				szMsg = "[전문 이상] 목표동구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}

			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//상차 Lot 편성 대상 재료 Select
			blnRtnVal = chkGetMtlFtmvCarLoadLotGp(msgRecord, rsResult);
			if(!blnRtnVal) return ;			
			
			//레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			//JMS TC CODE
			recOutPara.setField("JMS_TC_CD",          "YDYDJ249");
			
			//첫 레코드로 커서이동
			rsResult.first();
			
			//레코드 추출
			recPara = rsResult.getRecord();
			//=================================================================================
			//수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			//BRE 등록 안됨...테스트용 스케줄코드 생성
			//추후 구현..
			szYD_SCH_CD  = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CARLD_STOP_LOC") +
						   "UM";
			//=================================================================================
			//대상 소재 매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++){
				
				//레코드 추출
				recPara = rsResult.getRecord();
				//대상 재료 중량 합계
				lngSumMtlWt = lngSumMtlWt + Long.parseLong(ydDaoUtils.paraRecChkNull(recPara, "YD_MTL_WT"));
				//대차작업허용중량보다 대상재료들의 중량이 많으면 편성 중지
				if(lngYD_WRK_ALW_WT < lngSumMtlWt) break;
				//재료번호
				recOutPara.setField("STL_NO" + Loop_i, ydDaoUtils.paraRecChkNull(recPara, "STL_NO"));
				//권상모음순서
				recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, "" + Loop_i);
				//대차이송재료매수
				intYD_CARLD_SH = Loop_i;
				
				//다음 레코드로 커서 이동
				rsResult.next();

			}
			
			//전문 발생 일시
			szDate = ydUtils.getCurDate("yyyy/MM/dd HH:mm:ss");

			//발생 일시
			recOutPara.setField("JMS_TC_CREATE_DDTT", szDate);
			//스케줄코드
			recOutPara.setField("YD_SCH_CD",          szYD_SCH_CD);
			//CARRY_IN 재료매수
			recOutPara.setField("YD_CARLD_SH",        "" + intYD_CARLD_SH);
			//적치열구분
			recOutPara.setField("YD_STK_COL_GP",      szYD_EQP_ID);
			//적치BED번호
			recOutPara.setField("YD_STK_BED_NO",      "01");
			
			//2009.02.12 김진욱 수정/////////////////////////////////////////////////
			//목표동구분
			recOutPara.setField("YD_AIM_BAY_GP",      szYD_AIM_BAY_GP);
			//////////////////////////////////////////////////////////////////////
			
			
			//전문 송신
			ydDelegate.sendMsg(recOutPara);
			//ydDelegate.rmtSndMsg(recOutPara);
			szMsg = "C연주 대차 상차 LOT 편성 후 차량상차 작업요구 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch(Exception e){
			szMsg = "C연주 대차 상차 LOT 편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
	} //end of procCCsTcarLdLotComp
	
	
	
	
	
	
	
	/**
	 * 오퍼레이션명 : C연주대차상차작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procCCsTcarLdWrkReq(JDTORecord msgRecord)throws JDTOException  {
		
		//스케줄기준 DAO
		YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();
		//작업예약 재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils           = new YdDaoUtils();
		//공용 METHOD
		YdUtils ydutils                 = new YdUtils();
		
		//리턴값(boolean)
		boolean blnRtnVal      = false;
		//리턴값(int)
		int intRtnVal          = 0;
		//메세지
		String szMsg           = "";
		//METHOD명
		String szMethodName    = "procCCsTcarLdWrkReq";
		//사용자
		String szUser          = "SYSTEM";
		
		//레코드 선언
		JDTORecord recPara     = null;
		JDTORecord recStkPara  = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		
		//설비ID(열구분)
		String szYD_STK_COL_GP     = null;
		//적치BED번호
		String szYD_STK_BED_NO     = null;
		//재료매수(int)
		int intMtlCnt              = 0;
		//재료번호
		String[] szSTL_NO          = null;
		//권상모음순서
		String[] szYD_UP_COLL_SEQ  = null;
		//스케줄코드
		String szYD_SCH_CD         = null;
		//스케줄 금지 유무
		String szYD_SCH_PROH_EXN   = null;
		//작업크레인
		String szYD_WRK_CRN        = null;
		//대체크레인유무
		String szYD_ALT_CRN_YN     = null;
		//대체크레인
		String szYD_ALT_CRN        = null;
		//선택크레인
		String szCrn               = null;
		//작업예약ID
		String szYD_WBOOK_ID       = null;
		
		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if(szRcvTcCode == null){
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		
		}
		//TC CODE DISPLAY
		if(bDebugFlag){
		
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
		
			//받은 전문 편집
			//스케줄코드
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			if(szYD_SCH_CD.equals("")){
				
				szMsg = "[전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}
			//적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
			if(szYD_STK_COL_GP.equals("")){
				
				szMsg = "[전문 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}
			//적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
			if(szYD_STK_BED_NO.equals("")){
				
				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}
			//재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_CARLD_SH");
			if(intMtlCnt == 0){
				
				szMsg = "[전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}

			//재료번호
			szSTL_NO          = new String[intMtlCnt + 1];
			//권상모음순서
			szYD_UP_COLL_SEQ  = new String[intMtlCnt + 1];
			
			
			//재료번호, 권상모음순서
			for(int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
				
				//재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO" + Loop_i);
				if(szSTL_NO[Loop_i].equals("")){
					
					szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
					
				}
				//권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_COLL_SEQ" + Loop_i);
				if(szYD_UP_COLL_SEQ[Loop_i].equals("")){
					
					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i] + ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;	
					
				}
			}

			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if(!blnRtnVal) return ;
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//스케줄CD 체크
			//스케줄 금지 유무
			szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");
			//작업크레인
			szYD_WRK_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
			//대체크레인유무
			szYD_ALT_CRN_YN   = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
			//대체크레인
			szYD_ALT_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
			
			//2009.02.12 김진욱 추가////////////////////////////////////////////////////////////////////////
			//스케줄우선순위
			String szYD_WRK_CRN_PRIOR      = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN_PRIOR");
			String szYD_AIM_BAY_GP         = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_BAY_GP");
			String szTCAR                  = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
			
			String szYD_WRK_PLAN_TCAR = szTCAR.substring(0,1) + "X" + szTCAR.substring(2,6);
			
			/////////////////////////////////////////////////////////////////////////////////////////////
			
			
			
			//스케줄 금지 유무가  "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if(szYD_SCH_PROH_EXN.equals("Y")){
				
				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			
			//작업크레인 설비 상태 체크
			blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);
			
			//작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if(!blnRtnVal){
				
				szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
				//대체크레인의 유무를 체크한다.
				//대체크레인이 없으면 에러 리턴
				if(!szYD_ALT_CRN_YN.equals("Y")){
					
					szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if(!blnRtnVal){
					
					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
					
				} else {
					//대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					szCrn = szYD_ALT_CRN;
					
				}
			} else {
				//작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				szCrn = szYD_WRK_CRN;
				
			}

			//재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다.
			//작업예약재료 등록 여부를 체크한다.
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
				
				//크레인사양과 저장품 사양을 체크(길이,폭,중량)
				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
				if(!blnRtnVal) return ;
				
				//다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if(!blnRtnVal) return ;
				
			}	
			
			//리턴 recordSet 생성
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			
			//작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if(!blnRtnVal) return ;
			//레코드추출
			rsResult.first();
			recPara = rsResult.getRecord();
			//작업예약ID
			szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");
			
			//INSERT 항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			//야드구분
			String szYD_GP       = szYD_SCH_CD.substring(0, 1);
			//동구분
			String szYD_BAY_GP   = szYD_SCH_CD.substring(1,2);
			
			//INSERT할 항목 SET
			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			recPara.setField("YD_GP", 		szYD_GP);
			recPara.setField("YD_BAY_GP", 	szYD_BAY_GP);
			recPara.setField("YD_SCH_CD", 	szYD_SCH_CD);
			recPara.setField("REGISTER", 	szUser);
			
			
			//2009.02.12 김진욱 수정/////////////////////////////////////////
			//스케줄 우선순위 및 목표동, 목표야드 구분
			recPara.setField("YD_SCH_PRIOR", 	szYD_WRK_CRN_PRIOR);
			recPara.setField("YD_AIM_BAY_GP", 	szYD_AIM_BAY_GP);
			recPara.setField("YD_WRK_PLAN_TCAR",szYD_WRK_PLAN_TCAR);
			recPara.setField("YD_AIM_YD_GP", 	"A");
			/////////////////////////////////////////////////////////////
			
			
			
			//작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if(intRtnVal < 1){
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			
			
			//조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
//			recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
//			recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", 	  szUser);

			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
				
				//리턴 recordSet 생성
				rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
				//재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				intRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C", rsResult);
				if(intRtnVal != 1) return ;
				
				//레코드추출
				rsResult.first();
				recStkPara = rsResult.getRecord();
				
				//재료번호
				recPara.setField("STL_NO", 		   szSTL_NO[Loop_i]);
				//적치열구분
				recPara.setField("YD_STK_COL_GP",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_COL_GP"));
				//적치BED번호
				recPara.setField("YD_STK_BED_NO",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_BED_NO"));
				//적치단번호
				recPara.setField("YD_STK_LYR_NO",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_LYR_NO"));
				//권상모음순서
				recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ[Loop_i]);
				
				// 작업예약재료 테이블에 등록한다.
				intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
				
				if(intRtnVal < 1){
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
			}
		
		} catch(Exception e){
			szMsg = "C연주 대차상차 작업요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}
	
	} // end of procCCsTcarLdWrkReq()
	
	
	
	/**
	 * 오퍼레이션명 : A후판가열로보급Lot편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procAPlRefurSupLotComp(JDTORecord msgRecord) throws JDTOException  {
		
		YdDelegate ydDelegate = new YdDelegate();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		//레코드 선언
		JDTORecord recPara     = null;
		JDTORecord recOutPara  = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		
		//리턴값(boolean)
		boolean blnRtnVal      = false;
		//메세지
		String szMsg           = "";
		//메소드명
		String szMethodName    = "procAPlRefurSupLotComp";
		
		/*
		 * A후판슬라브야드의 크레인작업가능매수는 3으로 설정 --> 크레인사양 테이블에서 크레인의 설정값을 읽어서 사용하는 것으로 변경필요.
		 * 수정자 : 임춘수
		 * 수정일자 : 2009.08.11
		 */
		int intCarryInCnt         = 3;				
		//전문 생성 일시
		String szDate             = null;

		//설비ID
		String szYD_EQP_ID             = null;
		//목표행선구분
		String szYD_AIM_RT_GP          = null;
		//적치BED번호
		String szYD_STK_BED_NO         = null;
		//스케줄코드
		String szYD_SCH_CD             = null;
		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if(szRcvTcCode == null){
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		//TC CODE DISPLAY
		if(bDebugFlag){
		
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		try {
			//스케줄코드
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if(szYD_EQP_ID.equals("")){
				
				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}
			//목표행선구분
			szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_RT_GP");
			if(szYD_AIM_RT_GP.equals("")){
				
				szMsg = "[전문 이상] 목표행선구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}
			//적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
			if(szYD_STK_BED_NO.equals("")){
				
				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}
			
			//=================================================================================
			//수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			//BRE 등록 안됨...테스트용 스케줄코드 생성
			//추후 구현..
			if("".equals(szYD_STK_BED_NO)){
				szYD_SCH_CD  = YdConstant.SCH_CD_D_DAYD99MR;
			}else{
				szYD_SCH_CD  = szYD_EQP_ID + "UM";
			}
			//=================================================================================
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//보급 Lot 편성 대상 재료 Select
			blnRtnVal = this.chkGetCHRSupplyLotGpPlt(szYD_SCH_CD, "PA", rsResult);
			if(!blnRtnVal) return ;
			
			//지시 재료 매수가 Carry In 재료기준매수(현재3매)보다 작으면 지시 재료 매수를 재료기준매수에 대입한다.
			if(intCarryInCnt > rsResult.size()) intCarryInCnt = rsResult.size();
			
			//레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			
			//JMS TC CODE
			recOutPara.setField("JMS_TC_CD", "YDYDJ245");
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			double totalSlabT = 0;
			
			//Carry In 재료기준매수(현재3매)만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= intCarryInCnt; Loop_i++){

				totalSlabT = totalSlabT + ydDaoUtils.paraRecChkNullDouble(recPara, "SLAB_T");
				
				if(szYD_SCH_CD.equals(szYD_EQP_ID + "UM")){
					if(totalSlabT >= 850){
						
						szMsg = "A후판 가열로 보급 Lot 편성과정에서 총두께의  합이 850이 넘음!="+totalSlabT;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						--intCarryInCnt;
						break;
					}
				}
				
				//재료번호
				recOutPara.setField("STL_NO" + Loop_i, ydDaoUtils.paraRecChkNull(recPara, "STL_NO"));
				//권상모음순서
				recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, "" + Loop_i);

				//다음 레코드 추출
				rsResult.next();
				recPara = rsResult.getRecord();
			}
			
			//전문 발생 일시
			szDate = ydUtils.getCurDate("yyyy/MM/dd HH:mm:ss");
			//발생 일시
			recOutPara.setField("JMS_TC_CREATE_DDTT", szDate);
			//스케줄코드
			recOutPara.setField("YD_SCH_CD",          szYD_SCH_CD);
			//CARRY_IN 재료매수
			recOutPara.setField("YD_CARRY_IN_SH",     "" + intCarryInCnt);
			//적치열구분
			recOutPara.setField("YD_STK_COL_GP",      szYD_EQP_ID);
			//적치BED번호
			recOutPara.setField("YD_STK_BED_NO",      szYD_STK_BED_NO);
			
			//전문 송신
			ydDelegate.sendMsg(recOutPara);
			//this.procAPlCarryInWrkReq(recOutPara);
			
			szMsg = "A후판 가열로 보급 Lot 편성 후 CARRY_IN 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch(Exception e){
			szMsg = "A후판 가열로 보급 Lot 편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}
		
	} //end of procAPlRefurSupLotComp
	
	/**
	 * 오퍼레이션명 : A후판Carry-In작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procAPlCarryInWrkReq(JDTORecord msgRecord)throws JDTOException  {
		String szMsg           = "";
		String szMethodName    = "procCCsCHrSupCarryInWrkReq";
		
		try{
			JDTORecord recRtn = JDTORecordFactory.getInstance().create();
			
			EJBConnector ydEjbCon = new EJBConnector("default", this);
			recRtn = (JDTORecord)ydEjbCon.trx("IssueWrkDmdSeEJB", "procAPlCarryInWrkReq_Rnew", msgRecord);
			
			String sEqpId 	= ydDaoUtils.paraRecChkNull(recRtn, "YD_EQP_ID");
			
			if(!"".equals(sEqpId)){
				// 스케쥴 모듈 호출
				JDTORecord recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("JMS_TC_CD", 	"YDYDJ503");
				recPara.setField("YD_SCH_CD", 	ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD"));
				recPara.setField("YD_EQP_ID", 	ydDaoUtils.paraRecChkNull(recRtn, "YD_EQP_ID"));
				
				ydDelegate.sendMsg(recPara);
			}
		} catch (Exception e) {
			szMsg="["+szMethodName+"] Exception발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMsg);
		}	// end try catch문
	}
	/**
	 * 오퍼레이션명 : A후판Carry-In작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord procAPlCarryInWrkReq_Rnew(JDTORecord msgRecord)throws JDTOException  {
		
		//스케줄기준 DAO
		YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();
		//작업예약 재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils           = new YdDaoUtils();
		
		//리턴값(boolean)
		boolean blnRtnVal      = false;
		//리턴값(int)
		int intRtnVal          = 0;
		//메세지
		String szMsg           = "";
		//METHOD명
		String szMethodName    = "procAPlCarryInWrkReq";
		//사용자
		String szUser          = "SYSTEM";
		
		//레코드 선언
		JDTORecord recPara     = null;
		JDTORecord recStkPara  = null;
		JDTORecord recRtn      = JDTORecordFactory.getInstance().create();
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		
		//설비ID(열구분)
		String szYD_STK_COL_GP     = null;
		//적치BED번호
		String szYD_STK_BED_NO     = null;
		//재료매수(int)
		int intMtlCnt              = 0;
		//재료번호
		String[] szSTL_NO          = new String[5];
		//권상모음순서
		String[] szYD_UP_COLL_SEQ  = new String[5];
		//스케줄코드
		String szYD_SCH_CD         = null;
		//야드스케줄우선순위
		String szYD_SCH_PRIOR		= null;
		//스케줄 금지 유무
		String szYD_SCH_PROH_EXN   = null;
		//작업크레인
		String szYD_WRK_CRN        = null;
		// 작업크레인우선순위
		String szYD_WRK_CRN_PRIOR = null;
		//대체크레인유무
		String szYD_ALT_CRN_YN     = null;
		//대체크레인
		String szYD_ALT_CRN        = null;
		// 대체크레인우선순위
		String szYD_ALT_CRN_PRIOR = null;
		//선택크레인
		String szCrn               = null;
		//작업예약ID
		String szYD_WBOOK_ID       = null;
		// 크레인 작업가능 매수
		int  intWrkAblCnt          = 0;
		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if(szRcvTcCode == null){
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return recRtn;
		
		}
		//TC CODE DISPLAY
		if(bDebugFlag){
		
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
		
			//받은 전문 편집
			//스케줄코드
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			if(szYD_SCH_CD.equals("")){
				
				szMsg = "[전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return recRtn;
				
			}
			//적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
			if(szYD_STK_COL_GP.equals("")){
				
				szMsg = "[전문 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return recRtn;
				
			}
			//적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
			if(szYD_STK_BED_NO.equals("")){
				
				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}
			//재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_CARRY_IN_SH");
			if(intMtlCnt == 0){
				
				szMsg = "[전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return recRtn;
				
			}

			//재료번호, 권상모음순서
			for(int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
				
				//재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO" + Loop_i);
				if(szSTL_NO[Loop_i].equals("")){
					
					szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return recRtn;
					
				}
				//권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_COLL_SEQ" + Loop_i);
				if(szYD_UP_COLL_SEQ[Loop_i].equals("")){
					
					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i] + ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return recRtn;	
					
				}
			}

			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if(!blnRtnVal) return recRtn;
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//스케줄CD 체크
			//스케줄 금지 유무
			szYD_SCH_PROH_EXN 	= ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");
			//작업크레인
			szYD_WRK_CRN      	= ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
			//작업크레인우선순위
			szYD_WRK_CRN_PRIOR  = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN_PRIOR");
			//대체크레인유무
			szYD_ALT_CRN_YN   	= ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
			//대체크레인
			szYD_ALT_CRN      	= ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
			//대체크레인우선순위
			szYD_ALT_CRN_PRIOR  = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_PRIOR");
			
			//스케줄 금지 유무가  "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if(szYD_SCH_PROH_EXN.equals("Y")){
				
				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return recRtn;
			}
			
			//작업크레인 설비 상태 체크
			blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);
			
			//작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if(!blnRtnVal){
				
				szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
				//대체크레인의 유무를 체크한다.
				//대체크레인이 없으면 에러 리턴
				if(!szYD_ALT_CRN_YN.equals("Y")){
					
					szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return recRtn;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if(!blnRtnVal){
					
					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return recRtn;
					
				} else {
					//대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					szCrn = szYD_ALT_CRN;
					szYD_SCH_PRIOR = szYD_ALT_CRN_PRIOR;
				}
			} else {
				//작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				szCrn = szYD_WRK_CRN;
				szYD_SCH_PRIOR = szYD_WRK_CRN_PRIOR;
			}
			
			/////////////////////////////////////////////////////////////////////////////
			////////////////////보급 대상재를 가지고 여러가지 체크를한다..중요사항/////////////////
			/////////////////////////////////////////////////////////////////////////////
			/*
			 * 2010.07.05 YJK 단순체크사항 - 삭제해도 괜찮을거 같음.
			 */
			/*
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
				
				//크레인사양과 저장품 사양을 체크(길이,폭,중량)
				blnRtnVal = this.chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
				if(!blnRtnVal) return ;
				
				//다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if(!blnRtnVal) return ;
				
			}	
			*/
			/*
			 * 2010.07.05 YJK 폭/중량기준으로 대상재를 체크한다.
			 */
			intWrkAblCnt = this.chkWrkMtl_W(szSTL_NO, szCrn, intMtlCnt);
			if (intWrkAblCnt == 0) {
				szMsg = "작업예약 재료 폭체크 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return recRtn;
			}
			/////////////////////////////////////////////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////////
			
			
			/////////////////////////////////////////////////////////////////////////////
			////////////////////자동준비작업 보완기능 - 스케쥴 충돌현상 방지//////////////////////
			/////////////////////////////////////////////////////////////////////////////
			//1. 스케쥴코드가 DAPU01UM 이면 DAYD99MR로 Lot편성한 재료가 스케쥴에 등록되어 있는지 체크한다.
			//2. 등록되어 있으면 권하위치(szYD_STK_COL_GP + szYD_STK_BED_NO) 변경 모듈을 호출한다. 리턴한다.
			if(YdConstant.SCH_CD_D_REFUR_SUP1.equals(szYD_SCH_CD)){
				
				rsResult  	= JDTORecordFactory.getInstance().createRecordSet("");
				recPara 	= JDTORecordFactory.getInstance().create();
				recPara.setField("YD_EQP_ID", 			szCrn);
				recPara.setField("YD_SCH_CD", 			YdConstant.SCH_CD_D_DAYD99MR);
				
				intRtnVal = ydWrkbookDao.getYdSchCrnMtl(recPara,rsResult);
				
				if(intRtnVal > 0){
					
					JDTORecord recOutTemp 	= null;
					String sStlNo			= "";
					String sSchId			= "";
					boolean isWork			= true;
					
					// 재료들이 동일한지를 체크
					// 대상 - 작업예약재료 <> Lot편성재료
					for( int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++ ) {
						
						rsResult.absolute(Loop_i);
						recOutTemp = JDTORecordFactory.getInstance().create();
						recOutTemp.setRecord(rsResult.getRecord());
						
						sStlNo  = ydDaoUtils.paraRecChkNull(recOutTemp, "STL_NO");
						sSchId  = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CRN_SCH_ID");
						
						ydUtils.putLog(szSessionName, szMethodName, "YD_CRN_SCH_ID ="+sSchId, YdConstant.DEBUG);
						
						if(isWork){
							for (int Loop_j = 1; Loop_j <= intWrkAblCnt; Loop_j++){
								
								if(sStlNo.equals(szSTL_NO[Loop_j])){
									isWork	= true;
									break;
								}else{
									isWork = false;
								}
							}
						}else{
							break;
						}
					}
					ydUtils.putLog(szSessionName, szMethodName, "LAST YD_CRN_SCH_ID ="+sSchId, YdConstant.DEBUG);
					ydUtils.putLog(szSessionName, szMethodName, "isWork ="+isWork, YdConstant.DEBUG);
					
					if(isWork){
					
						//권하위치 변경(화면 권하위치 변경모듈 호출 )
						recPara 	= JDTORecordFactory.getInstance().create();
						recPara.setField("YD_EQP_ID", 			szCrn);
						recPara.setField("YD_CRN_SCH_ID", 		sSchId);
						recPara.setField("YD_GP", 				YdConstant.YD_GP_A_PLATE_SLAB_YARD);
						recPara.setField("YD_DN_WO_LOC", 		szYD_STK_COL_GP + szYD_STK_BED_NO);
	
						JDTORecord [] inRecord =  new JDTORecord[1];
						inRecord[0]	= recPara;
						EJBConnector ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
						ejbConn.trx("updToPosFix", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
		                
						return recRtn;
					}
				}
			//3. 스케쥴코드가 DAYD99MR 이면 DAYD99MR로 이미 작업예약에 등록이 되어 있는지 확인한다. 있으면 리턴..	
			}else if(YdConstant.SCH_CD_D_DAYD99MR.equals(szYD_SCH_CD)){
				
				recPara 	= JDTORecordFactory.getInstance().create();
				recPara.setField("YD_SCH_CD", YdConstant.SCH_CD_D_DAYD99MR);
				
				intRtnVal = ydWrkbookDao.getYdSchWrkMtl(recPara);
				
				if(intRtnVal > 0){
					return recRtn;
				}
			}
			/////////////////////////////////////////////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////////
			
			//리턴 recordSet 생성
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			
			//작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if(!blnRtnVal) return recRtn;
			//레코드추출
			rsResult.first();
			recPara = rsResult.getRecord();
			//작업예약ID
			szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");
			
			//INSERT 항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			//야드구분
			String szYD_GP       = szYD_SCH_CD.substring(0, 1);
			//동구분
			String szYD_BAY_GP   = szYD_SCH_CD.substring(1,2);
			
			//INSERT할 항목 SET
			recPara.setField("YD_WBOOK_ID", 		szYD_WBOOK_ID);
			recPara.setField("YD_GP", 				szYD_GP);
			recPara.setField("YD_BAY_GP", 			szYD_BAY_GP);
			recPara.setField("YD_SCH_CD", 			szYD_SCH_CD);
			recPara.setField("YD_SCH_PRIOR", 		szYD_SCH_PRIOR);
			
			if("".equals(szYD_STK_BED_NO)){
				
				recPara.setField("YD_TO_LOC_DCSN_MTD", 	"S");								//야드To위치결정방법
				
			}else{
			
				recPara.setField("YD_TO_LOC_DCSN_MTD", 	"F");								//야드To위치결정방법
				recPara.setField("YD_TO_LOC_GUIDE", 	szYD_STK_COL_GP + szYD_STK_BED_NO); //야드To위치Guide
			}
			
			recPara.setField("YD_AIM_YD_GP", 		szYD_GP);
			recPara.setField("YD_AIM_BAY_GP", 		szYD_BAY_GP);
			recPara.setField("REGISTER", 			szUser);
	
			//작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			
			//조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
			recPara.setField("REGISTER", 	  szUser);

			for (int Loop_i = 1; Loop_i <= intWrkAblCnt; Loop_i++){
				
				//리턴 recordSet 생성
				rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
				//재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				intRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C", rsResult);
				
				if(intRtnVal != 1) {
					if( intRtnVal == 0 ) {
						intRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "U", rsResult);			//보조작업으로 권상대기 인 경우
						if(intRtnVal != 1) {
							szMsg="적치단에 재료["+szSTL_NO[Loop_i]+"]가 존재하지 않습니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							throw new JDTOException(szMsg);
						}
					}else{
						szMsg="적치단에 재료["+szSTL_NO[Loop_i]+"] 조회 시 오류발생";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						throw new JDTOException(szMsg);
					}
					//return YdConstant.RETN_CD_FAILURE;
				}
				
				//레코드추출
				rsResult.first();
				recStkPara = rsResult.getRecord();
				
				//재료번호
				recPara.setField("STL_NO", 		   szSTL_NO[Loop_i]);
				//적치열구분
				recPara.setField("YD_STK_COL_GP",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_COL_GP"));
				//적치BED번호
				recPara.setField("YD_STK_BED_NO",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_BED_NO"));
				//적치단번호
				recPara.setField("YD_STK_LYR_NO",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_LYR_NO"));
				//권상모음순서
				recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ[Loop_i]);
				
				// 작업예약재료 테이블에 등록한다.
				intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
			}
					
			recRtn.setField("YD_EQP_ID", 	szCrn);
			
			return recRtn;
			
		} catch(Exception e){
			szMsg = "A후판 Carry-In 작업요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMsg);
		}
	} // end of procAPlCarryInWrkReq()
	
	
	/**
	 * 오퍼레이션명 : A후판소재이송상차Lot편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return String
	 * @throws JDTOException
	 */
	public String procAPlMatlFtmvCarLdLotComp (JDTORecord msgRecord) throws JDTOException  {
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils 			= new YdDaoUtils();
		//레코드 선언
		JDTORecord recPara     			= null;
		JDTORecord recOutPara  			= null;
		//레코드셋 선언
		JDTORecordSet rsResult 			= null;
		//리턴값(int)
		int intRtnVal          			= 0;
		//메세지
		String szMsg           			= "";
		//메소드명
		String szMethodName    			= "procAPlMatlFtmvCarLdLotComp";
		String szOperationName 			= "A후판소재이송상차Lot편성";
		String szREG_MOD_USER			= szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName;

		//운송장비코드
		String szTRN_EQP_CD        		= null;
		//발지개소코드
		String szSPOS_WLOC_CD      		= null;
		//차량스케줄ID
		String szYD_CAR_SCH_ID			= null;
		//스케줄코드
		String szYD_SCH_CD         		= null;
		
		String szYD_PREP_SCH_ID			= null;
		String szYD_WBOOK_ID			= null;
		String szSTL_NO 				= null;
		String szYD_GP					= null;
		String szYD_BAY_GP				= null;
		String szYD_STK_COL_GP     		= null;
		String szYD_STK_BED_NO			= null;
		String szYD_STK_LYR_NO			= null;
		String szYD_AIM_YD_GP			= null;
		String szYD_AIM_BAY_GP			= null;
		String szYD_SCH_PRIOR			= null;
		
		YdWrkbookDao		ydWrkbookDao	= new YdWrkbookDao();
		YdWrkbookMtlDao		ydWrkbookMtlDao	= new YdWrkbookMtlDao();
		
		//리턴값
		String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if(szRcvTcCode == null){
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_TC_ERROR;
		
		}
		//TC CODE DISPLAY
		if(bDebugFlag){
		
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			//-------------------------------------------------------------------------------------------------
			//	파라미터 확인
			//-------------------------------------------------------------------------------------------------
			
			szMsg = "["+szOperationName+"] ---------------------- 메소드 시작 : 파라미터 확인 ----------------------";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			ydUtils.displayRecord(szOperationName, msgRecord);
			
			//운송장비코드
			szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(msgRecord, "TRN_EQP_CD");
			if(szTRN_EQP_CD.equals("")){
				
				szMsg = "["+szOperationName+"] 운송장비코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			
			//발지개소코드
			szSPOS_WLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_WLOC_CD");
			if(szSPOS_WLOC_CD.equals("")){
				
				szMsg = "["+szOperationName+"] 발지개소코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			
			//차량스케줄ID
			szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_SCH_ID");
			if(szYD_CAR_SCH_ID.equals("")){
				
				szMsg = "["+szOperationName+"] 차량스케줄ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			
			//-------------------------------------------------------------------------------------------------
			
			
			//-------------------------------------------------------------------------------------------------
			//	배차순서가 빠르고 준비스케줄ID가 빠른 차량 이송 대상재를 준비스케줄에서 조회
			//-------------------------------------------------------------------------------------------------
			
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			recPara  = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_GP", 			YdConstant.YD_GP_A_PLATE_SLAB_YARD);
			recPara.setField("YD_SCH_CD", 		YdConstant.YD_GP_A_PLATE_SLAB_YARD + "_PT");
			recPara.setField("YD_WRK_PLAN_CRN", "");
			recPara.setField("YD_PREP_WK_ST", 	"L");
			//항목추가
			recPara.setField("CAR_GP", 	szTRN_EQP_CD.substring(1, 2));
			
			szMsg = "["+szOperationName+"] 배차순서가 빠르고 준비스케줄ID가 빠른 차량 이송 대상재를 준비스케줄에서 조회 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szRtnMsg			= DaoManager.getYdStock(recPara, rsResult, 151);
			
			szMsg = "["+szOperationName+"] 배차순서가 빠르고 준비스케줄ID가 빠른 차량 이송 대상재를 준비스케줄에서 조회 완료 - 메세지 : " + szRtnMsg + ", 대상재건수 : " + rsResult.size();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			if( szRtnMsg.equals(YdConstant.RETN_CD_NOTEXIST) ) {
				
				//-------------------------------------------------------------------------------------------------
				//	대상재가 존재하지 않는 경우에는 차량정지POINT요구 모듈 호출
				//-------------------------------------------------------------------------------------------------
				
				szMsg="["+szOperationName+"] 대상재가 존재하지 않는 경우 소재차량도착Point요구 모듈 호출 시작 - JMS Call";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				YdCommonUtils.callMatlCarArrPntReq(szTRN_EQP_CD, szSPOS_WLOC_CD, "N", this);
				
				szMsg="["+szOperationName+"] 대상재가 존재하지 않는 경우 소재차량도착Point요구 모듈 호출 완료 - JMS Call";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//-------------------------------------------------------------------------------------------------
				
				return szRtnMsg;
				
			}else if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				
				szMsg = "["+szOperationName+"] 배차순서가 빠르고 준비스케줄ID가 빠른 차량 이송 대상재를 준비스케줄에서 조회 시 오류발생 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
				return szRtnMsg;
			}
			
			//-------------------------------------------------------------------------------------------------
			
			
			//-------------------------------------------------------------------------------------------------
			//	작업예약/작업예약재료 등록
			//-------------------------------------------------------------------------------------------------
			
			
			
			for(int i = 1; i <= rsResult.size(); i++ ) {
				
				rsResult.absolute(i);
				recPara			= rsResult.getRecord();
				
				szSTL_NO				= ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
				
				szYD_SCH_CD				= ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_CD");
				szYD_GP					= szYD_SCH_CD.substring(0, 1);
				szYD_BAY_GP				= szYD_SCH_CD.substring(1, 2);
				szYD_AIM_YD_GP			= ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_YD_GP");
				szYD_AIM_BAY_GP			= ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_BAY_GP");
				
				szYD_STK_COL_GP			= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");
				szYD_STK_BED_NO			= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
				szYD_STK_LYR_NO			= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO");
				
				if( i == 1 ) {
					
					szYD_PREP_SCH_ID				= ydDaoUtils.paraRecChkNull(recPara, "YD_PREP_SCH_ID");
					
					//-------------------------------------------------------------------------------------------------
					//	스케줄코드 조회
					//-------------------------------------------------------------------------------------------------
					
					szMsg = "["+szOperationName+"] 스케줄코드["+szYD_SCH_CD+"]에 대한 스케줄 기준 조회 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					recOutPara = JDTORecordFactory.getInstance().create();
					
					szRtnMsg			= YdCommonUtils.getWrkableCrnBySchRule(szYD_SCH_CD, recOutPara);
					
					szMsg = "["+szOperationName+"] 스케줄코드["+szYD_SCH_CD+"]에 대한 스케줄 기준 조회 완료 - 메세지 : " + szRtnMsg + ", 대상재건수 : " + rsResult.size();
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					if( szRtnMsg.equals(YdConstant.RETN_CRN_NO_ALT_CRN)
							|| szRtnMsg.equals(YdConstant.YD_EQP_STAT_BREAK) 
							|| szRtnMsg.equals(YdConstant.YD_EQP_WRK_MODE_OFF_LINE)
							) {
						szYD_SCH_PRIOR				= ydDaoUtils.paraRecChkNull(recOutPara, "YD_WRK_CRN_PRIOR");
					}else if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
						
						szMsg = "["+szOperationName+"] 스케줄코드["+szYD_SCH_CD+"]에 대한 스케줄 기준 조회 시 오류발생 - 메세지 : " + szRtnMsg;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						
						return szRtnMsg;
					}else{
						szYD_SCH_PRIOR				= ydDaoUtils.paraRecChkNull(recOutPara, "YD_SCH_PRIOR");
					}
					
					//-------------------------------------------------------------------------------------------------
					
					//-------------------------------------------------------------------------------------------------
					//	작업예약 등록
					//-------------------------------------------------------------------------------------------------
					
					szYD_WBOOK_ID			= ydWrkbookDao.getYdWrkbookId();
					
					recOutPara = JDTORecordFactory.getInstance().create();
					
					recOutPara.setField("YD_WBOOK_ID", 			szYD_WBOOK_ID);
					recOutPara.setField("REGISTER", 			szREG_MOD_USER);
					recOutPara.setField("YD_GP", 				szYD_GP);
					recOutPara.setField("YD_BAY_GP", 			szYD_BAY_GP);
					recOutPara.setField("YD_SCH_CD", 			szYD_SCH_CD);
					recOutPara.setField("YD_SCH_PRIOR", 		szYD_SCH_PRIOR);
					recOutPara.setField("YD_AIM_YD_GP", 		szYD_AIM_YD_GP);
					recOutPara.setField("YD_AIM_BAY_GP", 		szYD_AIM_BAY_GP);
					recOutPara.setField("YD_CAR_USE_GP", 		YdConstant.YD_CAR_USE_GP_TS);
					recOutPara.setField("TRN_EQP_CD", 			szTRN_EQP_CD);
					
					szMsg = "["+szOperationName+"] 작업예약["+szYD_WBOOK_ID+"]등록 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					intRtnVal			= ydWrkbookDao.insYdWrkbook(recOutPara);
					
					if( intRtnVal <= 0 ) {
						
						szMsg = "["+szOperationName+"] 작업예약["+szYD_WBOOK_ID+"]등록 시 오류발생 - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						
						return YdConstant.RETN_CD_FAILURE;
					}
					
					szMsg = "["+szOperationName+"] 작업예약["+szYD_WBOOK_ID+"]등록 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					//-------------------------------------------------------------------------------------------------
				}
				
				//-------------------------------------------------------------------------------------------------
				//	작업예약재료 등록
				//-------------------------------------------------------------------------------------------------
				
				recOutPara = JDTORecordFactory.getInstance().create();
				
				recOutPara.setField("YD_WBOOK_ID", 				szYD_WBOOK_ID);
				recOutPara.setField("REGISTER", 				szREG_MOD_USER);
				recOutPara.setField("STL_NO", 					szSTL_NO);
				recOutPara.setField("YD_STK_COL_GP", 			szYD_STK_COL_GP);
				recOutPara.setField("YD_STK_BED_NO", 			szYD_STK_BED_NO);
				recOutPara.setField("YD_STK_LYR_NO", 			szYD_STK_LYR_NO);
				recOutPara.setField("YD_UP_COLL_SEQ", 			String.valueOf(i));
				
				szMsg = "["+szOperationName+"] ["+i+"] 작업예약["+szYD_WBOOK_ID+"]의 작업재료["+szSTL_NO+"] 등록 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				intRtnVal			= ydWrkbookMtlDao.insYdWrkbookmtl(recOutPara);
				
				if( intRtnVal <= 0 ) {
					
					szMsg = "["+szOperationName+"] ["+i+"] 작업예약["+szYD_WBOOK_ID+"]의 작업재료["+szSTL_NO+"] 등록 시 오류발생 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					
					throw new DAOException(szMsg);
				}
				
				szMsg = "["+szOperationName+"] ["+i+"] 작업예약["+szYD_WBOOK_ID+"]의 작업재료["+szSTL_NO+"] 등록 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//-------------------------------------------------------------------------------------------------
			}
			
			//-------------------------------------------------------------------------------------------------

			
			//-------------------------------------------------------------------------------------------------
			//	준비스케줄 삭제
			//-------------------------------------------------------------------------------------------------
			
			szMsg = "["+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"] 삭제 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szRtnMsg			= YdCommonUtils.deletePreSch(szYD_PREP_SCH_ID, szYD_WBOOK_ID, szMethodName);
			
			if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				
				szMsg = "["+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"] 삭제 시 오류발생 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
				throw new DAOException(szMsg);
			}
			
			szMsg = "["+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"] 삭제 완료 - 메세지 : " + szRtnMsg;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//-------------------------------------------------------------------------------------------------
			
			
			//-------------------------------------------------------------------------------------------------
			//	차량스케줄에 상차작업예약 등록
			//-------------------------------------------------------------------------------------------------
			
			recOutPara = JDTORecordFactory.getInstance().create();
			
			recOutPara.setField("YD_CAR_SCH_ID", 				szYD_CAR_SCH_ID);
			recOutPara.setField("YD_CARLD_WRK_BOOK_ID", 		szYD_WBOOK_ID);
			recOutPara.setField("MODIFIER", 					szREG_MOD_USER);
			
			szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"]에 상차작업예약["+szYD_WBOOK_ID+"] 등록 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szRtnMsg			= DaoManager.updYdCarsch(recOutPara, 0);
			
			if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				
				szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"]에 상차작업예약["+szYD_WBOOK_ID+"] 등록 시 오류발생 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
				throw new DAOException(szMsg);
			}
			
			szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"]에 상차작업예약["+szYD_WBOOK_ID+"] 등록 완료 - 메세지 : " + szRtnMsg;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//-------------------------------------------------------------------------------------------------
			
			
			//-------------------------------------------------------------------------------------------------
			//	차량정지POINT요구 모듈 호출
			//-------------------------------------------------------------------------------------------------
			
			szMsg="["+szOperationName+"] 작업예약["+szYD_WBOOK_ID+"] 등록 후 소재차량도착Point요구 모듈 호출 시작 - EJB Call";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			YdCommonUtils.callMatlCarArrPntReq(szTRN_EQP_CD, szSPOS_WLOC_CD, "Y", this);
			
			szMsg="["+szOperationName+"] 작업예약["+szYD_WBOOK_ID+"] 등록 후 소재차량도착Point요구 모듈 호출 완료 - EJB Call";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//-------------------------------------------------------------------------------------------------
			
	    	szMsg = "["+szOperationName+"] ---------------------- 메소드 끝 ----------------------";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
		}catch(DAOException e){
			szMsg = "["+szOperationName+"] DAOException 예외발생[1] : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw e;
		} catch(Exception e){
			szMsg = "["+szOperationName+"] 예외발생[2] : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMsg);
			//return YdConstant.RETN_CD_FAILURE;
		}
		//return szRtnMsg;
		return YdConstant.RETN_CD_SUCCESS;
	} //end of procAPlMatlFtmvCarLdLotComp
	

	/**
	 * 오퍼레이션명 : A후판차량상차작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String procAPlCarldWrkReq(JDTORecord msgRecord)throws JDTOException  {
		
		//스케줄기준 DAO
		YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();
		//작업예약 재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils           = new YdDaoUtils();
		//공용 METHOD
		YdUtils ydutils                 = new YdUtils();
		
		//리턴값(boolean)
		boolean blnRtnVal      = false;
		//리턴값(int)
		int intRtnVal          = 0;
		//메세지
		String szMsg           = "";
		//METHOD명
		String szMethodName    = "procAPlCarldWrkReq";
		//사용자
		String szUser          = "SYSTEM";
		
		//레코드 선언
		JDTORecord recPara     = null;
		JDTORecord recStkPara  = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		
		//설비ID(열구분)
		String szYD_STK_COL_GP     = null;
		//적치BED번호
		String szYD_STK_BED_NO     = null;
		//재료매수(int)
		int intMtlCnt              = 0;
		//재료번호
		String[] szSTL_NO          = null;
		//권상모음순서
		String[] szYD_UP_COLL_SEQ  = null;
		//스케줄코드
		String szYD_SCH_CD         = null;
		//스케줄 금지 유무
		String szYD_SCH_PROH_EXN   = null;
		//작업크레인
		String szYD_WRK_CRN        = null;
		//대체크레인유무
		String szYD_ALT_CRN_YN     = null;
		//대체크레인
		String szYD_ALT_CRN        = null;
		//선택크레인
		String szCrn               = null;
		//작업예약ID
		String szYD_WBOOK_ID       = null;
		//차량설비id
		String szYD_EQP_ID         = null;
		//차량사용구분
		String szYD_CAR_USE_GP     = null;
		//목표행선구분
		String szYD_AIM_RT_GP      = null;
		//운송장비코드
		String szTRN_EQP_CD        = null;
		//개소코드
		String szWLOC_CD           = null;
		//운송영공구분
		String szSP_TRUCK_LOADING_LOC_TP = null;
		//포인트요구일시
		String szPNT_DMD_DT        = null;
		//발지개소코드
		String szSPOS_WLOC_CD      = null;
		//발지포인트코드
		String szSPOS_YD_PNT_CD    = null;
		//목표야드구분
		String szYD_AIM_YD_GP      = null;
		//목표동구분
		String szYD_AIM_BAY_GP     = null;
		String szYD_DIRECT_CARLD_GP = null;
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if(szRcvTcCode == null){
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_TC_ERROR;
		
		}
		//TC CODE DISPLAY
		if(bDebugFlag){
		
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			
			//받은 전문 편집
			//스케줄코드
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			if(szYD_SCH_CD.equals("")){
				
				szMsg = "[전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			//설비ID
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if(szYD_EQP_ID.equals("")){
				
				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			//목표행선구분
			szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_RT_GP");
			if(szYD_AIM_RT_GP.equals("")){
				
				szMsg = "[전문 이상] 목표행선구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}
			//차량사용구분
			szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_USE_GP");
			if(szYD_CAR_USE_GP.equals("")){
				
				szMsg = "[전문 이상] 차량사용구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			
			if(szYD_CAR_USE_GP.equals("L")){
				//운송장비코드
				szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(msgRecord, "TRN_EQP_CD");
				if(szTRN_EQP_CD.equals("")){
					
					szMsg = "[전문 이상] 운송장비코드가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;
				}
				
				//개소코드
				szWLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord, "WLOC_CD");
				if(szWLOC_CD.equals("")){
					
					szMsg = "[전문 이상] 개소코드가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;
					
				}
				
				//운송작업영공구분코드
				szSP_TRUCK_LOADING_LOC_TP = ydDaoUtils.paraRecChkNull(msgRecord, "SP_TRUCK_LOADING_LOC_TP");
				if(szSP_TRUCK_LOADING_LOC_TP.equals("")){
					
					szMsg = "[전문 이상] 운송작업영공구분코드가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;
					
				}
				
				//포인트요구일시
				szPNT_DMD_DT = ydDaoUtils.paraRecChkNull(msgRecord, "PNT_DMD_DT");
				if(szPNT_DMD_DT.equals("")){
					
					szMsg = "[전문 이상] 포인트요구일시가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;
					
				}
				
			}


			
			szYD_DIRECT_CARLD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_DIRECT_CARLD_GP");
			

			
			
			//발지개소코드
			szSPOS_WLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_WLOC_CD");
			if(szSPOS_WLOC_CD.equals("")){
				
				szMsg = "[전문 이상] 발지개소코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return ;
				
			}
			
			//발지포인트코드
			szSPOS_YD_PNT_CD = ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_YD_PNT_CD");
			if(szSPOS_YD_PNT_CD.equals("")){
				
				szMsg = "[전문 이상] 발지포인트코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return ;
				
			}
			
			//재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_CARLD_SH");
			if(intMtlCnt == 0){
				
				szMsg = "[전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}

			//재료번호
			szSTL_NO          = new String[intMtlCnt + 1];
			//권상모음순서
			szYD_UP_COLL_SEQ  = new String[intMtlCnt + 1];
			
			
			
			
			
			
			//재료번호, 권상모음순서
			for(int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
				
				//재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO" + Loop_i);
				if(szSTL_NO[Loop_i].equals("")){
					
					szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;
					
				}
				//권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_COLL_SEQ" + Loop_i);
				if(szYD_UP_COLL_SEQ[Loop_i].equals("")){
					
					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i] + ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;
					
				}
			}

			if( szYD_DIRECT_CARLD_GP.equals("Y")) {
				//리턴 recordSet 생성
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	
				//스케줄 기준 체크
				blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
				if(!blnRtnVal){
					return YdConstant.RETN_CD_FAILURE;
				}
				
				//레코드 추출
				rsResult.first();
				recPara = rsResult.getRecord();
				
				//스케줄CD 체크
				//스케줄 금지 유무
				szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");
				//작업크레인
				szYD_WRK_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
				//대체크레인유무
				szYD_ALT_CRN_YN   = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
				//대체크레인
				szYD_ALT_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
				
				
				//스케줄 금지 유무가  "Y"이면 처리를 중지하고 유스케이스를 종료한다.
				if(szYD_SCH_PROH_EXN.equals("Y")){
					
					szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;
				}
				
				//작업크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);
				
				//작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
				if(!blnRtnVal){
					
					szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					
					//대체크레인의 유무를 체크한다.
					//대체크레인이 없으면 에러 리턴
					if(!szYD_ALT_CRN_YN.equals("Y")){
						
						szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return YdConstant.RETN_CD_FAILURE;
						
					}
					//대체크레인이 있으면 대체크레인 설비 상태 체크
					blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
					//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
					if(!blnRtnVal){
						
						szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return YdConstant.RETN_CD_FAILURE;
						
					} else {
						//대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
						szCrn = szYD_ALT_CRN;
						
					}
				} else {
					//작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
					szCrn = szYD_WRK_CRN;
					
				}
	
				//재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다.
				//작업예약재료 등록 여부를 체크한다.
				for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
					
					//크레인사양과 저장품 사양을 체크(길이,폭,중량)
					blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
					if(!blnRtnVal){
						return YdConstant.RETN_CD_FAILURE;
					}
					
					//다른 작업예약에 재료가 등록되어있는지 체크한다.
					blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
					if(!blnRtnVal){
						return YdConstant.RETN_CD_FAILURE;
					}
					
				}	
				
				//리턴 recordSet 생성
				rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
				
				//작업예약ID 생성
				blnRtnVal = getYdWbookId(rsResult);
				if(!blnRtnVal){
					return YdConstant.RETN_CD_FAILURE;
				}
				//레코드추출
				rsResult.first();
				recPara = rsResult.getRecord();
				//작업예약ID
				szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");
				
				//리턴 recordSet 생성
				rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
				//저장품테이블 조회
				blnRtnVal = this.chkGetStock(szSTL_NO[1], rsResult);
				if(!blnRtnVal){
					return YdConstant.RETN_CD_FAILURE;
				}
				//레코드추출
				rsResult.first();
				recPara = rsResult.getRecord();
				
				//야드목표야드구분
				szYD_AIM_YD_GP  = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_YD_GP");
				//야드목표동구분
				szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_BAY_GP");
				
				
				//INSERT 항목 record 생성
				recPara = JDTORecordFactory.getInstance().create();
				//야드구분
				String szYD_GP       = szYD_SCH_CD.substring(0, 1);
				//동구분
				String szYD_BAY_GP   = szYD_SCH_CD.substring(1,2);
				
				//INSERT할 항목 SET
				recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
				recPara.setField("YD_GP", 		  szYD_GP);
				recPara.setField("YD_BAY_GP", 	  szYD_BAY_GP);
				recPara.setField("YD_SCH_CD", 	  szYD_SCH_CD);
				recPara.setField("REGISTER", 	  szUser);
				recPara.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
				recPara.setField("TRN_EQP_CD", 	  szTRN_EQP_CD);
				recPara.setField("YD_AIM_YD_GP",  szYD_AIM_YD_GP);
				recPara.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP);
		
				//작업예약 INSERT
				intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
				if(intRtnVal < 1){
					szMsg = "작업예약 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;
				}
				
				
				//조회항목 record 생성
				recPara = JDTORecordFactory.getInstance().create();
				
				recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
	//			recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
	//			recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
				recPara.setField("REGISTER", 	  szUser);
	
				for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
					
					//리턴 recordSet 생성
					rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
					//재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
					intRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C", rsResult);
					if(intRtnVal != 1) return YdConstant.RETN_CD_FAILURE;
					
					//레코드추출
					rsResult.first();
					recStkPara = rsResult.getRecord();
					
					//재료번호
					recPara.setField("STL_NO", 		   szSTL_NO[Loop_i]);
					//적치열구분
					recPara.setField("YD_STK_COL_GP",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_COL_GP"));
					//적치BED번호
					recPara.setField("YD_STK_BED_NO",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_BED_NO"));
					//적치단번호
					recPara.setField("YD_STK_LYR_NO",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_LYR_NO"));
					//권상모음순서
					recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ[Loop_i]);
					
					// 작업예약재료 테이블에 등록한다.
					intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
					
					if(intRtnVal < 1){
						szMsg = "작업예약재료 데이터 등록 중 에러";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return YdConstant.RETN_CD_FAILURE;
					}
				}
			}
				
			//record 생성
			recPara = JDTORecordFactory.getInstance().create();
			//설비ID
			recPara.setField("YD_EQP_ID",     szYD_EQP_ID);
			//차량사용구분
			recPara.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
			//운송장비코드
			recPara.setField("TRN_EQP_CD",    szTRN_EQP_CD);
			//개소코드
			recPara.setField("WLOC_CD",       szWLOC_CD);
			//운송작업영공구분코드
			recPara.setField("SP_TRUCK_LOADING_LOC_TP", szSP_TRUCK_LOADING_LOC_TP);
			//포인트요구일시
			recPara.setField("PNT_DMD_DT",    szPNT_DMD_DT);
			//발지개소코드
			recPara.setField("SPOS_WLOC_CD",  szSPOS_WLOC_CD);
			//발지포인트코드
			recPara.setField("SPOS_YD_PNT_CD", szSPOS_YD_PNT_CD);
			//스케줄코드
			recPara.setField("YD_SCH_CD", szYD_SCH_CD);
			recPara.setField("YD_DIRECT_CARLD_GP", szYD_DIRECT_CARLD_GP);
			
			
			//스케줄 메소드 호출
			intRtnVal = this.mkY1CarSch(recPara);
			if( intRtnVal != 1 ){
				return YdConstant.RETN_CD_FAILURE;
			}
			
		} catch(Exception e){
			szMsg = "A후판 차량상차 작업요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;
		}
		return YdConstant.RETN_CD_SUCCESS;
	
	} // end of procAPlCarldWrkReq()
	
	
	/**
	 * 오퍼레이션명 : A후판장입LotNo적용보급Lot편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procAPlChgLotNoEffSupLotComp (JDTORecord msgRecord) throws JDTOException  {
		
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
		//리턴값(int)
		int intRtnVal          = 0;
		//메세지
		String szMsg           = "";
		//메소드명
		String szMethodName    = "procAPlChgLotNoEffSupLotComp";
		//사용자
		String szUser          = "SYSTEM";
		
		//Lot 편성 재료 매수
		int intLotGpSh           = 0;
		//전문 생성 일시
		String szDate             = null;

		//적치열구분
		String szYD_STK_COL_GP     = null;
		//적치BED번호
		String szYD_STK_BED_NO     = null;
		//장입Lot번호
		String szREFUR_CHG_LOT_NO  = null;
		//기준 가열로장입예정일련번호
		String szREFUR_CHG_PLN_SERNO = null;
		//비교가열로장입예정일련번호
		String szREFUR_CHG_PLN_SERNO_COMP = null;
		//스케줄코드
		String szYD_SCH_CD         = null;
		//재료번호
		String szSTL_NO            = null;


		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if(szRcvTcCode == null){
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		
		}
		//TC CODE DISPLAY
		if(bDebugFlag){
		
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			//받은 전문 편집
			//적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
			if(szYD_STK_COL_GP.equals("")){
				
				szMsg = "[전문 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}
			//적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
			if(szYD_STK_BED_NO.equals("")){
				
				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}
			//장입Lot번호
			szREFUR_CHG_LOT_NO = ydDaoUtils.paraRecChkNull(msgRecord, "REFUR_CHG_LOT_NO");
			if(szREFUR_CHG_LOT_NO.equals("")){
				
				szMsg = "[전문 이상] 장입Lot번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}

			//=================================================================================
			//수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			//BRE 등록 안됨...테스트용 스케줄코드 생성
			//추후 구현..
			szYD_SCH_CD  = "DASP01MM";
			//=================================================================================
			
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			//보급 Lot 편성 대상 재료 Select
			blnRtnVal = chkGetAPlChgLotNoSupplyLotGp(szYD_STK_COL_GP.trim(), szYD_STK_BED_NO.trim(), 
					                                 	szREFUR_CHG_LOT_NO.trim(), rsResult);
			if(!blnRtnVal) return ;
			
			//보급 Lot 편성 재료 매수(기준 데이터를 제외한 매수)
			intLotGpSh = rsResult.size() - 1;
			
			//레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			//JMS TC CODE
			recOutPara.setField("JMS_TC_CD",          "YDYDJ265");
			//레코드 커서 처음으로
			rsResult.first();
			
			//레코드 추출(첫 레코드는 비교 기준 데이터임)
			recPara = rsResult.getRecord();
			//기준 가열로장입예정일련번호
			szREFUR_CHG_PLN_SERNO = ydDaoUtils.paraRecChkNull(recPara, "REFUR_CHG_PLN_SERNO");
			
			//두번째 레코드부터가 비교 레코드임
			rsResult.next();
			
			//보급 Lot 편성 재료 매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= intLotGpSh; Loop_i++){
				
				//레코드 추출
				recPara = rsResult.getRecord();
				
				//재료번호 
				szSTL_NO = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
				//비교 가열로장입예정일련번호
				szREFUR_CHG_PLN_SERNO_COMP = ydDaoUtils.paraRecChkNull(recPara, "REFUR_CHG_PLN_SERNO");
				
				//다른 크레인 스케줄에 등록 되어 있는지 체크
				blnRtnVal = chkCrnWrkMtl(szSTL_NO);
				if(!blnRtnVal) return ;
				
				//장입LotNo가 빠른지 체크(장입LotNo가 늦거나 장입Lot 미편성이면 OK)
				if(!szREFUR_CHG_PLN_SERNO_COMP.trim().equals("")){
					
					blnRtnVal = chkChgRefurChgPlnSerNo(szREFUR_CHG_PLN_SERNO.trim(), szREFUR_CHG_PLN_SERNO_COMP.trim());
					if(!blnRtnVal) return ;
				
				}
				
				//재료번호
				recOutPara.setField("STL_NO" + Loop_i, szSTL_NO);
				//권상모음순서
				recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, "" + (intLotGpSh - (Loop_i - 1)));

				//다음 레코드로
				rsResult.next();
				
			}
			
			//전문 발생 일시
			szDate = ydUtils.getCurDate("yyyy/MM/dd HH:mm:ss");

			//발생 일시
			recOutPara.setField("JMS_TC_CREATE_DDTT", szDate);
			//스케줄코드
			recOutPara.setField("YD_SCH_CD",          szYD_SCH_CD);
			//Lot편성 매수
			recOutPara.setField("YD_LOT_GP_SH",       "" + intLotGpSh);
			//적치열구분
			recOutPara.setField("YD_STK_COL_GP",      szYD_STK_COL_GP);
			//적치BED번호
			recOutPara.setField("YD_STK_BED_NO",      szYD_STK_BED_NO);
			
			//전문 송신
			ydDelegate.sendMsg(recOutPara);
			szMsg = "A후판 장입LotNo적용 보급 Lot 편성 후 A후판 장입 준비작업 요구 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch(Exception e){
			szMsg = "A후판 장입LotNo적용 보급 Lot 편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
	} //end of procAPlChgLotNoEffSupLotComp
	
	/**
	 * 오퍼레이션명 : C연주 장입LotNo적용 보급 Lot 편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procCCsChgLotNoEffSupLotComp (JDTORecord msgRecord) throws JDTOException  {
		
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
		//리턴값(int)
		int intRtnVal          = 0;
		//메세지
		String szMsg           = "";
		//메소드명
		String szMethodName    = "procCCsChgLotNoEffSupLotComp";
		//오퍼레이션명
		String szOperationName = "C연주 장입LotNo적용 보급 Lot 편성";
		//사용자
		String szUser          = "SYSTEM";
		
		//Lot 편성 재료 매수
		int intLotGpSh           = 0;
		//전문 생성 일시
		String szDate             = null;

		//적치열구분
		String szYD_STK_COL_GP     = null;
		/*//적치BED번호
		String szYD_STK_BED_NO     = null;*/
		//장입Lot번호
		String szREFUR_CHG_LOT_NO  = null;
		//기준 가열로장입예정일련번호
		String szREFUR_CHG_PLN_SERNO = null;
		//비교가열로장입예정일련번호
		String szREFUR_CHG_PLN_SERNO_COMP = null;
		//스케줄코드
		String szYD_SCH_CD         = null;
		//재료번호
		String szSTL_NO            = null;
		//야드목표행선구분
		String szYD_AIM_RT_GP		= null;
		//재료품목
		String szYD_MTL_ITEM = null;
		//대차
		String szTCAR	= null;
		//야드구분
		String szYD_GP     = null;
		//동구분
		String szYD_BAY_GP     = null;
		
		//C연주 가열로 장입pickup bed - 파라미터로 넘겨 받을 지를 결정 필요
		String szYD_EQP_ID 			= "AAPUP1";


		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if(szRcvTcCode == null){
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		
		}
		//TC CODE DISPLAY
		if(bDebugFlag){
		
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			//받은 전문 편집
			//적치열구분
			/*szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP").trim();
			if(szYD_STK_COL_GP.equals("")){
				
				szMsg = "[전문 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}
			//적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO").trim();
			if(szYD_STK_BED_NO.equals("")){
				
				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}*/
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").trim();
			if(szYD_EQP_ID.equals("")){
				
				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}
			szYD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP").trim();
			if(szYD_GP.equals("")){
				
				szMsg = "[전문 이상] 야드구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}
			szYD_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP").trim();
			if(szYD_BAY_GP.equals("")){
				
				szMsg = "[전문 이상] 동구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}
			//장입Lot번호
			szREFUR_CHG_LOT_NO = ydDaoUtils.paraRecChkNull(msgRecord, "REFUR_CHG_LOT_NO").trim();
			if(szREFUR_CHG_LOT_NO.equals("")){
				
				szMsg = "[전문 이상] 장입Lot번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}

			//=================================================================================
			//수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			//BRE 등록 안됨...테스트용 스케줄코드 생성
			//추후 구현..
			//szYD_SCH_CD  = "AAPU01LM";
			//=================================================================================
			
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			//보급 Lot 편성 대상 재료 Select
			blnRtnVal = chkGetCCcChgLotNoSupplyLotGp(szYD_GP, szYD_BAY_GP, szYD_EQP_ID,
					                                 	szREFUR_CHG_LOT_NO, rsResult);
			if(!blnRtnVal) return ;
			
			//보급 Lot 편성 재료 매수(기준 데이터를 제외한 매수)
			intLotGpSh = rsResult.size();
			
			//레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			//JMS TC CODE
			recOutPara.setField("JMS_TC_CD",          "YDYDJ259");
			
			//레코드 커서 처음으로
			//rsResult.first();
			
			//레코드 추출(첫 레코드는 비교 기준 데이터임)
			//recPara = rsResult.getRecord();
			//기준 가열로장입예정일련번호
			//szREFUR_CHG_PLN_SERNO = ydDaoUtils.paraRecChkNull(recPara, "REFUR_CHG_PLN_SERNO").trim();
			//적치열구분
			//szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP").trim();
			
			//재료번호
			//recOutPara.setField("STL_NO1", szSTL_NO);
			//권상모음순서
			//recOutPara.setField("YD_UP_COLL_SEQ1", "" + (intLotGpSh + 1));
			//재료품목
			//szYD_MTL_ITEM = ydDaoUtils.paraRecChkNull(recPara, "YD_MTL_ITEM").trim();
			//야드목표행선구분
			//szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_RT_GP").trim();
			//두번째 레코드부터가 비교 레코드임
			//rsResult.next();
			
			//보급 Lot 편성 재료 매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= intLotGpSh; Loop_i++){
				rsResult.absolute(Loop_i);
				//레코드 추출
				recPara = rsResult.getRecord();
				
				//재료번호 
				szSTL_NO = ydDaoUtils.paraRecChkNull(recPara, "STL_NO").trim();
				//비교 가열로장입예정일련번호
				//szREFUR_CHG_PLN_SERNO_COMP = ydDaoUtils.paraRecChkNull(recPara, "REFUR_CHG_PLN_SERNO").trim();
				
				//다른 크레인 스케줄에 등록 되어 있는지 체크
				blnRtnVal = chkCrnWrkMtl(szSTL_NO);
				if(!blnRtnVal){
					return ;
				}
				
				if( Loop_i == 1 ){
					//적치열구분
					szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP").trim();
					//재료품목
					szYD_MTL_ITEM = ydDaoUtils.paraRecChkNull(recPara, "YD_MTL_ITEM").trim();
					//야드목표행선구분
					szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_RT_GP").trim();
				}
				
				//장입LotNo가 빠른지 체크(장입LotNo가 늦거나 장입Lot 미편성이면 OK)
				/*if(!szREFUR_CHG_PLN_SERNO_COMP.equals("")){
					
					blnRtnVal = chkChgRefurChgPlnSerNo(szREFUR_CHG_PLN_SERNO, szREFUR_CHG_PLN_SERNO_COMP);
					if(!blnRtnVal){
						return ;
					}
				
				}
				//현재 장입예정일련번호를 이전 장입예정일련번호로 설정
				szREFUR_CHG_PLN_SERNO = szREFUR_CHG_PLN_SERNO_COMP;*/
				//재료번호
				recOutPara.setField("STL_NO" +  Loop_i , szSTL_NO);
				//권상모음순서
				recOutPara.setField("YD_UP_COLL_SEQ" +  Loop_i , "" +  Loop_i);

				//다음 레코드로
				//rsResult.next();
				
			}
			
			
			/* 스케쥴코드 생성
			 * 현재동 = 목표동 : 동내이적
			 * 현재동 <> 목표동 : 동간이적 - 대차배정 필요
			 */
			JDTORecord recInParam = JDTORecordFactory.getInstance().create();
			YdCommonUtils.mkCrnSchCdForMv(recInParam, szYD_STK_COL_GP, szYD_EQP_ID, szYD_MTL_ITEM, szYD_AIM_RT_GP);
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(recInParam, "YD_SCH_CD").trim();
			szTCAR = YdCommonUtils.sltTCarByYD(szYD_STK_COL_GP.substring(0, 1), szYD_STK_COL_GP.substring(1, 2), szYD_EQP_ID.substring(0, 1), szYD_EQP_ID.substring(1, 2), szYD_MTL_ITEM, szYD_AIM_RT_GP);
			if(szYD_SCH_CD.equals("")){
				szMsg = szOperationName + " 처리중 스케쥴코드 할당 실패";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//스케쥴코드를 할당 못 받더라도 기본 스케쥴코드 생성하는 로직이 필요함
				return ;
			}
			
			//전문 발생 일시
			szDate = ydUtils.getCurDate("yyyy/MM/dd HH:mm:ss");

			//발생 일시
			recOutPara.setField("JMS_TC_CREATE_DDTT", szDate);
			//스케줄코드
			recOutPara.setField("YD_SCH_CD",          szYD_SCH_CD);
			//Lot편성 매수
			recOutPara.setField("YD_LOT_GP_SH",       "" + rsResult.size());
			//적치열구분
			recOutPara.setField("YD_STK_COL_GP",      szYD_STK_COL_GP);
			//적치BED번호
			//recOutPara.setField("YD_STK_BED_NO",      szYD_STK_BED_NO);
			//목표적치열구분
			recOutPara.setField("YD_STK_COL_GP_TO",   szYD_EQP_ID);
			//작업계획대차
			recOutPara.setField("YD_WRK_PLAN_TCAR",   szTCAR);
			//전문내용 로그 출력
			ydUtils.displayRecord(szOperationName, recOutPara);

			//전문 송신
			ydDelegate.sendMsg(recOutPara);
			szMsg = szOperationName + " 후 C연주 장입 준비작업 요구 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch(Exception e){
			szMsg = szOperationName + " Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}

	} //end of procCCsChgLotNoEffSupLotComp
	
	
	
	/**
	 * 오퍼레이션명 : C연주 장입LotNo적용 보급 Lot 편성 데이터 유무체크 및 데이터 반환
	 *  
	 * @param  String        szYdStkColGp    적치열구분
	 *         String        szYdStkBedNo    적치Bed번호
	 *         String        szRefurChgLotNo 가열로장입LotNo
	 *         JDTORecordSet rsResult        결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetCCcChgLotNoSupplyLotGp(String szYdGp, String szYdBayNo, String szYD_EQP_ID,
			                                    	String szRefurChgLotNo, JDTORecordSet rsResult)throws JDTOException  {
		
		//저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		//리턴값(boolean)
		boolean blnRtnVal     = false;
		//리턴값(int)
		int intRtnVal         = 0;
		//메소드명
		String szMethodName   = "chkGetCCcChgLotNoSupplyLotGp";
		String szMsg          = null;
		
		//레코드 선언
		JDTORecord recPara        = null;	

		try {
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();
			
			//조업공장구분(C열연)
			recPara.setField("PTOP_PLNT_GP",     "HC");
			//야드구분
			recPara.setField("YD_GP",    szYdGp);
			//동구분
			recPara.setField("YD_BAY_GP",    szYdBayNo);
			//설비ID
			recPara.setField("YD_STK_COL_GP", szYD_EQP_ID);
			//가열로장입Lot번호
			recPara.setField("REFUR_CHG_LOT_NO", szRefurChgLotNo);
			//4매
			recPara.setField("ROW_CNT", "4");
			
			//저장품 테이블 조회
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 41);
			
			//리턴값 메세지처리
			if(intRtnVal > 1){

				blnRtnVal = true;
				
			} else if(intRtnVal == 1){
				
				szMsg = "야드("       + szYdGp    + ")," +
				        "동("      + szYdBayNo    + ")," +
				        "가열로장입Lot번호(" + szRefurChgLotNo + ")," +
				        "에 대한 C연주 장입LotNo적용 보급 Lot 편성 대상 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == 0){
				
				szMsg = "야드("       + szYdGp    + ")," +
				        "동("      + szYdBayNo    + ")," +
				        "가열로장입Lot번호(" + szRefurChgLotNo + ")," +
				        "에 대한 C연주 장입LotNo적용 보급 Lot 편성 조회 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == -2){
				
				szMsg = "야드("       + szYdGp    + ")," +
				        "동("      + szYdBayNo    + ")," +
				        "가열로장입Lot번호(" + szRefurChgLotNo + ")," +
				        "에 대한 C연주 장입LotNo적용 보급 Lot 편성  데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "야드("       + szYdGp    + ")," +
				        "동("      + szYdBayNo    + ")," +
				        "가열로장입Lot번호(" + szRefurChgLotNo + ")," +
				        "에 대한 C연주 장입LotNo적용 보급 Lot 편성  데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e){
			szMsg = "C연주 장입LotNo적용 보급 Lot 편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of chkGetCCcChgLotNoSupplyLotGp
	
	
	
	/**
	 * 오퍼레이션명 : C열연 제품 대차상차 Lot 편성 데이터 유무체크 및 데이터 반환
	 *  
	 * @param  JDTORecord    recInPara 파라미터 레코드
	 *         JDTORecordSet rsResult  결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetCHRGdsTcarLoadLotGp(JDTORecord recInPara, JDTORecordSet rsResult)throws JDTOException  {
		
		//저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		//리턴값(boolean)
		boolean blnRtnVal     = false;
		//리턴값(int)
		int intRtnVal         = 0;
		//메소드명
		String szMethodName   = "chkGetCHRGdsTcarLoadLotGp";
		String szMsg          = null;
		
		//레코드 선언
		JDTORecord recPara        = null;	

		try {
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();

			//재료품목
			recPara.setField("YD_MTL_ITEM",   ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_ITEM"));
			//목표행선구분
			recPara.setField("YD_AIM_RT_GP",  ydDaoUtils.paraRecChkNull(recInPara, "YD_AIM_RT_GP"));
			//목표야드구분
			recPara.setField("YD_AIM_YD_GP",  ydDaoUtils.paraRecChkNull(recInPara, "YD_AIM_YD_GP"));
			//목표동구분
			recPara.setField("YD_AIM_BAY_GP", ydDaoUtils.paraRecChkNull(recInPara, "YD_AIM_BAY_GP"));
			//FROM 야드동
			recPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(recInPara, "YD_CARLD_STOP_LOC").substring(0, 2));
			
			//저장품 테이블 조회
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 17);
			
			//리턴값 메세지처리
			if(intRtnVal >= 1){

				blnRtnVal = true;
				
			} else if(intRtnVal == 0){
				
				szMsg = "C열연 제품 대차상차 Lot 편성 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == -2){
				
				szMsg = "C열연 제품 대차상차 Lot 편성 데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "C열연 제품 대차상차 Lot 편성 데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e){
			szMsg = "C열연 제품 대차상차 Lot 편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of chkGetCHRGdsTcarLoadLotGp
	
	
	
	/**
	 * 오퍼레이션명 : 가열로장입예정일련번호 비교
	 *  
	 * @param  String        szSerNo     기준가열로장입일련번호
	 *         String        szCompSerNo 비교가열로장입일련번호
	 * @return boolean       true(Lot편성O), false(Lot편성X)
	 */
	public boolean chkChgRefurChgPlnSerNo(String szSerNo, String szCompSerNo){

		//리턴값(boolean)
		boolean blnRtnVal     = false;

		//메소드명
		String szMethodName   = "chkChgRefurChgPlnSerNo";
		String szMsg          = null;

		try {
			
			//가열로장입일련번호를 비교하여 상위단의 일련번호가 더 빠르면 Lot 편성 중지
			if(szSerNo.compareTo(szCompSerNo) == 1 ||
					szSerNo.compareTo(szCompSerNo) == 0){

				szMsg = "현재 가열로장입예정일련번호("+ szSerNo +") 보다" +
				        "상위 적치단의 가열로장입예정일련번호(" + szCompSerNo +")의 순서가 더 빠릅니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else
				blnRtnVal = true;
				
		} catch(Exception e){
			szMsg = "가열로장입예정일련번호 비교 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of chkChgRefurChgPlnSerNo
	
	
	/**
	 * 오퍼레이션명 : A후판 장입LotNo적용 보급 Lot 편성 데이터 유무체크 및 데이터 반환
	 *  
	 * @param  String        szYdStkColGp    적치열구분
	 *         String        szYdStkBedNo    적치Bed번호
	 *         String        szRefurChgLotNo 가열로장입LotNo
	 *         JDTORecordSet rsResult        결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetAPlChgLotNoSupplyLotGp(String szYdStkColGp, String szYdStkBedNo,
			                                    	String szRefurChgLotNo, JDTORecordSet rsResult)throws JDTOException  {
		
		//저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		//리턴값(boolean)
		boolean blnRtnVal     = false;
		//리턴값(int)
		int intRtnVal         = 0;
		//메소드명
		String szMethodName   = "chkGetAPlChgLotNoSupplyLotGp";
		String szMsg          = null;
		
		//레코드 선언
		JDTORecord recPara        = null;	

		try {
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();
			
			//압연공장구분(A후판)
			recPara.setField("MILL_PLNT_GP",     "P");
			//적치열구분
			recPara.setField("YD_STK_COL_GP",    szYdStkColGp);
			//적치Bed번호
			recPara.setField("YD_STK_BED_NO",    szYdStkBedNo);
			//가열로장입Lot번호
			recPara.setField("REFUR_CHG_LOT_NO", szRefurChgLotNo);
			
			//저장품 테이블 조회
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 20);
			
			//리턴값 메세지처리
			if(intRtnVal > 1){

				blnRtnVal = true;
				
			} else if(intRtnVal == 1){
				
				szMsg = "적치열구분("       + szYdStkColGp    + ")," +
				        "적치Bed번호("      + szYdStkBedNo    + ")," +
				        "가열로장입Lot번호(" + szRefurChgLotNo + ")," +
				        "에 대한 A후판 장입LotNo적용 보급 Lot 편성 대상 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == 0){
				
				szMsg = "적치열구분("       + szYdStkColGp    + ")," +
				        "적치Bed번호("      + szYdStkBedNo    + ")," +
				        "가열로장입Lot번호(" + szRefurChgLotNo + ")," +
				        "에 대한 A후판 장입LotNo적용 보급 Lot 편성 조회 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == -2){
				
				szMsg = "적치열구분("       + szYdStkColGp    + ")," +
				        "적치Bed번호("      + szYdStkBedNo    + ")," +
				        "가열로장입Lot번호(" + szRefurChgLotNo + ")," +
				        "에 대한 A후판 장입LotNo적용 보급 Lot 편성  데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "적치열구분("       + szYdStkColGp    + ")," +
				        "적치Bed번호("      + szYdStkBedNo    + ")," +
				        "가열로장입Lot번호(" + szRefurChgLotNo + ")," +
				        "에 대한 A후판 장입LotNo적용 보급 Lot 편성  데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e){
			szMsg = "A후판 장입LotNo적용 보급 Lot 편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of chkGetAPlChgLotNoSupplyLotGp
	
	
	
	/**
	 * 오퍼레이션명 : 크레인작업재료 유무 체크
	 *  
	 * @param  String        szStlNo         설비ID
	 * @return boolean       true(작업재료없음), false(작업재료있음)
	 * @throws JDTOException
	 */
	public boolean chkCrnWrkMtl(String szStlNo)throws JDTOException  {
		
		//크레인 작업 재료
		YdCrnWrkMtlDao ydCrnWrkMtlDao     = new YdCrnWrkMtlDao();
		//리턴값(boolean)
		boolean blnRtnVal     = false;
		//리턴값(int)
		int intRtnVal         = 0;
		//메소드명
		String szMethodName   = "chkCrnWrkMtl";
		String szMsg          = null;
		
		//레코드 선언
		JDTORecord recPara        = null;	
		JDTORecordSet rsResult    = null;

		try {
			
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();
			//레코드셋 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			//재료번호
			recPara.setField("STL_NO",    szStlNo);
			
			//설비 테이블 조회
			intRtnVal = ydCrnWrkMtlDao.getYdCrnwrkmtl(recPara, rsResult, 2);

			//리턴값 메세지처리
			if(intRtnVal >= 1){
				
				szMsg = "재료번호("+ szStlNo    +")의 소재(제품)이 이미 크레인스케줄 작업 재료에 등록되어 있습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == 0){

				blnRtnVal = true;
				
			} else if(intRtnVal == -2){
				
				szMsg = "재료번호("+ szStlNo    +")에 대한 크레인스케줄 작업 재료 조회중 parameter error 발생.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "재료번호("+ szStlNo    +")에 대한 크레인스케줄 작업 재료 조회중 오류 발생.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e){
			szMsg = "크레인작업재료 유무 체크 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of chkCrnWrkMtl
	
	
	
	/**
	 * 오퍼레이션명 : C열연 소재 임가공 Lot 편성 데이터 유무체크 및 데이터 반환
	 *  
	 * @param  JDTORecord    recInPara 파라미터 레코드
	 *         JDTORecordSet rsResult  결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetCHRMatlRentProcLotGp(JDTORecord recInPara, JDTORecordSet rsResult)throws JDTOException  {
		
		//저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		//리턴값(boolean)
		boolean blnRtnVal     = false;
		//리턴값(int)
		int intRtnVal         = 0;
		//메소드명
		String szMethodName   = "chkGetCHRMatlRentProcLotGp";
		String szMsg          = null;
		
		//레코드 선언
		JDTORecord recPara        = null;	

		try {
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();

			//재료품목
			recPara.setField("YD_MTL_ITEM",   ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_ITEM"));
			//목표행선구분
			recPara.setField("YD_AIM_RT_GP",  ydDaoUtils.paraRecChkNull(recInPara, "YD_AIM_RT_GP"));
			//임가공사코드
			//recPara.setField("RENTPROC_CD",  ydDaoUtils.paraRecChkNull(recInPara, "RENTPROC_CD"));
			//test용 임시 임가공사코드 필드 -> TB_YD_STOCK.FRTOMOVE_PLANT_GP
			recPara.setField("FRTOMOVE_PLANT_GP",  ydDaoUtils.paraRecChkNull(recInPara, "RENTPROC_CD"));
//			//목표야드구분
//			recPara.setField("YD_AIM_YD_GP",  ydDaoUtils.paraRecChkNull(recInPara, "YD_AIM_YD_GP"));
//			//목표동구분
//			recPara.setField("YD_AIM_BAY_GP", ydDaoUtils.paraRecChkNull(recInPara, "YD_AIM_BAY_GP"));
			//FROM 야드동
			recPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(recInPara, "YD_CARLD_STOP_LOC").substring(0, 2));
			
			//저장품 테이블 조회
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 17);
			
			//리턴값 메세지처리
			if(intRtnVal >= 1){

				blnRtnVal = true;
				
			} else if(intRtnVal == 0){
				
				szMsg = "C열연 소재 임가공 Lot 편성 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == -2){
				
				szMsg = "C열연 소재 임가공 Lot 편성 데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "C열연 소재 임가공 Lot 편성 데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e){
			szMsg = "C열연 소재 임가공 Lot 편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of chkGetCHRMatlRentProcLotGp
	
	
	
	/**
	 * 오퍼레이션명 : C연주 외판출하 상차 LOT 편성 데이터 유무체크 및 데이터 반환
	 *  
	 * @param  String        szSchCd   스케줄코드
	 *         JDTORecordSet rsResult  결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetOutPlDistCarLoadLotGp(JDTORecord recInPara, JDTORecordSet rsResult)throws JDTOException  {
		
		//저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		//리턴값(boolean)
		boolean blnRtnVal     = false;
		//리턴값(int)
		int intRtnVal         = 0;
		//메소드명
		String szMethodName   = "chkGetOutPlDistCarLoadLotGp";
		String szMsg          = null;
		
		//레코드 선언
		JDTORecord recPara        = null;	

		try {
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();

			//재료품목
			recPara.setField("DEST_TEL_NO",   ydDaoUtils.paraRecChkNull(recInPara, "DEST_TEL_NO"));
			//FROM 야드동
			recPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(recInPara, "YD_CARLD_STOP_LOC").substring(0, 2));
			
			//저장품 테이블 조회 20090805 김진욱 수정 목적지 전화번호 검
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 111);
			
			//리턴값 메세지처리
			if(intRtnVal >= 1){

				blnRtnVal = true;
				
			} else if(intRtnVal == 0){
				
				szMsg = "C연주 외판출하 상차 LOT 편성 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == -2){
				
				szMsg = "C연주 외판출하 상차 LOT 편성 데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "C연주 외판출하 상차 LOT 편성 데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e){
			szMsg = "C연주 외판출하 상차 LOT 편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of chkGetOutPlDistCarLoadLotGp
	
	
	
	
	
	
	
	/**
	 * 오퍼레이션명 : 출하상차 LOT 편성 데이터 유무체크 및 데이터 반환
	 *  
	 * @param  String        szSchCd   스케줄코드
	 *         JDTORecordSet rsResult  결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkDistCarLoadLotGp(JDTORecord recInPara, JDTORecordSet rsResult)throws JDTOException  {
		
		//저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		//리턴값(boolean)
		boolean blnRtnVal     = false;
		//리턴값(int)
		int intRtnVal         = 0;
		//메소드명
		String szMethodName   = "chkGetOutPlDistCarLoadLotGp";
		String szMsg          = null;
		String szYD_STK_COL_GP = null;
		
		//레코드 선언
		JDTORecord recPara        = null;	

		try {
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recInPara, "YD_CARLD_STOP_LOC");
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();

			//카드번호
			recPara.setField("CARD_NO",   ydDaoUtils.paraRecChkNull(recInPara, "CARD_NO"));
			
			//운송지시일자
			recPara.setField("TRANS_ORD_DATE",   ydDaoUtils.paraRecChkNull(recInPara, "TRANS_ORD_DATE"));
			
			//운송지시순번
			recPara.setField("TRANS_ORD_SEQNO",   ydDaoUtils.paraRecChkNull(recInPara, "TRANS_ORD_SEQNO"));
			
			//야드동
			recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP.substring(0, 2));
			
			//저장품 테이블 조회
			//intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 38);
			if( szYD_STK_COL_GP.substring(0, 1).equals(YdConstant.YD_GP_C_SLAB_YARD)				/* C연주슬라브야드 */
				|| szYD_STK_COL_GP.substring(0, 1).equals(YdConstant.YD_GP_C_HR_COIL_GDS_YARD)		/* C열연코일제품야드 */
			) {
				//적치열 Desc, 적치베드Asc, 적치단 Desc
				intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 126);
			}else{
//PIDEV_S :병행가동용:PI_YD
				recPara.setField("PI_YD",    	szYD_STK_COL_GP.substring(0, 1));							
				//적치열 Asc, 적치베드Asc, 적치단 Desc
				intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 128);
			}
			
			//리턴값 메세지처리
			if(intRtnVal >= 1){

				blnRtnVal = true;
				
			} else if(intRtnVal == 0){
				
				szMsg = "출하 상차 LOT 편성 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == -2){
				
				szMsg = "출하 상차 LOT 편성 데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "출하 상차 LOT 편성 데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e){
			szMsg = "출하 상차 LOT 편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of chkGetOutPlDistCarLoadLotGp
	
		
	
	
	
	
	
	
	
	/**
	 * 오퍼레이션명 : C연주 소재이송 상차 LOT 편성 데이터 유무체크 및 데이터 반환
	 *  
	 * @param  String        szSchCd   스케줄코드
	 *         JDTORecordSet rsResult  결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public int chkGetCCsMtlFtmvCarLoadLotGp(JDTORecord recInPara, JDTORecordSet rsResult)throws JDTOException  {
		
		//저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		//리턴값(boolean)
		boolean blnRtnVal     = false;
		//리턴값(int)
		int intRtnVal         = -100;
		//메소드명
		String szMethodName   = "chkGetCCsMtlFtmvCarLoadLotGp";
		String szMsg          = null;
		
		//레코드 선언
		JDTORecord recPara        = null;	
		JDTORecordSet rsTemp	= JDTORecordFactory.getInstance().createRecordSet("");
		//야드구분
		String szYD_GP			= null;
		//야드동구분
		String szYD_BAY_GP		= null;
		//목표야드구분
		String sz_YD_AIM_YD_GP	= null;
		//야드목표행선
		String szYD_AIM_RT_GP1	= null;
		String szYD_AIM_RT_GP2	= null;
		String szYD_AIM_RT_GP3	= null;
		
		try {
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();
			
			
			szYD_GP = "A";					//C연주슬라브야드
			/*
			 * 직상차 용 대상재 조회
			 */
			/*
			 * 1. D동 PICKUP BED설비, B열연 이송대상재 - B열연HCR지시대기(B1), B열연압연작업대기(C1), B열연HCR 재공이송작업대기(E1) 순으로 조회
			 */
			szYD_BAY_GP = "D";												//D동	
			sz_YD_AIM_YD_GP = "2";											//B열연
			szYD_AIM_RT_GP1 = "B1";
			szYD_AIM_RT_GP2 = "C1";
			szYD_AIM_RT_GP3 = "E1";
			recPara.setField("YD_GP", szYD_GP);								//현 야드구분
			recPara.setField("YD_BAY_GP", szYD_BAY_GP);						//동구분
			recPara.setField("YD_AIM_YD_GP", sz_YD_AIM_YD_GP);				//목표야드구분
			recPara.setField("YD_AIM_RT_GP1", szYD_AIM_RT_GP1);
			recPara.setField("YD_AIM_RT_GP2", szYD_AIM_RT_GP2);
			recPara.setField("YD_AIM_RT_GP3", szYD_AIM_RT_GP3);
			intRtnVal = ydStockDao.getYdStock(recPara, rsTemp, 114);
			if(intRtnVal > 0){
				YdCommonUtils.filterStockStl(rsTemp, rsResult);
				szMsg = "C연주 소재이송 상차 LOT 편성 - D동 직상차용 B열연 이송대상재[야드목표행선:"+szYD_AIM_RT_GP1+","+szYD_AIM_RT_GP2+","+szYD_AIM_RT_GP3+"]가 존재합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return 100;
			}
			szMsg = "C연주 소재이송 상차 LOT 편성 - D동 직상차용 B열연 이송대상재[야드목표행선:"+szYD_AIM_RT_GP1+","+szYD_AIM_RT_GP2+","+szYD_AIM_RT_GP3+"]가 존재하지 않습니다.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			/*
			 * 2. C동 PICKUP BED설비, B열연 이송대상재 - B열연HCR지시대기(B1), B열연압연작업대기(C1), B열연HCR 재공이송작업대기(E1) 순으로 조회
			 */
			szYD_BAY_GP = "C";
			recPara.setField("YD_BAY_GP", szYD_BAY_GP);						//동구분
			intRtnVal = ydStockDao.getYdStock(recPara, rsTemp, 114);
			if(intRtnVal > 0){
				YdCommonUtils.filterStockStl(rsTemp, rsResult);
				szMsg = "C연주 소재이송 상차 LOT 편성 - C동 직상차용 B열연 이송대상재[야드목표행선:"+szYD_AIM_RT_GP1+","+szYD_AIM_RT_GP2+","+szYD_AIM_RT_GP3+"]가 존재합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return 100;
			}
			szMsg = "C연주 소재이송 상차 LOT 편성 - C동 직상차용 B열연 이송대상재[야드목표행선:"+szYD_AIM_RT_GP1+","+szYD_AIM_RT_GP2+","+szYD_AIM_RT_GP3+"]가 존재하지 않습니다.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			/*
			 * 3. D동 PICKUP BED설비, B열연 이송대상재 - B열연CCR스카핑 정정작업대기(A1), B열연스카핑 재공이송작업대기(E2), B열연압연 재공충당대기(Y1) 순으로 조회
			 */
			szYD_BAY_GP = "D";												//D동	
			sz_YD_AIM_YD_GP = "2";											//B열연
			szYD_AIM_RT_GP1 = "A1";
			szYD_AIM_RT_GP2 = "E2";
			szYD_AIM_RT_GP3 = "Y1";
			recPara.setField("YD_GP", szYD_GP);								//현 야드구분
			recPara.setField("YD_BAY_GP", szYD_BAY_GP);						//동구분
			recPara.setField("YD_AIM_YD_GP", sz_YD_AIM_YD_GP);				//목표야드구분
			recPara.setField("YD_AIM_RT_GP1", szYD_AIM_RT_GP1);
			recPara.setField("YD_AIM_RT_GP2", szYD_AIM_RT_GP2);
			recPara.setField("YD_AIM_RT_GP3", szYD_AIM_RT_GP3);
			intRtnVal = ydStockDao.getYdStock(recPara, rsTemp, 114);
			if(intRtnVal > 0){
				YdCommonUtils.filterStockStl(rsTemp, rsResult);
				szMsg = "C연주 소재이송 상차 LOT 편성 - " + szYD_BAY_GP + "동 직상차용 B열연 이송대상재[야드목표행선:"+szYD_AIM_RT_GP1+","+szYD_AIM_RT_GP2+","+szYD_AIM_RT_GP3+"]가 존재합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return 100;
			}
			szMsg = "C연주 소재이송 상차 LOT 편성 - " + szYD_BAY_GP + "동 직상차용 B열연 이송대상재[야드목표행선:"+szYD_AIM_RT_GP1+","+szYD_AIM_RT_GP2+","+szYD_AIM_RT_GP3+"]가 존재하지 않습니다.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			/*
			 * 4. C동 PICKUP BED설비, B열연 이송대상재 - B열연CCR스카핑 정정작업대기(A1), B열연스카핑 재공이송작업대기(E2), B열연압연 재공충당대기(Y1) 순으로 조회
			 */
			szYD_BAY_GP = "C";
			recPara.setField("YD_BAY_GP", szYD_BAY_GP);						//동구분
			intRtnVal = ydStockDao.getYdStock(recPara, rsTemp, 114);
			if(intRtnVal > 0){
				YdCommonUtils.filterStockStl(rsTemp, rsResult);
				szMsg = "C연주 소재이송 상차 LOT 편성 - " + szYD_BAY_GP + "동 직상차용 B열연 이송대상재[야드목표행선:"+szYD_AIM_RT_GP1+","+szYD_AIM_RT_GP2+","+szYD_AIM_RT_GP3+"]가 존재합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return 100;
			}
			szMsg = "C연주 소재이송 상차 LOT 편성 - " + szYD_BAY_GP + "동 직상차용 B열연 이송대상재[야드목표행선:"+szYD_AIM_RT_GP1+","+szYD_AIM_RT_GP2+","+szYD_AIM_RT_GP3+"]가 존재하지 않습니다.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			/*
			 * 5. D동 PICKUP BED설비, B열연 이송대상재 - B열연CCR지시대기(B2), B열연Non스카핑 재공이송작업대기(E3), C열연압연 재공충당대기(Y2) 순으로 조회
			 */
			szYD_BAY_GP = "D";												//D동	
			sz_YD_AIM_YD_GP = "2";											//B열연
			szYD_AIM_RT_GP1 = "B2";
			szYD_AIM_RT_GP2 = "E3";
			szYD_AIM_RT_GP3 = "Y2";
			recPara.setField("YD_GP", szYD_GP);								//현 야드구분
			recPara.setField("YD_BAY_GP", szYD_BAY_GP);						//동구분
			recPara.setField("YD_AIM_YD_GP", sz_YD_AIM_YD_GP);				//목표야드구분
			recPara.setField("YD_AIM_RT_GP1", szYD_AIM_RT_GP1);
			recPara.setField("YD_AIM_RT_GP2", szYD_AIM_RT_GP2);
			recPara.setField("YD_AIM_RT_GP3", szYD_AIM_RT_GP3);
			intRtnVal = ydStockDao.getYdStock(recPara, rsTemp, 114);
			if(intRtnVal > 0){
				YdCommonUtils.filterStockStl(rsTemp, rsResult);
				szMsg = "C연주 소재이송 상차 LOT 편성 - " + szYD_BAY_GP + "동 직상차용 B열연 이송대상재[야드목표행선:"+szYD_AIM_RT_GP1+","+szYD_AIM_RT_GP2+","+szYD_AIM_RT_GP3+"]가 존재합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return 100;
			}
			szMsg = "C연주 소재이송 상차 LOT 편성 - " + szYD_BAY_GP + "동 직상차용 B열연 이송대상재[야드목표행선:"+szYD_AIM_RT_GP1+","+szYD_AIM_RT_GP2+","+szYD_AIM_RT_GP3+"]가 존재하지 않습니다.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			/*
			 * 6. C동 PICKUP BED설비, B열연 이송대상재 - B열연CCR지시대기(B2), B열연Non스카핑 재공이송작업대기(E3), C열연압연 재공충당대기(Y2) 순으로 조회
			 */
			szYD_BAY_GP = "C";
			recPara.setField("YD_BAY_GP", szYD_BAY_GP);						//동구분
			intRtnVal = ydStockDao.getYdStock(recPara, rsTemp, 114);
			if(intRtnVal > 0){
				YdCommonUtils.filterStockStl(rsTemp, rsResult);
				szMsg = "C연주 소재이송 상차 LOT 편성 - " + szYD_BAY_GP + "동 직상차용 B열연 이송대상재[야드목표행선:"+szYD_AIM_RT_GP1+","+szYD_AIM_RT_GP2+","+szYD_AIM_RT_GP3+"]가 존재합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return 100;
			}
			szMsg = "C연주 소재이송 상차 LOT 편성 - " + szYD_BAY_GP + "동 직상차용 B열연 이송대상재[야드목표행선:"+szYD_AIM_RT_GP1+","+szYD_AIM_RT_GP2+","+szYD_AIM_RT_GP3+"]가 존재하지 않습니다.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			

			
			/*
			 * 야드내 대상재 조회
			 */
			
			
			
			
			/*
			 * 7. D동 , B열연 이송대상재 - B열연HCR지시대기(B1), B열연압연작업대기(C1), B열연HCR 재공이송작업대기(E1) 순으로 조회
			 */
			szYD_BAY_GP = "D";												//D동	
			sz_YD_AIM_YD_GP = "2";											//B열연
			szYD_AIM_RT_GP1 = "B1";
			szYD_AIM_RT_GP2 = "C1";
			szYD_AIM_RT_GP3 = "E1";
			recPara.setField("YD_GP", szYD_GP);								//현 야드구분
			recPara.setField("YD_BAY_GP", szYD_BAY_GP);						//동구분
			recPara.setField("YD_AIM_YD_GP", sz_YD_AIM_YD_GP);				//목표야드구분
			recPara.setField("YD_AIM_RT_GP1", szYD_AIM_RT_GP1);
			recPara.setField("YD_AIM_RT_GP2", szYD_AIM_RT_GP2);
			recPara.setField("YD_AIM_RT_GP3", szYD_AIM_RT_GP3);
			intRtnVal = ydStockDao.getYdStock(recPara, rsTemp, 115);
			if(intRtnVal > 0){
				YdCommonUtils.filterStockStl(rsTemp, rsResult);
				szMsg = "C연주 소재이송 상차 LOT 편성 - " + szYD_BAY_GP + "동야드 B열연 이송대상재[야드목표행선:"+szYD_AIM_RT_GP1+","+szYD_AIM_RT_GP2+","+szYD_AIM_RT_GP3+"]가 존재합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return 200;
			}
			szMsg = "C연주 소재이송 상차 LOT 편성 - " + szYD_BAY_GP + "동야드 B열연 이송대상재[야드목표행선:"+szYD_AIM_RT_GP1+","+szYD_AIM_RT_GP2+","+szYD_AIM_RT_GP3+"]가 존재하지 않습니다.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			/*
			 * 8. C동, B열연 이송대상재 - B열연HCR지시대기(B1), B열연압연작업대기(C1), B열연HCR 재공이송작업대기(E1) 순으로 조회
			 */
			szYD_BAY_GP = "C";
			recPara.setField("YD_BAY_GP", szYD_BAY_GP);						//동구분
			intRtnVal = ydStockDao.getYdStock(recPara, rsTemp, 115);
			if(intRtnVal > 0){
				YdCommonUtils.filterStockStl(rsTemp, rsResult);
				szMsg = "C연주 소재이송 상차 LOT 편성 - " + szYD_BAY_GP + "동야드 B열연 이송대상재[야드목표행선:"+szYD_AIM_RT_GP1+","+szYD_AIM_RT_GP2+","+szYD_AIM_RT_GP3+"]가 존재합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return 210;
			}
			szMsg = "C연주 소재이송 상차 LOT 편성 - " + szYD_BAY_GP + "동야드 B열연 이송대상재[야드목표행선:"+szYD_AIM_RT_GP1+","+szYD_AIM_RT_GP2+","+szYD_AIM_RT_GP3+"]가 존재하지 않습니다.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			/*
			 * 9. B동, B열연 이송대상재 - B열연HCR지시대기(B1), B열연압연작업대기(C1), B열연HCR 재공이송작업대기(E1) 순으로 조회
			 */
			szYD_BAY_GP = "B";
			recPara.setField("YD_BAY_GP", szYD_BAY_GP);						//동구분
			intRtnVal = ydStockDao.getYdStock(recPara, rsTemp, 115);
			if(intRtnVal > 0){
				YdCommonUtils.filterStockStl(rsTemp, rsResult);
				szMsg = "C연주 소재이송 상차 LOT 편성 - " + szYD_BAY_GP + "동야드 B열연 이송대상재[야드목표행선:"+szYD_AIM_RT_GP1+","+szYD_AIM_RT_GP2+","+szYD_AIM_RT_GP3+"]가 존재합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return 220;
			}
			szMsg = "C연주 소재이송 상차 LOT 편성 - " + szYD_BAY_GP + "동야드 B열연 이송대상재[야드목표행선:"+szYD_AIM_RT_GP1+","+szYD_AIM_RT_GP2+","+szYD_AIM_RT_GP3+"]가 존재하지 않습니다.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			/*
			 * 10. A동, B열연 이송대상재 - B열연HCR지시대기(B1), B열연압연작업대기(C1), B열연HCR 재공이송작업대기(E1) 순으로 조회
			 */
			szYD_BAY_GP = "A";
			recPara.setField("YD_BAY_GP", szYD_BAY_GP);						//동구분
			intRtnVal = ydStockDao.getYdStock(recPara, rsTemp, 115);
			if(intRtnVal > 0){
				YdCommonUtils.filterStockStl(rsTemp, rsResult);
				szMsg = "C연주 소재이송 상차 LOT 편성 - " + szYD_BAY_GP + "동야드 B열연 이송대상재[야드목표행선:"+szYD_AIM_RT_GP1+","+szYD_AIM_RT_GP2+","+szYD_AIM_RT_GP3+"]가 존재합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return 230;
			}
			szMsg = "C연주 소재이송 상차 LOT 편성 - " + szYD_BAY_GP + "동야드 B열연 이송대상재[야드목표행선:"+szYD_AIM_RT_GP1+","+szYD_AIM_RT_GP2+","+szYD_AIM_RT_GP3+"]가 존재하지 않습니다.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			
			
			
			
			/*
			 * 11. D동, B열연 이송대상재 - B열연CCR스카핑 정정작업대기(A1), B열연스카핑 재공이송작업대기(E2), B열연압연 재공충당대기(Y1) 순으로 조회
			 */
			szYD_BAY_GP = "D";												//D동	
			sz_YD_AIM_YD_GP = "2";											//B열연
			szYD_AIM_RT_GP1 = "A1";
			szYD_AIM_RT_GP2 = "E2";
			szYD_AIM_RT_GP3 = "Y1";
			recPara.setField("YD_GP", szYD_GP);								//현 야드구분
			recPara.setField("YD_BAY_GP", szYD_BAY_GP);						//동구분
			recPara.setField("YD_AIM_YD_GP", sz_YD_AIM_YD_GP);				//목표야드구분
			recPara.setField("YD_AIM_RT_GP1", szYD_AIM_RT_GP1);
			recPara.setField("YD_AIM_RT_GP2", szYD_AIM_RT_GP2);
			recPara.setField("YD_AIM_RT_GP3", szYD_AIM_RT_GP3);
			intRtnVal = ydStockDao.getYdStock(recPara, rsTemp, 115);
			if(intRtnVal > 0){
				YdCommonUtils.filterStockStl(rsTemp, rsResult);
				szMsg = "C연주 소재이송 상차 LOT 편성 - " + szYD_BAY_GP + "동야드 B열연 이송대상재[야드목표행선:"+szYD_AIM_RT_GP1+","+szYD_AIM_RT_GP2+","+szYD_AIM_RT_GP3+"]가 존재합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return 300;
			}
			szMsg = "C연주 소재이송 상차 LOT 편성 - " + szYD_BAY_GP + "동야드 B열연 이송대상재[야드목표행선:"+szYD_AIM_RT_GP1+","+szYD_AIM_RT_GP2+","+szYD_AIM_RT_GP3+"]가 존재하지 않습니다.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			/*
			 * 12. C동, B열연 이송대상재 - B열연CCR스카핑 정정작업대기(A1), B열연스카핑 재공이송작업대기(E2), B열연압연 재공충당대기(Y1) 순으로 조회
			 */
			szYD_BAY_GP = "C";
			recPara.setField("YD_BAY_GP", szYD_BAY_GP);						//동구분
			intRtnVal = ydStockDao.getYdStock(recPara, rsTemp, 115);
			if(intRtnVal > 0){
				YdCommonUtils.filterStockStl(rsTemp, rsResult);
				szMsg = "C연주 소재이송 상차 LOT 편성 - " + szYD_BAY_GP + "동야드 B열연 이송대상재[야드목표행선:"+szYD_AIM_RT_GP1+","+szYD_AIM_RT_GP2+","+szYD_AIM_RT_GP3+"]가 존재합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return 310;
			}
			szMsg = "C연주 소재이송 상차 LOT 편성 - " + szYD_BAY_GP + "동야드 B열연 이송대상재[야드목표행선:"+szYD_AIM_RT_GP1+","+szYD_AIM_RT_GP2+","+szYD_AIM_RT_GP3+"]가 존재하지 않습니다.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			/*
			 * 13. B동, B열연 이송대상재 - B열연CCR스카핑 정정작업대기(A1), B열연스카핑 재공이송작업대기(E2), B열연압연 재공충당대기(Y1) 순으로 조회
			 */
			szYD_BAY_GP = "B";
			recPara.setField("YD_BAY_GP", szYD_BAY_GP);						//동구분
			intRtnVal = ydStockDao.getYdStock(recPara, rsTemp, 115);
			if(intRtnVal > 0){
				YdCommonUtils.filterStockStl(rsTemp, rsResult);
				szMsg = "C연주 소재이송 상차 LOT 편성 - " + szYD_BAY_GP + "동야드 B열연 이송대상재[야드목표행선:"+szYD_AIM_RT_GP1+","+szYD_AIM_RT_GP2+","+szYD_AIM_RT_GP3+"]가 존재합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return 320;
			}
			szMsg = "C연주 소재이송 상차 LOT 편성 - " + szYD_BAY_GP + "동야드 B열연 이송대상재[야드목표행선:"+szYD_AIM_RT_GP1+","+szYD_AIM_RT_GP2+","+szYD_AIM_RT_GP3+"]가 존재하지 않습니다.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			/*
			 * 14. A동, B열연 이송대상재 - B열연CCR스카핑 정정작업대기(A1), B열연스카핑 재공이송작업대기(E2), B열연압연 재공충당대기(Y1) 순으로 조회
			 */
			szYD_BAY_GP = "A";
			recPara.setField("YD_BAY_GP", szYD_BAY_GP);						//동구분
			intRtnVal = ydStockDao.getYdStock(recPara, rsTemp, 115);
			if(intRtnVal > 0){
				YdCommonUtils.filterStockStl(rsTemp, rsResult);
				szMsg = "C연주 소재이송 상차 LOT 편성 - " + szYD_BAY_GP + "동야드 B열연 이송대상재[야드목표행선:"+szYD_AIM_RT_GP1+","+szYD_AIM_RT_GP2+","+szYD_AIM_RT_GP3+"]가 존재합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return 330;
			}
			szMsg = "C연주 소재이송 상차 LOT 편성 - " + szYD_BAY_GP + "동야드 B열연 이송대상재[야드목표행선:"+szYD_AIM_RT_GP1+","+szYD_AIM_RT_GP2+","+szYD_AIM_RT_GP3+"]가 존재하지 않습니다.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			
			
			
			/*
			 * 15. D동, B열연 이송대상재 - B열연CCR지시대기(B2), B열연Non스카핑 재공이송작업대기(E3), C열연압연 재공충당대기(Y2) 순으로 조회
			 */
			
			szYD_BAY_GP = "D";												//D동	
			sz_YD_AIM_YD_GP = "2";											//B열연
			szYD_AIM_RT_GP1 = "B2";
			szYD_AIM_RT_GP2 = "E3";
			szYD_AIM_RT_GP3 = "Y2";
			recPara.setField("YD_GP", szYD_GP);								//현 야드구분
			recPara.setField("YD_BAY_GP", szYD_BAY_GP);						//동구분
			recPara.setField("YD_AIM_YD_GP", sz_YD_AIM_YD_GP);				//목표야드구분
			recPara.setField("YD_AIM_RT_GP1", szYD_AIM_RT_GP1);
			recPara.setField("YD_AIM_RT_GP2", szYD_AIM_RT_GP2);
			recPara.setField("YD_AIM_RT_GP3", szYD_AIM_RT_GP3);
			intRtnVal = ydStockDao.getYdStock(recPara, rsTemp, 115);
			if(intRtnVal > 0){
				YdCommonUtils.filterStockStl(rsTemp, rsResult);
				szMsg = "C연주 소재이송 상차 LOT 편성 - " + szYD_BAY_GP + "동야드 B열연 이송대상재[야드목표행선:"+szYD_AIM_RT_GP1+","+szYD_AIM_RT_GP2+","+szYD_AIM_RT_GP3+"]가 존재합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return 400;
			}
			szMsg = "C연주 소재이송 상차 LOT 편성 - " + szYD_BAY_GP + "동야드 B열연 이송대상재[야드목표행선:"+szYD_AIM_RT_GP1+","+szYD_AIM_RT_GP2+","+szYD_AIM_RT_GP3+"]가 존재하지 않습니다.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			/*
			 * 16. C동, B열연 이송대상재 - B열연CCR지시대기(B2), B열연Non스카핑 재공이송작업대기(E3), C열연압연 재공충당대기(Y2) 순으로 조회
			 */
			szYD_BAY_GP = "C";
			recPara.setField("YD_BAY_GP", szYD_BAY_GP);						//동구분
			intRtnVal = ydStockDao.getYdStock(recPara, rsTemp, 115);
			if(intRtnVal > 0){
				YdCommonUtils.filterStockStl(rsTemp, rsResult);
				szMsg = "C연주 소재이송 상차 LOT 편성 - " + szYD_BAY_GP + "동야드 B열연 이송대상재[야드목표행선:"+szYD_AIM_RT_GP1+","+szYD_AIM_RT_GP2+","+szYD_AIM_RT_GP3+"]가 존재합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return 410;
			}
			szMsg = "C연주 소재이송 상차 LOT 편성 - " + szYD_BAY_GP + "동야드 B열연 이송대상재[야드목표행선:"+szYD_AIM_RT_GP1+","+szYD_AIM_RT_GP2+","+szYD_AIM_RT_GP3+"]가 존재하지 않습니다.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			/*
			 * 17. B동, B열연 이송대상재 - B열연CCR지시대기(B2), B열연Non스카핑 재공이송작업대기(E3), C열연압연 재공충당대기(Y2) 순으로 조회
			 */
			szYD_BAY_GP = "B";
			recPara.setField("YD_BAY_GP", szYD_BAY_GP);						//동구분
			intRtnVal = ydStockDao.getYdStock(recPara, rsTemp, 115);
			if(intRtnVal > 0){
				YdCommonUtils.filterStockStl(rsTemp, rsResult);
				szMsg = "C연주 소재이송 상차 LOT 편성 - " + szYD_BAY_GP + "동야드 B열연 이송대상재[야드목표행선:"+szYD_AIM_RT_GP1+","+szYD_AIM_RT_GP2+","+szYD_AIM_RT_GP3+"]가 존재합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return 420;
			}
			szMsg = "C연주 소재이송 상차 LOT 편성 - " + szYD_BAY_GP + "동야드 B열연 이송대상재[야드목표행선:"+szYD_AIM_RT_GP1+","+szYD_AIM_RT_GP2+","+szYD_AIM_RT_GP3+"]가 존재하지 않습니다.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			/*
			 * 18. A동, B열연 이송대상재 - B열연CCR지시대기(B2), B열연Non스카핑 재공이송작업대기(E3), C열연압연 재공충당대기(Y2) 순으로 조회
			 */
			szYD_BAY_GP = "A";
			recPara.setField("YD_BAY_GP", szYD_BAY_GP);						//동구분
			intRtnVal = ydStockDao.getYdStock(recPara, rsTemp, 115);
			if(intRtnVal > 0){
				YdCommonUtils.filterStockStl(rsTemp, rsResult);
				szMsg = "C연주 소재이송 상차 LOT 편성 - " + szYD_BAY_GP + "동야드 B열연 이송대상재[야드목표행선:"+szYD_AIM_RT_GP1+","+szYD_AIM_RT_GP2+","+szYD_AIM_RT_GP3+"]가 존재합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return 430;
			}
			szMsg = "C연주 소재이송 상차 LOT 편성 - " + szYD_BAY_GP + "동야드 B열연 이송대상재[야드목표행선:"+szYD_AIM_RT_GP1+","+szYD_AIM_RT_GP2+","+szYD_AIM_RT_GP3+"]가 존재하지 않습니다.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++B열연 이송대상재 조회 끝++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			
			/*
			 * 야드내 통합야드 이송대상재 조회
			 */
			/*+++++++++++++++ 야드에 적치된 통합야드 이송대상재 조회 시작 +++++++++++++++*/
			
			//19. D동에 적치된 대상재를 조회 - 통합야드 이송대상재
			szYD_BAY_GP = "D";												//D동	
			sz_YD_AIM_YD_GP = "S";											//통합야드
			recPara.setField("YD_GP", szYD_GP);								//현 야드구분
			recPara.setField("YD_BAY_GP", szYD_BAY_GP);						//동구분
			recPara.setField("YD_AIM_YD_GP", sz_YD_AIM_YD_GP);				//목표야드구분
			intRtnVal = ydStockDao.getYdStock(recPara, rsTemp, 106);
			if(intRtnVal > 0){
				YdCommonUtils.filterStockStl(rsTemp, rsResult);
				szMsg = "C연주 소재이송 상차 LOT 편성 - " + szYD_BAY_GP + "동야드에 통합야드 이송대상재가 존재합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return 500;
			}
			
			szMsg = "C연주 소재이송 상차 LOT 편성 - " + szYD_BAY_GP + "동야드에 통합야드 이송대상재가 존재하지 않습니다.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//20. C동에 적치된 대상재를 조회 - 통합야드 이송대상재
			szYD_BAY_GP = "C";												//C동	
			sz_YD_AIM_YD_GP = "S";											//통합야드
			recPara.setField("YD_GP", szYD_GP);								//현 야드구분
			recPara.setField("YD_BAY_GP", szYD_BAY_GP);						//동구분
			recPara.setField("YD_AIM_YD_GP", sz_YD_AIM_YD_GP);				//목표야드구분
			intRtnVal = ydStockDao.getYdStock(recPara, rsTemp, 106);
			if(intRtnVal > 0){
				YdCommonUtils.filterStockStl(rsTemp, rsResult);
				szMsg = "C연주 소재이송 상차 LOT 편성 - " + szYD_BAY_GP + "동야드에 통합야드 이송대상재가 존재합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return 600;
			}
			szMsg = "C연주 소재이송 상차 LOT 편성 - " + szYD_BAY_GP + "동야드에 통합야드 이송대상재가 존재하지 않습니다.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//21. B동에 적치된 대상재를 조회 - 통합야드 이송대상재
			szYD_BAY_GP = "B";												//B동	
			sz_YD_AIM_YD_GP = "S";											//통합야드
			recPara.setField("YD_GP", szYD_GP);								//현 야드구분
			recPara.setField("YD_BAY_GP", szYD_BAY_GP);						//동구분
			recPara.setField("YD_AIM_YD_GP", sz_YD_AIM_YD_GP);				//목표야드구분
			intRtnVal = ydStockDao.getYdStock(recPara, rsTemp, 106);
			if(intRtnVal > 0){
				YdCommonUtils.filterStockStl(rsTemp, rsResult);
				szMsg = "C연주 소재이송 상차 LOT 편성 - " + szYD_BAY_GP + "동야드에 통합야드 이송대상재가 존재합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return 700;
			}
			szMsg = "C연주 소재이송 상차 LOT 편성 - " + szYD_BAY_GP + "동야드에 통합야드 이송대상재가 존재하지 않습니다.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//22. A동에 적치된 대상재를 조회 - 통합야드 이송대상재
			szYD_BAY_GP = "A";												//A동	
			sz_YD_AIM_YD_GP = "S";											//통합야드
			recPara.setField("YD_GP", szYD_GP);								//현 야드구분
			recPara.setField("YD_BAY_GP", szYD_BAY_GP);						//동구분
			recPara.setField("YD_AIM_YD_GP", sz_YD_AIM_YD_GP);				//목표야드구분
			intRtnVal = ydStockDao.getYdStock(recPara, rsTemp, 106);
			if(intRtnVal > 0){
				YdCommonUtils.filterStockStl(rsTemp, rsResult);
				szMsg = "C연주 소재이송 상차 LOT 편성 - " + szYD_BAY_GP + "동야드에 통합야드 이송대상재가 존재합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return 800;
			}
			szMsg = "C연주 소재이송 상차 LOT 편성 - " + szYD_BAY_GP + "동야드에 통합야드 이송대상재가 존재하지 않습니다.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			/*+++++++++++++++ 야드에 적치된 통합야드 이송대상재 조회 끝 +++++++++++++++*/
			
			//대상 소재가 존재하면 리턴
			
			if(intRtnVal == 0){
				
				szMsg = "C연주 소재이송 상차 LOT 편성 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				return 0;
				
			} else if(intRtnVal == -2){
				
				szMsg = "C연주 소재이송 상차 LOT 편성 데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "C연주 소재이송 상차 LOT 편성 데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
			
		} catch(Exception e){
			szMsg = "C연주 소재이송 상차 LOT 편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return intRtnVal;
	} //end of chkGetCCsMtlFtmvCarLoadLotGp
	
	
	
	
	
	
	
	
	
	
	/**
	 * 오퍼레이션명 : A후판 소재이송 상차 LOT 편성 데이터 유무체크 및 데이터 반환
	 *  
	 * @param  String        szSchCd   스케줄코드
	 *         JDTORecordSet rsResult  결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public int chkGetACsMtlFtmvCarLoadLotGp(JDTORecord recInPara, JDTORecordSet rsResult)throws JDTOException  {
		
		//저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		//리턴값(boolean)
		boolean blnRtnVal     = false;
		//리턴값(int)
		int intRtnVal         = -100;
		//메소드명
		String szMethodName   = "chkGetACsMtlFtmvCarLoadLotGp";
		String szMsg          = null;
		
		//레코드 선언
		JDTORecord recPara        = null;	

		try {
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();
			
			
			
//			//저장품 테이블 조회(긴급구분 = 'Y')
//			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 17);
//			//대상 소재가 존재하면 리턴
//			if(intRtnVal > 0) return blnRtnVal = true;
			
			//직상차용 대상재 조회(D동 PICKUP BED 2개, C동 PICKUP BED 1개, C동 DEPILER 2개)
//			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 54);
//			if(intRtnVal > 0) return 100;
			
			//C연주 D동에 있는 B열연 이송대기인 HCR 열연주편
			//재료품목(열연주편)
			recPara.setField("YD_MTL_ITEM", "SP");
			//목표행선구분(C연주 B열연 이송대기)
			//recPara.setField("YD_AIM_RT_GP", "D5");
			recPara.setField("YD_AIM_RT_GP", YdConstant.AR_INLINE_FTMV_WRK_WAIT_A_SP);							//재공이송작업대기(A후판슬라브)
			//HCR구분(HCR)
//			recPara.setField("HCR_GP", "H");
			//FROM 야드동(C연주 D동)
			recPara.setField("YD_STK_COL_GP", "DA");
			//저장품 테이블 조회
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 30);
			//대상 소재가 존재하면 리턴
			if(intRtnVal > 0){
				return 200;
				
			} else if(intRtnVal == 0){
				
				szMsg = "A후판 소재이송 상차 LOT 편성 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
				
			} else if(intRtnVal == -2){
				
				szMsg = "A후판 소재이송 상차 LOT 편성 데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
				
			} else {
				
				szMsg = "A후판 소재이송 상차 LOT 편성 데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return 0;
				
			}
			
		} catch(Exception e){
			szMsg = "A후판 소재이송 상차 LOT 편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return -100;
		}
	} //end of chkGetACsMtlFtmvCarLoadLotGp
	
	
	
	
	
	
	
	/**
	 * 오퍼레이션명 : 통합야드 이송 상차 LOT 편성 데이터 유무체크 및 데이터 반환
	 *  
	 * @param  String        szSchCd   스케줄코드
	 *         JDTORecordSet rsResult  결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public int chkGetSUnMtlFtmvCarLoadLotGp(JDTORecord recInPara, JDTORecordSet rsResult)throws JDTOException  {
		
		//저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		//리턴값(boolean)
		boolean blnRtnVal     = false;
		//리턴값(int)
		int intRtnVal         = 0;
		//메소드명
		String szMethodName   = "chkGetSUnMtlFtmvCarLoadLotGp";
		String szMsg          = null;
		String szTRN_EQP_CD		= null;
		
		//레코드 선언
		JDTORecord recPara        = null;	

		try {
			
			szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(recInPara, "TRN_EQP_CD");
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();
			
//			//테스트용
//			//재료품목(열연주편)
//			recPara.setField("YD_MTL_ITEM", "SH");
//			//목표행선구분(통합야드 -- > B열연 이송대기)
//			recPara.setField("YD_AIM_RT_GP", "E2");
//			//HCR구분(HCR)
//			recPara.setField("HCR_GP", "C");
//			//FROM 야드동(C연주 D동)
//			recPara.setField("YD_STK_COL_GP", "SA");
//			//저장품 테이블 조회
//			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 27);
//			//대상 소재가 존재하면 리턴
//			if(intRtnVal > 0) 
//				return 100;
//			
//			
//			//재료품목(열연주편)
//			recPara.setField("YD_MTL_ITEM", "SH");
//			//목표행선구분(통합야드 --> B열연 이송대기)
//			recPara.setField("YD_AIM_RT_GP", "E3");
//			//HCR구분(HCR)
//			recPara.setField("HCR_GP", "C");
//			//FROM 야드동(C연주 D동)
//			recPara.setField("YD_STK_COL_GP", "SA");
//			//저장품 테이블 조회
//			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 27);
//			//대상 소재가 존재하면 리턴
//			if(intRtnVal > 0) {
//				return 200;
//			}
//			
//			
//			//A후판 슬라브 CCR재 B동 [통합야드 -> A후판 SLAB야드] 이송재료
//			//재료품목(열연주편)
//			recPara.setField("YD_MTL_ITEM", "SP");
//			//목표행선구분(C연주 B열연 이송대기)
//			recPara.setField("YD_AIM_RT_GP", "E8");
//			//HCR구분(HCR)
//			recPara.setField("HCR_GP", "C");
//			//FROM 야드동(C연주 D동)
//			recPara.setField("YD_STK_COL_GP", "SB");
//			//저장품 테이블 조회
//			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 27);
//			//대상 소재가 존재하면 리턴
//			if(intRtnVal > 0) {
//				return 300;
//			}
//			
//			
//			//재료품목(열연주편)
//			recPara.setField("YD_MTL_ITEM", "SH");
//			//목표행선구분(통합야드 --> C열연 이송대기)
//			recPara.setField("YD_AIM_RT_GP", "E5");
//			//HCR구분(HCR)
//			recPara.setField("HCR_GP", "C");
//			//FROM 야드동(C연주 D동)
//			recPara.setField("YD_STK_COL_GP", "SC");
//			//저장품 테이블 조회
//			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 27);
//			//대상 소재가 존재하면 리턴
//			if(intRtnVal > 0) {
//				return 400;
//			}
//			
//			//재료품목(열연주편)
//			recPara.setField("YD_MTL_ITEM", "SH");
//			//목표행선구분(통합야드 --> C열연 이송대기)
//			recPara.setField("YD_AIM_RT_GP", "E4");
//			//HCR구분(HCR)
//			recPara.setField("HCR_GP", "C");
//			//FROM 야드동(C연주 D동)
//			recPara.setField("YD_STK_COL_GP", "SC");
//			//저장품 테이블 조회
//			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 27);
//			//대상 소재가 존재하면 리턴
//			if(intRtnVal > 0) {
//				return 500;
//			}
//			
//			//재료품목(열연주편)
//			recPara.setField("YD_MTL_ITEM", "BP");
//			//목표행선구분(통합야드 --> C열연 이송대기)
//			recPara.setField("YD_AIM_RT_GP", "E6");
//			//HCR구분(HCR)
//			recPara.setField("HCR_GP", "C");
//			//FROM 야드동(C연주 D동)
//			recPara.setField("YD_STK_COL_GP", "SB");
//			//저장품 테이블 조회
//			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 27);
//			//대상 소재가 존재하면 리턴
//			if(intRtnVal > 0) {
//				return 600;
//			}
//			
//			//재료품목(열연주편)
//			recPara.setField("YD_MTL_ITEM", "BP");
//			//목표행선구분(통합야드 --> C열연 이송대기)
//			recPara.setField("YD_AIM_RT_GP", "E7");
//			//HCR구분(HCR)
//			recPara.setField("HCR_GP", "C");
//			//FROM 야드동(C연주 D동)
//			recPara.setField("YD_STK_COL_GP", "SB");
//			//저장품 테이블 조회
//			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 27);
			
			//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++=	
			/*
			 * 가장 빠른 준비스케줄을 가져온다.
			 */
			
			//야드구분
//			recPara.setField("YD_GP", "S");
//			recPara.setField("YD_SCH_CD", "");
//			recPara.setField("YD_PREP_WK_ST", "L");
//			
//			//저장품 테이블 조회
//			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 141);
			//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++=	
			
			//현재 임시로 갯수로 대상재를 가져오는 부분으로 수정 - 위 소스로 원복 필요.
			recPara.setField("YD_GP", "S");
			recPara.setField("YD_SCH_CD", "");
			recPara.setField("YD_PREP_WK_ST", "L");
			if( szTRN_EQP_CD.substring(1, 3).equals("TR") ) {
				recPara.setField("SEARCH_TYPE", "1");
			}else if( szTRN_EQP_CD.substring(1, 3).equals("PT") ) {
				recPara.setField("SEARCH_TYPE", "2");
			}
			
			//저장품 테이블 조회
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 145);
			
			
			//대상 소재가 존재하면 리턴
			if(intRtnVal > 0) {
				return 700;
				
			} else if(intRtnVal == 0){
				
				szMsg = "통합야드 소재이송 상차 LOT 편성 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				//blnRtnVal = false;
				return intRtnVal;
			} else if(intRtnVal == -2){
				
				szMsg = "통합야드 소재이송 상차 LOT 편성 데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				//blnRtnVal = false;
				return intRtnVal;
			} else {
				
				szMsg = "통합야드 소재이송 상차 LOT 편성 데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				//blnRtnVal = false;
				return intRtnVal;
			}
			
		} catch(Exception e){
			szMsg = "통합야드 소재이송 상차 LOT 편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			//blnRtnVal = false;
			return -100;
		}
		
	} //end of chkGetSUnMtlFtmvCarLoadLotGp
	
	
	/**
	 * 오퍼레이션명 : 통합야드이송상차LOT조회 -크레인별
	 *  
	 * @param  String        szSchCd   스케줄코드
	 *         JDTORecordSet rsResult  결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public int chkGetSUnMtlFtmvCarLoadLotGpByCrn(JDTORecord recInPara, JDTORecordSet rsResult)throws JDTOException  {
		/*
		 * 업무기준 : 1. 크레인별 배차기준에서 우선순위가 빠르고 배차가능한 크레인정보를 조회
		 * 				1-1. 정보가 존재하지 않으면 공차배차 처리
		 * 				1-2. 크레인정보가 존재하면 크레인의 현재동에 이송가능한 준비스케줄이 존재하는 지 조회
		 * 					1-2-1. 준비스케줄이 존재하지 않으면 다음우선순위의 크레인정보 조회 후 [1-1]부터 다시 시작
		 * 					1-2-2. 준비스케줄이 존재하면 작업예약 등록 가능
		 */
		//저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		YdEqpDao ydEqpDao = new YdEqpDao();
		//리턴값(boolean)
		boolean blnRtnVal     = false;
		//리턴값(int)
		int intRtnVal         = 0;
		//메소드명
		String szMethodName   = "chkGetSUnMtlFtmvCarLoadLotGpByCrn";
		String szOperationName	= "통합야드이송상차LOT조회(크레인별)";
		String szMsg          = null;
		
		//레코드 선언
		JDTORecord recPara        = null;
		JDTORecordSet rsTemp		= null;
		String szYD_EQP_ID		= null;			//크레인설비ID
		String szYD_CURR_BAY_GP		= null;		//크레인의 현재동
		String szTRN_EQP_CD		= null;			//운송장비코드
		

		try {
			szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(recInPara, "TRN_EQP_CD");
			
			szMsg = "["+szOperationName+"] 메소드 시작 - 운송장비코드["+szTRN_EQP_CD+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			//레코드 생성
			rsTemp = JDTORecordFactory.getInstance().createRecordSet("");
			recPara  = JDTORecordFactory.getInstance().create();
			
			//야드구분
			recPara.setField("YD_GP", YdConstant.YD_GP_INTGR_YARD);
			recPara.setField("YD_BAY_GP", "");
			
			//설비 테이블 조회
			intRtnVal = ydEqpDao.getYdEqp(recPara, rsTemp, 10);
			
			for(int i = 1; i <= rsTemp.size(); i++ ) {
				rsTemp.absolute(i);
				recPara = rsTemp.getRecord();
				szYD_EQP_ID = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_ID");
				szYD_CURR_BAY_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_CURR_BAY_GP");
				
				szMsg = "["+szOperationName+"] 크레인설비ID["+szYD_EQP_ID+"], 현재동["+szYD_CURR_BAY_GP+"]으로 가능한 빠른 준비스케줄 조회 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				recPara  = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_GP", 			YdConstant.YD_GP_INTGR_YARD);
				recPara.setField("YD_SCH_CD", 		YdConstant.YD_GP_INTGR_YARD + szYD_CURR_BAY_GP + "PT");
				recPara.setField("YD_WRK_PLAN_CRN", szYD_EQP_ID);
				recPara.setField("YD_PREP_WK_ST", 	"L");
				//항목추가
				recPara.setField("CAR_GP", 	szTRN_EQP_CD.substring(1, 2));
				//저장품 테이블 조회
				//intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 143);
				intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 151);
				if( intRtnVal > 0 ) {
					szMsg = "["+szOperationName+"] 크레인설비ID["+szYD_EQP_ID+"], 현재동["+szYD_CURR_BAY_GP+"]으로 가능한 빠른 준비스케줄 존재합니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					rsResult.first();
					recPara = rsResult.getRecord();
					recPara.setField("YD_CRN_EQP_ID", szYD_EQP_ID);
					break;
				}else{
					szMsg = "["+szOperationName+"] 크레인설비ID["+szYD_EQP_ID+"], 현재동["+szYD_CURR_BAY_GP+"]으로 가능한 빠른 준비스케줄 존재하지 않습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
			}
			
			//대상 소재가 존재하면 리턴
			if(intRtnVal > 0) {
				//return 700;
				intRtnVal = 700;
			} else if(intRtnVal == 0){
				
				szMsg = "["+szOperationName+"] 통합야드 소재이송 상차 LOT 편성 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				//blnRtnVal = false;
				//return intRtnVal;
			} else if(intRtnVal == -2){
				
				szMsg = "["+szOperationName+"] 통합야드 소재이송 상차 LOT 편성 데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				//blnRtnVal = false;
				//return intRtnVal;
			} else {
				
				szMsg = "["+szOperationName+"] 통합야드 소재이송 상차 LOT 편성 데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				//blnRtnVal = false;
				//return intRtnVal;
			}
			szMsg = "["+szOperationName+"] 메소드 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		} catch(Exception e){
			szMsg = "["+szOperationName+"] 통합야드 소재이송 상차 LOT 편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			//blnRtnVal = false;
			return -100;
		}
		return intRtnVal;
	} //end of chkGetSUnMtlFtmvCarLoadLotGpByCrn
	
	
	
	
	/**
	 * 오퍼레이션명 : 차량사양 유무체크 및 조회결과 데이터 반환
	 *  
	 * @param  String        szEqpId         설비ID
	 *         String        szYD_CAR_USE_GP 차량사용구분
	 *         String        szTrnEqpCd      운송장비코드
	 *         JDTORecordSet rsResult        결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetCarSpec(String szEqpId, String szCarUseGp, String szTrnEqpCd, JDTORecordSet rsResult)throws JDTOException  {
		
		//차량사양 DAO
		YdCarSpecDao ydCarSpecDao     = new YdCarSpecDao();
		//리턴값(boolean)
		boolean blnRtnVal     = false;
		//리턴값(int)
		int intRtnVal         = 0;
		//메소드명
		String szMethodName   = "chkGetCarSpec";
		String szMsg          = null;
		
		//레코드 선언
		JDTORecord recPara        = null;	

		try {
			
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();
			
			//설비ID
			//recPara.setField("YD_EQP_ID",    szEqpId);
			//차량사용구분
			//recPara.setField("YD_CAR_USE_GP", szCarUseGp);
			//운송장비코드
			recPara.setField("TRN_EQP_CD", szTrnEqpCd);
//			//차량번호
//			recPara.setField("CAR_NO", "");
			//설비 테이블 조회
			intRtnVal = ydCarSpecDao.getYdCarspec(recPara, rsResult, 2);

			//리턴값 메세지처리
			if(intRtnVal > 1){
				
				szMsg = "설비ID("      + szEqpId    + ") " +
				        "운송장비코드(" + szTrnEqpCd + ")에 대한 설비 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == 1){

				blnRtnVal = true;
				
			} else if(intRtnVal == 0){
				
				szMsg = "설비ID("      + szEqpId    + ") " +
		                "운송장비코드(" + szTrnEqpCd + ")에 대한 설비 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == -2){
				
				szMsg = "설비ID("      + szEqpId    + ") " +
                        "운송장비코드(" + szTrnEqpCd + ")에 대한 차량사양 조회중 parameter error 발생.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "설비ID("      + szEqpId    + ") " +
                        "운송장비코드(" + szTrnEqpCd + ")에 대한 차량사양 조회중 오류 발생.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e){
			szMsg = "차량사양 유무체크 및 조회결과 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of chkGetCarSpec

	
	/**
	 * 오퍼레이션명 : 크레인작업가능사양과 재료사양을 체크
	 *  
	 * @param   String szStlNo 재료번호
	 *          String szEqpId 크레인 설비ID
	 * @return boolean true(크레인재료이송가능), false(크레인재료이송불가)
	 * @throws JDTOException
	 */
	public boolean chkCrnSpecMtlSpec(String szStlNo, String szEqpId)throws JDTOException  {

		//리턴값(boolean)
		boolean blnRtnVal         = false;
		//메세지
		String szMsg              = null;
		//메소드명
		String szMethodName       = "chkCrnSpecMtlSpec";
		//레코드 선언
		JDTORecord recPara        = null;
		//레코드셋 선언
		JDTORecordSet rsResult    = null;		

		try {
			//레코드셋 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//저장품 유무 체크
			blnRtnVal = this.chkGetStock(szStlNo, rsResult);
			if(!blnRtnVal) return blnRtnVal;
			
			//결과 레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			// 폭
			double lngMtlW   = ydDaoUtils.paraRecChkNullDouble(recPara, "YD_MYD_MTL_WTL_W");
			// 길이
			long lngMtlL     = ydDaoUtils.paraRecChkNullLong(recPara, "YD_MTL_L");
			// 중량			
			long lngMtlWt    = ydDaoUtils.paraRecChkNullLong(recPara, "YD_MTL_WT");

			//레코드셋 재생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//크레인사양 체크 및 조회
			blnRtnVal = this.chkGetCrnSpec(szEqpId, rsResult);
			if(!blnRtnVal) return blnRtnVal;
			
			//크레인사양 추출
			rsResult.first();
			recPara = rsResult.getRecord();
	
			// 크레인 작업 능력
			// 작업가능길이
			long lngAbleL  	= ydDaoUtils.paraRecChkNullLong(recPara, "YD_WRK_ABLE_L");
			// 작업가능폭
			double lngAbleW = ydDaoUtils.paraRecChkNullDouble(recPara, "YD_WRK_ABLE_W");
			// 작업가능중량
			long lngAbleWt 	= ydDaoUtils.paraRecChkNullLong(recPara, "YD_WRK_ABLE_WT");
			
			//크레인 작업가능 길이와 재료의 길이 비교
			if(lngAbleL < lngMtlL){
				szMsg = "크레인 작업가능 길이(" + lngAbleL + ") 보다 재료의 길이(" + lngMtlL + ")가 더 큽니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return blnRtnVal = false;
			}
	
			//크레인 작업가능 폭과 재료의 폭 비교
			if(lngAbleW < lngMtlW){
				szMsg = "크레인 작업가능 폭(" + lngAbleW + ")보다 재료의 폭(" + lngMtlW + ")이 더 큽니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return blnRtnVal = false;
			}
			
			//크레인 작업가능 중량과 재료의 중량 비교
			if(lngAbleWt < lngMtlWt){
				szMsg = "크레인 작업가능 중량(" + lngAbleWt + ")보다 재료의 중량(" + lngMtlWt + ")이 더 큽니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return blnRtnVal = false;
			}
		
		} catch(Exception e){
			szMsg = "크레인작업가능사양과 재료사양을 체크 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return blnRtnVal = false;
		}
		return blnRtnVal = true;
	} //end of chkCrnSpecMtlSpec
	
	
	/**
	 * 오퍼레이션명 : 재료사양을 체크(재료폭)
	 *  
	 * @param   String szStlNo 재료번호
	 *          String szEqpId 크레인 설비ID
	 * @return int 작업가능매수
	 * @throws JDTOException
	 */
	public int chkWrkMtl_W(String[] szStlNo, String szEqpId, int intMtlCnt)throws JDTOException  {

		//리턴값(boolean)
		int intRtnVal         = 0;
		boolean blnRtnVal     = true;
		//메세지
		String szMsg              = null;
		//메소드명
		String szMethodName       = "chkWrkMtl_W";
		//레코드 선언
		JDTORecord recPara        = null;
		//레코드셋 선언
		JDTORecordSet rsResult    = null;		
		
		int intStlCnt   = 0;
		// 재료폭
		double dblYD_MTL_W;
		// 집게허용오차
		double intCrnTongWTol = 0;
		// 최대 폭			
		double lngMaxWidth  = 0;
		
		// 재료중량
		double dblYD_MTL_WT;
		// 크레인작업가능중량
		double intCrnTongWTTol = 0;
		// 중량합계			
		double lngMaxWt  = 0;
		
		// 재료두께
		double dblYD_MTL_T;
		// 크레인작업가능두께
		double intCrnTongTTol = 0;
		// 두께합계			
		double lngMaxT  = 0;
		
		int Loop_i = 0;

		try {
			intStlCnt = intMtlCnt;
			
			for(int i=1; i<=intStlCnt; i++) {
				
				if (szStlNo[i] != null) {
					szMsg = "순번[" + i + "] 재료번호[" + szStlNo[i] + "] 입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				} else {
					intStlCnt = i;
					break;
				}
			}
			
			//레코드셋 재생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//크레인사양 체크 및 조회
			blnRtnVal = this.chkGetCrnSpec(szEqpId, rsResult);
			if(!blnRtnVal) return 0;
			
			//크레인사양 추출
			rsResult.first();
			recPara = rsResult.getRecord();
	
			// 크레인 작업 능력
			// 작업가능길이
			// 크레인 집게허용 오차
			intCrnTongWTol  = ydDaoUtils.paraRecChkNullDouble(recPara,  "YD_CRN_TONG_W_TOL");
			intCrnTongWTTol = ydDaoUtils.paraRecChkNullDouble(recPara,  "YD_WRK_ABLE_WT");
			intCrnTongTTol  = ydDaoUtils.paraRecChkNullDouble(recPara,  "YD_CRN_TONG_H");
			
			for(Loop_i = 1; Loop_i <= intStlCnt; Loop_i++){
				
				if (szStlNo[Loop_i] != null) {
				
					rsResult = JDTORecordFactory.getInstance().createRecordSet("");
					blnRtnVal = this.chkGetStock(szStlNo[Loop_i], rsResult);
					if(!blnRtnVal) return intRtnVal;
				
					//결과 레코드 추출
					rsResult.first();
					recPara = rsResult.getRecord();
					// 폭
					dblYD_MTL_W   = ydDaoUtils.paraRecChkNullLong(recPara, "YD_MTL_W");
					
					if(lngMaxWidth > dblYD_MTL_W + intCrnTongWTol) {
						
						szMsg = "lngMaxWidt : " + lngMaxWidth + " > lngCurrWidth : " + dblYD_MTL_W + " intCrnTongWTol : " + intCrnTongWTol;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						return intRtnVal = Loop_i-1;
					}
					
					if (dblYD_MTL_W > lngMaxWidth) lngMaxWidth = dblYD_MTL_W;
					
					// 중량 
					dblYD_MTL_WT  = ydDaoUtils.paraRecChkNullLong(recPara, "YD_MTL_WT");
					
					lngMaxWt = lngMaxWt + dblYD_MTL_WT;
					
					szMsg = "lngMaxWt="+lngMaxWt+"/intCrnTongWTTol="+intCrnTongWTTol+"/dblYD_MTL_WT="+dblYD_MTL_WT;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					if(lngMaxWt > intCrnTongWTTol){
						szMsg = "작업재료들이 크레인 작업가능 중량을 초과함.여기서 종료처리.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						return intRtnVal = Loop_i-1;
					}
					
					// 두께 
					dblYD_MTL_T  = ydDaoUtils.paraRecChkNullLong(recPara, "YD_MTL_T");
					
					lngMaxT = lngMaxT + dblYD_MTL_T;
					
					szMsg = "lngMaxT="+lngMaxT+"/intCrnTongTTol="+intCrnTongTTol+"/dblYD_MTL_T="+dblYD_MTL_T;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					if(lngMaxT > intCrnTongTTol){
						szMsg = "작업재료들이 크레인 작업가능 두께를 초과함.여기서 종료처리.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						return intRtnVal = Loop_i-1;
					}
				}
			}
		} catch(Exception e){
			szMsg = "크레인작업가능사양과 재료사양을 체크 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return 0;
		}
		return intRtnVal = intStlCnt;
	} //end of chkCrnSpecMtlSpec
	
	
	/**
	 * 오퍼레이션명 : 크레인사양 유무체크 및 조회결과 데이터 반환
	 *  
	 * @param  String        szEqpId  설비ID
	 *         JDTORecordSet rsResult 결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetCrnSpec(String szEqpId, JDTORecordSet rsResult)throws JDTOException  {
		
		//크레인사양 DAO
		YdCrnSpecDao ydCrnSpecDao = new YdCrnSpecDao();
		//리턴값(boolean)
		boolean blnRtnVal     = false;
		//리턴값(int)
		int intRtnVal         = 0;
		//메소드명
		String szMethodName   = "chkGetCrnSpec";
		String szMsg          = null;
		
		//레코드 선언
		JDTORecord recPara        = null;	

		try {
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();
			
			//크레인 설비ID
			recPara.setField("YD_EQP_ID", szEqpId);
			
			//크레인사양 조회
			intRtnVal = ydCrnSpecDao.getYdCrnspec(recPara, rsResult, 0);

			//리턴값 메세지처리
			if(intRtnVal > 1){
				
				szMsg = "크레인설비ID(" + szEqpId + ")에 대한 크레인사양 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == 1){

				blnRtnVal = true;
				
			} else if(intRtnVal == 0){
				
				szMsg = "크레인설비ID(" + szEqpId + ")에 대한 크레인사양 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == -2){
				
				szMsg = "크레인설비ID(" + szEqpId + ")로 크레인사양 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "크레인설비ID(" + szEqpId + ")로 크레인사양 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e){
			szMsg = "크레인사양 유무체크 및 조회결과 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of chkGetCrnSpec
	
	
	/**
	 * 오퍼레이션명 : 저장품유무체크 및 조회결과 데이터 반환
	 *  
	 * @param  String        szStlNo  재료번호
	 *         JDTORecordSet rsResult 결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetStock(String szStlNo, JDTORecordSet rsResult)throws JDTOException  {
		
		//저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		//리턴값(boolean)
		boolean blnRtnVal     = false;
		//리턴값(int)
		int intRtnVal         = 0;
		//메소드명
		String szMethodName   = "chkGetStock";
		String szMsg          = null;
		
		//레코드 선언
		JDTORecord recPara        = null;	

		try {
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();
			
			//재료번호
			recPara.setField("STL_NO", szStlNo);
			
			//저장품 테이블 조회
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 0);
			
			//리턴값 메세지처리
			if(intRtnVal > 1){
				
				szMsg = "재료번호(" + szStlNo + ")에 대한 저장품 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == 1){
				szMsg = "재료번호(" + szStlNo + ")에 대한 저장품 데이터 조회 성공!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				blnRtnVal = true;
				
			} else if(intRtnVal == 0){
				
				szMsg = "재료번호(" + szStlNo + ")에 대한 저장품 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == -2){
				
				szMsg = "재료번호(" + szStlNo + ")로 저장품 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "재료번호(" + szStlNo + ")로 저장품 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e){
			szMsg = "저장품유무체크 및 조회결과 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of chkGetStock
	
	
	/**
	 * 오퍼레이션명 : 설비 유무체크 및 조회결과 데이터 반환
	 *  
	 * @param  String        szEqpId  설비ID
	 *         JDTORecordSet rsResult 결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetEqp(String szEqpId, JDTORecordSet rsResult)throws JDTOException  {
		
		//설비 DAO
		YdEqpDao ydEqpDao     = new YdEqpDao();
		//리턴값(boolean)
		boolean blnRtnVal     = false;
		//리턴값(int)
		int intRtnVal         = 0;
		//메소드명
		String szMethodName   = "chkGetEqp";
		String szMsg          = null;
		
		//레코드 선언
		JDTORecord recPara        = null;	

		try {
			
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();
			
			//설비ID
			recPara.setField("YD_EQP_ID", szEqpId);
			
			//설비 테이블 조회
			intRtnVal = ydEqpDao.getYdEqp(recPara, rsResult, 0);

			//리턴값 메세지처리
			if(intRtnVal > 1){
				
				szMsg = "설비ID(" + szEqpId + ")에 대한 설비 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == 1){

				blnRtnVal = true;
				
			} else if(intRtnVal == 0){
				
				szMsg = "설비ID(" + szEqpId + ")에 대한 설비 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == -2){
				
				szMsg = "설비ID(" + szEqpId + ")로 설비 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "설비ID(" + szEqpId + ")로 설비 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e){
			szMsg = "설비 유무체크 및 조회결과 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of chkGetEqp
	
	
	
	/**
	 * 오퍼레이션명 : C연주 정정 보급Lot 편성 데이터 유무체크 및 데이터 반환
	 *  
	 * @param  String        szSchCd   스케줄코드
	 *         JDTORecordSet rsResult  결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetSHEARSupplyLotGp(String szSchCd, JDTORecordSet rsResult)throws JDTOException  {
		
		//저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		//리턴값(boolean)
		boolean blnRtnVal     = false;
		//리턴값(int)
		int intRtnVal         = 0;
		//메소드명
		String szMethodName   = "chkGetSHEARSupplyLotGp";
		String szMsg          = null;
		
		//레코드 선언
		JDTORecord recPara        = null;	

		try {
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();
			
			//재료품목
			//recPara.setField("YD_MTL_ITEM",  szSchCd.substring(2, 4));
			//야드구분
			recPara.setField("YD_GP",        szSchCd.substring(0, 1));
			//동구분
			recPara.setField("YD_BAY_GP",    szSchCd.substring(1, 2));
			
			//저장품 테이블 조회
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 15);
			
			//리턴값 메세지처리
			if(intRtnVal >= 1){

				blnRtnVal = true;
				
			} else if(intRtnVal == 0){
				
				szMsg = "스케줄코드(" + szSchCd + ")에 대한 C연주 정정 보급Lot 편성 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == -2){
				
				szMsg = "스케줄코드(" + szSchCd + ")에 대한 C연주 정정 보급Lot 편성 데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "스케줄코드(" + szSchCd + ")에 대한 C연주 정정 보급Lot 편성 데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e){
			szMsg = "C연주 정정 보급Lot 편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of chkGetSHEARSupplyLotGp
	
	

	
	
	/**
	 * 오퍼레이션명 : C연주 M-Scarfing 보급Lot 편성 데이터 유무체크 및 데이터 반환
	 *  
	 * @param  String        szSchCd   스케줄코드
	 *         JDTORecordSet rsResult  결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetMScarfingSupplyLotGp(String szSchCd, JDTORecordSet rsResult)throws JDTOException  {
		
		//저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		//리턴값(boolean)
		boolean blnRtnVal     = false;
		//리턴값(int)
		int intRtnVal         = 0;
		//메소드명
		String szMethodName   = "chkGetScarfingSupplyLotGp";
		String szMsg          = null;
		
		//레코드 선언
		JDTORecord recPara        = null;	

		try {
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();

			//야드구분
			recPara.setField("YD_GP",        szSchCd.substring(0, 1));
			//동구분
			recPara.setField("YD_BAY_GP",    szSchCd.substring(1, 2));
			
			//저장품 테이블 조회
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 15);
			
			//리턴값 메세지처리
			if(intRtnVal >= 1){

				blnRtnVal = true;
				
			} else if(intRtnVal == 0){
				
				szMsg = "스케줄코드(" + szSchCd + ")에 대한 C연주 M-Scarfing 보급Lot 편성 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == -2){
				
				szMsg = "스케줄코드(" + szSchCd + ")에 대한 C연주 M-Scarfing 보급Lot 편성 데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "스케줄코드(" + szSchCd + ")에 대한 C연주 M-Scarfing 보급Lot 편성 데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e){
			szMsg = "C연주 M-Scarfing 보급Lot 편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of chkGetScarfingSupplyLotGp
	
	
	/**
	 * 오퍼레이션명 : C연주 M-Scarfing 보급Lot 편성 데이터 유무체크 및 데이터 반환
	 *  
	 * @param  String        szSchCd   스케줄코드
	 *         JDTORecordSet rsResult  결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public int chkMScarfingSupplyLotGp(JDTORecord recInParam, JDTORecordSet rsResult)throws JDTOException  {
		
		//저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		//리턴값(boolean)
		boolean blnRtnVal     = false;
		//리턴값(int)
		int intRtnVal         = 0;
		//메소드명
		String szMethodName   = "chkMScarfingSupplyLotGp";
		String szMsg          = null;
		
		//설비ID
		String szYD_EQP_ID = 	null;
		//목표행선구분
		String szYD_AIM_RT_GP = null;
		//재료품목
		String szYD_MTL_ITEM   = null;
		//스케쥴코드
		String szYD_SCH_CD = null;
		
		//레코드 선언
		JDTORecord recPara        = null;	

		try {
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(recInParam, "YD_EQP_ID");
			if(szYD_EQP_ID.equals("")){
				szMsg = "[IN PARAMNETER 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return -10;
			}
			//목표행선구분
			szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(recInParam, "YD_AIM_RT_GP");
			if(szYD_AIM_RT_GP.equals("")){
				
				szMsg = "[IN PARAMNETER 이상] 목표행선구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return -10;
				
			}
			szYD_MTL_ITEM = ydDaoUtils.paraRecChkNull(recInParam, "YD_MTL_ITEM");
			if(szYD_AIM_RT_GP.equals("")){
				
				szMsg = "[IN PARAMNETER 이상] 재료품목이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return -10;
				
			}
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(recInParam, "YD_SCH_CD");
			if(szYD_SCH_CD.equals("")){
				
				szMsg = "[IN PARAMNETER 이상] 목표스케쥴코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return -10;
				
			}
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();

			//야드구분
			recPara.setField("YD_GP",        szYD_SCH_CD.substring(0, 1));
			//동구분
			recPara.setField("YD_BAY_GP",    szYD_SCH_CD.substring(1, 2));
			//설비ID
			recPara.setField("YD_STK_COL_GP",    szYD_EQP_ID);
			//재료품목
			recPara.setField("YD_MTL_ITEM",    szYD_MTL_ITEM);
			//목표행선구분
			recPara.setField("YD_AIM_RT_GP",    szYD_AIM_RT_GP);
			//목표행선구분
			recPara.setField("YD_AIM_SCH_CD",    "");
			
			//저장품 테이블 조회
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 39);
			
			//리턴값 메세지처리
			if(intRtnVal >= 1){

				//blnRtnVal = true;
				
			} else if(intRtnVal == 0){
				
				szMsg = "스케줄코드(" + szYD_SCH_CD + ")와 재료품목(" + szYD_MTL_ITEM + ")에 대한 C연주 M-Scarfing 보급Lot 편성 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				//blnRtnVal = false;
				
			} else if(intRtnVal == -2){
				
				szMsg = "스케줄코드(" + szYD_SCH_CD + ")에 대한 C연주 M-Scarfing 보급Lot 편성 데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				//blnRtnVal = false;
				
			} else {
				
				szMsg = "스케줄코드(" + szYD_SCH_CD + ")에 대한 C연주 M-Scarfing 보급Lot 편성 데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				//blnRtnVal = false;
				
			}
		} catch(Exception e){
			szMsg = "C연주 M-Scarfing 보급Lot 편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			//blnRtnVal = false;
			intRtnVal = -100;
		}
		//return blnRtnVal;
		return intRtnVal;
	} //end of chkMScarfingSupplyLotGp
	
	
	/**
	 * 오퍼레이션명 : C연주 C열연 보급Lot 편성 데이터 유무체크 및 데이터 반환
	 *  
	 * @param  String        szSchCd   스케줄코드
	 *         String        szAimRtGp 목표행선구분
	 *         JDTORecordSet rsResult  결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetCHRSupplyLotGp(String szSchCd, String szPTOP_PLNT_GP, JDTORecordSet rsResult)throws JDTOException  {
		
		//저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		//리턴값(boolean)
		boolean blnRtnVal     = false;
		//리턴값(int)
		int intRtnVal         = 0;
		//메소드명
		String szMethodName   = "chkGetCHRSupplyLotGp";
		String szMsg          = null;
		
		//레코드 선언
		JDTORecord recPara    = null;	

		try {
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();
			//재료품목
			recPara.setField("PTOP_PLNT_GP", szPTOP_PLNT_GP);
			//야드구분
			recPara.setField("YD_GP",        szSchCd.substring(0, 1));
			//동구분
			recPara.setField("YD_BAY_GP",    szSchCd.substring(1, 2));
			//방향구분
			recPara.setField("YD_SPAN_GBN",  szSchCd.substring(7));
			
			//저장품 테이블 조회
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 91);
			
			//리턴값 메세지처리
			if(intRtnVal >= 1){

				blnRtnVal = true;
				
			} else if(intRtnVal == 0){
				
				szMsg = "스케줄코드(" + szSchCd + ")에 대한 C연주 C열연 보급Lot 편성 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == -2){
				
				szMsg = "스케줄코드(" + szSchCd + ")에 대한 C연주 C열연 보급Lot 편성 데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "스케줄코드(" + szSchCd + ")에 대한 C연주 C열연 보급Lot 편성 데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e){
			szMsg = "C연주 C열연 보급Lot 편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of chkGetCHRSupplyLotGp
	
	/**
	 * 오퍼레이션명 : A후판가열로보급Lot편성 데이터 유무체크 및 데이터 반환
	 *  
	 * @param  String        szSchCd   스케줄코드
	 *         String        szAimRtGp 목표행선구분
	 *         JDTORecordSet rsResult  결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetCHRSupplyLotGpPlt(String szSchCd, String szPTOP_PLNT_GP, JDTORecordSet rsResult)throws JDTOException  {
		
		//저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		//리턴값(boolean)
		boolean blnRtnVal     = false;
		//리턴값(int)
		int intRtnVal         = 0;
		//메소드명
		String szMethodName   = "chkGetCHRSupplyLotGpPlt";
		String szMsg          = null;
		//레코드 선언
		JDTORecord recPara    = null;	

		try {
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();
			//재료품목
			recPara.setField("PTOP_PLNT_GP", szPTOP_PLNT_GP);
			//야드구분
			recPara.setField("YD_GP",        szSchCd.substring(0, 1));
			//동구분
			recPara.setField("YD_BAY_GP",    szSchCd.substring(1, 2));
			//스케쥴코드
			recPara.setField("YD_SCH_CD",    szSchCd);
			//스케쥴코드
			recPara.setField("YD_ROUTE_GP",  YdConstant.AR_WRK_WAIT_A_MILL);
			 
			//저장품 테이블 조회
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 92);
			
			//리턴값 메세지처리
			if(intRtnVal >= 1){

				blnRtnVal = true;
				
			} else if(intRtnVal == 0){
				
				szMsg = "스케줄코드(" + szSchCd + ")에 대한 A후판가열로보급Lot편성 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == -2){
				
				szMsg = "스케줄코드(" + szSchCd + ")에 대한 A후판가열로보급Lot편성 데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "스케줄코드(" + szSchCd + ")에 대한 A후판가열로보급Lot편성 데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e){
			szMsg = "A후판가열로보급Lot편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of chkGetCHRSupplyLotGpPlt
	
	
	
	
	
	
	
	
	
	

	/**
	 * 오퍼레이션명 : 작업예약재료 등록여부 체크
	 *  
	 * @param   String szStlNo 재료번호
	 * @return boolean true(작업예약재료등록가능), false(작업예약재료등록불가)
	 * @throws JDTOException
	 */
	public boolean chkYdWrkBookMtl(String szStlNo)throws JDTOException  {
		
		//작업예약재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao  = new YdWrkbookMtlDao();
		
		//메세지
		String szMsg              = null;
		//메소드명
		String szMethodName       = "chkYdWrkBookMtl";
		//리턴값(boolean)
		boolean blnRtnVal 		  = false;
		//리턴값(int)
		int intRtnVal = 0;
		//레코드 선언
		JDTORecord recPara     	  = null;
		//레코드셋 선언
		JDTORecordSet rsResult 	  = null;
			
		try {	
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();
			//레코드셋 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//재료번호
			recPara.setField("STL_NO", szStlNo);
			
			//재료번호로 작업예약재료 테이블을 읽어온다.
			intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recPara, rsResult, 2);
			
			//리턴값 메세지처리
			if(intRtnVal > 0){
				
				szMsg = "재료번호(" + szStlNo + ")에 대한 작업예약재료 데이터가 이미 등록되어 있습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == 0){
				
				szMsg = "재료번호(" + szStlNo + ")에 대한 작업예약재료 데이터가 등록 가능합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				blnRtnVal = true;
				
			} else if(intRtnVal == -2){
				
				szMsg = "재료번호(" + szStlNo + ")로 작업예약재료 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "재료번호(" + szStlNo + ")로 작업예약재료 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e){
			szMsg = "작업예약재료 등록여부 체크 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
		
	} //end of chkYdWrkBookMtl
	
	
	/**
	 * 오퍼레이션명 : 작업예약ID생성
	 *  
	 * @param  JDTORecordSet rsResult 결과 레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean getYdWbookId(JDTORecordSet rsResult)throws JDTOException  {
		
		//작업예약 DAO
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		
		//메세지
		String szMsg              = null;
		//메소드명
		String szMethodName       = "getYdWbookId";
		//리턴값(int)
		int intRtnVal             = 0;
		//리턴값(boolean)
		boolean blnRtnVal         = false;
		//레코드 선언
		JDTORecord recPara        = null;
		
		try {
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();
			
			//================================================
			//파라미터를 설정하지 않으면 JSPEED에서 에러발생. 추후 수정요
			recPara.setField("YD_WBOOK_ID", "1");
			//================================================
			
			//작업예약 테이블의 시퀀스를 이용해 작업예약ID를 구해온다.
			intRtnVal = ydWrkbookDao.getYdWrkbook(recPara, rsResult, 1);
			//리턴값 메세지처리
			if(intRtnVal > 1){
				
				szMsg = "작업예약ID 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == 1){

				blnRtnVal = true;
				
			} else if(intRtnVal == 0){
				
				szMsg = "작업예약ID를 구하지 못했습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == -2){
				
				szMsg = "작업예약ID 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "작업예약ID 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e){
			szMsg = "작업예약ID생성 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of getYdWbookId
	
	
	/**
	 * 오퍼레이션명 : 소재이송 상차 LOT 편성 데이터 유무체크 및 데이터 반환
	 *  
	 * @param  String        szSchCd   스케줄코드
	 *         JDTORecordSet rsResult  결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetMtlFtmvCarLoadLotGp(JDTORecord recInPara, JDTORecordSet rsResult)throws JDTOException  {
		
		//저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		//리턴값(boolean)
		boolean blnRtnVal     = false;
		//리턴값(int)
		int intRtnVal         = 0;
		//메소드명
		String szMethodName   = "chkGetMtlFtmvCarLoadLotGp";
		String szMsg          = null;
		
		//레코드 선언
		JDTORecord recPara        = null;	
		JDTORecordSet rsLotStock = null;

		try {
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();

			//재료품목
			recPara.setField("YD_MTL_ITEM",   ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_ITEM"));
			//목표행선구분
			recPara.setField("YD_AIM_RT_GP",  ydDaoUtils.paraRecChkNull(recInPara, "YD_AIM_RT_GP"));
			//목표야드구분
			recPara.setField("YD_AIM_YD_GP",  ydDaoUtils.paraRecChkNull(recInPara, "YD_AIM_YD_GP"));
			//목표동구분
			recPara.setField("YD_AIM_BAY_GP", ydDaoUtils.paraRecChkNull(recInPara, "YD_AIM_BAY_GP"));
			//FROM 야드동
			recPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(recInPara, "YD_CARLD_STOP_LOC").substring(0, 2));
			
			//저장품 테이블 조회
			rsLotStock = JDTORecordFactory.getInstance().createRecordSet("");
			intRtnVal = ydStockDao.getYdStock(recPara, rsLotStock, 17);
			
			//20090315 김진욱 로그 및 레코드셋 addAll 추가
			szMsg = "대차 상차 Lot편성 후 재료의 매수 : " + rsLotStock.size();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			rsResult.addAll(rsLotStock);
			
			
			//리턴값 메세지처리
			if(intRtnVal >= 1){

				blnRtnVal = true;
				
			} else if(intRtnVal == 0){
				
				szMsg = "C연주 소재이송 상차 LOT 편성 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if(intRtnVal == -2){
				
				szMsg = "C연주 소재이송 상차 LOT 편성 데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "C연주 소재이송 상차 LOT 편성 데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e){
			szMsg = "C연주 소재이송 상차 LOT 편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of chkGetMtlFtmvCarLoadLotGp
	
	
	/**
	 * 오퍼레이션명 : 후판제품출하상차LOT편성(YDYDJ284)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String procPlGdsDistCarLdLotComp(JDTORecord msgRecord) throws JDTOException  {

		// DAO 선언
		YdStockDao ydStockDao      = new YdStockDao();
		YdDelegate ydDelegate      = new YdDelegate();
		YdDaoUtils ydDaoUtils      = new YdDaoUtils();
		YdUtils ydutils            = new YdUtils();
		
		YdPlateCommDAO commDao = new YdPlateCommDAO(); //3기 후판제품공용dao
		
		// 레코드 선언
		JDTORecord recPara         = null;
		JDTORecord recGetVal       = null;
		JDTORecord RecOut          = null;

		// 레코드셋 선언
		JDTORecordSet rsResult     = null;
		
		// 메세지
		String szMsg               = "";
		
		// 메소드명
		String szMethodName    	   = "procPlGdsDistCarLdLotComp";
		String szOperationName	   = "후판제품출하상차LOT편성";
		// 설비ID
		String szYD_EQP_ID         = "";
		// 상차정지위치
		String szYD_CARLD_STOP_LOC = "";
		// 차량사용구분
		String szYD_CAR_USE_GP     = "";
		// 야드
		String szYdGp              = "";
		// 동구분
		String szBayRGp            = "";
		// 차량번호
		String szCAR_NO            = "";
		String szCAR_KIND 		   = "";
		// 카드번호
		String szCARD_NO           = "";
		// 재료품목
		String szYD_MTL_ITEM       = "";
		// 스케줄코드
		String szYD_SCH_CD         = "";
		// 운송장비코드
		String szTRN_EQP_CD        = "";
		// 운송지시일자
		String szTRANS_ORD_DATE    = "";
		// 운송지시순번
		String szTRANS_ORD_SEQNO   = "";
		// 동구분
		String szPrevBayGp         = "";
		String szBayGp             = "";
		// 개소코드
		String szWLOC_CD           = "";
		// 운송작업영공구분코드
		String szSP_TRUCK_LOADING_LOC_TP = "";
		// 현재일시
		String szDate              = "";
		// 포인트요구일시
		String szPNT_DMD_DT        = "";
		// 발지개소코드
		String szSPOS_WLOC_CD      = "";
		// 발지포인트코드
		String szSPOS_YD_PNT_CD    = "";
		// 리턴값(int)
		int nRtnVal                = 0;
		//연계모듈을 EJB Call or JMS Call 호출 유무 변수
		String szIS_EJB_CALL = null;
		//리턴값
		String szRtnMsg			= null;
		
		
		// TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		if(szRcvTcCode == null){
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_TC_ERROR;
		}
		
		// TC CODE DISPLAY
		if(bDebugFlag){
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		try {
			// 야드
			szYdGp = ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP");
			if(szYdGp.equals("")){
				szMsg = "[전문 이상] 야드구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}
			// PIDEV
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "", "APPPI0", szYdGp, "*");				

			// 상차정지위치
			szYD_CARLD_STOP_LOC = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CARLD_STOP_LOC");
			if(szYD_CARLD_STOP_LOC.equals("")){
				szMsg = "[전문 이상] 상차정지위치가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}

			// 차량사용구분
			szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_USE_GP");
			if(szYD_CAR_USE_GP.equals("") || !szYD_CAR_USE_GP.equals("G")){
				szMsg = "[전문 이상] 차량사용구분(" + szYD_CAR_USE_GP + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}
			
			// 차량번호
			szCAR_NO = ydDaoUtils.paraRecChkNull(msgRecord, "CAR_NO");
			if(szCAR_NO.equals("")){
				szMsg = "[전문 이상] 차량번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}

			//PIDEV
//			if("Y".equals(sApplyYnPI)) {
				
				szCAR_KIND = ydDaoUtils.paraRecChkNull(msgRecord, "CAR_KIND");
				
//			} else {
//
//				// 카드번호
//				szCARD_NO = ydDaoUtils.paraRecChkNull(msgRecord, "CARD_NO");
//				
//				if(szCARD_NO.equals("")){
//					szMsg = "[전문 이상] 카드번호가 없습니다.";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//					return YdConstant.RETN_CD_NO_PARAM;
//				}				
//				
//			}
			
			// 운송지시 일자
			szTRANS_ORD_DATE = ydDaoUtils.paraRecChkNull(msgRecord, "TRANS_ORD_DATE");
			if(szTRANS_ORD_DATE.equals("")){
				szMsg = "[전문 이상] 운송지시 일자가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}
			
			// 운송지시 순번
			szTRANS_ORD_SEQNO = ydDaoUtils.paraRecChkNull(msgRecord, "TRANS_ORD_SEQNO");
			if(szTRANS_ORD_SEQNO.equals("")){
				szMsg = "[전문 이상] 운송지시 순번이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}
			

			// 동
			szBayRGp = ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP");
			if(szBayRGp.equals("")){
				szMsg = "[전문 이상] 동구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}
			
			// 발지개소코드 
			szSPOS_WLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_WLOC_CD");
			if(szSPOS_WLOC_CD.equals("")){
				szMsg = "[전문 이상] 발지개소코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}
			
			// 발지포인트 코드 
			szSPOS_YD_PNT_CD = ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_YD_PNT_CD");
			if(szSPOS_YD_PNT_CD.equals("")){
				szMsg = "[전문 이상] 발지개소코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}		
			
			//연계모듈을 EJB Call[Y값으로 설정] or JMS Call[값이 없거나 N값으로 설정] 호출 유무 변수  - 지금은 화면으로부터 전송
			szIS_EJB_CALL = ydDaoUtils.paraRecChkNull(msgRecord, "IS_EJB_CALL");

			szMsg="[후판제품출하상차LOT편성]szIS_EJB_CALL = " + szIS_EJB_CALL;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			String szSchCdPart = "";
			
			//통합 스케줄 방식
			if(szYD_CARLD_STOP_LOC.substring(4, 5).equals("1")) {
				szSchCdPart = "10UM"; //1통로 작업
			} else if(szYD_CARLD_STOP_LOC.substring(4, 5).equals("2")) {
				szSchCdPart = "20UM"; //2통로 작업
			} else if(szYD_CARLD_STOP_LOC.substring(4, 5).equals("3")) {
				szSchCdPart = "30UM"; //3통로 작업
			} else if(szYD_CARLD_STOP_LOC.substring(4, 5).equals("4")) {
				szSchCdPart = "40UM"; //4통로 작업
			} else if(szYD_CARLD_STOP_LOC.substring(4, 5).equals("5")) {
				szSchCdPart = "50UM"; //5통로 작업	
			} else if ("DKY23".equals(szSPOS_WLOC_CD)){
				szSchCdPart = "01UM";//후판정정야드 상차 PFPT01UM
			
			}else {
				szMsg = "["+szOperationName+"] szYD_CARLD_STOP_LOC ["+szYD_CARLD_STOP_LOC+"]을 가지고 스케줄코드 편집시 ERROR 발생!!!!";
				ydUtils.putLog(IssueWrkDmdSeEJBBean.class.getName(), szMethodName, szMsg, YdConstant.ERROR);					
				return YdConstant.RETN_CD_FAILURE;
			}
			

			if ("DKY23".equals(szSPOS_WLOC_CD)){
				szYD_SCH_CD = "PFPT01UM";//후판정정야드 상차 PFPT01UM
			
			}else if(szCAR_KIND.startsWith("P")) {
				szYD_SCH_CD  = szYdGp + szBayRGp + "PT" + szSchCdPart;   
			} else {
				szYD_SCH_CD  = szYdGp + szBayRGp + "TR" + szSchCdPart;    
			}
				
			 
			// RecordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara  = JDTORecordFactory.getInstance().create();
			
			// 저장품과 적치단 테이블 조회 
			//-------------------------------------------------------------
			szMsg = "["+szOperationName+"] 운송지시일자["+szTRANS_ORD_DATE+"], 운송지시순번["+szTRANS_ORD_SEQNO+"], 카드번호["+szCARD_NO+"]에 해당하는 대상재를 조회 시작";
			ydUtils.putLog(IssueWrkDmdSeEJBBean.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
			
			recPara.setField("YD_STK_COL_GP", 		szYdGp);
			recPara.setField("CAR_NO", 				szCAR_NO);
			
			// PIDEV			
//			if("N".equals(sApplyYnPI)) {
//				recPara.setField("CARD_NO", 			szCARD_NO);	
//			}
			
			recPara.setField("TRANS_ORD_DATE",  	szTRANS_ORD_DATE);
			recPara.setField("TRANS_ORD_SEQNO", 	szTRANS_ORD_SEQNO);
			recPara.setField("YD_CARLD_STOP_LOC", 	szYD_CARLD_STOP_LOC);
			
			// 전사물류개선 2021. 1.6
			// 복수동우선순위적용여부에 따라서 권상모음순서를 변경처리한다.
			//  - 권상모음순서는
			//    : 가변차량 - 폭이 작은 재료부터 우선순위부여
			//    : 그외차량 - 폭이 제일 큰것 부터 우선순위부여
			//
			
			String sQueryId = "";
			String sCAR_FRM_YN = ""; // 형상유무에 상관없이 크레인스케쥴 호출여부
			if(szYD_CARLD_STOP_LOC.substring(4, 5).equals("1") || szYD_CARLD_STOP_LOC.substring(4, 5).equals("3")) {
				//통로 구분이 1통로 , 3통로 인경우 RT 에서 통로 쪽으로 작업이 진행 되도록 ORDER BY YD_STK_COL_GP DESC 로 한다.
				//com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0038 과 동일하나 ORDER BY 구문만 다르다.
				sQueryId = "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0100_PIDEV"; //--2014.04.18 추가 (통합)		
    			
			} else {
				//통로 구분이 2통로 , 4통로 인경우 RT 에서 통로 쪽으로 작업이 진행 되도록 ORDER BY YD_STK_COL_GP ASC 로 한다.
				sQueryId = "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0038_PIDEV"; //--2014.04.18 수정 (통합)		
			}
			
			// 복수동일상차일 경우
			if(PlateGdsYdUtil.isPlateNewMoudleApply("DM")){ 
				//PIDEV_S :병행가동용:PI_YD
				recPara.setField("PI_YD",    	szYdGp);				
				if(commDao.select(recPara, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.isCMBN_CARLD_YN_PIDEV") > 0){
					szMsg = "["+szOperationName+"] 복수상차입니다. 출하신규로직 확인 : ";
					ydUtils.putLog(IssueWrkDmdSeEJBBean.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
					if(PlateGdsYdUtil.isApplyYn("복수상차우선순위적용여부")){
						
						sCAR_FRM_YN = ydDaoUtils.paraRecChkNull(msgRecord, "CAR_FRM_YN");
						szMsg = "["+szOperationName+"] 차량형상유무 SKIP : " + sCAR_FRM_YN;
						ydUtils.putLog(IssueWrkDmdSeEJBBean.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
						
						// 1. 동 통로는 제품의 길이그룹 차종에 따라서 결정된다.(가변: 짧은, 그외: 긴것)
						// 2. 1)번 사항에 결정된 상차포인트와 길이그룹먼저 차에 상차한다.
						// 3. 마지막 상차포인트일 경우 모두 차량에 상차한다.
						// 4. 해당 상차포인트에 마지막 상차일경우라도 다음 상차지가 결정되어 있다면 순위에 따라서 재입동처리한다.
						JDTORecordSet rsNextCarPoint = JDTORecordFactory.getInstance().createRecordSet("ydPlate");
						JDTORecord params  = JDTORecordFactory.getInstance().create();
						params.setField("CARD_NO", 			szCARD_NO);
						params.setField("TRANS_ORD_DATE",  	szTRANS_ORD_DATE);
						params.setField("TRANS_ORD_SEQNO", 	szTRANS_ORD_SEQNO);
						params.setField("YD_CARLD_STOP_LOC", szYD_CARLD_STOP_LOC);
						
						// 다음 상차지가 존재할경우
						if(commDao.select(params, rsNextCarPoint, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.getSelectDongByCMBN_CARLD_Point")>0){
							
							szMsg = "["+szOperationName+"] 상차포인트["+ szYD_CARLD_STOP_LOC +"] 검색완료 : 진행해야할 건 :: " + rsNextCarPoint.size();
							ydUtils.putLog(IssueWrkDmdSeEJBBean.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
							
							// 쿼리의 파라메터를 재정의한다.
							recPara.setField("YD_STK_COL_GP", 		szYdGp);
							recPara.setField("CARD_NO", 			szCARD_NO);
							recPara.setField("TRANS_ORD_DATE",  	szTRANS_ORD_DATE);
							recPara.setField("TRANS_ORD_SEQNO", 	szTRANS_ORD_SEQNO);
							recPara.setField("YD_CARLD_STOP_LOC", 	szYD_CARLD_STOP_LOC);
							
							int nTotSangChaCnt = rsNextCarPoint.size();
							// 마지막 상차일경우
							if(nTotSangChaCnt==1){
								
								szMsg = "["+szOperationName+"] 복수동 상차 마지막 :: " + szYD_CARLD_STOP_LOC;
								ydUtils.putLog(IssueWrkDmdSeEJBBean.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
								
								// 재료의 길이그룹이 동일한 재료만 상차하기 위함
								recPara.setField("YD_MTL_L_GP", "ALL");
								// 현재 고객우선순위
								recPara.setField("CUST_SEQ", "LAST");
								
								// 다음 상차지가 있을 경우
							}else{
								
								szMsg = "["+szOperationName+"] 복수동 상차 계속 :: 다음상차지:: " + rsNextCarPoint.getRecord(1).getFieldString("YD_STK_COL_GP");
								ydUtils.putLog(IssueWrkDmdSeEJBBean.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
								
								// 나머지 상차지를 체크한다.
								int nSameCarPointCnt = rsNextCarPoint.size();
								for(int i=1; i < nTotSangChaCnt; i++){
									
									szMsg = "["+szOperationName+"] 복수동 다음상차지 비교 :: " + szYD_CARLD_STOP_LOC + " Next :: " +  rsNextCarPoint.getRecord(i).getFieldString("YD_STK_COL_GP");
									ydUtils.putLog(IssueWrkDmdSeEJBBean.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
									
									if(!szYD_CARLD_STOP_LOC.equals(rsNextCarPoint.getRecord(i).getFieldString("YD_STK_COL_GP"))){
										nSameCarPointCnt--;
									}
								}
								
								// 남은 갯수 + 동일상차지 갯수가 동일하면 마지막 상차로 간주한다.
								if(nSameCarPointCnt==nTotSangChaCnt){
									
									szMsg = "["+szOperationName+"] 복수동 마지막동일 그룹일 경우 재료 모두 작업처리";
									ydUtils.putLog(IssueWrkDmdSeEJBBean.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
									// 재료의 길이그룹이 동일한 재료만 상차하기 위함
									recPara.setField("YD_MTL_L_GP", "ALL");
									// 현재 고객우선순위
									recPara.setField("CUST_SEQ", "LAST");
								}
								else{
									
									//다음상차지도 동일 포인트일 경우
									if(nTotSangChaCnt>1 
											&& szYD_CARLD_STOP_LOC.equals(rsNextCarPoint.getRecord(1).getFieldString("YD_STK_COL_GP"))){
										
										// 재료의 길이그룹이 동일한 재료만 상차하기 위함
										recPara.setField("YD_MTL_L_GP", rsNextCarPoint.getRecord(0).getFieldString("YD_MTL_L_GP"));
										recPara.setField("YD_MTL_L_GP2", rsNextCarPoint.getRecord(1).getFieldString("YD_MTL_L_GP"));
										
										// 현재 고객우선순위
										recPara.setField("CUST_SEQ", rsNextCarPoint.getRecord(0).getFieldString("CUST_SEQ"));			
										

										szMsg = "["+szOperationName+"] 복수동 다음상차지도 동일하므로 동일작업으로 간주한다.";
										ydUtils.putLog(IssueWrkDmdSeEJBBean.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
									}
									else{
										// 재료의 길이그룹이 동일한 재료만 상차하기 위함
										recPara.setField("YD_MTL_L_GP", rsNextCarPoint.getRecord(0).getFieldString("YD_MTL_L_GP"));
										// 현재 고객우선순위
										recPara.setField("CUST_SEQ", rsNextCarPoint.getRecord(0).getFieldString("CUST_SEQ"));

										szMsg = "["+szOperationName+"] 복수동 일반작업";
										ydUtils.putLog(IssueWrkDmdSeEJBBean.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
									}
								}
								
							}
							
							sQueryId = "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.getPlGdsDistCarLdWrkReq";
						}
					}
				}
			}
//			주석처리 2021. 1. 6
//			if(szYD_CARLD_STOP_LOC.substring(4, 5).equals("1") || szYD_CARLD_STOP_LOC.substring(4, 5).equals("3")) {
//				//통로 구분이 1통로 , 3통로 인경우 RT 에서 통로 쪽으로 작업이 진행 되도록 ORDER BY YD_STK_COL_GP DESC 로 한다.
//				//com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0038 과 동일하나 ORDER BY 구문만 다르다.
//    			nRtnVal = commDao.select(recPara, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0100_PIDEV"); //--2014.04.18 추가 (통합)		
//    			
//			} else {
//				//통로 구분이 2통로 , 4통로 인경우 RT 에서 통로 쪽으로 작업이 진행 되도록 ORDER BY YD_STK_COL_GP ASC 로 한다.
//				nRtnVal = commDao.select(recPara, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0038"); //--2014.04.18 수정 (통합)		
//			} 
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			nRtnVal = commDao.select(recPara, rsResult, sQueryId); //--2014.04.18 수정 (통합)
			if(nRtnVal <= 0){
				szMsg = "["+szOperationName+"] 운송지시일자["+szTRANS_ORD_DATE+"], 운송지시순번["+szTRANS_ORD_SEQNO+"], 카드번호["+szCARD_NO+"]에 해당하는 대상재가 존재하지 않음 : " + nRtnVal;
				ydUtils.putLog(IssueWrkDmdSeEJBBean.class.getName(), szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}

    		
			szMsg = "["+szOperationName+"] 운송지시일자["+szTRANS_ORD_DATE+"], 운송지시순번["+szTRANS_ORD_SEQNO+"], 카드번호["+szCARD_NO+"]에 해당하는 대상재 존재 - 건수 : " + nRtnVal;
			ydUtils.putLog(IssueWrkDmdSeEJBBean.class.getName(), szMethodName, szMsg, YdConstant.DEBUG);
			
			RecOut = JDTORecordFactory.getInstance().create();
			rsResult.first();
			recGetVal = rsResult.getRecord();	

			// JMS TC CODE
			RecOut.setField("JMS_TC_CD"         , "YDYDJ285");
			// 전문 발생 일시
			szDate = ydUtils.getCurDate("yyyy/MM/dd HH:mm:ss");
			// 발생 일시
			RecOut.setField("JMS_TC_CREATE_DDTT", szDate);
			// 스케줄코드
			RecOut.setField("YD_SCH_CD"         , szYD_SCH_CD);
			// 적치열구분 [설비ID]
			RecOut.setField("YD_EQP_ID"	   		, YdConstant.YD_DM_CAR_EQP_ID);
			// 차량사용구분
			RecOut.setField("YD_CAR_USE_GP"     , szYD_CAR_USE_GP);
			// 발지개소코드
			RecOut.setField("SPOS_WLOC_CD"	    , szSPOS_WLOC_CD);
			// 발지포인트코드
			RecOut.setField("SPOS_YD_PNT_CD"	, szSPOS_YD_PNT_CD);
			// 차량번호 
			RecOut.setField("CAR_NO"			, szCAR_NO);
			// 카드번호
			RecOut.setField("CARD_NO"			, szCARD_NO);
			//차량상차정지위치
			RecOut.setField("YD_CARLD_STOP_LOC"	, szYD_CARLD_STOP_LOC);
			
			long lnStlWt 	= 0;
			int intSh 		= 0;
			for(int nIdx=0; nIdx<nRtnVal; nIdx++){
				
				rsResult.absolute(nIdx+1);
				recGetVal = rsResult.getRecord();	
				lnStlWt += ydDaoUtils.paraRecChkNullLong(recGetVal, "YD_MTL_WT");
				
				// 재료번호
				RecOut.setField("STL_NO" + (nIdx + 1), ydDaoUtils.paraRecChkNull(recGetVal, "STL_NO"));
				RecOut.setField("YD_STK_COL_GP" + (nIdx + 1), ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_COL_GP"));
				RecOut.setField("YD_STK_BED_NO" + (nIdx + 1), ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_BED_NO"));
				RecOut.setField("YD_STK_LYR_NO" + (nIdx + 1), ydDaoUtils.paraRecChkNull(recGetVal, "YD_STK_LYR_NO"));
				// 권상모음순서
				RecOut.setField("YD_UP_COLL_SEQ" + (nIdx + 1), "" + (nIdx + 1));
				intSh++;
			}
			
			// 재료매수
			RecOut.setField("YD_CARLD_SH"	    , "" + (intSh));
			
			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			 * EJB Call or JMS Call 유무를 판단하여 호출하는 기능
			 * 수정자 : 임춘수
			 * 일자 : 2009.07.13
			 ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			if( szIS_EJB_CALL.equals("Y")){
				//EJB Call ==> 메소드 콜
				szMsg = "후판제품 출하상차LOT편성 후 차량상차 작업요구 송신 - 메소드[procPlGdsDistCarLdWrkReq] 콜 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
				szRtnMsg = procPlGdsDistCarLdWrkReq(RecOut);
					
				szMsg = "후판제품 출하상차LOT편성 후 차량상차 작업요구 송신 - 메소드[procPlGdsDistCarLdWrkReq] 콜 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}else{
				//전문 송신
				ydDelegate.sendMsg(RecOut);
				szMsg = "후판제품 출하상차LOT편성 후 차량상차 작업요구 송신[procPlGdsDistCarLdWrkReq] - 전문 송신 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			
			szMsg = "후판제품 출하상차LOT편성 후 차량상차 작업요구 송신 완료! [" +  szBayRGp + "]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		} catch(Exception e){
			szMsg = "출하상차LOT편성  Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;
		}
		return szRtnMsg;
	} //end of procPlGdsDistCarLdLotComp
	
	/**
	 * 오퍼레이션명 : 후판제품출하차량상차작업요구(YDYDJ285)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String procPlGdsDistCarLdWrkReq(JDTORecord msgRecord)throws JDTOException  {
		
		YdWrkbookDao ydWrkbookDao        = new YdWrkbookDao();
		YdWrkbookMtlDao ydWrkbookMtlDao  = new YdWrkbookMtlDao();
		YdCarSchDao ydCarSchDao          = new YdCarSchDao();
		YdStockDao ydStockDao 			 = new YdStockDao();
		YdStkColDao ydStkColDao 		 = new YdStkColDao();
		YdDaoUtils ydDaoUtils            = new YdDaoUtils();
		YdPlateCommDAO	commDao 		 = new YdPlateCommDAO();
		
		// 레코드 선언
		JDTORecord recPara               = null;
		JDTORecord recStk 				 = null;
		JDTORecord recStkPara            = null;
		JDTORecord recCarSchPara         = null;
		JDTORecord recInTemp         	 = null;
		JDTORecord recInPara			 = null;
		
		// 레코드셋 선언
		JDTORecordSet rsResult           = null;
		JDTORecordSet rsCarSchResult     = null;
		JDTORecordSet rsTemp           	 = null;
		
		JDTORecordSet rsStkCol     		 = null;
		
		// 메세지
		String szMsg                     = "";
		
		// METHOD명
		String szMethodName              = "procPlGdsDistCarLdWrkReq";
		String szOperationName			 = "후판제품출하차량상차작업요구";
		// 사용자
		String szUser                    = "SYSTEM";
		// 스케줄코드
		String szYD_SCH_CD               = "";
		String  szCURR_YD_SCH_CD		 = null;
		// 적치열구분
		String szYD_STK_COL_GP           = "";
		String szPREV_YD_STK_COL_GP		 = null;
		// 적치BED번호
		String szYD_STK_BED_NO           = null;
		String szYD_STK_LYR_NO 			 = null;
		// 목표행선구분
		//String szYD_AIM_RT_GP          = "";
		// 차량사용구분
		String szYD_CAR_USE_GP           = "";
		// 운송장비코드
		String szTRN_EQP_CD              = "";
		String szYD_EQP_ID               = null;
		// 재료매수(int)
		int intMtlCnt                    = 0;
		// 재료번호
		String szSTL_NO					 = null;
		// 권상모음순서
		String szYD_UP_COLL_SEQ          = null;
		// 야드작업우선순위
		String szYD_SCH_PRIOR            = "";
		// 야드구분
		String szYD_GP                   = null;
		String szYD_BAY_GP               = null;
		// 선택크레인
		String szCrn                     = "";
		String szCurrCrn                 = "";
		// 작업예약ID
		String szYD_WBOOK_ID             = "";
		String szCURR_YD_WBOOK_ID        = "";
		// 개소코드
		String szWLOC_CD                 = "";
		// 발지개소코드
		String szSPOS_WLOC_CD            = "";
		// 발지포인트코드
		String szSPOS_YD_PNT_CD          = "";
		// 야드목표야드구분
		String szYD_AIM_YD_GP            = "";
		// 야드목표동구분
		String szYD_AIM_BAY_GP           = "";
		//차량번호
		String szCAR_NO                  = "";
		//카드번호
		String szCARD_NO                 = "";
		// 상차정지위치
		String szYD_CARLD_STOP_LOC 		 = "";
		//차량스케줄ID
		String szYD_CAR_SCH_ID 			 = "";
		// 리턴값(int)
		int intRtnVal                    = 0;
		// 리턴값(boolean)
		boolean blnRtnVal                = false;
		String[] szTC_CODE				 = null;
		
		Vector vGroup = new Vector();

		// PIDEV
//		String sApplyYnPI = ydPICommDAO.ApplyYnPI("", szMethodName, "APPPI0", "T", "*");
		
		if("PIDEV".equals("PIDEV")) {
			String Msg = this.procPlGdsDistCarLdWrkReq_PIDEV(msgRecord);
			return Msg;
		}	
		
		// TC CODE 추출
		String szRcvTcCode               = ydUtils.getTcCode(msgRecord);
		if(szRcvTcCode == null || szRcvTcCode.equals("")){
			szMsg = "["+szOperationName+"] TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_TC_ERROR;
		}
		
		// TC CODE DISPLAY
		if(bDebugFlag){
			szMsg = "["+szOperationName+"] 전문수신 : TCCODE=" + szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		try {
			// 스케줄코드
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			if(szYD_SCH_CD.equals("")){
				szMsg = "["+szOperationName+"] [전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}
			
			// 재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_CARLD_SH");
			if(intMtlCnt == 0){
				szMsg = "["+szOperationName+"] [전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}

			// 설비ID (적치열구분)
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if(szYD_EQP_ID.equals("")){
				szMsg = "["+szOperationName+"] [전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}			

			// 차량사용구분
			szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_USE_GP");
			if(szYD_CAR_USE_GP.equals("")){
				szMsg = "["+szOperationName+"] [전문 이상] 차량사용구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}
			
			szCAR_NO = ydDaoUtils.paraRecChkNull(msgRecord, "CAR_NO");
			if(szCAR_NO.equals("")){
				szMsg = "["+szOperationName+"] [전문 이상] 차량번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}
			
			// PIDEV
//			if("N".equals(sApplyYnPI)) {			
//
//				szCARD_NO = ydDaoUtils.paraRecChkNull(msgRecord, "CARD_NO");				
//				
//				if(szCARD_NO.equals("")){
//					szMsg = "["+szOperationName+"] [전문 이상] 카드번호가 없습니다.";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				}
//			
//			}
			
			// 상차정지위치
			szYD_CARLD_STOP_LOC = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CARLD_STOP_LOC");
			if(szYD_CARLD_STOP_LOC.equals("")){
				szMsg = "["+szOperationName+"] [전문 이상] 상차정지위치가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}

			// 발지개소코드
			szSPOS_WLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_WLOC_CD");
			if(szSPOS_WLOC_CD.equals("")){
				szMsg = "["+szOperationName+"] [전문 이상] 발지개소코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}
			
			// 발지포인트코드
			szSPOS_YD_PNT_CD = ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_YD_PNT_CD");
			if(szSPOS_YD_PNT_CD.equals("")){
				szMsg = "["+szOperationName+"] [전문 이상] 발지포인트코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}
						
			int seq = 0;
			// 재료번호, 권상모음순서
			for(int i=0; i<intMtlCnt; i++){
				// 재료번호
				szSTL_NO = ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO" + (i+1));
				if(szSTL_NO.equals("")){
					szMsg = "["+szOperationName+"] [전문 이상] " + i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;
				}
				
				szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP" + (i+1));

				if( i == 0 ) {
					seq = 1;
					szPREV_YD_STK_COL_GP = szYD_STK_COL_GP;
					rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("STL_NO", szSTL_NO);
					recInTemp.setField("YD_UP_COLL_SEQ", "" + seq);
					recInTemp.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
					recInTemp.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO" + (i+1)));
					recInTemp.setField("YD_STK_LYR_NO", ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_LYR_NO" + (i+1)));
					rsResult.addRecord(recInTemp);
					vGroup.add(rsResult);
				}else{
					if( !szYD_STK_COL_GP.substring(0, 2).equals(szPREV_YD_STK_COL_GP.substring(0, 2)) ) {
						rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
						vGroup.add(rsResult);
						seq = 1;
					}
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("STL_NO", szSTL_NO);
					recInTemp.setField("YD_UP_COLL_SEQ", "" + seq);
					recInTemp.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
					recInTemp.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO" + (i+1)));
					recInTemp.setField("YD_STK_LYR_NO", ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_LYR_NO" + (i+1)));
					rsResult.addRecord(recInTemp);
					szPREV_YD_STK_COL_GP = szYD_STK_COL_GP;
				}
				seq++;
			}
			
			szMsg="["+szOperationName+"] 전문확인 후 출하상차LOT 그룹 수 : " + vGroup.size();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			

			for(int i = 0; i < vGroup.size(); i++ ) {
				rsTemp = (JDTORecordSet)vGroup.get(i);
				for(int j = 1; j <= rsTemp.size(); j++ ) {
					rsTemp.absolute(j);
					recInTemp = rsTemp.getRecord();
					szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recInTemp, "YD_STK_COL_GP");
					szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recInTemp, "YD_STK_BED_NO");
					szYD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(recInTemp, "YD_STK_LYR_NO");
					szYD_UP_COLL_SEQ = ydDaoUtils.paraRecChkNull(recInTemp, "YD_UP_COLL_SEQ");
					szSTL_NO = ydDaoUtils.paraRecChkNull(recInTemp, "STL_NO");
					if( j  == 1 ) {
						
						szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + szYD_SCH_CD.substring(2);
						
						recPara = JDTORecordFactory.getInstance().create();
						//-- 통합 크레인 스케줄 조회 -----------------------------------------------------------------start--
						rsResult = JDTORecordFactory.getInstance().createRecordSet("");
						recInPara = JDTORecordFactory.getInstance().create();
						
						recInPara.setField("YD_SCH_CD", szYD_SCH_CD);
						
						intRtnVal = commDao.select(recInPara, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0095");	
						
						if(intRtnVal == 0) {
							szMsg = "["+szOperationName+"] 통합 크레인 스케줄 코드 조회 0건 - ["+szYD_SCH_CD+"]";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							return YdConstant.RETN_CD_FAILURE;
						}
						
						//레코드 추출
						rsResult.first();
						recPara = rsResult.getRecord();
						
						szCrn 			= ydDaoUtils.paraRecChkNull(recPara, "WRK_CRN");
						szYD_SCH_PRIOR 	= ydDaoUtils.paraRecChkNull(recPara, "CRN_PRIOR");
						//-- 통합 크레인 스케줄 조회 ------------------------------------------------------------------end---
						
						// 리턴 recordSet 생성
						rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
						
						// 작업예약ID 생성
						blnRtnVal = getYdWbookId(rsResult);
						if(!blnRtnVal) return YdConstant.RETN_CD_FAILURE;
			
						// 레코드추출
						rsResult.first();
						recPara = rsResult.getRecord();
						
						// 작업예약ID
						szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");
						
						if( szYD_CARLD_STOP_LOC.substring(0, 2).equals(szYD_STK_COL_GP.substring(0, 2)) ) {
							//현재작업예약이 등록되는 동과 차량정지위치가 같은 스케줄코드와 
							szCURR_YD_SCH_CD = szYD_SCH_CD;
							szCURR_YD_WBOOK_ID = szYD_WBOOK_ID;
							szCurrCrn = szCrn;
						}
						
						// 리턴 RecordSet 생성
						rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
						
						// 저장품테이블 조회
						blnRtnVal = this.chkGetStock(szSTL_NO, rsResult);
						if(!blnRtnVal) return YdConstant.RETN_CD_FAILURE;
						
						// 레코드추출
						rsResult.first();
						recPara = rsResult.getRecord();
						
						// 야드목표야드구분
						szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_YD_GP");
						
						// 야드목표동구분
						szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_BAY_GP");
						
						// INSERT 항목 record 생성
						recPara = JDTORecordFactory.getInstance().create();
						
						// 야드구분
						szYD_GP = szYD_SCH_CD.substring(0, 1);
						
						// 동구분
						szYD_BAY_GP = szYD_SCH_CD.substring(1, 2);
						
						// INSERT할 항목 SET
						recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
						recPara.setField("YD_GP", 		  szYD_GP);
						recPara.setField("YD_BAY_GP", 	  szYD_BAY_GP);
						recPara.setField("YD_SCH_CD", 	  szYD_SCH_CD);
						recPara.setField("YD_SCH_PRIOR",  szYD_SCH_PRIOR);
						recPara.setField("REGISTER", 	  szUser);
						recPara.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP); 
						recPara.setField("CAR_NO",	      szCAR_NO);
						recPara.setField("CARD_NO", 	  szCARD_NO); 
						recPara.setField("YD_AIM_YD_GP",  szYD_AIM_YD_GP);  
						recPara.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP); 

						// 작업예약 INSERT
						intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
						if(intRtnVal < 1){
							szMsg = "["+szOperationName+"] 작업예약 데이터 등록 중 에러";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							return YdConstant.RETN_CD_FAILURE;
						}
					}
					
					//조회항목 record 생성
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
					recPara.setField("REGISTER", 	  szUser);
					recPara.setField("STL_NO"       , szSTL_NO);
					//적치열구분
					recPara.setField("YD_STK_COL_GP",  szYD_STK_COL_GP);
					//적치BED번호
					recPara.setField("YD_STK_BED_NO",  szYD_STK_BED_NO);
					//적치단번호
					recPara.setField("YD_STK_LYR_NO",  szYD_STK_LYR_NO);
					//권상모음순서
					recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ);
					
					// 작업예약재료 테이블에 등록한다.
					intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
					if(intRtnVal < 1){
						szMsg = "["+szOperationName+"] 작업예약재료 데이터 등록 중 에러";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						// 예외를 발생시켜 롤백 시킴
						throw new DAOException(szSessionName + " : " + szMethodName + " - " + szMsg);
					}
				}
			}
			// [전사물류시스템개선] 추가(Y9시스템 전송여부)
			// 2021/01/06
			if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYD_GP)){
				JDTORecord params = null;
				if(PlateGdsYdUtil.isPlateNewMoudleApply("CRN")){
					
					// 트랜잭션 이슈로 인하여 한 번 더 저장처리한다.
					recCarSchPara  = JDTORecordFactory.getInstance().create();
					rsCarSchResult = JDTORecordFactory.getInstance().createRecordSet("");
					recCarSchPara.setField("CAR_NO" , szCAR_NO);
					recCarSchPara.setField("CARD_NO", szCARD_NO);
//PIDEV_S :병행가동용:PI_YD
					recCarSchPara.setField("PI_YD",    	szYD_GP);		
					intRtnVal = ydCarSchDao.getYdCarsch(recCarSchPara, rsCarSchResult, 11);
					if(intRtnVal>0){
						szYD_CAR_SCH_ID = rsCarSchResult.getRecord(0).getFieldString("YD_CAR_SCH_ID"); 
						JDTORecord rTmp = JDTORecordFactory.getInstance().create();
						rTmp.setField("YD_CAR_PROG_STAT", "2");									//상차도착상태
						rTmp.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);
						rTmp.setField("YD_CARLD_STOP_LOC", szYD_CARLD_STOP_LOC); 
						rTmp.setField("YD_PNT_CD1"         , szSPOS_YD_PNT_CD);
						rTmp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);	
						ydCarSchDao.updYdCarsch(rTmp, 0);
					}
					
    				// 트랜잭션 이슈로 인하여 대기장 도착모듈 호출 이후 예정정보를 전송
    				// 상차 예정정보 전송은 
    				// 1. 전문을 수신하였을 경우 com.inisteel.cim.yd.plateGds.session.PlateYdRcvFaEJBBean.rcvDMYDR061(JDTORecord)
    				// 2. 마지막 권하실적을 처리 후 차량정보 초기화 모듈(현재 메서드)com.inisteel.cim.yd.ydWkAct.CraneUnloadWkrHd.CraneUdHdSeEJBBean.procY4CarWrkStatCtr(JDTORecord)
    				// 3. PT차량 출하차량도착 처리 이후 DMYDR038
    				params = JDTORecordFactory.getInstance().create();
    				JDTORecordSet rsCarSchInfo = JDTORecordFactory.getInstance().createRecordSet("ydPlate");
    				params.setField("CAR_NO", szCAR_NO);
    				params.setField("YD_CARLD_STOP_LOC", szYD_CARLD_STOP_LOC);
    				
    				if(commDao.select(params, rsCarSchInfo, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.getYdCarSchById")>0){
    					
    					// PT차량이거나, 최초로 입동한 차량에 대해서만
    					// 복수동은 두번째 부터 권하실적처리 하면서 처리함
//    					String FIRST_BAYIN_CAR = rsCarSchInfo.getRecord(0).getFieldString("FIRST_BAYIN_CAR");
//    					if( szCARD_NO.startsWith("P") || "Y".equals(FIRST_BAYIN_CAR)){						
    						szMsg = "차랑변호["+szCAR_NO+"] 차량에 대하여 차량예정정보(Y9)를 전송한다.";
    						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    						JDTORecord jYDY9L008 = JDTORecordFactory.getInstance().create();
    						JDTORecordSet msgSetYDY9L008 = JDTORecordFactory.getInstance().createRecordSet("ydPlate");
    						jYDY9L008.setField("JMS_TC_CD", "YDY9L008");
    						jYDY9L008.setField("SEARCH_FLAG", "1");  //1:상차도, 2:차량스케쥴 ID
    						jYDY9L008.setField("PT_LOAD_LOC", szYD_CARLD_STOP_LOC);  //상차도 위치
    						jYDY9L008.setField("YD_WBOOK_ID",  szYD_WBOOK_ID);  //차량정지 위치
    						jYDY9L008.setField("YD_CAR_SCH_ID", ""); //차량스케쥴 ID
    						jYDY9L008.setField("CAR_NO",  szCAR_NO);  //차량번호
    						jYDY9L008.setResultCode(ydUtils.getLogId());
    						EJBConnector ejbConn1 = new EJBConnector("default", "PlateYdRcvL2SeEJB", this);
    						ejbConn1.trx("rcvY9YDL019", new Class[] { JDTORecord.class }, new Object[] { jYDY9L008 });
//    					}
    				}
				}
			}
	
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("JMS_TC_CD", "YDYDJ293");
			recInTemp.setField("CAR_NO", 			szCAR_NO);
			recInTemp.setField("CARD_NO", 			szCARD_NO);
			recInTemp.setField("YD_WBOOK_ID", 		szCURR_YD_WBOOK_ID);
			recInTemp.setField("YD_CARLD_STOP_LOC", szYD_CARLD_STOP_LOC);
			recInTemp.setField("SPOS_YD_PNT_CD"   , szSPOS_YD_PNT_CD);
			recInTemp.setField("YD_SCH_CD", 		szCURR_YD_SCH_CD);
			recInTemp.setField("YD_CRN_ID", 		szCurrCrn);
			recInTemp.setField("SPOS_WLOC_CD", 		szSPOS_WLOC_CD); //2014.02.18 cho 추가
			 
			szMsg="["+szOperationName+"] 출하차량스케줄 수정 전문 전송 시작 - 전문내용확인";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			ydDelegate.sendMsg(recInTemp);
			
			szMsg="["+szOperationName+"] 출하차량스케줄 수정 전문 전송 성공";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		} catch(DAOException e){
			szMsg = "["+szOperationName+"] 출하 차량상차 작업요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw e;
		}catch(Exception e){
			szMsg = "["+szOperationName+"] 출하 차량상차 작업요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(e);
		}
		return YdConstant.RETN_CD_SUCCESS;
	} // end of procPlGdsDistCarLdWrkReq()			
	
	/**
	 * 출하차량스케줄 수정(YDYDJ293)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String procDistCarSch(JDTORecord msgRecord) {
		/*
		 * 업무기준:	1. 차량스케줄을 조회해서 상차도착으로 수정
		 * 			2. 해당 상차작업예약으로 크레인스케줄 호출
		 * 수정자 : 임춘수
		 * 수정일 :
		 * 			1. 2009.11.17 - 최초등록
		 * 파라미터정의 :
		 * 			1. CAR_NO				- 차량번호
		 * 			2. CARD_NO				- 카드번호
		 * 			3. YD_WBOOK_ID			- 상차작업예약ID
		 * 			4. YD_CARLD_STOP_LOC	- 상차정지위치
		 * 			5. SPOS_YD_PNT_CD		- 상차정지POINT코드
		 * 			6. YD_SCH_CD			- 크레인스케줄코드
		 * 			7. YD_CRN_ID			- 크레인설비ID
		 * 
		 * 호출 모듈 :
		 * 			1. 코일제품출하차량상차작업요구
		 * 			2. 후판제품출하차량상차작업요구
		 */
		String szMsg					= null;
		String szMethodName				= "procDistCarSch";
		String szOperationName			= "출하차량스케줄 수정";
		//전문 전달 파라미터 항목 시작
		String szCAR_NO					= null;
		String szCARD_NO				= null;
		String szYD_WBOOK_ID			= null;
		String szYD_CARLD_STOP_LOC		= null;
		String szSPOS_YD_PNT_CD			= null;
		String szYD_SCH_CD				= null;
		String szCurrCrn				= null;
		String szSPOS_WLOC_CD			= null;
		String szYD_EQP_WRK_STAT		= null;
		
		
		//전문 전달 파라미터 항목 끝
		String[] szTC_CODE				= null;
		String szYD_GP					= null;
		String szYD_CAR_SCH_ID			= null;
		JDTORecord recInTemp			= null;
		JDTORecord recCarSchPara		= null;
		JDTORecordSet rsCarSchResult	= null;
		YdCarSchDao	ydCarSchDao			= new YdCarSchDao();
		// 설비
		YdEqpDao     ydEqpDao     = new YdEqpDao();
		YdPlateCommDAO commDao = new YdPlateCommDAO();
		int intRtnVal					= -100;
		try {
			szMsg = "["+szOperationName+"] 메소드 시작 : 전문확인";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szCAR_NO 			= ydDaoUtils.paraRecChkNull(msgRecord, "CAR_NO");
			szCARD_NO 			= ydDaoUtils.paraRecChkNull(msgRecord, "CARD_NO");
			szYD_WBOOK_ID 		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");
			szYD_CARLD_STOP_LOC = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CARLD_STOP_LOC");
			szSPOS_YD_PNT_CD 	= ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_YD_PNT_CD");
			szYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			szCurrCrn 			= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_ID");
			szSPOS_WLOC_CD		= ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_WLOC_CD"); //2014.02.18 cho 추가
			
			szYD_GP = szYD_CARLD_STOP_LOC.substring(0, 1);
			//스케줄 메소드 호출
			recCarSchPara  = JDTORecordFactory.getInstance().create();
			rsCarSchResult = JDTORecordFactory.getInstance().createRecordSet("");
			recCarSchPara.setField("CAR_NO" , szCAR_NO);
			recCarSchPara.setField("CARD_NO", szCARD_NO);
//PIDEV_S :병행가동용:PI_YD
			recCarSchPara.setField("PI_YD",    	szYD_GP);
			intRtnVal = ydCarSchDao.getYdCarsch(recCarSchPara, rsCarSchResult, 11);
			if(intRtnVal < 0){
				szMsg = "["+szOperationName+"] 차량스케쥴 조회 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;			
			}else if(intRtnVal == 0){
				/*
				 * 수정자 : 임춘수
				 * 수정일 : 2009.09.11
				 */
				//차량스케줄 생성하는 부분은 제거처리 ----> 상차지시에서 차량스케줄을 먼저 생성하는 걸로 변경
				//intRtnVal = this.mkY1CarSch(recPara);
				szMsg = "["+szOperationName+"] 차량스케줄이 존재하지 않습니다 - 차량스케쥴 생성 유무 결정 필요?";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				if( intRtnVal != 1 ) return YdConstant.RETN_CD_FAILURE;
				
			}else {
				/* 
				 * 차량스케줄을 조회해서 상차작업예약ID와 차량진행상태 등의 상태값을 변경한다.
				 * 수정자 : 임춘수
				 * 수정일 : 2009.09.11
				 */
				rsCarSchResult.first();
				recCarSchPara = rsCarSchResult.getRecord();
				szYD_CAR_SCH_ID = StringHelper.evl(recCarSchPara.getFieldString("YD_CAR_SCH_ID"), "");
				szYD_EQP_WRK_STAT= StringHelper.evl(recCarSchPara.getFieldString("YD_EQP_WRK_STAT"), ""); 
				szMsg = "["+szOperationName+"] 차량스케쥴["+szYD_CAR_SCH_ID+"] 조회 성공 후 차량도착상태로 변경 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
				recInTemp.setField("MODIFIER", szMethodName.substring(0, 10));
				
				
			 
				if(!"T".equals(szYD_GP)){
					if(szYD_EQP_WRK_STAT.equals("L")){
						recInTemp.setField("YD_CAR_PROG_STAT", "B");									//하차도착상태 
						recInTemp.setField("YD_CARUD_WRK_BOOK_ID", szYD_WBOOK_ID);
						recInTemp.setField("YD_CARUD_STOP_LOC", szYD_CARLD_STOP_LOC);
						recInTemp.setField("YD_CARUD_ARR_DT", YdUtils.getCurDate("yyyyMMddHHmmss"));
						recInTemp.setField("YD_PNT_CD3"         , szSPOS_YD_PNT_CD);
					}else{
						recInTemp.setField("YD_CAR_PROG_STAT", "2");									//상차도착상태
						recInTemp.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);
						recInTemp.setField("YD_CARLD_STOP_LOC", szYD_CARLD_STOP_LOC);
						recInTemp.setField("YD_CARLD_ARR_DT", YdUtils.getCurDate("yyyyMMddHHmmss"));
						recInTemp.setField("YD_PNT_CD1"         , szSPOS_YD_PNT_CD);
					}
				}else{
					recInTemp.setField("YD_CAR_PROG_STAT", "2");									//상차도착상태
					recInTemp.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);
					recInTemp.setField("YD_CARLD_STOP_LOC", szYD_CARLD_STOP_LOC);
					recInTemp.setField("YD_CARLD_ARR_DT", YdUtils.getCurDate("yyyyMMddHHmmss"));
					recInTemp.setField("YD_PNT_CD1"         , szSPOS_YD_PNT_CD);
				}
				
				if(!"".equals(szSPOS_WLOC_CD)) {
					recInTemp.setField("SPOS_WLOC_CD"         , szSPOS_WLOC_CD); //2014.02.18 cho 발지개소코드를 파라메터 받은 값으로 설정한다.
				}
				
				intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 0);
				
				if( intRtnVal == 0 ) {
					szMsg = "["+szOperationName+"] 차량스케쥴["+szYD_CAR_SCH_ID+"] 조회 성공 후 차량도착상태로 변경 시 차량스케줄이 존재하지 않습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					throw new DAOException(szMsg);
				}else if( intRtnVal < 0 ) {
					szMsg = "["+szOperationName+"] 차량스케쥴["+szYD_CAR_SCH_ID+"] 조회 성공 후 차량도착상태로 변경 시 차량스케줄이 존재하지 않습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					throw new DAOException(szMsg);
				}
				
				szMsg = "["+szOperationName+"] 차량스케쥴["+szYD_CAR_SCH_ID+"] 조회 성공 후 차량도착상태로 변경 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				// 2021. 4. 28
				// 현업요청으로 인한
				//  - 하나의 차량에 2개의 운송지시가 편성된 차량(고중량)에 대해서 뒷차의 우선순위를 1로 설정
				//   : 하나의 작업으로 처리 (운송지시를 분리하여 동일차량으로 2개이상의 스케쥴이 편성되어짐)
				//   : 복수상차의 우선순위가 높아 중간에 껴들어가는 것을 방지하기 위함
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
				recInTemp.setField("MODIFIER", szMethodName.substring(0, 10));
				commDao.update(recInTemp, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updYdCarSchByOver30TBaySeq"); 

				/*
				 * hun 150820 야드구분=J , AutoCrn=true 일때 차량 작업 예정정보 전송
				 */
				szMsg = "입동시 야드 구분=J, Auto크레인 경우 차량정보 예정정보 전송 start";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				ydUtils.putLog(szSessionName, szMethodName, szCurrCrn+"szCurrCrn isAuto="+ydEqpDao.chkAutoCrn(szCurrCrn), YdConstant.DEBUG);
				ydUtils.putLog(szSessionName, szMethodName, "szYD_CAR_SCH_ID ="+szYD_CAR_SCH_ID , YdConstant.DEBUG);
				
				// 150824 hun 차량최초 도착시 차량작업 예정정보 chkAuto 삭제 유무인 모두 나감으로 변경
//				if("J".equals(szYD_GP) && ydEqpDao.chkAutoCrn(szCurrCrn) ){
				if("J".equals(szYD_GP) ){
					
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_CAR_SCH_ID"			,szYD_CAR_SCH_ID);
					
		        	String szRtnMsg = this.callYDY5L008(recInTemp);
		        	
		        	if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {													//성공
		        		szMsg = "[JSP Session](차량작업 예정정보송신) 호출 성공";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}else{
						szMsg = "[JSP Session](차량작업 예정정보송신) 호출 실패";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
		        	
	    		} // if
				
				szMsg = "입동시 야드 구분=J, Auto크레인 경우 차량정보 예정정보 전송 END";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				// 야드 구분=J, Auto크레인 경우 차량정보 예정정보 전송 END
				////////////////////////////////////////////////////////////////////////////////////////
				

				// [전사물류시스템개선] 추가(Y9시스템 전송여부)
				// 2021/01/06
				boolean isSendToEaiY9 = false;
				if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYD_GP)){
					
					//차량예정정보송신 YDY9L008
					JDTORecord params = null;
					isSendToEaiY9 = PlateGdsYdUtil.isSendToEaiY9_ydStkColGp(szYD_CARLD_STOP_LOC);
					if(isSendToEaiY9){
						szMsg = "입동시 야드 구분=T, 신규L2시스템 전송여부(true/false) :: " + isSendToEaiY9;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG); 
						JDTORecordSet rsResult = JDTORecordFactory.getInstance().createRecordSet("");
						params = JDTORecordFactory.getInstance().create();
			    		
						params.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
			    		params.setField("YD_STK_COL_GP", szYD_CARLD_STOP_LOC);
			    		params.setField("YD_GP", YdConstant.YD_GP_PLATE2_GDS_YARD );
			    		
			    		if(commDao.select(params, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.getYdCarPointByYdStkColGp") > 0){
							// 해당 상차지에 형상을 사용하겠다하면 크레인스케쥴 Main은 호출하지 않는다.
							if("Y".equals(rsResult.getRecord(0).getField("YD_FRM_YN"))
									&& !"Y".equals(rsResult.getRecord(0).getField("YD_FRM_PASS_YN"))){
								return YdConstant.RETN_CD_SUCCESS;
							}
//							
							// 형상유무 pass 여부를 공백으로 처리한다.
							if("Y".equals(rsResult.getRecord(0).getField("YD_FRM_PASS_YN"))){
								
								szMsg = "바로 전 입동상차지와 동일한 상차지 이므로 PASS하며";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								
								JDTORecord jtoUpdate = JDTORecordFactory.getInstance().create();
								jtoUpdate.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
								jtoUpdate.setField("YD_CARLD_SCH_REQ_GP",  "");
								jtoUpdate.setField("MODIFIER",        "YDYDJ293"); 
								commDao.update(jtoUpdate, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updYdCarschByFrmYnPassYn"); 

								szMsg = "바로 전 입동상차지와 동일한 상차지 이므로 형상은 측정완료 플래그를 업데이트한다.";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								jtoUpdate = JDTORecordFactory.getInstance().create();
								jtoUpdate.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
								jtoUpdate.setField("YD_CTS_RELAY_YN",  "Y");
								jtoUpdate.setField("MODIFIER",        "YDYDJ293"); 
								commDao.update(jtoUpdate, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.updYdWrkBookByFrmYnYn");
							}
						}
					}
// PIDEV 후판 포인트 지시 추가되면서 자동으로 스케쥴 생성 안됨  : M10LMYDJ1052
//					String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "", "APPPI0", "T", "*");
					
//					if("Y".equals(sApplyYnPI)) {
						ydUtils.putLog(szSessionName, szMethodName, "PI 적용됨", YdConstant.DEBUG);

						//PIDEV_S :병행가동용:PI_YD
						//후판 포인트 지시 추가되면서 자동으로 스케쥴 생성 안됨  : M10LMYDJ1052
						/* com.inisteel.cim.yd.common.dao.YdPlateCommDAO.getYdCrnSchStartYn_PIDEV 
						SELECT DECODE(COUNT(*),0,'N','Y') AS FLAG_GP 
						  FROM TB_YD_CARSCH A
						     , VW_LM_P_TRANSWORDCOMM B
						 WHERE A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID  
						   AND A.TRANS_ORD_DATE = B.TRANS_WORD_DATE
						   AND A.TRANS_ORD_SEQNO = B.TRANS_WORD_SEQNO
						   AND B.TRANS_FRTOMOVE_GP IN ('11','12','13') --내수만
						*/   
						JDTORecord recInTemp1 = JDTORecordFactory.getInstance().create();
						recInTemp1.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
						recInTemp1.setField("PI_YD"        , szYD_GP);
						JDTORecordSet rsResult3 	= JDTORecordFactory.getInstance().createRecordSet("");
						intRtnVal = commDao.select(recInTemp1, rsResult3, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.getYdCrnSchStartYn_PIDEV");	
						String szFLAG_GP = rsResult3.getRecord(0).getFieldString("FLAG_GP"); 
						if("Y".equals(szFLAG_GP)) {
							return YdConstant.RETN_CD_SUCCESS;
						}
//					} 
				}
				
				
				/* 
				 * 크레인스케줄Main을 호출한다.
				 * 수정자 : 임춘수
				 * 수정일 : 2009.09.11
				 */
				szTC_CODE = YdCommonUtils.getCrnSchTCByYD(szYD_GP);
				szMsg = "["+szOperationName+"] 해당야드["+szYD_GP+"]의 크레인스케줄Main["+szTC_CODE[0]+"] 호출 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				recInTemp = JDTORecordFactory.getInstance().create();
				//전문코드
				recInTemp.setField("JMS_TC_CD", szTC_CODE[0]);
	    		//스케줄코드
	    		recInTemp.setField("YD_SCH_CD", szYD_SCH_CD);
	    		//설비ID
	    		recInTemp.setField("YD_EQP_ID", szCurrCrn);
	    		//작업예약ID
	    		recInTemp.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
	    		
	    		//전문호출 조건 판단 
	    		// 해당 스케줄이 이미 존재한다면 작업예약만 만들고 스케줄Main 호출은 하지 않는다.
	    		if("TR".equals(szYD_SCH_CD.substring(2,4))||"PT".equals(szYD_SCH_CD.substring(2,4))) {
	    			
	    			JDTORecordSet rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
			
					intRtnVal = commDao.select(recInTemp, rsResult1, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0110");	
					
					if(intRtnVal > 0) {
						//스케줄이 존재한다면 스케줄 Main을 호출하지 않는다.
						
						szMsg = "["+szOperationName+"] 해당야드["+szYD_GP+"]의 크레인스케줄 ["+szYD_SCH_CD+"] 이 존재하여 크레인스케줄Main 호출 안함";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
						
					} else {
						
			    		//전문 송신
						ydDelegate.sendMsg(recInTemp);
						
						szMsg = "["+szOperationName+"] 해당야드["+szYD_GP+"]의 크레인스케줄Main["+szTC_CODE[0]+"] 호출 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		
						
						//25.07.23 후판정정야드 니켈강 출하 추가 - 허동수책임 요청 
						if("P".equals(szYD_GP)){
							szMsg = "["+szOperationName+"] 해당야드["+szYD_GP+"]의 크레인스케줄Main 전문 존재 안하므로 ejb 직접호출";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
							
							JDTORecord recSchPara = JDTORecordFactory.getInstance().create("");
							
							recSchPara.setField("MSG_ID",           "YDYDJ"			);      // TC코드
		                    recSchPara.setField("YD_EQP_ID",        szCurrCrn		);      // 크레인설비ID
		                    recSchPara.setField("YD_SCH_CD",        szYD_SCH_CD		);      // 크레인스케줄코드
		                    recSchPara.setField("YD_WBOOK_ID",      szYD_WBOOK_ID	);    	// 작업예약ID
		                    recSchPara.setField("REGISTER",         "prodDist"		);
		                    recSchPara.setField("MODIFIER",         "prodDist"		);
		                    recSchPara.setField("YD_TO_LOC_GUIDE",  ""	);    	// 야드To위치Guide
		                    recSchPara.setField("CHK_FROM_LOC",     "N"				);      // RT작업일경우 권상예약 체크 안하도록 보완
							
							
							EJBConnector ejbConn = new EJBConnector("default", "JPlateYdCrnSchYdPSeEJB", this);
			                ejbConn.trx("procCrnSchMainYdP", new Class[] { JDTORecord.class }, new Object[] { recSchPara });
						}
					}
	    			
	    		} else {
	    		
		    		//전문 송신
					ydDelegate.sendMsg(recInTemp);
					
					szMsg = "["+szOperationName+"] 해당야드["+szYD_GP+"]의 크레인스케줄Main["+szTC_CODE[0]+"] 호출 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		}
			}
		}catch(Exception ex) {
			szMsg = "["+szOperationName+"] 오류발생 - 메세지 : " + ex.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;
		}
		return YdConstant.RETN_CD_SUCCESS;
	}
	

    /**
     * 오퍼레이션명 : C열연코일야드L2 차량작업정보 송신 (YDY5L008)
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return String
     * @throws JDTOException
     */
    public String callYDY5L008 ( JDTORecord recInPara )throws JDTOException  {
    	JDTORecordSet  rsResult= JDTORecordFactory.getInstance().createRecordSet("temp");
    	YdCarSchDao ydCarSchDao = new YdCarSchDao();
//    	JDTORecord recInPara = null;
    	JDTORecord recOutTemp = null;
    	int intRtnVal =0;
    	String szMsg = "";
    	String szMethodName = "callYDY5L008";
    	String szOperationName			= "차량작업 예정정보 전송";
    	String szLOAD_LOC_CD = "";
    	
    	szMsg="callYDY5L008("+szMethodName+") 시작";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    	
    	try {
    		
	    	// 차량작업 예정정보 조회
    		ydUtils.putLog(szSessionName, szMethodName, "callYDY5L008 YD_CAR_SCH_ID=("+ydDaoUtils.paraRecChkNull(recInPara , "YD_CAR_SCH_ID")+")", YdConstant.DEBUG);
    		

//PIDEV_S :병행가동용:PI_YD
    		recInPara.setField("PI_YD",    	"J");    		
    		/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschCarGetInWorkInfo_PIDEV*/
    		intRtnVal = ydCarSchDao.getYdCarsch(recInPara, rsResult, 435);
	    	
	    	
	    	if( intRtnVal <= 0 ) {
				szMsg = " 차량스케줄이 존재하지 않습니다.";
	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	            return YdConstant.RETN_CD_TC_ERROR;
		    }else{
		    	
		    	rsResult.first();
				
				recOutTemp = JDTORecordFactory.getInstance().create();
				recOutTemp.setRecord(rsResult.getRecord());
				
				//차량작업 예정정보 전문 data setup
				recInPara.setField("MSG_ID"     , 		"YDY5L008");      // 전문번호
	        	
				recInPara.setField("PT_LOAD_LOC",       ydDaoUtils.paraRecChkNull(recOutTemp, "YD_PT_LOAD_LOC")); // 상차도 위치
	        	recInPara.setField("CAR_NO"     ,       ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CAR_NO")); // 차량번호
	        	recInPara.setField("PT_CLS"     ,		ydDaoUtils.paraRecChkNull(recOutTemp, "YD_PT_CLS"));
	        	recInPara.setField("WORK_CLS"   ,   	ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WORK_CLS")); // 작업구분
	        	recInPara.setField("PT_WTH"     , 		ydDaoUtils.paraRecChkNull(recOutTemp, "YD_PT_WTH"));  // 적재함 폭
	        	recInPara.setField("PT_LEN"     , 		ydDaoUtils.paraRecChkNull(recOutTemp, "YD_PT_LEN")); // 적재함 길이
	        	recInPara.setField("PT_HEIGHT"  , 		ydDaoUtils.paraRecChkNull(recOutTemp, "YD_PT_HEIGHT")); // 적재함 높이
	        	recInPara.setField("RAIN_CLS"   ,	 	ydDaoUtils.paraRecChkNull(recOutTemp, "YD_RAIN_CLS")); // 우천차량 여부
	        	recInPara.setField("WORK_COIL_MAX_CNT", ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WORK_COIL_MAX_CNT")); // 작업총 수량
	        	
	        	ydDelegate.sendMsg(recInPara);
	        	
	        	szMsg = "["+szOperationName+"] 코일야드 차량작업 예정정보 전송 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    }
	    }catch(Exception e){
			szMsg="Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_TC_ERROR;
		}

		szMsg="코일야드 차량작업 예정정보 전송 ("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		return YdConstant.RETN_CD_SUCCESS;

    }
	
	/**
	 * 오퍼레이션명 : 출하차량상차작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String procCCsOutCarLdWrkReq(JDTORecord msgRecord)throws JDTOException  {
		
		YdDelegate ydDelegate = new YdDelegate();
		//스케줄기준 DAO
		YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();
		//작업예약 재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils           = new YdDaoUtils();
		//공용 METHOD
		YdUtils ydutils                 = new YdUtils();
		
		//리턴값(boolean)
		boolean blnRtnVal      = false;
		//리턴값(int)
		int intRtnVal          = 0;
		//메세지
		String szMsg           = "";
		//METHOD명
		String szMethodName    = "procCCsOutCarLdWrkReq";
		//사용자
		String szUser          = "SYSTEM";
		
		//레코드 선언
		JDTORecord recPara     = null;
		JDTORecord recStkPara  = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		
		//설비ID
		String szYD_EQP_ID         = null;
		//목표행선구분
		String szYD_AIM_RT_GP      = null;
		//차량사용구분
		String szYD_CAR_USE_GP     = "";
		//운송장비코드
		String szTRN_EQP_CD        = "";
		//재료매수(int)
		int intMtlCnt              = 0;
		//재료번호
		String[] szSTL_NO          = null;
		//권상모음순서
		String[] szYD_UP_COLL_SEQ  = null;
		//스케줄코드
		String szYD_SCH_CD         = null;
		//야드작업크레인우선순위
		String szYD_WRK_CRN_PRIOR  = null;
		//스케줄 금지 유무
		String szYD_SCH_PROH_EXN   = null;
		//작업크레인
		String szYD_WRK_CRN        = null;
		//대체크레인유무
		String szYD_ALT_CRN_YN     = null;
		//대체크레인
		String szYD_ALT_CRN        = null;
		//선택크레인
		String szCrn               = null;
		//작업예약ID
		String szYD_WBOOK_ID       = null;
		//개소코드
		String szWLOC_CD           = null;
		//운송작업영공구분코드
		String szSP_TRUCK_LOADING_LOC_TP = null;
		//포인트요구일시
		String szPNT_DMD_DT          = null;
		//발지개소코드
		String szSPOS_WLOC_CD        = null;
		//발지포인트코드
		String szSPOS_YD_PNT_CD      = null;
		
		//야드목표야드구분
		String szYD_AIM_YD_GP        = null;
		//야드목표동구분
		String szYD_AIM_BAY_GP       = null;
		//상차정지위치
		String szYD_CARLD_STOP_LOC   = null;
		String szTRANS_ORD_DATE      = null;
		String szTRANS_ORD_SEQNO     = null;
		
		String szCAR_NO              = "";
		String szCARD_NO             = "";
		
		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		
		//에러 리턴
		if(szRcvTcCode == null){
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_TC_ERROR;
		
		}
		//TC CODE DISPLAY
		if(bDebugFlag){
		
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
		
			//받은 전문 편집
			//스케줄코드
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			if(szYD_SCH_CD.equals("")){
				
				szMsg = "[전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			// PIDEV
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "", "APPPI0", szYD_SCH_CD.substring(0,1), "*");		
			
			//설비ID
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if(szYD_EQP_ID.equals("")){
				
				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			//목표행선구분
			szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_RT_GP");
			if(szYD_AIM_RT_GP.equals("")){
				
				szMsg = "[전문 이상] 목표행선구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}
			//차량사용구분
			szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_USE_GP");
			if(szYD_CAR_USE_GP.equals("")){
				
				szMsg = "[전문 이상] 차량사용구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			
			if(szYD_CAR_USE_GP.equals("L")){
				//운송장비코드
				szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(msgRecord, "TRN_EQP_CD");
				if(szTRN_EQP_CD.equals("")){
					
					szMsg = "[전문 이상] 운송장비코드가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;
				}
				
				//개소코드
				szWLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord, "WLOC_CD");
				if(szWLOC_CD.equals("")){
					
					szMsg = "[전문 이상] 개소코드가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;
					
				}
				
				//운송작업영공구분코드
				szSP_TRUCK_LOADING_LOC_TP = ydDaoUtils.paraRecChkNull(msgRecord, "SP_TRUCK_LOADING_LOC_TP");
				if(szSP_TRUCK_LOADING_LOC_TP.equals("")){
					
					szMsg = "[전문 이상] 운송작업영공구분코드가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;
					
				}
				
				//포인트요구일시
				szPNT_DMD_DT = ydDaoUtils.paraRecChkNull(msgRecord, "PNT_DMD_DT");
				if(szPNT_DMD_DT.equals("")){
					
					szMsg = "[전문 이상] 포인트요구일시가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;
					
				}
				
			}

			//차량번호
			szCAR_NO = ydDaoUtils.paraRecChkNull(msgRecord, "CAR_NO");
			if(szCAR_NO.equals("")){
				
				szMsg = "[전문 이상] 차량번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			
			// PIDEV
//			if("N".equals(sApplyYnPI)) {
//
//				//카드번호
//				szCARD_NO = ydDaoUtils.paraRecChkNull(msgRecord, "CARD_NO");				
//				
//				if(szCARD_NO.equals("")){
//					
//					szMsg = "[전문 이상] 카드번호가 없습니다.";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//					return YdConstant.RETN_CD_NO_PARAM;
//					
//				}				
//				
//			}
						
			//발지개소코드
			szSPOS_WLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_WLOC_CD");
			if(szSPOS_WLOC_CD.equals("")){
				
				szMsg = "[전문 이상] 발지개소코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			
			//발지포인트코드
			szSPOS_YD_PNT_CD = ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_YD_PNT_CD");
			if(szSPOS_YD_PNT_CD.equals("")){
				
				szMsg = "[전문 이상] 발지포인트코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			
			//재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_CARLD_SH");
			if(intMtlCnt == 0){
				
				szMsg = "[전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			
			
			//상차정지위치
			szYD_CARLD_STOP_LOC = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CARLD_STOP_LOC");
			if(szYD_CARLD_STOP_LOC.equals("")){
				
				szMsg = "[전문 이상] 상차정지위치가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			
			//운송지시일자
			szTRANS_ORD_DATE = ydDaoUtils.paraRecChkNull(msgRecord, "TRANS_ORD_DATE");
			if(szTRANS_ORD_DATE.equals("")){
				
				szMsg = "[전문 이상] 운송지시일자가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			
			//운송지시순번
			szTRANS_ORD_SEQNO = ydDaoUtils.paraRecChkNull(msgRecord, "TRANS_ORD_SEQNO");
			if(szTRANS_ORD_SEQNO.equals("")){
				
				szMsg = "[전문 이상] 운송지시순번이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}		
			
			//재료번호
			szSTL_NO          = new String[intMtlCnt + 1];
			//권상모음순서
			szYD_UP_COLL_SEQ  = new String[intMtlCnt + 1];
			
			
			//재료번호, 권상모음순서
			for(int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
				
				//재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO" + Loop_i);
				if(szSTL_NO[Loop_i].equals("")){
					
					szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;
					
				}
				//권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_COLL_SEQ" + Loop_i);
				if(szYD_UP_COLL_SEQ[Loop_i].equals("")){
					
					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i] + ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;
					
				}
			}

			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if(!blnRtnVal) return YdConstant.RETN_CD_FAILURE;
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//스케줄CD 체크
			//스케줄 금지 유무
			szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");
			//작업크레인
			szYD_WRK_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
			//대체크레인유무
			szYD_ALT_CRN_YN   = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
			//대체크레인
			szYD_ALT_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");

			//야드작업크레인우선순위
			//szYD_WRK_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN_PRIOR");
			
			//스케줄 금지 유무가  "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if(szYD_SCH_PROH_EXN.equals("Y")){
				
				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}
			
			//작업크레인 설비 상태 체크
			blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);
			
			//작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if(!blnRtnVal){
				
				szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
				//대체크레인의 유무를 체크한다.
				//대체크레인이 없으면 에러 리턴
				if(!szYD_ALT_CRN_YN.equals("Y")){
					
					szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if(!blnRtnVal){
					
					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;
					
				} else {
					//대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					szCrn = szYD_ALT_CRN;
					szYD_WRK_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_PRIOR");
				}
			} else {
				//작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				szCrn = szYD_WRK_CRN;
				szYD_WRK_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN_PRIOR");
			}

			//재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다.
			//작업예약재료 등록 여부를 체크한다.
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
				
				//크레인사양과 저장품 사양을 체크(길이,폭,중량)
				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
				if(!blnRtnVal) return YdConstant.RETN_CD_FAILURE;
				
				//다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if(!blnRtnVal) return YdConstant.RETN_CD_FAILURE;
				
			}	
			
			//리턴 recordSet 생성
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			
			//작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if(!blnRtnVal) return YdConstant.RETN_CD_FAILURE;
			//레코드추출
			rsResult.first();
			recPara = rsResult.getRecord();
			//작업예약ID
			szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");
			
			//리턴 recordSet 생성
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			//저장품테이블 조회
			blnRtnVal = this.chkGetStock(szSTL_NO[1], rsResult);
			if(!blnRtnVal) return YdConstant.RETN_CD_FAILURE;
			//레코드추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//야드목표야드구분
			szYD_AIM_YD_GP  = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_YD_GP");
			//야드목표동구분
			szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_BAY_GP");
			
			//INSERT 항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			//야드구분
			String szYD_GP       = szYD_SCH_CD.substring(0, 1);
			//동구분
			String szYD_BAY_GP   = szYD_SCH_CD.substring(1, 2);
			
			//INSERT할 항목 SET
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
			recPara.setField("YD_GP", 		  szYD_GP);
			recPara.setField("YD_BAY_GP", 	  szYD_BAY_GP);
			recPara.setField("YD_SCH_CD", 	  szYD_SCH_CD);
			recPara.setField("YD_SCH_PRIOR",  szYD_WRK_CRN_PRIOR);
			recPara.setField("REGISTER", 	  szUser);
			recPara.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP); 
			recPara.setField("TRN_EQP_CD", 	  szTRN_EQP_CD); 
			recPara.setField("CAR_NO",		  szCAR_NO);
			recPara.setField("CARD_NO", 	  szCARD_NO); 
			recPara.setField("YD_AIM_YD_GP",  szYD_AIM_YD_GP);
			recPara.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP);
			//카드번호,차량번호
			recPara.setField("CARD_NO",  szCARD_NO);
			recPara.setField("CAR_NO", szCAR_NO);
			//운송장비코드
			recPara.setField("TRN_EQP_CD", szTRN_EQP_CD);
	
			//작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if(intRtnVal < 1){
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}	
			
			
			//조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
//			recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
//			recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", 	  szUser);

			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
				
				//리턴 recordSet 생성
				rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
				//재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				intRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C", rsResult);
				if(intRtnVal != 1) return YdConstant.RETN_CD_FAILURE;
				
				//레코드추출
				rsResult.first();
				recStkPara = rsResult.getRecord();
				
				//재료번호
				recPara.setField("STL_NO", 		   szSTL_NO[Loop_i]);
				//적치열구분
				recPara.setField("YD_STK_COL_GP",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_COL_GP"));
				//적치BED번호
				recPara.setField("YD_STK_BED_NO",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_BED_NO"));
				//적치단번호
				recPara.setField("YD_STK_LYR_NO",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_LYR_NO"));
				//권상모음순서
				recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ[Loop_i]);
				
				// 작업예약재료 테이블에 등록한다.
				intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
				
				if(intRtnVal < 1){
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;
				}
			}
			
			//record 생성
			recPara = JDTORecordFactory.getInstance().create();
			//설비ID
			recPara.setField("YD_EQP_ID",    szYD_EQP_ID);
			//차량사용구분
			recPara.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
			//운송장비코드
			recPara.setField("TRN_EQP_CD",    szTRN_EQP_CD);
			//개소코드
			recPara.setField("WLOC_CD",       szWLOC_CD);
			//운송작업영공구분코드
			recPara.setField("SP_TRUCK_LOADING_LOC_TP", szSP_TRUCK_LOADING_LOC_TP);
			//포인트요구일시
			recPara.setField("PNT_DMD_DT",    szPNT_DMD_DT);
			//발지개소코드
			recPara.setField("SPOS_WLOC_CD",  szSPOS_WLOC_CD);
			//발지포인트코드
			recPara.setField("SPOS_YD_PNT_CD", szSPOS_YD_PNT_CD);
			//작업예약id
			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			//스케줄코드
			recPara.setField("YD_SCH_CD", 	  szYD_SCH_CD);
			//크레인설비ID
			recPara.setField("YD_CRN_EQP_ID", 	  szCrn);
			//차량번호
			recPara.setField("CAR_NO", 	  szCAR_NO);
			//카드번호
			recPara.setField("CARD_NO",   szCARD_NO);
			//상차정지위치
			recPara.setField("YD_CARLD_STOP_LOC",   szYD_CARLD_STOP_LOC);
			//운송지시일자
			recPara.setField("TRANS_ORD_DATE",   szTRANS_ORD_DATE);
			//운송지시순번
			recPara.setField("TRANS_ORD_SEQNO",   szTRANS_ORD_SEQNO);
			
			//스케줄 메소드 호출
			intRtnVal = this.mkY1CarSch(recPara);
			
			if( intRtnVal != 1 ){
				return YdConstant.RETN_CD_FAILURE;
			}
			
			/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    		 * 업무기준 : 출하 공차 도착 시 저장위치 제원 야드L2로 전송
    		 * 수정자 : 임춘수
    		 * 수정일자 : 2009.08.24
    		 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
			if( !szYD_CAR_USE_GP.equals("L")){							//출하차량(G)
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_INFO_SYNC_CD", "3");							//1:동,2:SPAN,3:열,4:BED
				recPara.setField("YD_GP", szYD_EQP_ID.substring(0, 1));
				recPara.setField("YD_STK_COL_GP", szYD_EQP_ID);
				//recPara("YD_STK_BED_NO", szYD_CARLD_STOP_LOC);
				recPara.setField("YD_CAR_PROG_STAT", "2");
				recPara.setField("YD_EQP_WRK_STAT", "U");
				
				YdCommonUtils.sndStrPosSpecToL2(recPara);
				szMsg="<procCCsOutCarLdWrkReq>  출하 공차도착 시 저장위치 제원 야드L2로 전송";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
			}
			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * 			출하관리 외판슬라브운송Lot편성실적 전송  - YDDMR010
	         * 업무기준 Desc : 1. 외판슬라브운송Lot편성후 차량상차작업요구 등록 후
	         * 기능 추가 : 임춘수
	         * 일자 : 2009.06.16
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//			if( szYD_GP.equals(YdConstant.YD_GP_C_SLAB_YARD) ){
//				recPara = JDTORecordFactory.getInstance().create();
//				recPara.setField("TC_CODE", "YDDMR010");
//				recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
//				ydDelegate.sendMsg(recPara);
//				szMsg = "외판슬라브출하차량상차 작업요구 처리 - 출하관리 외판슬라브운송Lot편성실적 전송 완료 ";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//			}
			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
		
		} catch(Exception e){
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
	public void ydManualReq(JDTORecord msgRecord)throws JDTOException  {
		
		//스케줄기준 DAO
		YdWrkbookDao 	ydWrkbookDao    = new YdWrkbookDao();
		//작업예약 재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		//저장품 DAO
		YdStockDao 		ydStockDao 		= new YdStockDao();
		
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils           = new YdDaoUtils();
		//공용 METHOD
		YdUtils ydutils                 = new YdUtils();
		
		//리턴값(boolean)
		boolean blnRtnVal      = false;
		//리턴값(int)
		int intRtnVal          = 0;
		//메세지
		String szMsg           = "";
		//METHOD명
		String szMethodName    = "ydManualReq";
		//사용자
		String szUser          = ydDaoUtils.paraRecChkNull(msgRecord, "REGISTER");  
		
		
		//레코드 선언
		JDTORecord recPara     = null;
		JDTORecord recStkPara  = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		
		//설비ID(열구분)
		String szYD_STK_COL_GP     = null;
		//적치BED번호
		String szYD_STK_BED_NO     = null;
		//재료매수(int)
		int intMtlCnt              = 0;
		//재료번호
		String[] szSTL_NO          = null;
		//권상모음순서
		String[] szYD_UP_COLL_SEQ  = null;
		//스케줄코드
		String szYD_SCH_CD         = null;
		//스케줄 금지 유무
		String szYD_SCH_PROH_EXN   = null;
		//작업크레인
		String szYD_WRK_CRN        = null;
		//대체크레인유무
		String szYD_ALT_CRN_YN     = null;
		//대체크레인
		String szYD_ALT_CRN        = null;
		//선택크레인
		String szCrn               = null;
		//작업예약ID
		String szYD_WBOOK_ID       = null;
		//목표야드구분
		String szYD_AIM_YD_GP      = null;
		//목표동구분
		String szYD_AIM_BAY_GP     = null;
		//스케줄우선순위
		String szYD_SCH_PRIOR      = null;
		
		//야드 구분
		String szYdGp = "";
		
		try {
		
			//받은 전문 편집
			//스케줄코드
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			if(szYD_SCH_CD.equals("")){
				
				szMsg = "[데이터 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}
			//적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
			if(szYD_STK_COL_GP.equals("")){
				
				szMsg = "[데이터 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}
			//적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
			if(szYD_STK_BED_NO.equals("")){
				
				szMsg = "[데이터 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}
			//재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "SLAB_SH");
			if(intMtlCnt == 0){
				
				szMsg = "[데이터 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}
			
			//재료번호
			szSTL_NO          = new String[intMtlCnt + 1];
			//권상모음순서
			szYD_UP_COLL_SEQ  = new String[intMtlCnt + 1];

			//재료번호, 권상모음순서
			for(int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
				
				//재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO" + Loop_i);
				if(szSTL_NO[Loop_i].equals("")){
					
					szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
					
				}
				//권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_COLL_SEQ" + Loop_i);
				if(szYD_UP_COLL_SEQ[Loop_i].equals("")){
					
					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i] + ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;	
					
				}
			}

			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if(!blnRtnVal) return ;
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//스케줄CD 체크
			//스케줄 금지 유무
			szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");
			//작업크레인
			szYD_WRK_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
			//대체크레인유무
			szYD_ALT_CRN_YN   = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
			//대체크레인
			szYD_ALT_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
			
			
			//20090325 김진욱 추가////////////////////////////////////////////////////////
			//스케줄우선순위
			szYD_SCH_PRIOR    = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN_PRIOR");
			////////////////////////////////////////////////////////////////////////////
			
			
			//스케줄 금지 유무가  "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if(szYD_SCH_PROH_EXN.equals("Y")){
				
				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			
			//작업크레인 설비 상태 체크
			blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);
			
			//작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if(!blnRtnVal){
				
				szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
				//대체크레인의 유무를 체크한다.
				//대체크레인이 없으면 에러 리턴
				if(!szYD_ALT_CRN_YN.equals("Y")){
					
					szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if(!blnRtnVal){
					
					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
					
				} else {
					//대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					szCrn = szYD_ALT_CRN;
					
				}
			} else {
				//작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				szCrn = szYD_WRK_CRN;
				
			}

			//재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다.
			//작업예약재료 등록 여부를 체크한다.
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
				
				//다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if(!blnRtnVal) return ;
				
			}	
			
			
			// 리턴 RecordSet 생성
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			
			
			
			//20090325 김진욱 추가//////////////////////////////////////////////////////
			// 저장품테이블 조회
			blnRtnVal = this.chkGetStock(szSTL_NO[1], rsResult);
			if(!blnRtnVal) return ;
			
			// 레코드추출
			rsResult.first();
			
			recPara = rsResult.getRecord();
			
			// 야드목표야드구분
			szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_YD_GP");
			
			// 야드목표동구분
			szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_BAY_GP");
			//////////////////////////////////////////////////////////////////////////
			
			
			
			//리턴 recordSet 생성
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			
			//작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if(!blnRtnVal) return ;
			//레코드추출
			rsResult.first();
			recPara = rsResult.getRecord();
			//작업예약ID
			szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");
			
			//INSERT 항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			//야드구분
			String szYD_GP       = szYD_SCH_CD.substring(0, 1);
			//동구분
			String szYD_BAY_GP   = szYD_SCH_CD.substring(1,2);
			

			
			//INSERT할 항목 SET (더 추가할항목이 있다고함 // 김진욱에게 재확인할것 )
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
			recPara.setField("YD_GP", 		  szYD_GP);
			recPara.setField("YD_BAY_GP", 	  szYD_BAY_GP);
			recPara.setField("YD_SCH_CD", 	  szYD_SCH_CD);
			recPara.setField("REGISTER", 	  szUser);
			//20090325 김진욱 추가
			recPara.setField("YD_AIM_YD_GP",  szYD_AIM_YD_GP);
			recPara.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP);
			recPara.setField("YD_SCH_PRIOR",  szYD_SCH_PRIOR);
			
			//20090330 이현성 추가 
			//To위치 정보  To Guide 위치 및 To 위치 결정방법 'F': 지정위치 추가			
			
			recPara.setField("YD_TO_LOC_GUIDE",  ydDaoUtils.paraRecChkNull(msgRecord,"YD_TO_LOC_GUIDE"));
			if(!"".equals(ydDaoUtils.paraRecChkNull(msgRecord,"YD_TO_LOC_GUIDE")))
				recPara.setField("YD_TO_LOC_DCSN_MTD",  "F");
			
			//계획대차 
			recPara.setField("YD_WRK_PLAN_TCAR",  ydDaoUtils.paraRecChkNull(msgRecord,"YD_WRK_PLAN_TCAR"));
			
			
			
			//작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if(intRtnVal < 1){
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			
			
			//조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
			recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", 	  szUser);

			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
				
				//리턴 recordSet 생성
				rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
				//재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				intRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C", rsResult);
				if(intRtnVal != 1) return ;
				
				//레코드추출
				rsResult.first(); 
				recStkPara = rsResult.getRecord();
				
				//재료번호
				recPara.setField("STL_NO", 		   szSTL_NO[Loop_i]);
				//적치열구분
				recPara.setField("YD_STK_COL_GP",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_COL_GP"));
				//적치BED번호
				recPara.setField("YD_STK_BED_NO",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_BED_NO"));
				//적치단번호
				recPara.setField("YD_STK_LYR_NO",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_LYR_NO"));
				//권상모음순서
				recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ[Loop_i]);
				
				// 작업예약재료 테이블에 등록한다.
				intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
				
				if(intRtnVal < 1){
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
			}
			
			
			// 대차 작업일 경우는 스케줄 기동을 시키지않는다.
			if ("TC".equals(szYD_SCH_CD.substring(2,4))) {
				
				if ("A".equals(szYD_GP)) {
					//작업크레인 정보를 설비에 넣어준다. 
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("JMS_TC_CD","YDYDJ520");
					recPara.setField("YD_EQP_ID", ydDaoUtils.paraRecChkNull(msgRecord,"YD_WRK_PLAN_TCAR"));
					recPara.setField("YD_WBOOK_ID",szYD_WBOOK_ID);	
					ydEjbCon.trx("TransEqpSchSeEJB", "procY1TcarSch", recPara);
				} else if ("D".equals(szYD_GP)) {
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("JMS_TC_CD","YDYDJ522");
					recPara.setField("YD_EQP_ID", ydDaoUtils.paraRecChkNull(msgRecord,"YD_WRK_PLAN_TCAR"));
					recPara.setField("YD_WBOOK_ID",szYD_WBOOK_ID);	
					ydEjbCon.trx("TransEqpSchSeEJB", "procY3TcarSch", recPara);
				}
				
				return ;
			}
			
			
			
			//작업 예약 편성 후 스케줄 기동 
			
			recPara = JDTORecordFactory.getInstance().create();
			
			
			if("A".equals(szYD_GP)){
				//C연주 슬라브 야드 
				recPara.setField("JMS_TC_CD","YDYDJ500");
				
			} else if("D".equals(szYD_GP)){
				//A후판 슬라브야드
				recPara.setField("JMS_TC_CD","YDYDJ503");
				
			} else if("K".equals(szYD_GP)){
				
				recPara.setField("JMS_TC_CD","YDYDJ506");
			} else if("J".equals(szYD_GP)){
				
				recPara.setField("JMS_TC_CD","YDYDJ509");
			}  else if("H".equals(szYD_GP)){
				
				recPara.setField("JMS_TC_CD","YDYDJ509");
			} else if("S".equals(szYD_GP)){
				//통합야드
				recPara.setField("JMS_TC_CD","YDYDJ512");
			}
			
			recPara.setField("YD_SCH_CD", szYD_SCH_CD);
			//작업크레인 정보를 설비에 넣어준다. 
			recPara.setField("YD_EQP_ID",szCrn );
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
			
			ydDelegate.sendMsg(recPara);
			
		
		} catch(Exception e){
			szMsg = "메뉴얼 작업지시 요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}

	
	} // end of ydManualReq()
	
	/**
	 * 오퍼레이션명 : 후판제품이송상차Lot편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return String
	 * @throws JDTOException
	 */
	public String procPlGdsFtmvCarLdLotComp(JDTORecord msgRecord) throws JDTOException  {
		
		YdDelegate ydDelegate = new YdDelegate();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		//공용 METHOD
		YdUtils ydutils       = new YdUtils();
		
		//레코드 선언
		JDTORecord recPara     = null;
		JDTORecord recOutPara  = null;
		JDTORecord recSpecPara  = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		JDTORecordSet rsSpecResult = null;
		
		//리턴값(boolean)
		boolean blnRtnVal      = false;
		//리턴값(int)
		int intRtnVal          = 0;
		//메세지
		String szMsg           = "";
		//메소드명
		String szMethodName    = "procPlGdsFtmvCarLdLotComp";
		String szOperationName = "후판제품이송상차Lot편성";
		//사용자
		String szUser          = "SYSTEM";

		//전문 생성 일시
		String szDate             = null;

		//설비ID
		//String szYD_EQP_ID         = null;
		//차량사용구분
		//String szYD_CAR_USE_GP		= null;
		//운송장비코드
		String szTRN_EQP_CD			= null;
		//개소코드
		String szWLOC_CD			= null;
		//운송작업영공구분코드
		String szSP_TRUCK_LOADING_LOC_TP = null;
		//포인트요구일시
		String szPNT_DMD_DT			= null;
		//발지개소코드
		String szSPOS_WLOC_CD		= null;
		//발지포인트코드
		String szSPOS_YD_PNT_CD		= null;
		//적치열구분
		String szYD_STK_COL_GP		= null;
		String szPRE_YD_STK_COL_GP	= null;
		//야드목표행선구분
		String szYD_AIM_RT_GP		= null;
		//준비스케줄ID
		String szYD_PREP_SCH_ID		= null;
		//목표야드구분
		String szYD_AIM_YD_GP		= null;
		//목표동구분
		String szYD_AIM_BAY_GP		= null;
		
		//스케줄코드
		String szYD_SCH_CD         = null;
		//차량이송재료매수
		int intYD_CARLD_SH         = 0;
		//대상 재료 중량 합계
		long lngSumMtlWt           = 0;
		//야드작업허용중량
		long lngYD_WRK_ALW_WT      = 0;
		//차량작업허용매수
		int intYD_WRK_ALW_SH       = 0;
		//연계모듈을 EJB Call or JMS Call 호출 유무 변수
		String szIS_EJB_CALL = null;
		String szYD_AUTO_LOT = null;
		//리턴값
		String szRtnMsg			= null;
		//
		String szCurDate		= YdUtils.getCurDate("yyyyMMddHHmmss");

		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if(szRcvTcCode == null || szRcvTcCode.equals("")){
		
			szMsg = "["+szOperationName+"] TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_TC_ERROR;
		
		}
		//TC CODE DISPLAY
		if(bDebugFlag){
		
			szMsg="["+szOperationName+"] 전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
			szMsg="["+szOperationName+"] 메소드 시작 - 전문확인";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			ydUtils.displayRecord(szOperationName, msgRecord);
			
			//--------------- 1.받은 전문 확인 -------------------
			//설비ID
//			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").trim();
//			if(szYD_EQP_ID.equals("")){
//				
//				szMsg = "["+szOperationName+"] [전문 이상] 설비ID가 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return YdConstant.RETN_CD_NO_PARAM;
//				
//			}
//			
//			szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_USE_GP").trim();
//			szYD_CAR_USE_GP = "L";
//			if(szYD_CAR_USE_GP.equals("") || !szYD_CAR_USE_GP.equals("L")){
//				
//				szMsg = "["+szOperationName+"] [전문 이상] 차량사용구분(" + szYD_CAR_USE_GP + ")";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return YdConstant.RETN_CD_NO_PARAM;
//				
//			}
//			
//			//운송장비코드
//			szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(msgRecord, "TRN_EQP_CD").trim();
//			if(szTRN_EQP_CD.equals("")){
//				
//				szMsg = "["+szOperationName+"] [전문 이상] 운송장비코드가 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return YdConstant.RETN_CD_NO_PARAM;
//				
//			}
//			
//			//개소코드
//			szWLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord, "WLOC_CD").trim();
//			if(szWLOC_CD.equals("")){
//				
//				szMsg = "[전문 이상] 개소코드가 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return YdConstant.RETN_CD_NO_PARAM;
//				
//			}
//			//운송작업영공구분코드
//			szSP_TRUCK_LOADING_LOC_TP = ydDaoUtils.paraRecChkNull(msgRecord, "SP_TRUCK_LOADING_LOC_TP").trim();
//			if(szSP_TRUCK_LOADING_LOC_TP.equals("")){
//				
//				szMsg = "[전문 이상] 운송작업영공구분코드가 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return YdConstant.RETN_CD_NO_PARAM;
//				
//			}
//			//포인트요구일시
//			szPNT_DMD_DT = ydDaoUtils.paraRecChkNull(msgRecord, "PNT_DMD_DT").trim();
//			if(szPNT_DMD_DT.equals("")){
//				
//				szMsg = "[전문 이상] 포인트요구일시가 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return YdConstant.RETN_CD_NO_PARAM;
//				
//			}
//			//발지개소코드
//			szSPOS_WLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_WLOC_CD").trim();
//			if(szSPOS_WLOC_CD.equals("")){
//				
//				szMsg = "[전문 이상] 발지개소코드가 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return YdConstant.RETN_CD_NO_PARAM;
//				
//			}
//			//발지포인트코드
//			szSPOS_YD_PNT_CD = ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_YD_PNT_CD").trim();
//			if(szSPOS_YD_PNT_CD.equals("")){
//				
//				szMsg = "[전문 이상] 발지포인트코드가 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return YdConstant.RETN_CD_NO_PARAM;
//				
//			}
			
			//운송장비코드
			szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(msgRecord, "TRN_EQP_CD").trim();
			if(szTRN_EQP_CD.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 운송장비코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			
			//발지개소코드
			szSPOS_WLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_WLOC_CD").trim();
			if(szSPOS_WLOC_CD.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 발지개소코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			
			//연계모듈을 EJB Call[Y값으로 설정] or JMS Call[값이 없거나 N값으로 설정] 호출 유무 변수  - 지금은 화면으로부터 전송
			szIS_EJB_CALL = ydDaoUtils.paraRecChkNull(msgRecord, "IS_EJB_CALL");
			//szYD_AUTO_LOT = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AUTO_LOT");
			//szMsg="["+szOperationName+"] szIS_EJB_CALL = " + szIS_EJB_CALL + ", 자동LOT편성 유무["+szYD_AUTO_LOT+"]";
			//ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			//if( szYD_AUTO_LOT.equals("Y") ) {
				//----------------- 2. 상차 Lot 편성 대상 재료 Select -----------------
				//intRtnVal = chkGetPLGdsFtMvCarLoadLotGp(msgRecord, rsResult);
				intRtnVal = chkGetPLGdsFtMvCarLdLotFromPrepSch(msgRecord, rsResult);
				if( intRtnVal == 0 ){
					//소재차량POINT요구 모듈 호출
					szMsg="["+szOperationName+"] 대상재가 존재하지 않으므로 소재차량POINT요구 모듈 호출 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
//					//소재차량정지Point요구 호출
//					//record 생성
//					recPara = JDTORecordFactory.getInstance().create();
//					//JMS TC CODE
//					recPara.setField("JMS_TC_CD",               "YDYDJ630");
//					//운송장비코드
//					recPara.setField("TRN_EQP_CD",              szTRN_EQP_CD);
//					//개소코드
//					recPara.setField("WLOC_CD",                 szWLOC_CD);
//					//운송작업영공구분코드
//					recPara.setField("TRN_WRK_FULLVOID_GP",     "E");
//					//포인트요구일시
//					recPara.setField("PNT_DMD_DT",              szCurDate);
//					
//		    		//YdDelegate ydDelegate = new YdDelegate();
//					//전문 송신
//					ydDelegate.sendMsg(recPara);
					
					YdCommonUtils.callMatlCarArrPntReq(szTRN_EQP_CD, szSPOS_WLOC_CD, "N", this);
					
					szMsg="["+szOperationName+"] 대상재가 존재하지 않으므로 소재차량POINT요구 모듈 호출 성공";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					return YdConstant.RETN_CD_SUCCESS;
				}else if( intRtnVal < 0) {
					return YdConstant.RETN_CD_FAILURE;
				}
			//}
				
			rsSpecResult = JDTORecordFactory.getInstance().createRecordSet("");
			//----------------- 3. 차량사양 Select ------------------
			szRtnMsg = YdCommonUtils.chkGetCarSpec(szTRN_EQP_CD, rsSpecResult);
			if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS)) {
				szMsg = "["+szOperationName+"] 운송장비코드(" + szTRN_EQP_CD + ")에 대한 차량사양이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}
			
//			if(!blnRtnVal){
//				return YdConstant.RETN_CD_FAILURE;
//			}
			
			//레코드 추출
			rsSpecResult.first();
			recSpecPara = rsSpecResult.getRecord();
			
			//차량작업허용매수
			//intYD_WRK_ALW_SH = ydDaoUtils.paraRecChkNullInt(recSpecPara, "YD_WRK_ALW_SH");
			
			//이송 재료 매수가 차량 이송 가능 매수보다 작으면 이송 재료 매수를 차량 이송 가능 매수에 대입한다.
			//if(intYD_WRK_ALW_SH > rsResult.size()) intYD_WRK_ALW_SH = rsResult.size();
			
			intYD_WRK_ALW_SH = rsResult.size();
			
			//차량작업허용중량
			lngYD_WRK_ALW_WT = ydDaoUtils.paraRecChkNullLong(recSpecPara, "YD_WRK_ALW_WT");
			
			szMsg="["+szOperationName+"] 이송재료매수[" + intYD_WRK_ALW_SH + "], 차량작업허용중량["+lngYD_WRK_ALW_WT+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			
			//레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			//JMS TC CODE
			recOutPara.setField("JMS_TC_CD",          "YDYDJ247");
			//첫 레코드로 커서이동
			//rsResult.first();
			
			//대상 소재 매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			/*for (int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++){
				
				//레코드 추출
				recPara = rsResult.getRecord();
				//대상 재료 중량 합계
				lngSumMtlWt = lngSumMtlWt + ydDaoUtils.paraRecChkNullLong(recPara, "YD_MTL_WT");
				//차량작업허용중량보다 대상재료들의 중량이 많으면 편성 중지
				if(lngYD_WRK_ALW_WT < lngSumMtlWt) break;
				//재료번호
				recOutPara.setField("STL_NO" + Loop_i, ydDaoUtils.paraRecChkNull(recPara, "STL_NO"));
				//권상모음순서
				recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, "" + Loop_i);
				//차량이송재료매수
				intYD_CARLD_SH = Loop_i;
				
				//다음 레코드로 커서 이동
				rsResult.next();

			}*/
			
			//--------------------- 4. 차량 이송 가능 매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= intYD_WRK_ALW_SH; Loop_i++){
				rsResult.absolute(Loop_i);
				recPara = rsResult.getRecord();
				//동이 같은 지를 확인
				szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP").trim();
				if( Loop_i == 1 ){
					szPRE_YD_STK_COL_GP = szYD_STK_COL_GP;
					szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_RT_GP").trim();
					szYD_SCH_CD  = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_CD");
					szYD_PREP_SCH_ID  = ydDaoUtils.paraRecChkNull(recPara, "YD_PREP_SCH_ID");
					szYD_AIM_YD_GP  = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_YD_GP");
					szYD_AIM_BAY_GP  = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_BAY_GP");
				}else{
					if( !szPRE_YD_STK_COL_GP.substring(0, 2).equals(szYD_STK_COL_GP.substring(0, 2)) ){
						szMsg = "["+szOperationName+"] 동이 다르므로 대상재 추가 루프 break";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						break;
					}
				}
				//대상 재료 중량 합계
				lngSumMtlWt = lngSumMtlWt + ydDaoUtils.paraRecChkNullLong(recPara, "YD_MTL_WT");
				//차량작업허용중량보다 대상재료들의 중량이 많으면 편성 중지
				if(lngYD_WRK_ALW_WT < lngSumMtlWt) break;
				//재료번호
				recOutPara.setField("STL_NO" + Loop_i, ydDaoUtils.paraRecChkNull(recPara, "STL_NO"));
				//권상모음순서
				recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, "" + Loop_i);
				//차량이송재료매수
				intYD_CARLD_SH = Loop_i;
				
			}
			
			if( intYD_WRK_ALW_SH == 0 ) {
				szYD_AIM_RT_GP = "@@";
				intYD_CARLD_SH = 1;
				recOutPara.setField("STL_NO1", "DUMMY");
				recOutPara.setField("YD_UP_COLL_SEQ1", "" + 1);
				szYD_STK_COL_GP = "@@PT01";
			}
			
			//=================================================================================
			//수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			//BRE 등록 안됨...테스트용 스케줄코드 생성
			//추후 구현..
			//szYD_SCH_CD  = szYD_STK_COL_GP.substring(0, 2) + "PT01UM";
			//=================================================================================
			
			
			
			//전문 발생 일시
//			szDate = ydUtils.getCurDate("yyyy/MM/dd HH:mm:ss");
//
//			//발생 일시
//			recOutPara.setField("JMS_TC_CREATE_DDTT", szDate);

			
			//야드목표행선구분
			//recOutPara.setField("YD_AIM_RT_GP", szYD_AIM_RT_GP);
			//스케줄코드
			recOutPara.setField("YD_SCH_CD",     szYD_SCH_CD);
			//상차 LOT 재료매수
			recOutPara.setField("YD_CARLD_SH",   "" + intYD_CARLD_SH);
			//설비ID
			//recOutPara.setField("YD_EQP_ID",     szYD_EQP_ID);
			//차량사용구분
			//recOutPara.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
			//운송장비코드
			recOutPara.setField("TRN_EQP_CD",    szTRN_EQP_CD);
			//준비스케줄ID
			recOutPara.setField("YD_PREP_SCH_ID",    szYD_PREP_SCH_ID);
			//목표야드구분
			recOutPara.setField("YD_AIM_YD_GP",    szYD_AIM_YD_GP);
			//목표동구분
			recOutPara.setField("YD_AIM_BAY_GP",    szYD_AIM_BAY_GP);
			//개소코드
			//recOutPara.setField("WLOC_CD",       szWLOC_CD);
			//운송작업영공구분코드
			//recOutPara.setField("SP_TRUCK_LOADING_LOC_TP", szSP_TRUCK_LOADING_LOC_TP);
			//포인트요구일시
			//recOutPara.setField("PNT_DMD_DT",    szPNT_DMD_DT);
			//발지개소코드
			//recOutPara.setField("SPOS_WLOC_CD",  szSPOS_WLOC_CD);
			//발지포인트코드
			//recOutPara.setField("SPOS_YD_PNT_CD", szSPOS_YD_PNT_CD);
			
			//recOutPara.setField("IS_EJB_CALL", szIS_EJB_CALL);
			
			//recOutPara.setField("CAR_SCH_ONLY", (intYD_WRK_ALW_SH == 0 ? "Y" : "N"));
			
			//전문 로그 출력
			ydUtils.displayRecord(szOperationName, recOutPara);
			
			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			 * EJB Call or JMS Call 유무를 판단하여 호출하는 기능
			 * 수정자 : 임춘수
			 * 일자 : 2009.07.13
			 ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			if( szIS_EJB_CALL.equals("Y")){
				//EJB Call ==> 메소드 콜
				szMsg = "["+szOperationName+"] 차량상차 작업요구 송신 - 메소드[procY4CarLdWrkReq] 콜 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
				szRtnMsg = procY4CarLdWrkReq(recOutPara);
					
				szMsg = "["+szOperationName+"] 차량상차 작업요구 송신 - 메소드[procY4CarLdWrkReq] 콜 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}else{
				//전문 송신
				ydDelegate.sendMsg(recOutPara);
				szMsg = "["+szOperationName+"] 차량상차 작업요구 송신[procY4CarLdWrkReq] - 전문 송신 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			szMsg = "["+szOperationName+"] 차량상차 작업요구 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch(Exception e){
			szMsg = "["+szOperationName+"] Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;
		}
		return YdConstant.RETN_CD_SUCCESS;
	} //end of procPlGdsFtmvCarLdLotComp
	
	/**
	 * 오퍼레이션명 : 후판제품이송상차Lot편성 데이터유무체크 및 데이터반환
	 *  
	 * @param  JDTORecord    recInPara 파라미터 레코드
	 *         JDTORecordSet rsResult  결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public int chkGetPLGdsFtMvCarLoadLotGp(JDTORecord recInPara, JDTORecordSet rsResult)throws JDTOException  {
		
		//저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		//리턴값(boolean)
		boolean blnRtnVal     = false;
		//리턴값(int)
		int intRtnVal         = 0;
		//메소드명
		String szMethodName   = "chkGetPLGdsFtMvCarLoadLotGp";
		String szMsg          = null;
		
		//레코드 선언
		JDTORecord recPara        = null;	

		try {
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();

			//재료품목
			//recPara.setField("YD_MTL_ITEM",   ydDaoUtils.paraRecChkNull(recInPara, "YD_MTL_ITEM"));
			//목표행선구분
			//recPara.setField("YD_AIM_RT_GP",  ydDaoUtils.paraRecChkNull(recInPara, "YD_AIM_RT_GP"));
			//목표야드구분
			//recPara.setField("YD_AIM_YD_GP",  ydDaoUtils.paraRecChkNull(recInPara, "YD_AIM_YD_GP"));
			//목표동구분
			//recPara.setField("YD_AIM_BAY_GP", ydDaoUtils.paraRecChkNull(recInPara, "YD_AIM_BAY_GP"));
			//FROM 야드동
			//recPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(recInPara, "YD_CARLD_STOP_LOC").substring(0, 2));
			recPara.setField("YD_GP",  "K");
			//1. 목표행선 : KD - HYSCO이송대기
			recPara.setField("YD_AIM_RT_GP",  "KD");
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 50);
			if(intRtnVal >= 1) return 100;
			//2. 목표행선 : KC - 임가공이송대기
			recPara.setField("YD_AIM_RT_GP",  "KC");
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 50);
			if(intRtnVal >= 1) return 200;
			//3. 목표행선 : KB - 고간이송대기
			recPara.setField("YD_AIM_RT_GP",  "KB");
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 50);
			if(intRtnVal >= 1) return 300;
			//4. 목표행선 : KG - 반납대기
			recPara.setField("YD_AIM_RT_GP",  "KG");
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 50);
			if(intRtnVal >= 1) return 400;
			//저장품 테이블 조회
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 50);
			
			//리턴값 메세지처리
			if(intRtnVal >= 1){

				intRtnVal = 500;
				
			} else if(intRtnVal == 0){
				
				szMsg = "후판 제품이송 상차 Lot 편성 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				intRtnVal =  0;
				
			} else if(intRtnVal == -2){
				
				szMsg = "후판 제품이송 상차 Lot 편성 데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				intRtnVal =  -2;
				
			} else {
				
				szMsg = "후판 제품이송 상차 Lot 편성 데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
				
			}
		} catch(Exception e){
			szMsg = "후판 제품이송 상차 Lot 편성  데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			intRtnVal = -100;
		}
		return intRtnVal;
	} //end of chkGetPLGdsFtMvCarLoadLotGp
	
	/**
	 * 오퍼레이션명 : 후판제품이송LOT반환
	 *  
	 * @param  JDTORecord    recInPara 파라미터 레코드
	 *         JDTORecordSet rsResult  결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public int chkGetPLGdsFtMvCarLdLotFromPrepSch(JDTORecord recInPara, JDTORecordSet rsResult)throws JDTOException  {
		
		//저장품 DAO
		YdStockDao ydStockDao 	= new YdStockDao();
		//리턴값(boolean)
		boolean blnRtnVal     	= false;
		//리턴값(int)
		int intRtnVal         	= 0;
		//메소드명
		String szMethodName   	= "chkGetPLGdsFtMvCarLdLotFromPrepSch";
		String szOperationName	= "후판제품이송LOT반환";
		String szMsg          	= null;
		
		//레코드 선언
		JDTORecord recPara     = null;
		
		String szTRN_EQP_CD		= null;
		String szSPOS_WLOC_CD	= null;

		try {
			szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(recInPara, "TRN_EQP_CD");
			//발지개소코드
			szSPOS_WLOC_CD = ydDaoUtils.paraRecChkNull(recInPara, "SPOS_WLOC_CD"); //-2013.01.21 추가 (3기)
			
			
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();

			szMsg = "["+szOperationName+"] 운송장비코드["+szTRN_EQP_CD+"]로 가능한 빠른 준비스케줄 조회 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			recPara  = JDTORecordFactory.getInstance().create();
			if(YdConstant.WLOC_CD_PLATE2_GDS_YARD.equals(szSPOS_WLOC_CD)) {  //-2013.01.21 추가 (3기)
				//2후판제품출하야드
				recPara.setField("YD_GP", 			YdConstant.YD_GP_PLATE2_GDS_YARD);
				recPara.setField("YD_SCH_CD", 		YdConstant.YD_GP_PLATE2_GDS_YARD + "_PT");
			} else {
				//1후판제품출하야드
				recPara.setField("YD_GP", 			YdConstant.YD_GP_PLATE_GDS_YARD);
				recPara.setField("YD_SCH_CD", 		YdConstant.YD_GP_PLATE_GDS_YARD + "_PT");
			}
			recPara.setField("YD_WRK_PLAN_CRN", "");
			recPara.setField("YD_PREP_WK_ST", 	YdConstant.PREP_WK_CAR_LD);
			//항목추가
			recPara.setField("CAR_GP", 			szTRN_EQP_CD.substring(1, 2));
			//저장품 테이블 조회
			//intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 143);
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 151);
			if( intRtnVal > 0 ) {
				szMsg = "["+szOperationName+"] 운송장비코드["+szTRN_EQP_CD+"]로 가능한 빠른 준비스케줄 존재합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}else{
				szMsg = "["+szOperationName+"] 운송장비코드["+szTRN_EQP_CD+"]로 가능한 빠른 준비스케줄 존재하지 않습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			//리턴값 메세지처리
			if(intRtnVal >= 1){

				szMsg = "["+szOperationName+"] 후판 제품이송 상차 Lot 편성 데이터가 존재합니다. - 대상재건수["+intRtnVal+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
			} else if(intRtnVal == 0){
				
				szMsg = "["+szOperationName+"] 후판 제품이송 상차 Lot 편성 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//intRtnVal =  0;
				
			} else if(intRtnVal == -2){
				
				szMsg = "["+szOperationName+"] 후판 제품이송 상차 Lot 편성 데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				//intRtnVal =  -2;
				
			} else {
				
				szMsg = "["+szOperationName+"] 후판 제품이송 상차 Lot 편성 데이터 조회중 오류 발생 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
				
			}
		} catch(Exception e){
			szMsg = "["+szOperationName+"] 후판 제품이송 상차 Lot 편성  데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			intRtnVal = -100;
		}
		return intRtnVal;
	} //end of chkGetPLGdsFtMvCarLdLotFromPrepSch
	
	
	/**
	 * 오퍼레이션명 : 후판제품차량상차작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String procY4CarLdWrkReq(JDTORecord msgRecord)throws JDTOException  {
		
		YdDelegate ydDelegate = new YdDelegate();
		//스케줄기준 DAO
		YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();
		//작업예약 재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils           = new YdDaoUtils();
		//공용 METHOD
		YdUtils ydutils                 = new YdUtils();
		
		//리턴값(boolean)
		boolean blnRtnVal      = false;
		//리턴값(int)
		int intRtnVal          = 0;
		//메세지
		String szMsg           = "";
		//METHOD명
		String szMethodName    		= "procY4CarLdWrkReq";
		String szOperationName		= "후판제품차량상차작업요구";
		//사용자
		String szUser          		= "SYSTEM";
		
		//레코드 선언
		JDTORecord recPara     = null;
		JDTORecord recStkPara  = null;
		JDTORecord recTemp     = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		
		//설비ID
		String szYD_EQP_ID         = null;
		//목표행선구분
		String szYD_AIM_RT_GP      = null;
		//차량사용구분
		String szYD_CAR_USE_GP     = null;
		//운송장비코드
		String szTRN_EQP_CD        = null;
		//재료매수(int)
		int intMtlCnt              = 0;
		//재료번호
		String[] szSTL_NO          = null;
		//권상모음순서
		String[] szYD_UP_COLL_SEQ  = null;
		//스케줄코드
		String szYD_SCH_CD         = null;
		//야드작업크레인우선순위
		String szYD_WRK_CRN_PRIOR  = null;
		//스케줄 금지 유무
		String szYD_SCH_PROH_EXN   = null;
		//작업크레인
		String szYD_WRK_CRN        = null;
		//대체크레인유무
		String szYD_ALT_CRN_YN     = null;
		//대체크레인
		String szYD_ALT_CRN        = null;
		//선택크레인
		String szCrn               = null;
		//작업예약ID
		String szYD_WBOOK_ID       = null;
		//개소코드
		String szWLOC_CD           = null;
		//운송작업영공구분코드
		String szSP_TRUCK_LOADING_LOC_TP = null;
		//포인트요구일시
		String szPNT_DMD_DT          = null;
		//발지개소코드
		String szSPOS_WLOC_CD      = null;
		//발지포인트코드
		String szSPOS_YD_PNT_CD    = null;
		//차량스케줄만 생성할 지 판단하는 변수 정의
		String szCAR_SCH_ONLY = null;
		//준비스케줄ID
		String szYD_PREP_SCH_ID		= null;
		
		//야드목표야드구분
		String szYD_AIM_YD_GP        = null;
		//야드목표동구분
		String szYD_AIM_BAY_GP       = null;
		
		String szCurDate		= YdUtils.getCurDate("yyyyMMddHHmmss");
		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		
		//에러 리턴
		if(szRcvTcCode == null || szRcvTcCode.equals("")){
		
			szMsg = "["+szOperationName+"] TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_TC_ERROR;
		
		}
		//TC CODE DISPLAY
		if(bDebugFlag){
		
			szMsg = "["+szOperationName+"] 전문수신 : TCCODE=" + szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
		
			//받은 전문 편집
			//스케줄코드
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			if(szYD_SCH_CD.equals("")){
				
				szMsg = "["+szOperationName+"] [전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			//설비ID
//			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
//			if(szYD_EQP_ID.equals("")){
//				
//				szMsg = "[전문 이상] 설비ID가 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return YdConstant.RETN_CD_NO_PARAM;
//				
//			}
			//목표행선구분
//			szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_RT_GP");
//			if(szYD_AIM_RT_GP.equals("")){
//				
//				szMsg = "[전문 이상] 목표행선구분이 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return YdConstant.RETN_CD_NO_PARAM;
//			}
//			//차량사용구분
//			szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_USE_GP");
//			if(szYD_CAR_USE_GP.equals("")){
//				
//				szMsg = "[전문 이상] 차량사용구분이 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return YdConstant.RETN_CD_NO_PARAM;
//				
//			}
			
			//if(szYD_CAR_USE_GP.equals("L")){
				//운송장비코드
				szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(msgRecord, "TRN_EQP_CD");
				if(szTRN_EQP_CD.equals("")){
					
					szMsg = "["+szOperationName+"] [전문 이상] 운송장비코드가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;
				}
				
				//개소코드
//				szWLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord, "WLOC_CD");
//				if(szWLOC_CD.equals("")){
//					
//					szMsg = "[전문 이상] 개소코드가 없습니다.";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//					return YdConstant.RETN_CD_NO_PARAM;
//					
//				}
//				
//				//운송작업영공구분코드
//				szSP_TRUCK_LOADING_LOC_TP = ydDaoUtils.paraRecChkNull(msgRecord, "SP_TRUCK_LOADING_LOC_TP");
//				if(szSP_TRUCK_LOADING_LOC_TP.equals("")){
//					
//					szMsg = "[전문 이상] 운송작업영공구분코드가 없습니다.";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//					return YdConstant.RETN_CD_NO_PARAM;
//					
//				}
				
				//포인트요구일시
//				szPNT_DMD_DT = ydDaoUtils.paraRecChkNull(msgRecord, "PNT_DMD_DT");
//				if(szPNT_DMD_DT.equals("")){
//					
//					szMsg = "[전문 이상] 포인트요구일시가 없습니다.";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//					return YdConstant.RETN_CD_NO_PARAM;
//					
//				}
				
			//}


			//발지개소코드
//			szSPOS_WLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_WLOC_CD");
//			if(szSPOS_WLOC_CD.equals("")){
//				
//				szMsg = "[전문 이상] 발지개소코드가 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return YdConstant.RETN_CD_NO_PARAM;
//				
//			}
//			
//			//발지포인트코드
//			szSPOS_YD_PNT_CD = ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_YD_PNT_CD");
//			if(szSPOS_YD_PNT_CD.equals("")){
//				
//				szMsg = "[전문 이상] 발지포인트코드가 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return YdConstant.RETN_CD_NO_PARAM;
//				
//			}
				
			szYD_PREP_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_PREP_SCH_ID");
			szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_YD_GP");
			szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_BAY_GP");
			
			//재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_CARLD_SH");
			if(intMtlCnt == 0){
				
				szMsg = "["+szOperationName+"] [전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			
			//szCAR_SCH_ONLY = ydDaoUtils.paraRecChkNull(msgRecord, "CAR_SCH_ONLY");

			//재료번호
			szSTL_NO          = new String[intMtlCnt + 1];
			//권상모음순서
			szYD_UP_COLL_SEQ  = new String[intMtlCnt + 1];
			
			
			//재료번호, 권상모음순서
			for(int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
				
				//재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO" + Loop_i);
				if(szSTL_NO[Loop_i].equals("")){
					
					szMsg = "["+szOperationName+"] [전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;
					
				}
				//권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_COLL_SEQ" + Loop_i);
				if(szYD_UP_COLL_SEQ[Loop_i].equals("")){
					
					szMsg = "["+szOperationName+"] [전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i] + ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;
					
				}
			}
			//if( !szCAR_SCH_ONLY.equals("Y")) {
				//리턴 recordSet 생성
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	
				//스케줄 기준 체크
				blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
				if(!blnRtnVal) return YdConstant.RETN_CD_FAILURE;
				
				//레코드 추출
				rsResult.first();
				recPara = rsResult.getRecord();
				
				//스케줄CD 체크
				//스케줄 금지 유무
				szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");
				//작업크레인
				szYD_WRK_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
				//대체크레인유무
				szYD_ALT_CRN_YN   = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
				//대체크레인
				szYD_ALT_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
	
				//야드작업크레인우선순위
				szYD_WRK_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN_PRIOR");
				
				//스케줄 금지 유무가  "Y"이면 처리를 중지하고 유스케이스를 종료한다.
				if(szYD_SCH_PROH_EXN.equals("Y")){
					
					szMsg = "["+szOperationName+"] 스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					//return YdConstant.RETN_CD_FAILURE;
				}
				
				//작업크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);
				
				//작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
				if(!blnRtnVal){
					
					szMsg = "["+szOperationName+"] 작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					
					//대체크레인의 유무를 체크한다.
					//대체크레인이 없으면 에러 리턴
					if(!szYD_ALT_CRN_YN.equals("Y")){
						
						szMsg = "["+szOperationName+"] 대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return YdConstant.RETN_CD_FAILURE;
						
					}
					//대체크레인이 있으면 대체크레인 설비 상태 체크
					blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
					//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
					if(!blnRtnVal){
						
						szMsg = "["+szOperationName+"] 대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return YdConstant.RETN_CD_NO_PARAM;
						
					} else {
						//대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
						szCrn = szYD_ALT_CRN;
						
					}
				} else {
					//작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
					szCrn = szYD_WRK_CRN;
					
				}

			
				//재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다.
				//작업예약재료 등록 여부를 체크한다.
				for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
					
					//크레인사양과 저장품 사양을 체크(길이,폭,중량)
					blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
					if(!blnRtnVal) return YdConstant.RETN_CD_FAILURE;
					
					//다른 작업예약에 재료가 등록되어있는지 체크한다. - 주작업으로 등록되어 있는 지를 판단
					blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
					if(!blnRtnVal) return YdConstant.RETN_CD_FAILURE;
					
				}	
				
				//리턴 recordSet 생성
				rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
				
				//작업예약ID 생성
				blnRtnVal = getYdWbookId(rsResult);
				if(!blnRtnVal) return YdConstant.RETN_CD_FAILURE;
				//레코드추출
				rsResult.first();
				recPara = rsResult.getRecord();
				//작업예약ID
				szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");
				
				//리턴 recordSet 생성
//				rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
//				//저장품테이블 조회
//				blnRtnVal = this.chkGetStock(szSTL_NO[1], rsResult);
//				if(!blnRtnVal) return YdConstant.RETN_CD_FAILURE;
//				//레코드추출
//				rsResult.first();
//				recPara = rsResult.getRecord();
//				
//				//야드목표야드구분
//				szYD_AIM_YD_GP  = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_YD_GP");
//				//야드목표동구분
//				szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_BAY_GP");
				
				//INSERT 항목 record 생성
				recPara = JDTORecordFactory.getInstance().create();
				//야드구분
				String szYD_GP       = szYD_SCH_CD.substring(0, 1);
				//동구분
				String szYD_BAY_GP   = szYD_SCH_CD.substring(1, 2);
				
				//INSERT할 항목 SET
				recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
				recPara.setField("YD_GP", 		  szYD_GP);
				recPara.setField("YD_BAY_GP", 	  szYD_BAY_GP);
				recPara.setField("YD_SCH_CD", 	  szYD_SCH_CD);
				recPara.setField("REGISTER", 	  szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
				recPara.setField("YD_CAR_USE_GP", YdConstant.YD_CAR_USE_GP_TS);
				recPara.setField("TRN_EQP_CD", 	  szTRN_EQP_CD);
				recPara.setField("YD_AIM_YD_GP",  szYD_AIM_YD_GP);
				recPara.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP);
		
				//작업예약 INSERT
				intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
				if(intRtnVal < 1){
					szMsg = "["+szOperationName+"] 작업예약 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;
				}
				
				
				//조회항목 record 생성
				recPara = JDTORecordFactory.getInstance().create();
				
				recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
	//			recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
	//			recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
				recPara.setField("REGISTER", 	  szUser);
	
				for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
					
					//리턴 recordSet 생성
					rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
					//재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
					intRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C", rsResult);
					//if(intRtnVal != 1) return YdConstant.RETN_CD_FAILURE;
					
					if(intRtnVal != 1) {
						if( intRtnVal == 0 ) {
							intRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "U", rsResult);			//보조작업으로 권상대기 인 경우
							if(intRtnVal != 1) {
								szMsg="["+szOperationName+"] 적치단에 재료["+szSTL_NO[Loop_i]+"]가 존재하지 않습니다.";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
								throw new DAOException(szMsg);
							}
						}else{
							szMsg="["+szOperationName+"] 적치단에 재료["+szSTL_NO[Loop_i]+"] 조회 시 오류발생";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							throw new DAOException(szMsg);
						}
						//return YdConstant.RETN_CD_FAILURE;
					}
					
					//레코드추출
					rsResult.first();
					recStkPara = rsResult.getRecord();
					
					//재료번호
					recPara.setField("STL_NO", 		   szSTL_NO[Loop_i]);
					//적치열구분
					recPara.setField("YD_STK_COL_GP",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_COL_GP"));
					//적치BED번호
					recPara.setField("YD_STK_BED_NO",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_BED_NO"));
					//적치단번호
					recPara.setField("YD_STK_LYR_NO",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_LYR_NO"));
					//권상모음순서
					recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ[Loop_i]);
					
					// 작업예약재료 테이블에 등록한다.
					intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
					
					if(intRtnVal < 1){
						szMsg = "["+szOperationName+"] 작업예약재료 데이터 등록 중 에러";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						//return YdConstant.RETN_CD_FAILURE;
						throw new DAOException(szMsg);
					}
				}
			//}
				
				
			if( !szYD_PREP_SCH_ID.equals("") ) {
				/*
				 * 업무기준 : 통합야드인 경우 차량이송준비스케줄ID가 파라미터로 전달되는 경우에는 삭제처리 필요
				 * 업무기준 변경 : 차량이송준비스케줄 실제 삭제처리 대신 DEL_YN항목에 Y 설정
				 * 				작업예약 삭제 시 다시 차량이송준비스케줄의 DEL_YN항목에 N 설정되도록 처리
				 * 수정자 : 임춘수
				 * 수정일 : 2009.10.14
				 */
				szMsg="["+szOperationName+"] 작업예약 등록 후 차량이송준비스케줄["+szYD_PREP_SCH_ID+"] 삭제 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				String szReturnMsg = YdCommonUtils.deletePreSch(szYD_PREP_SCH_ID, szYD_WBOOK_ID, szMethodName);
				
				szMsg="["+szOperationName+"] 작업예약 등록 후 차량이송준비스케줄["+szYD_PREP_SCH_ID+"] 삭제, 작업예약ID["+szYD_WBOOK_ID+"] 등록 성공 : " + szReturnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			String szRtnMsg = YdCommonUtils.getCarSchByTrnEqpCd(szTRN_EQP_CD, rsResult);
			
			if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				szMsg = "["+szOperationName+"] 해당 운송장비코드로 차량스케줄 조회시 오류발생";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException(szMsg);
			}
			
			rsResult.first();
			recPara = rsResult.getRecord();
			
			szSPOS_WLOC_CD = ydDaoUtils.paraRecChkNull(recPara, "SPOS_WLOC_CD");
			
			recTemp = JDTORecordFactory.getInstance().create();
			recTemp.setField("YD_CAR_SCH_ID",   ydDaoUtils.paraRecChkNull(recPara, "YD_CAR_SCH_ID"));
			recTemp.setField("YD_CARLD_WRK_BOOK_ID",   szYD_WBOOK_ID);
			
			YdCarSchDao ydCarSchDao		= new YdCarSchDao();
			ydCarSchDao.updYdCarsch(recTemp, 0);
			
			szMsg = "["+szOperationName+"] 소재차량도착Point요구 호출 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//소재차량정지Point요구 호출
			//record 생성
//			recPara = JDTORecordFactory.getInstance().create();
//			//JMS TC CODE
//			recPara.setField("JMS_TC_CD",               "YDYDJ630");
//			//운송장비코드
//			recPara.setField("TRN_EQP_CD",              szTRN_EQP_CD);
//			//개소코드
//			//recPara.setField("WLOC_CD",                 szWLOC_CD);
//			//운송작업영공구분코드
//			recPara.setField("TRN_WRK_FULLVOID_GP",     "E");
//			//포인트요구일시
//			recPara.setField("PNT_DMD_DT",              szCurDate);
//			
//    		//YdDelegate ydDelegate = new YdDelegate();
//			//전문 송신
//			//ydDelegate.sendMsg(recPara);
//			//EJB CALL로 변경 필요
//    		EJBConnector ejbConn = null;
//			
//			ejbConn = new EJBConnector("default", this);
//			ejbConn.trx("CarMvHdSeEJB", "procMatlCarArrPntReq", recPara);
//			
//			szMsg = "["+szOperationName+"] 소재차량도착Point요구 호출 성공";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			YdCommonUtils.callMatlCarArrPntReq(szTRN_EQP_CD, szSPOS_WLOC_CD, "Y", this);
			
			//record 생성
//			recPara = JDTORecordFactory.getInstance().create();
//			//설비ID
//			recPara.setField("YD_EQP_ID",    szYD_EQP_ID);
//			//차량사용구분
//			recPara.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
//			//운송장비코드
//			recPara.setField("TRN_EQP_CD",    szTRN_EQP_CD);
//			//개소코드
//			recPara.setField("WLOC_CD",       szWLOC_CD);
//			//운송작업영공구분코드
//			recPara.setField("SP_TRUCK_LOADING_LOC_TP", szSP_TRUCK_LOADING_LOC_TP);
//			//포인트요구일시
//			recPara.setField("PNT_DMD_DT",    szPNT_DMD_DT);
//			//발지개소코드
//			recPara.setField("SPOS_WLOC_CD",  szSPOS_WLOC_CD);
//			//발지포인트코드
//			recPara.setField("SPOS_YD_PNT_CD", szSPOS_YD_PNT_CD);
//			recPara.setField("YD_DIRECT_CARLD_GP", "B");
			//스케줄코드
			/*
			 recPara = JDTORecordFactory.getInstance().create();
			//설비ID - 차량설비ID
			recPara.setField("YD_EQP_ID",    szYD_EQP_ID);
			//차량사용구분
			recPara.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
			//운송장비코드
			recPara.setField("TRN_EQP_CD",    szTRN_EQP_CD);
			//개소코드
			recPara.setField("WLOC_CD",       szWLOC_CD);
			//운송작업영공구분코드
			recPara.setField("SP_TRUCK_LOADING_LOC_TP", szSP_TRUCK_LOADING_LOC_TP);
			//포인트요구일시
			recPara.setField("PNT_DMD_DT",    szPNT_DMD_DT);
			//발지개소코드
			recPara.setField("SPOS_WLOC_CD",  szSPOS_WLOC_CD);
			//발지포인트코드
			recPara.setField("SPOS_YD_PNT_CD", szSPOS_YD_PNT_CD);
			//직상차 구분 - 임춘수 2009.04.23 추가
			recPara.setField("YD_DIRECT_CARLD_GP", szYD_DIRECT_CARLD_GP);
			//이송적치열 - 임춘수 2009.04.23 추가
			recPara.setField("YD_FTMV_COL", szYD_FTMV_COL);
			//상차작업예약ID 20090617.김진욱
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
			//스케줄코드 20090617.김진욱
			recPara.setField("YD_SCH_CD",   szYD_SCH_CD); 
			 */
			//차량스케줄 메소드 호출
			//intRtnVal = this.mkY1CarSch(recPara);
			
			//if( intRtnVal != 1 ) return YdConstant.RETN_CD_FAILURE;
		}catch(DAOException e) {
			throw e;
		} catch(Exception e){
			szMsg = "["+szOperationName+"] 후판제품 차량상차 작업요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMsg);
			//return YdConstant.RETN_CD_FAILURE;
		}
		return YdConstant.RETN_CD_SUCCESS;
	
	} // end of procY4CarLdWrkReq()
	
	/**
	 * 오퍼레이션명 : 후판제품 차량스케줄 생성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public int mkY4CarSch(JDTORecord msgRecord)throws JDTOException  {
		YdDelegate  ydDelegate  = new YdDelegate();
		YdCarSchDao ydCarSchDao = new YdCarSchDao();
		YdDBAssist  ydDBAssist  = new YdDBAssist();
		YdStkColDao ydStkColDao = new YdStkColDao();
		JDTORecordSet rsResult 		     = null;
		JDTORecordSet rsStkCol  		 = null;
		JDTORecord    recOutTemp		 = null;
		JDTORecord    recInTemp  		 = null;
		
		
	    int intRtnVal 		      		 = 0 ;
	    
	    String szMsg              		 = "";
	    String szMethodName       		 = "mkY4CarSch";
	    
	    String szWLOC_CD          		 = "";
	    String szYD_GP            		 = "";
	    String szQuery            		 = ""; 
	    String szYD_EQP_ID        		 = ""; 
	    String szTRN_EQP_CD       		 = "";
	    String szYD_CAR_USE_GP    		 = "";
	    String szSPOS_WLOC_CD            = "";
	    String szSPOS_YD_PNT_CD          = "";
	    String szYD_CARLD_LEV_LOC        = "";
	    String szPNT_DMD_DT              = "";
	    String szSP_TRUCK_LOADING_LOC_TP = "";
	    String szYD_WBOOK_ID             = "";
	    String szCAR_NO                  = "";
	    String szCARD_NO                 = "";
	    String szYD_CRN_EQP_ID           = "";
	    String szYD_SCH_CD               = "";
	    
	    try{
    		//파라미터 편집
	    	szYD_EQP_ID               = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
	    	szTRN_EQP_CD              = ydDaoUtils.paraRecChkNull(msgRecord, "TRN_EQP_CD"); 
	    	szYD_CAR_USE_GP           = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_USE_GP");
	    	szWLOC_CD                 = ydDaoUtils.paraRecChkNull(msgRecord, "WLOC_CD");
	    	szSPOS_WLOC_CD            = ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_WLOC_CD");
	    	szSPOS_YD_PNT_CD          = ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_YD_PNT_CD");
	    	szSP_TRUCK_LOADING_LOC_TP = ydDaoUtils.paraRecChkNull(msgRecord, "SP_TRUCK_LOADING_LOC_TP");
	    	szPNT_DMD_DT              = ydDaoUtils.paraRecChkNull(msgRecord, "PNT_DMD_DT");
	    	szYD_WBOOK_ID             = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");     
	    	szCAR_NO                  = ydDaoUtils.paraRecChkNull(msgRecord, "CAR_NO");
	    	szCARD_NO                 = ydDaoUtils.paraRecChkNull(msgRecord, "CARD_NO");
	    	
	    	if(szYD_CAR_USE_GP.equals("G")){
	    		szYD_CRN_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_EQP_ID"); 
	    		szYD_SCH_CD     = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
	    	}
	    	
	    	
	    	
	    	//발지개소코드를 변환, 발지개소Point를 변환(출발지 위치)
	    	rsStkCol = JDTORecordFactory.getInstance().createRecordSet("");
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("WLOC_CD",   szSPOS_WLOC_CD);
	    	recInTemp.setField("YD_PNT_CD", szSPOS_YD_PNT_CD);
	    	intRtnVal = ydStkColDao.getYdStkcol(recInTemp, rsStkCol, 4);
			if(intRtnVal <= 0){
				if(intRtnVal == 0){
					szMsg="data not found";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				}else if(intRtnVal == -2){
					szMsg="parameter error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
//				return intRtnVal = -1;
			}
	    	
		    //열구분을 조회(발지위치)
			if(intRtnVal <= 0){
				szYD_CARLD_LEV_LOC = "";
			}else{
				rsStkCol.first();
				recOutTemp = rsStkCol.getRecord();
				szYD_CARLD_LEV_LOC = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_COL_GP");
			}
		    
		    //차량스케줄ID 생성
//	    	szQuery  = " SELECT TO_CHAR(SYSDATE,'YYYYMMDDHH24MI') ||  LPAD(YD_CARSCH_SEQ.nextval,6,'0') AS YD_CAR_SCH_ID ";
//	    	szQuery += "   FROM DUAL ";
//	    	szQuery += "  WHERE '1' = '1' ";
//	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("");
//	    	intRtnVal = ydDBAssist.getData(szQuery, rsResult, null);
//	    	rsResult.absolute(1);
	    	recOutTemp = JDTORecordFactory.getInstance().create();
//	    	recOutTemp.setRecord(rsResult.getRecord());
	    	
	    	//차량스케줄INSERT 항목
	    	recOutTemp.setField("REGISTER",         "SYSTEM");
	    	recOutTemp.setField("DEL_YN",           "N");
	    	recOutTemp.setField("YD_EQP_WRK_STAT",  "U");
	    	recOutTemp.setField("YD_EQP_ID",        szYD_EQP_ID);
	    	recOutTemp.setField("TRN_EQP_CD",       szTRN_EQP_CD);
	    	recOutTemp.setField("YD_CAR_USE_GP",    szYD_CAR_USE_GP);
	    	
	    	if(szYD_CAR_USE_GP.equals("G")) {
	    		recOutTemp.setField("CAR_KIND","TR");
			}else{
				recOutTemp.setField("CAR_KIND","PT");
			}
	    	
	    	recOutTemp.setField("WLOC_CD",          szWLOC_CD);
	    	recOutTemp.setField("YD_CARLD_LEV_LOC", szYD_CARLD_LEV_LOC);
	    	//2009.11.17 임춘수 추가
	    	recOutTemp.setField("YD_BAYIN_WO_SEQ",  YdConstant.YD_BAYIN_WO_SEQ_DEFAULT);
	    	
	    	recOutTemp.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);
	    	recOutTemp.setField("CAR_NO",           szCAR_NO);
	    	recOutTemp.setField("CARD_NO",          szCARD_NO);
	    	
	    	
	    	
	    	//차량스케줄 등록
	    	intRtnVal = ydCarSchDao.insYdCarsch(recOutTemp);
    		if(intRtnVal == -2){
				szMsg="parameter error";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException("<mkY4CarSch> insYdCarsch처리중 parameter error");
    		}
    		
    		if(szYD_CAR_USE_GP.equals("L")){
	    		//후판제품차량정지Point요구 호출
				//record 생성
				recInTemp = JDTORecordFactory.getInstance().create();
				//JMS TC CODE
	    		recInTemp.setField("JMS_TC_CD",               "YDYDJ630");			//차량정지Point요구
				//운송장비코드
	    		recInTemp.setField("TRN_EQP_CD",              szTRN_EQP_CD);
				//개소코드
	    		recInTemp.setField("WLOC_CD",                 szWLOC_CD);
				//운송작업영공구분코드
	    		recInTemp.setField("SP_TRUCK_LOADING_LOC_TP", szSP_TRUCK_LOADING_LOC_TP);
				//포인트요구일시
	    		recInTemp.setField("PNT_DMD_DT",              szPNT_DMD_DT);
	    		
				//전문 송신
				ydDelegate.sendMsg(recInTemp);
    		}else{
    			//후판제품 크레인 스케줄 송신 - 출하차량인 경우 크레인스케쥴 호출
    			recInTemp = JDTORecordFactory.getInstance().create();
				//JMS TC CODE
	    		recInTemp.setField("JMS_TC_CD", "YDYDJ506");
	    		//스케줄코드
	    		recInTemp.setField("YD_SCH_CD", szYD_SCH_CD);
	    		//설비ID
	    		recInTemp.setField("YD_EQP_ID", szYD_CRN_EQP_ID);
	    		
	    		
	    		//전문 송신
				ydDelegate.sendMsg(recInTemp);
    		}
			
		}catch(Exception e){
			
			szMsg="후판제품차량스케줄 생성 중 Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(e);
		}
	
	
		szMsg="후판제품 차량스케줄 생성 ("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		return intRtnVal = 1;
	} //end of mkY4CarSch()
	
	/**
	 * 오퍼레이션명 : 후판제품 반납 대상재 Lot편성 : OFF-LINE R/T사용
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procPlGdsRetnLotComp(JDTORecord msgRecord) throws JDTOException {

		YdDelegate ydDelegate = new YdDelegate();
		// 공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		// 공용 METHOD
		YdUtils ydutils = new YdUtils();

		// 레코드 선언
		JDTORecord recPara = null;
		JDTORecord recOutPara = null;
		JDTORecord recResult = null;
		// 레코드셋 선언
		JDTORecordSet rsResult = null;

		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메세지
		String szMsg = "";
		// 메소드명
		String szMethodName = "procPlGdsRetnLotComp";
		// 사용자
		String szUser = "SYSTEM";
		String szOperationName = "후판제품 반납 대상재 Lot편성 : OFF-LINE R/T사용";

		// Lot 편성 재료 매수
		int intLotGpSh = 0;
		int intYD_LOT_GP_SH = 0;
		// 전문 생성 일시
		String szDate = null;
		
		//야드구분
		String szYD_GP			= null;
		//동구분
		String szYD_BAY_GP		= null;
		//목표행선구분
		String szYD_AIM_RT_GP	= null;
		//파일링 ZONE NO
		String szPILING_ZONE_NO = null;
		// 적치열구분
		String szYD_STK_COL_GP = null;
		// 적치BED번호
		String szYD_STK_BED_NO = null;
		// 저장위치 정보
		String[] arrRT_ZONE_NO = null;
		// 스케줄코드
		String szYD_SCH_CD = null;
		// 재료번호
		String szSTL_NO = null;
		// 적치단 재료상태
		String szYD_STK_LYR_MTL_STAT = null;
		//크레인 작업가능 중량
		long lngYD_WRK_ABLE_WT = 0;
		//야드재료중량
		long lngSumYD_MTL_WT = 0;
		
		// TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		// 에러 리턴
		if(szRcvTcCode == null){

			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error ("
					+ szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;

		}
		// TC CODE DISPLAY
		if(bDebugFlag){

			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		}

		try {
			// 받은 전문 확인
			//파일링 ZONE NO
//			szPILING_ZONE_NO = ydDaoUtils.paraRecChkNull(msgRecord, "PILING_ZONE_NO");
//			if(szPILING_ZONE_NO.equals("")){
//				
//				szMsg = "[전문 이상] 파일링 ZONE NO가 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return ;
//				
//			}
			
			szYD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP");
			if(szYD_GP.equals("")){
				
				szMsg = "[전문 이상] 야드구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}
			
			szYD_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP");
			if(szYD_BAY_GP.equals("")){
				
				szMsg = "[전문 이상] 동구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}
			// 파일링 ZONE NO ==> 저장위치 정보 변환
			//arrRT_ZONE_NO = YdCommonUtils.getY4PilingZoneNo2StrLoc(szPILING_ZONE_NO);
			//야드구분
			//szYD_GP = arrRT_ZONE_NO[0].substring(0, 1);
			//동구분
			//szYD_BAY_GP = arrRT_ZONE_NO[0].substring(1, 2);

			// =================================================================================
			// 수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			// BRE 등록 안됨...테스트용 스케줄코드 생성
			// 추후 구현..
			 //szYD_SCH_CD = arrRT_ZONE_NO[0].substring(0, 4) + "02UM";
			szYD_SCH_CD = szYD_GP + szYD_BAY_GP + "RTRBUM";
			// =================================================================================
			 //스케쥴기준 속성 조회, 크레인 사양 조회
			recResult = JDTORecordFactory.getInstance().create();
			intRtnVal = YdCommonUtils.getCrnInfoByCrnSchRule(szYD_SCH_CD, recResult);
			if( intRtnVal <= 0) return ;
			//야드작업가능중량
			lngYD_WRK_ABLE_WT = ydDaoUtils.paraRecChkNullLong(recResult, "YD_WRK_ABLE_WT");
			
			//목표행선구분 : 후판제품 반납대기
			szYD_AIM_RT_GP = "KG";
			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			// 보급 Lot 편성 대상 재료 Select
			blnRtnVal = chkPlGdsRetnLotGp(szYD_GP, szYD_BAY_GP,	szYD_AIM_RT_GP, rsResult);
			if(!blnRtnVal)	return ;

			// Lot 편성 매수
			intLotGpSh = rsResult.size();

			// 레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			// JMS TC CODE
			//recOutPara.setField("JMS_TC_CD", "YDYDJ261");
			// 레코드 커서 처음으로
			//rsResult.first();

			// 보급 Lot 편성 재료 매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= intLotGpSh; Loop_i++){
				rsResult.absolute(Loop_i);
				// 레코드 추출
				recPara = rsResult.getRecord();
				if(Loop_i == 1){
					szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");
					szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
				}

				// 재료번호
				szSTL_NO = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
				// 재료중량
				lngSumYD_MTL_WT += ydDaoUtils.paraRecChkNullLong(recPara, "YD_MTL_WT");
				
				if( lngSumYD_MTL_WT > lngYD_WRK_ABLE_WT ) break;
				// 적치단재료상태
				//szYD_STK_LYR_MTL_STAT = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_MTL_STAT");

				// 다른 크레인 스케줄에 등록 되어 있는지 체크
				blnRtnVal = chkCrnWrkMtl(szSTL_NO);
				if(!blnRtnVal) return ;

				// 재료번호
				recOutPara.setField("STL_NO" + Loop_i, szSTL_NO);
				// 권상모음순서
				recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, "" + Loop_i);
				intYD_LOT_GP_SH = Loop_i;
			}

			// 전문 발생 일시
			szDate = ydUtils.getCurDate("yyyy/MM/dd HH:mm:ss");

			// 발생 일시
			recOutPara.setField("JMS_TC_CREATE_DDTT", szDate);
			// 스케줄코드
			recOutPara.setField("YD_SCH_CD", szYD_SCH_CD);
			// Lot편성 매수
			recOutPara.setField("YD_LOT_GP_SH", "" + intYD_LOT_GP_SH);
			// 야드구분
			recOutPara.setField("YD_GP", szYD_GP);
			// 동구분
			recOutPara.setField("YD_BAY_GP", szYD_BAY_GP);
			// 목표야드구분
			recOutPara.setField("YD_AIM_YD_GP", szYD_GP);
			// 목표동구분
			recOutPara.setField("YD_AIM_BAY_GP", szYD_BAY_GP);
			
			//전문 로그 출력
			ydUtils.displayRecord(szOperationName, recOutPara);

			// 전문 송신
			//ydDelegate.sendMsg(recOutPara);
			//메소드 호출
			procPlGdsRetnWrkReq(recOutPara);
			szMsg = "후판제품 반납 대상재 Lot편성 후 후판제품 반납 대상재 작업요구 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		} catch (Exception e){
			szMsg = "후판제품 반납 대상재 Lot편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}

	} // end of procPlGdsRetnLotComp
	
	
	/**
	 * 오퍼레이션명 : 후판제품 반납 대상재 Lot편성 데이터 유무체크 및 데이터 반환
	 * @param szYD_GP			: 야드구분
	 * @param szYD_BAY_GP		: 동구분
	 * @param szYD_AIM_RT_GP	: 목표행선구분
	 * @param rsResult			: 결과저장 레코드셋
	 * @return
	 * @throws JDTOException
	 */
	public boolean chkPlGdsRetnLotGp(String szYD_GP, String szYD_BAY_GP, String szYD_AIM_RT_GP, JDTORecordSet rsResult) throws JDTOException {

		// 저장품 DAO
		YdStockDao ydStockDao = new YdStockDao();
		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메소드명
		String szMethodName = "chkPlGdsRetnLotGp";
		String szMsg = null;

		// 레코드 선언
		JDTORecord recPara = null;

		try {
			// 레코드 생성
			recPara = JDTORecordFactory.getInstance().create();

			// 야드구분
			recPara.setField("YD_GP", szYD_GP);
			// 동구분
			recPara.setField("YD_BAY_GP", szYD_BAY_GP);
			// 목표행선구분
			recPara.setField("YD_AIM_RT_GP", szYD_AIM_RT_GP);

			// 저장품 테이블 조회
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 51);

			// 리턴값 메세지처리
			if(intRtnVal >= 1){

				blnRtnVal = true;

			} else if(intRtnVal == 0){

				szMsg = "야드(" + szYD_GP + ")," + "동("
						+ szYD_BAY_GP + ")" + ", 목표행선구분(" + szYD_AIM_RT_GP + ") "
						+ "에 대한 대상재 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else if(intRtnVal == -2){

				szMsg = "야드(" + szYD_GP + ")," + "동("
				+ szYD_BAY_GP + ")" + ", 목표행선구분(" + szYD_AIM_RT_GP + ") "
				+ "에 대한 대상재  데이터 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = "야드(" + szYD_GP + ")," + "동("
				+ szYD_BAY_GP + ")" + ", 목표행선구분(" + szYD_AIM_RT_GP + ") "
				+ "에 대한 대상재  데이터 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;

			}
		} catch (Exception e){
			szMsg = "후판제품 반납 대상재 Lot편성 데이터 유무체크 및 데이터 반환 중 예외발생! 예외메세지: "
					+ e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} // end of chkPlGdsRetnLotGp

	/**
	 * 오퍼레이션명 : 후판제품 반납 대상재 작업요구
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procPlGdsRetnWrkReq(JDTORecord msgRecord) throws JDTOException {

		// 스케줄기준 DAO
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		// 작업예약 재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		// 공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		// 공용 METHOD
		YdUtils ydutils = new YdUtils();
		
		YdPlateCommDAO commDao = new YdPlateCommDAO();

		// 리턴값(boolean)
		boolean blnRtnVal = false;
		// 리턴값(int)
		int intRtnVal = 0;
		// 메세지
		String szMsg = "";
		// METHOD명
		String szMethodName = "procPlGdsRetnWrkReq";
		// 사용자
		String szUser = "SYSTEM";

		// 레코드 선언
		JDTORecord recPara = null;
		JDTORecord recStkPara = null;
		JDTORecord recInPara   = null;
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
		//야드To위치Guide
		String szYD_TO_LOC_GUIDE = null;
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
		// 크레인 호기
		String szYD_WRK_CRN_INPUT = null;
		
		// TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		// 에러 리턴
		if(szRcvTcCode == null){

			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error ("
					+ szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;

		}
		// TC CODE DISPLAY
		if(bDebugFlag){

			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		}

		try {

			// 받은 전문 편집
			// 스케줄코드
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			if(szYD_SCH_CD.equals("")){

				szMsg = "[전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;

			}
			//야드구분
			szYD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP");
			if(szYD_GP.equals("")){

				szMsg = "[전문 이상] 야드구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;

			}
			//동구분
			szYD_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP");
			if(szYD_BAY_GP.equals("")){

				szMsg = "[전문 이상] 동구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;

			}
			
			//목표야드구분
			szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_YD_GP");
			if(szYD_AIM_YD_GP.equals("")){

				szMsg = "[전문 이상] 목표야드구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;

			}
			//목표동구분
			szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_BAY_GP");
			if(szYD_AIM_BAY_GP.equals("")){

				szMsg = "[전문 이상] 목표동구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;

			}
			//야드To위치Guide
			szYD_TO_LOC_GUIDE = ydDaoUtils.paraRecChkNull(msgRecord, "YD_TO_LOC_GUIDE");
			
			// 재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_LOT_GP_SH");
			if(intMtlCnt == 0){

				szMsg = "[전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;

			}
			
			// 크레인 호기 입력 			
			szYD_WRK_CRN_INPUT = ydDaoUtils.paraRecChkNull(msgRecord, "WRK_CRN");
			
			// 재료번호
			szSTL_NO = new String[intMtlCnt + 1];
			// 권상모음순서
			szYD_UP_COLL_SEQ = new String[intMtlCnt + 1];

			// 재료번호, 권상모음순서
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){

				// 재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,
						"STL_NO" + Loop_i);
				if(szSTL_NO[Loop_i].equals("")){

					szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;

				}
				// 권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,
						"YD_UP_COLL_SEQ" + Loop_i);
				if(szYD_UP_COLL_SEQ[Loop_i].equals("")){

					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i]
							+ ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;

				}
			}
	
			if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYD_SCH_CD.substring(0, 1))){
				//통합스케줄 (신버전) 방식
				
				//-- 통합 크레인 스케줄 조회 -----------------------------------------------------------------start--
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				recInPara = JDTORecordFactory.getInstance().create();
				
				recInPara.setField("YD_SCH_CD", szYD_SCH_CD);				
				intRtnVal = commDao.select(recInPara, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0095");	
				
				if(intRtnVal == 0) {
					szMsg = "[ydManualReq] 통합 크레인 스케줄 코드 조회 0건 - ["+szYD_SCH_CD+"]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					return;
				}
				
				//레코드 추출
				rsResult.first();
				recPara = rsResult.getRecord();
				
				//입력 받은 크레인으로 지정
				if(szYD_WRK_CRN_INPUT.equals("")){
					szCrn 			= ydDaoUtils.paraRecChkNull(recPara, "WRK_CRN");
				}else{
					szCrn 			= szYD_WRK_CRN_INPUT ;
				}
				
				szYD_SCH_PRIOR 	= ydDaoUtils.paraRecChkNull(recPara, "CRN_PRIOR");
				//-- 통합 크레인 스케줄 조회 ------------------------------------------------------------------end---
				
			} else {
				//구 스케줄 방식 (통합이전)
			
				// 리턴 recordSet 생성
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	
				// 스케줄 기준 체크
				blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
				if(!blnRtnVal) return ;
	
				// 레코드 추출
				rsResult.first();
				recPara = rsResult.getRecord();
	
				// 스케줄CD 체크
				// 스케줄 금지 유무
				szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");
				
				// 스케줄 금지 유무가 "Y"이면 처리를 중지하고 유스케이스를 종료한다.
				if(szYD_SCH_PROH_EXN.equals("Y")){
	
					szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
				
				// 작업크레인
				szYD_WRK_CRN = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
				// 작업크레인우선순위
				szYD_WRK_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN_PRIOR");
				// 대체크레인유무
				szYD_ALT_CRN_YN = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
				// 대체크레인
				szYD_ALT_CRN = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
				// 대체크레인우선순위
				szYD_ALT_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_PRIOR");
				
				// 작업크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);
	
				// 작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
				if(!blnRtnVal){
	
					szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	
					// 대체크레인의 유무를 체크한다.
					// 대체크레인이 없으면 에러 리턴
					if(!szYD_ALT_CRN_YN.equals("Y")){
	
						szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return ;
	
					}
					// 대체크레인이 있으면 대체크레인 설비 상태 체크
					blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
					// 대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
					if(!blnRtnVal){
	
						szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return ;
	
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
				
				//입력 받은 크레인으로 지정
				if(!szYD_WRK_CRN_INPUT.equals("")){
					szCrn 			= szYD_WRK_CRN_INPUT ;
				}
			}

			// 재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다.
			// 작업예약재료 등록 여부를 체크한다.
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){

				// 크레인사양과 저장품 사양을 체크(길이,폭,중량)
				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
				if(!blnRtnVal)
					return ;

				// 다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if(!blnRtnVal)
					return ;

			}

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if(!blnRtnVal)
				return ;
			// 레코드추출
			rsResult.first();
			recPara = rsResult.getRecord();
			// 작업예약ID
			szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");

			// INSERT 항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();

			// INSERT할 항목 SET
			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID); 			// 작업예약ID
			recPara.setField("YD_GP", szYD_GP);	 						// 야드구분
			recPara.setField("YD_BAY_GP", szYD_BAY_GP); 				// 야드동구분
			recPara.setField("YD_SCH_CD", szYD_SCH_CD); 				// 야드스케쥴코드
			recPara.setField("YD_SCH_PRIOR", szYD_SCH_PRIOR); 			// 야드스케쥴우선순위
			recPara.setField("YD_AIM_YD_GP", szYD_AIM_YD_GP); 			// 야드목표야드구분
			recPara.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP); 		// 야드목표동구분
			recPara.setField("YD_TO_LOC_GUIDE", szYD_TO_LOC_GUIDE); 	// 야드To위치Guide
			// 야드스케쥴진행상태
			// 야드스케쥴기동구분
			// 야드스케쥴요청구분
			recPara.setField("REGISTER", szUser);
			// 작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if(intRtnVal < 1){
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}

			// 조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();

			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			// recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			// recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", szUser);

			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){

				// 리턴 recordSet 생성
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				// 재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				intRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C",
						rsResult);
				if(intRtnVal != 1){
					szMsg = "재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져오는 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					// 예외를 발생시켜 롤백 시킴
					throw new DAOException(szSessionName + " : " + szMethodName
							+ " - " + szMsg);
					// return ;
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

				if(intRtnVal < 1){
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					// 예외를 발생시켜 롤백 시킴
					throw new DAOException(szSessionName + " : " + szMethodName
							+ " - " + szMsg);
					// return ;
				}
			}

			// 후판제품 크레인스케쥴메인 호출 : 스케쥴코드, 설비ID(크레인)
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("JMS_TC_CD", "YDYDJ506");
			recPara.setField("YD_SCH_CD", szYD_SCH_CD);
			recPara.setField("YD_EQP_ID", szCrn);

			ydDelegate.sendMsg(recPara);

		} catch (JDTOException e){
			szMsg = "후판제품 반납 대상재 작업요구  처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(e);
		}
	} // end of procPlGdsRetnWrkReq()
	
	
	
	
	
	/**
	 * 오퍼레이션명 : C연주정정L2 OHC Take-In 요구 (C3YDL006, C7YDL006)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procOHCTakeInCmpl(JDTORecord msgRecord) throws JDTOException  {	
		//야드적치단
		YdStkLyrDao  	ydStkLyrDao  	= new YdStkLyrDao();
		YdStockDao  	ydStockDao   	= new YdStockDao();
		YdWrkbookDao 	ydWrkbookDao 	= new YdWrkbookDao();
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		// 레코드
		JDTORecord    recPara  = null;
		JDTORecordSet rsResult = null;
		
		// 메세지
		String szMsg           = "";

		// 메소드명
		String szMethodName    = "procOHCTakeInCmpl";
		// 사용자
		String szUser          = "SYSTEM";
		String szOperationName = "C연주정정L2 OHC Take-In 요구";
		
		// 전문발생일시
		String szDate          = "";
		// 설비ID
		String szYD_EQP_ID     = "";
		// 적치BED.NO
		String szYD_STK_BED_NO = "";
		// 재료번호
		String szSTL_NO        = "";
		// TO위치
		String szTO_LOC        = "";
		// 적치열구분
		String szYD_STK_COL_GP = "";
		// 적치단번호
		String szYD_STK_LYR_NO = "";
		//스케줄코드
		String szYD_SCH_CD 			 = "";
		//스케줄금지유무
		String szYD_SCH_PROH_EXN     = "";
		//작업크레인
		String szYD_WRK_CRN			 = "";
		//작업크레인우선순위
		String szYD_WRK_CRN_PRIOR	 = "";
		//대체크레인유무
		String szYD_ALT_CRN_YN		 = "";
		//대체크레인
		String szYD_ALT_CRN			 = "";
		//대체크레인우선순위
		String szYD_ALT_CRN_PRIOR	 = "";
		//작업예약ID
		String szYD_WBOOK_ID		 = "";
		//크레인
		String szCrn                 = "";
		//스케줄우선순위
		String szYD_SCH_PRIOR        = "";
		//야드구분
		String szYD_GP			     = "";
		//동구분
		String szYD_BAY_GP	         = "";
		//목표야드
		String szYD_AIM_YD_GP	     = "";
		//목표동
		String szYD_AIM_BAY_GP	     = "";
		
		boolean blnRtnVal      = false;
		int intRtnVal = 0;
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		
		//에러 리턴
		if(szRcvTcCode == null){
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;		
		}
		
		//TC CODE DISPLAY
		if(bDebugFlag){
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		try {
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[C연주정정L2] OHC Take-In 요구 수신";
			ydUtils.putLogMsg("A", YdConstant.YD_MONITORING_CHANNEL_A, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

			// 설비ID
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if(szYD_EQP_ID.equals("")){
				
				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			
			szMsg = "[1] 설비ID : " + szYD_EQP_ID;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			// 적치BED.No
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
			if(szYD_STK_BED_NO.equals("")){
				szMsg = "[전문 이상] 적치BED.No가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			
			szMsg = "[2] 적치BED.No : " + szYD_STK_BED_NO;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			// 재료번호
			szSTL_NO = ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO");
			if(szSTL_NO.equals("")){
				szMsg = "[전문 이상] 재료번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			
			szMsg = "[3] 재료번호 : " + szSTL_NO;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		
			
			//저장품을 조회하여 목표동 및 목표야드값을 가져온다.
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO", 		  szSTL_NO);
			//결과레코드셋
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 0);
			if(intRtnVal <= 0) {
				szMsg = "<procC3OhcTakeOutReq> Error!! 해당 저장품에 대한 정보가 없습니다." + szSTL_NO;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			rsResult.absolute(1);
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setRecord(rsResult.getRecord());
			szYD_AIM_YD_GP  = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_YD_GP");
			szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_BAY_GP");

			szTO_LOC = szYD_EQP_ID + szYD_STK_BED_NO;
			
			//스케줄코드
			szYD_SCH_CD = szYD_EQP_ID + "UM";
			
			//0. 기존 저장위치 삭제
			//크레인에 UPDATE
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP",       szYD_EQP_ID);    
			recPara.setField("YD_STK_BED_NO",       szYD_STK_BED_NO);   
			recPara.setField("YD_STK_LYR_NO",       "001") ;
			recPara.setField("YD_STK_LYR_MTL_STAT", "E");
			recPara.setField("STL_NO",              "");
			
			intRtnVal = ydStkLyrDao.updYdStklyr(recPara, 0);  //적치단의 재료정보 Clear
			if(intRtnVal <= 0) {
				szMsg = "OHC Take-In 요구 : From 저장위치 Claer 정상적으로 안됨.. ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			
			//1. 재료번호로 현재 야드에 적치중인 재료를 찾아 위치를 파악한다.
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO", szSTL_NO);
			recPara.setField("YD_STK_LYR_MTL_STAT", "C");
			
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsResult, 3);
			if(intRtnVal <= 0) {
				szMsg = "OHC Take-In 요구 : 검색한저장품이 현재 적치단에 등록되어있지 않습니다. ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}else if(intRtnVal > 1) {
				szMsg = "OHC Take-In 요구 : 검색한 저장품이 적치단에 중복등록되어있습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			rsResult.absolute(1);
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setRecord(rsResult.getRecord());
			
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
			szYD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO");
			
			//2. 찾은 위치를 FROM위치로하고 전문으로 받은 R/T위치를 TO위치로지정하여 작업예약과 작업예약 재료를 생성한다.
			//스케줄 기준 체크
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal) {
				//return ;
				throw new EJBException("스케줄 기준 체크 에러");
			}
			
			//레코드 추출
			rsResult.first();
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setRecord(rsResult.getRecord());

			//스케줄 금지 유무
			szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");
			//작업크레인
			szYD_WRK_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
			//작업크레인우선순위
			szYD_WRK_CRN_PRIOR     = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN_PRIOR");
			//대체크레인유무
			szYD_ALT_CRN_YN   = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
			//대체크레인
			szYD_ALT_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
			//대체크레인우선순위
			szYD_ALT_CRN_PRIOR     = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_PRIOR");
			
			szMsg = "스케줄기준항목";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//스케줄 금지 유무가  "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if (szYD_SCH_PROH_EXN.equals("Y")) {
				
				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				//return ;
				throw new EJBException(szMsg);
			}
			
			//작업크레인 설비 상태 체크
			blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);
			
			//작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if (!blnRtnVal) {
				
				szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
				//대체크레인의 유무를 체크한다.
				//대체크레인이 없으면 에러 리턴
				if (!szYD_ALT_CRN_YN.equals("Y")) {
					
					szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					//return ;
					throw new EJBException(szMsg);
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {
					
					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					//return ;
					throw new EJBException(szMsg);
				} else {
					//대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					szCrn = szYD_ALT_CRN;
					szYD_SCH_PRIOR = szYD_ALT_CRN_PRIOR;
				}
			} else {
				//작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				szCrn = szYD_WRK_CRN;
				szYD_SCH_PRIOR = szYD_WRK_CRN_PRIOR;
			}

			//다른 작업예약에 재료가 등록되어있는지 체크한다.
			blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO);
			if (!blnRtnVal) {
				//return ;
				throw new EJBException("다른 작업예약에 재료가 등록되어 있음");
			}

			//리턴 recordSet 생성
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			//작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if (!blnRtnVal) {
				//return ;
				throw new EJBException("작업예약ID 생성 에러");
			}
			//레코드추출
			rsResult.first();
			recPara = rsResult.getRecord();
			//작업예약ID
			szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");
			
			//야드구분
			szYD_GP       = szYD_SCH_CD.substring(0, 1);
			//동구분
			szYD_BAY_GP   = szYD_SCH_CD.substring(1, 2);
			
			//INSERT 항목 RECORD 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			//INSERT할 항목 SET
			recPara.setField("YD_WBOOK_ID", 		szYD_WBOOK_ID);
			recPara.setField("YD_GP", 				szYD_GP);
			recPara.setField("YD_BAY_GP", 			szYD_BAY_GP);
			recPara.setField("YD_SCH_PRIOR",		szYD_SCH_PRIOR);
			recPara.setField("YD_SCH_CD", 			szYD_SCH_CD);
			recPara.setField("YD_AIM_YD_GP", 		szYD_AIM_YD_GP);
			recPara.setField("YD_AIM_BAY_GP", 		szYD_AIM_BAY_GP);
			recPara.setField("YD_TO_LOC_DCSN_MTD", 	"F");
			recPara.setField("YD_TO_LOC_GUIDE", 	szTO_LOC);
			recPara.setField("REGISTER", 			szUser);
	
			//작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				//return ;
				throw new EJBException(szMsg);
			}
			
			
			//INSERT 항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			//작업예약재료 정보 SET
			recPara.setField("YD_WBOOK_ID",    szYD_WBOOK_ID);
			recPara.setField("YD_STK_COL_GP",  szYD_STK_COL_GP);
			recPara.setField("YD_STK_BED_NO",  szYD_STK_BED_NO);
			recPara.setField("REGISTER", 	   szUser);
			recPara.setField("STL_NO", 		   szSTL_NO);
			recPara.setField("YD_STK_LYR_NO",  szYD_STK_LYR_NO);
			recPara.setField("YD_UP_COLL_SEQ", "1");
				
			//작업예약재료 테이블에 등록한다.
			intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
			
			if (intRtnVal < 1) {
				szMsg = "작업예약재료 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				//return ;
				throw new EJBException(szMsg);
			}
			
			//C연주크레인스케줄Main 호출
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("JMS_TC_CD", "YDYDJ500");
			recPara.setField("YD_SCH_CD", szYD_SCH_CD);
			recPara.setField("YD_EQP_ID", szCrn);
			ydDelegate.sendMsg(recPara);
			//3. 크레인스케줄을 호출한다.
		} catch(Exception e){
			szMsg = "OHC Take-In 요구 예외발생, 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
	} //end of procOHCTakeInCmpl
	
	/**
	 * 오퍼레이션명 : 후판제품 반납 대상재 작업요구 (차량)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procPlGdsRetnWrkReqTR(JDTORecord msgRecord) throws JDTOException {

		// 스케줄기준 DAO
		YdWrkbookDao ydWrkbookDao 		= new YdWrkbookDao();
		// 작업예약 재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		// 공용 DAO METHOD
		YdDaoUtils ydDaoUtils 			= new YdDaoUtils();
		// 공용 METHOD
		YdUtils ydutils 				= new YdUtils();

		// 리턴값(boolean)
		boolean blnRtnVal 			= false;
		// 리턴값(int)
		int intRtnVal 				= 0;
		// 메세지
		String szMsg 				= "";
		// METHOD명
		String szMethodName 		= "procPlGdsRetnWrkReq";
		// 사용자
		String szUser 				= "SYSTEM";

		// 레코드 선언
		JDTORecord recPara 			= null;
		JDTORecord recStkPara 		= null;
		// 레코드셋 선언
		JDTORecordSet rsResult 		= null;

		//야드구분
		String szYD_GP 				= null;
		//동구분
		String szYD_BAY_GP 			= null;
		//야드구분
		String szYD_AIM_YD_GP 		= null;
		//동구분
		String szYD_AIM_BAY_GP 		= null;
		//To위치결정방법
		String szYD_TO_LOC_DCSN_MTD = null;
		//To위치Guide
		String szYD_TO_LOC_GUIDE 	= null;
		//차량사용구분
		String szYD_CAR_USE_GP 		= null;
		// 재료매수(int)
		int intMtlCnt 				= 0;
		// 재료번호
		String[] szSTL_NO 			= null;
		// 권상모음순서
		String[] szYD_UP_COLL_SEQ 	= null;
		// 스케줄코드
		String szYD_SCH_CD 			= null;
		// 야드스케쥴우선순위
		String szYD_SCH_PRIOR 		= null;
		// 스케줄 금지 유무
		String szYD_SCH_PROH_EXN 	= null;
		// 작업크레인
		String szYD_WRK_CRN 		= null;
		// 작업크레인우선순위
		String szYD_WRK_CRN_PRIOR 	= null;
		// 대체크레인유무
		String szYD_ALT_CRN_YN 		= null;
		// 대체크레인
		String szYD_ALT_CRN 		= null;
		// 대체크레인우선순위
		String szYD_ALT_CRN_PRIOR 	= null;
		// 선택크레인
		String szCrn 				= null;
		// 작업예약ID
		String szYD_WBOOK_ID 		= null;

		// TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		// 에러 리턴
		if(szRcvTcCode == null){

			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error ("
					+ szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;

		}
		// TC CODE DISPLAY
		if(bDebugFlag){

			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		}

		try {

			// 받은 전문 편집
			// 스케줄코드
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			if(szYD_SCH_CD.equals("")){

				szMsg = "[전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;

			}
			//야드구분
			szYD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP");
			if(szYD_GP.equals("")){

				szMsg = "[전문 이상] 야드구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;

			}
			//동구분
			szYD_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP");
			if(szYD_BAY_GP.equals("")){

				szMsg = "[전문 이상] 동구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;

			}
			
			//야드구분
			szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_YD_GP");
			if(szYD_AIM_YD_GP.equals("")){

				szMsg = "[전문 이상] 목표야드구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;

			}
			//동구분
			szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_BAY_GP");
			if(szYD_AIM_BAY_GP.equals("")){

				szMsg = "[전문 이상] 목표동구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;

			}

			//To위치결정방법
			szYD_TO_LOC_DCSN_MTD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_TO_LOC_DCSN_MTD");
			if(szYD_TO_LOC_DCSN_MTD.equals("")){

				szMsg = "[전문 이상] To위치결정방법이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;

			}
			
			//To위치Guide
			szYD_TO_LOC_GUIDE = ydDaoUtils.paraRecChkNull(msgRecord, "YD_TO_LOC_GUIDE");
			if(szYD_TO_LOC_GUIDE.equals("")){

				szMsg = "[전문 이상] To위치Guide가  없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;

			}
			
			//차량사용구분
			szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_USE_GP");
			if(szYD_CAR_USE_GP.equals("")){

				szMsg = "[전문 이상] 차량사용구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;

			}
			
			// 재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_LOT_GP_SH");
			if(intMtlCnt == 0){

				szMsg = "[전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;

			}

			
			// 재료번호
			szSTL_NO = new String[intMtlCnt + 1];
			// 권상모음순서
			szYD_UP_COLL_SEQ = new String[intMtlCnt + 1];

			// 재료번호, 권상모음순서
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){

				// 재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,
						"STL_NO" + Loop_i);
				if(szSTL_NO[Loop_i].equals("")){

					szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;

				}
				// 권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,
						"YD_UP_COLL_SEQ" + Loop_i);
				if(szYD_UP_COLL_SEQ[Loop_i].equals("")){

					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i]
							+ ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;

				}
			}

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if(!blnRtnVal) return ;

			// 레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();

			// 스케줄CD 체크
			// 스케줄 금지 유무
			szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");
			
			// 스케줄 금지 유무가 "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if(szYD_SCH_PROH_EXN.equals("Y")){

				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			
			// 작업크레인
			szYD_WRK_CRN = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
			// 작업크레인우선순위
			szYD_WRK_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN_PRIOR");
			// 대체크레인유무
			szYD_ALT_CRN_YN = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
			// 대체크레인
			szYD_ALT_CRN = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
			// 대체크레인우선순위
			szYD_ALT_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_PRIOR");
			
			// 작업크레인 설비 상태 체크
			blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);

			// 작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if(!blnRtnVal){

				szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				// 대체크레인의 유무를 체크한다.
				// 대체크레인이 없으면 에러 리턴
				if(!szYD_ALT_CRN_YN.equals("Y")){

					szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;

				}
				// 대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				// 대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if(!blnRtnVal){

					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;

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
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){

				// 크레인사양과 저장품 사양을 체크(길이,폭,중량)
				blnRtnVal = chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
				if(!blnRtnVal)
					return ;

				// 다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if(!blnRtnVal)
					return ;

			}

			// 리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			// 작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if(!blnRtnVal)
				return ;
			// 레코드추출
			rsResult.first();
			recPara = rsResult.getRecord();
			// 작업예약ID
			szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");

			// INSERT 항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();

			// INSERT할 항목 SET
			recPara.setField("YD_WBOOK_ID", 		szYD_WBOOK_ID); 			// 작업예약ID
			recPara.setField("YD_GP", 				szYD_GP);	 				// 야드구분
			recPara.setField("YD_BAY_GP", 			szYD_BAY_GP); 				// 야드동구분
			recPara.setField("YD_SCH_CD", 			szYD_SCH_CD); 				// 야드스케쥴코드
			recPara.setField("YD_SCH_PRIOR", 		szYD_SCH_PRIOR); 			// 야드스케쥴우선순위
			recPara.setField("YD_AIM_YD_GP", 		szYD_AIM_YD_GP); 			// 야드목표야드구분
			recPara.setField("YD_AIM_BAY_GP", 		szYD_AIM_BAY_GP); 			// 야드목표동구분
			recPara.setField("YD_TO_LOC_DCSN_MTD", 	szYD_TO_LOC_DCSN_MTD); 		// 야드To위치결정방법
			recPara.setField("YD_TO_LOC_GUIDE", 	szYD_TO_LOC_GUIDE); 		// 야드To위치Guide
			recPara.setField("YD_CAR_USE_GP", 		szYD_CAR_USE_GP);			// 야드차량사용구분
			// 야드스케쥴진행상태
			// 야드스케쥴기동구분
			// 야드스케쥴요청구분
			recPara.setField("REGISTER", szUser);
			// 작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if(intRtnVal < 1){
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}

			// 조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();

			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			// recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			// recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", szUser);

			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){

				// 리턴 recordSet 생성
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				// 재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				intRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C",
						rsResult);
				if(intRtnVal != 1){
					szMsg = "재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져오는 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					// 예외를 발생시켜 롤백 시킴
					throw new DAOException(szSessionName + " : " + szMethodName
							+ " - " + szMsg);
					// return ;
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

				if(intRtnVal < 1){
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					// 예외를 발생시켜 롤백 시킴
					throw new DAOException(szSessionName + " : " + szMethodName
							+ " - " + szMsg);
					// return ;
				}
			}

			// 후판제품 크레인스케쥴메인 호출 : 스케쥴코드, 설비ID(크레인)
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("JMS_TC_CD", "YDYDJ506");
			recPara.setField("YD_SCH_CD", szYD_SCH_CD);
			recPara.setField("YD_EQP_ID", szCrn);

			ydDelegate.sendMsg(recPara);

		} catch (JDTOException e){
			szMsg = "후판제품 반납 대상재 차량 작업요구  처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(e);
		}
	} // end of procPlGdsRetnWrkReqTR()
	
	
	/**
	 * 오퍼레이션명 : C연주/후판 슬라브 자동 준비작업 스케쥴 실행작업
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procAutoWorkLotComp(JDTORecord msgRecord) throws JDTOException  {
		
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		YdStockDao ydStockDao = new YdStockDao();
		YdEqpDao   ydEqpDao	  = new YdEqpDao();
		//레코드 선언
		JDTORecord recPara     = null;
		JDTORecord recOutPara  = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		//레코드셋 선언
		JDTORecordSet rsPara   = null;
		//리턴값(boolean)
		boolean blnRtnVal      = false;
		//메세지
		String szMsg           = ""; 
		//메소드명
		String szMethodName    = "procAutoWorkLotComp";
		
		//Carry_In 재료 기준 매수
		int intCarryInCnt      = 0;
		
		//전문 생성 일시
		String szDate          = null;
		//설비ID
		String szYD_EQP_ID     = null;
		//장압LOT순번SORT기준
		String szYD_ORDER_BY   = null;
		//스케줄코드
		String szYD_SCH_CD     = null;
		
		//재료매수
		int intRealSh		   = 0;
		//리턴값(int)
		int intRtnVal          = 0;
		
		String szYD_GP 			= "";
		String szBAY_GP 		= "";
		String szEQP_GP 		= "";
		String szSPN_GP 		= "";
		String szYD_STK_COL_GP1 = "";
		String szYD_STK_COL_GP2 = "";
		
		try {
			//설비ID
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if(szYD_EQP_ID.equals("")){
				
				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			String sIsWork 	= "";
			String sIsOrder = "";
			{
				/*
				 * 장입준비자업 크레인별 기준정보 가져오기.
				 * 	YD_CURR_BAY_GP(야드현재동구분)	> 준비작업실행여부[Y,N]
				 *	YD_HOME_BAY_GP(야드홈동구분)	> 준비작업검색방법[A,D]
				 */
				//레코드 생성
				recPara	= JDTORecordFactory.getInstance().create();
				//리턴 recordSet 생성
				rsPara 	= JDTORecordFactory.getInstance().createRecordSet("");
				//설비ID
				recPara.setField("YD_EQP_ID", szYD_EQP_ID);
				//설비 테이블 조회
				intRtnVal = ydEqpDao.getYdEqp(recPara, rsPara, 0);
				
				//레코드 추출
				rsPara.first();
				recPara = rsPara.getRecord();
				
				sIsWork 	= ydDaoUtils.paraRecChkNull(recPara, "YD_CURR_BAY_GP");
				sIsOrder 	= ydDaoUtils.paraRecChkNull(recPara, "YD_HOME_BAY_GP");
				
				if("N".equals(sIsWork)){
					szMsg = "[자동준비작업 Lot 편성] 준비작업 실행안함: " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					return;
				}
			}
			
			/*
			 * 자동준비작업 예외처리
			 * 후판슬라브 A1 크레인의 경우 자동준비작업의 기능변경
			 * - 장입보급을 야드에 하도록 기능 보완.
			 */
			if("DACRA1".equals(szYD_EQP_ID)){
				
				//레코드 생성
				recPara = JDTORecordFactory.getInstance().create();
				//JMS TC CODE
				recPara.setField("JMS_TC_CD"             , "YDYDJ237");
				//전문 발생 일시
				recPara.setField("JMS_TC_CREATE_DDTT"    , YdUtils.getCurDate("yyyy/MM/dd HH:mm:ss"));
				//설비ID 
				recPara.setField("YD_EQP_ID"             , YdConstant.EQP_D_PU1);
				//목표행선구분(wan ) -어떻게 보낼 것인가
				recPara.setField("YD_AIM_RT_GP"          , YdConstant.AR_WRK_WAIT_A_MILL);				//작업대기(A후판압연)
				
				ydDelegate.sendMsg(recPara);
				
				return;
			} else 	if("DBCRA2".equals(szYD_EQP_ID)){
				
				//레코드 생성
				recPara = JDTORecordFactory.getInstance().create();
				//JMS TC CODE
				recPara.setField("JMS_TC_CD"             , "YDYDJ497");
				//전문 발생 일시
				recPara.setField("JMS_TC_CREATE_DDTT"    , YdUtils.getCurDate("yyyy/MM/dd HH:mm:ss"));
				//설비ID 
				recPara.setField("YD_EQP_ID"             , "DBPU05");
				//목표행선구분(wan ) -어떻게 보낼 것인가
				recPara.setField("YD_AIM_RT_GP"          , "C3");				//작업대기(2후판압연)
				
				ydDelegate.sendMsg(recPara);
				
				return;
			}

			
			szYD_GP  = szYD_EQP_ID.substring(0,1);
			szBAY_GP = szYD_EQP_ID.substring(1,2);
			szEQP_GP = szYD_EQP_ID.substring(5);
			
			if(sIsOrder.equals("")){
				szYD_ORDER_BY = "A";
			}else{
				szYD_ORDER_BY = sIsOrder;
			}
			
			//=================================================================================
			//작업재료매수 생성 
			if("D".equals(szYD_GP)){
				intCarryInCnt = 3;
			}else if("A".equals(szYD_GP)){
				intCarryInCnt = 4;
			}
			//=================================================================================
			
			//=================================================================================
			//스케쥴코드 생성 
			if("D".equals(szYD_GP)){
				szYD_SCH_CD  = szYD_GP+szBAY_GP+"YD99M"+("1".equals(szEQP_GP)?"R":"L");
				ydUtils.putLog(szSessionName, szMethodName, "szYD_SCH_CD="+szYD_SCH_CD, YdConstant.DEBUG);
			}else if("A".equals(szYD_GP)){
				szYD_SCH_CD  = szYD_GP+szBAY_GP+"YD99M"+("1".equals(szEQP_GP)?"L":"R");
				ydUtils.putLog(szSessionName, szMethodName, "szYD_SCH_CD="+szYD_SCH_CD, YdConstant.DEBUG);
			}
			//=================================================================================
			
			//=================================================================================
			//저장영역 생성 
			if("D".equals(szYD_GP)){
				if("1".equals(szEQP_GP)){
					szYD_STK_COL_GP1 = szYD_GP+szBAY_GP+"01";
					szYD_STK_COL_GP2 = szYD_GP+szBAY_GP+"02";
				}else{
					szYD_STK_COL_GP1 = szYD_GP+szBAY_GP+"02";
					szYD_STK_COL_GP2 = szYD_GP+szBAY_GP+"03";
				}
			}else if("A".equals(szYD_GP)){
				if("1".equals(szEQP_GP)){
					szYD_STK_COL_GP1 = szYD_GP+szBAY_GP+"01";
					szYD_STK_COL_GP2 = szYD_GP+szBAY_GP+"02";
				}else{
					szYD_STK_COL_GP1 = szYD_GP+szBAY_GP+"03";
					szYD_STK_COL_GP2 = szYD_GP+szBAY_GP+"04";
				}
			}
			//=================================================================================
			
			szMsg = "[자동준비작업 Lot 편성]스케줄코드 : " + szYD_SCH_CD;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//자동준비작업 Lot 편성 대상 재료 Select
			recPara  = JDTORecordFactory.getInstance().create();
			//스케쥴코드
			recPara.setField("YD_SCH_CD", 		szYD_SCH_CD);
			//야드/동/스판 구분
			recPara.setField("YD_STK_COL_GP1", 	szYD_STK_COL_GP1);
			recPara.setField("YD_STK_COL_GP2", 	szYD_STK_COL_GP2);
			
			//저장품 테이블 조회
			intRtnVal = ydStockDao.getYdStock_AutoLot(recPara, rsResult, szYD_ORDER_BY);
			
			if(intRtnVal <= 0){
				szMsg = "[자동준비작업 Lot 편성] 대상정보 없슴: " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return;
			}
			
			//지시 재료 매수가 Carry In 재료기준 매수보다 작으면 지시 재료 매수를 재료기준매수에 대입한다.
			if(intCarryInCnt > rsResult.size()) intCarryInCnt = rsResult.size();
			
			//레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();

			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//Carry In 재료기준매수만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			if("D".equals(szYD_GP)){
				
				int intTmp	= 0;
				
				for (int Loop_i = 1; Loop_i <= intCarryInCnt; Loop_i++){
					
					//재료번호
					recOutPara.setField("STL_NO" + Loop_i, ydDaoUtils.paraRecChkNull(recPara, "STL_NO"));
					
					if("A".equals(szYD_ORDER_BY)){
						intTmp = Loop_i;
					}else{
						intTmp = intCarryInCnt -(Loop_i-1);
					}
					
					recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, "" + intTmp);
					
					//다음 레코드 추출
					rsResult.next();
					recPara = rsResult.getRecord();
					//재료매수
					intRealSh++;
				}
				
			}else if("A".equals(szYD_GP)){
				
				String sPreYdStkLotCd	= "";
				String sCurYdStkLotCd	= "";
				
				for (int Loop_i = 1; Loop_i <= intCarryInCnt; Loop_i++){
					
					//재료번호
					recOutPara.setField("STL_NO" + Loop_i, ydDaoUtils.paraRecChkNull(recPara, "STL_NO"));
					recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, "" + Loop_i);
					
					sCurYdStkLotCd = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LOT_CD");
					
					// 현재 작업대상 장입LOT순번만 대상으로 체크한다.(C연주만)
					if(Loop_i == 1){
						sPreYdStkLotCd = sCurYdStkLotCd;
					}else{
						if(!sPreYdStkLotCd.equals(sCurYdStkLotCd)){
							break;
						}
					}
					
					//다음 레코드 추출
					rsResult.next();
					recPara = rsResult.getRecord();
					//재료매수
					intRealSh++;
				}
			}
			
			//스케줄코드
			recOutPara.setField("YD_SCH_CD",          szYD_SCH_CD);
			//CARRY_IN 재료매수
			recOutPara.setField("YD_CARRY_IN_SH",     "" + intRealSh);
			//적치열구분
			recOutPara.setField("YD_STK_COL_GP",      szYD_EQP_ID);
			
			this.procAutoWorkLotCompReq(recOutPara);
			
			szMsg = "자동준비작업 Lot 편성 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch(Exception e){
			szMsg = "자동준비작업 Lot 편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}
		
	} //end of procAutoWorkLotComp
	
	/**
	 * 오퍼레이션명 : C연주/후판 슬라브 자동 준비작업 스케쥴 실행작업요청.
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procAutoWorkLotCompReq(JDTORecord msgRecord)throws JDTOException  {
		
		//스케줄기준 DAO
		YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();
		//작업예약 재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils           = new YdDaoUtils();
		//공용 METHOD
		YdUtils ydutils                 = new YdUtils();
		
		//리턴값(boolean)
		boolean blnRtnVal      = false;
		//리턴값(int)
		int intRtnVal          = 0;
		//메세지
		String szMsg           = "";
		//METHOD명
		String szMethodName    = "procAutoWorkLotCompReq";
		//사용자
		String szUser          = "SYSTEM";
		
		//레코드 선언
		JDTORecord recPara     = null;
		JDTORecord recStkPara  = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		
		//설비ID(열구분)
		String szYD_STK_COL_GP     = null;
		//적치BED번호
		String szYD_STK_BED_NO     = null;
		//재료매수(int)
		int intMtlCnt              = 0;
		//재료번호
		String[] szSTL_NO          = new String[6];
		//권상모음순서
		String[] szYD_UP_COLL_SEQ  = new String[6];
		//스케줄코드
		String szYD_SCH_CD         = null;
		//스케줄우선순위
		String szYD_SCH_PRIOR		= "";
		//스케줄 금지 유무
		String szYD_SCH_PROH_EXN   = null;
		//작업크레인
		String szYD_WRK_CRN        = null;
		// 작업크레인우선순위
		String szYD_WRK_CRN_PRIOR = null;
		//대체크레인유무
		String szYD_ALT_CRN_YN     = null;
		//대체크레인
		String szYD_ALT_CRN        = null;
		// 대체크레인우선순위
		String szYD_ALT_CRN_PRIOR = null;
		//선택크레인
		String szCrn               = null;
		//작업예약ID
		String szYD_WBOOK_ID       = null;
		// 크레인 작업가능 매수
		int  intWrkAblCnt          = 0;
		
		try {
		
			//받은 전문 편집
			//스케줄코드
			szYD_SCH_CD 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			
			//적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
			
			//재료매수
			intMtlCnt 		= ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_CARRY_IN_SH");
			
			//재료번호, 권상모음순서
			for(int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
				
				//재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO" + Loop_i);
				if(szSTL_NO[Loop_i].equals("")){
					
					szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
					
				}
				//권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_COLL_SEQ" + Loop_i);
				if(szYD_UP_COLL_SEQ[Loop_i].equals("")){
					
					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i] + ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;	
					
				}
			}
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if(!blnRtnVal) return ;
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//스케줄CD 체크
			//스케줄 금지 유무
			szYD_SCH_PROH_EXN 	= ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");
			//작업크레인
			szYD_WRK_CRN      	= ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
			//작업크레인우선순위
			szYD_WRK_CRN_PRIOR  = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN_PRIOR");
			//대체크레인유무
			szYD_ALT_CRN_YN   	= ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
			//대체크레인
			szYD_ALT_CRN      	= ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
			//대체크레인우선순위
			szYD_ALT_CRN_PRIOR  = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_PRIOR");
			
			//스케줄 금지 유무가  "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if(szYD_SCH_PROH_EXN.equals("Y")){
				
				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			
			//작업크레인 설비 상태 체크
			blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);
			
			//작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if(!blnRtnVal){
				
				szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
				//대체크레인의 유무를 체크한다.
				//대체크레인이 없으면 에러 리턴
				if(!szYD_ALT_CRN_YN.equals("Y")){
					
					szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if(!blnRtnVal){
					
					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
					
				} else {
					//대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					szCrn = szYD_ALT_CRN;
					szYD_SCH_PRIOR = szYD_ALT_CRN_PRIOR;
				}
			} else {
				//작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				szCrn = szYD_WRK_CRN;
				szYD_SCH_PRIOR = szYD_WRK_CRN_PRIOR;
			}
			
			/*
			 * 2010.07.05 YJK 폭/중량기준으로 대상재를 체크한다.
			 */
			intWrkAblCnt = this.chkWrkMtl_W(szSTL_NO, szCrn, intMtlCnt);
			if (intWrkAblCnt == 0) {
				szMsg = "작업예약 재료 폭체크 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
			
			//리턴 recordSet 생성
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			
			//작업예약ID 생성
			blnRtnVal = this.getYdWbookId(rsResult);
			if(!blnRtnVal) return ;
			//레코드추출
			rsResult.first();
			recPara = rsResult.getRecord();
			//작업예약ID
			szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");
			
			//INSERT 항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			//야드구분
			String szYD_GP       = szYD_SCH_CD.substring(0, 1);
			//동구분
			String szYD_BAY_GP   = szYD_SCH_CD.substring(1,2);
			
			//INSERT할 항목 SET
			recPara.setField("YD_WBOOK_ID", 	szYD_WBOOK_ID);
			recPara.setField("YD_GP", 			szYD_GP);
			recPara.setField("YD_BAY_GP", 		szYD_BAY_GP);
			recPara.setField("YD_SCH_CD", 		szYD_SCH_CD);
			recPara.setField("YD_SCH_PRIOR", 	szYD_SCH_PRIOR);
			recPara.setField("YD_AIM_YD_GP", 	szYD_GP);
			recPara.setField("YD_AIM_BAY_GP", 	szYD_BAY_GP);
			recPara.setField("YD_TO_LOC_DCSN_MTD", "S");	//야드To위치결정방법
			recPara.setField("YD_TO_LOC_GUIDE", "");		//야드To위치Guide
			recPara.setField("REGISTER", 		szUser);
	
			//작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			
			//조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
			recPara.setField("REGISTER", 	  szUser);

			for (int Loop_i = 1; Loop_i <= intWrkAblCnt; Loop_i++){
				
				//리턴 recordSet 생성
				rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
				//재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				intRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C", rsResult);
				//if( intRtnVal != 1 ) return ;
				
				if(intRtnVal != 1) {
					if( intRtnVal == 0 ) {
						intRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "U", rsResult);			//보조작업으로 권상대기 인 경우
						if(intRtnVal != 1) {
							szMsg="적치단에 재료["+szSTL_NO[Loop_i]+"]가 존재하지 않습니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							throw new JDTOException(szMsg);
						}
					}else{
						szMsg="적치단에 재료["+szSTL_NO[Loop_i]+"] 조회 시 오류발생";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						throw new JDTOException(szMsg);
					}
				}
				
				//레코드추출
				rsResult.first();
				recStkPara = rsResult.getRecord();
				
				//재료번호
				recPara.setField("STL_NO", 		   szSTL_NO[Loop_i]);
				//적치열구분
				recPara.setField("YD_STK_COL_GP",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_COL_GP"));
				//적치BED번호
				recPara.setField("YD_STK_BED_NO",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_BED_NO"));
				//적치단번호
				recPara.setField("YD_STK_LYR_NO",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_LYR_NO"));
				//권상모음순서
				recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ[Loop_i]);
				
				// 작업예약재료 테이블에 등록한다.
				intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
			}
			
			//스케줄코드, 설비id
			recPara = JDTORecordFactory.getInstance().create();
			
			if("A".equals(szYD_GP)){
				recPara.setField("JMS_TC_CD", 	"YDYDJ500");	//연주스케쥴 Main
			}else if("D".equals(szYD_GP)){
				recPara.setField("JMS_TC_CD", 	"YDYDJ503"); 	//후판스케쥴 Main
			}
			
			recPara.setField("YD_SCH_CD", 	szYD_SCH_CD);
			recPara.setField("YD_EQP_ID", 	szCrn);
			
			ydDelegate.sendMsg(recPara);
			
		} catch(Exception e){
			szMsg = "자동준비작업 Lot 편성요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}
	} // end of procAutoWorkLotCompReq()
	
	
	/**
	 * 오퍼레이션명 : 2후판가열로보급Lot편성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procBPlRefurSupLotComp(JDTORecord msgRecord) throws JDTOException  {
		
		YdDelegate ydDelegate = new YdDelegate();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils = new YdDaoUtils();
		//레코드 선언
		JDTORecord recPara     = null;
		JDTORecord recOutPara  = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
	
		//리턴값(boolean)
		boolean blnRtnVal      = false;
		//메세지
		String szMsg           = "";
		//메소드명
		String szMethodName    = "procBPlRefurSupLotComp";
		
		/*
		 * 2후판슬라브야드의 크레인작업가능매수는 3으로 설정 --> 크레인사양 테이블에서 크레인의 설정값을 읽어서 사용하는 것으로 변경필요.
		 * 수정자 : 임춘수
		 * 수정일자 : 2009.08.11
		 */
		int intCarryInCnt         = 3;				
		//전문 생성 일시
		String szDate             = null;

		//설비ID
		String szYD_EQP_ID             = null;
		//목표행선구분
		String szYD_AIM_RT_GP          = null;
		//적치BED번호
		String szYD_STK_BED_NO         = null;
		//스케줄코드
		String szYD_SCH_CD             = null;
		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if(szRcvTcCode == null){
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		//TC CODE DISPLAY
		if(bDebugFlag){
		
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		try {
			//스케줄코드
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if(szYD_EQP_ID.equals("")){
				
				szMsg = "[전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}
			//목표행선구분
			szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_AIM_RT_GP");
			if(szYD_AIM_RT_GP.equals("")){
				
				szMsg = "[전문 이상] 목표행선구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
				
			}
			//적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
			if(szYD_STK_BED_NO.equals("")){
				
				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}
			
			//=================================================================================
			//수신된 현재위치에 대해 야드적치Bed/야드저장집합구분 Table을 확인하여 스케쥴코드를 Rlue에서 가져온다.
			//BRE 등록 안됨...테스트용 스케줄코드 생성
			//추후 구현..
			if("".equals(szYD_STK_BED_NO)){
				szYD_SCH_CD  = "DBYD99MR";
			}else{
				szYD_SCH_CD  = szYD_EQP_ID + "UM";
			}
			//=================================================================================
			
			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//보급 Lot 편성 대상 재료 Select
			blnRtnVal = this.chkGetCHRSupplyLotGpPlt(szYD_SCH_CD, "PB", rsResult);
			if(!blnRtnVal) return ;
			
			//지시 재료 매수가 Carry In 재료기준매수(현재3매)보다 작으면 지시 재료 매수를 재료기준매수에 대입한다.
			if(intCarryInCnt > rsResult.size()) intCarryInCnt = rsResult.size();
			
			//레코드 생성
			recOutPara = JDTORecordFactory.getInstance().create();
			
			//JMS TC CODE
			recOutPara.setField("JMS_TC_CD", "YDYDJ495");
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			double totalSlabT = 0;
			
			//Carry In 재료기준매수(현재3매)만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
			for (int Loop_i = 1; Loop_i <= intCarryInCnt; Loop_i++){

				totalSlabT = totalSlabT + ydDaoUtils.paraRecChkNullDouble(recPara, "SLAB_T");
				
				if(szYD_SCH_CD.equals(szYD_EQP_ID + "UM")){
					if(totalSlabT >= 850){
						
						szMsg = "2후판 가열로 보급 Lot 편성과정에서 총두께의  합이 850이 넘음!="+totalSlabT;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						--intCarryInCnt;
						break;
					}
				}
				
				//재료번호
				recOutPara.setField("STL_NO" + Loop_i, ydDaoUtils.paraRecChkNull(recPara, "STL_NO"));
				//권상모음순서
				recOutPara.setField("YD_UP_COLL_SEQ" + Loop_i, "" + Loop_i);

				//다음 레코드 추출
				rsResult.next();
				recPara = rsResult.getRecord();
			}
			
			//전문 발생 일시
			szDate = ydUtils.getCurDate("yyyy/MM/dd HH:mm:ss");
			//발생 일시
			recOutPara.setField("JMS_TC_CREATE_DDTT", szDate);
			//스케줄코드
			recOutPara.setField("YD_SCH_CD",          szYD_SCH_CD);
			//CARRY_IN 재료매수
			recOutPara.setField("YD_CARRY_IN_SH",     "" + intCarryInCnt);
			//적치열구분
			recOutPara.setField("YD_STK_COL_GP",      szYD_EQP_ID);
			//적치BED번호
			recOutPara.setField("YD_STK_BED_NO",      szYD_STK_BED_NO);
			
			//전문 송신
			ydDelegate.sendMsg(recOutPara);
			//this.procBPlCarryInWrkReq(recOutPara);
			
			szMsg = "2후판 가열로 보급 Lot 편성 후 CARRY_IN 송신 완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch(Exception e){
			szMsg = "2후판 가열로 보급 Lot 편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new JDTOException(szMsg);
		}
		
	} //end of procBPlRefurSupLotComp
	
	/**
	 * 오퍼레이션명 : 2후판Carry-In작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procBPlCarryInWrkReq(JDTORecord msgRecord)throws JDTOException  {
		String szMsg           = "";
		String szMethodName    = "procCCsCHrSupCarryInWrkReq";
		
		try{
			JDTORecord recRtn = JDTORecordFactory.getInstance().create();
			
			EJBConnector ydEjbCon = new EJBConnector("default", this);
			recRtn = (JDTORecord)ydEjbCon.trx("IssueWrkDmdSeEJB", "procBPlCarryInWrkReq_Rnew", msgRecord);
			
			String sEqpId 	= ydDaoUtils.paraRecChkNull(recRtn, "YD_EQP_ID");
			
			if(!"".equals(sEqpId)){
				// 스케쥴 모듈 호출
				JDTORecord recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("JMS_TC_CD", 	"YDYDJ503");
				recPara.setField("YD_SCH_CD", 	ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD"));
				recPara.setField("YD_EQP_ID", 	ydDaoUtils.paraRecChkNull(recRtn, "YD_EQP_ID"));
				
				ydDelegate.sendMsg(recPara);
			}
		} catch (Exception e) {
			szMsg="["+szMethodName+"] Exception발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMsg);
		}	// end try catch문
	}
	
	/**
	 * 오퍼레이션명 : 2후판Carry-In작업요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord procBPlCarryInWrkReq_Rnew(JDTORecord msgRecord)throws JDTOException  {
		
		//스케줄기준 DAO
		YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();
		//작업예약 재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils           = new YdDaoUtils();
		
		//리턴값(boolean)
		boolean blnRtnVal      = false;
		//리턴값(int)
		int intRtnVal          = 0;
		//메세지
		String szMsg           = "";
		//METHOD명
		String szMethodName    = "procBPlCarryInWrkReq";
		//사용자
		String szUser          = "SYSTEM";
		
		//레코드 선언
		JDTORecord recPara     = null;
		JDTORecord recStkPara  = null;
		JDTORecord recRtn      = JDTORecordFactory.getInstance().create();
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		
		//설비ID(열구분)
		String szYD_STK_COL_GP     = null;
		//적치BED번호
		String szYD_STK_BED_NO     = null;
		//재료매수(int)
		int intMtlCnt              = 0;
		//재료번호
		String[] szSTL_NO          = new String[5];
		//권상모음순서
		String[] szYD_UP_COLL_SEQ  = new String[5];
		//스케줄코드
		String szYD_SCH_CD         = null;
		//야드스케줄우선순위
		String szYD_SCH_PRIOR		= null;
		//스케줄 금지 유무
		String szYD_SCH_PROH_EXN   = null;
		//작업크레인
		String szYD_WRK_CRN        = null;
		// 작업크레인우선순위
		String szYD_WRK_CRN_PRIOR = null;
		//대체크레인유무
		String szYD_ALT_CRN_YN     = null;
		//대체크레인
		String szYD_ALT_CRN        = null;
		// 대체크레인우선순위
		String szYD_ALT_CRN_PRIOR = null;
		//선택크레인
		String szCrn               = null;
		//작업예약ID
		String szYD_WBOOK_ID       = null;
		// 크레인 작업가능 매수
		int  intWrkAblCnt          = 0;
		
		//TC CODE 추출
		String szRcvTcCode = ydUtils.getTcCode(msgRecord);
		//에러 리턴
		if(szRcvTcCode == null){
		
			szMsg = szSessionName + "::" + szMethodName + "() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return recRtn;
		
		}
		//TC CODE DISPLAY
		if(bDebugFlag){
		
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}
		
		try {
		
			//받은 전문 편집
			//스케줄코드
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			if(szYD_SCH_CD.equals("")){
				
				szMsg = "[전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return recRtn;
				
			}
			//적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
			if(szYD_STK_COL_GP.equals("")){
				
				szMsg = "[전문 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return recRtn;
				
			}
			//적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
			if(szYD_STK_BED_NO.equals("")){
				
				szMsg = "[전문 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}
			//재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_CARRY_IN_SH");
			if(intMtlCnt == 0){
				
				szMsg = "[전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return recRtn;
				
			}

			//재료번호, 권상모음순서
			for(int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
				
				//재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO" + Loop_i);
				if(szSTL_NO[Loop_i].equals("")){
					
					szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return recRtn;
					
				}
				//권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_COLL_SEQ" + Loop_i);
				if(szYD_UP_COLL_SEQ[Loop_i].equals("")){
					
					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i] + ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return recRtn;	
					
				}
			}

			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if(!blnRtnVal) return recRtn;
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//스케줄CD 체크
			//스케줄 금지 유무
			szYD_SCH_PROH_EXN 	= ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");
			//작업크레인
			szYD_WRK_CRN      	= ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
			//작업크레인우선순위
			szYD_WRK_CRN_PRIOR  = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN_PRIOR");
			//대체크레인유무
			szYD_ALT_CRN_YN   	= ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
			//대체크레인
			szYD_ALT_CRN      	= ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
			//대체크레인우선순위
			szYD_ALT_CRN_PRIOR  = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_PRIOR");
			
			//스케줄 금지 유무가  "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if(szYD_SCH_PROH_EXN.equals("Y")){
				
				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return recRtn;
			}
			
			//작업크레인 설비 상태 체크
			blnRtnVal = this.eqpStatCheck(szYD_WRK_CRN);
			
			//작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
			if(!blnRtnVal){
				
				szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
				//대체크레인의 유무를 체크한다.
				//대체크레인이 없으면 에러 리턴
				if(!szYD_ALT_CRN_YN.equals("Y")){
					
					szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return recRtn;
					
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if(!blnRtnVal){
					
					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return recRtn;
					
				} else {
					//대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					szCrn = szYD_ALT_CRN;
					szYD_SCH_PRIOR = szYD_ALT_CRN_PRIOR;
				}
			} else {
				//작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				szCrn = szYD_WRK_CRN;
				szYD_SCH_PRIOR = szYD_WRK_CRN_PRIOR;
			}
			
			/////////////////////////////////////////////////////////////////////////////
			////////////////////보급 대상재를 가지고 여러가지 체크를한다..중요사항/////////////////
			/////////////////////////////////////////////////////////////////////////////
			/*
			 * 2010.07.05 YJK 단순체크사항 - 삭제해도 괜찮을거 같음.
			 */
			/*
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++){
				
				//크레인사양과 저장품 사양을 체크(길이,폭,중량)
				blnRtnVal = this.chkCrnSpecMtlSpec(szSTL_NO[Loop_i], szCrn);
				if(!blnRtnVal) return ;
				
				//다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if(!blnRtnVal) return ;
				
			}	
			*/
			/*
			 * 2010.07.05 YJK 폭/중량기준으로 대상재를 체크한다.
			 */
			intWrkAblCnt = this.chkWrkMtl_W(szSTL_NO, szCrn, intMtlCnt);
			if (intWrkAblCnt == 0) {
				szMsg = "작업예약 재료 폭체크 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return recRtn;
			}
			/////////////////////////////////////////////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////////
			
			
			/////////////////////////////////////////////////////////////////////////////
			////////////////////자동준비작업 보완기능 - 스케쥴 충돌현상 방지//////////////////////
			/////////////////////////////////////////////////////////////////////////////
			//1. 스케쥴코드가 DBPU05UM 이면 DBYD99MR로 Lot편성한 재료가 스케쥴에 등록되어 있는지 체크한다.
			//2. 등록되어 있으면 권하위치(szYD_STK_COL_GP + szYD_STK_BED_NO) 변경 모듈을 호출한다. 리턴한다.
			if("DBPU05UM".equals(szYD_SCH_CD)){
				
				rsResult  	= JDTORecordFactory.getInstance().createRecordSet("");
				recPara 	= JDTORecordFactory.getInstance().create();
				recPara.setField("YD_EQP_ID", 			szCrn);
				recPara.setField("YD_SCH_CD", 			"DBYD99MR");
				
				intRtnVal = ydWrkbookDao.getYdSchCrnMtl(recPara,rsResult);
				
				if(intRtnVal > 0){
					
					JDTORecord recOutTemp 	= null;
					String sStlNo			= "";
					String sSchId			= "";
					boolean isWork			= true;
					
					// 재료들이 동일한지를 체크
					// 대상 - 작업예약재료 <> Lot편성재료
					for( int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++ ) {
						
						rsResult.absolute(Loop_i);
						recOutTemp = JDTORecordFactory.getInstance().create();
						recOutTemp.setRecord(rsResult.getRecord());
						
						sStlNo  = ydDaoUtils.paraRecChkNull(recOutTemp, "STL_NO");
						sSchId  = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CRN_SCH_ID");
						
						ydUtils.putLog(szSessionName, szMethodName, "YD_CRN_SCH_ID ="+sSchId, YdConstant.DEBUG);
						
						if(isWork){
							for (int Loop_j = 1; Loop_j <= intWrkAblCnt; Loop_j++){
								
								if(sStlNo.equals(szSTL_NO[Loop_j])){
									isWork	= true;
									break;
								}else{
									isWork = false;
								}
							}
						}else{
							break;
						}
					}
					ydUtils.putLog(szSessionName, szMethodName, "LAST YD_CRN_SCH_ID ="+sSchId, YdConstant.DEBUG);
					ydUtils.putLog(szSessionName, szMethodName, "isWork ="+isWork, YdConstant.DEBUG);
					
					if(isWork){
					
						//권하위치 변경(화면 권하위치 변경모듈 호출 )
						recPara 	= JDTORecordFactory.getInstance().create();
						recPara.setField("YD_EQP_ID", 			szCrn);
						recPara.setField("YD_CRN_SCH_ID", 		sSchId);
						recPara.setField("YD_GP", 				YdConstant.YD_GP_A_PLATE_SLAB_YARD);
						recPara.setField("YD_DN_WO_LOC", 		szYD_STK_COL_GP + szYD_STK_BED_NO);
	
						JDTORecord [] inRecord =  new JDTORecord[1];
						inRecord[0]	= recPara;
						EJBConnector ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
						ejbConn.trx("updToPosFix", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
		                
						return recRtn;
					}
				}
			//3. 스케쥴코드가 DAYD99MR 이면 DAYD99MR로 이미 작업예약에 등록이 되어 있는지 확인한다. 있으면 리턴..	
			}else if("DBYD99MR".equals(szYD_SCH_CD)){
				
				recPara 	= JDTORecordFactory.getInstance().create();
				recPara.setField("YD_SCH_CD", "DBYD99MR");
				
				intRtnVal = ydWrkbookDao.getYdSchWrkMtl(recPara);
				
				if(intRtnVal > 0){
					return recRtn;
				}
			}
			/////////////////////////////////////////////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////////
			
			//리턴 recordSet 생성
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			
			//작업예약ID 생성
			blnRtnVal = getYdWbookId(rsResult);
			if(!blnRtnVal) return recRtn;
			//레코드추출
			rsResult.first();
			recPara = rsResult.getRecord();
			//작업예약ID
			szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");
			
			//INSERT 항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			//야드구분
			String szYD_GP       = szYD_SCH_CD.substring(0, 1);
			//동구분
			String szYD_BAY_GP   = szYD_SCH_CD.substring(1,2);
			
			//INSERT할 항목 SET
			recPara.setField("YD_WBOOK_ID", 		szYD_WBOOK_ID);
			recPara.setField("YD_GP", 				szYD_GP);
			recPara.setField("YD_BAY_GP", 			szYD_BAY_GP);
			recPara.setField("YD_SCH_CD", 			szYD_SCH_CD);
			recPara.setField("YD_SCH_PRIOR", 		szYD_SCH_PRIOR);
			
			if("".equals(szYD_STK_BED_NO)){
				
				recPara.setField("YD_TO_LOC_DCSN_MTD", 	"S");								//야드To위치결정방법
				
			}else{
			
				recPara.setField("YD_TO_LOC_DCSN_MTD", 	"F");								//야드To위치결정방법
				recPara.setField("YD_TO_LOC_GUIDE", 	szYD_STK_COL_GP + szYD_STK_BED_NO); //야드To위치Guide
			}
			
			recPara.setField("YD_AIM_YD_GP", 		szYD_GP);
			recPara.setField("YD_AIM_BAY_GP", 		szYD_BAY_GP);
			recPara.setField("REGISTER", 			szUser);
	
			//작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			
			//조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
			recPara.setField("REGISTER", 	  szUser);

			for (int Loop_i = 1; Loop_i <= intWrkAblCnt; Loop_i++){
				
				//리턴 recordSet 생성
				rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
				//재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				intRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "C", rsResult);
				
				if(intRtnVal != 1) {
					if( intRtnVal == 0 ) {
						intRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], "U", rsResult);			//보조작업으로 권상대기 인 경우
						if(intRtnVal != 1) {
							szMsg="적치단에 재료["+szSTL_NO[Loop_i]+"]가 존재하지 않습니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							throw new JDTOException(szMsg);
						}
					}else{
						szMsg="적치단에 재료["+szSTL_NO[Loop_i]+"] 조회 시 오류발생";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						throw new JDTOException(szMsg);
					}
					//return YdConstant.RETN_CD_FAILURE;
				}
				
				//레코드추출
				rsResult.first();
				recStkPara = rsResult.getRecord();
				
				//재료번호
				recPara.setField("STL_NO", 		   szSTL_NO[Loop_i]);
				//적치열구분
				recPara.setField("YD_STK_COL_GP",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_COL_GP"));
				//적치BED번호
				recPara.setField("YD_STK_BED_NO",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_BED_NO"));
				//적치단번호
				recPara.setField("YD_STK_LYR_NO",  ydDaoUtils.paraRecChkNull(recStkPara, "YD_STK_LYR_NO"));
				//권상모음순서
				recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ[Loop_i]);
				
				// 작업예약재료 테이블에 등록한다.
				intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
			}
					
			recRtn.setField("YD_EQP_ID", 	szCrn);
			
			return recRtn;
			
		} catch(Exception e){
			szMsg = "2후판 Carry-In 작업요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMsg);
		}
	} // end of procBPlCarryInWrkReq()
	
	
	/**
	 * 오퍼레이션명 : 후판제품출하차량상차작업요구(YDYDJ285)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String procPlGdsDistCarLdWrkReq_PIDEV(JDTORecord msgRecord)throws JDTOException  {
		
		YdWrkbookDao ydWrkbookDao        = new YdWrkbookDao();
		YdWrkbookMtlDao ydWrkbookMtlDao  = new YdWrkbookMtlDao();
		YdCarSchDao ydCarSchDao          = new YdCarSchDao();
		YdStockDao ydStockDao 			 = new YdStockDao();
		YdStkColDao ydStkColDao 		 = new YdStkColDao();
		YdDaoUtils ydDaoUtils            = new YdDaoUtils();
		YdPlateCommDAO	commDao 		 = new YdPlateCommDAO();
		
		// 레코드 선언
		JDTORecord recPara               = null;
		JDTORecord recStk 				 = null;
		JDTORecord recStkPara            = null;
		JDTORecord recCarSchPara         = null;
		JDTORecord recInTemp         	 = null;
		JDTORecord recInPara			 = null;
		
		// 레코드셋 선언
		JDTORecordSet rsResult           = null;
		JDTORecordSet rsCarSchResult     = null;
		JDTORecordSet rsTemp           	 = null;
		
		JDTORecordSet rsStkCol     		 = null;
		
		// 메세지
		String szMsg                     = "";
		
		// METHOD명
		String szMethodName              = "procPlGdsDistCarLdWrkReq_PIDEV";
		String szOperationName			 = "후판제품출하차량상차작업요구(PI)";
		// 사용자
		String szUser                    = "SYSTEM";
		// 스케줄코드
		String szYD_SCH_CD               = "";
		String  szCURR_YD_SCH_CD		 = null;
		// 적치열구분
		String szYD_STK_COL_GP           = "";
		String szPREV_YD_STK_COL_GP		 = null;
		// 적치BED번호
		String szYD_STK_BED_NO           = null;
		String szYD_STK_LYR_NO 			 = null;
		// 목표행선구분
		//String szYD_AIM_RT_GP          = "";
		// 차량사용구분
		String szYD_CAR_USE_GP           = "";
		// 운송장비코드
		String szTRN_EQP_CD              = "";
		String szYD_EQP_ID               = null;
		// 재료매수(int)
		int intMtlCnt                    = 0;
		// 재료번호
		String szSTL_NO					 = null;
		// 권상모음순서
		String szYD_UP_COLL_SEQ          = null;
		// 야드작업우선순위
		String szYD_SCH_PRIOR            = "";
		// 야드구분
		String szYD_GP                   = null;
		String szYD_BAY_GP               = null;
		// 선택크레인
		String szCrn                     = "";
		String szCurrCrn                 = "";
		// 작업예약ID
		String szYD_WBOOK_ID             = "";
		String szCURR_YD_WBOOK_ID        = "";
		// 개소코드
		String szWLOC_CD                 = "";
		// 발지개소코드
		String szSPOS_WLOC_CD            = "";
		// 발지포인트코드
		String szSPOS_YD_PNT_CD          = "";
		// 야드목표야드구분
		String szYD_AIM_YD_GP            = "";
		// 야드목표동구분
		String szYD_AIM_BAY_GP           = "";
		//차량번호
		String szCAR_NO                  = "";
		//카드번호
		String szCARD_NO                 = "";
		// 상차정지위치
		String szYD_CARLD_STOP_LOC 		 = "";
		//차량스케줄ID
		String szYD_CAR_SCH_ID 			 = "";
		// 리턴값(int)
		int intRtnVal                    = 0;
		// 리턴값(boolean)
		boolean blnRtnVal                = false;
		String[] szTC_CODE				 = null;
		
		Vector vGroup = new Vector();
		
		// TC CODE 추출
		String szRcvTcCode               = ydUtils.getTcCode(msgRecord);
		if(szRcvTcCode == null || szRcvTcCode.equals("")){
			szMsg = "["+szOperationName+"] TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_TC_ERROR;
		}
		
		// TC CODE DISPLAY
		if(bDebugFlag){
			szMsg = "["+szOperationName+"] 전문수신 : TCCODE=" + szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		try {
			// 스케줄코드
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			if(szYD_SCH_CD.equals("")){
				szMsg = "["+szOperationName+"] [전문 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}
			
			// 재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "YD_CARLD_SH");
			if(intMtlCnt == 0){
				szMsg = "["+szOperationName+"] [전문 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}

			// 설비ID (적치열구분)
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if(szYD_EQP_ID.equals("")){
				szMsg = "["+szOperationName+"] [전문 이상] 설비ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}			

			// 차량사용구분
			szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_USE_GP");
			if(szYD_CAR_USE_GP.equals("")){
				szMsg = "["+szOperationName+"] [전문 이상] 차량사용구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}
			
			szCAR_NO = ydDaoUtils.paraRecChkNull(msgRecord, "CAR_NO");
			if(szCAR_NO.equals("")){
				szMsg = "["+szOperationName+"] [전문 이상] 차량번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}
			
			szCARD_NO = ydDaoUtils.paraRecChkNull(msgRecord, "CARD_NO");
//			if(szCARD_NO.equals("")){
//				szMsg = "["+szOperationName+"] [전문 이상] 카드번호가 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			}
			
			// 상차정지위치
			szYD_CARLD_STOP_LOC = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CARLD_STOP_LOC");
			if(szYD_CARLD_STOP_LOC.equals("")){
				szMsg = "["+szOperationName+"] [전문 이상] 상차정지위치가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}

			// 발지개소코드
			szSPOS_WLOC_CD = ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_WLOC_CD");
			if(szSPOS_WLOC_CD.equals("")){
				szMsg = "["+szOperationName+"] [전문 이상] 발지개소코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}
			
			// 발지포인트코드
			szSPOS_YD_PNT_CD = ydDaoUtils.paraRecChkNull(msgRecord, "SPOS_YD_PNT_CD");
			if(szSPOS_YD_PNT_CD.equals("")){
				szMsg = "["+szOperationName+"] [전문 이상] 발지포인트코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
			}
						
			int seq = 0;
			// 재료번호, 권상모음순서
			for(int i=0; i<intMtlCnt; i++){
				// 재료번호
				szSTL_NO = ydDaoUtils.paraRecChkNull(msgRecord, "STL_NO" + (i+1));
				if(szSTL_NO.equals("")){
					szMsg = "["+szOperationName+"] [전문 이상] " + i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;
				}
				
				szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP" + (i+1));

				if( i == 0 ) {
					seq = 1;
					szPREV_YD_STK_COL_GP = szYD_STK_COL_GP;
					rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("STL_NO", szSTL_NO);
					recInTemp.setField("YD_UP_COLL_SEQ", "" + seq);
					recInTemp.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
					recInTemp.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO" + (i+1)));
					recInTemp.setField("YD_STK_LYR_NO", ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_LYR_NO" + (i+1)));
					rsResult.addRecord(recInTemp);
					vGroup.add(rsResult);
				}else{
					if( !szYD_STK_COL_GP.substring(0, 2).equals(szPREV_YD_STK_COL_GP.substring(0, 2)) ) {
						rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
						vGroup.add(rsResult);
						seq = 1;
					}
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("STL_NO", szSTL_NO);
					recInTemp.setField("YD_UP_COLL_SEQ", "" + seq);
					recInTemp.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
					recInTemp.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO" + (i+1)));
					recInTemp.setField("YD_STK_LYR_NO", ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_LYR_NO" + (i+1)));
					rsResult.addRecord(recInTemp);
					szPREV_YD_STK_COL_GP = szYD_STK_COL_GP;
				}
				seq++;
			}
			
			szMsg="["+szOperationName+"] 전문확인 후 출하상차LOT 그룹 수 : " + vGroup.size();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			

			for(int i = 0; i < vGroup.size(); i++ ) {
				rsTemp = (JDTORecordSet)vGroup.get(i);
				for(int j = 1; j <= rsTemp.size(); j++ ) {
					rsTemp.absolute(j);
					recInTemp = rsTemp.getRecord();
					szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recInTemp, "YD_STK_COL_GP");
					szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recInTemp, "YD_STK_BED_NO");
					szYD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(recInTemp, "YD_STK_LYR_NO");
					szYD_UP_COLL_SEQ = ydDaoUtils.paraRecChkNull(recInTemp, "YD_UP_COLL_SEQ");
					szSTL_NO = ydDaoUtils.paraRecChkNull(recInTemp, "STL_NO");
					if( j  == 1 ) {
						
						szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + szYD_SCH_CD.substring(2);
						
						recPara = JDTORecordFactory.getInstance().create();
						//-- 통합 크레인 스케줄 조회 -----------------------------------------------------------------start--
						rsResult = JDTORecordFactory.getInstance().createRecordSet("");
						recInPara = JDTORecordFactory.getInstance().create();
						
						recInPara.setField("YD_SCH_CD", szYD_SCH_CD);
						
						intRtnVal = commDao.select(recInPara, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0095");	
						
						//25.07.22 1후판정정 니켈강 출하대응 - 허동수 책임 요청
						JDTORecord recResult = JDTORecordFactory.getInstance().create();
						if(intRtnVal == 0 && "P".equals(szYD_SCH_CD.substring(0,1))){
							szMsg = "["+szOperationName+"] 1후판정정야드일시, 1후판정정야드 기준 스케줄 조회  - ["+szYD_SCH_CD+"]";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

							String szRtnMsg = JPlateYdCommonUtils.getCrnInfoByCrnSchRule(szYD_SCH_CD, recResult, "");
							
							if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
								return szRtnMsg;
							}
							
							szCrn 			= ydDaoUtils.paraRecChkNull(recResult, "YD_WRK_CRN");
							szYD_SCH_PRIOR 	= ydDaoUtils.paraRecChkNull(recResult, "YD_SCH_PRIOR");
							
							szMsg = "["+szOperationName+"] 후판정정야드 스케줄기준조회시 작업크레인 - ["+szCrn+"] 우선순위 ["+szYD_SCH_PRIOR+"]";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

						}
						
						else if(intRtnVal == 0) {
							szMsg = "["+szOperationName+"] 통합 크레인 스케줄 코드 조회 0건 - ["+szYD_SCH_CD+"]";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							
							return YdConstant.RETN_CD_FAILURE;
						}
						else {
							//레코드 추출
							rsResult.first();
							recPara = rsResult.getRecord();
							
							szCrn 			= ydDaoUtils.paraRecChkNull(recPara, "WRK_CRN");
							szYD_SCH_PRIOR 	= ydDaoUtils.paraRecChkNull(recPara, "CRN_PRIOR");
							
						}
						
						//-- 통합 크레인 스케줄 조회 ------------------------------------------------------------------end---
						
						// 리턴 recordSet 생성
						rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
						
						// 작업예약ID 생성
						blnRtnVal = getYdWbookId(rsResult);
						if(!blnRtnVal) return YdConstant.RETN_CD_FAILURE;
			
						// 레코드추출
						rsResult.first();
						recPara = rsResult.getRecord();
						
						// 작업예약ID
						szYD_WBOOK_ID = recPara.getFieldString("YD_WBOOK_ID");
						
						if( szYD_CARLD_STOP_LOC.substring(0, 2).equals(szYD_STK_COL_GP.substring(0, 2)) ) {
							//현재작업예약이 등록되는 동과 차량정지위치가 같은 스케줄코드와 
							szCURR_YD_SCH_CD = szYD_SCH_CD;
							szCURR_YD_WBOOK_ID = szYD_WBOOK_ID;
							szCurrCrn = szCrn;
						}
						
						// 리턴 RecordSet 생성
						rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
						
						// 저장품테이블 조회
						blnRtnVal = this.chkGetStock(szSTL_NO, rsResult);
						if(!blnRtnVal) return YdConstant.RETN_CD_FAILURE;
						
						// 레코드추출
						rsResult.first();
						recPara = rsResult.getRecord();
						
						// 야드목표야드구분
						szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_YD_GP");
						
						// 야드목표동구분
						szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_BAY_GP");
						
						// INSERT 항목 record 생성
						recPara = JDTORecordFactory.getInstance().create();
						
						// 야드구분
						szYD_GP = szYD_SCH_CD.substring(0, 1);
						
						// 동구분
						szYD_BAY_GP = szYD_SCH_CD.substring(1, 2);
						
						// INSERT할 항목 SET
						recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
						recPara.setField("YD_GP", 		  szYD_GP);
						recPara.setField("YD_BAY_GP", 	  szYD_BAY_GP);
						recPara.setField("YD_SCH_CD", 	  szYD_SCH_CD);
						recPara.setField("YD_SCH_PRIOR",  szYD_SCH_PRIOR);
						recPara.setField("REGISTER", 	  szUser);
						recPara.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP); 
						recPara.setField("CAR_NO",	      szCAR_NO);
						recPara.setField("CARD_NO", 	  szCARD_NO); 
						recPara.setField("YD_AIM_YD_GP",  szYD_AIM_YD_GP);  
						recPara.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP); 

						// 작업예약 INSERT
						intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
						if(intRtnVal < 1){
							szMsg = "["+szOperationName+"] 작업예약 데이터 등록 중 에러";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							return YdConstant.RETN_CD_FAILURE;
						}
					}
					
					//조회항목 record 생성
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
					recPara.setField("REGISTER", 	  szUser);
					recPara.setField("STL_NO"       , szSTL_NO);
					//적치열구분
					recPara.setField("YD_STK_COL_GP",  szYD_STK_COL_GP);
					//적치BED번호
					recPara.setField("YD_STK_BED_NO",  szYD_STK_BED_NO);
					//적치단번호
					recPara.setField("YD_STK_LYR_NO",  szYD_STK_LYR_NO);
					//권상모음순서
					recPara.setField("YD_UP_COLL_SEQ", szYD_UP_COLL_SEQ);
					
					// 작업예약재료 테이블에 등록한다.
					intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
					if(intRtnVal < 1){
						szMsg = "["+szOperationName+"] 작업예약재료 데이터 등록 중 에러";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						// 예외를 발생시켜 롤백 시킴
						throw new DAOException(szSessionName + " : " + szMethodName + " - " + szMsg);
					}
				}
			}
			// [전사물류시스템개선] 추가(Y9시스템 전송여부)
			// 2021/01/06
			if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYD_GP)){
				JDTORecord params = null;
				if(PlateGdsYdUtil.isPlateNewMoudleApply("CRN")){
					
					// 트랜잭션 이슈로 인하여 한 번 더 저장처리한다.
					recCarSchPara  = JDTORecordFactory.getInstance().create();
					rsCarSchResult = JDTORecordFactory.getInstance().createRecordSet("");
					recCarSchPara.setField("CAR_NO" , szCAR_NO);
					recCarSchPara.setField("CARD_NO", szCARD_NO);
//PIDEV_S :병행가동용:PI_YD
					recCarSchPara.setField("PI_YD",   szYD_GP);			
					intRtnVal = ydCarSchDao.getYdCarsch(recCarSchPara, rsCarSchResult, 11);
					if(intRtnVal>0){
						szYD_CAR_SCH_ID = rsCarSchResult.getRecord(0).getFieldString("YD_CAR_SCH_ID"); 
						JDTORecord rTmp = JDTORecordFactory.getInstance().create();
						rTmp.setField("YD_CAR_PROG_STAT", "2");									//상차도착상태
						rTmp.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);
						rTmp.setField("YD_CARLD_STOP_LOC", szYD_CARLD_STOP_LOC); 
						rTmp.setField("YD_PNT_CD1"         , szSPOS_YD_PNT_CD);
						rTmp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);	
						ydCarSchDao.updYdCarsch(rTmp, 0);
					}
					
    				// 트랜잭션 이슈로 인하여 대기장 도착모듈 호출 이후 예정정보를 전송
    				// 상차 예정정보 전송은 
    				// 1. 전문을 수신하였을 경우 com.inisteel.cim.yd.plateGds.session.PlateYdRcvFaEJBBean.rcvDMYDR061(JDTORecord)
    				// 2. 마지막 권하실적을 처리 후 차량정보 초기화 모듈(현재 메서드)com.inisteel.cim.yd.ydWkAct.CraneUnloadWkrHd.CraneUdHdSeEJBBean.procY4CarWrkStatCtr(JDTORecord)
    				// 3. PT차량 출하차량도착 처리 이후 DMYDR038
    				params = JDTORecordFactory.getInstance().create();
    				JDTORecordSet rsCarSchInfo = JDTORecordFactory.getInstance().createRecordSet("ydPlate");
    				params.setField("CAR_NO", szCAR_NO);
    				params.setField("YD_CARLD_STOP_LOC", szYD_CARLD_STOP_LOC);
    				
    				if(commDao.select(params, rsCarSchInfo, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.getYdCarSchById")>0){
    					
    					// PT차량이거나, 최초로 입동한 차량에 대해서만
    					// 복수동은 두번째 부터 권하실적처리 하면서 처리함
//    					String FIRST_BAYIN_CAR = rsCarSchInfo.getRecord(0).getFieldString("FIRST_BAYIN_CAR");
//    					if( szCARD_NO.startsWith("P") || "Y".equals(FIRST_BAYIN_CAR)){						
    						szMsg = "차랑변호["+szCAR_NO+"] 차량에 대하여 차량예정정보(Y9)를 전송한다.";
    						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    						JDTORecord jYDY9L008 = JDTORecordFactory.getInstance().create();
    						JDTORecordSet msgSetYDY9L008 = JDTORecordFactory.getInstance().createRecordSet("ydPlate");
    						jYDY9L008.setField("JMS_TC_CD", "YDY9L008");
    						jYDY9L008.setField("SEARCH_FLAG", "1");  //1:상차도, 2:차량스케쥴 ID
    						jYDY9L008.setField("PT_LOAD_LOC", szYD_CARLD_STOP_LOC);  //상차도 위치
    						jYDY9L008.setField("YD_WBOOK_ID",  szYD_WBOOK_ID);  //차량정지 위치
    						jYDY9L008.setField("YD_CAR_SCH_ID", ""); //차량스케쥴 ID
    						jYDY9L008.setField("CAR_NO",  szCAR_NO);  //차량번호
    						jYDY9L008.setResultCode(ydUtils.getLogId());
    						EJBConnector ejbConn1 = new EJBConnector("default", "PlateYdRcvL2SeEJB", this);
    						ejbConn1.trx("rcvY9YDL019", new Class[] { JDTORecord.class }, new Object[] { jYDY9L008 });
//    					}
    				}
				}
			}
	
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("JMS_TC_CD", "YDYDJ293");
			recInTemp.setField("CAR_NO", 			szCAR_NO);
//			recInTemp.setField("CARD_NO", 			szCARD_NO);
			recInTemp.setField("YD_WBOOK_ID", 		szCURR_YD_WBOOK_ID);
			recInTemp.setField("YD_CARLD_STOP_LOC", szYD_CARLD_STOP_LOC);
			recInTemp.setField("SPOS_YD_PNT_CD"   , szSPOS_YD_PNT_CD);
			recInTemp.setField("YD_SCH_CD", 		szCURR_YD_SCH_CD);
			recInTemp.setField("YD_CRN_ID", 		szCurrCrn);
			recInTemp.setField("SPOS_WLOC_CD", 		szSPOS_WLOC_CD); //2014.02.18 cho 추가
			 
			szMsg="["+szOperationName+"] 출하차량스케줄 수정 전문 전송 시작 - 전문내용확인";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			ydDelegate.sendMsg(recInTemp);
			
			szMsg="["+szOperationName+"] 출하차량스케줄 수정 전문 전송 성공";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		} catch(DAOException e){
			szMsg = "["+szOperationName+"] 출하 차량상차 작업요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw e;
		}catch(Exception e){
			szMsg = "["+szOperationName+"] 출하 차량상차 작업요구 처리중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(e);
		}
		return YdConstant.RETN_CD_SUCCESS;
	} // end of procPlGdsDistCarLdWrkReq()			
	
	
} // end of class IssueWrkDmdSeEJBBean
