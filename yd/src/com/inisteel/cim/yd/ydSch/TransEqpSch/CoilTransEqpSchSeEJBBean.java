package com.inisteel.cim.yd.ydSch.TransEqpSch;

import java.util.List;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

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
import com.inisteel.cim.ym.common.dao.ymCommonDAO;

/**
 * 대차명령선택 Session EJB
 *
 * @ejb.bean name="CoilTransEqpSchSeEJB" jndi-name="CoilTransEqpSchSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class CoilTransEqpSchSeEJBBean extends BaseSessionBean {
	
	// Session Name
	private String szSessionName=getClass().getName();
	
	private YdUtils ydUtils =new YdUtils();
	
	private YdTcConst ydTcConst =new YdTcConst();
	
	private YdDaoUtils ydDaoUtils =new YdDaoUtils();
	
	private YdDBAssist ydDBAssist =new YdDBAssist();
	
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
	
	
	
	
	
	
	
	
	/**
	 * 오퍼레이션명 : C열연 대차 스케줄 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * 		YD_LD_UD_GP,YD_WBOOK_ID,YD_EQP_ID
	 * 스케줄 기동 및 차량지시 삭제 처리 함 호출하는 부분에서 처리 하도록 함: ydDelegate 처리 안됨
	 * 
	 * 
	 * 
	 * 
	 * @return Integer
	 * @throws JDTOException
	 */
	public JDTORecord procY5TcarSch(JDTORecord msgRecord)throws JDTOException  {
		
		
	    int intRtnVal 					= 0 ;
	    
	    String szMsg              		= "";
	    String szMethodName       		= "procY5TcarSch";
	    String szOperationName			= "C열연대차스케줄";
	    JDTORecord outRecord  			= JDTORecordFactory.getInstance().create(); 
	    JDTORecord outRecord1  			= JDTORecordFactory.getInstance().create(); 
	    String sRTN_CD					= "";
	    String sRTN_MSG					= "";
	    String sTCAR_MOVE_SND           = "";
	    //상하차 구분
	    String szLdUdGp					= "";
	    
	    ydUtils.putLog(szSessionName, szMethodName, "C열연대차스케줄기동" , YdConstant.DEBUG);	    
	    
        String szRcvTcCode=ydUtils.getTcCode(msgRecord);
        if(szRcvTcCode==null || szRcvTcCode.equals("")){
        	szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
        	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//        	return YdConstant.RETN_INT_TC_ERROR;
			outRecord.setField("RTN_CD" , "0");	
			outRecord.setField("RTN_MSG", szMsg);	
			return outRecord;

        }
        
        if(bDebugFlag){
            szMsg="[DEBUG] 전문수신 : TCCODE=" +szRcvTcCode ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }
	    
	    try{
	        szMsg="["+szOperationName+"] ------------------- 메소드 시작 -------------------";
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            
            ydUtils.displayRecord(szOperationName, msgRecord);

	    	szLdUdGp = ydDaoUtils.paraRecChkNull(msgRecord, "YD_LD_UD_GP");
	    	

    		//------------------------------------------------------------------------------------------
    		//권상 실적 처리중 호출한 경우 - 대차에서 야드로 권상후 
	    	//------------------------------------------------------------------------------------------
    		if( szLdUdGp.equals("U") ) {
    			szMsg="["+szOperationName+"] 권상실적 처리 시 호출 - 대차하차스케줄 모듈 시작";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                
    			//대차 하차스케줄 호출  
                outRecord1 = (JDTORecord)this.Y5LdTcarSch(msgRecord);
    			sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
    			sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
    			sTCAR_MOVE_SND	= StringHelper.evl(outRecord1.getFieldString("TCAR_MOVE_SND"), "");
    			
      			ydUtils.putLog(szSessionName, szMethodName, sRTN_MSG, YdConstant.DEBUG);
    			if(sRTN_CD.equals("0")){ 
    				//throw new DAOException(szMsg);
    				outRecord.setField("RTN_CD" , "0");	
    				outRecord.setField("RTN_MSG", sRTN_MSG);	
    				//m_ctx.setRollbackOnly();
    				return outRecord;
    			}	
    			
    			szMsg="["+szOperationName+"] 권상실적 처리 시 호출 - 대차하차스케줄 모듈 완료 - 반환값 : " + sRTN_CD;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                
    		//------------------------------------------------------------------------------------------
    		//권하 실적 처리중 호출한 경우 - 야드에서 대차로 권하후
    		//------------------------------------------------------------------------------------------
    		}else if( szLdUdGp.equals("L") ){
    		
    			szMsg="["+szOperationName+"] 권하실적 처리 시 호출 - 대차상차스케줄 모듈 시작";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    		
    			//대차 상차 스케줄 호출
                outRecord1 = (JDTORecord)this.Y5UdTcarSch(msgRecord);
       			sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
    			sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
    			sTCAR_MOVE_SND	= StringHelper.evl(outRecord1.getFieldString("TCAR_MOVE_SND"), "");
    			
      			ydUtils.putLog(szSessionName, szMethodName, sRTN_MSG, YdConstant.DEBUG);
    			if(sRTN_CD.equals("0")){ 
    				//throw new DAOException(szMsg);
    				outRecord.setField("RTN_CD" , "0");	
    				outRecord.setField("RTN_MSG", sRTN_MSG);	
    				//m_ctx.setRollbackOnly();
    				return outRecord;
    			}	
   			
    			szMsg="["+szOperationName+"] 권하실적 처리 시 호출 - 대차상차스케줄 모듈 완료 - 반환값 : " + sRTN_CD;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			
    			
    		}else{
    			//------------------------------------------------------------------------------------------
    			//화면에서 송신한 경우 - 공대차출발지시
    			//------------------------------------------------------------------------------------------
    			szMsg="["+szOperationName+"] 화면에서 호출 - 공대차출발스케줄 모듈 시작";
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
                
                outRecord1 = (JDTORecord)this.Y5L2TcarSch(msgRecord);
       			sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
    			sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
    			sTCAR_MOVE_SND	= StringHelper.evl(outRecord1.getFieldString("TCAR_MOVE_SND"), "");
    			
      			ydUtils.putLog(szSessionName, szMethodName, sRTN_MSG, YdConstant.DEBUG);
    			if(sRTN_CD.equals("0")){ 
    				//throw new DAOException(szMsg);
    				outRecord.setField("RTN_CD" , "0");	
    				outRecord.setField("RTN_MSG", sRTN_MSG);	
    				//m_ctx.setRollbackOnly();
    				return outRecord;
    			}	
	    		
	    		szMsg="["+szOperationName+"] 화면에서 호출 - 공대차출발스케줄 모듈 완료 - 반환값 : " + sRTN_CD;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    		}

 		}catch(Exception e){
 			e.printStackTrace();
			szMsg="["+szOperationName+"] 예외 발생 : " +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			if(intRtnVal == -1) { 
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				//m_ctx.setRollbackOnly();
				return outRecord;
			}	
		}
	
		szMsg="["+szOperationName+"] C열연 대차 스케줄 끝";
        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        
		return outRecord1;
	} //end of procY5TcarSch()
	
	
    
	
	
	
	
	
	
	/**
	 * 오퍼레이션명 : 대차 하차 스케줄(공대차 출발지시)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public JDTORecord 	Y5L2TcarSch(JDTORecord msgRecord)throws JDTOException  {
		//동간이적요구시 화면에서  송신하는 경우
		YdTcarSchDao     ydTcarSchDao     = new YdTcarSchDao();
		YdEqpDao         ydEqpDao         = new YdEqpDao();
		YdSchRuleDao     ydSchRuleDao     = new YdSchRuleDao();
		YdWrkbookDao     ydWrkbookDao     = new YdWrkbookDao();
		JDTORecord outRecord  			= JDTORecordFactory.getInstance().create(); 
		JDTORecord outRecord1  			= JDTORecordFactory.getInstance().create(); 
		JDTORecordSet rsResult          = null;
		JDTORecordSet rsResult1         = null;
		JDTORecord    recInTemp         = null;
		JDTORecord    recOutTemp        = null;
		JDTORecord    recTcar           = null;
		JDTORecord    recWbook          = null;
	    int intRtnVal 					= 0 ;
	    
	    String szMsg              		= "";
	    String szMethodName       		= "Y5L2TcarSch";
	    String szOperationName			= "공대차스케줄";
	    
	    
	    String szQuery                  = "";
	    
	    String sTcarEqpId              	= "";
	    String sYdGp					= "";
	    String sYdBayGp					= "";		//from 동	
	    String sYdAimYdGp				= "";
	    String sYdAimBayGp				= "";    	//대차 목적동
	    String sYdWrkPlanTcar			= "";
	    String sYdSchCd                	= "";
	    String sYD_TCAR_CURR_BAY_GP    	= "";
	    String sYD_TCAR_SCH_ID         	= "";
	    String sYD_TO_BAY              	= "";
	    String sYD_CARLD_WRK_BOOK_ID   	= "";
	    String sYD_CARLD_STOP_LOC      	= "";					//상차정지위치
	    String sYD_CARUD_STOP_LOC		= null;					//하차정지위치
	    String sYD_WBOOK_ID            	= "";
	    String sRegister				= "SYSTEM";
		String sYD_HOME_BAY_GP         	= "";
	    String sTB_YD_EQP_YD_GP			= "";

	    String sRCPT_TCAR_USE_YN 		= "";
	    String sRCPT_TCAR_BAY			= "";
	    String sRCPT_TCAR_AIM_BAY_GP 	= "";
	    
	    
	    try{
	    	szMsg="["+szOperationName+"] ------------------- 메소드 시작 -------------------";
	        ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        
	        ydUtils.displayRecord(szOperationName, msgRecord);
	    	
	    	sTcarEqpId 		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
	    	sYD_TO_BAY 		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_TO_BAY");  //화면에서 지정
	    	sYD_WBOOK_ID	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");
	    	
	    	
	    	//-------------------------------------------------------------------------------------
	    	//	대차설비ID로 설비테이블을 조회한다.
	    	//-------------------------------------------------------------------------------------
	    	
	    	rsResult1 = JDTORecordFactory.getInstance().createRecordSet("");
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_EQP_ID", 			sTcarEqpId);
			/*com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpCoil*/
			intRtnVal = ydEqpDao.getYdEqp(recInTemp, rsResult1, 400);
			
			
			szMsg="["+szOperationName+"] 대차설비ID(" + sTcarEqpId + ")로 설비테이블 조회 완료 - 반환값 : " + intRtnVal;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			if(intRtnVal <= 0) {
				szMsg="["+szOperationName+"] 대차설비ID(" + sTcarEqpId + ")로 설비테이블 조회 시 오류발생 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;
			}
			
			rsResult1.absolute(1);
			recOutTemp = JDTORecordFactory.getInstance().create();
			recOutTemp.setRecord(rsResult1.getRecord());
			
			sTB_YD_EQP_YD_GP  		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_GP");	//설비동
			sYD_TCAR_CURR_BAY_GP 	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CURR_BAY_GP");	//현재동
			sYD_HOME_BAY_GP 		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_HOME_BAY_GP");	//HOME동
			sRCPT_TCAR_USE_YN 		= ydDaoUtils.paraRecChkNull(recOutTemp, "RCPT_TCAR_USE_YN");	    //입고대차유무
			sRCPT_TCAR_BAY			= ydDaoUtils.paraRecChkNull(recOutTemp, "RCPT_TCAR_BAY");	        //입고동
			sRCPT_TCAR_AIM_BAY_GP 	= ydDaoUtils.paraRecChkNull(recOutTemp, "RCPT_TCAR_AIM_BAY_GP");	//입고to동
			
			
	    	if(!sYD_WBOOK_ID.equals("")) {
// 해당사항 없슴	    		
//	    		//-------------------------------------------------------------------------------------
//	    		//	파라미터로 전달된 작업예약ID가 존재하면 작업예약을 조회한다.
//	    		//-------------------------------------------------------------------------------------
//	    		
//	    		recInTemp = JDTORecordFactory.getInstance().create();
//	    		rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
//	    		recInTemp.setField("YD_WBOOK_ID", sYD_WBOOK_ID);
//	    		intRtnVal = ydWrkbookDao.getYdWrkbook(recInTemp, rsResult, 0);
//	    		if(intRtnVal <= 0 ){
//	    			szMsg= "["+szOperationName+"] 파라미터로 전달된 작업예약[" + sYD_WBOOK_ID + "]을 조회 시 오류발생 - 반환값 : " + intRtnVal;
//	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//					outRecord.setField("RTN_CD" , "0");	
//					outRecord.setField("RTN_MSG", szMsg);	
//					return outRecord;
//	    		}
//	    		
//	    		szMsg="["+szOperationName+"] 파라미터로 전달된 작업예약[" + sYD_WBOOK_ID + "]을 조회 완료 - 반환값 : " + intRtnVal;
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//				
//	    		rsResult.absolute(1);
//	    		recWbook = JDTORecordFactory.getInstance().create();
//	    		recWbook.setRecord(rsResult.getRecord());
//	    		
//		    	//야드작업계획대차, 야드동구분, 목표동구분 , 야드구분
//		    	sYdGp          = ydDaoUtils.paraRecChkNull(recWbook, "YD_GP");
//		    	sYdBayGp       = ydDaoUtils.paraRecChkNull(recWbook, "YD_BAY_GP");
//		    	sYdAimBayGp    = ydDaoUtils.paraRecChkNull(recWbook, "YD_AIM_BAY_GP");
//		    	sYdWrkPlanTcar = ydDaoUtils.paraRecChkNull(recWbook, "YD_WRK_PLAN_TCAR");
//		    	sYdSchCd       = ydDaoUtils.paraRecChkNull(recWbook, "YD_SCH_CD");
//		    	
//		    	//-------------------------------------------------------------------------------------
		    	
		    }else{
	    		if( !sYD_TO_BAY.equals("") ) { // 지정한 동(화면에서 공대차 이동 지시 처리 
	    			
	    			sYdBayGp		= sYD_TCAR_CURR_BAY_GP;
	    			sYdAimBayGp		= sYD_TO_BAY;
	    			
	    			szMsg="["+szOperationName+"] 해당 대차["+sTcarEqpId+"]를 사용자가 지정한 동["+sYD_TO_BAY+"]으로 공대차출발 처리";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		} else {

	    			// 입고동 지정여부
	    			if( sRCPT_TCAR_USE_YN.equals("Y") && sTB_YD_EQP_YD_GP.equals("J") ) {
		    		
	    				sYdBayGp	= sYD_TCAR_CURR_BAY_GP;
		    			sYdAimBayGp	= sRCPT_TCAR_BAY;
				    			
		    			szMsg="["+szOperationName+"] 해당 대차["+sTcarEqpId+"]를 입고 동["+sRCPT_TCAR_BAY+"]으로 공대차출발 처리";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			
	    			} else {
		    			
		    	    	szMsg="["+szOperationName+"] 해당 대차["+sTcarEqpId+"]에 대해서 일반적인 규칙인 우선순위가 빠르고, " +
		    	    			"작업예약순서가 빠른 대차상차작업예약을 조회한다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    	    	
			    		//-------------------------------------------------------------------------------------
				    	//	파라미터로 전달된 작업예약ID가 존재하지 않으면 
			    		//	작업계획대차로 우선순위가 빠르고, 작업예약순서가 빠른 대차상차작업예약만 조회
			    		//-------------------------------------------------------------------------------------
			    		
			    		szMsg="["+szOperationName+"] 해당 대차["+sTcarEqpId+"]로 등록된 대차상차작업예약을 조회 시작";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						recInTemp.setField("YD_WRK_PLAN_TCAR"	, sTcarEqpId);
						recInTemp.setField("YD_GP"				, sTB_YD_EQP_YD_GP);
						recInTemp.setField("YD_BAY_GP"			, sYD_HOME_BAY_GP);
	    	    		
						rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
						/*대차상차 작업중우선순위가 빠르고, 작업예약순서가 빠른  작업예약 조회*/
						/* com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookYdWrkPlanTcarCoil */
						intRtnVal = ydWrkbookDao.getYdWrkbook(recInTemp, rsResult, 301);
						
						szMsg="["+szOperationName+"] 해당 대차["+sTcarEqpId+"]로 등록된 대차상차작업예약을 조회 완료 - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		
			    		if(intRtnVal > 0) {
			    			//홈동에 작업예약이 존재 하는 경우
					    	rsResult.first();
					    	recOutTemp = JDTORecordFactory.getInstance().create();
					    	recOutTemp.setRecord(rsResult.getRecord());
					    	
					    	sYD_WBOOK_ID  	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WBOOK_ID");
					    	sYdGp          	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_GP");
					    	sYdBayGp       	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_BAY_GP");
					    	sYdAimBayGp    	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_AIM_BAY_GP");
					    	sYdWrkPlanTcar 	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WRK_PLAN_TCAR");
					    	sYdSchCd       	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_SCH_CD");
			    		}else{
			    			//홈동에 작업예약이 존재 안 하는 경우
			    			recInTemp = JDTORecordFactory.getInstance().create();
			    			recInTemp.setField("YD_EQP_ID", 			sTcarEqpId);
			    			recInTemp.setField("YD_WRK_PLAN_TCAR"	, sTcarEqpId);
							recInTemp.setField("YD_GP"				, sTB_YD_EQP_YD_GP);
							recInTemp.setField("YD_BAY_GP"			, "_");
							
							rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
							/*대차상차 작업중우선순위가 빠르고, 작업예약순서가 빠른  작업예약 조회*/
							/* com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookYdWrkPlanTcarCoil */
							intRtnVal = ydWrkbookDao.getYdWrkbook(recInTemp, rsResult, 301);
							
							if(intRtnVal > 0) {
				    			//홈동에 작업예약이 존재 하는 경우
						    	rsResult.first();
						    	recOutTemp = JDTORecordFactory.getInstance().create();
						    	recOutTemp.setRecord(rsResult.getRecord());
						    	
						    	sYD_WBOOK_ID  	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WBOOK_ID");
						    	sYdGp          	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_GP");
						    	sYdBayGp       	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_BAY_GP");
						    	sYdAimBayGp    	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_AIM_BAY_GP");
						    	sYdWrkPlanTcar 	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WRK_PLAN_TCAR");
						    	sYdSchCd       	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_SCH_CD");
						    	
											    	
						    	JDTORecord recPara 	= JDTORecordFactory.getInstance().create();
								recPara.setField("YD_GP", 				sYdGp);
								recPara.setField("YD_EQP_ID", 			sTcarEqpId);
								recPara.setField("NEW_YD_CURR_BAY_GP", 	sYdAimBayGp);
								recPara.setField("YD_HOME_BAY_GP", 		sYdBayGp);
			
								JDTORecord [] inRecord =  new JDTORecord[1];
								inRecord[0]	= recPara;
								
						    	
						    	EJBConnector ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
								outRecord = (JDTORecord)ejbConn.trx("CoilYdTcarStsSetTcarF", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
				 
								szMsg="["+szOperationName+"] 정상적으로 HOME 동  변경 되었습니다.";
				    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				    			
				    		}else{
				    			//더이상 작업이 없는 경우 
			    			
				    			sYdBayGp		= sYD_TCAR_CURR_BAY_GP;
				    			sYdAimBayGp		= sYD_HOME_BAY_GP;
						    			
				    			szMsg="["+szOperationName+"] 대차와 관련된 작업예약이 없는 경우에는 홈동["+sYD_HOME_BAY_GP+"]으로 공대차 출발 지시를 처리.";
				    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				    		}
			    		}
	    			}

	    		}
		    }
	    	
	    	
	    	
	//-------------------------------------------------------------------------------------
	//	대차설비ID로 대차스케줄을 조회한다.
	//-------------------------------------------------------------------------------------
	    	
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_EQP_ID", 		sTcarEqpId);
	    	/*com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.getYdTcarschByYdEqpId*/
	    	intRtnVal = ydTcarSchDao.getYdTcarsch(recInTemp, rsResult, 4);
	    	
	    	szMsg="["+szOperationName+"] 대차설비ID(" + sTcarEqpId + ")로 대차스케줄 조회 완료 - 반환값 : " + intRtnVal;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
	    	if(intRtnVal > 0) {
//	대차스케줄이 존재
	    		rsResult.absolute(1);
	    		recTcar = JDTORecordFactory.getInstance().create();
	    		recTcar.setRecord(rsResult.getRecord());
	    		
	    		//상차작업예약 id가 있는지 없는지 Check!!!
	    		sYD_CARLD_WRK_BOOK_ID = ydDaoUtils.paraRecChkNull(recTcar, "YD_CARLD_WRK_BOOK_ID");
	    		sYD_CARLD_STOP_LOC    = ydDaoUtils.paraRecChkNull(recTcar, "YD_CARLD_STOP_LOC");
	    		sYD_TCAR_SCH_ID       = ydDaoUtils.paraRecChkNull(recTcar, "YD_TCAR_SCH_ID");
	    		
	    		szMsg="["+szOperationName+"] 대차설비ID(" + sTcarEqpId + ")로 대차스케줄["+sYD_TCAR_SCH_ID+"]이 존재하는 경우 - 이미 등록된 상차작업예약["+sYD_CARLD_WRK_BOOK_ID+"], 상차정지위치["+sYD_CARLD_STOP_LOC+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
	    		if(!sYD_CARLD_WRK_BOOK_ID.equals("")) {
//대차스케줄의 상차작업예약이 존재하는 경우 return
	    			
					szMsg="["+szOperationName+"] 대차[" + sTcarEqpId + "]스케줄["+sYD_TCAR_SCH_ID+"]이 존재하고 이미 등록된 상차작업예약["+sYD_CARLD_WRK_BOOK_ID+"]이 존재하므로 대차스케줄["+sYD_TCAR_SCH_ID+"]을 기동할 수 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					outRecord.setField("RTN_CD" , "1");	
					outRecord.setField("RTN_MSG", szMsg);	
					return outRecord;

	    		}else if (sYD_CARLD_WRK_BOOK_ID.equals("") && !sYD_TO_BAY.trim().equals("")){
	    			
//대차스케줄의 상차작업예약이 없고  사용자가 지정한 목표동의 값이 존재하는 경우
	    			
	    			szMsg="["+szOperationName+"] 대차스케줄["+sYD_TCAR_SCH_ID+"]의 상차작업예약이 등록되어 있지 않고 사용자가 지정한 목표동["+sYD_TO_BAY+"]이 존재하는 경우는 대차스케줄["+sYD_TCAR_SCH_ID+"]에 공차대기 상태[0]로 등록 후 공대차출발 지시 전송";
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			
	    			recTcar.setField("YD_CAR_PROG_STAT"		, "0");					//공차대기
	    			recTcar.setField("YD_CARLD_STOP_LOC"	, sTB_YD_EQP_YD_GP + sYD_TO_BAY + sTcarEqpId.substring(2,6));		//사용자지정위치
	    			
	    			/*com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.updYdTcarsch*/
	    			intRtnVal = ydTcarSchDao.updYdTcarsch(recTcar, 0);
	    			if(intRtnVal <= 0) {
	    				szMsg="["+szOperationName+"] 대차스케줄["+sYD_TCAR_SCH_ID+"]의 상차작업예약이 등록되어 있지 않고 사용자가 지정한 목표동["+sYD_TO_BAY+"]이 존재하는 경우는 대차스케줄["+sYD_TCAR_SCH_ID+"]에 공차대기 상태[0]로 등록 시 오류발생 - 반환값 : " + intRtnVal;
		    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" , "0");	
						outRecord.setField("RTN_MSG", szMsg);	
						return outRecord;
	    			}
	    			//-------------------------------------------------------------------------------------
					//공대차출발지시를 한다.야드구분, 야드스케줄코드, 대차스케줄ID
	    			//-------------------------------------------------------------------------------------

					szMsg="["+szOperationName+"] 대차 자동 출발 가능 여부 체크 시작!!";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    			
	    			// 대차 자동 출발 가능 여부 체크
	    			intRtnVal = this.Y5EtcTcarCheck(sTcarEqpId, sYD_TCAR_CURR_BAY_GP, sYD_TO_BAY);
			    	if(intRtnVal < 1) {
						szMsg="["+szOperationName+"] 대차 자동 출발 가능 여부 체크에 걸림!!";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

						outRecord.setField("RTN_CD" , "1");	
						outRecord.setField("RTN_MSG", szMsg);	
						return outRecord;
			    	}
			    	
			    	// 호출한 쪽에서 (하차나 상차 완료 처리 로직에서 차량 출발 처리 함)
			    	outRecord1 = JDTORecordFactory.getInstance().create();
			    	outRecord1.setField("MSG_ID"			, "YDY5L006");
			    	outRecord1.setField("TCAR_MOVE_SND"		, "Y");
			    	outRecord1.setField("YD_GP"				, sTB_YD_EQP_YD_GP);
			    	outRecord1.setField("YD_SCH_CD"			, "");
			    	outRecord1.setField("YD_TCAR_SCH_ID"	, sYD_TCAR_SCH_ID);
			    	outRecord1.setField("YD_AIM_BAY_GP"		, sYD_TO_BAY);		
					outRecord1.setField("RTN_CD" 			, "1");	
					outRecord1.setField("RTN_MSG"			, szMsg);	
					return outRecord1;

//대차스케줄의 상차작업예약이 없고 지정한 to 위치가 없는 경우 
	    		} else if (sYD_CARLD_WRK_BOOK_ID.equals("") && sYD_TO_BAY.equals("")) {
		    	
			    	sYD_CARLD_STOP_LOC	= sTB_YD_EQP_YD_GP + sYdBayGp    + sTcarEqpId.substring(2,6);
			    	sYD_CARUD_STOP_LOC	= sTB_YD_EQP_YD_GP + sYdAimBayGp + sTcarEqpId.substring(2,6);
			    		
		    		//대차스케줄 업데이트항목
			    	recInTemp = JDTORecordFactory.getInstance().create();
			    	recInTemp.setField("YD_TCAR_SCH_ID"			, sYD_TCAR_SCH_ID);
				    recInTemp.setField("YD_CARLD_SCH_REQ_GP"	, "6");					//공대차도착
					recInTemp.setField("YD_CARUD_SCH_REQ_GP"	, "3");					//영대차도착
					recInTemp.setField("YD_CARLD_WRK_BOOK_ID"	, sYD_WBOOK_ID);
					recInTemp.setField("YD_CARLD_STOP_LOC"		, sYD_CARLD_STOP_LOC);		//상차정지위치
					recInTemp.setField("YD_CARUD_STOP_LOC"		, sYD_CARUD_STOP_LOC);		//하차정지위치
					 
					//상차정지위치와 대차설비의 현재동이 같은경우
					//상차도착상태로 변경, 크레인스케줄 호출
					if(sYdBayGp.equals(sYD_TCAR_CURR_BAY_GP)) {

		    			szMsg="상차정지위치와 대차설비의 현재동이 같은경우";
		    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    			
		    			//-------------------------------------------------------------------------------------
						//	상차정지위치와 대차설비의 현재동이 같은경우 상차도착상태로 변경, 크레인스케줄 호출
						//-------------------------------------------------------------------------------------
		    			if( !sYD_WBOOK_ID.equals("") ){
			    			//상차작업예약id및 상차도착상태로 등록 후 크레인 스케줄 호출
			    			
		    				recInTemp.setField("YD_CAR_PROG_STAT", "2");
			    			
			    			/*com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.updYdTcarsch*/
			    			intRtnVal = ydTcarSchDao.updYdTcarsch(recInTemp, 0);
			    			if(intRtnVal <= 0) {
				    			szMsg="["+szOperationName+"] 대차스케줄["+sYD_TCAR_SCH_ID+"]에 상차작업예약["+sYD_WBOOK_ID+"], 상차도착 상태[2], 상차정지위치["+sYD_CARLD_STOP_LOC+"], 하차정지위치["+sYD_CARUD_STOP_LOC+"] 등록 시 오류발생 - 반환값 : " + intRtnVal;
				    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
								outRecord.setField("RTN_CD" , "0");	
								outRecord.setField("RTN_MSG", szMsg);	
								return outRecord;
			    			}
		    			
			    			//-------------------------------------------------------------------------------------
							//	크레인설비ID를 구하기 위해서 스케줄기준 조회
							//-------------------------------------------------------------------------------------

			    			recInTemp = JDTORecordFactory.getInstance().create();
			    			recInTemp.setField("MSG_ID",    "YDYDJ509");
			    			recInTemp.setField("YD_SCH_CD", sYdSchCd);
			    			
		    				
		    				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		    				intRtnVal = ydSchRuleDao.getYdSchrule(recInTemp, rsResult, 0);
		    				if(intRtnVal <= 0) {
		    					szMsg="["+szOperationName+"] 크레인설비ID를 구하기 위해서 스케줄코드["+sYdSchCd+"]로 스케줄기준 조회 시 오류발생 - 반환값 : " + intRtnVal;
			        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			    				outRecord.setField("RTN_CD" , "0");	
			    				outRecord.setField("RTN_MSG", szMsg);	
			    				return outRecord;
		    				}
		    				
			    			rsResult.absolute(1);
			    			recOutTemp = JDTORecordFactory.getInstance().create();
			        		recOutTemp.setRecord(rsResult.getRecord());
	
			    			
		    				szMsg="["+szOperationName+"] 상차정지위치동["+sYdBayGp+"]와 대차설비의 현재동["+sYD_TCAR_CURR_BAY_GP+"]이 같은 경우는 대차가 이미 도착한 상태이므로 크레인 스케줄 호출 완료 - 공대차출발스케줄 종료";
			    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			    			outRecord.setField("MSG_ID" 			, "YDYDJ509");	
			    			outRecord.setField("YD_SCH_CD" 			, sYdSchCd);	
			    			outRecord.setField("YD_EQP_ID" 			, ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WRK_CRN"));	
			    			outRecord.setField("CRN_YD_SCH_CD_SND" 	, "Y");	
							outRecord.setField("RTN_CD" 			, "1");	
							outRecord.setField("RTN_MSG"			, szMsg);	
							return outRecord;
			    			
			    			//-------------------------------------------------------------------------------------
		    			}else{
		    				szMsg="["+szOperationName+"] 상차작업예약["+sYD_WBOOK_ID+"]이 존재하지 않으므로 크레인 스케줄 호출하지 않음";
			    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    			}
		    			
		    		} else {
		    			//공대차이지만 다른동에 있을경우에는 대차스케줄 상차동을 현재동으로 등록한 후 공대차출발지시 등록 
		    			
		    			szMsg="["+szOperationName+"] 대차설비[" + sTcarEqpId + "]의 현재동["+sYD_TCAR_CURR_BAY_GP+"]과 대차상차작업예약의 상차정지위치동["+sYdBayGp+"]이 다른 경우 공대차출발지시를 L2로 전송";
		    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    			
		    			recInTemp.setField("YD_CAR_PROG_STAT"	, "0");
		    			
		    			/*com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.updYdTcarsch*/
		    			intRtnVal = ydTcarSchDao.updYdTcarsch(recInTemp, 0);
		    			
		    			if(intRtnVal <= 0) {
			    			szMsg="["+szOperationName+"] 상차정지위치동["+sYdBayGp+"]와 대차설비의 현재동["+sYD_TCAR_CURR_BAY_GP+"]이 다른 경우는 대차스케줄["+sYD_TCAR_SCH_ID+"]에 상차작업예약["+sYD_WBOOK_ID+"], 공차대기 상태[0]로 등록 시 오류발생 - 반환값 : " + intRtnVal;
			    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							outRecord.setField("RTN_CD" , "0");	
							outRecord.setField("RTN_MSG", szMsg);	
							return outRecord;
		    			}
		    			
						szMsg="["+szOperationName+"] 상차정지위치동["+sYdBayGp+"]와 대차설비의 현재동["+sYD_TCAR_CURR_BAY_GP+"]이 다른 경우는 대차스케줄["+sYD_TCAR_SCH_ID+"]에 상차작업예약["+sYD_WBOOK_ID+"], 공차대기 상태[0]로 등록 완료 - 반환값 : " + intRtnVal;
		    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    			
		    			//-------------------------------------------------------------------------------------
						//	대차자동출발에 걸리는 것은 ERROR가 아님 (단지 출발말 안함)
		    			//-------------------------------------------------------------------------------------
		    			// 대차 자동 출발 가능 여부 체크

						szMsg="["+szOperationName+"] 대차 자동 출발 가능 여부 체크 시작!!";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    			
		    			intRtnVal = this.Y5EtcTcarCheck(sTcarEqpId, sYD_TCAR_CURR_BAY_GP, sYdAimBayGp);
				    	if(intRtnVal < 1) {
							szMsg="["+szOperationName+"] 대차 자동 출발 가능 여부 체크에 걸림!!";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							
							outRecord.setField("RTN_CD" , "1");	
							outRecord.setField("RTN_MSG", szMsg);	
							return outRecord;

				    	}
				    	// 호출한 쪽에서 (하차나 상차 완료 처리 로직에서 차량 출발 처리 함)
				    	outRecord1 = JDTORecordFactory.getInstance().create();
				    	outRecord1.setField("MSG_ID"			, "YDY5L006");
				    	outRecord1.setField("TCAR_MOVE_SND"		, "Y");
				    	outRecord1.setField("YD_GP"				, "J");
				    	outRecord1.setField("YD_SCH_CD"			, "");
				    	outRecord1.setField("YD_TCAR_SCH_ID"	, sYD_TCAR_SCH_ID);
				    	outRecord1.setField("YD_AIM_BAY_GP"		, sYdBayGp);		//상차동을 목표동구분
						outRecord1.setField("RTN_CD" 			, "1");	
						outRecord1.setField("RTN_MSG"			, szMsg);	
						return outRecord1;
		    		}
	
	    		}
	
	    	}else{
	    		
//-------------------------------------------------------------------------------------
//	대차스케줄이 없는 경우!! 하차작업 후
//-------------------------------------------------------------------------------------
    		
	    		String szYD_LD_UD_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_LD_UD_GP");
	    		
	    		szMsg="["+szOperationName+"] 대차스케줄이 존재하지 않는 경우 - YD_LD_UD_GP["+szYD_LD_UD_GP+"], YD_WBOOK_ID["+sYD_WBOOK_ID+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		
//	    		if( szYD_LD_UD_GP.equals("U") ) {
//	    			szMsg="["+szOperationName+"] 파라미터로 전달된 권상/권하실적처리 호출구분["+szYD_LD_UD_GP+"]이 권상실적처리인 경우 - 하차작업이 끝나고 다음 작업예약을 찾는경우";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	    		}else{
//	    			szMsg="["+szOperationName+"] 파라미터로 전달된 권상/권하실적처리 호출구분["+szYD_LD_UD_GP+"]이 없는 경우 - 권상실적처리 시";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	    		}
	    		
		    	intRtnVal		= 0;
// 작업예약이 없는 경우		    	
				if( sYD_WBOOK_ID.equals("") ) {  //
					szMsg="intRtnVal = " + rsResult.size();
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					szMsg="["+szOperationName+"] 해당 대차설비[" + sTcarEqpId + "]의 작업예약이 등록되어 있지 않으므로 대차스케줄 생성 후 현재동과 홈동이 다르면 공대차출발지시를 전송한다.";
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
					 * 대차 하차 후 다음 대차작업을 찾을때 다음 대차 작업이 없는 경우 현재위치에 대차 스케줄을 생성한다.
					 ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
					
		    		
		    		//대차스케줄을 생성한다.
		    		//대차스케줄1개를 생성하고 해당 작업예약id를 상차작업예약id에 등록한다.
			    	recInTemp = JDTORecordFactory.getInstance().create();
			    	recInTemp.setField("YD_TCAR_SCH_ID", "1");
			    	rsResult1 = JDTORecordFactory.getInstance().createRecordSet("Temp");
			    	
			    	/*com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.getYdTcarschId*/
			    	intRtnVal = ydTcarSchDao.getYdTcarsch(recInTemp, rsResult1, 5);
			    	rsResult1.absolute(1);
			    	
			    	recOutTemp = JDTORecordFactory.getInstance().create();
			    	recOutTemp.setRecord(rsResult1.getRecord());
			    	sYD_TCAR_SCH_ID = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TCAR_SCH_ID");
			    	
			    	recInTemp = JDTORecordFactory.getInstance().create();
			    	//대차스케줄생성
			    	recInTemp.setField("YD_TCAR_SCH_ID"	, sYD_TCAR_SCH_ID);
			    	recInTemp.setField("REGISTER"		, sRegister);
		    		//현재위치가 상차정지위치로등록한다.
		    		
		    		boolean bSEND_START		= false;

			    	//대차상차정지위치-- to동이 지정되어 왔을 경우에는 To동에 상차위치로 잡고 대차출발지시를 내린다.
		    		szMsg="["+szOperationName+"] 지정목표동 : [" +sYD_TO_BAY+ "]";
	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    		
			    	if(sYD_TO_BAY.equals("")){
			    	  //대차상차정지위치-- 상차작업예약이 없고 to동이 지정되어 오지 않은경우에는 홈동으로 보내기위해...
			    		szMsg="["+szOperationName+"] 지정목표동이 존재하지 않으므로 해당 대차설비[" + sTcarEqpId + "]의 대차스케줄["+sYD_TCAR_SCH_ID+"]의 상차정지위치를 홈동["+sYD_HOME_BAY_GP+"], 공차대기[0]으로 지정한다.";
		    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		    			// 입고동 지정여부
		    			if( sRCPT_TCAR_USE_YN.equals("Y") && sTB_YD_EQP_YD_GP.equals("J") ) {
			    		
		    				recInTemp.setField("YD_CAR_PROG_STAT"	,	"0");
					    	recInTemp.setField("YD_CARLD_STOP_LOC"	,	sTB_YD_EQP_YD_GP + sYD_TCAR_CURR_BAY_GP + sTcarEqpId.substring(2));
					    	recInTemp.setField("YD_CARUD_STOP_LOC"	,	sTB_YD_EQP_YD_GP + sRCPT_TCAR_BAY + sTcarEqpId.substring(2));		//하차정지위치		    	
					    	sYdAimBayGp	= sRCPT_TCAR_BAY;    	//대차 목적동
						    
					    	if( !sRCPT_TCAR_BAY.equals(sYD_TCAR_CURR_BAY_GP) ) {
					    	
					    		bSEND_START			= true; // 공대차 출발
					    		szMsg="["+szOperationName+"] 해당 대차설비[" + sTcarEqpId + "]의 대차스케줄["+sYD_TCAR_SCH_ID+"]의 상차정지위치를 입고통동["+sRCPT_TCAR_BAY+"]과 현재동["+sYD_TCAR_CURR_BAY_GP+"]이 다르므로 공대차출발지시 전송한다.";
				    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					    	}

		    			} else {
		    				
		    				recInTemp.setField("YD_CAR_PROG_STAT"	,	"0");
					    	recInTemp.setField("YD_CARLD_STOP_LOC"	,	sTB_YD_EQP_YD_GP + sYD_TCAR_CURR_BAY_GP + sTcarEqpId.substring(2));
					    	recInTemp.setField("YD_CARUD_STOP_LOC"	,	sTB_YD_EQP_YD_GP + sYD_HOME_BAY_GP + sTcarEqpId.substring(2));		//하차정지위치		    	
					    	sYdAimBayGp	= sYD_HOME_BAY_GP;    	//대차 목적동
						    
					    	if( !sYD_HOME_BAY_GP.equals(sYD_TCAR_CURR_BAY_GP) ) {
					    		bSEND_START			= true; // 공대차 출발
					    		szMsg="["+szOperationName+"] 해당 대차설비[" + sTcarEqpId + "]의 대차스케줄["+sYD_TCAR_SCH_ID+"]의 상차정지위치를 홈동["+sYD_HOME_BAY_GP+"]과 현재동["+sYD_TCAR_CURR_BAY_GP+"]이 다르므로 공대차출발지시 전송한다.";
				    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					    	}
		    			}
			    	}else{
				    	//대차상차정지위치-- to동이 지정되어 왔을 경우에는 To동에 상차위치로 잡고 대차출발지시를 내린다.
			    		szMsg="["+szOperationName+"] 지정목표동["+sYD_TO_BAY+"]이 존재하므로 해당 대차설비[" + sTcarEqpId + "]의 대차스케줄["+sYD_TCAR_SCH_ID+"]의 상차정지위치를 지정목표동["+sYD_TO_BAY+"], 공차대기[0]으로 지정한다.";
		    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				    	
		    			recInTemp.setField("YD_CAR_PROG_STAT"	, 	"0");	
				    	recInTemp.setField("YD_CARLD_STOP_LOC"	, 	sTB_YD_EQP_YD_GP + sYD_TO_BAY + sTcarEqpId.substring(2));
				    	sYdAimBayGp	= sYD_TO_BAY;    	//지정된 목적동
					    
				    	if( !sYD_TO_BAY.equals(sYD_TCAR_CURR_BAY_GP) ) {
				    		bSEND_START			= true;
				    		
				    		szMsg="["+szOperationName+"] 해당 대차설비[" + sTcarEqpId + "]의 대차스케줄["+sYD_TCAR_SCH_ID+"]의 상차정지위치를 지정목표동["+sYD_TO_BAY+"]과 현재동["+sYD_TCAR_CURR_BAY_GP+"]이 다르므로 공대차출발지시 전송한다.";
			    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				    	}
			    	}
			    	
			    	recInTemp.setField("YD_CARLD_SCH_REQ_GP", "6");
					recInTemp.setField("YD_CARUD_SCH_REQ_GP", "3");
					recInTemp.setField("YD_EQP_WRK_STAT"	, "U");   //설비작업상태값( 공차로 등록 )
			    	recInTemp.setField("YD_EQP_ID"			, sTcarEqpId);
			    	intRtnVal = ydTcarSchDao.insYdTcarsch(recInTemp);
			    	
		    		if(intRtnVal == -2) {
						szMsg="["+szOperationName+"] parameter error[1]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		    		}
		    		
					szMsg="["+szOperationName+"] 대차하차 후 새로운 대차 작업이 없어 대차스케줄만 생성!!";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					if( bSEND_START ) {

						szMsg="["+szOperationName+"] 대차 자동 출발 가능 여부 체크 시작!!";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						// 대차 자동 출발 가능 여부 체크
		    			intRtnVal = this.Y5EtcTcarCheck(sTcarEqpId, sYD_TCAR_CURR_BAY_GP, sYdAimBayGp);
				    	if(intRtnVal < 1) {
							szMsg="["+szOperationName+"] 대차 자동 출발 가능 여부 체크에 걸림!!";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

							outRecord.setField("RTN_CD" , "1");	
							outRecord.setField("RTN_MSG", szMsg);	
							return outRecord;

				    	}
				    	
				    	//3번대차는 홈동이동을 안 한다.  
				    	if (sTcarEqpId.equals("JXTC03")) {
				    		szMsg="대차이동가능 체크 : JXTC03는 홈동 이동을 안 한다.";
				    		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				    		outRecord.setField("RTN_CD" , "1");	
							outRecord.setField("RTN_MSG", szMsg);	
							return outRecord;
				    	} 

				    	// 호출한 쪽에서 (하차나 상차 완료 처리 로직에서 차량 출발 처리 함)
				    	outRecord1 = JDTORecordFactory.getInstance().create();
				    	outRecord1.setField("MSG_ID"			, "YDY5L006");
				    	outRecord1.setField("TCAR_MOVE_SND"		, "Y");
				    	outRecord1.setField("YD_GP"				, sTB_YD_EQP_YD_GP);
				    	outRecord1.setField("YD_SCH_CD"			, "");
				    	outRecord1.setField("YD_TCAR_SCH_ID"	, sYD_TCAR_SCH_ID);
				    	outRecord1.setField("YD_AIM_BAY_GP"		, sYdAimBayGp);
						outRecord1.setField("RTN_CD" 			, "1");	
						outRecord1.setField("RTN_MSG"			, szMsg);	
						return outRecord1;
					}
					
					outRecord.setField("RTN_CD" , "1");	
					outRecord.setField("RTN_MSG", szMsg);	
					return outRecord;
		    	} else {
			    	
//대차 작업예약이 존재하는 경우//대차스케줄1개를 생성하고 해당 작업예약id를 상차작업예약id에 등록한다.
			    	
			    	szMsg="["+szOperationName+"] 해당 대차설비[" + sTcarEqpId + "]의 작업예약["+sYD_WBOOK_ID+"]이 등록되어 있으므로 대차스케줄 생성 후 현재동과 목표동이 다르면 공대차출발지시를 전송한다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			    	
			    	//대차스케줄1개를 생성하고 해당 작업예약id를 상차작업예약id에 등록한다.
			    	recInTemp = JDTORecordFactory.getInstance().create();
			    	recInTemp.setField("YD_TCAR_SCH_ID", "1");
			    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
			    	
			    	/*com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.getYdTcarschId*/
			    	intRtnVal = ydTcarSchDao.getYdTcarsch(recInTemp, rsResult, 5);
			    	rsResult.absolute(1);
			    	recOutTemp = JDTORecordFactory.getInstance().create();
			    	recOutTemp.setRecord(rsResult.getRecord());
			    	sYD_TCAR_SCH_ID = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TCAR_SCH_ID");
			    	
			    	recInTemp = JDTORecordFactory.getInstance().create();
			    	//대차스케줄생성
			    	recInTemp.setField("YD_TCAR_SCH_ID"			, sYD_TCAR_SCH_ID);
			    	recInTemp.setField("YD_CARLD_WRK_BOOK_ID"	, sYD_WBOOK_ID);
			    	recInTemp.setField("REGISTER"				, sRegister);
			    	recInTemp.setField("YD_CARLD_STOP_LOC"		, sTB_YD_EQP_YD_GP + sYdBayGp    + sYdWrkPlanTcar.substring(2,6));
			    	recInTemp.setField("YD_CARUD_STOP_LOC"		, sTB_YD_EQP_YD_GP + sYdAimBayGp + sYdWrkPlanTcar.substring(2,6));
			    	recInTemp.setField("YD_CARLD_SCH_REQ_GP"	, "6");              
					recInTemp.setField("YD_CARUD_SCH_REQ_GP"	, "3");
					recInTemp.setField("YD_EQP_WRK_STAT", "U");
					recInTemp.setField("YD_EQP_ID", sYdWrkPlanTcar);    //야드설비ID
			    	
					if(sYD_TCAR_CURR_BAY_GP.equals(sYdBayGp)) {
						//현재동과 상차동이 같으므로 상차도착 상태로 등록
						recInTemp.setField("YD_CAR_PROG_STAT"	, "2");
					}else{
						//현재동과 상차동이 다르므로 공차대기 상태로 등록
						recInTemp.setField("YD_CAR_PROG_STAT"	, "0");
					}
					
			    	
			    	intRtnVal = ydTcarSchDao.insYdTcarsch(recInTemp);
	    			if(intRtnVal <= 0) {
						szMsg="parameter error";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" , "0");	
						outRecord.setField("RTN_MSG", szMsg);	
						return outRecord;
		    		}
		    		
		    		
	 				szMsg="szYD_TCAR_CURR_BAY_GP : " + sYD_TCAR_CURR_BAY_GP + " ,  szYdBayGp : " + sYdBayGp + ": 대차스케쥴 등록";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    		
		    		
		    		//현재 대차동과 작업예약의 동과 일치하면 스케줄 호출 /비일치시 대차출발지시를 내린다.
		    		if(sYD_TCAR_CURR_BAY_GP.equals(sYdBayGp)) {

		    			recInTemp = JDTORecordFactory.getInstance().create();
		    			recInTemp.setField("MSG_ID"		, "YDYDJ509");
		    			recInTemp.setField("YD_SCH_CD"	, sYdSchCd);
		    			
		    			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		    			intRtnVal = ydSchRuleDao.getYdSchrule(recInTemp, rsResult, 0);
		    			if(intRtnVal <= 0) {
		        			szMsg="["+szOperationName+"] 스케줄 기준 조회중 값이없거나 Error Code : " + intRtnVal;
		        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							outRecord.setField("RTN_CD" , "0");	
							outRecord.setField("RTN_MSG", szMsg);	
							return outRecord;
		    			}
		    			
		    			rsResult.absolute(1);
		    			recOutTemp = JDTORecordFactory.getInstance().create();
		        		recOutTemp.setRecord(rsResult.getRecord());
		    			
		    			//크레인 스케줄 호출
		    			szMsg="["+szOperationName+"] 대차가 이미 도착해있으므로 크레인 스케줄 호출";
		    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    			
		    			szMsg="["+szOperationName+"] 대차 상차 스케줄("+szMethodName+") 완료";
		    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	
		    			outRecord.setField("MSG_ID" 			, "YDYDJ509");	
		    			outRecord.setField("YD_SCH_CD" 			, sYdSchCd);	
		    			outRecord.setField("YD_EQP_ID" 			, ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WRK_CRN"));	
		    			outRecord.setField("CRN_YD_SCH_CD_SND" 	, "Y");	
		    			outRecord.setField("RTN_CD" 			, "1");	
						outRecord.setField("RTN_MSG"			, szMsg);	
						return outRecord;
	
		    		} else {
	
						szMsg="["+szOperationName+"] 대차 자동 출발 가능 여부 체크 시작!!";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		    			// 대차 자동 출발 가능 여부 체크
		    			intRtnVal = this.Y5EtcTcarCheck(sTcarEqpId, sYD_TCAR_CURR_BAY_GP, sYdBayGp);
				    	if(intRtnVal < 1) {
							szMsg="["+szOperationName+"] 대차 자동 출발 가능 여부 체크에 걸림!!";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
							outRecord.setField("RTN_CD" , "1");	
							outRecord.setField("RTN_MSG", szMsg);	
							return outRecord;
				    	}
		
				    	// 호출한 쪽에서 (하차나 상차 완료 처리 로직에서 차량 출발 처리 함)
				    	outRecord1 = JDTORecordFactory.getInstance().create();
				    	outRecord1.setField("MSG_ID"			, "YDY5L006");
				    	outRecord1.setField("TCAR_MOVE_SND"		, "Y");
				    	outRecord1.setField("YD_GP"				, sYdGp);
				    	outRecord1.setField("YD_SCH_CD"			, sYdSchCd);
				    	outRecord1.setField("YD_TCAR_SCH_ID"	, sYD_TCAR_SCH_ID);
				    	outRecord1.setField("YD_AIM_BAY_GP"		, sYdBayGp);		
						outRecord1.setField("RTN_CD" 			, "1");	
						outRecord1.setField("RTN_MSG"			, szMsg);	
						return outRecord1;
		    		}
		    	}	
	    	}
	    	
			outRecord.setField("RTN_CD" , "1");	
			outRecord.setField("RTN_MSG", szMsg);	
			return outRecord;
	    	
	    	
		}catch(Exception e){
	
			szMsg="["+szOperationName+"] 공대차 이동지시 Error : " +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			outRecord.setField("RTN_CD" , "0");	
			outRecord.setField("RTN_MSG", szMsg);	
			return outRecord;
			
		}
	
	} //end of Y5L2TcarSch()
	
	
	
	

	/**
	 * 오퍼레이션명 : 대차 상차 스케줄(야드에서 대차로 권하후)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public JDTORecord Y5UdTcarSch(JDTORecord msgRecord)throws JDTOException  {
		YdTcarSchDao     ydTcarSchDao   = new YdTcarSchDao(); 
		YdWrkbookDao     ydWrkbookDao   = new YdWrkbookDao();
		YdWrkbookMtlDao  ydWrkbookMtlDao = new YdWrkbookMtlDao();
		YdTcarFtmvMtlDao ydTcarFtmvMtlDao = new YdTcarFtmvMtlDao();
		YdEqpDao     	 ydEqpDao     		= new YdEqpDao();
		YdSchRuleDao     ydSchRuleDao     = new YdSchRuleDao();
		
		JDTORecordSet rsResultMakeWrkBook      = null;
		JDTORecordSet rsResult      	= null;
		JDTORecordSet rsWrkBookMtl      = null;
		JDTORecordSet rsWRKBOOK         = null;
		JDTORecordSet rsTcarFtMtl       = null;
		JDTORecordSet rsResult1         = null;
		String szYD_TCAR_CURR_BAY_GP    = "";
		
		JDTORecord outRecord  			= JDTORecordFactory.getInstance().create();	
		JDTORecord outRecord1  			= JDTORecordFactory.getInstance().create();	
		
		JDTORecord    recInTemp         = null;
		JDTORecord    recOutTemp        = null;
		JDTORecord    recWrkBookMtl     = null;
		EJBConnector 	ejbConn 			= null;
		
	    int intRtnVal 					= 0;
	    int intWbookMtlCnt              = 0;
	    int iTC_CNT                     = 0;
	    int intTcarFtmvMtlCnt           = 0;
	    long lngSUM_MTL_WT              = 0;
	    long lngMTL_WT              	= 0;
	    long lngSUM_MTL_CNT             = 0;
	    String szTcarEqpId              = "";
	    	    
	    String szMsg              		= "";
	    String szMethodName       		= "Y5UdTcarSch";
	    
	    String szWbookId                = "";
	    String szYD_SCH_CD              = "";
	    String szYD_TCAR_SCH_ID			= "";
	    String sTO_DONG          		= "";
	    String sTO_LOC          		= "";
	    String sYD_GP 				= "";
	    String sYD_BAY_GP 			= "";
		String sYD_SCH_CD           = "";    
		String sRTN_CD			= null;
		String sRTN_MSG			= null;
		
	    boolean bSND_FLAG				= false;
 
	    try{
			szMsg="대차 상차 스케줄";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
			//작업예약ID로 대차상차작업예약ID로 대차스케줄을 조회해서 대차스케줄ID를 조회한다.
	    	szWbookId 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");
	    	szTcarEqpId	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");	

	    	//-------------------------------------------------------------------------------------
	    	//	대차설비ID로 설비테이블을 조회한다.
	    	//-------------------------------------------------------------------------------------
	    	
	    	szMsg="["+szMethodName+"] 대차설비ID(" + szTcarEqpId + ")로 설비테이블 조회 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			rsResult1 = JDTORecordFactory.getInstance().createRecordSet("");
			
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_EQP_ID", 			szTcarEqpId);
			/*com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqp*/
			intRtnVal = ydEqpDao.getYdEqp(recInTemp, rsResult1, 0);
			
			szMsg="["+szMethodName+"] 대차설비ID(" + szTcarEqpId + ")로 설비테이블 조회 완료 - 반환값 : " + intRtnVal;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			if(intRtnVal <= 0) {
				szMsg="["+szMethodName+"] 대차설비ID(" + szTcarEqpId + ")로 설비테이블 조회 시 오류발생 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//				return intRtnVal = -1;
   				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;
				
			}
			
			rsResult1.absolute(1);
			recOutTemp = JDTORecordFactory.getInstance().create();
			recOutTemp.setRecord(rsResult1.getRecord());
			
			szYD_TCAR_CURR_BAY_GP 	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CURR_BAY_GP");	//현재동
//////////////////////////////////
			
			//상차 완료 Check를 한다.
	    	recInTemp = JDTORecordFactory.getInstance().create();
//	    	recInTemp.setField("YD_CARLD_WRK_BOOK_ID", szWbookId);
	    	recInTemp.setField("YD_WBOOK_ID",          szWbookId);
	    	
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	/*com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookMtlbyCarLdWBookIdCoil*/
/*
SELECT COUNT(A.STL_NO) AS TCARFTMVMTL_CNT                          
     , A.YD_TCAR_SCH_ID AS YD_TCAR_SCH_ID                          
     , C.YD_GP                       
	 , C.YD_BAY_GP    
	 ,  C.YD_SCH_CD    
  FROM TB_YD_TCARFTMVMTL A                                                  
      ,TB_YD_WRKBOOKMTL  B                                                   
      ,TB_YD_WRKBOOK     C                                                   
 WHERE A.STL_NO = B.STL_NO                                                  
   AND B.YD_WBOOK_ID = C.YD_WBOOK_ID                                                  
   AND A.YD_TCAR_SCH_ID = (SELECT YD_TCAR_SCH_ID                            
                             FROM TB_YD_TCARSCH                             
                            WHERE YD_CARLD_WRK_BOOK_ID = :V_YD_WBOOK_ID
                              AND (DEL_YN IS NULL OR DEL_YN <> 'Y') )           
   AND B.YD_WBOOK_ID    = :V_YD_WBOOK_ID
   AND (A.DEL_YN IS NULL OR A.DEL_YN <> 'Y')   
 GROUP BY A.YD_TCAR_SCH_ID,C.YD_GP, C.YD_BAY_GP ,  C.YD_SCH_CD      
 */	    	
	    	intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recInTemp, rsResult, 306);

	    	if(intRtnVal <= 0) {
		    	szMsg="작업예약재료와 대차이송재료Table중 재료번호가 같은것의 갯수 조회중 Error Error Code : " + rsResult.size();
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	    	}
	    	rsResult.absolute(1);
	    	recOutTemp = JDTORecordFactory.getInstance().create();
	    	recOutTemp.setRecord(rsResult.getRecord());
	    	

	    	intTcarFtmvMtlCnt 	= recOutTemp.getFieldInt("TCARFTMVMTL_CNT");
			szYD_TCAR_SCH_ID 	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TCAR_SCH_ID");
	    	sYD_GP 				= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_GP");
	    	sYD_BAY_GP 			= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_BAY_GP");
	    	sYD_SCH_CD 			= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_SCH_CD");
	    	
			szMsg="대차이송상차재료 매수 : " + intTcarFtmvMtlCnt;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
	    	recOutTemp = JDTORecordFactory.getInstance().create();
	    	recOutTemp.setField("YD_TCAR_SCH_ID", szYD_TCAR_SCH_ID);
	    	recOutTemp.setField("YD_WBOOK_ID", szWbookId);
	    	rsTcarFtMtl = JDTORecordFactory.getInstance().createRecordSet("");
	    	/*com.inisteel.cim.yd.dao.ydtcarftmvmtldao.YdTcarftmvmtlDao.getYdTcarftmvmtlYdStockTcarSchId*/
/*SELECT 
	    A.YD_TCAR_SCH_ID  AS YD_TCAR_SCH_ID
	  , A.STL_NO  AS STL_NO
	  , A.REGISTER  AS REGISTER
	  , TO_CHAR(A.REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
	  , A.MODIFIER  AS MODIFIER
	  , TO_CHAR(A.MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT
	  , A.DEL_YN  AS DEL_YN
	  , A.YD_STK_BED_NO  AS YD_STK_BED_NO
	  , A.YD_STK_LYR_NO  AS YD_STK_LYR_NO
	  , A.HCR_GP  AS HCR_GP
	  , A.STL_PROG_CD  AS STL_PROG_CD
	  , A.YD_MTL_ITEM  AS YD_MTL_ITEM
	  , A.YD_ROUTE_GP  AS YD_ROUTE_GP
      , B.YD_AIM_RT_GP AS YD_AIM_RT_GP
      , B.YD_AIM_YD_GP AS YD_AIM_YD_GP
      , B.YD_AIM_BAY_GP AS YD_AIM_BAY_GP
	  , (SELECT SUM(T2.YD_MTL_WT)
	       FROM TB_YD_TCARFTMVMTL T1
	          , TB_YD_STOCK T2
          WHERE T1.YD_TCAR_SCH_ID = A.YD_TCAR_SCH_ID
	        AND T1.STL_NO = T2.STL_NO) SUM_MTL_WT
	  , (SELECT COUNT(1)
	       FROM TB_YD_TCARFTMVMTL T1
	          , TB_YD_STOCK T2
          WHERE T1.YD_TCAR_SCH_ID = A.YD_TCAR_SCH_ID
	        AND T1.STL_NO = T2.STL_NO) SUM_MTL_CNT
  FROM TB_YD_TCARFTMVMTL A
	 , TB_YD_STOCK B
	 , TB_YD_WRKBOOKMTL  C     
 WHERE A.YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
	AND A.STL_NO = B.STL_NO
	AND A.STL_NO = C.STL_NO
	AND (A.DEL_YN <> 'Y' OR A.DEL_YN IS NULL)
	AND (B.DEL_YN <> 'Y' OR B.DEL_YN IS NULL)
	AND (C.DEL_YN <> 'Y' OR C.DEL_YN IS NULL)
ORDER BY YD_STK_BED_NO, YD_STK_LYR_NO
*/	    	
	    	intRtnVal   = ydTcarFtmvMtlDao.getYdTcarftmvmtl(recOutTemp, rsTcarFtMtl, 300);
	    	if(intRtnVal <= 0) {
    			if(intRtnVal == 0) {
    				szMsg=" ydTcarFtmvMtlDao data not found";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);	
    			}else if(intRtnVal == -2) {
    				szMsg=" ydTcarFtmvMtlDao parameter error";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
    			}
   				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;
	
//    			return intRtnVal = -1;
    		}
	    	
	   
	    	rsTcarFtMtl.last();
	    	recOutTemp = JDTORecordFactory.getInstance().create();
	    	recOutTemp.setRecord(rsTcarFtMtl.getRecord());
	    	lngSUM_MTL_WT 	= ydDaoUtils.paraRecChkNullLong(recOutTemp, "SUM_MTL_WT");
	    	lngSUM_MTL_CNT 	= ydDaoUtils.paraRecChkNullLong(recOutTemp, "SUM_MTL_CNT");
	    	
    		szMsg="현재 대차이송재료의 총중량 : " + lngSUM_MTL_WT;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	    	rsResultMakeWrkBook = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	intRtnVal = this.Y5MakeWrkBook(msgRecord, rsTcarFtMtl, rsResultMakeWrkBook);
	    	if(intRtnVal == -1) {
   				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;
	
//	    		return intRtnVal;
	    	}

			
			//대차이송재료의 총 무게가 75톤 이상이라면 하차출발!!
			if((lngSUM_MTL_CNT >= 3)) {   
			 	bSND_FLAG			= true;
			} else {
		    	
				// 동일 작업이 더있는지 작업예약을 check 하여 없을 경우 출발처리 함 
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_WRK_PLAN_TCAR"	, szTcarEqpId);
				recInTemp.setField("YD_GP"				, sYD_GP);
				recInTemp.setField("YD_WBOOK_ID"		, szWbookId); //현 에약은 제외
				recInTemp.setField("YD_SCH_CD"			, sYD_SCH_CD); //동일 sch
				
		    	rsWrkBookMtl = JDTORecordFactory.getInstance().createRecordSet("Temp");
		    	/*com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getTCarWrkWaitListCoilSchCd*/
		    	intRtnVal = ydWrkbookDao.getYdWrkbook(recInTemp, rsWrkBookMtl, 400);
		    	szMsg="작업 카운트 : " + intRtnVal;
		    	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    	if(intRtnVal == 0  ) {
		    		if( "H".equals(sYD_GP)){
		    		//상차동에 더이상 동간이적 대상이 존재 안하는 경우
		    		bSND_FLAG			= true;	
		    		}
		    	} else {
			    	rsWrkBookMtl.absolute(1);
			    	recOutTemp = JDTORecordFactory.getInstance().create();
			    	recOutTemp.setRecord(rsWrkBookMtl.getRecord());
			    	lngMTL_WT 			= ydDaoUtils.paraRecChkNullLong(recOutTemp, "YD_MTL_WT");
			    	String sYD_WBOOK_ID = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WBOOK_ID");
			    	
			    	szMsg="비교중량 체크  : " + lngSUM_MTL_WT +"-" + lngMTL_WT;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
			    	if((lngSUM_MTL_WT + lngMTL_WT) > YdConstant.YD_COIL_TC_WEIGH_MAX){
			    		bSND_FLAG			= true;	
			    	} else {
			    		
				    	szMsg="동일 작업 카운트 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			    	
		    			recInTemp = JDTORecordFactory.getInstance().create();
		    			recInTemp.setField("MSG_ID",    "YDYDJ509");
		    			recInTemp.setField("YD_WBOOK_ID", sYD_WBOOK_ID);
		    			recInTemp.setField("YD_SCH_CD"  , sYD_SCH_CD);
		    			//-------------------------------------------------------------------------------------
					    //	크레인설비ID를 구하기 위해서 스케줄기준 조회후 스케줄 기동
						//-------------------------------------------------------------------------------------
	  				
		    			szMsg="[] 크레인설비ID를 구하기 위해서 스케줄코드[]로 스케줄기준 조회 시작";
		    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	  			
		    			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		    			intRtnVal = ydSchRuleDao.getYdSchrule(recInTemp, rsResult, 0);
		    			if(intRtnVal <= 0) {
		    				szMsg="[] 크레인설비ID를 구하기 위해서 스케줄코드로 스케줄기준 조회 시 오류발생 - 반환값 : " + intRtnVal;
		        			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		       				outRecord.setField("RTN_CD" , "0");	
		    				outRecord.setField("RTN_MSG", szMsg);	
		    				return outRecord;
	//	        			throw new DAOException(szMsg);
		    			}
	  				
		    			rsResult.absolute(1);
		    			recOutTemp = JDTORecordFactory.getInstance().create();
		        		recOutTemp.setRecord(rsResult.getRecord());
	
		    			recInTemp.setField("YD_EQP_ID", ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WRK_CRN"));
		    			
		    			
		    			//장애 발생시 이전 소스로 원복 하기 위한 조치
		    			ymCommonDAO dao = ymCommonDAO.getInstance();
		    			List sposYNChklist 	= null;
		    			String QueryId 	= "com.inisteel.cim.yd.dao.ydstkcoldao.YdStockDao.updYdStockchklist";
		    			sposYNChklist = dao.getCommonList(QueryId, new Object[]{});
		    
		    		    JDTORecord unloadPointrec = (JDTORecord)sposYNChklist.get(0);
		    	    	String CHK   = StringHelper.evl(unloadPointrec.getFieldString("CHK"), "");
		    	    	if(CHK.equals("Y")){
		    	    		//트렌젝션 분리 적용
		    	    		
		    	    		szMsg="크레인 스케줄 호출(EJB방식)";
		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    				
			    			ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
			    			outRecord1=(JDTORecord)ejbConn.trx("procY5CrnSchMain", new Class[] { JDTORecord.class },	new Object[] { recInTemp });
					
							
		    	    	}else{
						
		    	    		szMsg="크레인 스케줄 호출(TC방식)";
		    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    				
		    	    		ydDelegate.sendMsg(recInTemp);		    		
		    	    	}
		    			
		    			outRecord.setField("MSG_ID" 			, "YDYDJ509");	
		    			outRecord.setField("YD_SCH_CD" 			, sYD_SCH_CD);	
		    			outRecord.setField("YD_WBOOK_ID" 		, sYD_WBOOK_ID);	
		    			outRecord.setField("YD_EQP_ID" 			, ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WRK_CRN"));	
		    			outRecord.setField("CRN_YD_SCH_CD_SND" 	, "Y");	
		    			outRecord.setField("RTN_CD" , "1");	
		    			return outRecord;	
			    	}
		    	}
			}	
			
			
			if( bSND_FLAG ) {
				szMsg="하차작업예약 생성 및 등록";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		    	//하차 작업예약 및 작업예약재료 생성
		    	msgRecord.setField("YD_TCAR_SCH_ID"  , szYD_TCAR_SCH_ID);
		    	msgRecord.setField("YD_WRK_PLAN_TCAR", szTcarEqpId);
			    	  
//		    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
//		    	intRtnVal = this.Y5MakeWrkBook(msgRecord, rsTcarFtMtl, rsResult);
//		    	if(intRtnVal == -1) return intRtnVal;
		    	
		    	//대차스케줄에 하차작업예약id 등록
				szMsg="대차스케줄에 하차작업예약id 등록";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				rsResultMakeWrkBook.absolute(1);
		    	recWrkBookMtl = JDTORecordFactory.getInstance().create();
		    	recWrkBookMtl.setRecord(rsResultMakeWrkBook.getRecord());
		    	sTO_DONG = ydDaoUtils.paraRecChkNull(recWrkBookMtl, "YD_AIM_BAY_GP");
		    	sTO_LOC  = ydDaoUtils.paraRecChkNull(recWrkBookMtl, "YD_GP")+ sTO_DONG + szTcarEqpId.substring(2,6);
		    	
		    	recInTemp     = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_TCAR_SCH_ID"			, szYD_TCAR_SCH_ID);
				recInTemp.setField("YD_CARUD_WRK_BOOK_ID"	, ydDaoUtils.paraRecChkNull(recWrkBookMtl, "YD_WBOOK_ID"));
				recInTemp.setField("YD_EQP_WRK_STAT"		, "L");
				recInTemp.setField("YD_CAR_PROG_STAT"		, "5");
				recInTemp.setField("YD_CARUD_STOP_LOC"		, sTO_LOC);
				
				intRtnVal = ydTcarSchDao.updYdTcarsch(recInTemp, 0);
				if(intRtnVal <= 0) {
					szMsg=" 대차스케줄에 하차작업예약id 등록실패!!";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	   				outRecord.setField("RTN_CD" , "0");	
					outRecord.setField("RTN_MSG", szMsg);	
					return outRecord;
		
//					return intRtnVal = -1;
	    		}
				
				
				rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				intRtnVal = ydWrkbookDao.getYdWrkbook(recWrkBookMtl, rsResult, 0);
				if(intRtnVal <= 0) {
//					return intRtnVal = -1;
					szMsg="대차 자동 출발 가능 여부 체크에 걸림!!";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	   				outRecord.setField("RTN_CD" , "0");	
					outRecord.setField("RTN_MSG", szMsg);	
					return outRecord;
				}
				
				szMsg=" 대차 자동 출발 가능 여부 체크 시작!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				rsResult.absolute(1);
    			// 대차 자동 출발 가능 여부 체크
    			intRtnVal = this.Y5EtcTcarCheck(szTcarEqpId, szYD_TCAR_CURR_BAY_GP, sTO_DONG);
		    	if(intRtnVal < 1) {
					szMsg=" 대차 자동 출발 가능 여부 체크에 걸림!!";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	   				outRecord.setField("RTN_CD" , "1");	
					outRecord.setField("RTN_MSG", szMsg);	
					return outRecord;
		
//		    		return intRtnVal;
		    	}
				
		    	// 호출한 쪽에서 (하차나 상차 완료 처리 로직에서 차량 출발 처리 함)
		    	outRecord1 = JDTORecordFactory.getInstance().create();
		    	outRecord1.setField("MSG_ID"			, "YDY5L006");
		    	outRecord1.setField("TCAR_MOVE_SND"		, "Y");
		    	outRecord1.setField("YD_GP"				, ydDaoUtils.paraRecChkNull(recWrkBookMtl, "YD_GP"));
		    	outRecord1.setField("YD_SCH_CD"			, ydDaoUtils.paraRecChkNull(recWrkBookMtl, "YD_SCH_CD"));
		    	outRecord1.setField("YD_TCAR_SCH_ID"	, szYD_TCAR_SCH_ID);
		    	outRecord1.setField("YD_AIM_BAY_GP"		, sTO_DONG);		
				outRecord1.setField("RTN_CD" 			, "1");	
				outRecord1.setField("RTN_MSG"			, szMsg);	
				return outRecord1;
	
				
//				recInTemp = JDTORecordFactory.getInstance().create();
//				recInTemp.setField("MSG_ID", "YDY5L006");
//				recInTemp.addRecord(rsResult.getRecord());
//				//영대차출발지시
//	    		recInTemp.setField("YD_TCAR_SCH_ID", szYD_TCAR_SCH_ID);
//	    		recInTemp.setField("YD_GP",          ydDaoUtils.paraRecChkNull(recWrkBookMtl, "YD_GP"));
//	    		recInTemp.setField("YD_SCH_CD",      ydDaoUtils.paraRecChkNull(recWrkBookMtl, "YD_SCH_CD"));
//	    		
//	    		
//				szMsg="영대차출발지시!!  MSG_IG : " + recInTemp.getFieldString("MSG_ID") + " 전송";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//	    		ydDelegate.sendMsg(recInTemp);	
							
			} else {
		    	//상차계시로
				szMsg="대차스케줄에 하차작업예약id 등록";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				rsResultMakeWrkBook.absolute(1);
		    	recWrkBookMtl = JDTORecordFactory.getInstance().create();
		    	recWrkBookMtl.setRecord(rsResultMakeWrkBook.getRecord());
		    	sTO_DONG = ydDaoUtils.paraRecChkNull(recWrkBookMtl, "YD_AIM_BAY_GP");
		    	sTO_LOC  = ydDaoUtils.paraRecChkNull(recWrkBookMtl, "YD_GP")+ sTO_DONG + szTcarEqpId.substring(2,6);
		    	
		    	recInTemp     = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_TCAR_SCH_ID"			, szYD_TCAR_SCH_ID);
				recInTemp.setField("YD_CARUD_WRK_BOOK_ID"	, ydDaoUtils.paraRecChkNull(recWrkBookMtl, "YD_WBOOK_ID"));
				recInTemp.setField("YD_EQP_WRK_STAT"		, "L");
				recInTemp.setField("YD_CAR_PROG_STAT"		, "4");
				recInTemp.setField("YD_CARUD_STOP_LOC"		, sTO_LOC);
				
				intRtnVal = ydTcarSchDao.updYdTcarsch(recInTemp, 0);
				if(intRtnVal <= 0) {
					szMsg=" 대차스케줄에 하차작업예약id 등록실패!!";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	   				outRecord.setField("RTN_CD" , "0");	
					outRecord.setField("RTN_MSG", szMsg);	
					return outRecord;
		
//					return intRtnVal = -1;
	    		}
				
				
				
			}

			outRecord.setField("RTN_CD" , "1");	
			return outRecord;

		}catch(Exception e){
			szMsg="대차 상차 스케줄 Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			return intRtnVal = -1;
			outRecord.setField("RTN_CD" , "0");	
			outRecord.setField("RTN_MSG", szMsg);	
			return outRecord;
			
		}
	
	} //end of Y5UdTcarSch()
    
	
	
	/**
	 * 오퍼레이션명 : 대차이동가능 체크
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public int Y5EtcTcarCheck(String sYD_EQP_ID, String sFROM_LOC, String sTO_LOC)throws JDTOException  {
		YdEqpDao     	 ydEqpDao     		= new YdEqpDao();
		
		JDTORecordSet rsResult          = null;
		 
		JDTORecord    recInTemp         = null;
		JDTORecord    recOutTemp        = null;
		
		
	    int intRtnVal 					= 0;
	    
	    String szMsg              		= "";
	    String szMethodName       		= "Y5EtcTcarCheck";
	    
		String sYD_EQP_WRK_MODE			= "";	//야드설비작업MODE	
		String sYD_CURR_BAY_GP 			= "";	//야드현재동구분 		
	    String sETC_YD_EQP_ID          	= "";
	    String sUSE_YN					= "";	//사용가능 Y,N	
	    String sBRE_CNT  				= "";	//가능 획수 		
		
	    
	    boolean bSND_FLAG				= false;
	    try{
			
// 상대 대차 CHECK
// C증설	    	
	    	if (sYD_EQP_ID.equals("JXTC01")) {
	    		sETC_YD_EQP_ID = "JXTC02";
	    	} else if (sYD_EQP_ID.equals("JXTC02")) {
	    		sETC_YD_EQP_ID = "JXTC01";
	    	} else {
	    		szMsg="대차이동가능 체크 : JXTC01,JXTC02 가 아니라서 무조건 OK";
	    		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		return intRtnVal = 1;
	    	}
	    			
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_EQP_ID", sETC_YD_EQP_ID);
	    	
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	    	/*com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqp*/
	    	intRtnVal = ydEqpDao.getYdEqp(recInTemp, rsResult, 0);
			if(intRtnVal <= 0) {
    			return intRtnVal = -1;
    		}
			
			rsResult.absolute(1);
			recOutTemp = JDTORecordFactory.getInstance().create();
			recOutTemp.setRecord(rsResult.getRecord());
			sYD_EQP_WRK_MODE	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_WRK_MODE");	//야드설비작업MODE	
			sYD_CURR_BAY_GP 	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CURR_BAY_GP");	//야드현재동구분 		
			
			//sFROM_LOC, String sTO_LOC
			if(!(sYD_EQP_WRK_MODE.equals("1"))) {  //설비가 온라인이 아닌경우 무조건 안보냄
				return intRtnVal = -1;
			}
			
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_EQP_NO"		, sYD_EQP_ID);
	    	recInTemp.setField("YD_BAY_GP"		, sYD_CURR_BAY_GP);
	    	recInTemp.setField("YD_CURR_BAY_GP"	, sFROM_LOC);
	    	recInTemp.setField("YD_AIM_BAY_GP"	, sTO_LOC);
	    	
	    	
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("");
	    	/*com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpTcBre*/
	    	intRtnVal = ydEqpDao.getYdEqp(recInTemp, rsResult, 300);
			if(intRtnVal <= 0) {
    			return intRtnVal = -1;
    		}
			
			rsResult.absolute(1);
			recOutTemp = JDTORecordFactory.getInstance().create();
			recOutTemp.setRecord(rsResult.getRecord());
			sUSE_YN		= ydDaoUtils.paraRecChkNull(recOutTemp, "USE_YN");	//사용가능 Y,N	
			sBRE_CNT  	= ydDaoUtils.paraRecChkNull(recOutTemp, "BRE_CNT");	//가능 획수 		
			if(!(sBRE_CNT.equals("1"))) {  
				return intRtnVal = -1;
			}			
			if(sUSE_YN.equals("N")) {  
				return intRtnVal = -1;
			}			
			
		}catch(Exception e){
			szMsg="대차이동가능 체크 Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return intRtnVal = -1;
		}
	
		szMsg="대차이동가능 체크("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		return intRtnVal = 1;
	} //end of Y5UdTcarSch()
    
	
	
	
	
	
	
	
	/**
	 * 오퍼레이션명 : 대차 하차 스케줄(대차에서 야드로 권상후)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public JDTORecord Y5LdTcarSch(JDTORecord msgRecord)throws JDTOException  {
		YdTcarSchDao     ydTcarSchDao     = new YdTcarSchDao(); 
		YdTcarFtmvMtlDao ydTcarFtmvMtlDao = new YdTcarFtmvMtlDao();
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		
		JDTORecord outRecord  			= JDTORecordFactory.getInstance().create();	
		JDTORecord outRecord1 			= JDTORecordFactory.getInstance().create();	
		JDTORecordSet rsResult          = null;
		JDTORecordSet rsTcarSch         = null;
		JDTORecord    recInTemp         = null;
		JDTORecord    recOutTemp        = null;
	   
		int intRtnVal 					= 0 ;
	    
	    String szMsg              		= "";
	    String szMethodName       		= "Y5LdTcarSch";
	    String szOperationName			= "대차하차스케줄";
	    
	    String szWbookId                = "";
	    String szQuery                  = "";
	    String szYD_TCAR_SCH_ID			= "";
	    String szYD_EQP_ID				= null;
	    String sRTN_CD		= "";
	    String sRTN_MSG	= "";
	    String sTCAR_MOVE_SND	= "";

	    try{
			//하차 완료 Check를 한다.
	    	//하차작업예약ID로 대차하차작업예약ID로 대차스케줄을 조회해서 대차스케줄ID를 조회한다.
	    	szWbookId   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");
	    	szYD_EQP_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
	    	//대차스케줄 조회용
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_BOOK_ID", szWbookId);
	    	rsTcarSch = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	/*com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.getYdTcarschUD*/
	    	intRtnVal = ydTcarSchDao.getYdTcarsch(recInTemp, rsTcarSch, 201);
	    	
	    	//대차이송재료조회
	    	rsTcarSch.absolute(1);
	    	recOutTemp = JDTORecordFactory.getInstance().create();
	    	recOutTemp.setRecord(rsTcarSch.getRecord());
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	
	    	szYD_TCAR_SCH_ID	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TCAR_SCH_ID");
	    	szYD_EQP_ID			= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_ID");
	    	
	    	//대차이송재료가 전부 삭제상태인지 확인한다.
	    	/*com.inisteel.cim.yd.dao.ydtcarftmvmtldao.YdTcarftmvmtlDao.getYdTcarftmvmtlId*/
	    	intRtnVal = ydTcarFtmvMtlDao.getYdTcarftmvmtl(recOutTemp, rsResult, 1);
	
	    	
	    	//하차가 완료된 경우 
	    	if(intRtnVal == 0) {
	    		//대차스케줄 삭제처리
	    		recInTemp = JDTORecordFactory.getInstance().create();
	    		recInTemp.setField("YD_EQP_WRK_STAT"	, "U");
	    		recInTemp.setField("DEL_YN"				, "Y");
	    		recInTemp.setField("YD_TCAR_SCH_ID"		, ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TCAR_SCH_ID"));
				recInTemp.setField("YD_CAR_PROG_STAT"	, "E"); 
				/*com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.updYdTcarsch*/
	    		intRtnVal = ydTcarSchDao.updYdTcarsch(recInTemp, 0);
				if(intRtnVal <= 0) {
    				szMsg="<Y5LdTcarSch> updYdTcarsch execution failed";
    				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	    			return intRtnVal = -1;

       				outRecord.setField("RTN_CD" , "0");	
    				outRecord.setField("RTN_MSG", szMsg);	
    				return outRecord;
 				
				}

	    		//대차스케줄생성 메소드 호출 
				msgRecord = JDTORecordFactory.getInstance().create();
				msgRecord.setField("YD_EQP_ID", szYD_EQP_ID);
	    		
//				intRtnVal = this.Y5L2TcarSch(msgRecord);
//	    		if(intRtnVal == -1){ 
////	    			return intRtnVal; 
//       				outRecord.setField("RTN_CD" , "0");	
//    				outRecord.setField("RTN_MSG", szMsg);	
//    				return outRecord;
//	    		}
	    		outRecord1 = (JDTORecord)this.Y5L2TcarSch(msgRecord);
       			sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
       			sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
       			sTCAR_MOVE_SND	= StringHelper.evl(outRecord1.getFieldString("TCAR_MOVE_SND"), "");
    			
    			if(sRTN_CD.equals("0")){ 
    				//throw new DAOException(szMsg);
    				outRecord.setField("RTN_CD" , "0");	
    				outRecord.setField("RTN_MSG", sRTN_MSG);	
    				return outRecord;
    			} else {
    				
    				return outRecord1; 				
    			}	

	    		
	    	}else if(intRtnVal > 0) {
	    		//하차가 아직 완료되지 않음
	    		szMsg="하차가 아직 완료되지 않음";

	    	}
    		//하차가 아직 완료되지 않음
			szMsg="대차 하차 스케줄("+szMethodName+") 완료";
			outRecord.setField("RTN_CD" , "1");	
			outRecord.setField("TCAR_MOVE_SND" , sTCAR_MOVE_SND);	
			outRecord.setField("RTN_MSG", szMsg);	
			return outRecord;
	    	

		}catch(Exception e){
			e.printStackTrace();
			szMsg="대차 하차 스케줄 Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//			return intRtnVal = -1;
			outRecord.setField("RTN_CD" , "0");	
			outRecord.setField("RTN_MSG", szMsg);	
			return outRecord;
		}
	
	} //end of Y5LdTcarSch()
	

	
	
	
	
	
	
	
	
	
	/**
	 * 오퍼레이션명 : 대차작업예약재료생성
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord, rsWbooId
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public int Y5MakeWrkBook(JDTORecord msgRecord,JDTORecordSet rsTcarFtMtl,JDTORecordSet rsWbooId)throws JDTOException  {
		YdWrkbookMtlDao  ydWrkbookMtlDao  = new YdWrkbookMtlDao();
		YdWrkbookDao     ydWrkbookDao     = new YdWrkbookDao();
		YdSchRuleDao     ydSchRuleDao     = new YdSchRuleDao();
		YdTcarFtmvMtlDao ydTcarFtmvMtlDao = new YdTcarFtmvMtlDao();
		
		
		JDTORecordSet rsResult          = null;
		JDTORecordSet rsWrkBookMtl      = null;
		JDTORecord    recInTemp         = null;
		JDTORecord    recOutTemp        = null;
		JDTORecord    recPara           = null;
		
		
	    int intRtnVal 					= 0;
	    
	    String szMsg              		= "";
	    String szMethodName       		= "Y5MakeWrkBook";
	    
	    String szQuery                  = "";
	    
	    String szWbookId                = "";
	    String szYdGp                   = "";
	    String szYdBayGp                = "";
	    String szPreSchCd               = "";
	    String szCurSchCd               = "";
	    String szWrkPlanTcar            = "";
	    String szSchPrior               = "";
	    String szYD_TO_LOC_DCSN_MTD		= null;
	    String szYD_TO_LOC_GUIDE		= null;
	    
	    String szRegister               = "";

	    try{
	    	szWbookId = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");
	    	
	    	//작업예약id로 작업예약Table를 조회한다.
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_WBOOK_ID", szWbookId);
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	/*com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookExceptDelYN*/
	    	intRtnVal = ydWrkbookDao.getYdWrkbook(recInTemp, rsResult, 10);
			if(intRtnVal <= 0) {
				if(intRtnVal == 0) {
					szMsg="<Y5MakeWrkBook> getYdWrkbook : data not found";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				}else if(intRtnVal == -2) {
					szMsg="<Y5MakeWrkBook> getYdWrkbook : parameter error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
    			throw new DAOException(szSessionName + " : " + szMethodName + " - " + szMsg);
			}
	    	
	    	rsResult.absolute(1);
	    	recOutTemp = JDTORecordFactory.getInstance().create();
	    	recOutTemp.setRecord(rsResult.getRecord());
	    	
	    	
	    	szYdGp     		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_GP");	//야드구분 = 야드구분
	    	szYdBayGp     	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_AIM_BAY_GP");	//야드동구분 = 목표동구분
	    	szPreSchCd    	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_SCH_CD");		//이전스케줄코드 = 스케줄코드
	    	szWrkPlanTcar 	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WRK_PLAN_TCAR");	//작업계획대차  = 작업계획대(JXTC01,JXTC02)
	    	//현재스케줄코드 = 야드구분 + 야드동구분 + 이전스케줄코드.(2,4) + "TCLM;
	   // 	szCurSchCd    	= szYdGp + szYdBayGp + szWrkPlanTcar.substring(2,4) + "0" + szWrkPlanTcar.substring(5,6) + "LM";
	    	szCurSchCd    	= szYdGp + szYdBayGp + szWrkPlanTcar.substring(2,6) + "LM";
			szMsg="<Y5MakeWrkBook> szCurSchCd : " + szCurSchCd;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
			
			szYD_TO_LOC_DCSN_MTD 	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TO_LOC_DCSN_MTD");
			szYD_TO_LOC_GUIDE		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TO_LOC_GUIDE");
			
			if(!szYD_TO_LOC_GUIDE.equals("")){
				//반납일 경우 TC로 가이드 되고 동간이적일 경우에는 하차할 위치가 됨 
				if(szYD_TO_LOC_GUIDE.substring(2,4).equals("TC")){
					// 일반BED로 검색하기 위해 가이드를 CLEAR 한다.
					szYD_TO_LOC_DCSN_MTD 	= "S" ;      
					szYD_TO_LOC_GUIDE   	= "" ;      
					
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
					szMsg="<Y5MakeWrkBook> getYdSchrule : data not found";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				}else if(intRtnVal == -2) {
					szMsg="<Y5MakeWrkBook> getYdSchrule : parameter error";
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
					szMsg="<Y5MakeWrkBook> getYdWrkbook : data not found";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				}else if(intRtnVal == -2) {
					szMsg="<Y5MakeWrkBook> getYdWrkbook : parameter error";
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
	    	
	    	recInTemp.setField("YD_WBOOK_ID"		, szWbookId);
	    	recInTemp.setField("REGISTER"			, szRegister);
	    	recInTemp.setField("YD_GP"				, szYdGp);
	    	recInTemp.setField("YD_BAY_GP"			, szYdBayGp);
	    	recInTemp.setField("YD_AIM_YD_GP"		, szYdGp);
	    	recInTemp.setField("YD_AIM_BAY_GP"		, szYdBayGp);
	    	recInTemp.setField("YD_SCH_PRIOR"		, szSchPrior);
	    	recInTemp.setField("YD_SCH_CD"			, szCurSchCd);
	    	recInTemp.setField("YD_WRK_PLAN_TCAR"	, szWrkPlanTcar);
	    	recInTemp.setField("YD_TO_LOC_DCSN_MTD"	, szYD_TO_LOC_DCSN_MTD);
	    	recInTemp.setField("YD_TO_LOC_GUIDE"	, szYD_TO_LOC_GUIDE);
	    	
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
	    		recInTemp.setField("YD_STK_BED_NO",  ydDaoUtils.stringPlusInt2("00", Loop_i));
	    		recInTemp.setField("YD_STK_LYR_NO",  "001");
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
			return intRtnVal = -1;
		}
	
	
		szMsg="작업예약생성("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		return intRtnVal = 1;
	} //end of Y5MakeWrkBook()
	
	
	/**
	 * 오퍼레이션명 : 작업예약으로 대차이동지시 Check
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord, rsWbooId
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public JDTORecord TCarMoveWrkBook(JDTORecord msgRecord)throws JDTOException  {
		YdEqpDao     ydEqpDao		= new YdEqpDao();
		YdTcarSchDao ydTcarSchDao   = new YdTcarSchDao();
		
		
		JDTORecordSet rsResult          = null;
		JDTORecord    recInTemp         = null;
		JDTORecord    recOutTemp        = null;
		
		JDTORecord outRecord  			= JDTORecordFactory.getInstance().create(); 		
	    int intRtnVal 					= 0;
	    
	    String szMsg              		= "";
	    String szMethodName       		= "TcarMoveWrkBook";
	    
	    String sYD_TCAR_SCH_ID          = "";
	    String sYD_WBOOK_ID             = "";
	    String sYD_GP			  		= "";
	    String sYD_BAY_GP	  			= "";
	    String sYD_CURR_BAY_GP          = "";
	    
	    try{
	    	sYD_WBOOK_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_WBOOK_ID");
	    	
	    	//작업예약id로 작업예약Table를 조회한다.
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_WBOOK_ID", sYD_WBOOK_ID);
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	/*com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpTCarStatCoil*/
	    	intRtnVal = ydEqpDao.getYdEqp(recInTemp, rsResult, 303);
			if(intRtnVal <= 0) {
				outRecord.setField("SND_FLAG" , "N");	
				outRecord.setField("RTN_CD"   , "1");	
				return outRecord;
				
			}

	    	rsResult.absolute(1);
	    	recOutTemp = JDTORecordFactory.getInstance().create();
	    	recOutTemp.setRecord(rsResult.getRecord());
	    	

	    	sYD_TCAR_SCH_ID	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_TCAR_SCH_ID");	
	    	sYD_GP			= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_GP");	
	    	sYD_BAY_GP		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_BAY_GP");	
	    	sYD_CURR_BAY_GP	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CURR_BAY_GP");	

    		recInTemp = JDTORecordFactory.getInstance().create();
    		recInTemp.setField("YD_TCAR_SCH_ID"			, sYD_TCAR_SCH_ID);
    		recInTemp.setField("YD_CARLD_WRK_BOOK_ID"	, sYD_WBOOK_ID);
//    		recInTemp.setField("MODIFIER"				, sYD_WBOOK_ID); /*TODO.. 2010.07.29 - 박지열(수정자 추가)*/
 			/*com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.updYdTcarsch*/
    		intRtnVal = ydTcarSchDao.updYdTcarsch(recInTemp, 0);
			if(intRtnVal <= 0) {
				szMsg="대차스케줄 수정 실패";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//    			return intRtnVal = -1;
   				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;
				
			}
	    	
			
			szMsg="작업예약으로 대차이동지시("+szMethodName+") 완료";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			outRecord.setField("RTN_CD" 		, "1");	
			outRecord.setField("RTN_MSG"		, szMsg);	
			outRecord.setField("YD_TCAR_SCH_ID" , sYD_TCAR_SCH_ID);	
			outRecord.setField("YD_GP" 			, sYD_GP);	
			outRecord.setField("YD_AIM_BAY_GP" 	, sYD_BAY_GP);
			// 현위치와 목적동이 일치 하면 송신 하지 않는다
			if (sYD_BAY_GP.equals(sYD_CURR_BAY_GP)) {
				outRecord.setField("SND_FLAG" 		, "N");	
			} else {
				outRecord.setField("SND_FLAG" 		, "Y");	
			}
			
			return outRecord;
	    	
		}catch(Exception e){
			szMsg="작업예약으로 대차이동지시 Error :" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			outRecord.setField("RTN_CD" , "0");	
			outRecord.setField("RTN_MSG", szMsg);	
			return outRecord;
		}
	
	} //end of Y5MakeWrkBook()
	
	
	
	
	
  //---------------------------------------------------------------------------	
} // end of class TransEqpSchSeEJBBean
