package com.inisteel.cim.yd.ydSch.TransEqpSch;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.dao.ydEqpDao.YdEqpDao;
import com.inisteel.cim.yd.common.dao.ydSchRuleDao.YdSchRuleDao;
import com.inisteel.cim.yd.common.dao.ydTcarFtmvMtlDao.YdTcarFtmvMtlDao;
import com.inisteel.cim.yd.common.dao.ydTcarSchDao.YdTcarSchDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookDao.YdWrkbookDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookMtlDao.YdWrkbookMtlDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.DaoManager;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDBAssist;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdTcConst;
import com.inisteel.cim.yd.common.util.YdUtils;

/**
 * 대차명령선택 Session EJB
 *
 * @ejb.bean name="TransEqpSchSeEJB" jndi-name="TransEqpSchSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class TransEqpSchSeEJBBean extends BaseSessionBean {
	
	// Session Name
	private String szSessionName=getClass().getName();
	
	private YdUtils ydUtils =new YdUtils();
	
	private YdDaoUtils ydDaoUtils =new YdDaoUtils();
	
	private YdDelegate ydDelegate =new YdDelegate();
	
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
	 * 오퍼레이션명 : C연주 대차 스케줄 
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return Integer
	 * @throws JDTOException
	 */
	public Integer procY1TcarSch(JDTORecord msgRecord)throws DAOException  {
		
	    int intRtnVal 					= 0 ;
	    
	    String szMsg              		= "";
	    String szMethodName       		= "procY1TcarSch";
	    String szOperationName			= "C연주대차스케줄";
	    
	    //상하차 구분
	    String szLdUdGp					= "";
	    
        String szRcvTcCode=ydUtils.getTcCode(msgRecord);
        if(szRcvTcCode==null || szRcvTcCode.equals("")){
        	szMsg="["+szOperationName+"] TC Code Error ("+szRcvTcCode+")";
        	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
        	return YdConstant.RETN_INT_TC_ERROR;
        }
        
        if(bDebugFlag){
            szMsg="["+szOperationName+"] [DEBUG] 전문수신 : TCCODE=" +szRcvTcCode ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }
	    
	    try{
	    	szMsg="["+szOperationName+"] ------------------- 메소드 시작 -------------------";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            
            szLdUdGp = ydDaoUtils.paraRecChkNull(msgRecord, "YD_LD_UD_GP");
	    	
	    	//------------------------------------------------------------------------------------------
    		//권상 실적 처리중 호출한 경우 - 하차 시
	    	//------------------------------------------------------------------------------------------
    		if( szLdUdGp.equals("U") ) {
    			
    			szMsg="["+szOperationName+"] 권상실적 처리 시 호출 - 대차하차스케줄 모듈 시작";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			
    			//대차 하차스케줄 호출
    			intRtnVal = this.Y1LdTcarSch(msgRecord);
    			
    			szMsg="["+szOperationName+"] 권상실적 처리 시 호출 - 대차하차스케줄 모듈 완료 - 반환값 : " + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			
    		//------------------------------------------------------------------------------------------
    		//권하 실적 처리중 호출한 경우 - 상차 시
    		//------------------------------------------------------------------------------------------
    		}else if( szLdUdGp.equals("L") ){
    			
    			szMsg="["+szOperationName+"] 권하실적 처리 시 호출 - 대차상차스케줄 모듈 시작";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			
    			//대차 상차 스케줄 호출
    			intRtnVal = this.Y1UdTcarSch(msgRecord);
    			
    			szMsg="["+szOperationName+"] 권하실적 처리 시 호출 - 대차상차스케줄 모듈 완료 - 반환값 : " + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			
    		}else{
    			//------------------------------------------------------------------------------------------
    			//화면에서 송신한 경우 - 공대차출발지시
    			//------------------------------------------------------------------------------------------
    			szMsg="["+szOperationName+"] 화면에서 호출 - 공대차출발스케줄 모듈 시작";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			
	    		intRtnVal = this.Y1L2TcarSch(msgRecord);
	    		
	    		szMsg="["+szOperationName+"] 화면에서 호출 - 공대차출발스케줄 모듈 완료 - 반환값 : " + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		
			}
		}catch(Exception e){
	
			szMsg="["+szOperationName+"] 예외 발생 : " +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			m_ctx.setRollbackOnly();
			throw new DAOException(szMsg);
		}
	
		szMsg="["+szOperationName+"] ------------------- 메소드 끝 -------------------";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        
		return  YdConstant.RETN_INT_SUCCESS;
	} //end of procY1TcarSch()
	
	/**
	 * 오퍼레이션명 : 대차 하차 스케줄(공대차 출발지시)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public int 	Y1L2TcarSch(JDTORecord msgRecord)throws DAOException  {
		
		//동간이적요구시 화면에서  송신하는 경우
		YdTcarSchDao     ydTcarSchDao     = new YdTcarSchDao();
		YdEqpDao         ydEqpDao         = new YdEqpDao();
		YdSchRuleDao     ydSchRuleDao     = new YdSchRuleDao();
		YdWrkbookDao     ydWrkbookDao     = new YdWrkbookDao();
		
		JDTORecordSet rsResult          = null;
		JDTORecordSet rsResult1         = null;
		JDTORecord    recInTemp         = null;
		JDTORecord    recOutTemp        = null;
		JDTORecord    recTcar           = null;
		JDTORecord    recWbook          = null;
	    int intRtnVal 					= 0 ;
	    
	    String szMsg              		= "";
	    String szMethodName       		= "Y1L2TcarSch";
	    String szOperationName			= "공대차스케줄";
	    
	    String szTcarEqpId              = "";
	    String szWbookId                = "";
	    String szYdGp					= "";
	    String szYdBayGp				= "";
	    //String szYdAimYdGp			= "";
	    String szYdAimBayGp				= "";
	    String szYdWrkPlanTcar			= "";
	    String szYdSchCd                = "";
	    String szYD_TCAR_CURR_BAY_GP    = "";
	    String szYD_TCAR_SCH_ID         = "";
	    String szYD_TO_BAY              = "";
	    String szYD_CARLD_WRK_BOOK_ID   = "";
	    String szYD_CARLD_STOP_LOC      = "";					//상차정지위치
	    String szYD_CARUD_STOP_LOC		= null;					//하차정지위치
	    String szYD_WBOOK_ID            = "";
	    String szRegister				= "SYSTEM";
	    String szYD_HOME_BAY_GP         = "";
	    String[] szTCarRule				= null;
	    String szUSAGE_YN				= "";
	    String szWORK_GP				= "";
	    String szYD_CARLD_BAY_GP		= "";
	    String szYD_CARUD_BAY_GP		= "";
	    boolean bAfterQuery				= false;
	    
	    try{
	    	szMsg="["+szOperationName+"] ------------------- 메소드 시작 -------------------";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            
	    	szTcarEqpId 		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");	//대차설비ID
	    	szYD_TO_BAY 		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_TO_BAY");	//공대차출발 시 사용자가 지정한 목표동(값 없을수 있슴)
	    	szYD_WBOOK_ID 		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");	//작업예약ID(값 없을수 있슴)
	    	
	    	//-------------------------------------------------------------------------------------
	    	//	대차설비ID로 설비테이블을 조회한다.
	    	//-------------------------------------------------------------------------------------
	    	szMsg="["+szOperationName+"] 대차설비ID(" + szTcarEqpId + ")로 설비테이블 조회 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
    		rsResult1 = JDTORecordFactory.getInstance().createRecordSet("");
    		
    		recInTemp = JDTORecordFactory.getInstance().create();
    		recInTemp.setField("YD_EQP_ID", 			szTcarEqpId);
    		recInTemp.setField("YD_TCAR_SCH_ID", 		szYD_TCAR_SCH_ID);

    		intRtnVal = ydEqpDao.getYdEqp(recInTemp, rsResult1, 0);
    		
    		szMsg="["+szOperationName+"] 대차설비ID(" + szTcarEqpId + ")로 설비테이블 조회 완료 - 반환값 : " + intRtnVal;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			if(intRtnVal <= 0) {
    			szMsg="["+szOperationName+"] 대차설비ID(" + szTcarEqpId + ")로 설비테이블 조회 시 오류발생 - 반환값 : " + intRtnVal;
    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			return intRtnVal = -1;
			}
			
			rsResult1.absolute(1);
    		recOutTemp = JDTORecordFactory.getInstance().create();
    		recOutTemp.setRecord(rsResult1.getRecord());
    		
    		szYD_TCAR_CURR_BAY_GP 	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CURR_BAY_GP");	//현재동
	    	szYD_HOME_BAY_GP 		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_HOME_BAY_GP");	//홈동
    		//-------------------------------------------------------------------------------------
	    	
	    	if(!szYD_WBOOK_ID.equals("")) {
	    		
	    		//-------------------------------------------------------------------------------------
	    		//	파라미터로 전달된 작업예약ID가 존재하면 작업예약을 조회한다.
	    		//-------------------------------------------------------------------------------------
	    		szMsg="["+szOperationName+"] 파라미터로 전달된 작업예약[" + szYD_WBOOK_ID + "]을 조회 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		
				rsResult  			= JDTORecordFactory.getInstance().createRecordSet("");
	    		recInTemp 			= JDTORecordFactory.getInstance().create();
	    		
	    		recInTemp.setField("YD_WBOOK_ID", 			szYD_WBOOK_ID);
	    		
	    		intRtnVal = ydWrkbookDao.getYdWrkbook(recInTemp, rsResult, 0);
	    		if(intRtnVal <= 0 ){
	    			szMsg= "["+szOperationName+"] 파라미터로 전달된 작업예약[" + szYD_WBOOK_ID + "]을 조회 시 오류발생 - 반환값 : " + intRtnVal;
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			return intRtnVal = -1;
	    		}
	    		
	    		szMsg="["+szOperationName+"] 파라미터로 전달된 작업예약[" + szYD_WBOOK_ID + "]을 조회 완료 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		
	    		rsResult.absolute(1);
	    		recWbook = JDTORecordFactory.getInstance().create();
	    		recWbook.setRecord(rsResult.getRecord());
	    		
	    		szYdBayGp       = ydDaoUtils.paraRecChkNull(recWbook, "YD_BAY_GP");						//동구분
	    		
		    	szYdGp          = ydDaoUtils.paraRecChkNull(recWbook, "YD_GP");							//야드구분
		    	szYdAimBayGp    = ydDaoUtils.paraRecChkNull(recWbook, "YD_AIM_BAY_GP");					//목표동구분
		    	szYdWrkPlanTcar = ydDaoUtils.paraRecChkNull(recWbook, "YD_WRK_PLAN_TCAR");				//작업계획대차
		    	szYdSchCd       = ydDaoUtils.paraRecChkNull(recWbook, "YD_SCH_CD");						//스케줄코드
		    	//-------------------------------------------------------------------------------------
		    	
	    	}else{
	    		
	    		if( !szYD_TO_BAY.equals("") ) {
	    			
	    			szYdBayGp	= szYD_TO_BAY;
	    			
	    			szMsg="["+szOperationName+"] 해당 대차["+szTcarEqpId+"]를 사용자가 지정한 동["+szYD_TO_BAY+"]으로 공대차출발 처리";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		}else{
	    			
		    		//-------------------------------------------------------------------------------------
		    		//	대차작업지정기준을 BRE Rule에서 조회 후 조회된 결과 항목으로 대차스케줄 조정
		    		//	반환항목 
		    		//	1. USAGE_YN(사용여부)				: 사용(Y), 미사용(N)
		    		//	2. WORK_GP(작업구분)				: 직상차(D), 동간이적(R)
		    		//	3. YD_CARLD_BAY_GP(야드상차동구분)	: 상차 물량이 존재하는 동
		    		//	4. YD_CARUD_BAY_GP(야드하차동구분)	: 상차 물량을 하차할 동
		    		//	비고		: 
		    		//		미사용인 경우 2, 3, 4항목은 의미가 없음
		    		//	적용방법
		    		//		1. 미사용인 경우는 일반적인 대차작업우선순위를 적용해서 처리
		    		//		2. 사용인 경우
		    		//			2-1. 작업구분이 직상차인 경우
		    		//				2-1-1. 대차를 상차동으로 공대차출발지시 전송
		    		//				2-1-2. 상차동으로 공대차출발실적 처리
		    		//				2-1-3. 설비로부터 대상재를 대차로 상차완료 후 영대차출발지시 전송
		    		//				2-1-4. 하차동으로 영대차출발실적처리
		    		//				2-1-5. 하차동에서 대상재를 하차완료 후 루틴 반복 처리
		    		//			2-2. 작업구분이 동간이적인 경우
		    		//				2-2-1. 등록된 대차작업들 중에서 Rule에 지정된 상차동에 존재하는 작업우선순위가 빠른 작업 조회
		    		//				2-2-2. 존재하면 대차작업
		    		//				2-2-3. 존재하지 않으면 1의 경우와 동일하게 처리
		    		//-------------------------------------------------------------------------------------
		    		
		    		szTCarRule				= YdCommonUtils.getTCarWrkStdRule(szTcarEqpId);
		    		
		    		szUSAGE_YN				= szTCarRule[0];
		    	    szWORK_GP				= szTCarRule[1];
		    	    szYD_CARLD_BAY_GP		= szTCarRule[2];
		    	    szYD_CARUD_BAY_GP		= szTCarRule[3];
		    		
		    		//-------------------------------------------------------------------------------------
		    		
		    	    if( szUSAGE_YN.equals("Y")) {				//사용
		    	    	if( szWORK_GP.equals("D") ) {				//직상차
		    	    		//아무작업없음
		    	    		szYdGp				= YdConstant.YD_GP_C_SLAB_YARD;
		    	    		szYdBayGp			= szYD_CARLD_BAY_GP;
		    	    		szYdAimBayGp		= szYD_CARUD_BAY_GP;
		    	    		
		    	    		szMsg="["+szOperationName+"] 해당 대차["+szTcarEqpId+"]에 대해서 대차작업지정기준을 사용하고 직상차이므로 RULE의 상차동["+szYD_CARLD_BAY_GP+"], 하차동["+szYD_CARUD_BAY_GP+"]을 사용함";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    	    		
		    	    	}else if( szWORK_GP.equals("R") ) {			//동간이적
		    	    		
		    	    		szMsg="["+szOperationName+"] 해당 대차["+szTcarEqpId+"]에 대해서 대차작업지정기준을 사용하고 동간이적이므로 RULE의 상차동["+szYD_CARLD_BAY_GP+"], 하차동["+szYD_CARUD_BAY_GP+"]을 사용하여 대차작업 조회 시작";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    	    		
		    	    		rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
		    	    		recInTemp 			= JDTORecordFactory.getInstance().create();
		    	    		recInTemp.setField("YD_WRK_PLAN_TCAR", 			szTcarEqpId);
		    	    		recInTemp.setField("YD_AIM_BAY_GP", 			szYD_CARUD_BAY_GP);
		    	    		recInTemp.setField("YD_GP", 					szTcarEqpId.substring(0, 1));
		    	    		recInTemp.setField("YD_BAY_GP", 				szYD_CARLD_BAY_GP);
		    	    		
		    	    		String szRtnMsg			= DaoManager.getYdWrkbook(recInTemp, rsResult, 28);
		    	    		
		    	    		if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
		    	    			rsResult.first();
						    	recOutTemp = JDTORecordFactory.getInstance().create();
						    	recOutTemp.setRecord(rsResult.getRecord());
						    	
						    	szYD_WBOOK_ID   = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WBOOK_ID");				//작업예약ID
						    	szYdGp          = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_GP");					//야드구분
						    	szYdBayGp       = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_BAY_GP");				//동구분
						    	szYdAimBayGp    = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_AIM_BAY_GP");			//목표동구분
						    	szYdWrkPlanTcar = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WRK_PLAN_TCAR");		//작업계획대차
						    	szYdSchCd       = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_SCH_CD");				//스케줄코드
						    	
						    	szMsg="["+szOperationName+"] 해당 대차["+szTcarEqpId+"]에 대해서 대차작업지정기준을 사용하고 동간이적이므로 RULE의 상차동["+szYD_CARLD_BAY_GP+"], 하차동["+szYD_CARUD_BAY_GP+"]을 사용하여 대차작업 조회 완료";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    	    		}else{
		    	    			bAfterQuery			= true;
		    	    			
		    	    			szMsg="["+szOperationName+"] 해당 대차["+szTcarEqpId+"]에 대해서 대차작업지정기준을 사용하고 동간이적이므로 RULE의 상차동["+szYD_CARLD_BAY_GP+"], 하차동["+szYD_CARUD_BAY_GP+"]을 사용하여 대차작업 조회 시 대상재가 존재하지 않음";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    	    		}
		    	    		
		    	    	}
		    	    }else{										//미사용
		    	    	bAfterQuery			= true;
		    	    	
		    	    	szMsg="["+szOperationName+"] 해당 대차["+szTcarEqpId+"]에 대해서 대차작업지정기준을 사용여부가 N이므로 사용하지 않고 일반적인 규칙을 적용";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    	    }
		    		
		    	    if( bAfterQuery ) {
		    	    	
		    	    	szMsg="["+szOperationName+"] 해당 대차["+szTcarEqpId+"]에 대해서 일반적인 규칙인 우선순위가 빠르고, 작업예약순서가 빠른 대차상차작업예약을 조회한다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    	    	
			    		//-------------------------------------------------------------------------------------
				    	//	파라미터로 전달된 작업예약ID가 존재하지 않으면 
			    		//	작업계획대차로 우선순위가 빠르고, 작업예약순서가 빠른 대차상차작업예약만 조회
			    		//-------------------------------------------------------------------------------------
			    		szMsg="["+szOperationName+"] 해당 대차["+szTcarEqpId+"]로 등록된 대차상차작업예약을 조회 시작";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			    		
				    	msgRecord.setField("YD_WRK_PLAN_TCAR", 				szTcarEqpId);
				    	
				    	rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			    		intRtnVal = ydWrkbookDao.getYdWrkbook(msgRecord, rsResult, 27);
			    		
			    		szMsg="["+szOperationName+"] 해당 대차["+szTcarEqpId+"]로 등록된 대차상차작업예약을 조회 완료 - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			    		
			    		if(intRtnVal > 0) {
					    	rsResult.first();
					    	recOutTemp = JDTORecordFactory.getInstance().create();
					    	recOutTemp.setRecord(rsResult.getRecord());
					    	
					    	szYD_WBOOK_ID   = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WBOOK_ID");				//작업예약ID
					    	szYdGp          = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_GP");					//야드구분
					    	szYdBayGp       = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_BAY_GP");				//동구분
					    	szYdAimBayGp    = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_AIM_BAY_GP");			//목표동구분
					    	szYdWrkPlanTcar = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WRK_PLAN_TCAR");		//작업계획대차
					    	szYdSchCd       = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_SCH_CD");				//스케줄코드
			    		}else{
			    			
			    			szYdBayGp		= szYD_HOME_BAY_GP;
			    			
			    			szMsg="["+szOperationName+"] 대차와 관련된 작업예약이 없는 경우에는 홈동["+szYD_HOME_BAY_GP+"]으로 공대차 출발 지시를 처리.";
			    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			    		}
		    	    }
	    		}
	    	}
	    	
	    	//-------------------------------------------------------------------------------------
	    	//	대차설비ID로 대차스케줄을 조회한다.
	    	//-------------------------------------------------------------------------------------
	    	
			szMsg="["+szOperationName+"] 대차설비ID(" + szTcarEqpId + ")로 대차스케줄 조회 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			
			recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_EQP_ID", 		szTcarEqpId);
	    	
	    	intRtnVal = ydTcarSchDao.getYdTcarsch(recInTemp, rsResult, 4);
	    	
	    	szMsg="["+szOperationName+"] 대차설비ID(" + szTcarEqpId + ")로 대차스케줄 조회 완료 - 반환값 : " + intRtnVal;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	//-------------------------------------------------------------------------------------
	    	
	    	//-------------------------------------------------------------------------------------
	    	//	만약 대차스케줄이 존재하면 대차 스케줄을 기동 하지 않는다.(공대차인경우는 제외)
	    	//-------------------------------------------------------------------------------------
	    	
	    	if(intRtnVal > 0) {
	    		
	    		//-------------------------------------------------------------------------------------
		    	//	대차스케줄이 존재
	    		//-------------------------------------------------------------------------------------
	    		rsResult.absolute(1);
	    		recTcar = JDTORecordFactory.getInstance().create();
	    		recTcar.setRecord(rsResult.getRecord());
	    		
	    		//상차작업예약 id가 있는지 없는지 Check!!!
	    		szYD_CARLD_WRK_BOOK_ID = ydDaoUtils.paraRecChkNull(recTcar, "YD_CARLD_WRK_BOOK_ID");
	    		szYD_CARLD_STOP_LOC    = ydDaoUtils.paraRecChkNull(recTcar, "YD_CARLD_STOP_LOC");
	    		szYD_TCAR_SCH_ID       = ydDaoUtils.paraRecChkNull(recTcar, "YD_TCAR_SCH_ID");
	    		
	    		szMsg="["+szOperationName+"] 대차설비ID(" + szTcarEqpId + ")로 대차스케줄["+szYD_TCAR_SCH_ID+"]이 존재하는 경우 - 이미 등록된 상차작업예약["+szYD_CARLD_WRK_BOOK_ID+"], 상차정지위치["+szYD_CARLD_STOP_LOC+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		
	    		if(!szYD_CARLD_WRK_BOOK_ID.equals("")) {
	    			
	    			//-------------------------------------------------------------------------------------
		    		//	대차스케줄의 상차작업예약이 존재하는 경우
		    		//-------------------------------------------------------------------------------------
					szMsg="["+szOperationName+"] 대차[" + szTcarEqpId + "]스케줄["+szYD_TCAR_SCH_ID+"]이 존재하고 이미 등록된 상차작업예약["+szYD_CARLD_WRK_BOOK_ID+"]이 존재하므로 대차스케줄["+szYD_TCAR_SCH_ID+"]을 기동할 수 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					return intRtnVal = 1;
					//-------------------------------------------------------------------------------------
					
	    		}else if (szYD_CARLD_WRK_BOOK_ID.equals("") && szYD_TO_BAY.equals("")){
	    			
		    		//-------------------------------------------------------------------------------------
		    		//	대차스케줄 업데이트항목
		    		//-------------------------------------------------------------------------------------
		    		szYD_CARLD_STOP_LOC			= "A" + szYdBayGp 	+ szTcarEqpId.substring(2,6);
		    		szYD_CARUD_STOP_LOC			= "A" + szYdAimBayGp+ szTcarEqpId.substring(2,6);
		    		
		    		recInTemp = JDTORecordFactory.getInstance().create();
		    		recInTemp.setField("YD_TCAR_SCH_ID", 				szYD_TCAR_SCH_ID);
			    	recInTemp.setField("YD_CARLD_SCH_REQ_GP", 			"6");
					recInTemp.setField("YD_CARUD_SCH_REQ_GP", 			"3");
					recInTemp.setField("YD_CARLD_WRK_BOOK_ID", 			szYD_WBOOK_ID);				//상차작업예약
					recInTemp.setField("YD_CARLD_STOP_LOC", 			szYD_CARLD_STOP_LOC);		//상차정지위치
					if( !szYdAimBayGp.equals("") ) {
						recInTemp.setField("YD_CARUD_STOP_LOC", 		szYD_CARUD_STOP_LOC);		//하차정지위치
					}
					
		    		if(szYdBayGp.equals(szYD_TCAR_CURR_BAY_GP)) {
		    			
		    			//-------------------------------------------------------------------------------------
						//	상차정지위치와 대차설비의 현재동이 같은경우 상차도착상태로 변경, 크레인스케줄 호출
						//-------------------------------------------------------------------------------------
		    			szMsg="["+szOperationName+"] 상차정지위치동["+szYdBayGp+"]와 대차설비의 현재동["+szYD_TCAR_CURR_BAY_GP+"]이 같은 경우는 대차스케줄["+szYD_TCAR_SCH_ID+"]에 상차작업예약["+szYD_WBOOK_ID+"], 상차도착 상태[2]로 등록 후 크레인스케줄 호출";
		    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    			
		    			recInTemp.setField("YD_CAR_PROG_STAT", 			"2");	//상차도착
		    			intRtnVal = ydTcarSchDao.updYdTcarsch(recInTemp, 0);
		    			if(intRtnVal <= 0) {
			    			szMsg="["+szOperationName+"] 대차스케줄["+szYD_TCAR_SCH_ID+"]에 상차작업예약["+szYD_WBOOK_ID+"], 상차도착 상태[2], 상차정지위치["+szYD_CARLD_STOP_LOC+"], 하차정지위치["+szYD_CARUD_STOP_LOC+"] 등록 시 오류발생 - 반환값 : " + intRtnVal;
			    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			    			return intRtnVal = -1;
		    			}
		    			
		    			if( !szYD_WBOOK_ID.equals("") ){
		    				
			    			recInTemp = JDTORecordFactory.getInstance().create();
			    			recInTemp.setField("MSG_ID",    	"YDYDJ500");
			    			recInTemp.setField("YD_SCH_CD", 	szYdSchCd);
			    			
			    			//-------------------------------------------------------------------------------------
							//	크레인설비ID를 구하기 위해서 스케줄기준 조회
							//-------------------------------------------------------------------------------------
			    			szMsg="["+szOperationName+"] 크레인설비ID를 구하기 위해서 스케줄코드["+szYdSchCd+"]로 스케줄기준 조회 시작";
			    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			    			
			    			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			    			intRtnVal = ydSchRuleDao.getYdSchrule(recInTemp, rsResult, 0);
			    			if(intRtnVal <= 0) {
			        			szMsg="["+szOperationName+"] 크레인설비ID를 구하기 위해서 스케줄코드["+szYdSchCd+"]로 스케줄기준 조회 시 오류발생 - 반환값 : " + intRtnVal;
			        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			        			throw new DAOException(szMsg);
			    			}
			    			
			    			szMsg="["+szOperationName+"] 크레인설비ID를 구하기 위해서 스케줄코드["+szYdSchCd+"]로 스케줄기준 조회 완료";
			    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			    			
			    			rsResult.absolute(1);
			    			recOutTemp = JDTORecordFactory.getInstance().create();
			        		recOutTemp.setRecord(rsResult.getRecord());
			    			//-------------------------------------------------------------------------------------
			        		
			        		//-------------------------------------------------------------------------------------
			    			//	대차가 이미 도착한 상태이므로 크레인 스케줄 호출
			        		//-------------------------------------------------------------------------------------
			        		szMsg="["+szOperationName+"] 상차정지위치동["+szYdBayGp+"]와 대차설비의 현재동["+szYD_TCAR_CURR_BAY_GP+"]이 같은 경우는 대차가 이미 도착한 상태이므로 크레인 스케줄 호출 시작";
			    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			    			
			    			recInTemp.setField("YD_EQP_ID", ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WRK_CRN"));
			    			
			    			ydDelegate.sendMsg(recInTemp);
			    			
			    			szMsg="["+szOperationName+"] 상차정지위치동["+szYdBayGp+"]와 대차설비의 현재동["+szYD_TCAR_CURR_BAY_GP+"]이 같은 경우는 대차가 이미 도착한 상태이므로 크레인 스케줄 호출 완료 - 공대차출발스케줄 종료";
			    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			    			
			    			//-------------------------------------------------------------------------------------
		    			}else{
		    				szMsg="["+szOperationName+"] 상차작업예약["+szYD_WBOOK_ID+"]이 존재하지 않으므로 크레인 스케줄 호출하지 않음";
			    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    			}
		    			
		    		}else{
		    			
		    			//공대차이지만 다른동에 있을경우에는 대차스케줄 상차동을 현재동으로 등록한 후 공대차출발지시 등록 
		    			//-------------------------------------------------------------------------------------
		    			//	대차설비의 현재동과 대차상차작업예약의 상차정지위치가 다른 경우 공대차출발지시를 L2로 전송
		    			//-------------------------------------------------------------------------------------
		    			szMsg="["+szOperationName+"] 대차설비[" + szTcarEqpId + "]의 현재동["+szYD_TCAR_CURR_BAY_GP+"]과 대차상차작업예약의 상차정지위치동["+szYdBayGp+"]이 다른 경우 공대차출발지시를 L2로 전송";
		    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    			
		    			szMsg="["+szOperationName+"] 상차정지위치동["+szYdBayGp+"]와 대차설비[" + szTcarEqpId + "]의 현재동["+szYD_TCAR_CURR_BAY_GP+"]이 다른 경우는 대차스케줄["+szYD_TCAR_SCH_ID+"]에 상차작업예약["+szYD_WBOOK_ID+"], 공차대기 상태[0]로 등록 시작";
		    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    			
		    			recInTemp.setField("YD_CAR_PROG_STAT", "0");	//공차대기 - 공차출발실적에서 상차출발로 변경함.

		    			intRtnVal = ydTcarSchDao.updYdTcarsch(recInTemp, 0);
		    			
		    			if(intRtnVal <= 0) {
		    				szMsg="["+szOperationName+"] 상차정지위치동["+szYdBayGp+"]와 대차설비의 현재동["+szYD_TCAR_CURR_BAY_GP+"]이 다른 경우는 대차스케줄["+szYD_TCAR_SCH_ID+"]에 상차작업예약["+szYD_WBOOK_ID+"], 공차대기 상태[0]로 등록 시 오류발생 - 반환값 : " + intRtnVal;
			    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			    			return intRtnVal = -1;
		    			}
		    			
		    			szMsg="["+szOperationName+"] 상차정지위치동["+szYdBayGp+"]와 대차설비의 현재동["+szYD_TCAR_CURR_BAY_GP+"]이 다른 경우는 대차스케줄["+szYD_TCAR_SCH_ID+"]에 상차작업예약["+szYD_WBOOK_ID+"], 공차대기 상태[0]로 등록 완료 - 반환값 : " + intRtnVal;
		    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    			//-------------------------------------------------------------------------------------
		    			
		    			//-------------------------------------------------------------------------------------
						//	공대차출발지시를 한다.야드구분, 야드스케줄코드, 대차스케줄ID
		    			//-------------------------------------------------------------------------------------
		    			recInTemp = JDTORecordFactory.getInstance().create();
			    		recInTemp.setField("MSG_ID", 				"YDC3L006");
			    		recInTemp.setField("YD_GP", 				"A");
			    		recInTemp.setField("YD_SCH_CD", 			"");
			    		recInTemp.setField("YD_TCAR_SCH_ID", 		szYD_TCAR_SCH_ID);
			    		
			    		ydDelegate.sendMsg(recInTemp);
			    		
						szMsg="["+szOperationName+"] 상차정지위치동["+szYdBayGp+"]으로 공대차 출발지시[상차출발] 전송 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						//-------------------------------------------------------------------------------------
		    		}

	    		}else if (szYD_CARLD_WRK_BOOK_ID.equals("") && !szYD_TO_BAY.trim().equals("")){
	    			
	    			//-------------------------------------------------------------------------------------
	    			//	대차스케줄의 상차작업예약이 등록되어 있지 않고 사용자가 지정한 목표동의 값이 존재하는 경우
	    			//-------------------------------------------------------------------------------------
	    			szMsg="["+szOperationName+"] 대차스케줄["+szYD_TCAR_SCH_ID+"]의 상차작업예약이 등록되어 있지 않고 사용자가 지정한 목표동["+szYD_TO_BAY+"]이 존재하는 경우는 대차스케줄["+szYD_TCAR_SCH_ID+"]에 공차대기 상태[0]로 등록 후 공대차출발 지시 전송";
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			
	    			recTcar.setField("YD_CAR_PROG_STAT", 			"0");					//공차대기
	    			recTcar.setField("YD_CARLD_STOP_LOC", 			"A" + szYD_TO_BAY + szTcarEqpId.substring(2,6)); //사용자지정위치
	    			intRtnVal = ydTcarSchDao.updYdTcarsch(recTcar, 0);
	    			if(intRtnVal <= 0) {
	    				szMsg="["+szOperationName+"] 대차스케줄["+szYD_TCAR_SCH_ID+"]의 상차작업예약이 등록되어 있지 않고 사용자가 지정한 목표동["+szYD_TO_BAY+"]이 존재하는 경우는 대차스케줄["+szYD_TCAR_SCH_ID+"]에 공차대기 상태[0]로 등록 시 오류발생 - 반환값 : " + intRtnVal;
		    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		    			return intRtnVal = -1;
	    			}
	    			//-------------------------------------------------------------------------------------
	    			
	    			//-------------------------------------------------------------------------------------
					//공대차출발지시를 한다.야드구분, 야드스케줄코드, 대차스케줄ID
	    			//-------------------------------------------------------------------------------------
	    			recInTemp = JDTORecordFactory.getInstance().create();
		    		recInTemp.setField("MSG_ID", 			"YDC3L006");
		    		recInTemp.setField("YD_GP", 			"A");
		    		recInTemp.setField("YD_SCH_CD", 		"");
		    		recInTemp.setField("YD_TCAR_SCH_ID", 	szYD_TCAR_SCH_ID);
		    		
		    		ydDelegate.sendMsg(recInTemp);
		    		
		    		szMsg="["+szOperationName+"] 사용자 지정동["+szYD_TO_BAY+"]으로 공대차 출발지시[공차대기] 전송 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					//-------------------------------------------------------------------------------------
	    		}

	    	}else{
	    		
	    		//-------------------------------------------------------------------------------------
	    		//	대차스케줄이 없는 경우!!
	    		//-------------------------------------------------------------------------------------
	    		String szYD_LD_UD_GP			= ydDaoUtils.paraRecChkNull(msgRecord, "YD_LD_UD_GP");
	    		
	    		szMsg="["+szOperationName+"] 대차스케줄이 존재하지 않는 경우 - YD_LD_UD_GP["+szYD_LD_UD_GP+"], YD_WBOOK_ID["+szYD_WBOOK_ID+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		
	    		if( szYD_LD_UD_GP.equals("U") ) {
	    			szMsg="["+szOperationName+"] 파라미터로 전달된 권상/권하실적처리 호출구분["+szYD_LD_UD_GP+"]이 권상실적처리인 경우 - 하차작업이 끝나고 다음 작업예약을 찾는경우";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		}else{
	    			szMsg="["+szOperationName+"] 파라미터로 전달된 권상/권하실적처리 호출구분["+szYD_LD_UD_GP+"]이 없는 경우 - 권상실적처리 시";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		}
	    		
	    		//작업계획대차설정
		    	msgRecord.setField("YD_WRK_PLAN_TCAR", szTcarEqpId);
		    	
		    	intRtnVal		= 0;

		    	if( szYD_WBOOK_ID.equals("") ) {
		    		
					szMsg="intRtnVal = " + rsResult.size();
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					szMsg="["+szOperationName+"] 해당 대차설비[" + szTcarEqpId + "]의 작업예약이 등록되어 있지 않으므로 대차스케줄 생성 후 현재동과 홈동이 다르면 공대차출발지시를 전송한다.";
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
					 * 2009.06.29 김진욱
					 * 대차 하차 후 다음 대차작업을 찾을때 다음 대차 작업이 없는 경우 현재위치에 대차 스케줄을 생성한다.
					 ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
		    		
		    		//대차스케줄을 생성한다.
		    		//대차스케줄1개를 생성하고 해당 작업예약id를 상차작업예약id에 등록한다.
			    	recInTemp = JDTORecordFactory.getInstance().create();
			    	recInTemp.setField("YD_TCAR_SCH_ID", "1");
			    	rsResult1 = JDTORecordFactory.getInstance().createRecordSet("Temp");
			    	intRtnVal = ydTcarSchDao.getYdTcarsch(recInTemp, rsResult1, 5);
			    	rsResult1.absolute(1);
			    	recOutTemp = JDTORecordFactory.getInstance().create();
			    	recOutTemp.setRecord(rsResult1.getRecord());
			    	szYD_TCAR_SCH_ID = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TCAR_SCH_ID");
			    	
			    	recInTemp = JDTORecordFactory.getInstance().create();
			    	//대차스케줄생성
			    	recInTemp.setField("YD_TCAR_SCH_ID", 				szYD_TCAR_SCH_ID);
			    	recInTemp.setField("REGISTER", 						szRegister);
		    		
			    	//현재위치가 상차정지위치로등록한다.
		    		
			    	boolean bSEND_START		= false;
			    	
			    	if(szYD_TO_BAY.equals("")){
			    		
			    		//대차상차정지위치-- 상차작업예약이 없고 to동이 지정되어 오지 않은경우에는 홈동으로 보내기위해...
			    		szMsg="["+szOperationName+"] 지정목표동이 존재하지 않으므로 해당 대차설비[" + szTcarEqpId + "]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 상차정지위치를 홈동["+szYD_HOME_BAY_GP+"], 공차대기[0]으로 지정한다.";
		    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				    					    	
				    	if( szUSAGE_YN.equals("Y")) {				//사용
			    	    	if( szWORK_GP.equals("D") ) {			//직상차
			    	    		szMsg="["+szOperationName+"] 지정목표동이 존재하지 않으므로 해당 대차설비[" + szTcarEqpId + "]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 상차정지위치를 홈동["+szYD_HOME_BAY_GP+"]이 아닌 Rule의 직상차 상차동["+szYD_CARLD_BAY_GP+"]으로 설정한다.";
				    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			    	    		szYD_HOME_BAY_GP			= szYD_CARLD_BAY_GP;
			    	    	}
			    		}
			    		
				    	recInTemp.setField("YD_CARLD_STOP_LOC", 			szTcarEqpId.substring(0,1) + szYD_HOME_BAY_GP + szTcarEqpId.substring(2));
				    	
				    	if( !szYdAimBayGp.equals("") ) {
				    		recInTemp.setField("YD_CARUD_STOP_LOC", 			szTcarEqpId.substring(0,1) + szYdAimBayGp + szTcarEqpId.substring(2));		//하차정지위치
				    	}
				    	
				    	//------------------------------------------------
				    	recInTemp.setField("YD_CAR_PROG_STAT", 			"0");
				    	//------------------------------------------------
				    	
				    	if( !szYD_HOME_BAY_GP.equals(szYD_TCAR_CURR_BAY_GP) ) {
				    		bSEND_START			= true;
				    		
				    		szMsg="["+szOperationName+"] 해당 대차설비[" + szTcarEqpId + "]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 상차정지위치를 홈동["+szYD_HOME_BAY_GP+"]과 현재동["+szYD_TCAR_CURR_BAY_GP+"]이 다르므로 공대차출발지시 전송한다.";
			    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				    	}
			    	}else{
				    	//대차상차정지위치-- to동이 지정되어 왔을 경우에는 To동에 상차위치로 잡고 대차출발지시를 내린다.
			    		szMsg="["+szOperationName+"] 지정목표동["+szYD_TO_BAY+"]이 존재하므로 해당 대차설비[" + szTcarEqpId + "]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 상차정지위치를 지정목표동["+szYD_TO_BAY+"], 공차대기[0]으로 지정한다.";
		    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				    	recInTemp.setField("YD_CARLD_STOP_LOC", szTcarEqpId.substring(0,1) + szYD_TO_BAY + szTcarEqpId.substring(2));
				    	
				    	//------------------------------------------------
				    	recInTemp.setField("YD_CAR_PROG_STAT", "0");	
				    	//------------------------------------------------
				    	
				    	if( !szYD_TO_BAY.equals(szYD_TCAR_CURR_BAY_GP) ) {
				    		bSEND_START			= true;
				    		
				    		szMsg="["+szOperationName+"] 해당 대차설비[" + szTcarEqpId + "]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 상차정지위치를 지정목표동["+szYD_TO_BAY+"]과 현재동["+szYD_TCAR_CURR_BAY_GP+"]이 다르므로 공대차출발지시 전송한다.";
			    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				    	}
				    	
			    	}
			    	
			    	recInTemp.setField("YD_CARLD_SCH_REQ_GP", "6");
					recInTemp.setField("YD_CARUD_SCH_REQ_GP", "3");
					
			    	//설비작업상태값( 공차로 등록 )
			    	recInTemp.setField("YD_EQP_WRK_STAT", "U");
		    		
			    	//야드설비ID
			    	recInTemp.setField("YD_EQP_ID", szTcarEqpId);
			    	intRtnVal = ydTcarSchDao.insYdTcarsch(recInTemp);
			    	
		    		if(intRtnVal == -2) {
						szMsg="["+szOperationName+"] parameter error[1]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return intRtnVal = -1;
		    		}
		    		
					szMsg="["+szOperationName+"] 대차하차 후 새로운 대차 작업이 없어 대차스케줄만 생성!!";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					//홈동과 현재동이 틀릴경우 공대차 출발지시를 내린다.
					if( bSEND_START ) {
						//공대차출발지시를 한다.야드구분, 야드스케줄코드, 대차스케줄ID
			    		recInTemp = JDTORecordFactory.getInstance().create();
			    		recInTemp.setField("MSG_ID", 			"YDC3L006");
			    		recInTemp.setField("YD_GP", 			"A");
			    		recInTemp.setField("YD_SCH_CD", 		"");
			    		recInTemp.setField("YD_TCAR_SCH_ID", 	szYD_TCAR_SCH_ID);
			    		
			    		ydDelegate.sendMsg(recInTemp);
			    		
						szMsg="["+szOperationName+"] 공대차 출발지시 전송 완료[1]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
					
		    	}else{
			    	//----------------------------------------------------------------------------------------
			    	//	대차 작업예약이 존재하는 경우
			    	//----------------------------------------------------------------------------------------
			    	szMsg="["+szOperationName+"] 해당 대차설비[" + szTcarEqpId + "]의 작업예약["+szYD_WBOOK_ID+"]이 등록되어 있으므로 대차스케줄 생성 후 현재동과 목표동이 다르면 공대차출발지시를 전송한다.";
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			    	
			    	//대차스케줄1개를 생성하고 해당 작업예약id를 상차작업예약id에 등록한다.
			    	recInTemp = JDTORecordFactory.getInstance().create();
			    	recInTemp.setField("YD_TCAR_SCH_ID", "1");
			    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
			    	intRtnVal = ydTcarSchDao.getYdTcarsch(recInTemp, rsResult, 5);
			    	rsResult.absolute(1);
			    	recOutTemp = JDTORecordFactory.getInstance().create();
			    	recOutTemp.setRecord(rsResult.getRecord());
			    	szYD_TCAR_SCH_ID = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TCAR_SCH_ID");
			    	
			    	recInTemp = JDTORecordFactory.getInstance().create();
			    	//대차스케줄생성
			    	recInTemp.setField("YD_TCAR_SCH_ID", 				szYD_TCAR_SCH_ID);
			    	recInTemp.setField("YD_CARLD_WRK_BOOK_ID", 			szYD_WBOOK_ID);
			    	recInTemp.setField("REGISTER", 						szRegister);
			    	
			    	//대차상차정지위치, 하차정지위치
			    	recInTemp.setField("YD_CARLD_STOP_LOC", 			"A" + szYdBayGp + szYdWrkPlanTcar.substring(2,6));
			    	if( !szYdAimBayGp.equals("") ) {
			    		recInTemp.setField("YD_CARUD_STOP_LOC", 		"A" + szYdAimBayGp + szYdWrkPlanTcar.substring(2,6));
			    	}
			    	recInTemp.setField("YD_CARLD_SCH_REQ_GP", 			"6");
					recInTemp.setField("YD_CARUD_SCH_REQ_GP", 			"3");
					
					if(szYD_TCAR_CURR_BAY_GP.equals(szYdBayGp)) {
						//현재동과 상차동이 같으므로 상차도착 상태로 등록
						recInTemp.setField("YD_CAR_PROG_STAT", 				"2");
					}else{
						//현재동과 상차동이 다르므로 공차대기 상태로 등록
						recInTemp.setField("YD_CAR_PROG_STAT", 				"0");
					}
					//설비작업상태값( 공차로 등록 )
			    	recInTemp.setField("YD_EQP_WRK_STAT", 				"U");
			    	
			    	//야드설비ID
			    	recInTemp.setField("YD_EQP_ID", szYdWrkPlanTcar);
			    	intRtnVal = ydTcarSchDao.insYdTcarsch(recInTemp);
		    		if(intRtnVal == -2) {
						szMsg="["+szOperationName+"] parameter error[2]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return intRtnVal = -1;
		    		}
		    		
					szMsg="["+szOperationName+"] szYD_TCAR_CURR_BAY_GP : " + szYD_TCAR_CURR_BAY_GP + " ,  szYdBayGp : " + szYdBayGp;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    		
		    		//현재 대차동과 작업예약의 동과 일치하면 스케줄 호출 비일치시 대차출발지시를 내린다.
		    		if(szYD_TCAR_CURR_BAY_GP.equals(szYdBayGp)) {
		    			
		    			recInTemp = JDTORecordFactory.getInstance().create();
		    			recInTemp.setField("MSG_ID",    "YDYDJ500");
		    			recInTemp.setField("YD_SCH_CD", szYdSchCd);
		    			
		    			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		    			intRtnVal = ydSchRuleDao.getYdSchrule(recInTemp, rsResult, 0);
		    			if(intRtnVal <= 0) {
		        			szMsg="["+szOperationName+"] 스케줄 기준 조회중 값이없거나 Error Code : " + intRtnVal;
		        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    			}
		    			
		    			rsResult.absolute(1);
		    			recOutTemp = JDTORecordFactory.getInstance().create();
		        		recOutTemp.setRecord(rsResult.getRecord());
		    			
		        		//------------------------------------------------
		        		//	대차의 차량진행상태를 상차도착으로 변경 필요.
		        		//------------------------------------------------
		        		
		    			//크레인 스케줄 호출
		    			szMsg="["+szOperationName+"] 대차가 이미 도착해있으므로 크레인 스케줄 호출";
		    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    			recInTemp.setField("YD_EQP_ID", ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WRK_CRN"));
		    			
		    			ydDelegate.sendMsg(recInTemp);
		    			
		    			szMsg="["+szOperationName+"] 대차 상차 스케줄("+szMethodName+") 완료";
		    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    			
		    		}else{
			    	
				    	//공대차출발지시를 한다.야드구분, 야드스케줄코드, 대차스케줄ID
			    		recInTemp = JDTORecordFactory.getInstance().create();
			    		recInTemp.setField("MSG_ID", "YDC3L006");
			    		recInTemp.setField("YD_GP", szYdGp);
			    		recInTemp.setField("YD_SCH_CD", szYdSchCd);
			    		recInTemp.setField("YD_TCAR_SCH_ID", szYD_TCAR_SCH_ID);
			    		
			    		ydDelegate.sendMsg(recInTemp);
			    		
						szMsg="["+szOperationName+"] 공대차 출발지시 전송 완료[2]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    		}
		    	}
	    	}
		}catch(Exception e){
	
			szMsg="["+szOperationName+"] 공대차 이동지시 Error : " +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			m_ctx.setRollbackOnly();
			throw new DAOException(szMsg);
		}
	
		szMsg="["+szOperationName+"] ------------------- 메소드 끝 -------------------";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        
		return intRtnVal = 1;
	} //end of Y1L2TcarSch()
	
	/**
	 * 오퍼레이션명 : 대차 상차 스케줄(영대차 출발 지시)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public int Y1UdTcarSch(JDTORecord msgRecord)throws DAOException  {
		
		YdTcarSchDao     ydTcarSchDao   	= new YdTcarSchDao(); 
		YdWrkbookDao     ydWrkbookDao   	= new YdWrkbookDao();
		YdWrkbookMtlDao  ydWrkbookMtlDao 	= new YdWrkbookMtlDao();
		YdTcarFtmvMtlDao ydTcarFtmvMtlDao	= new YdTcarFtmvMtlDao();
		
		JDTORecordSet rsResult          = null;
		JDTORecordSet rsWrkBookMtl      = null;
		JDTORecordSet rsWRKBOOK         = null;
		JDTORecordSet rsTcarFtMtl       = null;
		 
		JDTORecord    recInTemp         = null;
		JDTORecord    recOutTemp        = null;
		JDTORecord    recWrkBookMtl     = null;
		
	    int intRtnVal 					= 0;
	    int intWbookMtlCnt              = 0;
	    int intTcarFtmvMtlCnt           = 0;
	    
	    String szMsg              		= "";
	    String szMethodName       		= "Y1UdTcarSch";
	    String szOperationName			= "대차상차스케줄";
	    
	    String szWbookId                = "";
	    String szYD_SCH_CD              = "";
	    String szYD_TCAR_SCH_ID			= "";
	    
	    
	    try{
	    	szMsg="["+szOperationName+"] ------------------- 메소드 시작 -------------------";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
			//상차 완료 Check를 한다.
			//작업예약ID로 대차상차작업예약ID로 대차스케줄을 조회해서 대차스케줄ID를 조회한다.
	    	szWbookId = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");
	    	
	    	//작업예약재료의 갯수 조회
			szMsg= "[" + szWbookId + "]번의 작업예약재료의 갯수 조회";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_WBOOK_ID", szWbookId);
	    	rsWrkBookMtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recInTemp, rsWrkBookMtl, 14);
	    	if(intRtnVal <= 0) {
				szMsg="작업예약재료의 갯수 조회중 Error!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return intRtnVal = -1;
	    	}
	    	rsWrkBookMtl.absolute(1);
	    	recOutTemp = JDTORecordFactory.getInstance().create();
	    	recOutTemp.setRecord(rsWrkBookMtl.getRecord());
	    	intWbookMtlCnt = ydDaoUtils.paraRecChkNullInt(recOutTemp, "WBOOKMTL_CNT");
	    	
	    	//작업예약재료와 대차이송재료Table중 재료번호가 같은것의 갯수 조회
	    	szMsg="작업예약재료와 대차이송재료Table중 재료번호가 같은것의 갯수 조회";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_CARLD_WRK_BOOK_ID", szWbookId);
	    	recInTemp.setField("YD_WBOOK_ID",          szWbookId);
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recInTemp, rsResult, 15);
	    	if(intRtnVal <= 0) {
		    	szMsg="작업예약재료와 	 재료번호가 같은것의 갯수 조회중 Error Error Code : " + rsResult.size();
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    	}
	    	
	    	szMsg="rsResult SIZE :" + rsResult.size();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		    	
	    	rsResult.absolute(1);
	    	recOutTemp = JDTORecordFactory.getInstance().create();
	    	recOutTemp.setRecord(rsResult.getRecord());
	    	
	    	intTcarFtmvMtlCnt = recOutTemp.getFieldInt("TCARFTMVMTL_CNT");
	    	szYD_TCAR_SCH_ID = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TCAR_SCH_ID");
	    	
			szMsg="작업예약 재료 매수 : " + intWbookMtlCnt + " , 대차이송재료매수 : " + intTcarFtmvMtlCnt;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//작업예약TABLE 조회(스케줄코드를 조회하여 스케줄코드가 대차관련 스케줄코드인지 아닌지에 판단해 
			//                  대차관련 스케줄코드가 아니라면 중량체크를해서 차량출발지시를 보내기위해...)
			rsWRKBOOK = JDTORecordFactory.getInstance().createRecordSet("Temp");
			recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_CARLD_WRK_BOOK_ID", szWbookId);
	    	recInTemp.setField("YD_WBOOK_ID",          szWbookId);
	    	intRtnVal = ydWrkbookDao.getYdWrkbook(recInTemp, rsWRKBOOK, 10);
	    	if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="getYdWrkbookmtl data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);	
    			}else if(intRtnVal == -2) {
    				szMsg="getYdWrkbookmtl parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}
    			return intRtnVal = -1;
    		}
	    	rsWRKBOOK.absolute(1);
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setRecord(rsWRKBOOK.getRecord());
	    	szYD_SCH_CD = ydDaoUtils.paraRecChkNull(recInTemp, "YD_SCH_CD");
			
	    	/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * 			연주정정Level2 대차작업실적 전송  - YDC3L007
	         * 업무기준 Desc : 대차에 상차 시
	         * 스케줄코드 :  대차상차스케줄
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	    	recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("MSG_ID",        "YDC3L007");
			recInTemp.setField("YD_TCAR_SCH_ID", szYD_TCAR_SCH_ID);
			ydDelegate.sendMsg(recInTemp);
			szMsg = "[대차스케줄]대차 상차 시 - 대차작업실적 [YDC3L007] 전송 완료" ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			
            boolean isWork = false;
            /*
             * 2011.04.15 YJK
             * 대차상차 스케쥴일경우 미리 만들어 놓은 작업재료 매수를 다 상차했는지 체크
             * 그 외의 스케쥴은 직상차작업일 경우 - 상차작업시 바로 출발 
             */
			if(intWbookMtlCnt == intTcarFtmvMtlCnt && 
			   szYD_SCH_CD.substring(2,4).equals("TC") ) {
				isWork = true;
			}else if(!szYD_SCH_CD.substring(2,4).equals("TC")){
				isWork = true;
	    	}
			
			if(isWork){
				
				szMsg="하차작업예약 생성 및 등록";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
		    	//하차 작업예약 및 작업예약재료 생성
		    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
		    	intRtnVal = this.Y1MakeWrkBook(msgRecord, rsResult);
		    	if(intRtnVal == -1) return intRtnVal;
		    	
		    	//대차스케줄에 하차작업예약id 등록
				szMsg="대차스케줄에 하차작업예약id 등록";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    	rsResult.absolute(1);
		    	recWrkBookMtl = JDTORecordFactory.getInstance().create();
		    	recWrkBookMtl.setRecord(rsResult.getRecord());
		    	
		    	recInTemp     = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_TCAR_SCH_ID", ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TCAR_SCH_ID"));
				recInTemp.setField("YD_CARUD_WRK_BOOK_ID", ydDaoUtils.paraRecChkNull(recWrkBookMtl, "YD_WBOOK_ID"));
				recInTemp.setField("YD_EQP_WRK_STAT", "L");
				recInTemp.setField("YD_CAR_PROG_STAT", "5");
				recInTemp.setField("YD_CARUD_STOP_LOC", ydDaoUtils.paraRecChkNull(recWrkBookMtl, "YD_SCH_CD").substring(0,6));
	
		    	intRtnVal = ydTcarSchDao.updYdTcarsch(recInTemp, 0);
				if(intRtnVal <= 0) {
	    			return intRtnVal = -1;
	    		}
				
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				intRtnVal = ydWrkbookDao.getYdWrkbook(recWrkBookMtl, rsResult, 0);
				if(intRtnVal <= 0) {
					return intRtnVal = -1;
				}
				
				rsResult.absolute(1);
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("MSG_ID", "YDC3L006");
				recInTemp.addRecord(rsResult.getRecord());
				
				//영대차출발지시
	    		recInTemp.setField("YD_TCAR_SCH_ID", ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TCAR_SCH_ID"));
	    		recInTemp.setField("YD_GP",          ydDaoUtils.paraRecChkNull(recWrkBookMtl, "YD_GP"));
	    		recInTemp.setField("YD_SCH_CD",      ydDaoUtils.paraRecChkNull(recWrkBookMtl, "YD_SCH_CD"));
	    		
				szMsg="영대차출발지시!!  MSG_IG : " + recInTemp.getFieldString("MSG_ID") + " 전송";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
	    		ydDelegate.sendMsg(recInTemp);
			}

		}catch(Exception e){
			szMsg="대차 상차 스케줄 Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			m_ctx.setRollbackOnly();
			throw new DAOException(szMsg);
		}
	
		szMsg="대차 상차 스케줄("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		return intRtnVal = 1;
	} //end of Y1UdTcarSch()
    
	
	/**
	 * 오퍼레이션명 : 대차 하차 스케줄(공대차 출발 지시)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public int Y1LdTcarSch(JDTORecord msgRecord)throws DAOException  {
		
		YdTcarSchDao     ydTcarSchDao     = new YdTcarSchDao(); 
		YdTcarFtmvMtlDao ydTcarFtmvMtlDao = new YdTcarFtmvMtlDao();

		JDTORecordSet rsResult          = null;
		JDTORecordSet rsTcarSch         = null;
		JDTORecord    recInTemp         = null;
		JDTORecord    recOutTemp        = null;
	   
		int intRtnVal 					= 0 ;
	    
	    String szMsg              		= "";
	    String szMethodName       		= "Y1LdTcarSch";
	    String szOperationName			= "대차하차스케줄";
	    
	    String szWbookId                = "";
	    String szYD_TCAR_SCH_ID			= "";
	    String szYD_EQP_ID				= "";
	    
	    try{
			//하차 완료 Check를 한다.
	    	//작업예약ID로 대차하차작업예약ID로 대차스케줄을 조회해서 대차스케줄ID를 조회한다.
	    	szWbookId = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");
	    	
	    	//대차스케줄 조회용
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_CARUD_WRK_BOOK_ID", szWbookId);
	    	rsTcarSch = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	intRtnVal = ydTcarSchDao.getYdTcarsch(recInTemp, rsTcarSch, 1);
	    	
	    	//대차이송재료조회
	    	rsTcarSch.absolute(1);
	    	recOutTemp = JDTORecordFactory.getInstance().create();
	    	recOutTemp.setRecord(rsTcarSch.getRecord());
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	
	    	szYD_TCAR_SCH_ID 	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TCAR_SCH_ID");
	    	szYD_EQP_ID 		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_ID");
	    	
	    	//대차이송재료가 전부 삭제상태인지 확인한다.
	    	intRtnVal = ydTcarFtmvMtlDao.getYdTcarftmvmtl(recOutTemp, rsResult, 1);
	
	    	/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * 			연주정정Level2 대차작업실적 전송  - YDC3L007
	         * 업무기준 Desc : 대차하차 시
	         * 스케줄코드 :  대차하차스케줄
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	    	recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("MSG_ID",        "YDC3L007");
			recInTemp.setField("YD_TCAR_SCH_ID", szYD_TCAR_SCH_ID);
			ydDelegate.sendMsg(recInTemp);
			szMsg = "[대차스케줄]대차 하차 시 - 대차작업실적 [YDC3L007] 전송 완료" ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	    	
	    	//하차가 완료된 경우 
	    	if(intRtnVal == 0) {
	    		
	    		//대차스케줄 삭제처리
	    		recInTemp = JDTORecordFactory.getInstance().create();
	    		recInTemp.setField("YD_EQP_WRK_STAT", "U");
	    		recInTemp.setField("DEL_YN", "Y");
	    		recInTemp.setField("YD_TCAR_SCH_ID", ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TCAR_SCH_ID"));
				recInTemp.setField("YD_CAR_PROG_STAT", "E");

				intRtnVal = ydTcarSchDao.updYdTcarsch(recInTemp, 0);
	    			
				/*
				 * 대차스케줄생성 메소드 호출 
				 * msgRecord에 값을 비워서 보내도록 변경
				 * 이전작업예약이 남아있어 대차상차작업예약ID에 대차 하차작업예약을 등록함.
				 * 작업예약이 없는 상태에서 다음 작업예약을 찾도록 조회!!
				 */
				msgRecord = JDTORecordFactory.getInstance().create();
				msgRecord.setField("YD_EQP_ID", szYD_EQP_ID);

				intRtnVal = this.Y1L2TcarSch(msgRecord);
	    	}

		}catch(Exception e){
	
			szMsg="대차 하차 스케줄 Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			m_ctx.setRollbackOnly();
			throw new DAOException(szMsg);
		}
		
		szMsg="대차 하차 스케줄("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		return intRtnVal;
	} //end of Y1LdTcarSch()
	
	/**
	 * 오퍼레이션명 : 작업예약재료생성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord, rsWbooId
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public int Y1MakeWrkBook(JDTORecord msgRecord, JDTORecordSet rsWbooId)throws DAOException  {
		
		YdWrkbookMtlDao  ydWrkbookMtlDao  = new YdWrkbookMtlDao();
		YdWrkbookDao     ydWrkbookDao     = new YdWrkbookDao();
		YdSchRuleDao     ydSchRuleDao     = new YdSchRuleDao();
		YdTcarFtmvMtlDao ydTcarFtmvMtlDao = new YdTcarFtmvMtlDao();
		
		JDTORecordSet rsResult          = null;
		JDTORecordSet rsWrkBookMtl      = null;
		JDTORecordSet rsTcarFtMtl       = null;
		JDTORecord    recInTemp         = null;
		JDTORecord    recOutTemp        = null;
		JDTORecord    recPara           = null;
		
	    int intRtnVal 					= 0;
	    
	    String szMsg              		= "";
	    String szMethodName       		= "Y1MakeWrkBook";
	    String szWbookId                = "";
	    String szYdGp                   = "";
	    String szYdBayGp                = "";
	    String szCurSchCd               = "";
	    String szPreSchCd               = "";
	    String szWrkPlanTcar            = "";
	    String szSchPrior               = "";
	    String szYD_TO_LOC_DCSN_MTD		= null;
	    String szYD_TO_LOC_GUIDE		= null;
	    
	    String szRegister               = szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName;

	    try{
	    	szWbookId = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");
	    	
	    	//작업예약id로 대차스케줄을 조회하여 대차스케줄id를 조회한다.
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_CARLD_WRK_BOOK_ID", szWbookId);
	    	recInTemp.setField("YD_WBOOK_ID",          szWbookId);
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	    	intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recInTemp, rsResult, 15);
	    	if(intRtnVal < 0) {
		    	szMsg="작업예약재료와 대차이송재료Table중 재료번호가 같은것의 갯수 조회중 Error Error Code : " + rsResult.size();
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    	}
	    	rsResult.absolute(1);
	    	recOutTemp = JDTORecordFactory.getInstance().create();
	    	recOutTemp.setRecord(rsResult.getRecord());
	    	
	    	rsTcarFtMtl = JDTORecordFactory.getInstance().createRecordSet("");
	    	intRtnVal   = ydTcarFtmvMtlDao.getYdTcarftmvmtl(recOutTemp, rsTcarFtMtl, 4);
	    	if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="<Y1MakeWrkBook> ydTcarFtmvMtlDao data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);	
    			}else if(intRtnVal == -2) {
    				szMsg="<Y1MakeWrkBook> ydTcarFtmvMtlDao parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}
    			return intRtnVal = -1;
    		}
	    	
	    	//작업예약재료 Table를 조회한다.
	    	rsWrkBookMtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(msgRecord, rsWrkBookMtl, 11);
			if(intRtnVal <= 0) {
				if(intRtnVal == 0) {
					szMsg="<Y1MakeWrkBook> getYdWrkbookmtl data not found";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				}else if(intRtnVal == -2) {
					szMsg="<Y1MakeWrkBook> getYdWrkbookmtl parameter error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
    			throw new DAOException(szSessionName + " : " + szMethodName + " - " + szMsg);
			}
	    	
	    	//작업예약id로 작업예약Table를 조회한다.
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_WBOOK_ID", szWbookId);
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	intRtnVal = ydWrkbookDao.getYdWrkbook(recInTemp, rsResult, 10);
			if(intRtnVal <= 0) {
				if(intRtnVal == 0) {
					szMsg="<Y1MakeWrkBook> getYdWrkbook : data not found";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				}else if(intRtnVal == -2) {
					szMsg="<Y1MakeWrkBook> getYdWrkbook : parameter error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
    			throw new DAOException(szSessionName + " : " + szMethodName + " - " + szMsg);
			}
	    	
	    	rsResult.absolute(1);
	    	recOutTemp = JDTORecordFactory.getInstance().create();
	    	recOutTemp.setRecord(rsResult.getRecord());
	    	
	    	//야드구분 = 목표야드구분
	    	//szYdGp     				= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_AIM_YD_GP");
	    	/*
	    	 * 2010.12.30 윤재광 - 목표야드는 Default 셋팅
	    	 */
	    	szYdGp     				= "A";
	    	//야드동구분 = 목표동구분
	    	szYdBayGp     			= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_AIM_BAY_GP");
	    	//이전스케줄코드 = 스케줄코드
	    	szPreSchCd    			= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_SCH_CD");
	    	//작업계획대차  = 작업계획대차
	    	szWrkPlanTcar 			= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WRK_PLAN_TCAR");
	    	//현재스케줄코드 = 야드구분 + 야드동구분 + 이전스케줄코드.(2,4) + "TCLM;
	    	szCurSchCd    			= szYdGp + szYdBayGp + szWrkPlanTcar.substring(2,4) + "0" + szWrkPlanTcar.substring(5,6) + "LM";
	    	szYD_TO_LOC_DCSN_MTD 	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TO_LOC_DCSN_MTD");
			szYD_TO_LOC_GUIDE		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TO_LOC_GUIDE");
	    	
			/*
	    	 * 2010.12.30 윤재광 - To위치 정합성 체크 추가
	    	 */
			if("F".equals(szYD_TO_LOC_DCSN_MTD)&&
			   szYD_TO_LOC_GUIDE.length()>2){
				if(!szYdBayGp.equals(szYD_TO_LOC_GUIDE.substring(1, 2))){
					szYD_TO_LOC_DCSN_MTD 	= "S";
					szYD_TO_LOC_GUIDE		= "";
					szMsg="대차 하차 작업예약 생성중 To위치 가이드 정보 변경 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
			}
			
	    	//스케줄코드로 스케줄기준Table조회
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_SCH_CD", szCurSchCd);
	    	
	    	//스케줄 기준테이블의 항목중 야드작업크레인우선순위를 가져온다.
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	intRtnVal = ydSchRuleDao.getYdSchrule(recInTemp, rsResult, 0);
			if(intRtnVal <= 0) {
				if(intRtnVal == 0) {
					szMsg="<Y1MakeWrkBook> getYdSchrule : data not found";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				}else if(intRtnVal == -2) {
					szMsg="<Y1MakeWrkBook> getYdSchrule : parameter error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				return intRtnVal = -1;
			}
	    	
	    	rsResult.absolute(1);
	    	recOutTemp = JDTORecordFactory.getInstance().create();
	    	recOutTemp.setRecord(rsResult.getRecord());
	    	szSchPrior = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WRK_CRN_PRIOR");
	    	
	    	//작업예약id 생성
	    	//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();
	
			//================================================
			//파라미터를 설정하지 않으면 JSPEED에서 에러발생. 추후 수정요
			recPara.setField("YD_WBOOK_ID", "1");
			//================================================
			
			//작업예약 테이블의 시퀀스를 이용해 작업예약ID를 구해온다.
			rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
			intRtnVal = ydWrkbookDao.getYdWrkbook(recPara, rsResult, 1);
			if(intRtnVal <= 0) {
				if(intRtnVal == 0) {
					szMsg="<Y1MakeWrkBook> getYdWrkbook : data not found";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				}else if(intRtnVal == -2) {
					szMsg="<Y1MakeWrkBook> getYdWrkbook : parameter error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				return intRtnVal = -1;
			}
			
			rsResult.absolute(1);
	    	recOutTemp = JDTORecordFactory.getInstance().create();
	    	recOutTemp.setRecord(rsResult.getRecord());
	    	
	    	//생성한 작업예약ID
	    	szWbookId = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WBOOK_ID");
	    	
	    	//작업예약항목SETTING
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	
	    	recInTemp.setField("YD_WBOOK_ID", 			szWbookId);
	    	recInTemp.setField("REGISTER", 				szRegister);
	    	recInTemp.setField("YD_GP", 				szYdGp);
	    	recInTemp.setField("YD_BAY_GP", 			szYdBayGp);
	    	recInTemp.setField("YD_AIM_YD_GP", 			szYdGp);
	    	recInTemp.setField("YD_AIM_BAY_GP", 		szYdBayGp);
	    	recInTemp.setField("YD_SCH_PRIOR", 			szSchPrior);
	    	recInTemp.setField("YD_SCH_CD", 			szCurSchCd);
	    	recInTemp.setField("YD_WRK_PLAN_TCAR", 		szWrkPlanTcar);
	    	recInTemp.setField("YD_TO_LOC_DCSN_MTD", 	szYD_TO_LOC_DCSN_MTD);
	    	recInTemp.setField("YD_TO_LOC_GUIDE", 		szYD_TO_LOC_GUIDE);
	    	
	    	intRtnVal = ydWrkbookDao.insYdWrkbook(recInTemp);
    		if(intRtnVal == -2) {
				szMsg="parameter error";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return intRtnVal = -1;
    		}
	    	
    		rsWbooId.addRecord(recInTemp);

	    	//작업예약재료 등록
	    	for(int Loop_i = 1; Loop_i <= rsTcarFtMtl.size(); Loop_i++) {
	    		rsTcarFtMtl.absolute(Loop_i);
	    		recInTemp  = JDTORecordFactory.getInstance().create();
	    		recOutTemp = JDTORecordFactory.getInstance().create();
	    		recOutTemp.setRecord(rsTcarFtMtl.getRecord());
	    		
	    		recInTemp.setField("YD_WBOOK_ID",    szWbookId);
	    		recInTemp.setField("REGISTER",       szRegister);
	    		recInTemp.setField("YD_STK_COL_GP",  szYdGp + szYdBayGp + szWrkPlanTcar.substring(2));
	    		recInTemp.setField("YD_STK_BED_NO",  "01");
	    		recInTemp.setField("YD_STK_LYR_NO",  ydDaoUtils.stringPlusInt("000", Loop_i));
	    		recInTemp.setField("STL_NO",         ydDaoUtils.paraRecChkNull(recOutTemp, "STL_NO"));
	    		recInTemp.setField("YD_UP_COLL_SEQ", "" + (rsTcarFtMtl.size()- Loop_i + 1) );
	    		intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recInTemp);
	    		if(intRtnVal == -2) {
					szMsg="parameter error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return intRtnVal = -1;
	    		}
	    	}
	    		
		}catch(Exception e){
			szMsg="작업예약생성 Error :" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			m_ctx.setRollbackOnly();
			throw new DAOException(szMsg);
		}
	
		szMsg="작업예약생성("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		return intRtnVal = 1;
	} //end of Y1MakeWrkBook()
	

	/**
	 * 오퍼레이션명 : 후판 대차 스케줄 
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return Integer
	 * @throws JDTOException
	 */
	public Integer procY3TcarSch(JDTORecord msgRecord)throws DAOException  {
		
	    int intRtnVal 					= 0 ;
	    
	    String szMsg              		= "";
	    String szMethodName       		= "procY3TcarSch";
	    String szOperationName			= "후판대차스케줄";
	    
	    //상하차 구분
	    String szLdUdGp					= "";
	    
        String szRcvTcCode=ydUtils.getTcCode(msgRecord);
        if(szRcvTcCode==null || szRcvTcCode.equals("")){
        	szMsg="["+szOperationName+"] TC Code Error ("+szRcvTcCode+")";
        	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
        	return YdConstant.RETN_INT_TC_ERROR;
        }
        
        if(bDebugFlag){
            szMsg="["+szOperationName+"] [DEBUG] 전문수신 : TCCODE=" +szRcvTcCode ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }
	    
	    try{
	    	szMsg="["+szOperationName+"] ------------------- 메소드 시작 -------------------";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            
            szLdUdGp = ydDaoUtils.paraRecChkNull(msgRecord, "YD_LD_UD_GP");
	    	
	    	//------------------------------------------------------------------------------------------
    		//권상 실적 처리중 호출한 경우 - 하차 시
	    	//------------------------------------------------------------------------------------------
    		if( szLdUdGp.equals("U") ) {
    			
    			szMsg="["+szOperationName+"] 권상실적 처리 시 호출 - 대차하차스케줄 모듈 시작";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			
    			//대차 하차스케줄 호출
    			intRtnVal = this.Y3LdTcarSch(msgRecord);
    			
    			szMsg="["+szOperationName+"] 권상실적 처리 시 호출 - 대차하차스케줄 모듈 완료 - 반환값 : " + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			
    		//------------------------------------------------------------------------------------------
    		//권하 실적 처리중 호출한 경우 - 상차 시
    		//------------------------------------------------------------------------------------------
    		}else if( szLdUdGp.equals("L") ){
    			
    			szMsg="["+szOperationName+"] 권하실적 처리 시 호출 - 대차상차스케줄 모듈 시작";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			
    			//대차 상차 스케줄 호출
    			intRtnVal = this.Y3UdTcarSch(msgRecord);
    			
    			szMsg="["+szOperationName+"] 권하실적 처리 시 호출 - 대차상차스케줄 모듈 완료 - 반환값 : " + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			
    		}else{
    			//------------------------------------------------------------------------------------------
    			//화면에서 송신한 경우 - 공대차출발지시
    			//------------------------------------------------------------------------------------------
    			szMsg="["+szOperationName+"] 화면에서 호출 - 공대차출발스케줄 모듈 시작";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			
	    		intRtnVal = this.Y3L2TcarSch(msgRecord);
	    		
	    		szMsg="["+szOperationName+"] 화면에서 호출 - 공대차출발스케줄 모듈 완료 - 반환값 : " + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		
			}
		}catch(Exception e){
	
			szMsg="["+szOperationName+"] 예외 발생 : " +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			m_ctx.setRollbackOnly();
			throw new DAOException(szMsg);
		}
	
		szMsg="["+szOperationName+"] ------------------- 메소드 끝 -------------------";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        
		return  YdConstant.RETN_INT_SUCCESS;
	} //end of procY3TcarSch()
	
	/**
	 * 오퍼레이션명 : 대차 하차 스케줄(공대차 출발지시)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public int 	Y3L2TcarSch(JDTORecord msgRecord)throws DAOException  {
		
		//동간이적요구시 화면에서  송신하는 경우
		YdTcarSchDao     ydTcarSchDao     = new YdTcarSchDao();
		YdEqpDao         ydEqpDao         = new YdEqpDao();
		YdSchRuleDao     ydSchRuleDao     = new YdSchRuleDao();
		YdWrkbookDao     ydWrkbookDao     = new YdWrkbookDao();
		
		JDTORecordSet rsResult          = null;
		JDTORecordSet rsResult1         = null;
		JDTORecord    recInTemp         = null;
		JDTORecord    recOutTemp        = null;
		JDTORecord    recTcar           = null;
		JDTORecord    recWbook          = null;
	    int intRtnVal 					= 0 ;
	    
	    String szMsg              		= "";
	    String szMethodName       		= "Y3L2TcarSch";
	    String szOperationName			= "공대차스케줄";
	    
	    String szTcarEqpId              = "";
	    String szWbookId                = "";
	    String szYdGp					= "";
	    String szYdBayGp				= "";
	    //String szYdAimYdGp			= "";
	    String szYdAimBayGp				= "";
	    String szYdWrkPlanTcar			= "";
	    String szYdSchCd                = "";
	    String szYD_TCAR_CURR_BAY_GP    = "";
	    String szYD_TCAR_SCH_ID         = "";
	    String szYD_TO_BAY              = "";
	    String szYD_CARLD_WRK_BOOK_ID   = "";
	    String szYD_CARLD_STOP_LOC      = "";					//상차정지위치
	    String szYD_CARUD_STOP_LOC		= null;					//하차정지위치
	    String szYD_WBOOK_ID            = "";
	    String szRegister				= "SYSTEM";
	    String szYD_HOME_BAY_GP         = "";
	    String[] szTCarRule				= null;
	    String szUSAGE_YN				= "";
	    String szWORK_GP				= "";
	    String szYD_CARLD_BAY_GP		= "";
	    String szYD_CARUD_BAY_GP		= "";
	    boolean bAfterQuery				= false;
	    
	    try{
	    	szMsg="["+szOperationName+"] ------------------- 메소드 시작 -------------------";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            
	    	szTcarEqpId 		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");	//대차설비ID
	    	szYD_TO_BAY 		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_TO_BAY");	//공대차출발 시 사용자가 지정한 목표동(값 없을수 있슴)
	    	szYD_WBOOK_ID 		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");	//작업예약ID(값 없을수 있슴)
	    	
	    	//-------------------------------------------------------------------------------------
	    	//	대차설비ID로 설비테이블을 조회한다.
	    	//-------------------------------------------------------------------------------------
	    	szMsg="["+szOperationName+"] 대차설비ID(" + szTcarEqpId + ")로 설비테이블 조회 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
    		rsResult1 = JDTORecordFactory.getInstance().createRecordSet("");
    		
    		recInTemp = JDTORecordFactory.getInstance().create();
    		recInTemp.setField("YD_EQP_ID", 			szTcarEqpId);
    		recInTemp.setField("YD_TCAR_SCH_ID", 		szYD_TCAR_SCH_ID);

    		intRtnVal = ydEqpDao.getYdEqp(recInTemp, rsResult1, 0);
    		
    		szMsg="["+szOperationName+"] 대차설비ID(" + szTcarEqpId + ")로 설비테이블 조회 완료 - 반환값 : " + intRtnVal;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			if(intRtnVal <= 0) {
    			szMsg="["+szOperationName+"] 대차설비ID(" + szTcarEqpId + ")로 설비테이블 조회 시 오류발생 - 반환값 : " + intRtnVal;
    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			return intRtnVal = -1;
			}
			
			rsResult1.absolute(1);
    		recOutTemp = JDTORecordFactory.getInstance().create();
    		recOutTemp.setRecord(rsResult1.getRecord());
    		
    		szYD_TCAR_CURR_BAY_GP 	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CURR_BAY_GP");	//현재동
	    	szYD_HOME_BAY_GP 		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_HOME_BAY_GP");	//홈동
    		//-------------------------------------------------------------------------------------
	    	
	    	if(!szYD_WBOOK_ID.equals("")) {
	    		
	    		//-------------------------------------------------------------------------------------
	    		//	파라미터로 전달된 작업예약ID가 존재하면 작업예약을 조회한다.
	    		//-------------------------------------------------------------------------------------
	    		szMsg="["+szOperationName+"] 파라미터로 전달된 작업예약[" + szYD_WBOOK_ID + "]을 조회 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		
				rsResult  			= JDTORecordFactory.getInstance().createRecordSet("");
	    		recInTemp 			= JDTORecordFactory.getInstance().create();
	    		
	    		recInTemp.setField("YD_WBOOK_ID", 			szYD_WBOOK_ID);
	    		
	    		intRtnVal = ydWrkbookDao.getYdWrkbook(recInTemp, rsResult, 0);
	    		if(intRtnVal <= 0 ){
	    			szMsg= "["+szOperationName+"] 파라미터로 전달된 작업예약[" + szYD_WBOOK_ID + "]을 조회 시 오류발생 - 반환값 : " + intRtnVal;
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    			return intRtnVal = -1;
	    		}
	    		
	    		szMsg="["+szOperationName+"] 파라미터로 전달된 작업예약[" + szYD_WBOOK_ID + "]을 조회 완료 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		
	    		rsResult.absolute(1);
	    		recWbook = JDTORecordFactory.getInstance().create();
	    		recWbook.setRecord(rsResult.getRecord());
	    		
	    		szYdBayGp       = ydDaoUtils.paraRecChkNull(recWbook, "YD_BAY_GP");						//동구분
	    		
		    	szYdGp          = ydDaoUtils.paraRecChkNull(recWbook, "YD_GP");							//야드구분
		    	szYdAimBayGp    = ydDaoUtils.paraRecChkNull(recWbook, "YD_AIM_BAY_GP");					//목표동구분
		    	szYdWrkPlanTcar = ydDaoUtils.paraRecChkNull(recWbook, "YD_WRK_PLAN_TCAR");				//작업계획대차
		    	szYdSchCd       = ydDaoUtils.paraRecChkNull(recWbook, "YD_SCH_CD");						//스케줄코드
		    	//-------------------------------------------------------------------------------------
		    	
	    	}else{
	    		
	    		if( !szYD_TO_BAY.equals("") ) {
	    			
	    			szYdBayGp	= szYD_TO_BAY;
	    			
	    			szMsg="["+szOperationName+"] 해당 대차["+szTcarEqpId+"]를 사용자가 지정한 동["+szYD_TO_BAY+"]으로 공대차출발 처리";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		}else{
	    	    	szMsg="["+szOperationName+"] 해당 대차["+szTcarEqpId+"]에 대해서 일반적인 규칙인 우선순위가 빠르고, 작업예약순서가 빠른 대차상차작업예약을 조회한다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	    	
		    		//-------------------------------------------------------------------------------------
			    	//	파라미터로 전달된 작업예약ID가 존재하지 않으면 
		    		//	작업계획대차로 우선순위가 빠르고, 작업예약순서가 빠른 대차상차작업예약만 조회
		    		//-------------------------------------------------------------------------------------
		    		szMsg="["+szOperationName+"] 해당 대차["+szTcarEqpId+"]로 등록된 대차상차작업예약을 조회 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    		
			    	msgRecord.setField("YD_WRK_PLAN_TCAR", 				szTcarEqpId);
			    	
			    	rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
		    		intRtnVal = ydWrkbookDao.getYdWrkbook(msgRecord, rsResult, 27);
		    		
		    		szMsg="["+szOperationName+"] 해당 대차["+szTcarEqpId+"]로 등록된 대차상차작업예약을 조회 완료 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    		
		    		if(intRtnVal > 0) {
				    	rsResult.first();
				    	recOutTemp = JDTORecordFactory.getInstance().create();
				    	recOutTemp.setRecord(rsResult.getRecord());
				    	
				    	szYD_WBOOK_ID   = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WBOOK_ID");				//작업예약ID
				    	szYdGp          = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_GP");					//야드구분
				    	szYdBayGp       = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_BAY_GP");				//동구분
				    	szYdAimBayGp    = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_AIM_BAY_GP");			//목표동구분
				    	szYdWrkPlanTcar = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WRK_PLAN_TCAR");		//작업계획대차
				    	szYdSchCd       = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_SCH_CD");				//스케줄코드
		    		}else{
		    			
		    			szYdBayGp		= szYD_HOME_BAY_GP;
		    			
		    			szMsg="["+szOperationName+"] 대차와 관련된 작업예약이 없는 경우에는 홈동["+szYD_HOME_BAY_GP+"]으로 공대차 출발 지시를 처리.";
		    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    		}
	    		}
	    	}
	    	
	    	//-------------------------------------------------------------------------------------
	    	//	대차설비ID로 대차스케줄을 조회한다.
	    	//-------------------------------------------------------------------------------------
	    	
			szMsg="["+szOperationName+"] 대차설비ID(" + szTcarEqpId + ")로 대차스케줄 조회 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			
			recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_EQP_ID", 		szTcarEqpId);
	    	
	    	intRtnVal = ydTcarSchDao.getYdTcarsch(recInTemp, rsResult, 4);
	    	
	    	szMsg="["+szOperationName+"] 대차설비ID(" + szTcarEqpId + ")로 대차스케줄 조회 완료 - 반환값 : " + intRtnVal;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	//-------------------------------------------------------------------------------------
	    	
	    	//-------------------------------------------------------------------------------------
	    	//	만약 대차스케줄이 존재하면 대차 스케줄을 기동 하지 않는다.(공대차인경우는 제외)
	    	//-------------------------------------------------------------------------------------
	    	
	    	if(intRtnVal > 0) {
	    		
	    		//-------------------------------------------------------------------------------------
		    	//	대차스케줄이 존재
	    		//-------------------------------------------------------------------------------------
	    		rsResult.absolute(1);
	    		recTcar = JDTORecordFactory.getInstance().create();
	    		recTcar.setRecord(rsResult.getRecord());
	    		
	    		//상차작업예약 id가 있는지 없는지 Check!!!
	    		szYD_CARLD_WRK_BOOK_ID = ydDaoUtils.paraRecChkNull(recTcar, "YD_CARLD_WRK_BOOK_ID");
	    		szYD_CARLD_STOP_LOC    = ydDaoUtils.paraRecChkNull(recTcar, "YD_CARLD_STOP_LOC");
	    		szYD_TCAR_SCH_ID       = ydDaoUtils.paraRecChkNull(recTcar, "YD_TCAR_SCH_ID");
	    		
	    		szMsg="["+szOperationName+"] 대차설비ID(" + szTcarEqpId + ")로 대차스케줄["+szYD_TCAR_SCH_ID+"]이 존재하는 경우 - 이미 등록된 상차작업예약["+szYD_CARLD_WRK_BOOK_ID+"], 상차정지위치["+szYD_CARLD_STOP_LOC+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		
	    		if(!szYD_CARLD_WRK_BOOK_ID.equals("")) {
	    			
	    			//-------------------------------------------------------------------------------------
		    		//	대차스케줄의 상차작업예약이 존재하는 경우
		    		//-------------------------------------------------------------------------------------
					szMsg="["+szOperationName+"] 대차[" + szTcarEqpId + "]스케줄["+szYD_TCAR_SCH_ID+"]이 존재하고 이미 등록된 상차작업예약["+szYD_CARLD_WRK_BOOK_ID+"]이 존재하므로 대차스케줄["+szYD_TCAR_SCH_ID+"]을 기동할 수 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					return intRtnVal = 1;
					//-------------------------------------------------------------------------------------
					
	    		}else if (szYD_CARLD_WRK_BOOK_ID.equals("") && szYD_TO_BAY.equals("")){
	    			
		    		//-------------------------------------------------------------------------------------
		    		//	대차스케줄 업데이트항목
		    		//-------------------------------------------------------------------------------------
		    		szYD_CARLD_STOP_LOC			= "D" + szYdBayGp 	+ szTcarEqpId.substring(2,6);
		    		szYD_CARUD_STOP_LOC			= "D" + szYdAimBayGp+ szTcarEqpId.substring(2,6);
		    		
		    		recInTemp = JDTORecordFactory.getInstance().create();
		    		recInTemp.setField("YD_TCAR_SCH_ID", 				szYD_TCAR_SCH_ID);
			    	recInTemp.setField("YD_CARLD_SCH_REQ_GP", 			"6");
					recInTemp.setField("YD_CARUD_SCH_REQ_GP", 			"3");
					recInTemp.setField("YD_CARLD_WRK_BOOK_ID", 			szYD_WBOOK_ID);				//상차작업예약
					recInTemp.setField("YD_CARLD_STOP_LOC", 			szYD_CARLD_STOP_LOC);		//상차정지위치
					if( !szYdAimBayGp.equals("") ) {
						recInTemp.setField("YD_CARUD_STOP_LOC", 		szYD_CARUD_STOP_LOC);		//하차정지위치
					}
					
		    		if(szYdBayGp.equals(szYD_TCAR_CURR_BAY_GP)) {
		    			
		    			//-------------------------------------------------------------------------------------
						//	상차정지위치와 대차설비의 현재동이 같은경우 상차도착상태로 변경, 크레인스케줄 호출
						//-------------------------------------------------------------------------------------
		    			szMsg="["+szOperationName+"] 상차정지위치동["+szYdBayGp+"]와 대차설비의 현재동["+szYD_TCAR_CURR_BAY_GP+"]이 같은 경우는 대차스케줄["+szYD_TCAR_SCH_ID+"]에 상차작업예약["+szYD_WBOOK_ID+"], 상차도착 상태[2]로 등록 후 크레인스케줄 호출";
		    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    			
		    			recInTemp.setField("YD_CAR_PROG_STAT", 			"2");	//상차도착
		    			intRtnVal = ydTcarSchDao.updYdTcarsch(recInTemp, 0);
		    			if(intRtnVal <= 0) {
			    			szMsg="["+szOperationName+"] 대차스케줄["+szYD_TCAR_SCH_ID+"]에 상차작업예약["+szYD_WBOOK_ID+"], 상차도착 상태[2], 상차정지위치["+szYD_CARLD_STOP_LOC+"], 하차정지위치["+szYD_CARUD_STOP_LOC+"] 등록 시 오류발생 - 반환값 : " + intRtnVal;
			    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			    			return intRtnVal = -1;
		    			}
		    			
		    			if( !szYD_WBOOK_ID.equals("") ){
		    				
			    			recInTemp = JDTORecordFactory.getInstance().create();
			    			recInTemp.setField("MSG_ID",    	"YDYDJ503");
			    			recInTemp.setField("YD_SCH_CD", 	szYdSchCd);
			    			
			    			//-------------------------------------------------------------------------------------
							//	크레인설비ID를 구하기 위해서 스케줄기준 조회
							//-------------------------------------------------------------------------------------
			    			szMsg="["+szOperationName+"] 크레인설비ID를 구하기 위해서 스케줄코드["+szYdSchCd+"]로 스케줄기준 조회 시작";
			    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			    			
			    			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			    			intRtnVal = ydSchRuleDao.getYdSchrule(recInTemp, rsResult, 0);
			    			if(intRtnVal <= 0) {
			        			szMsg="["+szOperationName+"] 크레인설비ID를 구하기 위해서 스케줄코드["+szYdSchCd+"]로 스케줄기준 조회 시 오류발생 - 반환값 : " + intRtnVal;
			        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			        			throw new DAOException(szMsg);
			    			}
			    			
			    			szMsg="["+szOperationName+"] 크레인설비ID를 구하기 위해서 스케줄코드["+szYdSchCd+"]로 스케줄기준 조회 완료";
			    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			    			
			    			rsResult.absolute(1);
			    			recOutTemp = JDTORecordFactory.getInstance().create();
			        		recOutTemp.setRecord(rsResult.getRecord());
			    			//-------------------------------------------------------------------------------------
			        		
			        		//-------------------------------------------------------------------------------------
			    			//	대차가 이미 도착한 상태이므로 크레인 스케줄 호출
			        		//-------------------------------------------------------------------------------------
			        		szMsg="["+szOperationName+"] 상차정지위치동["+szYdBayGp+"]와 대차설비의 현재동["+szYD_TCAR_CURR_BAY_GP+"]이 같은 경우는 대차가 이미 도착한 상태이므로 크레인 스케줄 호출 시작";
			    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			    			
			    			recInTemp.setField("YD_EQP_ID", ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WRK_CRN"));
			    			
			    			ydDelegate.sendMsg(recInTemp);
			    			
			    			szMsg="["+szOperationName+"] 상차정지위치동["+szYdBayGp+"]와 대차설비의 현재동["+szYD_TCAR_CURR_BAY_GP+"]이 같은 경우는 대차가 이미 도착한 상태이므로 크레인 스케줄 호출 완료 - 공대차출발스케줄 종료";
			    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			    			
			    			//-------------------------------------------------------------------------------------
		    			}else{
		    				szMsg="["+szOperationName+"] 상차작업예약["+szYD_WBOOK_ID+"]이 존재하지 않으므로 크레인 스케줄 호출하지 않음";
			    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    			}
		    			
		    		}else{
		    			
		    			//공대차이지만 다른동에 있을경우에는 대차스케줄 상차동을 현재동으로 등록한 후 공대차출발지시 등록 
		    			//-------------------------------------------------------------------------------------
		    			//	대차설비의 현재동과 대차상차작업예약의 상차정지위치가 다른 경우 공대차출발지시를 L2로 전송
		    			//-------------------------------------------------------------------------------------
		    			szMsg="["+szOperationName+"] 대차설비[" + szTcarEqpId + "]의 현재동["+szYD_TCAR_CURR_BAY_GP+"]과 대차상차작업예약의 상차정지위치동["+szYdBayGp+"]이 다른 경우 공대차출발지시를 L2로 전송";
		    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    			
		    			szMsg="["+szOperationName+"] 상차정지위치동["+szYdBayGp+"]와 대차설비[" + szTcarEqpId + "]의 현재동["+szYD_TCAR_CURR_BAY_GP+"]이 다른 경우는 대차스케줄["+szYD_TCAR_SCH_ID+"]에 상차작업예약["+szYD_WBOOK_ID+"], 공차대기 상태[0]로 등록 시작";
		    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    			
		    			recInTemp.setField("YD_CAR_PROG_STAT", "0");	//공차대기 - 공차출발실적에서 상차출발로 변경함.

		    			intRtnVal = ydTcarSchDao.updYdTcarsch(recInTemp, 0);
		    			
		    			if(intRtnVal <= 0) {
		    				szMsg="["+szOperationName+"] 상차정지위치동["+szYdBayGp+"]와 대차설비의 현재동["+szYD_TCAR_CURR_BAY_GP+"]이 다른 경우는 대차스케줄["+szYD_TCAR_SCH_ID+"]에 상차작업예약["+szYD_WBOOK_ID+"], 공차대기 상태[0]로 등록 시 오류발생 - 반환값 : " + intRtnVal;
			    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			    			return intRtnVal = -1;
		    			}
		    			
		    			szMsg="["+szOperationName+"] 상차정지위치동["+szYdBayGp+"]와 대차설비의 현재동["+szYD_TCAR_CURR_BAY_GP+"]이 다른 경우는 대차스케줄["+szYD_TCAR_SCH_ID+"]에 상차작업예약["+szYD_WBOOK_ID+"], 공차대기 상태[0]로 등록 완료 - 반환값 : " + intRtnVal;
		    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    			//-------------------------------------------------------------------------------------
		    			
		    			//-------------------------------------------------------------------------------------
						//	공대차출발지시를 한다.야드구분, 야드스케줄코드, 대차스케줄ID
		    			//-------------------------------------------------------------------------------------
		    			recInTemp = JDTORecordFactory.getInstance().create();
			    		recInTemp.setField("MSG_ID", 				"YDY3L006");
			    		recInTemp.setField("YD_GP", 				"D");
			    		recInTemp.setField("YD_SCH_CD", 			"");
			    		recInTemp.setField("YD_TCAR_SCH_ID", 		szYD_TCAR_SCH_ID);
			    		
			    		ydDelegate.sendMsg(recInTemp);
			    		
						szMsg="["+szOperationName+"] 상차정지위치동["+szYdBayGp+"]으로 공대차 출발지시[상차출발] 전송 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						//-------------------------------------------------------------------------------------
		    		}

	    		}else if (szYD_CARLD_WRK_BOOK_ID.equals("") && !szYD_TO_BAY.trim().equals("")){
	    			
	    			//-------------------------------------------------------------------------------------
	    			//	대차스케줄의 상차작업예약이 등록되어 있지 않고 사용자가 지정한 목표동의 값이 존재하는 경우
	    			//-------------------------------------------------------------------------------------
	    			szMsg="["+szOperationName+"] 대차스케줄["+szYD_TCAR_SCH_ID+"]의 상차작업예약이 등록되어 있지 않고 사용자가 지정한 목표동["+szYD_TO_BAY+"]이 존재하는 경우는 대차스케줄["+szYD_TCAR_SCH_ID+"]에 공차대기 상태[0]로 등록 후 공대차출발 지시 전송";
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			
	    			recTcar.setField("YD_CAR_PROG_STAT", 			"0");					//공차대기
	    			recTcar.setField("YD_CARLD_STOP_LOC", 			"D" + szYD_TO_BAY + szTcarEqpId.substring(2,6)); //사용자지정위치
	    			intRtnVal = ydTcarSchDao.updYdTcarsch(recTcar, 0);
	    			if(intRtnVal <= 0) {
	    				szMsg="["+szOperationName+"] 대차스케줄["+szYD_TCAR_SCH_ID+"]의 상차작업예약이 등록되어 있지 않고 사용자가 지정한 목표동["+szYD_TO_BAY+"]이 존재하는 경우는 대차스케줄["+szYD_TCAR_SCH_ID+"]에 공차대기 상태[0]로 등록 시 오류발생 - 반환값 : " + intRtnVal;
		    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		    			return intRtnVal = -1;
	    			}
	    			//-------------------------------------------------------------------------------------
	    			
	    			//-------------------------------------------------------------------------------------
					//공대차출발지시를 한다.야드구분, 야드스케줄코드, 대차스케줄ID
	    			//-------------------------------------------------------------------------------------
	    			recInTemp = JDTORecordFactory.getInstance().create();
		    		recInTemp.setField("MSG_ID", 			"YDY3L006");
		    		recInTemp.setField("YD_GP", 			"D");
		    		recInTemp.setField("YD_SCH_CD", 		"");
		    		recInTemp.setField("YD_TCAR_SCH_ID", 	szYD_TCAR_SCH_ID);
		    		
		    		ydDelegate.sendMsg(recInTemp);
		    		
		    		szMsg="["+szOperationName+"] 사용자 지정동["+szYD_TO_BAY+"]으로 공대차 출발지시[공차대기] 전송 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					//-------------------------------------------------------------------------------------
	    		}

	    	}else{
	    		
	    		//-------------------------------------------------------------------------------------
	    		//	대차스케줄이 없는 경우!!
	    		//-------------------------------------------------------------------------------------
	    		String szYD_LD_UD_GP			= ydDaoUtils.paraRecChkNull(msgRecord, "YD_LD_UD_GP");
	    		
	    		szMsg="["+szOperationName+"] 대차스케줄이 존재하지 않는 경우 - YD_LD_UD_GP["+szYD_LD_UD_GP+"], YD_WBOOK_ID["+szYD_WBOOK_ID+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		
	    		if( szYD_LD_UD_GP.equals("U") ) {
	    			szMsg="["+szOperationName+"] 파라미터로 전달된 권상/권하실적처리 호출구분["+szYD_LD_UD_GP+"]이 권상실적처리인 경우 - 하차작업이 끝나고 다음 작업예약을 찾는경우";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		}else{
	    			szMsg="["+szOperationName+"] 파라미터로 전달된 권상/권하실적처리 호출구분["+szYD_LD_UD_GP+"]이 없는 경우 - 권상실적처리 시";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		}
	    		
	    		//작업계획대차설정
		    	msgRecord.setField("YD_WRK_PLAN_TCAR", szTcarEqpId);
		    	
		    	intRtnVal		= 0;

		    	if( szYD_WBOOK_ID.equals("") ) {
		    		
					szMsg="intRtnVal = " + rsResult.size();
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					szMsg="["+szOperationName+"] 해당 대차설비[" + szTcarEqpId + "]의 작업예약이 등록되어 있지 않으므로 대차스케줄 생성 후 현재동과 홈동이 다르면 공대차출발지시를 전송한다.";
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
					 * 2009.06.29 김진욱
					 * 대차 하차 후 다음 대차작업을 찾을때 다음 대차 작업이 없는 경우 현재위치에 대차 스케줄을 생성한다.
					 ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
		    		
		    		//대차스케줄을 생성한다.
		    		//대차스케줄1개를 생성하고 해당 작업예약id를 상차작업예약id에 등록한다.
			    	recInTemp = JDTORecordFactory.getInstance().create();
			    	recInTemp.setField("YD_TCAR_SCH_ID", "1");
			    	rsResult1 = JDTORecordFactory.getInstance().createRecordSet("Temp");
			    	intRtnVal = ydTcarSchDao.getYdTcarsch(recInTemp, rsResult1, 5);
			    	rsResult1.absolute(1);
			    	recOutTemp = JDTORecordFactory.getInstance().create();
			    	recOutTemp.setRecord(rsResult1.getRecord());
			    	szYD_TCAR_SCH_ID = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TCAR_SCH_ID");
			    	
			    	recInTemp = JDTORecordFactory.getInstance().create();
			    	//대차스케줄생성
			    	recInTemp.setField("YD_TCAR_SCH_ID", 				szYD_TCAR_SCH_ID);
			    	recInTemp.setField("REGISTER", 						szRegister);
		    		
			    	//현재위치가 상차정지위치로등록한다.
		    		
			    	boolean bSEND_START		= false;
			    	
			    	if(szYD_TO_BAY.equals("")){
			    		
			    		//대차상차정지위치-- 상차작업예약이 없고 to동이 지정되어 오지 않은경우에는 홈동으로 보내기위해...
			    		szMsg="["+szOperationName+"] 지정목표동이 존재하지 않으므로 해당 대차설비[" + szTcarEqpId + "]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 상차정지위치를 홈동["+szYD_HOME_BAY_GP+"], 공차대기[0]으로 지정한다.";
		    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				    					    	
				    	if( szUSAGE_YN.equals("Y")) {				//사용
			    	    	if( szWORK_GP.equals("D") ) {			//직상차
			    	    		szMsg="["+szOperationName+"] 지정목표동이 존재하지 않으므로 해당 대차설비[" + szTcarEqpId + "]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 상차정지위치를 홈동["+szYD_HOME_BAY_GP+"]이 아닌 Rule의 직상차 상차동["+szYD_CARLD_BAY_GP+"]으로 설정한다.";
				    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			    	    		szYD_HOME_BAY_GP			= szYD_CARLD_BAY_GP;
			    	    	}
			    		}
			    		
				    	recInTemp.setField("YD_CARLD_STOP_LOC", 			szTcarEqpId.substring(0,1) + szYD_HOME_BAY_GP + szTcarEqpId.substring(2));
				    	
				    	if( !szYdAimBayGp.equals("") ) {
				    		recInTemp.setField("YD_CARUD_STOP_LOC", 			szTcarEqpId.substring(0,1) + szYdAimBayGp + szTcarEqpId.substring(2));		//하차정지위치
				    	}
				    	
				    	//------------------------------------------------
				    	recInTemp.setField("YD_CAR_PROG_STAT", 			"0");
				    	//------------------------------------------------
				    	
				    	if( !szYD_HOME_BAY_GP.equals(szYD_TCAR_CURR_BAY_GP) ) {
				    		bSEND_START			= true;
				    		
				    		szMsg="["+szOperationName+"] 해당 대차설비[" + szTcarEqpId + "]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 상차정지위치를 홈동["+szYD_HOME_BAY_GP+"]과 현재동["+szYD_TCAR_CURR_BAY_GP+"]이 다르므로 공대차출발지시 전송한다.";
			    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				    	}
			    	}else{
				    	//대차상차정지위치-- to동이 지정되어 왔을 경우에는 To동에 상차위치로 잡고 대차출발지시를 내린다.
			    		szMsg="["+szOperationName+"] 지정목표동["+szYD_TO_BAY+"]이 존재하므로 해당 대차설비[" + szTcarEqpId + "]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 상차정지위치를 지정목표동["+szYD_TO_BAY+"], 공차대기[0]으로 지정한다.";
		    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				    	recInTemp.setField("YD_CARLD_STOP_LOC", szTcarEqpId.substring(0,1) + szYD_TO_BAY + szTcarEqpId.substring(2));
				    	
				    	//------------------------------------------------
				    	recInTemp.setField("YD_CAR_PROG_STAT", "0");	
				    	//------------------------------------------------
				    	
				    	if( !szYD_TO_BAY.equals(szYD_TCAR_CURR_BAY_GP) ) {
				    		bSEND_START			= true;
				    		
				    		szMsg="["+szOperationName+"] 해당 대차설비[" + szTcarEqpId + "]의 대차스케줄["+szYD_TCAR_SCH_ID+"]의 상차정지위치를 지정목표동["+szYD_TO_BAY+"]과 현재동["+szYD_TCAR_CURR_BAY_GP+"]이 다르므로 공대차출발지시 전송한다.";
			    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				    	}
				    	
			    	}
			    	
			    	recInTemp.setField("YD_CARLD_SCH_REQ_GP", "6");
					recInTemp.setField("YD_CARUD_SCH_REQ_GP", "3");
					
			    	//설비작업상태값( 공차로 등록 )
			    	recInTemp.setField("YD_EQP_WRK_STAT", "U");
		    		
			    	//야드설비ID
			    	recInTemp.setField("YD_EQP_ID", szTcarEqpId);
			    	intRtnVal = ydTcarSchDao.insYdTcarsch(recInTemp);
			    	
		    		if(intRtnVal == -2) {
						szMsg="["+szOperationName+"] parameter error[1]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return intRtnVal = -1;
		    		}
		    		
					szMsg="["+szOperationName+"] 대차하차 후 새로운 대차 작업이 없어 대차스케줄만 생성!!";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					//홈동과 현재동이 틀릴경우 공대차 출발지시를 내린다.
					if( bSEND_START ) {
						//공대차출발지시를 한다.야드구분, 야드스케줄코드, 대차스케줄ID
			    		recInTemp = JDTORecordFactory.getInstance().create();
			    		recInTemp.setField("MSG_ID", 			"YDY3L006");
			    		recInTemp.setField("YD_GP", 			"D");
			    		recInTemp.setField("YD_SCH_CD", 		"");
			    		recInTemp.setField("YD_TCAR_SCH_ID", 	szYD_TCAR_SCH_ID);
			    		
			    		ydDelegate.sendMsg(recInTemp);
			    		
						szMsg="["+szOperationName+"] 공대차 출발지시 전송 완료[1]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
					
		    	}else{
			    	//----------------------------------------------------------------------------------------
			    	//	대차 작업예약이 존재하는 경우
			    	//----------------------------------------------------------------------------------------
			    	szMsg="["+szOperationName+"] 해당 대차설비[" + szTcarEqpId + "]의 작업예약["+szYD_WBOOK_ID+"]이 등록되어 있으므로 대차스케줄 생성 후 현재동과 목표동이 다르면 공대차출발지시를 전송한다.";
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			    	
			    	//대차스케줄1개를 생성하고 해당 작업예약id를 상차작업예약id에 등록한다.
			    	recInTemp = JDTORecordFactory.getInstance().create();
			    	recInTemp.setField("YD_TCAR_SCH_ID", "1");
			    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
			    	intRtnVal = ydTcarSchDao.getYdTcarsch(recInTemp, rsResult, 5);
			    	rsResult.absolute(1);
			    	recOutTemp = JDTORecordFactory.getInstance().create();
			    	recOutTemp.setRecord(rsResult.getRecord());
			    	szYD_TCAR_SCH_ID = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TCAR_SCH_ID");
			    	
			    	recInTemp = JDTORecordFactory.getInstance().create();
			    	//대차스케줄생성
			    	recInTemp.setField("YD_TCAR_SCH_ID", 				szYD_TCAR_SCH_ID);
			    	recInTemp.setField("YD_CARLD_WRK_BOOK_ID", 			szYD_WBOOK_ID);
			    	recInTemp.setField("REGISTER", 						szRegister);
			    	
			    	//대차상차정지위치, 하차정지위치
			    	recInTemp.setField("YD_CARLD_STOP_LOC", 			"D" + szYdBayGp + szYdWrkPlanTcar.substring(2,6));
			    	if( !szYdAimBayGp.equals("") ) {
			    		recInTemp.setField("YD_CARUD_STOP_LOC", 		"D" + szYdAimBayGp + szYdWrkPlanTcar.substring(2,6));
			    	}
			    	recInTemp.setField("YD_CARLD_SCH_REQ_GP", 			"6");
					recInTemp.setField("YD_CARUD_SCH_REQ_GP", 			"3");
					
					if(szYD_TCAR_CURR_BAY_GP.equals(szYdBayGp)) {
						//현재동과 상차동이 같으므로 상차도착 상태로 등록
						recInTemp.setField("YD_CAR_PROG_STAT", 				"2");
					}else{
						//현재동과 상차동이 다르므로 공차대기 상태로 등록
						recInTemp.setField("YD_CAR_PROG_STAT", 				"0");
					}
					//설비작업상태값( 공차로 등록 )
			    	recInTemp.setField("YD_EQP_WRK_STAT", 				"U");
			    	
			    	//야드설비ID
			    	recInTemp.setField("YD_EQP_ID", szYdWrkPlanTcar);
			    	intRtnVal = ydTcarSchDao.insYdTcarsch(recInTemp);
		    		if(intRtnVal == -2) {
						szMsg="["+szOperationName+"] parameter error[2]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return intRtnVal = -1;
		    		}
		    		
					szMsg="["+szOperationName+"] szYD_TCAR_CURR_BAY_GP : " + szYD_TCAR_CURR_BAY_GP + " ,  szYdBayGp : " + szYdBayGp;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    		
		    		//현재 대차동과 작업예약의 동과 일치하면 스케줄 호출 비일치시 대차출발지시를 내린다.
		    		if(szYD_TCAR_CURR_BAY_GP.equals(szYdBayGp)) {
		    			
		    			recInTemp = JDTORecordFactory.getInstance().create();
		    			recInTemp.setField("MSG_ID",    "YDYDJ503");
		    			recInTemp.setField("YD_SCH_CD", szYdSchCd);
		    			
		    			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		    			intRtnVal = ydSchRuleDao.getYdSchrule(recInTemp, rsResult, 0);
		    			if(intRtnVal <= 0) {
		        			szMsg="["+szOperationName+"] 스케줄 기준 조회중 값이없거나 Error Code : " + intRtnVal;
		        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    			}
		    			
		    			rsResult.absolute(1);
		    			recOutTemp = JDTORecordFactory.getInstance().create();
		        		recOutTemp.setRecord(rsResult.getRecord());
		    			
		        		//------------------------------------------------
		        		//	대차의 차량진행상태를 상차도착으로 변경 필요.
		        		//------------------------------------------------
		        		
		    			//크레인 스케줄 호출
		    			szMsg="["+szOperationName+"] 대차가 이미 도착해있으므로 크레인 스케줄 호출";
		    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    			recInTemp.setField("YD_EQP_ID", ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WRK_CRN"));
		    			
		    			ydDelegate.sendMsg(recInTemp);
		    			
		    			szMsg="["+szOperationName+"] 대차 상차 스케줄("+szMethodName+") 완료";
		    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    			
		    		}else{
			    	
				    	//공대차출발지시를 한다.야드구분, 야드스케줄코드, 대차스케줄ID
			    		recInTemp = JDTORecordFactory.getInstance().create();
			    		recInTemp.setField("MSG_ID", "YDY3L006");
			    		recInTemp.setField("YD_GP", szYdGp);
			    		recInTemp.setField("YD_SCH_CD", szYdSchCd);
			    		recInTemp.setField("YD_TCAR_SCH_ID", szYD_TCAR_SCH_ID);
			    		
			    		ydDelegate.sendMsg(recInTemp);
			    		
						szMsg="["+szOperationName+"] 공대차 출발지시 전송 완료[2]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    		}
		    	}
	    	}
		}catch(Exception e){
	
			szMsg="["+szOperationName+"] 공대차 이동지시 Error : " +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			m_ctx.setRollbackOnly();
			throw new DAOException(szMsg);
		}
	
		szMsg="["+szOperationName+"] ------------------- 메소드 끝 -------------------";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        
		return intRtnVal = 1;
	} //end of Y3L2TcarSch()
	
	/**
	 * 오퍼레이션명 : 대차 상차 스케줄(영대차 출발 지시)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public int Y3UdTcarSch(JDTORecord msgRecord)throws DAOException  {
		
		YdTcarSchDao     ydTcarSchDao   	= new YdTcarSchDao(); 
		YdWrkbookDao     ydWrkbookDao   	= new YdWrkbookDao();
		YdWrkbookMtlDao  ydWrkbookMtlDao 	= new YdWrkbookMtlDao();
		YdTcarFtmvMtlDao ydTcarFtmvMtlDao	= new YdTcarFtmvMtlDao();
		
		JDTORecordSet rsResult          = null;
		JDTORecordSet rsWrkBookMtl      = null;
		JDTORecordSet rsWRKBOOK         = null;
		JDTORecordSet rsTcarFtMtl       = null;
		 
		JDTORecord    recInTemp         = null;
		JDTORecord    recOutTemp        = null;
		JDTORecord    recWrkBookMtl     = null;
		
	    int intRtnVal 					= 0;
	    int intWbookMtlCnt              = 0;
	    int intTcarFtmvMtlCnt           = 0;
	    
	    String szMsg              		= "";
	    String szMethodName       		= "Y3UdTcarSch";
	    String szOperationName			= "대차상차스케줄";
	    
	    String szWbookId                = "";
	    String szYD_SCH_CD              = "";
	    String szYD_TCAR_SCH_ID			= "";
	    
	    
	    try{
	    	szMsg="["+szOperationName+"] ------------------- 메소드 시작 -------------------";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
			//상차 완료 Check를 한다.
			//작업예약ID로 대차상차작업예약ID로 대차스케줄을 조회해서 대차스케줄ID를 조회한다.
	    	szWbookId = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");
	    	
	    	//작업예약재료의 갯수 조회
			szMsg= "[" + szWbookId + "]번의 작업예약재료의 갯수 조회";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_WBOOK_ID", szWbookId);
	    	rsWrkBookMtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recInTemp, rsWrkBookMtl, 14);
	    	if(intRtnVal <= 0) {
				szMsg="작업예약재료의 갯수 조회중 Error!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return intRtnVal = -1;
	    	}
	    	rsWrkBookMtl.absolute(1);
	    	recOutTemp = JDTORecordFactory.getInstance().create();
	    	recOutTemp.setRecord(rsWrkBookMtl.getRecord());
	    	intWbookMtlCnt = ydDaoUtils.paraRecChkNullInt(recOutTemp, "WBOOKMTL_CNT");
	    	
	    	//작업예약재료와 대차이송재료Table중 재료번호가 같은것의 갯수 조회
	    	szMsg="작업예약재료와 대차이송재료Table중 재료번호가 같은것의 갯수 조회";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_CARLD_WRK_BOOK_ID", szWbookId);
	    	recInTemp.setField("YD_WBOOK_ID",          szWbookId);
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recInTemp, rsResult, 15);
	    	if(intRtnVal <= 0) {
		    	szMsg="작업예약재료와 	 재료번호가 같은것의 갯수 조회중 Error Error Code : " + rsResult.size();
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    	}
	    	
	    	szMsg="rsResult SIZE :" + rsResult.size();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		    	
	    	rsResult.absolute(1);
	    	recOutTemp = JDTORecordFactory.getInstance().create();
	    	recOutTemp.setRecord(rsResult.getRecord());
	    	
	    	intTcarFtmvMtlCnt = recOutTemp.getFieldInt("TCARFTMVMTL_CNT");
	    	szYD_TCAR_SCH_ID = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TCAR_SCH_ID");
	    	
			szMsg="작업예약 재료 매수 : " + intWbookMtlCnt + " , 대차이송재료매수 : " + intTcarFtmvMtlCnt;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//작업예약TABLE 조회(스케줄코드를 조회하여 스케줄코드가 대차관련 스케줄코드인지 아닌지에 판단해 
			//                  대차관련 스케줄코드가 아니라면 중량체크를해서 차량출발지시를 보내기위해...)
			rsWRKBOOK = JDTORecordFactory.getInstance().createRecordSet("Temp");
			recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_CARLD_WRK_BOOK_ID", szWbookId);
	    	recInTemp.setField("YD_WBOOK_ID",          szWbookId);
	    	intRtnVal = ydWrkbookDao.getYdWrkbook(recInTemp, rsWRKBOOK, 10);
	    	if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="getYdWrkbookmtl data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);	
    			}else if(intRtnVal == -2) {
    				szMsg="getYdWrkbookmtl parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}
    			return intRtnVal = -1;
    		}
	    	rsWRKBOOK.absolute(1);
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setRecord(rsWRKBOOK.getRecord());
	    	szYD_SCH_CD = ydDaoUtils.paraRecChkNull(recInTemp, "YD_SCH_CD");
			
	    	/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	         * 			연주정정Level2 대차작업실적 전송  - YDC3L007
	         * 업무기준 Desc : 대차에 상차 시
	         * 스케줄코드 :  대차상차스케줄
	         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//	    	recInTemp = JDTORecordFactory.getInstance().create();
//			recInTemp.setField("MSG_ID",        "YDC3L007");
//			recInTemp.setField("YD_TCAR_SCH_ID", szYD_TCAR_SCH_ID);
//			ydDelegate.sendMsg(recInTemp);
//			szMsg = "[대차스케줄]대차 상차 시 - 대차작업실적 [YDC3L007] 전송 완료" ;
//            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			
            boolean isWork = false;
            /*
             * 2011.04.15 YJK
             * 대차상차 스케쥴일경우 미리 만들어 놓은 작업재료 매수를 다 상차했는지 체크
             * 그 외의 스케쥴은 직상차작업일 경우 - 상차작업시 바로 출발 
             */
			if(intWbookMtlCnt == intTcarFtmvMtlCnt && 
			   szYD_SCH_CD.substring(2,4).equals("TC") ) {
				isWork = true;
			}else if(!szYD_SCH_CD.substring(2,4).equals("TC")){
				isWork = true;
	    	}
			
			if(isWork){
				
				szMsg="하차작업예약 생성 및 등록";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
		    	//하차 작업예약 및 작업예약재료 생성
		    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
		    	intRtnVal = this.Y3MakeWrkBook(msgRecord, rsResult);
		    	if(intRtnVal == -1) return intRtnVal;
		    	
		    	//대차스케줄에 하차작업예약id 등록
				szMsg="대차스케줄에 하차작업예약id 등록";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    	rsResult.absolute(1);
		    	recWrkBookMtl = JDTORecordFactory.getInstance().create();
		    	recWrkBookMtl.setRecord(rsResult.getRecord());
		    	
		    	recInTemp     = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_TCAR_SCH_ID", ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TCAR_SCH_ID"));
				recInTemp.setField("YD_CARUD_WRK_BOOK_ID", ydDaoUtils.paraRecChkNull(recWrkBookMtl, "YD_WBOOK_ID"));
				recInTemp.setField("YD_EQP_WRK_STAT", "L");
				recInTemp.setField("YD_CAR_PROG_STAT", "5");
				recInTemp.setField("YD_CARUD_STOP_LOC", ydDaoUtils.paraRecChkNull(recWrkBookMtl, "YD_SCH_CD").substring(0,6));
	
		    	intRtnVal = ydTcarSchDao.updYdTcarsch(recInTemp, 0);
				if(intRtnVal <= 0) {
	    			return intRtnVal = -1;
	    		}
				
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				intRtnVal = ydWrkbookDao.getYdWrkbook(recWrkBookMtl, rsResult, 0);
				if(intRtnVal <= 0) {
					return intRtnVal = -1;
				}
				
				rsResult.absolute(1);
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("MSG_ID", "YDY3L006");
				recInTemp.addRecord(rsResult.getRecord());
				
				//영대차출발지시
	    		recInTemp.setField("YD_TCAR_SCH_ID", ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TCAR_SCH_ID"));
	    		recInTemp.setField("YD_GP",          ydDaoUtils.paraRecChkNull(recWrkBookMtl, "YD_GP"));
	    		recInTemp.setField("YD_SCH_CD",      ydDaoUtils.paraRecChkNull(recWrkBookMtl, "YD_SCH_CD"));
	    		
				szMsg="영대차출발지시!!  MSG_IG : " + recInTemp.getFieldString("MSG_ID") + " 전송";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
	    		ydDelegate.sendMsg(recInTemp);
			}

		}catch(Exception e){
			szMsg="대차 상차 스케줄 Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			m_ctx.setRollbackOnly();
			throw new DAOException(szMsg);
		}
	
		szMsg="대차 상차 스케줄("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		return intRtnVal = 1;
	} //end of Y3UdTcarSch()
    
	
	/**
	 * 오퍼레이션명 : 대차 하차 스케줄(공대차 출발 지시)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public int Y3LdTcarSch(JDTORecord msgRecord)throws DAOException  {
		
		YdTcarSchDao     ydTcarSchDao     = new YdTcarSchDao(); 
		YdTcarFtmvMtlDao ydTcarFtmvMtlDao = new YdTcarFtmvMtlDao();

		JDTORecordSet rsResult          = null;
		JDTORecordSet rsTcarSch         = null;
		JDTORecord    recInTemp         = null;
		JDTORecord    recOutTemp        = null;
	   
		int intRtnVal 					= 0 ;
	    
	    String szMsg              		= "";
	    String szMethodName       		= "Y3LdTcarSch";
	    String szOperationName			= "대차하차스케줄";
	    
	    String szWbookId                = "";
	    String szYD_TCAR_SCH_ID			= "";
	    String szYD_EQP_ID				= "";
	    
	    try{
			//하차 완료 Check를 한다.
	    	//작업예약ID로 대차하차작업예약ID로 대차스케줄을 조회해서 대차스케줄ID를 조회한다.
	    	szWbookId = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");
	    	
	    	//대차스케줄 조회용
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_CARUD_WRK_BOOK_ID", szWbookId);
	    	rsTcarSch = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	intRtnVal = ydTcarSchDao.getYdTcarsch(recInTemp, rsTcarSch, 1);
	    	
	    	//대차이송재료조회
	    	rsTcarSch.absolute(1);
	    	recOutTemp = JDTORecordFactory.getInstance().create();
	    	recOutTemp.setRecord(rsTcarSch.getRecord());
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	
	    	szYD_TCAR_SCH_ID 	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TCAR_SCH_ID");
	    	szYD_EQP_ID 		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_ID");
	    	
	    	//대차이송재료가 전부 삭제상태인지 확인한다.
	    	intRtnVal = ydTcarFtmvMtlDao.getYdTcarftmvmtl(recOutTemp, rsResult, 1);
	
	    	//하차가 완료된 경우 
	    	if(intRtnVal == 0) {
	    		
	    		//대차스케줄 삭제처리
	    		recInTemp = JDTORecordFactory.getInstance().create();
	    		recInTemp.setField("YD_EQP_WRK_STAT", "U");
	    		recInTemp.setField("DEL_YN", "Y");
	    		recInTemp.setField("YD_TCAR_SCH_ID", ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TCAR_SCH_ID"));
				recInTemp.setField("YD_CAR_PROG_STAT", "E");

				intRtnVal = ydTcarSchDao.updYdTcarsch(recInTemp, 0);
	    			
				/*
				 * 대차스케줄생성 메소드 호출 
				 * msgRecord에 값을 비워서 보내도록 변경
				 * 이전작업예약이 남아있어 대차상차작업예약ID에 대차 하차작업예약을 등록함.
				 * 작업예약이 없는 상태에서 다음 작업예약을 찾도록 조회!!
				 */
				msgRecord = JDTORecordFactory.getInstance().create();
				msgRecord.setField("YD_EQP_ID", szYD_EQP_ID);

				intRtnVal = this.Y3L2TcarSch(msgRecord);
	    	}

		}catch(Exception e){
	
			szMsg="대차 하차 스케줄 Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			m_ctx.setRollbackOnly();
			throw new DAOException(szMsg);
		}
		
		szMsg="대차 하차 스케줄("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		return intRtnVal;
	} //end of Y3LdTcarSch()
	
	/**
	 * 오퍼레이션명 : 작업예약재료생성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord, rsWbooId
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public int Y3MakeWrkBook(JDTORecord msgRecord, JDTORecordSet rsWbooId)throws DAOException  {
		
		YdWrkbookMtlDao  ydWrkbookMtlDao  = new YdWrkbookMtlDao();
		YdWrkbookDao     ydWrkbookDao     = new YdWrkbookDao();
		YdSchRuleDao     ydSchRuleDao     = new YdSchRuleDao();
		YdTcarFtmvMtlDao ydTcarFtmvMtlDao = new YdTcarFtmvMtlDao();
		
		JDTORecordSet rsResult          = null;
		JDTORecordSet rsWrkBookMtl      = null;
		JDTORecordSet rsTcarFtMtl       = null;
		JDTORecord    recInTemp         = null;
		JDTORecord    recOutTemp        = null;
		JDTORecord    recPara           = null;
		
	    int intRtnVal 					= 0;
	    
	    String szMsg              		= "";
	    String szMethodName       		= "Y3MakeWrkBook";
	    String szWbookId                = "";
	    String szYdGp                   = "";
	    String szYdBayGp                = "";
	    String szCurSchCd               = "";
	    String szPreSchCd               = "";
	    String szWrkPlanTcar            = "";
	    String szSchPrior               = "";
	    String szYD_TO_LOC_DCSN_MTD		= null;
	    String szYD_TO_LOC_GUIDE		= null;
	    
	    String szRegister               = szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName;

	    try{
	    	szWbookId = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");
	    	
	    	//작업예약id로 대차스케줄을 조회하여 대차스케줄id를 조회한다.
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_CARLD_WRK_BOOK_ID", szWbookId);
	    	recInTemp.setField("YD_WBOOK_ID",          szWbookId);
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	    	intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recInTemp, rsResult, 15);
	    	if(intRtnVal < 0) {
		    	szMsg="작업예약재료와 대차이송재료Table중 재료번호가 같은것의 갯수 조회중 Error Error Code : " + rsResult.size();
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    	}
	    	rsResult.absolute(1);
	    	recOutTemp = JDTORecordFactory.getInstance().create();
	    	recOutTemp.setRecord(rsResult.getRecord());
	    	
	    	rsTcarFtMtl = JDTORecordFactory.getInstance().createRecordSet("");
	    	intRtnVal   = ydTcarFtmvMtlDao.getYdTcarftmvmtl(recOutTemp, rsTcarFtMtl, 4);
	    	if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg="<Y3MakeWrkBook> ydTcarFtmvMtlDao data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);	
    			}else if(intRtnVal == -2) {
    				szMsg="<Y3MakeWrkBook> ydTcarFtmvMtlDao parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}
    			return intRtnVal = -1;
    		}
	    	
	    	//작업예약재료 Table를 조회한다.
	    	rsWrkBookMtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(msgRecord, rsWrkBookMtl, 11);
			if(intRtnVal <= 0) {
				if(intRtnVal == 0) {
					szMsg="<Y3MakeWrkBook> getYdWrkbookmtl data not found";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				}else if(intRtnVal == -2) {
					szMsg="<Y3MakeWrkBook> getYdWrkbookmtl parameter error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
    			throw new DAOException(szSessionName + " : " + szMethodName + " - " + szMsg);
			}
	    	
	    	//작업예약id로 작업예약Table를 조회한다.
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_WBOOK_ID", szWbookId);
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	intRtnVal = ydWrkbookDao.getYdWrkbook(recInTemp, rsResult, 10);
			if(intRtnVal <= 0) {
				if(intRtnVal == 0) {
					szMsg="<Y3MakeWrkBook> getYdWrkbook : data not found";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				}else if(intRtnVal == -2) {
					szMsg="<Y3MakeWrkBook> getYdWrkbook : parameter error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
    			throw new DAOException(szSessionName + " : " + szMethodName + " - " + szMsg);
			}
	    	
	    	rsResult.absolute(1);
	    	recOutTemp = JDTORecordFactory.getInstance().create();
	    	recOutTemp.setRecord(rsResult.getRecord());
	    	
	    	//야드구분 = 목표야드구분
	    	//szYdGp     				= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_AIM_YD_GP");
	    	/*
	    	 * 2010.12.30 윤재광 - 목표야드는 Default 셋팅
	    	 */
	    	szYdGp     				= "D";
	    	//야드동구분 = 목표동구분
	    	szYdBayGp     			= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_AIM_BAY_GP");
	    	//이전스케줄코드 = 스케줄코드
	    	szPreSchCd    			= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_SCH_CD");
	    	//작업계획대차  = 작업계획대차
	    	szWrkPlanTcar 			= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WRK_PLAN_TCAR");
	    	//현재스케줄코드 = 야드구분 + 야드동구분 + 이전스케줄코드.(2,4) + "TCLM;
	    	szCurSchCd    			= szYdGp + szYdBayGp + szWrkPlanTcar.substring(2,4) + "0" + szWrkPlanTcar.substring(5,6) + "LM";
	    	szYD_TO_LOC_DCSN_MTD 	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TO_LOC_DCSN_MTD");
			szYD_TO_LOC_GUIDE		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TO_LOC_GUIDE");
	    	
			/*
	    	 * 2010.12.30 윤재광 - To위치 정합성 체크 추가
	    	 */
			if("F".equals(szYD_TO_LOC_DCSN_MTD)&&
			   szYD_TO_LOC_GUIDE.length()>2){
				if(!szYdBayGp.equals(szYD_TO_LOC_GUIDE.substring(1, 2))){
					szYD_TO_LOC_DCSN_MTD 	= "S";
					szYD_TO_LOC_GUIDE		= "";
					szMsg="대차 하차 작업예약 생성중 To위치 가이드 정보 변경 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
			}
			
	    	//스케줄코드로 스케줄기준Table조회
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_SCH_CD", szCurSchCd);
	    	
	    	//스케줄 기준테이블의 항목중 야드작업크레인우선순위를 가져온다.
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	intRtnVal = ydSchRuleDao.getYdSchrule(recInTemp, rsResult, 0);
			if(intRtnVal <= 0) {
				if(intRtnVal == 0) {
					szMsg="<Y3MakeWrkBook> getYdSchrule : data not found";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				}else if(intRtnVal == -2) {
					szMsg="<Y3MakeWrkBook> getYdSchrule : parameter error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				return intRtnVal = -1;
			}
	    	
	    	rsResult.absolute(1);
	    	recOutTemp = JDTORecordFactory.getInstance().create();
	    	recOutTemp.setRecord(rsResult.getRecord());
	    	szSchPrior = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WRK_CRN_PRIOR");
	    	
	    	//작업예약id 생성
	    	//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();
	
			//================================================
			//파라미터를 설정하지 않으면 JSPEED에서 에러발생. 추후 수정요
			recPara.setField("YD_WBOOK_ID", "1");
			//================================================
			
			//작업예약 테이블의 시퀀스를 이용해 작업예약ID를 구해온다.
			rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
			intRtnVal = ydWrkbookDao.getYdWrkbook(recPara, rsResult, 1);
			if(intRtnVal <= 0) {
				if(intRtnVal == 0) {
					szMsg="<Y3MakeWrkBook> getYdWrkbook : data not found";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				}else if(intRtnVal == -2) {
					szMsg="<Y3MakeWrkBook> getYdWrkbook : parameter error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				return intRtnVal = -1;
			}
			
			rsResult.absolute(1);
	    	recOutTemp = JDTORecordFactory.getInstance().create();
	    	recOutTemp.setRecord(rsResult.getRecord());
	    	
	    	//생성한 작업예약ID
	    	szWbookId = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WBOOK_ID");
	    	
	    	//작업예약항목SETTING
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	
	    	recInTemp.setField("YD_WBOOK_ID", 			szWbookId);
	    	recInTemp.setField("REGISTER", 				szRegister);
	    	recInTemp.setField("YD_GP", 				szYdGp);
	    	recInTemp.setField("YD_BAY_GP", 			szYdBayGp);
	    	recInTemp.setField("YD_AIM_YD_GP", 			szYdGp);
	    	recInTemp.setField("YD_AIM_BAY_GP", 		szYdBayGp);
	    	recInTemp.setField("YD_SCH_PRIOR", 			szSchPrior);
	    	recInTemp.setField("YD_SCH_CD", 			szCurSchCd);
	    	recInTemp.setField("YD_WRK_PLAN_TCAR", 		szWrkPlanTcar);
	    	recInTemp.setField("YD_TO_LOC_DCSN_MTD", 	szYD_TO_LOC_DCSN_MTD);
	    	recInTemp.setField("YD_TO_LOC_GUIDE", 		szYD_TO_LOC_GUIDE);
	    	
	    	intRtnVal = ydWrkbookDao.insYdWrkbook(recInTemp);
    		if(intRtnVal == -2) {
				szMsg="parameter error";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return intRtnVal = -1;
    		}
	    	
    		rsWbooId.addRecord(recInTemp);

	    	//작업예약재료 등록
	    	for(int Loop_i = 1; Loop_i <= rsTcarFtMtl.size(); Loop_i++) {
	    		rsTcarFtMtl.absolute(Loop_i);
	    		recInTemp  = JDTORecordFactory.getInstance().create();
	    		recOutTemp = JDTORecordFactory.getInstance().create();
	    		recOutTemp.setRecord(rsTcarFtMtl.getRecord());
	    		
	    		recInTemp.setField("YD_WBOOK_ID",    szWbookId);
	    		recInTemp.setField("REGISTER",       szRegister);
	    		recInTemp.setField("YD_STK_COL_GP",  szYdGp + szYdBayGp + szWrkPlanTcar.substring(2));
	    		recInTemp.setField("YD_STK_BED_NO",  "01");
	    		recInTemp.setField("YD_STK_LYR_NO",  ydDaoUtils.stringPlusInt("000", Loop_i));
	    		recInTemp.setField("STL_NO",         ydDaoUtils.paraRecChkNull(recOutTemp, "STL_NO"));
	    		recInTemp.setField("YD_UP_COLL_SEQ", "" + (rsTcarFtMtl.size()- Loop_i + 1) );
	    		intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recInTemp);
	    		if(intRtnVal == -2) {
					szMsg="parameter error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return intRtnVal = -1;
	    		}
	    	}
	    		
		}catch(Exception e){
			szMsg="작업예약생성 Error :" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			m_ctx.setRollbackOnly();
			throw new DAOException(szMsg);
		}
	
		szMsg="작업예약생성("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		return intRtnVal = 1;
	} //end of Y3MakeWrkBook()
	
	//---------------------------------------------------------------------------	
} // end of class TransEqpSchSeEJBBean
