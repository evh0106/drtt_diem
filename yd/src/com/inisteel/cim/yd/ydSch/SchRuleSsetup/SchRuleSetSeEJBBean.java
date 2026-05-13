package com.inisteel.cim.yd.ydSch.SchRuleSsetup;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.dao.ydCrnWrkMtlDao.YdCrnWrkMtlDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdTcConst;
import com.inisteel.cim.yd.common.util.YdUtils;


/**
 * 스케쥴기준설정 Session EJB
 *
 * @ejb.bean name="SchRuleSetSeEJB" jndi-name="SchRuleSetSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class SchRuleSetSeEJBBean extends BaseSessionBean {
	
	// Session Name
	private String szSessionName=getClass().getName();
	
	private YdUtils ydUtils =new YdUtils();
	
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
	 *      [A] 오퍼레이션명 : 크레인 스케줄 중복 체크 
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void procYdCrnschDuplicateChk(String  szYdGp ,String  szCrnSchId)throws JDTOException  {		
		String szMsg="";
		String szMethodName="procYdCrnschDuplicateChk";
		String szV_YD_DN_WO_LOC 	="";
		String szV_YD_DN_WO_LAYER 	="";
		
		JDTORecord 		recPara	 		= null;
		JDTORecordSet 	rsCrnSchInfo   	= null;
		YdCrnSchDao   	ydCrnSchDao 	= new YdCrnSchDao();
		YdCrnWrkMtlDao 	ydCrnWrkMtlDao  = new YdCrnWrkMtlDao();
		YdStkLyrDao 	ydStkLyrDao 	= new YdStkLyrDao();
		YdDaoUtils 		ydDaoUtils 		= new YdDaoUtils();
		JDTORecordSet 	rsRtnVal 		= JDTORecordFactory.getInstance().createRecordSet("temRs");
		JDTORecord 		recGetCrnSch   	= JDTORecordFactory.getInstance().create();
		int intRtnVal	=0;
		try {
		
			szMsg="크레인 스케줄 중복 체크  처리("+szMethodName+") 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			szYdGp="J";
			szCrnSchId="";
			
			
			recPara   = JDTORecordFactory.getInstance().create();
			rsCrnSchInfo = JDTORecordFactory.getInstance().createRecordSet("YD");
			recPara.setField("YD_GP", 		 szYdGp);
			recPara.setField("YD_CRN_SCH_ID", szCrnSchId);
		
			/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschDuplicateChk*/
			intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, rsCrnSchInfo, 507);
						
			if(intRtnVal > 0) {
				szMsg="◈◈◈◈("+szMethodName+") 중복 스케줄이 존재 함  log기록 후 삭제 처리 시작◈◈◈◈ ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//스케줄 이력 기록--------------------------------------------------------
				/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.insYdCrnschHist*/
				intRtnVal = ydCrnSchDao.insYdCrnschHist(recPara, 0);
				 
				/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.insYdCrnWrmtlHist*/
				intRtnVal = ydCrnSchDao.insYdCrnschHist(recPara, 1);
				//--------------------------------------------------------------------
				
				
				//스케줄 삭제 -----------------------------------------------------------
				
				//다음 스케줄 작업 재료 레코드로가기 전 작업한 스케줄 재료를 삭제 처리 해준다.				
				recPara   = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID"	, szCrnSchId);
				recPara.setField("DEL_YN"			, "Y"); 
				recPara.setField("MODIFIER"	, "Duplicate");
				
				szMsg = "[Jsp Session : "+szMethodName+"] : 크레인스케줄 작업 재료 삭제처리" ;				
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				intRtnVal = ydCrnWrkMtlDao.updYdCrnwrkmtl(recPara, 1);
				
				szMsg = "[Jsp Session : "+szMethodName+"] : 크레인스케줄  삭제처리" ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				
				intRtnVal = ydCrnSchDao.updYdCrnsch(recPara, 0);
				//-------------------------------------------------------------------
				
				//권하위치 복원 작업-----------------------------------------------------
				
				rsRtnVal = JDTORecordFactory.getInstance().createRecordSet("temRs");
				intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, rsRtnVal, 0);
				
				if (intRtnVal < 1 ){
					szMsg="해당크레인 스케줄이 존재하지않습니다";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return ;
				}
				
				rsRtnVal.first();
				recGetCrnSch 	   = rsRtnVal.getRecord();				
				szV_YD_DN_WO_LOC   = ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_DN_WO_LOC");   //권하 지시위치
				szV_YD_DN_WO_LAYER = ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_DN_WO_LAYER");  //권하 지시단
				
				recPara   = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_STK_COL_GP",       szV_YD_DN_WO_LOC.substring(0, 6));
				recPara.setField("YD_STK_BED_NO",       szV_YD_DN_WO_LOC.substring(6, 8));			 
				recPara.setField("YD_STK_LYR_NO",       szV_YD_DN_WO_LAYER);
				recPara.setField("STL_NO",              "");
				recPara.setField("YD_STK_LYR_MTL_STAT", "E");


				szMsg = "[Jsp Session : "+szMethodName+"] : 권하 재료 정보 복원";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				ydUtils.displayRecord(szMethodName, recPara);					
				intRtnVal = ydStkLyrDao.updYdStklyr(recPara, 0);
				
				//에러리턴
				if (intRtnVal < 1) {
					szMsg = "[Jsp Session : "+szMethodName+"] : 권하 지시 위치 CLEAR 실패";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING); 
					return ;
				}
				
				szMsg = "[Jsp Session : "+szMethodName+"] : 권하 지시 위치 CLEAR 성공";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
				//-------------------------------------------------------------------
				
				szMsg="◈◈◈◈("+szMethodName+") 중복 스케줄이 존재 함  log기록 후 삭제 처리 완료◈◈◈◈ ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
		
 
		szMsg="크레인 스케줄 중복 체크  처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		} catch(Exception e) {
			szMsg = "기울기 편차 CHECK 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return ;
		}
	
	}// end of procTest()

	
	

  //---------------------------------------------------------------------------	
} // end of class SchRuleSetSeEJBBean
