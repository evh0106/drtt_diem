package com.inisteel.cim.yd.ydWkAct.TcarMoveHd;

import java.util.List;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.dao.ydSchRuleDao.YdSchRuleDao;
import com.inisteel.cim.yd.common.dao.ydStkBedDao.YdStkBedDao;
import com.inisteel.cim.yd.common.dao.ydStkColDao.YdStkColDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.dao.ydTcarFtmvMtlDao.YdTcarFtmvMtlDao;
import com.inisteel.cim.yd.common.dao.ydTcarSchDao.YdTcarSchDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookDao.YdWrkbookDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookMtlDao.YdWrkbookMtlDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.rule.GetBreRule1;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdTcConst;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.YdDBAssist;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.dao.ydCarFtmvMtlDao.YdCarFtmvMtlDao;
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.dao.ydEqpDao.YdEqpDao;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO;
import com.inisteel.cim.yd.jsp.common.YDDataUtil;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;

/**
 * 대차이동처리 Session EJB
 *
 * @ejb.bean name="CoilTcarMvHdSeEJB" jndi-name="CoilTcarMvHdSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class CoilTcarMvHdSeEJBBean extends BaseSessionBean {
	
	// Session Name
	private String szSessionName=getClass().getName();
	
	private YdUtils ydUtils =new YdUtils();
	
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();
	
	private YdTcConst ydTcConst = new YdTcConst();
	
	private YdDBAssist ydDBAssist = new YdDBAssist();
	
	private YdDelegate ydDelegate = new YdDelegate();
	
	
	// [DEBUG] message flag
	private boolean bDebugFlag=true;
	
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	
	

	/**
	 *      [A] 오퍼레이션명 : 
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procTest(JDTORecord msgRecord)throws JDTOException  {
		
		
		String szMsg="";
		String szMethodName="procTest";
		
		

		String szRcvTcCode=ydUtils.getTcCode(msgRecord);
		if(szRcvTcCode==null){
			szMsg=szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			return;
		}
		if(bDebugFlag){
			szMsg="전문수신 : TCCODE=" +szRcvTcCode ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}

		
		
		//
		//
		//
		//
		//	toDo Something...
		//
		//
		//
		//
		//

		
		szMsg="Test정보수신 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	
	}// end of procTest()

	
	
	
	

	
	
//	/**
//	 * 오퍼레이션명 : C열연코일야드L2 대차이동실적 (Y5YDL011) 
//	 *  
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param msgRecord
//	 * @return Integer
//	 * @throws JDTOException
//	 */
//	public Integer procY5TcarMvWr(JDTORecord msgRecord)throws JDTOException  {
//		// DAO 및 UTIL 객체 선언
//		JDTORecordSet rsResult          = null;
//		JDTORecord    recTcarSch        = null;
//		JDTORecord recPara              = null;		
//		JDTORecord    recInTemp         = null;
//		JDTORecord    recOutTemp        = null;
//		YdTcarSchDao ydTcarSchDao = new YdTcarSchDao();
//		YdEqpDao     ydEqpDao     = new YdEqpDao();
//		JDTORecord outRecord  	= JDTORecordFactory.getInstance().create(); 
//		// 변수 선언
//	    String szMsg              		= "";
//	    String szMethodName       		= "procY5TcarMvWr";
//	    String sYD_EQP_ID              	= "";
//	    String sYD_TCAR_MOVE_GP        	= "";
//	    String sYD_TCAR_MOVE_DIR       	= "";
//	    String sYD_BAY_GP            	= "";
//	    String sYD_BAY_GP1            	= "";
//	    String sYD_BAY_GP2             	= "";
//	    String sYD_GP                  	= "";
//	    String sYD_STK_COL_GP          	= "";
//	    int nRet                        = 0;
//	    int intRtnVal 					= 0 ;
//	    String sEqpWrkStat             	= "";
//	    String sYD_MOVE_GP             	= "";
//	    String sRTN_CD                  = ""; 
//	    String sYD_EQP_STAT				= "";
//        String szRcvTcCode = ydUtils.getTcCode(msgRecord);
//        if(szRcvTcCode == null){
//        	szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
//        	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//        	return YdConstant.RETN_INT_TC_ERROR;
//        }
//
//        if(bDebugFlag){
//            szMsg="[DEBUG] 전문수신 : TCCODE=" +szRcvTcCode ;
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//        }
//        
//        ydUtils.putLog(szSessionName, szMethodName, "대차이동전문 :YD_EQP_ID -->"			+ ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID") , YdConstant.DEBUG);
//		ydUtils.putLog(szSessionName, szMethodName, "대차이동전문 :YD_MOVE_GP -->"			+ ydDaoUtils.paraRecChkNull(msgRecord, "YD_MOVE_GP") , YdConstant.DEBUG);
//		ydUtils.putLog(szSessionName, szMethodName, "대차이동전문 :YD_BAY_GP -->"			+ ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP") , YdConstant.DEBUG);
//		ydUtils.putLog(szSessionName, szMethodName, "대차이동전문 :YD_TCAR_CURR_BAY -->"	+ ydDaoUtils.paraRecChkNull(msgRecord, "YD_TCAR_CURR_BAY") , YdConstant.DEBUG);
//		ydUtils.putLog(szSessionName, szMethodName, "대차이동전문 :YD_TCAR_AIM_BAY -->"	+ ydDaoUtils.paraRecChkNull(msgRecord, "YD_TCAR_AIM_BAY") , YdConstant.DEBUG);
//		ydUtils.putLog(szSessionName, szMethodName, "대차이동전문 :YD_EQP_WRK_STAT -->"	+ ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_WRK_STAT") , YdConstant.DEBUG);
//	       
//		// 설비ID
//        sYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
//		if(sYD_EQP_ID.equals("")){
//			szMsg = "설비ID 값이 없습니다.";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			return YdConstant.RETN_INT_FAILURE;
//		}
//		
//		       
//		// 야드대차이동구분S: 출발,  M: 이동 중,  E: 도착
//		sYD_MOVE_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_MOVE_GP");
//		if(sYD_MOVE_GP.equals("")){
//			szMsg = "야드대차이동구분 값이 없습니다.";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			return YdConstant.RETN_INT_FAILURE;
//		}
//		sYD_BAY_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP");
//		if(sYD_BAY_GP.equals("")){
//			szMsg = "현재동 값이 없습니다.";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			return YdConstant.RETN_INT_FAILURE;
//		}
//		
//		// 야드대차이동방향
//		sYD_TCAR_MOVE_DIR = ydDaoUtils.paraRecChkNull(msgRecord, "YD_TCAR_MOVE_DIR");
//		if(sYD_TCAR_MOVE_DIR.equals("")){
//			szMsg = "야드대차이동방향 값이 없습니다.";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			return YdConstant.RETN_INT_FAILURE;
//		}
//		
//			    
//		// 현재동
//		sYD_BAY_GP1 = ydDaoUtils.paraRecChkNull(msgRecord, "YD_TCAR_CURR_BAY");
//		if(sYD_BAY_GP1.equals("")){
//			szMsg = "현재동 값이 없습니다.";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			return YdConstant.RETN_INT_FAILURE;
//		}
//
//			
//		// 목적동
//		sYD_BAY_GP2 = ydDaoUtils.paraRecChkNull(msgRecord, "YD_TCAR_AIM_BAY");	
//		if(sYD_BAY_GP2.equals("")){
//			szMsg = "목적동 값이 없습니다.";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			return YdConstant.RETN_INT_FAILURE;
//		}
//		
//		
//		// 야드
////		szYD_GP = szYD_EQP_ID.substring(0, 1);
//
//		// 적치열 구분
////		szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP1 + szYD_EQP_ID.substring(2, 6);
//
//		try{
//			
//			//=============================================================
//			//=============================================================
//			szMsg = "[열연 코일야드L2] 대차이동실적 수신";
//			ydUtils.putLogMsg("H", YdConstant.YD_MONITORING_CHANNEL_H, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
//
//			ydUtils.putLogMsg("J", YdConstant.YD_MONITORING_CHANNEL_J, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
//
//			/*
//			YD_EQP_ID 야드설비ID
//			YD_MOVE_GP 야드대차이동구분
//			YD_TCAR_MOVE_DIR 야드대차이동방향
//			YD_BAY_GP 야드동구분1 현재동
//			YD_TCAR_AIM_BAY 야드동구분2 목적동
//			*/
//
//	    	// 1.대차 공장 구분Check
//	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
//
//	    	recInTemp = JDTORecordFactory.getInstance().create();
//	    	recInTemp.setField("YD_EQP_ID", sYD_EQP_ID);
//	    	/*com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqp*/
//	    	intRtnVal = ydEqpDao.getYdEqp(recInTemp, rsResult, 0);
//	    	if(intRtnVal <= 0) {	    	 
//				szMsg = "설비ID 값이 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return YdConstant.RETN_INT_FAILURE;
//			
//	    	} 
//			rsResult.absolute(1);
//	    	recOutTemp = JDTORecordFactory.getInstance().create();
//	    	recOutTemp.setRecord(rsResult.getRecord());
//	        
//	    	sYD_GP 				= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_GP");
//	    	sYD_EQP_STAT		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_STAT");
//	    	
//	    	sYD_STK_COL_GP = sYD_GP + sYD_BAY_GP + sYD_EQP_ID.substring(2, 6); // 현재동
//	    	
//	    	msgRecord.setField("YD_GP", sYD_GP);
//	    	
//	    	
//	    	// 1.대차스케줄상태Check (공차출발,공차도착,영차출발,영차도착 Check)
//	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
//
//	    	recInTemp = JDTORecordFactory.getInstance().create();
//	    	recInTemp.setField("YD_EQP_ID", sYD_EQP_ID);
//	    	/*com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.getYdTcarschByYdEqpId*/
//	    	intRtnVal = ydTcarSchDao.getYdTcarsch(recInTemp, rsResult, 4);
//
//	    	
//	    	if(intRtnVal <= 0) {	    	 
//		    	sEqpWrkStat = "";
//	    	} else {
//				rsResult.absolute(1);
//		    	recOutTemp = JDTORecordFactory.getInstance().create();
//		    	recOutTemp.setRecord(rsResult.getRecord());
//		        //대차 스케줄이 공차인지 상차인지 Check한다.
//		    	sEqpWrkStat = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_WRK_STAT");
//	    		
//	    		
//	    	}
//	    	
//	    	//대차설비Table에 야드동구분을 update위한 Setting
//	    	recInTemp = JDTORecordFactory.getInstance().create();
//	    	//출발 실적인 경우
//	    	if(sYD_MOVE_GP.equals("S")) {
//	    		//설비Table에 야드동구분 상태를 Clear한다.
//				szMsg="설비Table에 야드동구분 상태를 Clear한다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		
//				recInTemp.setField("YD_EQP_ID"		, sYD_EQP_ID);
//				recInTemp.setField("YD_CURR_BAY_GP"	, sYD_BAY_GP);
//				recInTemp.setField("YD_EQP_STAT"	, "M"); // 이동중으로 SET
//				
//				EJBConnector ydEjbCon = new EJBConnector("default", this);
//				outRecord 	= (JDTORecord)ydEjbCon.trx("CoilTcarMvHdSeEJB", "updYdEqp", recInTemp);
//				sRTN_CD		= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
//				if ("0".equals(sRTN_CD)) {
//					return YdConstant.RETN_INT_FAILURE;
//				}	
//				
////				intRtnVal = ydEqpDao.updYdEqp(recInTemp, 0);
////	    		
////	    		
////	    		
////	    		if(intRtnVal <= 0) {
////	    			return YdConstant.RETN_INT_FAILURE;
////	    		}
//
//	    		//공대차 출발인경우
//	    		if(sEqpWrkStat.equals("U")) {
//	    			nRet = 1;
//	    		//영대차 출발인 경우	
//	    		}else if(sEqpWrkStat.equals("L")) {
//	    			nRet = 3;
//	    		}else{
//	    			nRet = 0;
//	    		}
//	    		
//	    	//도착 실적인 경우	
//	    	}else if (sYD_MOVE_GP.equals("E")) {
//	    		//설비Table에 야드동구분 상태를 Update한다.
//				szMsg="설비Table에 야드동구분 상태를 Update한다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//				
//				
//				if(sYD_EQP_STAT.equals("A")){
//					szMsg="대차설비 상태가 이미 도착 상태 입니다. 중복처리 skip";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//					return YdConstant.RETN_INT_FAILURE;
//				}
//				
//				//현재동 업데이트
//				recInTemp.setField("YD_EQP_ID"		, sYD_EQP_ID);
//				recInTemp.setField("YD_CURR_BAY_GP"	, sYD_BAY_GP);
//				recInTemp.setField("YD_EQP_STAT"	, "A"); // 도착으로 SET
//
//				EJBConnector ydEjbCon = new EJBConnector("default", this);
//				outRecord 	= (JDTORecord)ydEjbCon.trx("CoilTcarMvHdSeEJB", "updYdEqp", recInTemp);
//				sRTN_CD		= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
//				if ("0".equals(sRTN_CD)) {
//					return YdConstant.RETN_INT_FAILURE;
//				}	
//				
//				
////				intRtnVal = ydEqpDao.updYdEqp(recInTemp, 0);
////	    		if(intRtnVal <= 0) {
////	    			return YdConstant.RETN_INT_FAILURE;
////	    		}
//	    		
//	    		//공대차 도착인경우
//	    		if(sEqpWrkStat.equals("U")) {
//	    			nRet = 2;
//	    		//영대차 도착인 경우	
//	    		}else if(sEqpWrkStat.equals("L")) {
//	    			nRet = 4;
//	    		}else{
//	    			nRet = 0;
//	    		}
//		    	//이동중 실적인 경우	
//	    	}else if (sYD_MOVE_GP.equals("M")) {
//	    		//설비Table에 야드동구분 상태를 Update한다.
//				szMsg="설비Table에 야드동구분 상태를 Update한다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//				
//				//현재동 업데이트
//				recInTemp.setField("YD_EQP_ID"		, sYD_EQP_ID);
//				recInTemp.setField("YD_CURR_BAY_GP"	, sYD_BAY_GP);
//				recInTemp.setField("YD_EQP_STAT"	, "M"); // 이동중으로 SET
//
//				EJBConnector ydEjbCon = new EJBConnector("default", this);
//				outRecord 	= (JDTORecord)ydEjbCon.trx("CoilTcarMvHdSeEJB", "updYdEqp", recInTemp);
//				sRTN_CD		= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
//				if ("0".equals(sRTN_CD)) {
//					return YdConstant.RETN_INT_FAILURE;
//				}	
//				
////	    		intRtnVal = ydEqpDao.updYdEqp(recInTemp, 0);
////	    		if(intRtnVal <= 0) {
////	    			return YdConstant.RETN_INT_FAILURE;
////	    		}
//	    		nRet = 0;
//	    	}
//	    	if(nRet == 0 ) {
//	    		szMsg="[열연 코일야드L2]대차 이동 실적 처리 리턴값nRet: " + nRet;
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//				return YdConstant.RETN_INT_SUCCESS;
//	    	}
//	    	
//	    	switch (nRet) {
//    			case 1	:
//    				// 2.공차출발 Method호출
//    				intRtnVal = this.Y5UTcarStartWr(msgRecord, recOutTemp);
//    				break;
//	    		case 2	:
//	    			// 3.공차도착 Method호출
//	    			intRtnVal = this.Y5UTcarStopWr(msgRecord, recOutTemp);
//	    			break;
//	    		case 3	:
//	    			// 4.영차출발 Method호출
//	    			intRtnVal = this.Y5LTcarStartWr(msgRecord, recOutTemp);
//	    			break;
//	    		case 4	:
//	    			// 5.영차도착 Method호출
//	    			intRtnVal = this.Y5LTcarStopWr(msgRecord, recOutTemp);
//	    			break;
//	    	}
//	    	
//	    	if(intRtnVal < 1 ) {
//	    		szMsg="[열연 코일야드L2]대차 이동 실적 처리 리턴값 : " + intRtnVal;
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	    		return YdConstant.RETN_INT_FAILURE;
//	    	}
//	    	
//			//===========================================================================
//			// 적치대제원 전문을 L2로 전송
//			//===========================================================================
//			recPara = JDTORecordFactory.getInstance().create();
//			recPara.setField("YD_INFO_SYNC_CD" , "3");						          // 1:동,2:SPAN,3:열,4:BED
//			recPara.setField("YD_GP"           , sYD_GP);
//			recPara.setField("YD_STK_COL_GP"   , sYD_STK_COL_GP);
//			// 전문에 "S"면 출발이므로 "1" or "A" 를 넣어서 전문 호출하면 전문편집시 출발인 "S"로 송신
//			// 전문에 "E"면 도착이므로 "2" or "B" 를 넣어서 전문 호출하면 전문편집시 도착인 "A"로 송신
//			
//			if(sYD_TCAR_MOVE_GP.equals(YdConstant.YD_TCAR_MOVE_GP_LEAVE)){           //'S'
//				recPara.setField("YD_CAR_PROG_STAT", "1");						      // "1", "A" : 출발(S)    "2", "B" : 도착(A)        
//			} else if(sYD_TCAR_MOVE_GP.equals(YdConstant.YD_TCAR_MOVE_GP_ARRIVE)){	  //'E'	
//				recPara.setField("YD_CAR_PROG_STAT", "2");						     
//			} else if(sYD_TCAR_MOVE_GP.equals(YdConstant.YD_TCAR_MOVE_GP_MOVE)) {    //'M'
//				szMsg = "대차이동실적이므로 업무 종료 처리 YD_TCAR_MOVE_GP(" + sYD_TCAR_MOVE_GP + ")";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//			}
//
//			if(nRet == 1 || nRet == 2){
//				recPara.setField("YD_EQP_WRK_STAT" , YdUtils.fillSpZr("L", 1, 1));    // "L" : 공차(출하)(L), "U" : 영차(반입)(U)			
//			} else if(nRet == 3 || nRet == 4){
//				recPara.setField("YD_EQP_WRK_STAT" , YdUtils.fillSpZr("U", 1, 1)); 
//			} else {
//				szMsg = "공차도 아니고 영차도 아님";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);				
//			}
//
//			if(sYD_TCAR_MOVE_GP.equals("S") || sYD_TCAR_MOVE_GP.equals("E")){
//				if(nRet == 1 || nRet == 2 || nRet == 3 || nRet == 4){
//					YdCommonUtils.sndStrPosSpecToL2(recPara);
//				}
//			}
//			//===========================================================================	    	
//			
//		}catch(Exception e){
//	
//			szMsg="대차 이동 실적 처리 Error:" +e.getMessage();
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//			return YdConstant.RETN_INT_FAILURE;
//		}
//	
//	
//		szMsg="대차 이동 실적 처리("+szMethodName+") 완료";
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		return YdConstant.RETN_INT_SUCCESS;
//	} //end of procY5TcarMvWr()
	
	
	

//	/**
//	 * 오퍼레이션명 : C열연코일야드L2 대차도착실적 (Y5YDL018) 
//	 *  
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param msgRecord
//	 * @return Integer
//	 * @throws JDTOException
//	 */
//	public Integer procY5TcarArriveMvWr(JDTORecord msgRecord)throws JDTOException  {
//		// DAO 및 UTIL 객체 선언
//		YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();//작업예약 DAO
//		ymCommonDAO dao = ymCommonDAO.getInstance();
//		YdCarSchDao ydCarSchDao 	 = new YdCarSchDao();
//		YdCarFtmvMtlDao ydCarFtmvMtlDao 	= new YdCarFtmvMtlDao();
//		JPlateYdCommDAO ydCommDao = new JPlateYdCommDAO();
//		YdStkColDao ydStkColDao        = new YdStkColDao();
//		YdStkBedDao ydStkBedDao        = new YdStkBedDao();
//		YdStkLyrDao ydStkLyrDao        = new YdStkLyrDao();
//		YdStockDao ydStockDao = new YdStockDao();
//		
//		EJBConnector ejbConn = null;
//		
//		JDTORecordSet rsResult          = null;
//		JDTORecordSet rsGetSailNo       = null;
//		JDTORecordSet rsResult1          = null;
//		JDTORecordSet rsResult2          = null;
//		JDTORecordSet rsTemp          = null;
//		JDTORecordSet rsWrkbook22     = null;
//		JDTORecord    recTcarSch        = null;
//		JDTORecord recPara              = null;		
//		JDTORecord    recInTemp         = null;
//		JDTORecord    recOutTemp        = null;
//		JDTORecord		recParam	= null;
//		
//		JDTORecord inRecord     = JDTORecordFactory.getInstance().create();
//		JDTORecord outRecord21  = JDTORecordFactory.getInstance().create();
//		JDTORecord recParaCarSch  = JDTORecordFactory.getInstance().create();
//		JDTORecord recCarSch  = JDTORecordFactory.getInstance().create();
//		JDTORecord outRecordWbook  = JDTORecordFactory.getInstance().create();
//		JDTORecord recWrkkbookMtl  = JDTORecordFactory.getInstance().create();
//		JDTORecord inRec  = JDTORecordFactory.getInstance().create();
//		JDTORecord recTempPara  = JDTORecordFactory.getInstance().create();
//		
//		
//		YDDataUtil  yddatautil = new YDDataUtil();
//		// 변수 선언
//	    String szMsg              		= "";
//	    String szMethodName       		= "procY5TcarArriveMvWr";
//	    String szPT_LOAD_LOC              	= "";
//	    String szYD_STK_COL_ACT_STAT      	= "";
//	    String szCHK_WBOOK              	= "";
//	    String szCAR_NO              = "";
//	    String szCAR_NO_CD           = "";
//	    String szCAR_UPDN_GP         = "";
//	    String szCAR_UPDN_GP_CD      = "";
//	    String trnQueryId         	 = "";
//	    String szCAR_NO_GET       	 = "";
//	    String szYD_CAR_USETYPE_GP	 = "";
//	    String szYD_CARPNT_CD     	 = "";
//	    String szRtnMsg				 = "";
//	    int nRet                     = 0;
//	    int intRtnVal 			 	= 0 ;
//	    int intRtnValBook		 	= 0 ;
//	    int intRtnValCarSch	 		= 0 ;
//	    int intRtnValSailNo	 		= 0 ;
//	    String szYD_SCH_CD          = "";
//	    String szYD_SCH_CD1          = "";
//	    String szYD_SCH_CD2          = "";
//	    String sEqpWrkStat         	= "";
//	    String sYD_MOVE_GP         	= "";
//	    String sRTN_CD              = ""; 
//	    String sYD_EQP_STAT			= "";
//	    String sRTN_MSG				= "";
//	    String szYD_CAR_SCH_ID	= "";
//	    String szYD_WBOOK_ID	= "";
//	    String sQueryId	= "";
//	    String szSAILNO	= "";
//        String szRcvTcCode = ydUtils.getTcCode(msgRecord);
//        String szTRANS_ORD_DATE = "";
//        String szTRANS_ORD_SEQNO = "";
//        String szARR_WLOC_CD ="";
//        
//        if(szRcvTcCode == null){
//        	szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
//        	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//        	return YdConstant.RETN_INT_TC_ERROR;
//        }
//
//        if(bDebugFlag){
//            szMsg="[DEBUG] 전문수신 : TCCODE=" +szRcvTcCode ;
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//        }
//        
//        ydUtils.putLog(szSessionName, szMethodName, "대차도착전문 :PT_LOAD_LOC -->"			+ ydDaoUtils.paraRecChkNull(msgRecord, "PT_LOAD_LOC") , YdConstant.DEBUG);
//		ydUtils.putLog(szSessionName, szMethodName, "대차도착전문 :CAR_NO -->"			+ ydDaoUtils.paraRecChkNull(msgRecord, "CAR_NO") , YdConstant.DEBUG);
//		ydUtils.putLog(szSessionName, szMethodName, "대차도착전문 :CAR_UPDN_GP -->"			+ ydDaoUtils.paraRecChkNull(msgRecord, "CAR_UPDN_GP") , YdConstant.DEBUG);
//		   
//		// 상차도 위치
//        szPT_LOAD_LOC = ydDaoUtils.paraRecChkNull(msgRecord, "PT_LOAD_LOC");
//		if("".equals(szPT_LOAD_LOC)){
//			szMsg = "상차도 위치 값이 없습니다.";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			return YdConstant.RETN_INT_FAILURE;
//		}
//		// 차량번호
//		szCAR_NO = ydDaoUtils.paraRecChkNull(msgRecord, "CAR_NO");	
//		if("".equals(szCAR_NO)){
//			szMsg = "차량번호가 없습니다.";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			return YdConstant.RETN_INT_FAILURE;
//		}
////		if("7777".equals(szCAR_NO)){
////			szCAR_NO_CD = "7";
////		}else if("8888".equals(szCAR_NO)){
////			szCAR_NO_CD = "8";
////		}
//		
//		szCAR_NO_CD = szCAR_NO.substring(0 , 1);
//		
//		// 상하차 구분
//		szCAR_UPDN_GP = ydDaoUtils.paraRecChkNull(msgRecord, "CAR_UPDN_GP");	
//		if("".equals(szCAR_UPDN_GP)){
//			szMsg = "상하차 구분이 없습니다.";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			return YdConstant.RETN_INT_FAILURE;
//		}
////		상하차 구분 스케줄 코드
//		if("1".equals(szCAR_UPDN_GP)){
//			if("4".equals(szPT_LOAD_LOC.substring(5 , 6)) || "5".equals(szPT_LOAD_LOC.substring(5 , 6))){
//				szCAR_UPDN_GP_CD = "5";
//			}else{
//				szCAR_UPDN_GP_CD = "0";
//			}
//		}else if("2".equals(szCAR_UPDN_GP)){
//			if("4".equals(szPT_LOAD_LOC.substring(5 , 6)) || "5".equals(szPT_LOAD_LOC.substring(5 , 6))){
//				szCAR_UPDN_GP_CD = "6";
//			}else{
//				szCAR_UPDN_GP_CD = "1";
//			}
//		}
//		
//		try{
//			
//			
//			//포인트 체크
//    		recInTemp = dao.getCodeToName("com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdGetCarNoTypeByLoc" , new Object[]{szCAR_NO ,szPT_LOAD_LOC,szCAR_UPDN_GP,szPT_LOAD_LOC });
//			
//			szCAR_NO_GET    = StringHelper.evl(recInTemp.getFieldString("CAR_NO"), "");
//			szYD_CAR_USETYPE_GP = StringHelper.evl(recInTemp.getFieldString( "YD_CAR_USETYPE_GP"), ""); 
//			szYD_CARPNT_CD = StringHelper.evl(recInTemp.getFieldString("YD_CARPNT_CD"), "");
//			szYD_STK_COL_ACT_STAT = StringHelper.evl(recInTemp.getFieldString("YD_STK_COL_ACT_STAT"), "");
//			szCHK_WBOOK = StringHelper.evl(recInTemp.getFieldString("CHK_WBOOK"), "");
//    		
//			if(!"".equals(szCAR_NO_GET)){
//    			szMsg = szPT_LOAD_LOC+"동 개소지의 야드포인트가 사용불가. 현재차량 ["+szCAR_NO_GET+"]";
//    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//    			return YdConstant.RETN_INT_FAILURE;
//    		}
//			if(!"C".equals(szYD_STK_COL_ACT_STAT)){
//    			szMsg = szPT_LOAD_LOC+"동 개소지의 야드포인트가 사용불가.";
//    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//    			return YdConstant.RETN_INT_FAILURE;
//    		}
//			if("".equals(szCHK_WBOOK)){
//    			szMsg = szPT_LOAD_LOC+"동 개소지의 야드포인트가 사용불가 해당 작업예약이 없습니다.";
//    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//    			return YdConstant.RETN_INT_FAILURE;
//    		}
//    		szMsg = szPT_LOAD_LOC+"동 개소지의 야드포인트가 사용 가능.";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			
//			if(!"MT".equals(szYD_CAR_USETYPE_GP)){
//    			szMsg = szPT_LOAD_LOC+"동 개소지의 야드포인트가 사용불가. 현재 포인트 타입 ["+szYD_CAR_USETYPE_GP+"]";
//    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//    			return YdConstant.RETN_INT_FAILURE;
//    		}
//			szMsg = szPT_LOAD_LOC+"동 개소지의 야드포인트가 사용 가능.";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			
//			// 해당 포인트 점유
//			recInTemp = JDTORecordFactory.getInstance().create();
//			recInTemp.setField("TRN_EQP_CD", "");
//			recInTemp.setField("CAR_NO", szCAR_NO);
//			recInTemp.setField("CARD_NO", szCAR_NO);
//			recInTemp.setField("YD_MAKECARPNT_CD", szYD_CARPNT_CD);
//			recInTemp.setField("YD_STK_COL_GP", szPT_LOAD_LOC);
//			recInTemp.setField("YD_GP", szPT_LOAD_LOC.substring(0 , 1));
//			
//			EJBConnector 	ejbConnC = new EJBConnector("default", "CarMvHdSeEJB", this);	
//		    //차량 POINT TABLE 점유
//		    ejbConnC.trx("procUpdYdTransOrdChangeNEW", new Class[] { JDTORecord.class }, new Object[] { recInTemp });	
//		    //차량 스케줄 POINT 점유 		
//		    ejbConnC.trx("procUpdYdTransOrdChange", new Class[] { JDTORecord.class }, new Object[] { recInTemp });					
//			//YD 저장위치 맵 활성화
//		    ejbConnC.trx("procYdLayerOpen", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
//		    
//		    
//		    ydUtils.putLog(szSessionName, szMethodName, "상차포인트 변경", YdConstant.DEBUG);
//			
//			/* 위쪽 차량 스케줄 POINT 점유로 공통로직 반영
//		    //차량 sch update
//			recInTemp = JDTORecordFactory.getInstance().create();
//			//상차도 변경
//			recInTemp.setField("YD_CARLD_STOP_LOC"	, szPT_LOAD_LOC);
//			recInTemp.setField("YD_PNT_CD"			, szYD_CARPNT_CD);
//			recInTemp.setField("YD_CAR_SCH_ID"		, szYD_CAR_SCH_ID);
//			
//			/*com.inisteel.cim.yd.dao.ydcarschdao.ydCarSchDao.updYdCarschYdCoil2
//			intRtnVal = ydCarSchDao.updYdCarschYdCarWrkBookId(recInTemp, 304);
//			if(intRtnVal <= 0) {
//				ydUtils.putLog(szSessionName, szMethodName, "상차도 변경시 error", YdConstant.ERROR);
//				return YdConstant.RETN_INT_FAILURE;
//			}
//		    */
//		    
//			
//			/*
//			// szCAR_UPDN_GP = 1 상차 작업
//			 * 1. 차량 스케줄 생성
//			 * 2. 작업예약 선택
//			 * 3. 차량재료 등록
//			 * 4. 차량 예정정보 발송
//			 * 5. 크레인스케줄 
//			 */
//			if("1".equals(szCAR_UPDN_GP)){
//				szMsg = szPT_LOAD_LOC+"동 차량동간이적 상차 도착 szCAR_UPDN_GP="+szCAR_UPDN_GP;
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				
//
////				2.작업 예약 선택
////				2.1스케줄 코드 생성
//				szYD_SCH_CD1 = szPT_LOAD_LOC.substring(0 , 2)+"TR";
//				szYD_SCH_CD2 = szCAR_NO_CD+"MM";
////				2.2 생성한 스케줄코드로 작업 예약 select
//
//				
//				//스케줄 호출 (상차 가능 매수 만큼 스케줄 기능)
//				//재료번호
//				rsResult2 = JDTORecordFactory.getInstance().createRecordSet("Temp");
//				inRecord = JDTORecordFactory.getInstance().create();
//				inRecord.setField("YD_SCH_CD1",          	szYD_SCH_CD1);//스케줄코드
//				inRecord.setField("YD_SCH_CD2",          	szYD_SCH_CD2);//스케줄코드
//				sQueryId = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookBySchCd2";
//				intRtnValBook = ydCommDao.select(inRecord, rsResult2, sQueryId);
//				
//				rsResult2.first();
//				recOutTemp = JDTORecordFactory.getInstance().create();
//				recOutTemp		= rsResult2.getRecord();
//				
//				szYD_WBOOK_ID		= ydDaoUtils.paraRecChkNull(recOutTemp,"YD_WBOOK_ID");
//				szYD_SCH_CD		= ydDaoUtils.paraRecChkNull(recOutTemp,"YD_SCH_CD");
//				
//				//운송지시일자, 순번 생성  (988001 처럼 앞에  988를 붙인다.)
//				recParam = JDTORecordFactory.getInstance().create();
//				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
//				recOutTemp = JDTORecordFactory.getInstance().create();
//				
//				intRtnVal = ydCommDao.select(recParam, rsResult, "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getRetnTransOrdNoByCar");
//				
//				if(intRtnVal<1) {
//					szMsg="["+szMethodName+"] 운송지시일자,순번  생성시  오류발생 실패!! - intRtnVal : " + intRtnVal;
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//					throw new Exception(szMsg);
//				}
//				
//				rsResult.first();
//				recOutTemp		= rsResult.getRecord();
//				
//				szTRANS_ORD_DATE		= ydDaoUtils.paraRecChkNull(recOutTemp,"TRANS_ORD_DATE");
//				szTRANS_ORD_SEQNO		= ydDaoUtils.paraRecChkNull(recOutTemp,"TRANS_ORD_SEQNO");
//				
//				
//		    	recPara = JDTORecordFactory.getInstance().create();
//				recPara.setField("REGISTER",         szMethodName.length() > 10 ? szMethodName.subSequence(0, 10) : szMethodName);
//				recPara.setField("YD_EQP_WRK_STAT",  "U");										//야드설비작업상태
//				recPara.setField("YD_EQP_ID",        YdConstant.YD_TS_CAR_EQP_ID);				//야드설비ID
//				recPara.setField("CAR_NO",      	 szCAR_NO);							 		//운송장비코드
//				recPara.setField("CAR_KIND",      	 "TR");							 		//차량종류
//				recPara.setField("CARD_NO",      	 szCAR_NO);							 		//운송장비코드
//				recPara.setField("YD_CAR_USE_GP",    YdConstant.YD_CAR_USE_GP_DM);				//차량사용구분
//				recPara.setField("SPOS_WLOC_CD",     "DJY1E");								//발지개소코드
//				recPara.setField("ARR_WLOC_CD",      "DJY1E");								//착지개소코드
//				recPara.setField("YD_CARLD_LEV_LOC", szPT_LOAD_LOC);						//야드상차출발위치
//				recPara.setField("YD_CARLD_LEV_DT",  YdUtils.getCurDate("yyyyMMddHHmmss"));		//상차출발일시
//				recPara.setField("YD_BAYIN_WO_SEQ",  YdConstant.YD_BAYIN_WO_SEQ_DEFAULT);		//입동지시순번 - 기본값으로 설정(9)
//				recPara.setField("YD_CAR_PROG_STAT", YdConstant.YD_CARLD_ARR);					//상차출발상태
//				recPara.setField("YD_CARLD_STOP_LOC", szPT_LOAD_LOC);						//야드상차정지위치 (직상차 제외)
//				
////				recPara.setField("YD_CARLD_ST_DT",  YdUtils.getCurDate("yyyyMMddHHmmss"));
//				recPara.setField("YD_CARLD_ARR_DT",  YdUtils.getCurDate("yyyyMMddHHmmss"));
//				recPara.setField("TRANS_ORD_DATE", szTRANS_ORD_DATE);
//				recPara.setField("TRANS_ORD_SEQNO", szTRANS_ORD_SEQNO);
//				recPara.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);       // 상차 작업예약ID
//				recPara.setField("YD_PNT_CD1", szYD_CARPNT_CD);
//				
//				szRtnMsg = YdCommonUtils.mkCarSch(recPara);
//				
//				if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
//					
//					szMsg="[개소코드구분 및 상차Lot편성 호출처리]  차량스케줄 생성 시 오류발생 - 반환값 : " + szRtnMsg;
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//					
//					return YdConstant.RETN_INT_FAILURE;
//				}
//				
//				
//				
//				/*com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getSailNoWrkbookBySchCd*/ 
//	    		rsGetSailNo = JDTORecordFactory.getInstance().createRecordSet("Temp");
//	    		inRecord = JDTORecordFactory.getInstance().create();
//				inRecord.setField("YD_SCH_CD",          	szYD_SCH_CD);//스케줄코드
//				sQueryId = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getSailNoWrkbookBySchCd";
//				intRtnValSailNo = ydCommDao.select(inRecord, rsGetSailNo, sQueryId);
//				
//				if(intRtnValSailNo <= 0 )
//				{
//					szMsg="상차가능 개수 select 오류발생 - 반환값 : " + intRtnValSailNo;
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//					return YdConstant.RETN_INT_FAILURE;
//				}
//				
//				
//				recTempPara = JDTORecordFactory.getInstance().create();
//				// Temp Data inDto에 다시 세팅 
//				rsGetSailNo.absolute(1);
//				recTempPara.setRecord(rsGetSailNo.getRecord());
//	    		
//				szSAILNO = yddatautil.setDataDefault(recTempPara.getField("SAILNO"), "0");
//				 				
//// 				상차가능 수량이 더 많을때는 남은 재료카운트로 for 문
// 				if(Integer.parseInt(szSAILNO)>intRtnValBook){
//					szSAILNO = ""+intRtnValBook;
//				}
// 				
//				// 차량스케줄 조회
// 				rsResult1 = JDTORecordFactory.getInstance().createRecordSet("");
//				inRecord = JDTORecordFactory.getInstance().create();
//				inRecord.setField("CAR_NO",     szCAR_NO);
//				inRecord.setField("CARD_NO",     szCAR_NO);
//				
//		    	intRtnVal = this.Y0GetYdCarsch(inRecord, rsResult1, 11) ;
//		    	if (intRtnVal <= 0){
//		    		szMsg = "차량스케쥴 정보 오류발생.";
//		            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
//		            throw new  JDTOException(szMsg);
//		    	}
//		    	rsResult1.absolute(1);
//				recCarSch = rsResult1.getRecord();
//				
//				if(intRtnVal>0){
//					
//					// 상차 가능 매수  수만큼 루프
//					for(int Loop_i = 1; Loop_i <= Integer.parseInt(szSAILNO) ; Loop_i++) {
//						rsGetSailNo.absolute(Loop_i);
//						outRecordWbook = rsGetSailNo.getRecord();
////						작업예약재료 레코드
//						rsWrkbook22 = JDTORecordFactory.getInstance().createRecordSet("");
//		 				intRtnVal = ydWrkbookDao.getYdWrkbook(outRecordWbook, rsWrkbook22, 3);
//
////						작업예약 레코드
//						rsWrkbook22.absolute(1);
//						recWrkkbookMtl = rsWrkbook22.getRecord();
//						
////				2.3 차량재료 등록
//						//------------------------------------------------------------------------------------------------------
//						// 차량이송재료 등록
//						//------------------------------------------------------------------------------------------------------
//						szYD_CAR_SCH_ID = recCarSch.getFieldString("YD_CAR_SCH_ID");
//						
//						inRecord = JDTORecordFactory.getInstance().create();
//						inRecord.setField("YD_CAR_SCH_ID",  szYD_CAR_SCH_ID);//스케줄코드
//						inRecord.setField("STL_NO",   	recWrkkbookMtl.getFieldString("STL_NO"));
//						inRecord.setField("REGISTER", "Y5YDL018");
//						inRecord.setField("YD_STK_BED_NO", "0"+Loop_i);
//						inRecord.setField("YD_STK_LYR_NO", "001");
//						
//						intRtnVal = ydCarFtmvMtlDao.insYdCarftmvmtl(inRecord);
//				
//					}
//				}
////				2.4 차량예정정보 전송
//				szMsg = "차량정보 예정정보 전송 start";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//				
//				recInTemp = JDTORecordFactory.getInstance().create();
//		    	recInTemp.setField("YD_CAR_SCH_ID",    szYD_CAR_SCH_ID);
//		    	szRtnMsg = this.callYDY5L008(recInTemp);
//	        	
//	        	if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {													//성공
//	        		szMsg = "[JSP Session](차량작업 예정정보송신) 호출 성공";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//				}else{
//					szMsg = "[JSP Session](차량작업 예정정보송신) 호출 실패";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				}
//				
////				5.크레인스케줄기동
//				if(intRtnValSailNo>0){
//				
//					// 상차 가능 매수  수만큼 루프
//					for(int Loop_i = 1; Loop_i <= Integer.parseInt(szSAILNO) ; Loop_i++) {
//						rsResult2.absolute(Loop_i);
//						inRecord = rsResult2.getRecord();
//						
//						JDTORecord[] inRecordarr   	= null;
//						inRecordarr = new JDTORecord[1];
//						
//						inRecordarr[0] = JDTORecordFactory.getInstance().create();
//						inRecordarr[0].setField("YD_SCH_CD"		, szYD_SCH_CD); 
//						inRecordarr[0].setField("YD_WBOOK_ID"	, ydDaoUtils.paraRecChkNull(inRecord,"YD_WBOOK_ID")); 
//						
//						ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
//						outRecord21 = (JDTORecord) ejbConn.trx("trxRunSchedule", new Class[] { JDTORecord[].class },	new Object[] { inRecordarr });
//			
//						sRTN_CD		= StringHelper.evl(outRecord21.getFieldString("RTN_CD"), "0");
//						sRTN_MSG	= StringHelper.evl(outRecord21.getFieldString("RTN_MSG"), "");
//						ydUtils.putLog(szSessionName, szMethodName, "//" + sRTN_MSG, YdConstant.DEBUG);
//						if (!("1".equals(sRTN_CD))) {
//							return YdConstant.RETN_INT_FAILURE;	
//						} else {
//							szMsg = szPT_LOAD_LOC+" <br> 정상적으로 스케쥴까지 등록했습니다.";
//							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//							
//						} 
//					}
//				}
//				
//				
//			}else{
//				// szCAR_UPDN_GP = 2 하차 작업
//				szMsg = szPT_LOAD_LOC+"동 차량동간이적 하차 도착 szCAR_UPDN_GP="+szCAR_UPDN_GP;
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				
//				
//				
//				// 적치열 테이블에 활성상태 처리
//				recInTemp = JDTORecordFactory.getInstance().create();
//				recInTemp.setField("YD_STK_COL_GP" , szPT_LOAD_LOC);
//				recInTemp.setField("YD_STK_COL_ACT_STAT" , "L");
//				recInTemp.setField("YD_CAR_USE_GP" , "G");
//				recInTemp.setField("TRN_EQP_CD" , "");
//				recInTemp.setField("CAR_NO" , szCAR_NO);
//				recInTemp.setField("CARD_NO" , szCAR_NO);
//
//				intRtnVal = ydStkColDao.updYdStkcol(recInTemp , 0);
//				
//				
//				
//				recInTemp = JDTORecordFactory.getInstance().create();
//				recInTemp.setField("YD_STK_COL_GP", szPT_LOAD_LOC);
//				recInTemp.setField("YD_STK_LYR_ACT_STAT", "E");
//				recInTemp.setField("STL_NO", "");
//				recInTemp.setField("YD_STK_LYR_MTL_STAT", "E");
//		    	
//				intRtnVal = ydStkLyrDao.updYdStklyrYdStkColGp(recInTemp);
//				if(intRtnVal <= 0) {
//					szMsg = "[CarMvHdSeEJBBean - procOutCarArrWr] updYdStklyrYdStkColGp 적치단 정보 활성화중 Error!! ";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//					throw new DAOException("<CarMvHdSeEJBBean> updYdStklyrYdStkColGp " + szMsg);
//				}
//				
//				
//				
//				
//				
//				
//				
//				
//
//				/*
//				// 하차 작업
//				 * 1. 차량 스케줄 생성
//				 * 2. 작업예약 선택
//				 * 3. 차량재료 등록
//				 * 4. 차량 예정정보 발송
//				 * 5. 크레인스케줄 
//				 */
//
////				2.작업 예약 선택
////				2.1스케줄 코드 생성
//				szYD_SCH_CD = szPT_LOAD_LOC.substring(0 , 2)+"TR"+szCAR_UPDN_GP_CD+szCAR_NO_CD+"MM";
////				2.2 생성한 스케줄코드로 작업 예약 select
//
//				
//				//작업 예약 조회
//				rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
//				inRecord = JDTORecordFactory.getInstance().create();
//				inRecord.setField("YD_SCH_CD",          	szYD_SCH_CD);//스케줄코드
//				/*com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookBySchCd*/
//				intRtnValBook = ydWrkbookDao.getYdWrkbook(inRecord, rsResult2, 8);
//				rsResult2.first();
//				recOutTemp		= rsResult2.getRecord();
//				
//				szYD_WBOOK_ID		= ydDaoUtils.paraRecChkNull(recOutTemp,"YD_WBOOK_ID");
//				
//
//				//운송지시일자, 순번 생성  (988001 처럼 앞에  988를 붙인다.)
//				recParam = JDTORecordFactory.getInstance().create();
//				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
//				recOutTemp = JDTORecordFactory.getInstance().create();
//				
//				intRtnVal = ydCommDao.select(recParam, rsResult, "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getRetnTransOrdNoByCar");
//				
//				if(intRtnVal<1) {
//					szMsg="["+szMethodName+"] 운송지시일자,순번  생성시  오류발생 실패!! - intRtnVal : " + intRtnVal;
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//					throw new Exception(szMsg);
//				}
//				
//				rsResult.first();
//				recOutTemp		= rsResult.getRecord();
//				
//				szTRANS_ORD_DATE		= ydDaoUtils.paraRecChkNull(recOutTemp,"TRANS_ORD_DATE");
//				szTRANS_ORD_SEQNO		= ydDaoUtils.paraRecChkNull(recOutTemp,"TRANS_ORD_SEQNO");
//				
//				if("HA".equals(szPT_LOAD_LOC.substring(0 , 2))
//					||"HB".equals(szPT_LOAD_LOC.substring(0 , 2))
//					||"HC".equals(szPT_LOAD_LOC.substring(0 , 2))
//					||"HD".equals(szPT_LOAD_LOC.substring(0 , 2))
//					||"HE".equals(szPT_LOAD_LOC.substring(0 , 2))
//				){
//					szARR_WLOC_CD ="DJY21";
//				}else if("HH".equals(szPT_LOAD_LOC.substring(0 , 2))){
//					szARR_WLOC_CD ="DJY22";
//				}else {
//					szARR_WLOC_CD ="DJY1E";
//				}
//				
//				recPara = JDTORecordFactory.getInstance().create();
//				recPara.setField("REGISTER",         szMethodName.length() > 10 ? szMethodName.subSequence(0, 10) : szMethodName);
//				recPara.setField("YD_EQP_WRK_STAT",  "L");										//야드설비작업상태
//				recPara.setField("YD_EQP_ID",        YdConstant.YD_TS_CAR_EQP_ID);				//야드설비ID
//				recPara.setField("CAR_NO",      	 szCAR_NO);							 		//운송장비코드
//				recPara.setField("CARD_NO",      	 szCAR_NO);							 		//운송장비코드
//				recPara.setField("CAR_KIND",      	 "TR");							 		//차량종류
//				recPara.setField("YD_CAR_USE_GP",    YdConstant.YD_CAR_USE_GP_DM);				//차량사용구분
//				recPara.setField("SPOS_WLOC_CD",     "DJY1E");								//발지개소코드
//				recPara.setField("ARR_WLOC_CD",      szARR_WLOC_CD);						//착지개소코드
//				recPara.setField("YD_CARLD_LEV_LOC", szPT_LOAD_LOC);						//야드하차출발위치
//				recPara.setField("YD_CARUD_LEV_DT",  YdUtils.getCurDate("yyyyMMddHHmmss"));		//하차출발일시
//				recPara.setField("YD_BAYIN_WO_SEQ",  YdConstant.YD_BAYIN_WO_SEQ_DEFAULT);		//입동지시순번 - 기본값으로 설정(9)
//				recPara.setField("YD_CAR_PROG_STAT", YdConstant.YD_CARUD_ARR);					//상태
//				recPara.setField("YD_CARUD_STOP_LOC", szPT_LOAD_LOC);						//야드하차정지위치
////				recPara.setField("YD_CARUD_ST_DT",  YdUtils.getCurDate("yyyyMMddHHmmss"));
//				recPara.setField("YD_CARUD_ARR_DT",  YdUtils.getCurDate("yyyyMMddHHmmss"));
//				recPara.setField("YD_PNT_CD3", szYD_CARPNT_CD);
//
//				recPara.setField("TRANS_ORD_DATE", szTRANS_ORD_DATE);
//				recPara.setField("TRANS_ORD_SEQNO", szTRANS_ORD_SEQNO);
//				recPara.setField("YD_CARUD_WRK_BOOK_ID", szYD_WBOOK_ID);       // 상차 작업예약ID
//				
//				szRtnMsg = YdCommonUtils.mkCarSch(recPara);
//				
//				if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
//					
//					szMsg="[개소코드구분 및 상차Lot편성 호출처리]  차량스케줄 생성 시 오류발생 - 반환값 : " + szRtnMsg;
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//					
//					return YdConstant.RETN_INT_FAILURE;
//				}
//				
//				
//
//
//				/*com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getSailNoWrkbookBySchCd*/ 
//	    		rsGetSailNo = JDTORecordFactory.getInstance().createRecordSet("Temp");
//	    		inRecord = JDTORecordFactory.getInstance().create();
//				inRecord.setField("YD_SCH_CD",          	szYD_SCH_CD);//스케줄코드
//				sQueryId = "com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getSailNoWrkbookBySchCd";
//				intRtnValSailNo = ydCommDao.select(inRecord, rsGetSailNo, sQueryId);
//				
//				if(intRtnValSailNo <= 0 )
//				{
//					szMsg="상차가능 개수 select 오류발생 - 반환값 : " + intRtnValSailNo;
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//					return YdConstant.RETN_INT_FAILURE;
//				}
//				
//				rsGetSailNo.absolute(1);
//				recTempPara = JDTORecordFactory.getInstance().create();
//				// Temp Data inDto에 다시 세팅 
//				recTempPara.setRecord(rsGetSailNo.getRecord());
//	    		
//				szSAILNO = yddatautil.setDataDefault(recTempPara.getField("SAILNO"), "0");
//				
////				작업예약 레코드
//				rsResult2.absolute(1);
//				outRecordWbook = rsResult2.getRecord();
//				
////				작업예약재료 레코드 151104
//				rsWrkbook22 = JDTORecordFactory.getInstance().createRecordSet("");
// 				intRtnVal = ydWrkbookDao.getYdWrkbook(outRecordWbook, rsWrkbook22, 3);
// 				
// 				
//// 				상차가능 수량이 더 많을때는 남은 재료카운트로 for 문
// 				if(Integer.parseInt(szSAILNO)>intRtnValSailNo){
//					szSAILNO = ""+intRtnValSailNo;
//				}
//				
// 				// 차량스케줄 조회
// 				rsResult1 = JDTORecordFactory.getInstance().createRecordSet("");
//				inRecord = JDTORecordFactory.getInstance().create();
//				inRecord.setField("CAR_NO",     szCAR_NO);
//				inRecord.setField("CARD_NO",     szCAR_NO);
//				
//		    	intRtnVal = this.Y0GetYdCarsch(inRecord, rsResult1, 11) ;
//		    	if (intRtnVal <= 0){
//		    		szMsg = "차량스케쥴 정보 오류발생.";
//		            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
//		            throw new  JDTOException(szMsg);
//		    	}
//		    	rsResult1.absolute(1);
//				recCarSch = rsResult1.getRecord();
// 				
//				if(intRtnVal>0){
//					
//					// 상차 가능 매수  수만큼 루프
//					for(int Loop_i = 1; Loop_i <= Integer.parseInt(szSAILNO) ; Loop_i++) {
//						rsWrkbook22.absolute(Loop_i);
//		 				recWrkkbookMtl = rsWrkbook22.getRecord();
//						
//						
////				2.3 차량재료 등록
//						//------------------------------------------------------------------------------------------------------
//						// 차량이송재료 등록
//						//------------------------------------------------------------------------------------------------------
//						szYD_CAR_SCH_ID = recCarSch.getFieldString("YD_CAR_SCH_ID");
//						
//						inRecord = JDTORecordFactory.getInstance().create();
//						inRecord.setField("YD_CAR_SCH_ID",  szYD_CAR_SCH_ID);//스케줄코드
//						inRecord.setField("STL_NO",   	recWrkkbookMtl.getFieldString("STL_NO"));
//						inRecord.setField("REGISTER", "Y5YDL018");
//						inRecord.setField("YD_STK_BED_NO", recWrkkbookMtl.getFieldString("LOC"));
//						inRecord.setField("YD_STK_LYR_NO", "001");
//						
//						intRtnVal = ydCarFtmvMtlDao.insYdCarftmvmtl(inRecord);
//						
//						sQueryId = "com.inisteel.cim.ym.dao.ydCarPointdao.ydStockDao.upYdCarUppLocCdToTcar0";
//						intRtnVal = dao.updateData(sQueryId,new Object[]{ recWrkkbookMtl.getFieldString("STL_NO") });
//						
//						
//						sQueryId = "com.inisteel.cim.ym.dao.ydCarPointdao.ydStockDao.upYdCarUppLocCdToTcar";
//						intRtnVal = dao.updateData(sQueryId,new Object[]{ recWrkkbookMtl.getFieldString("STL_NO")
//																		  , szPT_LOAD_LOC
//								                                          , recWrkkbookMtl.getFieldString("LOC") });
//						
//						
//						
//					}
//				}
////				2.4 차량예정정보 전송
//				szMsg = "차량정보 예정정보 전송 start";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//				
//				recInTemp = JDTORecordFactory.getInstance().create();
//		    	recInTemp.setField("YD_CAR_SCH_ID",    szYD_CAR_SCH_ID);
//		    	szRtnMsg = this.callYDY5L008(recInTemp);
//	        	
//	        	if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {													//성공
//	        		szMsg = "[JSP Session](차량작업 예정정보송신) 호출 성공";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//				}else{
//					szMsg = "[JSP Session](차량작업 예정정보송신) 호출 실패";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				}
//				
////				5.크레인스케줄기동
//				if(rsResult2.size()>0){
//				
//					// 하차 가능 매수  수만큼 루프
//					for(int Loop_i = 1; Loop_i <= rsResult2.size() ; Loop_i++) {
//						rsResult2.absolute(Loop_i);
//						inRecord = rsResult2.getRecord();
//						
//						JDTORecord[] inRecordarr   	= null;
//						inRecordarr = new JDTORecord[1];
//						
//						inRecordarr[0] = JDTORecordFactory.getInstance().create();
//						inRecordarr[0].setField("YD_SCH_CD"		, szYD_SCH_CD); 
//						inRecordarr[0].setField("YD_WBOOK_ID"	, ydDaoUtils.paraRecChkNull(inRecord,"YD_WBOOK_ID")); 
//						
//						ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
//						outRecord21 = (JDTORecord) ejbConn.trx("trxRunSchedule", new Class[] { JDTORecord[].class },	new Object[] { inRecordarr });
//			
//						sRTN_CD		= StringHelper.evl(outRecord21.getFieldString("RTN_CD"), "0");
//						sRTN_MSG	= StringHelper.evl(outRecord21.getFieldString("RTN_MSG"), "");
//						ydUtils.putLog(szSessionName, szMethodName, "//" + sRTN_MSG, YdConstant.DEBUG);
//						if (!("1".equals(sRTN_CD))) {
//							return YdConstant.RETN_INT_FAILURE;	
//						} else {
//							szMsg = szPT_LOAD_LOC+" <br> 정상적으로 스케쥴까지 등록했습니다.";
//							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//							
//						} 
//						
////						szMsg="JSP-SESSION [Thread.sleep(1000)] 1초후 다음 하차스케쥴 호출"; 
////						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
////						
////						Thread.sleep(1000);		//야드 스케쥴 시간 대기.(1초 여유)
//						
//					}
//				}
//				
//			}
//			
//			
//			
//			
//		}catch(Exception e){
//	
//			szMsg="대차 이동 실적 처리 Error:" +e.getMessage();
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//			return YdConstant.RETN_INT_FAILURE;
//		}
//	
//	
//		szMsg="대차 이동 실적 처리("+szMethodName+") 완료";
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		return YdConstant.RETN_INT_SUCCESS;
//	
//	} //end of procY5TcarMvWr()
	
	
	

    /**
     * 오퍼레이션명 : 차량 Setting
     *  
     * @param inRecordSet, intGp
     * @return intRtnVal
     * @throws JDTOException
     */
    public int Y5SetYdCarCoil (JDTORecordSet inRecordSet, int intGp) throws JDTOException{
    	YdCarSchDao ydCarschDao = new YdCarSchDao();
    	//Data Setting
    	JDTORecord 	  setRecord 			= JDTORecordFactory.getInstance().create();
    	
    	//data를 받음
    	JDTORecord 	  getRecord 			= JDTORecordFactory.getInstance().create();
    	
    	//차량 스케줄 레코드셋의 레코드값을 받음
    	JDTORecord 	  getTcarRecord 		= JDTORecordFactory.getInstance().create();
    	
    	//차량 스케줄의 레코드셋
    	JDTORecordSet outRecSet  			= JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	String szMethodName 				= "Y5SetYdCarCoil" ;
    	String szMsg 						= "" ;
    	
    	int  intRtnVal = 0 ;
    	long lngYD_MTL_WT  = 0 ;
    	int  intYD_MTL_SH  = 0 ;
    	long lngYD_EQP_WRK_WT  = 0 ;
    	int  intYD_EQP_WRK_SH  = 0 ;
    	
    	//차량 스케줄 ID
    	String szYD_CAR_SCH_ID = "" ;
    	
    	try{
    		
	    	// 크레인스케줄 Data
	    	inRecordSet.first();
	    	getRecord = inRecordSet.getRecord();

	    	
	    	//하차 작업 예약 ID	Setting
	    	if(intGp == 0) {
				setRecord.setField("YD_CARUD_WRK_BOOK_ID", getRecord.getFieldString("YD_WBOOK_ID")) ;
		    	setRecord.setField("YD_CARLD_WRK_BOOK_ID", "") ;
		    	
		    //상차 작업 예약 ID	Setting
	    	}else if(intGp == 1) {
				setRecord.setField("YD_CARLD_WRK_BOOK_ID", getRecord.getFieldString("YD_WBOOK_ID")) ;
		    	setRecord.setField("YD_CARUD_WRK_BOOK_ID", "") ;
	    	}
	    	
	    	
	    	// 상하차 작업예약 ID로 대차스케줄 조회
	    	intRtnVal = this.Y5GetYdCarschCoil(setRecord, outRecSet, 3) ;
	    	if (intRtnVal <= 0) return -1 ;
	    	
	    	
	    	// 차량스케줄 Data
	    	outRecSet.first() ;
	    	getTcarRecord = outRecSet.getRecord() ;
	    	// 차량스케줄 ID를 추출한다
	    	szYD_CAR_SCH_ID  = ydDaoUtils.paraRecChkNull(getTcarRecord,"YD_CAR_SCH_ID");
	    	lngYD_EQP_WRK_WT = ydDaoUtils.paraRecChkNullLong(getTcarRecord,"YD_EQP_WRK_WT");
	    	intYD_EQP_WRK_SH = ydDaoUtils.paraRecChkNullInt(getTcarRecord,"YD_EQP_WRK_SH");
	    	
	    	
	    	//setRecord 초기화
	    	setRecord 			= JDTORecordFactory.getInstance().create();
	    	int szRowSize = inRecordSet.size(); 
	    	
	    	

	    	// 권상한 재료만큼 차량스케줄 ID로 차량이송재료에 가서 권상한 재료를 삭제유무를 "Y"로 한다.(Clear)  

	    	for(int i = 0; i < szRowSize; i++){
	    		intYD_MTL_SH = i+1;
	    		lngYD_MTL_WT = lngYD_MTL_WT + ydDaoUtils.paraRecChkNullLong(getRecord,"YD_MTL_WT");
		    	// 차량스케줄 ID로 차량이송재료에 가서 권상한 재료를 삭제유무를 "Y"로 한다.(Clear)        
	    		setRecord.setField("YD_CAR_SCH_ID",       	szYD_CAR_SCH_ID);
	    		setRecord.setField("STL_NO",              	ydDaoUtils.paraRecChkNull(getRecord, "STL_NO")); 
	    		
	    		//차량 이송재료 등록 (하차 )
	    		if(intGp == 0) {
		    		setRecord.setField("DEL_YN",       			"Y");
		    		intRtnVal = this.Y5UpdCarftmvmtlCoil(setRecord, 0) ;
			    	switch (intRtnVal) {
			        	case 0	:
			                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
			                return intRtnVal;
			        	case -1	:
			                szMsg = "dup_val_on_index!!!, ErrorCode:" + intRtnVal;
			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
			                return intRtnVal;
			        	case -2	:
			                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
			                return intRtnVal;
			        	case -3	:
			                szMsg = "execution failed!!!, ErrorCode:" + intRtnVal;
			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
			                return intRtnVal;
			        	case -4	:
			                szMsg = "Exception error!!!, ErrorCode:" + intRtnVal;
			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
			                return intRtnVal;
			        }
			    	
			    //차량 이송재료 등록 (상차 )	
	    		}else if(intGp == 1) {
		    		setRecord.setField("DEL_YN",       			"N");
		    		setRecord.setField("YD_STK_BED_NO",       	ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LOC").substring(6,8)) ;
		    		setRecord.setField("YD_STK_LYR_NO",       	"001") ;
		    		intRtnVal = this.Y5InsYdCarftmvmtlCoil(setRecord) ;
			    	switch (intRtnVal) {
			        	case -2	:
			                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
			                return intRtnVal;
			        	case -3	:
			                szMsg = "Exception error!!!, ErrorCode:" + intRtnVal;
			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
			                return intRtnVal;
			        }
	    		}
		    	inRecordSet.next() ;
		    	getRecord = inRecordSet.getRecord();
	    	}
	    	
	    	if(intGp == 1) {
    			//차량스케줄에 등록한다.
    			lngYD_EQP_WRK_WT = lngYD_EQP_WRK_WT - lngYD_MTL_WT;
    			intYD_EQP_WRK_SH = intYD_EQP_WRK_SH - intYD_MTL_SH;
    	    	//setRecord 초기화
    	    	setRecord 			= JDTORecordFactory.getInstance().create();
	    		setRecord.setField("YD_CAR_SCH_ID",       	szYD_CAR_SCH_ID);
	    		setRecord.setField("YD_EQP_WRK_WT",       	"" + lngYD_EQP_WRK_WT);
	    		setRecord.setField("YD_EQP_WRK_SH",       	"" + intYD_EQP_WRK_SH);
	    		
	    		intRtnVal = ydCarschDao.updYdCarsch(setRecord, 0);
	    		switch (intRtnVal) {
		        	case 0	:
		                szMsg = "data not found!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);	  
		                return intRtnVal;
		        	case -1	:
		                szMsg = "dup_val_on_index!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
		                return intRtnVal;
		        	case -2	:
		                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
		                return intRtnVal;
		        	case -3	:
		                szMsg = "execution failed!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
		                return intRtnVal;
		        	case -4	:
		                szMsg = "Exception error!!!, ErrorCode:" + intRtnVal;
		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
		                return intRtnVal;
		        }
    		}
	    	
		}catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -1 ;
		}//end of try~catch
		
    	return 1 ;
    	
    }//end of Y5SetYdCarCoil()


    /**
     * 오퍼레이션명 : 차량이송재료 Insert
     *  
     * @param msgRecord
     * @return intRtnVal
     * @throws 
     */
    public int Y5InsYdCarftmvmtlCoil(JDTORecord msgRecord){
    	YdCarFtmvMtlDao ydCarftmvmtlDao = new YdCarFtmvMtlDao();
    	
    	int intRtnVal = 0 ;

        
        try{
        	intRtnVal = ydCarftmvmtlDao.insYdCarftmvmtl(msgRecord);
        	if(intRtnVal <= 0) return intRtnVal ;
        	
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -3 ;
        }//end of try~catch
        
        return intRtnVal ;
    	
    }//end of Y5InsYdCarftmvmtlCoil



    /**
     * 오퍼레이션명 : 차량 이송재료 Update
     *  
     * @param inRecordSet, intGp
     * @return intRtnVal
     * @throws JDTOException
     */
    public int Y5UpdCarftmvmtlCoil (JDTORecord inRecord, int intGp) throws JDTOException {
    	YdCarFtmvMtlDao ydCarftmvmtlDao   = new YdCarFtmvMtlDao(); 
    	
    	int intRtnVal = 0 ;
        
        try{
        	
            intRtnVal = ydCarftmvmtlDao.updYdCarftmvmtl(inRecord, intGp) ;
		
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			intRtnVal = -1 ;
			return intRtnVal ;
	    }//end of try~catch	
		
		return intRtnVal ;
		
    }//end of Y5UpdCarftmvmtlCoil
    

    /**
     * 오퍼레이션명 : 차량 스케줄 Select
     *  
     * @param msgRecord, outRecset, intGp(1:상하차)
     * @return intRtnVal
     * @throws 
     */
    public int Y0GetYdCarsch (JDTORecord msgRecord, JDTORecordSet outRecset, int intGp){
    	YdCarSchDao ydCarschDao = new YdCarSchDao();
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	int intRtnVal 			= 0 ;
    	
    	String szMethodName = "Y0GetYdCarsch";
    	String szMsg        = "";
    	
        try{
        	
        	intRtnVal = ydCarschDao.getYdCarsch(msgRecord, getRecSet, intGp);
	        
			if(intRtnVal <= 0) {
				if(intRtnVal == 0) {
					szMsg="data not found";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				}else if(intRtnVal == -2) {
					szMsg="parameter error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				if (intRtnVal <= 0) return intRtnVal = -2;
			}
	        outRecset.addAll(getRecSet)  ; 
	        
        }catch(Exception e){
            szMsg = "Error : "+ e.getLocalizedMessage();
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
            return intRtnVal = -2;
        }//end of try~catch
        
        return intRtnVal ;
        
    }//end of Y0GetYdCarsch
    

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
    		ydUtils.putLog(szSessionName, szMethodName, "callYDY5L008 szYD_CAR_SCH_ID=("+ydDaoUtils.paraRecChkNull(recInPara , "YD_CAR_SCH_ID")+")", YdConstant.DEBUG);
    		
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
     * 오퍼레이션명 : 차량 스케줄 Select
     *  
     * @param msgRecord, outRecset, intGp
     * @return intRtnVal
     * @throws 
     */
    public int Y5GetYdCarschCoil (JDTORecord msgRecord, JDTORecordSet outRecset, int intGp){
    	YdCarSchDao ydCarschDao = new YdCarSchDao();
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	
    	int intRtnVal 			= 0 ;
        
        try{
        	
        	intRtnVal = ydCarschDao.getYdCarsch(msgRecord, getRecSet, intGp);
	        if (intRtnVal <= 0) return -2;
	        
	        outRecset.addAll(getRecSet)  ; 
	        
        }catch(Exception e){
			System.out.println("Error : "+ e.getLocalizedMessage());
			return -2 ;
        }//end of try~catch
        
        return intRtnVal ;
        
    }//end of Y5GetYdCarschCoil
    
	
//	
//
//	/**
//	 * 오퍼레이션명 : 대차스케줄상태Check
//	 *  
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param msgRecord, rsTarSch
//	 * @return intRtnVal
//	 * @throws JDTOException
//	 */
//	public int Y5ChkTcarSchStat(JDTORecord msgRecord, String szEqpWrkStat)throws JDTOException  {
//		YdTcarSchDao ydTcarSchDao = new YdTcarSchDao();
//		YdEqpDao     ydEqpDao     = new YdEqpDao();
//		
//		JDTORecordSet rsResult          = null;
//		JDTORecord    recOutTemp        = null;
//		JDTORecord    recInTemp         = null;
//
//	    
//	    String szMsg              		= "";
//	    String szMethodName       		= "Y5ChkTcarSchStat";
//	    
//	    
//	    String szQuery                  = "";
//
//	    String szEqpId                  = "";
//	    String szMoveGp                 = "";
////	    String szEqpWrkStat             = "";
//	    
//	    int intRtnVal                   = 0;
//
//	    
//	    try{
//	    	//설비id로 대차 스케줄을 조회한다.
//	    	szEqpId  = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
//	    	szMoveGp = ydDaoUtils.paraRecChkNull(msgRecord, "YD_MOVE_GP");   // 야드대차이동구분
//	    	
//	    	//대차설비Table에 야드동구분을 update위한 Setting
//	    	recInTemp = JDTORecordFactory.getInstance().create();
//	    	recInTemp.setField("YD_EQP_ID", szEqpId);
//	    	
//	    	
//	    	//출발 실적인 경우
//	    	if(szMoveGp.equals("S")) {
//	    		//설비Table에 야드동구분 상태를 Clear한다.
//				szMsg="설비Table에 야드동구분 상태를 Clear한다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//				
//	    		recInTemp.setField("YD_CURR_BAY_GP", "");
//	    		intRtnVal = ydEqpDao.updYdEqp(recInTemp, 0);
//	    		if(intRtnVal <= 0) {
//	    			if(intRtnVal == 0) {
//	    				szMsg=" updYdEqp : data not found";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//	    			}else if(intRtnVal == -1) {
//	    				szMsg=" updYdEqp : duplicate data,";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
//	    			}else if(intRtnVal == -2) {
//	    				szMsg=" updYdEqp : parameter error";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	    			}else if(intRtnVal == -3){
//	    				szMsg=" updYdEqp : execution failed";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	    			}
//	    			return intRtnVal = -1;
//	    		}
//
//	    		//공대차 출발인경우
//	    		if(szEqpWrkStat.equals("U")) {
//	    			return intRtnVal = 1;
//	    			
//	    		//영대차 출발인 경우	
//	    		}else if(szEqpWrkStat.equals("L")) {
//	    			return intRtnVal = 3;
//	    		}
//	    		
//	    	//도착 실적인 경우	
//	    	}else if (szMoveGp.equals("E")) {
//	    		//설비Table에 야드동구분 상태를 Update한다.
//				szMsg="설비Table에 야드동구분 상태를 Update한다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//				
//				//현재동 업데이트
//	    		recInTemp.setField("YD_CURR_BAY_GP", ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP"));
//	    		intRtnVal = ydEqpDao.updYdEqp(recInTemp, 0);
//	    		if(intRtnVal <= 0) {
//	    			if(intRtnVal == 0) {
//	    				szMsg=" updYdEqp : data not found";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//	    			}else if(intRtnVal == -1) {
//	    				szMsg=" updYdEqp : duplicate data,";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
//	    			}else if(intRtnVal == -2) {
//	    				szMsg=" updYdEqp : parameter error";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	    			}else if(intRtnVal == -3){
//	    				szMsg=" updYdEqp : execution failed";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	    			}
//	    			return intRtnVal = -1;
//	    		}
//	    		
//	    		//공대차 도착인경우
//	    		if(szEqpWrkStat.equals("U")) {
//	    			return intRtnVal = 2;
//	    			
//	    		//영대차 도착인 경우	
//	    		}else if(szEqpWrkStat.equals("L")) {
//	    			return intRtnVal = 4;
//	    			
//	    		}
//	    	}
//
//	    	
// 		}catch(Exception e){
//	
//			szMsg="대차스케줄상태Check Error:" +e.getMessage();
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//			return intRtnVal = -1;
//		}
//	
//		szMsg="대차스케줄상태Check("+szMethodName+") 완료";
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		return intRtnVal = 1;
//	} //end of Y5ChkTcarSchStat()
//	
//	
//	
//	
//	
//	
	
	
	/**
	 * 오퍼레이션명 : 공차출발실적
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord, recTcarSch
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public int Y5UTcarStartWr(JDTORecord msgRecord, JDTORecord recTcarSch)throws JDTOException  {
		YdStkBedDao ydStkBedDao = new YdStkBedDao();
		YdTcarSchDao ydTcarSchDao = new YdTcarSchDao();
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		
		JDTORecord    recInTemp          = null;
		JDTORecord    recInTemp1         = null;
		JDTORecord    recOutTemp         = null;
		JDTORecordSet rsResult           = null;
		JDTORecordSet rsResult1          = null;

	    
	    String szMsg              		= "";
	    String szMethodName       		= "Y5UTcarStartWr";
	    
	    

	    int intRtnVal                   = 0;
	    
	    String szStkColGp               = "";
	    String szStkBedNo               = "";
	    String szSchReqGp               = "";
	    String szYD_EQP_ID              = "";
	    String szYD_TCAR_SCH_ID         = "";
	    String szCURR_YD_BAY_GP         = "";
	    String sYD_BAY_GP      			= "";
	   
	    String szYD_GP         			= "";
	    
	    try{
			/*
			YD_EQP_ID 야드설비ID
			YD_MOVE_GP 야드대차이동구분
			YD_TCAR_MOVE_DIR 야드대차이동방향
			YD_BAY_GP 야드동구분1 현재동
			YD_TCAR_AIM_BAY 야드동구분2 목적동
			*/
	    	
			ydUtils.putLog(szSessionName, szMethodName, "공차출발처리시작", YdConstant.DEBUG);
			szYD_GP 			= ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP");
			szCURR_YD_BAY_GP 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP");
		//	szYD_TCAR_AIM_BAY	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_TCAR_AIM_BAY");
	    	
			sYD_BAY_GP			= ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP"); 
			//대차스케줄에 대차진행상태 update
	    	szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_EQP_ID", szYD_EQP_ID);
	    	
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	    	/*"com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.getYdTcarschByYdEqpId*/
	    	intRtnVal = ydTcarSchDao.getYdTcarsch(recInTemp, rsResult, 4);
			if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="Y5UTcarStartWr getYdTcarsch : data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
    			}else if(intRtnVal == -2) {
    				szMsg="Y5UTcarStartWr getYdTcarsch : parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			}
    			return intRtnVal = -1;
    		}
			
			rsResult.absolute(1);
			recOutTemp = JDTORecordFactory.getInstance().create();
			recOutTemp.setRecord(rsResult.getRecord());
			
			szYD_TCAR_SCH_ID = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TCAR_SCH_ID");
			recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_TCAR_SCH_ID", szYD_TCAR_SCH_ID);
	    	recInTemp.setField("YD_CAR_PROG_STAT", "1");
	    	recInTemp.setField("YD_CARLD_LEV_LOC",  szYD_GP + szCURR_YD_BAY_GP + szYD_EQP_ID.substring(2,6));
	    	recInTemp.setField("YD_CARLD_STOP_LOC", szYD_GP + sYD_BAY_GP + szYD_EQP_ID.substring(2,6));
	    	
	    	
	    	/* com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.updYdTcarsch *///TB_YD_TCARSCH
	    	intRtnVal = ydTcarSchDao.updYdTcarsch(recInTemp, 0);
			if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="Y5UTcarStartWr updYdTcarsch : data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
    			}else if(intRtnVal == -1) {
    				szMsg="Y5UTcarStartWr updYdTcarsch : duplicate data,";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
    			}else if(intRtnVal == -2) {
    				szMsg="Y5UTcarStartWr updYdTcarsch : parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			}else if(intRtnVal == -3){
    				szMsg="Y5UTcarStartWr updYdTcarsch : execution failed";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			}
    			return intRtnVal = -1;
    		}
			
			
	    	//출발위치Bed상태 비활성화
	    	szStkColGp = szYD_GP
	    	           + ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP") 
	    	           + ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(2);
	    	
	    	szMsg="[공차출발실적] szStkColGp = " + szStkColGp;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_STK_COL_GP", szStkColGp);
	    	
			rsResult1 = JDTORecordFactory.getInstance().createRecordSet("");
			/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedBed*/
			intRtnVal = ydStkBedDao.getYdStkbed(recInTemp, rsResult1, 1);
			if(intRtnVal <= 0) {
    			return intRtnVal = -1;
    		}
			
			for(int Loop_i = 1; Loop_i <= rsResult1.size(); Loop_i++) {
				rsResult1.absolute(Loop_i);
				recInTemp1  = JDTORecordFactory.getInstance().create();
	    		recInTemp1.setRecord(rsResult1.getRecord());		
				
		    	szStkBedNo = ydDaoUtils.paraRecChkNull(recInTemp1, "YD_STK_BED_NO");
				
		    	recInTemp = JDTORecordFactory.getInstance().create();
		    	recInTemp.setField("YD_STK_COL_GP", szStkColGp);
		    	recInTemp.setField("YD_STK_BED_NO", szStkBedNo);
		    	recInTemp.setField("YD_STK_BED_ACT_STAT", "C");
		    	/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.updYdStkbed*///TB_YD_STKBED
		    	intRtnVal = ydStkBedDao.updYdStkbed(recInTemp, 0);
				if(intRtnVal <= 0) {
	    			if(intRtnVal == 0) {
	    				szMsg="<Y5UTcarStartWr> updYdStkbed data not found";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
	    			}else if(intRtnVal == -1) {
	    				szMsg="<Y5UTcarStartWr> updYdStkbed duplicate data,";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
	    			}else if(intRtnVal == -2) {
	    				szMsg="<Y5UTcarStartWr> updYdStkbed parameter error";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			}else if(intRtnVal == -3){
	    				szMsg="<Y5UTcarStartWr> updYdStkbed execution failed";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			}
	    			return intRtnVal = -1;
	    		}
				//적치단 비활성화
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_STK_COL_GP", szStkColGp);
				recInTemp.setField("YD_STK_BED_NO", szStkBedNo);
				recInTemp.setField("YD_STK_LYR_ACT_STAT", "C");
				recInTemp.setField("STL_NO", "");
				recInTemp.setField("YD_STK_LYR_MTL_STAT", "E");
		    	/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrYdStkColGpYdStkBedNo*///TB_YD_STKLYR
				intRtnVal = ydStkLyrDao.updYdStklyrYdStkColGpBedNo(recInTemp);
				if(intRtnVal <= 0) {
					szMsg = "<Y5UTcarStartWr> 적치단 정보 활성화중 Error!! ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					throw new DAOException("<CarMvHdSeEJBBean> updYdStklyrYdStkColGp " + szMsg);
				}
			
			}
	    	
			ydUtils.putLog(szSessionName, szMethodName, "szSchReqGp" + szSchReqGp, YdConstant.DEBUG);
	    	//상차 스케줄 요청 구분이 공차 출발이라면 
//	    	if(szSchReqGp.equals("5")) {
//	    	//상차정지위치 베드 활성화
//		    	recInTemp = JDTORecordFactory.getInstance().create();
//		    	recInTemp.setField("YD_STK_COL_GP", szCarldStopLoc);
//		    	recInTemp.setField("YD_STK_BED_NO", szStkBedNo);
//		    	recInTemp.setField("YD_STK_BED_ACT_STAT", "L");
//		    	/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.updYdStkbed*///TB_YD_STKBED
//		    	intRtnVal = ydStkBedDao.updYdStkbed(recInTemp, 0);
//				if(intRtnVal <= 0) {
//	    			if(intRtnVal == 0) {
//	    				szMsg="<Y5UTcarStartWr> ydStkBedDao data not found";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//	    			}else if(intRtnVal == -1) {
//	    				szMsg="<Y5UTcarStartWr> ydStkBedDao duplicate data,";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
//	    			}else if(intRtnVal == -2) {
//	    				szMsg="<Y5UTcarStartWr> ydStkBedDao parameter error";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	    			}else if(intRtnVal == -3){
//	    				szMsg="<Y5UTcarStartWr> ydStkBedDao execution failed";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	    			}
//	    			return intRtnVal = -1;
//	    		}
//		    	
//		    	//상차정지위치 단정보 Clear, 
//				recInTemp = JDTORecordFactory.getInstance().create();
//				recInTemp.setField("YD_STK_COL_GP", szStkColGp);
//				recInTemp.setField("YD_STK_BED_NO", szStkBedNo);
//				recInTemp.setField("STL_NO", "");
//				recInTemp.setField("MODIFIER", "SYSTEM");
//				recInTemp.setField("YD_STK_LYR_ACT_STAT", "E");
//				recInTemp.setField("YD_STK_LYR_MTL_STAT", "E");
//
//		    	/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrYdStkColGpYdStkBedNo*///TB_YD_STKLYR
//				intRtnVal = ydStkLyrDao.updYdStklyrYdStkColGpBedNo(recInTemp);
//				if(intRtnVal <= 0) {
//					szMsg = "<Y5UTcarStartWr> 적치단 정보 활성화중 Error!! ";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//					throw new DAOException("<CarMvHdSeEJBBean> updYdStklyrYdStkColGp " + szMsg);
//				}
//		    	
//		    	//상차 크레인스케줄호출
//		    	recInTemp = JDTORecordFactory.getInstance().create();
//		    	recInTemp.setField("YD_WBOOK_ID", szWbookId);
//		    	intRtnVal = this.Y5CallCrnSch(recInTemp);	
//		    	
//		    	
//	    	}
	    	
 		}catch(Exception e){
	
			szMsg="공차 출발실적 처리 Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			return intRtnVal = -1;
		}
	
	
		szMsg="공차 출발실적 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		return intRtnVal = 1;
	} //end of Y5UTcarStartWr()
	
	
	
	
	
	
	

	/**
	 * 오퍼레이션명 : 공차도착실적
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord, recTcarSch
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public int Y5UTcarStopWr(JDTORecord msgRecord, JDTORecord recTcarSch)throws JDTOException  {
		YdStkBedDao 	ydStkBedDao 	= new YdStkBedDao();
		YdTcarSchDao 	ydTcarSchDao 	= new YdTcarSchDao();
		YdStkLyrDao 	ydStkLyrDao 	= new YdStkLyrDao();
		YdWrkbookDao    ydWrkbookDao    = new YdWrkbookDao();
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		JDTORecord    recInTemp         = null;
		JDTORecord    recInTemp1        = null;
		JDTORecord    recOutTemp        = null;
		JDTORecordSet rsResult          = null; 
		JDTORecordSet rsResult1         = null; 
		
	    
	    String szMsg              		= "";
	    String szMethodName       		= "Y5UTcarStopWr";
	    String szOperationName          = "공차도착실적";
	    
	    
	
	    int intRtnVal                   = 0;
	    int intRtnVal1                  = 0;
		    
	    String szStkBedNo               = "";
	    String szSchReqGp               = "";
	    String szCarldStopLoc           = "";
	    String szWbookId                = "";
	    String szYD_EQP_ID              = "";
	    String szYD_TCAR_SCH_ID         = "";
	    String szYD_BAY_GP              = "";
	    String szYD_TO_BAY              = "";
	    String szYD_GP              	= "";
	//    String szYD_TCAR_CURR_BAY      	= "";
	    try{
	    	
			/*
			YD_EQP_ID 야드설비ID
			YD_MOVE_GP 야드대차이동구분
			YD_TCAR_MOVE_DIR 야드대차이동방향
			YD_BAY_GP 야드동구분1 현재동
			YD_TCAR_AIM_BAY 야드동구분2 목적동
			*/
	    	//스케줄 요청 구분판단
	    	szYD_BAY_GP    		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP");
	    	szYD_EQP_ID    		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
	    	szYD_GP 			= ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP");
	    	//szYD_TCAR_CURR_BAY 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_TCAR_CURR_BAY");
			
	    	szSchReqGp     		= ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARLD_SCH_REQ_GP");
	    	//szCarldStopLoc 		= ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARLD_STOP_LOC");
	    	
	    	//if(szCarldStopLoc.equals("")){
	    		szCarldStopLoc =  szYD_GP + szYD_BAY_GP + szYD_EQP_ID.substring(2,6);
		    //}
	    	
	    	szWbookId      	= ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARLD_WRK_BOOK_ID");
	    	szYD_TO_BAY    	= ydDaoUtils.paraRecChkNull(recTcarSch, "YD_TO_BAY");
	    	
	    	ydUtils.putLog(szSessionName, szMethodName, "공차도착실적처리 시작:"+ szYD_GP, YdConstant.DEBUG);
	    	ydUtils.putLog(szSessionName, szMethodName, "공차도착실적처리 시작:"+ szSchReqGp, YdConstant.DEBUG);
	    	ydUtils.putLog(szSessionName, szMethodName, "공차도착실적처리 시작:"+ szCarldStopLoc, YdConstant.DEBUG);
	    	ydUtils.putLog(szSessionName, szMethodName, "공차도착실적처리 시작:"+ szWbookId, YdConstant.DEBUG);
	    	ydUtils.putLog(szSessionName, szMethodName, "공차도착실적처리 시작:"+ szYD_TO_BAY, YdConstant.DEBUG);
		  	
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_EQP_ID", szYD_EQP_ID);
	    	recInTemp.setField("YD_CURR_BAY_GP", szYD_BAY_GP);
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	    	/*com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.getYdTcarschByYdEqpId*/
	    	intRtnVal = ydTcarSchDao.getYdTcarsch(recInTemp, rsResult, 4);
			if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="Y5UTcarStopWr getYdTcarsch : data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
    			}else if(intRtnVal == -2) {
    				szMsg="Y5UTcarStopWr getYdTcarsch : parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			}
    			return intRtnVal = -1;
    		}
			
			rsResult.absolute(1);
			recOutTemp = JDTORecordFactory.getInstance().create();
			recOutTemp.setRecord(rsResult.getRecord());
			
			szYD_TCAR_SCH_ID = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TCAR_SCH_ID");
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_TCAR_SCH_ID", szYD_TCAR_SCH_ID);
			recInTemp.setField("YD_CARLD_STOP_LOC", szCarldStopLoc);
	    	
	    	//대차진행상태값 등록
	    	if(szWbookId.equals("")){
	    		//작업예약이 없는 이유는 작업자가 지정한 동으로 이동했거나 홈동으로 이동한 경우이기때문에...상차도착으로 처리하지 않는다.
	    		recInTemp.setField("YD_CAR_PROG_STAT", "0");
	    	}else{
		    	recInTemp1 = JDTORecordFactory.getInstance().create();
		    	recInTemp1.setField("YD_WBOOK_ID", szWbookId);
		    	rsResult  = JDTORecordFactory.getInstance().createRecordSet("Temp");
		    	
		    	intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recInTemp1, rsResult, 1);
				if(intRtnVal > 0) {
					recInTemp.setField("YD_CAR_PROG_STAT", "2");
				} else {
					recInTemp.setField("YD_CAR_PROG_STAT", "0");
					recInTemp.setField("YD_CARLD_WRK_BOOK_ID", "");
				}
	    	}
	    	/* com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.updYdTcarsch *///TB_YD_TCARSCH
	    	intRtnVal = ydTcarSchDao.updYdTcarsch(recInTemp, 0);
			if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="Y5UTcarStopWr updYdTcarsch : data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
    			}else if(intRtnVal == -1) {
    				szMsg="Y5UTcarStopWr updYdTcarsch : duplicate data,";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
    			}else if(intRtnVal == -2) {
    				szMsg="Y5UTcarStopWr updYdTcarsch : parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			}else if(intRtnVal == -3){
    				szMsg="Y5UTcarStopWr updYdTcarsch : execution failed";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			}
    			return intRtnVal = -1;
    		}
	    	
			ydUtils.putLog(szSessionName, szMethodName, "szSchReqGp" + szSchReqGp, YdConstant.DEBUG);
	    	//상차 스케줄 요청 구분이 공차 도착이라면 
	    	if(szSchReqGp.equals("6")) {
	    	//상차정지위치 베드 활성화
	    		
				recInTemp = JDTORecordFactory.getInstance().create();
		    	recInTemp.setField("YD_STK_COL_GP", szCarldStopLoc);
		    	
				rsResult1 = JDTORecordFactory.getInstance().createRecordSet("");
				/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedBed*/
				intRtnVal = ydStkBedDao.getYdStkbed(recInTemp, rsResult1, 1 );
				if(intRtnVal <= 0) {
	    			return intRtnVal = -1;
	    		}
				
				for(int Loop_i = 1; Loop_i <= rsResult1.size(); Loop_i++) {
					rsResult1.absolute(Loop_i);
					recInTemp1  = JDTORecordFactory.getInstance().create();
		    		recInTemp1.setRecord(rsResult1.getRecord());		
					
		    		szStkBedNo = ydDaoUtils.paraRecChkNull(recInTemp1, "YD_STK_BED_NO");
	    		
			    	recInTemp = JDTORecordFactory.getInstance().create();
			    	recInTemp.setField("YD_STK_COL_GP", szCarldStopLoc);
			    	recInTemp.setField("YD_STK_BED_NO", szStkBedNo);
			    	recInTemp.setField("YD_STK_BED_ACT_STAT", "L");
			    	/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.updYdStkbed*///TB_YD_STKBED
			    	intRtnVal = ydStkBedDao.updYdStkbed(recInTemp, 0);
					if(intRtnVal <= 0) {
		    			if(intRtnVal == 0) {
		    				szMsg="<Y5UTcarStopWr> ydStkBedDao data not found";
		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
		    			}else if(intRtnVal == -1) {
		    				szMsg="<Y5UTcarStopWr> ydStkBedDao duplicate data,";
		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
		    			}else if(intRtnVal == -2) {
		    				szMsg="<Y5UTcarStopWr> ydStkBedDao parameter error";
		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    			}else if(intRtnVal == -3){
		    				szMsg="<Y5UTcarStopWr> ydStkBedDao execution failed";
		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    			}
		    			return intRtnVal = -1;
		    		}
					
			    	//상차정지위치 단정보 Clear, 
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_STK_COL_GP", szCarldStopLoc);
					recInTemp.setField("YD_STK_BED_NO", szStkBedNo);
					recInTemp.setField("STL_NO", "");
					recInTemp.setField("MODIFIER", "SYSTEM");
					recInTemp.setField("YD_STK_LYR_ACT_STAT", "E");
					recInTemp.setField("YD_STK_LYR_MTL_STAT", "E");
					/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrYdStkColGpYdStkBedNo*///TB_YD_STKLYR
					intRtnVal = ydStkLyrDao.updYdStklyrYdStkColGpBedNo(recInTemp);
					if(intRtnVal <= 0) {
						szMsg = "<Y5UTcarStopWr> 적치단 정보 활성화중 Error!! ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						throw new DAOException("<CarMvHdSeEJBBean> updYdStklyrYdStkColGp " + szMsg);
					}
		    
				}
//		    	//상차 크레인스케줄호출
//		    	if(!szWbookId.equals("")) {
//					ydUtils.putLog(szSessionName, szMethodName, "기존작업예약으로 스케줄 기동" , YdConstant.DEBUG);
//
//		    		recInTemp = JDTORecordFactory.getInstance().create();
//			    	recInTemp.setField("YD_WBOOK_ID", szWbookId);
//			    	intRtnVal = this.Y5CallCrnSch(recInTemp);	
//		    	} else {
		    		
					ydUtils.putLog(szSessionName, szMethodName, "도착동,우선순위가 빠르고, 작업예약순서가 빠른 작업예약 조회" , YdConstant.DEBUG);

					rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
    	    		recInTemp = JDTORecordFactory.getInstance().create();
    	    		recInTemp.setField("YD_WRK_PLAN_TCAR", 			szYD_EQP_ID);                //작업계획 대차
    	    		recInTemp.setField("YD_BAY_GP", 				szYD_BAY_GP);
    	    		recInTemp.setField("YD_GP", 					szYD_GP);
    	    		
    	    		/*지정된 도착동,우선순위가 빠르고, 작업예약순서가 빠른 작업예약 조회*/
    	    		/* com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookYdWrkPlanTcarCoil */
    	    		intRtnVal1 = ydWrkbookDao.getYdWrkbook(recInTemp, rsResult, 301);
    	    		
   					ydUtils.putLog(szSessionName, szMethodName, "작업예약 대상재 유무 :" + intRtnVal1 , YdConstant.DEBUG);
   					if( intRtnVal1 > 0) {
   						for(int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {
   							rsResult.absolute(Loop_i);
   							recOutTemp = JDTORecordFactory.getInstance().create();
   					    	recOutTemp.setRecord(rsResult.getRecord());	
   							
   				    		szWbookId   = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WBOOK_ID");				//작업예약ID
   					    	recInTemp = JDTORecordFactory.getInstance().create();
   					    	recInTemp.setField("YD_WBOOK_ID", szWbookId);
   					    	intRtnVal = this.Y5CallCrnSch(recInTemp);	
   						}	
    	    		}
		    	}
//	    	}
 		}catch(Exception e){
	
			szMsg="공차 도착실적 처리 Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			return intRtnVal = -1;
		}
	
	
		szMsg="공차 도착실적 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		return intRtnVal = 1;
	} //end of Y5UTcarStopWr()
	
	
	
	
	
	

	/**
	 * 오퍼레이션명 : 영차출발실적
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord, recTcarSch
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public int Y5LTcarStartWr(JDTORecord msgRecord, JDTORecord recTcarSch)throws JDTOException  {
		YdStkBedDao ydStkBedDao = new YdStkBedDao();
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		YdTcarSchDao ydTcarSchDao = new YdTcarSchDao();
		JDTORecordSet rsResult          = null;
		JDTORecordSet rsResult1         = null;
		JDTORecord    recInTemp         = null;
		JDTORecord    recInTemp1        = null;
		JDTORecord    recOutTemp        = null;

	    
	    String szMsg              		= "";
	    String szMethodName       		= "Y5LTcarStartWr";
	    
	    String szSchReqGp               = "";
	    String szCarldStopLoc           = "";
	    String szCarudStopLoc           = "";
	    String szStkColGp               = "";
	    String szStkBedNo               = "";
	    String szWbookId                = "";
	    String szYD_EQP_ID              = "";
	    String szYD_TCAR_SCH_ID         = "";
	    
	    String szQuery                  = "";

	    int intRtnVal                   = 0;

	    
	    try{

			ydUtils.putLog(szSessionName, szMethodName, "영차출발처리시작", YdConstant.DEBUG);
	    	
	    	//대차스케줄에 대차진행상태 update
	    	szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_EQP_ID", szYD_EQP_ID);
	    	
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	    	/*"com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.getYdTcarschByYdEqpId*/
	    	intRtnVal = ydTcarSchDao.getYdTcarsch(recInTemp, rsResult, 4);
			if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="Y5LTcarStartWr getYdTcarsch : data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
    			}else if(intRtnVal == -2) {
    				szMsg="Y5UTcarStartWr getYdTcarsch : parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			}
    			return intRtnVal = -1;
    		}
			
			rsResult.absolute(1);
			recOutTemp = JDTORecordFactory.getInstance().create();
			recOutTemp.setRecord(rsResult.getRecord());
			  
			szYD_TCAR_SCH_ID = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TCAR_SCH_ID");
			recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_TCAR_SCH_ID"		, szYD_TCAR_SCH_ID);
	    	recInTemp.setField("YD_CAR_PROG_STAT"	, "A");
	    	/* com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.updYdTcarsch *///TB_YD_TCARSCH
	    	intRtnVal = ydTcarSchDao.updYdTcarsch(recInTemp, 0);
			if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="Y5LTcarStartWr updYdTcarsch : data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
    			}else if(intRtnVal == -1) {
    				szMsg="Y5LTcarStartWr updYdTcarsch : duplicate data,";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
    			}else if(intRtnVal == -2) {
    				szMsg="Y5LTcarStartWr updYdTcarsch : parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			}else if(intRtnVal == -3){
    				szMsg="Y5LTcarStartWr updYdTcarsch : execution failed";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			}
    			return intRtnVal = -1;
    		}
			
	    	//출발 정지위치의 베드상태 비활성화하고 적치단정보 Clear
	    	//스케줄 요청 구분판단
	    	szSchReqGp     = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARUD_SCH_REQ_GP");
	    	szCarldStopLoc = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARLD_STOP_LOC");
	    	szCarudStopLoc = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARUD_STOP_LOC");
	    	
			recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_STK_COL_GP", szCarldStopLoc);
	    	
			rsResult1 = JDTORecordFactory.getInstance().createRecordSet("");
			/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedBed*/
			intRtnVal = ydStkBedDao.getYdStkbed(recInTemp, rsResult1, 1);
			if(intRtnVal <= 0) {
    			return intRtnVal = -1;
    		}
			
			for(int Loop_i = 1; Loop_i <= rsResult1.size(); Loop_i++) {
				rsResult1.absolute(Loop_i);
				recInTemp1  = JDTORecordFactory.getInstance().create();
	    		recInTemp1.setRecord(rsResult1.getRecord());		
				
	    		szStkBedNo = ydDaoUtils.paraRecChkNull(recInTemp1, "YD_STK_BED_NO");
				
	       	
	    		recInTemp = JDTORecordFactory.getInstance().create();
		    	recInTemp.setField("YD_STK_COL_GP", szCarldStopLoc);
		    	recInTemp.setField("YD_STK_BED_NO", szStkBedNo);
		    	recInTemp.setField("YD_STK_BED_ACT_STAT", "C");
		    	/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.updYdStkbed*///TB_YD_STKBED
		    	intRtnVal = ydStkBedDao.updYdStkbed(recInTemp, 0);
				if(intRtnVal <= 0) {
	    			return intRtnVal = -1;
	    		}
				
				//출발정지위치 단정보 Clear, 적치단활성상태를 비활성화로...
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_STK_COL_GP", szCarldStopLoc);
				recInTemp.setField("YD_STK_BED_NO", szStkBedNo);
				recInTemp.setField("STL_NO", "");
				recInTemp.setField("MODIFIER", "SYSTEM");
				recInTemp.setField("YD_STK_LYR_ACT_STAT", "C");
				recInTemp.setField("YD_STK_LYR_MTL_STAT", "E");
	
				/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrYdStkColGpYdStkBedNo*///TB_YD_STKLYR
				intRtnVal = ydStkLyrDao.updYdStklyrYdStkColGpBedNo(recInTemp);
				if(intRtnVal <= 0) {
					szMsg = "<Y5LTcarStartWr> 적치단 정보 활성화중 Error!! ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					throw new DAOException("<CarMvHdSeEJBBean> updYdStklyrYdStkColGp " + szMsg);
				}
	    	
			}
	    	
			ydUtils.putLog(szSessionName, szMethodName, "szSchReqGp" + szSchReqGp, YdConstant.DEBUG);
//	    	//하차스케줄 요청 구분이 영차 출발이라면
//	    	if(szSchReqGp.equals("2")) {
//		    	//하차정지위치 Bed상태 활성화
//		    	recInTemp = JDTORecordFactory.getInstance().create();
//		    	recInTemp.setField("YD_STK_COL_GP", szCarudStopLoc);
//		    	recInTemp.setField("YD_STK_BED_NO", szStkBedNo);
//		    	recInTemp.setField("YD_STK_BED_ACT_STAT", "L");
//		    	/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.updYdStkbed*///TB_YD_STKBED
//		    	intRtnVal = ydStkBedDao.updYdStkbed(recInTemp, 0);
//		    	
//		    	//적치단에 재료정보 Update
//		        //하차작업에약ID를 조회해서 작업예약재료 Table를 조회한다.
//		    	szWbookId = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARUD_WRK_BOOK_ID");
//		    	recInTemp = JDTORecordFactory.getInstance().create();
//		    	recInTemp.setField("YD_WBOOK_ID", szWbookId);
//		    	rsResult  = JDTORecordFactory.getInstance().createRecordSet("Temp");
//		    	
//		    	intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recInTemp, rsResult, 1);
//				if(intRtnVal <= 0) {
//					if(intRtnVal == 0) {
//						szMsg="<Y5LTcarStartWr> ydWrkbookMtlDao data not found";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//					}else if(intRtnVal == -2) {
//						szMsg="<Y5LTcarStartWr> ydWrkbookMtlDao parameter error";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//					}
//					return intRtnVal = -1;
//				}
//		    	
//		    	
//		    	//작업예약재료의 정보를 적치단에 등록한다.
//		    	for(int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {
//		    		rsResult.absolute(Loop_i);
//		    		recInTemp  = JDTORecordFactory.getInstance().create();
//		    		recOutTemp = JDTORecordFactory.getInstance().create();
//		    		recInTemp.setRecord(rsResult.getRecord());
//		    		
//		    		recOutTemp.setField("YD_STK_COL_GP",       ydDaoUtils.paraRecChkNull(recInTemp, "YD_STK_COL_GP"));
//		    		recOutTemp.setField("YD_STK_BED_NO",       ydDaoUtils.paraRecChkNull(recInTemp, "YD_STK_BED_NO"));
//		    		recOutTemp.setField("YD_STK_LYR_NO",       ydDaoUtils.paraRecChkNull(recInTemp, "YD_STK_LYR_NO"));
//		    		recOutTemp.setField("STL_NO",              ydDaoUtils.paraRecChkNull(recInTemp, "STL_NO"));
//		    		recOutTemp.setField("YD_STK_LYR_MTL_STAT", "E"); //적치가능
//		    		recOutTemp.setField("YD_STK_LYR_ACT_STAT", "C"); //비활성화
//		    		
//
//					//recInTemp.setField("YD_STK_LYR_ACT_STAT",  "E");
//					/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyr*/
//		    		intRtnVal = ydStkLyrDao.updYdStklyr(recOutTemp, 0);
//					if(intRtnVal <= 0) {
//		    			if(intRtnVal == 0) {
//		    				szMsg="<Y5LTcarStartWr> updYdStklyr data not found";
//		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//		    			}else if(intRtnVal == -1) {
//		    				szMsg="<Y5LTcarStartWr> updYdStklyr duplicate data,";
//		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
//		    			}else if(intRtnVal == -2) {
//		    				szMsg="<Y5LTcarStartWr> updYdStklyr parameter error";
//		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		    			}else if(intRtnVal == -3){
//		    				szMsg="<Y5LTcarStartWr> updYdStklyr execution failed";
//		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		    			}
//		    			return intRtnVal = -1;
//		    		}
//		    	}
//		    	
//
//		    	//하차크레인 스케줄 호출
//		    	recInTemp = JDTORecordFactory.getInstance().create();
//		    	recInTemp.setField("YD_WBOOK_ID", szWbookId);
//		    	intRtnVal = this.Y5CallCrnSch(recInTemp);	
//		    	
//	    	}

	    	
 		}catch(Exception e){
	
			szMsg="영차 출발실적 처리 Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			return intRtnVal = -1;
		}
	
	
		szMsg="영차 출발실적 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		return intRtnVal = 1;
	} //end of Y5LTcarStartWr()
	
	
	
	

	

	/**
	 * 오퍼레이션명 : 영차도착실적
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord, recTcarSch
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public int Y5LTcarStopWr(JDTORecord msgRecord, JDTORecord recTcarSch)throws JDTOException  {
		YdStkBedDao ydStkBedDao = new YdStkBedDao();
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		YdEqpDao ydEqpDao = new YdEqpDao();
		YdWrkbookMtlDao  ydWrkbookMtlDao  = new YdWrkbookMtlDao();
		YdTcarSchDao ydTcarSchDao = new YdTcarSchDao();
		YdTcarFtmvMtlDao ydTcarFtmvMtlDao = new YdTcarFtmvMtlDao();
		
		JDTORecordSet rsResult          = null;
		JDTORecordSet rsResult1          = null;
		JDTORecord    recInTemp         = null;
		JDTORecord    recOutTemp        = null;
		JDTORecord    recInTemp1        = null;

	    
	    String szMsg              		= "";
	    String szMethodName       		= "Y5LTcarStopWr";
	    

	    int intRtnVal                   = 0;

	    String szSchReqGp               = "";
	    String szCarldStopLoc           = "";
	    String szCarudStopLoc           = "";
	    String szStkBedNo               = "";
	    String szWbookId                = "";
	    String szYD_EQP_ID              = "";
	    String szYD_TCAR_SCH_ID         = "";
	    String szYD_BAY_GP              = "";
	    String szYD_CAR_PROG_STAT       = "";
	    
	    try{
	    	
			ydUtils.putLog(szSessionName, szMethodName, "영차도착실적시작", YdConstant.DEBUG);
	    	szSchReqGp     = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARUD_SCH_REQ_GP");
	    	szCarldStopLoc = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARLD_STOP_LOC");
	    	szCarudStopLoc = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARUD_STOP_LOC");
	    	
	    	//대차스케줄에 대차진행상태 update
	    	szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_EQP_ID", szYD_EQP_ID);
    	
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	    	/*"com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.getYdTcarschByYdEqpId*/
	    	intRtnVal = ydTcarSchDao.getYdTcarsch(recInTemp, rsResult, 4);
			if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="Y5LTcarStopWr getYdTcarsch : data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
    			}else if(intRtnVal == -2) {
    				szMsg="Y5LTcarStopWr getYdTcarsch : parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			}
    			return intRtnVal = -1;
    		}
			
			rsResult.absolute(1);
			recOutTemp = JDTORecordFactory.getInstance().create();
			recOutTemp.setRecord(rsResult.getRecord());
			
			szYD_TCAR_SCH_ID 	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TCAR_SCH_ID");
			szYD_CAR_PROG_STAT 	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CAR_PROG_STAT");
			recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_TCAR_SCH_ID"		, szYD_TCAR_SCH_ID);
	    	recInTemp.setField("YD_CAR_PROG_STAT"	, "B");
//	    	recInTemp.setField("YD_CURR_BAY_GP", szCarudStopLoc.substring(1,2));
	    	/* com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.updYdTcarsch *///TB_YD_TCARSCH
	    	intRtnVal = ydTcarSchDao.updYdTcarsch(recInTemp, 0);
			if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="Y5LTcarStopWr updYdTcarsch : data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
    			}else if(intRtnVal == -1) {
    				szMsg="Y5LTcarStopWr updYdTcarsch : duplicate data,";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
    			}else if(intRtnVal == -2) {
    				szMsg="Y5LTcarStopWr updYdTcarsch : parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			}else if(intRtnVal == -3){
    				szMsg="Y5LTcarStopWr updYdTcarsch : execution failed";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			}
    			return intRtnVal = -1;
    		}
	    	
			ydUtils.putLog(szSessionName, szMethodName, "szSchReqGp" + szSchReqGp, YdConstant.DEBUG);
	    	
	    	//스케줄 요청 구분이 영대차도착 이라면
	    	if(szSchReqGp.equals("3")) {
	    		
	    		if(!szYD_CAR_PROG_STAT.equals("A")){
	    			
	    			recInTemp = JDTORecordFactory.getInstance().create();
	    	    	recInTemp.setField("YD_STK_COL_GP", szCarldStopLoc);
	    	    	
	    			rsResult1 = JDTORecordFactory.getInstance().createRecordSet("");
	    			/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedBed*/
	    			intRtnVal = ydStkBedDao.getYdStkbed(recInTemp, rsResult1, 1);
	    			if(intRtnVal <= 0) {
	        			return intRtnVal = -1;
	        		}
	    			
	    			for(int Loop_i = 1; Loop_i <= rsResult1.size(); Loop_i++) {
	    				rsResult1.absolute(Loop_i);
	    				recInTemp1  = JDTORecordFactory.getInstance().create();
	    	    		recInTemp1.setRecord(rsResult1.getRecord());		
	    				
	    	    		szStkBedNo = ydDaoUtils.paraRecChkNull(recInTemp1, "YD_STK_BED_NO");
	    				
	    	       	
	    	    		recInTemp = JDTORecordFactory.getInstance().create();
	    		    	recInTemp.setField("YD_STK_COL_GP", szCarldStopLoc);
	    		    	recInTemp.setField("YD_STK_BED_NO", szStkBedNo);
	    		    	recInTemp.setField("YD_STK_BED_ACT_STAT", "C");
	    		    	/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.updYdStkbed*///TB_YD_STKBED
	    		    	intRtnVal = ydStkBedDao.updYdStkbed(recInTemp, 0);
	    				if(intRtnVal <= 0) {
	    	    			return intRtnVal = -1;
	    	    		}
	    				
	    				//출발정지위치 단정보 Clear, 적치단활성상태를 비활성화로...
	    				recInTemp = JDTORecordFactory.getInstance().create();
	    				recInTemp.setField("YD_STK_COL_GP", szCarldStopLoc);
	    				recInTemp.setField("YD_STK_BED_NO", szStkBedNo);
	    				recInTemp.setField("STL_NO", "");
	    				recInTemp.setField("MODIFIER", "SYSTEM");
	    				recInTemp.setField("YD_STK_LYR_ACT_STAT", "C");
	    				recInTemp.setField("YD_STK_LYR_MTL_STAT", "E");
	    	
	    				/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrYdStkColGpYdStkBedNo*///TB_YD_STKLYR
	    				intRtnVal = ydStkLyrDao.updYdStklyrYdStkColGpBedNo(recInTemp);
	    				if(intRtnVal <= 0) {
	    					szMsg = "<Y5LTcarStartWr> 적치단 정보 활성화중 Error!! ";
	    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    					throw new DAOException("<CarMvHdSeEJBBean> updYdStklyrYdStkColGp " + szMsg);
	    				}
	    	    	
	    			}
	    		}
	    		
				recInTemp = JDTORecordFactory.getInstance().create();
		    	recInTemp.setField("YD_STK_COL_GP", szCarudStopLoc);
		    	
				rsResult1 = JDTORecordFactory.getInstance().createRecordSet("");
				/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedCol*/
				intRtnVal = ydStkBedDao.getYdStkbed(recInTemp, rsResult1, 1 );
				if(intRtnVal <= 0) {
	    			return intRtnVal = -1;
	    		}
				
				for(int Loop_i = 1; Loop_i <= rsResult1.size(); Loop_i++) {
					rsResult1.absolute(Loop_i);
					recInTemp1  = JDTORecordFactory.getInstance().create();
		    		recInTemp1.setRecord(rsResult1.getRecord());		
					
		    		szStkBedNo = ydDaoUtils.paraRecChkNull(recInTemp1, "YD_STK_BED_NO");
		    		
		    		String szYD_STK_BED_ACT_STAT = ydDaoUtils.paraRecChkNull(recInTemp1, "YD_STK_BED_ACT_STAT");
			    	
		    		if(szYD_STK_BED_ACT_STAT.equals("L")){
		    		// 2번 중복으로 온 경우임
		    		} else {
			    		//하차정지위치 Bed상태 활성화
				    	recInTemp = JDTORecordFactory.getInstance().create();
				    	recInTemp.setField("YD_STK_COL_GP", szCarudStopLoc);
				    	recInTemp.setField("YD_STK_BED_NO", szStkBedNo);
				    	recInTemp.setField("YD_STK_BED_ACT_STAT", "L");
				    	/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.updYdStkbed*///TB_YD_STKBED
				    	intRtnVal = ydStkBedDao.updYdStkbed(recInTemp, 0);
						if(intRtnVal <= 0) {
							if(intRtnVal == 0) {
								szMsg="<Y5LTcarStopWr> getYdWrkbookmtl data not found";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
							}else if(intRtnVal == -2) {
								szMsg="<Y5LTcarStopWr> getYdWrkbookmtl parameter error";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							}
							return intRtnVal = -1;
						}
						
			    		recOutTemp = JDTORecordFactory.getInstance().create();
			    		recOutTemp.setField("YD_STK_COL_GP",       szCarudStopLoc);
			    		recOutTemp.setField("YD_STK_BED_NO",       szStkBedNo);
			    		recOutTemp.setField("YD_STK_LYR_NO",       "001");
			    		recOutTemp.setField("YD_STK_LYR_MTL_STAT", "E"); //적치중
			    		recOutTemp.setField("YD_STK_LYR_ACT_STAT", "E"); //적치가능 
			    		/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyr*/
			    		intRtnVal = ydStkLyrDao.updYdStklyr(recOutTemp, 0);
						if(intRtnVal <= 0) {
			    			if(intRtnVal == 0) {
			    				szMsg="<Y5LTcarStopWr> updYdStklyr data not found";
			    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
			    			}else if(intRtnVal == -1) {
			    				szMsg="<Y5LTcarStopWr> updYdStklyr duplicate data,";
			    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
			    			}else if(intRtnVal == -2) {
			    				szMsg="<Y5LTcarStopWr> updYdStklyr parameter error";
			    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			    			}else if(intRtnVal == -3){
			    				szMsg="<Y5LTcarStopWr> updYdStklyr execution failed";
			    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			    			}
			    			return intRtnVal = -1;
			    		}
		    		}	
				}
				
		    	//적치단에 재료정보 Update
		        //하차작업에약ID를 조회해서 작업예약재료 Table를 조회한다.
		    	szWbookId = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARUD_WRK_BOOK_ID");
		    	
		    	recInTemp = JDTORecordFactory.getInstance().create();
		    	recInTemp.setField("YD_TCAR_SCH_ID", szYD_TCAR_SCH_ID);
		    	rsResult  = JDTORecordFactory.getInstance().createRecordSet("Temp");
		    	/*com.inisteel.cim.yd.dao.ydtcarftmvmtldao.YdTcarftmvmtlDao.getYdStockTcarSchIdSch*/
		    	intRtnVal = ydTcarFtmvMtlDao.getYdTcarftmvmtl(recInTemp, rsResult, 301);
				if(intRtnVal <= 0) {
					if(intRtnVal == 0) {
						szMsg="<Y5LTcarStopWr> getYdWrkbookmtl data not found";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
					}else if(intRtnVal == -2) {
						szMsg="<Y5LTcarStopWr> getYdWrkbookmtl parameter error";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
					return intRtnVal = -1;
				}
		    	
		    	
		    	//작업예약재료의 정보를 적치단에 등록한다.
		    	for(int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {
		    		rsResult.absolute(Loop_i);
		    		recInTemp  = JDTORecordFactory.getInstance().create();
		    		recInTemp.setRecord(rsResult.getRecord());
		    		
		    		recOutTemp = JDTORecordFactory.getInstance().create();
		    		recOutTemp.setField("YD_STK_COL_GP",       szCarudStopLoc);
		    		recOutTemp.setField("YD_STK_BED_NO",       ydDaoUtils.paraRecChkNull(recInTemp, "YD_STK_BED_NO"));
		    		recOutTemp.setField("YD_STK_LYR_NO",       "001");
		    		recOutTemp.setField("STL_NO",              ydDaoUtils.paraRecChkNull(recInTemp, "STL_NO"));
		    		recOutTemp.setField("YD_STK_LYR_MTL_STAT", "C"); //적치중
		    		recOutTemp.setField("YD_STK_LYR_ACT_STAT", "E"); //적치가능 
		    		/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyr*/
		    		intRtnVal = ydStkLyrDao.updYdStklyr(recOutTemp, 0);
					if(intRtnVal <= 0) {
	    				szMsg="<Y5LTcarStopWr> updYdStklyr execution failed";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    			return intRtnVal = -1;
		    		}
					
			    	//하차크레인 스케줄 호출
			    	recInTemp1 = JDTORecordFactory.getInstance().create();
			    	recInTemp1.setField("YD_WBOOK_ID", ydDaoUtils.paraRecChkNull(recInTemp, "YD_WBOOK_ID"));
			    	intRtnVal = this.Y5CallCrnSch(recInTemp1);	

		    	}
		    	
//		    	//하차크레인 스케줄 호출
//		    	recInTemp = JDTORecordFactory.getInstance().create();
//		    	recInTemp.setField("YD_WBOOK_ID", szWbookId);
//		    	intRtnVal = this.Y5CallCrnSch(recInTemp);	
	    	}
	    		
 		}catch(Exception e){
	
			szMsg="영차 도착실적 처리 Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			return intRtnVal = -1;
		}
	
	
		szMsg="영차 도착실적 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		return intRtnVal = 1;
	} //end of Y5LTcarStopWr()
	
	
	
	
	
	
	
	/**
	 * 오퍼레이션명 : 크레인스케줄호출
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public int Y5CallCrnSch(JDTORecord msgRecord)throws JDTOException  {
		YdWrkbookDao     ydWrkbookDao     = new YdWrkbookDao();
//		YdSchRuleDao     ydSchRuleDao     = new YdSchRuleDao();
//		YdDelegate       ydDelegate 	  = new YdDelegate();
		JDTORecord outRecord2    = JDTORecordFactory.getInstance().create();
		
		JDTORecordSet rsResult          = null;
		JDTORecord    recInTemp         = null;

	    
	    String szMsg              		= "";
	    String szMethodName       		= "Y5CallCrnSch";

	    int intRtnVal                   = 0;

	    String szWbookId                = "";
	    String szSchCd                  = "";
	    String szEqpId                  = "";
	    EJBConnector ejbConn = null;
	    
	    try{
	    	//작업예약ID
	    	szWbookId = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");
	    	
	    	//작업예약 id로 작업예약Table를 조회한다.
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	intRtnVal = ydWrkbookDao.getYdWrkbook(msgRecord, rsResult, 0);
			if(intRtnVal <= 0) {
				if(intRtnVal == 0) {
					szMsg="<Y5CallCrnSch> getYdWrkbook data not found";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				}else if(intRtnVal == -2) {
					szMsg="<Y5CallCrnSch> getYdWrkbook parameter error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
				return intRtnVal = -1;
			}
	    	
	    	rsResult.absolute(1);
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setRecord(rsResult.getRecord());
	    	
	    	//스케줄코드
	    	szSchCd = ydDaoUtils.paraRecChkNull(recInTemp, "YD_SCH_CD");
	    	
	    	/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			/*     스케쥴기준 조회 - 스케쥴 금지유무 판단, 작업크레인, 대체크레인 조회	*/
			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	    	recInTemp = JDTORecordFactory.getInstance().create();
			intRtnVal = YdCommonUtils.getCrnInfoByCrnSchRule(szSchCd, recInTemp);
			if( intRtnVal < 0 ) {
				szMsg="스케줄 기준 조회 Error Code : " + intRtnVal;
    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return intRtnVal;
			}
			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	    	
	    	//크레인설비ID
	    	szEqpId = ydDaoUtils.paraRecChkNull(recInTemp, "YD_WRK_CRN");

			JDTORecord[] inRecordarr   	= null;
			inRecordarr = new JDTORecord[1];
	
			// 스케줄 기동			
			inRecordarr[0] = JDTORecordFactory.getInstance().create();
			inRecordarr[0].setField("YD_SCH_CD"		, szSchCd); 
			inRecordarr[0].setField("YD_WBOOK_ID"	, szWbookId); 
			inRecordarr[0].setField("YD_EQP_ID"		, szEqpId); 
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			outRecord2 = (JDTORecord) ejbConn.trx("trxRunSchedule", new Class[] { JDTORecord[].class },	new Object[] { inRecordarr });
	    	

//	    	recInTemp = JDTORecordFactory.getInstance().create();
//	    	recInTemp.setField("MSG_ID"		, "YDYDJ509");
//	    	recInTemp.setField("YD_SCH_CD"	, szSchCd);
//	      	recInTemp.setField("YD_EQP_ID"	, szEqpId);
//	      	recInTemp.setField("YD_WBOOK_ID", szWbookId);
//
//	    	//서버 메세지 전송 메소드 크레인 스케줄 호출
//	    	ydDelegate.sendMsg(recInTemp);

	    	
 		}catch(Exception e){
	
			szMsg="크레인스케줄 호출 Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			return intRtnVal = -1;
		}
	
	
		szMsg="크레인스케줄 호출("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		return intRtnVal = 1;
	} //end of Y5CallCrnSch()
	
	
    /**
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
	 * 오퍼레이션명 : 설비 등록처리
	 * @exception
	 * @modelguid XDE가 생성한 ID를 위치시킨다.
	 * @author  송정현
	 * @date 	2009.04.16
	 * @ejb.transaction type="RequiresNew"
     */
    public JDTORecord updYdEqp(JDTORecord msgRecord) throws JDTOException {
    	int intRtnVal = 0 ;
    	JDTORecord outRecord       		= JDTORecordFactory.getInstance().create(); // 
 
    	String szMsg        = "";
    	String szMethodName = "updYdEqp";

    	YdEqpDao     ydEqpDao     = new YdEqpDao();
    	try{
			
  
    		intRtnVal = ydEqpDao.updYdEqp(msgRecord, 0);
			if(intRtnVal <= 0) {
				szMsg="<updYdEqp> YdEqp execution failed";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	  			outRecord.setField("RTN_CD" , "0");	
				return outRecord;
			}
	    	
			
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
        }//end of try~catch
		outRecord.setField("RTN_CD" , "1");	
		return outRecord;
   }// end of Y5UpdYdCrnsch
		
	
	
	
	
	
	
	

  //---------------------------------------------------------------------------	
} // end of class TcarMvHdSeEJBBean
