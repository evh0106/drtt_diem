package com.inisteel.cim.yd.ydEquipStat.Monitor;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTOException;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdTcConst;
import com.inisteel.cim.yd.common.util.YdUtils;


/**
 * Monitoring Session EJB
 *
 * @ejb.bean name="MonitoringSeEJB" jndi-name="MonitoringSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class MonitoringSeEJBBean extends BaseSessionBean {
	
	// Session Name
	private String szSessionName=getClass().getName();
	
	private YdUtils ydUtils =new YdUtils();
	
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();
	
	private YdTcConst ydTcConst =new YdTcConst();
	
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
	 *      [A] 오퍼레이션명 : 로그메세지처리
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procLogMsg(JDTORecord msgRecord)throws JDTOException  {
		
		
		String szMsg="";
		String szMethodName="procLogMsg";
		String szOperationName			= "로그메세지처리";
		
		String szYdGp           	= null;			// 야드구분
		String desti            	= null;			// Monitoring Channel
		String szLogMsg         	= null;			// Logging Message
		String szYdBayGp        	= null;			//  야드동구분
		String szYdEqpId        	= null;			//  설비 ID
		String szYdSchCd        	= null;			// 스케줄 코드
		String szYdEvtGp        	= null;			// 야드 Event 구분 (C:크레인, Q:설비, E:에러, W:경고, I:정보, Z:기타)
		String szYdMsgOutpwrGrd 	= null;			// Message출력등급(A~E 5단계)
		String szYdPgmTp        	= null;			//  야드프로그램유형 (W:화면, S:스케줄, I:인터페이스)
		String szYdIfCd         	= null;			//  야드 인터페이스 코드(TC CODE)
		String szEJBId	       		= null;			// Logging 요청 Class name
		String szMsgName 	   		= null;			// Logging 요청 Method Name
		

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
		
		
		try {
			szMsg="["+szOperationName+"] --------------------- 메소드 시작 --------------------";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			szYdGp           	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_GP");						// 야드구분
			desti            	= ydDaoUtils.paraRecChkNull(msgRecord, "MONITORING_CHANNEL");			// Monitoring Channel
			szLogMsg         	= ydDaoUtils.paraRecChkNull(msgRecord, "MSG_CONTENTS");					// Logging Message
			szYdBayGp        	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_BAY_GP");					//  야드동구분
			szYdEqpId        	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");					//  설비 ID
			szYdSchCd        	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");					// 스케줄 코드
			szYdEvtGp        	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EVT_GP");					// 야드 Event 구분 (C:크레인, Q:설비, E:에러, W:경고, I:정보, Z:기타)
			szYdMsgOutpwrGrd 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_MSG_OUTPWR_GRD");			// Message출력등급(A~E 5단계)
			szYdPgmTp        	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_PGM_TP");					//  야드프로그램유형 (W:화면, S:스케줄, I:인터페이스)
			szYdIfCd         	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_IF_CD");						//  야드 인터페이스 코드(TC CODE)
			szEJBId	       		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_E_J_B_ID");					// Logging 요청 Class name
			szMsgName 	   		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_MSG_NM");					// Logging 요청 Method Name
			
			ydUtils.putLogMsg(szYdGp, desti, szLogMsg, szYdBayGp, szYdEqpId, szYdSchCd, szYdEvtGp, szYdMsgOutpwrGrd, szYdPgmTp, szYdIfCd, szEJBId, szMsgName);
			
		}catch(Exception ex) {
			szMsg="["+szOperationName+"] " + ex.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}

		
		szMsg="["+szOperationName+"] --------------------- 메소드 끝 --------------------";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	
	}// end of procLogMsg()

  //---------------------------------------------------------------------------	
} // end of class MonitoringSeEJBBean
