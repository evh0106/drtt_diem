package com.inisteel.cim.yd.ydWkAct.CraneLoadWkrHd;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.dao.YdCommDAO;
import com.inisteel.cim.yd.common.dao.ptStlFrtoMoveDao.PtStlFrtoMoveDao;
import com.inisteel.cim.yd.common.dao.ydCarFtmvMtlDao.YdCarFtmvMtlDao;
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.dao.ydEqpDao.YdEqpDao;
import com.inisteel.cim.yd.common.dao.ydStkBedDao.YdStkBedDao;
import com.inisteel.cim.yd.common.dao.ydStkColDao.YdStkColDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.dao.ydTcarFtmvMtlDao.YdTcarFtmvMtlDao;
import com.inisteel.cim.yd.common.dao.ydTcarSchDao.YdTcarSchDao;
import com.inisteel.cim.yd.common.dao.ydWrkPlnSimulationDao.YdWrkPlnSimulationDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookDao.YdWrkbookDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.DaoManager;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDBAssist;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdTcConst;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;

/**
 * 권상실적처리 Session EJB
 *
 * @ejb.bean name="CoilCraneLdHdSeEJB" jndi-name="CoilCraneLdHdSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class CoilCraneLdHdSeEJBBean extends BaseSessionBean {
	
	// Session Name
	private String szSessionName=getClass().getName();
	
	private YdUtils ydUtils =new YdUtils();
	
	private YdDaoUtils ydDaoUtils =new YdDaoUtils();
	
	private YdTcConst ydTcConst =new YdTcConst();
	
	private YdDelegate ydDelegate =new YdDelegate();
	
	private YdDBAssist ydDBAssist =new YdDBAssist();
	// [DEBUG] message flag
	private boolean bDebugFlag=true;
	
	private EJBConnector ydEjbCon = new EJBConnector("default", this);
	
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
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			
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
	
    
	
//    /**
//     * 오퍼레이션명 : C열연코일야드L2 크레인작업지시 (Y5YDL007)
//     *  
//     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//     * @param msgRecord
//     * @return String
//     * @throws JDTOException
//     */
//    public String procY5CrnWrkOrdReq(JDTORecord msgRecord)throws JDTOException  {
//
//    	YdDelegate   ydDelegate   = new YdDelegate();
//    	YdCrnSchDao  YdCrnSchDao  = new YdCrnSchDao();
//    	YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
//    	YdEqpDao     ydEqpDao     = new YdEqpDao();
//    	JPlateYdCommDAO ydCommDao = new JPlateYdCommDAO();
//    	JDTORecord recCrnSch = JDTORecordFactory.getInstance().create();
//    	JDTORecord inRecord = JDTORecordFactory.getInstance().create();
//    	JDTORecord recInPara = null;
//    	JDTORecord recOutTemp = null;
//    	JDTORecord recIntTemp = null;
//    	JDTORecord recPara = null;
//        JDTORecordSet rsCrnSch = JDTORecordFactory.getInstance().createRecordSet("temp");
//        JDTORecordSet  rsResult= JDTORecordFactory.getInstance().createRecordSet("temp");
//        JDTORecordSet inRecordSet = JDTORecordFactory.getInstance().createRecordSet("temp");
//        JDTORecord 		inRecord1 = JDTORecordFactory.getInstance().create();
//    	JDTORecordSet rsWrkBook = null;
//
//        JDTORecord 		inRecord2 = JDTORecordFactory.getInstance().create();
//        JDTORecord 		outRecord4 = JDTORecordFactory.getInstance().create();
//    	
//        int intRtnVal 					= 0 ;
//        int intGfCoilChk                = 0 ;
//        
//        String szMsg              		= "";
//        String szMethodName       		= "procY5CrnWrkOrdReq";
//        String szOperationName			= "코일크레인작업지시";
//        
//        String szCmdChk					= "";
//        String szEqpId                  = "";
//        String szWrkProgStat            = "";
//        String szYD_GP                  = "";
//        String szYD_BAY_GP              = "";
//        EJBConnector ejbConn = null;
//        String szRtnMsg					= null;
//        String szLogMsg 			= "";
//        String sYD_STK_COL_GP			= "";
//        String szFLAG_CANCEL            = "";
//        String sQueryId					= "";
//        JDTORecord outRecord1	= JDTORecordFactory.getInstance().create();
//        JDTORecord outRecord2	= JDTORecordFactory.getInstance().create();
//        
//        
//        String szRcvTcCode=ydUtils.getTcCode(msgRecord);
//        if(szRcvTcCode==null || szRcvTcCode.equals("")){
//        	szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
//        	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//        	return YdConstant.RETN_CD_TC_ERROR;
//        }
//        
//        	
//        
//        
//        if(bDebugFlag){
//            szMsg="[DEBUG] Line 187 전문수신 : TCCODE=" +szRcvTcCode ;
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//        }
//
//        try{
//			//=============================================================
//		// Log 테이블 등록  
//			//=============================================================
//			szMsg = "[열연 코일야드L2] Line 195 크레인작업지시 수신";
//			ydUtils.putLogMsg("K", YdConstant.YD_MONITORING_CHANNEL_K, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
//
//			
//        	szEqpId     = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
//        	szCmdChk    = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CMD_CHK");
//        	szYD_GP     = szEqpId.substring(0,1);
//        	szYD_BAY_GP = szEqpId.substring(1,2);
//   
//        	//야드 작업 진행상태를 check한다.    
//        	szWrkProgStat = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WRK_PROG_STAT");
//        	String szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord,  "YD_SCH_CD");
//        	
//        	szMsg="["+szOperationName+"] 크레인설비["+szEqpId+"] 상태 체크 완료 -◈◈◈ 야드 작업 진행상태 : " + szWrkProgStat;
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//        	
//        	// 야드설비상태 Check		수신받은  야드설비Id로 설비Table를 조회하여 야드설비상태를 Check하고 고장이면 return
//			//bRtnCheck = this.eqpStatCheck(szEqpId);
//			//if(!bRtnCheck) return YdConstant.RETN_CD_FAILURE;
//        	
//        	//------------------------------------------------------------------------------------------------------
//        	// 야드설비상태 Check		수신받은  야드설비Id로 설비Table를 조회하여 야드설비상태를 Check하고 고장이면 return
//        	//------------------------------------------------------------------------------------------------------
//        	
//        	szMsg="["+szOperationName+"] 크레인설비["+szEqpId+"] 상태 체크 시작";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//        	
//        	szRtnMsg = this.eqpStatCheck(szEqpId);
//        	
//        	if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
//        		szMsg="["+szOperationName+"] 크레인설비["+szEqpId+"] 상태 체크 시 오류발생 - 메세지 : " + szRtnMsg;
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//        		return szRtnMsg;
//        	}	
//        	
//        	
//        	
//        	
////        	151216 hun 지포장장 투입가능 여부 체크
//			ydUtils.putLog(szSessionName, szMethodName, "★ 지포장장 To위치 가능 여부 체크 start★", YdConstant.INFO);
//			rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
//		    inRecord = JDTORecordFactory.getInstance().create();
//		    inRecord.setField("YD_BAY_GP",           szEqpId.substring(1,2));
//		    sQueryId = "com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocByGFplace";
//		    intGfCoilChk = ydCommDao.select(inRecord, rsResult, sQueryId);
//		    
//		    ydUtils.putLog(szSessionName, szMethodName, "★ 지포장장 To위치 가능 여부 체크 end★", YdConstant.INFO);
//			
//        	
//        	
//        	//----------------------------------------------------------------
//        	if("W".equals(szWrkProgStat)
//    			||"1".equals(szWrkProgStat)
//    			||"2".equals(szWrkProgStat)
//    			||"3".equals(szWrkProgStat)
//    			||"4".equals(szWrkProgStat)){
//        		
//	        	//레코드 생성
//				recPara = JDTORecordFactory.getInstance().create();
//				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
//				
//				//설비ID를 작업크레인으로 설정
//				recPara.setField("YD_EQP_ID", szEqpId);
//	        	szRtnMsg		= DaoManager.getYdEqp(recPara, rsResult, 0);
//				
//				if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
//					if( szRtnMsg.equals(YdConstant.RETN_CD_NOTEXIST) ) {
//						return YdConstant.YD_EQP_NOTEXIST;
//					}
//				}
//				
//				//레코드 추출
//				rsResult.first();
//				recPara = rsResult.getRecord();
//				
//				//설비상태
//				szWrkProgStat = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_STAT");
//        	}
//			//------------------------------------------------------------------
//        	
//        	szMsg="["+szOperationName+"] 크레인설비["+szEqpId+"] 상태 체크 완료 - 메세지 : " + szRtnMsg;
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//        	
//        	//------------------------------------------------------------------------------------------------------			
//			
//        	szMsg="["+szOperationName+"] 크레인설비["+szEqpId+"] 상태 체크 완료 -◈◈◈ 최종 야드 작업 진행상태 : " + szWrkProgStat;
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		
//        	
//			 
//        	//야드 작업 진행상태가 1,3인 경우 (작업지시를 재요구 하는 경우 사용한다.)
//        	if ((szWrkProgStat.equals("1") || szWrkProgStat.equals("2") || szWrkProgStat.equals("3"))&& !szCmdChk.equals("Y")){
//				szMsg="야드 작업 진행상태가 1,3인 경우";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
////         		intRtnVal = this.Y5ChkWrkProgStat(msgRecord, rsCrnSch);
////        		//크레인 스케줄이 없다면 없다는 메시지를 L2에 전송해야한다. 현재는 종료처리중..수정할것
////        		if( intRtnVal == 0 ) {
////            		//더이상 크레인 스케줄에 작업이 없을 경우 설비Table에 설비상태를 'W' 명령선택대기(idle)로 변경한다.
////            		recIntTemp = JDTORecordFactory.getInstance().create();
////            		recIntTemp.setField("YD_EQP_ID", szEqpId);
////            		recIntTemp.setField("YD_EQP_STAT", "W");	//'W' 명령선택대기
////            		intRtnVal = ydEqpDao.updYdEqp(recIntTemp, 0);
////        			if(intRtnVal <= 0) {
////    	    			if(intRtnVal == 0) {
////    	    				szMsg=" Y5ChkWrkProgStatW updYdEqp : data not found";
////    	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
////    	    			}else if(intRtnVal == -1) {
////    	    				szMsg=" Y5ChkWrkProgStatW updYdEqp : duplicate data,";
////    	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
////    	    			}else if(intRtnVal == -2) {
////    	    				szMsg=" Y5ChkWrkProgStatW updYdEqp : parameter error";
////    	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
////    	    			}else if(intRtnVal == -3){
////    	    				szMsg=" Y5ChkWrkProgStatW updYdEqp : execution failed";
////    	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
////    	    			}
////    	    		}
////        			
////        			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
////        	         * 			코일야드 송신 크레인작업실적응답 전송  - YDY3L005
////        	         * 업무기준 Desc :에서 크레인 작업지시 요구 시 크레인 작업이 더 이상 존재하지 않을 경우 크레인작업실적응답 전송
////        	         * 기능 추가 : 임춘수
////        	         * 일자 : 2009.06.19
////        	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
////        			recIntTemp = JDTORecordFactory.getInstance().create(); 
////        			recIntTemp.setField("MSG_ID"        , "YDY5L005");
////        			recIntTemp.setField("YD_EQP_ID"     , szEqpId);						//야드설비ID
////        			recIntTemp.setField("YD_L2_WR_GP"   , YdConstant.CRN_WRK_RE_WO_DMD);	//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
////        			recIntTemp.setField("YD_L3_HD_RS_CD", YdConstant.CRN_WRK_RE_CD_NO_WRK);	//야드L3처리결과코드 : 9999 - 크레인작업이 더 이상 없습니다.
////        			ydDelegate.sendMsg(recIntTemp);
////        			szMsg = "[코일 크레인 작업지시 요구]크레인 작업이 더 이상 존재하지 않을 경우코일야드L2 크레인작업실적응답[YDY3L005] 전송 완료" ;
////                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
////        	        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
////                    
////    				szMsg="더이상의 크레인스케줄이 조회되지 않습니다.";
////    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
////        			
////        			return YdConstant.RETN_CRN_NO_SCH;
////        		}else if(intRtnVal == -1) {
////        			return YdConstant.RETN_CD_FAILURE;
////        		}else{
//				 
//			 
//						//설비id로 크레인 스케줄을 조회한다. 현재 작업진행상태가 1또는 3인 경우를 조회...
//			        	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
//			        	recPara = JDTORecordFactory.getInstance().create();
//			        	recPara.setField("YD_EQP_ID", szEqpId);
//			        	/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnSchEqpIdWrkProgStat*/
//				    	intRtnVal = YdCrnSchDao.getYdCrnsch(recPara, rsResult, 16);
//						if(intRtnVal <= 0) {
//							//에러처리
//							szMsg="현재 작업진행상태가 1또는 3인 크레인 스케줄을 조회 중 Error";
//							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//							return YdConstant.RETN_CD_FAILURE;
//						}
//			 
//        			
//        			//현재크레인이 작업중인 스케줄의 값을 다시 재전송한다.
//					rsResult.absolute(1);
//        			recOutTemp = JDTORecordFactory.getInstance().create();
//        			recOutTemp.setRecord(rsResult.getRecord());
//        			
//        			String szYD_CRN_SCH_ID			= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CRN_SCH_ID");
//        			String szYD_WRK_PROG_STAT		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WRK_PROG_STAT");
//        			
//                	recInPara = JDTORecordFactory.getInstance().create();
//            		//작업지시 전문 전송 data setup
//        			recInPara.setField("MSG_ID", 			"YDY5L004");
//                	recInPara.setField("YD_CRN_SCH_ID",    	szYD_CRN_SCH_ID);
//                	recInPara.setField("YD_WRK_PROG_STAT", 	szYD_WRK_PROG_STAT);
//                	recInPara.setField("YD_SCH_CD",        	ydDaoUtils.paraRecChkNull(recOutTemp, "YD_SCH_CD"));
//                	recInPara.setField("YD_GP",            	ydDaoUtils.paraRecChkNull(recOutTemp, "YD_GP"));
//                	recInPara.setField("MODIFIER", 			"YDSYSTEM");
//                	
//                	//20090925 김진욱 추가 : 작업진행중인 작업을 재전송하는 경우는  MSG_GP 값을 'U' UPDATE로 설정해서 보낸다.
//                	recInPara.setField("MSG_GP", 			"U");
//                	ydDelegate.sendMsg(recInPara);
//                	szMsg = "["+szOperationName+"] 현재 작업중["+szYD_WRK_PROG_STAT+"]인 스케줄["+szYD_CRN_SCH_ID+"]을 재전송";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//        			
//                	return YdConstant.RETN_CD_SUCCESS;
////        		}
//        		
//        		
//        	//야드 작업 진행 상태가 'W'인경우 (작업이 없을때 요구)	
//        	}else if(szWrkProgStat.equals("W") || szCmdChk.equals("Y")){
//				szMsg="야드 작업 진행 상태가 'W'인경우";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
////				보급은 예약을 체크하지 않는다.
//	 	    	
//        		intRtnVal = this.Y5ChkWrkProgStatW(msgRecord, rsCrnSch);
//        		if(intRtnVal == -1) {
//    				szMsg="크레인 스케줄이 조회되지 않습니다.";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//        			return YdConstant.RETN_CRN_NO_SCH;
//        		}
//        		
//        		
//        	//야드 작업 진행 상태가 '4'인 경우 현재 진행중인 작업이 있을 경우 (현재 진행중인 작업이 있을 경우 해당작업을 호출한다.스케줄 코드로 조회, 조회한 data가 없다면 스케줄우선순위가 빠르고 크레인스케줄id가 가장빠른 작업을 보내준다.)
//          	}else if(szWrkProgStat.equals("4")) {
//				szMsg="야드 작업 진행 상태가 '4'인 경우 (권하실적처리에서 호출)";
//
//				recIntTemp = JDTORecordFactory.getInstance().create();
//        		recIntTemp.setField("YD_EQP_ID"		, szEqpId);
//        		recIntTemp.setField("YD_EQP_STAT"	, "W");	//'W' 명령선택대기
//        		intRtnVal = ydEqpDao.updYdEqp(recIntTemp, 0);
//    			if(intRtnVal <= 0) {
//	    			if(intRtnVal == 0) {
//	    				szMsg=" Y5ChkWrkProgStatW updYdEqp : data not found";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//	    			}else if(intRtnVal == -1) {
//	    				szMsg=" Y5ChkWrkProgStatW updYdEqp : duplicate data,";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
//	    			}else if(intRtnVal == -2) {
//	    				szMsg=" Y5ChkWrkProgStatW updYdEqp : parameter error";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	    			}else if(intRtnVal == -3){
//	    				szMsg=" Y5ChkWrkProgStatW updYdEqp : execution failed";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	    			}
//	    		}
//
//				
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//        		
//				intRtnVal = this.Y5ChkWrkProgStat4(msgRecord, rsCrnSch);
//        		
//	    		//더이상 크레인 스케줄을 찾지 못했을 경우 설비상태를 W <--idle로 변경하고 0으로 리턴, 에러인 경우  -1로 리턴.. 크레인 스케줄 호출부분이 아직 없기때문에 종료처리...추후 변경 0일경우는 크레인 스케줄 호출..
//	    		if(intRtnVal <= 0) {
//					szMsg="더이상 크레인 스케줄을 찾지못했거나 Error가 발생했을 경우 작업예약을 조회한다. intRtnVal : " + intRtnVal;
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//					
//					String sYD_SCH_CD	= ydDaoUtils.paraRecChkNull(msgRecord,  "YD_SCH_CD");
//					//작업예약조회 
//	    			recInPara = JDTORecordFactory.getInstance().create();
//	    			recInPara.setField("YD_EQP_ID", szEqpId);
//	    			rsWrkBook = JDTORecordFactory.getInstance().createRecordSet("");
////	    	    	//보급은 예약을 체크하지 않는다.
////	    			if((sYD_SCH_CD.equals("HEDE01UM"))||(sYD_SCH_CD.equals("HGFE01UM"))||(sYD_SCH_CD.equals("HHKE01UM")) ||  //보급존
//// 	     	 	       (sYD_SCH_CD.equals("HHKD01UM"))||(sYD_SCH_CD.equals("HEDD01UM")) ){                                //HFL 보급존
////	    				
//// 	 	    	    } else { 
//// 	 	    	    	/*com.inisteel.cim.yd.dao.ydWrkbookDao.getWorkTbRefNONECoil*/
//// 	 	    	    	intRtnVal = ydWrkbookDao.getYdWrkbook(recInPara, rsWrkBook, 501);
////	 	    			if(intRtnVal > 0) {
////		    				//검색된 작업예약을 크레인스케줄을 생성한다. 생성하면 설비상태값에 따라서 작업지시를 내려보내도록되어있다.
////		    				szMsg="현재 크레인(" + szEqpId + ")로 작업할 수 있는 작업예약을 조회하였습니다.  크레인 스케줄Main호출";
////		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
////		    				//크레인 스케줄 호출 (설비id,스케줄코드);
////		    				rsWrkBook.absolute(1);
////		    				recOutTemp = JDTORecordFactory.getInstance().create();
////		    				recOutTemp.setRecord(rsWrkBook.getRecord());
////	
////		    				recInPara = JDTORecordFactory.getInstance().create();
////		    				
////		    				recInPara.setField("MSG_ID"		, "YDYDJ509");
////		    				recInPara.setField("YD_EQP_ID"	, szEqpId);
////		    				recInPara.setField("YD_SCH_CD"	, ydDaoUtils.paraRecChkNull(recOutTemp, "YD_SCH_CD"));
////		    				recInPara.setField("YD_WBOOK_ID", ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WBOOK_ID"));
////		    				
////		    	        	//크레인 스케줄 호출 메세지 전송
////		    	    		ydDelegate.sendMsg(recInPara);
////		    	    		//다음 크레인스케줄이 생성되는 중이기때문에 현재상태에서는 작업지시를 내려보내지않고 리턴
////		    	    		return YdConstant.RETN_CD_SUCCESS;	    			    				
////		    			} 
//// 	 	    	    }	
//
//					szMsg="SQL2 : " + szEqpId;
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//					
////	 	    	    	/*com.inisteel.cim.yd.dao.ydWrkbookDao.getWorkTbRefNONECoil*/
//	 	    	    intRtnVal = ydWrkbookDao.getYdWrkbook(recInPara, rsWrkBook, 501);
//	 	        	for(int Loop_i = 1; Loop_i <= rsWrkBook.size(); Loop_i++) {
//	 	        		rsWrkBook.absolute(Loop_i);
//	 	        		inRecord = JDTORecordFactory.getInstance().create();
//	 	        		inRecord.setRecord(rsWrkBook.getRecord());
//	 	        		String sYD_SCH_CD_TMP			= ydDaoUtils.paraRecChkNull(inRecord,"YD_SCH_CD");
//	 	        		String sYD_WBOOK_ID_TMP			= ydDaoUtils.paraRecChkNull(inRecord,"YD_WBOOK_ID");
//	 	        		String sYD_WRK_PLAN_TCAR_TMP	= ydDaoUtils.paraRecChkNull(inRecord,"YD_WRK_PLAN_TCAR");
////C증설	 	        		
//	 	        		//보급은 예약을 체크하지 않는다.
//		    			if((sYD_SCH_CD_TMP.equals("HBKE01UM"))||
//			 	     	   (sYD_SCH_CD_TMP.equals("HBKD01UM"))||
//			 	     	   (sYD_SCH_CD_TMP.equals("HAKE01UM"))||
//			 	     	   (sYD_SCH_CD_TMP.equals("HAKD01UM"))||
//			 	     	   
//				    	   (sYD_SCH_CD_TMP.equals("HBFE01UM"))||
//				    	   
//	 	     	 	       (sYD_SCH_CD_TMP.equals("HCKE01UM"))||
//	 	     	 	       (sYD_SCH_CD_TMP.equals("HCKD01UM"))||
//	 	     	 	       (sYD_SCH_CD_TMP.equals("HCFE01UM"))||
//		     			 
//				    	   (sYD_SCH_CD_TMP.equals("HEDE01UM"))||
//	 	     	 	       (sYD_SCH_CD_TMP.equals("HEDD01UM"))||
//
//	 	     	 	       (sYD_SCH_CD_TMP.equals("HGFE01UM"))||
//				    	   (sYD_SCH_CD_TMP.equals("HHKE01UM"))||
//	 	     	 	       (sYD_SCH_CD_TMP.equals("HHKD01UM"))
//	 	     	 	       ) { 
//		    			
////대차인 상차 경우 설비 정보 READ 	
//	 	 	    	    } else if ((sYD_SCH_CD_TMP.substring(2,4).equals("TC")) && 
//	 	 	    	    		   (sYD_SCH_CD_TMP.substring(6,8).equals("UM")) && 
//	 	 	    	    		   (!sYD_WRK_PLAN_TCAR_TMP.equals(""))){
//	 	 	    	    	
//	 	 	    	    	inRecordSet = JDTORecordFactory.getInstance().createRecordSet("");
//	 	 	    	    	inRecord1 	= JDTORecordFactory.getInstance().create();
//	 	 	    	    	outRecord2 	= JDTORecordFactory.getInstance().create();
//		 	 	  			
//		 	 	  			//설비ID를 대차으로 설정
//	 	 	    	    	inRecord1.setField("YD_EQP_ID", sYD_WRK_PLAN_TCAR_TMP);
//		 	 	  			
//	 	 	    	    	intRtnVal = ydEqpDao.getYdEqp(inRecord1, inRecordSet, 0);
//	 	 	    	    	if(intRtnVal > 0) {
//		 	 	    	    	inRecordSet.first();
//			 	 	  			outRecord2 = inRecordSet.getRecord();
////설비상태
//			 	 	  			String YD_GP1 			= ydDaoUtils.paraRecChkNull(outRecord2, "YD_GP");
//			 	 	  			String YD_CURR_BAY_GP1	= ydDaoUtils.paraRecChkNull(outRecord2, "YD_CURR_BAY_GP");
//			 	 	  			String YD_EQP_STAT1 	= ydDaoUtils.paraRecChkNull(outRecord2, "YD_EQP_STAT");
//			 	 	  			
//			 	 	  			if((YD_GP1.equals(sYD_SCH_CD_TMP.substring(0,1))) &&             //야드구분 
//			 	 	  			   (YD_CURR_BAY_GP1.equals(sYD_SCH_CD_TMP.substring(1,2))) &&    //현재동
//			 	 	  			   (YD_EQP_STAT1.equals("A"))){                                  //도착
//
//			 	 					inRecord2 = JDTORecordFactory.getInstance().create();
//			 	 					inRecord2.setField("YD_SCH_CD"		, sYD_SCH_CD_TMP);//스케줄코드
//			 	 					inRecord2.setField("YD_WBOOK_ID"	, sYD_WBOOK_ID_TMP);  //
//			 	 					ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
//			 	 					outRecord4 = (JDTORecord)ejbConn.trx("getStkColTCarUpChk", new Class[] { JDTORecord.class }, new Object[] { inRecord2 });
//			 	 					
//			 	 					String sRTN_CD	= StringHelper.evl(outRecord4.getFieldString("RTN_CD"), "0");
//			 	 					String sSTL_CNT	= StringHelper.evl(outRecord4.getFieldString("STL_CNT"), "0");
//			 	 					String sSUM_WGT	= StringHelper.evl(outRecord4.getFieldString("SUM_WGT"), "0");
//			 	 					
//			 	 					if ("0".equals(sRTN_CD)) {
//			 	 						szMsg = "상차 가능 CHECK시  ERROR";
//			 	 						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			 	 					}	
//			 	 					
//			 	 					double dSTL_CNT = Integer.parseInt(sSTL_CNT);
//			 	 					double dSUM_WGT = Integer.parseInt(sSUM_WGT);
//			 	 					if (dSTL_CNT <= 3) {
//			 	 						if (dSUM_WGT <= YdConstant.YD_COIL_TC_WEIGH_MAX) {
//				 	 						szMsg = "대차 상차 가능 ok";
//				 	 						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//			 	 							recInPara = JDTORecordFactory.getInstance().create();
//			 			    				recInPara.setField("MSG_ID"		, "YDYDJ509");
//			 			    				recInPara.setField("YD_EQP_ID"	, szEqpId);
//			 			    				recInPara.setField("YD_SCH_CD"	, sYD_SCH_CD_TMP);
//			 			    				recInPara.setField("YD_WBOOK_ID", sYD_WBOOK_ID_TMP);
//			 			    				
//			 			    	        	//크레인 스케줄 호출 메세지 전송
//			 			    	    		ydDelegate.sendMsg(recInPara);
//			 			    	    		//다음 크레인스케줄이 생성되는 중이기때문에 현재상태에서는 작업지시를 내려보내지않고 리턴
//			 			    	    		return YdConstant.RETN_CD_SUCCESS;	    			    				
//			 	 						}
//			 	 					} 
//			 	 	  			}
//	 	 	    	    	}
//	 	 	    	    } else if ((sYD_SCH_CD_TMP.substring(2,4).equals("TC")) && 
//	 	 	    	    		   ((sYD_SCH_CD_TMP.substring(6,8).equals("UJ")) ||	(sYD_SCH_CD_TMP.substring(6,8).equals("UB"))) && 
//	 	 	    	    		   (!sYD_WRK_PLAN_TCAR_TMP.equals(""))){
//
////	 	 	    	    } else if (((sYD_SCH_CD_TMP.equals("HDYD03UM")) || 
//// 	 	    	    		    (sYD_SCH_CD_TMP.equals("HEYD03UM")) ||
//// 	 	    	    		    (sYD_SCH_CD_TMP.equals("HFYD03UM")) || 
//// 	 	    	    		    (sYD_SCH_CD_TMP.equals("HGYD03UM")) || 
//// 	 	    	    		    (sYD_SCH_CD_TMP.equals("HHYD03UM")) ) &&
//// 	 	    	    		    (!sYD_WRK_PLAN_TCAR_TMP.equals(""))){
//	 	 	    	    	
//	 	 	    	    	inRecordSet = JDTORecordFactory.getInstance().createRecordSet("");
//	 	 	    	    	inRecord1 	= JDTORecordFactory.getInstance().create();
//	 	 	    	    	outRecord2 	= JDTORecordFactory.getInstance().create();
//		 	 	  			
//		 	 	  			//설비ID를 대차으로 설정
//	 	 	    	    	inRecord1.setField("YD_EQP_ID", sYD_WRK_PLAN_TCAR_TMP);
//		 	 	  			
//	 	 	    	    	intRtnVal = ydEqpDao.getYdEqp(inRecord1, inRecordSet, 0);
//	 	 	    	    	if(intRtnVal > 0) {
//		 	 	    	    	inRecordSet.first();
//			 	 	  			outRecord2 = inRecordSet.getRecord();
////설비상태
//			 	 	  			String YD_GP1 			= ydDaoUtils.paraRecChkNull(outRecord2, "YD_GP");
//			 	 	  			String YD_CURR_BAY_GP1	= ydDaoUtils.paraRecChkNull(outRecord2, "YD_CURR_BAY_GP");
//			 	 	  			String YD_EQP_STAT1 	= ydDaoUtils.paraRecChkNull(outRecord2, "YD_EQP_STAT");
//			 	 	  			
//			 	 	  			if((YD_GP1.equals(sYD_SCH_CD_TMP.substring(0,1))) &&             //야드구분 
//			 	 	  			   (YD_CURR_BAY_GP1.equals(sYD_SCH_CD_TMP.substring(1,2))) &&    //현재동
//			 	 	  			   (YD_EQP_STAT1.equals("A"))){                                  //도착
//
//			 	 					inRecord2 = JDTORecordFactory.getInstance().create();
//			 	 					inRecord2.setField("YD_SCH_CD"		, sYD_SCH_CD_TMP);//스케줄코드
//			 	 					inRecord2.setField("YD_WBOOK_ID"	, sYD_WBOOK_ID_TMP);  //
//			 	 					ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
//			 	 					outRecord4 = (JDTORecord)ejbConn.trx("getStkColTCarUpChk", new Class[] { JDTORecord.class }, new Object[] { inRecord2 });
//			 	 					
//			 	 					String sRTN_CD	= StringHelper.evl(outRecord4.getFieldString("RTN_CD"), "0");
//			 	 					String sSTL_CNT	= StringHelper.evl(outRecord4.getFieldString("STL_CNT"), "0");
//			 	 					String sSUM_WGT	= StringHelper.evl(outRecord4.getFieldString("SUM_WGT"), "0");
//			 	 					
//			 	 					if ("0".equals(sRTN_CD)) {
//			 	 						szMsg = "상차 가능 CHECK시  ERROR";
//			 	 						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			 	 					}	
//			 	 					
//			 	 					double dSTL_CNT = Integer.parseInt(sSTL_CNT);
//			 	 					double dSUM_WGT = Integer.parseInt(sSUM_WGT);
//			 	 					if (dSTL_CNT <= 3) {
//			 	 						if (dSUM_WGT < YdConstant.YD_COIL_TC_WEIGH_MAX) {
//				 	 						szMsg = "대차 상차 가능 ok";
//				 	 						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//			 	 							recInPara = JDTORecordFactory.getInstance().create();
//			 			    				recInPara.setField("MSG_ID"		, "YDYDJ509");
//			 			    				recInPara.setField("YD_EQP_ID"	, szEqpId);
//			 			    				recInPara.setField("YD_SCH_CD"	, sYD_SCH_CD_TMP);
//			 			    				recInPara.setField("YD_WBOOK_ID", sYD_WBOOK_ID_TMP);
//			 			    				
//			 			    	        	//크레인 스케줄 호출 메세지 전송
//			 			    	    		ydDelegate.sendMsg(recInPara);
//			 			    	    		//다음 크레인스케줄이 생성되는 중이기때문에 현재상태에서는 작업지시를 내려보내지않고 리턴
//			 			    	    		return YdConstant.RETN_CD_SUCCESS;	    			    				
//			 	 						}
//			 	 					} 
//			 	 	  			}
//	 	 	    	    	}	
////대차 하차 작업	 	 	    	    	
//	 	 	    	    } else if ((sYD_SCH_CD_TMP.substring(2,4).equals("TC")) && 
//	 	 	    	    		   (sYD_SCH_CD_TMP.substring(6,8).equals("LM")) && 
//	 	 	    	    		   (!sYD_WRK_PLAN_TCAR_TMP.equals(""))){
//	 	 	    	    	
//	 	 	    	    	inRecordSet = JDTORecordFactory.getInstance().createRecordSet("");
//	 	 	    	    	inRecord1 	= JDTORecordFactory.getInstance().create();
//	 	 	    	    	outRecord2 	= JDTORecordFactory.getInstance().create();
//		 	 	  			
//		 	 	  			//설비ID를 대차으로 설정
//	 	 	    	    	inRecord1.setField("YD_EQP_ID", sYD_WRK_PLAN_TCAR_TMP);
//		 	 	  			
//	 	 	    	    	intRtnVal = ydEqpDao.getYdEqp(inRecord1, inRecordSet, 0);
//	 	 	    	    	if(intRtnVal > 0) {
//		 	 	    	    	inRecordSet.first();
//			 	 	  			outRecord2 = inRecordSet.getRecord();
////설비상태
//			 	 	  			String YD_GP1 			= ydDaoUtils.paraRecChkNull(outRecord2, "YD_GP");
//			 	 	  			String YD_CURR_BAY_GP1	= ydDaoUtils.paraRecChkNull(outRecord2, "YD_CURR_BAY_GP");
//			 	 	  			String YD_EQP_STAT1 	= ydDaoUtils.paraRecChkNull(outRecord2, "YD_EQP_STAT");
//			 	 	  			
//			 	 	  			if((YD_GP1.equals(sYD_SCH_CD_TMP.substring(0,1))) &&             //야드구분 
//			 	 	  			   (YD_CURR_BAY_GP1.equals(sYD_SCH_CD_TMP.substring(1,2))) &&    //현재동
//			 	 	  			   (YD_EQP_STAT1.equals("A"))){                                  //도착
//
//		 	 						szMsg = "하차 작업 가능 ok";
//		 	 						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	 	 							recInPara = JDTORecordFactory.getInstance().create();
//	 			    				recInPara.setField("MSG_ID"		, "YDYDJ509");
//	 			    				recInPara.setField("YD_EQP_ID"	, szEqpId);
//	 			    				recInPara.setField("YD_SCH_CD"	, sYD_SCH_CD_TMP);
//	 			    				recInPara.setField("YD_WBOOK_ID", sYD_WBOOK_ID_TMP);
//	 			    				
//	 			    	        	//크레인 스케줄 호출 메세지 전송
//	 			    	    		ydDelegate.sendMsg(recInPara);
//	 			    	    		//다음 크레인스케줄이 생성되는 중이기때문에 현재상태에서는 작업지시를 내려보내지않고 리턴
//	 			    	    		return YdConstant.RETN_CD_SUCCESS;	    			    				
//			 	 	  			}
//	 	 	    	    	}	
//	 	 	    	    	
//	 	 	    	    } else  {
////		 	     	 	       151216 hun 지포장 보급 추가
//	 	 	    	    	if(intGfCoilChk==0 &&
//	 		 	     	 	   (sYD_SCH_CD_TMP.equals("HBGF01UM")|| sYD_SCH_CD_TMP.equals("HCGF01UM")||sYD_SCH_CD_TMP.equals("HEGF01UM")||sYD_SCH_CD_TMP.equals("HHGF01UM"))
//	 		 	     	 	       ){
////	 	 	    	    	지포장 적치불가능일때 다음스케줄 기동안함...	
//	 	 	    	    	}else{
//			 	        		recInPara = JDTORecordFactory.getInstance().create();
//			    				recInPara.setField("MSG_ID"		, "YDYDJ509");
//			    				recInPara.setField("YD_EQP_ID"	, szEqpId);
//			    				recInPara.setField("YD_SCH_CD"	, sYD_SCH_CD_TMP);
//			    				recInPara.setField("YD_WBOOK_ID", sYD_WBOOK_ID_TMP);
//			    				
//			    	        	//크레인 스케줄 호출 메세지 전송
//			    	    		ydDelegate.sendMsg(recInPara);
//			    	    		//다음 크레인스케줄이 생성되는 중이기때문에 현재상태에서는 작업지시를 내려보내지않고 리턴
//			    	    		return YdConstant.RETN_CD_SUCCESS;
//	 	 	    	    	}
//	 	 	    	    }
//	 	        	}
//	 	        	
//	           		//더이상 크레인 스케줄에 작업이 없을 경우 설비Table에 설비상태를 'W' 명령선택대기(idle)로 변경한다.
//        			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//        	         * 			코일야드 송신 크레인작업실적응답 전송  - YDY3L005
//        	         * 업무기준 Desc :에서 크레인 작업지시 요구 시 크레인 작업이 더 이상 존재하지 않을 경우 크레인작업실적응답 전송
//        	         * 기능 추가 : 임춘수
//        	         * 일자 : 2009.06.19
//        	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//        			recIntTemp = JDTORecordFactory.getInstance().create(); 
//        			recIntTemp.setField("MSG_ID"        , "YDY5L005");
//        			recIntTemp.setField("YD_EQP_ID"     , szEqpId);						//야드설비ID
//        			recIntTemp.setField("YD_L2_WR_GP"   , YdConstant.CRN_WRK_RE_WO_DMD);	//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
//        			recIntTemp.setField("YD_L3_HD_RS_CD", YdConstant.CRN_WRK_RE_CD_NO_WRK);	//야드L3처리결과코드 : 9999 - 크레인작업이 더 이상 없습니다.
//        			ydDelegate.sendMsg(recIntTemp);
//        			szMsg = "[코일 크레인 작업지시 요구]크레인 작업이 더 이상 존재하지 않을 경우코일야드L2 크레인작업실적응답[YDY3L005] 전송 완료" ;
//                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//
//    				szMsg="현재 크레인(" + szEqpId + ")로 작업할 수 있는 작업예약이 없습니다.";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//
//    	    		return YdConstant.RETN_CRN_NO_WRK;	    
//	    			
//	    		}else{
//	    			//다음크레인 작업을 찾았을경우 작업지시상태로 변경
//	        		recIntTemp = JDTORecordFactory.getInstance().create();
//	        		recIntTemp.setField("YD_EQP_ID", szEqpId);
//	        		recIntTemp.setField("YD_EQP_STAT", "1");
//	        		intRtnVal = ydEqpDao.updYdEqp(recIntTemp, 0);
//	    			if(intRtnVal <= 0) {
//		    			if(intRtnVal == 0) {
//		    				szMsg=" Y1ChkWrkProgStatW updYdEqp : data not found";
//		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//		    			}else if(intRtnVal == -1) {
//		    				szMsg=" Y1ChkWrkProgStatW updYdEqp : duplicate data,";
//		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
//		    			}else if(intRtnVal == -2) {
//		    				szMsg=" Y1ChkWrkProgStatW updYdEqp : parameter error";
//		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//		    			}else if(intRtnVal == -3){
//		    				szMsg=" Y1ChkWrkProgStatW updYdEqp : execution failed";
//		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//		    			}
//		    		}
//	    		}
////	    		sjh 추가 'E': 입측 인터락 take out	    		
//           	}else if(szWrkProgStat.equals("E")) {
//           		szMsg="야드 작업 진행 상태가 'E'인 경우 (보급 인터락)";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	       		
//				intRtnVal = this.Y5ChkWrkProgStat(msgRecord, rsCrnSch);
//        		
//	       		//크레인 스케줄이 없다면 없다는 메시지를 L2에 전송해야한다. 현재는 종료처리중..수정할것
//        		if( intRtnVal == 0 ) {
//            		//더이상 크레인 스케줄에 작업이 없을 경우 설비Table에 설비상태를 'W' 명령선택대기(idle)로 변경한다.
//            		recIntTemp = JDTORecordFactory.getInstance().create();
//            		recIntTemp.setField("YD_EQP_ID", szEqpId);
//            		recIntTemp.setField("YD_EQP_STAT", "W");	//'W' 명령선택대기
//            		intRtnVal = ydEqpDao.updYdEqp(recIntTemp, 0);
//        			if(intRtnVal <= 0) {
//    	    			if(intRtnVal == 0) {
//    	    				szMsg=" Y5ChkWrkProgStatW updYdEqp : data not found";
//    	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//    	    			}else if(intRtnVal == -1) {
//    	    				szMsg=" Y5ChkWrkProgStatW updYdEqp : duplicate data,";
//    	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
//    	    			}else if(intRtnVal == -2) {
//    	    				szMsg=" Y5ChkWrkProgStatW updYdEqp : parameter error";
//    	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//    	    			}else if(intRtnVal == -3){
//    	    				szMsg=" Y5ChkWrkProgStatW updYdEqp : execution failed";
//    	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//    	    			}
//    	    		}
//        			
//        			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//        	         * 			코일야드 송신 크레인작업실적응답 전송  - YDY3L005
//        	         * 업무기준 Desc :에서 크레인 작업지시 요구 시 크레인 작업이 더 이상 존재하지 않을 경우 크레인작업실적응답 전송
//        	         * 기능 추가 : 임춘수
//        	         * 일자 : 2009.06.19
//        	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//        			recIntTemp = JDTORecordFactory.getInstance().create(); 
//        			recIntTemp.setField("MSG_ID"        , "YDY5L005");
//        			recIntTemp.setField("YD_EQP_ID"     , szEqpId);						//야드설비ID
//        			recIntTemp.setField("YD_L2_WR_GP"   , YdConstant.CRN_WRK_RE_WO_DMD);	//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
//        			recIntTemp.setField("YD_L3_HD_RS_CD", YdConstant.CRN_WRK_RE_CD_NO_WRK);	//야드L3처리결과코드 : 9999 - 크레인작업이 더 이상 없습니다.
//        			ydDelegate.sendMsg(recIntTemp);
//        			szMsg = "[코일 크레인 작업지시 요구]크레인 작업이 더 이상 존재하지 않을 경우코일야드L2 크레인작업실적응답[YDY3L005] 전송 완료" ;
//                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//        	        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//                    
//    				szMsg="더이상의 크레인스케줄이 조회되지 않습니다.";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//        			
//        			return YdConstant.RETN_CRN_NO_SCH;
//        		}else if(intRtnVal == -1) {
//        			return YdConstant.RETN_CD_FAILURE;
//        		}else{
//        			String sYD_TO_LOC_GUIDE = "";
//        			//현재크레인이 작업중인 스케줄의 값을 다시 편집하여 재전송한다.
//        			rsCrnSch.absolute(1);
//        			recOutTemp = JDTORecordFactory.getInstance().create();
//        			recOutTemp.setRecord(rsCrnSch.getRecord());
//        			
//        			String szYD_CRN_SCH_ID			= ydDaoUtils.paraRecChkNull(msgRecord,  "YD_CRN_SCH_ID");
//        			String sYD_SCH_CD				= ydDaoUtils.paraRecChkNull(msgRecord,  "YD_SCH_CD");
//        			String szYD_WRK_PROG_STAT		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WRK_PROG_STAT");
////C증설        			
//        			if(
//      				   (sYD_SCH_CD.equals("HBKE01UM")) ||  //H동 SPM4보급
//     				   (sYD_SCH_CD.equals("HBKE03UM")) ||  //H동 SPM4입측TakeIn
//     				   (sYD_SCH_CD.equals("HBKE03LM")) ||  //H동 SPM4입축TakeOut
//     				   (sYD_SCH_CD.equals("HBKD03LM")) ||  //H동 SPM4출축TakeOut
//     				   (sYD_SCH_CD.equals("HBKD02LM")) ||  //H동 SPM4재작업보급
//     				  (sYD_SCH_CD.equals("HBKD04LM")) ||  //H동 SPM4재작업추출
//     				   (sYD_SCH_CD.equals("HBKD05LM")) ||
//     				   
//     				   (sYD_SCH_CD.equals("HAKE01UM")) ||  //H동 SPM5보급
//    				   (sYD_SCH_CD.equals("HAKE03UM")) ||  //H동 SPM5입측TakeIn
//    				   (sYD_SCH_CD.equals("HAKE03LM")) ||  //H동 SPM5입축TakeOut
//    				   (sYD_SCH_CD.equals("HAKD03LM")) ||  //H동 SPM5출축TakeOut
//    				   (sYD_SCH_CD.equals("HAKD02LM")) ||  //H동 SPM5재작업보급
//    				   (sYD_SCH_CD.equals("HAKD04LM")) ||  //H동 SPM5재작업추출
//    				   (sYD_SCH_CD.equals("HAKD05LM")) ||
//    				   
//     				   (sYD_SCH_CD.equals("HBFE01UM")) ||  //G동 #5HFL보급
//     				   (sYD_SCH_CD.equals("HBFE03UM")) ||  //G동 #5HFL입측TakeIn
//     				   (sYD_SCH_CD.equals("HBFE03LM")) ||  //G동 #5HFL입측TakeOut
//     				   (sYD_SCH_CD.equals("HBFD02LM")) ||  //G동 #5HFL재작업추출
//
//     				   (sYD_SCH_CD.equals("HCKE01UM")) ||  //H동 SPM3보급
//     				   (sYD_SCH_CD.equals("HCKE03UM")) ||  //H동 SPM3입측TakeIn
//     				   (sYD_SCH_CD.equals("HCKE03LM")) ||  //H동 SPM3입축TakeOut
//     				   (sYD_SCH_CD.equals("HCKD03LM")) ||  //H동 SPM3출축TakeOut
//     				   (sYD_SCH_CD.equals("HCKD02LM")) ||  //H동 SPM3재작업보급
//     				  (sYD_SCH_CD.equals("HCKD04LM")) ||  //H동 SPM3재작업추출
//     				   (sYD_SCH_CD.equals("HCKD05LM")) ||
//
//     				   (sYD_SCH_CD.equals("HCFE01UM")) ||  //G동 #4HFL보급
//     				   (sYD_SCH_CD.equals("HCFE03UM")) ||  //G동 #4HFL입측TakeIn
//     				   (sYD_SCH_CD.equals("HCFE03LM")) ||  //G동 #4HFL입측TakeOut
//     				   (sYD_SCH_CD.equals("HCFD02LM")) ||  //G동 #4HFL재작업추출
//   				   
//        			   (sYD_SCH_CD.equals("HEDE01UM")) ||  //E동 SPM2보급
//       				   (sYD_SCH_CD.equals("HEDE03UM")) ||  //E동 SPM2입측TakeIn
//       				   (sYD_SCH_CD.equals("HEDE03LM")) ||  //E동 SPM2입측TakeOut	
//       				   (sYD_SCH_CD.equals("HEDD03LM")) ||  //E동 SPM2출측TakeOut	
//     				   (sYD_SCH_CD.equals("HEDD02LM")) ||  //E동 SPM2재작업보급
//     				  (sYD_SCH_CD.equals("HEDD04LM")) ||  //E동 SPM2재작업추출
//     				   (sYD_SCH_CD.equals("HEDD05LM")) ||
//     			   
//     				   (sYD_SCH_CD.equals("HGFE01UM")) ||  //G동 #1HFL보급
//     				   (sYD_SCH_CD.equals("HGFE03UM")) ||  //G동 #1HFL입측TakeIn
//     				   (sYD_SCH_CD.equals("HGFE03LM")) ||  //G동 #1HFL입측TakeOut
//     				   (sYD_SCH_CD.equals("HGFD02LM")) ||  //G동 #1HFL재작업추출
//     				   
////     				   151209 hun 지포장추출 4개 추가     		 
//    				   (sYD_SCH_CD.equals("HAKD06LM"))||
//    	 			   (sYD_SCH_CD.equals("HBKD06LM"))||
//    	 			   (sYD_SCH_CD.equals("HBFD06LM"))||
//    	 			   (sYD_SCH_CD.equals("HCKD06LM"))||
//    	 			   (sYD_SCH_CD.equals("HCFD06LM"))||
//    	 			   (sYD_SCH_CD.equals("HDFD06LM"))||
//    	 			   (sYD_SCH_CD.equals("HEDD06LM"))||
//    	 			   (sYD_SCH_CD.equals("HFFD06LM"))||
//    	 			   (sYD_SCH_CD.equals("HGFD06LM"))||
//    	 			   (sYD_SCH_CD.equals("HHKD06LM"))||
//    				   
//     				   (sYD_SCH_CD.equals("HHKD05LM")) ||
//       				   (sYD_SCH_CD.equals("HHKE01UM")) ||  //H동 SPM1보급
//     				   (sYD_SCH_CD.equals("HHKE03UM")) ||  //H동 SPM1입측TakeIn
//     				   (sYD_SCH_CD.equals("HHKE03LM")) ||  //H동 SPM1입축TakeOut
//     				   (sYD_SCH_CD.equals("HHKD03LM")) ||  //H동 SPM1출축TakeOut
//     				   (sYD_SCH_CD.equals("HHKD02LM")) ||     //H동 SPM1재작업보급
//     				    (sYD_SCH_CD.equals("HHKD04LM"))     //H동 SPM1재작업추출
//     				   
//  				       ){ 
//        				if(
//						   (sYD_SCH_CD.equals("HAKE01UM")) ||  
//	    	     		   (sYD_SCH_CD.equals("HAKE03UM")) ||
//	    	     		   (sYD_SCH_CD.equals("HAKD02LM")) ||  //H동 SPM5재작업추출
//            	     		   	   
//         	     		   (sYD_SCH_CD.equals("HBKE01UM")) ||  
//        	     		   (sYD_SCH_CD.equals("HBKE03UM")) ||
//        	     		   (sYD_SCH_CD.equals("HBKD02LM")) ||  //H동 SPM4재작업추출   	     		  
//        	     		   
//       	     		   	   (sYD_SCH_CD.equals("HBFE01UM")) ||
//        	     		   (sYD_SCH_CD.equals("HBFE03UM")) ||
//        	     		   
//        	     		   (sYD_SCH_CD.equals("HCKE01UM")) ||  
//        	     		   (sYD_SCH_CD.equals("HCKE03UM")) || 
//        	     		   (sYD_SCH_CD.equals("HCKD02LM")) ||  //H동 SPM3재작업추출
//        	     		  
//        	        	   (sYD_SCH_CD.equals("HCFE01UM")) ||
//        	     		   (sYD_SCH_CD.equals("HCFE03UM")) || 
//
//        	     		   (sYD_SCH_CD.equals("HEDE01UM")) ||
//        	     		   (sYD_SCH_CD.equals("HEDE03UM")) ||
//        	     		   (sYD_SCH_CD.equals("HEDD02LM")) ||  //E동 SPM2재작업추출
//
//        	     		   (sYD_SCH_CD.equals("HGFE01UM")) ||
//        	     		   (sYD_SCH_CD.equals("HGFE03UM")) ||
//        	     		   
//        	        	   (sYD_SCH_CD.equals("HHKE01UM")) ||  
//        	     		   (sYD_SCH_CD.equals("HHKE03UM")) ||
//        	     		   (sYD_SCH_CD.equals("HHKD02LM"))     //H동 SPM1재작업추출
//           	     		){ 
//        					
//	        				if(!szYD_WRK_PROG_STAT.equals("2")) {
//	          					szMsg="권상완료 상태가 아닙니다.  : " + sYD_SCH_CD;
//	        					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	        					return YdConstant.RETN_CD_FAILURE;
//	            			}
//        				} else {
//	        				if(!szYD_WRK_PROG_STAT.equals("1")) {
//	          					szMsg="선택상태가 아닙니다.  : " + sYD_SCH_CD;
//	        					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	        					return YdConstant.RETN_CD_FAILURE;
//	            			}
//        				}	
//         				
//           			} else {
//						szMsg="스케쥴 코드 이상. sYD_SCH_CD : " + sYD_SCH_CD;
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//    					return YdConstant.RETN_CD_FAILURE;
//           			}	
//        			
//        			String sYD_LOC_TMP				= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_DN_WO_LOC");  //권하위치
//        			sYD_STK_COL_GP					= sYD_LOC_TMP.substring(0,6);
//      				String sYD_TO_LOC_GUIDE_TMP		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TO_LOC_GUIDE");
//      			        			
//        			ydUtils.putLog(szSessionName, szMethodName, "sYD_STK_COL_GP ->" + sYD_STK_COL_GP, YdConstant.DEBUG);
//
//        			//C증설
//        			if(
//               		   (sYD_SCH_CD.equals("HBFE03UM")) ||
//               		   (sYD_SCH_CD.equals("HBKE03UM")) ||
//               		   (sYD_SCH_CD.equals("HAKE03UM")) ||
//              		   (sYD_SCH_CD.equals("HCFE03UM")) ||
//             		   (sYD_SCH_CD.equals("HCKE03UM")) ||
//               		   (sYD_SCH_CD.equals("HEDE03UM")) ||
//               		   (sYD_SCH_CD.equals("HGFE03UM")) ||
//             		   (sYD_SCH_CD.equals("HHKE03UM")) 
//             		   ){
//            			sYD_TO_LOC_GUIDE			= sYD_TO_LOC_GUIDE_TMP.substring(6,8);  ///takein bad
//                 	}else {	
//                		sYD_TO_LOC_GUIDE			= "";
//                    }
//        			
//        			if(
//						(sYD_SCH_CD.equals("HBFE03LM")) ||
//						(sYD_SCH_CD.equals("HCFE03LM")) ||
//						(sYD_SCH_CD.equals("HDFE03LM")) ||
//						(sYD_SCH_CD.equals("HEDE03LM")) ||
//						(sYD_SCH_CD.equals("HFFE03LM")) ||
//						(sYD_SCH_CD.equals("HGFE03LM")) 
//						){
//        				
//        			}else{
//        			         			
//	        			outRecord1 = (JDTORecord)this.Y5ChkWrkProgStatInlock(msgRecord, sYD_STK_COL_GP, sYD_SCH_CD, sYD_TO_LOC_GUIDE);
//	 
//	       				String sRTN_CD1	= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
//	        			if(sRTN_CD1.equals("0")){ 
//	        				szMsg="작업지시 재처리 실패다. intRtnVal : " + intRtnVal;
//	    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	    					return YdConstant.RETN_CD_FAILURE;
//	    		    	}
//        			}
//
////C증설	        			
//        			if(
//         			   (sYD_SCH_CD.equals("HBFE03LM"))|| 
//        			   (sYD_SCH_CD.equals("HBKE03LM"))||
//        			   (sYD_SCH_CD.equals("HAKE03LM"))||
//        			   (sYD_SCH_CD.equals("HCFE03LM"))||
//        			   (sYD_SCH_CD.equals("HCKE03LM"))||
//        			   (sYD_SCH_CD.equals("HEDE03LM"))||
//        			   (sYD_SCH_CD.equals("HGFE03LM"))||
//        			   (sYD_SCH_CD.equals("HHKE03LM"))||
//        			 //TAKE OUT 출축
//     	 			   (sYD_SCH_CD.equals("HAKD03LM"))||
//   	 			       (sYD_SCH_CD.equals("HBKD03LM"))||
//   	 			       (sYD_SCH_CD.equals("HCKD03LM"))||
//   	 			       (sYD_SCH_CD.equals("HEDD03LM"))||
//   	 			       (sYD_SCH_CD.equals("HHKD03LM"))
//   	 			    
//        			   ){ // TAKEOUT존
//	    				
//        				
//        				//조업 트레킹 정보로 FROM위치 지정
//            			outRecord1 	= (JDTORecord)this.Y5ChkWrkProgStatLineOfflock(msgRecord, sYD_STK_COL_GP, sYD_SCH_CD, "", "");
//            			
//            			String sRTN_CD1			= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
//            			szYD_CRN_SCH_ID 		= StringHelper.evl(outRecord1.getFieldString("YD_CRN_SCH_ID"), "");
//            			String sYD_WBOOK_ID  	= StringHelper.evl(outRecord1.getFieldString("YD_WBOOK_ID"), "");
//            			if(sRTN_CD1.equals("0")){ 
//            				szMsg=" LINE OFF2  작업지시 재처리 실패." ;
//        					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//            				return YdConstant.RETN_CD_FAILURE;
//            			} 
//            			
//        				//String sYD_WBOOK_ID		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WBOOK_ID");
//	    				recInPara = JDTORecordFactory.getInstance().create();
//	    				
//	    				recInPara.setField("YD_EQP_ID", 	szEqpId);
//	    				recInPara.setField("YD_SCH_CD", 	sYD_SCH_CD);
//	    				recInPara.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
//	    				recInPara.setField("YD_WBOOK_ID",	sYD_WBOOK_ID);
//	    					
//						ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
//						outRecord1 = (JDTORecord)ejbConn.trx("procY5CrnSchMainRe", new Class[] { JDTORecord.class }, new Object[] { recInPara });
//						String sRTN_CD	= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
//						if ("0".equals(sRTN_CD)) {
//							return YdConstant.RETN_CD_TC_ERROR;	
//						}	
//						return YdConstant.RETN_CD_SUCCESS;	
//        			}else {	
//        				
//        				
//        				recInPara = JDTORecordFactory.getInstance().create();	        	
//	                	recInPara.setField("YD_CRN_SCH_ID"		, szYD_CRN_SCH_ID);	                
//	                	recInPara.setField("MODIFIER"			, "YDSYSTEM");	                	
//	                	recInPara.setField("YD_SCH_ST_GP",      "I");	                
//
//	            		//크레인스케줄의 작업진행 상태를 권상지시로 변경
//	            		intRtnVal = YdCrnSchDao.updYdCrnsch(recInPara, 0);
//	        			if(intRtnVal <= 0) {
//	            			if(intRtnVal == 0) {
//	            				szMsg="procY5CrnWrkOrdReq updYdCrnsch : data not found";
//	            				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//	            				return YdConstant.RETN_CD_NOTEXIST;
//	            			}else if(intRtnVal == -2) {
//	            				szMsg="procY5CrnWrkOrdReq updYdCrnsch : parameter error";
//	            				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	            				return YdConstant.RETN_CD_NO_PARAM;
//	            			}else{
//	            				szMsg="procY5CrnWrkOrdReq updYdCrnsch : execution failed";
//	            				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	            				return YdConstant.RETN_CD_FAILURE;
//	            			}
//	        			}
//	        			
//        				
//        				recInPara = JDTORecordFactory.getInstance().create();
//	            		//작업지시 전문 전송 data setup
//	        			recInPara.setField("MSG_ID", 			"YDY5L004");
//	                	recInPara.setField("YD_CRN_SCH_ID",    	szYD_CRN_SCH_ID);
//	                	recInPara.setField("YD_WRK_PROG_STAT", 	szYD_WRK_PROG_STAT);
//	                	recInPara.setField("YD_SCH_CD",        	ydDaoUtils.paraRecChkNull(recOutTemp, "YD_SCH_CD"));
//	                	recInPara.setField("YD_GP",            	ydDaoUtils.paraRecChkNull(recOutTemp, "YD_GP"));
//	                	recInPara.setField("MODIFIER", 			"YDSYSTEM");
//	                	
//	                	recInPara.setField("MSG_GP", 			"U");
//	                	ydDelegate.sendMsg(recInPara);
//	                	szMsg = "["+szOperationName+"] 현재 작업중["+szYD_WRK_PROG_STAT+"]인 스케줄["+szYD_CRN_SCH_ID+"]을 재전송";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	        			
//	                	return YdConstant.RETN_CD_SUCCESS;
//        			}
//				}
//
////	    		sjh 추가 'D': 출측 인터락   		
//           	} else if(szWrkProgStat.equals("D")) {
//           		szMsg="야드 작업 진행 상태가 'D'인 경우 (출측 인터락)";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	       		 
//				intRtnVal = this.Y5ChkWrkProgStat(msgRecord, rsCrnSch);
//        		
//	       		if( intRtnVal == 0 ) {
//            		//더이상 크레인 스케줄에 작업이 없을 경우 설비Table에 설비상태를 'W' 명령선택대기(idle)로 변경한다.
//            		recIntTemp = JDTORecordFactory.getInstance().create();
//            		recIntTemp.setField("YD_EQP_ID", szEqpId);
//            		recIntTemp.setField("YD_EQP_STAT", "W");	//'W' 명령선택대기
//            		intRtnVal = ydEqpDao.updYdEqp(recIntTemp, 0);
//        			if(intRtnVal <= 0) {
//    	    			if(intRtnVal == 0) {
//    	    				szMsg=" Y5ChkWrkProgStatW updYdEqp : data not found";
//    	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//    	    			}else if(intRtnVal == -1) {
//    	    				szMsg=" Y5ChkWrkProgStatW updYdEqp : duplicate data,";
//    	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
//    	    			}else if(intRtnVal == -2) {
//    	    				szMsg=" Y5ChkWrkProgStatW updYdEqp : parameter error";
//    	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//    	    			}else if(intRtnVal == -3){
//    	    				szMsg=" Y5ChkWrkProgStatW updYdEqp : execution failed";
//    	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//    	    			}
//    	    		}
//        			
//        			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//        	         * 			코일야드 송신 크레인작업실적응답 전송  - YDY3L005
//        	         * 업무기준 Desc :에서 크레인 작업지시 요구 시 크레인 작업이 더 이상 존재하지 않을 경우 크레인작업실적응답 전송
//        	         * 기능 추가 : 임춘수
//        	         * 일자 : 2009.06.19
//        	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//        			recIntTemp = JDTORecordFactory.getInstance().create(); 
//        			recIntTemp.setField("MSG_ID"        , "YDY5L005");
//        			recIntTemp.setField("YD_EQP_ID"     , szEqpId);						//야드설비ID
//        			recIntTemp.setField("YD_L2_WR_GP"   , YdConstant.CRN_WRK_RE_WO_DMD);	//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
//        			recIntTemp.setField("YD_L3_HD_RS_CD", YdConstant.CRN_WRK_RE_CD_NO_WRK);	//야드L3처리결과코드 : 9999 - 크레인작업이 더 이상 없습니다.
//        			ydDelegate.sendMsg(recIntTemp);
//        			szMsg = "[코일 크레인 작업지시 요구]크레인 작업이 더 이상 존재하지 않을 경우코일야드L2 크레인작업실적응답[YDY3L005] 전송 완료" ;
//                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//        	        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//                    
//    				szMsg="더이상의 크레인스케줄이 조회되지 않습니다.";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//        			
//        			return YdConstant.RETN_CRN_NO_SCH;
//        		}else if(intRtnVal == -1) {
//        			return YdConstant.RETN_CD_FAILURE;
//        		}else{
//        			
//        			
//        			rsCrnSch.absolute(1);
//        			recOutTemp = JDTORecordFactory.getInstance().create();
//        			recOutTemp.setRecord(rsCrnSch.getRecord());
//        			
//        			String szYD_CRN_SCH_ID_OLD		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CRN_SCH_ID");
//        			String szYD_WRK_PROG_STAT		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WRK_PROG_STAT");
//        			String sYD_SCH_CD				= ydDaoUtils.paraRecChkNull(msgRecord,  "YD_SCH_CD");
////C증설 
//        			if( 
//        				(sYD_SCH_CD.equals("HBFD01LM")) || (sYD_SCH_CD.equals("HBFD02LM")) || (sYD_SCH_CD.equals("HBFD04LM")) ||
//        				(sYD_SCH_CD.equals("HBKD01LM")) || (sYD_SCH_CD.equals("HBKD01UM")) || (sYD_SCH_CD.equals("HBKD02LM")) ||(sYD_SCH_CD.equals("HBKD04LM")) ||
//        				(sYD_SCH_CD.equals("HAKD01LM")) || (sYD_SCH_CD.equals("HAKD01UM")) || (sYD_SCH_CD.equals("HAKD02LM")) ||(sYD_SCH_CD.equals("HAKD04LM")) ||
//               			//(sYD_SCH_CD.equals("JBFD01LM")) || 
//               			(sYD_SCH_CD.equals("JBKD01LM")) || (sYD_SCH_CD.equals("HBKD05LM")) ||  
//               			(sYD_SCH_CD.equals("JAKD01LM")) || (sYD_SCH_CD.equals("HAKD05LM")) ||
//        				//(sYD_SCH_CD.equals("JBTC01MM")) || (sYD_SCH_CD.equals("JBTC02MM")) || 
//        				(sYD_SCH_CD.equals("JBTC05MM")) ||  
//        				(sYD_SCH_CD.equals("JATC05MM")) ||
//        				
//        				(sYD_SCH_CD.equals("HCFD01LM")) || (sYD_SCH_CD.equals("HCFD02LM")) || (sYD_SCH_CD.equals("HCFD04LM")) ||  
//        				(sYD_SCH_CD.equals("HCKD01LM")) || (sYD_SCH_CD.equals("HCKD01UM")) || (sYD_SCH_CD.equals("HCKD02LM")) || (sYD_SCH_CD.equals("HCKD04LM")) ||   
//
//               			(sYD_SCH_CD.equals("JCFD01LM")) || (sYD_SCH_CD.equals("JCKD01LM")) || (sYD_SCH_CD.equals("HCKD05LM")) || 
//        			    (sYD_SCH_CD.equals("JCTC01MM")) || (sYD_SCH_CD.equals("JCTC02MM")) || (sYD_SCH_CD.equals("JCTC05MM")) ||  
//        				
//        				(sYD_SCH_CD.equals("HEDD01LM")) || (sYD_SCH_CD.equals("HEDD01UM")) || (sYD_SCH_CD.equals("HEDD05LM")) || (sYD_SCH_CD.equals("HEDD04LM")) || 
//               			(sYD_SCH_CD.equals("HEDD02LM")) || (sYD_SCH_CD.equals("JEDD01LM")) ||	
//        				(sYD_SCH_CD.equals("JETC01MM")) || (sYD_SCH_CD.equals("JETC02MM")) || (sYD_SCH_CD.equals("HHKD05LM")) || (sYD_SCH_CD.equals("HHKD04LM")) || 
////						151209 hun 지포장추출 4개추가
//        				(sYD_SCH_CD.equals("HAKD06LM"))||
//     	 			    (sYD_SCH_CD.equals("HBKD06LM"))||
//     	 			    (sYD_SCH_CD.equals("HBFD06LM"))||
//     	 			    (sYD_SCH_CD.equals("HCKD06LM"))||
//     	 			    (sYD_SCH_CD.equals("HCFD06LM"))||
//     	 			    (sYD_SCH_CD.equals("HDFD06LM"))||
//     	 			    (sYD_SCH_CD.equals("HEDD06LM"))||
//     	 			    (sYD_SCH_CD.equals("HFFD06LM"))||
//     	 			    (sYD_SCH_CD.equals("HGFD06LM"))||
//     	 			    (sYD_SCH_CD.equals("HHKD06LM"))||
//     	 			    //TAKE OUT 출축
//     	 			   (sYD_SCH_CD.equals("HAKD03LM"))||
//   	 			       (sYD_SCH_CD.equals("HBKD03LM"))||
//   	 			       (sYD_SCH_CD.equals("HCKD03LM"))||
//   	 			       (sYD_SCH_CD.equals("HEDD03LM"))||
//   	 			       (sYD_SCH_CD.equals("HHKD03LM"))||
//   	 			    
//        			    (sYD_SCH_CD.equals("HGFD01LM")) || (sYD_SCH_CD.equals("HGFD02LM")) || (sYD_SCH_CD.equals("HGFD04LM")) ||
//        				(sYD_SCH_CD.equals("JGFD01LM")) || (sYD_SCH_CD.equals("JGTC01MM")) || (sYD_SCH_CD.equals("JGTC02MM")) ||  
//
//        				(sYD_SCH_CD.equals("HHKD01LM")) || (sYD_SCH_CD.equals("HHKD01UM")) || (sYD_SCH_CD.equals("HHKD02LM")) ||  
//        				(sYD_SCH_CD.equals("JHKD01LM")) || (sYD_SCH_CD.equals("JHTC01MM")) || (sYD_SCH_CD.equals("JHTC02MM")) 
//        			
//        			){  
//
//        				if((sYD_SCH_CD.equals("HBKD01UM")) || 
//        				   (sYD_SCH_CD.equals("HAKD01UM")) ||
//        				   (sYD_SCH_CD.equals("HCKD01UM")) ||
//        				   (sYD_SCH_CD.equals("HEDD01UM")) ||
//        				   (sYD_SCH_CD.equals("HHKD01UM")) ) {  
//        					if(!szYD_WRK_PROG_STAT.equals("2")) {
//	          					szMsg="권상상태가 아닙니다.  : " + sYD_SCH_CD;
//	        					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	        					return YdConstant.RETN_CD_FAILURE;
//	            			}
//        				} else {	 
//	        				if(!szYD_WRK_PROG_STAT.equals("1")) {
//	          					szMsg="선택상태가 아닙니다.  : " + sYD_SCH_CD;
//	        					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	        					return YdConstant.RETN_CD_FAILURE;
//	            			}
//        				}
//        			} else {
//      					szMsg="스케쥴 코드 이상. sYD_SCH_CD : " + sYD_SCH_CD;
//    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//    					return YdConstant.RETN_CD_FAILURE;
//        			}		
//
//        			String sYD_WBOOK_ID				= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WBOOK_ID");
//        			 
//        			sYD_STK_COL_GP					= sYD_SCH_CD.substring(0,6);
//        			
//         			ydUtils.putLog(szSessionName, szMethodName, "sYD_STK_COL_GP ->" + sYD_STK_COL_GP, YdConstant.DEBUG);
////C증설        			
//         			if(	(sYD_SCH_CD.equals("HAKD01UM"))||
//         				(sYD_SCH_CD.equals("HBKD01UM"))||
//              			(sYD_SCH_CD.equals("HCKD01UM"))||
//         			    (sYD_SCH_CD.equals("HEDD01UM"))|| 
//         				(sYD_SCH_CD.equals("HHKD01UM"))){   //HFL 보급존
//         				
//         				
//         				if("HAKD01UM".equals(sYD_SCH_CD)){
//            				sYD_STK_COL_GP					= "HAKD05";
//            			}else if("HBKD01UM".equals(sYD_SCH_CD)){
//            				sYD_STK_COL_GP					= "HBKD04";
//            			}else if("HCKD01UM".equals(sYD_SCH_CD)){
//            				sYD_STK_COL_GP					= "HCKD03";
//            			}else if("HEDD01UM".equals(sYD_SCH_CD)){
//            				sYD_STK_COL_GP					= "HEDD01";
//            			}else if("HHKD01UM".equals(sYD_SCH_CD)){
//            				sYD_STK_COL_GP					= "HHKD01";
//            			}
//            			
//            				
//	////////////////////////        			
//        				outRecord1 	=  (JDTORecord)this.Y5ChkWrkProgStatOutlock(msgRecord, sYD_STK_COL_GP, sYD_SCH_CD);
//        				String sRTN_CD1	= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
//	        			////////////////////////
//	    				
//	        			if(sRTN_CD1.equals("0")){ 
//	        				szMsg=" LINE OFF  작업지시 재처리 실패다." ;
//	    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	        				return YdConstant.RETN_CD_FAILURE;
//	        			} 
//	        			
//	                	recInPara = JDTORecordFactory.getInstance().create();
//	            		//작업지시 전문 전송 data setup
//	        			recInPara.setField("MSG_ID", 			"YDY5L004");
//	                	recInPara.setField("YD_CRN_SCH_ID",    	szYD_CRN_SCH_ID_OLD);
//	                	recInPara.setField("YD_WRK_PROG_STAT", 	szYD_WRK_PROG_STAT);
//	                	recInPara.setField("YD_SCH_CD",        	ydDaoUtils.paraRecChkNull(recOutTemp, "YD_SCH_CD"));
//	                	recInPara.setField("YD_GP",            	ydDaoUtils.paraRecChkNull(recOutTemp, "YD_GP"));
//	                	recInPara.setField("MODIFIER", 			"YDSYSTEM");
//	                	
//	                	recInPara.setField("MSG_GP", 			"U");
//	                	ydDelegate.sendMsg(recInPara);
//	                	szMsg = "["+szOperationName+"] 현재 작업중["+szYD_WRK_PROG_STAT+"]인 스케줄["+szYD_CRN_SCH_ID_OLD+"]을 전송";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	        			
//	                	return YdConstant.RETN_CD_SUCCESS;
//	                	
//	        		} else {
//	        			
//	        			//인터락 추출 중복 발생 방지 기능 /////////////////////////////////////////////////
//	        			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
//	        			recInPara = JDTORecordFactory.getInstance().create(); 
//	                	recInPara.setField("YD_CRN_SCH_ID"		, szYD_CRN_SCH_ID_OLD);
//	        			
//	                	intRtnVal = YdCrnSchDao.getYdCrnsch(recInPara, outRecSet, 0);
//	        			
//	                	if(intRtnVal>0){
//	                		outRecSet.absolute(1);
//		        			recOutTemp = JDTORecordFactory.getInstance().create();
//		        			recOutTemp.setRecord(outRecSet.getRecord());
//		        			
//		        			String szYD_UP_WO_LOC		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_UP_WO_LOC");
//	        			
//		        			//HEDD0111 2016.10.06 소재크레인 제외
//		        			if(!"00".equals(szYD_UP_WO_LOC.substring(6,8)) && "J".equals(sYD_SCH_CD.substring(0 , 1))){
//		        				szMsg = "["+szOperationName+"]  인터락 이미 발생 대상 입니다. 스킵";
//								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//								
//								return YdConstant.RETN_CD_SUCCESS;
//		        			}
//		        			
//	                	}
//	        			///////////////////////////////////////////////////////////////////////////
//	        			
//	        			
//	        			
//                        // 기존 sche' 상태 'w'로 변경처리
//	        			recInPara = JDTORecordFactory.getInstance().create();
//	        			recInPara.setField("MSG_ID"				, "YDY5L004");
//	                	recInPara.setField("YD_CRN_SCH_ID"		, szYD_CRN_SCH_ID_OLD);
//	                	//recInPara.setField("YD_WORD_DT"			, "");
//	                	recInPara.setField("MODIFIER"			, "YDSYSTEM");
//	                	recInPara.setField("MSG_GP"				, "U");
//	            		
//	            		//크레인스케줄의 작업진행 상태를 권상지시로 변경
//	            		intRtnVal = YdCrnSchDao.updYdCrnsch(recInPara, 0);
//	        			if(intRtnVal <= 0) {
//	            			if(intRtnVal == 0) {
//	            				szMsg="procY5CrnWrkOrdReq updYdCrnsch : data not found";
//	            				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//	            				return YdConstant.RETN_CD_NOTEXIST;
//	            			}else if(intRtnVal == -2) {
//	            				szMsg="procY5CrnWrkOrdReq updYdCrnsch : parameter error";
//	            				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	            				return YdConstant.RETN_CD_NO_PARAM;
//	            			}else{
//	            				szMsg="procY5CrnWrkOrdReq updYdCrnsch : execution failed";
//	            				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	            				return YdConstant.RETN_CD_FAILURE;
//	            			}
//	        			}
//	        			
//	        			//조업 트레킹 정보로 FROM위치 지정
//	        			outRecord1 	= (JDTORecord)this.Y5ChkWrkProgStatLineOfflock(msgRecord, sYD_STK_COL_GP, sYD_SCH_CD, sYD_WBOOK_ID, szYD_CRN_SCH_ID_OLD);
//	        			
//	        			String sRTN_CD1			= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
//	        			String szYD_CRN_SCH_ID 	= StringHelper.evl(outRecord1.getFieldString("YD_CRN_SCH_ID"), "");
//	        			sYD_WBOOK_ID  			= StringHelper.evl(outRecord1.getFieldString("YD_WBOOK_ID"), "");
//	        			if(sRTN_CD1.equals("0")){ 
//	        				szMsg=" LINE OFF  작업지시 재처리 실패다." ;
//	    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	        				return YdConstant.RETN_CD_FAILURE;
//	        			} 
//	    			
//	        			
//	        			if(szYD_CRN_SCH_ID_OLD.equals(szYD_CRN_SCH_ID)) {
//	        				
//	        			} else {
////대상스케쥴이 바뀌면 이전 작업지시를 ('W') 변경	  
//	        				szMsg=" 대상스케쥴 변경처리됨 기존스케쥴 clear." ;
//	    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	        				
//		        			recInPara = JDTORecordFactory.getInstance().create();
//		        			recInPara.setField("MSG_ID"				, "YDY5L004");
//		                	recInPara.setField("YD_CRN_SCH_ID"		, szYD_CRN_SCH_ID_OLD);
//		                	recInPara.setField("YD_WRK_PROG_STAT"	, "W");
//		                	recInPara.setField("YD_WORD_DT"			, "");
//		                	recInPara.setField("MODIFIER"			, "YDSYSTEM");
//		                	recInPara.setField("MSG_GP"				, "U");
//		            		
//		            		//크레인스케줄의 작업진행 상태를 권상지시로 변경
//		            		intRtnVal = YdCrnSchDao.updYdCrnsch(recInPara, 0);
//		        			if(intRtnVal <= 0) {
//		            			if(intRtnVal == 0) {
//		            				szMsg="procY5CrnWrkOrdReq updYdCrnsch : data not found";
//		            				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//		            				return YdConstant.RETN_CD_NOTEXIST;
//		            			}else if(intRtnVal == -2) {
//		            				szMsg="procY5CrnWrkOrdReq updYdCrnsch : parameter error";
//		            				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//		            				return YdConstant.RETN_CD_NO_PARAM;
//		            			}else{
//		            				szMsg="procY5CrnWrkOrdReq updYdCrnsch : execution failed";
//		            				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//		            				return YdConstant.RETN_CD_FAILURE;
//		            			}
//		        			}
//	        				
//	        			}
//	        			
//	                	recInPara = JDTORecordFactory.getInstance().create();
//	        			recInPara.setField("MSG_ID"				, "YDY5L004");
//	                	recInPara.setField("YD_CRN_SCH_ID"		, szYD_CRN_SCH_ID);
//	                	recInPara.setField("YD_WRK_PROG_STAT"	, "1");
//	                	recInPara.setField("MODIFIER"			, "YDSYSTEM");
//	                	recInPara.setField("YD_DN_WO_LOC"		, "");
//	                	recInPara.setField("YD_DN_WO_LAYER"		, "");
//	                	recInPara.setField("MSG_GP"				, "U");
//	                	recInPara.setField("YD_SCH_ST_GP",      "I");
//	                
//
//	            		//크레인스케줄의 작업진행 상태를 권상지시로 변경
//	            		intRtnVal = YdCrnSchDao.updYdCrnsch(recInPara, 0);
//	        			if(intRtnVal <= 0) {
//	            			if(intRtnVal == 0) {
//	            				szMsg="procY5CrnWrkOrdReq updYdCrnsch : data not found";
//	            				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//	            				return YdConstant.RETN_CD_NOTEXIST;
//	            			}else if(intRtnVal == -2) {
//	            				szMsg="procY5CrnWrkOrdReq updYdCrnsch : parameter error";
//	            				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	            				return YdConstant.RETN_CD_NO_PARAM;
//	            			}else{
//	            				szMsg="procY5CrnWrkOrdReq updYdCrnsch : execution failed";
//	            				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	            				return YdConstant.RETN_CD_FAILURE;
//	            			}
//	        			}
//			    				
//	    				szMsg="현재 크레인(" + szEqpId + ")로 작업할 수 있는 작업예약을 조회하였습니다.  크레인스케줄 재Main호출";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//
//	    				recInPara = JDTORecordFactory.getInstance().create();
//	    				
//	    				recInPara.setField("YD_EQP_ID", 	szEqpId);
//	    				recInPara.setField("YD_SCH_CD", 	sYD_SCH_CD);
//	    				recInPara.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
//	    				recInPara.setField("YD_WBOOK_ID",	sYD_WBOOK_ID);
//	    					
//						ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
//						outRecord1 = (JDTORecord)ejbConn.trx("procY5CrnSchMainRe", new Class[] { JDTORecord.class }, new Object[] { recInPara });
//						String sRTN_CD	= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
//						if ("0".equals(sRTN_CD)) {
//							return YdConstant.RETN_CD_TC_ERROR;	
//						}	
////다음 크레인스케줄이 생성되는 중이기때문에 현재상태에서는 작업지시를 내려보내지않고 리턴
//	    	    		return YdConstant.RETN_CD_SUCCESS;	    			    				
//	        		}
//        		}
////	    		sjh 추가 'A': CONV 인터락   		
//           	} else if(szWrkProgStat.equals("A")) {
//           		szMsg="야드 작업 진행 상태가 'A'인 경우 (CONV 인터락)";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	       		 
//				intRtnVal = this.Y5ChkWrkProgStat(msgRecord, rsCrnSch);
//        		
//	       		if( intRtnVal == 0 ) {
//            		//더이상 크레인 스케줄에 작업이 없을 경우 설비Table에 설비상태를 'W' 명령선택대기(idle)로 변경한다.
//            		recIntTemp = JDTORecordFactory.getInstance().create();
//            		recIntTemp.setField("YD_EQP_ID", szEqpId);
//            		recIntTemp.setField("YD_EQP_STAT", "W");	//'W' 명령선택대기
//            		intRtnVal = ydEqpDao.updYdEqp(recIntTemp, 0);
//        			if(intRtnVal <= 0) {
//    	    			if(intRtnVal == 0) {
//    	    				szMsg=" Y5ChkWrkProgStatW updYdEqp : data not found";
//    	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//    	    			}else if(intRtnVal == -1) {
//    	    				szMsg=" Y5ChkWrkProgStatW updYdEqp : duplicate data,";
//    	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
//    	    			}else if(intRtnVal == -2) {
//    	    				szMsg=" Y5ChkWrkProgStatW updYdEqp : parameter error";
//    	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//    	    			}else if(intRtnVal == -3){
//    	    				szMsg=" Y5ChkWrkProgStatW updYdEqp : execution failed";
//    	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//    	    			}
//    	    		}
//        			
//        			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//        	         * 			코일야드 송신 크레인작업실적응답 전송  - YDY3L005
//        	         * 업무기준 Desc :에서 크레인 작업지시 요구 시 크레인 작업이 더 이상 존재하지 않을 경우 크레인작업실적응답 전송
//        	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//        			recIntTemp = JDTORecordFactory.getInstance().create(); 
//        			recIntTemp.setField("MSG_ID"        , "YDY5L005");
//        			recIntTemp.setField("YD_EQP_ID"     , szEqpId);						//야드설비ID
//        			recIntTemp.setField("YD_L2_WR_GP"   , YdConstant.CRN_WRK_RE_WO_DMD);	//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
//        			recIntTemp.setField("YD_L3_HD_RS_CD", YdConstant.CRN_WRK_RE_CD_NO_WRK);	//야드L3처리결과코드 : 9999 - 크레인작업이 더 이상 없습니다.
//        			ydDelegate.sendMsg(recIntTemp);
//        			szMsg = "[코일 크레인 작업지시 요구]크레인 작업이 더 이상 존재하지 않을 경우코일야드L2 크레인작업실적응답[YDY3L005] 전송 완료" ;
//                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//        	        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//                    
//    				szMsg="더이상의 크레인스케줄이 조회되지 않습니다.";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//        			
//        			return YdConstant.RETN_CRN_NO_SCH;
//        		}else if(intRtnVal == -1) {
//        			return YdConstant.RETN_CD_FAILURE;
//        		}else{
//        		
//        			rsCrnSch.absolute(1);
//        			recOutTemp = JDTORecordFactory.getInstance().create();
//        			recOutTemp.setRecord(rsCrnSch.getRecord());
//        			
//        			String szYD_CRN_SCH_ID_OLD		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CRN_SCH_ID");
//        			String szYD_WRK_PROG_STAT		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WRK_PROG_STAT");
//        			String sYD_SCH_CD				= ydDaoUtils.paraRecChkNull(msgRecord,  "YD_SCH_CD");
// 
//        			if( (sYD_SCH_CD.substring(2, 4).equals("CV")) ){   //CONV 인 경우
//
//        				if(!szYD_WRK_PROG_STAT.equals("1")) {
//          					szMsg="선택상태가 아닙니다.  : " + sYD_SCH_CD;
//        					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//        					return YdConstant.RETN_CD_FAILURE;
//            			}
//       				
//        			} else {
//      					szMsg="스케쥴 코드 이상. sYD_SCH_CD : " + sYD_SCH_CD;
//    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//    					return YdConstant.RETN_CD_FAILURE;
//        			}		
//
//        			String sYD_WBOOK_ID				= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WBOOK_ID");
//        			sYD_STK_COL_GP					= sYD_SCH_CD.substring(0,6);
//        			
//         			ydUtils.putLog(szSessionName, szMethodName, "sYD_STK_COL_GP ->" + sYD_STK_COL_GP, YdConstant.DEBUG);
// 
//        			
//        			outRecord1 	= (JDTORecord)this.Y5ChkWrkProgStatConvlock(msgRecord, sYD_STK_COL_GP, sYD_SCH_CD, sYD_WBOOK_ID, szYD_CRN_SCH_ID_OLD);
//        			
//        			String sRTN_CD1			= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
//        			String szYD_CRN_SCH_ID 	= StringHelper.evl(outRecord1.getFieldString("YD_CRN_SCH_ID"), "");
//        			sYD_WBOOK_ID  			= StringHelper.evl(outRecord1.getFieldString("YD_WBOOK_ID"), "");
//        			if(sRTN_CD1.equals("0")){ 
//        				szMsg=" CONV  작업지시 재처리 실패다." ;
//    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//        				return YdConstant.RETN_CD_FAILURE;
//        			} 
//    			
//        			
//        			if(szYD_CRN_SCH_ID_OLD.equals(szYD_CRN_SCH_ID)) {
//        				
//        			} else {
////대상스케쥴이 바뀌면 이전 작업지시를 ('W') 변경	  
//        				szMsg=" 대상스케쥴 변경처리됨 기존스케쥴 clear." ;
//    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//        				
//	        			recInPara = JDTORecordFactory.getInstance().create();
//	        			recInPara.setField("MSG_ID"				, "YDY5L004");
//	                	recInPara.setField("YD_CRN_SCH_ID"		, szYD_CRN_SCH_ID_OLD);
//	                	recInPara.setField("YD_WRK_PROG_STAT"	, "W");
//	                	recInPara.setField("YD_WORD_DT"			, "");
//	                	recInPara.setField("MODIFIER"			, "YDSYSTEM");
//	                	recInPara.setField("MSG_GP"				, "U");
//	            		
//	            		//크레인스케줄의 작업진행 상태를 권상지시로 변경
//	            		intRtnVal = YdCrnSchDao.updYdCrnsch(recInPara, 0);
//	        			if(intRtnVal <= 0) {
//	            			if(intRtnVal == 0) {
//	            				szMsg="procY5CrnWrkOrdReq updYdCrnsch : data not found";
//	            				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//	            				return YdConstant.RETN_CD_NOTEXIST;
//	            			}else if(intRtnVal == -2) {
//	            				szMsg="procY5CrnWrkOrdReq updYdCrnsch : parameter error";
//	            				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	            				return YdConstant.RETN_CD_NO_PARAM;
//	            			}else{
//	            				szMsg="procY5CrnWrkOrdReq updYdCrnsch : execution failed";
//	            				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	            				return YdConstant.RETN_CD_FAILURE;
//	            			}
//	        			}
//        				
//        			}
//        			
//        			
//                	recInPara = JDTORecordFactory.getInstance().create();
//        			recInPara.setField("MSG_ID"				, "YDY5L004");
//                	recInPara.setField("YD_CRN_SCH_ID"		, szYD_CRN_SCH_ID);
//                	recInPara.setField("YD_WRK_PROG_STAT"	, "1");
//                	recInPara.setField("MODIFIER"			, "YDSYSTEM");
//                	recInPara.setField("YD_DN_WO_LOC"		, "");
//                	recInPara.setField("YD_DN_WO_LAYER"		, "");
//                	recInPara.setField("MSG_GP"				, "U");
//                	recInPara.setField("YD_SCH_ST_GP",      "I");
//                
//
//            		//크레인스케줄의 작업진행 상태를 권상지시로 변경
//            		intRtnVal = YdCrnSchDao.updYdCrnsch(recInPara, 0);
//        			if(intRtnVal <= 0) {
//            			if(intRtnVal == 0) {
//            				szMsg="procY5CrnWrkOrdReq updYdCrnsch : data not found";
//            				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//            				return YdConstant.RETN_CD_NOTEXIST;
//            			}else if(intRtnVal == -2) {
//            				szMsg="procY5CrnWrkOrdReq updYdCrnsch : parameter error";
//            				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//            				return YdConstant.RETN_CD_NO_PARAM;
//            			}else{
//            				szMsg="procY5CrnWrkOrdReq updYdCrnsch : execution failed";
//            				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//            				return YdConstant.RETN_CD_FAILURE;
//            			}
//        			}
//		    				
//    				szMsg="현재 크레인(" + szEqpId + ")로 작업할 수 있는 작업예약을 조회하였습니다.  크레인스케줄 재Main호출";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//
//    				recInPara = JDTORecordFactory.getInstance().create();
//    				
//    				recInPara.setField("YD_EQP_ID", 	szEqpId);
//    				recInPara.setField("YD_SCH_CD", 	sYD_SCH_CD);
//    				recInPara.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
//    				recInPara.setField("YD_WBOOK_ID",	sYD_WBOOK_ID);
//    					
//					ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
//					outRecord1 = (JDTORecord)ejbConn.trx("procY5CrnSchMainRe", new Class[] { JDTORecord.class }, new Object[] { recInPara });
//					String sRTN_CD	= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
//					if ("0".equals(sRTN_CD)) {
//						return YdConstant.RETN_CD_TC_ERROR;	
//					}	
////다음 크레인스케줄이 생성되는 중이기때문에 현재상태에서는 작업지시를 내려보내지않고 리턴
//    	    		return YdConstant.RETN_CD_SUCCESS;	    			    				
//        		}	       		
//        	}
//           	// 150710 hun 크레인 무인화 상태 추가
//           	else if(szWrkProgStat.equals("5") && ydEqpDao.chkAutoCrn(szEqpId) ){
//				szMsg="야드 작업 진행 상태가 '5'인경우"; // 무인상태
//				ydUtils.putLog(szSessionName, szMethodName, szMsg+"ydEqpDao.chkAutoCrn(szEqpId)="+ydEqpDao.chkAutoCrn(szEqpId), YdConstant.DEBUG);
//	 	    	
//				String szCRN_SCH_ID     = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");
//				
//				rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
//				recInPara = JDTORecordFactory.getInstance().create();
//				recInPara.setField("YD_EQP_ID", szEqpId);
//				recInPara.setField("YD_CRN_SCH_ID", szCRN_SCH_ID);
//				sQueryId = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnSchEqpIdWrkProgStat5";
//				intRtnVal = ydCommDao.select(recInPara, rsResult, sQueryId);
//				
//				rsResult.absolute(1);
//				recOutTemp = JDTORecordFactory.getInstance().create();
//				recOutTemp.setRecord(rsResult.getRecord());
//				
//				
//	        	szMsg="야드 작업 진행 상태가 '5'인경우"; // 무인상태
//				ydUtils.putLog(szSessionName, szMethodName, szMsg+"recOutTemp.YD_SCH_CD="+ydDaoUtils.paraRecChkNull(recOutTemp, "YD_SCH_CD"), YdConstant.DEBUG);
//	 	    	
//			
//				//크레인 스케줄의 값을 다시 재전송한다. 
//	        	
//				String szYD_CRN_SCH_ID			= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CRN_SCH_ID");
//				String szYD_WRK_PROG_STAT		= szWrkProgStat;
//				recInPara = JDTORecordFactory.getInstance().create();
//	    		//작업지시 전문 전송 data setup
//				recInPara.setField("MSG_ID", 			"YDY5L004");
//	        	recInPara.setField("YD_CRN_SCH_ID",    	szYD_CRN_SCH_ID);
//	        	recInPara.setField("YD_WRK_PROG_STAT", 	"5");
//	        	recInPara.setField("YD_SCH_CD",        	ydDaoUtils.paraRecChkNull(recOutTemp, "YD_SCH_CD"));
//	        	recInPara.setField("YD_GP",            	ydDaoUtils.paraRecChkNull(recOutTemp, "YD_GP"));
//	        	recInPara.setField("MODIFIER", 			"YDSYSTEM");
//	        	recInPara.setField("MSG_GP", 			"U");
//	        	ydDelegate.sendMsg(recInPara);
//	        	szMsg = "["+szOperationName+"] ["+szYD_WRK_PROG_STAT+"]인 스케줄["+szYD_CRN_SCH_ID+"]을 재전송";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//				
//				return YdConstant.RETN_CD_SUCCESS;
//				
//			// 150831 hun 무인화 테스트중 전문역전 현상 발생시.. 무인쪽에서 S로 작업요청 대응
//           	}else if(szWrkProgStat.equals("S") ){
//
//				szMsg="야드 무인쪽 S로 작업요청 경우";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//			 
//				//설비id로 크레인 스케줄을 조회한다. 현재 작업진행상태가 1또는 S인 경우를 조회...
//	        	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
//	        	recPara = JDTORecordFactory.getInstance().create();
//	        	recPara.setField("YD_EQP_ID", szEqpId);
//	        	/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnSchEqpIdWrkProgStat1S*/
//		    	intRtnVal = YdCrnSchDao.getYdCrnsch(recPara, rsResult, 605);
//				if(intRtnVal <= 0) {
//					//에러처리
//					szMsg="현재 작업진행상태가 1또는 S인 크레인 스케줄을 조회 중 Error";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//					return YdConstant.RETN_CD_FAILURE;
//				}
//			 
//        			
//    			//현재크레인이 작업중인 스케줄의 값을 다시 재전송한다.
//				rsResult.absolute(1);
//    			recOutTemp = JDTORecordFactory.getInstance().create();
//    			recOutTemp.setRecord(rsResult.getRecord());
//    			
//    			String szYD_CRN_SCH_ID			= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CRN_SCH_ID");
//    			String szYD_WRK_PROG_STAT		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WRK_PROG_STAT");
//    			
//            	recInPara = JDTORecordFactory.getInstance().create();
//        		//작업지시 전문 전송 data setup
//    			recInPara.setField("MSG_ID", 			"YDY5L004");
//    			recInPara.setField("MSG_GP", 			"I");
//    			recInPara.setField("YD_CRN_SCH_ID",    	szYD_CRN_SCH_ID);
//            	recInPara.setField("YD_WRK_PROG_STAT", 	szYD_WRK_PROG_STAT);
//            	recInPara.setField("YD_SCH_CD",        	ydDaoUtils.paraRecChkNull(recOutTemp, "YD_SCH_CD"));
//            	recInPara.setField("YD_GP",            	ydDaoUtils.paraRecChkNull(recOutTemp, "YD_GP"));
//            	recInPara.setField("MODIFIER", 			"YDSYSTEM");
//            	
//            	ydDelegate.sendMsg(recInPara);
//            	szMsg = "["+szOperationName+"] 현재 작업중["+szYD_WRK_PROG_STAT+"]인 스케줄["+szYD_CRN_SCH_ID+"]을 재전송";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//    			
//            	return YdConstant.RETN_CD_SUCCESS;
//        		
//        		
//        	//진행 상태가 'S'인경우 (무인화에서 요구)	
//        	
//           	}
//        	
//        	String szYD_CRN_SCH_ID_RE = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID_RE");
//        	ydUtils.putLog(szSessionName, szMethodName, "szYD_CRN_SCH_ID_RE_ASIS:" + szYD_CRN_SCH_ID_RE, YdConstant.DEBUG);
//        	String szSCH_CHK ="";
//        	//L2로 전송 ( 전송을 하진 않고 Consol창에 보여준다. )
//        	String szMSG ="";
//        	
//        	
//        	
//        	
//        	ydUtils.putLog(szSessionName, szMethodName, "####간섭구간 시스템 자동화 대상 체크 start####", YdConstant.INFO);
//        	
//        	if(rsCrnSch.size()>0){
//	        	recCrnSch = JDTORecordFactory.getInstance().create();
//	        	recCrnSch.setRecord(rsCrnSch.getRecord(0)); 
//	        	szYD_SCH_CD	   				   = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_SCH_CD"); 
//	        	String szYD_CRN_SCH_ID_AS	   = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_CRN_SCH_ID"); 
//	        	
//	        	
//				rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
//			    inRecord = JDTORecordFactory.getInstance().create();
//			    
//			    if(!"".equals(szYD_SCH_CD)){
//				    inRecord.setField("YD_GP",           	 szYD_SCH_CD.substring(0,1));
//				    inRecord.setField("YD_BAY_GP",           szYD_SCH_CD.substring(1,2));
//			    }
//			    sQueryId = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnSchRejectChk";
//			    int intRejCoilChk = ydCommDao.select(inRecord, rsResult, sQueryId);
//			    
//			    
//			    ydUtils.putLog(szSessionName, szMethodName, "간섭구간 시스템 자동화intRejCoilChk:"+intRejCoilChk, YdConstant.INFO);
//			    if(intRejCoilChk >0){
//			    	recCrnSch = JDTORecordFactory.getInstance().create();
//		        	recCrnSch.setRecord(rsResult.getRecord(0)); 
//		        	
//		        	String szYD_CRN_SCH_ID_RJ	   = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_CRN_SCH_ID");
//		        	String szGOD_YD_SCH_CD  	   = ydDaoUtils.paraRecChkNull(recCrnSch, "GOD_YD_SCH_CD");
//		        	
//		        	ydUtils.putLog(szSessionName, szMethodName, "간섭구간 반대편 대상스케쥴 코드 GOD_YD_SCH_CD:"+szGOD_YD_SCH_CD, YdConstant.INFO);
//		        	ydUtils.putLog(szSessionName, szMethodName, "간섭구간 시스템 자동화szYD_CRN_SCH_ID_RJ:"+szYD_CRN_SCH_ID_RJ+" , szYD_CRN_SCH_ID_AS:"+ szYD_CRN_SCH_ID_AS, YdConstant.INFO);
//		        	
//		        	if(!szYD_CRN_SCH_ID_AS.equals(szYD_CRN_SCH_ID_RJ)){
//		        		//간섭구간 선택된 대상 크레인 작업 지시 전송
//		        		
//		        		
//		        		recInPara = JDTORecordFactory.getInstance().create();	        	 
//		    			recInPara.setField("YD_CRN_SCH_ID"			, szYD_CRN_SCH_ID_RJ);
//		    			
//		        		//크레인스케줄의 순위 및 간섭구간 처리 여부 표기
//		        		/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnWrkReject*/
//		        		intRtnVal = YdCrnSchDao.updYdCrnsch(recInPara, 304);
//		    			if(intRtnVal <= 0) {
//		    				szMsg="procY5CrnWrkOrdReq updYdCrnsch : execution failed";
//	        				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	        				return YdConstant.RETN_CD_FAILURE;
//		    			}
//		    			rsCrnSch = JDTORecordFactory.getInstance().createRecordSet("Temp");
//		    			rsCrnSch.addAll(rsResult);
//		        	}
//			    }
//		    
//        	}
//		    ydUtils.putLog(szSessionName, szMethodName, "####간섭구간 시스템 자동화 대상 체크 end####", YdConstant.INFO);
//        	
//        	
//        	
//        	
//        	ydUtils.putLog(szSessionName, szMethodName, "rsCrnSch.size():" + rsCrnSch.size(), YdConstant.DEBUG);
//        	for(int Loop_i = 1; Loop_i <= rsCrnSch.size(); Loop_i++) {
//        		rsCrnSch.absolute(Loop_i);
//        		recCrnSch = JDTORecordFactory.getInstance().create();
//        		recCrnSch.setRecord(rsCrnSch.getRecord()); 
//        		
//        		szMSG ="YD_CRN_SCH_ID : " + recCrnSch.getFieldString("YD_CRN_SCH_ID") + ", ";
//        		szMSG =szMSG + "YD_WBOOK_ID : "   + recCrnSch.getFieldString("YD_WBOOK_ID") + ", ";
//        		szMSG =szMSG + "YD_WRK_PROG_STAT : " + recCrnSch.getFieldString("YD_WRK_PROG_STAT") + ", ";
//        		szMSG =szMSG + "YD_SCH_CD : "     + recCrnSch.getFieldString("YD_SCH_CD") + ", ";
//        		szMSG =szMSG + "YD_SCH_PRIOR : "     + recCrnSch.getFieldString("YD_SCH_PRIOR") + ", ";
//        		szMSG =szMSG + "YD_SCH_ST_GP : "     + recCrnSch.getFieldString("YD_SCH_ST_GP")  ;
//        		
//        		ydUtils.putLog(szSessionName, szMethodName, szMSG, YdConstant.DEBUG);
//        		
//        		if(szYD_CRN_SCH_ID_RE.equals(recCrnSch.getFieldString("YD_CRN_SCH_ID"))){
//        			szSCH_CHK =recCrnSch.getFieldString("YD_SCH_CD");
//        			
//        			if(szSCH_CHK.substring(2, 4).equals("PT")||szSCH_CHK.substring(2, 4).equals("TT")|| szSCH_CHK.substring(2, 4).equals("TR")){
//        				szYD_CRN_SCH_ID_RE="";
//        				
//        				szMsg="szYD_CRN_SCH_ID_RE값을 스케줄 코드가 TR,PT인경우 비우는 작업 수행";
//        				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//        			}
//        		}
//        	}
//        	
//        	ydUtils.putLog(szSessionName, szMethodName, "rsCrnSch.size() End:" + rsCrnSch.size(), YdConstant.DEBUG);
//        	
//        	
//        	recCrnSch = JDTORecordFactory.getInstance().create();
//        	recCrnSch.setRecord(rsCrnSch.getRecord(0));
//        	
//        	String szYD_WRK_PROG_STAT1 = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_WRK_PROG_STAT");
//        	szYD_SCH_CD				   = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_SCH_CD");
//        	String szydSchGp		   = ydDaoUtils.paraRecChkNull(recCrnSch, "YD_SCH_ST_GP");
//        	
//        	recInPara = JDTORecordFactory.getInstance().create();
//    		//작업지시 전문 전송 data setup
//			recInPara.setField("MSG_ID"			, "YDY5L004");
//			
//			
//			ydUtils.putLog(szSessionName, szMethodName, "szYD_CRN_SCH_ID_RE_TOBE:" + szYD_CRN_SCH_ID_RE, YdConstant.DEBUG);
//			ydUtils.putLog(szSessionName, szMethodName, "szYD_WRK_PROG_STAT1:" + szYD_WRK_PROG_STAT1, YdConstant.DEBUG);
//			ydUtils.putLog(szSessionName, szMethodName, "szYD_SCH_CD:" + szYD_SCH_CD, YdConstant.DEBUG);
//			
//			if("".equals(szYD_SCH_CD)){
//				if(szYD_CRN_SCH_ID_RE.equals("")){
//					szYD_SCH_CD ="HAPT01";
//				}else{
//					szYD_SCH_CD ="HAYD01";
//				}
//			 } 
//			
//			//차량상차 작업지시 순서
//			if(szYD_CRN_SCH_ID_RE.equals("")) {
//					 
//				recInPara.setField("YD_CRN_SCH_ID"	, ydDaoUtils.paraRecChkNull(recCrnSch, "YD_CRN_SCH_ID"));
// 			}else {
// 				if(szYD_WRK_PROG_STAT1.equals("1")){
// 					recInPara.setField("YD_CRN_SCH_ID"	, ydDaoUtils.paraRecChkNull(recCrnSch, "YD_CRN_SCH_ID"));
// 				}else{
// 					recInPara.setField("YD_CRN_SCH_ID"	, ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID_RE"));
// 				}
//			}
//        	
//        	if( szYD_WRK_PROG_STAT1.equals("") || szYD_WRK_PROG_STAT1.equals("W") || szYD_WRK_PROG_STAT1.equals("1")) {
//        		recInPara.setField("YD_WRK_PROG_STAT", "1");        		
//        	}else{
//        		recInPara.setField("YD_WRK_PROG_STAT", szYD_WRK_PROG_STAT1);
//        	}
//        	
//        	//작업지시일자
//        	if(szydSchGp.equals("I") ) {
//        		recInPara.setField("YD_SCH_ST_GP",      "");
//        	}else{
//        		recInPara.setField("YD_WORD_DT",      YdUtils.getCurDate("yyyyMMddHHmmss"));
//        	}
//        	
//        	recInPara.setField("MODIFIER", "YDSYSTEM");
//
//        	if (szWrkProgStat.equals("1") || szWrkProgStat.equals("2") || szWrkProgStat.equals("3")){
//        		//20090925 김진욱 추가 : 작업진행중인 작업을 재전송하는 경우는  MSG_GP 값을 'U' UPDATE로 설정해서 보낸다.
//            	recInPara.setField("MSG_GP", "U");
//        	}
//        	
//    		
//    		//크레인스케줄의 작업진행 상태를 권상지시로 변경
//    		intRtnVal = YdCrnSchDao.updYdCrnsch(recInPara, 0);
//			if(intRtnVal <= 0) {
//    			if(intRtnVal == 0) {
//    				szMsg="procY5CrnWrkOrdReq updYdCrnsch : data not found";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//    				return YdConstant.RETN_CD_NOTEXIST;
//    			}else if(intRtnVal == -2) {
//    				szMsg="procY5CrnWrkOrdReq updYdCrnsch : parameter error";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//    				return YdConstant.RETN_CD_NO_PARAM;
//    			}else{
//    				szMsg="procY5CrnWrkOrdReq updYdCrnsch : execution failed";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//    				return YdConstant.RETN_CD_FAILURE;
//    			}
//			}
//    		
//
//        	recInPara.setField("YD_SCH_CD", ydDaoUtils.paraRecChkNull(recCrnSch, "YD_SCH_CD"));
//        	recInPara.setField("YD_GP", ydDaoUtils.paraRecChkNull(recCrnSch, "YD_GP"));
//        	
//        	
////        	151001 hun 크레인 작업지시 보내기전 1단코일일 경우 2단 더미 체크 start
//        	szMsg="151001 hun 크레인의 작업진행상태 update후 1단일때 더미재 있는지 확인후 있으면 리스케쥴 start YD_CRN_SCH_ID=["+ydDaoUtils.paraRecChkNull(recInPara, "YD_CRN_SCH_ID")+"]";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
////        	151001 hun 1단일 경우 크레인 리스케쥴 체크 추가
//        	intRtnVal = this.Y5ChkWrkReSchdule(ydDaoUtils.paraRecChkNull(recInPara, "YD_CRN_SCH_ID"), rsCrnSch);
//    		if(intRtnVal <= 0) {
//				szMsg="크레인 리스케쥴 에러 발생";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//    		}
//    		szMsg="151001 hun 크레인의 작업진행상태 update후 1단일때 더미재 있는지 확인후 있으면 리스케쥴 END";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//			
//        	
//        	
//        	//작업지시 메세지 전송
//    		ydDelegate.sendMsg(recInPara);
//        	
//    		
//// hun 크레인 무인화 YDY5L008 start
//    		/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//	         * 			코일야드 차량작업 예정정보 전송  - YDY5L008
//	         * 업무기준 Desc : 크레인 작업지시 요구 시 차량작업일 경우 차량작업 예정정보 전송
//	         * 기능 추가 : hun
//	         * 일자 : 2015.07.02
//	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
////    		150706 hun 크레인 무인화 차량 작업일경우 차량작업 예정정보 전송
//    		/*
//    		String szCheckCar = recCrnSch.getFieldString("YD_SCH_CD");
//        	
//    		if(szCheckCar.substring(2, 4).equals("PT")||szCheckCar.substring(2, 4).equals("TT")|| szCheckCar.substring(2, 4).equals("TR")){
//    		
//				rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
//	        	recPara = JDTORecordFactory.getInstance().create();
//	        	if(szYD_CRN_SCH_ID_RE.equals("")) {
//					 
//	        		recPara.setField("YD_CRN_SCH_ID"	, ydDaoUtils.paraRecChkNull(recCrnSch, "YD_CRN_SCH_ID"));
//	 			}else {
//	 				recPara.setField("YD_CRN_SCH_ID"	, ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID_RE"));
//				}
//	        	
//	        	
//	        	szRtnMsg = this.callYDY5L008(recPara);
//	        	
//	        	if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {													//성공
//	        		szMsg = "[JSP Session](차량작업 예정정보송신) 호출 성공";
//					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
//				}else{
//					szMsg = "[JSP Session](차량작업 예정정보송신) 호출 실패";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				}
//	        	
//    		} // if
//    		*/
//// hun 크레인 무인화 YDY5L008 end
//        	
//		}catch(Exception e){
//			szMsg="Error:" +e.getMessage();
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			return YdConstant.RETN_CD_FAILURE;
//		}
//
//		szMsg="크레인 작업지시("+szMethodName+") 완료";
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		return YdConstant.RETN_CD_SUCCESS;
//
//	} //end of procY5CrnWrkOrdReq()
        

    

//    /**
//     * 오퍼레이션명 : C열연코일야드L2 차량작업정보 송신 (YDY5L008)
//     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//     * @param msgRecord
//     * @return String
//     * @throws JDTOException
//     */
//    public String callYDY5L008 ( JDTORecord recInPara )throws JDTOException  {
//    	JDTORecordSet  rsResult= JDTORecordFactory.getInstance().createRecordSet("temp");
//    	YdCarSchDao ydCarSchDao = new YdCarSchDao();
////    	JDTORecord recInPara = null;
//    	JDTORecord recOutTemp = null;
//    	int intRtnVal =0;
//    	String szMsg = "";
//    	String szMethodName = "callYDY5L008";
//    	String szOperationName			= "차량작업 예정정보 전송";
//    	String szLOAD_LOC_CD = "";
//    	
//    	szMsg="callYDY5L008("+szMethodName+") 시작";
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//    	
//    	try {
//    		
//	    	// // 차량작업 예정정보 조회
//    		ydUtils.putLog(szSessionName, szMethodName, "callYDY5L008 szYD_CAR_SCH_ID=("+ydDaoUtils.paraRecChkNull(recInPara , "YD_CAR_SCH_ID")+")", YdConstant.DEBUG);
//    		ydUtils.putLog(szSessionName, szMethodName, "callYDY5L008 szPT_LOAD_LOC=("+ydDaoUtils.paraRecChkNull(recInPara , "YD_CARUD_STOP_LOC")+")", YdConstant.DEBUG);
//    		
//	    	if (!"".equals(ydDaoUtils.paraRecChkNull(recInPara , "YD_CAR_SCH_ID") )){
//	    		
//	    		ydUtils.putLog(szSessionName, szMethodName, "callYDY5L008 YD_CAR_SCH_ID=("+ydDaoUtils.paraRecChkNull(recInPara , "YD_CAR_SCH_ID")+")", YdConstant.DEBUG);
//	    		/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschCarGetInWorkInfo_PIDEV*/
//		    	intRtnVal = ydCarSchDao.getYdCarsch(recInPara, rsResult, 435);
//	    		
//	    	}else{
//	    		
//	    		ydUtils.putLog(szSessionName, szMethodName, "callYDY5L008 CAR_NO=("+ydDaoUtils.paraRecChkNull(recInPara , "CAR_NO")+")", YdConstant.DEBUG);
//	    		ydUtils.putLog(szSessionName, szMethodName, "callYDY5L008 YD_CARUD_STOP_LOC=("+ydDaoUtils.paraRecChkNull(recInPara , "YD_CARUD_STOP_LOC")+")", YdConstant.DEBUG);
//	    		/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschCarGetInWorkByCarNo_PIDEV*/
//	    		intRtnVal = ydCarSchDao.getYdCarsch(recInPara, rsResult, 437);
//	    		
//	    	}
//	    	
//	    	if( intRtnVal <= 0 ) {
//				szMsg = " 차량스케줄이 존재하지 않습니다.";
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	            
////	            150907 hun 차량정보 요구에 의한 요청일때 깡통전문 발송을 위해 호출
//	            if("".equals(ydDaoUtils.paraRecChkNull(recInPara , "YD_CAR_SCH_ID")) ){
//	            recInPara.setField("MSG_ID"     , 		"YDY5L008");      // 전문번호
//				recInPara.setField("PT_LOAD_LOC",       ydDaoUtils.paraRecChkNull(recInPara, "YD_PT_LOAD_LOC")); // 상차도 위치
//				
//				ydDelegate.sendMsg(recInPara);
//	            }
//	            
//		    }else{
//		    	
//		    	rsResult.first();
//				
//				recOutTemp = JDTORecordFactory.getInstance().create();
//				recOutTemp.setRecord(rsResult.getRecord());
//				
//				//차량작업 예정정보 전문 data setup
//				recInPara.setField("MSG_ID"     , 		"YDY5L008");      // 전문번호
//	        	
//				recInPara.setField("PT_LOAD_LOC",       ydDaoUtils.paraRecChkNull(recOutTemp, "YD_PT_LOAD_LOC")); // 상차도 위치
//	        	recInPara.setField("CAR_NO"     ,       ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CAR_NO")); // 차량번호
//	        	recInPara.setField("PT_CLS"     ,		ydDaoUtils.paraRecChkNull(recOutTemp, "YD_PT_CLS"));
//	        	recInPara.setField("WORK_CLS"   ,   	ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WORK_CLS")); // 작업구분
//	        	recInPara.setField("PT_WTH"     , 		ydDaoUtils.paraRecChkNull(recOutTemp, "YD_PT_WTH"));  // 적재함 폭
//	        	recInPara.setField("PT_LEN"     , 		ydDaoUtils.paraRecChkNull(recOutTemp, "YD_PT_LEN")); // 적재함 길이
//	        	recInPara.setField("PT_HEIGHT"  , 		ydDaoUtils.paraRecChkNull(recOutTemp, "YD_PT_HEIGHT")); // 적재함 높이
//	        	recInPara.setField("RAIN_CLS"   ,	 	ydDaoUtils.paraRecChkNull(recOutTemp, "YD_RAIN_CLS")); // 우천차량 여부
//	        	recInPara.setField("WORK_COIL_MAX_CNT", ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WORK_COIL_MAX_CNT")); // 작업총 수량
//	        	
////	        	Coil 수만큼 for
//	        	for(int i=0; i<rsResult.size(); i++) {
//	        		rsResult.absolute(i);
//	        		recOutTemp.setRecord(rsResult.getRecord());
//	        		
//	            	szLOAD_LOC_CD 		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CAR_UPP_LOC_CD");
//				} // for
//	        	
//	        	
//	        	ydDelegate.sendMsg(recInPara);
//	        	
//	        	szMsg = "["+szOperationName+"] 코일야드 차량작업 예정정보 전송 완료";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		    }
//	    }catch(Exception e){
//			szMsg="Error:" +e.getMessage();
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			return YdConstant.RETN_CD_TC_ERROR;
//		}
//
//		szMsg="코일야드 차량작업 예정정보 전송 ("+szMethodName+") 완료";
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		return YdConstant.RETN_CD_SUCCESS;
//
//    }
    

//    /**
//     * 오퍼레이션명 : 크레인 작업지시(작업지시를 재요구 하는 경우 사용한다.)
//     *  
//     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//     * @param msgRecord
//     * @return
//     * @throws JDTOException
//     */
//    public int Y5ChkWrkProgStat(JDTORecord msgRecord, JDTORecordSet rsCrnSch)throws JDTOException  {
//    	YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
//    	YdEqpDao    ydEqpDao    = new YdEqpDao();
//    	
//        JDTORecordSet rsResult          = null;
//        JDTORecord recInTemp            = null;
//        JDTORecord recOutTemp           = null;
//    	
//    	int intRtnVal 					= 0 ;
//        
//        String szMsg              		= "";
//        String szMethodName       		= "Y5ChkWrkProgStat";
//        String szCrnSchId               = "";
//        
//        String szQuery                  = "";
//        
//        String szYD_EQP_ID              = "";
//        String szWrkProgStat            = "";
//        String szYdEqpStat              = "";
//        
//        
//        try{
////        	//파라미터 중 스케줄ID가 있다 스케줄 ID로 크레인 스케줄을 조회하여 작업예약 ID를 조회한다.
////        	szCrnSchId = ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_SCH_ID");
////
////        	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
////        	intRtnVal = YdCrnSchDao.getYdCrnsch(msgRecord, rsResult, 0);
////        	if(intRtnVal <= 0) {
////        		szMsg="크레인 스케줄 조회중 Error";
////    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
////        	}
//        	
//        	
//        	//작업진행상태
//        	szWrkProgStat = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WRK_PROG_STAT");
//        	
//        	
//        	//설비Table를 조회하여 설비상태가 '1 : 권상지시'또는 '3 : 권하지시'이라면 크레인 스케줄 Table에서 야드작업진행 상태가 1또는 3인 것만 찾는다. 1건만 나와야 정상이고 1건이 아니라면 Error처리
//        	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
//        	/*com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqp*/
//        	intRtnVal = ydEqpDao.getYdEqp(msgRecord, rsResult, 0);
//			if(intRtnVal <= 0) {
//				if(intRtnVal == 0) {
//					szMsg="Y5ChkWrkProgStat getYdEqp : data not found";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//				}else if(intRtnVal == -2) {
//					szMsg="Y5ChkWrkProgStat getYdEqp : parameter error";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				}
//				return intRtnVal = -1;
//			}
//			
//			recOutTemp = JDTORecordFactory.getInstance().create();
//			recOutTemp.setRecord(rsResult.getRecord(0));
//			szYdEqpStat = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_STAT");
//			//설비상태가 1또는 3인경우
//			if(szYdEqpStat.equals("1") || szYdEqpStat.equals("2") || szYdEqpStat.equals("3")){
//				//설비id로 크레인 스케줄을 조회한다. 현재 작업진행상태가 1또는 3인 경우를 조회...
//	        	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
//	        	/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnSchEqpIdWrkProgStat*/
//		    	intRtnVal = ydCrnSchDao.getYdCrnsch(msgRecord, rsResult, 16);
//				if(intRtnVal != 1) {
//					//에러처리
//					szMsg="현재 작업진행상태가 1또는 3인 크레인 스케줄을 조회 중 Error";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//					return intRtnVal = -1;
//				}
//			}
//			
//        	rsCrnSch.addAll(rsResult);
//        	
//		}catch(Exception e){
//
//			szMsg="Error:" +e.getMessage();
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			return intRtnVal = -1;
//		}
//
//
//		szMsg="chkWrkProgStat("+szMethodName+") 처리 완료";
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		return intRtnVal = 1;
//	} //end of Y5ChkWrkProgStat()


	
	
	
	
	
//	/**
//	 * 오퍼레이션명 : 크레인 작업지시(작업이 없을 경우 요구한다.)
//	 *  
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param msgRecord
//	 * @return
//	 * @throws JDTOException
//	 */
//	public int Y5ChkWrkProgStatW(JDTORecord msgRecord, JDTORecordSet rsCrnSch)throws JDTOException  {
//		YdEqpDao ydEqpDao = new YdEqpDao();
//		YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
//		JPlateYdCommDAO ydCommDao = new JPlateYdCommDAO();
//		JDTORecordSet rsResult = null;
//		JDTORecordSet rsResult2 = null;
//		JDTORecord recIntTemp  = null;
//		JDTORecord para  = null;
//		JDTORecord inRecord  = null;
//		JDTORecord outRecord  = null;
//		EJBConnector ejbConn = null;
//		
//	    int intRtnVal 		   = 0 ;
//	    
//	    JDTORecord recCrnSch            = null;
//	    String szMsg           = "";
//	    String szMethodName    = "Y5ChkWrkProgStatW";
//	    
//	    String szYD_EQP_ID     = "";
//	    String szSchCd     	   = "";
//       	String szYD_WRK_PROG_STAT 	= "";
//       	String szYD_UP_WO_LAYER = "";
//    	String szYD_SCH_CD1 		= "";
//    	String szSND_FLAG 		= "N";
////    	String szFLAG_CANCEL	= "";
//	    try{
//	    	
//	    	
//	    	//스케줄 코드 Check
//	    	szSchCd 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
//	    	szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
////	    	szFLAG_CANCEL = ydDaoUtils.paraRecChkNull(msgRecord, "FLAG_CANCEL");
//	    	
////	    	ydUtils.putLog(szSessionName, szMethodName, "szFLAG_CANCEL" + szFLAG_CANCEL, YdConstant.DEBUG);
//	    	
//	    	//크레인 스케줄 전체에서 우선순위가 가장 빠른 작업을 조회한다. 크레인 스케줄을 조회한다.
//        	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
//        	para = JDTORecordFactory.getInstance().create();
//        	para.setField("YD_EQP_ID", ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID"));
//	
//	        /*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnSchEqpIdPrior*/
//		    intRtnVal = ydCrnSchDao.getYdCrnsch(para, rsResult, 15);
//	
//        	if(intRtnVal == 0) {
//        		//더이상 크레인 스케줄에 작업이 없을 경우 설비Table에 설비상태를 'W' 명령선택대기(idle)로 변경한다.
//        		recIntTemp = JDTORecordFactory.getInstance().create();
//        		recIntTemp.setField("YD_EQP_ID", szYD_EQP_ID);
//        		recIntTemp.setField("YD_EQP_STAT", "W");
//        		intRtnVal = ydEqpDao.updYdEqp(recIntTemp, 0);
//    			if(intRtnVal <= 0) {
//	    			if(intRtnVal == 0) {
//	    				szMsg=" Y5ChkWrkProgStatW updYdEqp : data not found";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//	    			}else if(intRtnVal == -1) {
//	    				szMsg=" Y5ChkWrkProgStatW updYdEqp : duplicate data,";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
//	    			}else if(intRtnVal == -2) {
//	    				szMsg=" Y5ChkWrkProgStatW updYdEqp : parameter error";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	    			}else if(intRtnVal == -3){
//	    				szMsg=" Y5ChkWrkProgStatW updYdEqp : execution failed";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	    			}
//	    		}
//    			
//    			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//    	         * 			C열연코일야드L2 크레인작업실적응답 전송  - YDY5L005
//    	         * 업무기준 Desc : C열연코일야드L2에서 크레인 작업지시 요구 시 크레인 작업이 더 이상 존재하지 않을 경우 크레인작업실적응답 전송
//    	         * 일자 : 2009.07.01
//    	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//    			recIntTemp = JDTORecordFactory.getInstance().create(); 
//    			recIntTemp.setField("MSG_ID"        , "YDY5L005");
//    			recIntTemp.setField("YD_EQP_ID"     , szYD_EQP_ID);						//야드설비ID
//    			recIntTemp.setField("YD_L2_WR_GP"   , YdConstant.CRN_WRK_RE_WO_DMD);	//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
//    			recIntTemp.setField("YD_L3_HD_RS_CD", YdConstant.CRN_WRK_RE_CD_NO_WRK);	//야드L3처리결과코드 : 9999 - 크레인작업이 더 이상 없습니다.
//    			ydDelegate.sendMsg(recIntTemp);
//    			szMsg = "[C열연코일야드L2 크레인 작업지시 요구]크레인 작업이 더 이상 존재하지 않을 경우 C열연코일야드L2 크레인작업실적응답[YDY5L005] 전송 완료" ;
//                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//    	        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//    			
//				szMsg="더이상의 크레인스케줄이 조회되지 않습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//    			
//        		return intRtnVal = -1;
//        		
//        	}else if(intRtnVal < 0) {
//        		szMsg="크레인 스케줄 조회중 Error";
//    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//    			return intRtnVal = -1;
//        	}
//        	
//         	
//        	rsCrnSch.addAll(rsResult);
//        	
//        	//-------------------------------------------------------------------------------------------------------------
////        	rsCrnSch.first();
////
////        	for(int Loop_i = 0; Loop_i < rsCrnSch.size(); Loop_i++) {
////        		recIntTemp = JDTORecordFactory.getInstance().create();
////        		
////        		recIntTemp = rsCrnSch.getRecord(Loop_i);
////            	
////        		szYD_WRK_PROG_STAT 	= ydDaoUtils.paraRecChkNull(recIntTemp, "YD_WRK_PROG_STAT");
////            	szYD_SCH_CD1 		= ydDaoUtils.paraRecChkNull(recIntTemp, "YD_SCH_CD");
////            	if(szFLAG_CANCEL.equals("")){
////            		break;
////            	} else {
////	            	if((szYD_SCH_CD1.equals("HEDE01UM"))||(szYD_SCH_CD1.equals("HGFE01UM"))||(szYD_SCH_CD1.equals("HHKE01UM")) ||  //보급존
////	        		   (szYD_SCH_CD1.equals("HHKD01UM"))||(szYD_SCH_CD1.equals("HEDD01UM")) ){    
////	        			szSND_FLAG = "Y";
////	        			continue;
////	        		} else { 
////	        			szSND_FLAG = "N";
////	        			
////	        			break;
////	        		}
////            	}
////        	}
//        	
//        	
//        	rsResult.first();
//
//        	recIntTemp	= rsResult.getRecord();
//        	
//        	szYD_WRK_PROG_STAT 	= ydDaoUtils.paraRecChkNull(recIntTemp, "YD_WRK_PROG_STAT");
//        	szYD_UP_WO_LAYER 	= ydDaoUtils.paraRecChkNull(recIntTemp, "YD_UP_WO_LAYER");
//        	//-------------------------------------------------------------------------------------------------------------
////        	
////			if((szSND_FLAG.equals("Y"))|| (rsCrnSch.size()==0)){                                //HFL 보급존
//// 				
////	   			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
////    	         * 			C열연코일야드L2 크레인작업실적응답 전송  - YDY5L005
////    	         * 업무기준 Desc : C열연코일야드L2에서 크레인 작업지시 요구 시 크레인 작업이 더 이상 존재하지 않을 경우 크레인작업실적응답 전송
////    	         * 일자 : 2009.07.01
////    	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
////    			recIntTemp = JDTORecordFactory.getInstance().create(); 
////    			recIntTemp.setField("MSG_ID"        , "YDY5L005");
////    			recIntTemp.setField("YD_EQP_ID"     , szYD_EQP_ID);						//야드설비ID
////    			recIntTemp.setField("YD_L2_WR_GP"   , YdConstant.CRN_WRK_RE_WO_DMD);	//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
////    			recIntTemp.setField("YD_L3_HD_RS_CD", YdConstant.CRN_WRK_RE_CD_NO_WRK);	//야드L3처리결과코드 : 9999 - 크레인작업이 더 이상 없습니다.
////    			ydDelegate.sendMsg(recIntTemp);
////    			szMsg = "[C열연코일야드L2 크레인 작업지시 요구]크레인 작업이 더 이상 존재하지 않을 경우 C열연코일야드L2 크레인작업실적응답[YDY5L005] 전송 완료" ;
////                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
////    	        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
////    			
////				szMsg="더이상의 크레인스케줄이 조회되지 않습니다.";
////				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
////    			
////        		return intRtnVal = -1;
////			}
//        	//다음 스케줄을 찾았을 경우 
//        	if( szYD_WRK_PROG_STAT.equals("") || szYD_WRK_PROG_STAT.equals(YdConstant.YD_EQP_STAT_IDLE)
//        			|| szYD_WRK_PROG_STAT.equals(YdConstant.YD_EQP_STAT_UP_WO))	{
//	    		recIntTemp = JDTORecordFactory.getInstance().create();
//	    		recIntTemp.setField("YD_EQP_ID", 		szYD_EQP_ID);
//	    		recIntTemp.setField("YD_EQP_STAT", 		"1");
//	    		recIntTemp.setField("YD_WORD_DT",      	YdUtils.getCurDate("yyyyMMddHHmmss"));
//	    		intRtnVal = ydEqpDao.updYdEqp(recIntTemp, 0);
//				if(intRtnVal <= 0) {
//	    			if(intRtnVal == 0) {
//	    				szMsg=" Y5ChkWrkProgStatW updYdEqp : data not found";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//	    			}else if(intRtnVal == -1) {
//	    				szMsg=" Y5ChkWrkProgStatW updYdEqp : duplicate data,";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
//	    			}else if(intRtnVal == -2) {
//	    				szMsg=" Y5ChkWrkProgStatW updYdEqp : parameter error";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	    			}else if(intRtnVal == -3){
//	    				szMsg=" Y5ChkWrkProgStatW updYdEqp : execution failed";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	    			}
//	    			return intRtnVal = -1;
//	    		}
//        	}else{
//        		szMsg="크레인의 스케줄의 야드작업진행상태["+szYD_WRK_PROG_STAT+"]가 W가 아니므로 크레인설비의 상태를 변경하지 않음";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//        	}
//        	
//		}catch(Exception e){
//	
//			szMsg="Error:" +e.getMessage();
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			return intRtnVal = -1;
//		}
//	
//	
//		szMsg="chkWrkProgStatW("+szMethodName+") 처리완료";
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		return intRtnVal = 1;
//	
//	} //end of Y5ChkWrkProgStatW()
	
	

//	/**
//	 * 오퍼레이션명 : 크레인 리스케쥴(스케쥴 취소후 다시 호출)
//	 *  
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param msgRecord
//	 * @return
//	 * @throws JDTOException
//	 */
//	public int Y5ChkWrkReSchdule(String szYD_CRN_SCH_ID, JDTORecordSet rsCrnSch)throws JDTOException  {
//		
//		JPlateYdCommDAO ydCommDao = new JPlateYdCommDAO();
//		YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
//		JDTORecordSet rsResult = null;
//		JDTORecord para  = null;
//		JDTORecord msgRecord  = null;
//		JDTORecord inRecord  = JDTORecordFactory.getInstance().create();
//		JDTORecord outRecord  = null;
//		JDTORecord[] inDto = new JDTORecord[1];
//		EJBConnector ejbConn = null;
//		
//	    int intRtnVal 		   = 0 ;
//	    
//	    String szMsg           = "크레인 리스케쥴(스케쥴 취소후 다시 호출)";
//	    String szMethodName    = "Y5ChkWrkReSchdule";
//	    
//	    String szYD_EQP_ID     = "";
//	    String szSchCd     	   = "";
//       	String szYD_WRK_PROG_STAT 	= "";
//       	String szYD_UP_WO_LAYER = "";
//    	String sQueryId = "";
//    	String szYD_SCH_CD = "";
//    	String sRTN_CD		= "";
//		String sRTN_MSG		= "";
//
//	    try{
//	    	
//        	szMsg="151001 hun 크레인의 작업진행상태 update후 더미재 있는지 확인후 있으면 리스케쥴 start szYD_CRN_SCH_ID=["+szYD_CRN_SCH_ID+"]";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//			
////			2단 더미 체크
//			rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
//        	para = JDTORecordFactory.getInstance().create();
//        	para.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
//        	
//			sQueryId = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.get2UpLyrCrnSchExist";
//			intRtnVal = ydCommDao.select(para, rsResult, sQueryId);
//			
//			if(intRtnVal <= 0 )
//			{
//				szMsg="더미 없음 pass 처리완료";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//				return intRtnVal = 1;
//			}else{
//				
//				szMsg="더미 존재 스케쥴취소 시작";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//				
//				//크레인 스케줄을 조회한다.
//	        	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
//	        	para = JDTORecordFactory.getInstance().create();
//	        	para.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
//		
//			    intRtnVal = ydCrnSchDao.getYdCrnsch(para, rsResult, 0);
//				
//				rsResult.first();
//				inRecord = rsResult.getRecord();
//	        	
//				inRecord.setField("IS_LAST_SELECTED" ,"1");
//				inRecord.setField("YD_L2_RETURN_FLAG" ,"Y");
//				ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);			
//				outRecord 	= (JDTORecord)ejbConn.trx("WrkCancel", new Class[] { JDTORecord.class }, new Object[] { inRecord });
//				sRTN_CD		= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
//				sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
//				
//				if ("0".equals(sRTN_CD)) {
//					szMsg="스케쥴 취소 실패!!";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//					
//					return intRtnVal = -1;
//				}else{
//					szMsg="스케쥴 취소완료 스케쥴 재편성 시작";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//					
//					inRecord.setField("YD_GP" ,"J");
//					inDto[0] = inRecord;
//					ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
//					outRecord = (JDTORecord) ejbConn.trx("trxRunSchedule", new Class[] { JDTORecord[].class },	new Object[] { inDto });
//				}
//			}
//			
//			szMsg="크레인의 작업진행상태 update후 1단일때 더미재 있는지 확인후 있으면 리스케쥴 end";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//        	
//        	
//		}catch(Exception e){
//	
//			szMsg="Error:" +e.getMessage();
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			return intRtnVal = -1;
//		}
//	
//	
//		szMsg="chkWrkProgStatW("+szMethodName+") 처리완료";
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		return intRtnVal = 1;
//	
//	} //end of Y5ChkWrkReSchdule()
	
	
//	/**
//	 * 오퍼레이션명 : 크레인 작업지시(현재 진행중인 작업이 있을경우 해당작업)
//	 *  
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param msgRecord
//	 * @return
//	 * @throws JDTOException
//	 */
//	public int Y5ChkWrkProgStat4(JDTORecord msgRecord, JDTORecordSet rsCrnSch)throws JDTOException  {
//		 YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
//		 
//		JDTORecordSet rsResult          = null;
//		JDTORecord para  = null;
//	    int intRtnVal 					= 0 ;
//	    
//	    String szMsg              		= "";
//	    String szMethodName       		= "Y5ChkWrkProgStat4";
//	    
//	    String szSchCd                  = "";
//	    String szQuery                  = "";
//	    String szDnCarFlag                  = "N";
//	    
//	    try{
//	    	//스케줄 코드 Check
//	    	szSchCd = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
//	    	if(szSchCd.equals("")) {
//	    		szMsg="스케줄코드가 없습니다. : parameter error";
//    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//    			return intRtnVal = -1;
//	    	}
//	    	szDnCarFlag = ydDaoUtils.paraRecChkNull(msgRecord, "YD_DN_CAR_FLAG");
//
///*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschSCHCD*/
////intRtnVal = ydCrnSchDao.getYdCrnsch(msgRecord, rsResult, 6);
// 
//	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
//        	para = JDTORecordFactory.getInstance().create();
//        	para.setField("YD_EQP_ID", ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID"));
//        	
//        	
//        	if("Y".equals(szDnCarFlag)){
//        		/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnSchEqpIdPriorByTR*/
//    		    intRtnVal = ydCrnSchDao.getYdCrnsch(para, rsResult, 606);
//        	}else{
//
//		        /*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnSchEqpIdPrior*/
//			    intRtnVal = ydCrnSchDao.getYdCrnsch(para, rsResult, 15);
//        	}
//	    	//조회된 크레인 스케줄이 없다면  전체에서 빠른 스케줄을 호출한다.
//	    	if(intRtnVal <= 0) {
////	    		rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
////	    		intRtnVal = this.Y5ChkWrkProgStatW(msgRecord, rsResult);	
////	    		if(intRtnVal == -1) {
//	    			szMsg="크레인 작업이 없습니다!!!";
//	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	    			return intRtnVal = -1;
////	    		}else if(intRtnVal == 0) {
////	    			szMsg="같은 스케줄 코드의 크레인 작업이 없습니다.";
////	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
////	    			return intRtnVal = 0;
////	    		}
//	    		
//	    	}
//	
//	    	
//	    	rsCrnSch.addAll(rsResult);
//	
//		}catch(Exception e){
//	
//			szMsg="Error:" +e.getMessage();
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			return intRtnVal = -1;
//		}
//	
//	
//		szMsg="크레인 작업지시("+szMethodName+") 완료";
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		return intRtnVal = 1;
//	} //end of Y5ChkWrkProgStat4()
	
	
//	/**
//	 * 오퍼레이션명 : C 열연 입측 인터락인 경우
//	 *  
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param msgRecord
//	 * @return
//	 * @throws JDTOException
//	 */
//	public JDTORecord Y5ChkWrkProgStatInlock(JDTORecord msgRecord, String szYD_STK_COL_GP, String szYD_SCH_CD, String szYD_TO_LOC_GUIDE_BED)throws JDTOException  {
//		YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
//		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
//		JDTORecordSet jRecordSet        = null;
//		JDTORecord outRecord			= null;
//		JDTORecord inRecord				= null;
//		JDTORecord recGetStkBedData 	= null;
//		JDTORecord recUpdCrnSchData		= null;
//	   	JDTORecordSet rsUpStkBed  = JDTORecordFactory.getInstance().createRecordSet("Temp");
//	   	JDTORecordSet rsDnStkBed  = JDTORecordFactory.getInstance().createRecordSet("Temp");
//	   	JDTORecord outRecord1			= JDTORecordFactory.getInstance().create();
//	   	JDTORecord recPara			= JDTORecordFactory.getInstance().create();
//	   	YdStkBedDao ydStkBedDao = new YdStkBedDao();		
//	    int intRtnVal 					= 0 ;
//	    
//	    String szMsg              		= "";
//	    String szMethodName       		= "Y5ChkWrkProgStatInlock";
//	    
//	    String szSchId                  = "";
//	    String szQuery                  = "";
//	    String szBUN                    = "";
//	    String sYD_STK_COL_GP= "";
//	    String sYD_STK_BED_NO= "";
//	    String sSTL_NO       = "";
//	    
//	    
//	    try{
//	    	//스케줄 코드 Check
//	    	szSchId = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");
//	    	if(szSchId.equals("")) {
//	    		szMsg="스케줄ID가 없습니다. : parameter error";
//    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	   			outRecord1.setField("RTN_CD" , "0");	
//				outRecord1.setField("RTN_MSG", szMsg);	
//				return outRecord1;
//	    	}
//	    	
//	    	jRecordSet = JDTORecordFactory.getInstance().createRecordSet("Temp");
////C증설	    	
//	    	//takeout권상
//	    	if((szYD_SCH_CD.equals("HBFE03LM"))||
//	    	   (szYD_SCH_CD.equals("HBKE03LM"))||
//	    	   (szYD_SCH_CD.equals("HAKE03LM"))||
//	   	       (szYD_SCH_CD.equals("HCFE03LM"))||
//	   	       (szYD_SCH_CD.equals("HCKE03LM"))||
//	    	   (szYD_SCH_CD.equals("HEDE03LM"))||
//	    	   (szYD_SCH_CD.equals("HGFE03LM"))||
//	    	   (szYD_SCH_CD.equals("HHKE03LM"))||
//	    	   //추출 
//	    	   (szYD_SCH_CD.equals("HBKD03LM"))||
//	    	   (szYD_SCH_CD.equals("HAKD03LM"))|| 
//	   	       (szYD_SCH_CD.equals("HCKD03LM"))||
//	    	   (szYD_SCH_CD.equals("HEDD03LM"))|| 
//	    	   (szYD_SCH_CD.equals("HHKD03LM"))
//	    	   
//	    	    ){ // TAKEOUT존
//	    		
//	    		/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdTrackingBun*/
//		    	intRtnVal = ydCrnSchDao.getYdCrnsch(msgRecord, jRecordSet, 305);
//		    	if(intRtnVal > 0) {
//			    	jRecordSet.first();
//			        //레코드셋의 사이즈값으로 ErrorCheck
//			        if(jRecordSet.size() == 0){
//			            szMsg = "no data fount!!!, ErrorCode:" + intRtnVal;
//			            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			   			outRecord1.setField("RTN_CD" , "0");	
//						outRecord1.setField("RTN_MSG", szMsg);	
//						return outRecord1;
//			        }
//			        outRecord = jRecordSet.getRecord();
//
//			        recPara = JDTORecordFactory.getInstance().create();
//			        //intRtnVal = this.ydEqpGpConvert(outRecord, recPara); 
//			        intRtnVal = this.ydEqpGpConvertNew(outRecord, recPara); 
//					if(intRtnVal < 0 ){
//						szMsg = "C열연설비구분변경실패!!";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//
//						outRecord.setField("RTN_CD" , "0");	
//						outRecord.setField("RTN_MSG", "[전문 이상] 설비구분변경실패(ydEqpGpConvert)");	
//						return outRecord;
//					}
//					
//			        sSTL_NO 	   = ydDaoUtils.paraRecChkNull(outRecord, "STL_NO");
//			        sYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_ID");
//			        sYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
//
//			    } else {
//					szMsg="열연 tracking에 코일 정보 없음";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//		   			outRecord1.setField("RTN_CD" , "0");	
//					outRecord1.setField("RTN_MSG", szMsg);	
//					return outRecord1;
//		    	}   
////C증설
//	    	} else if((szYD_SCH_CD.equals("HEDE01UM")) ||
//	    			  (szYD_SCH_CD.equals("HGFE01UM")) ||
//	    			  (szYD_SCH_CD.equals("HHKE01UM")) 
//	    			  ) { //보급존
//		        	sYD_STK_COL_GP = szYD_STK_COL_GP;
//			        sYD_STK_BED_NO = "02";
//
//	    	} else if((szYD_SCH_CD.equals("HCFE01UM"))) { //보급존
//    				sYD_STK_COL_GP = szYD_SCH_CD.substring(0, 3) +"E04";
//    				sYD_STK_BED_NO = "02";
//
//	    	} else if((szYD_SCH_CD.equals("HCKE01UM"))) { //보급존
//	        		sYD_STK_COL_GP = szYD_SCH_CD.substring(0, 3) +"E03";
//	        		sYD_STK_BED_NO = "02";
//
//	    	} else if((szYD_SCH_CD.equals("HBFE01UM"))) { //보급존
//	        		sYD_STK_COL_GP = szYD_SCH_CD.substring(0, 3) +"E05";
//	        		sYD_STK_BED_NO = "02";
//		        
//	    	} else if((szYD_SCH_CD.equals("HBKE01UM"))) { //보급존
//		        	sYD_STK_COL_GP = szYD_SCH_CD.substring(0, 3) +"E04";
//			        sYD_STK_BED_NO = "02";
//
//	    	} else if((szYD_SCH_CD.equals("HAKE01UM"))) { //보급존
//	        	sYD_STK_COL_GP = szYD_SCH_CD.substring(0, 3) +"E05";
//		        sYD_STK_BED_NO = "02";
//
//	    	}else if((szYD_SCH_CD.equals("HAKD02LM")) ||
//	    			  (szYD_SCH_CD.equals("HBKD02LM")) ||
//	    			  (szYD_SCH_CD.equals("HCKD02LM")) ||
//	    			  (szYD_SCH_CD.equals("HCFD02LM")) ||
//	    			  (szYD_SCH_CD.equals("HEDD02LM")) ||	    			  
//	    			  (szYD_SCH_CD.equals("HGFD02LM")) ||
//	    			  (szYD_SCH_CD.equals("HHKD02LM")) 
//	    			  ) { //재작업지시
//	    		   if(szYD_SCH_CD.equals("HAKD02LM")){
//			        sYD_STK_COL_GP = szYD_SCH_CD.substring(0, 3) +"E05";  //입측으로
//	    		   }else if(szYD_SCH_CD.equals("HBKD02LM")){
//			        sYD_STK_COL_GP = szYD_SCH_CD.substring(0, 3) +"E04";  //입측으로
//	    		   }else if(szYD_SCH_CD.equals("HCKD02LM")){
//			        sYD_STK_COL_GP = szYD_SCH_CD.substring(0, 3) +"E03";  //입측으로
//	    		   }else  if(szYD_SCH_CD.equals("HCFD02LM")){
//			        sYD_STK_COL_GP = szYD_SCH_CD.substring(0, 3) +"E04";  //입측으로
//	    		   }else{
//	    			sYD_STK_COL_GP = szYD_SCH_CD.substring(0, 3) +"E01";  //입측으로
//	    		   }
//	    			   
//			        sYD_STK_BED_NO = "02";
//
//	    	} else {  //TAKEIN존
//		        sYD_STK_COL_GP = szYD_STK_COL_GP;
//		        sYD_STK_BED_NO = szYD_TO_LOC_GUIDE_BED;
//	    	
//	    	}
//	    	
//	    	szMsg="크레인스케줄 권상위치 Update sYD_STK_COL_GP:"+sYD_STK_COL_GP+" szYD_SCH_CD:"+szYD_SCH_CD;
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//
//	    	
////C증설
//	    	if(//입측존
//	    	   (szYD_SCH_CD.equals("HBFE03LM"))||
//	    	   (szYD_SCH_CD.equals("HBKE03LM"))||
//	    	   (szYD_SCH_CD.equals("HAKE03LM"))||
//	    	   (szYD_SCH_CD.equals("HCFE03LM"))||
//	    	   (szYD_SCH_CD.equals("HCKE03LM"))||
//	 	       (szYD_SCH_CD.equals("HEDE03LM"))||
//	    	   (szYD_SCH_CD.equals("HGFE03LM"))||
//	    	   (szYD_SCH_CD.equals("HHKE03LM"))||
//	    	    //추출존
//	    	   (szYD_SCH_CD.equals("HBKD03LM"))||
//	    	   (szYD_SCH_CD.equals("HAKD03LM"))|| 
//	    	   (szYD_SCH_CD.equals("HCKD03LM"))||
//	 	       (szYD_SCH_CD.equals("HEDD03LM"))|| 
//	    	   (szYD_SCH_CD.equals("HHKD03LM"))
//	    	   ){ // TAKEOUT존
////SJH
//	    		
//				JDTORecordSet   outRecSet   = JDTORecordFactory.getInstance().createRecordSet("YD");
//				inRecord = JDTORecordFactory.getInstance().create();
//				inRecord.setField("STL_NO",   sSTL_NO);
////	getYdStklyr24  
//				/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdStkPosInfo_PIDEV*/
//				intRtnVal = ydStkLyrDao.getYdStklyr(inRecord, outRecSet, 24);
//	    
//				for(int Loop_i = 1; Loop_i <= outRecSet.size(); Loop_i++) {
//	        		
//	        		outRecSet.absolute(Loop_i);
//	        		outRecord = JDTORecordFactory.getInstance().create();
//	        		outRecord.setRecord(outRecSet.getRecord());
//	        		 
//	        		inRecord = JDTORecordFactory.getInstance().create();
//					//적치단 재료상태가 적치 가능이면 재료 등록
//					//적치단 테이블 업데이트
//					//적치열구분 = 설비ID
//					inRecord.setField("YD_STK_COL_GP", 	    ydDaoUtils.paraRecChkNull(outRecord, "YD_STK_COL_GP"));
//					inRecord.setField("YD_STK_BED_NO", 	    ydDaoUtils.paraRecChkNull(outRecord, "YD_STK_BED_NO"));
//					inRecord.setField("YD_STK_LYR_NO", 	    ydDaoUtils.paraRecChkNull(outRecord, "YD_STK_LYR_NO"));
//					inRecord.setField("YD_STK_LYR_MTL_STAT","E");
//					inRecord.setField("STL_NO", 			"");
//					
//					//업데이트 실행
//					//com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrInStlNo
//			    	intRtnVal = ydStkLyrDao.updYdStklyrNEW(inRecord, 303);
//					if(intRtnVal <= 0){
//						szMsg="적치단 update Error";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			   			outRecord1.setField("RTN_CD" , "0");	
//						outRecord1.setField("RTN_MSG", szMsg);	
//						return outRecord1;
//
//					}
//	        	}
//		
//		    	inRecord = JDTORecordFactory.getInstance().create();
//				 
//				//적치단 재료상태가 적치 가능이면 재료 등록
//		    	inRecord.setField("YD_STK_COL_GP", 	    sYD_STK_COL_GP);
//		    	inRecord.setField("YD_STK_BED_NO", 	    sYD_STK_BED_NO);
//		    	inRecord.setField("YD_STK_LYR_NO", 	    "001");
//		    	inRecord.setField("YD_STK_LYR_MTL_STAT", "C");
//		    	inRecord.setField("STL_NO", 			sSTL_NO);
//				
//				//업데이트 실행
//				//com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrInStlNo
//		    	intRtnVal = ydStkLyrDao.updYdStklyrNEW(inRecord, 303);
//				if(intRtnVal <= 0){
//					szMsg="적치단 update Error";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//		   			outRecord1.setField("RTN_CD" , "0");	
//					outRecord1.setField("RTN_MSG", szMsg);	
//					return outRecord1;
//
//				}
//
//	
//		        rsUpStkBed = JDTORecordFactory.getInstance().createRecordSet("");
//				recGetStkBedData = JDTORecordFactory.getInstance().create();
//				recGetStkBedData.setField("YD_STK_COL_GP", sYD_STK_COL_GP );
//				recGetStkBedData.setField("YD_STK_BED_NO", sYD_STK_BED_NO );
//				recGetStkBedData.setField("YD_STK_LYR_NO", "001" );
//				/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedLyr*/
//				intRtnVal = ydStkBedDao.getYdStkbed(recGetStkBedData, rsUpStkBed, 304);
//				if(intRtnVal <= 0){
//					szMsg="저장위치가 이상합니다";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//		   			outRecord1.setField("RTN_CD" , "0");	
//					outRecord1.setField("RTN_MSG", szMsg);	
//					return outRecord1;
//	
//				}
//				
//
//				rsUpStkBed.absolute(1);
//				inRecord = JDTORecordFactory.getInstance().create();
//				inRecord.setRecord(rsUpStkBed.getRecord());
//					
//					
//				szMsg="크레인스케줄 권상위치 Update";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	
//				recUpdCrnSchData = JDTORecordFactory.getInstance().create();
//				
//				recUpdCrnSchData.setField("YD_CRN_SCH_ID",  			szSchId ) ;
////				recUpdCrnSchData.setField("YD_WRK_PROG_STAT",  			"W" ) ;
//				
//				recUpdCrnSchData.setField("YD_UP_WO_LOC",   			recGetStkBedData.getFieldString("YD_STK_COL_GP") + recGetStkBedData.getFieldString("YD_STK_BED_NO")) ;
//				recUpdCrnSchData.setField("YD_UP_WO_LAYER", 			recGetStkBedData.getFieldString("YD_STK_LYR_NO") ) ;
//				recUpdCrnSchData.setField("YD_UP_WO_LOC_XAXIS",  		ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_LYR_XAXIS") ) ;
//				recUpdCrnSchData.setField("YD_UP_WO_XAXIS_GAP_MAX",  	ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_BED_XAXIS_TOL") ) ;
//				recUpdCrnSchData.setField("YD_UP_WO_XAXIS_GAP_MIN",  	ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_BED_XAXIS_TOL") ) ;
//				recUpdCrnSchData.setField("YD_UP_WO_LOC_YAXIS",  		ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_LYR_YAXIS") ) ;
//				recUpdCrnSchData.setField("YD_UP_WO_YAXIS_GAP_MAX",  	ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_BED_YAXIS_TOL") ) ;
//				recUpdCrnSchData.setField("YD_UP_WO_YAXIS_GAP_MIN",  	ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_BED_YAXIS_TOL") ) ;
//				recUpdCrnSchData.setField("YD_UP_WO_ZAXIS_GAP_MAX",  	String.valueOf(YdConstant.C_COIL_CRANE_GAP_Z) ) ;
//				recUpdCrnSchData.setField("YD_UP_WO_ZAXIS_GAP_MIN",  	String.valueOf(YdConstant.C_COIL_CRANE_GAP_Z) ) ;
//				recUpdCrnSchData.setField("YD_DN_WO_LOC",  				"" ) ;
//				recUpdCrnSchData.setField("YD_DN_WO_LAYER",  			"" ) ;
//		
//		
//				
//	    	} else {
//		        rsDnStkBed = JDTORecordFactory.getInstance().createRecordSet("");
//				recGetStkBedData = JDTORecordFactory.getInstance().create();
//				recGetStkBedData.setField("YD_STK_COL_GP", sYD_STK_COL_GP );
//				recGetStkBedData.setField("YD_STK_BED_NO", sYD_STK_BED_NO );
//				recGetStkBedData.setField("YD_STK_LYR_NO", "001"  );
//				/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedLyr*/
//				intRtnVal = ydStkBedDao.getYdStkbed(recGetStkBedData, rsDnStkBed, 304);
//				if(intRtnVal <= 0){
//					szMsg="저장위치가 이상합니다";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//		   			outRecord1.setField("RTN_CD" , "0");	
//					outRecord1.setField("RTN_MSG", szMsg);	
//					return outRecord1;
//
//				}
//				rsDnStkBed.absolute(1);
//				inRecord = JDTORecordFactory.getInstance().create();
//				inRecord.setRecord(rsDnStkBed.getRecord());
//					
//					
//				szMsg="크레인스케줄 권하지시위치 Update4";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//
//				recUpdCrnSchData = JDTORecordFactory.getInstance().create();
//				
//				recUpdCrnSchData.setField("YD_CRN_SCH_ID",  			szSchId ) ;
////				recUpdCrnSchData.setField("YD_WRK_PROG_STAT",  			"W" ) ;
//				
//				recUpdCrnSchData.setField("YD_DN_WO_LOC",   			recGetStkBedData.getFieldString("YD_STK_COL_GP") + recGetStkBedData.getFieldString("YD_STK_BED_NO")) ;
//				recUpdCrnSchData.setField("YD_DN_WO_LAYER", 			recGetStkBedData.getFieldString("YD_STK_LYR_NO") ) ;
//				recUpdCrnSchData.setField("YD_DN_WO_LOC_XAXIS",  		ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_LYR_XAXIS") ) ;
//				recUpdCrnSchData.setField("YD_DN_WO_XAXIS_GAP_MAX",  	ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_BED_XAXIS_TOL") ) ;
//				recUpdCrnSchData.setField("YD_DN_WO_XAXIS_GAP_MIN",  	ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_BED_XAXIS_TOL") ) ;
//				recUpdCrnSchData.setField("YD_DN_WO_LOC_YAXIS",  		ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_LYR_YAXIS") ) ;
//				recUpdCrnSchData.setField("YD_DN_WO_YAXIS_GAP_MAX",  	ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_BED_YAXIS_TOL") ) ;
//				recUpdCrnSchData.setField("YD_DN_WO_YAXIS_GAP_MIN",  	ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_BED_YAXIS_TOL") ) ;
//				recUpdCrnSchData.setField("YD_DN_WO_ZAXIS_GAP_MAX",  	String.valueOf(YdConstant.C_COIL_CRANE_GAP_Z) ) ;
//				recUpdCrnSchData.setField("YD_DN_WO_ZAXIS_GAP_MIN",  	String.valueOf(YdConstant.C_COIL_CRANE_GAP_Z) ) ;
//	    		
//	    	}
//				
//			 /*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnsch*/ 
//			intRtnVal = ydCrnSchDao.updYdCrnsch(recUpdCrnSchData, 0);		        
//			if(intRtnVal <= 0) {
//    			if(intRtnVal == 0) {
//    				szMsg=" updYdCrnsch data not found";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//    			}else if(intRtnVal == -1) {
//    				szMsg=" updYdCrnsch duplicate data,";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
//    			}else if(intRtnVal == -2) {
//    				szMsg=" updYdCrnsch parameter error";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//    			}else if(intRtnVal == -3){
//    				szMsg=" updYdCrnsch execution failed";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//    			}
//	   			outRecord1.setField("RTN_CD" , "0");	
//				outRecord1.setField("RTN_MSG", szMsg);	
//				return outRecord1;
//    		}
//		
//		}catch(Exception e){
//	
//			szMsg="Error:" +e.getMessage();
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//   			outRecord1.setField("RTN_CD" , "0");	
//			outRecord1.setField("RTN_MSG", szMsg);	
//			return outRecord1;
//		}
//	
//	
//		szMsg="크레인 작업지시("+szMethodName+") 완료";
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		outRecord1.setField("RTN_CD" , "1");	
//		return outRecord1;
//	} //end of Y5ChkWrkProgStatInlock()
    
//	/**
//	 * 오퍼레이션명 : C 열연 추출 인터락인 경우 HFL 보급존
//	 *  
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param msgRecord
//	 * @return
//	 * @throws JDTOException
//	 */
//	public JDTORecord Y5ChkWrkProgStatOutlock(JDTORecord msgRecord, String szYD_STK_COL_GP, String szYD_SCH_CD)throws JDTOException  {
//		YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
//		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
//		JDTORecordSet jRecordSet        = null;
//		JDTORecord outRecord			= null;
//		JDTORecord inRecord				= null;
//		JDTORecord recGetStkBedData 	= null;
//		JDTORecord recUpdCrnSchData		= null;
//		JDTORecordSet rsUpStkBed  = JDTORecordFactory.getInstance().createRecordSet("Temp");
//	   	JDTORecordSet rsDnStkBed  = JDTORecordFactory.getInstance().createRecordSet("Temp");
//	   	YdStkBedDao ydStkBedDao = new YdStkBedDao();		
//	    int intRtnVal 					= 0 ;
//	    JDTORecord outRecord1			= JDTORecordFactory.getInstance().create();
//	    String szMsg              		= "";
//	    String szMethodName       		= "Y5ChkWrkProgStatOutlock";
//	    
//	    String szSchId                  = "";
//	    String szQuery                  = "";
//	    String szBUN                    = "";
//	    String sYD_STK_COL_GP= "";
//	    String sYD_STK_BED_NO= "";
//	    String sSTL_NO = "";
//	    
//	    
//	    try{
//	    	//스케줄 코드 Check
//	    	szSchId = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");
//	    	if(szSchId.equals("")) {
//	    		szMsg="스케줄ID가 없습니다. : parameter error";
//    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	   			outRecord1.setField("RTN_CD" , "0");	
//				outRecord1.setField("RTN_MSG", szMsg);	
//				return outRecord1;
//	    	}
//	    	
//	    	jRecordSet = JDTORecordFactory.getInstance().createRecordSet("Temp");
////C증설	    	
//	    	if((szYD_SCH_CD.equals("HBKD01UM"))||
//	    	   (szYD_SCH_CD.equals("HAKD01UM"))||
//	 	       (szYD_SCH_CD.equals("HCKD01UM"))||
//		       (szYD_SCH_CD.equals("HHKD01UM"))||
//	    	   (szYD_SCH_CD.equals("HEDD01UM")) 
//	    	   ){   
//	    		sYD_STK_COL_GP = szYD_STK_COL_GP;
//		        sYD_STK_BED_NO = "01";
////		    	inRecord = JDTORecordFactory.getInstance().create();
////		    	inRecord.setField("YD_SCH_CD", szYD_SCH_CD );
////		    	/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdTrackingStlNo*/
////		    	intRtnVal = ydCrnSchDao.getYdCrnsch(inRecord, jRecordSet, 307);
////		    	if(intRtnVal > 0) {
////			    	jRecordSet.first();
////			        //레코드셋의 사이즈값으로 ErrorCheck
////			        if(jRecordSet.size() == 0){
////			            szMsg = "no data fount!!!, ErrorCode:" + intRtnVal;
////			            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
////			   			outRecord1.setField("RTN_CD" , "0");	
////						outRecord1.setField("RTN_MSG", szMsg);	
////						return outRecord1;
////			        }
////			        outRecord = jRecordSet.getRecord();
////			        sSTL_NO 	   = ydDaoUtils.paraRecChkNull(outRecord, "STL_NO");
////			        
////		    	} else {
////		            szMsg = "no data fount!!!, ErrorCode:" + intRtnVal;
////		            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
////		   			outRecord1.setField("RTN_CD" , "0");	
////					outRecord1.setField("RTN_MSG", szMsg);	
////					return outRecord1;
////		    	}   
////		    	
////				JDTORecordSet   outRecSet   = JDTORecordFactory.getInstance().createRecordSet("YD");
////				inRecord = JDTORecordFactory.getInstance().create();
////				inRecord.setField("STL_NO",   sSTL_NO);
//////	getYdStklyr24
////				/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdStkPosInfo_PIDEV*/
////				intRtnVal = ydStkLyrDao.getYdStklyr(inRecord, outRecSet, 24);
////				if (intRtnVal > 0) {
////					//적치되어 있는 정보 삭제처리
////					outRecSet.first();
////					outRecord = outRecSet.getRecord();
////					//UPDATE 항목 record  생성
////					inRecord = JDTORecordFactory.getInstance().create();
////					
////					//적치단 재료상태가 적치 가능이면 재료 등록
////					//적치단 테이블 업데이트
////					//적치열구분 = 설비ID
////					inRecord.setField("YD_STK_COL_GP", 	    ydDaoUtils.paraRecChkNull(outRecord, "YD_STK_COL_GP"));
////					inRecord.setField("YD_STK_BED_NO", 	    ydDaoUtils.paraRecChkNull(outRecord, "YD_STK_BED_NO"));
////					inRecord.setField("YD_STK_LYR_NO", 	    ydDaoUtils.paraRecChkNull(outRecord, "YD_STK_LYR_NO"));
////					inRecord.setField("YD_STK_LYR_MTL_STAT", "E");
////					inRecord.setField("STL_NO", 			    "");
////					
////					//업데이트 실행
////					//com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrInStlNo
////			    	intRtnVal = ydStkLyrDao.updYdStklyr(inRecord, 303);
////					if(intRtnVal <= 0){
////						szMsg="적치단 update Error";
////						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
////			   			outRecord1.setField("RTN_CD" , "0");	
////						outRecord1.setField("RTN_MSG", szMsg);	
////						return outRecord1;
////
////					}
////				} 
////		
////		    	inRecord = JDTORecordFactory.getInstance().create();
////				
////				//적치단 재료상태가 적치 가능이면 재료 등록
////		    	inRecord.setField("YD_STK_COL_GP", 	    sYD_STK_COL_GP);
////		    	inRecord.setField("YD_STK_BED_NO", 	    sYD_STK_BED_NO);
////		    	inRecord.setField("YD_STK_LYR_NO", 	    "001");
////		    	inRecord.setField("YD_STK_LYR_MTL_STAT", "C");
////		    	inRecord.setField("STL_NO", 			sSTL_NO);
////				
////				//업데이트 실행
////				//com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrInStlNo
////		    	intRtnVal = ydStkLyrDao.updYdStklyr(inRecord, 303);
////				if(intRtnVal <= 0){
////					szMsg="적치단 update Error";
////					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
////		   			outRecord1.setField("RTN_CD" , "0");	
////					outRecord1.setField("RTN_MSG", szMsg);	
////					return outRecord1;
////
////				} 
//
//				rsDnStkBed = JDTORecordFactory.getInstance().createRecordSet("");
//				recGetStkBedData = JDTORecordFactory.getInstance().create();
//				recGetStkBedData.setField("YD_STK_COL_GP", sYD_STK_COL_GP );
//				recGetStkBedData.setField("YD_STK_BED_NO", sYD_STK_BED_NO );
//				recGetStkBedData.setField("YD_STK_LYR_NO", "001"  );
//				/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedLyr*/
//				intRtnVal = ydStkBedDao.getYdStkbed(recGetStkBedData, rsDnStkBed, 304);
//				if(intRtnVal <= 0){
//					szMsg="execution failed";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//		   			outRecord1.setField("RTN_CD" , "0");	
//					outRecord1.setField("RTN_MSG", szMsg);	
//					return outRecord1;
//
//				}
//				rsDnStkBed.absolute(1);
//				inRecord = JDTORecordFactory.getInstance().create();
//				inRecord.setRecord(rsDnStkBed.getRecord());
//					
//					
//				szMsg="크레인스케줄 권하지시위치 Update5";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//
//				recUpdCrnSchData = JDTORecordFactory.getInstance().create();
//				
//				recUpdCrnSchData.setField("YD_CRN_SCH_ID",  			szSchId ) ;
////				recUpdCrnSchData.setField("YD_WRK_PROG_STAT",  			"W" ) ;
//				
//				recUpdCrnSchData.setField("YD_DN_WO_LOC",   			recGetStkBedData.getFieldString("YD_STK_COL_GP") + recGetStkBedData.getFieldString("YD_STK_BED_NO")) ;
//				recUpdCrnSchData.setField("YD_DN_WO_LAYER", 			recGetStkBedData.getFieldString("YD_STK_LYR_NO") ) ;
//				recUpdCrnSchData.setField("YD_DN_WO_LOC_XAXIS",  		ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_LYR_XAXIS") ) ;
//				recUpdCrnSchData.setField("YD_DN_WO_XAXIS_GAP_MAX",  	ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_BED_XAXIS_TOL") ) ;
//				recUpdCrnSchData.setField("YD_DN_WO_XAXIS_GAP_MIN",  	ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_BED_XAXIS_TOL") ) ;
//				recUpdCrnSchData.setField("YD_DN_WO_LOC_YAXIS",  		ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_LYR_YAXIS") ) ;
//				recUpdCrnSchData.setField("YD_DN_WO_YAXIS_GAP_MAX",  	ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_BED_YAXIS_TOL") ) ;
//				recUpdCrnSchData.setField("YD_DN_WO_YAXIS_GAP_MIN",  	ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_BED_YAXIS_TOL") ) ;
//				recUpdCrnSchData.setField("YD_DN_WO_ZAXIS_GAP_MAX",  	String.valueOf(YdConstant.C_COIL_CRANE_GAP_Z) ) ;
//				recUpdCrnSchData.setField("YD_DN_WO_ZAXIS_GAP_MIN",  	String.valueOf(YdConstant.C_COIL_CRANE_GAP_Z) ) ;
//	    		
//	    	}
//			 /*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnsch*/ 
//			intRtnVal = ydCrnSchDao.updYdCrnsch(recUpdCrnSchData, 0);		        
//			if(intRtnVal <= 0) {
//    			if(intRtnVal == 0) {
//    				szMsg=" updYdCrnsch data not found";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//    			}else if(intRtnVal == -1) {
//    				szMsg=" updYdCrnsch duplicate data,";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
//    			}else if(intRtnVal == -2) {
//    				szMsg=" updYdCrnsch parameter error";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//    			}else if(intRtnVal == -3){
//    				szMsg=" updYdCrnsch execution failed";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//    			}
//	   			outRecord1.setField("RTN_CD" , "0");	
//				outRecord1.setField("RTN_MSG", szMsg);	
//				return outRecord1;
//    		}
//		
//		}catch(Exception e){
//	
//			szMsg="Error:" +e.getMessage();
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//   			outRecord1.setField("RTN_CD" , "0");	
//			outRecord1.setField("RTN_MSG", szMsg);	
//			return outRecord1;
//		}
//	
//	
//		szMsg="크레인 작업지시("+szMethodName+") 완료";
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		outRecord1.setField("RTN_CD" 		, "1");	
//		outRecord1.setField("YD_STK_COL_GP"	, sYD_STK_COL_GP);	
//		outRecord1.setField("YD_STK_BED_NO"	, sYD_STK_BED_NO);	
//		outRecord1.setField("RTN_MSG"		, szMsg);	
//		return outRecord1;
//	} //end of Y5ChkWrkProgStatOutlock()
    
    
//	/**
//	 * 오퍼레이션명 : C 열연 추출 인터락인 경우(추출LINEOFF)
//	 *  
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param msgRecord
//	 * @return
//	 * @throws JDTOException
//	 */
//	public JDTORecord Y5ChkWrkProgStatLineOfflock(JDTORecord msgRecord, String szYD_STK_COL_GP, String szYD_SCH_CD,String sYD_WBOOK_ID, String sYD_CRN_SCH_ID)throws JDTOException  {
//		YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
//		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
//		
//		JDTORecordSet jRecordSet        = null;
//		JDTORecord outRecord			= JDTORecordFactory.getInstance().create();
//		JDTORecord outRecord1			= JDTORecordFactory.getInstance().create();
//		JDTORecord inRecord				= null;
//		JDTORecord recGetStkBedData 	= null;
//		JDTORecord recUpdCrnSchData		= null;
//		JDTORecordSet rsUpStkBed  = JDTORecordFactory.getInstance().createRecordSet("Temp");
//	   	JDTORecordSet rsDnStkBed  = JDTORecordFactory.getInstance().createRecordSet("Temp");
//	   	YdStkBedDao ydStkBedDao = new YdStkBedDao();		
//	    int intRtnVal 					= 0 ;
//	    
//	    String szMsg              		= "";
//	    String szMethodName       		= "Y5ChkWrkProgStatLineOfflock";
//	    
//	    String szSchId                  = "";
//	    String szSchCd                  = "";
//	    String szQuery                  = "";
//	    String szBUN                    = "";
//	    String sYD_STK_COL_GP			= "";
//	    String sYD_STK_BED_NO			= "";
//	    String sSTL_NO       			= "";
//	    String stkColGp 				= "";
//	    
//	    try{
//	    	//스케줄 코드 Check
//	    	szSchCd = szYD_SCH_CD;
//	    	if(szSchCd.equals("")) {
//	    		szMsg="스케줄CD가 없습니다. : parameter error";
//    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//    			outRecord1.setField("RTN_CD" , "0");	
//				outRecord1.setField("RTN_MSG", szMsg);	
//				return outRecord1;
//	
//	    	}
//	    	//스케줄 코드 Check
//	    	szSchId = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");
//	    	if(szSchId.equals("")) {
//	    		szMsg="스케줄ID가 없습니다. : parameter error";
//    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//       			outRecord1.setField("RTN_CD" , "0");	
//				outRecord1.setField("RTN_MSG", szMsg);	
//				return outRecord1;
//	    	}
//	    	
//	    	jRecordSet = JDTORecordFactory.getInstance().createRecordSet("Temp");
//			//조업정보 read
//	  		/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdTrackingStlno*/
//
//	    	inRecord = JDTORecordFactory.getInstance().create();
//	    	inRecord.setField("YD_SCH_CD", szYD_SCH_CD );
//
//	    	intRtnVal = ydCrnSchDao.getYdCrnsch(inRecord, jRecordSet, 307);
//	    	if(intRtnVal > 0) {
//		    	jRecordSet.first();
//		        //레코드셋의 사이즈값으로 ErrorCheck
//		        if(jRecordSet.size() == 0){
//		            szMsg = "no data fount!!!, ErrorCode:" + intRtnVal;
//		            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//		   			outRecord1.setField("RTN_CD" , "0");	
//					outRecord1.setField("RTN_MSG", szMsg);	
//					return outRecord1;
//		        }
//		        outRecord = jRecordSet.getRecord();
//		        sYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(outRecord, "YD_STK_COL_GP_UP_WO");
//		        sYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(outRecord, "YD_STK_BED_NO");
//		        sYD_WBOOK_ID   = ydDaoUtils.paraRecChkNull(outRecord, "YD_WBOOK_ID");
//		        sYD_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(outRecord, "YD_CRN_SCH_ID");
//		        sSTL_NO 	   = ydDaoUtils.paraRecChkNull(outRecord, "STL_NO");
//		        
//	    	} else {
//	            szMsg = "no data fount!!!, ErrorCode:" + intRtnVal;
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	   			outRecord1.setField("RTN_CD" , "0");	
//				outRecord1.setField("RTN_MSG", szMsg);	
//				return outRecord1;
//	    	}   
//	    	
//			JDTORecordSet   outRecSet   = JDTORecordFactory.getInstance().createRecordSet("YD");
//			inRecord = JDTORecordFactory.getInstance().create();
//			inRecord.setField("STL_NO",   sSTL_NO);
////getYdStklyr24  
//			/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdStkPosInfo_PIDEV*/
//			intRtnVal = ydStkLyrDao.getYdStklyr(inRecord, outRecSet, 24);
////			if (intRtnVal > 0) {
////				//적치되어 있는 정보 삭제처리
////				outRecSet.first();
////				outRecord = outRecSet.getRecord();
////				//UPDATE 항목 record  생성
////				inRecord = JDTORecordFactory.getInstance().create();
////				
////				//적치단 재료상태가 적치 가능이면 재료 등록
////				//적치단 테이블 업데이트
////				//적치열구분 = 설비ID
////				inRecord.setField("YD_STK_COL_GP", 	    ydDaoUtils.paraRecChkNull(outRecord, "YD_STK_COL_GP"));
////				inRecord.setField("YD_STK_BED_NO", 	    ydDaoUtils.paraRecChkNull(outRecord, "YD_STK_BED_NO"));
////				inRecord.setField("YD_STK_LYR_NO", 	    ydDaoUtils.paraRecChkNull(outRecord, "YD_STK_LYR_NO"));
////				inRecord.setField("YD_STK_LYR_MTL_STAT","E");
////				inRecord.setField("STL_NO", 			"");
////				
////				//업데이트 실행
////				//com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrInStlNo
////		    	intRtnVal = ydStkLyrDao.updYdStklyr(inRecord, 303);
////				if(intRtnVal <= 0){
////					szMsg="적치단 update Error";
////					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
////		   			outRecord1.setField("RTN_CD" , "0");	
////					outRecord1.setField("RTN_MSG", szMsg);	
////					return outRecord1;
////
////				}
////
////
////			} 
//			
//        	for(int Loop_i = 1; Loop_i <= outRecSet.size(); Loop_i++) {
//        		
//        		outRecSet.absolute(Loop_i);
//        		outRecord = JDTORecordFactory.getInstance().create();
//        		outRecord.setRecord(outRecSet.getRecord());
//        		 
//        		inRecord = JDTORecordFactory.getInstance().create();
//				//적치단 재료상태가 적치 가능이면 재료 등록
//				//적치단 테이블 업데이트
//				//적치열구분 = 설비ID
//				inRecord.setField("YD_STK_COL_GP", 	    ydDaoUtils.paraRecChkNull(outRecord, "YD_STK_COL_GP"));
//				inRecord.setField("YD_STK_BED_NO", 	    ydDaoUtils.paraRecChkNull(outRecord, "YD_STK_BED_NO"));
//				inRecord.setField("YD_STK_LYR_NO", 	    ydDaoUtils.paraRecChkNull(outRecord, "YD_STK_LYR_NO"));
//				inRecord.setField("YD_STK_LYR_MTL_STAT","E");
//				inRecord.setField("STL_NO", 			"");
//				
//				stkColGp = ydDaoUtils.paraRecChkNull(outRecord, "YD_STK_COL_GP") ;
//				stkColGp =stkColGp.substring(2 , 4);
//				if(!"TC".equals(stkColGp)){
//				
//					//업데이트 실행
//					//com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrInStlNo
//			    	intRtnVal = ydStkLyrDao.updYdStklyrNEW(inRecord, 303);
//					if(intRtnVal <= 0){
//						szMsg="적치단 update Error";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			   			outRecord1.setField("RTN_CD" , "0");	
//						outRecord1.setField("RTN_MSG", szMsg);	
//						return outRecord1;
//	
//					}
//				}else{
//					szMsg=" 추출 인터락인 경우 대차 위치에서는 비우는 작업을 안한다.@@@";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//				}
//        	}
//	
//	    	
//	    	inRecord = JDTORecordFactory.getInstance().create();
//			
//			//적치단 재료상태가 적치 가능이면 재료 등록
//	    	inRecord.setField("YD_STK_COL_GP", 	    sYD_STK_COL_GP);
//	    	inRecord.setField("YD_STK_BED_NO", 	    sYD_STK_BED_NO);
//	    	inRecord.setField("YD_STK_LYR_NO", 	    "001");
//	    	inRecord.setField("YD_STK_LYR_MTL_STAT", "C");
//	    	inRecord.setField("STL_NO", 			sSTL_NO);
//			
//			//업데이트 실행
//			//com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrInStlNo
//	    	intRtnVal = ydStkLyrDao.updYdStklyrNEW(inRecord, 303);
//			if(intRtnVal <= 0){
//				szMsg="적치단 update Error";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	   			outRecord1.setField("RTN_CD" , "0");	
//				outRecord1.setField("RTN_MSG", szMsg);	
//				return outRecord1;
//
//			}
//
//	    	
//	    	
// 	        rsUpStkBed = JDTORecordFactory.getInstance().createRecordSet("");
//			recGetStkBedData = JDTORecordFactory.getInstance().create();
//			recGetStkBedData.setField("YD_STK_COL_GP", sYD_STK_COL_GP );
//			recGetStkBedData.setField("YD_STK_BED_NO", sYD_STK_BED_NO );
//			recGetStkBedData.setField("YD_STK_LYR_NO", "001"  );
//			/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedLyr*/
//			intRtnVal = ydStkBedDao.getYdStkbed(recGetStkBedData, rsUpStkBed, 304);
//			if(intRtnVal <= 0){
//				szMsg="execution failed";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	   			outRecord1.setField("RTN_CD" , "0");	
//				outRecord1.setField("RTN_MSG", szMsg);	
//				return outRecord1;
//
//			}
//			
//			rsUpStkBed.absolute(1);
//			inRecord = JDTORecordFactory.getInstance().create();
//			inRecord.setRecord(rsUpStkBed.getRecord());
//				
//				
//			szMsg="크레인스케줄 권상위치 Update";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//
//			recUpdCrnSchData = JDTORecordFactory.getInstance().create();
//			
//			recUpdCrnSchData.setField("YD_CRN_SCH_ID",  			sYD_CRN_SCH_ID ) ;
////			recUpdCrnSchData.setField("YD_WRK_PROG_STAT",  			"W" ) ;
//			
//			recUpdCrnSchData.setField("YD_UP_WO_LOC",   			recGetStkBedData.getFieldString("YD_STK_COL_GP") + recGetStkBedData.getFieldString("YD_STK_BED_NO")) ;
//			recUpdCrnSchData.setField("YD_UP_WO_LAYER", 			recGetStkBedData.getFieldString("YD_STK_LYR_NO") ) ;
//			recUpdCrnSchData.setField("YD_UP_WO_LOC_XAXIS",  		ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_LYR_XAXIS") ) ;
//			recUpdCrnSchData.setField("YD_UP_WO_XAXIS_GAP_MAX",  	ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_BED_XAXIS_TOL") ) ;
//			recUpdCrnSchData.setField("YD_UP_WO_XAXIS_GAP_MIN",  	ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_BED_XAXIS_TOL") ) ;
//			recUpdCrnSchData.setField("YD_UP_WO_LOC_YAXIS",  		ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_LYR_YAXIS") ) ;
//			recUpdCrnSchData.setField("YD_UP_WO_YAXIS_GAP_MAX",  	ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_BED_YAXIS_TOL") ) ;
//			recUpdCrnSchData.setField("YD_UP_WO_YAXIS_GAP_MIN",  	ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_BED_YAXIS_TOL") ) ;
//			recUpdCrnSchData.setField("YD_UP_WO_ZAXIS_GAP_MAX",  	String.valueOf(YdConstant.C_COIL_CRANE_GAP_Z) ) ;
//			recUpdCrnSchData.setField("YD_UP_WO_ZAXIS_GAP_MIN",  	String.valueOf(YdConstant.C_COIL_CRANE_GAP_Z) ) ;
//
//			/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnsch*/ 
//			intRtnVal = ydCrnSchDao.updYdCrnsch(recUpdCrnSchData, 0);	 	        
//			if(intRtnVal <= 0) {
//    			if(intRtnVal == 0) {
//    				szMsg=" updYdCrnsch data not found";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//    			}else if(intRtnVal == -1) {
//    				szMsg=" updYdCrnsch duplicate data,";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
//    			}else if(intRtnVal == -2) {
//    				szMsg=" updYdCrnsch parameter error";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//    			}else if(intRtnVal == -3){
//    				szMsg=" updYdCrnsch execution failed";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//    			}
//       			outRecord1.setField("RTN_CD" , "0");	
//				outRecord1.setField("RTN_MSG", szMsg);	
//				return outRecord1;
//			}
//		
//		}catch(Exception e){
//	
//			szMsg="Error:" +e.getMessage();
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//   			outRecord1.setField("RTN_CD" , "0");	
//			outRecord1.setField("RTN_MSG", szMsg);	
//			return outRecord1;
//		}
//	
//		
//		szMsg="크레인 작업지시("+szMethodName+") 완료";
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		outRecord1.setField("RTN_CD" 		, "1");	
//		outRecord1.setField("YD_STK_COL_GP"	, sYD_STK_COL_GP);	
//		outRecord1.setField("YD_STK_BED_NO"	, sYD_STK_BED_NO);	
//		outRecord1.setField("YD_WBOOK_ID"	, sYD_WBOOK_ID);	
//		outRecord1.setField("YD_CRN_SCH_ID"	, sYD_CRN_SCH_ID);	
//		outRecord1.setField("RTN_MSG"		, szMsg);	
//		return outRecord1;
//	} //end of Y5ChkWrkProgStatLineOfflock()
    
    
    
    
//	/**
//	 * 오퍼레이션명 : C 열연 CONV 인터락인 경우()
//	 *  
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param msgRecord
//	 * @return
//	 * @throws JDTOException
//	 */
//	public JDTORecord Y5ChkWrkProgStatConvlock(JDTORecord msgRecord, String szYD_STK_COL_GP, String szYD_SCH_CD,String sYD_WBOOK_ID, String sYD_CRN_SCH_ID)throws JDTOException  {
//		YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
//		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
//		
//		JDTORecordSet jRecordSet        = null;
//		JDTORecord outRecord			= null;
//		JDTORecord outRecord1			= JDTORecordFactory.getInstance().create();
//		JDTORecord inRecord				= null;
//		JDTORecord recGetStkBedData 	= null;
//		JDTORecord recUpdCrnSchData		= null;
//		JDTORecordSet rsUpStkBed  = JDTORecordFactory.getInstance().createRecordSet("Temp");
//	   	JDTORecordSet rsDnStkBed  = JDTORecordFactory.getInstance().createRecordSet("Temp");
//	   	YdStkBedDao ydStkBedDao = new YdStkBedDao();		
//	    int intRtnVal 					= 0 ;
//	    
//	    String szMsg              		= "";
//	    String szMethodName       		= "Y5ChkWrkProgStatConvlock";
//	    
//	    String szSchId                  = "";
//	    String szSchCd                  = "";
//	    String szQuery                  = "";
//	    String szBUN                    = "";
//	    String sYD_STK_COL_GP			= "";
//	    String sYD_STK_BED_NO			= "";
//	    String sSTL_NO       			= "";
//	    
//	    
//	    try{
//	    	//스케줄 코드 Check
//	    	szSchCd = szYD_SCH_CD;
//	    	if(szSchCd.equals("")) {
//	    		szMsg="스케줄CD가 없습니다. : parameter error";
//    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//    			outRecord1.setField("RTN_CD" , "0");	
//				outRecord1.setField("RTN_MSG", szMsg);	
//				return outRecord1;
//	
//	    	}
//	    	//스케줄 코드 Check
//	    	szSchId = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");
//	    	if(szSchId.equals("")) {
//	    		szMsg="스케줄ID가 없습니다. : parameter error";
//    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//       			outRecord1.setField("RTN_CD" , "0");	
//				outRecord1.setField("RTN_MSG", szMsg);	
//				return outRecord1;
//	    	}
//	    	
//	    	jRecordSet = JDTORecordFactory.getInstance().createRecordSet("Temp");
//			//조업정보 read
//	  		/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdCvTrackingStlNo1*/
//
//	    	inRecord = JDTORecordFactory.getInstance().create();
//	    	inRecord.setField("YD_SCH_CD", szYD_SCH_CD );
//	    	inRecord.setField("YD_CRN_SCH_ID", szSchId );
//
//	    	intRtnVal = ydCrnSchDao.getYdCrnsch(inRecord, jRecordSet, 501);
//	    	if(intRtnVal > 0) {
//		    	jRecordSet.first();
//		        //레코드셋의 사이즈값으로 ErrorCheck
//		        if(jRecordSet.size() == 0){
//		            szMsg = "no data fount!!!, ErrorCode:" + intRtnVal;
//		            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//		   			outRecord1.setField("RTN_CD" , "0");	
//					outRecord1.setField("RTN_MSG", szMsg);	
//					return outRecord1;
//		        }
//		        outRecord = jRecordSet.getRecord();
//		        sYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(outRecord, "YD_STK_COL_GP_UP_WO");
//		        sYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(outRecord, "YD_STK_BED_NO");
//		        sYD_WBOOK_ID   = ydDaoUtils.paraRecChkNull(outRecord, "YD_WBOOK_ID");
//		        sYD_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(outRecord, "YD_CRN_SCH_ID");
//		        sSTL_NO 	   = ydDaoUtils.paraRecChkNull(outRecord, "STL_NO");
//		        
//	    	} else {
//	            szMsg = "no data fount!!!, ErrorCode:" + intRtnVal;
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	   			outRecord1.setField("RTN_CD" , "0");	
//				outRecord1.setField("RTN_MSG", szMsg);	
//				return outRecord1;
//	    	}   
//	    	
//			JDTORecordSet   outRecSet   = JDTORecordFactory.getInstance().createRecordSet("YD");
//			inRecord = JDTORecordFactory.getInstance().create();
//			inRecord.setField("STL_NO",   sSTL_NO);
////getYdStklyr24
//			/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdStkPosInfo_PIDEV*/
//			intRtnVal = ydStkLyrDao.getYdStklyr(inRecord, outRecSet, 24);
////			if (intRtnVal > 0) {
////				//적치되어 있는 정보 삭제처리
////				outRecSet.first();
////				outRecord = outRecSet.getRecord();
////				//UPDATE 항목 record  생성
////				inRecord = JDTORecordFactory.getInstance().create();
////				
////				//적치단 재료상태가 적치 가능이면 재료 등록
////				//적치단 테이블 업데이트
////				//적치열구분 = 설비ID
////				inRecord.setField("YD_STK_COL_GP"		, ydDaoUtils.paraRecChkNull(outRecord, "YD_STK_COL_GP"));
////				inRecord.setField("YD_STK_BED_NO"		, ydDaoUtils.paraRecChkNull(outRecord, "YD_STK_BED_NO"));
////				inRecord.setField("YD_STK_LYR_NO"		, ydDaoUtils.paraRecChkNull(outRecord, "YD_STK_LYR_NO"));
////				inRecord.setField("YD_STK_LYR_MTL_STAT"	, "E");
////				inRecord.setField("STL_NO"				, "");
////				
////				//업데이트 실행
////				//com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrInStlNo
////		    	intRtnVal = ydStkLyrDao.updYdStklyr(inRecord, 303);
////				if(intRtnVal <= 0){
////					szMsg="적치단 update Error";
////					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
////		   			outRecord1.setField("RTN_CD" , "0");	
////					outRecord1.setField("RTN_MSG", szMsg);	
////					return outRecord1;
////
////				}
////
////
////			} 
//	
//			for(int Loop_i = 1; Loop_i <= outRecSet.size(); Loop_i++) {
//        		
//        		outRecSet.absolute(Loop_i);
//        		outRecord = JDTORecordFactory.getInstance().create();
//        		outRecord.setRecord(outRecSet.getRecord());
//        		 
//        		inRecord = JDTORecordFactory.getInstance().create();
//				//적치단 재료상태가 적치 가능이면 재료 등록
//				//적치단 테이블 업데이트
//				//적치열구분 = 설비ID
//				inRecord.setField("YD_STK_COL_GP", 	    ydDaoUtils.paraRecChkNull(outRecord, "YD_STK_COL_GP"));
//				inRecord.setField("YD_STK_BED_NO", 	    ydDaoUtils.paraRecChkNull(outRecord, "YD_STK_BED_NO"));
//				inRecord.setField("YD_STK_LYR_NO", 	    ydDaoUtils.paraRecChkNull(outRecord, "YD_STK_LYR_NO"));
//				inRecord.setField("YD_STK_LYR_MTL_STAT","E");
//				inRecord.setField("STL_NO", 			"");
//				
//				//업데이트 실행
//				//com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrInStlNo
//		    	intRtnVal = ydStkLyrDao.updYdStklyrNEW(inRecord, 303);
//				if(intRtnVal <= 0){
//					szMsg="적치단 update Error";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//		   			outRecord1.setField("RTN_CD" , "0");	
//					outRecord1.setField("RTN_MSG", szMsg);	
//					return outRecord1;
//
//				}
//        	}
//	    	
//	    	inRecord = JDTORecordFactory.getInstance().create();
//			
//			//적치단 재료상태가 적치 가능이면 재료 등록
//	    	inRecord.setField("YD_STK_COL_GP", 	    sYD_STK_COL_GP);
//	    	inRecord.setField("YD_STK_BED_NO", 	    sYD_STK_BED_NO);
//	    	inRecord.setField("YD_STK_LYR_NO", 	    "001");
//	    	inRecord.setField("YD_STK_LYR_MTL_STAT", "C");
//	    	inRecord.setField("STL_NO", 			sSTL_NO);
//			
//			//업데이트 실행
//			//com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrInStlNo
//	    	intRtnVal = ydStkLyrDao.updYdStklyrNEW(inRecord, 303);
//			if(intRtnVal <= 0){
//				szMsg="적치단 update Error";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	   			outRecord1.setField("RTN_CD" , "0");	
//				outRecord1.setField("RTN_MSG", szMsg);	
//				return outRecord1;
//
//			}
//
//	    	
//	    	
// 	        rsUpStkBed = JDTORecordFactory.getInstance().createRecordSet("");
//			recGetStkBedData = JDTORecordFactory.getInstance().create();
//			recGetStkBedData.setField("YD_STK_COL_GP", sYD_STK_COL_GP );
//			recGetStkBedData.setField("YD_STK_BED_NO", sYD_STK_BED_NO );
//			recGetStkBedData.setField("YD_STK_LYR_NO", "001"  );
//			/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedLyr*/
//			intRtnVal = ydStkBedDao.getYdStkbed(recGetStkBedData, rsUpStkBed, 304);
//			if(intRtnVal <= 0){
//				szMsg="execution failed";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	   			outRecord1.setField("RTN_CD" , "0");	
//				outRecord1.setField("RTN_MSG", szMsg);	
//				return outRecord1;
//
//			}
//			
//			rsUpStkBed.absolute(1);
//			inRecord = JDTORecordFactory.getInstance().create();
//			inRecord.setRecord(rsUpStkBed.getRecord());
//				
//				
//			szMsg="크레인스케줄 권상위치 Update";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//
//			recUpdCrnSchData = JDTORecordFactory.getInstance().create();
//			
//			recUpdCrnSchData.setField("YD_CRN_SCH_ID",  			sYD_CRN_SCH_ID ) ;
////			recUpdCrnSchData.setField("YD_WRK_PROG_STAT",  			"W" ) ;
//			
//			recUpdCrnSchData.setField("YD_UP_WO_LOC",   			recGetStkBedData.getFieldString("YD_STK_COL_GP") + recGetStkBedData.getFieldString("YD_STK_BED_NO")) ;
//			recUpdCrnSchData.setField("YD_UP_WO_LAYER", 			recGetStkBedData.getFieldString("YD_STK_LYR_NO") ) ;
//			recUpdCrnSchData.setField("YD_UP_WO_LOC_XAXIS",  		ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_LYR_XAXIS") ) ;
//			recUpdCrnSchData.setField("YD_UP_WO_XAXIS_GAP_MAX",  	ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_BED_XAXIS_TOL") ) ;
//			recUpdCrnSchData.setField("YD_UP_WO_XAXIS_GAP_MIN",  	ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_BED_XAXIS_TOL") ) ;
//			recUpdCrnSchData.setField("YD_UP_WO_LOC_YAXIS",  		ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_LYR_YAXIS") ) ;
//			recUpdCrnSchData.setField("YD_UP_WO_YAXIS_GAP_MAX",  	ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_BED_YAXIS_TOL") ) ;
//			recUpdCrnSchData.setField("YD_UP_WO_YAXIS_GAP_MIN",  	ydDaoUtils.paraRecChkNull(inRecord, "YD_STK_BED_YAXIS_TOL") ) ;
//			recUpdCrnSchData.setField("YD_UP_WO_ZAXIS_GAP_MAX",  	String.valueOf(YdConstant.C_COIL_CRANE_GAP_Z) ) ;
//			recUpdCrnSchData.setField("YD_UP_WO_ZAXIS_GAP_MIN",  	String.valueOf(YdConstant.C_COIL_CRANE_GAP_Z) ) ;
//
//			/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnsch*/ 
//			intRtnVal = ydCrnSchDao.updYdCrnsch(recUpdCrnSchData, 0);	 	        
//			if(intRtnVal <= 0) {
//    			if(intRtnVal == 0) {
//    				szMsg=" updYdCrnsch data not found";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//    			}else if(intRtnVal == -1) {
//    				szMsg=" updYdCrnsch duplicate data,";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
//    			}else if(intRtnVal == -2) {
//    				szMsg=" updYdCrnsch parameter error";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//    			}else if(intRtnVal == -3){
//    				szMsg=" updYdCrnsch execution failed";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//    			}
//       			outRecord1.setField("RTN_CD" , "0");	
//				outRecord1.setField("RTN_MSG", szMsg);	
//				return outRecord1;
//			}
//		
//		}catch(Exception e){
//	
//			szMsg="Error:" +e.getMessage();
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//   			outRecord1.setField("RTN_CD" , "0");	
//			outRecord1.setField("RTN_MSG", szMsg);	
//			return outRecord1;
//		}
//	
//		
//		szMsg="크레인 작업지시("+szMethodName+") 완료";
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		outRecord1.setField("RTN_CD" 		, "1");	
//		outRecord1.setField("YD_STK_COL_GP"	, sYD_STK_COL_GP);	
//		outRecord1.setField("YD_STK_BED_NO"	, sYD_STK_BED_NO);	
//		outRecord1.setField("YD_WBOOK_ID"	, sYD_WBOOK_ID);	
//		outRecord1.setField("YD_CRN_SCH_ID"	, sYD_CRN_SCH_ID);	
//		outRecord1.setField("RTN_MSG"		, szMsg);	
//		return outRecord1;
//	} //end of Y5ChkWrkProgStatConvlock()
    
    
    
    
    
    
    
    
    
//	/**
//     * 오퍼레이션명 : C열연코일야드L2 권상처리실적등록 (Y5YDL008)
//     * sjh1
//     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//     * @param msgRecord
//     * @return String
//     * @throws JDTOException
//     * @ejb.transaction type="RequiresNew"  
//     */            
//    public String procY5CrnLdWr(JDTORecord msgRecord)throws JDTOException  {
//
//    	YdEqpDao ydEqpDao = new YdEqpDao();
//    	YdStkColDao ydStkColDao = new YdStkColDao();
//    	YdCarSchDao ydCarSchDao = new YdCarSchDao();
//    	YdDelegate ydDelegate = new YdDelegate();
//    	YdCrnSchDao     ydCrnSchDao     = new YdCrnSchDao();
//    	YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
//    	JPlateYdCommDAO ydCommDao = new JPlateYdCommDAO();
//    	ymCommonDAO dao = ymCommonDAO.getInstance();
//    	//업데이트 할 크레인 스케줄 Data 항목 set
//        JDTORecord setCrnschRecord = JDTORecordFactory.getInstance().create();
//        JDTORecord outRecord1  			= JDTORecordFactory.getInstance().create(); 
//        //파라미터 null check 후 받아온 Data
//        JDTORecord getParamRecord = JDTORecordFactory.getInstance().create();
//        
//        JDTORecord recSendMsg = JDTORecordFactory.getInstance().create();
//        
//        JDTORecordSet  rsResult = null;
//        
//        //스케줄Table의 컬럼을 저장하기위해 생성
//        JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
//        //레코드 셋의 레코드값을 받음
//        JDTORecord getRecord 	= JDTORecordFactory.getInstance().create();
//        JDTORecord recInTemp   	= null;
//        JDTORecord recOutTemp  	= null;
//        JDTORecord recFirst   	= null;
//        int intRtnVal 					= 0 ;
//        
//        String szMsg              		= "";
//        String szMethodName       		= "procY5CrnLdWr";
//        
//		String szYD_CRN_SCH_ID = "";
//
//		//설비ID
//		String szYD_EQP_ID     = null;
//		
//		//야드스케줄코드
//		String szYD_SCH_CD     = null;
//		
//		String szYD_UP_WR_LOC  = "";
//        String szSTL_NO        = "";
//        String szYD_UP_CMPL_DT = "";
//        String szWbookId       = "";
//        String szTcarEqpId     = "";
//		//야드To위치결정방법
//		String szYD_TO_LOC_DCSN_MTD = null;
//		//권하지시위치
//		String szYD_DN_WO_LOC 	= null;
//		//야드목표야드구분
//		String szYD_AIM_YD_GP 	= null;
//		String sRTN_CD			= "";
//		String sRTN_MSG			= "";
//		String sTCAR_MOVE_SND	= "N";
//		String szARR_WLOC_CD 	= "";
//		String szHyscoGp        = "";
//		String szSTL_APPEAR_GP  = "";
//		String szCMBN_CARLD_YN  = "";
//		String szISPTOR			= null;
//		String szTAKE_OUT_DT	= null;
//		String szTAKE_OUT_CD	= null;
//		String sHFL_YD_SCH_CD	= "";
//		String sGF_YD_SCH_CD	= "";
//		String szYD_CAR_SCH_ID 	= "";
//		String szTRANS_EQUIPMENT_TYPE= "";
//		String szSANGCHCHK2     = "";
//        String szCAR_KIND       = "";
//        String szYD_CAR_WRK_GP  = "";
//        String szCAR_NO       	= "";
//        String sQueryId  		= "";
//        String szYD_CAR_SCH_ID_TEMP = "";
//        String szDEL_YN_TEMP    = "";
//        String szSTL_NO_TEMP    = "";
//		String szRcvTcCode		= ydUtils.getTcCode(msgRecord);
//        YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
//        PtStlFrtoMoveDao ptStlFrtoMoveDao 	= new PtStlFrtoMoveDao();
//        
//        if(szRcvTcCode==null){
//        	szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
//        	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//        	throw new DAOException("<"+szMethodName+"> CraneLdHdSeEJB " + szMsg);
//        }
//        
//        if(bDebugFlag){
//            szMsg="[DEBUG] 전문수신 : TCCODE=" +szRcvTcCode ;
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//        }
//
//       
//        
//        try{
//        	ydUtils.putLog(szSessionName, szMethodName, "▲▲▲▲▲[C열연 코일야드]권상실적처리 START▲▲▲▲▲", YdConstant.INFO);
//        	
//			szMsg = "[열연 코일야드L2] 권상실적등록 수신";
//			ydUtils.putLogMsg("H", YdConstant.YD_MONITORING_CHANNEL_H, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
//
//			//파라미터 check
//	        intRtnVal = this.Y5ParamCheckCoil(msgRecord, getParamRecord, 0) ;
//	        if(intRtnVal == -1) {
//                szMsg = "파라미터 Check중 Error	: " + intRtnVal;
//                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//                throw new DAOException("<"+szMethodName+"> CraneLdHdSeEJB " + szMsg);
//	        }
//	        
//		    //크레인스케줄ID
//	        szYD_CRN_SCH_ID = getParamRecord.getFieldString("YD_CRN_SCH_ID");
//	        //야드스케줄코드
//	        szYD_SCH_CD 	= getParamRecord.getFieldString("YD_SCH_CD");
//	        //야드구분
//	        String szYD_GP  = szYD_SCH_CD.substring(0,1);
//	        //권상실적위치
//	        szYD_UP_WR_LOC 	= getParamRecord.getFieldString("YD_UP_WR_LOC");
//	        //설비ID(크레인설비ID)
//	        szYD_EQP_ID 	= ydDaoUtils.paraRecChkNull(getParamRecord, "YD_EQP_ID");
//
//	        setCrnschRecord.setField("YD_CRN_SCH_ID",         getParamRecord.getFieldString("YD_CRN_SCH_ID"));
//	        setCrnschRecord.setField("YD_SCH_CD",             getParamRecord.getFieldString("YD_SCH_CD")) ;
//	        setCrnschRecord.setField("YD_UP_WR_LOC",          getParamRecord.getFieldString("YD_UP_WR_LOC")) ;
//	        setCrnschRecord.setField("YD_UP_WR_LAYER",        getParamRecord.getFieldString("YD_UP_WR_LAYER")) ;
//	        setCrnschRecord.setField("YD_UP_WRK_MODE2",       getParamRecord.getFieldString("YD_UP_WRK_MODE2")) ;
//	        setCrnschRecord.setField("YD_UP_CMPL_DT",         YdUtils.getCurDate("yyyyMMddHHmmss")) ;
//	        
//	        
//	        //크레인 스케줄의 업데이트하기위해 스케줄의 변경항목을 정의하고 업데이트한다.
//	        intRtnVal = this.Y5UpdYdCrnschCoil(setCrnschRecord, 0) ;
//	        switch (intRtnVal) {
//	        	case 0	:
//	                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
//	                throw new DAOException("<"+szMethodName+"> CraneLdHdSeEJB " + szMsg);
//	        	case -1	:
//	                szMsg = "dup_val_on_index!!!, ErrorCode:" + intRtnVal;
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
//	                throw new DAOException("<"+szMethodName+"> CraneLdHdSeEJB " + szMsg);
//	        	case -2	:
//	                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
//	                throw new DAOException("<"+szMethodName+"> CraneLdHdSeEJB " + szMsg);
//	        	case -3	:
//	                szMsg = "execution failed!!!, ErrorCode:" + intRtnVal;
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
//	                throw new DAOException("<"+szMethodName+"> CraneLdHdSeEJB " + szMsg);
//	        }
//
//	        
//	        if(setCrnschRecord.getFieldString("YD_CRN_SCH_ID") == null || setCrnschRecord.getFieldString("YD_CRN_SCH_ID").equals("")) {
//                szMsg = "'YD_CRN_SCH_ID' Data Error	: 크레인스케줄ID가 없습니다." ;
//                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//                throw new DAOException("<"+szMethodName+"> CraneLdHdSeEJB " + szMsg);
//	        }
//	        
//	        //대상 데이터 SELECT
//	        /*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdCrnwrkmtlCoilComm*/
//	        intRtnVal = this.Y5GetYdCrnschCoil(setCrnschRecord, getRecSet,400);
//		    switch (intRtnVal) {
//	        	case 0	:
//	                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
//	                throw new DAOException("<"+szMethodName+"> CraneLdHdSeEJB " + szMsg);
//	        	case -2	:
//	                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
//	                throw new DAOException("<"+szMethodName+"> CraneLdHdSeEJB " + szMsg);
//	        }
//
//	        getRecSet.first();
//	        //레코드셋의 사이즈값으로 ErrorCheck
//	        if(getRecSet.size() == 0){
//	            szMsg = "no data fount!!!, ErrorCode:" + intRtnVal;
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	            throw new DAOException("<"+szMethodName+"> CraneLdHdSeEJB " + szMsg);
//	        }
//	        getRecord = getRecSet.getRecord();
//	        
//	        szSTL_NO 		= ydDaoUtils.paraRecChkNull(getRecord, "STL_NO");
//			//제료외형구분 E:소재, Y:제품
//			szSTL_APPEAR_GP = ydDaoUtils.paraRecChkNull(getRecord, "STL_APPEAR_GP");
//	        
//            szMsg = szSTL_NO+ "권상시작함" ;
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	        
//	        szYD_UP_CMPL_DT = ydDaoUtils.paraRecChkNull(getRecord, "YD_UP_CMPL_DT");
//	        
//	        //작업예약ID
//	        szWbookId 				= ydDaoUtils.paraRecChkNull(getRecord, "YD_WBOOK_ID");	//작업예약ID
//	        szYD_TO_LOC_DCSN_MTD 	= ydDaoUtils.paraRecChkNull(getRecord, "YD_TO_LOC_DCSN_MTD"); //야드To위치결정방법
//	        szYD_DN_WO_LOC 			= ydDaoUtils.paraRecChkNull(getRecord, "YD_DN_WO_LOC"); //권하지시위치
//	        szYD_AIM_YD_GP 			= ydDaoUtils.paraRecChkNull(getRecord, "YD_AIM_YD_GP");//야드목표야드구분
//	        szHyscoGp 				= ydDaoUtils.paraRecChkNull(getRecord, "HYSCO_TRANS_GP") ;
//	        
//	        szISPTOR 				= ydDaoUtils.paraRecChkNull(getRecord, "YD_ISPTOR") ;
//	        szTAKE_OUT_DT 				= ydDaoUtils.paraRecChkNull(getRecord, "YD_TAKE_OUT_DT") ;
//	        szTAKE_OUT_CD 				= ydDaoUtils.paraRecChkNull(getRecord, "YD_TAKE_OUT_CD") ;
//	        
//	        //조회한 크레인 스케줄의 작업활성상태가 check를 한다.
//	        if (getRecord.getFieldString("YD_WRK_PROG_STAT").equals("1")
//	                || getRecord.getFieldString("YD_WRK_PROG_STAT").equals("W")
//	                || getRecord.getFieldString("YD_WRK_PROG_STAT").equals("S")
//	                || getRecord.getFieldString("YD_WRK_PROG_STAT").equals("2") // 151203 hun 무인화에서 권상처리안됨으로 추가
//	                ) {
//	            
//
//	        	// 적치단 정보 Clear			(1개의 크레인스케줄에 잡혀있는 크레인작업재료의 정보를 모두 Check!)
//	        	intRtnVal = this.Y5ClearYdStklyrCoil(getRecSet, 0);
//    	        switch (intRtnVal) {
//		        	case 0	:
//		                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
//		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
//		                throw new DAOException("<"+szMethodName+"> CraneLdHdSeEJB " + szMsg);
//		        	case -1	:
//		                szMsg = "dup_val_on_index!!!, ErrorCode:" + intRtnVal;
//		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
//		                throw new DAOException("<"+szMethodName+"> CraneLdHdSeEJB " + szMsg);
//		        	case -2	:
//		                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
//		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
//		                throw new DAOException("<"+szMethodName+"> CraneLdHdSeEJB " + szMsg);
//		        	case -3	:
//		                szMsg = "execution failed!!!, ErrorCode:" + intRtnVal;
//		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
//		                throw new DAOException("<"+szMethodName+"> CraneLdHdSeEJB " + szMsg);
//    	        }
//		        
//		        
//	            //크레인 스케줄의 업데이트하기위해 스케줄의 변경항목을 정의하고 업데이트한다.
//		        setCrnschRecord = JDTORecordFactory.getInstance().create();
//		        setCrnschRecord.setField("YD_CRN_SCH_ID",      getParamRecord.getFieldString("YD_CRN_SCH_ID"));
//		        setCrnschRecord.setField("YD_WRK_PROG_STAT",   getParamRecord.getFieldString("YD_WRK_PROG_STAT"));
//		        setCrnschRecord.setField("YD_EQP_ID",     	   getParamRecord.getFieldString("YD_EQP_ID"));
//		        setCrnschRecord.setField("YD_UP_WR_LOC",       getParamRecord.getFieldString("YD_UP_WR_LOC"));
//		        setCrnschRecord.setField("YD_UP_WR_LAYER",     getParamRecord.getFieldString("YD_UP_WR_LAYER"));
//		        setCrnschRecord.setField("YD_UP_WRK_ACT_GP",   getParamRecord.getFieldString("YD_UP_WRK_ACT_GP"));
//		        setCrnschRecord.setField("YD_UP_WR_XAXIS",     getParamRecord.getFieldString("YD_CRN_XAXIS"));
//		        setCrnschRecord.setField("YD_UP_WR_YAXIS",     getParamRecord.getFieldString("YD_CRN_YAXIS"));
//		        setCrnschRecord.setField("YD_UP_WR_ZAXIS",     getParamRecord.getFieldString("YD_CRN_ZAXIS"));
//		        setCrnschRecord.setField("YD_UP_WRK_MODE2",     getParamRecord.getFieldString("YD_UP_WRK_MODE2"));
//		        
//		        //권상완료일시
//		        setCrnschRecord.setField("YD_UP_CMPL_DT",      YdUtils.getCurDate("yyyyMMddHHmmss"));
//	
//	            intRtnVal = this.Y5UpdYdCrnschCoil(setCrnschRecord, 0);
//	            if(intRtnVal <= 0) {
//			        switch (intRtnVal) {
//			        	case 0	:
//			                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
//			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
//			                throw new DAOException("<"+szMethodName+"> CraneLdHdSeEJB " + szMsg);
//			        	case -1	:
//			                szMsg = "dup_val_on_index!!!, ErrorCode:" + intRtnVal;
//			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
//			                throw new DAOException("<"+szMethodName+"> CraneLdHdSeEJB " + szMsg);
//			        	case -2	:
//			                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
//			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
//			                throw new DAOException("<"+szMethodName+"> CraneLdHdSeEJB " + szMsg);
//			        	case -3	:
//			                szMsg = "execution failed!!!, ErrorCode:" + intRtnVal;
//			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
//			                throw new DAOException("<"+szMethodName+"> CraneLdHdSeEJB " + szMsg);
//			        }
//			        throw new DAOException("<"+szMethodName+"> CraneLdHdSeEJB " + szMsg);
//	            }
//	            
//	          //설비Table의 상태 변경 (권하상태로 변경)
//		        setCrnschRecord = JDTORecordFactory.getInstance().create();
//		        setCrnschRecord.setField("YD_EQP_ID",     	   getParamRecord.getFieldString("YD_EQP_ID"));
//		        setCrnschRecord.setField("YD_EQP_STAT",        getParamRecord.getFieldString("YD_WRK_PROG_STAT"));
//		        
//		        intRtnVal = ydEqpDao.updYdEqp(setCrnschRecord, 0);
//				if(intRtnVal <= 0) {
//	    			if(intRtnVal == 0) {
//	    				szMsg="procY5CrnLdWr updYdEqp : data not found";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//	    				throw new DAOException("<"+szMethodName+"> CraneLdHdSeEJB " + szMsg);
//	    			}else if(intRtnVal == -1) {
//	    				szMsg="procY5CrnLdWr updYdEqp : duplicate data,";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
//	    				throw new DAOException("<"+szMethodName+"> CraneLdHdSeEJB " + szMsg);
//	    			}else if(intRtnVal == -2) {
//	    				szMsg="procY5CrnLdWr updYdEqp : parameter error";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	    				throw new DAOException("<"+szMethodName+"> CraneLdHdSeEJB " + szMsg);
//	    			}else if(intRtnVal == -3){
//	    				szMsg="procY5CrnLdWr updYdEqp : execution failed";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	    				throw new DAOException("<"+szMethodName+"> CraneLdHdSeEJB " + szMsg);
//	    			}
//	    			throw new DAOException("<"+szMethodName+"> CraneLdHdSeEJB " + szMsg);
//	    		}
//				
//			    //CV Handling    
//	            if(ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC").substring(2, 4).equals("CV")){
//	            	
//	            	recInTemp = JDTORecordFactory.getInstance().create();
//	    			recInTemp.setField("MSG_ID"       	, "YDH1L001");						//전문코드YD_EQP_ID
//	    			recInTemp.setField("YD_EQP_ID"    	, ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC").substring(0, 6));							//재료번호
//	    			recInTemp.setField("STL_NO"       	, szSTL_NO);							//재료번호
//	    			recInTemp.setField("YD_STK_BED_NO" 	, ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC").substring(6, 8));							//재료번호
//	    			ydDelegate.sendMsg(recInTemp);
//					szMsg = "열연 압연 L2 전문 송신 완료";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
////대차 재료 Handling  	
//	            }else if(ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC").substring(2, 4).equals("TC")){
//
//	            	szMsg = szSTL_NO + "대차작업처리 시작" ;
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	            	
//	            	//하차작업 SETTING
//	            	intRtnVal = this.Y5SetYdTcarCoil(getRecSet, 0) ;
//	            	
//	            	//setYdTcarCoil에서 Error Message를 보여준다.
//	            	if(intRtnVal <= 0) {
//	            		return YdConstant.RETN_CD_FAILURE;
//	            	}else{
//		        		szMsg = "대차이송재료 삭제 완료" ;
//		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	            	}
//	            	
////	            	szTcarEqpId = ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC").substring(0,1) + "X" + ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC").substring(2,6);
//	            	szTcarEqpId = "JX" + ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC").substring(2,6);
//
//	            	//대차스케줄 호출
//	            	recSendMsg = JDTORecordFactory.getInstance().create();
//	            	recSendMsg.setField("JMS_TC_CD"		, "YDYDJ521");
//	            	recSendMsg.setField("YD_LD_UD_GP"	, "U");
//	            	recSendMsg.setField("YD_EQP_ID"		, szTcarEqpId);
//	            	recSendMsg.setField("YD_WBOOK_ID"	, szWbookId);
//	            	//ydDelegate.simSndMsg(recSendMsg);
//	            	//서버용 메세지 전송 메소드 권상실적처리
//
//	            	outRecord1 =(JDTORecord)ydEjbCon.trx("CoilTransEqpSchSeEJB", "procY5TcarSch", recSendMsg);
//	       			sRTN_CD			= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
//	       			sRTN_MSG		= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
//	       			sTCAR_MOVE_SND	= StringHelper.evl(outRecord1.getFieldString("TCAR_MOVE_SND"), "N");
//	       			String sYD_TCAR_SCH_ID_temp = StringHelper.evl(outRecord1.getFieldString("YD_TCAR_SCH_ID"), "N");;
//	       			// 권GK처리 완료 후 이동지시 실적 날리기 위해
//	       			if(sRTN_CD.equals("0")){
//	       				throw new DAOException("<"+szMethodName+"> CraneLdHdSeEJB " + "대차작업지시 이상(procY5TcarSch)");
//	       			}
//	            	szMsg = "대차작업처리 종료" ;
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg +  sRTN_CD + sTCAR_MOVE_SND + ":" + sYD_TCAR_SCH_ID_temp, YdConstant.DEBUG);
//
//	            }
//
//	            
//	            
//	            
////	            차량 작업 권상 처리
////	            if(!szYD_SCH_CD.substring(2, 8).equals("TR05MM")&&
////            		!szYD_SCH_CD.substring(2, 8).equals("TR06MM")&&
////            		!szYD_SCH_CD.substring(2, 8).equals("TR15MM")&&
////            		!szYD_SCH_CD.substring(2, 8).equals("TR16MM")&&
////            		!szYD_SCH_CD.substring(2, 8).equals("TR07MM")&&
////            		!szYD_SCH_CD.substring(2, 8).equals("TR08MM")&&
////            		!szYD_SCH_CD.substring(2, 8).equals("TR17MM")&&
////            		!szYD_SCH_CD.substring(2, 8).equals("TR18MM")&&
////            		!szYD_SCH_CD.substring(2, 8).equals("TR57MM")&&
////            		!szYD_SCH_CD.substring(2, 8).equals("TR58MM")&&
////            		!szYD_SCH_CD.substring(2, 8).equals("TR67MM")&&
////            		!szYD_SCH_CD.substring(2, 8).equals("TR68MM")
////            		){
//	            if(!("TR".equals(szYD_SCH_CD.substring(2 , 4)) && "MM".equals(szYD_SCH_CD.substring(6 , 8)))){	
//            		//###################################################################################################
//            			if(ydDaoUtils.paraRecChkNull(getParamRecord,"YD_UP_WR_LOC").substring(2, 4).equals("PT")
//            				|| ydDaoUtils.paraRecChkNull(getParamRecord,"YD_UP_WR_LOC").substring(2, 4).equals("TR")){  
//	            	
//		            	
//		            		//권상실적위치로 적치열을 조회하여 차량정지위치 정보를 가져온다.
//			            	recInTemp = JDTORecordFactory.getInstance().create();
//			            	recInTemp.setField("YD_STK_COL_GP", szYD_UP_WR_LOC.substring(0, 6));
//			            	rsResult = JDTORecordFactory.getInstance().createRecordSet("");
//			            	intRtnVal = ydStkColDao.getYdStkcol(recInTemp, rsResult, 0);
//			            	if( intRtnVal <= 0 ) {
//			            		szMsg = "[권상실적처리 - 하차개시]차량정지위치[" + szYD_UP_WR_LOC.substring(0, 6) + "] 정보가 존재하지 않습니다." ;
//				                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//			            	}else{
//			            		
//			            		//조회된 차량정지위치에서 운송장비코드를 가져온다.
//				            	rsResult.first();
//				            	recInTemp = rsResult.getRecord();
//				            	
//				            	//운송장비코드
//				        		String szTRN_EQP_CD 	= ydDaoUtils.paraRecChkNull(recInTemp, "TRN_EQP_CD");
//				        		String szYD_CAR_USE_GP 	= ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_USE_GP");
//				        		szCAR_NO 		= ydDaoUtils.paraRecChkNull(recInTemp, "CAR_NO");
//				        		String szCARD_NO 		= ydDaoUtils.paraRecChkNull(recInTemp, "CARD_NO");
//				        		szMsg = "[권상실적처리]차량정지위치[" + szYD_DN_WO_LOC.substring(0, 6) + "] 정보가 존재하므로 차량사용구분["+szYD_CAR_USE_GP+"], 운송장비코드[" + szTRN_EQP_CD + "], 차량번호["+szCAR_NO+"], 카드번호["+szCARD_NO+"]로 차량스케줄 조회" ;
//				                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//				            	
//				                //운송장비코드로 차량스케줄조회
//				            	recInTemp = JDTORecordFactory.getInstance().create();
//				            	recInTemp.setField("YD_CAR_USE_GP"	, szYD_CAR_USE_GP);
//				    			recInTemp.setField("TRN_EQP_CD"		, szTRN_EQP_CD);
//				    			recInTemp.setField("STL_NO"			, szSTL_NO);
//				    			 
//				    			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
//				    			/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschByStlNoCarID*/
//				    			intRtnVal = ydCarSchDao.getYdCarsch(recInTemp, rsResult, 433);
//				    			if( intRtnVal <= 0 ) {
//				    				szMsg = "[권상실적처리 - 하차개시]차량사용구분["+szYD_CAR_USE_GP+"], 운송장비코드[" + szTRN_EQP_CD + "], 차량번호["+szCAR_NO+"], 카드번호["+szCARD_NO+"]에 해당하는 차량스케줄이 존재하지 않습니다." ;
//					                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//				            	}else{
//				            		/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//							         * 하차개시 전문 송신 처리 - 구내운송
//							         * 업무기준 Desc : 권상실적위치로 적치열에서 운송장비코드를 조회하고 그 코드로 차량스케줄을 조회하여 차량진행상태가
//							         * 				하차검수 or 하차도착이면 하차개시로 차량스케줄을 업데이트하고 전문을 전송한다.
//							         * 기능 추가 : 임춘수
//							         * 일자 : 2009.07.15
//							         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//				            		
//				            		//차량진행상태를 파악하여 하차검수이거나 하차도착일 때만 하차개시 전문을 송신한다.
//				            		rsResult.absolute(1);
//				            		recInTemp = rsResult.getRecord();
//				            		
//				            		szYD_CAR_SCH_ID 			= ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_SCH_ID"); //차량스케줄ID
//				            		String szYD_CAR_PROG_STAT 	= ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_PROG_STAT");//야드차량진행상태
//				            		szYD_CAR_USE_GP 			= ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_USE_GP");//차량사용구분
//				            		szTRANS_EQUIPMENT_TYPE		= ydDaoUtils.paraRecChkNull(recInTemp, "TRANS_EQUIPMENT_TYPE"); //운송장비TYPE P:PDA
//				            		szCAR_KIND                  = ydDaoUtils.paraRecChkNull(recInTemp, "CAR_KIND"); // 차량 종류
//				            		szYD_CAR_WRK_GP             = ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_WRK_GP"); // 차량 작업구분
//				            		//하차검수이거나 하차도착일 때 하차개시 전문 송신
//				            		if( szYD_CAR_PROG_STAT.equals("C") || szYD_CAR_PROG_STAT.equals("B") ) {
//				            			String szYD_CARUD_ST_DT = YdUtils.getCurDate("yyyyMMddHHmmss");
//				            			recInTemp = JDTORecordFactory.getInstance().create();
//				            			recInTemp.setField("YD_CAR_SCH_ID"		, szYD_CAR_SCH_ID);								//차량스케줄ID
//				    	    			recInTemp.setField("YD_CAR_PROG_STAT"	, "D");										//차량진행상태
//				    	    			recInTemp.setField("YD_CARUD_ST_DT"		, szYD_CARUD_ST_DT);								//하차개시일시
//				    	    			intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 0);
//				    	    			if(intRtnVal <= 0) {
//				    						szMsg="[권상실적처리 - 하차개시]차량스케줄에 하차 개시 등록시 Error!! Code : " + intRtnVal;
//				    						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//				    	    			}
//				    	    			
//				    	    			
//				    	    			if( szYD_CAR_USE_GP.equals("L") ) {
//					    	    			//하차작업개시 송신 YDTSJ009
//					    		    		recInTemp = JDTORecordFactory.getInstance().create();
//					    	    			recInTemp.setField("MSG_ID"			, "YDTSJ009");
//					    	    			recInTemp.setField("YD_SCH_CD"		, szYD_SCH_CD);
//					    	    			recInTemp.setField("YD_CAR_SCH_ID"	, szYD_CAR_SCH_ID);
//					    	    			recInTemp.setField("YD_GP"			, szYD_GP);
//					    	    			recInTemp.setField("YD_CARUD_ST_DT"	, szYD_CARUD_ST_DT);
//					    	    			ydDelegate.sendMsg(recInTemp);
//					    	    			
//					    					szMsg="[권상실적처리 - 하차개시]하차작업개시[YDTSJ009] 송신 완료";
//					    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//					    					
//					    					
//					    	    			//코일제품고간이송상하차개시 송신 YDDMR019(제품인 경우에만 )											 
//					    					if(szSTL_APPEAR_GP.equals("Y")){	
//												recInTemp = JDTORecordFactory.getInstance().create();
//												recInTemp.setField("MSG_ID",        "YDDMR019"); 
//												recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
//												recInTemp.setField("YD_GP"			, szYD_GP);
//												ydDelegate.sendMsg(recInTemp);
//											}
//				    	    			}else if( szTRANS_EQUIPMENT_TYPE.equals("P") ) {					//출하PDA
//				    	    			    
//				        	    			//코일제품이송 하차개시 전송PDA
//				        	    			recInTemp = JDTORecordFactory.getInstance().create();
//											recInTemp.setField("MSG_ID",        "YDDMR075"); 
//											recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
//											recInTemp.setField("YD_GP"			, szYD_GP);
//											ydDelegate.sendMsg(recInTemp);
//				            			}
//				            		}
//				            	}
//				    			//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//			            	
//			            	
//			            	
//				            	/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//						         * 하차완료 전문 송신 처리 - 구내운송
//						         * 업무기준 Desc : 마지막 코일을 권상하는 시점에 하차완료 전문을 전송함.
//						         * 기능 추가 : 정종균
//						         * 일자 : 2014.03.27
//						         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//				            	
//				            	recInTemp 	= JDTORecordFactory.getInstance().create();
//				    	    	rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
//				    	    	recInTemp.setField("YD_WBOOK_ID", szWbookId);
//				    	    	
//				            	//하차완료유무 체크 ------------------------------------------------------------------
//				    			/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarSangchendChk2*/
//				    			intRtnVal = ydCrnSchDao.getYdCrnsch(recInTemp, rsResult, 508);
//				    	    	if(intRtnVal <= 0) {
//				    				szMsg="작업예약재료로 크레인 스케줄을 조회 중 Error";
//				    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	    				
//				    	    	}else{		    	    	
//					    	    	rsResult.first();
//					    	    	recFirst = JDTORecordFactory.getInstance().create();
//					    	    	recFirst.setRecord(rsResult.getRecord());
//					    	    	String szSANGCHCHK 			= ydDaoUtils.paraRecChkNull(recFirst, "SANGCHCHK");
//					    	    	szYD_CAR_SCH_ID 			= ydDaoUtils.paraRecChkNull(recFirst, "YD_CAR_SCH_ID"); //차량스케줄ID
//					    	    	String szYD_EQP_WRK_STAT	= ydDaoUtils.paraRecChkNull(recFirst, "YD_EQP_WRK_STAT");
//					    	    	szMsg = "[권상실적처리 - 하차완료]차량정지위치[" + szYD_DN_WO_LOC.substring(0, 6) + "] 정보가 존재하므로 차량사용구분["+szYD_EQP_WRK_STAT+"], 차량스케줄번호[" + szYD_CAR_SCH_ID + "] 로 차량스케줄 조회" ;
//					                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//					                //----------------------------------------------------------------------------------
//					    			
//				    			
//					            	//마지막 크레인스케줄 ID와 전문항목의 크레인 스케줄 ID가 동일한지 비교 
//					    			if("Y".equals(szSANGCHCHK)){
//				    				
//						    			//동일하면 차량스케줄에 하차완료일시 등록
//						    			recInTemp = JDTORecordFactory.getInstance().create();
//						    			recInTemp.setField("YD_EQP_WRK_STAT", "U"); //공차
//						    			recInTemp.setField("YD_CAR_PROG_STAT", "E"); //하차완료
//						    			recInTemp.setField("DEL_YN",           "N");
//						    			recInTemp.setField("YD_CARUD_WRK_BOOK_ID", szWbookId);
//						    			intRtnVal = ydCarSchDao.updYdCarschYdCarWrkBookId(recInTemp, 3);
//						    			if(intRtnVal <= 0) {
//											szMsg="차량스케줄에 하차 개시 등록시 Error!! Code : " + intRtnVal;
//											ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//						    			}
//						    			
//						    			//반품,회송,부분하차인경우 여기서 차량출발처리를 한다.
//						    			if("J".equals(szYD_SCH_CD.substring(0,1))&& "PT".equals(szYD_SCH_CD.substring(2,4))
//						    				&& "4LM".equals(szYD_SCH_CD.substring(5,8))	
//						    			) {
//						    				
//						    				szMsg= "151118 hun TTcar 반품,회송,부분하차 자동차량출발 제외, 이전 차량 상차 스케줄로 원복 start YD_CAR_WRK_GP"+szYD_CAR_WRK_GP;
//											ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//						    				
//											if("TT".equals(szCAR_KIND) && "3".equals(szYD_CAR_WRK_GP)){
//						    					szMsg= "151118 hun TTcar 반품,회송,부분하차 자동차량출발 제외, 이전 차량 상차 스케줄로 원복 in";
//												ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//												
//												//151119 HUN 반품,회송,부분하차인경우 이전 차량 상차 스케줄로 원복
//									        	
////									        	01. 현재 차량 스케줄 삭제 처리, 이전차량 스케줄 DEL_YN = Y 로 변경 start
//												rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
//												recInTemp = JDTORecordFactory.getInstance().create();
//												recInTemp.setField("CAR_NO",           szCAR_NO);
//												
//										        sQueryId = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarSchReverseYN";
//										        intRtnVal = ydCommDao.select(recInTemp, rsResult, sQueryId);
//										        
//										        if(rsResult.size()>0){
//										        	for(int Loop_i = 1; Loop_i <= rsResult.size() ; Loop_i++) {
//											        	rsResult.absolute(Loop_i);
//											        	recOutTemp = JDTORecordFactory.getInstance().create();
//												        recOutTemp = rsResult.getRecord();
//												        
//												        szDEL_YN_TEMP = ydDaoUtils.paraRecChkNull(recOutTemp,"DEL_YN_TO");
//														szYD_CAR_SCH_ID_TEMP = ydDaoUtils.paraRecChkNull(recOutTemp,"YD_CAR_SCH_ID");
//														
//														sQueryId = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.updYdCarSchReverseYN";
//														intRtnVal = dao.updateData(sQueryId,new Object[]{ szDEL_YN_TEMP, szYD_CAR_SCH_ID_TEMP});
//											        	if (intRtnVal < 1){
//											        		ydUtils.putLog(szMethodName, szMethodName, "차량스케줄 Reverse 저장실패 YD_CAR_SCH_ID"+szYD_CAR_SCH_ID_TEMP, YdConstant.DEBUG);
//														}else{
//															ydUtils.putLog(szMethodName, szMethodName, "차량스케줄 Reverse 저장성공 YD_CAR_SCH_ID="+szYD_CAR_SCH_ID_TEMP, YdConstant.DEBUG);
//														}
//													
//										        	}
//										        }
////									        	01. 현재 차량 스케줄 삭제 처리, 이전차량 스케줄 DEL_YN = Y 로 변경 end										        
//									        	
//									        	
////									        	02. 이전 상차 완료 스케줄중 부분하차 코일 제외하고 나머지 상차 완료 원복 start
//									        	
//										        rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
//												recInTemp = JDTORecordFactory.getInstance().create();
//												recInTemp.setField("CAR_NO",           szCAR_NO);
//												recInTemp.setField("YD_CAR_SCH_ID",           szYD_CAR_SCH_ID);
//										        sQueryId = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarSchReverseYNMTL";
//										        intRtnVal = ydCommDao.select(recInTemp, rsResult, sQueryId);
//										        
//										        if(rsResult.size()>0){
//										        	for(int Loop_i = 1; Loop_i <= rsResult.size() ; Loop_i++) {
//											        	rsResult.absolute(Loop_i);
//											        	recOutTemp = JDTORecordFactory.getInstance().create();
//												        recOutTemp = rsResult.getRecord();
//												        
//												        szDEL_YN_TEMP = ydDaoUtils.paraRecChkNull(recOutTemp,"DEL_YN_TO");
//														szYD_CAR_SCH_ID_TEMP = ydDaoUtils.paraRecChkNull(recOutTemp,"YD_CAR_SCH_ID");
//														szSTL_NO_TEMP = ydDaoUtils.paraRecChkNull(recOutTemp,"STL_NO");
//														
//														sQueryId = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.updYdCarSchReverseYNMTL";
//														intRtnVal = dao.updateData(sQueryId,new Object[]{ szDEL_YN_TEMP, szYD_CAR_SCH_ID_TEMP, szSTL_NO_TEMP});
//											        	if (intRtnVal < 1){
//											        		ydUtils.putLog(szMethodName, szMethodName, "차량스케줄 재료 Reverse 저장실패 STL_NO ="+szSTL_NO_TEMP, YdConstant.DEBUG);
//														}else{
//															ydUtils.putLog(szMethodName, szMethodName, "차량스케줄 재료 Reverse 저장성공 STL_NO"+szSTL_NO_TEMP, YdConstant.DEBUG);
//														}
//													
//										        	}
//										        }
//												
////									        	02. 이전 상차 완료 스케줄중 부분하차 코일 제외하고 나머지 상차 완료 원복 end												
//												
//						    				}
////											else{
////						    					 
////								    			recInTemp  = JDTORecordFactory.getInstance().create();
////								    			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
////												recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
////												intRtnVal = ydCarSchDao.getYdCarsch(recInTemp, rsResult, 0);
////												if(intRtnVal > 0){
////											 
////													rsResult.first();
////													JDTORecord recResult = rsResult.getRecord();
////													
////													recInTemp = JDTORecordFactory.getInstance().create();
////													recInTemp.setField("TC_CODE",        		"DMYDR040");									//전문코드
////													recInTemp.setField("CARD_NO", 				ydDaoUtils.paraRecChkNull(recResult,"CARD_NO"));
////													recInTemp.setField("CAR_NO", 				szCAR_NO);			
////													recInTemp.setField("SPOS_WLOC_CD", 			ydDaoUtils.paraRecChkNull(recResult,"ARR_WLOC_CD"));
////													recInTemp.setField("SPOS_YD_PNT_CD", 		ydDaoUtils.paraRecChkNull(recResult,"YD_PNT_CD3"));
////													recInTemp.setField("TRANS_ORD_DT", 			ydDaoUtils.paraRecChkNull(recResult,"TRANS_ORD_DATE"));
////													recInTemp.setField("TRANS_ORD_SEQNO", 		ydDaoUtils.paraRecChkNull(recResult,"TRANS_ORD_SEQNO"));
////					  
////													szMsg= "차량번호[" + szCAR_NO + "]는 자동차량출발";
////													ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
////													EJBConnector ejbConn = new EJBConnector("default", "CarMvHdFaEJB", this);
////													ejbConn.trx("rcvCoilGdsdistCarLevWr", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
////							    			     
////												}
////						    				}
//						    				
//						    			}
//				 
//						    			//하차작업완료 송신 YDTSJ010
//						    			if( szYD_EQP_WRK_STAT.equals("L") ) {	
//						    				
//						    				//출하 PDA하차 작업 인경우
//							    			 if( szTRANS_EQUIPMENT_TYPE.equals("P") ) {					//출하PDA
//							    				 
//						    	    			//코일제품이송 하차완료 전송PDA
//						    	    			recInTemp = JDTORecordFactory.getInstance().create();
//												recInTemp.setField("MSG_ID",        "YDDMR076"); 
//												recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
//												recInTemp.setField("YD_GP"			, szYD_GP);
//												ydDelegate.sendMsg(recInTemp);
//						        			}else{
//							    			
//						        				//구내운송 
//							    				recInTemp = JDTORecordFactory.getInstance().create();
//								    			recInTemp.setField("MSG_ID",        "YDTSJ010");
//								    			recInTemp.setField("YD_SCH_CD",     szYD_SCH_CD);
//								    			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
//								    			recInTemp.setField("YD_GP",         szYD_GP);
//								    			ydDelegate.sendMsg(recInTemp);
//								    			
//								    			szMsg="[권상실적처리 - 하차완료]하차작업완료[YDTSJ010] 송신 완료";
//						    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//												
//												
//												//코일제품고간이송상하차완료 송신 YDDMR021(제품인 경우에만 )
//												if(szSTL_APPEAR_GP.equals("Y")){
//													
//													recInTemp = JDTORecordFactory.getInstance().create();
//													recInTemp.setField("MSG_ID",        "YDDMR021"); // 출하 - 상차완료 YDDMR021
//													recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
//													ydDelegate.sendMsg(recInTemp);
//													
//													szMsg="[권상실적처리 - 하차완료]하차작업완료[YDDMR021] 송신 완료";
//							    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//												}
//						        			} 
//						    			}
//					    			}			        
//				    	    	}
//				    	    	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//			            	}	 
//	            	
//			            	//차량재료상태 Y 처리
//		            		intRtnVal = this.Y5SetYdCarCoil(getRecSet, 0) ; 				           
//			            	if(intRtnVal <= 0) {
//			            		return YdConstant.RETN_CD_FAILURE;
//			            	}else{
//				        		szMsg = "차량이송재료 삭제 완료" ;
//				                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//			            	}
//			            	
//			            	//151118 HUN 반품,회송,부분하차인경우 검수테이블 삭제 처리
//			    			if("J".equals(szYD_SCH_CD.substring(0,1))&& "PT".equals(szYD_SCH_CD.substring(2,4))
//			    				&& "4LM".equals(szYD_SCH_CD.substring(5,8))	
//			    			) {
//			    				sQueryId = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.updCarExaminationDel";
//								intRtnVal = dao.updateData(sQueryId,new Object[]{ szSTL_NO, szCAR_NO});
//					        	if (intRtnVal < 1){
//					        		ydUtils.putLog(szMethodName, szMethodName, "검수테이블 삭제 처리 저장실패", YdConstant.DEBUG);
//								}else{
//									ydUtils.putLog(szMethodName, szMethodName, "검수테이블 삭제 처리 저장성공", YdConstant.DEBUG);
//								}
//					        	
//			    			}
//	            		}
//            			//###################################################################################################
//	            	} 
//	            /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//	            
//	                        
//	            
//	            
// 	            
//	            szMsg = "[권상실적처리] 상차개시 진입 전 - 권하지시위치[" + szYD_DN_WO_LOC + "], " + "설비구분[" + szYD_DN_WO_LOC.substring(2, 4) + "]";
//                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//                
//                
//                /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//		         * 상차개시 전문 송신 처리 - 구내운송, 출하관리
//		         * 업무기준 Desc : 권하지시위치로 적치열에서 운송장비코드를 조회하고 그 코드로 차량스케줄을 조회하여 차량진행상태가
//		         * 				상차검수 or 상차도착이면 상차개시로 차량스케줄을 업데이트하고 전문을 전송한다.
//		         * 기능 추가 : 임춘수
//		         * 일자 : 2009.07.15
//		         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//	            //권하지시위치가 차상인 경우 상차개시 전문 송신 처리
////                if(!szYD_SCH_CD.substring(2, 8).equals("TR05MM")&&
////            		!szYD_SCH_CD.substring(2, 8).equals("TR06MM")&&
////            		!szYD_SCH_CD.substring(2, 8).equals("TR15MM")&&
////            		!szYD_SCH_CD.substring(2, 8).equals("TR16MM")&&
////            		!szYD_SCH_CD.substring(2, 8).equals("TR07MM")&&
////            		!szYD_SCH_CD.substring(2, 8).equals("TR08MM")&&
////            		!szYD_SCH_CD.substring(2, 8).equals("TR17MM")&&
////            		!szYD_SCH_CD.substring(2, 8).equals("TR18MM")&&
////            		!szYD_SCH_CD.substring(2, 8).equals("TR57MM")&&
////            		!szYD_SCH_CD.substring(2, 8).equals("TR58MM")&&
////            		!szYD_SCH_CD.substring(2, 8).equals("TR67MM")&&
////            		!szYD_SCH_CD.substring(2, 8).equals("TR68MM")
////            		){
//                if(!("TR".equals(szYD_SCH_CD.substring(2 , 4)) && "MM".equals(szYD_SCH_CD.substring(6 , 8)))){		
//            	//###################################################################################################
//	            if( szYD_DN_WO_LOC.substring(2, 4).equals("PT") || szYD_DN_WO_LOC.substring(2, 4).equals("TR") ) {
//	            	//권하지시위치로 적치열을 조회하여 차량정지위치 정보를 가져온다.
//	            	recInTemp = JDTORecordFactory.getInstance().create();
//	            	recInTemp.setField("YD_STK_COL_GP", szYD_DN_WO_LOC.substring(0, 6));
//	            	rsResult = JDTORecordFactory.getInstance().createRecordSet("");
//	            	intRtnVal = ydStkColDao.getYdStkcol(recInTemp, rsResult, 0);
//	            	if( intRtnVal <= 0 ) {
//	            		szMsg = "[권상실적처리 - 상차개시]차량정지위치[" + szYD_DN_WO_LOC.substring(0, 6) + "] 정보가 존재하지 않습니다." ;
//		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	            	}else{
//	            		//조회된 차량정지위치에서 운송장비코드를 가져온다.
//		            	rsResult.first();
//		            	recInTemp = rsResult.getRecord();
//		            	//운송장비코드
//		        		String  szTRN_EQP_CD 	= ydDaoUtils.paraRecChkNull(recInTemp, "TRN_EQP_CD");
//		        		String szYD_CAR_USE_GP 	= ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_USE_GP");
//		        		szCAR_NO 		= ydDaoUtils.paraRecChkNull(recInTemp, "CAR_NO");
//		        		String szCARD_NO 		= ydDaoUtils.paraRecChkNull(recInTemp, "CARD_NO");
//		        	//	String szARR_WLOC_CD	= ydDaoUtils.paraRecChkNull(recInTemp, "WLOC_CD"); // 착지개소코드 
//		        		szMsg = "[권상실적처리 - 상차개시]차량정지위치[" + szYD_DN_WO_LOC.substring(0, 6) + "] 정보가 존재하므로 차량사용구분["+szYD_CAR_USE_GP+"], 운송장비코드[" + szTRN_EQP_CD + "], 차량번호["+szCAR_NO+"], 카드번호["+szCARD_NO+"]로 차량스케줄 조회" ;
//		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		            	//운송장비코드로 차량스케줄조회
//		            	recInTemp = JDTORecordFactory.getInstance().create();
//		    			recInTemp.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
//		    			recInTemp.setField("TRN_EQP_CD", szTRN_EQP_CD);
//		    			recInTemp.setField("STL_NO", szSTL_NO); 
//		    			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
//		    			/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschByStlNoCarID*/
//		    			intRtnVal = ydCarSchDao.getYdCarsch(recInTemp, rsResult, 433);
//		    			if( intRtnVal <= 0 ) {
//		    				szMsg = "[권상실적처리 - 상차개시]차량사용구분["+szYD_CAR_USE_GP+"], 운송장비코드[" + szTRN_EQP_CD + "], 차량번호["+szCAR_NO+"], 카드번호["+szCARD_NO+"]에 해당하는 차량스케줄이 존재하지 않습니다." ;
//			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//		            	}else{
//		            		//차량진행상태를 파악하여 상차검수이거나 상차도착일 때만 상차개시 전문을 송신한다.
//		            		rsResult.absolute(1);
//		            		recInTemp = rsResult.getRecord();
//		            		
//		            		szYD_CAR_SCH_ID 			= ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_SCH_ID"); //차량스케줄ID
//		            		String szYD_CAR_PROG_STAT 	= ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_PROG_STAT"); //야드차량진행상태
//		            		szYD_CAR_USE_GP 			= ydDaoUtils.paraRecChkNull(recInTemp, "YD_CAR_USE_GP");//차량사용구분
//		            		szCMBN_CARLD_YN				= ydDaoUtils.paraRecChkNull(recInTemp, "CMBN_CARLD_YN"); //복수창고 마지막 창고,동 인경우
//		            		szTRANS_EQUIPMENT_TYPE		= ydDaoUtils.paraRecChkNull(recInTemp, "TRANS_EQUIPMENT_TYPE"); //PDA
//		            		
//		            		szMsg = "[권상실적처리 - 상차개시]차량스케줄 조회 후 차량스케줄ID[" + szYD_CAR_SCH_ID + "], 야드차량진행상태[" + szYD_CAR_PROG_STAT + "], 차량사용구분[" + szYD_CAR_USE_GP + "]" ;
//			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//			                
//		            		//상차검수이거나 상차도착일 때 상차개시 전문 송신
//		            		if( (szYD_CAR_PROG_STAT.equals("3") || szYD_CAR_PROG_STAT.equals("2")) && !"E".equals(szCMBN_CARLD_YN) ) {
//		
//		            			
//		            			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
//		            			recInTemp = JDTORecordFactory.getInstance().create();
//		            			recInTemp.setField("STL_NO"	, szSTL_NO);
//		            			intRtnVal = ydWrkbookDao.getYdWrkbook(recInTemp, outRecSet, 500);
//		            			
//		            			if( intRtnVal > 0 ) {
//		            				outRecSet.first();
//		            				recInTemp = outRecSet.getRecord();
//		            				szARR_WLOC_CD = ydDaoUtils.paraRecChkNull(recInTemp, "ARR_WLOC_CD");
//		            			}
//		            			
//		            			//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//		            			
//		            			
//	        	    			 
//		            			if( szYD_CAR_USE_GP.equals("G") ){			//출하차량
//		            				
//		            				//야드구분에 따른 개소코드 반환
//			            			//String szARR_WLOC_CD = YdCommonUtils.getWlocCd(szYD_AIM_YD_GP);
//		            				//차량스케줄 업데이트 - 상차개시
//		            				recInTemp = JDTORecordFactory.getInstance().create();
//		            				recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);								//차량스케줄ID
//		            				recInTemp.setField("YD_CAR_PROG_STAT", "4");										//차량진행상태
//		        	    			recInTemp.setField("YD_EQP_WRK_STAT", "U");											//작업상태
//		        	    			recInTemp.setField("YD_CARLD_WRK_BOOK_ID", szWbookId);								//작업예약ID
//		        	    			recInTemp.setField("YD_CARLD_ST_DT", YdUtils.getCurDate("yyyyMMddHHmmss"));			//상차개시일시
//		        	    			recInTemp.setField("MODIFIER",	"YDSYSTEM");										//수정자
//		        	    			if( szYD_CAR_USE_GP.equals("L") ) {					//구내운송
//		        	    				recInTemp.setField("ARR_WLOC_CD", szARR_WLOC_CD);								//착지개소코드
//		        	    			}
//		        	    			intRtnVal = ydCarSchDao.updYdCarsch(recInTemp, 0);
//		        	    			
//		        	    			if(intRtnVal <= 0) {
//		        						szMsg="[권상실적처리]차량스케줄에 상차개시일시 등록시 Error!! Code : " + intRtnVal;
//		        						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		        	    			}
//		        	    			
//		        	    			if( !szTRANS_EQUIPMENT_TYPE.equals("P") ) {					//출하PDA
//		        	    			
//			            				recInTemp = JDTORecordFactory.getInstance().create();
//			            				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
//			    	    				recInTemp.setField("STL_NO",   szSTL_NO);
//			    	    				intRtnVal = ydCrnSchDao.getYdCrnsch(recInTemp, rsResult, 504);
//			    	    		    	if(intRtnVal > 0) {
//			    	    					szMsg="임가공대상제가 존재 함.";
//			    	    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//			    	    					//##############################################################################################
//			    	    	    			//임가공 출하차량  상차개시 인 경우 (제품 인 경우)
//			    	    	    			//##############################################################################################
//			    	    	    			//상차작업개시 송신 YDDMR020 (임가공이송하차개시)
//			    		    		    	recInTemp = JDTORecordFactory.getInstance().create();
//			    							recInTemp.setField("MSG_ID",        "YDDMR020");
//			    			    			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
//			    			    			recInTemp.setField("YD_GP",         szYD_GP);		    			    			
//			    			    			ydDelegate.sendMsg(recInTemp);
//			    	    		    	}else {
//					    	    			//##############################################################################################
//					    	    			//출하차량  상차개시 인 경우 (제품 인 경우)
//					    	    			//##############################################################################################
//				            				//상차작업개시 송신 YDDMR009 (코일출하상차개시)
//				    		    			recInTemp.setField("MSG_ID",        "YDDMR007");
//				    		    			recInTemp.setField("YD_SCH_CD",     szYD_SCH_CD);
//					    	    			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
//					    	    			recInTemp.setField("YD_GP",         szYD_GP);
//					    	    			ydDelegate.sendMsg(recInTemp);
//			    	    		    	}
//		    	    		    	
//			            			}else  {					//출하PDA
//			    	    		    	    
//				        	    			//코일제품이송 상차개시 전송PDA
//				        	    			recInTemp = JDTORecordFactory.getInstance().create();
//											recInTemp.setField("MSG_ID",        "YDDMR071"); 
//											recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
//											recInTemp.setField("YD_GP"			, szYD_GP);
//											ydDelegate.sendMsg(recInTemp);
//				            			}
//			    	    			 
//		            			}
//		            			
//		    	    			szMsg="[권상실적처리]상차작업개시 송신 완료";
//		    					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		            		}
//		            		
//		            		
//		            		if( szYD_CAR_USE_GP.equals("G") ){			//출하차량
//			            		recInTemp 	= JDTORecordFactory.getInstance().create();
//			        	    	rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
//			        	    	recInTemp.setField("YD_WBOOK_ID", szWbookId);
//			        	 
//			        			
//			        			//상차완료유무 체크 ------------------------------------------------------------------
//			        			/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarSangchendChk*/
//			        			intRtnVal = ydCrnSchDao.getYdCrnsch(recInTemp, rsResult, 506);
//			        	    	if(intRtnVal <= 0) {
//			        				szMsg="작업예약재료로 크레인 스케줄을 조회 중 Error2";
//			        				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			        				throw new DAOException("<procY5CarWrkStatCtr> getYdCrnsch" + szMsg);
//			        	    	}		
//			        	    	
//			        	    	rsResult.first();
//			        	    	recFirst = JDTORecordFactory.getInstance().create();
//			        	    	recFirst.setRecord(rsResult.getRecord());
//			        	    	szSANGCHCHK2 = ydDaoUtils.paraRecChkNull(recFirst, "SANGCHCHK");
//			        	    	
//			        	    	szMsg = "▼▼▼▼▼플래그 상태 : 전문 스케줄id : " + szYD_CRN_SCH_ID+" 상차완료유무 : " + szSANGCHCHK2+" ▼▼▼▼▼";
//			        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//			        	    	//----------------------------------------------------------------------------------
//			            		
//			            		
//			        			if( !szTRANS_EQUIPMENT_TYPE.equals("P") ) {
//			            		
//				            		recInTemp 	= JDTORecordFactory.getInstance().create();
//					    			rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
//				    				recInTemp.setField("STL_NO",         szSTL_NO);
//				    				/*yd.facilitystatus.facilityinquiry.CraneSchDAO.getYmPoFrtoInfo_PIDEV*/
//				    				intRtnVal = ydCrnSchDao.getYdCrnsch(recInTemp, rsResult, 504);
//				    		    	if(intRtnVal <= 0) {
//										szMsg="일품 상차실적 송신 YDDMR011 (코일일품출하상차실적 송신)";
//										ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//							    		recInTemp = JDTORecordFactory.getInstance().create();
//						    			recInTemp.setField("MSG_ID",        "YDDMR011");					
//										recInTemp.setField("YD_SCH_CD",     szYD_SCH_CD);
//										recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
//										recInTemp.setField("YD_GP",         szYD_GP);
//										recInTemp.setField("STL_NO",        szSTL_NO);
//		
//										if("Y".equals(szSANGCHCHK2)){ // 상차완료인 저장품이 ALL일때 처리
//											recInTemp.setField("GOODS_EA","*");
//					                    }else{
//					                    	recInTemp.setField("GOODS_EA","1");
//					                    }
//										
//										ydDelegate.sendMsg(recInTemp);
//				    		    	}
//			        			}
//		            		}
//		            	}
//	            	}
//	            }
//            	//###################################################################################################
//                }
//	            /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//	            
//	            
//		        
//////권상 실적 위치 출측(Line-Off)
////C증설                
//	            szYD_UP_WR_LOC = ydDaoUtils.paraRecChkNull(getRecord, "YD_UP_WR_LOC");
//				szMsg = "+++++ Line-Off에 대핸 요구 처리 : 권상 실적 위치 YD_UP_WR_LOC(" + szYD_UP_WR_LOC + ") +++++";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	            
//	            if((szYD_UP_WR_LOC.startsWith("HBFE05")) ||
//	               (szYD_UP_WR_LOC.startsWith("HBFD05")) || 
//	 	           (szYD_UP_WR_LOC.startsWith("HBKD04")) ||
//	 	           (szYD_UP_WR_LOC.startsWith("HAKD05")) ||
//	               (szYD_UP_WR_LOC.startsWith("HCFD04")) || 
//	               (szYD_UP_WR_LOC.startsWith("HCKD03")) ||
//	               (szYD_UP_WR_LOC.startsWith("HDFE03")) ||
//	               (szYD_UP_WR_LOC.startsWith("HEDD01")) ||  
//	 	           (szYD_UP_WR_LOC.startsWith("HFFE02")) ||
//	               (szYD_UP_WR_LOC.startsWith("HGFD01")) || 
//	               (szYD_UP_WR_LOC.startsWith("HDCR01")) || //크래들롤
//	               (szYD_UP_WR_LOC.startsWith("HFCR01")) || //크래들롤
//	               (szYD_UP_WR_LOC.startsWith("HHKD01"))           )   {
//	            	if(
//	            	   ( szYD_SCH_CD.equals("HBFE04LM")) ||		
//	            	   ( szYD_SCH_CD.equals("JATC01MM")) ||
//	            	   ( szYD_SCH_CD.equals("JATC02MM")) ||
//	            	   
// 	            	   ( szYD_SCH_CD.equals("HBFD01LM")) ||
//	            	   ( szYD_SCH_CD.equals("HBFD02LM")) ||	            	   
//	            	   ( szYD_SCH_CD.equals("HBFD04LM")) ||
//	            	   ( szYD_SCH_CD.equals("HBKD01LM")) ||
//	            	   ( szYD_SCH_CD.equals("HBKD02LM")) ||    
//	            	   ( szYD_SCH_CD.equals("JBFD01LM")) ||
//	            	   ( szYD_SCH_CD.equals("JBKD01LM")) ||
//	            	   ( szYD_SCH_CD.equals("HBKD05LM")) ||
//	            	   ( szYD_SCH_CD.equals("JBTC01MM")) ||
//	            	   ( szYD_SCH_CD.equals("JBTC02MM")) ||
//	            	   
//	            	   ( szYD_SCH_CD.equals("HAKD01LM")) ||
//	            	   ( szYD_SCH_CD.equals("HAKD02LM")) ||    
//	            	   ( szYD_SCH_CD.equals("JAKD01LM")) ||
//	            	   ( szYD_SCH_CD.equals("HAKD05LM")) ||
//	            	   	
//	            	   ( szYD_SCH_CD.equals("HCFD01LM")) ||
//	            	   ( szYD_SCH_CD.equals("HCFD02LM")) ||	            	   
//	            	   ( szYD_SCH_CD.equals("HCFD04LM")) ||
//	            	   ( szYD_SCH_CD.equals("HCKD01LM")) ||
//	            	   ( szYD_SCH_CD.equals("HCKD02LM")) ||
//	            	   ( szYD_SCH_CD.equals("JCFD01LM")) ||
//	            	   ( szYD_SCH_CD.equals("JCKD01LM")) ||
//	            	   ( szYD_SCH_CD.equals("HCKD05LM")) ||
//	            	   ( szYD_SCH_CD.equals("JCTC01MM")) ||
//	            	   ( szYD_SCH_CD.equals("JCTC02MM")) ||
//
//	            	   ( szYD_SCH_CD.equals("HDFE04LM")) ||
//	            	   ( szYD_SCH_CD.equals("JDFD01LM")) ||
//	            	   ( szYD_SCH_CD.equals("JDTC01MM")) ||
//	            	   ( szYD_SCH_CD.equals("JDTC02MM")) ||
//	            	   ( szYD_SCH_CD.startsWith("HDYD")) ||
//	            	   
//	            	   ( szYD_SCH_CD.equals("HEDD01LM")) ||
//	            	   ( szYD_SCH_CD.equals("HEDD02LM")) ||
//	            	   ( szYD_SCH_CD.equals("JEDD01LM")) ||
//	            	   ( szYD_SCH_CD.equals("HEDD05LM")) ||
//	            	   
//	            	   ( szYD_SCH_CD.equals("HFFE04LM")) ||
//	            	   ( szYD_SCH_CD.equals("JFFD01LM")) ||
//	            	   ( szYD_SCH_CD.equals("JFTC01MM")) ||
//	            	   ( szYD_SCH_CD.equals("JFTC02MM")) ||
//	            	   ( szYD_SCH_CD.startsWith("HFYD")) ||
//	            	   
//	            	   ( szYD_SCH_CD.equals("JGFD01LM")) ||
//	            	   ( szYD_SCH_CD.equals("HGFD01LM")) ||
//	            	   ( szYD_SCH_CD.equals("HGFD02LM")) ||	            	   
//	            	   ( szYD_SCH_CD.equals("HGFD04LM")) ||
//
//	            	   ( szYD_SCH_CD.equals("JHKD01LM")) ||
//	            	   ( szYD_SCH_CD.equals("HHKD01LM")) ||
//	            	   ( szYD_SCH_CD.equals("HHKD05LM")) ||
//	            	   ( szYD_SCH_CD.equals("HHKD02LM")) ||
////	            	   151224 hun 지포장 스케줄 추가
//	            	    (szYD_SCH_CD.equals("HBFE06LM"))||
//		   			    (szYD_SCH_CD.equals("HDFE06LM"))||
//		   			    (szYD_SCH_CD.equals("HFFE06LM"))||
//		   			    
//	            	    (szYD_SCH_CD.equals("HAKD06LM"))||
//		   			    (szYD_SCH_CD.equals("HBKD06LM"))||
//		   			    (szYD_SCH_CD.equals("HBFD06LM"))||
//		   			    (szYD_SCH_CD.equals("HCKD06LM"))||
//		   			    (szYD_SCH_CD.equals("HCFD06LM"))||
//		   			    (szYD_SCH_CD.equals("HDFD06LM"))||
//		   			    (szYD_SCH_CD.equals("HEDD06LM"))||
//		   			    (szYD_SCH_CD.equals("HFFD06LM"))||
//		   			    (szYD_SCH_CD.equals("HGFD06LM"))||
//		   			    (szYD_SCH_CD.equals("HHKD06LM"))
//
//	            	   ){
//	            	
//		            	recInTemp = JDTORecordFactory.getInstance().create();
//		    			recInTemp.setField("MSG_ID"       , "YDHRJ002");						//전문코드
//		    			recInTemp.setField("STL_NO"       , szSTL_NO);							//재료번호
//		    			recInTemp.setField("TREAT_GP"     , "3");							//재료번호
//		    			recInTemp.setField("YD_UP_CMPL_DT", szYD_UP_CMPL_DT);					//크레인스케줄ID
//		    			ydDelegate.sendMsg(recInTemp);
//						szMsg = "열연조업 L3 정정추출완료실적 전문 송신 완료";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//						
//						
//						//HFL결속장에 대한 추출실적 전송 후 해당 스케줄코드로 다음 작업예약을 스케줄 기동 작업						
//						if( szYD_UP_WR_LOC.startsWith("HBFE05")||
//							szYD_UP_WR_LOC.startsWith("HDFE03")||
//							szYD_UP_WR_LOC.startsWith("HFFE02")){
//							
//							
//							recInTemp = JDTORecordFactory.getInstance().create();
//		                    recInTemp.setField("YD_STK_COL_GP", szYD_UP_WR_LOC);		                 
//		                    rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
//		                    /*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrStlchk*/
//		                    intRtnVal = ydStkLyrDao.getYdStklyr(recInTemp, rsResult, 623);
//							
//		                    if(intRtnVal > 0) {
//								if(szYD_UP_WR_LOC.startsWith("HBFE05")){	//HFL5#
//									sHFL_YD_SCH_CD ="HBFE01UM"; //HFL5# 보급
//								}else if(szYD_UP_WR_LOC.startsWith("HDFE03")){	//HFL3#
//									sHFL_YD_SCH_CD ="HDFE01UM";	//HFL3# 보급
//								}else if(szYD_UP_WR_LOC.startsWith("HFFE02")){	//HFL2#
//									sHFL_YD_SCH_CD ="HFFE01UM";	//HFL2# 보급
//								}
//								
//								recInTemp = JDTORecordFactory.getInstance().create();
//				    			recInTemp.setField("JMS_TC_CD"    , "YDYDJ509");						//전문코드
//				    			recInTemp.setField("YD_SCH_CD"    , sHFL_YD_SCH_CD);					//스케줄코드			    		 
//				    			ydDelegate.sendMsg(recInTemp);
//								szMsg = "열연조업 L3 정정추출완료실적 전문 송신 후 스케줄 기동";
//								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		                    }else{
//		                    	szMsg = "열연조업 L3 정정추출완료실적 보급위치가 없습니다.";
//								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		                    }
//						}
//						
//	            	}
//	            	
//					recInTemp = JDTORecordFactory.getInstance().create();
//					
//					if( szYD_UP_WR_LOC.startsWith("HHKD01") && ((szYD_SCH_CD.equals("JHKD01LM") )||
//							                                    (szYD_SCH_CD.equals("JHTC01MM") )||
//							                                    (szYD_SCH_CD.equals("JHTC02MM") )||
//							                                    (szYD_SCH_CD.equals("HHKD02LM") )||
//							                                    (szYD_SCH_CD.equals("HHKD05LM") )||
//							                                    ( szYD_SCH_CD.equals("HHKD06LM"))||
//							                                    (szYD_SCH_CD.equals("HHKD01LM") ))){
//						recInTemp.setField("MSG_ID"       , "YDH2L003");// SPM1 전문코드
//		    			recInTemp.setField("YD_EQP_ID"    , szYD_UP_WR_LOC.substring(0,6));		// 야드설비ID
//		    			recInTemp.setField("YD_STK_BED_NO", szYD_UP_WR_LOC.substring(6,8));		// 야드적치Bed번호 
//		    			recInTemp.setField("STL_NO"       , szSTL_NO);							// 재료번호
//		    			ydDelegate.sendMsg(recInTemp);
//						szMsg = "C열연 SPM1 정정출측Line-Off실적 전문(YDH2L003) 송신 완료";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
//					}
//					if( szYD_UP_WR_LOC.startsWith("HGFD01") && ((szYD_SCH_CD.equals("JGFD01LM")) ||
//							                                    (szYD_SCH_CD.equals("JGTC01MM")) ||
//							                                    (szYD_SCH_CD.equals("JGTC02MM")) ||
//							                                    (szYD_SCH_CD.equals("HGFD02LM")) ||
//							                                    (szYD_SCH_CD.equals("HGFD01LM")) ||
//							                                    (szYD_SCH_CD.equals("HGFD06LM")) || //지포장 추출 포함 2018.03.22 진기양 계장							                                    
//                                                                (szYD_SCH_CD.equals("HGFD04LM") ))){
//						recInTemp.setField("MSG_ID"       , "YDH2L013");// HFL 전문코드
//		    			recInTemp.setField("YD_EQP_ID"    , szYD_UP_WR_LOC.substring(0,6));		// 야드설비ID
//		    			recInTemp.setField("YD_STK_BED_NO", szYD_UP_WR_LOC.substring(6,8));		// 야드적치Bed번호 
//		    			recInTemp.setField("STL_NO"       , szSTL_NO);							// 재료번호
//		    			ydDelegate.sendMsg(recInTemp);
//						szMsg = "C열연 HFL 정정출측Line-Off실적 전문(YDH2L013) 송신 완료";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
//					}	
//					if( szYD_UP_WR_LOC.startsWith("HEDD01") && ((szYD_SCH_CD.equals("JEDD01LM")) ||
//							                                    (szYD_SCH_CD.equals("JETC01MM")) ||
//							                                    (szYD_SCH_CD.equals("JETC02MM")) ||
//							                                    (szYD_SCH_CD.equals("HEDD02LM")) ||
//							                                    (szYD_SCH_CD.equals("HEDD05LM") )||
//							                                    (szYD_SCH_CD.equals("HEDD06LM")) ||
//							                                    (szYD_SCH_CD.equals("HEDD01LM") ))){
//						recInTemp.setField("MSG_ID"       , "YDH2L023");// SPM2 전문코드
//		    			recInTemp.setField("YD_EQP_ID"    , szYD_UP_WR_LOC.substring(0,6));		// 야드설비ID
//		    			recInTemp.setField("YD_STK_BED_NO", szYD_UP_WR_LOC.substring(6,8));		// 야드적치Bed번호 
//		    			recInTemp.setField("STL_NO"       , szSTL_NO);							// 재료번호
//		    			ydDelegate.sendMsg(recInTemp);
//						szMsg = "C열연 SPM2 정정출측Line-Off실적 전문(YDH2L023) 송신 완료";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
// 				    }
////C증설
//					if( szYD_UP_WR_LOC.startsWith("HCKD03") && ((szYD_SCH_CD.equals("JCKD01LM") )||
//                            (szYD_SCH_CD.equals("JCTC01MM") )||
//                            (szYD_SCH_CD.equals("JCTC02MM") )||
//                            (szYD_SCH_CD.equals("JCTC05MM") )||
//                            (szYD_SCH_CD.equals("HCKD02LM") )||
//                            (szYD_SCH_CD.equals("HCKD05LM") )||
//                            (szYD_SCH_CD.equals("HCKD06LM")) ||
//                            (szYD_SCH_CD.equals("HCKD01LM") ))){
//						recInTemp.setField("MSG_ID"       , "YDH2L033");// SPM3 전문코드
//						recInTemp.setField("YD_EQP_ID"    , szYD_UP_WR_LOC.substring(0,6));		// 야드설비ID
//						recInTemp.setField("YD_STK_BED_NO", szYD_UP_WR_LOC.substring(6,8));		// 야드적치Bed번호 
//						recInTemp.setField("STL_NO"       , szSTL_NO);							// 재료번호
//						ydDelegate.sendMsg(recInTemp);
//						szMsg = "C열연 SPM3 정정출측Line-Off실적 전문(YDH2L033) 송신 완료";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
//					}
//	
//					if( szYD_UP_WR_LOC.startsWith("HBKD04") && ((szYD_SCH_CD.equals("JBKD01LM") )||
//                            (szYD_SCH_CD.equals("JBTC01MM") )||
//                            (szYD_SCH_CD.equals("JBTC02MM") )||
//                            (szYD_SCH_CD.equals("JBTC05MM") )||
//                            (szYD_SCH_CD.equals("HBKD02LM") )||
//                            (szYD_SCH_CD.equals("HBKD05LM") )||
//                            (szYD_SCH_CD.equals("HBKD06LM")) ||
//                            (szYD_SCH_CD.equals("HBKD01LM") ))){
//						recInTemp.setField("MSG_ID"       , "YDH2L043");// SPM4 전문코드
//						recInTemp.setField("YD_EQP_ID"    , szYD_UP_WR_LOC.substring(0,6));		// 야드설비ID
//						recInTemp.setField("YD_STK_BED_NO", szYD_UP_WR_LOC.substring(6,8));		// 야드적치Bed번호 
//						recInTemp.setField("STL_NO"       , szSTL_NO);							// 재료번호
//						ydDelegate.sendMsg(recInTemp);
//						szMsg = "C열연 SPM4 정정출측Line-Off실적 전문(YDH2L043) 송신 완료";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
//					}
//
//					if( szYD_UP_WR_LOC.startsWith("HAKD05") && ((szYD_SCH_CD.equals("JAKD01LM") )||
//                            (szYD_SCH_CD.equals("JATC01MM") )||
//                            (szYD_SCH_CD.equals("JATC02MM") )||
//                            (szYD_SCH_CD.equals("JATC05MM") )||
//                            (szYD_SCH_CD.equals("HAKD02LM") )||
//                            (szYD_SCH_CD.equals("HAKD05LM") )||
//                            (szYD_SCH_CD.equals("HAKD06LM")) ||
//                            (szYD_SCH_CD.equals("HAKD01LM") ))){
//						recInTemp.setField("MSG_ID"       , "YDH2L073");// SPM5 전문코드
//						recInTemp.setField("YD_EQP_ID"    , szYD_UP_WR_LOC.substring(0,6));		// 야드설비ID
//						recInTemp.setField("YD_STK_BED_NO", szYD_UP_WR_LOC.substring(6,8));		// 야드적치Bed번호 
//						recInTemp.setField("STL_NO"       , szSTL_NO);							// 재료번호
//						ydDelegate.sendMsg(recInTemp);
//						szMsg = "C열연 SPM5 정정출측Line-Off실적 전문(YDH2L073) 송신 완료";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
//					}
//					
//					if( szYD_UP_WR_LOC.startsWith("HCFD04") && ((szYD_SCH_CD.equals("JCFD01LM")) ||
//                            (szYD_SCH_CD.equals("JCTC01MM")) ||
//                            (szYD_SCH_CD.equals("JCTC02MM")) ||
//                            (szYD_SCH_CD.equals("HCFD02LM")) ||
//                            (szYD_SCH_CD.equals("HCFD01LM")) ||
//                            (szYD_SCH_CD.equals("HCFD04LM") ))){
//						recInTemp.setField("MSG_ID"       , "YDH2L053");// HFL4 전문코드
//						recInTemp.setField("YD_EQP_ID"    , szYD_UP_WR_LOC.substring(0,6));		// 야드설비ID
//						recInTemp.setField("YD_STK_BED_NO", szYD_UP_WR_LOC.substring(6,8));		// 야드적치Bed번호 
//						recInTemp.setField("STL_NO"       , szSTL_NO);							// 재료번호
//						ydDelegate.sendMsg(recInTemp);
//						szMsg = "C열연 HFL4 정정출측Line-Off실적 전문(YDH2L053) 송신 완료";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
//					}	
//
////					if( szYD_UP_WR_LOC.startsWith("HBFE05") && 
////						   ((szYD_SCH_CD.equals("JBFD01LM")) ||
////                            (szYD_SCH_CD.equals("JBTC01MM")) ||
////                            (szYD_SCH_CD.equals("JBTC02MM")) ||
////                            (szYD_SCH_CD.equals("HBFD02LM")) ||
////                            (szYD_SCH_CD.equals("HBFD01LM")) ||
////                            (szYD_SCH_CD.equals("HBFD04LM") ))){
////						recInTemp.setField("MSG_ID"       , "YDH2L063");// HFL5 전문코드
////						recInTemp.setField("YD_EQP_ID"    , szYD_UP_WR_LOC.substring(0,6));		// 야드설비ID
////						recInTemp.setField("YD_STK_BED_NO", szYD_UP_WR_LOC.substring(6,8));		// 야드적치Bed번호 
////						recInTemp.setField("STL_NO"       , szSTL_NO);							// 재료번호
////						ydDelegate.sendMsg(recInTemp);
////						szMsg = "C열연 HFL5 정정출측Line-Off실적 전문(YDH2L063) 송신 완료";
////						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
////					}	
//					
//	            
//	            
//	            }
//				szMsg = "===================================================================================================================";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//
////// 권상 실적 위치 입측(Take-Out)
//	            szYD_UP_WR_LOC = ydDaoUtils.paraRecChkNull(getRecord, "YD_UP_WR_LOC");
//				szMsg = "+++++ Take-Out 에 대한 요구 처리 : 권상 실적 위치 YD_UP_WR_LOC(" + szYD_UP_WR_LOC + ") +++++";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	            
//	            if(szYD_UP_WR_LOC.startsWith("HBFE05") || 
//	 		 	   szYD_UP_WR_LOC.startsWith("HBKE04") ||
//	 		 	   szYD_UP_WR_LOC.startsWith("HAKE05") || 
//	               szYD_UP_WR_LOC.startsWith("HCFE04") || 
//	 	           szYD_UP_WR_LOC.startsWith("HCKE03") || 
//	               szYD_UP_WR_LOC.startsWith("HEDE01") ||
//	               szYD_UP_WR_LOC.startsWith("HGFE01") || 
//			 	   szYD_UP_WR_LOC.startsWith("HHKE01") ||
//			 	   //2020.08.27 추출존 TAKE OUT 추가
//			 	   szYD_UP_WR_LOC.startsWith("HHKD01") ||
//			 	   szYD_UP_WR_LOC.startsWith("HEDD01") ||
//			 	   szYD_UP_WR_LOC.startsWith("HCKD03") ||
//			 	   szYD_UP_WR_LOC.startsWith("HBKD04") ||
//			 	   szYD_UP_WR_LOC.startsWith("HAKD05")
//	               ){
//	            	
//	            	if(( szYD_SCH_CD.equals("HBFE03LM") )||
//              		   ( szYD_SCH_CD.equals("HBKE03LM") )||
//              		   ( szYD_SCH_CD.equals("HAKE03LM") )||
//	 	               ( szYD_SCH_CD.equals("HCFE03LM") )||
// 	            	   ( szYD_SCH_CD.equals("HCKE03LM") )||
//	 	               ( szYD_SCH_CD.equals("HEDE03LM") )|| 
//	 	               ( szYD_SCH_CD.equals("HGFE03LM") )|| 
//	 	               ( szYD_SCH_CD.equals("HHKE03LM") )||
//	 	              //2020.08.27 추출존 TAKE OUT 추가
//	 	              ( szYD_SCH_CD.equals("HHKD03LM") )||
//	 	              ( szYD_SCH_CD.equals("HEDD03LM") )||
//	 	              ( szYD_SCH_CD.equals("HCKD03LM") )||
//	 	              ( szYD_SCH_CD.equals("HBKD03LM") )||
//	 	              ( szYD_SCH_CD.equals("HAKD03LM") )
//	 	               
//	 	               ){
//	   		         
//		            	recInTemp = JDTORecordFactory.getInstance().create();
//		    			recInTemp.setField("MSG_ID"       , "YDHRJ002");						//전문코드
//		    			recInTemp.setField("STL_NO"       , szSTL_NO);							//재료번호
//		    			recInTemp.setField("TREAT_GP"       , "4");							    //TAKEOUT
//		    			recInTemp.setField("YD_UP_CMPL_DT", szYD_UP_CMPL_DT);					//크레인스케줄ID
//		    			
//		    			recInTemp.setField("YD_ISPTOR"		, szISPTOR);
//		    			recInTemp.setField("YD_TAKE_OUT_DT"	, szTAKE_OUT_DT);
//		    			recInTemp.setField("YD_TAKE_OUT_CD"	, szTAKE_OUT_CD);
//		    			
//		    			ydDelegate.sendMsg(recInTemp);
//						szMsg = "열연조업 L3 정정추출완료실적 전문 송신 완료";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	            	}	
//		        	// 조업 정정 L2 전문
//					recInTemp = JDTORecordFactory.getInstance().create();
//					
//					if( (szYD_UP_WR_LOC.startsWith("HHKE01") && szYD_SCH_CD.equals("HHKE03LM")) || (szYD_UP_WR_LOC.startsWith("HHKD01") && szYD_SCH_CD.equals("HHKD03LM")) ){
//						recInTemp.setField("MSG_ID"       , "YDH2L004");// SPM1 전문코드
//		    			recInTemp.setField("YD_EQP_ID"    , szYD_UP_WR_LOC.substring(0,6));		// 야드설비ID
//		    			recInTemp.setField("YD_STK_BED_NO", szYD_UP_WR_LOC.substring(6,8));		// 야드적치Bed번호 
//		    			recInTemp.setField("STL_NO"       , szSTL_NO);							// 재료번호
//		    			recInTemp.setField("YD_CRN_SCH_ID"       , szYD_CRN_SCH_ID);							// 재료번호		    			
//		    			ydDelegate.sendMsg(recInTemp);
//						szMsg = "C열연 SPM1 정정입측TAKE-OUT실적 전문(YDH2L004) 송신 완료";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
//					}
//					if( szYD_UP_WR_LOC.startsWith("HGFE01") && szYD_SCH_CD.equals("HGFE03LM") ){
//					//	if(szYD_UP_WR_LOC.startsWith("HHKD01")){
//						recInTemp.setField("MSG_ID"       , "YDH2L014");// HFL 전문코드
//		    			recInTemp.setField("YD_EQP_ID"    , szYD_UP_WR_LOC.substring(0,6));		// 야드설비ID
//		    			recInTemp.setField("YD_STK_BED_NO", szYD_UP_WR_LOC.substring(6,8));		// 야드적치Bed번호 
//		    			recInTemp.setField("STL_NO"       , szSTL_NO);							// 재료번호
//		    			ydDelegate.sendMsg(recInTemp);
//						szMsg = "C열연 HFL 정정입측TAKE-OUT실적 전문(YDH2L014) 송신 완료";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
//					}	
//					if(( szYD_UP_WR_LOC.startsWith("HEDE01") && szYD_SCH_CD.equals("HEDE03LM")) || (szYD_UP_WR_LOC.startsWith("HEDD01") && szYD_SCH_CD.equals("HEDD03LM")) ){
//					//		if(szYD_UP_WR_LOC.startsWith("HEDD01")){
//						recInTemp.setField("MSG_ID"       , "YDH2L024");// SPM2 전문코드
//		    			recInTemp.setField("YD_EQP_ID"    , szYD_UP_WR_LOC.substring(0,6));		// 야드설비ID
//		    			recInTemp.setField("YD_STK_BED_NO", szYD_UP_WR_LOC.substring(6,8));		// 야드적치Bed번호 
//		    			recInTemp.setField("STL_NO"       , szSTL_NO);							// 재료번호
//		    			ydDelegate.sendMsg(recInTemp);
//						szMsg = "C열연 SPM2 정정입측TAKE-OUT실적 전문(YDH2L024) 송신 완료";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
// 				    }
////C증설					
//					if( (szYD_UP_WR_LOC.startsWith("HCKE03") && szYD_SCH_CD.equals("HCKE03LM")) || szYD_UP_WR_LOC.startsWith("HCKD03") && szYD_SCH_CD.equals("HCKD03LM")){
//						recInTemp.setField("MSG_ID"       , "YDH2L034");// SPM3 전문코드
//		    			recInTemp.setField("YD_EQP_ID"    , szYD_UP_WR_LOC.substring(0,6));		// 야드설비ID
//		    			recInTemp.setField("YD_STK_BED_NO", szYD_UP_WR_LOC.substring(6,8));		// 야드적치Bed번호 
//		    			recInTemp.setField("STL_NO"       , szSTL_NO);							// 재료번호
//		    			recInTemp.setField("YD_CRN_SCH_ID"       , szYD_CRN_SCH_ID);							// 재료번호		    			
//		    			ydDelegate.sendMsg(recInTemp);
//						szMsg = "C열연 SPM3 정정입측TAKE-OUT실적 전문(YDH2L034) 송신 완료";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
//					}
//					if( (szYD_UP_WR_LOC.startsWith("HBKE04") && szYD_SCH_CD.equals("HBKE03LM")) || (szYD_UP_WR_LOC.startsWith("HBKD04") && szYD_SCH_CD.equals("HBKD03LM"))  ){
//						recInTemp.setField("MSG_ID"       , "YDH2L044");// SPM4 전문코드
//		    			recInTemp.setField("YD_EQP_ID"    , szYD_UP_WR_LOC.substring(0,6));		// 야드설비ID
//		    			recInTemp.setField("YD_STK_BED_NO", szYD_UP_WR_LOC.substring(6,8));		// 야드적치Bed번호 
//		    			recInTemp.setField("STL_NO"       , szSTL_NO);							// 재료번호
//		    			recInTemp.setField("YD_CRN_SCH_ID"       , szYD_CRN_SCH_ID);							// 재료번호		    			
//		    			ydDelegate.sendMsg(recInTemp);
//						szMsg = "C열연 SPM4 정정입측TAKE-OUT실적 전문(YDH2L044) 송신 완료";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
//					}
//					if( (szYD_UP_WR_LOC.startsWith("HAKE05") && szYD_SCH_CD.equals("HAKE03LM")) || (szYD_UP_WR_LOC.startsWith("HAKD05") && szYD_SCH_CD.equals("HAKD03LM")) ){
//						recInTemp.setField("MSG_ID"       , "YDH2L074");// SPM4 전문코드
//		    			recInTemp.setField("YD_EQP_ID"    , szYD_UP_WR_LOC.substring(0,6));		// 야드설비ID
//		    			recInTemp.setField("YD_STK_BED_NO", szYD_UP_WR_LOC.substring(6,8));		// 야드적치Bed번호 
//		    			recInTemp.setField("STL_NO"       , szSTL_NO);							// 재료번호
//		    			recInTemp.setField("YD_CRN_SCH_ID"       , szYD_CRN_SCH_ID);							// 재료번호		    			
//		    			ydDelegate.sendMsg(recInTemp);
//						szMsg = "C열연 SPM5 정정입측TAKE-OUT실적 전문(YDH2L074) 송신 완료";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
//					}
//					if( szYD_UP_WR_LOC.startsWith("HCFE04") && szYD_SCH_CD.equals("HCFE03LM") ){
//							recInTemp.setField("MSG_ID"       , "YDH2L054");// HFL4 전문코드
//			    			recInTemp.setField("YD_EQP_ID"    , szYD_UP_WR_LOC.substring(0,6));		// 야드설비ID
//			    			recInTemp.setField("YD_STK_BED_NO", szYD_UP_WR_LOC.substring(6,8));		// 야드적치Bed번호 
//			    			recInTemp.setField("STL_NO"       , szSTL_NO);							// 재료번호
//			    			ydDelegate.sendMsg(recInTemp);
//							szMsg = "C열연 HFL4 정정입측TAKE-OUT실적 전문(YDH2L054) 송신 완료";
//							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
//					}	
//					if( szYD_UP_WR_LOC.startsWith("HBFE05") && szYD_SCH_CD.equals("HBFE03LM") ){
//							recInTemp.setField("MSG_ID"       , "YDH2L064");// HFL5 전문코드
//			    			recInTemp.setField("YD_EQP_ID"    , szYD_UP_WR_LOC.substring(0,6));		// 야드설비ID
//			    			recInTemp.setField("YD_STK_BED_NO", szYD_UP_WR_LOC.substring(6,8));		// 야드적치Bed번호 
//			    			recInTemp.setField("STL_NO"       , szSTL_NO);							// 재료번호
//			    			ydDelegate.sendMsg(recInTemp);
//							szMsg = "C열연 HFL5 정정입측TAKE-OUT실적 전문(YDH2L064) 송신 완료";
//							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
//					}	
//	    									
//
//		        } else {
//					szMsg = "권상실적 위치가 Take-Out요구가 아님";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		        	
//		        }
//				szMsg = "===================================================================================================================";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		        		        	
//	            
//				
/////권상 실적 위치 지포장(Line-Off)
//// 151217 hun 지포장 권상시 마지막권상(지포장비었을때..) 일때 지포장 보급 스케줄 기동
//	            szYD_UP_WR_LOC = ydDaoUtils.paraRecChkNull(getRecord, "YD_UP_WR_LOC");
//				szMsg = "+++++ 지포장보급(지포장장->제품야드)에 대한 권상 처리 start : 권상 실적 위치 YD_UP_WR_LOC(" + szYD_UP_WR_LOC + ") +++++";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	            
//	            if((szYD_UP_WR_LOC.startsWith("HBGF01")) ||
//	               (szYD_UP_WR_LOC.startsWith("HCGF01")) || 
//	 	           (szYD_UP_WR_LOC.startsWith("HEGF01")) ||
//	 	           (szYD_UP_WR_LOC.startsWith("HHGF01"))   )   {
//	            	if(
//	            	   ( szYD_SCH_CD.equals("JBGF01LM")) ||
//	            	   ( szYD_SCH_CD.equals("JCGF01LM")) ||
//	            	   ( szYD_SCH_CD.equals("JEGF01LM")) ||
//	            	   ( szYD_SCH_CD.equals("JHGF01LM")) 
//
//	            	   ){
//	            	
//		            	recInTemp = JDTORecordFactory.getInstance().create();
//		    			recInTemp.setField("MSG_ID"       , "YDHRJ002");						//전문코드
//		    			recInTemp.setField("STL_NO"       , szSTL_NO);							//재료번호
//		    			recInTemp.setField("TREAT_GP"     , "3");							//재료번호
//		    			recInTemp.setField("YD_UP_CMPL_DT", szYD_UP_CMPL_DT);					//크레인스케줄ID
//		    			ydDelegate.sendMsg(recInTemp);
//						szMsg = "열연조업 L3 정정추출완료실적 전문 송신 완료";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//						
//						
//						//지포장장에 대한 추출실적 전송 후 해당 스케줄코드로 다음 작업예약을 스케줄 기동 작업						
//						recInTemp = JDTORecordFactory.getInstance().create();
//	                    recInTemp.setField("YD_STK_COL_GP", szYD_UP_WR_LOC);		                 
//	                    rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
//	                    /*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrStlchk*/
//	                    intRtnVal = ydStkLyrDao.getYdStklyr(recInTemp, rsResult, 623);
//						
////	                    지포장장 6개 이므로 빈자리가 6개일때 스케줄 기동
//	                    if(intRtnVal > 5) {
//							if(szYD_UP_WR_LOC.startsWith("HBGF01")){	// B동 지포장
//								sGF_YD_SCH_CD ="HBGF01UM"; 
//							}else if(szYD_UP_WR_LOC.startsWith("HCGF01")){	// C동 지포장
//								sGF_YD_SCH_CD ="HCGF01UM";	
//							}else if(szYD_UP_WR_LOC.startsWith("HEGF01")){	// E동 지포장
//								sGF_YD_SCH_CD ="HEGF01UM";	
//							}else if(szYD_UP_WR_LOC.startsWith("HHGF01")){	// H동 지포장
//								sGF_YD_SCH_CD ="HHGF01UM";	
//							}
//							
//							recInTemp = JDTORecordFactory.getInstance().create();
//			    			recInTemp.setField("JMS_TC_CD"    , "YDYDJ509");						//전문코드
//			    			recInTemp.setField("YD_SCH_CD"    , sGF_YD_SCH_CD);					//스케줄코드			    		 
//			    			ydDelegate.sendMsg(recInTemp);
//							szMsg = "열연조업 L3 정정추출완료실적 전문 송신 후 스케줄 기동";
//							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	                    }else{
//	                    	szMsg = "지포장위치에 코일이 있습니다. 현재빈자리 intRtnVal ="+intRtnVal;
//							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	                    }
//						
//	            	}
//	            	
//	            }
//	            szMsg = "+++++ 지포장보급에 대한 권상 처리 end : 권상 실적 위치 YD_UP_WR_LOC(" + szYD_UP_WR_LOC + ") +++++";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//
//				
//				
//				// #3HFL 결속장 시편채취 권상처리
//				szYD_UP_WR_LOC = ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC");						
//				szMsg = "+++++ #3HFL 결속장 시편채취 권상처리 : 권상 실적 위치 YD_UP_WR_LOC("+ szYD_UP_WR_LOC + ") +++++";						
//				ydUtils.putLog(szSessionName, szMethodName, szMsg,YdConstant.DEBUG);
//
//				if ((szYD_UP_WR_LOC.startsWith("HDCR01"))) {
//					// 3HFL 신규 시편채취기에서 시편 채취 완료 정보 기록 메소드
////					EJBConnector ejbConn = new EJBConnector("hsteelApp","HrShrWrHdSeEJB", this);							
////					ejbConn.trx("updHrShrReagentYReg",new Class[]{String.class}, new Object[]{szSTL_NO});
//					
//					recInTemp = JDTORecordFactory.getInstance().create();
//	    			recInTemp.setField("MSG_ID"       , "YDHRJ003");						//전문코드
//	    			recInTemp.setField("STL_NO"       , szSTL_NO);							//재료번호	    			
//	    			ydDelegate.sendMsg(recInTemp);
//				}
//
//				szMsg = "===================================================================================================================";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg,
//						YdConstant.DEBUG);
//				
//				
//				
//				//결로방지 자동 보급 추가
//				if(szYD_UP_WR_LOC.startsWith("JE") && szYD_SCH_CD.equals("HEHC01UM")){
//					
//					recInTemp = JDTORecordFactory.getInstance().create();
//                    recInTemp.setField("YD_STK_COL_GP", szYD_UP_WR_LOC);		                 
//                    rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
//                    /*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrHotCoilChk*/
//                    intRtnVal = ydStkLyrDao.getYdStklyr(recInTemp, rsResult, 628);
//                    
//                    if(intRtnVal > 0) {
//                    	 rsResult.absolute(1);
//                         recOutTemp = JDTORecordFactory.getInstance().create();
//                         recOutTemp.setRecord(rsResult.getRecord());
//                         
//     	                
//                         String szCHK  = ydDaoUtils.paraRecChkNull(recOutTemp,"CHK") ;
//                         
//                         if("Y".equals(szCHK)){
//                        	 szMsg = "########결로방지 자동 보급 실행 ########";
//             				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//                        	 
//             				JDTORecord recPara		= JDTORecordFactory.getInstance().create();
//             				
//             				recPara.setField("YD_CHK_GP"			,"1");
//             				recPara.setField("YD_USER_ID"			,"doHotcoilCall"); 
//             				
//             				EJBConnector ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
//							ejbConn.trx("procHotcoilAuto2", new Class[] { JDTORecord.class },	new Object[] { recPara });
//                         }
//                    }
//				}
//				
//				
//				
//				/*
//		         * HFL결속장에 대한 크레인스케쥴기동  - YDYDJ509 권상완료
//		         * 2020.01.30
//		         */ 
//                JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
//    			recInTemp = JDTORecordFactory.getInstance().create();
//    			recInTemp.setField("YD_BAY_GP", szYD_UP_WR_LOC.substring(1,2));	
//    			/*com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookHFLChk*/
//    			intRtnVal = ydWrkbookDao.getYdWrkbook(recInTemp, outRecSet, 509);
//    			
//    			if( intRtnVal > 0 ) {
//    				outRecSet.first();
//    				recInTemp = outRecSet.getRecord();
//    			 
//					
//    				getRecord = JDTORecordFactory.getInstance().create();
//    				getRecord.setField("JMS_TC_CD"    , "YDYDJ509");						//전문코드
//    				getRecord.setField("YD_EQP_ID"	, ydDaoUtils.paraRecChkNull(recInTemp, "YD_EQP_ID"));
//    				getRecord.setField("YD_SCH_CD"	, ydDaoUtils.paraRecChkNull(recInTemp, "YD_SCH_CD"));
//    				getRecord.setField("YD_WBOOK_ID", ydDaoUtils.paraRecChkNull(recInTemp, "YD_WBOOK_ID"));	    		 
//	    			ydDelegate.sendMsg(getRecord);
//					szMsg = "@@@다음 HFL결속장 작업예약으로 스케줄 기동 한다.";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//                }else{
//                	szMsg = "@@@HFL결속장 작업예약이 없습니다.";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//                }
//                
//                
//			 
//				
//		        /*
//		         * C열연코일L2 크레인작업실적응답 전송  - YDY5L005 권상완료
//		         * 2009.07.01
//		         */
//		        szYD_EQP_ID = ydDaoUtils.paraRecChkNull(getParamRecord, "YD_EQP_ID");
//		        recInTemp = JDTORecordFactory.getInstance().create(); 
//		        recInTemp.setField("MSG_ID"          , "YDY5L005");
//		        recInTemp.setField("YD_EQP_ID"       , szYD_EQP_ID);			        	//야드설비ID
//		        recInTemp.setField("YD_WRK_PROG_STAT", YdConstant.YD_EQP_STAT_UP_CMPL);		//야드작업진행상태
//		        recInTemp.setField("YD_SCH_CD"       , szYD_SCH_CD);				        //야드스케줄코드
//		        recInTemp.setField("YD_CRN_SCH_ID"   , szYD_CRN_SCH_ID);		            //야드크레인스케줄ID
//		        recInTemp.setField("YD_L2_WR_GP"     , YdConstant.CRN_WRK_RE_LD_WR);		//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
//		        recInTemp.setField("YD_L3_HD_RS_CD"  , YdConstant.CRN_WRK_RE_CD_NORMAL_HD);	//야드L3처리결과코드
//				ydDelegate.sendMsg(recInTemp);
//				szMsg = "[권상실적처리]C열연코일L2 크레인작업실적응답[YDY5L005] 전송 완료" ;
//                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	            
//// 대차이동 실적 송신
//				szMsg = "[권상실적처리]대차 이동 여부 :" + sTCAR_MOVE_SND ;
//                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//                if(sTCAR_MOVE_SND.equals("Y")){
//                	
//		    		recInTemp = JDTORecordFactory.getInstance().create();
//		    		recInTemp.setField("MSG_ID"			, "YDY5L006");
//		    		recInTemp.setField("YD_GP"			, StringHelper.evl(outRecord1.getFieldString("YD_GP"), ""));
//		    		recInTemp.setField("YD_SCH_CD"		, StringHelper.evl(outRecord1.getFieldString("YD_SCH_CD"), ""));
//		    		recInTemp.setField("YD_TCAR_SCH_ID"	, StringHelper.evl(outRecord1.getFieldString("YD_TCAR_SCH_ID"), ""));
//		    		recInTemp.setField("YD_AIM_BAY_GP"	, StringHelper.evl(outRecord1.getFieldString("YD_AIM_BAY_GP"), ""));
//		    		ydDelegate.sendMsg(recInTemp);
//		    		
//					szMsg="[권상실적처리] 대차 출발지시 전송 완료[2]";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//                	
//                }
//                
//                
//                
//        	    //------------------------------------------------------------------
//                // 권상 실적시 Flex 실시간 처리
//                //------------------------------------------------------------------
//                JDTORecord recFlex   = JDTORecordFactory.getInstance().create();
//                if(szYD_SCH_CD.substring(0,1).equals("H")){
//                	recFlex.setField("YD_GP",  YdConstant.YD_GP_C_HR_COIL_MATL_YARD);
//                }else {
//                	recFlex.setField("YD_GP",  YdConstant.YD_GP_C_HR_COIL_GDS_YARD);
//                }
//        	 	recFlex.setField("YD_EQP_ID", szYD_EQP_ID);
//        	 	recFlex.setField("YD_UP_WR_LOC", szYD_UP_WR_LOC);
//        	 	szMsg="Flex 권상 완료 실적 전송";
//        	 	 
//        	 	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//        		ydUtils.putYdFlexCrnWrk("", recFlex);  
//                
//                //------------------------------------------------------------------
//        		
//        		
//        		
//	        }else{
//	            szMsg = "YD_WRK_PROG_STAT data : '1' or 'w' not" ;
//	            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	            throw new DAOException("<"+szMethodName+"> CraneLdHdSeEJB " + szMsg);
//	        }
//
//	        ydUtils.putLog(szSessionName, szMethodName, "▲▲▲▲▲[C열연 코일야드]권상실적처리 END ▲▲▲▲▲", YdConstant.INFO);
//	        
//	    }catch(JDTOException e) {
//			//System.out.println("JDTOError :  "+ e.getLocalizedMessage());
//			szMsg="JDTOError :  "+ e.getLocalizedMessage();
//	        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	        throw new DAOException("<"+szMethodName+"> CraneLdHdSeEJB " + szMsg);
//	    }catch(Exception e) {
//	    	//System.out.println("Error :  "+ e.getLocalizedMessage());
//	    	szMsg="Error :  "+ e.getLocalizedMessage();
//	        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	        throw new DAOException("<"+szMethodName+"> CraneLdHdSeEJB " + szMsg);
//	    }//end of try~catch
//	  
//	    return YdConstant.RETN_CD_SUCCESS;
//    }// end of procY5CrnLdWr()
    

    
    
    
    
    
    
//    /**
//     * 오퍼레이션명 : 권상 파라미터 체크
//     *  
//     * @param  ● msgRecord, outRecord, intGp
//     * @return ● intRtnVal
//     * @throws ● JDTOException
//     */
//    public int Y5ParamCheckCoil (JDTORecord msgRecord, JDTORecord outRecord, int intGp) throws JDTOException {
//    	JDTORecord setRecord = JDTORecordFactory.getInstance().create();
//        String szMsg                        = "" ;
//        String szMethodName                 = "Y5ParamCheck";
//        int intRtnVal = 0 ;
//        
//    	try{
//            
//			setRecord.setField("YD_CRN_SCH_ID"			, ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_SCH_ID")) ;
//			setRecord.setField("YD_SCH_CD"        		, ydDaoUtils.paraRecChkNull(msgRecord,"YD_SCH_CD")) ;
//			setRecord.setField("MSG_ID"          		, ydDaoUtils.paraRecChkNull(msgRecord,"MSG_ID")) ;
//			setRecord.setField("DATE"          			, ydDaoUtils.paraRecChkNull(msgRecord,"DATE")) ;
//			setRecord.setField("TIME"          			, ydDaoUtils.paraRecChkNull(msgRecord,"TIME")) ;
//			setRecord.setField("MSG_GP"          		, ydDaoUtils.paraRecChkNull(msgRecord,"MSG_GP")) ;
//			setRecord.setField("YD_EQP_ID"          	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_ID")) ;
//			setRecord.setField("YD_EQP_WRK_MODE"        , ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_WRK_MODE")) ;
//			setRecord.setField("YD_EQP_WRK_MODE2"       , ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_WRK_MODE2")) ; // 150623 크레인무인화
//			setRecord.setField("YD_WRK_PROG_STAT"       , ydDaoUtils.paraRecChkNull(msgRecord,"YD_WRK_PROG_STAT")) ;
//			setRecord.setField("YD_CRN_XAXIS"          	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_XAXIS")) ;
//			setRecord.setField("YD_CRN_YAXIS"         	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_YAXIS")) ;
//			setRecord.setField("YD_CRN_ZAXIS"          	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_CRN_ZAXIS")) ;
//
//			setRecord.setField("YD_UP_WR_LOC"          	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_WR_LOC")) ;
//			setRecord.setField("YD_UP_WR_LAYER"        	, ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_WR_LAYER")) ;
//		
//	        //전문 송신지 위치 Check				:AUTO MANUAL BACKUP구분
//	        if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("1")) {
//	        	setRecord.setField("YD_UP_WRK_ACT_GP", "A") ;
//	        }else if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("9")) {
//	        	setRecord.setField("YD_UP_WRK_ACT_GP", "B") ;
//	        }else if(setRecord.getFieldString("YD_EQP_WRK_MODE").equals("0")) {
//	        	setRecord.setField("YD_UP_WRK_ACT_GP", "M") ;
//	        }
//	        
//	      // 150623 크레인무인화
//	        if("A".equals(ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_WRK_MODE2"))) {
//	        	setRecord.setField("YD_UP_WRK_MODE2", "A") ;
//	        	setRecord.setField("YD_DN_WRK_MODE2", "A") ;
//	        }else if("R".equals(ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_WRK_MODE2"))) {
//	        	setRecord.setField("YD_UP_WRK_MODE2", "R") ;
//	        	setRecord.setField("YD_DN_WRK_MODE2", "R") ;
//	        }else if(("E").equals(ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_WRK_MODE2"))) {
//	        	setRecord.setField("YD_UP_WRK_MODE2", "E") ;
//	        	setRecord.setField("YD_DN_WRK_MODE2", "E") ;
//	        }else if("M".equals(ydDaoUtils.paraRecChkNull(msgRecord,"YD_EQP_WRK_MODE2"))) {
//	        	setRecord.setField("YD_UP_WRK_MODE2", "M") ;
//	        	setRecord.setField("YD_DN_WRK_MODE2", "M") ;
//	        }
//
//    		outRecord.addRecord(setRecord) ;
//            
//        }catch(Exception e){
//			System.out.println("Error : "+ e.getLocalizedMessage());
//			intRtnVal = -1 ;
//			return intRtnVal;
//        }//end of try~catch
//        
//        intRtnVal = 1 ;
//        return intRtnVal;
//        
//    }//end of Y5ParamCheck()
    
    
    
    
    
    
    
    
    
//    /**
//     * 오퍼레이션명 : 크레인스케줄 Select
//     *  
//     * @param  ● msgRecord, outRecSet, intGp
//     * @return ● intRtnVal
//     * @throws ● JDTOException
//     */
//    public int Y5GetYdCrnschCoil (JDTORecord msgRecord, JDTORecordSet outRecSet, int intGp) throws JDTOException {
//    	
//    	YdCrnSchDao ydCrnschDao = new YdCrnSchDao();
//    	
//    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
//    	
//    	int intRtnVal 			= 0 ;
//    	
//        try{
//        	
//	        intRtnVal = ydCrnschDao.getYdCrnsch(msgRecord, getRecSet, intGp);  
//	        outRecSet.addAll(getRecSet);
//
//        }catch(Exception e){
//			System.out.println("Error : "+ e.getLocalizedMessage());
//			return -1 ;
//        }//end of try~catch
//
//        return intRtnVal ;
//    	
//    }//end of Y5GetYdCrnschCoil()
    
    
    
    
    
    
    
    
//    /**
//     * 오퍼레이션명 : 크레인스케줄 Update
//     *  
//     * @param  ● msgRecord, intGp
//     * @return ● intRtnVal
//     * @throws ● JDTOException
//     */
//    public int Y5UpdYdCrnschCoil(JDTORecord msgRecord, int intGp) throws JDTOException {
//    	YdCrnSchDao ydCrnschDao = new YdCrnSchDao();
//
//    	int intRtnVal = 0 ;
//
//		try{
//			intRtnVal = ydCrnschDao.updYdCrnsch(msgRecord, intGp);		        
//		}catch(Exception e){
//			System.out.println("Error : "+ e.getLocalizedMessage());
//			return -3 ;
//        }//end of try~catch
//		
//		return intRtnVal ;
//		
//    }// end of Y5UpdYdCrnschCoil
    
    
    
    
    
    
//    /**
//     * 오퍼레이션명 : 적치단 Clear
//     *  
//     * @param  ● getRecSet, intGp
//     * @return ● intRtnVal
//     * @throws ● JDTOException
//     */
//    public int Y5ClearYdStklyrCoil (JDTORecordSet getRecSet, int intGp) throws JDTOException {
//    	YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
//    	
//    	JDTORecord getRecord = JDTORecordFactory.getInstance().create();
//    	JDTORecord setRecord = JDTORecordFactory.getInstance().create();
//    	
//    	
//    	JDTORecord recInTemp  = null;
//    	JDTORecord recOutTemp = null;
//    	
//    	
//    	//outRecSet의 첫번째 레코드값
//    	JDTORecord outRecord 	= JDTORecordFactory.getInstance().create();
//    	//적치단 조회
//    	JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
//    	
//    	JDTORecordSet rsResult  = null;
//    	
//    	
//    	
//    	int intRtnVal 		= 0;
//    	String szMsg 		= "";
//    	String szMethodName = "Y5ClearYdStklyrCoil";
//    	
//    	String szSTL_NO         = "";
//    	String szYD_CRN_SCH_ID  = "";
//    	String szYD_UP_WR_LOC   = "";
//    	String szYD_UP_WR_LAYER = "";
//    	
//    	String szStkLyr = "";
//    	
//    	try{
//    		int rowsize = getRecSet.size();
//            getRecSet.first();
//            getRecord = getRecSet.getRecord();
//            
//    		for(int i=0; i<rowsize ; i++){
//    			
//    			//권상 실적위치 Clear
//                if(intGp == 0) {
//        			
//                    szSTL_NO         = ydDaoUtils.paraRecChkNull(getRecord,"STL_NO");
//                    szYD_CRN_SCH_ID  = ydDaoUtils.paraRecChkNull(getRecord,"YD_CRN_SCH_ID");
//                    
//                    //재료번호로 권상대기인 재료의 위치를 찾는다.
//                    
//                    recInTemp = JDTORecordFactory.getInstance().create();
//                    recInTemp.setField("STL_NO", szSTL_NO);
//                    recInTemp.setField("YD_STK_LYR_MTL_STAT", "U");
//                    rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
//                    intRtnVal = ydStkLyrDao.getYdStklyr(recInTemp, rsResult, 3);
//                    if(intRtnVal <= 0) {
//		                szMsg = "재료번호의 적치단 위치를 찾을 수 없습니다.";
//		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		                
////		                150907 hun 적치단 정보 조회 안될때 에러로 멈추는 현상 발생하여 멈춤 삭제 start
//		                intRtnVal = 1;
//		                continue;
////		                150907 hun 적치단 정보 조회 안될때 에러로 멈추는 현상 발생하여 멈춤 삭제 end
//                    }
//                    rsResult.absolute(1);
//                    recOutTemp = JDTORecordFactory.getInstance().create();
//                    recOutTemp.setRecord(rsResult.getRecord());
//                    
//	                
//                    szYD_UP_WR_LOC   = ydDaoUtils.paraRecChkNull(recOutTemp,"YD_STK_COL_GP") + ydDaoUtils.paraRecChkNull(recOutTemp,"YD_STK_BED_NO");
//                    szYD_UP_WR_LAYER = ydDaoUtils.paraRecChkNull(recOutTemp,"YD_STK_LYR_NO");
//                    
//                    
//	                szMsg = "재료번호의 적치단 위치 ==> szYD_UP_WR_LOC : " + szYD_UP_WR_LOC 
//                    + " ,  szYD_UP_WR_LAYER : " + szYD_UP_WR_LAYER;
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//              
//	                
//                    if(szYD_UP_WR_LAYER.equals("002")) {
//                    	//권상 실적위치가 2단 일 경우 
//                    	setRecord = JDTORecordFactory.getInstance().create();
//                    	
//	                    setRecord.setField("YD_STK_COL_GP",       szYD_UP_WR_LOC.substring(0,6));    
//	                    setRecord.setField("YD_STK_BED_NO",       szYD_UP_WR_LOC.substring(6,8));   
//                        
//                        
//                        //적치단 설정
//                        szStkLyr =ydDaoUtils.stringPlusInt(szYD_UP_WR_LAYER, i);
//                        
//                        
//                        setRecord.setField("YD_STK_LYR_NO",       	 szStkLyr) ;
//                        setRecord.setField("STL_NO",             	 "");
//                        setRecord.setField("YD_STK_LYR_MTL_STAT", 	 "E");
////                        setRecord.setField("YD_STK_LYR_XAXIS",       "") ;
////                        setRecord.setField("YD_STK_LYR_YAXIS",       "") ;
////                        setRecord.setField("YD_STK_LYR_ZAXIS",       "") ;
//                        intRtnVal = this.Y5UpdYdStklyrCoil(setRecord, 1);  //적치단의 재료정보 Clear
//                        if(intRtnVal <= 0) {
//                        	return intRtnVal ;
//                        }
//                        //에러 메시지
//                        //
//                        
//                    //권상 실적위치가 1단 일 경우  
//                    }
//                    else if (szYD_UP_WR_LAYER.equals("001")){
//                    	setRecord = JDTORecordFactory.getInstance().create();
//	                    setRecord.setField("YD_STK_COL_GP",       szYD_UP_WR_LOC.substring(0,6));    
//	                    setRecord.setField("YD_STK_BED_NO",       szYD_UP_WR_LOC.substring(6,8));   
//	                    //적치단 설정
//	                    szStkLyr =ydDaoUtils.stringPlusInt(szYD_UP_WR_LAYER, i);
//
//	                    
//	                    
////	                    //적치BED의 앞쪽 DATA NULL CHECK
////	                    //실적위치Bed Right 2단 Check			적치상태를 사용불가
////	                    setRecord = JDTORecordFactory.getInstance().create();
////	                    setRecord.setField("YD_STK_COL_GP",       szYD_UP_WR_LOC.substring(0,6));    
////	                    setRecord.setField("YD_STK_BED_NO",       szYD_UP_WR_LOC.substring(6,8));  
////	                    setRecord.setField("YD_STK_LYR_NO",       "002") ;
////	                    setRecord.setField("YD_STK_LYR_MTL_STAT", "X");
////	                    setRecord.setField("STL_NO",              "");
////	                    intRtnVal = this.Y5GetYdStklyrCoil(setRecord, outRecSet, 0); 
////	                    if(intRtnVal>0) {
////
////		                    //실적위치Bed Right 2단 Check			적치상태를 사용불가
////		                    setRecord = JDTORecordFactory.getInstance().create();
////		                    setRecord.setField("YD_STK_COL_GP",       szYD_UP_WR_LOC.substring(0,6));    
////		                    setRecord.setField("YD_STK_BED_NO",       szYD_UP_WR_LOC.substring(6,8));  
////		                    setRecord.setField("YD_STK_LYR_NO",       "002") ;
////		                    setRecord.setField("YD_STK_LYR_MTL_STAT", "X");
////		                    setRecord.setField("STL_NO",              "");
////		                    intRtnVal = this.Y5UpdYdStklyrCoil(setRecord, 1); 
////		                    //에러 메시지
////		                    //
////	                    }
////	                    
////	                    
////	                    //적치BED의 앞쪽 DATA NULL CHECK
////	                    //실적위치Bed Left 2단 Check 		적치상태를 사용불가
////                    	setRecord = JDTORecordFactory.getInstance().create() ;
////                    	//실적위치 Bed -1 = 실적위치Bed 왼쪽 2단
////	                    String szStkBedNo =ydDaoUtils.stringPlusInt(szYD_UP_WR_LOC.substring(6,8), -1);
////	                    setRecord.setField("YD_STK_COL_GP",       szYD_UP_WR_LOC.substring(0,6));    
////	                    setRecord.setField("YD_STK_BED_NO",       szStkBedNo); 
////	                    setRecord.setField("YD_STK_LYR_NO",       "002") ;
////	                    setRecord.setField("YD_STK_LYR_MTL_STAT", "X");
////	                    setRecord.setField("STL_NO",              "");
////	                    intRtnVal = this.Y5UpdYdStklyrCoil(setRecord, 1); 
////	                    if(intRtnVal>0){
////	
////		                    //실적위치Bed Left 2단 Check 		적치상태를 사용불가
////	                    	setRecord = JDTORecordFactory.getInstance().create() ;
////	                    	//실적위치 Bed -1 = 실적위치Bed 왼쪽 2단
////		                    szStkBedNo =ydDaoUtils.stringPlusInt(szYD_UP_WR_LOC.substring(6,8), -1);
////		                    setRecord.setField("YD_STK_COL_GP",       szYD_UP_WR_LOC.substring(0,6));    
////		                    setRecord.setField("YD_STK_BED_NO",       szStkBedNo); 
////		                    setRecord.setField("YD_STK_LYR_NO",       "002") ;
////		                    setRecord.setField("YD_STK_LYR_MTL_STAT", "X");
////		                    setRecord.setField("STL_NO",              "");
////		                    intRtnVal = this.Y5UpdYdStklyrCoil(setRecord, 1); 
////		                    //에러 메시지
////		                    //
//// 
////	                    }
//	                    
//	                    
//                    	setRecord = JDTORecordFactory.getInstance().create();
//                    	
//	                    setRecord.setField("YD_STK_COL_GP",       szYD_UP_WR_LOC.substring(0,6));    
//	                    setRecord.setField("YD_STK_BED_NO",       szYD_UP_WR_LOC.substring(6,8));   
//                        //적치단 설정
//                        szStkLyr =ydDaoUtils.stringPlusInt(szYD_UP_WR_LAYER, i);
//                        setRecord.setField("YD_STK_LYR_NO",       	 szStkLyr) ;
//                        setRecord.setField("STL_NO",             	 "");
//                        setRecord.setField("YD_STK_LYR_MTL_STAT", 	 "E");
//
////==========================================================================================                    
////                      기준이기 때문에 클리어 되면 안됨
////                      2009.09.25 권오창
////                        
////                      setRecord.setField("YD_STK_LYR_XAXIS",       "");
////                      setRecord.setField("YD_STK_LYR_YAXIS",       "");
////                      setRecord.setField("YD_STK_LYR_ZAXIS",       "");
////==========================================================================================                    
//
//                        intRtnVal = this.Y5UpdYdStklyrCoil(setRecord, 1);  //적치단의 재료정보 Clear
//                        if(intRtnVal <= 0) {
//                        	return intRtnVal ;
//                        }
//                        //에러 메시지
//                        //
//	                    
//	                    
//	                    intRtnVal = 1 ;
//                    }
//   
//                }
//                
//                
//                
//                //권하 지시위치 Clear
//                if(intGp == 1) {
//                	String szYD_DN_WO_LOC             = 	  ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WO_LOC") ;
//                    String szYD_DN_WO_LAYER           = 	  ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WO_LAYER") ;
//                    
//                    //권상 실적위치가 2단 일 경우 
//                    setRecord.setField("YD_STK_COL_GP",       szYD_DN_WO_LOC.substring(0,6));    
//                    setRecord.setField("YD_STK_BED_NO",       szYD_DN_WO_LOC.substring(6,8));   
//                    //적치단 설정
//                    szStkLyr =ydDaoUtils.stringPlusInt(szYD_DN_WO_LAYER, i);
//                    setRecord.setField("YD_STK_LYR_NO",       szStkLyr) ;
//
////==========================================================================================                    
////                  기준이기 때문에 클리어 되면 안됨
////                  2009.09.25 권오창
////                    
////                  setRecord.setField("YD_STK_LYR_XAXIS",       "") ;
////                  setRecord.setField("YD_STK_LYR_YAXIS",       "") ;
////                  setRecord.setField("YD_STK_LYR_ZAXIS",       "") ;
////==========================================================================================                    
//                    
//                    intRtnVal = this.Y5UpdYdStklyrCoil(setRecord, 1);  //적치단의 재료정보 Clear
//                    
//                    //권상 실적위치가 1단 일 경우
//                    if (szYD_DN_WO_LAYER == "001") {
//	                    setRecord.setField("YD_STK_COL_GP",       szYD_DN_WO_LOC.substring(0,6));    
//	                    setRecord.setField("YD_STK_BED_NO",       szYD_DN_WO_LOC.substring(6,8));   
//
//	                    
////	                    //실적위치Bed Right 2단 Check			적치상태를 사용불가
////	                    setRecord = JDTORecordFactory.getInstance().create();
////	                    setRecord.setField("YD_STK_COL_GP",       szYD_DN_WO_LOC.substring(0,6));    
////	                    setRecord.setField("YD_STK_BED_NO",       szYD_DN_WO_LOC.substring(6,8));  
////	                    setRecord.setField("YD_STK_LYR_NO",       "002") ;
////	                    setRecord.setField("YD_STK_LYR_MTL_STAT", "X");
////	                    setRecord.setField("STL_NO",              "");
////	                    intRtnVal = this.Y5UpdYdStklyrCoil(setRecord, 1); 
////                    	
////	                    
////	                    //실적위치Bed Left 2단 Check 		적치상태를 사용불가
////                    	setRecord = JDTORecordFactory.getInstance().create();
////                    	//실적위치 Bed -1 = 실적위치Bed 왼쪽 2단
////	                    String szStkBedNo =ydDaoUtils.stringPlusInt(szYD_DN_WO_LOC.substring(6,8), -1);
////	                    setRecord.setField("YD_STK_COL_GP",       szYD_DN_WO_LOC.substring(0,6));    
////	                    setRecord.setField("YD_STK_BED_NO",       szStkBedNo); 
////	                    setRecord.setField("YD_STK_LYR_NO",       "002") ;
////	                    setRecord.setField("YD_STK_LYR_MTL_STAT", "X");
////	                    setRecord.setField("STL_NO",              "");
////	                    intRtnVal = this.Y5UpdYdStklyrCoil(setRecord, 1); 
//                    }
//                    	
//                }
//                    
//                //권하처리값 리턴
//                if(intRtnVal <= 0) {
//                	return intRtnVal ;
//                }
//                
//                getRecSet.next();
//                getRecord = getRecSet.getRecord();
//            } //end of for
//    		
//    		
//		}catch(Exception e){
//			System.out.println("Error : "+ e.getLocalizedMessage());
//			intRtnVal = -1 ;
//			return intRtnVal;
//	    }//end of try~catch
//		
//		intRtnVal = 1 ;
//		return  intRtnVal;
//    }//end of Y5ClearYdStklyrCoil()
    
    
    
    
//    /**
//     * 오퍼레이션명 : 적치단 등록
//     *  
//     * @param  ● getRecSet, intGp
//     * @return ● intRtnVal
//     * @throws ● JDTOException
//     */
//    public int Y5RegYdStklyrCoil (JDTORecordSet getRecSet, int intGp)throws JDTOException {
//    	//getRecSet의 첫번째 레코드값
//    	JDTORecord getRecord 			= JDTORecordFactory.getInstance().create();
//    	//업데이트 data 셋팅
//    	JDTORecord setRecord 			= JDTORecordFactory.getInstance().create();
//    	
//    	//outRecSet의 첫번째 레코드값
//    	JDTORecord outRecord 			= JDTORecordFactory.getInstance().create();
//    	//적치단 조회
//    	JDTORecordSet outRecSet 		= JDTORecordFactory.getInstance().createRecordSet("temp");
//    	
//    	
//    	String szMsg 					= "" ;
//        String szMethodName				= "Y5RegYdStklyrCoil" ;
//        int intRtnVal 					= 0 ;
//        String szStkBedNo				= "" ;
//        
//    	//JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
//        String szYdWbookId 				= "" ;
//    	
//    	try{
//    		int rowsize = getRecSet.size();
//            
//    		getRecSet.first();
//    		getRecord 	= getRecSet.getRecord();
//
//    		szYdWbookId = ydDaoUtils.paraRecChkNull(getRecord,"YD_WBOOK_ID") ;
//        	for(int i=0; i<rowsize; i++) {
//        		//권하 실적위치 등록
//        		String szYD_DN_WR_LOC	   = ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LOC") ;
//        		String szYD_DN_WR_LAYER	   = ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LAYER") ;
//        		String szSTL_NO	 		   = ydDaoUtils.paraRecChkNull(getRecord,"STL_NO") ;
//        		
//        		
//        		setRecord.setField("YD_STK_COL_GP",       	szYD_DN_WR_LOC.substring(0,6));   
//        		setRecord.setField("YD_STK_BED_NO",       	szYD_DN_WR_LOC.substring(6,8));               
//                String szStkLyr = ydDaoUtils.stringPlusInt(szYD_DN_WR_LAYER, i);
//                setRecord.setField("YD_STK_LYR_NO", 			szStkLyr) ;
//                setRecord.setField("STL_NO",              	szSTL_NO);                            
//                setRecord.setField("YD_STK_LYR_MTL_STAT", 	"C");           
//                setRecord.setField("YD_STK_LYR_XAXIS",      ydDaoUtils.paraRecChkNull(getRecord,"YD_STK_LYR_XAXIS")) ;
//                setRecord.setField("YD_STK_LYR_YAXIS",      ydDaoUtils.paraRecChkNull(getRecord,"YD_STK_LYR_YAXIS")) ;
//                setRecord.setField("YD_STK_LYR_ZAXIS",      ydDaoUtils.paraRecChkNull(getRecord,"YD_STK_LYR_ZAXIS")) ;
//                //적치단dao를 호출해서 업데이트를 한다.
//                intRtnVal = this.Y5UpdYdStklyrCoil(setRecord, 0); 
//    	        switch (intRtnVal) {
//		        	case 0	:
//		                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
//		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
//		                return intRtnVal;
//		        	case -1	:
//		                szMsg = "dup_val_on_index!!!, ErrorCode:" + intRtnVal;
//		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
//		                return intRtnVal;
//		        	case -2	:
//		                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
//		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
//		                return intRtnVal;
//		        	case -3	:
//		                szMsg = "execution failed!!!, ErrorCode:" + intRtnVal;
//		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
//		                return intRtnVal;
//		        }
//
//    	        if(szStkLyr.equals("001")) {
//    	        	
//                    //실적위치의 좌측 Bed 1단 Select
//    	        	outRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
//                    setRecord = JDTORecordFactory.getInstance().create();
//                    szStkBedNo = "0" + ( (Integer.parseInt(szYD_DN_WR_LOC.substring(6,8)) - 1 ) );
//                    setRecord.setField("YD_STK_COL_GP",       szYD_DN_WR_LOC.substring(0,6));    
//                    setRecord.setField("YD_STK_BED_NO",       szStkBedNo);  
//                    setRecord.setField("YD_STK_LYR_NO",       "001") ;
//                    //적치단 상태조회		:적치불가인지 아닌지 알기위해서...
//                    intRtnVal = this.Y5GetYdStklyrCoil(setRecord, outRecSet, 0);
//                    //에러 메시지
//                    outRecSet.first() ;
//                    outRecord = outRecSet.getRecord() ;
//                    
//                    //적치불가라면 적치가능으로 업데이트
//                    if(outRecord.getFieldString("YD_STK_LYR_MTL_STAT").equals("C")) {
//
//                    	//실적위치 좌측 Bed 2단 Select	
//                    	outRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
//                        setRecord = JDTORecordFactory.getInstance().create();
//                        szStkBedNo = "0" + ( (Integer.parseInt(szYD_DN_WR_LOC.substring(6,8)) - 1 ) );
//                        setRecord.setField("YD_STK_COL_GP",       szYD_DN_WR_LOC.substring(0,6));    
//                        setRecord.setField("YD_STK_BED_NO",       szStkBedNo);  
//                        setRecord.setField("YD_STK_LYR_NO",       "002") ;
//                        //적치단 상태조회		:적치불가인지 아닌지 알기위해서...
//                        intRtnVal = this.Y5GetYdStklyrCoil(setRecord, outRecSet, 0);
//                        //에러 메시지
//                        outRecSet.first() ;
//                        outRecord = outRecSet.getRecord() ;
//                        
//                        //적치불가라면 적치가능으로 업데이트
//                        if(outRecord.getFieldString("YD_STK_LYR_MTL_STAT").equals("X")) {
//                        	//실적위치Bed 좌측 Bed 2단 Check 		적치상태를 적치가능
//                        	outRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
//    	                	setRecord = JDTORecordFactory.getInstance().create() ;
//    	                	//실적위치 Bed -1 = 실적위치Bed 왼쪽 2단
//    	                	szStkBedNo = "0" + ( (Integer.parseInt(szYD_DN_WR_LOC.substring(6,8)) - 1 ) );
//    	                    setRecord.setField("YD_STK_COL_GP",       szYD_DN_WR_LOC.substring(0,6));    
//    	                    setRecord.setField("YD_STK_BED_NO",       szStkBedNo); 
//    	                    setRecord.setField("YD_STK_LYR_NO",       "002") ;
//    	                    setRecord.setField("YD_STK_LYR_MTL_STAT", "E");
//                        	intRtnVal = this.Y5UpdYdStklyrCoil(setRecord, 1); 
//                        	//에러 메시지
//                        	//
//                        }
//                    	
//                    }
//    	        	
//                    
//                    //실적위치의 우측 Bed Select
//                    outRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
//                    setRecord = JDTORecordFactory.getInstance().create();
//                    szStkBedNo = "0" + ( (Integer.parseInt(szYD_DN_WR_LOC.substring(6,8)) + 1 ) );
//                    setRecord.setField("YD_STK_COL_GP",       szYD_DN_WR_LOC.substring(0,6));    
//                    setRecord.setField("YD_STK_BED_NO",       szStkBedNo);  
//                    setRecord.setField("YD_STK_LYR_NO",       "001") ;
//                    //적치단 상태조회		:적치불가인지 아닌지 알기위해서...
//                    intRtnVal = this.Y5GetYdStklyrCoil(setRecord, outRecSet, 0);
//                    //에러 메시지
//                    outRecSet.first() ;
//                    outRecord = outRecSet.getRecord() ;
//                    
//                    //적치불가라면 적치가능으로 업데이트
//                    if(outRecord.getFieldString("YD_STK_LYR_MTL_STAT").equals("C")) {
//                    
//                    	//실적위치Bed 왼쪽 2단 Select
//                    	outRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
//                    	setRecord = JDTORecordFactory.getInstance().create() ;
//                    	//실적위치 Bed -1 = 실적위치Bed 왼쪽 2단
//       
//                        setRecord.setField("YD_STK_COL_GP",       szYD_DN_WR_LOC.substring(0,6));    
//                        setRecord.setField("YD_STK_BED_NO",       szYD_DN_WR_LOC.substring(6,8)); 
//                        setRecord.setField("YD_STK_LYR_NO",       "002") ;
//                        
//                        //적치단 상태조회		: 적치불가인지 아닌지 알기위해서...
//                        intRtnVal = this.Y5GetYdStklyrCoil(setRecord, outRecSet, 0);
//                        //에러 메시지
//                        outRecSet.first() ;
//                        outRecord = outRecSet.getRecord() ;
//                        //적치불가라면 적치가능으로 업데이트
//                       
//                        if(outRecord.getFieldString("YD_STK_LYR_MTL_STAT").equals("X")) {
//                        	//실적위치Bed 왼쪽 2단 Check 		적치상태를 적치가능
//                        	outRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
//    	                	setRecord = JDTORecordFactory.getInstance().create() ;
//    	                	//실적위치 Bed -1 = 실적위치Bed 왼쪽 2단
//                            setRecord.setField("YD_STK_COL_GP",       szYD_DN_WR_LOC.substring(0,6));    
//                            setRecord.setField("YD_STK_BED_NO",       szYD_DN_WR_LOC.substring(6,8)); 
//                            setRecord.setField("YD_STK_LYR_NO",       "002") ;
//    	                    setRecord.setField("YD_STK_LYR_MTL_STAT", "E");
//                        	intRtnVal = this.Y5UpdYdStklyrCoil(setRecord, 1); 
//                        }
//                    	
//                    }
//
//    	        }
//    	        
//    	        intRtnVal = this.Y5SetYdWrkplnsimulationCoil(getRecord, 1) ;
//    	        if(intRtnVal <= 0 ) return intRtnVal;
//    	        //에러 메시지
//    	        
//    	        //차량 하차작업 일 경우			공통테이블에 진도코드를 갱신한다.
//    	        if(ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC").substring(2, 4).equals("PT")
//                		|| ydDaoUtils.paraRecChkNull(getRecord,"YD_UP_WR_LOC").substring(2, 4).equals("TR")){
//    	        	//진도코드 갱신
//    	        	intRtnVal = this.Y5SetProgCodeCoil(getRecord) ;
//    	        	//에러 메시지
//    	        }
//    	        
//    	        
//    	        
//    	        //저장위치 갱신				공통테이블에 저장위치를 갱신한다.
//    	        setRecord 	= JDTORecordFactory.getInstance().create();
//    	        setRecord.setField("YD_SCH_CD",       		getRecord.getFieldString("YD_SCH_CD"));   
//    	        setRecord.setField("YD_GP",       			getRecord.getFieldString("YD_GP"));   
//    	        setRecord.setField("YD_BAY_GP",       		getRecord.getFieldString("YD_BAY_GP"));   
//    	        setRecord.setField("YD_EQP_GP",       		getRecord.getFieldString("YD_EQP_ID").substring(2, 4)); 
//    	        setRecord.setField("YD_STK_COL_NO",       	szYD_DN_WR_LOC.substring(0,6));   
//    	        setRecord.setField("YD_STK_BED_NO",       	szYD_DN_WR_LOC.substring(6,8)); 
//    	        setRecord.setField("YD_STK_LYR_NO", 		szStkLyr) ;
//    	        setRecord.setField("SLAB_NO",       		getRecord.getFieldString("STL_NO")); 
//    	        setRecord.setField("MSLAB_NO",       		getRecord.getFieldString("STL_NO")); 
//    	        
// //   	        intRtnVal = this.Y5SetYdStrLocCoil(setRecord) ;
// //   	        if(intRtnVal <= 0 ) return intRtnVal;
// //   	        System.out.println("NO3=========================================================");
//    	        
//                getRecSet.next();
//                getRecord = getRecSet.getRecord();
//        	
//        	}//end of for
//        	
//		}catch(Exception e){
//			System.out.println("Error : "+ e.getLocalizedMessage());
//			intRtnVal = -1 ;
//			return intRtnVal ;
//	    }//end of try~catch
//		
//		intRtnVal = 1 ;
//		return intRtnVal ;
//    
//    }//end of Y5RegYdStklyrCoil()
    
    
    
    

    
   
//    /**
//     * 오퍼레이션명 : 적치단 Select
//     *  
//     * @param  ● msgRecord, outRecSet, intGp
//     * @return ● intRtnVal
//     * @throws ● JDTOException
//     */
//    public int Y5GetYdStklyrCoil (JDTORecord msgRecord, JDTORecordSet outRecSet, int intGp) throws JDTOException {
//    	
//    	YdStkLyrDao ydStklyrDao = new YdStkLyrDao();
//    	
//    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
//    	
//    	int intRtnVal 			= 0 ;
//    	
//        try{
//        	
//	        intRtnVal = ydStklyrDao.getYdStklyr(msgRecord, getRecSet, intGp);  
//	        outRecSet.addAll(getRecSet);
//
//        }catch(Exception e){
//			System.out.println("Error : "+ e.getLocalizedMessage());
//			return -1 ;
//        }//end of try~catch
//
//        return intRtnVal ;
//    	
//    }//end of Y5GetYdStklyrCoil()
    
    
    
    
    
//    /**
//     * 오퍼레이션명 : 적치단 Update
//     *  
//     * @param  ● msgRecord, intGp
//     * @return ● intRtnVal
//     * @throws ● JDTOException
//     */
//    public int Y5UpdYdStklyrCoil (JDTORecord msgRecord, int intGp) throws JDTOException {
//    	YdStkLyrDao ydStklyrDao = new YdStkLyrDao();
//    	
//    	int intRtnVal = 0 ;
//        
//        try{
//        	
//	    	intRtnVal = ydStklyrDao.updYdStklyr(msgRecord, intGp);  //적치단의 재료정보 Clear
//	    	
//		}catch(Exception e){
//			System.out.println("Error : "+ e.getLocalizedMessage());
//			return -3 ;
//	    }//end of try~catch
//		
//		return intRtnVal ;
//		
//    }//end of Y5UpdYdStklyrCoil
	

    
    
    
    
//    /**
//     * 오퍼레이션명 : 작업예약 Update
//     *  
//     * @param  ● msgRecord, intGp
//     * @return ● intRtnVal
//     * @throws ● JDTOException
//     */
//    public int Y5UpdYdWrkbookCoil (JDTORecord msgRecord, int intGp) throws JDTOException {
//    	YdWrkbookDao ydWrkbookDao   = new YdWrkbookDao(); 
//    	
//    	int intRtnVal = 0 ;
//        
//        try{
//        	
//            intRtnVal = ydWrkbookDao.updYdWrkbook(msgRecord, intGp);
//
//		}catch(Exception e){
//			System.out.println("Error : "+ e.getLocalizedMessage());
//			intRtnVal = -4 ;
//			return intRtnVal ;
//	    }//end of try~catch
//		
//		return intRtnVal ;
//    }//end of Y5UpdYdWrkbookCoil
    
    
    
    
    
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////   대차관련 METHOD   /////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    /**
//     * 오퍼레이션명 : 대차 Setting
//     *  
//     * @param  ● inRecordSet, intGp
//     * @return ● intRtnVal
//     * @throws ● JDTOException
//     */
//    public int Y5SetYdTcarCoil (JDTORecordSet inRecordSet, int intGp) throws JDTOException{
//    	YdTcarSchDao ydTcarschDao = new YdTcarSchDao();//Data Setting
//    	JDTORecord 	  setRecord 			= JDTORecordFactory.getInstance().create();
//    	JDTORecord 	  getRecord 			= JDTORecordFactory.getInstance().create();
//    	JDTORecord 	  getTcarRecord 		= JDTORecordFactory.getInstance().create();
//    	
//    	JDTORecordSet outRecSet  			= JDTORecordFactory.getInstance().createRecordSet("temp");
//    	int intRtnVal 						= 0 ;
//    	
//    	String szMethodName 				= "Y5etYdTcarCoil" ;
//    	String szMsg 						= "" ;
//    	
//    	//대차 스케줄 ID
//    	String szYD_TCAR_SCH_ID 			= "" ;
//    	
//    	try{
//	    	// 크레인스케줄 Data
//	    	inRecordSet.first();
//	    	getRecord = inRecordSet.getRecord();
//
//			setRecord.setField("YD_BOOK_ID"	, ydDaoUtils.paraRecChkNull(getRecord, "YD_WBOOK_ID")) ;
//	    	setRecord.setField("STL_NO"		, ydDaoUtils.paraRecChkNull(getRecord, "STL_NO")) ;
//	
//	    	
//	    	//하차 작업 예약 ID	Setting
//	    	if(intGp == 0) {
//	    		/*com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.getYdTcarschUD*/
//		    	intRtnVal = ydTcarschDao.getYdTcarsch(setRecord, outRecSet, 201);
//		    	if (intRtnVal <= 0) {
//		            throw new DAOException("<Y5etYdTcarCoil> " +  "상하차 작업예약 ID로 대차스케줄 조회" + intRtnVal + "건수");
//		    	}
//		    	
//		    //상차 작업 예약 ID	Setting
//	    	}else if(intGp == 1) {
////				setRecord.setField("YD_CARLD_WRK_BOOK_ID", getRecord.getFieldString("YD_WBOOK_ID")) ;
////		    	setRecord.setField("YD_CARUD_WRK_BOOK_ID", "") ;
//	    	}
//	    	
//	    	
////	    	// 상하차 작업예약 ID로 대차스케줄 조회
////	    	intRtnVal = this.Y5GetYdTcarschCoil(setRecord, outRecSet, 1) ;
////	    	if (intRtnVal <= 0) return intRtnVal ;
//	    	
//	    	
//	    	// 대차스케줄 Data
//	    	outRecSet.first() ;
//	    	getTcarRecord = outRecSet.getRecord() ;
//	    	// 대차스케줄 ID를 추출한다
//	    	szYD_TCAR_SCH_ID = ydDaoUtils.paraRecChkNull(getTcarRecord,"YD_TCAR_SCH_ID");
//	    	
//	    	//setRecord 초기화
//	    	setRecord 	  = JDTORecordFactory.getInstance().create();
//	    	int szRowSize = inRecordSet.size(); 
//	    	
//	    	
//
//	    	// 권상한 재료만큼 대차스케줄 ID로 대차이송재료에 가서 권상한 재료를 삭제유무를 "Y"로 한다.(Clear)  
//
//	    	for(int i = 0; i < szRowSize; i++){
//		    	
//	    		// 대차스케줄 ID로 대차이송재료에 가서 권상한 재료를 삭제유무를 "Y"로 한다.(Clear)        
//	    		setRecord.setField("YD_TCAR_SCH_ID",       	szYD_TCAR_SCH_ID);
//	    		setRecord.setField("STL_NO",              	ydDaoUtils.paraRecChkNull(getRecord, "STL_NO")); 
//	    		
//	    		//대차 이송재료 등록 (하차 )
//	    		if(intGp == 0) {
//		    		setRecord.setField("DEL_YN",       			"Y");
//		    		/*com.inisteel.cim.yd.dao.ydtcarftmvmtldao.YdTcarftmvmtlDao.updYdTcarftmvmtl*/
//		    		intRtnVal = this.Y5UpdTcarftmvmtlCoil(setRecord, 0) ;
//			    	switch (intRtnVal) {
//			        	case 0	:
//			                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
//			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
//			                return intRtnVal;
//			        	case -1	:
//			                szMsg = "dup_val_on_index!!!, ErrorCode:" + intRtnVal;
//			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
//			                return intRtnVal;
//			        	case -2	:
//			                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
//			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
//			                return intRtnVal;
//			        	case -3	:
//			                szMsg = "execution failed!!!, ErrorCode:" + intRtnVal;
//			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
//			                return intRtnVal;
//			        }
//			    	
//			    //대차 이송재료 등록 (상차 )	
//	    		}else if(intGp == 1) {
//		    		setRecord.setField("DEL_YN",       			"N");
//		    		setRecord.setField("YD_STK_BED_NO",       	ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LOC").substring(6,8)) ;
//		    		setRecord.setField("YD_STK_LYR_NO",       	"001") ;
//		    		/*com.inisteel.cim.yd.dao.ydtcarftmvmtldao.YdTcarftmvmtlDao.insYdTcarftmvmtl*/
//		    		intRtnVal = this.Y5InsYdTcarftmvmtlCoil(setRecord) ;
//			    	switch (intRtnVal) {
//			        	case -2	:
//			                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
//			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
//			                return intRtnVal;
//			        	case -3	:
//			                szMsg = "execution failed!!!, ErrorCode:" + intRtnVal;
//			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
//			                return intRtnVal;
//			        }
//	    		}
//		    	
//	    		inRecordSet.next() ;
//		    	getRecord = inRecordSet.getRecord();
//	    	}
//	    	
//		}catch(Exception e){
//			System.out.println("Error : "+ e.getLocalizedMessage());
//			return -1 ;
//		}//end of try~catch
//    	
//		return 1 ;
//    	
//    }//end of Y5etYdTcarCoil()
    
//   /**
//     * 오퍼레이션명 : 대차 스케줄 Select
//     *  
//     * @param  ● msgRecord, outRecset, intGp
//     * @return ● intRtnVal
//     * @throws ● 
//     */
//    public int Y5GetYdTcarschCoil (JDTORecord msgRecord, JDTORecordSet outRecset, int intGp){
//    	YdTcarSchDao ydTcarschDao = new YdTcarSchDao();
//    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
//    	
//    	int intRtnVal 			= 0 ;
//        
//        try{
//        	
//	        intRtnVal = ydTcarschDao.getYdTcarsch(msgRecord, getRecSet, intGp);
//	        if (intRtnVal <= 0) {
//	        	return intRtnVal;
//	        }
//	        outRecset.addAll(getRecSet)  ;  
//	        
//        }catch(Exception e){
//			System.out.println("Error : "+ e.getLocalizedMessage());
//			return -3 ;
//        }
//        return intRtnVal ;
//    }//end of Y5GetYdTcarschCoil
    
//   /**
//     * 오퍼레이션명 : 대차이송재료 Update
//     *  
//     * @param inRecord, intGp
//     * @return intRtnVal
//     * @throws JDTOException
//     */
//    public int Y5UpdTcarftmvmtlCoil (JDTORecord inRecord, int intGp) throws JDTOException {
//    	YdTcarFtmvMtlDao ydTcarftmvmtlDao   = new YdTcarFtmvMtlDao(); 
//    	
//    	int intRtnVal = 0 ;
//        
//        try{
//        	
//            intRtnVal = ydTcarftmvmtlDao.updYdTcarftmvmtl(inRecord, intGp) ;
//            
//		}catch(Exception e){
//			System.out.println("Error : "+ e.getLocalizedMessage());
//			intRtnVal = -3 ;
//			return intRtnVal ;
//	    }	
//		
//		return intRtnVal ;
//    }//end of Y5UpdTcarftmvmtlCoil
    
//   /**
//     * 오퍼레이션명 : 대차이송재료 Insert
//     *  
//     * @param msgRecord
//     * @return intRtnVal
//     * @throws 
//     */
//    public int Y5InsYdTcarftmvmtlCoil(JDTORecord msgRecord){
//    	YdTcarFtmvMtlDao ydTcarftmvmtlDao = new YdTcarFtmvMtlDao();
//    	
//    	int intRtnVal 			= 0 ;
//
//        
//        try{
//        	
//        	intRtnVal = ydTcarftmvmtlDao.insYdTcarftmvmtl(msgRecord);
//        	if(intRtnVal == -2) return intRtnVal ;
//        	
//        }catch(Exception e){
//			System.out.println("Error : "+ e.getLocalizedMessage());
//			return -3 ;
//        }
//        return intRtnVal ;
//    	
//    }//end of Y5InsYdTcarftmvmtlCoil
    
    
//    /**
//     * 오퍼레이션명 : 대차스케줄 Update
//     *  
//     * @param msgRecord, intGp
//     * @return intRtnVal
//     * @throws 
//     */
//    public int Y5UpdYdTcarschCoil (JDTORecord msgRecord, int intGp){
//    	YdTcarSchDao ydTcarschDao = new YdTcarSchDao();
//    	
//    	int intRtnVal 			= 0 ;
//        
//        try{
//        	
//        	intRtnVal = ydTcarschDao.updYdTcarsch(msgRecord, intGp);
//	        if (intRtnVal <= 0) {
//	        	return -2;
//	        }
//	        
//        }catch(Exception e){
//			System.out.println("Error : "+ e.getLocalizedMessage());
//        }//end of try~catch
//        
//        
//        return 1 ;
//    	
//    }//end of Y5UpdYdTcarschCoil
    
   
    
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////    차량관련 METHOD    ///////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
//    /**
//     * 오퍼레이션명 : 차량 Setting
//     *  
//     * @param inRecordSet, intGp
//     * @return intRtnVal
//     * @throws JDTOException
//     */
//    public int Y5SetYdCarCoil (JDTORecordSet inRecordSet, int intGp) throws JDTOException{
//    	YdCarSchDao ydCarschDao = new YdCarSchDao();
//    	//Data Setting
//    	JDTORecord 	  setRecord 			= JDTORecordFactory.getInstance().create();
//    	
//    	//data를 받음
//    	JDTORecord 	  getRecord 			= JDTORecordFactory.getInstance().create();
//    	
//    	//차량 스케줄 레코드셋의 레코드값을 받음
//    	JDTORecord 	  getTcarRecord 		= JDTORecordFactory.getInstance().create();
//    	
//    	//차량 스케줄의 레코드셋
//    	JDTORecordSet outRecSet  			= JDTORecordFactory.getInstance().createRecordSet("temp");
//    	
//    	String szMethodName 				= "Y5SetYdCarCoil" ;
//    	String szMsg 						= "" ;
//    	
//    	int  intRtnVal = 0 ;
//    	long lngYD_MTL_WT  = 0 ;
//    	int  intYD_MTL_SH  = 0 ;
//    	long lngYD_EQP_WRK_WT  = 0 ;
//    	int  intYD_EQP_WRK_SH  = 0 ;
//    	
//    	//차량 스케줄 ID
//    	String szYD_CAR_SCH_ID = "" ;
//    	
//    	try{
//    		
//	    	// 크레인스케줄 Data
//	    	inRecordSet.first();
//	    	getRecord = inRecordSet.getRecord();
//
//	    	
//	    	//하차 작업 예약 ID	Setting
//	    	if(intGp == 0) {
//				setRecord.setField("YD_CARUD_WRK_BOOK_ID", getRecord.getFieldString("YD_WBOOK_ID")) ;
//		    	setRecord.setField("YD_CARLD_WRK_BOOK_ID", "") ;
//		    	
//		    //상차 작업 예약 ID	Setting
//	    	}else if(intGp == 1) {
//				setRecord.setField("YD_CARLD_WRK_BOOK_ID", getRecord.getFieldString("YD_WBOOK_ID")) ;
//		    	setRecord.setField("YD_CARUD_WRK_BOOK_ID", "") ;
//	    	}
//	    	
//	    	
//	    	// 상하차 작업예약 ID로 대차스케줄 조회
//	    	intRtnVal = this.Y5GetYdCarschCoil(setRecord, outRecSet, 3) ;
//	    	if (intRtnVal <= 0) return -1 ;
//	    	
//	    	
//	    	// 차량스케줄 Data
//	    	outRecSet.first() ;
//	    	getTcarRecord = outRecSet.getRecord() ;
//	    	// 차량스케줄 ID를 추출한다
//	    	szYD_CAR_SCH_ID  = ydDaoUtils.paraRecChkNull(getTcarRecord,"YD_CAR_SCH_ID");
//	    	lngYD_EQP_WRK_WT = ydDaoUtils.paraRecChkNullLong(getTcarRecord,"YD_EQP_WRK_WT");
//	    	intYD_EQP_WRK_SH = ydDaoUtils.paraRecChkNullInt(getTcarRecord,"YD_EQP_WRK_SH");
//	    	
//	    	
//	    	//setRecord 초기화
//	    	setRecord 			= JDTORecordFactory.getInstance().create();
//	    	int szRowSize = inRecordSet.size(); 
//	    	
//	    	
//
//	    	// 권상한 재료만큼 차량스케줄 ID로 차량이송재료에 가서 권상한 재료를 삭제유무를 "Y"로 한다.(Clear)  
//
//	    	for(int i = 0; i < szRowSize; i++){
//	    		intYD_MTL_SH = i+1;
//	    		lngYD_MTL_WT = lngYD_MTL_WT + ydDaoUtils.paraRecChkNullLong(getRecord,"YD_MTL_WT");
//		    	// 차량스케줄 ID로 차량이송재료에 가서 권상한 재료를 삭제유무를 "Y"로 한다.(Clear)        
//	    		setRecord.setField("YD_CAR_SCH_ID",       	szYD_CAR_SCH_ID);
//	    		setRecord.setField("STL_NO",              	ydDaoUtils.paraRecChkNull(getRecord, "STL_NO")); 
//	    		
//	    		//차량 이송재료 등록 (하차 )
//	    		if(intGp == 0) {
//		    		setRecord.setField("DEL_YN",       			"Y");
//		    		intRtnVal = this.Y5UpdCarftmvmtlCoil(setRecord, 0) ;
//			    	switch (intRtnVal) {
//			        	case 0	:
//			                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
//			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	  
//			                return intRtnVal;
//			        	case -1	:
//			                szMsg = "dup_val_on_index!!!, ErrorCode:" + intRtnVal;
//			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
//			                return intRtnVal;
//			        	case -2	:
//			                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
//			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
//			                return intRtnVal;
//			        	case -3	:
//			                szMsg = "execution failed!!!, ErrorCode:" + intRtnVal;
//			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
//			                return intRtnVal;
//			        	case -4	:
//			                szMsg = "Exception error!!!, ErrorCode:" + intRtnVal;
//			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
//			                return intRtnVal;
//			        }
//			    	
//			    //차량 이송재료 등록 (상차 )	
//	    		}else if(intGp == 1) {
//		    		setRecord.setField("DEL_YN",       			"N");
//		    		setRecord.setField("YD_STK_BED_NO",       	ydDaoUtils.paraRecChkNull(getRecord,"YD_DN_WR_LOC").substring(6,8)) ;
//		    		setRecord.setField("YD_STK_LYR_NO",       	"001") ;
//		    		intRtnVal = this.Y5InsYdCarftmvmtlCoil(setRecord) ;
//			    	switch (intRtnVal) {
//			        	case -2	:
//			                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
//			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
//			                return intRtnVal;
//			        	case -3	:
//			                szMsg = "Exception error!!!, ErrorCode:" + intRtnVal;
//			                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
//			                return intRtnVal;
//			        }
//	    		}
//		    	inRecordSet.next() ;
//		    	getRecord = inRecordSet.getRecord();
//	    	}
//	    	
//	    	if(intGp == 1) {
//    			//차량스케줄에 등록한다.
//    			lngYD_EQP_WRK_WT = lngYD_EQP_WRK_WT - lngYD_MTL_WT;
//    			intYD_EQP_WRK_SH = intYD_EQP_WRK_SH - intYD_MTL_SH;
//    	    	//setRecord 초기화
//    	    	setRecord 			= JDTORecordFactory.getInstance().create();
//	    		setRecord.setField("YD_CAR_SCH_ID",       	szYD_CAR_SCH_ID);
//	    		setRecord.setField("YD_EQP_WRK_WT",       	"" + lngYD_EQP_WRK_WT);
//	    		setRecord.setField("YD_EQP_WRK_SH",       	"" + intYD_EQP_WRK_SH);
//	    		
//	    		intRtnVal = ydCarschDao.updYdCarsch(setRecord, 0);
//	    		switch (intRtnVal) {
//		        	case 0	:
//		                szMsg = "data not found!!, ErrorCode:" + intRtnVal;
//		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);	  
//		                return intRtnVal;
//		        	case -1	:
//		                szMsg = "dup_val_on_index!!!, ErrorCode:" + intRtnVal;
//		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
//		                return intRtnVal;
//		        	case -2	:
//		                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
//		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	   
//		                return intRtnVal;
//		        	case -3	:
//		                szMsg = "execution failed!!!, ErrorCode:" + intRtnVal;
//		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
//		                return intRtnVal;
//		        	case -4	:
//		                szMsg = "Exception error!!!, ErrorCode:" + intRtnVal;
//		                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	 
//		                return intRtnVal;
//		        }
//    		}
//	    	
//		}catch(Exception e){
//			System.out.println("Error : "+ e.getLocalizedMessage());
//			return -1 ;
//		}//end of try~catch
//		
//    	return 1 ;
//    	
//    }//end of Y5SetYdCarCoil()

    
    
    
    
    
    
    
    
    
//    /**
//     * 오퍼레이션명 : 차량 스케줄 Select
//     *  
//     * @param msgRecord, outRecset, intGp
//     * @return intRtnVal
//     * @throws 
//     */
//    public int Y5GetYdCarschCoil (JDTORecord msgRecord, JDTORecordSet outRecset, int intGp){
//    	YdCarSchDao ydCarschDao = new YdCarSchDao();
//    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
//    	
//    	int intRtnVal 			= 0 ;
//        
//        try{
//        	
//        	intRtnVal = ydCarschDao.getYdCarsch(msgRecord, getRecSet, intGp);
//	        if (intRtnVal <= 0) return -2;
//	        
//	        outRecset.addAll(getRecSet)  ; 
//	        
//        }catch(Exception e){
//			System.out.println("Error : "+ e.getLocalizedMessage());
//			return -2 ;
//        }//end of try~catch
//        
//        return intRtnVal ;
//        
//    }//end of Y5GetYdCarschCoil
    
    
    
    
    
    
    
    
    
    
//    /**
//     * 오퍼레이션명 : 차량 이송재료 Update
//     *  
//     * @param inRecordSet, intGp
//     * @return intRtnVal
//     * @throws JDTOException
//     */
//    public int Y5UpdCarftmvmtlCoil (JDTORecord inRecord, int intGp) throws JDTOException {
//    	YdCarFtmvMtlDao ydCarftmvmtlDao   = new YdCarFtmvMtlDao(); 
//    	
//    	int intRtnVal = 0 ;
//        
//        try{
//        	
//            intRtnVal = ydCarftmvmtlDao.updYdCarftmvmtl(inRecord, intGp) ;
//		
//        }catch(Exception e){
//			System.out.println("Error : "+ e.getLocalizedMessage());
//			intRtnVal = -1 ;
//			return intRtnVal ;
//	    }//end of try~catch	
//		
//		return intRtnVal ;
//		
//    }//end of Y5UpdCarftmvmtlCoil
    

    
    
    
    
    
    
    
    
//    /**
//     * 오퍼레이션명 : 차량이송재료 Insert
//     *  
//     * @param msgRecord
//     * @return intRtnVal
//     * @throws 
//     */
//    public int Y5InsYdCarftmvmtlCoil(JDTORecord msgRecord){
//    	YdCarFtmvMtlDao ydCarftmvmtlDao = new YdCarFtmvMtlDao();
//    	
//    	int intRtnVal = 0 ;
//
//        
//        try{
//        	intRtnVal = ydCarftmvmtlDao.insYdCarftmvmtl(msgRecord);
//        	if(intRtnVal <= 0) return intRtnVal ;
//        	
//        }catch(Exception e){
//			System.out.println("Error : "+ e.getLocalizedMessage());
//			return -3 ;
//        }//end of try~catch
//        
//        return intRtnVal ;
//    	
//    }//end of Y5InsYdCarftmvmtlCoil


    
    
    
    
    
    
    
//    /**
//     * 오퍼레이션명 : 차량스케줄 Update
//     *  
//     * @param msgRecord, intGp
//     * @return getRecSet
//     * @throws 
//     */
//    public JDTORecordSet Y5UpdYdCarschCoil (JDTORecord msgRecord, int intGp){
//    	YdCarSchDao ydCarschDao = new YdCarSchDao();
//    	
//    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
//    	
//    	int intRtnVal 			= 0 ;
//        String szMsg            = "";
//        String szMethodName     = "Y5UpdYdCarschCoil";
//        
//        try{
//        	
//        	intRtnVal = ydCarschDao.updYdCarsch(msgRecord, intGp);
//	        if (intRtnVal <= 0) {
//	            if (intRtnVal == 0) {
//	                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	            } else if (intRtnVal == -2) {
//	                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	            }
//	        }
//        }catch(Exception e){
//			System.out.println("Error : "+ e.getLocalizedMessage());
//        }//end of try~catch
//        
//        
//        return getRecSet ;
//    	
//    }//end of Y5UpdYdCarschCoil


    
    
    
    
    
    
    
//    /**
//     * 오퍼레이션명 : 저장품 Update
//     *  
//     * @param msgRecord, intGp
//     * @return intRtnVal
//     * @throws 
//     */
//    public int Y5UpdPtCommCoil (JDTORecord msgRecord, int intGp){
//    	YdStockDao ydStockDao = new YdStockDao();
//    	
//    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
//    	
//    	int intRtnVal 			= 0 ;
//        String szMsg            = "";
//        String szMethodName     = "Y5UpdPtCommCoil";
//        
//        try{
//        	
//        	intRtnVal = ydStockDao.updPtComm_LOC(msgRecord, intGp);
//	        if (intRtnVal <= 0) {
//	            if (intRtnVal == 0) {
//	                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	            } else if (intRtnVal == -2) {
//	                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	            }
//	        }
//        }catch(Exception e){
//			System.out.println("Error : "+ e.getLocalizedMessage());
//        }//end of try~catch
//        
//        
//        return  intRtnVal;
//    	
//    }//end of Y5UpdPtCommCoil()
    
    
    
    
    
    
    
    
    
//    /**
//     * 오퍼레이션명 : 저장품 Select
//     *  
//     * @param msgRecord, outRecset, intGp
//     * @return intRtnVal
//     * @throws 
//     */
//    public int Y5GetYdStockCoil (JDTORecord msgRecord, JDTORecordSet outRecset, int intGp){
//    	YdStockDao ydStockDao = new YdStockDao();
//    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
//    	
//    	String szMsg			= "" ;
//    	String szMethodName		= "Y5GetYdStockCoil" ;
//    	
//    	int intRtnVal 			= 0 ;
//        
//        try{
//        	
//        	intRtnVal = ydStockDao.getYdStock(msgRecord, getRecSet, intGp);
//	        if (intRtnVal <= 0) {
//	            if (intRtnVal == 0) {
//	                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	            } else if (intRtnVal == -2) {
//	                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	            }
//	        }
//	        outRecset.addAll(getRecSet)  ; 
//	        
//        }catch(Exception e){
//			System.out.println("Error : "+ e.getLocalizedMessage());
//			return -3 ;
//        }//end of try~catch
//        
//        return intRtnVal ;
//        
//    }//end of Y5GetYdStockCoil()
    
    


    
    
    
    
    
    
//    /**
//     * 오퍼레이션명 : 작업계획 Simulation 삭제 Setting  
//     * @param msgRecord, intGp(1:상하차)
//     * @return intRtnVal
//     * @throws 
//     */
//    public int Y5SetYdWrkplnsimulationCoil (JDTORecord msgRecord, int intGp){
//    	YdWrkPlnSimulationDao ydWrkplnsimulationDao = new YdWrkPlnSimulationDao();
//    	//작업계획 Sim 조회 한 값
//    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
//    	//getRecSet의 첫번째 레코드 값을 저장
//    	JDTORecord 	  getRecord 			= JDTORecordFactory.getInstance().create();
//    	
//    	String szMsg			= "" ;
//    	String szMethodName		= "Y5SetYdWrkplnsimulationCoil" ;
//    	
//    	int intRtnVal 			= 0 ;
//        
//        try{
//        	//작업계획 Simulation Select				msgRecord에는 스케줄코드와 재료번호가 있음
//        	intRtnVal = this.Y5GetYdWrkplnsimulationCoil(msgRecord, getRecSet, intGp);
//	        if (intRtnVal <= 0) {
//	            if (intRtnVal == 0) {
//	                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	            } else if (intRtnVal == -2) {
//	                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	            }
//	            return intRtnVal ;
//	        }
//	        getRecSet.first();
//	        getRecord = getRecSet.getRecord();
//	        getRecord.setField("DEL_YN", "Y") ;
//	        getRecord.setField("MODIFIER", "YDSYSTEM") ;
//	        
//	        intRtnVal = this.Y5UpdYdWrkplnsimulationCoil(getRecord) ;
//	        if (intRtnVal <= 0) {
//	            if (intRtnVal == 0) {
//	                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	            } else if (intRtnVal == -2) {
//	                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	            }
//	            return intRtnVal ;
//	        }
//	        
//        }catch(Exception e){
//			System.out.println("Error : "+ e.getLocalizedMessage());
//			return -3 ;
//        }//end of try~catch
//        
//        return intRtnVal ;
//        
//    }//end of Y5SetYdWrkplnsimulationCoil()
    
    


    
    
   
    
    
//    /**
//     * 오퍼레이션명 : 작업계획 Simulation Select
//     *  
//     * @param msgRecord, outRecset, intGp(1:상하차)
//     * @return intRtnVal
//     * @throws 
//     */
//    public int Y5GetYdWrkplnsimulationCoil (JDTORecord msgRecord, JDTORecordSet outRecset, int intGp){
//    	YdWrkPlnSimulationDao ydWrkplnsimulationDao = new YdWrkPlnSimulationDao();
//    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
//    	
//    	String szMsg			= "" ;
//    	String szMethodName		= "Y5GetYdWrkplnsimulationCoil" ;
//    	
//    	int intRtnVal 			= 0 ;
//        
//        try{
//        	
//        	intRtnVal = ydWrkplnsimulationDao.getYdWrkplnsimulation(msgRecord, getRecSet, intGp);
//	        if (intRtnVal <= 0) {
//	            if (intRtnVal == 0) {
//	                szMsg = "no data found!!!, ErrorCode:" + intRtnVal;
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	            } else if (intRtnVal == -2) {
//	                szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	            }
//	            return intRtnVal ;
//	        }
//	        outRecset.addAll(getRecSet)  ; 
//	        
//        }catch(Exception e){
//			System.out.println("Error : "+ e.getLocalizedMessage());
//			return -3 ;
//        }//end of try~catch
//        
//        return intRtnVal ;
//        
//    }//end of Y5GetYdWrkplnsimulationCoil()
    
    
    
    
    
    
    
    
    
//    /**
//     * 오퍼레이션명 : 작업계획 Simulation Update
//     *  
//     * @param msgRecord
//     * @return intRtnVal
//     * @throws 
//     */
//    public int Y5UpdYdWrkplnsimulationCoil (JDTORecord msgRecord){
//    	YdWrkPlnSimulationDao ydWrkplnsimulationDao = new YdWrkPlnSimulationDao();
//    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
//    	
//    	String szMsg			= "" ;
//    	String szMethodName		= "Y5UpdYdWrkplnsimulationCoil" ;
//    	
//    	int intRtnVal 			= 0 ;
//        
//        try{
//        	
//        	intRtnVal = ydWrkplnsimulationDao.updYdWrkplnsimulationPlnIdAndLess(msgRecord);
//	        if (intRtnVal <= 0) {
//	            if (intRtnVal == -2) {
//	                szMsg = "parameter error	Error code:" + intRtnVal;
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	            } else if (intRtnVal == -3) {
//	                szMsg = "execution failed	Error code:" + intRtnVal;
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	            }
//	            return intRtnVal ;
//	        }
//	        
//        }catch(Exception e){
//			System.out.println("Error : "+ e.getLocalizedMessage());
//			return -3 ;
//        }//end of try~catch
//        
//        return intRtnVal ;
//        
//    }//end of Y5UpdYdWrkplnsimulationCoil()
    
    
    
    
    
    
    
    
    
//    /**
//     * 오퍼레이션명 : 진도코드갱신 Setting 
//     * @param msgRecord
//     * @return intRtnVal
//     * @throws 
//     */
//    public int Y5SetProgCodeCoil (JDTORecord msgRecord){
//    	
//    	
//    	//작업계획 Sim 조회 한 값
//    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
//    	//공통테이블 정보를 담기위한 값
//    	JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
//    	//getRecSet의 첫번째 레코드 값을 저장
//    	JDTORecord 	  getRecord 			= JDTORecordFactory.getInstance().create();
//    	//Update할 레코드 셋팅
//    	JDTORecord 	  setRecord 			= JDTORecordFactory.getInstance().create();
//    	
//    	String szCurrProgCd					= "" ;
//    	String szBefoProgCd					= "" ;
//    	
//    	String szMsg						= "" ;
//    	String szMethodName					= "getYdWrkplnsimulation" ;
//    	//재료품목 정의
//    	String szYdMtlItem					= "" ;
//    	//재료종류별 번호
//    	String szStlNo						= "" ;
//    	int intRtnVal 						= 0 ;
//        
//        try{
//        	
//        	//재료품목으로 비교하고 품목에따라 맞는 공통테이블을 조회한다.
//        	szYdMtlItem = msgRecord.getFieldString("YD_SCH_CD").substring(2, 4) ;
//        	szStlNo 	= msgRecord.getFieldString("STL_NO") ;
//        	
//        	if(szYdMtlItem.equals("BM")){
//        		//주편 공통
//        		msgRecord.setField("MSLAB_NO", szStlNo) ;
//        		intRtnVal = this.Y5GetYdStockCoil(msgRecord, getRecSet, 6);
//        		//에러 메시지
//        		//
//        		if(intRtnVal<0) return intRtnVal ;
//        	}else if (szYdMtlItem.equals("SM") || szYdMtlItem.equals("SG") || szYdMtlItem.equals("SZ")) {
//        		//슬라브 공통
//        		msgRecord.setField("SLAB_NO", szStlNo) ;
//        		intRtnVal = this.Y5GetYdStockCoil(msgRecord, getRecSet, 2);
//        		//에러 메시지
//        		//
//        		if(intRtnVal<0) return intRtnVal ;
//        	}
//        	
//        	getRecSet.first();
//        	getRecord = getRecSet.getRecord() ;
//        	//읽어온 값의 항목을 저장
//        	szCurrProgCd = getRecord.getFieldString("CURR_PROG_CD") ;
//        	szBefoProgCd = getRecord.getFieldString("BEFO_PROG_CD") ;
//        	
//        	//현재진도코드 = 이송지시대기'D'
//        	if(getRecord.getFieldString("CURR_PROG_CD").equals("D")) {
//        		if(getRecord.getFieldInt("ORD_YEOJAE_GP")== 1){
//        			if(getRecord.getFieldString("SCARFING_YN").equals("Y")) {
//        				//정정작업대기(작업대기)
//        				setRecord.setField("CURR_PROG_CD", "C") ;
//        			}else{
//        				//압연지시대(지시대기)
//        				setRecord.setField("CURR_PROG_CD", "B") ;
//        			}
//        		}else{
//        			//충당대기
//        			setRecord.setField("CURR_PROG_CD", "Z") ;
//        		}
//        	}
//        	//전 진도코드 = 현재 진도코드,   전전진도코드 = 전진도코드
//			setRecord.setField("BEFO_PROG_CD", 					szCurrProgCd) ;
//			setRecord.setField("BEFOBEFO_PROG_CD",  			szBefoProgCd) ; 
//			//현재시간
//			setRecord.setField("CURR_PROG_REG_DDTT", 			ydUtils.getCurDate("yyyyMMddHHmmss")) ;
//			setRecord.setField("BEFO_PROG_REG_DDTT",  			ydUtils.getCurDate("yyyyMMddHHmmss")) ; 
//			setRecord.setField("BEFOBEFO_PROG_REG_DDTT", 		ydUtils.getCurDate("yyyyMMddHHmmss")) ;
//
//			
//
//        	//업데이트 항목 추가해야함 ///////////////////////////////////////////////////////
//        	if(szYdMtlItem.equals("BM")){
//        		//주편 공통
//        		//구분자 설정 다시해야함!
//        		intRtnVal = this.Y5UpdPtCommCoil(msgRecord,  6);
//        		//에러 메시지
//        		//
//        		if(intRtnVal<0) return intRtnVal ;
//        	}else if (szYdMtlItem.equals("SM") || szYdMtlItem.equals("SG") || szYdMtlItem.equals("SZ")) {
//        		//슬라브 공통
//        		intRtnVal = this.Y5UpdPtCommCoil(msgRecord,  2);
//        		//에러 메시지
//        		//
//        		if(intRtnVal<0) return intRtnVal ;
//        	}
//        	
//        	
//        	
//        }catch(Exception e){
//			System.out.println("Error : "+ e.getLocalizedMessage());
//			return -3 ;
//        }//end of try~catch
//        
//        return intRtnVal ;
//        
//    }//end of setProgCodeCoil()
    
    
    
    
    
     
    
    
    
    
//	/**
//	 * 오퍼레이션명 : 설비상태 체크
//	 *  
//	 * @param   String szEqpId 설비ID
//	 * @return boolean true(설비사용가능), false(설비사용불가)
//	 * @throws JDTOException
//	 */
//	public String eqpStatCheck(String szEqpId)throws JDTOException  {
//		//메세지
//		String szMsg           = null;
//		//메소드명
//		String szMethodName    = "eqpStatCheck";		
//		//설비상태
//		String szYD_EQP_STAT   = null;
//		//야드설비작업Mode
//		String szYD_EQP_WRK_MODE	= null;
//		//레코드 선언
//		JDTORecord recPara     = null;
//		//레코드셋 선언
//		JDTORecordSet rsResult = null;	
//		
//		String szRtnMsg			= null;
//		
//		try {
//			//레코드 생성
//			recPara = JDTORecordFactory.getInstance().create();
//			//레코드셋 생성
//			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
//			
//			//설비ID를 작업크레인으로 설정
//			recPara.setField("YD_EQP_ID", szEqpId);
//			
//			//설비 체크 및 데이터 조회
////			blnRtnVal = this.chkGetEqp(szEqpId, rsResult);
////			if (!blnRtnVal) return blnRtnVal;
//			
//			szRtnMsg		= DaoManager.getYdEqp(recPara, rsResult, 0);
//			
//			if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
//				if( szRtnMsg.equals(YdConstant.RETN_CD_NOTEXIST) ) {
//					return YdConstant.YD_EQP_NOTEXIST;
//				}
//			}
//			
//			//레코드 추출
//			rsResult.first();
//			recPara = rsResult.getRecord();
//			
//			//설비상태
//			szYD_EQP_STAT = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_STAT");
//			ydUtils.putLog(szSessionName, szMethodName, "szYD_EQP_STAT: " + szYD_EQP_STAT, YdConstant.DEBUG);
//
//			//야드설비작업Mode
//			szYD_EQP_WRK_MODE = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_WRK_MODE");
//			ydUtils.putLog(szSessionName, szMethodName, "YD_EQP_WRK_MODE: " + szYD_EQP_WRK_MODE, YdConstant.DEBUG);
//
//			//크레인의 상태가 'b'이면 false 리턴.
//			if (szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_BREAK)) {
//				
//				szMsg = "설비ID(" + szEqpId + ")의 상태가 고장(" + szYD_EQP_STAT + ") 입니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				//blnRtnVal = false;
//				
//				return YdConstant.YD_EQP_STAT_BREAK;
//			}else if (szYD_EQP_WRK_MODE.equals(YdConstant.YD_EQP_WRK_MODE_OFF_LINE)) {
//				
//				szMsg = "설비ID(" + szEqpId + ")의 상태가  OFF LINE(" + szYD_EQP_WRK_MODE + ")상태 입니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				//blnRtnVal = false;
//				
//				return YdConstant.YD_EQP_WRK_MODE_OFF_LINE;
//			} else {
//	
//				//blnRtnVal = true;
//				return YdConstant.RETN_CD_SUCCESS;
//			}
//		} catch(Exception e) {
//			szMsg = "설비상태 체크 중 예외발생! 예외메세지: " + e.getMessage();
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			return YdConstant.RETN_CD_FAILURE;
//		}
//		//return blnRtnVal;
//		
//	} //end of eqpStatCheck
    
    
	
	
	
	
	
	
	
	
	
//	/**
//	 * 오퍼레이션명 : 설비 유무체크 및 조회결과 데이터 반환
//	 *  
//	 * @param  String        szEqpId  설비ID
//	 *         JDTORecordSet rsResult 결과레코드셋
//	 * @return boolean       true(성공), false(실패)
//	 * @throws JDTOException
//	 */
//	public boolean chkGetEqp(String szEqpId, JDTORecordSet rsResult)throws JDTOException  {
//		
//		//설비 DAO
//		YdEqpDao ydEqpDao     = new YdEqpDao();
//		//리턴값(boolean)
//		boolean blnRtnVal     = false;
//		//리턴값(int)
//		int intRtnVal         = 0;
//		//메소드명
//		String szMethodName   = "chkGetEqp";
//		String szMsg          = null;
//		
//		//레코드 선언
//		JDTORecord recPara        = null;	
//
//		try {
//			
//			//레코드 생성
//			recPara  = JDTORecordFactory.getInstance().create();
//			
//			//설비ID
//			recPara.setField("YD_EQP_ID", szEqpId);
//			
//			//설비 테이블 조회
//			intRtnVal = ydEqpDao.getYdEqp(recPara, rsResult, 0);
//
//			//리턴값 메세지처리
//			if (intRtnVal > 1) {
//				
//				szMsg = "설비ID(" + szEqpId + ")에 대한 설비 데이터가 중복되었습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				blnRtnVal = false;
//				
//			} else if (intRtnVal == 1) {
//
//				blnRtnVal = true;
//				
//			} else if (intRtnVal == 0) {
//				
//				szMsg = "설비ID(" + szEqpId + ")에 대한 설비 데이터가 없습니다.";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				blnRtnVal = false;
//				
//			} else if (intRtnVal == -2) {
//				
//				szMsg = "설비ID(" + szEqpId + ")로 설비 조회중 parameter error 발생!";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				blnRtnVal = false;
//				
//			} else {
//				
//				szMsg = "설비ID(" + szEqpId + ")로 설비 조회중 오류 발생!";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				blnRtnVal = false;
//				
//			}
//		} catch(Exception e) {
//			szMsg = "설비 유무체크 및 조회결과 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			return blnRtnVal = false;
//		}
//		return blnRtnVal;
//	} //end of chkGetEqp
//    
    
//	/**
//	 * 오퍼레이션명 : 설비구분변경
//	 *  
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param msgRecord
//	 * @return
//	 * @throws JDTOException
//	 */
//	public int ydEqpGpConvert(JDTORecord msgRecord, JDTORecord resultRecord) throws JDTOException {
//		String szMsg = null;
//		String szMethodName = "ydEqpGpConvert";
//		String szOperationName = "설비구분변경";
//		String szEQP_GP = null;
//		String szYD_EQP_ID = null;
//		String szYD_STK_BED_NO = null;
//		int intRtnVal = 0;
//		
//		try {
//			/* 열연조업화면에서 대상재를 선택하여 C열연정정입측Line-In요구 시 */
//			ydUtils.displayRecord(szOperationName, msgRecord);
//			
//			szEQP_GP = ydDaoUtils.paraRecChkNull(msgRecord, "EQP_GP");
//			// #1HOT FINAL 입측(G)
//			if(szEQP_GP.equals("ECC06")){
//				szYD_EQP_ID = "HGFE01";
//				szYD_STK_BED_NO = "06";
//			} else if(szEQP_GP.equals("ECC05")){
//				szYD_EQP_ID = "HGFE01";
//				szYD_STK_BED_NO = "05";
//			} else if(szEQP_GP.equals("ECC04")){
//				szYD_EQP_ID = "HGFE01";
//				szYD_STK_BED_NO = "04";
//			} else if(szEQP_GP.equals("ECC03")){
//				szYD_EQP_ID = "HGFE01";
//				szYD_STK_BED_NO = "03";
//			} else if(szEQP_GP.equals("ECC02")){
//				szYD_EQP_ID = "HGFE01";
//				szYD_STK_BED_NO = "02";
//			} else if(szEQP_GP.equals("ECC01")){
//				szYD_EQP_ID = "HGFE01";
//				szYD_STK_BED_NO = "01";
//
//				// #1HOT FINAL 출측
//			} else if(szEQP_GP.equals("DCC01")){
//				szYD_EQP_ID = "HGFD01";
//				szYD_STK_BED_NO = "01";
//			} else if(szEQP_GP.equals("DCC02")){
//				szYD_EQP_ID = "HGFD01";
//				szYD_STK_BED_NO = "02";
//			} else if(szEQP_GP.equals("DCC03")){
//				szYD_EQP_ID = "HGFD01";
//				szYD_STK_BED_NO = "03";
//			} else if(szEQP_GP.equals("DCC04")){
//				szYD_EQP_ID = "HGFD01";
//				szYD_STK_BED_NO = "04";
//			} else if(szEQP_GP.equals("DCC05")){
//				szYD_EQP_ID = "HGFD01";
//				szYD_STK_BED_NO = "05";
//			} else if(szEQP_GP.equals("DCC06")){
//				szYD_EQP_ID = "HGFD01";
//				szYD_STK_BED_NO = "06";
//			} else if(szEQP_GP.equals("DCC07")){
//				szYD_EQP_ID = "HGFD01";
//				szYD_STK_BED_NO = "07";
//			} else if(szEQP_GP.equals("DCC08")){
//				szYD_EQP_ID = "HGFD01";
//				szYD_STK_BED_NO = "08";
//			} else if(szEQP_GP.equals("DCC09")){
//				szYD_EQP_ID = "HGFD01";
//				szYD_STK_BED_NO = "09";
//			} else if(szEQP_GP.equals("DCC10")){
//				szYD_EQP_ID = "HGFD01";
//				szYD_STK_BED_NO = "10";
//			} else if(szEQP_GP.equals("DCC11")){
//				szYD_EQP_ID = "HGFD01";
//				szYD_STK_BED_NO = "11";
//
//				// #2HOT FINAL 입측
//			} else if(szEQP_GP.equals("K2-01")){
//				szYD_EQP_ID = "HFFE02";
//				szYD_STK_BED_NO = "01";
//			} else if(szEQP_GP.equals("K2-02")){
//				szYD_EQP_ID = "HFFE02";
//				szYD_STK_BED_NO = "02";
//			} else if(szEQP_GP.equals("K2-03")){
//				szYD_EQP_ID = "HFFE02";
//				szYD_STK_BED_NO = "03";
//			} else if(szEQP_GP.equals("K2-04")){
//				szYD_EQP_ID = "HFFE02";
//				szYD_STK_BED_NO = "04";
//			} else if(szEQP_GP.equals("K2-05")){
//				szYD_EQP_ID = "HFFE02";
//				szYD_STK_BED_NO = "05";
//			} else if(szEQP_GP.equals("K2-06")){
//				szYD_EQP_ID = "HFFE02";
//				szYD_STK_BED_NO = "06";
//			} else if(szEQP_GP.equals("K2-07")){
//				szYD_EQP_ID = "HFFE02";
//				szYD_STK_BED_NO = "07";
//			} else if(szEQP_GP.equals("K2-08")){
//				szYD_EQP_ID = "HFFE02";
//				szYD_STK_BED_NO = "08";
//			} else if(szEQP_GP.equals("K2-09")){
//				szYD_EQP_ID = "HFFE02";
//				szYD_STK_BED_NO = "09";
//			} else if(szEQP_GP.equals("K2-10")){
//				szYD_EQP_ID = "HFFE02";
//				szYD_STK_BED_NO = "10";
//			} else if(szEQP_GP.equals("K2-11")){
//				szYD_EQP_ID = "HFFE02";
//				szYD_STK_BED_NO = "11";
//			} else if(szEQP_GP.equals("K2-12")){
//				szYD_EQP_ID = "HFFE02";
//				szYD_STK_BED_NO = "12";
//
//				// #3HOT FINAL 입측
//			} else if(szEQP_GP.equals("K3-01")){
//				szYD_EQP_ID = "HDFE03";
//				szYD_STK_BED_NO = "01";
//			} else if(szEQP_GP.equals("K3-02")){
//				szYD_EQP_ID = "HDFE03";
//				szYD_STK_BED_NO = "02";
//			} else if(szEQP_GP.equals("K3-03")){
//				szYD_EQP_ID = "HDFE03";
//				szYD_STK_BED_NO = "03";
//			} else if(szEQP_GP.equals("K3-04")){
//				szYD_EQP_ID = "HDFE03";
//				szYD_STK_BED_NO = "04";
//			} else if(szEQP_GP.equals("K3-05")){
//				szYD_EQP_ID = "HDFE03";
//				szYD_STK_BED_NO = "05";
//			} else if(szEQP_GP.equals("K3-06")){
//				szYD_EQP_ID = "HDFE03";
//				szYD_STK_BED_NO = "06";
//			} else if(szEQP_GP.equals("K3-07")){
//				szYD_EQP_ID = "HDFE03";
//				szYD_STK_BED_NO = "07";
//			} else if(szEQP_GP.equals("K3-08")){
//				szYD_EQP_ID = "HDFE03";
//				szYD_STK_BED_NO = "08";
//			} else if(szEQP_GP.equals("K3-09")){
//				szYD_EQP_ID = "HDFE03";
//				szYD_STK_BED_NO = "09";
//			} else if(szEQP_GP.equals("K3-10")){
//				szYD_EQP_ID = "HDFE03";
//				szYD_STK_BED_NO = "10";
//			} else if(szEQP_GP.equals("K3-11")){
//				szYD_EQP_ID = "HDFE03";
//				szYD_STK_BED_NO = "11";
//			} else if(szEQP_GP.equals("K3-12")){
//				szYD_EQP_ID = "HDFE03";
//				szYD_STK_BED_NO = "12";
//			} else if(szEQP_GP.equals("K3-13")){
//				szYD_EQP_ID = "HDFE03";
//				szYD_STK_BED_NO = "13";
//			} else if(szEQP_GP.equals("K3-14")){
//				szYD_EQP_ID = "HDFE03";
//				szYD_STK_BED_NO = "14";
//			} else if(szEQP_GP.equals("K3-15")){
//				szYD_EQP_ID = "HDFE03";
//				szYD_STK_BED_NO = "15";
//			} else if(szEQP_GP.equals("K3-16")){
//				szYD_EQP_ID = "HDFE03";
//				szYD_STK_BED_NO = "16";
//			} else if(szEQP_GP.equals("K3-17")){
//				szYD_EQP_ID = "HDFE03";
//				szYD_STK_BED_NO = "17";
//			} else if(szEQP_GP.equals("K3-18")){
//				szYD_EQP_ID = "HDFE03";
//				szYD_STK_BED_NO = "18";
//			} else if(szEQP_GP.equals("K3-19")){
//				szYD_EQP_ID = "HDFE03";
//				szYD_STK_BED_NO = "19";
//			} else if(szEQP_GP.equals("K3-20")){
//				szYD_EQP_ID = "HDFE03";
//				szYD_STK_BED_NO = "20";
//
//				// SPM1 입측
//			} else if(szEQP_GP.equals("ECC01")){
//				szYD_EQP_ID = "HHKE01";
//				szYD_STK_BED_NO = "01";
//			} else if(szEQP_GP.equals("ECC02")){
//				szYD_EQP_ID = "HHKE01";
//				szYD_STK_BED_NO = "02";
//			} else if(szEQP_GP.equals("ECC03")){
//				szYD_EQP_ID = "HHKE01";
//				szYD_STK_BED_NO = "03";
//			} else if(szEQP_GP.equals("ECC04")){
//				szYD_EQP_ID = "HHKE01";
//				szYD_STK_BED_NO = "04";
//			} else if(szEQP_GP.equals("ECC05")){
//				szYD_EQP_ID = "HHKE01";
//				szYD_STK_BED_NO = "05";
//			} else if(szEQP_GP.equals("ECC06")){
//				szYD_EQP_ID = "HHKE01";
//				szYD_STK_BED_NO = "06";
//
//				// SPM1 출측
//			} else if(szEQP_GP.equals("DCC01")){
//				szYD_EQP_ID = "HHKD01";
//				szYD_STK_BED_NO = "01";
//			} else if(szEQP_GP.equals("DCC02")){
//				szYD_EQP_ID = "HHKD01";
//				szYD_STK_BED_NO = "02";
//			} else if(szEQP_GP.equals("DCC03")){
//				szYD_EQP_ID = "HHKD01";
//				szYD_STK_BED_NO = "03";
//			} else if(szEQP_GP.equals("DCC04")){
//				szYD_EQP_ID = "HHKD01";
//				szYD_STK_BED_NO = "04";
//			} else if(szEQP_GP.equals("DCC05")){
//				szYD_EQP_ID = "HHKD01";
//				szYD_STK_BED_NO = "05";
//			} else if(szEQP_GP.equals("DCC06")){
//				szYD_EQP_ID = "HHKD01";
//				szYD_STK_BED_NO = "06";
//			} else if(szEQP_GP.equals("DCC07")){
//				szYD_EQP_ID = "HHKD01";
//				szYD_STK_BED_NO = "07";
//			} else if(szEQP_GP.equals("DCC08")){
//				szYD_EQP_ID = "HHKD01";
//				szYD_STK_BED_NO = "08";
//			} else if(szEQP_GP.equals("DCC09")){
//				szYD_EQP_ID = "HHKD01";
//				szYD_STK_BED_NO = "09";
//			} else if(szEQP_GP.equals("DCC10")){
//				szYD_EQP_ID = "HHKD01";
//				szYD_STK_BED_NO = "10";
//			} else if(szEQP_GP.equals("DCC11")){
//				szYD_EQP_ID = "HHKD01";
//				szYD_STK_BED_NO = "11";
//
//				// SPM2 입측
//			} else if(szEQP_GP.equals("ECC01")){
//				szYD_EQP_ID = "HEDE01";
//				szYD_STK_BED_NO = "01";
//			} else if(szEQP_GP.equals("ECC02")){
//				szYD_EQP_ID = "HEDE01";
//				szYD_STK_BED_NO = "02";
//			} else if(szEQP_GP.equals("ECC03")){
//				szYD_EQP_ID = "HEDE01";
//				szYD_STK_BED_NO = "03";
//			} else if(szEQP_GP.equals("ECC04")){
//				szYD_EQP_ID = "HEDE01";
//				szYD_STK_BED_NO = "04";
//			} else if(szEQP_GP.equals("ECC05")){
//				szYD_EQP_ID = "HEDE01";
//				szYD_STK_BED_NO = "05";
//			} else if(szEQP_GP.equals("ECC06")){
//				szYD_EQP_ID = "HEDE01";
//				szYD_STK_BED_NO = "06";
//
//				// SPM2 출측
//			} else if(szEQP_GP.equals("DCC01")){
//				szYD_EQP_ID = "HEDD01";
//				szYD_STK_BED_NO = "01";
//			} else if(szEQP_GP.equals("DCC02")){
//				szYD_EQP_ID = "HEDD01";
//				szYD_STK_BED_NO = "02";
//			} else if(szEQP_GP.equals("DCC03")){
//				szYD_EQP_ID = "HEDD01";
//				szYD_STK_BED_NO = "03";
//			} else if(szEQP_GP.equals("DCC04")){
//				szYD_EQP_ID = "HEDD01";
//				szYD_STK_BED_NO = "04";
//			} else if(szEQP_GP.equals("DCC05")){
//				szYD_EQP_ID = "HEDD01";
//				szYD_STK_BED_NO = "05";
//			} else if(szEQP_GP.equals("DCC06")){
//				szYD_EQP_ID = "HEDD01";
//				szYD_STK_BED_NO = "06";
//			} else if(szEQP_GP.equals("DCC07")){
//				szYD_EQP_ID = "HEDD01";
//				szYD_STK_BED_NO = "07";
//			} else if(szEQP_GP.equals("DCC08")){
//				szYD_EQP_ID = "HEDD01";
//				szYD_STK_BED_NO = "08";
//			} else if(szEQP_GP.equals("DCC09")){
//				szYD_EQP_ID = "HEDD01";
//				szYD_STK_BED_NO = "09";
//			} else if(szEQP_GP.equals("DCC10")){
//				szYD_EQP_ID = "HEDD01";
//				szYD_STK_BED_NO = "10";
//			} else if(szEQP_GP.equals("DCC11")){
//				szYD_EQP_ID = "HEDD01";
//				szYD_STK_BED_NO = "11";
//			}
//			
//
//			resultRecord.addField("YD_EQP_ID", szYD_EQP_ID);
//			resultRecord.addField("YD_STK_BED_NO", szYD_STK_BED_NO);
//
//		} catch (Exception e){
//			szMsg = "C열연 정정설비구분 변경!! Error!! "	+ e.getMessage();
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			return intRtnVal = -1;
//		}
//		return intRtnVal = 1;
//	} //end of ydEqpGpConversion
//
//	/**
//	 * 오퍼레이션명 : 설비구분변경SJH
//	 *  
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param msgRecord
//	 * @return
//	 * @throws JDTOException
//	 */
//	public int ydEqpGpConvertNew(JDTORecord msgRecord, JDTORecord resultRecord) throws JDTOException {
//		String szMsg = null;
//		String szMethodName = "ydEqpGpConvertNew";
//		String szOperationName = "설비구분변경";
//		String szEQP_GP = null;
//		String szYD_EQP_ID = null;
//		String szYD_STK_BED_NO = null;
//		String szYD_STK_COL_GP_UP_WO = "";
//		int intRtnVal = 0;
//		
//		try {
//			/* 열연조업화면에서 대상재를 선택하여 C열연정정입측Line-In요구 시 */
//			ydUtils.displayRecord(szOperationName, msgRecord);
//			
//			szYD_STK_COL_GP_UP_WO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP_UP_WO");
//			szEQP_GP = ydDaoUtils.paraRecChkNull(msgRecord, "EQP_GP");
//			
//			// #1HOT FINAL 입측(G)
//			if(szEQP_GP.equals("ECC06") && szYD_STK_COL_GP_UP_WO.substring(1,2).equals("G")){
//				szYD_EQP_ID = "HGFE01";
//				szYD_STK_BED_NO = "06";
//			} else if(szEQP_GP.equals("ECC05")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("G")){
//				szYD_EQP_ID = "HGFE01";
//				szYD_STK_BED_NO = "05";
//			} else if(szEQP_GP.equals("ECC04")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("G")){
//				szYD_EQP_ID = "HGFE01";
//				szYD_STK_BED_NO = "04";
//			} else if(szEQP_GP.equals("ECC03")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("G")){
//				szYD_EQP_ID = "HGFE01";
//				szYD_STK_BED_NO = "03";
//			} else if(szEQP_GP.equals("ECC02")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("G")){
//				szYD_EQP_ID = "HGFE01";
//				szYD_STK_BED_NO = "02";
//			} else if(szEQP_GP.equals("ECC01")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("G")){
//				szYD_EQP_ID = "HGFE01";
//				szYD_STK_BED_NO = "01";
//				//  #1HOT FINAL 출측
//			} else if(szEQP_GP.equals("DCC01")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("G")){
//				szYD_EQP_ID = "HGFD01";
//				szYD_STK_BED_NO = "01";
//			} else if(szEQP_GP.equals("DCC02")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("G")){
//				szYD_EQP_ID = "HGFD01";
//				szYD_STK_BED_NO = "02";
//			} else if(szEQP_GP.equals("DCC03")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("G")){
//				szYD_EQP_ID = "HGFD01";
//				szYD_STK_BED_NO = "03";
//			} else if(szEQP_GP.equals("DCC04")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("G")){
//				szYD_EQP_ID = "HGFD01";
//				szYD_STK_BED_NO = "04";
//			} else if(szEQP_GP.equals("DCC05")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("G")){
//				szYD_EQP_ID = "HGFD01";
//				szYD_STK_BED_NO = "05";
//			} else if(szEQP_GP.equals("DCC06")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("G")){
//				szYD_EQP_ID = "HGFD01";
//				szYD_STK_BED_NO = "06";
//			} else if(szEQP_GP.equals("DCC07")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("G")){
//				szYD_EQP_ID = "HGFD01";
//				szYD_STK_BED_NO = "07";
//			} else if(szEQP_GP.equals("DCC08")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("G")){
//				szYD_EQP_ID = "HGFD01";
//				szYD_STK_BED_NO = "08";
//			} else if(szEQP_GP.equals("DCC09")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("G")){
//				szYD_EQP_ID = "HGFD01";
//				szYD_STK_BED_NO = "09";
//			} else if(szEQP_GP.equals("DCC10")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("G")){
//				szYD_EQP_ID = "HGFD01";
//				szYD_STK_BED_NO = "10";
//			} else if(szEQP_GP.equals("DCC11")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("G")){
//				szYD_EQP_ID = "HGFD01";
//				szYD_STK_BED_NO = "11";
//
//				// #2HOT FINAL 입측
//			} else if(szEQP_GP.equals("K2-01")){
//				szYD_EQP_ID = "HFFE02";
//				szYD_STK_BED_NO = "01";
//			} else if(szEQP_GP.equals("K2-02")){
//				szYD_EQP_ID = "HFFE02";
//				szYD_STK_BED_NO = "02";
//			} else if(szEQP_GP.equals("K2-03")){
//				szYD_EQP_ID = "HFFE02";
//				szYD_STK_BED_NO = "03";
//			} else if(szEQP_GP.equals("K2-04")){
//				szYD_EQP_ID = "HFFE02";
//				szYD_STK_BED_NO = "04";
//			} else if(szEQP_GP.equals("K2-05")){
//				szYD_EQP_ID = "HFFE02";
//				szYD_STK_BED_NO = "05";
//			} else if(szEQP_GP.equals("K2-06")){
//				szYD_EQP_ID = "HFFE02";
//				szYD_STK_BED_NO = "06";
//			} else if(szEQP_GP.equals("K2-07")){
//				szYD_EQP_ID = "HFFE02";
//				szYD_STK_BED_NO = "07";
//			} else if(szEQP_GP.equals("K2-08")){
//				szYD_EQP_ID = "HFFE02";
//				szYD_STK_BED_NO = "08";
//			} else if(szEQP_GP.equals("K2-09")){
//				szYD_EQP_ID = "HFFE02";
//				szYD_STK_BED_NO = "09";
//			} else if(szEQP_GP.equals("K2-10")){
//				szYD_EQP_ID = "HFFE02";
//				szYD_STK_BED_NO = "10";
//			} else if(szEQP_GP.equals("K2-11")){
//				szYD_EQP_ID = "HFFE02";
//				szYD_STK_BED_NO = "11";
//			} else if(szEQP_GP.equals("K2-12")){
//				szYD_EQP_ID = "HFFE02";
//				szYD_STK_BED_NO = "12";
//
//				// #3HOT FINAL 입측
//			} else if(szEQP_GP.equals("K3-01")){
//				szYD_EQP_ID = "HDFE03";
//				szYD_STK_BED_NO = "01";
//			} else if(szEQP_GP.equals("K3-02")){
//				szYD_EQP_ID = "HDFE03";
//				szYD_STK_BED_NO = "02";
//			} else if(szEQP_GP.equals("K3-03")){
//				szYD_EQP_ID = "HDFE03";
//				szYD_STK_BED_NO = "03";
//			} else if(szEQP_GP.equals("K3-04")){
//				szYD_EQP_ID = "HDFE03";
//				szYD_STK_BED_NO = "04";
//			} else if(szEQP_GP.equals("K3-05")){
//				szYD_EQP_ID = "HDFE03";
//				szYD_STK_BED_NO = "05";
//			} else if(szEQP_GP.equals("K3-06")){
//				szYD_EQP_ID = "HDFE03";
//				szYD_STK_BED_NO = "06";
//			} else if(szEQP_GP.equals("K3-07")){
//				szYD_EQP_ID = "HDFE03";
//				szYD_STK_BED_NO = "07";
//			} else if(szEQP_GP.equals("K3-08")){
//				szYD_EQP_ID = "HDFE03";
//				szYD_STK_BED_NO = "08";
//			} else if(szEQP_GP.equals("K3-09")){
//				szYD_EQP_ID = "HDFE03";
//				szYD_STK_BED_NO = "09";
//			} else if(szEQP_GP.equals("K3-10")){
//				szYD_EQP_ID = "HDFE03";
//				szYD_STK_BED_NO = "10";
//			} else if(szEQP_GP.equals("K3-11")){
//				szYD_EQP_ID = "HDFE03";
//				szYD_STK_BED_NO = "11";
//			} else if(szEQP_GP.equals("K3-12")){
//				szYD_EQP_ID = "HDFE03";
//				szYD_STK_BED_NO = "12";
//			} else if(szEQP_GP.equals("K3-13")){
//				szYD_EQP_ID = "HDFE03";
//				szYD_STK_BED_NO = "13";
//			} else if(szEQP_GP.equals("K3-14")){
//				szYD_EQP_ID = "HDFE03";
//				szYD_STK_BED_NO = "14";
//			} else if(szEQP_GP.equals("K3-15")){
//				szYD_EQP_ID = "HDFE03";
//				szYD_STK_BED_NO = "15";
//			} else if(szEQP_GP.equals("K3-16")){
//				szYD_EQP_ID = "HDFE03";
//				szYD_STK_BED_NO = "16";
//			} else if(szEQP_GP.equals("K3-17")){
//				szYD_EQP_ID = "HDFE03";
//				szYD_STK_BED_NO = "17";
//			} else if(szEQP_GP.equals("K3-18")){
//				szYD_EQP_ID = "HDFE03";
//				szYD_STK_BED_NO = "18";
//			} else if(szEQP_GP.equals("K3-19")){
//				szYD_EQP_ID = "HDFE03";
//				szYD_STK_BED_NO = "19";
//			} else if(szEQP_GP.equals("K3-20")){
//				szYD_EQP_ID = "HDFE03";
//				szYD_STK_BED_NO = "20";
//
//				// SPM 입측
//			} else if(szEQP_GP.equals("ECC01")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("H")){
//				szYD_EQP_ID = "HHKE01";
//				szYD_STK_BED_NO = "01";
//			} else if(szEQP_GP.equals("ECC02")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("H")){
//				szYD_EQP_ID = "HHKE01";
//				szYD_STK_BED_NO = "02";
//			} else if(szEQP_GP.equals("ECC03")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("H")){
//				szYD_EQP_ID = "HHKE01";
//				szYD_STK_BED_NO = "03";
//			} else if(szEQP_GP.equals("ECC04")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("H")){
//				szYD_EQP_ID = "HHKE01";
//				szYD_STK_BED_NO = "04";
//			} else if(szEQP_GP.equals("ECC05")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("H")){
//				szYD_EQP_ID = "HHKE01";
//				szYD_STK_BED_NO = "05";
//			} else if(szEQP_GP.equals("ECC06")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("H")){
//				szYD_EQP_ID = "HHKE01";
//				szYD_STK_BED_NO = "06";
//
//				// SPM 출측
//			} else if(szEQP_GP.equals("DCC01")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("H")){
//				szYD_EQP_ID = "HHKD01";
//				szYD_STK_BED_NO = "01";
//			} else if(szEQP_GP.equals("DCC02")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("H")){
//				szYD_EQP_ID = "HHKD01";
//				szYD_STK_BED_NO = "02";
//			} else if(szEQP_GP.equals("DCC03")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("H")){
//				szYD_EQP_ID = "HHKD01";
//				szYD_STK_BED_NO = "03";
//			} else if(szEQP_GP.equals("DCC04")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("H")){
//				szYD_EQP_ID = "HHKD01";
//				szYD_STK_BED_NO = "04";
//			} else if(szEQP_GP.equals("DCC05")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("H")){
//				szYD_EQP_ID = "HHKD01";
//				szYD_STK_BED_NO = "05";
//			} else if(szEQP_GP.equals("DCC06")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("H")){
//				szYD_EQP_ID = "HHKD01";
//				szYD_STK_BED_NO = "06";
//			} else if(szEQP_GP.equals("DCC07")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("H")){
//				szYD_EQP_ID = "HHKD01";
//				szYD_STK_BED_NO = "07";
//			} else if(szEQP_GP.equals("DCC08")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("H")){
//				szYD_EQP_ID = "HHKD01";
//				szYD_STK_BED_NO = "08";
//			} else if(szEQP_GP.equals("DCC09")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("H")){
//				szYD_EQP_ID = "HHKD01";
//				szYD_STK_BED_NO = "09";
//			} else if(szEQP_GP.equals("DCC10")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("H")){
//				szYD_EQP_ID = "HHKD01";
//				szYD_STK_BED_NO = "10";
//			} else if(szEQP_GP.equals("DCC11")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("H")){
//				szYD_EQP_ID = "HHKD01";
//				szYD_STK_BED_NO = "11";
//
//				// SPM2 입측
//			} else if(szEQP_GP.equals("ECC01")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("E")){
//				szYD_EQP_ID = "HEDE01";
//				szYD_STK_BED_NO = "01";
//			} else if(szEQP_GP.equals("ECC02")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("E")){
//				szYD_EQP_ID = "HEDE01";
//				szYD_STK_BED_NO = "02";
//			} else if(szEQP_GP.equals("ECC03")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("E")){
//				szYD_EQP_ID = "HEDE01";
//				szYD_STK_BED_NO = "03";
//			} else if(szEQP_GP.equals("ECC04")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("E")){
//				szYD_EQP_ID = "HEDE01";
//				szYD_STK_BED_NO = "04";
//			} else if(szEQP_GP.equals("ECC05")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("E")){
//				szYD_EQP_ID = "HEDE01";
//				szYD_STK_BED_NO = "05";
//			} else if(szEQP_GP.equals("ECC06")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("E")){
//				szYD_EQP_ID = "HEDE01";
//				szYD_STK_BED_NO = "06";
//
//				// SPM2 출측
//			} else if(szEQP_GP.equals("DCC01")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("E")){
//				szYD_EQP_ID = "HEDD01";
//				szYD_STK_BED_NO = "01";
//			} else if(szEQP_GP.equals("DCC02")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("E")){
//				szYD_EQP_ID = "HEDD01";
//				szYD_STK_BED_NO = "02";
//			} else if(szEQP_GP.equals("DCC03")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("E")){
//				szYD_EQP_ID = "HEDD01";
//				szYD_STK_BED_NO = "03";
//			} else if(szEQP_GP.equals("DCC04")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("E")){
//				szYD_EQP_ID = "HEDD01";
//				szYD_STK_BED_NO = "04";
//			} else if(szEQP_GP.equals("DCC05")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("E")){
//				szYD_EQP_ID = "HEDD01";
//				szYD_STK_BED_NO = "05";
//			} else if(szEQP_GP.equals("DCC06")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("E")){
//				szYD_EQP_ID = "HEDD01";
//				szYD_STK_BED_NO = "06";
//			} else if(szEQP_GP.equals("DCC07")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("E")){
//				szYD_EQP_ID = "HEDD01";
//				szYD_STK_BED_NO = "07";
//			} else if(szEQP_GP.equals("DCC08")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("E")){
//				szYD_EQP_ID = "HEDD01";
//				szYD_STK_BED_NO = "08";
//			} else if(szEQP_GP.equals("DCC09")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("E")){
//				szYD_EQP_ID = "HEDD01";
//				szYD_STK_BED_NO = "09";
//			} else if(szEQP_GP.equals("DCC10")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("E")){
//				szYD_EQP_ID = "HEDD01";
//				szYD_STK_BED_NO = "10";
//			} else if(szEQP_GP.equals("DCC11")&& szYD_STK_COL_GP_UP_WO.substring(1,2).equals("E")){
//				szYD_EQP_ID = "HEDD01";
//				szYD_STK_BED_NO = "11";
////C증설
//				// SPM3 입측
//			} else if(szEQP_GP.equals("ECC01")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("CK")){
//				szYD_EQP_ID = "HCKE03";
//				szYD_STK_BED_NO = "01";
//			} else if(szEQP_GP.equals("ECC02")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("CK")){
//				szYD_EQP_ID = "HCKE03";
//				szYD_STK_BED_NO = "02";
//			} else if(szEQP_GP.equals("ECC03")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("CK")){
//				szYD_EQP_ID = "HCKE03";
//				szYD_STK_BED_NO = "03";
//			} else if(szEQP_GP.equals("ECC04")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("CK")){
//				szYD_EQP_ID = "HCKE03";
//				szYD_STK_BED_NO = "04";
//			} else if(szEQP_GP.equals("ECC05")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("CK")){
//				szYD_EQP_ID = "HCKE03";
//				szYD_STK_BED_NO = "05";
//			} else if(szEQP_GP.equals("ECC06")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("CK")){
//				szYD_EQP_ID = "HCKE03";
//				szYD_STK_BED_NO = "06";
//			} else if(szEQP_GP.equals("ECC07")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("CK")){
//				szYD_EQP_ID = "HCKE03";
//				szYD_STK_BED_NO = "07";
//			} else if(szEQP_GP.equals("ECC08")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("CK")){
//				szYD_EQP_ID = "HCKE03";
//				szYD_STK_BED_NO = "08";
//			} else if(szEQP_GP.equals("ECC09")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("CK")){
//				szYD_EQP_ID = "HCKE03";
//				szYD_STK_BED_NO = "09";
//			} else if(szEQP_GP.equals("ECC10")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("CK")){
//				szYD_EQP_ID = "HCKE03";
//				szYD_STK_BED_NO = "10";
//			} else if(szEQP_GP.equals("ECC11")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("CK")){
//				szYD_EQP_ID = "HCKE03";
//				szYD_STK_BED_NO = "11";
//			} else if(szEQP_GP.equals("ECC12")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("CK")){
//				szYD_EQP_ID = "HCKE03";
//				szYD_STK_BED_NO = "12";
//	
//				// SPM3 출측
//			} else if(szEQP_GP.equals("DCC01")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("CK")){
//				szYD_EQP_ID = "HCKD03";
//				szYD_STK_BED_NO = "01";
//			} else if(szEQP_GP.equals("DCC02")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("CK")){
//				szYD_EQP_ID = "HCKD03";
//				szYD_STK_BED_NO = "02";
//			} else if(szEQP_GP.equals("DCC03")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("CK")){
//				szYD_EQP_ID = "HCKD03";
//				szYD_STK_BED_NO = "03";
//			} else if(szEQP_GP.equals("DCC04")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("CK")){
//				szYD_EQP_ID = "HCKD03";
//				szYD_STK_BED_NO = "04";
//			} else if(szEQP_GP.equals("DCC05")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("CK")){
//				szYD_EQP_ID = "HCKD03";
//				szYD_STK_BED_NO = "05";
//			} else if(szEQP_GP.equals("DCC06")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("CK")){
//				szYD_EQP_ID = "HCKD03";
//				szYD_STK_BED_NO = "06";
//			} else if(szEQP_GP.equals("DCC07")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("CK")){
//				szYD_EQP_ID = "HCKD03";
//				szYD_STK_BED_NO = "07";
//			} else if(szEQP_GP.equals("DCC08")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("CK")){
//				szYD_EQP_ID = "HCKD03";
//				szYD_STK_BED_NO = "08";
//			} else if(szEQP_GP.equals("DCC09")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("CK")){
//				szYD_EQP_ID = "HCKD03";
//				szYD_STK_BED_NO = "09";
//			} else if(szEQP_GP.equals("DCC10")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("CK")){
//				szYD_EQP_ID = "HCKD03";
//				szYD_STK_BED_NO = "10";
//			} else if(szEQP_GP.equals("DCC11")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("CK")){
//				szYD_EQP_ID = "HCKD03";
//				szYD_STK_BED_NO = "11";
//
//			// SPM4 입측
//			} else if(szEQP_GP.equals("ECC01")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("BK")){
//				szYD_EQP_ID = "HBKE04";
//				szYD_STK_BED_NO = "01";
//			} else if(szEQP_GP.equals("ECC02")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("BK")){
//				szYD_EQP_ID = "HBKE04";
//				szYD_STK_BED_NO = "02";
//			} else if(szEQP_GP.equals("ECC03")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("BK")){
//				szYD_EQP_ID = "HBKE04";
//				szYD_STK_BED_NO = "03";
//			} else if(szEQP_GP.equals("ECC04")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("BK")){
//				szYD_EQP_ID = "HBKE04";
//				szYD_STK_BED_NO = "04";
//			} else if(szEQP_GP.equals("ECC05")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("BK")){
//				szYD_EQP_ID = "HBKE04";
//				szYD_STK_BED_NO = "05";
//			} else if(szEQP_GP.equals("ECC06")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("BK")){
//				szYD_EQP_ID = "HBKE04";
//				szYD_STK_BED_NO = "06";
//			} else if(szEQP_GP.equals("ECC07")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("BK")){
//				szYD_EQP_ID = "HBKE04";
//				szYD_STK_BED_NO = "07";
//			} else if(szEQP_GP.equals("ECC08")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("BK")){
//				szYD_EQP_ID = "HBKE04";
//				szYD_STK_BED_NO = "08";
//			} else if(szEQP_GP.equals("ECC09")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("BK")){
//				szYD_EQP_ID = "HBKE04";
//				szYD_STK_BED_NO = "09";
//			} else if(szEQP_GP.equals("ECC10")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("BK")){
//				szYD_EQP_ID = "HBKE04";
//				szYD_STK_BED_NO = "10";
//			} else if(szEQP_GP.equals("ECC11")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("BK")){
//				szYD_EQP_ID = "HBKE04";
//				szYD_STK_BED_NO = "11";
//			} else if(szEQP_GP.equals("ECC12")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("BK")){
//				szYD_EQP_ID = "HBKE04";
//				szYD_STK_BED_NO = "12";
//	
//
//			// SPM5 입측
//			} else if(szEQP_GP.equals("ECC01")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("AK")){
//				szYD_EQP_ID = "HAKE05";
//				szYD_STK_BED_NO = "01";
//			} else if(szEQP_GP.equals("ECC02")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("AK")){
//				szYD_EQP_ID = "HAKE05";
//				szYD_STK_BED_NO = "02";
//			} else if(szEQP_GP.equals("ECC03")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("AK")){
//				szYD_EQP_ID = "HAKE05";
//				szYD_STK_BED_NO = "03";
//			} else if(szEQP_GP.equals("ECC04")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("AK")){
//				szYD_EQP_ID = "HAKE05";
//				szYD_STK_BED_NO = "04";
//			} else if(szEQP_GP.equals("ECC05")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("AK")){
//				szYD_EQP_ID = "HAKE05";
//				szYD_STK_BED_NO = "05";
//			} else if(szEQP_GP.equals("ECC06")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("AK")){
//				szYD_EQP_ID = "HAKE05";
//				szYD_STK_BED_NO = "06";
//			} else if(szEQP_GP.equals("ECC07")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("AK")){
//				szYD_EQP_ID = "HAKE05";
//				szYD_STK_BED_NO = "07";
//			} else if(szEQP_GP.equals("ECC08")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("AK")){
//				szYD_EQP_ID = "HAKE05";
//				szYD_STK_BED_NO = "08";
//			} else if(szEQP_GP.equals("ECC09")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("AK")){
//				szYD_EQP_ID = "HAKE05";
//				szYD_STK_BED_NO = "09";
//			} else if(szEQP_GP.equals("ECC10")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("AK")){
//				szYD_EQP_ID = "HAKE05";
//				szYD_STK_BED_NO = "10";
//			} else if(szEQP_GP.equals("ECC11")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("AK")){
//				szYD_EQP_ID = "HAKE05";
//				szYD_STK_BED_NO = "11";
//			} else if(szEQP_GP.equals("ECC12")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("AK")){
//				szYD_EQP_ID = "HAKE05";
//				szYD_STK_BED_NO = "12";
//		
//				// SPM4 출측
//			} else if(szEQP_GP.equals("DCC01")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("BK")){
//				szYD_EQP_ID = "HBKD04";
//				szYD_STK_BED_NO = "01";
//			} else if(szEQP_GP.equals("DCC02")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("BK")){
//				szYD_EQP_ID = "HBKD04";
//				szYD_STK_BED_NO = "02";
//			} else if(szEQP_GP.equals("DCC03")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("BK")){
//				szYD_EQP_ID = "HBKD04";
//				szYD_STK_BED_NO = "03";
//			} else if(szEQP_GP.equals("DCC04")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("BK")){
//				szYD_EQP_ID = "HBKD04";
//				szYD_STK_BED_NO = "04";
//			} else if(szEQP_GP.equals("DCC05")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("BK")){
//				szYD_EQP_ID = "HBKD04";
//				szYD_STK_BED_NO = "05";
//			} else if(szEQP_GP.equals("DCC06")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("BK")){
//				szYD_EQP_ID = "HBKD04";
//				szYD_STK_BED_NO = "06";
//			} else if(szEQP_GP.equals("DCC07")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("BK")){
//				szYD_EQP_ID = "HBKD04";
//				szYD_STK_BED_NO = "07";
//			} else if(szEQP_GP.equals("DCC08")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("BK")){
//				szYD_EQP_ID = "HBKD04";
//				szYD_STK_BED_NO = "08";
//			} else if(szEQP_GP.equals("DCC09")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("BK")){
//				szYD_EQP_ID = "HBKD04";
//				szYD_STK_BED_NO = "09";
//			} else if(szEQP_GP.equals("DCC10")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("BK")){
//				szYD_EQP_ID = "HBKD04";
//				szYD_STK_BED_NO = "10";
//			} else if(szEQP_GP.equals("DCC11")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("BK")){
//				szYD_EQP_ID = "HBKD04";
//				szYD_STK_BED_NO = "11";
//
//			// SPM5 출측
//			} else if(szEQP_GP.equals("DCC01")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("AK")){
//				szYD_EQP_ID = "HAKD05";
//				szYD_STK_BED_NO = "01";
//			} else if(szEQP_GP.equals("DCC02")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("AK")){
//				szYD_EQP_ID = "HAKD05";
//				szYD_STK_BED_NO = "02";
//			} else if(szEQP_GP.equals("DCC03")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("AK")){
//				szYD_EQP_ID = "HAKD05";
//				szYD_STK_BED_NO = "03";
//			} else if(szEQP_GP.equals("DCC04")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("AK")){
//				szYD_EQP_ID = "HAKD05";
//				szYD_STK_BED_NO = "04";
//			} else if(szEQP_GP.equals("DCC05")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("AK")){
//				szYD_EQP_ID = "HAKD05";
//				szYD_STK_BED_NO = "05";
//			} else if(szEQP_GP.equals("DCC06")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("AK")){
//				szYD_EQP_ID = "HAKD05";
//				szYD_STK_BED_NO = "06";
//			} else if(szEQP_GP.equals("DCC07")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("AK")){
//				szYD_EQP_ID = "HAKD05";
//				szYD_STK_BED_NO = "07";
//			} else if(szEQP_GP.equals("DCC08")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("AK")){
//				szYD_EQP_ID = "HAKD05";
//				szYD_STK_BED_NO = "08";
//			} else if(szEQP_GP.equals("DCC09")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("AK")){
//				szYD_EQP_ID = "HAKD05";
//				szYD_STK_BED_NO = "09";
//			} else if(szEQP_GP.equals("DCC10")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("AK")){
//				szYD_EQP_ID = "HAKD05";
//				szYD_STK_BED_NO = "10";
//			} else if(szEQP_GP.equals("DCC11")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("AK")){
//				szYD_EQP_ID = "HAKD05";
//				szYD_STK_BED_NO = "11";
//
//				// #4HOT FINAL 입측(C)
//			} else if(szEQP_GP.equals("ECC06")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("CF")){
//				szYD_EQP_ID = "HCFE04";
//				szYD_STK_BED_NO = "06";
//			} else if(szEQP_GP.equals("ECC05")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("CF")){
//				szYD_EQP_ID = "HCFE04";
//				szYD_STK_BED_NO = "05";
//			} else if(szEQP_GP.equals("ECC04")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("CF")){
//				szYD_EQP_ID = "HCFE04";
//				szYD_STK_BED_NO = "04";
//			} else if(szEQP_GP.equals("ECC03")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("CF")){
//				szYD_EQP_ID = "HCFE04";
//				szYD_STK_BED_NO = "03";
//			} else if(szEQP_GP.equals("ECC02")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("CF")){
//				szYD_EQP_ID = "HCFE04";
//				szYD_STK_BED_NO = "02";
//			} else if(szEQP_GP.equals("ECC01")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("CF")){
//				szYD_EQP_ID = "HCFE04";
//				szYD_STK_BED_NO = "01";
//				//  #4 HOT FINAL 출측
//			} else if(szEQP_GP.equals("DCC01")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("CF")){
//				szYD_EQP_ID = "HCFD04";
//				szYD_STK_BED_NO = "01";
//			} else if(szEQP_GP.equals("DCC02")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("CF")){
//				szYD_EQP_ID = "HCFD04";
//				szYD_STK_BED_NO = "02";
//			} else if(szEQP_GP.equals("DCC03")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("CF")){
//				szYD_EQP_ID = "HCFD04";
//				szYD_STK_BED_NO = "03";
//			} else if(szEQP_GP.equals("DCC04")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("CF")){
//				szYD_EQP_ID = "HCFD04";
//				szYD_STK_BED_NO = "04";
//			} else if(szEQP_GP.equals("DCC05")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("CF")){
//				szYD_EQP_ID = "HCFD04";
//				szYD_STK_BED_NO = "05";
//			} else if(szEQP_GP.equals("DCC06")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("CF")){
//				szYD_EQP_ID = "HCFD04";
//				szYD_STK_BED_NO = "06";
//			} else if(szEQP_GP.equals("DCC07")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("CF")){
//				szYD_EQP_ID = "HCFD04";
//				szYD_STK_BED_NO = "07";
//			} else if(szEQP_GP.equals("DCC08")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("CF")){
//				szYD_EQP_ID = "HCFD04";
//				szYD_STK_BED_NO = "08";
//			} else if(szEQP_GP.equals("DCC09")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("CF")){
//				szYD_EQP_ID = "HCFD04";
//				szYD_STK_BED_NO = "09";
//			} else if(szEQP_GP.equals("DCC10")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("CF")){
//				szYD_EQP_ID = "HCFD04";
//				szYD_STK_BED_NO = "10";
//			} else if(szEQP_GP.equals("DCC11")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("CF")){
//				szYD_EQP_ID = "HCFD04";
//				szYD_STK_BED_NO = "11";
//			
//				// #5HOT FINAL 입측(B)
//			} else if(szEQP_GP.equals("ECC06")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("BF")){
//				szYD_EQP_ID = "HBFE05";
//				szYD_STK_BED_NO = "06";
//			} else if(szEQP_GP.equals("ECC05")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("BF")){
//				szYD_EQP_ID = "HBFE05";
//				szYD_STK_BED_NO = "05";
//			} else if(szEQP_GP.equals("ECC04")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("BF")){
//				szYD_EQP_ID = "HBFE05";
//				szYD_STK_BED_NO = "04";
//			} else if(szEQP_GP.equals("ECC03")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("BF")){
//				szYD_EQP_ID = "HBFE05";
//				szYD_STK_BED_NO = "03";
//			} else if(szEQP_GP.equals("ECC02")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("BF")){
//				szYD_EQP_ID = "HBFE05";
//				szYD_STK_BED_NO = "02";
//			} else if(szEQP_GP.equals("ECC01")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("BF")){
//				szYD_EQP_ID = "HBFE05";
//				szYD_STK_BED_NO = "01";
//				//  #5 HOT FINAL 출측
//			} else if(szEQP_GP.equals("DCC01")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("BF")){
//				szYD_EQP_ID = "HBFD05";
//				szYD_STK_BED_NO = "01";
//			} else if(szEQP_GP.equals("DCC02")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("BF")){
//				szYD_EQP_ID = "HBFD05";
//				szYD_STK_BED_NO = "02";
//			} else if(szEQP_GP.equals("DCC03")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("BF")){
//				szYD_EQP_ID = "HBFD05";
//				szYD_STK_BED_NO = "03";
//			} else if(szEQP_GP.equals("DCC04")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("BF")){
//				szYD_EQP_ID = "HBFD05";
//				szYD_STK_BED_NO = "04";
//			} else if(szEQP_GP.equals("DCC05")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("BF")){
//				szYD_EQP_ID = "HBFD05";
//				szYD_STK_BED_NO = "05";
//			} else if(szEQP_GP.equals("DCC06")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("BF")){
//				szYD_EQP_ID = "HBFD05";
//				szYD_STK_BED_NO = "06";
//			} else if(szEQP_GP.equals("DCC07")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("BF")){
//				szYD_EQP_ID = "HBFD05";
//				szYD_STK_BED_NO = "07";
//			} else if(szEQP_GP.equals("DCC08")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("BF")){
//				szYD_EQP_ID = "HBFD05";
//				szYD_STK_BED_NO = "08";
//			} else if(szEQP_GP.equals("DCC09")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("BF")){
//				szYD_EQP_ID = "HBFD05";
//				szYD_STK_BED_NO = "09";
//			} else if(szEQP_GP.equals("DCC10")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("BF")){
//				szYD_EQP_ID = "HBFD05";
//				szYD_STK_BED_NO = "10";
//			} else if(szEQP_GP.equals("DCC11")&& szYD_STK_COL_GP_UP_WO.substring(1,3).equals("BF")){
//				szYD_EQP_ID = "HBFD05";
//				szYD_STK_BED_NO = "11";
//			
//			}	
//			resultRecord.addField("YD_EQP_ID", szYD_EQP_ID);
//			resultRecord.addField("YD_STK_BED_NO", szYD_STK_BED_NO);
//
//		} catch (Exception e){
//			szMsg = "C열연 정정설비구분 변경!! Error!! "	+ e.getMessage();
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			return intRtnVal = -1;
//		}
//		return intRtnVal = 1;
//	} //end of ydEqpGpConvertNew
	
//    /**
//     * 오퍼레이션명 : C열연코일야드L2 크레인이적작업지시 (Y5YDL011)
//     *  
//     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//     * @param msgRecord
//     * @return String
//     * @throws JDTOException
//     */
//    public String procY5CrnMvstk(JDTORecord msgRecord)throws JDTOException  {
//
//    	YdDelegate ydDelegate   	= new YdDelegate();
//    	YdStkLyrDao ydStkLyrDao 	= new YdStkLyrDao();
//    	YdEqpDao   ydEqpDao     	= new YdEqpDao();
//    	YdCrnSchDao  ydCrnSchDao  = new YdCrnSchDao();
//    	JDTORecord recOutTemp 		= null;
//    	JDTORecord recOutTemp1 		= null;
//    	JDTORecord recIntTemp 		= null;
//    	JDTORecord inRecord 		= JDTORecordFactory.getInstance().create();
//    	JDTORecord inRecord1 		= JDTORecordFactory.getInstance().create();
//    	JDTORecord inRecord2 		= JDTORecordFactory.getInstance().create();
//        JDTORecord outRecord2		= JDTORecordFactory.getInstance().create();
//    	
//        JDTORecordSet inRecordSet 	= JDTORecordFactory.getInstance().createRecordSet("temp");
//        JDTORecordSet rsResult 		= JDTORecordFactory.getInstance().createRecordSet("temp");
//        
//    	
//        int intRtnVal 			= 0 ;
//        
//        String szMsg            = "";
//        String szMethodName     = "procY5CrnMvstk";
//        String szOperationName	= "코일크레인이적작업지시";
//        
//        String sYD_EQP_ID  		= "";
//        String sYD_GP          	= "";
//        String sYD_BAY_GP      	= "";
//        String sYD_UP_WR_LOC	= "";
//        String sYD_UP_WR_LAYER	= "";
//        String sYD_CRN_XAXIS	= "";
//        String sYD_CRN_YAXIS	= "";
//        String sYD_CRN_ZAXIS	= "";
//
//        EJBConnector ejbConn 	= null;
//        String szRtnMsg			= null;
//        String sYD_STK_COL_GP	= "";
//        String sYD_STK_BED_NO	= "";
//        String sYD_STK_LYR_NO	= "";
//        String szFLAG_CANCEL    = "";
//        String sSTL_NO          = "";
//        String sRTN_CD			= "";
//        String sRTN_MSG			= "";	
//        String sYD_WBOOK_ID		= "";
//        String sYD_SCH_CD		= "";
//        String sYD_EQP_ID_GP	= "";
//        
//        
//        String szRcvTcCode=ydUtils.getTcCode(msgRecord);
//        if(szRcvTcCode==null || szRcvTcCode.equals("")){
//        	szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
//        	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//        	return YdConstant.RETN_CD_TC_ERROR;
//        }
//        
//        if(bDebugFlag){
//            szMsg="[DEBUG] 전문수신 : TCCODE=" +szRcvTcCode ;
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//        }
//
//        try{
//			//=============================================================
//		// Log 테이블 등록 
//			//=============================================================
//			szMsg = "[열연 코일야드L2] 크레인작업지시 수신";
//			ydUtils.putLogMsg("H", YdConstant.YD_MONITORING_CHANNEL_H, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
//
//			
//			sYD_EQP_ID     	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
//			sYD_UP_WR_LOC	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_UP_WR_LOC");
//        	sYD_GP     		= sYD_UP_WR_LOC.substring(0,1);
//        	sYD_BAY_GP 		= sYD_UP_WR_LOC.substring(1,2);
//        	sYD_EQP_ID_GP	= sYD_EQP_ID.substring(5,6);
//        	
//        	//확장동 이외의 제품야드에서는 자동이적을 할 수 없음
//           	if(sYD_GP.equals("J") && 
//    			((sYD_UP_WR_LOC.substring(1,2).equals("D")) ||
//    			(sYD_UP_WR_LOC.substring(1,2).equals("E")) ||
//    			(sYD_UP_WR_LOC.substring(1,2).equals("F")) ||
//    			(sYD_UP_WR_LOC.substring(1,2).equals("G")) ||
//    			(sYD_UP_WR_LOC.substring(1,2).equals("H")) )
//	        	   ){
//           		szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
//            	
//				recIntTemp = JDTORecordFactory.getInstance().create(); 
//    			recIntTemp.setField("MSG_ID"        , "YDY5L005");
//    			recIntTemp.setField("YD_EQP_ID"     , sYD_EQP_ID);						//야드설비ID
//    			recIntTemp.setField("YD_L2_WR_GP"   , "J");	//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
//    			recIntTemp.setField("YD_L3_HD_RS_CD", YdConstant.CRN_WRK_RE_CD_NO_WRK2);	//야드L3처리결과코드 : 9999 - 크레인작업이 더 이상 없습니다.
//    			recIntTemp.setField("YD_L3_MSG"		, "에러:확장동이외 이적불가" + sYD_UP_WR_LOC);	
//     			recIntTemp.setField("YD_WRK_PROG_STAT", "1");	
//    			ydDelegate.sendMsg(recIntTemp);
// 
//            	return YdConstant.RETN_CD_TC_ERROR;
//	        	}
//        	
//        	if(sYD_UP_WR_LOC==null || sYD_UP_WR_LOC.equals("")|| sYD_UP_WR_LOC.length()!=8 ){
//            	szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
//            	
//				recIntTemp = JDTORecordFactory.getInstance().create(); 
//    			recIntTemp.setField("MSG_ID"        , "YDY5L005");
//    			recIntTemp.setField("YD_EQP_ID"     , sYD_EQP_ID);						//야드설비ID
//    			recIntTemp.setField("YD_L2_WR_GP"   , "J");	//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
//    			recIntTemp.setField("YD_L3_HD_RS_CD", YdConstant.CRN_WRK_RE_CD_NO_WRK2);	//야드L3처리결과코드 : 9999 - 크레인작업이 더 이상 없습니다.
//    			recIntTemp.setField("YD_L3_MSG"		, "에러:정보이상권상:" + sYD_UP_WR_LOC);	
//     			recIntTemp.setField("YD_WRK_PROG_STAT", "1");	
//    			ydDelegate.sendMsg(recIntTemp);
// 
//            	return YdConstant.RETN_CD_TC_ERROR;
//        	}
//        	sYD_STK_COL_GP  = sYD_UP_WR_LOC.substring(0, 6);
//        	sYD_STK_BED_NO  = sYD_UP_WR_LOC.substring(6, 8);
//        	sYD_UP_WR_LAYER	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_UP_WR_LAYER");
//        	sYD_STK_LYR_NO	= sYD_UP_WR_LAYER;
//        	sYD_CRN_XAXIS	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_XAXIS");
//        	sYD_CRN_YAXIS	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_YAXIS");
//        	sYD_CRN_ZAXIS	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_ZAXIS");
//        	
//        	//------------------------------------------------------------------------------------------------------
//        	// 야드설비상태 Check	
//        	//------------------------------------------------------------------------------------------------------
//        	
//        	szMsg="["+szOperationName+"] 크레인설비["+sYD_EQP_ID+"] 상태 체크 시작";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//        	
//        	szRtnMsg = this.eqpStatCheck(sYD_EQP_ID);
//        	
//        	if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
//        		szMsg="["+szOperationName+"] 크레인설비["+sYD_EQP_ID+"] 상태 체크 시 오류발생 - 메세지 : " + szRtnMsg;
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	
//				recIntTemp = JDTORecordFactory.getInstance().create(); 
//    			recIntTemp.setField("MSG_ID"        , "YDY5L005");
//    			recIntTemp.setField("YD_EQP_ID"     , sYD_EQP_ID);						//야드설비ID
//    			recIntTemp.setField("YD_L2_WR_GP"   , "J");	//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
//    			recIntTemp.setField("YD_L3_HD_RS_CD", YdConstant.CRN_WRK_RE_CD_NO_WRK2);	//야드L3처리결과코드 : 9999 - 크레인작업이 더 이상 없습니다.
//    			recIntTemp.setField("YD_L3_MSG"		, "에러:설비상태가 이적할수 없음");	
//     			recIntTemp.setField("YD_WRK_PROG_STAT", "1");	
//    			ydDelegate.sendMsg(recIntTemp);
//  
//				
//				return szRtnMsg;
//        	}	
//        	
//        	szMsg="["+szOperationName+"] 크레인설비["+sYD_EQP_ID+"] 상태 체크 완료 - 메세지 : " + szRtnMsg;
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//        
//	       	//
//        	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
//        	inRecord = JDTORecordFactory.getInstance().create();
//        	inRecord.setField("YD_EQP_ID"     , sYD_EQP_ID);	
//        	/*com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqp*/
//        	intRtnVal = ydEqpDao.getYdEqp(inRecord, rsResult, 0);
//			if(intRtnVal > 0) {
//				recOutTemp = JDTORecordFactory.getInstance().create();
//				recOutTemp.setRecord(rsResult.getRecord(0));
//				String szYdEqpStat = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_STAT");
//				//설비상태가 1또는 3인경우
//				if(szYdEqpStat.equals("1")||szYdEqpStat.equals("W")){
//					
//					if(szYdEqpStat.equals("1")){
//						rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
//			        	/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnSchEqpIdWrkProgStat*/
//				    	intRtnVal = ydCrnSchDao.getYdCrnsch(inRecord, rsResult, 16);
//						if(intRtnVal != 1) {
//							//에러처리
//							recIntTemp = JDTORecordFactory.getInstance().create(); 
//							recIntTemp.setField("MSG_ID"        , "YDY5L005");
//			    			recIntTemp.setField("YD_EQP_ID"     , sYD_EQP_ID);						//야드설비ID
//			    			recIntTemp.setField("YD_L2_WR_GP"   , "J");	//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
//			    			recIntTemp.setField("YD_L3_HD_RS_CD", YdConstant.CRN_WRK_RE_CD_NO_WRK2);	//야드L3처리결과코드 : 9999 - 크레인작업이 더 이상 없습니다.
//			    			recIntTemp.setField("YD_L3_MSG"		, "에러:이적요구 할수 없음1");	
//			     			recIntTemp.setField("YD_WRK_PROG_STAT", "1");	
//			    			ydDelegate.sendMsg(recIntTemp);
//			    			
//			    			return YdConstant.RETN_CD_TC_ERROR;
//						}else {
//	                        // 기존 sche' 상태 'w'로 변경처리
//		
//							rsResult.absolute(1);
//		        			recOutTemp1 = JDTORecordFactory.getInstance().create();
//		        			recOutTemp1.setRecord(rsResult.getRecord());
//		        			
//		        			String szYD_CRN_SCH_ID			= ydDaoUtils.paraRecChkNull(recOutTemp1, "YD_CRN_SCH_ID");
//		        			inRecord2 = JDTORecordFactory.getInstance().create();
//		        			inRecord2.setField("MSG_ID"				, "YDY5L004");
//		        			inRecord2.setField("YD_CRN_SCH_ID"		, szYD_CRN_SCH_ID);
//		        			inRecord2.setField("YD_WRK_PROG_STAT"	, "W");
//		                	inRecord2.setField("YD_WORD_DT"			, YdUtils.getCurDate("yyyyMMddHHmmss"));
//		        			inRecord2.setField("MODIFIER"			, "YDSYSTEM");
//		        			inRecord2.setField("MSG_GP"				, "U");
//		            		
//		            		//크레인스케줄의 작업진행 상태를 권상지시로 변경
//		            		intRtnVal = ydCrnSchDao.updYdCrnsch(inRecord2, 0);
//		        			if(intRtnVal <= 0) {
//		        			  m_ctx.setRollbackOnly();
//		        			  throw new JDTOException("<procY5CrnMvstk> " + szMsg);
//		        			}
//
//		               		//더이상 크레인 스케줄에 작업이 없을 경우 설비Table에 설비상태를 'W' 명령선택대기(idle)로 변경한다.
//		            		recIntTemp = JDTORecordFactory.getInstance().create();
//		            		recIntTemp.setField("YD_EQP_ID", sYD_EQP_ID);
//		            		recIntTemp.setField("YD_EQP_STAT", "W");	//'W' 명령선택대기
//		            		intRtnVal = ydEqpDao.updYdEqp(recIntTemp, 0);
//		        			if(intRtnVal <= 0) {
//		        			  m_ctx.setRollbackOnly();
//		        			  throw new JDTOException("<procY5CrnMvstk> " + szMsg);
//
//		        			}
//						}
//					}	
//				} else {
//					recIntTemp = JDTORecordFactory.getInstance().create(); 
//		   			recIntTemp.setField("MSG_ID"        , "YDY5L005");
//	    			recIntTemp.setField("YD_EQP_ID"     , sYD_EQP_ID);						//야드설비ID
//	    			recIntTemp.setField("YD_L2_WR_GP"   , "J");	//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
//	    			recIntTemp.setField("YD_L3_HD_RS_CD", YdConstant.CRN_WRK_RE_CD_NO_WRK2);	//야드L3처리결과코드 : 9999 - 크레인작업이 더 이상 없습니다.
//	    			recIntTemp.setField("YD_L3_MSG"		, "에러:현상태 이적요구 할수 없음2");	
//	     			recIntTemp.setField("YD_WRK_PROG_STAT", "1");	
//	    			ydDelegate.sendMsg(recIntTemp);
//	    			
//	    			return YdConstant.RETN_CD_TC_ERROR;
//				}
//
//			}
//			
//			
//			
//        	//------------------------------------------------------------------------------------------------------
//		
//       		
//			inRecordSet = JDTORecordFactory.getInstance().createRecordSet("Temp");
//        	/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrXYZ*/
//			inRecord1 		= JDTORecordFactory.getInstance().create();
//			inRecord1.setField("YD_STK_COL_GP"		, sYD_STK_COL_GP);						//야드설비ID
//			inRecord1.setField("YD_STK_BED_NO"  	, sYD_STK_BED_NO);
//			inRecord1.setField("YD_STK_LYR_NO"		, sYD_STK_LYR_NO);
//			inRecord1.setField("YD_STK_LYR_XAXIS"	, sYD_CRN_XAXIS);
//			inRecord1.setField("YD_STK_LYR_YAXIS"	, sYD_CRN_YAXIS);	
//						
//        	intRtnVal = ydStkLyrDao.getYdStklyr(inRecord1, inRecordSet, 403);
//
//			//크레인 스케줄이 없다면 없다는 메시지를 L2에 전송해야한다. 
//    		if( intRtnVal <= 0 ) {
//   			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//    	         * 업무기준 Desc :에서 크레인 작업지시 요구 시 크레인 작업이 더 이상 존재하지 않을 경우 크레인작업실적응답 전송
//    	       +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//    			recIntTemp = JDTORecordFactory.getInstance().create(); 
//    			recIntTemp.setField("MSG_ID"        , "YDY5L005");
//    			recIntTemp.setField("YD_EQP_ID"     , sYD_EQP_ID);						//야드설비ID
//    			recIntTemp.setField("YD_L2_WR_GP"   , "J");	//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
//    			recIntTemp.setField("YD_L3_HD_RS_CD", YdConstant.CRN_WRK_RE_CD_NO_WRK2);	//야드L3처리결과코드 : 9999 - 크레인작업이 더 이상 없습니다.
//    			recIntTemp.setField("YD_L3_MSG"		, "에러:이적할수 없음3");	
//     			recIntTemp.setField("YD_WRK_PROG_STAT", "1");	
//    			ydDelegate.sendMsg(recIntTemp);
//    		    
//				szMsg="좌표에 맞는 제품이 없습니다..";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//				m_ctx.setRollbackOnly();
//	            throw new JDTOException("<procY5CrnMvstk> " + szMsg);
////    			return YdConstant.RETN_CRN_NO_SCH;
//    		}else if(intRtnVal > 1) {
//				szMsg="1개 이상의 크레인 좌표가 존재 합니다..";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//    			
//				recIntTemp = JDTORecordFactory.getInstance().create(); 
//    			recIntTemp.setField("MSG_ID"        , "YDY5L005");
//    			recIntTemp.setField("YD_EQP_ID"     , sYD_EQP_ID);						//야드설비ID
//    			recIntTemp.setField("YD_L2_WR_GP"   , "J");	//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
//    			recIntTemp.setField("YD_L3_HD_RS_CD", "9999");	//야드L3처리결과코드 : 9999 - 크레인작업이 더 이상 없습니다.
//    			recIntTemp.setField("YD_L3_MSG"		, "에러:1개 이상의 크레인 좌표가 존재");	
//     			recIntTemp.setField("YD_WRK_PROG_STAT", "1");	
//     			
//     			ydDelegate.sendMsg(recIntTemp);
//    			
//    			szMsg = "[코일 크레인 작업지시 요구]크레인 작업이 더 이상 존재하지 않을 경우코일야드L2 크레인작업실적응답[YDY3L005] 전송 완료" ;
//                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//
//				m_ctx.setRollbackOnly();
//	            throw new JDTOException("<procY5CrnMvstk> " + szMsg);
//   		}else{
//    			inRecordSet.absolute(1);
//    			recOutTemp = JDTORecordFactory.getInstance().create();
//    			recOutTemp.setRecord(inRecordSet.getRecord());
//    			
//    			sSTL_NO		= ydDaoUtils.paraRecChkNull(recOutTemp, "STL_NO");
//    			
//    			if(sSTL_NO.equals("")){
//					szMsg="해당 제품이 없습니다.sSTL_NO : " + sSTL_NO;
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	    			
//					recIntTemp = JDTORecordFactory.getInstance().create(); 
//	    			recIntTemp.setField("MSG_ID"        , "YDY5L005");
//	    			recIntTemp.setField("YD_EQP_ID"     , sYD_EQP_ID);						//야드설비ID
//	    			recIntTemp.setField("YD_L2_WR_GP"   , "J");	//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
//	    			recIntTemp.setField("YD_L3_HD_RS_CD", "9999");	//야드L3처리결과코드 : 9999 - 크레인작업이 더 이상 없습니다.
//	    			recIntTemp.setField("YD_L3_MSG"		, "에러:해당 재료가 L3에 없습니다");	
//	     			recIntTemp.setField("YD_WRK_PROG_STAT", "1");	
// 	    			ydDelegate.sendMsg(recIntTemp);
//
// 	    			szMsg = "[코일 크레인 작업지시 요구]크레인 작업이 더 이상 존재하지 않을 경우코일야드L2 크레인작업실적응답[YDY3L005] 전송 완료" ;
//	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	    	        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//	                
//					szMsg="더이상의 크레인스케줄이 조회되지 않습니다.";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//	    			
//					m_ctx.setRollbackOnly();
//		            throw new JDTOException("<procY5CrnMvstk> " + szMsg);
//       			}	
//
////작업예약등록		
//    			inRecord1 		= JDTORecordFactory.getInstance().create();
////    			inRecord1.setField("YD_SCH_CD"				, sYD_GP + sYD_BAY_GP + "YD02MM"); 
//    			inRecord1.setField("YD_SCH_CD"				, sYD_GP + sYD_BAY_GP + "YD"+ sYD_EQP_ID_GP + "2MM"); 
//    			inRecord1.setField("STL_SH"  				, "1");
//    			inRecord1.setField("YD_USER_ID"				, sYD_EQP_ID);
//    			inRecord1.setField("FR_YD_STK_BED_NO"		, sYD_STK_COL_GP.substring(1,6)+ sYD_STK_BED_NO );
//    			inRecord1.setField("TO_YD_STK_BED_NO"		, "");	
//    			inRecord1.setField("YD_TO_LOC_DCSN_MTD"		, "S");
//    			inRecord1.setField("YD_AIM_YD_GP"			, sYD_GP);
//    			inRecord1.setField("YD_AIM_BAY_GP"			, sYD_BAY_GP);
//    			inRecord1.setField("STL_NO1"				, sSTL_NO);
//    			inRecord1.setField("YD_UP_COLL_SEQ1"		, "1");
//    			
//        		ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
//    			outRecord2 = (JDTORecord)ejbConn.trx("WrkBookInsertProc", new Class[] { JDTORecord.class }, new Object[] { inRecord1 });
//    			sRTN_CD		= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
//    			sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
//    			sYD_WBOOK_ID= StringHelper.evl(outRecord2.getFieldString("YD_WBOOK_ID"), "");
//    			sYD_SCH_CD	= StringHelper.evl(outRecord2.getFieldString("YD_SCH_CD"), "");
//    			if (!("1".equals(sRTN_CD))) {
//					szMsg="더이상의 크레인스케줄이 조회되지 않습니다.";
//
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//					recIntTemp = JDTORecordFactory.getInstance().create(); 
//	    			recIntTemp.setField("MSG_ID"        , "YDY5L005");
//	    			recIntTemp.setField("YD_EQP_ID"     , sYD_EQP_ID);						//야드설비ID
//	    			recIntTemp.setField("YD_L2_WR_GP"   , "J");	//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
//	    			recIntTemp.setField("YD_L3_HD_RS_CD", "9999");	//야드L3처리결과코드 : 9999 - 크레인작업이 더 이상 없습니다.
//	    			recIntTemp.setField("YD_L3_MSG"		, "에러:작업예약등록시 이상");	
//	     			recIntTemp.setField("YD_WRK_PROG_STAT", "1");	
// 	    			ydDelegate.sendMsg(recIntTemp);
//	    			
// 					m_ctx.setRollbackOnly();
// 		            throw new JDTOException("<procY5CrnMvstk> " + szMsg);
//
//    			} 	
//    			szMsg = "스케줄 코드 :"+ sYD_SCH_CD;
//    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
////sch 기동    			
//				JDTORecord[] inRecordarr   	= null;
//				inRecordarr = new JDTORecord[1];
//				
//				inRecordarr[0] = JDTORecordFactory.getInstance().create();
//				inRecordarr[0].setField("YD_SCH_CD"		, sYD_SCH_CD); 
//				inRecordarr[0].setField("YD_WBOOK_ID"	, sYD_WBOOK_ID); 
//				ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
//				outRecord2 = (JDTORecord) ejbConn.trx("trxRunSchedule", new Class[] { JDTORecord[].class },	new Object[] { inRecordarr });
//	
//				sRTN_CD		= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
//				sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
//				ydUtils.putLog(szSessionName, szMethodName, "//" + sRTN_MSG, YdConstant.DEBUG);
//				if (!("1".equals(sRTN_CD))) {
//					szMsg=" 크레인스케줄이 기동 실패";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//				
//					recIntTemp = JDTORecordFactory.getInstance().create(); 
//	    			recIntTemp.setField("MSG_ID"        , "YDY5L005");
//	    			recIntTemp.setField("YD_EQP_ID"     , sYD_EQP_ID);						//야드설비ID
//	    			recIntTemp.setField("YD_L2_WR_GP"   , "J");	//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
//	    			recIntTemp.setField("YD_L3_HD_RS_CD", "9999");	//야드L3처리결과코드 : 9999 - 크레인작업이 더 이상 없습니다.
//	    			recIntTemp.setField("YD_L3_MSG"		, "에러:크레인스케줄이 기동 실패");	
//	     			recIntTemp.setField("YD_WRK_PROG_STAT", "1");	
// 	    			ydDelegate.sendMsg(recIntTemp);
//	    			
// 					m_ctx.setRollbackOnly();
// 		            throw new JDTOException("<procY5CrnMvstk> " + szMsg);
//
//				} 	
//    		}	
//     
//        	
//		}catch(Exception e){
//			szMsg="Error:" +e.getMessage();
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			return YdConstant.RETN_CD_FAILURE;
//		}
//
//		szMsg="크레인이적  작업지시("+szMethodName+") 완료";
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		return YdConstant.RETN_CD_SUCCESS;
//
//	} //end of procY5CrnMvstk()
        

    
//    /**
//     * 오퍼레이션명 : C열연코일야드L2 작업현황요구 (Y5YDL013)
//     *  
//     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//     * @param msgRecord
//     * @return String
//     * @throws JDTOException
//     */
//    public String procY5CrnMvwbk(JDTORecord msgRecord)throws JDTOException  {
//
//    	YdDelegate ydDelegate   	= new YdDelegate();
//    	YdStkLyrDao ydStkLyrDao 	= new YdStkLyrDao();
//    	YdEqpDao   ydEqpDao     	= new YdEqpDao();
//    	YdCrnSchDao  ydCrnSchDao  = new YdCrnSchDao();
//    	JDTORecord recOutTemp 		= null;
//    	JDTORecord recOutTemp1 		= null;
//    	JDTORecord recIntTemp 		= null;
//    	JDTORecord inRecord 		= JDTORecordFactory.getInstance().create();
//    	JDTORecord inRecord1 		= JDTORecordFactory.getInstance().create();
//    	JDTORecord inRecord2 		= JDTORecordFactory.getInstance().create();
//        JDTORecord outRecord2		= JDTORecordFactory.getInstance().create();
//    	
//        JDTORecordSet inRecordSet 	= JDTORecordFactory.getInstance().createRecordSet("temp");
//        JDTORecordSet rsResult 		= JDTORecordFactory.getInstance().createRecordSet("temp");
//        
//    	
//        int intRtnVal 			= 0 ;
//        
//        String szMsg            = "";
//        String szMethodName     = "procY5CrnMvwbk";
//        String szOperationName	= "코일크레인작업현황요구";
//        
//        String sYD_EQP_ID  		= "";
//        String sYD_GP          	= "";
//        String sYD_BAY_GP      	= "";
//        String sYD_UP_WR_LOC	= "";
//        String sYD_UP_WR_LAYER	= "";
//        String sYD_CRN_XAXIS	= "";
//        String sYD_CRN_YAXIS	= "";
//        String sYD_CRN_ZAXIS	= "";
//
//        EJBConnector ejbConn 	= null;
//        String szRtnMsg			= null;
//        String sYD_STK_COL_GP	= "";
//        String sYD_STK_BED_NO	= "";
//        String sYD_STK_LYR_NO	= "";
//        String szFLAG_CANCEL    = "";
//        String sSTL_NO          = "";
//        String sRTN_CD			= "";
//        String sRTN_MSG			= "";	
//        String sYD_WBOOK_ID		= "";
//        String sYD_SCH_CD		= "";
//        String sYD_EQP_ID_GP	= "";
//        
//        
//        String szRcvTcCode=ydUtils.getTcCode(msgRecord);
//        if(szRcvTcCode==null || szRcvTcCode.equals("")){
//        	szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
//        	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//        	return YdConstant.RETN_CD_TC_ERROR;
//        }
//        
//        if(bDebugFlag){
//            szMsg="[DEBUG] 전문수신 : TCCODE=" +szRcvTcCode ;
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//        }
//
//        try{
//			//=============================================================
//        	// Log 테이블 등록 
//			//=============================================================
//			szMsg = "[열연 코일야드L2] 코일크레인작업현황요구 수신";
//			ydUtils.putLogMsg("H", YdConstant.YD_MONITORING_CHANNEL_H, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
//
//			
//			sYD_EQP_ID     	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
//        	
//			recIntTemp = JDTORecordFactory.getInstance().create(); 
//			recIntTemp.setField("MSG_ID"        , "YDY5L007");
//			recIntTemp.setField("YD_EQP_ID"     , sYD_EQP_ID);						//야드설비ID
//
//			ydDelegate.sendMsg(recIntTemp);	
//     
//        	
//		}catch(Exception e){
//			szMsg="Error:" +e.getMessage();
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			return YdConstant.RETN_CD_FAILURE;
//		}
//
//		szMsg="코일크레인작업현황요구 ("+szMethodName+") 완료";
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		return YdConstant.RETN_CD_SUCCESS;
//
//	} //end of procY5CrnMvwbk()
    
   
//    /**
//     * 오퍼레이션명 : C열연코일야드L2 스케줄작업요구 (Y5YDL014)
//     *  
//     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//     * @param msgRecord
//     * @return String
//     * @throws JDTOException
//     */
//    public String procY5CrnSchRequest(JDTORecord msgRecord)throws JDTOException  {
//
//    	YdDelegate ydDelegate   	= new YdDelegate();
//    	YdStkLyrDao ydStkLyrDao 	= new YdStkLyrDao();
//    	YdEqpDao   ydEqpDao     	= new YdEqpDao();
//    	YdCrnSchDao  ydCrnSchDao  = new YdCrnSchDao();
//    	JDTORecord recOutTemp 		= null;
//    	JDTORecord recOutTemp1 		= null;
//    	JDTORecord recIntTemp 		= null;
//    	JDTORecord inRecord 		= JDTORecordFactory.getInstance().create();
//    	JDTORecord inRecord1 		= JDTORecordFactory.getInstance().create();
//    	JDTORecord inRecord2 		= JDTORecordFactory.getInstance().create();
//        JDTORecord outRecord2		= JDTORecordFactory.getInstance().create();
//    	
//        JDTORecordSet inRecordSet 	= JDTORecordFactory.getInstance().createRecordSet("temp");
//        JDTORecordSet rsResult 		= JDTORecordFactory.getInstance().createRecordSet("temp");
//        JDTORecordSet rsGetYdSch = JDTORecordFactory.getInstance().createRecordSet("");
//        YdWrkbookDao YdWrkbookDao	= new YdWrkbookDao();
//        int intRtnVal 			= 0 ;
//        JDTORecord recPara 			= null;
//        String szMsg            = "";
//        String szMethodName     = "procY5CrnSchRequest";
//        String szOperationName	= "코일크레인스케줄작업요구";
//        
//        String sYD_EQP_ID  		= "";
//        String sYD_GP          	= "";
//        String sYD_BAY_GP      	= "";
//        String sYD_UP_WR_LOC	= "";
//        String sYD_UP_WR_LAYER	= "";
//        String sYD_CRN_XAXIS	= "";
//        String sYD_CRN_YAXIS	= "";
//        String sYD_CRN_ZAXIS	= "";
//
//        EJBConnector ejbConn 	= null;
//        String szRtnMsg			= null;
//        String sYD_STK_COL_GP	= "";
//        String sYD_STK_BED_NO	= "";
//        String sYD_STK_LYR_NO	= "";
//        String szFLAG_CANCEL    = "";
//        String sSTL_NO          = "";
//        String sRTN_CD			= "";
//        String sRTN_MSG			= "";	
//        String sYD_WBOOK_ID		= "";
//        String sYD_SCH_CD		= "";
//        String sYD_EQP_ID_GP	= "";
//        String sYD_SCH_FLAG		= "";
//        String sYD_SCH_PRIOR	= "";
//        
//        
//        String szRcvTcCode=ydUtils.getTcCode(msgRecord);
//        if(szRcvTcCode==null || szRcvTcCode.equals("")){
//        	szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
//        	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//        	return YdConstant.RETN_CD_TC_ERROR;
//        }
//        
//        if(bDebugFlag){
//            szMsg="[DEBUG] 전문수신 : TCCODE=" +szRcvTcCode ;
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//        }
//
//        try{
//			//=============================================================
//        	// Log 테이블 등록 
//			//=============================================================
//			szMsg = "[열연 코일야드L2] 코일크레인스케줄작업요구 수신";
//			ydUtils.putLogMsg("H", YdConstant.YD_MONITORING_CHANNEL_H, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
//
//			
//			sYD_EQP_ID     	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
//			sYD_SCH_FLAG    = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_FLAG");
//			
//			
//			
//			recIntTemp = JDTORecordFactory.getInstance().create(); 
//			recIntTemp.setField("YD_EQP_ID"     , sYD_EQP_ID);						//야드설비ID
//			recIntTemp.setField("YD_SCH_FLAG"   , sYD_SCH_FLAG);					
//			//A : 수입, B : 보급, C : 동내이적, D : 반입, E : HYSCO 출하, F : 이송  , K : 입측SCRAP추출 ,L : 출측SCRAP추출			
//
//			//크레인 작업현형  조회
//			//com.inisteel.cim.yd.dao.ydWrkbookDao.procY5CrnSchReques
//			intRtnVal = YdWrkbookDao.getYdWrkbook(recIntTemp, rsGetYdSch, 505);
//			if(intRtnVal < 0){
//				szMsg = "C열연코일L2 코일크레인스케줄작업요구 조회 중 오류 : [" +intRtnVal+"]";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return YdConstant.RETN_CD_FAILURE;
//			} else if(intRtnVal == 0){
//				szMsg = "C열연코일L2 코일크레인스케줄작업요구 조회건수 없음 : [" +intRtnVal+"]";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//				return YdConstant.RETN_CD_FAILURE;
//			}
//			
//			
//			//이전기급대상재 순위를 원상태로 복원
//			recIntTemp = JDTORecordFactory.getInstance().create(); 
//			recIntTemp.setField("YD_EQP_ID"     , sYD_EQP_ID);						//야드설비ID
//			intRtnVal = ydCrnSchDao.updYdCrnschDelay(recIntTemp, 301);
//
//			
//			
//			recPara = rsGetYdSch.getRecord(0);
//			
//			// 요구스케쥴구분
//			sYD_WBOOK_ID 	= ydDaoUtils.paraRecChkNull(recPara, "YD_WBOOK_ID");
//			sYD_SCH_CD		= ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_CD");
//			sYD_SCH_PRIOR	= ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PRIOR");
//			
//			recIntTemp = JDTORecordFactory.getInstance().create(); 
//			recIntTemp.setField("MSG_ID"        , "YDY5L007");
//			recIntTemp.setField("YD_EQP_ID"     , sYD_EQP_ID);						//야드설비ID
//			recIntTemp.setField("YD_SCH_CD"     , sYD_SCH_CD);	
//			recIntTemp.setField("YD_WBOOK_ID"   , sYD_WBOOK_ID);	
//			recIntTemp.setField("YD_SCH_PRIOR"   , sYD_SCH_PRIOR);	
//
//			ejbConn = new EJBConnector("default", "CoilJspFaEJB", this);			
//			ejbConn.trx("crnChgSchPriorCoilRecord", new Class[] { JDTORecord.class }, new Object[] { recIntTemp });
//     
//        	
//		}catch(Exception e){
//			szMsg="Error:" +e.getMessage();
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			return YdConstant.RETN_CD_FAILURE;
//		}
//
//		szMsg="코일크레인스케줄작업요구 ("+szMethodName+") 완료";
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		return YdConstant.RETN_CD_SUCCESS;
//
//	} //end of procY5CrnSchRequest()
    
    
    
//    /**
//     * 오퍼레이션명 : C열연코일야드L2 EAI전문수신
//     *  
//     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//     * @param msgRecord
//     * @return String
//     * @throws JDTOException
//     */
//    public String procY5CommRequest(JDTORecord msgRecord)throws JDTOException  {
//
//    	YdDelegate ydDelegate   	= new YdDelegate();
//
//
//        int intRtnVal 			= 0 ;
//        JDTORecord recPara 			= null;
//        String szMsg            = "";
//        String szMethodName     = "procY5CommRequest";
//        String szOperationName	= "C열연코일야드L2 EAI전문수신";
//        
//
//
//        EJBConnector ejbConn 	= null;
//        String szRtnMsg			= null;
//
//        
//        
//        String szRcvTcCode=ydUtils.getTcCode(msgRecord);
//        if(szRcvTcCode==null || szRcvTcCode.equals("")){
//        	szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
//        	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//        	return YdConstant.RETN_CD_TC_ERROR;
//        }
//        
//        if(bDebugFlag){
//            szMsg="[DEBUG] 전문수신 : TCCODE=" +szRcvTcCode ;
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//        }
//
//        try{
//		
//        	if(szRcvTcCode.equals("Y5YDL013")){
//				ejbConn = new EJBConnector("default", "CoilCraneLdHdSeEJB", this);			
//				ejbConn.trx("procY5CrnMvwbk", new Class[] { JDTORecord.class }, new Object[] { msgRecord });
//        	}else if(szRcvTcCode.equals("Y5YDL014")){
//        		ejbConn = new EJBConnector("default", "CoilCraneLdHdSeEJB", this);			
//				ejbConn.trx("procY5CrnSchRequest", new Class[] { JDTORecord.class }, new Object[] { msgRecord });
////			150626 hun Y5YDL015 CoilJspFaEJB updToPosFixCoilProc 권하위치 변경가능유무 응답 크레인무인화
//        	}else if(szRcvTcCode.equals("Y5YDL015")){
//        		ejbConn = new EJBConnector("default", "CoilJspFaEJB", this);			
//				ejbConn.trx("updToPosFixCoilProc", new Class[] { JDTORecord.class }, new Object[] { msgRecord });
////	        150626 hun Y5YDL016 CarLdLotRegFaEJB rcvY5DrvCarPlan 차량작업 예정정보 요구 크레인무인화
//        	}else if(szRcvTcCode.equals("Y5YDL016")){
//        		ejbConn = new EJBConnector("default", "CarLdLotRegFaEJB", this);			
//				ejbConn.trx("rcvY5DrvCarPlan", new Class[] { JDTORecord.class }, new Object[] { msgRecord });
////			150626 hun Y5YDL017 CarLdLotRegFaEJB rcvY5CarNotWrk 상차도 작업 불가 크레인무인화
//        	}else if(szRcvTcCode.equals("Y5YDL017")){
//        		ejbConn = new EJBConnector("default", "CarLdLotRegFaEJB", this);			
//				ejbConn.trx("rcvY5CarNotWrk", new Class[] { JDTORecord.class }, new Object[] { msgRecord });
//        	}else if(szRcvTcCode.equals("Y5YDL019")){
//        		ejbConn = new EJBConnector("default", "CoilSpecRegSeEJB", this);			
//				ejbConn.trx("rcvY5YDL019", new Class[] { JDTORecord.class }, new Object[] { msgRecord });
//        	}
//        	
//        	
//		}catch(Exception e){
//			szMsg="Error:" +e.getMessage();
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			return YdConstant.RETN_CD_FAILURE;
//		}
//
//		szMsg="C열연코일야드L2 EAI전문수신 ("+szMethodName+") 완료";
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		return YdConstant.RETN_CD_SUCCESS;
//
//	} //end of procY5CommRequest()
  //---------------------------------------------------------------------------	
} // end of class CraneLdHdSeEJBBean
