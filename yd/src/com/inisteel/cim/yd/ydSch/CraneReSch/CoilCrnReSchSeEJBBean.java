package com.inisteel.cim.yd.ydSch.CraneReSch;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.util.YdTcConst;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdDBAssist;
import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.dao.ydEqpDao.YdEqpDao;
import com.inisteel.cim.yd.common.dao.ydSchRuleDao.YdSchRuleDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookDao.YdWrkbookDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.crn.CrnSchUtil;


/**
 * 크레인리스케쥴요청 Session EJB
 *
 * @ejb.bean name="CoilCrnReSchSeEJB" jndi-name="CoilCrnReSchSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class CoilCrnReSchSeEJBBean extends BaseSessionBean {
	
	// Session Name
	private String szSessionName=getClass().getName();
	private YdUtils ydUtils =new YdUtils();
	private YdDaoUtils ydDaoUtils =new YdDaoUtils();
	private YdDBAssist ydDBAssist =new YdDBAssist();
	private YdDelegate ydDelegate =new YdDelegate();
	private YdTcConst ydTcConst =new YdTcConst();
	private YdSchRuleDao ydSchRuleDao = new YdSchRuleDao();
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
     * 오퍼레이션명 : 코일제품창고 크레인 리스케줄
     *  
     * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param msgRecord
     * @return
     * @throws JDTOException
     */
    public void procY5CrnReSch(JDTORecord msgRecord)throws JDTOException  {
    	
    	
    	JDTORecordSet rsSchRule = null;
    	JDTORecordSet outRecSet = null;
        JDTORecordSet rsReSchResult = JDTORecordFactory.getInstance().createRecordSet("temp");
        JDTORecord recTemp = null;
        
        int intRtnVal 					= 0 ;
        int intGp						= 0 ;
        String szMsg              		= "";
        String szMethodName       		= "procY5CrnReSch";
        
        String szEqpId                  = "";
        String szYdGp                   = "";
        String szYdBayGp                = "";
        String szQuery                  = "";	
        
        boolean bRtnCheck               = true;
        
        
        String szRcvTcCode=ydUtils.getTcCode(msgRecord);
        if(szRcvTcCode==null){
        	szMsg="[ERROR] "+szSessionName+"::"+szMethodName+"() TC Code Error ("+szRcvTcCode+")";
        	ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
        	return;
        }
        
        	
        
        
        if(bDebugFlag){
            szMsg="[DEBUG] 전문수신 : TCCODE=" +szRcvTcCode ;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        }

       
        
        try{
  
        	//파라미터로 넘어온 설비 ID로 설비 상태를 Check
        	szEqpId   = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
        	
        	bRtnCheck = this.Y5EqpStatCheck(szEqpId);
        	
        	//설비아이디의 야드구분 동구분을 빼서 스케줄기준Table조회
        	szYdGp    = szEqpId.substring(0,1);
        	szYdBayGp = szEqpId.substring(1,2);
        	recTemp = JDTORecordFactory.getInstance().create();
        	recTemp.setField("YD_GP",     szYdGp);
        	recTemp.setField("YD_BAY_GP", szYdBayGp);
        	outRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
        	/* com.inisteel.cim.yd.dao.ydschruledao.YdSchruleDao.getYdSchruleYdGpYdBayGp */
         	intRtnVal = ydSchRuleDao.getYdSchrule(recTemp, outRecSet, 7);
			if(intRtnVal <= 0) {	
				szMsg="스케줄기준이 조회가 되지않았습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
			
			if(bRtnCheck == false) {
				//1. 고장시 고장 리스케줄 메소드 호출
				intRtnVal = this.Y5DisableReSch(szEqpId, outRecSet, rsReSchResult);
				if(intRtnVal == -1) {
					szMsg="고장 리스케줄 처리 Error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
				
			}else{
				//2. 정상시 복구 리스케줄 메소드 호출
				intRtnVal = this.Y5ResPairReSch(szEqpId, outRecSet, rsReSchResult); 
				if(intRtnVal == -1) {
					szMsg="복구 리스케줄 처리 Error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				}
			}
        	
        	//3. 호출한 메소드의 결과값으로 작업예약 Table및 크레인 스케줄 Table업데이트 메소드 호출
        	intRtnVal = this.Y5UpdWbookCrnsch(rsReSchResult);
			if(intRtnVal == -1) {
				szMsg="작업예약 및 크레인 스케줄 등록 중 Error";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;
			}
        	
		}catch(Exception e){

			szMsg="Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}

		
		szMsg="크레인 리스케줄("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		return;

	} //end of procY5CrnReSch()
    

    
    
    
    
    
	/**
	 * 오퍼레이션명 : 고장 리스케줄
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param szEqpId, rsSchRule, rsReSchResult
	 * @return
	 * @throws JDTOException
	 */
	public int Y5DisableReSch(String szEqpId, JDTORecordSet rsSchRule, JDTORecordSet rsReSchResult)throws JDTOException  {
		
		JDTORecord recTemp              = null;
		JDTORecord recResult            = null;
		JDTORecordSet rsResult          = JDTORecordFactory.getInstance().createRecordSet("Temp");
		
	    int intRtnVal 					= 0 ;
	    
	    String szMsg              		= "";
	    String szMethodName       		= "Y5DisableReSch";
	    
	    String szWrkCrn                 = "";
	    String szAltCrn                 = "";
	    
	    
	    try{

	    	for(int Loop_i = 1; Loop_i <= rsSchRule.size(); Loop_i++) {
	    		rsSchRule.absolute(Loop_i);
	    		recTemp = JDTORecordFactory.getInstance().create();
	    		recTemp.setRecord(rsSchRule.getRecord());
	    		szWrkCrn = ydDaoUtils.paraRecChkNull(recTemp, "YD_WRK_CRN");
	    		szAltCrn = ydDaoUtils.paraRecChkNull(recTemp, "YD_ALT_CRN");
	    		
	    		recResult = JDTORecordFactory.getInstance().create();
	    		//설비아이디가 스케줄 기준의 주크레인과 같거나 대체크레인과 같을 경우
	    		if(szEqpId.equals(szWrkCrn)) {
	    			//새로운 레코드 셋에 등록한다.(스케줄 코드, 설비ID, 스케줄우선순위)
	    			recResult.setField("YD_SCH_CD", recTemp.getFieldString("YD_SCH_CD"));
	    			recResult.setField("YD_EQP_ID", recTemp.getFieldString("YD_ALT_CRN"));
	    			recResult.setField("YD_SCH_PRIOR", recTemp.getFieldString("YD_ALT_CRN_PRIOR"));
	    			recResult.setField("WRK_CRN_YN", "Y");
	    			rsResult.addRecord(recResult);
	    			
	    		}else if (szEqpId.equals(szAltCrn)) {
	    			//새로운 레코드 셋에 등록한다.
	    			recResult.setField("YD_SCH_CD", recTemp.getFieldString("YD_SCH_CD"));
	    			recResult.setField("YD_SCH_PRIOR", recTemp.getFieldString("YD_ALT_CRN_PRIOR"));
	    			recResult.setField("WRK_CRN_YN", "N");
	    			rsResult.addRecord(recResult);
	    		}
	    		
	    	}//end of for
	    	rsReSchResult.addAll(rsResult);
	    	
	
		}catch(Exception e){
			szMsg="고장 리스케줄 처리 Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return intRtnVal = -1;
		}
	
	
		szMsg="고장 리스케줄 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		return intRtnVal = 1;
	} //end of Y5DisableReSch()
	
	
	
	
	
	
	
	/**
	 * 오퍼레이션명 : 복구리스케줄
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param szEqpId, rsSchRule, rsReSchResult
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public int Y5ResPairReSch(String szEqpId, JDTORecordSet rsSchRule, JDTORecordSet rsReSchResult)throws JDTOException  {
		
		JDTORecord recTemp              = null;
		JDTORecord recResult            = null;
		JDTORecordSet rsResult          = JDTORecordFactory.getInstance().createRecordSet("Temp");
		
	    int intRtnVal 					= 0 ;
	    
	    String szMsg              		= "";
	    String szMethodName       		= "Y5ResPairReSch";
	    
	    String szWrkCrn                 = "";
	    String szAltCrn                 = "";
	    
	    
	    try{

	    	for(int Loop_i = 1; Loop_i <= rsSchRule.size(); Loop_i++) {
	    		rsSchRule.absolute(Loop_i);
	    		recTemp = JDTORecordFactory.getInstance().create();
	    		recTemp.setRecord(rsSchRule.getRecord());
	    		szWrkCrn = ydDaoUtils.paraRecChkNull(recTemp, "YD_WRK_CRN");
	    		szAltCrn = ydDaoUtils.paraRecChkNull(recTemp, "YD_ALT_CRN");
	    		
	    		recResult = JDTORecordFactory.getInstance().create();
	    		//설비아이디가 스케줄 기준의 주크레인과 같거나 대체크레인과 같을 경우
	    		if(szEqpId.equals(szWrkCrn)) {
	    			//새로운 레코드 셋에 등록한다.(스케줄 코드, 설비ID, 스케줄우선순위)
	    			recResult.setField("YD_SCH_CD", recTemp.getFieldString("YD_SCH_CD"));
	    			recResult.setField("YD_EQP_ID", recTemp.getFieldString("YD_WRK_CRN"));
	    			recResult.setField("YD_SCH_PRIOR", recTemp.getFieldString("YD_WRK_CRN_PRIOR"));
	    			recResult.setField("WRK_CRN_YN", "Y");
	    			rsResult.addRecord(recResult);
	    			
	    		}else if (szEqpId.equals(szAltCrn)) {
	    			//새로운 레코드 셋에 등록한다.
	    			recResult.setField("YD_SCH_CD", recTemp.getFieldString("YD_SCH_CD"));
	    			recResult.setField("YD_SCH_PRIOR", recTemp.getFieldString("YD_WRK_CRN_PRIOR"));
	    			recResult.setField("WRK_CRN_YN", "N");
	    			rsResult.addRecord(recResult);
	    		}
	    		
	    	}//end of for
	    	rsReSchResult.addAll(rsResult);
	    	
		}catch(Exception e){
	
			szMsg="복구 리스케줄 처리 Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return intRtnVal = -1;
		}
	
	
		szMsg="복구 리스케줄 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		return intRtnVal = 1;
	} //end of Y5ResPairReSch()
	
	
	
	
	
	
	
	/**
	 * 오퍼레이션명 : C연주 크레인 리스케줄 작업예약 및 크레인스케줄 등록
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param rsCrnSch
	 * @return intRtnVal
	 * @throws JDTOException
	 */
	public int Y5UpdWbookCrnsch(JDTORecordSet rsCrnSch)throws JDTOException  {
		YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		JDTORecord    recTemp           = null;
		JDTORecord    inRec             = null;
		
		JDTORecordSet outRecSet         = null;
	    int intRtnVal 					= 0 ;
	    
	    String szMsg              		= "";
	    String szMethodName       		= "Y5UpdWbookCrnsch";
	    
	    
	    String szQuery                  = "";
	    
	    String szSchCd                  = "";
	    String szSchPrior               = "";
	    String szEqpId                  = "";
	    
	    try{

	    	for(int Loop_i = 1; Loop_i <= rsCrnSch.size(); Loop_i++) {
	    		rsCrnSch.absolute(Loop_i);
	    		recTemp = JDTORecordFactory.getInstance().create();
	    		recTemp.setRecord(rsCrnSch.getRecord());

	    		szSchCd    = ydDaoUtils.paraRecChkNull(recTemp, "YD_SCH_CD");
	    		szSchPrior = ydDaoUtils.paraRecChkNull(recTemp, "YD_SCH_PRIOR");
	    		
	    		//작업예약TABLE 스케줄우선순위 정보 UPDATE
	    		inRec = JDTORecordFactory.getInstance().create();
	    		inRec.setField("YD_SCH_CD", szSchCd);
	    		inRec.setField("MODIFIER", "SYSTEM");
	    		inRec.setField("YD_SCH_PRIOR", szSchPrior);
	    		/* com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.updYdWrkbookYD_SCH_PRIOR */
	    		intRtnVal = ydWrkbookDao.updYdWrkbook_YD_SCH_PRIOR(inRec);
	    		
	    		//크레인 스케줄 Table 업데이트
    			//리스케줄 대상크레인이 주작업 크레인인 경우
	    		if(recTemp.getFieldString("WRK_CRN_YN").equals("Y")){
	    			intRtnVal = ydCrnSchDao.updYdCrnschReSch(recTemp, 0);
	    			
	    		}else{
	    			//리스케줄 대상크레인이 보조작업 크레인인 경우
	    			intRtnVal = ydCrnSchDao.updYdCrnschReSch(recTemp, 1);
	    		}
	    	}//end of for
	
		}catch(Exception e){
	
			szMsg="작업예약 및 크레인 스케줄 업데이트 Error:" +e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return intRtnVal = -1;
		}
	
	
		szMsg="작업예약 및 크레인 스케줄 업데이트("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		return intRtnVal = 1;
	} //end of Y5UpdWbookCrnsch()
	
	
	
	
	
	
	
	
	/**
	 * 오퍼레이션명 : 설비상태 체크
	 *  
	 * @param   String szEqpId 설비ID
	 * @return boolean true(설비사용가능), false(설비사용불가)
	 * @throws JDTOException
	 */
	public boolean Y5EqpStatCheck(String szEqpId)throws JDTOException  {

		//리턴값(boolean)
		boolean blnRtnVal      = false;
		//메세지
		String szMsg           = null;
		//메소드명
		String szMethodName    = "Y5EqpStatCheck";		
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
			blnRtnVal = this.Y5ChkGetEqp(szEqpId, rsResult);
			if (!blnRtnVal) return blnRtnVal;
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//설비상태
			szYD_EQP_STAT = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_STAT");
			
			//크레인의 상태가 'T'이면 false 리턴.
			// 상수값으로 변경 - [2009.12.03 이현성]
			if (szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_BREAK)) {
				
				szMsg = "설비ID(" + szEqpId + ")의 상태가 고장(" + szYD_EQP_STAT + ") 입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
	
				blnRtnVal = true;
	
			}
		} catch(Exception e) {
			szMsg = "설비상태 체크 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
		
	} //end of Y5EqpStatCheck
	
	
	
	
	
	/**
	 * 오퍼레이션명 : 설비 유무체크 및 조회결과 데이터 반환
	 *  
	 * @param  String        szEqpId  설비ID
	 *         JDTORecordSet rsResult 결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean Y5ChkGetEqp(String szEqpId, JDTORecordSet rsResult)throws JDTOException  {
		
		//설비 DAO
		YdEqpDao ydEqpDao     = new YdEqpDao();
		//리턴값(boolean)
		boolean blnRtnVal     = false;
		//리턴값(int)
		int intRtnVal         = 0;
		//메소드명
		String szMethodName   = "Y5ChkGetEqp";
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
			if (intRtnVal > 1) {
				
				szMsg = "설비ID(" + szEqpId + ")에 대한 설비 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == 1) {

				blnRtnVal = true;
				
			} else if (intRtnVal == 0) {
				
				szMsg = "설비ID(" + szEqpId + ")에 대한 설비 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == -2) {
				
				szMsg = "설비ID(" + szEqpId + ")로 설비 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "설비ID(" + szEqpId + ")로 설비 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e) {
			szMsg = "설비 유무체크 및 조회결과 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of Y5ChkGetEqp
	

  //---------------------------------------------------------------------------	
} // end of class CrnReSchSeEJBBean
