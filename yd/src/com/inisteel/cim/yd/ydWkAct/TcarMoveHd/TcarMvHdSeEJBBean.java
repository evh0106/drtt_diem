package com.inisteel.cim.yd.ydWkAct.TcarMoveHd;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.dao.ydSchRuleDao.YdSchRuleDao;
import com.inisteel.cim.yd.common.dao.ydStkBedDao.YdStkBedDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
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
import com.inisteel.cim.yd.common.dao.ydEqpDao.YdEqpDao;

/**
 * 대차이동처리 Session EJB
 *
 * @ejb.bean name="TcarMvHdSeEJB" jndi-name="TcarMvHdSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class TcarMvHdSeEJBBean extends BaseSessionBean {
	
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
	
	/**
	 * 오퍼레이션명 : C연주정정L2 대차이동실적 (C3YDL007)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return Integer
	 * @throws JDTOException
	 */
	public Integer procC3TcarMvWr(JDTORecord msgRecord)throws DAOException  {
		
		try{
			JDTORecord recRtnVal = JDTORecordFactory.getInstance().create();
			
			EJBConnector ydEjbCon = new EJBConnector("default", this);
			recRtnVal = (JDTORecord)ydEjbCon.trx("TcarMvHdSeEJB", "procC3TcarMvWrNew", msgRecord);
			
			/*
	    	 * 크레인스케쥴 호출시점
	    	        공차출발시(US) : 요청구분 - Y, 작업예약 IS NOT NULL
				공차도착시(UE) : 요청구분 - N, 작업예약 IS NOT NULL
				영차출발시(LS) : 요청구분 - Y, 작업예약 IS NOT NULL
				영차도착시(LE) : 요청구분 - N, 작업예약 IS NOT NULL
	    	 */
	    	boolean isWorkCall	= false;
	    	String sYdStatGbn 	= ydDaoUtils.paraRecChkNull(recRtnVal, "YD_STAT_GBN");
	    	String sYdReqGbn 	= ydDaoUtils.paraRecChkNull(recRtnVal, "YD_REQ_GBN");
	    	String sYdWbookId 	= ydDaoUtils.paraRecChkNull(recRtnVal, "YD_WBOOK_ID");
	    	
	    	if("".equals(sYdStatGbn)) {
	    		return YdConstant.RETN_INT_SUCCESS;
	    	}
	    	
	    	if("US".equals(sYdStatGbn)){
	    		if("Y".equals(sYdReqGbn)){
	    			if(!"".equals(sYdWbookId)){
	    				isWorkCall	= true;
	    			}
	    		}
	    	}else if("UE".equals(sYdStatGbn)){
	    		if("N".equals(sYdReqGbn)){
	    			if(!"".equals(sYdWbookId)){
	    				isWorkCall	= true;
	    			}
	    		}
	    	}else if("LS".equals(sYdStatGbn)){
	    		if("Y".equals(sYdReqGbn)){
	    			if(!"".equals(sYdWbookId)){
	    				isWorkCall	= true;
	    			}
	    		}
	    	}else if("LE".equals(sYdStatGbn)){
	    		if("N".equals(sYdReqGbn)){
	    			if(!"".equals(sYdWbookId)){
	    				isWorkCall	= true;
	    			}
	    		}
	    	}
	    	if(isWorkCall){
	    		String szRtnMsg		= YdCommonUtils.callCrnSchByWbookId(sYdWbookId, "Y");
	    	}
						
		} catch (Exception e) {
			throw new DAOException("[procC3TcarMvWr] Exception발생 : " + e.getMessage());
		}	// end try catch문
		
		return YdConstant.RETN_INT_SUCCESS;
	}
	/**
	 * 오퍼레이션명 : C연주정정L2 대차이동실적 (C3YDL007)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return JDTORecord
	 * @throws JDTOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord procC3TcarMvWrNew(JDTORecord msgRecord)throws DAOException  {
		
		JDTORecord recRtnVal = JDTORecordFactory.getInstance().create();
		
		JDTORecordSet rsResult          = null;
		JDTORecord recTcarSch           = null;
		JDTORecord recPara              = null;
		
	    String szMethodName       		= "procC3TcarMvWr";
	    String szOperationName       	= "C연주대차이동실적";
	    String szMsg              		= "";
	    String szYD_EQP_ID              = "";
	    String szYD_TCAR_MOVE_GP        = "";
	    String szYD_TCAR_MOVE_DIR       = "";
	    String szYD_BAY_GP1             = "";
	    String szYD_BAY_GP2             = "";
	    String szYD_GP                  = "";
	    String szYD_STK_COL_GP          = "";
	    int intRtnVal 					= 0;
	    int nRet                        = 0;
	    
        String szRcvTcCode=ydUtils.getTcCode(msgRecord);
        
        if( szRcvTcCode==null || szRcvTcCode.equals("") ){
        	szMsg="["+szOperationName+"] TC Code Error ("+szRcvTcCode+")";
        	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
        	return recRtnVal;
        }

        if(bDebugFlag){
            szMsg="["+szOperationName+"] [DEBUG] 전문수신 : TCCODE=" +szRcvTcCode ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }
	    
		try{
			szMsg="["+szOperationName+"] ---------------------- 메소드 시작 ----------------------";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//------------------------------------------------------------------------------------------
			//	파라미터확인
			//------------------------------------------------------------------------------------------
			
			// 설비ID
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if(szYD_EQP_ID.equals("")){
				szMsg = "["+szOperationName+"] 설비ID 값이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return recRtnVal;
			}
			
			// 야드대차이동구분
			szYD_TCAR_MOVE_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_TCAR_MOVE_GP");
			if(szYD_TCAR_MOVE_GP.equals("")){
				szMsg = "["+szOperationName+"] 야드대차이동구분 값이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return recRtnVal;
			}

			
			// 야드대차이동방향
			szYD_TCAR_MOVE_DIR = ydDaoUtils.paraRecChkNull(msgRecord, "YD_TCAR_MOVE_DIR");
			if(szYD_TCAR_MOVE_DIR.equals("")){
				szMsg = "["+szOperationName+"] 야드대차이동방향 값이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return recRtnVal;
			}
			
			
			// 현재동
			szYD_BAY_GP1 = ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP1");
			if(szYD_BAY_GP1.equals("")){
				szMsg = "["+szOperationName+"] 현재동 값이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return recRtnVal;
			}

			
			// 목적동
			szYD_BAY_GP2 = ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP2");	
			if(szYD_BAY_GP2.equals("")){
				szMsg = "["+szOperationName+"] 목적동 값이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return recRtnVal;
			}
			
			// LOG
			szMsg = "["+szOperationName+"] [1] 설비ID : " + szYD_EQP_ID;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    
			szMsg = "["+szOperationName+"] [2] 야드대차이동구분 : " + szYD_TCAR_MOVE_GP;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			szMsg = "["+szOperationName+"] [3] 야드대차이동방향 : " + szYD_TCAR_MOVE_DIR;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szMsg = "["+szOperationName+"] [4] 현재동 : " + szYD_BAY_GP1;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szMsg = "["+szOperationName+"] [5] 목적동 : " + szYD_BAY_GP2;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//------------------------------------------------------------------------------------------------------------------
			//	대차이동실적 파라미터 확인 - 대차이동중인 경우에는 업무 종료 처리
			//------------------------------------------------------------------------------------------------------------------
			if( szYD_TCAR_MOVE_GP.equals(YdConstant.YD_TCAR_MOVE_GP_MOVE)) {
				szMsg = "["+szOperationName+"] ----------------- 대차이동실적이므로 업무 종료 처리  ----------------- ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return recRtnVal;
			}
			
			// 야드
			szYD_GP = szYD_EQP_ID.substring(0, 1);

			// 적치열 구분
			szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP1 + szYD_EQP_ID.substring(2, 6);
			
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "["+szOperationName+"] 대차이동실적 수신";
			ydUtils.putLogMsg("A", YdConstant.YD_MONITORING_CHANNEL_A, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
			//------------------------------------------------------------------------------------------------------------------
			
			//------------------------------------------------------------------------------------------
	    	//	대차스케줄상태Check (공차출발,공차도착,영차출발,영차도착 Check)
			//------------------------------------------------------------------------------------------
			
			szMsg = "["+szOperationName+"] 대차["+szYD_EQP_ID+"]상태체크 시작- 이동상태["+szYD_TCAR_MOVE_GP+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	nRet = this.C3ChkTcarSchStat(msgRecord, rsResult);
	    	rsResult.absolute(1);
	    	recTcarSch = JDTORecordFactory.getInstance().create();
	    	recTcarSch.setRecord(rsResult.getRecord());
						
	    	szMsg = "["+szOperationName+"] 대차["+szYD_EQP_ID+"]상태체크 완료- 이동상태["+szYD_TCAR_MOVE_GP+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//------------------------------------------------------------------------------------------
	    	
	    	switch (nRet) {
	    		case 1	:
	    			
	    			//------------------------------------------------------------------------------------------
	    	    	//	공차출발모듈 호출
	    			//------------------------------------------------------------------------------------------
	    			szMsg = "["+szOperationName+"] 대차["+szYD_EQP_ID+"]의 공차출발 모듈 호출 시작";
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			
	    			// 2.공차출발 Method호출
	    			recRtnVal = this.C3UTcarStartWr(msgRecord, recTcarSch);
	    			
	    			szMsg = "["+szOperationName+"] 대차["+szYD_EQP_ID+"]의 공차출발 모듈 호출 완료 - 반환값 : " + intRtnVal;
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			
	    			break;
	    			//------------------------------------------------------------------------------------------
	    			
	    		case 2	:
	    			
	    			//------------------------------------------------------------------------------------------
	    	    	// 	공차도착모듈 호출
	    			//------------------------------------------------------------------------------------------
	    			szMsg = "["+szOperationName+"] 대차["+szYD_EQP_ID+"]의 공차도착 모듈 호출 시작";
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			
	    			// 3.공차도착 Method호출
	    			recRtnVal = this.C3UTcarStopWr(msgRecord, recTcarSch);
	    			
	    			szMsg = "["+szOperationName+"] 대차["+szYD_EQP_ID+"]의 공차도착 모듈 호출 완료 - 반환값 : " + intRtnVal;
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			
	    			break;
	    			//------------------------------------------------------------------------------------------
	    			
	    		case 3	:
	    			
	    			//------------------------------------------------------------------------------------------
	    	    	// 	영차출발모듈 호출
	    			//------------------------------------------------------------------------------------------
	    			szMsg = "["+szOperationName+"] 대차["+szYD_EQP_ID+"]의 영차출발 모듈 호출 시작";
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			
	    			// 4.영차출발 Method호출
	    			recRtnVal = this.C3LTcarStartWr(msgRecord, recTcarSch);
	    			
	    			szMsg = "["+szOperationName+"] 대차["+szYD_EQP_ID+"]의 영차출발 모듈 호출 완료 - 반환값 : " + intRtnVal;
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			
	    			break;
	    			
	    			//------------------------------------------------------------------------------------------
	    			
	    		case 4	:
	    			
	    			//------------------------------------------------------------------------------------------
	    	    	//	영차도착모듈 호출
	    			//------------------------------------------------------------------------------------------
	    			szMsg = "["+szOperationName+"] 대차["+szYD_EQP_ID+"]의 영차도착 모듈 호출 시작";
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			
	    			// 5.영차도착 Method호출
	    			recRtnVal = this.C3LTcarStopWr(msgRecord, recTcarSch);
	    			
	    			szMsg = "["+szOperationName+"] 대차["+szYD_EQP_ID+"]의 영차도착 모듈 호출 완료 - 반환값 : " + intRtnVal;
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			
	    			break;
	    			
	    			//------------------------------------------------------------------------------------------
	    	}
	    	String sYdStatGbn 	= ydDaoUtils.paraRecChkNull(recRtnVal, "YD_STAT_GBN");
	    	
	    	if("".equals(sYdStatGbn)) {
	    		szMsg="["+szOperationName+"] 대차 이동 실적 처리 시 오류발생 : 지원하지 않는 차량이동상태입니다. - 리턴값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		return recRtnVal;
	    	}
	    	
			//===========================================================================
	    	// 2009.12.10 호출 순서  변경
			// 적치대제원 전문을 L2로 전송
			//===========================================================================
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_INFO_SYNC_CD" , "3");						          // 1:동,2:SPAN,3:열,4:BED
			recPara.setField("YD_GP"           , szYD_GP);
			recPara.setField("YD_STK_COL_GP"   , szYD_STK_COL_GP);
			// 전문에 "S"면 출발이므로 "1" or "A" 를 넣어서 전문 호출하면 전문편집시 출발인 "S"로 송신
			// 전문에 "E"면 도착이므로 "2" or "B" 를 넣어서 전문 호출하면 전문편집시 도착인 "A"로 송신
			if(szYD_TCAR_MOVE_GP.equals("S")){
				recPara.setField("YD_CAR_PROG_STAT", "1");						      // "1", "A" : 출발(S)    "2", "B" : 도착(A)        
			} else if(szYD_TCAR_MOVE_GP.equals("E")){
				recPara.setField("YD_CAR_PROG_STAT", "2");						     
			}

			if(nRet == 1 || nRet == 2){
				recPara.setField("YD_EQP_WRK_STAT" , YdUtils.fillSpZr("L", 1, 1));    // "L" : 공차(출하)(L), "U" : 영차(반입)(U)			
			} else if(nRet == 3 || nRet == 4){
				recPara.setField("YD_EQP_WRK_STAT" , YdUtils.fillSpZr("U", 1, 1)); 
			} else {
				szMsg = "공차도 아니고 영차도 아님";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);				
			}

			if(szYD_TCAR_MOVE_GP.equals("S") || szYD_TCAR_MOVE_GP.equals("E")){
				if(nRet == 1 || nRet == 2 || nRet == 3 || nRet == 4){
					YdCommonUtils.sndStrPosSpecToL2(recPara);
				}
			}
			//===========================================================================	    	
	    	
		}catch(Exception e){
			szMsg="["+szOperationName+"] 대차 이동 실적 처리 Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			throw new DAOException(szMsg);
		}
	
		szMsg="["+szOperationName+"] ---------------------- 메소드 끝 ----------------------";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        
        return recRtnVal;
	} //end of procC3TcarMvWr()
		

	/**
	 * 오퍼레이션명 : 대차스케줄상태Check
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord, rsTarSch
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public int C3ChkTcarSchStat(JDTORecord msgRecord, JDTORecordSet rsTarSch)throws JDTOException  {
		YdTcarSchDao ydTcarSchDao = new YdTcarSchDao();
		YdEqpDao     ydEqpDao     = new YdEqpDao();
		
		JDTORecordSet rsResult          = null;
		JDTORecord    recOutTemp        = null;
		JDTORecord    recInTemp         = null;

	    
	    String szMsg              		= "";
	    String szMethodName       		= "C3ChkTcarSchStat";
	    String szOperationName			= "대차스케줄상태체크";
	    
	    String szEqpId                  = "";
	    String szYD_TCAR_SCH_ID			= null;
	    String szMoveGp                 = "";
	    String szEqpWrkStat             = "";
	    String szYD_BAY_GP1				= null;
	    
	    int intRtnVal                   = 0;

	    
	    try{
	    	szMsg="["+szOperationName+"] -------------------- 메소드 시작 --------------------";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
	    	
	    	//------------------------------------------------------------------------------------------
	    	//	대차설비ID로 대차 스케줄을 조회한다.
	    	//------------------------------------------------------------------------------------------
	    	szEqpId  				= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
	    	szMoveGp 				= ydDaoUtils.paraRecChkNull(msgRecord, "YD_TCAR_MOVE_GP");
	    	szYD_BAY_GP1			= ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP1");				//현재동(출발 시 - 출발동, 도착 시 - 도착동)
	    	
	    	szMsg="["+szOperationName+"] 대차설비ID["+szEqpId+"]로 대차스케줄 조회 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_EQP_ID", szEqpId);
	    	
	    	intRtnVal = ydTcarSchDao.getYdTcarsch(recInTemp, rsResult, 4);
	    	
			if(intRtnVal <= 0) {
				if(intRtnVal == 0) {
					szMsg="["+szOperationName+"] 대차설비ID["+szEqpId+"]로 대차스케줄 조회 시 data not found";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}else if(intRtnVal == -2) {
					szMsg="["+szOperationName+"] 대차설비ID["+szEqpId+"]로 대차스케줄 조회 시 parameter error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				return intRtnVal = -1;
			}
	    	
			szMsg="["+szOperationName+"] 대차설비ID["+szEqpId+"]로 대차스케줄 조회 완료 - 반환값 : " + intRtnVal;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//------------------------------------------------------------------------------------------
	    	
	    	rsResult.absolute(1);
	    	recOutTemp = JDTORecordFactory.getInstance().create();
	    	recOutTemp.setRecord(rsResult.getRecord());

	    	szYD_TCAR_SCH_ID 		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TCAR_SCH_ID");
	    	
	        //대차 스케줄이 공차인지 상차인지 Check한다.
	    	szEqpWrkStat 			= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_WRK_STAT");

	    	//대차스케줄은 리턴하고
	    	rsTarSch.addRecord(recOutTemp);

	    	//대차설비Table에 야드동구분을 update위한 Setting
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_EQP_ID", szEqpId);
	    	
	    	//------------------------------------------------------------------------------------------
	    	//	출발 실적인 경우
	    	//------------------------------------------------------------------------------------------
	    	if(szMoveGp.equals("S")) {
	    		
	    		//------------------------------------------------------------------------------------------
		    	//	출발 실적인 경우는 대차설비의 현재동 정보를 Clear
		    	//------------------------------------------------------------------------------------------
	    		
				szMsg="["+szOperationName+"] 출발실적이므로 설비Table에 대차설비ID["+szEqpId+"]의 현재동 정보를 Clear 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
	    		recInTemp.setField("YD_CURR_BAY_GP", "");
	    		intRtnVal = ydEqpDao.updYdEqp(recInTemp, 0);
	    		if(intRtnVal <= 0) {
	    			if(intRtnVal == 0) {
	    				szMsg="["+szOperationName+"] 출발실적이므로 설비Table에 대차설비ID["+szEqpId+"]의 현재동 정보를 Clear 시 : data not found";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}else if(intRtnVal == -1) {
	    				szMsg="["+szOperationName+"] 출발실적이므로 설비Table에 대차설비ID["+szEqpId+"]의 현재동 정보를 Clear 시 : duplicate data,";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
	    			}else if(intRtnVal == -2) {
	    				szMsg="["+szOperationName+"] 출발실적이므로 설비Table에 대차설비ID["+szEqpId+"]의 현재동 정보를 Clear 시 : parameter error";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}else if(intRtnVal == -3){
	    				szMsg="["+szOperationName+"] 출발실적이므로 설비Table에 대차설비ID["+szEqpId+"]의 현재동 정보를 Clear 시 : execution failed";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}
	    			//return intRtnVal = -1;
	    			throw new DAOException(szMsg);
	    		}

	    		szMsg="["+szOperationName+"] 출발실적이므로 설비Table에 대차설비ID["+szEqpId+"]의 현재동 정보를 Clear 완료 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//------------------------------------------------------------------------------------------
				
				
				//------------------------------------------------------------------------------------------
	    		//	공대차 출발인경우
				//------------------------------------------------------------------------------------------
	    		if(szEqpWrkStat.equals("U")) {
	    			
	    			szMsg="["+szOperationName+"] 대차설비ID["+szEqpId+"]로 대차스케줄["+szYD_TCAR_SCH_ID+"] - 공대차출발";
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			
	    			return intRtnVal = 1;
	    		//------------------------------------------------------------------------------------------
	    		//	영대차 출발인 경우
	    		//------------------------------------------------------------------------------------------
	    		}else if(szEqpWrkStat.equals("L")) {
	    			
	    			szMsg="["+szOperationName+"] 대차설비ID["+szEqpId+"]로 대차스케줄["+szYD_TCAR_SCH_ID+"] - 영대차출발";
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			
	    			return intRtnVal = 3;
	    			
	    		}
	    		
	    		
	    	//------------------------------------------------------------------------------------------
	    	//	도착 실적인 경우	
	    	//------------------------------------------------------------------------------------------
	    	}else if (szMoveGp.equals("E")) {
	    		
	    		//------------------------------------------------------------------------------------------
		    	//	도착 실적인 경우 대차설비의 현재동 정보를 수정
		    	//------------------------------------------------------------------------------------------
	    		szMsg="["+szOperationName+"] 도착실적이므로 설비Table에 대차설비ID["+szEqpId+"]의 현재동["+szYD_BAY_GP1+"] 정보를 수정 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//현재동 업데이트
	    		recInTemp.setField("YD_CURR_BAY_GP", szYD_BAY_GP1);
	    		intRtnVal = ydEqpDao.updYdEqp(recInTemp, 0);
	    		if(intRtnVal <= 0) {
	    			if(intRtnVal == 0) {
	    				szMsg="["+szOperationName+"] 도착실적이므로 설비Table에 대차설비ID["+szEqpId+"]의 현재동["+szYD_BAY_GP1+"] 정보를 수정 시 : data not found";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}else if(intRtnVal == -1) {
	    				szMsg="["+szOperationName+"] 도착실적이므로 설비Table에 대차설비ID["+szEqpId+"]의 현재동["+szYD_BAY_GP1+"] 정보를 수정 시 : duplicate data,";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
	    			}else if(intRtnVal == -2) {
	    				szMsg="["+szOperationName+"] 도착실적이므로 설비Table에 대차설비ID["+szEqpId+"]의 현재동["+szYD_BAY_GP1+"] 정보를 수정 시 : parameter error";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}else if(intRtnVal == -3){
	    				szMsg="["+szOperationName+"] 도착실적이므로 설비Table에 대차설비ID["+szEqpId+"]의 현재동["+szYD_BAY_GP1+"] 정보를 수정 시 : execution failed";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}
	    			//return intRtnVal = -1;
	    			throw new DAOException(szMsg);
	    		}

	    		szMsg="["+szOperationName+"] 도착실적이므로 설비Table에 대차설비ID["+szEqpId+"]의 현재동["+szYD_BAY_GP1+"] 정보를 수정 완료 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//------------------------------------------------------------------------------------------
				
	    		
	    		//------------------------------------------------------------------------------------------
	    		//	공대차 도착인경우
	    		//------------------------------------------------------------------------------------------
	    		if(szEqpWrkStat.equals("U")) {
	    			
	    			szMsg="["+szOperationName+"] 대차설비ID["+szEqpId+"]로 대차스케줄["+szYD_TCAR_SCH_ID+"] - 공대차도착";
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			
	    			return intRtnVal = 2;
	    			
	    		//------------------------------------------------------------------------------------------
	    		//	영대차 도착인 경우	
	    		//------------------------------------------------------------------------------------------
	    		}else if(szEqpWrkStat.equals("L")) {
	    			
	    			szMsg="["+szOperationName+"] 대차설비ID["+szEqpId+"]로 대차스케줄["+szYD_TCAR_SCH_ID+"] - 영대차도착";
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			
	    			return intRtnVal = 4;
	    			
	    		}
	    	}

	    	
 		}catch(Exception e){
	
			szMsg="["+szOperationName+"] Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			//return intRtnVal = -1;
			throw new DAOException(szMsg);
		}
	
	
 		szMsg="["+szOperationName+"] -------------------- 메소드 끝 --------------------";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		return intRtnVal = 1;
	} //end of C3ChkTcarSchStat()
	
	
	
	
	
	
	
	
	/**
	 * 오퍼레이션명 : 공차출발실적
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord, recTcarSch
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public JDTORecord C3UTcarStartWr(JDTORecord msgRecord, JDTORecord recTcarSch)throws JDTOException  {
		
		YdStkBedDao ydStkBedDao = new YdStkBedDao();
		YdTcarSchDao ydTcarSchDao = new YdTcarSchDao();
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		
		JDTORecord recInTemp          	= null;
		JDTORecord recRtnVal 			= JDTORecordFactory.getInstance().create();
		
	    String szMsg              		= "";
	    String szMethodName       		= "C3UTcarStartWr";
	    String szOperationName			= "대차공차출발실적";
	    
	    int intRtnVal                   = 0;
	    
	    String szStkColGp               = "";
	    String szStkBedNo               = "01";
	    String szSchReqGp				= "";
	    String szCarldStopLoc           = "";
	    String szWbookId                = "";
	    String szYD_EQP_ID              = "";
	    String szYD_TCAR_SCH_ID         = "";
	    String szYD_BAY_GP1				= null;
	    String szLD_START_SCH_REQ_YN	= null;
	    
	    try{
	    	szMsg="["+szOperationName+"] --------------------- 메소드 시작 ---------------------";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
			/*
			YD_EQP_ID 야드설비ID
			YD_TCAR_MOVE_GP 야드대차이동구분
			YD_TCAR_MOVE_DIR 야드대차이동방향
			YD_BAY_GP1 야드동구분1 현재동
			YD_BAY_GP2 야드동구분2 목적동
			*/
	    	
	    	//--------------------------------------------------------------------------------------------
	    	//	대차설비ID로 대차스케줄을 조회
	    	//--------------------------------------------------------------------------------------------
	    	szYD_EQP_ID 		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");					//대차설비ID
	    	szYD_BAY_GP1		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP1");					//현재동
			//--------------------------------------------------------------------------------------------
			
			//--------------------------------------------------------------------------------------------
			//	대차스케줄의 차량진행상태를 상차출발로 수정
			//--------------------------------------------------------------------------------------------
			szYD_TCAR_SCH_ID = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_TCAR_SCH_ID");
			
			szMsg="["+szOperationName+"] 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태[1]를 상차출발로 수정 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_TCAR_SCH_ID", 			szYD_TCAR_SCH_ID);
	    	recInTemp.setField("YD_CAR_PROG_STAT", 			YdConstant.YD_CARLD_LEV);
	    	intRtnVal = ydTcarSchDao.updYdTcarsch(recInTemp, 0);
			if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="["+szOperationName+"] 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태[1]를 상차출발로 수정 시 : data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}else if(intRtnVal == -1) {
    				szMsg="["+szOperationName+"] 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태[1]를 상차출발로 수정 시 : duplicate data,";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
    			}else if(intRtnVal == -2) {
    				szMsg="["+szOperationName+"] 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태[1]를 상차출발로 수정 시 : parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}else if(intRtnVal == -3){
    				szMsg="["+szOperationName+"] 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태[1]를 상차출발로 수정 시 : execution failed";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}
    			//return intRtnVal = -1;
    			throw new DAOException(szMsg);
    		}
			
			szMsg="["+szOperationName+"] 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태[1]를 상차출발로 수정 완료 - 반환값 : " + intRtnVal;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//--------------------------------------------------------------------------------------------
			
			//--------------------------------------------------------------------------------------------
	    	//	출발위치 적치Bed상태 비활성화
			//--------------------------------------------------------------------------------------------
	    	szStkColGp = szYD_EQP_ID.substring(0,1) + szYD_BAY_GP1 + szYD_EQP_ID.substring(2);
	    	
	    	szMsg="["+szOperationName+"] 대차설비의 출발위치베드[적치열:"+szStkColGp+", 적치베드:"+szStkBedNo+"]를 비활성화 처리 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_STK_COL_GP", 			szStkColGp);
	    	recInTemp.setField("YD_STK_BED_NO", 			szStkBedNo);
	    	recInTemp.setField("YD_STK_BED_ACT_STAT", 		YdConstant.YD_STK_BED_INACTIVE);
	    	intRtnVal = ydStkBedDao.updYdStkbed(recInTemp, 0);
			if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="["+szOperationName+"] 대차설비의 출발위치베드[적치열:"+szStkColGp+", 적치베드:"+szStkBedNo+"]를 비활성화 처리 시 data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}else if(intRtnVal == -1) {
    				szMsg="["+szOperationName+"] 대차설비의 출발위치베드[적치열:"+szStkColGp+", 적치베드:"+szStkBedNo+"]를 비활성화 처리 시 duplicate data,";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
    			}else if(intRtnVal == -2) {
    				szMsg="["+szOperationName+"] 대차설비의 출발위치베드[적치열:"+szStkColGp+", 적치베드:"+szStkBedNo+"]를 비활성화 처리 시 parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}else if(intRtnVal == -3){
    				szMsg="["+szOperationName+"] 대차설비의 출발위치베드[적치열:"+szStkColGp+", 적치베드:"+szStkBedNo+"]를 비활성화 처리 시 execution failed";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}
    			//return intRtnVal = -1;
    			throw new DAOException(szMsg);
    		}
			
			szMsg="["+szOperationName+"] 대차설비의 출발위치베드[적치열:"+szStkColGp+", 적치베드:"+szStkBedNo+"]를 비활성화 처리 완료 - 반환값 : " + intRtnVal;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//--------------------------------------------------------------------------------------------
			
			//--------------------------------------------------------------------------------------------
			//	출발위치 적치단 비활성화
			//--------------------------------------------------------------------------------------------
			szMsg="["+szOperationName+"] 대차설비의 출발위치단[적치열:"+szStkColGp+", 적치베드:"+szStkBedNo+"]를 비활성화 처리 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_STK_COL_GP", 			szStkColGp);
			recInTemp.setField("YD_STK_BED_NO", 			szStkBedNo);
			recInTemp.setField("YD_STK_LYR_ACT_STAT", 		"C");
			recInTemp.setField("STL_NO", 					"");
			recInTemp.setField("YD_STK_LYR_MTL_STAT", 		"E");
	    	
			intRtnVal = ydStkLyrDao.updYdStklyrYdStkColGpBedNo(recInTemp);
			
			if(intRtnVal <= 0) {
				szMsg = "["+szOperationName+"] 대차설비의 출발위치단[적치열:"+szStkColGp+", 적치베드:"+szStkBedNo+"]를 비활성화 처리 시 Error!! - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException(szMsg);
			}
			
			szMsg="["+szOperationName+"] 대차설비의 출발위치단[적치열:"+szStkColGp+", 적치베드:"+szStkBedNo+"]를 비활성화 처리 완료 - 반환값 : " + intRtnVal;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//--------------------------------------------------------------------------------------------
			
	    	szSchReqGp     		= ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARLD_SCH_REQ_GP");					//상차스케줄요청구분
	    	szCarldStopLoc 		= ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARLD_STOP_LOC");					//상차정지위치
	    	szWbookId      		= ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARLD_WRK_BOOK_ID");				//상차작업예약ID
	    	
	    	//--------------------------------------------------------------------------------------------
	    	//	상차 스케줄 요청 구분이 공차 출발이고 대차스케줄에 차량상차작업이 등록되어 있으면
	    	//	상차정지위치 활성화, 상차작업예약에 대한 크레인스케줄 기동
	    	//	--> 1. 변경내역 : 상차 스케줄 요청 구분을 BRE에 등록된 상차출발시스케줄 요청여부를 사용하여 판단하도록 로직 변경
	    	//	수정일 : 1. 2010.02.23 임춘수
	    	//--------------------------------------------------------------------------------------------
	    	szLD_START_SCH_REQ_YN = YdCommonUtils.getLdStartSchReqYN(szYD_EQP_ID);
	    	
	    	//--------------------------------------------------------------------------------------------
	    	//	상차출발시 스케쥴 요청여부를 판단해서 맵활성화 판단되도록 변경 - 임춘수 2010.02.25
	    	if(szLD_START_SCH_REQ_YN.equals("Y") ) {				
	    		
	    		szMsg="["+szOperationName+"] 대차스케줄["+szYD_TCAR_SCH_ID+"]의 상차출발시스케줄 요청여부["+szLD_START_SCH_REQ_YN+"]이 공차출발이고 차량상차작업["+szWbookId+"]이므로 "; 
	    		szMsg+="상차정지위치[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 활성화, 크레인스케줄 호출";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//--------------------------------------------------------------------------------------------
	    		//	상차정지위치 베드 활성화
	    		//--------------------------------------------------------------------------------------------
				szMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		
		    	recInTemp = JDTORecordFactory.getInstance().create();
		    	recInTemp.setField("YD_STK_COL_GP", 				szCarldStopLoc);
		    	recInTemp.setField("YD_STK_BED_NO", 				szStkBedNo);
		    	recInTemp.setField("YD_STK_BED_ACT_STAT", 			"L");
		    	intRtnVal = ydStkBedDao.updYdStkbed(recInTemp, 0);
				if(intRtnVal <= 0) {
	    			if(intRtnVal == 0) {
	    				szMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 시 data not found";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}else if(intRtnVal == -1) {
	    				szMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 시 duplicate data,";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
	    			}else if(intRtnVal == -2) {
	    				szMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 시 parameter error";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}else if(intRtnVal == -3){
	    				szMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 시 execution failed";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}
	    			//return intRtnVal = -1;
	    			throw new DAOException(szMsg);
	    		}
				
				szMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 완료 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//--------------------------------------------------------------------------------------------
		    	
				//--------------------------------------------------------------------------------------------
		    	//	상차정지위치 단정보 활성화
				//--------------------------------------------------------------------------------------------
				szMsg="["+szOperationName+"] 상차정지위치단[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_STK_COL_GP", 			szCarldStopLoc);
				recInTemp.setField("YD_STK_BED_NO", 			szStkBedNo);
				recInTemp.setField("STL_NO", 					"");
				recInTemp.setField("YD_STK_LYR_ACT_STAT", 		"E");
				recInTemp.setField("YD_STK_LYR_MTL_STAT", 		"E");
		    	
				intRtnVal = ydStkLyrDao.updYdStklyrYdStkColGpBedNo2(recInTemp);
				if(intRtnVal <= 0) {
					szMsg = "["+szOperationName+"] 상차정지위치단[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 시 오류발생 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					throw new DAOException(szMsg);
				}
				
				szMsg="["+szOperationName+"] 상차정지위치단[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 완료 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//--------------------------------------------------------------------------------------------

	    	}
	    	//--------------------------------------------------------------------------------------------
	    	
	    	recRtnVal.setField("YD_STAT_GBN" , "US");						          
	    	recRtnVal.setField("YD_REQ_GBN"  , szLD_START_SCH_REQ_YN);
	    	recRtnVal.setField("YD_WBOOK_ID" , szWbookId);
	    	
 		}catch(Exception e){
	
			szMsg="["+szOperationName+"]  Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMsg);
		}
	
	
 		szMsg="["+szOperationName+"] --------------------- 메소드 끝 ---------------------";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		return recRtnVal;
	} //end of C3UTcarStartWr()
	
	
	
	
	
	
	

	/**
	 * 오퍼레이션명 : 공차도착실적
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord, recTcarSch
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public JDTORecord C3UTcarStopWr(JDTORecord msgRecord, JDTORecord recTcarSch)throws JDTOException  {
		
		YdStkBedDao ydStkBedDao = new YdStkBedDao();
		YdTcarSchDao ydTcarSchDao       = new YdTcarSchDao();
		YdStkLyrDao ydStkLyrDao         = new YdStkLyrDao();
		
		JDTORecord    recInTemp         = null;
		JDTORecord 	  recRtnVal 		= JDTORecordFactory.getInstance().create();
		
	    String szMsg              		= "";
	    String szMethodName       		= "C3UTcarStopWr";
	    String szOperationName			= "대차공차도착실적";
	    
	    int intRtnVal                   = 0;
	    
	    String szStkColGp               = "";
	    String szStkBedNo               = "01";
	    String szSchReqGp               = "";
	    String szCarldStopLoc           = "";
	    String szWbookId                = "";
	    String szYD_TCAR_SCH_ID         = "";
	    String szYD_TO_BAY              = "";
	    String szYD_GP                  = "";
	    String szYD_EQP_ID              = "";
	    String szYD_BAY_GP1             = "";
	    String szYD_BAY_GP2             = "";
	    String szYD_CAR_PROG_STAT		= "";
	    String szLD_START_SCH_REQ_YN	= null;

	    try{	
	    	
	    	szMsg="["+szOperationName+"] --------------------- 메소드 시작 ---------------------";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
			/*
			YD_EQP_ID 야드설비ID
			YD_TCAR_MOVE_GP 야드대차이동구분
			YD_TCAR_MOVE_DIR 야드대차이동방향
			YD_BAY_GP1 야드동구분1 현재동
			YD_BAY_GP2 야드동구분2 목적동
			*/
	    	//스케줄 요청 구분판단
	    	szSchReqGp     = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARLD_SCH_REQ_GP");					//상차스케줄요청구분(상차출발, 상차도착)
	    	szCarldStopLoc = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARLD_STOP_LOC");					//상차정지위치
	    	szWbookId      = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARLD_WRK_BOOK_ID");					//상차작업예약ID
	    	szYD_TO_BAY    = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_TO_BAY");							//
	    	szYD_EQP_ID    = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");								//대차설비ID	    	
	    	szYD_BAY_GP1   = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_BAY_GP1");							//현재동
	    	szYD_BAY_GP2   = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_BAY_GP2");							//목적동
			
			szYD_TCAR_SCH_ID = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_TCAR_SCH_ID");
			
			recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_TCAR_SCH_ID", szYD_TCAR_SCH_ID);
	    	
	    	//20090702 김진욱 대차진행상태값 등록
	    	if(szWbookId.equals("")){
	    		//--------------------------------------------------------------------------------------------------
	    		//	상차작업예약이 없는 이유는 작업자가 지정한 동으로 이동했거나 홈동으로 이동한 경우이기때문에...상차도착으로 처리하지 않고
	    		//	공차대기로 수정
	    		//--------------------------------------------------------------------------------------------------
	    		szYD_CAR_PROG_STAT	= "0";
	    		
	    	}else{
	    		//--------------------------------------------------------------------------------------------------
	    		//	상차작업예약이 존재하므로 상차도착으로 수정
	    		//--------------------------------------------------------------------------------------------------
	    		szYD_CAR_PROG_STAT	= "2";
	    	}
	    	
	    	recInTemp.setField("YD_CAR_PROG_STAT", 		szYD_CAR_PROG_STAT);			//차량진행상태
	    	
	    	//--------------------------------------------------------------------------------------------------
    		//	대차스케줄의 차량진행상태를 수정
    		//--------------------------------------------------------------------------------------------------
	    	szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태를 ["+szYD_CAR_PROG_STAT+"]로 수정 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
	    	intRtnVal = ydTcarSchDao.updYdTcarsch(recInTemp, 0);
			if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태를 ["+szYD_CAR_PROG_STAT+"]로 수정 시 : data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}else if(intRtnVal == -1) {
    				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태를 ["+szYD_CAR_PROG_STAT+"]로 수정 시 : duplicate data,";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
    			}else if(intRtnVal == -2) {
    				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태를 ["+szYD_CAR_PROG_STAT+"]로 수정 시 : parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}else if(intRtnVal == -3){
    				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태를 ["+szYD_CAR_PROG_STAT+"]로 수정 시 : execution failed";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}
    			throw new DAOException(szMsg);
    		}
			szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태를 ["+szYD_CAR_PROG_STAT+"]로 수정 완료 - 반환값 : " + intRtnVal;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	//--------------------------------------------------------------------------------------------------
			
			//--------------------------------------------------------------------------------------------------
	    	//	상차 스케줄 요청 구분이 공차 도착이라면 상차정지위치 활성화, 대차스케줄에 상차작업예약이 존재하면 크레인스케줄 호출
			//	--> 1. 상차 스케줄 요청 구분을 BRE Rule에서 상차출발시스케줄요청여부을 조회해서 처리하도록 로직 변경
			//	수정일 : 1. 2010.02.23 임춘수
			//--------------------------------------------------------------------------------------------------
			szLD_START_SCH_REQ_YN	= YdCommonUtils.getLdStartSchReqYN(szYD_EQP_ID);
			
			if(szLD_START_SCH_REQ_YN.equals("N")) {
	    		
	    		szMsg="["+szOperationName+"] 대차스케줄["+szYD_TCAR_SCH_ID+"]의 상차스케줄요청구분["+szSchReqGp+"]이 공차도착이면 "; 
	    		szMsg+="상차정지위치[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 활성화";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//--------------------------------------------------------------------------------------------
	    		//	상차정지위치 베드 활성화
	    		//--------------------------------------------------------------------------------------------
				szMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		
	    		recInTemp = JDTORecordFactory.getInstance().create();
		    	recInTemp.setField("YD_STK_COL_GP", 			szCarldStopLoc);
		    	recInTemp.setField("YD_STK_BED_NO", 			szStkBedNo);
		    	recInTemp.setField("YD_STK_BED_ACT_STAT", 		"L");
		    	intRtnVal = ydStkBedDao.updYdStkbed(recInTemp, 0);
				if(intRtnVal <= 0) {
	    			if(intRtnVal == 0) {
	    				szMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 시 data not found";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}else if(intRtnVal == -1) {
	    				szMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 시 duplicate data,";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
	    			}else if(intRtnVal == -2) {
	    				szMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 시 parameter error";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}else if(intRtnVal == -3){
	    				szMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 시 execution failed";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}
	    			throw new DAOException(szMsg);
	    		}
				
				szMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 완료 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//--------------------------------------------------------------------------------------------------
		    	//	상차정지위치 단정보 활성화
				//--------------------------------------------------------------------------------------------------
				szMsg="["+szOperationName+"] 상차정지위치단[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_STK_COL_GP", 			szCarldStopLoc);
				recInTemp.setField("YD_STK_BED_NO", 			szStkBedNo);
				recInTemp.setField("STL_NO", 					"");
				recInTemp.setField("YD_STK_LYR_ACT_STAT", 		"E");
				recInTemp.setField("YD_STK_LYR_MTL_STAT", 		"E");

				intRtnVal = ydStkLyrDao.updYdStklyrYdStkColGpBedNo2(recInTemp);
				if(intRtnVal <= 0) {
					szMsg = "["+szOperationName+"] 상차정지위치단[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 시 Error!!  - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					throw new DAOException(szMsg);
				}
				
				szMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 완료 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//--------------------------------------------------------------------------------------------------
	    	}
			
			recRtnVal.setField("YD_STAT_GBN" , "UE");						          
			recRtnVal.setField("YD_REQ_GBN"  , szLD_START_SCH_REQ_YN);
			recRtnVal.setField("YD_WBOOK_ID" , szWbookId);
			
 		}catch(Exception e){
	
			szMsg="["+szOperationName+"] Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			throw new DAOException(szMsg);
		}
	
	
 		szMsg="["+szOperationName+"] --------------------- 메소드 끝 ---------------------";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		return recRtnVal;
	} //end of C3UTcarStopWr()
	
	
	
	
	
	

	/**
	 * 오퍼레이션명 : 영차출발실적
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord, recTcarSch
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public JDTORecord C3LTcarStartWr(JDTORecord msgRecord, JDTORecord recTcarSch)throws JDTOException  {
		YdStkBedDao ydStkBedDao = new YdStkBedDao();
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		YdWrkbookMtlDao  ydWrkbookMtlDao  = new YdWrkbookMtlDao();
		YdTcarSchDao ydTcarSchDao = new YdTcarSchDao();
		
		JDTORecordSet rsResult          = null;
		JDTORecord    recInTemp         = null;
		JDTORecord    recOutTemp        = null;
		JDTORecord 	  recRtnVal 		= JDTORecordFactory.getInstance().create();
		
	    String szMsg              		= "";
	    String szMethodName       		= "C3LTcarStartWr";
	    String szOperationName			= "대차영차출발실적";
	    
	    String szSchReqGp               = "";
	    String szCarldStopLoc           = "";
	    String szCarudStopLoc           = "";
	    String szStkColGp               = "";
	    String szStkBedNo               = "01";
	    String szYD_STK_LYR_NO			= null;
	    String szWbookId                = "";
	    String szYD_EQP_ID              = "";
	    String szYD_TCAR_SCH_ID         = "";
	    String szSTL_NO					= null;
	    
	    int intRtnVal                   = 0;
	    
	    try{
	    	szMsg="["+szOperationName+"] ----------------------- 메소드 시작 -----------------------";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
	    	szYD_EQP_ID 	 = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			szYD_TCAR_SCH_ID = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_TCAR_SCH_ID");
			
			//-------------------------------------------------------------------------------------------------
			//	대차스케줄의 차량진행상태를 영차출발로 수정
			//-------------------------------------------------------------------------------------------------
			szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태를 영차출발(하차출발)[A]상태로 수정 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_TCAR_SCH_ID", 			szYD_TCAR_SCH_ID);
	    	recInTemp.setField("YD_CAR_PROG_STAT", 			"A");
	    	intRtnVal = ydTcarSchDao.updYdTcarsch(recInTemp, 0);
			if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태를 영차출발(하차출발)[A]상태로 수정 시 : data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}else if(intRtnVal == -1) {
    				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태를 영차출발(하차출발)[A]상태로 수정 시 : duplicate data,";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
    			}else if(intRtnVal == -2) {
    				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태를 영차출발(하차출발)[A]상태로 수정 시 : parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}else if(intRtnVal == -3){
    				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태를 영차출발(하차출발)[A]상태로 수정 시 : execution failed";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}
    			throw new DAOException(szMsg);
    		}
			szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태를 영차출발(하차출발)[A]상태로 수정 완료 - 반환값 : " + intRtnVal;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//-------------------------------------------------------------------------------------------------
			
			//-------------------------------------------------------------------------------------------------
	    	//	상차 정지위치의 베드상태 비활성화하고 적치단정보 Clear
	    	//-------------------------------------------------------------------------------------------------
	    	szSchReqGp     			= ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARUD_SCH_REQ_GP");
	    	szCarldStopLoc 			= ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARLD_STOP_LOC");
	    	szCarudStopLoc 			= ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARUD_STOP_LOC");
	    	
	    	szMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 비활성화 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_STK_COL_GP", 			szCarldStopLoc);
	    	recInTemp.setField("YD_STK_BED_NO", 			szStkBedNo);
	    	recInTemp.setField("YD_STK_BED_ACT_STAT", 		"C");
	    	intRtnVal = ydStkBedDao.updYdStkbed(recInTemp, 0);
			if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 비활성화 시 data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}else if(intRtnVal == -1) {
    				szMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 비활성화 시 duplicate data,";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
    			}else if(intRtnVal == -2) {
    				szMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 비활성화 시 parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}else if(intRtnVal == -3){
    				szMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 비활성화 시 execution failed";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}
    			throw new DAOException(szMsg);
    		}
			szMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 비활성화 완료 - 반환값 : " + intRtnVal;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//-------------------------------------------------------------------------------------------------
			
			//-------------------------------------------------------------------------------------------------
			//	상차정지위치 단정보 Clear, 적치단활성상태를 비활성화로...
			//-------------------------------------------------------------------------------------------------
			
			szMsg="["+szOperationName+"] 상차정지위치단[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 비활성화 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_STK_COL_GP", 			szCarldStopLoc);
			recInTemp.setField("YD_STK_BED_NO", 			szStkBedNo);
			recInTemp.setField("STL_NO", 					"");
			recInTemp.setField("YD_STK_LYR_ACT_STAT", 		"C");
			recInTemp.setField("YD_STK_LYR_MTL_STAT", 		"E");
	    	
			intRtnVal = ydStkLyrDao.updYdStklyrYdStkColGpBedNo2(recInTemp);
			if(intRtnVal <= 0) {
				szMsg = "["+szOperationName+"] 상차정지위치단[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 비활성화 시 Error!! - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException(szMsg);
			}
	    	
			szMsg="["+szOperationName+"] 상차정지위치단[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 비활성화 완료 - 반환값 : " + intRtnVal;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//-------------------------------------------------------------------------------------------------
			
			//-------------------------------------------------------------------------------------------------
	    	//	하차스케줄 요청 구분이 영차 출발이라면 하차정지위치 맵 활성화, 대차스케줄의 하차작업예약에 대한 크레인스케줄 호출
			//	1. 하차스케쥴요청구분을 BRE Rule에서 가져오는 것으로 변경 2010.02.24 임춘수
			//-------------------------------------------------------------------------------------------------
			
			String szUL_START_SCH_REQ_YN = YdCommonUtils.getUlStartSchReqYN(szYD_EQP_ID);
			
			if(szUL_START_SCH_REQ_YN.equals("Y")) {
	    		
	    		szWbookId = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARUD_WRK_BOOK_ID");
	    		
	    		szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차스케줄요청구분["+szSchReqGp+"]이 영차 출발이므로 하차정지위치 맵 활성화, 하차작업예약["+szWbookId+"]에 대한 크레인스케줄 호출";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		
	    		//-------------------------------------------------------------------------------------------------
		    	//	하차정지위치 Bed상태 활성화
	    		//-------------------------------------------------------------------------------------------------
	    		szMsg="["+szOperationName+"] 하차정지위치베드[적치열:"+szCarudStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		
		    	recInTemp = JDTORecordFactory.getInstance().create();
		    	recInTemp.setField("YD_STK_COL_GP", 		szCarudStopLoc);
		    	recInTemp.setField("YD_STK_BED_NO", 		szStkBedNo);
		    	recInTemp.setField("YD_STK_BED_ACT_STAT", 	"L");
		    	intRtnVal = ydStkBedDao.updYdStkbed(recInTemp, 0);
		    	
		    	szMsg="["+szOperationName+"] 하차정지위치베드[적치열:"+szCarudStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 완료 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//-------------------------------------------------------------------------------------------------
				
				//-------------------------------------------------------------------------------------------------
		    	//	하차정지위치 단상태 활성화
	    		//-------------------------------------------------------------------------------------------------
				szMsg="["+szOperationName+"] 하차정지위치단[적치열:"+szCarudStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_STK_COL_GP", 			szCarudStopLoc);
				recInTemp.setField("YD_STK_BED_NO", 			szStkBedNo);
				recInTemp.setField("STL_NO", 					"");
				recInTemp.setField("YD_STK_LYR_ACT_STAT", 		"E");
				recInTemp.setField("YD_STK_LYR_MTL_STAT", 		"E");
				
				intRtnVal = ydStkLyrDao.updYdStklyrYdStkColGpBedNo2(recInTemp);
				if(intRtnVal <= 0) {
					szMsg = "["+szOperationName+"] 하차정지위치단[적치열:"+szCarudStopLoc+", 적치베드:"+szStkBedNo+"] 비활성화 시 Error!! - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					throw new DAOException(szMsg);
				}
		    	
				szMsg="["+szOperationName+"] 하차정지위치단[적치열:"+szCarudStopLoc+", 적치베드:"+szStkBedNo+"] 비활성화 완료 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//-------------------------------------------------------------------------------------------------
				
		    	//-------------------------------------------------------------------------------------------------
		        //	하차작업예약ID를 조회해서 작업예약재료 Table를 조회한다.
		    	//-------------------------------------------------------------------------------------------------
		    	szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차작업예약["+szWbookId+"]의 작업재료를 적치단에 등록하기 위해서 조회 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    	
		    	recInTemp = JDTORecordFactory.getInstance().create();
		    	recInTemp.setField("YD_WBOOK_ID", szWbookId);
		    	rsResult  = JDTORecordFactory.getInstance().createRecordSet("Temp");
		    	intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recInTemp, rsResult, 1);
				if(intRtnVal <= 0) {
					if(intRtnVal == 0) {
						szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차작업예약["+szWbookId+"]의 작업재료를 적치단에 등록하기 위해서 조회 시 data not found";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}else if(intRtnVal == -2) {
						szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차작업예약["+szWbookId+"]의 작업재료를 적치단에 등록하기 위해서 조회 시 parameter error";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					throw new DAOException(szMsg);
				}
				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차작업예약["+szWbookId+"]의 작업재료를 적치단에 등록하기 위해서 조회 완료 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//-------------------------------------------------------------------------------------------------
		    	
				//-------------------------------------------------------------------------------------------------
		    	//	작업예약재료의 정보를 적치단에 등록한다.
				//-------------------------------------------------------------------------------------------------
				for(int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {
		    		
		    		rsResult.absolute(Loop_i);
		    		recInTemp  = JDTORecordFactory.getInstance().create();
		    		recOutTemp = JDTORecordFactory.getInstance().create();
		    		recInTemp.setRecord(rsResult.getRecord());
		    		
		    		szStkColGp 			= ydDaoUtils.paraRecChkNull(recInTemp, "YD_STK_COL_GP");
					szStkBedNo 			= ydDaoUtils.paraRecChkNull(recInTemp, "YD_STK_BED_NO");
		    		szYD_STK_LYR_NO		= ydDaoUtils.paraRecChkNull(recInTemp, "YD_STK_LYR_NO");
		    		szSTL_NO			= ydDaoUtils.paraRecChkNull(recInTemp, "STL_NO");
		    		
		    		szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차작업예약["+szWbookId+"]의 작업재료["+szSTL_NO+"]를 적치단[열:"+szStkColGp+", 베드:"+szStkBedNo+", 단:"+szYD_STK_LYR_NO+"]에 등록 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    		
		    		recOutTemp.setField("YD_STK_COL_GP",       szStkColGp);
		    		recOutTemp.setField("YD_STK_BED_NO",       szStkBedNo);
		    		recOutTemp.setField("YD_STK_LYR_NO",       szYD_STK_LYR_NO);
		    		recOutTemp.setField("STL_NO",              szSTL_NO);
		    		recOutTemp.setField("YD_STK_LYR_MTL_STAT", "C");
					recInTemp.setField("YD_STK_LYR_ACT_STAT",  "E");
		    		intRtnVal = ydStkLyrDao.updYdStklyr(recOutTemp, 0);
					if(intRtnVal <= 0) {
		    			if(intRtnVal == 0) {
		    				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차작업예약["+szWbookId+"]의 작업재료["+szSTL_NO+"]를 적치단[열:"+szStkColGp+", 베드:"+szStkBedNo+", 단:"+szYD_STK_LYR_NO+"]에 등록 시 data not found";
		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		    			}else if(intRtnVal == -1) {
		    				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차작업예약["+szWbookId+"]의 작업재료["+szSTL_NO+"]를 적치단[열:"+szStkColGp+", 베드:"+szStkBedNo+", 단:"+szYD_STK_LYR_NO+"]에 등록 시 duplicate data,";
		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
		    			}else if(intRtnVal == -2) {
		    				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차작업예약["+szWbookId+"]의 작업재료["+szSTL_NO+"]를 적치단[열:"+szStkColGp+", 베드:"+szStkBedNo+", 단:"+szYD_STK_LYR_NO+"]에 등록 시 parameter error";
		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		    			}else if(intRtnVal == -3){
		    				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차작업예약["+szWbookId+"]의 작업재료["+szSTL_NO+"]를 적치단[열:"+szStkColGp+", 베드:"+szStkBedNo+", 단:"+szYD_STK_LYR_NO+"]에 등록 시 execution failed";
		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		    			}
		    			throw new DAOException(szMsg);
		    		}
					
					szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차작업예약["+szWbookId+"]의 작업재료["+szSTL_NO+"]를 적치단[열:"+szStkColGp+", 베드:"+szStkBedNo+", 단:"+szYD_STK_LYR_NO+"]에 등록 완료 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    	}
		    	//-------------------------------------------------------------------------------------------------
	    	}
			
			recRtnVal.setField("YD_STAT_GBN" , "LS");						          
			recRtnVal.setField("YD_REQ_GBN"  , szUL_START_SCH_REQ_YN);
			recRtnVal.setField("YD_WBOOK_ID" , szWbookId);
			
 		}catch(Exception e){
	
			szMsg="["+szOperationName+"] Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMsg);
		}
	
 		szMsg="["+szOperationName+"] ----------------------- 메소드 끝 -----------------------";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		return recRtnVal;
	} //end of C3LTcarStartWr()
	
	
	
	

	

	/**
	 * 오퍼레이션명 : 영차도착실적
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord, recTcarSch
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public JDTORecord C3LTcarStopWr(JDTORecord msgRecord, JDTORecord recTcarSch)throws JDTOException  {
		
		YdStkBedDao ydStkBedDao = new YdStkBedDao();
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		YdWrkbookMtlDao  ydWrkbookMtlDao  = new YdWrkbookMtlDao();
		YdTcarSchDao ydTcarSchDao = new YdTcarSchDao();
		
		JDTORecordSet rsResult          = null;
		JDTORecord    recInTemp         = null;
		JDTORecord    recOutTemp        = null;
		JDTORecord 	  recRtnVal 		= JDTORecordFactory.getInstance().create();
		
	    String szMsg              		= "";
	    String szMethodName       		= "C3LTcarStopWr";
	    String szOperationName			= "대차영차도착실적";

	    int intRtnVal                   = 0;

	    String szSchReqGp               = "";
	    String szCarldStopLoc           = "";
	    String szCarudStopLoc           = "";
	    String szStkBedNo               = "01";
	    String szYD_STK_LYR_NO			= null;
	    String szSTL_NO					= null;
	    String szWbookId                = "";
	    String szYD_EQP_ID              = "";
	    String szYD_TCAR_SCH_ID         = "";
	    String szYD_BAY_GP              = "";
	    
	    try{
	    	
	    	szMsg="["+szOperationName+"] ------------------------ 메소드 시작 ------------------------";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
	    	szSchReqGp     		= ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARUD_SCH_REQ_GP");
	    	szCarldStopLoc 		= ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARLD_STOP_LOC");
	    	szCarudStopLoc 		= ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARUD_STOP_LOC");
	    	
	    	//대차스케줄에 대차진행상태 update
	    	szYD_EQP_ID 		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			szYD_TCAR_SCH_ID 	= ydDaoUtils.paraRecChkNull(recTcarSch, "YD_TCAR_SCH_ID");
			
			//-------------------------------------------------------------------------------------------------
			//	대차스케줄의 차량진행상태를 영차도착로 수정
			//-------------------------------------------------------------------------------------------------
			szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태를 영차도착(하차도착)[B]상태로 수정 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_TCAR_SCH_ID", 			szYD_TCAR_SCH_ID);
	    	recInTemp.setField("YD_CAR_PROG_STAT", 			"B");
	    	recInTemp.setField("YD_CURR_BAY_GP", 			szCarudStopLoc.substring(1,2));
	    	intRtnVal = ydTcarSchDao.updYdTcarsch(recInTemp, 0);
			if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태를 영차도착(하차도착)[B]상태로 수정 시 : data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}else if(intRtnVal == -1) {
    				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태를 영차도착(하차도착)[B]상태로 수정 시 : duplicate data,";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
    			}else if(intRtnVal == -2) {
    				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태를 영차도착(하차도착)[B]상태로 수정 시 : parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}else if(intRtnVal == -3){
    				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태를 영차도착(하차도착)[B]상태로 수정 시 : execution failed";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}
    			throw new DAOException(szMsg);
    		}
			szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태를 영차도착(하차도착)[B]상태로 수정 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//-------------------------------------------------------------------------------------------------
			
			//-------------------------------------------------------------------------------------------------
	    	//	하차스케줄 요청 구분이 영차도착이라면 하차정지위치 맵 활성화, 하차작업예약에 대한 크레인스케줄 호출
			//-------------------------------------------------------------------------------------------------
			String szUL_START_SCH_REQ_YN = YdCommonUtils.getUlStartSchReqYN(szYD_EQP_ID);
			
	    	if(szUL_START_SCH_REQ_YN.equals("N")) {

	    		szWbookId = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARUD_WRK_BOOK_ID");
	    		
	    		szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차스케줄요청구분["+szSchReqGp+"]이 영차 도착이므로 하차정지위치 맵 활성화, 하차작업예약["+szWbookId+"]에 대한 크레인스케줄 호출";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				//-------------------------------------------------------------------------------------------------
		    	//	하차정지위치 Bed상태 활성화
	    		//-------------------------------------------------------------------------------------------------
		    	szMsg="["+szOperationName+"] 하차정지위치베드[적치열:"+szCarudStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		
		    	recInTemp = JDTORecordFactory.getInstance().create();
		    	recInTemp.setField("YD_STK_COL_GP", 			szCarudStopLoc);
		    	recInTemp.setField("YD_STK_BED_NO", 			szStkBedNo);
		    	recInTemp.setField("YD_STK_BED_ACT_STAT", 		"L");
		    	intRtnVal = ydStkBedDao.updYdStkbed(recInTemp, 0);
		    	
		    	szMsg="["+szOperationName+"] 하차정지위치베드[적치열:"+szCarudStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 완료 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    	//-------------------------------------------------------------------------------------------------
		    	
				//-------------------------------------------------------------------------------------------------
		    	//	하차정지위치 단상태 활성화
	    		//-------------------------------------------------------------------------------------------------
				szMsg="["+szOperationName+"] 하차정지위치단[적치열:"+szCarudStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_STK_COL_GP", 			szCarudStopLoc);
				recInTemp.setField("YD_STK_BED_NO", 			szStkBedNo);
				recInTemp.setField("STL_NO", 					"");
				recInTemp.setField("YD_STK_LYR_ACT_STAT", 		"E");
				recInTemp.setField("YD_STK_LYR_MTL_STAT", 		"E");
				
				intRtnVal = ydStkLyrDao.updYdStklyrYdStkColGpBedNo2(recInTemp);
				if(intRtnVal <= 0) {
					szMsg = "["+szOperationName+"] 하차정지위치단[적치열:"+szCarudStopLoc+", 적치베드:"+szStkBedNo+"] 비활성화 시 Error!! - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					throw new DAOException(szMsg);
				}
		    	
				szMsg="["+szOperationName+"] 하차정지위치단[적치열:"+szCarudStopLoc+", 적치베드:"+szStkBedNo+"] 비활성화 완료 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//-------------------------------------------------------------------------------------------------
				
				//-------------------------------------------------------------------------------------------------
		    	//	하차작업예약ID를 조회해서 작업예약재료 Table를 조회한다.
				//-------------------------------------------------------------------------------------------------
				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차작업예약["+szWbookId+"]의 작업재료를 적치단에 등록하기 위해서 조회 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
		    	recInTemp = JDTORecordFactory.getInstance().create();
		    	recInTemp.setField("YD_WBOOK_ID", 				szWbookId);
		    	rsResult  = JDTORecordFactory.getInstance().createRecordSet("Temp");
		    	intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recInTemp, rsResult, 1);
				if(intRtnVal <= 0) {
					if(intRtnVal == 0) {
						szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차작업예약["+szWbookId+"]의 작업재료를 적치단에 등록하기 위해서 조회 시 data not found";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}else if(intRtnVal == -2) {
						szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차작업예약["+szWbookId+"]의 작업재료를 적치단에 등록하기 위해서 조회 시 parameter error";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					throw new DAOException(szMsg);
				}
				
				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차작업예약["+szWbookId+"]의 작업재료를 적치단에 등록하기 위해서 조회 완료 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//-------------------------------------------------------------------------------------------------
		    	
				//-------------------------------------------------------------------------------------------------
		    	//	작업예약재료의 정보를 적치단에 등록한다.
				//-------------------------------------------------------------------------------------------------
		    	for(int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {
		    		
		    		rsResult.absolute(Loop_i);
		    		recInTemp  = JDTORecordFactory.getInstance().create();
		    		recOutTemp = JDTORecordFactory.getInstance().create();
		    		recInTemp.setRecord(rsResult.getRecord());
		    		
		    		szStkBedNo 			= ydDaoUtils.paraRecChkNull(recInTemp, "YD_STK_BED_NO");
		    		szYD_STK_LYR_NO		= ydDaoUtils.paraRecChkNull(recInTemp, "YD_STK_LYR_NO");
		    		szSTL_NO			= ydDaoUtils.paraRecChkNull(recInTemp, "STL_NO");
		    		
		    		szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차작업예약["+szWbookId+"]의 작업재료["+szSTL_NO+"]를 적치단[열:"+szCarudStopLoc+", 베드:"+szStkBedNo+", 단:"+szYD_STK_LYR_NO+"]에 등록 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    		
		    		recOutTemp.setField("YD_STK_COL_GP",       		szCarudStopLoc);
		    		recOutTemp.setField("YD_STK_BED_NO",       		szStkBedNo);
		    		recOutTemp.setField("YD_STK_LYR_NO",       		szYD_STK_LYR_NO);
		    		recOutTemp.setField("STL_NO",              		szSTL_NO);
		    		recOutTemp.setField("YD_STK_LYR_MTL_STAT", 		"C");
		    		recOutTemp.setField("YD_STK_LYR_ACT_STAT", 		"E");
		    		intRtnVal = ydStkLyrDao.updYdStklyr(recOutTemp, 0);
					if(intRtnVal <= 0) {
		    			if(intRtnVal == 0) {
		    				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차작업예약["+szWbookId+"]의 작업재료["+szSTL_NO+"]를 적치단[열:"+szCarudStopLoc+", 베드:"+szStkBedNo+", 단:"+szYD_STK_LYR_NO+"]에 등록 시 data not found";
		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		    			}else if(intRtnVal == -1) {
		    				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차작업예약["+szWbookId+"]의 작업재료["+szSTL_NO+"]를 적치단[열:"+szCarudStopLoc+", 베드:"+szStkBedNo+", 단:"+szYD_STK_LYR_NO+"]에 등록 시 duplicate data,";
		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
		    			}else if(intRtnVal == -2) {
		    				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차작업예약["+szWbookId+"]의 작업재료["+szSTL_NO+"]를 적치단[열:"+szCarudStopLoc+", 베드:"+szStkBedNo+", 단:"+szYD_STK_LYR_NO+"]에 등록 시 parameter error";
		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		    			}else if(intRtnVal == -3){
		    				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차작업예약["+szWbookId+"]의 작업재료["+szSTL_NO+"]를 적치단[열:"+szCarudStopLoc+", 베드:"+szStkBedNo+", 단:"+szYD_STK_LYR_NO+"]에 등록 시 execution failed";
		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		    			}
		    			throw new DAOException(szMsg);
		    		}
					
					szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차작업예약["+szWbookId+"]의 작업재료["+szSTL_NO+"]를 적치단[열:"+szCarudStopLoc+", 베드:"+szStkBedNo+", 단:"+szYD_STK_LYR_NO+"]에 등록 완료 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    	}
		    	//-------------------------------------------------------------------------------------------------
	    	}
	    	
	    	recRtnVal.setField("YD_STAT_GBN" , "LE");						          
	    	recRtnVal.setField("YD_REQ_GBN"  , szUL_START_SCH_REQ_YN);
	    	recRtnVal.setField("YD_WBOOK_ID" , szWbookId);
	    	
 		}catch(Exception e){
	
			szMsg="["+szOperationName+"] Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMsg);
		}
	
 		szMsg="["+szOperationName+"] ------------------------ 메소드 끝 ------------------------";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		return recRtnVal;
	} //end of C3LTcarStopWr()
	
	
	/**
	 * 오퍼레이션명 : 크레인스케줄호출
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public int C3CallCrnSch1(JDTORecord msgRecord)throws JDTOException  {
		YdWrkbookDao     ydWrkbookDao     = new YdWrkbookDao();
		YdSchRuleDao     ydSchRuleDao     = new YdSchRuleDao();
		YdDelegate       ydDelegate 	  = new YdDelegate();
		
		JDTORecordSet rsResult          = null;
		JDTORecord    recInTemp         = null;

	    
	    String szMsg              		= "";
	    String szMethodName       		= "C3CallCrnSch";

	    int intRtnVal                   = 0;

	    String szWbookId                = "";
	    String szSchCd                  = "";
	    String szEqpId                  = "";
	    
	    try{
	    	//작업예약ID
	    	szWbookId = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");
	    	
	    	//작업예약 id로 작업예약Table를 조회한다.
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	intRtnVal = ydWrkbookDao.getYdWrkbook(msgRecord, rsResult, 0);
			if(intRtnVal <= 0) {
				if(intRtnVal == 0) {
					szMsg="<C3CallCrnSch> getYdWrkbook data not found";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				}else if(intRtnVal == -2) {
					szMsg="<C3CallCrnSch> getYdWrkbook parameter error";
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
			/*     스케쥴기준 조회 - 스케쥴 금지유무 판단, 작업크레인, 대체크레인 조회	- 2009.04.10 임춘수	*/
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
	    	

	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	//크레인스케줄MAIN호출 TC : YDYDJ500, 스케줄코드 설비ID
	    	if(szEqpId.substring(0,1).equals("A")) {
		    	recInTemp.setField("MSG_ID",    "YDYDJ500");
		    	recInTemp.setField("YD_SCH_CD", szSchCd);
		    	recInTemp.setField("YD_EQP_ID", szEqpId);
	    	}else if(szEqpId.substring(0,1).equals("S")) {
		    	recInTemp.setField("MSG_ID",    "YDYDJ512");
		    	recInTemp.setField("YD_SCH_CD", szSchCd);
		    	recInTemp.setField("YD_EQP_ID", szEqpId);
	    	}else if(szEqpId.substring(0,1).equals("D")) {
		    	recInTemp.setField("MSG_ID",    "YDYDJ503");
		    	recInTemp.setField("YD_SCH_CD", szSchCd);
		    	recInTemp.setField("YD_EQP_ID", szEqpId);
	    	}else if(szEqpId.substring(0,1).equals("K")) {
		    	recInTemp.setField("MSG_ID",    "YDYDJ506");
		    	recInTemp.setField("YD_SCH_CD", szSchCd);
		    	recInTemp.setField("YD_EQP_ID", szEqpId);
	    	}else if(szEqpId.substring(0,1).equals("H") || szEqpId.substring(0,1).equals("J")) {
		    	recInTemp.setField("MSG_ID",    "YDYDJ509");
		    	recInTemp.setField("YD_SCH_CD", szSchCd);
		    	recInTemp.setField("YD_EQP_ID", szEqpId);
	    	}

	    	//서버 메세지 전송 메소드 크레인 스케줄 호출
	    	ydDelegate.sendMsg(recInTemp);

	    	
 		}catch(Exception e){
	
			szMsg="크레인스케줄 호출 Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			return intRtnVal = -1;
		}
	
	
		szMsg="크레인스케줄 호출("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		return intRtnVal = 1;
	} //end of C3CallCrnSch()
	
	
	
	
	
	
	/**
	 * 오퍼레이션명 : 후판슬라브야드 대차이동실적 (Y3YDL014)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return Integer
	 * @throws JDTOException
	 */
	public Integer procY3TcarMvWr(JDTORecord msgRecord)throws DAOException  {
		
		try{
			JDTORecord recRtnVal = JDTORecordFactory.getInstance().create();
			
			EJBConnector ydEjbCon = new EJBConnector("default", this);
			recRtnVal = (JDTORecord)ydEjbCon.trx("TcarMvHdSeEJB", "procY3TcarMvWrNew", msgRecord);
			
			/*
	    	 * 크레인스케쥴 호출시점
	    	        공차출발시(US) : 요청구분 - Y, 작업예약 IS NOT NULL
				공차도착시(UE) : 요청구분 - N, 작업예약 IS NOT NULL
				영차출발시(LS) : 요청구분 - Y, 작업예약 IS NOT NULL
				영차도착시(LE) : 요청구분 - N, 작업예약 IS NOT NULL
	    	 */
	    	boolean isWorkCall	= false;
	    	String sYdStatGbn 	= ydDaoUtils.paraRecChkNull(recRtnVal, "YD_STAT_GBN");
	    	String sYdReqGbn 	= ydDaoUtils.paraRecChkNull(recRtnVal, "YD_REQ_GBN");
	    	String sYdWbookId 	= ydDaoUtils.paraRecChkNull(recRtnVal, "YD_WBOOK_ID");
	    	
	    	if("".equals(sYdStatGbn)) {
	    		return YdConstant.RETN_INT_SUCCESS;
	    	}
	    	
	    	if("US".equals(sYdStatGbn)){
	    		if("Y".equals(sYdReqGbn)){
	    			if(!"".equals(sYdWbookId)){
	    				isWorkCall	= true;
	    			}
	    		}
	    	}else if("UE".equals(sYdStatGbn)){
	    		if("N".equals(sYdReqGbn)){
	    			if(!"".equals(sYdWbookId)){
	    				isWorkCall	= true;
	    			}
	    		}
	    	}else if("LS".equals(sYdStatGbn)){
	    		if("Y".equals(sYdReqGbn)){
	    			if(!"".equals(sYdWbookId)){
	    				isWorkCall	= true;
	    			}
	    		}
	    	}else if("LE".equals(sYdStatGbn)){
	    		if("N".equals(sYdReqGbn)){
	    			if(!"".equals(sYdWbookId)){
	    				isWorkCall	= true;
	    			}
	    		}
	    	}
	    	if(isWorkCall){
	    		String szRtnMsg		= YdCommonUtils.callCrnSchByWbookId(sYdWbookId, "Y");
	    	}
						
		} catch (Exception e) {
			throw new DAOException("[procY3TcarMvWr] Exception발생 : " + e.getMessage());
		}	// end try catch문
		
		return YdConstant.RETN_INT_SUCCESS;
	}

	
	/**
	 * 오퍼레이션명 : 후판슬라브야드 대차이동실적 (Y3YDL014)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return JDTORecord
	 * @throws JDTOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord procY3TcarMvWrNew(JDTORecord msgRecord)throws DAOException  {
		
		JDTORecord recRtnVal = JDTORecordFactory.getInstance().create();
		
		JDTORecordSet rsResult          = null;
		JDTORecord recTcarSch           = null;
		JDTORecord recPara              = null;
		
	    String szMethodName       		= "procY3TcarMvWr";
	    String szOperationName       	= "후판대차이동실적";
	    String szMsg              		= "";
	    String szYD_EQP_ID              = "";
	    String szYD_TCAR_MOVE_GP        = "";
	    String szYD_TCAR_MOVE_DIR       = "";
	    String szYD_BAY_GP1             = "";
	    String szYD_BAY_GP2             = "";
	    String szYD_GP                  = "";
	    String szYD_STK_COL_GP          = "";
	    int intRtnVal 					= 0;
	    int nRet                        = 0;
	    
        String szRcvTcCode=ydUtils.getTcCode(msgRecord);
        
        if( szRcvTcCode==null || szRcvTcCode.equals("") ){
        	szMsg="["+szOperationName+"] TC Code Error ("+szRcvTcCode+")";
        	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
        	return recRtnVal;
        }

        if(bDebugFlag){
            szMsg="["+szOperationName+"] [DEBUG] 전문수신 : TCCODE=" +szRcvTcCode ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }
	    
		try{
			szMsg="["+szOperationName+"] ---------------------- 메소드 시작 ----------------------";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//------------------------------------------------------------------------------------------
			//	파라미터확인
			//------------------------------------------------------------------------------------------
			
			// 설비ID
			szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			if(szYD_EQP_ID.equals("")){
				szMsg = "["+szOperationName+"] 설비ID 값이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return recRtnVal;
			}
			
			// 야드대차이동구분
			szYD_TCAR_MOVE_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_TCAR_MOVE_GP");
			if(szYD_TCAR_MOVE_GP.equals("")){
				szMsg = "["+szOperationName+"] 야드대차이동구분 값이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return recRtnVal;
			}

			
			// 야드대차이동방향
			szYD_TCAR_MOVE_DIR = ydDaoUtils.paraRecChkNull(msgRecord, "YD_TCAR_MOVE_DIR");
			if(szYD_TCAR_MOVE_DIR.equals("")){
				szMsg = "["+szOperationName+"] 야드대차이동방향 값이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return recRtnVal;
			}
			
			
			// 현재동
			szYD_BAY_GP1 = ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP1");
			if(szYD_BAY_GP1.equals("")){
				szMsg = "["+szOperationName+"] 현재동 값이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return recRtnVal;
			}

			
			// 목적동
			szYD_BAY_GP2 = ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP2");	
			if(szYD_BAY_GP2.equals("")){
				szMsg = "["+szOperationName+"] 목적동 값이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return recRtnVal;
			}
			
			// LOG
			szMsg = "["+szOperationName+"] [1] 설비ID : " + szYD_EQP_ID;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    
			szMsg = "["+szOperationName+"] [2] 야드대차이동구분 : " + szYD_TCAR_MOVE_GP;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			szMsg = "["+szOperationName+"] [3] 야드대차이동방향 : " + szYD_TCAR_MOVE_DIR;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szMsg = "["+szOperationName+"] [4] 현재동 : " + szYD_BAY_GP1;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szMsg = "["+szOperationName+"] [5] 목적동 : " + szYD_BAY_GP2;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//------------------------------------------------------------------------------------------------------------------
			//	대차이동실적 파라미터 확인 - 대차이동중인 경우에는 업무 종료 처리
			//------------------------------------------------------------------------------------------------------------------
			if( szYD_TCAR_MOVE_GP.equals(YdConstant.YD_TCAR_MOVE_GP_MOVE)) {
				szMsg = "["+szOperationName+"] ----------------- 대차이동실적이므로 업무 종료 처리  ----------------- ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return recRtnVal;
			}
			
			// 야드
			szYD_GP = szYD_EQP_ID.substring(0, 1);

			// 적치열 구분
			szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP1 + szYD_EQP_ID.substring(2, 6);
			
			//=============================================================
			// Log 테이블 등록 
			//=============================================================
			szMsg = "["+szOperationName+"] 대차이동실적 수신";
			ydUtils.putLogMsg("A", YdConstant.YD_MONITORING_CHANNEL_A, szMsg, "", "", "", "I", "A", "I", szRcvTcCode, szSessionName, szMethodName);
			//------------------------------------------------------------------------------------------------------------------
			
			//------------------------------------------------------------------------------------------
	    	//	대차스케줄상태Check (공차출발,공차도착,영차출발,영차도착 Check)
			//------------------------------------------------------------------------------------------
			
			szMsg = "["+szOperationName+"] 대차["+szYD_EQP_ID+"]상태체크 시작- 이동상태["+szYD_TCAR_MOVE_GP+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	nRet = this.Y3ChkTcarSchStat(msgRecord, rsResult);
	    	rsResult.absolute(1);
	    	recTcarSch = JDTORecordFactory.getInstance().create();
	    	recTcarSch.setRecord(rsResult.getRecord());
						
	    	szMsg = "["+szOperationName+"] 대차["+szYD_EQP_ID+"]상태체크 완료- 이동상태["+szYD_TCAR_MOVE_GP+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//------------------------------------------------------------------------------------------
	    	
	    	switch (nRet) {
	    		case 1	:
	    			
	    			//------------------------------------------------------------------------------------------
	    	    	//	공차출발모듈 호출
	    			//------------------------------------------------------------------------------------------
	    			szMsg = "["+szOperationName+"] 대차["+szYD_EQP_ID+"]의 공차출발 모듈 호출 시작";
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			
	    			// 2.공차출발 Method호출
	    			recRtnVal = this.Y3UTcarStartWr(msgRecord, recTcarSch);
	    			
	    			szMsg = "["+szOperationName+"] 대차["+szYD_EQP_ID+"]의 공차출발 모듈 호출 완료 - 반환값 : " + intRtnVal;
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			
	    			break;
	    			//------------------------------------------------------------------------------------------
	    			
	    		case 2	:
	    			
	    			//------------------------------------------------------------------------------------------
	    	    	// 	공차도착모듈 호출
	    			//------------------------------------------------------------------------------------------
	    			szMsg = "["+szOperationName+"] 대차["+szYD_EQP_ID+"]의 공차도착 모듈 호출 시작";
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			
	    			// 3.공차도착 Method호출
	    			recRtnVal = this.Y3UTcarStopWr(msgRecord, recTcarSch);
	    			
	    			szMsg = "["+szOperationName+"] 대차["+szYD_EQP_ID+"]의 공차도착 모듈 호출 완료 - 반환값 : " + intRtnVal;
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			
	    			break;
	    			//------------------------------------------------------------------------------------------
	    			
	    		case 3	:
	    			
	    			//------------------------------------------------------------------------------------------
	    	    	// 	영차출발모듈 호출
	    			//------------------------------------------------------------------------------------------
	    			szMsg = "["+szOperationName+"] 대차["+szYD_EQP_ID+"]의 영차출발 모듈 호출 시작";
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			
	    			// 4.영차출발 Method호출
	    			recRtnVal = this.Y3LTcarStartWr(msgRecord, recTcarSch);
	    			
	    			szMsg = "["+szOperationName+"] 대차["+szYD_EQP_ID+"]의 영차출발 모듈 호출 완료 - 반환값 : " + intRtnVal;
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			
	    			break;
	    			
	    			//------------------------------------------------------------------------------------------
	    			
	    		case 4	:
	    			
	    			//------------------------------------------------------------------------------------------
	    	    	//	영차도착모듈 호출
	    			//------------------------------------------------------------------------------------------
	    			szMsg = "["+szOperationName+"] 대차["+szYD_EQP_ID+"]의 영차도착 모듈 호출 시작";
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			
	    			// 5.영차도착 Method호출
	    			recRtnVal = this.Y3LTcarStopWr(msgRecord, recTcarSch);
	    			
	    			szMsg = "["+szOperationName+"] 대차["+szYD_EQP_ID+"]의 영차도착 모듈 호출 완료 - 반환값 : " + intRtnVal;
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			
	    			break;
	    			
	    			//------------------------------------------------------------------------------------------
	    	}
	    	String sYdStatGbn 	= ydDaoUtils.paraRecChkNull(recRtnVal, "YD_STAT_GBN");
	    	
	    	if("".equals(sYdStatGbn)) {
	    		szMsg="["+szOperationName+"] 대차 이동 실적 처리 시 오류발생 : 지원하지 않는 차량이동상태입니다. - 리턴값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		return recRtnVal;
	    	}
	    	
			//===========================================================================
	    	// 2009.12.10 호출 순서  변경
			// 적치대제원 전문을 L2로 전송
			//===========================================================================
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_INFO_SYNC_CD" , "3");						          // 1:동,2:SPAN,3:열,4:BED
			recPara.setField("YD_GP"           , szYD_GP);
			recPara.setField("YD_STK_COL_GP"   , szYD_STK_COL_GP);
			// 전문에 "S"면 출발이므로 "1" or "A" 를 넣어서 전문 호출하면 전문편집시 출발인 "S"로 송신
			// 전문에 "E"면 도착이므로 "2" or "B" 를 넣어서 전문 호출하면 전문편집시 도착인 "A"로 송신
			if(szYD_TCAR_MOVE_GP.equals("S")){
				recPara.setField("YD_CAR_PROG_STAT", "1");						      // "1", "A" : 출발(S)    "2", "B" : 도착(A)        
			} else if(szYD_TCAR_MOVE_GP.equals("E")){
				recPara.setField("YD_CAR_PROG_STAT", "2");						     
			}

			if(nRet == 1 || nRet == 2){
				recPara.setField("YD_EQP_WRK_STAT" , YdUtils.fillSpZr("L", 1, 1));    // "L" : 공차(출하)(L), "U" : 영차(반입)(U)			
			} else if(nRet == 3 || nRet == 4){
				recPara.setField("YD_EQP_WRK_STAT" , YdUtils.fillSpZr("U", 1, 1)); 
			} else {
				szMsg = "공차도 아니고 영차도 아님";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);				
			}

			if(szYD_TCAR_MOVE_GP.equals("S") || szYD_TCAR_MOVE_GP.equals("E")){
				if(nRet == 1 || nRet == 2 || nRet == 3 || nRet == 4){
					YdCommonUtils.sndStrPosSpecToL2(recPara);
				}
			}
			//===========================================================================	    	
	    	
		}catch(Exception e){
			szMsg="["+szOperationName+"] 대차 이동 실적 처리 Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			throw new DAOException(szMsg);
		}
	
		szMsg="["+szOperationName+"] ---------------------- 메소드 끝 ----------------------";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        
        return recRtnVal;
	} //end of procY3TcarMvWr()
		

	/**
	 * 오퍼레이션명 : 대차스케줄상태Check
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord, rsTarSch
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public int Y3ChkTcarSchStat(JDTORecord msgRecord, JDTORecordSet rsTarSch)throws JDTOException  {
		YdTcarSchDao ydTcarSchDao = new YdTcarSchDao();
		YdEqpDao     ydEqpDao     = new YdEqpDao();
		
		JDTORecordSet rsResult          = null;
		JDTORecord    recOutTemp        = null;
		JDTORecord    recInTemp         = null;

	    
	    String szMsg              		= "";
	    String szMethodName       		= "Y3ChkTcarSchStat";
	    String szOperationName			= "대차스케줄상태체크";
	    
	    String szEqpId                  = "";
	    String szYD_TCAR_SCH_ID			= null;
	    String szMoveGp                 = "";
	    String szEqpWrkStat             = "";
	    String szYD_BAY_GP1				= null;
	    
	    int intRtnVal                   = 0;

	    
	    try{
	    	szMsg="["+szOperationName+"] -------------------- 메소드 시작 --------------------";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
	    	
	    	//------------------------------------------------------------------------------------------
	    	//	대차설비ID로 대차 스케줄을 조회한다.
	    	//------------------------------------------------------------------------------------------
	    	szEqpId  				= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
	    	szMoveGp 				= ydDaoUtils.paraRecChkNull(msgRecord, "YD_TCAR_MOVE_GP");
	    	szYD_BAY_GP1			= ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP1");				//현재동(출발 시 - 출발동, 도착 시 - 도착동)
	    	
	    	szMsg="["+szOperationName+"] 대차설비ID["+szEqpId+"]로 대차스케줄 조회 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_EQP_ID", szEqpId);
	    	
	    	intRtnVal = ydTcarSchDao.getYdTcarsch(recInTemp, rsResult, 4);
	    	
			if(intRtnVal <= 0) {
				if(intRtnVal == 0) {
					szMsg="["+szOperationName+"] 대차설비ID["+szEqpId+"]로 대차스케줄 조회 시 data not found";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}else if(intRtnVal == -2) {
					szMsg="["+szOperationName+"] 대차설비ID["+szEqpId+"]로 대차스케줄 조회 시 parameter error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				return intRtnVal = -1;
			}
	    	
			szMsg="["+szOperationName+"] 대차설비ID["+szEqpId+"]로 대차스케줄 조회 완료 - 반환값 : " + intRtnVal;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//------------------------------------------------------------------------------------------
	    	
	    	rsResult.absolute(1);
	    	recOutTemp = JDTORecordFactory.getInstance().create();
	    	recOutTemp.setRecord(rsResult.getRecord());

	    	szYD_TCAR_SCH_ID 		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TCAR_SCH_ID");
	    	
	        //대차 스케줄이 공차인지 상차인지 Check한다.
	    	szEqpWrkStat 			= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_WRK_STAT");

	    	//대차스케줄은 리턴하고
	    	rsTarSch.addRecord(recOutTemp);

	    	//대차설비Table에 야드동구분을 update위한 Setting
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_EQP_ID", szEqpId);
	    	
	    	//------------------------------------------------------------------------------------------
	    	//	출발 실적인 경우
	    	//------------------------------------------------------------------------------------------
	    	if(szMoveGp.equals("S")) {
	    		
	    		//------------------------------------------------------------------------------------------
		    	//	출발 실적인 경우는 대차설비의 현재동 정보를 Clear
		    	//------------------------------------------------------------------------------------------
	    		
				szMsg="["+szOperationName+"] 출발실적이므로 설비Table에 대차설비ID["+szEqpId+"]의 현재동 정보를 Clear 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
	    		recInTemp.setField("YD_CURR_BAY_GP", "");
	    		intRtnVal = ydEqpDao.updYdEqp(recInTemp, 0);
	    		if(intRtnVal <= 0) {
	    			if(intRtnVal == 0) {
	    				szMsg="["+szOperationName+"] 출발실적이므로 설비Table에 대차설비ID["+szEqpId+"]의 현재동 정보를 Clear 시 : data not found";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}else if(intRtnVal == -1) {
	    				szMsg="["+szOperationName+"] 출발실적이므로 설비Table에 대차설비ID["+szEqpId+"]의 현재동 정보를 Clear 시 : duplicate data,";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
	    			}else if(intRtnVal == -2) {
	    				szMsg="["+szOperationName+"] 출발실적이므로 설비Table에 대차설비ID["+szEqpId+"]의 현재동 정보를 Clear 시 : parameter error";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}else if(intRtnVal == -3){
	    				szMsg="["+szOperationName+"] 출발실적이므로 설비Table에 대차설비ID["+szEqpId+"]의 현재동 정보를 Clear 시 : execution failed";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}
	    			//return intRtnVal = -1;
	    			throw new DAOException(szMsg);
	    		}

	    		szMsg="["+szOperationName+"] 출발실적이므로 설비Table에 대차설비ID["+szEqpId+"]의 현재동 정보를 Clear 완료 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//------------------------------------------------------------------------------------------
				
				
				//------------------------------------------------------------------------------------------
	    		//	공대차 출발인경우
				//------------------------------------------------------------------------------------------
	    		if(szEqpWrkStat.equals("U")) {
	    			
	    			szMsg="["+szOperationName+"] 대차설비ID["+szEqpId+"]로 대차스케줄["+szYD_TCAR_SCH_ID+"] - 공대차출발";
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			
	    			return intRtnVal = 1;
	    		//------------------------------------------------------------------------------------------
	    		//	영대차 출발인 경우
	    		//------------------------------------------------------------------------------------------
	    		}else if(szEqpWrkStat.equals("L")) {
	    			
	    			szMsg="["+szOperationName+"] 대차설비ID["+szEqpId+"]로 대차스케줄["+szYD_TCAR_SCH_ID+"] - 영대차출발";
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			
	    			return intRtnVal = 3;
	    			
	    		}
	    		
	    		
	    	//------------------------------------------------------------------------------------------
	    	//	도착 실적인 경우	
	    	//------------------------------------------------------------------------------------------
	    	}else if (szMoveGp.equals("E")) {
	    		
	    		//------------------------------------------------------------------------------------------
		    	//	도착 실적인 경우 대차설비의 현재동 정보를 수정
		    	//------------------------------------------------------------------------------------------
	    		szMsg="["+szOperationName+"] 도착실적이므로 설비Table에 대차설비ID["+szEqpId+"]의 현재동["+szYD_BAY_GP1+"] 정보를 수정 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//현재동 업데이트
	    		recInTemp.setField("YD_CURR_BAY_GP", szYD_BAY_GP1);
	    		intRtnVal = ydEqpDao.updYdEqp(recInTemp, 0);
	    		if(intRtnVal <= 0) {
	    			if(intRtnVal == 0) {
	    				szMsg="["+szOperationName+"] 도착실적이므로 설비Table에 대차설비ID["+szEqpId+"]의 현재동["+szYD_BAY_GP1+"] 정보를 수정 시 : data not found";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}else if(intRtnVal == -1) {
	    				szMsg="["+szOperationName+"] 도착실적이므로 설비Table에 대차설비ID["+szEqpId+"]의 현재동["+szYD_BAY_GP1+"] 정보를 수정 시 : duplicate data,";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
	    			}else if(intRtnVal == -2) {
	    				szMsg="["+szOperationName+"] 도착실적이므로 설비Table에 대차설비ID["+szEqpId+"]의 현재동["+szYD_BAY_GP1+"] 정보를 수정 시 : parameter error";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}else if(intRtnVal == -3){
	    				szMsg="["+szOperationName+"] 도착실적이므로 설비Table에 대차설비ID["+szEqpId+"]의 현재동["+szYD_BAY_GP1+"] 정보를 수정 시 : execution failed";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}
	    			//return intRtnVal = -1;
	    			throw new DAOException(szMsg);
	    		}

	    		szMsg="["+szOperationName+"] 도착실적이므로 설비Table에 대차설비ID["+szEqpId+"]의 현재동["+szYD_BAY_GP1+"] 정보를 수정 완료 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//------------------------------------------------------------------------------------------
				
	    		
	    		//------------------------------------------------------------------------------------------
	    		//	공대차 도착인경우
	    		//------------------------------------------------------------------------------------------
	    		if(szEqpWrkStat.equals("U")) {
	    			
	    			szMsg="["+szOperationName+"] 대차설비ID["+szEqpId+"]로 대차스케줄["+szYD_TCAR_SCH_ID+"] - 공대차도착";
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			
	    			return intRtnVal = 2;
	    			
	    		//------------------------------------------------------------------------------------------
	    		//	영대차 도착인 경우	
	    		//------------------------------------------------------------------------------------------
	    		}else if(szEqpWrkStat.equals("L")) {
	    			
	    			szMsg="["+szOperationName+"] 대차설비ID["+szEqpId+"]로 대차스케줄["+szYD_TCAR_SCH_ID+"] - 영대차도착";
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			
	    			return intRtnVal = 4;
	    			
	    		}
	    	}

	    	
 		}catch(Exception e){
	
			szMsg="["+szOperationName+"] Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			//return intRtnVal = -1;
			throw new DAOException(szMsg);
		}
	
	
 		szMsg="["+szOperationName+"] -------------------- 메소드 끝 --------------------";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		return intRtnVal = 1;
	} //end of Y3ChkTcarSchStat()
	
	
	
	
	
	
	
	
	/**
	 * 오퍼레이션명 : 공차출발실적
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord, recTcarSch
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public JDTORecord Y3UTcarStartWr(JDTORecord msgRecord, JDTORecord recTcarSch)throws JDTOException  {
		
		YdStkBedDao ydStkBedDao = new YdStkBedDao();
		YdTcarSchDao ydTcarSchDao = new YdTcarSchDao();
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		
		JDTORecord recInTemp          	= null;
		JDTORecord recRtnVal 			= JDTORecordFactory.getInstance().create();
		
	    String szMsg              		= "";
	    String szMethodName       		= "Y3UTcarStartWr";
	    String szOperationName			= "대차공차출발실적";
	    
	    int intRtnVal                   = 0;
	    
	    String szStkColGp               = "";
	    String szStkBedNo               = "01";
	    String szSchReqGp				= "";
	    String szCarldStopLoc           = "";
	    String szWbookId                = "";
	    String szYD_EQP_ID              = "";
	    String szYD_TCAR_SCH_ID         = "";
	    String szYD_BAY_GP1				= null;
	    String szLD_START_SCH_REQ_YN	= null;
	    
	    try{
	    	szMsg="["+szOperationName+"] --------------------- 메소드 시작 ---------------------";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
			/*
			YD_EQP_ID 야드설비ID
			YD_TCAR_MOVE_GP 야드대차이동구분
			YD_TCAR_MOVE_DIR 야드대차이동방향
			YD_BAY_GP1 야드동구분1 현재동
			YD_BAY_GP2 야드동구분2 목적동
			*/
	    	
	    	//--------------------------------------------------------------------------------------------
	    	//	대차설비ID로 대차스케줄을 조회
	    	//--------------------------------------------------------------------------------------------
	    	szYD_EQP_ID 		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");					//대차설비ID
	    	szYD_BAY_GP1		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP1");					//현재동
			//--------------------------------------------------------------------------------------------
			
			//--------------------------------------------------------------------------------------------
			//	대차스케줄의 차량진행상태를 상차출발로 수정
			//--------------------------------------------------------------------------------------------
			szYD_TCAR_SCH_ID = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_TCAR_SCH_ID");
			
			szMsg="["+szOperationName+"] 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태[1]를 상차출발로 수정 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_TCAR_SCH_ID", 			szYD_TCAR_SCH_ID);
	    	recInTemp.setField("YD_CAR_PROG_STAT", 			YdConstant.YD_CARLD_LEV);
	    	intRtnVal = ydTcarSchDao.updYdTcarsch(recInTemp, 0);
			if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="["+szOperationName+"] 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태[1]를 상차출발로 수정 시 : data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}else if(intRtnVal == -1) {
    				szMsg="["+szOperationName+"] 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태[1]를 상차출발로 수정 시 : duplicate data,";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
    			}else if(intRtnVal == -2) {
    				szMsg="["+szOperationName+"] 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태[1]를 상차출발로 수정 시 : parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}else if(intRtnVal == -3){
    				szMsg="["+szOperationName+"] 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태[1]를 상차출발로 수정 시 : execution failed";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}
    			//return intRtnVal = -1;
    			throw new DAOException(szMsg);
    		}
			
			szMsg="["+szOperationName+"] 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태[1]를 상차출발로 수정 완료 - 반환값 : " + intRtnVal;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//--------------------------------------------------------------------------------------------
			
			//--------------------------------------------------------------------------------------------
	    	//	출발위치 적치Bed상태 비활성화
			//--------------------------------------------------------------------------------------------
	    	szStkColGp = szYD_EQP_ID.substring(0,1) + szYD_BAY_GP1 + szYD_EQP_ID.substring(2);
	    	
	    	szMsg="["+szOperationName+"] 대차설비의 출발위치베드[적치열:"+szStkColGp+", 적치베드:"+szStkBedNo+"]를 비활성화 처리 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_STK_COL_GP", 			szStkColGp);
	    	recInTemp.setField("YD_STK_BED_NO", 			szStkBedNo);
	    	recInTemp.setField("YD_STK_BED_ACT_STAT", 		YdConstant.YD_STK_BED_INACTIVE);
	    	intRtnVal = ydStkBedDao.updYdStkbed(recInTemp, 0);
			if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="["+szOperationName+"] 대차설비의 출발위치베드[적치열:"+szStkColGp+", 적치베드:"+szStkBedNo+"]를 비활성화 처리 시 data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}else if(intRtnVal == -1) {
    				szMsg="["+szOperationName+"] 대차설비의 출발위치베드[적치열:"+szStkColGp+", 적치베드:"+szStkBedNo+"]를 비활성화 처리 시 duplicate data,";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
    			}else if(intRtnVal == -2) {
    				szMsg="["+szOperationName+"] 대차설비의 출발위치베드[적치열:"+szStkColGp+", 적치베드:"+szStkBedNo+"]를 비활성화 처리 시 parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}else if(intRtnVal == -3){
    				szMsg="["+szOperationName+"] 대차설비의 출발위치베드[적치열:"+szStkColGp+", 적치베드:"+szStkBedNo+"]를 비활성화 처리 시 execution failed";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}
    			//return intRtnVal = -1;
    			throw new DAOException(szMsg);
    		}
			
			szMsg="["+szOperationName+"] 대차설비의 출발위치베드[적치열:"+szStkColGp+", 적치베드:"+szStkBedNo+"]를 비활성화 처리 완료 - 반환값 : " + intRtnVal;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//--------------------------------------------------------------------------------------------
			
			//--------------------------------------------------------------------------------------------
			//	출발위치 적치단 비활성화
			//--------------------------------------------------------------------------------------------
			szMsg="["+szOperationName+"] 대차설비의 출발위치단[적치열:"+szStkColGp+", 적치베드:"+szStkBedNo+"]를 비활성화 처리 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_STK_COL_GP", 			szStkColGp);
			recInTemp.setField("YD_STK_BED_NO", 			szStkBedNo);
			recInTemp.setField("YD_STK_LYR_ACT_STAT", 		"C");
			recInTemp.setField("STL_NO", 					"");
			recInTemp.setField("YD_STK_LYR_MTL_STAT", 		"E");
	    	
			intRtnVal = ydStkLyrDao.updYdStklyrYdStkColGpBedNo(recInTemp);
			
			if(intRtnVal <= 0) {
				szMsg = "["+szOperationName+"] 대차설비의 출발위치단[적치열:"+szStkColGp+", 적치베드:"+szStkBedNo+"]를 비활성화 처리 시 Error!! - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException(szMsg);
			}
			
			szMsg="["+szOperationName+"] 대차설비의 출발위치단[적치열:"+szStkColGp+", 적치베드:"+szStkBedNo+"]를 비활성화 처리 완료 - 반환값 : " + intRtnVal;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//--------------------------------------------------------------------------------------------
			
	    	szSchReqGp     		= ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARLD_SCH_REQ_GP");					//상차스케줄요청구분
	    	szCarldStopLoc 		= ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARLD_STOP_LOC");					//상차정지위치
	    	szWbookId      		= ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARLD_WRK_BOOK_ID");				//상차작업예약ID
	    	
	    	//--------------------------------------------------------------------------------------------
	    	//	상차 스케줄 요청 구분이 공차 출발이고 대차스케줄에 차량상차작업이 등록되어 있으면
	    	//	상차정지위치 활성화, 상차작업예약에 대한 크레인스케줄 기동
	    	//	--> 1. 변경내역 : 상차 스케줄 요청 구분을 BRE에 등록된 상차출발시스케줄 요청여부를 사용하여 판단하도록 로직 변경
	    	//	수정일 : 1. 2010.02.23 임춘수
	    	//--------------------------------------------------------------------------------------------
	    	szLD_START_SCH_REQ_YN = YdCommonUtils.getLdStartSchReqYN(szYD_EQP_ID);
	    	
	    	//--------------------------------------------------------------------------------------------
	    	//	상차출발시 스케쥴 요청여부를 판단해서 맵활성화 판단되도록 변경 - 임춘수 2010.02.25
	    	if(szLD_START_SCH_REQ_YN.equals("Y") ) {				
	    		
	    		szMsg="["+szOperationName+"] 대차스케줄["+szYD_TCAR_SCH_ID+"]의 상차출발시스케줄 요청여부["+szLD_START_SCH_REQ_YN+"]이 공차출발이고 차량상차작업["+szWbookId+"]이므로 "; 
	    		szMsg+="상차정지위치[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 활성화, 크레인스케줄 호출";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//--------------------------------------------------------------------------------------------
	    		//	상차정지위치 베드 활성화
	    		//--------------------------------------------------------------------------------------------
				szMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		
		    	recInTemp = JDTORecordFactory.getInstance().create();
		    	recInTemp.setField("YD_STK_COL_GP", 				szCarldStopLoc);
		    	recInTemp.setField("YD_STK_BED_NO", 				szStkBedNo);
		    	recInTemp.setField("YD_STK_BED_ACT_STAT", 			"L");
		    	intRtnVal = ydStkBedDao.updYdStkbed(recInTemp, 0);
				if(intRtnVal <= 0) {
	    			if(intRtnVal == 0) {
	    				szMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 시 data not found";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}else if(intRtnVal == -1) {
	    				szMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 시 duplicate data,";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
	    			}else if(intRtnVal == -2) {
	    				szMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 시 parameter error";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}else if(intRtnVal == -3){
	    				szMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 시 execution failed";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}
	    			//return intRtnVal = -1;
	    			throw new DAOException(szMsg);
	    		}
				
				szMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 완료 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//--------------------------------------------------------------------------------------------
		    	
				//--------------------------------------------------------------------------------------------
		    	//	상차정지위치 단정보 활성화
				//--------------------------------------------------------------------------------------------
				szMsg="["+szOperationName+"] 상차정지위치단[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_STK_COL_GP", 			szCarldStopLoc);
				recInTemp.setField("YD_STK_BED_NO", 			szStkBedNo);
				recInTemp.setField("STL_NO", 					"");
				recInTemp.setField("YD_STK_LYR_ACT_STAT", 		"E");
				recInTemp.setField("YD_STK_LYR_MTL_STAT", 		"E");
		    	
				intRtnVal = ydStkLyrDao.updYdStklyrYdStkColGpBedNo2(recInTemp);
				if(intRtnVal <= 0) {
					szMsg = "["+szOperationName+"] 상차정지위치단[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 시 오류발생 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					throw new DAOException(szMsg);
				}
				
				szMsg="["+szOperationName+"] 상차정지위치단[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 완료 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//--------------------------------------------------------------------------------------------

	    	}
	    	//--------------------------------------------------------------------------------------------
	    	
	    	recRtnVal.setField("YD_STAT_GBN" , "US");						          
	    	recRtnVal.setField("YD_REQ_GBN"  , szLD_START_SCH_REQ_YN);
	    	recRtnVal.setField("YD_WBOOK_ID" , szWbookId);
	    	
 		}catch(Exception e){
	
			szMsg="["+szOperationName+"]  Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMsg);
		}
	
	
 		szMsg="["+szOperationName+"] --------------------- 메소드 끝 ---------------------";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		return recRtnVal;
	} //end of Y3UTcarStartWr()
	
	
	
	
	
	
	

	/**
	 * 오퍼레이션명 : 공차도착실적
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord, recTcarSch
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public JDTORecord Y3UTcarStopWr(JDTORecord msgRecord, JDTORecord recTcarSch)throws JDTOException  {
		
		YdStkBedDao ydStkBedDao = new YdStkBedDao();
		YdTcarSchDao ydTcarSchDao       = new YdTcarSchDao();
		YdStkLyrDao ydStkLyrDao         = new YdStkLyrDao();
		
		JDTORecord    recInTemp         = null;
		JDTORecord 	  recRtnVal 		= JDTORecordFactory.getInstance().create();
		
	    String szMsg              		= "";
	    String szMethodName       		= "Y3UTcarStopWr";
	    String szOperationName			= "대차공차도착실적";
	    
	    int intRtnVal                   = 0;
	    
	    String szStkColGp               = "";
	    String szStkBedNo               = "01";
	    String szSchReqGp               = "";
	    String szCarldStopLoc           = "";
	    String szWbookId                = "";
	    String szYD_TCAR_SCH_ID         = "";
	    String szYD_TO_BAY              = "";
	    String szYD_GP                  = "";
	    String szYD_EQP_ID              = "";
	    String szYD_BAY_GP1             = "";
	    String szYD_BAY_GP2             = "";
	    String szYD_CAR_PROG_STAT		= "";
	    String szLD_START_SCH_REQ_YN	= null;

	    try{	
	    	
	    	szMsg="["+szOperationName+"] --------------------- 메소드 시작 ---------------------";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
			/*
			YD_EQP_ID 야드설비ID
			YD_TCAR_MOVE_GP 야드대차이동구분
			YD_TCAR_MOVE_DIR 야드대차이동방향
			YD_BAY_GP1 야드동구분1 현재동
			YD_BAY_GP2 야드동구분2 목적동
			*/
	    	//스케줄 요청 구분판단
	    	szSchReqGp     = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARLD_SCH_REQ_GP");					//상차스케줄요청구분(상차출발, 상차도착)
	    	szCarldStopLoc = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARLD_STOP_LOC");					//상차정지위치
	    	szWbookId      = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARLD_WRK_BOOK_ID");					//상차작업예약ID
	    	szYD_TO_BAY    = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_TO_BAY");							//
	    	szYD_EQP_ID    = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");								//대차설비ID	    	
	    	szYD_BAY_GP1   = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_BAY_GP1");							//현재동
	    	szYD_BAY_GP2   = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_BAY_GP2");							//목적동
			
			szYD_TCAR_SCH_ID = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_TCAR_SCH_ID");
			
			recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_TCAR_SCH_ID", szYD_TCAR_SCH_ID);
	    	
	    	//20090702 김진욱 대차진행상태값 등록
	    	if(szWbookId.equals("")){
	    		//--------------------------------------------------------------------------------------------------
	    		//	상차작업예약이 없는 이유는 작업자가 지정한 동으로 이동했거나 홈동으로 이동한 경우이기때문에...상차도착으로 처리하지 않고
	    		//	공차대기로 수정
	    		//--------------------------------------------------------------------------------------------------
	    		szYD_CAR_PROG_STAT	= "0";
	    		
	    	}else{
	    		//--------------------------------------------------------------------------------------------------
	    		//	상차작업예약이 존재하므로 상차도착으로 수정
	    		//--------------------------------------------------------------------------------------------------
	    		szYD_CAR_PROG_STAT	= "2";
	    	}
	    	
	    	recInTemp.setField("YD_CAR_PROG_STAT", 		szYD_CAR_PROG_STAT);			//차량진행상태
	    	
	    	//--------------------------------------------------------------------------------------------------
    		//	대차스케줄의 차량진행상태를 수정
    		//--------------------------------------------------------------------------------------------------
	    	szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태를 ["+szYD_CAR_PROG_STAT+"]로 수정 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
	    	intRtnVal = ydTcarSchDao.updYdTcarsch(recInTemp, 0);
			if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태를 ["+szYD_CAR_PROG_STAT+"]로 수정 시 : data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}else if(intRtnVal == -1) {
    				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태를 ["+szYD_CAR_PROG_STAT+"]로 수정 시 : duplicate data,";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
    			}else if(intRtnVal == -2) {
    				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태를 ["+szYD_CAR_PROG_STAT+"]로 수정 시 : parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}else if(intRtnVal == -3){
    				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태를 ["+szYD_CAR_PROG_STAT+"]로 수정 시 : execution failed";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}
    			throw new DAOException(szMsg);
    		}
			szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태를 ["+szYD_CAR_PROG_STAT+"]로 수정 완료 - 반환값 : " + intRtnVal;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	//--------------------------------------------------------------------------------------------------
			
			//--------------------------------------------------------------------------------------------------
	    	//	상차 스케줄 요청 구분이 공차 도착이라면 상차정지위치 활성화, 대차스케줄에 상차작업예약이 존재하면 크레인스케줄 호출
			//	--> 1. 상차 스케줄 요청 구분을 BRE Rule에서 상차출발시스케줄요청여부을 조회해서 처리하도록 로직 변경
			//	수정일 : 1. 2010.02.23 임춘수
			//--------------------------------------------------------------------------------------------------
			szLD_START_SCH_REQ_YN	= YdCommonUtils.getLdStartSchReqYN(szYD_EQP_ID);
			
			if(szLD_START_SCH_REQ_YN.equals("N")) {
	    		
	    		szMsg="["+szOperationName+"] 대차스케줄["+szYD_TCAR_SCH_ID+"]의 상차스케줄요청구분["+szSchReqGp+"]이 공차도착이면 "; 
	    		szMsg+="상차정지위치[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 활성화";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//--------------------------------------------------------------------------------------------
	    		//	상차정지위치 베드 활성화
	    		//--------------------------------------------------------------------------------------------
				szMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		
	    		recInTemp = JDTORecordFactory.getInstance().create();
		    	recInTemp.setField("YD_STK_COL_GP", 			szCarldStopLoc);
		    	recInTemp.setField("YD_STK_BED_NO", 			szStkBedNo);
		    	recInTemp.setField("YD_STK_BED_ACT_STAT", 		"L");
		    	intRtnVal = ydStkBedDao.updYdStkbed(recInTemp, 0);
				if(intRtnVal <= 0) {
	    			if(intRtnVal == 0) {
	    				szMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 시 data not found";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}else if(intRtnVal == -1) {
	    				szMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 시 duplicate data,";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
	    			}else if(intRtnVal == -2) {
	    				szMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 시 parameter error";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}else if(intRtnVal == -3){
	    				szMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 시 execution failed";
	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			}
	    			throw new DAOException(szMsg);
	    		}
				
				szMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 완료 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//--------------------------------------------------------------------------------------------------
		    	//	상차정지위치 단정보 활성화
				//--------------------------------------------------------------------------------------------------
				szMsg="["+szOperationName+"] 상차정지위치단[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_STK_COL_GP", 			szCarldStopLoc);
				recInTemp.setField("YD_STK_BED_NO", 			szStkBedNo);
				recInTemp.setField("STL_NO", 					"");
				recInTemp.setField("YD_STK_LYR_ACT_STAT", 		"E");
				recInTemp.setField("YD_STK_LYR_MTL_STAT", 		"E");

				intRtnVal = ydStkLyrDao.updYdStklyrYdStkColGpBedNo2(recInTemp);
				if(intRtnVal <= 0) {
					szMsg = "["+szOperationName+"] 상차정지위치단[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 시 Error!!  - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					throw new DAOException(szMsg);
				}
				
				szMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 완료 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//--------------------------------------------------------------------------------------------------
	    	}
			
			recRtnVal.setField("YD_STAT_GBN" , "UE");						          
			recRtnVal.setField("YD_REQ_GBN"  , szLD_START_SCH_REQ_YN);
			recRtnVal.setField("YD_WBOOK_ID" , szWbookId);
			
 		}catch(Exception e){
	
			szMsg="["+szOperationName+"] Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			throw new DAOException(szMsg);
		}
	
	
 		szMsg="["+szOperationName+"] --------------------- 메소드 끝 ---------------------";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		return recRtnVal;
	} //end of Y3UTcarStopWr()
	
	
	
	
	
	

	/**
	 * 오퍼레이션명 : 영차출발실적
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord, recTcarSch
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public JDTORecord Y3LTcarStartWr(JDTORecord msgRecord, JDTORecord recTcarSch)throws JDTOException  {
		YdStkBedDao ydStkBedDao = new YdStkBedDao();
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		YdWrkbookMtlDao  ydWrkbookMtlDao  = new YdWrkbookMtlDao();
		YdTcarSchDao ydTcarSchDao = new YdTcarSchDao();
		
		JDTORecordSet rsResult          = null;
		JDTORecord    recInTemp         = null;
		JDTORecord    recOutTemp        = null;
		JDTORecord 	  recRtnVal 		= JDTORecordFactory.getInstance().create();
		
	    String szMsg              		= "";
	    String szMethodName       		= "Y3LTcarStartWr";
	    String szOperationName			= "대차영차출발실적";
	    
	    String szSchReqGp               = "";
	    String szCarldStopLoc           = "";
	    String szCarudStopLoc           = "";
	    String szStkColGp               = "";
	    String szStkBedNo               = "01";
	    String szYD_STK_LYR_NO			= null;
	    String szWbookId                = "";
	    String szYD_EQP_ID              = "";
	    String szYD_TCAR_SCH_ID         = "";
	    String szSTL_NO					= null;
	    
	    int intRtnVal                   = 0;
	    
	    try{
	    	szMsg="["+szOperationName+"] ----------------------- 메소드 시작 -----------------------";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
	    	szYD_EQP_ID 	 = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			szYD_TCAR_SCH_ID = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_TCAR_SCH_ID");
			
			//-------------------------------------------------------------------------------------------------
			//	대차스케줄의 차량진행상태를 영차출발로 수정
			//-------------------------------------------------------------------------------------------------
			szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태를 영차출발(하차출발)[A]상태로 수정 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_TCAR_SCH_ID", 			szYD_TCAR_SCH_ID);
	    	recInTemp.setField("YD_CAR_PROG_STAT", 			"A");
	    	intRtnVal = ydTcarSchDao.updYdTcarsch(recInTemp, 0);
			if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태를 영차출발(하차출발)[A]상태로 수정 시 : data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}else if(intRtnVal == -1) {
    				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태를 영차출발(하차출발)[A]상태로 수정 시 : duplicate data,";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
    			}else if(intRtnVal == -2) {
    				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태를 영차출발(하차출발)[A]상태로 수정 시 : parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}else if(intRtnVal == -3){
    				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태를 영차출발(하차출발)[A]상태로 수정 시 : execution failed";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}
    			throw new DAOException(szMsg);
    		}
			szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태를 영차출발(하차출발)[A]상태로 수정 완료 - 반환값 : " + intRtnVal;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//-------------------------------------------------------------------------------------------------
			
			//-------------------------------------------------------------------------------------------------
	    	//	상차 정지위치의 베드상태 비활성화하고 적치단정보 Clear
	    	//-------------------------------------------------------------------------------------------------
	    	szSchReqGp     			= ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARUD_SCH_REQ_GP");
	    	szCarldStopLoc 			= ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARLD_STOP_LOC");
	    	szCarudStopLoc 			= ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARUD_STOP_LOC");
	    	
	    	szMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 비활성화 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_STK_COL_GP", 			szCarldStopLoc);
	    	recInTemp.setField("YD_STK_BED_NO", 			szStkBedNo);
	    	recInTemp.setField("YD_STK_BED_ACT_STAT", 		"C");
	    	intRtnVal = ydStkBedDao.updYdStkbed(recInTemp, 0);
			if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 비활성화 시 data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}else if(intRtnVal == -1) {
    				szMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 비활성화 시 duplicate data,";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
    			}else if(intRtnVal == -2) {
    				szMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 비활성화 시 parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}else if(intRtnVal == -3){
    				szMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 비활성화 시 execution failed";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}
    			throw new DAOException(szMsg);
    		}
			szMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 비활성화 완료 - 반환값 : " + intRtnVal;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//-------------------------------------------------------------------------------------------------
			
			//-------------------------------------------------------------------------------------------------
			//	상차정지위치 단정보 Clear, 적치단활성상태를 비활성화로...
			//-------------------------------------------------------------------------------------------------
			
			szMsg="["+szOperationName+"] 상차정지위치단[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 비활성화 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_STK_COL_GP", 			szCarldStopLoc);
			recInTemp.setField("YD_STK_BED_NO", 			szStkBedNo);
			recInTemp.setField("STL_NO", 					"");
			recInTemp.setField("YD_STK_LYR_ACT_STAT", 		"C");
			recInTemp.setField("YD_STK_LYR_MTL_STAT", 		"E");
	    	
			intRtnVal = ydStkLyrDao.updYdStklyrYdStkColGpBedNo2(recInTemp);
			if(intRtnVal <= 0) {
				szMsg = "["+szOperationName+"] 상차정지위치단[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 비활성화 시 Error!! - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException(szMsg);
			}
	    	
			szMsg="["+szOperationName+"] 상차정지위치단[적치열:"+szCarldStopLoc+", 적치베드:"+szStkBedNo+"] 비활성화 완료 - 반환값 : " + intRtnVal;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//-------------------------------------------------------------------------------------------------
			
			//-------------------------------------------------------------------------------------------------
	    	//	하차스케줄 요청 구분이 영차 출발이라면 하차정지위치 맵 활성화, 대차스케줄의 하차작업예약에 대한 크레인스케줄 호출
			//	1. 하차스케쥴요청구분을 BRE Rule에서 가져오는 것으로 변경 2010.02.24 임춘수
			//-------------------------------------------------------------------------------------------------
			
			String szUL_START_SCH_REQ_YN = YdCommonUtils.getUlStartSchReqYN(szYD_EQP_ID);
			
			if(szUL_START_SCH_REQ_YN.equals("Y")) {
	    		
	    		szWbookId = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARUD_WRK_BOOK_ID");
	    		
	    		szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차스케줄요청구분["+szSchReqGp+"]이 영차 출발이므로 하차정지위치 맵 활성화, 하차작업예약["+szWbookId+"]에 대한 크레인스케줄 호출";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		
	    		//-------------------------------------------------------------------------------------------------
		    	//	하차정지위치 Bed상태 활성화
	    		//-------------------------------------------------------------------------------------------------
	    		szMsg="["+szOperationName+"] 하차정지위치베드[적치열:"+szCarudStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		
		    	recInTemp = JDTORecordFactory.getInstance().create();
		    	recInTemp.setField("YD_STK_COL_GP", 		szCarudStopLoc);
		    	recInTemp.setField("YD_STK_BED_NO", 		szStkBedNo);
		    	recInTemp.setField("YD_STK_BED_ACT_STAT", 	"L");
		    	intRtnVal = ydStkBedDao.updYdStkbed(recInTemp, 0);
		    	
		    	szMsg="["+szOperationName+"] 하차정지위치베드[적치열:"+szCarudStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 완료 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//-------------------------------------------------------------------------------------------------
				
				//-------------------------------------------------------------------------------------------------
		    	//	하차정지위치 단상태 활성화
	    		//-------------------------------------------------------------------------------------------------
				szMsg="["+szOperationName+"] 하차정지위치단[적치열:"+szCarudStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_STK_COL_GP", 			szCarudStopLoc);
				recInTemp.setField("YD_STK_BED_NO", 			szStkBedNo);
				recInTemp.setField("STL_NO", 					"");
				recInTemp.setField("YD_STK_LYR_ACT_STAT", 		"E");
				recInTemp.setField("YD_STK_LYR_MTL_STAT", 		"E");
				
				intRtnVal = ydStkLyrDao.updYdStklyrYdStkColGpBedNo2(recInTemp);
				if(intRtnVal <= 0) {
					szMsg = "["+szOperationName+"] 하차정지위치단[적치열:"+szCarudStopLoc+", 적치베드:"+szStkBedNo+"] 비활성화 시 Error!! - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					throw new DAOException(szMsg);
				}
		    	
				szMsg="["+szOperationName+"] 하차정지위치단[적치열:"+szCarudStopLoc+", 적치베드:"+szStkBedNo+"] 비활성화 완료 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//-------------------------------------------------------------------------------------------------
				
		    	//-------------------------------------------------------------------------------------------------
		        //	하차작업예약ID를 조회해서 작업예약재료 Table를 조회한다.
		    	//-------------------------------------------------------------------------------------------------
		    	szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차작업예약["+szWbookId+"]의 작업재료를 적치단에 등록하기 위해서 조회 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    	
		    	recInTemp = JDTORecordFactory.getInstance().create();
		    	recInTemp.setField("YD_WBOOK_ID", szWbookId);
		    	rsResult  = JDTORecordFactory.getInstance().createRecordSet("Temp");
		    	intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recInTemp, rsResult, 1);
				if(intRtnVal <= 0) {
					if(intRtnVal == 0) {
						szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차작업예약["+szWbookId+"]의 작업재료를 적치단에 등록하기 위해서 조회 시 data not found";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}else if(intRtnVal == -2) {
						szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차작업예약["+szWbookId+"]의 작업재료를 적치단에 등록하기 위해서 조회 시 parameter error";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					throw new DAOException(szMsg);
				}
				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차작업예약["+szWbookId+"]의 작업재료를 적치단에 등록하기 위해서 조회 완료 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//-------------------------------------------------------------------------------------------------
		    	
				//-------------------------------------------------------------------------------------------------
		    	//	작업예약재료의 정보를 적치단에 등록한다.
				//-------------------------------------------------------------------------------------------------
				for(int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {
		    		
		    		rsResult.absolute(Loop_i);
		    		recInTemp  = JDTORecordFactory.getInstance().create();
		    		recOutTemp = JDTORecordFactory.getInstance().create();
		    		recInTemp.setRecord(rsResult.getRecord());
		    		
		    		szStkColGp 			= ydDaoUtils.paraRecChkNull(recInTemp, "YD_STK_COL_GP");
					szStkBedNo 			= ydDaoUtils.paraRecChkNull(recInTemp, "YD_STK_BED_NO");
		    		szYD_STK_LYR_NO		= ydDaoUtils.paraRecChkNull(recInTemp, "YD_STK_LYR_NO");
		    		szSTL_NO			= ydDaoUtils.paraRecChkNull(recInTemp, "STL_NO");
		    		
		    		szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차작업예약["+szWbookId+"]의 작업재료["+szSTL_NO+"]를 적치단[열:"+szStkColGp+", 베드:"+szStkBedNo+", 단:"+szYD_STK_LYR_NO+"]에 등록 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    		
		    		recOutTemp.setField("YD_STK_COL_GP",       szStkColGp);
		    		recOutTemp.setField("YD_STK_BED_NO",       szStkBedNo);
		    		recOutTemp.setField("YD_STK_LYR_NO",       szYD_STK_LYR_NO);
		    		recOutTemp.setField("STL_NO",              szSTL_NO);
		    		recOutTemp.setField("YD_STK_LYR_MTL_STAT", "C");
					recInTemp.setField("YD_STK_LYR_ACT_STAT",  "E");
		    		intRtnVal = ydStkLyrDao.updYdStklyr(recOutTemp, 0);
					if(intRtnVal <= 0) {
		    			if(intRtnVal == 0) {
		    				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차작업예약["+szWbookId+"]의 작업재료["+szSTL_NO+"]를 적치단[열:"+szStkColGp+", 베드:"+szStkBedNo+", 단:"+szYD_STK_LYR_NO+"]에 등록 시 data not found";
		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		    			}else if(intRtnVal == -1) {
		    				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차작업예약["+szWbookId+"]의 작업재료["+szSTL_NO+"]를 적치단[열:"+szStkColGp+", 베드:"+szStkBedNo+", 단:"+szYD_STK_LYR_NO+"]에 등록 시 duplicate data,";
		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
		    			}else if(intRtnVal == -2) {
		    				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차작업예약["+szWbookId+"]의 작업재료["+szSTL_NO+"]를 적치단[열:"+szStkColGp+", 베드:"+szStkBedNo+", 단:"+szYD_STK_LYR_NO+"]에 등록 시 parameter error";
		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		    			}else if(intRtnVal == -3){
		    				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차작업예약["+szWbookId+"]의 작업재료["+szSTL_NO+"]를 적치단[열:"+szStkColGp+", 베드:"+szStkBedNo+", 단:"+szYD_STK_LYR_NO+"]에 등록 시 execution failed";
		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		    			}
		    			throw new DAOException(szMsg);
		    		}
					
					szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차작업예약["+szWbookId+"]의 작업재료["+szSTL_NO+"]를 적치단[열:"+szStkColGp+", 베드:"+szStkBedNo+", 단:"+szYD_STK_LYR_NO+"]에 등록 완료 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    	}
		    	//-------------------------------------------------------------------------------------------------
	    	}
			
			recRtnVal.setField("YD_STAT_GBN" , "LS");						          
			recRtnVal.setField("YD_REQ_GBN"  , szUL_START_SCH_REQ_YN);
			recRtnVal.setField("YD_WBOOK_ID" , szWbookId);
			
 		}catch(Exception e){
	
			szMsg="["+szOperationName+"] Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMsg);
		}
	
 		szMsg="["+szOperationName+"] ----------------------- 메소드 끝 -----------------------";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		return recRtnVal;
	} //end of Y3LTcarStartWr()
	
	
	
	

	

	/**
	 * 오퍼레이션명 : 영차도착실적
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord, recTcarSch
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public JDTORecord Y3LTcarStopWr(JDTORecord msgRecord, JDTORecord recTcarSch)throws JDTOException  {
		
		YdStkBedDao ydStkBedDao = new YdStkBedDao();
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		YdWrkbookMtlDao  ydWrkbookMtlDao  = new YdWrkbookMtlDao();
		YdTcarSchDao ydTcarSchDao = new YdTcarSchDao();
		
		JDTORecordSet rsResult          = null;
		JDTORecord    recInTemp         = null;
		JDTORecord    recOutTemp        = null;
		JDTORecord 	  recRtnVal 		= JDTORecordFactory.getInstance().create();
		
	    String szMsg              		= "";
	    String szMethodName       		= "Y3LTcarStopWr";
	    String szOperationName			= "대차영차도착실적";

	    int intRtnVal                   = 0;

	    String szSchReqGp               = "";
	    String szCarldStopLoc           = "";
	    String szCarudStopLoc           = "";
	    String szStkBedNo               = "01";
	    String szYD_STK_LYR_NO			= null;
	    String szSTL_NO					= null;
	    String szWbookId                = "";
	    String szYD_EQP_ID              = "";
	    String szYD_TCAR_SCH_ID         = "";
	    String szYD_BAY_GP              = "";
	    
	    try{
	    	
	    	szMsg="["+szOperationName+"] ------------------------ 메소드 시작 ------------------------";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
	    	szSchReqGp     		= ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARUD_SCH_REQ_GP");
	    	szCarldStopLoc 		= ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARLD_STOP_LOC");
	    	szCarudStopLoc 		= ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARUD_STOP_LOC");
	    	
	    	//대차스케줄에 대차진행상태 update
	    	szYD_EQP_ID 		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			szYD_TCAR_SCH_ID 	= ydDaoUtils.paraRecChkNull(recTcarSch, "YD_TCAR_SCH_ID");
			
			//-------------------------------------------------------------------------------------------------
			//	대차스케줄의 차량진행상태를 영차도착로 수정
			//-------------------------------------------------------------------------------------------------
			szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태를 영차도착(하차도착)[B]상태로 수정 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_TCAR_SCH_ID", 			szYD_TCAR_SCH_ID);
	    	recInTemp.setField("YD_CAR_PROG_STAT", 			"B");
	    	recInTemp.setField("YD_CURR_BAY_GP", 			szCarudStopLoc.substring(1,2));
	    	intRtnVal = ydTcarSchDao.updYdTcarsch(recInTemp, 0);
			if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태를 영차도착(하차도착)[B]상태로 수정 시 : data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}else if(intRtnVal == -1) {
    				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태를 영차도착(하차도착)[B]상태로 수정 시 : duplicate data,";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
    			}else if(intRtnVal == -2) {
    				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태를 영차도착(하차도착)[B]상태로 수정 시 : parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}else if(intRtnVal == -3){
    				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태를 영차도착(하차도착)[B]상태로 수정 시 : execution failed";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}
    			throw new DAOException(szMsg);
    		}
			szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 차량진행상태를 영차도착(하차도착)[B]상태로 수정 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//-------------------------------------------------------------------------------------------------
			
			//-------------------------------------------------------------------------------------------------
	    	//	하차스케줄 요청 구분이 영차도착이라면 하차정지위치 맵 활성화, 하차작업예약에 대한 크레인스케줄 호출
			//-------------------------------------------------------------------------------------------------
			String szUL_START_SCH_REQ_YN = YdCommonUtils.getUlStartSchReqYN(szYD_EQP_ID);
			
	    	if(szUL_START_SCH_REQ_YN.equals("N")) {

	    		szWbookId = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARUD_WRK_BOOK_ID");
	    		
	    		szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차스케줄요청구분["+szSchReqGp+"]이 영차 도착이므로 하차정지위치 맵 활성화, 하차작업예약["+szWbookId+"]에 대한 크레인스케줄 호출";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				//-------------------------------------------------------------------------------------------------
		    	//	하차정지위치 Bed상태 활성화
	    		//-------------------------------------------------------------------------------------------------
		    	szMsg="["+szOperationName+"] 하차정지위치베드[적치열:"+szCarudStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		
		    	recInTemp = JDTORecordFactory.getInstance().create();
		    	recInTemp.setField("YD_STK_COL_GP", 			szCarudStopLoc);
		    	recInTemp.setField("YD_STK_BED_NO", 			szStkBedNo);
		    	recInTemp.setField("YD_STK_BED_ACT_STAT", 		"L");
		    	intRtnVal = ydStkBedDao.updYdStkbed(recInTemp, 0);
		    	
		    	szMsg="["+szOperationName+"] 하차정지위치베드[적치열:"+szCarudStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 완료 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    	//-------------------------------------------------------------------------------------------------
		    	
				//-------------------------------------------------------------------------------------------------
		    	//	하차정지위치 단상태 활성화
	    		//-------------------------------------------------------------------------------------------------
				szMsg="["+szOperationName+"] 하차정지위치단[적치열:"+szCarudStopLoc+", 적치베드:"+szStkBedNo+"] 활성화 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_STK_COL_GP", 			szCarudStopLoc);
				recInTemp.setField("YD_STK_BED_NO", 			szStkBedNo);
				recInTemp.setField("STL_NO", 					"");
				recInTemp.setField("YD_STK_LYR_ACT_STAT", 		"E");
				recInTemp.setField("YD_STK_LYR_MTL_STAT", 		"E");
				
				intRtnVal = ydStkLyrDao.updYdStklyrYdStkColGpBedNo2(recInTemp);
				if(intRtnVal <= 0) {
					szMsg = "["+szOperationName+"] 하차정지위치단[적치열:"+szCarudStopLoc+", 적치베드:"+szStkBedNo+"] 비활성화 시 Error!! - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					throw new DAOException(szMsg);
				}
		    	
				szMsg="["+szOperationName+"] 하차정지위치단[적치열:"+szCarudStopLoc+", 적치베드:"+szStkBedNo+"] 비활성화 완료 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//-------------------------------------------------------------------------------------------------
				
				//-------------------------------------------------------------------------------------------------
		    	//	하차작업예약ID를 조회해서 작업예약재료 Table를 조회한다.
				//-------------------------------------------------------------------------------------------------
				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차작업예약["+szWbookId+"]의 작업재료를 적치단에 등록하기 위해서 조회 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
		    	recInTemp = JDTORecordFactory.getInstance().create();
		    	recInTemp.setField("YD_WBOOK_ID", 				szWbookId);
		    	rsResult  = JDTORecordFactory.getInstance().createRecordSet("Temp");
		    	intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recInTemp, rsResult, 1);
				if(intRtnVal <= 0) {
					if(intRtnVal == 0) {
						szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차작업예약["+szWbookId+"]의 작업재료를 적치단에 등록하기 위해서 조회 시 data not found";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}else if(intRtnVal == -2) {
						szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차작업예약["+szWbookId+"]의 작업재료를 적치단에 등록하기 위해서 조회 시 parameter error";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					throw new DAOException(szMsg);
				}
				
				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차작업예약["+szWbookId+"]의 작업재료를 적치단에 등록하기 위해서 조회 완료 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//-------------------------------------------------------------------------------------------------
		    	
				//-------------------------------------------------------------------------------------------------
		    	//	작업예약재료의 정보를 적치단에 등록한다.
				//-------------------------------------------------------------------------------------------------
		    	for(int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {
		    		
		    		rsResult.absolute(Loop_i);
		    		recInTemp  = JDTORecordFactory.getInstance().create();
		    		recOutTemp = JDTORecordFactory.getInstance().create();
		    		recInTemp.setRecord(rsResult.getRecord());
		    		
		    		szStkBedNo 			= ydDaoUtils.paraRecChkNull(recInTemp, "YD_STK_BED_NO");
		    		szYD_STK_LYR_NO		= ydDaoUtils.paraRecChkNull(recInTemp, "YD_STK_LYR_NO");
		    		szSTL_NO			= ydDaoUtils.paraRecChkNull(recInTemp, "STL_NO");
		    		
		    		szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차작업예약["+szWbookId+"]의 작업재료["+szSTL_NO+"]를 적치단[열:"+szCarudStopLoc+", 베드:"+szStkBedNo+", 단:"+szYD_STK_LYR_NO+"]에 등록 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    		
		    		recOutTemp.setField("YD_STK_COL_GP",       		szCarudStopLoc);
		    		recOutTemp.setField("YD_STK_BED_NO",       		szStkBedNo);
		    		recOutTemp.setField("YD_STK_LYR_NO",       		szYD_STK_LYR_NO);
		    		recOutTemp.setField("STL_NO",              		szSTL_NO);
		    		recOutTemp.setField("YD_STK_LYR_MTL_STAT", 		"C");
		    		recOutTemp.setField("YD_STK_LYR_ACT_STAT", 		"E");
		    		intRtnVal = ydStkLyrDao.updYdStklyr(recOutTemp, 0);
					if(intRtnVal <= 0) {
		    			if(intRtnVal == 0) {
		    				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차작업예약["+szWbookId+"]의 작업재료["+szSTL_NO+"]를 적치단[열:"+szCarudStopLoc+", 베드:"+szStkBedNo+", 단:"+szYD_STK_LYR_NO+"]에 등록 시 data not found";
		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		    			}else if(intRtnVal == -1) {
		    				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차작업예약["+szWbookId+"]의 작업재료["+szSTL_NO+"]를 적치단[열:"+szCarudStopLoc+", 베드:"+szStkBedNo+", 단:"+szYD_STK_LYR_NO+"]에 등록 시 duplicate data,";
		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);	
		    			}else if(intRtnVal == -2) {
		    				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차작업예약["+szWbookId+"]의 작업재료["+szSTL_NO+"]를 적치단[열:"+szCarudStopLoc+", 베드:"+szStkBedNo+", 단:"+szYD_STK_LYR_NO+"]에 등록 시 parameter error";
		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		    			}else if(intRtnVal == -3){
		    				szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차작업예약["+szWbookId+"]의 작업재료["+szSTL_NO+"]를 적치단[열:"+szCarudStopLoc+", 베드:"+szStkBedNo+", 단:"+szYD_STK_LYR_NO+"]에 등록 시 execution failed";
		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		    			}
		    			throw new DAOException(szMsg);
		    		}
					
					szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 하차작업예약["+szWbookId+"]의 작업재료["+szSTL_NO+"]를 적치단[열:"+szCarudStopLoc+", 베드:"+szStkBedNo+", 단:"+szYD_STK_LYR_NO+"]에 등록 완료 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    	}
		    	//-------------------------------------------------------------------------------------------------
	    	}
	    	
	    	recRtnVal.setField("YD_STAT_GBN" , "LE");						          
	    	recRtnVal.setField("YD_REQ_GBN"  , szUL_START_SCH_REQ_YN);
	    	recRtnVal.setField("YD_WBOOK_ID" , szWbookId);
	    	
 		}catch(Exception e){
	
			szMsg="["+szOperationName+"] Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMsg);
		}
	
 		szMsg="["+szOperationName+"] ------------------------ 메소드 끝 ------------------------";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		return recRtnVal;
	} //end of Y3LTcarStopWr()
	
	
	/**
	 * 오퍼레이션명 : 크레인스케줄호출
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public int Y3CallCrnSch1(JDTORecord msgRecord)throws JDTOException  {
		YdWrkbookDao     ydWrkbookDao     = new YdWrkbookDao();
		YdSchRuleDao     ydSchRuleDao     = new YdSchRuleDao();
		YdDelegate       ydDelegate 	  = new YdDelegate();
		
		JDTORecordSet rsResult          = null;
		JDTORecord    recInTemp         = null;

	    
	    String szMsg              		= "";
	    String szMethodName       		= "Y3CallCrnSch";

	    int intRtnVal                   = 0;

	    String szWbookId                = "";
	    String szSchCd                  = "";
	    String szEqpId                  = "";
	    
	    try{
	    	//작업예약ID
	    	szWbookId = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");
	    	
	    	//작업예약 id로 작업예약Table를 조회한다.
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	intRtnVal = ydWrkbookDao.getYdWrkbook(msgRecord, rsResult, 0);
			if(intRtnVal <= 0) {
				if(intRtnVal == 0) {
					szMsg="<Y3CallCrnSch> getYdWrkbook data not found";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				}else if(intRtnVal == -2) {
					szMsg="<Y3CallCrnSch> getYdWrkbook parameter error";
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
			/*     스케쥴기준 조회 - 스케쥴 금지유무 판단, 작업크레인, 대체크레인 조회	- 2009.04.10 임춘수	*/
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
	    	

	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	//크레인스케줄MAIN호출 TC : YDYDJ503, 스케줄코드 설비ID
	    	if(szEqpId.substring(0,1).equals("A")) {
		    	recInTemp.setField("MSG_ID",    "YDYDJ500");
		    	recInTemp.setField("YD_SCH_CD", szSchCd);
		    	recInTemp.setField("YD_EQP_ID", szEqpId);
	    	}else if(szEqpId.substring(0,1).equals("S")) {
		    	recInTemp.setField("MSG_ID",    "YDYDJ512");
		    	recInTemp.setField("YD_SCH_CD", szSchCd);
		    	recInTemp.setField("YD_EQP_ID", szEqpId);
	    	}else if(szEqpId.substring(0,1).equals("D")) {
		    	recInTemp.setField("MSG_ID",    "YDYDJ503");
		    	recInTemp.setField("YD_SCH_CD", szSchCd);
		    	recInTemp.setField("YD_EQP_ID", szEqpId);
	    	}else if(szEqpId.substring(0,1).equals("K")) {
		    	recInTemp.setField("MSG_ID",    "YDYDJ506");
		    	recInTemp.setField("YD_SCH_CD", szSchCd);
		    	recInTemp.setField("YD_EQP_ID", szEqpId);
	    	}else if(szEqpId.substring(0,1).equals("H") || szEqpId.substring(0,1).equals("J")) {
		    	recInTemp.setField("MSG_ID",    "YDYDJ509");
		    	recInTemp.setField("YD_SCH_CD", szSchCd);
		    	recInTemp.setField("YD_EQP_ID", szEqpId);
	    	}

	    	//서버 메세지 전송 메소드 크레인 스케줄 호출
	    	ydDelegate.sendMsg(recInTemp);

	    	
 		}catch(Exception e){
	
			szMsg="크레인스케줄 호출 Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			return intRtnVal = -1;
		}
	
	
		szMsg="크레인스케줄 호출("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		return intRtnVal = 1;
	} //end of Y3CallCrnSch()



	

//sjhkim	
//
//	
//	
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
//
//		
//		// 변수 선언
//	    String szMsg              		= "";
//	    String szMethodName       		= "procY5TcarMvWr";
//	    String szYD_EQP_ID              = "";
//	    String szYD_TCAR_MOVE_GP        = "";
//	    String szYD_TCAR_MOVE_DIR       = "";
//	    String szYD_BAY_GP1             = "";
//	    String szYD_BAY_GP2             = "";
//	    String szYD_GP                  = "";
//	    String szYD_STK_COL_GP          = "";
//	    int nRet                        = 0;
//	    int intRtnVal 					= 0 ;
//
//
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
//        // 설비ID
//		szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
//		if(szYD_EQP_ID.equals("")){
//			szMsg = "설비ID 값이 없습니다.";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			return YdConstant.RETN_INT_FAILURE;
//		}
//		
//		
//		// 야드대차이동구분
//		szYD_TCAR_MOVE_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_MOVE_GP");
//		if(szYD_TCAR_MOVE_GP.equals("")){
//			szMsg = "야드대차이동구분 값이 없습니다.";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			return YdConstant.RETN_INT_FAILURE;
//		}
//
//		
//		// 야드대차이동방향
//		szYD_TCAR_MOVE_DIR = ydDaoUtils.paraRecChkNull(msgRecord, "YD_TCAR_MOVE_DIR");
//		if(szYD_TCAR_MOVE_DIR.equals("")){
//			szMsg = "야드대차이동방향 값이 없습니다.";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			return YdConstant.RETN_INT_FAILURE;
//		}
//		
//		
//		// 현재동
//		szYD_BAY_GP1 = ydDaoUtils.paraRecChkNull(msgRecord, "YD_TCAR_CURR_BAY");
//		if(szYD_BAY_GP1.equals("")){
//			szMsg = "현재동 값이 없습니다.";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			return YdConstant.RETN_INT_FAILURE;
//		}
//
//		
//		// 목적동
//		szYD_BAY_GP2 = ydDaoUtils.paraRecChkNull(msgRecord, "YD_TCAR_AIM_BAY");	
//		if(szYD_BAY_GP2.equals("")){
//			szMsg = "목적동 값이 없습니다.";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			return YdConstant.RETN_INT_FAILURE;
//		}
//		
//		
//		// 야드
//		szYD_GP = szYD_EQP_ID.substring(0, 1);
//
//		// 적치열 구분
//		szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP1 + szYD_EQP_ID.substring(2, 6);
//
//		
//		
//		
//		
//		try{
//			//=============================================================
//			// 권오창
//			// 2009.11.05
//			//
//			// Log 테이블 등록 
//			//=============================================================

//			
//			
//			
//			/*
//			YD_EQP_ID 야드설비ID
//			YD_MOVE_GP 야드대차이동구분
//			YD_TCAR_MOVE_DIR 야드대차이동방향
//			YD_BAY_GP 야드동구분1 현재동
//			YD_TCAR_AIM_BAY 야드동구분2 목적동
//			*/
//			
//	    	// 1.대차스케줄상태Check (공차출발,공차도착,영차출발,영차도착 Check)
//	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
//	    	nRet = this.Y5ChkTcarSchStat(msgRecord, rsResult);
//	    	rsResult.absolute(1);
//	    	recTcarSch = JDTORecordFactory.getInstance().create();
//	    	recTcarSch.setRecord(rsResult.getRecord());
//	    	
//	    	switch (nRet) {
//	    		case 1	:
//	    			// 2.공차출발 Method호출
//	    			intRtnVal = this.Y5UTcarStartWr(msgRecord, recTcarSch);
//	    			break;
//	    		case 2	:
//	    			// 3.공차도착 Method호출
//	    			intRtnVal = this.Y5UTcarStopWr(msgRecord, recTcarSch);
//	    			break;
//	    		case 3	:
//	    			// 4.영차출발 Method호출
//	    			intRtnVal = this.Y5LTcarStartWr(msgRecord, recTcarSch);
//	    			break;
//	    		case 4	:
//	    			// 5.영차도착 Method호출
//	    			intRtnVal = this.Y5LTcarStopWr(msgRecord, recTcarSch);
//	    			break;
//	    	}
//	    	
//	    	if(intRtnVal < 0 ) {
//	    		szMsg="[연주정정 대차 이동실적]대차 이동 실적 처리 리턴값 : " + intRtnVal;
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	    		return YdConstant.RETN_INT_FAILURE;
//	    	}
//	    	
//	    	
//	    	
//	    	
//	    	
//			//===========================================================================
//			// 2009.12.02
//			// 권오창
//			//
//	    	// 2009.12.10 호출 순서  변경
//			// 적치대제원 전문을 L2로 전송
//			//===========================================================================
//			recPara = JDTORecordFactory.getInstance().create();
//			recPara.setField("YD_INFO_SYNC_CD" , "3");						          // 1:동,2:SPAN,3:열,4:BED
//			recPara.setField("YD_GP"           , szYD_GP);
//			recPara.setField("YD_STK_COL_GP"   , szYD_STK_COL_GP);
//			// 전문에 "S"면 출발이므로 "1" or "A" 를 넣어서 전문 호출하면 전문편집시 출발인 "S"로 송신
//			// 전문에 "E"면 도착이므로 "2" or "B" 를 넣어서 전문 호출하면 전문편집시 도착인 "A"로 송신
//			if(szYD_TCAR_MOVE_GP.equals(YdConstant.YD_TCAR_MOVE_GP_LEAVE)){
//				recPara.setField("YD_CAR_PROG_STAT", "1");						      // "1", "A" : 출발(S)    "2", "B" : 도착(A)        
//			} else if(szYD_TCAR_MOVE_GP.equals(YdConstant.YD_TCAR_MOVE_GP_ARRIVE)){
//				recPara.setField("YD_CAR_PROG_STAT", "2");						     
//			} else if(szYD_TCAR_MOVE_GP.equals(YdConstant.YD_TCAR_MOVE_GP_MOVE)) {
//				szMsg = "대차이동실적이므로 업무 종료 처리 YD_TCAR_MOVE_GP(" + szYD_TCAR_MOVE_GP + ")";
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
//			if(szYD_TCAR_MOVE_GP.equals("S") || szYD_TCAR_MOVE_GP.equals("E")){
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
//	
//	
//	
//	
//	
//	
//	
//	
//	
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
//	public int Y5ChkTcarSchStat(JDTORecord msgRecord, JDTORecordSet rsTarSch)throws JDTOException  {
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
//	    String szEqpWrkStat             = "";
//	    
//	    int intRtnVal                   = 0;
//
//	    
//	    try{
//	    	//설비id로 대차 스케줄을 조회한다.
//	    	szEqpId  = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
//	    	szMoveGp = ydDaoUtils.paraRecChkNull(msgRecord, "YD_MOVE_GP");
//	    	
//	    	
//	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
//	    	recInTemp = JDTORecordFactory.getInstance().create();
//	    	recInTemp.setField("YD_EQP_ID", szEqpId);
//	    	intRtnVal = ydTcarSchDao.getYdTcarsch(recInTemp, rsResult, 4);
//			if(intRtnVal <= 0) {
//				if(intRtnVal == 0) {
//					szMsg="<Y5ChkTcarSchStat> ydTcarSchDao data not found";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//				}else if(intRtnVal == -2) {
//					szMsg="<Y5ChkTcarSchStat> ydTcarSchDao parameter error";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//				}
//				return intRtnVal = -1;
//			}
//	    	
//	    	
//	    	
//	    	rsResult.absolute(1);
//	    	recOutTemp = JDTORecordFactory.getInstance().create();
//	    	recOutTemp.setRecord(rsResult.getRecord());
//	    	
//	    	
//	    	
//	        //대차 스케줄이 공차인지 상차인지 Check한다.
//	    	szEqpWrkStat = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_WRK_STAT");
//	    	
//	    	
//	    	//대차스케줄은 리턴하고
//	    	rsTarSch.addRecord(recOutTemp);
//	    	
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
//	    				szMsg="Y5ChkTcarSchStat updYdEqp : data not found";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//	    			}else if(intRtnVal == -1) {
//	    				szMsg="Y5ChkTcarSchStat updYdEqp : duplicate data,";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
//	    			}else if(intRtnVal == -2) {
//	    				szMsg="Y5ChkTcarSchStat updYdEqp : parameter error";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	    			}else if(intRtnVal == -3){
//	    				szMsg="Y5ChkTcarSchStat updYdEqp : execution failed";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	    			}
//	    			return intRtnVal = -1;
//	    		}
//
//	    		
//	    		
//	    		//공대차 출발인경우
//	    		if(szEqpWrkStat.equals("U")) {
//	    			return intRtnVal = 1;
//	    			
//	    		//영대차 출발인 경우	
//	    		}else if(szEqpWrkStat.equals("L")) {
//	    			return intRtnVal = 3;
//	    			
//	    		}
//	    		
//	    		
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
//	    				szMsg="Y5ChkTcarSchStat updYdEqp : data not found";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//	    			}else if(intRtnVal == -1) {
//	    				szMsg="Y5ChkTcarSchStat updYdEqp : duplicate data,";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
//	    			}else if(intRtnVal == -2) {
//	    				szMsg="Y5ChkTcarSchStat updYdEqp : parameter error";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	    			}else if(intRtnVal == -3){
//	    				szMsg="Y5ChkTcarSchStat updYdEqp : execution failed";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	    			}
//	    			return intRtnVal = -1;
//	    		}
//
//	    		
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
//	
//	
//	/**
//	 * 오퍼레이션명 : 공차출발실적
//	 *  
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param msgRecord, recTcarSch
//	 * @return intRtnVal
//	 * @throws JDTOException
//	 */
//	public int Y5UTcarStartWr(JDTORecord msgRecord, JDTORecord recTcarSch)throws JDTOException  {
//		YdStkBedDao ydStkBedDao = new YdStkBedDao();
//		YdTcarSchDao ydTcarSchDao = new YdTcarSchDao();
//		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
//		
//		JDTORecord    recInTemp          = null;
//		JDTORecord    recOutTemp         = null;
//		JDTORecordSet rsResult           = null;
//
//	    
//	    String szMsg              		= "";
//	    String szMethodName       		= "Y5UTcarStartWr";
//	    
//	    
//	    String szQuery                  = "";
//
//	    int intRtnVal                   = 0;
//	    
//	    String szStkColGp               = "";
//	    String szStkBedNo               = "01";
//	    String szSchReqGp               = "";
//	    String szCarldStopLoc           = "";
//	    String szWbookId                = "";
//	    String szYD_EQP_ID              = "";
//	    String szYD_TCAR_SCH_ID         = "";
//	    
//	    try{
//			/*
//			YD_EQP_ID 야드설비ID
//			YD_MOVE_GP 야드대차이동구분
//			YD_TCAR_MOVE_DIR 야드대차이동방향
//			YD_BAY_GP 야드동구분1 현재동
//			YD_TCAR_AIM_BAY 야드동구분2 목적동
//			*/
//	    	
//	    	
//	    	//대차스케줄에 대차진행상태 update
//	    	szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
//	    	recInTemp = JDTORecordFactory.getInstance().create();
//	    	recInTemp.setField("YD_EQP_ID", szYD_EQP_ID);
//	    	
//	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("");
//	    	intRtnVal = ydTcarSchDao.getYdTcarsch(recInTemp, rsResult, 4);
//			if(intRtnVal <= 0) {
//    			if(intRtnVal == 0) {
//    				szMsg="Y5UTcarStartWr getYdTcarsch : data not found";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//    			}else if(intRtnVal == -2) {
//    				szMsg="Y5UTcarStartWr getYdTcarsch : parameter error";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//    			}
//    			return intRtnVal = -1;
//    		}
//			
//			rsResult.absolute(1);
//			recOutTemp = JDTORecordFactory.getInstance().create();
//			recOutTemp.setRecord(rsResult.getRecord());
//			
//			szYD_TCAR_SCH_ID = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TCAR_SCH_ID");
//			recInTemp = JDTORecordFactory.getInstance().create();
//	    	recInTemp.setField("YD_TCAR_SCH_ID", szYD_TCAR_SCH_ID);
//	    	recInTemp.setField("YD_CAR_PROG_STAT", "1");
//	    	intRtnVal = ydTcarSchDao.updYdTcarsch(recInTemp, 0);
//			if(intRtnVal <= 0) {
//    			if(intRtnVal == 0) {
//    				szMsg="Y5UTcarStartWr updYdTcarsch : data not found";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//    			}else if(intRtnVal == -1) {
//    				szMsg="Y5UTcarStartWr updYdTcarsch : duplicate data,";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
//    			}else if(intRtnVal == -2) {
//    				szMsg="Y5UTcarStartWr updYdTcarsch : parameter error";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//    			}else if(intRtnVal == -3){
//    				szMsg="Y5UTcarStartWr updYdTcarsch : execution failed";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//    			}
//    			return intRtnVal = -1;
//    		}
//			
//			
//			
//			
//	    	//출발위치Bed상태 비활성화
//	    	szStkColGp = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(0,1) 
//	    	           + ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP") 
//	    	           + ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID").substring(2);
//	    	
//	    	szMsg="[공차출발실적] szStkColGp = " + szStkColGp;
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	    	
//	    	recInTemp = JDTORecordFactory.getInstance().create();
//	    	recInTemp.setField("YD_STK_COL_GP", szStkColGp);
//	    	recInTemp.setField("YD_STK_BED_NO", szStkBedNo);
//	    	recInTemp.setField("YD_STK_BED_ACT_STAT", "C");
//	    	intRtnVal = ydStkBedDao.updYdStkbed(recInTemp, 0);
//			if(intRtnVal <= 0) {
//    			if(intRtnVal == 0) {
//    				szMsg="<Y5UTcarStartWr> updYdStkbed data not found";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//    			}else if(intRtnVal == -1) {
//    				szMsg="<Y5UTcarStartWr> updYdStkbed duplicate data,";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
//    			}else if(intRtnVal == -2) {
//    				szMsg="<Y5UTcarStartWr> updYdStkbed parameter error";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//    			}else if(intRtnVal == -3){
//    				szMsg="<Y5UTcarStartWr> updYdStkbed execution failed";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//    			}
//    			return intRtnVal = -1;
//    		}
//			//적치단 비활성화
//			recInTemp = JDTORecordFactory.getInstance().create();
//			recInTemp.setField("YD_STK_COL_GP", szStkColGp);
//			recInTemp.setField("YD_STK_BED_NO", szStkBedNo);
//			recInTemp.setField("YD_STK_LYR_ACT_STAT", "C");
//			recInTemp.setField("STL_NO", "");
//			recInTemp.setField("YD_STK_LYR_MTL_STAT", "E");
//	    	
//			intRtnVal = ydStkLyrDao.updYdStklyrYdStkColGpBedNo(recInTemp);
//			if(intRtnVal <= 0) {
//				szMsg = "<CarMvHdSeEJBBean> 적치단 정보 활성화중 Error!! ";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//				throw new DAOException("<CarMvHdSeEJBBean> updYdStklyrYdStkColGp " + szMsg);
//			}
//			
//			
//	    	szSchReqGp     = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARLD_SCH_REQ_GP");
//	    	szCarldStopLoc = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARLD_STOP_LOC");
//	    	szWbookId      = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARLD_WRK_BOOK_ID");
//	    	
//	    	//상차 스케줄 요청 구분이 공차 출발이라면 
//	    	if(szSchReqGp.equals("5")) {
//	    	//상차정지위치 베드 활성화
//		    	recInTemp = JDTORecordFactory.getInstance().create();
//		    	recInTemp.setField("YD_STK_COL_GP", szCarldStopLoc);
//		    	recInTemp.setField("YD_STK_BED_NO", szStkBedNo);
//		    	recInTemp.setField("YD_STK_BED_ACT_STAT", "L");
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
////==========================================================================================                    
////              기준이기 때문에 클리어 되면 안됨
////              2009.09.25 권오창
////                
////				recInTemp.setField("YD_STK_LYR_XAXIS", "0");
////				recInTemp.setField("YD_STK_LYR_YAXIS", "0");
////				recInTemp.setField("YD_STK_LYR_ZAXIS", "0");
////==========================================================================================                    
//		    	
//				intRtnVal = ydStkLyrDao.updYdStklyrYdStkColGpBedNo2(recInTemp);
//				if(intRtnVal <= 0) {
//					szMsg = "<CarMvHdSeEJBBean> 적치단 정보 활성화중 Error!! ";
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
//	    	
// 		}catch(Exception e){
//	
//			szMsg="공차 출발실적 처리 Error:" +e.getMessage();
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//			return intRtnVal = -1;
//		}
//	
//	
//		szMsg="공차 출발실적 처리("+szMethodName+") 완료";
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		return intRtnVal = 1;
//	} //end of Y5UTcarStartWr()
//	
//	
//	
//	
//	
//	
//	
//
//	/**
//	 * 오퍼레이션명 : 공차도착실적
//	 *  
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param msgRecord, recTcarSch
//	 * @return intRtnVal
//	 * @throws JDTOException
//	 */
//	public int Y5UTcarStopWr(JDTORecord msgRecord, JDTORecord recTcarSch)throws JDTOException  {
//		YdStkBedDao ydStkBedDao = new YdStkBedDao();
//		YdEqpDao ydEqpDao = new YdEqpDao();
//		YdTcarSchDao ydTcarSchDao = new YdTcarSchDao();
//		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
//		
//		JDTORecord    recInTemp          = null;
//		JDTORecord    recOutTemp         = null;
//		JDTORecordSet rsResult           = null; 
//		
//	    
//	    String szMsg              		= "";
//	    String szMethodName       		= "Y5UTcarStopWr";
//	    String szOperationName          = "공차도착실적";
//	    
//	    
//	    String szQuery                  = "";
//
//	    int intRtnVal                   = 0;
//	    
//	    String szStkColGp               = "";
//	    String szStkBedNo               = "01";
//	    String szSchReqGp               = "";
//	    String szCarldStopLoc           = "";
//	    String szWbookId                = "";
//	    String szYD_EQP_ID              = "";
//	    String szYD_TCAR_SCH_ID         = "";
//	    String szYD_BAY_GP              = "";
//	    String szYD_TO_BAY              = "";
//	    try{
//	    	
//			/*
//			YD_EQP_ID 야드설비ID
//			YD_MOVE_GP 야드대차이동구분
//			YD_TCAR_MOVE_DIR 야드대차이동방향
//			YD_BAY_GP 야드동구분1 현재동
//			YD_TCAR_AIM_BAY 야드동구분2 목적동
//			*/
//	    	//스케줄 요청 구분판단
//	    	szSchReqGp     = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARLD_SCH_REQ_GP");
//	    	szCarldStopLoc = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARLD_STOP_LOC");
//	    	szWbookId      = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARLD_WRK_BOOK_ID");
//	    	szYD_BAY_GP    = ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP");
//	    	szYD_EQP_ID    = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
//	    	szYD_TO_BAY    = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_TO_BAY");
//	    	
//	    	ydUtils.displayRecord(szOperationName, msgRecord);
//	    	
//	    	
//	    	recInTemp = JDTORecordFactory.getInstance().create();
//	    	recInTemp.setField("YD_EQP_ID", szYD_EQP_ID);
//	    	recInTemp.setField("YD_CURR_BAY_GP", szYD_BAY_GP);
//	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("");
//	    	intRtnVal = ydTcarSchDao.getYdTcarsch(recInTemp, rsResult, 4);
//			if(intRtnVal <= 0) {
//    			if(intRtnVal == 0) {
//    				szMsg="Y5UTcarStartWr getYdTcarsch : data not found";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//    			}else if(intRtnVal == -2) {
//    				szMsg="Y5UTcarStartWr getYdTcarsch : parameter error";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//    			}
//    			return intRtnVal = -1;
//    		}
//			
//			rsResult.absolute(1);
//			recOutTemp = JDTORecordFactory.getInstance().create();
//			recOutTemp.setRecord(rsResult.getRecord());
//			
//			szYD_TCAR_SCH_ID = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TCAR_SCH_ID");
//			recInTemp = JDTORecordFactory.getInstance().create();
//	    	recInTemp.setField("YD_TCAR_SCH_ID", szYD_TCAR_SCH_ID);
//	    	
//	    	//20090702 김진욱 대차진행상태값 등록
//	    	if(szWbookId.equals("")){
//	    		//작업예약이 없는 이유는 작업자가 지정한 동으로 이동했거나 홈동으로 이동한 경우이기때문에...상차도착으로 처리하지 않는다.
//	    		recInTemp.setField("YD_CAR_PROG_STAT", "0");
//	    	}else{
//	    		recInTemp.setField("YD_CAR_PROG_STAT", "2");
//	    	}
//	    	
//	    	intRtnVal = ydTcarSchDao.updYdTcarsch(recInTemp, 0);
//			if(intRtnVal <= 0) {
//    			if(intRtnVal == 0) {
//    				szMsg="Y5UTcarStartWr updYdTcarsch : data not found";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//    			}else if(intRtnVal == -1) {
//    				szMsg="Y5UTcarStartWr updYdTcarsch : duplicate data,";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
//    			}else if(intRtnVal == -2) {
//    				szMsg="Y5UTcarStartWr updYdTcarsch : parameter error";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//    			}else if(intRtnVal == -3){
//    				szMsg="Y5UTcarStartWr updYdTcarsch : execution failed";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//    			}
//    			return intRtnVal = -1;
//    		}
//	    	
//	    	
//	    	//상차 스케줄 요청 구분이 공차 도착이라면 
//	    	if(szSchReqGp.equals("6")) {
//	    	//상차정지위치 베드 활성화
//		    	recInTemp = JDTORecordFactory.getInstance().create();
//		    	recInTemp.setField("YD_STK_COL_GP", szCarldStopLoc);
//		    	recInTemp.setField("YD_STK_BED_NO", szStkBedNo);
//		    	recInTemp.setField("YD_STK_BED_ACT_STAT", "L");
//		    	intRtnVal = ydStkBedDao.updYdStkbed(recInTemp, 0);
//				if(intRtnVal <= 0) {
//	    			if(intRtnVal == 0) {
//	    				szMsg="<Y5UTcarStopWr> ydStkBedDao data not found";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//	    			}else if(intRtnVal == -1) {
//	    				szMsg="<Y5UTcarStopWr> ydStkBedDao duplicate data,";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
//	    			}else if(intRtnVal == -2) {
//	    				szMsg="<Y5UTcarStopWr> ydStkBedDao parameter error";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	    			}else if(intRtnVal == -3){
//	    				szMsg="<Y5UTcarStopWr> ydStkBedDao execution failed";
//	    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	    			}
//	    			return intRtnVal = -1;
//	    		}
//				
//		    	//상차정지위치 단정보 Clear, 
//				recInTemp = JDTORecordFactory.getInstance().create();
//				recInTemp.setField("YD_STK_COL_GP", szCarldStopLoc);
//				recInTemp.setField("YD_STK_BED_NO", szStkBedNo);
//				recInTemp.setField("STL_NO", "");
//				recInTemp.setField("MODIFIER", "SYSTEM");
//				recInTemp.setField("YD_STK_LYR_ACT_STAT", "E");
//				recInTemp.setField("YD_STK_LYR_MTL_STAT", "E");
//				
////==========================================================================================                    
////              기준이기 때문에 클리어 되면 안됨
////              2009.09.25 권오창
////                
////				recInTemp.setField("YD_STK_LYR_XAXIS", "0");
////				recInTemp.setField("YD_STK_LYR_YAXIS", "0");
////				recInTemp.setField("YD_STK_LYR_ZAXIS", "0");
////==========================================================================================                    
//		    	
//				intRtnVal = ydStkLyrDao.updYdStklyrYdStkColGpBedNo2(recInTemp);
//				if(intRtnVal <= 0) {
//					szMsg = "<CarMvHdSeEJBBean> 적치단 정보 활성화중 Error!! ";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//					throw new DAOException("<CarMvHdSeEJBBean> updYdStklyrYdStkColGp " + szMsg);
//				}
//		    
//		    	
//		    	//상차 크레인스케줄호출
//		    	if(!szWbookId.equals("")) {
//			    	recInTemp = JDTORecordFactory.getInstance().create();
//			    	recInTemp.setField("YD_WBOOK_ID", szWbookId);
//			    	intRtnVal = this.Y5CallCrnSch(recInTemp);	
//		    	}
//
//	    	}
// 		}catch(Exception e){
//	
//			szMsg="공차 도착실적 처리 Error:" +e.getMessage();
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//			return intRtnVal = -1;
//		}
//	
//	
//		szMsg="공차 도착실적 처리("+szMethodName+") 완료";
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		return intRtnVal = 1;
//	} //end of Y5UTcarStopWr()
//	
//	
//	
//	
//	
//	
//
//	/**
//	 * 오퍼레이션명 : 영차출발실적
//	 *  
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param msgRecord, recTcarSch
//	 * @return intRtnVal
//	 * @throws JDTOException
//	 */
//	public int Y5LTcarStartWr(JDTORecord msgRecord, JDTORecord recTcarSch)throws JDTOException  {
//		YdStkBedDao ydStkBedDao = new YdStkBedDao();
//		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
//		YdWrkbookMtlDao  ydWrkbookMtlDao  = new YdWrkbookMtlDao();
//		YdTcarSchDao ydTcarSchDao = new YdTcarSchDao();
//		
//		JDTORecordSet rsResult          = null;
//		JDTORecord    recInTemp         = null;
//		JDTORecord    recOutTemp        = null;
//
//	    
//	    String szMsg              		= "";
//	    String szMethodName       		= "Y5LTcarStartWr";
//	    
//	    String szSchReqGp               = "";
//	    String szCarldStopLoc           = "";
//	    String szCarudStopLoc           = "";
//	    String szStkColGp               = "";
//	    String szStkBedNo               = "01";
//	    String szWbookId                = "";
//	    String szYD_EQP_ID              = "";
//	    String szYD_TCAR_SCH_ID         = "";
//	    
//	    String szQuery                  = "";
//
//	    int intRtnVal                   = 0;
//
//	    
//	    try{
//	    	//대차스케줄에 대차진행상태 update
//	    	szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
//	    	recInTemp = JDTORecordFactory.getInstance().create();
//	    	recInTemp.setField("YD_EQP_ID", szYD_EQP_ID);
//	    	
//	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("");
//	    	intRtnVal = ydTcarSchDao.getYdTcarsch(recInTemp, rsResult, 4);
//			if(intRtnVal <= 0) {
//    			if(intRtnVal == 0) {
//    				szMsg="Y5UTcarStartWr getYdTcarsch : data not found";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//    			}else if(intRtnVal == -2) {
//    				szMsg="Y5UTcarStartWr getYdTcarsch : parameter error";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//    			}
//    			return intRtnVal = -1;
//    		}
//			
//			rsResult.absolute(1);
//			recOutTemp = JDTORecordFactory.getInstance().create();
//			recOutTemp.setRecord(rsResult.getRecord());
//			
//			szYD_TCAR_SCH_ID = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TCAR_SCH_ID");
//			recInTemp = JDTORecordFactory.getInstance().create();
//	    	recInTemp.setField("YD_TCAR_SCH_ID", szYD_TCAR_SCH_ID);
//	    	recInTemp.setField("YD_CAR_PROG_STAT", "A");
//	    	intRtnVal = ydTcarSchDao.updYdTcarsch(recInTemp, 0);
//			if(intRtnVal <= 0) {
//    			if(intRtnVal == 0) {
//    				szMsg="Y5UTcarStartWr updYdTcarsch : data not found";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//    			}else if(intRtnVal == -1) {
//    				szMsg="Y5UTcarStartWr updYdTcarsch : duplicate data,";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
//    			}else if(intRtnVal == -2) {
//    				szMsg="Y5UTcarStartWr updYdTcarsch : parameter error";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//    			}else if(intRtnVal == -3){
//    				szMsg="Y5UTcarStartWr updYdTcarsch : execution failed";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//    			}
//    			return intRtnVal = -1;
//    		}
//	    	
//	    	
//	    	
//	    	
//	    	//상차 정지위치의 베드상태 비활성화하고 적치단정보 Clear
//	    	//스케줄 요청 구분판단
//	    	szSchReqGp     = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARUD_SCH_REQ_GP");
//	    	szCarldStopLoc = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARLD_STOP_LOC");
//	    	szCarudStopLoc = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARUD_STOP_LOC");
//	    	
//	    	recInTemp = JDTORecordFactory.getInstance().create();
//	    	recInTemp.setField("YD_STK_COL_GP", szCarldStopLoc);
//	    	recInTemp.setField("YD_STK_BED_NO", szStkBedNo);
//	    	recInTemp.setField("REG_DDTT", null);
//	    	recInTemp.setField("YD_STK_BED_ACT_STAT", "C");
//	    	intRtnVal = ydStkBedDao.updYdStkbed(recInTemp, 0);
//			if(intRtnVal <= 0) {
//    			if(intRtnVal == 0) {
//    				szMsg="<Y5LTcarStartWr> updYdStkbed data not found";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//    			}else if(intRtnVal == -1) {
//    				szMsg="<Y5LTcarStartWr> updYdStkbed duplicate data,";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
//    			}else if(intRtnVal == -2) {
//    				szMsg="<Y5LTcarStartWr> updYdStkbed parameter error";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//    			}else if(intRtnVal == -3){
//    				szMsg="<Y5LTcarStartWr> updYdStkbed execution failed";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//    			}
//    			return intRtnVal = -1;
//    		}
//			
//			//상차정지위치 단정보 Clear, 적치단활성상태를 비활성화로...
//			recInTemp = JDTORecordFactory.getInstance().create();
//			recInTemp.setField("YD_STK_COL_GP", szCarldStopLoc);
//			recInTemp.setField("YD_STK_BED_NO", szStkBedNo);
//			recInTemp.setField("STL_NO", "");
//			recInTemp.setField("MODIFIER", "SYSTEM");
//			recInTemp.setField("YD_STK_LYR_ACT_STAT", "C");
//			recInTemp.setField("YD_STK_LYR_MTL_STAT", "E");
//			
////==========================================================================================                    
////          기준이기 때문에 클리어 되면 안됨
////          2009.09.25 권오창
////            
////			recInTemp.setField("YD_STK_LYR_XAXIS", "0");
////			recInTemp.setField("YD_STK_LYR_YAXIS", "0");
////			recInTemp.setField("YD_STK_LYR_ZAXIS", "0");
////==========================================================================================                    
//	    	
//			intRtnVal = ydStkLyrDao.updYdStklyrYdStkColGpBedNo2(recInTemp);
//			if(intRtnVal <= 0) {
//				szMsg = "<CarMvHdSeEJBBean> 적치단 정보 활성화중 Error!! ";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//				throw new DAOException("<CarMvHdSeEJBBean> updYdStklyrYdStkColGp " + szMsg);
//			}
//	    	
//	    	
//	    	
//	    	//스케줄 요청 구분이 영차 출발이라면
//	    	if(szSchReqGp.equals("2")) {
//		    	//하차정지위치 Bed상태 활성화
//		    	recInTemp = JDTORecordFactory.getInstance().create();
//		    	recInTemp.setField("YD_STK_COL_GP", szCarudStopLoc);
//		    	recInTemp.setField("YD_STK_BED_NO", szStkBedNo);
//		    	recInTemp.setField("YD_STK_BED_ACT_STAT", "L");
//		    	intRtnVal = ydStkBedDao.updYdStkbed(recInTemp, 0);
//		    	
//		    	//적치단에 재료정보 Update
//		        //하차작업에약ID를 조회해서 작업예약재료 Table를 조회한다.
//		    	szWbookId = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARUD_WRK_BOOK_ID");
//		    	recInTemp = JDTORecordFactory.getInstance().create();
//		    	recInTemp.setField("YD_WBOOK_ID", szWbookId);
//		    	rsResult  = JDTORecordFactory.getInstance().createRecordSet("Temp");
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
//		    		recOutTemp.setField("YD_STK_LYR_MTL_STAT", "E");
//					recInTemp.setField("YD_STK_LYR_ACT_STAT",  "E");
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
//		    	szStkColGp = ydDaoUtils.paraRecChkNull(recInTemp, "YD_STK_COL_GP");
//				szStkBedNo = ydDaoUtils.paraRecChkNull(recInTemp, "YD_STK_BED_NO");
//
//		    	//하차크레인 스케줄 호출
//		    	recInTemp = JDTORecordFactory.getInstance().create();
//		    	recInTemp.setField("YD_WBOOK_ID", szWbookId);
//		    	intRtnVal = this.Y5CallCrnSch(recInTemp);	
//		    	
//	    	}
//
//	    	
// 		}catch(Exception e){
//	
//			szMsg="영차 출발실적 처리 Error:" +e.getMessage();
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//			return intRtnVal = -1;
//		}
//	
//	
//		szMsg="영차 출발실적 처리("+szMethodName+") 완료";
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		return intRtnVal = 1;
//	} //end of Y5LTcarStartWr()
//	
//	
//	
//	
//
//	
//
//	/**
//	 * 오퍼레이션명 : 영차도착실적
//	 *  
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param msgRecord, recTcarSch
//	 * @return intRtnVal
//	 * @throws JDTOException
//	 */
//	public int Y5LTcarStopWr(JDTORecord msgRecord, JDTORecord recTcarSch)throws JDTOException  {
//		YdStkBedDao ydStkBedDao = new YdStkBedDao();
//		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
//		YdEqpDao ydEqpDao = new YdEqpDao();
//		YdWrkbookMtlDao  ydWrkbookMtlDao  = new YdWrkbookMtlDao();
//		YdTcarSchDao ydTcarSchDao = new YdTcarSchDao();
//		
//		
//		JDTORecordSet rsResult          = null;
//		JDTORecord    recInTemp         = null;
//		JDTORecord    recOutTemp        = null;
//
//	    
//	    String szMsg              		= "";
//	    String szMethodName       		= "Y5LTcarStopWr";
//	    
//
//	    int intRtnVal                   = 0;
//
//	    String szSchReqGp               = "";
//	    String szCarldStopLoc           = "";
//	    String szCarudStopLoc           = "";
//	    String szStkBedNo               = "01";
//	    String szWbookId                = "";
//	    String szYD_EQP_ID              = "";
//	    String szYD_TCAR_SCH_ID         = "";
//	    String szYD_BAY_GP              = "";
//	    
//	    try{
//	    	
//	    	szSchReqGp     = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARUD_SCH_REQ_GP");
//	    	szCarldStopLoc = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARLD_STOP_LOC");
//	    	szCarudStopLoc = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARUD_STOP_LOC");
//	    	
//	    	//대차스케줄에 대차진행상태 update
//	    	szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
//	    	recInTemp = JDTORecordFactory.getInstance().create();
//	    	recInTemp.setField("YD_EQP_ID", szYD_EQP_ID);
////	    	recInTemp.setField("YD_CURR_BAY_GP", szYD_BAY_GP);	    	
//	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("");
//	    	intRtnVal = ydTcarSchDao.getYdTcarsch(recInTemp, rsResult, 4);
//			if(intRtnVal <= 0) {
//    			if(intRtnVal == 0) {
//    				szMsg="Y5UTcarStartWr getYdTcarsch : data not found";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//    			}else if(intRtnVal == -2) {
//    				szMsg="Y5UTcarStartWr getYdTcarsch : parameter error";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//    			}
//    			return intRtnVal = -1;
//    		}
//			
//			rsResult.absolute(1);
//			recOutTemp = JDTORecordFactory.getInstance().create();
//			recOutTemp.setRecord(rsResult.getRecord());
//			
//			szYD_TCAR_SCH_ID = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TCAR_SCH_ID");
//			recInTemp = JDTORecordFactory.getInstance().create();
//	    	recInTemp.setField("YD_TCAR_SCH_ID", szYD_TCAR_SCH_ID);
//	    	recInTemp.setField("YD_CAR_PROG_STAT", "B");
//	    	recInTemp.setField("YD_CURR_BAY_GP", szCarudStopLoc.substring(1,2));
//	    	intRtnVal = ydTcarSchDao.updYdTcarsch(recInTemp, 0);
//			if(intRtnVal <= 0) {
//    			if(intRtnVal == 0) {
//    				szMsg="Y5UTcarStartWr updYdTcarsch : data not found";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//    			}else if(intRtnVal == -1) {
//    				szMsg="Y5UTcarStartWr updYdTcarsch : duplicate data,";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
//    			}else if(intRtnVal == -2) {
//    				szMsg="Y5UTcarStartWr updYdTcarsch : parameter error";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//    			}else if(intRtnVal == -3){
//    				szMsg="Y5UTcarStartWr updYdTcarsch : execution failed";
//    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//    			}
//    			return intRtnVal = -1;
//    		}
//	    	
//	    	
//	    	
//
//	    	
//
//			
//	    	
//	    	//스케줄 요청 구분이 영차 출발이라면
//	    	if(szSchReqGp.equals("3")) {
//		    	//하차정지위치 Bed상태 활성화
//		    	recInTemp = JDTORecordFactory.getInstance().create();
//		    	recInTemp.setField("YD_STK_COL_GP", szCarudStopLoc);
//		    	recInTemp.setField("YD_STK_BED_NO", szStkBedNo);
//		    	recInTemp.setField("YD_STK_BED_ACT_STAT", "L");
//		    	intRtnVal = ydStkBedDao.updYdStkbed(recInTemp, 0);
//		    	
//		    	//적치단에 재료정보 Update
//		        //하차작업에약ID를 조회해서 작업예약재료 Table를 조회한다.
//		    	szWbookId = ydDaoUtils.paraRecChkNull(recTcarSch, "YD_CARUD_WRK_BOOK_ID");
//		    	recInTemp = JDTORecordFactory.getInstance().create();
//		    	recInTemp.setField("YD_WBOOK_ID", szWbookId);
//		    	rsResult  = JDTORecordFactory.getInstance().createRecordSet("Temp");
//		    	intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recInTemp, rsResult, 1);
//				if(intRtnVal <= 0) {
//					if(intRtnVal == 0) {
//						szMsg="<Y5UTcarStartWr> getYdWrkbookmtl data not found";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//					}else if(intRtnVal == -2) {
//						szMsg="<Y5UTcarStartWr> getYdWrkbookmtl parameter error";
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
//		    		recOutTemp.setField("YD_STK_COL_GP",       szCarudStopLoc);
//		    		recOutTemp.setField("YD_STK_BED_NO",       ydDaoUtils.paraRecChkNull(recInTemp, "YD_STK_BED_NO"));
//		    		recOutTemp.setField("YD_STK_LYR_NO",       ydDaoUtils.paraRecChkNull(recInTemp, "YD_STK_LYR_NO"));
//		    		recOutTemp.setField("STL_NO",              ydDaoUtils.paraRecChkNull(recInTemp, "STL_NO"));
//		    		recOutTemp.setField("YD_STK_LYR_MTL_STAT", "C");
//		    		recOutTemp.setField("YD_STK_LYR_ACT_STAT", "E");
//		    		intRtnVal = ydStkLyrDao.updYdStklyr(recOutTemp, 0);
//					if(intRtnVal <= 0) {
//		    			if(intRtnVal == 0) {
//		    				szMsg="<Y5UTcarStartWr> updYdStklyr data not found";
//		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//		    			}else if(intRtnVal == -1) {
//		    				szMsg="<Y5UTcarStartWr> updYdStklyr duplicate data,";
//		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
//		    			}else if(intRtnVal == -2) {
//		    				szMsg="<Y5UTcarStartWr> updYdStklyr parameter error";
//		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		    			}else if(intRtnVal == -3){
//		    				szMsg="<Y5UTcarStartWr> updYdStklyr execution failed";
//		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		    			}
//		    			return intRtnVal = -1;
//		    		}
//					
//
//		    	}
//		    	
//		    	//하차크레인 스케줄 호출
//		    	recInTemp = JDTORecordFactory.getInstance().create();
//		    	recInTemp.setField("YD_WBOOK_ID", szWbookId);
//		    	intRtnVal = this.Y5CallCrnSch(recInTemp);	
//	    	}
//	    		
// 		}catch(Exception e){
//	
//			szMsg="영차 도착실적 처리 Error:" +e.getMessage();
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//			return intRtnVal = -1;
//		}
//	
//	
//		szMsg="영차 도착실적 처리("+szMethodName+") 완료";
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		return intRtnVal = 1;
//	} //end of Y5LTcarStopWr()
//	
//	
//	
//	
//	
//	
//	
//	/**
//	 * 오퍼레이션명 : 크레인스케줄호출
//	 *  
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param msgRecord
//	 * @return intRtnVal
//	 * @throws JDTOException
//	 */
//	public int Y5CallCrnSch(JDTORecord msgRecord)throws JDTOException  {
//		YdWrkbookDao     ydWrkbookDao     = new YdWrkbookDao();
//		YdSchRuleDao     ydSchRuleDao     = new YdSchRuleDao();
//		YdDelegate       ydDelegate 	  = new YdDelegate();
//		
//		JDTORecordSet rsResult          = null;
//		JDTORecord    recInTemp         = null;
//
//	    
//	    String szMsg              		= "";
//	    String szMethodName       		= "Y5CallCrnSch";
//
//	    int intRtnVal                   = 0;
//
//	    String szWbookId                = "";
//	    String szSchCd                  = "";
//	    String szEqpId                  = "";
//	    
//	    try{
//	    	//작업예약ID
//	    	szWbookId = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");
//	    	
//	    	//작업예약 id로 작업예약Table를 조회한다.
//	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
//	    	intRtnVal = ydWrkbookDao.getYdWrkbook(msgRecord, rsResult, 0);
//			if(intRtnVal <= 0) {
//				if(intRtnVal == 0) {
//					szMsg="<Y5CallCrnSch> getYdWrkbook data not found";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
//				}else if(intRtnVal == -2) {
//					szMsg="<Y5CallCrnSch> getYdWrkbook parameter error";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//				}
//				return intRtnVal = -1;
//			}
//	    	
//	    	rsResult.absolute(1);
//	    	recInTemp = JDTORecordFactory.getInstance().create();
//	    	recInTemp.setRecord(rsResult.getRecord());
//	    	
//	    	//스케줄코드
//	    	szSchCd = ydDaoUtils.paraRecChkNull(recInTemp, "YD_SCH_CD");
//	    	
//	    	/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//			/*     스케쥴기준 조회 - 스케쥴 금지유무 판단, 작업크레인, 대체크레인 조회	- 2009.04.10 임춘수	*/
//			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//	    	recInTemp = JDTORecordFactory.getInstance().create();
//			intRtnVal = YdCommonUtils.getCrnInfoByCrnSchRule(szSchCd, recInTemp);
//			if( intRtnVal < 0 ) {
//				szMsg="스케줄 기준 조회 Error Code : " + intRtnVal;
//    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//				return intRtnVal;
//			}
//			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//	    	
//	    	//크레인설비ID
//	    	szEqpId = ydDaoUtils.paraRecChkNull(recInTemp, "YD_WRK_CRN");
//	    	
//
//	    	recInTemp = JDTORecordFactory.getInstance().create();
//	    	//크레인스케줄MAIN호출 TC : YDYDJ500, 스케줄코드 설비ID
//	    	if(szEqpId.substring(0,1).equals("A")) {
//		    	recInTemp.setField("MSG_ID",    "YDYDJ500");
//		    	recInTemp.setField("YD_SCH_CD", szSchCd);
//		    	recInTemp.setField("YD_EQP_ID", szEqpId);
//	    	}else if(szEqpId.substring(0,1).equals("S")) {
//		    	recInTemp.setField("MSG_ID",    "YDYDJ512");
//		    	recInTemp.setField("YD_SCH_CD", szSchCd);
//		    	recInTemp.setField("YD_EQP_ID", szEqpId);
//	    	}else if(szEqpId.substring(0,1).equals("D")) {
//		    	recInTemp.setField("MSG_ID",    "YDYDJ503");
//		    	recInTemp.setField("YD_SCH_CD", szSchCd);
//		    	recInTemp.setField("YD_EQP_ID", szEqpId);
//	    	}else if(szEqpId.substring(0,1).equals("K")) {
//		    	recInTemp.setField("MSG_ID",    "YDYDJ506");
//		    	recInTemp.setField("YD_SCH_CD", szSchCd);
//		    	recInTemp.setField("YD_EQP_ID", szEqpId);
//	    	}else if(szEqpId.substring(0,1).equals("H") || szEqpId.substring(0,1).equals("J")) {
//		    	recInTemp.setField("MSG_ID",    "YDYDJ509");
//		    	recInTemp.setField("YD_SCH_CD", szSchCd);
//		    	recInTemp.setField("YD_EQP_ID", szEqpId);
//	    	}
//
//	    	//서버 메세지 전송 메소드 크레인 스케줄 호출
//	    	ydDelegate.sendMsg(recInTemp);
//
//	    	
// 		}catch(Exception e){
//	
//			szMsg="크레인스케줄 호출 Error:" +e.getMessage();
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//			return intRtnVal = -1;
//		}
//	
//	
//		szMsg="크레인스케줄 호출("+szMethodName+") 완료";
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		return intRtnVal = 1;
//	} //end of Y5CallCrnSch()
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	

  //---------------------------------------------------------------------------	
} // end of class TcarMvHdSeEJBBean
