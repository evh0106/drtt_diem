package com.inisteel.cim.yd.ydStock.CarLoadLotReg;

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
import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.dao.ydStkColDao.YdStkColDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdTcConst;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.ydWkAct.CraneLoadWkrHd.CoilCraneLdHdSeEJB;
import com.inisteel.cim.yd.ydWkAct.CraneLoadWkrHd.CoilCraneLdHdSeEJBBean;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;

/**
 * 상차LOT등록 Session EJB
 *
 * @ejb.bean name="CarLdLotRegSeEJB" jndi-name="CarLdLotRegSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class CarLdLotRegSeEJBBean extends BaseSessionBean {
	
	// Session Name
	private String szSessionName 	= getClass().getName();
	
	private YdUtils ydUtils			= new YdUtils();
	private YdDaoUtils ydDaoUtils 	= new YdDaoUtils();
	
	private YdTcConst ydTcConst 	= new YdTcConst();
 	private YdDelegate ydDelegate 	=new YdDelegate();
	// [DEBUG] message flag
	private boolean bDebugFlag		= true;
	String[] rVal = new String[1];
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	
	
	/**
	 * 코일제품상차지시등록(DMYDR023)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procCoilGdsCarLdOrd(JDTORecord inRecord)throws JDTOException  {
		/*
		 * 업무기준 : 1. 수신된 전문의 재료들을 재료상태변경이력 테이블로 등록
		 *			 2. 저장품테이블의 재료들에 재료진도코드와 야드목표행선을 수정처리
		 *			 3. 저장품이 적치된 저장위치 정보를 조회
		 *				- 차량진입순서[J-->D]에 따라 적치열Desc, 적치베드Asc, 적치단 Desc순으로 조회
		 *			 4. 위의 저장위치정보를 가지고 차량이 입동가능한 차량정지위치를 조회한다
		 *				- 입동가능한 차량정지위치가 존재하지 않을 시 첫번째 차량정지위치[기본값]를 사용한다.
		 *			 5. 위에서 결정된 차량정지위치를 사용하여 차량스케줄을 상차출발로 처리해서 생성한다.
		 *			 6. 차량정지위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
		 * 수정자 : 임춘수
		 * 수정일 : 2009.09.14
		*/
		//DAO정의
		YdStockDao ydStockDao 					= new YdStockDao();				//저장품DAO
		YdStkColDao ydStkColDao 				= new YdStkColDao();			//적치열DAO
		YdCarSchDao ydCarSchDao					= new YdCarSchDao();

		JDTORecord recStockColumn        		=  JDTORecordFactory.getInstance().create();
		JDTORecord recStlNo 					=  JDTORecordFactory.getInstance().create();
		JDTORecord	recInTemp					= JDTORecordFactory.getInstance().create();
		JDTORecord	recInTemp1					= JDTORecordFactory.getInstance().create();
		JDTORecord	recInTemp2					= JDTORecordFactory.getInstance().create();
		JDTORecord	recInTemp3					= JDTORecordFactory.getInstance().create();
		JDTORecord	recPara						= JDTORecordFactory.getInstance().create();
		JDTORecord	outRec						= JDTORecordFactory.getInstance().create();
		
		JDTORecordSet rsGetStock 				= null;
		JDTORecordSet rsResult					= null;
		JDTORecordSet rsResult1 				= null;
		String szMethodName 					= "procCoilGdsCarLdOrd";
		String szOperationName					= "코일제품상차지시등록(DMYDR023)";
		String szMsg 							= "";
		String szSTL_NO							= "";
		String szYD_GP							= "";
		String szYD_BAY_GP						= "";
		String szYD_STK_COL_GP					= null;
		String szYD_SND_STK_COL_GP				= "";
		String szWLOC_CD						= null;
		String szYD_STK_COL_ACT_STAT			= null;
		String szCAR_NO							= null;
		String szCARD_NO						= null;
		String szTRANS_ORD_DATE					= null;
		String szTRANS_ORD_SEQNO				= null;
		String szYD_CAR_SCH_ID					= null;
		String szDELY_CAR                       = "";
		String szYD_EQP_GP                      = "";
		String szYD_PNT_CD                      = "";
		String szTC_CODE	                    = "";
		
		String szNEW_LINE                       = "N";
		int intRtnVal 							= 0;
		int i									= 0;
		boolean isCarArrPoint					= false;
		long lngDELY_CAR                        = 0;
		long lngMinDELY_CAR                     = 99;
		long lngDELY_CAR5                       = 0;
		long lngMinDELY_CAR5                    = 99;
		String szYD_SND_STK_COL_GP5				= "";
		String sWORK_GP 						= "";
		String sCAR_KIND 						= "";
		// 전문받아서 szRcvTcCode에 대입
		String szRcvTcCode = ydUtils.getTcCode(inRecord);

		// 수신한 전문이 null이라면 error
		if(szRcvTcCode==null || szRcvTcCode.equals("")){
			szMsg="["+szOperationName+"] TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
		
		szMsg="[" + szOperationName + "] 메소드 시작 - 전문내용 확인 시작";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		ydUtils.displayRecord(szOperationName, inRecord);
		
		
		try{
			//=============================================================
			// 권오창
			// 2009.11.05
			//
			// Log 테이블 등록 
			// 야드는 X로 일단 두었음 수정해야됨
			//=============================================================
			szMsg = "[출하] 코일제품상차지시등록 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_A , szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
			
			rsGetStock 			= JDTORecordFactory.getInstance().createRecordSet("");
			sWORK_GP 			=ydDaoUtils.paraRecChkNull(inRecord,"WORK_GP");
			sCAR_KIND 			=ydDaoUtils.paraRecChkNull(inRecord,"CAR_KIND");
			szCAR_NO 			= ydDaoUtils.paraRecChkNull(inRecord,"CAR_NO");
			szCARD_NO 			= ydDaoUtils.paraRecChkNull(inRecord,"CARD_NO");
			szTRANS_ORD_DATE 	= ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_DT");
			szTRANS_ORD_SEQNO 	= ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO"); 
			
			
			//작업구분(1:내수/2:수출/3:연안해송/9:HYSCO스케줄)
			if(!sWORK_GP.equals("9")){
				rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
				recPara 	= JDTORecordFactory.getInstance().create();
				recPara.setField("TRANS_ORD_DT"		, szTRANS_ORD_DATE);
				recPara.setField("TRANS_ORD_SEQNO"	, szTRANS_ORD_SEQNO);
				recPara.setField("CARD_NO"			, szCARD_NO );
	
				//중복 check
				/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarYdDEL_YN*/
				intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult1, 304);
				if(intRtnVal > 0){
					szMsg= "["+szOperationName+"] TB_YD_CARSCH[차량스케줄이 편성되어 있습니다.]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
			}
			
			//저장품 조회
			/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockTransOrdDateSeqNo*/
			intRtnVal = ydStockDao.getYdStock(inRecord, rsGetStock, 113);
			if(intRtnVal <= 0){
				if(intRtnVal == 0){
					szMsg= "["+szOperationName+"] YD_STOCK[저장품] SELECT Error ::  DO NOT EXIST";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}else{
					szMsg= "["+szOperationName+"] YD_STOCK[저장품] SELECT Error :: [" + intRtnVal + "] PARAMETER ERROR" ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
			}
			
			//수신한 전문값******************************************************************************************
			/*
			TRANS_ORD_DT	운송지시일자
			TRANS_ORD_SEQNO	운송지시순번
			CAR_NO			차량번호
			CARD_NO			카드번호
			*/
			/*
			 * 2. 저장품테이블의 재료들에 재료진도코드와 야드목표행선을 수정처리
			 */
			recStockColumn.setField("TRANS_ORD_DATE", 		szTRANS_ORD_DATE);
			recStockColumn.setField("TRANS_ORD_SEQNO", 		szTRANS_ORD_SEQNO);
			recStockColumn.setField("CAR_NO",				szCAR_NO);
			recStockColumn.setField("CARD_NO", 				szCARD_NO);
			recStockColumn.setField("ARR_WLOC_CD", 			ydDaoUtils.paraRecChkNull(inRecord,"ARR_WLOC_CD"));
			recStockColumn.setField("YD_PNT_CD", 			ydDaoUtils.paraRecChkNull(inRecord,"ARR_YD_PNT_CD"));
			recStockColumn.setField("MODIFIER", 			"DMYDR023");

			//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
			rVal= YdCommonUtils.getYdAimRtGp("C",inRecord );		
			recStockColumn.setField("YD_AIM_RT_GP", rVal[0]);
			recStockColumn.setField("STL_PROG_CD", rVal[1]); 
			//****************************************************************************************************
		
						
			//저장품갱신******************************************************************************************** 
			intRtnVal = ydStockDao.updYdStock2(recStockColumn, 0);
			if(intRtnVal <= 0){
				szMsg= "["+szOperationName+"] YD_STOCK[코일제품상차지시등록] UPDATE Error :: [" + intRtnVal + "]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			ydUtils.putLog(szSessionName, szMethodName,"["+szOperationName+"] [2] YD_STOCK[코일제품상차지시등록] UPDATE Success", YdConstant.DEBUG);
			//****************************************************************************************************

			/*
			 * 3. 저장품이 적치된 저장위치 정보를 조회 - 저장위치가 04, 05, 06스판이면 A통로, 07스판이면 B통로로 처리한다.
			 */ 
			szMsg="[" + szOperationName + "] 차량정지위치를 구하기 위한 저장품의 저장위치 조회 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//저장품 동 구하기 
			rsGetStock = JDTORecordFactory.getInstance().createRecordSet("");
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_STK_COL_GP"	, "");
			recInTemp.setField("CARD_NO"		, szCARD_NO);
			recInTemp.setField("TRANS_ORD_DATE"	, szTRANS_ORD_DATE);
			recInTemp.setField("TRANS_ORD_SEQNO", szTRANS_ORD_SEQNO);
			//조회조건 : CARD_NO, TRANS_ORD_DATE, TRANS_ORD_SEQNO, YD_STK_COL_GP(LIKE) --> 코일제품창고의 차량진입순서 : J==>D순으로 진입
			//정렬순서 : 적치열 Desc, 적치베드 Asc, 적치단 Desc
//PIDEV_S :병행가동용:PI_YD
			recInTemp.setField("PI_YD",    	"J");				
			/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrMtlCTransDTSeqCardNoDesc_PIDEV*/
			intRtnVal = ydStockDao.getYdStock(recInTemp, rsGetStock, 126);
			if(intRtnVal <= 0){
				if(intRtnVal == 0){
					szMsg="[" + szOperationName + "] YD_STOCK[저장품] SELECT Error ::  DO NOT EXIST";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}else{
					szMsg="[" + szOperationName + "] YD_STOCK[저장품] SELECT Error :: [" + intRtnVal + "] PARAMETER ERROR" ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
			}
			szMsg="[" + szOperationName + "] 차량정지위치를 구하기 위한 저장품의 저장위치 조회 성공 - 건수["+intRtnVal+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			rsGetStock.first();
			recStlNo = rsGetStock.getRecord();
			
			szMsg="[" + szOperationName + "] recStlNo["+recStlNo+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recStlNo,"YD_STK_COL_GP");
			
			szMsg="[" + szOperationName + "] 차량정지위치를 구하기 위한 대상재가 존재하는 동["+szYD_STK_COL_GP+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szYD_GP 	= szYD_STK_COL_GP.substring(0, 1);
			szYD_BAY_GP = szYD_STK_COL_GP.substring(1, 2);
			szYD_EQP_GP = szYD_STK_COL_GP.substring(2, 4);   // 열
			
			/*
			 * 4. 위의 저장위치정보를 가지고 차량이 입동가능한 차량정지위치를 조회한다
			 *	카드번호 : E,P,T 로 시작 ->PT  , 숫자 로 시작 ->TR
			 */
//SJH12			

	    	if(szYD_GP.equals("H")){
				// 하이스코 출하에 해당함
				szMsg="[" + szOperationName + "]상차위치를 제품인지 소재인지 구분처리 함" + szYD_GP;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//차량정지위치 구하기
				rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
		    	recInTemp1 	= JDTORecordFactory.getInstance().create();
		    	recInTemp1.setField("YD_STK_COL_GP"	, szYD_GP + szYD_BAY_GP  + "PT");
				
		    	/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcolColGpLikeCardHysco*/
		    	intRtnVal = ydStkColDao.getYdStkcol(recInTemp1, rsResult1, 307);
				if(intRtnVal <= 0) {
					szMsg="["+szOperationName+"] 출하상차LOT 대상재가 존재하는 차량정지위치를 조회 시 오류발생 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}

// 소재 하이스코 스케쥴 코드로 야드 구분 재 setting		
				rsResult1.first();
				recInTemp3 = rsResult1.getRecord();
				szYD_GP    = StringHelper.evl(recInTemp3.getFieldString("CAR_GP"), "");
			}

	    	
	    	
			
			//작업구분(1:내수/2:수출/3:연안해송/9:HYSCO스케줄)
			if(sWORK_GP.equals("9")){
				//########################################## 9:HYSCO스케줄 ####################################################
				if(szYD_GP.equals("H")){
					szTC_CODE = "YDYDJ660";													//코일임가공차량도착실적
//					szMsg= "["+ szOperationName +"] 야드["+szYD_GP+"]는 [코일HYSCO차량도착]처리.";
					szMsg= "["+ szOperationName +"] 야드["+szYD_GP+"]는 [코일소재야드 공냉장 차량도착]처리.";
				} else {
					
					szTC_CODE = "YDYDJ653";	
					szMsg= "["+ szOperationName +"] 야드["+szYD_GP+"]는 [코일출하차량도착]처리.";
				}
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

				
				rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
				recPara 	= JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CARPNT_CD"		, ydDaoUtils.paraRecChkNull(inRecord,"CARLD_PNT_CD"));
				
				//포인트코드 -> 개소코드와 저장위치 가져오기
				/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarPoint*/
				intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult1, 412);
				if(intRtnVal > 0){
					szMsg= "["+szMethodName+"] TB_YD_CARPOINT[차량포인트가 존재 합니다..]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
					
					rsResult1.first();
					recInTemp = rsResult1.getRecord();
					szWLOC_CD    		= StringHelper.evl(recInTemp.getFieldString("WLOC_CD"), "");
					szYD_PNT_CD    		= StringHelper.evl(recInTemp.getFieldString("YD_PNT_CD"), "");	
					szYD_STK_COL_ACT_STAT	= StringHelper.evl(recInTemp.getFieldString("YD_STK_COL_ACT_STAT"), "");
					
					szMsg= "["+ szOperationName +"] 차량스케줄의 출하차량[차량번호:" + szCAR_NO + ", 카드번호:" + szCARD_NO + ", 포인트 상태:" + szYD_STK_COL_ACT_STAT  ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
					
					// TT-Car가 예약인 경우 차량도착처리 호출이 가능 하도록 조치
					if ("TT".equals(sCAR_KIND) && "R".equals(szYD_STK_COL_ACT_STAT)) {
						szYD_STK_COL_ACT_STAT = "C";
					}					
					
					//TR가 도착해 있는 경우 
					if ("TR".equals(sCAR_KIND) && "L".equals(szYD_STK_COL_ACT_STAT)) {
						szYD_STK_COL_ACT_STAT = "C";
					}
					
					if("C".equals(szYD_STK_COL_ACT_STAT)){
					
						outRec = JDTORecordFactory.getInstance().create();
						outRec.setField("TC_CODE"			,szTC_CODE);
						outRec.setField("TC_CREATE_DDTT"	,new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
						outRec.setField("YD_GP"				,szYD_GP);
						outRec.setField("TRANS_ORD_DT"		,szTRANS_ORD_DATE);
						outRec.setField("TRANS_ORD_SEQNO" 	,szTRANS_ORD_SEQNO);
						outRec.setField("CAR_NO"			,szCAR_NO);
						outRec.setField("CARD_NO"			,szCARD_NO);
						outRec.setField("SPOS_WLOC_CD"		,szWLOC_CD);	
						outRec.setField("SPOS_YD_PNT_CD"	,szYD_PNT_CD);
						outRec.setField("CAR_KIND"			,sCAR_KIND);
						outRec.setField("WORK_GP"			,sWORK_GP);
						outRec.setField("IS_EJB_CALL"		,"N");	
						//EJB Call로 처리 or JMS 송신처리
						//일단 차량도착처리 호출은 JMS 송신으로 처리
						ydUtils.displayRecord(szOperationName, outRec);
						ydDelegate.sendMsg(outRec);
						
//						EJBConnector ejbConn1 = new EJBConnector("default","CarMvHdFaEJB",this);
//						ejbConn1.trx("rcvCoilGdsDistCarArrWr",new Class[]{JDTORecord.class},new Object[]{outRec});
					}
				}
				
				szMsg= "["+ szOperationName +"] 차량스케줄의 출하차량[차량번호:" + szCAR_NO + ", 카드번호:" + szCARD_NO + ", 운송지시일자:" + szTRANS_ORD_DATE + ", 운송지시순번:" + szTRANS_ORD_SEQNO + "]에 대해 차량도착 전문전송 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				//################################################################################################################
			}else{
				//##########################################1:내수/2:수출/3:연안해송#############################################
		    	//B,C동 가상스판 70인 경우 제품통로 2번으로 저장위치 편성
				if (szYD_GP.equals("J")){
					if (szYD_BAY_GP.equals("B")||szYD_BAY_GP.equals("C")) {
						if(Integer.parseInt(szYD_EQP_GP) < 30 || Integer.parseInt(szYD_EQP_GP) == 70 ){
							szNEW_LINE = "Y";
						}	
					}
				}	
				szMsg="["+szOperationName+"] 조회된 차량정지위치에 대한 사용가능 체크 시작 szNEW_LINE:" + szNEW_LINE;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
	
				rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
		    	recInTemp 	= JDTORecordFactory.getInstance().create();
		    	recInTemp.setField("YD_STK_COL_GP"	, szYD_GP + szYD_BAY_GP + "PT");
		    	recInTemp.setField("CARD_NO"		, szCARD_NO);
		    	recInTemp.setField("NEW_LINE"		, szNEW_LINE);
		    	
		    	szMsg="[" + szOperationName + "] 출하상차LOT 대상재가 존재하는 차량이 입동가능한 차량정지위치를 조회 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    	/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcolColGpLikeCard*/
		    	intRtnVal = ydStkColDao.getYdStkcol(recInTemp, rsResult, 304);
				if(intRtnVal <= 0) {
					if(intRtnVal == 0) {
						szMsg="["+szOperationName+"] 출하상차LOT 대상재가 존재하는 차량정지위치를 조회 시 data not found";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}else if(intRtnVal == -2 ) {
						szMsg="["+szOperationName+"] 출하상차LOT 대상재가 존재하는 차량정지위치를 조회 시 parameter error";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}else{
						szMsg="["+szOperationName+"] 출하상차LOT 대상재가 존재하는 차량정지위치를 조회 시 오류발생 - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					return;
				}
		
				szMsg="["+szOperationName+"] 출하상차LOT 대상재가 존재하는 차량이 입동가능한 차량정지위치를 조회 성공 - 건수["+intRtnVal+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				szMsg="["+szOperationName+"] 조회된 차량정지위치에 대한 위치결정";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	
		
			
				for( i = 1; i <= rsResult.size(); i++ ) {
					
					rsResult.absolute(i);
					recInTemp = rsResult.getRecord() ;
					szYD_STK_COL_GP 		= StringHelper.evl(recInTemp.getFieldString("YD_STK_COL_GP"), "");
					szWLOC_CD 				= StringHelper.evl(recInTemp.getFieldString("WLOC_CD"), "");
					szYD_STK_COL_ACT_STAT 	= StringHelper.evl(recInTemp.getFieldString("YD_STK_COL_ACT_STAT"), "");
					szDELY_CAR 				= StringHelper.evl(recInTemp.getFieldString("DELY_CAR"), "");
					
					// 포인트 앞자리 5이면 기존 창고 반대 통로
					szYD_PNT_CD 			= StringHelper.evl(recInTemp.getFieldString("YD_PNT_CD"), "XXXX");
					
					szMsg="["+szOperationName+"] 조회된 차량정지위치["+szYD_STK_COL_GP+"]에 대한 야드적치열활성상태["+szYD_STK_COL_ACT_STAT+"]"+szYD_PNT_CD ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
				
					
					if( szYD_STK_COL_ACT_STAT.equals(YdConstant.YD_STK_COL_INACTIVE) ) {    //"	C"
						szMsg="["+szOperationName+"] 조회된 차량정지위치["+szYD_STK_COL_GP+"]가 사용가능";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	//C증설				
						if(!szYD_PNT_CD.substring(0,1).equals("X")){
							if (szNEW_LINE.equals("Y")){
								if(szYD_PNT_CD.substring(0,1).equals("5")){
									isCarArrPoint = true;
									break;
								}
							} else {
								isCarArrPoint = true;
								break;
							}
						}	
					}
					
					if( szYD_STK_COL_ACT_STAT.equals(YdConstant.YD_STK_COL_ACTIVE) ) {    //"L"
						if(!szYD_PNT_CD.substring(0,1).equals("X")){
							if(szYD_PNT_CD.substring(0,1).equals("5")){
								lngDELY_CAR5 = Long.parseLong(szDELY_CAR);
								if (lngMinDELY_CAR5 > lngDELY_CAR5) {
									lngMinDELY_CAR5 		= lngDELY_CAR5;
									szYD_SND_STK_COL_GP5 	= szYD_STK_COL_GP;
								}
							} else {
								lngDELY_CAR = Long.parseLong(szDELY_CAR);
								if (lngMinDELY_CAR > lngDELY_CAR) {
									lngMinDELY_CAR 			= lngDELY_CAR;
									szYD_SND_STK_COL_GP 	= szYD_STK_COL_GP;
								}
							}
						}	
					}
				}
				
				
				if( !isCarArrPoint ) {
					if (szNEW_LINE.equals("Y")){
						szYD_STK_COL_GP = szYD_SND_STK_COL_GP5;
					} else {
						szYD_STK_COL_GP = szYD_SND_STK_COL_GP;
					}	
					szMsg="["+szOperationName+"] 사용가능한 차량정지위치가 없으므로 대기가 적은["+szYD_STK_COL_GP+"]을 사용";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
				
				szMsg="["+szOperationName+"] 조회된 차량정지위치에 대한 사용가능 체크 완료 - 사용가능한 차량정지위치["+szYD_STK_COL_GP+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				/*
				 * 5. 위에서 결정된 차량정지위치를 사용하여 차량스케줄을 상차출발로 처리해서 생성한다.
				 */
				szYD_CAR_SCH_ID = ydCarSchDao.getYdCarschId();
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_CAR_SCH_ID",    szYD_CAR_SCH_ID);
				recInTemp.setField("REGISTER",         szRcvTcCode);
				recInTemp.setField("YD_EQP_WRK_STAT",  "U");									//야드설비작업상태
				recInTemp.setField("YD_EQP_ID",        YdConstant.YD_DM_CAR_EQP_ID);			//야드설비ID
				recInTemp.setField("YD_CAR_USE_GP",    YdConstant.YD_CAR_USE_GP_DM);			//차량사용구분
				recInTemp.setField("CAR_KIND", 			"TR");
				recInTemp.setField("SPOS_WLOC_CD",     szWLOC_CD);								//발지개소코드
				recInTemp.setField("CAR_NO",           szCAR_NO);								//차량번호
				recInTemp.setField("CARD_NO",          szCARD_NO);								//카드번호
				recInTemp.setField("YD_CARLD_LEV_DT",  YdUtils.getCurDate("yyyyMMddHHmmss"));	//상차출발일시
				recInTemp.setField("TRANS_ORD_DATE",   szTRANS_ORD_DATE);						//운송지시일자
				recInTemp.setField("TRANS_ORD_SEQNO",  szTRANS_ORD_SEQNO);						//운송지시순번
		    	recInTemp.setField("YD_CARLD_STOP_LOC",szYD_STK_COL_GP);						//차량상차정지위치
		    	recInTemp.setField("YD_BAYIN_WO_SEQ",  YdConstant.YD_BAYIN_WO_SEQ_DEFAULT);		//입동지시순번 - 기본값으로 설정(9)
				recInTemp.setField("YD_CAR_PROG_STAT", "1");									//상차출발상태
				recInTemp.setField("YD_CAR_WRK_GP", 	sWORK_GP);
	    		
	    		//차량스케줄 등록
		    	intRtnVal = ydCarSchDao.insYdCarsch(recInTemp);
	    		if( intRtnVal <= 0 ){
					szMsg="[" + szOperationName + "] 차량스케줄 생성 시 오류발생[반환값 : " + intRtnVal + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
	    		}
	    		
	    		szMsg="[" + szOperationName + "] 차량스케줄 생성 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				/*
				 * 6. 차량정지위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
				 */
				szMsg="[" + szOperationName + "] 차량정지위치[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 호출 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("TC_CODE"				,"YDYDJ633");
				recInTemp.setField("TC_CREATE_DDTT"			,YdUtils.getCurDate("yyyyMMddHHmmss"));
				recInTemp.setField("YD_CAR_STOP_LOC"		,szYD_STK_COL_GP);
				recInTemp.setField("YD_CAR_SCH_ID"			,szYD_CAR_SCH_ID);
				ydUtils.displayRecord(szOperationName, recInTemp);
				
				ydDelegate.sendMsg(recInTemp);
				szMsg="[" + szOperationName + "] 차량정지위치[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 호출 성공";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//################################################################################################################
			}

		}catch(Exception e){
			szMsg="[코일제품상차지시등록] Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;

		} // end of try-catch
		szMsg="[코일제품상차지시등록]수신처리 ("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of procCoilGdsCarLdOrd()


	
	/**
	 * 후판제품상차지시등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procPlGdsCarLdOrd(JDTORecord inRecord)throws JDTOException  {
		
		// 2011.04.11 YJK - 사용안함.

	} // end of procPlGdsCarLdOrd()
	
	
	/**
	 * 임가공이송상차지시등록(DMYDR025)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procCoilRentGdsCarLdOrd(JDTORecord inRecord)throws JDTOException  {
		// 저장품DAO
		YdStockDao ydStockDao 					= new YdStockDao();
		YdStkColDao ydStkColDao 				= new YdStkColDao();			//적치열DAO
		YdCarSchDao ydCarSchDao					= new YdCarSchDao();
		JDTORecord recEditColumn        		=  JDTORecordFactory.getInstance().create();
		JDTORecord recStlNo 					=  JDTORecordFactory.getInstance().create();
		JDTORecord	recInTemp		= JDTORecordFactory.getInstance().create();
		JDTORecord	recInTemp2		= JDTORecordFactory.getInstance().create();
		JDTORecord	recPara		= JDTORecordFactory.getInstance().create();		
		JDTORecordSet rsResult					= null;
		JDTORecordSet rsResult1 				= null;
		JDTORecordSet rsGetStock 			= JDTORecordFactory.getInstance().createRecordSet("");
		String szMethodName 					= "procCoilRentGdsCarLdOrd";
		String szMsg 							= "";
		String szOperationName                  = "임가공이송상차지시등록";
		String szSTL_NO							= "";
		String sYD_BAY_GP						= "";
		String szYD_GP							= "";
		String szYD_BAY_GP						= "";
		String szYD_STK_COL_GP					= null;
		String szYD_SND_STK_COL_GP				= "";
		String szWLOC_CD						= null;
		String szYD_STK_COL_ACT_STAT			= null;
		String szCAR_NO							= null;
		String szCARD_NO						= null;
		String szTRANS_ORD_DATE					= null;
		String szTRANS_ORD_SEQNO				= null;
		String szYD_CAR_SCH_ID					= null;
		String szDELY_CAR                       = "";
		String szYD_EQP_GP						="";
		String szNEW_LINE						="";
		
		
		int intRtnVal 							= 0;
		int i									= 0;
		boolean isCarArrPoint					= false;
		long lngDELY_CAR                        = 0;
		long lngMinDELY_CAR                     = 99;

		// 전문받아서 szRcvTcCode에 대입
		String szRcvTcCode = ydUtils.getTcCode(inRecord);

		// 수신한 전문이 null이라면 error
		if(szRcvTcCode==null){
			szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
		
		// 수신한 전문내용 
		ydUtils.displayRecord(szOperationName, inRecord);

		try{
			//=============================================================
			// 권오창
			// 2009.11.05
			//
			// Log 테이블 등록 
			// 야드는 X로 일단 두었음 수정해야됨
			//=============================================================
			szMsg = "[출하] 임가공이송상차지시등록 수신";
			ydUtils.putLogMsg("X", YdConstant.YD_MONITORING_CHANNEL_A, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);

			
			//수신한 전문값******************************************************************************************
			/*
			STL_APPEAR_GP	재료외형구분
			TRANS_ORD_DT	운송지시일자
			TRANS_ORD_SEQNO	운송지시순번
			CAR_NO			차량번호			추가
			CARD_NO			카드번호			추가
			YD_EQP_WRK_SH	야드설비작업매수
			STL_NO1			재료번호1
			STL_NO2			재료번호2
			STL_NO3			재료번호3
			STL_NO4			재료번호4
			STL_NO5			재료번호5
			STL_NO6			재료번호6
			STL_NO7			재료번호7
			STL_NO8			재료번호8
			STL_NO9			재료번호9
			STL_NO10		재료번호10
			STL_NO11		재료번호11
			STL_NO12		재료번호12
			STL_NO13		재료번호13
			STL_NO14		재료번호14
			STL_NO15		재료번호15
			STL_NO16		재료번호16
			STL_NO17		재료번호17
			STL_NO18		재료번호18
			STL_NO19		재료번호19
			STL_NO20		재료번호20
			*/
			
			//****************************************************************************************************
			
			
			//수신된 전문의 STL_NO의 수 만큼 Loop***********************************************************************
				recEditColumn 	= JDTORecordFactory.getInstance().create();
				recEditColumn.setField("STL_APPEAR_GP", 		ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP"));
				recEditColumn.setField("TRANS_ORD_DATE", 		ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_DT"));
				recEditColumn.setField("TRANS_ORD_SEQNO", 		ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO"));
				recEditColumn.setField("CAR_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"CAR_NO"));
				recEditColumn.setField("CARD_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"CARD_NO"));
				recEditColumn.setField("MODIFIER", 				"DMYDR025");
				
				
				//중복 전문 체크 **********************************************************************
				rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
				recPara 	= JDTORecordFactory.getInstance().create();
				recPara.setField("TRANS_ORD_DT"		, ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_DT"));
				recPara.setField("TRANS_ORD_SEQNO"	, ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO"));
				recPara.setField("CARD_NO"			, ydDaoUtils.paraRecChkNull(inRecord,"CARD_NO"));

				//중복 check
				/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarYdDEL_YN*/
				intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult1, 304);
				if(intRtnVal > 0){
					szMsg= "["+szOperationName+"] TB_YD_CARSCH[차량스케줄이 편성되어 있습니다.]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
				//************************************************************************************
				
			for(i = 1 ; i<=20; i++){
				recEditColumn.setField("STL_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"+i));
				inRecord.setField("STL_NO", 				ydDaoUtils.paraRecChkNull(inRecord,"STL_NO"+i));
				
				//STL_NO가 없다면 loop종료
				szSTL_NO = recEditColumn.getFieldString("STL_NO");
				if(szSTL_NO.equals("")){
					break;
				}				

				//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
				rVal= YdCommonUtils.getYdAimRtGp("C",inRecord );		
				recEditColumn.setField("YD_AIM_RT_GP", rVal[0]);
				recEditColumn.setField("STL_PROG_CD", rVal[1]);
					
					
				//저장품갱신******************************************************************************************** 
				intRtnVal = ydStockDao.updYdStock(recEditColumn, 0);
				if(intRtnVal <= 0){
					szMsg= "YD_STOCK[외판슬라브운송상차지시] UPDATE Error :: [" + intRtnVal + "]" ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
				ydUtils.putLog(szSessionName, szMethodName,"[2] YD_STOCK[외판슬라브운송상차지시] UPDATE Success",3);
				//****************************************************************************************************


			} //end of for *******************************************************************************************

			
//			//저장품 조회
//			intRtnVal = ydStockDao.getYdStock(inRecord, rsGetStock, 113);
//			if(intRtnVal <= 0){
//				if(intRtnVal == 0){
//					szMsg= "YD_STOCK[저장품] SELECT Error ::  DO NOT EXIST";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//					return;
//				}else{
//					szMsg= "YD_STOCK[저장품] SELECT Error :: [" + intRtnVal + "] PARAMETER ERROR" ;
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//					return ;
//				}
//			}
//			
//			recStlNo = (JDTORecord)rsGetStock.getRecord(0);	
//			
//			//수신한 재료 동 
//			sYD_BAY_GP = ydDaoUtils.paraRecChkNull(recStlNo,"YD_BAY_GP");
//			
//			//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//			/*
//			 * 차량입동지시스케줄
//			 */
//			recInTemp2.setField("YD_GP", 		ydDaoUtils.paraRecChkNull(inRecord,"YD_GP"));
//			recInTemp2.setField("YD_BAY_GP", 	sYD_BAY_GP);
//			recInTemp2.setField("CARD_NO", 		ydDaoUtils.paraRecChkNull(inRecord,"CARD_NO"));
//			
//			
//			EJBConnector ejbConn = new EJBConnector("default", "JNDICarPntReg", this);
//			Boolean isSucf = (Boolean) ejbConn.trx("rcvCarPointJisiReq", new Class[] { JDTORecord.class }, new Object[] { recInTemp2 });
//			//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
			
			/*
			 * 3. 저장품이 적치된 저장위치 정보를 조회 - 저장위치가 04, 05, 06스판이면 A통로, 07스판이면 B통로로 처리한다.
			 */
			szMsg="[" + szOperationName + "] 차량정지위치를 구하기 위한 저장품의 저장위치 조회 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szCAR_NO 			= ydDaoUtils.paraRecChkNull(inRecord,"CAR_NO");
			szCARD_NO 			= ydDaoUtils.paraRecChkNull(inRecord,"CARD_NO");
			szTRANS_ORD_DATE 	= ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_DT");
			szTRANS_ORD_SEQNO 	= ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO");
			//저장품 동 구하기 
			rsGetStock = JDTORecordFactory.getInstance().createRecordSet("");
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_STK_COL_GP"	, "");
			recInTemp.setField("CARD_NO"		, szCARD_NO);
			recInTemp.setField("TRANS_ORD_DATE"	, szTRANS_ORD_DATE);
			recInTemp.setField("TRANS_ORD_SEQNO", szTRANS_ORD_SEQNO);
			//조회조건 : CARD_NO, TRANS_ORD_DATE, TRANS_ORD_SEQNO, YD_STK_COL_GP(LIKE) --> 코일제품창고의 차량진입순서 : J==>D순으로 진입
			//정렬순서 : 적치열 Desc, 적치베드 Asc, 적치단 Desc
//PIDEV_S :병행가동용:PI_YD
			recInTemp.setField("PI_YD",    	"J");					
			/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrMtlCTransDTSeqCardNoDesc_PIDEV*/
			intRtnVal = ydStockDao.getYdStock(recInTemp, rsGetStock, 126);
			if(intRtnVal <= 0){
				if(intRtnVal == 0){
					szMsg="[" + szOperationName + "] YD_STOCK[저장품] SELECT Error ::  DO NOT EXIST";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}else{
					szMsg="[" + szOperationName + "] YD_STOCK[저장품] SELECT Error :: [" + intRtnVal + "] PARAMETER ERROR" ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
			}
			szMsg="[" + szOperationName + "] 차량정지위치를 구하기 위한 저장품의 저장위치 조회 성공 - 건수["+intRtnVal+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//recStlNo = (JDTORecord)rsGetStock.getRecord(0);
			rsGetStock.first();
			recStlNo = rsGetStock.getRecord();
			
			szMsg="[" + szOperationName + "] recStlNo["+recStlNo+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recStlNo,"YD_STK_COL_GP");
			
			szMsg="[" + szOperationName + "] 차량정지위치를 구하기 위한 대상재가 존재하는 동["+szYD_STK_COL_GP+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szYD_GP 	= szYD_STK_COL_GP.substring(0, 1);
			szYD_BAY_GP = szYD_STK_COL_GP.substring(1, 2);
			szYD_EQP_GP = szYD_STK_COL_GP.substring(2, 4); 
			
			if (szYD_GP.equals("J")){
				if (szYD_BAY_GP.equals("B")||szYD_BAY_GP.equals("C")) {
					if(Integer.parseInt(szYD_EQP_GP) < 30){
						szNEW_LINE = "Y";
					}	
				}
			}	
			
			/*
			 * 4. 위의 저장위치정보를 가지고 차량이 입동가능한 차량정지위치를 조회한다
			 *	카드번호 : E,P,T 로 시작 ->PT  , 숫자 로 시작 ->TR
			 */
			szMsg="[" + szOperationName + "] 출하상차LOT 대상재가 존재하는 차량이 입동가능한 차량정지위치를 조회 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//차량정지위치 구하기
			rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
	    	recInTemp 	= JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_STK_COL_GP"	, szYD_STK_COL_GP.substring(0,2) + "PT");
	    	recInTemp.setField("CARD_NO"		, szCARD_NO);
	    	recInTemp.setField("NEW_LINE"		, szNEW_LINE);
	    	
	    	/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcolColGpLikeCard*/
	    	intRtnVal = ydStkColDao.getYdStkcol(recInTemp, rsResult, 304);
			if(intRtnVal <= 0) {
				if(intRtnVal == 0) {
					szMsg="["+szOperationName+"] 출하상차LOT 대상재가 존재하는 차량정지위치를 조회 시 data not found";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}else if(intRtnVal == -2 ) {
					szMsg="["+szOperationName+"] 출하상차LOT 대상재가 존재하는 차량정지위치를 조회 시 parameter error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}else{
					szMsg="["+szOperationName+"] 출하상차LOT 대상재가 존재하는 차량정지위치를 조회 시 오류발생 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				return;
			}
			
			szMsg="["+szOperationName+"] 출하상차LOT 대상재가 존재하는 차량이 입동가능한 차량정지위치를 조회 성공 - 건수["+intRtnVal+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szMsg="["+szOperationName+"] 조회된 차량정지위치에 대한 사용가능 체크 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			for( i = 1; i <= rsResult.size(); i++ ) {
				rsResult.absolute(i);
				recInTemp = rsResult.getRecord() ;
				szYD_STK_COL_GP 		= StringHelper.evl(recInTemp.getFieldString("YD_STK_COL_GP"), "");
				szWLOC_CD 				= StringHelper.evl(recInTemp.getFieldString("WLOC_CD"), "");
				szYD_STK_COL_ACT_STAT 	= StringHelper.evl(recInTemp.getFieldString("YD_STK_COL_ACT_STAT"), "");
				szDELY_CAR 				= StringHelper.evl(recInTemp.getFieldString("DELY_CAR"), "");
				
				szMsg="["+szOperationName+"] 조회된 차량정지위치["+szYD_STK_COL_GP+"]에 대한 야드적치열활성상태["+szYD_STK_COL_ACT_STAT+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				if( szYD_STK_COL_ACT_STAT.equals(YdConstant.YD_STK_COL_INACTIVE) ) {    //"	C"
					szMsg="["+szOperationName+"] 조회된 차량정지위치["+szYD_STK_COL_GP+"]가 사용가능";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					isCarArrPoint = true;
					break;
				}
				
				if( szYD_STK_COL_ACT_STAT.equals(YdConstant.YD_STK_COL_ACTIVE) ) {    //"L"
					lngDELY_CAR = Long.parseLong(szDELY_CAR);
					if (lngMinDELY_CAR > lngDELY_CAR) {
						lngMinDELY_CAR = lngDELY_CAR;
						szYD_SND_STK_COL_GP = szYD_STK_COL_GP;
					}
				}
			}
			
			if( !isCarArrPoint ) {
				//송정현수정
//				szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP + "PT01";
				szYD_STK_COL_GP = szYD_SND_STK_COL_GP;
				szMsg="["+szOperationName+"] 사용가능한 차량정지위치가 없으므로 기본값["+szYD_STK_COL_GP+"]을 사용";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			szMsg="["+szOperationName+"] 조회된 차량정지위치에 대한 사용가능 체크 완료 - 사용가능한 차량정지위치["+szYD_STK_COL_GP+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			/*
			 * 5. 위에서 결정된 차량정지위치를 사용하여 차량스케줄을 상차출발로 처리해서 생성한다.
			 */
			szYD_CAR_SCH_ID = ydCarSchDao.getYdCarschId();
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_CAR_SCH_ID",    szYD_CAR_SCH_ID);
			recInTemp.setField("REGISTER",         szRcvTcCode);
			recInTemp.setField("YD_EQP_WRK_STAT",  "U");									//야드설비작업상태
			recInTemp.setField("YD_EQP_ID",        YdConstant.YD_DM_CAR_EQP_ID);			//야드설비ID
			recInTemp.setField("YD_CAR_USE_GP",    YdConstant.YD_CAR_USE_GP_DM);			//차량사용구분
			recInTemp.setField("CAR_KIND", 			"TR");
			recInTemp.setField("SPOS_WLOC_CD",     szWLOC_CD);								//발지개소코드
			recInTemp.setField("CAR_NO",           szCAR_NO);								//차량번호
			recInTemp.setField("CARD_NO",          szCARD_NO);								//카드번호
			recInTemp.setField("YD_CARLD_LEV_DT",  YdUtils.getCurDate("yyyyMMddHHmmss"));	//상차출발일시
			recInTemp.setField("TRANS_ORD_DATE",   szTRANS_ORD_DATE);						//운송지시일자
			recInTemp.setField("TRANS_ORD_SEQNO",  szTRANS_ORD_SEQNO);						//운송지시순번
	    	recInTemp.setField("YD_CARLD_STOP_LOC",szYD_STK_COL_GP);						//차량상차정지위치
	    	recInTemp.setField("YD_BAYIN_WO_SEQ",  YdConstant.YD_BAYIN_WO_SEQ_DEFAULT);		//입동지시순번 - 기본값으로 설정(9)
			recInTemp.setField("YD_CAR_PROG_STAT", "1");									//상차출발상태
    		
    		//차량스케줄 등록
	    	intRtnVal = ydCarSchDao.insYdCarsch(recInTemp);
    		if( intRtnVal <= 0 ){
				szMsg="[" + szOperationName + "] 차량스케줄 생성 시 오류발생[반환값 : " + intRtnVal + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
    		}
    		
    		szMsg="[" + szOperationName + "] 차량스케줄 생성 완료";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			/*
			 * 6. 차량정지위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
			 */
			szMsg="[" + szOperationName + "] 차량정지위치[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 호출 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("TC_CODE"				,"YDYDJ633");
			recInTemp.setField("TC_CREATE_DDTT"			,YdUtils.getCurDate("yyyyMMddHHmmss"));
			recInTemp.setField("YD_CAR_STOP_LOC"		,szYD_STK_COL_GP);
			recInTemp.setField("YD_CAR_SCH_ID"			,szYD_CAR_SCH_ID);
			ydUtils.displayRecord(szOperationName, recInTemp);
			
			ydDelegate.sendMsg(recInTemp);
			szMsg="[" + szOperationName + "] 차량정지위치[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 호출 성공";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		}catch(Exception e){
			szMsg="[임가공이송상차지시등록] Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;

		} // end of try-catch
		szMsg="[임가공이송상차지시등록]수신처리 ("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of procCoilRentGdsCarLdOrd()
	

	
	/**
	 * 코일이송상차도착PDA(DMYDR071)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procStandByYdArriveLdPDA(JDTORecord inRecord)throws JDTOException  {
 
		//DAO정의
		YdStockDao ydStockDao 					= new YdStockDao();				//저장품DAO
 
		YdCarSchDao ydCarSchDao					= new YdCarSchDao();

		JDTORecord recStockColumn        		=  JDTORecordFactory.getInstance().create();
		JDTORecord recStlNo 					=  JDTORecordFactory.getInstance().create();
		JDTORecord	recInTemp					= JDTORecordFactory.getInstance().create(); 
		JDTORecord	recPara						= JDTORecordFactory.getInstance().create();
		JDTORecord	outRec						= JDTORecordFactory.getInstance().create();
		
		JDTORecordSet rsGetStock 				= null; 
		JDTORecordSet rsResult1 				= null;
		String szMethodName 					= "procStandByYdArriveLdPDA";
		String szOperationName					= "코일이송상차도착PDA";
		String szMsg 							= ""; 
		String szYD_GP							= ""; 
		String szYD_STK_COL_GP					= null; 
		String szWLOC_CD						= null;
		String szYD_STK_COL_ACT_STAT			= null;
		String szCAR_NO							= null;
		String szCARD_NO						= null;
		String szTRANS_ORD_DATE					= null;
		String szTRANS_ORD_SEQNO				= null; 
		String szYD_PNT_CD                      = "";
		String szTC_CODE	                    = "";	
		 
		int intRtnVal 							= 0; 
		 
		String sWORK_GP 						= "";
		String sCAR_KIND 						= "";
		// 전문받아서 szRcvTcCode에 대입
		String szRcvTcCode = ydUtils.getTcCode(inRecord);

		// 수신한 전문이 null이라면 error
		if(szRcvTcCode==null || szRcvTcCode.equals("")){
			szMsg="["+szOperationName+"] TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
		
		szMsg="[" + szOperationName + "] 메소드 시작 - 전문내용 확인 시작";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		ydUtils.displayRecord(szOperationName, inRecord);
		
		
		try{
 
			rsGetStock 			= JDTORecordFactory.getInstance().createRecordSet("");
			sWORK_GP 			= ydDaoUtils.paraRecChkNull(inRecord,"WORK_GP");
			sCAR_KIND 			= ydDaoUtils.paraRecChkNull(inRecord,"CAR_KIND");
			szCAR_NO 			= ydDaoUtils.paraRecChkNull(inRecord,"CAR_NO");
			szCARD_NO 			= ydDaoUtils.paraRecChkNull(inRecord,"CARD_NO");
			szTRANS_ORD_DATE 	= ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_DT");
			szTRANS_ORD_SEQNO 	= ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO"); 
	
			
			//저장품 조회
			/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockTransOrdDateSeqNo*/
			intRtnVal = ydStockDao.getYdStock(inRecord, rsGetStock, 113);
			if(intRtnVal <= 0){
				if(intRtnVal == 0){
					szMsg= "["+szOperationName+"] YD_STOCK[저장품] SELECT Error ::  DO NOT EXIST";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}else{
					szMsg= "["+szOperationName+"] YD_STOCK[저장품] SELECT Error :: [" + intRtnVal + "] PARAMETER ERROR" ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
			}
			
			//수신한 전문값******************************************************************************************
			/*
			TRANS_ORD_DT	운송지시일자
			TRANS_ORD_SEQNO	운송지시순번
			CAR_NO			차량번호
			CARD_NO			카드번호
			*/
			/*
			 * 2. 저장품테이블의 재료들에 재료진도코드와 야드목표행선을 수정처리
			 */
			recStockColumn.setField("TRANS_ORD_DATE", 		szTRANS_ORD_DATE);
			recStockColumn.setField("TRANS_ORD_SEQNO", 		szTRANS_ORD_SEQNO);
			recStockColumn.setField("CAR_NO",				szCAR_NO);
			recStockColumn.setField("CARD_NO", 				szCARD_NO); 
			recStockColumn.setField("MODIFIER", 			szRcvTcCode);

			//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
//			rVal= YdCommonUtils.getYdAimRtGp("C",inRecord );		
//			recStockColumn.setField("YD_AIM_RT_GP", rVal[0]);
//			recStockColumn.setField("STL_PROG_CD", rVal[1]); 
			//****************************************************************************************************
		
						
			//저장품갱신******************************************************************************************** 
			intRtnVal = ydStockDao.updYdStock2(recStockColumn, 0);
			if(intRtnVal <= 0){
				szMsg= "["+szOperationName+"] YD_STOCK[코일제품상차지시등록] UPDATE Error :: [" + intRtnVal + "]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			ydUtils.putLog(szSessionName, szMethodName,"["+szOperationName+"] [2] YD_STOCK[코일제품상차지시등록] UPDATE Success", YdConstant.DEBUG);
			//****************************************************************************************************

			/*
			 * 3. 저장품이 적치된 저장위치 정보를 조회 - 저장위치가 04, 05, 06스판이면 A통로, 07스판이면 B통로로 처리한다.
			 */ 
			szMsg="[" + szOperationName + "] 차량정지위치를 구하기 위한 저장품의 저장위치 조회 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//저장품 동 구하기 
			rsGetStock = JDTORecordFactory.getInstance().createRecordSet("");
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_STK_COL_GP"	, "");
			recInTemp.setField("CARD_NO"		, szCARD_NO);
			recInTemp.setField("TRANS_ORD_DATE"	, szTRANS_ORD_DATE);
			recInTemp.setField("TRANS_ORD_SEQNO", szTRANS_ORD_SEQNO);
			//조회조건 : CARD_NO, TRANS_ORD_DATE, TRANS_ORD_SEQNO, YD_STK_COL_GP(LIKE) --> 코일제품창고의 차량진입순서 : J==>D순으로 진입
			//정렬순서 : 적치열 Desc, 적치베드 Asc, 적치단 Desc
//PIDEV_S :병행가동용:PI_YD
			recInTemp.setField("PI_YD",    	szYD_GP);			
			/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrMtlCTransDTSeqCardNoDesc_PIDEV*/
			intRtnVal = ydStockDao.getYdStock(recInTemp, rsGetStock, 126);
			if(intRtnVal <= 0){
				if(intRtnVal == 0){
					szMsg="[" + szOperationName + "] YD_STOCK[저장품] SELECT Error ::  DO NOT EXIST";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}else{
					szMsg="[" + szOperationName + "] YD_STOCK[저장품] SELECT Error :: [" + intRtnVal + "] PARAMETER ERROR" ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
			}
			szMsg="[" + szOperationName + "] 차량정지위치를 구하기 위한 저장품의 저장위치 조회 성공 - 건수["+intRtnVal+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			rsGetStock.first();
			recStlNo = rsGetStock.getRecord();
			
			szMsg="[" + szOperationName + "] recStlNo["+recStlNo+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recStlNo,"YD_STK_COL_GP");
			
			szMsg="[" + szOperationName + "] 차량정지위치를 구하기 위한 대상재가 존재하는 동["+szYD_STK_COL_GP+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szYD_GP 	= szYD_STK_COL_GP.substring(0, 1); 
 
			
			/*
			 * 4. 위의 저장위치정보를 가지고 차량이 입동가능한 차량정지위치를 조회한다
			 *	카드번호 : E,P,T 로 시작 ->PT  , 숫자 로 시작 ->TR
			 */
 
			
			//작업구분(1:내수/2:수출/3:연안해송/9:HYSCO스케줄)
			if(sWORK_GP.equals("9")){
				//########################################## 9:HYSCO스케줄 ####################################################
				if(szYD_GP.equals("H")){
					szTC_CODE = "YDYDJ660";													//코일임가공차량도착실적
					//szMsg= "["+ szOperationName +"] 야드["+szYD_GP+"]는 [코일HYSCO차량도착]처리.";
					szMsg= "["+ szOperationName +"] 야드["+szYD_GP+"]는 [코일소재야드 공냉장 차량도착]처리.";
				} else {
					
					szTC_CODE = "YDYDJ653";	
					szMsg= "["+ szOperationName +"] 야드["+szYD_GP+"]는 [코일출하차량도착]처리.";
				}
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

				
				rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
				recPara 	= JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CARPNT_CD"		, ydDaoUtils.paraRecChkNull(inRecord,"CARLD_PNT_CD"));
				
				//포인트코드 -> 개소코드와 저장위치 가져오기
				/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarPoint*/
				intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult1, 412);
				if(intRtnVal > 0){
					szMsg= "["+szMethodName+"] TB_YD_CARPOINT[차량포인트가 존재 합니다..]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
					
					rsResult1.first();
					recInTemp = rsResult1.getRecord();
					szWLOC_CD    		= StringHelper.evl(recInTemp.getFieldString("WLOC_CD"), "");
					szYD_PNT_CD    		= StringHelper.evl(recInTemp.getFieldString("YD_PNT_CD"), "");	
					szYD_STK_COL_ACT_STAT	= StringHelper.evl(recInTemp.getFieldString("YD_STK_COL_ACT_STAT"), "");
					
					szMsg= "["+ szOperationName +"] 차량스케줄의 출하차량[차량번호:" + szCAR_NO + ", 카드번호:" + szCARD_NO + ", 포인트 상태:" + szYD_STK_COL_ACT_STAT  ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
					
					// TT-Car가 예약인 경우 차량도착처리 호출이 가능 하도록 조치
					if ("TT".equals(sCAR_KIND) && "R".equals(szYD_STK_COL_ACT_STAT)) {
						szYD_STK_COL_ACT_STAT = "C";
					}					
					
					//TR가 도착해 있는 경우 
					if ("TR".equals(sCAR_KIND) && "L".equals(szYD_STK_COL_ACT_STAT)) {
						szYD_STK_COL_ACT_STAT = "C";
					}
					
					if("C".equals(szYD_STK_COL_ACT_STAT)){
					
						outRec = JDTORecordFactory.getInstance().create();
						outRec.setField("TC_CODE"			,szTC_CODE);
						outRec.setField("TC_CREATE_DDTT"	,new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
						outRec.setField("YD_GP"				,szYD_GP);
						outRec.setField("TRANS_ORD_DT"		,szTRANS_ORD_DATE);
						outRec.setField("TRANS_ORD_SEQNO" 	,szTRANS_ORD_SEQNO);
						outRec.setField("CAR_NO"			,szCAR_NO);
						outRec.setField("CARD_NO"			,szCARD_NO);
						outRec.setField("SPOS_WLOC_CD"		,szWLOC_CD);	
						outRec.setField("SPOS_YD_PNT_CD"	,szYD_PNT_CD);
						outRec.setField("CAR_KIND"			,sCAR_KIND);
						outRec.setField("WORK_GP"			,sWORK_GP);
						outRec.setField("IS_EJB_CALL"		,"N");	
						//EJB Call로 처리 or JMS 송신처리
						//일단 차량도착처리 호출은 JMS 송신으로 처리
						ydUtils.displayRecord(szOperationName, outRec);
						ydDelegate.sendMsg(outRec);
						
//						EJBConnector ejbConn1 = new EJBConnector("default","CarMvHdFaEJB",this);
//						ejbConn1.trx("rcvCoilGdsDistCarArrWr",new Class[]{JDTORecord.class},new Object[]{outRec});
					}
				}
				
				szMsg= "["+ szOperationName +"] 차량스케줄의 출하차량[차량번호:" + szCAR_NO + ", 카드번호:" + szCARD_NO + ", 운송지시일자:" + szTRANS_ORD_DATE + ", 운송지시순번:" + szTRANS_ORD_SEQNO + "]에 대해 차량도착 전문전송 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				//################################################################################################################
			} 

		}catch(Exception e){
			szMsg="["+szOperationName+"] Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;

		} // end of try-catch
		szMsg="["+szOperationName+"] 수신처리 ("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of procStandByYdArriveLdPDA()

	
	
	/**
	 * 코일이송하차도착PDA(DMYDR074)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @throws JDTOException
	 */
	public void procStandByYdArriveUdPDA(JDTORecord inRecord)throws JDTOException  {
 
		//DAO정의
		YdStockDao ydStockDao 					= new YdStockDao();				//저장품DAO
		ymCommonDAO dao = ymCommonDAO.getInstance();
		YdCarSchDao ydCarSchDao					= new YdCarSchDao();

		JDTORecord recStockColumn        		=  JDTORecordFactory.getInstance().create();
		JDTORecord recStlNo 					=  JDTORecordFactory.getInstance().create();
		JDTORecord	recInTemp					= JDTORecordFactory.getInstance().create();
 
		JDTORecord	recPara						= JDTORecordFactory.getInstance().create();
		JDTORecord	outRec						= JDTORecordFactory.getInstance().create();
		
		JDTORecordSet rsGetStock 				= null;
		JDTORecordSet rsResult	 				= null;
		JDTORecordSet rsResult1 				= null;
		String szMethodName 					= "procStandByYdArriveUdPDA";
		String szOperationName					= "코일이송하차도착PDA";
		String szMsg 							= "";
 
		String szYD_GP							= "";
		String szYD_BAY_GP						= "";
		String szYD_STK_COL_GP					= null;
		String szYD_CAR_SCH_ID					= "";
		String szWLOC_CD						= null;
		String szYD_STK_COL_ACT_STAT			= null;
		String szCAR_NO							= null;
		String szCARD_NO						= null;
		String szTRANS_ORD_DATE					= null;
		String szTRANS_ORD_SEQNO				= null;
		String szCR_FRTOMOVE_GP 				= "";
 
		String szYD_EQP_GP                      = "";
		String szYD_PNT_CD                      = "";
 
		
		int intRtnVal 							= 0;
		int i									= 0;
 
		String sWORK_GP 						= "";
		String sCAR_KIND 						= "";
		// 전문받아서 szRcvTcCode에 대입
		String szRcvTcCode = ydUtils.getTcCode(inRecord);

		// 수신한 전문이 null이라면 error
		if(szRcvTcCode==null || szRcvTcCode.equals("")){
			szMsg="["+szOperationName+"] TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
		
		szMsg="[" + szOperationName + "] 메소드 시작 - 전문내용 확인 시작";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		ydUtils.displayRecord(szOperationName, inRecord);
		
		
		try{
 
			rsGetStock 			= JDTORecordFactory.getInstance().createRecordSet("");
			sWORK_GP 			= ydDaoUtils.paraRecChkNull(inRecord,"WORK_GP");
			sCAR_KIND 			= ydDaoUtils.paraRecChkNull(inRecord,"CAR_KIND");
			szCAR_NO 			= ydDaoUtils.paraRecChkNull(inRecord,"CAR_NO");
			szCARD_NO 			= ydDaoUtils.paraRecChkNull(inRecord,"CARD_NO");
			szTRANS_ORD_DATE 	= ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_DT");
			szTRANS_ORD_SEQNO 	= ydDaoUtils.paraRecChkNull(inRecord,"TRANS_ORD_SEQNO"); 
			szYD_GP				= ydDaoUtils.paraRecChkNull(inRecord,"YD_GP"); 
			szCR_FRTOMOVE_GP	= ydDaoUtils.paraRecChkNull(inRecord,"CR_FRTOMOVE_GP"); 

			
			//수신한 전문값******************************************************************************************
			/*
			TRANS_ORD_DT	운송지시일자
			TRANS_ORD_SEQNO	운송지시순번
			CAR_NO			차량번호
			CARD_NO			카드번호
			*/
			/*
			 * 2. 저장품테이블의 재료들에 재료진도코드와 야드목표행선을 수정처리
			 */
			recStockColumn.setField("TRANS_ORD_DATE", 		szTRANS_ORD_DATE);
			recStockColumn.setField("TRANS_ORD_SEQNO", 		szTRANS_ORD_SEQNO);
			recStockColumn.setField("CAR_NO",				szCAR_NO);
			recStockColumn.setField("CARD_NO", 				szCARD_NO); 
			recStockColumn.setField("MODIFIER", 			szRcvTcCode);

	 		
			recStockColumn.setField("YD_AIM_RT_GP", "A1");
			recStockColumn.setField("STL_PROG_CD", "E"); 
			//****************************************************************************************************
		
						
			//저장품갱신******************************************************************************************** 
			intRtnVal = ydStockDao.updYdStock2(recStockColumn, 0);
			if(intRtnVal <= 0){
				szMsg= "["+szOperationName+"] YD_STOCK[코일제품상차지시등록] UPDATE Error :: [" + intRtnVal + "]" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			ydUtils.putLog(szSessionName, szMethodName,"["+szOperationName+"] [2] YD_STOCK[코일제품상차지시등록] UPDATE Success", YdConstant.DEBUG);
			//****************************************************************************************************

			/*
			 * 3. 저장품이 적치된 저장위치 정보를 조회 - 저장위치가 04, 05, 06스판이면 A통로, 07스판이면 B통로로 처리한다.
			 */ 
			szMsg="[" + szOperationName + "] 차량정지위치를 구하기 위한 저장품의 저장위치 조회 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//저장품 동 구하기 
			rsGetStock = JDTORecordFactory.getInstance().createRecordSet("");
			recInTemp = JDTORecordFactory.getInstance().create();
 			recInTemp.setField("TRANS_ORD_DATE"	, szTRANS_ORD_DATE);
			recInTemp.setField("TRANS_ORD_SEQNO", szTRANS_ORD_SEQNO);
 
			/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockTRANS_ORD_DAT*/
			intRtnVal = ydStockDao.getYdStock(recInTemp, rsGetStock, 31);
			if(intRtnVal <= 0){
				if(intRtnVal == 0){
					szMsg="[" + szOperationName + "] YD_STOCK[저장품] SELECT Error ::  DO NOT EXIST";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}else{
					szMsg="[" + szOperationName + "] YD_STOCK[저장품] SELECT Error :: [" + intRtnVal + "] PARAMETER ERROR" ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
			}
			szMsg="[" + szOperationName + "] 차량정지위치를 구하기 위한 저장품의 저장위치 조회 성공 - 건수["+intRtnVal+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			rsGetStock.first();
			recStlNo = rsGetStock.getRecord(); 
 
			szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(recStlNo, "YD_CAR_SCH_ID");
			
			szMsg="[" + szOperationName + "] 차량정지위치를 구하기 위한 대상재가 존재하는 동["+szYD_STK_COL_GP+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
 
			//작업구분(1:내수/2:수출/3:연안해송/9:HYSCO스케줄)
			if(sWORK_GP.equals("9")){
				//########################################## 9:HYSCO스케줄 ####################################################
				 
				szMsg= "["+ szOperationName +"] 야드["+szYD_GP+"]는 [코일출하차량도착]처리.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

				
				rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
				recPara 	= JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CARPNT_CD"		, ydDaoUtils.paraRecChkNull(inRecord,"CARUD_PNT_CD"));
				
				//포인트코드 -> 개소코드와 저장위치 가져오기
				/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarPoint*/
				intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult1, 412);
				if(intRtnVal > 0){
					szMsg= "["+szMethodName+"] TB_YD_CARPOINT[차량포인트가 존재 합니다..]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
					
					rsResult1.first();
					recInTemp = rsResult1.getRecord();
					szWLOC_CD    		= StringHelper.evl(recInTemp.getFieldString("WLOC_CD"), "");
					szYD_PNT_CD    		= StringHelper.evl(recInTemp.getFieldString("YD_PNT_CD"), "");	
					szYD_STK_COL_ACT_STAT	= StringHelper.evl(recInTemp.getFieldString("YD_STK_COL_ACT_STAT"), "");
					szYD_STK_COL_GP   		= StringHelper.evl(recInTemp.getFieldString("YD_STK_COL_GP"), "");
					
					szMsg= "["+ szOperationName +"] 차량스케줄의 출하차량[차량번호:" + szCAR_NO + ", 카드번호:" + szCARD_NO + ", 포인트 상태:" + szYD_STK_COL_ACT_STAT  ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
					
					
					
					String sQueryId = "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.updYdStacklayer";
					int  count = dao.updateData(sQueryId,new Object[]{szYD_STK_COL_GP , szYD_CAR_SCH_ID });
					 
					szMsg= "["+szMethodName+"] TB_YD_STKLYR [차상위치에 저장위치 등록 완료 count:]"+count;
					YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
					
					
					//차량스케줄에 차량진행상태 수정
			    	szMsg="[" + szOperationName + "] 차량스케줄["+szYD_CAR_SCH_ID+"]에 , 차량진행상태[B] 등록 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			    	recInTemp = JDTORecordFactory.getInstance().create();
			    	recInTemp.setField("YD_CAR_SCH_ID", 	   szYD_CAR_SCH_ID);
			    	recInTemp.setField("YD_CAR_PROG_STAT",     "B");
			    	//하차도착
			    	recInTemp.setField("YD_CARUD_ARR_DT", YdUtils.getCurDate("yyyyMMddHHmmss"));
			    	intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 0);
			    	if(intRtnVal <= 0){
						szMsg= "["+szOperationName+"]차량스케줄에 차량진행상태 수정 UPDATE Error :: [" + intRtnVal + "]" ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return;
					}
			    	
			    	
					
					// TT-Car가 예약인 경우 차량도착처리 호출이 가능 하도록 조치
					if ("TT".equals(sCAR_KIND) && "R".equals(szYD_STK_COL_ACT_STAT)) {
						szYD_STK_COL_ACT_STAT = "C";
					}					
					
					//TR가 도착해 있는 경우 
					if ("TR".equals(sCAR_KIND) && "L".equals(szYD_STK_COL_ACT_STAT)) {
						szYD_STK_COL_ACT_STAT = "C";
					}
					
					if("C".equals(szYD_STK_COL_ACT_STAT)){
					
						outRec = JDTORecordFactory.getInstance().create();
						outRec.setField("TC_CODE"			, "YDYDJ653");
						outRec.setField("TC_CREATE_DDTT"	,new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
						outRec.setField("YD_GP"				,szYD_GP);
						outRec.setField("TRANS_ORD_DT"		,szTRANS_ORD_DATE);
						outRec.setField("TRANS_ORD_SEQNO" 	,szTRANS_ORD_SEQNO);
						outRec.setField("CAR_NO"			,szCAR_NO);
						outRec.setField("CARD_NO"			,szCARD_NO);
						outRec.setField("SPOS_WLOC_CD"		,szWLOC_CD);	
						outRec.setField("SPOS_YD_PNT_CD"	,szYD_PNT_CD);
						outRec.setField("CAR_KIND"			,sCAR_KIND);
						outRec.setField("WORK_GP"			,sWORK_GP);
						outRec.setField("YD_CARLD_STOP_LOC",szYD_STK_COL_GP);						//차량상차정지위치
						outRec.setField("CR_FRTOMOVE_GP",szCR_FRTOMOVE_GP); 
						outRec.setField("IS_EJB_CALL"		,"N");	
						//EJB Call로 처리 or JMS 송신처리
						//일단 차량도착처리 호출은 JMS 송신으로 처리
						ydUtils.displayRecord(szOperationName, outRec);
						ydDelegate.sendMsg(outRec);
						
//						EJBConnector ejbConn1 = new EJBConnector("default","CarMvHdFaEJB",this);
//						ejbConn1.trx("rcvCoilGdsDistCarArrWr",new Class[]{JDTORecord.class},new Object[]{outRec});
					}
				}
				
				szMsg= "["+ szOperationName +"] 차량스케줄의 출하차량[차량번호:" + szCAR_NO + ", 카드번호:" + szCARD_NO + ", 운송지시일자:" + szTRANS_ORD_DATE + ", 운송지시순번:" + szTRANS_ORD_SEQNO + "]에 대해 차량도착 전문전송 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				//################################################################################################################
			} 

		}catch(Exception e){
			szMsg="["+szOperationName+"] Exception Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;

		} // end of try-catch
		szMsg="["+szOperationName+"] 수신처리 ("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of procStandByYdArriveUdPDA()
	

	/**
	 * 오퍼레이션명 : Y5 차량작업 예정정보 요구 (Y5YDL016)
	 * 						
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */		
	public void rcvY5DrvCarPlan(JDTORecord msgRecord) throws JDTOException {
		// 레코드 선언
		JDTORecord recPara         = null;
		JDTORecordSet rsResult     = null;
        JDTORecord getparamRecord  = null;  
        
        // DAO 객체 생성
		YdCarSchDao ydCarSchDao   = new YdCarSchDao();
		
		// 변수 선언
    	String szMethodName        = "rcvY5DrvCarPlan";
		String szMsg               = "";
		String szOperationName     = "차량작업 예정정보 요구";
		String szRcvTcCode         = null;
		int nRet                   = 0;
		int intRtnVal 			   = 0;
		
		szRcvTcCode = ydUtils.getTcCode(msgRecord);
		if(szRcvTcCode == null){
			szMsg = szSessionName + "::" + szMethodName+"() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		if(bDebugFlag){
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		try{
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "[열연 코일야드L2] 차량작업 예정정보 요구 수신";
			ydUtils.putLogMsg("H", YdConstant.YD_MONITORING_CHANNEL_H, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
			
			rsResult     = JDTORecordFactory.getInstance().createRecordSet("");
	        getparamRecord  = JDTORecordFactory.getInstance().create();
			
			// 파라미터 Check
			ydUtils.putLog(szSessionName, szMethodName, szMethodName + "::    [1] 파라미터 검사", YdConstant.DEBUG);
			nRet = this.paramY5YDL016Check(msgRecord, getparamRecord, 0);
	        if(nRet == -1) {
                szMsg = "파라미터 Check중 Error	: " + nRet;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
                return ;
	        }
	        
	        // YDY5L008 차량작업 예정정보송신을 위해 carNo get 
	        intRtnVal = ydCarSchDao.getYdCarsch(getparamRecord, rsResult, 436);
	        rsResult.first();
			recPara = rsResult.getRecord();
			String szChkCarNum = ydDaoUtils.paraRecChkNull(recPara, "CAR_NO");
			
	        if(intRtnVal < 0 || "".equals(szChkCarNum.trim())){ 
				szMsg= "["+szOperationName+"] TB_YD_CARSCH[해당 위치에 차량이 없습니다.]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
			}
				
			szMsg = "[JSP Session] PT_LOAD_LOC ="+ydDaoUtils.paraRecChkNull(getparamRecord, "PT_LOAD_LOC");
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			recPara.setField("YD_CARUD_STOP_LOC"      , ydDaoUtils.paraRecChkNull(getparamRecord, "PT_LOAD_LOC"));
			
	        // YDY5L008(차량작업 예정정보송신) 호출
			EJBConnector ejbConn = new EJBConnector("default", this);
			szMsg = (String)ejbConn.trx("CoilCraneLdHdSeEJB", "callYDY5L008", recPara);
			
//				CoilCraneLdHdSeEJBBean coilCraneLdHdSeEJBBean = new CoilCraneLdHdSeEJBBean(); 
//				coilCraneLdHdSeEJBBean.callYDY5L008(recPara);
			
			if( YdConstant.RETN_CD_SUCCESS.equals(szMsg) ) {
				szMsg = "[JSP Session](차량작업 예정정보송신) 호출 성공";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}else{
				szMsg = "[JSP Session](차량작업 예정정보송신) 호출 실패";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}
			
	        
	        
	    }catch(JDTOException e) {
            szMsg = "JDTOError : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
            return ;	   
	    }catch(Exception e) {
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
            return ;
	    } 
	} 
	

	/**
     * 오퍼레이션명 : 차량작업 예정정보 요구 수신 파라미터 체크 [hun 2015.06.29]
     * 
     * @param  ● msgRecord, outRecord, intGp
     * @return ● nRtnVal
     * @throws ● JDTOException
     */	
	public int paramY5YDL016Check(JDTORecord msgRecord, JDTORecord outRecord, int intGp) throws JDTOException {
		// 레코드 선언
		JDTORecord setRecord = null;
        
		// 변수 선언
        String szMethodName  = "paramY5YDL016Check";
    	String szMsg         = "" ;
		
        
        try{
        	setRecord = JDTORecordFactory.getInstance().create();
        	
        	// 레코드 값 체크 
			setRecord.setField("PT_LOAD_LOC"      , ydDaoUtils.paraRecChkNull(msgRecord, "PT_LOAD_LOC"));
			
			// 레퍼런스 레코드인자에 설정
			outRecord.setRecord(setRecord);
        }catch(Exception e){
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
			return -1;
        } 
        
		return 1;
	} 
	
	

	/**
     * 오퍼레이션명 : 상차도 작업불가 수신 파라미터 체크 [hun 2015.06.29]
     * 
     * @param  ● msgRecord, outRecord, intGp
     * @return ● nRtnVal
     * @throws ● JDTOException
     */	
	public int paramY5YDL017Check(JDTORecord msgRecord, JDTORecord outRecord, int intGp) throws JDTOException {
		// 레코드 선언
		JDTORecord setRecord = null;
        
		// 변수 선언
        String szMethodName  = "paramY5YDL017Check";
    	String szMsg         = "" ;
		
        
        try{
        	setRecord = JDTORecordFactory.getInstance().create();
        	
        	// 레코드 값 체크 
        	if("Y".equals(ydDaoUtils.paraRecChkNull(msgRecord, "USE_YN"))){
        		setRecord.setField("YD_STK_COL_ACT_STAT"      , "L");
        	}else{
        		setRecord.setField("YD_STK_COL_ACT_STAT"      , "N");
        	}
			setRecord.setField("PT_LOAD_LOC"		      , ydDaoUtils.paraRecChkNull(msgRecord, "PT_LOAD_LOC"));
			
			// 레퍼런스 레코드인자에 설정
			outRecord.setRecord(setRecord);
        }catch(Exception e){
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
			return -1;
        } 
        
		return 1;
	} 



    /**
     * 오퍼레이션명 : C열연 야드 차량작업 예정정보 update
     * 
     * @param  ● msgRecord, intGp
     * @return ● nRtnVal
     * @throws ● JDTOException
     */	
    public int Y5YDL017UpdYdEqp(JDTORecord msgRecord, int intGp) throws JDTOException {
    	
    	// 변수 선언
        String szMethodName  = "Y5YDL017UpdYdEqp";
        String szOperationName  = "C열연 야드 차량작업 예정정보";
    	String szMsg         = "" ;	
    	String szYD_STK_COL_ACT_STAT = "";
    	String szPT_LOAD_LOC = "";
    	int intRtnVal = 0;
    	ymCommonDAO dao = ymCommonDAO.getInstance();
    		
		try {
			
			
			szYD_STK_COL_ACT_STAT	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_ACT_STAT");
			szPT_LOAD_LOC   		= ydDaoUtils.paraRecChkNull(msgRecord, "PT_LOAD_LOC");
			
			String sQueryId = "com.inisteel.cim.ym.dao.ydCarPointdao.ydCarPointDao.updYdCarPointActStat";
			intRtnVal = dao.updateData(sQueryId,new Object[]{szYD_STK_COL_ACT_STAT , szPT_LOAD_LOC });
			
			szMsg= "["+szMethodName+"] TB_YD_STKLYR [차상위치에 저장위치 등록 완료 count:]"+intRtnVal;
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			
			
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return intRtnVal; 
    	
    } 

    

	/**
	 * 오퍼레이션명 : 상차도 작업 불가 (Y5YDL017)
	 * 						
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */		
	public void rcvY5CarNotWrk(JDTORecord msgRecord) throws JDTOException {
		// 레코드 선언  
        JDTORecord getparamRecord  = null;  
        JDTORecord setCarPointRecord = null;   
         
		// 변수 선언
    	String szMethodName      = "rcvY5CarNotWrk";
		String szMsg                 = "";
		String szOperationName   = "상차도 작업 불가";
		String szRcvTcCode         = null;
		String szYdEqpCd			= null;
		String szUseYn				= "";
		int nRet                   	= 0;
		
		szRcvTcCode = ydUtils.getTcCode(msgRecord);
		if(szRcvTcCode == null){
			szMsg = szSessionName + "::" + szMethodName+"() TC Code Error (" + szRcvTcCode + ")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
		
		if(bDebugFlag){
			szMsg = "전문수신 : TCCODE=" + szRcvTcCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		try{
			szYdEqpCd = ydDaoUtils.paraRecChkNull(msgRecord, "PT_LOAD_LOC");
			szUseYn	  = ydDaoUtils.paraRecChkNull(msgRecord, "USE_YN");
			
			// 결속장 인터락 상태 수신 시
			if("HFFE02".equals(szYdEqpCd) || "HDFE03".equals(szYdEqpCd) || "HBFE05".equals(szYdEqpCd) ){
				//=============================================================
				// Log 테이블 등록 (결속장 인터락 상태)
				//=============================================================
				szMsg = "[열연 코일야드L2] 결속장 인터락 상태 수신";
				ydUtils.putLogMsg("H", YdConstant.YD_MONITORING_CHANNEL_H, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
				
				getparamRecord  = JDTORecordFactory.getInstance().create();
				getparamRecord.setField("YD_STK_COL_GP"        		, szYdEqpCd);
				getparamRecord.setField("MATL_SUP_MTD_GP"     	, szUseYn);
		        
		        nRet = this.Y5YDL017UpdYdHFLLock(getparamRecord, 0);
		        
		        if(nRet == -1){
	                szMsg = "결속장 인터락 업데이트 중  Error : (" + getparamRecord.getFieldString("YD_STK_COL_GP") + ")(" + getparamRecord.getFieldString("MATL_SUP_MTD_GP") + ")" + nRet;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	                return ;
		        }
			}else{
			
				//=============================================================
				// Log 테이블 등록 ( 차량작업 예정정보 요구)
				//=============================================================
				szMsg = "[열연 코일야드L2] 차량작업 예정정보 요구 수신";
				ydUtils.putLogMsg("H", YdConstant.YD_MONITORING_CHANNEL_H, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
				 
		        getparamRecord  = JDTORecordFactory.getInstance().create();
		        setCarPointRecord = JDTORecordFactory.getInstance().create(); 
				
				// 파라미터 Check
				ydUtils.putLog(szSessionName, szMethodName, szMethodName + "::    [1] 파라미터 검사", YdConstant.DEBUG);
				nRet = this.paramY5YDL017Check(msgRecord, getparamRecord, 0);
		        if(nRet == -1) {
	                szMsg = "파라미터 Check중 Error	: " + nRet;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	                return ;
		        }
		        
		        // 차량포인트 테이블에  업데이트 
		        // TB_YD_CARPOINT.YD_STK_COL_ACT_STAT 항목 C:Close(비활성화), L:적치 가능(활성), N:사용 불가
				ydUtils.putLog(szSessionName, szMethodName, szMethodName + "::    [2] 차량포인트 테이블 업데이트 처리", YdConstant.DEBUG);	        
		        setCarPointRecord.setField("PT_LOAD_LOC"        		, getparamRecord.getFieldString("PT_LOAD_LOC"));
		        setCarPointRecord.setField("YD_STK_COL_ACT_STAT"     	, getparamRecord.getFieldString("YD_STK_COL_ACT_STAT"));
		        
		        nRet = this.Y5YDL017UpdYdEqp(setCarPointRecord, 0);
		        
		      //TB_YD_CARPOINT 상태 변경
		        
				
		        if(nRet == -1){
	                szMsg = "차량포인트 업데이트 중  Error : (" + getparamRecord.getFieldString("PT_LOAD_LOC") + ")(" + getparamRecord.getFieldString("YD_STK_COL_ACT_STAT") + ")" + nRet;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	                return ;
		        }

			}
	            
	        
	    }catch(JDTOException e) {
            szMsg = "JDTOError : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
            return ;	   
	    }catch(Exception e) {
            szMsg = "Error : " + e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
            return ;
	    } 
	} 
	
	 /**
     * 오퍼레이션명 : 결속장 인터락 상태 update
     * 
     * @param msgRecord, intGp
     * @return intRtnVal
     * @throws JDTOException
     */	
    public int Y5YDL017UpdYdHFLLock(JDTORecord msgRecord, int intGp) throws JDTOException {
    	
    	// 변수 선언
        String szMethodName  		 = "Y5YDL017UpdYdHFLLock";
        String szOperationName 		 = "결속장 인터락 상태 수정";
    	String szMsg         			 = "" ;	
    	String szMATL_SUP_MTD_GP	 = "";
    	String szYD_STK_COL_GP		 = "";
    	int intRtnVal = 0;
    	ymCommonDAO dao = ymCommonDAO.getInstance();
    		
		try {
			
			
			szMATL_SUP_MTD_GP	= ydDaoUtils.paraRecChkNull(msgRecord, "MATL_SUP_MTD_GP");
			szYD_STK_COL_GP   		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
			
			String sQueryId = "com.inisteel.cim.ym.dao.ydCarPointdao.ydCarPointDao.Y5YDL017UpdYdHFLLock";
			intRtnVal = dao.updateData(sQueryId,new Object[]{szMATL_SUP_MTD_GP , szYD_STK_COL_GP });
			
			szMsg= "["+szMethodName+"] TB_YD_STKCOL [결속장 인터락 상태 수정 완료 count:]"+intRtnVal;
			YmCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			
			
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return intRtnVal; 
    	
    } 
	
  //---------------------------------------------------------------------------	
} // end of class CarLdLotRegSeEJBBean
